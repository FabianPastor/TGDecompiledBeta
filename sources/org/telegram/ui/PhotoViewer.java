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
import org.telegram.ui.Components.FadingTextViewLayout;
import org.telegram.ui.Components.FilterGLThread;
import org.telegram.ui.Components.FilterShaders;
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
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanUserMentionPhotoViewer;
import org.telegram.ui.Components.VideoEditTextureView;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayerSeekBar;
import org.telegram.ui.Components.VideoSeekPreviewImage;
import org.telegram.ui.Components.VideoTimelinePlayView;
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
    private long currentAverageDuration;
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
    private ArrayList<VideoEditedInfo.MediaEntity> currentMediaEntities;
    /* access modifiers changed from: private */
    public MessageObject currentMessageObject;
    private String currentPaintPath;
    /* access modifiers changed from: private */
    public int currentPanTranslationY;
    private String currentPathObject;
    /* access modifiers changed from: private */
    public PlaceProviderObject currentPlaceObject;
    private Uri currentPlayingVideoFile;
    private MediaController.SavedFilterState currentSavedFilterState;
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
            PipVideoView access$2300 = photoViewer.pipVideoView;
            Activity access$900 = PhotoViewer.this.parentActivity;
            PhotoViewer photoViewer2 = PhotoViewer.this;
            TextureView unused8 = photoViewer.changedTextureView = access$2300.show(access$900, photoViewer2, photoViewer2.aspectRatioFrameLayout.getAspectRatio(), PhotoViewer.this.aspectRatioFrameLayout.getVideoRotation());
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
                            private final /* synthetic */ float f$1;

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
                        if (PhotoViewer.this.muteVideo || PhotoViewer.this.currentEditMode != 0) {
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
    public VideoPlayerSeekBar videoPlayerSeekbar;
    /* access modifiers changed from: private */
    public View videoPlayerSeekbarView;
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

    static /* synthetic */ boolean lambda$null$17(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ boolean lambda$null$18(View view, MotionEvent motionEvent) {
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
                        if (url.startsWith("video")) {
                            if (!(PhotoViewer.this.videoPlayer == null || PhotoViewer.this.currentMessageObject == null)) {
                                int intValue = Utilities.parseInt(url).intValue();
                                if (PhotoViewer.this.videoPlayer.getDuration() == -9223372036854775807L) {
                                    float unused = PhotoViewer.this.seekToProgressPending = ((float) intValue) / ((float) PhotoViewer.this.currentMessageObject.getDuration());
                                } else {
                                    PhotoViewer.this.videoPlayer.seekTo(((long) intValue) * 1000);
                                }
                            }
                        } else if (url.startsWith("#") && (PhotoViewer.this.parentActivity instanceof LaunchActivity)) {
                            DialogsActivity dialogsActivity = new DialogsActivity((Bundle) null);
                            dialogsActivity.setSearchString(url);
                            ((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(dialogsActivity, false, true);
                            PhotoViewer.this.closePhoto(false, false);
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
        private float[] animAlphas = new float[3];
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
                this.parent.invalidate();
            }
        }

        public void setAlpha(float f) {
            float[] fArr = this.alphas;
            this.animAlphas[0] = f;
            fArr[0] = f;
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

        public boolean isVisible() {
            int i = 0;
            while (true) {
                float[] fArr = this.alphas;
                if (i >= fArr.length) {
                    return true;
                }
                if (fArr[i] != 1.0f) {
                    return false;
                }
                i++;
            }
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
                    PhotoViewer.this.captionTextViewSwitcher.apply($$Lambda$PhotoViewer$FrameLayoutDrawer$atk5h8fL3t6GtOkDI7x8j4Ibcy8.INSTANCE);
                } else {
                    PhotoViewer.this.captionTextViewSwitcher.apply($$Lambda$PhotoViewer$FrameLayoutDrawer$Dqv0Erl_CdxxgDxSSJpMAyTCLASSNAMEc.INSTANCE);
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
            for (int i6 = 0; i6 < childCount; i6++) {
                View childAt = getChildAt(i6);
                if (!(childAt.getVisibility() == 8 || childAt == PhotoViewer.this.captionEditText || childAt == PhotoViewer.this.groupedPhotosListView)) {
                    if (childAt == PhotoViewer.this.aspectRatioFrameLayout) {
                        childAt.measure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0), NUM));
                    } else {
                        int i7 = i;
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

        static /* synthetic */ void lambda$onMeasure$0(TextView textView) {
            Point point = AndroidUtilities.displaySize;
            textView.setMaxLines(point.x > point.y ? 5 : 10);
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
                PhotoViewer.this.videoPlayerSeekbar.setTransitionProgress(1.0f - f);
            } else {
                PhotoViewer.this.videoPlayerSeekbarView.setAlpha(f);
            }
        }

        public void setSeekBarTransitionEnabled(boolean z) {
            if (this.seekBarTransitionEnabled != z) {
                this.seekBarTransitionEnabled = z;
                if (z) {
                    PhotoViewer.this.videoPlayerSeekbarView.setAlpha(1.0f);
                } else {
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
                int y = PhotoViewer.this.photoProgressViews[0].getY() + PhotoViewer.this.photoProgressViews[0].size;
                PhotoViewer.this.photoProgressViews[0].setIndexedAlpha(2, (((PhotoViewer.this.captionContainer.getTop() + ((int) PhotoViewer.this.captionTextViewSwitcher.getTranslationY())) - getScrollY()) + ((Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight())) - AndroidUtilities.dp(12.0f) > y ? 1.0f : 0.0f, true);
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
    /* JADX WARNING: Removed duplicated region for block: B:377:? A[RETURN, SYNTHETIC] */
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
            if (r2 >= r4) goto L_0x0687
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
            goto L_0x0687
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
            if (r2 >= r4) goto L_0x0687
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
            if (r2 != 0) goto L_0x0687
            org.telegram.ui.Components.VideoPlayer r1 = r0.videoPlayer
            if (r1 == 0) goto L_0x0687
            r0.currentVideoFinishedLoading = r6
            goto L_0x0687
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
            if (r2 >= r4) goto L_0x0687
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
            if (r5 != r2) goto L_0x0687
            int r2 = r0.classGuid
            if (r2 != r1) goto L_0x0687
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
            if (r1 == 0) goto L_0x0687
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r3 = r0.avatarsDialogId
            r4 = 80
            r5 = 0
            r7 = 0
            int r8 = r0.classGuid
            r2.loadDialogPhotos(r3, r4, r5, r7, r8)
            goto L_0x0687
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
            if (r5 != 0) goto L_0x0687
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
            goto L_0x0687
        L_0x033c:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0687
            boolean r1 = r0.opennedFromMedia
            r2 = 2131626010(0x7f0e081a, float:1.8879244E38)
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
            goto L_0x0687
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
            goto L_0x0687
        L_0x03a0:
            int r2 = org.telegram.messenger.NotificationCenter.mediaDidLoad
            if (r1 != r2) goto L_0x05ed
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
            if (r10 != 0) goto L_0x0687
        L_0x03c1:
            int r5 = r0.classGuid
            if (r3 != r5) goto L_0x0687
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
            if (r2 == 0) goto L_0x0587
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
            goto L_0x0687
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
            if (r2 != 0) goto L_0x0687
            r2 = 1
            r0.loadingMoreImages = r2
            boolean r2 = r0.opennedFromMedia
            if (r2 == 0) goto L_0x056e
            int r2 = r0.currentAccount
            org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r2)
            if (r1 != 0) goto L_0x0560
            long r3 = r0.currentDialogId
            goto L_0x0562
        L_0x0560:
            long r3 = r0.mergeDialogId
        L_0x0562:
            r5 = 40
            int r7 = r0.sharedMediaType
            r8 = 1
            int r9 = r0.classGuid
            r2.loadMedia(r3, r5, r6, r7, r8, r9)
            goto L_0x0687
        L_0x056e:
            int r2 = r0.currentAccount
            org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r2)
            if (r1 != 0) goto L_0x0579
            long r3 = r0.currentDialogId
            goto L_0x057b
        L_0x0579:
            long r3 = r0.mergeDialogId
        L_0x057b:
            r5 = 40
            int r7 = r0.sharedMediaType
            r8 = 1
            int r9 = r0.classGuid
            r2.loadMedia(r3, r5, r6, r7, r8, r9)
            goto L_0x0687
        L_0x0587:
            java.util.Iterator r2 = r3.iterator()
            r3 = 0
        L_0x058c:
            boolean r5 = r2.hasNext()
            if (r5 == 0) goto L_0x05c3
            java.lang.Object r5 = r2.next()
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r6 = r0.imagesByIds
            r6 = r6[r1]
            int r8 = r5.getId()
            int r6 = r6.indexOfKey(r8)
            if (r6 >= 0) goto L_0x058c
            int r3 = r3 + 1
            boolean r6 = r0.opennedFromMedia
            if (r6 == 0) goto L_0x05b2
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r0.imagesArr
            r6.add(r5)
            goto L_0x05b7
        L_0x05b2:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r0.imagesArr
            r6.add(r7, r5)
        L_0x05b7:
            android.util.SparseArray<org.telegram.messenger.MessageObject>[] r6 = r0.imagesByIds
            r6 = r6[r1]
            int r8 = r5.getId()
            r6.put(r8, r5)
            goto L_0x058c
        L_0x05c3:
            boolean r1 = r0.opennedFromMedia
            if (r1 == 0) goto L_0x05d5
            if (r3 != 0) goto L_0x0687
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r1 = r1.size()
            r0.totalImagesCount = r1
            r0.totalImagesCountMerge = r7
            goto L_0x0687
        L_0x05d5:
            if (r3 == 0) goto L_0x05e1
            int r1 = r0.currentIndex
            r0.currentIndex = r4
            int r1 = r1 + r3
            r0.setImageIndex(r1)
            goto L_0x0687
        L_0x05e1:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r1 = r1.size()
            r0.totalImagesCount = r1
            r0.totalImagesCountMerge = r7
            goto L_0x0687
        L_0x05ed:
            int r2 = org.telegram.messenger.NotificationCenter.emojiDidLoad
            if (r1 != r2) goto L_0x05fc
            org.telegram.ui.PhotoViewer$CaptionTextViewSwitcher r1 = r0.captionTextViewSwitcher
            if (r1 == 0) goto L_0x0687
            org.telegram.ui.-$$Lambda$q2bRHwD1z0FjUfGv8w92lVdonEY r2 = org.telegram.ui.$$Lambda$q2bRHwD1z0FjUfGv8w92lVdonEY.INSTANCE
            r1.apply(r2)
            goto L_0x0687
        L_0x05fc:
            int r2 = org.telegram.messenger.NotificationCenter.filePreparingFailed
            if (r1 != r2) goto L_0x0639
            r1 = r24[r7]
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            boolean r2 = r0.loadInitialVideo
            if (r2 == 0) goto L_0x0617
            r0.loadInitialVideo = r7
            org.telegram.ui.Components.RadialProgressView r1 = r0.progressView
            r1.setVisibility(r3)
            android.net.Uri r1 = r0.currentPlayingVideoFile
            org.telegram.messenger.MediaController$SavedFilterState r2 = r0.currentSavedFilterState
            r0.preparePlayer(r1, r7, r7, r2)
            goto L_0x0687
        L_0x0617:
            boolean r2 = r0.tryStartRequestPreviewOnFinish
            if (r2 == 0) goto L_0x062d
            r0.releasePlayer(r7)
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r2 = r0.videoPreviewMessageObject
            r3 = 1
            boolean r1 = r1.scheduleVideoConvert(r2, r3)
            r1 = r1 ^ r3
            r0.tryStartRequestPreviewOnFinish = r1
            goto L_0x0687
        L_0x062d:
            org.telegram.messenger.MessageObject r2 = r0.videoPreviewMessageObject
            if (r1 != r2) goto L_0x0687
            r0.requestingPreview = r7
            org.telegram.ui.Components.RadialProgressView r1 = r0.progressView
            r1.setVisibility(r3)
            goto L_0x0687
        L_0x0639:
            int r2 = org.telegram.messenger.NotificationCenter.fileNewChunkAvailable
            if (r1 != r2) goto L_0x0687
            r1 = r24[r7]
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            org.telegram.messenger.MessageObject r2 = r0.videoPreviewMessageObject
            if (r1 != r2) goto L_0x0687
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
            if (r3 == 0) goto L_0x0687
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
            org.telegram.messenger.MediaController$SavedFilterState r3 = r0.currentSavedFilterState
            r0.preparePlayer(r2, r7, r1, r3)
        L_0x0687:
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
            r3 = 2131626774(0x7f0e0b16, float:1.8880794E38)
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
        int i;
        Activity activity2 = activity;
        int i2 = UserConfig.selectedAccount;
        this.currentAccount = i2;
        this.centerImage.setCurrentAccount(i2);
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
                    if (PhotoViewer.this.isCurrentVideo && PhotoViewer.this.videoPlayer != null && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == 0 && (keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25)) {
                        PhotoViewer.this.videoPlayer.setVolume(1.0f);
                    }
                    return super.dispatchKeyEvent(keyEvent);
                }

                /* JADX WARNING: Code restructure failed: missing block: B:13:0x0020, code lost:
                    if (r0 != 6) goto L_0x0038;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public boolean dispatchTouchEvent(android.view.MotionEvent r4) {
                    /*
                        r3 = this;
                        org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                        boolean r0 = r0.videoPlayerControlVisible
                        if (r0 == 0) goto L_0x0038
                        org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                        boolean r0 = r0.isPlaying
                        if (r0 == 0) goto L_0x0038
                        int r0 = r4.getActionMasked()
                        if (r0 == 0) goto L_0x002f
                        r1 = 1
                        if (r0 == r1) goto L_0x0023
                        r1 = 3
                        if (r0 == r1) goto L_0x0023
                        r1 = 5
                        if (r0 == r1) goto L_0x002f
                        r1 = 6
                        if (r0 == r1) goto L_0x0023
                        goto L_0x0038
                    L_0x0023:
                        org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                        java.lang.Runnable r0 = r0.hideActionBarRunnable
                        r1 = 3000(0xbb8, double:1.482E-320)
                        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
                        goto L_0x0038
                    L_0x002f:
                        org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                        java.lang.Runnable r0 = r0.hideActionBarRunnable
                        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
                    L_0x0038:
                        boolean r4 = super.dispatchTouchEvent(r4)
                        return r4
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
                        return PhotoViewer.this.lambda$setParentActivity$1$PhotoViewer(view, windowInsets);
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
                            int access$10000 = (int) PhotoViewer.this.currentDialogId;
                            int access$100002 = (int) (PhotoViewer.this.currentDialogId >> 32);
                            if (access$10000 == 0) {
                                bundle2.putInt("enc_id", access$100002);
                            } else if (access$10000 > 0) {
                                bundle2.putInt("user_id", access$10000);
                            } else if (access$10000 < 0) {
                                TLRPC$Chat chat = MessagesController.getInstance(PhotoViewer.this.currentAccount).getChat(Integer.valueOf(-access$10000));
                                if (!(chat == null || chat.migrated_to == null)) {
                                    bundle2.putInt("migrated_to", access$10000);
                                    access$10000 = -chat.migrated_to.channel_id;
                                }
                                bundle2.putInt("chat_id", -access$10000);
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
                                            private final /* synthetic */ boolean[] f$0;

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
                                private final /* synthetic */ boolean[] f$1;

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
                    if (r2.location.volume_id == org.telegram.ui.PhotoViewer.access$11500(r11.this$0).location.volume_id) goto L_0x017f;
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
                    TLRPC$FileLocation access$11000 = photoViewer.getFileLocation(photoViewer.currentFileLocation);
                    if (PhotoViewer.this.avatarsDialogId == 0 && !PhotoViewer.this.isEvent) {
                        z = false;
                    }
                    return FileLoader.getPathToAttach(access$11000, z).exists();
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
                        AndroidUtilities.runOnUIThread(PhotoViewer.this.hideActionBarRunnable, 3000);
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
            CaptionTextViewSwitcher captionTextViewSwitcher2 = new CaptionTextViewSwitcher(this.containerView.getContext());
            this.captionTextViewSwitcher = captionTextViewSwitcher2;
            captionTextViewSwitcher2.setFactory(new ViewSwitcher.ViewFactory() {
                public final View makeView() {
                    return PhotoViewer.this.createCaptionTextView();
                }
            });
            this.captionTextViewSwitcher.setVisibility(4);
            for (int i3 = 0; i3 < 3; i3++) {
                this.photoProgressViews[i3] = new PhotoProgressView(this.containerView);
                this.photoProgressViews[i3].setBackgroundState(0, false);
            }
            AnonymousClass13 r23 = new RadialProgressView(this.actvityContext) {
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
                    PhotoViewer.this.lambda$setParentActivity$2$PhotoViewer(view);
                }
            });
            this.shareButton.setContentDescription(LocaleController.getString("ShareFile", NUM));
            AnonymousClass14 r24 = new FadingTextViewLayout(this, this.containerView.getContext()) {
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
            AnonymousClass15 r25 = new FadingTextViewLayout(this, this.containerView.getContext(), true) {
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
                        if (!LocaleController.isRTL || (staticCharsCount2 = getStaticCharsCount()) <= 0 || (charSequence.length() == staticCharsCount2 && getText().length() == staticCharsCount2)) {
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
                    PhotoViewer.this.lambda$setParentActivity$3$PhotoViewer(view);
                }
            });
            this.qualityPicker.doneButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$4$PhotoViewer(view);
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
            AnonymousClass17 r26 = new FrameLayout(this.actvityContext) {
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
            AnonymousClass19 r27 = new ImageView(this.parentActivity) {
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
                    PhotoViewer.this.lambda$setParentActivity$5$PhotoViewer(view);
                }
            });
            this.pickerViewSendButton.setOnLongClickListener(new View.OnLongClickListener() {
                public final boolean onLongClick(View view) {
                    return PhotoViewer.this.lambda$setParentActivity$9$PhotoViewer(view);
                }
            });
            LinearLayout linearLayout = new LinearLayout(this.parentActivity);
            linearLayout.setOrientation(0);
            this.pickerView.addView(linearLayout, LayoutHelper.createFrame(-2, 48.0f, 81, 0.0f, 0.0f, 34.0f, 0.0f));
            ImageView imageView2 = new ImageView(this.parentActivity);
            this.cropItem = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.cropItem.setImageResource(NUM);
            this.cropItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            linearLayout.addView(this.cropItem, LayoutHelper.createLinear(70, 48));
            this.cropItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$10$PhotoViewer(view);
                }
            });
            this.cropItem.setContentDescription(LocaleController.getString("CropImage", NUM));
            ImageView imageView3 = new ImageView(this.parentActivity);
            this.rotateItem = imageView3;
            imageView3.setScaleType(ImageView.ScaleType.CENTER);
            this.rotateItem.setImageResource(NUM);
            this.rotateItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            linearLayout.addView(this.rotateItem, LayoutHelper.createLinear(70, 48));
            this.rotateItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$11$PhotoViewer(view);
                }
            });
            this.rotateItem.setContentDescription(LocaleController.getString("AccDescrRotate", NUM));
            ImageView imageView4 = new ImageView(this.parentActivity);
            this.compressItem = imageView4;
            imageView4.setTag(1);
            this.compressItem.setScaleType(ImageView.ScaleType.CENTER);
            this.compressItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            int i4 = MessagesController.getGlobalMainSettings().getInt("compress_video2", selectCompression());
            this.selectedCompression = i4;
            if (i4 <= 1) {
                this.compressItem.setImageResource(NUM);
                i = 48;
            } else {
                if (i4 == 2) {
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
            ImageView imageView5 = new ImageView(this.parentActivity);
            this.paintItem = imageView5;
            imageView5.setScaleType(ImageView.ScaleType.CENTER);
            this.paintItem.setImageResource(NUM);
            this.paintItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            linearLayout.addView(this.paintItem, LayoutHelper.createLinear(70, 48));
            this.paintItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$13$PhotoViewer(view);
                }
            });
            this.paintItem.setContentDescription(LocaleController.getString("AccDescrPhotoEditor", NUM));
            ImageView imageView6 = new ImageView(this.parentActivity);
            this.muteItem = imageView6;
            imageView6.setScaleType(ImageView.ScaleType.CENTER);
            this.muteItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.containerView.addView(this.muteItem, LayoutHelper.createFrame(48, 48.0f, 83, 16.0f, 0.0f, 0.0f, 0.0f));
            this.muteItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$14$PhotoViewer(view);
                }
            });
            ImageView imageView7 = new ImageView(this.parentActivity);
            this.cameraItem = imageView7;
            imageView7.setScaleType(ImageView.ScaleType.CENTER);
            this.cameraItem.setImageResource(NUM);
            this.cameraItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.cameraItem.setContentDescription(LocaleController.getString("AccDescrTakeMorePics", NUM));
            this.containerView.addView(this.cameraItem, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 16.0f, 0.0f));
            this.cameraItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$15$PhotoViewer(view);
                }
            });
            ImageView imageView8 = new ImageView(this.parentActivity);
            this.tuneItem = imageView8;
            imageView8.setScaleType(ImageView.ScaleType.CENTER);
            this.tuneItem.setImageResource(NUM);
            this.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            linearLayout.addView(this.tuneItem, LayoutHelper.createLinear(70, 48));
            this.tuneItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$16$PhotoViewer(view);
                }
            });
            this.tuneItem.setContentDescription(LocaleController.getString("AccDescrPhotoAdjust", NUM));
            ImageView imageView9 = new ImageView(this.parentActivity);
            this.timeItem = imageView9;
            imageView9.setScaleType(ImageView.ScaleType.CENTER);
            this.timeItem.setImageResource(NUM);
            this.timeItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
            this.timeItem.setContentDescription(LocaleController.getString("SetTimer", NUM));
            linearLayout.addView(this.timeItem, LayoutHelper.createLinear(70, 48));
            this.timeItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$22$PhotoViewer(view);
                }
            });
            PickerBottomLayoutViewer pickerBottomLayoutViewer2 = new PickerBottomLayoutViewer(this.actvityContext);
            this.editorDoneLayout = pickerBottomLayoutViewer2;
            pickerBottomLayoutViewer2.setBackgroundColor(NUM);
            this.editorDoneLayout.updateSelectedCount(0, false);
            this.editorDoneLayout.setVisibility(8);
            this.containerView.addView(this.editorDoneLayout, LayoutHelper.createFrame(-1, 48, 83));
            this.editorDoneLayout.cancelButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$23$PhotoViewer(view);
                }
            });
            this.editorDoneLayout.doneButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$setParentActivity$24$PhotoViewer(view);
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
                    PhotoViewer.this.lambda$setParentActivity$25$PhotoViewer(view);
                }
            });
            GestureDetector2 gestureDetector2 = new GestureDetector2(this.containerView.getContext(), this);
            this.gestureDetector = gestureDetector2;
            gestureDetector2.setIsLongpressEnabled(false);
            setDoubleTapEnabled(true);
            $$Lambda$PhotoViewer$8BZxkvQLRSzDRLLi99Q_qJA1OU r1 = new ImageReceiver.ImageReceiverDelegate() {
                public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                    PhotoViewer.this.lambda$setParentActivity$26$PhotoViewer(imageReceiver, z, z2, z3);
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
            AnonymousClass21 r28 = new CheckBox(this.containerView.getContext(), NUM) {
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
                }
            };
            this.checkImageView = r28;
            r28.setDrawBackground(true);
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
                    PhotoViewer.this.lambda$setParentActivity$27$PhotoViewer(view);
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
                    PhotoViewer.this.lambda$setParentActivity$28$PhotoViewer(view);
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
                    PhotoViewer.this.lambda$setParentActivity$29$PhotoViewer(view, i);
                }
            });
            AnonymousClass23 r12 = new PhotoViewerCaptionEnterView(this.actvityContext, this.containerView, this.windowView) {
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
            AnonymousClass25 r13 = new RecyclerListView(this.actvityContext) {
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
            AnonymousClass26 r14 = new LinearLayoutManager(this, this.actvityContext) {
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
                    PhotoViewer.this.lambda$setParentActivity$30$PhotoViewer(view, i);
                }
            });
            this.mentionListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
                public final boolean onItemClick(View view, int i) {
                    return PhotoViewer.this.lambda$setParentActivity$32$PhotoViewer(view, i);
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

    public /* synthetic */ WindowInsets lambda$setParentActivity$1$PhotoViewer(View view, WindowInsets windowInsets) {
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

    public /* synthetic */ boolean lambda$setParentActivity$9$PhotoViewer(View view) {
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
                    return PhotoViewer.this.lambda$null$6$PhotoViewer(view, motionEvent);
                }
            });
            this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    PhotoViewer.this.lambda$null$7$PhotoViewer(keyEvent);
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
                        PhotoViewer.this.lambda$null$8$PhotoViewer(this.f$1, view);
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

    public /* synthetic */ boolean lambda$null$6$PhotoViewer(View view, MotionEvent motionEvent) {
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

    public /* synthetic */ void lambda$null$7$PhotoViewer(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$null$8$PhotoViewer(int i, View view) {
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

    public /* synthetic */ void lambda$setParentActivity$10$PhotoViewer(View view) {
        if (this.captionEditText.getTag() == null) {
            switchToEditMode(1);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$11$PhotoViewer(View view) {
        PhotoCropView photoCropView2 = this.photoCropView;
        if (photoCropView2 != null) {
            photoCropView2.rotate();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$12$PhotoViewer(Activity activity, View view) {
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

    public /* synthetic */ void lambda$setParentActivity$13$PhotoViewer(View view) {
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

    public /* synthetic */ void lambda$setParentActivity$14$PhotoViewer(View view) {
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

    public /* synthetic */ void lambda$setParentActivity$15$PhotoViewer(View view) {
        if (this.placeProvider != null && this.captionEditText.getTag() == null) {
            this.placeProvider.needAddMorePhotos();
            closePhoto(true, false);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$16$PhotoViewer(View view) {
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

    public /* synthetic */ void lambda$setParentActivity$22$PhotoViewer(View view) {
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
            textView.setOnTouchListener($$Lambda$PhotoViewer$NS34BJTSE805Ka6Cky_LZyYatpM.INSTANCE);
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
            textView2.setOnTouchListener($$Lambda$PhotoViewer$CD6gVrhnqRuwdDguMD9zXdZUmk.INSTANCE);
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
            numberPicker.setFormatter($$Lambda$PhotoViewer$wZVYCxGADb62_ysv1vUKCjCIfgw.INSTANCE);
            linearLayout.addView(numberPicker, LayoutHelper.createLinear(-1, -2));
            AnonymousClass20 r9 = new FrameLayout(this, this.parentActivity) {
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
                private final /* synthetic */ NumberPicker f$1;
                private final /* synthetic */ BottomSheet f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    PhotoViewer.this.lambda$null$20$PhotoViewer(this.f$1, this.f$2, view);
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

    static /* synthetic */ String lambda$null$19(int i) {
        if (i == 0) {
            return LocaleController.getString("ShortMessageLifetimeForever", NUM);
        }
        if (i < 1 || i >= 21) {
            return LocaleController.formatTTLString((i - 16) * 5);
        }
        return LocaleController.formatTTLString(i);
    }

    public /* synthetic */ void lambda$null$20$PhotoViewer(NumberPicker numberPicker, BottomSheet bottomSheet, View view) {
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

    public /* synthetic */ void lambda$setParentActivity$23$PhotoViewer(View view) {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$setParentActivity$24$PhotoViewer(View view) {
        if (this.currentEditMode != 1 || this.photoCropView.isReady()) {
            applyCurrentEditMode();
            switchToEditMode(0);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$25$PhotoViewer(View view) {
        this.photoCropView.reset();
    }

    public /* synthetic */ void lambda$setParentActivity$26$PhotoViewer(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        PhotoViewerProvider photoViewerProvider;
        Bitmap bitmap;
        if (imageReceiver == this.centerImage && z && !z2) {
            if (!((this.currentEditMode != 1 && this.sendPhotoType != 1) || this.photoCropView == null || (bitmap = imageReceiver.getBitmap()) == null)) {
                this.photoCropView.setBitmap(bitmap, imageReceiver.getOrientation(), this.sendPhotoType != 1, true, this.paintingOverlay);
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

    public /* synthetic */ void lambda$setParentActivity$27$PhotoViewer(View view) {
        if (this.captionEditText.getTag() == null) {
            setPhotoChecked();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$28$PhotoViewer(View view) {
        PhotoViewerProvider photoViewerProvider;
        if (this.captionEditText.getTag() == null && (photoViewerProvider = this.placeProvider) != null && photoViewerProvider.getSelectedPhotosOrder() != null && !this.placeProvider.getSelectedPhotosOrder().isEmpty()) {
            togglePhotosListView(!this.isPhotosListViewVisible, true);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$29$PhotoViewer(View view, int i) {
        this.ignoreDidSetImage = true;
        int indexOf = this.imagesArrLocals.indexOf(view.getTag());
        if (indexOf >= 0) {
            this.currentIndex = -1;
            setImageIndex(indexOf);
        }
        this.ignoreDidSetImage = false;
    }

    public /* synthetic */ void lambda$setParentActivity$30$PhotoViewer(View view, int i) {
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

    public /* synthetic */ boolean lambda$setParentActivity$32$PhotoViewer(View view, int i) {
        if (!(this.mentionsAdapter.getItem(i) instanceof String)) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("ClearSearch", NUM));
        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PhotoViewer.this.lambda$null$31$PhotoViewer(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showAlertDialog(builder);
        return true;
    }

    public /* synthetic */ void lambda$null$31$PhotoViewer(DialogInterface dialogInterface, int i) {
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
                    TLRPC$Chat currentChat = chatActivity.getCurrentChat();
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
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", NUM));
        builder.setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PhotoViewer.this.lambda$checkInlinePermissions$33$PhotoViewer(dialogInterface, i);
            }
        });
        builder.show();
        return false;
    }

    public /* synthetic */ void lambda$checkInlinePermissions$33$PhotoViewer(DialogInterface dialogInterface, int i) {
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
    public TextView createCaptionTextView() {
        AnonymousClass28 r0 = new TextView(this.actvityContext) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
            }

            public void scrollTo(int i, int i2) {
                if (getParent().getParent() == PhotoViewer.this.pickerView) {
                    super.scrollTo(i, i2);
                }
            }
        };
        r0.setMovementMethod(new LinkMovementMethodMy());
        r0.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
        r0.setLinkTextColor(-8994063);
        r0.setTextColor(-1);
        r0.setHighlightColor(NUM);
        r0.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        r0.setTextSize(1, 16.0f);
        r0.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PhotoViewer.this.lambda$createCaptionTextView$34$PhotoViewer(view);
            }
        });
        return r0;
    }

    public /* synthetic */ void lambda$createCaptionTextView$34$PhotoViewer(View view) {
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
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.textureImageView, View.SCALE_X, new float[]{width}), ObjectAnimator.ofFloat(this.textureImageView, View.SCALE_Y, new float[]{width}), ObjectAnimator.ofFloat(this.textureImageView, View.TRANSLATION_X, new float[]{pipRect.x}), ObjectAnimator.ofFloat(this.textureImageView, View.TRANSLATION_Y, new float[]{pipRect.y}), ObjectAnimator.ofFloat(this.videoTextureView, View.SCALE_X, new float[]{width}), ObjectAnimator.ofFloat(this.videoTextureView, View.SCALE_Y, new float[]{width}), ObjectAnimator.ofFloat(this.videoTextureView, View.TRANSLATION_X, new float[]{(pipRect.x - this.aspectRatioFrameLayout.getX()) + ((float) getLeftInset())}), ObjectAnimator.ofFloat(this.videoTextureView, View.TRANSLATION_Y, new float[]{pipRect.y - this.aspectRatioFrameLayout.getY()}), ObjectAnimator.ofInt(this.backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0}), ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.captionTextViewSwitcher, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.groupedPhotosListView, View.ALPHA, new float[]{0.0f})});
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
        AnonymousClass31 r0 = new View(this.containerView.getContext()) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                PhotoViewer.this.videoPlayerSeekbar.draw(canvas);
            }
        };
        this.videoPlayerSeekbarView = r0;
        this.videoPlayerControlFrameLayout.addView(r0, LayoutHelper.createFrame(-1, -1.0f));
        VideoPlayerSeekBar videoPlayerSeekBar = new VideoPlayerSeekBar(this.videoPlayerSeekbarView);
        this.videoPlayerSeekbar = videoPlayerSeekBar;
        videoPlayerSeekBar.setHorizontalPadding(AndroidUtilities.dp(2.0f));
        this.videoPlayerSeekbar.setColors(NUM, NUM, -1, -1, -1, NUM);
        this.videoPlayerSeekbar.setDelegate(new VideoPlayerSeekBar.SeekBarDelegate() {
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
        AnonymousClass33 r02 = new VideoSeekPreviewImage(this.containerView.getContext(), new VideoSeekPreviewImage.VideoSeekPreviewImageDelegate() {
            public final void onReady() {
                PhotoViewer.this.lambda$createVideoControlsInterface$35$PhotoViewer();
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
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -1.0f, 53, 0.0f, 15.0f, 12.0f, 0.0f));
    }

    public /* synthetic */ void lambda$createVideoControlsInterface$35$PhotoViewer() {
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
        int i = -1;
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
            videoEditedInfo.originalPath = this.currentPlayingVideoFile.getPath();
            int i2 = this.estimatedSize;
            videoEditedInfo.estimatedSize = i2 != 0 ? (long) i2 : 1;
            videoEditedInfo.estimatedDuration = this.estimatedDuration;
            videoEditedInfo.framerate = this.videoFramerate;
            videoEditedInfo.originalDuration = (long) (this.videoDuration * 1000.0f);
            videoEditedInfo.filterState = this.currentSavedFilterState;
            videoEditedInfo.paintPath = this.currentPaintPath;
            ArrayList<VideoEditedInfo.MediaEntity> arrayList2 = this.currentMediaEntities;
            if (arrayList2 != null && !arrayList2.isEmpty()) {
                arrayList = this.currentMediaEntities;
            }
            videoEditedInfo.mediaEntities = arrayList;
            if (this.muteVideo || !(this.compressItem.getTag() == null || (videoEditedInfo.resultWidth == this.originalWidth && videoEditedInfo.resultHeight == this.originalHeight))) {
                if (this.muteVideo) {
                    this.selectedCompression = 1;
                    updateWidthHeightBitrateForCompression();
                }
                videoEditedInfo.resultWidth = this.resultWidth;
                videoEditedInfo.resultHeight = this.resultHeight;
                if (!this.muteVideo) {
                    i = this.bitrate;
                }
                videoEditedInfo.bitrate = i;
                videoEditedInfo.muted = this.muteVideo;
            } else {
                videoEditedInfo.resultWidth = this.originalWidth;
                videoEditedInfo.resultHeight = this.originalHeight;
                if (!this.muteVideo) {
                    i = this.originalBitrate;
                }
                videoEditedInfo.bitrate = i;
                videoEditedInfo.muted = this.muteVideo;
            }
            return videoEditedInfo;
        }
        VideoEditedInfo videoEditedInfo2 = new VideoEditedInfo();
        videoEditedInfo2.startTime = 0;
        videoEditedInfo2.start = (float) 0;
        videoEditedInfo2.endTime = Math.min(3000, this.currentAverageDuration);
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
                videoEditedInfo2.filterState = this.currentSavedFilterState;
                videoEditedInfo2.paintPath = this.currentPaintPath;
                videoEditedInfo2.mediaEntities = this.currentMediaEntities;
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
        videoEditedInfo2.filterState = this.currentSavedFilterState;
        videoEditedInfo2.paintPath = this.currentPaintPath;
        videoEditedInfo2.mediaEntities = this.currentMediaEntities;
        videoEditedInfo2.isPhoto = true;
        float bitmapWidth2 = (float) this.centerImage.getBitmapWidth();
        float bitmapHeight2 = (float) this.centerImage.getBitmapHeight();
        float max2 = Math.max(bitmapWidth2 / 854.0f, bitmapHeight2 / 854.0f);
        if (max2 < 1.0f) {
            max2 = 1.0f;
        }
        int i3 = (int) (bitmapWidth2 / max2);
        videoEditedInfo2.resultWidth = i3;
        videoEditedInfo2.originalWidth = i3;
        int i4 = (int) (bitmapHeight2 / max2);
        videoEditedInfo2.resultHeight = i4;
        videoEditedInfo2.originalHeight = i4;
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
                        AndroidUtilities.runOnUIThread(this.hideActionBarRunnable, 3000);
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
                if (!this.pipItem.isEnabled()) {
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
                    this.photoProgressViews[0].setIndexedAlpha(1, 1.0f, false);
                    this.photoProgressViews[0].setBackgroundState(3, false);
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
                            if (this.currentEditMode == 0) {
                                this.videoPlayer.pause();
                            } else {
                                this.videoPlayer.play();
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
                if (this.isCurrentVideo || ((!this.playerAutoStarted || this.playerWasPlaying) && this.isActionBarVisible)) {
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
        MediaController.SavedFilterState savedFilterState2 = savedFilterState;
        if (!z4) {
            this.currentPlayingVideoFile = uri2;
            this.currentSavedFilterState = savedFilterState2;
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

                    public void onError(Exception exc) {
                        if (PhotoViewer.this.videoPlayer != null) {
                            FileLog.e((Throwable) exc);
                            if (PhotoViewer.this.menuItem.isSubItemVisible(11)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) PhotoViewer.this.parentActivity);
                                builder.setTitle(LocaleController.getString("AppName", NUM));
                                builder.setMessage(LocaleController.getString("CantPlayVideo", NUM));
                                builder.setPositiveButton(LocaleController.getString("Open", NUM), new DialogInterface.OnClickListener() {
                                    public final void onClick(DialogInterface dialogInterface, int i) {
                                        PhotoViewer.AnonymousClass35.this.lambda$onError$0$PhotoViewer$35(dialogInterface, i);
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                                PhotoViewer.this.showAlertDialog(builder);
                            }
                        }
                    }

                    public /* synthetic */ void lambda$onError$0$PhotoViewer$35(DialogInterface dialogInterface, int i) {
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
                                int[] access$13800 = PhotoViewer.this.pipPosition;
                                access$13800[1] = (int) (((float) access$13800[1]) - PhotoViewer.this.containerView.getTranslationY());
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
                createVideoTextureView(savedFilterState2);
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
                AndroidUtilities.runOnUIThread(this.hideActionBarRunnable, this.playerAutoStarted ? 3000 : 1000);
            }
            this.inPreview = z4;
            updateAccessibilityOverlayVisibility();
        }
    }

    private void createVideoTextureView(MediaController.SavedFilterState savedFilterState) {
        if (this.videoTextureView == null) {
            AnonymousClass36 r0 = new AspectRatioFrameLayout(this.parentActivity) {
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
            this.aspectRatioFrameLayout = r0;
            r0.setVisibility(4);
            this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
            if (this.imagesArrLocals.isEmpty()) {
                this.videoTextureView = new TextureView(this.parentActivity);
            } else {
                VideoEditTextureView videoEditTextureView = new VideoEditTextureView(this.parentActivity, this.videoPlayer);
                if (savedFilterState != null) {
                    videoEditTextureView.setDelegate(new VideoEditTextureView.VideoEditTextureViewDelegate() {
                        public final void onEGLThreadAvailable(FilterGLThread filterGLThread) {
                            filterGLThread.setFilterGLThreadDelegate(FilterShaders.getFilterShadersDelegate(MediaController.SavedFilterState.this));
                        }
                    });
                }
                this.videoTextureView = videoEditTextureView;
            }
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
            updateAccessibilityOverlayVisibility();
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
                    private final /* synthetic */ boolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PhotoViewer.this.lambda$setVideoPlayerControlVisible$37$PhotoViewer(this.f$1, valueAnimator);
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

    public /* synthetic */ void lambda$setVideoPlayerControlVisible$37$PhotoViewer(boolean z, ValueAnimator valueAnimator) {
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
                        PhotoViewer.this.lambda$showAlertDialog$38$PhotoViewer(dialogInterface);
                    }
                });
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    public /* synthetic */ void lambda$showAlertDialog$38$PhotoViewer(DialogInterface dialogInterface) {
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
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = this.currentMediaEntities;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                VideoEditedInfo.MediaEntity mediaEntity = this.currentMediaEntities.get(i);
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
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = this.currentMediaEntities;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                VideoEditedInfo.MediaEntity mediaEntity = this.currentMediaEntities.get(i);
                if (mediaEntity.type == 0 && (mediaEntity.subType & 1) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x003b, code lost:
        r1 = r7.sendPhotoType;
     */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x059d  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x05a7  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x05ba  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x05fd  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0604  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x0685  */
    /* JADX WARNING: Removed duplicated region for block: B:198:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyCurrentEditMode() {
        /*
            r31 = this;
            r7 = r31
            r8 = 1
            android.graphics.Bitmap[] r0 = new android.graphics.Bitmap[r8]
            java.util.ArrayList<java.lang.Object> r1 = r7.imagesArrLocals
            int r2 = r7.currentIndex
            java.lang.Object r1 = r1.get(r2)
            r9 = r1
            org.telegram.messenger.MediaController$MediaEditState r9 = (org.telegram.messenger.MediaController.MediaEditState) r9
            int r1 = r7.currentEditMode
            r10 = 3
            r11 = 2
            r12 = 0
            if (r1 == r8) goto L_0x005d
            if (r1 != 0) goto L_0x001e
            int r1 = r7.sendPhotoType
            if (r1 != r8) goto L_0x001e
            goto L_0x005d
        L_0x001e:
            int r1 = r7.currentEditMode
            if (r1 != r11) goto L_0x0033
            org.telegram.ui.Components.PhotoFilterView r1 = r7.photoFilterView
            android.graphics.Bitmap r1 = r1.getBitmap()
            org.telegram.ui.Components.PhotoFilterView r2 = r7.photoFilterView
            org.telegram.messenger.MediaController$SavedFilterState r2 = r2.getSavedFilterState()
            r6 = r1
            r5 = r2
            r1 = r12
            r4 = r1
            goto L_0x0067
        L_0x0033:
            if (r1 != r10) goto L_0x0058
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 18
            if (r1 < r2) goto L_0x0047
            int r1 = r7.sendPhotoType
            if (r1 == 0) goto L_0x0041
            if (r1 != r11) goto L_0x0047
        L_0x0041:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            goto L_0x0048
        L_0x0047:
            r1 = r12
        L_0x0048:
            org.telegram.ui.Components.PhotoPaintView r2 = r7.photoPaintView
            android.graphics.Bitmap r2 = r2.getBitmap(r1, r0)
            org.telegram.ui.Components.PhotoPaintView r3 = r7.photoPaintView
            java.util.ArrayList r3 = r3.getMasks()
            r6 = r2
            r4 = r3
            r5 = r12
            goto L_0x0067
        L_0x0058:
            r1 = r12
            r4 = r1
            r5 = r4
            r6 = r5
            goto L_0x0067
        L_0x005d:
            org.telegram.ui.Components.PhotoCropView r1 = r7.photoCropView
            android.graphics.Bitmap r1 = r1.getBitmap(r9)
            r6 = r1
            r1 = r12
            r4 = r1
            r5 = r4
        L_0x0067:
            if (r6 == 0) goto L_0x0688
            int r2 = r7.currentEditMode
            if (r2 != r10) goto L_0x0070
            android.graphics.Bitmap$CompressFormat r2 = android.graphics.Bitmap.CompressFormat.PNG
            goto L_0x0072
        L_0x0070:
            android.graphics.Bitmap$CompressFormat r2 = android.graphics.Bitmap.CompressFormat.JPEG
        L_0x0072:
            r14 = r2
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r15 = (float) r2
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r2 = (float) r2
            r17 = 80
            r18 = 0
            r19 = 101(0x65, float:1.42E-43)
            r20 = 101(0x65, float:1.42E-43)
            r13 = r6
            r16 = r2
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r13, r14, r15, r16, r17, r18, r19, r20)
            if (r2 == 0) goto L_0x0688
            java.lang.String r3 = r9.thumbPath
            if (r3 == 0) goto L_0x009e
            java.io.File r3 = new java.io.File
            java.lang.String r13 = r9.thumbPath
            r3.<init>(r13)
            r3.delete()
            r9.thumbPath = r12
        L_0x009e:
            int r3 = r7.currentEditMode
            java.lang.String r15 = "_temp.jpg"
            r14 = 4
            r13 = 0
            if (r3 != r8) goto L_0x01fc
            java.lang.String r0 = r9.filterPath
            if (r0 != 0) goto L_0x00c5
            java.lang.String r0 = r9.croppedPath
            if (r0 == 0) goto L_0x00b8
            java.io.File r0 = new java.io.File
            java.lang.String r1 = r9.croppedPath
            r0.<init>(r1)
            r0.delete()
        L_0x00b8:
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r8)
            java.lang.String r0 = r0.toString()
            r7.currentImagePath = r0
            r9.croppedPath = r0
            goto L_0x00dd
        L_0x00c5:
            if (r0 == 0) goto L_0x00d1
            java.io.File r0 = new java.io.File
            java.lang.String r1 = r9.filterPath
            r0.<init>(r1)
            r0.delete()
        L_0x00d1:
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r8)
            java.lang.String r0 = r0.toString()
            r7.currentImagePath = r0
            r9.filterPath = r0
        L_0x00dd:
            java.lang.String r0 = r9.imagePath
            if (r0 == 0) goto L_0x00eb
            java.io.File r0 = new java.io.File
            java.lang.String r1 = r9.imagePath
            r0.<init>(r1)
            r0.delete()
        L_0x00eb:
            java.lang.String r0 = r9.paintPath
            if (r0 == 0) goto L_0x01a4
            java.io.File r0 = new java.io.File
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r14)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r3 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r2.append(r3)
            r2.append(r15)
            java.lang.String r2 = r2.toString()
            r0.<init>(r1, r2)
            java.lang.String r2 = r0.getAbsolutePath()
            r9.thumbPath = r2
            java.lang.String r3 = r9.fullPaintPath
            r0 = 0
            r15 = 1
            r1 = r31
            r21 = r4
            r4 = r0
            r22 = r5
            r5 = r6
            r20 = r6
            r6 = r15
            r1.mergeThumbs(r2, r3, r4, r5, r6)
            boolean r0 = r31.hasAnimatedMediaEntities()
            if (r0 == 0) goto L_0x012c
            java.lang.String r0 = r9.paintPath
            goto L_0x012e
        L_0x012c:
            java.lang.String r0 = r9.fullPaintPath
        L_0x012e:
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0)
            org.telegram.ui.Components.PaintingOverlay r1 = r7.paintingOverlay
            r1.setBitmap(r0)
            int r1 = r20.getWidth()
            int r2 = r20.getHeight()
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r1, r2, r3)
            android.graphics.Canvas r2 = new android.graphics.Canvas
            r2.<init>(r1)
            android.graphics.Rect r3 = new android.graphics.Rect
            int r4 = r20.getWidth()
            int r5 = r20.getHeight()
            r3.<init>(r13, r13, r4, r5)
            android.graphics.Paint r4 = r7.bitmapPaint
            r6 = r20
            r2.drawBitmap(r6, r12, r3, r4)
            android.graphics.Rect r3 = new android.graphics.Rect
            int r4 = r6.getWidth()
            int r5 = r6.getHeight()
            r3.<init>(r13, r13, r4, r5)
            android.graphics.Paint r4 = r7.bitmapPaint
            r2.drawBitmap(r0, r12, r3, r4)
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r0 = (float) r0
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r2 = (float) r2
            r26 = 80
            r27 = 0
            r28 = 101(0x65, float:1.42E-43)
            r29 = 101(0x65, float:1.42E-43)
            r23 = r1
            r24 = r0
            r25 = r2
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r23, (float) r24, (float) r25, (int) r26, (boolean) r27, (int) r28, (int) r29)
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r8)
            java.lang.String r0 = r0.toString()
            r9.imagePath = r0
            r1.recycle()
            org.telegram.ui.Components.PaintingOverlay r0 = r7.paintingOverlay
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r1 = r7.currentMediaEntities
            boolean r2 = r7.isCurrentVideo
            r0.setEntities(r1, r2, r8)
            goto L_0x034d
        L_0x01a4:
            r21 = r4
            r22 = r5
            r0 = 1140850688(0x44000000, float:512.0)
            r1 = 1140850688(0x44000000, float:512.0)
            r16 = 70
            r17 = 0
            r18 = 101(0x65, float:1.42E-43)
            r19 = 101(0x65, float:1.42E-43)
            r5 = 0
            r13 = r6
            r4 = 4
            r14 = r0
            r2 = r15
            r15 = r1
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r13, (float) r14, (float) r15, (int) r16, (boolean) r17, (int) r18, (int) r19)
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r8)
            java.lang.String r0 = r0.toString()
            r9.thumbPath = r0
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x01f6 }
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r4)     // Catch:{ Exception -> 0x01f6 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f6 }
            r3.<init>()     // Catch:{ Exception -> 0x01f6 }
            int r13 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ Exception -> 0x01f6 }
            r3.append(r13)     // Catch:{ Exception -> 0x01f6 }
            r3.append(r2)     // Catch:{ Exception -> 0x01f6 }
            java.lang.String r2 = r3.toString()     // Catch:{ Exception -> 0x01f6 }
            r0.<init>(r1, r2)     // Catch:{ Exception -> 0x01f6 }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x01f6 }
            java.lang.String r2 = r9.croppedPath     // Catch:{ Exception -> 0x01f6 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x01f6 }
            org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r1, (java.io.File) r0)     // Catch:{ Exception -> 0x01f6 }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x01f6 }
            r9.imagePath = r0     // Catch:{ Exception -> 0x01f6 }
            goto L_0x034d
        L_0x01f6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x034d
        L_0x01fc:
            r21 = r4
            r22 = r5
            r13 = r15
            r4 = 4
            r5 = 0
            if (r3 != r11) goto L_0x039e
            java.lang.String r1 = r9.filterPath
            if (r1 == 0) goto L_0x0213
            java.io.File r1 = new java.io.File
            java.lang.String r3 = r9.filterPath
            r1.<init>(r3)
            r1.delete()
        L_0x0213:
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r8)
            java.lang.String r1 = r1.toString()
            r7.currentImagePath = r1
            r9.filterPath = r1
            java.lang.String r1 = r9.paintPath
            if (r1 == 0) goto L_0x02d3
            boolean r1 = r31.hasAnimatedMediaEntities()
            if (r1 != 0) goto L_0x02d3
            boolean r1 = r7.isCurrentVideo
            if (r1 != 0) goto L_0x02d3
            java.lang.String r0 = r9.paintPath
            java.lang.String r1 = r9.fullPaintPath
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x023f
            org.telegram.ui.Components.PaintingOverlay r0 = r7.paintingOverlay
            android.graphics.Bitmap r0 = r0.getBitmap()
            r13 = 0
            goto L_0x0246
        L_0x023f:
            java.lang.String r0 = r9.fullPaintPath
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0)
            r13 = 1
        L_0x0246:
            int r1 = r6.getWidth()
            int r2 = r6.getHeight()
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r1, r2, r3)
            android.graphics.Canvas r2 = new android.graphics.Canvas
            r2.<init>(r1)
            android.graphics.Rect r3 = new android.graphics.Rect
            int r14 = r6.getWidth()
            int r15 = r6.getHeight()
            r3.<init>(r5, r5, r14, r15)
            android.graphics.Paint r14 = r7.bitmapPaint
            r2.drawBitmap(r6, r12, r3, r14)
            android.graphics.Rect r3 = new android.graphics.Rect
            int r14 = r6.getWidth()
            int r15 = r6.getHeight()
            r3.<init>(r5, r5, r14, r15)
            android.graphics.Paint r14 = r7.bitmapPaint
            r2.drawBitmap(r0, r12, r3, r14)
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r15 = (float) r2
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r2 = (float) r2
            r17 = 80
            r18 = 0
            r19 = 101(0x65, float:1.42E-43)
            r20 = 101(0x65, float:1.42E-43)
            r14 = r1
            r16 = r2
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r14, (float) r15, (float) r16, (int) r17, (boolean) r18, (int) r19, (int) r20)
            java.lang.String r3 = r9.imagePath
            if (r3 == 0) goto L_0x02a4
            java.io.File r3 = new java.io.File
            java.lang.String r14 = r9.imagePath
            r3.<init>(r14)
            r3.delete()
        L_0x02a4:
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r8)
            java.lang.String r2 = r2.toString()
            r9.imagePath = r2
            r15 = 1140850688(0x44000000, float:512.0)
            r16 = 1140850688(0x44000000, float:512.0)
            r17 = 70
            r18 = 0
            r19 = 101(0x65, float:1.42E-43)
            r20 = 101(0x65, float:1.42E-43)
            r14 = r1
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r14, (float) r15, (float) r16, (int) r17, (boolean) r18, (int) r19, (int) r20)
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r8)
            java.lang.String r2 = r2.toString()
            r9.thumbPath = r2
            if (r13 == 0) goto L_0x02ce
            r0.recycle()
        L_0x02ce:
            r1.recycle()
            goto L_0x034d
        L_0x02d3:
            java.lang.String r1 = r9.paintPath
            if (r1 != 0) goto L_0x0352
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0304 }
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r4)     // Catch:{ Exception -> 0x0304 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0304 }
            r2.<init>()     // Catch:{ Exception -> 0x0304 }
            int r3 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ Exception -> 0x0304 }
            r2.append(r3)     // Catch:{ Exception -> 0x0304 }
            r2.append(r13)     // Catch:{ Exception -> 0x0304 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0304 }
            r0.<init>(r1, r2)     // Catch:{ Exception -> 0x0304 }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x0304 }
            java.lang.String r2 = r9.filterPath     // Catch:{ Exception -> 0x0304 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0304 }
            org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r1, (java.io.File) r0)     // Catch:{ Exception -> 0x0304 }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x0304 }
            r9.thumbPath = r0     // Catch:{ Exception -> 0x0304 }
            goto L_0x0308
        L_0x0304:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0308:
            boolean r0 = r31.hasAnimatedMediaEntities()
            if (r0 != 0) goto L_0x034d
            java.lang.String r0 = r9.imagePath
            if (r0 == 0) goto L_0x031c
            java.io.File r0 = new java.io.File
            java.lang.String r1 = r9.imagePath
            r0.<init>(r1)
            r0.delete()
        L_0x031c:
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0349 }
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r4)     // Catch:{ Exception -> 0x0349 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0349 }
            r2.<init>()     // Catch:{ Exception -> 0x0349 }
            int r3 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ Exception -> 0x0349 }
            r2.append(r3)     // Catch:{ Exception -> 0x0349 }
            r2.append(r13)     // Catch:{ Exception -> 0x0349 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0349 }
            r0.<init>(r1, r2)     // Catch:{ Exception -> 0x0349 }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x0349 }
            java.lang.String r2 = r9.filterPath     // Catch:{ Exception -> 0x0349 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0349 }
            org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r1, (java.io.File) r0)     // Catch:{ Exception -> 0x0349 }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x0349 }
            r9.imagePath = r0     // Catch:{ Exception -> 0x0349 }
            goto L_0x034d
        L_0x0349:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x034d:
            r11 = r6
            r14 = 0
            r15 = 4
            goto L_0x0592
        L_0x0352:
            java.io.File r1 = new java.io.File
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r4)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r14 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r3.append(r14)
            r3.append(r13)
            java.lang.String r3 = r3.toString()
            r1.<init>(r2, r3)
            java.lang.String r2 = r1.getAbsolutePath()
            r9.thumbPath = r2
            java.lang.String r3 = r9.fullPaintPath
            java.lang.String r1 = r9.paintPath
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x0386
            org.telegram.ui.Components.PaintingOverlay r1 = r7.paintingOverlay
            android.graphics.Bitmap r1 = r1.getThumb()
            r13 = r1
            goto L_0x0387
        L_0x0386:
            r13 = r12
        L_0x0387:
            r1 = r0[r5]
            if (r1 == 0) goto L_0x038e
            r0 = r0[r5]
            goto L_0x038f
        L_0x038e:
            r0 = r6
        L_0x038f:
            r14 = 1
            r1 = r31
            r15 = 4
            r4 = r13
            r13 = 0
            r5 = r0
            r11 = r6
            r6 = r14
            r1.mergeThumbs(r2, r3, r4, r5, r6)
            r14 = 0
            goto L_0x0592
        L_0x039e:
            r11 = r6
            r14 = 0
            r15 = 4
            if (r3 != r10) goto L_0x0592
            java.lang.String r3 = r9.paintPath
            if (r3 == 0) goto L_0x03c5
            java.io.File r3 = new java.io.File
            java.lang.String r4 = r9.paintPath
            r3.<init>(r4)
            r3.delete()
            java.lang.String r3 = r9.paintPath
            java.lang.String r4 = r9.fullPaintPath
            boolean r3 = r3.equals(r4)
            if (r3 != 0) goto L_0x03c5
            java.io.File r3 = new java.io.File
            java.lang.String r4 = r9.fullPaintPath
            r3.<init>(r4)
            r3.delete()
        L_0x03c5:
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r8)
            java.lang.String r2 = r2.toString()
            r7.currentPaintPath = r2
            r9.paintPath = r2
            org.telegram.ui.Components.PaintingOverlay r2 = r7.paintingOverlay
            if (r1 == 0) goto L_0x03db
            boolean r3 = r1.isEmpty()
            if (r3 == 0) goto L_0x03dc
        L_0x03db:
            r1 = r12
        L_0x03dc:
            r7.currentMediaEntities = r1
            r9.mediaEntities = r1
            boolean r3 = r7.isCurrentVideo
            r2.setEntities(r1, r3, r8)
            org.telegram.ui.Components.PhotoPaintView r1 = r7.photoPaintView
            long r1 = r1.getLcm()
            r7.currentAverageDuration = r1
            r9.averageDuration = r1
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r1 = r9.mediaEntities
            if (r1 == 0) goto L_0x0420
            r1 = r0[r14]
            if (r1 == 0) goto L_0x0420
            r23 = r0[r14]
            android.graphics.Bitmap$CompressFormat r24 = android.graphics.Bitmap.CompressFormat.PNG
            int r1 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r1 = (float) r1
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r2 = (float) r2
            r27 = 80
            r28 = 0
            r29 = 101(0x65, float:1.42E-43)
            r30 = 101(0x65, float:1.42E-43)
            r25 = r1
            r26 = r2
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r23, r24, r25, r26, r27, r28, r29, r30)
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r8)
            java.lang.String r1 = r1.toString()
            r9.fullPaintPath = r1
            goto L_0x0424
        L_0x0420:
            java.lang.String r1 = r9.paintPath
            r9.fullPaintPath = r1
        L_0x0424:
            org.telegram.ui.Components.PaintingOverlay r1 = r7.paintingOverlay
            r1.setBitmap(r11)
            boolean r1 = r31.hasAnimatedMediaEntities()
            if (r1 != 0) goto L_0x04cc
            boolean r1 = r7.isCurrentVideo
            if (r1 != 0) goto L_0x04cc
            java.lang.String r1 = r9.filterPath
            if (r1 == 0) goto L_0x0438
            goto L_0x0441
        L_0x0438:
            java.lang.String r1 = r9.croppedPath
            if (r1 == 0) goto L_0x043d
            goto L_0x0441
        L_0x043d:
            java.lang.String r1 = r9.getPath()
        L_0x0441:
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r3 = (float) r3
            android.graphics.Bitmap r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r12, r2, r3, r8)
            boolean r2 = r1.isMutable()
            if (r2 != 0) goto L_0x045b
            android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r1 = r1.copy(r2, r8)
        L_0x045b:
            android.graphics.Canvas r2 = new android.graphics.Canvas
            r2.<init>(r1)
            r3 = r0[r14]
            if (r3 == 0) goto L_0x0467
            r6 = r0[r14]
            goto L_0x0468
        L_0x0467:
            r6 = r11
        L_0x0468:
            android.graphics.Rect r0 = new android.graphics.Rect
            int r3 = r1.getWidth()
            int r4 = r1.getHeight()
            r0.<init>(r14, r14, r3, r4)
            android.graphics.Paint r3 = r7.bitmapPaint
            r2.drawBitmap(r6, r12, r0, r3)
            int r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r0 = (float) r0
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            float r2 = (float) r2
            r26 = 80
            r27 = 0
            r28 = 101(0x65, float:1.42E-43)
            r29 = 101(0x65, float:1.42E-43)
            r23 = r1
            r24 = r0
            r25 = r2
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r23, (float) r24, (float) r25, (int) r26, (boolean) r27, (int) r28, (int) r29)
            java.lang.String r2 = r9.imagePath
            if (r2 == 0) goto L_0x04a4
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r9.imagePath
            r2.<init>(r3)
            r2.delete()
        L_0x04a4:
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r8)
            java.lang.String r0 = r0.toString()
            r9.imagePath = r0
            r24 = 1140850688(0x44000000, float:512.0)
            r25 = 1140850688(0x44000000, float:512.0)
            r26 = 70
            r27 = 0
            r28 = 101(0x65, float:1.42E-43)
            r29 = 101(0x65, float:1.42E-43)
            r23 = r1
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r23, (float) r24, (float) r25, (int) r26, (boolean) r27, (int) r28, (int) r29)
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r8)
            java.lang.String r0 = r0.toString()
            r9.thumbPath = r0
            goto L_0x0592
        L_0x04cc:
            java.lang.String r1 = r9.filterPath
            if (r1 != 0) goto L_0x055e
            java.lang.String r1 = r9.imagePath
            if (r1 == 0) goto L_0x04e0
            java.io.File r1 = new java.io.File
            java.lang.String r2 = r9.imagePath
            r1.<init>(r2)
            r1.delete()
            r9.imagePath = r12
        L_0x04e0:
            boolean r1 = r7.isCurrentVideo
            if (r1 == 0) goto L_0x0505
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
            goto L_0x0520
        L_0x0505:
            java.lang.String r1 = r9.croppedPath
            if (r1 == 0) goto L_0x050a
            goto L_0x050e
        L_0x050a:
            java.lang.String r1 = r9.getPath()
        L_0x050e:
            r2 = 1140850688(0x44000000, float:512.0)
            android.graphics.Bitmap r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r12, r2, r2, r8)
            boolean r2 = r1.isMutable()
            if (r2 != 0) goto L_0x0520
            android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r1 = r1.copy(r2, r8)
        L_0x0520:
            if (r1 == 0) goto L_0x0592
            android.graphics.Canvas r2 = new android.graphics.Canvas
            r2.<init>(r1)
            r3 = r0[r14]
            if (r3 == 0) goto L_0x052e
            r6 = r0[r14]
            goto L_0x052f
        L_0x052e:
            r6 = r11
        L_0x052f:
            android.graphics.Rect r0 = new android.graphics.Rect
            int r3 = r1.getWidth()
            int r4 = r1.getHeight()
            r0.<init>(r14, r14, r3, r4)
            android.graphics.Paint r3 = r7.bitmapPaint
            r2.drawBitmap(r6, r12, r0, r3)
            r24 = 1140850688(0x44000000, float:512.0)
            r25 = 1140850688(0x44000000, float:512.0)
            r26 = 70
            r27 = 0
            r28 = 101(0x65, float:1.42E-43)
            r29 = 101(0x65, float:1.42E-43)
            r23 = r1
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r23, (float) r24, (float) r25, (int) r26, (boolean) r27, (int) r28, (int) r29)
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r8)
            java.lang.String r0 = r0.toString()
            r9.thumbPath = r0
            goto L_0x0592
        L_0x055e:
            java.io.File r1 = new java.io.File
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r15)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r4 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r3.append(r4)
            r3.append(r13)
            java.lang.String r3 = r3.toString()
            r1.<init>(r2, r3)
            java.lang.String r2 = r1.getAbsolutePath()
            r9.thumbPath = r2
            java.lang.String r3 = r9.filterPath
            r4 = 0
            r1 = r0[r14]
            if (r1 == 0) goto L_0x058b
            r0 = r0[r14]
            r5 = r0
            goto L_0x058c
        L_0x058b:
            r5 = r11
        L_0x058c:
            r6 = 0
            r1 = r31
            r1.mergeThumbs(r2, r3, r4, r5, r6)
        L_0x0592:
            org.telegram.messenger.SharedConfig.saveConfig()
            r3 = r21
            r9.stickers = r3
            r2 = r22
            if (r2 == 0) goto L_0x05a1
            r7.currentSavedFilterState = r2
            r9.savedFilterState = r2
        L_0x05a1:
            int r0 = r7.currentEditMode
            java.lang.String r1 = "dialogFloatingButton"
            if (r0 != r8) goto L_0x05ba
            r9.isCropped = r8
            android.widget.ImageView r0 = r7.cropItem
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r1, r3)
            r0.setColorFilter(r2)
            goto L_0x05e4
        L_0x05ba:
            r2 = 2
            if (r0 != r2) goto L_0x05d0
            r9.isFiltered = r8
            android.widget.ImageView r0 = r7.tuneItem
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r1, r3)
            r0.setColorFilter(r2)
            goto L_0x05e4
        L_0x05d0:
            if (r0 != r10) goto L_0x05e4
            r9.isPainted = r8
            android.widget.ImageView r0 = r7.paintItem
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r1, r3)
            r0.setColorFilter(r2)
        L_0x05e4:
            int r0 = r7.sendPhotoType
            if (r0 == 0) goto L_0x05ea
            if (r0 != r15) goto L_0x0600
        L_0x05ea:
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r0 = r7.placeProvider
            if (r0 == 0) goto L_0x0600
            int r1 = r7.currentIndex
            r0.updatePhotoAtIndex(r1)
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r0 = r7.placeProvider
            int r1 = r7.currentIndex
            boolean r0 = r0.isPhotoChecked(r1)
            if (r0 != 0) goto L_0x0600
            r31.setPhotoChecked()
        L_0x0600:
            int r0 = r7.currentEditMode
            if (r0 != r8) goto L_0x065a
            org.telegram.ui.Components.PhotoCropView r0 = r7.photoCropView
            float r0 = r0.getRectSizeX()
            int r1 = r31.getContainerViewWidth()
            float r1 = (float) r1
            float r0 = r0 / r1
            org.telegram.ui.Components.PhotoCropView r1 = r7.photoCropView
            float r1 = r1.getRectSizeY()
            int r2 = r31.getContainerViewHeight()
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
            int r1 = r31.getContainerViewWidth()
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
            int r1 = r31.getContainerViewHeight()
            int r1 = r1 / r3
            float r1 = (float) r1
            float r0 = r0 - r1
            r7.translationY = r0
            r7.zoomAnimation = r8
            org.telegram.ui.Components.PhotoCropView r0 = r7.photoCropView
            r0.onDisappear()
        L_0x065a:
            org.telegram.messenger.ImageReceiver r0 = r7.centerImage
            r0.setParentView(r12)
            r7.ignoreDidSetImage = r8
            boolean r0 = r7.isCurrentVideo
            if (r0 != 0) goto L_0x0678
            int r0 = r7.currentEditMode
            if (r0 == r10) goto L_0x0678
            org.telegram.messenger.ImageReceiver r0 = r7.centerImage
            r0.setImageBitmap((android.graphics.Bitmap) r11)
            org.telegram.messenger.ImageReceiver r0 = r7.centerImage
            r0.setOrientation(r14, r8)
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r0 = r7.containerView
            r0.requestLayout()
        L_0x0678:
            r7.ignoreDidSetImage = r14
            org.telegram.messenger.ImageReceiver r0 = r7.centerImage
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r1 = r7.containerView
            r0.setParentView(r1)
            int r0 = r7.sendPhotoType
            if (r0 != r8) goto L_0x0688
            r31.setCropBitmap()
        L_0x0688:
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
            this.containerView.addView(this.photoCropView, this.containerView.indexOfChild(this.pickerViewSendButton) - 1, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
            this.photoCropView.setDelegate(new PhotoCropView.PhotoCropViewDelegate() {
                public final void onChange(boolean z) {
                    PhotoViewer.this.lambda$createCropView$39$PhotoViewer(z);
                }
            });
        }
    }

    public /* synthetic */ void lambda$createCropView$39$PhotoViewer(boolean z) {
        this.resetButton.setVisibility(z ? 8 : 0);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v0, resolved type: android.graphics.Bitmap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v1, resolved type: android.graphics.Bitmap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v4, resolved type: android.graphics.Bitmap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v1, resolved type: org.telegram.ui.Components.PaintingOverlay} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v9, resolved type: android.graphics.Bitmap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v10, resolved type: android.graphics.Bitmap} */
    /* JADX WARNING: type inference failed for: r14v5, types: [org.telegram.ui.Components.PaintingOverlay] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x036b  */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0370  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x038f  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0394  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x039b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void switchToEditMode(int r24) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            int r2 = r0.currentEditMode
            if (r2 == r1) goto L_0x06b9
            boolean r2 = r0.isCurrentVideo
            r3 = -1
            r4 = 3
            r5 = 0
            if (r2 == 0) goto L_0x002f
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r2 = r0.photoProgressViews
            r2 = r2[r5]
            int r2 = r2.backgroundState
            if (r2 == r4) goto L_0x002f
            boolean r2 = r0.isCurrentVideo
            if (r2 != 0) goto L_0x002f
            org.telegram.messenger.ImageReceiver r2 = r0.centerImage
            android.graphics.Bitmap r2 = r2.getBitmap()
            if (r2 == 0) goto L_0x06b9
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r2 = r0.photoProgressViews
            r2 = r2[r5]
            int r2 = r2.backgroundState
            if (r2 != r3) goto L_0x06b9
        L_0x002f:
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            if (r2 != 0) goto L_0x06b9
            android.animation.AnimatorSet r2 = r0.imageMoveAnimation
            if (r2 != 0) goto L_0x06b9
            org.telegram.ui.Components.PhotoViewerCaptionEnterView r2 = r0.captionEditText
            java.lang.Object r2 = r2.getTag()
            if (r2 == 0) goto L_0x0041
            goto L_0x06b9
        L_0x0041:
            r2 = 1127874560(0x433a0000, float:186.0)
            r6 = 1123811328(0x42fCLASSNAME, float:126.0)
            r9 = 4
            r10 = 0
            r11 = 1
            r12 = 2
            if (r1 != 0) goto L_0x01df
            org.telegram.messenger.ImageReceiver r3 = r0.centerImage
            android.graphics.Bitmap r3 = r3.getBitmap()
            if (r3 == 0) goto L_0x00d8
            org.telegram.messenger.ImageReceiver r3 = r0.centerImage
            int r3 = r3.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r13 = r0.centerImage
            int r13 = r13.getBitmapHeight()
            int r14 = r23.getContainerViewWidth()
            float r14 = (float) r14
            float r3 = (float) r3
            float r14 = r14 / r3
            int r15 = r23.getContainerViewHeight()
            float r15 = (float) r15
            float r13 = (float) r13
            float r15 = r15 / r13
            int r7 = r0.getContainerViewWidth(r5)
            float r7 = (float) r7
            float r7 = r7 / r3
            int r3 = r0.getContainerViewHeight(r5)
            float r3 = (float) r3
            float r3 = r3 / r13
            float r8 = java.lang.Math.min(r14, r15)
            float r3 = java.lang.Math.min(r7, r3)
            int r7 = r0.sendPhotoType
            if (r7 != r11) goto L_0x0089
            r0.setCropTranslations(r11)
            goto L_0x00d8
        L_0x0089:
            float r3 = r3 / r8
            r0.animateToScale = r3
            r0.animateToX = r10
            int r3 = r23.getLeftInset()
            int r3 = r3 / r12
            int r7 = r23.getRightInset()
            int r7 = r7 / r12
            int r3 = r3 - r7
            float r3 = (float) r3
            r0.translationX = r3
            int r3 = r0.currentEditMode
            if (r3 != r11) goto L_0x00aa
            r3 = 1114112000(0x42680000, float:58.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r0.animateToY = r3
            goto L_0x00c1
        L_0x00aa:
            if (r3 != r12) goto L_0x00b6
            r3 = 1119354880(0x42b80000, float:92.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r0.animateToY = r3
            goto L_0x00c1
        L_0x00b6:
            if (r3 != r4) goto L_0x00c1
            r3 = 1110441984(0x42300000, float:44.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r0.animateToY = r3
        L_0x00c1:
            int r3 = android.os.Build.VERSION.SDK_INT
            r7 = 21
            if (r3 < r7) goto L_0x00d0
            float r3 = r0.animateToY
            int r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            int r7 = r7 / r12
            float r7 = (float) r7
            float r3 = r3 - r7
            r0.animateToY = r3
        L_0x00d0:
            long r7 = java.lang.System.currentTimeMillis()
            r0.animationStartTime = r7
            r0.zoomAnimation = r11
        L_0x00d8:
            r0.padImageForHorizontalInsets = r5
            android.animation.AnimatorSet r3 = new android.animation.AnimatorSet
            r3.<init>()
            r0.imageMoveAnimation = r3
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>(r9)
            int r7 = r0.currentEditMode
            if (r7 != r11) goto L_0x011f
            org.telegram.ui.Components.PickerBottomLayoutViewer r2 = r0.editorDoneLayout
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r11]
            r7 = 1111490560(0x42400000, float:48.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r6[r5] = r7
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r4, r6)
            r3.add(r2)
            android.util.Property<org.telegram.ui.PhotoViewer, java.lang.Float> r2 = org.telegram.ui.Components.AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE
            float[] r4 = new float[r12]
            r4 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r0, r2, r4)
            r3.add(r2)
            org.telegram.ui.Components.PhotoCropView r2 = r0.photoCropView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r6 = new float[r11]
            r6[r5] = r10
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r4, r6)
            r3.add(r2)
            goto L_0x01c2
        L_0x011f:
            if (r7 != r12) goto L_0x0173
            org.telegram.ui.Components.PhotoFilterView r4 = r0.photoFilterView
            r4.shutdown()
            org.telegram.ui.Components.PhotoFilterView r4 = r0.photoFilterView
            android.widget.FrameLayout r4 = r4.getToolsView()
            android.util.Property r6 = android.view.View.TRANSLATION_Y
            float[] r7 = new float[r11]
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r7[r5] = r2
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r4, r6, r7)
            r3.add(r2)
            org.telegram.ui.Components.PhotoFilterView r2 = r0.photoFilterView
            android.view.View r2 = r2.getCurveControl()
            android.util.Property r4 = android.view.View.ALPHA
            float[] r6 = new float[r11]
            r6[r5] = r10
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r4, r6)
            r3.add(r2)
            org.telegram.ui.Components.PhotoFilterView r2 = r0.photoFilterView
            android.view.View r2 = r2.getBlurControl()
            android.util.Property r4 = android.view.View.ALPHA
            float[] r6 = new float[r11]
            r6[r5] = r10
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r4, r6)
            r3.add(r2)
            android.util.Property<org.telegram.ui.PhotoViewer, java.lang.Float> r2 = org.telegram.ui.Components.AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE
            float[] r4 = new float[r12]
            r4 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r0, r2, r4)
            r3.add(r2)
            goto L_0x01c2
        L_0x0173:
            if (r7 != r4) goto L_0x01c2
            org.telegram.ui.Components.PaintingOverlay r2 = r0.paintingOverlay
            r2.showAll()
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r2 = r0.containerView
            r2.invalidate()
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            r2.shutdown()
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            android.widget.FrameLayout r2 = r2.getToolsView()
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r7 = new float[r11]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r8 = (float) r8
            r7[r5] = r8
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r4, r7)
            r3.add(r2)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            org.telegram.ui.Components.Paint.Views.ColorPicker r2 = r2.getColorPicker()
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r7 = new float[r11]
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            r7[r5] = r6
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r4, r7)
            r3.add(r2)
            android.util.Property<org.telegram.ui.PhotoViewer, java.lang.Float> r2 = org.telegram.ui.Components.AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE
            float[] r4 = new float[r12]
            r4 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r0, r2, r4)
            r3.add(r2)
        L_0x01c2:
            android.animation.AnimatorSet r2 = r0.imageMoveAnimation
            r2.playTogether(r3)
            android.animation.AnimatorSet r2 = r0.imageMoveAnimation
            r3 = 200(0xc8, double:9.9E-322)
            r2.setDuration(r3)
            android.animation.AnimatorSet r2 = r0.imageMoveAnimation
            org.telegram.ui.PhotoViewer$38 r3 = new org.telegram.ui.PhotoViewer$38
            r3.<init>(r1)
            r2.addListener(r3)
            android.animation.AnimatorSet r1 = r0.imageMoveAnimation
            r1.start()
            goto L_0x06b9
        L_0x01df:
            r8 = 1119879168(0x42CLASSNAME, float:96.0)
            if (r1 != r11) goto L_0x0317
            r23.createCropView()
            org.telegram.ui.Components.PhotoCropView r2 = r0.photoCropView
            r2.onAppear()
            org.telegram.ui.Components.PickerBottomLayoutViewer r2 = r0.editorDoneLayout
            android.widget.TextView r2 = r2.doneButton
            r3 = 2131624820(0x7f0e0374, float:1.887683E38)
            java.lang.String r4 = "Crop"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            org.telegram.ui.Components.PickerBottomLayoutViewer r2 = r0.editorDoneLayout
            android.widget.TextView r2 = r2.doneButton
            java.lang.String r3 = "dialogFloatingButton"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            r0.changeModeAnimation = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            android.widget.FrameLayout r3 = r0.pickerView
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r5] = r10
            boolean r13 = r0.isCurrentVideo
            if (r13 == 0) goto L_0x0223
            r13 = 1125777408(0x431a0000, float:154.0)
            goto L_0x0225
        L_0x0223:
            r13 = 1119879168(0x42CLASSNAME, float:96.0)
        L_0x0225:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r6[r11] = r13
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            android.widget.ImageView r3 = r0.pickerViewSendButton
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r5] = r10
            boolean r13 = r0.isCurrentVideo
            if (r13 == 0) goto L_0x0242
            r13 = 1125777408(0x431a0000, float:154.0)
            goto L_0x0244
        L_0x0242:
            r13 = 1119879168(0x42CLASSNAME, float:96.0)
        L_0x0244:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r6[r11] = r13
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r5] = r10
            int r13 = r3.getHeight()
            int r13 = -r13
            float r13 = (float) r13
            r6[r11] = r13
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            boolean r3 = r0.needCaptionLayout
            if (r3 == 0) goto L_0x028c
            org.telegram.ui.PhotoViewer$CaptionTextViewSwitcher r3 = r0.captionTextViewSwitcher
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r5] = r10
            boolean r5 = r0.isCurrentVideo
            if (r5 == 0) goto L_0x027c
            r7 = 1125777408(0x431a0000, float:154.0)
            goto L_0x027e
        L_0x027c:
            r7 = 1119879168(0x42CLASSNAME, float:96.0)
        L_0x027e:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r5 = (float) r5
            r6[r11] = r5
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
        L_0x028c:
            int r3 = r0.sendPhotoType
            if (r3 == 0) goto L_0x0292
            if (r3 != r9) goto L_0x02b2
        L_0x0292:
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
        L_0x02b2:
            org.telegram.ui.PhotoViewer$SelectedPhotosListView r3 = r0.selectedPhotosListView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x02ca
            org.telegram.ui.PhotoViewer$SelectedPhotosListView r3 = r0.selectedPhotosListView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x02ca:
            android.widget.ImageView r3 = r0.cameraItem
            java.lang.Object r3 = r3.getTag()
            if (r3 == 0) goto L_0x02e2
            android.widget.ImageView r3 = r0.cameraItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x02e2:
            android.widget.ImageView r3 = r0.muteItem
            java.lang.Object r3 = r3.getTag()
            if (r3 == 0) goto L_0x02fa
            android.widget.ImageView r3 = r0.muteItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x02fa:
            android.animation.AnimatorSet r3 = r0.changeModeAnimation
            r3.playTogether(r2)
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            r3 = 200(0xc8, double:9.9E-322)
            r2.setDuration(r3)
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            org.telegram.ui.PhotoViewer$39 r3 = new org.telegram.ui.PhotoViewer$39
            r3.<init>(r1)
            r2.addListener(r3)
            android.animation.AnimatorSet r1 = r0.changeModeAnimation
            r1.start()
            goto L_0x06b9
        L_0x0317:
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r14 = 0
            if (r1 != r12) goto L_0x04db
            boolean r4 = r0.isCurrentVideo
            if (r4 == 0) goto L_0x0332
            org.telegram.ui.Components.VideoPlayer r4 = r0.videoPlayer
            if (r4 == 0) goto L_0x0332
            boolean r4 = r4.isPlaying()
            if (r4 != 0) goto L_0x0332
            org.telegram.ui.Components.VideoPlayer r4 = r0.videoPlayer
            r4.setVolume(r10)
            r23.toggleVideoPlayer()
        L_0x0332:
            org.telegram.ui.Components.PhotoFilterView r4 = r0.photoFilterView
            if (r4 != 0) goto L_0x03db
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x0363
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            int r6 = r0.currentIndex
            java.lang.Object r4 = r4.get(r6)
            boolean r6 = r4 instanceof org.telegram.messenger.MediaController.PhotoEntry
            if (r6 == 0) goto L_0x0350
            r6 = r4
            org.telegram.messenger.MediaController$PhotoEntry r6 = (org.telegram.messenger.MediaController.PhotoEntry) r6
            int r6 = r6.orientation
            goto L_0x0351
        L_0x0350:
            r6 = 0
        L_0x0351:
            org.telegram.messenger.MediaController$MediaEditState r4 = (org.telegram.messenger.MediaController.MediaEditState) r4
            org.telegram.messenger.MediaController$SavedFilterState r15 = r4.savedFilterState
            java.lang.String r7 = r4.croppedPath
            if (r7 == 0) goto L_0x035c
            r21 = r15
            goto L_0x0366
        L_0x035c:
            java.lang.String r7 = r4.getPath()
            r21 = r15
            goto L_0x0367
        L_0x0363:
            r7 = r14
            r21 = r7
        L_0x0366:
            r6 = 0
        L_0x0367:
            android.view.TextureView r4 = r0.videoTextureView
            if (r4 == 0) goto L_0x0370
            r20 = r6
            r19 = r14
            goto L_0x0387
        L_0x0370:
            if (r21 != 0) goto L_0x037f
            org.telegram.messenger.ImageReceiver r4 = r0.centerImage
            android.graphics.Bitmap r4 = r4.getBitmap()
            org.telegram.messenger.ImageReceiver r6 = r0.centerImage
            int r6 = r6.getOrientation()
            goto L_0x0383
        L_0x037f:
            android.graphics.Bitmap r4 = android.graphics.BitmapFactory.decodeFile(r7)
        L_0x0383:
            r19 = r4
            r20 = r6
        L_0x0387:
            org.telegram.ui.Components.PhotoFilterView r4 = new org.telegram.ui.Components.PhotoFilterView
            android.app.Activity r6 = r0.parentActivity
            android.view.TextureView r7 = r0.videoTextureView
            if (r7 == 0) goto L_0x0394
            org.telegram.ui.Components.VideoEditTextureView r7 = (org.telegram.ui.Components.VideoEditTextureView) r7
            r18 = r7
            goto L_0x0396
        L_0x0394:
            r18 = r14
        L_0x0396:
            boolean r7 = r0.isCurrentVideo
            if (r7 == 0) goto L_0x039b
            goto L_0x039d
        L_0x039b:
            org.telegram.ui.Components.PaintingOverlay r14 = r0.paintingOverlay
        L_0x039d:
            r22 = r14
            r16 = r4
            r17 = r6
            r16.<init>(r17, r18, r19, r20, r21, r22)
            r0.photoFilterView = r4
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r6 = r0.containerView
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r13)
            r6.addView(r4, r3)
            org.telegram.ui.Components.PhotoFilterView r3 = r0.photoFilterView
            android.widget.TextView r3 = r3.getDoneTextView()
            org.telegram.ui.-$$Lambda$PhotoViewer$Ws0W6J-E4CCAikNDkwUaS7ufRkg r4 = new org.telegram.ui.-$$Lambda$PhotoViewer$Ws0W6J-E4CCAikNDkwUaS7ufRkg
            r4.<init>()
            r3.setOnClickListener(r4)
            org.telegram.ui.Components.PhotoFilterView r3 = r0.photoFilterView
            android.widget.TextView r3 = r3.getCancelTextView()
            org.telegram.ui.-$$Lambda$PhotoViewer$xJsW02m8k2_4bt00iyBHUcpGVOQ r4 = new org.telegram.ui.-$$Lambda$PhotoViewer$xJsW02m8k2_4bt00iyBHUcpGVOQ
            r4.<init>()
            r3.setOnClickListener(r4)
            org.telegram.ui.Components.PhotoFilterView r3 = r0.photoFilterView
            android.widget.FrameLayout r3 = r3.getToolsView()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r3.setTranslationY(r2)
        L_0x03db:
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            r0.changeModeAnimation = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            android.widget.FrameLayout r3 = r0.pickerView
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r5] = r10
            boolean r7 = r0.isCurrentVideo
            if (r7 == 0) goto L_0x03f6
            r7 = 1125777408(0x431a0000, float:154.0)
            goto L_0x03f8
        L_0x03f6:
            r7 = 1119879168(0x42CLASSNAME, float:96.0)
        L_0x03f8:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r6[r11] = r7
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            android.widget.ImageView r3 = r0.pickerViewSendButton
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r5] = r10
            boolean r7 = r0.isCurrentVideo
            if (r7 == 0) goto L_0x0415
            r7 = 1125777408(0x431a0000, float:154.0)
            goto L_0x0417
        L_0x0415:
            r7 = 1119879168(0x42CLASSNAME, float:96.0)
        L_0x0417:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r6[r11] = r7
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r5] = r10
            int r5 = r3.getHeight()
            int r5 = -r5
            float r5 = (float) r5
            r6[r11] = r5
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            int r3 = r0.sendPhotoType
            if (r3 == 0) goto L_0x0456
            if (r3 != r9) goto L_0x0443
            goto L_0x0456
        L_0x0443:
            if (r3 != r11) goto L_0x0476
            org.telegram.ui.Components.PhotoCropView r3 = r0.photoCropView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
            goto L_0x0476
        L_0x0456:
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
        L_0x0476:
            org.telegram.ui.PhotoViewer$SelectedPhotosListView r3 = r0.selectedPhotosListView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x048e
            org.telegram.ui.PhotoViewer$SelectedPhotosListView r3 = r0.selectedPhotosListView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x048e:
            android.widget.ImageView r3 = r0.cameraItem
            java.lang.Object r3 = r3.getTag()
            if (r3 == 0) goto L_0x04a6
            android.widget.ImageView r3 = r0.cameraItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x04a6:
            android.widget.ImageView r3 = r0.muteItem
            java.lang.Object r3 = r3.getTag()
            if (r3 == 0) goto L_0x04be
            android.widget.ImageView r3 = r0.muteItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x04be:
            android.animation.AnimatorSet r3 = r0.changeModeAnimation
            r3.playTogether(r2)
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            r3 = 200(0xc8, double:9.9E-322)
            r2.setDuration(r3)
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            org.telegram.ui.PhotoViewer$40 r3 = new org.telegram.ui.PhotoViewer$40
            r3.<init>(r1)
            r2.addListener(r3)
            android.animation.AnimatorSet r1 = r0.changeModeAnimation
            r1.start()
            goto L_0x06b9
        L_0x04db:
            if (r1 != r4) goto L_0x06b9
            boolean r2 = r0.isCurrentVideo
            if (r2 == 0) goto L_0x04f3
            org.telegram.ui.Components.VideoPlayer r2 = r0.videoPlayer
            if (r2 == 0) goto L_0x04f3
            boolean r2 = r2.isPlaying()
            if (r2 != 0) goto L_0x04f3
            org.telegram.ui.Components.VideoPlayer r2 = r0.videoPlayer
            r2.setVolume(r10)
            r23.toggleVideoPlayer()
        L_0x04f3:
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            if (r2 != 0) goto L_0x0598
            android.view.TextureView r2 = r0.videoTextureView
            if (r2 == 0) goto L_0x0510
            org.telegram.ui.Components.VideoEditTextureView r2 = (org.telegram.ui.Components.VideoEditTextureView) r2
            int r4 = r2.getVideoWidth()
            int r2 = r2.getVideoHeight()
        L_0x0505:
            r7 = 1280(0x500, float:1.794E-42)
            if (r4 > r7) goto L_0x050b
            if (r2 <= r7) goto L_0x051c
        L_0x050b:
            int r4 = r4 / 2
            int r2 = r2 / 2
            goto L_0x0505
        L_0x0510:
            org.telegram.messenger.ImageReceiver r2 = r0.centerImage
            int r4 = r2.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r2 = r0.centerImage
            int r2 = r2.getBitmapHeight()
        L_0x051c:
            org.telegram.ui.Components.PaintingOverlay r7 = r0.paintingOverlay
            android.graphics.Bitmap r7 = r7.getBitmap()
            if (r7 != 0) goto L_0x052a
            android.graphics.Bitmap$Config r7 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r7 = android.graphics.Bitmap.createBitmap(r4, r2, r7)
        L_0x052a:
            r18 = r7
            org.telegram.ui.Components.PhotoPaintView r2 = new org.telegram.ui.Components.PhotoPaintView
            android.app.Activity r4 = r0.parentActivity
            boolean r7 = r0.isCurrentVideo
            if (r7 == 0) goto L_0x0535
            goto L_0x053b
        L_0x0535:
            org.telegram.messenger.ImageReceiver r7 = r0.centerImage
            android.graphics.Bitmap r14 = r7.getBitmap()
        L_0x053b:
            r19 = r14
            org.telegram.messenger.ImageReceiver r7 = r0.centerImage
            int r20 = r7.getOrientation()
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r7 = r0.currentMediaEntities
            org.telegram.ui.-$$Lambda$PhotoViewer$AWA7DMHwzPZsVPpxXYMPaFHmwAk r14 = new org.telegram.ui.-$$Lambda$PhotoViewer$AWA7DMHwzPZsVPpxXYMPaFHmwAk
            r14.<init>()
            r16 = r2
            r17 = r4
            r21 = r7
            r22 = r14
            r16.<init>(r17, r18, r19, r20, r21, r22)
            r0.photoPaintView = r2
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r4 = r0.containerView
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r13)
            r4.addView(r2, r3)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            android.widget.TextView r2 = r2.getDoneTextView()
            org.telegram.ui.-$$Lambda$PhotoViewer$45KBxdR5IxTnMB6GdFo9_8PB4KQ r3 = new org.telegram.ui.-$$Lambda$PhotoViewer$45KBxdR5IxTnMB6GdFo9_8PB4KQ
            r3.<init>()
            r2.setOnClickListener(r3)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            android.widget.TextView r2 = r2.getCancelTextView()
            org.telegram.ui.-$$Lambda$PhotoViewer$X9yXMjzrxu7FnqFHaZ-uUDEz4f8 r3 = new org.telegram.ui.-$$Lambda$PhotoViewer$X9yXMjzrxu7FnqFHaZ-uUDEz4f8
            r3.<init>()
            r2.setOnClickListener(r3)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            org.telegram.ui.Components.Paint.Views.ColorPicker r2 = r2.getColorPicker()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            r2.setTranslationY(r3)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            android.widget.FrameLayout r2 = r2.getToolsView()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            r2.setTranslationY(r3)
        L_0x0598:
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            r0.changeModeAnimation = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            android.widget.FrameLayout r3 = r0.pickerView
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r5] = r10
            boolean r7 = r0.isCurrentVideo
            if (r7 == 0) goto L_0x05b3
            r7 = 1125777408(0x431a0000, float:154.0)
            goto L_0x05b5
        L_0x05b3:
            r7 = 1119879168(0x42CLASSNAME, float:96.0)
        L_0x05b5:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r6[r11] = r7
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            android.widget.ImageView r3 = r0.pickerViewSendButton
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r5] = r10
            boolean r7 = r0.isCurrentVideo
            if (r7 == 0) goto L_0x05d2
            r7 = 1125777408(0x431a0000, float:154.0)
            goto L_0x05d4
        L_0x05d2:
            r7 = 1119879168(0x42CLASSNAME, float:96.0)
        L_0x05d4:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r6[r11] = r7
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r5] = r10
            int r7 = r3.getHeight()
            int r7 = -r7
            float r7 = (float) r7
            r6[r11] = r7
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            boolean r3 = r0.needCaptionLayout
            if (r3 == 0) goto L_0x061c
            org.telegram.ui.PhotoViewer$CaptionTextViewSwitcher r3 = r0.captionTextViewSwitcher
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r6 = new float[r12]
            r6[r5] = r10
            boolean r5 = r0.isCurrentVideo
            if (r5 == 0) goto L_0x060c
            r7 = 1125777408(0x431a0000, float:154.0)
            goto L_0x060e
        L_0x060c:
            r7 = 1119879168(0x42CLASSNAME, float:96.0)
        L_0x060e:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r5 = (float) r5
            r6[r11] = r5
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
        L_0x061c:
            int r3 = r0.sendPhotoType
            if (r3 == 0) goto L_0x0636
            if (r3 != r9) goto L_0x0623
            goto L_0x0636
        L_0x0623:
            if (r3 != r11) goto L_0x0656
            org.telegram.ui.Components.PhotoCropView r3 = r0.photoCropView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
            goto L_0x0656
        L_0x0636:
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
        L_0x0656:
            org.telegram.ui.PhotoViewer$SelectedPhotosListView r3 = r0.selectedPhotosListView
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x066e
            org.telegram.ui.PhotoViewer$SelectedPhotosListView r3 = r0.selectedPhotosListView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x066e:
            android.widget.ImageView r3 = r0.cameraItem
            java.lang.Object r3 = r3.getTag()
            if (r3 == 0) goto L_0x0686
            android.widget.ImageView r3 = r0.cameraItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x0686:
            android.widget.ImageView r3 = r0.muteItem
            java.lang.Object r3 = r3.getTag()
            if (r3 == 0) goto L_0x069e
            android.widget.ImageView r3 = r0.muteItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r12]
            r5 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
            r2.add(r3)
        L_0x069e:
            android.animation.AnimatorSet r3 = r0.changeModeAnimation
            r3.playTogether(r2)
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            r3 = 200(0xc8, double:9.9E-322)
            r2.setDuration(r3)
            android.animation.AnimatorSet r2 = r0.changeModeAnimation
            org.telegram.ui.PhotoViewer$41 r3 = new org.telegram.ui.PhotoViewer$41
            r3.<init>(r1)
            r2.addListener(r3)
            android.animation.AnimatorSet r1 = r0.changeModeAnimation
            r1.start()
        L_0x06b9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.switchToEditMode(int):void");
    }

    public /* synthetic */ void lambda$switchToEditMode$40$PhotoViewer(View view) {
        applyCurrentEditMode();
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$switchToEditMode$42$PhotoViewer(View view) {
        if (this.photoFilterView.hasChanges()) {
            Activity activity = this.parentActivity;
            if (activity != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
                builder.setMessage(LocaleController.getString("DiscardChanges", NUM));
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PhotoViewer.this.lambda$null$41$PhotoViewer(dialogInterface, i);
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

    public /* synthetic */ void lambda$null$41$PhotoViewer(DialogInterface dialogInterface, int i) {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$switchToEditMode$43$PhotoViewer() {
        this.paintingOverlay.hideBitmap();
    }

    public /* synthetic */ void lambda$switchToEditMode$44$PhotoViewer(View view) {
        applyCurrentEditMode();
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$null$45$PhotoViewer() {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$switchToEditMode$46$PhotoViewer(View view) {
        this.photoPaintView.maybeShowDismissalAlert(this, this.parentActivity, new Runnable() {
            public final void run() {
                PhotoViewer.this.lambda$null$45$PhotoViewer();
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
            CaptionTextViewSwitcher captionTextViewSwitcher2 = this.captionTextViewSwitcher;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(captionTextViewSwitcher2, property3, fArr3));
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

    /* access modifiers changed from: private */
    public void toggleActionBar(final boolean z, boolean z2) {
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
            if (this.videoPlayerControlVisible) {
                VideoPlayerControlFrameLayout videoPlayerControlFrameLayout2 = this.videoPlayerControlFrameLayout;
                Property<VideoPlayerControlFrameLayout, Float> property3 = VPC_PROGRESS;
                float[] fArr3 = new float[1];
                fArr3[0] = z ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(videoPlayerControlFrameLayout2, property3, fArr3));
            } else {
                this.videoPlayerControlFrameLayout.setProgress(z ? 1.0f : 0.0f);
            }
            GroupedPhotosListView groupedPhotosListView2 = this.groupedPhotosListView;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView2, property4, fArr4));
            if (this.captionTextViewSwitcher.getTag() != null) {
                CaptionTextViewSwitcher captionTextViewSwitcher2 = this.captionTextViewSwitcher;
                Property property5 = View.ALPHA;
                float[] fArr5 = new float[1];
                fArr5[0] = z ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(captionTextViewSwitcher2, property5, fArr5));
            }
            if (this.muteItem.getTag() != null) {
                ImageView imageView = this.muteItem;
                Property property6 = View.ALPHA;
                float[] fArr6 = new float[1];
                fArr6[0] = z ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(imageView, property6, fArr6));
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
        } else {
            this.actionBar.setAlpha(z ? 1.0f : 0.0f);
            this.bottomLayout.setAlpha(z ? 1.0f : 0.0f);
            this.groupedPhotosListView.setAlpha(z ? 1.0f : 0.0f);
            this.captionTextViewSwitcher.setAlpha(z ? 1.0f : 0.0f);
            this.videoPlayerControlFrameLayout.setProgress(z ? 1.0f : 0.0f);
            if (this.muteItem.getTag() != null) {
                this.muteItem.setAlpha(z ? 1.0f : 0.0f);
            }
        }
        if (!this.videoPlayerControlVisible || !this.isPlaying) {
            AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
            return;
        }
        if (z) {
            AndroidUtilities.runOnUIThread(this.hideActionBarRunnable, 3000);
        } else {
            AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
        }
        PhotoProgressView photoProgressView = this.photoProgressViews[0];
        if (!z) {
            f = 0.0f;
        }
        photoProgressView.setIndexedAlpha(1, f, z2);
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
                    AndroidUtilities.runOnUIThread(this.hideActionBarRunnable, 1000);
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
                private final /* synthetic */ boolean f$0;
                private final /* synthetic */ View f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    PhotoViewer.lambda$setItemVisible$47(this.f$0, this.f$1);
                }
            }).start();
        }
    }

    static /* synthetic */ void lambda$setItemVisible$47(boolean z, View view) {
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
        this.checkImageView.setVisibility(8);
        this.actionBar.setTitleRightMargin(0);
        this.photosCounterView.setAlpha(1.0f);
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
                    setItemVisible(this.sendItem, true, false);
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
                setItemVisible(this.sendItem, true, false);
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

    /* JADX WARNING: Removed duplicated region for block: B:255:0x05f0  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x069c  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x074b  */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x0772  */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x0798  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x07a7  */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x07c3  */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x07eb  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0856  */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x0858  */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x0860  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0862  */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x086a  */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x086c  */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x0875  */
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
            if (r3 != 0) goto L_0x0499
            int r1 = r0.switchingToIndex
            if (r1 < 0) goto L_0x0498
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            int r3 = r3.size()
            if (r1 < r3) goto L_0x0033
            goto L_0x0498
        L_0x0033:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r3 = r0.switchingToIndex
            java.lang.Object r1 = r1.get(r3)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            boolean r3 = r1.isVideo()
            boolean r5 = r1.isInvoice()
            r10 = 11
            if (r5 == 0) goto L_0x0079
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
            goto L_0x0212
        L_0x0079:
            boolean r18 = r1.isNewGif()
            if (r18 == 0) goto L_0x0086
            org.telegram.ui.ActionBar.ActionBarMenuItem r15 = r0.menuItem
            r8 = 14
            r15.showSubItem(r8)
        L_0x0086:
            org.telegram.ui.ChatActivity r8 = r0.parentChatActivity
            if (r8 == 0) goto L_0x0092
            boolean r8 = r8.isInScheduleMode()
            if (r8 == 0) goto L_0x0092
            r8 = 1
            goto L_0x0093
        L_0x0092:
            r8 = 0
        L_0x0093:
            boolean r8 = r1.canDeleteMessage(r8, r6)
            if (r8 == 0) goto L_0x00a3
            int r8 = r0.slideshowMessageId
            if (r8 != 0) goto L_0x00a3
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.menuItem
            r8.showSubItem(r13)
            goto L_0x00a8
        L_0x00a3:
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.menuItem
            r8.hideSubItem(r13)
        L_0x00a8:
            if (r3 == 0) goto L_0x00f7
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.menuItem
            r8.showSubItem(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.masksItem
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x00b9
            r8 = 1
            goto L_0x00ba
        L_0x00b9:
            r8 = 0
        L_0x00ba:
            if (r8 == 0) goto L_0x00c1
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.masksItem
            r0.setItemVisible(r10, r4, r4)
        L_0x00c1:
            boolean r10 = r0.pipAvailable
            if (r10 != 0) goto L_0x00d3
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.pipItem
            r10.setEnabled(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.pipItem
            r8 = r8 ^ r11
            r15 = 1056964608(0x3var_, float:0.5)
            r0.setItemVisible(r10, r11, r8, r15)
            goto L_0x00d9
        L_0x00d3:
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.pipItem
            r8 = r8 ^ r11
            r0.setItemVisible(r10, r11, r8)
        L_0x00d9:
            boolean r8 = r1.hasAttachedStickers()
            r10 = 15
            if (r8 == 0) goto L_0x00ef
            r8 = r7
            long r6 = r1.getDialogId()
            int r7 = (int) r6
            if (r7 == 0) goto L_0x00f0
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.menuItem
            r6.showSubItem(r10)
            goto L_0x00f5
        L_0x00ef:
            r8 = r7
        L_0x00f0:
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.menuItem
            r6.hideSubItem(r10)
        L_0x00f5:
            r10 = r14
            goto L_0x0128
        L_0x00f7:
            r8 = r7
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.menuItem
            r6.hideSubItem(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.pipItem
            int r6 = r6.getVisibility()
            if (r6 != 0) goto L_0x0107
            r6 = 1
            goto L_0x0108
        L_0x0107:
            r6 = 0
        L_0x0108:
            boolean r7 = r1.hasAttachedStickers()
            r10 = r14
            if (r7 == 0) goto L_0x0118
            long r13 = r1.getDialogId()
            int r14 = (int) r13
            if (r14 == 0) goto L_0x0118
            r13 = 1
            goto L_0x0119
        L_0x0118:
            r13 = 0
        L_0x0119:
            if (r6 == 0) goto L_0x0122
            org.telegram.ui.ActionBar.ActionBarMenuItem r14 = r0.pipItem
            r7 = r13 ^ 1
            r0.setItemVisible(r14, r4, r7)
        L_0x0122:
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.masksItem
            r6 = r6 ^ r11
            r0.setItemVisible(r7, r13, r6)
        L_0x0128:
            boolean r6 = r0.shouldMessageObjectAutoPlayed(r1)
            if (r6 != 0) goto L_0x0210
            boolean r6 = r0.playerWasPlaying
            r6 = r6 ^ r11
            java.lang.String r7 = r0.nameOverride
            if (r7 == 0) goto L_0x013c
            org.telegram.ui.Components.FadingTextViewLayout r10 = r0.nameTextView
            r10.setText(r7)
            goto L_0x01ad
        L_0x013c:
            boolean r7 = r1.isFromUser()
            if (r7 == 0) goto L_0x0166
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            org.telegram.tgnet.TLRPC$Message r13 = r1.messageOwner
            int r13 = r13.from_id
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r13)
            if (r7 == 0) goto L_0x0160
            org.telegram.ui.Components.FadingTextViewLayout r10 = r0.nameTextView
            java.lang.String r7 = org.telegram.messenger.UserObject.getUserName(r7)
            r10.setText(r7, r6)
            goto L_0x01ad
        L_0x0160:
            org.telegram.ui.Components.FadingTextViewLayout r7 = r0.nameTextView
            r7.setText(r10, r6)
            goto L_0x01ad
        L_0x0166:
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            org.telegram.tgnet.TLRPC$Message r13 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r13 = r13.to_id
            int r13 = r13.channel_id
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getChat(r13)
            boolean r13 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r13 == 0) goto L_0x019e
            boolean r13 = r7.megagroup
            if (r13 == 0) goto L_0x019e
            boolean r13 = r1.isForwardedChannelPost()
            if (r13 == 0) goto L_0x019e
            int r7 = r0.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            org.telegram.tgnet.TLRPC$Message r13 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r13 = r13.fwd_from
            int r13 = r13.channel_id
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getChat(r13)
        L_0x019e:
            if (r7 == 0) goto L_0x01a8
            org.telegram.ui.Components.FadingTextViewLayout r10 = r0.nameTextView
            java.lang.String r7 = r7.title
            r10.setText(r7, r6)
            goto L_0x01ad
        L_0x01a8:
            org.telegram.ui.Components.FadingTextViewLayout r7 = r0.nameTextView
            r7.setText(r10, r6)
        L_0x01ad:
            int r7 = r0.dateOverride
            if (r7 == 0) goto L_0x01b2
            goto L_0x01b6
        L_0x01b2:
            org.telegram.tgnet.TLRPC$Message r7 = r1.messageOwner
            int r7 = r7.date
        L_0x01b6:
            long r13 = (long) r7
            r19 = 1000(0x3e8, double:4.94E-321)
            long r13 = r13 * r19
            r10 = 2
            java.lang.Object[] r15 = new java.lang.Object[r10]
            org.telegram.messenger.LocaleController r10 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r10 = r10.formatterYear
            java.util.Date r7 = new java.util.Date
            r7.<init>(r13)
            java.lang.String r7 = r10.format((java.util.Date) r7)
            r15[r4] = r7
            org.telegram.messenger.LocaleController r7 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r7 = r7.formatterDay
            java.util.Date r10 = new java.util.Date
            r10.<init>(r13)
            java.lang.String r7 = r7.format((java.util.Date) r10)
            r15[r11] = r7
            java.lang.String r7 = "formatDateAtTime"
            r10 = 2131627398(0x7f0e0d86, float:1.888206E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r10, r15)
            if (r2 == 0) goto L_0x020b
            if (r3 == 0) goto L_0x020b
            org.telegram.ui.Components.FadingTextViewLayout r2 = r0.dateTextView
            r3 = 2
            java.lang.Object[] r10 = new java.lang.Object[r3]
            r10[r4] = r7
            org.telegram.tgnet.TLRPC$Document r3 = r1.getDocument()
            int r3 = r3.size
            long r13 = (long) r3
            java.lang.String r3 = org.telegram.messenger.AndroidUtilities.formatFileSize(r13)
            r10[r11] = r3
            java.lang.String r3 = "%s (%s)"
            java.lang.String r3 = java.lang.String.format(r3, r10)
            r2.setText(r3, r6)
            goto L_0x0210
        L_0x020b:
            org.telegram.ui.Components.FadingTextViewLayout r2 = r0.dateTextView
            r2.setText(r7, r6)
        L_0x0210:
            java.lang.CharSequence r2 = r1.caption
        L_0x0212:
            org.telegram.ui.Components.AnimatedFileDrawable r3 = r0.currentAnimation
            if (r3 == 0) goto L_0x0253
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r3.hideSubItem(r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r5 = 10
            r3.hideSubItem(r5)
            org.telegram.ui.ChatActivity r3 = r0.parentChatActivity
            if (r3 == 0) goto L_0x022e
            boolean r3 = r3.isInScheduleMode()
            if (r3 == 0) goto L_0x022e
            r3 = 1
            goto L_0x022f
        L_0x022e:
            r3 = 0
        L_0x022f:
            r5 = 0
            boolean r3 = r1.canDeleteMessage(r3, r5)
            if (r3 != 0) goto L_0x023c
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r5 = 6
            r3.hideSubItem(r5)
        L_0x023c:
            r0.allowShare = r11
            android.widget.ImageView r3 = r0.shareButton
            r3.setVisibility(r4)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r4 = 2131624289(0x7f0e0161, float:1.8875754E38)
            java.lang.String r5 = "AttachGif"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setTitle(r4)
            goto L_0x048e
        L_0x0253:
            int r3 = r0.totalImagesCount
            int r6 = r0.totalImagesCountMerge
            int r3 = r3 + r6
            if (r3 == 0) goto L_0x03c8
            boolean r3 = r0.needSearchImageInArr
            if (r3 != 0) goto L_0x03c8
            boolean r3 = r0.opennedFromMedia
            if (r3 == 0) goto L_0x0317
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            int r3 = r3.size()
            int r5 = r0.totalImagesCount
            int r6 = r0.totalImagesCountMerge
            int r5 = r5 + r6
            if (r3 >= r5) goto L_0x02f2
            boolean r3 = r0.loadingMoreImages
            if (r3 != 0) goto L_0x02f2
            int r3 = r0.switchingToIndex
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            int r5 = r5.size()
            r6 = 5
            int r5 = r5 - r6
            if (r3 <= r5) goto L_0x02f2
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0289
            r3 = 0
            goto L_0x029a
        L_0x0289:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            int r5 = r3.size()
            int r5 = r5 - r11
            java.lang.Object r3 = r3.get(r5)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            int r3 = r3.getId()
        L_0x029a:
            boolean[] r5 = r0.endReached
            boolean r5 = r5[r4]
            if (r5 == 0) goto L_0x02cf
            long r5 = r0.mergeDialogId
            r7 = 0
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x02cf
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x02cb
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            int r6 = r5.size()
            int r6 = r6 - r11
            java.lang.Object r5 = r5.get(r6)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            long r5 = r5.getDialogId()
            long r7 = r0.mergeDialogId
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x02cb
            r3 = 1
            r23 = 0
            goto L_0x02d2
        L_0x02cb:
            r23 = r3
            r3 = 1
            goto L_0x02d2
        L_0x02cf:
            r23 = r3
            r3 = 0
        L_0x02d2:
            int r5 = r0.currentAccount
            org.telegram.messenger.MediaDataController r19 = org.telegram.messenger.MediaDataController.getInstance(r5)
            if (r3 != 0) goto L_0x02dd
            long r5 = r0.currentDialogId
            goto L_0x02df
        L_0x02dd:
            long r5 = r0.mergeDialogId
        L_0x02df:
            r20 = r5
            r22 = 40
            int r3 = r0.sharedMediaType
            r25 = 1
            int r5 = r0.classGuid
            r24 = r3
            r26 = r5
            r19.loadMedia(r20, r22, r23, r24, r25, r26)
            r0.loadingMoreImages = r11
        L_0x02f2:
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
            r6 = 2131626010(0x7f0e081a, float:1.8879244E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r6, r5)
            r3.setTitle(r5)
            goto L_0x0439
        L_0x0317:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            int r3 = r3.size()
            int r5 = r0.totalImagesCount
            int r6 = r0.totalImagesCountMerge
            int r5 = r5 + r6
            if (r3 >= r5) goto L_0x0396
            boolean r3 = r0.loadingMoreImages
            if (r3 != 0) goto L_0x0396
            int r3 = r0.switchingToIndex
            r5 = 5
            if (r3 >= r5) goto L_0x0396
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0337
            r3 = 0
            goto L_0x0343
        L_0x0337:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.imagesArr
            java.lang.Object r3 = r3.get(r4)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            int r3 = r3.getId()
        L_0x0343:
            boolean[] r5 = r0.endReached
            boolean r5 = r5[r4]
            if (r5 == 0) goto L_0x0373
            long r5 = r0.mergeDialogId
            r7 = 0
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x0373
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x036f
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.imagesArr
            java.lang.Object r5 = r5.get(r4)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            long r5 = r5.getDialogId()
            long r7 = r0.mergeDialogId
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x036f
            r3 = 1
            r23 = 0
            goto L_0x0376
        L_0x036f:
            r23 = r3
            r3 = 1
            goto L_0x0376
        L_0x0373:
            r23 = r3
            r3 = 0
        L_0x0376:
            int r5 = r0.currentAccount
            org.telegram.messenger.MediaDataController r19 = org.telegram.messenger.MediaDataController.getInstance(r5)
            if (r3 != 0) goto L_0x0381
            long r5 = r0.currentDialogId
            goto L_0x0383
        L_0x0381:
            long r5 = r0.mergeDialogId
        L_0x0383:
            r20 = r5
            r22 = 80
            int r3 = r0.sharedMediaType
            r25 = 1
            int r5 = r0.classGuid
            r24 = r3
            r26 = r5
            r19.loadMedia(r20, r22, r23, r24, r25, r26)
            r0.loadingMoreImages = r11
        L_0x0396:
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
            r6 = 2131626010(0x7f0e081a, float:1.8879244E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r6, r5)
            r3.setTitle(r5)
            goto L_0x0439
        L_0x03c8:
            int r3 = r0.slideshowMessageId
            r6 = 2131624287(0x7f0e015f, float:1.887575E38)
            java.lang.String r7 = "AttachDocument"
            if (r3 != 0) goto L_0x0409
            org.telegram.tgnet.TLRPC$Message r3 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r3 == 0) goto L_0x0409
            boolean r3 = r1.canPreviewDocument()
            if (r3 == 0) goto L_0x03e9
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r3.setTitle(r5)
            goto L_0x0439
        L_0x03e9:
            boolean r3 = r1.isVideo()
            if (r3 == 0) goto L_0x03fc
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r5 = 2131624306(0x7f0e0172, float:1.8875788E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r3.setTitle(r5)
            goto L_0x0439
        L_0x03fc:
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r5 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.setTitle(r5)
            goto L_0x0439
        L_0x0409:
            if (r5 == 0) goto L_0x0417
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            org.telegram.tgnet.TLRPC$Message r5 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            java.lang.String r5 = r5.title
            r3.setTitle(r5)
            goto L_0x0439
        L_0x0417:
            boolean r3 = r1.isVideo()
            if (r3 == 0) goto L_0x042a
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r5 = 2131624306(0x7f0e0172, float:1.8875788E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r3.setTitle(r5)
            goto L_0x0439
        L_0x042a:
            org.telegram.tgnet.TLRPC$Document r3 = r1.getDocument()
            if (r3 == 0) goto L_0x0439
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r3.setTitle(r5)
        L_0x0439:
            long r5 = r0.currentDialogId
            int r3 = (int) r5
            if (r3 != 0) goto L_0x0443
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.sendItem
            r0.setItemVisible(r3, r4, r4)
        L_0x0443:
            org.telegram.tgnet.TLRPC$Message r3 = r1.messageOwner
            int r3 = r3.ttl
            if (r3 == 0) goto L_0x0463
            r5 = 3600(0xe10, float:5.045E-42)
            if (r3 >= r5) goto L_0x0463
            r0.allowShare = r4
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r3.hideSubItem(r11)
            android.widget.ImageView r3 = r0.shareButton
            r4 = 8
            r3.setVisibility(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r4 = 10
            r3.hideSubItem(r4)
            goto L_0x048e
        L_0x0463:
            r0.allowShare = r11
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r3.showSubItem(r11)
            android.widget.ImageView r3 = r0.shareButton
            boolean r5 = r0.videoPlayerControlVisible
            if (r5 != 0) goto L_0x0472
            r11 = 0
            goto L_0x0474
        L_0x0472:
            r11 = 8
        L_0x0474:
            r3.setVisibility(r11)
            android.widget.ImageView r3 = r0.shareButton
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x0487
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r4 = 10
            r3.hideSubItem(r4)
            goto L_0x048e
        L_0x0487:
            r4 = 10
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.menuItem
            r3.showSubItem(r4)
        L_0x048e:
            org.telegram.ui.Components.GroupedPhotosListView r3 = r0.groupedPhotosListView
            r3.fillList()
            r6 = r31
            r15 = r1
            goto L_0x0880
        L_0x0498:
            return
        L_0x0499:
            r8 = r7
            r10 = r14
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r2 = r0.secureDocuments
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x04db
            r0.allowShare = r4
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.hideSubItem(r11)
            org.telegram.ui.Components.FadingTextViewLayout r1 = r0.nameTextView
            r1.setText(r10)
            org.telegram.ui.Components.FadingTextViewLayout r1 = r0.dateTextView
            r1.setText(r10)
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
            r3 = 2131626010(0x7f0e081a, float:1.8879244E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r3, r2)
            r1.setTitle(r2)
        L_0x04d8:
            r15 = 0
            goto L_0x087d
        L_0x04db:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r2 = r0.imagesArrLocations
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0584
            if (r1 < 0) goto L_0x0583
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r2 = r0.imagesArrLocations
            int r2 = r2.size()
            if (r1 < r2) goto L_0x04ef
            goto L_0x0583
        L_0x04ef:
            org.telegram.ui.Components.FadingTextViewLayout r1 = r0.nameTextView
            r1.setText(r10)
            org.telegram.ui.Components.FadingTextViewLayout r1 = r0.dateTextView
            r1.setText(r10)
            int r1 = r0.avatarsDialogId
            int r2 = r0.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            int r2 = r2.getClientUserId()
            if (r1 != r2) goto L_0x0516
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r1 = r0.avatarsArr
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0516
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r2 = 6
            r1.showSubItem(r2)
            goto L_0x051c
        L_0x0516:
            r2 = 6
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.hideSubItem(r2)
        L_0x051c:
            boolean r1 = r0.isEvent
            if (r1 == 0) goto L_0x052d
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r2 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r1.setTitle(r2)
            goto L_0x0551
        L_0x052d:
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
            r3 = 2131626010(0x7f0e081a, float:1.8879244E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r3, r2)
            r1.setTitle(r2)
        L_0x0551:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.showSubItem(r11)
            r0.allowShare = r11
            android.widget.ImageView r1 = r0.shareButton
            boolean r2 = r0.videoPlayerControlVisible
            if (r2 != 0) goto L_0x0560
            r11 = 0
            goto L_0x0562
        L_0x0560:
            r11 = 8
        L_0x0562:
            r1.setVisibility(r11)
            android.widget.ImageView r1 = r0.shareButton
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0575
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r2 = 10
            r1.hideSubItem(r2)
            goto L_0x057c
        L_0x0575:
            r2 = 10
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.showSubItem(r2)
        L_0x057c:
            org.telegram.ui.Components.GroupedPhotosListView r1 = r0.groupedPhotosListView
            r1.fillList()
            goto L_0x04d8
        L_0x0583:
            return
        L_0x0584:
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x04d8
            if (r1 < 0) goto L_0x087c
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            int r2 = r2.size()
            if (r1 < r2) goto L_0x0598
            goto L_0x087c
        L_0x0598:
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            java.lang.Object r1 = r2.get(r1)
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$BotInlineResult
            r3 = 4
            if (r2 == 0) goto L_0x05cb
            r2 = r1
            org.telegram.tgnet.TLRPC$BotInlineResult r2 = (org.telegram.tgnet.TLRPC$BotInlineResult) r2
            r0.currentBotInlineResult = r2
            org.telegram.tgnet.TLRPC$Document r5 = r2.document
            if (r5 == 0) goto L_0x05b1
            boolean r2 = org.telegram.messenger.MessageObject.isVideoDocument(r5)
            goto L_0x05c1
        L_0x05b1:
            org.telegram.tgnet.TLRPC$WebDocument r5 = r2.content
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r5 == 0) goto L_0x05c0
            java.lang.String r2 = r2.type
            java.lang.String r5 = "video"
            boolean r2 = r2.equals(r5)
            goto L_0x05c1
        L_0x05c0:
            r2 = 0
        L_0x05c1:
            r6 = r31
        L_0x05c3:
            r5 = 0
            r10 = 0
            r13 = 0
            r14 = 0
            r19 = 0
            goto L_0x078e
        L_0x05cb:
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.PhotoEntry
            if (r2 == 0) goto L_0x05d7
            r5 = r1
            org.telegram.messenger.MediaController$PhotoEntry r5 = (org.telegram.messenger.MediaController.PhotoEntry) r5
            java.lang.String r6 = r5.path
            boolean r5 = r5.isVideo
            goto L_0x05ed
        L_0x05d7:
            boolean r5 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r5 == 0) goto L_0x05eb
            r5 = r1
            org.telegram.messenger.MediaController$SearchImage r5 = (org.telegram.messenger.MediaController.SearchImage) r5
            java.lang.String r6 = r5.getPathToAttach()
            int r5 = r5.type
            if (r5 != r11) goto L_0x05e9
            r5 = 0
            r7 = 1
            goto L_0x05ee
        L_0x05e9:
            r5 = 0
            goto L_0x05ed
        L_0x05eb:
            r5 = 0
            r6 = 0
        L_0x05ed:
            r7 = 0
        L_0x05ee:
            if (r5 == 0) goto L_0x069c
            boolean r7 = r0.isCurrentVideo
            if (r7 != 0) goto L_0x05f6
            r7 = 0
            goto L_0x05f8
        L_0x05f6:
            r7 = r31
        L_0x05f8:
            r0.isCurrentVideo = r11
            r28.updateAccessibilityOverlayVisibility()
            r10 = 0
            r13 = 1065353216(0x3var_, float:1.0)
            if (r2 == 0) goto L_0x0610
            r14 = r1
            org.telegram.messenger.MediaController$PhotoEntry r14 = (org.telegram.messenger.MediaController.PhotoEntry) r14
            org.telegram.messenger.VideoEditedInfo r14 = r14.editedInfo
            if (r14 == 0) goto L_0x0610
            boolean r10 = r14.muted
            float r13 = r14.start
            float r14 = r14.end
            goto L_0x0614
        L_0x0610:
            r10 = 0
            r13 = 0
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x0614:
            r0.processOpenVideo(r6, r10, r13, r14)
            boolean r6 = r0.isDocumentsPicker
            if (r6 == 0) goto L_0x0633
            org.telegram.ui.Components.VideoTimelinePlayView r6 = r0.videoTimelineView
            r10 = 8
            r6.setVisibility(r10)
            android.widget.ImageView r6 = r0.muteItem
            r6.setVisibility(r10)
            android.widget.ImageView r6 = r0.muteItem
            r13 = 0
            r6.setTag(r13)
            android.widget.ImageView r6 = r0.compressItem
            r6.setVisibility(r10)
            goto L_0x064b
        L_0x0633:
            org.telegram.ui.Components.VideoTimelinePlayView r6 = r0.videoTimelineView
            r6.setVisibility(r4)
            android.widget.ImageView r6 = r0.muteItem
            r6.setVisibility(r4)
            android.widget.ImageView r6 = r0.muteItem
            java.lang.Integer r10 = java.lang.Integer.valueOf(r11)
            r6.setTag(r10)
            android.widget.ImageView r6 = r0.compressItem
            r6.setVisibility(r4)
        L_0x064b:
            android.widget.ImageView r6 = r0.cropItem
            r10 = 8
            r6.setVisibility(r10)
            boolean r6 = r0.isDocumentsPicker
            if (r6 != 0) goto L_0x067d
            int r6 = android.os.Build.VERSION.SDK_INT
            r10 = 18
            if (r6 >= r10) goto L_0x065d
            goto L_0x067d
        L_0x065d:
            android.widget.ImageView r6 = r0.tuneItem
            r6.setVisibility(r4)
            android.widget.ImageView r6 = r0.tuneItem
            java.lang.Integer r10 = java.lang.Integer.valueOf(r11)
            r6.setTag(r10)
            android.widget.ImageView r6 = r0.paintItem
            r6.setVisibility(r4)
            android.widget.ImageView r6 = r0.paintItem
            java.lang.Integer r10 = java.lang.Integer.valueOf(r11)
            r6.setTag(r10)
            r10 = 8
            r13 = 0
            goto L_0x0694
        L_0x067d:
            android.widget.ImageView r6 = r0.tuneItem
            r10 = 8
            r6.setVisibility(r10)
            android.widget.ImageView r6 = r0.tuneItem
            r13 = 0
            r6.setTag(r13)
            android.widget.ImageView r6 = r0.paintItem
            r6.setVisibility(r10)
            android.widget.ImageView r6 = r0.paintItem
            r6.setTag(r13)
        L_0x0694:
            android.widget.ImageView r6 = r0.rotateItem
            r6.setVisibility(r10)
            r6 = r7
            goto L_0x0749
        L_0x069c:
            r10 = 8
            r13 = 0
            org.telegram.ui.Components.VideoTimelinePlayView r6 = r0.videoTimelineView
            r6.setVisibility(r10)
            android.widget.ImageView r6 = r0.muteItem
            r6.setVisibility(r10)
            android.widget.ImageView r6 = r0.muteItem
            r6.setTag(r13)
            boolean r6 = r0.isCurrentVideo
            if (r6 == 0) goto L_0x06b4
            r6 = 0
            goto L_0x06b6
        L_0x06b4:
            r6 = r31
        L_0x06b6:
            r0.isCurrentVideo = r4
            r28.updateAccessibilityOverlayVisibility()
            android.widget.ImageView r13 = r0.compressItem
            r13.setVisibility(r10)
            if (r7 != 0) goto L_0x0723
            int r7 = r0.sendPhotoType
            r10 = 10
            if (r7 == r10) goto L_0x0723
            boolean r10 = r0.isDocumentsPicker
            if (r10 == 0) goto L_0x06cd
            goto L_0x0723
        L_0x06cd:
            if (r7 == r3) goto L_0x06f0
            r10 = 5
            if (r7 != r10) goto L_0x06d3
            goto L_0x06f0
        L_0x06d3:
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
            goto L_0x0707
        L_0x06f0:
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
        L_0x0707:
            android.widget.ImageView r7 = r0.cropItem
            int r10 = r0.sendPhotoType
            if (r10 == r11) goto L_0x070f
            r10 = 0
            goto L_0x0711
        L_0x070f:
            r10 = 8
        L_0x0711:
            r7.setVisibility(r10)
            android.widget.ImageView r7 = r0.rotateItem
            int r10 = r0.sendPhotoType
            if (r10 == r11) goto L_0x071d
            r10 = 8
            goto L_0x071e
        L_0x071d:
            r10 = 0
        L_0x071e:
            r7.setVisibility(r10)
            r13 = 0
            goto L_0x0744
        L_0x0723:
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
        L_0x0744:
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            r7.setSubtitle(r13)
        L_0x0749:
            if (r2 == 0) goto L_0x0772
            r2 = r1
            org.telegram.messenger.MediaController$PhotoEntry r2 = (org.telegram.messenger.MediaController.PhotoEntry) r2
            int r7 = r2.bucketId
            if (r7 != 0) goto L_0x0764
            long r13 = r2.dateTaken
            r16 = 0
            int r7 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r7 != 0) goto L_0x0764
            java.util.ArrayList<java.lang.Object> r7 = r0.imagesArrLocals
            int r7 = r7.size()
            if (r7 != r11) goto L_0x0764
            r7 = 1
            goto L_0x0765
        L_0x0764:
            r7 = 0
        L_0x0765:
            r0.fromCamera = r7
            java.lang.CharSequence r7 = r2.caption
            int r10 = r2.ttl
            boolean r13 = r2.isFiltered
            boolean r14 = r2.isPainted
            boolean r2 = r2.isCropped
            goto L_0x0783
        L_0x0772:
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r2 == 0) goto L_0x078b
            r2 = r1
            org.telegram.messenger.MediaController$SearchImage r2 = (org.telegram.messenger.MediaController.SearchImage) r2
            java.lang.CharSequence r7 = r2.caption
            int r10 = r2.ttl
            boolean r13 = r2.isFiltered
            boolean r14 = r2.isPainted
            boolean r2 = r2.isCropped
        L_0x0783:
            r19 = r7
            r27 = r5
            r5 = r2
            r2 = r27
            goto L_0x078e
        L_0x078b:
            r2 = r5
            goto L_0x05c3
        L_0x078e:
            android.widget.FrameLayout r7 = r0.bottomLayout
            int r7 = r7.getVisibility()
            r15 = 8
            if (r7 == r15) goto L_0x079d
            android.widget.FrameLayout r7 = r0.bottomLayout
            r7.setVisibility(r15)
        L_0x079d:
            android.widget.FrameLayout r7 = r0.bottomLayout
            r15 = 0
            r7.setTag(r15)
            boolean r7 = r0.fromCamera
            if (r7 == 0) goto L_0x07c3
            if (r2 == 0) goto L_0x07b6
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r7 = 2131624306(0x7f0e0172, float:1.8875788E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r2.setTitle(r7)
            goto L_0x07e7
        L_0x07b6:
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r7 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r2.setTitle(r7)
            goto L_0x07e7
        L_0x07c3:
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r7 = 2
            java.lang.Object[] r8 = new java.lang.Object[r7]
            int r7 = r0.switchingToIndex
            int r7 = r7 + r11
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r8[r4] = r7
            java.util.ArrayList<java.lang.Object> r7 = r0.imagesArrLocals
            int r7 = r7.size()
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r8[r11] = r7
            r7 = 2131626010(0x7f0e081a, float:1.8879244E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r12, r7, r8)
            r2.setTitle(r7)
        L_0x07e7:
            org.telegram.ui.ChatActivity r2 = r0.parentChatActivity
            if (r2 == 0) goto L_0x0821
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getCurrentChat()
            if (r2 == 0) goto L_0x07f9
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            java.lang.String r2 = r2.title
            r7.setTitle(r2)
            goto L_0x0821
        L_0x07f9:
            org.telegram.ui.ChatActivity r2 = r0.parentChatActivity
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()
            if (r2 == 0) goto L_0x0821
            boolean r7 = r2.self
            if (r7 == 0) goto L_0x0814
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r7 = 2131626630(0x7f0e0a86, float:1.8880502E38)
            java.lang.String r8 = "SavedMessages"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r2.setTitle(r7)
            goto L_0x0821
        L_0x0814:
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            java.lang.String r8 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r8, r2)
            r7.setTitle(r2)
        L_0x0821:
            int r2 = r0.sendPhotoType
            if (r2 == 0) goto L_0x0835
            if (r2 == r3) goto L_0x0835
            r3 = 2
            if (r2 == r3) goto L_0x082d
            r3 = 5
            if (r2 != r3) goto L_0x0842
        L_0x082d:
            java.util.ArrayList<java.lang.Object> r2 = r0.imagesArrLocals
            int r2 = r2.size()
            if (r2 <= r11) goto L_0x0842
        L_0x0835:
            org.telegram.ui.Components.CheckBox r2 = r0.checkImageView
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r3 = r0.placeProvider
            int r7 = r0.switchingToIndex
            boolean r3 = r3.isPhotoChecked(r7)
            r2.setChecked(r3, r4)
        L_0x0842:
            r0.updateCaptionTextForCurrentPhoto(r1)
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r2 = "dialogFloatingButton"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r3)
            android.widget.ImageView r2 = r0.timeItem
            if (r10 == 0) goto L_0x0858
            r3 = r1
            goto L_0x0859
        L_0x0858:
            r3 = r15
        L_0x0859:
            r2.setColorFilter(r3)
            android.widget.ImageView r2 = r0.paintItem
            if (r14 == 0) goto L_0x0862
            r3 = r1
            goto L_0x0863
        L_0x0862:
            r3 = r15
        L_0x0863:
            r2.setColorFilter(r3)
            android.widget.ImageView r2 = r0.cropItem
            if (r5 == 0) goto L_0x086c
            r3 = r1
            goto L_0x086d
        L_0x086c:
            r3 = r15
        L_0x086d:
            r2.setColorFilter(r3)
            android.widget.ImageView r2 = r0.tuneItem
            if (r13 == 0) goto L_0x0875
            goto L_0x0876
        L_0x0875:
            r1 = r15
        L_0x0876:
            r2.setColorFilter(r1)
            r2 = r19
            goto L_0x0880
        L_0x087c:
            return
        L_0x087d:
            r6 = r31
            r2 = r15
        L_0x0880:
            r0.setCurrentCaption(r15, r2, r6)
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

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x007a, code lost:
        r4 = r0.currentMessageObject;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setImageIndex(int r19, boolean r20, boolean r21) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            int r2 = r0.currentIndex
            if (r2 == r1) goto L_0x0391
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r2 = r0.placeProvider
            if (r2 != 0) goto L_0x000e
            goto L_0x0391
        L_0x000e:
            r2 = 0
            if (r20 != 0) goto L_0x001a
            org.telegram.messenger.ImageReceiver$BitmapHolder r3 = r0.currentThumb
            if (r3 == 0) goto L_0x001a
            r3.release()
            r0.currentThumb = r2
        L_0x001a:
            java.lang.String[] r3 = r0.currentFileNames
            java.lang.String r4 = r18.getFileName(r19)
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
            r18.setIsAboutToSwitchToIndex(r19, r20, r21)
            r0.currentPaintPath = r2
            r0.currentMediaEntities = r2
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x00b6
            int r1 = r0.currentIndex
            if (r1 < 0) goto L_0x00b2
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.imagesArr
            int r4 = r4.size()
            if (r1 < r4) goto L_0x006e
            goto L_0x00b2
        L_0x006e:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.imagesArr
            int r4 = r0.currentIndex
            java.lang.Object r1 = r1.get(r4)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r20 == 0) goto L_0x008a
            org.telegram.messenger.MessageObject r4 = r0.currentMessageObject
            if (r4 == 0) goto L_0x008a
            int r4 = r4.getId()
            int r10 = r1.getId()
            if (r4 != r10) goto L_0x008a
            r4 = 1
            goto L_0x008b
        L_0x008a:
            r4 = 0
        L_0x008b:
            r0.currentMessageObject = r1
            boolean r10 = r1.isVideo()
            int r11 = r0.sharedMediaType
            if (r11 != r6) goto L_0x00ae
            boolean r1 = r1.canPreviewDocument()
            r0.canZoom = r1
            if (r1 == 0) goto L_0x00a6
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.showSubItem(r6)
            r0.setDoubleTapEnabled(r6)
            goto L_0x00ae
        L_0x00a6:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.menuItem
            r1.hideSubItem(r6)
            r0.setDoubleTapEnabled(r5)
        L_0x00ae:
            r11 = r2
            r13 = r11
            goto L_0x026b
        L_0x00b2:
            r0.closePhoto(r5, r5)
            return
        L_0x00b6:
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r4 = r0.secureDocuments
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x00d9
            if (r1 < 0) goto L_0x00d5
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r4 = r0.secureDocuments
            int r4 = r4.size()
            if (r1 < r4) goto L_0x00c9
            goto L_0x00d5
        L_0x00c9:
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r4 = r0.secureDocuments
            java.lang.Object r1 = r4.get(r1)
            org.telegram.messenger.SecureDocument r1 = (org.telegram.messenger.SecureDocument) r1
            r0.currentSecureDocument = r1
            goto L_0x0267
        L_0x00d5:
            r0.closePhoto(r5, r5)
            return
        L_0x00d9:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r4 = r0.imagesArrLocations
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x0124
            if (r1 < 0) goto L_0x0120
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r4 = r0.imagesArrLocations
            int r4 = r4.size()
            if (r1 < r4) goto L_0x00ec
            goto L_0x0120
        L_0x00ec:
            org.telegram.messenger.ImageLocation r4 = r0.currentFileLocation
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r10 = r0.imagesArrLocations
            java.lang.Object r10 = r10.get(r1)
            org.telegram.messenger.ImageLocation r10 = (org.telegram.messenger.ImageLocation) r10
            if (r20 == 0) goto L_0x0110
            if (r4 == 0) goto L_0x0110
            if (r10 == 0) goto L_0x0110
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r4 = r4.location
            int r11 = r4.local_id
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r10 = r10.location
            int r12 = r10.local_id
            if (r11 != r12) goto L_0x0110
            long r11 = r4.volume_id
            long r13 = r10.volume_id
            int r4 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r4 != 0) goto L_0x0110
            r4 = 1
            goto L_0x0111
        L_0x0110:
            r4 = 0
        L_0x0111:
            java.util.ArrayList<org.telegram.messenger.ImageLocation> r10 = r0.imagesArrLocations
            java.lang.Object r1 = r10.get(r1)
            org.telegram.messenger.ImageLocation r1 = (org.telegram.messenger.ImageLocation) r1
            r0.currentFileLocation = r1
            r11 = r2
            r13 = r11
            r10 = 0
            goto L_0x026b
        L_0x0120:
            r0.closePhoto(r5, r5)
            return
        L_0x0124:
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x0267
            if (r1 < 0) goto L_0x0263
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            int r4 = r4.size()
            if (r1 < r4) goto L_0x0138
            goto L_0x0263
        L_0x0138:
            java.util.ArrayList<java.lang.Object> r4 = r0.imagesArrLocals
            java.lang.Object r1 = r4.get(r1)
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$BotInlineResult
            if (r4 == 0) goto L_0x018d
            org.telegram.tgnet.TLRPC$BotInlineResult r1 = (org.telegram.tgnet.TLRPC$BotInlineResult) r1
            r0.currentBotInlineResult = r1
            org.telegram.tgnet.TLRPC$Document r4 = r1.document
            if (r4 == 0) goto L_0x015c
            java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r4)
            java.lang.String r4 = r4.getAbsolutePath()
            r0.currentPathObject = r4
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isVideoDocument(r1)
        L_0x015a:
            r10 = r1
            goto L_0x0189
        L_0x015c:
            org.telegram.tgnet.TLRPC$Photo r4 = r1.photo
            if (r4 == 0) goto L_0x0175
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.sizes
            int r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r4)
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1)
            java.lang.String r1 = r1.getAbsolutePath()
            r0.currentPathObject = r1
            goto L_0x0188
        L_0x0175:
            org.telegram.tgnet.TLRPC$WebDocument r4 = r1.content
            boolean r10 = r4 instanceof org.telegram.tgnet.TLRPC$TL_webDocument
            if (r10 == 0) goto L_0x0188
            java.lang.String r4 = r4.url
            r0.currentPathObject = r4
            java.lang.String r1 = r1.type
            java.lang.String r4 = "video"
            boolean r1 = r1.equals(r4)
            goto L_0x015a
        L_0x0188:
            r10 = 0
        L_0x0189:
            r11 = r2
            r13 = r11
            goto L_0x0261
        L_0x018d:
            boolean r4 = r1 instanceof org.telegram.messenger.MediaController.PhotoEntry
            if (r4 == 0) goto L_0x0231
            r4 = r1
            org.telegram.messenger.MediaController$PhotoEntry r4 = (org.telegram.messenger.MediaController.PhotoEntry) r4
            java.lang.String r10 = r4.path
            r0.currentPathObject = r10
            if (r10 != 0) goto L_0x019e
            r0.closePhoto(r5, r5)
            return
        L_0x019e:
            boolean r10 = r4.isVideo
            org.telegram.messenger.MediaController$SavedFilterState r11 = r4.savedFilterState
            java.lang.String r12 = r4.paintPath
            r0.currentPaintPath = r12
            long r12 = r4.averageDuration
            r0.currentAverageDuration = r12
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r12 = r4.mediaEntities
            r0.currentMediaEntities = r12
            java.io.File r12 = new java.io.File
            java.lang.String r13 = r4.path
            r12.<init>(r13)
            android.net.Uri r13 = android.net.Uri.fromFile(r12)
            boolean r14 = r0.isDocumentsPicker
            if (r14 == 0) goto L_0x0249
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            int r15 = r4.width
            java.lang.String r2 = ", "
            if (r15 == 0) goto L_0x01f2
            int r15 = r4.height
            if (r15 == 0) goto L_0x01f2
            int r15 = r14.length()
            if (r15 <= 0) goto L_0x01d5
            r14.append(r2)
        L_0x01d5:
            java.util.Locale r15 = java.util.Locale.US
            java.lang.Object[] r8 = new java.lang.Object[r7]
            int r9 = r4.width
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r8[r5] = r9
            int r9 = r4.height
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r8[r6] = r9
            java.lang.String r9 = "%dx%d"
            java.lang.String r8 = java.lang.String.format(r15, r9, r8)
            r14.append(r8)
        L_0x01f2:
            boolean r8 = r4.isVideo
            if (r8 == 0) goto L_0x0208
            int r8 = r14.length()
            if (r8 <= 0) goto L_0x01ff
            r14.append(r2)
        L_0x01ff:
            int r8 = r4.duration
            java.lang.String r8 = org.telegram.messenger.AndroidUtilities.formatShortDuration(r8)
            r14.append(r8)
        L_0x0208:
            long r8 = r4.size
            r16 = 0
            int r15 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r15 == 0) goto L_0x0222
            int r8 = r14.length()
            if (r8 <= 0) goto L_0x0219
            r14.append(r2)
        L_0x0219:
            long r8 = r4.size
            java.lang.String r2 = org.telegram.messenger.AndroidUtilities.formatFileSize(r8)
            r14.append(r2)
        L_0x0222:
            android.widget.TextView r2 = r0.docNameTextView
            java.lang.String r4 = r12.getName()
            r2.setText(r4)
            android.widget.TextView r2 = r0.docInfoTextView
            r2.setText(r14)
            goto L_0x0249
        L_0x0231:
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r2 == 0) goto L_0x0246
            r2 = r1
            org.telegram.messenger.MediaController$SearchImage r2 = (org.telegram.messenger.MediaController.SearchImage) r2
            java.lang.String r4 = r2.getPathToAttach()
            r0.currentPathObject = r4
            long r8 = r2.averageDuration
            r0.currentAverageDuration = r8
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r2 = r2.mediaEntities
            r0.currentMediaEntities = r2
        L_0x0246:
            r10 = 0
            r11 = 0
            r13 = 0
        L_0x0249:
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.MediaEditState
            if (r2 == 0) goto L_0x0261
            org.telegram.messenger.MediaController$MediaEditState r1 = (org.telegram.messenger.MediaController.MediaEditState) r1
            java.lang.String r2 = r1.filterPath
            if (r2 == 0) goto L_0x0256
            r0.currentImagePath = r2
            goto L_0x0261
        L_0x0256:
            java.lang.String r1 = r1.croppedPath
            if (r1 == 0) goto L_0x025d
            r0.currentImagePath = r1
            goto L_0x0261
        L_0x025d:
            java.lang.String r1 = r0.currentPathObject
            r0.currentImagePath = r1
        L_0x0261:
            r4 = 0
            goto L_0x026b
        L_0x0263:
            r0.closePhoto(r5, r5)
            return
        L_0x0267:
            r4 = 0
            r10 = 0
            r11 = 0
            r13 = 0
        L_0x026b:
            org.telegram.ui.PhotoViewer$PlaceProviderObject r1 = r0.currentPlaceObject
            if (r1 == 0) goto L_0x027b
            int r2 = r0.animationInProgress
            if (r2 != 0) goto L_0x0279
            org.telegram.messenger.ImageReceiver r1 = r1.imageReceiver
            r1.setVisible(r6, r6)
            goto L_0x027b
        L_0x0279:
            r0.showAfterAnimation = r1
        L_0x027b:
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r1 = r0.placeProvider
            org.telegram.messenger.MessageObject r2 = r0.currentMessageObject
            org.telegram.messenger.ImageLocation r8 = r0.currentFileLocation
            org.telegram.tgnet.TLRPC$FileLocation r8 = r0.getFileLocation(r8)
            int r9 = r0.currentIndex
            org.telegram.ui.PhotoViewer$PlaceProviderObject r1 = r1.getPlaceForPhoto(r2, r8, r9, r5)
            r0.currentPlaceObject = r1
            if (r1 == 0) goto L_0x029b
            int r2 = r0.animationInProgress
            if (r2 != 0) goto L_0x0299
            org.telegram.messenger.ImageReceiver r1 = r1.imageReceiver
            r1.setVisible(r5, r6)
            goto L_0x029b
        L_0x0299:
            r0.hideAfterAnimation = r1
        L_0x029b:
            if (r4 != 0) goto L_0x0309
            r0.draggingDown = r5
            r1 = 0
            r0.translationX = r1
            r0.translationY = r1
            r2 = 1065353216(0x3var_, float:1.0)
            r0.scale = r2
            r0.animateToX = r1
            r0.animateToY = r1
            r0.animateToScale = r2
            r8 = 0
            r0.animationStartTime = r8
            r4 = 0
            r0.imageMoveAnimation = r4
            r0.changeModeAnimation = r4
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r4 = r0.aspectRatioFrameLayout
            if (r4 == 0) goto L_0x02bf
            r8 = 4
            r4.setVisibility(r8)
        L_0x02bf:
            r0.pinchStartDistance = r1
            r0.pinchStartScale = r2
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
            r0.canDragDown = r6
            r0.changingPage = r5
            r0.switchImageAfterAnimation = r5
            int r1 = r0.sharedMediaType
            if (r1 == r6) goto L_0x0301
            java.util.ArrayList<java.lang.Object> r1 = r0.imagesArrLocals
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x02fe
            java.lang.String[] r1 = r0.currentFileNames
            r1 = r1[r5]
            if (r1 == 0) goto L_0x02fc
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r1 = r0.photoProgressViews
            r1 = r1[r5]
            int r1 = r1.backgroundState
            if (r1 == 0) goto L_0x02fc
            goto L_0x02fe
        L_0x02fc:
            r1 = 0
            goto L_0x02ff
        L_0x02fe:
            r1 = 1
        L_0x02ff:
            r0.canZoom = r1
        L_0x0301:
            float r1 = r0.scale
            r0.updateMinMax(r1)
            r0.releasePlayer(r5)
        L_0x0309:
            if (r10 == 0) goto L_0x0312
            if (r13 == 0) goto L_0x0312
            r0.isStreaming = r5
            r0.preparePlayer(r13, r5, r5, r11)
        L_0x0312:
            java.util.ArrayList<java.lang.Object> r1 = r0.imagesArrLocals
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x032b
            org.telegram.ui.Components.PaintingOverlay r1 = r0.paintingOverlay
            r1.setVisibility(r5)
            org.telegram.ui.Components.PaintingOverlay r1 = r0.paintingOverlay
            java.lang.String r2 = r0.currentPaintPath
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r4 = r0.currentMediaEntities
            boolean r8 = r0.isCurrentVideo
            r1.setData(r2, r4, r8, r5)
            goto L_0x0337
        L_0x032b:
            org.telegram.ui.Components.PaintingOverlay r1 = r0.paintingOverlay
            r2 = 8
            r1.setVisibility(r2)
            r1 = 0
            r0.currentPaintPath = r1
            r0.currentMediaEntities = r1
        L_0x0337:
            r1 = -1
            if (r3 != r1) goto L_0x0347
            r18.setImages()
            r1 = 0
        L_0x033e:
            r2 = 3
            if (r1 >= r2) goto L_0x0391
            r0.checkProgress(r1, r5, r5)
            int r1 = r1 + 1
            goto L_0x033e
        L_0x0347:
            r0.checkProgress(r5, r6, r5)
            int r1 = r0.currentIndex
            if (r3 <= r1) goto L_0x036f
            org.telegram.messenger.ImageReceiver r2 = r0.rightImage
            org.telegram.messenger.ImageReceiver r3 = r0.centerImage
            r0.rightImage = r3
            org.telegram.messenger.ImageReceiver r3 = r0.leftImage
            r0.centerImage = r3
            r0.leftImage = r2
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r3 = r0.photoProgressViews
            r4 = r3[r5]
            r8 = r3[r7]
            r3[r5] = r8
            r3[r7] = r4
            int r1 = r1 - r6
            r0.setIndexToImage(r2, r1)
            r0.checkProgress(r6, r6, r5)
            r0.checkProgress(r7, r6, r5)
            goto L_0x0391
        L_0x036f:
            if (r3 >= r1) goto L_0x0391
            org.telegram.messenger.ImageReceiver r2 = r0.leftImage
            org.telegram.messenger.ImageReceiver r3 = r0.centerImage
            r0.leftImage = r3
            org.telegram.messenger.ImageReceiver r3 = r0.rightImage
            r0.centerImage = r3
            r0.rightImage = r2
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r3 = r0.photoProgressViews
            r4 = r3[r5]
            r8 = r3[r6]
            r3[r5] = r8
            r3[r6] = r4
            int r1 = r1 + r6
            r0.setIndexToImage(r2, r1)
            r0.checkProgress(r6, r6, r5)
            r0.checkProgress(r7, r6, r5)
        L_0x0391:
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
                nextView.setSingleLine(false);
            }
            if (this.needCaptionLayout) {
                Point point = AndroidUtilities.displaySize;
                i2 = point.x > point.y ? 5 : 10;
            } else {
                i2 = Integer.MAX_VALUE;
            }
            if (maxLines != i2) {
                nextView.setMaxLines(i2);
                nextView.setEllipsize((TextUtils.TruncateAt) null);
            }
        } else if (nextView.getMaxLines() != 1) {
            nextView.setMaxLines(1);
            nextView.setSingleLine(true);
            nextView.setEllipsize(TextUtils.TruncateAt.END);
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
                                    PhotoViewer.AnonymousClass46.this.lambda$onDisappear$0$PhotoViewer$46(valueAnimator);
                                }
                            });
                        }
                        return onDisappear;
                    }

                    public /* synthetic */ void lambda$onDisappear$0$PhotoViewer$46(ValueAnimator valueAnimator) {
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
                                    PhotoViewer.AnonymousClass45.this.lambda$onAppear$0$PhotoViewer$45(valueAnimator);
                                }
                            });
                        }
                        return onAppear;
                    }

                    public /* synthetic */ void lambda$onAppear$0$PhotoViewer$45(ValueAnimator valueAnimator) {
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
                                        PhotoViewer.AnonymousClass47.this.lambda$createAnimator$0$PhotoViewer$47(valueAnimator);
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

                        public /* synthetic */ void lambda$createAnimator$0$PhotoViewer$47(ValueAnimator valueAnimator) {
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
                private final /* synthetic */ File f$1;
                private final /* synthetic */ File f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ MessageObject f$4;
                private final /* synthetic */ boolean f$5;
                private final /* synthetic */ boolean f$6;
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
                    PhotoViewer.this.lambda$checkProgress$49$PhotoViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
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

    public /* synthetic */ void lambda$checkProgress$49$PhotoViewer(File file, File file2, int i, MessageObject messageObject, boolean z, boolean z2, boolean z3) {
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
            private final /* synthetic */ File f$1;
            private final /* synthetic */ File f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ boolean f$6;
            private final /* synthetic */ MessageObject f$7;
            private final /* synthetic */ boolean f$8;

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
                PhotoViewer.this.lambda$null$48$PhotoViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$null$48$PhotoViewer(File file, File file2, boolean z, boolean z2, int i, boolean z3, MessageObject messageObject, boolean z4) {
        boolean z5 = true;
        if (!(file == null && file2 == null) && (z || z2)) {
            if (i != 0 || !this.isPlaying) {
                if (!z3 || shouldMessageObjectAutoPlayed(messageObject)) {
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v13, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v10, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v70, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v15, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v18, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v5, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v71, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v6, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v19, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v11, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v16, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v73, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v15, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v12, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v20, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v7, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v17, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v21, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v13, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v8, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v24, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v9, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v15, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v10, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v25, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v16, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v25, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v29, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v17, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v26, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v11, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v26, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v18, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v12, resolved type: org.telegram.messenger.WebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v49, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v54, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v99, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v101, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v55, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX WARNING: type inference failed for: r12v4, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r12v18 */
    /* JADX WARNING: type inference failed for: r12v20 */
    /* JADX WARNING: type inference failed for: r12v22, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r13v38, types: [org.telegram.messenger.ImageLocation] */
    /* JADX WARNING: type inference failed for: r14v27, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r14v31 */
    /* JADX WARNING: type inference failed for: r14v32 */
    /* JADX WARNING: type inference failed for: r14v33, types: [org.telegram.messenger.ImageLocation] */
    /* JADX WARNING: type inference failed for: r2v85 */
    /* JADX WARNING: type inference failed for: r2v88, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r2v90 */
    /* JADX WARNING: type inference failed for: r6v37, types: [android.graphics.drawable.Drawable] */
    /* JADX WARNING: type inference failed for: r5v51, types: [android.graphics.drawable.BitmapDrawable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0515  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0543  */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setIndexToImage(org.telegram.messenger.ImageReceiver r23, int r24) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            r2 = r24
            r3 = 0
            r1.setOrientation(r3, r3)
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r4 = r0.secureDocuments
            boolean r4 = r4.isEmpty()
            r5 = 0
            if (r4 != 0) goto L_0x0066
            if (r2 < 0) goto L_0x058a
            java.util.ArrayList<org.telegram.messenger.SecureDocument> r3 = r0.secureDocuments
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x058a
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
            r1 = r23
            r5 = r6
            r6 = r8
            r8 = r9
            r9 = r10
            r10 = r11
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x058a
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
            r21 = r13
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
            r21 = r15
            r15 = r12
        L_0x0243:
            r12 = r21
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
            r1 = r23
            r2 = r5
            r4 = r17
            r5 = r10
            r6 = r8
            r8 = r16
            r9 = r12
            r10 = r13
            r12 = r14
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            goto L_0x058a
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
            r1 = r23
            r2 = r5
            r4 = r8
            r5 = r6
            r6 = r16
            r7 = r12
            r8 = r9
            r9 = r11
            r10 = r14
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x058a
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
            r1 = r23
            r3 = r15
            r5 = r12
            r7 = r11
            r8 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (android.graphics.drawable.Drawable) r4, (int) r5, (java.lang.String) r6, (java.lang.Object) r7, (int) r8)
            goto L_0x058a
        L_0x0304:
            r3 = 2131165751(0x7var_, float:1.7945728E38)
            if (r2 == 0) goto L_0x0347
            if (r17 == 0) goto L_0x031d
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForWebFile(r2)
            r5 = 0
            r6 = 0
            r1 = r23
            r3 = r15
            r4 = r17
            r7 = r11
            r8 = r14
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r3, (org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (java.lang.String) r6, (java.lang.Object) r7, (int) r8)
            goto L_0x058a
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
            r1 = r23
            r3 = r15
            r6 = r11
            r7 = r14
            r1.setImage(r2, r3, r4, r5, r6, r7)
            goto L_0x058a
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
            r1 = r23
            r2 = r13
            r3 = r15
            r6 = r12
            r1.setImage(r2, r3, r4, r5, r6)
            goto L_0x058a
        L_0x036d:
            r2 = r5
            r1.setImageBitmap((android.graphics.Bitmap) r2)
            goto L_0x058a
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
            r4 = 2131165814(0x7var_, float:1.7945856E38)
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
            r1 = r23
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
            r1 = r23
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
            if (r8 == 0) goto L_0x0570
            r1.setNeedsQualityThumb(r6)
            org.telegram.messenger.ImageReceiver$BitmapHolder r4 = r0.currentThumb
            if (r4 == 0) goto L_0x0499
            org.telegram.messenger.ImageReceiver r6 = r0.centerImage
            if (r1 != r6) goto L_0x0499
            goto L_0x049a
        L_0x0499:
            r4 = 0
        L_0x049a:
            r6 = r7[r3]
            if (r6 != 0) goto L_0x04a1
            r6 = -1
            r7[r3] = r6
        L_0x04a1:
            if (r9 == 0) goto L_0x04ac
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r9.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r5)
            org.telegram.tgnet.TLObject r6 = r9.photoThumbsObject
            goto L_0x04ae
        L_0x04ac:
            r5 = 0
            r6 = 0
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
            if (r5 != 0) goto L_0x04c9
        L_0x04bd:
            int r5 = r0.avatarsDialogId
            if (r5 != 0) goto L_0x04c9
            boolean r5 = r0.isEvent
            if (r5 == 0) goto L_0x04c6
            goto L_0x04c9
        L_0x04c6:
            r20 = 0
            goto L_0x04cb
        L_0x04c9:
            r20 = 1
        L_0x04cb:
            if (r9 == 0) goto L_0x04e7
            int r5 = r0.sharedMediaType
            r10 = 5
            if (r5 != r10) goto L_0x04e4
            org.telegram.tgnet.TLRPC$Document r5 = r9.getDocument()
            org.telegram.tgnet.TLRPC$TL_videoSize r10 = org.telegram.messenger.MessageObject.getDocumentVideoThumb(r5)
            if (r10 == 0) goto L_0x04e1
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$TL_videoSize) r10, (org.telegram.tgnet.TLRPC$Document) r5)
            goto L_0x04e2
        L_0x04e1:
            r5 = 0
        L_0x04e2:
            r11 = r9
            goto L_0x0513
        L_0x04e4:
            r11 = r9
        L_0x04e5:
            r5 = 0
            goto L_0x0513
        L_0x04e7:
            int r5 = r0.avatarsDialogId
            if (r5 == 0) goto L_0x0511
            if (r5 <= 0) goto L_0x04fe
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r9 = r0.avatarsDialogId
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r9)
            goto L_0x050f
        L_0x04fe:
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r9 = r0.avatarsDialogId
            int r9 = -r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r9)
        L_0x050f:
            r11 = r5
            goto L_0x04e5
        L_0x0511:
            r5 = 0
            r11 = 0
        L_0x0513:
            if (r5 == 0) goto L_0x0543
            r9 = 0
            r10 = 0
            if (r4 != 0) goto L_0x051f
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForObject(r2, r6)
            r6 = r2
            goto L_0x0520
        L_0x051f:
            r6 = 0
        L_0x0520:
            if (r4 == 0) goto L_0x052c
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r4 = r4.bitmap
            r2.<init>(r4)
            r16 = r2
            goto L_0x052e
        L_0x052c:
            r16 = 0
        L_0x052e:
            r12 = r7[r3]
            r13 = 0
            java.lang.String r7 = "b"
            r1 = r23
            r2 = r8
            r3 = r9
            r4 = r5
            r5 = r10
            r8 = r16
            r9 = r12
            r10 = r13
            r12 = r20
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            goto L_0x058a
        L_0x0543:
            r5 = 0
            if (r4 != 0) goto L_0x054c
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForObject(r2, r6)
            r6 = r2
            goto L_0x054d
        L_0x054c:
            r6 = 0
        L_0x054d:
            if (r4 == 0) goto L_0x0559
            android.graphics.drawable.BitmapDrawable r2 = new android.graphics.drawable.BitmapDrawable
            android.graphics.Bitmap r4 = r4.bitmap
            r2.<init>(r4)
            r16 = r2
            goto L_0x055b
        L_0x0559:
            r16 = 0
        L_0x055b:
            r7 = r7[r3]
            r9 = 0
            java.lang.String r10 = "b"
            r1 = r23
            r2 = r8
            r3 = r5
            r4 = r6
            r5 = r10
            r6 = r16
            r8 = r9
            r9 = r11
            r10 = r20
            r1.setImage(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x058a
        L_0x0570:
            r2 = 1
            r1.setNeedsQualityThumb(r2)
            r2 = r7[r3]
            if (r2 != 0) goto L_0x057d
            r2 = 0
            r1.setImageBitmap((android.graphics.Bitmap) r2)
            goto L_0x058a
        L_0x057d:
            android.app.Activity r2 = r0.parentActivity
            android.content.res.Resources r2 = r2.getResources()
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r4)
            r1.setImageBitmap((android.graphics.drawable.Drawable) r2)
        L_0x058a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.setIndexToImage(org.telegram.messenger.ImageReceiver, int):void");
    }

    public static boolean isShowingImage(MessageObject messageObject) {
        boolean z = true;
        boolean z2 = Instance != null && !Instance.pipAnimationInProgress && Instance.isVisible && !Instance.disableShowCheck && messageObject != null && Instance.currentMessageObject != null && Instance.currentMessageObject.getId() == messageObject.getId() && Instance.currentMessageObject.getDialogId() == messageObject.getDialogId();
        if (z2 || PipInstance == null) {
            return z2;
        }
        if (!PipInstance.isVisible || PipInstance.disableShowCheck || messageObject == null || PipInstance.currentMessageObject == null || PipInstance.currentMessageObject.getId() != messageObject.getId() || PipInstance.currentMessageObject.getDialogId() != messageObject.getDialogId()) {
            z = false;
        }
        return z;
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
                this.photoCropView.setBitmap(bitmap2, i, false, false, this.paintingOverlay);
                if (this.currentEditMode == 0) {
                    setCropTranslations(false);
                }
            }
        }
    }

    private void initCropView() {
        if (this.sendPhotoType == 1) {
            this.photoCropView.setBitmap((Bitmap) null, 0, false, false, (PaintingOverlay) null);
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
            r6 = 2131626010(0x7f0e081a, float:1.8879244E38)
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
            org.telegram.ui.PhotoViewer$48 r1 = new org.telegram.ui.PhotoViewer$48
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
            r3 = 2131623995(0x7f0e003b, float:1.8875157E38)
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
            updateAccessibilityOverlayVisibility();
        }
    }

    /* JADX WARNING: type inference failed for: r3v1, types: [android.view.View, org.telegram.ui.Components.AnimatedFileDrawable, android.graphics.drawable.Drawable] */
    /* JADX WARNING: type inference failed for: r3v15 */
    /* JADX WARNING: type inference failed for: r3v56 */
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
                            layoutParams.width = (int) this.centerImage.getImageWidth();
                            layoutParams.height = (int) this.centerImage.getImageHeight();
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
                        float min = Math.min(((float) this.windowView.getMeasuredWidth()) / ((float) layoutParams.width), ((float) (AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / ((float) layoutParams.height));
                        float f = this.scale;
                        float f2 = ((float) layoutParams.height) * f * min;
                        float measuredWidth = (((float) this.windowView.getMeasuredWidth()) - ((((float) layoutParams.width) * f) * min)) / 2.0f;
                        float f3 = (((float) (AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) - f2) / 2.0f;
                        for (int i7 = 0; i7 < animatingImageViews.length; i7++) {
                            animatingImageViews[i7].setLayoutParams(layoutParams);
                            animatingImageViews[i7].setTranslationX(this.translationX + measuredWidth);
                            animatingImageViews[i7].setTranslationY(this.translationY + f3);
                            animatingImageViews[i7].setScaleX(this.scale * min);
                            animatingImageViews[i7].setScaleY(this.scale * min);
                        }
                        if (placeForPhoto != null) {
                            placeForPhoto.imageReceiver.setVisible(false, true);
                            int abs = (int) Math.abs(rectF.left - placeForPhoto.imageReceiver.getImageX());
                            int abs2 = (int) Math.abs(rectF.top - placeForPhoto.imageReceiver.getImageY());
                            int[] iArr = new int[2];
                            placeForPhoto.parentView.getLocationInWindow(iArr);
                            int i8 = (int) ((((float) (iArr[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight))) - (((float) placeForPhoto.viewY) + rectF.top)) + ((float) placeForPhoto.clipTopAddition));
                            if (i8 < 0) {
                                i8 = 0;
                            }
                            float f4 = rectF.top;
                            int height = (int) ((((((float) placeForPhoto.viewY) + f4) + (rectF.bottom - f4)) - ((float) ((iArr[1] + placeForPhoto.parentView.getHeight()) - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight)))) + ((float) placeForPhoto.clipBottomAddition));
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
                            float f5 = placeForPhoto.scale;
                            fArr2[0] = f5;
                            fArr[1][1] = f5;
                            fArr[1][2] = ((float) placeForPhoto.viewX) + (rectF.left * f5);
                            fArr[1][3] = ((float) placeForPhoto.viewY) + (rectF.top * f5);
                            float f6 = (float) abs;
                            fArr[1][4] = f6 * f5;
                            fArr[1][5] = ((float) max) * f5;
                            fArr[1][6] = ((float) max2) * f5;
                            int i9 = 0;
                            for (int i10 = 4; i9 < i10; i10 = 4) {
                                float[] fArr3 = this.animationValues[1];
                                int i11 = i9 + 7;
                                int[] iArr2 = placeForPhoto.radius;
                                fArr3[i11] = iArr2 != null ? (float) iArr2[i9] : 0.0f;
                                i9++;
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
                            int i12 = AndroidUtilities.displaySize.y + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                            Animator[] animatorArr = new Animator[4];
                            animatorArr[0] = ObjectAnimator.ofInt(this.backgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.animatingImageView, View.ALPHA, new float[]{0.0f});
                            ClippingImageView clippingImageView = this.animatingImageView;
                            Property property4 = View.TRANSLATION_Y;
                            float[] fArr8 = new float[1];
                            if (this.translationY < 0.0f) {
                                i12 = -i12;
                            }
                            fArr8[0] = (float) i12;
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
                                PhotoViewer.this.lambda$closePhoto$51$PhotoViewer(this.f$1);
                            }
                        };
                        animatorSet.setDuration(200);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public final void run() {
                                        PhotoViewer.AnonymousClass49.this.lambda$onAnimationEnd$0$PhotoViewer$49();
                                    }
                                });
                            }

                            public /* synthetic */ void lambda$onAnimationEnd$0$PhotoViewer$49() {
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
                                PhotoViewer.this.lambda$closePhoto$52$PhotoViewer(this.f$1);
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
                    PhotoViewer.this.lambda$closePhoto$50$PhotoViewer();
                }
            });
        }
    }

    public /* synthetic */ void lambda$closePhoto$50$PhotoViewer() {
        switchToEditMode(0);
    }

    public /* synthetic */ void lambda$closePhoto$51$PhotoViewer(PlaceProviderObject placeProviderObject) {
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, (Paint) null);
        }
        this.animationInProgress = 0;
        onPhotoClosed(placeProviderObject);
    }

    public /* synthetic */ void lambda$closePhoto$52$PhotoViewer(PlaceProviderObject placeProviderObject) {
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
            private final /* synthetic */ PhotoViewer.PlaceProviderObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PhotoViewer.this.lambda$onPhotoClosed$53$PhotoViewer(this.f$1);
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

    public /* synthetic */ void lambda$onPhotoClosed$53$PhotoViewer(PlaceProviderObject placeProviderObject) {
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
                    PhotoViewer.this.lambda$redraw$54$PhotoViewer(this.f$1);
                }
            }, 100);
        }
    }

    public /* synthetic */ void lambda$redraw$54$PhotoViewer(int i) {
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
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x041b, code lost:
        if (r1 > r4) goto L_0x0415;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x042a, code lost:
        if (r2 > r4) goto L_0x0424;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x044f, code lost:
        if (r3 > r4) goto L_0x0449;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x0460, code lost:
        if (r3 > r4) goto L_0x045a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x052a, code lost:
        if (r3 > r4) goto L_0x0524;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x053b, code lost:
        if (r3 > r4) goto L_0x0535;
     */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0361  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r20) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            int r2 = r0.currentEditMode
            r3 = 5
            r4 = 0
            r6 = 3
            r7 = 2
            r8 = 1
            if (r2 != r6) goto L_0x002b
            long r9 = r0.animationStartTime
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
            int r2 = r0.animationInProgress
            r9 = 0
            if (r2 != 0) goto L_0x064a
            long r10 = r0.animationStartTime
            int r2 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x0038
            goto L_0x064a
        L_0x0038:
            int r2 = r0.currentEditMode
            if (r2 != r7) goto L_0x0042
            org.telegram.ui.Components.PhotoFilterView r2 = r0.photoFilterView
            r2.onTouch(r1)
            return r8
        L_0x0042:
            if (r2 == r8) goto L_0x0649
            if (r2 == r6) goto L_0x004c
            int r2 = r0.sendPhotoType
            if (r2 != r8) goto L_0x004c
            goto L_0x0649
        L_0x004c:
            org.telegram.ui.Components.PhotoViewerCaptionEnterView r2 = r0.captionEditText
            boolean r2 = r2.isPopupShowing()
            if (r2 != 0) goto L_0x0640
            org.telegram.ui.Components.PhotoViewerCaptionEnterView r2 = r0.captionEditText
            boolean r2 = r2.isKeyboardVisible()
            if (r2 == 0) goto L_0x005e
            goto L_0x0640
        L_0x005e:
            int r2 = r0.currentEditMode
            if (r2 != 0) goto L_0x0082
            int r2 = r0.sendPhotoType
            if (r2 == r8) goto L_0x0082
            int r2 = r20.getPointerCount()
            if (r2 != r8) goto L_0x0082
            org.telegram.ui.Components.GestureDetector2 r2 = r0.gestureDetector
            boolean r2 = r2.onTouchEvent(r1)
            if (r2 == 0) goto L_0x0082
            boolean r2 = r0.doubleTap
            if (r2 == 0) goto L_0x0082
            r0.doubleTap = r9
            r0.moving = r9
            r0.zooming = r9
            r0.checkMinMax(r9)
            return r8
        L_0x0082:
            org.telegram.ui.Components.Tooltip r2 = r0.tooltip
            if (r2 == 0) goto L_0x0089
            r2.hide()
        L_0x0089:
            int r2 = r20.getActionMasked()
            r10 = 1073741824(0x40000000, float:2.0)
            if (r2 == 0) goto L_0x0545
            int r2 = r20.getActionMasked()
            if (r2 != r3) goto L_0x0099
            goto L_0x0545
        L_0x0099:
            int r2 = r20.getActionMasked()
            r3 = 1077936128(0x40400000, float:3.0)
            r11 = 1065353216(0x3var_, float:1.0)
            r12 = 0
            if (r2 != r7) goto L_0x0383
            boolean r2 = r0.canZoom
            if (r2 == 0) goto L_0x01b6
            int r2 = r20.getPointerCount()
            if (r2 != r7) goto L_0x01b6
            boolean r2 = r0.draggingDown
            if (r2 != 0) goto L_0x01b6
            boolean r2 = r0.zooming
            if (r2 == 0) goto L_0x01b6
            boolean r2 = r0.changingPage
            if (r2 != 0) goto L_0x01b6
            r0.discardTap = r8
            int r2 = r0.currentEditMode
            if (r2 != r6) goto L_0x013e
            float r2 = r1.getX(r9)
            float r4 = r1.getX(r8)
            float r2 = r2 + r4
            float r2 = r2 / r10
            float r4 = r1.getY(r9)
            float r5 = r1.getY(r8)
            float r4 = r4 + r5
            float r4 = r4 / r10
            float r5 = r0.moveStartX
            float r5 = r5 - r2
            float r10 = r0.moveStartY
            float r10 = r10 - r4
            r0.moveStartX = r2
            r0.moveStartY = r4
            float r11 = r0.translationX
            float r12 = r0.minX
            int r12 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r12 < 0) goto L_0x00ec
            float r12 = r0.maxX
            int r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r11 <= 0) goto L_0x00ed
        L_0x00ec:
            float r5 = r5 / r3
        L_0x00ed:
            float r11 = r0.translationY
            float r12 = r0.minY
            int r12 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r12 < 0) goto L_0x00fb
            float r12 = r0.maxY
            int r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r11 <= 0) goto L_0x00fc
        L_0x00fb:
            float r10 = r10 / r3
        L_0x00fc:
            float r3 = r0.pinchCenterX
            int r11 = r19.getContainerViewWidth()
            int r11 = r11 / r7
            float r11 = (float) r11
            float r3 = r3 - r11
            float r11 = r0.pinchCenterX
            int r12 = r19.getContainerViewWidth()
            int r12 = r12 / r7
            float r12 = (float) r12
            float r11 = r11 - r12
            float r12 = r0.translationX
            float r11 = r11 - r12
            float r12 = r0.scale
            float r13 = r0.pinchStartScale
            float r12 = r12 / r13
            float r11 = r11 / r12
            float r3 = r3 - r11
            float r3 = r3 - r5
            r0.pinchStartX = r3
            float r3 = r0.pinchCenterY
            int r5 = r19.getContainerViewHeight()
            int r5 = r5 / r7
            float r5 = (float) r5
            float r3 = r3 - r5
            float r5 = r0.pinchCenterY
            int r11 = r19.getContainerViewHeight()
            int r11 = r11 / r7
            float r11 = (float) r11
            float r5 = r5 - r11
            float r11 = r0.translationY
            float r5 = r5 - r11
            float r11 = r0.scale
            float r12 = r0.pinchStartScale
            float r11 = r11 / r12
            float r5 = r5 / r11
            float r3 = r3 - r5
            float r3 = r3 - r10
            r0.pinchStartY = r3
            r0.pinchCenterX = r2
            r0.pinchCenterY = r4
        L_0x013e:
            float r2 = r1.getX(r8)
            float r3 = r1.getX(r9)
            float r2 = r2 - r3
            double r2 = (double) r2
            float r4 = r1.getY(r8)
            float r1 = r1.getY(r9)
            float r4 = r4 - r1
            double r4 = (double) r4
            double r1 = java.lang.Math.hypot(r2, r4)
            float r1 = (float) r1
            float r2 = r0.pinchStartDistance
            float r1 = r1 / r2
            float r2 = r0.pinchStartScale
            float r1 = r1 * r2
            r0.scale = r1
            float r1 = r0.pinchCenterX
            int r2 = r19.getContainerViewWidth()
            int r2 = r2 / r7
            float r2 = (float) r2
            float r1 = r1 - r2
            float r2 = r0.pinchCenterX
            int r3 = r19.getContainerViewWidth()
            int r3 = r3 / r7
            float r3 = (float) r3
            float r2 = r2 - r3
            float r3 = r0.pinchStartX
            float r2 = r2 - r3
            float r3 = r0.scale
            float r4 = r0.pinchStartScale
            float r3 = r3 / r4
            float r2 = r2 * r3
            float r1 = r1 - r2
            r0.translationX = r1
            float r1 = r0.pinchCenterY
            int r2 = r19.getContainerViewHeight()
            int r2 = r2 / r7
            float r2 = (float) r2
            float r1 = r1 - r2
            float r2 = r0.pinchCenterY
            int r3 = r19.getContainerViewHeight()
            int r3 = r3 / r7
            float r3 = (float) r3
            float r2 = r2 - r3
            float r3 = r0.pinchStartY
            float r2 = r2 - r3
            float r3 = r0.scale
            float r4 = r0.pinchStartScale
            float r4 = r3 / r4
            float r2 = r2 * r4
            float r1 = r1 - r2
            r0.translationY = r1
            int r2 = r0.currentEditMode
            if (r2 != r6) goto L_0x01aa
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            float r4 = r0.translationX
            r2.setTransform(r3, r4, r1)
        L_0x01aa:
            float r1 = r0.scale
            r0.updateMinMax(r1)
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r1 = r0.containerView
            r1.invalidate()
            goto L_0x063f
        L_0x01b6:
            int r2 = r20.getPointerCount()
            if (r2 != r8) goto L_0x063f
            int r2 = r0.paintViewTouched
            if (r2 != r8) goto L_0x01de
            android.view.MotionEvent r1 = android.view.MotionEvent.obtain(r20)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            float r2 = r2.getX()
            float r2 = -r2
            org.telegram.ui.Components.PhotoPaintView r3 = r0.photoPaintView
            float r3 = r3.getY()
            float r3 = -r3
            r1.offsetLocation(r2, r3)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            r2.onTouch(r1)
            r1.recycle()
            return r8
        L_0x01de:
            android.view.VelocityTracker r2 = r0.velocityTracker
            if (r2 == 0) goto L_0x01e5
            r2.addMovement(r1)
        L_0x01e5:
            float r2 = r20.getX()
            float r7 = r0.moveStartX
            float r2 = r2 - r7
            float r2 = java.lang.Math.abs(r2)
            float r7 = r20.getY()
            float r13 = r0.dragY
            float r7 = r7 - r13
            float r7 = java.lang.Math.abs(r7)
            int r13 = r0.touchSlop
            float r14 = (float) r13
            int r14 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r14 > 0) goto L_0x0207
            float r13 = (float) r13
            int r13 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r13 <= 0) goto L_0x0217
        L_0x0207:
            r0.discardTap = r8
            r19.hidePressedDrawables()
            org.telegram.ui.PhotoViewer$QualityChooseView r13 = r0.qualityChooseView
            if (r13 == 0) goto L_0x0217
            int r13 = r13.getVisibility()
            if (r13 != 0) goto L_0x0217
            return r8
        L_0x0217:
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r13 = r0.placeProvider
            boolean r13 = r13.canScrollAway()
            if (r13 == 0) goto L_0x0274
            int r13 = r0.currentEditMode
            if (r13 != 0) goto L_0x0274
            int r13 = r0.sendPhotoType
            if (r13 == r8) goto L_0x0274
            boolean r13 = r0.canDragDown
            if (r13 == 0) goto L_0x0274
            boolean r13 = r0.draggingDown
            if (r13 != 0) goto L_0x0274
            float r13 = r0.scale
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
            r0.draggingDown = r8
            r19.hidePressedDrawables()
            r0.moving = r9
            float r1 = r20.getY()
            r0.dragY = r1
            boolean r1 = r0.isActionBarVisible
            if (r1 == 0) goto L_0x0262
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r1 = r0.containerView
            java.lang.Object r1 = r1.getTag()
            if (r1 == 0) goto L_0x0262
            r0.toggleActionBar(r9, r8)
            goto L_0x0273
        L_0x0262:
            android.widget.FrameLayout r1 = r0.pickerView
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0273
            r0.toggleActionBar(r9, r8)
            r0.togglePhotosListView(r9, r8)
            r0.toggleCheckImageView(r9)
        L_0x0273:
            return r8
        L_0x0274:
            boolean r2 = r0.draggingDown
            if (r2 == 0) goto L_0x0288
            float r1 = r20.getY()
            float r2 = r0.dragY
            float r1 = r1 - r2
            r0.translationY = r1
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r1 = r0.containerView
            r1.invalidate()
            goto L_0x063f
        L_0x0288:
            boolean r2 = r0.invalidCoords
            if (r2 != 0) goto L_0x0373
            long r13 = r0.animationStartTime
            int r2 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x0373
            float r2 = r0.moveStartX
            float r4 = r20.getX()
            float r2 = r2 - r4
            float r4 = r0.moveStartY
            float r5 = r20.getY()
            float r4 = r4 - r5
            boolean r5 = r0.moving
            if (r5 != 0) goto L_0x02c8
            int r5 = r0.currentEditMode
            if (r5 != 0) goto L_0x02c8
            float r5 = r0.scale
            int r5 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r5 != 0) goto L_0x02c2
            float r5 = java.lang.Math.abs(r4)
            r7 = 1094713344(0x41400000, float:12.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r5 = r5 + r7
            float r7 = java.lang.Math.abs(r2)
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 < 0) goto L_0x02c8
        L_0x02c2:
            float r5 = r0.scale
            int r5 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r5 == 0) goto L_0x063f
        L_0x02c8:
            boolean r5 = r0.moving
            if (r5 != 0) goto L_0x02d5
            r0.moving = r8
            r0.canDragDown = r9
            r19.hidePressedDrawables()
            r2 = 0
            r4 = 0
        L_0x02d5:
            float r5 = r20.getX()
            r0.moveStartX = r5
            float r1 = r20.getY()
            r0.moveStartY = r1
            float r1 = r0.scale
            r0.updateMinMax(r1)
            float r1 = r0.translationX
            float r5 = r0.minX
            int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r1 >= 0) goto L_0x02fa
            int r1 = r0.currentEditMode
            if (r1 != 0) goto L_0x030e
            org.telegram.messenger.ImageReceiver r1 = r0.rightImage
            boolean r1 = r1.hasImageSet()
            if (r1 == 0) goto L_0x030e
        L_0x02fa:
            float r1 = r0.translationX
            float r5 = r0.maxX
            int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x030f
            int r1 = r0.currentEditMode
            if (r1 != 0) goto L_0x030e
            org.telegram.messenger.ImageReceiver r1 = r0.leftImage
            boolean r1 = r1.hasImageSet()
            if (r1 != 0) goto L_0x030f
        L_0x030e:
            float r2 = r2 / r3
        L_0x030f:
            float r1 = r0.maxY
            int r5 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r5 != 0) goto L_0x0336
            float r5 = r0.minY
            int r7 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r7 != 0) goto L_0x0336
            int r7 = r0.currentEditMode
            if (r7 != 0) goto L_0x0336
            int r7 = r0.sendPhotoType
            if (r7 == r8) goto L_0x0336
            float r3 = r0.translationY
            float r7 = r3 - r4
            int r7 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r7 >= 0) goto L_0x032e
            r0.translationY = r5
            goto L_0x0349
        L_0x032e:
            float r3 = r3 - r4
            int r3 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x0345
            r0.translationY = r1
            goto L_0x0349
        L_0x0336:
            float r1 = r0.translationY
            float r5 = r0.minY
            int r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r5 < 0) goto L_0x0347
            float r5 = r0.maxY
            int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0345
            goto L_0x0347
        L_0x0345:
            r12 = r4
            goto L_0x0349
        L_0x0347:
            float r12 = r4 / r3
        L_0x0349:
            float r1 = r0.translationX
            float r1 = r1 - r2
            r0.translationX = r1
            float r1 = r0.scale
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x0358
            int r1 = r0.currentEditMode
            if (r1 == 0) goto L_0x035d
        L_0x0358:
            float r1 = r0.translationY
            float r1 = r1 - r12
            r0.translationY = r1
        L_0x035d:
            int r1 = r0.currentEditMode
            if (r1 != r6) goto L_0x036c
            org.telegram.ui.Components.PhotoPaintView r1 = r0.photoPaintView
            float r2 = r0.scale
            float r3 = r0.translationX
            float r4 = r0.translationY
            r1.setTransform(r2, r3, r4)
        L_0x036c:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r1 = r0.containerView
            r1.invalidate()
            goto L_0x063f
        L_0x0373:
            r0.invalidCoords = r9
            float r2 = r20.getX()
            r0.moveStartX = r2
            float r1 = r20.getY()
            r0.moveStartY = r1
            goto L_0x063f
        L_0x0383:
            int r2 = r20.getActionMasked()
            if (r2 == r6) goto L_0x0396
            int r2 = r20.getActionMasked()
            if (r2 == r8) goto L_0x0396
            int r2 = r20.getActionMasked()
            r4 = 6
            if (r2 != r4) goto L_0x063f
        L_0x0396:
            int r2 = r0.paintViewTouched
            if (r2 != r8) goto L_0x03ba
            android.view.MotionEvent r1 = android.view.MotionEvent.obtain(r20)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            float r2 = r2.getX()
            float r2 = -r2
            org.telegram.ui.Components.PhotoPaintView r3 = r0.photoPaintView
            float r3 = r3.getY()
            float r3 = -r3
            r1.offsetLocation(r2, r3)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            r2.onTouch(r1)
            r1.recycle()
            r0.paintViewTouched = r9
            return r8
        L_0x03ba:
            r0.paintViewTouched = r9
            boolean r2 = r0.zooming
            if (r2 == 0) goto L_0x046e
            r0.invalidCoords = r8
            float r1 = r0.scale
            int r2 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r2 >= 0) goto L_0x03d0
            r0.updateMinMax(r11)
            r0.animateTo(r11, r12, r12, r8)
            goto L_0x0468
        L_0x03d0:
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 <= 0) goto L_0x0431
            float r1 = r0.pinchCenterX
            int r2 = r19.getContainerViewWidth()
            int r2 = r2 / r7
            float r2 = (float) r2
            float r1 = r1 - r2
            float r2 = r0.pinchCenterX
            int r4 = r19.getContainerViewWidth()
            int r4 = r4 / r7
            float r4 = (float) r4
            float r2 = r2 - r4
            float r4 = r0.pinchStartX
            float r2 = r2 - r4
            float r4 = r0.pinchStartScale
            float r4 = r3 / r4
            float r2 = r2 * r4
            float r1 = r1 - r2
            float r2 = r0.pinchCenterY
            int r4 = r19.getContainerViewHeight()
            int r4 = r4 / r7
            float r4 = (float) r4
            float r2 = r2 - r4
            float r4 = r0.pinchCenterY
            int r5 = r19.getContainerViewHeight()
            int r5 = r5 / r7
            float r5 = (float) r5
            float r4 = r4 - r5
            float r5 = r0.pinchStartY
            float r4 = r4 - r5
            float r5 = r0.pinchStartScale
            float r5 = r3 / r5
            float r4 = r4 * r5
            float r2 = r2 - r4
            r0.updateMinMax(r3)
            float r4 = r0.minX
            int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x0417
        L_0x0415:
            r1 = r4
            goto L_0x041e
        L_0x0417:
            float r4 = r0.maxX
            int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r5 <= 0) goto L_0x041e
            goto L_0x0415
        L_0x041e:
            float r4 = r0.minY
            int r5 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x0426
        L_0x0424:
            r2 = r4
            goto L_0x042d
        L_0x0426:
            float r4 = r0.maxY
            int r5 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r5 <= 0) goto L_0x042d
            goto L_0x0424
        L_0x042d:
            r0.animateTo(r3, r1, r2, r8)
            goto L_0x0468
        L_0x0431:
            r0.checkMinMax(r8)
            int r1 = r0.currentEditMode
            if (r1 != r6) goto L_0x0468
            float r1 = r0.translationX
            float r2 = r0.translationY
            float r3 = r0.scale
            r0.updateMinMax(r3)
            float r3 = r0.translationX
            float r4 = r0.minX
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x044b
        L_0x0449:
            r1 = r4
            goto L_0x0452
        L_0x044b:
            float r4 = r0.maxX
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x0452
            goto L_0x0449
        L_0x0452:
            float r3 = r0.translationY
            float r4 = r0.minY
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x045c
        L_0x045a:
            r2 = r4
            goto L_0x0463
        L_0x045c:
            float r4 = r0.maxY
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x0463
            goto L_0x045a
        L_0x0463:
            float r3 = r0.scale
            r0.animateTo(r3, r1, r2, r9)
        L_0x0468:
            r0.zooming = r9
            r0.moving = r9
            goto L_0x063f
        L_0x046e:
            boolean r2 = r0.draggingDown
            if (r2 == 0) goto L_0x04a2
            float r2 = r0.dragY
            float r1 = r20.getY()
            float r2 = r2 - r1
            float r1 = java.lang.Math.abs(r2)
            int r2 = r19.getContainerViewHeight()
            float r2 = (float) r2
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            float r2 = r2 / r3
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x048d
            r0.closePhoto(r8, r9)
            goto L_0x049e
        L_0x048d:
            android.widget.FrameLayout r1 = r0.pickerView
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x049b
            r0.toggleActionBar(r8, r8)
            r0.toggleCheckImageView(r8)
        L_0x049b:
            r0.animateTo(r11, r12, r12, r9)
        L_0x049e:
            r0.draggingDown = r9
            goto L_0x063f
        L_0x04a2:
            boolean r1 = r0.moving
            if (r1 == 0) goto L_0x063f
            float r1 = r0.translationX
            float r2 = r0.translationY
            float r3 = r0.scale
            r0.updateMinMax(r3)
            r0.moving = r9
            r0.canDragDown = r8
            android.view.VelocityTracker r3 = r0.velocityTracker
            if (r3 == 0) goto L_0x04c8
            float r4 = r0.scale
            int r4 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r4 != 0) goto L_0x04c8
            r4 = 1000(0x3e8, float:1.401E-42)
            r3.computeCurrentVelocity(r4)
            android.view.VelocityTracker r3 = r0.velocityTracker
            float r12 = r3.getXVelocity()
        L_0x04c8:
            int r3 = r0.currentEditMode
            if (r3 != 0) goto L_0x051c
            int r3 = r0.sendPhotoType
            if (r3 == r8) goto L_0x051c
            float r3 = r0.translationX
            float r4 = r0.minX
            int r5 = r19.getContainerViewWidth()
            int r5 = r5 / r6
            float r5 = (float) r5
            float r4 = r4 - r5
            r5 = 1143111680(0x44228000, float:650.0)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x04ec
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = -r3
            float r3 = (float) r3
            int r3 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x04f8
        L_0x04ec:
            org.telegram.messenger.ImageReceiver r3 = r0.rightImage
            boolean r3 = r3.hasImageSet()
            if (r3 == 0) goto L_0x04f8
            r19.goToNext()
            return r8
        L_0x04f8:
            float r3 = r0.translationX
            float r4 = r0.maxX
            int r7 = r19.getContainerViewWidth()
            int r7 = r7 / r6
            float r6 = (float) r7
            float r4 = r4 + r6
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 > 0) goto L_0x0510
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            int r3 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x051c
        L_0x0510:
            org.telegram.messenger.ImageReceiver r3 = r0.leftImage
            boolean r3 = r3.hasImageSet()
            if (r3 == 0) goto L_0x051c
            r19.goToPrev()
            return r8
        L_0x051c:
            float r3 = r0.translationX
            float r4 = r0.minX
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x0526
        L_0x0524:
            r1 = r4
            goto L_0x052d
        L_0x0526:
            float r4 = r0.maxX
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x052d
            goto L_0x0524
        L_0x052d:
            float r3 = r0.translationY
            float r4 = r0.minY
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x0537
        L_0x0535:
            r2 = r4
            goto L_0x053e
        L_0x0537:
            float r4 = r0.maxY
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x053e
            goto L_0x0535
        L_0x053e:
            float r3 = r0.scale
            r0.animateTo(r3, r1, r2, r9)
            goto L_0x063f
        L_0x0545:
            r0.discardTap = r9
            android.widget.Scroller r2 = r0.scroller
            boolean r2 = r2.isFinished()
            if (r2 != 0) goto L_0x0554
            android.widget.Scroller r2 = r0.scroller
            r2.abortAnimation()
        L_0x0554:
            boolean r2 = r0.draggingDown
            if (r2 != 0) goto L_0x063f
            boolean r2 = r0.changingPage
            if (r2 != 0) goto L_0x063f
            boolean r2 = r0.canZoom
            if (r2 == 0) goto L_0x05df
            int r2 = r20.getPointerCount()
            if (r2 != r7) goto L_0x05df
            int r2 = r0.paintViewTouched
            if (r2 != r8) goto L_0x0583
            r11 = 0
            r13 = 0
            r15 = 3
            r16 = 0
            r17 = 0
            r18 = 0
            android.view.MotionEvent r2 = android.view.MotionEvent.obtain(r11, r13, r15, r16, r17, r18)
            org.telegram.ui.Components.PhotoPaintView r3 = r0.photoPaintView
            r3.onTouch(r2)
            r2.recycle()
            r0.paintViewTouched = r7
        L_0x0583:
            float r2 = r1.getX(r8)
            float r3 = r1.getX(r9)
            float r2 = r2 - r3
            double r2 = (double) r2
            float r4 = r1.getY(r8)
            float r5 = r1.getY(r9)
            float r4 = r4 - r5
            double r4 = (double) r4
            double r2 = java.lang.Math.hypot(r2, r4)
            float r2 = (float) r2
            r0.pinchStartDistance = r2
            float r2 = r0.scale
            r0.pinchStartScale = r2
            float r2 = r1.getX(r9)
            float r3 = r1.getX(r8)
            float r2 = r2 + r3
            float r2 = r2 / r10
            r0.pinchCenterX = r2
            float r2 = r1.getY(r9)
            float r1 = r1.getY(r8)
            float r2 = r2 + r1
            float r2 = r2 / r10
            r0.pinchCenterY = r2
            float r1 = r0.translationX
            r0.pinchStartX = r1
            float r1 = r0.translationY
            r0.pinchStartY = r1
            r0.zooming = r8
            r0.moving = r9
            int r1 = r0.currentEditMode
            if (r1 != r6) goto L_0x05d4
            float r1 = r0.pinchCenterX
            r0.moveStartX = r1
            r0.moveStartY = r2
            r0.draggingDown = r9
            r0.canDragDown = r9
        L_0x05d4:
            r19.hidePressedDrawables()
            android.view.VelocityTracker r1 = r0.velocityTracker
            if (r1 == 0) goto L_0x063f
            r1.clear()
            goto L_0x063f
        L_0x05df:
            int r2 = r20.getPointerCount()
            if (r2 != r8) goto L_0x063f
            int r2 = r0.currentEditMode
            if (r2 != r6) goto L_0x0626
            int r2 = r0.paintViewTouched
            if (r2 != 0) goto L_0x063f
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            android.graphics.Rect r3 = r0.hitRect
            r2.getHitRect(r3)
            android.graphics.Rect r2 = r0.hitRect
            float r3 = r20.getX()
            int r3 = (int) r3
            float r4 = r20.getY()
            int r4 = (int) r4
            boolean r2 = r2.contains(r3, r4)
            if (r2 == 0) goto L_0x063f
            android.view.MotionEvent r1 = android.view.MotionEvent.obtain(r20)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            float r2 = r2.getX()
            float r2 = -r2
            org.telegram.ui.Components.PhotoPaintView r3 = r0.photoPaintView
            float r3 = r3.getY()
            float r3 = -r3
            r1.offsetLocation(r2, r3)
            org.telegram.ui.Components.PhotoPaintView r2 = r0.photoPaintView
            r2.onTouch(r1)
            r1.recycle()
            r0.paintViewTouched = r8
            goto L_0x063f
        L_0x0626:
            float r2 = r20.getX()
            r0.moveStartX = r2
            float r1 = r20.getY()
            r0.moveStartY = r1
            r0.dragY = r1
            r0.draggingDown = r9
            r0.canDragDown = r8
            android.view.VelocityTracker r1 = r0.velocityTracker
            if (r1 == 0) goto L_0x063f
            r1.clear()
        L_0x063f:
            return r9
        L_0x0640:
            int r1 = r20.getAction()
            if (r1 != r8) goto L_0x0649
            r0.closeCaptionEnter(r8)
        L_0x0649:
            return r8
        L_0x064a:
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
    /* JADX WARNING: Removed duplicated region for block: B:122:0x023c  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0320  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0322  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x038c  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0399  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x03ce  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x03d5  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x040f  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x049c  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x04ba  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0577  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0579  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0587  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x0589  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x058c  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x05a3  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x05a5  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x05a8  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x05b3  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x05b8  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x06cf  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x072c  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x073b  */
    /* JADX WARNING: Removed duplicated region for block: B:303:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"NewApi", "DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r25) {
        /*
            r24 = this;
            r1 = r24
            r2 = r25
            int r0 = r1.animationInProgress
            r3 = 1
            if (r0 == r3) goto L_0x0775
            r4 = 3
            if (r0 == r4) goto L_0x0775
            boolean r5 = r1.isVisible
            r6 = 2
            if (r5 != 0) goto L_0x0019
            if (r0 == r6) goto L_0x0019
            boolean r0 = r1.pipAnimationInProgress
            if (r0 != 0) goto L_0x0019
            goto L_0x0775
        L_0x0019:
            boolean r0 = r1.padImageForHorizontalInsets
            r5 = 0
            if (r0 == 0) goto L_0x0030
            r25.save()
            int r0 = r24.getLeftInset()
            int r0 = r0 / r6
            int r7 = r24.getRightInset()
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
            boolean r4 = r0 instanceof org.telegram.messenger.MediaController.PhotoEntry
            if (r4 == 0) goto L_0x012c
            org.telegram.messenger.MediaController$PhotoEntry r0 = (org.telegram.messenger.MediaController.PhotoEntry) r0
            org.telegram.messenger.VideoEditedInfo r4 = r24.getCurrentVideoEditedInfo()
            r0.editedInfo = r4
        L_0x012c:
            int r0 = r1.switchImageAfterAnimation
            if (r0 != r3) goto L_0x0139
            org.telegram.ui.-$$Lambda$PhotoViewer$xeEXfOYZyO2ny1QI3rPToT4hDRA r0 = new org.telegram.ui.-$$Lambda$PhotoViewer$xeEXfOYZyO2ny1QI3rPToT4hDRA
            r0.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x0144
        L_0x0139:
            r4 = 2
            if (r0 != r4) goto L_0x0144
            org.telegram.ui.-$$Lambda$PhotoViewer$1vsK74fMt4-OQt89nm0CxwS11gA r0 = new org.telegram.ui.-$$Lambda$PhotoViewer$1vsK74fMt4-OQt89nm0CxwS11gA
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
            int r6 = r1.animationInProgress
            r12 = 1132396544(0x437var_, float:255.0)
            r14 = 2
            if (r6 == r14) goto L_0x01ae
            boolean r6 = r1.pipAnimationInProgress
            if (r6 != 0) goto L_0x01ae
            boolean r6 = r1.isInline
            if (r6 != 0) goto L_0x01ae
            int r6 = r1.currentEditMode
            if (r6 != 0) goto L_0x01a7
            int r6 = r1.sendPhotoType
            if (r6 == r3) goto L_0x01a7
            float r6 = r1.scale
            int r6 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r6 != 0) goto L_0x01a7
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 == 0) goto L_0x01a7
            boolean r6 = r1.zoomAnimation
            if (r6 != 0) goto L_0x01a7
            int r6 = r24.getContainerViewHeight()
            float r6 = (float) r6
            r14 = 1082130432(0x40800000, float:4.0)
            float r6 = r6 / r14
            org.telegram.ui.PhotoViewer$BackgroundDrawable r14 = r1.backgroundDrawable
            r15 = 1123942400(0x42fe0000, float:127.0)
            float r0 = java.lang.Math.abs(r0)
            float r0 = java.lang.Math.min(r0, r6)
            float r0 = r0 / r6
            float r0 = r11 - r0
            float r0 = r0 * r12
            float r0 = java.lang.Math.max(r15, r0)
            int r0 = (int) r0
            r14.setAlpha(r0)
            goto L_0x01ae
        L_0x01a7:
            org.telegram.ui.PhotoViewer$BackgroundDrawable r0 = r1.backgroundDrawable
            r6 = 255(0xff, float:3.57E-43)
            r0.setAlpha(r6)
        L_0x01ae:
            r6 = 0
            r1.sideImage = r6
            int r0 = r1.currentEditMode
            if (r0 != 0) goto L_0x01f9
            int r0 = r1.sendPhotoType
            if (r0 == r3) goto L_0x01f9
            float r0 = r1.scale
            int r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r0 < 0) goto L_0x01f0
            boolean r0 = r1.zoomAnimation
            if (r0 != 0) goto L_0x01f0
            boolean r0 = r1.zooming
            if (r0 != 0) goto L_0x01f0
            float r0 = r1.maxX
            r14 = 1084227584(0x40a00000, float:5.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r15 = (float) r15
            float r0 = r0 + r15
            int r0 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x01da
            org.telegram.messenger.ImageReceiver r0 = r1.leftImage
            r1.sideImage = r0
            goto L_0x01f0
        L_0x01da:
            float r0 = r1.minX
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            float r0 = r0 - r14
            int r0 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r0 >= 0) goto L_0x01eb
            org.telegram.messenger.ImageReceiver r0 = r1.rightImage
            r1.sideImage = r0
            goto L_0x01f0
        L_0x01eb:
            org.telegram.ui.Components.GroupedPhotosListView r0 = r1.groupedPhotosListView
            r0.setMoveProgress(r5)
        L_0x01f0:
            org.telegram.messenger.ImageReceiver r0 = r1.sideImage
            if (r0 == 0) goto L_0x01f6
            r0 = 1
            goto L_0x01f7
        L_0x01f6:
            r0 = 0
        L_0x01f7:
            r1.changingPage = r0
        L_0x01f9:
            org.telegram.messenger.ImageReceiver r0 = r1.sideImage
            org.telegram.messenger.ImageReceiver r14 = r1.rightImage
            r15 = 1050253722(0x3e99999a, float:0.3)
            r16 = 1073741824(0x40000000, float:2.0)
            r18 = 1106247680(0x41var_, float:30.0)
            if (r0 != r14) goto L_0x02e9
            boolean r0 = r1.zoomAnimation
            if (r0 != 0) goto L_0x0230
            float r0 = r1.minX
            int r14 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r14 >= 0) goto L_0x0230
            float r0 = r0 - r7
            int r14 = r24.getContainerViewWidth()
            float r14 = (float) r14
            float r0 = r0 / r14
            float r0 = java.lang.Math.min(r11, r0)
            float r14 = r11 - r0
            float r14 = r14 * r15
            int r6 = r24.getContainerViewWidth()
            int r6 = -r6
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r17 = 2
            int r19 = r19 / 2
            int r6 = r6 - r19
            float r6 = (float) r6
            goto L_0x0234
        L_0x0230:
            r6 = r7
            r0 = 1065353216(0x3var_, float:1.0)
            r14 = 0
        L_0x0234:
            org.telegram.messenger.ImageReceiver r12 = r1.sideImage
            boolean r12 = r12.hasBitmapImage()
            if (r12 == 0) goto L_0x02a9
            r25.save()
            int r12 = r24.getContainerViewWidth()
            r17 = 2
            int r12 = r12 / 2
            float r12 = (float) r12
            int r20 = r24.getContainerViewHeight()
            int r8 = r20 / 2
            float r8 = (float) r8
            r2.translate(r12, r8)
            int r8 = r24.getContainerViewWidth()
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r12 = r12 / 2
            int r8 = r8 + r12
            float r8 = (float) r8
            float r8 = r8 + r6
            r2.translate(r8, r5)
            float r8 = r11 - r14
            r2.scale(r8, r8)
            org.telegram.messenger.ImageReceiver r8 = r1.sideImage
            int r8 = r8.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r12 = r1.sideImage
            int r12 = r12.getBitmapHeight()
            int r5 = r24.getContainerViewWidth()
            float r5 = (float) r5
            float r8 = (float) r8
            float r5 = r5 / r8
            int r15 = r24.getContainerViewHeight()
            float r15 = (float) r15
            float r12 = (float) r12
            float r15 = r15 / r12
            float r5 = java.lang.Math.min(r5, r15)
            float r8 = r8 * r5
            int r8 = (int) r8
            float r12 = r12 * r5
            int r5 = (int) r12
            org.telegram.messenger.ImageReceiver r12 = r1.sideImage
            r12.setAlpha(r0)
            org.telegram.messenger.ImageReceiver r12 = r1.sideImage
            int r15 = -r8
            r17 = 2
            int r15 = r15 / 2
            float r15 = (float) r15
            int r3 = -r5
            int r3 = r3 / 2
            float r3 = (float) r3
            float r8 = (float) r8
            float r5 = (float) r5
            r12.setImageCoords(r15, r3, r8, r5)
            org.telegram.messenger.ImageReceiver r3 = r1.sideImage
            r3.draw(r2)
            r25.restore()
        L_0x02a9:
            org.telegram.ui.Components.GroupedPhotosListView r3 = r1.groupedPhotosListView
            float r5 = -r0
            r3.setMoveProgress(r5)
            r25.save()
            float r3 = r4 / r13
            r2.translate(r6, r3)
            int r3 = r24.getContainerViewWidth()
            float r3 = (float) r3
            float r5 = r1.scale
            float r5 = r5 + r11
            float r3 = r3 * r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r5 = (float) r5
            float r3 = r3 + r5
            float r3 = r3 / r16
            float r5 = -r4
            float r5 = r5 / r13
            r2.translate(r3, r5)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r3 = r1.photoProgressViews
            r5 = 1
            r3 = r3[r5]
            float r6 = r11 - r14
            r3.setScale(r6)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r3 = r1.photoProgressViews
            r3 = r3[r5]
            r3.setAlpha(r0)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r0 = r1.photoProgressViews
            r0 = r0[r5]
            r0.onDraw(r2)
            r25.restore()
        L_0x02e9:
            boolean r0 = r1.zoomAnimation
            if (r0 != 0) goto L_0x0312
            float r0 = r1.maxX
            int r3 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
            if (r3 <= 0) goto L_0x0312
            int r3 = r1.currentEditMode
            if (r3 != 0) goto L_0x0312
            int r3 = r1.sendPhotoType
            r5 = 1
            if (r3 == r5) goto L_0x0312
            float r0 = r7 - r0
            int r3 = r24.getContainerViewWidth()
            float r3 = (float) r3
            float r0 = r0 / r3
            float r0 = java.lang.Math.min(r11, r0)
            r3 = 1050253722(0x3e99999a, float:0.3)
            float r3 = r3 * r0
            float r0 = r11 - r0
            float r5 = r1.maxX
            goto L_0x0316
        L_0x0312:
            r5 = r7
            r0 = 1065353216(0x3var_, float:1.0)
            r3 = 0
        L_0x0316:
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r6 = r1.aspectRatioFrameLayout
            if (r6 == 0) goto L_0x0322
            int r6 = r6.getVisibility()
            if (r6 != 0) goto L_0x0322
            r6 = 1
            goto L_0x0323
        L_0x0322:
            r6 = 0
        L_0x0323:
            org.telegram.messenger.ImageReceiver r8 = r1.centerImage
            boolean r8 = r8.hasBitmapImage()
            if (r8 != 0) goto L_0x033a
            if (r6 == 0) goto L_0x0332
            boolean r8 = r1.textureUploaded
            if (r8 == 0) goto L_0x0332
            goto L_0x033a
        L_0x0332:
            r22 = r3
            r21 = r7
            r23 = r13
            goto L_0x0568
        L_0x033a:
            r25.save()
            int r8 = r24.getContainerViewWidth()
            r12 = 2
            int r8 = r8 / r12
            int r14 = r24.getAdditionX()
            int r8 = r8 + r14
            float r8 = (float) r8
            int r14 = r24.getContainerViewHeight()
            int r14 = r14 / r12
            int r12 = r24.getAdditionY()
            int r14 = r14 + r12
            float r12 = (float) r14
            r2.translate(r8, r12)
            int r8 = r1.currentPanTranslationY
            float r8 = (float) r8
            float r8 = r8 + r4
            r2.translate(r5, r8)
            float r8 = r13 - r3
            r2.scale(r8, r8)
            int r8 = r1.currentEditMode
            r12 = 3
            if (r8 != r12) goto L_0x0386
            int r8 = r1.keyboardSize
            r12 = 1101004800(0x41a00000, float:20.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            if (r8 <= r12) goto L_0x0386
            r8 = 1
            r12 = 0
            int r14 = r1.getContainerViewHeight(r8, r12)
            int r8 = r1.getContainerViewHeight(r12, r12)
            if (r14 == r8) goto L_0x0386
            int r14 = r14 - r8
            r8 = 2
            int r14 = r14 / r8
            float r8 = (float) r14
            r12 = 0
            r2.translate(r12, r8)
        L_0x0386:
            if (r6 == 0) goto L_0x0399
            boolean r8 = r1.textureUploaded
            if (r8 == 0) goto L_0x0399
            android.view.TextureView r8 = r1.videoTextureView
            int r8 = r8.getMeasuredWidth()
            android.view.TextureView r12 = r1.videoTextureView
            int r12 = r12.getMeasuredHeight()
            goto L_0x03a5
        L_0x0399:
            org.telegram.messenger.ImageReceiver r8 = r1.centerImage
            int r8 = r8.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r12 = r1.centerImage
            int r12 = r12.getBitmapHeight()
        L_0x03a5:
            int r14 = r24.getContainerViewWidth()
            float r14 = (float) r14
            float r8 = (float) r8
            float r14 = r14 / r8
            int r15 = r24.getContainerViewHeight()
            float r15 = (float) r15
            float r12 = (float) r12
            float r15 = r15 / r12
            float r14 = java.lang.Math.min(r14, r15)
            float r8 = r8 * r14
            int r8 = (int) r8
            float r12 = r12 * r14
            int r12 = (int) r12
            if (r6 == 0) goto L_0x03d5
            boolean r15 = r1.textureUploaded
            if (r15 == 0) goto L_0x03d5
            boolean r15 = r1.videoCrossfadeStarted
            if (r15 == 0) goto L_0x03d5
            float r15 = r1.videoCrossfadeAlpha
            int r15 = (r15 > r11 ? 1 : (r15 == r11 ? 0 : -1))
            if (r15 == 0) goto L_0x03ce
            goto L_0x03d5
        L_0x03ce:
            r22 = r3
            r21 = r7
            r23 = r13
            goto L_0x03f6
        L_0x03d5:
            org.telegram.messenger.ImageReceiver r15 = r1.centerImage
            r15.setAlpha(r0)
            org.telegram.messenger.ImageReceiver r15 = r1.centerImage
            int r11 = -r8
            r17 = 2
            int r11 = r11 / 2
            float r11 = (float) r11
            r21 = r7
            int r7 = -r12
            int r7 = r7 / 2
            float r7 = (float) r7
            r22 = r3
            float r3 = (float) r8
            r23 = r13
            float r13 = (float) r12
            r15.setImageCoords(r11, r7, r3, r13)
            org.telegram.messenger.ImageReceiver r3 = r1.centerImage
            r3.draw(r2)
        L_0x03f6:
            if (r6 != 0) goto L_0x0400
            org.telegram.ui.Components.PaintingOverlay r3 = r1.paintingOverlay
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x040d
        L_0x0400:
            int r3 = -r8
            r7 = 2
            int r3 = r3 / r7
            float r3 = (float) r3
            int r11 = -r12
            int r11 = r11 / r7
            float r7 = (float) r11
            r2.translate(r3, r7)
            r2.scale(r14, r14)
        L_0x040d:
            if (r6 == 0) goto L_0x0494
            boolean r3 = r1.videoCrossfadeStarted
            if (r3 != 0) goto L_0x0428
            boolean r3 = r1.textureUploaded
            if (r3 == 0) goto L_0x0428
            r3 = 1
            r1.videoCrossfadeStarted = r3
            r3 = 0
            r1.videoCrossfadeAlpha = r3
            long r6 = java.lang.System.currentTimeMillis()
            r1.videoCrossfadeAlphaLastTime = r6
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r3 = r1.containerView
            r3.getMeasuredHeight()
        L_0x0428:
            android.view.TextureView r3 = r1.videoTextureView
            float r6 = r1.videoCrossfadeAlpha
            float r6 = r6 * r0
            r3.setAlpha(r6)
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r3 = r1.aspectRatioFrameLayout
            r3.draw(r2)
            android.view.TextureView r3 = r1.videoTextureView
            boolean r6 = r3 instanceof org.telegram.ui.Components.VideoEditTextureView
            if (r6 == 0) goto L_0x0462
            org.telegram.ui.Components.VideoEditTextureView r3 = (org.telegram.ui.Components.VideoEditTextureView) r3
            int r6 = r24.getContainerViewWidth()
            int r6 = r6 - r8
            r7 = 2
            int r6 = r6 / r7
            int r11 = r24.getAdditionX()
            int r6 = r6 + r11
            float r6 = (float) r6
            float r6 = r6 + r5
            int r11 = r24.getContainerViewHeight()
            int r11 = r11 - r12
            int r11 = r11 / r7
            int r7 = r24.getAdditionY()
            int r11 = r11 + r7
            float r7 = (float) r11
            float r7 = r7 + r4
            int r11 = r1.currentPanTranslationY
            float r11 = (float) r11
            float r7 = r7 + r11
            float r8 = (float) r8
            float r11 = (float) r12
            r3.setViewRect(r6, r7, r8, r11)
        L_0x0462:
            boolean r3 = r1.videoCrossfadeStarted
            if (r3 == 0) goto L_0x048b
            float r3 = r1.videoCrossfadeAlpha
            r6 = 1065353216(0x3var_, float:1.0)
            int r7 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r7 >= 0) goto L_0x048b
            float r6 = (float) r9
            boolean r7 = r1.playerInjected
            if (r7 == 0) goto L_0x0476
            r7 = 1120403456(0x42CLASSNAME, float:100.0)
            goto L_0x0478
        L_0x0476:
            r7 = 1128792064(0x43480000, float:200.0)
        L_0x0478:
            float r6 = r6 / r7
            float r3 = r3 + r6
            r1.videoCrossfadeAlpha = r3
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r3 = r1.containerView
            r3.invalidate()
            float r3 = r1.videoCrossfadeAlpha
            r6 = 1065353216(0x3var_, float:1.0)
            int r3 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r3 <= 0) goto L_0x048b
            r1.videoCrossfadeAlpha = r6
        L_0x048b:
            org.telegram.ui.Components.PaintingOverlay r3 = r1.paintingOverlay
            float r6 = r1.videoCrossfadeAlpha
            float r6 = r6 * r0
            r3.setAlpha(r6)
        L_0x0494:
            org.telegram.ui.Components.PaintingOverlay r3 = r1.paintingOverlay
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x04b1
            org.telegram.ui.Components.PaintingOverlay r3 = r1.paintingOverlay
            int r3 = r3.getMeasuredWidth()
            org.telegram.ui.Components.PaintingOverlay r6 = r1.paintingOverlay
            int r6 = r6.getMeasuredHeight()
            r7 = 0
            r2.clipRect(r7, r7, r3, r6)
            org.telegram.ui.Components.PaintingOverlay r3 = r1.paintingOverlay
            r3.draw(r2)
        L_0x04b1:
            r25.restore()
            r3 = 0
        L_0x04b5:
            android.graphics.drawable.GradientDrawable[] r6 = r1.pressedDrawable
            int r6 = r6.length
            if (r3 >= r6) goto L_0x0568
            boolean[] r6 = r1.drawPressedDrawable
            boolean r6 = r6[r3]
            if (r6 != 0) goto L_0x04cd
            float[] r6 = r1.pressedDrawableAlpha
            r6 = r6[r3]
            r7 = 0
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 == 0) goto L_0x04ca
            goto L_0x04cd
        L_0x04ca:
            r8 = 1132396544(0x437var_, float:255.0)
            goto L_0x0520
        L_0x04cd:
            android.graphics.drawable.GradientDrawable[] r6 = r1.pressedDrawable
            r6 = r6[r3]
            float[] r7 = r1.pressedDrawableAlpha
            r7 = r7[r3]
            r8 = 1132396544(0x437var_, float:255.0)
            float r7 = r7 * r8
            int r7 = (int) r7
            r6.setAlpha(r7)
            if (r3 != 0) goto L_0x04f6
            android.graphics.drawable.GradientDrawable[] r6 = r1.pressedDrawable
            r6 = r6[r3]
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r7 = r1.containerView
            int r7 = r7.getMeasuredWidth()
            int r7 = r7 / 5
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r11 = r1.containerView
            int r11 = r11.getMeasuredHeight()
            r12 = 0
            r6.setBounds(r12, r12, r7, r11)
            goto L_0x0519
        L_0x04f6:
            android.graphics.drawable.GradientDrawable[] r6 = r1.pressedDrawable
            r6 = r6[r3]
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r7 = r1.containerView
            int r7 = r7.getMeasuredWidth()
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r11 = r1.containerView
            int r11 = r11.getMeasuredWidth()
            int r11 = r11 / 5
            int r7 = r7 - r11
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r11 = r1.containerView
            int r11 = r11.getMeasuredWidth()
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r12 = r1.containerView
            int r12 = r12.getMeasuredHeight()
            r13 = 0
            r6.setBounds(r7, r13, r11, r12)
        L_0x0519:
            android.graphics.drawable.GradientDrawable[] r6 = r1.pressedDrawable
            r6 = r6[r3]
            r6.draw(r2)
        L_0x0520:
            boolean[] r6 = r1.drawPressedDrawable
            boolean r6 = r6[r3]
            r7 = 1127481344(0x43340000, float:180.0)
            if (r6 == 0) goto L_0x0547
            float[] r6 = r1.pressedDrawableAlpha
            r11 = r6[r3]
            r12 = 1065353216(0x3var_, float:1.0)
            int r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r11 >= 0) goto L_0x0564
            r11 = r6[r3]
            float r13 = (float) r9
            float r13 = r13 / r7
            float r11 = r11 + r13
            r6[r3] = r11
            r7 = r6[r3]
            int r7 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1))
            if (r7 <= 0) goto L_0x0541
            r6[r3] = r12
        L_0x0541:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r6 = r1.containerView
            r6.invalidate()
            goto L_0x0564
        L_0x0547:
            float[] r6 = r1.pressedDrawableAlpha
            r11 = r6[r3]
            r12 = 0
            int r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r11 <= 0) goto L_0x0564
            r11 = r6[r3]
            float r13 = (float) r9
            float r13 = r13 / r7
            float r11 = r11 - r13
            r6[r3] = r11
            r7 = r6[r3]
            int r7 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1))
            if (r7 >= 0) goto L_0x055f
            r6[r3] = r12
        L_0x055f:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r6 = r1.containerView
            r6.invalidate()
        L_0x0564:
            int r3 = r3 + 1
            goto L_0x04b5
        L_0x0568:
            boolean r3 = r1.isCurrentVideo
            if (r3 == 0) goto L_0x0579
            org.telegram.ui.Components.VideoPlayer r3 = r1.videoPlayer
            if (r3 == 0) goto L_0x0579
            boolean r3 = r3.isPlaying()
            if (r3 != 0) goto L_0x0577
            goto L_0x0579
        L_0x0577:
            r3 = 0
            goto L_0x057a
        L_0x0579:
            r3 = 1
        L_0x057a:
            org.telegram.ui.Components.RadialProgressView r6 = r1.miniProgressView
            int r6 = r6.getVisibility()
            if (r6 == 0) goto L_0x0589
            android.animation.AnimatorSet r6 = r1.miniProgressAnimator
            if (r6 == 0) goto L_0x0587
            goto L_0x0589
        L_0x0587:
            r6 = 0
            goto L_0x058a
        L_0x0589:
            r6 = 1
        L_0x058a:
            if (r3 == 0) goto L_0x05e3
            boolean r3 = r1.zoomAnimation
            if (r3 != 0) goto L_0x059c
            float r3 = -r5
            float r7 = r1.maxX
            int r3 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r3 <= 0) goto L_0x059c
            float r12 = r5 + r7
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x059f
        L_0x059c:
            r3 = 1065353216(0x3var_, float:1.0)
            r12 = 0
        L_0x059f:
            int r7 = (r23 > r3 ? 1 : (r23 == r3 ? 0 : -1))
            if (r7 != 0) goto L_0x05a5
            r7 = r4
            goto L_0x05a6
        L_0x05a5:
            r7 = 0
        L_0x05a6:
            if (r6 == 0) goto L_0x05b3
            org.telegram.ui.Components.RadialProgressView r8 = r1.miniProgressView
            float r8 = r8.getAlpha()
            float r11 = r3 - r8
            float r11 = r11 * r0
            goto L_0x05b4
        L_0x05b3:
            r11 = r0
        L_0x05b4:
            boolean r3 = r1.pipAnimationInProgress
            if (r3 == 0) goto L_0x05c0
            org.telegram.ui.ActionBar.ActionBar r3 = r1.actionBar
            float r3 = r3.getAlpha()
            float r11 = r11 * r3
        L_0x05c0:
            r25.save()
            r2.translate(r12, r7)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r3 = r1.photoProgressViews
            r7 = 0
            r3 = r3[r7]
            r8 = 1065353216(0x3var_, float:1.0)
            float r9 = r8 - r22
            r3.setScale(r9)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r3 = r1.photoProgressViews
            r3 = r3[r7]
            r3.setAlpha(r11)
            org.telegram.ui.PhotoViewer$PhotoProgressView[] r3 = r1.photoProgressViews
            r3 = r3[r7]
            r3.onDraw(r2)
            r25.restore()
        L_0x05e3:
            if (r6 == 0) goto L_0x0609
            boolean r3 = r1.pipAnimationInProgress
            if (r3 != 0) goto L_0x0609
            r25.save()
            org.telegram.ui.Components.RadialProgressView r3 = r1.miniProgressView
            int r3 = r3.getLeft()
            float r3 = (float) r3
            float r3 = r3 + r5
            org.telegram.ui.Components.RadialProgressView r5 = r1.miniProgressView
            int r5 = r5.getTop()
            float r5 = (float) r5
            float r6 = r4 / r23
            float r5 = r5 + r6
            r2.translate(r3, r5)
            org.telegram.ui.Components.RadialProgressView r3 = r1.miniProgressView
            r3.draw(r2)
            r25.restore()
        L_0x0609:
            org.telegram.messenger.ImageReceiver r3 = r1.sideImage
            org.telegram.messenger.ImageReceiver r5 = r1.leftImage
            if (r3 != r5) goto L_0x06cb
            boolean r3 = r3.hasBitmapImage()
            if (r3 == 0) goto L_0x0684
            r25.save()
            int r3 = r24.getContainerViewWidth()
            r5 = 2
            int r3 = r3 / r5
            float r3 = (float) r3
            int r6 = r24.getContainerViewHeight()
            int r6 = r6 / r5
            float r5 = (float) r6
            r2.translate(r3, r5)
            int r3 = r24.getContainerViewWidth()
            float r3 = (float) r3
            float r5 = r1.scale
            r6 = 1065353216(0x3var_, float:1.0)
            float r5 = r5 + r6
            float r3 = r3 * r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r5 = (float) r5
            float r3 = r3 + r5
            float r3 = -r3
            float r3 = r3 / r16
            float r3 = r3 + r21
            r5 = 0
            r2.translate(r3, r5)
            org.telegram.messenger.ImageReceiver r3 = r1.sideImage
            int r3 = r3.getBitmapWidth()
            org.telegram.messenger.ImageReceiver r5 = r1.sideImage
            int r5 = r5.getBitmapHeight()
            int r6 = r24.getContainerViewWidth()
            float r6 = (float) r6
            float r3 = (float) r3
            float r6 = r6 / r3
            int r7 = r24.getContainerViewHeight()
            float r7 = (float) r7
            float r5 = (float) r5
            float r7 = r7 / r5
            float r6 = java.lang.Math.min(r6, r7)
            float r3 = r3 * r6
            int r3 = (int) r3
            float r5 = r5 * r6
            int r5 = (int) r5
            org.telegram.messenger.ImageReceiver r6 = r1.sideImage
            r7 = 1065353216(0x3var_, float:1.0)
            r6.setAlpha(r7)
            org.telegram.messenger.ImageReceiver r6 = r1.sideImage
            int r7 = -r3
            r8 = 2
            int r7 = r7 / r8
            float r7 = (float) r7
            int r9 = -r5
            int r9 = r9 / r8
            float r8 = (float) r9
            float r3 = (float) r3
            float r5 = (float) r5
            r6.setImageCoords(r7, r8, r3, r5)
            org.telegram.messenger.ImageReceiver r3 = r1.sideImage
            r3.draw(r2)
            r25.restore()
        L_0x0684:
            org.telegram.ui.Components.GroupedPhotosListView r3 = r1.groupedPhotosListView
            r5 = 1065353216(0x3var_, float:1.0)
            float r11 = r5 - r0
            r3.setMoveProgress(r11)
            r25.save()
            float r0 = r4 / r23
            r7 = r21
            r2.translate(r7, r0)
            int r0 = r24.getContainerViewWidth()
            float r0 = (float) r0
            float r3 = r1.scale
            float r3 = r3 + r5
            float r0 = r0 * r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            float r0 = r0 + r3
            float r0 = -r0
            float r0 = r0 / r16
            float r3 = -r4
            float r3 = r3 / r23
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
            r25.restore()
        L_0x06cb:
            int r0 = r1.waitingForDraw
            if (r0 == 0) goto L_0x0728
            r3 = 1
            int r0 = r0 - r3
            r1.waitingForDraw = r0
            if (r0 != 0) goto L_0x0723
            android.widget.ImageView r0 = r1.textureImageView
            if (r0 == 0) goto L_0x071a
            android.view.TextureView r0 = r1.videoTextureView     // Catch:{ all -> 0x06f3 }
            int r0 = r0.getWidth()     // Catch:{ all -> 0x06f3 }
            android.view.TextureView r3 = r1.videoTextureView     // Catch:{ all -> 0x06f3 }
            int r3 = r3.getHeight()     // Catch:{ all -> 0x06f3 }
            android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x06f3 }
            android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createBitmap(r0, r3, r5)     // Catch:{ all -> 0x06f3 }
            r1.currentBitmap = r0     // Catch:{ all -> 0x06f3 }
            android.view.TextureView r3 = r1.changedTextureView     // Catch:{ all -> 0x06f3 }
            r3.getBitmap(r0)     // Catch:{ all -> 0x06f3 }
            goto L_0x0701
        L_0x06f3:
            r0 = move-exception
            android.graphics.Bitmap r3 = r1.currentBitmap
            if (r3 == 0) goto L_0x06fe
            r3.recycle()
            r3 = 0
            r1.currentBitmap = r3
        L_0x06fe:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0701:
            android.graphics.Bitmap r0 = r1.currentBitmap
            if (r0 == 0) goto L_0x0713
            android.widget.ImageView r0 = r1.textureImageView
            r3 = 0
            r0.setVisibility(r3)
            android.widget.ImageView r0 = r1.textureImageView
            android.graphics.Bitmap r3 = r1.currentBitmap
            r0.setImageBitmap(r3)
            goto L_0x071a
        L_0x0713:
            android.widget.ImageView r0 = r1.textureImageView
            r3 = 0
            r0.setImageDrawable(r3)
            goto L_0x071b
        L_0x071a:
            r3 = 0
        L_0x071b:
            org.telegram.ui.Components.PipVideoView r0 = r1.pipVideoView
            r0.close()
            r1.pipVideoView = r3
            goto L_0x0728
        L_0x0723:
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r0 = r1.containerView
            r0.invalidate()
        L_0x0728:
            boolean r0 = r1.padImageForHorizontalInsets
            if (r0 == 0) goto L_0x072f
            r25.restore()
        L_0x072f:
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r0 = r1.aspectRatioFrameLayout
            if (r0 == 0) goto L_0x0775
            org.telegram.ui.Components.VideoForwardDrawable r0 = r1.videoForwardDrawable
            boolean r0 = r0.isAnimating()
            if (r0 == 0) goto L_0x0775
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
            float r4 = r4 / r23
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
        L_0x0775:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onDraw(android.graphics.Canvas):void");
    }

    public /* synthetic */ void lambda$onDraw$55$PhotoViewer() {
        switchToNextIndex(1, false);
    }

    public /* synthetic */ void lambda$onDraw$56$PhotoViewer() {
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
                    int access$14500 = photoProgressViewArr[0].backgroundState;
                    if (x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                        if (!z) {
                            if (access$14500 > 0 && access$14500 <= 3) {
                                onActionClick(true);
                                checkProgress(0, false, true);
                                return true;
                            }
                        } else if ((access$14500 == 3 || access$14500 == 4) && this.photoProgressViews[0].isVisible()) {
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
                    int access$145002 = this.photoProgressViews[0].backgroundState;
                    if (access$145002 > 0 && access$145002 <= 3) {
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
                this.videoPlayer.setVolume(1.0f);
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
            this.videoTimelineView.setMaxProgressDiff(30000.0f / this.videoDuration);
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
                int i9 = (int) (((float) (this.audioFramesSize + this.videoFramesSize)) * (((float) this.estimatedDuration) / this.videoDuration));
                this.estimatedSize = i9;
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
                preparePlayer(this.currentPlayingVideoFile, false, false, this.currentSavedFilterState);
                this.videoPlayer.seekTo((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration));
            }
        } else if (this.resultHeight == this.originalHeight && this.resultWidth == this.originalWidth) {
            this.tryStartRequestPreviewOnFinish = false;
            PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
            photoProgressViewArr[0].setProgress(0.0f, photoProgressViewArr[0].backgroundState == 0 || this.photoProgressViews[0].previousBackgroundState == 0);
            this.photoProgressViews[0].setBackgroundState(3, false);
            if (!z2) {
                preparePlayer(this.currentPlayingVideoFile, false, false, this.currentSavedFilterState);
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
        Object obj = this.imagesArrLocals.get(this.currentIndex);
        if (obj instanceof MediaController.PhotoEntry) {
            ((MediaController.PhotoEntry) obj).editedInfo = getCurrentVideoEditedInfo();
        }
        this.compressionsCount = -1;
        this.rotationValue = 0;
        this.videoFramerate = 25;
        this.originalSize = new File(str).length();
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        AnonymousClass53 r6 = new Runnable() {
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
                                PhotoViewer.AnonymousClass53.this.lambda$run$0$PhotoViewer$53(this.f$1, this.f$2);
                            }
                        });
                    }
                }
            }

            public /* synthetic */ void lambda$run$0$PhotoViewer$53(int[] iArr, int i) {
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
                        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                        PhotoViewer photoViewer6 = PhotoViewer.this;
                        int unused13 = photoViewer6.selectedCompression = globalMainSettings.getInt("compress_video2", photoViewer6.selectCompression());
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
                            try {
                                MediaCodecInfo selectCodec = MediaController.selectCodec("video/avc");
                                if (selectCodec == null) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("no codec info for video/avc");
                                    }
                                    boolean unused15 = PhotoViewer.this.videoConvertSupported = false;
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
                                    boolean unused16 = PhotoViewer.this.videoConvertSupported = false;
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
                        int unused17 = PhotoViewer.this.compressionsCount = 0;
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
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.compressItemAnimation = animatorSet2;
                    Animator[] animatorArr = new Animator[3];
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
                    if (!this.videoConvertSupported) {
                        f = 0.5f;
                    }
                    fArr3[0] = f;
                    animatorArr[2] = ObjectAnimator.ofFloat(imageView4, property3, fArr3);
                    animatorSet2.playTogether(animatorArr);
                    this.compressItemAnimation.setDuration(180);
                    this.compressItemAnimation.setInterpolator(decelerateInterpolator);
                    this.compressItemAnimation.start();
                    return;
                }
                ImageView imageView5 = this.compressItem;
                if (!z) {
                    f = 0.5f;
                }
                imageView5.setAlpha(f);
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
