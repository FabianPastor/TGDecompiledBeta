package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaCodecInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Property;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerEnd;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SecureDocument;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.PhotoPickerPhotoCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.GestureDetector2;
import org.telegram.ui.Components.GroupedPhotosListView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.PhotoCropView;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.PhotoPaintView;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView;
import org.telegram.ui.Components.PickerBottomLayoutViewer;
import org.telegram.ui.Components.PipVideoView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.Tooltip;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanUserMentionPhotoViewer;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoSeekPreviewImage;
import org.telegram.ui.Components.VideoTimelinePlayView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.PhotoViewer;

public class PhotoViewer implements NotificationCenter.NotificationCenterDelegate, GestureDetector2.OnGestureListener, GestureDetector2.OnDoubleTapListener {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile PhotoViewer Instance = null;
    private static volatile PhotoViewer PipInstance = null;
    public static final int SELECT_TYPE_AVATAR = 1;
    public static final int SELECT_TYPE_QR = 10;
    public static final int SELECT_TYPE_WALLPAPER = 3;
    /* access modifiers changed from: private */
    public static DecelerateInterpolator decelerateInterpolator = null;
    private static final int gallery_menu_cancel_loading = 7;
    private static final int gallery_menu_delete = 6;
    private static final int gallery_menu_masks = 13;
    private static final int gallery_menu_openin = 11;
    private static final int gallery_menu_pip = 5;
    private static final int gallery_menu_save = 1;
    private static final int gallery_menu_savegif = 14;
    private static final int gallery_menu_send = 3;
    private static final int gallery_menu_share = 10;
    private static final int gallery_menu_showall = 2;
    private static final int gallery_menu_showinchat = 4;
    /* access modifiers changed from: private */
    public static Drawable[] progressDrawables;
    /* access modifiers changed from: private */
    public static Paint progressPaint;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimator;
    private Context actvityContext;
    private ActionBarMenuSubItem allMediaItem;
    /* access modifiers changed from: private */
    public boolean allowMentions;
    private boolean allowOrder = true;
    private boolean allowShare;
    /* access modifiers changed from: private */
    public float animateToScale;
    /* access modifiers changed from: private */
    public float animateToX;
    /* access modifiers changed from: private */
    public float animateToY;
    /* access modifiers changed from: private */
    public ClippingImageView animatingImageView;
    /* access modifiers changed from: private */
    public Runnable animationEndRunnable;
    /* access modifiers changed from: private */
    public int animationInProgress;
    /* access modifiers changed from: private */
    public long animationStartTime;
    private float animationValue;
    /* access modifiers changed from: private */
    public float[][] animationValues = ((float[][]) Array.newInstance(float.class, new int[]{2, 13}));
    /* access modifiers changed from: private */
    public boolean applying;
    /* access modifiers changed from: private */
    public AspectRatioFrameLayout aspectRatioFrameLayout;
    /* access modifiers changed from: private */
    public boolean attachedToWindow;
    /* access modifiers changed from: private */
    public long audioFramesSize;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Photo> avatarsArr = new ArrayList<>();
    /* access modifiers changed from: private */
    public int avatarsDialogId;
    /* access modifiers changed from: private */
    public BackgroundDrawable backgroundDrawable = new BackgroundDrawable(-16777216);
    /* access modifiers changed from: private */
    public int bitrate;
    /* access modifiers changed from: private */
    public Paint blackPaint = new Paint();
    /* access modifiers changed from: private */
    public FrameLayout bottomLayout;
    /* access modifiers changed from: private */
    public boolean bottomTouchEnabled = true;
    /* access modifiers changed from: private */
    public ImageView cameraItem;
    private boolean canDragDown = true;
    private boolean canZoom = true;
    /* access modifiers changed from: private */
    public PhotoViewerCaptionEnterView captionEditText;
    /* access modifiers changed from: private */
    public TextView captionTextView;
    /* access modifiers changed from: private */
    public ImageReceiver centerImage = new ImageReceiver();
    /* access modifiers changed from: private */
    public AnimatorSet changeModeAnimation;
    /* access modifiers changed from: private */
    public TextureView changedTextureView;
    private boolean changingPage;
    /* access modifiers changed from: private */
    public boolean changingTextureView;
    /* access modifiers changed from: private */
    public CheckBox checkImageView;
    private int classGuid;
    /* access modifiers changed from: private */
    public ImageView compressItem;
    private AnimatorSet compressItemAnimation;
    /* access modifiers changed from: private */
    public int compressionsCount = -1;
    /* access modifiers changed from: private */
    public FrameLayoutDrawer containerView;
    private ImageView cropItem;
    /* access modifiers changed from: private */
    public int currentAccount;
    private AnimatedFileDrawable currentAnimation;
    /* access modifiers changed from: private */
    public Bitmap currentBitmap;
    private TLRPC.BotInlineResult currentBotInlineResult;
    /* access modifiers changed from: private */
    public AnimatorSet currentCaptionAnimation;
    /* access modifiers changed from: private */
    public long currentDialogId;
    /* access modifiers changed from: private */
    public int currentEditMode;
    /* access modifiers changed from: private */
    public ImageLocation currentFileLocation;
    /* access modifiers changed from: private */
    public String[] currentFileNames = new String[3];
    /* access modifiers changed from: private */
    public int currentIndex;
    /* access modifiers changed from: private */
    public AnimatorSet currentListViewAnimation;
    /* access modifiers changed from: private */
    public Runnable currentLoadingVideoRunnable;
    /* access modifiers changed from: private */
    public MessageObject currentMessageObject;
    /* access modifiers changed from: private */
    public int currentPanTranslationY;
    private String currentPathObject;
    /* access modifiers changed from: private */
    public PlaceProviderObject currentPlaceObject;
    private Uri currentPlayingVideoFile;
    private SecureDocument currentSecureDocument;
    private String currentSubtitle;
    /* access modifiers changed from: private */
    public ImageReceiver.BitmapHolder currentThumb;
    /* access modifiers changed from: private */
    public ImageLocation currentUserAvatarLocation = null;
    /* access modifiers changed from: private */
    public boolean currentVideoFinishedLoading;
    private int dateOverride;
    private TextView dateTextView;
    /* access modifiers changed from: private */
    public boolean disableShowCheck;
    private boolean discardTap;
    private TextView docInfoTextView;
    private TextView docNameTextView;
    private boolean doneButtonPressed;
    /* access modifiers changed from: private */
    public boolean dontResetZoomOnFirstLayout;
    private boolean doubleTap;
    private boolean doubleTapEnabled;
    private float dragY;
    private boolean draggingDown;
    private boolean[] drawPressedDrawable = new boolean[2];
    /* access modifiers changed from: private */
    public PickerBottomLayoutViewer editorDoneLayout;
    private boolean[] endReached = {false, true};
    private long endTime;
    private long estimatedDuration;
    private int estimatedSize;
    private boolean firstAnimationDelay;
    boolean fromCamera;
    private GestureDetector2 gestureDetector;
    /* access modifiers changed from: private */
    public GroupedPhotosListView groupedPhotosListView;
    /* access modifiers changed from: private */
    public PlaceProviderObject hideAfterAnimation;
    private boolean ignoreDidSetImage;
    /* access modifiers changed from: private */
    public AnimatorSet imageMoveAnimation;
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> imagesArr = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Object> imagesArrLocals = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<ImageLocation> imagesArrLocations = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Integer> imagesArrLocationsSizes = new ArrayList<>();
    private ArrayList<MessageObject> imagesArrTemp = new ArrayList<>();
    private SparseArray<MessageObject>[] imagesByIds = {new SparseArray<>(), new SparseArray<>()};
    private SparseArray<MessageObject>[] imagesByIdsTemp = {new SparseArray<>(), new SparseArray<>()};
    /* access modifiers changed from: private */
    public boolean inPreview;
    private VideoPlayer injectingVideoPlayer;
    private SurfaceTexture injectingVideoPlayerSurface;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private boolean invalidCoords;
    private boolean isActionBarVisible = true;
    /* access modifiers changed from: private */
    public boolean isCurrentVideo;
    private boolean isDocumentsPicker;
    /* access modifiers changed from: private */
    public boolean isEvent;
    private boolean isFirstLoading;
    /* access modifiers changed from: private */
    public boolean isInline;
    /* access modifiers changed from: private */
    public boolean isPhotosListViewVisible;
    /* access modifiers changed from: private */
    public boolean isPlaying;
    private boolean isSingleLine;
    /* access modifiers changed from: private */
    public boolean isStreaming;
    /* access modifiers changed from: private */
    public boolean isVisible;
    private boolean keepScreenOnFlagSet;
    /* access modifiers changed from: private */
    public long lastBufferedPositionCheck;
    /* access modifiers changed from: private */
    public Object lastInsets;
    /* access modifiers changed from: private */
    public long lastSaveTime;
    private String lastTitle;
    private ImageReceiver leftImage = new ImageReceiver();
    private boolean loadInitialVideo;
    private boolean loadingMoreImages;
    private ActionBarMenuItem masksItem;
    private int maxSelectedPhotos = -1;
    private float maxX;
    private float maxY;
    /* access modifiers changed from: private */
    public LinearLayoutManager mentionLayoutManager;
    /* access modifiers changed from: private */
    public AnimatorSet mentionListAnimation;
    /* access modifiers changed from: private */
    public RecyclerListView mentionListView;
    /* access modifiers changed from: private */
    public MentionsAdapter mentionsAdapter;
    /* access modifiers changed from: private */
    public ActionBarMenuItem menuItem;
    private long mergeDialogId;
    private float minX;
    private float minY;
    /* access modifiers changed from: private */
    public AnimatorSet miniProgressAnimator;
    private Runnable miniProgressShowRunnable = new Runnable() {
        public final void run() {
            PhotoViewer.this.lambda$new$0$PhotoViewer();
        }
    };
    /* access modifiers changed from: private */
    public RadialProgressView miniProgressView;
    private float moveStartX;
    private float moveStartY;
    /* access modifiers changed from: private */
    public boolean moving;
    /* access modifiers changed from: private */
    public ImageView muteItem;
    /* access modifiers changed from: private */
    public boolean muteVideo;
    private String nameOverride;
    private TextView nameTextView;
    /* access modifiers changed from: private */
    public boolean needCaptionLayout;
    private boolean needSearchImageInArr;
    /* access modifiers changed from: private */
    public boolean needShowOnReady;
    private boolean openAnimationInProgress;
    /* access modifiers changed from: private */
    public boolean openedFullScreenVideo;
    private boolean opennedFromMedia;
    /* access modifiers changed from: private */
    public int originalBitrate;
    /* access modifiers changed from: private */
    public int originalHeight;
    private long originalSize;
    /* access modifiers changed from: private */
    public int originalWidth;
    /* access modifiers changed from: private */
    public boolean padImageForHorizontalInsets;
    private ImageView paintItem;
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public ChatAttachAlert parentAlert;
    /* access modifiers changed from: private */
    public ChatActivity parentChatActivity;
    /* access modifiers changed from: private */
    public PhotoCropView photoCropView;
    /* access modifiers changed from: private */
    public PhotoFilterView photoFilterView;
    /* access modifiers changed from: private */
    public PhotoPaintView photoPaintView;
    private PhotoProgressView[] photoProgressViews = new PhotoProgressView[3];
    /* access modifiers changed from: private */
    public CounterView photosCounterView;
    /* access modifiers changed from: private */
    public FrameLayout pickerView;
    /* access modifiers changed from: private */
    public ImageView pickerViewSendButton;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartScale = 1.0f;
    private float pinchStartX;
    private float pinchStartY;
    /* access modifiers changed from: private */
    public boolean pipAnimationInProgress;
    private boolean pipAvailable;
    /* access modifiers changed from: private */
    public ActionBarMenuItem pipItem;
    /* access modifiers changed from: private */
    public int[] pipPosition = new int[2];
    /* access modifiers changed from: private */
    public PipVideoView pipVideoView;
    /* access modifiers changed from: private */
    public PhotoViewerProvider placeProvider;
    private View playButtonAccessibilityOverlay;
    private boolean playerInjected;
    private boolean playerWasReady;
    private GradientDrawable[] pressedDrawable = new GradientDrawable[2];
    private float[] pressedDrawableAlpha = new float[2];
    private int previewViewEnd;
    private int previousCompression;
    /* access modifiers changed from: private */
    public RadialProgressView progressView;
    /* access modifiers changed from: private */
    public QualityChooseView qualityChooseView;
    /* access modifiers changed from: private */
    public AnimatorSet qualityChooseViewAnimation;
    /* access modifiers changed from: private */
    public PickerBottomLayoutViewer qualityPicker;
    private boolean requestingPreview;
    private TextView resetButton;
    /* access modifiers changed from: private */
    public int resultHeight;
    /* access modifiers changed from: private */
    public int resultWidth;
    private ImageReceiver rightImage = new ImageReceiver();
    private ImageView rotateItem;
    /* access modifiers changed from: private */
    public int rotationValue;
    /* access modifiers changed from: private */
    public float scale = 1.0f;
    private Scroller scroller;
    /* access modifiers changed from: private */
    public ArrayList<SecureDocument> secureDocuments = new ArrayList<>();
    /* access modifiers changed from: private */
    public float seekToProgressPending;
    private float seekToProgressPending2;
    /* access modifiers changed from: private */
    public int selectedCompression;
    /* access modifiers changed from: private */
    public ListAdapter selectedPhotosAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView selectedPhotosListView;
    private ActionBarMenuItem sendItem;
    /* access modifiers changed from: private */
    public int sendPhotoType;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    private Runnable setLoadingRunnable = new Runnable() {
        public void run() {
            if (PhotoViewer.this.currentMessageObject != null) {
                FileLoader.getInstance(PhotoViewer.this.currentMessageObject.currentAccount).setLoadingVideo(PhotoViewer.this.currentMessageObject.getDocument(), true, false);
            }
        }
    };
    private ImageView shareButton;
    /* access modifiers changed from: private */
    public int sharedMediaType;
    /* access modifiers changed from: private */
    public String shouldSavePositionForCurrentVideo;
    /* access modifiers changed from: private */
    public PlaceProviderObject showAfterAnimation;
    private ImageReceiver sideImage;
    private boolean skipFirstBufferingProgress;
    /* access modifiers changed from: private */
    public int slideshowMessageId;
    private long startTime;
    private long startedPlayTime;
    private boolean streamingAlertShown;
    /* access modifiers changed from: private */
    public TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (PhotoViewer.this.videoTextureView == null || !PhotoViewer.this.changingTextureView) {
                return true;
            }
            if (PhotoViewer.this.switchingInlineMode) {
                int unused = PhotoViewer.this.waitingForFirstTextureUpload = 2;
            }
            PhotoViewer.this.videoTextureView.setSurfaceTexture(surfaceTexture);
            PhotoViewer.this.videoTextureView.setVisibility(0);
            boolean unused2 = PhotoViewer.this.changingTextureView = false;
            PhotoViewer.this.containerView.invalidate();
            return false;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            if (PhotoViewer.this.waitingForFirstTextureUpload == 1) {
                PhotoViewer.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        PhotoViewer.this.changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (PhotoViewer.this.textureImageView != null) {
                            PhotoViewer.this.textureImageView.setVisibility(4);
                            PhotoViewer.this.textureImageView.setImageDrawable((Drawable) null);
                            if (PhotoViewer.this.currentBitmap != null) {
                                PhotoViewer.this.currentBitmap.recycle();
                                Bitmap unused = PhotoViewer.this.currentBitmap = null;
                            }
                        }
                        AndroidUtilities.runOnUIThread(
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0052: INVOKE  
                              (wrap: org.telegram.ui.-$$Lambda$PhotoViewer$4$1$chreZZnt0YkItDSej6M4QHP4VU4 : 0x004f: CONSTRUCTOR  (r0v7 org.telegram.ui.-$$Lambda$PhotoViewer$4$1$chreZZnt0YkItDSej6M4QHP4VU4) = (r2v0 'this' org.telegram.ui.PhotoViewer$4$1 A[THIS]) call: org.telegram.ui.-$$Lambda$PhotoViewer$4$1$chreZZnt0YkItDSej6M4QHP4VU4.<init>(org.telegram.ui.PhotoViewer$4$1):void type: CONSTRUCTOR)
                             org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.PhotoViewer.4.1.onPreDraw():boolean, dex: classes.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1257)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1257)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
                            	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
                            	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x004f: CONSTRUCTOR  (r0v7 org.telegram.ui.-$$Lambda$PhotoViewer$4$1$chreZZnt0YkItDSej6M4QHP4VU4) = (r2v0 'this' org.telegram.ui.PhotoViewer$4$1 A[THIS]) call: org.telegram.ui.-$$Lambda$PhotoViewer$4$1$chreZZnt0YkItDSej6M4QHP4VU4.<init>(org.telegram.ui.PhotoViewer$4$1):void type: CONSTRUCTOR in method: org.telegram.ui.PhotoViewer.4.1.onPreDraw():boolean, dex: classes.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 81 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.-$$Lambda$PhotoViewer$4$1$chreZZnt0YkItDSej6M4QHP4VU4, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 87 more
                            */
                        /*
                            this = this;
                            org.telegram.ui.PhotoViewer$4 r0 = org.telegram.ui.PhotoViewer.AnonymousClass4.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.view.TextureView r0 = r0.changedTextureView
                            android.view.ViewTreeObserver r0 = r0.getViewTreeObserver()
                            r0.removeOnPreDrawListener(r2)
                            org.telegram.ui.PhotoViewer$4 r0 = org.telegram.ui.PhotoViewer.AnonymousClass4.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.widget.ImageView r0 = r0.textureImageView
                            if (r0 == 0) goto L_0x004d
                            org.telegram.ui.PhotoViewer$4 r0 = org.telegram.ui.PhotoViewer.AnonymousClass4.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.widget.ImageView r0 = r0.textureImageView
                            r1 = 4
                            r0.setVisibility(r1)
                            org.telegram.ui.PhotoViewer$4 r0 = org.telegram.ui.PhotoViewer.AnonymousClass4.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.widget.ImageView r0 = r0.textureImageView
                            r1 = 0
                            r0.setImageDrawable(r1)
                            org.telegram.ui.PhotoViewer$4 r0 = org.telegram.ui.PhotoViewer.AnonymousClass4.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.graphics.Bitmap r0 = r0.currentBitmap
                            if (r0 == 0) goto L_0x004d
                            org.telegram.ui.PhotoViewer$4 r0 = org.telegram.ui.PhotoViewer.AnonymousClass4.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.graphics.Bitmap r0 = r0.currentBitmap
                            r0.recycle()
                            org.telegram.ui.PhotoViewer$4 r0 = org.telegram.ui.PhotoViewer.AnonymousClass4.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.graphics.Bitmap unused = r0.currentBitmap = r1
                        L_0x004d:
                            org.telegram.ui.-$$Lambda$PhotoViewer$4$1$chreZZnt0YkItDSej6M4QHP4VU4 r0 = new org.telegram.ui.-$$Lambda$PhotoViewer$4$1$chreZZnt0YkItDSej6M4QHP4VU4
                            r0.<init>(r2)
                            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                            org.telegram.ui.PhotoViewer$4 r0 = org.telegram.ui.PhotoViewer.AnonymousClass4.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            r1 = 0
                            int unused = r0.waitingForFirstTextureUpload = r1
                            r0 = 1
                            return r0
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.AnonymousClass4.AnonymousClass1.onPreDraw():boolean");
                    }

                    public /* synthetic */ void lambda$onPreDraw$0$PhotoViewer$4$1() {
                        if (PhotoViewer.this.isInline) {
                            PhotoViewer.this.dismissInternal();
                        }
                    }
                });
                PhotoViewer.this.changedTextureView.invalidate();
            }
        }
    };
    /* access modifiers changed from: private */
    public TextView switchCaptionTextView;
    private int switchImageAfterAnimation;
    /* access modifiers changed from: private */
    public Runnable switchToInlineRunnable = new Runnable() {
        public void run() {
            boolean unused = PhotoViewer.this.switchingInlineMode = false;
            if (PhotoViewer.this.currentBitmap != null) {
                PhotoViewer.this.currentBitmap.recycle();
                Bitmap unused2 = PhotoViewer.this.currentBitmap = null;
            }
            boolean unused3 = PhotoViewer.this.changingTextureView = true;
            if (PhotoViewer.this.textureImageView != null) {
                try {
                    Bitmap unused4 = PhotoViewer.this.currentBitmap = Bitmaps.createBitmap(PhotoViewer.this.videoTextureView.getWidth(), PhotoViewer.this.videoTextureView.getHeight(), Bitmap.Config.ARGB_8888);
                    PhotoViewer.this.videoTextureView.getBitmap(PhotoViewer.this.currentBitmap);
                } catch (Throwable th) {
                    if (PhotoViewer.this.currentBitmap != null) {
                        PhotoViewer.this.currentBitmap.recycle();
                        Bitmap unused5 = PhotoViewer.this.currentBitmap = null;
                    }
                    FileLog.e(th);
                }
                if (PhotoViewer.this.currentBitmap != null) {
                    PhotoViewer.this.textureImageView.setVisibility(0);
                    PhotoViewer.this.textureImageView.setImageBitmap(PhotoViewer.this.currentBitmap);
                } else {
                    PhotoViewer.this.textureImageView.setImageDrawable((Drawable) null);
                }
            }
            boolean unused6 = PhotoViewer.this.isInline = true;
            PipVideoView unused7 = PhotoViewer.this.pipVideoView = new PipVideoView(false);
            PhotoViewer photoViewer = PhotoViewer.this;
            PipVideoView access$1500 = photoViewer.pipVideoView;
            Activity access$2800 = PhotoViewer.this.parentActivity;
            PhotoViewer photoViewer2 = PhotoViewer.this;
            TextureView unused8 = photoViewer.changedTextureView = access$1500.show(access$2800, photoViewer2, photoViewer2.aspectRatioFrameLayout.getAspectRatio(), PhotoViewer.this.aspectRatioFrameLayout.getVideoRotation());
            PhotoViewer.this.changedTextureView.setVisibility(4);
            PhotoViewer.this.aspectRatioFrameLayout.removeView(PhotoViewer.this.videoTextureView);
        }
    };
    /* access modifiers changed from: private */
    public boolean switchingInlineMode;
    private int switchingToIndex;
    /* access modifiers changed from: private */
    public ImageView textureImageView;
    /* access modifiers changed from: private */
    public boolean textureUploaded;
    private ImageView timeItem;
    private Tooltip tooltip;
    private int totalImagesCount;
    private int totalImagesCountMerge;
    /* access modifiers changed from: private */
    public long transitionAnimationStartTime;
    /* access modifiers changed from: private */
    public float translationX;
    /* access modifiers changed from: private */
    public float translationY;
    private boolean tryStartRequestPreviewOnFinish;
    private ImageView tuneItem;
    /* access modifiers changed from: private */
    public Runnable updateProgressRunnable = new Runnable() {
        public void run() {
            float f;
            if (PhotoViewer.this.videoPlayer != null) {
                if (!PhotoViewer.this.isCurrentVideo) {
                    float currentPosition = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                    if (PhotoViewer.this.currentVideoFinishedLoading) {
                        f = 1.0f;
                    } else {
                        long elapsedRealtime = SystemClock.elapsedRealtime();
                        if (Math.abs(elapsedRealtime - PhotoViewer.this.lastBufferedPositionCheck) >= 500) {
                            if (PhotoViewer.this.isStreaming) {
                                f = FileLoader.getInstance(PhotoViewer.this.currentAccount).getBufferedProgressFromPosition(PhotoViewer.this.seekToProgressPending != 0.0f ? PhotoViewer.this.seekToProgressPending : currentPosition, PhotoViewer.this.currentFileNames[0]);
                            } else {
                                f = 1.0f;
                            }
                            long unused = PhotoViewer.this.lastBufferedPositionCheck = elapsedRealtime;
                        } else {
                            f = -1.0f;
                        }
                    }
                    if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility() != 0) {
                        if (PhotoViewer.this.seekToProgressPending == 0.0f) {
                            PhotoViewer.this.videoPlayerSeekbar.setProgress(currentPosition);
                        }
                        if (f != -1.0f) {
                            PhotoViewer.this.videoPlayerSeekbar.setBufferedProgress(f);
                            if (PhotoViewer.this.pipVideoView != null) {
                                PhotoViewer.this.pipVideoView.setBufferedProgress(f);
                            }
                        }
                    } else if (currentPosition >= PhotoViewer.this.videoTimelineView.getRightProgress()) {
                        PhotoViewer.this.videoPlayer.pause();
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration()))));
                        PhotoViewer.this.containerView.invalidate();
                    } else {
                        float leftProgress = currentPosition - PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (leftProgress < 0.0f) {
                            leftProgress = 0.0f;
                        }
                        currentPosition = leftProgress / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
                        if (currentPosition > 1.0f) {
                            currentPosition = 1.0f;
                        }
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(currentPosition);
                    }
                    PhotoViewer.this.videoPlayerControlFrameLayout.invalidate();
                    if (PhotoViewer.this.shouldSavePositionForCurrentVideo != null && currentPosition >= 0.0f && PhotoViewer.this.shouldSavePositionForCurrentVideo != null && SystemClock.elapsedRealtime() - PhotoViewer.this.lastSaveTime >= 1000) {
                        String unused2 = PhotoViewer.this.shouldSavePositionForCurrentVideo;
                        long unused3 = PhotoViewer.this.lastSaveTime = SystemClock.elapsedRealtime();
                        Utilities.globalQueue.postRunnable(new Runnable(currentPosition) {
                            private final /* synthetic */ float f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                PhotoViewer.AnonymousClass2.this.lambda$run$0$PhotoViewer$2(this.f$1);
                            }
                        });
                    }
                    PhotoViewer.this.updateVideoPlayerTime();
                } else if (!PhotoViewer.this.videoTimelineView.isDragging()) {
                    float currentPosition2 = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                    if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility() != 0) {
                        PhotoViewer.this.videoTimelineView.setProgress(currentPosition2);
                    } else if (currentPosition2 >= PhotoViewer.this.videoTimelineView.getRightProgress()) {
                        PhotoViewer.this.videoTimelineView.setProgress(0.0f);
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration()))));
                        if (PhotoViewer.this.muteVideo) {
                            PhotoViewer.this.videoPlayer.play();
                        } else {
                            PhotoViewer.this.videoPlayer.pause();
                        }
                        PhotoViewer.this.containerView.invalidate();
                    } else {
                        float leftProgress2 = currentPosition2 - PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (leftProgress2 < 0.0f) {
                            leftProgress2 = 0.0f;
                        }
                        float rightProgress = leftProgress2 / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
                        if (rightProgress > 1.0f) {
                            rightProgress = 1.0f;
                        }
                        PhotoViewer.this.videoTimelineView.setProgress(rightProgress);
                    }
                    PhotoViewer.this.updateVideoPlayerTime();
                }
            }
            if (PhotoViewer.this.isPlaying) {
                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable, 17);
            }
        }

        public /* synthetic */ void lambda$run$0$PhotoViewer$2(float f) {
            ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).edit().putFloat(PhotoViewer.this.shouldSavePositionForCurrentVideo, f).commit();
        }
    };
    /* access modifiers changed from: private */
    public boolean useSmoothKeyboard;
    private VelocityTracker velocityTracker;
    private ImageView videoBackwardButton;
    /* access modifiers changed from: private */
    public boolean videoConvertSupported;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    private float videoCutEnd;
    private float videoCutStart;
    /* access modifiers changed from: private */
    public float videoDuration;
    private ImageView videoForwardButton;
    private VideoForwardDrawable videoForwardDrawable;
    /* access modifiers changed from: private */
    public int videoFramerate;
    /* access modifiers changed from: private */
    public long videoFramesSize;
    private ImageView videoPlayButton;
    /* access modifiers changed from: private */
    public VideoPlayer videoPlayer;
    /* access modifiers changed from: private */
    public FrameLayout videoPlayerControlFrameLayout;
    /* access modifiers changed from: private */
    public SeekBar videoPlayerSeekbar;
    /* access modifiers changed from: private */
    public SimpleTextView videoPlayerTime;
    /* access modifiers changed from: private */
    public VideoSeekPreviewImage videoPreviewFrame;
    /* access modifiers changed from: private */
    public AnimatorSet videoPreviewFrameAnimation;
    private MessageObject videoPreviewMessageObject;
    /* access modifiers changed from: private */
    public TextureView videoTextureView;
    /* access modifiers changed from: private */
    public VideoTimelinePlayView videoTimelineView;
    private AlertDialog visibleDialog;
    private int waitingForDraw;
    /* access modifiers changed from: private */
    public int waitingForFirstTextureUpload;
    /* access modifiers changed from: private */
    public boolean wasLayout;
    /* access modifiers changed from: private */
    public WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    public FrameLayout windowView;
    /* access modifiers changed from: private */
    public boolean zoomAnimation;
    private boolean zooming;

    public static class EmptyPhotoViewerProvider implements PhotoViewerProvider {
        public boolean allowCaption() {
            return true;
        }

        public boolean canCaptureMorePhotos() {
            return true;
        }

        public boolean canScrollAway() {
            return true;
        }

        public boolean cancelButtonPressed() {
            return true;
        }

        public void deleteImageAtIndex(int i) {
        }

        public String getDeleteMessageString() {
            return null;
        }

        public int getPhotoIndex(int i) {
            return -1;
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z) {
            return null;
        }

        public int getSelectedCount() {
            return 0;
        }

        public HashMap<Object, Object> getSelectedPhotos() {
            return null;
        }

        public ArrayList<Object> getSelectedPhotosOrder() {
            return null;
        }

        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i) {
            return null;
        }

        public boolean isPhotoChecked(int i) {
            return false;
        }

        public void needAddMorePhotos() {
        }

        public boolean scaleToFill() {
            return false;
        }

        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2) {
        }

        public int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo) {
            return -1;
        }

        public int setPhotoUnchecked(Object obj) {
            return -1;
        }

        public void updatePhotoAtIndex(int i) {
        }

        public void willHidePhotoViewer() {
        }

        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i) {
        }
    }

    public interface PhotoViewerProvider {
        boolean allowCaption();

        boolean canCaptureMorePhotos();

        boolean canScrollAway();

        boolean cancelButtonPressed();

        void deleteImageAtIndex(int i);

        String getDeleteMessageString();

        int getPhotoIndex(int i);

        PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z);

        int getSelectedCount();

        HashMap<Object, Object> getSelectedPhotos();

        ArrayList<Object> getSelectedPhotosOrder();

        ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i);

        boolean isPhotoChecked(int i);

        void needAddMorePhotos();

        boolean scaleToFill();

        void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2);

        int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo);

        int setPhotoUnchecked(Object obj);

        void updatePhotoAtIndex(int i);

        void willHidePhotoViewer();

        void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i);
    }

    public static class PlaceProviderObject {
        public ClippingImageView animatingImageView;
        public int animatingImageViewYOffset;
        public int clipBottomAddition;
        public int clipTopAddition;
        public int dialogId;
        public ImageReceiver imageReceiver;
        public int index;
        public boolean isEvent;
        public View parentView;
        public int[] radius;
        public float scale = 1.0f;
        public int size;
        public ImageReceiver.BitmapHolder thumb;
        public int viewX;
        public int viewY;
    }

    static /* synthetic */ boolean lambda$null$16(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ boolean lambda$null$17(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    public void onLongPress(MotionEvent motionEvent) {
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public /* synthetic */ void lambda$new$0$PhotoViewer() {
        toggleMiniProgressInternal(true);
    }

    private class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    URLSpanNoUnderline[] uRLSpanNoUnderlineArr = (URLSpanNoUnderline[]) spannable.getSpans(textView.getSelectionStart(), textView.getSelectionEnd(), URLSpanNoUnderline.class);
                    if (uRLSpanNoUnderlineArr != null && uRLSpanNoUnderlineArr.length > 0) {
                        String url = uRLSpanNoUnderlineArr[0].getURL();
                        if (!(!url.startsWith("video") || PhotoViewer.this.videoPlayer == null || PhotoViewer.this.currentMessageObject == null)) {
                            int intValue = Utilities.parseInt(url).intValue();
                            if (PhotoViewer.this.videoPlayer.getDuration() == -9223372036854775807L) {
                                float unused = PhotoViewer.this.seekToProgressPending = ((float) intValue) / ((float) PhotoViewer.this.currentMessageObject.getDuration());
                            } else {
                                PhotoViewer.this.videoPlayer.seekTo(((long) intValue) * 1000);
                            }
                        }
                    }
                    Selection.removeSelection(spannable);
                }
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    private class BackgroundDrawable extends ColorDrawable {
        private boolean allowDrawContent;
        /* access modifiers changed from: private */
        public Runnable drawRunnable;
        private final Paint paint = new Paint(1);
        private final RectF rect = new RectF();
        private final RectF visibleRect = new RectF();

        public BackgroundDrawable(int i) {
            super(i);
            this.paint.setColor(i);
        }

        @Keep
        public void setAlpha(int i) {
            if (PhotoViewer.this.parentActivity instanceof LaunchActivity) {
                this.allowDrawContent = !PhotoViewer.this.isVisible || i != 255;
                ((LaunchActivity) PhotoViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent(this.allowDrawContent);
                if (PhotoViewer.this.parentAlert != null) {
                    if (!this.allowDrawContent) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                PhotoViewer.BackgroundDrawable.this.lambda$setAlpha$0$PhotoViewer$BackgroundDrawable();
                            }
                        }, 50);
                    } else if (PhotoViewer.this.parentAlert != null) {
                        PhotoViewer.this.parentAlert.setAllowDrawContent(this.allowDrawContent);
                    }
                }
            }
            super.setAlpha(i);
            this.paint.setAlpha(i);
        }

        public /* synthetic */ void lambda$setAlpha$0$PhotoViewer$BackgroundDrawable() {
            if (PhotoViewer.this.parentAlert != null) {
                PhotoViewer.this.parentAlert.setAllowDrawContent(this.allowDrawContent);
            }
        }

        public void draw(Canvas canvas) {
            Runnable runnable;
            if (PhotoViewer.this.animationInProgress == 0 || AndroidUtilities.isTablet() || PhotoViewer.this.currentPlaceObject == null || PhotoViewer.this.currentPlaceObject.animatingImageView == null) {
                super.draw(canvas);
            } else {
                PhotoViewer.this.animatingImageView.getClippedVisibleRect(this.visibleRect);
                if (!this.visibleRect.isEmpty()) {
                    this.visibleRect.inset((float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f));
                    Rect bounds = getBounds();
                    float f = (float) bounds.right;
                    float f2 = (float) bounds.bottom;
                    for (int i = 0; i < 4; i++) {
                        if (i == 0) {
                            RectF rectF = this.rect;
                            RectF rectF2 = this.visibleRect;
                            rectF.set(0.0f, rectF2.top, rectF2.left, rectF2.bottom);
                        } else if (i == 1) {
                            this.rect.set(0.0f, 0.0f, f, this.visibleRect.top);
                        } else if (i == 2) {
                            RectF rectF3 = this.rect;
                            RectF rectF4 = this.visibleRect;
                            rectF3.set(rectF4.right, rectF4.top, f, rectF4.bottom);
                        } else if (i == 3) {
                            this.rect.set(0.0f, this.visibleRect.bottom, f, f2);
                        }
                        canvas.drawRect(this.rect, this.paint);
                    }
                }
            }
            if (getAlpha() != 0 && (runnable = this.drawRunnable) != null) {
                AndroidUtilities.runOnUIThread(runnable);
                this.drawRunnable = null;
            }
        }
    }

    private class CounterView extends View {
        private int currentCount = 0;
        private int height;
        private Paint paint;
        private RectF rect;
        private float rotation;
        private StaticLayout staticLayout;
        private TextPaint textPaint = new TextPaint(1);
        private int width;

        public CounterView(Context context) {
            super(context);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(18.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textPaint.setColor(-1);
            this.paint = new Paint(1);
            this.paint.setColor(-1);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeJoin(Paint.Join.ROUND);
            this.rect = new RectF();
            setCount(0);
        }

        @Keep
        public void setScaleX(float f) {
            super.setScaleX(f);
            invalidate();
        }

        @Keep
        public void setRotationX(float f) {
            this.rotation = f;
            invalidate();
        }

        public float getRotationX() {
            return this.rotation;
        }

        public void setCount(int i) {
            this.staticLayout = new StaticLayout("" + Math.max(1, i), this.textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.width = (int) Math.ceil((double) this.staticLayout.getLineWidth(0));
            this.height = this.staticLayout.getLineBottom(0);
            AnimatorSet animatorSet = new AnimatorSet();
            if (i == 0) {
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofInt(this.paint, AnimationProperties.PAINT_ALPHA, new int[]{0}), ObjectAnimator.ofInt(this.textPaint, AnimationProperties.PAINT_ALPHA, new int[]{0})});
                animatorSet.setInterpolator(new DecelerateInterpolator());
            } else {
                int i2 = this.currentCount;
                if (i2 == 0) {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.0f, 1.0f}), ObjectAnimator.ofInt(this.paint, AnimationProperties.PAINT_ALPHA, new int[]{0, 255}), ObjectAnimator.ofInt(this.textPaint, AnimationProperties.PAINT_ALPHA, new int[]{0, 255})});
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                } else if (i < i2) {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{1.1f, 1.0f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{1.1f, 1.0f})});
                    animatorSet.setInterpolator(new OvershootInterpolator());
                } else {
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, View.SCALE_X, new float[]{0.9f, 1.0f}), ObjectAnimator.ofFloat(this, View.SCALE_Y, new float[]{0.9f, 1.0f})});
                    animatorSet.setInterpolator(new OvershootInterpolator());
                }
            }
            animatorSet.setDuration(180);
            animatorSet.start();
            requestLayout();
            this.currentCount = i;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.max(this.width + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(30.0f)), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int measuredHeight = getMeasuredHeight() / 2;
            this.paint.setAlpha(255);
            this.rect.set((float) AndroidUtilities.dp(1.0f), (float) (measuredHeight - AndroidUtilities.dp(14.0f)), (float) (getMeasuredWidth() - AndroidUtilities.dp(1.0f)), (float) (measuredHeight + AndroidUtilities.dp(14.0f)));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), this.paint);
            if (this.staticLayout != null) {
                this.textPaint.setAlpha((int) ((1.0f - this.rotation) * 255.0f));
                canvas.save();
                canvas.translate((float) ((getMeasuredWidth() - this.width) / 2), ((float) ((getMeasuredHeight() - this.height) / 2)) + AndroidUtilities.dpf2(0.2f) + (this.rotation * ((float) AndroidUtilities.dp(5.0f))));
                this.staticLayout.draw(canvas);
                canvas.restore();
                this.paint.setAlpha((int) (this.rotation * 255.0f));
                int centerX = (int) this.rect.centerX();
                int centerY = (int) (((float) ((int) this.rect.centerY())) - ((((float) AndroidUtilities.dp(5.0f)) * (1.0f - this.rotation)) + ((float) AndroidUtilities.dp(3.0f))));
                canvas.drawLine((float) (AndroidUtilities.dp(0.5f) + centerX), (float) (centerY - AndroidUtilities.dp(0.5f)), (float) (centerX - AndroidUtilities.dp(6.0f)), (float) (AndroidUtilities.dp(6.0f) + centerY), this.paint);
                canvas.drawLine((float) (centerX - AndroidUtilities.dp(0.5f)), (float) (centerY - AndroidUtilities.dp(0.5f)), (float) (centerX + AndroidUtilities.dp(6.0f)), (float) (centerY + AndroidUtilities.dp(6.0f)), this.paint);
            }
        }
    }

    private class PhotoProgressView {
        private float alpha = 1.0f;
        private float animatedAlphaValue = 1.0f;
        private float animatedProgressValue = 0.0f;
        private float animationProgressStart = 0.0f;
        /* access modifiers changed from: private */
        public int backgroundState = -1;
        private float currentProgress = 0.0f;
        private long currentProgressTime = 0;
        private long lastUpdateTime = 0;
        private View parent;
        /* access modifiers changed from: private */
        public int previousBackgroundState = -2;
        private RectF progressRect = new RectF();
        private float radOffset = 0.0f;
        private float scale = 1.0f;
        private int size = AndroidUtilities.dp(64.0f);

        public PhotoProgressView(Context context, View view) {
            if (PhotoViewer.decelerateInterpolator == null) {
                DecelerateInterpolator unused = PhotoViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                Paint unused2 = PhotoViewer.progressPaint = new Paint(1);
                PhotoViewer.progressPaint.setStyle(Paint.Style.STROKE);
                PhotoViewer.progressPaint.setStrokeCap(Paint.Cap.ROUND);
                PhotoViewer.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
                PhotoViewer.progressPaint.setColor(-1);
            }
            this.parent = view;
        }

        private void updateAnimation() {
            long currentTimeMillis = System.currentTimeMillis();
            long j = currentTimeMillis - this.lastUpdateTime;
            if (j > 18) {
                j = 18;
            }
            this.lastUpdateTime = currentTimeMillis;
            if (!(this.animatedProgressValue == 1.0f && this.currentProgress == 1.0f)) {
                this.radOffset += ((float) (360 * j)) / 3000.0f;
                float f = this.currentProgress - this.animationProgressStart;
                if (Math.abs(f) > 0.0f) {
                    this.currentProgressTime += j;
                    if (this.currentProgressTime >= 300) {
                        float f2 = this.currentProgress;
                        this.animatedProgressValue = f2;
                        this.animationProgressStart = f2;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = this.animationProgressStart + (f * PhotoViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f));
                    }
                }
                this.parent.invalidate();
            }
            if (this.animatedProgressValue >= 1.0f && this.previousBackgroundState != -2) {
                this.animatedAlphaValue -= ((float) j) / 200.0f;
                if (this.animatedAlphaValue <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousBackgroundState = -2;
                }
                this.parent.invalidate();
            }
        }

        public void setProgress(float f, boolean z) {
            if (!z) {
                this.animatedProgressValue = f;
                this.animationProgressStart = f;
            } else {
                this.animationProgressStart = this.animatedProgressValue;
            }
            this.currentProgress = f;
            this.currentProgressTime = 0;
            this.parent.invalidate();
        }

        public void setBackgroundState(int i, boolean z) {
            int i2;
            if (this.backgroundState != i) {
                this.lastUpdateTime = System.currentTimeMillis();
                if (!z || (i2 = this.backgroundState) == i) {
                    this.previousBackgroundState = -2;
                } else {
                    this.previousBackgroundState = i2;
                    this.animatedAlphaValue = 1.0f;
                }
                this.backgroundState = i;
                this.parent.invalidate();
            }
        }

        public void setAlpha(float f) {
            this.alpha = f;
        }

        public void setScale(float f) {
            this.scale = f;
        }

        public void onDraw(Canvas canvas) {
            int i;
            Drawable drawable;
            Drawable drawable2;
            int i2 = (int) (((float) this.size) * this.scale);
            int access$3900 = (PhotoViewer.this.getContainerViewWidth() - i2) / 2;
            int access$4000 = (PhotoViewer.this.getContainerViewHeight() - i2) / 2;
            int i3 = this.previousBackgroundState;
            if (i3 >= 0 && i3 < 4 && (drawable2 = PhotoViewer.progressDrawables[this.previousBackgroundState]) != null) {
                drawable2.setAlpha((int) (this.animatedAlphaValue * 255.0f * this.alpha));
                drawable2.setBounds(access$3900, access$4000, access$3900 + i2, access$4000 + i2);
                drawable2.draw(canvas);
            }
            int i4 = this.backgroundState;
            if (i4 >= 0 && i4 < 4 && (drawable = PhotoViewer.progressDrawables[this.backgroundState]) != null) {
                if (this.previousBackgroundState != -2) {
                    drawable.setAlpha((int) ((1.0f - this.animatedAlphaValue) * 255.0f * this.alpha));
                } else {
                    drawable.setAlpha((int) (this.alpha * 255.0f));
                }
                drawable.setBounds(access$3900, access$4000, access$3900 + i2, access$4000 + i2);
                drawable.draw(canvas);
            }
            int i5 = this.backgroundState;
            if (i5 == 0 || i5 == 1 || (i = this.previousBackgroundState) == 0 || i == 1) {
                int dp = AndroidUtilities.dp(4.0f);
                if (this.previousBackgroundState != -2) {
                    PhotoViewer.progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f * this.alpha));
                } else {
                    PhotoViewer.progressPaint.setAlpha((int) (this.alpha * 255.0f));
                }
                this.progressRect.set((float) (access$3900 + dp), (float) (access$4000 + dp), (float) ((access$3900 + i2) - dp), (float) ((access$4000 + i2) - dp));
                canvas.drawArc(this.progressRect, this.radOffset - 0.049804688f, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, PhotoViewer.progressPaint);
                updateAnimation();
            }
        }
    }

    private class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto {
        private boolean ignoreLayout;
        private Paint paint = new Paint();

        public FrameLayoutDrawer(Context context) {
            super(context, false);
            setWillNotDraw(false);
            this.paint.setColor(NUM);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            this.ignoreLayout = true;
            TextView access$4200 = PhotoViewer.this.captionTextView;
            Point point = AndroidUtilities.displaySize;
            access$4200.setMaxLines(point.x > point.y ? 5 : 10);
            this.ignoreLayout = false;
            measureChildWithMargins(PhotoViewer.this.captionEditText, i, 0, i2, 0);
            int measuredHeight = PhotoViewer.this.captionEditText.getMeasuredHeight();
            int paddingRight = size - (getPaddingRight() + getPaddingLeft());
            int paddingBottom = size2 - getPaddingBottom();
            int childCount = getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                if (!(childAt.getVisibility() == 8 || childAt == PhotoViewer.this.captionEditText)) {
                    if (childAt == PhotoViewer.this.aspectRatioFrameLayout) {
                        childAt.measure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0), NUM));
                    } else if (!PhotoViewer.this.captionEditText.isPopupView(childAt)) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    } else if (!AndroidUtilities.isInMultiwindow) {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(paddingRight, NUM), View.MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                    } else if (AndroidUtilities.isTablet()) {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(paddingRight, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), (paddingBottom - measuredHeight) - AndroidUtilities.statusBarHeight), NUM));
                    } else {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(paddingRight, NUM), View.MeasureSpec.makeMeasureSpec((paddingBottom - measuredHeight) - AndroidUtilities.statusBarHeight, NUM));
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x0094  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x00a2  */
        /* JADX WARNING: Removed duplicated region for block: B:37:0x00b6  */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x00c3  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onLayout(boolean r16, int r17, int r18, int r19, int r20) {
            /*
                r15 = this;
                r0 = r15
                int r1 = r15.getChildCount()
                org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                boolean r2 = r2.useSmoothKeyboard
                r3 = 0
                if (r2 == 0) goto L_0x0010
                r2 = 0
                goto L_0x0014
            L_0x0010:
                int r2 = r15.getKeyboardHeight()
            L_0x0014:
                r4 = 1101004800(0x41a00000, float:20.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                if (r2 > r4) goto L_0x002b
                boolean r2 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                if (r2 != 0) goto L_0x002b
                org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.PhotoViewerCaptionEnterView r2 = r2.captionEditText
                int r2 = r2.getEmojiPadding()
                goto L_0x002c
            L_0x002b:
                r2 = 0
            L_0x002c:
                if (r3 >= r1) goto L_0x01b6
                android.view.View r4 = r15.getChildAt(r3)
                int r5 = r4.getVisibility()
                r6 = 8
                if (r5 != r6) goto L_0x003c
                goto L_0x01b2
            L_0x003c:
                org.telegram.ui.PhotoViewer r5 = org.telegram.ui.PhotoViewer.this
                com.google.android.exoplayer2.ui.AspectRatioFrameLayout r5 = r5.aspectRatioFrameLayout
                if (r4 != r5) goto L_0x004b
                r5 = r17
                r6 = r19
                r7 = r20
                goto L_0x005d
            L_0x004b:
                int r5 = r15.getPaddingLeft()
                int r5 = r17 + r5
                int r6 = r15.getPaddingRight()
                int r6 = r19 - r6
                int r7 = r15.getPaddingBottom()
                int r7 = r20 - r7
            L_0x005d:
                android.view.ViewGroup$LayoutParams r8 = r4.getLayoutParams()
                android.widget.FrameLayout$LayoutParams r8 = (android.widget.FrameLayout.LayoutParams) r8
                int r9 = r4.getMeasuredWidth()
                int r10 = r4.getMeasuredHeight()
                int r11 = r8.gravity
                r12 = -1
                if (r11 != r12) goto L_0x0072
                r11 = 51
            L_0x0072:
                r12 = r11 & 7
                r11 = r11 & 112(0x70, float:1.57E-43)
                r12 = r12 & 7
                r13 = 5
                r14 = 1
                if (r12 == r14) goto L_0x0086
                if (r12 == r13) goto L_0x0081
                int r6 = r8.leftMargin
                goto L_0x0090
            L_0x0081:
                int r6 = r6 - r5
                int r6 = r6 - r9
                int r12 = r8.rightMargin
                goto L_0x008f
            L_0x0086:
                int r6 = r6 - r5
                int r6 = r6 - r9
                int r6 = r6 / 2
                int r12 = r8.leftMargin
                int r6 = r6 + r12
                int r12 = r8.rightMargin
            L_0x008f:
                int r6 = r6 - r12
            L_0x0090:
                r12 = 16
                if (r11 == r12) goto L_0x00a2
                r12 = 80
                if (r11 == r12) goto L_0x009b
                int r7 = r8.topMargin
                goto L_0x00ae
            L_0x009b:
                int r7 = r7 - r2
                int r7 = r7 - r18
                int r7 = r7 - r10
                int r8 = r8.bottomMargin
                goto L_0x00ad
            L_0x00a2:
                int r7 = r7 - r2
                int r7 = r7 - r18
                int r7 = r7 - r10
                int r7 = r7 / 2
                int r11 = r8.topMargin
                int r7 = r7 + r11
                int r8 = r8.bottomMargin
            L_0x00ad:
                int r7 = r7 - r8
            L_0x00ae:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.RecyclerListView r8 = r8.mentionListView
                if (r4 != r8) goto L_0x00c3
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = r8.captionEditText
                int r8 = r8.getMeasuredHeight()
            L_0x00c0:
                int r7 = r7 - r8
                goto L_0x01aa
            L_0x00c3:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = r8.captionEditText
                boolean r8 = r8.isPopupView(r4)
                if (r8 == 0) goto L_0x00f7
                boolean r7 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                if (r7 == 0) goto L_0x00eb
                org.telegram.ui.PhotoViewer r7 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.PhotoViewerCaptionEnterView r7 = r7.captionEditText
                int r7 = r7.getTop()
                int r8 = r4.getMeasuredHeight()
                int r7 = r7 - r8
                r8 = 1065353216(0x3var_, float:1.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r7 = r7 + r8
                goto L_0x01aa
            L_0x00eb:
                org.telegram.ui.PhotoViewer r7 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.PhotoViewerCaptionEnterView r7 = r7.captionEditText
                int r7 = r7.getBottom()
                goto L_0x01aa
            L_0x00f7:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.RecyclerListView r8 = r8.selectedPhotosListView
                if (r4 != r8) goto L_0x010b
                org.telegram.ui.PhotoViewer r7 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.ActionBar.ActionBar r7 = r7.actionBar
                int r7 = r7.getMeasuredHeight()
                goto L_0x01aa
            L_0x010b:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                android.widget.TextView r8 = r8.captionTextView
                if (r4 == r8) goto L_0x0190
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                android.widget.TextView r8 = r8.switchCaptionTextView
                if (r4 != r8) goto L_0x011d
                goto L_0x0190
            L_0x011d:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                android.widget.ImageView r8 = r8.cameraItem
                if (r4 != r8) goto L_0x0157
                org.telegram.ui.PhotoViewer r7 = org.telegram.ui.PhotoViewer.this
                android.widget.FrameLayout r7 = r7.pickerView
                int r7 = r7.getTop()
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                int r8 = r8.sendPhotoType
                r11 = 4
                if (r8 == r11) goto L_0x0144
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                int r8 = r8.sendPhotoType
                if (r8 != r13) goto L_0x0141
                goto L_0x0144
            L_0x0141:
                r8 = 1097859072(0x41700000, float:15.0)
                goto L_0x0146
            L_0x0144:
                r8 = 1109393408(0x42200000, float:40.0)
            L_0x0146:
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r7 = r7 - r8
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                android.widget.ImageView r8 = r8.cameraItem
                int r8 = r8.getMeasuredHeight()
                goto L_0x00c0
            L_0x0157:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.VideoSeekPreviewImage r8 = r8.videoPreviewFrame
                if (r4 != r8) goto L_0x01aa
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.GroupedPhotosListView r8 = r8.groupedPhotosListView
                java.util.ArrayList<org.telegram.messenger.ImageLocation> r8 = r8.currentPhotos
                boolean r8 = r8.isEmpty()
                if (r8 != 0) goto L_0x0178
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.GroupedPhotosListView r8 = r8.groupedPhotosListView
                int r8 = r8.getMeasuredHeight()
                int r7 = r7 - r8
            L_0x0178:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                android.widget.TextView r8 = r8.captionTextView
                int r8 = r8.getVisibility()
                if (r8 != 0) goto L_0x01aa
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                android.widget.TextView r8 = r8.captionTextView
                int r8 = r8.getMeasuredHeight()
                goto L_0x00c0
            L_0x0190:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.GroupedPhotosListView r8 = r8.groupedPhotosListView
                java.util.ArrayList<org.telegram.messenger.ImageLocation> r8 = r8.currentPhotos
                boolean r8 = r8.isEmpty()
                if (r8 != 0) goto L_0x01aa
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.GroupedPhotosListView r8 = r8.groupedPhotosListView
                int r8 = r8.getMeasuredHeight()
                goto L_0x00c0
            L_0x01aa:
                int r8 = r6 + r5
                int r6 = r6 + r9
                int r6 = r6 + r5
                int r10 = r10 + r7
                r4.layout(r8, r7, r6, r10)
            L_0x01b2:
                int r3 = r3 + 1
                goto L_0x002c
            L_0x01b6:
                r15.notifyHeightChanged()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.FrameLayoutDrawer.onLayout(boolean, int, int, int, int):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            PhotoViewer.this.onDraw(canvas);
            if (Build.VERSION.SDK_INT >= 21 && AndroidUtilities.statusBarHeight != 0 && PhotoViewer.this.actionBar != null) {
                this.paint.setAlpha((int) (PhotoViewer.this.actionBar.getAlpha() * 255.0f * 0.2f));
                canvas.drawRect(0.0f, (float) PhotoViewer.this.currentPanTranslationY, (float) getMeasuredWidth(), (float) (PhotoViewer.this.currentPanTranslationY + AndroidUtilities.statusBarHeight), this.paint);
                this.paint.setAlpha((int) (PhotoViewer.this.actionBar.getAlpha() * 255.0f * 0.498f));
                if (getPaddingRight() > 0) {
                    canvas.drawRect((float) (getMeasuredWidth() - getPaddingRight()), 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
                }
                if (getPaddingLeft() > 0) {
                    canvas.drawRect(0.0f, 0.0f, (float) getPaddingLeft(), (float) getMeasuredHeight(), this.paint);
                }
            }
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (view == PhotoViewer.this.mentionListView || view == PhotoViewer.this.captionEditText) {
                if (!PhotoViewer.this.captionEditText.isPopupShowing() && PhotoViewer.this.captionEditText.getEmojiPadding() == 0 && ((AndroidUtilities.usingHardwareInput && PhotoViewer.this.captionEditText.getTag() == null) || getKeyboardHeight() == 0)) {
                    return false;
                }
            } else if (view == PhotoViewer.this.cameraItem || view == PhotoViewer.this.pickerView || view == PhotoViewer.this.pickerViewSendButton || view == PhotoViewer.this.captionTextView || (PhotoViewer.this.muteItem.getVisibility() == 0 && view == PhotoViewer.this.bottomLayout)) {
                int emojiPadding = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : PhotoViewer.this.captionEditText.getEmojiPadding();
                if (PhotoViewer.this.captionEditText.isPopupShowing() || ((AndroidUtilities.usingHardwareInput && PhotoViewer.this.captionEditText.getTag() != null) || getKeyboardHeight() > AndroidUtilities.dp(80.0f) || emojiPadding != 0)) {
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.d("keyboard height = " + getKeyboardHeight() + " padding = " + emojiPadding);
                    }
                    boolean unused = PhotoViewer.this.bottomTouchEnabled = false;
                    return false;
                }
                boolean unused2 = PhotoViewer.this.bottomTouchEnabled = true;
            } else if (view == PhotoViewer.this.checkImageView || view == PhotoViewer.this.photosCounterView) {
                if (PhotoViewer.this.captionEditText.getTag() != null) {
                    boolean unused3 = PhotoViewer.this.bottomTouchEnabled = false;
                    return false;
                }
                boolean unused4 = PhotoViewer.this.bottomTouchEnabled = true;
            } else if (view == PhotoViewer.this.miniProgressView) {
                return false;
            }
            try {
                if (view == PhotoViewer.this.aspectRatioFrameLayout || !super.drawChild(canvas, view, j)) {
                    return false;
                }
                return true;
            } catch (Throwable unused5) {
                return true;
            }
        }

        public void requestLayout() {
            if (!this.ignoreLayout) {
                super.requestLayout();
            }
        }

        /* access modifiers changed from: protected */
        public void onPanTranslationUpdate(int i) {
            int unused = PhotoViewer.this.currentPanTranslationY = i;
            float f = (float) i;
            PhotoViewer.this.actionBar.setTranslationY(f);
            if (PhotoViewer.this.miniProgressView != null) {
                PhotoViewer.this.miniProgressView.setTranslationY(f);
            }
            if (PhotoViewer.this.progressView != null) {
                PhotoViewer.this.progressView.setTranslationY(f);
            }
            if (PhotoViewer.this.checkImageView != null) {
                PhotoViewer.this.checkImageView.setTranslationY(f);
            }
            if (PhotoViewer.this.photosCounterView != null) {
                PhotoViewer.this.photosCounterView.setTranslationY(f);
            }
            if (PhotoViewer.this.selectedPhotosListView != null) {
                PhotoViewer.this.selectedPhotosListView.setTranslationY(f);
            }
            if (PhotoViewer.this.aspectRatioFrameLayout != null) {
                PhotoViewer.this.aspectRatioFrameLayout.setTranslationY(f);
            }
            if (PhotoViewer.this.textureImageView != null) {
                PhotoViewer.this.textureImageView.setTranslationY(f);
            }
            if (PhotoViewer.this.photoCropView != null) {
                PhotoViewer.this.photoCropView.setTranslationY(f);
            }
            if (PhotoViewer.this.photoFilterView != null) {
                PhotoViewer.this.photoFilterView.setTranslationY(f);
            }
            if (PhotoViewer.this.photoPaintView != null) {
                PhotoViewer.this.photoPaintView.setTranslationY(f);
            }
            invalidate();
        }
    }

    public static PhotoViewer getPipInstance() {
        return PipInstance;
    }

    public static PhotoViewer getInstance() {
        PhotoViewer photoViewer = Instance;
        if (photoViewer == null) {
            synchronized (PhotoViewer.class) {
                photoViewer = Instance;
                if (photoViewer == null) {
                    photoViewer = new PhotoViewer();
                    Instance = photoViewer;
                }
            }
        }
        return photoViewer;
    }

    public boolean isOpenedFullScreenVideo() {
        return this.openedFullScreenVideo;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    public PhotoViewer() {
        this.blackPaint.setColor(-16777216);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:254:0x0505, code lost:
        if (r2.get(r2.size() - 1).getDialogId() != r0.mergeDialogId) goto L_0x0546;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x0544, code lost:
        if (r0.imagesArrTemp.get(0).getDialogId() != r0.mergeDialogId) goto L_0x0546;
     */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0553  */
    /* JADX WARNING: Removed duplicated region for block: B:378:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r22, int r23, java.lang.Object... r24) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            int r2 = org.telegram.messenger.NotificationCenter.fileDidFailToLoad
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = 3
            r5 = 2
            r6 = 1
            r7 = 0
            if (r1 != r2) goto L_0x0048
            r1 = r24[r7]
            java.lang.String r1 = (java.lang.String) r1
            r2 = 0
        L_0x0013:
            if (r2 >= r4) goto L_0x0689
            java.lang.String[] r8 = r0.currentFileNames
            r9 = r8[r2]
            if (r9 == 0) goto L_0x0045
            r8 = r8[r2]
            boolean r8 = r8.equals(r1)
            if (r8 == 0) goto L_0x0045
            if (r2 == 0) goto L_0x0038
            if (r2 != r6) goto L_0x002d
            org.telegram.messenger.ImageReceiver r1 = r0.sideImage
            org.telegram.messenger.ImageReceiver r4 = r0.rightImage
            if (r1 == r4) goto L_0x0038
        L_0x002d:
            if (r2 != r5) goto L_0x0036
            org.telegram.messenger.ImageReceiver r1 = r0.sideImage
            org.telegram.messenger.ImageReceiver r4 = r0.leftImage
            if (r1 != r4) goto L_0x0036
            goto L_0x0038
        L_0x0036:
            r1 = 0
            goto L_0x0039
        L_0x0038:
            r1 = 1
        L_0x0039:
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r4 = r0.photoProgressViews
            r4 = r4[r2]
            r4.setProgress(r3, r1)
            r0.checkProgress(r2, r7, r6)
            goto L_0x0689
        L_0x0045:
            int r2 = r2 + 1
            goto L_0x0013
        L_0x0048:
            int r2 = org.telegram.messenger.NotificationCenter.fileDidLoad
            if (r1 != r2) goto L_0x00ba
            r1 = r24[r7]
            java.lang.String r1 = (java.lang.String) r1
            r2 = 0
        L_0x0051:
            if (r2 >= r4) goto L_0x0689
            java.lang.String[] r8 = r0.currentFileNames
            r9 = r8[r2]
            if (r9 == 0) goto L_0x00b7
            r8 = r8[r2]
            boolean r8 = r8.equals(r1)
            if (r8 == 0) goto L_0x00b7
            if (r2 == 0) goto L_0x0076
            if (r2 != r6) goto L_0x006b
            org.telegram.messenger.ImageReceiver r1 = r0.sideImage
            org.telegram.messenger.ImageReceiver r4 = r0.rightImage
            if (r1 == r4) goto L_0x0076
        L_0x006b:
            if (r2 != r5) goto L_0x0074
            org.telegram.messenger.ImageReceiver r1 = r0.sideImage
            org.telegram.messenger.ImageReceiver r4 = r0.leftImage
            if (r1 != r4) goto L_0x0074
            goto L_0x0076
        L_0x0074:
            r1 = 0
            goto L_0x0077
        L_0x0076:
            r1 = 1
        L_0x0077:
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r4 = r0.photoProgressViews
            r4 = r4[r2]
            r4.setProgress(r3, r1)
            r0.checkProgress(r2, r7, r1)
            org.telegram.ui.Components.VideoPlayer r1 = r0.videoPlayer
            if (r1 != 0) goto L_0x00ad
            if (r2 != 0) goto L_0x00ad
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            if (r1 == 0) goto L_0x0091
            boolean r1 = r1.isVideo()
            if (r1 != 0) goto L_0x00aa
        L_0x0091:
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = r0.currentBotInlineResult
            if (r1 == 0) goto L_0x00ad
            java.lang.String r1 = r1.type
            java.lang.String r3 = "video"
            boolean r1 = r1.equals(r3)
            if (r1 != 0) goto L_0x00aa
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = r0.currentBotInlineResult
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isVideoDocument(r1)
            if (r1 == 0) goto L_0x00ad
        L_0x00aa:
            r0.onActionClick(r7)
        L_0x00ad:
            if (r2 != 0) goto L_0x0689
            org.telegram.ui.Components.VideoPlayer r1 = r0.videoPlayer
            if (r1 == 0) goto L_0x0689
            r0.currentVideoFinishedLoading = r6
            goto L_0x0689
        L_0x00b7:
            int r2 = r2 + 1
            goto L_0x0051
        L_0x00ba:
            int r2 = org.telegram.messenger.NotificationCenter.FileLoadProgressChanged
            r8 = 0
            if (r1 != r2) goto L_0x0191
            r1 = r24[r7]
            java.lang.String r1 = (java.lang.String) r1
            r2 = 0
        L_0x00c5:
            if (r2 >= r4) goto L_0x0689
            java.lang.String[] r10 = r0.currentFileNames
            r11 = r10[r2]
            if (r11 == 0) goto L_0x0188
            r10 = r10[r2]
            boolean r10 = r10.equals(r1)
            if (r10 == 0) goto L_0x0188
            r10 = r24[r6]
            java.lang.Long r10 = (java.lang.Long) r10
            r11 = r24[r5]
            java.lang.Long r11 = (java.lang.Long) r11
            long r12 = r10.longValue()
            float r10 = (float) r12
            long r11 = r11.longValue()
            float r11 = (float) r11
            float r10 = r10 / r11
            float r10 = java.lang.Math.min(r3, r10)
            if (r2 == 0) goto L_0x0101
            if (r2 != r6) goto L_0x00f6
            org.telegram.messenger.ImageReceiver r11 = r0.sideImage
            org.telegram.messenger.ImageReceiver r12 = r0.rightImage
            if (r11 == r12) goto L_0x0101
        L_0x00f6:
            if (r2 != r5) goto L_0x00ff
            org.telegram.messenger.ImageReceiver r11 = r0.sideImage
            org.telegram.messenger.ImageReceiver r12 = r0.leftImage
            if (r11 != r12) goto L_0x00ff
            goto L_0x0101
        L_0x00ff:
            r11 = 0
            goto L_0x0102
        L_0x0101:
            r11 = 1
        L_0x0102:
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r12 = r0.photoProgressViews
            r12 = r12[r2]
            r12.setProgress(r10, r11)
            if (r2 != 0) goto L_0x0188
            org.telegram.ui.Components.VideoPlayer r11 = r0.videoPlayer
            if (r11 == 0) goto L_0x0188
            org.telegram.ui.Components.SeekBar r11 = r0.videoPlayerSeekbar
            if (r11 == 0) goto L_0x0188
            boolean r11 = r0.currentVideoFinishedLoading
            if (r11 == 0) goto L_0x011a
        L_0x0117:
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x0170
        L_0x011a:
            long r13 = android.os.SystemClock.elapsedRealtime()
            long r5 = r0.lastBufferedPositionCheck
            long r5 = r13 - r5
            long r5 = java.lang.Math.abs(r5)
            r15 = 500(0x1f4, double:2.47E-321)
            int r17 = (r5 > r15 ? 1 : (r5 == r15 ? 0 : -1))
            if (r17 < 0) goto L_0x016d
            float r5 = r0.seekToProgressPending
            r6 = 0
            int r15 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r15 != 0) goto L_0x0155
            org.telegram.ui.Components.VideoPlayer r5 = r0.videoPlayer
            long r3 = r5.getDuration()
            org.telegram.ui.Components.VideoPlayer r5 = r0.videoPlayer
            long r11 = r5.getCurrentPosition()
            int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r5 < 0) goto L_0x0154
            r18 = -9223372036854775807(0xNUM, double:-4.9E-324)
            int r5 = (r3 > r18 ? 1 : (r3 == r18 ? 0 : -1))
            if (r5 == 0) goto L_0x0154
            int r5 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r5 < 0) goto L_0x0154
            float r5 = (float) r11
            float r3 = (float) r3
            float r5 = r5 / r3
            goto L_0x0155
        L_0x0154:
            r5 = 0
        L_0x0155:
            boolean r3 = r0.isStreaming
            if (r3 == 0) goto L_0x0168
            int r3 = r0.currentAccount
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r3)
            java.lang.String[] r4 = r0.currentFileNames
            r4 = r4[r7]
            float r3 = r3.getBufferedProgressFromPosition(r5, r4)
            goto L_0x016a
        L_0x0168:
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x016a:
            r0.lastBufferedPositionCheck = r13
            goto L_0x0117
        L_0x016d:
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x0117
        L_0x0170:
            int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r4 == 0) goto L_0x0185
            org.telegram.ui.Components.SeekBar r4 = r0.videoPlayerSeekbar
            r4.setBufferedProgress(r3)
            org.telegram.ui.Components.PipVideoView r4 = r0.pipVideoView
            if (r4 == 0) goto L_0x0180
            r4.setBufferedProgress(r3)
        L_0x0180:
            android.widget.FrameLayout r3 = r0.videoPlayerControlFrameLayout
            r3.invalidate()
        L_0x0185:
            r0.checkBufferedProgress(r10)
        L_0x0188:
            int r2 = r2 + 1
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = 3
            r5 = 2
            r6 = 1
            goto L_0x00c5
        L_0x0191:
            int r2 = org.telegram.messenger.NotificationCenter.dialogPhotosLoaded
            r3 = 4
            r4 = -1
            if (r1 != r2) goto L_0x02e3
            r2 = 3
            r1 = r24[r2]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r2 = r24[r7]
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            int r5 = r0.avatarsDialogId
            if (r5 != r2) goto L_0x0689
            int r2 = r0.classGuid
            if (r2 != r1) goto L_0x0689
            r1 = 2
            r1 = r24[r1]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r2 = r24[r3]
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            boolean r3 = r2.isEmpty()
            if (r3 == 0) goto L_0x01c4
            return
        L_0x01c4:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.imagesArrLocations
            r3.clear()
            java.util.ArrayList<java.lang.Integer> r3 = r0.imagesArrLocationsSizes
            r3.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r3 = r0.avatarsArr
            r3.clear()
            r3 = 0
            r5 = -1
        L_0x01d5:
            int r6 = r2.size()
            if (r3 >= r6) goto L_0x0253
            java.lang.Object r6 = r2.get(r3)
            org.telegram.tgnet.TLRPC$Photo r6 = (org.telegram.tgnet.TLRPC.Photo) r6
            if (r6 == 0) goto L_0x0250
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty
            if (r8 != 0) goto L_0x0250
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r6.sizes
            if (r8 != 0) goto L_0x01ec
            goto L_0x0250
        L_0x01ec:
            r9 = 640(0x280, float:8.97E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r9)
            if (r8 == 0) goto L_0x0250
            if (r5 != r4) goto L_0x0229
            org.telegram.messenger.ImageLocation r9 = r0.currentFileLocation
            if (r9 == 0) goto L_0x0229
            r9 = 0
        L_0x01fb:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r6.sizes
            int r10 = r10.size()
            if (r9 >= r10) goto L_0x0229
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r6.sizes
            java.lang.Object r10 = r10.get(r9)
            org.telegram.tgnet.TLRPC$PhotoSize r10 = (org.telegram.tgnet.TLRPC.PhotoSize) r10
            org.telegram.tgnet.TLRPC$FileLocation r10 = r10.location
            int r11 = r10.local_id
            org.telegram.messenger.ImageLocation r12 = r0.currentFileLocation
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r12 = r12.location
            int r13 = r12.local_id
            if (r11 != r13) goto L_0x0226
            long r10 = r10.volume_id
            long r12 = r12.volume_id
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 != 0) goto L_0x0226
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r5 = r0.imagesArrLocations
            int r5 = r5.size()
            goto L_0x0229
        L_0x0226:
            int r9 = r9 + 1
            goto L_0x01fb
        L_0x0229:
            int r9 = r6.dc_id
            if (r9 == 0) goto L_0x0235
            org.telegram.tgnet.TLRPC$FileLocation r10 = r8.location
            r10.dc_id = r9
            byte[] r9 = r6.file_reference
            r10.file_reference = r9
        L_0x0235:
            org.telegram.messenger.ImageLocation r9 = org.telegram.messenger.ImageLocation.getForPhoto(r8, r6)
            if (r9 == 0) goto L_0x0250
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r10 = r0.imagesArrLocations
            r10.add(r9)
            java.util.ArrayList<java.lang.Integer> r9 = r0.imagesArrLocationsSizes
            int r8 = r8.size
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r9.add(r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r8 = r0.avatarsArr
            r8.add(r6)
        L_0x0250:
            int r3 = r3 + 1
            goto L_0x01d5
        L_0x0253:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r2 = r0.avatarsArr
            boolean r2 = r2.isEmpty()
            r3 = 6
            if (r2 != 0) goto L_0x0262
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.menuItem
            r2.showSubItem(r3)
            goto L_0x0267
        L_0x0262:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.menuItem
            r2.hideSubItem(r3)
        L_0x0267:
            r0.needSearchImageInArr = r7
            r0.currentIndex = r4
            if (r5 == r4) goto L_0x0272
            r2 = 1
            r0.setImageIndex(r5, r2)
            goto L_0x02cd
        L_0x0272:
            int r2 = r0.avatarsDialogId
            r3 = 0
            if (r2 <= 0) goto L_0x0288
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r4 = r0.avatarsDialogId
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r4)
            goto L_0x029e
        L_0x0288:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r4 = r0.avatarsDialogId
            int r4 = -r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r4)
            r20 = r3
            r3 = r2
            r2 = r20
        L_0x029e:
            if (r2 != 0) goto L_0x02a2
            if (r3 == 0) goto L_0x02cd
        L_0x02a2:
            if (r2 == 0) goto L_0x02aa
            r4 = 1
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForUser(r2, r4)
            goto L_0x02af
        L_0x02aa:
            r4 = 1
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForChat(r3, r4)
        L_0x02af:
            if (r2 == 0) goto L_0x02cd
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.imagesArrLocations
            r3.add(r7, r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r2 = r0.avatarsArr
            org.telegram.tgnet.TLRPC$TL_photoEmpty r3 = new org.telegram.tgnet.TLRPC$TL_photoEmpty
            r3.<init>()
            r2.add(r7, r3)
            java.util.ArrayList<java.lang.Integer> r2 = r0.imagesArrLocationsSizes
            java.lang.Integer r3 = java.lang.Integer.valueOf(r7)
            r2.add(r7, r3)
            r2 = 1
            r0.setImageIndex(r7, r2)
        L_0x02cd:
            if (r1 == 0) goto L_0x0689
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r3 = r0.avatarsDialogId
            r4 = 80
            r5 = 0
            r7 = 0
            int r8 = r0.classGuid
            r2.loadDialogPhotos(r3, r4, r5, r7, r8)
            goto L_0x0689
        L_0x02e3:
            int r2 = org.telegram.messenger.NotificationCenter.mediaCountDidLoad
            if (r1 != r2) goto L_0x03a3
            r1 = r24[r7]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            long r3 = r0.currentDialogId
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x02fb
            long r3 = r0.mergeDialogId
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x0689
        L_0x02fb:
            long r3 = r0.currentDialogId
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x030d
            r3 = 1
            r1 = r24[r3]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r0.totalImagesCount = r1
            goto L_0x031e
        L_0x030d:
            r3 = 1
            long r4 = r0.mergeDialogId
            int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x031e
            r1 = r24[r3]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r0.totalImagesCountMerge = r1
        L_0x031e:
            boolean r1 = r0.needSearchImageInArr
            if (r1 == 0) goto L_0x033f
            boolean r1 = r0.isFirstLoading
            if (r1 == 0) goto L_0x033f
            r0.isFirstLoading = r7
            r0.loadingMoreImages = r3
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r1)
            long r3 = r0.currentDialogId
            r5 = 80
            r6 = 0
            int r7 = r0.sharedMediaType
            r8 = 1
            int r9 = r0.classGuid
            r2.loadMedia(r3, r5, r6, r7, r8, r9)
            goto L_0x0689
        L_0x033f:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0689
            boolean r1 = r0.opennedFromMedia
            r2 = 2131625890(0x7f0e07a2, float:1.8879E38)
            java.lang.String r3 = "Of"
            if (r1 == 0) goto L_0x0373
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            int r5 = r0.currentIndex
            r6 = 1
            int r5 = r5 + r6
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r7] = r5
            int r5 = r0.totalImagesCount
            int r7 = r0.totalImagesCountMerge
            int r5 = r5 + r7
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r6] = r5
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r4)
            r1.setTitle(r2)
            goto L_0x0689
        L_0x0373:
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            int r5 = r0.totalImagesCount
            int r6 = r0.totalImagesCountMerge
            int r5 = r5 + r6
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r0.imagesArr
            int r6 = r6.size()
            int r5 = r5 - r6
            int r6 = r0.currentIndex
            int r5 = r5 + r6
            r6 = 1
            int r5 = r5 + r6
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r7] = r5
            int r5 = r0.totalImagesCount
            int r7 = r0.totalImagesCountMerge
            int r5 = r5 + r7
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r6] = r5
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r3, r2, r4)
            r1.setTitle(r2)
            goto L_0x0689
        L_0x03a3:
            int r2 = org.telegram.messenger.NotificationCenter.mediaDidLoad
            if (r1 != r2) goto L_0x05f5
            r1 = r24[r7]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r3 = 3
            r3 = r24[r3]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            long r5 = r0.currentDialogId
            int r10 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r10 == 0) goto L_0x03c4
            long r5 = r0.mergeDialogId
            int r10 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r10 != 0) goto L_0x0689
        L_0x03c4:
            int r5 = r0.classGuid
            if (r3 != r5) goto L_0x0689
            r0.loadingMoreImages = r7
            long r5 = r0.currentDialogId
            int r3 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            r1 = 2
            if (r3 != 0) goto L_0x03d3
            r6 = 0
            goto L_0x03d4
        L_0x03d3:
            r6 = 1
        L_0x03d4:
            r2 = r24[r1]
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            boolean[] r1 = r0.endReached
            r3 = 5
            r3 = r24[r3]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r3 = r3.booleanValue()
            r1[r6] = r3
            boolean r1 = r0.needSearchImageInArr
            if (r1 == 0) goto L_0x058e
            boolean r1 = r2.isEmpty()
            if (r1 == 0) goto L_0x03fa
            if (r6 != 0) goto L_0x03f7
            long r12 = r0.mergeDialogId
            int r1 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r1 != 0) goto L_0x03fa
        L_0x03f7:
            r0.needSearchImageInArr = r7
            return
        L_0x03fa:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r3 = r0.currentIndex
            java.lang.Object r1 = r1.get(r3)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            r3 = 0
            r5 = 0
            r10 = -1
        L_0x0407:
            int r12 = r2.size()
            if (r3 >= r12) goto L_0x045c
            java.lang.Object r12 = r2.get(r3)
            org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r13 = r0.imagesByIdsTemp
            r13 = r13[r6]
            int r14 = r12.getId()
            int r13 = r13.indexOfKey(r14)
            if (r13 >= 0) goto L_0x0459
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r13 = r0.imagesByIdsTemp
            r13 = r13[r6]
            int r14 = r12.getId()
            r13.put(r14, r12)
            boolean r13 = r0.opennedFromMedia
            if (r13 == 0) goto L_0x0443
            java.util.ArrayList<org.telegram.messenger.MessageObject> r13 = r0.imagesArrTemp
            r13.add(r12)
            int r12 = r12.getId()
            int r13 = r1.getId()
            if (r12 != r13) goto L_0x0440
            r10 = r5
        L_0x0440:
            int r5 = r5 + 1
            goto L_0x0459
        L_0x0443:
            int r5 = r5 + 1
            java.util.ArrayList<org.telegram.messenger.MessageObject> r13 = r0.imagesArrTemp
            r13.add(r7, r12)
            int r12 = r12.getId()
            int r13 = r1.getId()
            if (r12 != r13) goto L_0x0459
            int r10 = r2.size()
            int r10 = r10 - r5
        L_0x0459:
            int r3 = r3 + 1
            goto L_0x0407
        L_0x045c:
            if (r5 != 0) goto L_0x0470
            if (r6 != 0) goto L_0x0466
            long r1 = r0.mergeDialogId
            int r3 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r3 != 0) goto L_0x0470
        L_0x0466:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r1 = r1.size()
            r0.totalImagesCount = r1
            r0.totalImagesCountMerge = r7
        L_0x0470:
            if (r10 == r4) goto L_0x04b9
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            r1.clear()
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArrTemp
            r1.addAll(r2)
            r1 = 0
            r2 = 2
        L_0x0480:
            if (r1 >= r2) goto L_0x0498
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r3 = r0.imagesByIds
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r5 = r0.imagesByIdsTemp
            r5 = r5[r1]
            android.util.SparseArray r5 = r5.clone()
            r3[r1] = r5
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r3 = r0.imagesByIdsTemp
            r3 = r3[r1]
            r3.clear()
            int r1 = r1 + 1
            goto L_0x0480
        L_0x0498:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArrTemp
            r1.clear()
            r0.needSearchImageInArr = r7
            r0.currentIndex = r4
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r1 = r1.size()
            if (r10 < r1) goto L_0x04b3
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r1 = r1.size()
            r2 = 1
            int r10 = r1 + -1
            goto L_0x04b4
        L_0x04b3:
            r2 = 1
        L_0x04b4:
            r0.setImageIndex(r10, r2)
            goto L_0x0689
        L_0x04b9:
            r2 = 1
            boolean r1 = r0.opennedFromMedia
            if (r1 == 0) goto L_0x0508
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArrTemp
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x04c8
            r1 = 0
            goto L_0x04d9
        L_0x04c8:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArrTemp
            int r3 = r1.size()
            int r3 = r3 - r2
            java.lang.Object r1 = r1.get(r3)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            int r1 = r1.getId()
        L_0x04d9:
            if (r6 != 0) goto L_0x054c
            boolean[] r2 = r0.endReached
            boolean r2 = r2[r6]
            if (r2 == 0) goto L_0x054c
            long r2 = r0.mergeDialogId
            int r4 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r4 == 0) goto L_0x054c
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArrTemp
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0549
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArrTemp
            int r3 = r2.size()
            r4 = 1
            int r3 = r3 - r4
            java.lang.Object r2 = r2.get(r3)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            long r2 = r2.getDialogId()
            long r4 = r0.mergeDialogId
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x0549
            goto L_0x0546
        L_0x0508:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArrTemp
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0512
            r1 = 0
            goto L_0x051e
        L_0x0512:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArrTemp
            java.lang.Object r1 = r1.get(r7)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            int r1 = r1.getId()
        L_0x051e:
            if (r6 != 0) goto L_0x054c
            boolean[] r2 = r0.endReached
            boolean r2 = r2[r6]
            if (r2 == 0) goto L_0x054c
            long r2 = r0.mergeDialogId
            int r4 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r4 == 0) goto L_0x054c
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArrTemp
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0549
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArrTemp
            java.lang.Object r2 = r2.get(r7)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            long r2 = r2.getDialogId()
            long r4 = r0.mergeDialogId
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x0549
        L_0x0546:
            r6 = 1
            r11 = 0
            goto L_0x054d
        L_0x0549:
            r11 = r1
            r6 = 1
            goto L_0x054d
        L_0x054c:
            r11 = r1
        L_0x054d:
            boolean[] r1 = r0.endReached
            boolean r1 = r1[r6]
            if (r1 != 0) goto L_0x0689
            r1 = 1
            r0.loadingMoreImages = r1
            boolean r1 = r0.opennedFromMedia
            if (r1 == 0) goto L_0x0574
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r7 = org.telegram.messenger.MediaDataController.getInstance(r1)
            if (r6 != 0) goto L_0x0565
            long r1 = r0.currentDialogId
            goto L_0x0567
        L_0x0565:
            long r1 = r0.mergeDialogId
        L_0x0567:
            r8 = r1
            r10 = 80
            int r12 = r0.sharedMediaType
            r13 = 1
            int r14 = r0.classGuid
            r7.loadMedia(r8, r10, r11, r12, r13, r14)
            goto L_0x0689
        L_0x0574:
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r7 = org.telegram.messenger.MediaDataController.getInstance(r1)
            if (r6 != 0) goto L_0x057f
            long r1 = r0.currentDialogId
            goto L_0x0581
        L_0x057f:
            long r1 = r0.mergeDialogId
        L_0x0581:
            r8 = r1
            r10 = 80
            int r12 = r0.sharedMediaType
            r13 = 1
            int r14 = r0.classGuid
            r7.loadMedia(r8, r10, r11, r12, r13, r14)
            goto L_0x0689
        L_0x058e:
            java.util.Iterator r1 = r2.iterator()
            r2 = 0
        L_0x0593:
            boolean r3 = r1.hasNext()
            if (r3 == 0) goto L_0x05ca
            java.lang.Object r3 = r1.next()
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r5 = r0.imagesByIds
            r5 = r5[r6]
            int r8 = r3.getId()
            int r5 = r5.indexOfKey(r8)
            if (r5 >= 0) goto L_0x0593
            int r2 = r2 + 1
            boolean r5 = r0.opennedFromMedia
            if (r5 == 0) goto L_0x05b9
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            r5.add(r3)
            goto L_0x05be
        L_0x05b9:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            r5.add(r7, r3)
        L_0x05be:
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r5 = r0.imagesByIds
            r5 = r5[r6]
            int r8 = r3.getId()
            r5.put(r8, r3)
            goto L_0x0593
        L_0x05ca:
            boolean r1 = r0.opennedFromMedia
            if (r1 == 0) goto L_0x05dc
            if (r2 != 0) goto L_0x0689
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r1 = r1.size()
            r0.totalImagesCount = r1
            r0.totalImagesCountMerge = r7
            goto L_0x0689
        L_0x05dc:
            if (r2 == 0) goto L_0x05e9
            int r1 = r0.currentIndex
            r0.currentIndex = r4
            int r1 = r1 + r2
            r2 = 1
            r0.setImageIndex(r1, r2)
            goto L_0x0689
        L_0x05e9:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r1 = r1.size()
            r0.totalImagesCount = r1
            r0.totalImagesCountMerge = r7
            goto L_0x0689
        L_0x05f5:
            int r2 = org.telegram.messenger.NotificationCenter.emojiDidLoad
            if (r1 != r2) goto L_0x0602
            android.widget.TextView r1 = r0.captionTextView
            if (r1 == 0) goto L_0x0689
            r1.invalidate()
            goto L_0x0689
        L_0x0602:
            int r2 = org.telegram.messenger.NotificationCenter.filePreparingFailed
            if (r1 != r2) goto L_0x063d
            r1 = r24[r7]
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            boolean r2 = r0.loadInitialVideo
            if (r2 == 0) goto L_0x061b
            r0.loadInitialVideo = r7
            org.telegram.ui.Components.RadialProgressView r1 = r0.progressView
            r1.setVisibility(r3)
            android.net.Uri r1 = r0.currentPlayingVideoFile
            r0.preparePlayer(r1, r7, r7)
            goto L_0x0689
        L_0x061b:
            boolean r2 = r0.tryStartRequestPreviewOnFinish
            if (r2 == 0) goto L_0x0631
            r0.releasePlayer(r7)
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r2 = r0.videoPreviewMessageObject
            r3 = 1
            boolean r1 = r1.scheduleVideoConvert(r2, r3)
            r1 = r1 ^ r3
            r0.tryStartRequestPreviewOnFinish = r1
            goto L_0x0689
        L_0x0631:
            org.telegram.messenger.MessageObject r2 = r0.videoPreviewMessageObject
            if (r1 != r2) goto L_0x0689
            r0.requestingPreview = r7
            org.telegram.ui.Components.RadialProgressView r1 = r0.progressView
            r1.setVisibility(r3)
            goto L_0x0689
        L_0x063d:
            int r2 = org.telegram.messenger.NotificationCenter.fileNewChunkAvailable
            if (r1 != r2) goto L_0x0689
            r1 = r24[r7]
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            org.telegram.messenger.MessageObject r2 = r0.videoPreviewMessageObject
            if (r1 != r2) goto L_0x0689
            r1 = 1
            r2 = r24[r1]
            java.lang.String r2 = (java.lang.String) r2
            r4 = 3
            r5 = r24[r4]
            java.lang.Long r5 = (java.lang.Long) r5
            long r4 = r5.longValue()
            r3 = r24[r3]
            java.lang.Float r3 = (java.lang.Float) r3
            float r3 = r3.floatValue()
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r6 = r0.photoProgressViews
            r6 = r6[r7]
            r6.setProgress(r3, r1)
            int r3 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r3 == 0) goto L_0x0689
            r0.requestingPreview = r7
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r3 = r0.photoProgressViews
            r3 = r3[r7]
            r4 = 1065353216(0x3var_, float:1.0)
            r3.setProgress(r4, r1)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r3 = r0.photoProgressViews
            r3 = r3[r7]
            r4 = 3
            r3.setBackgroundState(r4, r1)
            java.io.File r3 = new java.io.File
            r3.<init>(r2)
            android.net.Uri r2 = android.net.Uri.fromFile(r3)
            r0.preparePlayer(r2, r7, r1)
        L_0x0689:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* access modifiers changed from: private */
    public void showDownloadAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        MessageObject messageObject = this.currentMessageObject;
        boolean z = false;
        if (messageObject != null && messageObject.isVideo() && FileLoader.getInstance(this.currentMessageObject.currentAccount).isLoadingFile(this.currentFileNames[0])) {
            z = true;
        }
        if (z) {
            builder.setMessage(LocaleController.getString("PleaseStreamDownload", NUM));
        } else {
            builder.setMessage(LocaleController.getString("PleaseDownload", NUM));
        }
        showAlertDialog(builder);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(4:37|38|39|40) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:39:0x0099 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSharePressed() {
        /*
            r6 = this;
            android.app.Activity r0 = r6.parentActivity
            if (r0 == 0) goto L_0x00c5
            boolean r0 = r6.allowShare
            if (r0 != 0) goto L_0x000a
            goto L_0x00c5
        L_0x000a:
            org.telegram.messenger.MessageObject r0 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c1 }
            r1 = 1
            r2 = 0
            r3 = 0
            if (r0 == 0) goto L_0x0041
            org.telegram.messenger.MessageObject r0 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c1 }
            boolean r2 = r0.isVideo()     // Catch:{ Exception -> 0x00c1 }
            org.telegram.messenger.MessageObject r0 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c1 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x00c1 }
            java.lang.String r0 = r0.attachPath     // Catch:{ Exception -> 0x00c1 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x00c1 }
            if (r0 != 0) goto L_0x0036
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x00c1 }
            org.telegram.messenger.MessageObject r4 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c1 }
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner     // Catch:{ Exception -> 0x00c1 }
            java.lang.String r4 = r4.attachPath     // Catch:{ Exception -> 0x00c1 }
            r0.<init>(r4)     // Catch:{ Exception -> 0x00c1 }
            boolean r4 = r0.exists()     // Catch:{ Exception -> 0x00c1 }
            if (r4 != 0) goto L_0x0035
            goto L_0x0036
        L_0x0035:
            r3 = r0
        L_0x0036:
            if (r3 != 0) goto L_0x0059
            org.telegram.messenger.MessageObject r0 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c1 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x00c1 }
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToMessage(r0)     // Catch:{ Exception -> 0x00c1 }
            goto L_0x0059
        L_0x0041:
            org.telegram.messenger.ImageLocation r0 = r6.currentFileLocation     // Catch:{ Exception -> 0x00c1 }
            if (r0 == 0) goto L_0x0059
            org.telegram.messenger.ImageLocation r0 = r6.currentFileLocation     // Catch:{ Exception -> 0x00c1 }
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r0 = r0.location     // Catch:{ Exception -> 0x00c1 }
            int r3 = r6.avatarsDialogId     // Catch:{ Exception -> 0x00c1 }
            if (r3 != 0) goto L_0x0054
            boolean r3 = r6.isEvent     // Catch:{ Exception -> 0x00c1 }
            if (r3 == 0) goto L_0x0052
            goto L_0x0054
        L_0x0052:
            r3 = 0
            goto L_0x0055
        L_0x0054:
            r3 = 1
        L_0x0055:
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r3)     // Catch:{ Exception -> 0x00c1 }
        L_0x0059:
            boolean r0 = r3.exists()     // Catch:{ Exception -> 0x00c1 }
            if (r0 == 0) goto L_0x00bd
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x00c1 }
            java.lang.String r4 = "android.intent.action.SEND"
            r0.<init>(r4)     // Catch:{ Exception -> 0x00c1 }
            if (r2 == 0) goto L_0x006f
            java.lang.String r2 = "video/mp4"
            r0.setType(r2)     // Catch:{ Exception -> 0x00c1 }
            goto L_0x0082
        L_0x006f:
            org.telegram.messenger.MessageObject r2 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c1 }
            if (r2 == 0) goto L_0x007d
            org.telegram.messenger.MessageObject r2 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c1 }
            java.lang.String r2 = r2.getMimeType()     // Catch:{ Exception -> 0x00c1 }
            r0.setType(r2)     // Catch:{ Exception -> 0x00c1 }
            goto L_0x0082
        L_0x007d:
            java.lang.String r2 = "image/jpeg"
            r0.setType(r2)     // Catch:{ Exception -> 0x00c1 }
        L_0x0082:
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00c1 }
            r4 = 24
            java.lang.String r5 = "android.intent.extra.STREAM"
            if (r2 < r4) goto L_0x00a1
            android.app.Activity r2 = r6.parentActivity     // Catch:{ Exception -> 0x0099 }
            java.lang.String r4 = "org.telegram.messenger.beta.provider"
            android.net.Uri r2 = androidx.core.content.FileProvider.getUriForFile(r2, r4, r3)     // Catch:{ Exception -> 0x0099 }
            r0.putExtra(r5, r2)     // Catch:{ Exception -> 0x0099 }
            r0.setFlags(r1)     // Catch:{ Exception -> 0x0099 }
            goto L_0x00a8
        L_0x0099:
            android.net.Uri r1 = android.net.Uri.fromFile(r3)     // Catch:{ Exception -> 0x00c1 }
            r0.putExtra(r5, r1)     // Catch:{ Exception -> 0x00c1 }
            goto L_0x00a8
        L_0x00a1:
            android.net.Uri r1 = android.net.Uri.fromFile(r3)     // Catch:{ Exception -> 0x00c1 }
            r0.putExtra(r5, r1)     // Catch:{ Exception -> 0x00c1 }
        L_0x00a8:
            android.app.Activity r1 = r6.parentActivity     // Catch:{ Exception -> 0x00c1 }
            java.lang.String r2 = "ShareFile"
            r3 = 2131626629(0x7f0e0a85, float:1.88805E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ Exception -> 0x00c1 }
            android.content.Intent r0 = android.content.Intent.createChooser(r0, r2)     // Catch:{ Exception -> 0x00c1 }
            r2 = 500(0x1f4, float:7.0E-43)
            r1.startActivityForResult(r0, r2)     // Catch:{ Exception -> 0x00c1 }
            goto L_0x00c5
        L_0x00bd:
            r6.showDownloadAlert()     // Catch:{ Exception -> 0x00c1 }
            goto L_0x00c5
        L_0x00c1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00c5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onSharePressed():void");
    }

    /* access modifiers changed from: private */
    public void setScaleToFill() {
        float bitmapWidth = (float) this.centerImage.getBitmapWidth();
        float bitmapHeight = (float) this.centerImage.getBitmapHeight();
        if (bitmapWidth != 0.0f && bitmapHeight != 0.0f) {
            float containerViewWidth = (float) getContainerViewWidth();
            float containerViewHeight = (float) getContainerViewHeight();
            float min = Math.min(containerViewHeight / bitmapHeight, containerViewWidth / bitmapWidth);
            this.scale = Math.max(containerViewWidth / ((float) ((int) (bitmapWidth * min))), containerViewHeight / ((float) ((int) (bitmapHeight * min))));
            updateMinMax(this.scale);
        }
    }

    public void setParentAlert(ChatAttachAlert chatAttachAlert) {
        this.parentAlert = chatAttachAlert;
    }

    public void setParentActivity(Activity activity) {
        int i;
        Activity activity2 = activity;
        this.currentAccount = UserConfig.selectedAccount;
        this.centerImage.setCurrentAccount(this.currentAccount);
        this.leftImage.setCurrentAccount(this.currentAccount);
        this.rightImage.setCurrentAccount(this.currentAccount);
        if (this.parentActivity != activity2 && activity2 != null) {
            this.parentActivity = activity2;
            this.actvityContext = new ContextThemeWrapper(this.parentActivity, NUM);
            if (progressDrawables == null) {
                progressDrawables = new Drawable[4];
                progressDrawables[0] = this.parentActivity.getResources().getDrawable(NUM);
                progressDrawables[1] = this.parentActivity.getResources().getDrawable(NUM);
                progressDrawables[2] = this.parentActivity.getResources().getDrawable(NUM);
                progressDrawables[3] = this.parentActivity.getResources().getDrawable(NUM);
            }
            this.scroller = new Scroller(activity2);
            this.windowView = new FrameLayout(activity2) {
                private Runnable attachRunnable;

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.isVisible && super.onInterceptTouchEvent(motionEvent);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.isVisible && PhotoViewer.this.onTouchEvent(motionEvent);
                }

                /* access modifiers changed from: protected */
                public boolean drawChild(Canvas canvas, View view, long j) {
                    try {
                        return super.drawChild(canvas, view, j);
                    } catch (Throwable unused) {
                        return false;
                    }
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int size = View.MeasureSpec.getSize(i);
                    int size2 = View.MeasureSpec.getSize(i2);
                    if (Build.VERSION.SDK_INT < 21 || PhotoViewer.this.lastInsets == null) {
                        int i3 = AndroidUtilities.displaySize.y;
                        if (size2 > i3) {
                            size2 = i3;
                        }
                    } else {
                        WindowInsets windowInsets = (WindowInsets) PhotoViewer.this.lastInsets;
                        if (AndroidUtilities.incorrectDisplaySizeFix) {
                            int i4 = AndroidUtilities.displaySize.y;
                            if (size2 > i4) {
                                size2 = i4;
                            }
                            size2 += AndroidUtilities.statusBarHeight;
                        }
                        size2 -= windowInsets.getSystemWindowInsetBottom();
                    }
                    setMeasuredDimension(size, size2);
                    ViewGroup.LayoutParams layoutParams = PhotoViewer.this.animatingImageView.getLayoutParams();
                    PhotoViewer.this.animatingImageView.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(layoutParams.height, Integer.MIN_VALUE));
                    PhotoViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    PhotoViewer.this.animatingImageView.layout(0, 0, PhotoViewer.this.animatingImageView.getMeasuredWidth() + 0, PhotoViewer.this.animatingImageView.getMeasuredHeight());
                    PhotoViewer.this.containerView.layout(0, 0, PhotoViewer.this.containerView.getMeasuredWidth() + 0, PhotoViewer.this.containerView.getMeasuredHeight());
                    boolean unused = PhotoViewer.this.wasLayout = true;
                    if (z) {
                        if (!PhotoViewer.this.dontResetZoomOnFirstLayout) {
                            float unused2 = PhotoViewer.this.scale = 1.0f;
                            float unused3 = PhotoViewer.this.translationX = 0.0f;
                            float unused4 = PhotoViewer.this.translationY = 0.0f;
                            PhotoViewer photoViewer = PhotoViewer.this;
                            photoViewer.updateMinMax(photoViewer.scale);
                        }
                        if (PhotoViewer.this.checkImageView != null) {
                            PhotoViewer.this.checkImageView.post(new Runnable() {
                                public final void run() {
                                    PhotoViewer.AnonymousClass5.this.lambda$onLayout$0$PhotoViewer$5();
                                }
                            });
                        }
                    }
                    if (PhotoViewer.this.dontResetZoomOnFirstLayout) {
                        PhotoViewer.this.setScaleToFill();
                        boolean unused5 = PhotoViewer.this.dontResetZoomOnFirstLayout = false;
                    }
                }

                public /* synthetic */ void lambda$onLayout$0$PhotoViewer$5() {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.checkImageView.getLayoutParams();
                    ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                    int i = 0;
                    layoutParams.topMargin = ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(40.0f)) / 2) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                    PhotoViewer.this.checkImageView.setLayoutParams(layoutParams);
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) PhotoViewer.this.photosCounterView.getLayoutParams();
                    int currentActionBarHeight = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(40.0f)) / 2;
                    if (Build.VERSION.SDK_INT >= 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    layoutParams2.topMargin = currentActionBarHeight + i;
                    PhotoViewer.this.photosCounterView.setLayoutParams(layoutParams2);
                }

                /* access modifiers changed from: protected */
                public void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    boolean unused = PhotoViewer.this.attachedToWindow = true;
                }

                /* access modifiers changed from: protected */
                public void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    boolean unused = PhotoViewer.this.attachedToWindow = false;
                    boolean unused2 = PhotoViewer.this.wasLayout = false;
                }

                public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
                    if (keyEvent == null || keyEvent.getKeyCode() != 4 || keyEvent.getAction() != 1) {
                        return super.dispatchKeyEventPreIme(keyEvent);
                    }
                    if (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible()) {
                        PhotoViewer.this.closeCaptionEnter(false);
                        return false;
                    }
                    PhotoViewer.getInstance().closePhoto(true, false);
                    return true;
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (Build.VERSION.SDK_INT >= 21 && PhotoViewer.this.isVisible && PhotoViewer.this.lastInsets != null) {
                        WindowInsets windowInsets = (WindowInsets) PhotoViewer.this.lastInsets;
                        if (PhotoViewer.this.animationInProgress == 1) {
                            PhotoViewer.this.blackPaint.setAlpha((int) (PhotoViewer.this.animatingImageView.getAnimationProgress() * 255.0f));
                        } else if (PhotoViewer.this.animationInProgress == 3) {
                            PhotoViewer.this.blackPaint.setAlpha((int) ((1.0f - PhotoViewer.this.animatingImageView.getAnimationProgress()) * 255.0f));
                        } else {
                            PhotoViewer.this.blackPaint.setAlpha(255);
                        }
                        canvas.drawRect(0.0f, (float) getMeasuredHeight(), (float) getMeasuredWidth(), (float) (getMeasuredHeight() + windowInsets.getSystemWindowInsetBottom()), PhotoViewer.this.blackPaint);
                    }
                }
            };
            this.windowView.setBackgroundDrawable(this.backgroundDrawable);
            this.windowView.setClipChildren(true);
            this.windowView.setFocusable(false);
            this.animatingImageView = new ClippingImageView(activity2);
            this.animatingImageView.setAnimationValues(this.animationValues);
            this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
            this.containerView = new FrameLayoutDrawer(activity2);
            this.containerView.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            if (Build.VERSION.SDK_INT >= 21) {
                this.containerView.setFitsSystemWindows(true);
                this.containerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                        return PhotoViewer.this.lambda$setParentActivity$1$PhotoViewer(view, windowInsets);
                    }
                });
                this.containerView.setSystemUiVisibility(1792);
            }
            this.windowLayoutParams = new WindowManager.LayoutParams();
            WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
            layoutParams.height = -1;
            layoutParams.format = -3;
            layoutParams.width = -1;
            layoutParams.gravity = 51;
            layoutParams.type = 99;
            if (Build.VERSION.SDK_INT >= 28) {
                layoutParams.layoutInDisplayCutoutMode = 1;
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.windowLayoutParams.flags = -NUM;
            } else {
                this.windowLayoutParams.flags = 131072;
            }
            this.actionBar = new ActionBar(activity2) {
                public void setAlpha(float f) {
                    super.setAlpha(f);
                    PhotoViewer.this.containerView.invalidate();
                }
            };
            this.actionBar.setTitleColor(-1);
            this.actionBar.setSubtitleColor(-1);
            this.actionBar.setBackgroundColor(NUM);
            this.actionBar.setOccupyStatusBar(Build.VERSION.SDK_INT >= 21);
            this.actionBar.setItemsBackgroundColor(NUM, false);
            this.actionBar.setBackButtonImage(NUM);
            this.actionBar.setTitle(LocaleController.formatString("Of", NUM, 1, 1));
            this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                public void onItemClick(int i) {
                    int dialogId;
                    TLRPC.Chat chat;
                    TLRPC.User user;
                    int i2;
                    File file;
                    int i3 = i;
                    int i4 = 1;
                    if (i3 == -1) {
                        if (!PhotoViewer.this.needCaptionLayout || (!PhotoViewer.this.captionEditText.isPopupShowing() && !PhotoViewer.this.captionEditText.isKeyboardVisible())) {
                            PhotoViewer.this.closePhoto(true, false);
                        } else {
                            PhotoViewer.this.closeCaptionEnter(false);
                        }
                    } else if (i3 == 1) {
                        if (Build.VERSION.SDK_INT < 23 || PhotoViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                            if (PhotoViewer.this.currentMessageObject != null) {
                                if (!(PhotoViewer.this.currentMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) || PhotoViewer.this.currentMessageObject.messageOwner.media.webpage == null || PhotoViewer.this.currentMessageObject.messageOwner.media.webpage.document != null) {
                                    file = FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner);
                                } else {
                                    PhotoViewer photoViewer = PhotoViewer.this;
                                    file = FileLoader.getPathToAttach(photoViewer.getFileLocation(photoViewer.currentIndex, (int[]) null), true);
                                }
                            } else if (PhotoViewer.this.currentFileLocation != null) {
                                file = FileLoader.getPathToAttach(PhotoViewer.this.currentFileLocation.location, PhotoViewer.this.avatarsDialogId != 0 || PhotoViewer.this.isEvent);
                            } else {
                                file = null;
                            }
                            if (file == null || !file.exists()) {
                                PhotoViewer.this.showDownloadAlert();
                                return;
                            }
                            String file2 = file.toString();
                            Activity access$2800 = PhotoViewer.this.parentActivity;
                            if (PhotoViewer.this.currentMessageObject == null || !PhotoViewer.this.currentMessageObject.isVideo()) {
                                i4 = 0;
                            }
                            MediaController.saveFile(file2, access$2800, i4, (String) null, (String) null);
                            return;
                        }
                        PhotoViewer.this.parentActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    } else if (i3 == 2) {
                        if (PhotoViewer.this.currentDialogId != 0) {
                            boolean unused = PhotoViewer.this.disableShowCheck = true;
                            Bundle bundle = new Bundle();
                            bundle.putLong("dialog_id", PhotoViewer.this.currentDialogId);
                            MediaActivity mediaActivity = new MediaActivity(bundle, new int[]{-1, -1, -1, -1, -1}, (SharedMediaLayout.SharedMediaData[]) null, PhotoViewer.this.sharedMediaType);
                            if (PhotoViewer.this.parentChatActivity != null) {
                                mediaActivity.setChatInfo(PhotoViewer.this.parentChatActivity.getCurrentChatInfo());
                            }
                            PhotoViewer.this.closePhoto(false, false);
                            ((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(mediaActivity, false, true);
                        }
                    } else if (i3 == 4) {
                        if (PhotoViewer.this.currentMessageObject != null) {
                            Bundle bundle2 = new Bundle();
                            int access$8600 = (int) PhotoViewer.this.currentDialogId;
                            int access$86002 = (int) (PhotoViewer.this.currentDialogId >> 32);
                            if (access$8600 == 0) {
                                bundle2.putInt("enc_id", access$86002);
                            } else if (access$8600 > 0) {
                                bundle2.putInt("user_id", access$8600);
                            } else if (access$8600 < 0) {
                                TLRPC.Chat chat2 = MessagesController.getInstance(PhotoViewer.this.currentAccount).getChat(Integer.valueOf(-access$8600));
                                if (!(chat2 == null || chat2.migrated_to == null)) {
                                    bundle2.putInt("migrated_to", access$8600);
                                    access$8600 = -chat2.migrated_to.channel_id;
                                }
                                bundle2.putInt("chat_id", -access$8600);
                            }
                            bundle2.putInt("message_id", PhotoViewer.this.currentMessageObject.getId());
                            NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            LaunchActivity launchActivity = (LaunchActivity) PhotoViewer.this.parentActivity;
                            launchActivity.presentFragment(new ChatActivity(bundle2), launchActivity.getMainFragmentsCount() > 1 || AndroidUtilities.isTablet(), true);
                            MessageObject unused2 = PhotoViewer.this.currentMessageObject = null;
                            PhotoViewer.this.closePhoto(false, false);
                        }
                    } else if (i3 == 3) {
                        if (PhotoViewer.this.currentMessageObject != null && PhotoViewer.this.parentActivity != null) {
                            ((LaunchActivity) PhotoViewer.this.parentActivity).switchToAccount(PhotoViewer.this.currentMessageObject.currentAccount, true);
                            Bundle bundle3 = new Bundle();
                            bundle3.putBoolean("onlySelect", true);
                            bundle3.putInt("dialogsType", 3);
                            DialogsActivity dialogsActivity = new DialogsActivity(bundle3);
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(PhotoViewer.this.currentMessageObject);
                            dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate(arrayList) {
                                private final /* synthetic */ ArrayList f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                                    PhotoViewer.AnonymousClass7.this.lambda$onItemClick$0$PhotoViewer$7(this.f$1, dialogsActivity, arrayList, charSequence, z);
                                }
                            });
                            ((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(dialogsActivity, false, true);
                            PhotoViewer.this.closePhoto(false, false);
                        }
                    } else if (i3 == 6) {
                        if (PhotoViewer.this.parentActivity != null && PhotoViewer.this.placeProvider != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) PhotoViewer.this.parentActivity);
                            String deleteMessageString = PhotoViewer.this.placeProvider.getDeleteMessageString();
                            if (deleteMessageString != null) {
                                builder.setTitle(LocaleController.getString("AreYouSureDeletePhotoTitle", NUM));
                                builder.setMessage(deleteMessageString);
                            } else if (PhotoViewer.this.currentMessageObject != null && PhotoViewer.this.currentMessageObject.isVideo()) {
                                builder.setTitle(LocaleController.getString("AreYouSureDeleteVideoTitle", NUM));
                                builder.setMessage(LocaleController.formatString("AreYouSureDeleteVideo", NUM, new Object[0]));
                            } else if (PhotoViewer.this.currentMessageObject == null || !PhotoViewer.this.currentMessageObject.isGif()) {
                                builder.setTitle(LocaleController.getString("AreYouSureDeletePhotoTitle", NUM));
                                builder.setMessage(LocaleController.formatString("AreYouSureDeletePhoto", NUM, new Object[0]));
                            } else {
                                builder.setTitle(LocaleController.getString("AreYouSureDeleteGIFTitle", NUM));
                                builder.setMessage(LocaleController.formatString("AreYouSureDeleteGIF", NUM, new Object[0]));
                            }
                            boolean[] zArr = new boolean[1];
                            if (!(PhotoViewer.this.currentMessageObject == null || PhotoViewer.this.currentMessageObject.scheduled || (dialogId = (int) PhotoViewer.this.currentMessageObject.getDialogId()) == 0)) {
                                if (dialogId > 0) {
                                    user = MessagesController.getInstance(PhotoViewer.this.currentAccount).getUser(Integer.valueOf(dialogId));
                                    chat = null;
                                } else {
                                    chat = MessagesController.getInstance(PhotoViewer.this.currentAccount).getChat(Integer.valueOf(-dialogId));
                                    user = null;
                                }
                                if (user != null || !ChatObject.isChannel(chat)) {
                                    int currentTime = ConnectionsManager.getInstance(PhotoViewer.this.currentAccount).getCurrentTime();
                                    if (user != null) {
                                        i2 = MessagesController.getInstance(PhotoViewer.this.currentAccount).revokeTimePmLimit;
                                    } else {
                                        i2 = MessagesController.getInstance(PhotoViewer.this.currentAccount).revokeTimeLimit;
                                    }
                                    if (!((user == null || user.id == UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId()) && chat == null) && ((PhotoViewer.this.currentMessageObject.messageOwner.action == null || (PhotoViewer.this.currentMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty)) && PhotoViewer.this.currentMessageObject.isOut() && currentTime - PhotoViewer.this.currentMessageObject.messageOwner.date <= i2)) {
                                        FrameLayout frameLayout = new FrameLayout(PhotoViewer.this.parentActivity);
                                        CheckBoxCell checkBoxCell = new CheckBoxCell(PhotoViewer.this.parentActivity, 1);
                                        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                        if (chat != null) {
                                            checkBoxCell.setText(LocaleController.getString("DeleteForAll", NUM), "", false, false);
                                        } else {
                                            checkBoxCell.setText(LocaleController.formatString("DeleteForUser", NUM, UserObject.getFirstName(user)), "", false, false);
                                        }
                                        checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                                        frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                                        checkBoxCell.setOnClickListener(new View.OnClickListener(zArr) {
                                            private final /* synthetic */ boolean[] f$0;

                                            {
                                                this.f$0 = r1;
                                            }

                                            public final void onClick(View view) {
                                                PhotoViewer.AnonymousClass7.lambda$onItemClick$1(this.f$0, view);
                                            }
                                        });
                                        builder.setView(frameLayout);
                                    }
                                }
                            }
                            builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener(zArr) {
                                private final /* synthetic */ boolean[] f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    PhotoViewer.AnonymousClass7.this.lambda$onItemClick$2$PhotoViewer$7(this.f$1, dialogInterface, i);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                            AlertDialog create = builder.create();
                            PhotoViewer.this.showAlertDialog(builder);
                            TextView textView = (TextView) create.getButton(-1);
                            if (textView != null) {
                                textView.setTextColor(Theme.getColor("dialogTextRed2"));
                            }
                        }
                    } else if (i3 == 10) {
                        PhotoViewer.this.onSharePressed();
                    } else if (i3 == 11) {
                        try {
                            AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                            PhotoViewer.this.closePhoto(false, false);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else if (i3 == 13) {
                        if (PhotoViewer.this.parentActivity != null && PhotoViewer.this.currentMessageObject != null && PhotoViewer.this.currentMessageObject.messageOwner.media != null && PhotoViewer.this.currentMessageObject.messageOwner.media.photo != null) {
                            new StickersAlert(PhotoViewer.this.parentActivity, PhotoViewer.this.currentMessageObject, PhotoViewer.this.currentMessageObject.messageOwner.media.photo).show();
                        }
                    } else if (i3 == 5) {
                        if (PhotoViewer.this.pipItem.getAlpha() == 1.0f) {
                            PhotoViewer.this.switchToPip();
                        }
                    } else if (i3 == 7) {
                        if (PhotoViewer.this.currentMessageObject != null) {
                            FileLoader.getInstance(PhotoViewer.this.currentAccount).cancelLoadFile(PhotoViewer.this.currentMessageObject.getDocument());
                            PhotoViewer.this.releasePlayer(false);
                            PhotoViewer.this.bottomLayout.setTag(1);
                            PhotoViewer.this.bottomLayout.setVisibility(0);
                        }
                    } else if (i3 == 14 && PhotoViewer.this.currentMessageObject != null) {
                        MessagesController.getInstance(PhotoViewer.this.currentAccount).saveGif(PhotoViewer.this.currentMessageObject, PhotoViewer.this.currentMessageObject.getDocument());
                    }
                }

                public /* synthetic */ void lambda$onItemClick$0$PhotoViewer$7(ArrayList arrayList, DialogsActivity dialogsActivity, ArrayList arrayList2, CharSequence charSequence, boolean z) {
                    ArrayList arrayList3 = arrayList2;
                    if (arrayList2.size() > 1 || ((Long) arrayList3.get(0)).longValue() == ((long) UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId()) || charSequence != null) {
                        ArrayList arrayList4 = arrayList;
                        for (int i = 0; i < arrayList2.size(); i++) {
                            long longValue = ((Long) arrayList3.get(i)).longValue();
                            if (charSequence != null) {
                                SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage(charSequence.toString(), longValue, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                            }
                            SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage(arrayList, longValue, true, 0);
                        }
                        dialogsActivity.finishFragment();
                        return;
                    }
                    long longValue2 = ((Long) arrayList3.get(0)).longValue();
                    int i2 = (int) longValue2;
                    int i3 = (int) (longValue2 >> 32);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("scrollToTopOnResume", true);
                    if (i2 == 0) {
                        bundle.putInt("enc_id", i3);
                    } else if (i2 > 0) {
                        bundle.putInt("user_id", i2);
                    } else if (i2 < 0) {
                        bundle.putInt("chat_id", -i2);
                    }
                    NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    ChatActivity chatActivity = new ChatActivity(bundle);
                    if (((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(chatActivity, true, false)) {
                        chatActivity.showFieldPanelForForward(true, arrayList);
                    } else {
                        dialogsActivity.finishFragment();
                    }
                }

                static /* synthetic */ void lambda$onItemClick$1(boolean[] zArr, View view) {
                    zArr[0] = !zArr[0];
                    ((CheckBoxCell) view).setChecked(zArr[0], true);
                }

                /* JADX WARNING: Code restructure failed: missing block: B:41:0x017d, code lost:
                    if (r2.location.volume_id == org.telegram.ui.PhotoViewer.access$10000(r11.this$0).location.volume_id) goto L_0x017f;
                 */
                /* JADX WARNING: Removed duplicated region for block: B:45:0x0184  */
                /* JADX WARNING: Removed duplicated region for block: B:46:0x0198  */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public /* synthetic */ void lambda$onItemClick$2$PhotoViewer$7(boolean[] r12, android.content.DialogInterface r13, int r14) {
                    /*
                        r11 = this;
                        org.telegram.ui.PhotoViewer r13 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r13 = r13.imagesArr
                        boolean r13 = r13.isEmpty()
                        r14 = 0
                        r0 = 0
                        if (r13 != 0) goto L_0x00cb
                        org.telegram.ui.PhotoViewer r13 = org.telegram.ui.PhotoViewer.this
                        int r13 = r13.currentIndex
                        if (r13 < 0) goto L_0x00ca
                        org.telegram.ui.PhotoViewer r13 = org.telegram.ui.PhotoViewer.this
                        int r13 = r13.currentIndex
                        org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r1 = r1.imagesArr
                        int r1 = r1.size()
                        if (r13 < r1) goto L_0x002a
                        goto L_0x00ca
                    L_0x002a:
                        org.telegram.ui.PhotoViewer r13 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r13 = r13.imagesArr
                        org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                        int r1 = r1.currentIndex
                        java.lang.Object r13 = r13.get(r1)
                        org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
                        boolean r1 = r13.isSent()
                        if (r1 == 0) goto L_0x02aa
                        org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                        r1.closePhoto(r0, r0)
                        java.util.ArrayList r3 = new java.util.ArrayList
                        r3.<init>()
                        org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                        int r1 = r1.slideshowMessageId
                        if (r1 == 0) goto L_0x0062
                        org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                        int r1 = r1.slideshowMessageId
                        java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                        r3.add(r1)
                        goto L_0x006d
                    L_0x0062:
                        int r1 = r13.getId()
                        java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                        r3.add(r1)
                    L_0x006d:
                        long r1 = r13.getDialogId()
                        int r2 = (int) r1
                        if (r2 != 0) goto L_0x00ab
                        org.telegram.tgnet.TLRPC$Message r1 = r13.messageOwner
                        long r1 = r1.random_id
                        r4 = 0
                        int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                        if (r6 == 0) goto L_0x00ab
                        java.util.ArrayList r14 = new java.util.ArrayList
                        r14.<init>()
                        org.telegram.tgnet.TLRPC$Message r1 = r13.messageOwner
                        long r1 = r1.random_id
                        java.lang.Long r1 = java.lang.Long.valueOf(r1)
                        r14.add(r1)
                        org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                        int r1 = r1.currentAccount
                        org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
                        long r4 = r13.getDialogId()
                        r2 = 32
                        long r4 = r4 >> r2
                        int r2 = (int) r4
                        java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                        org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r2)
                        r4 = r14
                        r5 = r1
                        goto L_0x00ad
                    L_0x00ab:
                        r4 = r14
                        r5 = r4
                    L_0x00ad:
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        int r14 = r14.currentAccount
                        org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r14)
                        long r6 = r13.getDialogId()
                        org.telegram.tgnet.TLRPC$Message r14 = r13.messageOwner
                        org.telegram.tgnet.TLRPC$Peer r14 = r14.to_id
                        int r8 = r14.channel_id
                        boolean r9 = r12[r0]
                        boolean r10 = r13.scheduled
                        r2.deleteMessages(r3, r4, r5, r6, r8, r9, r10)
                        goto L_0x02aa
                    L_0x00ca:
                        return
                    L_0x00cb:
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.avatarsArr
                        boolean r12 = r12.isEmpty()
                        r13 = -1
                        r1 = 1
                        if (r12 != 0) goto L_0x023e
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        int r12 = r12.currentIndex
                        if (r12 < 0) goto L_0x023d
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        int r12 = r12.currentIndex
                        org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r2 = r2.avatarsArr
                        int r2 = r2.size()
                        if (r12 < r2) goto L_0x00f5
                        goto L_0x023d
                    L_0x00f5:
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.avatarsArr
                        org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                        int r2 = r2.currentIndex
                        java.lang.Object r12 = r12.get(r2)
                        org.telegram.tgnet.TLRPC$Photo r12 = (org.telegram.tgnet.TLRPC.Photo) r12
                        org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r2 = r2.imagesArrLocations
                        org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this
                        int r3 = r3.currentIndex
                        java.lang.Object r2 = r2.get(r3)
                        org.telegram.messenger.ImageLocation r2 = (org.telegram.messenger.ImageLocation) r2
                        boolean r3 = r12 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty
                        if (r3 == 0) goto L_0x011e
                        r12 = r14
                    L_0x011e:
                        org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this
                        org.telegram.messenger.ImageLocation r3 = r3.currentUserAvatarLocation
                        if (r3 == 0) goto L_0x0181
                        if (r12 == 0) goto L_0x015d
                        java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r12.sizes
                        java.util.Iterator r2 = r2.iterator()
                    L_0x012e:
                        boolean r3 = r2.hasNext()
                        if (r3 == 0) goto L_0x0181
                        java.lang.Object r3 = r2.next()
                        org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC.PhotoSize) r3
                        org.telegram.tgnet.TLRPC$FileLocation r4 = r3.location
                        int r4 = r4.local_id
                        org.telegram.ui.PhotoViewer r5 = org.telegram.ui.PhotoViewer.this
                        org.telegram.messenger.ImageLocation r5 = r5.currentUserAvatarLocation
                        org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r5 = r5.location
                        int r5 = r5.local_id
                        if (r4 != r5) goto L_0x012e
                        org.telegram.tgnet.TLRPC$FileLocation r3 = r3.location
                        long r3 = r3.volume_id
                        org.telegram.ui.PhotoViewer r5 = org.telegram.ui.PhotoViewer.this
                        org.telegram.messenger.ImageLocation r5 = r5.currentUserAvatarLocation
                        org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r5 = r5.location
                        long r5 = r5.volume_id
                        int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                        if (r7 != 0) goto L_0x012e
                        goto L_0x017f
                    L_0x015d:
                        org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r3 = r2.location
                        int r3 = r3.local_id
                        org.telegram.ui.PhotoViewer r4 = org.telegram.ui.PhotoViewer.this
                        org.telegram.messenger.ImageLocation r4 = r4.currentUserAvatarLocation
                        org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r4 = r4.location
                        int r4 = r4.local_id
                        if (r3 != r4) goto L_0x0181
                        org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r2 = r2.location
                        long r2 = r2.volume_id
                        org.telegram.ui.PhotoViewer r4 = org.telegram.ui.PhotoViewer.this
                        org.telegram.messenger.ImageLocation r4 = r4.currentUserAvatarLocation
                        org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r4 = r4.location
                        long r4 = r4.volume_id
                        int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                        if (r6 != 0) goto L_0x0181
                    L_0x017f:
                        r2 = 1
                        goto L_0x0182
                    L_0x0181:
                        r2 = 0
                    L_0x0182:
                        if (r2 == 0) goto L_0x0198
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        int r12 = r12.currentAccount
                        org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
                        r12.deleteUserPhoto(r14)
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        r12.closePhoto(r0, r0)
                        goto L_0x02aa
                    L_0x0198:
                        if (r12 == 0) goto L_0x02aa
                        org.telegram.tgnet.TLRPC$TL_inputPhoto r14 = new org.telegram.tgnet.TLRPC$TL_inputPhoto
                        r14.<init>()
                        long r2 = r12.id
                        r14.id = r2
                        long r2 = r12.access_hash
                        r14.access_hash = r2
                        byte[] r2 = r12.file_reference
                        r14.file_reference = r2
                        byte[] r2 = r14.file_reference
                        if (r2 != 0) goto L_0x01b3
                        byte[] r2 = new byte[r0]
                        r14.file_reference = r2
                    L_0x01b3:
                        org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                        int r2 = r2.currentAccount
                        org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
                        r2.deleteUserPhoto(r14)
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        int r14 = r14.currentAccount
                        org.telegram.messenger.MessagesStorage r14 = org.telegram.messenger.MessagesStorage.getInstance(r14)
                        org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                        int r2 = r2.avatarsDialogId
                        long r3 = r12.id
                        r14.clearUserPhoto(r2, r3)
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.imagesArrLocations
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        int r14 = r14.currentIndex
                        r12.remove(r14)
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.imagesArrLocationsSizes
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        int r14 = r14.currentIndex
                        r12.remove(r14)
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.avatarsArr
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        int r14 = r14.currentIndex
                        r12.remove(r14)
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.imagesArrLocations
                        boolean r12 = r12.isEmpty()
                        if (r12 == 0) goto L_0x0215
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        r12.closePhoto(r0, r0)
                        goto L_0x02aa
                    L_0x0215:
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        int r12 = r12.currentIndex
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r14 = r14.avatarsArr
                        int r14 = r14.size()
                        if (r12 < r14) goto L_0x0232
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.avatarsArr
                        int r12 = r12.size()
                        int r12 = r12 - r1
                    L_0x0232:
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        int unused = r14.currentIndex = r13
                        org.telegram.ui.PhotoViewer r13 = org.telegram.ui.PhotoViewer.this
                        r13.setImageIndex(r12, r1)
                        goto L_0x02aa
                    L_0x023d:
                        return
                    L_0x023e:
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.secureDocuments
                        boolean r12 = r12.isEmpty()
                        if (r12 != 0) goto L_0x02aa
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        org.telegram.ui.PhotoViewer$PhotoViewerProvider r12 = r12.placeProvider
                        if (r12 != 0) goto L_0x0253
                        return
                    L_0x0253:
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.secureDocuments
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        int r14 = r14.currentIndex
                        r12.remove(r14)
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        org.telegram.ui.PhotoViewer$PhotoViewerProvider r12 = r12.placeProvider
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        int r14 = r14.currentIndex
                        r12.deleteImageAtIndex(r14)
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.secureDocuments
                        boolean r12 = r12.isEmpty()
                        if (r12 == 0) goto L_0x0283
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        r12.closePhoto(r0, r0)
                        goto L_0x02aa
                    L_0x0283:
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        int r12 = r12.currentIndex
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r14 = r14.secureDocuments
                        int r14 = r14.size()
                        if (r12 < r14) goto L_0x02a0
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.secureDocuments
                        int r12 = r12.size()
                        int r12 = r12 - r1
                    L_0x02a0:
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        int unused = r14.currentIndex = r13
                        org.telegram.ui.PhotoViewer r13 = org.telegram.ui.PhotoViewer.this
                        r13.setImageIndex(r12, r1)
                    L_0x02aa:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.AnonymousClass7.lambda$onItemClick$2$PhotoViewer$7(boolean[], android.content.DialogInterface, int):void");
                }

                public boolean canOpenMenu() {
                    if (PhotoViewer.this.currentMessageObject != null) {
                        return FileLoader.getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner).exists();
                    }
                    boolean z = false;
                    if (PhotoViewer.this.currentFileLocation == null) {
                        return false;
                    }
                    PhotoViewer photoViewer = PhotoViewer.this;
                    TLRPC.FileLocation access$9500 = photoViewer.getFileLocation(photoViewer.currentFileLocation);
                    if (PhotoViewer.this.avatarsDialogId != 0 || PhotoViewer.this.isEvent) {
                        z = true;
                    }
                    return FileLoader.getPathToAttach(access$9500, z).exists();
                }
            });
            ActionBarMenu createMenu = this.actionBar.createMenu();
            this.masksItem = createMenu.addItem(13, NUM);
            this.pipItem = createMenu.addItem(5, NUM);
            this.sendItem = createMenu.addItem(3, NUM);
            this.menuItem = createMenu.addItem(0, NUM);
            this.menuItem.addSubItem(11, NUM, (CharSequence) LocaleController.getString("OpenInExternalApp", NUM)).setColors(-328966, -328966);
            this.menuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            this.allMediaItem = this.menuItem.addSubItem(2, NUM, (CharSequence) LocaleController.getString("ShowAllMedia", NUM));
            this.allMediaItem.setColors(-328966, -328966);
            this.menuItem.addSubItem(14, NUM, (CharSequence) LocaleController.getString("SaveToGIFs", NUM)).setColors(-328966, -328966);
            this.menuItem.addSubItem(4, NUM, (CharSequence) LocaleController.getString("ShowInChat", NUM)).setColors(-328966, -328966);
            this.menuItem.addSubItem(10, NUM, (CharSequence) LocaleController.getString("ShareFile", NUM)).setColors(-328966, -328966);
            this.menuItem.addSubItem(1, NUM, (CharSequence) LocaleController.getString("SaveToGallery", NUM)).setColors(-328966, -328966);
            this.menuItem.addSubItem(6, NUM, (CharSequence) LocaleController.getString("Delete", NUM)).setColors(-328966, -328966);
            this.menuItem.addSubItem(7, NUM, (CharSequence) LocaleController.getString("StopDownload", NUM)).setColors(-328966, -328966);
            this.menuItem.redrawPopup(-NUM);
            this.sendItem.setContentDescription(LocaleController.getString("Forward", NUM));
            this.bottomLayout = new FrameLayout(this.actvityContext);
            this.bottomLayout.setBackgroundColor(NUM);
            this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.pressedDrawable[0] = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{NUM, 0});
            this.pressedDrawable[0].setShape(0);
            this.pressedDrawable[1] = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[]{NUM, 0});
            this.pressedDrawable[1].setShape(0);
            this.groupedPhotosListView = new GroupedPhotosListView(this.actvityContext);
            this.containerView.addView(this.groupedPhotosListView, LayoutHelper.createFrame(-1, 62.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            this.groupedPhotosListView.setDelegate(new GroupedPhotosListView.GroupedPhotosListViewDelegate() {
                public ArrayList<TLRPC.PageBlock> getPageBlockArr() {
                    return null;
                }

                public Object getParentObject() {
                    return null;
                }

                public int getCurrentIndex() {
                    return PhotoViewer.this.currentIndex;
                }

                public int getCurrentAccount() {
                    return PhotoViewer.this.currentAccount;
                }

                public int getAvatarsDialogId() {
                    return PhotoViewer.this.avatarsDialogId;
                }

                public int getSlideshowMessageId() {
                    return PhotoViewer.this.slideshowMessageId;
                }

                public ArrayList<ImageLocation> getImagesArrLocations() {
                    return PhotoViewer.this.imagesArrLocations;
                }

                public ArrayList<MessageObject> getImagesArr() {
                    return PhotoViewer.this.imagesArr;
                }

                public void setCurrentIndex(int i) {
                    int unused = PhotoViewer.this.currentIndex = -1;
                    if (PhotoViewer.this.currentThumb != null) {
                        PhotoViewer.this.currentThumb.release();
                        ImageReceiver.BitmapHolder unused2 = PhotoViewer.this.currentThumb = null;
                    }
                    PhotoViewer.this.setImageIndex(i, true);
                }
            });
            this.captionTextView = createCaptionTextView();
            this.switchCaptionTextView = createCaptionTextView();
            for (int i2 = 0; i2 < 3; i2++) {
                this.photoProgressViews[i2] = new PhotoProgressView(this.containerView.getContext(), this.containerView);
                this.photoProgressViews[i2].setBackgroundState(0, false);
            }
            this.miniProgressView = new RadialProgressView(this.actvityContext) {
                public void setAlpha(float f) {
                    super.setAlpha(f);
                    if (PhotoViewer.this.containerView != null) {
                        PhotoViewer.this.containerView.invalidate();
                    }
                }

                public void invalidate() {
                    super.invalidate();
                    if (PhotoViewer.this.containerView != null) {
                        PhotoViewer.this.containerView.invalidate();
                    }
                }
            };
            this.miniProgressView.setUseSelfAlpha(true);
            this.miniProgressView.setProgressColor(-1);
            this.miniProgressView.setSize(AndroidUtilities.dp(54.0f));
            this.miniProgressView.setBackgroundResource(NUM);
            this.miniProgressView.setVisibility(4);
            this.miniProgressView.setAlpha(0.0f);
            this.containerView.addView(this.miniProgressView, LayoutHelper.createFrame(64, 64, 17));
            this.shareButton = new ImageView(this.containerView.getContext());
            this.shareButton.setImageResource(NUM);
            this.shareButton.setScaleType(ImageView.ScaleType.CENTER);
            this.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.bottomLayout.addView(this.shareButton, LayoutHelper.createFrame(50, -1, 53));
            this.shareButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$2$PhotoViewer(view);
                }
            });
            this.shareButton.setContentDescription(LocaleController.getString("ShareFile", NUM));
            this.nameTextView = new TextView(this.containerView.getContext());
            this.nameTextView.setTextSize(1, 14.0f);
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setMaxLines(1);
            this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.nameTextView.setTextColor(-1);
            this.nameTextView.setGravity(3);
            this.bottomLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 5.0f, 60.0f, 0.0f));
            this.dateTextView = new TextView(this.containerView.getContext());
            this.dateTextView.setTextSize(1, 13.0f);
            this.dateTextView.setSingleLine(true);
            this.dateTextView.setMaxLines(1);
            this.dateTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.dateTextView.setTextColor(-1);
            this.dateTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.dateTextView.setGravity(3);
            this.bottomLayout.addView(this.dateTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 25.0f, 50.0f, 0.0f));
            createVideoControlsInterface();
            this.progressView = new RadialProgressView(this.parentActivity);
            this.progressView.setProgressColor(-1);
            this.progressView.setBackgroundResource(NUM);
            this.progressView.setVisibility(4);
            this.containerView.addView(this.progressView, LayoutHelper.createFrame(54, 54, 17));
            this.qualityPicker = new PickerBottomLayoutViewer(this.parentActivity);
            this.qualityPicker.setBackgroundColor(NUM);
            this.qualityPicker.updateSelectedCount(0, false);
            this.qualityPicker.setTranslationY((float) AndroidUtilities.dp(120.0f));
            this.qualityPicker.doneButton.setText(LocaleController.getString("Done", NUM).toUpperCase());
            this.qualityPicker.doneButton.setTextColor(Theme.getColor("dialogFloatingButton"));
            this.containerView.addView(this.qualityPicker, LayoutHelper.createFrame(-1, 48, 83));
            this.qualityPicker.cancelButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$3$PhotoViewer(view);
                }
            });
            this.qualityPicker.doneButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$4$PhotoViewer(view);
                }
            });
            this.videoForwardDrawable = new VideoForwardDrawable();
            this.videoForwardDrawable.setDelegate(new VideoForwardDrawable.VideoForwardDrawableDelegate() {
                public void onAnimationEnd() {
                }

                public void invalidate() {
                    PhotoViewer.this.containerView.invalidate();
                }
            });
            this.qualityChooseView = new QualityChooseView(this.parentActivity);
            this.qualityChooseView.setTranslationY((float) AndroidUtilities.dp(120.0f));
            this.qualityChooseView.setVisibility(4);
            this.qualityChooseView.setBackgroundColor(NUM);
            this.containerView.addView(this.qualityChooseView, LayoutHelper.createFrame(-1, 70.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            this.pickerView = new FrameLayout(this.actvityContext) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(motionEvent);
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(motionEvent);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
                }
            };
            this.pickerView.setBackgroundColor(NUM);
            this.containerView.addView(this.pickerView, LayoutHelper.createFrame(-1, -2, 83));
            this.docNameTextView = new TextView(this.containerView.getContext());
            this.docNameTextView.setTextSize(1, 15.0f);
            this.docNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.docNameTextView.setSingleLine(true);
            this.docNameTextView.setMaxLines(1);
            this.docNameTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.docNameTextView.setTextColor(-1);
            this.docNameTextView.setGravity(3);
            this.pickerView.addView(this.docNameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 20.0f, 23.0f, 84.0f, 0.0f));
            this.docInfoTextView = new TextView(this.containerView.getContext());
            this.docInfoTextView.setTextSize(1, 14.0f);
            this.docInfoTextView.setSingleLine(true);
            this.docInfoTextView.setMaxLines(1);
            this.docInfoTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.docInfoTextView.setTextColor(-1);
            this.docInfoTextView.setGravity(3);
            this.pickerView.addView(this.docInfoTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 20.0f, 46.0f, 84.0f, 0.0f));
            this.videoTimelineView = new VideoTimelinePlayView(this.parentActivity);
            this.videoTimelineView.setDelegate(new VideoTimelinePlayView.VideoTimelineViewDelegate() {
                public void didStartDragging() {
                }

                public void didStopDragging() {
                }

                public void onLeftProgressChanged(float f) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        if (PhotoViewer.this.videoPlayer.isPlaying()) {
                            PhotoViewer.this.videoPlayer.pause();
                            PhotoViewer.this.containerView.invalidate();
                        }
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoDuration * f)));
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                        PhotoViewer.this.videoTimelineView.setProgress(0.0f);
                        PhotoViewer.this.updateVideoInfo();
                    }
                }

                public void onRightProgressChanged(float f) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        if (PhotoViewer.this.videoPlayer.isPlaying()) {
                            PhotoViewer.this.videoPlayer.pause();
                            PhotoViewer.this.containerView.invalidate();
                        }
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoDuration * f)));
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(1.0f);
                        PhotoViewer.this.videoTimelineView.setProgress(1.0f);
                        PhotoViewer.this.updateVideoInfo();
                    }
                }

                public void onPlayProgressChanged(float f) {
                    if (PhotoViewer.this.videoPlayer != null) {
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoDuration * f)));
                    }
                }
            });
            this.pickerView.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, 58.0f, 51, 0.0f, 8.0f, 0.0f, 88.0f));
            this.pickerViewSendButton = new ImageView(this.parentActivity) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(motionEvent);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
                }
            };
            this.pickerViewSendButton.setScaleType(ImageView.ScaleType.CENTER);
            this.pickerViewSendButton.setBackgroundDrawable(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("dialogFloatingButton"), Theme.getColor(Build.VERSION.SDK_INT >= 21 ? "dialogFloatingButtonPressed" : "dialogFloatingButton")));
            this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
            this.pickerViewSendButton.setImageResource(NUM);
            this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
            this.containerView.addView(this.pickerViewSendButton, LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 14.0f, 14.0f));
            this.pickerViewSendButton.setContentDescription(LocaleController.getString("Send", NUM));
            this.pickerViewSendButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$5$PhotoViewer(view);
                }
            });
            this.pickerViewSendButton.setOnLongClickListener(new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return PhotoViewer.this.lambda$setParentActivity$8$PhotoViewer(view);
                }
            });
            LinearLayout linearLayout = new LinearLayout(this.parentActivity);
            linearLayout.setOrientation(0);
            this.pickerView.addView(linearLayout, LayoutHelper.createFrame(-2, 48.0f, 81, 0.0f, 0.0f, 34.0f, 0.0f));
            this.cropItem = new ImageView(this.parentActivity);
            this.cropItem.setScaleType(ImageView.ScaleType.CENTER);
            this.cropItem.setImageResource(NUM);
            this.cropItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            linearLayout.addView(this.cropItem, LayoutHelper.createLinear(70, 48));
            this.cropItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$9$PhotoViewer(view);
                }
            });
            this.cropItem.setContentDescription(LocaleController.getString("CropImage", NUM));
            this.rotateItem = new ImageView(this.parentActivity);
            this.rotateItem.setScaleType(ImageView.ScaleType.CENTER);
            this.rotateItem.setImageResource(NUM);
            this.rotateItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            linearLayout.addView(this.rotateItem, LayoutHelper.createLinear(70, 48));
            this.rotateItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$10$PhotoViewer(view);
                }
            });
            this.rotateItem.setContentDescription(LocaleController.getString("AccDescrRotate", NUM));
            this.paintItem = new ImageView(this.parentActivity);
            this.paintItem.setScaleType(ImageView.ScaleType.CENTER);
            this.paintItem.setImageResource(NUM);
            this.paintItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            linearLayout.addView(this.paintItem, LayoutHelper.createLinear(70, 48));
            this.paintItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$11$PhotoViewer(view);
                }
            });
            this.paintItem.setContentDescription(LocaleController.getString("AccDescrPhotoEditor", NUM));
            this.compressItem = new ImageView(this.parentActivity);
            this.compressItem.setTag(1);
            this.compressItem.setScaleType(ImageView.ScaleType.CENTER);
            this.compressItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.selectedCompression = MessagesController.getGlobalMainSettings().getInt("compress_video2", selectCompression());
            int i3 = this.selectedCompression;
            if (i3 <= 1) {
                this.compressItem.setImageResource(NUM);
                i = 48;
            } else {
                if (i3 == 2) {
                    this.compressItem.setImageResource(NUM);
                } else {
                    this.selectedCompression = this.compressionsCount - 1;
                    this.compressItem.setImageResource(NUM);
                }
                i = 64;
            }
            linearLayout.addView(this.compressItem, LayoutHelper.createLinear(70, 48));
            float f = (float) (70 - i);
            this.compressItem.setPadding(AndroidUtilities.dp(f) / 2, 0, AndroidUtilities.dp(f) / 2, 0);
            this.compressItem.setOnClickListener(new View.OnClickListener(activity2) {
                private final /* synthetic */ Activity f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$12$PhotoViewer(this.f$1, view);
                }
            });
            this.compressItem.setContentDescription(LocaleController.getString("AccDescrVideoQuality", NUM) + ", " + new String[]{"360", "480", "720", "1080"}[Math.max(0, this.selectedCompression)]);
            this.muteItem = new ImageView(this.parentActivity);
            this.muteItem.setScaleType(ImageView.ScaleType.CENTER);
            this.muteItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            linearLayout.addView(this.muteItem, LayoutHelper.createLinear(70, 48));
            this.muteItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$13$PhotoViewer(view);
                }
            });
            this.cameraItem = new ImageView(this.parentActivity);
            this.cameraItem.setScaleType(ImageView.ScaleType.CENTER);
            this.cameraItem.setImageResource(NUM);
            this.cameraItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.cameraItem.setContentDescription(LocaleController.getString("AccDescrTakeMorePics", NUM));
            this.containerView.addView(this.cameraItem, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 16.0f, 0.0f));
            this.cameraItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$14$PhotoViewer(view);
                }
            });
            this.tuneItem = new ImageView(this.parentActivity);
            this.tuneItem.setScaleType(ImageView.ScaleType.CENTER);
            this.tuneItem.setImageResource(NUM);
            this.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            linearLayout.addView(this.tuneItem, LayoutHelper.createLinear(70, 48));
            this.tuneItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$15$PhotoViewer(view);
                }
            });
            this.tuneItem.setContentDescription(LocaleController.getString("AccDescrPhotoAdjust", NUM));
            this.timeItem = new ImageView(this.parentActivity);
            this.timeItem.setScaleType(ImageView.ScaleType.CENTER);
            this.timeItem.setImageResource(NUM);
            this.timeItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.timeItem.setContentDescription(LocaleController.getString("SetTimer", NUM));
            linearLayout.addView(this.timeItem, LayoutHelper.createLinear(70, 48));
            this.timeItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$21$PhotoViewer(view);
                }
            });
            this.editorDoneLayout = new PickerBottomLayoutViewer(this.actvityContext);
            this.editorDoneLayout.setBackgroundColor(NUM);
            this.editorDoneLayout.updateSelectedCount(0, false);
            this.editorDoneLayout.setVisibility(8);
            this.containerView.addView(this.editorDoneLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.editorDoneLayout.cancelButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$22$PhotoViewer(view);
                }
            });
            this.editorDoneLayout.doneButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$23$PhotoViewer(view);
                }
            });
            this.resetButton = new TextView(this.actvityContext);
            this.resetButton.setVisibility(8);
            this.resetButton.setTextSize(1, 14.0f);
            this.resetButton.setTextColor(-1);
            this.resetButton.setGravity(17);
            this.resetButton.setBackgroundDrawable(Theme.createSelectorDrawable(-12763843, 0));
            this.resetButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
            this.resetButton.setText(LocaleController.getString("Reset", NUM).toUpperCase());
            this.resetButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.editorDoneLayout.addView(this.resetButton, LayoutHelper.createFrame(-2, -1, 49));
            this.resetButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$24$PhotoViewer(view);
                }
            });
            this.gestureDetector = new GestureDetector2(this.containerView.getContext(), (GestureDetector2.OnGestureListener) this);
            this.gestureDetector.setIsLongpressEnabled(false);
            setDoubleTapEnabled(true);
            $$Lambda$PhotoViewer$PCLASSNAMEVrOt5LzTSK9H7QNcqFyuY r1 = new ImageReceiver.ImageReceiverDelegate() {
                public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
                    PhotoViewer.this.lambda$setParentActivity$25$PhotoViewer(imageReceiver, z, z2);
                }

                public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                    ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
                }
            };
            this.centerImage.setParentView(this.containerView);
            this.centerImage.setCrossfadeAlpha((byte) 2);
            this.centerImage.setInvalidateAll(true);
            this.centerImage.setDelegate(r1);
            this.leftImage.setParentView(this.containerView);
            this.leftImage.setCrossfadeAlpha((byte) 2);
            this.leftImage.setInvalidateAll(true);
            this.leftImage.setDelegate(r1);
            this.rightImage.setParentView(this.containerView);
            this.rightImage.setCrossfadeAlpha((byte) 2);
            this.rightImage.setInvalidateAll(true);
            this.rightImage.setDelegate(r1);
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            this.checkImageView = new CheckBox(this.containerView.getContext(), NUM) {
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
                }
            };
            this.checkImageView.setDrawBackground(true);
            this.checkImageView.setHasBorder(true);
            this.checkImageView.setSize(40);
            this.checkImageView.setCheckOffset(AndroidUtilities.dp(1.0f));
            this.checkImageView.setColor(Theme.getColor("dialogFloatingButton"), -1);
            this.checkImageView.setVisibility(8);
            this.containerView.addView(this.checkImageView, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, (rotation == 3 || rotation == 1) ? 58.0f : 68.0f, 10.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                ((FrameLayout.LayoutParams) this.checkImageView.getLayoutParams()).topMargin += AndroidUtilities.statusBarHeight;
            }
            this.checkImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$26$PhotoViewer(view);
                }
            });
            this.photosCounterView = new CounterView(this.parentActivity);
            this.containerView.addView(this.photosCounterView, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, (rotation == 3 || rotation == 1) ? 58.0f : 68.0f, 66.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                ((FrameLayout.LayoutParams) this.photosCounterView.getLayoutParams()).topMargin += AndroidUtilities.statusBarHeight;
            }
            this.photosCounterView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$27$PhotoViewer(view);
                }
            });
            this.selectedPhotosListView = new RecyclerListView(this.parentActivity);
            this.selectedPhotosListView.setVisibility(8);
            this.selectedPhotosListView.setAlpha(0.0f);
            this.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
            this.selectedPhotosListView.addItemDecoration(new RecyclerView.ItemDecoration() {
                public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                    int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                    if (!(view instanceof PhotoPickerPhotoCell) || childAdapterPosition != 0) {
                        rect.left = 0;
                    } else {
                        rect.left = AndroidUtilities.dp(3.0f);
                    }
                    rect.right = AndroidUtilities.dp(3.0f);
                }
            });
            ((DefaultItemAnimator) this.selectedPhotosListView.getItemAnimator()).setDelayAnimations(false);
            this.selectedPhotosListView.setBackgroundColor(NUM);
            this.selectedPhotosListView.setPadding(0, AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f));
            this.selectedPhotosListView.setLayoutManager(new LinearLayoutManager(this.parentActivity, 0, false) {
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                    LinearSmoothScrollerEnd linearSmoothScrollerEnd = new LinearSmoothScrollerEnd(recyclerView.getContext());
                    linearSmoothScrollerEnd.setTargetPosition(i);
                    startSmoothScroll(linearSmoothScrollerEnd);
                }
            });
            RecyclerListView recyclerListView = this.selectedPhotosListView;
            ListAdapter listAdapter = new ListAdapter(this.parentActivity);
            this.selectedPhotosAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            this.containerView.addView(this.selectedPhotosListView, LayoutHelper.createFrame(-1, 88, 51));
            this.selectedPhotosListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    PhotoViewer.this.lambda$setParentActivity$28$PhotoViewer(view, i);
                }
            });
            this.captionEditText = new PhotoViewerCaptionEnterView(this.actvityContext, this.containerView, this.windowView) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    try {
                        return !PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(motionEvent);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return false;
                    }
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    try {
                        return !PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(motionEvent);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return false;
                    }
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
                }

                /* access modifiers changed from: protected */
                public void extendActionMode(ActionMode actionMode, Menu menu) {
                    if (PhotoViewer.this.parentChatActivity != null) {
                        PhotoViewer.this.parentChatActivity.extendActionMode(menu);
                    }
                }
            };
            this.captionEditText.setDelegate(new PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate() {
                public void onCaptionEnter() {
                    PhotoViewer.this.closeCaptionEnter(true);
                }

                public void onTextChanged(CharSequence charSequence) {
                    if (PhotoViewer.this.mentionsAdapter != null && PhotoViewer.this.captionEditText != null && PhotoViewer.this.parentChatActivity != null && charSequence != null) {
                        PhotoViewer.this.mentionsAdapter.searchUsernameOrHashtag(charSequence.toString(), PhotoViewer.this.captionEditText.getCursorPosition(), PhotoViewer.this.parentChatActivity.messages, false);
                    }
                }

                public void onWindowSizeChanged(int i) {
                    if (i - (ActionBar.getCurrentActionBarHeight() * 2) < AndroidUtilities.dp((float) ((Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36) + (PhotoViewer.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0)))) {
                        boolean unused = PhotoViewer.this.allowMentions = false;
                        if (PhotoViewer.this.mentionListView != null && PhotoViewer.this.mentionListView.getVisibility() == 0) {
                            PhotoViewer.this.mentionListView.setVisibility(4);
                            return;
                        }
                        return;
                    }
                    boolean unused2 = PhotoViewer.this.allowMentions = true;
                    if (PhotoViewer.this.mentionListView != null && PhotoViewer.this.mentionListView.getVisibility() == 4) {
                        PhotoViewer.this.mentionListView.setVisibility(0);
                    }
                }
            });
            if (Build.VERSION.SDK_INT >= 19) {
                this.captionEditText.setImportantForAccessibility(4);
            }
            this.containerView.addView(this.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
            this.mentionListView = new RecyclerListView(this.actvityContext) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(motionEvent);
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(motionEvent);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return !PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
                }
            };
            this.mentionListView.setTag(5);
            this.mentionLayoutManager = new LinearLayoutManager(this.actvityContext) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            this.mentionLayoutManager.setOrientation(1);
            this.mentionListView.setLayoutManager(this.mentionLayoutManager);
            this.mentionListView.setBackgroundColor(NUM);
            this.mentionListView.setVisibility(8);
            this.mentionListView.setClipToPadding(true);
            this.mentionListView.setOverScrollMode(2);
            this.containerView.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
            RecyclerListView recyclerListView2 = this.mentionListView;
            MentionsAdapter mentionsAdapter2 = new MentionsAdapter(this.actvityContext, true, 0, new MentionsAdapter.MentionsAdapterDelegate() {
                public void onContextClick(TLRPC.BotInlineResult botInlineResult) {
                }

                public void onContextSearch(boolean z) {
                }

                public void needChangePanelVisibility(boolean z) {
                    if (z) {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.mentionListView.getLayoutParams();
                        float min = (float) ((Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36) + (PhotoViewer.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0));
                        layoutParams.height = AndroidUtilities.dp(min);
                        layoutParams.topMargin = -AndroidUtilities.dp(min);
                        PhotoViewer.this.mentionListView.setLayoutParams(layoutParams);
                        if (PhotoViewer.this.mentionListAnimation != null) {
                            PhotoViewer.this.mentionListAnimation.cancel();
                            AnimatorSet unused = PhotoViewer.this.mentionListAnimation = null;
                        }
                        if (PhotoViewer.this.mentionListView.getVisibility() == 0) {
                            PhotoViewer.this.mentionListView.setAlpha(1.0f);
                            return;
                        }
                        PhotoViewer.this.mentionLayoutManager.scrollToPositionWithOffset(0, 10000);
                        if (PhotoViewer.this.allowMentions) {
                            PhotoViewer.this.mentionListView.setVisibility(0);
                            AnimatorSet unused2 = PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                            PhotoViewer.this.mentionListAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, View.ALPHA, new float[]{0.0f, 1.0f})});
                            PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animator)) {
                                        AnimatorSet unused = PhotoViewer.this.mentionListAnimation = null;
                                    }
                                }
                            });
                            PhotoViewer.this.mentionListAnimation.setDuration(200);
                            PhotoViewer.this.mentionListAnimation.start();
                            return;
                        }
                        PhotoViewer.this.mentionListView.setAlpha(1.0f);
                        PhotoViewer.this.mentionListView.setVisibility(4);
                        return;
                    }
                    if (PhotoViewer.this.mentionListAnimation != null) {
                        PhotoViewer.this.mentionListAnimation.cancel();
                        AnimatorSet unused3 = PhotoViewer.this.mentionListAnimation = null;
                    }
                    if (PhotoViewer.this.mentionListView.getVisibility() != 8) {
                        if (PhotoViewer.this.allowMentions) {
                            AnimatorSet unused4 = PhotoViewer.this.mentionListAnimation = new AnimatorSet();
                            PhotoViewer.this.mentionListAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.mentionListView, View.ALPHA, new float[]{0.0f})});
                            PhotoViewer.this.mentionListAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (PhotoViewer.this.mentionListAnimation != null && PhotoViewer.this.mentionListAnimation.equals(animator)) {
                                        PhotoViewer.this.mentionListView.setVisibility(8);
                                        AnimatorSet unused = PhotoViewer.this.mentionListAnimation = null;
                                    }
                                }
                            });
                            PhotoViewer.this.mentionListAnimation.setDuration(200);
                            PhotoViewer.this.mentionListAnimation.start();
                            return;
                        }
                        PhotoViewer.this.mentionListView.setVisibility(8);
                    }
                }
            });
            this.mentionsAdapter = mentionsAdapter2;
            recyclerListView2.setAdapter(mentionsAdapter2);
            this.mentionListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    PhotoViewer.this.lambda$setParentActivity$29$PhotoViewer(view, i);
                }
            });
            this.mentionListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
                public final boolean onItemClick(View view, int i) {
                    return PhotoViewer.this.lambda$setParentActivity$31$PhotoViewer(view, i);
                }
            });
            if (((AccessibilityManager) this.actvityContext.getSystemService("accessibility")).isEnabled()) {
                this.playButtonAccessibilityOverlay = new View(this.actvityContext);
                this.playButtonAccessibilityOverlay.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
                this.playButtonAccessibilityOverlay.setFocusable(true);
                this.containerView.addView(this.playButtonAccessibilityOverlay, LayoutHelper.createFrame(64, 64, 17));
            }
        }
    }

    public /* synthetic */ WindowInsets lambda$setParentActivity$1$PhotoViewer(View view, WindowInsets windowInsets) {
        WindowInsets windowInsets2 = (WindowInsets) this.lastInsets;
        this.lastInsets = windowInsets;
        if (windowInsets2 == null || !windowInsets2.toString().equals(windowInsets.toString())) {
            int i = this.animationInProgress;
            if (i == 1 || i == 3) {
                ClippingImageView clippingImageView = this.animatingImageView;
                clippingImageView.setTranslationX(clippingImageView.getTranslationX() - ((float) getLeftInset()));
                this.animationValues[0][2] = this.animatingImageView.getTranslationX();
            }
            FrameLayout frameLayout = this.windowView;
            if (frameLayout != null) {
                frameLayout.requestLayout();
            }
        }
        this.containerView.setPadding(windowInsets.getSystemWindowInsetLeft(), 0, windowInsets.getSystemWindowInsetRight(), 0);
        return windowInsets.consumeSystemWindowInsets();
    }

    public /* synthetic */ void lambda$setParentActivity$2$PhotoViewer(View view) {
        onSharePressed();
    }

    public /* synthetic */ void lambda$setParentActivity$3$PhotoViewer(View view) {
        this.selectedCompression = this.previousCompression;
        didChangedCompressionLevel(false);
        showQualityView(false);
        requestVideoPreview(2);
    }

    public /* synthetic */ void lambda$setParentActivity$4$PhotoViewer(View view) {
        showQualityView(false);
        requestVideoPreview(2);
    }

    public /* synthetic */ void lambda$setParentActivity$5$PhotoViewer(View view) {
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity == null || !chatActivity.isInScheduleMode() || this.parentChatActivity.isEditingMessageMedia()) {
            sendPressed(true, 0);
        } else {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentChatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    PhotoViewer.this.sendPressed(z, i);
                }
            });
        }
    }

    public /* synthetic */ boolean lambda$setParentActivity$8$PhotoViewer(View view) {
        PhotoViewerProvider photoViewerProvider;
        boolean z;
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity != null && !chatActivity.isInScheduleMode()) {
            this.parentChatActivity.getCurrentChat();
            TLRPC.User currentUser = this.parentChatActivity.getCurrentUser();
            if (this.parentChatActivity.getCurrentEncryptedChat() != null) {
                return false;
            }
            this.sendPopupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity);
            this.sendPopupLayout.setAnimationEnabled(false);
            this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                private Rect popupRect = new Rect();

                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getActionMasked() != 0 || PhotoViewer.this.sendPopupWindow == null || !PhotoViewer.this.sendPopupWindow.isShowing()) {
                        return false;
                    }
                    view.getHitRect(this.popupRect);
                    if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                        return false;
                    }
                    PhotoViewer.this.sendPopupWindow.dismiss();
                    return false;
                }
            });
            this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    PhotoViewer.this.lambda$null$6$PhotoViewer(keyEvent);
                }
            });
            this.sendPopupLayout.setShowedFromBotton(false);
            this.sendPopupLayout.setBackgroundColor(-NUM);
            int i = 0;
            for (int i2 = 0; i2 < 2; i2++) {
                if (i2 == 0 && (photoViewerProvider = this.placeProvider) != null && photoViewerProvider.getSelectedPhotos() != null) {
                    Iterator<Map.Entry<Object, Object>> it = this.placeProvider.getSelectedPhotos().entrySet().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            z = false;
                            break;
                        }
                        Object value = it.next().getValue();
                        if (!(value instanceof MediaController.PhotoEntry)) {
                            if ((value instanceof MediaController.SearchImage) && ((MediaController.SearchImage) value).ttl != 0) {
                                break;
                            }
                        } else if (((MediaController.PhotoEntry) value).ttl != 0) {
                            break;
                        }
                    }
                    z = true;
                    if (z) {
                    }
                } else if (i2 == 1 && UserObject.isUserSelf(currentUser)) {
                }
                ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(this.parentActivity);
                actionBarMenuSubItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 7));
                if (i2 == 0) {
                    if (UserObject.isUserSelf(currentUser)) {
                        actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                    } else {
                        actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                    }
                } else if (i2 == 1) {
                    actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                }
                actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
                actionBarMenuSubItem.setColors(-1, -1);
                this.sendPopupLayout.addView(actionBarMenuSubItem, LayoutHelper.createFrame(-1, 48.0f, LocaleController.isRTL ? 5 : 3, 0.0f, (float) (i * 48), 0.0f, 0.0f));
                actionBarMenuSubItem.setOnClickListener(new View.OnClickListener(i2) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        PhotoViewer.this.lambda$null$7$PhotoViewer(this.f$1, view);
                    }
                });
                i++;
            }
            this.sendPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
            this.sendPopupWindow.setAnimationEnabled(false);
            this.sendPopupWindow.setAnimationStyle(NUM);
            this.sendPopupWindow.setOutsideTouchable(true);
            this.sendPopupWindow.setClippingEnabled(true);
            this.sendPopupWindow.setInputMethodMode(2);
            this.sendPopupWindow.setSoftInputMode(0);
            this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
            this.sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.sendPopupWindow.setFocusable(true);
            int[] iArr = new int[2];
            view.getLocationInWindow(iArr);
            this.sendPopupWindow.showAtLocation(view, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(14.0f), (iArr[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(18.0f));
            view.performHapticFeedback(3, 2);
        }
        return false;
    }

    public /* synthetic */ void lambda$null$6$PhotoViewer(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$null$7$PhotoViewer(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentChatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    PhotoViewer.this.sendPressed(z, i);
                }
            });
        } else if (i == 1) {
            sendPressed(false, 0);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$9$PhotoViewer(View view) {
        if (this.captionEditText.getTag() == null) {
            switchToEditMode(1);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$10$PhotoViewer(View view) {
        PhotoCropView photoCropView2 = this.photoCropView;
        if (photoCropView2 != null) {
            photoCropView2.rotate();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$11$PhotoViewer(View view) {
        if (this.captionEditText.getTag() == null) {
            switchToEditMode(3);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$12$PhotoViewer(Activity activity, View view) {
        if (this.captionEditText.getTag() == null) {
            if (this.compressItem.getTag() != null) {
                showQualityView(true);
                requestVideoPreview(1);
            } else if (this.videoConvertSupported) {
                if (this.tooltip == null) {
                    this.tooltip = new Tooltip(activity, this.containerView, -NUM, -1);
                }
                this.tooltip.setText(LocaleController.getString("VideoQualityIsTooLow", NUM));
                this.tooltip.show(this.compressItem);
            }
        }
    }

    public /* synthetic */ void lambda$setParentActivity$13$PhotoViewer(View view) {
        if (this.captionEditText.getTag() == null) {
            this.muteVideo = !this.muteVideo;
            updateMuteButton();
            updateVideoInfo();
            if (!this.muteVideo || this.checkImageView.isChecked()) {
                Object obj = this.imagesArrLocals.get(this.currentIndex);
                if (obj instanceof MediaController.PhotoEntry) {
                    ((MediaController.PhotoEntry) obj).editedInfo = getCurrentVideoEditedInfo();
                    return;
                }
                return;
            }
            this.checkImageView.callOnClick();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$14$PhotoViewer(View view) {
        if (this.placeProvider != null && this.captionEditText.getTag() == null) {
            this.placeProvider.needAddMorePhotos();
            closePhoto(true, false);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$15$PhotoViewer(View view) {
        if (this.captionEditText.getTag() == null) {
            switchToEditMode(2);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$21$PhotoViewer(View view) {
        String str;
        int i;
        int i2;
        if (this.parentActivity != null && this.captionEditText.getTag() == null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
            builder.setUseHardwareLayer(false);
            LinearLayout linearLayout = new LinearLayout(this.parentActivity);
            linearLayout.setOrientation(1);
            builder.setCustomView(linearLayout);
            TextView textView = new TextView(this.parentActivity);
            textView.setLines(1);
            textView.setSingleLine(true);
            textView.setText(LocaleController.getString("MessageLifetime", NUM));
            textView.setTextColor(-1);
            textView.setTextSize(1, 16.0f);
            textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(4.0f));
            textView.setGravity(16);
            linearLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f));
            textView.setOnTouchListener($$Lambda$PhotoViewer$dNgnxOfGozESSNmhIUiGrmXOvYo.INSTANCE);
            TextView textView2 = new TextView(this.parentActivity);
            if (this.isCurrentVideo) {
                i = NUM;
                str = "MessageLifetimeVideo";
            } else {
                i = NUM;
                str = "MessageLifetimePhoto";
            }
            textView2.setText(LocaleController.getString(str, i));
            textView2.setTextColor(-8355712);
            textView2.setTextSize(1, 14.0f);
            textView2.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            textView2.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
            textView2.setGravity(16);
            linearLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f));
            textView2.setOnTouchListener($$Lambda$PhotoViewer$NS34BJTSE805Ka6Cky_LZyYatpM.INSTANCE);
            BottomSheet create = builder.create();
            NumberPicker numberPicker = new NumberPicker(this.parentActivity);
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(28);
            Object obj = this.imagesArrLocals.get(this.currentIndex);
            if (obj instanceof MediaController.PhotoEntry) {
                i2 = ((MediaController.PhotoEntry) obj).ttl;
            } else {
                i2 = obj instanceof MediaController.SearchImage ? ((MediaController.SearchImage) obj).ttl : 0;
            }
            if (i2 == 0) {
                numberPicker.setValue(MessagesController.getGlobalMainSettings().getInt("self_destruct", 7));
            } else if (i2 < 0 || i2 >= 21) {
                numberPicker.setValue(((i2 / 5) + 21) - 5);
            } else {
                numberPicker.setValue(i2);
            }
            numberPicker.setTextColor(-1);
            numberPicker.setSelectorColor(-11711155);
            numberPicker.setFormatter($$Lambda$PhotoViewer$jBfQR6oRFkAIVuAnOk58WefFBr8.INSTANCE);
            linearLayout.addView(numberPicker, LayoutHelper.createLinear(-1, -2));
            AnonymousClass15 r6 = new FrameLayout(this.parentActivity) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int childCount = getChildCount();
                    int i5 = i3 - i;
                    View view = null;
                    for (int i6 = 0; i6 < childCount; i6++) {
                        View childAt = getChildAt(i6);
                        if (((Integer) childAt.getTag()).intValue() == -1) {
                            childAt.layout((i5 - getPaddingRight()) - childAt.getMeasuredWidth(), getPaddingTop(), (i5 - getPaddingRight()) + childAt.getMeasuredWidth(), getPaddingTop() + childAt.getMeasuredHeight());
                            view = childAt;
                        } else if (((Integer) childAt.getTag()).intValue() == -2) {
                            int paddingRight = (i5 - getPaddingRight()) - childAt.getMeasuredWidth();
                            if (view != null) {
                                paddingRight -= view.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                            }
                            childAt.layout(paddingRight, getPaddingTop(), childAt.getMeasuredWidth() + paddingRight, getPaddingTop() + childAt.getMeasuredHeight());
                        } else {
                            childAt.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + childAt.getMeasuredWidth(), getPaddingTop() + childAt.getMeasuredHeight());
                        }
                    }
                }
            };
            r6.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            linearLayout.addView(r6, LayoutHelper.createLinear(-1, 52));
            TextView textView3 = new TextView(this.parentActivity);
            textView3.setMinWidth(AndroidUtilities.dp(64.0f));
            textView3.setTag(-1);
            textView3.setTextSize(1, 14.0f);
            textView3.setTextColor(Theme.getColor("dialogFloatingButton"));
            textView3.setGravity(17);
            textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView3.setText(LocaleController.getString("Done", NUM).toUpperCase());
            textView3.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(-11944718));
            textView3.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            r6.addView(textView3, LayoutHelper.createFrame(-2, 36, 53));
            textView3.setOnClickListener(new View.OnClickListener(numberPicker, create) {
                private final /* synthetic */ NumberPicker f$1;
                private final /* synthetic */ BottomSheet f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    PhotoViewer.this.lambda$null$19$PhotoViewer(this.f$1, this.f$2, view);
                }
            });
            TextView textView4 = new TextView(this.parentActivity);
            textView4.setMinWidth(AndroidUtilities.dp(64.0f));
            textView4.setTag(-2);
            textView4.setTextSize(1, 14.0f);
            textView4.setTextColor(Theme.getColor("dialogFloatingButton"));
            textView4.setGravity(17);
            textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView4.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
            textView4.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(-11944718));
            textView4.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            r6.addView(textView4, LayoutHelper.createFrame(-2, 36, 53));
            textView4.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    BottomSheet.this.dismiss();
                }
            });
            create.show();
            create.setBackgroundColor(-16777216);
        }
    }

    static /* synthetic */ String lambda$null$18(int i) {
        if (i == 0) {
            return LocaleController.getString("ShortMessageLifetimeForever", NUM);
        }
        if (i < 1 || i >= 21) {
            return LocaleController.formatTTLString((i - 16) * 5);
        }
        return LocaleController.formatTTLString(i);
    }

    public /* synthetic */ void lambda$null$19$PhotoViewer(NumberPicker numberPicker, BottomSheet bottomSheet, View view) {
        int value = numberPicker.getValue();
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("self_destruct", value);
        edit.commit();
        bottomSheet.dismiss();
        if (value < 0 || value >= 21) {
            value = (value - 16) * 5;
        }
        Object obj = this.imagesArrLocals.get(this.currentIndex);
        if (obj instanceof MediaController.PhotoEntry) {
            ((MediaController.PhotoEntry) obj).ttl = value;
        } else if (obj instanceof MediaController.SearchImage) {
            ((MediaController.SearchImage) obj).ttl = value;
        }
        this.timeItem.setColorFilter(value != 0 ? new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY) : null);
        if (!this.checkImageView.isChecked()) {
            this.checkImageView.callOnClick();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$22$PhotoViewer(View view) {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$setParentActivity$23$PhotoViewer(View view) {
        if (this.currentEditMode != 1 || this.photoCropView.isReady()) {
            applyCurrentEditMode();
            switchToEditMode(0);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$24$PhotoViewer(View view) {
        this.photoCropView.reset();
    }

    public /* synthetic */ void lambda$setParentActivity$25$PhotoViewer(ImageReceiver imageReceiver, boolean z, boolean z2) {
        PhotoViewerProvider photoViewerProvider;
        Bitmap bitmap;
        if (imageReceiver == this.centerImage && z && !z2 && !((this.currentEditMode != 1 && this.sendPhotoType != 1) || this.photoCropView == null || (bitmap = imageReceiver.getBitmap()) == null)) {
            this.photoCropView.setBitmap(bitmap, imageReceiver.getOrientation(), this.sendPhotoType != 1, true);
        }
        if (imageReceiver == this.centerImage && z && (photoViewerProvider = this.placeProvider) != null && photoViewerProvider.scaleToFill() && !this.ignoreDidSetImage) {
            if (!this.wasLayout) {
                this.dontResetZoomOnFirstLayout = true;
            } else {
                setScaleToFill();
            }
        }
    }

    public /* synthetic */ void lambda$setParentActivity$26$PhotoViewer(View view) {
        if (this.captionEditText.getTag() == null) {
            setPhotoChecked();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$27$PhotoViewer(View view) {
        PhotoViewerProvider photoViewerProvider;
        if (this.captionEditText.getTag() == null && (photoViewerProvider = this.placeProvider) != null && photoViewerProvider.getSelectedPhotosOrder() != null && !this.placeProvider.getSelectedPhotosOrder().isEmpty()) {
            togglePhotosListView(!this.isPhotosListViewVisible, true);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$28$PhotoViewer(View view, int i) {
        this.ignoreDidSetImage = true;
        int indexOf = this.imagesArrLocals.indexOf(view.getTag());
        if (indexOf >= 0) {
            this.currentIndex = -1;
            setImageIndex(indexOf, true);
        }
        this.ignoreDidSetImage = false;
    }

    public /* synthetic */ void lambda$setParentActivity$29$PhotoViewer(View view, int i) {
        Object item = this.mentionsAdapter.getItem(i);
        int resultStartPosition = this.mentionsAdapter.getResultStartPosition();
        int resultLength = this.mentionsAdapter.getResultLength();
        if (item instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) item;
            if (user.username != null) {
                PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
                photoViewerCaptionEnterView.replaceWithText(resultStartPosition, resultLength, "@" + user.username + " ", false);
                return;
            }
            String firstName = UserObject.getFirstName(user);
            SpannableString spannableString = new SpannableString(firstName + " ");
            spannableString.setSpan(new URLSpanUserMentionPhotoViewer("" + user.id, true), 0, spannableString.length(), 33);
            this.captionEditText.replaceWithText(resultStartPosition, resultLength, spannableString, false);
        } else if (item instanceof String) {
            PhotoViewerCaptionEnterView photoViewerCaptionEnterView2 = this.captionEditText;
            photoViewerCaptionEnterView2.replaceWithText(resultStartPosition, resultLength, item + " ", false);
        } else if (item instanceof MediaDataController.KeywordResult) {
            String str = ((MediaDataController.KeywordResult) item).emoji;
            this.captionEditText.addEmojiToRecent(str);
            this.captionEditText.replaceWithText(resultStartPosition, resultLength, str, true);
        }
    }

    public /* synthetic */ boolean lambda$setParentActivity$31$PhotoViewer(View view, int i) {
        if (!(this.mentionsAdapter.getItem(i) instanceof String)) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("ClearSearch", NUM));
        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PhotoViewer.this.lambda$null$30$PhotoViewer(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showAlertDialog(builder);
        return true;
    }

    public /* synthetic */ void lambda$null$30$PhotoViewer(DialogInterface dialogInterface, int i) {
        this.mentionsAdapter.clearRecentHashtags();
    }

    /* access modifiers changed from: private */
    public void sendPressed(boolean z, int i) {
        if (this.captionEditText.getTag() == null) {
            if (this.sendPhotoType == 1) {
                applyCurrentEditMode();
            }
            if (this.placeProvider != null && !this.doneButtonPressed) {
                ChatActivity chatActivity = this.parentChatActivity;
                if (chatActivity != null) {
                    TLRPC.Chat currentChat = chatActivity.getCurrentChat();
                    if (this.parentChatActivity.getCurrentUser() != null || ((ChatObject.isChannel(currentChat) && currentChat.megagroup) || !ChatObject.isChannel(currentChat))) {
                        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                        edit.putBoolean("silent_" + this.parentChatActivity.getDialogId(), !z).commit();
                    }
                }
                this.placeProvider.sendButtonPressed(this.currentIndex, getCurrentVideoEditedInfo(), z, i);
                this.doneButtonPressed = true;
                closePhoto(false, false);
            }
        }
    }

    private boolean checkInlinePermissions() {
        Activity activity = this.parentActivity;
        if (activity == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(activity)) {
            return true;
        }
        new AlertDialog.Builder((Context) this.parentActivity).setTitle(LocaleController.getString("AppName", NUM)).setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", NUM)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PhotoViewer.this.lambda$checkInlinePermissions$32$PhotoViewer(dialogInterface, i);
            }
        }).show();
        return false;
    }

    public /* synthetic */ void lambda$checkInlinePermissions$32$PhotoViewer(DialogInterface dialogInterface, int i) {
        Activity activity = this.parentActivity;
        if (activity != null) {
            try {
                activity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + this.parentActivity.getPackageName())));
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private TextView createCaptionTextView() {
        AnonymousClass24 r0 = new TextView(this.actvityContext) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
            }
        };
        r0.setMovementMethod(new LinkMovementMethodMy());
        r0.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
        r0.setLinkTextColor(-8994063);
        r0.setTextColor(-1);
        r0.setHighlightColor(NUM);
        r0.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        r0.setTextSize(1, 16.0f);
        r0.setVisibility(4);
        r0.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoViewer.this.lambda$createCaptionTextView$33$PhotoViewer(view);
            }
        });
        return r0;
    }

    public /* synthetic */ void lambda$createCaptionTextView$33$PhotoViewer(View view) {
        if (this.needCaptionLayout) {
            openCaptionEnter();
        }
    }

    /* access modifiers changed from: private */
    public int getLeftInset() {
        Object obj = this.lastInsets;
        if (obj == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets) obj).getSystemWindowInsetLeft();
    }

    /* access modifiers changed from: private */
    public int getRightInset() {
        Object obj = this.lastInsets;
        if (obj == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets) obj).getSystemWindowInsetRight();
    }

    /* access modifiers changed from: private */
    public void dismissInternal() {
        try {
            if (this.windowView.getParent() != null) {
                ((LaunchActivity) this.parentActivity).drawerLayoutContainer.setAllowDrawContent(true);
                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void switchToPip() {
        if (this.videoPlayer != null && this.textureUploaded && checkInlinePermissions() && !this.changingTextureView && !this.switchingInlineMode && !this.isInline) {
            if (PipInstance != null) {
                PipInstance.destroyPhotoViewer();
            }
            this.openedFullScreenVideo = false;
            PipInstance = Instance;
            Instance = null;
            this.switchingInlineMode = true;
            this.isVisible = false;
            PlaceProviderObject placeProviderObject = this.currentPlaceObject;
            if (placeProviderObject != null) {
                placeProviderObject.imageReceiver.setVisible(true, true);
                AnimatedFileDrawable animation = this.currentPlaceObject.imageReceiver.getAnimation();
                if (animation != null) {
                    Bitmap animatedBitmap = animation.getAnimatedBitmap();
                    if (animatedBitmap != null) {
                        try {
                            Bitmap bitmap = this.videoTextureView.getBitmap(animatedBitmap.getWidth(), animatedBitmap.getHeight());
                            new Canvas(animatedBitmap).drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                            bitmap.recycle();
                        } catch (Throwable th) {
                            FileLog.e(th);
                        }
                    }
                    animation.seekTo(this.videoPlayer.getCurrentPosition(), true);
                    this.currentPlaceObject.imageReceiver.setAllowStartAnimation(true);
                    this.currentPlaceObject.imageReceiver.startAnimation();
                }
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.pipAnimationInProgress = true;
                org.telegram.ui.Components.Rect pipRect = PipVideoView.getPipRect(this.aspectRatioFrameLayout.getAspectRatio());
                float width = pipRect.width / ((float) this.videoTextureView.getWidth());
                pipRect.y += (float) AndroidUtilities.statusBarHeight;
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.textureImageView, View.SCALE_X, new float[]{width}), ObjectAnimator.ofFloat(this.textureImageView, View.SCALE_Y, new float[]{width}), ObjectAnimator.ofFloat(this.textureImageView, View.TRANSLATION_X, new float[]{pipRect.x}), ObjectAnimator.ofFloat(this.textureImageView, View.TRANSLATION_Y, new float[]{pipRect.y}), ObjectAnimator.ofFloat(this.videoTextureView, View.SCALE_X, new float[]{width}), ObjectAnimator.ofFloat(this.videoTextureView, View.SCALE_Y, new float[]{width}), ObjectAnimator.ofFloat(this.videoTextureView, View.TRANSLATION_X, new float[]{(pipRect.x - this.aspectRatioFrameLayout.getX()) + ((float) getLeftInset())}), ObjectAnimator.ofFloat(this.videoTextureView, View.TRANSLATION_Y, new float[]{pipRect.y - this.aspectRatioFrameLayout.getY()}), ObjectAnimator.ofInt(this.backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0}), ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.captionTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.groupedPhotosListView, View.ALPHA, new float[]{0.0f})});
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(250);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = PhotoViewer.this.pipAnimationInProgress = false;
                        PhotoViewer.this.switchToInlineRunnable.run();
                    }
                });
                animatorSet.start();
                return;
            }
            this.switchToInlineRunnable.run();
            dismissInternal();
        }
    }

    public VideoPlayer getVideoPlayer() {
        return this.videoPlayer;
    }

    public void exitFromPip() {
        if (this.isInline) {
            if (Instance != null) {
                Instance.closePhoto(false, true);
            }
            Instance = PipInstance;
            PipInstance = null;
            this.switchingInlineMode = true;
            Bitmap bitmap = this.currentBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.currentBitmap = null;
            }
            this.changingTextureView = true;
            this.isInline = false;
            this.videoTextureView.setVisibility(4);
            this.aspectRatioFrameLayout.addView(this.videoTextureView);
            if (ApplicationLoader.mainInterfacePaused) {
                try {
                    this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.pipAnimationInProgress = true;
                org.telegram.ui.Components.Rect pipRect = PipVideoView.getPipRect(this.aspectRatioFrameLayout.getAspectRatio());
                float f = pipRect.width / ((float) this.textureImageView.getLayoutParams().width);
                pipRect.y += (float) AndroidUtilities.statusBarHeight;
                this.textureImageView.setScaleX(f);
                this.textureImageView.setScaleY(f);
                this.textureImageView.setTranslationX(pipRect.x);
                this.textureImageView.setTranslationY(pipRect.y);
                this.videoTextureView.setScaleX(f);
                this.videoTextureView.setScaleY(f);
                this.videoTextureView.setTranslationX(pipRect.x - this.aspectRatioFrameLayout.getX());
                this.videoTextureView.setTranslationY(pipRect.y - this.aspectRatioFrameLayout.getY());
            } else {
                this.pipVideoView.close();
                this.pipVideoView = null;
            }
            try {
                this.isVisible = true;
                ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
                if (this.currentPlaceObject != null) {
                    this.currentPlaceObject.imageReceiver.setVisible(false, false);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.waitingForDraw = 4;
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateVideoSeekPreviewPosition() {
        int thumbX = (this.videoPlayerSeekbar.getThumbX() + AndroidUtilities.dp(48.0f)) - (this.videoPreviewFrame.getMeasuredWidth() / 2);
        int dp = AndroidUtilities.dp(10.0f);
        int measuredWidth = (this.videoPlayerControlFrameLayout.getMeasuredWidth() - AndroidUtilities.dp(10.0f)) - (this.videoPreviewFrame.getMeasuredWidth() / 2);
        if (thumbX < dp) {
            thumbX = dp;
        } else if (thumbX >= measuredWidth) {
            thumbX = measuredWidth;
        }
        this.videoPreviewFrame.setTranslationX((float) thumbX);
    }

    /* access modifiers changed from: private */
    public void showVideoSeekPreviewPosition(boolean z) {
        if (z && this.videoPreviewFrame.getTag() != null) {
            return;
        }
        if (!z && this.videoPreviewFrame.getTag() == null) {
            return;
        }
        if (!z || this.videoPreviewFrame.isReady()) {
            AnimatorSet animatorSet = this.videoPreviewFrameAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.videoPreviewFrame.setTag(z ? 1 : null);
            this.videoPreviewFrameAnimation = new AnimatorSet();
            AnimatorSet animatorSet2 = this.videoPreviewFrameAnimation;
            Animator[] animatorArr = new Animator[1];
            VideoSeekPreviewImage videoSeekPreviewImage = this.videoPreviewFrame;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(videoSeekPreviewImage, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.videoPreviewFrameAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = PhotoViewer.this.videoPreviewFrameAnimation = null;
                }
            });
            this.videoPreviewFrameAnimation.setDuration(180);
            this.videoPreviewFrameAnimation.start();
            return;
        }
        this.needShowOnReady = z;
    }

    private void createVideoControlsInterface() {
        this.videoPlayerControlFrameLayout = new FrameLayout(this.containerView.getContext()) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                motionEvent.getX();
                motionEvent.getY();
                if (PhotoViewer.this.videoPlayerSeekbar.onTouch(motionEvent.getAction(), motionEvent.getX() - ((float) AndroidUtilities.dp(48.0f)), motionEvent.getY())) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    invalidate();
                }
                return true;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                long j = 0;
                if (PhotoViewer.this.videoPlayer != null) {
                    long duration = PhotoViewer.this.videoPlayer.getDuration();
                    if (duration != -9223372036854775807L) {
                        j = duration;
                    }
                }
                long j2 = j / 1000;
                Paint paint = PhotoViewer.this.videoPlayerTime.getPaint();
                long j3 = j2 / 60;
                long j4 = j2 % 60;
                Object[] objArr = {Long.valueOf(j3), Long.valueOf(j4), Long.valueOf(j3), Long.valueOf(j4)};
                PhotoViewer.this.videoPlayerSeekbar.setSize((getMeasuredWidth() - AndroidUtilities.dp(64.0f)) - ((int) Math.ceil((double) paint.measureText(String.format("%02d:%02d / %02d:%02d", objArr)))), getMeasuredHeight());
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                float f;
                super.onLayout(z, i, i2, i3, i4);
                if (PhotoViewer.this.videoPlayer != null) {
                    f = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                    if (!PhotoViewer.this.inPreview && PhotoViewer.this.videoTimelineView.getVisibility() == 0) {
                        float leftProgress = f - PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (leftProgress < 0.0f) {
                            leftProgress = 0.0f;
                        }
                        f = leftProgress / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
                        if (f > 1.0f) {
                            f = 1.0f;
                        }
                    }
                } else {
                    f = 0.0f;
                }
                PhotoViewer.this.videoPlayerSeekbar.setProgress(f);
                PhotoViewer.this.videoTimelineView.setProgress(f);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(48.0f), 0.0f);
                PhotoViewer.this.videoPlayerSeekbar.draw(canvas);
                canvas.restore();
            }
        };
        this.videoPlayerControlFrameLayout.setWillNotDraw(false);
        this.bottomLayout.addView(this.videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
        this.videoPlayerSeekbar = new SeekBar(this.videoPlayerControlFrameLayout);
        this.videoPlayerSeekbar.setLineHeight(AndroidUtilities.dp(4.0f));
        this.videoPlayerSeekbar.setColors(NUM, NUM, -2764585, -1, -1);
        this.videoPlayerSeekbar.setDelegate(new SeekBar.SeekBarDelegate() {
            public void onSeekBarDrag(float f) {
                if (PhotoViewer.this.videoPlayer != null) {
                    if (!PhotoViewer.this.inPreview && PhotoViewer.this.videoTimelineView.getVisibility() == 0) {
                        f = PhotoViewer.this.videoTimelineView.getLeftProgress() + ((PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress()) * f);
                    }
                    long duration = PhotoViewer.this.videoPlayer.getDuration();
                    if (duration == -9223372036854775807L) {
                        float unused = PhotoViewer.this.seekToProgressPending = f;
                    } else {
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (f * ((float) duration))));
                    }
                    PhotoViewer.this.showVideoSeekPreviewPosition(false);
                    boolean unused2 = PhotoViewer.this.needShowOnReady = false;
                }
            }

            public void onSeekBarContinuousDrag(float f) {
                if (!(PhotoViewer.this.videoPlayer == null || PhotoViewer.this.videoPreviewFrame == null)) {
                    PhotoViewer.this.videoPreviewFrame.setProgress(f, PhotoViewer.this.videoPlayerSeekbar.getWidth());
                }
                PhotoViewer.this.showVideoSeekPreviewPosition(true);
                PhotoViewer.this.updateVideoSeekPreviewPosition();
            }
        });
        this.videoPreviewFrame = new VideoSeekPreviewImage(this.containerView.getContext(), new VideoSeekPreviewImage.VideoSeekPreviewImageDelegate() {
            public final void onReady() {
                PhotoViewer.this.lambda$createVideoControlsInterface$34$PhotoViewer();
            }
        }) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                PhotoViewer.this.updateVideoSeekPreviewPosition();
            }

            public void setVisibility(int i) {
                super.setVisibility(i);
                if (i == 0) {
                    PhotoViewer.this.updateVideoSeekPreviewPosition();
                }
            }
        };
        this.videoPreviewFrame.setAlpha(0.0f);
        this.containerView.addView(this.videoPreviewFrame, LayoutHelper.createFrame(-2, -2.0f, 83, 0.0f, 0.0f, 0.0f, 58.0f));
        this.videoPlayButton = new ImageView(this.containerView.getContext());
        this.videoPlayButton.setScaleType(ImageView.ScaleType.CENTER);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48, 48.0f, 51, 4.0f, 0.0f, 0.0f, 0.0f));
        this.videoPlayButton.setFocusable(true);
        this.videoPlayButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
        this.videoPlayButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoViewer.this.lambda$createVideoControlsInterface$35$PhotoViewer(view);
            }
        });
        this.videoPlayerTime = new SimpleTextView(this.containerView.getContext());
        this.videoPlayerTime.setTextColor(-1);
        this.videoPlayerTime.setGravity(53);
        this.videoPlayerTime.setTextSize(13);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -1.0f, 53, 0.0f, 17.0f, 7.0f, 0.0f));
    }

    public /* synthetic */ void lambda$createVideoControlsInterface$34$PhotoViewer() {
        if (this.needShowOnReady) {
            showVideoSeekPreviewPosition(true);
        }
    }

    public /* synthetic */ void lambda$createVideoControlsInterface$35$PhotoViewer(View view) {
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            if (this.isPlaying) {
                videoPlayer2.pause();
            } else {
                if (this.isCurrentVideo) {
                    if (Math.abs(this.videoTimelineView.getProgress() - 1.0f) < 0.01f || this.videoPlayer.getCurrentPosition() == this.videoPlayer.getDuration()) {
                        this.videoPlayer.seekTo((long) ((int) (this.videoTimelineView.getLeftProgress() * ((float) this.videoPlayer.getDuration()))));
                    }
                } else if (Math.abs(this.videoPlayerSeekbar.getProgress() - 1.0f) < 0.01f || this.videoPlayer.getCurrentPosition() == this.videoPlayer.getDuration()) {
                    this.videoPlayer.seekTo(0);
                }
                this.videoPlayer.play();
            }
            this.containerView.invalidate();
        }
    }

    private void openCaptionEnter() {
        int i;
        String str;
        int i2;
        if (this.imageMoveAnimation == null && this.changeModeAnimation == null && this.currentEditMode == 0 && (i = this.sendPhotoType) != 1 && i != 3 && i != 10) {
            this.selectedPhotosListView.setVisibility(8);
            this.selectedPhotosListView.setEnabled(false);
            this.selectedPhotosListView.setAlpha(0.0f);
            this.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
            this.photosCounterView.setRotationX(0.0f);
            this.isPhotosListViewVisible = false;
            this.captionEditText.setTag(1);
            this.captionEditText.openKeyboard();
            this.captionEditText.setImportantForAccessibility(0);
            this.lastTitle = this.actionBar.getTitle();
            if (this.isCurrentVideo) {
                ActionBar actionBar2 = this.actionBar;
                if (this.muteVideo) {
                    i2 = NUM;
                    str = "GifCaption";
                } else {
                    i2 = NUM;
                    str = "VideoCaption";
                }
                actionBar2.setTitle(LocaleController.getString(str, i2));
                this.actionBar.setSubtitle((CharSequence) null);
                return;
            }
            this.actionBar.setTitle(LocaleController.getString("PhotoCaption", NUM));
        }
    }

    /* access modifiers changed from: private */
    public VideoEditedInfo getCurrentVideoEditedInfo() {
        if (!this.isCurrentVideo || this.currentPlayingVideoFile == null || this.compressionsCount == 0) {
            return null;
        }
        VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
        videoEditedInfo.startTime = this.startTime;
        videoEditedInfo.endTime = this.endTime;
        videoEditedInfo.start = this.videoCutStart;
        videoEditedInfo.end = this.videoCutEnd;
        videoEditedInfo.rotationValue = this.rotationValue;
        videoEditedInfo.originalWidth = this.originalWidth;
        videoEditedInfo.originalHeight = this.originalHeight;
        videoEditedInfo.bitrate = this.bitrate;
        videoEditedInfo.originalPath = this.currentPlayingVideoFile.getPath();
        int i = this.estimatedSize;
        videoEditedInfo.estimatedSize = i != 0 ? (long) i : 1;
        videoEditedInfo.estimatedDuration = this.estimatedDuration;
        videoEditedInfo.framerate = this.videoFramerate;
        videoEditedInfo.originalDuration = (long) (this.videoDuration * 1000.0f);
        int i2 = -1;
        if (this.muteVideo || !(this.compressItem.getTag() == null || (videoEditedInfo.resultWidth == this.originalWidth && videoEditedInfo.resultHeight == this.originalHeight))) {
            if (this.muteVideo) {
                this.selectedCompression = 1;
                updateWidthHeightBitrateForCompression();
            }
            videoEditedInfo.resultWidth = this.resultWidth;
            videoEditedInfo.resultHeight = this.resultHeight;
            if (!this.muteVideo) {
                i2 = this.bitrate;
            }
            videoEditedInfo.bitrate = i2;
            videoEditedInfo.muted = this.muteVideo;
        } else {
            videoEditedInfo.resultWidth = this.originalWidth;
            videoEditedInfo.resultHeight = this.originalHeight;
            if (!this.muteVideo) {
                i2 = this.originalBitrate;
            }
            videoEditedInfo.bitrate = i2;
            videoEditedInfo.muted = this.muteVideo;
        }
        return videoEditedInfo;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r0.currentEncryptedChat;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean supportsSendingNewEntities() {
        /*
            r2 = this;
            org.telegram.ui.ChatActivity r0 = r2.parentChatActivity
            if (r0 == 0) goto L_0x0014
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.currentEncryptedChat
            if (r0 == 0) goto L_0x0012
            int r0 = r0.layer
            int r0 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r0)
            r1 = 101(0x65, float:1.42E-43)
            if (r0 < r1) goto L_0x0014
        L_0x0012:
            r0 = 1
            goto L_0x0015
        L_0x0014:
            r0 = 0
        L_0x0015:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.supportsSendingNewEntities():boolean");
    }

    /* access modifiers changed from: private */
    public void closeCaptionEnter(boolean z) {
        int i = this.currentIndex;
        if (i >= 0 && i < this.imagesArrLocals.size()) {
            Object obj = this.imagesArrLocals.get(this.currentIndex);
            String str = null;
            if (z) {
                CharSequence[] charSequenceArr = {this.captionEditText.getFieldCharSequence()};
                ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities());
                if (obj instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) obj;
                    photoEntry.caption = charSequenceArr[0];
                    photoEntry.entities = entities;
                } else if (obj instanceof MediaController.SearchImage) {
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                    searchImage.caption = charSequenceArr[0];
                    searchImage.entities = entities;
                }
                if (this.captionEditText.getFieldCharSequence().length() != 0 && !this.placeProvider.isPhotoChecked(this.currentIndex)) {
                    setPhotoChecked();
                }
                setCurrentCaption((MessageObject) null, charSequenceArr[0], false);
            }
            this.captionEditText.setTag((Object) null);
            String str2 = this.lastTitle;
            if (str2 != null) {
                this.actionBar.setTitle(str2);
                this.lastTitle = null;
            }
            if (this.isCurrentVideo) {
                ActionBar actionBar2 = this.actionBar;
                if (!this.muteVideo) {
                    str = this.currentSubtitle;
                }
                actionBar2.setSubtitle(str);
            }
            updateCaptionTextForCurrentPhoto(obj);
            if (this.captionEditText.isPopupShowing()) {
                this.captionEditText.hidePopup();
            }
            this.captionEditText.closeKeyboard();
            if (Build.VERSION.SDK_INT >= 19) {
                this.captionEditText.setImportantForAccessibility(4);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateVideoPlayerTime() {
        String str;
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 == null) {
            str = String.format("%02d:%02d / %02d:%02d", new Object[]{0, 0, 0, 0});
        } else {
            long currentPosition = videoPlayer2.getCurrentPosition();
            long j = 0;
            if (currentPosition < 0) {
                currentPosition = 0;
            }
            long duration = this.videoPlayer.getDuration();
            if (duration >= 0) {
                j = duration;
            }
            if (j == -9223372036854775807L || currentPosition == -9223372036854775807L) {
                str = String.format("%02d:%02d / %02d:%02d", new Object[]{0, 0, 0, 0});
            } else {
                if (!this.inPreview && this.videoTimelineView.getVisibility() == 0) {
                    j = (long) (((float) j) * (this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()));
                    currentPosition = (long) (((float) currentPosition) - (this.videoTimelineView.getLeftProgress() * ((float) j)));
                    if (currentPosition > j) {
                        currentPosition = j;
                    }
                }
                long j2 = currentPosition / 1000;
                long j3 = j / 1000;
                str = String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(j2 / 60), Long.valueOf(j2 % 60), Long.valueOf(j3 / 60), Long.valueOf(j3 % 60)});
            }
        }
        this.videoPlayerTime.setText(str);
    }

    private void checkBufferedProgress(float f) {
        MessageObject messageObject;
        TLRPC.Document document;
        if (this.isStreaming && this.parentActivity != null && !this.streamingAlertShown && this.videoPlayer != null && (messageObject = this.currentMessageObject) != null && (document = messageObject.getDocument()) != null && this.currentMessageObject.getDuration() >= 20 && f < 0.9f) {
            int i = document.size;
            if ((((float) i) * f >= 5242880.0f || (f >= 0.5f && i >= 2097152)) && Math.abs(SystemClock.elapsedRealtime() - this.startedPlayTime) >= 2000) {
                if (this.videoPlayer.getDuration() == -9223372036854775807L) {
                    Toast.makeText(this.parentActivity, LocaleController.getString("VideoDoesNotSupportStreaming", NUM), 1).show();
                }
                this.streamingAlertShown = true;
            }
        }
    }

    public void updateColors() {
        String str;
        int color = Theme.getColor("dialogFloatingButton");
        ImageView imageView = this.pickerViewSendButton;
        if (imageView != null) {
            Drawable background = imageView.getBackground();
            Theme.setSelectorDrawableColor(background, color, false);
            if (Build.VERSION.SDK_INT >= 21) {
                str = "dialogFloatingButtonPressed";
            } else {
                str = "dialogFloatingButton";
            }
            Theme.setSelectorDrawableColor(background, Theme.getColor(str), true);
            this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
        }
        CheckBox checkBox = this.checkImageView;
        if (checkBox != null) {
            checkBox.setColor(Theme.getColor("dialogFloatingButton"), -1);
        }
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
        ImageView imageView2 = this.timeItem;
        if (!(imageView2 == null || imageView2.getColorFilter() == null)) {
            this.timeItem.setColorFilter(porterDuffColorFilter);
        }
        ImageView imageView3 = this.paintItem;
        if (!(imageView3 == null || imageView3.getColorFilter() == null)) {
            this.paintItem.setColorFilter(porterDuffColorFilter);
        }
        ImageView imageView4 = this.cropItem;
        if (!(imageView4 == null || imageView4.getColorFilter() == null)) {
            this.cropItem.setColorFilter(porterDuffColorFilter);
        }
        ImageView imageView5 = this.tuneItem;
        if (!(imageView5 == null || imageView5.getColorFilter() == null)) {
            this.tuneItem.setColorFilter(porterDuffColorFilter);
        }
        ImageView imageView6 = this.muteItem;
        if (!(imageView6 == null || imageView6.getColorFilter() == null)) {
            this.muteItem.setColorFilter(porterDuffColorFilter);
        }
        PickerBottomLayoutViewer pickerBottomLayoutViewer = this.editorDoneLayout;
        if (pickerBottomLayoutViewer != null) {
            pickerBottomLayoutViewer.doneButton.setTextColor(color);
        }
        PickerBottomLayoutViewer pickerBottomLayoutViewer2 = this.qualityPicker;
        if (pickerBottomLayoutViewer2 != null) {
            pickerBottomLayoutViewer2.doneButton.setTextColor(color);
        }
        PhotoPaintView photoPaintView2 = this.photoPaintView;
        if (photoPaintView2 != null) {
            photoPaintView2.updateColors();
        }
        PhotoFilterView photoFilterView2 = this.photoFilterView;
        if (photoFilterView2 != null) {
            photoFilterView2.updateColors();
        }
        PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
        if (photoViewerCaptionEnterView != null) {
            photoViewerCaptionEnterView.updateColors();
        }
        RecyclerListView recyclerListView = this.selectedPhotosListView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.selectedPhotosListView.getChildAt(i);
                if (childAt instanceof PhotoPickerPhotoCell) {
                    ((PhotoPickerPhotoCell) childAt).updateColors();
                }
            }
        }
    }

    public void injectVideoPlayer(VideoPlayer videoPlayer2) {
        this.injectingVideoPlayer = videoPlayer2;
    }

    public void injectVideoPlayerSurface(SurfaceTexture surfaceTexture) {
        this.injectingVideoPlayerSurface = surfaceTexture;
    }

    public boolean isInjectingVideoPlayer() {
        return this.injectingVideoPlayer != null;
    }

    /* access modifiers changed from: private */
    public void updatePlayerState(boolean z, int i) {
        MessageObject messageObject;
        if (this.videoPlayer != null) {
            if (this.isStreaming) {
                if (i != 2 || !this.skipFirstBufferingProgress) {
                    toggleMiniProgress(this.seekToProgressPending != 0.0f || i == 2, true);
                } else if (z) {
                    this.skipFirstBufferingProgress = false;
                }
            }
            if (!z || i == 4 || i == 1) {
                try {
                    this.parentActivity.getWindow().clearFlags(128);
                    this.keepScreenOnFlagSet = false;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                try {
                    this.parentActivity.getWindow().addFlags(128);
                    this.keepScreenOnFlagSet = true;
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            if (i == 3 || i == 1) {
                if (this.currentMessageObject != null) {
                    this.videoPreviewFrame.open(this.videoPlayer.getCurrentUri());
                }
                if (this.seekToProgressPending != 0.0f) {
                    this.videoPlayer.seekTo((long) ((int) (((float) this.videoPlayer.getDuration()) * this.seekToProgressPending)));
                    this.seekToProgressPending = 0.0f;
                    MessageObject messageObject2 = this.currentMessageObject;
                    if (messageObject2 != null && !FileLoader.getInstance(messageObject2.currentAccount).isLoadingVideoAny(this.currentMessageObject.getDocument())) {
                        this.skipFirstBufferingProgress = true;
                    }
                }
            }
            if (i == 3) {
                if (this.aspectRatioFrameLayout.getVisibility() != 0) {
                    this.aspectRatioFrameLayout.setVisibility(0);
                }
                if (!this.pipItem.isEnabled()) {
                    this.pipAvailable = true;
                    this.pipItem.setEnabled(true);
                    this.pipItem.setAlpha(1.0f);
                }
                this.playerWasReady = true;
                MessageObject messageObject3 = this.currentMessageObject;
                if (messageObject3 != null && messageObject3.isVideo()) {
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(this.currentMessageObject.currentAccount).removeLoadingVideo(this.currentMessageObject.getDocument(), true, false);
                }
            } else if (i == 2 && z && (messageObject = this.currentMessageObject) != null && messageObject.isVideo()) {
                if (this.playerWasReady) {
                    this.setLoadingRunnable.run();
                } else {
                    AndroidUtilities.runOnUIThread(this.setLoadingRunnable, 1000);
                }
            }
            if (!this.videoPlayer.isPlaying() || i == 4) {
                if (this.isPlaying) {
                    this.isPlaying = false;
                    this.videoPlayButton.setImageResource(NUM);
                    AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
                    if (i == 4) {
                        if (!this.isCurrentVideo) {
                            if (!this.isActionBarVisible) {
                                toggleActionBar(true, true);
                            }
                            this.videoPlayerSeekbar.setProgress(0.0f);
                            this.videoPlayerControlFrameLayout.invalidate();
                            if (this.inPreview || this.videoTimelineView.getVisibility() != 0) {
                                this.videoPlayer.seekTo(0);
                            } else {
                                this.videoPlayer.seekTo((long) ((int) (this.videoTimelineView.getLeftProgress() * ((float) this.videoPlayer.getDuration()))));
                            }
                            this.videoPlayer.pause();
                        } else if (!this.videoTimelineView.isDragging()) {
                            this.videoTimelineView.setProgress(0.0f);
                            if (this.inPreview || this.videoTimelineView.getVisibility() != 0) {
                                this.videoPlayer.seekTo(0);
                            } else {
                                this.videoPlayer.seekTo((long) ((int) (this.videoTimelineView.getLeftProgress() * ((float) this.videoPlayer.getDuration()))));
                            }
                            this.videoPlayer.pause();
                            this.containerView.invalidate();
                        }
                        PipVideoView pipVideoView2 = this.pipVideoView;
                        if (pipVideoView2 != null) {
                            pipVideoView2.onVideoCompleted();
                        }
                    }
                }
            } else if (!this.isPlaying) {
                this.isPlaying = true;
                this.videoPlayButton.setImageResource(NUM);
                AndroidUtilities.runOnUIThread(this.updateProgressRunnable);
            }
            PipVideoView pipVideoView3 = this.pipVideoView;
            if (pipVideoView3 != null) {
                pipVideoView3.updatePlayButton();
            }
            updateVideoPlayerTime();
        }
    }

    private void preparePlayer(Uri uri, boolean z, boolean z2) {
        if (!z2) {
            this.currentPlayingVideoFile = uri;
        }
        if (this.parentActivity != null) {
            int i = 0;
            this.streamingAlertShown = false;
            this.startedPlayTime = SystemClock.elapsedRealtime();
            this.currentVideoFinishedLoading = false;
            this.lastBufferedPositionCheck = 0;
            boolean z3 = true;
            this.firstAnimationDelay = true;
            this.inPreview = z2;
            releasePlayer(false);
            if (this.videoTextureView == null) {
                this.aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, i2);
                        if (PhotoViewer.this.textureImageView != null) {
                            ViewGroup.LayoutParams layoutParams = PhotoViewer.this.textureImageView.getLayoutParams();
                            layoutParams.width = getMeasuredWidth();
                            layoutParams.height = getMeasuredHeight();
                        }
                    }
                };
                this.aspectRatioFrameLayout.setVisibility(4);
                this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                this.videoTextureView = new TextureView(this.parentActivity);
                SurfaceTexture surfaceTexture = this.injectingVideoPlayerSurface;
                if (surfaceTexture != null) {
                    this.videoTextureView.setSurfaceTexture(surfaceTexture);
                    this.textureUploaded = true;
                    this.injectingVideoPlayerSurface = null;
                }
                this.videoTextureView.setPivotX(0.0f);
                this.videoTextureView.setPivotY(0.0f);
                this.videoTextureView.setOpaque(false);
                this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
            }
            if (Build.VERSION.SDK_INT >= 21 && this.textureImageView == null) {
                this.textureImageView = new ImageView(this.parentActivity);
                this.textureImageView.setBackgroundColor(-65536);
                this.textureImageView.setPivotX(0.0f);
                this.textureImageView.setPivotY(0.0f);
                this.textureImageView.setVisibility(4);
                this.containerView.addView(this.textureImageView);
            }
            this.textureUploaded = false;
            this.videoCrossfadeStarted = false;
            TextureView textureView = this.videoTextureView;
            this.videoCrossfadeAlpha = 0.0f;
            textureView.setAlpha(0.0f);
            this.videoPlayButton.setImageResource(NUM);
            this.playerWasReady = false;
            if (this.videoPlayer == null) {
                VideoPlayer videoPlayer2 = this.injectingVideoPlayer;
                if (videoPlayer2 != null) {
                    this.videoPlayer = videoPlayer2;
                    this.injectingVideoPlayer = null;
                    this.playerInjected = true;
                    updatePlayerState(this.videoPlayer.getPlayWhenReady(), this.videoPlayer.getPlaybackState());
                    z3 = false;
                } else {
                    this.videoPlayer = new VideoPlayer();
                }
                this.videoPlayer.setTextureView(this.videoTextureView);
                this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                    public void onStateChanged(boolean z, int i) {
                        PhotoViewer.this.updatePlayerState(z, i);
                    }

                    public void onError(Exception exc) {
                        if (PhotoViewer.this.videoPlayer != null) {
                            FileLog.e((Throwable) exc);
                            if (PhotoViewer.this.menuItem.isSubItemVisible(11)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) PhotoViewer.this.parentActivity);
                                builder.setTitle(LocaleController.getString("AppName", NUM));
                                builder.setMessage(LocaleController.getString("CantPlayVideo", NUM));
                                builder.setPositiveButton(LocaleController.getString("Open", NUM), new DialogInterface.OnClickListener() {
                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        PhotoViewer.AnonymousClass31.this.lambda$onError$0$PhotoViewer$31(dialogInterface, i);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                                PhotoViewer.this.showAlertDialog(builder);
                            }
                        }
                    }

                    public /* synthetic */ void lambda$onError$0$PhotoViewer$31(DialogInterface dialogInterface, int i) {
                        try {
                            AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity);
                            PhotoViewer.this.closePhoto(false, false);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }

                    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                        if (PhotoViewer.this.aspectRatioFrameLayout != null) {
                            if (!(i3 == 90 || i3 == 270)) {
                                int i4 = i2;
                                i2 = i;
                                i = i4;
                            }
                            PhotoViewer.this.aspectRatioFrameLayout.setAspectRatio(i == 0 ? 1.0f : (((float) i2) * f) / ((float) i), i3);
                        }
                    }

                    public void onRenderedFirstFrame() {
                        if (!PhotoViewer.this.textureUploaded) {
                            boolean unused = PhotoViewer.this.textureUploaded = true;
                            PhotoViewer.this.containerView.invalidate();
                        }
                    }

                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        if (PhotoViewer.this.changingTextureView) {
                            boolean unused = PhotoViewer.this.changingTextureView = false;
                            if (PhotoViewer.this.isInline) {
                                if (PhotoViewer.this.isInline) {
                                    int unused2 = PhotoViewer.this.waitingForFirstTextureUpload = 1;
                                }
                                PhotoViewer.this.changedTextureView.setSurfaceTexture(surfaceTexture);
                                PhotoViewer.this.changedTextureView.setSurfaceTextureListener(PhotoViewer.this.surfaceTextureListener);
                                PhotoViewer.this.changedTextureView.setVisibility(0);
                                return true;
                            }
                        }
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                        if (PhotoViewer.this.waitingForFirstTextureUpload == 2) {
                            if (PhotoViewer.this.textureImageView != null) {
                                PhotoViewer.this.textureImageView.setVisibility(4);
                                PhotoViewer.this.textureImageView.setImageDrawable((Drawable) null);
                                if (PhotoViewer.this.currentBitmap != null) {
                                    PhotoViewer.this.currentBitmap.recycle();
                                    Bitmap unused = PhotoViewer.this.currentBitmap = null;
                                }
                            }
                            boolean unused2 = PhotoViewer.this.switchingInlineMode = false;
                            if (Build.VERSION.SDK_INT >= 21) {
                                PhotoViewer.this.aspectRatioFrameLayout.getLocationInWindow(PhotoViewer.this.pipPosition);
                                int[] access$12300 = PhotoViewer.this.pipPosition;
                                access$12300[1] = (int) (((float) access$12300[1]) - PhotoViewer.this.containerView.getTranslationY());
                                PhotoViewer.this.textureImageView.setTranslationX(PhotoViewer.this.textureImageView.getTranslationX() + ((float) PhotoViewer.this.getLeftInset()));
                                PhotoViewer.this.videoTextureView.setTranslationX((PhotoViewer.this.videoTextureView.getTranslationX() + ((float) PhotoViewer.this.getLeftInset())) - PhotoViewer.this.aspectRatioFrameLayout.getX());
                                AnimatorSet animatorSet = new AnimatorSet();
                                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.TRANSLATION_X, new float[]{(float) PhotoViewer.this.pipPosition[0]}), ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.TRANSLATION_Y, new float[]{(float) PhotoViewer.this.pipPosition[1]}), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.TRANSLATION_X, new float[]{((float) PhotoViewer.this.pipPosition[0]) - PhotoViewer.this.aspectRatioFrameLayout.getX()}), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.TRANSLATION_Y, new float[]{((float) PhotoViewer.this.pipPosition[1]) - PhotoViewer.this.aspectRatioFrameLayout.getY()}), ObjectAnimator.ofInt(PhotoViewer.this.backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{255}), ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.captionTextView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.groupedPhotosListView, View.ALPHA, new float[]{1.0f})});
                                animatorSet.setInterpolator(new DecelerateInterpolator());
                                animatorSet.setDuration(250);
                                animatorSet.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        boolean unused = PhotoViewer.this.pipAnimationInProgress = false;
                                    }
                                });
                                animatorSet.start();
                            }
                            int unused3 = PhotoViewer.this.waitingForFirstTextureUpload = 0;
                        }
                    }
                });
            } else {
                z3 = false;
            }
            this.shouldSavePositionForCurrentVideo = null;
            this.lastSaveTime = 0;
            if (z3) {
                this.seekToProgressPending = this.seekToProgressPending2;
                this.videoPlayer.preparePlayer(uri, "other");
                this.videoPlayerSeekbar.setProgress(0.0f);
                this.videoTimelineView.setProgress(0.0f);
                this.videoPlayerSeekbar.setBufferedProgress(0.0f);
                this.videoPlayer.setPlayWhenReady(z);
                MessageObject messageObject = this.currentMessageObject;
                if (messageObject != null) {
                    String fileName = messageObject.getFileName();
                    if (!TextUtils.isEmpty(fileName) && this.currentMessageObject.getDuration() >= 1200) {
                        if (this.currentMessageObject.forceSeekTo < 0.0f) {
                            float f = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).getFloat(fileName, -1.0f);
                            if (f > 0.0f && f < 0.999f) {
                                this.currentMessageObject.forceSeekTo = f;
                                this.videoPlayerSeekbar.setProgress(f);
                            }
                        }
                        this.shouldSavePositionForCurrentVideo = fileName;
                    }
                }
            }
            MessageObject messageObject2 = this.currentMessageObject;
            if (messageObject2 != null) {
                float f2 = messageObject2.forceSeekTo;
                if (f2 >= 0.0f) {
                    this.seekToProgressPending = f2;
                    messageObject2.forceSeekTo = -1.0f;
                }
            }
            TLRPC.BotInlineResult botInlineResult = this.currentBotInlineResult;
            if (botInlineResult == null || (!botInlineResult.type.equals("video") && !MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                this.bottomLayout.setPadding(0, 0, 0, 0);
            } else {
                this.bottomLayout.setVisibility(0);
                this.bottomLayout.setPadding(0, 0, AndroidUtilities.dp(84.0f), 0);
                this.pickerView.setVisibility(8);
            }
            FrameLayout frameLayout = this.videoPlayerControlFrameLayout;
            if (this.isCurrentVideo) {
                i = 8;
            }
            frameLayout.setVisibility(i);
            this.dateTextView.setVisibility(8);
            this.nameTextView.setVisibility(8);
            if (this.allowShare) {
                this.shareButton.setVisibility(8);
                this.menuItem.showSubItem(10);
            }
            this.inPreview = z2;
            updateAccessibilityOverlayVisibility();
        }
    }

    /* access modifiers changed from: private */
    public void releasePlayer(boolean z) {
        if (this.videoPlayer != null) {
            AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
            this.videoPlayer.releasePlayer(true);
            this.videoPlayer = null;
            updateAccessibilityOverlayVisibility();
        }
        this.videoPreviewFrame.close();
        toggleMiniProgress(false, false);
        this.pipAvailable = false;
        this.playerInjected = false;
        if (this.pipItem.isEnabled()) {
            this.pipItem.setEnabled(false);
            this.pipItem.setAlpha(0.5f);
        }
        if (this.keepScreenOnFlagSet) {
            try {
                this.parentActivity.getWindow().clearFlags(128);
                this.keepScreenOnFlagSet = false;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        AspectRatioFrameLayout aspectRatioFrameLayout2 = this.aspectRatioFrameLayout;
        if (aspectRatioFrameLayout2 != null) {
            try {
                this.containerView.removeView(aspectRatioFrameLayout2);
            } catch (Throwable unused) {
            }
            this.aspectRatioFrameLayout = null;
        }
        if (this.videoTextureView != null) {
            this.videoTextureView = null;
        }
        if (this.isPlaying) {
            this.isPlaying = false;
            if (!z) {
                this.videoPlayButton.setImageResource(NUM);
            }
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
        }
        if (!z && !this.inPreview && !this.requestingPreview) {
            this.videoPlayerControlFrameLayout.setVisibility(8);
            this.dateTextView.setVisibility(0);
            this.nameTextView.setVisibility(0);
            if (this.allowShare) {
                this.shareButton.setVisibility(0);
                this.menuItem.hideSubItem(10);
            }
        }
    }

    private void updateCaptionTextForCurrentPhoto(Object obj) {
        CharSequence charSequence;
        if (obj instanceof MediaController.PhotoEntry) {
            charSequence = ((MediaController.PhotoEntry) obj).caption;
        } else {
            charSequence = (!(obj instanceof TLRPC.BotInlineResult) && (obj instanceof MediaController.SearchImage)) ? ((MediaController.SearchImage) obj).caption : null;
        }
        if (TextUtils.isEmpty(charSequence)) {
            this.captionEditText.setFieldText("");
        } else {
            this.captionEditText.setFieldText(charSequence);
        }
        this.captionEditText.setAllowTextEntitiesIntersection(supportsSendingNewEntities());
    }

    public void showAlertDialog(AlertDialog.Builder builder) {
        if (this.parentActivity != null) {
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                this.visibleDialog = builder.show();
                this.visibleDialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        PhotoViewer.this.lambda$showAlertDialog$36$PhotoViewer(dialogInterface);
                    }
                });
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    public /* synthetic */ void lambda$showAlertDialog$36$PhotoViewer(DialogInterface dialogInterface) {
        this.visibleDialog = null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:73:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyCurrentEditMode() {
        /*
            r19 = this;
            r0 = r19
            int r1 = r0.currentEditMode
            r2 = 3
            r4 = 2
            r5 = 0
            r6 = 1
            if (r1 == r6) goto L_0x003b
            if (r1 != 0) goto L_0x0011
            int r1 = r0.sendPhotoType
            if (r1 != r6) goto L_0x0011
            goto L_0x003b
        L_0x0011:
            int r1 = r0.currentEditMode
            if (r1 != r4) goto L_0x0024
            org.telegram.ui.Components.PhotoFilterView r1 = r0.photoFilterView
            android.graphics.Bitmap r1 = r1.getBitmap()
            org.telegram.ui.Components.PhotoFilterView r7 = r0.photoFilterView
            org.telegram.messenger.MediaController$SavedFilterState r7 = r7.getSavedFilterState()
            r14 = r5
            r15 = r7
            goto L_0x0038
        L_0x0024:
            if (r1 != r2) goto L_0x0035
            org.telegram.ui.Components.PhotoPaintView r1 = r0.photoPaintView
            android.graphics.Bitmap r1 = r1.getBitmap()
            org.telegram.ui.Components.PhotoPaintView r7 = r0.photoPaintView
            java.util.ArrayList r7 = r7.getMasks()
            r15 = r5
            r14 = r7
            goto L_0x0043
        L_0x0035:
            r1 = r5
            r14 = r1
            r15 = r14
        L_0x0038:
            r16 = 0
            goto L_0x0045
        L_0x003b:
            org.telegram.ui.Components.PhotoCropView r1 = r0.photoCropView
            android.graphics.Bitmap r1 = r1.getBitmap()
            r14 = r5
            r15 = r14
        L_0x0043:
            r16 = 1
        L_0x0045:
            if (r1 == 0) goto L_0x0213
            int r7 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r8 = (float) r7
            int r7 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r9 = (float) r7
            r10 = 80
            r11 = 0
            r12 = 101(0x65, float:1.42E-43)
            r13 = 101(0x65, float:1.42E-43)
            r7 = r1
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r7, (float) r8, (float) r9, (int) r10, (boolean) r11, (int) r12, (int) r13)
            if (r7 == 0) goto L_0x0213
            java.util.ArrayList<java.lang.Object> r8 = r0.imagesArrLocals
            int r9 = r0.currentIndex
            java.lang.Object r8 = r8.get(r9)
            boolean r9 = r8 instanceof org.telegram.messenger.MediaController.PhotoEntry
            r10 = 1123024896(0x42var_, float:120.0)
            java.lang.String r17 = "dialogFloatingButton"
            if (r9 == 0) goto L_0x00f4
            r13 = r8
            org.telegram.messenger.MediaController$PhotoEntry r13 = (org.telegram.messenger.MediaController.PhotoEntry) r13
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r6)
            java.lang.String r7 = r7.toString()
            r13.imagePath = r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r8 = (float) r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r9 = (float) r7
            r10 = 70
            r11 = 0
            r12 = 101(0x65, float:1.42E-43)
            r18 = 101(0x65, float:1.42E-43)
            r7 = r1
            r3 = r13
            r13 = r18
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r7, (float) r8, (float) r9, (int) r10, (boolean) r11, (int) r12, (int) r13)
            if (r7 == 0) goto L_0x00a1
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r6)
            java.lang.String r7 = r7.toString()
            r3.thumbPath = r7
        L_0x00a1:
            if (r14 == 0) goto L_0x00a8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r7 = r3.stickers
            r7.addAll(r14)
        L_0x00a8:
            int r7 = r0.currentEditMode
            if (r7 != r6) goto L_0x00bf
            android.widget.ImageView r2 = r0.cropItem
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r2.setColorFilter(r7)
            r3.isCropped = r6
            goto L_0x00e8
        L_0x00bf:
            if (r7 != r4) goto L_0x00d4
            android.widget.ImageView r2 = r0.tuneItem
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r2.setColorFilter(r7)
            r3.isFiltered = r6
            goto L_0x00e8
        L_0x00d4:
            if (r7 != r2) goto L_0x00e8
            android.widget.ImageView r2 = r0.paintItem
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r2.setColorFilter(r7)
            r3.isPainted = r6
        L_0x00e8:
            if (r15 == 0) goto L_0x00ee
            r3.savedFilterState = r15
            goto L_0x0177
        L_0x00ee:
            if (r16 == 0) goto L_0x0177
            r3.savedFilterState = r5
            goto L_0x0177
        L_0x00f4:
            boolean r3 = r8 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r3 == 0) goto L_0x0177
            r3 = r8
            org.telegram.messenger.MediaController$SearchImage r3 = (org.telegram.messenger.MediaController.SearchImage) r3
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r6)
            java.lang.String r7 = r7.toString()
            r3.imagePath = r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r8 = (float) r7
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r9 = (float) r7
            r10 = 70
            r11 = 0
            r12 = 101(0x65, float:1.42E-43)
            r13 = 101(0x65, float:1.42E-43)
            r7 = r1
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r7, (float) r8, (float) r9, (int) r10, (boolean) r11, (int) r12, (int) r13)
            if (r7 == 0) goto L_0x0127
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r6)
            java.lang.String r7 = r7.toString()
            r3.thumbPath = r7
        L_0x0127:
            if (r14 == 0) goto L_0x012e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r7 = r3.stickers
            r7.addAll(r14)
        L_0x012e:
            int r7 = r0.currentEditMode
            if (r7 != r6) goto L_0x0145
            android.widget.ImageView r2 = r0.cropItem
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r2.setColorFilter(r7)
            r3.isCropped = r6
            goto L_0x016e
        L_0x0145:
            if (r7 != r4) goto L_0x015a
            android.widget.ImageView r2 = r0.tuneItem
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r2.setColorFilter(r7)
            r3.isFiltered = r6
            goto L_0x016e
        L_0x015a:
            if (r7 != r2) goto L_0x016e
            android.widget.ImageView r2 = r0.paintItem
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r17)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r2.setColorFilter(r7)
            r3.isPainted = r6
        L_0x016e:
            if (r15 == 0) goto L_0x0173
            r3.savedFilterState = r15
            goto L_0x0177
        L_0x0173:
            if (r16 == 0) goto L_0x0177
            r3.savedFilterState = r5
        L_0x0177:
            int r2 = r0.sendPhotoType
            if (r2 == 0) goto L_0x017e
            r3 = 4
            if (r2 != r3) goto L_0x0194
        L_0x017e:
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r2 = r0.placeProvider
            if (r2 == 0) goto L_0x0194
            int r3 = r0.currentIndex
            r2.updatePhotoAtIndex(r3)
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r2 = r0.placeProvider
            int r3 = r0.currentIndex
            boolean r2 = r2.isPhotoChecked(r3)
            if (r2 != 0) goto L_0x0194
            r19.setPhotoChecked()
        L_0x0194:
            int r2 = r0.currentEditMode
            if (r2 != r6) goto L_0x01f1
            org.telegram.ui.Components.PhotoCropView r2 = r0.photoCropView
            float r2 = r2.getRectSizeX()
            int r3 = r19.getContainerViewWidth()
            float r3 = (float) r3
            float r2 = r2 / r3
            org.telegram.ui.Components.PhotoCropView r3 = r0.photoCropView
            float r3 = r3.getRectSizeY()
            int r7 = r19.getContainerViewHeight()
            float r7 = (float) r7
            float r3 = r3 / r7
            int r7 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r7 <= 0) goto L_0x01b5
            goto L_0x01b6
        L_0x01b5:
            r2 = r3
        L_0x01b6:
            r0.scale = r2
            org.telegram.ui.Components.PhotoCropView r2 = r0.photoCropView
            float r2 = r2.getRectX()
            org.telegram.ui.Components.PhotoCropView r3 = r0.photoCropView
            float r3 = r3.getRectSizeX()
            r7 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 / r7
            float r2 = r2 + r3
            int r3 = r19.getContainerViewWidth()
            int r3 = r3 / r4
            float r3 = (float) r3
            float r2 = r2 - r3
            r0.translationX = r2
            org.telegram.ui.Components.PhotoCropView r2 = r0.photoCropView
            float r2 = r2.getRectY()
            org.telegram.ui.Components.PhotoCropView r3 = r0.photoCropView
            float r3 = r3.getRectSizeY()
            float r3 = r3 / r7
            float r2 = r2 + r3
            int r3 = r19.getContainerViewHeight()
            int r3 = r3 / r4
            float r3 = (float) r3
            float r2 = r2 - r3
            r0.translationY = r2
            r0.zoomAnimation = r6
            r0.applying = r6
            org.telegram.ui.Components.PhotoCropView r2 = r0.photoCropView
            r2.onDisappear()
        L_0x01f1:
            org.telegram.messenger.ImageReceiver r2 = r0.centerImage
            r2.setParentView(r5)
            org.telegram.messenger.ImageReceiver r2 = r0.centerImage
            r3 = 0
            r2.setOrientation(r3, r6)
            r0.ignoreDidSetImage = r6
            org.telegram.messenger.ImageReceiver r2 = r0.centerImage
            r2.setImageBitmap((android.graphics.Bitmap) r1)
            r0.ignoreDidSetImage = r3
            org.telegram.messenger.ImageReceiver r1 = r0.centerImage
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r2 = r0.containerView
            r1.setParentView(r2)
            int r1 = r0.sendPhotoType
            if (r1 != r6) goto L_0x0213
            r19.setCropBitmap()
        L_0x0213:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.applyCurrentEditMode():void");
    }

    private void setPhotoChecked() {
        ChatActivity chatActivity;
        TLRPC.Chat currentChat;
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        if (photoViewerProvider == null) {
            return;
        }
        if (photoViewerProvider.getSelectedPhotos() == null || this.maxSelectedPhotos <= 0 || this.placeProvider.getSelectedPhotos().size() < this.maxSelectedPhotos || this.placeProvider.isPhotoChecked(this.currentIndex)) {
            int photoChecked = this.placeProvider.setPhotoChecked(this.currentIndex, getCurrentVideoEditedInfo());
            boolean isPhotoChecked = this.placeProvider.isPhotoChecked(this.currentIndex);
            this.checkImageView.setChecked(isPhotoChecked, true);
            if (photoChecked >= 0) {
                if (isPhotoChecked) {
                    this.selectedPhotosAdapter.notifyItemInserted(photoChecked);
                    this.selectedPhotosListView.smoothScrollToPosition(photoChecked);
                } else {
                    this.selectedPhotosAdapter.notifyItemRemoved(photoChecked);
                }
            }
            updateSelectedCount();
        } else if (this.allowOrder && (chatActivity = this.parentChatActivity) != null && (currentChat = chatActivity.getCurrentChat()) != null && !ChatObject.hasAdminRights(currentChat) && currentChat.slowmode_enabled) {
            AlertsCreator.createSimpleAlert(this.parentActivity, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSelectSendError", NUM)).show();
        }
    }

    private void createCropView() {
        if (this.photoCropView == null) {
            this.photoCropView = new PhotoCropView(this.actvityContext);
            this.photoCropView.setVisibility(8);
            this.containerView.addView(this.photoCropView, this.containerView.indexOfChild(this.pickerViewSendButton) - 1, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
            this.photoCropView.setDelegate(new PhotoCropView.PhotoCropViewDelegate() {
                public final void onChange(boolean z) {
                    PhotoViewer.this.lambda$createCropView$37$PhotoViewer(z);
                }
            });
        }
    }

    public /* synthetic */ void lambda$createCropView$37$PhotoViewer(boolean z) {
        this.resetButton.setVisibility(z ? 8 : 0);
    }

    /* JADX WARNING: Removed duplicated region for block: B:79:0x02e9  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x02f6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void switchToEditMode(int r18) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            int r2 = r0.currentEditMode
            if (r2 == r1) goto L_0x055a
            org.telegram.messenger.ImageReceiver r2 = r0.centerImage
            android.graphics.Bitmap r2 = r2.getBitmap()
            if (r2 == 0) goto L_0x055a
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            if (r2 != 0) goto L_0x055a
            android.animation.AnimatorSet r2 = r0.imageMoveAnimation
            if (r2 != 0) goto L_0x055a
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r2 = r0.photoProgressViews
            r3 = 0
            r2 = r2[r3]
            int r2 = r2.backgroundState
            r4 = -1
            if (r2 != r4) goto L_0x055a
            org.telegram.ui.Components.PhotoViewerCaptionEnterView r2 = r0.captionEditText
            java.lang.Object r2 = r2.getTag()
            if (r2 == 0) goto L_0x002e
            goto L_0x055a
        L_0x002e:
            r2 = 1127874560(0x433a0000, float:186.0)
            r5 = 3
            r6 = 1123811328(0x42fCLASSNAME, float:126.0)
            r9 = 4
            r10 = 0
            r11 = 1
            r12 = 2
            if (r1 != 0) goto L_0x019f
            org.telegram.messenger.ImageReceiver r4 = r0.centerImage
            android.graphics.Bitmap r4 = r4.getBitmap()
            if (r4 == 0) goto L_0x00c9
            org.telegram.messenger.ImageReceiver r4 = r0.centerImage
            int r4 = r4.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r13 = r0.centerImage
            int r13 = r13.getBitmapHeight()
            int r14 = r17.getContainerViewWidth()
            float r14 = (float) r14
            float r4 = (float) r4
            float r14 = r14 / r4
            int r15 = r17.getContainerViewHeight()
            float r15 = (float) r15
            float r13 = (float) r13
            float r15 = r15 / r13
            int r7 = r0.getContainerViewWidth(r3)
            float r7 = (float) r7
            float r7 = r7 / r4
            int r4 = r0.getContainerViewHeight(r3)
            float r4 = (float) r4
            float r4 = r4 / r13
            int r8 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1))
            if (r8 <= 0) goto L_0x006c
            r14 = r15
        L_0x006c:
            int r8 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r8 <= 0) goto L_0x0071
            goto L_0x0072
        L_0x0071:
            r4 = r7
        L_0x0072:
            int r7 = r0.sendPhotoType
            if (r7 != r11) goto L_0x007a
            r0.setCropTranslations(r11)
            goto L_0x00c9
        L_0x007a:
            float r4 = r4 / r14
            r0.animateToScale = r4
            r0.animateToX = r10
            int r4 = r17.getLeftInset()
            int r4 = r4 / r12
            int r7 = r17.getRightInset()
            int r7 = r7 / r12
            int r4 = r4 - r7
            float r4 = (float) r4
            r0.translationX = r4
            int r4 = r0.currentEditMode
            if (r4 != r11) goto L_0x009b
            r4 = 1114112000(0x42680000, float:58.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.animateToY = r4
            goto L_0x00b2
        L_0x009b:
            if (r4 != r12) goto L_0x00a7
            r4 = 1119354880(0x42b80000, float:92.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.animateToY = r4
            goto L_0x00b2
        L_0x00a7:
            if (r4 != r5) goto L_0x00b2
            r4 = 1110441984(0x42300000, float:44.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.animateToY = r4
        L_0x00b2:
            int r4 = android.os.Build.VERSION.SDK_INT
            r7 = 21
            if (r4 < r7) goto L_0x00c1
            float r4 = r0.animateToY
            int r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            int r7 = r7 / r12
            float r7 = (float) r7
            float r4 = r4 - r7
            r0.animateToY = r4
        L_0x00c1:
            long r7 = java.lang.System.currentTimeMillis()
            r0.animationStartTime = r7
            r0.zoomAnimation = r11
        L_0x00c9:
            r0.padImageForHorizontalInsets = r3
            android.animation.AnimatorSet r4 = new android.animation.AnimatorSet
            r4.<init>()
            r0.imageMoveAnimation = r4
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>(r9)
            int r7 = r0.currentEditMode
            if (r7 != r11) goto L_0x010f
            org.telegram.ui.Components.PickerBottomLayoutViewer r2 = r0.editorDoneLayout
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r11]
            r7 = 1111490560(0x42400000, float:48.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r6[r3] = r7
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r6)
            r4.add(r2)
            android.util.Property<org.telegram.ui.PhotoViewer, java.lang.Float> r2 = org.telegram.ui.Components.AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE
            float[] r5 = new float[r12]
            r5 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r0, r2, r5)
            r4.add(r2)
            org.telegram.ui.Components.PhotoCropView r2 = r0.photoCropView
            android.util.Property r5 = android.view.View.ALPHA
            float[] r6 = new float[r11]
            r6[r3] = r10
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r6)
            r4.add(r2)
            goto L_0x0182
        L_0x010f:
            if (r7 != r12) goto L_0x013d
            org.telegram.ui.Components.PhotoFilterView r5 = r0.photoFilterView
            r5.shutdown()
            org.telegram.ui.Components.PhotoFilterView r5 = r0.photoFilterView
            android.widget.FrameLayout r5 = r5.getToolsView()
            android.util.Property r6 = android.view.View.TRANSLATION_Y
            float[] r7 = new float[r11]
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r7[r3] = r2
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r4.add(r2)
            android.util.Property<org.telegram.ui.PhotoViewer, java.lang.Float> r2 = org.telegram.ui.Components.AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE
            float[] r3 = new float[r12]
            r3 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r0, r2, r3)
            r4.add(r2)
            goto L_0x0182
        L_0x013d:
            if (r7 != r5) goto L_0x0182
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            r2.shutdown()
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            android.widget.FrameLayout r2 = r2.getToolsView()
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r7 = new float[r11]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r8 = (float) r8
            r7[r3] = r8
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r7)
            r4.add(r2)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            org.telegram.ui.Components.Paint.Views.ColorPicker r2 = r2.getColorPicker()
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r7 = new float[r11]
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            r7[r3] = r6
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r7)
            r4.add(r2)
            android.util.Property<org.telegram.ui.PhotoViewer, java.lang.Float> r2 = org.telegram.ui.Components.AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE
            float[] r3 = new float[r12]
            r3 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r0, r2, r3)
            r4.add(r2)
        L_0x0182:
            android.animation.AnimatorSet r2 = r0.imageMoveAnimation
            r2.playTogether(r4)
            android.animation.AnimatorSet r2 = r0.imageMoveAnimation
            r3 = 200(0xc8, double:9.9E-322)
            r2.setDuration(r3)
            android.animation.AnimatorSet r2 = r0.imageMoveAnimation
            org.telegram.ui.PhotoViewer$32 r3 = new org.telegram.ui.PhotoViewer$32
            r3.<init>(r1)
            r2.addListener(r3)
            android.animation.AnimatorSet r1 = r0.imageMoveAnimation
            r1.start()
            goto L_0x055a
        L_0x019f:
            r7 = 1119879168(0x42CLASSNAME, float:96.0)
            if (r1 != r11) goto L_0x02a4
            r17.createCropView()
            org.telegram.ui.Components.PhotoCropView r2 = r0.photoCropView
            r2.onAppear()
            org.telegram.ui.Components.PickerBottomLayoutViewer r2 = r0.editorDoneLayout
            android.widget.TextView r2 = r2.doneButton
            r4 = 2131624802(0x7f0e0362, float:1.8876794E38)
            java.lang.String r5 = "Crop"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setText(r4)
            org.telegram.ui.Components.PickerBottomLayoutViewer r2 = r0.editorDoneLayout
            android.widget.TextView r2 = r2.doneButton
            java.lang.String r4 = "dialogFloatingButton"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setTextColor(r4)
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            r0.changeModeAnimation = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            android.widget.FrameLayout r4 = r0.pickerView
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r3] = r10
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r8 = (float) r8
            r6[r11] = r8
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
            r2.add(r4)
            android.widget.ImageView r4 = r0.pickerViewSendButton
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r3] = r10
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r8 = (float) r8
            r6[r11] = r8
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
            r2.add(r4)
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r3] = r10
            int r8 = r4.getHeight()
            int r8 = -r8
            float r8 = (float) r8
            r6[r11] = r8
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
            r2.add(r4)
            boolean r4 = r0.needCaptionLayout
            if (r4 == 0) goto L_0x0231
            android.widget.TextView r4 = r0.captionTextView
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r3] = r10
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r3 = (float) r3
            r6[r11] = r3
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
            r2.add(r3)
        L_0x0231:
            int r3 = r0.sendPhotoType
            if (r3 == 0) goto L_0x0237
            if (r3 != r9) goto L_0x0257
        L_0x0237:
            org.telegram.ui.Components.CheckBox r3 = r0.checkImageView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
            org.telegram.ui.PhotoViewer$CounterView r3 = r0.photosCounterView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x0257:
            org.telegram.ui.Components.RecyclerListView r3 = r0.selectedPhotosListView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x026f
            org.telegram.ui.Components.RecyclerListView r3 = r0.selectedPhotosListView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x026f:
            android.widget.ImageView r3 = r0.cameraItem
            java.lang.Object r3 = r3.getTag()
            if (r3 == 0) goto L_0x0287
            android.widget.ImageView r3 = r0.cameraItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x0287:
            android.animation.AnimatorSet r3 = r0.changeModeAnimation
            r3.playTogether(r2)
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            r3 = 200(0xc8, double:9.9E-322)
            r2.setDuration(r3)
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            org.telegram.ui.PhotoViewer$33 r3 = new org.telegram.ui.PhotoViewer$33
            r3.<init>(r1)
            r2.addListener(r3)
            android.animation.AnimatorSet r1 = r0.changeModeAnimation
            r1.start()
            goto L_0x055a
        L_0x02a4:
            r8 = -1082130432(0xffffffffbvar_, float:-1.0)
            if (r1 != r12) goto L_0x040e
            org.telegram.ui.Components.PhotoFilterView r5 = r0.photoFilterView
            if (r5 != 0) goto L_0x0338
            java.util.ArrayList<java.lang.Object> r5 = r0.imagesArrLocals
            boolean r5 = r5.isEmpty()
            r6 = 0
            if (r5 != 0) goto L_0x02e5
            java.util.ArrayList<java.lang.Object> r5 = r0.imagesArrLocals
            int r13 = r0.currentIndex
            java.lang.Object r5 = r5.get(r13)
            boolean r13 = r5 instanceof org.telegram.messenger.MediaController.PhotoEntry
            if (r13 == 0) goto L_0x02da
            org.telegram.messenger.MediaController$PhotoEntry r5 = (org.telegram.messenger.MediaController.PhotoEntry) r5
            java.lang.String r13 = r5.imagePath
            if (r13 != 0) goto L_0x02d1
            java.lang.String r6 = r5.path
            org.telegram.messenger.MediaController$SavedFilterState r13 = r5.savedFilterState
            r16 = r13
            r13 = r6
            r6 = r16
            goto L_0x02d2
        L_0x02d1:
            r13 = r6
        L_0x02d2:
            int r5 = r5.orientation
            r16 = r13
            r13 = r5
            r5 = r16
            goto L_0x02e7
        L_0x02da:
            boolean r13 = r5 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r13 == 0) goto L_0x02e5
            org.telegram.messenger.MediaController$SearchImage r5 = (org.telegram.messenger.MediaController.SearchImage) r5
            org.telegram.messenger.MediaController$SavedFilterState r6 = r5.savedFilterState
            java.lang.String r5 = r5.imageUrl
            goto L_0x02e6
        L_0x02e5:
            r5 = r6
        L_0x02e6:
            r13 = 0
        L_0x02e7:
            if (r6 != 0) goto L_0x02f6
            org.telegram.messenger.ImageReceiver r5 = r0.centerImage
            android.graphics.Bitmap r5 = r5.getBitmap()
            org.telegram.messenger.ImageReceiver r13 = r0.centerImage
            int r13 = r13.getOrientation()
            goto L_0x02fa
        L_0x02f6:
            android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeFile(r5)
        L_0x02fa:
            org.telegram.ui.Components.PhotoFilterView r14 = new org.telegram.ui.Components.PhotoFilterView
            android.app.Activity r15 = r0.parentActivity
            r14.<init>(r15, r5, r13, r6)
            r0.photoFilterView = r14
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r5 = r0.containerView
            org.telegram.ui.Components.PhotoFilterView r6 = r0.photoFilterView
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r8)
            r5.addView(r6, r4)
            org.telegram.ui.Components.PhotoFilterView r4 = r0.photoFilterView
            android.widget.TextView r4 = r4.getDoneTextView()
            org.telegram.ui.-$$Lambda$PhotoViewer$iPzcqyK9klw8fE_zuZB7xfys4J8 r5 = new org.telegram.ui.-$$Lambda$PhotoViewer$iPzcqyK9klw8fE_zuZB7xfys4J8
            r5.<init>()
            r4.setOnClickListener(r5)
            org.telegram.ui.Components.PhotoFilterView r4 = r0.photoFilterView
            android.widget.TextView r4 = r4.getCancelTextView()
            org.telegram.ui.-$$Lambda$PhotoViewer$Ws0W6J-E4CCAikNDkwUaS7ufRkg r5 = new org.telegram.ui.-$$Lambda$PhotoViewer$Ws0W6J-E4CCAikNDkwUaS7ufRkg
            r5.<init>()
            r4.setOnClickListener(r5)
            org.telegram.ui.Components.PhotoFilterView r4 = r0.photoFilterView
            android.widget.FrameLayout r4 = r4.getToolsView()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r4.setTranslationY(r2)
        L_0x0338:
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            r0.changeModeAnimation = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            android.widget.FrameLayout r4 = r0.pickerView
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r3] = r10
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r8 = (float) r8
            r6[r11] = r8
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
            r2.add(r4)
            android.widget.ImageView r4 = r0.pickerViewSendButton
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r3] = r10
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r6[r11] = r7
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
            r2.add(r4)
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r3] = r10
            int r3 = r4.getHeight()
            int r3 = -r3
            float r3 = (float) r3
            r6[r11] = r3
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
            r2.add(r3)
            int r3 = r0.sendPhotoType
            if (r3 == 0) goto L_0x03a1
            if (r3 != r9) goto L_0x038e
            goto L_0x03a1
        L_0x038e:
            if (r3 != r11) goto L_0x03c1
            org.telegram.ui.Components.PhotoCropView r3 = r0.photoCropView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
            goto L_0x03c1
        L_0x03a1:
            org.telegram.ui.Components.CheckBox r3 = r0.checkImageView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
            org.telegram.ui.PhotoViewer$CounterView r3 = r0.photosCounterView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x03c1:
            org.telegram.ui.Components.RecyclerListView r3 = r0.selectedPhotosListView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x03d9
            org.telegram.ui.Components.RecyclerListView r3 = r0.selectedPhotosListView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x03d9:
            android.widget.ImageView r3 = r0.cameraItem
            java.lang.Object r3 = r3.getTag()
            if (r3 == 0) goto L_0x03f1
            android.widget.ImageView r3 = r0.cameraItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x03f1:
            android.animation.AnimatorSet r3 = r0.changeModeAnimation
            r3.playTogether(r2)
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            r3 = 200(0xc8, double:9.9E-322)
            r2.setDuration(r3)
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            org.telegram.ui.PhotoViewer$34 r3 = new org.telegram.ui.PhotoViewer$34
            r3.<init>(r1)
            r2.addListener(r3)
            android.animation.AnimatorSet r1 = r0.changeModeAnimation
            r1.start()
            goto L_0x055a
        L_0x040e:
            if (r1 != r5) goto L_0x055a
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            if (r2 != 0) goto L_0x046c
            org.telegram.ui.Components.PhotoPaintView r2 = new org.telegram.ui.Components.PhotoPaintView
            android.app.Activity r5 = r0.parentActivity
            org.telegram.messenger.ImageReceiver r13 = r0.centerImage
            android.graphics.Bitmap r13 = r13.getBitmap()
            org.telegram.messenger.ImageReceiver r14 = r0.centerImage
            int r14 = r14.getOrientation()
            r2.<init>(r5, r13, r14)
            r0.photoPaintView = r2
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r2 = r0.containerView
            org.telegram.ui.Components.PhotoPaintView r5 = r0.photoPaintView
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r8)
            r2.addView(r5, r4)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            android.widget.TextView r2 = r2.getDoneTextView()
            org.telegram.ui.-$$Lambda$PhotoViewer$9pMvR6fP_n59bDPsHRYPAq6ryyo r4 = new org.telegram.ui.-$$Lambda$PhotoViewer$9pMvR6fP_n59bDPsHRYPAq6ryyo
            r4.<init>()
            r2.setOnClickListener(r4)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            android.widget.TextView r2 = r2.getCancelTextView()
            org.telegram.ui.-$$Lambda$PhotoViewer$xHUEjTPCEWvnAkiqr8jK2QN-GCg r4 = new org.telegram.ui.-$$Lambda$PhotoViewer$xHUEjTPCEWvnAkiqr8jK2QN-GCg
            r4.<init>()
            r2.setOnClickListener(r4)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            org.telegram.ui.Components.Paint.Views.ColorPicker r2 = r2.getColorPicker()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r4 = (float) r4
            r2.setTranslationY(r4)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            android.widget.FrameLayout r2 = r2.getToolsView()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r4 = (float) r4
            r2.setTranslationY(r4)
        L_0x046c:
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            r0.changeModeAnimation = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            android.widget.FrameLayout r4 = r0.pickerView
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r3] = r10
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r8 = (float) r8
            r6[r11] = r8
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
            r2.add(r4)
            android.widget.ImageView r4 = r0.pickerViewSendButton
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r3] = r10
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r8 = (float) r8
            r6[r11] = r8
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
            r2.add(r4)
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r3] = r10
            int r8 = r4.getHeight()
            int r8 = -r8
            float r8 = (float) r8
            r6[r11] = r8
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
            r2.add(r4)
            boolean r4 = r0.needCaptionLayout
            if (r4 == 0) goto L_0x04d5
            android.widget.TextView r4 = r0.captionTextView
            android.util.Property r5 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r3] = r10
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r3 = (float) r3
            r6[r11] = r3
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r4, r5, r6)
            r2.add(r3)
        L_0x04d5:
            int r3 = r0.sendPhotoType
            if (r3 == 0) goto L_0x04ef
            if (r3 != r9) goto L_0x04dc
            goto L_0x04ef
        L_0x04dc:
            if (r3 != r11) goto L_0x050f
            org.telegram.ui.Components.PhotoCropView r3 = r0.photoCropView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
            goto L_0x050f
        L_0x04ef:
            org.telegram.ui.Components.CheckBox r3 = r0.checkImageView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
            org.telegram.ui.PhotoViewer$CounterView r3 = r0.photosCounterView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x050f:
            org.telegram.ui.Components.RecyclerListView r3 = r0.selectedPhotosListView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x0527
            org.telegram.ui.Components.RecyclerListView r3 = r0.selectedPhotosListView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x0527:
            android.widget.ImageView r3 = r0.cameraItem
            java.lang.Object r3 = r3.getTag()
            if (r3 == 0) goto L_0x053f
            android.widget.ImageView r3 = r0.cameraItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x053f:
            android.animation.AnimatorSet r3 = r0.changeModeAnimation
            r3.playTogether(r2)
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            r3 = 200(0xc8, double:9.9E-322)
            r2.setDuration(r3)
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            org.telegram.ui.PhotoViewer$35 r3 = new org.telegram.ui.PhotoViewer$35
            r3.<init>(r1)
            r2.addListener(r3)
            android.animation.AnimatorSet r1 = r0.changeModeAnimation
            r1.start()
        L_0x055a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.switchToEditMode(int):void");
    }

    public /* synthetic */ void lambda$switchToEditMode$38$PhotoViewer(View view) {
        applyCurrentEditMode();
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$switchToEditMode$40$PhotoViewer(View view) {
        if (this.photoFilterView.hasChanges()) {
            Activity activity = this.parentActivity;
            if (activity != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
                builder.setMessage(LocaleController.getString("DiscardChanges", NUM));
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PhotoViewer.this.lambda$null$39$PhotoViewer(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showAlertDialog(builder);
                return;
            }
            return;
        }
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$null$39$PhotoViewer(DialogInterface dialogInterface, int i) {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$switchToEditMode$41$PhotoViewer(View view) {
        applyCurrentEditMode();
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$null$42$PhotoViewer() {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$switchToEditMode$43$PhotoViewer(View view) {
        this.photoPaintView.maybeShowDismissalAlert(this, this.parentActivity, new Runnable() {
            public final void run() {
                PhotoViewer.this.lambda$null$42$PhotoViewer();
            }
        });
    }

    private void toggleCheckImageView(boolean z) {
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        FrameLayout frameLayout = this.pickerView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
        ImageView imageView = this.pickerViewSendButton;
        Property property2 = View.ALPHA;
        float[] fArr2 = new float[1];
        fArr2[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(imageView, property2, fArr2));
        if (this.needCaptionLayout) {
            TextView textView = this.captionTextView;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(textView, property3, fArr3));
        }
        int i = this.sendPhotoType;
        if (i == 0 || i == 4) {
            CheckBox checkBox = this.checkImageView;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(checkBox, property4, fArr4));
            CounterView counterView = this.photosCounterView;
            Property property5 = View.ALPHA;
            float[] fArr5 = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr5[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(counterView, property5, fArr5));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void toggleMiniProgressInternal(final boolean z) {
        if (z) {
            this.miniProgressView.setVisibility(0);
        }
        this.miniProgressAnimator = new AnimatorSet();
        AnimatorSet animatorSet = this.miniProgressAnimator;
        Animator[] animatorArr = new Animator[1];
        RadialProgressView radialProgressView = this.miniProgressView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(radialProgressView, property, fArr);
        animatorSet.playTogether(animatorArr);
        this.miniProgressAnimator.setDuration(200);
        this.miniProgressAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(PhotoViewer.this.miniProgressAnimator)) {
                    if (!z) {
                        PhotoViewer.this.miniProgressView.setVisibility(4);
                    }
                    AnimatorSet unused = PhotoViewer.this.miniProgressAnimator = null;
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (animator.equals(PhotoViewer.this.miniProgressAnimator)) {
                    AnimatorSet unused = PhotoViewer.this.miniProgressAnimator = null;
                }
            }
        });
        this.miniProgressAnimator.start();
    }

    private void toggleMiniProgress(boolean z, boolean z2) {
        int i = 0;
        if (z2) {
            toggleMiniProgressInternal(z);
            if (z) {
                AnimatorSet animatorSet = this.miniProgressAnimator;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.miniProgressAnimator = null;
                }
                AndroidUtilities.cancelRunOnUIThread(this.miniProgressShowRunnable);
                if (this.firstAnimationDelay) {
                    this.firstAnimationDelay = false;
                    toggleMiniProgressInternal(true);
                    return;
                }
                AndroidUtilities.runOnUIThread(this.miniProgressShowRunnable, 500);
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(this.miniProgressShowRunnable);
            AnimatorSet animatorSet2 = this.miniProgressAnimator;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                toggleMiniProgressInternal(false);
                return;
            }
            return;
        }
        AnimatorSet animatorSet3 = this.miniProgressAnimator;
        if (animatorSet3 != null) {
            animatorSet3.cancel();
            this.miniProgressAnimator = null;
        }
        this.miniProgressView.setAlpha(z ? 1.0f : 0.0f);
        RadialProgressView radialProgressView = this.miniProgressView;
        if (!z) {
            i = 4;
        }
        radialProgressView.setVisibility(i);
    }

    private void toggleActionBar(final boolean z, boolean z2) {
        AnimatorSet animatorSet = this.actionBarAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (z) {
            this.actionBar.setVisibility(0);
            if (this.bottomLayout.getTag() != null) {
                this.bottomLayout.setVisibility(0);
            }
            if (this.captionTextView.getTag() != null) {
                this.captionTextView.setVisibility(0);
                VideoSeekPreviewImage videoSeekPreviewImage = this.videoPreviewFrame;
                if (videoSeekPreviewImage != null) {
                    videoSeekPreviewImage.requestLayout();
                }
            }
        }
        this.isActionBarVisible = z;
        if (Build.VERSION.SDK_INT >= 21 && this.sendPhotoType != 1) {
            int i = 4 | ((this.containerView.getPaddingLeft() > 0 || this.containerView.getPaddingRight() > 0) ? 4098 : 0);
            if (z) {
                FrameLayoutDrawer frameLayoutDrawer = this.containerView;
                frameLayoutDrawer.setSystemUiVisibility((i ^ -1) & frameLayoutDrawer.getSystemUiVisibility());
            } else {
                FrameLayoutDrawer frameLayoutDrawer2 = this.containerView;
                frameLayoutDrawer2.setSystemUiVisibility(i | frameLayoutDrawer2.getSystemUiVisibility());
            }
        }
        float f = 1.0f;
        if (z2) {
            ArrayList arrayList = new ArrayList();
            ActionBar actionBar2 = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar2, property, fArr));
            FrameLayout frameLayout = this.bottomLayout;
            if (frameLayout != null) {
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(frameLayout, property2, fArr2));
            }
            GroupedPhotosListView groupedPhotosListView2 = this.groupedPhotosListView;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView2, property3, fArr3));
            if (this.captionTextView.getTag() != null) {
                TextView textView = this.captionTextView;
                Property property4 = View.ALPHA;
                float[] fArr4 = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr4[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(textView, property4, fArr4));
            }
            this.actionBarAnimator = new AnimatorSet();
            this.actionBarAnimator.playTogether(arrayList);
            this.actionBarAnimator.setDuration(200);
            this.actionBarAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(PhotoViewer.this.actionBarAnimator)) {
                        if (!z) {
                            PhotoViewer.this.actionBar.setVisibility(4);
                            if (PhotoViewer.this.bottomLayout.getTag() != null) {
                                PhotoViewer.this.bottomLayout.setVisibility(4);
                            }
                            if (PhotoViewer.this.captionTextView.getTag() != null) {
                                PhotoViewer.this.captionTextView.setVisibility(4);
                            }
                        }
                        AnimatorSet unused = PhotoViewer.this.actionBarAnimator = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(PhotoViewer.this.actionBarAnimator)) {
                        AnimatorSet unused = PhotoViewer.this.actionBarAnimator = null;
                    }
                }
            });
            this.actionBarAnimator.start();
            return;
        }
        this.actionBar.setAlpha(z ? 1.0f : 0.0f);
        this.bottomLayout.setAlpha(z ? 1.0f : 0.0f);
        this.groupedPhotosListView.setAlpha(z ? 1.0f : 0.0f);
        TextView textView2 = this.captionTextView;
        if (!z) {
            f = 0.0f;
        }
        textView2.setAlpha(f);
    }

    private void togglePhotosListView(boolean z, boolean z2) {
        if (z != this.isPhotosListViewVisible) {
            if (z) {
                this.selectedPhotosListView.setVisibility(0);
            }
            this.isPhotosListViewVisible = z;
            this.selectedPhotosListView.setEnabled(z);
            float f = 1.0f;
            if (z2) {
                ArrayList arrayList = new ArrayList();
                RecyclerListView recyclerListView = this.selectedPhotosListView;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(recyclerListView, property, fArr));
                RecyclerListView recyclerListView2 = this.selectedPhotosListView;
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 0.0f : (float) (-AndroidUtilities.dp(10.0f));
                arrayList.add(ObjectAnimator.ofFloat(recyclerListView2, property2, fArr2));
                CounterView counterView = this.photosCounterView;
                Property property3 = View.ROTATION_X;
                float[] fArr3 = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr3[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(counterView, property3, fArr3));
                this.currentListViewAnimation = new AnimatorSet();
                this.currentListViewAnimation.playTogether(arrayList);
                if (!z) {
                    this.currentListViewAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (PhotoViewer.this.currentListViewAnimation != null && PhotoViewer.this.currentListViewAnimation.equals(animator)) {
                                PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                                AnimatorSet unused = PhotoViewer.this.currentListViewAnimation = null;
                            }
                        }
                    });
                }
                this.currentListViewAnimation.setDuration(200);
                this.currentListViewAnimation.start();
                return;
            }
            this.selectedPhotosListView.setAlpha(z ? 1.0f : 0.0f);
            this.selectedPhotosListView.setTranslationY(z ? 0.0f : (float) (-AndroidUtilities.dp(10.0f)));
            CounterView counterView2 = this.photosCounterView;
            if (!z) {
                f = 0.0f;
            }
            counterView2.setRotationX(f);
            if (!z) {
                this.selectedPhotosListView.setVisibility(8);
            }
        }
    }

    private String getFileName(int i) {
        ImageLocation imageLocation;
        if (i < 0) {
            return null;
        }
        if (this.secureDocuments.isEmpty()) {
            if (!this.imagesArrLocations.isEmpty() || !this.imagesArr.isEmpty()) {
                if (!this.imagesArrLocations.isEmpty()) {
                    if (i >= this.imagesArrLocations.size() || (imageLocation = this.imagesArrLocations.get(i)) == null) {
                        return null;
                    }
                    return imageLocation.location.volume_id + "_" + imageLocation.location.local_id + ".jpg";
                } else if (this.imagesArr.isEmpty() || i >= this.imagesArr.size()) {
                    return null;
                } else {
                    return FileLoader.getMessageFileName(this.imagesArr.get(i).messageOwner);
                }
            } else if (this.imagesArrLocals.isEmpty() || i >= this.imagesArrLocals.size()) {
                return null;
            } else {
                Object obj = this.imagesArrLocals.get(i);
                if (obj instanceof MediaController.SearchImage) {
                    return ((MediaController.SearchImage) obj).getAttachName();
                }
                if (obj instanceof TLRPC.BotInlineResult) {
                    TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) obj;
                    TLRPC.Document document = botInlineResult.document;
                    if (document != null) {
                        return FileLoader.getAttachFileName(document);
                    }
                    TLRPC.Photo photo = botInlineResult.photo;
                    if (photo != null) {
                        return FileLoader.getAttachFileName(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize()));
                    }
                    if (botInlineResult.content instanceof TLRPC.TL_webDocument) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(Utilities.MD5(botInlineResult.content.url));
                        sb.append(".");
                        TLRPC.WebDocument webDocument = botInlineResult.content;
                        sb.append(ImageLoader.getHttpUrlExtension(webDocument.url, FileLoader.getMimeTypePart(webDocument.mime_type)));
                        return sb.toString();
                    }
                }
            }
            return null;
        } else if (i >= this.secureDocuments.size()) {
            return null;
        } else {
            SecureDocument secureDocument = this.secureDocuments.get(i);
            return secureDocument.secureFile.dc_id + "_" + secureDocument.secureFile.id + ".jpg";
        }
    }

    private ImageLocation getImageLocation(int i, int[] iArr) {
        if (i < 0) {
            return null;
        }
        if (!this.secureDocuments.isEmpty()) {
            if (i >= this.secureDocuments.size()) {
                return null;
            }
            if (iArr != null) {
                iArr[0] = this.secureDocuments.get(i).secureFile.size;
            }
            return ImageLocation.getForSecureDocument(this.secureDocuments.get(i));
        } else if (!this.imagesArrLocations.isEmpty()) {
            if (i >= this.imagesArrLocations.size()) {
                return null;
            }
            if (iArr != null) {
                iArr[0] = this.imagesArrLocationsSizes.get(i).intValue();
            }
            return this.imagesArrLocations.get(i);
        } else if (this.imagesArr.isEmpty() || i >= this.imagesArr.size()) {
            return null;
        } else {
            MessageObject messageObject = this.imagesArr.get(i);
            TLRPC.Message message = messageObject.messageOwner;
            if (!(message instanceof TLRPC.TL_messageService)) {
                TLRPC.MessageMedia messageMedia = message.media;
                if (!(messageMedia instanceof TLRPC.TL_messageMediaPhoto) || messageMedia.photo == null) {
                    TLRPC.MessageMedia messageMedia2 = messageObject.messageOwner.media;
                    if (!(messageMedia2 instanceof TLRPC.TL_messageMediaWebPage) || messageMedia2.webpage == null) {
                        TLRPC.MessageMedia messageMedia3 = messageObject.messageOwner.media;
                        if (messageMedia3 instanceof TLRPC.TL_messageMediaInvoice) {
                            return ImageLocation.getForWebFile(WebFile.createWithWebDocument(((TLRPC.TL_messageMediaInvoice) messageMedia3).photo));
                        }
                        if (messageObject.getDocument() != null && MessageObject.isDocumentHasThumb(messageObject.getDocument())) {
                            TLRPC.Document document = messageObject.getDocument();
                            TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                            if (iArr != null) {
                                iArr[0] = closestPhotoSizeWithSize.size;
                                if (iArr[0] == 0) {
                                    iArr[0] = -1;
                                }
                            }
                            return ImageLocation.getForDocument(closestPhotoSizeWithSize, document);
                        }
                    }
                }
                if (messageObject.isGif()) {
                    return ImageLocation.getForDocument(messageObject.getDocument());
                }
                TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                if (closestPhotoSizeWithSize2 != null) {
                    if (iArr != null) {
                        iArr[0] = closestPhotoSizeWithSize2.size;
                        if (iArr[0] == 0) {
                            iArr[0] = -1;
                        }
                    }
                    return ImageLocation.getForObject(closestPhotoSizeWithSize2, messageObject.photoThumbsObject);
                } else if (iArr != null) {
                    iArr[0] = -1;
                }
            } else if (message.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                return null;
            } else {
                TLRPC.PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                if (closestPhotoSizeWithSize3 != null) {
                    if (iArr != null) {
                        iArr[0] = closestPhotoSizeWithSize3.size;
                        if (iArr[0] == 0) {
                            iArr[0] = -1;
                        }
                    }
                    return ImageLocation.getForObject(closestPhotoSizeWithSize3, messageObject.photoThumbsObject);
                } else if (iArr != null) {
                    iArr[0] = -1;
                }
            }
            return null;
        }
    }

    /* access modifiers changed from: private */
    public TLObject getFileLocation(int i, int[] iArr) {
        if (i < 0) {
            return null;
        }
        if (!this.secureDocuments.isEmpty()) {
            if (i >= this.secureDocuments.size()) {
                return null;
            }
            if (iArr != null) {
                iArr[0] = this.secureDocuments.get(i).secureFile.size;
            }
            return this.secureDocuments.get(i);
        } else if (!this.imagesArrLocations.isEmpty()) {
            if (i >= this.imagesArrLocations.size()) {
                return null;
            }
            if (iArr != null) {
                iArr[0] = this.imagesArrLocationsSizes.get(i).intValue();
            }
            return this.imagesArrLocations.get(i).location;
        } else if (this.imagesArr.isEmpty() || i >= this.imagesArr.size()) {
            return null;
        } else {
            MessageObject messageObject = this.imagesArr.get(i);
            TLRPC.Message message = messageObject.messageOwner;
            if (message instanceof TLRPC.TL_messageService) {
                TLRPC.MessageAction messageAction = message.action;
                if (messageAction instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                    return messageAction.newUserPhoto.photo_big;
                }
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                if (closestPhotoSizeWithSize != null) {
                    if (iArr != null) {
                        iArr[0] = closestPhotoSizeWithSize.size;
                        if (iArr[0] == 0) {
                            iArr[0] = -1;
                        }
                    }
                    return closestPhotoSizeWithSize;
                } else if (iArr != null) {
                    iArr[0] = -1;
                }
            } else {
                TLRPC.MessageMedia messageMedia = message.media;
                if (!(messageMedia instanceof TLRPC.TL_messageMediaPhoto) || messageMedia.photo == null) {
                    TLRPC.MessageMedia messageMedia2 = messageObject.messageOwner.media;
                    if (!(messageMedia2 instanceof TLRPC.TL_messageMediaWebPage) || messageMedia2.webpage == null) {
                        TLRPC.MessageMedia messageMedia3 = messageObject.messageOwner.media;
                        if (messageMedia3 instanceof TLRPC.TL_messageMediaInvoice) {
                            return ((TLRPC.TL_messageMediaInvoice) messageMedia3).photo;
                        }
                        if (messageObject.getDocument() != null && MessageObject.isDocumentHasThumb(messageObject.getDocument())) {
                            TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.getDocument().thumbs, 90);
                            if (iArr != null) {
                                iArr[0] = closestPhotoSizeWithSize2.size;
                                if (iArr[0] == 0) {
                                    iArr[0] = -1;
                                }
                            }
                            return closestPhotoSizeWithSize2;
                        }
                    }
                }
                TLRPC.PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                if (closestPhotoSizeWithSize3 != null) {
                    if (iArr != null) {
                        iArr[0] = closestPhotoSizeWithSize3.size;
                        if (iArr[0] == 0) {
                            iArr[0] = -1;
                        }
                    }
                    return closestPhotoSizeWithSize3;
                } else if (iArr != null) {
                    iArr[0] = -1;
                }
            }
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void updateSelectedCount() {
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        if (photoViewerProvider != null) {
            int selectedCount = photoViewerProvider.getSelectedCount();
            this.photosCounterView.setCount(selectedCount);
            if (selectedCount == 0) {
                togglePhotosListView(false, true);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:229:0x05ec  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x0627  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x06e2  */
    /* JADX WARNING: Removed duplicated region for block: B:298:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onPhotoShow(org.telegram.messenger.MessageObject r27, org.telegram.tgnet.TLRPC.FileLocation r28, org.telegram.messenger.ImageLocation r29, java.util.ArrayList<org.telegram.messenger.MessageObject> r30, java.util.ArrayList<org.telegram.messenger.SecureDocument> r31, java.util.ArrayList<java.lang.Object> r32, int r33, org.telegram.ui.PhotoViewer.PlaceProviderObject r34) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r30
            r3 = r31
            r4 = r32
            r5 = r33
            r6 = r34
            int r7 = org.telegram.tgnet.ConnectionsManager.generateClassGuid()
            r0.classGuid = r7
            r7 = 0
            r0.currentMessageObject = r7
            r0.currentFileLocation = r7
            r0.currentSecureDocument = r7
            r0.currentPathObject = r7
            r8 = 0
            r0.fromCamera = r8
            r0.currentBotInlineResult = r7
            r9 = -1
            r0.currentIndex = r9
            java.lang.String[] r10 = r0.currentFileNames
            r10[r8] = r7
            r11 = 1
            java.lang.Integer r12 = java.lang.Integer.valueOf(r11)
            r10[r11] = r7
            r13 = 2
            r10[r13] = r7
            r0.avatarsDialogId = r8
            r0.totalImagesCount = r8
            r0.totalImagesCountMerge = r8
            r0.currentEditMode = r8
            r0.isFirstLoading = r11
            r0.needSearchImageInArr = r8
            r0.loadingMoreImages = r8
            boolean[] r10 = r0.endReached
            r10[r8] = r8
            long r14 = r0.mergeDialogId
            r16 = 0
            int r18 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x004f
            r14 = 1
            goto L_0x0050
        L_0x004f:
            r14 = 0
        L_0x0050:
            r10[r11] = r14
            r0.opennedFromMedia = r8
            r0.needCaptionLayout = r8
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r10 = r0.containerView
            r10.setTag(r12)
            r0.isCurrentVideo = r8
            java.util.ArrayList<org.telegram.messenger.MessageObject> r10 = r0.imagesArr
            r10.clear()
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r10 = r0.imagesArrLocations
            r10.clear()
            java.util.ArrayList<java.lang.Integer> r10 = r0.imagesArrLocationsSizes
            r10.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r10 = r0.avatarsArr
            r10.clear()
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r10 = r0.secureDocuments
            r10.clear()
            java.util.ArrayList<java.lang.Object> r10 = r0.imagesArrLocals
            r10.clear()
            r10 = 0
        L_0x007c:
            if (r10 >= r13) goto L_0x008f
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r14 = r0.imagesByIds
            r14 = r14[r10]
            r14.clear()
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r14 = r0.imagesByIdsTemp
            r14 = r14[r10]
            r14.clear()
            int r10 = r10 + 1
            goto L_0x007c
        L_0x008f:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r10 = r0.imagesArrTemp
            r10.clear()
            r0.currentUserAvatarLocation = r7
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r10 = r0.containerView
            r10.setPadding(r8, r8, r8, r8)
            org.telegram.messenger.ImageReceiver$BitmapHolder r10 = r0.currentThumb
            if (r10 == 0) goto L_0x00a2
            r10.release()
        L_0x00a2:
            if (r6 == 0) goto L_0x00a7
            org.telegram.messenger.ImageReceiver$BitmapHolder r10 = r6.thumb
            goto L_0x00a8
        L_0x00a7:
            r10 = r7
        L_0x00a8:
            r0.currentThumb = r10
            if (r6 == 0) goto L_0x00b2
            boolean r10 = r6.isEvent
            if (r10 == 0) goto L_0x00b2
            r10 = 1
            goto L_0x00b3
        L_0x00b2:
            r10 = 0
        L_0x00b3:
            r0.isEvent = r10
            r0.sharedMediaType = r8
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r10 = r0.allMediaItem
            r14 = 2131626669(0x7f0e0aad, float:1.888058E38)
            java.lang.String r15 = "ShowAllMedia"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r10.setText(r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.menuItem
            r10.setVisibility(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.sendItem
            r14 = 8
            r10.setVisibility(r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.pipItem
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.cameraItem
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.cameraItem
            r10.setTag(r7)
            android.widget.FrameLayout r10 = r0.bottomLayout
            r10.setVisibility(r8)
            android.widget.FrameLayout r10 = r0.bottomLayout
            r10.setTag(r12)
            android.widget.FrameLayout r10 = r0.bottomLayout
            r15 = 0
            r10.setTranslationY(r15)
            android.widget.TextView r10 = r0.captionTextView
            r10.setTranslationY(r15)
            android.widget.ImageView r10 = r0.shareButton
            r10.setVisibility(r14)
            org.telegram.ui.PhotoViewer$QualityChooseView r10 = r0.qualityChooseView
            r9 = 4
            if (r10 == 0) goto L_0x010c
            r10.setVisibility(r9)
            org.telegram.ui.Components.PickerBottomLayoutViewer r10 = r0.qualityPicker
            r10.setVisibility(r9)
            org.telegram.ui.PhotoViewer$QualityChooseView r10 = r0.qualityChooseView
            r10.setTag(r7)
        L_0x010c:
            android.animation.AnimatorSet r10 = r0.qualityChooseViewAnimation
            if (r10 == 0) goto L_0x0115
            r10.cancel()
            r0.qualityChooseViewAnimation = r7
        L_0x0115:
            r0.setDoubleTapEnabled(r11)
            r0.allowShare = r8
            r0.slideshowMessageId = r8
            r0.nameOverride = r7
            r0.dateOverride = r8
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.menuItem
            r10.hideSubItem(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.menuItem
            r10.hideSubItem(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.menuItem
            r13 = 10
            r10.hideSubItem(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.menuItem
            r13 = 11
            r10.hideSubItem(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.menuItem
            r13 = 14
            r10.hideSubItem(r13)
            org.telegram.ui.ActionBar.ActionBar r10 = r0.actionBar
            r10.setTranslationY(r15)
            org.telegram.ui.Components.CheckBox r10 = r0.checkImageView
            r13 = 1065353216(0x3var_, float:1.0)
            r10.setAlpha(r13)
            org.telegram.ui.Components.CheckBox r10 = r0.checkImageView
            r10.setVisibility(r14)
            org.telegram.ui.ActionBar.ActionBar r10 = r0.actionBar
            r10.setTitleRightMargin(r8)
            org.telegram.ui.PhotoViewer$CounterView r10 = r0.photosCounterView
            r10.setAlpha(r13)
            org.telegram.ui.PhotoViewer$CounterView r10 = r0.photosCounterView
            r10.setVisibility(r14)
            android.widget.FrameLayout r10 = r0.pickerView
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.pickerViewSendButton
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.pickerViewSendButton
            r10.setTranslationY(r15)
            android.widget.FrameLayout r10 = r0.pickerView
            r10.setAlpha(r13)
            android.widget.ImageView r10 = r0.pickerViewSendButton
            r10.setAlpha(r13)
            android.widget.FrameLayout r10 = r0.pickerView
            r10.setTranslationY(r15)
            android.widget.ImageView r10 = r0.paintItem
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.cropItem
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.tuneItem
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.timeItem
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.rotateItem
            r10.setVisibility(r14)
            android.widget.FrameLayout r10 = r0.pickerView
            android.view.ViewGroup$LayoutParams r10 = r10.getLayoutParams()
            r13 = -2
            r10.height = r13
            android.widget.TextView r10 = r0.docInfoTextView
            r10.setVisibility(r14)
            android.widget.TextView r10 = r0.docNameTextView
            r10.setVisibility(r14)
            org.telegram.ui.Components.VideoTimelinePlayView r10 = r0.videoTimelineView
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.compressItem
            r10.setVisibility(r14)
            org.telegram.ui.Components.PhotoViewerCaptionEnterView r10 = r0.captionEditText
            r10.setVisibility(r14)
            org.telegram.ui.Components.RecyclerListView r10 = r0.mentionListView
            r10.setVisibility(r14)
            android.widget.ImageView r10 = r0.muteItem
            r10.setVisibility(r14)
            org.telegram.ui.ActionBar.ActionBar r10 = r0.actionBar
            r10.setSubtitle(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.masksItem
            r10.setVisibility(r14)
            r0.muteVideo = r8
            android.widget.ImageView r10 = r0.muteItem
            r13 = 2131165954(0x7var_, float:1.794614E38)
            r10.setImageResource(r13)
            org.telegram.ui.Components.PickerBottomLayoutViewer r10 = r0.editorDoneLayout
            r10.setVisibility(r14)
            android.widget.TextView r10 = r0.captionTextView
            r10.setTag(r7)
            android.widget.TextView r10 = r0.captionTextView
            r10.setVisibility(r9)
            org.telegram.ui.Components.PhotoCropView r10 = r0.photoCropView
            if (r10 == 0) goto L_0x01ec
            r10.setVisibility(r14)
        L_0x01ec:
            org.telegram.ui.Components.PhotoFilterView r10 = r0.photoFilterView
            if (r10 == 0) goto L_0x01f3
            r10.setVisibility(r14)
        L_0x01f3:
            r10 = 0
        L_0x01f4:
            r13 = 3
            if (r10 >= r13) goto L_0x0209
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r13 = r0.photoProgressViews
            r22 = r13[r10]
            if (r22 == 0) goto L_0x0204
            r13 = r13[r10]
            r15 = -1
            r13.setBackgroundState(r15, r8)
            goto L_0x0205
        L_0x0204:
            r15 = -1
        L_0x0205:
            int r10 = r10 + 1
            r15 = 0
            goto L_0x01f4
        L_0x0209:
            r10 = 2131626668(0x7f0e0aac, float:1.8880579E38)
            java.lang.String r13 = "ShowAllFiles"
            if (r1 == 0) goto L_0x0309
            if (r2 != 0) goto L_0x0309
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r3 == 0) goto L_0x02a1
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            if (r2 == 0) goto L_0x02a1
            java.lang.String r3 = r2.site_name
            if (r3 == 0) goto L_0x02a1
            java.lang.String r3 = r3.toLowerCase()
            java.lang.String r4 = "instagram"
            boolean r4 = r3.equals(r4)
            if (r4 != 0) goto L_0x0241
            java.lang.String r4 = "twitter"
            boolean r3 = r3.equals(r4)
            if (r3 != 0) goto L_0x0241
            java.lang.String r3 = r2.type
            java.lang.String r4 = "telegram_album"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x02a1
        L_0x0241:
            java.lang.String r3 = r2.author
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x024d
            java.lang.String r3 = r2.author
            r0.nameOverride = r3
        L_0x024d:
            org.telegram.tgnet.TLRPC$Page r3 = r2.cached_page
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_page
            if (r3 == 0) goto L_0x0276
            r3 = 0
        L_0x0254:
            org.telegram.tgnet.TLRPC$Page r4 = r2.cached_page
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r4 = r4.blocks
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0276
            org.telegram.tgnet.TLRPC$Page r4 = r2.cached_page
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r4 = r4.blocks
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$PageBlock r4 = (org.telegram.tgnet.TLRPC.PageBlock) r4
            boolean r6 = r4 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockAuthorDate
            if (r6 == 0) goto L_0x0273
            org.telegram.tgnet.TLRPC$TL_pageBlockAuthorDate r4 = (org.telegram.tgnet.TLRPC.TL_pageBlockAuthorDate) r4
            int r2 = r4.published_date
            r0.dateOverride = r2
            goto L_0x0276
        L_0x0273:
            int r3 = r3 + 1
            goto L_0x0254
        L_0x0276:
            java.util.ArrayList r2 = r1.getWebPagePhotos(r7, r7)
            boolean r3 = r2.isEmpty()
            if (r3 != 0) goto L_0x02a1
            int r3 = r27.getId()
            r0.slideshowMessageId = r3
            r0.needSearchImageInArr = r8
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            r3.addAll(r2)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArr
            int r2 = r2.size()
            r0.totalImagesCount = r2
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArr
            int r2 = r2.indexOf(r1)
            if (r2 >= 0) goto L_0x029e
            r2 = 0
        L_0x029e:
            r0.setImageIndex(r2, r11)
        L_0x02a1:
            boolean r2 = r27.canPreviewDocument()
            if (r2 == 0) goto L_0x02b2
            r0.sharedMediaType = r11
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r0.allMediaItem
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r13, r10)
            r2.setText(r3)
        L_0x02b2:
            int r2 = r0.slideshowMessageId
            if (r2 != 0) goto L_0x0313
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArr
            r2.add(r1)
            long r2 = r1.eventId
            int r4 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r4 == 0) goto L_0x02c4
            r0.needSearchImageInArr = r8
            goto L_0x0305
        L_0x02c4:
            org.telegram.ui.Components.AnimatedFileDrawable r2 = r0.currentAnimation
            if (r2 == 0) goto L_0x02d0
            r0.needSearchImageInArr = r8
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.sendItem
            r1.setVisibility(r8)
            goto L_0x0305
        L_0x02d0:
            boolean r2 = r1.scheduled
            if (r2 != 0) goto L_0x0305
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r2.media
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice
            if (r4 != 0) goto L_0x0305
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r3 != 0) goto L_0x0305
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            if (r2 == 0) goto L_0x02e8
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty
            if (r2 == 0) goto L_0x0305
        L_0x02e8:
            r0.needSearchImageInArr = r11
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r2 = r0.imagesByIds
            r2 = r2[r8]
            int r3 = r27.getId()
            r2.put(r3, r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.showSubItem(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r2 = 2
            r1.showSubItem(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.sendItem
            r1.setVisibility(r8)
        L_0x0305:
            r0.setImageIndex(r8, r11)
            goto L_0x0313
        L_0x0309:
            if (r3 == 0) goto L_0x0316
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r1 = r0.secureDocuments
            r1.addAll(r3)
            r0.setImageIndex(r5, r11)
        L_0x0313:
            r3 = r7
            goto L_0x05d2
        L_0x0316:
            if (r28 == 0) goto L_0x03a0
            int r1 = r6.dialogId
            r0.avatarsDialogId = r1
            if (r29 != 0) goto L_0x034d
            int r1 = r0.avatarsDialogId
            if (r1 <= 0) goto L_0x0337
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r0.avatarsDialogId
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForUser(r1, r11)
            goto L_0x034f
        L_0x0337:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = r0.avatarsDialogId
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForChat(r1, r11)
            goto L_0x034f
        L_0x034d:
            r1 = r29
        L_0x034f:
            if (r1 != 0) goto L_0x0352
            return
        L_0x0352:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r2 = r0.imagesArrLocations
            r2.add(r1)
            r0.currentUserAvatarLocation = r1
            java.util.ArrayList<java.lang.Integer> r1 = r0.imagesArrLocationsSizes
            int r2 = r6.size
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r1.add(r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r1 = r0.avatarsArr
            org.telegram.tgnet.TLRPC$TL_photoEmpty r2 = new org.telegram.tgnet.TLRPC$TL_photoEmpty
            r2.<init>()
            r1.add(r2)
            android.widget.ImageView r1 = r0.shareButton
            android.widget.FrameLayout r2 = r0.videoPlayerControlFrameLayout
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x0379
            r14 = 0
        L_0x0379:
            r1.setVisibility(r14)
            r0.allowShare = r11
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r2 = 2
            r1.hideSubItem(r2)
            android.widget.ImageView r1 = r0.shareButton
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0394
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r2 = 10
            r1.hideSubItem(r2)
            goto L_0x039b
        L_0x0394:
            r2 = 10
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.showSubItem(r2)
        L_0x039b:
            r0.setImageIndex(r8, r11)
            goto L_0x0313
        L_0x03a0:
            if (r2 == 0) goto L_0x040d
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            r1.addAll(r2)
            r1 = 0
        L_0x03a8:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArr
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x03d5
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArr
            java.lang.Object r2 = r2.get(r1)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r3 = r0.imagesByIds
            long r14 = r2.getDialogId()
            long r7 = r0.currentDialogId
            int r4 = (r14 > r7 ? 1 : (r14 == r7 ? 0 : -1))
            if (r4 != 0) goto L_0x03c6
            r4 = 0
            goto L_0x03c7
        L_0x03c6:
            r4 = 1
        L_0x03c7:
            r3 = r3[r4]
            int r4 = r2.getId()
            r3.put(r4, r2)
            int r1 = r1 + 1
            r7 = 0
            r8 = 0
            goto L_0x03a8
        L_0x03d5:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            java.lang.Object r1 = r1.get(r5)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            boolean r2 = r1.scheduled
            if (r2 != 0) goto L_0x0400
            r0.opennedFromMedia = r11
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.menuItem
            r2.showSubItem(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.sendItem
            r3 = 0
            r2.setVisibility(r3)
            boolean r1 = r1.canPreviewDocument()
            if (r1 == 0) goto L_0x0408
            r0.sharedMediaType = r11
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r0.allMediaItem
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r13, r10)
            r1.setText(r2)
            goto L_0x0408
        L_0x0400:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r1 = r1.size()
            r0.totalImagesCount = r1
        L_0x0408:
            r0.setImageIndex(r5, r11)
            goto L_0x05d1
        L_0x040d:
            if (r4 == 0) goto L_0x05d1
            int r1 = r0.sendPhotoType
            r2 = 5
            if (r1 == 0) goto L_0x0421
            if (r1 == r9) goto L_0x0421
            r3 = 2
            if (r1 == r3) goto L_0x041b
            if (r1 != r2) goto L_0x0437
        L_0x041b:
            int r1 = r32.size()
            if (r1 <= r11) goto L_0x0437
        L_0x0421:
            org.telegram.ui.Components.CheckBox r1 = r0.checkImageView
            r3 = 0
            r1.setVisibility(r3)
            org.telegram.ui.PhotoViewer$CounterView r1 = r0.photosCounterView
            r1.setVisibility(r3)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r3 = 1120403456(0x42CLASSNAME, float:100.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.setTitleRightMargin(r3)
        L_0x0437:
            int r1 = r0.sendPhotoType
            r3 = 2
            if (r1 == r3) goto L_0x043e
            if (r1 != r2) goto L_0x0451
        L_0x043e:
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r1 = r0.placeProvider
            boolean r1 = r1.canCaptureMorePhotos()
            if (r1 == 0) goto L_0x0451
            android.widget.ImageView r1 = r0.cameraItem
            r3 = 0
            r1.setVisibility(r3)
            android.widget.ImageView r1 = r0.cameraItem
            r1.setTag(r12)
        L_0x0451:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.setVisibility(r14)
            java.util.ArrayList<java.lang.Object> r1 = r0.imagesArrLocals
            r1.addAll(r4)
            java.util.ArrayList<java.lang.Object> r1 = r0.imagesArrLocals
            java.lang.Object r1 = r1.get(r5)
            boolean r3 = r1 instanceof org.telegram.messenger.MediaController.PhotoEntry
            if (r3 == 0) goto L_0x04e4
            int r3 = r0.sendPhotoType
            r4 = 10
            if (r3 != r4) goto L_0x0476
            android.widget.ImageView r1 = r0.cropItem
            r1.setVisibility(r14)
            android.widget.ImageView r1 = r0.rotateItem
            r1.setVisibility(r14)
            goto L_0x04df
        L_0x0476:
            boolean r4 = r0.isDocumentsPicker
            if (r4 == 0) goto L_0x049e
            android.widget.ImageView r1 = r0.cropItem
            r1.setVisibility(r14)
            android.widget.ImageView r1 = r0.rotateItem
            r1.setVisibility(r14)
            android.widget.TextView r1 = r0.docInfoTextView
            r3 = 0
            r1.setVisibility(r3)
            android.widget.TextView r1 = r0.docNameTextView
            r1.setVisibility(r3)
            android.widget.FrameLayout r1 = r0.pickerView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            r3 = 1118306304(0x42a80000, float:84.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.height = r3
            goto L_0x04df
        L_0x049e:
            org.telegram.messenger.MediaController$PhotoEntry r1 = (org.telegram.messenger.MediaController.PhotoEntry) r1
            boolean r1 = r1.isVideo
            if (r1 == 0) goto L_0x04c7
            android.widget.ImageView r1 = r0.cropItem
            r1.setVisibility(r14)
            android.widget.ImageView r1 = r0.rotateItem
            r1.setVisibility(r14)
            android.widget.FrameLayout r1 = r0.bottomLayout
            r3 = 0
            r1.setVisibility(r3)
            android.widget.FrameLayout r1 = r0.bottomLayout
            r1.setTag(r12)
            android.widget.FrameLayout r1 = r0.bottomLayout
            r3 = 1111490560(0x42400000, float:48.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = -r3
            float r3 = (float) r3
            r1.setTranslationY(r3)
            goto L_0x04df
        L_0x04c7:
            android.widget.ImageView r1 = r0.cropItem
            if (r3 == r11) goto L_0x04cd
            r3 = 0
            goto L_0x04cf
        L_0x04cd:
            r3 = 8
        L_0x04cf:
            r1.setVisibility(r3)
            android.widget.ImageView r1 = r0.rotateItem
            int r3 = r0.sendPhotoType
            if (r3 == r11) goto L_0x04db
            r3 = 8
            goto L_0x04dc
        L_0x04db:
            r3 = 0
        L_0x04dc:
            r1.setVisibility(r3)
        L_0x04df:
            boolean r1 = r0.isDocumentsPicker
            r8 = r1 ^ 1
            goto L_0x0515
        L_0x04e4:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC.BotInlineResult
            if (r3 == 0) goto L_0x04f4
            android.widget.ImageView r1 = r0.cropItem
            r1.setVisibility(r14)
            android.widget.ImageView r1 = r0.rotateItem
            r1.setVisibility(r14)
        L_0x04f2:
            r8 = 0
            goto L_0x0515
        L_0x04f4:
            android.widget.ImageView r3 = r0.cropItem
            boolean r4 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r4 == 0) goto L_0x0502
            org.telegram.messenger.MediaController$SearchImage r1 = (org.telegram.messenger.MediaController.SearchImage) r1
            int r1 = r1.type
            if (r1 != 0) goto L_0x0502
            r1 = 0
            goto L_0x0504
        L_0x0502:
            r1 = 8
        L_0x0504:
            r3.setVisibility(r1)
            android.widget.ImageView r1 = r0.rotateItem
            r1.setVisibility(r14)
            android.widget.ImageView r1 = r0.cropItem
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x04f2
            r8 = 1
        L_0x0515:
            org.telegram.ui.ChatActivity r1 = r0.parentChatActivity
            if (r1 == 0) goto L_0x056d
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.currentEncryptedChat
            if (r1 == 0) goto L_0x0527
            int r1 = r1.layer
            int r1 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r1)
            r3 = 46
            if (r1 < r3) goto L_0x056d
        L_0x0527:
            org.telegram.ui.Adapters.MentionsAdapter r1 = r0.mentionsAdapter
            org.telegram.ui.ChatActivity r3 = r0.parentChatActivity
            org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
            r1.setChatInfo(r3)
            org.telegram.ui.Adapters.MentionsAdapter r1 = r0.mentionsAdapter
            org.telegram.ui.ChatActivity r3 = r0.parentChatActivity
            org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
            if (r3 == 0) goto L_0x053a
            r3 = 1
            goto L_0x053b
        L_0x053a:
            r3 = 0
        L_0x053b:
            r1.setNeedUsernames(r3)
            org.telegram.ui.Adapters.MentionsAdapter r1 = r0.mentionsAdapter
            r3 = 0
            r1.setNeedBotContext(r3)
            if (r8 == 0) goto L_0x0554
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r1 = r0.placeProvider
            if (r1 == 0) goto L_0x0552
            if (r1 == 0) goto L_0x0554
            boolean r1 = r1.allowCaption()
            if (r1 == 0) goto L_0x0554
        L_0x0552:
            r1 = 1
            goto L_0x0555
        L_0x0554:
            r1 = 0
        L_0x0555:
            r0.needCaptionLayout = r1
            org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = r0.captionEditText
            boolean r3 = r0.needCaptionLayout
            if (r3 == 0) goto L_0x055f
            r3 = 0
            goto L_0x0561
        L_0x055f:
            r3 = 8
        L_0x0561:
            r1.setVisibility(r3)
            boolean r1 = r0.needCaptionLayout
            if (r1 == 0) goto L_0x056d
            org.telegram.ui.Components.PhotoViewerCaptionEnterView r1 = r0.captionEditText
            r1.onCreate()
        L_0x056d:
            android.widget.FrameLayout r1 = r0.pickerView
            r3 = 0
            r1.setVisibility(r3)
            android.widget.ImageView r1 = r0.pickerViewSendButton
            r1.setVisibility(r3)
            android.widget.ImageView r1 = r0.pickerViewSendButton
            r3 = 0
            r1.setTranslationY(r3)
            android.widget.ImageView r1 = r0.pickerViewSendButton
            r3 = 1065353216(0x3var_, float:1.0)
            r1.setAlpha(r3)
            android.widget.FrameLayout r1 = r0.bottomLayout
            r1.setVisibility(r14)
            android.widget.FrameLayout r1 = r0.bottomLayout
            r3 = 0
            r1.setTag(r3)
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r1 = r0.containerView
            r1.setTag(r3)
            r0.setImageIndex(r5, r11)
            int r1 = r0.sendPhotoType
            if (r1 != r11) goto L_0x05a8
            android.widget.ImageView r1 = r0.paintItem
            r2 = 0
            r1.setVisibility(r2)
            android.widget.ImageView r1 = r0.tuneItem
            r1.setVisibility(r2)
            goto L_0x05cd
        L_0x05a8:
            if (r1 == r9) goto L_0x05c3
            if (r1 == r2) goto L_0x05c3
            android.widget.ImageView r1 = r0.paintItem
            android.widget.ImageView r2 = r0.cropItem
            int r2 = r2.getVisibility()
            r1.setVisibility(r2)
            android.widget.ImageView r1 = r0.tuneItem
            android.widget.ImageView r2 = r0.cropItem
            int r2 = r2.getVisibility()
            r1.setVisibility(r2)
            goto L_0x05cd
        L_0x05c3:
            android.widget.ImageView r1 = r0.paintItem
            r1.setVisibility(r14)
            android.widget.ImageView r1 = r0.tuneItem
            r1.setVisibility(r14)
        L_0x05cd:
            r26.updateSelectedCount()
            goto L_0x05d2
        L_0x05d1:
            r3 = 0
        L_0x05d2:
            org.telegram.ui.Components.AnimatedFileDrawable r1 = r0.currentAnimation
            if (r1 != 0) goto L_0x0642
            boolean r1 = r0.isEvent
            if (r1 != 0) goto L_0x0642
            long r1 = r0.currentDialogId
            int r4 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r4 == 0) goto L_0x0627
            int r1 = r0.totalImagesCount
            if (r1 != 0) goto L_0x0627
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            if (r1 == 0) goto L_0x0627
            boolean r1 = r1.scheduled
            if (r1 != 0) goto L_0x0627
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            long r6 = r0.currentDialogId
            int r2 = r0.sharedMediaType
            int r4 = r0.classGuid
            r8 = 1
            r27 = r1
            r28 = r6
            r30 = r2
            r31 = r4
            r32 = r8
            r27.getMediaCount(r28, r30, r31, r32)
            long r1 = r0.mergeDialogId
            int r4 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r4 == 0) goto L_0x0642
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            long r6 = r0.mergeDialogId
            int r2 = r0.sharedMediaType
            int r4 = r0.classGuid
            r8 = 1
            r27 = r1
            r28 = r6
            r30 = r2
            r31 = r4
            r32 = r8
            r27.getMediaCount(r28, r30, r31, r32)
            goto L_0x0642
        L_0x0627:
            int r1 = r0.avatarsDialogId
            if (r1 == 0) goto L_0x0642
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r19 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r1 = r0.avatarsDialogId
            r21 = 80
            r22 = 0
            r24 = 1
            int r2 = r0.classGuid
            r20 = r1
            r25 = r2
            r19.loadDialogPhotos(r20, r21, r22, r24, r25)
        L_0x0642:
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            if (r1 == 0) goto L_0x064f
            boolean r1 = r1.isVideo()
            if (r1 != 0) goto L_0x064d
            goto L_0x064f
        L_0x064d:
            r1 = 0
            goto L_0x0669
        L_0x064f:
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = r0.currentBotInlineResult
            if (r1 == 0) goto L_0x066e
            java.lang.String r1 = r1.type
            java.lang.String r2 = "video"
            boolean r1 = r1.equals(r2)
            if (r1 != 0) goto L_0x064d
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = r0.currentBotInlineResult
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isVideoDocument(r1)
            if (r1 == 0) goto L_0x066e
            goto L_0x064d
        L_0x0669:
            r0.onActionClick(r1)
            goto L_0x06e8
        L_0x066e:
            java.util.ArrayList<java.lang.Object> r1 = r0.imagesArrLocals
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x06e8
            java.util.ArrayList<java.lang.Object> r1 = r0.imagesArrLocals
            java.lang.Object r1 = r1.get(r5)
            org.telegram.ui.ChatActivity r2 = r0.parentChatActivity
            if (r2 == 0) goto L_0x0685
            org.telegram.tgnet.TLRPC$User r7 = r2.getCurrentUser()
            goto L_0x0686
        L_0x0685:
            r7 = r3
        L_0x0686:
            boolean r2 = r0.isDocumentsPicker
            if (r2 != 0) goto L_0x06b2
            org.telegram.ui.ChatActivity r2 = r0.parentChatActivity
            if (r2 == 0) goto L_0x06b2
            boolean r2 = r2.isSecretChat()
            if (r2 != 0) goto L_0x06b2
            org.telegram.ui.ChatActivity r2 = r0.parentChatActivity
            boolean r2 = r2.isInScheduleMode()
            if (r2 != 0) goto L_0x06b2
            if (r7 == 0) goto L_0x06b2
            boolean r2 = r7.bot
            if (r2 != 0) goto L_0x06b2
            boolean r2 = org.telegram.messenger.UserObject.isUserSelf(r7)
            if (r2 != 0) goto L_0x06b2
            org.telegram.ui.ChatActivity r2 = r0.parentChatActivity
            boolean r2 = r2.isEditingMessageMedia()
            if (r2 != 0) goto L_0x06b2
            r8 = 1
            goto L_0x06b3
        L_0x06b2:
            r8 = 0
        L_0x06b3:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.BotInlineResult
            if (r2 == 0) goto L_0x06b9
        L_0x06b7:
            r8 = 0
            goto L_0x06e0
        L_0x06b9:
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.PhotoEntry
            if (r2 == 0) goto L_0x06d3
            org.telegram.messenger.MediaController$PhotoEntry r1 = (org.telegram.messenger.MediaController.PhotoEntry) r1
            boolean r2 = r1.isVideo
            if (r2 == 0) goto L_0x06e0
            java.io.File r2 = new java.io.File
            java.lang.String r1 = r1.path
            r2.<init>(r1)
            android.net.Uri r1 = android.net.Uri.fromFile(r2)
            r2 = 0
            r0.preparePlayer(r1, r2, r2)
            goto L_0x06e0
        L_0x06d3:
            if (r8 == 0) goto L_0x06e0
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r2 == 0) goto L_0x06e0
            org.telegram.messenger.MediaController$SearchImage r1 = (org.telegram.messenger.MediaController.SearchImage) r1
            int r1 = r1.type
            if (r1 != 0) goto L_0x06b7
            r8 = 1
        L_0x06e0:
            if (r8 == 0) goto L_0x06e8
            android.widget.ImageView r1 = r0.timeItem
            r2 = 0
            r1.setVisibility(r2)
        L_0x06e8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onPhotoShow(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, org.telegram.messenger.ImageLocation, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, int, org.telegram.ui.PhotoViewer$PlaceProviderObject):void");
    }

    private void setDoubleTapEnabled(boolean z) {
        this.doubleTapEnabled = z;
        this.gestureDetector.setOnDoubleTapListener(z ? this : null);
    }

    public boolean isMuteVideo() {
        return this.muteVideo;
    }

    /* access modifiers changed from: private */
    public void setImages() {
        if (this.animationInProgress == 0) {
            setIndexToImage(this.centerImage, this.currentIndex);
            setIndexToImage(this.rightImage, this.currentIndex + 1);
            setIndexToImage(this.leftImage, this.currentIndex - 1);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x009e  */
    /* JADX WARNING: Removed duplicated region for block: B:284:0x06e9  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x06f7  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x0713  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x073b  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x07a7  */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x07a9  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x07b1  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x07b3  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x07bb  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x07bd  */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x07c6  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00bc  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00c9  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00ec  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0102  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0109  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x017f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setIsAboutToSwitchToIndex(int r27, boolean r28) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            if (r28 != 0) goto L_0x000b
            int r2 = r0.switchingToIndex
            if (r2 != r1) goto L_0x000b
            return
        L_0x000b:
            r0.switchingToIndex = r1
            java.lang.String r2 = r26.getFileName(r27)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            boolean r3 = r3.isEmpty()
            java.lang.String r4 = "AttachVideo"
            java.lang.String r6 = "AttachPhoto"
            java.lang.String r9 = "Of"
            r10 = 6
            java.lang.String r11 = ""
            r13 = 2
            r14 = 0
            r15 = 8
            r8 = 0
            if (r3 != 0) goto L_0x0477
            int r1 = r0.switchingToIndex
            if (r1 < 0) goto L_0x0476
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            int r3 = r3.size()
            if (r1 < r3) goto L_0x0035
            goto L_0x0476
        L_0x0035:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r3 = r0.switchingToIndex
            java.lang.Object r1 = r1.get(r3)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            boolean r3 = r1.isVideo()
            boolean r16 = r1.isInvoice()
            r7 = 11
            if (r16 == 0) goto L_0x007e
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.masksItem
            r2.setVisibility(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.menuItem
            r2.hideSubItem(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.menuItem
            r2.hideSubItem(r7)
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            java.lang.String r2 = r2.description
            r0.allowShare = r8
            android.widget.FrameLayout r3 = r0.bottomLayout
            r7 = 1111490560(0x42400000, float:48.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r3.setTranslationY(r7)
            android.widget.TextView r3 = r0.captionTextView
            r7 = 1111490560(0x42400000, float:48.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r3.setTranslationY(r7)
            r17 = r6
            goto L_0x01df
        L_0x007e:
            org.telegram.ui.ActionBar.ActionBarMenuItem r12 = r0.masksItem
            boolean r17 = r1.hasPhotoStickers()
            if (r17 == 0) goto L_0x0091
            r17 = r6
            long r5 = r1.getDialogId()
            int r6 = (int) r5
            if (r6 == 0) goto L_0x0093
            r5 = 0
            goto L_0x0095
        L_0x0091:
            r17 = r6
        L_0x0093:
            r5 = 8
        L_0x0095:
            r12.setVisibility(r5)
            boolean r5 = r1.isNewGif()
            if (r5 == 0) goto L_0x00a5
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.menuItem
            r6 = 14
            r5.showSubItem(r6)
        L_0x00a5:
            org.telegram.ui.ChatActivity r5 = r0.parentChatActivity
            if (r5 == 0) goto L_0x00b1
            boolean r5 = r5.isInScheduleMode()
            if (r5 == 0) goto L_0x00b1
            r5 = 1
            goto L_0x00b2
        L_0x00b1:
            r5 = 0
        L_0x00b2:
            boolean r5 = r1.canDeleteMessage(r5, r14)
            if (r5 == 0) goto L_0x00c2
            int r5 = r0.slideshowMessageId
            if (r5 != 0) goto L_0x00c2
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.menuItem
            r5.showSubItem(r10)
            goto L_0x00c7
        L_0x00c2:
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.menuItem
            r5.hideSubItem(r10)
        L_0x00c7:
            if (r3 == 0) goto L_0x00ec
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.menuItem
            r5.showSubItem(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pipItem
            int r5 = r5.getVisibility()
            if (r5 == 0) goto L_0x00db
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pipItem
            r5.setVisibility(r8)
        L_0x00db:
            boolean r5 = r0.pipAvailable
            if (r5 != 0) goto L_0x00fe
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pipItem
            r5.setEnabled(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pipItem
            r6 = 1056964608(0x3var_, float:0.5)
            r5.setAlpha(r6)
            goto L_0x00fe
        L_0x00ec:
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.menuItem
            r5.hideSubItem(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pipItem
            int r5 = r5.getVisibility()
            if (r5 == r15) goto L_0x00fe
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pipItem
            r5.setVisibility(r15)
        L_0x00fe:
            java.lang.String r5 = r0.nameOverride
            if (r5 == 0) goto L_0x0109
            android.widget.TextView r6 = r0.nameTextView
            r6.setText(r5)
            goto L_0x017a
        L_0x0109:
            boolean r5 = r1.isFromUser()
            if (r5 == 0) goto L_0x0133
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            org.telegram.tgnet.TLRPC$Message r6 = r1.messageOwner
            int r6 = r6.from_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            if (r5 == 0) goto L_0x012d
            android.widget.TextView r6 = r0.nameTextView
            java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r5)
            r6.setText(r5)
            goto L_0x017a
        L_0x012d:
            android.widget.TextView r5 = r0.nameTextView
            r5.setText(r11)
            goto L_0x017a
        L_0x0133:
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            org.telegram.tgnet.TLRPC$Message r6 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.to_id
            int r6 = r6.channel_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r6)
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r6 == 0) goto L_0x016b
            boolean r6 = r5.megagroup
            if (r6 == 0) goto L_0x016b
            boolean r6 = r1.isForwardedChannelPost()
            if (r6 == 0) goto L_0x016b
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            org.telegram.tgnet.TLRPC$Message r6 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            int r6 = r6.channel_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r6)
        L_0x016b:
            if (r5 == 0) goto L_0x0175
            android.widget.TextView r6 = r0.nameTextView
            java.lang.String r5 = r5.title
            r6.setText(r5)
            goto L_0x017a
        L_0x0175:
            android.widget.TextView r5 = r0.nameTextView
            r5.setText(r11)
        L_0x017a:
            int r5 = r0.dateOverride
            if (r5 == 0) goto L_0x017f
            goto L_0x0183
        L_0x017f:
            org.telegram.tgnet.TLRPC$Message r5 = r1.messageOwner
            int r5 = r5.date
        L_0x0183:
            long r5 = (long) r5
            r11 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 * r11
            r7 = 2131627343(0x7f0e0d4f, float:1.8881948E38)
            java.lang.Object[] r11 = new java.lang.Object[r13]
            org.telegram.messenger.LocaleController r12 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r12 = r12.formatterYear
            java.util.Date r15 = new java.util.Date
            r15.<init>(r5)
            java.lang.String r12 = r12.format((java.util.Date) r15)
            r11[r8] = r12
            org.telegram.messenger.LocaleController r12 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r12 = r12.formatterDay
            java.util.Date r15 = new java.util.Date
            r15.<init>(r5)
            java.lang.String r5 = r12.format((java.util.Date) r15)
            r6 = 1
            r11[r6] = r5
            java.lang.String r5 = "formatDateAtTime"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r7, r11)
            if (r2 == 0) goto L_0x01d8
            if (r3 == 0) goto L_0x01d8
            android.widget.TextView r2 = r0.dateTextView
            java.lang.Object[] r3 = new java.lang.Object[r13]
            r3[r8] = r5
            org.telegram.tgnet.TLRPC$Document r5 = r1.getDocument()
            int r5 = r5.size
            long r5 = (long) r5
            java.lang.String r5 = org.telegram.messenger.AndroidUtilities.formatFileSize(r5)
            r6 = 1
            r3[r6] = r5
            java.lang.String r5 = "%s (%s)"
            java.lang.String r3 = java.lang.String.format(r5, r3)
            r2.setText(r3)
            goto L_0x01dd
        L_0x01d8:
            android.widget.TextView r2 = r0.dateTextView
            r2.setText(r5)
        L_0x01dd:
            java.lang.CharSequence r2 = r1.caption
        L_0x01df:
            org.telegram.ui.Components.AnimatedFileDrawable r3 = r0.currentAnimation
            if (r3 == 0) goto L_0x0220
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r4 = 1
            r3.hideSubItem(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r4 = 10
            r3.hideSubItem(r4)
            org.telegram.ui.ChatActivity r3 = r0.parentChatActivity
            if (r3 == 0) goto L_0x01fc
            boolean r3 = r3.isInScheduleMode()
            if (r3 == 0) goto L_0x01fc
            r3 = 1
            goto L_0x01fd
        L_0x01fc:
            r3 = 0
        L_0x01fd:
            boolean r3 = r1.canDeleteMessage(r3, r14)
            if (r3 != 0) goto L_0x0208
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r3.hideSubItem(r10)
        L_0x0208:
            r3 = 1
            r0.allowShare = r3
            android.widget.ImageView r3 = r0.shareButton
            r3.setVisibility(r8)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r4 = 2131624279(0x7f0e0157, float:1.8875733E38)
            java.lang.String r5 = "AttachGif"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setTitle(r4)
            goto L_0x046e
        L_0x0220:
            int r3 = r0.totalImagesCount
            int r5 = r0.totalImagesCountMerge
            int r3 = r3 + r5
            if (r3 == 0) goto L_0x039a
            boolean r3 = r0.needSearchImageInArr
            if (r3 != 0) goto L_0x039a
            boolean r3 = r0.opennedFromMedia
            if (r3 == 0) goto L_0x02e8
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            int r3 = r3.size()
            int r4 = r0.totalImagesCount
            int r5 = r0.totalImagesCountMerge
            int r4 = r4 + r5
            if (r3 >= r4) goto L_0x02c3
            boolean r3 = r0.loadingMoreImages
            if (r3 != 0) goto L_0x02c3
            int r3 = r0.switchingToIndex
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            int r4 = r4.size()
            r5 = 5
            int r4 = r4 - r5
            if (r3 <= r4) goto L_0x02c3
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0256
            r3 = 0
            goto L_0x0268
        L_0x0256:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            int r4 = r3.size()
            r5 = 1
            int r4 = r4 - r5
            java.lang.Object r3 = r3.get(r4)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            int r3 = r3.getId()
        L_0x0268:
            boolean[] r4 = r0.endReached
            boolean r4 = r4[r8]
            if (r4 == 0) goto L_0x029e
            long r4 = r0.mergeDialogId
            r6 = 0
            int r10 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r10 == 0) goto L_0x029e
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x029a
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            int r5 = r4.size()
            r6 = 1
            int r5 = r5 - r6
            java.lang.Object r4 = r4.get(r5)
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            long r4 = r4.getDialogId()
            long r6 = r0.mergeDialogId
            int r10 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r10 == 0) goto L_0x029a
            r3 = 1
            r22 = 0
            goto L_0x02a1
        L_0x029a:
            r22 = r3
            r3 = 1
            goto L_0x02a1
        L_0x029e:
            r22 = r3
            r3 = 0
        L_0x02a1:
            int r4 = r0.currentAccount
            org.telegram.messenger.MediaDataController r18 = org.telegram.messenger.MediaDataController.getInstance(r4)
            if (r3 != 0) goto L_0x02ac
            long r3 = r0.currentDialogId
            goto L_0x02ae
        L_0x02ac:
            long r3 = r0.mergeDialogId
        L_0x02ae:
            r19 = r3
            r21 = 80
            int r3 = r0.sharedMediaType
            r24 = 1
            int r4 = r0.classGuid
            r23 = r3
            r25 = r4
            r18.loadMedia(r19, r21, r22, r23, r24, r25)
            r3 = 1
            r0.loadingMoreImages = r3
            goto L_0x02c4
        L_0x02c3:
            r3 = 1
        L_0x02c4:
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            java.lang.Object[] r5 = new java.lang.Object[r13]
            int r6 = r0.switchingToIndex
            int r6 = r6 + r3
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r8] = r6
            int r6 = r0.totalImagesCount
            int r7 = r0.totalImagesCountMerge
            int r6 = r6 + r7
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r3] = r6
            r3 = 2131625890(0x7f0e07a2, float:1.8879E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r9, r3, r5)
            r4.setTitle(r3)
            goto L_0x0412
        L_0x02e8:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            int r3 = r3.size()
            int r4 = r0.totalImagesCount
            int r5 = r0.totalImagesCountMerge
            int r4 = r4 + r5
            if (r3 >= r4) goto L_0x0368
            boolean r3 = r0.loadingMoreImages
            if (r3 != 0) goto L_0x0368
            int r3 = r0.switchingToIndex
            r4 = 5
            if (r3 >= r4) goto L_0x0368
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0308
            r3 = 0
            goto L_0x0314
        L_0x0308:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            java.lang.Object r3 = r3.get(r8)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            int r3 = r3.getId()
        L_0x0314:
            boolean[] r4 = r0.endReached
            boolean r4 = r4[r8]
            if (r4 == 0) goto L_0x0344
            long r4 = r0.mergeDialogId
            r6 = 0
            int r10 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r10 == 0) goto L_0x0344
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x0340
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            java.lang.Object r4 = r4.get(r8)
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            long r4 = r4.getDialogId()
            long r6 = r0.mergeDialogId
            int r10 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r10 == 0) goto L_0x0340
            r3 = 1
            r22 = 0
            goto L_0x0347
        L_0x0340:
            r22 = r3
            r3 = 1
            goto L_0x0347
        L_0x0344:
            r22 = r3
            r3 = 0
        L_0x0347:
            int r4 = r0.currentAccount
            org.telegram.messenger.MediaDataController r18 = org.telegram.messenger.MediaDataController.getInstance(r4)
            if (r3 != 0) goto L_0x0352
            long r3 = r0.currentDialogId
            goto L_0x0354
        L_0x0352:
            long r3 = r0.mergeDialogId
        L_0x0354:
            r19 = r3
            r21 = 80
            int r3 = r0.sharedMediaType
            r24 = 1
            int r4 = r0.classGuid
            r23 = r3
            r25 = r4
            r18.loadMedia(r19, r21, r22, r23, r24, r25)
            r3 = 1
            r0.loadingMoreImages = r3
        L_0x0368:
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.Object[] r4 = new java.lang.Object[r13]
            int r5 = r0.totalImagesCount
            int r6 = r0.totalImagesCountMerge
            int r5 = r5 + r6
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r0.imagesArr
            int r6 = r6.size()
            int r5 = r5 - r6
            int r6 = r0.switchingToIndex
            int r5 = r5 + r6
            r6 = 1
            int r5 = r5 + r6
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r8] = r5
            int r5 = r0.totalImagesCount
            int r7 = r0.totalImagesCountMerge
            int r5 = r5 + r7
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r6] = r5
            r5 = 2131625890(0x7f0e07a2, float:1.8879E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r5, r4)
            r3.setTitle(r4)
            goto L_0x0412
        L_0x039a:
            int r3 = r0.slideshowMessageId
            if (r3 != 0) goto L_0x03dd
            org.telegram.tgnet.TLRPC$Message r3 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r3 == 0) goto L_0x03dd
            boolean r3 = r1.canPreviewDocument()
            if (r3 == 0) goto L_0x03bb
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r4 = 2131624277(0x7f0e0155, float:1.887573E38)
            java.lang.String r5 = "AttachDocument"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setTitle(r4)
            goto L_0x0412
        L_0x03bb:
            boolean r3 = r1.isVideo()
            if (r3 == 0) goto L_0x03ce
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r5 = 2131624296(0x7f0e0168, float:1.8875768E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r5)
            r3.setTitle(r4)
            goto L_0x0412
        L_0x03ce:
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r5 = r17
            r4 = 2131624290(0x7f0e0162, float:1.8875756E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setTitle(r4)
            goto L_0x0412
        L_0x03dd:
            if (r16 == 0) goto L_0x03eb
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            org.telegram.tgnet.TLRPC$Message r4 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            java.lang.String r4 = r4.title
            r3.setTitle(r4)
            goto L_0x0412
        L_0x03eb:
            boolean r3 = r1.isVideo()
            if (r3 == 0) goto L_0x03fe
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r5 = 2131624296(0x7f0e0168, float:1.8875768E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r5)
            r3.setTitle(r4)
            goto L_0x0412
        L_0x03fe:
            org.telegram.tgnet.TLRPC$Document r3 = r1.getDocument()
            if (r3 == 0) goto L_0x0412
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r4 = 2131624277(0x7f0e0155, float:1.887573E38)
            java.lang.String r5 = "AttachDocument"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setTitle(r4)
        L_0x0412:
            long r3 = r0.currentDialogId
            int r4 = (int) r3
            if (r4 != 0) goto L_0x041e
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.sendItem
            r4 = 8
            r3.setVisibility(r4)
        L_0x041e:
            org.telegram.tgnet.TLRPC$Message r3 = r1.messageOwner
            int r3 = r3.ttl
            if (r3 == 0) goto L_0x043f
            r4 = 3600(0xe10, float:5.045E-42)
            if (r3 >= r4) goto L_0x043f
            r0.allowShare = r8
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r4 = 1
            r3.hideSubItem(r4)
            android.widget.ImageView r3 = r0.shareButton
            r5 = 8
            r3.setVisibility(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r5 = 10
            r3.hideSubItem(r5)
            goto L_0x046e
        L_0x043f:
            r4 = 1
            r0.allowShare = r4
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r3.showSubItem(r4)
            android.widget.ImageView r3 = r0.shareButton
            android.widget.FrameLayout r4 = r0.videoPlayerControlFrameLayout
            int r4 = r4.getVisibility()
            if (r4 == 0) goto L_0x0452
            goto L_0x0454
        L_0x0452:
            r8 = 8
        L_0x0454:
            r3.setVisibility(r8)
            android.widget.ImageView r3 = r0.shareButton
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x0467
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r4 = 10
            r3.hideSubItem(r4)
            goto L_0x046e
        L_0x0467:
            r4 = 10
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r3.showSubItem(r4)
        L_0x046e:
            org.telegram.ui.Components.GroupedPhotosListView r3 = r0.groupedPhotosListView
            r3.fillList()
            r14 = r1
            goto L_0x07ce
        L_0x0476:
            return
        L_0x0477:
            r5 = r6
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r2 = r0.secureDocuments
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x04b7
            r0.allowShare = r8
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r2 = 1
            r1.hideSubItem(r2)
            android.widget.TextView r1 = r0.nameTextView
            r1.setText(r11)
            android.widget.TextView r1 = r0.dateTextView
            r1.setText(r11)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            java.lang.Object[] r3 = new java.lang.Object[r13]
            int r4 = r0.switchingToIndex
            int r4 = r4 + r2
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r8] = r4
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r4 = r0.secureDocuments
            int r4 = r4.size()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r2] = r4
            r2 = 2131625890(0x7f0e07a2, float:1.8879E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r9, r2, r3)
            r1.setTitle(r2)
            goto L_0x07cd
        L_0x04b7:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r2 = r0.imagesArrLocations
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0562
            if (r1 < 0) goto L_0x0561
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r2 = r0.imagesArrLocations
            int r2 = r2.size()
            if (r1 < r2) goto L_0x04cb
            goto L_0x0561
        L_0x04cb:
            android.widget.TextView r1 = r0.nameTextView
            r1.setText(r11)
            android.widget.TextView r1 = r0.dateTextView
            r1.setText(r11)
            int r1 = r0.avatarsDialogId
            int r2 = r0.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.getClientUserId()
            if (r1 != r2) goto L_0x04f1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r1 = r0.avatarsArr
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x04f1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.showSubItem(r10)
            goto L_0x04f6
        L_0x04f1:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.hideSubItem(r10)
        L_0x04f6:
            boolean r1 = r0.isEvent
            if (r1 == 0) goto L_0x0508
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r2 = 2131624290(0x7f0e0162, float:1.8875756E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r1.setTitle(r2)
            r4 = 1
            goto L_0x052c
        L_0x0508:
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            java.lang.Object[] r2 = new java.lang.Object[r13]
            int r3 = r0.switchingToIndex
            r4 = 1
            int r3 = r3 + r4
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2[r8] = r3
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.imagesArrLocations
            int r3 = r3.size()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2[r4] = r3
            r3 = 2131625890(0x7f0e07a2, float:1.8879E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r9, r3, r2)
            r1.setTitle(r2)
        L_0x052c:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.showSubItem(r4)
            r0.allowShare = r4
            android.widget.ImageView r1 = r0.shareButton
            android.widget.FrameLayout r2 = r0.videoPlayerControlFrameLayout
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x053e
            goto L_0x0540
        L_0x053e:
            r8 = 8
        L_0x0540:
            r1.setVisibility(r8)
            android.widget.ImageView r1 = r0.shareButton
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0553
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r2 = 10
            r1.hideSubItem(r2)
            goto L_0x055a
        L_0x0553:
            r2 = 10
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.showSubItem(r2)
        L_0x055a:
            org.telegram.ui.Components.GroupedPhotosListView r1 = r0.groupedPhotosListView
            r1.fillList()
            goto L_0x07cd
        L_0x0561:
            return
        L_0x0562:
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x07cd
            if (r1 < 0) goto L_0x07cc
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            int r2 = r2.size()
            if (r1 < r2) goto L_0x0576
            goto L_0x07cc
        L_0x0576:
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            java.lang.Object r1 = r2.get(r1)
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.BotInlineResult
            if (r2 == 0) goto L_0x05a7
            r2 = r1
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = (org.telegram.tgnet.TLRPC.BotInlineResult) r2
            r0.currentBotInlineResult = r2
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            if (r3 == 0) goto L_0x058e
            boolean r2 = org.telegram.messenger.MessageObject.isVideoDocument(r3)
            goto L_0x059f
        L_0x058e:
            org.telegram.tgnet.TLRPC$WebDocument r3 = r2.content
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r3 == 0) goto L_0x059e
            java.lang.String r2 = r2.type
            java.lang.String r3 = "video"
            boolean r2 = r2.equals(r3)
            goto L_0x059f
        L_0x059e:
            r2 = 0
        L_0x059f:
            r3 = r2
        L_0x05a0:
            r6 = r14
            r2 = 0
            r7 = 0
            r10 = 0
            r11 = 0
            goto L_0x06df
        L_0x05a7:
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.PhotoEntry
            if (r2 == 0) goto L_0x05b6
            r3 = r1
            org.telegram.messenger.MediaController$PhotoEntry r3 = (org.telegram.messenger.MediaController.PhotoEntry) r3
            java.lang.String r6 = r3.path
            boolean r3 = r3.isVideo
            r10 = r6
            r6 = 0
            r7 = 1
            goto L_0x05ce
        L_0x05b6:
            boolean r3 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r3 == 0) goto L_0x05ca
            r3 = r1
            org.telegram.messenger.MediaController$SearchImage r3 = (org.telegram.messenger.MediaController.SearchImage) r3
            java.lang.String r6 = r3.getPathToAttach()
            int r3 = r3.type
            r7 = 1
            r10 = r6
            if (r3 != r7) goto L_0x05cc
            r3 = 0
            r6 = 1
            goto L_0x05ce
        L_0x05ca:
            r7 = 1
            r10 = r14
        L_0x05cc:
            r3 = 0
            r6 = 0
        L_0x05ce:
            if (r3 == 0) goto L_0x062c
            r0.isCurrentVideo = r7
            r26.updateAccessibilityOverlayVisibility()
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            if (r2 == 0) goto L_0x05e8
            r11 = r1
            org.telegram.messenger.MediaController$PhotoEntry r11 = (org.telegram.messenger.MediaController.PhotoEntry) r11
            org.telegram.messenger.VideoEditedInfo r11 = r11.editedInfo
            if (r11 == 0) goto L_0x05e8
            boolean r6 = r11.muted
            float r7 = r11.start
            float r11 = r11.end
            goto L_0x05ec
        L_0x05e8:
            r6 = 0
            r7 = 0
            r11 = 1065353216(0x3var_, float:1.0)
        L_0x05ec:
            r0.processOpenVideo(r10, r6, r7, r11)
            boolean r6 = r0.isDocumentsPicker
            if (r6 == 0) goto L_0x0605
            org.telegram.ui.Components.VideoTimelinePlayView r6 = r0.videoTimelineView
            r7 = 8
            r6.setVisibility(r7)
            android.widget.ImageView r6 = r0.muteItem
            r6.setVisibility(r7)
            android.widget.ImageView r6 = r0.compressItem
            r6.setVisibility(r7)
            goto L_0x0616
        L_0x0605:
            r7 = 8
            org.telegram.ui.Components.VideoTimelinePlayView r6 = r0.videoTimelineView
            r6.setVisibility(r8)
            android.widget.ImageView r6 = r0.muteItem
            r6.setVisibility(r8)
            android.widget.ImageView r6 = r0.compressItem
            r6.setVisibility(r8)
        L_0x0616:
            android.widget.ImageView r6 = r0.paintItem
            r6.setVisibility(r7)
            android.widget.ImageView r6 = r0.cropItem
            r6.setVisibility(r7)
            android.widget.ImageView r6 = r0.tuneItem
            r6.setVisibility(r7)
            android.widget.ImageView r6 = r0.rotateItem
            r6.setVisibility(r7)
            goto L_0x06a4
        L_0x062c:
            r7 = 8
            org.telegram.ui.Components.VideoTimelinePlayView r10 = r0.videoTimelineView
            r10.setVisibility(r7)
            android.widget.ImageView r10 = r0.muteItem
            r10.setVisibility(r7)
            r0.isCurrentVideo = r8
            r26.updateAccessibilityOverlayVisibility()
            android.widget.ImageView r10 = r0.compressItem
            r10.setVisibility(r7)
            if (r6 != 0) goto L_0x0689
            int r6 = r0.sendPhotoType
            r7 = 10
            if (r6 == r7) goto L_0x0689
            boolean r7 = r0.isDocumentsPicker
            if (r7 == 0) goto L_0x064f
            goto L_0x0689
        L_0x064f:
            r7 = 4
            if (r6 == r7) goto L_0x0661
            r7 = 5
            if (r6 != r7) goto L_0x0656
            goto L_0x0661
        L_0x0656:
            android.widget.ImageView r6 = r0.paintItem
            r6.setVisibility(r8)
            android.widget.ImageView r6 = r0.tuneItem
            r6.setVisibility(r8)
            goto L_0x066d
        L_0x0661:
            android.widget.ImageView r6 = r0.paintItem
            r7 = 8
            r6.setVisibility(r7)
            android.widget.ImageView r6 = r0.tuneItem
            r6.setVisibility(r7)
        L_0x066d:
            android.widget.ImageView r6 = r0.cropItem
            int r7 = r0.sendPhotoType
            r10 = 1
            if (r7 == r10) goto L_0x0676
            r7 = 0
            goto L_0x0678
        L_0x0676:
            r7 = 8
        L_0x0678:
            r6.setVisibility(r7)
            android.widget.ImageView r6 = r0.rotateItem
            int r7 = r0.sendPhotoType
            if (r7 == r10) goto L_0x0684
            r7 = 8
            goto L_0x0685
        L_0x0684:
            r7 = 0
        L_0x0685:
            r6.setVisibility(r7)
            goto L_0x069f
        L_0x0689:
            android.widget.ImageView r6 = r0.paintItem
            r7 = 8
            r6.setVisibility(r7)
            android.widget.ImageView r6 = r0.cropItem
            r6.setVisibility(r7)
            android.widget.ImageView r6 = r0.rotateItem
            r6.setVisibility(r7)
            android.widget.ImageView r6 = r0.tuneItem
            r6.setVisibility(r7)
        L_0x069f:
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            r6.setSubtitle(r14)
        L_0x06a4:
            if (r2 == 0) goto L_0x06ce
            r2 = r1
            org.telegram.messenger.MediaController$PhotoEntry r2 = (org.telegram.messenger.MediaController.PhotoEntry) r2
            int r6 = r2.bucketId
            if (r6 != 0) goto L_0x06c0
            long r6 = r2.dateTaken
            r10 = 0
            int r12 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x06c0
            java.util.ArrayList<java.lang.Object> r6 = r0.imagesArrLocals
            int r6 = r6.size()
            r7 = 1
            if (r6 != r7) goto L_0x06c0
            r6 = 1
            goto L_0x06c1
        L_0x06c0:
            r6 = 0
        L_0x06c1:
            r0.fromCamera = r6
            java.lang.CharSequence r6 = r2.caption
            int r7 = r2.ttl
            boolean r10 = r2.isFiltered
            boolean r11 = r2.isPainted
            boolean r2 = r2.isCropped
            goto L_0x06df
        L_0x06ce:
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r2 == 0) goto L_0x05a0
            r2 = r1
            org.telegram.messenger.MediaController$SearchImage r2 = (org.telegram.messenger.MediaController.SearchImage) r2
            java.lang.CharSequence r6 = r2.caption
            int r7 = r2.ttl
            boolean r10 = r2.isFiltered
            boolean r11 = r2.isPainted
            boolean r2 = r2.isCropped
        L_0x06df:
            android.widget.FrameLayout r12 = r0.bottomLayout
            int r12 = r12.getVisibility()
            r15 = 8
            if (r12 == r15) goto L_0x06ee
            android.widget.FrameLayout r12 = r0.bottomLayout
            r12.setVisibility(r15)
        L_0x06ee:
            android.widget.FrameLayout r12 = r0.bottomLayout
            r12.setTag(r14)
            boolean r12 = r0.fromCamera
            if (r12 == 0) goto L_0x0713
            if (r3 == 0) goto L_0x0706
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r5 = 2131624296(0x7f0e0168, float:1.8875768E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r5)
            r3.setTitle(r4)
            goto L_0x0737
        L_0x0706:
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r4 = 2131624290(0x7f0e0162, float:1.8875756E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setTitle(r4)
            goto L_0x0737
        L_0x0713:
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.Object[] r4 = new java.lang.Object[r13]
            int r5 = r0.switchingToIndex
            r12 = 1
            int r5 = r5 + r12
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r8] = r5
            java.util.ArrayList<java.lang.Object> r5 = r0.imagesArrLocals
            int r5 = r5.size()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r12] = r5
            r5 = 2131625890(0x7f0e07a2, float:1.8879E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r5, r4)
            r3.setTitle(r4)
        L_0x0737:
            org.telegram.ui.ChatActivity r3 = r0.parentChatActivity
            if (r3 == 0) goto L_0x0771
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getCurrentChat()
            if (r3 == 0) goto L_0x0749
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            java.lang.String r3 = r3.title
            r4.setTitle(r3)
            goto L_0x0771
        L_0x0749:
            org.telegram.ui.ChatActivity r3 = r0.parentChatActivity
            org.telegram.tgnet.TLRPC$User r3 = r3.getCurrentUser()
            if (r3 == 0) goto L_0x0771
            boolean r4 = r3.self
            if (r4 == 0) goto L_0x0764
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r4 = 2131626489(0x7f0e09f9, float:1.8880216E38)
            java.lang.String r5 = "SavedMessages"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setTitle(r4)
            goto L_0x0771
        L_0x0764:
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            java.lang.String r5 = r3.first_name
            java.lang.String r3 = r3.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r5, r3)
            r4.setTitle(r3)
        L_0x0771:
            int r3 = r0.sendPhotoType
            if (r3 == 0) goto L_0x0786
            r4 = 4
            if (r3 == r4) goto L_0x0786
            if (r3 == r13) goto L_0x077d
            r4 = 5
            if (r3 != r4) goto L_0x0793
        L_0x077d:
            java.util.ArrayList<java.lang.Object> r3 = r0.imagesArrLocals
            int r3 = r3.size()
            r4 = 1
            if (r3 <= r4) goto L_0x0793
        L_0x0786:
            org.telegram.ui.Components.CheckBox r3 = r0.checkImageView
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r4 = r0.placeProvider
            int r5 = r0.switchingToIndex
            boolean r4 = r4.isPhotoChecked(r5)
            r3.setChecked(r4, r8)
        L_0x0793:
            r0.updateCaptionTextForCurrentPhoto(r1)
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r3 = "dialogFloatingButton"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r3, r4)
            android.widget.ImageView r3 = r0.timeItem
            if (r7 == 0) goto L_0x07a9
            r4 = r1
            goto L_0x07aa
        L_0x07a9:
            r4 = r14
        L_0x07aa:
            r3.setColorFilter(r4)
            android.widget.ImageView r3 = r0.paintItem
            if (r11 == 0) goto L_0x07b3
            r4 = r1
            goto L_0x07b4
        L_0x07b3:
            r4 = r14
        L_0x07b4:
            r3.setColorFilter(r4)
            android.widget.ImageView r3 = r0.cropItem
            if (r2 == 0) goto L_0x07bd
            r2 = r1
            goto L_0x07be
        L_0x07bd:
            r2 = r14
        L_0x07be:
            r3.setColorFilter(r2)
            android.widget.ImageView r2 = r0.tuneItem
            if (r10 == 0) goto L_0x07c6
            goto L_0x07c7
        L_0x07c6:
            r1 = r14
        L_0x07c7:
            r2.setColorFilter(r1)
            r2 = r6
            goto L_0x07ce
        L_0x07cc:
            return
        L_0x07cd:
            r2 = r14
        L_0x07ce:
            r1 = 1
            r1 = r28 ^ 1
            r0.setCurrentCaption(r14, r2, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.setIsAboutToSwitchToIndex(int, boolean):void");
    }

    /* access modifiers changed from: private */
    public TLRPC.FileLocation getFileLocation(ImageLocation imageLocation) {
        if (imageLocation == null) {
            return null;
        }
        return imageLocation.location;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0076, code lost:
        r2 = r0.currentMessageObject;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setImageIndex(int r17, boolean r18) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r2 = r18
            int r3 = r0.currentIndex
            if (r3 == r1) goto L_0x033b
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r3 = r0.placeProvider
            if (r3 != 0) goto L_0x0010
            goto L_0x033b
        L_0x0010:
            r3 = 0
            if (r2 != 0) goto L_0x001c
            org.telegram.messenger.ImageReceiver$BitmapHolder r4 = r0.currentThumb
            if (r4 == 0) goto L_0x001c
            r4.release()
            r0.currentThumb = r3
        L_0x001c:
            java.lang.String[] r4 = r0.currentFileNames
            java.lang.String r5 = r16.getFileName(r17)
            r6 = 0
            r4[r6] = r5
            java.lang.String[] r4 = r0.currentFileNames
            int r5 = r1 + 1
            java.lang.String r5 = r0.getFileName(r5)
            r7 = 1
            r4[r7] = r5
            java.lang.String[] r4 = r0.currentFileNames
            int r5 = r1 + -1
            java.lang.String r5 = r0.getFileName(r5)
            r8 = 2
            r4[r8] = r5
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r4 = r0.placeProvider
            org.telegram.messenger.MessageObject r5 = r0.currentMessageObject
            org.telegram.messenger.ImageLocation r9 = r0.currentFileLocation
            org.telegram.tgnet.TLRPC$FileLocation r9 = r0.getFileLocation(r9)
            int r10 = r0.currentIndex
            r4.willSwitchFromPhoto(r5, r9, r10)
            int r4 = r0.currentIndex
            r0.currentIndex = r1
            int r5 = r0.currentIndex
            r0.setIsAboutToSwitchToIndex(r5, r2)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            boolean r5 = r5.isEmpty()
            r9 = 0
            if (r5 != 0) goto L_0x00b1
            int r1 = r0.currentIndex
            if (r1 < 0) goto L_0x00ad
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            int r5 = r5.size()
            if (r1 < r5) goto L_0x006a
            goto L_0x00ad
        L_0x006a:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r5 = r0.currentIndex
            java.lang.Object r1 = r1.get(r5)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r2 == 0) goto L_0x0086
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            if (r2 == 0) goto L_0x0086
            int r2 = r2.getId()
            int r5 = r1.getId()
            if (r2 != r5) goto L_0x0086
            r2 = 1
            goto L_0x0087
        L_0x0086:
            r2 = 0
        L_0x0087:
            r0.currentMessageObject = r1
            boolean r5 = r1.isVideo()
            int r11 = r0.sharedMediaType
            if (r11 != r7) goto L_0x00aa
            boolean r1 = r1.canPreviewDocument()
            r0.canZoom = r1
            if (r1 == 0) goto L_0x00a2
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.showSubItem(r7)
            r0.setDoubleTapEnabled(r7)
            goto L_0x00aa
        L_0x00a2:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.hideSubItem(r7)
            r0.setDoubleTapEnabled(r6)
        L_0x00aa:
            r13 = r4
            goto L_0x0235
        L_0x00ad:
            r0.closePhoto(r6, r6)
            return
        L_0x00b1:
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r5 = r0.secureDocuments
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x00d4
            if (r1 < 0) goto L_0x00d0
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r2 = r0.secureDocuments
            int r2 = r2.size()
            if (r1 < r2) goto L_0x00c4
            goto L_0x00d0
        L_0x00c4:
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r2 = r0.secureDocuments
            java.lang.Object r1 = r2.get(r1)
            org.telegram.messenger.SecureDocument r1 = (org.telegram.messenger.SecureDocument) r1
            r0.currentSecureDocument = r1
            goto L_0x0231
        L_0x00d0:
            r0.closePhoto(r6, r6)
            return
        L_0x00d4:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r5 = r0.imagesArrLocations
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x011d
            if (r1 < 0) goto L_0x0119
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r5 = r0.imagesArrLocations
            int r5 = r5.size()
            if (r1 < r5) goto L_0x00e7
            goto L_0x0119
        L_0x00e7:
            org.telegram.messenger.ImageLocation r5 = r0.currentFileLocation
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r11 = r0.imagesArrLocations
            java.lang.Object r11 = r11.get(r1)
            org.telegram.messenger.ImageLocation r11 = (org.telegram.messenger.ImageLocation) r11
            if (r2 == 0) goto L_0x010b
            if (r5 == 0) goto L_0x010b
            if (r11 == 0) goto L_0x010b
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r2 = r5.location
            int r5 = r2.local_id
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r11 = r11.location
            int r12 = r11.local_id
            if (r5 != r12) goto L_0x010b
            long r12 = r2.volume_id
            long r14 = r11.volume_id
            int r2 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r2 != 0) goto L_0x010b
            r2 = 1
            goto L_0x010c
        L_0x010b:
            r2 = 0
        L_0x010c:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r5 = r0.imagesArrLocations
            java.lang.Object r1 = r5.get(r1)
            org.telegram.messenger.ImageLocation r1 = (org.telegram.messenger.ImageLocation) r1
            r0.currentFileLocation = r1
            r13 = r4
            goto L_0x0234
        L_0x0119:
            r0.closePhoto(r6, r6)
            return
        L_0x011d:
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0231
            if (r1 < 0) goto L_0x022d
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            int r2 = r2.size()
            if (r1 < r2) goto L_0x0131
            goto L_0x022d
        L_0x0131:
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            java.lang.Object r1 = r2.get(r1)
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.BotInlineResult
            if (r2 == 0) goto L_0x0187
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = (org.telegram.tgnet.TLRPC.BotInlineResult) r1
            r0.currentBotInlineResult = r1
            org.telegram.tgnet.TLRPC$Document r2 = r1.document
            if (r2 == 0) goto L_0x0154
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r2)
            java.lang.String r2 = r2.getAbsolutePath()
            r0.currentPathObject = r2
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isVideoDocument(r1)
            goto L_0x0182
        L_0x0154:
            org.telegram.tgnet.TLRPC$Photo r2 = r1.photo
            if (r2 == 0) goto L_0x016d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r2.sizes
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r2)
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1)
            java.lang.String r1 = r1.getAbsolutePath()
            r0.currentPathObject = r1
            goto L_0x0181
        L_0x016d:
            org.telegram.tgnet.TLRPC$WebDocument r2 = r1.content
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r5 == 0) goto L_0x0181
            java.lang.String r2 = r2.url
            r0.currentPathObject = r2
            java.lang.String r1 = r1.type
            java.lang.String r2 = "video"
            boolean r1 = r1.equals(r2)
            goto L_0x0182
        L_0x0181:
            r1 = 0
        L_0x0182:
            r5 = r1
            r13 = r4
        L_0x0184:
            r2 = 0
            goto L_0x0235
        L_0x0187:
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.PhotoEntry
            if (r2 == 0) goto L_0x021f
            org.telegram.messenger.MediaController$PhotoEntry r1 = (org.telegram.messenger.MediaController.PhotoEntry) r1
            java.lang.String r2 = r1.path
            r0.currentPathObject = r2
            java.lang.String r5 = r0.currentPathObject
            if (r5 != 0) goto L_0x0199
            r0.closePhoto(r6, r6)
            return
        L_0x0199:
            boolean r5 = r1.isVideo
            java.io.File r11 = new java.io.File
            r11.<init>(r2)
            android.net.Uri r2 = android.net.Uri.fromFile(r11)
            boolean r12 = r0.isDocumentsPicker
            if (r12 == 0) goto L_0x021b
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            int r13 = r1.width
            java.lang.String r14 = ", "
            if (r13 == 0) goto L_0x01dd
            int r13 = r1.height
            if (r13 == 0) goto L_0x01dd
            int r13 = r12.length()
            if (r13 <= 0) goto L_0x01c0
            r12.append(r14)
        L_0x01c0:
            java.util.Locale r13 = java.util.Locale.US
            java.lang.Object[] r15 = new java.lang.Object[r8]
            int r8 = r1.width
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r15[r6] = r8
            int r8 = r1.height
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r15[r7] = r8
            java.lang.String r8 = "%dx%d"
            java.lang.String r8 = java.lang.String.format(r13, r8, r15)
            r12.append(r8)
        L_0x01dd:
            boolean r8 = r1.isVideo
            if (r8 == 0) goto L_0x01f3
            int r8 = r12.length()
            if (r8 <= 0) goto L_0x01ea
            r12.append(r14)
        L_0x01ea:
            int r8 = r1.duration
            java.lang.String r8 = org.telegram.messenger.AndroidUtilities.formatShortDuration(r8)
            r12.append(r8)
        L_0x01f3:
            r13 = r4
            long r3 = r1.size
            int r15 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r15 == 0) goto L_0x020c
            int r3 = r12.length()
            if (r3 <= 0) goto L_0x0203
            r12.append(r14)
        L_0x0203:
            long r3 = r1.size
            java.lang.String r1 = org.telegram.messenger.AndroidUtilities.formatFileSize(r3)
            r12.append(r1)
        L_0x020c:
            android.widget.TextView r1 = r0.docNameTextView
            java.lang.String r3 = r11.getName()
            r1.setText(r3)
            android.widget.TextView r1 = r0.docInfoTextView
            r1.setText(r12)
            goto L_0x021c
        L_0x021b:
            r13 = r4
        L_0x021c:
            r3 = r2
            goto L_0x0184
        L_0x021f:
            r13 = r4
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r2 == 0) goto L_0x0232
            org.telegram.messenger.MediaController$SearchImage r1 = (org.telegram.messenger.MediaController.SearchImage) r1
            java.lang.String r1 = r1.getPathToAttach()
            r0.currentPathObject = r1
            goto L_0x0232
        L_0x022d:
            r0.closePhoto(r6, r6)
            return
        L_0x0231:
            r13 = r4
        L_0x0232:
            r2 = 0
            r3 = 0
        L_0x0234:
            r5 = 0
        L_0x0235:
            org.telegram.ui.PhotoViewer$PlaceProviderObject r1 = r0.currentPlaceObject
            if (r1 == 0) goto L_0x0245
            int r4 = r0.animationInProgress
            if (r4 != 0) goto L_0x0243
            org.telegram.messenger.ImageReceiver r1 = r1.imageReceiver
            r1.setVisible(r7, r7)
            goto L_0x0245
        L_0x0243:
            r0.showAfterAnimation = r1
        L_0x0245:
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r1 = r0.placeProvider
            org.telegram.messenger.MessageObject r4 = r0.currentMessageObject
            org.telegram.messenger.ImageLocation r11 = r0.currentFileLocation
            org.telegram.tgnet.TLRPC$FileLocation r11 = r0.getFileLocation(r11)
            int r12 = r0.currentIndex
            org.telegram.ui.PhotoViewer$PlaceProviderObject r1 = r1.getPlaceForPhoto(r4, r11, r12, r6)
            r0.currentPlaceObject = r1
            org.telegram.ui.PhotoViewer$PlaceProviderObject r1 = r0.currentPlaceObject
            if (r1 == 0) goto L_0x0267
            int r4 = r0.animationInProgress
            if (r4 != 0) goto L_0x0265
            org.telegram.messenger.ImageReceiver r1 = r1.imageReceiver
            r1.setVisible(r6, r7)
            goto L_0x0267
        L_0x0265:
            r0.hideAfterAnimation = r1
        L_0x0267:
            if (r2 != 0) goto L_0x02d1
            r0.draggingDown = r6
            r1 = 0
            r0.translationX = r1
            r0.translationY = r1
            r2 = 1065353216(0x3var_, float:1.0)
            r0.scale = r2
            r0.animateToX = r1
            r0.animateToY = r1
            r0.animateToScale = r2
            r0.animationStartTime = r9
            r4 = 0
            r0.imageMoveAnimation = r4
            r0.changeModeAnimation = r4
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r4 = r0.aspectRatioFrameLayout
            if (r4 == 0) goto L_0x0289
            r8 = 4
            r4.setVisibility(r8)
        L_0x0289:
            r0.pinchStartDistance = r1
            r0.pinchStartScale = r2
            r0.pinchCenterX = r1
            r0.pinchCenterY = r1
            r0.pinchStartX = r1
            r0.pinchStartY = r1
            r0.moveStartX = r1
            r0.moveStartY = r1
            r0.zooming = r6
            r0.moving = r6
            r0.doubleTap = r6
            r0.invalidCoords = r6
            r0.canDragDown = r7
            r0.changingPage = r6
            r0.switchImageAfterAnimation = r6
            int r1 = r0.sharedMediaType
            if (r1 == r7) goto L_0x02c9
            java.util.ArrayList<java.lang.Object> r1 = r0.imagesArrLocals
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x02c6
            java.lang.String[] r1 = r0.currentFileNames
            r1 = r1[r6]
            if (r1 == 0) goto L_0x02c4
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r1 = r0.photoProgressViews
            r1 = r1[r6]
            int r1 = r1.backgroundState
            if (r1 == 0) goto L_0x02c4
            goto L_0x02c6
        L_0x02c4:
            r1 = 0
            goto L_0x02c7
        L_0x02c6:
            r1 = 1
        L_0x02c7:
            r0.canZoom = r1
        L_0x02c9:
            float r1 = r0.scale
            r0.updateMinMax(r1)
            r0.releasePlayer(r6)
        L_0x02d1:
            if (r5 == 0) goto L_0x02da
            if (r3 == 0) goto L_0x02da
            r0.isStreaming = r6
            r0.preparePlayer(r3, r6, r6)
        L_0x02da:
            r1 = -1
            r2 = r13
            if (r2 != r1) goto L_0x02eb
            r16.setImages()
            r1 = 0
        L_0x02e2:
            r2 = 3
            if (r1 >= r2) goto L_0x033b
            r0.checkProgress(r1, r6, r6)
            int r1 = r1 + 1
            goto L_0x02e2
        L_0x02eb:
            r0.checkProgress(r6, r7, r6)
            int r1 = r0.currentIndex
            if (r2 <= r1) goto L_0x0316
            org.telegram.messenger.ImageReceiver r2 = r0.rightImage
            org.telegram.messenger.ImageReceiver r3 = r0.centerImage
            r0.rightImage = r3
            org.telegram.messenger.ImageReceiver r3 = r0.leftImage
            r0.centerImage = r3
            r0.leftImage = r2
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r2 = r0.photoProgressViews
            r3 = r2[r6]
            r4 = 2
            r5 = r2[r4]
            r2[r6] = r5
            r2[r4] = r3
            org.telegram.messenger.ImageReceiver r2 = r0.leftImage
            int r1 = r1 - r7
            r0.setIndexToImage(r2, r1)
            r0.checkProgress(r7, r7, r6)
            r0.checkProgress(r4, r7, r6)
            goto L_0x033b
        L_0x0316:
            if (r2 >= r1) goto L_0x033b
            org.telegram.messenger.ImageReceiver r2 = r0.leftImage
            org.telegram.messenger.ImageReceiver r3 = r0.centerImage
            r0.leftImage = r3
            org.telegram.messenger.ImageReceiver r3 = r0.rightImage
            r0.centerImage = r3
            r0.rightImage = r2
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r2 = r0.photoProgressViews
            r3 = r2[r6]
            r4 = r2[r7]
            r2[r6] = r4
            r2[r7] = r3
            org.telegram.messenger.ImageReceiver r2 = r0.rightImage
            int r1 = r1 + r7
            r0.setIndexToImage(r2, r1)
            r0.checkProgress(r7, r7, r6)
            r1 = 2
            r0.checkProgress(r1, r7, r6)
        L_0x033b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.setImageIndex(int, boolean):void");
    }

    private void setCurrentCaption(MessageObject messageObject, CharSequence charSequence, boolean z) {
        CharSequence charSequence2;
        MessageObject messageObject2 = messageObject;
        if (this.needCaptionLayout) {
            if (this.captionTextView.getParent() != this.pickerView) {
                this.captionTextView.setBackgroundDrawable((Drawable) null);
                this.containerView.removeView(this.captionTextView);
                this.pickerView.addView(this.captionTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 76.0f, 48.0f));
            }
        } else if (this.captionTextView.getParent() != this.containerView) {
            this.captionTextView.setBackgroundColor(NUM);
            this.pickerView.removeView(this.captionTextView);
            this.containerView.addView(this.captionTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        }
        if (this.isCurrentVideo) {
            if (this.captionTextView.getMaxLines() != 1) {
                this.captionTextView.setMaxLines(1);
            }
            if (!this.isSingleLine) {
                TextView textView = this.captionTextView;
                this.isSingleLine = true;
                textView.setSingleLine(true);
            }
        } else {
            if (this.isSingleLine) {
                TextView textView2 = this.captionTextView;
                this.isSingleLine = false;
                textView2.setSingleLine(false);
            }
            Point point = AndroidUtilities.displaySize;
            int i = point.x > point.y ? 5 : 10;
            if (this.captionTextView.getMaxLines() != i) {
                this.captionTextView.setMaxLines(i);
            }
        }
        boolean z2 = this.captionTextView.getTag() != null;
        if (!TextUtils.isEmpty(charSequence)) {
            Theme.createChatResources((Context) null, true);
            if (messageObject2 == null || messageObject2.messageOwner.entities.isEmpty()) {
                charSequence2 = Emoji.replaceEmoji(new SpannableStringBuilder(charSequence), this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            } else {
                SpannableString valueOf = SpannableString.valueOf(charSequence.toString());
                messageObject2.addEntitiesToText(valueOf, true, false);
                if (messageObject.isVideo()) {
                    MessageObject.addUrlsByPattern(messageObject.isOutOwner(), valueOf, false, 3, messageObject.getDuration(), false);
                }
                charSequence2 = Emoji.replaceEmoji(valueOf, this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.captionTextView.setTag(charSequence2);
            AnimatorSet animatorSet = this.currentCaptionAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentCaptionAnimation = null;
            }
            try {
                this.captionTextView.setText(charSequence2);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.captionTextView.setScrollY(0);
            this.captionTextView.setTextColor(-1);
            if (this.isActionBarVisible && (this.bottomLayout.getVisibility() == 0 || this.pickerView.getVisibility() == 0)) {
                this.captionTextView.setVisibility(0);
                if (!z || z2) {
                    this.captionTextView.setAlpha(1.0f);
                    return;
                }
                this.currentCaptionAnimation = new AnimatorSet();
                this.currentCaptionAnimation.setDuration(200);
                this.currentCaptionAnimation.setInterpolator(decelerateInterpolator);
                this.currentCaptionAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(PhotoViewer.this.currentCaptionAnimation)) {
                            AnimatorSet unused = PhotoViewer.this.currentCaptionAnimation = null;
                        }
                    }
                });
                this.currentCaptionAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.captionTextView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.captionTextView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(5.0f), 0.0f})});
                this.currentCaptionAnimation.start();
            } else if (this.captionTextView.getVisibility() == 0) {
                this.captionTextView.setVisibility(4);
                this.captionTextView.setAlpha(0.0f);
            }
        } else if (this.needCaptionLayout) {
            this.captionTextView.setText(LocaleController.getString("AddCaption", NUM));
            this.captionTextView.setTag("empty");
            this.captionTextView.setVisibility(0);
            this.captionTextView.setTextColor(-NUM);
        } else {
            this.captionTextView.setTextColor(-1);
            this.captionTextView.setTag((Object) null);
            if (!z || !z2) {
                this.captionTextView.setVisibility(4);
                return;
            }
            this.currentCaptionAnimation = new AnimatorSet();
            this.currentCaptionAnimation.setDuration(200);
            this.currentCaptionAnimation.setInterpolator(decelerateInterpolator);
            this.currentCaptionAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(PhotoViewer.this.currentCaptionAnimation)) {
                        PhotoViewer.this.captionTextView.setVisibility(4);
                        AnimatorSet unused = PhotoViewer.this.currentCaptionAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(PhotoViewer.this.currentCaptionAnimation)) {
                        AnimatorSet unused = PhotoViewer.this.currentCaptionAnimation = null;
                    }
                }
            });
            this.currentCaptionAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.captionTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.captionTextView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(5.0f)})});
            this.currentCaptionAnimation.start();
        }
    }

    private void checkProgress(int i, boolean z, boolean z2) {
        boolean z3;
        File file;
        File file2;
        File file3;
        File file4;
        TLRPC.WebPage webPage;
        int i2 = this.currentIndex;
        boolean z4 = true;
        if (i == 1) {
            i2++;
        } else if (i == 2) {
            i2--;
        }
        boolean z5 = false;
        if (this.currentFileNames[i] != null) {
            File file5 = null;
            if (this.currentMessageObject != null) {
                if (i2 < 0 || i2 >= this.imagesArr.size()) {
                    this.photoProgressViews[i].setBackgroundState(-1, z2);
                    return;
                }
                MessageObject messageObject = this.imagesArr.get(i2);
                if (this.sharedMediaType != 1 || messageObject.canPreviewDocument()) {
                    File file6 = !TextUtils.isEmpty(messageObject.messageOwner.attachPath) ? new File(messageObject.messageOwner.attachPath) : null;
                    TLRPC.MessageMedia messageMedia = messageObject.messageOwner.media;
                    if (!(messageMedia instanceof TLRPC.TL_messageMediaWebPage) || (webPage = messageMedia.webpage) == null || webPage.document != null) {
                        file4 = FileLoader.getPathToMessage(messageObject.messageOwner);
                    } else {
                        file4 = FileLoader.getPathToAttach(getFileLocation(i2, (int[]) null), true);
                    }
                    if (!SharedConfig.streamMedia || !messageObject.isVideo() || !messageObject.canStreamVideo() || ((int) messageObject.getDialogId()) == 0) {
                        z4 = false;
                    }
                    z5 = z4;
                    z3 = messageObject.isVideo();
                    file2 = file6;
                    file = file4;
                } else {
                    this.photoProgressViews[i].setBackgroundState(-1, z2);
                    return;
                }
            } else if (this.currentBotInlineResult == null) {
                if (this.currentFileLocation != null) {
                    if (i2 < 0 || i2 >= this.imagesArrLocations.size()) {
                        this.photoProgressViews[i].setBackgroundState(-1, z2);
                        return;
                    }
                    TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = this.imagesArrLocations.get(i2).location;
                    if (this.avatarsDialogId == 0 && !this.isEvent) {
                        z4 = false;
                    }
                    file3 = FileLoader.getPathToAttach(tL_fileLocationToBeDeprecated, z4);
                } else if (this.currentSecureDocument == null) {
                    if (this.currentPathObject != null) {
                        file2 = new File(FileLoader.getDirectory(3), this.currentFileNames[i]);
                        file = new File(FileLoader.getDirectory(4), this.currentFileNames[i]);
                    } else {
                        file2 = null;
                        file = null;
                    }
                    z3 = false;
                } else if (i2 < 0 || i2 >= this.secureDocuments.size()) {
                    this.photoProgressViews[i].setBackgroundState(-1, z2);
                    return;
                } else {
                    file3 = FileLoader.getPathToAttach(this.secureDocuments.get(i2), true);
                }
                file2 = file3;
                file = null;
                z3 = false;
            } else if (i2 < 0 || i2 >= this.imagesArrLocals.size()) {
                this.photoProgressViews[i].setBackgroundState(-1, z2);
                return;
            } else {
                TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) this.imagesArrLocals.get(i2);
                if (botInlineResult.type.equals("video") || MessageObject.isVideoDocument(botInlineResult.document)) {
                    TLRPC.Document document = botInlineResult.document;
                    if (document != null) {
                        file5 = FileLoader.getPathToAttach(document);
                    } else if (botInlineResult.content instanceof TLRPC.TL_webDocument) {
                        file5 = new File(FileLoader.getDirectory(4), Utilities.MD5(botInlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.content.url, "mp4"));
                    }
                } else {
                    if (botInlineResult.document != null) {
                        file5 = new File(FileLoader.getDirectory(3), this.currentFileNames[i]);
                    } else if (botInlineResult.photo != null) {
                        file5 = new File(FileLoader.getDirectory(0), this.currentFileNames[i]);
                    }
                    z4 = false;
                }
                file = new File(FileLoader.getDirectory(4), this.currentFileNames[i]);
                z3 = z4;
                file2 = file5;
            }
            Utilities.globalQueue.postRunnable(new Runnable(file2, file, z5, z3, i, z2) {
                private final /* synthetic */ File f$1;
                private final /* synthetic */ File f$2;
                private final /* synthetic */ boolean f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ int f$5;
                private final /* synthetic */ boolean f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void run() {
                    PhotoViewer.this.lambda$checkProgress$45$PhotoViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            });
            return;
        }
        if (!this.imagesArrLocals.isEmpty() && i2 >= 0 && i2 < this.imagesArrLocals.size()) {
            Object obj = this.imagesArrLocals.get(i2);
            if (obj instanceof MediaController.PhotoEntry) {
                z5 = ((MediaController.PhotoEntry) obj).isVideo;
            }
        }
        if (z5) {
            this.photoProgressViews[i].setBackgroundState(3, z2);
        } else {
            this.photoProgressViews[i].setBackgroundState(-1, z2);
        }
    }

    public /* synthetic */ void lambda$checkProgress$45$PhotoViewer(File file, File file2, boolean z, boolean z2, int i, boolean z3) {
        boolean exists = file != null ? file.exists() : false;
        if (!exists && file2 != null) {
            exists = file2.exists();
        }
        AndroidUtilities.runOnUIThread(new Runnable(file, file2, exists, z, z2, i, z3) {
            private final /* synthetic */ File f$1;
            private final /* synthetic */ File f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ boolean f$5;
            private final /* synthetic */ int f$6;
            private final /* synthetic */ boolean f$7;

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
                PhotoViewer.this.lambda$null$44$PhotoViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$null$44$PhotoViewer(File file, File file2, boolean z, boolean z2, boolean z3, int i, boolean z4) {
        boolean z5 = true;
        if (!(file == null && file2 == null) && (z || z2)) {
            if (z3) {
                this.photoProgressViews[i].setBackgroundState(3, z4);
            } else {
                this.photoProgressViews[i].setBackgroundState(-1, z4);
            }
            if (i == 0) {
                if (z) {
                    this.menuItem.hideSubItem(7);
                } else if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[i])) {
                    this.menuItem.hideSubItem(7);
                } else {
                    this.menuItem.showSubItem(7);
                }
            }
        } else {
            if (!z3) {
                this.photoProgressViews[i].setBackgroundState(0, z4);
            } else if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[i])) {
                this.photoProgressViews[i].setBackgroundState(2, false);
            } else {
                this.photoProgressViews[i].setBackgroundState(1, false);
            }
            Float fileProgress = ImageLoader.getInstance().getFileProgress(this.currentFileNames[i]);
            if (fileProgress == null) {
                fileProgress = Float.valueOf(0.0f);
            }
            this.photoProgressViews[i].setProgress(fileProgress.floatValue(), false);
        }
        if (i == 0) {
            if (this.imagesArrLocals.isEmpty() && (this.currentFileNames[0] == null || this.photoProgressViews[0].backgroundState == 0)) {
                z5 = false;
            }
            this.canZoom = z5;
        }
    }

    public int getSelectiongLength() {
        PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
        if (photoViewerCaptionEnterView != null) {
            return photoViewerCaptionEnterView.getSelectionLength();
        }
        return 0;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v2, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v59, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v61, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v63, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v64, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v65, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v67, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v12, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v13, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v0, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v71, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v11, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v27, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v22, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v23, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v28, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v89, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v30, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v31, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v34, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v31, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX WARNING: type inference failed for: r2v13, types: [org.telegram.tgnet.TLRPC$Chat] */
    /* JADX WARNING: type inference failed for: r2v16, types: [org.telegram.tgnet.TLRPC$User] */
    /* JADX WARNING: type inference failed for: r2v66, types: [org.telegram.tgnet.TLRPC$Photo] */
    /* JADX WARNING: type inference failed for: r12v22, types: [org.telegram.tgnet.TLRPC$PhotoSize] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setIndexToImage(org.telegram.messenger.ImageReceiver r22, int r23) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            r2 = r23
            r3 = 0
            r1.setOrientation(r3, r3)
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r4 = r0.secureDocuments
            boolean r4 = r4.isEmpty()
            r5 = 0
            if (r4 != 0) goto L_0x0066
            if (r2 < 0) goto L_0x0491
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r3 = r0.secureDocuments
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0491
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r3 = r0.secureDocuments
            r3.get(r2)
            org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r3 = org.telegram.messenger.AndroidUtilities.density
            org.telegram.messenger.ImageReceiver$BitmapHolder r3 = r0.currentThumb
            if (r3 == 0) goto L_0x0030
            org.telegram.messenger.ImageReceiver r4 = r0.centerImage
            if (r1 != r4) goto L_0x0030
            goto L_0x0031
        L_0x0030:
            r3 = r5
        L_0x0031:
            if (r3 != 0) goto L_0x0039
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r3 = r0.placeProvider
            org.telegram.messenger.ImageReceiver$BitmapHolder r3 = r3.getThumbForPhoto(r5, r5, r2)
        L_0x0039:
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r4 = r0.secureDocuments
            java.lang.Object r2 = r4.get(r2)
            org.telegram.messenger.SecureDocument r2 = (org.telegram.messenger.SecureDocument) r2
            org.telegram.tgnet.TLRPC$TL_secureFile r4 = r2.secureFile
            int r7 = r4.size
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForSecureDocument(r2)
            r4 = 0
            r6 = 0
            if (r3 == 0) goto L_0x0054
            android.graphics.drawable.BitmapDrawable r5 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r3 = r3.bitmap
            r5.<init>(r3)
        L_0x0054:
            r8 = r5
            r9 = 0
            r10 = 0
            r11 = 0
            java.lang.String r3 = "d"
            r1 = r22
            r5 = r6
            r6 = r8
            r8 = r9
            r9 = r10
            r10 = r11
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x0491
        L_0x0066:
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            boolean r4 = r4.isEmpty()
            r6 = 2
            java.lang.String r7 = "%d_%d"
            r8 = 1
            if (r4 != 0) goto L_0x02d7
            if (r2 < 0) goto L_0x02d2
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            int r4 = r4.size()
            if (r2 >= r4) goto L_0x02d2
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            java.lang.Object r9 = r4.get(r2)
            int r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r4 = (float) r4
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r10
            int r4 = (int) r4
            org.telegram.messenger.ImageReceiver$BitmapHolder r10 = r0.currentThumb
            if (r10 == 0) goto L_0x0094
            org.telegram.messenger.ImageReceiver r11 = r0.centerImage
            if (r1 != r11) goto L_0x0094
            goto L_0x0095
        L_0x0094:
            r10 = r5
        L_0x0095:
            if (r10 != 0) goto L_0x009d
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r10 = r0.placeProvider
            org.telegram.messenger.ImageReceiver$BitmapHolder r10 = r10.getThumbForPhoto(r5, r5, r2)
        L_0x009d:
            boolean r2 = r9 instanceof org.telegram.messenger.MediaController.PhotoEntry
            r11 = 90
            java.lang.String r12 = "d"
            if (r2 == 0) goto L_0x0100
            r2 = r9
            org.telegram.messenger.MediaController$PhotoEntry r2 = (org.telegram.messenger.MediaController.PhotoEntry) r2
            boolean r12 = r2.isVideo
            if (r12 != 0) goto L_0x00cd
            java.lang.String r13 = r2.imagePath
            if (r13 == 0) goto L_0x00b1
            goto L_0x00b8
        L_0x00b1:
            int r13 = r2.orientation
            r1.setOrientation(r13, r3)
            java.lang.String r13 = r2.path
        L_0x00b8:
            java.util.Locale r2 = java.util.Locale.US
            java.lang.Object[] r14 = new java.lang.Object[r6]
            java.lang.Integer r15 = java.lang.Integer.valueOf(r4)
            r14[r3] = r15
            java.lang.Integer r15 = java.lang.Integer.valueOf(r4)
            r14[r8] = r15
            java.lang.String r2 = java.lang.String.format(r2, r7, r14)
            goto L_0x00f1
        L_0x00cd:
            java.lang.String r13 = r2.thumbPath
            if (r13 == 0) goto L_0x00d2
            goto L_0x00f0
        L_0x00d2:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "vthumb://"
            r13.append(r14)
            int r14 = r2.imageId
            r13.append(r14)
            java.lang.String r14 = ":"
            r13.append(r14)
            java.lang.String r2 = r2.path
            r13.append(r2)
            java.lang.String r13 = r13.toString()
        L_0x00f0:
            r2 = r5
        L_0x00f1:
            r15 = r2
            r2 = r5
            r14 = r2
            r17 = r12
            r16 = r13
            r18 = 0
            r19 = 0
            r12 = r14
            r13 = r12
            goto L_0x0212
        L_0x0100:
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.BotInlineResult
            if (r2 == 0) goto L_0x01c3
            r2 = r9
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = (org.telegram.tgnet.TLRPC.BotInlineResult) r2
            java.lang.String r13 = r2.type
            java.lang.String r14 = "video"
            boolean r13 = r13.equals(r14)
            if (r13 != 0) goto L_0x0190
            org.telegram.tgnet.TLRPC$Document r13 = r2.document
            boolean r13 = org.telegram.messenger.MessageObject.isVideoDocument(r13)
            if (r13 == 0) goto L_0x011c
            goto L_0x0190
        L_0x011c:
            java.lang.String r13 = r2.type
            java.lang.String r14 = "gif"
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x0134
            org.telegram.tgnet.TLRPC$Document r13 = r2.document
            if (r13 == 0) goto L_0x0134
            int r2 = r13.size
            r16 = r2
            r2 = r5
            r14 = r2
            r15 = r12
        L_0x0131:
            r12 = r14
            goto L_0x01b4
        L_0x0134:
            org.telegram.tgnet.TLRPC$Photo r13 = r2.photo
            if (r13 == 0) goto L_0x0161
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r12 = r13.sizes
            int r13 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r12 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r12, r13)
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            int r13 = r12.size
            java.util.Locale r14 = java.util.Locale.US
            java.lang.Object[] r15 = new java.lang.Object[r6]
            java.lang.Integer r16 = java.lang.Integer.valueOf(r4)
            r15[r3] = r16
            java.lang.Integer r16 = java.lang.Integer.valueOf(r4)
            r15[r8] = r16
            java.lang.String r14 = java.lang.String.format(r14, r7, r15)
            r16 = r13
            r15 = r14
            r14 = r2
            r2 = r5
            r13 = r2
            goto L_0x01b4
        L_0x0161:
            org.telegram.tgnet.TLRPC$WebDocument r13 = r2.content
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r13 == 0) goto L_0x01ad
            java.lang.String r13 = r2.type
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x0170
            goto L_0x0184
        L_0x0170:
            java.util.Locale r12 = java.util.Locale.US
            java.lang.Object[] r13 = new java.lang.Object[r6]
            java.lang.Integer r14 = java.lang.Integer.valueOf(r4)
            r13[r3] = r14
            java.lang.Integer r14 = java.lang.Integer.valueOf(r4)
            r13[r8] = r14
            java.lang.String r12 = java.lang.String.format(r12, r7, r13)
        L_0x0184:
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.content
            org.telegram.messenger.WebFile r2 = org.telegram.messenger.WebFile.createWithWebDocument(r2)
            r13 = r5
            r14 = r13
            r15 = r12
            r16 = 0
            goto L_0x0131
        L_0x0190:
            org.telegram.tgnet.TLRPC$Document r12 = r2.document
            if (r12 == 0) goto L_0x01a1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r12 = r12.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r12 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r12, r11)
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            r14 = r2
            r2 = r5
            r13 = r2
            r15 = r13
            goto L_0x01b2
        L_0x01a1:
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.thumb
            boolean r12 = r2 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r12 == 0) goto L_0x01ad
            org.telegram.messenger.WebFile r2 = org.telegram.messenger.WebFile.createWithWebDocument(r2)
            r12 = r5
            goto L_0x01af
        L_0x01ad:
            r2 = r5
            r12 = r2
        L_0x01af:
            r13 = r12
            r14 = r13
            r15 = r14
        L_0x01b2:
            r16 = 0
        L_0x01b4:
            r18 = r16
            r17 = 0
            r19 = 1
            r16 = r5
            r20 = r12
            r12 = r2
            r2 = r20
            goto L_0x0212
        L_0x01c3:
            boolean r2 = r9 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r2 == 0) goto L_0x0205
            r2 = r9
            org.telegram.messenger.MediaController$SearchImage r2 = (org.telegram.messenger.MediaController.SearchImage) r2
            org.telegram.tgnet.TLRPC$PhotoSize r13 = r2.photoSize
            if (r13 == 0) goto L_0x01d9
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            int r14 = r13.size
            r15 = r5
            r16 = r14
            r14 = r2
            r2 = r13
            r13 = r15
            goto L_0x01fa
        L_0x01d9:
            java.lang.String r13 = r2.imagePath
            if (r13 == 0) goto L_0x01e4
            r2 = r5
            r14 = r2
            r15 = r13
            r16 = 0
        L_0x01e2:
            r13 = r14
            goto L_0x01fa
        L_0x01e4:
            org.telegram.tgnet.TLRPC$Document r13 = r2.document
            if (r13 == 0) goto L_0x01f0
            int r2 = r13.size
            r16 = r2
            r2 = r5
            r14 = r2
            r15 = r14
            goto L_0x01fa
        L_0x01f0:
            java.lang.String r13 = r2.imageUrl
            int r2 = r2.size
            r16 = r2
            r2 = r5
            r14 = r2
            r15 = r13
            goto L_0x01e2
        L_0x01fa:
            r18 = r16
            r17 = 0
            r19 = 1
            r16 = r15
            r15 = r12
            r12 = r5
            goto L_0x0212
        L_0x0205:
            r2 = r5
            r12 = r2
            r13 = r12
            r14 = r13
            r15 = r14
            r16 = r15
            r17 = 0
            r18 = 0
            r19 = 0
        L_0x0212:
            if (r13 == 0) goto L_0x0259
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r13.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r11)
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForDocument(r13)
            if (r10 != 0) goto L_0x0226
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r2, r13)
            r12 = r2
            goto L_0x0227
        L_0x0226:
            r12 = r5
        L_0x0227:
            java.util.Locale r2 = java.util.Locale.US
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.Integer r13 = java.lang.Integer.valueOf(r4)
            r6[r3] = r13
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            r6[r8] = r3
            java.lang.String r6 = java.lang.String.format(r2, r7, r6)
            if (r10 == 0) goto L_0x0246
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r3 = r10.bitmap
            r2.<init>(r3)
            r7 = r2
            goto L_0x0247
        L_0x0246:
            r7 = r5
        L_0x0247:
            r8 = 0
            java.lang.String r3 = "d"
            r1 = r22
            r2 = r11
            r4 = r12
            r5 = r6
            r6 = r7
            r7 = r18
            r10 = r19
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x0491
        L_0x0259:
            if (r2 == 0) goto L_0x0279
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForObject(r2, r14)
            if (r10 == 0) goto L_0x026a
            android.graphics.drawable.BitmapDrawable r3 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r4 = r10.bitmap
            r3.<init>(r4)
            r4 = r3
            goto L_0x026b
        L_0x026a:
            r4 = r5
        L_0x026b:
            r6 = 0
            r1 = r22
            r3 = r15
            r5 = r18
            r7 = r9
            r8 = r19
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (android.graphics.drawable.Drawable) r4, (int) r5, (java.lang.String) r6, (java.lang.Object) r7, (int) r8)
            goto L_0x0491
        L_0x0279:
            r2 = 2131165734(0x7var_, float:1.7945693E38)
            if (r12 == 0) goto L_0x02aa
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForWebFile(r12)
            if (r10 == 0) goto L_0x028d
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r4 = r10.bitmap
            r2.<init>(r4)
        L_0x028b:
            r4 = r2
            goto L_0x029d
        L_0x028d:
            if (r17 == 0) goto L_0x029c
            android.app.Activity r4 = r0.parentActivity
            if (r4 == 0) goto L_0x029c
            android.content.res.Resources r4 = r4.getResources()
            android.graphics.drawable.Drawable r2 = r4.getDrawable(r2)
            goto L_0x028b
        L_0x029c:
            r4 = r5
        L_0x029d:
            r5 = 0
            r1 = r22
            r2 = r3
            r3 = r15
            r6 = r9
            r7 = r19
            r1.setImage(r2, r3, r4, r5, r6, r7)
            goto L_0x0491
        L_0x02aa:
            if (r10 == 0) goto L_0x02b5
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r3 = r10.bitmap
            r2.<init>(r3)
        L_0x02b3:
            r4 = r2
            goto L_0x02c5
        L_0x02b5:
            if (r17 == 0) goto L_0x02c4
            android.app.Activity r3 = r0.parentActivity
            if (r3 == 0) goto L_0x02c4
            android.content.res.Resources r3 = r3.getResources()
            android.graphics.drawable.Drawable r2 = r3.getDrawable(r2)
            goto L_0x02b3
        L_0x02c4:
            r4 = r5
        L_0x02c5:
            r5 = 0
            r1 = r22
            r2 = r16
            r3 = r15
            r6 = r18
            r1.setImage(r2, r3, r4, r5, r6)
            goto L_0x0491
        L_0x02d2:
            r1.setImageBitmap((android.graphics.Bitmap) r5)
            goto L_0x0491
        L_0x02d7:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x02f6
            if (r2 < 0) goto L_0x02f6
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            int r4 = r4.size()
            if (r2 >= r4) goto L_0x02f6
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            java.lang.Object r4 = r4.get(r2)
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            r1.setShouldGenerateQualityThumb(r8)
            r9 = r4
            goto L_0x02f7
        L_0x02f6:
            r9 = r5
        L_0x02f7:
            r4 = 2131165796(0x7var_, float:1.794582E38)
            r10 = 100
            if (r9 == 0) goto L_0x03dd
            boolean r11 = r9.isVideo()
            if (r11 == 0) goto L_0x035a
            r1.setNeedsQualityThumb(r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r9.photoThumbs
            if (r2 == 0) goto L_0x034c
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x034c
            org.telegram.messenger.ImageReceiver$BitmapHolder r2 = r0.currentThumb
            if (r2 == 0) goto L_0x031a
            org.telegram.messenger.ImageReceiver r3 = r0.centerImage
            if (r1 != r3) goto L_0x031a
            goto L_0x031b
        L_0x031a:
            r2 = r5
        L_0x031b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r9.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r10)
            r4 = 0
            r6 = 0
            if (r2 != 0) goto L_0x032d
            org.telegram.tgnet.TLObject r7 = r9.photoThumbsObject
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForObject(r3, r7)
            r7 = r3
            goto L_0x032e
        L_0x032d:
            r7 = r5
        L_0x032e:
            if (r2 == 0) goto L_0x0339
            android.graphics.drawable.BitmapDrawable r3 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r2 = r2.bitmap
            r3.<init>(r2)
            r8 = r3
            goto L_0x033a
        L_0x0339:
            r8 = r5
        L_0x033a:
            r10 = 0
            r11 = 0
            r12 = 1
            java.lang.String r5 = "b"
            r1 = r22
            r2 = r4
            r3 = r6
            r4 = r7
            r6 = r8
            r7 = r10
            r8 = r11
            r10 = r12
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x0359
        L_0x034c:
            android.app.Activity r2 = r0.parentActivity
            android.content.res.Resources r2 = r2.getResources()
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r4)
            r1.setImageBitmap((android.graphics.drawable.Drawable) r2)
        L_0x0359:
            return
        L_0x035a:
            org.telegram.ui.Components.AnimatedFileDrawable r11 = r0.currentAnimation
            if (r11 == 0) goto L_0x0369
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r2 = r0.containerView
            r11.setSecondParentView(r2)
            org.telegram.ui.Components.AnimatedFileDrawable r2 = r0.currentAnimation
            r1.setImageBitmap((android.graphics.drawable.Drawable) r2)
            return
        L_0x0369:
            int r11 = r0.sharedMediaType
            if (r11 != r8) goto L_0x03dd
            boolean r2 = r9.canPreviewDocument()
            if (r2 == 0) goto L_0x03d0
            org.telegram.tgnet.TLRPC$Document r2 = r9.getDocument()
            r1.setNeedsQualityThumb(r8)
            org.telegram.messenger.ImageReceiver$BitmapHolder r4 = r0.currentThumb
            if (r4 == 0) goto L_0x0383
            org.telegram.messenger.ImageReceiver r11 = r0.centerImage
            if (r1 != r11) goto L_0x0383
            goto L_0x0384
        L_0x0383:
            r4 = r5
        L_0x0384:
            if (r9 == 0) goto L_0x038d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r9.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r10 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r11, r10)
            goto L_0x038e
        L_0x038d:
            r10 = r5
        L_0x038e:
            r11 = 1157627904(0x45000000, float:2048.0)
            float r12 = org.telegram.messenger.AndroidUtilities.density
            float r11 = r11 / r12
            int r11 = (int) r11
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            java.util.Locale r13 = java.util.Locale.US
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.Integer r14 = java.lang.Integer.valueOf(r11)
            r6[r3] = r14
            java.lang.Integer r3 = java.lang.Integer.valueOf(r11)
            r6[r8] = r3
            java.lang.String r3 = java.lang.String.format(r13, r7, r6)
            if (r4 != 0) goto L_0x03b3
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForDocument(r10, r2)
            goto L_0x03b4
        L_0x03b3:
            r6 = r5
        L_0x03b4:
            if (r4 == 0) goto L_0x03bd
            android.graphics.drawable.BitmapDrawable r5 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r4 = r4.bitmap
            r5.<init>(r4)
        L_0x03bd:
            r7 = r5
            int r8 = r2.size
            r10 = 0
            r11 = 0
            java.lang.String r5 = "b"
            r1 = r22
            r2 = r12
            r4 = r6
            r6 = r7
            r7 = r8
            r8 = r10
            r10 = r11
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x03dc
        L_0x03d0:
            org.telegram.ui.Components.OtherDocumentPlaceholderDrawable r2 = new org.telegram.ui.Components.OtherDocumentPlaceholderDrawable
            android.app.Activity r3 = r0.parentActivity
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r4 = r0.containerView
            r2.<init>(r3, r4, r9)
            r1.setImageBitmap((android.graphics.drawable.Drawable) r2)
        L_0x03dc:
            return
        L_0x03dd:
            int[] r6 = new int[r8]
            org.telegram.messenger.ImageLocation r7 = r0.getImageLocation(r2, r6)
            org.telegram.tgnet.TLObject r2 = r0.getFileLocation(r2, r6)
            if (r7 == 0) goto L_0x0479
            r1.setNeedsQualityThumb(r8)
            org.telegram.messenger.ImageReceiver$BitmapHolder r4 = r0.currentThumb
            if (r4 == 0) goto L_0x03f5
            org.telegram.messenger.ImageReceiver r11 = r0.centerImage
            if (r1 != r11) goto L_0x03f5
            goto L_0x03f6
        L_0x03f5:
            r4 = r5
        L_0x03f6:
            r11 = r6[r3]
            if (r11 != 0) goto L_0x03fd
            r11 = -1
            r6[r3] = r11
        L_0x03fd:
            if (r9 == 0) goto L_0x0408
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r9.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r10 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r11, r10)
            org.telegram.tgnet.TLObject r11 = r9.photoThumbsObject
            goto L_0x040a
        L_0x0408:
            r10 = r5
            r11 = r10
        L_0x040a:
            if (r10 == 0) goto L_0x040f
            if (r10 != r2) goto L_0x040f
            r10 = r5
        L_0x040f:
            if (r9 == 0) goto L_0x0417
            boolean r2 = r9.isWebpage()
            if (r2 != 0) goto L_0x0422
        L_0x0417:
            int r2 = r0.avatarsDialogId
            if (r2 != 0) goto L_0x0422
            boolean r2 = r0.isEvent
            if (r2 == 0) goto L_0x0420
            goto L_0x0422
        L_0x0420:
            r12 = 0
            goto L_0x0423
        L_0x0422:
            r12 = 1
        L_0x0423:
            if (r9 == 0) goto L_0x0426
            goto L_0x0451
        L_0x0426:
            int r2 = r0.avatarsDialogId
            if (r2 == 0) goto L_0x0450
            if (r2 <= 0) goto L_0x043d
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r8 = r0.avatarsDialogId
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r8)
            goto L_0x044e
        L_0x043d:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r8 = r0.avatarsDialogId
            int r8 = -r8
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r8)
        L_0x044e:
            r9 = r2
            goto L_0x0451
        L_0x0450:
            r9 = r5
        L_0x0451:
            r8 = 0
            if (r4 != 0) goto L_0x045a
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForObject(r10, r11)
            r10 = r2
            goto L_0x045b
        L_0x045a:
            r10 = r5
        L_0x045b:
            if (r4 == 0) goto L_0x0466
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r4 = r4.bitmap
            r2.<init>(r4)
            r11 = r2
            goto L_0x0467
        L_0x0466:
            r11 = r5
        L_0x0467:
            r13 = r6[r3]
            r14 = 0
            java.lang.String r5 = "b"
            r1 = r22
            r2 = r7
            r3 = r8
            r4 = r10
            r6 = r11
            r7 = r13
            r8 = r14
            r10 = r12
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x0491
        L_0x0479:
            r1.setNeedsQualityThumb(r8)
            r2 = r6[r3]
            if (r2 != 0) goto L_0x0484
            r1.setImageBitmap((android.graphics.Bitmap) r5)
            goto L_0x0491
        L_0x0484:
            android.app.Activity r2 = r0.parentActivity
            android.content.res.Resources r2 = r2.getResources()
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r4)
            r1.setImageBitmap((android.graphics.drawable.Drawable) r2)
        L_0x0491:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.setIndexToImage(org.telegram.messenger.ImageReceiver, int):void");
    }

    public static boolean isShowingImage(MessageObject messageObject) {
        boolean z = Instance != null && !Instance.pipAnimationInProgress && Instance.isVisible && !Instance.disableShowCheck && messageObject != null && Instance.currentMessageObject != null && Instance.currentMessageObject.getId() == messageObject.getId() && Instance.currentMessageObject.getDialogId() == messageObject.getDialogId();
        if (z || PipInstance == null) {
            return z;
        }
        return PipInstance.isVisible && !PipInstance.disableShowCheck && messageObject != null && PipInstance.currentMessageObject != null && PipInstance.currentMessageObject.getId() == messageObject.getId() && PipInstance.currentMessageObject.getDialogId() == messageObject.getDialogId();
    }

    public static boolean isPlayingMessageInPip(MessageObject messageObject) {
        return (PipInstance == null || messageObject == null || PipInstance.currentMessageObject == null || PipInstance.currentMessageObject.getId() != messageObject.getId() || PipInstance.currentMessageObject.getDialogId() != messageObject.getDialogId()) ? false : true;
    }

    public static boolean isPlayingMessage(MessageObject messageObject) {
        return Instance != null && !Instance.pipAnimationInProgress && Instance.isVisible && messageObject != null && Instance.currentMessageObject != null && Instance.currentMessageObject.getId() == messageObject.getId() && Instance.currentMessageObject.getDialogId() == messageObject.getDialogId();
    }

    public static boolean isShowingImage(TLRPC.FileLocation fileLocation) {
        if (Instance == null || !Instance.isVisible || Instance.disableShowCheck || fileLocation == null || Instance.currentFileLocation == null || fileLocation.local_id != Instance.currentFileLocation.location.local_id || fileLocation.volume_id != Instance.currentFileLocation.location.volume_id || fileLocation.dc_id != Instance.currentFileLocation.dc_id) {
            return false;
        }
        return true;
    }

    public static boolean isShowingImage(TLRPC.BotInlineResult botInlineResult) {
        if (Instance == null || !Instance.isVisible || Instance.disableShowCheck || botInlineResult == null || Instance.currentBotInlineResult == null || botInlineResult.id != Instance.currentBotInlineResult.id) {
            return false;
        }
        return true;
    }

    public static boolean isShowingImage(String str) {
        if (Instance == null || !Instance.isVisible || Instance.disableShowCheck || str == null || !str.equals(Instance.currentPathObject)) {
            return false;
        }
        return true;
    }

    public void setParentChatActivity(ChatActivity chatActivity) {
        this.parentChatActivity = chatActivity;
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        this.maxSelectedPhotos = i;
        this.allowOrder = z;
    }

    public boolean openPhoto(MessageObject messageObject, long j, long j2, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(messageObject, (TLRPC.FileLocation) null, (ImageLocation) null, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, 0, photoViewerProvider, (ChatActivity) null, j, j2, true);
    }

    public boolean openPhoto(MessageObject messageObject, long j, long j2, PhotoViewerProvider photoViewerProvider, boolean z) {
        return openPhoto(messageObject, (TLRPC.FileLocation) null, (ImageLocation) null, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, 0, photoViewerProvider, (ChatActivity) null, j, j2, z);
    }

    public boolean openPhoto(TLRPC.FileLocation fileLocation, PhotoViewerProvider photoViewerProvider) {
        return openPhoto((MessageObject) null, fileLocation, (ImageLocation) null, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, 0, photoViewerProvider, (ChatActivity) null, 0, 0, true);
    }

    public boolean openPhoto(TLRPC.FileLocation fileLocation, ImageLocation imageLocation, PhotoViewerProvider photoViewerProvider) {
        return openPhoto((MessageObject) null, fileLocation, imageLocation, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, 0, photoViewerProvider, (ChatActivity) null, 0, 0, true);
    }

    public boolean openPhoto(ArrayList<MessageObject> arrayList, int i, long j, long j2, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(arrayList.get(i), (TLRPC.FileLocation) null, (ImageLocation) null, arrayList, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, i, photoViewerProvider, (ChatActivity) null, j, j2, true);
    }

    public boolean openPhoto(ArrayList<SecureDocument> arrayList, int i, PhotoViewerProvider photoViewerProvider) {
        return openPhoto((MessageObject) null, (TLRPC.FileLocation) null, (ImageLocation) null, (ArrayList<MessageObject>) null, arrayList, (ArrayList<Object>) null, i, photoViewerProvider, (ChatActivity) null, 0, 0, true);
    }

    public boolean openPhotoForSelect(ArrayList<Object> arrayList, int i, int i2, boolean z, PhotoViewerProvider photoViewerProvider, ChatActivity chatActivity) {
        this.sendPhotoType = i2;
        this.isDocumentsPicker = z;
        ImageView imageView = this.pickerViewSendButton;
        if (imageView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            int i3 = this.sendPhotoType;
            if (i3 == 4 || i3 == 5) {
                this.pickerViewSendButton.setImageResource(NUM);
                layoutParams.bottomMargin = AndroidUtilities.dp(19.0f);
            } else if (i3 == 1 || i3 == 3 || i3 == 10) {
                this.pickerViewSendButton.setImageResource(NUM);
                this.pickerViewSendButton.setPadding(0, AndroidUtilities.dp(1.0f), 0, 0);
                layoutParams.bottomMargin = AndroidUtilities.dp(19.0f);
            } else {
                this.pickerViewSendButton.setImageResource(NUM);
                layoutParams.bottomMargin = AndroidUtilities.dp(14.0f);
            }
            this.pickerViewSendButton.setLayoutParams(layoutParams);
        }
        return openPhoto((MessageObject) null, (TLRPC.FileLocation) null, (ImageLocation) null, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, arrayList, i, photoViewerProvider, chatActivity, 0, 0, true);
    }

    private boolean checkAnimation() {
        if (this.animationInProgress != 0 && Math.abs(this.transitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            Runnable runnable = this.animationEndRunnable;
            if (runnable != null) {
                runnable.run();
                this.animationEndRunnable = null;
            }
            this.animationInProgress = 0;
        }
        if (this.animationInProgress != 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void setCropTranslations(boolean z) {
        if (this.sendPhotoType == 1) {
            int bitmapWidth = this.centerImage.getBitmapWidth();
            int bitmapHeight = this.centerImage.getBitmapHeight();
            if (bitmapWidth != 0 && bitmapHeight != 0) {
                float f = (float) bitmapWidth;
                float containerViewWidth = ((float) getContainerViewWidth()) / f;
                float f2 = (float) bitmapHeight;
                float containerViewHeight = ((float) getContainerViewHeight()) / f2;
                if (containerViewWidth > containerViewHeight) {
                    containerViewWidth = containerViewHeight;
                }
                float min = (float) Math.min(getContainerViewWidth(1), getContainerViewHeight(1));
                float f3 = min / f;
                float f4 = min / f2;
                if (f3 <= f4) {
                    f3 = f4;
                }
                if (z) {
                    this.animationStartTime = System.currentTimeMillis();
                    this.animateToX = (float) ((getLeftInset() / 2) - (getRightInset() / 2));
                    int i = this.currentEditMode;
                    if (i == 2) {
                        this.animateToY = (float) (AndroidUtilities.dp(92.0f) - AndroidUtilities.dp(56.0f));
                    } else if (i == 3) {
                        this.animateToY = (float) (AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(56.0f));
                    }
                    this.animateToScale = f3 / containerViewWidth;
                    this.zoomAnimation = true;
                    return;
                }
                this.animationStartTime = 0;
                this.translationX = (float) ((getLeftInset() / 2) - (getRightInset() / 2));
                this.translationY = (float) ((-AndroidUtilities.dp(56.0f)) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                this.scale = f3 / containerViewWidth;
                updateMinMax(this.scale);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setCropBitmap() {
        if (this.sendPhotoType == 1) {
            Bitmap bitmap = this.centerImage.getBitmap();
            int orientation = this.centerImage.getOrientation();
            if (bitmap == null) {
                bitmap = this.animatingImageView.getBitmap();
                orientation = this.animatingImageView.getOrientation();
            }
            if (bitmap != null) {
                this.photoCropView.setBitmap(bitmap, orientation, false, false);
                if (this.currentEditMode == 0) {
                    setCropTranslations(false);
                }
            }
        }
    }

    private void initCropView() {
        if (this.sendPhotoType == 1) {
            this.photoCropView.setBitmap((Bitmap) null, 0, false, false);
            this.photoCropView.onAppear();
            this.photoCropView.setVisibility(0);
            this.photoCropView.setAlpha(1.0f);
            this.photoCropView.onAppeared();
            this.padImageForHorizontalInsets = true;
        }
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0047 */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x034a  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0053 A[Catch:{ Exception -> 0x0367 }] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x005b A[Catch:{ Exception -> 0x0367 }] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0069 A[Catch:{ Exception -> 0x0367 }] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0072 A[Catch:{ Exception -> 0x0367 }] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0082 A[Catch:{ Exception -> 0x0367 }] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0085 A[Catch:{ Exception -> 0x0367 }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0131  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0144  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0148  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x02de  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean openPhoto(org.telegram.messenger.MessageObject r18, org.telegram.tgnet.TLRPC.FileLocation r19, org.telegram.messenger.ImageLocation r20, java.util.ArrayList<org.telegram.messenger.MessageObject> r21, java.util.ArrayList<org.telegram.messenger.SecureDocument> r22, java.util.ArrayList<java.lang.Object> r23, int r24, org.telegram.ui.PhotoViewer.PhotoViewerProvider r25, org.telegram.ui.ChatActivity r26, long r27, long r29, boolean r31) {
        /*
            r17 = this;
            r10 = r17
            r0 = r18
            r3 = r19
            r1 = r25
            r2 = r26
            android.app.Activity r4 = r10.parentActivity
            r11 = 0
            if (r4 == 0) goto L_0x036b
            boolean r4 = r10.isVisible
            if (r4 != 0) goto L_0x036b
            if (r1 != 0) goto L_0x001b
            boolean r4 = r17.checkAnimation()
            if (r4 != 0) goto L_0x036b
        L_0x001b:
            if (r0 != 0) goto L_0x0029
            if (r3 != 0) goto L_0x0029
            if (r21 != 0) goto L_0x0029
            if (r23 != 0) goto L_0x0029
            if (r22 != 0) goto L_0x0029
            if (r20 != 0) goto L_0x0029
            goto L_0x036b
        L_0x0029:
            r12 = 1
            r8 = r24
            org.telegram.ui.PhotoViewer$PlaceProviderObject r13 = r1.getPlaceForPhoto(r0, r3, r8, r12)
            r4 = 0
            r10.lastInsets = r4
            android.app.Activity r5 = r10.parentActivity
            java.lang.String r6 = "window"
            java.lang.Object r5 = r5.getSystemService(r6)
            android.view.WindowManager r5 = (android.view.WindowManager) r5
            boolean r6 = r10.attachedToWindow
            if (r6 == 0) goto L_0x0047
            android.widget.FrameLayout r6 = r10.windowView     // Catch:{ Exception -> 0x0047 }
            r5.removeView(r6)     // Catch:{ Exception -> 0x0047 }
        L_0x0047:
            android.view.WindowManager$LayoutParams r6 = r10.windowLayoutParams     // Catch:{ Exception -> 0x0367 }
            r7 = 99
            r6.type = r7     // Catch:{ Exception -> 0x0367 }
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0367 }
            r7 = 21
            if (r6 < r7) goto L_0x005b
            android.view.WindowManager$LayoutParams r6 = r10.windowLayoutParams     // Catch:{ Exception -> 0x0367 }
            r9 = -2147286784(0xfffffffvar_, float:-2.75865E-40)
            r6.flags = r9     // Catch:{ Exception -> 0x0367 }
            goto L_0x0061
        L_0x005b:
            android.view.WindowManager$LayoutParams r6 = r10.windowLayoutParams     // Catch:{ Exception -> 0x0367 }
            r9 = 131072(0x20000, float:1.83671E-40)
            r6.flags = r9     // Catch:{ Exception -> 0x0367 }
        L_0x0061:
            if (r2 == 0) goto L_0x0072
            org.telegram.tgnet.TLRPC$EncryptedChat r6 = r26.getCurrentEncryptedChat()     // Catch:{ Exception -> 0x0367 }
            if (r6 == 0) goto L_0x0072
            android.view.WindowManager$LayoutParams r6 = r10.windowLayoutParams     // Catch:{ Exception -> 0x0367 }
            int r9 = r6.flags     // Catch:{ Exception -> 0x0367 }
            r9 = r9 | 8192(0x2000, float:1.14794E-41)
            r6.flags = r9     // Catch:{ Exception -> 0x0367 }
            goto L_0x007a
        L_0x0072:
            android.view.WindowManager$LayoutParams r6 = r10.windowLayoutParams     // Catch:{ Exception -> 0x0367 }
            int r9 = r6.flags     // Catch:{ Exception -> 0x0367 }
            r9 = r9 & -8193(0xffffffffffffdfff, float:NaN)
            r6.flags = r9     // Catch:{ Exception -> 0x0367 }
        L_0x007a:
            android.view.WindowManager$LayoutParams r6 = r10.windowLayoutParams     // Catch:{ Exception -> 0x0367 }
            boolean r9 = r10.useSmoothKeyboard     // Catch:{ Exception -> 0x0367 }
            r15 = 16
            if (r9 == 0) goto L_0x0085
            r9 = 32
            goto L_0x0087
        L_0x0085:
            r9 = 16
        L_0x0087:
            r9 = r9 | 256(0x100, float:3.59E-43)
            r6.softInputMode = r9     // Catch:{ Exception -> 0x0367 }
            android.widget.FrameLayout r6 = r10.windowView     // Catch:{ Exception -> 0x0367 }
            r6.setFocusable(r11)     // Catch:{ Exception -> 0x0367 }
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r6 = r10.containerView     // Catch:{ Exception -> 0x0367 }
            r6.setFocusable(r11)     // Catch:{ Exception -> 0x0367 }
            android.widget.FrameLayout r6 = r10.windowView     // Catch:{ Exception -> 0x0367 }
            android.view.WindowManager$LayoutParams r9 = r10.windowLayoutParams     // Catch:{ Exception -> 0x0367 }
            r5.addView(r6, r9)     // Catch:{ Exception -> 0x0367 }
            r10.doneButtonPressed = r11
            r10.parentChatActivity = r2
            org.telegram.ui.ActionBar.ActionBar r2 = r10.actionBar
            r6 = 2131625890(0x7f0e07a2, float:1.8879E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            java.lang.Integer r16 = java.lang.Integer.valueOf(r12)
            r9[r11] = r16
            java.lang.Integer r16 = java.lang.Integer.valueOf(r12)
            r9[r12] = r16
            java.lang.String r14 = "Of"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r14, r6, r9)
            r2.setTitle(r6)
            int r2 = r10.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r6 = org.telegram.messenger.NotificationCenter.fileDidFailToLoad
            r2.addObserver(r10, r6)
            int r2 = r10.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r6 = org.telegram.messenger.NotificationCenter.fileDidLoad
            r2.addObserver(r10, r6)
            int r2 = r10.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r6 = org.telegram.messenger.NotificationCenter.FileLoadProgressChanged
            r2.addObserver(r10, r6)
            int r2 = r10.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r6 = org.telegram.messenger.NotificationCenter.mediaCountDidLoad
            r2.addObserver(r10, r6)
            int r2 = r10.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r6 = org.telegram.messenger.NotificationCenter.mediaDidLoad
            r2.addObserver(r10, r6)
            int r2 = r10.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r6 = org.telegram.messenger.NotificationCenter.dialogPhotosLoaded
            r2.addObserver(r10, r6)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r6 = org.telegram.messenger.NotificationCenter.emojiDidLoad
            r2.addObserver(r10, r6)
            int r2 = r10.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r6 = org.telegram.messenger.NotificationCenter.filePreparingFailed
            r2.addObserver(r10, r6)
            int r2 = r10.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r6 = org.telegram.messenger.NotificationCenter.fileNewChunkAvailable
            r2.addObserver(r10, r6)
            r10.placeProvider = r1
            r1 = r29
            r10.mergeDialogId = r1
            r1 = r27
            r10.currentDialogId = r1
            org.telegram.ui.PhotoViewer$ListAdapter r1 = r10.selectedPhotosAdapter
            r1.notifyDataSetChanged()
            android.view.VelocityTracker r1 = r10.velocityTracker
            if (r1 != 0) goto L_0x0137
            android.view.VelocityTracker r1 = android.view.VelocityTracker.obtain()
            r10.velocityTracker = r1
        L_0x0137:
            r10.isVisible = r12
            r10.togglePhotosListView(r11, r11)
            r1 = r31 ^ 1
            r10.openedFullScreenVideo = r1
            boolean r1 = r10.openedFullScreenVideo
            if (r1 == 0) goto L_0x0148
            r10.toggleActionBar(r11, r11)
            goto L_0x0156
        L_0x0148:
            int r1 = r10.sendPhotoType
            if (r1 != r12) goto L_0x0153
            r17.createCropView()
            r10.toggleActionBar(r11, r11)
            goto L_0x0156
        L_0x0153:
            r10.toggleActionBar(r12, r11)
        L_0x0156:
            r14 = 0
            r10.seekToProgressPending2 = r14
            r10.skipFirstBufferingProgress = r11
            r10.playerInjected = r11
            r9 = 1065353216(0x3var_, float:1.0)
            if (r13 == 0) goto L_0x02de
            r10.disableShowCheck = r12
            r10.animationInProgress = r12
            if (r0 == 0) goto L_0x01d1
            org.telegram.messenger.ImageReceiver r1 = r13.imageReceiver
            org.telegram.ui.Components.AnimatedFileDrawable r1 = r1.getAnimation()
            r10.currentAnimation = r1
            org.telegram.ui.Components.AnimatedFileDrawable r1 = r10.currentAnimation
            if (r1 == 0) goto L_0x01d1
            boolean r1 = r18.isVideo()
            if (r1 == 0) goto L_0x01c5
            org.telegram.messenger.ImageReceiver r1 = r13.imageReceiver
            r1.setAllowStartAnimation(r11)
            org.telegram.messenger.ImageReceiver r1 = r13.imageReceiver
            r1.stopAnimation()
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()
            boolean r1 = r1.isPlayingMessage(r0)
            if (r1 == 0) goto L_0x0191
            float r1 = r0.audioProgress
            r10.seekToProgressPending2 = r1
        L_0x0191:
            org.telegram.ui.Components.VideoPlayer r1 = r10.injectingVideoPlayer
            if (r1 != 0) goto L_0x01bf
            int r1 = r0.currentAccount
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)
            org.telegram.tgnet.TLRPC$Document r2 = r18.getDocument()
            boolean r1 = r1.isLoadingVideo(r2, r12)
            if (r1 != 0) goto L_0x01bf
            org.telegram.ui.Components.AnimatedFileDrawable r1 = r10.currentAnimation
            boolean r1 = r1.hasBitmap()
            if (r1 != 0) goto L_0x01bd
            int r1 = r0.currentAccount
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)
            org.telegram.tgnet.TLRPC$Document r2 = r18.getDocument()
            boolean r1 = r1.isLoadingVideo(r2, r11)
            if (r1 != 0) goto L_0x01bf
        L_0x01bd:
            r1 = 1
            goto L_0x01c0
        L_0x01bf:
            r1 = 0
        L_0x01c0:
            r10.skipFirstBufferingProgress = r1
            r10.currentAnimation = r4
            goto L_0x01d1
        L_0x01c5:
            java.util.ArrayList r1 = r0.getWebPagePhotos(r4, r4)
            int r1 = r1.size()
            if (r1 <= r12) goto L_0x01d1
            r10.currentAnimation = r4
        L_0x01d1:
            r1 = r17
            r2 = r18
            r3 = r19
            r4 = r20
            r5 = r21
            r6 = r22
            r7 = r23
            r8 = r24
            r0 = 1065353216(0x3var_, float:1.0)
            r9 = r13
            r1.onPhotoShow(r2, r3, r4, r5, r6, r7, r8, r9)
            int r1 = r10.sendPhotoType
            if (r1 != r12) goto L_0x01fa
            org.telegram.ui.Components.PhotoCropView r1 = r10.photoCropView
            r1.setVisibility(r11)
            org.telegram.ui.Components.PhotoCropView r1 = r10.photoCropView
            r1.setAlpha(r14)
            org.telegram.ui.Components.PhotoCropView r1 = r10.photoCropView
            r1.setFreeform(r11)
        L_0x01fa:
            org.telegram.messenger.ImageReceiver r1 = r13.imageReceiver
            android.graphics.RectF r1 = r1.getDrawRegion()
            float r2 = r1.left
            float r3 = r1.top
            org.telegram.messenger.ImageReceiver r4 = r13.imageReceiver
            int r4 = r4.getOrientation()
            org.telegram.messenger.ImageReceiver r5 = r13.imageReceiver
            int r5 = r5.getAnimatedOrientation()
            if (r5 == 0) goto L_0x0213
            r4 = r5
        L_0x0213:
            org.telegram.ui.Components.ClippingImageView[] r5 = r10.getAnimatingImageViews(r13)
            r6 = 0
        L_0x0218:
            int r7 = r5.length
            if (r6 >= r7) goto L_0x023d
            r7 = r5[r6]
            float[][] r8 = r10.animationValues
            r7.setAnimationValues(r8)
            r7 = r5[r6]
            r7.setVisibility(r11)
            r7 = r5[r6]
            int[] r8 = r13.radius
            r7.setRadius(r8)
            r7 = r5[r6]
            r7.setOrientation(r4)
            r7 = r5[r6]
            org.telegram.messenger.ImageReceiver$BitmapHolder r8 = r13.thumb
            r7.setImageBitmap(r8)
            int r6 = r6 + 1
            goto L_0x0218
        L_0x023d:
            r17.initCropView()
            int r4 = r10.sendPhotoType
            if (r4 != r12) goto L_0x024e
            org.telegram.ui.Components.PhotoCropView r4 = r10.photoCropView
            r4.hideBackView()
            org.telegram.ui.Components.PhotoCropView r4 = r10.photoCropView
            r4.setAspectRatio(r0)
        L_0x024e:
            org.telegram.ui.Components.ClippingImageView r4 = r10.animatingImageView
            android.view.ViewGroup$LayoutParams r4 = r4.getLayoutParams()
            float r6 = r1.width()
            int r6 = (int) r6
            r4.width = r6
            float r6 = r1.height()
            int r6 = (int) r6
            r4.height = r6
            int r6 = r4.width
            r7 = 100
            if (r6 > 0) goto L_0x026a
            r4.width = r7
        L_0x026a:
            int r6 = r4.height
            if (r6 > 0) goto L_0x0270
            r4.height = r7
        L_0x0270:
            int r6 = r5.length
            if (r11 >= r6) goto L_0x02bf
            int r6 = r5.length
            if (r6 <= r12) goto L_0x027c
            r6 = r5[r11]
            r6.setAlpha(r14)
            goto L_0x0281
        L_0x027c:
            r6 = r5[r11]
            r6.setAlpha(r0)
        L_0x0281:
            r6 = r5[r11]
            r6.setPivotX(r14)
            r6 = r5[r11]
            r6.setPivotY(r14)
            r6 = r5[r11]
            float r7 = r13.scale
            r6.setScaleX(r7)
            r6 = r5[r11]
            float r7 = r13.scale
            r6.setScaleY(r7)
            r6 = r5[r11]
            int r7 = r13.viewX
            float r7 = (float) r7
            float r8 = r1.left
            float r9 = r13.scale
            float r8 = r8 * r9
            float r7 = r7 + r8
            r6.setTranslationX(r7)
            r6 = r5[r11]
            int r7 = r13.viewY
            float r7 = (float) r7
            float r8 = r1.top
            float r9 = r13.scale
            float r8 = r8 * r9
            float r7 = r7 + r8
            r6.setTranslationY(r7)
            r6 = r5[r11]
            r6.setLayoutParams(r4)
            int r11 = r11 + 1
            goto L_0x0270
        L_0x02bf:
            android.widget.FrameLayout r0 = r10.windowView
            android.view.ViewTreeObserver r0 = r0.getViewTreeObserver()
            org.telegram.ui.PhotoViewer$41 r1 = new org.telegram.ui.PhotoViewer$41
            r24 = r1
            r25 = r17
            r26 = r5
            r27 = r4
            r28 = r2
            r29 = r13
            r30 = r3
            r31 = r23
            r24.<init>(r26, r27, r28, r29, r30, r31)
            r0.addOnPreDrawListener(r1)
            goto L_0x033a
        L_0x02de:
            r1 = 1065353216(0x3var_, float:1.0)
            if (r23 == 0) goto L_0x0314
            int r2 = r10.sendPhotoType
            r4 = 3
            if (r2 == r4) goto L_0x0314
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r7) goto L_0x02f3
            android.view.WindowManager$LayoutParams r2 = r10.windowLayoutParams
            r4 = -2147417856(0xfffffffvar_, float:-9.2194E-41)
            r2.flags = r4
            goto L_0x02f7
        L_0x02f3:
            android.view.WindowManager$LayoutParams r2 = r10.windowLayoutParams
            r2.flags = r11
        L_0x02f7:
            android.view.WindowManager$LayoutParams r2 = r10.windowLayoutParams
            boolean r4 = r10.useSmoothKeyboard
            if (r4 == 0) goto L_0x02ff
            r15 = 32
        L_0x02ff:
            r4 = r15 | 256(0x100, float:3.59E-43)
            r2.softInputMode = r4
            android.widget.FrameLayout r2 = r10.windowView
            android.view.WindowManager$LayoutParams r4 = r10.windowLayoutParams
            r5.updateViewLayout(r2, r4)
            android.widget.FrameLayout r2 = r10.windowView
            r2.setFocusable(r12)
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r2 = r10.containerView
            r2.setFocusable(r12)
        L_0x0314:
            org.telegram.ui.PhotoViewer$BackgroundDrawable r2 = r10.backgroundDrawable
            r4 = 255(0xff, float:3.57E-43)
            r2.setAlpha(r4)
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r2 = r10.containerView
            r2.setAlpha(r1)
            r1 = r17
            r2 = r18
            r3 = r19
            r4 = r20
            r5 = r21
            r6 = r22
            r7 = r23
            r8 = r24
            r9 = r13
            r1.onPhotoShow(r2, r3, r4, r5, r6, r7, r8, r9)
            r17.initCropView()
            r17.setCropBitmap()
        L_0x033a:
            android.app.Activity r0 = r10.parentActivity
            java.lang.String r1 = "accessibility"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.view.accessibility.AccessibilityManager r0 = (android.view.accessibility.AccessibilityManager) r0
            boolean r1 = r0.isTouchExplorationEnabled()
            if (r1 == 0) goto L_0x0366
            android.view.accessibility.AccessibilityEvent r1 = android.view.accessibility.AccessibilityEvent.obtain()
            r2 = 16384(0x4000, float:2.2959E-41)
            r1.setEventType(r2)
            java.util.List r2 = r1.getText()
            r3 = 2131623994(0x7f0e003a, float:1.8875155E38)
            java.lang.String r4 = "AccDescrPhotoViewer"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.add(r3)
            r0.sendAccessibilityEvent(r1)
        L_0x0366:
            return r12
        L_0x0367:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x036b:
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.openPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, org.telegram.messenger.ImageLocation, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, int, org.telegram.ui.PhotoViewer$PhotoViewerProvider, org.telegram.ui.ChatActivity, long, long, boolean):boolean");
    }

    public void injectVideoPlayerToMediaController() {
        if (this.videoPlayer.isPlaying()) {
            MediaController.getInstance().injectVideoPlayer(this.videoPlayer, this.currentMessageObject);
            this.videoPlayer = null;
            updateAccessibilityOverlayVisibility();
        }
    }

    /* JADX WARNING: type inference failed for: r3v1, types: [android.view.View, org.telegram.ui.Components.AnimatedFileDrawable, android.graphics.drawable.Drawable] */
    /* JADX WARNING: type inference failed for: r3v15 */
    /* JADX WARNING: type inference failed for: r3v55 */
    public void closePhoto(boolean z, boolean z2) {
        ? r3;
        boolean z3;
        RectF rectF;
        int i;
        AnimatedFileDrawable animation;
        Bitmap animatedBitmap;
        int systemUiVisibility;
        int i2;
        PhotoPaintView photoPaintView2;
        if (z2 || (i2 = this.currentEditMode) == 0) {
            QualityChooseView qualityChooseView2 = this.qualityChooseView;
            if (qualityChooseView2 == null || qualityChooseView2.getTag() == null) {
                this.openedFullScreenVideo = false;
                try {
                    if (this.visibleDialog != null) {
                        this.visibleDialog.dismiss();
                        this.visibleDialog = null;
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                if (!(Build.VERSION.SDK_INT < 21 || this.actionBar == null || (systemUiVisibility = this.containerView.getSystemUiVisibility() & 4102) == 0)) {
                    FrameLayoutDrawer frameLayoutDrawer = this.containerView;
                    frameLayoutDrawer.setSystemUiVisibility((systemUiVisibility ^ -1) & frameLayoutDrawer.getSystemUiVisibility());
                }
                int i3 = this.currentEditMode;
                if (i3 != 0) {
                    if (i3 == 2) {
                        this.photoFilterView.shutdown();
                        this.containerView.removeView(this.photoFilterView);
                        this.photoFilterView = null;
                    } else if (i3 == 1) {
                        this.editorDoneLayout.setVisibility(8);
                        this.photoCropView.setVisibility(8);
                    } else if (i3 == 3) {
                        this.photoPaintView.shutdown();
                        this.containerView.removeView(this.photoPaintView);
                        this.photoPaintView = null;
                    }
                    this.currentEditMode = 0;
                } else if (this.sendPhotoType == 1) {
                    this.photoCropView.setVisibility(8);
                }
                if (this.parentActivity == null) {
                    return;
                }
                if ((!this.isInline && !this.isVisible) || checkAnimation() || this.placeProvider == null) {
                    return;
                }
                if (!this.captionEditText.hideActionMode() || z2) {
                    PlaceProviderObject placeForPhoto = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, getFileLocation(this.currentFileLocation), this.currentIndex, true);
                    if (!(this.videoPlayer == null || placeForPhoto == null || (animation = placeForPhoto.imageReceiver.getAnimation()) == null)) {
                        if (this.textureUploaded && (animatedBitmap = animation.getAnimatedBitmap()) != null) {
                            try {
                                Bitmap bitmap = this.videoTextureView.getBitmap(animatedBitmap.getWidth(), animatedBitmap.getHeight());
                                new Canvas(animatedBitmap).drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                                bitmap.recycle();
                            } catch (Throwable th) {
                                FileLog.e(th);
                            }
                        }
                        animation.seekTo(this.videoPlayer.getCurrentPosition(), !FileLoader.getInstance(this.currentMessageObject.currentAccount).isLoadingVideo(this.currentMessageObject.getDocument(), true));
                        placeForPhoto.imageReceiver.setAllowStartAnimation(true);
                        placeForPhoto.imageReceiver.startAnimation();
                    }
                    releasePlayer(true);
                    this.captionEditText.onDestroy();
                    this.parentChatActivity = null;
                    removeObservers();
                    this.isActionBarVisible = false;
                    VelocityTracker velocityTracker2 = this.velocityTracker;
                    if (velocityTracker2 != null) {
                        velocityTracker2.recycle();
                        this.velocityTracker = null;
                    }
                    if (this.isInline) {
                        this.isInline = false;
                        this.animationInProgress = 0;
                        onPhotoClosed(placeForPhoto);
                        this.containerView.setScaleX(1.0f);
                        this.containerView.setScaleY(1.0f);
                        return;
                    }
                    if (z) {
                        ClippingImageView[] animatingImageViews = getAnimatingImageViews(placeForPhoto);
                        for (int i4 = 0; i4 < animatingImageViews.length; i4++) {
                            animatingImageViews[i4].setAnimationValues(this.animationValues);
                            animatingImageViews[i4].setVisibility(0);
                        }
                        this.animationInProgress = 3;
                        this.containerView.invalidate();
                        AnimatorSet animatorSet = new AnimatorSet();
                        ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
                        if (placeForPhoto != null) {
                            rectF = placeForPhoto.imageReceiver.getDrawRegion();
                            layoutParams.width = (int) rectF.width();
                            layoutParams.height = (int) rectF.height();
                            int orientation = placeForPhoto.imageReceiver.getOrientation();
                            int animatedOrientation = placeForPhoto.imageReceiver.getAnimatedOrientation();
                            if (animatedOrientation != 0) {
                                orientation = animatedOrientation;
                            }
                            for (int i5 = 0; i5 < animatingImageViews.length; i5++) {
                                animatingImageViews[i5].setOrientation(orientation);
                                animatingImageViews[i5].setImageBitmap(placeForPhoto.thumb);
                            }
                        } else {
                            layoutParams.width = this.centerImage.getImageWidth();
                            layoutParams.height = this.centerImage.getImageHeight();
                            for (int i6 = 0; i6 < animatingImageViews.length; i6++) {
                                animatingImageViews[i6].setOrientation(this.centerImage.getOrientation());
                                animatingImageViews[i6].setImageBitmap(this.centerImage.getBitmapSafe());
                            }
                            rectF = null;
                        }
                        if (layoutParams.width <= 0) {
                            layoutParams.width = 100;
                        }
                        if (layoutParams.height <= 0) {
                            layoutParams.height = 100;
                        }
                        float measuredWidth = ((float) this.windowView.getMeasuredWidth()) / ((float) layoutParams.width);
                        float f = ((float) (AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / ((float) layoutParams.height);
                        if (measuredWidth > f) {
                            measuredWidth = f;
                        }
                        float f2 = this.scale;
                        float f3 = ((float) layoutParams.height) * f2 * measuredWidth;
                        float measuredWidth2 = (((float) this.windowView.getMeasuredWidth()) - ((((float) layoutParams.width) * f2) * measuredWidth)) / 2.0f;
                        float f4 = (((float) (AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) - f3) / 2.0f;
                        for (int i7 = 0; i7 < animatingImageViews.length; i7++) {
                            animatingImageViews[i7].setLayoutParams(layoutParams);
                            animatingImageViews[i7].setTranslationX(this.translationX + measuredWidth2);
                            animatingImageViews[i7].setTranslationY(this.translationY + f4);
                            animatingImageViews[i7].setScaleX(this.scale * measuredWidth);
                            animatingImageViews[i7].setScaleY(this.scale * measuredWidth);
                        }
                        if (placeForPhoto != null) {
                            placeForPhoto.imageReceiver.setVisible(false, true);
                            int abs = (int) Math.abs(rectF.left - ((float) placeForPhoto.imageReceiver.getImageX()));
                            int abs2 = (int) Math.abs(rectF.top - ((float) placeForPhoto.imageReceiver.getImageY()));
                            int[] iArr = new int[2];
                            placeForPhoto.parentView.getLocationInWindow(iArr);
                            int i8 = (int) ((((float) (iArr[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) - (((float) placeForPhoto.viewY) + rectF.top)) + ((float) placeForPhoto.clipTopAddition));
                            if (i8 < 0) {
                                i8 = 0;
                            }
                            float f5 = rectF.top;
                            int height = (int) ((((((float) placeForPhoto.viewY) + f5) + (rectF.bottom - f5)) - ((float) ((iArr[1] + placeForPhoto.parentView.getHeight()) - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)))) + ((float) placeForPhoto.clipBottomAddition));
                            if (height < 0) {
                                height = 0;
                            }
                            int max = Math.max(i8, abs2);
                            int max2 = Math.max(height, abs2);
                            this.animationValues[0][0] = this.animatingImageView.getScaleX();
                            this.animationValues[0][1] = this.animatingImageView.getScaleY();
                            this.animationValues[0][2] = this.animatingImageView.getTranslationX();
                            this.animationValues[0][3] = this.animatingImageView.getTranslationY();
                            float[][] fArr = this.animationValues;
                            fArr[0][4] = 0.0f;
                            fArr[0][5] = 0.0f;
                            fArr[0][6] = 0.0f;
                            fArr[0][7] = 0.0f;
                            fArr[0][8] = 0.0f;
                            fArr[0][9] = 0.0f;
                            fArr[0][10] = 0.0f;
                            fArr[0][11] = 0.0f;
                            fArr[0][12] = 0.0f;
                            float[] fArr2 = fArr[1];
                            float f6 = placeForPhoto.scale;
                            fArr2[0] = f6;
                            fArr[1][1] = f6;
                            fArr[1][2] = ((float) placeForPhoto.viewX) + (rectF.left * f6);
                            fArr[1][3] = ((float) placeForPhoto.viewY) + (rectF.top * f6);
                            float f7 = (float) abs;
                            fArr[1][4] = f7 * f6;
                            fArr[1][5] = ((float) max) * f6;
                            fArr[1][6] = ((float) max2) * f6;
                            for (int i9 = 0; i9 < 4; i9++) {
                                float[] fArr3 = this.animationValues[1];
                                int i10 = i9 + 7;
                                int[] iArr2 = placeForPhoto.radius;
                                fArr3[i10] = iArr2 != null ? (float) iArr2[i9] : 0.0f;
                            }
                            float[][] fArr4 = this.animationValues;
                            float[] fArr5 = fArr4[1];
                            float f8 = placeForPhoto.scale;
                            fArr5[11] = ((float) abs2) * f8;
                            fArr4[1][12] = f7 * f8;
                            ArrayList arrayList = new ArrayList((this.sendPhotoType == 1 ? 3 : 2) + animatingImageViews.length + (animatingImageViews.length > 1 ? 1 : 0));
                            for (ClippingImageView ofFloat : animatingImageViews) {
                                arrayList.add(ObjectAnimator.ofFloat(ofFloat, AnimationProperties.CLIPPING_IMAGE_VIEW_PROGRESS, new float[]{0.0f, 1.0f}));
                            }
                            if (animatingImageViews.length > 1) {
                                i = 0;
                                arrayList.add(ObjectAnimator.ofFloat(this.animatingImageView, View.ALPHA, new float[]{0.0f}));
                            } else {
                                i = 0;
                            }
                            BackgroundDrawable backgroundDrawable2 = this.backgroundDrawable;
                            Property<ColorDrawable, Integer> property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
                            int[] iArr3 = new int[1];
                            iArr3[i] = i;
                            arrayList.add(ObjectAnimator.ofInt(backgroundDrawable2, property, iArr3));
                            FrameLayoutDrawer frameLayoutDrawer2 = this.containerView;
                            Property property2 = View.ALPHA;
                            float[] fArr6 = new float[1];
                            fArr6[i] = 0.0f;
                            arrayList.add(ObjectAnimator.ofFloat(frameLayoutDrawer2, property2, fArr6));
                            if (this.sendPhotoType == 1) {
                                PhotoCropView photoCropView2 = this.photoCropView;
                                Property property3 = View.ALPHA;
                                float[] fArr7 = new float[1];
                                fArr7[i] = 0.0f;
                                arrayList.add(ObjectAnimator.ofFloat(photoCropView2, property3, fArr7));
                            }
                            animatorSet.playTogether(arrayList);
                        } else {
                            int i11 = AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                            Animator[] animatorArr = new Animator[4];
                            animatorArr[0] = ObjectAnimator.ofInt(this.backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.animatingImageView, View.ALPHA, new float[]{0.0f});
                            ClippingImageView clippingImageView = this.animatingImageView;
                            Property property4 = View.TRANSLATION_Y;
                            float[] fArr8 = new float[1];
                            if (this.translationY < 0.0f) {
                                i11 = -i11;
                            }
                            fArr8[0] = (float) i11;
                            animatorArr[2] = ObjectAnimator.ofFloat(clippingImageView, property4, fArr8);
                            animatorArr[3] = ObjectAnimator.ofFloat(this.containerView, View.ALPHA, new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        }
                        this.animationEndRunnable = new Runnable(placeForPhoto) {
                            private final /* synthetic */ PhotoViewer.PlaceProviderObject f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                PhotoViewer.this.lambda$closePhoto$47$PhotoViewer(this.f$1);
                            }
                        };
                        animatorSet.setDuration(200);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public final void run() {
                                        PhotoViewer.AnonymousClass42.this.lambda$onAnimationEnd$0$PhotoViewer$42();
                                    }
                                });
                            }

                            public /* synthetic */ void lambda$onAnimationEnd$0$PhotoViewer$42() {
                                if (PhotoViewer.this.animationEndRunnable != null) {
                                    PhotoViewer.this.animationEndRunnable.run();
                                    Runnable unused = PhotoViewer.this.animationEndRunnable = null;
                                }
                            }
                        });
                        this.transitionAnimationStartTime = System.currentTimeMillis();
                        if (Build.VERSION.SDK_INT >= 18) {
                            this.containerView.setLayerType(2, (Paint) null);
                        }
                        animatorSet.start();
                        r3 = 0;
                    } else {
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerView, View.SCALE_X, new float[]{0.9f}), ObjectAnimator.ofFloat(this.containerView, View.SCALE_Y, new float[]{0.9f}), ObjectAnimator.ofInt(this.backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0}), ObjectAnimator.ofFloat(this.containerView, View.ALPHA, new float[]{0.0f})});
                        this.animationInProgress = 2;
                        this.animationEndRunnable = new Runnable(placeForPhoto) {
                            private final /* synthetic */ PhotoViewer.PlaceProviderObject f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                PhotoViewer.this.lambda$closePhoto$48$PhotoViewer(this.f$1);
                            }
                        };
                        animatorSet2.setDuration(200);
                        animatorSet2.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (PhotoViewer.this.animationEndRunnable != null) {
                                    PhotoViewer.this.animationEndRunnable.run();
                                    Runnable unused = PhotoViewer.this.animationEndRunnable = null;
                                }
                            }
                        });
                        this.transitionAnimationStartTime = System.currentTimeMillis();
                        if (Build.VERSION.SDK_INT >= 18) {
                            z3 = false;
                            this.containerView.setLayerType(2, (Paint) null);
                        } else {
                            z3 = false;
                        }
                        animatorSet2.start();
                        r3 = z3;
                    }
                    AnimatedFileDrawable animatedFileDrawable = this.currentAnimation;
                    if (animatedFileDrawable != null) {
                        animatedFileDrawable.setSecondParentView(r3);
                        this.currentAnimation = r3;
                        this.centerImage.setImageBitmap((Drawable) r3);
                    }
                    PhotoViewerProvider photoViewerProvider = this.placeProvider;
                    if (photoViewerProvider != null && !photoViewerProvider.canScrollAway()) {
                        this.placeProvider.cancelButtonPressed();
                        return;
                    }
                    return;
                }
                return;
            }
            this.qualityPicker.cancelButton.callOnClick();
        } else if (i2 != 3 || (photoPaintView2 = this.photoPaintView) == null) {
            switchToEditMode(0);
        } else {
            photoPaintView2.maybeShowDismissalAlert(this, this.parentActivity, new Runnable() {
                public final void run() {
                    PhotoViewer.this.lambda$closePhoto$46$PhotoViewer();
                }
            });
        }
    }

    public /* synthetic */ void lambda$closePhoto$46$PhotoViewer() {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$closePhoto$47$PhotoViewer(PlaceProviderObject placeProviderObject) {
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, (Paint) null);
        }
        this.animationInProgress = 0;
        onPhotoClosed(placeProviderObject);
    }

    public /* synthetic */ void lambda$closePhoto$48$PhotoViewer(PlaceProviderObject placeProviderObject) {
        FrameLayoutDrawer frameLayoutDrawer = this.containerView;
        if (frameLayoutDrawer != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                frameLayoutDrawer.setLayerType(0, (Paint) null);
            }
            this.animationInProgress = 0;
            onPhotoClosed(placeProviderObject);
            this.containerView.setScaleX(1.0f);
            this.containerView.setScaleY(1.0f);
        }
    }

    private ClippingImageView[] getAnimatingImageViews(PlaceProviderObject placeProviderObject) {
        int i = (AndroidUtilities.isTablet() || placeProviderObject == null || placeProviderObject.animatingImageView == null) ? 0 : 1;
        ClippingImageView[] clippingImageViewArr = new ClippingImageView[(i + 1)];
        clippingImageViewArr[0] = this.animatingImageView;
        if (i != 0) {
            ClippingImageView clippingImageView = placeProviderObject.animatingImageView;
            clippingImageViewArr[1] = clippingImageView;
            clippingImageView.setAdditionalTranslationY((float) placeProviderObject.animatingImageViewYOffset);
        }
        return clippingImageViewArr;
    }

    private void removeObservers() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailToLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
        ConnectionsManager.getInstance(this.currentAccount).cancelRequestsForGuid(this.classGuid);
    }

    public void destroyPhotoViewer() {
        if (this.parentActivity != null && this.windowView != null) {
            PipVideoView pipVideoView2 = this.pipVideoView;
            if (pipVideoView2 != null) {
                pipVideoView2.close();
                this.pipVideoView = null;
            }
            removeObservers();
            releasePlayer(false);
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.currentThumb = null;
            }
            this.animatingImageView.setImageBitmap((ImageReceiver.BitmapHolder) null);
            PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
            if (photoViewerCaptionEnterView != null) {
                photoViewerCaptionEnterView.onDestroy();
            }
            if (this == PipInstance) {
                PipInstance = null;
            } else {
                Instance = null;
            }
        }
    }

    private void onPhotoClosed(PlaceProviderObject placeProviderObject) {
        this.isVisible = false;
        this.disableShowCheck = true;
        this.currentMessageObject = null;
        this.currentBotInlineResult = null;
        this.currentFileLocation = null;
        this.currentSecureDocument = null;
        this.currentPathObject = null;
        FrameLayout frameLayout = this.videoPlayerControlFrameLayout;
        if (frameLayout != null) {
            frameLayout.setVisibility(8);
            this.dateTextView.setVisibility(0);
            this.nameTextView.setVisibility(0);
        }
        this.sendPhotoType = 0;
        this.isDocumentsPicker = false;
        ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
            this.currentThumb = null;
        }
        this.parentAlert = null;
        AnimatedFileDrawable animatedFileDrawable = this.currentAnimation;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.setSecondParentView((View) null);
            this.currentAnimation = null;
        }
        for (int i = 0; i < 3; i++) {
            PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
            if (photoProgressViewArr[i] != null) {
                photoProgressViewArr[i].setBackgroundState(-1, false);
            }
        }
        requestVideoPreview(0);
        VideoTimelinePlayView videoTimelinePlayView = this.videoTimelineView;
        if (videoTimelinePlayView != null) {
            videoTimelinePlayView.destroy();
        }
        this.centerImage.setImageBitmap((Bitmap) null);
        this.leftImage.setImageBitmap((Bitmap) null);
        this.rightImage.setImageBitmap((Bitmap) null);
        this.containerView.post(new Runnable(placeProviderObject) {
            private final /* synthetic */ PhotoViewer.PlaceProviderObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PhotoViewer.this.lambda$onPhotoClosed$49$PhotoViewer(this.f$1);
            }
        });
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        if (photoViewerProvider != null) {
            photoViewerProvider.willHidePhotoViewer();
        }
        this.groupedPhotosListView.clear();
        this.placeProvider = null;
        this.selectedPhotosAdapter.notifyDataSetChanged();
        this.disableShowCheck = false;
        this.videoCutStart = 0.0f;
        this.videoCutEnd = 1.0f;
        if (placeProviderObject != null) {
            placeProviderObject.imageReceiver.setVisible(true, true);
        }
    }

    public /* synthetic */ void lambda$onPhotoClosed$49$PhotoViewer(PlaceProviderObject placeProviderObject) {
        ClippingImageView clippingImageView;
        this.animatingImageView.setImageBitmap((ImageReceiver.BitmapHolder) null);
        if (!(placeProviderObject == null || AndroidUtilities.isTablet() || (clippingImageView = placeProviderObject.animatingImageView) == null)) {
            clippingImageView.setImageBitmap((ImageReceiver.BitmapHolder) null);
        }
        try {
            if (this.windowView.getParent() != null) {
                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void redraw(int i) {
        FrameLayoutDrawer frameLayoutDrawer;
        if (i < 6 && (frameLayoutDrawer = this.containerView) != null) {
            frameLayoutDrawer.invalidate();
            AndroidUtilities.runOnUIThread(new Runnable(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PhotoViewer.this.lambda$redraw$50$PhotoViewer(this.f$1);
                }
            }, 100);
        }
    }

    public /* synthetic */ void lambda$redraw$50$PhotoViewer(int i) {
        redraw(i + 1);
    }

    public void onResume() {
        redraw(0);
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.seekTo(videoPlayer2.getCurrentPosition() + 1);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        PipVideoView pipVideoView2 = this.pipVideoView;
        if (pipVideoView2 != null) {
            pipVideoView2.onConfigurationChanged();
        }
    }

    public void onPause() {
        if (this.currentAnimation != null) {
            closePhoto(false, false);
        } else if (this.lastTitle != null) {
            closeCaptionEnter(true);
        }
    }

    public boolean isVisible() {
        return this.isVisible && this.placeProvider != null;
    }

    /* access modifiers changed from: private */
    public void updateMinMax(float f) {
        int imageWidth = ((int) ((((float) this.centerImage.getImageWidth()) * f) - ((float) getContainerViewWidth()))) / 2;
        int imageHeight = ((int) ((((float) this.centerImage.getImageHeight()) * f) - ((float) getContainerViewHeight()))) / 2;
        if (imageWidth > 0) {
            this.minX = (float) (-imageWidth);
            this.maxX = (float) imageWidth;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (imageHeight > 0) {
            this.minY = (float) (-imageHeight);
            this.maxY = (float) imageHeight;
            return;
        }
        this.maxY = 0.0f;
        this.minY = 0.0f;
    }

    private int getAdditionX() {
        int i = this.currentEditMode;
        if (i == 0 || i == 3) {
            return 0;
        }
        return AndroidUtilities.dp(14.0f);
    }

    private int getAdditionY() {
        int i = this.currentEditMode;
        int i2 = 0;
        if (i == 3) {
            int dp = AndroidUtilities.dp(8.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            return dp + i2;
        } else if (i == 0) {
            return 0;
        } else {
            int dp2 = AndroidUtilities.dp(14.0f);
            if (Build.VERSION.SDK_INT >= 21) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            return dp2 + i2;
        }
    }

    /* access modifiers changed from: private */
    public int getContainerViewWidth() {
        return getContainerViewWidth(this.currentEditMode);
    }

    /* access modifiers changed from: private */
    public int getContainerViewWidth(int i) {
        int width = this.containerView.getWidth();
        return (i == 0 || i == 3) ? width : width - AndroidUtilities.dp(28.0f);
    }

    /* access modifiers changed from: private */
    public int getContainerViewHeight() {
        return getContainerViewHeight(this.currentEditMode);
    }

    /* access modifiers changed from: private */
    public int getContainerViewHeight(int i) {
        int dp;
        int i2 = AndroidUtilities.displaySize.y;
        if (i == 0 && Build.VERSION.SDK_INT >= 21) {
            i2 += AndroidUtilities.statusBarHeight;
        }
        if (i == 1) {
            dp = AndroidUtilities.dp(144.0f);
        } else if (i == 2) {
            dp = AndroidUtilities.dp(214.0f);
        } else if (i != 3) {
            return i2;
        } else {
            dp = AndroidUtilities.dp(48.0f) + ActionBar.getCurrentActionBarHeight();
        }
        return i2 - dp;
    }

    /* access modifiers changed from: private */
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.animationInProgress != 0 || this.animationStartTime != 0) {
            return false;
        }
        int i = this.currentEditMode;
        if (i == 2) {
            this.photoFilterView.onTouch(motionEvent);
            return true;
        }
        if (!(i == 1 || this.sendPhotoType == 1)) {
            if (this.captionEditText.isPopupShowing() || this.captionEditText.isKeyboardVisible()) {
                if (motionEvent.getAction() == 1) {
                    closeCaptionEnter(true);
                }
            } else if (this.currentEditMode != 0 || this.sendPhotoType == 1 || motionEvent.getPointerCount() != 1 || !this.gestureDetector.onTouchEvent(motionEvent) || !this.doubleTap) {
                Tooltip tooltip2 = this.tooltip;
                if (tooltip2 != null) {
                    tooltip2.hide();
                }
                if (motionEvent.getActionMasked() == 0 || motionEvent.getActionMasked() == 5) {
                    this.discardTap = false;
                    if (!this.scroller.isFinished()) {
                        this.scroller.abortAnimation();
                    }
                    if (!this.draggingDown && !this.changingPage) {
                        if (this.canZoom && motionEvent.getPointerCount() == 2) {
                            this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)));
                            this.pinchStartScale = this.scale;
                            this.pinchCenterX = (motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f;
                            this.pinchCenterY = (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f;
                            this.pinchStartX = this.translationX;
                            this.pinchStartY = this.translationY;
                            this.zooming = true;
                            this.moving = false;
                            hidePressedDrawables();
                            VelocityTracker velocityTracker2 = this.velocityTracker;
                            if (velocityTracker2 != null) {
                                velocityTracker2.clear();
                            }
                        } else if (motionEvent.getPointerCount() == 1) {
                            this.moveStartX = motionEvent.getX();
                            float y = motionEvent.getY();
                            this.moveStartY = y;
                            this.dragY = y;
                            this.draggingDown = false;
                            this.canDragDown = true;
                            VelocityTracker velocityTracker3 = this.velocityTracker;
                            if (velocityTracker3 != null) {
                                velocityTracker3.clear();
                            }
                        }
                    }
                } else {
                    float f = 0.0f;
                    if (motionEvent.getActionMasked() == 2) {
                        if (this.canZoom && motionEvent.getPointerCount() == 2 && !this.draggingDown && this.zooming && !this.changingPage) {
                            this.discardTap = true;
                            this.scale = (((float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)))) / this.pinchStartDistance) * this.pinchStartScale;
                            this.translationX = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (this.scale / this.pinchStartScale));
                            float containerViewHeight = this.pinchCenterY - ((float) (getContainerViewHeight() / 2));
                            float containerViewHeight2 = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY;
                            float f2 = this.scale;
                            this.translationY = containerViewHeight - (containerViewHeight2 * (f2 / this.pinchStartScale));
                            updateMinMax(f2);
                            this.containerView.invalidate();
                        } else if (motionEvent.getPointerCount() == 1) {
                            VelocityTracker velocityTracker4 = this.velocityTracker;
                            if (velocityTracker4 != null) {
                                velocityTracker4.addMovement(motionEvent);
                            }
                            float abs = Math.abs(motionEvent.getX() - this.moveStartX);
                            float abs2 = Math.abs(motionEvent.getY() - this.dragY);
                            if (abs > ((float) AndroidUtilities.dp(3.0f)) || abs2 > ((float) AndroidUtilities.dp(3.0f))) {
                                this.discardTap = true;
                                hidePressedDrawables();
                                QualityChooseView qualityChooseView2 = this.qualityChooseView;
                                if (qualityChooseView2 != null && qualityChooseView2.getVisibility() == 0) {
                                    return true;
                                }
                            }
                            if (this.placeProvider.canScrollAway() && this.currentEditMode == 0 && this.sendPhotoType != 1 && this.canDragDown && !this.draggingDown && this.scale == 1.0f && abs2 >= ((float) AndroidUtilities.dp(30.0f)) && abs2 / 2.0f > abs) {
                                this.draggingDown = true;
                                hidePressedDrawables();
                                this.moving = false;
                                this.dragY = motionEvent.getY();
                                if (this.isActionBarVisible && this.containerView.getTag() != null) {
                                    toggleActionBar(false, true);
                                } else if (this.pickerView.getVisibility() == 0) {
                                    toggleActionBar(false, true);
                                    togglePhotosListView(false, true);
                                    toggleCheckImageView(false);
                                }
                                return true;
                            } else if (this.draggingDown) {
                                this.translationY = motionEvent.getY() - this.dragY;
                                this.containerView.invalidate();
                            } else if (this.invalidCoords || this.animationStartTime != 0) {
                                this.invalidCoords = false;
                                this.moveStartX = motionEvent.getX();
                                this.moveStartY = motionEvent.getY();
                            } else {
                                float x = this.moveStartX - motionEvent.getX();
                                float y2 = this.moveStartY - motionEvent.getY();
                                if (this.moving || this.currentEditMode != 0 || ((this.scale == 1.0f && Math.abs(y2) + ((float) AndroidUtilities.dp(12.0f)) < Math.abs(x)) || this.scale != 1.0f)) {
                                    if (!this.moving) {
                                        this.moving = true;
                                        this.canDragDown = false;
                                        hidePressedDrawables();
                                        x = 0.0f;
                                        y2 = 0.0f;
                                    }
                                    this.moveStartX = motionEvent.getX();
                                    this.moveStartY = motionEvent.getY();
                                    updateMinMax(this.scale);
                                    if ((this.translationX < this.minX && (this.currentEditMode != 0 || !this.rightImage.hasImageSet())) || (this.translationX > this.maxX && (this.currentEditMode != 0 || !this.leftImage.hasImageSet()))) {
                                        x /= 3.0f;
                                    }
                                    float f3 = this.maxY;
                                    if (f3 == 0.0f) {
                                        float f4 = this.minY;
                                        if (f4 == 0.0f && this.currentEditMode == 0 && this.sendPhotoType != 1) {
                                            float f5 = this.translationY;
                                            if (f5 - y2 < f4) {
                                                this.translationY = f4;
                                            } else {
                                                if (f5 - y2 > f3) {
                                                    this.translationY = f3;
                                                }
                                                f = y2;
                                            }
                                            this.translationX -= x;
                                            if (!(this.scale == 1.0f && this.currentEditMode == 0)) {
                                                this.translationY -= f;
                                            }
                                            this.containerView.invalidate();
                                        }
                                    }
                                    float f6 = this.translationY;
                                    if (f6 < this.minY || f6 > this.maxY) {
                                        f = y2 / 3.0f;
                                        this.translationX -= x;
                                        this.translationY -= f;
                                        this.containerView.invalidate();
                                    }
                                    f = y2;
                                    this.translationX -= x;
                                    this.translationY -= f;
                                    this.containerView.invalidate();
                                }
                            }
                        }
                    } else if (motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
                        if (this.zooming) {
                            this.invalidCoords = true;
                            float f7 = this.scale;
                            if (f7 < 1.0f) {
                                updateMinMax(1.0f);
                                animateTo(1.0f, 0.0f, 0.0f, true);
                            } else if (f7 > 3.0f) {
                                float containerViewWidth = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (3.0f / this.pinchStartScale));
                                float containerViewHeight3 = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (3.0f / this.pinchStartScale));
                                updateMinMax(3.0f);
                                float f8 = this.minX;
                                if (containerViewWidth >= f8) {
                                    f8 = this.maxX;
                                    if (containerViewWidth <= f8) {
                                        f8 = containerViewWidth;
                                    }
                                }
                                float f9 = this.minY;
                                if (containerViewHeight3 >= f9) {
                                    f9 = this.maxY;
                                    if (containerViewHeight3 <= f9) {
                                        f9 = containerViewHeight3;
                                    }
                                }
                                animateTo(3.0f, f8, f9, true);
                            } else {
                                checkMinMax(true);
                            }
                            this.zooming = false;
                        } else if (this.draggingDown) {
                            if (Math.abs(this.dragY - motionEvent.getY()) > ((float) getContainerViewHeight()) / 6.0f) {
                                closePhoto(true, false);
                            } else {
                                if (this.pickerView.getVisibility() == 0) {
                                    toggleActionBar(true, true);
                                    toggleCheckImageView(true);
                                }
                                animateTo(1.0f, 0.0f, 0.0f, false);
                            }
                            this.draggingDown = false;
                        } else if (this.moving) {
                            float var_ = this.translationX;
                            float var_ = this.translationY;
                            updateMinMax(this.scale);
                            this.moving = false;
                            this.canDragDown = true;
                            VelocityTracker velocityTracker5 = this.velocityTracker;
                            if (velocityTracker5 != null && this.scale == 1.0f) {
                                velocityTracker5.computeCurrentVelocity(1000);
                                f = this.velocityTracker.getXVelocity();
                            }
                            if (this.currentEditMode == 0 && this.sendPhotoType != 1) {
                                if ((this.translationX < this.minX - ((float) (getContainerViewWidth() / 3)) || f < ((float) (-AndroidUtilities.dp(650.0f)))) && this.rightImage.hasImageSet()) {
                                    goToNext();
                                    return true;
                                } else if ((this.translationX > this.maxX + ((float) (getContainerViewWidth() / 3)) || f > ((float) AndroidUtilities.dp(650.0f))) && this.leftImage.hasImageSet()) {
                                    goToPrev();
                                    return true;
                                }
                            }
                            float var_ = this.translationX;
                            float var_ = this.minX;
                            if (var_ >= var_) {
                                var_ = this.maxX;
                                if (var_ <= var_) {
                                    var_ = var_;
                                }
                            }
                            float var_ = this.translationY;
                            float var_ = this.minY;
                            if (var_ >= var_) {
                                var_ = this.maxY;
                                if (var_ <= var_) {
                                    var_ = var_;
                                }
                            }
                            animateTo(this.scale, var_, var_, false);
                        }
                    }
                }
                return false;
            } else {
                this.doubleTap = false;
                this.moving = false;
                this.zooming = false;
                checkMinMax(false);
                return true;
            }
        }
        return true;
    }

    private void checkMinMax(boolean z) {
        float f = this.translationX;
        float f2 = this.translationY;
        updateMinMax(this.scale);
        float f3 = this.translationX;
        float f4 = this.minX;
        if (f3 >= f4) {
            f4 = this.maxX;
            if (f3 <= f4) {
                f4 = f;
            }
        }
        float f5 = this.translationY;
        float f6 = this.minY;
        if (f5 >= f6) {
            f6 = this.maxY;
            if (f5 <= f6) {
                f6 = f2;
            }
        }
        animateTo(this.scale, f4, f6, z);
    }

    private void goToNext() {
        float containerViewWidth = this.scale != 1.0f ? ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale : 0.0f;
        this.switchImageAfterAnimation = 1;
        animateTo(this.scale, ((this.minX - ((float) getContainerViewWidth())) - containerViewWidth) - ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
    }

    private void goToPrev() {
        float containerViewWidth = this.scale != 1.0f ? ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale : 0.0f;
        this.switchImageAfterAnimation = 2;
        animateTo(this.scale, this.maxX + ((float) getContainerViewWidth()) + containerViewWidth + ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
    }

    private void animateTo(float f, float f2, float f3, boolean z) {
        animateTo(f, f2, f3, z, 250);
    }

    private void animateTo(float f, float f2, float f3, boolean z, int i) {
        if (this.scale != f || this.translationX != f2 || this.translationY != f3) {
            this.zoomAnimation = z;
            this.animateToScale = f;
            this.animateToX = f2;
            this.animateToY = f3;
            this.animationStartTime = System.currentTimeMillis();
            this.imageMoveAnimation = new AnimatorSet();
            this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f})});
            this.imageMoveAnimation.setInterpolator(this.interpolator);
            this.imageMoveAnimation.setDuration((long) i);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = PhotoViewer.this.imageMoveAnimation = null;
                    PhotoViewer.this.containerView.invalidate();
                }
            });
            this.imageMoveAnimation.start();
        }
    }

    @Keep
    public void setAnimationValue(float f) {
        this.animationValue = f;
        this.containerView.invalidate();
    }

    @Keep
    public float getAnimationValue() {
        return this.animationValue;
    }

    private void switchToNextIndex(int i, boolean z) {
        if (this.currentMessageObject != null) {
            releasePlayer(false);
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
        }
        setImageIndex(this.currentIndex + i, z);
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.isVideo()) {
            MessageObject messageObject2 = this.currentMessageObject;
            if ((messageObject2.mediaExists || messageObject2.attachPathExists || (messageObject2.canStreamVideo() && SharedConfig.streamMedia)) && SharedConfig.autoplayVideo) {
                onActionClick(true);
                checkProgress(0, false, true);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x050c, code lost:
        if (r4.isLoadingStream() == false) goto L_0x050e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0234  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0316  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0318  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x035d  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x036a  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0389  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x03a3  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03a8  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x03c5  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0437  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x04e9  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x04f5  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x0511  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x056c  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x0628  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0689  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0698  */
    /* JADX WARNING: Removed duplicated region for block: B:286:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"NewApi", "DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r26) {
        /*
            r25 = this;
            r1 = r25
            r2 = r26
            int r0 = r1.animationInProgress
            r3 = 1
            if (r0 == r3) goto L_0x06d1
            r4 = 3
            if (r0 == r4) goto L_0x06d1
            boolean r4 = r1.isVisible
            r5 = 2
            if (r4 != 0) goto L_0x0019
            if (r0 == r5) goto L_0x0019
            boolean r0 = r1.pipAnimationInProgress
            if (r0 != 0) goto L_0x0019
            goto L_0x06d1
        L_0x0019:
            boolean r0 = r1.padImageForHorizontalInsets
            r4 = 0
            if (r0 == 0) goto L_0x0030
            r26.save()
            int r0 = r25.getLeftInset()
            int r0 = r0 / r5
            int r6 = r25.getRightInset()
            int r6 = r6 / r5
            int r0 = r0 - r6
            float r0 = (float) r0
            r2.translate(r0, r4)
        L_0x0030:
            long r6 = java.lang.System.currentTimeMillis()
            long r8 = r1.videoCrossfadeAlphaLastTime
            long r8 = r6 - r8
            r10 = 20
            int r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r0 <= 0) goto L_0x0040
            r8 = 17
        L_0x0040:
            r1.videoCrossfadeAlphaLastTime = r6
            android.animation.AnimatorSet r0 = r1.imageMoveAnimation
            r7 = 0
            r10 = 1065353216(0x3var_, float:1.0)
            if (r0 == 0) goto L_0x0088
            android.widget.Scroller r0 = r1.scroller
            boolean r0 = r0.isFinished()
            if (r0 != 0) goto L_0x0056
            android.widget.Scroller r0 = r1.scroller
            r0.abortAnimation()
        L_0x0056:
            float r0 = r1.scale
            float r11 = r1.animateToScale
            float r12 = r11 - r0
            float r13 = r1.animationValue
            float r12 = r12 * r13
            float r12 = r12 + r0
            float r14 = r1.translationX
            float r15 = r1.animateToX
            float r15 = r15 - r14
            float r15 = r15 * r13
            float r15 = r15 + r14
            float r6 = r1.translationY
            float r5 = r1.animateToY
            float r5 = r5 - r6
            float r5 = r5 * r13
            float r6 = r6 + r5
            int r5 = (r11 > r10 ? 1 : (r11 == r10 ? 0 : -1))
            if (r5 != 0) goto L_0x007f
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 != 0) goto L_0x007f
            int r0 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x007f
            r0 = r6
            goto L_0x0081
        L_0x007f:
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x0081:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r5 = r1.containerView
            r5.invalidate()
            goto L_0x0156
        L_0x0088:
            long r5 = r1.animationStartTime
            r11 = 0
            int r0 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r0 == 0) goto L_0x00a5
            float r0 = r1.animateToX
            r1.translationX = r0
            float r0 = r1.animateToY
            r1.translationY = r0
            float r0 = r1.animateToScale
            r1.scale = r0
            r1.animationStartTime = r11
            float r0 = r1.scale
            r1.updateMinMax(r0)
            r1.zoomAnimation = r7
        L_0x00a5:
            android.widget.Scroller r0 = r1.scroller
            boolean r0 = r0.isFinished()
            if (r0 != 0) goto L_0x0100
            android.widget.Scroller r0 = r1.scroller
            boolean r0 = r0.computeScrollOffset()
            if (r0 == 0) goto L_0x0100
            android.widget.Scroller r0 = r1.scroller
            int r0 = r0.getStartX()
            float r0 = (float) r0
            float r5 = r1.maxX
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x00d8
            android.widget.Scroller r0 = r1.scroller
            int r0 = r0.getStartX()
            float r0 = (float) r0
            float r5 = r1.minX
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x00d8
            android.widget.Scroller r0 = r1.scroller
            int r0 = r0.getCurrX()
            float r0 = (float) r0
            r1.translationX = r0
        L_0x00d8:
            android.widget.Scroller r0 = r1.scroller
            int r0 = r0.getStartY()
            float r0 = (float) r0
            float r5 = r1.maxY
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x00fb
            android.widget.Scroller r0 = r1.scroller
            int r0 = r0.getStartY()
            float r0 = (float) r0
            float r5 = r1.minY
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x00fb
            android.widget.Scroller r0 = r1.scroller
            int r0 = r0.getCurrY()
            float r0 = (float) r0
            r1.translationY = r0
        L_0x00fb:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r0 = r1.containerView
            r0.invalidate()
        L_0x0100:
            int r0 = r1.switchImageAfterAnimation
            if (r0 == 0) goto L_0x0148
            r1.openedFullScreenVideo = r7
            java.util.ArrayList<java.lang.Object> r0 = r1.imagesArrLocals
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x012e
            int r0 = r1.currentIndex
            if (r0 < 0) goto L_0x012e
            java.util.ArrayList<java.lang.Object> r5 = r1.imagesArrLocals
            int r5 = r5.size()
            if (r0 >= r5) goto L_0x012e
            java.util.ArrayList<java.lang.Object> r0 = r1.imagesArrLocals
            int r5 = r1.currentIndex
            java.lang.Object r0 = r0.get(r5)
            boolean r5 = r0 instanceof org.telegram.messenger.MediaController.PhotoEntry
            if (r5 == 0) goto L_0x012e
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            org.telegram.messenger.VideoEditedInfo r5 = r25.getCurrentVideoEditedInfo()
            r0.editedInfo = r5
        L_0x012e:
            int r0 = r1.switchImageAfterAnimation
            if (r0 != r3) goto L_0x013b
            org.telegram.ui.-$$Lambda$PhotoViewer$YNhnzOYkJma59ygxzSKDhfN6VOc r0 = new org.telegram.ui.-$$Lambda$PhotoViewer$YNhnzOYkJma59ygxzSKDhfN6VOc
            r0.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x0146
        L_0x013b:
            r5 = 2
            if (r0 != r5) goto L_0x0146
            org.telegram.ui.-$$Lambda$PhotoViewer$HRUHEM_-JwzoxUbHp12eZwRDyEI r0 = new org.telegram.ui.-$$Lambda$PhotoViewer$HRUHEM_-JwzoxUbHp12eZwRDyEI
            r0.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x0146:
            r1.switchImageAfterAnimation = r7
        L_0x0148:
            float r12 = r1.scale
            float r6 = r1.translationY
            float r15 = r1.translationX
            boolean r0 = r1.moving
            if (r0 != 0) goto L_0x0154
            r0 = r6
            goto L_0x0156
        L_0x0154:
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x0156:
            int r5 = r1.animationInProgress
            r11 = 1132396544(0x437var_, float:255.0)
            r13 = 2
            if (r5 == r13) goto L_0x01a6
            boolean r5 = r1.pipAnimationInProgress
            if (r5 != 0) goto L_0x01a6
            boolean r5 = r1.isInline
            if (r5 != 0) goto L_0x01a6
            int r5 = r1.currentEditMode
            if (r5 != 0) goto L_0x019f
            int r5 = r1.sendPhotoType
            if (r5 == r3) goto L_0x019f
            float r5 = r1.scale
            int r5 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r5 != 0) goto L_0x019f
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r5 == 0) goto L_0x019f
            boolean r5 = r1.zoomAnimation
            if (r5 != 0) goto L_0x019f
            int r5 = r25.getContainerViewHeight()
            float r5 = (float) r5
            r13 = 1082130432(0x40800000, float:4.0)
            float r5 = r5 / r13
            org.telegram.ui.PhotoViewer$BackgroundDrawable r13 = r1.backgroundDrawable
            r14 = 1123942400(0x42fe0000, float:127.0)
            float r0 = java.lang.Math.abs(r0)
            float r0 = java.lang.Math.min(r0, r5)
            float r0 = r0 / r5
            float r0 = r10 - r0
            float r0 = r0 * r11
            float r0 = java.lang.Math.max(r14, r0)
            int r0 = (int) r0
            r13.setAlpha(r0)
            goto L_0x01a6
        L_0x019f:
            org.telegram.ui.PhotoViewer$BackgroundDrawable r0 = r1.backgroundDrawable
            r5 = 255(0xff, float:3.57E-43)
            r0.setAlpha(r5)
        L_0x01a6:
            r5 = 0
            r1.sideImage = r5
            int r0 = r1.currentEditMode
            if (r0 != 0) goto L_0x01f1
            int r0 = r1.sendPhotoType
            if (r0 == r3) goto L_0x01f1
            float r0 = r1.scale
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 < 0) goto L_0x01e8
            boolean r0 = r1.zoomAnimation
            if (r0 != 0) goto L_0x01e8
            boolean r0 = r1.zooming
            if (r0 != 0) goto L_0x01e8
            float r0 = r1.maxX
            r13 = 1084227584(0x40a00000, float:5.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r14 = (float) r14
            float r0 = r0 + r14
            int r0 = (r15 > r0 ? 1 : (r15 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x01d2
            org.telegram.messenger.ImageReceiver r0 = r1.leftImage
            r1.sideImage = r0
            goto L_0x01e8
        L_0x01d2:
            float r0 = r1.minX
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r0 = r0 - r13
            int r0 = (r15 > r0 ? 1 : (r15 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x01e3
            org.telegram.messenger.ImageReceiver r0 = r1.rightImage
            r1.sideImage = r0
            goto L_0x01e8
        L_0x01e3:
            org.telegram.ui.Components.GroupedPhotosListView r0 = r1.groupedPhotosListView
            r0.setMoveProgress(r4)
        L_0x01e8:
            org.telegram.messenger.ImageReceiver r0 = r1.sideImage
            if (r0 == 0) goto L_0x01ee
            r0 = 1
            goto L_0x01ef
        L_0x01ee:
            r0 = 0
        L_0x01ef:
            r1.changingPage = r0
        L_0x01f1:
            org.telegram.messenger.ImageReceiver r0 = r1.sideImage
            org.telegram.messenger.ImageReceiver r13 = r1.rightImage
            r14 = 1050253722(0x3e99999a, float:0.3)
            r16 = 1073741824(0x40000000, float:2.0)
            r18 = 1106247680(0x41var_, float:30.0)
            if (r0 != r13) goto L_0x02df
            boolean r0 = r1.zoomAnimation
            if (r0 != 0) goto L_0x0228
            float r0 = r1.minX
            int r13 = (r15 > r0 ? 1 : (r15 == r0 ? 0 : -1))
            if (r13 >= 0) goto L_0x0228
            float r0 = r0 - r15
            int r13 = r25.getContainerViewWidth()
            float r13 = (float) r13
            float r0 = r0 / r13
            float r0 = java.lang.Math.min(r10, r0)
            float r13 = r10 - r0
            float r13 = r13 * r14
            int r5 = r25.getContainerViewWidth()
            int r5 = -r5
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r17 = 2
            int r19 = r19 / 2
            int r5 = r5 - r19
            float r5 = (float) r5
            goto L_0x022c
        L_0x0228:
            r5 = r15
            r0 = 1065353216(0x3var_, float:1.0)
            r13 = 0
        L_0x022c:
            org.telegram.messenger.ImageReceiver r7 = r1.sideImage
            boolean r7 = r7.hasBitmapImage()
            if (r7 == 0) goto L_0x029f
            r26.save()
            int r7 = r25.getContainerViewWidth()
            r17 = 2
            int r7 = r7 / 2
            float r7 = (float) r7
            int r20 = r25.getContainerViewHeight()
            int r11 = r20 / 2
            float r11 = (float) r11
            r2.translate(r7, r11)
            int r7 = r25.getContainerViewWidth()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r11 = r11 / 2
            int r7 = r7 + r11
            float r7 = (float) r7
            float r7 = r7 + r5
            r2.translate(r7, r4)
            float r7 = r10 - r13
            r2.scale(r7, r7)
            org.telegram.messenger.ImageReceiver r7 = r1.sideImage
            int r7 = r7.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r11 = r1.sideImage
            int r11 = r11.getBitmapHeight()
            int r4 = r25.getContainerViewWidth()
            float r4 = (float) r4
            float r7 = (float) r7
            float r4 = r4 / r7
            int r14 = r25.getContainerViewHeight()
            float r14 = (float) r14
            float r11 = (float) r11
            float r14 = r14 / r11
            int r22 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r22 <= 0) goto L_0x027e
            goto L_0x027f
        L_0x027e:
            r14 = r4
        L_0x027f:
            float r7 = r7 * r14
            int r4 = (int) r7
            float r11 = r11 * r14
            int r7 = (int) r11
            org.telegram.messenger.ImageReceiver r11 = r1.sideImage
            r11.setAlpha(r0)
            org.telegram.messenger.ImageReceiver r11 = r1.sideImage
            int r14 = -r4
            r17 = 2
            int r14 = r14 / 2
            int r3 = -r7
            int r3 = r3 / 2
            r11.setImageCoords(r14, r3, r4, r7)
            org.telegram.messenger.ImageReceiver r3 = r1.sideImage
            r3.draw(r2)
            r26.restore()
        L_0x029f:
            org.telegram.ui.Components.GroupedPhotosListView r3 = r1.groupedPhotosListView
            float r4 = -r0
            r3.setMoveProgress(r4)
            r26.save()
            float r3 = r6 / r12
            r2.translate(r5, r3)
            int r3 = r25.getContainerViewWidth()
            float r3 = (float) r3
            float r4 = r1.scale
            float r4 = r4 + r10
            float r3 = r3 * r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r4 = (float) r4
            float r3 = r3 + r4
            float r3 = r3 / r16
            float r4 = -r6
            float r4 = r4 / r12
            r2.translate(r3, r4)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r3 = r1.photoProgressViews
            r4 = 1
            r3 = r3[r4]
            float r5 = r10 - r13
            r3.setScale(r5)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r3 = r1.photoProgressViews
            r3 = r3[r4]
            r3.setAlpha(r0)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r0 = r0[r4]
            r0.onDraw(r2)
            r26.restore()
        L_0x02df:
            boolean r0 = r1.zoomAnimation
            if (r0 != 0) goto L_0x0308
            float r0 = r1.maxX
            int r3 = (r15 > r0 ? 1 : (r15 == r0 ? 0 : -1))
            if (r3 <= 0) goto L_0x0308
            int r3 = r1.currentEditMode
            if (r3 != 0) goto L_0x0308
            int r3 = r1.sendPhotoType
            r4 = 1
            if (r3 == r4) goto L_0x0308
            float r0 = r15 - r0
            int r3 = r25.getContainerViewWidth()
            float r3 = (float) r3
            float r0 = r0 / r3
            float r0 = java.lang.Math.min(r10, r0)
            r3 = 1050253722(0x3e99999a, float:0.3)
            float r4 = r0 * r3
            float r0 = r10 - r0
            float r3 = r1.maxX
            goto L_0x030c
        L_0x0308:
            r3 = r15
            r0 = 1065353216(0x3var_, float:1.0)
            r4 = 0
        L_0x030c:
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r5 = r1.aspectRatioFrameLayout
            if (r5 == 0) goto L_0x0318
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x0318
            r5 = 1
            goto L_0x0319
        L_0x0318:
            r5 = 0
        L_0x0319:
            org.telegram.messenger.ImageReceiver r7 = r1.centerImage
            boolean r7 = r7.hasBitmapImage()
            if (r7 != 0) goto L_0x032e
            if (r5 == 0) goto L_0x0328
            boolean r7 = r1.textureUploaded
            if (r7 == 0) goto L_0x0328
            goto L_0x032e
        L_0x0328:
            r24 = r4
            r23 = r15
            goto L_0x04e5
        L_0x032e:
            r26.save()
            int r7 = r25.getContainerViewWidth()
            r11 = 2
            int r7 = r7 / r11
            int r13 = r25.getAdditionX()
            int r7 = r7 + r13
            float r7 = (float) r7
            int r13 = r25.getContainerViewHeight()
            int r13 = r13 / r11
            int r11 = r25.getAdditionY()
            int r13 = r13 + r11
            float r11 = (float) r13
            r2.translate(r7, r11)
            int r7 = r1.currentPanTranslationY
            float r7 = (float) r7
            float r7 = r7 + r6
            r2.translate(r3, r7)
            float r7 = r12 - r4
            r2.scale(r7, r7)
            if (r5 == 0) goto L_0x036a
            boolean r7 = r1.textureUploaded
            if (r7 == 0) goto L_0x036a
            android.view.TextureView r7 = r1.videoTextureView
            int r7 = r7.getMeasuredWidth()
            android.view.TextureView r11 = r1.videoTextureView
            int r11 = r11.getMeasuredHeight()
            goto L_0x0376
        L_0x036a:
            org.telegram.messenger.ImageReceiver r7 = r1.centerImage
            int r7 = r7.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r11 = r1.centerImage
            int r11 = r11.getBitmapHeight()
        L_0x0376:
            int r13 = r25.getContainerViewWidth()
            float r13 = (float) r13
            float r7 = (float) r7
            float r13 = r13 / r7
            int r14 = r25.getContainerViewHeight()
            float r14 = (float) r14
            float r11 = (float) r11
            float r14 = r14 / r11
            int r21 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
            if (r21 <= 0) goto L_0x0389
            goto L_0x038a
        L_0x0389:
            r14 = r13
        L_0x038a:
            float r13 = r7 * r14
            int r13 = (int) r13
            float r14 = r14 * r11
            int r14 = (int) r14
            if (r5 == 0) goto L_0x03a8
            boolean r10 = r1.textureUploaded
            if (r10 == 0) goto L_0x03a8
            boolean r10 = r1.videoCrossfadeStarted
            if (r10 == 0) goto L_0x03a8
            float r10 = r1.videoCrossfadeAlpha
            r21 = 1065353216(0x3var_, float:1.0)
            int r10 = (r10 > r21 ? 1 : (r10 == r21 ? 0 : -1))
            if (r10 == 0) goto L_0x03a3
            goto L_0x03a8
        L_0x03a3:
            r24 = r4
            r23 = r15
            goto L_0x03c3
        L_0x03a8:
            org.telegram.messenger.ImageReceiver r10 = r1.centerImage
            r10.setAlpha(r0)
            org.telegram.messenger.ImageReceiver r10 = r1.centerImage
            r23 = r15
            int r15 = -r13
            r17 = 2
            int r15 = r15 / 2
            r24 = r4
            int r4 = -r14
            int r4 = r4 / 2
            r10.setImageCoords(r15, r4, r13, r14)
            org.telegram.messenger.ImageReceiver r4 = r1.centerImage
            r4.draw(r2)
        L_0x03c3:
            if (r5 == 0) goto L_0x042e
            int r4 = r26.getWidth()
            float r4 = (float) r4
            float r4 = r4 / r7
            int r7 = r26.getHeight()
            float r7 = (float) r7
            float r7 = r7 / r11
            int r10 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r10 <= 0) goto L_0x03d6
            r4 = r7
        L_0x03d6:
            float r11 = r11 * r4
            int r4 = (int) r11
            boolean r7 = r1.videoCrossfadeStarted
            if (r7 != 0) goto L_0x03ed
            boolean r7 = r1.textureUploaded
            if (r7 == 0) goto L_0x03ed
            r7 = 1
            r1.videoCrossfadeStarted = r7
            r7 = 0
            r1.videoCrossfadeAlpha = r7
            long r10 = java.lang.System.currentTimeMillis()
            r1.videoCrossfadeAlphaLastTime = r10
        L_0x03ed:
            int r7 = -r13
            r10 = 2
            int r7 = r7 / r10
            float r7 = (float) r7
            int r4 = -r4
            int r4 = r4 / r10
            float r4 = (float) r4
            r2.translate(r7, r4)
            android.view.TextureView r4 = r1.videoTextureView
            float r7 = r1.videoCrossfadeAlpha
            float r7 = r7 * r0
            r4.setAlpha(r7)
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r4 = r1.aspectRatioFrameLayout
            r4.draw(r2)
            boolean r4 = r1.videoCrossfadeStarted
            if (r4 == 0) goto L_0x042e
            float r4 = r1.videoCrossfadeAlpha
            r7 = 1065353216(0x3var_, float:1.0)
            int r10 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r10 >= 0) goto L_0x042e
            float r7 = (float) r8
            boolean r10 = r1.playerInjected
            if (r10 == 0) goto L_0x0419
            r10 = 1120403456(0x42CLASSNAME, float:100.0)
            goto L_0x041b
        L_0x0419:
            r10 = 1128792064(0x43480000, float:200.0)
        L_0x041b:
            float r7 = r7 / r10
            float r4 = r4 + r7
            r1.videoCrossfadeAlpha = r4
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r4 = r1.containerView
            r4.invalidate()
            float r4 = r1.videoCrossfadeAlpha
            r7 = 1065353216(0x3var_, float:1.0)
            int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r4 <= 0) goto L_0x042e
            r1.videoCrossfadeAlpha = r7
        L_0x042e:
            r26.restore()
            r4 = 0
        L_0x0432:
            android.graphics.drawable.GradientDrawable[] r7 = r1.pressedDrawable
            int r7 = r7.length
            if (r4 >= r7) goto L_0x04e5
            boolean[] r7 = r1.drawPressedDrawable
            boolean r7 = r7[r4]
            if (r7 != 0) goto L_0x044a
            float[] r7 = r1.pressedDrawableAlpha
            r7 = r7[r4]
            r10 = 0
            int r7 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r7 == 0) goto L_0x0447
            goto L_0x044a
        L_0x0447:
            r11 = 1132396544(0x437var_, float:255.0)
            goto L_0x049d
        L_0x044a:
            android.graphics.drawable.GradientDrawable[] r7 = r1.pressedDrawable
            r7 = r7[r4]
            float[] r10 = r1.pressedDrawableAlpha
            r10 = r10[r4]
            r11 = 1132396544(0x437var_, float:255.0)
            float r10 = r10 * r11
            int r10 = (int) r10
            r7.setAlpha(r10)
            if (r4 != 0) goto L_0x0473
            android.graphics.drawable.GradientDrawable[] r7 = r1.pressedDrawable
            r7 = r7[r4]
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r10 = r1.containerView
            int r10 = r10.getMeasuredWidth()
            int r10 = r10 / 5
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r13 = r1.containerView
            int r13 = r13.getMeasuredHeight()
            r14 = 0
            r7.setBounds(r14, r14, r10, r13)
            goto L_0x0496
        L_0x0473:
            android.graphics.drawable.GradientDrawable[] r7 = r1.pressedDrawable
            r7 = r7[r4]
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r10 = r1.containerView
            int r10 = r10.getMeasuredWidth()
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r13 = r1.containerView
            int r13 = r13.getMeasuredWidth()
            int r13 = r13 / 5
            int r10 = r10 - r13
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r13 = r1.containerView
            int r13 = r13.getMeasuredWidth()
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r14 = r1.containerView
            int r14 = r14.getMeasuredHeight()
            r15 = 0
            r7.setBounds(r10, r15, r13, r14)
        L_0x0496:
            android.graphics.drawable.GradientDrawable[] r7 = r1.pressedDrawable
            r7 = r7[r4]
            r7.draw(r2)
        L_0x049d:
            boolean[] r7 = r1.drawPressedDrawable
            boolean r7 = r7[r4]
            r10 = 1127481344(0x43340000, float:180.0)
            if (r7 == 0) goto L_0x04c4
            float[] r7 = r1.pressedDrawableAlpha
            r13 = r7[r4]
            r14 = 1065353216(0x3var_, float:1.0)
            int r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
            if (r13 >= 0) goto L_0x04e1
            r13 = r7[r4]
            float r15 = (float) r8
            float r15 = r15 / r10
            float r13 = r13 + r15
            r7[r4] = r13
            r10 = r7[r4]
            int r10 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1))
            if (r10 <= 0) goto L_0x04be
            r7[r4] = r14
        L_0x04be:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r7 = r1.containerView
            r7.invalidate()
            goto L_0x04e1
        L_0x04c4:
            float[] r7 = r1.pressedDrawableAlpha
            r13 = r7[r4]
            r14 = 0
            int r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
            if (r13 <= 0) goto L_0x04e1
            r13 = r7[r4]
            float r15 = (float) r8
            float r15 = r15 / r10
            float r13 = r13 - r15
            r7[r4] = r13
            r10 = r7[r4]
            int r10 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1))
            if (r10 >= 0) goto L_0x04dc
            r7[r4] = r14
        L_0x04dc:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r7 = r1.containerView
            r7.invalidate()
        L_0x04e1:
            int r4 = r4 + 1
            goto L_0x0432
        L_0x04e5:
            boolean r4 = r1.isCurrentVideo
            if (r4 == 0) goto L_0x04f5
            org.telegram.ui.Components.VideoPlayer r4 = r1.videoPlayer
            if (r4 == 0) goto L_0x04f3
            boolean r4 = r4.isPlaying()
            if (r4 != 0) goto L_0x050e
        L_0x04f3:
            r7 = 1
            goto L_0x050f
        L_0x04f5:
            if (r5 != 0) goto L_0x0501
            android.widget.FrameLayout r4 = r1.videoPlayerControlFrameLayout
            int r4 = r4.getVisibility()
            if (r4 == 0) goto L_0x0501
            r7 = 1
            goto L_0x0502
        L_0x0501:
            r7 = 0
        L_0x0502:
            if (r7 == 0) goto L_0x050f
            org.telegram.ui.Components.AnimatedFileDrawable r4 = r1.currentAnimation
            if (r4 == 0) goto L_0x050f
            boolean r4 = r4.isLoadingStream()
            if (r4 != 0) goto L_0x050f
        L_0x050e:
            r7 = 0
        L_0x050f:
            if (r7 == 0) goto L_0x0536
            r26.save()
            float r4 = r6 / r12
            r2.translate(r3, r4)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r4 = r1.photoProgressViews
            r5 = 0
            r4 = r4[r5]
            r7 = 1065353216(0x3var_, float:1.0)
            float r10 = r7 - r24
            r4.setScale(r10)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r4 = r1.photoProgressViews
            r4 = r4[r5]
            r4.setAlpha(r0)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r4 = r1.photoProgressViews
            r4 = r4[r5]
            r4.onDraw(r2)
            r26.restore()
        L_0x0536:
            boolean r4 = r1.pipAnimationInProgress
            if (r4 != 0) goto L_0x0566
            org.telegram.ui.Components.RadialProgressView r4 = r1.miniProgressView
            int r4 = r4.getVisibility()
            if (r4 == 0) goto L_0x0546
            android.animation.AnimatorSet r4 = r1.miniProgressAnimator
            if (r4 == 0) goto L_0x0566
        L_0x0546:
            r26.save()
            org.telegram.ui.Components.RadialProgressView r4 = r1.miniProgressView
            int r4 = r4.getLeft()
            float r4 = (float) r4
            float r4 = r4 + r3
            org.telegram.ui.Components.RadialProgressView r3 = r1.miniProgressView
            int r3 = r3.getTop()
            float r3 = (float) r3
            float r5 = r6 / r12
            float r3 = r3 + r5
            r2.translate(r4, r3)
            org.telegram.ui.Components.RadialProgressView r3 = r1.miniProgressView
            r3.draw(r2)
            r26.restore()
        L_0x0566:
            org.telegram.messenger.ImageReceiver r3 = r1.sideImage
            org.telegram.messenger.ImageReceiver r4 = r1.leftImage
            if (r3 != r4) goto L_0x0624
            boolean r3 = r3.hasBitmapImage()
            if (r3 == 0) goto L_0x05de
            r26.save()
            int r3 = r25.getContainerViewWidth()
            r4 = 2
            int r3 = r3 / r4
            float r3 = (float) r3
            int r5 = r25.getContainerViewHeight()
            int r5 = r5 / r4
            float r4 = (float) r5
            r2.translate(r3, r4)
            int r3 = r25.getContainerViewWidth()
            float r3 = (float) r3
            float r4 = r1.scale
            r5 = 1065353216(0x3var_, float:1.0)
            float r4 = r4 + r5
            float r3 = r3 * r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r4 = (float) r4
            float r3 = r3 + r4
            float r3 = -r3
            float r3 = r3 / r16
            float r3 = r3 + r23
            r4 = 0
            r2.translate(r3, r4)
            org.telegram.messenger.ImageReceiver r3 = r1.sideImage
            int r3 = r3.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r4 = r1.sideImage
            int r4 = r4.getBitmapHeight()
            int r5 = r25.getContainerViewWidth()
            float r5 = (float) r5
            float r3 = (float) r3
            float r5 = r5 / r3
            int r7 = r25.getContainerViewHeight()
            float r7 = (float) r7
            float r4 = (float) r4
            float r7 = r7 / r4
            int r8 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r8 <= 0) goto L_0x05bf
            r5 = r7
        L_0x05bf:
            float r3 = r3 * r5
            int r3 = (int) r3
            float r4 = r4 * r5
            int r4 = (int) r4
            org.telegram.messenger.ImageReceiver r5 = r1.sideImage
            r7 = 1065353216(0x3var_, float:1.0)
            r5.setAlpha(r7)
            org.telegram.messenger.ImageReceiver r5 = r1.sideImage
            int r7 = -r3
            r8 = 2
            int r7 = r7 / r8
            int r9 = -r4
            int r9 = r9 / r8
            r5.setImageCoords(r7, r9, r3, r4)
            org.telegram.messenger.ImageReceiver r3 = r1.sideImage
            r3.draw(r2)
            r26.restore()
        L_0x05de:
            org.telegram.ui.Components.GroupedPhotosListView r3 = r1.groupedPhotosListView
            r4 = 1065353216(0x3var_, float:1.0)
            float r10 = r4 - r0
            r3.setMoveProgress(r10)
            r26.save()
            float r0 = r6 / r12
            r15 = r23
            r2.translate(r15, r0)
            int r0 = r25.getContainerViewWidth()
            float r0 = (float) r0
            float r3 = r1.scale
            float r3 = r3 + r4
            float r0 = r0 * r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r0 = r0 + r3
            float r0 = -r0
            float r0 = r0 / r16
            float r3 = -r6
            float r3 = r3 / r12
            r2.translate(r0, r3)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r3 = 2
            r0 = r0[r3]
            r4 = 1065353216(0x3var_, float:1.0)
            r0.setScale(r4)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r0 = r0[r3]
            r0.setAlpha(r4)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r0 = r0[r3]
            r0.onDraw(r2)
            r26.restore()
        L_0x0624:
            int r0 = r1.waitingForDraw
            if (r0 == 0) goto L_0x0685
            r3 = 1
            int r0 = r0 - r3
            r1.waitingForDraw = r0
            int r0 = r1.waitingForDraw
            if (r0 != 0) goto L_0x0680
            android.widget.ImageView r0 = r1.textureImageView
            if (r0 == 0) goto L_0x0677
            android.view.TextureView r0 = r1.videoTextureView     // Catch:{ all -> 0x0650 }
            int r0 = r0.getWidth()     // Catch:{ all -> 0x0650 }
            android.view.TextureView r3 = r1.videoTextureView     // Catch:{ all -> 0x0650 }
            int r3 = r3.getHeight()     // Catch:{ all -> 0x0650 }
            android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0650 }
            android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createBitmap(r0, r3, r4)     // Catch:{ all -> 0x0650 }
            r1.currentBitmap = r0     // Catch:{ all -> 0x0650 }
            android.view.TextureView r0 = r1.changedTextureView     // Catch:{ all -> 0x0650 }
            android.graphics.Bitmap r3 = r1.currentBitmap     // Catch:{ all -> 0x0650 }
            r0.getBitmap(r3)     // Catch:{ all -> 0x0650 }
            goto L_0x065e
        L_0x0650:
            r0 = move-exception
            android.graphics.Bitmap r3 = r1.currentBitmap
            if (r3 == 0) goto L_0x065b
            r3.recycle()
            r3 = 0
            r1.currentBitmap = r3
        L_0x065b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x065e:
            android.graphics.Bitmap r0 = r1.currentBitmap
            if (r0 == 0) goto L_0x0670
            android.widget.ImageView r0 = r1.textureImageView
            r3 = 0
            r0.setVisibility(r3)
            android.widget.ImageView r0 = r1.textureImageView
            android.graphics.Bitmap r3 = r1.currentBitmap
            r0.setImageBitmap(r3)
            goto L_0x0677
        L_0x0670:
            android.widget.ImageView r0 = r1.textureImageView
            r3 = 0
            r0.setImageDrawable(r3)
            goto L_0x0678
        L_0x0677:
            r3 = 0
        L_0x0678:
            org.telegram.ui.Components.PipVideoView r0 = r1.pipVideoView
            r0.close()
            r1.pipVideoView = r3
            goto L_0x0685
        L_0x0680:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r0 = r1.containerView
            r0.invalidate()
        L_0x0685:
            boolean r0 = r1.padImageForHorizontalInsets
            if (r0 == 0) goto L_0x068c
            r26.restore()
        L_0x068c:
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r0 = r1.aspectRatioFrameLayout
            if (r0 == 0) goto L_0x06d1
            org.telegram.ui.Components.VideoForwardDrawable r0 = r1.videoForwardDrawable
            boolean r0 = r0.isAnimating()
            if (r0 == 0) goto L_0x06d1
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r0 = r1.aspectRatioFrameLayout
            int r0 = r0.getMeasuredHeight()
            float r0 = (float) r0
            float r3 = r1.scale
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r3 - r4
            float r0 = r0 * r3
            int r0 = (int) r0
            r3 = 2
            int r0 = r0 / r3
            org.telegram.ui.Components.VideoForwardDrawable r3 = r1.videoForwardDrawable
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r4 = r1.aspectRatioFrameLayout
            int r4 = r4.getLeft()
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r5 = r1.aspectRatioFrameLayout
            int r5 = r5.getTop()
            int r5 = r5 - r0
            float r6 = r6 / r12
            int r6 = (int) r6
            int r5 = r5 + r6
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r7 = r1.aspectRatioFrameLayout
            int r7 = r7.getRight()
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r8 = r1.aspectRatioFrameLayout
            int r8 = r8.getBottom()
            int r8 = r8 + r0
            int r8 = r8 + r6
            r3.setBounds(r4, r5, r7, r8)
            org.telegram.ui.Components.VideoForwardDrawable r0 = r1.videoForwardDrawable
            r0.draw(r2)
        L_0x06d1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onDraw(android.graphics.Canvas):void");
    }

    public /* synthetic */ void lambda$onDraw$51$PhotoViewer() {
        switchToNextIndex(1, false);
    }

    public /* synthetic */ void lambda$onDraw$52$PhotoViewer() {
        switchToNextIndex(-1, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0037, code lost:
        if (r1.exists() == false) goto L_0x0039;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x013f, code lost:
        if (r0.exists() == false) goto L_0x0180;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x017e, code lost:
        if (r0.exists() == false) goto L_0x0180;
     */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x018c  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x023e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onActionClick(boolean r11) {
        /*
            r10 = this;
            java.lang.String r0 = "UTF-8"
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            if (r1 != 0) goto L_0x000a
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = r10.currentBotInlineResult
            if (r1 == 0) goto L_0x0011
        L_0x000a:
            java.lang.String[] r1 = r10.currentFileNames
            r2 = 0
            r1 = r1[r2]
            if (r1 != 0) goto L_0x0012
        L_0x0011:
            return
        L_0x0012:
            r10.isStreaming = r2
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            java.lang.String r3 = "mp4"
            r4 = 1
            r5 = 0
            if (r1 == 0) goto L_0x012f
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.attachPath
            if (r1 == 0) goto L_0x0039
            int r1 = r1.length()
            if (r1 == 0) goto L_0x0039
            java.io.File r1 = new java.io.File
            org.telegram.messenger.MessageObject r6 = r10.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner
            java.lang.String r6 = r6.attachPath
            r1.<init>(r6)
            boolean r6 = r1.exists()
            if (r6 != 0) goto L_0x003a
        L_0x0039:
            r1 = r5
        L_0x003a:
            if (r1 != 0) goto L_0x012c
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1)
            boolean r6 = r1.exists()
            if (r6 != 0) goto L_0x012c
            boolean r1 = org.telegram.messenger.SharedConfig.streamMedia
            if (r1 == 0) goto L_0x0180
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            long r6 = r1.getDialogId()
            int r1 = (int) r6
            if (r1 == 0) goto L_0x0180
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            boolean r1 = r1.isVideo()
            if (r1 == 0) goto L_0x0180
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            boolean r1 = r1.canStreamVideo()
            if (r1 == 0) goto L_0x0180
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject     // Catch:{ Exception -> 0x0128 }
            int r1 = r1.currentAccount     // Catch:{ Exception -> 0x0128 }
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)     // Catch:{ Exception -> 0x0128 }
            org.telegram.messenger.MessageObject r6 = r10.currentMessageObject     // Catch:{ Exception -> 0x0128 }
            int r1 = r1.getFileReference(r6)     // Catch:{ Exception -> 0x0128 }
            int r6 = r10.currentAccount     // Catch:{ Exception -> 0x0128 }
            org.telegram.messenger.FileLoader r6 = org.telegram.messenger.FileLoader.getInstance(r6)     // Catch:{ Exception -> 0x0128 }
            org.telegram.messenger.MessageObject r7 = r10.currentMessageObject     // Catch:{ Exception -> 0x0128 }
            org.telegram.tgnet.TLRPC$Document r7 = r7.getDocument()     // Catch:{ Exception -> 0x0128 }
            org.telegram.messenger.MessageObject r8 = r10.currentMessageObject     // Catch:{ Exception -> 0x0128 }
            r6.loadFile(r7, r8, r4, r2)     // Catch:{ Exception -> 0x0128 }
            org.telegram.messenger.MessageObject r6 = r10.currentMessageObject     // Catch:{ Exception -> 0x0128 }
            org.telegram.tgnet.TLRPC$Document r6 = r6.getDocument()     // Catch:{ Exception -> 0x0128 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0128 }
            r7.<init>()     // Catch:{ Exception -> 0x0128 }
            java.lang.String r8 = "?account="
            r7.append(r8)     // Catch:{ Exception -> 0x0128 }
            org.telegram.messenger.MessageObject r8 = r10.currentMessageObject     // Catch:{ Exception -> 0x0128 }
            int r8 = r8.currentAccount     // Catch:{ Exception -> 0x0128 }
            r7.append(r8)     // Catch:{ Exception -> 0x0128 }
            java.lang.String r8 = "&id="
            r7.append(r8)     // Catch:{ Exception -> 0x0128 }
            long r8 = r6.id     // Catch:{ Exception -> 0x0128 }
            r7.append(r8)     // Catch:{ Exception -> 0x0128 }
            java.lang.String r8 = "&hash="
            r7.append(r8)     // Catch:{ Exception -> 0x0128 }
            long r8 = r6.access_hash     // Catch:{ Exception -> 0x0128 }
            r7.append(r8)     // Catch:{ Exception -> 0x0128 }
            java.lang.String r8 = "&dc="
            r7.append(r8)     // Catch:{ Exception -> 0x0128 }
            int r8 = r6.dc_id     // Catch:{ Exception -> 0x0128 }
            r7.append(r8)     // Catch:{ Exception -> 0x0128 }
            java.lang.String r8 = "&size="
            r7.append(r8)     // Catch:{ Exception -> 0x0128 }
            int r8 = r6.size     // Catch:{ Exception -> 0x0128 }
            r7.append(r8)     // Catch:{ Exception -> 0x0128 }
            java.lang.String r8 = "&mime="
            r7.append(r8)     // Catch:{ Exception -> 0x0128 }
            java.lang.String r8 = r6.mime_type     // Catch:{ Exception -> 0x0128 }
            java.lang.String r8 = java.net.URLEncoder.encode(r8, r0)     // Catch:{ Exception -> 0x0128 }
            r7.append(r8)     // Catch:{ Exception -> 0x0128 }
            java.lang.String r8 = "&rid="
            r7.append(r8)     // Catch:{ Exception -> 0x0128 }
            r7.append(r1)     // Catch:{ Exception -> 0x0128 }
            java.lang.String r1 = "&name="
            r7.append(r1)     // Catch:{ Exception -> 0x0128 }
            java.lang.String r1 = org.telegram.messenger.FileLoader.getDocumentFileName(r6)     // Catch:{ Exception -> 0x0128 }
            java.lang.String r0 = java.net.URLEncoder.encode(r1, r0)     // Catch:{ Exception -> 0x0128 }
            r7.append(r0)     // Catch:{ Exception -> 0x0128 }
            java.lang.String r0 = "&reference="
            r7.append(r0)     // Catch:{ Exception -> 0x0128 }
            byte[] r0 = r6.file_reference     // Catch:{ Exception -> 0x0128 }
            if (r0 == 0) goto L_0x00f7
            byte[] r0 = r6.file_reference     // Catch:{ Exception -> 0x0128 }
            goto L_0x00f9
        L_0x00f7:
            byte[] r0 = new byte[r2]     // Catch:{ Exception -> 0x0128 }
        L_0x00f9:
            java.lang.String r0 = org.telegram.messenger.Utilities.bytesToHex(r0)     // Catch:{ Exception -> 0x0128 }
            r7.append(r0)     // Catch:{ Exception -> 0x0128 }
            java.lang.String r0 = r7.toString()     // Catch:{ Exception -> 0x0128 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0128 }
            r1.<init>()     // Catch:{ Exception -> 0x0128 }
            java.lang.String r6 = "tg://"
            r1.append(r6)     // Catch:{ Exception -> 0x0128 }
            org.telegram.messenger.MessageObject r6 = r10.currentMessageObject     // Catch:{ Exception -> 0x0128 }
            java.lang.String r6 = r6.getFileName()     // Catch:{ Exception -> 0x0128 }
            r1.append(r6)     // Catch:{ Exception -> 0x0128 }
            r1.append(r0)     // Catch:{ Exception -> 0x0128 }
            java.lang.String r0 = r1.toString()     // Catch:{ Exception -> 0x0128 }
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ Exception -> 0x0128 }
            r10.isStreaming = r4     // Catch:{ Exception -> 0x0129 }
            r10.checkProgress(r2, r2, r2)     // Catch:{ Exception -> 0x0129 }
            goto L_0x0129
        L_0x0128:
            r0 = r5
        L_0x0129:
            r1 = r0
            r0 = r5
            goto L_0x0182
        L_0x012c:
            r0 = r1
        L_0x012d:
            r1 = r5
            goto L_0x0182
        L_0x012f:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.currentBotInlineResult
            if (r0 == 0) goto L_0x0180
            org.telegram.tgnet.TLRPC$Document r1 = r0.document
            if (r1 == 0) goto L_0x0142
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r1)
            boolean r1 = r0.exists()
            if (r1 != 0) goto L_0x012d
            goto L_0x0180
        L_0x0142:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.content
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r0 == 0) goto L_0x0180
            java.io.File r0 = new java.io.File
            r1 = 4
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r1)
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            org.telegram.tgnet.TLRPC$BotInlineResult r7 = r10.currentBotInlineResult
            org.telegram.tgnet.TLRPC$WebDocument r7 = r7.content
            java.lang.String r7 = r7.url
            java.lang.String r7 = org.telegram.messenger.Utilities.MD5(r7)
            r6.append(r7)
            java.lang.String r7 = "."
            r6.append(r7)
            org.telegram.tgnet.TLRPC$BotInlineResult r7 = r10.currentBotInlineResult
            org.telegram.tgnet.TLRPC$WebDocument r7 = r7.content
            java.lang.String r7 = r7.url
            java.lang.String r7 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r7, r3)
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r0.<init>(r1, r6)
            boolean r1 = r0.exists()
            if (r1 != 0) goto L_0x012d
        L_0x0180:
            r0 = r5
            r1 = r0
        L_0x0182:
            if (r0 == 0) goto L_0x018a
            if (r1 != 0) goto L_0x018a
            android.net.Uri r1 = android.net.Uri.fromFile(r0)
        L_0x018a:
            if (r1 != 0) goto L_0x023e
            if (r11 == 0) goto L_0x0255
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            if (r11 == 0) goto L_0x01c5
            int r11 = r10.currentAccount
            org.telegram.messenger.FileLoader r11 = org.telegram.messenger.FileLoader.getInstance(r11)
            java.lang.String[] r0 = r10.currentFileNames
            r0 = r0[r2]
            boolean r11 = r11.isLoadingFile(r0)
            if (r11 != 0) goto L_0x01b5
            int r11 = r10.currentAccount
            org.telegram.messenger.FileLoader r11 = org.telegram.messenger.FileLoader.getInstance(r11)
            org.telegram.messenger.MessageObject r0 = r10.currentMessageObject
            org.telegram.tgnet.TLRPC$Document r0 = r0.getDocument()
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            r11.loadFile(r0, r1, r4, r2)
            goto L_0x022e
        L_0x01b5:
            int r11 = r10.currentAccount
            org.telegram.messenger.FileLoader r11 = org.telegram.messenger.FileLoader.getInstance(r11)
            org.telegram.messenger.MessageObject r0 = r10.currentMessageObject
            org.telegram.tgnet.TLRPC$Document r0 = r0.getDocument()
            r11.cancelLoadFile((org.telegram.tgnet.TLRPC.Document) r0)
            goto L_0x022e
        L_0x01c5:
            org.telegram.tgnet.TLRPC$BotInlineResult r11 = r10.currentBotInlineResult
            if (r11 == 0) goto L_0x022e
            org.telegram.tgnet.TLRPC$Document r0 = r11.document
            if (r0 == 0) goto L_0x01fb
            int r11 = r10.currentAccount
            org.telegram.messenger.FileLoader r11 = org.telegram.messenger.FileLoader.getInstance(r11)
            java.lang.String[] r0 = r10.currentFileNames
            r0 = r0[r2]
            boolean r11 = r11.isLoadingFile(r0)
            if (r11 != 0) goto L_0x01ed
            int r11 = r10.currentAccount
            org.telegram.messenger.FileLoader r11 = org.telegram.messenger.FileLoader.getInstance(r11)
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.currentBotInlineResult
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            r11.loadFile(r0, r1, r4, r2)
            goto L_0x022e
        L_0x01ed:
            int r11 = r10.currentAccount
            org.telegram.messenger.FileLoader r11 = org.telegram.messenger.FileLoader.getInstance(r11)
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.currentBotInlineResult
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            r11.cancelLoadFile((org.telegram.tgnet.TLRPC.Document) r0)
            goto L_0x022e
        L_0x01fb:
            org.telegram.tgnet.TLRPC$WebDocument r11 = r11.content
            boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC.TL_webDocument
            if (r11 == 0) goto L_0x022e
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.currentBotInlineResult
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.content
            java.lang.String r0 = r0.url
            boolean r11 = r11.isLoadingHttpFile(r0)
            if (r11 != 0) goto L_0x0221
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.currentBotInlineResult
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.content
            java.lang.String r0 = r0.url
            int r1 = r10.currentAccount
            r11.loadHttpFile(r0, r3, r1)
            goto L_0x022e
        L_0x0221:
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.currentBotInlineResult
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.content
            java.lang.String r0 = r0.url
            r11.cancelLoadHttpFile(r0)
        L_0x022e:
            org.telegram.messenger.ImageReceiver r11 = r10.centerImage
            android.graphics.drawable.Drawable r11 = r11.getStaticThumb()
            boolean r0 = r11 instanceof org.telegram.ui.Components.OtherDocumentPlaceholderDrawable
            if (r0 == 0) goto L_0x0255
            org.telegram.ui.Components.OtherDocumentPlaceholderDrawable r11 = (org.telegram.ui.Components.OtherDocumentPlaceholderDrawable) r11
            r11.checkFileExist()
            goto L_0x0255
        L_0x023e:
            int r11 = r10.sharedMediaType
            if (r11 != r4) goto L_0x0252
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            boolean r11 = r11.canPreviewDocument()
            if (r11 != 0) goto L_0x0252
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            android.app.Activity r0 = r10.parentActivity
            org.telegram.messenger.AndroidUtilities.openDocument(r11, r0, r5)
            return
        L_0x0252:
            r10.preparePlayer(r1, r4, r2)
        L_0x0255:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onActionClick(boolean):void");
    }

    public boolean onDown(MotionEvent motionEvent) {
        if (this.checkImageView.getVisibility() != 0) {
            boolean[] zArr = this.drawPressedDrawable;
            if (!zArr[0] && !zArr[1]) {
                float x = motionEvent.getX();
                int measuredWidth = this.containerView.getMeasuredWidth() / 5;
                if (x < ((float) measuredWidth)) {
                    if (this.leftImage.hasImageSet()) {
                        this.drawPressedDrawable[0] = true;
                        this.containerView.invalidate();
                    }
                } else if (x > ((float) (this.containerView.getMeasuredWidth() - measuredWidth)) && this.rightImage.hasImageSet()) {
                    this.drawPressedDrawable[1] = true;
                    this.containerView.invalidate();
                }
            }
        }
        return false;
    }

    private void hidePressedDrawables() {
        boolean[] zArr = this.drawPressedDrawable;
        zArr[1] = false;
        zArr[0] = false;
        this.containerView.invalidate();
    }

    public void onUp(MotionEvent motionEvent) {
        hidePressedDrawables();
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        if (this.canZoom || this.doubleTapEnabled) {
            return false;
        }
        return onSingleTapConfirmed(motionEvent);
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (this.scale == 1.0f) {
            return false;
        }
        this.scroller.abortAnimation();
        this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(f), Math.round(f2), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
        this.containerView.postInvalidate();
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        int access$12600;
        MessageObject messageObject;
        if (this.discardTap) {
            return false;
        }
        float x = motionEvent.getX();
        if (this.checkImageView.getVisibility() != 0) {
            int measuredWidth = this.containerView.getMeasuredWidth() / 5;
            if (x < ((float) measuredWidth)) {
                if (this.leftImage.hasImageSet()) {
                    switchToNextIndex(-1, true);
                    return true;
                }
            } else if (x > ((float) (this.containerView.getMeasuredWidth() - measuredWidth)) && this.rightImage.hasImageSet()) {
                switchToNextIndex(1, true);
                return true;
            }
        }
        if (this.containerView.getTag() != null) {
            AspectRatioFrameLayout aspectRatioFrameLayout2 = this.aspectRatioFrameLayout;
            boolean z = aspectRatioFrameLayout2 != null && aspectRatioFrameLayout2.getVisibility() == 0;
            float y = motionEvent.getY();
            if (this.sharedMediaType != 1 || (messageObject = this.currentMessageObject) == null) {
                PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
                if (photoProgressViewArr[0] != null && this.containerView != null && !z && (access$12600 = photoProgressViewArr[0].backgroundState) > 0 && access$12600 <= 3 && x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                    onActionClick(true);
                    checkProgress(0, false, true);
                    return true;
                }
            } else if (!messageObject.canPreviewDocument()) {
                float containerViewHeight = ((float) (getContainerViewHeight() - AndroidUtilities.dp(360.0f))) / 2.0f;
                if (y >= containerViewHeight && y <= containerViewHeight + ((float) AndroidUtilities.dp(360.0f))) {
                    onActionClick(true);
                    return true;
                }
            }
            toggleActionBar(!this.isActionBarVisible, true);
        } else {
            int i = this.sendPhotoType;
            if (i != 0 && i != 4) {
                TLRPC.BotInlineResult botInlineResult = this.currentBotInlineResult;
                if (botInlineResult != null && (botInlineResult.type.equals("video") || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                    int access$126002 = this.photoProgressViews[0].backgroundState;
                    if (access$126002 > 0 && access$126002 <= 3) {
                        float y2 = motionEvent.getY();
                        if (x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y2 >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y2 <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                            onActionClick(true);
                            checkProgress(0, false, true);
                            return true;
                        }
                    }
                } else if (this.sendPhotoType == 2 && this.isCurrentVideo) {
                    this.videoPlayButton.callOnClick();
                }
            } else if (this.isCurrentVideo) {
                this.videoPlayButton.callOnClick();
            } else {
                this.checkImageView.performClick();
            }
        }
        return true;
    }

    public boolean onDoubleTap(MotionEvent motionEvent) {
        boolean z = false;
        long j = 0;
        if (this.videoPlayer != null && this.videoPlayerControlFrameLayout.getVisibility() == 0) {
            long currentPosition = this.videoPlayer.getCurrentPosition();
            long duration = this.videoPlayer.getDuration();
            if (duration >= 0 && currentPosition >= 0 && duration != -9223372036854775807L && currentPosition != -9223372036854775807L) {
                int containerViewWidth = getContainerViewWidth();
                float x = motionEvent.getX();
                int i = containerViewWidth / 3;
                long j2 = x >= ((float) (i * 2)) ? 10000 + currentPosition : x < ((float) i) ? currentPosition - 10000 : currentPosition;
                if (currentPosition != j2) {
                    if (j2 > duration) {
                        j = duration;
                    } else if (j2 >= 0) {
                        j = j2;
                    }
                    VideoForwardDrawable videoForwardDrawable2 = this.videoForwardDrawable;
                    if (x < ((float) i)) {
                        z = true;
                    }
                    videoForwardDrawable2.setLeftSide(z);
                    this.videoPlayer.seekTo(j);
                    this.containerView.invalidate();
                    this.videoPlayerSeekbar.setProgress(((float) j) / ((float) duration));
                    this.videoPlayerControlFrameLayout.invalidate();
                    return true;
                }
            }
        }
        if (!this.canZoom || ((this.scale == 1.0f && (this.translationY != 0.0f || this.translationX != 0.0f)) || this.animationStartTime != 0 || this.animationInProgress != 0)) {
            return false;
        }
        if (this.scale == 1.0f) {
            float x2 = (motionEvent.getX() - ((float) (getContainerViewWidth() / 2))) - (((motionEvent.getX() - ((float) (getContainerViewWidth() / 2))) - this.translationX) * (3.0f / this.scale));
            float y = (motionEvent.getY() - ((float) (getContainerViewHeight() / 2))) - (((motionEvent.getY() - ((float) (getContainerViewHeight() / 2))) - this.translationY) * (3.0f / this.scale));
            updateMinMax(3.0f);
            float f = this.minX;
            if (x2 >= f) {
                f = this.maxX;
                if (x2 <= f) {
                    f = x2;
                }
            }
            float f2 = this.minY;
            if (y >= f2) {
                f2 = this.maxY;
                if (y <= f2) {
                    f2 = y;
                }
            }
            animateTo(3.0f, f, f2, true);
        } else {
            animateTo(1.0f, 0.0f, 0.0f, true);
        }
        this.doubleTap = true;
        return true;
    }

    private class QualityChooseView extends View {
        private int circleSize;
        private int gapSize;
        private String hightQualityDescription;
        private int lineSize;
        private String lowQualityDescription;
        private Paint paint = new Paint(1);
        private int sideSide;
        private int startMovingQuality;
        private TextPaint textPaint = new TextPaint(1);

        public QualityChooseView(Context context) {
            super(context);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            this.textPaint.setColor(-3289651);
            this.lowQualityDescription = LocaleController.getString("AccDescrVideoCompressLow", NUM);
            this.hightQualityDescription = LocaleController.getString("AccDescrVideoCompressHigh", NUM);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            if (motionEvent.getAction() == 0) {
                this.startMovingQuality = PhotoViewer.this.selectedCompression;
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                int i = 0;
                while (true) {
                    if (i >= PhotoViewer.this.compressionsCount) {
                        break;
                    }
                    int i2 = this.sideSide;
                    int i3 = this.lineSize;
                    int i4 = this.gapSize;
                    int i5 = this.circleSize;
                    int i6 = i2 + (((i4 * 2) + i3 + i5) * i) + (i5 / 2);
                    int i7 = (i3 / 2) + (i5 / 2) + i4;
                    if (x <= ((float) (i6 - i7)) || x >= ((float) (i6 + i7))) {
                        i++;
                    } else if (PhotoViewer.this.selectedCompression != i) {
                        int unused = PhotoViewer.this.selectedCompression = i;
                        PhotoViewer.this.didChangedCompressionLevel(false);
                        invalidate();
                    }
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (PhotoViewer.this.selectedCompression != this.startMovingQuality) {
                    PhotoViewer.this.requestVideoPreview(1);
                }
                boolean unused2 = PhotoViewer.this.moving = false;
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            this.circleSize = AndroidUtilities.dp(8.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(18.0f);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (PhotoViewer.this.compressionsCount != 1) {
                this.lineSize = (((getMeasuredWidth() - (this.circleSize * PhotoViewer.this.compressionsCount)) - (this.gapSize * ((PhotoViewer.this.compressionsCount * 2) - 2))) - (this.sideSide * 2)) / (PhotoViewer.this.compressionsCount - 1);
            } else {
                this.lineSize = ((getMeasuredWidth() - (this.circleSize * PhotoViewer.this.compressionsCount)) - (this.gapSize * 2)) - (this.sideSide * 2);
            }
            int measuredHeight = (getMeasuredHeight() / 2) + AndroidUtilities.dp(6.0f);
            int i = 0;
            while (i < PhotoViewer.this.compressionsCount) {
                int i2 = this.sideSide;
                int i3 = this.lineSize + (this.gapSize * 2);
                int i4 = this.circleSize;
                int i5 = i2 + ((i3 + i4) * i) + (i4 / 2);
                if (i <= PhotoViewer.this.selectedCompression) {
                    this.paint.setColor(-11292945);
                } else {
                    this.paint.setColor(NUM);
                }
                canvas.drawCircle((float) i5, (float) measuredHeight, (float) (i == PhotoViewer.this.selectedCompression ? AndroidUtilities.dp(6.0f) : this.circleSize / 2), this.paint);
                if (i != 0) {
                    int i6 = ((i5 - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    float f = 0.0f;
                    float dpf2 = i == PhotoViewer.this.selectedCompression + 1 ? AndroidUtilities.dpf2(2.0f) : 0.0f;
                    if (i == PhotoViewer.this.selectedCompression) {
                        f = AndroidUtilities.dpf2(2.0f);
                    }
                    canvas.drawRect(dpf2 + ((float) i6), (float) (measuredHeight - AndroidUtilities.dp(1.0f)), ((float) (i6 + this.lineSize)) - f, (float) (AndroidUtilities.dp(2.0f) + measuredHeight), this.paint);
                }
                i++;
            }
            canvas.drawText(this.lowQualityDescription, (float) this.sideSide, (float) (measuredHeight - AndroidUtilities.dp(16.0f)), this.textPaint);
            canvas.drawText(this.hightQualityDescription, ((float) (getMeasuredWidth() - this.sideSide)) - this.textPaint.measureText(this.hightQualityDescription), (float) (measuredHeight - AndroidUtilities.dp(16.0f)), this.textPaint);
        }
    }

    public void updateMuteButton() {
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.setMute(this.muteVideo);
        }
        if (!this.videoConvertSupported) {
            this.muteItem.setEnabled(false);
            this.muteItem.setClickable(false);
            this.muteItem.animate().alpha(0.5f).setDuration(180).start();
            return;
        }
        this.muteItem.setEnabled(true);
        this.muteItem.setClickable(true);
        this.muteItem.animate().alpha(1.0f).setDuration(180).start();
        if (this.muteVideo) {
            this.actionBar.setSubtitle((CharSequence) null);
            this.muteItem.setImageResource(NUM);
            this.muteItem.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
            if (this.compressItem.getTag() != null) {
                this.compressItem.setAlpha(0.5f);
            }
            this.videoTimelineView.setMaxProgressDiff(30000.0f / this.videoDuration);
            this.muteItem.setContentDescription(LocaleController.getString("NoSound", NUM));
            return;
        }
        this.muteItem.setColorFilter((ColorFilter) null);
        this.actionBar.setSubtitle(this.currentSubtitle);
        this.muteItem.setImageResource(NUM);
        this.muteItem.setContentDescription(LocaleController.getString("Sound", NUM));
        if (this.compressItem.getTag() != null) {
            this.compressItem.setAlpha(1.0f);
        }
        this.videoTimelineView.setMaxProgressDiff(1.0f);
    }

    /* access modifiers changed from: private */
    public void didChangedCompressionLevel(boolean z) {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("compress_video2", this.selectedCompression);
        edit.commit();
        updateWidthHeightBitrateForCompression();
        updateVideoInfo();
        if (z) {
            requestVideoPreview(1);
        }
    }

    /* access modifiers changed from: private */
    public void updateVideoInfo() {
        int i;
        int i2;
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            String str = null;
            if (this.compressionsCount == 0) {
                actionBar2.setSubtitle((CharSequence) null);
                return;
            }
            int i3 = this.selectedCompression;
            int i4 = 64;
            if (i3 < 2) {
                this.compressItem.setImageResource(NUM);
                i4 = 48;
            } else if (i3 == 2) {
                this.compressItem.setImageResource(NUM);
            } else if (i3 == 3) {
                this.compressItem.setImageResource(NUM);
            }
            float f = (float) (70 - i4);
            this.compressItem.setPadding(AndroidUtilities.dp(f) / 2, 0, AndroidUtilities.dp(f) / 2, 0);
            this.compressItem.requestLayout();
            ImageView imageView = this.compressItem;
            imageView.setContentDescription(LocaleController.getString("AccDescrVideoQuality", NUM) + ", " + new String[]{"360", "480", "720", "1080"}[Math.max(0, this.selectedCompression)]);
            this.estimatedDuration = (long) Math.ceil((double) ((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration));
            if (this.compressItem.getTag() == null) {
                int i5 = this.rotationValue;
                i2 = (i5 == 90 || i5 == 270) ? this.originalHeight : this.originalWidth;
                int i6 = this.rotationValue;
                i = (i6 == 90 || i6 == 270) ? this.originalWidth : this.originalHeight;
                this.estimatedSize = (int) (((float) this.originalSize) * (((float) this.estimatedDuration) / this.videoDuration));
            } else {
                int i7 = this.rotationValue;
                i2 = (i7 == 90 || i7 == 270) ? this.resultHeight : this.resultWidth;
                int i8 = this.rotationValue;
                i = (i8 == 90 || i8 == 270) ? this.resultWidth : this.resultHeight;
                this.estimatedSize = (int) (((float) (this.audioFramesSize + this.videoFramesSize)) * (((float) this.estimatedDuration) / this.videoDuration));
                int i9 = this.estimatedSize;
                this.estimatedSize = i9 + ((i9 / 32768) * 16);
            }
            this.videoCutStart = this.videoTimelineView.getLeftProgress();
            this.videoCutEnd = this.videoTimelineView.getRightProgress();
            float f2 = this.videoCutStart;
            if (f2 == 0.0f) {
                this.startTime = -1;
            } else {
                this.startTime = ((long) (f2 * this.videoDuration)) * 1000;
            }
            float f3 = this.videoCutEnd;
            if (f3 == 1.0f) {
                this.endTime = -1;
            } else {
                this.endTime = ((long) (f3 * this.videoDuration)) * 1000;
            }
            this.currentSubtitle = String.format("%s, %s", new Object[]{String.format("%dx%d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i)}), String.format("%s, ~%s", new Object[]{AndroidUtilities.formatShortDuration((int) (this.estimatedDuration / 1000)), AndroidUtilities.formatFileSize((long) this.estimatedSize)})});
            ActionBar actionBar3 = this.actionBar;
            if (!this.muteVideo) {
                str = this.currentSubtitle;
            }
            actionBar3.setSubtitle(str);
        }
    }

    /* access modifiers changed from: private */
    public void requestVideoPreview(int i) {
        if (this.videoPreviewMessageObject != null) {
            MediaController.getInstance().cancelVideoConvert(this.videoPreviewMessageObject);
        }
        boolean z = true;
        boolean z2 = this.requestingPreview && !this.tryStartRequestPreviewOnFinish;
        this.requestingPreview = false;
        this.loadInitialVideo = false;
        this.progressView.setVisibility(4);
        if (i != 1) {
            this.tryStartRequestPreviewOnFinish = false;
            this.photoProgressViews[0].setBackgroundState(3, false);
            if (i == 2) {
                preparePlayer(this.currentPlayingVideoFile, false, false);
                this.videoPlayer.seekTo((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration));
            }
        } else if (this.resultHeight == this.originalHeight && this.resultWidth == this.originalWidth) {
            this.tryStartRequestPreviewOnFinish = false;
            PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
            photoProgressViewArr[0].setProgress(0.0f, photoProgressViewArr[0].backgroundState == 0 || this.photoProgressViews[0].previousBackgroundState == 0);
            this.photoProgressViews[0].setBackgroundState(3, false);
            if (!z2) {
                preparePlayer(this.currentPlayingVideoFile, false, false);
                this.videoPlayer.seekTo((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration));
            } else {
                this.loadInitialVideo = true;
            }
        } else {
            releasePlayer(false);
            if (this.videoPreviewMessageObject == null) {
                TLRPC.TL_message tL_message = new TLRPC.TL_message();
                tL_message.id = 0;
                tL_message.message = "";
                tL_message.media = new TLRPC.TL_messageMediaEmpty();
                tL_message.action = new TLRPC.TL_messageActionEmpty();
                tL_message.dialog_id = this.currentDialogId;
                this.videoPreviewMessageObject = new MessageObject(UserConfig.selectedAccount, tL_message, false);
                this.videoPreviewMessageObject.messageOwner.attachPath = new File(FileLoader.getDirectory(4), "video_preview.mp4").getAbsolutePath();
                this.videoPreviewMessageObject.videoEditedInfo = new VideoEditedInfo();
                VideoEditedInfo videoEditedInfo = this.videoPreviewMessageObject.videoEditedInfo;
                videoEditedInfo.rotationValue = this.rotationValue;
                videoEditedInfo.originalWidth = this.originalWidth;
                videoEditedInfo.originalHeight = this.originalHeight;
                videoEditedInfo.framerate = this.videoFramerate;
                videoEditedInfo.originalPath = this.currentPlayingVideoFile.getPath();
            }
            VideoEditedInfo videoEditedInfo2 = this.videoPreviewMessageObject.videoEditedInfo;
            long j = this.startTime;
            videoEditedInfo2.startTime = j;
            long j2 = this.endTime;
            videoEditedInfo2.endTime = j2;
            if (j == -1) {
                j = 0;
            }
            if (j2 == -1) {
                j2 = (long) (this.videoDuration * 1000.0f);
            }
            if (j2 - j > 5000000) {
                this.videoPreviewMessageObject.videoEditedInfo.endTime = j + 5000000;
            }
            VideoEditedInfo videoEditedInfo3 = this.videoPreviewMessageObject.videoEditedInfo;
            videoEditedInfo3.bitrate = this.bitrate;
            videoEditedInfo3.resultWidth = this.resultWidth;
            videoEditedInfo3.resultHeight = this.resultHeight;
            videoEditedInfo3.needUpdateProgress = true;
            videoEditedInfo3.originalDuration = (long) (this.videoDuration * 1000.0f);
            if (!MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true)) {
                this.tryStartRequestPreviewOnFinish = true;
            }
            this.requestingPreview = true;
            PhotoProgressView[] photoProgressViewArr2 = this.photoProgressViews;
            PhotoProgressView photoProgressView = photoProgressViewArr2[0];
            if (!(photoProgressViewArr2[0].backgroundState == 0 || this.photoProgressViews[0].previousBackgroundState == 0)) {
                z = false;
            }
            photoProgressView.setProgress(0.0f, z);
            this.photoProgressViews[0].setBackgroundState(0, false);
        }
        this.containerView.invalidate();
    }

    /* access modifiers changed from: private */
    public void updateWidthHeightBitrateForCompression() {
        int i = this.compressionsCount;
        if (i > 0) {
            if (this.selectedCompression >= i) {
                this.selectedCompression = i - 1;
            }
            int i2 = this.selectedCompression;
            float f = i2 != 0 ? i2 != 1 ? i2 != 2 ? 1920.0f : 1280.0f : 854.0f : 480.0f;
            int i3 = this.originalWidth;
            int i4 = this.originalHeight;
            float f2 = f / (i3 > i4 ? (float) i3 : (float) i4);
            if (this.selectedCompression != this.compressionsCount - 1 || f2 < 1.0f) {
                this.resultWidth = Math.round((((float) this.originalWidth) * f2) / 2.0f) * 2;
                this.resultHeight = Math.round((((float) this.originalHeight) * f2) / 2.0f) * 2;
            } else {
                this.resultWidth = this.originalWidth;
                this.resultHeight = this.originalHeight;
            }
            if (this.bitrate != 0) {
                if (this.resultWidth == this.originalWidth && this.resultHeight == this.originalHeight) {
                    this.bitrate = this.originalBitrate;
                } else {
                    this.bitrate = MediaController.makeVideoBitrate(this.originalHeight, this.originalWidth, this.originalBitrate, this.resultHeight, this.resultWidth);
                }
                this.videoFramesSize = (long) ((((float) (this.bitrate / 8)) * this.videoDuration) / 1000.0f);
            }
        }
    }

    private void showQualityView(final boolean z) {
        if (z) {
            this.previousCompression = this.selectedCompression;
        }
        AnimatorSet animatorSet = this.qualityChooseViewAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.qualityChooseViewAnimation = new AnimatorSet();
        float f = 0.0f;
        if (z) {
            this.qualityChooseView.setTag(1);
            this.qualityChooseViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.pickerView, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(152.0f)}), ObjectAnimator.ofFloat(this.pickerViewSendButton, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(152.0f)}), ObjectAnimator.ofFloat(this.bottomLayout, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f)), (float) AndroidUtilities.dp(104.0f)})});
        } else {
            this.qualityChooseView.setTag((Object) null);
            this.qualityChooseViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.qualityChooseView, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(166.0f)}), ObjectAnimator.ofFloat(this.qualityPicker, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(166.0f)}), ObjectAnimator.ofFloat(this.bottomLayout, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f)), (float) AndroidUtilities.dp(118.0f)})});
        }
        this.qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(PhotoViewer.this.qualityChooseViewAnimation)) {
                    AnimatorSet unused = PhotoViewer.this.qualityChooseViewAnimation = new AnimatorSet();
                    if (z) {
                        PhotoViewer.this.qualityChooseView.setVisibility(0);
                        PhotoViewer.this.qualityPicker.setVisibility(0);
                        PhotoViewer.this.qualityChooseViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.qualityChooseView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.qualityPicker, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))})});
                    } else {
                        PhotoViewer.this.qualityChooseView.setVisibility(4);
                        PhotoViewer.this.qualityPicker.setVisibility(4);
                        PhotoViewer.this.qualityChooseViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.pickerViewSendButton, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))})});
                    }
                    PhotoViewer.this.qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(PhotoViewer.this.qualityChooseViewAnimation)) {
                                AnimatorSet unused = PhotoViewer.this.qualityChooseViewAnimation = null;
                            }
                        }
                    });
                    PhotoViewer.this.qualityChooseViewAnimation.setDuration(200);
                    PhotoViewer.this.qualityChooseViewAnimation.setInterpolator(new AccelerateInterpolator());
                    PhotoViewer.this.qualityChooseViewAnimation.start();
                }
            }

            public void onAnimationCancel(Animator animator) {
                AnimatorSet unused = PhotoViewer.this.qualityChooseViewAnimation = null;
            }
        });
        this.qualityChooseViewAnimation.setDuration(200);
        this.qualityChooseViewAnimation.setInterpolator(new DecelerateInterpolator());
        this.qualityChooseViewAnimation.start();
        if (this.cameraItem.getVisibility() == 0) {
            float f2 = 0.25f;
            ViewPropertyAnimator scaleX = this.cameraItem.animate().scaleX(z ? 0.25f : 1.0f);
            if (!z) {
                f2 = 1.0f;
            }
            ViewPropertyAnimator scaleY = scaleX.scaleY(f2);
            if (!z) {
                f = 1.0f;
            }
            scaleY.alpha(f).setDuration(200);
        }
    }

    private ByteArrayInputStream cleanBuffer(byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length];
        int i = 0;
        int i2 = 0;
        while (i < bArr.length) {
            if (bArr[i] == 0 && bArr[i + 1] == 0 && bArr[i + 2] == 3) {
                bArr2[i2] = 0;
                bArr2[i2 + 1] = 0;
                i += 3;
                i2 += 2;
            } else {
                bArr2[i2] = bArr[i];
                i++;
                i2++;
            }
        }
        return new ByteArrayInputStream(bArr2, 0, i2);
    }

    private void processOpenVideo(final String str, boolean z, float f, float f2) {
        if (this.currentLoadingVideoRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.currentLoadingVideoRunnable);
            this.currentLoadingVideoRunnable = null;
        }
        this.videoTimelineView.setVideoPath(str, f, f2);
        this.videoPreviewMessageObject = null;
        this.muteVideo = z;
        Object obj = this.imagesArrLocals.get(this.currentIndex);
        if (obj instanceof MediaController.PhotoEntry) {
            ((MediaController.PhotoEntry) obj).editedInfo = getCurrentVideoEditedInfo();
        }
        this.compressionsCount = -1;
        this.rotationValue = 0;
        this.videoFramerate = 25;
        this.originalSize = new File(str).length();
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        AnonymousClass46 r6 = new Runnable() {
            public void run() {
                if (PhotoViewer.this.currentLoadingVideoRunnable == this) {
                    int videoBitrate = MediaController.getVideoBitrate(str);
                    int[] iArr = new int[11];
                    AnimatedFileDrawable.getVideoInfo(str, iArr);
                    if (PhotoViewer.this.currentLoadingVideoRunnable == this) {
                        Runnable unused = PhotoViewer.this.currentLoadingVideoRunnable = null;
                        AndroidUtilities.runOnUIThread(new Runnable(iArr, videoBitrate) {
                            private final /* synthetic */ int[] f$1;
                            private final /* synthetic */ int f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                PhotoViewer.AnonymousClass46.this.lambda$run$0$PhotoViewer$46(this.f$1, this.f$2);
                            }
                        });
                    }
                }
            }

            public /* synthetic */ void lambda$run$0$PhotoViewer$46(int[] iArr, int i) {
                if (PhotoViewer.this.parentActivity != null) {
                    boolean unused = PhotoViewer.this.videoConvertSupported = iArr[0] != 0 && (!(iArr[10] != 0) || iArr[9] != 0);
                    long unused2 = PhotoViewer.this.audioFramesSize = (long) iArr[5];
                    float unused3 = PhotoViewer.this.videoDuration = (float) iArr[4];
                    PhotoViewer photoViewer = PhotoViewer.this;
                    int unused4 = photoViewer.originalBitrate = photoViewer.bitrate = i;
                    int unused5 = PhotoViewer.this.videoFramerate = iArr[7];
                    PhotoViewer photoViewer2 = PhotoViewer.this;
                    long unused6 = photoViewer2.videoFramesSize = (long) ((((float) (photoViewer2.bitrate / 8)) * PhotoViewer.this.videoDuration) / 1000.0f);
                    if (PhotoViewer.this.videoConvertSupported) {
                        int unused7 = PhotoViewer.this.rotationValue = iArr[8];
                        PhotoViewer photoViewer3 = PhotoViewer.this;
                        int unused8 = photoViewer3.resultWidth = photoViewer3.originalWidth = iArr[1];
                        PhotoViewer photoViewer4 = PhotoViewer.this;
                        int unused9 = photoViewer4.resultHeight = photoViewer4.originalHeight = iArr[2];
                        PhotoViewer photoViewer5 = PhotoViewer.this;
                        photoViewer5.updateCompressionsCount(photoViewer5.originalWidth, PhotoViewer.this.originalHeight);
                        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                        PhotoViewer photoViewer6 = PhotoViewer.this;
                        int unused10 = photoViewer6.selectedCompression = globalMainSettings.getInt("compress_video2", photoViewer6.selectCompression());
                        PhotoViewer.this.updateWidthHeightBitrateForCompression();
                        if (PhotoViewer.this.selectedCompression > PhotoViewer.this.compressionsCount - 1) {
                            PhotoViewer photoViewer7 = PhotoViewer.this;
                            int unused11 = photoViewer7.selectedCompression = photoViewer7.compressionsCount - 1;
                        }
                        PhotoViewer photoViewer8 = PhotoViewer.this;
                        photoViewer8.setCompressItemEnabled(photoViewer8.compressionsCount > 1, true);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("compressionsCount = " + PhotoViewer.this.compressionsCount + " w = " + PhotoViewer.this.originalWidth + " h = " + PhotoViewer.this.originalHeight);
                        }
                        if (Build.VERSION.SDK_INT < 18 && PhotoViewer.this.compressItem.getTag() != null) {
                            try {
                                MediaCodecInfo selectCodec = MediaController.selectCodec("video/avc");
                                if (selectCodec == null) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("no codec info for video/avc");
                                    }
                                    boolean unused12 = PhotoViewer.this.videoConvertSupported = false;
                                    PhotoViewer.this.setCompressItemEnabled(false, true);
                                } else {
                                    String name = selectCodec.getName();
                                    if (!name.equals("OMX.google.h264.encoder") && !name.equals("OMX.ST.VFM.H264Enc") && !name.equals("OMX.Exynos.avc.enc") && !name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") && !name.equals("OMX.MARVELL.VIDEO.H264ENCODER") && !name.equals("OMX.k3.video.encoder.avc")) {
                                        if (!name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                                            if (MediaController.selectColorFormat(selectCodec, "video/avc") == 0) {
                                                if (BuildVars.LOGS_ENABLED) {
                                                    FileLog.d("no color format for video/avc");
                                                }
                                                PhotoViewer.this.setCompressItemEnabled(false, true);
                                            }
                                        }
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("unsupported encoder = " + name);
                                    }
                                    boolean unused13 = PhotoViewer.this.videoConvertSupported = false;
                                    PhotoViewer.this.setCompressItemEnabled(false, true);
                                }
                            } catch (Exception e) {
                                PhotoViewer.this.setCompressItemEnabled(false, true);
                                FileLog.e((Throwable) e);
                            }
                        }
                        PhotoViewer.this.qualityChooseView.invalidate();
                    } else {
                        PhotoViewer.this.setCompressItemEnabled(false, true);
                        int unused14 = PhotoViewer.this.compressionsCount = 0;
                    }
                    PhotoViewer.this.updateVideoInfo();
                    PhotoViewer.this.updateMuteButton();
                }
            }
        };
        this.currentLoadingVideoRunnable = r6;
        dispatchQueue.postRunnable(r6);
    }

    /* access modifiers changed from: private */
    public int selectCompression() {
        return Math.round(((float) DownloadController.getInstance(this.currentAccount).getMaxVideoBitrate()) / (100.0f / ((float) this.compressionsCount))) - 1;
    }

    /* access modifiers changed from: private */
    public void updateCompressionsCount(int i, int i2) {
        int max = Math.max(i, i2);
        if (max > 1280) {
            this.compressionsCount = 4;
        } else if (max > 854) {
            this.compressionsCount = 3;
        } else if (max > 640) {
            this.compressionsCount = 2;
        } else {
            this.compressionsCount = 1;
        }
    }

    /* access modifiers changed from: private */
    public void setCompressItemEnabled(boolean z, boolean z2) {
        ImageView imageView = this.compressItem;
        if (imageView != null) {
            if (z && imageView.getTag() != null) {
                return;
            }
            if (z || this.compressItem.getTag() != null) {
                this.compressItem.setTag(z ? 1 : null);
                AnimatorSet animatorSet = this.compressItemAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.compressItemAnimation = null;
                }
                float f = 1.0f;
                if (z2) {
                    this.compressItemAnimation = new AnimatorSet();
                    AnimatorSet animatorSet2 = this.compressItemAnimation;
                    Animator[] animatorArr = new Animator[1];
                    ImageView imageView2 = this.compressItem;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    if (!z) {
                        f = 0.5f;
                    }
                    fArr[0] = f;
                    animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                    animatorSet2.playTogether(animatorArr);
                    this.compressItemAnimation.setDuration(180);
                    this.compressItemAnimation.setInterpolator(decelerateInterpolator);
                    this.compressItemAnimation.start();
                    return;
                }
                ImageView imageView3 = this.compressItem;
                if (!z) {
                    f = 0.5f;
                }
                imageView3.setAlpha(f);
            }
        }
    }

    private void updateAccessibilityOverlayVisibility() {
        VideoPlayer videoPlayer2;
        if (this.playButtonAccessibilityOverlay != null) {
            if (!this.isCurrentVideo || ((videoPlayer2 = this.videoPlayer) != null && videoPlayer2.isPlaying())) {
                this.playButtonAccessibilityOverlay.setVisibility(4);
            } else {
                this.playButtonAccessibilityOverlay.setVisibility(0);
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            if (PhotoViewer.this.placeProvider == null || PhotoViewer.this.placeProvider.getSelectedPhotosOrder() == null) {
                return 0;
            }
            return PhotoViewer.this.placeProvider.getSelectedPhotosOrder().size();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            PhotoPickerPhotoCell photoPickerPhotoCell = new PhotoPickerPhotoCell(this.mContext, false);
            photoPickerPhotoCell.checkFrame.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.ListAdapter.this.lambda$onCreateViewHolder$0$PhotoViewer$ListAdapter(view);
                }
            });
            return new RecyclerListView.Holder(photoPickerPhotoCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$PhotoViewer$ListAdapter(View view) {
            Object tag = ((View) view.getParent()).getTag();
            int indexOf = PhotoViewer.this.imagesArrLocals.indexOf(tag);
            if (indexOf >= 0) {
                int photoChecked = PhotoViewer.this.placeProvider.setPhotoChecked(indexOf, PhotoViewer.this.getCurrentVideoEditedInfo());
                PhotoViewer.this.placeProvider.isPhotoChecked(indexOf);
                if (indexOf == PhotoViewer.this.currentIndex) {
                    PhotoViewer.this.checkImageView.setChecked(-1, false, true);
                }
                if (photoChecked >= 0) {
                    PhotoViewer.this.selectedPhotosAdapter.notifyItemRemoved(photoChecked);
                }
                PhotoViewer.this.updateSelectedCount();
                return;
            }
            int photoUnchecked = PhotoViewer.this.placeProvider.setPhotoUnchecked(tag);
            if (photoUnchecked >= 0) {
                PhotoViewer.this.selectedPhotosAdapter.notifyItemRemoved(photoUnchecked);
                PhotoViewer.this.updateSelectedCount();
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            PhotoPickerPhotoCell photoPickerPhotoCell = (PhotoPickerPhotoCell) viewHolder.itemView;
            photoPickerPhotoCell.itemWidth = AndroidUtilities.dp(82.0f);
            BackupImageView backupImageView = photoPickerPhotoCell.imageView;
            backupImageView.setOrientation(0, true);
            Object obj = PhotoViewer.this.placeProvider.getSelectedPhotos().get(PhotoViewer.this.placeProvider.getSelectedPhotosOrder().get(i));
            if (obj instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) obj;
                photoPickerPhotoCell.setTag(photoEntry);
                photoPickerPhotoCell.videoInfoContainer.setVisibility(4);
                String str = photoEntry.thumbPath;
                if (str != null) {
                    backupImageView.setImage(str, (String) null, this.mContext.getResources().getDrawable(NUM));
                } else if (photoEntry.path != null) {
                    backupImageView.setOrientation(photoEntry.orientation, true);
                    if (photoEntry.isVideo) {
                        photoPickerPhotoCell.videoInfoContainer.setVisibility(0);
                        photoPickerPhotoCell.videoTextView.setText(AndroidUtilities.formatShortDuration(photoEntry.duration));
                        backupImageView.setImage("vthumb://" + photoEntry.imageId + ":" + photoEntry.path, (String) null, this.mContext.getResources().getDrawable(NUM));
                    } else {
                        backupImageView.setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, (String) null, this.mContext.getResources().getDrawable(NUM));
                    }
                } else {
                    backupImageView.setImageResource(NUM);
                }
                photoPickerPhotoCell.setChecked(-1, true, false);
                photoPickerPhotoCell.checkBox.setVisibility(0);
            } else if (obj instanceof MediaController.SearchImage) {
                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                photoPickerPhotoCell.setTag(searchImage);
                photoPickerPhotoCell.setImage(searchImage);
                photoPickerPhotoCell.videoInfoContainer.setVisibility(4);
                photoPickerPhotoCell.setChecked(-1, true, false);
                photoPickerPhotoCell.checkBox.setVisibility(0);
            }
        }
    }
}
