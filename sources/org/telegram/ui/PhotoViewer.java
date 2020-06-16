package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.util.FloatProperty;
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
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import androidx.annotation.Keep;
import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerEnd;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
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
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$PageBlock;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_page;
import org.telegram.tgnet.TLRPC$TL_pageBlockAuthorDate;
import org.telegram.tgnet.TLRPC$TL_photoEmpty;
import org.telegram.tgnet.TLRPC$TL_webDocument;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebDocument;
import org.telegram.tgnet.TLRPC$WebPage;
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
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FadingTextViewLayout;
import org.telegram.ui.Components.FilterGLThread;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.GestureDetector2;
import org.telegram.ui.Components.GroupedPhotosListView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.PaintingOverlay;
import org.telegram.ui.Components.PhotoCropView;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.PhotoPaintView;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView;
import org.telegram.ui.Components.PickerBottomLayoutViewer;
import org.telegram.ui.Components.PipVideoView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TextViewSwitcher;
import org.telegram.ui.Components.Tooltip;
import org.telegram.ui.Components.URLSpanUserMentionPhotoViewer;
import org.telegram.ui.Components.VideoEditTextureView;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayerSeekBar;
import org.telegram.ui.Components.VideoSeekPreviewImage;
import org.telegram.ui.Components.VideoTimelinePlayView;
import org.telegram.ui.Components.ViewHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.PhotoViewer;

public class PhotoViewer implements NotificationCenter.NotificationCenterDelegate, GestureDetector2.OnGestureListener, GestureDetector2.OnDoubleTapListener {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile PhotoViewer Instance = null;
    private static volatile PhotoViewer PipInstance = null;
    private static final Property<VideoPlayerControlFrameLayout, Float> VPC_PROGRESS;
    /* access modifiers changed from: private */
    public static DecelerateInterpolator decelerateInterpolator;
    /* access modifiers changed from: private */
    public static Drawable[] progressDrawables;
    /* access modifiers changed from: private */
    public static Paint progressPaint;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimator;
    private Map<View, Boolean> actionBarItemsVisibility = new HashMap(3);
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
    public ArrayList<TLRPC$Photo> avatarsArr = new ArrayList<>();
    /* access modifiers changed from: private */
    public int avatarsDialogId;
    /* access modifiers changed from: private */
    public BackgroundDrawable backgroundDrawable = new BackgroundDrawable(-16777216);
    private Paint bitmapPaint = new Paint(2);
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
    public FrameLayout captionContainer;
    /* access modifiers changed from: private */
    public PhotoViewerCaptionEnterView captionEditText;
    private boolean captionHwLayerEnabled;
    /* access modifiers changed from: private */
    public CaptionScrollView captionScrollView;
    /* access modifiers changed from: private */
    public CaptionTextViewSwitcher captionTextViewSwitcher;
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
    private TLRPC$BotInlineResult currentBotInlineResult;
    /* access modifiers changed from: private */
    public long currentDialogId;
    /* access modifiers changed from: private */
    public int currentEditMode;
    /* access modifiers changed from: private */
    public ImageLocation currentFileLocation;
    /* access modifiers changed from: private */
    public String[] currentFileNames = new String[3];
    private String currentImagePath;
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
    /* access modifiers changed from: private */
    public FadingTextViewLayout dateTextView;
    /* access modifiers changed from: private */
    public boolean disableShowCheck;
    private boolean discardTap;
    private TextView docInfoTextView;
    private TextView docNameTextView;
    private boolean doneButtonPressed;
    /* access modifiers changed from: private */
    public boolean dontChangeCaptionPosition;
    /* access modifiers changed from: private */
    public boolean dontResetZoomOnFirstLayout;
    private boolean doubleTap;
    private boolean doubleTapEnabled;
    private float dragY;
    private boolean draggingDown;
    private boolean[] drawPressedDrawable = new boolean[2];
    /* access modifiers changed from: private */
    public EditState editState = new EditState();
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
    public Runnable hideActionBarRunnable = new Runnable() {
        public void run() {
            if (PhotoViewer.this.videoPlayerControlVisible && PhotoViewer.this.isPlaying && !ApplicationLoader.mainInterfacePaused) {
                if (PhotoViewer.this.menuItem != null && PhotoViewer.this.menuItem.isSubMenuShowing()) {
                    return;
                }
                if (PhotoViewer.this.captionScrollView != null && PhotoViewer.this.captionScrollView.getScrollY() != 0) {
                    return;
                }
                if (PhotoViewer.this.miniProgressView == null || PhotoViewer.this.miniProgressView.getVisibility() != 0) {
                    PhotoViewer.this.toggleActionBar(false, true);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public PlaceProviderObject hideAfterAnimation;
    private Rect hitRect = new Rect();
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
    /* access modifiers changed from: private */
    public boolean isActionBarVisible = true;
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
    /* access modifiers changed from: private */
    public boolean isStreaming;
    /* access modifiers changed from: private */
    public boolean isVisible;
    /* access modifiers changed from: private */
    public LinearLayout itemsLayout;
    private boolean keepScreenOnFlagSet;
    /* access modifiers changed from: private */
    public int keyboardSize;
    /* access modifiers changed from: private */
    public long lastBufferedPositionCheck;
    /* access modifiers changed from: private */
    public Object lastInsets;
    private long lastPhotoSetTime;
    /* access modifiers changed from: private */
    public long lastSaveTime;
    private String lastTitle;
    private ImageReceiver leftImage = new ImageReceiver();
    private boolean loadInitialVideo;
    private boolean loadingMoreImages;
    /* access modifiers changed from: private */
    public StickersAlert masksAlert;
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
    /* access modifiers changed from: private */
    public FadingTextViewLayout nameTextView;
    /* access modifiers changed from: private */
    public boolean needCaptionLayout;
    private boolean needSearchImageInArr;
    /* access modifiers changed from: private */
    public boolean needShowOnReady;
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
    private int paintViewTouched;
    /* access modifiers changed from: private */
    public PaintingOverlay paintingOverlay;
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
    /* access modifiers changed from: private */
    public PhotoProgressView[] photoProgressViews = new PhotoProgressView[3];
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
    private boolean playerAutoStarted;
    private boolean playerInjected;
    private boolean playerLooping;
    private boolean playerWasPlaying;
    /* access modifiers changed from: private */
    public boolean playerWasReady;
    private GradientDrawable[] pressedDrawable = new GradientDrawable[2];
    private float[] pressedDrawableAlpha = new float[2];
    private int previousCompression;
    private int previousCropOrientation;
    private float previousCropPh;
    private float previousCropPw;
    private float previousCropPx;
    private float previousCropPy;
    private float previousCropRotation;
    private float previousCropScale;
    private boolean previousHasTransform;
    /* access modifiers changed from: private */
    public RadialProgressView progressView;
    /* access modifiers changed from: private */
    public QualityChooseView qualityChooseView;
    /* access modifiers changed from: private */
    public AnimatorSet qualityChooseViewAnimation;
    /* access modifiers changed from: private */
    public PickerBottomLayoutViewer qualityPicker;
    private boolean requestingPreview;
    /* access modifiers changed from: private */
    public TextView resetButton;
    /* access modifiers changed from: private */
    public int resultHeight;
    /* access modifiers changed from: private */
    public int resultWidth;
    private ImageReceiver rightImage = new ImageReceiver();
    private ImageView rotateItem;
    /* access modifiers changed from: private */
    public int rotationValue;
    private ArrayMap<String, SavedVideoPosition> savedVideoPositions = new ArrayMap<>();
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
    public SelectedPhotosListView selectedPhotosListView;
    private ActionBarMenuItem sendItem;
    /* access modifiers changed from: private */
    public int sendPhotoType;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private Runnable setLoadingRunnable = new Runnable() {
        public void run() {
            if (PhotoViewer.this.currentMessageObject != null) {
                FileLoader.getInstance(PhotoViewer.this.currentMessageObject.currentAccount).setLoadingVideo(PhotoViewer.this.currentMessageObject.getDocument(), true, false);
            }
        }
    };
    /* access modifiers changed from: private */
    public ImageView shareButton;
    /* access modifiers changed from: private */
    public int sharedMediaType;
    /* access modifiers changed from: private */
    public String shouldSavePositionForCurrentVideo;
    private String shouldSavePositionForCurrentVideoShortTerm;
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
                              (wrap: org.telegram.ui.-$$Lambda$PhotoViewer$5$1$wS7CEJAZQr9T3UUR7g2VjfDLYxc : 0x004f: CONSTRUCTOR  (r0v7 org.telegram.ui.-$$Lambda$PhotoViewer$5$1$wS7CEJAZQr9T3UUR7g2VjfDLYxc) = (r2v0 'this' org.telegram.ui.PhotoViewer$5$1 A[THIS]) call: org.telegram.ui.-$$Lambda$PhotoViewer$5$1$wS7CEJAZQr9T3UUR7g2VjfDLYxc.<init>(org.telegram.ui.PhotoViewer$5$1):void type: CONSTRUCTOR)
                             org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.PhotoViewer.5.1.onPreDraw():boolean, dex: classes.dex
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
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x004f: CONSTRUCTOR  (r0v7 org.telegram.ui.-$$Lambda$PhotoViewer$5$1$wS7CEJAZQr9T3UUR7g2VjfDLYxc) = (r2v0 'this' org.telegram.ui.PhotoViewer$5$1 A[THIS]) call: org.telegram.ui.-$$Lambda$PhotoViewer$5$1$wS7CEJAZQr9T3UUR7g2VjfDLYxc.<init>(org.telegram.ui.PhotoViewer$5$1):void type: CONSTRUCTOR in method: org.telegram.ui.PhotoViewer.5.1.onPreDraw():boolean, dex: classes.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 81 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.-$$Lambda$PhotoViewer$5$1$wS7CEJAZQr9T3UUR7g2VjfDLYxc, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 87 more
                            */
                        /*
                            this = this;
                            org.telegram.ui.PhotoViewer$5 r0 = org.telegram.ui.PhotoViewer.AnonymousClass5.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.view.TextureView r0 = r0.changedTextureView
                            android.view.ViewTreeObserver r0 = r0.getViewTreeObserver()
                            r0.removeOnPreDrawListener(r2)
                            org.telegram.ui.PhotoViewer$5 r0 = org.telegram.ui.PhotoViewer.AnonymousClass5.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.widget.ImageView r0 = r0.textureImageView
                            if (r0 == 0) goto L_0x004d
                            org.telegram.ui.PhotoViewer$5 r0 = org.telegram.ui.PhotoViewer.AnonymousClass5.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.widget.ImageView r0 = r0.textureImageView
                            r1 = 4
                            r0.setVisibility(r1)
                            org.telegram.ui.PhotoViewer$5 r0 = org.telegram.ui.PhotoViewer.AnonymousClass5.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.widget.ImageView r0 = r0.textureImageView
                            r1 = 0
                            r0.setImageDrawable(r1)
                            org.telegram.ui.PhotoViewer$5 r0 = org.telegram.ui.PhotoViewer.AnonymousClass5.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.graphics.Bitmap r0 = r0.currentBitmap
                            if (r0 == 0) goto L_0x004d
                            org.telegram.ui.PhotoViewer$5 r0 = org.telegram.ui.PhotoViewer.AnonymousClass5.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.graphics.Bitmap r0 = r0.currentBitmap
                            r0.recycle()
                            org.telegram.ui.PhotoViewer$5 r0 = org.telegram.ui.PhotoViewer.AnonymousClass5.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            android.graphics.Bitmap unused = r0.currentBitmap = r1
                        L_0x004d:
                            org.telegram.ui.-$$Lambda$PhotoViewer$5$1$wS7CEJAZQr9T3UUR7g2VjfDLYxc r0 = new org.telegram.ui.-$$Lambda$PhotoViewer$5$1$wS7CEJAZQr9T3UUR7g2VjfDLYxc
                            r0.<init>(r2)
                            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                            org.telegram.ui.PhotoViewer$5 r0 = org.telegram.ui.PhotoViewer.AnonymousClass5.this
                            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                            r1 = 0
                            int unused = r0.waitingForFirstTextureUpload = r1
                            r0 = 1
                            return r0
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.AnonymousClass5.AnonymousClass1.onPreDraw():boolean");
                    }

                    public /* synthetic */ void lambda$onPreDraw$0$PhotoViewer$5$1() {
                        if (PhotoViewer.this.isInline) {
                            PhotoViewer.this.dismissInternal();
                        }
                    }
                });
                PhotoViewer.this.changedTextureView.invalidate();
            }
        }
    };
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
            PipVideoView access$2500 = photoViewer.pipVideoView;
            Activity access$900 = PhotoViewer.this.parentActivity;
            PhotoViewer photoViewer2 = PhotoViewer.this;
            TextureView unused8 = photoViewer.changedTextureView = access$2500.show(access$900, photoViewer2, photoViewer2.aspectRatioFrameLayout.getAspectRatio(), PhotoViewer.this.aspectRatioFrameLayout.getVideoRotation());
            PhotoViewer.this.changedTextureView.setVisibility(4);
            PhotoViewer.this.aspectRatioFrameLayout.removeView(PhotoViewer.this.videoTextureView);
        }
    };
    /* access modifiers changed from: private */
    public boolean switchingInlineMode;
    private int switchingToIndex;
    /* access modifiers changed from: private */
    public int switchingToMode;
    /* access modifiers changed from: private */
    public ImageView textureImageView;
    /* access modifiers changed from: private */
    public boolean textureUploaded;
    private ImageView timeItem;
    private Tooltip tooltip;
    private int totalImagesCount;
    private int totalImagesCountMerge;
    private int touchSlop;
    /* access modifiers changed from: private */
    public long transitionAnimationStartTime;
    /* access modifiers changed from: private */
    public int transitionIndex;
    /* access modifiers changed from: private */
    public float translationX;
    /* access modifiers changed from: private */
    public float translationY;
    private boolean tryStartRequestPreviewOnFinish;
    private ImageView tuneItem;
    private final Runnable updateContainerFlagsRunnable = new Runnable() {
        public final void run() {
            PhotoViewer.this.lambda$new$1$PhotoViewer();
        }
    };
    /* access modifiers changed from: private */
    public Runnable updateProgressRunnable = new Runnable() {
        public void run() {
            float f;
            if (PhotoViewer.this.videoPlayer != null) {
                float f2 = 1.0f;
                float f3 = 0.0f;
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
                        float rightProgress = leftProgress / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
                        if (rightProgress <= 1.0f) {
                            f2 = rightProgress;
                        }
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(f2);
                        currentPosition = f2;
                    }
                    PhotoViewer.this.videoPlayerSeekbarView.invalidate();
                    if (PhotoViewer.this.shouldSavePositionForCurrentVideo != null && currentPosition >= 0.0f && PhotoViewer.this.shouldSavePositionForCurrentVideo != null && SystemClock.elapsedRealtime() - PhotoViewer.this.lastSaveTime >= 1000) {
                        String unused2 = PhotoViewer.this.shouldSavePositionForCurrentVideo;
                        long unused3 = PhotoViewer.this.lastSaveTime = SystemClock.elapsedRealtime();
                        Utilities.globalQueue.postRunnable(new Runnable(currentPosition) {
                            public final /* synthetic */ float f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                PhotoViewer.AnonymousClass3.this.lambda$run$0$PhotoViewer$3(this.f$1);
                            }
                        });
                    }
                    PhotoViewer.this.updateVideoPlayerTime();
                } else if (!PhotoViewer.this.videoTimelineView.isDragging()) {
                    float currentPosition2 = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                    if (PhotoViewer.this.inPreview || (PhotoViewer.this.currentEditMode == 0 && PhotoViewer.this.videoTimelineView.getVisibility() != 0)) {
                        PhotoViewer.this.videoTimelineView.setProgress(currentPosition2);
                    } else if (currentPosition2 >= PhotoViewer.this.videoTimelineView.getRightProgress()) {
                        PhotoViewer.this.videoTimelineView.setProgress(0.0f);
                        PhotoViewer.this.videoPlayer.seekTo((long) ((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.videoPlayer.getDuration()))));
                        if (PhotoViewer.this.muteVideo || PhotoViewer.this.currentEditMode != 0 || PhotoViewer.this.switchingToMode > 0) {
                            PhotoViewer.this.videoPlayer.play();
                        } else {
                            PhotoViewer.this.videoPlayer.pause();
                        }
                        PhotoViewer.this.containerView.invalidate();
                    } else {
                        float leftProgress2 = currentPosition2 - PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (leftProgress2 >= 0.0f) {
                            f3 = leftProgress2;
                        }
                        float rightProgress2 = f3 / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
                        if (rightProgress2 <= 1.0f) {
                            f2 = rightProgress2;
                        }
                        PhotoViewer.this.videoTimelineView.setProgress(f2);
                    }
                    PhotoViewer.this.updateVideoPlayerTime();
                }
            }
            if (PhotoViewer.this.isPlaying) {
                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable, 17);
            }
        }

        public /* synthetic */ void lambda$run$0$PhotoViewer$3(float f) {
            ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).edit().putFloat(PhotoViewer.this.shouldSavePositionForCurrentVideo, f).commit();
        }
    };
    /* access modifiers changed from: private */
    public boolean useSmoothKeyboard;
    private VelocityTracker velocityTracker;
    /* access modifiers changed from: private */
    public boolean videoConvertSupported;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    private float videoCutEnd;
    private float videoCutStart;
    /* access modifiers changed from: private */
    public float videoDuration;
    private VideoForwardDrawable videoForwardDrawable;
    /* access modifiers changed from: private */
    public int videoFramerate;
    /* access modifiers changed from: private */
    public long videoFramesSize;
    /* access modifiers changed from: private */
    public VideoPlayer videoPlayer;
    private Animator videoPlayerControlAnimator;
    /* access modifiers changed from: private */
    public VideoPlayerControlFrameLayout videoPlayerControlFrameLayout;
    /* access modifiers changed from: private */
    public boolean videoPlayerControlVisible = true;
    /* access modifiers changed from: private */
    public int[] videoPlayerCurrentTime = new int[2];
    /* access modifiers changed from: private */
    public VideoPlayerSeekBar videoPlayerSeekbar;
    /* access modifiers changed from: private */
    public View videoPlayerSeekbarView;
    /* access modifiers changed from: private */
    public SimpleTextView videoPlayerTime;
    /* access modifiers changed from: private */
    public int[] videoPlayerTotalTime = new int[2];
    /* access modifiers changed from: private */
    public VideoSeekPreviewImage videoPreviewFrame;
    /* access modifiers changed from: private */
    public AnimatorSet videoPreviewFrameAnimation;
    private MessageObject videoPreviewMessageObject;
    /* access modifiers changed from: private */
    public boolean videoSizeSet;
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

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
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

        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i) {
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

        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i) {
        }
    }

    public interface PhotoViewerProvider {
        boolean allowCaption();

        boolean canCaptureMorePhotos();

        boolean canScrollAway();

        boolean cancelButtonPressed();

        void deleteImageAtIndex(int i);

        String getDeleteMessageString();

        PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z);

        int getSelectedCount();

        HashMap<Object, Object> getSelectedPhotos();

        ArrayList<Object> getSelectedPhotosOrder();

        ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i);

        boolean isPhotoChecked(int i);

        void needAddMorePhotos();

        boolean scaleToFill();

        void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2);

        int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo);

        int setPhotoUnchecked(Object obj);

        void updatePhotoAtIndex(int i);

        void willHidePhotoViewer();

        void willSwitchFromPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i);
    }

    public static class PlaceProviderObject {
        public boolean allowTakeAnimation = true;
        public ClippingImageView animatingImageView;
        public int animatingImageViewYOffset;
        public int clipBottomAddition;
        public int clipTopAddition;
        public int dialogId;
        public ImageReceiver imageReceiver;
        public boolean isEvent;
        public View parentView;
        public int[] radius;
        public float scale = 1.0f;
        public int size;
        public ImageReceiver.BitmapHolder thumb;
        public int viewX;
        public int viewY;
    }

    static /* synthetic */ boolean lambda$null$19(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ boolean lambda$null$20(View view, MotionEvent motionEvent) {
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

    private static class SavedVideoPosition {
        public final float position;
        public final long timestamp;

        public SavedVideoPosition(float f, long j) {
            this.position = f;
            this.timestamp = j;
        }
    }

    private class CaptionLinkMovementMethod extends LinkMovementMethod {
        private CaptionLinkMovementMethod() {
        }

        /* JADX WARNING: Removed duplicated region for block: B:40:0x0109 A[ORIG_RETURN, RETURN, SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:44:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.widget.TextView r9, android.text.Spannable r10, android.view.MotionEvent r11) {
            /*
                r8 = this;
                r0 = 0
                int r1 = r11.getAction()     // Catch:{ Exception -> 0x010b }
                r2 = 1
                if (r1 == r2) goto L_0x000a
                if (r1 != 0) goto L_0x0100
            L_0x000a:
                float r3 = r11.getX()     // Catch:{ Exception -> 0x010b }
                int r3 = (int) r3     // Catch:{ Exception -> 0x010b }
                float r4 = r11.getY()     // Catch:{ Exception -> 0x010b }
                int r4 = (int) r4     // Catch:{ Exception -> 0x010b }
                int r5 = r9.getTotalPaddingLeft()     // Catch:{ Exception -> 0x010b }
                int r3 = r3 - r5
                int r5 = r9.getTotalPaddingTop()     // Catch:{ Exception -> 0x010b }
                int r4 = r4 - r5
                int r5 = r9.getScrollX()     // Catch:{ Exception -> 0x010b }
                int r3 = r3 + r5
                int r5 = r9.getScrollY()     // Catch:{ Exception -> 0x010b }
                int r4 = r4 + r5
                android.text.Layout r5 = r9.getLayout()     // Catch:{ Exception -> 0x010b }
                int r4 = r5.getLineForVertical(r4)     // Catch:{ Exception -> 0x010b }
                float r3 = (float) r3     // Catch:{ Exception -> 0x010b }
                int r3 = r5.getOffsetForHorizontal(r4, r3)     // Catch:{ Exception -> 0x010b }
                java.lang.Class<android.text.style.ClickableSpan> r4 = android.text.style.ClickableSpan.class
                java.lang.Object[] r3 = r10.getSpans(r3, r3, r4)     // Catch:{ Exception -> 0x010b }
                android.text.style.ClickableSpan[] r3 = (android.text.style.ClickableSpan[]) r3     // Catch:{ Exception -> 0x010b }
                int r4 = r3.length     // Catch:{ Exception -> 0x010b }
                if (r4 == 0) goto L_0x00fd
                r3 = r3[r0]     // Catch:{ Exception -> 0x010b }
                if (r1 != r2) goto L_0x00f0
                boolean r1 = r3 instanceof android.text.style.URLSpan     // Catch:{ Exception -> 0x010b }
                if (r1 == 0) goto L_0x00ec
                r1 = r3
                android.text.style.URLSpan r1 = (android.text.style.URLSpan) r1     // Catch:{ Exception -> 0x010b }
                java.lang.String r1 = r1.getURL()     // Catch:{ Exception -> 0x010b }
                java.lang.String r4 = "video"
                boolean r4 = r1.startsWith(r4)     // Catch:{ Exception -> 0x010b }
                if (r4 == 0) goto L_0x00a4
                org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this     // Catch:{ Exception -> 0x010b }
                org.telegram.ui.Components.VideoPlayer r3 = r3.videoPlayer     // Catch:{ Exception -> 0x010b }
                if (r3 == 0) goto L_0x00fb
                org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this     // Catch:{ Exception -> 0x010b }
                org.telegram.messenger.MessageObject r3 = r3.currentMessageObject     // Catch:{ Exception -> 0x010b }
                if (r3 == 0) goto L_0x00fb
                java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)     // Catch:{ Exception -> 0x010b }
                int r1 = r1.intValue()     // Catch:{ Exception -> 0x010b }
                org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this     // Catch:{ Exception -> 0x010b }
                org.telegram.ui.Components.VideoPlayer r3 = r3.videoPlayer     // Catch:{ Exception -> 0x010b }
                long r3 = r3.getDuration()     // Catch:{ Exception -> 0x010b }
                r5 = -9223372036854775807(0xNUM, double:-4.9E-324)
                int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r7 != 0) goto L_0x0095
                org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this     // Catch:{ Exception -> 0x010b }
                float r1 = (float) r1     // Catch:{ Exception -> 0x010b }
                org.telegram.ui.PhotoViewer r4 = org.telegram.ui.PhotoViewer.this     // Catch:{ Exception -> 0x010b }
                org.telegram.messenger.MessageObject r4 = r4.currentMessageObject     // Catch:{ Exception -> 0x010b }
                int r4 = r4.getDuration()     // Catch:{ Exception -> 0x010b }
                float r4 = (float) r4     // Catch:{ Exception -> 0x010b }
                float r1 = r1 / r4
                float unused = r3.seekToProgressPending = r1     // Catch:{ Exception -> 0x010b }
                goto L_0x00fb
            L_0x0095:
                org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this     // Catch:{ Exception -> 0x010b }
                org.telegram.ui.Components.VideoPlayer r3 = r3.videoPlayer     // Catch:{ Exception -> 0x010b }
                long r4 = (long) r1     // Catch:{ Exception -> 0x010b }
                r6 = 1000(0x3e8, double:4.94E-321)
                long r4 = r4 * r6
                r3.seekTo(r4)     // Catch:{ Exception -> 0x010b }
                goto L_0x00fb
            L_0x00a4:
                java.lang.String r4 = "#"
                boolean r4 = r1.startsWith(r4)     // Catch:{ Exception -> 0x010b }
                if (r4 == 0) goto L_0x00d0
                org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this     // Catch:{ Exception -> 0x010b }
                android.app.Activity r3 = r3.parentActivity     // Catch:{ Exception -> 0x010b }
                boolean r3 = r3 instanceof org.telegram.ui.LaunchActivity     // Catch:{ Exception -> 0x010b }
                if (r3 == 0) goto L_0x00fb
                org.telegram.ui.DialogsActivity r3 = new org.telegram.ui.DialogsActivity     // Catch:{ Exception -> 0x010b }
                r4 = 0
                r3.<init>(r4)     // Catch:{ Exception -> 0x010b }
                r3.setSearchString(r1)     // Catch:{ Exception -> 0x010b }
                org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this     // Catch:{ Exception -> 0x010b }
                android.app.Activity r1 = r1.parentActivity     // Catch:{ Exception -> 0x010b }
                org.telegram.ui.LaunchActivity r1 = (org.telegram.ui.LaunchActivity) r1     // Catch:{ Exception -> 0x010b }
                r1.presentFragment(r3, r0, r2)     // Catch:{ Exception -> 0x010b }
                org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this     // Catch:{ Exception -> 0x010b }
                r1.closePhoto(r0, r0)     // Catch:{ Exception -> 0x010b }
                goto L_0x00fb
            L_0x00d0:
                org.telegram.ui.PhotoViewer r4 = org.telegram.ui.PhotoViewer.this     // Catch:{ Exception -> 0x010b }
                org.telegram.ui.ChatActivity r4 = r4.parentChatActivity     // Catch:{ Exception -> 0x010b }
                if (r4 == 0) goto L_0x00e8
                boolean r4 = org.telegram.messenger.AndroidUtilities.shouldShowUrlInAlert(r1)     // Catch:{ Exception -> 0x010b }
                if (r4 == 0) goto L_0x00e8
                org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this     // Catch:{ Exception -> 0x010b }
                org.telegram.ui.ChatActivity r3 = r3.parentChatActivity     // Catch:{ Exception -> 0x010b }
                org.telegram.ui.Components.AlertsCreator.showOpenUrlAlert(r3, r1, r2, r2)     // Catch:{ Exception -> 0x010b }
                goto L_0x00fb
            L_0x00e8:
                r3.onClick(r9)     // Catch:{ Exception -> 0x010b }
                goto L_0x00fb
            L_0x00ec:
                r3.onClick(r9)     // Catch:{ Exception -> 0x010b }
                goto L_0x00fb
            L_0x00f0:
                int r1 = r10.getSpanStart(r3)     // Catch:{ Exception -> 0x010b }
                int r3 = r10.getSpanEnd(r3)     // Catch:{ Exception -> 0x010b }
                android.text.Selection.setSelection(r10, r1, r3)     // Catch:{ Exception -> 0x010b }
            L_0x00fb:
                r1 = 1
                goto L_0x0101
            L_0x00fd:
                android.text.Selection.removeSelection(r10)     // Catch:{ Exception -> 0x010b }
            L_0x0100:
                r1 = 0
            L_0x0101:
                if (r1 != 0) goto L_0x0109
                boolean r9 = android.text.method.Touch.onTouchEvent(r9, r10, r11)     // Catch:{ Exception -> 0x010b }
                if (r9 == 0) goto L_0x010a
            L_0x0109:
                r0 = 1
            L_0x010a:
                return r0
            L_0x010b:
                r9 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.CaptionLinkMovementMethod.onTouchEvent(android.widget.TextView, android.text.Spannable, android.view.MotionEvent):boolean");
        }
    }

    public /* synthetic */ void lambda$new$1$PhotoViewer() {
        if (this.isVisible && this.animationInProgress == 0) {
            updateContainerFlags(this.isActionBarVisible);
        }
    }

    private static class EditState {
        public long averageDuration;
        public MediaController.CropState cropState;
        public ArrayList<VideoEditedInfo.MediaEntity> croppedMediaEntities;
        public String croppedPaintPath;
        public ArrayList<VideoEditedInfo.MediaEntity> mediaEntities;
        public String paintPath;
        public MediaController.SavedFilterState savedFilterState;

        private EditState() {
        }

        public void reset() {
            this.paintPath = null;
            this.cropState = null;
            this.savedFilterState = null;
            this.mediaEntities = null;
            this.croppedPaintPath = null;
            this.croppedMediaEntities = null;
            this.averageDuration = 0;
        }
    }

    private class BackgroundDrawable extends ColorDrawable {
        private boolean allowDrawContent;
        /* access modifiers changed from: private */
        public Runnable drawRunnable;
        private final Paint paint;
        private final RectF rect = new RectF();
        private final RectF visibleRect = new RectF();

        public BackgroundDrawable(int i) {
            super(i);
            Paint paint2 = new Paint(1);
            this.paint = paint2;
            paint2.setColor(i);
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

    private static class SelectedPhotosListView extends RecyclerListView {
        private Drawable arrowDrawable;
        private Paint paint = new Paint(1);
        private RectF rect = new RectF();

        public SelectedPhotosListView(Context context) {
            super(context);
            setWillNotDraw(false);
            setClipToPadding(false);
            setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
            AnonymousClass1 r1 = new DefaultItemAnimator() {
                /* access modifiers changed from: protected */
                public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                    SelectedPhotosListView.this.invalidate();
                }
            };
            setItemAnimator(r1);
            r1.setDelayAnimations(false);
            r1.setSupportsChangeAnimations(false);
            setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(6.0f));
            this.paint.setColor(NUM);
            this.arrowDrawable = context.getResources().getDrawable(NUM).mutate();
        }

        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int childCount = getChildCount();
            if (childCount > 0) {
                int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(87.0f);
                Drawable drawable = this.arrowDrawable;
                drawable.setBounds(measuredWidth, 0, drawable.getIntrinsicWidth() + measuredWidth, AndroidUtilities.dp(6.0f));
                this.arrowDrawable.draw(canvas);
                int i = Integer.MAX_VALUE;
                int i2 = Integer.MIN_VALUE;
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = getChildAt(i3);
                    i = (int) Math.min((double) i, Math.floor((double) childAt.getX()));
                    i2 = (int) Math.max((double) i2, Math.ceil((double) (childAt.getX() + ((float) childAt.getMeasuredWidth()))));
                }
                if (i != Integer.MAX_VALUE && i2 != Integer.MIN_VALUE) {
                    this.rect.set((float) (i - AndroidUtilities.dp(6.0f)), (float) AndroidUtilities.dp(6.0f), (float) (i2 + AndroidUtilities.dp(6.0f)), (float) AndroidUtilities.dp(103.0f));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), this.paint);
                }
            }
        }
    }

    private static class CounterView extends View {
        private int currentCount = 0;
        private int height;
        private Paint paint;
        private RectF rect;
        private float rotation;
        private StaticLayout staticLayout;
        private TextPaint textPaint;
        private int width;

        public CounterView(Context context) {
            super(context);
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(15.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textPaint.setColor(-1);
            Paint paint2 = new Paint(1);
            this.paint = paint2;
            paint2.setColor(-1);
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
            StaticLayout staticLayout2 = new StaticLayout("" + Math.max(1, i), this.textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.staticLayout = staticLayout2;
            this.width = (int) Math.ceil((double) staticLayout2.getLineWidth(0));
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
                int centerY = (int) (((float) ((int) this.rect.centerY())) - (((float) AndroidUtilities.dp(5.0f)) * (1.0f - this.rotation)));
                Canvas canvas2 = canvas;
                canvas2.drawLine((float) (AndroidUtilities.dp(5.0f) + centerX), (float) (centerY - AndroidUtilities.dp(5.0f)), (float) (centerX - AndroidUtilities.dp(5.0f)), (float) (AndroidUtilities.dp(5.0f) + centerY), this.paint);
                canvas2.drawLine((float) (centerX - AndroidUtilities.dp(5.0f)), (float) (centerY - AndroidUtilities.dp(5.0f)), (float) (centerX + AndroidUtilities.dp(5.0f)), (float) (centerY + AndroidUtilities.dp(5.0f)), this.paint);
            }
        }
    }

    private class PhotoProgressView {
        private float[] alphas = new float[3];
        /* access modifiers changed from: private */
        public float[] animAlphas = new float[3];
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
        /* access modifiers changed from: private */
        public int size = AndroidUtilities.dp(64.0f);
        private boolean visible;

        /* access modifiers changed from: protected */
        public void onBackgroundStateUpdated(int i) {
            throw null;
        }

        /* access modifiers changed from: protected */
        public void onVisibilityChanged(boolean z) {
            throw null;
        }

        public PhotoProgressView(View view) {
            if (PhotoViewer.decelerateInterpolator == null) {
                DecelerateInterpolator unused = PhotoViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                Paint unused2 = PhotoViewer.progressPaint = new Paint(1);
                PhotoViewer.progressPaint.setStyle(Paint.Style.STROKE);
                PhotoViewer.progressPaint.setStrokeCap(Paint.Cap.ROUND);
                PhotoViewer.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
                PhotoViewer.progressPaint.setColor(-1);
            }
            this.parent = view;
            resetAlphas();
        }

        private void updateAnimation(boolean z) {
            boolean z2;
            long currentTimeMillis = System.currentTimeMillis();
            long j = currentTimeMillis - this.lastUpdateTime;
            if (j > 18) {
                j = 18;
            }
            this.lastUpdateTime = currentTimeMillis;
            int i = 0;
            if (z) {
                if (this.animatedProgressValue == 1.0f && this.currentProgress == 1.0f) {
                    z2 = false;
                } else {
                    this.radOffset += ((float) (360 * j)) / 3000.0f;
                    float f = this.currentProgress - this.animationProgressStart;
                    if (Math.abs(f) > 0.0f) {
                        long j2 = this.currentProgressTime + j;
                        this.currentProgressTime = j2;
                        if (j2 >= 300) {
                            float f2 = this.currentProgress;
                            this.animatedProgressValue = f2;
                            this.animationProgressStart = f2;
                            this.currentProgressTime = 0;
                        } else {
                            this.animatedProgressValue = this.animationProgressStart + (f * PhotoViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f));
                        }
                    }
                    z2 = true;
                }
                float f3 = this.animatedAlphaValue;
                if (f3 > 0.0f && this.previousBackgroundState != -2) {
                    float f4 = f3 - (((float) j) / 200.0f);
                    this.animatedAlphaValue = f4;
                    if (f4 <= 0.0f) {
                        this.animatedAlphaValue = 0.0f;
                        this.previousBackgroundState = -2;
                    }
                    z2 = true;
                }
            } else {
                z2 = false;
            }
            while (true) {
                float[] fArr = this.alphas;
                if (i >= fArr.length) {
                    break;
                }
                float f5 = fArr[i];
                float[] fArr2 = this.animAlphas;
                if (f5 > fArr2[i]) {
                    fArr2[i] = Math.min(1.0f, fArr2[i] + (((float) j) / 200.0f));
                } else if (fArr[i] < fArr2[i]) {
                    fArr2[i] = Math.max(0.0f, fArr2[i] - (((float) j) / 200.0f));
                } else {
                    i++;
                }
                z2 = true;
                i++;
            }
            if (z2) {
                this.parent.postInvalidateOnAnimation();
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
                onBackgroundStateUpdated(i);
                this.parent.invalidate();
            }
        }

        public void setAlpha(float f) {
            float[] fArr = this.alphas;
            this.animAlphas[0] = f;
            fArr[0] = f;
            checkVisibility();
        }

        public void setScale(float f) {
            this.scale = f;
        }

        public void setIndexedAlpha(int i, float f, boolean z) {
            float[] fArr = this.alphas;
            if (fArr[i] != f) {
                fArr[i] = f;
                if (!z) {
                    this.animAlphas[i] = f;
                }
                checkVisibility();
                this.parent.invalidate();
            }
        }

        public void resetAlphas() {
            int i = 0;
            while (true) {
                float[] fArr = this.alphas;
                if (i < fArr.length) {
                    this.animAlphas[i] = 1.0f;
                    fArr[i] = 1.0f;
                    i++;
                } else {
                    checkVisibility();
                    return;
                }
            }
        }

        private float calculateAlpha() {
            float f;
            float f2 = 1.0f;
            int i = 0;
            while (true) {
                float[] fArr = this.animAlphas;
                if (i >= fArr.length) {
                    return f2;
                }
                if (i == 2) {
                    f = AndroidUtilities.accelerateInterpolator.getInterpolation(fArr[i]);
                } else {
                    f = fArr[i];
                }
                f2 *= f;
                i++;
            }
        }

        private void checkVisibility() {
            boolean z = false;
            int i = 0;
            while (true) {
                float[] fArr = this.alphas;
                if (i >= fArr.length) {
                    z = true;
                    break;
                } else if (fArr[i] != 1.0f) {
                    break;
                } else {
                    i++;
                }
            }
            if (z != this.visible) {
                this.visible = z;
                onVisibilityChanged(z);
            }
        }

        public boolean isVisible() {
            return this.visible;
        }

        public int getX() {
            return (PhotoViewer.this.getContainerViewWidth() - ((int) (((float) this.size) * this.scale))) / 2;
        }

        public int getY() {
            return (PhotoViewer.this.getContainerViewHeight() - ((int) (((float) this.size) * this.scale))) / 2;
        }

        public void onDraw(Canvas canvas) {
            int i;
            Drawable drawable;
            Drawable drawable2;
            int i2 = (int) (((float) this.size) * this.scale);
            int x = getX();
            int y = getY();
            float calculateAlpha = calculateAlpha();
            int i3 = this.previousBackgroundState;
            if (i3 >= 0 && i3 < PhotoViewer.progressDrawables.length && (drawable2 = PhotoViewer.progressDrawables[this.previousBackgroundState]) != null) {
                drawable2.setAlpha((int) (this.animatedAlphaValue * 255.0f * calculateAlpha));
                drawable2.setBounds(x, y, x + i2, y + i2);
                drawable2.draw(canvas);
            }
            int i4 = this.backgroundState;
            if (i4 >= 0 && i4 < PhotoViewer.progressDrawables.length && (drawable = PhotoViewer.progressDrawables[this.backgroundState]) != null) {
                if (this.previousBackgroundState != -2) {
                    drawable.setAlpha((int) ((1.0f - this.animatedAlphaValue) * 255.0f * calculateAlpha));
                } else {
                    drawable.setAlpha((int) (calculateAlpha * 255.0f));
                }
                drawable.setBounds(x, y, x + i2, y + i2);
                drawable.draw(canvas);
            }
            int i5 = this.backgroundState;
            if (i5 == 0 || i5 == 1 || (i = this.previousBackgroundState) == 0 || i == 1) {
                int dp = AndroidUtilities.dp(4.0f);
                if (this.previousBackgroundState != -2) {
                    PhotoViewer.progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f * calculateAlpha));
                } else {
                    PhotoViewer.progressPaint.setAlpha((int) (calculateAlpha * 255.0f));
                }
                this.progressRect.set((float) (x + dp), (float) (y + dp), (float) ((x + i2) - dp), (float) ((y + i2) - dp));
                canvas.drawArc(this.progressRect, this.radOffset - 0.049804688f, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, PhotoViewer.progressPaint);
                updateAnimation(true);
                return;
            }
            updateAnimation(false);
        }
    }

    private class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto {
        private boolean captionAbove;
        private boolean ignoreLayout;
        private Paint paint = new Paint();

        public FrameLayoutDrawer(Context context) {
            super(context, false);
            setWillNotDraw(false);
            this.paint.setColor(NUM);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int i3;
            int i4;
            int i5;
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            if (!PhotoViewer.this.isCurrentVideo) {
                this.ignoreLayout = true;
                if (PhotoViewer.this.needCaptionLayout) {
                    Point point = AndroidUtilities.displaySize;
                    int i6 = point.x > point.y ? 5 : 10;
                    PhotoViewer.this.captionTextViewSwitcher.getCurrentView().setMaxLines(i6);
                    PhotoViewer.this.captionTextViewSwitcher.getNextView().setMaxLines(i6);
                } else {
                    PhotoViewer.this.captionTextViewSwitcher.getCurrentView().setMaxLines(Integer.MAX_VALUE);
                    PhotoViewer.this.captionTextViewSwitcher.getNextView().setMaxLines(Integer.MAX_VALUE);
                }
                this.ignoreLayout = false;
            }
            measureChildWithMargins(PhotoViewer.this.captionEditText, i, 0, i2, 0);
            int measuredHeight = PhotoViewer.this.captionEditText.getMeasuredHeight();
            if (PhotoViewer.this.groupedPhotosListView == null || PhotoViewer.this.groupedPhotosListView.getVisibility() == 8) {
                i3 = 0;
            } else {
                measureChildWithMargins(PhotoViewer.this.groupedPhotosListView, i, 0, i2, 0);
                i3 = PhotoViewer.this.groupedPhotosListView.getMeasuredHeight();
            }
            int paddingRight = size - (getPaddingRight() + getPaddingLeft());
            int paddingBottom = size2 - getPaddingBottom();
            int childCount = getChildCount();
            for (int i7 = 0; i7 < childCount; i7++) {
                View childAt = getChildAt(i7);
                if (!(childAt.getVisibility() == 8 || childAt == PhotoViewer.this.captionEditText || childAt == PhotoViewer.this.groupedPhotosListView)) {
                    if (childAt == PhotoViewer.this.aspectRatioFrameLayout) {
                        childAt.measure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0), NUM));
                    } else {
                        int i8 = i;
                        if (childAt == PhotoViewer.this.paintingOverlay) {
                            if (PhotoViewer.this.aspectRatioFrameLayout == null || PhotoViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                                i5 = PhotoViewer.this.centerImage.getBitmapWidth();
                                i4 = PhotoViewer.this.centerImage.getBitmapHeight();
                            } else {
                                i5 = PhotoViewer.this.videoTextureView.getMeasuredWidth();
                                i4 = PhotoViewer.this.videoTextureView.getMeasuredHeight();
                            }
                            if (i5 == 0 || i4 == 0) {
                                i5 = paddingRight;
                                i4 = paddingBottom;
                            }
                            PhotoViewer.this.paintingOverlay.measure(View.MeasureSpec.makeMeasureSpec(i5, NUM), View.MeasureSpec.makeMeasureSpec(i4, NUM));
                        } else if (PhotoViewer.this.captionEditText.isPopupView(childAt)) {
                            if (!AndroidUtilities.isInMultiwindow) {
                                childAt.measure(View.MeasureSpec.makeMeasureSpec(paddingRight, NUM), View.MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                            } else if (AndroidUtilities.isTablet()) {
                                childAt.measure(View.MeasureSpec.makeMeasureSpec(paddingRight, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), (paddingBottom - measuredHeight) - AndroidUtilities.statusBarHeight), NUM));
                            } else {
                                childAt.measure(View.MeasureSpec.makeMeasureSpec(paddingRight, NUM), View.MeasureSpec.makeMeasureSpec((paddingBottom - measuredHeight) - AndroidUtilities.statusBarHeight, NUM));
                            }
                        } else if (childAt == PhotoViewer.this.captionScrollView) {
                            int dp = AndroidUtilities.dp(48.0f);
                            if (PhotoViewer.this.dontChangeCaptionPosition) {
                                if (this.captionAbove) {
                                    dp += i3;
                                }
                            } else if (PhotoViewer.this.groupedPhotosListView.hasPhotos()) {
                                dp += i3;
                                this.captionAbove = true;
                            } else {
                                this.captionAbove = false;
                            }
                            int currentActionBarHeight = (paddingBottom - ((Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight())) - dp;
                            ((ViewGroup.MarginLayoutParams) PhotoViewer.this.captionScrollView.getLayoutParams()).bottomMargin = dp;
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(paddingRight, NUM), View.MeasureSpec.makeMeasureSpec(currentActionBarHeight, NUM));
                        } else {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x009d  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x00ab  */
        /* JADX WARNING: Removed duplicated region for block: B:37:0x00bf  */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x00cc  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onLayout(boolean r16, int r17, int r18, int r19, int r20) {
            /*
                r15 = this;
                r0 = r15
                int r1 = r15.getChildCount()
                org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                boolean r3 = r2.useSmoothKeyboard
                r4 = 0
                if (r3 == 0) goto L_0x0010
                r3 = 0
                goto L_0x0014
            L_0x0010:
                int r3 = r15.getKeyboardHeight()
            L_0x0014:
                int unused = r2.keyboardSize = r3
                org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                int r2 = r2.keyboardSize
                r3 = 1101004800(0x41a00000, float:20.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                if (r2 > r3) goto L_0x0034
                boolean r2 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                if (r2 != 0) goto L_0x0034
                org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.PhotoViewerCaptionEnterView r2 = r2.captionEditText
                int r2 = r2.getEmojiPadding()
                goto L_0x0035
            L_0x0034:
                r2 = 0
            L_0x0035:
                if (r4 >= r1) goto L_0x0194
                android.view.View r3 = r15.getChildAt(r4)
                int r5 = r3.getVisibility()
                r6 = 8
                if (r5 != r6) goto L_0x0045
                goto L_0x0190
            L_0x0045:
                org.telegram.ui.PhotoViewer r5 = org.telegram.ui.PhotoViewer.this
                com.google.android.exoplayer2.ui.AspectRatioFrameLayout r5 = r5.aspectRatioFrameLayout
                if (r3 != r5) goto L_0x0054
                r5 = r17
                r6 = r19
                r7 = r20
                goto L_0x0066
            L_0x0054:
                int r5 = r15.getPaddingLeft()
                int r5 = r17 + r5
                int r6 = r15.getPaddingRight()
                int r6 = r19 - r6
                int r7 = r15.getPaddingBottom()
                int r7 = r20 - r7
            L_0x0066:
                android.view.ViewGroup$LayoutParams r8 = r3.getLayoutParams()
                android.widget.FrameLayout$LayoutParams r8 = (android.widget.FrameLayout.LayoutParams) r8
                int r9 = r3.getMeasuredWidth()
                int r10 = r3.getMeasuredHeight()
                int r11 = r8.gravity
                r12 = -1
                if (r11 != r12) goto L_0x007b
                r11 = 51
            L_0x007b:
                r12 = r11 & 7
                r11 = r11 & 112(0x70, float:1.57E-43)
                r12 = r12 & 7
                r13 = 5
                r14 = 1
                if (r12 == r14) goto L_0x008f
                if (r12 == r13) goto L_0x008a
                int r6 = r8.leftMargin
                goto L_0x0099
            L_0x008a:
                int r6 = r6 - r5
                int r6 = r6 - r9
                int r12 = r8.rightMargin
                goto L_0x0098
            L_0x008f:
                int r6 = r6 - r5
                int r6 = r6 - r9
                int r6 = r6 / 2
                int r12 = r8.leftMargin
                int r6 = r6 + r12
                int r12 = r8.rightMargin
            L_0x0098:
                int r6 = r6 - r12
            L_0x0099:
                r12 = 16
                if (r11 == r12) goto L_0x00ab
                r12 = 80
                if (r11 == r12) goto L_0x00a4
                int r7 = r8.topMargin
                goto L_0x00b7
            L_0x00a4:
                int r7 = r7 - r2
                int r7 = r7 - r18
                int r7 = r7 - r10
                int r8 = r8.bottomMargin
                goto L_0x00b6
            L_0x00ab:
                int r7 = r7 - r2
                int r7 = r7 - r18
                int r7 = r7 - r10
                int r7 = r7 / 2
                int r11 = r8.topMargin
                int r7 = r7 + r11
                int r8 = r8.bottomMargin
            L_0x00b6:
                int r7 = r7 - r8
            L_0x00b7:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.RecyclerListView r8 = r8.mentionListView
                if (r3 != r8) goto L_0x00cc
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = r8.captionEditText
                int r8 = r8.getMeasuredHeight()
            L_0x00c9:
                int r7 = r7 - r8
                goto L_0x0188
            L_0x00cc:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.PhotoViewerCaptionEnterView r8 = r8.captionEditText
                boolean r8 = r8.isPopupView(r3)
                if (r8 == 0) goto L_0x00fe
                boolean r7 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                if (r7 == 0) goto L_0x00f2
                org.telegram.ui.PhotoViewer r7 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.PhotoViewerCaptionEnterView r7 = r7.captionEditText
                int r7 = r7.getTop()
                int r8 = r3.getMeasuredHeight()
                int r7 = r7 - r8
                r8 = 1065353216(0x3var_, float:1.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                goto L_0x0116
            L_0x00f2:
                org.telegram.ui.PhotoViewer r7 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.PhotoViewerCaptionEnterView r7 = r7.captionEditText
                int r7 = r7.getBottom()
                goto L_0x0188
            L_0x00fe:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.PhotoViewer$SelectedPhotosListView r8 = r8.selectedPhotosListView
                if (r3 != r8) goto L_0x0119
                org.telegram.ui.PhotoViewer r7 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.ActionBar.ActionBar r7 = r7.actionBar
                int r7 = r7.getMeasuredHeight()
                r8 = 1084227584(0x40a00000, float:5.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            L_0x0116:
                int r7 = r7 + r8
                goto L_0x0188
            L_0x0119:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                android.widget.ImageView r8 = r8.cameraItem
                if (r3 == r8) goto L_0x013d
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                android.widget.ImageView r8 = r8.muteItem
                if (r3 != r8) goto L_0x012a
                goto L_0x013d
            L_0x012a:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.VideoTimelinePlayView r8 = r8.videoTimelineView
                if (r3 != r8) goto L_0x0188
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                android.widget.FrameLayout r8 = r8.pickerView
                int r8 = r8.getHeight()
                goto L_0x00c9
            L_0x013d:
                org.telegram.ui.PhotoViewer r7 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.VideoTimelinePlayView r7 = r7.videoTimelineView
                if (r7 == 0) goto L_0x015c
                org.telegram.ui.PhotoViewer r7 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.VideoTimelinePlayView r7 = r7.videoTimelineView
                int r7 = r7.getVisibility()
                if (r7 != 0) goto L_0x015c
                org.telegram.ui.PhotoViewer r7 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.Components.VideoTimelinePlayView r7 = r7.videoTimelineView
                int r7 = r7.getTop()
                goto L_0x0166
            L_0x015c:
                org.telegram.ui.PhotoViewer r7 = org.telegram.ui.PhotoViewer.this
                android.widget.FrameLayout r7 = r7.pickerView
                int r7 = r7.getTop()
            L_0x0166:
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                int r8 = r8.sendPhotoType
                r11 = 4
                if (r8 == r11) goto L_0x017b
                org.telegram.ui.PhotoViewer r8 = org.telegram.ui.PhotoViewer.this
                int r8 = r8.sendPhotoType
                if (r8 != r13) goto L_0x0178
                goto L_0x017b
            L_0x0178:
                r8 = 1097859072(0x41700000, float:15.0)
                goto L_0x017d
            L_0x017b:
                r8 = 1109393408(0x42200000, float:40.0)
            L_0x017d:
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r7 = r7 - r8
                int r8 = r3.getMeasuredHeight()
                goto L_0x00c9
            L_0x0188:
                int r8 = r6 + r5
                int r6 = r6 + r9
                int r6 = r6 + r5
                int r10 = r10 + r7
                r3.layout(r8, r7, r6, r10)
            L_0x0190:
                int r4 = r4 + 1
                goto L_0x0035
            L_0x0194:
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
                if (PhotoViewer.this.currentEditMode != 0 || (!PhotoViewer.this.captionEditText.isPopupShowing() && PhotoViewer.this.captionEditText.getEmojiPadding() == 0 && ((AndroidUtilities.usingHardwareInput && PhotoViewer.this.captionEditText.getTag() == null) || getKeyboardHeight() == 0))) {
                    return false;
                }
            } else if (view == PhotoViewer.this.cameraItem || view == PhotoViewer.this.muteItem || view == PhotoViewer.this.pickerView || view == PhotoViewer.this.videoTimelineView || view == PhotoViewer.this.pickerViewSendButton || view == PhotoViewer.this.captionTextViewSwitcher || (PhotoViewer.this.muteItem.getVisibility() == 0 && view == PhotoViewer.this.bottomLayout)) {
                int emojiPadding = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : PhotoViewer.this.captionEditText.getEmojiPadding();
                if (PhotoViewer.this.captionEditText.isPopupShowing() || ((AndroidUtilities.usingHardwareInput && PhotoViewer.this.captionEditText.getTag() != null) || getKeyboardHeight() > AndroidUtilities.dp(80.0f) || emojiPadding != 0)) {
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
                if (view == PhotoViewer.this.aspectRatioFrameLayout || view == PhotoViewer.this.paintingOverlay || !super.drawChild(canvas, view, j)) {
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

    static {
        if (Build.VERSION.SDK_INT >= 24) {
            VPC_PROGRESS = new FloatProperty<VideoPlayerControlFrameLayout>("progress") {
                public void setValue(VideoPlayerControlFrameLayout videoPlayerControlFrameLayout, float f) {
                    videoPlayerControlFrameLayout.setProgress(f);
                }

                public Float get(VideoPlayerControlFrameLayout videoPlayerControlFrameLayout) {
                    return Float.valueOf(videoPlayerControlFrameLayout.getProgress());
                }
            };
        } else {
            VPC_PROGRESS = new Property<VideoPlayerControlFrameLayout, Float>(Float.class, "progress") {
                public void set(VideoPlayerControlFrameLayout videoPlayerControlFrameLayout, Float f) {
                    videoPlayerControlFrameLayout.setProgress(f.floatValue());
                }

                public Float get(VideoPlayerControlFrameLayout videoPlayerControlFrameLayout) {
                    return Float.valueOf(videoPlayerControlFrameLayout.getProgress());
                }
            };
        }
    }

    private class VideoPlayerControlFrameLayout extends FrameLayout {
        private float progress = 1.0f;
        private boolean seekBarTransitionEnabled;

        public VideoPlayerControlFrameLayout(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.progress < 1.0f) {
                return false;
            }
            if (PhotoViewer.this.videoPlayerSeekbar.onTouch(motionEvent.getAction(), motionEvent.getX() - ((float) AndroidUtilities.dp(2.0f)), motionEvent.getY())) {
                getParent().requestDisallowInterceptTouchEvent(true);
                PhotoViewer.this.videoPlayerSeekbarView.invalidate();
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
            Locale locale = Locale.ROOT;
            long j3 = j2 / 60;
            long j4 = j2 % 60;
            Object[] objArr = {Long.valueOf(j3), Long.valueOf(j4), Long.valueOf(j3), Long.valueOf(j4)};
            PhotoViewer.this.videoPlayerSeekbar.setSize((getMeasuredWidth() - AndroidUtilities.dp(16.0f)) - ((int) Math.ceil((double) paint.measureText(String.format(locale, "%02d:%02d / %02d:%02d", objArr)))), getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            float f = 1.0f;
            float f2 = 0.0f;
            if (PhotoViewer.this.videoPlayer != null) {
                float currentPosition = ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration());
                if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility() != 0) {
                    f = currentPosition;
                } else {
                    float leftProgress = currentPosition - PhotoViewer.this.videoTimelineView.getLeftProgress();
                    if (leftProgress >= 0.0f) {
                        f2 = leftProgress;
                    }
                    float rightProgress = f2 / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
                    if (rightProgress <= 1.0f) {
                        f = rightProgress;
                    }
                }
            } else {
                f = 0.0f;
            }
            if (PhotoViewer.this.playerWasReady) {
                PhotoViewer.this.videoPlayerSeekbar.setProgress(f);
            }
            PhotoViewer.this.videoTimelineView.setProgress(f);
        }

        public float getProgress() {
            return this.progress;
        }

        public void setProgress(float f) {
            if (this.progress != f) {
                this.progress = f;
                onProgressChanged(f);
            }
        }

        private void onProgressChanged(float f) {
            PhotoViewer.this.videoPlayerTime.setAlpha(f);
            if (this.seekBarTransitionEnabled) {
                PhotoViewer.this.videoPlayerTime.setPivotX((float) PhotoViewer.this.videoPlayerTime.getWidth());
                PhotoViewer.this.videoPlayerTime.setPivotY((float) PhotoViewer.this.videoPlayerTime.getHeight());
                float f2 = 1.0f - f;
                float f3 = 1.0f - (0.1f * f2);
                PhotoViewer.this.videoPlayerTime.setScaleX(f3);
                PhotoViewer.this.videoPlayerTime.setScaleY(f3);
                PhotoViewer.this.videoPlayerSeekbar.setTransitionProgress(f2);
                return;
            }
            setTranslationY(AndroidUtilities.dpf2(24.0f) * (1.0f - f));
            PhotoViewer.this.videoPlayerSeekbarView.setAlpha(f);
        }

        public void setSeekBarTransitionEnabled(boolean z) {
            if (this.seekBarTransitionEnabled != z) {
                this.seekBarTransitionEnabled = z;
                if (z) {
                    setTranslationY(0.0f);
                    PhotoViewer.this.videoPlayerSeekbarView.setAlpha(1.0f);
                } else {
                    PhotoViewer.this.videoPlayerTime.setScaleX(1.0f);
                    PhotoViewer.this.videoPlayerTime.setScaleY(1.0f);
                    PhotoViewer.this.videoPlayerSeekbar.setTransitionProgress(0.0f);
                }
                onProgressChanged(this.progress);
            }
        }
    }

    private class CaptionTextViewSwitcher extends TextViewSwitcher {
        private float alpha = 1.0f;
        private boolean inScrollView = false;

        public CaptionTextViewSwitcher(Context context) {
            super(context);
        }

        public void setVisibility(int i) {
            setVisibility(i, true);
        }

        public void setVisibility(int i, boolean z) {
            super.setVisibility(i);
            if (this.inScrollView && z) {
                PhotoViewer.this.captionScrollView.setVisibility(i);
            }
        }

        public void setAlpha(float f) {
            this.alpha = f;
            if (this.inScrollView) {
                PhotoViewer.this.captionScrollView.setAlpha(f);
            } else {
                super.setAlpha(f);
            }
        }

        public float getAlpha() {
            if (this.inScrollView) {
                return this.alpha;
            }
            return super.getAlpha();
        }

        public void setTranslationY(float f) {
            super.setTranslationY(f);
            if (this.inScrollView) {
                PhotoViewer.this.captionScrollView.invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (PhotoViewer.this.captionContainer != null && getParent() == PhotoViewer.this.captionContainer) {
                this.inScrollView = true;
                PhotoViewer.this.captionScrollView.setVisibility(getVisibility());
                PhotoViewer.this.captionScrollView.setAlpha(this.alpha);
                super.setAlpha(1.0f);
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (this.inScrollView) {
                this.inScrollView = false;
                PhotoViewer.this.captionScrollView.setVisibility(8);
                super.setAlpha(this.alpha);
            }
        }
    }

    private class CaptionScrollView extends NestedScrollView {
        private Method abortAnimatedScrollMethod;
        /* access modifiers changed from: private */
        public float backgroundAlpha = 1.0f;
        /* access modifiers changed from: private */
        public boolean dontChangeTopMargin;
        private boolean isLandscape;
        private boolean nestedScrollStarted;
        private float overScrollY;
        private final Paint paint = new Paint(1);
        private int pendingTopMargin = -1;
        private int prevHeight;
        private OverScroller scroller;
        private final SpringAnimation springAnimation;
        private int textHash;
        private float velocitySign;
        private float velocityY;

        /* access modifiers changed from: protected */
        public float getBottomFadingEdgeStrength() {
            return 1.0f;
        }

        /* access modifiers changed from: protected */
        public float getTopFadingEdgeStrength() {
            return 1.0f;
        }

        public CaptionScrollView(Context context) {
            super(context);
            setClipChildren(false);
            setOverScrollMode(2);
            this.paint.setColor(-16777216);
            setFadingEdgeLength(AndroidUtilities.dp(12.0f));
            setVerticalFadingEdgeEnabled(true);
            setWillNotDraw(false);
            SpringAnimation springAnimation2 = new SpringAnimation(PhotoViewer.this.captionTextViewSwitcher, DynamicAnimation.TRANSLATION_Y, 0.0f);
            this.springAnimation = springAnimation2;
            springAnimation2.getSpring().setStiffness(100.0f);
            this.springAnimation.setMinimumVisibleChange(1.0f);
            this.springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                    PhotoViewer.CaptionScrollView.this.lambda$new$0$PhotoViewer$CaptionScrollView(dynamicAnimation, f, f2);
                }
            });
            this.springAnimation.getSpring().setDampingRatio(1.0f);
            try {
                Method declaredMethod = NestedScrollView.class.getDeclaredMethod("abortAnimatedScroll", new Class[0]);
                this.abortAnimatedScrollMethod = declaredMethod;
                declaredMethod.setAccessible(true);
            } catch (Exception e) {
                this.abortAnimatedScrollMethod = null;
                FileLog.e((Throwable) e);
            }
            try {
                Field declaredField = NestedScrollView.class.getDeclaredField("mScroller");
                declaredField.setAccessible(true);
                this.scroller = (OverScroller) declaredField.get(this);
            } catch (Exception e2) {
                this.scroller = null;
                FileLog.e((Throwable) e2);
            }
        }

        public /* synthetic */ void lambda$new$0$PhotoViewer$CaptionScrollView(DynamicAnimation dynamicAnimation, float f, float f2) {
            this.overScrollY = f;
            this.velocityY = f2;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0 || motionEvent.getY() >= ((float) (PhotoViewer.this.captionContainer.getTop() - getScrollY())) + PhotoViewer.this.captionTextViewSwitcher.getTranslationY()) {
                return super.onTouchEvent(motionEvent);
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            updateTopMargin(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
            super.onMeasure(i, i2);
        }

        public void applyPendingTopMargin() {
            this.dontChangeTopMargin = false;
            if (this.pendingTopMargin >= 0) {
                ((ViewGroup.MarginLayoutParams) PhotoViewer.this.captionContainer.getLayoutParams()).topMargin = this.pendingTopMargin;
                this.pendingTopMargin = -1;
                requestLayout();
            }
        }

        public int getPendingMarginTopDiff() {
            int i = this.pendingTopMargin;
            if (i >= 0) {
                return i - ((ViewGroup.MarginLayoutParams) PhotoViewer.this.captionContainer.getLayoutParams()).topMargin;
            }
            return 0;
        }

        public void updateTopMargin() {
            updateTopMargin(getWidth(), getHeight());
        }

        private void updateTopMargin(int i, int i2) {
            int calculateNewContainerMarginTop = calculateNewContainerMarginTop(i, i2);
            if (calculateNewContainerMarginTop < 0) {
                return;
            }
            if (this.dontChangeTopMargin) {
                this.pendingTopMargin = calculateNewContainerMarginTop;
                return;
            }
            ((ViewGroup.MarginLayoutParams) PhotoViewer.this.captionContainer.getLayoutParams()).topMargin = calculateNewContainerMarginTop;
            this.pendingTopMargin = -1;
        }

        public int calculateNewContainerMarginTop(int i, int i2) {
            int i3;
            if (i == 0 || i2 == 0) {
                return -1;
            }
            TextView currentView = PhotoViewer.this.captionTextViewSwitcher.getCurrentView();
            CharSequence text = currentView.getText();
            int hashCode = text.hashCode();
            Point point = AndroidUtilities.displaySize;
            boolean z = point.x > point.y;
            if (this.textHash == hashCode && this.isLandscape == z && this.prevHeight == i2) {
                return -1;
            }
            this.textHash = hashCode;
            this.isLandscape = z;
            this.prevHeight = i2;
            currentView.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
            Layout layout = currentView.getLayout();
            int lineCount = layout.getLineCount();
            int i4 = 3;
            if ((!z || lineCount > 3) && (z || lineCount > 5)) {
                if (!z) {
                    i4 = 5;
                }
                int min = Math.min(i4, lineCount);
                loop0:
                while (min > 1) {
                    int i5 = min - 1;
                    for (int lineStart = layout.getLineStart(i5); lineStart < layout.getLineEnd(i5); lineStart++) {
                        if (Character.isLetterOrDigit(text.charAt(lineStart))) {
                            break loop0;
                        }
                    }
                    min--;
                }
                i2 -= currentView.getPaint().getFontMetricsInt((Paint.FontMetricsInt) null) * min;
                i3 = AndroidUtilities.dp(8.0f);
            } else {
                i3 = currentView.getMeasuredHeight();
            }
            return i2 - i3;
        }

        public void reset() {
            scrollTo(0, 0);
        }

        public void stopScrolling() {
            Method method = this.abortAnimatedScrollMethod;
            if (method != null) {
                try {
                    method.invoke(this, new Object[0]);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }

        public void fling(int i) {
            super.fling(i);
            this.velocitySign = Math.signum((float) i);
            this.velocityY = 0.0f;
        }

        public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2, int i3) {
            iArr[1] = 0;
            if (!this.nestedScrollStarted || ((this.overScrollY <= 0.0f || i2 <= 0) && (this.overScrollY >= 0.0f || i2 >= 0))) {
                return false;
            }
            float f = this.overScrollY;
            float f2 = (float) i2;
            float f3 = f - f2;
            if (f > 0.0f) {
                if (f3 < 0.0f) {
                    this.overScrollY = 0.0f;
                    iArr[1] = (int) (((float) iArr[1]) + f2 + f3);
                } else {
                    this.overScrollY = f3;
                    iArr[1] = iArr[1] + i2;
                }
            } else if (f3 > 0.0f) {
                this.overScrollY = 0.0f;
                iArr[1] = (int) (((float) iArr[1]) + f2 + f3);
            } else {
                this.overScrollY = f3;
                iArr[1] = iArr[1] + i2;
            }
            PhotoViewer.this.captionTextViewSwitcher.setTranslationY(this.overScrollY);
            return true;
        }

        public void dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr, int i5, int[] iArr2) {
            float f;
            if (i4 != 0) {
                int round = Math.round(((float) i4) * (1.0f - Math.abs((-this.overScrollY) / ((float) (PhotoViewer.this.captionContainer.getTop() - ((Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()))))));
                if (round == 0) {
                    return;
                }
                if (this.nestedScrollStarted) {
                    this.overScrollY -= (float) round;
                    PhotoViewer.this.captionTextViewSwitcher.setTranslationY(this.overScrollY);
                } else if (!this.springAnimation.isRunning()) {
                    OverScroller overScroller = this.scroller;
                    float currVelocity = overScroller != null ? overScroller.getCurrVelocity() : Float.NaN;
                    if (!Float.isNaN(currVelocity)) {
                        Point point = AndroidUtilities.displaySize;
                        float min = Math.min(point.x > point.y ? 3000.0f : 5000.0f, currVelocity);
                        round = (int) ((((float) round) * min) / currVelocity);
                        f = min * (-this.velocitySign);
                    } else {
                        f = 0.0f;
                    }
                    if (round != 0) {
                        this.overScrollY -= (float) round;
                        PhotoViewer.this.captionTextViewSwitcher.setTranslationY(this.overScrollY);
                    }
                    startSpringAnimationIfNotRunning(f);
                }
            }
        }

        private void startSpringAnimationIfNotRunning(float f) {
            if (!this.springAnimation.isRunning()) {
                this.springAnimation.setStartVelocity(f);
                this.springAnimation.start();
            }
        }

        public boolean startNestedScroll(int i, int i2) {
            if (i2 == 0) {
                this.springAnimation.cancel();
                this.nestedScrollStarted = true;
                this.overScrollY = PhotoViewer.this.captionTextViewSwitcher.getTranslationY();
            }
            return true;
        }

        public void computeScroll() {
            OverScroller overScroller;
            super.computeScroll();
            if (!this.nestedScrollStarted && this.overScrollY != 0.0f && (overScroller = this.scroller) != null && overScroller.isFinished()) {
                startSpringAnimationIfNotRunning(0.0f);
            }
        }

        public void stopNestedScroll(int i) {
            OverScroller overScroller;
            if (this.nestedScrollStarted && i == 0) {
                this.nestedScrollStarted = false;
                if (this.overScrollY != 0.0f && (overScroller = this.scroller) != null && overScroller.isFinished()) {
                    startSpringAnimationIfNotRunning(this.velocityY);
                }
            }
        }

        public void draw(Canvas canvas) {
            this.paint.setAlpha((int) (this.backgroundAlpha * 127.0f));
            canvas.drawRect(0.0f, ((float) PhotoViewer.this.captionContainer.getTop()) + PhotoViewer.this.captionTextViewSwitcher.getTranslationY(), (float) getWidth(), (float) (getHeight() + getScrollY()), this.paint);
            super.draw(canvas);
        }

        public void invalidate() {
            super.invalidate();
            if (PhotoViewer.this.isActionBarVisible) {
                boolean z = getScrollY() == 0;
                if (!z) {
                    z = (((PhotoViewer.this.captionContainer.getTop() + ((int) PhotoViewer.this.captionTextViewSwitcher.getTranslationY())) - getScrollY()) + ((Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight())) - AndroidUtilities.dp(12.0f) > PhotoViewer.this.photoProgressViews[0].getY() + PhotoViewer.this.photoProgressViews[0].size;
                }
                PhotoViewer.this.photoProgressViews[0].setIndexedAlpha(2, z ? 1.0f : 0.0f, true);
            }
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

    /* JADX WARNING: Code restructure failed: missing block: B:253:0x0500, code lost:
        if (r1.get(r1.size() - 1).getDialogId() != r0.mergeDialogId) goto L_0x0541;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x053f, code lost:
        if (r0.imagesArrTemp.get(0).getDialogId() != r0.mergeDialogId) goto L_0x0541;
     */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x054e  */
    /* JADX WARNING: Removed duplicated region for block: B:370:? A[RETURN, SYNTHETIC] */
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
            if (r2 >= r4) goto L_0x066c
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
            goto L_0x066c
        L_0x0045:
            int r2 = r2 + 1
            goto L_0x0013
        L_0x0048:
            int r2 = org.telegram.messenger.NotificationCenter.fileDidLoad
            if (r1 != r2) goto L_0x00b9
            r1 = r24[r7]
            java.lang.String r1 = (java.lang.String) r1
            r2 = 0
        L_0x0051:
            if (r2 >= r4) goto L_0x066c
            java.lang.String[] r8 = r0.currentFileNames
            r9 = r8[r2]
            if (r9 == 0) goto L_0x00b6
            r8 = r8[r2]
            boolean r8 = r8.equals(r1)
            if (r8 == 0) goto L_0x00b6
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
            if (r1 != 0) goto L_0x00ac
            if (r2 != 0) goto L_0x00ac
            org.telegram.messenger.MessageObject r1 = r0.currentMessageObject
            if (r1 == 0) goto L_0x0091
            boolean r1 = r1.isVideo()
            if (r1 != 0) goto L_0x00a9
        L_0x0091:
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = r0.currentBotInlineResult
            if (r1 == 0) goto L_0x00ac
            java.lang.String r1 = r1.type
            java.lang.String r3 = "video"
            boolean r1 = r1.equals(r3)
            if (r1 != 0) goto L_0x00a9
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = r0.currentBotInlineResult
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isVideoDocument(r1)
            if (r1 == 0) goto L_0x00ac
        L_0x00a9:
            r0.onActionClick(r7)
        L_0x00ac:
            if (r2 != 0) goto L_0x066c
            org.telegram.ui.Components.VideoPlayer r1 = r0.videoPlayer
            if (r1 == 0) goto L_0x066c
            r0.currentVideoFinishedLoading = r6
            goto L_0x066c
        L_0x00b6:
            int r2 = r2 + 1
            goto L_0x0051
        L_0x00b9:
            int r2 = org.telegram.messenger.NotificationCenter.FileLoadProgressChanged
            r8 = 0
            if (r1 != r2) goto L_0x0190
            r1 = r24[r7]
            java.lang.String r1 = (java.lang.String) r1
            r2 = 0
        L_0x00c4:
            if (r2 >= r4) goto L_0x066c
            java.lang.String[] r10 = r0.currentFileNames
            r11 = r10[r2]
            if (r11 == 0) goto L_0x0187
            r10 = r10[r2]
            boolean r10 = r10.equals(r1)
            if (r10 == 0) goto L_0x0187
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
            if (r2 == 0) goto L_0x0100
            if (r2 != r6) goto L_0x00f5
            org.telegram.messenger.ImageReceiver r11 = r0.sideImage
            org.telegram.messenger.ImageReceiver r12 = r0.rightImage
            if (r11 == r12) goto L_0x0100
        L_0x00f5:
            if (r2 != r5) goto L_0x00fe
            org.telegram.messenger.ImageReceiver r11 = r0.sideImage
            org.telegram.messenger.ImageReceiver r12 = r0.leftImage
            if (r11 != r12) goto L_0x00fe
            goto L_0x0100
        L_0x00fe:
            r11 = 0
            goto L_0x0101
        L_0x0100:
            r11 = 1
        L_0x0101:
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r12 = r0.photoProgressViews
            r12 = r12[r2]
            r12.setProgress(r10, r11)
            if (r2 != 0) goto L_0x0187
            org.telegram.ui.Components.VideoPlayer r11 = r0.videoPlayer
            if (r11 == 0) goto L_0x0187
            org.telegram.ui.Components.VideoPlayerSeekBar r11 = r0.videoPlayerSeekbar
            if (r11 == 0) goto L_0x0187
            boolean r11 = r0.currentVideoFinishedLoading
            if (r11 == 0) goto L_0x0119
        L_0x0116:
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x016f
        L_0x0119:
            long r13 = android.os.SystemClock.elapsedRealtime()
            long r5 = r0.lastBufferedPositionCheck
            long r5 = r13 - r5
            long r5 = java.lang.Math.abs(r5)
            r15 = 500(0x1f4, double:2.47E-321)
            int r17 = (r5 > r15 ? 1 : (r5 == r15 ? 0 : -1))
            if (r17 < 0) goto L_0x016c
            float r5 = r0.seekToProgressPending
            r6 = 0
            int r15 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r15 != 0) goto L_0x0154
            org.telegram.ui.Components.VideoPlayer r5 = r0.videoPlayer
            long r3 = r5.getDuration()
            org.telegram.ui.Components.VideoPlayer r5 = r0.videoPlayer
            long r11 = r5.getCurrentPosition()
            int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r5 < 0) goto L_0x0153
            r18 = -9223372036854775807(0xNUM, double:-4.9E-324)
            int r5 = (r3 > r18 ? 1 : (r3 == r18 ? 0 : -1))
            if (r5 == 0) goto L_0x0153
            int r5 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r5 < 0) goto L_0x0153
            float r5 = (float) r11
            float r3 = (float) r3
            float r5 = r5 / r3
            goto L_0x0154
        L_0x0153:
            r5 = 0
        L_0x0154:
            boolean r3 = r0.isStreaming
            if (r3 == 0) goto L_0x0167
            int r3 = r0.currentAccount
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r3)
            java.lang.String[] r4 = r0.currentFileNames
            r4 = r4[r7]
            float r3 = r3.getBufferedProgressFromPosition(r5, r4)
            goto L_0x0169
        L_0x0167:
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x0169:
            r0.lastBufferedPositionCheck = r13
            goto L_0x0116
        L_0x016c:
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x0116
        L_0x016f:
            int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r4 == 0) goto L_0x0184
            org.telegram.ui.Components.VideoPlayerSeekBar r4 = r0.videoPlayerSeekbar
            r4.setBufferedProgress(r3)
            org.telegram.ui.Components.PipVideoView r4 = r0.pipVideoView
            if (r4 == 0) goto L_0x017f
            r4.setBufferedProgress(r3)
        L_0x017f:
            android.view.View r3 = r0.videoPlayerSeekbarView
            r3.invalidate()
        L_0x0184:
            r0.checkBufferedProgress(r10)
        L_0x0187:
            int r2 = r2 + 1
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = 3
            r5 = 2
            r6 = 1
            goto L_0x00c4
        L_0x0190:
            int r2 = org.telegram.messenger.NotificationCenter.dialogPhotosLoaded
            r3 = 4
            r4 = -1
            if (r1 != r2) goto L_0x02e0
            r2 = 3
            r1 = r24[r2]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r2 = r24[r7]
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            int r5 = r0.avatarsDialogId
            if (r5 != r2) goto L_0x066c
            int r2 = r0.classGuid
            if (r2 != r1) goto L_0x066c
            r1 = 2
            r1 = r24[r1]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r2 = r24[r3]
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            boolean r3 = r2.isEmpty()
            if (r3 == 0) goto L_0x01c3
            return
        L_0x01c3:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.imagesArrLocations
            r3.clear()
            java.util.ArrayList<java.lang.Integer> r3 = r0.imagesArrLocationsSizes
            r3.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r3 = r0.avatarsArr
            r3.clear()
            r3 = 0
            r5 = -1
        L_0x01d4:
            int r6 = r2.size()
            if (r3 >= r6) goto L_0x0252
            java.lang.Object r6 = r2.get(r3)
            org.telegram.tgnet.TLRPC$Photo r6 = (org.telegram.tgnet.TLRPC$Photo) r6
            if (r6 == 0) goto L_0x024f
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r8 != 0) goto L_0x024f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r6.sizes
            if (r8 != 0) goto L_0x01eb
            goto L_0x024f
        L_0x01eb:
            r9 = 640(0x280, float:8.97E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r9)
            if (r8 == 0) goto L_0x024f
            if (r5 != r4) goto L_0x0228
            org.telegram.messenger.ImageLocation r9 = r0.currentFileLocation
            if (r9 == 0) goto L_0x0228
            r9 = 0
        L_0x01fa:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r6.sizes
            int r10 = r10.size()
            if (r9 >= r10) goto L_0x0228
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r6.sizes
            java.lang.Object r10 = r10.get(r9)
            org.telegram.tgnet.TLRPC$PhotoSize r10 = (org.telegram.tgnet.TLRPC$PhotoSize) r10
            org.telegram.tgnet.TLRPC$FileLocation r10 = r10.location
            int r11 = r10.local_id
            org.telegram.messenger.ImageLocation r12 = r0.currentFileLocation
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r12 = r12.location
            int r13 = r12.local_id
            if (r11 != r13) goto L_0x0225
            long r10 = r10.volume_id
            long r12 = r12.volume_id
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 != 0) goto L_0x0225
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r5 = r0.imagesArrLocations
            int r5 = r5.size()
            goto L_0x0228
        L_0x0225:
            int r9 = r9 + 1
            goto L_0x01fa
        L_0x0228:
            int r9 = r6.dc_id
            if (r9 == 0) goto L_0x0234
            org.telegram.tgnet.TLRPC$FileLocation r10 = r8.location
            r10.dc_id = r9
            byte[] r9 = r6.file_reference
            r10.file_reference = r9
        L_0x0234:
            org.telegram.messenger.ImageLocation r9 = org.telegram.messenger.ImageLocation.getForPhoto(r8, r6)
            if (r9 == 0) goto L_0x024f
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r10 = r0.imagesArrLocations
            r10.add(r9)
            java.util.ArrayList<java.lang.Integer> r9 = r0.imagesArrLocationsSizes
            int r8 = r8.size
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r9.add(r8)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r8 = r0.avatarsArr
            r8.add(r6)
        L_0x024f:
            int r3 = r3 + 1
            goto L_0x01d4
        L_0x0252:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r2 = r0.avatarsArr
            boolean r2 = r2.isEmpty()
            r3 = 6
            if (r2 != 0) goto L_0x0261
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.menuItem
            r2.showSubItem(r3)
            goto L_0x0266
        L_0x0261:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.menuItem
            r2.hideSubItem(r3)
        L_0x0266:
            r0.needSearchImageInArr = r7
            r0.currentIndex = r4
            if (r5 == r4) goto L_0x0270
            r0.setImageIndex(r5)
            goto L_0x02ca
        L_0x0270:
            int r2 = r0.avatarsDialogId
            r3 = 0
            if (r2 <= 0) goto L_0x028b
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r4 = r0.avatarsDialogId
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r4)
            r20 = r3
            r3 = r2
            r2 = r20
            goto L_0x029c
        L_0x028b:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r4 = r0.avatarsDialogId
            int r4 = -r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r4)
        L_0x029c:
            if (r3 != 0) goto L_0x02a0
            if (r2 == 0) goto L_0x02ca
        L_0x02a0:
            if (r3 == 0) goto L_0x02a8
            r4 = 1
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForUser(r3, r4)
            goto L_0x02ad
        L_0x02a8:
            r4 = 1
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForChat(r2, r4)
        L_0x02ad:
            if (r2 == 0) goto L_0x02ca
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.imagesArrLocations
            r3.add(r7, r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r2 = r0.avatarsArr
            org.telegram.tgnet.TLRPC$TL_photoEmpty r3 = new org.telegram.tgnet.TLRPC$TL_photoEmpty
            r3.<init>()
            r2.add(r7, r3)
            java.util.ArrayList<java.lang.Integer> r2 = r0.imagesArrLocationsSizes
            java.lang.Integer r3 = java.lang.Integer.valueOf(r7)
            r2.add(r7, r3)
            r0.setImageIndex(r7)
        L_0x02ca:
            if (r1 == 0) goto L_0x066c
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r3 = r0.avatarsDialogId
            r4 = 80
            r5 = 0
            r7 = 0
            int r8 = r0.classGuid
            r2.loadDialogPhotos(r3, r4, r5, r7, r8)
            goto L_0x066c
        L_0x02e0:
            int r2 = org.telegram.messenger.NotificationCenter.mediaCountDidLoad
            if (r1 != r2) goto L_0x03a0
            r1 = r24[r7]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            long r3 = r0.currentDialogId
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x02f8
            long r3 = r0.mergeDialogId
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x066c
        L_0x02f8:
            long r3 = r0.currentDialogId
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x030a
            r3 = 1
            r1 = r24[r3]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r0.totalImagesCount = r1
            goto L_0x031b
        L_0x030a:
            r3 = 1
            long r4 = r0.mergeDialogId
            int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x031b
            r1 = r24[r3]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r0.totalImagesCountMerge = r1
        L_0x031b:
            boolean r1 = r0.needSearchImageInArr
            if (r1 == 0) goto L_0x033c
            boolean r1 = r0.isFirstLoading
            if (r1 == 0) goto L_0x033c
            r0.isFirstLoading = r7
            r0.loadingMoreImages = r3
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r1)
            long r3 = r0.currentDialogId
            r5 = 20
            r6 = 0
            int r7 = r0.sharedMediaType
            r8 = 1
            int r9 = r0.classGuid
            r2.loadMedia(r3, r5, r6, r7, r8, r9)
            goto L_0x066c
        L_0x033c:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x066c
            boolean r1 = r0.opennedFromMedia
            r2 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.String r3 = "Of"
            if (r1 == 0) goto L_0x0370
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
            goto L_0x066c
        L_0x0370:
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
            goto L_0x066c
        L_0x03a0:
            int r2 = org.telegram.messenger.NotificationCenter.mediaDidLoad
            if (r1 != r2) goto L_0x05d0
            r1 = r24[r7]
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r3 = 3
            r3 = r24[r3]
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            long r5 = r0.currentDialogId
            int r10 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r10 == 0) goto L_0x03c1
            long r5 = r0.mergeDialogId
            int r10 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r10 != 0) goto L_0x066c
        L_0x03c1:
            int r5 = r0.classGuid
            if (r3 != r5) goto L_0x066c
            r0.loadingMoreImages = r7
            long r5 = r0.currentDialogId
            int r3 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r3 != 0) goto L_0x03cf
            r1 = 0
            goto L_0x03d0
        L_0x03cf:
            r1 = 1
        L_0x03d0:
            r2 = 2
            r3 = r24[r2]
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            boolean[] r2 = r0.endReached
            r5 = 5
            r5 = r24[r5]
            java.lang.Boolean r5 = (java.lang.Boolean) r5
            boolean r5 = r5.booleanValue()
            r2[r1] = r5
            boolean r2 = r0.needSearchImageInArr
            if (r2 == 0) goto L_0x056a
            boolean r2 = r3.isEmpty()
            if (r2 == 0) goto L_0x03f7
            if (r1 != 0) goto L_0x03f4
            long r5 = r0.mergeDialogId
            int r2 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r2 != 0) goto L_0x03f7
        L_0x03f4:
            r0.needSearchImageInArr = r7
            return
        L_0x03f7:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArr
            int r5 = r0.currentIndex
            java.lang.Object r2 = r2.get(r5)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            r5 = 0
            r6 = 0
            r10 = -1
        L_0x0404:
            int r12 = r3.size()
            if (r5 >= r12) goto L_0x0459
            java.lang.Object r12 = r3.get(r5)
            org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r13 = r0.imagesByIdsTemp
            r13 = r13[r1]
            int r14 = r12.getId()
            int r13 = r13.indexOfKey(r14)
            if (r13 >= 0) goto L_0x0456
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r13 = r0.imagesByIdsTemp
            r13 = r13[r1]
            int r14 = r12.getId()
            r13.put(r14, r12)
            boolean r13 = r0.opennedFromMedia
            if (r13 == 0) goto L_0x0440
            java.util.ArrayList<org.telegram.messenger.MessageObject> r13 = r0.imagesArrTemp
            r13.add(r12)
            int r12 = r12.getId()
            int r13 = r2.getId()
            if (r12 != r13) goto L_0x043d
            r10 = r6
        L_0x043d:
            int r6 = r6 + 1
            goto L_0x0456
        L_0x0440:
            int r6 = r6 + 1
            java.util.ArrayList<org.telegram.messenger.MessageObject> r13 = r0.imagesArrTemp
            r13.add(r7, r12)
            int r12 = r12.getId()
            int r13 = r2.getId()
            if (r12 != r13) goto L_0x0456
            int r10 = r3.size()
            int r10 = r10 - r6
        L_0x0456:
            int r5 = r5 + 1
            goto L_0x0404
        L_0x0459:
            if (r6 != 0) goto L_0x046d
            if (r1 != 0) goto L_0x0463
            long r2 = r0.mergeDialogId
            int r5 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r5 != 0) goto L_0x046d
        L_0x0463:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArr
            int r2 = r2.size()
            r0.totalImagesCount = r2
            r0.totalImagesCountMerge = r7
        L_0x046d:
            if (r10 == r4) goto L_0x04b4
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            r1.clear()
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArrTemp
            r1.addAll(r2)
            r1 = 0
            r2 = 2
        L_0x047d:
            if (r1 >= r2) goto L_0x0495
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r3 = r0.imagesByIds
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r5 = r0.imagesByIdsTemp
            r5 = r5[r1]
            android.util.SparseArray r5 = r5.clone()
            r3[r1] = r5
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r3 = r0.imagesByIdsTemp
            r3 = r3[r1]
            r3.clear()
            int r1 = r1 + 1
            goto L_0x047d
        L_0x0495:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArrTemp
            r1.clear()
            r0.needSearchImageInArr = r7
            r0.currentIndex = r4
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r1 = r1.size()
            if (r10 < r1) goto L_0x04af
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r1 = r1.size()
            r2 = 1
            int r10 = r1 + -1
        L_0x04af:
            r0.setImageIndex(r10)
            goto L_0x066c
        L_0x04b4:
            boolean r2 = r0.opennedFromMedia
            if (r2 == 0) goto L_0x0503
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArrTemp
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x04c2
            r2 = 0
            goto L_0x04d4
        L_0x04c2:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArrTemp
            int r3 = r2.size()
            r4 = 1
            int r3 = r3 - r4
            java.lang.Object r2 = r2.get(r3)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            int r2 = r2.getId()
        L_0x04d4:
            if (r1 != 0) goto L_0x0547
            boolean[] r3 = r0.endReached
            boolean r3 = r3[r1]
            if (r3 == 0) goto L_0x0547
            long r3 = r0.mergeDialogId
            int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r5 == 0) goto L_0x0547
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArrTemp
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0544
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArrTemp
            int r3 = r1.size()
            r4 = 1
            int r3 = r3 - r4
            java.lang.Object r1 = r1.get(r3)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            long r3 = r1.getDialogId()
            long r5 = r0.mergeDialogId
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 == 0) goto L_0x0544
            goto L_0x0541
        L_0x0503:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArrTemp
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x050d
            r2 = 0
            goto L_0x0519
        L_0x050d:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r0.imagesArrTemp
            java.lang.Object r2 = r2.get(r7)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            int r2 = r2.getId()
        L_0x0519:
            if (r1 != 0) goto L_0x0547
            boolean[] r3 = r0.endReached
            boolean r3 = r3[r1]
            if (r3 == 0) goto L_0x0547
            long r3 = r0.mergeDialogId
            int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r5 == 0) goto L_0x0547
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArrTemp
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0544
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArrTemp
            java.lang.Object r1 = r1.get(r7)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            long r3 = r1.getDialogId()
            long r5 = r0.mergeDialogId
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 == 0) goto L_0x0544
        L_0x0541:
            r1 = 1
            r6 = 0
            goto L_0x0548
        L_0x0544:
            r6 = r2
            r1 = 1
            goto L_0x0548
        L_0x0547:
            r6 = r2
        L_0x0548:
            boolean[] r2 = r0.endReached
            boolean r2 = r2[r1]
            if (r2 != 0) goto L_0x066c
            r2 = 1
            r0.loadingMoreImages = r2
            int r2 = r0.currentAccount
            org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r2)
            if (r1 != 0) goto L_0x055c
            long r3 = r0.currentDialogId
            goto L_0x055e
        L_0x055c:
            long r3 = r0.mergeDialogId
        L_0x055e:
            r5 = 40
            int r7 = r0.sharedMediaType
            r8 = 1
            int r9 = r0.classGuid
            r2.loadMedia(r3, r5, r6, r7, r8, r9)
            goto L_0x066c
        L_0x056a:
            java.util.Iterator r2 = r3.iterator()
            r3 = 0
        L_0x056f:
            boolean r5 = r2.hasNext()
            if (r5 == 0) goto L_0x05a6
            java.lang.Object r5 = r2.next()
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r6 = r0.imagesByIds
            r6 = r6[r1]
            int r8 = r5.getId()
            int r6 = r6.indexOfKey(r8)
            if (r6 >= 0) goto L_0x056f
            int r3 = r3 + 1
            boolean r6 = r0.opennedFromMedia
            if (r6 == 0) goto L_0x0595
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r0.imagesArr
            r6.add(r5)
            goto L_0x059a
        L_0x0595:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r0.imagesArr
            r6.add(r7, r5)
        L_0x059a:
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r6 = r0.imagesByIds
            r6 = r6[r1]
            int r8 = r5.getId()
            r6.put(r8, r5)
            goto L_0x056f
        L_0x05a6:
            boolean r1 = r0.opennedFromMedia
            if (r1 == 0) goto L_0x05b8
            if (r3 != 0) goto L_0x066c
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r1 = r1.size()
            r0.totalImagesCount = r1
            r0.totalImagesCountMerge = r7
            goto L_0x066c
        L_0x05b8:
            if (r3 == 0) goto L_0x05c4
            int r1 = r0.currentIndex
            r0.currentIndex = r4
            int r1 = r1 + r3
            r0.setImageIndex(r1)
            goto L_0x066c
        L_0x05c4:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r1 = r1.size()
            r0.totalImagesCount = r1
            r0.totalImagesCountMerge = r7
            goto L_0x066c
        L_0x05d0:
            int r2 = org.telegram.messenger.NotificationCenter.emojiDidLoad
            if (r1 != r2) goto L_0x05dd
            org.telegram.ui.PhotoViewer$CaptionTextViewSwitcher r1 = r0.captionTextViewSwitcher
            if (r1 == 0) goto L_0x066c
            r1.invalidateViews()
            goto L_0x066c
        L_0x05dd:
            int r2 = org.telegram.messenger.NotificationCenter.filePreparingFailed
            if (r1 != r2) goto L_0x061c
            r1 = r24[r7]
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            boolean r2 = r0.loadInitialVideo
            if (r2 == 0) goto L_0x05fa
            r0.loadInitialVideo = r7
            org.telegram.ui.Components.RadialProgressView r1 = r0.progressView
            r1.setVisibility(r3)
            android.net.Uri r1 = r0.currentPlayingVideoFile
            org.telegram.ui.PhotoViewer$EditState r2 = r0.editState
            org.telegram.messenger.MediaController$SavedFilterState r2 = r2.savedFilterState
            r0.preparePlayer(r1, r7, r7, r2)
            goto L_0x066c
        L_0x05fa:
            boolean r2 = r0.tryStartRequestPreviewOnFinish
            if (r2 == 0) goto L_0x0610
            r0.releasePlayer(r7)
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r2 = r0.videoPreviewMessageObject
            r3 = 1
            boolean r1 = r1.scheduleVideoConvert(r2, r3)
            r1 = r1 ^ r3
            r0.tryStartRequestPreviewOnFinish = r1
            goto L_0x066c
        L_0x0610:
            org.telegram.messenger.MessageObject r2 = r0.videoPreviewMessageObject
            if (r1 != r2) goto L_0x066c
            r0.requestingPreview = r7
            org.telegram.ui.Components.RadialProgressView r1 = r0.progressView
            r1.setVisibility(r3)
            goto L_0x066c
        L_0x061c:
            int r2 = org.telegram.messenger.NotificationCenter.fileNewChunkAvailable
            if (r1 != r2) goto L_0x066c
            r1 = r24[r7]
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            org.telegram.messenger.MessageObject r2 = r0.videoPreviewMessageObject
            if (r1 != r2) goto L_0x066c
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
            if (r3 == 0) goto L_0x066c
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
            org.telegram.ui.PhotoViewer$EditState r3 = r0.editState
            org.telegram.messenger.MediaController$SavedFilterState r3 = r3.savedFilterState
            r0.preparePlayer(r2, r7, r1, r3)
        L_0x066c:
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
    /* JADX WARNING: Missing exception handler attribute for start block: B:39:0x0098 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSharePressed() {
        /*
            r6 = this;
            android.app.Activity r0 = r6.parentActivity
            if (r0 == 0) goto L_0x00c4
            boolean r0 = r6.allowShare
            if (r0 != 0) goto L_0x000a
            goto L_0x00c4
        L_0x000a:
            org.telegram.messenger.MessageObject r0 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c0 }
            r1 = 1
            r2 = 0
            r3 = 0
            if (r0 == 0) goto L_0x0041
            org.telegram.messenger.MessageObject r0 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c0 }
            boolean r2 = r0.isVideo()     // Catch:{ Exception -> 0x00c0 }
            org.telegram.messenger.MessageObject r0 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c0 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x00c0 }
            java.lang.String r0 = r0.attachPath     // Catch:{ Exception -> 0x00c0 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x00c0 }
            if (r0 != 0) goto L_0x0036
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x00c0 }
            org.telegram.messenger.MessageObject r4 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c0 }
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner     // Catch:{ Exception -> 0x00c0 }
            java.lang.String r4 = r4.attachPath     // Catch:{ Exception -> 0x00c0 }
            r0.<init>(r4)     // Catch:{ Exception -> 0x00c0 }
            boolean r4 = r0.exists()     // Catch:{ Exception -> 0x00c0 }
            if (r4 != 0) goto L_0x0035
            goto L_0x0036
        L_0x0035:
            r3 = r0
        L_0x0036:
            if (r3 != 0) goto L_0x0059
            org.telegram.messenger.MessageObject r0 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c0 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x00c0 }
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToMessage(r0)     // Catch:{ Exception -> 0x00c0 }
            goto L_0x0059
        L_0x0041:
            org.telegram.messenger.ImageLocation r0 = r6.currentFileLocation     // Catch:{ Exception -> 0x00c0 }
            if (r0 == 0) goto L_0x0059
            org.telegram.messenger.ImageLocation r0 = r6.currentFileLocation     // Catch:{ Exception -> 0x00c0 }
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r0 = r0.location     // Catch:{ Exception -> 0x00c0 }
            int r3 = r6.avatarsDialogId     // Catch:{ Exception -> 0x00c0 }
            if (r3 != 0) goto L_0x0054
            boolean r3 = r6.isEvent     // Catch:{ Exception -> 0x00c0 }
            if (r3 == 0) goto L_0x0052
            goto L_0x0054
        L_0x0052:
            r3 = 0
            goto L_0x0055
        L_0x0054:
            r3 = 1
        L_0x0055:
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r3)     // Catch:{ Exception -> 0x00c0 }
        L_0x0059:
            boolean r0 = r3.exists()     // Catch:{ Exception -> 0x00c0 }
            if (r0 == 0) goto L_0x00bc
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x00c0 }
            java.lang.String r4 = "android.intent.action.SEND"
            r0.<init>(r4)     // Catch:{ Exception -> 0x00c0 }
            if (r2 == 0) goto L_0x006e
            java.lang.String r2 = "video/mp4"
            r0.setType(r2)     // Catch:{ Exception -> 0x00c0 }
            goto L_0x0081
        L_0x006e:
            org.telegram.messenger.MessageObject r2 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c0 }
            if (r2 == 0) goto L_0x007c
            org.telegram.messenger.MessageObject r2 = r6.currentMessageObject     // Catch:{ Exception -> 0x00c0 }
            java.lang.String r2 = r2.getMimeType()     // Catch:{ Exception -> 0x00c0 }
            r0.setType(r2)     // Catch:{ Exception -> 0x00c0 }
            goto L_0x0081
        L_0x007c:
            java.lang.String r2 = "image/jpeg"
            r0.setType(r2)     // Catch:{ Exception -> 0x00c0 }
        L_0x0081:
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00c0 }
            r4 = 24
            java.lang.String r5 = "android.intent.extra.STREAM"
            if (r2 < r4) goto L_0x00a0
            android.app.Activity r2 = r6.parentActivity     // Catch:{ Exception -> 0x0098 }
            java.lang.String r4 = "org.telegram.messenger.beta.provider"
            android.net.Uri r2 = androidx.core.content.FileProvider.getUriForFile(r2, r4, r3)     // Catch:{ Exception -> 0x0098 }
            r0.putExtra(r5, r2)     // Catch:{ Exception -> 0x0098 }
            r0.setFlags(r1)     // Catch:{ Exception -> 0x0098 }
            goto L_0x00a7
        L_0x0098:
            android.net.Uri r1 = android.net.Uri.fromFile(r3)     // Catch:{ Exception -> 0x00c0 }
            r0.putExtra(r5, r1)     // Catch:{ Exception -> 0x00c0 }
            goto L_0x00a7
        L_0x00a0:
            android.net.Uri r1 = android.net.Uri.fromFile(r3)     // Catch:{ Exception -> 0x00c0 }
            r0.putExtra(r5, r1)     // Catch:{ Exception -> 0x00c0 }
        L_0x00a7:
            android.app.Activity r1 = r6.parentActivity     // Catch:{ Exception -> 0x00c0 }
            java.lang.String r2 = "ShareFile"
            r3 = 2131626792(0x7f0e0b28, float:1.888083E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ Exception -> 0x00c0 }
            android.content.Intent r0 = android.content.Intent.createChooser(r0, r2)     // Catch:{ Exception -> 0x00c0 }
            r2 = 500(0x1f4, float:7.0E-43)
            r1.startActivityForResult(r0, r2)     // Catch:{ Exception -> 0x00c0 }
            goto L_0x00c4
        L_0x00bc:
            r6.showDownloadAlert()     // Catch:{ Exception -> 0x00c0 }
            goto L_0x00c4
        L_0x00c0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00c4:
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
            float max = Math.max(containerViewWidth / ((float) ((int) (bitmapWidth * min))), containerViewHeight / ((float) ((int) (bitmapHeight * min))));
            this.scale = max;
            updateMinMax(max);
        }
    }

    public void setParentAlert(ChatAttachAlert chatAttachAlert) {
        this.parentAlert = chatAttachAlert;
    }

    public void setParentActivity(Activity activity) {
        String str;
        Activity activity2 = activity;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.centerImage.setCurrentAccount(i);
        this.leftImage.setCurrentAccount(this.currentAccount);
        this.rightImage.setCurrentAccount(this.currentAccount);
        if (this.parentActivity != activity2 && activity2 != null) {
            this.parentActivity = activity2;
            this.actvityContext = new ContextThemeWrapper(this.parentActivity, NUM);
            this.touchSlop = ViewConfiguration.get(this.parentActivity).getScaledTouchSlop();
            if (progressDrawables == null) {
                Drawable drawable = ContextCompat.getDrawable(this.parentActivity, NUM);
                progressDrawables = new Drawable[]{drawable, ContextCompat.getDrawable(this.parentActivity, NUM), ContextCompat.getDrawable(this.parentActivity, NUM), new CombinedDrawable(drawable.mutate(), ContextCompat.getDrawable(this.parentActivity, NUM)), new CombinedDrawable(drawable.mutate(), ContextCompat.getDrawable(this.parentActivity, NUM))};
            }
            this.scroller = new Scroller(activity2);
            AnonymousClass8 r2 = new FrameLayout(activity2) {
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.isVisible && super.onInterceptTouchEvent(motionEvent);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.isVisible && PhotoViewer.this.onTouchEvent(motionEvent);
                }

                public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                    keyEvent.getKeyCode();
                    if (!PhotoViewer.this.muteVideo && PhotoViewer.this.isCurrentVideo && PhotoViewer.this.videoPlayer != null && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == 0 && (keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25)) {
                        PhotoViewer.this.videoPlayer.setVolume(1.0f);
                    }
                    return super.dispatchKeyEvent(keyEvent);
                }

                /* JADX WARNING: Code restructure failed: missing block: B:13:0x0020, code lost:
                    if (r0 != 6) goto L_0x0032;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public boolean dispatchTouchEvent(android.view.MotionEvent r3) {
                    /*
                        r2 = this;
                        org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                        boolean r0 = r0.videoPlayerControlVisible
                        if (r0 == 0) goto L_0x0032
                        org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                        boolean r0 = r0.isPlaying
                        if (r0 == 0) goto L_0x0032
                        int r0 = r3.getActionMasked()
                        if (r0 == 0) goto L_0x0029
                        r1 = 1
                        if (r0 == r1) goto L_0x0023
                        r1 = 3
                        if (r0 == r1) goto L_0x0023
                        r1 = 5
                        if (r0 == r1) goto L_0x0029
                        r1 = 6
                        if (r0 == r1) goto L_0x0023
                        goto L_0x0032
                    L_0x0023:
                        org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                        r0.scheduleActionBarHide()
                        goto L_0x0032
                    L_0x0029:
                        org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                        java.lang.Runnable r0 = r0.hideActionBarRunnable
                        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
                    L_0x0032:
                        boolean r3 = super.dispatchTouchEvent(r3)
                        return r3
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.AnonymousClass8.dispatchTouchEvent(android.view.MotionEvent):boolean");
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
                    int i3;
                    int stableInsetBottom;
                    int size = View.MeasureSpec.getSize(i);
                    int size2 = View.MeasureSpec.getSize(i2);
                    if (Build.VERSION.SDK_INT < 21 || PhotoViewer.this.lastInsets == null) {
                        int i4 = AndroidUtilities.displaySize.y;
                        if (size2 > i4) {
                            size2 = i4;
                        }
                    } else {
                        WindowInsets windowInsets = (WindowInsets) PhotoViewer.this.lastInsets;
                        if (AndroidUtilities.incorrectDisplaySizeFix) {
                            int i5 = AndroidUtilities.displaySize.y;
                            if (size2 > i5) {
                                size2 = i5;
                            }
                            size2 += AndroidUtilities.statusBarHeight;
                        } else if (windowInsets.getStableInsetBottom() >= 0 && (i3 = AndroidUtilities.statusBarHeight) >= 0 && (stableInsetBottom = (size2 - i3) - windowInsets.getStableInsetBottom()) > 0 && stableInsetBottom < 4096) {
                            AndroidUtilities.displaySize.y = stableInsetBottom;
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
                                    PhotoViewer.AnonymousClass8.this.lambda$onLayout$0$PhotoViewer$8();
                                }
                            });
                        }
                    }
                    if (PhotoViewer.this.dontResetZoomOnFirstLayout) {
                        PhotoViewer.this.setScaleToFill();
                        boolean unused5 = PhotoViewer.this.dontResetZoomOnFirstLayout = false;
                    }
                }

                public /* synthetic */ void lambda$onLayout$0$PhotoViewer$8() {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.checkImageView.getLayoutParams();
                    ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                    int i = 0;
                    layoutParams.topMargin = ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(34.0f)) / 2) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
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
            this.windowView = r2;
            r2.setBackgroundDrawable(this.backgroundDrawable);
            this.windowView.setClipChildren(true);
            this.windowView.setFocusable(false);
            ClippingImageView clippingImageView = new ClippingImageView(activity2);
            this.animatingImageView = clippingImageView;
            clippingImageView.setAnimationValues(this.animationValues);
            this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
            FrameLayoutDrawer frameLayoutDrawer = new FrameLayoutDrawer(activity2);
            this.containerView = frameLayoutDrawer;
            frameLayoutDrawer.setFocusable(false);
            this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
            if (Build.VERSION.SDK_INT >= 21) {
                this.containerView.setFitsSystemWindows(true);
                this.containerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                        return PhotoViewer.this.lambda$setParentActivity$2$PhotoViewer(view, windowInsets);
                    }
                });
                this.containerView.setSystemUiVisibility(1792);
            }
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.windowLayoutParams = layoutParams;
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
            PaintingOverlay paintingOverlay2 = new PaintingOverlay(this.parentActivity);
            this.paintingOverlay = paintingOverlay2;
            this.containerView.addView(paintingOverlay2, LayoutHelper.createFrame(-2, -2.0f));
            AnonymousClass9 r22 = new ActionBar(activity2) {
                public void setAlpha(float f) {
                    super.setAlpha(f);
                    PhotoViewer.this.containerView.invalidate();
                }
            };
            this.actionBar = r22;
            r22.setTitleColor(-1);
            this.actionBar.setSubtitleColor(-1);
            this.actionBar.setBackgroundColor(NUM);
            this.actionBar.setOccupyStatusBar(Build.VERSION.SDK_INT >= 21);
            this.actionBar.setItemsBackgroundColor(NUM, false);
            this.actionBar.setBackButtonImage(NUM);
            this.actionBar.setTitle(LocaleController.formatString("Of", NUM, 1, 1));
            this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                public void onItemClick(int i) {
                    TLObject tLObject;
                    int dialogId;
                    TLRPC$Chat tLRPC$Chat;
                    TLRPC$User tLRPC$User;
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
                                if (!(PhotoViewer.this.currentMessageObject.messageOwner.media instanceof TLRPC$TL_messageMediaWebPage) || PhotoViewer.this.currentMessageObject.messageOwner.media.webpage == null || PhotoViewer.this.currentMessageObject.messageOwner.media.webpage.document != null) {
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
                            Activity access$900 = PhotoViewer.this.parentActivity;
                            if (PhotoViewer.this.currentMessageObject == null || !PhotoViewer.this.currentMessageObject.isVideo()) {
                                i4 = 0;
                            }
                            MediaController.saveFile(file2, access$900, i4, (String) null, (String) null);
                            return;
                        }
                        PhotoViewer.this.parentActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    } else if (i3 == 2) {
                        if (PhotoViewer.this.currentDialogId != 0) {
                            boolean unused = PhotoViewer.this.disableShowCheck = true;
                            Bundle bundle = new Bundle();
                            bundle.putLong("dialog_id", PhotoViewer.this.currentDialogId);
                            MediaActivity mediaActivity = new MediaActivity(bundle, new int[]{-1, -1, -1, -1, -1, -1}, (SharedMediaLayout.SharedMediaData[]) null, PhotoViewer.this.sharedMediaType);
                            if (PhotoViewer.this.parentChatActivity != null) {
                                mediaActivity.setChatInfo(PhotoViewer.this.parentChatActivity.getCurrentChatInfo());
                            }
                            PhotoViewer.this.closePhoto(false, false);
                            ((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(mediaActivity, false, true);
                        }
                    } else if (i3 == 4) {
                        if (PhotoViewer.this.currentMessageObject != null) {
                            Bundle bundle2 = new Bundle();
                            int access$10400 = (int) PhotoViewer.this.currentDialogId;
                            int access$104002 = (int) (PhotoViewer.this.currentDialogId >> 32);
                            if (access$10400 == 0) {
                                bundle2.putInt("enc_id", access$104002);
                            } else if (access$10400 > 0) {
                                bundle2.putInt("user_id", access$10400);
                            } else if (access$10400 < 0) {
                                TLRPC$Chat chat = MessagesController.getInstance(PhotoViewer.this.currentAccount).getChat(Integer.valueOf(-access$10400));
                                if (!(chat == null || chat.migrated_to == null)) {
                                    bundle2.putInt("migrated_to", access$10400);
                                    access$10400 = -chat.migrated_to.channel_id;
                                }
                                bundle2.putInt("chat_id", -access$10400);
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
                                public final /* synthetic */ ArrayList f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                                    PhotoViewer.AnonymousClass10.this.lambda$onItemClick$0$PhotoViewer$10(this.f$1, dialogsActivity, arrayList, charSequence, z);
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
                                    tLRPC$User = MessagesController.getInstance(PhotoViewer.this.currentAccount).getUser(Integer.valueOf(dialogId));
                                    tLRPC$Chat = null;
                                } else {
                                    tLRPC$Chat = MessagesController.getInstance(PhotoViewer.this.currentAccount).getChat(Integer.valueOf(-dialogId));
                                    tLRPC$User = null;
                                }
                                if (tLRPC$User != null || !ChatObject.isChannel(tLRPC$Chat)) {
                                    int currentTime = ConnectionsManager.getInstance(PhotoViewer.this.currentAccount).getCurrentTime();
                                    if (tLRPC$User != null) {
                                        i2 = MessagesController.getInstance(PhotoViewer.this.currentAccount).revokeTimePmLimit;
                                    } else {
                                        i2 = MessagesController.getInstance(PhotoViewer.this.currentAccount).revokeTimeLimit;
                                    }
                                    if (!((tLRPC$User == null || tLRPC$User.id == UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId()) && tLRPC$Chat == null) && ((PhotoViewer.this.currentMessageObject.messageOwner.action == null || (PhotoViewer.this.currentMessageObject.messageOwner.action instanceof TLRPC$TL_messageActionEmpty)) && PhotoViewer.this.currentMessageObject.isOut() && currentTime - PhotoViewer.this.currentMessageObject.messageOwner.date <= i2)) {
                                        FrameLayout frameLayout = new FrameLayout(PhotoViewer.this.parentActivity);
                                        CheckBoxCell checkBoxCell = new CheckBoxCell(PhotoViewer.this.parentActivity, 1);
                                        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                        if (tLRPC$Chat != null) {
                                            checkBoxCell.setText(LocaleController.getString("DeleteForAll", NUM), "", false, false);
                                        } else {
                                            checkBoxCell.setText(LocaleController.formatString("DeleteForUser", NUM, UserObject.getFirstName(tLRPC$User)), "", false, false);
                                        }
                                        checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                                        frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                                        checkBoxCell.setOnClickListener(new View.OnClickListener(zArr) {
                                            public final /* synthetic */ boolean[] f$0;

                                            {
                                                this.f$0 = r1;
                                            }

                                            public final void onClick(View view) {
                                                PhotoViewer.AnonymousClass10.lambda$onItemClick$1(this.f$0, view);
                                            }
                                        });
                                        builder.setView(frameLayout);
                                    }
                                }
                            }
                            builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener(zArr) {
                                public final /* synthetic */ boolean[] f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    PhotoViewer.AnonymousClass10.this.lambda$onItemClick$2$PhotoViewer$10(this.f$1, dialogInterface, i);
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
                            if (AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity)) {
                                PhotoViewer.this.closePhoto(false, false);
                            } else {
                                PhotoViewer.this.showDownloadAlert();
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else if (i3 == 13 || i3 == 15) {
                        if (PhotoViewer.this.parentActivity != null && PhotoViewer.this.currentMessageObject != null) {
                            if (PhotoViewer.this.currentMessageObject.messageOwner.media instanceof TLRPC$TL_messageMediaPhoto) {
                                tLObject = PhotoViewer.this.currentMessageObject.messageOwner.media.photo;
                            } else if (PhotoViewer.this.currentMessageObject.messageOwner.media instanceof TLRPC$TL_messageMediaDocument) {
                                tLObject = PhotoViewer.this.currentMessageObject.messageOwner.media.document;
                            } else {
                                return;
                            }
                            StickersAlert unused3 = PhotoViewer.this.masksAlert = new StickersAlert(PhotoViewer.this.parentActivity, PhotoViewer.this.currentMessageObject, tLObject) {
                                public void dismiss() {
                                    super.dismiss();
                                    if (PhotoViewer.this.masksAlert == this) {
                                        StickersAlert unused = PhotoViewer.this.masksAlert = null;
                                    }
                                }
                            };
                            PhotoViewer.this.masksAlert.show();
                        }
                    } else if (i3 == 5) {
                        if (PhotoViewer.this.pipItem.getAlpha() == 1.0f) {
                            PhotoViewer.this.switchToPip(false);
                        }
                    } else if (i3 == 7) {
                        if (PhotoViewer.this.currentMessageObject != null) {
                            FileLoader.getInstance(PhotoViewer.this.currentAccount).cancelLoadFile(PhotoViewer.this.currentMessageObject.getDocument());
                            PhotoViewer.this.releasePlayer(false);
                            PhotoViewer.this.bottomLayout.setTag(1);
                            PhotoViewer.this.bottomLayout.setVisibility(0);
                        }
                    } else if (i3 == 14 && PhotoViewer.this.currentMessageObject != null) {
                        TLRPC$Document document = PhotoViewer.this.currentMessageObject.getDocument();
                        if (PhotoViewer.this.parentChatActivity == null || PhotoViewer.this.parentChatActivity.chatActivityEnterView == null) {
                            MediaDataController.getInstance(PhotoViewer.this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
                        } else {
                            PhotoViewer.this.parentChatActivity.chatActivityEnterView.addRecentGif(document);
                        }
                        MessagesController.getInstance(PhotoViewer.this.currentAccount).saveGif(PhotoViewer.this.currentMessageObject, document);
                    }
                }

                public /* synthetic */ void lambda$onItemClick$0$PhotoViewer$10(ArrayList arrayList, DialogsActivity dialogsActivity, ArrayList arrayList2, CharSequence charSequence, boolean z) {
                    ArrayList arrayList3 = arrayList2;
                    if (arrayList2.size() > 1 || ((Long) arrayList3.get(0)).longValue() == ((long) UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId()) || charSequence != null) {
                        ArrayList arrayList4 = arrayList;
                        for (int i = 0; i < arrayList2.size(); i++) {
                            long longValue = ((Long) arrayList3.get(i)).longValue();
                            if (charSequence != null) {
                                SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage(charSequence.toString(), longValue, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
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
                    if (r2.location.volume_id == org.telegram.ui.PhotoViewer.access$11800(r11.this$0).location.volume_id) goto L_0x017f;
                 */
                /* JADX WARNING: Removed duplicated region for block: B:45:0x0184  */
                /* JADX WARNING: Removed duplicated region for block: B:46:0x0198  */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public /* synthetic */ void lambda$onItemClick$2$PhotoViewer$10(boolean[] r12, android.content.DialogInterface r13, int r14) {
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
                        if (r1 == 0) goto L_0x02a8
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
                        goto L_0x02a8
                    L_0x00ca:
                        return
                    L_0x00cb:
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.avatarsArr
                        boolean r12 = r12.isEmpty()
                        r13 = -1
                        r1 = 1
                        if (r12 != 0) goto L_0x023c
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        int r12 = r12.currentIndex
                        if (r12 < 0) goto L_0x023b
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        int r12 = r12.currentIndex
                        org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r2 = r2.avatarsArr
                        int r2 = r2.size()
                        if (r12 < r2) goto L_0x00f5
                        goto L_0x023b
                    L_0x00f5:
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.avatarsArr
                        org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                        int r2 = r2.currentIndex
                        java.lang.Object r12 = r12.get(r2)
                        org.telegram.tgnet.TLRPC$Photo r12 = (org.telegram.tgnet.TLRPC$Photo) r12
                        org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r2 = r2.imagesArrLocations
                        org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this
                        int r3 = r3.currentIndex
                        java.lang.Object r2 = r2.get(r3)
                        org.telegram.messenger.ImageLocation r2 = (org.telegram.messenger.ImageLocation) r2
                        boolean r3 = r12 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
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
                        org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3
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
                        goto L_0x02a8
                    L_0x0198:
                        if (r12 == 0) goto L_0x02a8
                        org.telegram.tgnet.TLRPC$TL_inputPhoto r14 = new org.telegram.tgnet.TLRPC$TL_inputPhoto
                        r14.<init>()
                        long r2 = r12.id
                        r14.id = r2
                        long r2 = r12.access_hash
                        r14.access_hash = r2
                        byte[] r2 = r12.file_reference
                        r14.file_reference = r2
                        if (r2 != 0) goto L_0x01b1
                        byte[] r2 = new byte[r0]
                        r14.file_reference = r2
                    L_0x01b1:
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
                        if (r12 == 0) goto L_0x0213
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        r12.closePhoto(r0, r0)
                        goto L_0x02a8
                    L_0x0213:
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        int r12 = r12.currentIndex
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r14 = r14.avatarsArr
                        int r14 = r14.size()
                        if (r12 < r14) goto L_0x0230
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.avatarsArr
                        int r12 = r12.size()
                        int r12 = r12 - r1
                    L_0x0230:
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        int unused = r14.currentIndex = r13
                        org.telegram.ui.PhotoViewer r13 = org.telegram.ui.PhotoViewer.this
                        r13.setImageIndex(r12)
                        goto L_0x02a8
                    L_0x023b:
                        return
                    L_0x023c:
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.secureDocuments
                        boolean r12 = r12.isEmpty()
                        if (r12 != 0) goto L_0x02a8
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        org.telegram.ui.PhotoViewer$PhotoViewerProvider r12 = r12.placeProvider
                        if (r12 != 0) goto L_0x0251
                        return
                    L_0x0251:
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
                        if (r12 == 0) goto L_0x0281
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        r12.closePhoto(r0, r0)
                        goto L_0x02a8
                    L_0x0281:
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        int r12 = r12.currentIndex
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r14 = r14.secureDocuments
                        int r14 = r14.size()
                        if (r12 < r14) goto L_0x029e
                        org.telegram.ui.PhotoViewer r12 = org.telegram.ui.PhotoViewer.this
                        java.util.ArrayList r12 = r12.secureDocuments
                        int r12 = r12.size()
                        int r12 = r12 - r1
                    L_0x029e:
                        org.telegram.ui.PhotoViewer r14 = org.telegram.ui.PhotoViewer.this
                        int unused = r14.currentIndex = r13
                        org.telegram.ui.PhotoViewer r13 = org.telegram.ui.PhotoViewer.this
                        r13.setImageIndex(r12)
                    L_0x02a8:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.AnonymousClass10.lambda$onItemClick$2$PhotoViewer$10(boolean[], android.content.DialogInterface, int):void");
                }

                public boolean canOpenMenu() {
                    boolean z = true;
                    if (PhotoViewer.this.currentMessageObject != null) {
                        return true;
                    }
                    if (PhotoViewer.this.currentFileLocation == null) {
                        return false;
                    }
                    PhotoViewer photoViewer = PhotoViewer.this;
                    TLRPC$FileLocation access$11300 = photoViewer.getFileLocation(photoViewer.currentFileLocation);
                    if (PhotoViewer.this.avatarsDialogId == 0 && !PhotoViewer.this.isEvent) {
                        z = false;
                    }
                    return FileLoader.getPathToAttach(access$11300, z).exists();
                }
            });
            ActionBarMenu createMenu = this.actionBar.createMenu();
            this.masksItem = createMenu.addItem(13, NUM);
            this.pipItem = createMenu.addItem(5, NUM);
            this.sendItem = createMenu.addItem(3, NUM);
            ActionBarMenuItem addItem = createMenu.addItem(0, NUM);
            this.menuItem = addItem;
            addItem.addSubItem(11, NUM, LocaleController.getString("OpenInExternalApp", NUM)).setColors(-328966, -328966);
            this.menuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            ActionBarMenuSubItem addSubItem = this.menuItem.addSubItem(2, NUM, LocaleController.getString("ShowAllMedia", NUM));
            this.allMediaItem = addSubItem;
            addSubItem.setColors(-328966, -328966);
            this.menuItem.addSubItem(14, NUM, LocaleController.getString("SaveToGIFs", NUM)).setColors(-328966, -328966);
            this.menuItem.addSubItem(4, NUM, LocaleController.getString("ShowInChat", NUM)).setColors(-328966, -328966);
            this.menuItem.addSubItem(15, NUM, LocaleController.getString("ShowStickers", NUM)).setColors(-328966, -328966);
            this.menuItem.addSubItem(10, NUM, LocaleController.getString("ShareFile", NUM)).setColors(-328966, -328966);
            this.menuItem.addSubItem(1, NUM, LocaleController.getString("SaveToGallery", NUM)).setColors(-328966, -328966);
            this.menuItem.addSubItem(6, NUM, LocaleController.getString("Delete", NUM)).setColors(-328966, -328966);
            this.menuItem.addSubItem(7, NUM, LocaleController.getString("StopDownload", NUM)).setColors(-328966, -328966);
            this.menuItem.redrawPopup(-NUM);
            this.sendItem.setContentDescription(LocaleController.getString("Forward", NUM));
            this.menuItem.setSubMenuDelegate(new ActionBarMenuItem.ActionBarSubMenuItemDelegate() {
                public void onShowSubMenu() {
                    if (PhotoViewer.this.videoPlayerControlVisible && PhotoViewer.this.isPlaying) {
                        AndroidUtilities.cancelRunOnUIThread(PhotoViewer.this.hideActionBarRunnable);
                    }
                }

                public void onHideSubMenu() {
                    if (PhotoViewer.this.videoPlayerControlVisible && PhotoViewer.this.isPlaying) {
                        PhotoViewer.this.scheduleActionBarHide();
                    }
                }
            });
            FrameLayout frameLayout = new FrameLayout(this.actvityContext);
            this.bottomLayout = frameLayout;
            frameLayout.setBackgroundColor(NUM);
            this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.pressedDrawable[0] = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{NUM, 0});
            this.pressedDrawable[0].setShape(0);
            this.pressedDrawable[1] = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[]{NUM, 0});
            this.pressedDrawable[1].setShape(0);
            GroupedPhotosListView groupedPhotosListView2 = new GroupedPhotosListView(this.actvityContext, AndroidUtilities.dp(10.0f));
            this.groupedPhotosListView = groupedPhotosListView2;
            this.containerView.addView(groupedPhotosListView2, LayoutHelper.createFrame(-1, 68.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            this.groupedPhotosListView.setDelegate(new GroupedPhotosListView.GroupedPhotosListViewDelegate() {
                public ArrayList<TLRPC$PageBlock> getPageBlockArr() {
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
                    PhotoViewer.this.setImageIndex(i);
                }
            });
            CaptionLinkMovementMethod captionLinkMovementMethod = new CaptionLinkMovementMethod();
            CaptionTextViewSwitcher captionTextViewSwitcher2 = new CaptionTextViewSwitcher(this.containerView.getContext());
            this.captionTextViewSwitcher = captionTextViewSwitcher2;
            captionTextViewSwitcher2.setFactory(new ViewSwitcher.ViewFactory(captionLinkMovementMethod) {
                public final /* synthetic */ LinkMovementMethod f$1;

                {
                    this.f$1 = r2;
                }

                public final View makeView() {
                    return PhotoViewer.this.lambda$setParentActivity$3$PhotoViewer(this.f$1);
                }
            });
            this.captionTextViewSwitcher.setVisibility(4);
            setCaptionHwLayerEnabled(true);
            for (int i2 = 0; i2 < 3; i2++) {
                this.photoProgressViews[i2] = new PhotoProgressView(this.containerView) {
                    /* access modifiers changed from: protected */
                    public void onBackgroundStateUpdated(int i) {
                        if (this == PhotoViewer.this.photoProgressViews[0]) {
                            PhotoViewer.this.updateAccessibilityOverlayVisibility();
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void onVisibilityChanged(boolean z) {
                        if (this == PhotoViewer.this.photoProgressViews[0]) {
                            PhotoViewer.this.updateAccessibilityOverlayVisibility();
                        }
                    }
                };
                this.photoProgressViews[i2].setBackgroundState(0, false);
            }
            AnonymousClass14 r23 = new RadialProgressView(this.actvityContext) {
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
            this.miniProgressView = r23;
            r23.setUseSelfAlpha(true);
            this.miniProgressView.setProgressColor(-1);
            this.miniProgressView.setSize(AndroidUtilities.dp(54.0f));
            this.miniProgressView.setBackgroundResource(NUM);
            this.miniProgressView.setVisibility(4);
            this.miniProgressView.setAlpha(0.0f);
            this.containerView.addView(this.miniProgressView, LayoutHelper.createFrame(64, 64, 17));
            ImageView imageView = new ImageView(this.containerView.getContext());
            this.shareButton = imageView;
            imageView.setImageResource(NUM);
            this.shareButton.setScaleType(ImageView.ScaleType.CENTER);
            this.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.bottomLayout.addView(this.shareButton, LayoutHelper.createFrame(50, -1, 53));
            this.shareButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$4$PhotoViewer(view);
                }
            });
            this.shareButton.setContentDescription(LocaleController.getString("ShareFile", NUM));
            AnonymousClass15 r24 = new FadingTextViewLayout(this, this.containerView.getContext()) {
                /* access modifiers changed from: protected */
                public void onTextViewCreated(TextView textView) {
                    super.onTextViewCreated(textView);
                    textView.setTextSize(1, 14.0f);
                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                    textView.setTextColor(-1);
                    textView.setGravity(3);
                }
            };
            this.nameTextView = r24;
            this.bottomLayout.addView(r24, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 5.0f, 60.0f, 0.0f));
            AnonymousClass16 r25 = new FadingTextViewLayout(this, this.containerView.getContext(), true) {
                private LocaleController.LocaleInfo lastLocaleInfo = null;
                private int staticCharsCount = 0;

                /* access modifiers changed from: protected */
                public void onTextViewCreated(TextView textView) {
                    super.onTextViewCreated(textView);
                    textView.setTextSize(1, 13.0f);
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                    textView.setTextColor(-1);
                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    textView.setGravity(3);
                }

                /* access modifiers changed from: protected */
                public int getStaticCharsCount() {
                    LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
                    if (this.lastLocaleInfo != currentLocaleInfo) {
                        this.lastLocaleInfo = currentLocaleInfo;
                        this.staticCharsCount = LocaleController.formatString("formatDateAtTime", NUM, LocaleController.getInstance().formatterYear.format(new Date()), LocaleController.getInstance().formatterDay.format(new Date())).length();
                    }
                    return this.staticCharsCount;
                }

                public void setText(CharSequence charSequence, boolean z) {
                    int staticCharsCount2;
                    boolean z2 = false;
                    if (z) {
                        if (!LocaleController.isRTL || (staticCharsCount2 = getStaticCharsCount()) <= 0 || (charSequence.length() == staticCharsCount2 && getText() != null && getText().length() == staticCharsCount2)) {
                            z2 = true;
                        }
                        setText(charSequence, true, z2);
                        return;
                    }
                    setText(charSequence, false, false);
                }
            };
            this.dateTextView = r25;
            this.bottomLayout.addView(r25, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 25.0f, 50.0f, 0.0f));
            createVideoControlsInterface();
            RadialProgressView radialProgressView = new RadialProgressView(this.parentActivity);
            this.progressView = radialProgressView;
            radialProgressView.setProgressColor(-1);
            this.progressView.setBackgroundResource(NUM);
            this.progressView.setVisibility(4);
            this.containerView.addView(this.progressView, LayoutHelper.createFrame(54, 54, 17));
            PickerBottomLayoutViewer pickerBottomLayoutViewer = new PickerBottomLayoutViewer(this.parentActivity);
            this.qualityPicker = pickerBottomLayoutViewer;
            pickerBottomLayoutViewer.setBackgroundColor(NUM);
            this.qualityPicker.updateSelectedCount(0, false);
            this.qualityPicker.setTranslationY((float) AndroidUtilities.dp(120.0f));
            this.qualityPicker.doneButton.setText(LocaleController.getString("Done", NUM).toUpperCase());
            this.qualityPicker.doneButton.setTextColor(Theme.getColor("dialogFloatingButton"));
            this.containerView.addView(this.qualityPicker, LayoutHelper.createFrame(-1, 48, 83));
            this.qualityPicker.cancelButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$5$PhotoViewer(view);
                }
            });
            this.qualityPicker.doneButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$6$PhotoViewer(view);
                }
            });
            VideoForwardDrawable videoForwardDrawable2 = new VideoForwardDrawable();
            this.videoForwardDrawable = videoForwardDrawable2;
            videoForwardDrawable2.setDelegate(new VideoForwardDrawable.VideoForwardDrawableDelegate() {
                public void onAnimationEnd() {
                }

                public void invalidate() {
                    PhotoViewer.this.containerView.invalidate();
                }
            });
            QualityChooseView qualityChooseView2 = new QualityChooseView(this.parentActivity);
            this.qualityChooseView = qualityChooseView2;
            qualityChooseView2.setTranslationY((float) AndroidUtilities.dp(120.0f));
            this.qualityChooseView.setVisibility(4);
            this.qualityChooseView.setBackgroundColor(NUM);
            this.containerView.addView(this.qualityChooseView, LayoutHelper.createFrame(-1, 70.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            AnonymousClass18 r26 = new FrameLayout(this.actvityContext) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(motionEvent);
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(motionEvent);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
                }

                public void setTranslationY(float f) {
                    super.setTranslationY(f);
                    if (PhotoViewer.this.videoTimelineView != null && PhotoViewer.this.videoTimelineView.getVisibility() != 8) {
                        PhotoViewer.this.videoTimelineView.setTranslationY(f);
                    }
                }

                public void setAlpha(float f) {
                    super.setAlpha(f);
                    if (PhotoViewer.this.videoTimelineView != null && PhotoViewer.this.videoTimelineView.getVisibility() != 8) {
                        PhotoViewer.this.videoTimelineView.setAlpha(f);
                    }
                }

                public void setVisibility(int i) {
                    super.setVisibility(i);
                    if (PhotoViewer.this.videoTimelineView != null && PhotoViewer.this.videoTimelineView.getVisibility() != 8) {
                        PhotoViewer.this.videoTimelineView.setVisibility(i == 0 ? 0 : 4);
                    }
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    if (PhotoViewer.this.itemsLayout.getVisibility() != 8) {
                        int dp = (((i3 - i) - AndroidUtilities.dp(70.0f)) - PhotoViewer.this.itemsLayout.getMeasuredWidth()) / 2;
                        PhotoViewer.this.itemsLayout.layout(dp, PhotoViewer.this.itemsLayout.getTop(), PhotoViewer.this.itemsLayout.getMeasuredWidth() + dp, PhotoViewer.this.itemsLayout.getTop() + PhotoViewer.this.itemsLayout.getMeasuredHeight());
                    }
                }
            };
            this.pickerView = r26;
            r26.setBackgroundColor(NUM);
            this.containerView.addView(this.pickerView, LayoutHelper.createFrame(-1, -2, 83));
            TextView textView = new TextView(this.containerView.getContext());
            this.docNameTextView = textView;
            textView.setTextSize(1, 15.0f);
            this.docNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.docNameTextView.setSingleLine(true);
            this.docNameTextView.setMaxLines(1);
            this.docNameTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.docNameTextView.setTextColor(-1);
            this.docNameTextView.setGravity(3);
            this.pickerView.addView(this.docNameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 20.0f, 23.0f, 84.0f, 0.0f));
            TextView textView2 = new TextView(this.containerView.getContext());
            this.docInfoTextView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.docInfoTextView.setSingleLine(true);
            this.docInfoTextView.setMaxLines(1);
            this.docInfoTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.docInfoTextView.setTextColor(-1);
            this.docInfoTextView.setGravity(3);
            this.pickerView.addView(this.docInfoTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 20.0f, 46.0f, 84.0f, 0.0f));
            VideoTimelinePlayView videoTimelinePlayView = new VideoTimelinePlayView(this.parentActivity);
            this.videoTimelineView = videoTimelinePlayView;
            videoTimelinePlayView.setDelegate(new VideoTimelinePlayView.VideoTimelineViewDelegate() {
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
            this.videoTimelineView.setVisibility(8);
            this.videoTimelineView.setBackgroundColor(NUM);
            this.containerView.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, 58.0f, 83, 0.0f, 8.0f, 0.0f, 0.0f));
            AnonymousClass20 r27 = new ImageView(this.parentActivity) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(motionEvent);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
                }
            };
            this.pickerViewSendButton = r27;
            r27.setScaleType(ImageView.ScaleType.CENTER);
            int dp = AndroidUtilities.dp(56.0f);
            int color = Theme.getColor("dialogFloatingButton");
            if (Build.VERSION.SDK_INT >= 21) {
                str = "dialogFloatingButtonPressed";
            } else {
                str = "dialogFloatingButton";
            }
            this.pickerViewSendButton.setBackgroundDrawable(Theme.createSimpleSelectorCircleDrawable(dp, color, Theme.getColor(str)));
            this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
            this.pickerViewSendButton.setImageResource(NUM);
            this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
            this.containerView.addView(this.pickerViewSendButton, LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 14.0f, 14.0f));
            this.pickerViewSendButton.setContentDescription(LocaleController.getString("Send", NUM));
            this.pickerViewSendButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$7$PhotoViewer(view);
                }
            });
            this.pickerViewSendButton.setOnLongClickListener(new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return PhotoViewer.this.lambda$setParentActivity$11$PhotoViewer(view);
                }
            });
            AnonymousClass21 r28 = new LinearLayout(this.parentActivity) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int childCount = getChildCount();
                    int i3 = 0;
                    for (int i4 = 0; i4 < childCount; i4++) {
                        if (getChildAt(i4).getVisibility() == 0) {
                            i3++;
                        }
                    }
                    int size = View.MeasureSpec.getSize(i);
                    int size2 = View.MeasureSpec.getSize(i2);
                    if (i3 != 0) {
                        int min = Math.min(AndroidUtilities.dp(70.0f), size / i3);
                        if (PhotoViewer.this.compressItem.getVisibility() == 0) {
                            int i5 = 64;
                            if (PhotoViewer.this.selectedCompression < 2) {
                                i5 = 48;
                            } else if (PhotoViewer.this.selectedCompression != 2) {
                                int access$12900 = PhotoViewer.this.selectedCompression;
                            }
                            int max = Math.max(0, (min - AndroidUtilities.dp((float) i5)) / 2);
                            PhotoViewer.this.compressItem.setPadding(max, 0, max, 0);
                        }
                        for (int i6 = 0; i6 < childCount; i6++) {
                            View childAt = getChildAt(i6);
                            if (childAt.getVisibility() != 8) {
                                childAt.measure(View.MeasureSpec.makeMeasureSpec(min, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
                            }
                        }
                        setMeasuredDimension(min * i3, size2);
                        return;
                    }
                    setMeasuredDimension(size, size2);
                }
            };
            this.itemsLayout = r28;
            r28.setOrientation(0);
            this.pickerView.addView(this.itemsLayout, LayoutHelper.createFrame(-2, 48.0f, 81, 0.0f, 0.0f, 70.0f, 0.0f));
            ImageView imageView2 = new ImageView(this.parentActivity);
            this.cropItem = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.cropItem.setImageResource(NUM);
            this.cropItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.itemsLayout.addView(this.cropItem, LayoutHelper.createLinear(48, 48));
            this.cropItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$12$PhotoViewer(view);
                }
            });
            this.cropItem.setContentDescription(LocaleController.getString("CropImage", NUM));
            ImageView imageView3 = new ImageView(this.parentActivity);
            this.rotateItem = imageView3;
            imageView3.setScaleType(ImageView.ScaleType.CENTER);
            this.rotateItem.setImageResource(NUM);
            this.rotateItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.itemsLayout.addView(this.rotateItem, LayoutHelper.createLinear(48, 48));
            this.rotateItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$13$PhotoViewer(view);
                }
            });
            this.rotateItem.setContentDescription(LocaleController.getString("AccDescrRotate", NUM));
            ImageView imageView4 = new ImageView(this.parentActivity);
            this.paintItem = imageView4;
            imageView4.setScaleType(ImageView.ScaleType.CENTER);
            this.paintItem.setImageResource(NUM);
            this.paintItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.itemsLayout.addView(this.paintItem, LayoutHelper.createLinear(48, 48));
            this.paintItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$14$PhotoViewer(view);
                }
            });
            this.paintItem.setContentDescription(LocaleController.getString("AccDescrPhotoEditor", NUM));
            ImageView imageView5 = new ImageView(this.parentActivity);
            this.muteItem = imageView5;
            imageView5.setScaleType(ImageView.ScaleType.CENTER);
            this.muteItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.containerView.addView(this.muteItem, LayoutHelper.createFrame(48, 48.0f, 83, 16.0f, 0.0f, 0.0f, 0.0f));
            this.muteItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$15$PhotoViewer(view);
                }
            });
            ImageView imageView6 = new ImageView(this.parentActivity);
            this.cameraItem = imageView6;
            imageView6.setScaleType(ImageView.ScaleType.CENTER);
            this.cameraItem.setImageResource(NUM);
            this.cameraItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.cameraItem.setContentDescription(LocaleController.getString("AccDescrTakeMorePics", NUM));
            this.containerView.addView(this.cameraItem, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 16.0f, 0.0f));
            this.cameraItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$16$PhotoViewer(view);
                }
            });
            ImageView imageView7 = new ImageView(this.parentActivity);
            this.tuneItem = imageView7;
            imageView7.setScaleType(ImageView.ScaleType.CENTER);
            this.tuneItem.setImageResource(NUM);
            this.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.itemsLayout.addView(this.tuneItem, LayoutHelper.createLinear(48, 48));
            this.tuneItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$17$PhotoViewer(view);
                }
            });
            this.tuneItem.setContentDescription(LocaleController.getString("AccDescrPhotoAdjust", NUM));
            ImageView imageView8 = new ImageView(this.parentActivity);
            this.compressItem = imageView8;
            imageView8.setTag(1);
            this.compressItem.setScaleType(ImageView.ScaleType.CENTER);
            this.compressItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            int selectCompression = selectCompression();
            this.selectedCompression = selectCompression;
            if (selectCompression <= 1) {
                this.compressItem.setImageResource(NUM);
            } else if (selectCompression == 2) {
                this.compressItem.setImageResource(NUM);
            } else {
                this.selectedCompression = this.compressionsCount - 1;
                this.compressItem.setImageResource(NUM);
            }
            this.compressItem.setContentDescription(LocaleController.getString("AccDescrVideoQuality", NUM));
            this.itemsLayout.addView(this.compressItem, LayoutHelper.createLinear(48, 48));
            this.compressItem.setOnClickListener(new View.OnClickListener(activity2) {
                public final /* synthetic */ Activity f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$18$PhotoViewer(this.f$1, view);
                }
            });
            ImageView imageView9 = new ImageView(this.parentActivity);
            this.timeItem = imageView9;
            imageView9.setScaleType(ImageView.ScaleType.CENTER);
            this.timeItem.setImageResource(NUM);
            this.timeItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.timeItem.setContentDescription(LocaleController.getString("SetTimer", NUM));
            this.itemsLayout.addView(this.timeItem, LayoutHelper.createLinear(48, 48));
            this.timeItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$24$PhotoViewer(view);
                }
            });
            PickerBottomLayoutViewer pickerBottomLayoutViewer2 = new PickerBottomLayoutViewer(this.actvityContext);
            this.editorDoneLayout = pickerBottomLayoutViewer2;
            pickerBottomLayoutViewer2.setBackgroundColor(-NUM);
            this.editorDoneLayout.updateSelectedCount(0, false);
            this.editorDoneLayout.setVisibility(8);
            this.containerView.addView(this.editorDoneLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.editorDoneLayout.cancelButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$25$PhotoViewer(view);
                }
            });
            this.editorDoneLayout.doneButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$26$PhotoViewer(view);
                }
            });
            TextView textView3 = new TextView(this.actvityContext);
            this.resetButton = textView3;
            textView3.setVisibility(8);
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
                    PhotoViewer.this.lambda$setParentActivity$27$PhotoViewer(view);
                }
            });
            GestureDetector2 gestureDetector2 = new GestureDetector2(this.containerView.getContext(), this);
            this.gestureDetector = gestureDetector2;
            gestureDetector2.setIsLongpressEnabled(false);
            setDoubleTapEnabled(true);
            $$Lambda$PhotoViewer$XTiltsq63TAspdbDBJQgianJQ r1 = new ImageReceiver.ImageReceiverDelegate() {
                public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                    PhotoViewer.this.lambda$setParentActivity$28$PhotoViewer(imageReceiver, z, z2, z3);
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
            AnonymousClass23 r29 = new CheckBox(this.containerView.getContext(), NUM) {
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
                }
            };
            this.checkImageView = r29;
            r29.setDrawBackground(true);
            this.checkImageView.setHasBorder(true);
            this.checkImageView.setSize(34);
            this.checkImageView.setCheckOffset(AndroidUtilities.dp(1.0f));
            this.checkImageView.setColor(Theme.getColor("dialogFloatingButton"), -1);
            this.checkImageView.setVisibility(8);
            this.containerView.addView(this.checkImageView, LayoutHelper.createFrame(34, 34.0f, 53, 0.0f, (rotation == 3 || rotation == 1) ? 61.0f : 71.0f, 11.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                ((FrameLayout.LayoutParams) this.checkImageView.getLayoutParams()).topMargin += AndroidUtilities.statusBarHeight;
            }
            this.checkImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$29$PhotoViewer(view);
                }
            });
            CounterView counterView = new CounterView(this.parentActivity);
            this.photosCounterView = counterView;
            this.containerView.addView(counterView, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, (rotation == 3 || rotation == 1) ? 58.0f : 68.0f, 64.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                ((FrameLayout.LayoutParams) this.photosCounterView.getLayoutParams()).topMargin += AndroidUtilities.statusBarHeight;
            }
            this.photosCounterView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$30$PhotoViewer(view);
                }
            });
            SelectedPhotosListView selectedPhotosListView2 = new SelectedPhotosListView(this.parentActivity);
            this.selectedPhotosListView = selectedPhotosListView2;
            selectedPhotosListView2.setVisibility(8);
            this.selectedPhotosListView.setAlpha(0.0f);
            this.selectedPhotosListView.setLayoutManager(new LinearLayoutManager(this, this.parentActivity, 0, true) {
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                    AnonymousClass1 r2 = new LinearSmoothScrollerEnd(this, recyclerView.getContext()) {
                        /* access modifiers changed from: protected */
                        public int calculateTimeForDeceleration(int i) {
                            return Math.max(180, super.calculateTimeForDeceleration(i));
                        }
                    };
                    r2.setTargetPosition(i);
                    startSmoothScroll(r2);
                }
            });
            SelectedPhotosListView selectedPhotosListView3 = this.selectedPhotosListView;
            ListAdapter listAdapter = new ListAdapter(this.parentActivity);
            this.selectedPhotosAdapter = listAdapter;
            selectedPhotosListView3.setAdapter(listAdapter);
            this.containerView.addView(this.selectedPhotosListView, LayoutHelper.createFrame(-1, 103, 51));
            this.selectedPhotosListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    PhotoViewer.this.lambda$setParentActivity$31$PhotoViewer(view, i);
                }
            });
            AnonymousClass25 r12 = new PhotoViewerCaptionEnterView(this.actvityContext, this.containerView, this.windowView) {
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
            this.captionEditText = r12;
            r12.setDelegate(new PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate() {
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
            AnonymousClass27 r13 = new RecyclerListView(this.actvityContext) {
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
            this.mentionListView = r13;
            r13.setTag(5);
            AnonymousClass28 r14 = new LinearLayoutManager(this, this.actvityContext) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            this.mentionLayoutManager = r14;
            r14.setOrientation(1);
            this.mentionListView.setLayoutManager(this.mentionLayoutManager);
            this.mentionListView.setBackgroundColor(NUM);
            this.mentionListView.setVisibility(8);
            this.mentionListView.setClipToPadding(true);
            this.mentionListView.setOverScrollMode(2);
            this.containerView.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
            RecyclerListView recyclerListView = this.mentionListView;
            MentionsAdapter mentionsAdapter2 = new MentionsAdapter(this.actvityContext, true, 0, new MentionsAdapter.MentionsAdapterDelegate() {
                public void onContextClick(TLRPC$BotInlineResult tLRPC$BotInlineResult) {
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
            recyclerListView.setAdapter(mentionsAdapter2);
            this.mentionListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    PhotoViewer.this.lambda$setParentActivity$32$PhotoViewer(view, i);
                }
            });
            this.mentionListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
                public final boolean onItemClick(View view, int i) {
                    return PhotoViewer.this.lambda$setParentActivity$34$PhotoViewer(view, i);
                }
            });
            if (((AccessibilityManager) this.actvityContext.getSystemService("accessibility")).isEnabled()) {
                View view = new View(this.actvityContext);
                this.playButtonAccessibilityOverlay = view;
                view.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
                this.playButtonAccessibilityOverlay.setFocusable(true);
                this.containerView.addView(this.playButtonAccessibilityOverlay, LayoutHelper.createFrame(64, 64, 17));
            }
        }
    }

    public /* synthetic */ WindowInsets lambda$setParentActivity$2$PhotoViewer(View view, WindowInsets windowInsets) {
        if (AndroidUtilities.statusBarHeight != windowInsets.getSystemWindowInsetTop()) {
            AndroidUtilities.statusBarHeight = windowInsets.getSystemWindowInsetTop();
            ((LaunchActivity) this.parentActivity).drawerLayoutContainer.requestLayout();
        }
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
        if (this.actionBar != null) {
            AndroidUtilities.cancelRunOnUIThread(this.updateContainerFlagsRunnable);
            if (this.isVisible && this.animationInProgress == 0) {
                AndroidUtilities.runOnUIThread(this.updateContainerFlagsRunnable, 200);
            }
        }
        return windowInsets.consumeSystemWindowInsets();
    }

    public /* synthetic */ void lambda$setParentActivity$4$PhotoViewer(View view) {
        onSharePressed();
    }

    public /* synthetic */ void lambda$setParentActivity$5$PhotoViewer(View view) {
        this.selectedCompression = this.previousCompression;
        didChangedCompressionLevel(false);
        showQualityView(false);
        requestVideoPreview(2);
    }

    public /* synthetic */ void lambda$setParentActivity$6$PhotoViewer(View view) {
        showQualityView(false);
        requestVideoPreview(2);
    }

    public /* synthetic */ void lambda$setParentActivity$7$PhotoViewer(View view) {
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

    public /* synthetic */ boolean lambda$setParentActivity$11$PhotoViewer(View view) {
        PhotoViewerProvider photoViewerProvider;
        boolean z;
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity != null && !chatActivity.isInScheduleMode()) {
            this.parentChatActivity.getCurrentChat();
            TLRPC$User currentUser = this.parentChatActivity.getCurrentUser();
            if (this.parentChatActivity.getCurrentEncryptedChat() != null) {
                return false;
            }
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity);
            this.sendPopupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setAnimationEnabled(false);
            this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return PhotoViewer.this.lambda$null$8$PhotoViewer(view, motionEvent);
                }
            });
            this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    PhotoViewer.this.lambda$null$9$PhotoViewer(keyEvent);
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
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        PhotoViewer.this.lambda$null$10$PhotoViewer(this.f$1, view);
                    }
                });
                i++;
            }
            ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
            this.sendPopupWindow = actionBarPopupWindow;
            actionBarPopupWindow.setAnimationEnabled(false);
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

    public /* synthetic */ boolean lambda$null$8$PhotoViewer(View view, MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (motionEvent.getActionMasked() != 0 || (actionBarPopupWindow = this.sendPopupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        view.getHitRect(this.hitRect);
        if (this.hitRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            return false;
        }
        this.sendPopupWindow.dismiss();
        return false;
    }

    public /* synthetic */ void lambda$null$9$PhotoViewer(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$null$10$PhotoViewer(int i, View view) {
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

    public /* synthetic */ void lambda$setParentActivity$12$PhotoViewer(View view) {
        if (this.captionEditText.getTag() == null) {
            if (this.isCurrentVideo) {
                if (this.videoConvertSupported) {
                    TextureView textureView = this.videoTextureView;
                    if (textureView instanceof VideoEditTextureView) {
                        VideoEditTextureView videoEditTextureView = (VideoEditTextureView) textureView;
                        if (videoEditTextureView.getVideoWidth() <= 0 || videoEditTextureView.getVideoHeight() <= 0) {
                            return;
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
            switchToEditMode(1);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$13$PhotoViewer(View view) {
        PhotoCropView photoCropView2 = this.photoCropView;
        if (photoCropView2 != null) {
            photoCropView2.rotate();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$14$PhotoViewer(View view) {
        if (this.captionEditText.getTag() == null) {
            if (this.isCurrentVideo) {
                if (this.videoConvertSupported) {
                    TextureView textureView = this.videoTextureView;
                    if (textureView instanceof VideoEditTextureView) {
                        VideoEditTextureView videoEditTextureView = (VideoEditTextureView) textureView;
                        if (videoEditTextureView.getVideoWidth() <= 0 || videoEditTextureView.getVideoHeight() <= 0) {
                            return;
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
            switchToEditMode(3);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$15$PhotoViewer(View view) {
        if (this.captionEditText.getTag() == null) {
            this.muteVideo = !this.muteVideo;
            updateMuteButton();
            updateVideoInfo();
            if (!this.muteVideo || this.checkImageView.isChecked()) {
                Object obj = this.imagesArrLocals.get(this.currentIndex);
                if (obj instanceof MediaController.MediaEditState) {
                    ((MediaController.MediaEditState) obj).editedInfo = getCurrentVideoEditedInfo();
                    return;
                }
                return;
            }
            this.checkImageView.callOnClick();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$16$PhotoViewer(View view) {
        if (this.placeProvider != null && this.captionEditText.getTag() == null) {
            this.placeProvider.needAddMorePhotos();
            closePhoto(true, false);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$17$PhotoViewer(View view) {
        if (this.captionEditText.getTag() == null) {
            if (this.isCurrentVideo) {
                if (this.videoConvertSupported) {
                    TextureView textureView = this.videoTextureView;
                    if (textureView instanceof VideoEditTextureView) {
                        VideoEditTextureView videoEditTextureView = (VideoEditTextureView) textureView;
                        if (videoEditTextureView.getVideoWidth() <= 0 || videoEditTextureView.getVideoHeight() <= 0) {
                            return;
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
            switchToEditMode(2);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$18$PhotoViewer(Activity activity, View view) {
        if (this.captionEditText.getTag() == null && !this.muteVideo) {
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

    public /* synthetic */ void lambda$setParentActivity$24$PhotoViewer(View view) {
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
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setTextSize(1, 20.0f);
            textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(4.0f));
            textView.setGravity(16);
            linearLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f));
            textView.setOnTouchListener($$Lambda$PhotoViewer$OSzayuiqRWAhvG8jnXH73llAbu0.INSTANCE);
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
            textView2.setOnTouchListener($$Lambda$PhotoViewer$Shr6nLjFdlzxQwiVMSNAujGIq8I.INSTANCE);
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
            numberPicker.setFormatter($$Lambda$PhotoViewer$7_vQZhIP8Mu2pI7PZ2U6_UULGHY.INSTANCE);
            linearLayout.addView(numberPicker, LayoutHelper.createLinear(-1, -2));
            AnonymousClass22 r9 = new FrameLayout(this, this.parentActivity) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int childCount = getChildCount();
                    int i5 = i3 - i;
                    for (int i6 = 0; i6 < childCount; i6++) {
                        View childAt = getChildAt(i6);
                        if (((Integer) childAt.getTag()).intValue() == -1) {
                            childAt.layout((i5 - getPaddingRight()) - childAt.getMeasuredWidth(), getPaddingTop(), (i5 - getPaddingRight()) + childAt.getMeasuredWidth(), getPaddingTop() + childAt.getMeasuredHeight());
                        } else if (((Integer) childAt.getTag()).intValue() == -2) {
                            int paddingLeft = getPaddingLeft();
                            childAt.layout(paddingLeft, getPaddingTop(), childAt.getMeasuredWidth() + paddingLeft, getPaddingTop() + childAt.getMeasuredHeight());
                        } else {
                            childAt.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + childAt.getMeasuredWidth(), getPaddingTop() + childAt.getMeasuredHeight());
                        }
                    }
                }
            };
            r9.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            linearLayout.addView(r9, LayoutHelper.createLinear(-1, 52));
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
            r9.addView(textView3, LayoutHelper.createFrame(-2, 36, 53));
            textView3.setOnClickListener(new View.OnClickListener(numberPicker, create) {
                public final /* synthetic */ NumberPicker f$1;
                public final /* synthetic */ BottomSheet f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    PhotoViewer.this.lambda$null$22$PhotoViewer(this.f$1, this.f$2, view);
                }
            });
            TextView textView4 = new TextView(this.parentActivity);
            textView4.setMinWidth(AndroidUtilities.dp(64.0f));
            textView4.setTag(-2);
            textView4.setTextSize(1, 14.0f);
            textView4.setTextColor(-1);
            textView4.setGravity(17);
            textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView4.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
            textView4.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(-1));
            textView4.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            r9.addView(textView4, LayoutHelper.createFrame(-2, 36, 53));
            textView4.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    BottomSheet.this.dismiss();
                }
            });
            create.show();
            create.setBackgroundColor(-16777216);
        }
    }

    static /* synthetic */ String lambda$null$21(int i) {
        if (i == 0) {
            return LocaleController.getString("ShortMessageLifetimeForever", NUM);
        }
        if (i < 1 || i >= 21) {
            return LocaleController.formatTTLString((i - 16) * 5);
        }
        return LocaleController.formatTTLString(i);
    }

    public /* synthetic */ void lambda$null$22$PhotoViewer(NumberPicker numberPicker, BottomSheet bottomSheet, View view) {
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

    public /* synthetic */ void lambda$setParentActivity$25$PhotoViewer(View view) {
        if (this.isCurrentVideo) {
            ((VideoEditTextureView) this.videoTextureView).setViewTransform(this.previousHasTransform, this.previousCropPx, this.previousCropPy, this.previousCropRotation, this.previousCropOrientation, this.previousCropScale, this.previousCropPw, this.previousCropPh, 0.0f, 0.0f);
        }
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$setParentActivity$26$PhotoViewer(View view) {
        if (this.currentEditMode != 1 || this.photoCropView.isReady()) {
            applyCurrentEditMode();
            switchToEditMode(0);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$27$PhotoViewer(View view) {
        this.photoCropView.reset();
    }

    public /* synthetic */ void lambda$setParentActivity$28$PhotoViewer(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        PhotoViewerProvider photoViewerProvider;
        Bitmap bitmap;
        if (imageReceiver == this.centerImage && z && !z2) {
            if (!this.isCurrentVideo && !((this.currentEditMode != 1 && this.sendPhotoType != 1) || this.photoCropView == null || (bitmap = imageReceiver.getBitmap()) == null)) {
                this.photoCropView.setBitmap(bitmap, imageReceiver.getOrientation(), this.sendPhotoType != 1, true, this.paintingOverlay, (VideoEditTextureView) null, (MediaController.CropState) null);
            }
            if (this.paintingOverlay.getVisibility() == 0) {
                this.containerView.requestLayout();
            }
        }
        if (imageReceiver == this.centerImage && z && (photoViewerProvider = this.placeProvider) != null && photoViewerProvider.scaleToFill() && !this.ignoreDidSetImage) {
            if (!this.wasLayout) {
                this.dontResetZoomOnFirstLayout = true;
            } else {
                setScaleToFill();
            }
        }
    }

    public /* synthetic */ void lambda$setParentActivity$29$PhotoViewer(View view) {
        if (this.captionEditText.getTag() == null) {
            setPhotoChecked();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$30$PhotoViewer(View view) {
        PhotoViewerProvider photoViewerProvider;
        if (this.captionEditText.getTag() == null && (photoViewerProvider = this.placeProvider) != null && photoViewerProvider.getSelectedPhotosOrder() != null && !this.placeProvider.getSelectedPhotosOrder().isEmpty()) {
            togglePhotosListView(!this.isPhotosListViewVisible, true);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$31$PhotoViewer(View view, int i) {
        int i2;
        if (!this.imagesArrLocals.isEmpty() && (i2 = this.currentIndex) >= 0 && i2 < this.imagesArrLocals.size()) {
            Object obj = this.imagesArrLocals.get(this.currentIndex);
            if (obj instanceof MediaController.MediaEditState) {
                ((MediaController.MediaEditState) obj).editedInfo = getCurrentVideoEditedInfo();
            }
        }
        this.ignoreDidSetImage = true;
        int indexOf = this.imagesArrLocals.indexOf(view.getTag());
        if (indexOf >= 0) {
            this.currentIndex = -1;
            setImageIndex(indexOf);
        }
        this.ignoreDidSetImage = false;
    }

    public /* synthetic */ void lambda$setParentActivity$32$PhotoViewer(View view, int i) {
        Object item = this.mentionsAdapter.getItem(i);
        int resultStartPosition = this.mentionsAdapter.getResultStartPosition();
        int resultLength = this.mentionsAdapter.getResultLength();
        if (item instanceof TLRPC$User) {
            TLRPC$User tLRPC$User = (TLRPC$User) item;
            if (tLRPC$User.username != null) {
                PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
                photoViewerCaptionEnterView.replaceWithText(resultStartPosition, resultLength, "@" + tLRPC$User.username + " ", false);
                return;
            }
            String firstName = UserObject.getFirstName(tLRPC$User);
            SpannableString spannableString = new SpannableString(firstName + " ");
            spannableString.setSpan(new URLSpanUserMentionPhotoViewer("" + tLRPC$User.id, true), 0, spannableString.length(), 33);
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

    public /* synthetic */ boolean lambda$setParentActivity$34$PhotoViewer(View view, int i) {
        if (!(this.mentionsAdapter.getItem(i) instanceof String)) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("ClearSearch", NUM));
        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PhotoViewer.this.lambda$null$33$PhotoViewer(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showAlertDialog(builder);
        return true;
    }

    public /* synthetic */ void lambda$null$33$PhotoViewer(DialogInterface dialogInterface, int i) {
        this.mentionsAdapter.clearRecentHashtags();
    }

    /* access modifiers changed from: private */
    public void sendPressed(boolean z, int i) {
        int i2;
        if (this.captionEditText.getTag() == null) {
            if (this.sendPhotoType == 1) {
                applyCurrentEditMode();
            }
            if (this.placeProvider != null && !this.doneButtonPressed) {
                ChatActivity chatActivity = this.parentChatActivity;
                if (chatActivity != null) {
                    TLRPC$Chat currentChat = chatActivity.getCurrentChat();
                    if (this.parentChatActivity.getCurrentUser() != null || ((ChatObject.isChannel(currentChat) && currentChat.megagroup) || !ChatObject.isChannel(currentChat))) {
                        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                        edit.putBoolean("silent_" + this.parentChatActivity.getDialogId(), !z).commit();
                    }
                }
                VideoEditedInfo currentVideoEditedInfo = getCurrentVideoEditedInfo();
                if (!this.imagesArrLocals.isEmpty() && (i2 = this.currentIndex) >= 0 && i2 < this.imagesArrLocals.size()) {
                    Object obj = this.imagesArrLocals.get(this.currentIndex);
                    if (obj instanceof MediaController.MediaEditState) {
                        ((MediaController.MediaEditState) obj).editedInfo = currentVideoEditedInfo;
                    }
                }
                this.placeProvider.sendButtonPressed(this.currentIndex, currentVideoEditedInfo, z, i);
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
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", NUM));
        builder.setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PhotoViewer.this.lambda$checkInlinePermissions$35$PhotoViewer(dialogInterface, i);
            }
        });
        builder.show();
        return false;
    }

    public /* synthetic */ void lambda$checkInlinePermissions$35$PhotoViewer(DialogInterface dialogInterface, int i) {
        Activity activity = this.parentActivity;
        if (activity != null) {
            try {
                activity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + this.parentActivity.getPackageName())));
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: createCaptionTextView */
    public TextView lambda$setParentActivity$3$PhotoViewer(LinkMovementMethod linkMovementMethod) {
        AnonymousClass30 r0 = new TextView(this.actvityContext) {
            private boolean handleClicks;

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == 0) {
                    this.handleClicks = PhotoViewer.this.needCaptionLayout;
                }
                return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
            }

            public void scrollTo(int i, int i2) {
                if (getParent().getParent() == PhotoViewer.this.pickerView) {
                    super.scrollTo(i, i2);
                    this.handleClicks = false;
                }
            }

            public boolean performClick() {
                return this.handleClicks && super.performClick();
            }

            public void setPressed(boolean z) {
                boolean z2 = z != isPressed();
                super.setPressed(z);
                if (z2) {
                    invalidate();
                }
            }
        };
        r0.setMovementMethod(linkMovementMethod);
        ViewHelper.setPadding(r0, 16.0f, 8.0f, 16.0f, 8.0f);
        r0.setLinkTextColor(-8994063);
        r0.setTextColor(-1);
        r0.setHighlightColor(NUM);
        r0.setGravity(LayoutHelper.getAbsoluteGravityStart() | 16);
        r0.setTextSize(1, 16.0f);
        r0.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoViewer.this.lambda$createCaptionTextView$36$PhotoViewer(view);
            }
        });
        return r0;
    }

    public /* synthetic */ void lambda$createCaptionTextView$36$PhotoViewer(View view) {
        openCaptionEnter();
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
    public void switchToPip(boolean z) {
        if (this.videoPlayer != null && this.textureUploaded && checkInlinePermissions() && !this.changingTextureView && !this.switchingInlineMode && !this.isInline) {
            if (PipInstance != null) {
                PipInstance.destroyPhotoViewer();
            }
            this.openedFullScreenVideo = false;
            PipInstance = Instance;
            CubicBezierInterpolator cubicBezierInterpolator = null;
            Instance = null;
            this.switchingInlineMode = true;
            this.isVisible = false;
            PlaceProviderObject placeProviderObject = this.currentPlaceObject;
            if (placeProviderObject != null && !placeProviderObject.imageReceiver.getVisible()) {
                this.currentPlaceObject.imageReceiver.setVisible(true, true);
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
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                float translationX2 = this.videoTextureView.getTranslationX();
                float translationY2 = this.videoTextureView.getTranslationY() + this.translationY;
                float f = pipRect.x;
                float x = (f - this.aspectRatioFrameLayout.getX()) + ((float) getLeftInset());
                float f2 = pipRect.y;
                float y = f2 - this.aspectRatioFrameLayout.getY();
                this.textureImageView.setTranslationY(this.translationY);
                this.videoTextureView.setTranslationY(this.translationY);
                this.translationY = 0.0f;
                if (z) {
                    if (translationY2 < y) {
                        cubicBezierInterpolator = new CubicBezierInterpolator(0.5d, 0.0d, 0.9d, 0.9d);
                    } else {
                        cubicBezierInterpolator = new CubicBezierInterpolator(0.0d, 0.5d, 0.9d, 0.9d);
                    }
                }
                $$Lambda$PhotoViewer$aK8VglSyYgt7oP162Y0HcNjHjTk r13 = r1;
                ValueAnimator valueAnimator = ofFloat;
                $$Lambda$PhotoViewer$aK8VglSyYgt7oP162Y0HcNjHjTk r1 = new ValueAnimator.AnimatorUpdateListener(cubicBezierInterpolator, translationX2, f, translationY2, f2, x, y) {
                    public final /* synthetic */ CubicBezierInterpolator f$1;
                    public final /* synthetic */ float f$2;
                    public final /* synthetic */ float f$3;
                    public final /* synthetic */ float f$4;
                    public final /* synthetic */ float f$5;
                    public final /* synthetic */ float f$6;
                    public final /* synthetic */ float f$7;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                        this.f$7 = r8;
                    }

                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PhotoViewer.this.lambda$switchToPip$37$PhotoViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, valueAnimator);
                    }
                };
                valueAnimator.addUpdateListener(r13);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.textureImageView, View.SCALE_X, new float[]{width}), ObjectAnimator.ofFloat(this.textureImageView, View.SCALE_Y, new float[]{width}), ObjectAnimator.ofFloat(this.videoTextureView, View.SCALE_X, new float[]{width}), ObjectAnimator.ofFloat(this.videoTextureView, View.SCALE_Y, new float[]{width}), ObjectAnimator.ofInt(this.backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0}), ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.captionTextViewSwitcher, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.groupedPhotosListView, View.ALPHA, new float[]{0.0f}), valueAnimator});
                if (z) {
                    animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    animatorSet.setDuration(300);
                } else {
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.setDuration(250);
                }
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

    public /* synthetic */ void lambda$switchToPip$37$PhotoViewer(CubicBezierInterpolator cubicBezierInterpolator, float f, float f2, float f3, float f4, float f5, float f6, ValueAnimator valueAnimator) {
        float f7;
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (cubicBezierInterpolator == null) {
            f7 = floatValue;
        } else {
            f7 = cubicBezierInterpolator.getInterpolation(floatValue);
        }
        float f8 = f * (1.0f - floatValue);
        this.textureImageView.setTranslationX((f2 * floatValue) + f8);
        float f9 = f3 * (1.0f - f7);
        this.textureImageView.setTranslationY((f4 * f7) + f9);
        this.videoTextureView.setTranslationX(f8 + (f5 * floatValue));
        this.videoTextureView.setTranslationY(f9 + (f6 * f7));
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
        int thumbX = (this.videoPlayerSeekbar.getThumbX() + AndroidUtilities.dp(2.0f)) - (this.videoPreviewFrame.getMeasuredWidth() / 2);
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
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.videoPreviewFrameAnimation = animatorSet2;
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
        VideoPlayerControlFrameLayout videoPlayerControlFrameLayout2 = new VideoPlayerControlFrameLayout(this.containerView.getContext());
        this.videoPlayerControlFrameLayout = videoPlayerControlFrameLayout2;
        this.containerView.addView(videoPlayerControlFrameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        final AnonymousClass33 r0 = new VideoPlayerSeekBar.SeekBarDelegate() {
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
        };
        AnonymousClass34 r1 = new FloatSeekBarAccessibilityDelegate() {
            public float getProgress() {
                return PhotoViewer.this.videoPlayerSeekbar.getProgress();
            }

            public void setProgress(float f) {
                r0.onSeekBarDrag(f);
                PhotoViewer.this.videoPlayerSeekbar.setProgress(f);
                PhotoViewer.this.videoPlayerSeekbarView.invalidate();
            }

            public String getContentDescription(View view) {
                return LocaleController.formatString("AccDescrPlayerDuration", NUM, LocaleController.formatPluralString("Minutes", PhotoViewer.this.videoPlayerCurrentTime[0]) + ' ' + LocaleController.formatPluralString("Seconds", PhotoViewer.this.videoPlayerCurrentTime[1]), LocaleController.formatPluralString("Minutes", PhotoViewer.this.videoPlayerTotalTime[0]) + ' ' + LocaleController.formatPluralString("Seconds", PhotoViewer.this.videoPlayerTotalTime[1]));
            }
        };
        AnonymousClass35 r3 = new View(this.containerView.getContext()) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                PhotoViewer.this.videoPlayerSeekbar.draw(canvas);
            }
        };
        this.videoPlayerSeekbarView = r3;
        r3.setAccessibilityDelegate(r1);
        this.videoPlayerSeekbarView.setImportantForAccessibility(1);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerSeekbarView, LayoutHelper.createFrame(-1, -1.0f));
        VideoPlayerSeekBar videoPlayerSeekBar = new VideoPlayerSeekBar(this.videoPlayerSeekbarView);
        this.videoPlayerSeekbar = videoPlayerSeekBar;
        videoPlayerSeekBar.setHorizontalPadding(AndroidUtilities.dp(2.0f));
        this.videoPlayerSeekbar.setColors(NUM, NUM, -1, -1, -1, NUM);
        this.videoPlayerSeekbar.setDelegate(r0);
        AnonymousClass36 r02 = new VideoSeekPreviewImage(this.containerView.getContext(), new VideoSeekPreviewImage.VideoSeekPreviewImageDelegate() {
            public final void onReady() {
                PhotoViewer.this.lambda$createVideoControlsInterface$38$PhotoViewer();
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
        this.videoPreviewFrame = r02;
        r02.setAlpha(0.0f);
        this.containerView.addView(this.videoPreviewFrame, LayoutHelper.createFrame(-2, -2.0f, 83, 0.0f, 0.0f, 0.0f, 58.0f));
        SimpleTextView simpleTextView = new SimpleTextView(this.containerView.getContext());
        this.videoPlayerTime = simpleTextView;
        simpleTextView.setTextColor(-1);
        this.videoPlayerTime.setGravity(53);
        this.videoPlayerTime.setTextSize(14);
        this.videoPlayerTime.setImportantForAccessibility(2);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 15.0f, 12.0f, 0.0f));
    }

    public /* synthetic */ void lambda$createVideoControlsInterface$38$PhotoViewer() {
        if (this.needShowOnReady) {
            showVideoSeekPreviewPosition(true);
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
        int i;
        int i2 = -1;
        if (this.isCurrentVideo || !hasAnimatedMediaEntities() || this.centerImage.getBitmapWidth() <= 0) {
            ArrayList<VideoEditedInfo.MediaEntity> arrayList = null;
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
            videoEditedInfo.originalPath = this.currentPathObject;
            int i3 = this.estimatedSize;
            videoEditedInfo.estimatedSize = i3 != 0 ? (long) i3 : 1;
            videoEditedInfo.estimatedDuration = this.estimatedDuration;
            videoEditedInfo.framerate = this.videoFramerate;
            videoEditedInfo.originalDuration = (long) (this.videoDuration * 1000.0f);
            EditState editState2 = this.editState;
            videoEditedInfo.filterState = editState2.savedFilterState;
            String str = editState2.croppedPaintPath;
            if (str != null) {
                videoEditedInfo.paintPath = str;
                ArrayList<VideoEditedInfo.MediaEntity> arrayList2 = editState2.croppedMediaEntities;
                if (arrayList2 != null && !arrayList2.isEmpty()) {
                    arrayList = this.editState.croppedMediaEntities;
                }
                videoEditedInfo.mediaEntities = arrayList;
            } else {
                videoEditedInfo.paintPath = editState2.paintPath;
                ArrayList<VideoEditedInfo.MediaEntity> arrayList3 = editState2.mediaEntities;
                if (arrayList3 != null && !arrayList3.isEmpty()) {
                    arrayList = this.editState.mediaEntities;
                }
                videoEditedInfo.mediaEntities = arrayList;
            }
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
            } else {
                videoEditedInfo.resultWidth = this.originalWidth;
                videoEditedInfo.resultHeight = this.originalHeight;
                if (!this.muteVideo) {
                    i2 = this.originalBitrate;
                }
                videoEditedInfo.bitrate = i2;
            }
            MediaController.CropState cropState = this.editState.cropState;
            videoEditedInfo.cropState = cropState;
            if (cropState != null) {
                videoEditedInfo.rotationValue += cropState.transformRotation;
                while (true) {
                    i = videoEditedInfo.rotationValue;
                    if (i < 360) {
                        break;
                    }
                    videoEditedInfo.rotationValue = i - 360;
                }
                if (i == 90 || i == 270) {
                    MediaController.CropState cropState2 = videoEditedInfo.cropState;
                    cropState2.transformWidth = (int) (((float) videoEditedInfo.resultWidth) * cropState2.cropPh);
                    cropState2.transformHeight = (int) (((float) videoEditedInfo.resultHeight) * cropState2.cropPw);
                } else {
                    MediaController.CropState cropState3 = videoEditedInfo.cropState;
                    cropState3.transformWidth = (int) (((float) videoEditedInfo.resultWidth) * cropState3.cropPw);
                    cropState3.transformHeight = (int) (((float) videoEditedInfo.resultHeight) * cropState3.cropPh);
                }
                MediaController.CropState cropState4 = videoEditedInfo.cropState;
                int i4 = cropState4.transformWidth;
                if (i4 % 16 != 0) {
                    cropState4.transformWidth = Math.min(1, Math.round(((float) i4) / 16.0f)) * 16;
                }
                MediaController.CropState cropState5 = videoEditedInfo.cropState;
                int i5 = cropState5.transformHeight;
                if (i5 % 16 != 0) {
                    cropState5.transformHeight = Math.min(1, Math.round(((float) i5) / 16.0f)) * 16;
                }
            }
            videoEditedInfo.muted = this.muteVideo;
            return videoEditedInfo;
        }
        VideoEditedInfo videoEditedInfo2 = new VideoEditedInfo();
        videoEditedInfo2.startTime = 0;
        videoEditedInfo2.start = (float) 0;
        videoEditedInfo2.endTime = Math.min(3000, this.editState.averageDuration);
        while (true) {
            long j = videoEditedInfo2.endTime;
            if (j <= 0 || j >= 1000) {
                long j2 = videoEditedInfo2.endTime;
                videoEditedInfo2.end = (float) j2;
                videoEditedInfo2.rotationValue = 0;
                videoEditedInfo2.originalPath = this.currentImagePath;
                videoEditedInfo2.estimatedSize = (long) ((int) ((((float) j2) / 1000.0f) * 115200.0f));
                videoEditedInfo2.estimatedDuration = j2;
                videoEditedInfo2.framerate = 30;
                videoEditedInfo2.originalDuration = j2;
                EditState editState3 = this.editState;
                videoEditedInfo2.filterState = editState3.savedFilterState;
                videoEditedInfo2.paintPath = editState3.paintPath;
                videoEditedInfo2.mediaEntities = editState3.mediaEntities;
                videoEditedInfo2.isPhoto = true;
                float bitmapWidth = (float) this.centerImage.getBitmapWidth();
                float bitmapHeight = (float) this.centerImage.getBitmapHeight();
                float max = Math.max(bitmapWidth / 854.0f, bitmapHeight / 854.0f);
            } else {
                videoEditedInfo2.endTime = j * 2;
            }
        }
        long j22 = videoEditedInfo2.endTime;
        videoEditedInfo2.end = (float) j22;
        videoEditedInfo2.rotationValue = 0;
        videoEditedInfo2.originalPath = this.currentImagePath;
        videoEditedInfo2.estimatedSize = (long) ((int) ((((float) j22) / 1000.0f) * 115200.0f));
        videoEditedInfo2.estimatedDuration = j22;
        videoEditedInfo2.framerate = 30;
        videoEditedInfo2.originalDuration = j22;
        EditState editState32 = this.editState;
        videoEditedInfo2.filterState = editState32.savedFilterState;
        videoEditedInfo2.paintPath = editState32.paintPath;
        videoEditedInfo2.mediaEntities = editState32.mediaEntities;
        videoEditedInfo2.isPhoto = true;
        float bitmapWidth2 = (float) this.centerImage.getBitmapWidth();
        float bitmapHeight2 = (float) this.centerImage.getBitmapHeight();
        float max2 = Math.max(bitmapWidth2 / 854.0f, bitmapHeight2 / 854.0f);
        if (max2 < 1.0f) {
            max2 = 1.0f;
        }
        int i6 = (int) (bitmapWidth2 / max2);
        int i7 = (int) (bitmapHeight2 / max2);
        if (i6 % 16 != 0) {
            i6 = Math.round(((float) i6) / 16.0f) * 16;
        }
        if (i7 % 16 != 0) {
            i7 = Math.round(((float) i7) / 16.0f) * 16;
        }
        videoEditedInfo2.resultWidth = i6;
        videoEditedInfo2.originalWidth = i6;
        videoEditedInfo2.resultHeight = i7;
        videoEditedInfo2.originalHeight = i7;
        videoEditedInfo2.bitrate = -1;
        videoEditedInfo2.muted = true;
        return videoEditedInfo2;
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
            if (z) {
                CharSequence[] charSequenceArr = {this.captionEditText.getFieldCharSequence()};
                ArrayList<TLRPC$MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities());
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
            String str = this.lastTitle;
            if (str != null) {
                this.actionBar.setTitle(str);
                this.lastTitle = null;
            }
            if (this.isCurrentVideo) {
                this.actionBar.setSubtitle(this.muteVideo ? LocaleController.getString("SoundMuted", NUM) : this.currentSubtitle);
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
        Arrays.fill(this.videoPlayerCurrentTime, 0);
        Arrays.fill(this.videoPlayerTotalTime, 0);
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            long max = Math.max(0, videoPlayer2.getCurrentPosition());
            long max2 = Math.max(0, this.videoPlayer.getDuration());
            if (!this.inPreview && this.videoTimelineView.getVisibility() == 0) {
                max2 = (long) (((float) max2) * (this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()));
                max = (long) (((float) max) - (this.videoTimelineView.getLeftProgress() * ((float) max2)));
                if (max > max2) {
                    max = max2;
                }
            }
            long j = max / 1000;
            long j2 = max2 / 1000;
            int[] iArr = this.videoPlayerCurrentTime;
            iArr[0] = (int) (j / 60);
            iArr[1] = (int) (j % 60);
            int[] iArr2 = this.videoPlayerTotalTime;
            iArr2[0] = (int) (j2 / 60);
            iArr2[1] = (int) (j2 % 60);
        }
        this.videoPlayerTime.setText(String.format(Locale.ROOT, "%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(this.videoPlayerCurrentTime[0]), Integer.valueOf(this.videoPlayerCurrentTime[1]), Integer.valueOf(this.videoPlayerTotalTime[0]), Integer.valueOf(this.videoPlayerTotalTime[1])}));
    }

    private void checkBufferedProgress(float f) {
        MessageObject messageObject;
        TLRPC$Document document;
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
        SelectedPhotosListView selectedPhotosListView2 = this.selectedPhotosListView;
        if (selectedPhotosListView2 != null) {
            int childCount = selectedPhotosListView2.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.selectedPhotosListView.getChildAt(i);
                if (childAt instanceof PhotoPickerPhotoCell) {
                    ((PhotoPickerPhotoCell) childAt).updateColors();
                }
            }
        }
        StickersAlert stickersAlert = this.masksAlert;
        if (stickersAlert != null) {
            stickersAlert.updateColors(true);
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
    public void scheduleActionBarHide() {
        scheduleActionBarHide(3000);
    }

    private void scheduleActionBarHide(int i) {
        if (!isAccessibilityEnabled()) {
            AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
            AndroidUtilities.runOnUIThread(this.hideActionBarRunnable, (long) i);
        }
    }

    private boolean isAccessibilityEnabled() {
        try {
            return ((AccessibilityManager) this.actvityContext.getSystemService("accessibility")).isEnabled();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void updatePlayerState(boolean z, int i) {
        MessageObject messageObject;
        if (this.videoPlayer != null) {
            float f = 0.0f;
            if (this.isStreaming) {
                if (i != 2 || !this.skipFirstBufferingProgress) {
                    boolean z2 = this.seekToProgressPending != 0.0f || i == 2;
                    if (z2) {
                        AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
                    } else {
                        scheduleActionBarHide();
                    }
                    toggleMiniProgress(z2, true);
                } else if (z) {
                    this.skipFirstBufferingProgress = false;
                }
            }
            int i2 = 4;
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
                if (!this.pipItem.isEnabled() && this.pipItem.getVisibility() == 0) {
                    this.pipAvailable = true;
                    this.pipItem.setEnabled(true);
                    this.pipItem.animate().alpha(1.0f).setDuration(175).withEndAction((Runnable) null).start();
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
                    if (this.currentEditMode != 3) {
                        this.photoProgressViews[0].setIndexedAlpha(1, 1.0f, false);
                        this.photoProgressViews[0].setBackgroundState(3, false);
                    }
                    AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
                    if (i == 4) {
                        if (!this.isCurrentVideo) {
                            this.videoPlayerSeekbar.setProgress(0.0f);
                            this.videoPlayerSeekbarView.invalidate();
                            if (this.inPreview || this.videoTimelineView.getVisibility() != 0) {
                                this.videoPlayer.seekTo(0);
                            } else {
                                this.videoPlayer.seekTo((long) ((int) (this.videoTimelineView.getLeftProgress() * ((float) this.videoPlayer.getDuration()))));
                            }
                            this.videoPlayer.pause();
                            if (!this.isActionBarVisible) {
                                toggleActionBar(true, true);
                            }
                        } else if (!this.videoTimelineView.isDragging()) {
                            this.videoTimelineView.setProgress(0.0f);
                            if (this.inPreview || (this.currentEditMode == 0 && this.videoTimelineView.getVisibility() != 0)) {
                                this.videoPlayer.seekTo(0);
                            } else {
                                this.videoPlayer.seekTo((long) ((int) (this.videoTimelineView.getLeftProgress() * ((float) this.videoPlayer.getDuration()))));
                            }
                            if (this.currentEditMode != 0 || this.switchingToMode > 0) {
                                this.videoPlayer.play();
                            } else {
                                this.videoPlayer.pause();
                            }
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
                PhotoProgressView photoProgressView = this.photoProgressViews[0];
                if (this.isCurrentVideo) {
                    i2 = -1;
                }
                photoProgressView.setBackgroundState(i2, false);
                PhotoProgressView photoProgressView2 = this.photoProgressViews[0];
                if (this.isCurrentVideo || isAccessibilityEnabled() || ((!this.playerAutoStarted || this.playerWasPlaying) && this.isActionBarVisible)) {
                    f = 1.0f;
                }
                photoProgressView2.setIndexedAlpha(1, f, false);
                this.playerWasPlaying = true;
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
        preparePlayer(uri, z, z2, (MediaController.SavedFilterState) null);
    }

    private void preparePlayer(Uri uri, boolean z, boolean z2, MediaController.SavedFilterState savedFilterState) {
        boolean z3;
        Uri uri2 = uri;
        boolean z4 = z2;
        if (!z4) {
            this.currentPlayingVideoFile = uri2;
        }
        if (this.parentActivity != null) {
            this.streamingAlertShown = false;
            this.startedPlayTime = SystemClock.elapsedRealtime();
            this.currentVideoFinishedLoading = false;
            this.lastBufferedPositionCheck = 0;
            this.firstAnimationDelay = true;
            this.inPreview = z4;
            releasePlayer(false);
            SavedVideoPosition savedVideoPosition = null;
            if (this.imagesArrLocals.isEmpty()) {
                createVideoTextureView((MediaController.SavedFilterState) null);
            }
            if (Build.VERSION.SDK_INT >= 21 && this.textureImageView == null) {
                ImageView imageView = new ImageView(this.parentActivity);
                this.textureImageView = imageView;
                imageView.setBackgroundColor(-65536);
                this.textureImageView.setPivotX(0.0f);
                this.textureImageView.setPivotY(0.0f);
                this.textureImageView.setVisibility(4);
                this.containerView.addView(this.textureImageView);
            }
            this.textureUploaded = false;
            this.videoSizeSet = false;
            this.videoCrossfadeStarted = false;
            this.playerWasReady = false;
            this.playerWasPlaying = false;
            if (this.videoPlayer == null) {
                VideoPlayer videoPlayer2 = this.injectingVideoPlayer;
                if (videoPlayer2 != null) {
                    this.videoPlayer = videoPlayer2;
                    this.injectingVideoPlayer = null;
                    this.playerInjected = true;
                    updatePlayerState(videoPlayer2.getPlayWhenReady(), this.videoPlayer.getPlaybackState());
                    z3 = false;
                } else {
                    this.videoPlayer = new VideoPlayer() {
                        public void play() {
                            super.play();
                            PhotoViewer.this.playOrStopAnimatedStickers(true);
                        }

                        public void pause() {
                            super.pause();
                            if (PhotoViewer.this.currentEditMode == 0) {
                                PhotoViewer.this.playOrStopAnimatedStickers(false);
                            }
                        }
                    };
                    z3 = true;
                }
                TextureView textureView = this.videoTextureView;
                if (textureView != null) {
                    this.videoPlayer.setTextureView(textureView);
                }
                this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                    public void onStateChanged(boolean z, int i) {
                        PhotoViewer.this.updatePlayerState(z, i);
                    }

                    public void onError(VideoPlayer videoPlayer, Exception exc) {
                        if (PhotoViewer.this.videoPlayer == videoPlayer) {
                            FileLog.e((Throwable) exc);
                            if (PhotoViewer.this.menuItem.isSubItemVisible(11)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) PhotoViewer.this.parentActivity);
                                builder.setTitle(LocaleController.getString("AppName", NUM));
                                builder.setMessage(LocaleController.getString("CantPlayVideo", NUM));
                                builder.setPositiveButton(LocaleController.getString("Open", NUM), new DialogInterface.OnClickListener() {
                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        PhotoViewer.AnonymousClass38.this.lambda$onError$0$PhotoViewer$38(dialogInterface, i);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                                PhotoViewer.this.showAlertDialog(builder);
                            }
                        }
                    }

                    public /* synthetic */ void lambda$onError$0$PhotoViewer$38(DialogInterface dialogInterface, int i) {
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
                            if (PhotoViewer.this.videoTextureView instanceof VideoEditTextureView) {
                                ((VideoEditTextureView) PhotoViewer.this.videoTextureView).setVideoSize((int) (((float) i2) * f), i);
                            }
                            boolean unused = PhotoViewer.this.videoSizeSet = true;
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
                                int[] access$14800 = PhotoViewer.this.pipPosition;
                                access$14800[1] = (int) (((float) access$14800[1]) - PhotoViewer.this.containerView.getTranslationY());
                                PhotoViewer.this.textureImageView.setTranslationX(PhotoViewer.this.textureImageView.getTranslationX() + ((float) PhotoViewer.this.getLeftInset()));
                                PhotoViewer.this.videoTextureView.setTranslationX((PhotoViewer.this.videoTextureView.getTranslationX() + ((float) PhotoViewer.this.getLeftInset())) - PhotoViewer.this.aspectRatioFrameLayout.getX());
                                AnimatorSet animatorSet = new AnimatorSet();
                                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.TRANSLATION_X, new float[]{(float) PhotoViewer.this.pipPosition[0]}), ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.TRANSLATION_Y, new float[]{(float) PhotoViewer.this.pipPosition[1]}), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.TRANSLATION_X, new float[]{((float) PhotoViewer.this.pipPosition[0]) - PhotoViewer.this.aspectRatioFrameLayout.getX()}), ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.TRANSLATION_Y, new float[]{((float) PhotoViewer.this.pipPosition[1]) - PhotoViewer.this.aspectRatioFrameLayout.getY()}), ObjectAnimator.ofInt(PhotoViewer.this.backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{255}), ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.captionTextViewSwitcher, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.groupedPhotosListView, View.ALPHA, new float[]{1.0f})});
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
            if (!this.imagesArrLocals.isEmpty()) {
                createVideoTextureView(savedFilterState);
            }
            TextureView textureView2 = this.videoTextureView;
            this.videoCrossfadeAlpha = 0.0f;
            textureView2.setAlpha(0.0f);
            PaintingOverlay paintingOverlay2 = this.paintingOverlay;
            if (paintingOverlay2 != null) {
                paintingOverlay2.setAlpha(this.videoCrossfadeAlpha);
            }
            this.shouldSavePositionForCurrentVideo = null;
            this.shouldSavePositionForCurrentVideoShortTerm = null;
            this.lastSaveTime = 0;
            if (z3) {
                this.seekToProgressPending = this.seekToProgressPending2;
                this.videoPlayerSeekbar.setProgress(0.0f);
                this.videoTimelineView.setProgress(0.0f);
                this.videoPlayerSeekbar.setBufferedProgress(0.0f);
                MessageObject messageObject = this.currentMessageObject;
                if (messageObject != null) {
                    int duration = messageObject.getDuration();
                    String fileName = this.currentMessageObject.getFileName();
                    if (!TextUtils.isEmpty(fileName)) {
                        if (duration >= 1200) {
                            if (this.currentMessageObject.forceSeekTo < 0.0f) {
                                float f = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).getFloat(fileName, -1.0f);
                                if (f > 0.0f && f < 0.999f) {
                                    this.currentMessageObject.forceSeekTo = f;
                                    this.videoPlayerSeekbar.setProgress(f);
                                }
                            }
                            this.shouldSavePositionForCurrentVideo = fileName;
                        } else if (duration >= 10) {
                            for (int size = this.savedVideoPositions.size() - 1; size >= 0; size--) {
                                SavedVideoPosition valueAt = this.savedVideoPositions.valueAt(size);
                                if (valueAt.timestamp < SystemClock.elapsedRealtime() - 5000) {
                                    this.savedVideoPositions.removeAt(size);
                                } else if (savedVideoPosition == null && this.savedVideoPositions.keyAt(size).equals(fileName)) {
                                    savedVideoPosition = valueAt;
                                }
                            }
                            MessageObject messageObject2 = this.currentMessageObject;
                            if (messageObject2.forceSeekTo < 0.0f && savedVideoPosition != null) {
                                float f2 = savedVideoPosition.position;
                                if (f2 > 0.0f && f2 < 0.999f) {
                                    messageObject2.forceSeekTo = f2;
                                    this.videoPlayerSeekbar.setProgress(f2);
                                }
                            }
                            this.shouldSavePositionForCurrentVideoShortTerm = fileName;
                        }
                    }
                }
                this.videoPlayer.preparePlayer(uri2, "other");
                this.videoPlayer.setPlayWhenReady(z);
            }
            MessageObject messageObject3 = this.currentMessageObject;
            boolean z5 = messageObject3 != null && messageObject3.getDuration() <= 30;
            this.playerLooping = z5;
            this.videoPlayerControlFrameLayout.setSeekBarTransitionEnabled(z5);
            this.videoPlayer.setLooping(this.playerLooping);
            MessageObject messageObject4 = this.currentMessageObject;
            if (messageObject4 != null) {
                float f3 = messageObject4.forceSeekTo;
                if (f3 >= 0.0f) {
                    this.seekToProgressPending = f3;
                    messageObject4.forceSeekTo = -1.0f;
                }
            }
            TLRPC$BotInlineResult tLRPC$BotInlineResult = this.currentBotInlineResult;
            if (tLRPC$BotInlineResult == null || (!tLRPC$BotInlineResult.type.equals("video") && !MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                this.bottomLayout.setPadding(0, 0, 0, 0);
            } else {
                this.bottomLayout.setVisibility(0);
                this.bottomLayout.setPadding(0, 0, AndroidUtilities.dp(84.0f), 0);
                this.pickerView.setVisibility(8);
            }
            setVideoPlayerControlVisible(!this.isCurrentVideo, true);
            if (!this.isCurrentVideo) {
                scheduleActionBarHide(this.playerAutoStarted ? 3000 : 1000);
            }
            this.inPreview = z4;
        }
    }

    private void createVideoTextureView(MediaController.SavedFilterState savedFilterState) {
        MediaController.SavedFilterState savedFilterState2 = savedFilterState;
        if (this.videoTextureView == null) {
            AnonymousClass39 r2 = new AspectRatioFrameLayout(this.parentActivity) {
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
            this.aspectRatioFrameLayout = r2;
            r2.setVisibility(4);
            this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
            if (this.imagesArrLocals.isEmpty()) {
                this.videoTextureView = new TextureView(this.parentActivity);
            } else {
                VideoEditTextureView videoEditTextureView = new VideoEditTextureView(this.parentActivity, this.videoPlayer);
                if (savedFilterState2 != null) {
                    videoEditTextureView.setDelegate(new VideoEditTextureView.VideoEditTextureViewDelegate() {
                        public final void onEGLThreadAvailable(FilterGLThread filterGLThread) {
                            filterGLThread.setFilterGLThreadDelegate(FilterShaders.getFilterShadersDelegate(MediaController.SavedFilterState.this));
                        }
                    });
                }
                MediaController.CropState cropState = this.editState.cropState;
                if (cropState != null) {
                    this.previousHasTransform = true;
                    float f = cropState.cropPx;
                    this.previousCropPx = f;
                    float f2 = cropState.cropPy;
                    this.previousCropPy = f2;
                    float f3 = cropState.cropScale;
                    this.previousCropScale = f3;
                    float f4 = cropState.cropRotate;
                    this.previousCropRotation = f4;
                    int i = cropState.transformRotation;
                    this.previousCropOrientation = i;
                    float f5 = cropState.cropPw;
                    this.previousCropPw = f5;
                    float f6 = cropState.cropPh;
                    this.previousCropPh = f6;
                    videoEditTextureView.setViewTransform(true, f, f2, f4, i, f3, f5, f6, 0.0f, 0.0f);
                } else {
                    this.previousHasTransform = false;
                    videoEditTextureView.setViewTransform(false, this.previousCropPx, this.previousCropPy, this.previousCropRotation, this.previousCropOrientation, this.previousCropScale, this.previousCropPw, this.previousCropPh, 0.0f, 0.0f);
                }
                this.videoTextureView = videoEditTextureView;
            }
            SurfaceTexture surfaceTexture = this.injectingVideoPlayerSurface;
            if (surfaceTexture != null) {
                this.videoTextureView.setSurfaceTexture(surfaceTexture);
                this.textureUploaded = true;
                this.videoSizeSet = true;
                this.injectingVideoPlayerSurface = null;
            }
            this.videoTextureView.setPivotX(0.0f);
            this.videoTextureView.setPivotY(0.0f);
            this.videoTextureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
        }
    }

    /* access modifiers changed from: private */
    public void releasePlayer(boolean z) {
        if (this.videoPlayer != null) {
            AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
            AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
            if (this.shouldSavePositionForCurrentVideoShortTerm != null) {
                this.savedVideoPositions.put(this.shouldSavePositionForCurrentVideoShortTerm, new SavedVideoPosition(((float) this.videoPlayer.getCurrentPosition()) / ((float) this.videoPlayer.getDuration()), SystemClock.elapsedRealtime()));
            }
            this.videoPlayer.releasePlayer(true);
            this.videoPlayer = null;
        } else {
            this.playerWasPlaying = false;
        }
        this.videoPreviewFrame.close();
        toggleMiniProgress(false, false);
        this.pipAvailable = false;
        this.playerInjected = false;
        if (this.pipItem.isEnabled()) {
            this.pipItem.setEnabled(false);
            this.pipItem.animate().alpha(0.5f).setDuration(175).withEndAction((Runnable) null).start();
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
        TextureView textureView = this.videoTextureView;
        if (textureView != null) {
            if (textureView instanceof VideoEditTextureView) {
                ((VideoEditTextureView) textureView).release();
            }
            this.videoTextureView = null;
        }
        if (this.isPlaying) {
            this.isPlaying = false;
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
        }
        if (!z && !this.inPreview && !this.requestingPreview) {
            setVideoPlayerControlVisible(false, true);
        }
        this.photoProgressViews[0].resetAlphas();
    }

    private void setVideoPlayerControlVisible(final boolean z, boolean z2) {
        if (this.videoPlayerControlVisible != z) {
            Animator animator = this.videoPlayerControlAnimator;
            if (animator != null) {
                animator.cancel();
            }
            this.videoPlayerControlVisible = z;
            float f = 1.0f;
            int i = 0;
            if (z2) {
                if (z) {
                    this.videoPlayerControlFrameLayout.setVisibility(0);
                } else {
                    this.dateTextView.setVisibility(0);
                    this.nameTextView.setVisibility(0);
                    if (this.allowShare) {
                        this.shareButton.setVisibility(0);
                    }
                }
                final boolean z3 = this.allowShare;
                float[] fArr = new float[2];
                fArr[0] = this.videoPlayerControlFrameLayout.getAlpha();
                if (!z) {
                    f = 0.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                ofFloat.setDuration(200);
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(z3) {
                    public final /* synthetic */ boolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PhotoViewer.this.lambda$setVideoPlayerControlVisible$40$PhotoViewer(this.f$1, valueAnimator);
                    }
                });
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            PhotoViewer.this.videoPlayerControlFrameLayout.setVisibility(8);
                            return;
                        }
                        PhotoViewer.this.dateTextView.setVisibility(8);
                        PhotoViewer.this.nameTextView.setVisibility(8);
                        if (z3) {
                            PhotoViewer.this.shareButton.setVisibility(8);
                        }
                    }
                });
                this.videoPlayerControlAnimator = ofFloat;
                ofFloat.start();
            } else {
                this.videoPlayerControlFrameLayout.setVisibility(z ? 0 : 8);
                this.videoPlayerControlFrameLayout.setAlpha(z ? 1.0f : 0.0f);
                this.dateTextView.setVisibility(z ? 8 : 0);
                this.dateTextView.setAlpha(z ? 0.0f : 1.0f);
                this.nameTextView.setVisibility(z ? 8 : 0);
                this.nameTextView.setAlpha(z ? 0.0f : 1.0f);
                if (this.allowShare) {
                    ImageView imageView = this.shareButton;
                    if (z) {
                        i = 8;
                    }
                    imageView.setVisibility(i);
                    ImageView imageView2 = this.shareButton;
                    if (z) {
                        f = 0.0f;
                    }
                    imageView2.setAlpha(f);
                }
            }
            if (!this.allowShare) {
                return;
            }
            if (z) {
                this.menuItem.showSubItem(10);
            } else {
                this.menuItem.hideSubItem(10);
            }
        }
    }

    public /* synthetic */ void lambda$setVideoPlayerControlVisible$40$PhotoViewer(boolean z, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.videoPlayerControlFrameLayout.setAlpha(floatValue);
        float f = 1.0f - floatValue;
        this.dateTextView.setAlpha(f);
        this.nameTextView.setAlpha(f);
        if (z) {
            this.shareButton.setAlpha(f);
        }
    }

    private void updateCaptionTextForCurrentPhoto(Object obj) {
        CharSequence charSequence;
        if (obj instanceof MediaController.PhotoEntry) {
            charSequence = ((MediaController.PhotoEntry) obj).caption;
        } else {
            charSequence = (!(obj instanceof TLRPC$BotInlineResult) && (obj instanceof MediaController.SearchImage)) ? ((MediaController.SearchImage) obj).caption : null;
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
                AlertDialog show = builder.show();
                this.visibleDialog = show;
                show.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        PhotoViewer.this.lambda$showAlertDialog$41$PhotoViewer(dialogInterface);
                    }
                });
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    public /* synthetic */ void lambda$showAlertDialog$41$PhotoViewer(DialogInterface dialogInterface) {
        this.visibleDialog = null;
    }

    private void mergeThumbs(String str, String str2, Bitmap bitmap, Bitmap bitmap2, boolean z) {
        boolean z2;
        if (bitmap == null) {
            try {
                bitmap = BitmapFactory.decodeFile(str2);
                z2 = true;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            } catch (Throwable th) {
                FileLog.e(th);
                return;
            }
        } else {
            z2 = false;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float f = (float) width;
        if (f > 512.0f || ((float) height) > 512.0f) {
            float max = ((float) Math.max(width, height)) / 512.0f;
            height = (int) (((float) height) / max);
            width = (int) (f / max);
        }
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Rect rect = new Rect(0, 0, width, height);
        if (z) {
            canvas.drawBitmap(bitmap2, (Rect) null, rect, this.bitmapPaint);
            canvas.drawBitmap(bitmap, (Rect) null, rect, this.bitmapPaint);
        } else {
            canvas.drawBitmap(bitmap, (Rect) null, rect, this.bitmapPaint);
            canvas.drawBitmap(bitmap2, (Rect) null, rect, this.bitmapPaint);
        }
        FileOutputStream fileOutputStream = new FileOutputStream(new File(str));
        createBitmap.compress(Bitmap.CompressFormat.JPEG, 83, fileOutputStream);
        fileOutputStream.close();
        if (z2) {
            bitmap.recycle();
        }
        createBitmap.recycle();
    }

    /* access modifiers changed from: private */
    public void playOrStopAnimatedStickers(boolean z) {
        RLottieDrawable lottieAnimation;
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = this.editState.mediaEntities;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                VideoEditedInfo.MediaEntity mediaEntity = this.editState.mediaEntities.get(i);
                if (mediaEntity.type == 0 && (mediaEntity.subType & 1) != 0) {
                    View view = mediaEntity.view;
                    if ((view instanceof BackupImageView) && (lottieAnimation = ((BackupImageView) view).getImageReceiver().getLottieAnimation()) != null) {
                        if (z) {
                            lottieAnimation.start();
                        } else {
                            lottieAnimation.stop();
                        }
                    }
                }
            }
        }
    }

    private boolean hasAnimatedMediaEntities() {
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = this.editState.mediaEntities;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                VideoEditedInfo.MediaEntity mediaEntity = this.editState.mediaEntities.get(i);
                if (mediaEntity.type == 0 && (mediaEntity.subType & 1) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private Bitmap createCroppedBitmap(Bitmap bitmap, MediaController.CropState cropState) {
        int i;
        int i2;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int i3 = cropState.transformRotation;
        if (i3 == 90 || i3 == 270) {
            i = width;
            i2 = height;
        } else {
            i2 = width;
            i = height;
        }
        float f = (float) i2;
        int i4 = (int) (cropState.cropPw * f);
        float f2 = (float) i;
        int i5 = (int) (cropState.cropPh * f2);
        Bitmap createBitmap = Bitmap.createBitmap(i4, i5, Bitmap.Config.ARGB_8888);
        Matrix matrix = new Matrix();
        matrix.postTranslate((float) ((-width) / 2), (float) ((-height) / 2));
        matrix.postRotate(cropState.cropRotate + ((float) cropState.transformRotation));
        matrix.postTranslate(cropState.cropPx * f, cropState.cropPy * f2);
        float f3 = cropState.cropScale;
        matrix.postScale(f3, f3);
        matrix.postTranslate((float) (i4 / 2), (float) (i5 / 2));
        new Canvas(createBitmap).drawBitmap(bitmap, matrix, new Paint(2));
        return createBitmap;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v0, resolved type: org.telegram.messenger.MediaController$SavedFilterState} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v12, resolved type: java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v1, resolved type: org.telegram.messenger.MediaController$SavedFilterState} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v2, resolved type: org.telegram.messenger.MediaController$SavedFilterState} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v14, resolved type: java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: org.telegram.messenger.MediaController$SavedFilterState} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v4, resolved type: org.telegram.messenger.MediaController$SavedFilterState} */
    /* JADX WARNING: type inference failed for: r6v0, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument>] */
    /* JADX WARNING: type inference failed for: r6v13 */
    /* JADX WARNING: type inference failed for: r6v15 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a7 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00be  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00e5  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00e8 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00e9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyCurrentEditMode() {
        /*
            r38 = this;
            r7 = r38
            r8 = 1
            android.graphics.Bitmap[] r0 = new android.graphics.Bitmap[r8]
            java.util.ArrayList<java.lang.Object> r1 = r7.imagesArrLocals
            int r2 = r7.currentIndex
            java.lang.Object r1 = r1.get(r2)
            r9 = r1
            org.telegram.messenger.MediaController$MediaEditState r9 = (org.telegram.messenger.MediaController.MediaEditState) r9
            int r1 = r7.currentEditMode
            r2 = 1148846080(0x447a0000, float:1000.0)
            r3 = 1140850688(0x44000000, float:512.0)
            r10 = 2
            r11 = 3
            r12 = 0
            r13 = 0
            if (r1 == r8) goto L_0x0067
            if (r1 != 0) goto L_0x0023
            int r1 = r7.sendPhotoType
            if (r1 != r8) goto L_0x0023
            goto L_0x0067
        L_0x0023:
            int r1 = r7.currentEditMode
            if (r1 != r10) goto L_0x003a
            org.telegram.ui.Components.PhotoFilterView r1 = r7.photoFilterView
            android.graphics.Bitmap r1 = r1.getBitmap()
            org.telegram.ui.Components.PhotoFilterView r4 = r7.photoFilterView
            org.telegram.messenger.MediaController$SavedFilterState r4 = r4.getSavedFilterState()
            boolean r5 = r7.isCurrentVideo
            r14 = r4
            r4 = r12
            r6 = r4
            goto L_0x00a3
        L_0x003a:
            if (r1 != r11) goto L_0x0064
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r1 < r4) goto L_0x004e
            int r1 = r7.sendPhotoType
            if (r1 == 0) goto L_0x0048
            if (r1 != r10) goto L_0x004e
        L_0x0048:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            goto L_0x004f
        L_0x004e:
            r1 = r12
        L_0x004f:
            org.telegram.ui.Components.PhotoPaintView r4 = r7.photoPaintView
            android.graphics.Bitmap r4 = r4.getBitmap(r1, r0)
            org.telegram.ui.Components.PhotoPaintView r5 = r7.photoPaintView
            java.util.ArrayList r5 = r5.getMasks()
            r6 = r5
            r14 = r12
            r5 = 0
            r37 = r4
            r4 = r1
            r1 = r37
            goto L_0x00a3
        L_0x0064:
            r1 = r12
            r4 = r1
            goto L_0x00a0
        L_0x0067:
            org.telegram.ui.Components.PhotoCropView r1 = r7.photoCropView
            android.graphics.Bitmap r1 = r1.getBitmap(r9)
            boolean r4 = r7.isCurrentVideo
            if (r4 == 0) goto L_0x009f
            java.lang.String r1 = r9.filterPath
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0080
            java.lang.String r1 = r9.filterPath
            android.graphics.Bitmap r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r12, r3, r3, r8)
            goto L_0x009a
        L_0x0080:
            java.lang.String r1 = r9.getPath()
            org.telegram.ui.Components.VideoTimelinePlayView r4 = r7.videoTimelineView
            float r4 = r4.getLeftProgress()
            org.telegram.ui.Components.VideoPlayer r5 = r7.videoPlayer
            long r5 = r5.getDuration()
            float r5 = (float) r5
            float r4 = r4 * r5
            float r4 = r4 * r2
            long r4 = (long) r4
            android.graphics.Bitmap r1 = org.telegram.messenger.SendMessagesHelper.createVideoThumbnailAtTime(r1, r4)
        L_0x009a:
            r4 = r12
            r6 = r4
            r14 = r6
            r5 = 1
            goto L_0x00a3
        L_0x009f:
            r4 = r12
        L_0x00a0:
            r6 = r4
            r14 = r6
            r5 = 0
        L_0x00a3:
            int r15 = r7.currentEditMode
            if (r15 == r11) goto L_0x00bb
            if (r1 == 0) goto L_0x00bb
            boolean r15 = r7.isCurrentVideo
            if (r15 == 0) goto L_0x00bb
            org.telegram.messenger.MediaController$CropState r15 = r9.cropState
            if (r15 == 0) goto L_0x00bb
            if (r5 == 0) goto L_0x00bb
            android.graphics.Bitmap r5 = r7.createCroppedBitmap(r1, r15)
            r1.recycle()
            goto L_0x00bc
        L_0x00bb:
            r5 = r1
        L_0x00bc:
            if (r5 == 0) goto L_0x00e5
            int r1 = r7.currentEditMode
            if (r1 != r11) goto L_0x00c5
            android.graphics.Bitmap$CompressFormat r1 = android.graphics.Bitmap.CompressFormat.PNG
            goto L_0x00c7
        L_0x00c5:
            android.graphics.Bitmap$CompressFormat r1 = android.graphics.Bitmap.CompressFormat.JPEG
        L_0x00c7:
            r16 = r1
            int r1 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r1 = (float) r1
            int r15 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r15 = (float) r15
            r19 = 87
            r20 = 0
            r21 = 101(0x65, float:1.42E-43)
            r22 = 101(0x65, float:1.42E-43)
            r18 = r15
            r15 = r5
            r17 = r1
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r15, r16, r17, r18, r19, r20, r21, r22)
            goto L_0x00e6
        L_0x00e5:
            r1 = r12
        L_0x00e6:
            if (r1 != 0) goto L_0x00e9
            return
        L_0x00e9:
            java.lang.String r15 = r9.thumbPath
            if (r15 == 0) goto L_0x00f9
            java.io.File r15 = new java.io.File
            java.lang.String r3 = r9.thumbPath
            r15.<init>(r3)
            r15.delete()
            r9.thumbPath = r12
        L_0x00f9:
            int r3 = r7.currentEditMode
            java.lang.String r15 = "_temp.jpg"
            r11 = 4
            if (r3 == r8) goto L_0x05c2
            if (r3 != 0) goto L_0x0108
            int r3 = r7.sendPhotoType
            if (r3 != r8) goto L_0x0108
            goto L_0x05c2
        L_0x0108:
            int r3 = r7.currentEditMode
            if (r3 != r10) goto L_0x02ce
            java.lang.String r0 = r9.filterPath
            if (r0 == 0) goto L_0x011a
            java.io.File r0 = new java.io.File
            java.lang.String r2 = r9.filterPath
            r0.<init>(r2)
            r0.delete()
        L_0x011a:
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r8)
            java.lang.String r0 = r0.toString()
            r7.currentImagePath = r0
            r9.filterPath = r0
            java.lang.String r0 = r9.paintPath
            if (r0 == 0) goto L_0x01dc
            boolean r0 = r38.hasAnimatedMediaEntities()
            if (r0 != 0) goto L_0x01dc
            boolean r0 = r7.isCurrentVideo
            if (r0 != 0) goto L_0x01dc
            java.lang.String r0 = r9.paintPath
            java.lang.String r1 = r9.fullPaintPath
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0146
            org.telegram.ui.Components.PaintingOverlay r0 = r7.paintingOverlay
            android.graphics.Bitmap r0 = r0.getBitmap()
            r1 = 0
            goto L_0x014d
        L_0x0146:
            java.lang.String r0 = r9.fullPaintPath
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0)
            r1 = 1
        L_0x014d:
            int r2 = r5.getWidth()
            int r3 = r5.getHeight()
            android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r2 = android.graphics.Bitmap.createBitmap(r2, r3, r4)
            android.graphics.Canvas r3 = new android.graphics.Canvas
            r3.<init>(r2)
            android.graphics.Rect r4 = new android.graphics.Rect
            int r6 = r5.getWidth()
            int r15 = r5.getHeight()
            r4.<init>(r13, r13, r6, r15)
            android.graphics.Paint r6 = r7.bitmapPaint
            r3.drawBitmap(r5, r12, r4, r6)
            android.graphics.Rect r4 = new android.graphics.Rect
            int r6 = r5.getWidth()
            int r15 = r5.getHeight()
            r4.<init>(r13, r13, r6, r15)
            android.graphics.Paint r6 = r7.bitmapPaint
            r3.drawBitmap(r0, r12, r4, r6)
            int r3 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r3 = (float) r3
            int r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r4 = (float) r4
            r18 = 87
            r19 = 0
            r20 = 101(0x65, float:1.42E-43)
            r21 = 101(0x65, float:1.42E-43)
            r15 = r2
            r16 = r3
            r17 = r4
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r15, (float) r16, (float) r17, (int) r18, (boolean) r19, (int) r20, (int) r21)
            java.lang.String r4 = r9.imagePath
            if (r4 == 0) goto L_0x01ad
            java.io.File r4 = new java.io.File
            java.lang.String r6 = r9.imagePath
            r4.<init>(r6)
            r4.delete()
        L_0x01ad:
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r8)
            java.lang.String r3 = r3.toString()
            r9.imagePath = r3
            r16 = 1140850688(0x44000000, float:512.0)
            r17 = 1140850688(0x44000000, float:512.0)
            r18 = 70
            r19 = 0
            r20 = 101(0x65, float:1.42E-43)
            r21 = 101(0x65, float:1.42E-43)
            r15 = r2
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r15, (float) r16, (float) r17, (int) r18, (boolean) r19, (int) r20, (int) r21)
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r8)
            java.lang.String r3 = r3.toString()
            r9.thumbPath = r3
            if (r1 == 0) goto L_0x01d7
            r0.recycle()
        L_0x01d7:
            r2.recycle()
            goto L_0x0256
        L_0x01dc:
            java.lang.String r0 = r9.paintPath
            if (r0 != 0) goto L_0x0259
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x020d }
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r11)     // Catch:{ Exception -> 0x020d }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x020d }
            r2.<init>()     // Catch:{ Exception -> 0x020d }
            int r3 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ Exception -> 0x020d }
            r2.append(r3)     // Catch:{ Exception -> 0x020d }
            r2.append(r15)     // Catch:{ Exception -> 0x020d }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x020d }
            r0.<init>(r1, r2)     // Catch:{ Exception -> 0x020d }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x020d }
            java.lang.String r2 = r9.filterPath     // Catch:{ Exception -> 0x020d }
            r1.<init>(r2)     // Catch:{ Exception -> 0x020d }
            org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r1, (java.io.File) r0)     // Catch:{ Exception -> 0x020d }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x020d }
            r9.thumbPath = r0     // Catch:{ Exception -> 0x020d }
            goto L_0x0211
        L_0x020d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0211:
            boolean r0 = r38.hasAnimatedMediaEntities()
            if (r0 != 0) goto L_0x0256
            java.lang.String r0 = r9.imagePath
            if (r0 == 0) goto L_0x0225
            java.io.File r0 = new java.io.File
            java.lang.String r1 = r9.imagePath
            r0.<init>(r1)
            r0.delete()
        L_0x0225:
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0252 }
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r11)     // Catch:{ Exception -> 0x0252 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0252 }
            r2.<init>()     // Catch:{ Exception -> 0x0252 }
            int r3 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ Exception -> 0x0252 }
            r2.append(r3)     // Catch:{ Exception -> 0x0252 }
            r2.append(r15)     // Catch:{ Exception -> 0x0252 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0252 }
            r0.<init>(r1, r2)     // Catch:{ Exception -> 0x0252 }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x0252 }
            java.lang.String r2 = r9.filterPath     // Catch:{ Exception -> 0x0252 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0252 }
            org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r1, (java.io.File) r0)     // Catch:{ Exception -> 0x0252 }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x0252 }
            r9.imagePath = r0     // Catch:{ Exception -> 0x0252 }
            goto L_0x0256
        L_0x0252:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0256:
            r11 = r5
            goto L_0x0757
        L_0x0259:
            java.io.File r0 = new java.io.File
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r11)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r3 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r2.append(r3)
            r2.append(r15)
            java.lang.String r2 = r2.toString()
            r0.<init>(r1, r2)
            boolean r1 = r7.isCurrentVideo
            if (r1 == 0) goto L_0x02a0
            org.telegram.messenger.MediaController$CropState r1 = r9.cropState
            if (r1 == 0) goto L_0x02a0
            java.lang.String r1 = r9.paintPath
            java.lang.String r2 = r9.fullPaintPath
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x028b
            java.lang.String r1 = r9.croppedPaintPath
            r2 = r12
            goto L_0x029b
        L_0x028b:
            java.lang.String r1 = r9.fullPaintPath
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeFile(r1)
            org.telegram.messenger.MediaController$CropState r2 = r9.cropState
            android.graphics.Bitmap r2 = r7.createCroppedBitmap(r1, r2)
            r1.recycle()
            r1 = r12
        L_0x029b:
            r3 = r1
            r15 = r2
            r16 = 1
            goto L_0x02b6
        L_0x02a0:
            java.lang.String r1 = r9.fullPaintPath
            java.lang.String r2 = r9.paintPath
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x02b1
            org.telegram.ui.Components.PaintingOverlay r2 = r7.paintingOverlay
            android.graphics.Bitmap r2 = r2.getThumb()
            goto L_0x02b2
        L_0x02b1:
            r2 = r12
        L_0x02b2:
            r3 = r1
            r15 = r2
            r16 = 0
        L_0x02b6:
            java.lang.String r2 = r0.getAbsolutePath()
            r9.thumbPath = r2
            r6 = 1
            r1 = r38
            r4 = r15
            r23 = r5
            r1.mergeThumbs(r2, r3, r4, r5, r6)
            if (r16 == 0) goto L_0x05be
            if (r15 == 0) goto L_0x05be
            r15.recycle()
            goto L_0x05be
        L_0x02ce:
            r23 = r5
            r5 = 3
            if (r3 != r5) goto L_0x05be
            java.lang.String r3 = r9.paintPath
            if (r3 == 0) goto L_0x02f5
            java.io.File r3 = new java.io.File
            java.lang.String r5 = r9.paintPath
            r3.<init>(r5)
            r3.delete()
            java.lang.String r3 = r9.paintPath
            java.lang.String r5 = r9.fullPaintPath
            boolean r3 = r3.equals(r5)
            if (r3 != 0) goto L_0x02f5
            java.io.File r3 = new java.io.File
            java.lang.String r5 = r9.fullPaintPath
            r3.<init>(r5)
            r3.delete()
        L_0x02f5:
            r9.stickers = r6
            org.telegram.ui.PhotoViewer$EditState r3 = r7.editState
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r8)
            java.lang.String r1 = r1.toString()
            r3.paintPath = r1
            r9.paintPath = r1
            org.telegram.ui.Components.PaintingOverlay r1 = r7.paintingOverlay
            org.telegram.ui.PhotoViewer$EditState r3 = r7.editState
            if (r4 == 0) goto L_0x0311
            boolean r5 = r4.isEmpty()
            if (r5 == 0) goto L_0x0312
        L_0x0311:
            r4 = r12
        L_0x0312:
            r3.mediaEntities = r4
            r9.mediaEntities = r4
            boolean r3 = r7.isCurrentVideo
            r1.setEntities(r4, r3, r8)
            org.telegram.ui.PhotoViewer$EditState r1 = r7.editState
            org.telegram.ui.Components.PhotoPaintView r3 = r7.photoPaintView
            long r3 = r3.getLcm()
            r1.averageDuration = r3
            r9.averageDuration = r3
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r1 = r9.mediaEntities
            if (r1 == 0) goto L_0x0358
            r1 = r0[r13]
            if (r1 == 0) goto L_0x0358
            r24 = r0[r13]
            android.graphics.Bitmap$CompressFormat r25 = android.graphics.Bitmap.CompressFormat.PNG
            int r1 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r1 = (float) r1
            int r3 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r3 = (float) r3
            r28 = 87
            r29 = 0
            r30 = 101(0x65, float:1.42E-43)
            r31 = 101(0x65, float:1.42E-43)
            r26 = r1
            r27 = r3
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r24, r25, r26, r27, r28, r29, r30, r31)
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r8)
            java.lang.String r1 = r1.toString()
            r9.fullPaintPath = r1
            goto L_0x035c
        L_0x0358:
            java.lang.String r1 = r9.paintPath
            r9.fullPaintPath = r1
        L_0x035c:
            org.telegram.ui.Components.PaintingOverlay r1 = r7.paintingOverlay
            r6 = r23
            r1.setBitmap(r6)
            boolean r1 = r7.isCurrentVideo
            if (r1 == 0) goto L_0x0426
            org.telegram.messenger.MediaController$CropState r1 = r9.cropState
            if (r1 == 0) goto L_0x0426
            org.telegram.ui.PhotoViewer$EditState r1 = r7.editState
            java.lang.String r1 = r1.paintPath
            java.lang.String r1 = org.telegram.ui.Components.Crop.CropView.getPathOrCopy(r8, r1)
            org.telegram.ui.PhotoViewer$EditState r3 = r7.editState
            java.lang.String r3 = r3.croppedPaintPath
            if (r3 == 0) goto L_0x0389
            java.io.File r3 = new java.io.File
            org.telegram.ui.PhotoViewer$EditState r4 = r7.editState
            java.lang.String r4 = r4.croppedPaintPath
            r3.<init>(r4)
            r3.delete()
            org.telegram.ui.PhotoViewer$EditState r3 = r7.editState
            r3.croppedPaintPath = r12
        L_0x0389:
            org.telegram.ui.PhotoViewer$EditState r3 = r7.editState
            r3.croppedPaintPath = r1
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r3 = r3.mediaEntities
            if (r3 == 0) goto L_0x03ca
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x03ca
            org.telegram.ui.PhotoViewer$EditState r3 = r7.editState
            java.util.ArrayList r4 = new java.util.ArrayList
            org.telegram.ui.PhotoViewer$EditState r5 = r7.editState
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r5 = r5.mediaEntities
            int r5 = r5.size()
            r4.<init>(r5)
            r3.croppedMediaEntities = r4
            org.telegram.ui.PhotoViewer$EditState r3 = r7.editState
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r3 = r3.mediaEntities
            int r3 = r3.size()
            r4 = 0
        L_0x03b1:
            if (r4 >= r3) goto L_0x03ce
            org.telegram.ui.PhotoViewer$EditState r5 = r7.editState
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r10 = r5.croppedMediaEntities
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r5 = r5.mediaEntities
            java.lang.Object r5 = r5.get(r4)
            org.telegram.messenger.VideoEditedInfo$MediaEntity r5 = (org.telegram.messenger.VideoEditedInfo.MediaEntity) r5
            org.telegram.messenger.VideoEditedInfo$MediaEntity r5 = r5.copy()
            r10.add(r5)
            int r4 = r4 + 1
            r10 = 2
            goto L_0x03b1
        L_0x03ca:
            org.telegram.ui.PhotoViewer$EditState r3 = r7.editState
            r3.croppedMediaEntities = r12
        L_0x03ce:
            org.telegram.messenger.MediaController$CropState r3 = r9.cropState
            int r4 = r3.width
            int r3 = r3.height
            android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r3 = android.graphics.Bitmap.createBitmap(r4, r3, r5)
            android.graphics.Canvas r4 = new android.graphics.Canvas
            r4.<init>(r3)
            android.view.TextureView r5 = r7.videoTextureView
            org.telegram.ui.Components.VideoEditTextureView r5 = (org.telegram.ui.Components.VideoEditTextureView) r5
            r24 = 0
            android.graphics.Bitmap$CompressFormat r27 = android.graphics.Bitmap.CompressFormat.PNG
            org.telegram.messenger.MediaController$CropState r10 = r9.cropState
            android.graphics.Matrix r10 = r10.matrix
            int r29 = r5.getVideoWidth()
            int r30 = r5.getVideoHeight()
            org.telegram.messenger.MediaController$CropState r5 = r9.cropState
            float r11 = r5.stateScale
            float r2 = r5.cropRotate
            int r13 = r5.transformRotation
            float r13 = (float) r13
            float r5 = r5.scale
            org.telegram.ui.PhotoViewer$EditState r8 = r7.editState
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r8 = r8.croppedMediaEntities
            r36 = 0
            r23 = r1
            r25 = r4
            r26 = r3
            r28 = r10
            r31 = r11
            r32 = r2
            r33 = r13
            r34 = r5
            r35 = r8
            org.telegram.ui.Components.Crop.CropView.editBitmap(r23, r24, r25, r26, r27, r28, r29, r30, r31, r32, r33, r34, r35, r36)
            r3.recycle()
            org.telegram.ui.PhotoViewer$EditState r1 = r7.editState
            java.lang.String r2 = r1.croppedPaintPath
            r9.croppedPaintPath = r2
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r1 = r1.croppedMediaEntities
            r9.croppedMediaEntities = r1
        L_0x0426:
            boolean r1 = r38.hasAnimatedMediaEntities()
            if (r1 != 0) goto L_0x04d0
            boolean r1 = r7.isCurrentVideo
            if (r1 != 0) goto L_0x04d0
            java.lang.String r1 = r9.filterPath
            if (r1 == 0) goto L_0x0435
            goto L_0x043e
        L_0x0435:
            java.lang.String r1 = r9.croppedPath
            if (r1 == 0) goto L_0x043a
            goto L_0x043e
        L_0x043a:
            java.lang.String r1 = r9.getPath()
        L_0x043e:
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r3 = (float) r3
            r4 = 1
            android.graphics.Bitmap r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r12, r2, r3, r4)
            if (r1 == 0) goto L_0x045b
            boolean r2 = r1.isMutable()
            if (r2 != 0) goto L_0x045b
            android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r1 = r1.copy(r2, r4)
        L_0x045b:
            if (r1 == 0) goto L_0x04cd
            android.graphics.Canvas r2 = new android.graphics.Canvas
            r2.<init>(r1)
            r3 = 0
            r4 = r0[r3]
            if (r4 == 0) goto L_0x046a
            r5 = r0[r3]
            goto L_0x046b
        L_0x046a:
            r5 = r6
        L_0x046b:
            android.graphics.Rect r0 = new android.graphics.Rect
            int r4 = r1.getWidth()
            int r8 = r1.getHeight()
            r0.<init>(r3, r3, r4, r8)
            android.graphics.Paint r3 = r7.bitmapPaint
            r2.drawBitmap(r5, r12, r0, r3)
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r0 = (float) r0
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r2 = (float) r2
            r18 = 87
            r19 = 0
            r20 = 101(0x65, float:1.42E-43)
            r21 = 101(0x65, float:1.42E-43)
            r15 = r1
            r16 = r0
            r17 = r2
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r15, (float) r16, (float) r17, (int) r18, (boolean) r19, (int) r20, (int) r21)
            java.lang.String r2 = r9.imagePath
            if (r2 == 0) goto L_0x04a6
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r9.imagePath
            r2.<init>(r3)
            r2.delete()
        L_0x04a6:
            r2 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r2)
            java.lang.String r0 = r0.toString()
            r9.imagePath = r0
            r16 = 1140850688(0x44000000, float:512.0)
            r17 = 1140850688(0x44000000, float:512.0)
            r18 = 70
            r19 = 0
            r20 = 101(0x65, float:1.42E-43)
            r21 = 101(0x65, float:1.42E-43)
            r15 = r1
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r15, (float) r16, (float) r17, (int) r18, (boolean) r19, (int) r20, (int) r21)
            r1 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r1)
            java.lang.String r0 = r0.toString()
            r9.thumbPath = r0
        L_0x04cd:
            r11 = r6
            goto L_0x0757
        L_0x04d0:
            r1 = 0
            r2 = r0[r1]
            if (r2 == 0) goto L_0x04d8
            r5 = r0[r1]
            goto L_0x04d9
        L_0x04d8:
            r5 = r6
        L_0x04d9:
            boolean r0 = r7.isCurrentVideo
            if (r0 == 0) goto L_0x04e8
            org.telegram.messenger.MediaController$CropState r0 = r9.cropState
            if (r0 == 0) goto L_0x04e8
            android.graphics.Bitmap r5 = r7.createCroppedBitmap(r5, r0)
            r0 = r5
            r8 = 1
            goto L_0x04ea
        L_0x04e8:
            r0 = r5
            r8 = 0
        L_0x04ea:
            java.lang.String r1 = r9.filterPath
            if (r1 != 0) goto L_0x0586
            java.lang.String r1 = r9.imagePath
            if (r1 == 0) goto L_0x04fe
            java.io.File r1 = new java.io.File
            java.lang.String r2 = r9.imagePath
            r1.<init>(r2)
            r1.delete()
            r9.imagePath = r12
        L_0x04fe:
            boolean r1 = r7.isCurrentVideo
            if (r1 == 0) goto L_0x0530
            android.view.TextureView r1 = r7.videoTextureView
            org.telegram.ui.Components.VideoEditTextureView r1 = (org.telegram.ui.Components.VideoEditTextureView) r1
            java.lang.String r1 = r9.getPath()
            org.telegram.ui.Components.VideoTimelinePlayView r2 = r7.videoTimelineView
            float r2 = r2.getLeftProgress()
            org.telegram.ui.Components.VideoPlayer r3 = r7.videoPlayer
            long r3 = r3.getDuration()
            float r3 = (float) r3
            float r2 = r2 * r3
            r3 = 1148846080(0x447a0000, float:1000.0)
            float r2 = r2 * r3
            long r2 = (long) r2
            android.graphics.Bitmap r1 = org.telegram.messenger.SendMessagesHelper.createVideoThumbnailAtTime(r1, r2)
            org.telegram.messenger.MediaController$CropState r2 = r9.cropState
            if (r2 == 0) goto L_0x052e
            android.graphics.Bitmap r2 = r7.createCroppedBitmap(r1, r2)
            r1.recycle()
            r1 = r2
        L_0x052e:
            r15 = r1
            goto L_0x054f
        L_0x0530:
            java.lang.String r1 = r9.croppedPath
            if (r1 == 0) goto L_0x0535
            goto L_0x0539
        L_0x0535:
            java.lang.String r1 = r9.getPath()
        L_0x0539:
            r2 = 1140850688(0x44000000, float:512.0)
            r3 = 1
            android.graphics.Bitmap r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r12, r2, r2, r3)
            if (r1 == 0) goto L_0x052e
            boolean r2 = r1.isMutable()
            if (r2 != 0) goto L_0x052e
            android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r1 = r1.copy(r2, r3)
            goto L_0x052e
        L_0x054f:
            if (r15 == 0) goto L_0x0584
            android.graphics.Canvas r1 = new android.graphics.Canvas
            r1.<init>(r15)
            android.graphics.Rect r2 = new android.graphics.Rect
            int r3 = r15.getWidth()
            int r4 = r15.getHeight()
            r5 = 0
            r2.<init>(r5, r5, r3, r4)
            android.graphics.Paint r3 = r7.bitmapPaint
            r1.drawBitmap(r0, r12, r2, r3)
            r16 = 1140850688(0x44000000, float:512.0)
            r17 = 1140850688(0x44000000, float:512.0)
            r18 = 70
            r19 = 0
            r20 = 101(0x65, float:1.42E-43)
            r21 = 101(0x65, float:1.42E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r15, (float) r16, (float) r17, (int) r18, (boolean) r19, (int) r20, (int) r21)
            r2 = 1
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r2)
            java.lang.String r1 = r1.toString()
            r9.thumbPath = r1
        L_0x0584:
            r11 = r6
            goto L_0x05b5
        L_0x0586:
            java.io.File r1 = new java.io.File
            r2 = 4
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r4 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r2.append(r4)
            r2.append(r15)
            java.lang.String r2 = r2.toString()
            r1.<init>(r3, r2)
            java.lang.String r2 = r1.getAbsolutePath()
            r9.thumbPath = r2
            java.lang.String r3 = r9.filterPath
            r4 = 0
            r10 = 0
            r1 = r38
            r5 = r0
            r11 = r6
            r6 = r10
            r1.mergeThumbs(r2, r3, r4, r5, r6)
        L_0x05b5:
            if (r8 == 0) goto L_0x0757
            if (r0 == 0) goto L_0x0757
            r0.recycle()
            goto L_0x0757
        L_0x05be:
            r11 = r23
            goto L_0x0757
        L_0x05c2:
            r11 = r5
            boolean r0 = r7.isCurrentVideo
            if (r0 == 0) goto L_0x05d6
            org.telegram.ui.PhotoViewer$EditState r0 = r7.editState
            org.telegram.messenger.MediaController$CropState r1 = r9.cropState
            r0.cropState = r1
            java.lang.String r1 = r9.croppedPaintPath
            r0.croppedPaintPath = r1
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r1 = r9.croppedMediaEntities
            r0.croppedMediaEntities = r1
            goto L_0x060f
        L_0x05d6:
            java.lang.String r0 = r9.filterPath
            if (r0 != 0) goto L_0x05f6
            java.lang.String r0 = r9.croppedPath
            if (r0 == 0) goto L_0x05e8
            java.io.File r0 = new java.io.File
            java.lang.String r2 = r9.croppedPath
            r0.<init>(r2)
            r0.delete()
        L_0x05e8:
            r2 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r2)
            java.lang.String r0 = r0.toString()
            r7.currentImagePath = r0
            r9.croppedPath = r0
            goto L_0x060f
        L_0x05f6:
            if (r0 == 0) goto L_0x0602
            java.io.File r0 = new java.io.File
            java.lang.String r2 = r9.filterPath
            r0.<init>(r2)
            r0.delete()
        L_0x0602:
            r2 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r2)
            java.lang.String r0 = r0.toString()
            r7.currentImagePath = r0
            r9.filterPath = r0
        L_0x060f:
            java.lang.String r0 = r9.imagePath
            if (r0 == 0) goto L_0x061d
            java.io.File r0 = new java.io.File
            java.lang.String r1 = r9.imagePath
            r0.<init>(r1)
            r0.delete()
        L_0x061d:
            java.lang.String r0 = r9.paintPath
            if (r0 == 0) goto L_0x0708
            java.io.File r0 = new java.io.File
            r1 = 4
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r3 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r1.append(r3)
            r1.append(r15)
            java.lang.String r1 = r1.toString()
            r0.<init>(r2, r1)
            boolean r1 = r7.isCurrentVideo
            if (r1 != 0) goto L_0x06e6
            java.lang.String r2 = r0.getAbsolutePath()
            r9.thumbPath = r2
            java.lang.String r3 = r9.fullPaintPath
            r4 = 0
            r6 = 1
            r1 = r38
            r5 = r11
            r1.mergeThumbs(r2, r3, r4, r5, r6)
            java.lang.String r0 = r9.paintPath
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0)
            org.telegram.ui.Components.PaintingOverlay r1 = r7.paintingOverlay
            r1.setBitmap(r0)
            java.lang.String r1 = r9.fullPaintPath
            if (r1 == 0) goto L_0x0678
            java.lang.String r2 = r9.paintPath
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0678
            boolean r0 = r38.hasAnimatedMediaEntities()
            if (r0 == 0) goto L_0x0672
            java.lang.String r0 = r9.paintPath
            goto L_0x0674
        L_0x0672:
            java.lang.String r0 = r9.fullPaintPath
        L_0x0674:
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0)
        L_0x0678:
            int r1 = r11.getWidth()
            int r2 = r11.getHeight()
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r1, r2, r3)
            android.graphics.Canvas r2 = new android.graphics.Canvas
            r2.<init>(r1)
            android.graphics.Rect r3 = new android.graphics.Rect
            int r4 = r11.getWidth()
            int r5 = r11.getHeight()
            r6 = 0
            r3.<init>(r6, r6, r4, r5)
            android.graphics.Paint r4 = r7.bitmapPaint
            r2.drawBitmap(r11, r12, r3, r4)
            android.graphics.Rect r3 = new android.graphics.Rect
            int r4 = r11.getWidth()
            int r5 = r11.getHeight()
            r3.<init>(r6, r6, r4, r5)
            android.graphics.Paint r4 = r7.bitmapPaint
            r2.drawBitmap(r0, r12, r3, r4)
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r0 = (float) r0
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r2 = (float) r2
            r18 = 87
            r19 = 0
            r20 = 101(0x65, float:1.42E-43)
            r21 = 101(0x65, float:1.42E-43)
            r15 = r1
            r16 = r0
            r17 = r2
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r15, (float) r16, (float) r17, (int) r18, (boolean) r19, (int) r20, (int) r21)
            r2 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r2)
            java.lang.String r0 = r0.toString()
            r9.imagePath = r0
            r1.recycle()
            org.telegram.ui.Components.PaintingOverlay r0 = r7.paintingOverlay
            org.telegram.ui.PhotoViewer$EditState r1 = r7.editState
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r1 = r1.mediaEntities
            boolean r3 = r7.isCurrentVideo
            r0.setEntities(r1, r3, r2)
            goto L_0x0757
        L_0x06e6:
            java.lang.String r1 = r9.fullPaintPath
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeFile(r1)
            org.telegram.messenger.MediaController$CropState r2 = r9.cropState
            android.graphics.Bitmap r8 = r7.createCroppedBitmap(r1, r2)
            r1.recycle()
            java.lang.String r2 = r0.getAbsolutePath()
            r9.thumbPath = r2
            r3 = 0
            r6 = 1
            r1 = r38
            r4 = r8
            r5 = r11
            r1.mergeThumbs(r2, r3, r4, r5, r6)
            r8.recycle()
            goto L_0x0757
        L_0x0708:
            r16 = 1140850688(0x44000000, float:512.0)
            r17 = 1140850688(0x44000000, float:512.0)
            r18 = 70
            r19 = 0
            r20 = 101(0x65, float:1.42E-43)
            r21 = 101(0x65, float:1.42E-43)
            r1 = r15
            r15 = r11
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r15, (float) r16, (float) r17, (int) r18, (boolean) r19, (int) r20, (int) r21)
            r2 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r2)
            java.lang.String r0 = r0.toString()
            r9.thumbPath = r0
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0753 }
            r2 = 4
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r2)     // Catch:{ Exception -> 0x0753 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0753 }
            r2.<init>()     // Catch:{ Exception -> 0x0753 }
            int r4 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ Exception -> 0x0753 }
            r2.append(r4)     // Catch:{ Exception -> 0x0753 }
            r2.append(r1)     // Catch:{ Exception -> 0x0753 }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x0753 }
            r0.<init>(r3, r1)     // Catch:{ Exception -> 0x0753 }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x0753 }
            java.lang.String r2 = r9.croppedPath     // Catch:{ Exception -> 0x0753 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0753 }
            org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r1, (java.io.File) r0)     // Catch:{ Exception -> 0x0753 }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x0753 }
            r9.imagePath = r0     // Catch:{ Exception -> 0x0753 }
            goto L_0x0757
        L_0x0753:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0757:
            org.telegram.messenger.SharedConfig.saveConfig()
            if (r14 == 0) goto L_0x0762
            org.telegram.ui.PhotoViewer$EditState r0 = r7.editState
            r0.savedFilterState = r14
            r9.savedFilterState = r14
        L_0x0762:
            int r0 = r7.currentEditMode
            java.lang.String r1 = "dialogFloatingButton"
            r2 = 1
            if (r0 != r2) goto L_0x077c
            r9.isCropped = r2
            android.widget.ImageView r0 = r7.cropItem
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r1, r4)
            r0.setColorFilter(r3)
            goto L_0x07a7
        L_0x077c:
            r3 = 2
            if (r0 != r3) goto L_0x0792
            r9.isFiltered = r2
            android.widget.ImageView r0 = r7.tuneItem
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r1, r4)
            r0.setColorFilter(r3)
            goto L_0x07a7
        L_0x0792:
            r3 = 3
            if (r0 != r3) goto L_0x07a7
            r9.isPainted = r2
            android.widget.ImageView r0 = r7.paintItem
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r1, r3)
            r0.setColorFilter(r2)
        L_0x07a7:
            int r0 = r7.sendPhotoType
            if (r0 == 0) goto L_0x07ae
            r1 = 4
            if (r0 != r1) goto L_0x07c4
        L_0x07ae:
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r0 = r7.placeProvider
            if (r0 == 0) goto L_0x07c4
            int r1 = r7.currentIndex
            r0.updatePhotoAtIndex(r1)
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r0 = r7.placeProvider
            int r1 = r7.currentIndex
            boolean r0 = r0.isPhotoChecked(r1)
            if (r0 != 0) goto L_0x07c4
            r38.setPhotoChecked()
        L_0x07c4:
            int r0 = r7.currentEditMode
            r1 = 1
            if (r0 != r1) goto L_0x0820
            org.telegram.ui.Components.PhotoCropView r0 = r7.photoCropView
            float r0 = r0.getRectSizeX()
            int r1 = r38.getContainerViewWidth()
            float r1 = (float) r1
            float r0 = r0 / r1
            org.telegram.ui.Components.PhotoCropView r1 = r7.photoCropView
            float r1 = r1.getRectSizeY()
            int r2 = r38.getContainerViewHeight()
            float r2 = (float) r2
            float r1 = r1 / r2
            float r0 = java.lang.Math.max(r0, r1)
            r7.scale = r0
            org.telegram.ui.Components.PhotoCropView r0 = r7.photoCropView
            float r0 = r0.getRectX()
            org.telegram.ui.Components.PhotoCropView r1 = r7.photoCropView
            float r1 = r1.getRectSizeX()
            r2 = 1073741824(0x40000000, float:2.0)
            float r1 = r1 / r2
            float r0 = r0 + r1
            int r1 = r38.getContainerViewWidth()
            r3 = 2
            int r1 = r1 / r3
            float r1 = (float) r1
            float r0 = r0 - r1
            r7.translationX = r0
            org.telegram.ui.Components.PhotoCropView r0 = r7.photoCropView
            float r0 = r0.getRectY()
            org.telegram.ui.Components.PhotoCropView r1 = r7.photoCropView
            float r1 = r1.getRectSizeY()
            float r1 = r1 / r2
            float r0 = r0 + r1
            int r1 = r38.getContainerViewHeight()
            int r1 = r1 / r3
            float r1 = (float) r1
            float r0 = r0 - r1
            r7.translationY = r0
            r1 = 1
            r7.zoomAnimation = r1
            org.telegram.ui.Components.PhotoCropView r0 = r7.photoCropView
            r0.onDisappear()
        L_0x0820:
            org.telegram.messenger.ImageReceiver r0 = r7.centerImage
            r0.setParentView(r12)
            r7.ignoreDidSetImage = r1
            boolean r0 = r7.isCurrentVideo
            if (r0 != 0) goto L_0x0830
            int r0 = r7.currentEditMode
            r2 = 3
            if (r0 != r2) goto L_0x0834
        L_0x0830:
            int r0 = r7.currentEditMode
            if (r0 != r1) goto L_0x0845
        L_0x0834:
            org.telegram.messenger.ImageReceiver r0 = r7.centerImage
            r0.setImageBitmap((android.graphics.Bitmap) r11)
            org.telegram.messenger.ImageReceiver r0 = r7.centerImage
            r2 = 0
            r0.setOrientation(r2, r1)
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r0 = r7.containerView
            r0.requestLayout()
            goto L_0x0846
        L_0x0845:
            r2 = 0
        L_0x0846:
            r7.ignoreDidSetImage = r2
            org.telegram.messenger.ImageReceiver r0 = r7.centerImage
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r2 = r7.containerView
            r0.setParentView(r2)
            int r0 = r7.sendPhotoType
            if (r0 != r1) goto L_0x0856
            r38.setCropBitmap()
        L_0x0856:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.applyCurrentEditMode():void");
    }

    private void setPhotoChecked() {
        ChatActivity chatActivity;
        TLRPC$Chat currentChat;
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
                    if (photoChecked == 0) {
                        this.selectedPhotosAdapter.notifyItemChanged(0);
                    }
                }
            }
            updateSelectedCount();
        } else if (this.allowOrder && (chatActivity = this.parentChatActivity) != null && (currentChat = chatActivity.getCurrentChat()) != null && !ChatObject.hasAdminRights(currentChat) && currentChat.slowmode_enabled) {
            AlertsCreator.createSimpleAlert(this.parentActivity, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSelectSendError", NUM)).show();
        }
    }

    private void createCropView() {
        if (this.photoCropView == null) {
            PhotoCropView photoCropView2 = new PhotoCropView(this.actvityContext);
            this.photoCropView = photoCropView2;
            photoCropView2.setVisibility(8);
            this.photoCropView.onDisappear();
            this.containerView.addView(this.photoCropView, this.containerView.indexOfChild(this.pickerViewSendButton) - 1, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
            this.photoCropView.setDelegate(new PhotoCropView.PhotoCropViewDelegate() {
                public void onChange(boolean z) {
                    PhotoViewer.this.resetButton.setVisibility(z ? 8 : 0);
                }

                public void onUpdate() {
                    PhotoViewer.this.containerView.invalidate();
                }
            });
        }
    }

    private void startVideoPlayer() {
        VideoPlayer videoPlayer2;
        if (this.isCurrentVideo && (videoPlayer2 = this.videoPlayer) != null && !videoPlayer2.isPlaying()) {
            if (!this.muteVideo) {
                this.videoPlayer.setVolume(0.0f);
            }
            toggleVideoPlayer();
        }
    }

    private void switchToEditMode(int i) {
        float f;
        int i2;
        int i3;
        int i4;
        MediaController.SavedFilterState savedFilterState;
        int i5;
        String str;
        int i6;
        Bitmap bitmap;
        Bitmap bitmap2;
        final int i7 = i;
        if (this.currentEditMode == i7) {
            return;
        }
        if ((!this.isCurrentVideo || this.photoProgressViews[0].backgroundState == 3 || this.isCurrentVideo || (this.centerImage.getBitmap() != null && this.photoProgressViews[0].backgroundState == -1)) && this.changeModeAnimation == null && this.imageMoveAnimation == null && this.captionEditText.getTag() == null) {
            this.switchingToMode = i7;
            if (i7 == 0) {
                if (this.centerImage.getBitmap() != null) {
                    int bitmapWidth = this.centerImage.getBitmapWidth();
                    int bitmapHeight = this.centerImage.getBitmapHeight();
                    MediaController.CropState cropState = this.editState.cropState;
                    if (cropState != null) {
                        int i8 = cropState.transformRotation;
                        if (i8 == 90 || i8 == 270) {
                            int i9 = bitmapHeight;
                            bitmapHeight = bitmapWidth;
                            bitmapWidth = i9;
                        }
                        MediaController.CropState cropState2 = this.editState.cropState;
                        bitmapWidth = (int) (((float) bitmapWidth) * cropState2.cropPw);
                        bitmapHeight = (int) (((float) bitmapHeight) * cropState2.cropPh);
                    }
                    float f2 = (float) bitmapWidth;
                    float f3 = (float) bitmapHeight;
                    float containerViewWidth = ((float) getContainerViewWidth(0)) / f2;
                    float containerViewHeight = ((float) getContainerViewHeight(0)) / f3;
                    float min = Math.min(((float) getContainerViewWidth()) / f2, ((float) getContainerViewHeight()) / f3);
                    float min2 = Math.min(containerViewWidth, containerViewHeight);
                    if (this.sendPhotoType == 1) {
                        setCropTranslations(true);
                    } else {
                        this.animateToScale = min2 / min;
                        this.animateToX = 0.0f;
                        this.translationX = (float) ((getLeftInset() / 2) - (getRightInset() / 2));
                        int i10 = this.currentEditMode;
                        if (i10 == 1) {
                            this.animateToY = (float) AndroidUtilities.dp(58.0f);
                        } else if (i10 == 2) {
                            this.animateToY = (float) AndroidUtilities.dp(92.0f);
                        } else if (i10 == 3) {
                            this.animateToY = (float) AndroidUtilities.dp(44.0f);
                        }
                        if (Build.VERSION.SDK_INT >= 21) {
                            this.animateToY -= (float) (AndroidUtilities.statusBarHeight / 2);
                        }
                        this.animationStartTime = System.currentTimeMillis();
                        this.zoomAnimation = true;
                    }
                }
                this.padImageForHorizontalInsets = false;
                this.imageMoveAnimation = new AnimatorSet();
                ArrayList arrayList = new ArrayList(4);
                int i11 = this.currentEditMode;
                if (i11 == 1) {
                    arrayList.add(ObjectAnimator.ofFloat(this.editorDoneLayout, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(48.0f)}));
                    arrayList.add(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.photoCropView, View.ALPHA, new float[]{0.0f}));
                } else if (i11 == 2) {
                    this.photoFilterView.shutdown();
                    arrayList.add(ObjectAnimator.ofFloat(this.photoFilterView.getToolsView(), View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(186.0f)}));
                    arrayList.add(ObjectAnimator.ofFloat(this.photoFilterView.getCurveControl(), View.ALPHA, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.photoFilterView.getBlurControl(), View.ALPHA, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f}));
                } else if (i11 == 3) {
                    this.paintingOverlay.showAll();
                    this.containerView.invalidate();
                    this.photoPaintView.shutdown();
                    arrayList.add(ObjectAnimator.ofFloat(this.photoPaintView.getToolsView(), View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(126.0f)}));
                    arrayList.add(ObjectAnimator.ofFloat(this.photoPaintView.getColorPicker(), View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(126.0f)}));
                    arrayList.add(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f}));
                }
                this.imageMoveAnimation.playTogether(arrayList);
                this.imageMoveAnimation.setDuration(200);
                this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (PhotoViewer.this.currentEditMode == 1) {
                            PhotoViewer.this.photoCropView.onDisappear();
                            PhotoViewer.this.photoCropView.onHide();
                            PhotoViewer.this.editorDoneLayout.setVisibility(8);
                            PhotoViewer.this.photoCropView.setVisibility(8);
                        } else if (PhotoViewer.this.currentEditMode == 2) {
                            try {
                                PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoFilterView);
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                            PhotoFilterView unused = PhotoViewer.this.photoFilterView = null;
                        } else if (PhotoViewer.this.currentEditMode == 3) {
                            try {
                                PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoPaintView);
                            } catch (Exception e2) {
                                FileLog.e((Throwable) e2);
                            }
                            PhotoPaintView unused2 = PhotoViewer.this.photoPaintView = null;
                        }
                        AnimatorSet unused3 = PhotoViewer.this.imageMoveAnimation = null;
                        int unused4 = PhotoViewer.this.currentEditMode = i7;
                        int unused5 = PhotoViewer.this.switchingToMode = -1;
                        boolean unused6 = PhotoViewer.this.applying = false;
                        if (PhotoViewer.this.sendPhotoType == 1) {
                            PhotoViewer.this.photoCropView.setVisibility(0);
                        } else {
                            float unused7 = PhotoViewer.this.animateToScale = 1.0f;
                            float unused8 = PhotoViewer.this.animateToX = 0.0f;
                            float unused9 = PhotoViewer.this.animateToY = 0.0f;
                            float unused10 = PhotoViewer.this.scale = 1.0f;
                        }
                        PhotoViewer photoViewer = PhotoViewer.this;
                        photoViewer.updateMinMax(photoViewer.scale);
                        PhotoViewer.this.containerView.invalidate();
                        AnimatorSet animatorSet = new AnimatorSet();
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, View.TRANSLATION_Y, new float[]{0.0f}));
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerViewSendButton, View.TRANSLATION_Y, new float[]{0.0f}));
                        if (PhotoViewer.this.sendPhotoType != 1) {
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, View.TRANSLATION_Y, new float[]{0.0f}));
                        }
                        if (PhotoViewer.this.needCaptionLayout) {
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.captionTextViewSwitcher, View.TRANSLATION_Y, new float[]{0.0f}));
                        }
                        if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4) {
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.checkImageView, View.ALPHA, new float[]{1.0f}));
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.photosCounterView, View.ALPHA, new float[]{1.0f}));
                        } else if (PhotoViewer.this.sendPhotoType == 1) {
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, View.ALPHA, new float[]{1.0f}));
                        }
                        if (PhotoViewer.this.cameraItem.getTag() != null) {
                            PhotoViewer.this.cameraItem.setVisibility(0);
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.cameraItem, View.ALPHA, new float[]{1.0f}));
                        }
                        if (PhotoViewer.this.muteItem.getTag() != null) {
                            PhotoViewer.this.muteItem.setVisibility(0);
                            arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.muteItem, View.ALPHA, new float[]{1.0f}));
                        }
                        animatorSet.playTogether(arrayList);
                        animatorSet.setDuration(200);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationStart(Animator animator) {
                                PhotoViewer.this.pickerView.setVisibility(0);
                                PhotoViewer.this.pickerViewSendButton.setVisibility(0);
                                PhotoViewer.this.actionBar.setVisibility(0);
                                if (PhotoViewer.this.needCaptionLayout) {
                                    PhotoViewer.this.captionTextViewSwitcher.setVisibility(PhotoViewer.this.captionTextViewSwitcher.getTag() != null ? 0 : 4);
                                }
                                if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4 || ((PhotoViewer.this.sendPhotoType == 2 || PhotoViewer.this.sendPhotoType == 5) && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                                    PhotoViewer.this.checkImageView.setVisibility(0);
                                    PhotoViewer.this.photosCounterView.setVisibility(0);
                                } else if (PhotoViewer.this.sendPhotoType == 1) {
                                    PhotoViewer.this.setCropTranslations(false);
                                }
                            }
                        });
                        animatorSet.start();
                    }
                });
                this.imageMoveAnimation.start();
            } else if (i7 == 1) {
                startVideoPlayer();
                createCropView();
                if (this.isCurrentVideo) {
                    VideoEditTextureView videoEditTextureView = (VideoEditTextureView) this.videoTextureView;
                    this.previousHasTransform = videoEditTextureView.hasViewTransform();
                    this.previousCropPx = videoEditTextureView.getCropPx();
                    this.previousCropPy = videoEditTextureView.getCropPy();
                    this.previousCropScale = videoEditTextureView.getScale();
                    this.previousCropRotation = videoEditTextureView.getRotation();
                    this.previousCropOrientation = videoEditTextureView.getOrientation();
                    this.previousCropPw = videoEditTextureView.getCropPw();
                    this.previousCropPh = videoEditTextureView.getCropPh();
                }
                this.photoCropView.onAppear();
                this.editorDoneLayout.doneButton.setText(LocaleController.getString("Crop", NUM));
                this.editorDoneLayout.doneButton.setTextColor(Theme.getColor("dialogFloatingButton"));
                this.changeModeAnimation = new AnimatorSet();
                ArrayList arrayList2 = new ArrayList();
                FrameLayout frameLayout = this.pickerView;
                Property property = View.TRANSLATION_Y;
                float[] fArr = new float[2];
                fArr[0] = 0.0f;
                fArr[1] = (float) AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList2.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
                ImageView imageView = this.pickerViewSendButton;
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[2];
                fArr2[0] = 0.0f;
                fArr2[1] = (float) AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList2.add(ObjectAnimator.ofFloat(imageView, property2, fArr2));
                ActionBar actionBar2 = this.actionBar;
                arrayList2.add(ObjectAnimator.ofFloat(actionBar2, View.TRANSLATION_Y, new float[]{0.0f, (float) (-actionBar2.getHeight())}));
                if (this.needCaptionLayout) {
                    CaptionTextViewSwitcher captionTextViewSwitcher2 = this.captionTextViewSwitcher;
                    Property property3 = View.TRANSLATION_Y;
                    float[] fArr3 = new float[2];
                    fArr3[0] = 0.0f;
                    fArr3[1] = (float) AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                    arrayList2.add(ObjectAnimator.ofFloat(captionTextViewSwitcher2, property3, fArr3));
                }
                int i12 = this.sendPhotoType;
                if (i12 == 0 || i12 == 4) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.checkImageView, View.ALPHA, new float[]{1.0f, 0.0f}));
                    arrayList2.add(ObjectAnimator.ofFloat(this.photosCounterView, View.ALPHA, new float[]{1.0f, 0.0f}));
                }
                if (this.selectedPhotosListView.getVisibility() == 0) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.selectedPhotosListView, View.ALPHA, new float[]{1.0f, 0.0f}));
                }
                if (this.cameraItem.getTag() != null) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.cameraItem, View.ALPHA, new float[]{1.0f, 0.0f}));
                }
                if (this.muteItem.getTag() != null) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.muteItem, View.ALPHA, new float[]{1.0f, 0.0f}));
                }
                this.changeModeAnimation.playTogether(arrayList2);
                this.changeModeAnimation.setDuration(200);
                this.changeModeAnimation.addListener(new AnimatorListenerAdapter() {
                    /* JADX WARNING: type inference failed for: r1v65, types: [android.view.TextureView] */
                    /* JADX WARNING: Multi-variable type inference failed */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void onAnimationEnd(android.animation.Animator r19) {
                        /*
                            r18 = this;
                            r0 = r18
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            r2 = 0
                            android.animation.AnimatorSet unused = r1.changeModeAnimation = r2
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            android.widget.FrameLayout r1 = r1.pickerView
                            r3 = 8
                            r1.setVisibility(r3)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            android.widget.ImageView r1 = r1.pickerViewSendButton
                            r1.setVisibility(r3)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            android.widget.ImageView r1 = r1.cameraItem
                            r1.setVisibility(r3)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            android.widget.ImageView r1 = r1.muteItem
                            r1.setVisibility(r3)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.PhotoViewer$SelectedPhotosListView r1 = r1.selectedPhotosListView
                            r1.setVisibility(r3)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.PhotoViewer$SelectedPhotosListView r1 = r1.selectedPhotosListView
                            r4 = 0
                            r1.setAlpha(r4)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.PhotoViewer$SelectedPhotosListView r1 = r1.selectedPhotosListView
                            r5 = 1092616192(0x41200000, float:10.0)
                            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                            int r5 = -r5
                            float r5 = (float) r5
                            r1.setTranslationY(r5)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.PhotoViewer$CounterView r1 = r1.photosCounterView
                            r1.setRotationX(r4)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.PhotoViewer$SelectedPhotosListView r1 = r1.selectedPhotosListView
                            r5 = 0
                            r1.setEnabled(r5)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            boolean unused = r1.isPhotosListViewVisible = r5
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            boolean r1 = r1.needCaptionLayout
                            r6 = 4
                            if (r1 == 0) goto L_0x007c
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.PhotoViewer$CaptionTextViewSwitcher r1 = r1.captionTextViewSwitcher
                            r1.setVisibility(r6)
                        L_0x007c:
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            int r1 = r1.sendPhotoType
                            r7 = 2
                            r8 = 1
                            if (r1 == 0) goto L_0x00ab
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            int r1 = r1.sendPhotoType
                            if (r1 == r6) goto L_0x00ab
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            int r1 = r1.sendPhotoType
                            if (r1 == r7) goto L_0x009f
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            int r1 = r1.sendPhotoType
                            r6 = 5
                            if (r1 != r6) goto L_0x00bd
                        L_0x009f:
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            java.util.ArrayList r1 = r1.imagesArrLocals
                            int r1 = r1.size()
                            if (r1 <= r8) goto L_0x00bd
                        L_0x00ab:
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.Components.CheckBox r1 = r1.checkImageView
                            r1.setVisibility(r3)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.PhotoViewer$CounterView r1 = r1.photosCounterView
                            r1.setVisibility(r3)
                        L_0x00bd:
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.messenger.ImageReceiver r1 = r1.centerImage
                            android.graphics.Bitmap r10 = r1.getBitmap()
                            if (r10 != 0) goto L_0x00d1
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            boolean r1 = r1.isCurrentVideo
                            if (r1 == 0) goto L_0x0200
                        L_0x00d1:
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.Components.PhotoCropView r9 = r1.photoCropView
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.messenger.ImageReceiver r1 = r1.centerImage
                            int r11 = r1.getOrientation()
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            int r1 = r1.sendPhotoType
                            if (r1 == r8) goto L_0x00eb
                            r12 = 1
                            goto L_0x00ec
                        L_0x00eb:
                            r12 = 0
                        L_0x00ec:
                            r13 = 0
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.Components.PaintingOverlay r14 = r1.paintingOverlay
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            boolean r1 = r1.isCurrentVideo
                            if (r1 == 0) goto L_0x0104
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            android.view.TextureView r1 = r1.videoTextureView
                            r2 = r1
                            org.telegram.ui.Components.VideoEditTextureView r2 = (org.telegram.ui.Components.VideoEditTextureView) r2
                        L_0x0104:
                            r15 = r2
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.PhotoViewer$EditState r1 = r1.editState
                            org.telegram.messenger.MediaController$CropState r1 = r1.cropState
                            r16 = r1
                            r9.setBitmap(r10, r11, r12, r13, r14, r15, r16)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.Components.PhotoCropView r1 = r1.photoCropView
                            r1.onDisappear()
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            org.telegram.messenger.ImageReceiver r1 = r1.centerImage
                            int r1 = r1.getBitmapWidth()
                            org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.this
                            org.telegram.messenger.ImageReceiver r2 = r2.centerImage
                            int r2 = r2.getBitmapHeight()
                            org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.PhotoViewer$EditState r3 = r3.editState
                            org.telegram.messenger.MediaController$CropState r3 = r3.cropState
                            if (r3 == 0) goto L_0x0176
                            org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.PhotoViewer$EditState r3 = r3.editState
                            org.telegram.messenger.MediaController$CropState r3 = r3.cropState
                            int r3 = r3.transformRotation
                            r6 = 90
                            if (r3 == r6) goto L_0x0155
                            org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.PhotoViewer$EditState r3 = r3.editState
                            org.telegram.messenger.MediaController$CropState r3 = r3.cropState
                            int r3 = r3.transformRotation
                            r6 = 270(0x10e, float:3.78E-43)
                            if (r3 != r6) goto L_0x015a
                        L_0x0155:
                            r17 = r2
                            r2 = r1
                            r1 = r17
                        L_0x015a:
                            float r1 = (float) r1
                            org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.PhotoViewer$EditState r3 = r3.editState
                            org.telegram.messenger.MediaController$CropState r3 = r3.cropState
                            float r3 = r3.cropPw
                            float r1 = r1 * r3
                            int r1 = (int) r1
                            float r2 = (float) r2
                            org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.PhotoViewer$EditState r3 = r3.editState
                            org.telegram.messenger.MediaController$CropState r3 = r3.cropState
                            float r3 = r3.cropPh
                            float r2 = r2 * r3
                            int r2 = (int) r2
                        L_0x0176:
                            org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this
                            int r3 = r3.getContainerViewWidth()
                            float r3 = (float) r3
                            float r1 = (float) r1
                            float r3 = r3 / r1
                            org.telegram.ui.PhotoViewer r6 = org.telegram.ui.PhotoViewer.this
                            int r6 = r6.getContainerViewHeight()
                            float r6 = (float) r6
                            float r2 = (float) r2
                            float r6 = r6 / r2
                            org.telegram.ui.PhotoViewer r9 = org.telegram.ui.PhotoViewer.this
                            int r9 = r9.getContainerViewWidth(r8)
                            float r9 = (float) r9
                            float r9 = r9 / r1
                            org.telegram.ui.PhotoViewer r10 = org.telegram.ui.PhotoViewer.this
                            int r10 = r10.getContainerViewHeight(r8)
                            float r10 = (float) r10
                            float r10 = r10 / r2
                            float r3 = java.lang.Math.min(r3, r6)
                            float r6 = java.lang.Math.min(r9, r10)
                            org.telegram.ui.PhotoViewer r9 = org.telegram.ui.PhotoViewer.this
                            int r9 = r9.sendPhotoType
                            if (r9 != r8) goto L_0x01c0
                            org.telegram.ui.PhotoViewer r6 = org.telegram.ui.PhotoViewer.this
                            int r6 = r6.getContainerViewWidth(r8)
                            org.telegram.ui.PhotoViewer r9 = org.telegram.ui.PhotoViewer.this
                            int r9 = r9.getContainerViewHeight(r8)
                            int r6 = java.lang.Math.min(r6, r9)
                            float r6 = (float) r6
                            float r1 = r6 / r1
                            float r6 = r6 / r2
                            float r6 = java.lang.Math.max(r1, r6)
                        L_0x01c0:
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            float r6 = r6 / r3
                            float unused = r1.animateToScale = r6
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            int r2 = r1.getLeftInset()
                            int r2 = r2 / r7
                            org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this
                            int r3 = r3.getRightInset()
                            int r3 = r3 / r7
                            int r2 = r2 - r3
                            float r2 = (float) r2
                            float unused = r1.animateToX = r2
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            r2 = 1113587712(0x42600000, float:56.0)
                            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                            int r2 = -r2
                            int r3 = android.os.Build.VERSION.SDK_INT
                            r6 = 21
                            if (r3 < r6) goto L_0x01ec
                            int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                            int r3 = r3 / r7
                            goto L_0x01ed
                        L_0x01ec:
                            r3 = 0
                        L_0x01ed:
                            int r2 = r2 + r3
                            float r2 = (float) r2
                            float unused = r1.animateToY = r2
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            long r2 = java.lang.System.currentTimeMillis()
                            long unused = r1.animationStartTime = r2
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            boolean unused = r1.zoomAnimation = r8
                        L_0x0200:
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
                            r2.<init>()
                            android.animation.AnimatorSet unused = r1.imageMoveAnimation = r2
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            android.animation.AnimatorSet r1 = r1.imageMoveAnimation
                            r2 = 3
                            android.animation.Animator[] r2 = new android.animation.Animator[r2]
                            org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.Components.PickerBottomLayoutViewer r3 = r3.editorDoneLayout
                            android.util.Property r6 = android.view.View.TRANSLATION_Y
                            float[] r9 = new float[r7]
                            r10 = 1111490560(0x42400000, float:48.0)
                            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                            float r10 = (float) r10
                            r9[r5] = r10
                            r9[r8] = r4
                            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r6, r9)
                            r2[r5] = r3
                            org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this
                            android.util.Property<org.telegram.ui.PhotoViewer, java.lang.Float> r4 = org.telegram.ui.Components.AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE
                            float[] r5 = new float[r7]
                            r5 = {0, NUM} // fill-array
                            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
                            r2[r8] = r3
                            org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.this
                            org.telegram.ui.Components.PhotoCropView r3 = r3.photoCropView
                            android.util.Property r4 = android.view.View.ALPHA
                            float[] r5 = new float[r7]
                            r5 = {0, NUM} // fill-array
                            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
                            r2[r7] = r3
                            r1.playTogether(r2)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            android.animation.AnimatorSet r1 = r1.imageMoveAnimation
                            r2 = 200(0xc8, double:9.9E-322)
                            r1.setDuration(r2)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            android.animation.AnimatorSet r1 = r1.imageMoveAnimation
                            org.telegram.ui.PhotoViewer$43$1 r2 = new org.telegram.ui.PhotoViewer$43$1
                            r2.<init>()
                            r1.addListener(r2)
                            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.this
                            android.animation.AnimatorSet r1 = r1.imageMoveAnimation
                            r1.start()
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.AnonymousClass43.onAnimationEnd(android.animation.Animator):void");
                    }
                });
                this.changeModeAnimation.start();
            } else if (i7 == 2) {
                startVideoPlayer();
                if (this.photoFilterView == null) {
                    if (!this.imagesArrLocals.isEmpty()) {
                        Object obj = this.imagesArrLocals.get(this.currentIndex);
                        i5 = obj instanceof MediaController.PhotoEntry ? ((MediaController.PhotoEntry) obj).orientation : 0;
                        MediaController.MediaEditState mediaEditState = (MediaController.MediaEditState) obj;
                        MediaController.SavedFilterState savedFilterState2 = mediaEditState.savedFilterState;
                        str = mediaEditState.croppedPath;
                        if (str != null) {
                            savedFilterState = savedFilterState2;
                            i5 = 0;
                        } else {
                            str = mediaEditState.getPath();
                            savedFilterState = savedFilterState2;
                        }
                    } else {
                        str = null;
                        i5 = 0;
                        savedFilterState = null;
                    }
                    if (this.videoTextureView != null) {
                        i6 = i5;
                        bitmap = null;
                    } else {
                        if (savedFilterState == null) {
                            bitmap2 = this.centerImage.getBitmap();
                            i5 = this.centerImage.getOrientation();
                        } else {
                            bitmap2 = BitmapFactory.decodeFile(str);
                        }
                        bitmap = bitmap2;
                        i6 = i5;
                    }
                    Activity activity = this.parentActivity;
                    TextureView textureView = this.videoTextureView;
                    PhotoFilterView photoFilterView2 = new PhotoFilterView(activity, textureView != null ? (VideoEditTextureView) textureView : null, bitmap, i6, savedFilterState, this.isCurrentVideo ? null : this.paintingOverlay);
                    this.photoFilterView = photoFilterView2;
                    this.containerView.addView(photoFilterView2, LayoutHelper.createFrame(-1, -1.0f));
                    this.photoFilterView.getDoneTextView().setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            PhotoViewer.this.lambda$switchToEditMode$42$PhotoViewer(view);
                        }
                    });
                    this.photoFilterView.getCancelTextView().setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            PhotoViewer.this.lambda$switchToEditMode$44$PhotoViewer(view);
                        }
                    });
                    this.photoFilterView.getToolsView().setTranslationY((float) AndroidUtilities.dp(186.0f));
                }
                this.changeModeAnimation = new AnimatorSet();
                ArrayList arrayList3 = new ArrayList();
                FrameLayout frameLayout2 = this.pickerView;
                Property property4 = View.TRANSLATION_Y;
                float[] fArr4 = new float[2];
                fArr4[0] = 0.0f;
                fArr4[1] = (float) AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList3.add(ObjectAnimator.ofFloat(frameLayout2, property4, fArr4));
                ImageView imageView2 = this.pickerViewSendButton;
                Property property5 = View.TRANSLATION_Y;
                float[] fArr5 = new float[2];
                fArr5[0] = 0.0f;
                fArr5[1] = (float) AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList3.add(ObjectAnimator.ofFloat(imageView2, property5, fArr5));
                ActionBar actionBar3 = this.actionBar;
                arrayList3.add(ObjectAnimator.ofFloat(actionBar3, View.TRANSLATION_Y, new float[]{0.0f, (float) (-actionBar3.getHeight())}));
                int i13 = this.sendPhotoType;
                if (i13 == 0 || i13 == 4) {
                    arrayList3.add(ObjectAnimator.ofFloat(this.checkImageView, View.ALPHA, new float[]{1.0f, 0.0f}));
                    arrayList3.add(ObjectAnimator.ofFloat(this.photosCounterView, View.ALPHA, new float[]{1.0f, 0.0f}));
                } else if (i13 == 1) {
                    arrayList3.add(ObjectAnimator.ofFloat(this.photoCropView, View.ALPHA, new float[]{1.0f, 0.0f}));
                }
                if (this.selectedPhotosListView.getVisibility() == 0) {
                    arrayList3.add(ObjectAnimator.ofFloat(this.selectedPhotosListView, View.ALPHA, new float[]{1.0f, 0.0f}));
                }
                if (this.cameraItem.getTag() != null) {
                    arrayList3.add(ObjectAnimator.ofFloat(this.cameraItem, View.ALPHA, new float[]{1.0f, 0.0f}));
                }
                if (this.muteItem.getTag() != null) {
                    arrayList3.add(ObjectAnimator.ofFloat(this.muteItem, View.ALPHA, new float[]{1.0f, 0.0f}));
                }
                this.changeModeAnimation.playTogether(arrayList3);
                this.changeModeAnimation.setDuration(200);
                this.changeModeAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = PhotoViewer.this.changeModeAnimation = null;
                        PhotoViewer.this.pickerView.setVisibility(8);
                        PhotoViewer.this.pickerViewSendButton.setVisibility(8);
                        PhotoViewer.this.actionBar.setVisibility(8);
                        PhotoViewer.this.cameraItem.setVisibility(8);
                        PhotoViewer.this.muteItem.setVisibility(8);
                        if (PhotoViewer.this.photoCropView != null) {
                            PhotoViewer.this.photoCropView.setVisibility(4);
                        }
                        PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                        PhotoViewer.this.selectedPhotosListView.setAlpha(0.0f);
                        PhotoViewer.this.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
                        PhotoViewer.this.photosCounterView.setRotationX(0.0f);
                        PhotoViewer.this.selectedPhotosListView.setEnabled(false);
                        boolean unused2 = PhotoViewer.this.isPhotosListViewVisible = false;
                        if (PhotoViewer.this.needCaptionLayout) {
                            PhotoViewer.this.captionTextViewSwitcher.setVisibility(4);
                        }
                        if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4 || ((PhotoViewer.this.sendPhotoType == 2 || PhotoViewer.this.sendPhotoType == 5) && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                            PhotoViewer.this.checkImageView.setVisibility(8);
                            PhotoViewer.this.photosCounterView.setVisibility(8);
                        }
                        if (PhotoViewer.this.centerImage.getBitmap() != null) {
                            int bitmapWidth = PhotoViewer.this.centerImage.getBitmapWidth();
                            int bitmapHeight = PhotoViewer.this.centerImage.getBitmapHeight();
                            if (PhotoViewer.this.editState.cropState != null) {
                                if (PhotoViewer.this.editState.cropState.transformRotation == 90 || PhotoViewer.this.editState.cropState.transformRotation == 270) {
                                    int i = bitmapHeight;
                                    bitmapHeight = bitmapWidth;
                                    bitmapWidth = i;
                                }
                                bitmapWidth = (int) (((float) bitmapWidth) * PhotoViewer.this.editState.cropState.cropPw);
                                bitmapHeight = (int) (((float) bitmapHeight) * PhotoViewer.this.editState.cropState.cropPh);
                            }
                            float f = (float) bitmapWidth;
                            float f2 = (float) bitmapHeight;
                            float access$16800 = ((float) PhotoViewer.this.getContainerViewWidth(2)) / f;
                            float access$16900 = ((float) PhotoViewer.this.getContainerViewHeight(2)) / f2;
                            float unused3 = PhotoViewer.this.animateToScale = Math.min(access$16800, access$16900) / Math.min(((float) PhotoViewer.this.getContainerViewWidth()) / f, ((float) PhotoViewer.this.getContainerViewHeight()) / f2);
                            PhotoViewer photoViewer = PhotoViewer.this;
                            float unused4 = photoViewer.animateToX = (float) ((photoViewer.getLeftInset() / 2) - (PhotoViewer.this.getRightInset() / 2));
                            float unused5 = PhotoViewer.this.animateToY = (float) ((-AndroidUtilities.dp(92.0f)) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                            long unused6 = PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                            boolean unused7 = PhotoViewer.this.zoomAnimation = true;
                        }
                        AnimatorSet unused8 = PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                        PhotoViewer.this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.photoFilterView.getToolsView(), View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(186.0f), 0.0f})});
                        PhotoViewer.this.imageMoveAnimation.setDuration(200);
                        PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationStart(Animator animator) {
                            }

                            public void onAnimationEnd(Animator animator) {
                                PhotoViewer.this.photoFilterView.init();
                                AnimatorSet unused = PhotoViewer.this.imageMoveAnimation = null;
                                AnonymousClass44 r3 = AnonymousClass44.this;
                                int unused2 = PhotoViewer.this.currentEditMode = i7;
                                int unused3 = PhotoViewer.this.switchingToMode = -1;
                                float unused4 = PhotoViewer.this.animateToScale = 1.0f;
                                float unused5 = PhotoViewer.this.animateToX = 0.0f;
                                float unused6 = PhotoViewer.this.animateToY = 0.0f;
                                float unused7 = PhotoViewer.this.scale = 1.0f;
                                PhotoViewer photoViewer = PhotoViewer.this;
                                photoViewer.updateMinMax(photoViewer.scale);
                                boolean unused8 = PhotoViewer.this.padImageForHorizontalInsets = true;
                                PhotoViewer.this.containerView.invalidate();
                                if (PhotoViewer.this.sendPhotoType == 1) {
                                    PhotoViewer.this.photoCropView.reset();
                                }
                            }
                        });
                        PhotoViewer.this.imageMoveAnimation.start();
                    }
                });
                this.changeModeAnimation.start();
            } else if (i7 == 3) {
                startVideoPlayer();
                if (this.photoPaintView == null) {
                    TextureView textureView2 = this.videoTextureView;
                    if (textureView2 != null) {
                        VideoEditTextureView videoEditTextureView2 = (VideoEditTextureView) textureView2;
                        i3 = videoEditTextureView2.getVideoWidth();
                        i4 = videoEditTextureView2.getVideoHeight();
                        while (true) {
                            if (i3 <= 1280 && i4 <= 1280) {
                                break;
                            }
                            i3 /= 2;
                            i4 /= 2;
                        }
                    } else {
                        i3 = this.centerImage.getBitmapWidth();
                        i4 = this.centerImage.getBitmapHeight();
                    }
                    Bitmap bitmap3 = this.paintingOverlay.getBitmap();
                    AnonymousClass45 r13 = r0;
                    i2 = 2;
                    f = 0.0f;
                    AnonymousClass45 r0 = new PhotoPaintView(this.parentActivity, bitmap3 == null ? Bitmap.createBitmap(i3, i4, Bitmap.Config.ARGB_8888) : bitmap3, this.isCurrentVideo ? null : this.centerImage.getBitmap(), this.centerImage.getOrientation(), this.editState.mediaEntities, new Runnable() {
                        public final void run() {
                            PhotoViewer.this.lambda$switchToEditMode$45$PhotoViewer();
                        }
                    }) {
                        /* access modifiers changed from: protected */
                        public void onOpenCloseStickersAlert(boolean z) {
                            if (PhotoViewer.this.videoPlayer == null) {
                                return;
                            }
                            if (z) {
                                PhotoViewer.this.videoPlayer.pause();
                            } else {
                                PhotoViewer.this.videoPlayer.play();
                            }
                        }
                    };
                    this.photoPaintView = r13;
                    this.containerView.addView(r13, LayoutHelper.createFrame(-1, -1.0f));
                    this.photoPaintView.getDoneTextView().setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            PhotoViewer.this.lambda$switchToEditMode$46$PhotoViewer(view);
                        }
                    });
                    this.photoPaintView.getCancelTextView().setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            PhotoViewer.this.lambda$switchToEditMode$48$PhotoViewer(view);
                        }
                    });
                    this.photoPaintView.getColorPicker().setTranslationY((float) AndroidUtilities.dp(126.0f));
                    this.photoPaintView.getToolsView().setTranslationY((float) AndroidUtilities.dp(126.0f));
                } else {
                    i2 = 2;
                    f = 0.0f;
                }
                this.changeModeAnimation = new AnimatorSet();
                ArrayList arrayList4 = new ArrayList();
                FrameLayout frameLayout3 = this.pickerView;
                Property property6 = View.TRANSLATION_Y;
                float[] fArr6 = new float[i2];
                fArr6[0] = f;
                fArr6[1] = (float) AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList4.add(ObjectAnimator.ofFloat(frameLayout3, property6, fArr6));
                ImageView imageView3 = this.pickerViewSendButton;
                Property property7 = View.TRANSLATION_Y;
                float[] fArr7 = new float[i2];
                fArr7[0] = f;
                fArr7[1] = (float) AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList4.add(ObjectAnimator.ofFloat(imageView3, property7, fArr7));
                ActionBar actionBar4 = this.actionBar;
                Property property8 = View.TRANSLATION_Y;
                float[] fArr8 = new float[i2];
                fArr8[0] = f;
                fArr8[1] = (float) (-actionBar4.getHeight());
                arrayList4.add(ObjectAnimator.ofFloat(actionBar4, property8, fArr8));
                if (this.needCaptionLayout) {
                    CaptionTextViewSwitcher captionTextViewSwitcher3 = this.captionTextViewSwitcher;
                    Property property9 = View.TRANSLATION_Y;
                    float[] fArr9 = new float[i2];
                    fArr9[0] = f;
                    fArr9[1] = (float) AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                    arrayList4.add(ObjectAnimator.ofFloat(captionTextViewSwitcher3, property9, fArr9));
                }
                int i14 = this.sendPhotoType;
                if (i14 == 0 || i14 == 4) {
                    float[] fArr10 = new float[i2];
                    // fill-array-data instruction
                    fArr10[0] = NUM;
                    fArr10[1] = 0;
                    arrayList4.add(ObjectAnimator.ofFloat(this.checkImageView, View.ALPHA, fArr10));
                    float[] fArr11 = new float[i2];
                    // fill-array-data instruction
                    fArr11[0] = NUM;
                    fArr11[1] = 0;
                    arrayList4.add(ObjectAnimator.ofFloat(this.photosCounterView, View.ALPHA, fArr11));
                } else if (i14 == 1) {
                    float[] fArr12 = new float[i2];
                    // fill-array-data instruction
                    fArr12[0] = NUM;
                    fArr12[1] = 0;
                    arrayList4.add(ObjectAnimator.ofFloat(this.photoCropView, View.ALPHA, fArr12));
                }
                if (this.selectedPhotosListView.getVisibility() == 0) {
                    float[] fArr13 = new float[i2];
                    // fill-array-data instruction
                    fArr13[0] = NUM;
                    fArr13[1] = 0;
                    arrayList4.add(ObjectAnimator.ofFloat(this.selectedPhotosListView, View.ALPHA, fArr13));
                }
                if (this.cameraItem.getTag() != null) {
                    float[] fArr14 = new float[i2];
                    // fill-array-data instruction
                    fArr14[0] = NUM;
                    fArr14[1] = 0;
                    arrayList4.add(ObjectAnimator.ofFloat(this.cameraItem, View.ALPHA, fArr14));
                }
                if (this.muteItem.getTag() != null) {
                    float[] fArr15 = new float[i2];
                    // fill-array-data instruction
                    fArr15[0] = NUM;
                    fArr15[1] = 0;
                    arrayList4.add(ObjectAnimator.ofFloat(this.muteItem, View.ALPHA, fArr15));
                }
                this.changeModeAnimation.playTogether(arrayList4);
                this.changeModeAnimation.setDuration(200);
                this.changeModeAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = PhotoViewer.this.changeModeAnimation = null;
                        PhotoViewer.this.pickerView.setVisibility(8);
                        PhotoViewer.this.pickerViewSendButton.setVisibility(8);
                        PhotoViewer.this.cameraItem.setVisibility(8);
                        PhotoViewer.this.muteItem.setVisibility(8);
                        if (PhotoViewer.this.photoCropView != null) {
                            PhotoViewer.this.photoCropView.setVisibility(4);
                        }
                        PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                        PhotoViewer.this.selectedPhotosListView.setAlpha(0.0f);
                        PhotoViewer.this.selectedPhotosListView.setTranslationY((float) (-AndroidUtilities.dp(10.0f)));
                        PhotoViewer.this.photosCounterView.setRotationX(0.0f);
                        PhotoViewer.this.selectedPhotosListView.setEnabled(false);
                        boolean unused2 = PhotoViewer.this.isPhotosListViewVisible = false;
                        if (PhotoViewer.this.needCaptionLayout) {
                            PhotoViewer.this.captionTextViewSwitcher.setVisibility(4);
                        }
                        if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4 || ((PhotoViewer.this.sendPhotoType == 2 || PhotoViewer.this.sendPhotoType == 5) && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                            PhotoViewer.this.checkImageView.setVisibility(8);
                            PhotoViewer.this.photosCounterView.setVisibility(8);
                        }
                        if (PhotoViewer.this.centerImage.getBitmap() != null) {
                            float bitmapWidth = (float) PhotoViewer.this.centerImage.getBitmapWidth();
                            float bitmapHeight = (float) PhotoViewer.this.centerImage.getBitmapHeight();
                            float access$16800 = ((float) PhotoViewer.this.getContainerViewWidth(3)) / bitmapWidth;
                            float access$16900 = ((float) PhotoViewer.this.getContainerViewHeight(3)) / bitmapHeight;
                            float unused3 = PhotoViewer.this.animateToScale = Math.min(access$16800, access$16900) / Math.min(((float) PhotoViewer.this.getContainerViewWidth()) / bitmapWidth, ((float) PhotoViewer.this.getContainerViewHeight()) / bitmapHeight);
                            PhotoViewer photoViewer = PhotoViewer.this;
                            float unused4 = photoViewer.animateToX = (float) ((photoViewer.getLeftInset() / 2) - (PhotoViewer.this.getRightInset() / 2));
                            float unused5 = PhotoViewer.this.animateToY = (float) ((-AndroidUtilities.dp(44.0f)) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                            long unused6 = PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                            boolean unused7 = PhotoViewer.this.zoomAnimation = true;
                        }
                        AnimatorSet unused8 = PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                        PhotoViewer.this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(PhotoViewer.this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getColorPicker(), View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(126.0f), 0.0f}), ObjectAnimator.ofFloat(PhotoViewer.this.photoPaintView.getToolsView(), View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(126.0f), 0.0f})});
                        PhotoViewer.this.imageMoveAnimation.setDuration(200);
                        PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationStart(Animator animator) {
                            }

                            public void onAnimationEnd(Animator animator) {
                                PhotoViewer.this.photoPaintView.init();
                                PhotoViewer.this.paintingOverlay.hideEntities();
                                AnimatorSet unused = PhotoViewer.this.imageMoveAnimation = null;
                                AnonymousClass46 r3 = AnonymousClass46.this;
                                int unused2 = PhotoViewer.this.currentEditMode = i7;
                                int unused3 = PhotoViewer.this.switchingToMode = -1;
                                float unused4 = PhotoViewer.this.animateToScale = 1.0f;
                                float unused5 = PhotoViewer.this.animateToX = 0.0f;
                                float unused6 = PhotoViewer.this.animateToY = 0.0f;
                                float unused7 = PhotoViewer.this.scale = 1.0f;
                                PhotoViewer photoViewer = PhotoViewer.this;
                                photoViewer.updateMinMax(photoViewer.scale);
                                boolean unused8 = PhotoViewer.this.padImageForHorizontalInsets = true;
                                PhotoViewer.this.containerView.invalidate();
                                if (PhotoViewer.this.sendPhotoType == 1) {
                                    PhotoViewer.this.photoCropView.reset();
                                }
                            }
                        });
                        PhotoViewer.this.imageMoveAnimation.start();
                    }
                });
                this.changeModeAnimation.start();
            }
        }
    }

    public /* synthetic */ void lambda$switchToEditMode$42$PhotoViewer(View view) {
        applyCurrentEditMode();
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$switchToEditMode$44$PhotoViewer(View view) {
        if (this.photoFilterView.hasChanges()) {
            Activity activity = this.parentActivity;
            if (activity != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
                builder.setMessage(LocaleController.getString("DiscardChanges", NUM));
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PhotoViewer.this.lambda$null$43$PhotoViewer(dialogInterface, i);
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

    public /* synthetic */ void lambda$null$43$PhotoViewer(DialogInterface dialogInterface, int i) {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$switchToEditMode$45$PhotoViewer() {
        this.paintingOverlay.hideBitmap();
    }

    public /* synthetic */ void lambda$switchToEditMode$46$PhotoViewer(View view) {
        applyCurrentEditMode();
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$null$47$PhotoViewer() {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$switchToEditMode$48$PhotoViewer(View view) {
        this.photoPaintView.maybeShowDismissalAlert(this, this.parentActivity, new Runnable() {
            public final void run() {
                PhotoViewer.this.lambda$null$47$PhotoViewer();
            }
        });
    }

    private void toggleCheckImageView(boolean z) {
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        float dpf2 = AndroidUtilities.dpf2(24.0f);
        FrameLayout frameLayout = this.pickerView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        float f = 1.0f;
        float f2 = 0.0f;
        fArr[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
        FrameLayout frameLayout2 = this.pickerView;
        Property property2 = View.TRANSLATION_Y;
        float[] fArr2 = new float[1];
        fArr2[0] = z ? 0.0f : dpf2;
        arrayList.add(ObjectAnimator.ofFloat(frameLayout2, property2, fArr2));
        ImageView imageView = this.pickerViewSendButton;
        Property property3 = View.ALPHA;
        float[] fArr3 = new float[1];
        fArr3[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(imageView, property3, fArr3));
        ImageView imageView2 = this.pickerViewSendButton;
        Property property4 = View.TRANSLATION_Y;
        float[] fArr4 = new float[1];
        fArr4[0] = z ? 0.0f : dpf2;
        arrayList.add(ObjectAnimator.ofFloat(imageView2, property4, fArr4));
        int i = this.sendPhotoType;
        if (i == 0 || i == 4) {
            CheckBox checkBox = this.checkImageView;
            Property property5 = View.ALPHA;
            float[] fArr5 = new float[1];
            fArr5[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(checkBox, property5, fArr5));
            CheckBox checkBox2 = this.checkImageView;
            Property property6 = View.TRANSLATION_Y;
            float[] fArr6 = new float[1];
            fArr6[0] = z ? 0.0f : -dpf2;
            arrayList.add(ObjectAnimator.ofFloat(checkBox2, property6, fArr6));
            CounterView counterView = this.photosCounterView;
            Property property7 = View.ALPHA;
            float[] fArr7 = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr7[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(counterView, property7, fArr7));
            CounterView counterView2 = this.photosCounterView;
            Property property8 = View.TRANSLATION_Y;
            float[] fArr8 = new float[1];
            if (!z) {
                f2 = -dpf2;
            }
            fArr8[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(counterView2, property8, fArr8));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    private void toggleMiniProgressInternal(final boolean z) {
        if (z) {
            this.miniProgressView.setVisibility(0);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        this.miniProgressAnimator = animatorSet;
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
        AndroidUtilities.cancelRunOnUIThread(this.miniProgressShowRunnable);
        int i = 0;
        if (z2) {
            toggleMiniProgressInternal(z);
            if (z) {
                AnimatorSet animatorSet = this.miniProgressAnimator;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.miniProgressAnimator = null;
                }
                if (this.firstAnimationDelay) {
                    this.firstAnimationDelay = false;
                    toggleMiniProgressInternal(true);
                    return;
                }
                AndroidUtilities.runOnUIThread(this.miniProgressShowRunnable, 500);
                return;
            }
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

    private void updateContainerFlags(boolean z) {
        FrameLayoutDrawer frameLayoutDrawer;
        if (Build.VERSION.SDK_INT >= 21 && this.sendPhotoType != 1 && (frameLayoutDrawer = this.containerView) != null) {
            int i = 1792;
            if (!z) {
                i = 1796;
                if (frameLayoutDrawer.getPaddingLeft() > 0 || this.containerView.getPaddingRight() > 0) {
                    i = 5894;
                }
            }
            this.containerView.setSystemUiVisibility(i);
        }
    }

    /* access modifiers changed from: private */
    public void toggleActionBar(final boolean z, boolean z2) {
        CaptionScrollView captionScrollView2;
        CaptionScrollView captionScrollView3;
        AnimatorSet animatorSet = this.actionBarAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        if (z) {
            this.actionBar.setVisibility(0);
            if (this.bottomLayout.getTag() != null) {
                this.bottomLayout.setVisibility(0);
            }
            if (this.captionTextViewSwitcher.getTag() != null) {
                this.captionTextViewSwitcher.setVisibility(0);
                VideoSeekPreviewImage videoSeekPreviewImage = this.videoPreviewFrame;
                if (videoSeekPreviewImage != null) {
                    videoSeekPreviewImage.requestLayout();
                }
            }
        }
        this.isActionBarVisible = z;
        updateContainerFlags(z);
        if (!this.videoPlayerControlVisible || !this.isPlaying || !z) {
            AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
        } else {
            scheduleActionBarHide();
        }
        float dpf2 = AndroidUtilities.dpf2(24.0f);
        float f = 1.0f;
        if (z2) {
            ArrayList arrayList = new ArrayList();
            ActionBar actionBar2 = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar2, property, fArr));
            ActionBar actionBar3 = this.actionBar;
            Property property2 = View.TRANSLATION_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 0.0f : -dpf2;
            arrayList.add(ObjectAnimator.ofFloat(actionBar3, property2, fArr2));
            FrameLayout frameLayout = this.bottomLayout;
            if (frameLayout != null) {
                Property property3 = View.ALPHA;
                float[] fArr3 = new float[1];
                fArr3[0] = z ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(frameLayout, property3, fArr3));
                FrameLayout frameLayout2 = this.bottomLayout;
                Property property4 = View.TRANSLATION_Y;
                float[] fArr4 = new float[1];
                fArr4[0] = z ? 0.0f : dpf2;
                arrayList.add(ObjectAnimator.ofFloat(frameLayout2, property4, fArr4));
            }
            if (this.videoPlayerControlVisible) {
                VideoPlayerControlFrameLayout videoPlayerControlFrameLayout2 = this.videoPlayerControlFrameLayout;
                Property<VideoPlayerControlFrameLayout, Float> property5 = VPC_PROGRESS;
                float[] fArr5 = new float[1];
                fArr5[0] = z ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(videoPlayerControlFrameLayout2, property5, fArr5));
            } else {
                this.videoPlayerControlFrameLayout.setProgress(z ? 1.0f : 0.0f);
            }
            GroupedPhotosListView groupedPhotosListView2 = this.groupedPhotosListView;
            Property property6 = View.ALPHA;
            float[] fArr6 = new float[1];
            fArr6[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView2, property6, fArr6));
            GroupedPhotosListView groupedPhotosListView3 = this.groupedPhotosListView;
            Property property7 = View.TRANSLATION_Y;
            float[] fArr7 = new float[1];
            fArr7[0] = z ? 0.0f : dpf2;
            arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView3, property7, fArr7));
            if (!this.needCaptionLayout && (captionScrollView3 = this.captionScrollView) != null) {
                Property property8 = View.ALPHA;
                float[] fArr8 = new float[1];
                fArr8[0] = z ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(captionScrollView3, property8, fArr8));
                CaptionScrollView captionScrollView4 = this.captionScrollView;
                Property property9 = View.TRANSLATION_Y;
                float[] fArr9 = new float[1];
                if (z) {
                    dpf2 = 0.0f;
                }
                fArr9[0] = dpf2;
                arrayList.add(ObjectAnimator.ofFloat(captionScrollView4, property9, fArr9));
            }
            if (this.videoPlayerControlVisible && this.isPlaying) {
                float[] fArr10 = new float[2];
                fArr10[0] = this.photoProgressViews[0].animAlphas[1];
                fArr10[1] = z ? 1.0f : 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr10);
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PhotoViewer.this.lambda$toggleActionBar$49$PhotoViewer(valueAnimator);
                    }
                });
                arrayList.add(ofFloat);
            }
            if (this.muteItem.getTag() != null) {
                ImageView imageView = this.muteItem;
                Property property10 = View.ALPHA;
                float[] fArr11 = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr11[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(imageView, property10, fArr11));
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionBarAnimator = animatorSet2;
            animatorSet2.playTogether(arrayList);
            this.actionBarAnimator.setDuration(200);
            this.actionBarAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(PhotoViewer.this.actionBarAnimator)) {
                        if (!z) {
                            PhotoViewer.this.actionBar.setVisibility(4);
                            if (PhotoViewer.this.bottomLayout.getTag() != null) {
                                PhotoViewer.this.bottomLayout.setVisibility(4);
                            }
                            if (PhotoViewer.this.captionTextViewSwitcher.getTag() != null) {
                                PhotoViewer.this.captionTextViewSwitcher.setVisibility(4);
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
        this.actionBar.setTranslationY(z ? 0.0f : -dpf2);
        this.bottomLayout.setAlpha(z ? 1.0f : 0.0f);
        this.bottomLayout.setTranslationY(z ? 0.0f : dpf2);
        this.groupedPhotosListView.setAlpha(z ? 1.0f : 0.0f);
        this.groupedPhotosListView.setTranslationY(z ? 0.0f : dpf2);
        if (!this.needCaptionLayout && (captionScrollView2 = this.captionScrollView) != null) {
            captionScrollView2.setAlpha(z ? 1.0f : 0.0f);
            CaptionScrollView captionScrollView5 = this.captionScrollView;
            if (z) {
                dpf2 = 0.0f;
            }
            captionScrollView5.setTranslationY(dpf2);
        }
        this.videoPlayerControlFrameLayout.setProgress(z ? 1.0f : 0.0f);
        if (this.muteItem.getTag() != null) {
            this.muteItem.setAlpha(z ? 1.0f : 0.0f);
        }
        if (this.videoPlayerControlVisible && this.isPlaying) {
            PhotoProgressView photoProgressView = this.photoProgressViews[0];
            if (!z) {
                f = 0.0f;
            }
            photoProgressView.setIndexedAlpha(1, f, false);
        }
    }

    public /* synthetic */ void lambda$toggleActionBar$49$PhotoViewer(ValueAnimator valueAnimator) {
        this.photoProgressViews[0].setIndexedAlpha(1, ((Float) valueAnimator.getAnimatedValue()).floatValue(), false);
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
                SelectedPhotosListView selectedPhotosListView2 = this.selectedPhotosListView;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(selectedPhotosListView2, property, fArr));
                SelectedPhotosListView selectedPhotosListView3 = this.selectedPhotosListView;
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 0.0f : (float) (-AndroidUtilities.dp(10.0f));
                arrayList.add(ObjectAnimator.ofFloat(selectedPhotosListView3, property2, fArr2));
                CounterView counterView = this.photosCounterView;
                Property property3 = View.ROTATION_X;
                float[] fArr3 = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr3[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(counterView, property3, fArr3));
                AnimatorSet animatorSet = new AnimatorSet();
                this.currentListViewAnimation = animatorSet;
                animatorSet.playTogether(arrayList);
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

    private void toggleVideoPlayer() {
        if (this.videoPlayer != null) {
            AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
            if (this.isPlaying) {
                this.videoPlayer.pause();
            } else {
                if (!this.isCurrentVideo) {
                    if (Math.abs(this.videoPlayerSeekbar.getProgress() - 1.0f) < 0.01f || this.videoPlayer.getCurrentPosition() == this.videoPlayer.getDuration()) {
                        this.videoPlayer.seekTo(0);
                    }
                    scheduleActionBarHide();
                } else if (Math.abs(this.videoTimelineView.getProgress() - 1.0f) < 0.01f || this.videoPlayer.getCurrentPosition() == this.videoPlayer.getDuration()) {
                    this.videoPlayer.seekTo((long) ((int) (this.videoTimelineView.getLeftProgress() * ((float) this.videoPlayer.getDuration()))));
                }
                this.videoPlayer.play();
            }
            this.containerView.invalidate();
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
                if (obj instanceof TLRPC$BotInlineResult) {
                    TLRPC$BotInlineResult tLRPC$BotInlineResult = (TLRPC$BotInlineResult) obj;
                    TLRPC$Document tLRPC$Document = tLRPC$BotInlineResult.document;
                    if (tLRPC$Document != null) {
                        return FileLoader.getAttachFileName(tLRPC$Document);
                    }
                    TLRPC$Photo tLRPC$Photo = tLRPC$BotInlineResult.photo;
                    if (tLRPC$Photo != null) {
                        return FileLoader.getAttachFileName(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, AndroidUtilities.getPhotoSize()));
                    }
                    if (tLRPC$BotInlineResult.content instanceof TLRPC$TL_webDocument) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(Utilities.MD5(tLRPC$BotInlineResult.content.url));
                        sb.append(".");
                        TLRPC$WebDocument tLRPC$WebDocument = tLRPC$BotInlineResult.content;
                        sb.append(ImageLoader.getHttpUrlExtension(tLRPC$WebDocument.url, FileLoader.getMimeTypePart(tLRPC$WebDocument.mime_type)));
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
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if (!(tLRPC$Message instanceof TLRPC$TL_messageService)) {
                TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
                if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || tLRPC$MessageMedia.photo == null) {
                    TLRPC$MessageMedia tLRPC$MessageMedia2 = messageObject.messageOwner.media;
                    if (!(tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaWebPage) || tLRPC$MessageMedia2.webpage == null) {
                        TLRPC$MessageMedia tLRPC$MessageMedia3 = messageObject.messageOwner.media;
                        if (tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaInvoice) {
                            return ImageLocation.getForWebFile(WebFile.createWithWebDocument(((TLRPC$TL_messageMediaInvoice) tLRPC$MessageMedia3).photo));
                        }
                        if (messageObject.getDocument() != null) {
                            TLRPC$Document document = messageObject.getDocument();
                            if (this.sharedMediaType == 5) {
                                return ImageLocation.getForDocument(document);
                            }
                            if (MessageObject.isDocumentHasThumb(messageObject.getDocument())) {
                                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
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
                }
                if (messageObject.isGif()) {
                    return ImageLocation.getForDocument(messageObject.getDocument());
                }
                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
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
            } else if (tLRPC$Message.action instanceof TLRPC$TL_messageActionUserUpdatedPhoto) {
                return null;
            } else {
                TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
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
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if (tLRPC$Message instanceof TLRPC$TL_messageService) {
                TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
                if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionUserUpdatedPhoto) {
                    return tLRPC$MessageAction.newUserPhoto.photo_big;
                }
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
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
                TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
                if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || tLRPC$MessageMedia.photo == null) {
                    TLRPC$MessageMedia tLRPC$MessageMedia2 = messageObject.messageOwner.media;
                    if (!(tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaWebPage) || tLRPC$MessageMedia2.webpage == null) {
                        TLRPC$MessageMedia tLRPC$MessageMedia3 = messageObject.messageOwner.media;
                        if (tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaInvoice) {
                            return ((TLRPC$TL_messageMediaInvoice) tLRPC$MessageMedia3).photo;
                        }
                        if (messageObject.getDocument() != null && MessageObject.isDocumentHasThumb(messageObject.getDocument())) {
                            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.getDocument().thumbs, 90);
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
                TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
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

    private void setItemVisible(View view, boolean z, boolean z2) {
        setItemVisible(view, z, z2, 1.0f);
    }

    private void setItemVisible(View view, boolean z, boolean z2, float f) {
        Boolean bool = this.actionBarItemsVisibility.get(view);
        if (bool == null || bool.booleanValue() != z) {
            this.actionBarItemsVisibility.put(view, Boolean.valueOf(z));
            view.animate().cancel();
            float f2 = (z ? 1.0f : 0.0f) * f;
            int i = 0;
            if (!z2 || bool == null) {
                if (!z) {
                    i = 8;
                }
                view.setVisibility(i);
                view.setAlpha(f2);
                return;
            }
            if (z) {
                view.setVisibility(0);
            }
            view.animate().alpha(f2).setDuration(100).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable(z, view) {
                public final /* synthetic */ boolean f$0;
                public final /* synthetic */ View f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    PhotoViewer.lambda$setItemVisible$50(this.f$0, this.f$1);
                }
            }).start();
        }
    }

    static /* synthetic */ void lambda$setItemVisible$50(boolean z, View view) {
        if (!z) {
            view.setVisibility(8);
        }
    }

    private void onPhotoShow(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, ImageLocation imageLocation, ArrayList<MessageObject> arrayList, ArrayList<SecureDocument> arrayList2, ArrayList<Object> arrayList3, int i, PlaceProviderObject placeProviderObject) {
        TLRPC$BotInlineResult tLRPC$BotInlineResult;
        ChatActivity chatActivity;
        MessageObject messageObject2;
        boolean z;
        TLRPC$EncryptedChat tLRPC$EncryptedChat;
        PhotoViewerProvider photoViewerProvider;
        ImageLocation imageLocation2;
        TLRPC$MessageAction tLRPC$MessageAction;
        TLRPC$WebPage tLRPC$WebPage;
        String str;
        MessageObject messageObject3 = messageObject;
        ArrayList<MessageObject> arrayList4 = arrayList;
        ArrayList<SecureDocument> arrayList5 = arrayList2;
        ArrayList<Object> arrayList6 = arrayList3;
        int i2 = i;
        PlaceProviderObject placeProviderObject2 = placeProviderObject;
        this.classGuid = ConnectionsManager.generateClassGuid();
        TLRPC$User tLRPC$User = null;
        this.currentMessageObject = null;
        this.currentFileLocation = null;
        this.currentSecureDocument = null;
        this.currentPathObject = null;
        this.fromCamera = false;
        this.currentBotInlineResult = null;
        this.currentIndex = -1;
        String[] strArr = this.currentFileNames;
        strArr[0] = null;
        boolean z2 = true;
        strArr[1] = null;
        strArr[2] = null;
        this.avatarsDialogId = 0;
        this.totalImagesCount = 0;
        this.totalImagesCountMerge = 0;
        this.currentEditMode = 0;
        this.isFirstLoading = true;
        this.needSearchImageInArr = false;
        this.loadingMoreImages = false;
        boolean[] zArr = this.endReached;
        zArr[0] = false;
        zArr[1] = this.mergeDialogId == 0;
        this.opennedFromMedia = false;
        this.needCaptionLayout = false;
        this.containerView.setTag(1);
        this.playerAutoStarted = false;
        this.isCurrentVideo = false;
        this.imagesArr.clear();
        this.imagesArrLocations.clear();
        this.imagesArrLocationsSizes.clear();
        this.avatarsArr.clear();
        this.secureDocuments.clear();
        this.imagesArrLocals.clear();
        for (int i3 = 0; i3 < 2; i3++) {
            this.imagesByIds[i3].clear();
            this.imagesByIdsTemp[i3].clear();
        }
        this.imagesArrTemp.clear();
        this.currentUserAvatarLocation = null;
        this.containerView.setPadding(0, 0, 0, 0);
        ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
        }
        this.currentThumb = placeProviderObject2 != null ? placeProviderObject2.thumb : null;
        this.isEvent = placeProviderObject2 != null && placeProviderObject2.isEvent;
        this.sharedMediaType = 0;
        this.allMediaItem.setText(LocaleController.getString("ShowAllMedia", NUM));
        this.menuItem.setVisibility(0);
        setItemVisible(this.sendItem, false, false);
        setItemVisible(this.pipItem, false, true);
        this.cameraItem.setVisibility(8);
        this.cameraItem.setTag((Object) null);
        this.bottomLayout.setVisibility(0);
        this.bottomLayout.setTag(1);
        this.bottomLayout.setTranslationY(0.0f);
        this.captionTextViewSwitcher.setTranslationY(0.0f);
        this.shareButton.setVisibility(8);
        QualityChooseView qualityChooseView2 = this.qualityChooseView;
        if (qualityChooseView2 != null) {
            qualityChooseView2.setVisibility(4);
            this.qualityPicker.setVisibility(4);
            this.qualityChooseView.setTag((Object) null);
        }
        AnimatorSet animatorSet = this.qualityChooseViewAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.qualityChooseViewAnimation = null;
        }
        setDoubleTapEnabled(true);
        this.allowShare = false;
        this.slideshowMessageId = 0;
        this.nameOverride = null;
        this.dateOverride = 0;
        this.menuItem.hideSubItem(2);
        this.menuItem.hideSubItem(4);
        this.menuItem.hideSubItem(10);
        this.menuItem.hideSubItem(11);
        this.menuItem.hideSubItem(14);
        this.menuItem.hideSubItem(15);
        this.actionBar.setTranslationY(0.0f);
        this.checkImageView.setAlpha(1.0f);
        this.checkImageView.setTranslationY(0.0f);
        this.checkImageView.setVisibility(8);
        this.actionBar.setTitleRightMargin(0);
        this.photosCounterView.setAlpha(1.0f);
        this.photosCounterView.setTranslationY(0.0f);
        this.photosCounterView.setVisibility(8);
        this.pickerView.setVisibility(8);
        this.pickerViewSendButton.setVisibility(8);
        this.pickerViewSendButton.setTranslationY(0.0f);
        this.pickerView.setAlpha(1.0f);
        this.pickerViewSendButton.setAlpha(1.0f);
        this.pickerView.setTranslationY(0.0f);
        this.paintItem.setVisibility(8);
        this.paintItem.setTag((Object) null);
        this.cropItem.setVisibility(8);
        this.tuneItem.setVisibility(8);
        this.tuneItem.setTag((Object) null);
        this.timeItem.setVisibility(8);
        this.rotateItem.setVisibility(8);
        this.pickerView.getLayoutParams().height = -2;
        this.docInfoTextView.setVisibility(8);
        this.docNameTextView.setVisibility(8);
        this.videoTimelineView.setVisibility(8);
        this.compressItem.setVisibility(8);
        this.captionEditText.setVisibility(8);
        this.mentionListView.setVisibility(8);
        this.muteItem.setVisibility(8);
        this.muteItem.setTag((Object) null);
        this.actionBar.setSubtitle((CharSequence) null);
        setItemVisible(this.masksItem, false, true);
        this.muteVideo = false;
        this.muteItem.setImageResource(NUM);
        this.editorDoneLayout.setVisibility(8);
        this.captionTextViewSwitcher.setTag((Object) null);
        this.captionTextViewSwitcher.setVisibility(4);
        PhotoCropView photoCropView2 = this.photoCropView;
        if (photoCropView2 != null) {
            photoCropView2.setVisibility(8);
        }
        PhotoFilterView photoFilterView2 = this.photoFilterView;
        if (photoFilterView2 != null) {
            photoFilterView2.setVisibility(8);
        }
        for (int i4 = 0; i4 < 3; i4++) {
            PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
            if (photoProgressViewArr[i4] != null) {
                photoProgressViewArr[i4].setBackgroundState(-1, false);
            }
        }
        if (messageObject3 != null && arrayList4 == null) {
            TLRPC$MessageMedia tLRPC$MessageMedia = messageObject3.messageOwner.media;
            if (!(!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) || (tLRPC$WebPage = tLRPC$MessageMedia.webpage) == null || (str = tLRPC$WebPage.site_name) == null)) {
                String lowerCase = str.toLowerCase();
                if (lowerCase.equals("instagram") || lowerCase.equals("twitter") || "telegram_album".equals(tLRPC$WebPage.type)) {
                    if (!TextUtils.isEmpty(tLRPC$WebPage.author)) {
                        this.nameOverride = tLRPC$WebPage.author;
                    }
                    if (tLRPC$WebPage.cached_page instanceof TLRPC$TL_page) {
                        int i5 = 0;
                        while (true) {
                            if (i5 >= tLRPC$WebPage.cached_page.blocks.size()) {
                                break;
                            }
                            TLRPC$PageBlock tLRPC$PageBlock = tLRPC$WebPage.cached_page.blocks.get(i5);
                            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockAuthorDate) {
                                this.dateOverride = ((TLRPC$TL_pageBlockAuthorDate) tLRPC$PageBlock).published_date;
                                break;
                            }
                            i5++;
                        }
                    }
                    ArrayList<MessageObject> webPagePhotos = messageObject3.getWebPagePhotos((ArrayList<MessageObject>) null, (ArrayList<TLRPC$PageBlock>) null);
                    if (!webPagePhotos.isEmpty()) {
                        this.slideshowMessageId = messageObject.getId();
                        this.needSearchImageInArr = false;
                        this.imagesArr.addAll(webPagePhotos);
                        this.totalImagesCount = this.imagesArr.size();
                        int indexOf = this.imagesArr.indexOf(messageObject3);
                        if (indexOf < 0) {
                            indexOf = 0;
                        }
                        setImageIndex(indexOf);
                    }
                }
            }
            if (messageObject.canPreviewDocument()) {
                this.sharedMediaType = 1;
                this.allMediaItem.setText(LocaleController.getString("ShowAllFiles", NUM));
            } else if (messageObject.isGif()) {
                this.sharedMediaType = 5;
                this.allMediaItem.setText(LocaleController.getString("ShowAllGIFs", NUM));
            }
            if (this.slideshowMessageId == 0) {
                this.imagesArr.add(messageObject3);
                if (messageObject3.eventId != 0) {
                    this.needSearchImageInArr = false;
                } else if (this.currentAnimation != null) {
                    this.needSearchImageInArr = false;
                    if (messageObject.canForwardMessage()) {
                        setItemVisible(this.sendItem, true, false);
                    }
                } else if (!messageObject3.scheduled) {
                    TLRPC$Message tLRPC$Message = messageObject3.messageOwner;
                    TLRPC$MessageMedia tLRPC$MessageMedia2 = tLRPC$Message.media;
                    if (!(tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaInvoice) && !(tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaWebPage) && ((tLRPC$MessageAction = tLRPC$Message.action) == null || (tLRPC$MessageAction instanceof TLRPC$TL_messageActionEmpty))) {
                        this.needSearchImageInArr = true;
                        this.imagesByIds[0].put(messageObject.getId(), messageObject3);
                        this.menuItem.showSubItem(4);
                        this.menuItem.showSubItem(2);
                        setItemVisible(this.sendItem, true, false);
                    }
                }
                setImageIndex(0);
            }
        } else if (arrayList5 != null) {
            this.secureDocuments.addAll(arrayList5);
            setImageIndex(i2);
        } else if (tLRPC$FileLocation != null) {
            int i6 = placeProviderObject2.dialogId;
            this.avatarsDialogId = i6;
            if (imageLocation == null) {
                imageLocation2 = i6 > 0 ? ImageLocation.getForUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.avatarsDialogId)), true) : ImageLocation.getForChat(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-this.avatarsDialogId)), true);
            } else {
                imageLocation2 = imageLocation;
            }
            if (imageLocation2 != null) {
                this.imagesArrLocations.add(imageLocation2);
                this.currentUserAvatarLocation = imageLocation2;
                this.imagesArrLocationsSizes.add(Integer.valueOf(placeProviderObject2.size));
                this.avatarsArr.add(new TLRPC$TL_photoEmpty());
                this.shareButton.setVisibility(!this.videoPlayerControlVisible ? 0 : 8);
                this.allowShare = true;
                this.menuItem.hideSubItem(2);
                if (this.shareButton.getVisibility() == 0) {
                    this.menuItem.hideSubItem(10);
                } else {
                    this.menuItem.showSubItem(10);
                }
                setImageIndex(0);
            } else {
                return;
            }
        } else if (arrayList4 != null) {
            this.imagesArr.addAll(arrayList4);
            for (int i7 = 0; i7 < this.imagesArr.size(); i7++) {
                MessageObject messageObject4 = this.imagesArr.get(i7);
                this.imagesByIds[messageObject4.getDialogId() == this.currentDialogId ? (char) 0 : 1].put(messageObject4.getId(), messageObject4);
            }
            MessageObject messageObject5 = this.imagesArr.get(i2);
            if (!messageObject5.scheduled) {
                this.opennedFromMedia = true;
                this.menuItem.showSubItem(4);
                if (messageObject5.canForwardMessage()) {
                    setItemVisible(this.sendItem, true, false);
                }
                if (messageObject5.canPreviewDocument()) {
                    this.sharedMediaType = 1;
                    this.allMediaItem.setText(LocaleController.getString("ShowAllFiles", NUM));
                } else if (messageObject5.isGif()) {
                    this.sharedMediaType = 5;
                    this.allMediaItem.setText(LocaleController.getString("ShowAllGIFs", NUM));
                }
            } else {
                this.totalImagesCount = this.imagesArr.size();
            }
            setImageIndex(i2);
        } else if (arrayList6 != null) {
            int i8 = this.sendPhotoType;
            if (i8 == 0 || i8 == 4 || ((i8 == 2 || i8 == 5) && arrayList3.size() > 1)) {
                this.checkImageView.setVisibility(0);
                this.photosCounterView.setVisibility(0);
                this.actionBar.setTitleRightMargin(AndroidUtilities.dp(100.0f));
            }
            int i9 = this.sendPhotoType;
            if ((i9 == 2 || i9 == 5) && this.placeProvider.canCaptureMorePhotos()) {
                this.cameraItem.setVisibility(0);
                this.cameraItem.setTag(1);
            }
            this.menuItem.setVisibility(8);
            this.imagesArrLocals.addAll(arrayList6);
            Object obj = this.imagesArrLocals.get(i2);
            if (obj instanceof MediaController.PhotoEntry) {
                int i10 = this.sendPhotoType;
                if (i10 == 10) {
                    this.cropItem.setVisibility(8);
                    this.rotateItem.setVisibility(8);
                } else if (this.isDocumentsPicker) {
                    this.cropItem.setVisibility(8);
                    this.rotateItem.setVisibility(8);
                    this.docInfoTextView.setVisibility(0);
                    this.docNameTextView.setVisibility(0);
                    this.pickerView.getLayoutParams().height = AndroidUtilities.dp(84.0f);
                } else if (((MediaController.PhotoEntry) obj).isVideo) {
                    this.cropItem.setVisibility(8);
                    this.rotateItem.setVisibility(8);
                    this.bottomLayout.setVisibility(0);
                    this.bottomLayout.setTag(1);
                    this.bottomLayout.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                } else {
                    this.cropItem.setVisibility(i10 != 1 ? 0 : 8);
                    this.rotateItem.setVisibility(this.sendPhotoType != 1 ? 8 : 0);
                }
                z = !this.isDocumentsPicker;
            } else {
                if (obj instanceof TLRPC$BotInlineResult) {
                    this.cropItem.setVisibility(8);
                    this.rotateItem.setVisibility(8);
                } else {
                    this.cropItem.setVisibility((!(obj instanceof MediaController.SearchImage) || ((MediaController.SearchImage) obj).type != 0) ? 8 : 0);
                    this.rotateItem.setVisibility(8);
                    if (this.cropItem.getVisibility() == 0) {
                        z = true;
                    }
                }
                z = false;
            }
            ChatActivity chatActivity2 = this.parentChatActivity;
            if (chatActivity2 != null && ((tLRPC$EncryptedChat = chatActivity2.currentEncryptedChat) == null || AndroidUtilities.getPeerLayerVersion(tLRPC$EncryptedChat.layer) >= 46)) {
                this.mentionsAdapter.setChatInfo(this.parentChatActivity.chatInfo);
                this.mentionsAdapter.setNeedUsernames(this.parentChatActivity.currentChat != null);
                this.mentionsAdapter.setNeedBotContext(false);
                boolean z3 = z && ((photoViewerProvider = this.placeProvider) == null || (photoViewerProvider != null && photoViewerProvider.allowCaption()));
                this.needCaptionLayout = z3;
                this.captionEditText.setVisibility(z3 ? 0 : 8);
                if (this.needCaptionLayout) {
                    this.captionEditText.onCreate();
                }
            }
            this.pickerView.setVisibility(0);
            this.pickerViewSendButton.setVisibility(0);
            this.pickerViewSendButton.setTranslationY(0.0f);
            this.pickerViewSendButton.setAlpha(1.0f);
            this.bottomLayout.setVisibility(8);
            this.bottomLayout.setTag((Object) null);
            this.containerView.setTag((Object) null);
            setImageIndex(i2);
            int i11 = this.sendPhotoType;
            if (i11 == 1) {
                this.paintItem.setVisibility(0);
                this.tuneItem.setVisibility(0);
            } else if (i11 == 4 || i11 == 5) {
                this.paintItem.setVisibility(8);
                this.tuneItem.setVisibility(8);
            } else {
                ImageView imageView = this.paintItem;
                imageView.setVisibility(imageView.getTag() != null ? 0 : 8);
                ImageView imageView2 = this.tuneItem;
                imageView2.setVisibility(imageView2.getTag() != null ? 0 : 8);
            }
            updateSelectedCount();
        }
        if (this.currentAnimation == null && !this.isEvent) {
            if (this.currentDialogId != 0 && this.totalImagesCount == 0 && (messageObject2 = this.currentMessageObject) != null && !messageObject2.scheduled) {
                MediaDataController.getInstance(this.currentAccount).getMediaCount(this.currentDialogId, this.sharedMediaType, this.classGuid, true);
                if (this.mergeDialogId != 0) {
                    MediaDataController.getInstance(this.currentAccount).getMediaCount(this.mergeDialogId, this.sharedMediaType, this.classGuid, true);
                }
            } else if (this.avatarsDialogId != 0) {
                MessagesController.getInstance(this.currentAccount).loadDialogPhotos(this.avatarsDialogId, 80, 0, true, this.classGuid);
            }
        }
        MessageObject messageObject6 = this.currentMessageObject;
        if ((messageObject6 != null && messageObject6.isVideo()) || ((tLRPC$BotInlineResult = this.currentBotInlineResult) != null && (tLRPC$BotInlineResult.type.equals("video") || MessageObject.isVideoDocument(this.currentBotInlineResult.document)))) {
            this.playerAutoStarted = true;
            onActionClick(false);
        } else if (!this.imagesArrLocals.isEmpty()) {
            Object obj2 = this.imagesArrLocals.get(i2);
            ChatActivity chatActivity3 = this.parentChatActivity;
            if (chatActivity3 != null) {
                tLRPC$User = chatActivity3.getCurrentUser();
            }
            boolean z4 = !this.isDocumentsPicker && (chatActivity = this.parentChatActivity) != null && !chatActivity.isSecretChat() && !this.parentChatActivity.isInScheduleMode() && tLRPC$User != null && !tLRPC$User.bot && !UserObject.isUserSelf(tLRPC$User) && !this.parentChatActivity.isEditingMessageMedia();
            if (obj2 instanceof TLRPC$BotInlineResult) {
                z4 = false;
            } else if (!(obj2 instanceof MediaController.PhotoEntry) && z4 && (obj2 instanceof MediaController.SearchImage)) {
                if (((MediaController.SearchImage) obj2).type != 0) {
                    z2 = false;
                }
                z4 = z2;
            }
            if (z4) {
                this.timeItem.setVisibility(0);
            }
        }
    }

    private void setDoubleTapEnabled(boolean z) {
        this.doubleTapEnabled = z;
        this.gestureDetector.setOnDoubleTapListener(z ? this : null);
    }

    /* access modifiers changed from: private */
    public void setImages() {
        if (this.animationInProgress == 0) {
            setIndexToImage(this.centerImage, this.currentIndex);
            setIndexToImage(this.rightImage, this.currentIndex + 1);
            setIndexToImage(this.leftImage, this.currentIndex - 1);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:256:0x05fd  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x06b8  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x0765  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x078c  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x07b0  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x07bf  */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x07db  */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x0803  */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x086f  */
    /* JADX WARNING: Removed duplicated region for block: B:352:0x0871  */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x0879  */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x087b  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x0883  */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x0885  */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x088d  */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x088f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setIsAboutToSwitchToIndex(int r29, boolean r30, boolean r31) {
        /*
            r28 = this;
            r0 = r28
            r1 = r29
            if (r30 != 0) goto L_0x000b
            int r2 = r0.switchingToIndex
            if (r2 != r1) goto L_0x000b
            return
        L_0x000b:
            r0.switchingToIndex = r1
            java.lang.String r2 = r28.getFileName(r29)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            boolean r3 = r3.isEmpty()
            java.lang.String r7 = "AttachVideo"
            java.lang.String r9 = "AttachPhoto"
            java.lang.String r12 = "Of"
            r13 = 6
            java.lang.String r14 = ""
            r6 = 0
            r11 = 1
            r4 = 0
            if (r3 != 0) goto L_0x04a6
            int r1 = r0.switchingToIndex
            if (r1 < 0) goto L_0x04a5
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            int r3 = r3.size()
            if (r1 < r3) goto L_0x0033
            goto L_0x04a5
        L_0x0033:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r3 = r0.switchingToIndex
            java.lang.Object r1 = r1.get(r3)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            boolean r3 = r1.isVideo()
            boolean r5 = r1.isInvoice()
            r10 = 11
            if (r5 == 0) goto L_0x007b
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.masksItem
            r0.setItemVisible(r2, r4, r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.menuItem
            r2.hideSubItem(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.menuItem
            r2.hideSubItem(r10)
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            java.lang.String r2 = r2.description
            r0.allowShare = r4
            android.widget.FrameLayout r3 = r0.bottomLayout
            r10 = 1111490560(0x42400000, float:48.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r14 = (float) r14
            r3.setTranslationY(r14)
            org.telegram.ui.PhotoViewer$CaptionTextViewSwitcher r3 = r0.captionTextViewSwitcher
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r3.setTranslationY(r10)
            r8 = r7
            r16 = r9
            goto L_0x021c
        L_0x007b:
            boolean r16 = r1.isNewGif()
            if (r16 == 0) goto L_0x0090
            r16 = r9
            long r8 = r0.currentDialogId
            int r9 = (int) r8
            if (r9 == 0) goto L_0x0092
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.menuItem
            r9 = 14
            r8.showSubItem(r9)
            goto L_0x0092
        L_0x0090:
            r16 = r9
        L_0x0092:
            org.telegram.ui.ChatActivity r8 = r0.parentChatActivity
            if (r8 == 0) goto L_0x009e
            boolean r8 = r8.isInScheduleMode()
            if (r8 == 0) goto L_0x009e
            r8 = 1
            goto L_0x009f
        L_0x009e:
            r8 = 0
        L_0x009f:
            boolean r8 = r1.canDeleteMessage(r8, r6)
            if (r8 == 0) goto L_0x00af
            int r8 = r0.slideshowMessageId
            if (r8 != 0) goto L_0x00af
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.menuItem
            r8.showSubItem(r13)
            goto L_0x00b4
        L_0x00af:
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.menuItem
            r8.hideSubItem(r13)
        L_0x00b4:
            if (r3 == 0) goto L_0x0102
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.menuItem
            r8.showSubItem(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.masksItem
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x00c5
            r8 = 1
            goto L_0x00c6
        L_0x00c5:
            r8 = 0
        L_0x00c6:
            if (r8 == 0) goto L_0x00cd
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.masksItem
            r0.setItemVisible(r9, r4, r4)
        L_0x00cd:
            boolean r9 = r0.pipAvailable
            if (r9 != 0) goto L_0x00df
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.pipItem
            r9.setEnabled(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.pipItem
            r8 = r8 ^ r11
            r10 = 1056964608(0x3var_, float:0.5)
            r0.setItemVisible(r9, r11, r8, r10)
            goto L_0x00e5
        L_0x00df:
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.pipItem
            r8 = r8 ^ r11
            r0.setItemVisible(r9, r11, r8)
        L_0x00e5:
            boolean r8 = r1.hasAttachedStickers()
            r9 = 15
            if (r8 == 0) goto L_0x00fb
            r8 = r7
            long r6 = r1.getDialogId()
            int r7 = (int) r6
            if (r7 == 0) goto L_0x00fc
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.menuItem
            r6.showSubItem(r9)
            goto L_0x0132
        L_0x00fb:
            r8 = r7
        L_0x00fc:
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.menuItem
            r6.hideSubItem(r9)
            goto L_0x0132
        L_0x0102:
            r8 = r7
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.menuItem
            r6.hideSubItem(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.pipItem
            int r6 = r6.getVisibility()
            if (r6 != 0) goto L_0x0112
            r6 = 1
            goto L_0x0113
        L_0x0112:
            r6 = 0
        L_0x0113:
            boolean r7 = r1.hasAttachedStickers()
            if (r7 == 0) goto L_0x0122
            long r9 = r1.getDialogId()
            int r7 = (int) r9
            if (r7 == 0) goto L_0x0122
            r7 = 1
            goto L_0x0123
        L_0x0122:
            r7 = 0
        L_0x0123:
            if (r6 == 0) goto L_0x012c
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.pipItem
            r10 = r7 ^ 1
            r0.setItemVisible(r9, r4, r10)
        L_0x012c:
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r0.masksItem
            r6 = r6 ^ r11
            r0.setItemVisible(r9, r7, r6)
        L_0x0132:
            boolean r6 = r0.shouldMessageObjectAutoPlayed(r1)
            if (r6 != 0) goto L_0x021a
            boolean r6 = r0.playerWasPlaying
            r6 = r6 ^ r11
            java.lang.String r7 = r0.nameOverride
            if (r7 == 0) goto L_0x0146
            org.telegram.ui.Components.FadingTextViewLayout r9 = r0.nameTextView
            r9.setText(r7)
            goto L_0x01b7
        L_0x0146:
            boolean r7 = r1.isFromUser()
            if (r7 == 0) goto L_0x0170
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            org.telegram.tgnet.TLRPC$Message r9 = r1.messageOwner
            int r9 = r9.from_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r9)
            if (r7 == 0) goto L_0x016a
            org.telegram.ui.Components.FadingTextViewLayout r9 = r0.nameTextView
            java.lang.String r7 = org.telegram.messenger.UserObject.getUserName(r7)
            r9.setText(r7, r6)
            goto L_0x01b7
        L_0x016a:
            org.telegram.ui.Components.FadingTextViewLayout r7 = r0.nameTextView
            r7.setText(r14, r6)
            goto L_0x01b7
        L_0x0170:
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            org.telegram.tgnet.TLRPC$Message r9 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r9 = r9.to_id
            int r9 = r9.channel_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getChat(r9)
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r9 == 0) goto L_0x01a8
            boolean r9 = r7.megagroup
            if (r9 == 0) goto L_0x01a8
            boolean r9 = r1.isForwardedChannelPost()
            if (r9 == 0) goto L_0x01a8
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            org.telegram.tgnet.TLRPC$Message r9 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r9 = r9.fwd_from
            int r9 = r9.channel_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getChat(r9)
        L_0x01a8:
            if (r7 == 0) goto L_0x01b2
            org.telegram.ui.Components.FadingTextViewLayout r9 = r0.nameTextView
            java.lang.String r7 = r7.title
            r9.setText(r7, r6)
            goto L_0x01b7
        L_0x01b2:
            org.telegram.ui.Components.FadingTextViewLayout r7 = r0.nameTextView
            r7.setText(r14, r6)
        L_0x01b7:
            int r7 = r0.dateOverride
            if (r7 == 0) goto L_0x01bc
            goto L_0x01c0
        L_0x01bc:
            org.telegram.tgnet.TLRPC$Message r7 = r1.messageOwner
            int r7 = r7.date
        L_0x01c0:
            long r9 = (long) r7
            r17 = 1000(0x3e8, double:4.94E-321)
            long r9 = r9 * r17
            r7 = 2131627417(0x7f0e0d99, float:1.8882098E38)
            r14 = 2
            java.lang.Object[] r13 = new java.lang.Object[r14]
            org.telegram.messenger.LocaleController r14 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r14 = r14.formatterYear
            java.util.Date r15 = new java.util.Date
            r15.<init>(r9)
            java.lang.String r14 = r14.format((java.util.Date) r15)
            r13[r4] = r14
            org.telegram.messenger.LocaleController r14 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r14 = r14.formatterDay
            java.util.Date r15 = new java.util.Date
            r15.<init>(r9)
            java.lang.String r9 = r14.format((java.util.Date) r15)
            r13[r11] = r9
            java.lang.String r9 = "formatDateAtTime"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r9, r7, r13)
            if (r2 == 0) goto L_0x0215
            if (r3 == 0) goto L_0x0215
            org.telegram.ui.Components.FadingTextViewLayout r2 = r0.dateTextView
            r3 = 2
            java.lang.Object[] r9 = new java.lang.Object[r3]
            r9[r4] = r7
            org.telegram.tgnet.TLRPC$Document r3 = r1.getDocument()
            int r3 = r3.size
            long r13 = (long) r3
            java.lang.String r3 = org.telegram.messenger.AndroidUtilities.formatFileSize(r13)
            r9[r11] = r3
            java.lang.String r3 = "%s (%s)"
            java.lang.String r3 = java.lang.String.format(r3, r9)
            r2.setText(r3, r6)
            goto L_0x021a
        L_0x0215:
            org.telegram.ui.Components.FadingTextViewLayout r2 = r0.dateTextView
            r2.setText(r7, r6)
        L_0x021a:
            java.lang.CharSequence r2 = r1.caption
        L_0x021c:
            org.telegram.ui.Components.AnimatedFileDrawable r3 = r0.currentAnimation
            if (r3 == 0) goto L_0x025d
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r3.hideSubItem(r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r5 = 10
            r3.hideSubItem(r5)
            org.telegram.ui.ChatActivity r3 = r0.parentChatActivity
            if (r3 == 0) goto L_0x0238
            boolean r3 = r3.isInScheduleMode()
            if (r3 == 0) goto L_0x0238
            r3 = 1
            goto L_0x0239
        L_0x0238:
            r3 = 0
        L_0x0239:
            r5 = 0
            boolean r3 = r1.canDeleteMessage(r3, r5)
            if (r3 != 0) goto L_0x0246
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r5 = 6
            r3.hideSubItem(r5)
        L_0x0246:
            r0.allowShare = r11
            android.widget.ImageView r3 = r0.shareButton
            r3.setVisibility(r4)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r4 = 2131624297(0x7f0e0169, float:1.887577E38)
            java.lang.String r5 = "AttachGif"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setTitle(r4)
            goto L_0x049a
        L_0x025d:
            int r3 = r0.totalImagesCount
            int r6 = r0.totalImagesCountMerge
            int r3 = r3 + r6
            if (r3 == 0) goto L_0x03d2
            boolean r3 = r0.needSearchImageInArr
            if (r3 != 0) goto L_0x03d2
            boolean r3 = r0.opennedFromMedia
            if (r3 == 0) goto L_0x0321
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            int r3 = r3.size()
            int r5 = r0.totalImagesCount
            int r6 = r0.totalImagesCountMerge
            int r5 = r5 + r6
            if (r3 >= r5) goto L_0x02fc
            boolean r3 = r0.loadingMoreImages
            if (r3 != 0) goto L_0x02fc
            int r3 = r0.switchingToIndex
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            int r5 = r5.size()
            r6 = 5
            int r5 = r5 - r6
            if (r3 <= r5) goto L_0x02fc
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0293
            r3 = 0
            goto L_0x02a4
        L_0x0293:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            int r5 = r3.size()
            int r5 = r5 - r11
            java.lang.Object r3 = r3.get(r5)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            int r3 = r3.getId()
        L_0x02a4:
            boolean[] r5 = r0.endReached
            boolean r5 = r5[r4]
            if (r5 == 0) goto L_0x02d9
            long r5 = r0.mergeDialogId
            r7 = 0
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x02d9
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x02d5
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            int r6 = r5.size()
            int r6 = r6 - r11
            java.lang.Object r5 = r5.get(r6)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            long r5 = r5.getDialogId()
            long r7 = r0.mergeDialogId
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x02d5
            r3 = 1
            r23 = 0
            goto L_0x02dc
        L_0x02d5:
            r23 = r3
            r3 = 1
            goto L_0x02dc
        L_0x02d9:
            r23 = r3
            r3 = 0
        L_0x02dc:
            int r5 = r0.currentAccount
            org.telegram.messenger.MediaDataController r19 = org.telegram.messenger.MediaDataController.getInstance(r5)
            if (r3 != 0) goto L_0x02e7
            long r5 = r0.currentDialogId
            goto L_0x02e9
        L_0x02e7:
            long r5 = r0.mergeDialogId
        L_0x02e9:
            r20 = r5
            r22 = 40
            int r3 = r0.sharedMediaType
            r25 = 1
            int r5 = r0.classGuid
            r24 = r3
            r26 = r5
            r19.loadMedia(r20, r22, r23, r24, r25, r26)
            r0.loadingMoreImages = r11
        L_0x02fc:
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            int r6 = r0.switchingToIndex
            int r6 = r6 + r11
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r4] = r6
            int r6 = r0.totalImagesCount
            int r7 = r0.totalImagesCountMerge
            int r6 = r6 + r7
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r11] = r6
            r6 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r6, r5)
            r3.setTitle(r5)
            goto L_0x0445
        L_0x0321:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            int r3 = r3.size()
            int r5 = r0.totalImagesCount
            int r6 = r0.totalImagesCountMerge
            int r5 = r5 + r6
            if (r3 >= r5) goto L_0x03a0
            boolean r3 = r0.loadingMoreImages
            if (r3 != 0) goto L_0x03a0
            int r3 = r0.switchingToIndex
            r5 = 5
            if (r3 >= r5) goto L_0x03a0
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0341
            r3 = 0
            goto L_0x034d
        L_0x0341:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            java.lang.Object r3 = r3.get(r4)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            int r3 = r3.getId()
        L_0x034d:
            boolean[] r5 = r0.endReached
            boolean r5 = r5[r4]
            if (r5 == 0) goto L_0x037d
            long r5 = r0.mergeDialogId
            r7 = 0
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x037d
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x0379
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            java.lang.Object r5 = r5.get(r4)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            long r5 = r5.getDialogId()
            long r7 = r0.mergeDialogId
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x0379
            r3 = 1
            r23 = 0
            goto L_0x0380
        L_0x0379:
            r23 = r3
            r3 = 1
            goto L_0x0380
        L_0x037d:
            r23 = r3
            r3 = 0
        L_0x0380:
            int r5 = r0.currentAccount
            org.telegram.messenger.MediaDataController r19 = org.telegram.messenger.MediaDataController.getInstance(r5)
            if (r3 != 0) goto L_0x038b
            long r5 = r0.currentDialogId
            goto L_0x038d
        L_0x038b:
            long r5 = r0.mergeDialogId
        L_0x038d:
            r20 = r5
            r22 = 80
            int r3 = r0.sharedMediaType
            r25 = 1
            int r5 = r0.classGuid
            r24 = r3
            r26 = r5
            r19.loadMedia(r20, r22, r23, r24, r25, r26)
            r0.loadingMoreImages = r11
        L_0x03a0:
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            int r6 = r0.totalImagesCount
            int r7 = r0.totalImagesCountMerge
            int r6 = r6 + r7
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r0.imagesArr
            int r7 = r7.size()
            int r6 = r6 - r7
            int r7 = r0.switchingToIndex
            int r6 = r6 + r7
            int r6 = r6 + r11
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r4] = r6
            int r6 = r0.totalImagesCount
            int r7 = r0.totalImagesCountMerge
            int r6 = r6 + r7
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r11] = r6
            r6 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r6, r5)
            r3.setTitle(r5)
            goto L_0x0445
        L_0x03d2:
            int r3 = r0.slideshowMessageId
            r6 = 2131624295(0x7f0e0167, float:1.8875766E38)
            java.lang.String r7 = "AttachDocument"
            if (r3 != 0) goto L_0x0415
            org.telegram.tgnet.TLRPC$Message r3 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r3 == 0) goto L_0x0415
            boolean r3 = r1.canPreviewDocument()
            if (r3 == 0) goto L_0x03f3
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r3.setTitle(r5)
            goto L_0x0445
        L_0x03f3:
            boolean r3 = r1.isVideo()
            if (r3 == 0) goto L_0x0406
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r5 = 2131624314(0x7f0e017a, float:1.8875804E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r3.setTitle(r5)
            goto L_0x0445
        L_0x0406:
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r6 = r16
            r5 = 2131624308(0x7f0e0174, float:1.8875792E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setTitle(r5)
            goto L_0x0445
        L_0x0415:
            if (r5 == 0) goto L_0x0423
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            org.telegram.tgnet.TLRPC$Message r5 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            java.lang.String r5 = r5.title
            r3.setTitle(r5)
            goto L_0x0445
        L_0x0423:
            boolean r3 = r1.isVideo()
            if (r3 == 0) goto L_0x0436
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r5 = 2131624314(0x7f0e017a, float:1.8875804E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r3.setTitle(r5)
            goto L_0x0445
        L_0x0436:
            org.telegram.tgnet.TLRPC$Document r3 = r1.getDocument()
            if (r3 == 0) goto L_0x0445
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r3.setTitle(r5)
        L_0x0445:
            long r5 = r0.currentDialogId
            int r3 = (int) r5
            if (r3 != 0) goto L_0x044f
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.sendItem
            r0.setItemVisible(r3, r4, r4)
        L_0x044f:
            org.telegram.tgnet.TLRPC$Message r3 = r1.messageOwner
            int r3 = r3.ttl
            if (r3 == 0) goto L_0x046f
            r5 = 3600(0xe10, float:5.045E-42)
            if (r3 >= r5) goto L_0x046f
            r0.allowShare = r4
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r3.hideSubItem(r11)
            android.widget.ImageView r3 = r0.shareButton
            r4 = 8
            r3.setVisibility(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r4 = 10
            r3.hideSubItem(r4)
            goto L_0x049a
        L_0x046f:
            r0.allowShare = r11
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r3.showSubItem(r11)
            android.widget.ImageView r3 = r0.shareButton
            boolean r5 = r0.videoPlayerControlVisible
            if (r5 != 0) goto L_0x047e
            r11 = 0
            goto L_0x0480
        L_0x047e:
            r11 = 8
        L_0x0480:
            r3.setVisibility(r11)
            android.widget.ImageView r3 = r0.shareButton
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x0493
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r4 = 10
            r3.hideSubItem(r4)
            goto L_0x049a
        L_0x0493:
            r4 = 10
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r3.showSubItem(r4)
        L_0x049a:
            org.telegram.ui.Components.GroupedPhotosListView r3 = r0.groupedPhotosListView
            r3.fillList()
            r7 = r31
            r6 = r1
            r15 = r2
            goto L_0x089a
        L_0x04a5:
            return
        L_0x04a6:
            r8 = r7
            r6 = r9
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r2 = r0.secureDocuments
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x04e8
            r0.allowShare = r4
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.hideSubItem(r11)
            org.telegram.ui.Components.FadingTextViewLayout r1 = r0.nameTextView
            r1.setText(r14)
            org.telegram.ui.Components.FadingTextViewLayout r1 = r0.dateTextView
            r1.setText(r14)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            int r3 = r0.switchingToIndex
            int r3 = r3 + r11
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2[r4] = r3
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r3 = r0.secureDocuments
            int r3 = r3.size()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2[r11] = r3
            r3 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r3, r2)
            r1.setTitle(r2)
        L_0x04e5:
            r15 = 0
            goto L_0x0897
        L_0x04e8:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r2 = r0.imagesArrLocations
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0591
            if (r1 < 0) goto L_0x0590
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r2 = r0.imagesArrLocations
            int r2 = r2.size()
            if (r1 < r2) goto L_0x04fc
            goto L_0x0590
        L_0x04fc:
            org.telegram.ui.Components.FadingTextViewLayout r1 = r0.nameTextView
            r1.setText(r14)
            org.telegram.ui.Components.FadingTextViewLayout r1 = r0.dateTextView
            r1.setText(r14)
            int r1 = r0.avatarsDialogId
            int r2 = r0.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.getClientUserId()
            if (r1 != r2) goto L_0x0523
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r1 = r0.avatarsArr
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0523
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r2 = 6
            r1.showSubItem(r2)
            goto L_0x0529
        L_0x0523:
            r2 = 6
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.hideSubItem(r2)
        L_0x0529:
            boolean r1 = r0.isEvent
            if (r1 == 0) goto L_0x053a
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r2 = 2131624308(0x7f0e0174, float:1.8875792E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r1.setTitle(r2)
            goto L_0x055e
        L_0x053a:
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            int r3 = r0.switchingToIndex
            int r3 = r3 + r11
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2[r4] = r3
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r3 = r0.imagesArrLocations
            int r3 = r3.size()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2[r11] = r3
            r3 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r3, r2)
            r1.setTitle(r2)
        L_0x055e:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.showSubItem(r11)
            r0.allowShare = r11
            android.widget.ImageView r1 = r0.shareButton
            boolean r2 = r0.videoPlayerControlVisible
            if (r2 != 0) goto L_0x056d
            r11 = 0
            goto L_0x056f
        L_0x056d:
            r11 = 8
        L_0x056f:
            r1.setVisibility(r11)
            android.widget.ImageView r1 = r0.shareButton
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0582
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r2 = 10
            r1.hideSubItem(r2)
            goto L_0x0589
        L_0x0582:
            r2 = 10
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.showSubItem(r2)
        L_0x0589:
            org.telegram.ui.Components.GroupedPhotosListView r1 = r0.groupedPhotosListView
            r1.fillList()
            goto L_0x04e5
        L_0x0590:
            return
        L_0x0591:
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x04e5
            if (r1 < 0) goto L_0x0896
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            int r2 = r2.size()
            if (r1 < r2) goto L_0x05a5
            goto L_0x0896
        L_0x05a5:
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            java.lang.Object r1 = r2.get(r1)
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$BotInlineResult
            r3 = 4
            if (r2 == 0) goto L_0x05d7
            r2 = r1
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = (org.telegram.tgnet.TLRPC$BotInlineResult) r2
            r0.currentBotInlineResult = r2
            org.telegram.tgnet.TLRPC$Document r5 = r2.document
            if (r5 == 0) goto L_0x05be
            boolean r2 = org.telegram.messenger.MessageObject.isVideoDocument(r5)
            goto L_0x05ce
        L_0x05be:
            org.telegram.tgnet.TLRPC$WebDocument r5 = r2.content
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r5 == 0) goto L_0x05cd
            java.lang.String r2 = r2.type
            java.lang.String r5 = "video"
            boolean r2 = r2.equals(r5)
            goto L_0x05ce
        L_0x05cd:
            r2 = 0
        L_0x05ce:
            r7 = r31
        L_0x05d0:
            r5 = 0
            r9 = 0
            r10 = 0
            r13 = 0
            r14 = 0
            goto L_0x07a6
        L_0x05d7:
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.PhotoEntry
            if (r2 == 0) goto L_0x05e5
            r5 = r1
            org.telegram.messenger.MediaController$PhotoEntry r5 = (org.telegram.messenger.MediaController.PhotoEntry) r5
            java.lang.String r7 = r5.path
            r0.currentPathObject = r7
            boolean r5 = r5.isVideo
            goto L_0x05fa
        L_0x05e5:
            boolean r5 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r5 == 0) goto L_0x05f9
            r5 = r1
            org.telegram.messenger.MediaController$SearchImage r5 = (org.telegram.messenger.MediaController.SearchImage) r5
            java.lang.String r7 = r5.getPathToAttach()
            r0.currentPathObject = r7
            int r5 = r5.type
            if (r5 != r11) goto L_0x05f9
            r5 = 0
            r7 = 1
            goto L_0x05fb
        L_0x05f9:
            r5 = 0
        L_0x05fa:
            r7 = 0
        L_0x05fb:
            if (r5 == 0) goto L_0x06b8
            boolean r7 = r0.isCurrentVideo
            if (r7 != 0) goto L_0x0603
            r7 = 0
            goto L_0x0605
        L_0x0603:
            r7 = r31
        L_0x0605:
            r0.isCurrentVideo = r11
            r9 = 0
            r10 = 1065353216(0x3var_, float:1.0)
            if (r2 == 0) goto L_0x061a
            r13 = r1
            org.telegram.messenger.MediaController$PhotoEntry r13 = (org.telegram.messenger.MediaController.PhotoEntry) r13
            org.telegram.messenger.VideoEditedInfo r13 = r13.editedInfo
            if (r13 == 0) goto L_0x061a
            boolean r9 = r13.muted
            float r10 = r13.start
            float r13 = r13.end
            goto L_0x061e
        L_0x061a:
            r9 = 0
            r10 = 0
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x061e:
            java.lang.String r14 = r0.currentPathObject
            r0.processOpenVideo(r14, r9, r10, r13)
            boolean r9 = r0.isDocumentsPicker
            if (r9 == 0) goto L_0x063f
            org.telegram.ui.Components.VideoTimelinePlayView r9 = r0.videoTimelineView
            r10 = 8
            r9.setVisibility(r10)
            android.widget.ImageView r9 = r0.muteItem
            r9.setVisibility(r10)
            android.widget.ImageView r9 = r0.muteItem
            r13 = 0
            r9.setTag(r13)
            android.widget.ImageView r9 = r0.compressItem
            r9.setVisibility(r10)
            goto L_0x0657
        L_0x063f:
            org.telegram.ui.Components.VideoTimelinePlayView r9 = r0.videoTimelineView
            r9.setVisibility(r4)
            android.widget.ImageView r9 = r0.muteItem
            r9.setVisibility(r4)
            android.widget.ImageView r9 = r0.muteItem
            java.lang.Integer r10 = java.lang.Integer.valueOf(r11)
            r9.setTag(r10)
            android.widget.ImageView r9 = r0.compressItem
            r9.setVisibility(r4)
        L_0x0657:
            boolean r9 = r0.isDocumentsPicker
            if (r9 != 0) goto L_0x0690
            int r9 = android.os.Build.VERSION.SDK_INT
            r10 = 18
            if (r9 >= r10) goto L_0x0662
            goto L_0x0690
        L_0x0662:
            android.widget.ImageView r9 = r0.cropItem
            r9.setVisibility(r4)
            android.widget.ImageView r9 = r0.cropItem
            java.lang.Integer r10 = java.lang.Integer.valueOf(r11)
            r9.setTag(r10)
            android.widget.ImageView r9 = r0.tuneItem
            r9.setVisibility(r4)
            android.widget.ImageView r9 = r0.tuneItem
            java.lang.Integer r10 = java.lang.Integer.valueOf(r11)
            r9.setTag(r10)
            android.widget.ImageView r9 = r0.paintItem
            r9.setVisibility(r4)
            android.widget.ImageView r9 = r0.paintItem
            java.lang.Integer r10 = java.lang.Integer.valueOf(r11)
            r9.setTag(r10)
            r10 = 8
            r13 = 0
            goto L_0x06b1
        L_0x0690:
            android.widget.ImageView r9 = r0.cropItem
            r10 = 8
            r9.setVisibility(r10)
            android.widget.ImageView r9 = r0.cropItem
            r13 = 0
            r9.setTag(r13)
            android.widget.ImageView r9 = r0.tuneItem
            r9.setVisibility(r10)
            android.widget.ImageView r9 = r0.tuneItem
            r9.setTag(r13)
            android.widget.ImageView r9 = r0.paintItem
            r9.setVisibility(r10)
            android.widget.ImageView r9 = r0.paintItem
            r9.setTag(r13)
        L_0x06b1:
            android.widget.ImageView r9 = r0.rotateItem
            r9.setVisibility(r10)
            goto L_0x0763
        L_0x06b8:
            r10 = 8
            r13 = 0
            org.telegram.ui.Components.VideoTimelinePlayView r9 = r0.videoTimelineView
            r9.setVisibility(r10)
            android.widget.ImageView r9 = r0.muteItem
            r9.setVisibility(r10)
            android.widget.ImageView r9 = r0.muteItem
            r9.setTag(r13)
            boolean r9 = r0.isCurrentVideo
            if (r9 == 0) goto L_0x06d0
            r9 = 0
            goto L_0x06d2
        L_0x06d0:
            r9 = r31
        L_0x06d2:
            r0.isCurrentVideo = r4
            android.widget.ImageView r13 = r0.compressItem
            r13.setVisibility(r10)
            if (r7 != 0) goto L_0x073c
            int r7 = r0.sendPhotoType
            r10 = 10
            if (r7 == r10) goto L_0x073c
            boolean r10 = r0.isDocumentsPicker
            if (r10 == 0) goto L_0x06e6
            goto L_0x073c
        L_0x06e6:
            if (r7 == r3) goto L_0x0709
            r10 = 5
            if (r7 != r10) goto L_0x06ec
            goto L_0x0709
        L_0x06ec:
            android.widget.ImageView r7 = r0.paintItem
            r7.setVisibility(r4)
            android.widget.ImageView r7 = r0.paintItem
            java.lang.Integer r10 = java.lang.Integer.valueOf(r11)
            r7.setTag(r10)
            android.widget.ImageView r7 = r0.tuneItem
            r7.setVisibility(r4)
            android.widget.ImageView r7 = r0.tuneItem
            java.lang.Integer r10 = java.lang.Integer.valueOf(r11)
            r7.setTag(r10)
            goto L_0x0720
        L_0x0709:
            android.widget.ImageView r7 = r0.paintItem
            r10 = 8
            r7.setVisibility(r10)
            android.widget.ImageView r7 = r0.paintItem
            r13 = 0
            r7.setTag(r13)
            android.widget.ImageView r7 = r0.tuneItem
            r7.setVisibility(r10)
            android.widget.ImageView r7 = r0.tuneItem
            r7.setTag(r13)
        L_0x0720:
            android.widget.ImageView r7 = r0.cropItem
            int r10 = r0.sendPhotoType
            if (r10 == r11) goto L_0x0728
            r10 = 0
            goto L_0x072a
        L_0x0728:
            r10 = 8
        L_0x072a:
            r7.setVisibility(r10)
            android.widget.ImageView r7 = r0.rotateItem
            int r10 = r0.sendPhotoType
            if (r10 == r11) goto L_0x0736
            r10 = 8
            goto L_0x0737
        L_0x0736:
            r10 = 0
        L_0x0737:
            r7.setVisibility(r10)
            r13 = 0
            goto L_0x075d
        L_0x073c:
            android.widget.ImageView r7 = r0.paintItem
            r10 = 8
            r7.setVisibility(r10)
            android.widget.ImageView r7 = r0.paintItem
            r13 = 0
            r7.setTag(r13)
            android.widget.ImageView r7 = r0.cropItem
            r7.setVisibility(r10)
            android.widget.ImageView r7 = r0.rotateItem
            r7.setVisibility(r10)
            android.widget.ImageView r7 = r0.tuneItem
            r7.setVisibility(r10)
            android.widget.ImageView r7 = r0.tuneItem
            r7.setTag(r13)
        L_0x075d:
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            r7.setSubtitle(r13)
            r7 = r9
        L_0x0763:
            if (r2 == 0) goto L_0x078c
            r2 = r1
            org.telegram.messenger.MediaController$PhotoEntry r2 = (org.telegram.messenger.MediaController.PhotoEntry) r2
            int r9 = r2.bucketId
            if (r9 != 0) goto L_0x077e
            long r9 = r2.dateTaken
            r13 = 0
            int r15 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r15 != 0) goto L_0x077e
            java.util.ArrayList<java.lang.Object> r9 = r0.imagesArrLocals
            int r9 = r9.size()
            if (r9 != r11) goto L_0x077e
            r9 = 1
            goto L_0x077f
        L_0x077e:
            r9 = 0
        L_0x077f:
            r0.fromCamera = r9
            java.lang.CharSequence r9 = r2.caption
            int r10 = r2.ttl
            boolean r13 = r2.isFiltered
            boolean r14 = r2.isPainted
            boolean r2 = r2.isCropped
            goto L_0x079d
        L_0x078c:
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r2 == 0) goto L_0x07a3
            r2 = r1
            org.telegram.messenger.MediaController$SearchImage r2 = (org.telegram.messenger.MediaController.SearchImage) r2
            java.lang.CharSequence r9 = r2.caption
            int r10 = r2.ttl
            boolean r13 = r2.isFiltered
            boolean r14 = r2.isPainted
            boolean r2 = r2.isCropped
        L_0x079d:
            r27 = r5
            r5 = r2
            r2 = r27
            goto L_0x07a6
        L_0x07a3:
            r2 = r5
            goto L_0x05d0
        L_0x07a6:
            android.widget.FrameLayout r15 = r0.bottomLayout
            int r15 = r15.getVisibility()
            r3 = 8
            if (r15 == r3) goto L_0x07b5
            android.widget.FrameLayout r15 = r0.bottomLayout
            r15.setVisibility(r3)
        L_0x07b5:
            android.widget.FrameLayout r3 = r0.bottomLayout
            r15 = 0
            r3.setTag(r15)
            boolean r3 = r0.fromCamera
            if (r3 == 0) goto L_0x07db
            if (r2 == 0) goto L_0x07ce
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r3 = 2131624314(0x7f0e017a, float:1.8875804E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            r2.setTitle(r3)
            goto L_0x07ff
        L_0x07ce:
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r3 = 2131624308(0x7f0e0174, float:1.8875792E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setTitle(r3)
            goto L_0x07ff
        L_0x07db:
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r3 = 2
            java.lang.Object[] r6 = new java.lang.Object[r3]
            int r3 = r0.switchingToIndex
            int r3 = r3 + r11
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r6[r4] = r3
            java.util.ArrayList<java.lang.Object> r3 = r0.imagesArrLocals
            int r3 = r3.size()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r6[r11] = r3
            r3 = 2131626024(0x7f0e0828, float:1.8879273E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r12, r3, r6)
            r2.setTitle(r3)
        L_0x07ff:
            org.telegram.ui.ChatActivity r2 = r0.parentChatActivity
            if (r2 == 0) goto L_0x0839
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getCurrentChat()
            if (r2 == 0) goto L_0x0811
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.String r2 = r2.title
            r3.setTitle(r2)
            goto L_0x0839
        L_0x0811:
            org.telegram.ui.ChatActivity r2 = r0.parentChatActivity
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()
            if (r2 == 0) goto L_0x0839
            boolean r3 = r2.self
            if (r3 == 0) goto L_0x082c
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r3 = 2131626648(0x7f0e0a98, float:1.8880538E38)
            java.lang.String r6 = "SavedMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r2.setTitle(r3)
            goto L_0x0839
        L_0x082c:
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.String r6 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r6, r2)
            r3.setTitle(r2)
        L_0x0839:
            int r2 = r0.sendPhotoType
            if (r2 == 0) goto L_0x084e
            r3 = 4
            if (r2 == r3) goto L_0x084e
            r3 = 2
            if (r2 == r3) goto L_0x0846
            r3 = 5
            if (r2 != r3) goto L_0x085b
        L_0x0846:
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            int r2 = r2.size()
            if (r2 <= r11) goto L_0x085b
        L_0x084e:
            org.telegram.ui.Components.CheckBox r2 = r0.checkImageView
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r3 = r0.placeProvider
            int r6 = r0.switchingToIndex
            boolean r3 = r3.isPhotoChecked(r6)
            r2.setChecked(r3, r4)
        L_0x085b:
            r0.updateCaptionTextForCurrentPhoto(r1)
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r2 = "dialogFloatingButton"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r3)
            android.widget.ImageView r2 = r0.timeItem
            if (r10 == 0) goto L_0x0871
            r3 = r1
            goto L_0x0872
        L_0x0871:
            r3 = r15
        L_0x0872:
            r2.setColorFilter(r3)
            android.widget.ImageView r2 = r0.paintItem
            if (r14 == 0) goto L_0x087b
            r3 = r1
            goto L_0x087c
        L_0x087b:
            r3 = r15
        L_0x087c:
            r2.setColorFilter(r3)
            android.widget.ImageView r2 = r0.cropItem
            if (r5 == 0) goto L_0x0885
            r5 = r1
            goto L_0x0886
        L_0x0885:
            r5 = r15
        L_0x0886:
            r2.setColorFilter(r5)
            android.widget.ImageView r2 = r0.tuneItem
            if (r13 == 0) goto L_0x088f
            r5 = r1
            goto L_0x0890
        L_0x088f:
            r5 = r15
        L_0x0890:
            r2.setColorFilter(r5)
            r6 = r15
            r15 = r9
            goto L_0x089a
        L_0x0896:
            return
        L_0x0897:
            r7 = r31
            r6 = r15
        L_0x089a:
            r0.setCurrentCaption(r6, r15, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.setIsAboutToSwitchToIndex(int, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    public TLRPC$FileLocation getFileLocation(ImageLocation imageLocation) {
        if (imageLocation == null) {
            return null;
        }
        return imageLocation.location;
    }

    /* access modifiers changed from: private */
    public void setImageIndex(int i) {
        setImageIndex(i, true, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x007d, code lost:
        r4 = r0.currentMessageObject;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setImageIndex(int r17, boolean r18, boolean r19) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            int r2 = r0.currentIndex
            if (r2 == r1) goto L_0x03bd
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r2 = r0.placeProvider
            if (r2 != 0) goto L_0x000e
            goto L_0x03bd
        L_0x000e:
            r2 = 0
            if (r18 != 0) goto L_0x001a
            org.telegram.messenger.ImageReceiver$BitmapHolder r3 = r0.currentThumb
            if (r3 == 0) goto L_0x001a
            r3.release()
            r0.currentThumb = r2
        L_0x001a:
            java.lang.String[] r3 = r0.currentFileNames
            java.lang.String r4 = r16.getFileName(r17)
            r5 = 0
            r3[r5] = r4
            java.lang.String[] r3 = r0.currentFileNames
            int r4 = r1 + 1
            java.lang.String r4 = r0.getFileName(r4)
            r6 = 1
            r3[r6] = r4
            java.lang.String[] r3 = r0.currentFileNames
            int r4 = r1 + -1
            java.lang.String r4 = r0.getFileName(r4)
            r7 = 2
            r3[r7] = r4
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r3 = r0.placeProvider
            org.telegram.messenger.MessageObject r4 = r0.currentMessageObject
            org.telegram.messenger.ImageLocation r8 = r0.currentFileLocation
            org.telegram.tgnet.TLRPC$FileLocation r8 = r0.getFileLocation(r8)
            int r9 = r0.currentIndex
            r3.willSwitchFromPhoto(r4, r8, r9)
            long r3 = android.os.SystemClock.elapsedRealtime()
            r0.lastPhotoSetTime = r3
            int r3 = r0.currentIndex
            r0.currentIndex = r1
            r16.setIsAboutToSwitchToIndex(r17, r18, r19)
            org.telegram.ui.PhotoViewer$EditState r4 = r0.editState
            r4.reset()
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            boolean r4 = r4.isEmpty()
            r8 = 0
            if (r4 != 0) goto L_0x00b8
            int r1 = r0.currentIndex
            if (r1 < 0) goto L_0x00b4
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            int r4 = r4.size()
            if (r1 < r4) goto L_0x0071
            goto L_0x00b4
        L_0x0071:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r4 = r0.currentIndex
            java.lang.Object r1 = r1.get(r4)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r18 == 0) goto L_0x008d
            org.telegram.messenger.MessageObject r4 = r0.currentMessageObject
            if (r4 == 0) goto L_0x008d
            int r4 = r4.getId()
            int r10 = r1.getId()
            if (r4 != r10) goto L_0x008d
            r4 = 1
            goto L_0x008e
        L_0x008d:
            r4 = 0
        L_0x008e:
            r0.currentMessageObject = r1
            boolean r10 = r1.isVideo()
            int r11 = r0.sharedMediaType
            if (r11 != r6) goto L_0x00b1
            boolean r1 = r1.canPreviewDocument()
            r0.canZoom = r1
            if (r1 == 0) goto L_0x00a9
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.showSubItem(r6)
            r0.setDoubleTapEnabled(r6)
            goto L_0x00b1
        L_0x00a9:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.hideSubItem(r6)
            r0.setDoubleTapEnabled(r5)
        L_0x00b1:
            r12 = r2
            goto L_0x0285
        L_0x00b4:
            r0.closePhoto(r5, r5)
            return
        L_0x00b8:
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r4 = r0.secureDocuments
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x00db
            if (r1 < 0) goto L_0x00d7
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r4 = r0.secureDocuments
            int r4 = r4.size()
            if (r1 < r4) goto L_0x00cb
            goto L_0x00d7
        L_0x00cb:
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r4 = r0.secureDocuments
            java.lang.Object r1 = r4.get(r1)
            org.telegram.messenger.SecureDocument r1 = (org.telegram.messenger.SecureDocument) r1
            r0.currentSecureDocument = r1
            goto L_0x0282
        L_0x00d7:
            r0.closePhoto(r5, r5)
            return
        L_0x00db:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r4 = r0.imagesArrLocations
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x0125
            if (r1 < 0) goto L_0x0121
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r4 = r0.imagesArrLocations
            int r4 = r4.size()
            if (r1 < r4) goto L_0x00ee
            goto L_0x0121
        L_0x00ee:
            org.telegram.messenger.ImageLocation r4 = r0.currentFileLocation
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r10 = r0.imagesArrLocations
            java.lang.Object r10 = r10.get(r1)
            org.telegram.messenger.ImageLocation r10 = (org.telegram.messenger.ImageLocation) r10
            if (r18 == 0) goto L_0x0112
            if (r4 == 0) goto L_0x0112
            if (r10 == 0) goto L_0x0112
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r4 = r4.location
            int r11 = r4.local_id
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r10 = r10.location
            int r12 = r10.local_id
            if (r11 != r12) goto L_0x0112
            long r11 = r4.volume_id
            long r13 = r10.volume_id
            int r4 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r4 != 0) goto L_0x0112
            r4 = 1
            goto L_0x0113
        L_0x0112:
            r4 = 0
        L_0x0113:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r10 = r0.imagesArrLocations
            java.lang.Object r1 = r10.get(r1)
            org.telegram.messenger.ImageLocation r1 = (org.telegram.messenger.ImageLocation) r1
            r0.currentFileLocation = r1
            r12 = r2
            r10 = 0
            goto L_0x0285
        L_0x0121:
            r0.closePhoto(r5, r5)
            return
        L_0x0125:
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x0282
            if (r1 < 0) goto L_0x027e
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            int r4 = r4.size()
            if (r1 < r4) goto L_0x0139
            goto L_0x027e
        L_0x0139:
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            java.lang.Object r1 = r4.get(r1)
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$BotInlineResult
            if (r4 == 0) goto L_0x018d
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = (org.telegram.tgnet.TLRPC$BotInlineResult) r1
            r0.currentBotInlineResult = r1
            org.telegram.tgnet.TLRPC$Document r4 = r1.document
            if (r4 == 0) goto L_0x015d
            java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r4)
            java.lang.String r4 = r4.getAbsolutePath()
            r0.currentPathObject = r4
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isVideoDocument(r1)
        L_0x015b:
            r10 = r1
            goto L_0x018a
        L_0x015d:
            org.telegram.tgnet.TLRPC$Photo r4 = r1.photo
            if (r4 == 0) goto L_0x0176
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.sizes
            int r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r4)
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1)
            java.lang.String r1 = r1.getAbsolutePath()
            r0.currentPathObject = r1
            goto L_0x0189
        L_0x0176:
            org.telegram.tgnet.TLRPC$WebDocument r4 = r1.content
            boolean r10 = r4 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r10 == 0) goto L_0x0189
            java.lang.String r4 = r4.url
            r0.currentPathObject = r4
            java.lang.String r1 = r1.type
            java.lang.String r4 = "video"
            boolean r1 = r1.equals(r4)
            goto L_0x015b
        L_0x0189:
            r10 = 0
        L_0x018a:
            r12 = r2
            goto L_0x027c
        L_0x018d:
            boolean r4 = r1 instanceof org.telegram.messenger.MediaController.PhotoEntry
            if (r4 == 0) goto L_0x0237
            r4 = r1
            org.telegram.messenger.MediaController$PhotoEntry r4 = (org.telegram.messenger.MediaController.PhotoEntry) r4
            java.lang.String r10 = r4.path
            r0.currentPathObject = r10
            if (r10 != 0) goto L_0x019e
            r0.closePhoto(r5, r5)
            return
        L_0x019e:
            boolean r10 = r4.isVideo
            org.telegram.ui.PhotoViewer$EditState r11 = r0.editState
            org.telegram.messenger.MediaController$SavedFilterState r12 = r4.savedFilterState
            r11.savedFilterState = r12
            java.lang.String r12 = r4.paintPath
            r11.paintPath = r12
            long r12 = r4.averageDuration
            r11.averageDuration = r12
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r12 = r4.mediaEntities
            r11.mediaEntities = r12
            org.telegram.messenger.MediaController$CropState r12 = r4.cropState
            r11.cropState = r12
            java.io.File r11 = new java.io.File
            java.lang.String r12 = r4.path
            r11.<init>(r12)
            android.net.Uri r12 = android.net.Uri.fromFile(r11)
            boolean r13 = r0.isDocumentsPicker
            if (r13 == 0) goto L_0x0264
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            int r14 = r4.width
            java.lang.String r15 = ", "
            if (r14 == 0) goto L_0x01fa
            int r14 = r4.height
            if (r14 == 0) goto L_0x01fa
            int r14 = r13.length()
            if (r14 <= 0) goto L_0x01dd
            r13.append(r15)
        L_0x01dd:
            java.util.Locale r14 = java.util.Locale.US
            java.lang.Object[] r2 = new java.lang.Object[r7]
            int r7 = r4.width
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r2[r5] = r7
            int r7 = r4.height
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r2[r6] = r7
            java.lang.String r7 = "%dx%d"
            java.lang.String r2 = java.lang.String.format(r14, r7, r2)
            r13.append(r2)
        L_0x01fa:
            boolean r2 = r4.isVideo
            if (r2 == 0) goto L_0x0210
            int r2 = r13.length()
            if (r2 <= 0) goto L_0x0207
            r13.append(r15)
        L_0x0207:
            int r2 = r4.duration
            java.lang.String r2 = org.telegram.messenger.AndroidUtilities.formatShortDuration(r2)
            r13.append(r2)
        L_0x0210:
            long r6 = r4.size
            int r14 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r14 == 0) goto L_0x0228
            int r6 = r13.length()
            if (r6 <= 0) goto L_0x021f
            r13.append(r15)
        L_0x021f:
            long r6 = r4.size
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.formatFileSize(r6)
            r13.append(r4)
        L_0x0228:
            android.widget.TextView r4 = r0.docNameTextView
            java.lang.String r6 = r11.getName()
            r4.setText(r6)
            android.widget.TextView r4 = r0.docInfoTextView
            r4.setText(r13)
            goto L_0x0264
        L_0x0237:
            boolean r4 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r4 == 0) goto L_0x0262
            r4 = r1
            org.telegram.messenger.MediaController$SearchImage r4 = (org.telegram.messenger.MediaController.SearchImage) r4
            java.lang.String r6 = r4.getPathToAttach()
            r0.currentPathObject = r6
            org.telegram.ui.PhotoViewer$EditState r6 = r0.editState
            org.telegram.messenger.MediaController$SavedFilterState r7 = r4.savedFilterState
            r6.savedFilterState = r7
            java.lang.String r7 = r4.paintPath
            r6.paintPath = r7
            java.lang.String r7 = r4.croppedPaintPath
            r6.croppedPaintPath = r7
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r7 = r4.croppedMediaEntities
            r6.croppedMediaEntities = r7
            long r10 = r4.averageDuration
            r6.averageDuration = r10
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r7 = r4.mediaEntities
            r6.mediaEntities = r7
            org.telegram.messenger.MediaController$CropState r4 = r4.cropState
            r6.cropState = r4
        L_0x0262:
            r10 = 0
            r12 = 0
        L_0x0264:
            boolean r4 = r1 instanceof org.telegram.messenger.MediaController.MediaEditState
            if (r4 == 0) goto L_0x027c
            org.telegram.messenger.MediaController$MediaEditState r1 = (org.telegram.messenger.MediaController.MediaEditState) r1
            java.lang.String r4 = r1.filterPath
            if (r4 == 0) goto L_0x0271
            r0.currentImagePath = r4
            goto L_0x027c
        L_0x0271:
            java.lang.String r1 = r1.croppedPath
            if (r1 == 0) goto L_0x0278
            r0.currentImagePath = r1
            goto L_0x027c
        L_0x0278:
            java.lang.String r1 = r0.currentPathObject
            r0.currentImagePath = r1
        L_0x027c:
            r4 = 0
            goto L_0x0285
        L_0x027e:
            r0.closePhoto(r5, r5)
            return
        L_0x0282:
            r4 = 0
            r10 = 0
            r12 = 0
        L_0x0285:
            org.telegram.ui.PhotoViewer$PlaceProviderObject r1 = r0.currentPlaceObject
            if (r1 == 0) goto L_0x0296
            int r6 = r0.animationInProgress
            if (r6 != 0) goto L_0x0294
            org.telegram.messenger.ImageReceiver r1 = r1.imageReceiver
            r2 = 1
            r1.setVisible(r2, r2)
            goto L_0x0296
        L_0x0294:
            r0.showAfterAnimation = r1
        L_0x0296:
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r1 = r0.placeProvider
            org.telegram.messenger.MessageObject r6 = r0.currentMessageObject
            org.telegram.messenger.ImageLocation r7 = r0.currentFileLocation
            org.telegram.tgnet.TLRPC$FileLocation r7 = r0.getFileLocation(r7)
            int r11 = r0.currentIndex
            org.telegram.ui.PhotoViewer$PlaceProviderObject r1 = r1.getPlaceForPhoto(r6, r7, r11, r5)
            r0.currentPlaceObject = r1
            if (r1 == 0) goto L_0x02b7
            int r6 = r0.animationInProgress
            if (r6 != 0) goto L_0x02b5
            org.telegram.messenger.ImageReceiver r1 = r1.imageReceiver
            r2 = 1
            r1.setVisible(r5, r2)
            goto L_0x02b7
        L_0x02b5:
            r0.hideAfterAnimation = r1
        L_0x02b7:
            if (r4 != 0) goto L_0x0324
            r0.draggingDown = r5
            r1 = 0
            r0.translationX = r1
            r0.translationY = r1
            r4 = 1065353216(0x3var_, float:1.0)
            r0.scale = r4
            r0.animateToX = r1
            r0.animateToY = r1
            r0.animateToScale = r4
            r0.animationStartTime = r8
            r6 = 0
            r0.imageMoveAnimation = r6
            r0.changeModeAnimation = r6
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r6 = r0.aspectRatioFrameLayout
            if (r6 == 0) goto L_0x02d9
            r7 = 4
            r6.setVisibility(r7)
        L_0x02d9:
            r0.pinchStartDistance = r1
            r0.pinchStartScale = r4
            r0.pinchCenterX = r1
            r0.pinchCenterY = r1
            r0.pinchStartX = r1
            r0.pinchStartY = r1
            r0.moveStartX = r1
            r0.moveStartY = r1
            r0.zooming = r5
            r0.moving = r5
            r0.paintViewTouched = r5
            r0.doubleTap = r5
            r0.invalidCoords = r5
            r1 = 1
            r0.canDragDown = r1
            r0.changingPage = r5
            r0.switchImageAfterAnimation = r5
            int r2 = r0.sharedMediaType
            if (r2 == r1) goto L_0x031c
            java.util.ArrayList<java.lang.Object> r1 = r0.imagesArrLocals
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0319
            java.lang.String[] r1 = r0.currentFileNames
            r1 = r1[r5]
            if (r1 == 0) goto L_0x0317
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r1 = r0.photoProgressViews
            r1 = r1[r5]
            int r1 = r1.backgroundState
            if (r1 == 0) goto L_0x0317
            goto L_0x0319
        L_0x0317:
            r1 = 0
            goto L_0x031a
        L_0x0319:
            r1 = 1
        L_0x031a:
            r0.canZoom = r1
        L_0x031c:
            float r1 = r0.scale
            r0.updateMinMax(r1)
            r0.releasePlayer(r5)
        L_0x0324:
            if (r10 == 0) goto L_0x0331
            if (r12 == 0) goto L_0x0331
            r0.isStreaming = r5
            org.telegram.ui.PhotoViewer$EditState r1 = r0.editState
            org.telegram.messenger.MediaController$SavedFilterState r1 = r1.savedFilterState
            r0.preparePlayer(r12, r5, r5, r1)
        L_0x0331:
            java.util.ArrayList<java.lang.Object> r1 = r0.imagesArrLocals
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x034c
            org.telegram.ui.Components.PaintingOverlay r1 = r0.paintingOverlay
            r1.setVisibility(r5)
            org.telegram.ui.Components.PaintingOverlay r1 = r0.paintingOverlay
            org.telegram.ui.PhotoViewer$EditState r4 = r0.editState
            java.lang.String r6 = r4.paintPath
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r4 = r4.mediaEntities
            boolean r7 = r0.isCurrentVideo
            r1.setData(r6, r4, r7, r5)
            goto L_0x0358
        L_0x034c:
            org.telegram.ui.Components.PaintingOverlay r1 = r0.paintingOverlay
            r4 = 8
            r1.setVisibility(r4)
            org.telegram.ui.PhotoViewer$EditState r1 = r0.editState
            r1.reset()
        L_0x0358:
            r1 = -1
            if (r3 != r1) goto L_0x0368
            r16.setImages()
            r1 = 0
        L_0x035f:
            r2 = 3
            if (r1 >= r2) goto L_0x03bd
            r0.checkProgress(r1, r5, r5)
            int r1 = r1 + 1
            goto L_0x035f
        L_0x0368:
            r1 = 1
            r0.checkProgress(r5, r1, r5)
            int r1 = r0.currentIndex
            if (r3 <= r1) goto L_0x0396
            org.telegram.messenger.ImageReceiver r3 = r0.rightImage
            org.telegram.messenger.ImageReceiver r4 = r0.centerImage
            r0.rightImage = r4
            org.telegram.messenger.ImageReceiver r4 = r0.leftImage
            r0.centerImage = r4
            r0.leftImage = r3
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r4 = r0.photoProgressViews
            r6 = r4[r5]
            r7 = 2
            r8 = r4[r7]
            r4[r5] = r8
            r4[r7] = r6
            r2 = 1
            int r1 = r1 - r2
            r0.setIndexToImage(r3, r1)
            r16.updateAccessibilityOverlayVisibility()
            r0.checkProgress(r2, r2, r5)
            r0.checkProgress(r7, r2, r5)
            goto L_0x03bd
        L_0x0396:
            r2 = 1
            if (r3 >= r1) goto L_0x03bd
            org.telegram.messenger.ImageReceiver r3 = r0.leftImage
            org.telegram.messenger.ImageReceiver r4 = r0.centerImage
            r0.leftImage = r4
            org.telegram.messenger.ImageReceiver r4 = r0.rightImage
            r0.centerImage = r4
            r0.rightImage = r3
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r4 = r0.photoProgressViews
            r6 = r4[r5]
            r7 = r4[r2]
            r4[r5] = r7
            r4[r2] = r6
            int r1 = r1 + r2
            r0.setIndexToImage(r3, r1)
            r16.updateAccessibilityOverlayVisibility()
            r0.checkProgress(r2, r2, r5)
            r1 = 2
            r0.checkProgress(r1, r2, r5)
        L_0x03bd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.setImageIndex(int, boolean, boolean):void");
    }

    private void setCurrentCaption(MessageObject messageObject, CharSequence charSequence, boolean z) {
        boolean z2;
        CharSequence charSequence2;
        int i;
        int i2;
        MessageObject messageObject2 = messageObject;
        boolean z3 = z;
        boolean z4 = true;
        int i3 = 0;
        if (!this.needCaptionLayout) {
            if (this.captionScrollView == null) {
                this.captionScrollView = new CaptionScrollView(this.containerView.getContext());
                FrameLayout frameLayout = new FrameLayout(this.containerView.getContext());
                this.captionContainer = frameLayout;
                frameLayout.setClipChildren(false);
                this.captionScrollView.addView((View) this.captionContainer, new ViewGroup.LayoutParams(-1, -2));
                this.containerView.addView(this.captionScrollView, LayoutHelper.createFrame(-1, -1, 80));
            }
            if (this.captionTextViewSwitcher.getParent() != this.captionContainer) {
                this.pickerView.removeView(this.captionTextViewSwitcher);
                this.captionTextViewSwitcher.setMeasureAllChildren(true);
                this.captionContainer.addView(this.captionTextViewSwitcher, -1, -2);
                this.videoPreviewFrame.bringToFront();
            }
        } else if (this.captionTextViewSwitcher.getParent() != this.pickerView) {
            FrameLayout frameLayout2 = this.captionContainer;
            if (frameLayout2 != null) {
                frameLayout2.removeView(this.captionTextViewSwitcher);
            }
            this.captionTextViewSwitcher.setMeasureAllChildren(false);
            this.pickerView.addView(this.captionTextViewSwitcher, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 76.0f, 48.0f));
        }
        final boolean isEmpty = TextUtils.isEmpty(charSequence);
        final boolean isEmpty2 = TextUtils.isEmpty(this.captionTextViewSwitcher.getCurrentView().getText());
        CaptionTextViewSwitcher captionTextViewSwitcher2 = this.captionTextViewSwitcher;
        TextView nextView = z3 ? captionTextViewSwitcher2.getNextView() : captionTextViewSwitcher2.getCurrentView();
        if (!this.isCurrentVideo) {
            int maxLines = nextView.getMaxLines();
            if (maxLines == 1) {
                this.captionTextViewSwitcher.getCurrentView().setSingleLine(false);
                this.captionTextViewSwitcher.getNextView().setSingleLine(false);
            }
            if (this.needCaptionLayout) {
                Point point = AndroidUtilities.displaySize;
                i2 = point.x > point.y ? 5 : 10;
            } else {
                i2 = Integer.MAX_VALUE;
            }
            if (maxLines != i2) {
                this.captionTextViewSwitcher.getCurrentView().setMaxLines(i2);
                this.captionTextViewSwitcher.getNextView().setMaxLines(i2);
                this.captionTextViewSwitcher.getCurrentView().setEllipsize((TextUtils.TruncateAt) null);
                this.captionTextViewSwitcher.getNextView().setEllipsize((TextUtils.TruncateAt) null);
            }
        } else if (nextView.getMaxLines() != 1) {
            this.captionTextViewSwitcher.getCurrentView().setMaxLines(1);
            this.captionTextViewSwitcher.getNextView().setMaxLines(1);
            this.captionTextViewSwitcher.getCurrentView().setSingleLine(true);
            this.captionTextViewSwitcher.getNextView().setSingleLine(true);
            this.captionTextViewSwitcher.getCurrentView().setEllipsize(TextUtils.TruncateAt.END);
            this.captionTextViewSwitcher.getNextView().setEllipsize(TextUtils.TruncateAt.END);
        }
        nextView.setScrollX(0);
        this.dontChangeCaptionPosition = !this.needCaptionLayout && z3 && isEmpty;
        if (!this.needCaptionLayout) {
            boolean unused = this.captionScrollView.dontChangeTopMargin = false;
        }
        if (!z3 || (i = Build.VERSION.SDK_INT) < 19) {
            this.captionTextViewSwitcher.getCurrentView().setText((CharSequence) null);
            CaptionScrollView captionScrollView2 = this.captionScrollView;
            if (captionScrollView2 != null) {
                captionScrollView2.scrollTo(0, 0);
            }
            z2 = false;
        } else {
            if (i >= 23) {
                TransitionManager.endTransitions(this.needCaptionLayout ? this.pickerView : this.captionScrollView);
            }
            if (this.needCaptionLayout) {
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.setOrdering(0);
                transitionSet.addTransition(new ChangeBounds());
                transitionSet.addTransition(new Fade(2));
                transitionSet.addTransition(new Fade(1));
                transitionSet.setDuration(200);
                TransitionManager.beginDelayedTransition(this.pickerView, transitionSet);
            } else {
                TransitionSet duration = new TransitionSet().addTransition(new Fade(2) {
                    public Animator onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
                        Animator onDisappear = super.onDisappear(viewGroup, view, transitionValues, transitionValues2);
                        if (!isEmpty2 && isEmpty && view == PhotoViewer.this.captionTextViewSwitcher) {
                            onDisappear.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    PhotoViewer.this.captionScrollView.setVisibility(4);
                                    float unused = PhotoViewer.this.captionScrollView.backgroundAlpha = 1.0f;
                                }
                            });
                            ((ObjectAnimator) onDisappear).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    PhotoViewer.AnonymousClass51.this.lambda$onDisappear$0$PhotoViewer$51(valueAnimator);
                                }
                            });
                        }
                        return onDisappear;
                    }

                    public /* synthetic */ void lambda$onDisappear$0$PhotoViewer$51(ValueAnimator valueAnimator) {
                        float unused = PhotoViewer.this.captionScrollView.backgroundAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                        PhotoViewer.this.captionScrollView.invalidate();
                    }
                }).addTransition(new Fade(1) {
                    public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
                        Animator onAppear = super.onAppear(viewGroup, view, transitionValues, transitionValues2);
                        if (isEmpty2 && !isEmpty && view == PhotoViewer.this.captionTextViewSwitcher) {
                            onAppear.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    float unused = PhotoViewer.this.captionScrollView.backgroundAlpha = 1.0f;
                                }
                            });
                            ((ObjectAnimator) onAppear).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    PhotoViewer.AnonymousClass50.this.lambda$onAppear$0$PhotoViewer$50(valueAnimator);
                                }
                            });
                        }
                        return onAppear;
                    }

                    public /* synthetic */ void lambda$onAppear$0$PhotoViewer$50(ValueAnimator valueAnimator) {
                        float unused = PhotoViewer.this.captionScrollView.backgroundAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                        PhotoViewer.this.captionScrollView.invalidate();
                    }
                }).setDuration(200);
                if (!isEmpty2) {
                    boolean unused2 = this.captionScrollView.dontChangeTopMargin = true;
                    duration.addTransition(new Transition() {
                        public void captureStartValues(TransitionValues transitionValues) {
                            if (transitionValues.view == PhotoViewer.this.captionScrollView) {
                                transitionValues.values.put("scrollY", Integer.valueOf(PhotoViewer.this.captionScrollView.getScrollY()));
                            }
                        }

                        public void captureEndValues(TransitionValues transitionValues) {
                            if (transitionValues.view == PhotoViewer.this.captionTextViewSwitcher) {
                                transitionValues.values.put("translationY", Integer.valueOf(PhotoViewer.this.captionScrollView.getPendingMarginTopDiff()));
                            }
                        }

                        public Animator createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
                            int intValue;
                            if (transitionValues.view == PhotoViewer.this.captionScrollView) {
                                ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{((Integer) transitionValues.values.get("scrollY")).intValue(), 0});
                                ofInt.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        PhotoViewer.this.captionTextViewSwitcher.getNextView().setText((CharSequence) null);
                                        PhotoViewer.this.captionScrollView.applyPendingTopMargin();
                                    }

                                    public void onAnimationStart(Animator animator) {
                                        PhotoViewer.this.captionScrollView.stopScrolling();
                                    }
                                });
                                ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        PhotoViewer.AnonymousClass52.this.lambda$createAnimator$0$PhotoViewer$52(valueAnimator);
                                    }
                                });
                                return ofInt;
                            } else if (transitionValues2.view != PhotoViewer.this.captionTextViewSwitcher || (intValue = ((Integer) transitionValues2.values.get("translationY")).intValue()) == 0) {
                                return null;
                            } else {
                                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(PhotoViewer.this.captionTextViewSwitcher, View.TRANSLATION_Y, new float[]{0.0f, (float) intValue});
                                ofFloat.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        PhotoViewer.this.captionTextViewSwitcher.setTranslationY(0.0f);
                                    }
                                });
                                return ofFloat;
                            }
                        }

                        public /* synthetic */ void lambda$createAnimator$0$PhotoViewer$52(ValueAnimator valueAnimator) {
                            PhotoViewer.this.captionScrollView.scrollTo(0, ((Integer) valueAnimator.getAnimatedValue()).intValue());
                        }
                    });
                }
                if (isEmpty2 && !isEmpty) {
                    duration.addTarget(this.captionTextViewSwitcher);
                }
                TransitionManager.beginDelayedTransition(this.captionScrollView, duration);
            }
            z2 = true;
        }
        if (!isEmpty) {
            Theme.createChatResources((Context) null, true);
            if (messageObject2 == null || messageObject2.messageOwner.entities.isEmpty()) {
                charSequence2 = Emoji.replaceEmoji(new SpannableStringBuilder(charSequence), nextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            } else {
                SpannableString valueOf = SpannableString.valueOf(charSequence.toString());
                messageObject2.addEntitiesToText(valueOf, true, false);
                if (messageObject.isVideo()) {
                    MessageObject.addUrlsByPattern(messageObject.isOutOwner(), valueOf, false, 3, messageObject.getDuration(), false);
                }
                charSequence2 = Emoji.replaceEmoji(valueOf, nextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.captionTextViewSwitcher.setTag(charSequence2);
            try {
                this.captionTextViewSwitcher.setText(charSequence2, z3);
                this.captionScrollView.updateTopMargin();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            nextView.setScrollY(0);
            nextView.setTextColor(-1);
            if (!this.isActionBarVisible || !(this.bottomLayout.getVisibility() == 0 || this.pickerView.getVisibility() == 0)) {
                z4 = false;
            }
            CaptionTextViewSwitcher captionTextViewSwitcher3 = this.captionTextViewSwitcher;
            if (!z4) {
                i3 = 4;
            }
            captionTextViewSwitcher3.setVisibility(i3);
        } else if (this.needCaptionLayout) {
            this.captionTextViewSwitcher.setText(LocaleController.getString("AddCaption", NUM), z3);
            this.captionTextViewSwitcher.getCurrentView().setTextColor(-NUM);
            this.captionTextViewSwitcher.setTag("empty");
            this.captionTextViewSwitcher.setVisibility(0);
        } else {
            this.captionTextViewSwitcher.setText((CharSequence) null, z3);
            this.captionTextViewSwitcher.getCurrentView().setTextColor(-1);
            CaptionTextViewSwitcher captionTextViewSwitcher4 = this.captionTextViewSwitcher;
            if (z2 && !isEmpty2) {
                z4 = false;
            }
            captionTextViewSwitcher4.setVisibility(4, z4);
            this.captionTextViewSwitcher.setTag((Object) null);
        }
    }

    /* access modifiers changed from: private */
    public void setCaptionHwLayerEnabled(boolean z) {
        if (this.captionHwLayerEnabled != z) {
            this.captionHwLayerEnabled = z;
            this.captionTextViewSwitcher.setLayerType(2, (Paint) null);
            this.captionTextViewSwitcher.getCurrentView().setLayerType(2, (Paint) null);
            this.captionTextViewSwitcher.getNextView().setLayerType(2, (Paint) null);
        }
    }

    private void checkProgress(int i, boolean z, boolean z2) {
        boolean z3;
        boolean z4;
        MessageObject messageObject;
        File file;
        File file2;
        File file3;
        MessageObject messageObject2;
        File file4;
        File file5;
        File file6;
        TLRPC$WebPage tLRPC$WebPage;
        int i2 = this.currentIndex;
        boolean z5 = true;
        if (i == 1) {
            i2++;
        } else if (i == 2) {
            i2--;
        }
        boolean z6 = false;
        if (this.currentFileNames[i] != null) {
            if (this.currentMessageObject != null) {
                if (i2 < 0 || i2 >= this.imagesArr.size()) {
                    this.photoProgressViews[i].setBackgroundState(-1, z2);
                    return;
                }
                MessageObject messageObject3 = this.imagesArr.get(i2);
                if (this.sharedMediaType != 1 || messageObject3.canPreviewDocument()) {
                    File file7 = !TextUtils.isEmpty(messageObject3.messageOwner.attachPath) ? new File(messageObject3.messageOwner.attachPath) : null;
                    TLRPC$MessageMedia tLRPC$MessageMedia = messageObject3.messageOwner.media;
                    if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) || (tLRPC$WebPage = tLRPC$MessageMedia.webpage) == null || tLRPC$WebPage.document != null) {
                        file6 = FileLoader.getPathToMessage(messageObject3.messageOwner);
                    } else {
                        file6 = FileLoader.getPathToAttach(getFileLocation(i2, (int[]) null), true);
                    }
                    if (messageObject3.isVideo()) {
                        if (SharedConfig.streamMedia && messageObject3.canStreamVideo() && ((int) messageObject3.getDialogId()) != 0) {
                            z6 = true;
                        }
                        z4 = z6;
                        z3 = true;
                        messageObject = messageObject3;
                        file2 = file7;
                    } else {
                        messageObject = messageObject3;
                        file2 = file7;
                        z4 = false;
                        z3 = false;
                    }
                    file = file6;
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
                    TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = this.imagesArrLocations.get(i2).location;
                    if (this.avatarsDialogId == 0 && !this.isEvent) {
                        z5 = false;
                    }
                    file4 = FileLoader.getPathToAttach(tLRPC$TL_fileLocationToBeDeprecated, z5);
                } else if (this.currentSecureDocument != null) {
                    if (i2 < 0 || i2 >= this.secureDocuments.size()) {
                        this.photoProgressViews[i].setBackgroundState(-1, z2);
                        return;
                    }
                    file4 = FileLoader.getPathToAttach(this.secureDocuments.get(i2), true);
                } else if (this.currentPathObject != null) {
                    file3 = new File(FileLoader.getDirectory(3), this.currentFileNames[i]);
                    file = new File(FileLoader.getDirectory(4), this.currentFileNames[i]);
                    messageObject2 = null;
                    z4 = false;
                    z3 = false;
                } else {
                    file3 = null;
                    file = null;
                    messageObject2 = file;
                    z4 = false;
                    z3 = false;
                }
                file3 = file4;
                file = null;
                messageObject2 = file;
                z4 = false;
                z3 = false;
            } else if (i2 < 0 || i2 >= this.imagesArrLocals.size()) {
                this.photoProgressViews[i].setBackgroundState(-1, z2);
                return;
            } else {
                TLRPC$BotInlineResult tLRPC$BotInlineResult = (TLRPC$BotInlineResult) this.imagesArrLocals.get(i2);
                if (tLRPC$BotInlineResult.type.equals("video") || MessageObject.isVideoDocument(tLRPC$BotInlineResult.document)) {
                    TLRPC$Document tLRPC$Document = tLRPC$BotInlineResult.document;
                    if (tLRPC$Document != null) {
                        file5 = FileLoader.getPathToAttach(tLRPC$Document);
                    } else if (tLRPC$BotInlineResult.content instanceof TLRPC$TL_webDocument) {
                        file5 = new File(FileLoader.getDirectory(4), Utilities.MD5(tLRPC$BotInlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(tLRPC$BotInlineResult.content.url, "mp4"));
                    } else {
                        file5 = null;
                    }
                } else {
                    if (tLRPC$BotInlineResult.document != null) {
                        file5 = new File(FileLoader.getDirectory(3), this.currentFileNames[i]);
                    } else {
                        file5 = tLRPC$BotInlineResult.photo != null ? new File(FileLoader.getDirectory(0), this.currentFileNames[i]) : null;
                    }
                    z5 = false;
                }
                file = new File(FileLoader.getDirectory(4), this.currentFileNames[i]);
                messageObject = null;
                z4 = false;
                file2 = file5;
                z3 = z5;
            }
            Utilities.globalQueue.postRunnable(new Runnable(file2, file, i, messageObject, z4, z3, z2) {
                public final /* synthetic */ File f$1;
                public final /* synthetic */ File f$2;
                public final /* synthetic */ int f$3;
                public final /* synthetic */ MessageObject f$4;
                public final /* synthetic */ boolean f$5;
                public final /* synthetic */ boolean f$6;
                public final /* synthetic */ boolean f$7;

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
                    PhotoViewer.this.lambda$checkProgress$52$PhotoViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
            return;
        }
        if (!this.imagesArrLocals.isEmpty() && i2 >= 0 && i2 < this.imagesArrLocals.size()) {
            Object obj = this.imagesArrLocals.get(i2);
            if (obj instanceof MediaController.PhotoEntry) {
                z6 = ((MediaController.PhotoEntry) obj).isVideo;
            }
        }
        if (z6) {
            this.photoProgressViews[i].setBackgroundState(3, z2);
        } else {
            this.photoProgressViews[i].setBackgroundState(-1, z2);
        }
    }

    public /* synthetic */ void lambda$checkProgress$52$PhotoViewer(File file, File file2, int i, MessageObject messageObject, boolean z, boolean z2, boolean z3) {
        ChatActivity chatActivity;
        TLRPC$Document document;
        MessageObject messageObject2 = messageObject;
        boolean exists = file != null ? file.exists() : false;
        if (!exists && file2 != null) {
            exists = file2.exists();
        }
        boolean z4 = exists;
        if (!z4 && i != 0 && messageObject2 != null && z && DownloadController.getInstance(this.currentAccount).canDownloadMedia(messageObject2.messageOwner) != 0 && (((chatActivity = this.parentChatActivity) == null || chatActivity.getCurrentEncryptedChat() == null) && !messageObject.shouldEncryptPhotoOrVideo() && (document = messageObject.getDocument()) != null)) {
            FileLoader.getInstance(this.currentAccount).loadFile(document, messageObject2, 0, 10);
        }
        AndroidUtilities.runOnUIThread(new Runnable(file, file2, z4, z, i, z2, messageObject, z3) {
            public final /* synthetic */ File f$1;
            public final /* synthetic */ File f$2;
            public final /* synthetic */ boolean f$3;
            public final /* synthetic */ boolean f$4;
            public final /* synthetic */ int f$5;
            public final /* synthetic */ boolean f$6;
            public final /* synthetic */ MessageObject f$7;
            public final /* synthetic */ boolean f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                PhotoViewer.this.lambda$null$51$PhotoViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$null$51$PhotoViewer(File file, File file2, boolean z, boolean z2, int i, boolean z3, MessageObject messageObject, boolean z4) {
        boolean z5 = true;
        if (!(file == null && file2 == null) && (z || z2)) {
            if (i != 0 || !this.isPlaying) {
                if (!z3 || (shouldMessageObjectAutoPlayed(messageObject) && (i != 0 || !this.playerWasPlaying))) {
                    this.photoProgressViews[i].setBackgroundState(-1, z4);
                } else {
                    this.photoProgressViews[i].setBackgroundState(3, z4);
                }
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v0, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v0, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v47, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v55, resolved type: org.telegram.messenger.ImageLocation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v1, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v1, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v2, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v2, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v3, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v4, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v4, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v17, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v9, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v14, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v10, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v69, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v16, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v18, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v5, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v70, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v12, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v6, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v19, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v11, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v17, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v72, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v16, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v12, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v20, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v7, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v18, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v21, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v13, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v8, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v20, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v24, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v9, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v15, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v10, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v25, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v16, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v26, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v30, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v17, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v26, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v11, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v27, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v18, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v12, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v49, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v54, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v98, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v100, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v55, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX WARNING: type inference failed for: r12v5, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r12v19 */
    /* JADX WARNING: type inference failed for: r12v21 */
    /* JADX WARNING: type inference failed for: r12v23, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r13v38, types: [org.telegram.messenger.ImageLocation] */
    /* JADX WARNING: type inference failed for: r14v28, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r14v32 */
    /* JADX WARNING: type inference failed for: r14v33 */
    /* JADX WARNING: type inference failed for: r14v34, types: [org.telegram.messenger.ImageLocation] */
    /* JADX WARNING: type inference failed for: r2v84 */
    /* JADX WARNING: type inference failed for: r2v87, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r2v89 */
    /* JADX WARNING: type inference failed for: r6v36, types: [android.graphics.drawable.Drawable] */
    /* JADX WARNING: type inference failed for: r5v51, types: [android.graphics.drawable.BitmapDrawable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0513  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0540  */
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
            if (r2 < 0) goto L_0x0582
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r3 = r0.secureDocuments
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0582
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
            goto L_0x0582
        L_0x0066:
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            boolean r4 = r4.isEmpty()
            r6 = 2
            java.lang.String r7 = "%d_%d"
            r8 = 1
            if (r4 != 0) goto L_0x0373
            if (r2 < 0) goto L_0x036d
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            int r4 = r4.size()
            if (r2 >= r4) goto L_0x036d
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            java.lang.Object r11 = r4.get(r2)
            int r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r4 = (float) r4
            float r9 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r9
            int r4 = (int) r4
            org.telegram.messenger.ImageReceiver$BitmapHolder r9 = r0.currentThumb
            if (r9 == 0) goto L_0x0094
            org.telegram.messenger.ImageReceiver r10 = r0.centerImage
            if (r1 != r10) goto L_0x0094
            goto L_0x0095
        L_0x0094:
            r9 = r5
        L_0x0095:
            if (r9 != 0) goto L_0x009d
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r9 = r0.placeProvider
            org.telegram.messenger.ImageReceiver$BitmapHolder r9 = r9.getThumbForPhoto(r5, r5, r2)
        L_0x009d:
            boolean r2 = r11 instanceof org.telegram.messenger.MediaController.PhotoEntry
            r10 = 90
            java.lang.String r12 = "d"
            if (r2 == 0) goto L_0x0103
            r2 = r11
            org.telegram.messenger.MediaController$PhotoEntry r2 = (org.telegram.messenger.MediaController.PhotoEntry) r2
            boolean r12 = r2.isVideo
            if (r12 == 0) goto L_0x00d0
            java.lang.String r13 = r2.thumbPath
            if (r13 == 0) goto L_0x00b1
            goto L_0x00ce
        L_0x00b1:
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
        L_0x00ce:
            r2 = r5
            goto L_0x00f6
        L_0x00d0:
            java.lang.String r13 = r2.filterPath
            if (r13 == 0) goto L_0x00d5
            goto L_0x00e2
        L_0x00d5:
            java.lang.String r13 = r2.croppedPath
            if (r13 == 0) goto L_0x00da
            goto L_0x00e2
        L_0x00da:
            int r13 = r2.orientation
            r1.setOrientation(r13, r3)
            java.lang.String r2 = r2.path
            r13 = r2
        L_0x00e2:
            java.util.Locale r2 = java.util.Locale.US
            java.lang.Object[] r14 = new java.lang.Object[r6]
            java.lang.Integer r15 = java.lang.Integer.valueOf(r4)
            r14[r3] = r15
            java.lang.Integer r15 = java.lang.Integer.valueOf(r4)
            r14[r8] = r15
            java.lang.String r2 = java.lang.String.format(r2, r7, r14)
        L_0x00f6:
            r15 = r2
            r2 = r5
            r8 = r2
            r17 = r8
            r19 = r17
            r18 = r12
            r12 = 0
            r14 = 0
            goto L_0x0252
        L_0x0103:
            boolean r2 = r11 instanceof org.telegram.tgnet.TLRPC$BotInlineResult
            if (r2 == 0) goto L_0x01f8
            r2 = r11
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = (org.telegram.tgnet.TLRPC$BotInlineResult) r2
            java.lang.String r13 = r2.type
            java.lang.String r14 = "video"
            boolean r13 = r13.equals(r14)
            if (r13 != 0) goto L_0x01c0
            org.telegram.tgnet.TLRPC$Document r13 = r2.document
            boolean r13 = org.telegram.messenger.MessageObject.isVideoDocument(r13)
            if (r13 == 0) goto L_0x011e
            goto L_0x01c0
        L_0x011e:
            java.lang.String r13 = r2.type
            java.lang.String r14 = "gif"
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x0145
            org.telegram.tgnet.TLRPC$Document r13 = r2.document
            if (r13 == 0) goto L_0x0145
            int r2 = r13.size
            org.telegram.tgnet.TLRPC$TL_videoSize r14 = org.telegram.messenger.MessageObject.getDocumentVideoThumb(r13)
            if (r14 == 0) goto L_0x0139
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$TL_videoSize) r14, (org.telegram.tgnet.TLRPC$Document) r13)
            goto L_0x013a
        L_0x0139:
            r14 = r5
        L_0x013a:
            r15 = r12
            r16 = r13
            r17 = r14
            r13 = r2
            r2 = r5
            r12 = r2
        L_0x0142:
            r14 = r12
            goto L_0x01ec
        L_0x0145:
            org.telegram.tgnet.TLRPC$Photo r13 = r2.photo
            if (r13 == 0) goto L_0x0172
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
            r16 = r5
            r17 = r16
            r15 = r14
            r14 = r12
            goto L_0x01d3
        L_0x0172:
            org.telegram.tgnet.TLRPC$WebDocument r13 = r2.content
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r13 == 0) goto L_0x01e3
            java.lang.String r13 = r2.type
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x019c
            org.telegram.tgnet.TLRPC$WebDocument r13 = r2.thumb
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r14 == 0) goto L_0x01b0
            java.lang.String r13 = r13.mime_type
            java.lang.String r14 = "video/mp4"
            boolean r13 = r14.equals(r13)
            if (r13 == 0) goto L_0x01b0
            org.telegram.tgnet.TLRPC$WebDocument r13 = r2.thumb
            org.telegram.messenger.WebFile r13 = org.telegram.messenger.WebFile.createWithWebDocument(r13)
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForWebFile(r13)
            r14 = r13
            goto L_0x01b1
        L_0x019c:
            java.util.Locale r12 = java.util.Locale.US
            java.lang.Object[] r13 = new java.lang.Object[r6]
            java.lang.Integer r14 = java.lang.Integer.valueOf(r4)
            r13[r3] = r14
            java.lang.Integer r14 = java.lang.Integer.valueOf(r4)
            r13[r8] = r14
            java.lang.String r12 = java.lang.String.format(r12, r7, r13)
        L_0x01b0:
            r14 = r5
        L_0x01b1:
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.content
            org.telegram.messenger.WebFile r2 = org.telegram.messenger.WebFile.createWithWebDocument(r2)
            r16 = r5
            r15 = r12
            r17 = r14
            r13 = 0
            r12 = r16
            goto L_0x0142
        L_0x01c0:
            org.telegram.tgnet.TLRPC$Document r12 = r2.document
            if (r12 == 0) goto L_0x01d7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r12 = r12.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r12 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r12, r10)
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            r15 = r5
            r16 = r15
            r17 = r16
            r14 = r12
            r13 = 0
        L_0x01d3:
            r12 = r2
            r2 = r17
            goto L_0x01ec
        L_0x01d7:
            org.telegram.tgnet.TLRPC$WebDocument r2 = r2.thumb
            boolean r12 = r2 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r12 == 0) goto L_0x01e3
            org.telegram.messenger.WebFile r2 = org.telegram.messenger.WebFile.createWithWebDocument(r2)
            r12 = r5
            goto L_0x01e5
        L_0x01e3:
            r2 = r5
            r12 = r2
        L_0x01e5:
            r14 = r12
            r15 = r14
            r16 = r15
            r17 = r16
            r13 = 0
        L_0x01ec:
            r19 = r14
            r8 = r16
            r14 = 1
            r18 = 0
            r20 = r13
            r13 = r5
            r5 = r12
            goto L_0x0243
        L_0x01f8:
            boolean r2 = r11 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r2 == 0) goto L_0x0246
            r2 = r11
            org.telegram.messenger.MediaController$SearchImage r2 = (org.telegram.messenger.MediaController.SearchImage) r2
            org.telegram.tgnet.TLRPC$PhotoSize r13 = r2.photoSize
            if (r13 == 0) goto L_0x020e
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            int r14 = r13.size
            r16 = r5
            r15 = r14
            r14 = r13
            r13 = r16
            goto L_0x0234
        L_0x020e:
            java.lang.String r13 = r2.filterPath
            if (r13 == 0) goto L_0x0218
        L_0x0212:
            r2 = r5
            r14 = r2
            r16 = r14
            r15 = 0
            goto L_0x0234
        L_0x0218:
            java.lang.String r13 = r2.croppedPath
            if (r13 == 0) goto L_0x021d
            goto L_0x0212
        L_0x021d:
            org.telegram.tgnet.TLRPC$Document r13 = r2.document
            if (r13 == 0) goto L_0x022a
            int r14 = r13.size
            r2 = r5
            r16 = r13
            r15 = r14
            r13 = r2
            r14 = r13
            goto L_0x0234
        L_0x022a:
            java.lang.String r13 = r2.imageUrl
            int r14 = r2.size
            r2 = r5
            r16 = r2
            r15 = r14
            r14 = r16
        L_0x0234:
            r17 = r5
            r19 = r14
            r8 = r16
            r14 = 1
            r18 = 0
            r5 = r2
            r2 = r17
            r20 = r15
            r15 = r12
        L_0x0243:
            r12 = r20
            goto L_0x0252
        L_0x0246:
            r2 = r5
            r8 = r2
            r13 = r8
            r15 = r13
            r17 = r15
            r19 = r17
            r12 = 0
            r14 = 0
            r18 = 0
        L_0x0252:
            if (r8 == 0) goto L_0x02e4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r8.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r10)
            if (r17 == 0) goto L_0x02a1
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForDocument(r8)
            r10 = 0
            if (r9 != 0) goto L_0x0269
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r8)
            r8 = r2
            goto L_0x026a
        L_0x0269:
            r8 = 0
        L_0x026a:
            java.util.Locale r2 = java.util.Locale.US
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.Integer r13 = java.lang.Integer.valueOf(r4)
            r6[r3] = r13
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            r4 = 1
            r6[r4] = r3
            java.lang.String r7 = java.lang.String.format(r2, r7, r6)
            if (r9 == 0) goto L_0x028b
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r3 = r9.bitmap
            r2.<init>(r3)
            r16 = r2
            goto L_0x028d
        L_0x028b:
            r16 = 0
        L_0x028d:
            r13 = 0
            java.lang.String r3 = "d"
            r1 = r22
            r2 = r5
            r4 = r17
            r5 = r10
            r6 = r8
            r8 = r16
            r9 = r12
            r10 = r13
            r12 = r14
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            goto L_0x0582
        L_0x02a1:
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForDocument(r8)
            if (r9 != 0) goto L_0x02ad
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Document) r8)
            r8 = r2
            goto L_0x02ae
        L_0x02ad:
            r8 = 0
        L_0x02ae:
            java.util.Locale r2 = java.util.Locale.US
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
            r6[r3] = r10
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            r4 = 1
            r6[r4] = r3
            java.lang.String r6 = java.lang.String.format(r2, r7, r6)
            if (r9 == 0) goto L_0x02cf
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r3 = r9.bitmap
            r2.<init>(r3)
            r16 = r2
            goto L_0x02d1
        L_0x02cf:
            r16 = 0
        L_0x02d1:
            r9 = 0
            java.lang.String r3 = "d"
            r1 = r22
            r2 = r5
            r4 = r8
            r5 = r6
            r6 = r16
            r7 = r12
            r8 = r9
            r9 = r11
            r10 = r14
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x0582
        L_0x02e4:
            r3 = r19
            if (r3 == 0) goto L_0x0304
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForObject(r3, r5)
            if (r9 == 0) goto L_0x02f7
            android.graphics.drawable.BitmapDrawable r3 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r4 = r9.bitmap
            r3.<init>(r4)
            r4 = r3
            goto L_0x02f8
        L_0x02f7:
            r4 = 0
        L_0x02f8:
            r6 = 0
            r1 = r22
            r3 = r15
            r5 = r12
            r7 = r11
            r8 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (android.graphics.drawable.Drawable) r4, (int) r5, (java.lang.String) r6, (java.lang.Object) r7, (int) r8)
            goto L_0x0582
        L_0x0304:
            r3 = 2131165756(0x7var_c, float:1.7945738E38)
            if (r2 == 0) goto L_0x0347
            if (r17 == 0) goto L_0x031d
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForWebFile(r2)
            r5 = 0
            r6 = 0
            r1 = r22
            r3 = r15
            r4 = r17
            r7 = r11
            r8 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (java.lang.String) r6, (java.lang.Object) r7, (int) r8)
            goto L_0x0582
        L_0x031d:
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForWebFile(r2)
            if (r9 == 0) goto L_0x032c
            android.graphics.drawable.BitmapDrawable r3 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r4 = r9.bitmap
            r3.<init>(r4)
        L_0x032a:
            r4 = r3
            goto L_0x033c
        L_0x032c:
            if (r18 == 0) goto L_0x033b
            android.app.Activity r4 = r0.parentActivity
            if (r4 == 0) goto L_0x033b
            android.content.res.Resources r4 = r4.getResources()
            android.graphics.drawable.Drawable r3 = r4.getDrawable(r3)
            goto L_0x032a
        L_0x033b:
            r4 = 0
        L_0x033c:
            r5 = 0
            r1 = r22
            r3 = r15
            r6 = r11
            r7 = r14
            r1.setImage(r2, r3, r4, r5, r6, r7)
            goto L_0x0582
        L_0x0347:
            if (r9 == 0) goto L_0x0352
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r3 = r9.bitmap
            r2.<init>(r3)
        L_0x0350:
            r4 = r2
            goto L_0x0362
        L_0x0352:
            if (r18 == 0) goto L_0x0361
            android.app.Activity r2 = r0.parentActivity
            if (r2 == 0) goto L_0x0361
            android.content.res.Resources r2 = r2.getResources()
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r3)
            goto L_0x0350
        L_0x0361:
            r4 = 0
        L_0x0362:
            r5 = 0
            r1 = r22
            r2 = r13
            r3 = r15
            r6 = r12
            r1.setImage(r2, r3, r4, r5, r6)
            goto L_0x0582
        L_0x036d:
            r2 = r5
            r1.setImageBitmap((android.graphics.Bitmap) r2)
            goto L_0x0582
        L_0x0373:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x0393
            if (r2 < 0) goto L_0x0393
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            int r4 = r4.size()
            if (r2 >= r4) goto L_0x0393
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            java.lang.Object r4 = r4.get(r2)
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            r5 = 1
            r1.setShouldGenerateQualityThumb(r5)
            r9 = r4
            goto L_0x0394
        L_0x0393:
            r9 = 0
        L_0x0394:
            r4 = 2131165816(0x7var_, float:1.794586E38)
            r5 = 100
            if (r9 == 0) goto L_0x0480
            boolean r8 = r9.isVideo()
            if (r8 == 0) goto L_0x03fc
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r9.photoThumbs
            if (r2 == 0) goto L_0x03ee
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x03ee
            org.telegram.messenger.ImageReceiver$BitmapHolder r2 = r0.currentThumb
            if (r2 == 0) goto L_0x03b4
            org.telegram.messenger.ImageReceiver r4 = r0.centerImage
            if (r1 != r4) goto L_0x03b4
            goto L_0x03b5
        L_0x03b4:
            r2 = 0
        L_0x03b5:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r9.photoThumbs
            r6 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6)
            int r6 = r4.w
            if (r6 >= r5) goto L_0x03c6
            int r6 = r4.h
            if (r6 >= r5) goto L_0x03c6
            r3 = 1
        L_0x03c6:
            r1.setNeedsQualityThumb(r3)
            r3 = 0
            r5 = 0
            if (r2 != 0) goto L_0x03d4
            org.telegram.tgnet.TLObject r6 = r9.photoThumbsObject
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForObject(r4, r6)
            goto L_0x03d5
        L_0x03d4:
            r4 = 0
        L_0x03d5:
            if (r2 == 0) goto L_0x03df
            android.graphics.drawable.BitmapDrawable r6 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r2 = r2.bitmap
            r6.<init>(r2)
            goto L_0x03e0
        L_0x03df:
            r6 = 0
        L_0x03e0:
            r7 = 0
            r8 = 0
            r10 = 1
            java.lang.String r11 = "b"
            r1 = r22
            r2 = r3
            r3 = r5
            r5 = r11
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x03fb
        L_0x03ee:
            android.app.Activity r2 = r0.parentActivity
            android.content.res.Resources r2 = r2.getResources()
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r4)
            r1.setImageBitmap((android.graphics.drawable.Drawable) r2)
        L_0x03fb:
            return
        L_0x03fc:
            org.telegram.ui.Components.AnimatedFileDrawable r8 = r0.currentAnimation
            if (r8 == 0) goto L_0x040b
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r2 = r0.containerView
            r8.setSecondParentView(r2)
            org.telegram.ui.Components.AnimatedFileDrawable r2 = r0.currentAnimation
            r1.setImageBitmap((android.graphics.drawable.Drawable) r2)
            return
        L_0x040b:
            int r8 = r0.sharedMediaType
            r10 = 1
            if (r8 != r10) goto L_0x0480
            boolean r2 = r9.canPreviewDocument()
            if (r2 == 0) goto L_0x0473
            org.telegram.tgnet.TLRPC$Document r2 = r9.getDocument()
            r1.setNeedsQualityThumb(r10)
            org.telegram.messenger.ImageReceiver$BitmapHolder r4 = r0.currentThumb
            if (r4 == 0) goto L_0x0426
            org.telegram.messenger.ImageReceiver r8 = r0.centerImage
            if (r1 != r8) goto L_0x0426
            goto L_0x0427
        L_0x0426:
            r4 = 0
        L_0x0427:
            if (r9 == 0) goto L_0x0430
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r9.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r5)
            goto L_0x0431
        L_0x0430:
            r5 = 0
        L_0x0431:
            r8 = 1157627904(0x45000000, float:2048.0)
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r8 = r8 / r10
            int r8 = (int) r8
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            java.util.Locale r11 = java.util.Locale.US
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.Integer r12 = java.lang.Integer.valueOf(r8)
            r6[r3] = r12
            java.lang.Integer r3 = java.lang.Integer.valueOf(r8)
            r8 = 1
            r6[r8] = r3
            java.lang.String r3 = java.lang.String.format(r11, r7, r6)
            if (r4 != 0) goto L_0x0457
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r5, (org.telegram.tgnet.TLRPC$Document) r2)
            goto L_0x0458
        L_0x0457:
            r5 = 0
        L_0x0458:
            if (r4 == 0) goto L_0x0462
            android.graphics.drawable.BitmapDrawable r6 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r4 = r4.bitmap
            r6.<init>(r4)
            goto L_0x0463
        L_0x0462:
            r6 = 0
        L_0x0463:
            int r7 = r2.size
            r8 = 0
            r11 = 0
            java.lang.String r12 = "b"
            r1 = r22
            r2 = r10
            r4 = r5
            r5 = r12
            r10 = r11
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x047f
        L_0x0473:
            org.telegram.ui.Components.OtherDocumentPlaceholderDrawable r2 = new org.telegram.ui.Components.OtherDocumentPlaceholderDrawable
            android.app.Activity r3 = r0.parentActivity
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r4 = r0.containerView
            r2.<init>(r3, r4, r9)
            r1.setImageBitmap((android.graphics.drawable.Drawable) r2)
        L_0x047f:
            return
        L_0x0480:
            r6 = 1
            int[] r7 = new int[r6]
            org.telegram.messenger.ImageLocation r8 = r0.getImageLocation(r2, r7)
            org.telegram.tgnet.TLObject r2 = r0.getFileLocation(r2, r7)
            r1.setNeedsQualityThumb(r6)
            if (r8 == 0) goto L_0x056c
            org.telegram.messenger.ImageReceiver$BitmapHolder r4 = r0.currentThumb
            if (r4 == 0) goto L_0x0499
            org.telegram.messenger.ImageReceiver r10 = r0.centerImage
            if (r1 != r10) goto L_0x0499
            goto L_0x049a
        L_0x0499:
            r4 = 0
        L_0x049a:
            r10 = r7[r3]
            if (r10 != 0) goto L_0x04a1
            r10 = -1
            r7[r3] = r10
        L_0x04a1:
            if (r9 == 0) goto L_0x04ac
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r9.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r10, r5)
            org.telegram.tgnet.TLObject r10 = r9.photoThumbsObject
            goto L_0x04ae
        L_0x04ac:
            r5 = 0
            r10 = 0
        L_0x04ae:
            if (r5 == 0) goto L_0x04b4
            if (r5 != r2) goto L_0x04b4
            r2 = 0
            goto L_0x04b5
        L_0x04b4:
            r2 = r5
        L_0x04b5:
            if (r9 == 0) goto L_0x04bd
            boolean r5 = r9.isWebpage()
            if (r5 != 0) goto L_0x04c8
        L_0x04bd:
            int r5 = r0.avatarsDialogId
            if (r5 != 0) goto L_0x04c8
            boolean r5 = r0.isEvent
            if (r5 == 0) goto L_0x04c6
            goto L_0x04c8
        L_0x04c6:
            r12 = 0
            goto L_0x04c9
        L_0x04c8:
            r12 = 1
        L_0x04c9:
            if (r9 == 0) goto L_0x04e5
            int r5 = r0.sharedMediaType
            r6 = 5
            if (r5 != r6) goto L_0x04e2
            org.telegram.tgnet.TLRPC$Document r5 = r9.getDocument()
            org.telegram.tgnet.TLRPC$TL_videoSize r6 = org.telegram.messenger.MessageObject.getDocumentVideoThumb(r5)
            if (r6 == 0) goto L_0x04df
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$TL_videoSize) r6, (org.telegram.tgnet.TLRPC$Document) r5)
            goto L_0x04e0
        L_0x04df:
            r5 = 0
        L_0x04e0:
            r11 = r9
            goto L_0x0511
        L_0x04e2:
            r11 = r9
        L_0x04e3:
            r5 = 0
            goto L_0x0511
        L_0x04e5:
            int r5 = r0.avatarsDialogId
            if (r5 == 0) goto L_0x050f
            if (r5 <= 0) goto L_0x04fc
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r6 = r0.avatarsDialogId
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            goto L_0x050d
        L_0x04fc:
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r6 = r0.avatarsDialogId
            int r6 = -r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r6)
        L_0x050d:
            r11 = r5
            goto L_0x04e3
        L_0x050f:
            r5 = 0
            r11 = 0
        L_0x0511:
            if (r5 == 0) goto L_0x0540
            r6 = 0
            r9 = 0
            if (r4 != 0) goto L_0x051d
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForObject(r2, r10)
            r10 = r2
            goto L_0x051e
        L_0x051d:
            r10 = 0
        L_0x051e:
            if (r4 == 0) goto L_0x052a
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r4 = r4.bitmap
            r2.<init>(r4)
            r16 = r2
            goto L_0x052c
        L_0x052a:
            r16 = 0
        L_0x052c:
            r13 = r7[r3]
            r14 = 0
            java.lang.String r7 = "b"
            r1 = r22
            r2 = r8
            r3 = r6
            r4 = r5
            r5 = r9
            r6 = r10
            r8 = r16
            r9 = r13
            r10 = r14
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            goto L_0x0582
        L_0x0540:
            r5 = 0
            if (r4 != 0) goto L_0x0549
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForObject(r2, r10)
            r6 = r2
            goto L_0x054a
        L_0x0549:
            r6 = 0
        L_0x054a:
            if (r4 == 0) goto L_0x0556
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r4 = r4.bitmap
            r2.<init>(r4)
            r16 = r2
            goto L_0x0558
        L_0x0556:
            r16 = 0
        L_0x0558:
            r7 = r7[r3]
            r9 = 0
            java.lang.String r10 = "b"
            r1 = r22
            r2 = r8
            r3 = r5
            r4 = r6
            r5 = r10
            r6 = r16
            r8 = r9
            r9 = r11
            r10 = r12
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x0582
        L_0x056c:
            r2 = r7[r3]
            if (r2 != 0) goto L_0x0575
            r2 = 0
            r1.setImageBitmap((android.graphics.Bitmap) r2)
            goto L_0x0582
        L_0x0575:
            android.app.Activity r2 = r0.parentActivity
            android.content.res.Resources r2 = r2.getResources()
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r4)
            r1.setImageBitmap((android.graphics.drawable.Drawable) r2)
        L_0x0582:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.setIndexToImage(org.telegram.messenger.ImageReceiver, int):void");
    }

    public static boolean isShowingImage(MessageObject messageObject) {
        boolean z;
        boolean z2 = true;
        if (Instance == null || (!BuildVars.DEBUG_PRIVATE_VERSION ? Instance.pipAnimationInProgress || !Instance.isVisible || Instance.disableShowCheck || messageObject == null || Instance.currentMessageObject == null || Instance.currentMessageObject.getId() != messageObject.getId() || Instance.currentMessageObject.getDialogId() != messageObject.getDialogId() : Instance.pipAnimationInProgress || !Instance.isVisible || Instance.disableShowCheck || messageObject == null || Instance.currentMessageObject == null || Instance.currentMessageObject.getId() != messageObject.getId() || Instance.currentMessageObject.getDialogId() != messageObject.getDialogId() || (Instance.draggingDown && Instance.pipAvailable && Instance.translationY < 0.0f))) {
            z = false;
        } else {
            z = true;
        }
        if (z || PipInstance == null) {
            return z;
        }
        if (!PipInstance.isVisible || PipInstance.disableShowCheck || messageObject == null || PipInstance.currentMessageObject == null || PipInstance.currentMessageObject.getId() != messageObject.getId() || PipInstance.currentMessageObject.getDialogId() != messageObject.getDialogId()) {
            z2 = false;
        }
        return z2;
    }

    public static boolean isPlayingMessageInPip(MessageObject messageObject) {
        return (PipInstance == null || messageObject == null || PipInstance.currentMessageObject == null || PipInstance.currentMessageObject.getId() != messageObject.getId() || PipInstance.currentMessageObject.getDialogId() != messageObject.getDialogId()) ? false : true;
    }

    public static boolean isPlayingMessage(MessageObject messageObject) {
        return Instance != null && !Instance.pipAnimationInProgress && Instance.isVisible && messageObject != null && Instance.currentMessageObject != null && Instance.currentMessageObject.getId() == messageObject.getId() && Instance.currentMessageObject.getDialogId() == messageObject.getDialogId();
    }

    public static boolean isShowingImage(TLRPC$FileLocation tLRPC$FileLocation) {
        if (Instance == null || !Instance.isVisible || Instance.disableShowCheck || tLRPC$FileLocation == null || Instance.currentFileLocation == null || tLRPC$FileLocation.local_id != Instance.currentFileLocation.location.local_id || tLRPC$FileLocation.volume_id != Instance.currentFileLocation.location.volume_id || tLRPC$FileLocation.dc_id != Instance.currentFileLocation.dc_id) {
            return false;
        }
        return true;
    }

    public static boolean isShowingImage(TLRPC$BotInlineResult tLRPC$BotInlineResult) {
        if (Instance == null || !Instance.isVisible || Instance.disableShowCheck || tLRPC$BotInlineResult == null || Instance.currentBotInlineResult == null || tLRPC$BotInlineResult.id != Instance.currentBotInlineResult.id) {
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
        return openPhoto(messageObject, (TLRPC$FileLocation) null, (ImageLocation) null, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, 0, photoViewerProvider, (ChatActivity) null, j, j2, true);
    }

    public boolean openPhoto(MessageObject messageObject, long j, long j2, PhotoViewerProvider photoViewerProvider, boolean z) {
        return openPhoto(messageObject, (TLRPC$FileLocation) null, (ImageLocation) null, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, 0, photoViewerProvider, (ChatActivity) null, j, j2, z);
    }

    public boolean openPhoto(TLRPC$FileLocation tLRPC$FileLocation, PhotoViewerProvider photoViewerProvider) {
        return openPhoto((MessageObject) null, tLRPC$FileLocation, (ImageLocation) null, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, 0, photoViewerProvider, (ChatActivity) null, 0, 0, true);
    }

    public boolean openPhoto(TLRPC$FileLocation tLRPC$FileLocation, ImageLocation imageLocation, PhotoViewerProvider photoViewerProvider) {
        return openPhoto((MessageObject) null, tLRPC$FileLocation, imageLocation, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, 0, photoViewerProvider, (ChatActivity) null, 0, 0, true);
    }

    public boolean openPhoto(ArrayList<MessageObject> arrayList, int i, long j, long j2, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(arrayList.get(i), (TLRPC$FileLocation) null, (ImageLocation) null, arrayList, (ArrayList<SecureDocument>) null, (ArrayList<Object>) null, i, photoViewerProvider, (ChatActivity) null, j, j2, true);
    }

    public boolean openPhoto(ArrayList<SecureDocument> arrayList, int i, PhotoViewerProvider photoViewerProvider) {
        return openPhoto((MessageObject) null, (TLRPC$FileLocation) null, (ImageLocation) null, (ArrayList<MessageObject>) null, arrayList, (ArrayList<Object>) null, i, photoViewerProvider, (ChatActivity) null, 0, 0, true);
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
        return openPhoto((MessageObject) null, (TLRPC$FileLocation) null, (ImageLocation) null, (ArrayList<MessageObject>) null, (ArrayList<SecureDocument>) null, arrayList, i, photoViewerProvider, chatActivity, 0, 0, true);
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
                float f2 = (float) bitmapHeight;
                float min = Math.min(((float) getContainerViewWidth()) / f, ((float) getContainerViewHeight()) / f2);
                float min2 = (float) Math.min(getContainerViewWidth(1), getContainerViewHeight(1));
                float max = Math.max(min2 / f, min2 / f2);
                if (z) {
                    this.animationStartTime = System.currentTimeMillis();
                    this.animateToX = (float) ((getLeftInset() / 2) - (getRightInset() / 2));
                    int i = this.currentEditMode;
                    if (i == 2) {
                        this.animateToY = (float) (AndroidUtilities.dp(92.0f) - AndroidUtilities.dp(56.0f));
                    } else if (i == 3) {
                        this.animateToY = (float) (AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(56.0f));
                    }
                    this.animateToScale = max / min;
                    this.zoomAnimation = true;
                    return;
                }
                this.animationStartTime = 0;
                this.translationX = (float) ((getLeftInset() / 2) - (getRightInset() / 2));
                this.translationY = (float) ((-AndroidUtilities.dp(56.0f)) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight / 2 : 0));
                float f3 = max / min;
                this.scale = f3;
                updateMinMax(f3);
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
            Bitmap bitmap2 = bitmap;
            int i = orientation;
            if (bitmap2 != null) {
                this.photoCropView.setBitmap(bitmap2, i, false, false, this.paintingOverlay, (VideoEditTextureView) null, (MediaController.CropState) null);
                if (this.currentEditMode == 0) {
                    setCropTranslations(false);
                }
            }
        }
    }

    private void initCropView() {
        if (this.sendPhotoType == 1) {
            this.photoCropView.setBitmap((Bitmap) null, 0, false, false, (PaintingOverlay) null, (VideoEditTextureView) null, (MediaController.CropState) null);
            this.photoCropView.onAppear();
            this.photoCropView.setVisibility(0);
            this.photoCropView.setAlpha(1.0f);
            this.photoCropView.onAppeared();
            this.padImageForHorizontalInsets = true;
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(18:13|(2:15|16)|17|18|(1:20)(1:21)|(1:26)(1:25)|27|(1:29)(1:30)|31|32|(1:34)|35|(1:37)(2:38|(1:40)(1:41))|42|(19:44|(4:46|(1:48)(1:49)|50|(2:52|(5:54|(1:56)|57|(1:66)(1:65)|67)(2:68|(1:70))))|71|(1:73)|74|(1:76)|77|(2:80|78)|119|81|(1:83)|84|(1:86)|87|(1:89)|123|(4:92|(2:94|122)(2:95|121)|96|90)|120|97)(3:98|(5:102|(1:104)(1:105)|106|(1:108)(1:109)|110)|111)|112|(1:114)|115) */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x036b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x036c, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0046 */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x034e  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0052 A[Catch:{ Exception -> 0x036b }] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x005a A[Catch:{ Exception -> 0x036b }] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0068 A[Catch:{ Exception -> 0x036b }] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0071 A[Catch:{ Exception -> 0x036b }] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0081 A[Catch:{ Exception -> 0x036b }] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0084 A[Catch:{ Exception -> 0x036b }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0130  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0145  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x02df  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean openPhoto(org.telegram.messenger.MessageObject r18, org.telegram.tgnet.TLRPC$FileLocation r19, org.telegram.messenger.ImageLocation r20, java.util.ArrayList<org.telegram.messenger.MessageObject> r21, java.util.ArrayList<org.telegram.messenger.SecureDocument> r22, java.util.ArrayList<java.lang.Object> r23, int r24, org.telegram.ui.PhotoViewer.PhotoViewerProvider r25, org.telegram.ui.ChatActivity r26, long r27, long r29, boolean r31) {
        /*
            r17 = this;
            r10 = r17
            r0 = r18
            r3 = r19
            r1 = r25
            r2 = r26
            android.app.Activity r4 = r10.parentActivity
            r11 = 0
            if (r4 == 0) goto L_0x036f
            boolean r4 = r10.isVisible
            if (r4 != 0) goto L_0x036f
            if (r1 != 0) goto L_0x001b
            boolean r4 = r17.checkAnimation()
            if (r4 != 0) goto L_0x036f
        L_0x001b:
            if (r0 != 0) goto L_0x0029
            if (r3 != 0) goto L_0x0029
            if (r21 != 0) goto L_0x0029
            if (r23 != 0) goto L_0x0029
            if (r22 != 0) goto L_0x0029
            if (r20 != 0) goto L_0x0029
            goto L_0x036f
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
            if (r6 == 0) goto L_0x0046
            android.widget.FrameLayout r6 = r10.windowView     // Catch:{ Exception -> 0x0046 }
            r5.removeView(r6)     // Catch:{ Exception -> 0x0046 }
        L_0x0046:
            android.view.WindowManager$LayoutParams r6 = r10.windowLayoutParams     // Catch:{ Exception -> 0x036b }
            r7 = 99
            r6.type = r7     // Catch:{ Exception -> 0x036b }
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x036b }
            r7 = 21
            if (r6 < r7) goto L_0x005a
            android.view.WindowManager$LayoutParams r6 = r10.windowLayoutParams     // Catch:{ Exception -> 0x036b }
            r9 = -2147286784(0xfffffffvar_, float:-2.75865E-40)
            r6.flags = r9     // Catch:{ Exception -> 0x036b }
            goto L_0x0060
        L_0x005a:
            android.view.WindowManager$LayoutParams r6 = r10.windowLayoutParams     // Catch:{ Exception -> 0x036b }
            r9 = 131072(0x20000, float:1.83671E-40)
            r6.flags = r9     // Catch:{ Exception -> 0x036b }
        L_0x0060:
            if (r2 == 0) goto L_0x0071
            org.telegram.tgnet.TLRPC$EncryptedChat r6 = r26.getCurrentEncryptedChat()     // Catch:{ Exception -> 0x036b }
            if (r6 == 0) goto L_0x0071
            android.view.WindowManager$LayoutParams r6 = r10.windowLayoutParams     // Catch:{ Exception -> 0x036b }
            int r9 = r6.flags     // Catch:{ Exception -> 0x036b }
            r9 = r9 | 8192(0x2000, float:1.14794E-41)
            r6.flags = r9     // Catch:{ Exception -> 0x036b }
            goto L_0x0079
        L_0x0071:
            android.view.WindowManager$LayoutParams r6 = r10.windowLayoutParams     // Catch:{ Exception -> 0x036b }
            int r9 = r6.flags     // Catch:{ Exception -> 0x036b }
            r9 = r9 & -8193(0xffffffffffffdfff, float:NaN)
            r6.flags = r9     // Catch:{ Exception -> 0x036b }
        L_0x0079:
            android.view.WindowManager$LayoutParams r6 = r10.windowLayoutParams     // Catch:{ Exception -> 0x036b }
            boolean r9 = r10.useSmoothKeyboard     // Catch:{ Exception -> 0x036b }
            r15 = 16
            if (r9 == 0) goto L_0x0084
            r9 = 32
            goto L_0x0086
        L_0x0084:
            r9 = 16
        L_0x0086:
            r9 = r9 | 256(0x100, float:3.59E-43)
            r6.softInputMode = r9     // Catch:{ Exception -> 0x036b }
            android.widget.FrameLayout r6 = r10.windowView     // Catch:{ Exception -> 0x036b }
            r6.setFocusable(r11)     // Catch:{ Exception -> 0x036b }
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r6 = r10.containerView     // Catch:{ Exception -> 0x036b }
            r6.setFocusable(r11)     // Catch:{ Exception -> 0x036b }
            android.widget.FrameLayout r6 = r10.windowView     // Catch:{ Exception -> 0x036b }
            android.view.WindowManager$LayoutParams r9 = r10.windowLayoutParams     // Catch:{ Exception -> 0x036b }
            r5.addView(r6, r9)     // Catch:{ Exception -> 0x036b }
            r10.doneButtonPressed = r11
            r10.parentChatActivity = r2
            org.telegram.ui.ActionBar.ActionBar r2 = r10.actionBar
            r6 = 2131626024(0x7f0e0828, float:1.8879273E38)
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
            if (r1 != 0) goto L_0x0136
            android.view.VelocityTracker r1 = android.view.VelocityTracker.obtain()
            r10.velocityTracker = r1
        L_0x0136:
            r10.isVisible = r12
            r10.togglePhotosListView(r11, r11)
            r1 = r31 ^ 1
            r10.openedFullScreenVideo = r1
            if (r1 == 0) goto L_0x0145
            r10.toggleActionBar(r11, r11)
            goto L_0x0153
        L_0x0145:
            int r1 = r10.sendPhotoType
            if (r1 != r12) goto L_0x0150
            r17.createCropView()
            r10.toggleActionBar(r11, r11)
            goto L_0x0153
        L_0x0150:
            r10.toggleActionBar(r12, r11)
        L_0x0153:
            r14 = 0
            r10.seekToProgressPending2 = r14
            r10.skipFirstBufferingProgress = r11
            r10.playerInjected = r11
            r9 = 1065353216(0x3var_, float:1.0)
            if (r13 == 0) goto L_0x02df
            r10.disableShowCheck = r12
            r10.animationInProgress = r12
            if (r0 == 0) goto L_0x01d2
            boolean r1 = r13.allowTakeAnimation
            if (r1 == 0) goto L_0x016f
            org.telegram.messenger.ImageReceiver r1 = r13.imageReceiver
            org.telegram.ui.Components.AnimatedFileDrawable r1 = r1.getAnimation()
            goto L_0x0170
        L_0x016f:
            r1 = r4
        L_0x0170:
            r10.currentAnimation = r1
            if (r1 == 0) goto L_0x01d2
            boolean r1 = r18.isVideo()
            if (r1 == 0) goto L_0x01c6
            org.telegram.messenger.ImageReceiver r1 = r13.imageReceiver
            r1.setAllowStartAnimation(r11)
            org.telegram.messenger.ImageReceiver r1 = r13.imageReceiver
            r1.stopAnimation()
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()
            boolean r1 = r1.isPlayingMessage(r0)
            if (r1 == 0) goto L_0x0192
            float r1 = r0.audioProgress
            r10.seekToProgressPending2 = r1
        L_0x0192:
            org.telegram.ui.Components.VideoPlayer r1 = r10.injectingVideoPlayer
            if (r1 != 0) goto L_0x01c0
            int r1 = r0.currentAccount
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)
            org.telegram.tgnet.TLRPC$Document r2 = r18.getDocument()
            boolean r1 = r1.isLoadingVideo(r2, r12)
            if (r1 != 0) goto L_0x01c0
            org.telegram.ui.Components.AnimatedFileDrawable r1 = r10.currentAnimation
            boolean r1 = r1.hasBitmap()
            if (r1 != 0) goto L_0x01be
            int r1 = r0.currentAccount
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)
            org.telegram.tgnet.TLRPC$Document r2 = r18.getDocument()
            boolean r1 = r1.isLoadingVideo(r2, r11)
            if (r1 != 0) goto L_0x01c0
        L_0x01be:
            r1 = 1
            goto L_0x01c1
        L_0x01c0:
            r1 = 0
        L_0x01c1:
            r10.skipFirstBufferingProgress = r1
            r10.currentAnimation = r4
            goto L_0x01d2
        L_0x01c6:
            java.util.ArrayList r1 = r0.getWebPagePhotos(r4, r4)
            int r1 = r1.size()
            if (r1 <= r12) goto L_0x01d2
            r10.currentAnimation = r4
        L_0x01d2:
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
            if (r1 != r12) goto L_0x01fb
            org.telegram.ui.Components.PhotoCropView r1 = r10.photoCropView
            r1.setVisibility(r11)
            org.telegram.ui.Components.PhotoCropView r1 = r10.photoCropView
            r1.setAlpha(r14)
            org.telegram.ui.Components.PhotoCropView r1 = r10.photoCropView
            r1.setFreeform(r11)
        L_0x01fb:
            org.telegram.messenger.ImageReceiver r1 = r13.imageReceiver
            android.graphics.RectF r1 = r1.getDrawRegion()
            float r2 = r1.left
            float r3 = r1.top
            org.telegram.messenger.ImageReceiver r4 = r13.imageReceiver
            int r4 = r4.getOrientation()
            org.telegram.messenger.ImageReceiver r5 = r13.imageReceiver
            int r5 = r5.getAnimatedOrientation()
            if (r5 == 0) goto L_0x0214
            r4 = r5
        L_0x0214:
            org.telegram.ui.Components.ClippingImageView[] r5 = r10.getAnimatingImageViews(r13)
            r6 = 0
        L_0x0219:
            int r7 = r5.length
            if (r6 >= r7) goto L_0x023e
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
            goto L_0x0219
        L_0x023e:
            r17.initCropView()
            int r4 = r10.sendPhotoType
            if (r4 != r12) goto L_0x024f
            org.telegram.ui.Components.PhotoCropView r4 = r10.photoCropView
            r4.hideBackView()
            org.telegram.ui.Components.PhotoCropView r4 = r10.photoCropView
            r4.setAspectRatio(r0)
        L_0x024f:
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
            if (r6 > 0) goto L_0x026b
            r4.width = r7
        L_0x026b:
            int r6 = r4.height
            if (r6 > 0) goto L_0x0271
            r4.height = r7
        L_0x0271:
            int r6 = r5.length
            if (r11 >= r6) goto L_0x02c0
            int r6 = r5.length
            if (r6 <= r12) goto L_0x027d
            r6 = r5[r11]
            r6.setAlpha(r14)
            goto L_0x0282
        L_0x027d:
            r6 = r5[r11]
            r6.setAlpha(r0)
        L_0x0282:
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
            goto L_0x0271
        L_0x02c0:
            android.widget.FrameLayout r0 = r10.windowView
            android.view.ViewTreeObserver r0 = r0.getViewTreeObserver()
            org.telegram.ui.PhotoViewer$53 r1 = new org.telegram.ui.PhotoViewer$53
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
            goto L_0x033e
        L_0x02df:
            r1 = 1065353216(0x3var_, float:1.0)
            if (r23 == 0) goto L_0x0318
            int r2 = r10.sendPhotoType
            r4 = 3
            if (r2 == r4) goto L_0x0318
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r7) goto L_0x02f4
            android.view.WindowManager$LayoutParams r2 = r10.windowLayoutParams
            r4 = -2147417856(0xfffffffvar_, float:-9.2194E-41)
            r2.flags = r4
            goto L_0x02f8
        L_0x02f4:
            android.view.WindowManager$LayoutParams r2 = r10.windowLayoutParams
            r2.flags = r11
        L_0x02f8:
            android.view.WindowManager$LayoutParams r2 = r10.windowLayoutParams
            boolean r4 = r10.useSmoothKeyboard
            if (r4 == 0) goto L_0x0301
            r14 = 32
            goto L_0x0303
        L_0x0301:
            r14 = 16
        L_0x0303:
            r4 = r14 | 256(0x100, float:3.59E-43)
            r2.softInputMode = r4
            android.widget.FrameLayout r2 = r10.windowView
            android.view.WindowManager$LayoutParams r4 = r10.windowLayoutParams
            r5.updateViewLayout(r2, r4)
            android.widget.FrameLayout r2 = r10.windowView
            r2.setFocusable(r12)
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r2 = r10.containerView
            r2.setFocusable(r12)
        L_0x0318:
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
        L_0x033e:
            android.app.Activity r0 = r10.parentActivity
            java.lang.String r1 = "accessibility"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.view.accessibility.AccessibilityManager r0 = (android.view.accessibility.AccessibilityManager) r0
            boolean r1 = r0.isTouchExplorationEnabled()
            if (r1 == 0) goto L_0x036a
            android.view.accessibility.AccessibilityEvent r1 = android.view.accessibility.AccessibilityEvent.obtain()
            r2 = 16384(0x4000, float:2.2959E-41)
            r1.setEventType(r2)
            java.util.List r2 = r1.getText()
            r3 = 2131624001(0x7f0e0041, float:1.887517E38)
            java.lang.String r4 = "AccDescrPhotoViewer"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.add(r3)
            r0.sendAccessibilityEvent(r1)
        L_0x036a:
            return r12
        L_0x036b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x036f:
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.openPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, org.telegram.messenger.ImageLocation, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, int, org.telegram.ui.PhotoViewer$PhotoViewerProvider, org.telegram.ui.ChatActivity, long, long, boolean):boolean");
    }

    public void injectVideoPlayerToMediaController() {
        if (this.videoPlayer.isPlaying()) {
            if (this.playerLooping) {
                this.videoPlayer.setLooping(false);
            }
            MediaController.getInstance().injectVideoPlayer(this.videoPlayer, this.currentMessageObject);
            this.videoPlayer = null;
        }
    }

    /* JADX WARNING: type inference failed for: r3v1, types: [android.view.View, org.telegram.ui.Components.AnimatedFileDrawable, android.graphics.drawable.Drawable] */
    /* JADX WARNING: type inference failed for: r3v9 */
    /* JADX WARNING: type inference failed for: r3v16 */
    public void closePhoto(boolean z, boolean z2) {
        ? r3;
        boolean z3;
        RectF rectF;
        int i;
        AnimatedFileDrawable animation;
        Bitmap animatedBitmap;
        int i2;
        int i3;
        PhotoPaintView photoPaintView2;
        if (z2 || (i3 = this.currentEditMode) == 0) {
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
                if (Build.VERSION.SDK_INT >= 21 && this.containerView != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.updateContainerFlagsRunnable);
                    updateContainerFlags(true);
                }
                int i4 = this.currentEditMode;
                if (i4 != 0) {
                    if (i4 == 2) {
                        this.photoFilterView.shutdown();
                        this.containerView.removeView(this.photoFilterView);
                        this.photoFilterView = null;
                    } else if (i4 == 1) {
                        this.editorDoneLayout.setVisibility(8);
                        this.photoCropView.setVisibility(8);
                    } else if (i4 == 3) {
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
                    if (!this.doneButtonPressed && !this.imagesArrLocals.isEmpty() && (i2 = this.currentIndex) >= 0 && i2 < this.imagesArrLocals.size()) {
                        Object obj = this.imagesArrLocals.get(this.currentIndex);
                        if (obj instanceof MediaController.MediaEditState) {
                            ((MediaController.MediaEditState) obj).editedInfo = getCurrentVideoEditedInfo();
                        }
                    }
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
                        if (this.currentMessageObject != null) {
                            long startTime2 = animation.getStartTime();
                            long currentPosition = this.videoPlayer.getCurrentPosition();
                            if (startTime2 <= 0) {
                                startTime2 = 0;
                            }
                            animation.seekTo(currentPosition + startTime2, !FileLoader.getInstance(this.currentMessageObject.currentAccount).isLoadingVideo(this.currentMessageObject.getDocument(), true));
                        }
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
                        for (int i5 = 0; i5 < animatingImageViews.length; i5++) {
                            animatingImageViews[i5].setAnimationValues(this.animationValues);
                            animatingImageViews[i5].setVisibility(0);
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
                            for (int i6 = 0; i6 < animatingImageViews.length; i6++) {
                                animatingImageViews[i6].setOrientation(orientation);
                                animatingImageViews[i6].setImageBitmap(placeForPhoto.thumb);
                            }
                        } else {
                            layoutParams.width = (int) this.centerImage.getImageWidth();
                            layoutParams.height = (int) this.centerImage.getImageHeight();
                            for (int i7 = 0; i7 < animatingImageViews.length; i7++) {
                                animatingImageViews[i7].setOrientation(this.centerImage.getOrientation());
                                animatingImageViews[i7].setImageBitmap(this.centerImage.getBitmapSafe());
                            }
                            rectF = null;
                        }
                        if (layoutParams.width <= 0) {
                            layoutParams.width = 100;
                        }
                        if (layoutParams.height <= 0) {
                            layoutParams.height = 100;
                        }
                        float min = Math.min(((float) this.windowView.getMeasuredWidth()) / ((float) layoutParams.width), ((float) (AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / ((float) layoutParams.height));
                        float f = this.scale;
                        float f2 = ((float) layoutParams.height) * f * min;
                        float measuredWidth = (((float) this.windowView.getMeasuredWidth()) - ((((float) layoutParams.width) * f) * min)) / 2.0f;
                        float f3 = (((float) (AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) - f2) / 2.0f;
                        for (int i8 = 0; i8 < animatingImageViews.length; i8++) {
                            animatingImageViews[i8].setLayoutParams(layoutParams);
                            animatingImageViews[i8].setTranslationX(this.translationX + measuredWidth);
                            animatingImageViews[i8].setTranslationY(this.translationY + f3);
                            animatingImageViews[i8].setScaleX(this.scale * min);
                            animatingImageViews[i8].setScaleY(this.scale * min);
                        }
                        if (placeForPhoto != null) {
                            placeForPhoto.imageReceiver.setVisible(false, true);
                            int abs = (int) Math.abs(rectF.left - placeForPhoto.imageReceiver.getImageX());
                            int abs2 = (int) Math.abs(rectF.top - placeForPhoto.imageReceiver.getImageY());
                            int[] iArr = new int[2];
                            placeForPhoto.parentView.getLocationInWindow(iArr);
                            int i9 = (int) ((((float) (iArr[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) - (((float) placeForPhoto.viewY) + rectF.top)) + ((float) placeForPhoto.clipTopAddition));
                            if (i9 < 0) {
                                i9 = 0;
                            }
                            float f4 = rectF.top;
                            int height = (int) ((((((float) placeForPhoto.viewY) + f4) + (rectF.bottom - f4)) - ((float) ((iArr[1] + placeForPhoto.parentView.getHeight()) - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)))) + ((float) placeForPhoto.clipBottomAddition));
                            if (height < 0) {
                                height = 0;
                            }
                            int max = Math.max(i9, abs2);
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
                            float f5 = placeForPhoto.scale;
                            fArr2[0] = f5;
                            fArr[1][1] = f5;
                            fArr[1][2] = ((float) placeForPhoto.viewX) + (rectF.left * f5);
                            fArr[1][3] = ((float) placeForPhoto.viewY) + (rectF.top * f5);
                            float f6 = (float) abs;
                            fArr[1][4] = f6 * f5;
                            fArr[1][5] = ((float) max) * f5;
                            fArr[1][6] = ((float) max2) * f5;
                            int i10 = 0;
                            for (int i11 = 4; i10 < i11; i11 = 4) {
                                float[] fArr3 = this.animationValues[1];
                                int i12 = i10 + 7;
                                int[] iArr2 = placeForPhoto.radius;
                                fArr3[i12] = iArr2 != null ? (float) iArr2[i10] : 0.0f;
                                i10++;
                            }
                            float[][] fArr4 = this.animationValues;
                            float[] fArr5 = fArr4[1];
                            float f7 = placeForPhoto.scale;
                            fArr5[11] = ((float) abs2) * f7;
                            fArr4[1][12] = f6 * f7;
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
                            FrameLayoutDrawer frameLayoutDrawer = this.containerView;
                            Property property2 = View.ALPHA;
                            float[] fArr6 = new float[1];
                            fArr6[i] = 0.0f;
                            arrayList.add(ObjectAnimator.ofFloat(frameLayoutDrawer, property2, fArr6));
                            if (this.sendPhotoType == 1) {
                                PhotoCropView photoCropView2 = this.photoCropView;
                                Property property3 = View.ALPHA;
                                float[] fArr7 = new float[1];
                                fArr7[i] = 0.0f;
                                arrayList.add(ObjectAnimator.ofFloat(photoCropView2, property3, fArr7));
                            }
                            animatorSet.playTogether(arrayList);
                        } else {
                            int i13 = AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                            Animator[] animatorArr = new Animator[4];
                            animatorArr[0] = ObjectAnimator.ofInt(this.backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.animatingImageView, View.ALPHA, new float[]{0.0f});
                            ClippingImageView clippingImageView = this.animatingImageView;
                            Property property4 = View.TRANSLATION_Y;
                            float[] fArr8 = new float[1];
                            if (this.translationY < 0.0f) {
                                i13 = -i13;
                            }
                            fArr8[0] = (float) i13;
                            animatorArr[2] = ObjectAnimator.ofFloat(clippingImageView, property4, fArr8);
                            animatorArr[3] = ObjectAnimator.ofFloat(this.containerView, View.ALPHA, new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        }
                        this.animationEndRunnable = new Runnable(placeForPhoto) {
                            public final /* synthetic */ PhotoViewer.PlaceProviderObject f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                PhotoViewer.this.lambda$closePhoto$54$PhotoViewer(this.f$1);
                            }
                        };
                        animatorSet.setDuration(200);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public final void run() {
                                        PhotoViewer.AnonymousClass54.this.lambda$onAnimationEnd$0$PhotoViewer$54();
                                    }
                                });
                            }

                            public /* synthetic */ void lambda$onAnimationEnd$0$PhotoViewer$54() {
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
                            public final /* synthetic */ PhotoViewer.PlaceProviderObject f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                PhotoViewer.this.lambda$closePhoto$55$PhotoViewer(this.f$1);
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
        } else if (i3 != 3 || (photoPaintView2 = this.photoPaintView) == null) {
            if (this.currentEditMode == 1 && this.isCurrentVideo) {
                ((VideoEditTextureView) this.videoTextureView).setViewTransform(this.previousHasTransform, this.previousCropPx, this.previousCropPy, this.previousCropRotation, this.previousCropOrientation, this.previousCropScale, this.previousCropPw, this.previousCropPh, 0.0f, 0.0f);
            }
            switchToEditMode(0);
        } else {
            photoPaintView2.maybeShowDismissalAlert(this, this.parentActivity, new Runnable() {
                public final void run() {
                    PhotoViewer.this.lambda$closePhoto$53$PhotoViewer();
                }
            });
        }
    }

    public /* synthetic */ void lambda$closePhoto$53$PhotoViewer() {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$closePhoto$54$PhotoViewer(PlaceProviderObject placeProviderObject) {
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, (Paint) null);
        }
        this.animationInProgress = 0;
        onPhotoClosed(placeProviderObject);
    }

    public /* synthetic */ void lambda$closePhoto$55$PhotoViewer(PlaceProviderObject placeProviderObject) {
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
        if (this.videoPlayerControlFrameLayout != null) {
            setVideoPlayerControlVisible(false, false);
        }
        CaptionScrollView captionScrollView2 = this.captionScrollView;
        if (captionScrollView2 != null) {
            captionScrollView2.reset();
        }
        GroupedPhotosListView groupedPhotosListView2 = this.groupedPhotosListView;
        if (groupedPhotosListView2 != null) {
            groupedPhotosListView2.reset();
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
            public final /* synthetic */ PhotoViewer.PlaceProviderObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PhotoViewer.this.lambda$onPhotoClosed$56$PhotoViewer(this.f$1);
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

    public /* synthetic */ void lambda$onPhotoClosed$56$PhotoViewer(PlaceProviderObject placeProviderObject) {
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
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PhotoViewer.this.lambda$redraw$57$PhotoViewer(this.f$1);
                }
            }, 100);
        }
    }

    public /* synthetic */ void lambda$redraw$57$PhotoViewer(int i) {
        redraw(i + 1);
    }

    public void onResume() {
        redraw(0);
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.seekTo(videoPlayer2.getCurrentPosition() + 1);
            if (this.playerLooping) {
                this.videoPlayer.setLooping(true);
            }
        }
        PhotoPaintView photoPaintView2 = this.photoPaintView;
        if (photoPaintView2 != null) {
            photoPaintView2.onResume();
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
            return;
        }
        if (this.lastTitle != null) {
            closeCaptionEnter(true);
        }
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null && this.playerLooping) {
            videoPlayer2.setLooping(false);
        }
    }

    public boolean isVisible() {
        return this.isVisible && this.placeProvider != null;
    }

    /* access modifiers changed from: private */
    public void updateMinMax(float f) {
        AspectRatioFrameLayout aspectRatioFrameLayout2;
        if (this.currentEditMode == 3 && (aspectRatioFrameLayout2 = this.aspectRatioFrameLayout) != null && aspectRatioFrameLayout2.getVisibility() == 0 && this.textureUploaded) {
            f *= Math.min(((float) getContainerViewWidth()) / ((float) this.videoTextureView.getMeasuredWidth()), ((float) getContainerViewHeight()) / ((float) this.videoTextureView.getMeasuredHeight()));
        }
        int imageWidth = ((int) ((this.centerImage.getImageWidth() * f) - ((float) getContainerViewWidth()))) / 2;
        int imageHeight = ((int) ((this.centerImage.getImageHeight() * f) - ((float) getContainerViewHeight()))) / 2;
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
        return getContainerViewHeight(false, i);
    }

    private int getContainerViewHeight(boolean z, int i) {
        int i2;
        int dp;
        if (z) {
            i2 = this.containerView.getMeasuredHeight();
        } else {
            i2 = AndroidUtilities.displaySize.y;
            if (i == 0 && Build.VERSION.SDK_INT >= 21) {
                i2 += AndroidUtilities.statusBarHeight;
            }
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
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x048c, code lost:
        if (r0 > r4) goto L_0x0486;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x049b, code lost:
        if (r2 > r4) goto L_0x0495;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x04c0, code lost:
        if (r3 > r4) goto L_0x04ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x04d1, code lost:
        if (r3 > r4) goto L_0x04cb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x05b2, code lost:
        if (r3 > r4) goto L_0x05ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x05c3, code lost:
        if (r3 > r4) goto L_0x05bd;
     */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x03ce  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r20) {
        /*
            r19 = this;
            r1 = r19
            r0 = r20
            int r2 = r1.currentEditMode
            r3 = 5
            r4 = 0
            r6 = 3
            r7 = 2
            r8 = 1
            if (r2 != r6) goto L_0x002b
            long r9 = r1.animationStartTime
            int r2 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x002b
            int r2 = r20.getActionMasked()
            if (r2 == 0) goto L_0x0020
            int r2 = r20.getActionMasked()
            if (r2 != r3) goto L_0x002b
        L_0x0020:
            int r2 = r20.getPointerCount()
            if (r2 < r7) goto L_0x002a
            r19.cancelMoveZoomAnimation()
            goto L_0x002b
        L_0x002a:
            return r8
        L_0x002b:
            int r2 = r1.animationInProgress
            r9 = 0
            if (r2 != 0) goto L_0x06d2
            long r10 = r1.animationStartTime
            int r2 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x0038
            goto L_0x06d2
        L_0x0038:
            int r2 = r1.currentEditMode
            if (r2 != r7) goto L_0x0042
            org.telegram.ui.Components.PhotoFilterView r2 = r1.photoFilterView
            r2.onTouch(r0)
            return r8
        L_0x0042:
            if (r2 == r8) goto L_0x06d1
            if (r2 == r6) goto L_0x004c
            int r2 = r1.sendPhotoType
            if (r2 != r8) goto L_0x004c
            goto L_0x06d1
        L_0x004c:
            org.telegram.ui.Components.PhotoViewerCaptionEnterView r2 = r1.captionEditText
            boolean r2 = r2.isPopupShowing()
            if (r2 != 0) goto L_0x06c8
            org.telegram.ui.Components.PhotoViewerCaptionEnterView r2 = r1.captionEditText
            boolean r2 = r2.isKeyboardVisible()
            if (r2 == 0) goto L_0x005e
            goto L_0x06c8
        L_0x005e:
            int r2 = r1.currentEditMode
            if (r2 != 0) goto L_0x0082
            int r2 = r1.sendPhotoType
            if (r2 == r8) goto L_0x0082
            int r2 = r20.getPointerCount()
            if (r2 != r8) goto L_0x0082
            org.telegram.ui.Components.GestureDetector2 r2 = r1.gestureDetector
            boolean r2 = r2.onTouchEvent(r0)
            if (r2 == 0) goto L_0x0082
            boolean r2 = r1.doubleTap
            if (r2 == 0) goto L_0x0082
            r1.doubleTap = r9
            r1.moving = r9
            r1.zooming = r9
            r1.checkMinMax(r9)
            return r8
        L_0x0082:
            org.telegram.ui.Components.Tooltip r2 = r1.tooltip
            if (r2 == 0) goto L_0x0089
            r2.hide()
        L_0x0089:
            int r2 = r20.getActionMasked()
            r10 = 1073741824(0x40000000, float:2.0)
            if (r2 == 0) goto L_0x05cd
            int r2 = r20.getActionMasked()
            if (r2 != r3) goto L_0x0099
            goto L_0x05cd
        L_0x0099:
            int r2 = r20.getActionMasked()
            r3 = 1077936128(0x40400000, float:3.0)
            r11 = 1065353216(0x3var_, float:1.0)
            r12 = 0
            if (r2 != r7) goto L_0x03f0
            boolean r2 = r1.canZoom
            if (r2 == 0) goto L_0x01b6
            int r2 = r20.getPointerCount()
            if (r2 != r7) goto L_0x01b6
            boolean r2 = r1.draggingDown
            if (r2 != 0) goto L_0x01b6
            boolean r2 = r1.zooming
            if (r2 == 0) goto L_0x01b6
            boolean r2 = r1.changingPage
            if (r2 != 0) goto L_0x01b6
            r1.discardTap = r8
            int r2 = r1.currentEditMode
            if (r2 != r6) goto L_0x013e
            float r2 = r0.getX(r9)
            float r4 = r0.getX(r8)
            float r2 = r2 + r4
            float r2 = r2 / r10
            float r4 = r0.getY(r9)
            float r5 = r0.getY(r8)
            float r4 = r4 + r5
            float r4 = r4 / r10
            float r5 = r1.moveStartX
            float r5 = r5 - r2
            float r10 = r1.moveStartY
            float r10 = r10 - r4
            r1.moveStartX = r2
            r1.moveStartY = r4
            float r11 = r1.translationX
            float r12 = r1.minX
            int r12 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r12 < 0) goto L_0x00ec
            float r12 = r1.maxX
            int r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r11 <= 0) goto L_0x00ed
        L_0x00ec:
            float r5 = r5 / r3
        L_0x00ed:
            float r11 = r1.translationY
            float r12 = r1.minY
            int r12 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r12 < 0) goto L_0x00fb
            float r12 = r1.maxY
            int r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r11 <= 0) goto L_0x00fc
        L_0x00fb:
            float r10 = r10 / r3
        L_0x00fc:
            float r3 = r1.pinchCenterX
            int r11 = r19.getContainerViewWidth()
            int r11 = r11 / r7
            float r11 = (float) r11
            float r3 = r3 - r11
            float r11 = r1.pinchCenterX
            int r12 = r19.getContainerViewWidth()
            int r12 = r12 / r7
            float r12 = (float) r12
            float r11 = r11 - r12
            float r12 = r1.translationX
            float r11 = r11 - r12
            float r12 = r1.scale
            float r13 = r1.pinchStartScale
            float r12 = r12 / r13
            float r11 = r11 / r12
            float r3 = r3 - r11
            float r3 = r3 - r5
            r1.pinchStartX = r3
            float r3 = r1.pinchCenterY
            int r5 = r19.getContainerViewHeight()
            int r5 = r5 / r7
            float r5 = (float) r5
            float r3 = r3 - r5
            float r5 = r1.pinchCenterY
            int r11 = r19.getContainerViewHeight()
            int r11 = r11 / r7
            float r11 = (float) r11
            float r5 = r5 - r11
            float r11 = r1.translationY
            float r5 = r5 - r11
            float r11 = r1.scale
            float r12 = r1.pinchStartScale
            float r11 = r11 / r12
            float r5 = r5 / r11
            float r3 = r3 - r5
            float r3 = r3 - r10
            r1.pinchStartY = r3
            r1.pinchCenterX = r2
            r1.pinchCenterY = r4
        L_0x013e:
            float r2 = r0.getX(r8)
            float r3 = r0.getX(r9)
            float r2 = r2 - r3
            double r2 = (double) r2
            float r4 = r0.getY(r8)
            float r0 = r0.getY(r9)
            float r4 = r4 - r0
            double r4 = (double) r4
            double r2 = java.lang.Math.hypot(r2, r4)
            float r0 = (float) r2
            float r2 = r1.pinchStartDistance
            float r0 = r0 / r2
            float r2 = r1.pinchStartScale
            float r0 = r0 * r2
            r1.scale = r0
            float r0 = r1.pinchCenterX
            int r2 = r19.getContainerViewWidth()
            int r2 = r2 / r7
            float r2 = (float) r2
            float r0 = r0 - r2
            float r2 = r1.pinchCenterX
            int r3 = r19.getContainerViewWidth()
            int r3 = r3 / r7
            float r3 = (float) r3
            float r2 = r2 - r3
            float r3 = r1.pinchStartX
            float r2 = r2 - r3
            float r3 = r1.scale
            float r4 = r1.pinchStartScale
            float r3 = r3 / r4
            float r2 = r2 * r3
            float r0 = r0 - r2
            r1.translationX = r0
            float r0 = r1.pinchCenterY
            int r2 = r19.getContainerViewHeight()
            int r2 = r2 / r7
            float r2 = (float) r2
            float r0 = r0 - r2
            float r2 = r1.pinchCenterY
            int r3 = r19.getContainerViewHeight()
            int r3 = r3 / r7
            float r3 = (float) r3
            float r2 = r2 - r3
            float r3 = r1.pinchStartY
            float r2 = r2 - r3
            float r3 = r1.scale
            float r4 = r1.pinchStartScale
            float r4 = r3 / r4
            float r2 = r2 * r4
            float r0 = r0 - r2
            r1.translationY = r0
            int r2 = r1.currentEditMode
            if (r2 != r6) goto L_0x01aa
            org.telegram.ui.Components.PhotoPaintView r2 = r1.photoPaintView
            float r4 = r1.translationX
            r2.setTransform(r3, r4, r0)
        L_0x01aa:
            float r0 = r1.scale
            r1.updateMinMax(r0)
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r0 = r1.containerView
            r0.invalidate()
            goto L_0x06c7
        L_0x01b6:
            int r2 = r20.getPointerCount()
            if (r2 != r8) goto L_0x06c7
            int r2 = r1.paintViewTouched
            if (r2 != r8) goto L_0x01de
            android.view.MotionEvent r0 = android.view.MotionEvent.obtain(r20)
            org.telegram.ui.Components.PhotoPaintView r2 = r1.photoPaintView
            float r2 = r2.getX()
            float r2 = -r2
            org.telegram.ui.Components.PhotoPaintView r3 = r1.photoPaintView
            float r3 = r3.getY()
            float r3 = -r3
            r0.offsetLocation(r2, r3)
            org.telegram.ui.Components.PhotoPaintView r2 = r1.photoPaintView
            r2.onTouch(r0)
            r0.recycle()
            return r8
        L_0x01de:
            android.view.VelocityTracker r2 = r1.velocityTracker
            if (r2 == 0) goto L_0x01e5
            r2.addMovement(r0)
        L_0x01e5:
            float r2 = r20.getX()
            float r7 = r1.moveStartX
            float r2 = r2 - r7
            float r2 = java.lang.Math.abs(r2)
            float r7 = r20.getY()
            float r13 = r1.dragY
            float r7 = r7 - r13
            float r7 = java.lang.Math.abs(r7)
            int r13 = r1.touchSlop
            float r14 = (float) r13
            int r14 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r14 > 0) goto L_0x0207
            float r13 = (float) r13
            int r13 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r13 <= 0) goto L_0x0217
        L_0x0207:
            r1.discardTap = r8
            r19.hidePressedDrawables()
            org.telegram.ui.PhotoViewer$QualityChooseView r13 = r1.qualityChooseView
            if (r13 == 0) goto L_0x0217
            int r13 = r13.getVisibility()
            if (r13 != 0) goto L_0x0217
            return r8
        L_0x0217:
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r13 = r1.placeProvider
            boolean r13 = r13.canScrollAway()
            if (r13 == 0) goto L_0x0274
            int r13 = r1.currentEditMode
            if (r13 != 0) goto L_0x0274
            int r13 = r1.sendPhotoType
            if (r13 == r8) goto L_0x0274
            boolean r13 = r1.canDragDown
            if (r13 == 0) goto L_0x0274
            boolean r13 = r1.draggingDown
            if (r13 != 0) goto L_0x0274
            float r13 = r1.scale
            int r13 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
            if (r13 != 0) goto L_0x0274
            r13 = 1106247680(0x41var_, float:30.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            int r13 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r13 < 0) goto L_0x0274
            float r7 = r7 / r10
            int r2 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x0274
            r1.draggingDown = r8
            r19.hidePressedDrawables()
            r1.moving = r9
            float r0 = r20.getY()
            r1.dragY = r0
            boolean r0 = r1.isActionBarVisible
            if (r0 == 0) goto L_0x0262
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r0 = r1.containerView
            java.lang.Object r0 = r0.getTag()
            if (r0 == 0) goto L_0x0262
            r1.toggleActionBar(r9, r8)
            goto L_0x0273
        L_0x0262:
            android.widget.FrameLayout r0 = r1.pickerView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0273
            r1.toggleActionBar(r9, r8)
            r1.togglePhotosListView(r9, r8)
            r1.toggleCheckImageView(r9)
        L_0x0273:
            return r8
        L_0x0274:
            boolean r2 = r1.draggingDown
            if (r2 == 0) goto L_0x02f5
            float r0 = r20.getY()
            float r2 = r1.dragY
            float r0 = r0 - r2
            r1.translationY = r0
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r2 == 0) goto L_0x02ee
            org.telegram.ui.PhotoViewer$PlaceProviderObject r2 = r1.currentPlaceObject
            if (r2 == 0) goto L_0x02ee
            boolean r3 = r1.pipAvailable
            if (r3 == 0) goto L_0x02ee
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 >= 0) goto L_0x02e7
            org.telegram.messenger.ImageReceiver r0 = r2.imageReceiver
            boolean r0 = r0.getVisible()
            if (r0 != 0) goto L_0x02e7
            org.telegram.ui.PhotoViewer$PlaceProviderObject r0 = r1.currentPlaceObject
            org.telegram.messenger.ImageReceiver r0 = r0.imageReceiver
            r0.setVisible(r8, r8)
            org.telegram.ui.PhotoViewer$PlaceProviderObject r0 = r1.currentPlaceObject
            org.telegram.messenger.ImageReceiver r0 = r0.imageReceiver
            org.telegram.ui.Components.AnimatedFileDrawable r2 = r0.getAnimation()
            if (r2 == 0) goto L_0x02ee
            android.graphics.Bitmap r0 = r2.getAnimatedBitmap()
            if (r0 == 0) goto L_0x02cf
            android.view.TextureView r3 = r1.videoTextureView     // Catch:{ all -> 0x02cb }
            int r4 = r0.getWidth()     // Catch:{ all -> 0x02cb }
            int r5 = r0.getHeight()     // Catch:{ all -> 0x02cb }
            android.graphics.Bitmap r3 = r3.getBitmap(r4, r5)     // Catch:{ all -> 0x02cb }
            android.graphics.Canvas r4 = new android.graphics.Canvas     // Catch:{ all -> 0x02cb }
            r4.<init>(r0)     // Catch:{ all -> 0x02cb }
            r0 = 0
            r4.drawBitmap(r3, r12, r12, r0)     // Catch:{ all -> 0x02cb }
            r3.recycle()     // Catch:{ all -> 0x02cb }
            goto L_0x02cf
        L_0x02cb:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02cf:
            org.telegram.ui.Components.VideoPlayer r0 = r1.videoPlayer
            long r3 = r0.getCurrentPosition()
            r2.seekTo(r3, r8)
            org.telegram.ui.PhotoViewer$PlaceProviderObject r0 = r1.currentPlaceObject
            org.telegram.messenger.ImageReceiver r0 = r0.imageReceiver
            r0.setAllowStartAnimation(r8)
            org.telegram.ui.PhotoViewer$PlaceProviderObject r0 = r1.currentPlaceObject
            org.telegram.messenger.ImageReceiver r0 = r0.imageReceiver
            r0.startAnimation()
            goto L_0x02ee
        L_0x02e7:
            org.telegram.ui.PhotoViewer$PlaceProviderObject r0 = r1.currentPlaceObject
            org.telegram.messenger.ImageReceiver r0 = r0.imageReceiver
            r0.setVisible(r9, r8)
        L_0x02ee:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r0 = r1.containerView
            r0.invalidate()
            goto L_0x06c7
        L_0x02f5:
            boolean r2 = r1.invalidCoords
            if (r2 != 0) goto L_0x03e0
            long r13 = r1.animationStartTime
            int r2 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x03e0
            float r2 = r1.moveStartX
            float r4 = r20.getX()
            float r2 = r2 - r4
            float r4 = r1.moveStartY
            float r5 = r20.getY()
            float r4 = r4 - r5
            boolean r5 = r1.moving
            if (r5 != 0) goto L_0x0335
            int r5 = r1.currentEditMode
            if (r5 != 0) goto L_0x0335
            float r5 = r1.scale
            int r5 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r5 != 0) goto L_0x032f
            float r5 = java.lang.Math.abs(r4)
            r7 = 1094713344(0x41400000, float:12.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r5 = r5 + r7
            float r7 = java.lang.Math.abs(r2)
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 < 0) goto L_0x0335
        L_0x032f:
            float r5 = r1.scale
            int r5 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r5 == 0) goto L_0x06c7
        L_0x0335:
            boolean r5 = r1.moving
            if (r5 != 0) goto L_0x0342
            r1.moving = r8
            r1.canDragDown = r9
            r19.hidePressedDrawables()
            r2 = 0
            r4 = 0
        L_0x0342:
            float r5 = r20.getX()
            r1.moveStartX = r5
            float r0 = r20.getY()
            r1.moveStartY = r0
            float r0 = r1.scale
            r1.updateMinMax(r0)
            float r0 = r1.translationX
            float r5 = r1.minX
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x0367
            int r0 = r1.currentEditMode
            if (r0 != 0) goto L_0x037b
            org.telegram.messenger.ImageReceiver r0 = r1.rightImage
            boolean r0 = r0.hasImageSet()
            if (r0 == 0) goto L_0x037b
        L_0x0367:
            float r0 = r1.translationX
            float r5 = r1.maxX
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x037c
            int r0 = r1.currentEditMode
            if (r0 != 0) goto L_0x037b
            org.telegram.messenger.ImageReceiver r0 = r1.leftImage
            boolean r0 = r0.hasImageSet()
            if (r0 != 0) goto L_0x037c
        L_0x037b:
            float r2 = r2 / r3
        L_0x037c:
            float r0 = r1.maxY
            int r5 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r5 != 0) goto L_0x03a3
            float r5 = r1.minY
            int r7 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r7 != 0) goto L_0x03a3
            int r7 = r1.currentEditMode
            if (r7 != 0) goto L_0x03a3
            int r7 = r1.sendPhotoType
            if (r7 == r8) goto L_0x03a3
            float r3 = r1.translationY
            float r7 = r3 - r4
            int r7 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r7 >= 0) goto L_0x039b
            r1.translationY = r5
            goto L_0x03b6
        L_0x039b:
            float r3 = r3 - r4
            int r3 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
            if (r3 <= 0) goto L_0x03b2
            r1.translationY = r0
            goto L_0x03b6
        L_0x03a3:
            float r0 = r1.translationY
            float r5 = r1.minY
            int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r5 < 0) goto L_0x03b4
            float r5 = r1.maxY
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x03b2
            goto L_0x03b4
        L_0x03b2:
            r12 = r4
            goto L_0x03b6
        L_0x03b4:
            float r12 = r4 / r3
        L_0x03b6:
            float r0 = r1.translationX
            float r0 = r0 - r2
            r1.translationX = r0
            float r0 = r1.scale
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x03c5
            int r0 = r1.currentEditMode
            if (r0 == 0) goto L_0x03ca
        L_0x03c5:
            float r0 = r1.translationY
            float r0 = r0 - r12
            r1.translationY = r0
        L_0x03ca:
            int r0 = r1.currentEditMode
            if (r0 != r6) goto L_0x03d9
            org.telegram.ui.Components.PhotoPaintView r0 = r1.photoPaintView
            float r2 = r1.scale
            float r3 = r1.translationX
            float r4 = r1.translationY
            r0.setTransform(r2, r3, r4)
        L_0x03d9:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r0 = r1.containerView
            r0.invalidate()
            goto L_0x06c7
        L_0x03e0:
            r1.invalidCoords = r9
            float r2 = r20.getX()
            r1.moveStartX = r2
            float r0 = r20.getY()
            r1.moveStartY = r0
            goto L_0x06c7
        L_0x03f0:
            int r2 = r20.getActionMasked()
            if (r2 == r6) goto L_0x0403
            int r2 = r20.getActionMasked()
            if (r2 == r8) goto L_0x0403
            int r2 = r20.getActionMasked()
            r4 = 6
            if (r2 != r4) goto L_0x06c7
        L_0x0403:
            int r2 = r1.paintViewTouched
            if (r2 != r8) goto L_0x042b
            org.telegram.ui.Components.PhotoPaintView r2 = r1.photoPaintView
            if (r2 == 0) goto L_0x0428
            android.view.MotionEvent r0 = android.view.MotionEvent.obtain(r20)
            org.telegram.ui.Components.PhotoPaintView r2 = r1.photoPaintView
            float r2 = r2.getX()
            float r2 = -r2
            org.telegram.ui.Components.PhotoPaintView r3 = r1.photoPaintView
            float r3 = r3.getY()
            float r3 = -r3
            r0.offsetLocation(r2, r3)
            org.telegram.ui.Components.PhotoPaintView r2 = r1.photoPaintView
            r2.onTouch(r0)
            r0.recycle()
        L_0x0428:
            r1.paintViewTouched = r9
            return r8
        L_0x042b:
            r1.paintViewTouched = r9
            boolean r2 = r1.zooming
            if (r2 == 0) goto L_0x04df
            r1.invalidCoords = r8
            float r0 = r1.scale
            int r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r2 >= 0) goto L_0x0441
            r1.updateMinMax(r11)
            r1.animateTo(r11, r12, r12, r8)
            goto L_0x04d9
        L_0x0441:
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x04a2
            float r0 = r1.pinchCenterX
            int r2 = r19.getContainerViewWidth()
            int r2 = r2 / r7
            float r2 = (float) r2
            float r0 = r0 - r2
            float r2 = r1.pinchCenterX
            int r4 = r19.getContainerViewWidth()
            int r4 = r4 / r7
            float r4 = (float) r4
            float r2 = r2 - r4
            float r4 = r1.pinchStartX
            float r2 = r2 - r4
            float r4 = r1.pinchStartScale
            float r4 = r3 / r4
            float r2 = r2 * r4
            float r0 = r0 - r2
            float r2 = r1.pinchCenterY
            int r4 = r19.getContainerViewHeight()
            int r4 = r4 / r7
            float r4 = (float) r4
            float r2 = r2 - r4
            float r4 = r1.pinchCenterY
            int r5 = r19.getContainerViewHeight()
            int r5 = r5 / r7
            float r5 = (float) r5
            float r4 = r4 - r5
            float r5 = r1.pinchStartY
            float r4 = r4 - r5
            float r5 = r1.pinchStartScale
            float r5 = r3 / r5
            float r4 = r4 * r5
            float r2 = r2 - r4
            r1.updateMinMax(r3)
            float r4 = r1.minX
            int r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x0488
        L_0x0486:
            r0 = r4
            goto L_0x048f
        L_0x0488:
            float r4 = r1.maxX
            int r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r5 <= 0) goto L_0x048f
            goto L_0x0486
        L_0x048f:
            float r4 = r1.minY
            int r5 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x0497
        L_0x0495:
            r2 = r4
            goto L_0x049e
        L_0x0497:
            float r4 = r1.maxY
            int r5 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r5 <= 0) goto L_0x049e
            goto L_0x0495
        L_0x049e:
            r1.animateTo(r3, r0, r2, r8)
            goto L_0x04d9
        L_0x04a2:
            r1.checkMinMax(r8)
            int r0 = r1.currentEditMode
            if (r0 != r6) goto L_0x04d9
            float r0 = r1.translationX
            float r2 = r1.translationY
            float r3 = r1.scale
            r1.updateMinMax(r3)
            float r3 = r1.translationX
            float r4 = r1.minX
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x04bc
        L_0x04ba:
            r0 = r4
            goto L_0x04c3
        L_0x04bc:
            float r4 = r1.maxX
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x04c3
            goto L_0x04ba
        L_0x04c3:
            float r3 = r1.translationY
            float r4 = r1.minY
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x04cd
        L_0x04cb:
            r2 = r4
            goto L_0x04d4
        L_0x04cd:
            float r4 = r1.maxY
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x04d4
            goto L_0x04cb
        L_0x04d4:
            float r3 = r1.scale
            r1.animateTo(r3, r0, r2, r9)
        L_0x04d9:
            r1.zooming = r9
            r1.moving = r9
            goto L_0x06c7
        L_0x04df:
            boolean r2 = r1.draggingDown
            if (r2 == 0) goto L_0x052a
            float r2 = r1.dragY
            float r3 = r20.getY()
            float r2 = r2 - r3
            float r2 = java.lang.Math.abs(r2)
            int r3 = r19.getContainerViewHeight()
            float r3 = (float) r3
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            float r3 = r3 / r4
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0515
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r2 == 0) goto L_0x0511
            boolean r2 = r1.pipAvailable
            if (r2 == 0) goto L_0x0511
            float r2 = r1.dragY
            float r0 = r20.getY()
            float r2 = r2 - r0
            int r0 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x0511
            r1.switchToPip(r8)
            goto L_0x0526
        L_0x0511:
            r1.closePhoto(r8, r9)
            goto L_0x0526
        L_0x0515:
            android.widget.FrameLayout r0 = r1.pickerView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0523
            r1.toggleActionBar(r8, r8)
            r1.toggleCheckImageView(r8)
        L_0x0523:
            r1.animateTo(r11, r12, r12, r9)
        L_0x0526:
            r1.draggingDown = r9
            goto L_0x06c7
        L_0x052a:
            boolean r0 = r1.moving
            if (r0 == 0) goto L_0x06c7
            float r0 = r1.translationX
            float r2 = r1.translationY
            float r3 = r1.scale
            r1.updateMinMax(r3)
            r1.moving = r9
            r1.canDragDown = r8
            android.view.VelocityTracker r3 = r1.velocityTracker
            if (r3 == 0) goto L_0x0550
            float r4 = r1.scale
            int r4 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r4 != 0) goto L_0x0550
            r4 = 1000(0x3e8, float:1.401E-42)
            r3.computeCurrentVelocity(r4)
            android.view.VelocityTracker r3 = r1.velocityTracker
            float r12 = r3.getXVelocity()
        L_0x0550:
            int r3 = r1.currentEditMode
            if (r3 != 0) goto L_0x05a4
            int r3 = r1.sendPhotoType
            if (r3 == r8) goto L_0x05a4
            float r3 = r1.translationX
            float r4 = r1.minX
            int r5 = r19.getContainerViewWidth()
            int r5 = r5 / r6
            float r5 = (float) r5
            float r4 = r4 - r5
            r5 = 1143111680(0x44228000, float:650.0)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x0574
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = -r3
            float r3 = (float) r3
            int r3 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x0580
        L_0x0574:
            org.telegram.messenger.ImageReceiver r3 = r1.rightImage
            boolean r3 = r3.hasImageSet()
            if (r3 == 0) goto L_0x0580
            r19.goToNext()
            return r8
        L_0x0580:
            float r3 = r1.translationX
            float r4 = r1.maxX
            int r7 = r19.getContainerViewWidth()
            int r7 = r7 / r6
            float r6 = (float) r7
            float r4 = r4 + r6
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 > 0) goto L_0x0598
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            int r3 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x05a4
        L_0x0598:
            org.telegram.messenger.ImageReceiver r3 = r1.leftImage
            boolean r3 = r3.hasImageSet()
            if (r3 == 0) goto L_0x05a4
            r19.goToPrev()
            return r8
        L_0x05a4:
            float r3 = r1.translationX
            float r4 = r1.minX
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x05ae
        L_0x05ac:
            r0 = r4
            goto L_0x05b5
        L_0x05ae:
            float r4 = r1.maxX
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x05b5
            goto L_0x05ac
        L_0x05b5:
            float r3 = r1.translationY
            float r4 = r1.minY
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x05bf
        L_0x05bd:
            r2 = r4
            goto L_0x05c6
        L_0x05bf:
            float r4 = r1.maxY
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x05c6
            goto L_0x05bd
        L_0x05c6:
            float r3 = r1.scale
            r1.animateTo(r3, r0, r2, r9)
            goto L_0x06c7
        L_0x05cd:
            r1.discardTap = r9
            android.widget.Scroller r2 = r1.scroller
            boolean r2 = r2.isFinished()
            if (r2 != 0) goto L_0x05dc
            android.widget.Scroller r2 = r1.scroller
            r2.abortAnimation()
        L_0x05dc:
            boolean r2 = r1.draggingDown
            if (r2 != 0) goto L_0x06c7
            boolean r2 = r1.changingPage
            if (r2 != 0) goto L_0x06c7
            boolean r2 = r1.canZoom
            if (r2 == 0) goto L_0x0667
            int r2 = r20.getPointerCount()
            if (r2 != r7) goto L_0x0667
            int r2 = r1.paintViewTouched
            if (r2 != r8) goto L_0x060b
            r11 = 0
            r13 = 0
            r15 = 3
            r16 = 0
            r17 = 0
            r18 = 0
            android.view.MotionEvent r2 = android.view.MotionEvent.obtain(r11, r13, r15, r16, r17, r18)
            org.telegram.ui.Components.PhotoPaintView r3 = r1.photoPaintView
            r3.onTouch(r2)
            r2.recycle()
            r1.paintViewTouched = r7
        L_0x060b:
            float r2 = r0.getX(r8)
            float r3 = r0.getX(r9)
            float r2 = r2 - r3
            double r2 = (double) r2
            float r4 = r0.getY(r8)
            float r5 = r0.getY(r9)
            float r4 = r4 - r5
            double r4 = (double) r4
            double r2 = java.lang.Math.hypot(r2, r4)
            float r2 = (float) r2
            r1.pinchStartDistance = r2
            float r2 = r1.scale
            r1.pinchStartScale = r2
            float r2 = r0.getX(r9)
            float r3 = r0.getX(r8)
            float r2 = r2 + r3
            float r2 = r2 / r10
            r1.pinchCenterX = r2
            float r2 = r0.getY(r9)
            float r0 = r0.getY(r8)
            float r2 = r2 + r0
            float r2 = r2 / r10
            r1.pinchCenterY = r2
            float r0 = r1.translationX
            r1.pinchStartX = r0
            float r0 = r1.translationY
            r1.pinchStartY = r0
            r1.zooming = r8
            r1.moving = r9
            int r0 = r1.currentEditMode
            if (r0 != r6) goto L_0x065c
            float r0 = r1.pinchCenterX
            r1.moveStartX = r0
            r1.moveStartY = r2
            r1.draggingDown = r9
            r1.canDragDown = r9
        L_0x065c:
            r19.hidePressedDrawables()
            android.view.VelocityTracker r0 = r1.velocityTracker
            if (r0 == 0) goto L_0x06c7
            r0.clear()
            goto L_0x06c7
        L_0x0667:
            int r2 = r20.getPointerCount()
            if (r2 != r8) goto L_0x06c7
            int r2 = r1.currentEditMode
            if (r2 != r6) goto L_0x06ae
            int r2 = r1.paintViewTouched
            if (r2 != 0) goto L_0x06c7
            org.telegram.ui.Components.PhotoPaintView r2 = r1.photoPaintView
            android.graphics.Rect r3 = r1.hitRect
            r2.getHitRect(r3)
            android.graphics.Rect r2 = r1.hitRect
            float r3 = r20.getX()
            int r3 = (int) r3
            float r4 = r20.getY()
            int r4 = (int) r4
            boolean r2 = r2.contains(r3, r4)
            if (r2 == 0) goto L_0x06c7
            android.view.MotionEvent r0 = android.view.MotionEvent.obtain(r20)
            org.telegram.ui.Components.PhotoPaintView r2 = r1.photoPaintView
            float r2 = r2.getX()
            float r2 = -r2
            org.telegram.ui.Components.PhotoPaintView r3 = r1.photoPaintView
            float r3 = r3.getY()
            float r3 = -r3
            r0.offsetLocation(r2, r3)
            org.telegram.ui.Components.PhotoPaintView r2 = r1.photoPaintView
            r2.onTouch(r0)
            r0.recycle()
            r1.paintViewTouched = r8
            goto L_0x06c7
        L_0x06ae:
            float r2 = r20.getX()
            r1.moveStartX = r2
            float r0 = r20.getY()
            r1.moveStartY = r0
            r1.dragY = r0
            r1.draggingDown = r9
            r1.canDragDown = r8
            android.view.VelocityTracker r0 = r1.velocityTracker
            if (r0 == 0) goto L_0x06c7
            r0.clear()
        L_0x06c7:
            return r9
        L_0x06c8:
            int r0 = r20.getAction()
            if (r0 != r8) goto L_0x06d1
            r1.closeCaptionEnter(r8)
        L_0x06d1:
            return r8
        L_0x06d2:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0017, code lost:
        if (r2 > r3) goto L_0x0011;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0028, code lost:
        if (r2 > r3) goto L_0x0022;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkMinMax(boolean r6) {
        /*
            r5 = this;
            float r0 = r5.translationX
            float r1 = r5.translationY
            float r2 = r5.scale
            r5.updateMinMax(r2)
            float r2 = r5.translationX
            float r3 = r5.minX
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x0013
        L_0x0011:
            r0 = r3
            goto L_0x001a
        L_0x0013:
            float r3 = r5.maxX
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x001a
            goto L_0x0011
        L_0x001a:
            float r2 = r5.translationY
            float r3 = r5.minY
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x0024
        L_0x0022:
            r1 = r3
            goto L_0x002b
        L_0x0024:
            float r3 = r5.maxY
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x002b
            goto L_0x0022
        L_0x002b:
            float r2 = r5.scale
            r5.animateTo(r2, r0, r1, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.checkMinMax(boolean):void");
    }

    private void goToNext() {
        float containerViewWidth = this.scale != 1.0f ? ((((float) getContainerViewWidth()) - this.centerImage.getImageWidth()) / 2.0f) * this.scale : 0.0f;
        this.switchImageAfterAnimation = 1;
        animateTo(this.scale, ((this.minX - ((float) getContainerViewWidth())) - containerViewWidth) - ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
    }

    private void goToPrev() {
        float containerViewWidth = this.scale != 1.0f ? ((((float) getContainerViewWidth()) - this.centerImage.getImageWidth()) / 2.0f) * this.scale : 0.0f;
        this.switchImageAfterAnimation = 2;
        animateTo(this.scale, this.maxX + ((float) getContainerViewWidth()) + containerViewWidth + ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
    }

    private void cancelMoveZoomAnimation() {
        AnimatorSet animatorSet = this.imageMoveAnimation;
        if (animatorSet != null) {
            float f = this.scale;
            float f2 = this.animationValue;
            float f3 = f + ((this.animateToScale - f) * f2);
            float f4 = this.translationX;
            float f5 = f4 + ((this.animateToX - f4) * f2);
            float f6 = this.translationY;
            animatorSet.cancel();
            this.scale = f3;
            this.translationX = f5;
            this.translationY = f6 + ((this.animateToY - f6) * f2);
            this.animationStartTime = 0;
            updateMinMax(f3);
            this.zoomAnimation = false;
            this.containerView.invalidate();
        }
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
            AnimatorSet animatorSet = new AnimatorSet();
            this.imageMoveAnimation = animatorSet;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, new float[]{0.0f, 1.0f})});
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
        this.playerAutoStarted = false;
        setImageIndex(this.currentIndex + i, z, true);
        if (shouldMessageObjectAutoPlayed(this.currentMessageObject)) {
            this.playerAutoStarted = true;
            onActionClick(true);
            checkProgress(0, false, true);
        }
    }

    private boolean shouldMessageObjectAutoPlayed(MessageObject messageObject) {
        return messageObject != null && messageObject.isVideo() && (messageObject.mediaExists || messageObject.attachPathExists || (messageObject.canStreamVideo() && SharedConfig.streamMedia)) && SharedConfig.autoplayVideo;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0239  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x0294  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x0311  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0313  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x0391  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0394  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x03d4  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x03e1  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x040b  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x04d7  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x04fc  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x0587  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0591  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x05af  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0661  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0671  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x067f  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x0681  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0684  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x069f  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x06a2  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x06a5  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x06b0  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x06b6  */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x06bf  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x0703  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0731  */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x07df  */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x07e5  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x0842  */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x0851  */
    /* JADX WARNING: Removed duplicated region for block: B:363:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"NewApi", "DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r32) {
        /*
            r31 = this;
            r1 = r31
            r2 = r32
            int r0 = r1.animationInProgress
            r3 = 1
            if (r0 == r3) goto L_0x088b
            r4 = 3
            if (r0 == r4) goto L_0x088b
            boolean r5 = r1.isVisible
            r6 = 2
            if (r5 != 0) goto L_0x0019
            if (r0 == r6) goto L_0x0019
            boolean r0 = r1.pipAnimationInProgress
            if (r0 != 0) goto L_0x0019
            goto L_0x088b
        L_0x0019:
            boolean r0 = r1.padImageForHorizontalInsets
            r5 = 0
            if (r0 == 0) goto L_0x0030
            r32.save()
            int r0 = r31.getLeftInset()
            int r0 = r0 / r6
            int r7 = r31.getRightInset()
            int r7 = r7 / r6
            int r0 = r0 - r7
            float r0 = (float) r0
            r2.translate(r0, r5)
        L_0x0030:
            long r7 = java.lang.System.currentTimeMillis()
            long r9 = r1.videoCrossfadeAlphaLastTime
            long r9 = r7 - r9
            r11 = 20
            int r0 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r0 <= 0) goto L_0x0040
            r9 = 17
        L_0x0040:
            r1.videoCrossfadeAlphaLastTime = r7
            android.animation.AnimatorSet r0 = r1.imageMoveAnimation
            r8 = 0
            r11 = 1065353216(0x3var_, float:1.0)
            if (r0 == 0) goto L_0x0088
            android.widget.Scroller r0 = r1.scroller
            boolean r0 = r0.isFinished()
            if (r0 != 0) goto L_0x0056
            android.widget.Scroller r0 = r1.scroller
            r0.abortAnimation()
        L_0x0056:
            float r0 = r1.scale
            float r12 = r1.animateToScale
            float r13 = r12 - r0
            float r14 = r1.animationValue
            float r13 = r13 * r14
            float r13 = r13 + r0
            float r15 = r1.translationX
            float r7 = r1.animateToX
            float r7 = r7 - r15
            float r7 = r7 * r14
            float r7 = r7 + r15
            float r4 = r1.translationY
            float r6 = r1.animateToY
            float r6 = r6 - r4
            float r6 = r6 * r14
            float r4 = r4 + r6
            int r6 = (r12 > r11 ? 1 : (r12 == r11 ? 0 : -1))
            if (r6 != 0) goto L_0x007f
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x007f
            int r0 = (r15 > r5 ? 1 : (r15 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x007f
            r0 = r4
            goto L_0x0081
        L_0x007f:
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x0081:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r6 = r1.containerView
            r6.invalidate()
            goto L_0x0154
        L_0x0088:
            long r6 = r1.animationStartTime
            r12 = 0
            int r0 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r0 == 0) goto L_0x00a3
            float r0 = r1.animateToX
            r1.translationX = r0
            float r0 = r1.animateToY
            r1.translationY = r0
            float r0 = r1.animateToScale
            r1.scale = r0
            r1.animationStartTime = r12
            r1.updateMinMax(r0)
            r1.zoomAnimation = r8
        L_0x00a3:
            android.widget.Scroller r0 = r1.scroller
            boolean r0 = r0.isFinished()
            if (r0 != 0) goto L_0x00fe
            android.widget.Scroller r0 = r1.scroller
            boolean r0 = r0.computeScrollOffset()
            if (r0 == 0) goto L_0x00fe
            android.widget.Scroller r0 = r1.scroller
            int r0 = r0.getStartX()
            float r0 = (float) r0
            float r4 = r1.maxX
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x00d6
            android.widget.Scroller r0 = r1.scroller
            int r0 = r0.getStartX()
            float r0 = (float) r0
            float r4 = r1.minX
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x00d6
            android.widget.Scroller r0 = r1.scroller
            int r0 = r0.getCurrX()
            float r0 = (float) r0
            r1.translationX = r0
        L_0x00d6:
            android.widget.Scroller r0 = r1.scroller
            int r0 = r0.getStartY()
            float r0 = (float) r0
            float r4 = r1.maxY
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x00f9
            android.widget.Scroller r0 = r1.scroller
            int r0 = r0.getStartY()
            float r0 = (float) r0
            float r4 = r1.minY
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x00f9
            android.widget.Scroller r0 = r1.scroller
            int r0 = r0.getCurrY()
            float r0 = (float) r0
            r1.translationY = r0
        L_0x00f9:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r0 = r1.containerView
            r0.invalidate()
        L_0x00fe:
            int r0 = r1.switchImageAfterAnimation
            if (r0 == 0) goto L_0x0146
            r1.openedFullScreenVideo = r8
            java.util.ArrayList<java.lang.Object> r0 = r1.imagesArrLocals
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x012c
            int r0 = r1.currentIndex
            if (r0 < 0) goto L_0x012c
            java.util.ArrayList<java.lang.Object> r4 = r1.imagesArrLocals
            int r4 = r4.size()
            if (r0 >= r4) goto L_0x012c
            java.util.ArrayList<java.lang.Object> r0 = r1.imagesArrLocals
            int r4 = r1.currentIndex
            java.lang.Object r0 = r0.get(r4)
            boolean r4 = r0 instanceof org.telegram.messenger.MediaController.MediaEditState
            if (r4 == 0) goto L_0x012c
            org.telegram.messenger.MediaController$MediaEditState r0 = (org.telegram.messenger.MediaController.MediaEditState) r0
            org.telegram.messenger.VideoEditedInfo r4 = r31.getCurrentVideoEditedInfo()
            r0.editedInfo = r4
        L_0x012c:
            int r0 = r1.switchImageAfterAnimation
            if (r0 != r3) goto L_0x0139
            org.telegram.ui.-$$Lambda$PhotoViewer$EGq5S-wSWptn95wK2UikggNFAek r0 = new org.telegram.ui.-$$Lambda$PhotoViewer$EGq5S-wSWptn95wK2UikggNFAek
            r0.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x0144
        L_0x0139:
            r4 = 2
            if (r0 != r4) goto L_0x0144
            org.telegram.ui.-$$Lambda$PhotoViewer$PpayAsPNeMPDqsSnGTaetQNvCpE r0 = new org.telegram.ui.-$$Lambda$PhotoViewer$PpayAsPNeMPDqsSnGTaetQNvCpE
            r0.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x0144:
            r1.switchImageAfterAnimation = r8
        L_0x0146:
            float r13 = r1.scale
            float r4 = r1.translationY
            float r7 = r1.translationX
            boolean r0 = r1.moving
            if (r0 != 0) goto L_0x0152
            r0 = r4
            goto L_0x0154
        L_0x0152:
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x0154:
            int r6 = r1.currentEditMode
            r12 = 3
            if (r6 != r12) goto L_0x015e
            org.telegram.ui.Components.PhotoPaintView r6 = r1.photoPaintView
            r6.setTransform(r13, r7, r4)
        L_0x015e:
            int r6 = r31.getContainerViewWidth()
            int r12 = r31.getContainerViewHeight()
            int r14 = r1.animationInProgress
            r15 = 1132396544(0x437var_, float:255.0)
            r8 = 2
            if (r14 == r8) goto L_0x01b2
            boolean r8 = r1.pipAnimationInProgress
            if (r8 != 0) goto L_0x01b2
            boolean r8 = r1.isInline
            if (r8 != 0) goto L_0x01b2
            int r8 = r1.currentEditMode
            if (r8 != 0) goto L_0x01ab
            int r8 = r1.sendPhotoType
            if (r8 == r3) goto L_0x01ab
            float r8 = r1.scale
            int r8 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r8 != 0) goto L_0x01ab
            r8 = -1082130432(0xffffffffbvar_, float:-1.0)
            int r8 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r8 == 0) goto L_0x01ab
            boolean r8 = r1.zoomAnimation
            if (r8 != 0) goto L_0x01ab
            float r8 = (float) r6
            r14 = 1082130432(0x40800000, float:4.0)
            float r8 = r8 / r14
            org.telegram.ui.PhotoViewer$BackgroundDrawable r14 = r1.backgroundDrawable
            r5 = 1123942400(0x42fe0000, float:127.0)
            float r0 = java.lang.Math.abs(r0)
            float r0 = java.lang.Math.min(r0, r8)
            float r0 = r0 / r8
            float r0 = r11 - r0
            float r0 = r0 * r15
            float r0 = java.lang.Math.max(r5, r0)
            int r0 = (int) r0
            r14.setAlpha(r0)
            goto L_0x01b2
        L_0x01ab:
            org.telegram.ui.PhotoViewer$BackgroundDrawable r0 = r1.backgroundDrawable
            r5 = 255(0xff, float:3.57E-43)
            r0.setAlpha(r5)
        L_0x01b2:
            r5 = 0
            r1.sideImage = r5
            int r0 = r1.currentEditMode
            if (r0 != 0) goto L_0x01fe
            int r0 = r1.sendPhotoType
            if (r0 == r3) goto L_0x01fe
            float r0 = r1.scale
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 < 0) goto L_0x01f5
            boolean r0 = r1.zoomAnimation
            if (r0 != 0) goto L_0x01f5
            boolean r0 = r1.zooming
            if (r0 != 0) goto L_0x01f5
            float r0 = r1.maxX
            r8 = 1084227584(0x40a00000, float:5.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r14 = (float) r14
            float r0 = r0 + r14
            int r0 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x01de
            org.telegram.messenger.ImageReceiver r0 = r1.leftImage
            r1.sideImage = r0
            goto L_0x01f5
        L_0x01de:
            float r0 = r1.minX
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            float r0 = r0 - r8
            int r0 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x01ef
            org.telegram.messenger.ImageReceiver r0 = r1.rightImage
            r1.sideImage = r0
            goto L_0x01f5
        L_0x01ef:
            org.telegram.ui.Components.GroupedPhotosListView r0 = r1.groupedPhotosListView
            r8 = 0
            r0.setMoveProgress(r8)
        L_0x01f5:
            org.telegram.messenger.ImageReceiver r0 = r1.sideImage
            if (r0 == 0) goto L_0x01fb
            r0 = 1
            goto L_0x01fc
        L_0x01fb:
            r0 = 0
        L_0x01fc:
            r1.changingPage = r0
        L_0x01fe:
            org.telegram.messenger.ImageReceiver r0 = r1.sideImage
            org.telegram.messenger.ImageReceiver r8 = r1.rightImage
            r14 = 1050253722(0x3e99999a, float:0.3)
            r18 = 1106247680(0x41var_, float:30.0)
            r19 = 1073741824(0x40000000, float:2.0)
            if (r0 != r8) goto L_0x02d5
            boolean r0 = r1.zoomAnimation
            if (r0 != 0) goto L_0x022d
            float r0 = r1.minX
            int r8 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r8 >= 0) goto L_0x022d
            float r0 = r0 - r7
            float r8 = (float) r6
            float r0 = r0 / r8
            float r0 = java.lang.Math.min(r11, r0)
            float r8 = r11 - r0
            float r8 = r8 * r14
            int r5 = -r6
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r17 = 2
            int r20 = r20 / 2
            int r5 = r5 - r20
            float r5 = (float) r5
            goto L_0x0231
        L_0x022d:
            r5 = r7
            r0 = 1065353216(0x3var_, float:1.0)
            r8 = 0
        L_0x0231:
            org.telegram.messenger.ImageReceiver r15 = r1.sideImage
            boolean r15 = r15.hasBitmapImage()
            if (r15 == 0) goto L_0x0294
            r32.save()
            int r15 = r6 / 2
            float r15 = (float) r15
            int r14 = r12 / 2
            float r14 = (float) r14
            r2.translate(r15, r14)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r15 = 2
            int r14 = r14 / r15
            int r14 = r14 + r6
            float r14 = (float) r14
            float r14 = r14 + r5
            r15 = 0
            r2.translate(r14, r15)
            float r14 = r11 - r8
            r2.scale(r14, r14)
            org.telegram.messenger.ImageReceiver r14 = r1.sideImage
            int r14 = r14.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r15 = r1.sideImage
            int r15 = r15.getBitmapHeight()
            float r3 = (float) r6
            float r14 = (float) r14
            float r3 = r3 / r14
            float r11 = (float) r12
            float r15 = (float) r15
            float r11 = r11 / r15
            float r3 = java.lang.Math.min(r3, r11)
            float r14 = r14 * r3
            int r11 = (int) r14
            float r15 = r15 * r3
            int r3 = (int) r15
            org.telegram.messenger.ImageReceiver r14 = r1.sideImage
            r14.setAlpha(r0)
            org.telegram.messenger.ImageReceiver r14 = r1.sideImage
            int r15 = -r11
            r17 = 2
            int r15 = r15 / 2
            float r15 = (float) r15
            r22 = r9
            int r9 = -r3
            int r9 = r9 / 2
            float r9 = (float) r9
            float r10 = (float) r11
            float r3 = (float) r3
            r14.setImageCoords(r15, r9, r10, r3)
            org.telegram.messenger.ImageReceiver r3 = r1.sideImage
            r3.draw(r2)
            r32.restore()
            goto L_0x0296
        L_0x0294:
            r22 = r9
        L_0x0296:
            org.telegram.ui.Components.GroupedPhotosListView r3 = r1.groupedPhotosListView
            float r9 = -r0
            r3.setMoveProgress(r9)
            r32.save()
            float r3 = r4 / r13
            r2.translate(r5, r3)
            float r3 = (float) r6
            float r5 = r1.scale
            r9 = 1065353216(0x3var_, float:1.0)
            float r5 = r5 + r9
            float r3 = r3 * r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r5 = (float) r5
            float r3 = r3 + r5
            float r3 = r3 / r19
            float r5 = -r4
            float r5 = r5 / r13
            r2.translate(r3, r5)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r3 = r1.photoProgressViews
            r5 = 1
            r3 = r3[r5]
            float r11 = r9 - r8
            r3.setScale(r11)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r3 = r1.photoProgressViews
            r3 = r3[r5]
            r3.setAlpha(r0)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r0 = r0[r5]
            r0.onDraw(r2)
            r32.restore()
            goto L_0x02d7
        L_0x02d5:
            r22 = r9
        L_0x02d7:
            boolean r0 = r1.zoomAnimation
            if (r0 != 0) goto L_0x02ff
            float r0 = r1.maxX
            int r3 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r3 <= 0) goto L_0x02ff
            int r3 = r1.currentEditMode
            if (r3 != 0) goto L_0x02ff
            int r3 = r1.sendPhotoType
            r5 = 1
            if (r3 == r5) goto L_0x02ff
            float r0 = r7 - r0
            float r3 = (float) r6
            float r0 = r0 / r3
            r3 = 1065353216(0x3var_, float:1.0)
            float r0 = java.lang.Math.min(r3, r0)
            r5 = 1050253722(0x3e99999a, float:0.3)
            float r5 = r5 * r0
            float r0 = r3 - r0
            float r3 = r1.maxX
            r8 = r5
            goto L_0x0303
        L_0x02ff:
            r3 = r7
            r0 = 1065353216(0x3var_, float:1.0)
            r8 = 0
        L_0x0303:
            boolean r5 = r1.videoSizeSet
            if (r5 == 0) goto L_0x0313
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r5 = r1.aspectRatioFrameLayout
            if (r5 == 0) goto L_0x0313
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x0313
            r5 = 1
            goto L_0x0314
        L_0x0313:
            r5 = 0
        L_0x0314:
            org.telegram.messenger.ImageReceiver r9 = r1.centerImage
            boolean r9 = r9.hasBitmapImage()
            if (r9 != 0) goto L_0x0335
            if (r5 == 0) goto L_0x0323
            boolean r9 = r1.textureUploaded
            if (r9 == 0) goto L_0x0323
            goto L_0x0335
        L_0x0323:
            r30 = r0
            r28 = r3
            r16 = r4
            r29 = r6
            r21 = r7
            r26 = r8
            r25 = r12
            r27 = r13
            goto L_0x065d
        L_0x0335:
            r32.save()
            int r9 = r6 / 2
            int r10 = r31.getAdditionX()
            int r9 = r9 + r10
            float r9 = (float) r9
            int r10 = r12 / 2
            int r11 = r31.getAdditionY()
            int r10 = r10 + r11
            float r10 = (float) r10
            r2.translate(r9, r10)
            int r9 = r1.currentPanTranslationY
            float r9 = (float) r9
            float r9 = r9 + r4
            r2.translate(r3, r9)
            float r9 = r13 - r8
            r2.scale(r9, r9)
            int r9 = r1.currentEditMode
            r10 = 3
            if (r9 != r10) goto L_0x037a
            int r9 = r1.keyboardSize
            r10 = 1101004800(0x41a00000, float:20.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            if (r9 <= r10) goto L_0x037a
            r9 = 1
            r10 = 0
            int r11 = r1.getContainerViewHeight(r9, r10)
            int r9 = r1.getContainerViewHeight(r10, r10)
            if (r11 == r9) goto L_0x037a
            int r11 = r11 - r9
            r9 = 2
            int r11 = r11 / r9
            float r9 = (float) r11
            r10 = 0
            r2.translate(r10, r9)
        L_0x037a:
            if (r5 == 0) goto L_0x0394
            boolean r9 = r1.textureUploaded
            if (r9 == 0) goto L_0x0394
            boolean r9 = r1.videoSizeSet
            if (r9 == 0) goto L_0x0394
            boolean r9 = r1.videoCrossfadeStarted
            if (r9 == 0) goto L_0x0394
            float r9 = r1.videoCrossfadeAlpha
            r10 = 1065353216(0x3var_, float:1.0)
            int r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r9 == 0) goto L_0x0391
            goto L_0x0394
        L_0x0391:
            r21 = r7
            goto L_0x03ca
        L_0x0394:
            org.telegram.messenger.ImageReceiver r9 = r1.centerImage
            r9.setAlpha(r0)
            org.telegram.messenger.ImageReceiver r9 = r1.centerImage
            int r9 = r9.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r10 = r1.centerImage
            int r10 = r10.getBitmapHeight()
            float r11 = (float) r6
            float r9 = (float) r9
            float r11 = r11 / r9
            float r14 = (float) r12
            float r10 = (float) r10
            float r14 = r14 / r10
            float r11 = java.lang.Math.min(r11, r14)
            float r9 = r9 * r11
            int r9 = (int) r9
            float r10 = r10 * r11
            int r10 = (int) r10
            org.telegram.messenger.ImageReceiver r11 = r1.centerImage
            int r14 = -r9
            r15 = 2
            int r14 = r14 / r15
            float r14 = (float) r14
            r21 = r7
            int r7 = -r10
            int r7 = r7 / r15
            float r7 = (float) r7
            float r9 = (float) r9
            float r10 = (float) r10
            r11.setImageCoords(r14, r7, r9, r10)
            org.telegram.messenger.ImageReceiver r7 = r1.centerImage
            r7.draw(r2)
        L_0x03ca:
            if (r5 == 0) goto L_0x03e1
            boolean r7 = r1.textureUploaded
            if (r7 == 0) goto L_0x03e1
            boolean r7 = r1.videoSizeSet
            if (r7 == 0) goto L_0x03e1
            android.view.TextureView r7 = r1.videoTextureView
            int r7 = r7.getMeasuredWidth()
            android.view.TextureView r9 = r1.videoTextureView
            int r9 = r9.getMeasuredHeight()
            goto L_0x03ed
        L_0x03e1:
            org.telegram.messenger.ImageReceiver r7 = r1.centerImage
            int r7 = r7.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r9 = r1.centerImage
            int r9 = r9.getBitmapHeight()
        L_0x03ed:
            float r10 = (float) r6
            float r11 = (float) r7
            float r14 = r10 / r11
            float r15 = (float) r12
            r24 = r7
            float r7 = (float) r9
            r25 = r9
            float r9 = r15 / r7
            float r9 = java.lang.Math.min(r14, r9)
            float r11 = r11 * r9
            int r11 = (int) r11
            float r7 = r7 * r9
            int r7 = (int) r7
            android.view.TextureView r14 = r1.videoTextureView
            r26 = r8
            boolean r8 = r14 instanceof org.telegram.ui.Components.VideoEditTextureView
            if (r8 == 0) goto L_0x04d7
            int r8 = r1.switchingToMode
            r27 = r13
            r13 = 3
            if (r8 == r13) goto L_0x04cc
            int r8 = r1.currentEditMode
            if (r8 == r13) goto L_0x04cc
            org.telegram.ui.Components.VideoEditTextureView r14 = (org.telegram.ui.Components.VideoEditTextureView) r14
            int r8 = r14.getOrientation()
            r13 = 90
            if (r8 == r13) goto L_0x042c
            r13 = 270(0x10e, float:3.78E-43)
            if (r8 != r13) goto L_0x0425
            goto L_0x042c
        L_0x0425:
            r16 = r4
            r13 = r24
            r4 = r25
            goto L_0x0432
        L_0x042c:
            r16 = r4
            r4 = r24
            r13 = r25
        L_0x0432:
            float r13 = (float) r13
            float r24 = r14.getCropPw()
            r25 = r12
            float r12 = r13 * r24
            int r12 = (int) r12
            float r4 = (float) r4
            float r24 = r14.getCropPh()
            r28 = r3
            float r3 = r4 * r24
            int r3 = (int) r3
            float r12 = (float) r12
            float r24 = r10 / r12
            float r3 = (float) r3
            float r29 = r24 * r3
            int r29 = (r29 > r15 ? 1 : (r29 == r15 ? 0 : -1))
            if (r29 <= 0) goto L_0x0452
            float r24 = r15 / r3
        L_0x0452:
            r29 = r6
            int r6 = r1.currentEditMode
            r30 = r0
            r0 = 1
            if (r6 != r0) goto L_0x045f
            int r0 = r1.switchingToMode
            if (r0 != 0) goto L_0x0476
        L_0x045f:
            org.telegram.ui.PhotoViewer$EditState r0 = r1.editState
            org.telegram.messenger.MediaController$CropState r0 = r0.cropState
            if (r0 == 0) goto L_0x0476
            float r12 = r12 * r24
            float r3 = r3 * r24
            float r0 = -r12
            float r0 = r0 / r19
            float r6 = -r3
            float r6 = r6 / r19
            float r12 = r12 / r19
            float r3 = r3 / r19
            r2.clipRect(r0, r6, r12, r3)
        L_0x0476:
            boolean r0 = r14.hasViewTransform()
            if (r0 == 0) goto L_0x04e3
            int r0 = r1.currentEditMode
            r3 = 1
            if (r0 != r3) goto L_0x0492
            float r0 = r14.getScale()
            float r10 = r10 / r13
            float r3 = r10 * r4
            int r3 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r3 <= 0) goto L_0x048e
            float r10 = r15 / r4
        L_0x048e:
            float r10 = r10 / r9
            float r0 = r0 * r10
            goto L_0x04a1
        L_0x0492:
            org.telegram.ui.PhotoViewer$EditState r0 = r1.editState
            org.telegram.messenger.MediaController$CropState r0 = r0.cropState
            if (r0 == 0) goto L_0x049b
            float r0 = r0.cropScale
            goto L_0x049d
        L_0x049b:
            r0 = 1065353216(0x3var_, float:1.0)
        L_0x049d:
            float r24 = r24 / r9
            float r0 = r0 * r24
        L_0x04a1:
            float r3 = r14.getCropAreaX()
            float r6 = r14.getCropAreaY()
            r2.translate(r3, r6)
            r2.scale(r0, r0)
            float r0 = r14.getCropPx()
            float r0 = r0 * r13
            float r0 = r0 * r9
            float r3 = r14.getCropPy()
            float r3 = r3 * r4
            float r3 = r3 * r9
            r2.translate(r0, r3)
            float r0 = r14.getRotation()
            float r3 = (float) r8
            float r0 = r0 + r3
            r2.rotate(r0)
            goto L_0x04e3
        L_0x04cc:
            r30 = r0
            r28 = r3
            r16 = r4
            r29 = r6
            r25 = r12
            goto L_0x04e3
        L_0x04d7:
            r30 = r0
            r28 = r3
            r16 = r4
            r29 = r6
            r25 = r12
            r27 = r13
        L_0x04e3:
            int r0 = -r11
            r3 = 2
            int r0 = r0 / r3
            float r0 = (float) r0
            int r4 = -r7
            int r4 = r4 / r3
            float r3 = (float) r4
            r2.translate(r0, r3)
            if (r5 != 0) goto L_0x04f7
            org.telegram.ui.Components.PaintingOverlay r0 = r1.paintingOverlay
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x04fa
        L_0x04f7:
            r2.scale(r9, r9)
        L_0x04fa:
            if (r5 == 0) goto L_0x0587
            boolean r0 = r1.videoCrossfadeStarted
            if (r0 != 0) goto L_0x0519
            boolean r0 = r1.textureUploaded
            if (r0 == 0) goto L_0x0519
            boolean r0 = r1.videoSizeSet
            if (r0 == 0) goto L_0x0519
            r0 = 1
            r1.videoCrossfadeStarted = r0
            r0 = 0
            r1.videoCrossfadeAlpha = r0
            long r3 = java.lang.System.currentTimeMillis()
            r1.videoCrossfadeAlphaLastTime = r3
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r0 = r1.containerView
            r0.getMeasuredHeight()
        L_0x0519:
            android.view.TextureView r0 = r1.videoTextureView
            float r3 = r1.videoCrossfadeAlpha
            float r3 = r3 * r30
            r0.setAlpha(r3)
            android.view.TextureView r0 = r1.videoTextureView
            boolean r3 = r0 instanceof org.telegram.ui.Components.VideoEditTextureView
            if (r3 == 0) goto L_0x054a
            org.telegram.ui.Components.VideoEditTextureView r0 = (org.telegram.ui.Components.VideoEditTextureView) r0
            int r6 = r29 - r11
            r3 = 2
            int r6 = r6 / r3
            int r4 = r31.getAdditionX()
            int r6 = r6 + r4
            float r4 = (float) r6
            float r4 = r4 + r28
            int r12 = r25 - r7
            int r12 = r12 / r3
            int r3 = r31.getAdditionY()
            int r12 = r12 + r3
            float r3 = (float) r12
            float r3 = r3 + r16
            int r5 = r1.currentPanTranslationY
            float r5 = (float) r5
            float r3 = r3 + r5
            float r5 = (float) r11
            float r6 = (float) r7
            r0.setViewRect(r4, r3, r5, r6)
        L_0x054a:
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r0 = r1.aspectRatioFrameLayout
            r0.draw(r2)
            boolean r0 = r1.videoCrossfadeStarted
            if (r0 == 0) goto L_0x057b
            float r0 = r1.videoCrossfadeAlpha
            r3 = 1065353216(0x3var_, float:1.0)
            int r4 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x057b
            r9 = r22
            float r3 = (float) r9
            boolean r4 = r1.playerInjected
            if (r4 == 0) goto L_0x0565
            r4 = 1120403456(0x42CLASSNAME, float:100.0)
            goto L_0x0567
        L_0x0565:
            r4 = 1128792064(0x43480000, float:200.0)
        L_0x0567:
            float r3 = r3 / r4
            float r0 = r0 + r3
            r1.videoCrossfadeAlpha = r0
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r0 = r1.containerView
            r0.invalidate()
            float r0 = r1.videoCrossfadeAlpha
            r3 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x057d
            r1.videoCrossfadeAlpha = r3
            goto L_0x057d
        L_0x057b:
            r9 = r22
        L_0x057d:
            org.telegram.ui.Components.PaintingOverlay r0 = r1.paintingOverlay
            float r3 = r1.videoCrossfadeAlpha
            float r3 = r3 * r30
            r0.setAlpha(r3)
            goto L_0x0589
        L_0x0587:
            r9 = r22
        L_0x0589:
            org.telegram.ui.Components.PaintingOverlay r0 = r1.paintingOverlay
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x05a6
            org.telegram.ui.Components.PaintingOverlay r0 = r1.paintingOverlay
            int r0 = r0.getMeasuredWidth()
            org.telegram.ui.Components.PaintingOverlay r3 = r1.paintingOverlay
            int r3 = r3.getMeasuredHeight()
            r4 = 0
            r2.clipRect(r4, r4, r0, r3)
            org.telegram.ui.Components.PaintingOverlay r0 = r1.paintingOverlay
            r0.draw(r2)
        L_0x05a6:
            r32.restore()
            r0 = 0
        L_0x05aa:
            android.graphics.drawable.GradientDrawable[] r3 = r1.pressedDrawable
            int r3 = r3.length
            if (r0 >= r3) goto L_0x065d
            boolean[] r3 = r1.drawPressedDrawable
            boolean r3 = r3[r0]
            if (r3 != 0) goto L_0x05c2
            float[] r3 = r1.pressedDrawableAlpha
            r3 = r3[r0]
            r4 = 0
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x05bf
            goto L_0x05c2
        L_0x05bf:
            r5 = 1132396544(0x437var_, float:255.0)
            goto L_0x0615
        L_0x05c2:
            android.graphics.drawable.GradientDrawable[] r3 = r1.pressedDrawable
            r3 = r3[r0]
            float[] r4 = r1.pressedDrawableAlpha
            r4 = r4[r0]
            r5 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r5
            int r4 = (int) r4
            r3.setAlpha(r4)
            if (r0 != 0) goto L_0x05eb
            android.graphics.drawable.GradientDrawable[] r3 = r1.pressedDrawable
            r3 = r3[r0]
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r4 = r1.containerView
            int r4 = r4.getMeasuredWidth()
            int r4 = r4 / 5
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r6 = r1.containerView
            int r6 = r6.getMeasuredHeight()
            r7 = 0
            r3.setBounds(r7, r7, r4, r6)
            goto L_0x060e
        L_0x05eb:
            android.graphics.drawable.GradientDrawable[] r3 = r1.pressedDrawable
            r3 = r3[r0]
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r4 = r1.containerView
            int r4 = r4.getMeasuredWidth()
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r6 = r1.containerView
            int r6 = r6.getMeasuredWidth()
            int r6 = r6 / 5
            int r4 = r4 - r6
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r6 = r1.containerView
            int r6 = r6.getMeasuredWidth()
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r7 = r1.containerView
            int r7 = r7.getMeasuredHeight()
            r8 = 0
            r3.setBounds(r4, r8, r6, r7)
        L_0x060e:
            android.graphics.drawable.GradientDrawable[] r3 = r1.pressedDrawable
            r3 = r3[r0]
            r3.draw(r2)
        L_0x0615:
            boolean[] r3 = r1.drawPressedDrawable
            boolean r3 = r3[r0]
            r4 = 1127481344(0x43340000, float:180.0)
            if (r3 == 0) goto L_0x063c
            float[] r3 = r1.pressedDrawableAlpha
            r6 = r3[r0]
            r7 = 1065353216(0x3var_, float:1.0)
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 >= 0) goto L_0x0659
            r6 = r3[r0]
            float r8 = (float) r9
            float r8 = r8 / r4
            float r6 = r6 + r8
            r3[r0] = r6
            r4 = r3[r0]
            int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r4 <= 0) goto L_0x0636
            r3[r0] = r7
        L_0x0636:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r3 = r1.containerView
            r3.invalidate()
            goto L_0x0659
        L_0x063c:
            float[] r3 = r1.pressedDrawableAlpha
            r6 = r3[r0]
            r7 = 0
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 <= 0) goto L_0x0659
            r6 = r3[r0]
            float r8 = (float) r9
            float r8 = r8 / r4
            float r6 = r6 - r8
            r3[r0] = r6
            r4 = r3[r0]
            int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r4 >= 0) goto L_0x0654
            r3[r0] = r7
        L_0x0654:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r3 = r1.containerView
            r3.invalidate()
        L_0x0659:
            int r0 = r0 + 1
            goto L_0x05aa
        L_0x065d:
            boolean r0 = r1.isCurrentVideo
            if (r0 == 0) goto L_0x0671
            org.telegram.ui.Components.VideoPlayer r0 = r1.videoPlayer
            if (r0 == 0) goto L_0x066e
            boolean r0 = r0.isPlaying()
            if (r0 != 0) goto L_0x066c
            goto L_0x066e
        L_0x066c:
            r0 = 0
            goto L_0x066f
        L_0x066e:
            r0 = 1
        L_0x066f:
            r5 = r0
            goto L_0x0672
        L_0x0671:
            r5 = 1
        L_0x0672:
            org.telegram.ui.Components.RadialProgressView r0 = r1.miniProgressView
            int r0 = r0.getVisibility()
            if (r0 == 0) goto L_0x0681
            android.animation.AnimatorSet r0 = r1.miniProgressAnimator
            if (r0 == 0) goto L_0x067f
            goto L_0x0681
        L_0x067f:
            r0 = 0
            goto L_0x0682
        L_0x0681:
            r0 = 1
        L_0x0682:
            if (r5 == 0) goto L_0x0703
            boolean r3 = r1.zoomAnimation
            if (r3 != 0) goto L_0x0696
            r3 = r28
            float r4 = -r3
            float r5 = r1.maxX
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x0698
            float r8 = r3 + r5
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x069b
        L_0x0696:
            r3 = r28
        L_0x0698:
            r4 = 1065353216(0x3var_, float:1.0)
            r8 = 0
        L_0x069b:
            int r5 = (r27 > r4 ? 1 : (r27 == r4 ? 0 : -1))
            if (r5 != 0) goto L_0x06a2
            r5 = r16
            goto L_0x06a3
        L_0x06a2:
            r5 = 0
        L_0x06a3:
            if (r0 == 0) goto L_0x06b0
            org.telegram.ui.Components.RadialProgressView r6 = r1.miniProgressView
            float r6 = r6.getAlpha()
            float r11 = r4 - r6
            float r4 = r30 * r11
            goto L_0x06b2
        L_0x06b0:
            r4 = r30
        L_0x06b2:
            boolean r6 = r1.pipAnimationInProgress
            if (r6 == 0) goto L_0x06bf
            org.telegram.ui.ActionBar.ActionBar r6 = r1.actionBar
            float r6 = r6.getAlpha()
            float r4 = r4 * r6
            goto L_0x06df
        L_0x06bf:
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r6 = r1.photoProgressViews
            r7 = 0
            r6 = r6[r7]
            int r6 = r6.backgroundState
            r7 = 4
            if (r6 != r7) goto L_0x06df
            r6 = 1090519040(0x41000000, float:8.0)
            float r6 = org.telegram.messenger.AndroidUtilities.dpf2(r6)
            org.telegram.ui.ActionBar.ActionBar r7 = r1.actionBar
            float r7 = r7.getAlpha()
            r9 = 1065353216(0x3var_, float:1.0)
            float r11 = r9 - r7
            float r6 = r6 * r11
            float r5 = r5 + r6
            goto L_0x06e1
        L_0x06df:
            r9 = 1065353216(0x3var_, float:1.0)
        L_0x06e1:
            r32.save()
            r2.translate(r8, r5)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r5 = r1.photoProgressViews
            r6 = 0
            r5 = r5[r6]
            float r11 = r9 - r26
            r5.setScale(r11)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r5 = r1.photoProgressViews
            r5 = r5[r6]
            r5.setAlpha(r4)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r4 = r1.photoProgressViews
            r4 = r4[r6]
            r4.onDraw(r2)
            r32.restore()
            goto L_0x0705
        L_0x0703:
            r3 = r28
        L_0x0705:
            if (r0 == 0) goto L_0x072b
            boolean r0 = r1.pipAnimationInProgress
            if (r0 != 0) goto L_0x072b
            r32.save()
            org.telegram.ui.Components.RadialProgressView r0 = r1.miniProgressView
            int r0 = r0.getLeft()
            float r0 = (float) r0
            float r0 = r0 + r3
            org.telegram.ui.Components.RadialProgressView r3 = r1.miniProgressView
            int r3 = r3.getTop()
            float r3 = (float) r3
            float r4 = r16 / r27
            float r3 = r3 + r4
            r2.translate(r0, r3)
            org.telegram.ui.Components.RadialProgressView r0 = r1.miniProgressView
            r0.draw(r2)
            r32.restore()
        L_0x072b:
            org.telegram.messenger.ImageReceiver r0 = r1.sideImage
            org.telegram.messenger.ImageReceiver r3 = r1.leftImage
            if (r0 != r3) goto L_0x07df
            boolean r0 = r0.hasBitmapImage()
            if (r0 == 0) goto L_0x0797
            r32.save()
            int r6 = r29 / 2
            float r0 = (float) r6
            int r12 = r25 / 2
            float r3 = (float) r12
            r2.translate(r0, r3)
            r0 = r29
            float r3 = (float) r0
            float r4 = r1.scale
            r5 = 1065353216(0x3var_, float:1.0)
            float r4 = r4 + r5
            float r4 = r4 * r3
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r5 = (float) r5
            float r4 = r4 + r5
            float r4 = -r4
            float r4 = r4 / r19
            float r4 = r4 + r21
            r5 = 0
            r2.translate(r4, r5)
            org.telegram.messenger.ImageReceiver r4 = r1.sideImage
            int r4 = r4.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r5 = r1.sideImage
            int r5 = r5.getBitmapHeight()
            float r4 = (float) r4
            float r3 = r3 / r4
            r6 = r25
            float r6 = (float) r6
            float r5 = (float) r5
            float r6 = r6 / r5
            float r3 = java.lang.Math.min(r3, r6)
            float r4 = r4 * r3
            int r4 = (int) r4
            float r5 = r5 * r3
            int r3 = (int) r5
            org.telegram.messenger.ImageReceiver r5 = r1.sideImage
            r6 = 1065353216(0x3var_, float:1.0)
            r5.setAlpha(r6)
            org.telegram.messenger.ImageReceiver r5 = r1.sideImage
            int r6 = -r4
            r7 = 2
            int r6 = r6 / r7
            float r6 = (float) r6
            int r8 = -r3
            int r8 = r8 / r7
            float r7 = (float) r8
            float r4 = (float) r4
            float r3 = (float) r3
            r5.setImageCoords(r6, r7, r4, r3)
            org.telegram.messenger.ImageReceiver r3 = r1.sideImage
            r3.draw(r2)
            r32.restore()
            goto L_0x0799
        L_0x0797:
            r0 = r29
        L_0x0799:
            org.telegram.ui.Components.GroupedPhotosListView r3 = r1.groupedPhotosListView
            r4 = 1065353216(0x3var_, float:1.0)
            float r11 = r4 - r30
            r3.setMoveProgress(r11)
            r32.save()
            float r3 = r16 / r27
            r7 = r21
            r2.translate(r7, r3)
            float r0 = (float) r0
            float r3 = r1.scale
            float r3 = r3 + r4
            float r0 = r0 * r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r0 = r0 + r3
            float r0 = -r0
            float r0 = r0 / r19
            r4 = r16
            float r3 = -r4
            float r3 = r3 / r27
            r2.translate(r0, r3)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r3 = 2
            r0 = r0[r3]
            r5 = 1065353216(0x3var_, float:1.0)
            r0.setScale(r5)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r0 = r0[r3]
            r0.setAlpha(r5)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r0 = r0[r3]
            r0.onDraw(r2)
            r32.restore()
            goto L_0x07e1
        L_0x07df:
            r4 = r16
        L_0x07e1:
            int r0 = r1.waitingForDraw
            if (r0 == 0) goto L_0x083e
            r3 = 1
            int r0 = r0 - r3
            r1.waitingForDraw = r0
            if (r0 != 0) goto L_0x0839
            android.widget.ImageView r0 = r1.textureImageView
            if (r0 == 0) goto L_0x0830
            android.view.TextureView r0 = r1.videoTextureView     // Catch:{ all -> 0x0809 }
            int r0 = r0.getWidth()     // Catch:{ all -> 0x0809 }
            android.view.TextureView r3 = r1.videoTextureView     // Catch:{ all -> 0x0809 }
            int r3 = r3.getHeight()     // Catch:{ all -> 0x0809 }
            android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0809 }
            android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createBitmap(r0, r3, r5)     // Catch:{ all -> 0x0809 }
            r1.currentBitmap = r0     // Catch:{ all -> 0x0809 }
            android.view.TextureView r3 = r1.changedTextureView     // Catch:{ all -> 0x0809 }
            r3.getBitmap(r0)     // Catch:{ all -> 0x0809 }
            goto L_0x0817
        L_0x0809:
            r0 = move-exception
            android.graphics.Bitmap r3 = r1.currentBitmap
            if (r3 == 0) goto L_0x0814
            r3.recycle()
            r3 = 0
            r1.currentBitmap = r3
        L_0x0814:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0817:
            android.graphics.Bitmap r0 = r1.currentBitmap
            if (r0 == 0) goto L_0x0829
            android.widget.ImageView r0 = r1.textureImageView
            r3 = 0
            r0.setVisibility(r3)
            android.widget.ImageView r0 = r1.textureImageView
            android.graphics.Bitmap r3 = r1.currentBitmap
            r0.setImageBitmap(r3)
            goto L_0x0830
        L_0x0829:
            android.widget.ImageView r0 = r1.textureImageView
            r3 = 0
            r0.setImageDrawable(r3)
            goto L_0x0831
        L_0x0830:
            r3 = 0
        L_0x0831:
            org.telegram.ui.Components.PipVideoView r0 = r1.pipVideoView
            r0.close()
            r1.pipVideoView = r3
            goto L_0x083e
        L_0x0839:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r0 = r1.containerView
            r0.invalidate()
        L_0x083e:
            boolean r0 = r1.padImageForHorizontalInsets
            if (r0 == 0) goto L_0x0845
            r32.restore()
        L_0x0845:
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r0 = r1.aspectRatioFrameLayout
            if (r0 == 0) goto L_0x088b
            org.telegram.ui.Components.VideoForwardDrawable r0 = r1.videoForwardDrawable
            boolean r0 = r0.isAnimating()
            if (r0 == 0) goto L_0x088b
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r0 = r1.aspectRatioFrameLayout
            int r0 = r0.getMeasuredHeight()
            float r0 = (float) r0
            float r3 = r1.scale
            r5 = 1065353216(0x3var_, float:1.0)
            float r3 = r3 - r5
            float r0 = r0 * r3
            int r0 = (int) r0
            r3 = 2
            int r0 = r0 / r3
            org.telegram.ui.Components.VideoForwardDrawable r3 = r1.videoForwardDrawable
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r5 = r1.aspectRatioFrameLayout
            int r5 = r5.getLeft()
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r6 = r1.aspectRatioFrameLayout
            int r6 = r6.getTop()
            int r6 = r6 - r0
            float r4 = r4 / r27
            int r4 = (int) r4
            int r6 = r6 + r4
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r7 = r1.aspectRatioFrameLayout
            int r7 = r7.getRight()
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r8 = r1.aspectRatioFrameLayout
            int r8 = r8.getBottom()
            int r8 = r8 + r0
            int r8 = r8 + r4
            r3.setBounds(r5, r6, r7, r8)
            org.telegram.ui.Components.VideoForwardDrawable r0 = r1.videoForwardDrawable
            r0.draw(r2)
        L_0x088b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onDraw(android.graphics.Canvas):void");
    }

    public /* synthetic */ void lambda$onDraw$58$PhotoViewer() {
        switchToNextIndex(1, false);
    }

    public /* synthetic */ void lambda$onDraw$59$PhotoViewer() {
        switchToNextIndex(-1, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0037, code lost:
        if (r1.exists() == false) goto L_0x0039;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x013d, code lost:
        if (r1.exists() == false) goto L_0x017e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x017c, code lost:
        if (r1.exists() == false) goto L_0x017e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x023c  */
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
            if (r1 == 0) goto L_0x012d
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
            if (r1 != 0) goto L_0x012b
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1)
            boolean r6 = r1.exists()
            if (r6 != 0) goto L_0x012b
            boolean r1 = org.telegram.messenger.SharedConfig.streamMedia
            if (r1 == 0) goto L_0x017e
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            long r6 = r1.getDialogId()
            int r1 = (int) r6
            if (r1 == 0) goto L_0x017e
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            boolean r1 = r1.isVideo()
            if (r1 == 0) goto L_0x017e
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            boolean r1 = r1.canStreamVideo()
            if (r1 == 0) goto L_0x017e
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
            r1 = r5
            goto L_0x0180
        L_0x012b:
            r0 = r5
            goto L_0x0180
        L_0x012d:
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.currentBotInlineResult
            if (r0 == 0) goto L_0x017e
            org.telegram.tgnet.TLRPC$Document r1 = r0.document
            if (r1 == 0) goto L_0x0140
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1)
            boolean r0 = r1.exists()
            if (r0 != 0) goto L_0x012b
            goto L_0x017e
        L_0x0140:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.content
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r0 == 0) goto L_0x017e
            java.io.File r1 = new java.io.File
            r0 = 4
            java.io.File r0 = org.telegram.messenger.FileLoader.getDirectory(r0)
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
            r1.<init>(r0, r6)
            boolean r0 = r1.exists()
            if (r0 != 0) goto L_0x012b
        L_0x017e:
            r0 = r5
            r1 = r0
        L_0x0180:
            if (r1 == 0) goto L_0x0188
            if (r0 != 0) goto L_0x0188
            android.net.Uri r0 = android.net.Uri.fromFile(r1)
        L_0x0188:
            if (r0 != 0) goto L_0x023c
            if (r11 == 0) goto L_0x0253
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            if (r11 == 0) goto L_0x01c3
            int r11 = r10.currentAccount
            org.telegram.messenger.FileLoader r11 = org.telegram.messenger.FileLoader.getInstance(r11)
            java.lang.String[] r0 = r10.currentFileNames
            r0 = r0[r2]
            boolean r11 = r11.isLoadingFile(r0)
            if (r11 != 0) goto L_0x01b3
            int r11 = r10.currentAccount
            org.telegram.messenger.FileLoader r11 = org.telegram.messenger.FileLoader.getInstance(r11)
            org.telegram.messenger.MessageObject r0 = r10.currentMessageObject
            org.telegram.tgnet.TLRPC$Document r0 = r0.getDocument()
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            r11.loadFile(r0, r1, r4, r2)
            goto L_0x022c
        L_0x01b3:
            int r11 = r10.currentAccount
            org.telegram.messenger.FileLoader r11 = org.telegram.messenger.FileLoader.getInstance(r11)
            org.telegram.messenger.MessageObject r0 = r10.currentMessageObject
            org.telegram.tgnet.TLRPC$Document r0 = r0.getDocument()
            r11.cancelLoadFile((org.telegram.tgnet.TLRPC$Document) r0)
            goto L_0x022c
        L_0x01c3:
            org.telegram.tgnet.TLRPC$BotInlineResult r11 = r10.currentBotInlineResult
            if (r11 == 0) goto L_0x022c
            org.telegram.tgnet.TLRPC$Document r0 = r11.document
            if (r0 == 0) goto L_0x01f9
            int r11 = r10.currentAccount
            org.telegram.messenger.FileLoader r11 = org.telegram.messenger.FileLoader.getInstance(r11)
            java.lang.String[] r0 = r10.currentFileNames
            r0 = r0[r2]
            boolean r11 = r11.isLoadingFile(r0)
            if (r11 != 0) goto L_0x01eb
            int r11 = r10.currentAccount
            org.telegram.messenger.FileLoader r11 = org.telegram.messenger.FileLoader.getInstance(r11)
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.currentBotInlineResult
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            org.telegram.messenger.MessageObject r1 = r10.currentMessageObject
            r11.loadFile(r0, r1, r4, r2)
            goto L_0x022c
        L_0x01eb:
            int r11 = r10.currentAccount
            org.telegram.messenger.FileLoader r11 = org.telegram.messenger.FileLoader.getInstance(r11)
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.currentBotInlineResult
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            r11.cancelLoadFile((org.telegram.tgnet.TLRPC$Document) r0)
            goto L_0x022c
        L_0x01f9:
            org.telegram.tgnet.TLRPC$WebDocument r11 = r11.content
            boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r11 == 0) goto L_0x022c
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.currentBotInlineResult
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.content
            java.lang.String r0 = r0.url
            boolean r11 = r11.isLoadingHttpFile(r0)
            if (r11 != 0) goto L_0x021f
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.currentBotInlineResult
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.content
            java.lang.String r0 = r0.url
            int r1 = r10.currentAccount
            r11.loadHttpFile(r0, r3, r1)
            goto L_0x022c
        L_0x021f:
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$BotInlineResult r0 = r10.currentBotInlineResult
            org.telegram.tgnet.TLRPC$WebDocument r0 = r0.content
            java.lang.String r0 = r0.url
            r11.cancelLoadHttpFile(r0)
        L_0x022c:
            org.telegram.messenger.ImageReceiver r11 = r10.centerImage
            android.graphics.drawable.Drawable r11 = r11.getStaticThumb()
            boolean r0 = r11 instanceof org.telegram.ui.Components.OtherDocumentPlaceholderDrawable
            if (r0 == 0) goto L_0x0253
            org.telegram.ui.Components.OtherDocumentPlaceholderDrawable r11 = (org.telegram.ui.Components.OtherDocumentPlaceholderDrawable) r11
            r11.checkFileExist()
            goto L_0x0253
        L_0x023c:
            int r11 = r10.sharedMediaType
            if (r11 != r4) goto L_0x0250
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            boolean r11 = r11.canPreviewDocument()
            if (r11 != 0) goto L_0x0250
            org.telegram.messenger.MessageObject r11 = r10.currentMessageObject
            android.app.Activity r0 = r10.parentActivity
            org.telegram.messenger.AndroidUtilities.openDocument(r11, r0, r5)
            return
        L_0x0250:
            r10.preparePlayer(r0, r4, r2)
        L_0x0253:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onActionClick(boolean):void");
    }

    public boolean onDown(MotionEvent motionEvent) {
        if (!this.doubleTap && this.checkImageView.getVisibility() != 0) {
            boolean[] zArr = this.drawPressedDrawable;
            if (!zArr[0] && !zArr[1]) {
                float x = motionEvent.getX();
                int min = Math.min(135, this.containerView.getMeasuredWidth() / 8);
                if (x < ((float) min)) {
                    if (this.leftImage.hasImageSet()) {
                        this.drawPressedDrawable[0] = true;
                        this.containerView.invalidate();
                    }
                } else if (x > ((float) (this.containerView.getMeasuredWidth() - min)) && this.rightImage.hasImageSet()) {
                    this.drawPressedDrawable[1] = true;
                    this.containerView.invalidate();
                }
            }
        }
        return false;
    }

    public boolean canDoubleTap(MotionEvent motionEvent) {
        MessageObject messageObject;
        if (this.checkImageView.getVisibility() == 0) {
            return true;
        }
        boolean[] zArr = this.drawPressedDrawable;
        if (zArr[0] || zArr[1]) {
            return true;
        }
        float x = motionEvent.getX();
        int min = Math.min(135, this.containerView.getMeasuredWidth() / 8);
        if ((x >= ((float) min) && x <= ((float) (this.containerView.getMeasuredWidth() - min))) || (messageObject = this.currentMessageObject) == null) {
            return true;
        }
        if (!messageObject.isVideo() || SystemClock.elapsedRealtime() - this.lastPhotoSetTime < 500 || !canDoubleTapSeekVideo(motionEvent)) {
            return false;
        }
        return true;
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
        MessageObject messageObject;
        if (this.discardTap) {
            return false;
        }
        float x = motionEvent.getX();
        if (this.checkImageView.getVisibility() != 0) {
            int min = Math.min(135, this.containerView.getMeasuredWidth() / 8);
            if (x < ((float) min)) {
                if (this.leftImage.hasImageSet()) {
                    switchToNextIndex(-1, true);
                    return true;
                }
            } else if (x > ((float) (this.containerView.getMeasuredWidth() - min)) && this.rightImage.hasImageSet()) {
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
                if (!(photoProgressViewArr[0] == null || this.containerView == null)) {
                    int access$15600 = photoProgressViewArr[0].backgroundState;
                    if (x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                        if (!z) {
                            if (access$15600 > 0 && access$15600 <= 3) {
                                onActionClick(true);
                                checkProgress(0, false, true);
                                return true;
                            }
                        } else if ((access$15600 == 3 || access$15600 == 4) && this.photoProgressViews[0].isVisible()) {
                            toggleVideoPlayer();
                            return true;
                        }
                    }
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
                TLRPC$BotInlineResult tLRPC$BotInlineResult = this.currentBotInlineResult;
                if (tLRPC$BotInlineResult != null && (tLRPC$BotInlineResult.type.equals("video") || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                    int access$156002 = this.photoProgressViews[0].backgroundState;
                    if (access$156002 > 0 && access$156002 <= 3) {
                        float y2 = motionEvent.getY();
                        if (x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y2 >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y2 <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                            onActionClick(true);
                            checkProgress(0, false, true);
                            return true;
                        }
                    }
                } else if (this.sendPhotoType == 2 && this.isCurrentVideo) {
                    toggleVideoPlayer();
                }
            } else if (this.isCurrentVideo) {
                VideoPlayer videoPlayer2 = this.videoPlayer;
                if (videoPlayer2 != null && !this.muteVideo) {
                    videoPlayer2.setVolume(1.0f);
                }
                toggleVideoPlayer();
            } else {
                this.checkImageView.performClick();
            }
        }
        return true;
    }

    private boolean canDoubleTapSeekVideo(MotionEvent motionEvent) {
        if (this.videoPlayer == null) {
            return false;
        }
        boolean z = motionEvent.getX() >= ((float) ((getContainerViewWidth() / 3) * 2));
        long currentPosition = this.videoPlayer.getCurrentPosition();
        long duration = this.videoPlayer.getDuration();
        if (currentPosition == -9223372036854775807L || duration <= 15000) {
            return false;
        }
        if (!z || duration - currentPosition > 10000) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00f1, code lost:
        if (r0 > r14) goto L_0x00eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0100, code lost:
        if (r1 > r14) goto L_0x00fa;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onDoubleTap(android.view.MotionEvent r14) {
        /*
            r13 = this;
            org.telegram.ui.Components.VideoPlayer r0 = r13.videoPlayer
            r1 = 0
            r3 = 0
            r4 = 1
            if (r0 == 0) goto L_0x006f
            boolean r5 = r13.videoPlayerControlVisible
            if (r5 == 0) goto L_0x006f
            long r5 = r0.getCurrentPosition()
            org.telegram.ui.Components.VideoPlayer r0 = r13.videoPlayer
            long r7 = r0.getDuration()
            float r0 = r14.getX()
            int r9 = r13.getContainerViewWidth()
            int r9 = r9 / 3
            int r10 = r9 * 2
            float r10 = (float) r10
            int r11 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            boolean r11 = r13.canDoubleTapSeekVideo(r14)
            if (r11 == 0) goto L_0x006f
            r11 = 10000(0x2710, double:4.9407E-320)
            int r10 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r10 < 0) goto L_0x0033
            long r11 = r11 + r5
            goto L_0x003c
        L_0x0033:
            float r10 = (float) r9
            int r10 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r10 >= 0) goto L_0x003b
            long r11 = r5 - r11
            goto L_0x003c
        L_0x003b:
            r11 = r5
        L_0x003c:
            int r10 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r10 == 0) goto L_0x006f
            int r14 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r14 <= 0) goto L_0x0046
            r1 = r7
            goto L_0x004c
        L_0x0046:
            int r14 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
            if (r14 >= 0) goto L_0x004b
            goto L_0x004c
        L_0x004b:
            r1 = r11
        L_0x004c:
            org.telegram.ui.Components.VideoForwardDrawable r14 = r13.videoForwardDrawable
            float r5 = (float) r9
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x0054
            r3 = 1
        L_0x0054:
            r14.setLeftSide(r3)
            org.telegram.ui.Components.VideoPlayer r14 = r13.videoPlayer
            r14.seekTo(r1)
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r14 = r13.containerView
            r14.invalidate()
            org.telegram.ui.Components.VideoPlayerSeekBar r14 = r13.videoPlayerSeekbar
            float r0 = (float) r1
            float r1 = (float) r7
            float r0 = r0 / r1
            r14.setProgress(r0)
            android.view.View r14 = r13.videoPlayerSeekbarView
            r14.invalidate()
            return r4
        L_0x006f:
            boolean r0 = r13.canZoom
            if (r0 == 0) goto L_0x0110
            float r0 = r13.scale
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 0
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x008a
            float r0 = r13.translationY
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 != 0) goto L_0x0110
            float r0 = r13.translationX
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x008a
            goto L_0x0110
        L_0x008a:
            long r7 = r13.animationStartTime
            int r0 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x0110
            int r0 = r13.animationInProgress
            if (r0 == 0) goto L_0x0096
            goto L_0x0110
        L_0x0096:
            float r0 = r13.scale
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x0107
            float r0 = r14.getX()
            int r1 = r13.getContainerViewWidth()
            int r1 = r1 / 2
            float r1 = (float) r1
            float r0 = r0 - r1
            float r1 = r14.getX()
            int r2 = r13.getContainerViewWidth()
            int r2 = r2 / 2
            float r2 = (float) r2
            float r1 = r1 - r2
            float r2 = r13.translationX
            float r1 = r1 - r2
            float r2 = r13.scale
            r3 = 1077936128(0x40400000, float:3.0)
            float r2 = r3 / r2
            float r1 = r1 * r2
            float r0 = r0 - r1
            float r1 = r14.getY()
            int r2 = r13.getContainerViewHeight()
            int r2 = r2 / 2
            float r2 = (float) r2
            float r1 = r1 - r2
            float r14 = r14.getY()
            int r2 = r13.getContainerViewHeight()
            int r2 = r2 / 2
            float r2 = (float) r2
            float r14 = r14 - r2
            float r2 = r13.translationY
            float r14 = r14 - r2
            float r2 = r13.scale
            float r2 = r3 / r2
            float r14 = r14 * r2
            float r1 = r1 - r14
            r13.updateMinMax(r3)
            float r14 = r13.minX
            int r2 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r2 >= 0) goto L_0x00ed
        L_0x00eb:
            r0 = r14
            goto L_0x00f4
        L_0x00ed:
            float r14 = r13.maxX
            int r2 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r2 <= 0) goto L_0x00f4
            goto L_0x00eb
        L_0x00f4:
            float r14 = r13.minY
            int r2 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r2 >= 0) goto L_0x00fc
        L_0x00fa:
            r1 = r14
            goto L_0x0103
        L_0x00fc:
            float r14 = r13.maxY
            int r2 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r2 <= 0) goto L_0x0103
            goto L_0x00fa
        L_0x0103:
            r13.animateTo(r3, r0, r1, r4)
            goto L_0x010a
        L_0x0107:
            r13.animateTo(r5, r6, r6, r4)
        L_0x010a:
            r13.doubleTap = r4
            r13.hidePressedDrawables()
            return r4
        L_0x0110:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onDoubleTap(android.view.MotionEvent):boolean");
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
        private TextPaint textPaint;

        public QualityChooseView(Context context) {
            super(context);
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(14.0f));
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
            this.actionBar.setSubtitle(LocaleController.getString("SoundMuted", NUM));
            this.muteItem.setImageResource(NUM);
            if (this.compressItem.getTag() != null) {
                this.compressItem.setAlpha(0.5f);
                this.compressItem.setEnabled(false);
            }
            this.muteItem.setContentDescription(LocaleController.getString("NoSound", NUM));
            return;
        }
        this.actionBar.setSubtitle(this.currentSubtitle);
        this.muteItem.setImageResource(NUM);
        this.muteItem.setContentDescription(LocaleController.getString("Sound", NUM));
        if (this.compressItem.getTag() != null) {
            this.compressItem.setAlpha(1.0f);
            this.compressItem.setEnabled(true);
        }
    }

    /* access modifiers changed from: private */
    public void didChangedCompressionLevel(boolean z) {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt(String.format("compress_video_%d", new Object[]{Integer.valueOf(this.compressionsCount)}), this.selectedCompression);
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
            if (this.compressionsCount == 0) {
                actionBar2.setSubtitle((CharSequence) null);
                return;
            }
            int i3 = this.selectedCompression;
            if (i3 < 2) {
                this.compressItem.setImageResource(NUM);
            } else if (i3 == 2) {
                this.compressItem.setImageResource(NUM);
            } else if (i3 == 3) {
                this.compressItem.setImageResource(NUM);
            }
            this.itemsLayout.requestLayout();
            this.estimatedDuration = (long) Math.ceil((double) ((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration));
            if (this.compressItem.getTag() == null) {
                int i4 = this.rotationValue;
                i2 = (i4 == 90 || i4 == 270) ? this.originalHeight : this.originalWidth;
                int i5 = this.rotationValue;
                i = (i5 == 90 || i5 == 270) ? this.originalWidth : this.originalHeight;
                this.estimatedSize = (int) (((float) this.originalSize) * (((float) this.estimatedDuration) / this.videoDuration));
            } else {
                int i6 = this.rotationValue;
                i2 = (i6 == 90 || i6 == 270) ? this.resultHeight : this.resultWidth;
                int i7 = this.rotationValue;
                i = (i7 == 90 || i7 == 270) ? this.resultWidth : this.resultHeight;
                int i8 = (int) (((float) (this.audioFramesSize + this.videoFramesSize)) * (((float) this.estimatedDuration) / this.videoDuration));
                this.estimatedSize = i8;
                this.estimatedSize = i8 + ((i8 / 32768) * 16);
            }
            this.videoCutStart = this.videoTimelineView.getLeftProgress();
            this.videoCutEnd = this.videoTimelineView.getRightProgress();
            float f = this.videoCutStart;
            if (f == 0.0f) {
                this.startTime = -1;
            } else {
                this.startTime = ((long) (f * this.videoDuration)) * 1000;
            }
            float f2 = this.videoCutEnd;
            if (f2 == 1.0f) {
                this.endTime = -1;
            } else {
                this.endTime = ((long) (f2 * this.videoDuration)) * 1000;
            }
            String format = String.format("%s, %s", new Object[]{String.format("%dx%d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i)}), String.format("%s, ~%s", new Object[]{AndroidUtilities.formatShortDuration((int) (this.estimatedDuration / 1000)), AndroidUtilities.formatFileSize((long) this.estimatedSize)})});
            this.currentSubtitle = format;
            ActionBar actionBar3 = this.actionBar;
            if (this.muteVideo) {
                format = LocaleController.getString("SoundMuted", NUM);
            }
            actionBar3.setSubtitle(format);
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
                preparePlayer(this.currentPlayingVideoFile, false, false, this.editState.savedFilterState);
                this.videoPlayer.seekTo((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration));
            }
        } else if (this.resultHeight == this.originalHeight && this.resultWidth == this.originalWidth) {
            this.tryStartRequestPreviewOnFinish = false;
            PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
            photoProgressViewArr[0].setProgress(0.0f, photoProgressViewArr[0].backgroundState == 0 || this.photoProgressViews[0].previousBackgroundState == 0);
            this.photoProgressViews[0].setBackgroundState(3, false);
            if (!z2) {
                preparePlayer(this.currentPlayingVideoFile, false, false, this.editState.savedFilterState);
                this.videoPlayer.seekTo((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration));
            } else {
                this.loadInitialVideo = true;
            }
        } else {
            releasePlayer(false);
            if (this.videoPreviewMessageObject == null) {
                TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
                tLRPC$TL_message.id = 0;
                tLRPC$TL_message.message = "";
                tLRPC$TL_message.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message.action = new TLRPC$TL_messageActionEmpty();
                tLRPC$TL_message.dialog_id = this.currentDialogId;
                MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message, false);
                this.videoPreviewMessageObject = messageObject;
                messageObject.messageOwner.attachPath = new File(FileLoader.getDirectory(4), "video_preview.mp4").getAbsolutePath();
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
                    PhotoViewer.this.qualityChooseViewAnimation.setInterpolator(AndroidUtilities.decelerateInterpolator);
                    PhotoViewer.this.qualityChooseViewAnimation.start();
                }
            }

            public void onAnimationCancel(Animator animator) {
                AnimatorSet unused = PhotoViewer.this.qualityChooseViewAnimation = null;
            }
        });
        this.qualityChooseViewAnimation.setDuration(200);
        this.qualityChooseViewAnimation.setInterpolator(AndroidUtilities.accelerateInterpolator);
        this.qualityChooseViewAnimation.start();
        float f2 = 0.25f;
        if (this.cameraItem.getVisibility() == 0) {
            this.cameraItem.animate().scaleX(z ? 0.25f : 1.0f).scaleY(z ? 0.25f : 1.0f).alpha(z ? 0.0f : 1.0f).setDuration(200);
        }
        if (this.muteItem.getVisibility() == 0) {
            ViewPropertyAnimator scaleX = this.muteItem.animate().scaleX(z ? 0.25f : 1.0f);
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

    private void processOpenVideo(final String str, boolean z, float f, float f2) {
        if (this.currentLoadingVideoRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.currentLoadingVideoRunnable);
            this.currentLoadingVideoRunnable = null;
        }
        this.videoTimelineView.setVideoPath(str, f, f2);
        this.videoPreviewMessageObject = null;
        this.muteVideo = z;
        this.compressionsCount = -1;
        this.rotationValue = 0;
        this.videoFramerate = 25;
        this.originalSize = new File(str).length();
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        AnonymousClass58 r6 = new Runnable() {
            public void run() {
                if (PhotoViewer.this.currentLoadingVideoRunnable == this) {
                    int videoBitrate = MediaController.getVideoBitrate(str);
                    int[] iArr = new int[11];
                    AnimatedFileDrawable.getVideoInfo(str, iArr);
                    if (PhotoViewer.this.currentLoadingVideoRunnable == this) {
                        Runnable unused = PhotoViewer.this.currentLoadingVideoRunnable = null;
                        AndroidUtilities.runOnUIThread(new Runnable(iArr, videoBitrate) {
                            public final /* synthetic */ int[] f$1;
                            public final /* synthetic */ int f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                PhotoViewer.AnonymousClass58.this.lambda$run$0$PhotoViewer$58(this.f$1, this.f$2);
                            }
                        });
                    }
                }
            }

            public /* synthetic */ void lambda$run$0$PhotoViewer$58(int[] iArr, int i) {
                if (PhotoViewer.this.parentActivity != null) {
                    boolean unused = PhotoViewer.this.videoConvertSupported = iArr[0] != 0 && (!(iArr[10] != 0) || iArr[9] != 0);
                    long unused2 = PhotoViewer.this.audioFramesSize = (long) iArr[5];
                    float unused3 = PhotoViewer.this.videoDuration = (float) iArr[4];
                    PhotoViewer photoViewer = PhotoViewer.this;
                    int unused4 = photoViewer.bitrate = i;
                    int unused5 = photoViewer.originalBitrate = i;
                    int unused6 = PhotoViewer.this.videoFramerate = iArr[7];
                    PhotoViewer photoViewer2 = PhotoViewer.this;
                    long unused7 = photoViewer2.videoFramesSize = (long) ((((float) (photoViewer2.bitrate / 8)) * PhotoViewer.this.videoDuration) / 1000.0f);
                    if (PhotoViewer.this.videoConvertSupported) {
                        int unused8 = PhotoViewer.this.rotationValue = iArr[8];
                        PhotoViewer photoViewer3 = PhotoViewer.this;
                        int i2 = iArr[1];
                        int unused9 = photoViewer3.originalWidth = i2;
                        int unused10 = photoViewer3.resultWidth = i2;
                        PhotoViewer photoViewer4 = PhotoViewer.this;
                        int i3 = iArr[2];
                        int unused11 = photoViewer4.originalHeight = i3;
                        int unused12 = photoViewer4.resultHeight = i3;
                        PhotoViewer photoViewer5 = PhotoViewer.this;
                        photoViewer5.updateCompressionsCount(photoViewer5.originalWidth, PhotoViewer.this.originalHeight);
                        PhotoViewer photoViewer6 = PhotoViewer.this;
                        int unused13 = photoViewer6.selectedCompression = photoViewer6.selectCompression();
                        PhotoViewer.this.updateWidthHeightBitrateForCompression();
                        if (PhotoViewer.this.selectedCompression > PhotoViewer.this.compressionsCount - 1) {
                            PhotoViewer photoViewer7 = PhotoViewer.this;
                            int unused14 = photoViewer7.selectedCompression = photoViewer7.compressionsCount - 1;
                        }
                        PhotoViewer photoViewer8 = PhotoViewer.this;
                        photoViewer8.setCompressItemEnabled(photoViewer8.compressionsCount > 1, true);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("compressionsCount = " + PhotoViewer.this.compressionsCount + " w = " + PhotoViewer.this.originalWidth + " h = " + PhotoViewer.this.originalHeight);
                        }
                        if (Build.VERSION.SDK_INT < 18 && PhotoViewer.this.compressItem.getTag() != null) {
                            boolean unused15 = PhotoViewer.this.videoConvertSupported = false;
                            PhotoViewer.this.setCompressItemEnabled(false, true);
                        }
                        PhotoViewer.this.qualityChooseView.invalidate();
                    } else {
                        PhotoViewer.this.setCompressItemEnabled(false, true);
                        int unused16 = PhotoViewer.this.compressionsCount = 0;
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
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        int i = this.compressionsCount;
        while (i < 5) {
            int i2 = globalMainSettings.getInt(String.format(Locale.US, "compress_video_%d", new Object[]{Integer.valueOf(i)}), -1);
            if (i2 >= 0) {
                return i2;
            }
            i++;
        }
        return Math.round(((float) DownloadController.getInstance(this.currentAccount).getMaxVideoBitrate()) / (100.0f / ((float) i))) - 1;
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
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.compressItemAnimation = animatorSet2;
                    Animator[] animatorArr = new Animator[4];
                    ImageView imageView2 = this.compressItem;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = z ? 1.0f : 0.5f;
                    animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                    ImageView imageView3 = this.paintItem;
                    Property property2 = View.ALPHA;
                    float[] fArr2 = new float[1];
                    fArr2[0] = this.videoConvertSupported ? 1.0f : 0.5f;
                    animatorArr[1] = ObjectAnimator.ofFloat(imageView3, property2, fArr2);
                    ImageView imageView4 = this.tuneItem;
                    Property property3 = View.ALPHA;
                    float[] fArr3 = new float[1];
                    fArr3[0] = this.videoConvertSupported ? 1.0f : 0.5f;
                    animatorArr[2] = ObjectAnimator.ofFloat(imageView4, property3, fArr3);
                    ImageView imageView5 = this.cropItem;
                    Property property4 = View.ALPHA;
                    float[] fArr4 = new float[1];
                    if (!this.videoConvertSupported) {
                        f = 0.5f;
                    }
                    fArr4[0] = f;
                    animatorArr[3] = ObjectAnimator.ofFloat(imageView5, property4, fArr4);
                    animatorSet2.playTogether(animatorArr);
                    this.compressItemAnimation.setDuration(180);
                    this.compressItemAnimation.setInterpolator(decelerateInterpolator);
                    this.compressItemAnimation.start();
                    return;
                }
                ImageView imageView6 = this.compressItem;
                if (!z) {
                    f = 0.5f;
                }
                imageView6.setAlpha(f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateAccessibilityOverlayVisibility() {
        if (this.playButtonAccessibilityOverlay != null) {
            int access$15600 = this.photoProgressViews[0].backgroundState;
            if (!this.photoProgressViews[0].isVisible() || !(access$15600 == 3 || access$15600 == 4)) {
                this.playButtonAccessibilityOverlay.setVisibility(4);
                return;
            }
            if (access$15600 == 3) {
                this.playButtonAccessibilityOverlay.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
            } else {
                this.playButtonAccessibilityOverlay.setContentDescription(LocaleController.getString("AccActionPause", NUM));
            }
            this.playButtonAccessibilityOverlay.setVisibility(0);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
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
            PhotoPickerPhotoCell photoPickerPhotoCell = new PhotoPickerPhotoCell(this.mContext);
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
                    if (photoChecked == 0) {
                        PhotoViewer.this.selectedPhotosAdapter.notifyItemChanged(0);
                    }
                }
                PhotoViewer.this.updateSelectedCount();
                return;
            }
            int photoUnchecked = PhotoViewer.this.placeProvider.setPhotoUnchecked(tag);
            if (photoUnchecked >= 0) {
                PhotoViewer.this.selectedPhotosAdapter.notifyItemRemoved(photoUnchecked);
                if (photoUnchecked == 0) {
                    PhotoViewer.this.selectedPhotosAdapter.notifyItemChanged(0);
                }
                PhotoViewer.this.updateSelectedCount();
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            PhotoPickerPhotoCell photoPickerPhotoCell = (PhotoPickerPhotoCell) viewHolder.itemView;
            photoPickerPhotoCell.setItemWidth(AndroidUtilities.dp(85.0f), i != 0 ? AndroidUtilities.dp(6.0f) : 0);
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
