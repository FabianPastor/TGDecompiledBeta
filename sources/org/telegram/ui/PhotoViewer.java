package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
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
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
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
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
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
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebView;
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
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import androidx.core.widget.NestedScrollView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerEnd;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
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
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SecureDocument;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.video.VideoPlayerRewinder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$PageBlock;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.tgnet.TLRPC$TL_inputPhoto;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photoEmpty;
import org.telegram.tgnet.TLRPC$TL_photos_photo;
import org.telegram.tgnet.TLRPC$TL_webDocument;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebDocument;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.PhotoPickerPhotoCell;
import org.telegram.ui.ChooseSpeedLayout;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.Crop.CropAreaView;
import org.telegram.ui.Components.Crop.CropTransform;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FadingTextViewLayout;
import org.telegram.ui.Components.FilterGLThread;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.GestureDetector2;
import org.telegram.ui.Components.GroupedPhotosListView;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.PaintingOverlay;
import org.telegram.ui.Components.PhotoCropView;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.PhotoPaintView;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView;
import org.telegram.ui.Components.PhotoViewerWebView;
import org.telegram.ui.Components.PickerBottomLayoutViewer;
import org.telegram.ui.Components.PipVideoOverlay;
import org.telegram.ui.Components.PlayPauseDrawable;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TextViewSwitcher;
import org.telegram.ui.Components.Tooltip;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMentionPhotoViewer;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.VideoEditTextureView;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayerSeekBar;
import org.telegram.ui.Components.VideoSeekPreviewImage;
import org.telegram.ui.Components.VideoTimelinePlayView;
import org.telegram.ui.Components.ViewHelper;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.PhotoViewer;
@SuppressLint({"WrongConstant"})
/* loaded from: classes3.dex */
public class PhotoViewer implements NotificationCenter.NotificationCenterDelegate, GestureDetector2.OnGestureListener, GestureDetector2.OnDoubleTapListener {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile PhotoViewer Instance;
    private static volatile PhotoViewer PipInstance;
    private static final Property<VideoPlayerControlFrameLayout, Float> VPC_PROGRESS;
    private static DecelerateInterpolator decelerateInterpolator;
    private static Drawable[] progressDrawables;
    private static Paint progressPaint;
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimator;
    private Context activityContext;
    private ActionBarMenuSubItem allMediaItem;
    private boolean allowMentions;
    private boolean allowShare;
    private boolean allowShowFullscreenButton;
    private float animateToMirror;
    private float animateToRotate;
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    private ClippingImageView animatingImageView;
    private Runnable animationEndRunnable;
    private int animationInProgress;
    private long animationStartTime;
    private float animationValue;
    private boolean applying;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private boolean attachedToWindow;
    private long audioFramesSize;
    private float avatarStartProgress;
    private long avatarStartTime;
    private long avatarsDialogId;
    private int bitrate;
    private LinearLayout bottomButtonsLayout;
    private FrameLayout bottomLayout;
    private ImageView cameraItem;
    private boolean canEditAvatar;
    private FrameLayout captionContainer;
    private PhotoViewerCaptionEnterView captionEditText;
    public CharSequence captionForAllMedia;
    private boolean captionHwLayerEnabled;
    private TextView captionLimitView;
    private CaptionScrollView captionScrollView;
    private CaptionTextViewSwitcher captionTextViewSwitcher;
    private boolean centerImageIsVideo;
    private AnimatorSet changeModeAnimation;
    private TextureView changedTextureView;
    private boolean changingPage;
    private boolean changingTextureView;
    private CheckBox checkImageView;
    ChooseSpeedLayout chooseSpeedLayout;
    private int classGuid;
    private float clippingImageProgress;
    private ImageView compressItem;
    private AnimatorSet compressItemAnimation;
    private FrameLayoutDrawer containerView;
    private boolean cropInitied;
    private ImageView cropItem;
    private int currentAccount;
    private AnimatedFileDrawable currentAnimation;
    private Bitmap currentBitmap;
    private TLRPC$BotInlineResult currentBotInlineResult;
    private long currentDialogId;
    private int currentEditMode;
    private ImageLocation currentFileLocation;
    private ImageLocation currentFileLocationVideo;
    private String currentImageFaceKey;
    private int currentImageHasFace;
    private String currentImagePath;
    private int currentIndex;
    private AnimatorSet currentListViewAnimation;
    private Runnable currentLoadingVideoRunnable;
    private MessageObject currentMessageObject;
    private TLRPC$PageBlock currentPageBlock;
    private float currentPanTranslationY;
    private String currentPathObject;
    private PlaceProviderObject currentPlaceObject;
    private Uri currentPlayingVideoFile;
    private SecureDocument currentSecureDocument;
    private String currentSubtitle;
    private ImageReceiver.BitmapHolder currentThumb;
    private boolean currentVideoFinishedLoading;
    private float currentVideoSpeed;
    private int dateOverride;
    private FadingTextViewLayout dateTextView;
    private boolean disableShowCheck;
    private boolean discardTap;
    private TextView docInfoTextView;
    private TextView docNameTextView;
    private boolean doneButtonPressed;
    private boolean dontAutoPlay;
    private boolean dontChangeCaptionPosition;
    private boolean dontResetZoomOnFirstLayout;
    private boolean doubleTap;
    private boolean doubleTapEnabled;
    private float dragY;
    private boolean draggingDown;
    private PickerBottomLayoutViewer editorDoneLayout;
    private long endTime;
    private long estimatedDuration;
    private long estimatedSize;
    private ImageView exitFullscreenButton;
    private boolean firstAnimationDelay;
    private FirstFrameView firstFrameView;
    private AnimatorSet flashAnimator;
    private View flashView;
    boolean fromCamera;
    private int fullscreenedByButton;
    private GestureDetector2 gestureDetector;
    private GroupedPhotosListView groupedPhotosListView;
    public boolean hasCaptionForAllMedia;
    private PlaceProviderObject hideAfterAnimation;
    private UndoView hintView;
    private boolean ignoreDidSetImage;
    private AnimatorSet imageMoveAnimation;
    private boolean inBubbleMode;
    private boolean inPreview;
    private VideoPlayer injectingVideoPlayer;
    private SurfaceTexture injectingVideoPlayerSurface;
    private float inlineOutAnimationProgress;
    private boolean invalidCoords;
    private boolean isCurrentVideo;
    private boolean isDocumentsPicker;
    private boolean isEmbedVideo;
    private boolean isEvent;
    private boolean isFirstLoading;
    private boolean isInline;
    private boolean isPhotosListViewVisible;
    private boolean isPlaying;
    private boolean isStreaming;
    private boolean isVisible;
    private LinearLayout itemsLayout;
    private boolean keepScreenOnFlagSet;
    boolean keyboardAnimationEnabled;
    private int keyboardSize;
    private long lastBufferedPositionCheck;
    private String lastControlFrameDuration;
    private Object lastInsets;
    private long lastPhotoSetTime;
    private long lastSaveTime;
    private String lastTitle;
    private MediaController.CropState leftCropState;
    private boolean leftImageIsVideo;
    private PaintingOverlay leftPaintingOverlay;
    private boolean loadInitialVideo;
    private boolean loadingMoreImages;
    float longPressX;
    private boolean manuallyPaused;
    private StickersAlert masksAlert;
    private ActionBarMenuItem masksItem;
    private float maxX;
    private float maxY;
    private LinearLayoutManager mentionLayoutManager;
    private SpringAnimation mentionListAnimation;
    private RecyclerListView mentionListView;
    private boolean mentionListViewVisible;
    private MentionsAdapter mentionsAdapter;
    private ActionBarMenuItem menuItem;
    private long mergeDialogId;
    private float minX;
    private float minY;
    private AnimatorSet miniProgressAnimator;
    private RadialProgressView miniProgressView;
    private ImageView mirrorItem;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
    private ImageView muteItem;
    private boolean muteVideo;
    private String nameOverride;
    private FadingTextViewLayout nameTextView;
    private ValueAnimator navBarAnimator;
    private View navigationBar;
    private int navigationBarHeight;
    private boolean needCaptionLayout;
    private boolean needSearchImageInArr;
    private boolean needShowOnReady;
    private boolean openedFullScreenVideo;
    private boolean opennedFromMedia;
    private OrientationEventListener orientationEventListener;
    private int originalBitrate;
    private int originalHeight;
    private long originalSize;
    private int originalWidth;
    private boolean padImageForHorizontalInsets;
    private PageBlocksAdapter pageBlocksAdapter;
    private ImageView paintButton;
    private ImageView paintItem;
    private int paintViewTouched;
    private PaintingOverlay paintingOverlay;
    private Activity parentActivity;
    private ChatAttachAlert parentAlert;
    private ChatActivity parentChatActivity;
    private BaseFragment parentFragment;
    private PhotoCropView photoCropView;
    private PhotoFilterView photoFilterView;
    private PhotoPaintView photoPaintView;
    private PhotoViewerWebView photoViewerWebView;
    private CounterView photosCounterView;
    private FrameLayout pickerView;
    private ImageView pickerViewSendButton;
    private Drawable pickerViewSendDrawable;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartX;
    private float pinchStartY;
    private boolean pipAnimationInProgress;
    private boolean pipAvailable;
    private ActionBarMenuItem pipItem;
    private PhotoViewerProvider placeProvider;
    private View playButtonAccessibilityOverlay;
    private boolean playerAutoStarted;
    private boolean playerInjected;
    private boolean playerLooping;
    private boolean playerWasPlaying;
    private boolean playerWasReady;
    private int previousCompression;
    private boolean previousCropMirrored;
    private int previousCropOrientation;
    private float previousCropPh;
    private float previousCropPw;
    private float previousCropPx;
    private float previousCropPy;
    private float previousCropRotation;
    private float previousCropScale;
    private boolean previousHasTransform;
    private RadialProgressView progressView;
    private QualityChooseView qualityChooseView;
    private AnimatorSet qualityChooseViewAnimation;
    private PickerBottomLayoutViewer qualityPicker;
    private boolean requestingPreview;
    private TextView resetButton;
    private Theme.ResourcesProvider resourcesProvider;
    private int resultHeight;
    private int resultWidth;
    private MediaController.CropState rightCropState;
    private boolean rightImageIsVideo;
    private PaintingOverlay rightPaintingOverlay;
    private ImageView rotateItem;
    private int rotationValue;
    private Scroller scroller;
    private float seekToProgressPending;
    private float seekToProgressPending2;
    private int selectedCompression;
    private ListAdapter selectedPhotosAdapter;
    private SelectedPhotosListView selectedPhotosListView;
    private ActionBarMenuItem sendItem;
    private int sendPhotoType;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private ImageView shareButton;
    private ActionBarMenuItem shareItem;
    private int sharedMediaType;
    private String shouldSavePositionForCurrentVideo;
    private String shouldSavePositionForCurrentVideoShortTerm;
    private PlaceProviderObject showAfterAnimation;
    private ImageReceiver sideImage;
    private boolean skipFirstBufferingProgress;
    private int slideshowMessageId;
    private ActionBarPopupWindow.GapView speedGap;
    private ActionBarMenuSubItem speedItem;
    private int startOffset;
    private long startTime;
    private long startedPlayTime;
    private boolean streamingAlertShown;
    private int switchImageAfterAnimation;
    private boolean switchingInlineMode;
    private int switchingToIndex;
    private ImageView textureImageView;
    private boolean textureUploaded;
    private ImageView timeItem;
    private Tooltip tooltip;
    private int topicId;
    private int totalImagesCount;
    private int totalImagesCountMerge;
    private int touchSlop;
    private long transitionAnimationStartTime;
    private int transitionIndex;
    private float translationX;
    private float translationY;
    private boolean tryStartRequestPreviewOnFinish;
    private ImageView tuneItem;
    private boolean useSmoothKeyboard;
    private VelocityTracker velocityTracker;
    private TextView videoAvatarTooltip;
    private boolean videoConvertSupported;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    private float videoCutEnd;
    private float videoCutStart;
    private float videoDuration;
    private VideoForwardDrawable videoForwardDrawable;
    private int videoFramerate;
    private long videoFramesSize;
    private int videoHeight;
    private Runnable videoPlayRunnable;
    private VideoPlayer videoPlayer;
    private Animator videoPlayerControlAnimator;
    private VideoPlayerControlFrameLayout videoPlayerControlFrameLayout;
    private VideoPlayerSeekBar videoPlayerSeekbar;
    private View videoPlayerSeekbarView;
    private SimpleTextView videoPlayerTime;
    private VideoSeekPreviewImage videoPreviewFrame;
    private AnimatorSet videoPreviewFrameAnimation;
    private MessageObject videoPreviewMessageObject;
    private boolean videoSizeSet;
    private TextureView videoTextureView;
    private ObjectAnimator videoTimelineAnimator;
    private VideoTimelinePlayView videoTimelineView;
    private int videoWidth;
    private AlertDialog visibleDialog;
    private int waitingForDraw;
    private int waitingForFirstTextureUpload;
    private boolean wasLayout;
    private boolean wasRotated;
    private WindowManager.LayoutParams windowLayoutParams;
    private FrameLayout windowView;
    private boolean zoomAnimation;
    private boolean zooming;
    private int maxSelectedPhotos = -1;
    private boolean allowOrder = true;
    private Runnable miniProgressShowRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda55
        @Override // java.lang.Runnable
        public final void run() {
            PhotoViewer.this.lambda$new$0();
        }
    };
    private boolean isActionBarVisible = true;
    private Map<View, Boolean> actionBarItemsVisibility = new HashMap(3);
    private BackgroundDrawable backgroundDrawable = new BackgroundDrawable(-16777216);
    private Paint blackPaint = new Paint();
    private PhotoProgressView[] photoProgressViews = new PhotoProgressView[3];
    private Runnable onUserLeaveHintListener = new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda56
        @Override // java.lang.Runnable
        public final void run() {
            PhotoViewer.this.onUserLeaveHint();
        }
    };
    private GradientDrawable[] pressedDrawable = new GradientDrawable[2];
    private boolean[] drawPressedDrawable = new boolean[2];
    private float[] pressedDrawableAlpha = new float[2];
    private CropTransform cropTransform = new CropTransform();
    private CropTransform leftCropTransform = new CropTransform();
    private CropTransform rightCropTransform = new CropTransform();
    private Paint bitmapPaint = new Paint(2);
    private Runnable setLoadingRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer.1
        @Override // java.lang.Runnable
        public void run() {
            if (PhotoViewer.this.currentMessageObject == null) {
                return;
            }
            FileLoader.getInstance(PhotoViewer.this.currentMessageObject.currentAccount).setLoadingVideo(PhotoViewer.this.currentMessageObject.getDocument(), true, false);
        }
    };
    private Runnable hideActionBarRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer.2
        @Override // java.lang.Runnable
        public void run() {
            if (!PhotoViewer.this.videoPlayerControlVisible || !PhotoViewer.this.isPlaying || ApplicationLoader.mainInterfacePaused) {
                return;
            }
            if (PhotoViewer.this.menuItem != null && PhotoViewer.this.menuItem.isSubMenuShowing()) {
                return;
            }
            if (PhotoViewer.this.captionScrollView != null && PhotoViewer.this.captionScrollView.getScrollY() != 0) {
                return;
            }
            if (PhotoViewer.this.miniProgressView != null && PhotoViewer.this.miniProgressView.getVisibility() == 0) {
                return;
            }
            PhotoViewer photoViewer = PhotoViewer.PipInstance;
            PhotoViewer photoViewer2 = PhotoViewer.this;
            if (photoViewer == photoViewer2) {
                return;
            }
            photoViewer2.toggleActionBar(false, true);
        }
    };
    private ArrayMap<String, SavedVideoPosition> savedVideoPositions = new ArrayMap<>();
    private boolean videoPlayerControlVisible = true;
    private int[] videoPlayerCurrentTime = new int[2];
    private int[] videoPlayerTotalTime = new int[2];
    private ImageView[] fullscreenButton = new ImageView[3];
    private int[] pipPosition = new int[2];
    private boolean pipVideoOverlayAnimateFlag = true;
    private int lastImageId = -1;
    private int prevOrientation = -10;
    VideoPlayerRewinder videoPlayerRewinder = new VideoPlayerRewinder() { // from class: org.telegram.ui.PhotoViewer.3
        @Override // org.telegram.messenger.video.VideoPlayerRewinder
        protected void onRewindCanceled() {
            PhotoViewer.this.onTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0));
            PhotoViewer.this.videoForwardDrawable.setShowing(false);
            PipVideoOverlay.onRewindCanceled();
        }

        @Override // org.telegram.messenger.video.VideoPlayerRewinder
        protected void updateRewindProgressUi(long j, float f, boolean z) {
            PhotoViewer.this.videoForwardDrawable.setTime(Math.abs(j));
            if (z) {
                PhotoViewer.this.videoPlayerSeekbar.setProgress(f);
                PhotoViewer.this.videoPlayerSeekbarView.invalidate();
            }
            PipVideoOverlay.onUpdateRewindProgressUi(j, f, z);
        }

        @Override // org.telegram.messenger.video.VideoPlayerRewinder
        protected void onRewindStart(boolean z) {
            PhotoViewer.this.videoForwardDrawable.setOneShootAnimation(false);
            PhotoViewer.this.videoForwardDrawable.setLeftSide(!z);
            PhotoViewer.this.videoForwardDrawable.setShowing(true);
            PhotoViewer.this.containerView.invalidate();
            PipVideoOverlay.onRewindStart(z);
        }
    };
    public final Property<View, Float> FLASH_VIEW_VALUE = new AnimationProperties.FloatProperty<View>("flashViewAlpha") { // from class: org.telegram.ui.PhotoViewer.4
        @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
        public void setValue(View view, float f) {
            view.setAlpha(f);
            if (PhotoViewer.this.photoCropView != null) {
                PhotoViewer.this.photoCropView.setVideoThumbFlashAlpha(f);
            }
        }

        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getAlpha());
        }
    };
    private Runnable updateProgressRunnable = new AnonymousClass5();
    private Runnable switchToInlineRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer.6
        @Override // java.lang.Runnable
        public void run() {
            if (!PipVideoOverlay.isVisible()) {
                PhotoViewer.this.switchingInlineMode = false;
                if (PhotoViewer.this.currentBitmap != null) {
                    PhotoViewer.this.currentBitmap.recycle();
                    PhotoViewer.this.currentBitmap = null;
                }
                PhotoViewer.this.changingTextureView = true;
                if (PhotoViewer.this.textureImageView != null) {
                    try {
                        PhotoViewer photoViewer = PhotoViewer.this;
                        photoViewer.currentBitmap = Bitmaps.createBitmap(photoViewer.videoTextureView.getWidth(), PhotoViewer.this.videoTextureView.getHeight(), Bitmap.Config.ARGB_8888);
                        PhotoViewer.this.videoTextureView.getBitmap(PhotoViewer.this.currentBitmap);
                    } catch (Throwable th) {
                        if (PhotoViewer.this.currentBitmap != null) {
                            PhotoViewer.this.currentBitmap.recycle();
                            PhotoViewer.this.currentBitmap = null;
                        }
                        FileLog.e(th);
                    }
                    if (PhotoViewer.this.currentBitmap != null) {
                        PhotoViewer.this.textureImageView.setVisibility(0);
                        PhotoViewer.this.textureImageView.setImageBitmap(PhotoViewer.this.currentBitmap);
                    } else {
                        PhotoViewer.this.textureImageView.setImageDrawable(null);
                    }
                }
                PhotoViewer.this.isInline = true;
                PhotoViewer.this.changedTextureView = new TextureView(PhotoViewer.this.parentActivity);
                if (PipVideoOverlay.show(false, PhotoViewer.this.parentActivity, PhotoViewer.this.changedTextureView, PhotoViewer.this.videoWidth, PhotoViewer.this.videoHeight, PhotoViewer.this.pipVideoOverlayAnimateFlag)) {
                    PipVideoOverlay.setPhotoViewer(PhotoViewer.this);
                }
                PhotoViewer.this.pipVideoOverlayAnimateFlag = true;
                PhotoViewer.this.changedTextureView.setVisibility(4);
                PhotoViewer.this.aspectRatioFrameLayout.removeView(PhotoViewer.this.videoTextureView);
                return;
            }
            PipVideoOverlay.dismiss();
            AndroidUtilities.runOnUIThread(this, 250L);
        }
    };
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() { // from class: org.telegram.ui.PhotoViewer.7
        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (PhotoViewer.this.videoTextureView != null && PhotoViewer.this.changingTextureView) {
                if (PhotoViewer.this.switchingInlineMode) {
                    PhotoViewer.this.waitingForFirstTextureUpload = 2;
                }
                PhotoViewer.this.videoTextureView.setSurfaceTexture(surfaceTexture);
                PhotoViewer.this.videoTextureView.setVisibility(0);
                PhotoViewer.this.changingTextureView = false;
                PhotoViewer.this.containerView.invalidate();
                return false;
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.PhotoViewer$7$1  reason: invalid class name */
        /* loaded from: classes3.dex */
        public class AnonymousClass1 implements ViewTreeObserver.OnPreDrawListener {
            AnonymousClass1() {
            }

            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                PhotoViewer.this.changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (PhotoViewer.this.textureImageView != null) {
                    if (!PhotoViewer.this.isInline) {
                        PhotoViewer.this.textureImageView.setVisibility(4);
                        PhotoViewer.this.textureImageView.setImageDrawable(null);
                        if (PhotoViewer.this.currentBitmap != null) {
                            PhotoViewer.this.currentBitmap.recycle();
                            PhotoViewer.this.currentBitmap = null;
                        }
                    } else {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$7$1$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                PhotoViewer.AnonymousClass7.AnonymousClass1.this.lambda$onPreDraw$0();
                            }
                        }, 300L);
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$7$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass7.AnonymousClass1.this.lambda$onPreDraw$1();
                    }
                });
                PhotoViewer.this.waitingForFirstTextureUpload = 0;
                return true;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onPreDraw$0() {
                PhotoViewer.this.textureImageView.setVisibility(4);
                PhotoViewer.this.textureImageView.setImageDrawable(null);
                if (PhotoViewer.this.currentBitmap != null) {
                    PhotoViewer.this.currentBitmap.recycle();
                    PhotoViewer.this.currentBitmap = null;
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onPreDraw$1() {
                if (PhotoViewer.this.isInline) {
                    PhotoViewer.this.dismissInternal();
                }
            }
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            if (PhotoViewer.this.waitingForFirstTextureUpload == 1) {
                PhotoViewer.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new AnonymousClass1());
                PhotoViewer.this.changedTextureView.invalidate();
            }
        }
    };
    private float[][] animationValues = (float[][]) Array.newInstance(float.class, 2, 13);
    private final Runnable updateContainerFlagsRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda54
        @Override // java.lang.Runnable
        public final void run() {
            PhotoViewer.this.lambda$new$3();
        }
    };
    private ImageReceiver leftImage = new ImageReceiver();
    private ImageReceiver centerImage = new ImageReceiver();
    private ImageReceiver rightImage = new ImageReceiver();
    private Paint videoFrameBitmapPaint = new Paint();
    private Bitmap videoFrameBitmap = null;
    private EditState editState = new EditState();
    private String[] currentFileNames = new String[3];
    private boolean[] endReached = {false, true};
    private float scale = 1.0f;
    private float rotate = 0.0f;
    private float mirror = 0.0f;
    private int switchingToMode = -1;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private float pinchStartScale = 1.0f;
    private boolean canZoom = true;
    private boolean canDragDown = true;
    private boolean shownControlsByEnd = false;
    private boolean actionBarWasShownBeforeByEnd = false;
    private boolean bottomTouchEnabled = true;
    private ArrayList<MessageObject> imagesArrTemp = new ArrayList<>();
    private SparseArray<MessageObject>[] imagesByIdsTemp = {new SparseArray<>(), new SparseArray<>()};
    private ArrayList<MessageObject> imagesArr = new ArrayList<>();
    private SparseArray<MessageObject>[] imagesByIds = {new SparseArray<>(), new SparseArray<>()};
    private ArrayList<ImageLocation> imagesArrLocations = new ArrayList<>();
    private ArrayList<ImageLocation> imagesArrLocationsVideo = new ArrayList<>();
    private ArrayList<Long> imagesArrLocationsSizes = new ArrayList<>();
    private ArrayList<TLRPC$Message> imagesArrMessages = new ArrayList<>();
    private ArrayList<SecureDocument> secureDocuments = new ArrayList<>();
    private ArrayList<TLRPC$Photo> avatarsArr = new ArrayList<>();
    private ArrayList<Object> imagesArrLocals = new ArrayList<>();
    private ImageLocation currentAvatarLocation = null;
    private SavedState savedState = null;
    private Rect hitRect = new Rect();
    Runnable longPressRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda51
        @Override // java.lang.Runnable
        public final void run() {
            PhotoViewer.this.onLongPress();
        }
    };
    private int[] tempInt = new int[2];
    private long captureFrameAtTime = -1;
    private long captureFrameReadyAtTime = -1;
    private long needCaptureFrameReadyAtTime = -1;
    private int compressionsCount = -1;

    /* loaded from: classes3.dex */
    public static class EmptyPhotoViewerProvider implements PhotoViewerProvider {
        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean allowCaption() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean allowSendingSubmenu() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canCaptureMorePhotos() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canReplace(int i) {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canScrollAway() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean cancelButtonPressed() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean closeKeyboard() {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void deleteImageAtIndex(int i) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public String getDeleteMessageString() {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public MessageObject getEditingMessageObject() {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getPhotoIndex(int i) {
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getSelectedCount() {
            return 0;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public HashMap<Object, Object> getSelectedPhotos() {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public ArrayList<Object> getSelectedPhotosOrder() {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public CharSequence getSubtitleFor(int i) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public CharSequence getTitleFor(int i) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getTotalImageCount() {
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean isPhotoChecked(int i) {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean loadMore() {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void needAddMorePhotos() {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onApplyCaption(CharSequence charSequence) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onCaptionChanged(CharSequence charSequence) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onClose() {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onOpen() {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void openPhotoForEdit(String str, String str2, boolean z) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void replaceButtonPressed(int i, VideoEditedInfo videoEditedInfo) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean scaleToFill() {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo) {
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int setPhotoUnchecked(Object obj) {
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void updatePhotoAtIndex(int i) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void willHidePhotoViewer() {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i) {
        }
    }

    /* loaded from: classes3.dex */
    public interface PageBlocksAdapter {
        TLRPC$PageBlock get(int i);

        List<TLRPC$PageBlock> getAll();

        CharSequence getCaption(int i);

        File getFile(int i);

        TLRPC$PhotoSize getFileLocation(TLObject tLObject, int[] iArr);

        String getFileName(int i);

        int getItemsCount();

        TLObject getMedia(int i);

        Object getParentObject();

        boolean isVideo(int i);

        void updateSlideshowCell(TLRPC$PageBlock tLRPC$PageBlock);
    }

    /* loaded from: classes3.dex */
    public interface PhotoViewerProvider {
        boolean allowCaption();

        boolean allowSendingSubmenu();

        boolean canCaptureMorePhotos();

        boolean canReplace(int i);

        boolean canScrollAway();

        boolean cancelButtonPressed();

        boolean closeKeyboard();

        void deleteImageAtIndex(int i);

        String getDeleteMessageString();

        MessageObject getEditingMessageObject();

        int getPhotoIndex(int i);

        PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z);

        int getSelectedCount();

        HashMap<Object, Object> getSelectedPhotos();

        ArrayList<Object> getSelectedPhotosOrder();

        CharSequence getSubtitleFor(int i);

        ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i);

        CharSequence getTitleFor(int i);

        int getTotalImageCount();

        boolean isPhotoChecked(int i);

        boolean loadMore();

        void needAddMorePhotos();

        void onApplyCaption(CharSequence charSequence);

        void onCaptionChanged(CharSequence charSequence);

        void onClose();

        void onOpen();

        void openPhotoForEdit(String str, String str2, boolean z);

        void replaceButtonPressed(int i, VideoEditedInfo videoEditedInfo);

        boolean scaleToFill();

        void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2);

        int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo);

        int setPhotoUnchecked(Object obj);

        void updatePhotoAtIndex(int i);

        void willHidePhotoViewer();

        void willSwitchFromPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i);
    }

    /* loaded from: classes3.dex */
    public static class PlaceProviderObject {
        public ClippingImageView animatingImageView;
        public int animatingImageViewYOffset;
        public boolean canEdit;
        public int clipBottomAddition;
        public int clipTopAddition;
        public long dialogId;
        public ImageReceiver imageReceiver;
        public boolean isEvent;
        public View parentView;
        public int[] radius;
        public long size;
        public int starOffset;
        public ImageReceiver.BitmapHolder thumb;
        public int viewX;
        public int viewY;
        public float scale = 1.0f;
        public boolean allowTakeAnimation = true;
    }

    private boolean enableSwipeToPiP() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$setParentActivity$24(View view, MotionEvent motionEvent) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$setParentActivity$25(View view, MotionEvent motionEvent) {
        return true;
    }

    public void onConfigurationChanged(Configuration configuration) {
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnDoubleTapListener
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public void onShowPress(MotionEvent motionEvent) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        toggleMiniProgressInternal(true);
    }

    public void addPhoto(MessageObject messageObject, int i) {
        if (i != this.classGuid) {
            return;
        }
        if (this.imagesByIds[0].indexOfKey(messageObject.getId()) < 0) {
            if (this.opennedFromMedia) {
                this.imagesArr.add(messageObject);
            } else {
                this.imagesArr.add(0, messageObject);
            }
            this.imagesByIds[0].put(messageObject.getId(), messageObject);
        }
        this.endReached[0] = this.imagesArr.size() == this.totalImagesCount;
        setImages();
    }

    public int getClassGuid() {
        return this.classGuid;
    }

    public void setCaption(CharSequence charSequence) {
        this.hasCaptionForAllMedia = true;
        this.captionForAllMedia = charSequence;
        setCurrentCaption(null, charSequence, false);
        updateCaptionTextForCurrentPhoto(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class SavedVideoPosition {
        public final float position;
        public final long timestamp;

        public SavedVideoPosition(float f, long j) {
            this.position = f;
            this.timestamp = j;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLinkClick(ClickableSpan clickableSpan, TextView textView) {
        if (textView != null && (clickableSpan instanceof URLSpan)) {
            String url = ((URLSpan) clickableSpan).getURL();
            if (url.startsWith("video")) {
                if (this.videoPlayer == null || this.currentMessageObject == null) {
                    return;
                }
                int intValue = Utilities.parseInt((CharSequence) url).intValue();
                if (this.videoPlayer.getDuration() == -9223372036854775807L) {
                    this.seekToProgressPending = intValue / this.currentMessageObject.getDuration();
                    return;
                }
                long j = intValue * 1000;
                this.videoPlayer.seekTo(j);
                this.videoPlayerSeekbar.setProgress(((float) j) / ((float) this.videoPlayer.getDuration()), true);
                this.videoPlayerSeekbarView.invalidate();
                return;
            } else if (url.startsWith("#")) {
                if (!(this.parentActivity instanceof LaunchActivity)) {
                    return;
                }
                DialogsActivity dialogsActivity = new DialogsActivity(null);
                dialogsActivity.setSearchString(url);
                ((LaunchActivity) this.parentActivity).presentFragment(dialogsActivity, false, true);
                closePhoto(false, false);
                return;
            } else if (this.parentChatActivity != null && ((clickableSpan instanceof URLSpanReplacement) || AndroidUtilities.shouldShowUrlInAlert(url))) {
                AlertsCreator.showOpenUrlAlert(this.parentChatActivity, url, true, true);
                return;
            } else {
                clickableSpan.onClick(textView);
                return;
            }
        }
        clickableSpan.onClick(textView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't wrap try/catch for region: R(9:1|(8:15|16|(1:5)(1:14)|6|7|8|9|10)|3|(0)(0)|6|7|8|9|10) */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0036  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x003e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onLinkLongPress(final android.text.style.URLSpan r11, final android.widget.TextView r12, final java.lang.Runnable r13) {
        /*
            r10 = this;
            org.telegram.ui.ActionBar.BottomSheet$Builder r0 = new org.telegram.ui.ActionBar.BottomSheet$Builder
            android.app.Activity r1 = r10.parentActivity
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r10.resourcesProvider
            r3 = 0
            r4 = -14933463(0xffffffffff1CLASSNAME, float:-2.0753694E38)
            r0.<init>(r1, r3, r2, r4)
            java.lang.String r1 = r11.getURL()
            java.lang.String r2 = "video?"
            boolean r1 = r1.startsWith(r2)
            r2 = -1
            r5 = 1
            if (r1 == 0) goto L33
            java.lang.String r1 = r11.getURL()     // Catch: java.lang.Throwable -> L33
            java.lang.String r6 = r11.getURL()     // Catch: java.lang.Throwable -> L33
            r7 = 63
            int r6 = r6.indexOf(r7)     // Catch: java.lang.Throwable -> L33
            int r6 = r6 + r5
            java.lang.String r1 = r1.substring(r6)     // Catch: java.lang.Throwable -> L33
            int r1 = java.lang.Integer.parseInt(r1)     // Catch: java.lang.Throwable -> L33
            goto L34
        L33:
            r1 = -1
        L34:
            if (r1 < 0) goto L3e
            java.lang.String r6 = org.telegram.messenger.AndroidUtilities.formatDuration(r1, r3)
            r0.setTitle(r6)
            goto L45
        L3e:
            java.lang.String r6 = r11.getURL()
            r0.setTitle(r6)
        L45:
            r6 = 2
            java.lang.CharSequence[] r7 = new java.lang.CharSequence[r6]
            int r8 = org.telegram.messenger.R.string.Open
            java.lang.String r9 = "Open"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r7[r3] = r8
            int r8 = org.telegram.messenger.R.string.Copy
            java.lang.String r9 = "Copy"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r7[r5] = r8
            org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda13 r8 = new org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda13
            r8.<init>()
            r0.setItems(r7, r8)
            org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda14 r11 = new org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda14
            r11.<init>()
            r0.setOnPreDismissListener(r11)
            org.telegram.ui.ActionBar.BottomSheet r11 = r0.create()
            r11.scrollNavBar = r5
            r11.show()
            org.telegram.ui.PhotoViewer$FrameLayoutDrawer r12 = r10.containerView     // Catch: java.lang.Exception -> L7a
            r12.performHapticFeedback(r3, r6)     // Catch: java.lang.Exception -> L7a
        L7a:
            r11.setItemColor(r3, r2, r2)
            r11.setItemColor(r5, r2, r2)
            r11.setBackgroundColor(r4)
            r12 = -7697782(0xffffffffff8a8a8a, float:NaN)
            r11.setTitleColor(r12)
            r11.setCalcMandatoryInsets(r5)
            android.view.Window r12 = r11.getWindow()
            org.telegram.messenger.AndroidUtilities.setNavigationBarColor(r12, r4, r3)
            android.view.Window r12 = r11.getWindow()
            org.telegram.messenger.AndroidUtilities.setLightNavigationBar(r12, r3)
            r11.scrollNavBar = r5
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onLinkLongPress(android.text.style.URLSpan, android.widget.TextView, java.lang.Runnable):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0102  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x010b  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x013b  */
    /* JADX WARN: Removed duplicated region for block: B:65:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$onLinkLongPress$1(android.text.style.URLSpan r6, android.widget.TextView r7, int r8, android.content.DialogInterface r9, int r10) {
        /*
            Method dump skipped, instructions count: 333
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.lambda$onLinkLongPress$1(android.text.style.URLSpan, android.widget.TextView, int, android.content.DialogInterface, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelFlashAnimations() {
        View view = this.flashView;
        if (view != null) {
            view.animate().setListener(null).cancel();
            this.flashView.setAlpha(0.0f);
        }
        AnimatorSet animatorSet = this.flashAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.flashAnimator = null;
        }
        PhotoCropView photoCropView = this.photoCropView;
        if (photoCropView != null) {
            photoCropView.cancelThumbAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelVideoPlayRunnable() {
        Runnable runnable = this.videoPlayRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.videoPlayRunnable = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getCurrentVideoPosition() {
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
            return this.photoViewerWebView.getCurrentPosition();
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            return videoPlayer.getCurrentPosition();
        }
        return 0L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getVideoDuration() {
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
            return this.photoViewerWebView.getVideoDuration();
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            return videoPlayer.getDuration();
        }
        return 0L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void seekVideoOrWebTo(long j) {
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
            this.photoViewerWebView.seekTo(j);
        } else {
            VideoPlayer videoPlayer = this.videoPlayer;
            if (videoPlayer != null) {
                videoPlayer.seekTo(j);
            }
        }
        updateVideoPlayerTime();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isVideoPlaying() {
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
            return this.photoViewerWebView.isPlaying();
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        return videoPlayer != null && videoPlayer.isPlaying();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$5  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass5 implements Runnable {
        AnonymousClass5() {
        }

        @Override // java.lang.Runnable
        public void run() {
            float f;
            if (PhotoViewer.this.videoPlayer != null || (PhotoViewer.this.photoViewerWebView != null && PhotoViewer.this.photoViewerWebView.isControllable())) {
                float f2 = 0.0f;
                if (PhotoViewer.this.isCurrentVideo) {
                    if (!PhotoViewer.this.videoTimelineView.isDragging()) {
                        float currentVideoPosition = ((float) PhotoViewer.this.getCurrentVideoPosition()) / ((float) PhotoViewer.this.getVideoDuration());
                        if (!PhotoViewer.this.shownControlsByEnd || PhotoViewer.this.actionBarWasShownBeforeByEnd) {
                            f2 = currentVideoPosition;
                        }
                        if (PhotoViewer.this.inPreview || (PhotoViewer.this.currentEditMode == 0 && PhotoViewer.this.videoTimelineView.getVisibility() != 0)) {
                            if (PhotoViewer.this.sendPhotoType != 1) {
                                PhotoViewer.this.videoTimelineView.setProgress(f2);
                            }
                        } else if (f2 >= PhotoViewer.this.videoTimelineView.getRightProgress()) {
                            PhotoViewer.this.videoTimelineView.setProgress(PhotoViewer.this.videoTimelineView.getLeftProgress());
                            PhotoViewer.this.videoPlayer.seekTo((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.getVideoDuration())));
                            PhotoViewer.this.manuallyPaused = false;
                            PhotoViewer.this.cancelVideoPlayRunnable();
                            if (PhotoViewer.this.muteVideo || PhotoViewer.this.sendPhotoType == 1 || PhotoViewer.this.currentEditMode != 0 || PhotoViewer.this.switchingToMode > 0) {
                                PhotoViewer.this.playVideoOrWeb();
                            } else {
                                PhotoViewer.this.pauseVideoOrWeb();
                            }
                            PhotoViewer.this.containerView.invalidate();
                        } else {
                            PhotoViewer.this.videoTimelineView.setProgress(f2);
                        }
                        PhotoViewer.this.updateVideoPlayerTime();
                    }
                } else {
                    final float currentVideoPosition2 = ((float) PhotoViewer.this.getCurrentVideoPosition()) / ((float) PhotoViewer.this.getVideoDuration());
                    if (PhotoViewer.this.shownControlsByEnd && !PhotoViewer.this.actionBarWasShownBeforeByEnd) {
                        currentVideoPosition2 = 0.0f;
                    }
                    if (PhotoViewer.this.currentVideoFinishedLoading) {
                        f = 1.0f;
                    } else {
                        long elapsedRealtime = SystemClock.elapsedRealtime();
                        if (Math.abs(elapsedRealtime - PhotoViewer.this.lastBufferedPositionCheck) >= 500) {
                            if (PhotoViewer.this.photoViewerWebView == null || !PhotoViewer.this.photoViewerWebView.isControllable()) {
                                if (PhotoViewer.this.isStreaming) {
                                    f = FileLoader.getInstance(PhotoViewer.this.currentAccount).getBufferedProgressFromPosition(PhotoViewer.this.seekToProgressPending != 0.0f ? PhotoViewer.this.seekToProgressPending : currentVideoPosition2, PhotoViewer.this.currentFileNames[0]);
                                } else {
                                    f = 1.0f;
                                }
                            } else {
                                f = PhotoViewer.this.photoViewerWebView.getBufferedPosition();
                            }
                            PhotoViewer.this.lastBufferedPositionCheck = elapsedRealtime;
                        } else {
                            f = -1.0f;
                        }
                    }
                    if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineView.getVisibility() != 0) {
                        if (PhotoViewer.this.seekToProgressPending == 0.0f) {
                            PhotoViewer photoViewer = PhotoViewer.this;
                            VideoPlayerRewinder videoPlayerRewinder = photoViewer.videoPlayerRewinder;
                            if (videoPlayerRewinder.rewindCount == 0 || !videoPlayerRewinder.rewindByBackSeek) {
                                photoViewer.videoPlayerSeekbar.setProgress(currentVideoPosition2, false);
                            }
                        }
                        if (f != -1.0f) {
                            PhotoViewer.this.videoPlayerSeekbar.setBufferedProgress(f);
                            PipVideoOverlay.setBufferedProgress(f);
                        }
                    } else if (currentVideoPosition2 >= PhotoViewer.this.videoTimelineView.getRightProgress()) {
                        PhotoViewer.this.manuallyPaused = false;
                        PhotoViewer.this.pauseVideoOrWeb();
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                        PhotoViewer photoViewer2 = PhotoViewer.this;
                        photoViewer2.seekVideoOrWebTo((int) (photoViewer2.videoTimelineView.getLeftProgress() * ((float) PhotoViewer.this.getVideoDuration())));
                        PhotoViewer.this.containerView.invalidate();
                    } else {
                        float leftProgress = currentVideoPosition2 - PhotoViewer.this.videoTimelineView.getLeftProgress();
                        if (leftProgress < 0.0f) {
                            leftProgress = 0.0f;
                        }
                        currentVideoPosition2 = leftProgress / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
                        if (currentVideoPosition2 > 1.0f) {
                            currentVideoPosition2 = 1.0f;
                        }
                        PhotoViewer.this.videoPlayerSeekbar.setProgress(currentVideoPosition2);
                    }
                    PhotoViewer.this.videoPlayerSeekbarView.invalidate();
                    if (PhotoViewer.this.shouldSavePositionForCurrentVideo != null && currentVideoPosition2 >= 0.0f && SystemClock.elapsedRealtime() - PhotoViewer.this.lastSaveTime >= 1000) {
                        String unused = PhotoViewer.this.shouldSavePositionForCurrentVideo;
                        PhotoViewer.this.lastSaveTime = SystemClock.elapsedRealtime();
                        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PhotoViewer$5$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                PhotoViewer.AnonymousClass5.this.lambda$run$0(currentVideoPosition2);
                            }
                        });
                    }
                    PhotoViewer.this.updateVideoPlayerTime();
                }
            }
            if (PhotoViewer.this.firstFrameView != null) {
                PhotoViewer.this.firstFrameView.updateAlpha();
            }
            if (PhotoViewer.this.isPlaying) {
                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable, 17L);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0(float f) {
            ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).edit().putFloat(PhotoViewer.this.shouldSavePositionForCurrentVideo, f).commit();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3() {
        if (!this.isVisible || this.animationInProgress != 0) {
            return;
        }
        updateContainerFlags(this.isActionBarVisible);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class EditState {
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
            this.averageDuration = 0L;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class SavedState {
        private int index;
        private ArrayList<MessageObject> messages;
        private PhotoViewerProvider provider;

        public SavedState(int i, ArrayList<MessageObject> arrayList, PhotoViewerProvider photoViewerProvider) {
            this.messages = arrayList;
            this.index = i;
            this.provider = photoViewerProvider;
        }

        public void restore() {
            PhotoViewer.this.placeProvider = this.provider;
            if (Build.VERSION.SDK_INT >= 21) {
                PhotoViewer.this.windowLayoutParams.flags = -NUM;
            } else {
                PhotoViewer.this.windowLayoutParams.flags = 131072;
            }
            PhotoViewer.this.windowLayoutParams.softInputMode = (PhotoViewer.this.useSmoothKeyboard ? 32 : 16) | 256;
            PhotoViewer.this.windowView.setFocusable(false);
            PhotoViewer.this.containerView.setFocusable(false);
            PhotoViewer.this.backgroundDrawable.setAlpha(255);
            PhotoViewer.this.containerView.setAlpha(1.0f);
            PhotoViewer photoViewer = PhotoViewer.this;
            ArrayList<MessageObject> arrayList = this.messages;
            int i = this.index;
            photoViewer.onPhotoShow(null, null, null, null, arrayList, null, null, i, this.provider.getPlaceForPhoto(arrayList.get(i), null, this.index, true));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class BackgroundDrawable extends ColorDrawable {
        private boolean allowDrawContent;
        private Runnable drawRunnable;
        private final Paint paint;
        private final RectF rect;
        private final RectF visibleRect;

        public BackgroundDrawable(int i) {
            super(i);
            this.rect = new RectF();
            this.visibleRect = new RectF();
            Paint paint = new Paint(1);
            this.paint = paint;
            paint.setColor(i);
        }

        @Override // android.graphics.drawable.ColorDrawable, android.graphics.drawable.Drawable
        @Keep
        public void setAlpha(int i) {
            if (PhotoViewer.this.parentActivity instanceof LaunchActivity) {
                this.allowDrawContent = !PhotoViewer.this.isVisible || i != 255;
                ((LaunchActivity) PhotoViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent(this.allowDrawContent);
                if (PhotoViewer.this.parentAlert != null) {
                    if (this.allowDrawContent) {
                        PhotoViewer.this.parentAlert.setAllowDrawContent(true);
                    } else {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$BackgroundDrawable$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                PhotoViewer.BackgroundDrawable.this.lambda$setAlpha$0();
                            }
                        }, 50L);
                    }
                }
            }
            super.setAlpha(i);
            this.paint.setAlpha(i);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setAlpha$0() {
            if (PhotoViewer.this.parentAlert != null) {
                PhotoViewer.this.parentAlert.setAllowDrawContent(this.allowDrawContent);
            }
        }

        @Override // android.graphics.drawable.ColorDrawable, android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            Runnable runnable;
            if (PhotoViewer.this.animationInProgress != 0 && !AndroidUtilities.isTablet() && PhotoViewer.this.currentPlaceObject != null && PhotoViewer.this.currentPlaceObject.animatingImageView != null) {
                PhotoViewer.this.animatingImageView.getClippedVisibleRect(this.visibleRect);
                if (!this.visibleRect.isEmpty()) {
                    this.visibleRect.inset(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                    Rect bounds = getBounds();
                    float f = bounds.right;
                    float f2 = bounds.bottom;
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
            } else {
                super.draw(canvas);
            }
            if (getAlpha() == 0 || (runnable = this.drawRunnable) == null) {
                return;
            }
            AndroidUtilities.runOnUIThread(runnable);
            this.drawRunnable = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class SelectedPhotosListView extends RecyclerListView {
        private Drawable arrowDrawable;
        private Paint paint;
        private RectF rect;

        public SelectedPhotosListView(Context context) {
            super(context);
            this.paint = new Paint(1);
            this.rect = new RectF();
            setWillNotDraw(false);
            setClipToPadding(false);
            setTranslationY(-AndroidUtilities.dp(10.0f));
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() { // from class: org.telegram.ui.PhotoViewer.SelectedPhotosListView.1
                @Override // androidx.recyclerview.widget.DefaultItemAnimator
                protected void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                    SelectedPhotosListView.this.invalidate();
                }
            };
            setItemAnimator(defaultItemAnimator);
            defaultItemAnimator.setDelayAnimations(false);
            defaultItemAnimator.setSupportsChangeAnimations(false);
            setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(6.0f));
            this.paint.setColor(NUM);
            this.arrowDrawable = context.getResources().getDrawable(R.drawable.photo_tooltip2).mutate();
        }

        @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
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
                    i = (int) Math.min(i, Math.floor(childAt.getX()));
                    i2 = (int) Math.max(i2, Math.ceil(childAt.getX() + childAt.getMeasuredWidth()));
                }
                if (i == Integer.MAX_VALUE || i2 == Integer.MIN_VALUE) {
                    return;
                }
                this.rect.set(i - AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), i2 + AndroidUtilities.dp(6.0f), AndroidUtilities.dp(103.0f));
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), this.paint);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class CounterView extends View {
        private int currentCount;
        private int height;
        private Paint paint;
        private RectF rect;
        private float rotation;
        private StaticLayout staticLayout;
        private TextPaint textPaint;
        private int width;

        public CounterView(Context context) {
            super(context);
            this.currentCount = 0;
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(15.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textPaint.setColor(-1);
            Paint paint = new Paint(1);
            this.paint = paint;
            paint.setColor(-1);
            this.paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeJoin(Paint.Join.ROUND);
            this.rect = new RectF();
            setCount(0);
        }

        @Override // android.view.View
        @Keep
        public void setScaleX(float f) {
            super.setScaleX(f);
            invalidate();
        }

        @Override // android.view.View
        @Keep
        public void setRotationX(float f) {
            this.rotation = f;
            invalidate();
        }

        @Override // android.view.View
        public float getRotationX() {
            return this.rotation;
        }

        public void setCount(int i) {
            StaticLayout staticLayout = new StaticLayout("" + Math.max(1, i), this.textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.staticLayout = staticLayout;
            this.width = (int) Math.ceil((double) staticLayout.getLineWidth(0));
            this.height = this.staticLayout.getLineBottom(0);
            AnimatorSet animatorSet = new AnimatorSet();
            if (i == 0) {
                Paint paint = this.paint;
                Property<Paint, Integer> property = AnimationProperties.PAINT_ALPHA;
                animatorSet.playTogether(ObjectAnimator.ofFloat(this, View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.0f), ObjectAnimator.ofInt(paint, property, 0), ObjectAnimator.ofInt(this.textPaint, (Property<TextPaint, Integer>) property, 0));
                animatorSet.setInterpolator(new DecelerateInterpolator());
            } else {
                int i2 = this.currentCount;
                if (i2 == 0) {
                    Paint paint2 = this.paint;
                    Property<Paint, Integer> property2 = AnimationProperties.PAINT_ALPHA;
                    animatorSet.playTogether(ObjectAnimator.ofFloat(this, View.SCALE_X, 0.0f, 1.0f), ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.0f, 1.0f), ObjectAnimator.ofInt(paint2, property2, 0, 255), ObjectAnimator.ofInt(this.textPaint, (Property<TextPaint, Integer>) property2, 0, 255));
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                } else if (i < i2) {
                    animatorSet.playTogether(ObjectAnimator.ofFloat(this, View.SCALE_X, 1.1f, 1.0f), ObjectAnimator.ofFloat(this, View.SCALE_Y, 1.1f, 1.0f));
                    animatorSet.setInterpolator(new OvershootInterpolator());
                } else {
                    animatorSet.playTogether(ObjectAnimator.ofFloat(this, View.SCALE_X, 0.9f, 1.0f), ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.9f, 1.0f));
                    animatorSet.setInterpolator(new OvershootInterpolator());
                }
            }
            animatorSet.setDuration(180L);
            animatorSet.start();
            requestLayout();
            this.currentCount = i;
        }

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.max(this.width + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(30.0f)), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), NUM));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int measuredHeight = getMeasuredHeight() / 2;
            this.paint.setAlpha(255);
            this.rect.set(AndroidUtilities.dp(1.0f), measuredHeight - AndroidUtilities.dp(14.0f), getMeasuredWidth() - AndroidUtilities.dp(1.0f), measuredHeight + AndroidUtilities.dp(14.0f));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f), this.paint);
            if (this.staticLayout != null) {
                this.textPaint.setAlpha((int) ((1.0f - this.rotation) * 255.0f));
                canvas.save();
                canvas.translate((getMeasuredWidth() - this.width) / 2, ((getMeasuredHeight() - this.height) / 2) + AndroidUtilities.dpf2(0.2f) + (this.rotation * AndroidUtilities.dp(5.0f)));
                this.staticLayout.draw(canvas);
                canvas.restore();
                this.paint.setAlpha((int) (this.rotation * 255.0f));
                int centerX = (int) this.rect.centerX();
                int centerY = (int) (((int) this.rect.centerY()) - (AndroidUtilities.dp(5.0f) * (1.0f - this.rotation)));
                canvas.drawLine(AndroidUtilities.dp(5.0f) + centerX, centerY - AndroidUtilities.dp(5.0f), centerX - AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f) + centerY, this.paint);
                canvas.drawLine(centerX - AndroidUtilities.dp(5.0f), centerY - AndroidUtilities.dp(5.0f), centerX + AndroidUtilities.dp(5.0f), centerY + AndroidUtilities.dp(5.0f), this.paint);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class PhotoProgressView {
        private View parent;
        private final CombinedDrawable playDrawable;
        private final PlayPauseDrawable playPauseDrawable;
        private boolean visible;
        private long lastUpdateTime = 0;
        private float radOffset = 0.0f;
        private float currentProgress = 0.0f;
        private float animationProgressStart = 0.0f;
        private long currentProgressTime = 0;
        private float animatedProgressValue = 0.0f;
        private RectF progressRect = new RectF();
        private int backgroundState = -1;
        private int size = AndroidUtilities.dp(64.0f);
        private int previousBackgroundState = -2;
        private float animatedAlphaValue = 1.0f;
        private float[] animAlphas = new float[3];
        private float[] alphas = new float[3];
        private float scale = 1.0f;

        protected void onBackgroundStateUpdated(int i) {
            throw null;
        }

        protected void onVisibilityChanged(boolean z) {
            throw null;
        }

        public PhotoProgressView(View view) {
            if (PhotoViewer.decelerateInterpolator == null) {
                DecelerateInterpolator unused = PhotoViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                Paint unused2 = PhotoViewer.progressPaint = new Paint(1);
                PhotoViewer.progressPaint.setStyle(Paint.Style.STROKE);
                PhotoViewer.progressPaint.setStrokeCap(Paint.Cap.ROUND);
                PhotoViewer.progressPaint.setStrokeWidth(AndroidUtilities.dp(3.0f));
                PhotoViewer.progressPaint.setColor(-1);
            }
            this.parent = view;
            resetAlphas();
            PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable(28);
            this.playPauseDrawable = playPauseDrawable;
            playPauseDrawable.setDuration(200);
            this.playDrawable = new CombinedDrawable(ContextCompat.getDrawable(PhotoViewer.this.parentActivity, R.drawable.circle_big).mutate(), playPauseDrawable);
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
                        if (j2 < 300) {
                            this.animatedProgressValue = this.animationProgressStart + (f * PhotoViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f));
                        } else {
                            float f2 = this.currentProgress;
                            this.animatedProgressValue = f2;
                            this.animationProgressStart = f2;
                            this.currentProgressTime = 0L;
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
            this.currentProgressTime = 0L;
            this.parent.invalidate();
        }

        public void setBackgroundState(int i, boolean z, boolean z2) {
            int i2;
            int i3 = this.backgroundState;
            if (i3 == i) {
                return;
            }
            PlayPauseDrawable playPauseDrawable = this.playPauseDrawable;
            if (playPauseDrawable != null) {
                boolean z3 = z2 && (i3 == 3 || i3 == 4);
                if (i == 3) {
                    playPauseDrawable.setPause(false, z3);
                } else if (i == 4) {
                    playPauseDrawable.setPause(true, z3);
                }
                this.playPauseDrawable.setParent(this.parent);
                this.playPauseDrawable.invalidateSelf();
            }
            this.lastUpdateTime = System.currentTimeMillis();
            if (z && (i2 = this.backgroundState) != i) {
                this.previousBackgroundState = i2;
                this.animatedAlphaValue = 1.0f;
            } else {
                this.previousBackgroundState = -2;
            }
            this.backgroundState = i;
            onBackgroundStateUpdated(i);
            this.parent.invalidate();
        }

        public void setAlpha(float f) {
            setIndexedAlpha(0, f, false);
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
                if (i < fArr.length) {
                    if (i == 2) {
                        f = AndroidUtilities.accelerateInterpolator.getInterpolation(fArr[i]);
                    } else {
                        f = fArr[i];
                    }
                    f2 *= f;
                    i++;
                } else {
                    return f2;
                }
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
            return (PhotoViewer.this.containerView.getWidth() - ((int) (this.size * this.scale))) / 2;
        }

        public int getY() {
            int i = (int) ((((AndroidUtilities.displaySize.y + (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) - ((int) (this.size * this.scale))) / 2) + PhotoViewer.this.currentPanTranslationY);
            return PhotoViewer.this.sendPhotoType == 1 ? i - AndroidUtilities.dp(38.0f) : i;
        }

        public void onDraw(Canvas canvas) {
            int i;
            Drawable drawable;
            Drawable drawable2;
            int i2 = (int) (this.size * this.scale);
            int x = getX();
            int y = getY();
            float calculateAlpha = calculateAlpha();
            int i3 = this.previousBackgroundState;
            if (i3 >= 0 && i3 < PhotoViewer.progressDrawables.length + 2) {
                if (this.previousBackgroundState < PhotoViewer.progressDrawables.length) {
                    drawable2 = PhotoViewer.progressDrawables[this.previousBackgroundState];
                } else {
                    drawable2 = this.playDrawable;
                }
                if (drawable2 != null) {
                    drawable2.setAlpha((int) (this.animatedAlphaValue * 255.0f * calculateAlpha));
                    drawable2.setBounds(x, y, x + i2, y + i2);
                    drawable2.draw(canvas);
                }
            }
            int i4 = this.backgroundState;
            if (i4 >= 0 && i4 < PhotoViewer.progressDrawables.length + 2) {
                if (this.backgroundState < PhotoViewer.progressDrawables.length) {
                    drawable = PhotoViewer.progressDrawables[this.backgroundState];
                } else {
                    drawable = this.playDrawable;
                }
                if (drawable != null) {
                    if (this.previousBackgroundState != -2) {
                        drawable.setAlpha((int) ((1.0f - this.animatedAlphaValue) * 255.0f * calculateAlpha));
                    } else {
                        drawable.setAlpha((int) (calculateAlpha * 255.0f));
                    }
                    drawable.setBounds(x, y, x + i2, y + i2);
                    drawable.draw(canvas);
                }
            }
            int i5 = this.backgroundState;
            if (i5 == 0 || i5 == 1 || (i = this.previousBackgroundState) == 0 || i == 1) {
                int dp = AndroidUtilities.dp(4.0f);
                if (this.previousBackgroundState != -2) {
                    PhotoViewer.progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f * calculateAlpha));
                } else {
                    PhotoViewer.progressPaint.setAlpha((int) (calculateAlpha * 255.0f));
                }
                this.progressRect.set(x + dp, y + dp, (x + i2) - dp, (y + i2) - dp);
                canvas.drawArc(this.progressRect, this.radOffset - 90.0f, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, PhotoViewer.progressPaint);
                updateAnimation(true);
                return;
            }
            updateAnimation(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class FrameLayoutDrawer extends SizeNotifierFrameLayoutPhoto {
        AdjustPanLayoutHelper adjustPanLayoutHelper;
        private boolean captionAbove;
        private boolean ignoreLayout;
        private Paint paint;

        public FrameLayoutDrawer(Context context) {
            super(context, false);
            this.paint = new Paint();
            this.adjustPanLayoutHelper = new AdjustPanLayoutHelper(this, false) { // from class: org.telegram.ui.PhotoViewer.FrameLayoutDrawer.1
                @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                protected void onPanTranslationUpdate(float f, float f2, boolean z) {
                    PhotoViewer.this.currentPanTranslationY = f;
                    if (PhotoViewer.this.currentEditMode != 3) {
                        PhotoViewer.this.actionBar.setTranslationY(f);
                    }
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
                    if (PhotoViewer.this.pickerView != null) {
                        PhotoViewer.this.pickerView.setTranslationY(f);
                    }
                    if (PhotoViewer.this.pickerViewSendButton != null) {
                        PhotoViewer.this.pickerViewSendButton.setTranslationY(f);
                    }
                    float f3 = 0.0f;
                    if (PhotoViewer.this.currentEditMode == 3) {
                        if (PhotoViewer.this.captionEditText != null) {
                            PhotoViewer.this.captionEditText.setTranslationY(f);
                        }
                        if (PhotoViewer.this.photoPaintView != null) {
                            PhotoViewer.this.photoPaintView.setTranslationY(0.0f);
                            PhotoViewer.this.photoPaintView.getColorPicker().setTranslationY(f);
                            PhotoViewer.this.photoPaintView.getToolsView().setTranslationY(f);
                            PhotoViewer.this.photoPaintView.getColorPickerBackground().setTranslationY(f);
                            PhotoViewer.this.photoPaintView.getCurtainView().setTranslationY(f);
                        }
                    } else {
                        if (PhotoViewer.this.photoPaintView != null) {
                            PhotoViewer.this.photoPaintView.setTranslationY(f);
                        }
                        if (PhotoViewer.this.captionEditText != null) {
                            if (f2 >= 0.5f) {
                                f3 = (f2 - 0.5f) / 0.5f;
                            }
                            PhotoViewer.this.captionEditText.setAlpha(f3);
                            PhotoViewerCaptionEnterView photoViewerCaptionEnterView = PhotoViewer.this.captionEditText;
                            float f4 = this.keyboardSize;
                            photoViewerCaptionEnterView.setTranslationY((f - f4) + (AndroidUtilities.dp(f4 / 2.0f) * (1.0f - f2)));
                        }
                    }
                    if (PhotoViewer.this.muteItem != null) {
                        PhotoViewer.this.muteItem.setTranslationY(f);
                    }
                    if (PhotoViewer.this.cameraItem != null) {
                        PhotoViewer.this.cameraItem.setTranslationY(f);
                    }
                    if (PhotoViewer.this.captionLimitView != null) {
                        PhotoViewer.this.captionLimitView.setTranslationY(f);
                    }
                    FrameLayoutDrawer.this.invalidate();
                }

                @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                protected void onTransitionStart(boolean z, int i) {
                    int i2;
                    String str;
                    PhotoViewer.this.navigationBar.setVisibility(4);
                    PhotoViewer.this.animateNavBarColorTo(-16777216);
                    if (PhotoViewer.this.captionEditText.getTag() == null || !z) {
                        PhotoViewer.this.checkImageView.animate().alpha(1.0f).setDuration(220L).start();
                        PhotoViewer.this.photosCounterView.animate().alpha(1.0f).setDuration(220L).start();
                        if (PhotoViewer.this.lastTitle == null || PhotoViewer.this.isCurrentVideo) {
                            return;
                        }
                        PhotoViewer.this.actionBar.setTitleAnimated(PhotoViewer.this.lastTitle, false, 220L);
                        PhotoViewer.this.lastTitle = null;
                        return;
                    }
                    if (PhotoViewer.this.isCurrentVideo) {
                        if (PhotoViewer.this.muteVideo) {
                            i2 = R.string.GifCaption;
                            str = "GifCaption";
                        } else {
                            i2 = R.string.VideoCaption;
                            str = "VideoCaption";
                        }
                        PhotoViewer.this.actionBar.setTitleAnimated(LocaleController.getString(str, i2), true, 220L);
                    } else {
                        PhotoViewer.this.actionBar.setTitleAnimated(LocaleController.getString("PhotoCaption", R.string.PhotoCaption), true, 220L);
                    }
                    PhotoViewer.this.captionEditText.setAlpha(0.0f);
                    PhotoViewer.this.checkImageView.animate().alpha(0.0f).setDuration(220L).start();
                    PhotoViewer.this.photosCounterView.animate().alpha(0.0f).setDuration(220L).start();
                    PhotoViewer.this.selectedPhotosListView.animate().alpha(0.0f).translationY(-AndroidUtilities.dp(10.0f)).setDuration(220L).start();
                }

                @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                protected void onTransitionEnd() {
                    super.onTransitionEnd();
                    PhotoViewer.this.navigationBar.setVisibility(PhotoViewer.this.currentEditMode != 2 ? 0 : 4);
                    if (PhotoViewer.this.captionEditText.getTag() == null) {
                        PhotoViewer.this.captionEditText.setVisibility(8);
                    }
                    PhotoViewer.this.captionEditText.setTranslationY(0.0f);
                }

                @Override // org.telegram.ui.ActionBar.AdjustPanLayoutHelper
                protected boolean heightAnimationEnabled() {
                    return !PhotoViewer.this.captionEditText.isPopupShowing() && PhotoViewer.this.keyboardAnimationEnabled;
                }
            };
            setWillNotDraw(false);
            this.paint.setColor(NUM);
        }

        /* JADX WARN: Removed duplicated region for block: B:95:0x029c  */
        /* JADX WARN: Removed duplicated region for block: B:96:0x029f  */
        @Override // android.widget.FrameLayout, android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onMeasure(int r19, int r20) {
            /*
                Method dump skipped, instructions count: 730
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.FrameLayoutDrawer.onMeasure(int, int):void");
        }

        /* JADX WARN: Removed duplicated region for block: B:28:0x008d  */
        /* JADX WARN: Removed duplicated region for block: B:32:0x009b  */
        /* JADX WARN: Removed duplicated region for block: B:36:0x00af  */
        /* JADX WARN: Removed duplicated region for block: B:38:0x00bc  */
        @Override // org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onLayout(boolean r16, int r17, int r18, int r19, int r20) {
            /*
                Method dump skipped, instructions count: 435
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.FrameLayoutDrawer.onLayout(boolean, int, int, int, int):void");
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            PhotoViewer.this.onDraw(canvas);
            if (!PhotoViewer.this.isStatusBarVisible() || AndroidUtilities.statusBarHeight == 0 || PhotoViewer.this.actionBar == null) {
                return;
            }
            if (Build.VERSION.SDK_INT < 21) {
                this.paint.setAlpha((int) (PhotoViewer.this.actionBar.getAlpha() * 255.0f * 0.2f));
                canvas.drawRect(0.0f, PhotoViewer.this.currentPanTranslationY, getMeasuredWidth(), PhotoViewer.this.currentPanTranslationY + AndroidUtilities.statusBarHeight, this.paint);
            }
            this.paint.setAlpha((int) (PhotoViewer.this.actionBar.getAlpha() * 255.0f * 0.498f));
            if (getPaddingRight() > 0) {
                canvas.drawRect(getMeasuredWidth() - getPaddingRight(), 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.paint);
            }
            if (getPaddingLeft() > 0) {
                canvas.drawRect(0.0f, 0.0f, getPaddingLeft(), getMeasuredHeight(), this.paint);
            }
            if (getPaddingBottom() <= 0) {
                return;
            }
            float dpf2 = AndroidUtilities.dpf2(24.0f) * (1.0f - PhotoViewer.this.actionBar.getAlpha());
            canvas.drawRect(0.0f, (getMeasuredHeight() - getPaddingBottom()) + dpf2, getMeasuredWidth(), getMeasuredHeight() + dpf2, this.paint);
        }

        @Override // android.view.View
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (PhotoViewer.this.photoViewerWebView == null || !PhotoViewer.this.photoViewerWebView.isControllable() || PhotoViewer.this.videoForwardDrawable == null || !PhotoViewer.this.videoForwardDrawable.isAnimating()) {
                return;
            }
            int measuredHeight = ((int) (PhotoViewer.this.photoViewerWebView.getWebView().getMeasuredHeight() * (PhotoViewer.this.scale - 1.0f))) / 2;
            PhotoViewer.this.videoForwardDrawable.setBounds(PhotoViewer.this.photoViewerWebView.getLeft(), (PhotoViewer.this.photoViewerWebView.getWebView().getTop() - measuredHeight) + ((int) (PhotoViewer.this.translationY / PhotoViewer.this.scale)), PhotoViewer.this.photoViewerWebView.getRight(), PhotoViewer.this.photoViewerWebView.getWebView().getBottom() + measuredHeight + ((int) (PhotoViewer.this.translationY / PhotoViewer.this.scale)));
            PhotoViewer.this.videoForwardDrawable.draw(canvas);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0, 0, getWidth(), getHeight());
            super.dispatchDraw(canvas);
            canvas.restore();
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (view == PhotoViewer.this.leftPaintingOverlay || view == PhotoViewer.this.rightPaintingOverlay) {
                return false;
            }
            if (view != PhotoViewer.this.navigationBar) {
                canvas.save();
                canvas.clipRect(0, 0, getWidth(), getHeight());
            }
            boolean drawChildInternal = drawChildInternal(canvas, view, j);
            if (view != PhotoViewer.this.navigationBar) {
                canvas.restore();
            }
            return drawChildInternal;
        }

        protected boolean drawChildInternal(Canvas canvas, View view, long j) {
            if (view == PhotoViewer.this.mentionListView || view == PhotoViewer.this.captionEditText) {
                if (PhotoViewer.this.currentEditMode != 0 && PhotoViewer.this.currentPanTranslationY == 0.0f) {
                    return false;
                }
                if (AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) {
                    if (!PhotoViewer.this.captionEditText.isPopupShowing() && PhotoViewer.this.captionEditText.getEmojiPadding() == 0 && PhotoViewer.this.captionEditText.getTag() == null) {
                        return false;
                    }
                } else if (!PhotoViewer.this.captionEditText.isPopupShowing() && PhotoViewer.this.captionEditText.getEmojiPadding() == 0 && getKeyboardHeight() == 0 && PhotoViewer.this.currentPanTranslationY == 0.0f) {
                    return false;
                }
                if (view == PhotoViewer.this.mentionListView) {
                    canvas.save();
                    canvas.clipRect(view.getX(), view.getY(), view.getX() + view.getWidth(), PhotoViewer.this.captionEditText.getTop());
                    canvas.drawColor(NUM);
                    boolean drawChild = super.drawChild(canvas, view, j);
                    canvas.restore();
                    return drawChild;
                }
            } else if (view == PhotoViewer.this.cameraItem || view == PhotoViewer.this.muteItem || view == PhotoViewer.this.pickerView || view == PhotoViewer.this.videoTimelineView || view == PhotoViewer.this.pickerViewSendButton || view == PhotoViewer.this.captionLimitView || view == PhotoViewer.this.captionTextViewSwitcher || (PhotoViewer.this.muteItem.getVisibility() == 0 && view == PhotoViewer.this.bottomLayout)) {
                if (PhotoViewer.this.captionEditText.isPopupAnimating()) {
                    view.setTranslationY(PhotoViewer.this.captionEditText.getEmojiPadding());
                    PhotoViewer.this.bottomTouchEnabled = false;
                } else {
                    int emojiPadding = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : PhotoViewer.this.captionEditText.getEmojiPadding();
                    if (PhotoViewer.this.captionEditText.isPopupShowing() || (((AndroidUtilities.isInMultiwindow || AndroidUtilities.usingHardwareInput) && PhotoViewer.this.captionEditText.getTag() != null) || getKeyboardHeight() > AndroidUtilities.dp(80.0f) || emojiPadding != 0)) {
                        PhotoViewer.this.bottomTouchEnabled = false;
                        return false;
                    }
                    PhotoViewer.this.bottomTouchEnabled = true;
                }
            } else if (view == PhotoViewer.this.checkImageView || view == PhotoViewer.this.photosCounterView) {
                if (PhotoViewer.this.captionEditText.getTag() != null) {
                    PhotoViewer.this.bottomTouchEnabled = false;
                    if (view.getAlpha() < 0.0f) {
                        return false;
                    }
                } else {
                    PhotoViewer.this.bottomTouchEnabled = true;
                }
            } else if (view == PhotoViewer.this.miniProgressView) {
                return false;
            }
            if (view != PhotoViewer.this.videoTimelineView || PhotoViewer.this.videoTimelineView.getTranslationY() <= 0.0f || PhotoViewer.this.pickerView.getTranslationY() != 0.0f) {
                try {
                    if (view != PhotoViewer.this.aspectRatioFrameLayout && view != PhotoViewer.this.paintingOverlay) {
                        if (super.drawChild(canvas, view, j)) {
                            return true;
                        }
                    }
                    return false;
                } catch (Throwable unused) {
                    return true;
                }
            }
            canvas.save();
            canvas.clipRect(PhotoViewer.this.videoTimelineView.getX(), PhotoViewer.this.videoTimelineView.getY(), PhotoViewer.this.videoTimelineView.getX() + PhotoViewer.this.videoTimelineView.getMeasuredWidth(), PhotoViewer.this.videoTimelineView.getBottom());
            boolean drawChild2 = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild2;
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.adjustPanLayoutHelper.setResizableView(PhotoViewer.this.windowView);
            this.adjustPanLayoutHelper.onAttach();
            Bulletin.addDelegate(this, new Bulletin.Delegate() { // from class: org.telegram.ui.PhotoViewer.FrameLayoutDrawer.2
                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ int getTopOffset(int i) {
                    return Bulletin.Delegate.CC.$default$getTopOffset(this, i);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ void onBottomOffsetChange(float f) {
                    Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ void onHide(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onHide(this, bulletin);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ void onShow(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onShow(this, bulletin);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public int getBottomOffset(int i) {
                    int i2 = 0;
                    if (PhotoViewer.this.bottomLayout != null && PhotoViewer.this.bottomLayout.getVisibility() == 0) {
                        i2 = 0 + PhotoViewer.this.bottomLayout.getHeight();
                    }
                    return (PhotoViewer.this.groupedPhotosListView == null || !PhotoViewer.this.groupedPhotosListView.hasPhotos()) ? i2 : (AndroidUtilities.isTablet() || PhotoViewer.this.containerView.getMeasuredHeight() > PhotoViewer.this.containerView.getMeasuredWidth()) ? i2 + PhotoViewer.this.groupedPhotosListView.getHeight() : i2;
                }
            });
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.adjustPanLayoutHelper.onDetach();
            Bulletin.removeDelegate(this);
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto
        public void notifyHeightChanged() {
            super.notifyHeightChanged();
            if (PhotoViewer.this.isCurrentVideo) {
                PhotoViewer.this.photoProgressViews[0].setIndexedAlpha(2, getKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? 1.0f : 0.0f, true);
            }
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 24) {
            VPC_PROGRESS = new FloatProperty<VideoPlayerControlFrameLayout>("progress") { // from class: org.telegram.ui.PhotoViewer.8
                @Override // android.util.FloatProperty
                public void setValue(VideoPlayerControlFrameLayout videoPlayerControlFrameLayout, float f) {
                    videoPlayerControlFrameLayout.setProgress(f);
                }

                @Override // android.util.Property
                public Float get(VideoPlayerControlFrameLayout videoPlayerControlFrameLayout) {
                    return Float.valueOf(videoPlayerControlFrameLayout.getProgress());
                }
            };
        } else {
            VPC_PROGRESS = new Property<VideoPlayerControlFrameLayout, Float>(Float.class, "progress") { // from class: org.telegram.ui.PhotoViewer.9
                @Override // android.util.Property
                public void set(VideoPlayerControlFrameLayout videoPlayerControlFrameLayout, Float f) {
                    videoPlayerControlFrameLayout.setProgress(f.floatValue());
                }

                @Override // android.util.Property
                public Float get(VideoPlayerControlFrameLayout videoPlayerControlFrameLayout) {
                    return Float.valueOf(videoPlayerControlFrameLayout.getProgress());
                }
            };
        }
        Instance = null;
        PipInstance = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class VideoPlayerControlFrameLayout extends FrameLayout {
        private boolean ignoreLayout;
        private int lastTimeWidth;
        private int parentHeight;
        private int parentWidth;
        private float progress;
        private boolean seekBarTransitionEnabled;
        private SpringAnimation timeSpring;
        private FloatValueHolder timeValue;
        private boolean translationYAnimationEnabled;

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(DynamicAnimation dynamicAnimation, float f, float f2) {
            PhotoViewer.this.videoPlayerSeekbar.setSize((int) (((getMeasuredWidth() - AndroidUtilities.dp(16.0f)) - f) - (this.parentWidth > this.parentHeight ? AndroidUtilities.dp(48.0f) : 0)), getMeasuredHeight());
        }

        public VideoPlayerControlFrameLayout(Context context) {
            super(context);
            this.progress = 1.0f;
            this.translationYAnimationEnabled = true;
            this.timeValue = new FloatValueHolder(0.0f);
            this.timeSpring = new SpringAnimation(this.timeValue).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.PhotoViewer$VideoPlayerControlFrameLayout$$ExternalSyntheticLambda0
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                    PhotoViewer.VideoPlayerControlFrameLayout.this.lambda$new$0(dynamicAnimation, f, f2);
                }
            });
            setWillNotDraw(false);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.progress < 1.0f) {
                return false;
            }
            if (PhotoViewer.this.videoPlayerSeekbar.onTouch(motionEvent.getAction(), motionEvent.getX() - AndroidUtilities.dp(2.0f), motionEvent.getY())) {
                getParent().requestDisallowInterceptTouchEvent(true);
                PhotoViewer.this.videoPlayerSeekbarView.invalidate();
            }
            return true;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.timeValue.setValue(0.0f);
            this.lastTimeWidth = 0;
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            int i3;
            String format;
            this.ignoreLayout = true;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.videoPlayerTime.getLayoutParams();
            if (this.parentWidth > this.parentHeight) {
                if (PhotoViewer.this.exitFullscreenButton.getVisibility() != 0) {
                    PhotoViewer.this.exitFullscreenButton.setVisibility(0);
                }
                i3 = AndroidUtilities.dp(48.0f);
                layoutParams.rightMargin = AndroidUtilities.dp(47.0f);
            } else {
                if (PhotoViewer.this.exitFullscreenButton.getVisibility() != 4) {
                    PhotoViewer.this.exitFullscreenButton.setVisibility(4);
                }
                layoutParams.rightMargin = AndroidUtilities.dp(12.0f);
                i3 = 0;
            }
            this.ignoreLayout = false;
            super.onMeasure(i, i2);
            long j = 0;
            if (PhotoViewer.this.videoPlayer != null) {
                long duration = PhotoViewer.this.videoPlayer.getDuration();
                if (duration != -9223372036854775807L) {
                    j = duration;
                }
            } else if (PhotoViewer.this.photoViewerWebView != null && PhotoViewer.this.photoViewerWebView.isControllable()) {
                j = PhotoViewer.this.photoViewerWebView.getVideoDuration();
            }
            long j2 = j / 1000;
            long j3 = j2 / 60;
            if (j3 > 60) {
                format = String.format(Locale.ROOT, "%02d:%02d:%02d", Long.valueOf(j3 / 60), Long.valueOf(j3 % 60), Long.valueOf(j2 % 60));
            } else {
                format = String.format(Locale.ROOT, "%02d:%02d", Long.valueOf(j3), Long.valueOf(j2 % 60));
            }
            int ceil = (int) Math.ceil(PhotoViewer.this.videoPlayerTime.getPaint().measureText(String.format(Locale.ROOT, "%1$s / %1$s", format)));
            this.timeSpring.cancel();
            if (this.lastTimeWidth != 0) {
                float f = ceil;
                if (this.timeValue.getValue() != f) {
                    this.timeSpring.getSpring().setFinalPosition(f);
                    this.timeSpring.start();
                    this.lastTimeWidth = ceil;
                }
            }
            PhotoViewer.this.videoPlayerSeekbar.setSize(((getMeasuredWidth() - AndroidUtilities.dp(16.0f)) - ceil) - i3, getMeasuredHeight());
            this.timeValue.setValue(ceil);
            this.lastTimeWidth = ceil;
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            float currentPosition = PhotoViewer.this.videoPlayer != null ? ((float) PhotoViewer.this.videoPlayer.getCurrentPosition()) / ((float) PhotoViewer.this.videoPlayer.getDuration()) : 0.0f;
            if (PhotoViewer.this.playerWasReady) {
                PhotoViewer.this.videoPlayerSeekbar.setProgress(currentPosition);
            }
            PhotoViewer.this.videoTimelineView.setProgress(currentPosition);
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
            PhotoViewer.this.exitFullscreenButton.setAlpha(f);
            if (this.seekBarTransitionEnabled) {
                PhotoViewer.this.videoPlayerTime.setPivotX(PhotoViewer.this.videoPlayerTime.getWidth());
                PhotoViewer.this.videoPlayerTime.setPivotY(PhotoViewer.this.videoPlayerTime.getHeight());
                float f2 = 1.0f - f;
                float f3 = 1.0f - (0.1f * f2);
                PhotoViewer.this.videoPlayerTime.setScaleX(f3);
                PhotoViewer.this.videoPlayerTime.setScaleY(f3);
                PhotoViewer.this.videoPlayerSeekbar.setTransitionProgress(f2);
                return;
            }
            if (this.translationYAnimationEnabled) {
                setTranslationY(AndroidUtilities.dpf2(24.0f) * (1.0f - f));
            }
            PhotoViewer.this.videoPlayerSeekbarView.setAlpha(f);
        }

        public void setSeekBarTransitionEnabled(boolean z) {
            if (this.seekBarTransitionEnabled != z) {
                this.seekBarTransitionEnabled = z;
                if (!z) {
                    PhotoViewer.this.videoPlayerTime.setScaleX(1.0f);
                    PhotoViewer.this.videoPlayerTime.setScaleY(1.0f);
                    PhotoViewer.this.videoPlayerSeekbar.setTransitionProgress(0.0f);
                } else {
                    setTranslationY(0.0f);
                    PhotoViewer.this.videoPlayerSeekbarView.setAlpha(1.0f);
                }
                onProgressChanged(this.progress);
            }
        }

        public void setTranslationYAnimationEnabled(boolean z) {
            if (this.translationYAnimationEnabled != z) {
                this.translationYAnimationEnabled = z;
                if (!z) {
                    setTranslationY(0.0f);
                }
                onProgressChanged(this.progress);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class CaptionTextViewSwitcher extends TextViewSwitcher {
        private float alpha;
        private boolean inScrollView;

        public CaptionTextViewSwitcher(Context context) {
            super(context);
            this.inScrollView = false;
            this.alpha = 1.0f;
        }

        @Override // android.view.View
        public void setVisibility(int i) {
            setVisibility(i, true);
        }

        public void setVisibility(int i, boolean z) {
            super.setVisibility(i);
            if (!this.inScrollView || !z) {
                return;
            }
            PhotoViewer.this.captionScrollView.setVisibility(i);
        }

        @Override // android.view.View
        public void setAlpha(float f) {
            this.alpha = f;
            if (this.inScrollView) {
                PhotoViewer.this.captionScrollView.setAlpha(f);
            } else {
                super.setAlpha(f);
            }
        }

        @Override // android.view.View
        public float getAlpha() {
            if (this.inScrollView) {
                return this.alpha;
            }
            return super.getAlpha();
        }

        @Override // android.view.View
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            if (this.inScrollView) {
                PhotoViewer.this.captionScrollView.invalidate();
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (PhotoViewer.this.captionContainer == null || getParent() != PhotoViewer.this.captionContainer) {
                return;
            }
            this.inScrollView = true;
            PhotoViewer.this.captionScrollView.setVisibility(getVisibility());
            PhotoViewer.this.captionScrollView.setAlpha(this.alpha);
            super.setAlpha(1.0f);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (this.inScrollView) {
                this.inScrollView = false;
                PhotoViewer.this.captionScrollView.setVisibility(8);
                super.setAlpha(this.alpha);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class CaptionScrollView extends NestedScrollView {
        private Method abortAnimatedScrollMethod;
        private float backgroundAlpha;
        private boolean dontChangeTopMargin;
        private boolean isLandscape;
        private boolean nestedScrollStarted;
        private float overScrollY;
        private final Paint paint;
        private int pendingTopMargin;
        private int prevHeight;
        private OverScroller scroller;
        private final SpringAnimation springAnimation;
        private int textHash;
        private float velocitySign;
        private float velocityY;

        @Override // androidx.core.widget.NestedScrollView, android.view.View
        protected float getBottomFadingEdgeStrength() {
            return 1.0f;
        }

        @Override // androidx.core.widget.NestedScrollView, android.view.View
        protected float getTopFadingEdgeStrength() {
            return 1.0f;
        }

        public CaptionScrollView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.paint = paint;
            this.backgroundAlpha = 1.0f;
            this.pendingTopMargin = -1;
            setClipChildren(false);
            setOverScrollMode(2);
            paint.setColor(-16777216);
            setFadingEdgeLength(AndroidUtilities.dp(12.0f));
            setVerticalFadingEdgeEnabled(true);
            setWillNotDraw(false);
            SpringAnimation springAnimation = new SpringAnimation(PhotoViewer.this.captionTextViewSwitcher, DynamicAnimation.TRANSLATION_Y, 0.0f);
            this.springAnimation = springAnimation;
            springAnimation.getSpring().setStiffness(100.0f);
            springAnimation.setMinimumVisibleChange(1.0f);
            springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.PhotoViewer$CaptionScrollView$$ExternalSyntheticLambda0
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                    PhotoViewer.CaptionScrollView.this.lambda$new$0(dynamicAnimation, f, f2);
                }
            });
            springAnimation.getSpring().setDampingRatio(1.0f);
            try {
                Method declaredMethod = NestedScrollView.class.getDeclaredMethod("abortAnimatedScroll", new Class[0]);
                this.abortAnimatedScrollMethod = declaredMethod;
                declaredMethod.setAccessible(true);
            } catch (Exception e) {
                this.abortAnimatedScrollMethod = null;
                FileLog.e(e);
            }
            try {
                Field declaredField = NestedScrollView.class.getDeclaredField("mScroller");
                declaredField.setAccessible(true);
                this.scroller = (OverScroller) declaredField.get(this);
            } catch (Exception e2) {
                this.scroller = null;
                FileLog.e(e2);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(DynamicAnimation dynamicAnimation, float f, float f2) {
            this.overScrollY = f;
            this.velocityY = f2;
        }

        @Override // androidx.core.widget.NestedScrollView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0 || motionEvent.getY() >= (PhotoViewer.this.captionContainer.getTop() - getScrollY()) + PhotoViewer.this.captionTextViewSwitcher.getTranslationY()) {
                return super.onTouchEvent(motionEvent);
            }
            return false;
        }

        @Override // androidx.core.widget.NestedScrollView, android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
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
            if (calculateNewContainerMarginTop >= 0) {
                if (!this.dontChangeTopMargin) {
                    ((ViewGroup.MarginLayoutParams) PhotoViewer.this.captionContainer.getLayoutParams()).topMargin = calculateNewContainerMarginTop;
                    this.pendingTopMargin = -1;
                    return;
                }
                this.pendingTopMargin = calculateNewContainerMarginTop;
            }
        }

        public int calculateNewContainerMarginTop(int i, int i2) {
            int dp;
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
            int i3 = 2;
            if ((z && lineCount <= 2) || (!z && lineCount <= 5)) {
                dp = currentView.getMeasuredHeight();
            } else {
                if (!z) {
                    i3 = 5;
                }
                int min = Math.min(i3, lineCount);
                loop0: while (min > 1) {
                    int i4 = min - 1;
                    for (int lineStart = layout.getLineStart(i4); lineStart < layout.getLineEnd(i4); lineStart++) {
                        if (Character.isLetterOrDigit(text.charAt(lineStart))) {
                            break loop0;
                        }
                    }
                    min--;
                }
                i2 -= currentView.getPaint().getFontMetricsInt(null) * min;
                dp = AndroidUtilities.dp(8.0f);
            }
            return i2 - dp;
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
                    FileLog.e(e);
                }
            }
        }

        @Override // androidx.core.widget.NestedScrollView
        public void fling(int i) {
            super.fling(i);
            this.velocitySign = Math.signum(i);
            this.velocityY = 0.0f;
        }

        @Override // androidx.core.widget.NestedScrollView
        public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2, int i3) {
            iArr[1] = 0;
            if (this.nestedScrollStarted) {
                float f = this.overScrollY;
                if ((f > 0.0f && i2 > 0) || (f < 0.0f && i2 < 0)) {
                    float f2 = i2;
                    float f3 = f - f2;
                    if (f > 0.0f) {
                        if (f3 < 0.0f) {
                            this.overScrollY = 0.0f;
                            iArr[1] = (int) (iArr[1] + f2 + f3);
                        } else {
                            this.overScrollY = f3;
                            iArr[1] = iArr[1] + i2;
                        }
                    } else if (f3 > 0.0f) {
                        this.overScrollY = 0.0f;
                        iArr[1] = (int) (iArr[1] + f2 + f3);
                    } else {
                        this.overScrollY = f3;
                        iArr[1] = iArr[1] + i2;
                    }
                    PhotoViewer.this.captionTextViewSwitcher.setTranslationY(this.overScrollY);
                    return true;
                }
            }
            return false;
        }

        @Override // androidx.core.widget.NestedScrollView
        public void dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr, int i5, int[] iArr2) {
            float f;
            if (i4 != 0) {
                int round = Math.round(i4 * (1.0f - Math.abs((-this.overScrollY) / (PhotoViewer.this.captionContainer.getTop() - ((PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight())))));
                if (round == 0) {
                    return;
                }
                if (!this.nestedScrollStarted) {
                    if (this.springAnimation.isRunning()) {
                        return;
                    }
                    OverScroller overScroller = this.scroller;
                    float currVelocity = overScroller != null ? overScroller.getCurrVelocity() : Float.NaN;
                    if (!Float.isNaN(currVelocity)) {
                        Point point = AndroidUtilities.displaySize;
                        float min = Math.min(point.x > point.y ? 3000.0f : 5000.0f, currVelocity);
                        round = (int) ((round * min) / currVelocity);
                        f = min * (-this.velocitySign);
                    } else {
                        f = 0.0f;
                    }
                    if (round != 0) {
                        this.overScrollY -= round;
                        PhotoViewer.this.captionTextViewSwitcher.setTranslationY(this.overScrollY);
                    }
                    startSpringAnimationIfNotRunning(f);
                    return;
                }
                this.overScrollY -= round;
                PhotoViewer.this.captionTextViewSwitcher.setTranslationY(this.overScrollY);
            }
        }

        private void startSpringAnimationIfNotRunning(float f) {
            if (!this.springAnimation.isRunning()) {
                this.springAnimation.setStartVelocity(f);
                this.springAnimation.start();
            }
        }

        @Override // androidx.core.widget.NestedScrollView
        public boolean startNestedScroll(int i, int i2) {
            if (i2 == 0) {
                this.springAnimation.cancel();
                this.nestedScrollStarted = true;
                this.overScrollY = PhotoViewer.this.captionTextViewSwitcher.getTranslationY();
            }
            return true;
        }

        @Override // androidx.core.widget.NestedScrollView, android.view.View
        public void computeScroll() {
            OverScroller overScroller;
            super.computeScroll();
            if (this.nestedScrollStarted || this.overScrollY == 0.0f || (overScroller = this.scroller) == null || !overScroller.isFinished()) {
                return;
            }
            startSpringAnimationIfNotRunning(0.0f);
        }

        @Override // androidx.core.widget.NestedScrollView
        public void stopNestedScroll(int i) {
            OverScroller overScroller;
            if (!this.nestedScrollStarted || i != 0) {
                return;
            }
            this.nestedScrollStarted = false;
            if (this.overScrollY == 0.0f || (overScroller = this.scroller) == null || !overScroller.isFinished()) {
                return;
            }
            startSpringAnimationIfNotRunning(this.velocityY);
        }

        @Override // androidx.core.widget.NestedScrollView, android.view.View
        public void draw(Canvas canvas) {
            int width = getWidth();
            int height = getHeight();
            int scrollY = getScrollY();
            int save = canvas.save();
            int i = height + scrollY;
            canvas.clipRect(0, scrollY, width, i);
            this.paint.setAlpha((int) (this.backgroundAlpha * 127.0f));
            canvas.drawRect(0.0f, PhotoViewer.this.captionContainer.getTop() + PhotoViewer.this.captionTextViewSwitcher.getTranslationY(), width, i, this.paint);
            super.draw(canvas);
            canvas.restoreToCount(save);
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            if (PhotoViewer.this.isActionBarVisible) {
                int scrollY = getScrollY();
                float translationY = PhotoViewer.this.captionTextViewSwitcher.getTranslationY();
                float f = 0.0f;
                boolean z = scrollY == 0 && translationY == 0.0f;
                boolean z2 = scrollY == 0 && translationY == 0.0f;
                if (!z) {
                    int y = PhotoViewer.this.photoProgressViews[0].getY() + PhotoViewer.this.photoProgressViews[0].size;
                    int top = (((PhotoViewer.this.captionContainer.getTop() + ((int) translationY)) - scrollY) + ((PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight())) - AndroidUtilities.dp(12.0f);
                    z2 = top > ((int) PhotoViewer.this.fullscreenButton[0].getY()) + AndroidUtilities.dp(32.0f);
                    z = top > y;
                }
                if (PhotoViewer.this.allowShowFullscreenButton) {
                    if (PhotoViewer.this.fullscreenButton[0].getTag() == null || ((Integer) PhotoViewer.this.fullscreenButton[0].getTag()).intValue() != 3 || !z2) {
                        if (PhotoViewer.this.fullscreenButton[0].getTag() == null && !z2) {
                            PhotoViewer.this.fullscreenButton[0].setTag(3);
                            PhotoViewer.this.fullscreenButton[0].animate().alpha(0.0f).setListener(null).setDuration(150L).start();
                        }
                    } else {
                        PhotoViewer.this.fullscreenButton[0].setTag(2);
                        PhotoViewer.this.fullscreenButton[0].animate().alpha(1.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.CaptionScrollView.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                PhotoViewer.this.fullscreenButton[0].setTag(null);
                            }
                        }).start();
                    }
                }
                PhotoProgressView photoProgressView = PhotoViewer.this.photoProgressViews[0];
                if (z) {
                    f = 1.0f;
                }
                photoProgressView.setIndexedAlpha(2, f, true);
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
        this.videoFrameBitmapPaint.setColor(-1);
        this.centerImage.setFileLoadingPriority(3);
    }

    /* JADX WARN: Code restructure failed: missing block: B:298:0x0629, code lost:
        if (r1.get(r1.size() - 1).getDialogId() != r31.mergeDialogId) goto L345;
     */
    /* JADX WARN: Code restructure failed: missing block: B:312:0x066c, code lost:
        if (r31.imagesArrTemp.get(0).getDialogId() != r31.mergeDialogId) goto L345;
     */
    /* JADX WARN: Code restructure failed: missing block: B:313:0x066e, code lost:
        r1 = 1;
        r6 = 0;
     */
    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void didReceivedNotification(int r32, int r33, java.lang.Object... r34) {
        /*
            Method dump skipped, instructions count: 2231
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDownloadAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity, this.resourcesProvider);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        MessageObject messageObject = this.currentMessageObject;
        boolean z = false;
        if (messageObject != null && messageObject.isVideo() && FileLoader.getInstance(this.currentMessageObject.currentAccount).isLoadingFile(this.currentFileNames[0])) {
            z = true;
        }
        if (z) {
            builder.setMessage(LocaleController.getString("PleaseStreamDownload", R.string.PleaseStreamDownload));
        } else {
            builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
        }
        showAlertDialog(builder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSharePressed() {
        boolean z;
        if (this.parentActivity == null || !this.allowShare) {
            return;
        }
        try {
            MessageObject messageObject = this.currentMessageObject;
            boolean z2 = false;
            File file = null;
            if (messageObject != null) {
                z2 = messageObject.isVideo();
                if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
                    File file2 = new File(this.currentMessageObject.messageOwner.attachPath);
                    if (file2.exists()) {
                        file = file2;
                    }
                }
                if (file == null) {
                    file = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.currentMessageObject.messageOwner);
                }
            } else if (this.currentFileLocationVideo != null) {
                FileLoader fileLoader = FileLoader.getInstance(this.currentAccount);
                TLRPC$FileLocation fileLocation = getFileLocation(this.currentFileLocationVideo);
                String fileLocationExt = getFileLocationExt(this.currentFileLocationVideo);
                if (this.avatarsDialogId == 0 && !this.isEvent) {
                    z = false;
                    file = fileLoader.getPathToAttach(fileLocation, fileLocationExt, z);
                }
                z = true;
                file = fileLoader.getPathToAttach(fileLocation, fileLocationExt, z);
            } else {
                PageBlocksAdapter pageBlocksAdapter = this.pageBlocksAdapter;
                if (pageBlocksAdapter != null) {
                    file = pageBlocksAdapter.getFile(this.currentIndex);
                }
            }
            if (file != null && file.exists()) {
                Intent intent = new Intent("android.intent.action.SEND");
                if (z2) {
                    intent.setType("video/mp4");
                } else {
                    MessageObject messageObject2 = this.currentMessageObject;
                    if (messageObject2 != null) {
                        intent.setType(messageObject2.getMimeType());
                    } else {
                        intent.setType("image/jpeg");
                    }
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        Activity activity = this.parentActivity;
                        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(activity, ApplicationLoader.getApplicationId() + ".provider", file));
                        intent.setFlags(1);
                    } catch (Exception unused) {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
                    }
                } else {
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
                }
                this.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                return;
            }
            showDownloadAlert();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScaleToFill() {
        float bitmapWidth = this.centerImage.getBitmapWidth();
        float bitmapHeight = this.centerImage.getBitmapHeight();
        if (bitmapWidth == 0.0f || bitmapHeight == 0.0f) {
            return;
        }
        float containerViewWidth = getContainerViewWidth();
        float containerViewHeight = getContainerViewHeight();
        float min = Math.min(containerViewHeight / bitmapHeight, containerViewWidth / bitmapWidth);
        float max = Math.max(containerViewWidth / ((int) (bitmapWidth * min)), containerViewHeight / ((int) (bitmapHeight * min)));
        this.scale = max;
        updateMinMax(max);
    }

    public void setParentAlert(ChatAttachAlert chatAttachAlert) {
        this.parentAlert = chatAttachAlert;
    }

    public void setParentActivity(Activity activity, Theme.ResourcesProvider resourcesProvider) {
        setParentActivity(activity, null, resourcesProvider);
    }

    public void setParentActivity(BaseFragment baseFragment) {
        setParentActivity(baseFragment, (Theme.ResourcesProvider) null);
    }

    public void setParentActivity(BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        setParentActivity(null, baseFragment, resourcesProvider);
    }

    public void setParentActivity(Activity activity, BaseFragment baseFragment, final Theme.ResourcesProvider resourcesProvider) {
        final Activity parentActivity = activity != null ? activity : baseFragment.getParentActivity();
        Theme.createChatResources(parentActivity, false);
        this.resourcesProvider = resourcesProvider;
        this.parentFragment = baseFragment;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.centerImage.setCurrentAccount(i);
        this.leftImage.setCurrentAccount(this.currentAccount);
        this.rightImage.setCurrentAccount(this.currentAccount);
        PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
        if (photoViewerCaptionEnterView != null) {
            photoViewerCaptionEnterView.currentAccount = UserConfig.selectedAccount;
        }
        if (this.parentActivity == parentActivity || parentActivity == null) {
            updateColors();
            return;
        }
        this.inBubbleMode = parentActivity instanceof BubbleActivity;
        this.parentActivity = parentActivity;
        this.activityContext = new ContextThemeWrapper(this.parentActivity, R.style.Theme_TMessages);
        this.touchSlop = ViewConfiguration.get(this.parentActivity).getScaledTouchSlop();
        if (progressDrawables == null) {
            progressDrawables = new Drawable[]{ContextCompat.getDrawable(this.parentActivity, R.drawable.circle_big), ContextCompat.getDrawable(this.parentActivity, R.drawable.cancel_big), ContextCompat.getDrawable(this.parentActivity, R.drawable.load_big)};
        }
        this.scroller = new Scroller(parentActivity);
        AnonymousClass10 anonymousClass10 = new AnonymousClass10(parentActivity);
        this.windowView = anonymousClass10;
        anonymousClass10.setBackgroundDrawable(this.backgroundDrawable);
        this.windowView.setFocusable(false);
        ClippingImageView clippingImageView = new ClippingImageView(parentActivity);
        this.animatingImageView = clippingImageView;
        clippingImageView.setAnimationValues(this.animationValues);
        this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
        FrameLayoutDrawer frameLayoutDrawer = new FrameLayoutDrawer(parentActivity) { // from class: org.telegram.ui.PhotoViewer.11
            private Bulletin.Delegate delegate = new Bulletin.Delegate() { // from class: org.telegram.ui.PhotoViewer.11.1
                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ int getTopOffset(int i2) {
                    return Bulletin.Delegate.CC.$default$getTopOffset(this, i2);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ void onBottomOffsetChange(float f) {
                    Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ void onHide(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onHide(this, bulletin);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public /* synthetic */ void onShow(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onShow(this, bulletin);
                }

                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public int getBottomOffset(int i2) {
                    return getHeight() - PhotoViewer.this.captionEditText.getTop();
                }
            };

            @Override // org.telegram.ui.PhotoViewer.FrameLayoutDrawer, android.view.ViewGroup, android.view.View
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                Bulletin.addDelegate(this, this.delegate);
            }

            @Override // org.telegram.ui.PhotoViewer.FrameLayoutDrawer, android.view.ViewGroup, android.view.View
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                Bulletin.removeDelegate(this);
            }
        };
        this.containerView = frameLayoutDrawer;
        frameLayoutDrawer.setFocusable(false);
        this.containerView.setClipChildren(true);
        this.containerView.setClipToPadding(true);
        this.windowView.setClipChildren(false);
        this.windowView.setClipToPadding(false);
        this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 21) {
            this.containerView.setFitsSystemWindows(true);
            this.containerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda16
                @Override // android.view.View.OnApplyWindowInsetsListener
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    WindowInsets lambda$setParentActivity$4;
                    lambda$setParentActivity$4 = PhotoViewer.this.lambda$setParentActivity$4(view, windowInsets);
                    return lambda$setParentActivity$4;
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
        if (i2 >= 28) {
            layoutParams.layoutInDisplayCutoutMode = 1;
        }
        if (i2 >= 21) {
            layoutParams.flags = -NUM;
        } else {
            layoutParams.flags = 131072;
        }
        PaintingOverlay paintingOverlay = new PaintingOverlay(this.parentActivity);
        this.paintingOverlay = paintingOverlay;
        this.containerView.addView(paintingOverlay, LayoutHelper.createFrame(-2, -2.0f));
        PaintingOverlay paintingOverlay2 = new PaintingOverlay(this.parentActivity);
        this.leftPaintingOverlay = paintingOverlay2;
        this.containerView.addView(paintingOverlay2, LayoutHelper.createFrame(-2, -2.0f));
        PaintingOverlay paintingOverlay3 = new PaintingOverlay(this.parentActivity);
        this.rightPaintingOverlay = paintingOverlay3;
        this.containerView.addView(paintingOverlay3, LayoutHelper.createFrame(-2, -2.0f));
        ActionBar actionBar = new ActionBar(parentActivity) { // from class: org.telegram.ui.PhotoViewer.12
            @Override // android.view.View
            public void setAlpha(float f) {
                super.setAlpha(f);
                PhotoViewer.this.containerView.invalidate();
            }
        };
        this.actionBar = actionBar;
        actionBar.setOverlayTitleAnimation(true);
        this.actionBar.setTitleColor(-1);
        this.actionBar.setSubtitleColor(-1);
        this.actionBar.setBackgroundColor(NUM);
        this.actionBar.setOccupyStatusBar(isStatusBarVisible());
        this.actionBar.setItemsBackgroundColor(NUM, false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, 1, 1));
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass13(resourcesProvider));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        ActionBarMenuItem addItem = createMenu.addItem(13, R.drawable.msg_mask);
        this.masksItem = addItem;
        addItem.setContentDescription(LocaleController.getString("Masks", R.string.Masks));
        ActionBarMenuItem addItem2 = createMenu.addItem(5, R.drawable.ic_goinline);
        this.pipItem = addItem2;
        addItem2.setContentDescription(LocaleController.getString("AccDescrPipMode", R.string.AccDescrPipMode));
        ActionBarMenuItem addItem3 = createMenu.addItem(3, R.drawable.msg_forward);
        this.sendItem = addItem3;
        addItem3.setContentDescription(LocaleController.getString("Forward", R.string.Forward));
        ActionBarMenuItem addItem4 = createMenu.addItem(18, R.drawable.share);
        this.shareItem = addItem4;
        int i3 = R.string.ShareFile;
        addItem4.setContentDescription(LocaleController.getString("ShareFile", i3));
        ActionBarMenuItem addItem5 = createMenu.addItem(0, R.drawable.ic_ab_other);
        this.menuItem = addItem5;
        addItem5.getPopupLayout().swipeBackGravityRight = true;
        this.chooseSpeedLayout = new ChooseSpeedLayout(this.activityContext, this.menuItem.getPopupLayout().getSwipeBack(), new ChooseSpeedLayout.Callback() { // from class: org.telegram.ui.PhotoViewer.14
            @Override // org.telegram.ui.ChooseSpeedLayout.Callback
            public void onSpeedSelected(float f) {
                PhotoViewer.this.menuItem.toggleSubMenu();
                if (f != PhotoViewer.this.currentVideoSpeed) {
                    PhotoViewer.this.currentVideoSpeed = f;
                    if (PhotoViewer.this.currentMessageObject != null) {
                        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("playback_speed", 0);
                        if (Math.abs(PhotoViewer.this.currentVideoSpeed - 1.0f) < 0.001f) {
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.remove("speed" + PhotoViewer.this.currentMessageObject.getDialogId() + "_" + PhotoViewer.this.currentMessageObject.getId()).commit();
                        } else {
                            SharedPreferences.Editor edit2 = sharedPreferences.edit();
                            edit2.putFloat("speed" + PhotoViewer.this.currentMessageObject.getDialogId() + "_" + PhotoViewer.this.currentMessageObject.getId(), PhotoViewer.this.currentVideoSpeed).commit();
                        }
                    }
                    if (PhotoViewer.this.videoPlayer != null) {
                        PhotoViewer.this.videoPlayer.setPlaybackSpeed(PhotoViewer.this.currentVideoSpeed);
                    }
                    if (PhotoViewer.this.photoViewerWebView != null) {
                        PhotoViewer.this.photoViewerWebView.setPlaybackSpeed(PhotoViewer.this.currentVideoSpeed);
                    }
                    PhotoViewer.this.setMenuItemIcon();
                }
            }
        });
        this.speedItem = this.menuItem.addSwipeBackItem(R.drawable.msg_speed, null, LocaleController.getString("Speed", R.string.Speed), this.chooseSpeedLayout.speedSwipeBackLayout);
        this.menuItem.getPopupLayout().setSwipeBackForegroundColor(-14540254);
        this.speedItem.setSubtext(LocaleController.getString("SpeedNormal", R.string.SpeedNormal));
        this.speedItem.setColors(-328966, -328966);
        ActionBarPopupWindow.GapView addColoredGap = this.menuItem.addColoredGap();
        this.speedGap = addColoredGap;
        addColoredGap.setColor(-15198184);
        this.menuItem.getPopupLayout().setFitItems(true);
        this.menuItem.addSubItem(11, R.drawable.msg_openin, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp)).setColors(-328966, -328966);
        this.menuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        ActionBarMenuSubItem addSubItem = this.menuItem.addSubItem(2, R.drawable.msg_media, LocaleController.getString("ShowAllMedia", R.string.ShowAllMedia));
        this.allMediaItem = addSubItem;
        addSubItem.setColors(-328966, -328966);
        this.menuItem.addSubItem(14, R.drawable.msg_gif, LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs)).setColors(-328966, -328966);
        this.menuItem.addSubItem(4, R.drawable.msg_message, LocaleController.getString("ShowInChat", R.string.ShowInChat)).setColors(-328966, -328966);
        this.menuItem.addSubItem(15, R.drawable.msg_sticker, LocaleController.getString("ShowStickers", R.string.ShowStickers)).setColors(-328966, -328966);
        this.menuItem.addSubItem(10, R.drawable.msg_shareout, LocaleController.getString("ShareFile", i3)).setColors(-328966, -328966);
        this.menuItem.addSubItem(1, R.drawable.msg_gallery, LocaleController.getString("SaveToGallery", R.string.SaveToGallery)).setColors(-328966, -328966);
        this.menuItem.addSubItem(16, R.drawable.msg_openprofile, LocaleController.getString("SetAsMain", R.string.SetAsMain)).setColors(-328966, -328966);
        this.menuItem.addSubItem(6, R.drawable.msg_delete, LocaleController.getString("Delete", R.string.Delete)).setColors(-328966, -328966);
        this.menuItem.addSubItem(7, R.drawable.msg_cancel, LocaleController.getString("StopDownload", R.string.StopDownload)).setColors(-328966, -328966);
        this.menuItem.redrawPopup(-NUM);
        setMenuItemIcon();
        this.menuItem.setPopupItemsSelectorColor(NUM);
        this.menuItem.setSubMenuDelegate(new ActionBarMenuItem.ActionBarSubMenuItemDelegate() { // from class: org.telegram.ui.PhotoViewer.15
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarSubMenuItemDelegate
            public void onShowSubMenu() {
                if (!PhotoViewer.this.videoPlayerControlVisible || !PhotoViewer.this.isPlaying) {
                    return;
                }
                AndroidUtilities.cancelRunOnUIThread(PhotoViewer.this.hideActionBarRunnable);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarSubMenuItemDelegate
            public void onHideSubMenu() {
                if (!PhotoViewer.this.videoPlayerControlVisible || !PhotoViewer.this.isPlaying) {
                    return;
                }
                PhotoViewer.this.scheduleActionBarHide();
            }
        });
        FrameLayout frameLayout = new FrameLayout(this.activityContext) { // from class: org.telegram.ui.PhotoViewer.16
            @Override // android.view.ViewGroup
            protected void measureChildWithMargins(View view, int i4, int i5, int i6, int i7) {
                if (view == PhotoViewer.this.nameTextView || view == PhotoViewer.this.dateTextView) {
                    i5 = PhotoViewer.this.bottomButtonsLayout.getMeasuredWidth();
                }
                super.measureChildWithMargins(view, i4, i5, i6, i7);
            }
        };
        this.bottomLayout = frameLayout;
        frameLayout.setBackgroundColor(NUM);
        this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        View view = new View(this.activityContext);
        this.navigationBar = view;
        view.setBackgroundColor(NUM);
        this.windowView.addView(this.navigationBar, LayoutHelper.createFrame(-1.0f, this.navigationBarHeight / AndroidUtilities.density, 87));
        this.pressedDrawable[0] = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{NUM, 0});
        this.pressedDrawable[0].setShape(0);
        this.pressedDrawable[1] = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[]{NUM, 0});
        this.pressedDrawable[1].setShape(0);
        GroupedPhotosListView groupedPhotosListView = new GroupedPhotosListView(this.activityContext, AndroidUtilities.dp(10.0f));
        this.groupedPhotosListView = groupedPhotosListView;
        this.containerView.addView(groupedPhotosListView, LayoutHelper.createFrame(-1, 68, 83));
        this.groupedPhotosListView.setDelegate(new GroupedPhotosListView.GroupedPhotosListViewDelegate() { // from class: org.telegram.ui.PhotoViewer.17
            @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
            public int getCurrentIndex() {
                return PhotoViewer.this.currentIndex;
            }

            @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
            public int getCurrentAccount() {
                return PhotoViewer.this.currentAccount;
            }

            @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
            public long getAvatarsDialogId() {
                return PhotoViewer.this.avatarsDialogId;
            }

            @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
            public int getSlideshowMessageId() {
                return PhotoViewer.this.slideshowMessageId;
            }

            @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
            public ArrayList<ImageLocation> getImagesArrLocations() {
                return PhotoViewer.this.imagesArrLocations;
            }

            @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
            public ArrayList<MessageObject> getImagesArr() {
                return PhotoViewer.this.imagesArr;
            }

            @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
            public List<TLRPC$PageBlock> getPageBlockArr() {
                if (PhotoViewer.this.pageBlocksAdapter != null) {
                    return PhotoViewer.this.pageBlocksAdapter.getAll();
                }
                return null;
            }

            @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
            public Object getParentObject() {
                if (PhotoViewer.this.pageBlocksAdapter != null) {
                    return PhotoViewer.this.pageBlocksAdapter.getParentObject();
                }
                return null;
            }

            @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
            public void setCurrentIndex(int i4) {
                PhotoViewer.this.currentIndex = -1;
                if (PhotoViewer.this.currentThumb != null) {
                    PhotoViewer.this.currentThumb.release();
                    PhotoViewer.this.currentThumb = null;
                }
                PhotoViewer.this.dontAutoPlay = true;
                PhotoViewer.this.setImageIndex(i4);
                PhotoViewer.this.dontAutoPlay = false;
            }

            @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
            public void onShowAnimationStart() {
                PhotoViewer.this.containerView.requestLayout();
            }

            @Override // org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate
            public void onStopScrolling() {
                PhotoViewer photoViewer = PhotoViewer.this;
                if (photoViewer.shouldMessageObjectAutoPlayed(photoViewer.currentMessageObject)) {
                    PhotoViewer.this.playerAutoStarted = true;
                    PhotoViewer.this.onActionClick(true);
                    PhotoViewer.this.checkProgress(0, false, true);
                }
            }
        });
        for (int i4 = 0; i4 < 3; i4++) {
            this.fullscreenButton[i4] = new ImageView(this.parentActivity);
            this.fullscreenButton[i4].setImageResource(R.drawable.msg_maxvideo);
            this.fullscreenButton[i4].setContentDescription(LocaleController.getString("AccSwitchToFullscreen", R.string.AccSwitchToFullscreen));
            this.fullscreenButton[i4].setScaleType(ImageView.ScaleType.CENTER);
            this.fullscreenButton[i4].setBackground(Theme.createSelectorDrawable(NUM));
            this.fullscreenButton[i4].setVisibility(4);
            this.fullscreenButton[i4].setAlpha(1.0f);
            this.containerView.addView(this.fullscreenButton[i4], LayoutHelper.createFrame(48, 48.0f));
            this.fullscreenButton[i4].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda30
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoViewer.this.lambda$setParentActivity$5(view2);
                }
            });
        }
        CaptionTextViewSwitcher captionTextViewSwitcher = new CaptionTextViewSwitcher(this.containerView.getContext());
        this.captionTextViewSwitcher = captionTextViewSwitcher;
        captionTextViewSwitcher.setFactory(new ViewSwitcher.ViewFactory() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda50
            @Override // android.widget.ViewSwitcher.ViewFactory
            public final View makeView() {
                View lambda$setParentActivity$6;
                lambda$setParentActivity$6 = PhotoViewer.this.lambda$setParentActivity$6();
                return lambda$setParentActivity$6;
            }
        });
        this.captionTextViewSwitcher.setVisibility(4);
        setCaptionHwLayerEnabled(true);
        for (int i5 = 0; i5 < 3; i5++) {
            this.photoProgressViews[i5] = new PhotoProgressView(this.containerView) { // from class: org.telegram.ui.PhotoViewer.18
                @Override // org.telegram.ui.PhotoViewer.PhotoProgressView
                protected void onBackgroundStateUpdated(int i6) {
                    if (this == PhotoViewer.this.photoProgressViews[0]) {
                        PhotoViewer.this.updateAccessibilityOverlayVisibility();
                    }
                }

                @Override // org.telegram.ui.PhotoViewer.PhotoProgressView
                protected void onVisibilityChanged(boolean z) {
                    if (this == PhotoViewer.this.photoProgressViews[0]) {
                        PhotoViewer.this.updateAccessibilityOverlayVisibility();
                    }
                }
            };
            this.photoProgressViews[i5].setBackgroundState(0, false, true);
        }
        RadialProgressView radialProgressView = new RadialProgressView(this.activityContext, resourcesProvider) { // from class: org.telegram.ui.PhotoViewer.19
            @Override // org.telegram.ui.Components.RadialProgressView, android.view.View
            public void setAlpha(float f) {
                super.setAlpha(f);
                if (PhotoViewer.this.containerView != null) {
                    PhotoViewer.this.containerView.invalidate();
                }
            }

            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                if (PhotoViewer.this.containerView != null) {
                    PhotoViewer.this.containerView.invalidate();
                }
            }
        };
        this.miniProgressView = radialProgressView;
        radialProgressView.setUseSelfAlpha(true);
        this.miniProgressView.setProgressColor(-1);
        this.miniProgressView.setSize(AndroidUtilities.dp(54.0f));
        RadialProgressView radialProgressView2 = this.miniProgressView;
        int i6 = R.drawable.circle_big;
        radialProgressView2.setBackgroundResource(i6);
        this.miniProgressView.setVisibility(4);
        this.miniProgressView.setAlpha(0.0f);
        this.containerView.addView(this.miniProgressView, LayoutHelper.createFrame(64, 64, 17));
        LinearLayout linearLayout = new LinearLayout(this.containerView.getContext());
        this.bottomButtonsLayout = linearLayout;
        linearLayout.setOrientation(0);
        this.bottomLayout.addView(this.bottomButtonsLayout, LayoutHelper.createFrame(-2, -1, 53));
        ImageView imageView = new ImageView(this.containerView.getContext());
        this.paintButton = imageView;
        int i7 = R.drawable.msg_photo_draw;
        imageView.setImageResource(i7);
        this.paintButton.setScaleType(ImageView.ScaleType.CENTER);
        this.paintButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.bottomButtonsLayout.addView(this.paintButton, LayoutHelper.createFrame(50, -1.0f));
        this.paintButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda29
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$7(view2);
            }
        });
        ImageView imageView2 = this.paintButton;
        int i8 = R.string.AccDescrPhotoEditor;
        imageView2.setContentDescription(LocaleController.getString("AccDescrPhotoEditor", i8));
        ImageView imageView3 = new ImageView(this.containerView.getContext());
        this.shareButton = imageView3;
        imageView3.setImageResource(R.drawable.share);
        this.shareButton.setScaleType(ImageView.ScaleType.CENTER);
        this.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.bottomButtonsLayout.addView(this.shareButton, LayoutHelper.createFrame(50, -1.0f));
        this.shareButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda32
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$8(view2);
            }
        });
        this.shareButton.setContentDescription(LocaleController.getString("ShareFile", R.string.ShareFile));
        FadingTextViewLayout fadingTextViewLayout = new FadingTextViewLayout(this, this.containerView.getContext()) { // from class: org.telegram.ui.PhotoViewer.20
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.FadingTextViewLayout
            public void onTextViewCreated(TextView textView) {
                super.onTextViewCreated(textView);
                textView.setTextSize(1, 14.0f);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTextColor(-1);
                textView.setGravity(3);
            }
        };
        this.nameTextView = fadingTextViewLayout;
        this.bottomLayout.addView(fadingTextViewLayout, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 5.0f, 8.0f, 0.0f));
        FadingTextViewLayout fadingTextViewLayout2 = new FadingTextViewLayout(this, this.containerView.getContext(), true) { // from class: org.telegram.ui.PhotoViewer.21
            private LocaleController.LocaleInfo lastLocaleInfo = null;
            private int staticCharsCount = 0;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.FadingTextViewLayout
            public void onTextViewCreated(TextView textView) {
                super.onTextViewCreated(textView);
                textView.setTextSize(1, 13.0f);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTextColor(-1);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setGravity(3);
            }

            @Override // org.telegram.ui.Components.FadingTextViewLayout
            protected int getStaticCharsCount() {
                LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
                if (this.lastLocaleInfo != currentLocaleInfo) {
                    this.lastLocaleInfo = currentLocaleInfo;
                    this.staticCharsCount = LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(new Date()), LocaleController.getInstance().formatterDay.format(new Date())).length();
                }
                return this.staticCharsCount;
            }

            @Override // org.telegram.ui.Components.FadingTextViewLayout
            public void setText(CharSequence charSequence, boolean z) {
                int staticCharsCount;
                boolean z2 = false;
                if (z) {
                    if (!LocaleController.isRTL || (staticCharsCount = getStaticCharsCount()) <= 0 || (charSequence.length() == staticCharsCount && getText() != null && getText().length() == staticCharsCount)) {
                        z2 = true;
                    }
                    setText(charSequence, true, z2);
                    return;
                }
                setText(charSequence, false, false);
            }
        };
        this.dateTextView = fadingTextViewLayout2;
        this.bottomLayout.addView(fadingTextViewLayout2, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 25.0f, 8.0f, 0.0f));
        createVideoControlsInterface();
        RadialProgressView radialProgressView3 = new RadialProgressView(this.parentActivity, resourcesProvider);
        this.progressView = radialProgressView3;
        radialProgressView3.setProgressColor(-1);
        this.progressView.setBackgroundResource(i6);
        this.progressView.setVisibility(4);
        this.containerView.addView(this.progressView, LayoutHelper.createFrame(54, 54, 17));
        PickerBottomLayoutViewer pickerBottomLayoutViewer = new PickerBottomLayoutViewer(this.parentActivity);
        this.qualityPicker = pickerBottomLayoutViewer;
        pickerBottomLayoutViewer.setBackgroundColor(NUM);
        this.qualityPicker.updateSelectedCount(0, false);
        this.qualityPicker.setTranslationY(AndroidUtilities.dp(120.0f));
        this.qualityPicker.doneButton.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
        this.qualityPicker.doneButton.setTextColor(getThemedColor("dialogFloatingButton"));
        this.containerView.addView(this.qualityPicker, LayoutHelper.createFrame(-1, 48, 83));
        this.qualityPicker.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda28
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$9(view2);
            }
        });
        this.qualityPicker.doneButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda36
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$10(view2);
            }
        });
        VideoForwardDrawable videoForwardDrawable = new VideoForwardDrawable(false);
        this.videoForwardDrawable = videoForwardDrawable;
        videoForwardDrawable.setDelegate(new VideoForwardDrawable.VideoForwardDrawableDelegate() { // from class: org.telegram.ui.PhotoViewer.22
            @Override // org.telegram.ui.Components.VideoForwardDrawable.VideoForwardDrawableDelegate
            public void onAnimationEnd() {
            }

            @Override // org.telegram.ui.Components.VideoForwardDrawable.VideoForwardDrawableDelegate
            public void invalidate() {
                PhotoViewer.this.containerView.invalidate();
            }
        });
        QualityChooseView qualityChooseView = new QualityChooseView(this.parentActivity);
        this.qualityChooseView = qualityChooseView;
        qualityChooseView.setTranslationY(AndroidUtilities.dp(120.0f));
        this.qualityChooseView.setVisibility(4);
        this.qualityChooseView.setBackgroundColor(NUM);
        this.containerView.addView(this.qualityChooseView, LayoutHelper.createFrame(-1, 70.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        FrameLayout frameLayout2 = new FrameLayout(this.activityContext) { // from class: org.telegram.ui.PhotoViewer.23
            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                return PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(motionEvent);
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(motionEvent);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
            }

            @Override // android.view.View
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                if (PhotoViewer.this.videoTimelineView != null && PhotoViewer.this.videoTimelineView.getVisibility() != 8) {
                    PhotoViewer.this.videoTimelineView.setTranslationY(f);
                    PhotoViewer.this.videoAvatarTooltip.setTranslationY(f);
                }
                if (PhotoViewer.this.videoAvatarTooltip == null || PhotoViewer.this.videoAvatarTooltip.getVisibility() == 8) {
                    return;
                }
                PhotoViewer.this.videoAvatarTooltip.setTranslationY(f);
            }

            @Override // android.view.View
            public void setAlpha(float f) {
                super.setAlpha(f);
                if (PhotoViewer.this.videoTimelineView == null || PhotoViewer.this.videoTimelineView.getVisibility() == 8) {
                    return;
                }
                PhotoViewer.this.videoTimelineView.setAlpha(f);
            }

            @Override // android.view.View
            public void setVisibility(int i9) {
                super.setVisibility(i9);
                if (PhotoViewer.this.videoTimelineView == null || PhotoViewer.this.videoTimelineView.getVisibility() == 8) {
                    return;
                }
                PhotoViewer.this.videoTimelineView.setVisibility(i9 == 0 ? 0 : 4);
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean z, int i9, int i10, int i11, int i12) {
                super.onLayout(z, i9, i10, i11, i12);
                if (PhotoViewer.this.itemsLayout.getVisibility() != 8) {
                    int dp = (((i11 - i9) - AndroidUtilities.dp(70.0f)) - PhotoViewer.this.itemsLayout.getMeasuredWidth()) / 2;
                    PhotoViewer.this.itemsLayout.layout(dp, PhotoViewer.this.itemsLayout.getTop(), PhotoViewer.this.itemsLayout.getMeasuredWidth() + dp, PhotoViewer.this.itemsLayout.getTop() + PhotoViewer.this.itemsLayout.getMeasuredHeight());
                }
            }
        };
        this.pickerView = frameLayout2;
        frameLayout2.setBackgroundColor(NUM);
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
        VideoTimelinePlayView videoTimelinePlayView = new VideoTimelinePlayView(this.parentActivity) { // from class: org.telegram.ui.PhotoViewer.24
            @Override // android.view.View
            public void setTranslationY(float f) {
                if (getTranslationY() != f) {
                    super.setTranslationY(f);
                    PhotoViewer.this.containerView.invalidate();
                }
            }
        };
        this.videoTimelineView = videoTimelinePlayView;
        videoTimelinePlayView.setDelegate(new AnonymousClass25());
        showVideoTimeline(false, false);
        this.videoTimelineView.setBackgroundColor(NUM);
        this.containerView.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, 58.0f, 83, 0.0f, 8.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(this.parentActivity);
        this.videoAvatarTooltip = textView3;
        textView3.setSingleLine(true);
        this.videoAvatarTooltip.setVisibility(8);
        this.videoAvatarTooltip.setText(LocaleController.getString("ChooseCover", R.string.ChooseCover));
        this.videoAvatarTooltip.setGravity(1);
        this.videoAvatarTooltip.setTextSize(1, 14.0f);
        this.videoAvatarTooltip.setTextColor(-7566196);
        this.containerView.addView(this.videoAvatarTooltip, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 8.0f, 0.0f, 0.0f));
        ImageView imageView4 = new ImageView(this.parentActivity) { // from class: org.telegram.ui.PhotoViewer.26
            @Override // android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                return PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(motionEvent);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
            }

            @Override // android.widget.ImageView, android.view.View
            public void setVisibility(int i9) {
                super.setVisibility(i9);
                if (PhotoViewer.this.captionEditText.getCaptionLimitOffset() < 0) {
                    PhotoViewer.this.captionLimitView.setVisibility(i9);
                } else {
                    PhotoViewer.this.captionLimitView.setVisibility(8);
                }
            }

            @Override // android.view.View
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                PhotoViewer.this.captionLimitView.setTranslationY(f);
            }

            @Override // android.view.View
            public void setAlpha(float f) {
                super.setAlpha(f);
                PhotoViewer.this.captionLimitView.setAlpha(f);
            }
        };
        this.pickerViewSendButton = imageView4;
        imageView4.setScaleType(ImageView.ScaleType.CENTER);
        int dp = AndroidUtilities.dp(56.0f);
        int themedColor = getThemedColor("dialogFloatingButton");
        int i9 = Build.VERSION.SDK_INT;
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(dp, themedColor, getThemedColor(i9 >= 21 ? "dialogFloatingButtonPressed" : "dialogFloatingButton"));
        this.pickerViewSendDrawable = createSimpleSelectorCircleDrawable;
        this.pickerViewSendButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        this.pickerViewSendButton.setImageResource(R.drawable.attach_send);
        this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
        this.containerView.addView(this.pickerViewSendButton, LayoutHelper.createFrame(56, 56.0f, 85, 0.0f, 0.0f, 14.0f, 14.0f));
        this.pickerViewSendButton.setContentDescription(LocaleController.getString("Send", R.string.Send));
        this.pickerViewSendButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda34
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$11(view2);
            }
        });
        this.pickerViewSendButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda46
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view2) {
                boolean lambda$setParentActivity$15;
                lambda$setParentActivity$15 = PhotoViewer.this.lambda$setParentActivity$15(resourcesProvider, view2);
                return lambda$setParentActivity$15;
            }
        });
        TextView textView4 = new TextView(this.parentActivity);
        this.captionLimitView = textView4;
        textView4.setTextSize(1, 15.0f);
        this.captionLimitView.setTextColor(-1280137);
        this.captionLimitView.setGravity(17);
        this.captionLimitView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.containerView.addView(this.captionLimitView, LayoutHelper.createFrame(56, 20.0f, 85, 3.0f, 0.0f, 14.0f, 78.0f));
        LinearLayout linearLayout2 = new LinearLayout(this.parentActivity) { // from class: org.telegram.ui.PhotoViewer.27
            @Override // android.widget.LinearLayout, android.view.View
            protected void onMeasure(int i10, int i11) {
                int childCount = getChildCount();
                int i12 = 0;
                for (int i13 = 0; i13 < childCount; i13++) {
                    if (getChildAt(i13).getVisibility() == 0) {
                        i12++;
                    }
                }
                int size = View.MeasureSpec.getSize(i10);
                int size2 = View.MeasureSpec.getSize(i11);
                if (i12 != 0) {
                    int min = Math.min(AndroidUtilities.dp(70.0f), size / i12);
                    if (PhotoViewer.this.compressItem.getVisibility() == 0) {
                        int max = Math.max(0, (min - AndroidUtilities.dp(PhotoViewer.this.selectedCompression < 2 ? 48 : 64)) / 2);
                        PhotoViewer.this.compressItem.setPadding(max, 0, max, 0);
                    }
                    for (int i14 = 0; i14 < childCount; i14++) {
                        View childAt = getChildAt(i14);
                        if (childAt.getVisibility() != 8) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(min, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
                        }
                    }
                    setMeasuredDimension(min * i12, size2);
                    return;
                }
                setMeasuredDimension(size, size2);
            }
        };
        this.itemsLayout = linearLayout2;
        linearLayout2.setOrientation(0);
        this.pickerView.addView(this.itemsLayout, LayoutHelper.createFrame(-2, 48.0f, 81, 0.0f, 0.0f, 70.0f, 0.0f));
        ImageView imageView5 = new ImageView(this.parentActivity);
        this.cropItem = imageView5;
        imageView5.setScaleType(ImageView.ScaleType.CENTER);
        this.cropItem.setImageResource(R.drawable.msg_photo_crop);
        this.cropItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.itemsLayout.addView(this.cropItem, LayoutHelper.createLinear(48, 48));
        this.cropItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda40
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$16(view2);
            }
        });
        this.cropItem.setContentDescription(LocaleController.getString("CropImage", R.string.CropImage));
        ImageView imageView6 = new ImageView(this.parentActivity);
        this.rotateItem = imageView6;
        imageView6.setScaleType(ImageView.ScaleType.CENTER);
        this.rotateItem.setImageResource(R.drawable.msg_photo_rotate);
        this.rotateItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.itemsLayout.addView(this.rotateItem, LayoutHelper.createLinear(48, 48));
        this.rotateItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda25
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$17(view2);
            }
        });
        this.rotateItem.setContentDescription(LocaleController.getString("AccDescrRotate", R.string.AccDescrRotate));
        ImageView imageView7 = new ImageView(this.parentActivity);
        this.mirrorItem = imageView7;
        imageView7.setScaleType(ImageView.ScaleType.CENTER);
        this.mirrorItem.setImageResource(R.drawable.msg_photo_flip);
        this.mirrorItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.itemsLayout.addView(this.mirrorItem, LayoutHelper.createLinear(48, 48));
        this.mirrorItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda21
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$18(view2);
            }
        });
        this.mirrorItem.setContentDescription(LocaleController.getString("AccDescrMirror", R.string.AccDescrMirror));
        ImageView imageView8 = new ImageView(this.parentActivity);
        this.paintItem = imageView8;
        imageView8.setScaleType(ImageView.ScaleType.CENTER);
        this.paintItem.setImageResource(i7);
        this.paintItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.itemsLayout.addView(this.paintItem, LayoutHelper.createLinear(48, 48));
        this.paintItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda33
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$19(view2);
            }
        });
        this.paintItem.setContentDescription(LocaleController.getString("AccDescrPhotoEditor", i8));
        ImageView imageView9 = new ImageView(this.parentActivity);
        this.muteItem = imageView9;
        imageView9.setScaleType(ImageView.ScaleType.CENTER);
        this.muteItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.containerView.addView(this.muteItem, LayoutHelper.createFrame(48, 48.0f, 83, 16.0f, 0.0f, 0.0f, 0.0f));
        this.muteItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda26
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$20(view2);
            }
        });
        ImageView imageView10 = new ImageView(this.parentActivity);
        this.cameraItem = imageView10;
        imageView10.setScaleType(ImageView.ScaleType.CENTER);
        this.cameraItem.setImageResource(R.drawable.photo_add);
        this.cameraItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.cameraItem.setContentDescription(LocaleController.getString("AccDescrTakeMorePics", R.string.AccDescrTakeMorePics));
        this.containerView.addView(this.cameraItem, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 16.0f, 0.0f));
        this.cameraItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda35
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$21(view2);
            }
        });
        ImageView imageView11 = new ImageView(this.parentActivity);
        this.tuneItem = imageView11;
        imageView11.setScaleType(ImageView.ScaleType.CENTER);
        this.tuneItem.setImageResource(R.drawable.msg_photo_settings);
        this.tuneItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.itemsLayout.addView(this.tuneItem, LayoutHelper.createLinear(48, 48));
        this.tuneItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda31
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$22(view2);
            }
        });
        this.tuneItem.setContentDescription(LocaleController.getString("AccDescrPhotoAdjust", R.string.AccDescrPhotoAdjust));
        ImageView imageView12 = new ImageView(this.parentActivity);
        this.compressItem = imageView12;
        imageView12.setTag(1);
        this.compressItem.setScaleType(ImageView.ScaleType.CENTER);
        this.compressItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        int selectCompression = selectCompression();
        this.selectedCompression = selectCompression;
        if (selectCompression <= 1) {
            this.compressItem.setImageResource(R.drawable.video_quality1);
        } else if (selectCompression == 2) {
            this.compressItem.setImageResource(R.drawable.video_quality2);
        } else {
            this.selectedCompression = this.compressionsCount - 1;
            this.compressItem.setImageResource(R.drawable.video_quality3);
        }
        this.compressItem.setContentDescription(LocaleController.getString("AccDescrVideoQuality", R.string.AccDescrVideoQuality));
        this.itemsLayout.addView(this.compressItem, LayoutHelper.createLinear(48, 48));
        this.compressItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda43
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$23(parentActivity, view2);
            }
        });
        ImageView imageView13 = new ImageView(this.parentActivity);
        this.timeItem = imageView13;
        imageView13.setScaleType(ImageView.ScaleType.CENTER);
        this.timeItem.setImageResource(R.drawable.msg_autodelete);
        this.timeItem.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.timeItem.setContentDescription(LocaleController.getString("SetTimer", R.string.SetTimer));
        this.itemsLayout.addView(this.timeItem, LayoutHelper.createLinear(48, 48));
        this.timeItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda44
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$29(resourcesProvider, view2);
            }
        });
        PickerBottomLayoutViewer pickerBottomLayoutViewer2 = new PickerBottomLayoutViewer(this.activityContext);
        this.editorDoneLayout = pickerBottomLayoutViewer2;
        pickerBottomLayoutViewer2.setBackgroundColor(-NUM);
        this.editorDoneLayout.updateSelectedCount(0, false);
        this.editorDoneLayout.setVisibility(8);
        this.containerView.addView(this.editorDoneLayout, LayoutHelper.createFrame(-1, 48, 83));
        this.editorDoneLayout.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda19
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$31(view2);
            }
        });
        this.editorDoneLayout.doneButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda20
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$32(view2);
            }
        });
        TextView textView5 = new TextView(this.activityContext);
        this.resetButton = textView5;
        textView5.setClickable(false);
        this.resetButton.setVisibility(8);
        this.resetButton.setTextSize(1, 14.0f);
        this.resetButton.setTextColor(-1);
        this.resetButton.setGravity(17);
        this.resetButton.setBackgroundDrawable(Theme.createSelectorDrawable(-12763843, 0));
        this.resetButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.resetButton.setText(LocaleController.getString("Reset", R.string.CropReset).toUpperCase());
        this.resetButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.editorDoneLayout.addView(this.resetButton, LayoutHelper.createFrame(-2, -1, 49));
        this.resetButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda18
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$34(view2);
            }
        });
        GestureDetector2 gestureDetector2 = new GestureDetector2(this.containerView.getContext(), this);
        this.gestureDetector = gestureDetector2;
        gestureDetector2.setIsLongpressEnabled(false);
        setDoubleTapEnabled(true);
        ImageReceiver.ImageReceiverDelegate imageReceiverDelegate = new ImageReceiver.ImageReceiverDelegate() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda77
            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                PhotoViewer.this.lambda$setParentActivity$35(imageReceiver, z, z2, z3);
            }

            @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
            }
        };
        this.centerImage.setParentView(this.containerView);
        this.centerImage.setCrossfadeAlpha((byte) 2);
        this.centerImage.setInvalidateAll(true);
        this.centerImage.setDelegate(imageReceiverDelegate);
        this.leftImage.setParentView(this.containerView);
        this.leftImage.setCrossfadeAlpha((byte) 2);
        this.leftImage.setInvalidateAll(true);
        this.leftImage.setDelegate(imageReceiverDelegate);
        this.rightImage.setParentView(this.containerView);
        this.rightImage.setCrossfadeAlpha((byte) 2);
        this.rightImage.setInvalidateAll(true);
        this.rightImage.setDelegate(imageReceiverDelegate);
        int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        CheckBox checkBox = new CheckBox(this.containerView.getContext(), R.drawable.selectphoto_large) { // from class: org.telegram.ui.PhotoViewer.29
            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
            }
        };
        this.checkImageView = checkBox;
        checkBox.setDrawBackground(true);
        this.checkImageView.setHasBorder(true);
        this.checkImageView.setSize(34);
        this.checkImageView.setCheckOffset(AndroidUtilities.dp(1.0f));
        this.checkImageView.setColor(getThemedColor("dialogFloatingButton"), -1);
        this.checkImageView.setVisibility(8);
        this.containerView.addView(this.checkImageView, LayoutHelper.createFrame(34, 34.0f, 53, 0.0f, (rotation == 3 || rotation == 1) ? 61.0f : 71.0f, 11.0f, 0.0f));
        if (isStatusBarVisible()) {
            ((FrameLayout.LayoutParams) this.checkImageView.getLayoutParams()).topMargin += AndroidUtilities.statusBarHeight;
        }
        this.checkImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda37
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$36(view2);
            }
        });
        CounterView counterView = new CounterView(this.parentActivity);
        this.photosCounterView = counterView;
        this.containerView.addView(counterView, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, (rotation == 3 || rotation == 1) ? 58.0f : 68.0f, 64.0f, 0.0f));
        if (isStatusBarVisible()) {
            ((FrameLayout.LayoutParams) this.photosCounterView.getLayoutParams()).topMargin += AndroidUtilities.statusBarHeight;
        }
        this.photosCounterView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda27
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$37(view2);
            }
        });
        SelectedPhotosListView selectedPhotosListView = new SelectedPhotosListView(this.parentActivity);
        this.selectedPhotosListView = selectedPhotosListView;
        selectedPhotosListView.setVisibility(8);
        this.selectedPhotosListView.setAlpha(0.0f);
        this.selectedPhotosListView.setLayoutManager(new LinearLayoutManager(this, this.parentActivity, 0, true) { // from class: org.telegram.ui.PhotoViewer.30
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i10) {
                LinearSmoothScrollerEnd linearSmoothScrollerEnd = new LinearSmoothScrollerEnd(this, recyclerView.getContext()) { // from class: org.telegram.ui.PhotoViewer.30.1
                    @Override // androidx.recyclerview.widget.LinearSmoothScrollerEnd
                    protected int calculateTimeForDeceleration(int i11) {
                        return Math.max(180, super.calculateTimeForDeceleration(i11));
                    }
                };
                linearSmoothScrollerEnd.setTargetPosition(i10);
                startSmoothScroll(linearSmoothScrollerEnd);
            }
        });
        SelectedPhotosListView selectedPhotosListView2 = this.selectedPhotosListView;
        ListAdapter listAdapter = new ListAdapter(this.parentActivity);
        this.selectedPhotosAdapter = listAdapter;
        selectedPhotosListView2.setAdapter(listAdapter);
        this.containerView.addView(this.selectedPhotosListView, LayoutHelper.createFrame(-1, 103, 51));
        this.selectedPhotosListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda81
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view2, int i10) {
                PhotoViewer.this.lambda$setParentActivity$38(view2, i10);
            }
        });
        PhotoViewerCaptionEnterView photoViewerCaptionEnterView2 = new PhotoViewerCaptionEnterView(this, this.activityContext, this.containerView, this.windowView, resourcesProvider) { // from class: org.telegram.ui.PhotoViewer.31
            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                try {
                    if (PhotoViewer.this.bottomTouchEnabled) {
                        return false;
                    }
                    return super.dispatchTouchEvent(motionEvent);
                } catch (Exception e) {
                    FileLog.e(e);
                    return false;
                }
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                try {
                    if (PhotoViewer.this.bottomTouchEnabled) {
                        return false;
                    }
                    return super.onInterceptTouchEvent(motionEvent);
                } catch (Exception e) {
                    FileLog.e(e);
                    return false;
                }
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (PhotoViewer.this.bottomTouchEnabled && motionEvent.getAction() == 0) {
                    PhotoViewer.this.keyboardAnimationEnabled = true;
                }
                return !PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
            }

            @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView
            protected void extendActionMode(ActionMode actionMode, Menu menu) {
                if (PhotoViewer.this.parentChatActivity != null) {
                    PhotoViewer.this.parentChatActivity.extendActionMode(menu);
                }
            }
        };
        this.captionEditText = photoViewerCaptionEnterView2;
        photoViewerCaptionEnterView2.setDelegate(new PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate() { // from class: org.telegram.ui.PhotoViewer.32
            @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate
            public void onCaptionEnter() {
                PhotoViewer.this.closeCaptionEnter(true);
            }

            @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate
            public void onTextChanged(CharSequence charSequence) {
                if (PhotoViewer.this.mentionsAdapter != null && PhotoViewer.this.captionEditText != null && PhotoViewer.this.parentChatActivity != null && charSequence != null) {
                    PhotoViewer.this.mentionsAdapter.searchUsernameOrHashtag(charSequence.toString(), PhotoViewer.this.captionEditText.getCursorPosition(), PhotoViewer.this.parentChatActivity.messages, false, false);
                }
                int themedColor2 = PhotoViewer.this.getThemedColor("dialogFloatingIcon");
                if (PhotoViewer.this.captionEditText.getCaptionLimitOffset() < 0) {
                    PhotoViewer.this.captionLimitView.setText(Integer.toString(PhotoViewer.this.captionEditText.getCaptionLimitOffset()));
                    PhotoViewer.this.captionLimitView.setVisibility(PhotoViewer.this.pickerViewSendButton.getVisibility());
                    PhotoViewer.this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(ColorUtils.setAlphaComponent(themedColor2, (int) (Color.alpha(themedColor2) * 0.58f)), PorterDuff.Mode.MULTIPLY));
                } else {
                    PhotoViewer.this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(themedColor2, PorterDuff.Mode.MULTIPLY));
                    PhotoViewer.this.captionLimitView.setVisibility(8);
                }
                if (PhotoViewer.this.placeProvider != null) {
                    PhotoViewer.this.placeProvider.onCaptionChanged(charSequence);
                }
            }

            @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate
            public void onWindowSizeChanged(int i10) {
                if (i10 - (ActionBar.getCurrentActionBarHeight() * 2) < AndroidUtilities.dp((Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36) + (PhotoViewer.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0))) {
                    PhotoViewer.this.allowMentions = false;
                    if (PhotoViewer.this.mentionListView == null || PhotoViewer.this.mentionListView.getVisibility() != 0) {
                        return;
                    }
                    PhotoViewer.this.mentionListView.setVisibility(4);
                    return;
                }
                PhotoViewer.this.allowMentions = true;
                if (PhotoViewer.this.mentionListView == null || PhotoViewer.this.mentionListView.getVisibility() != 4) {
                    return;
                }
                PhotoViewer.this.mentionListView.setVisibility(0);
            }

            @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate
            public void onEmojiViewOpen() {
                PhotoViewer.this.navigationBar.setVisibility(4);
                PhotoViewer photoViewer = PhotoViewer.this;
                photoViewer.animateNavBarColorTo(Theme.getColor("chat_emojiPanelBackground", photoViewer.captionEditText.getResourcesProvider()), false);
            }

            @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate
            public void onEmojiViewCloseStart() {
                int i10;
                String str;
                PhotoViewer.this.navigationBar.setVisibility(PhotoViewer.this.currentEditMode != 2 ? 0 : 4);
                PhotoViewer.this.animateNavBarColorTo(-16777216);
                setOffset(PhotoViewer.this.captionEditText.getEmojiPadding());
                if (PhotoViewer.this.captionEditText.getTag() != null) {
                    if (PhotoViewer.this.isCurrentVideo) {
                        ActionBar actionBar2 = PhotoViewer.this.actionBar;
                        if (PhotoViewer.this.muteVideo) {
                            i10 = R.string.GifCaption;
                            str = "GifCaption";
                        } else {
                            i10 = R.string.VideoCaption;
                            str = "VideoCaption";
                        }
                        actionBar2.setTitleAnimated(LocaleController.getString(str, i10), true, 220L);
                    } else {
                        PhotoViewer.this.actionBar.setTitleAnimated(LocaleController.getString("PhotoCaption", R.string.PhotoCaption), true, 220L);
                    }
                    PhotoViewer.this.checkImageView.animate().alpha(0.0f).setDuration(220L).start();
                    PhotoViewer.this.photosCounterView.animate().alpha(0.0f).setDuration(220L).start();
                    PhotoViewer.this.selectedPhotosListView.animate().alpha(0.0f).translationY(-AndroidUtilities.dp(10.0f)).setDuration(220L).start();
                    return;
                }
                PhotoViewer.this.checkImageView.animate().alpha(1.0f).setDuration(220L).start();
                PhotoViewer.this.photosCounterView.animate().alpha(1.0f).setDuration(220L).start();
                if (PhotoViewer.this.lastTitle == null) {
                    return;
                }
                PhotoViewer.this.actionBar.setTitleAnimated(PhotoViewer.this.lastTitle, false, 220L);
                PhotoViewer.this.lastTitle = null;
            }

            @Override // org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate
            public void onEmojiViewCloseEnd() {
                setOffset(0);
                PhotoViewer.this.captionEditText.setVisibility(8);
            }

            private void setOffset(int i10) {
                for (int i11 = 0; i11 < PhotoViewer.this.containerView.getChildCount(); i11++) {
                    View childAt = PhotoViewer.this.containerView.getChildAt(i11);
                    if (childAt == PhotoViewer.this.cameraItem || childAt == PhotoViewer.this.muteItem || childAt == PhotoViewer.this.pickerView || childAt == PhotoViewer.this.videoTimelineView || childAt == PhotoViewer.this.pickerViewSendButton || childAt == PhotoViewer.this.captionTextViewSwitcher || (PhotoViewer.this.muteItem.getVisibility() == 0 && childAt == PhotoViewer.this.bottomLayout)) {
                        childAt.setTranslationY(i10);
                    }
                }
            }
        });
        if (i9 >= 19) {
            this.captionEditText.setImportantForAccessibility(4);
        }
        this.captionEditText.setVisibility(8);
        this.containerView.addView(this.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
        AnonymousClass33 anonymousClass33 = new AnonymousClass33(this.activityContext, resourcesProvider);
        this.mentionListView = anonymousClass33;
        anonymousClass33.setTag(5);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, this.activityContext) { // from class: org.telegram.ui.PhotoViewer.34
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.mentionLayoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(1);
        this.mentionListView.setLayoutManager(this.mentionLayoutManager);
        this.mentionListView.setVisibility(8);
        this.mentionListView.setClipToPadding(true);
        this.mentionListView.setOverScrollMode(2);
        this.containerView.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
        RecyclerListView recyclerListView = this.mentionListView;
        MentionsAdapter mentionsAdapter = new MentionsAdapter(this.activityContext, true, 0L, 0, new AnonymousClass35(), resourcesProvider);
        this.mentionsAdapter = mentionsAdapter;
        recyclerListView.setAdapter(mentionsAdapter);
        this.mentionListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda82
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view2, int i10) {
                PhotoViewer.this.lambda$setParentActivity$39(view2, i10);
            }
        });
        this.mentionListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda83
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view2, int i10) {
                boolean lambda$setParentActivity$41;
                lambda$setParentActivity$41 = PhotoViewer.this.lambda$setParentActivity$41(resourcesProvider, view2, i10);
                return lambda$setParentActivity$41;
            }
        });
        UndoView undoView = new UndoView(this.activityContext, null, false, resourcesProvider);
        this.hintView = undoView;
        undoView.setAdditionalTranslationY(AndroidUtilities.dp(112.0f));
        this.hintView.setColors(-NUM, -1);
        this.containerView.addView(this.hintView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        if (!AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
            return;
        }
        View view2 = new View(this.activityContext);
        this.playButtonAccessibilityOverlay = view2;
        view2.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
        this.playButtonAccessibilityOverlay.setFocusable(true);
        this.containerView.addView(this.playButtonAccessibilityOverlay, LayoutHelper.createFrame(64, 64, 17));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$10  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass10 extends FrameLayout {
        AnonymousClass10(Context context) {
            super(context);
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return PhotoViewer.this.isVisible && super.onInterceptTouchEvent(motionEvent);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return PhotoViewer.this.isVisible && PhotoViewer.this.onTouchEvent(motionEvent);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            keyEvent.getKeyCode();
            if (!PhotoViewer.this.muteVideo && PhotoViewer.this.sendPhotoType != 1 && PhotoViewer.this.isCurrentVideo && PhotoViewer.this.videoPlayer != null && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == 0 && (keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25)) {
                PhotoViewer.this.videoPlayer.setVolume(1.0f);
            }
            return super.dispatchKeyEvent(keyEvent);
        }

        /* JADX WARN: Code restructure failed: missing block: B:15:0x0020, code lost:
            if (r0 != 6) goto L17;
         */
        @Override // android.view.ViewGroup, android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean dispatchTouchEvent(android.view.MotionEvent r3) {
            /*
                r2 = this;
                org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                boolean r0 = org.telegram.ui.PhotoViewer.access$100(r0)
                if (r0 == 0) goto L32
                org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                boolean r0 = org.telegram.ui.PhotoViewer.access$200(r0)
                if (r0 == 0) goto L32
                int r0 = r3.getActionMasked()
                if (r0 == 0) goto L29
                r1 = 1
                if (r0 == r1) goto L23
                r1 = 3
                if (r0 == r1) goto L23
                r1 = 5
                if (r0 == r1) goto L29
                r1 = 6
                if (r0 == r1) goto L23
                goto L32
            L23:
                org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                org.telegram.ui.PhotoViewer.access$12100(r0)
                goto L32
            L29:
                org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.this
                java.lang.Runnable r0 = org.telegram.ui.PhotoViewer.access$12000(r0)
                org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            L32:
                boolean r3 = super.dispatchTouchEvent(r3)
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.AnonymousClass10.dispatchTouchEvent(android.view.MotionEvent):boolean");
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View view, long j) {
            try {
                return super.drawChild(canvas, view, j);
            } catch (Throwable unused) {
                return false;
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            int i3;
            int stableInsetBottom;
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            if (Build.VERSION.SDK_INT >= 21 && PhotoViewer.this.lastInsets != null) {
                WindowInsets windowInsets = (WindowInsets) PhotoViewer.this.lastInsets;
                if (!PhotoViewer.this.inBubbleMode) {
                    if (AndroidUtilities.incorrectDisplaySizeFix) {
                        int i4 = AndroidUtilities.displaySize.y;
                        if (size2 > i4) {
                            size2 = i4;
                        }
                        size2 += AndroidUtilities.statusBarHeight;
                    } else if (windowInsets.getStableInsetBottom() >= 0 && (i3 = AndroidUtilities.statusBarHeight) >= 0 && (stableInsetBottom = (size2 - i3) - windowInsets.getStableInsetBottom()) > 0 && stableInsetBottom < 4096) {
                        AndroidUtilities.displaySize.y = stableInsetBottom;
                    }
                }
                int systemWindowInsetBottom = windowInsets.getSystemWindowInsetBottom();
                if (PhotoViewer.this.captionEditText.isPopupShowing()) {
                    systemWindowInsetBottom -= PhotoViewer.this.containerView.getKeyboardHeight();
                }
                size2 -= systemWindowInsetBottom;
            } else {
                int i5 = AndroidUtilities.displaySize.y;
                if (size2 > i5) {
                    size2 = i5;
                }
            }
            int paddingLeft = size - (getPaddingLeft() + getPaddingRight());
            int paddingBottom = size2 - getPaddingBottom();
            setMeasuredDimension(paddingLeft, paddingBottom);
            ViewGroup.LayoutParams layoutParams = PhotoViewer.this.animatingImageView.getLayoutParams();
            PhotoViewer.this.animatingImageView.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(layoutParams.height, Integer.MIN_VALUE));
            PhotoViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(paddingLeft, NUM), View.MeasureSpec.makeMeasureSpec(paddingBottom, NUM));
            PhotoViewer.this.navigationBar.measure(View.MeasureSpec.makeMeasureSpec(paddingLeft, NUM), View.MeasureSpec.makeMeasureSpec(PhotoViewer.this.navigationBarHeight, NUM));
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            PhotoViewer.this.animatingImageView.layout(getPaddingLeft(), 0, getPaddingLeft() + PhotoViewer.this.animatingImageView.getMeasuredWidth(), PhotoViewer.this.animatingImageView.getMeasuredHeight());
            PhotoViewer.this.containerView.layout(getPaddingLeft(), 0, getPaddingLeft() + PhotoViewer.this.containerView.getMeasuredWidth(), PhotoViewer.this.containerView.getMeasuredHeight());
            PhotoViewer.this.navigationBar.layout(getPaddingLeft(), PhotoViewer.this.containerView.getMeasuredHeight(), PhotoViewer.this.navigationBar.getMeasuredWidth(), PhotoViewer.this.containerView.getMeasuredHeight() + PhotoViewer.this.navigationBar.getMeasuredHeight());
            PhotoViewer.this.wasLayout = true;
            if (z) {
                if (!PhotoViewer.this.dontResetZoomOnFirstLayout) {
                    PhotoViewer.this.scale = 1.0f;
                    PhotoViewer.this.translationX = 0.0f;
                    PhotoViewer.this.translationY = 0.0f;
                    PhotoViewer photoViewer = PhotoViewer.this;
                    photoViewer.updateMinMax(photoViewer.scale);
                }
                if (PhotoViewer.this.checkImageView != null) {
                    PhotoViewer.this.checkImageView.post(new Runnable() { // from class: org.telegram.ui.PhotoViewer$10$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.AnonymousClass10.this.lambda$onLayout$0();
                        }
                    });
                }
            }
            if (PhotoViewer.this.dontResetZoomOnFirstLayout) {
                PhotoViewer.this.setScaleToFill();
                PhotoViewer.this.dontResetZoomOnFirstLayout = false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLayout$0() {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.checkImageView.getLayoutParams();
            ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            int i = 0;
            int currentActionBarHeight = ((ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(34.0f)) / 2) + (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0);
            if (currentActionBarHeight != layoutParams.topMargin) {
                layoutParams.topMargin = currentActionBarHeight;
                PhotoViewer.this.checkImageView.setLayoutParams(layoutParams);
            }
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) PhotoViewer.this.photosCounterView.getLayoutParams();
            int currentActionBarHeight2 = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(40.0f)) / 2;
            if (PhotoViewer.this.isStatusBarVisible()) {
                i = AndroidUtilities.statusBarHeight;
            }
            int i2 = currentActionBarHeight2 + i;
            if (layoutParams2.topMargin != i2) {
                layoutParams2.topMargin = i2;
                PhotoViewer.this.photosCounterView.setLayoutParams(layoutParams2);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            PhotoViewer.this.centerImage.onAttachedToWindow();
            PhotoViewer.this.leftImage.onAttachedToWindow();
            PhotoViewer.this.rightImage.onAttachedToWindow();
            PhotoViewer.this.attachedToWindow = true;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            PhotoViewer.this.centerImage.onDetachedFromWindow();
            PhotoViewer.this.leftImage.onDetachedFromWindow();
            PhotoViewer.this.rightImage.onDetachedFromWindow();
            PhotoViewer.this.attachedToWindow = false;
            PhotoViewer.this.wasLayout = false;
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
            if (keyEvent != null && keyEvent.getKeyCode() == 4 && keyEvent.getAction() == 1) {
                if (PhotoViewer.this.captionEditText.isPopupShowing() || PhotoViewer.this.captionEditText.isKeyboardVisible()) {
                    PhotoViewer.this.closeCaptionEnter(true);
                    return false;
                }
                PhotoViewer.getInstance().closePhoto(true, false);
                return true;
            }
            return super.dispatchKeyEventPreIme(keyEvent);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (Build.VERSION.SDK_INT < 21 || !PhotoViewer.this.isVisible || PhotoViewer.this.lastInsets == null) {
                return;
            }
            PhotoViewer.this.blackPaint.setAlpha(PhotoViewer.this.backgroundDrawable.getAlpha());
            canvas.drawRect(0.0f, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight() + ((WindowInsets) PhotoViewer.this.lastInsets).getSystemWindowInsetBottom(), PhotoViewer.this.blackPaint);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (PhotoViewer.this.parentChatActivity != null) {
                UndoView undoView = PhotoViewer.this.parentChatActivity.getUndoView();
                if (undoView.getVisibility() != 0) {
                    return;
                }
                canvas.save();
                View view = (View) undoView.getParent();
                canvas.clipRect(view.getX(), view.getY(), view.getX() + view.getWidth(), view.getY() + view.getHeight());
                canvas.translate(undoView.getX(), undoView.getY());
                undoView.draw(canvas);
                canvas.restore();
                invalidate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ WindowInsets lambda$setParentActivity$4(View view, WindowInsets windowInsets) {
        int systemWindowInsetTop = windowInsets.getSystemWindowInsetTop();
        Activity activity = this.parentActivity;
        if ((activity instanceof LaunchActivity) && ((systemWindowInsetTop != 0 || AndroidUtilities.isInMultiwindow) && !this.inBubbleMode && AndroidUtilities.statusBarHeight != systemWindowInsetTop)) {
            AndroidUtilities.statusBarHeight = systemWindowInsetTop;
            ((LaunchActivity) activity).drawerLayoutContainer.requestLayout();
        }
        WindowInsets windowInsets2 = (WindowInsets) this.lastInsets;
        this.lastInsets = windowInsets;
        if (windowInsets2 == null || !windowInsets2.toString().equals(windowInsets.toString())) {
            int i = this.animationInProgress;
            if (i == 1 || i == 3) {
                ClippingImageView clippingImageView = this.animatingImageView;
                clippingImageView.setTranslationX(clippingImageView.getTranslationX() - getLeftInset());
                this.animationValues[0][2] = this.animatingImageView.getTranslationX();
            }
            FrameLayout frameLayout = this.windowView;
            if (frameLayout != null) {
                frameLayout.requestLayout();
            }
        }
        if (this.navigationBar != null) {
            this.navigationBarHeight = windowInsets.getSystemWindowInsetBottom();
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.navigationBar.getLayoutParams();
            int i2 = this.navigationBarHeight;
            marginLayoutParams.height = i2;
            marginLayoutParams.bottomMargin = (-i2) / 2;
            this.navigationBar.setLayoutParams(marginLayoutParams);
        }
        this.containerView.setPadding(windowInsets.getSystemWindowInsetLeft(), 0, windowInsets.getSystemWindowInsetRight(), 0);
        if (this.actionBar != null) {
            AndroidUtilities.cancelRunOnUIThread(this.updateContainerFlagsRunnable);
            if (this.isVisible && this.animationInProgress == 0) {
                AndroidUtilities.runOnUIThread(this.updateContainerFlagsRunnable, 200L);
            }
        }
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return windowInsets.consumeSystemWindowInsets();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$13  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass13 extends ActionBar.ActionBarMenuOnItemClick {
        final /* synthetic */ Theme.ResourcesProvider val$resourcesProvider;

        AnonymousClass13(Theme.ResourcesProvider resourcesProvider) {
            this.val$resourcesProvider = resourcesProvider;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$0(boolean z) {
            BulletinFactory.createSaveToGalleryBulletin(PhotoViewer.this.containerView, z, -NUM, -1).show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$2(DialogInterface dialogInterface, int i) {
            File pathToMessage;
            if (PhotoViewer.this.currentMessageObject == null) {
                return;
            }
            if (!(MessageObject.getMedia(PhotoViewer.this.currentMessageObject.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) || MessageObject.getMedia(PhotoViewer.this.currentMessageObject.messageOwner).webpage == null || MessageObject.getMedia(PhotoViewer.this.currentMessageObject.messageOwner).webpage.document != null) {
                pathToMessage = FileLoader.getInstance(PhotoViewer.this.currentAccount).getPathToMessage(PhotoViewer.this.currentMessageObject.messageOwner);
            } else {
                PhotoViewer photoViewer = PhotoViewer.this;
                pathToMessage = FileLoader.getInstance(PhotoViewer.this.currentAccount).getPathToAttach(photoViewer.getFileLocation(photoViewer.currentIndex, null), true);
            }
            final boolean isVideo = PhotoViewer.this.currentMessageObject.isVideo();
            if (pathToMessage == null || !pathToMessage.exists()) {
                PhotoViewer.this.showDownloadAlert();
                return;
            }
            MediaController.saveFile(pathToMessage.toString(), PhotoViewer.this.parentActivity, isVideo ? 1 : 0, null, null, new Runnable() { // from class: org.telegram.ui.PhotoViewer$13$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass13.this.lambda$onItemClick$1(isVideo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$1(boolean z) {
            BulletinFactory.createSaveToGalleryBulletin(PhotoViewer.this.containerView, z, -NUM, -1).show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$5(final boolean z, ArrayList arrayList, DialogInterface dialogInterface, int i) {
            File pathToMessage;
            final int[] iArr = new int[1];
            final int[] iArr2 = new int[1];
            final Runnable runnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$13$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass13.this.lambda$onItemClick$3(iArr2, iArr, z);
                }
            };
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i2);
                if (messageObject != null) {
                    if (!(MessageObject.getMedia(messageObject.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) || MessageObject.getMedia(messageObject.messageOwner).webpage == null || MessageObject.getMedia(messageObject.messageOwner).webpage.document != null) {
                        pathToMessage = FileLoader.getInstance(PhotoViewer.this.currentAccount).getPathToMessage(messageObject.messageOwner);
                    } else {
                        FileLoader fileLoader = FileLoader.getInstance(PhotoViewer.this.currentAccount);
                        PhotoViewer photoViewer = PhotoViewer.this;
                        pathToMessage = fileLoader.getPathToAttach(photoViewer.getFileLocation(photoViewer.currentIndex, null), true);
                    }
                    boolean isVideo = messageObject.isVideo();
                    if (pathToMessage != null && pathToMessage.exists()) {
                        iArr[0] = iArr[0] + 1;
                        MediaController.saveFile(pathToMessage.toString(), PhotoViewer.this.parentActivity, isVideo ? 1 : 0, null, null, new Runnable() { // from class: org.telegram.ui.PhotoViewer$13$$ExternalSyntheticLambda8
                            @Override // java.lang.Runnable
                            public final void run() {
                                AndroidUtilities.runOnUIThread(runnable);
                            }
                        });
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$3(int[] iArr, int[] iArr2, boolean z) {
            iArr[0] = iArr[0] + 1;
            if (iArr[0] == iArr2[0]) {
                BulletinFactory.createSaveToGalleryBulletin(PhotoViewer.this.containerView, iArr2[0], z, -NUM, -1).show();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$7(DialogInterface dialogInterface, int i) {
            ArrayList arrayList = new ArrayList(1);
            arrayList.add(PhotoViewer.this.currentMessageObject);
            PhotoViewer.this.showShareAlert(arrayList);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$8(ArrayList arrayList, DialogInterface dialogInterface, int i) {
            PhotoViewer.this.showShareAlert(arrayList);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$10(ArrayList arrayList, ChatActivity chatActivity, DialogsActivity dialogsActivity, ArrayList arrayList2, CharSequence charSequence, boolean z) {
            if (arrayList2.size() > 1 || ((MessagesStorage.TopicKey) arrayList2.get(0)).dialogId == UserConfig.getInstance(PhotoViewer.this.currentAccount).getClientUserId() || charSequence != null) {
                for (int i = 0; i < arrayList2.size(); i++) {
                    long j = ((MessagesStorage.TopicKey) arrayList2.get(i)).dialogId;
                    if (charSequence != null) {
                        SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage(charSequence.toString(), j, null, null, null, true, null, null, null, true, 0, null, false);
                    }
                    SendMessagesHelper.getInstance(PhotoViewer.this.currentAccount).sendMessage((ArrayList<MessageObject>) arrayList, j, false, false, true, 0);
                }
                dialogsActivity.finishFragment();
                if (chatActivity == null) {
                    return;
                }
                if (arrayList2.size() == 1) {
                    chatActivity.getUndoView().showWithAction(((MessagesStorage.TopicKey) arrayList2.get(0)).dialogId, 53, Integer.valueOf(arrayList.size()));
                    return;
                } else {
                    chatActivity.getUndoView().showWithAction(0L, 53, Integer.valueOf(arrayList.size()), Integer.valueOf(arrayList2.size()), (Runnable) null, (Runnable) null);
                    return;
                }
            }
            long j2 = ((MessagesStorage.TopicKey) arrayList2.get(0)).dialogId;
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (DialogObject.isEncryptedDialog(j2)) {
                bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j2));
            } else if (DialogObject.isUserDialog(j2)) {
                bundle.putLong("user_id", j2);
            } else {
                bundle.putLong("chat_id", -j2);
            }
            NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            ChatActivity chatActivity2 = new ChatActivity(bundle);
            if (((LaunchActivity) PhotoViewer.this.parentActivity).presentFragment(chatActivity2, true, false)) {
                chatActivity2.showFieldPanelForForward(true, arrayList);
            } else {
                dialogsActivity.finishFragment();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onItemClick$11(boolean[] zArr, View view) {
            zArr[0] = !zArr[0];
            ((CheckBoxCell) view).setChecked(zArr[0], true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$onItemClick$12(boolean[] zArr, DialogInterface dialogInterface, int i) {
            ArrayList arrayList;
            TLRPC$EncryptedChat tLRPC$EncryptedChat;
            if (!PhotoViewer.this.imagesArr.isEmpty()) {
                if (PhotoViewer.this.currentIndex < 0 || PhotoViewer.this.currentIndex >= PhotoViewer.this.imagesArr.size()) {
                    return;
                }
                MessageObject messageObject = (MessageObject) PhotoViewer.this.imagesArr.get(PhotoViewer.this.currentIndex);
                if (!messageObject.isSent()) {
                    return;
                }
                PhotoViewer.this.closePhoto(false, false);
                ArrayList arrayList2 = new ArrayList();
                if (PhotoViewer.this.slideshowMessageId != 0) {
                    arrayList2.add(Integer.valueOf(PhotoViewer.this.slideshowMessageId));
                } else {
                    arrayList2.add(Integer.valueOf(messageObject.getId()));
                }
                if (!DialogObject.isEncryptedDialog(messageObject.getDialogId()) || messageObject.messageOwner.random_id == 0) {
                    arrayList = null;
                    tLRPC$EncryptedChat = null;
                } else {
                    ArrayList arrayList3 = new ArrayList();
                    arrayList3.add(Long.valueOf(messageObject.messageOwner.random_id));
                    arrayList = arrayList3;
                    tLRPC$EncryptedChat = MessagesController.getInstance(PhotoViewer.this.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(messageObject.getDialogId())));
                }
                MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteMessages(arrayList2, arrayList, tLRPC$EncryptedChat, messageObject.getDialogId(), zArr[0], messageObject.scheduled);
            } else if (!PhotoViewer.this.avatarsArr.isEmpty()) {
                if (PhotoViewer.this.currentIndex < 0 || PhotoViewer.this.currentIndex >= PhotoViewer.this.avatarsArr.size()) {
                    return;
                }
                TLRPC$Message tLRPC$Message = (TLRPC$Message) PhotoViewer.this.imagesArrMessages.get(PhotoViewer.this.currentIndex);
                if (tLRPC$Message != null) {
                    ArrayList<Integer> arrayList4 = new ArrayList<>();
                    arrayList4.add(Integer.valueOf(tLRPC$Message.id));
                    MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteMessages(arrayList4, null, null, MessageObject.getDialogId(tLRPC$Message), true, false);
                    NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.reloadDialogPhotos, new Object[0]);
                }
                if (PhotoViewer.this.isCurrentAvatarSet()) {
                    if (PhotoViewer.this.avatarsDialogId > 0) {
                        MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto(null);
                    } else {
                        MessagesController.getInstance(PhotoViewer.this.currentAccount).changeChatAvatar(-PhotoViewer.this.avatarsDialogId, null, null, null, 0.0d, null, null, null, null);
                    }
                    PhotoViewer.this.closePhoto(false, false);
                    return;
                }
                TLRPC$Photo tLRPC$Photo = (TLRPC$Photo) PhotoViewer.this.avatarsArr.get(PhotoViewer.this.currentIndex);
                if (tLRPC$Photo == null) {
                    return;
                }
                TLRPC$TL_inputPhoto tLRPC$TL_inputPhoto = new TLRPC$TL_inputPhoto();
                tLRPC$TL_inputPhoto.id = tLRPC$Photo.id;
                tLRPC$TL_inputPhoto.access_hash = tLRPC$Photo.access_hash;
                byte[] bArr = tLRPC$Photo.file_reference;
                tLRPC$TL_inputPhoto.file_reference = bArr;
                if (bArr == null) {
                    tLRPC$TL_inputPhoto.file_reference = new byte[0];
                }
                if (PhotoViewer.this.avatarsDialogId > 0) {
                    MessagesController.getInstance(PhotoViewer.this.currentAccount).deleteUserPhoto(tLRPC$TL_inputPhoto);
                }
                MessagesStorage.getInstance(PhotoViewer.this.currentAccount).clearUserPhoto(PhotoViewer.this.avatarsDialogId, tLRPC$Photo.id);
                PhotoViewer.this.imagesArrLocations.remove(PhotoViewer.this.currentIndex);
                PhotoViewer.this.imagesArrLocationsSizes.remove(PhotoViewer.this.currentIndex);
                PhotoViewer.this.imagesArrLocationsVideo.remove(PhotoViewer.this.currentIndex);
                PhotoViewer.this.imagesArrMessages.remove(PhotoViewer.this.currentIndex);
                PhotoViewer.this.avatarsArr.remove(PhotoViewer.this.currentIndex);
                if (!PhotoViewer.this.imagesArrLocations.isEmpty()) {
                    int i2 = PhotoViewer.this.currentIndex;
                    if (i2 >= PhotoViewer.this.avatarsArr.size()) {
                        i2 = PhotoViewer.this.avatarsArr.size() - 1;
                    }
                    PhotoViewer.this.currentIndex = -1;
                    PhotoViewer.this.setImageIndex(i2);
                } else {
                    PhotoViewer.this.closePhoto(false, false);
                }
                if (tLRPC$Message != null) {
                    return;
                }
                NotificationCenter.getInstance(PhotoViewer.this.currentAccount).postNotificationName(NotificationCenter.reloadDialogPhotos, new Object[0]);
            } else if (PhotoViewer.this.secureDocuments.isEmpty() || PhotoViewer.this.placeProvider == null) {
            } else {
                PhotoViewer.this.secureDocuments.remove(PhotoViewer.this.currentIndex);
                PhotoViewer.this.placeProvider.deleteImageAtIndex(PhotoViewer.this.currentIndex);
                if (!PhotoViewer.this.secureDocuments.isEmpty()) {
                    int i3 = PhotoViewer.this.currentIndex;
                    if (i3 >= PhotoViewer.this.secureDocuments.size()) {
                        i3 = PhotoViewer.this.secureDocuments.size() - 1;
                    }
                    PhotoViewer.this.currentIndex = -1;
                    PhotoViewer.this.setImageIndex(i3);
                    return;
                }
                PhotoViewer.this.closePhoto(false, false);
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:143:0x0429  */
        /* JADX WARN: Removed duplicated region for block: B:144:0x0440  */
        /* JADX WARN: Removed duplicated region for block: B:146:0x0444  */
        /* JADX WARN: Removed duplicated region for block: B:147:0x044a  */
        /* JADX WARN: Removed duplicated region for block: B:149:0x0455  */
        /* JADX WARN: Removed duplicated region for block: B:152:0x0463  */
        /* JADX WARN: Removed duplicated region for block: B:155:0x046a  */
        /* JADX WARN: Removed duplicated region for block: B:182:0x0556  */
        /* JADX WARN: Removed duplicated region for block: B:201:0x0606  */
        /* JADX WARN: Removed duplicated region for block: B:203:0x0615  */
        /* JADX WARN: Removed duplicated region for block: B:236:0x0703  */
        /* JADX WARN: Removed duplicated region for block: B:282:0x087d  */
        /* JADX WARN: Removed duplicated region for block: B:432:? A[RETURN, SYNTHETIC] */
        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onItemClick(int r33) {
            /*
                Method dump skipped, instructions count: 3577
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.AnonymousClass13.onItemClick(int):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$14(final UserConfig userConfig, final TLRPC$Photo tLRPC$Photo, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$13$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass13.this.lambda$onItemClick$13(tLObject, userConfig, tLRPC$Photo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$13(TLObject tLObject, UserConfig userConfig, TLRPC$Photo tLRPC$Photo) {
            if (tLObject instanceof TLRPC$TL_photos_photo) {
                TLRPC$TL_photos_photo tLRPC$TL_photos_photo = (TLRPC$TL_photos_photo) tLObject;
                MessagesController.getInstance(PhotoViewer.this.currentAccount).putUsers(tLRPC$TL_photos_photo.users, false);
                TLRPC$User user = MessagesController.getInstance(PhotoViewer.this.currentAccount).getUser(Long.valueOf(userConfig.clientUserId));
                if (!(tLRPC$TL_photos_photo.photo instanceof TLRPC$TL_photo)) {
                    return;
                }
                int indexOf = PhotoViewer.this.avatarsArr.indexOf(tLRPC$Photo);
                if (indexOf >= 0) {
                    PhotoViewer.this.avatarsArr.set(indexOf, tLRPC$TL_photos_photo.photo);
                }
                if (user == null) {
                    return;
                }
                user.photo.photo_id = tLRPC$TL_photos_photo.photo.id;
                userConfig.setCurrentUser(user);
                userConfig.saveConfig(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$15() {
            if (PhotoViewer.this.menuItem == null) {
                return;
            }
            PhotoViewer.this.menuItem.hideSubItem(16);
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public boolean canOpenMenu() {
            boolean z = true;
            if (PhotoViewer.this.currentMessageObject == null && PhotoViewer.this.currentSecureDocument == null) {
                if (PhotoViewer.this.currentFileLocationVideo == null) {
                    return PhotoViewer.this.pageBlocksAdapter != null;
                }
                FileLoader fileLoader = FileLoader.getInstance(PhotoViewer.this.currentAccount);
                TLRPC$FileLocation fileLocation = PhotoViewer.getFileLocation(PhotoViewer.this.currentFileLocationVideo);
                String fileLocationExt = PhotoViewer.getFileLocationExt(PhotoViewer.this.currentFileLocationVideo);
                if (PhotoViewer.this.avatarsDialogId == 0 && !PhotoViewer.this.isEvent) {
                    z = false;
                }
                return fileLoader.getPathToAttach(fileLocation, fileLocationExt, z).exists();
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$5(View view) {
        Activity activity = this.parentActivity;
        if (activity == null) {
            return;
        }
        this.wasRotated = false;
        this.fullscreenedByButton = 1;
        if (this.prevOrientation == -10) {
            this.prevOrientation = activity.getRequestedOrientation();
        }
        if (((WindowManager) this.parentActivity.getSystemService("window")).getDefaultDisplay().getRotation() == 3) {
            this.parentActivity.setRequestedOrientation(8);
        } else {
            this.parentActivity.setRequestedOrientation(0);
        }
        toggleActionBar(false, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$7(View view) {
        openCurrentPhotoInPaintModeForSelect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$8(View view) {
        onSharePressed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$9(View view) {
        this.selectedCompression = this.previousCompression;
        didChangedCompressionLevel(false);
        showQualityView(false);
        requestVideoPreview(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$10(View view) {
        showQualityView(false);
        requestVideoPreview(2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$25  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass25 implements VideoTimelinePlayView.VideoTimelineViewDelegate {
        private int seekTo;
        private Runnable seekToRunnable;
        private boolean wasPlaying;

        AnonymousClass25() {
        }

        @Override // org.telegram.ui.Components.VideoTimelinePlayView.VideoTimelineViewDelegate
        public void onLeftProgressChanged(float f) {
            if (PhotoViewer.this.videoPlayer == null) {
                return;
            }
            if (PhotoViewer.this.videoPlayer.isPlaying()) {
                PhotoViewer.this.manuallyPaused = false;
                PhotoViewer.this.videoPlayer.pause();
                PhotoViewer.this.containerView.invalidate();
            }
            updateAvatarStartTime(1);
            seekTo(f);
            PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
            PhotoViewer.this.videoTimelineView.setProgress(f);
            PhotoViewer.this.updateVideoInfo();
        }

        @Override // org.telegram.ui.Components.VideoTimelinePlayView.VideoTimelineViewDelegate
        public void onRightProgressChanged(float f) {
            if (PhotoViewer.this.videoPlayer == null) {
                return;
            }
            if (PhotoViewer.this.videoPlayer.isPlaying()) {
                PhotoViewer.this.manuallyPaused = false;
                PhotoViewer.this.videoPlayer.pause();
                PhotoViewer.this.containerView.invalidate();
            }
            updateAvatarStartTime(2);
            seekTo(f);
            PhotoViewer.this.videoPlayerSeekbar.setProgress(1.0f);
            PhotoViewer.this.videoTimelineView.setProgress(f);
            PhotoViewer.this.updateVideoInfo();
        }

        @Override // org.telegram.ui.Components.VideoTimelinePlayView.VideoTimelineViewDelegate
        public void onPlayProgressChanged(float f) {
            if (PhotoViewer.this.videoPlayer == null) {
                return;
            }
            if (PhotoViewer.this.sendPhotoType == 1) {
                updateAvatarStartTime(0);
            }
            seekTo(f);
        }

        private void seekTo(float f) {
            this.seekTo = (int) (PhotoViewer.this.videoDuration * f);
            if (SharedConfig.getDevicePerformanceClass() == 2) {
                PhotoViewer.this.seekVideoOrWebTo(this.seekTo);
                if (PhotoViewer.this.sendPhotoType == 1) {
                    PhotoViewer.this.needCaptureFrameReadyAtTime = this.seekTo;
                    if (PhotoViewer.this.captureFrameReadyAtTime != PhotoViewer.this.needCaptureFrameReadyAtTime) {
                        PhotoViewer.this.captureFrameReadyAtTime = -1L;
                    }
                }
                this.seekToRunnable = null;
            } else if (this.seekToRunnable != null) {
            } else {
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$25$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass25.this.lambda$seekTo$0();
                    }
                };
                this.seekToRunnable = runnable;
                AndroidUtilities.runOnUIThread(runnable, 100L);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$seekTo$0() {
            PhotoViewer.this.seekVideoOrWebTo(this.seekTo);
            if (PhotoViewer.this.sendPhotoType == 1) {
                PhotoViewer.this.needCaptureFrameReadyAtTime = this.seekTo;
                if (PhotoViewer.this.captureFrameReadyAtTime != PhotoViewer.this.needCaptureFrameReadyAtTime) {
                    PhotoViewer.this.captureFrameReadyAtTime = -1L;
                }
            }
            this.seekToRunnable = null;
        }

        private void updateAvatarStartTime(int i) {
            if (PhotoViewer.this.sendPhotoType != 1) {
                return;
            }
            if (i != 0) {
                if (PhotoViewer.this.photoCropView == null) {
                    return;
                }
                if (PhotoViewer.this.videoTimelineView.getLeftProgress() <= PhotoViewer.this.avatarStartProgress && PhotoViewer.this.videoTimelineView.getRightProgress() >= PhotoViewer.this.avatarStartProgress) {
                    return;
                }
                PhotoViewer.this.photoCropView.setVideoThumbVisible(false);
                if (i == 1) {
                    PhotoViewer photoViewer = PhotoViewer.this;
                    photoViewer.avatarStartTime = photoViewer.videoDuration * 1000.0f * PhotoViewer.this.videoTimelineView.getLeftProgress();
                } else {
                    PhotoViewer photoViewer2 = PhotoViewer.this;
                    photoViewer2.avatarStartTime = photoViewer2.videoDuration * 1000.0f * PhotoViewer.this.videoTimelineView.getRightProgress();
                }
                PhotoViewer.this.captureFrameAtTime = -1L;
                return;
            }
            PhotoViewer photoViewer3 = PhotoViewer.this;
            photoViewer3.avatarStartProgress = photoViewer3.videoTimelineView.getProgress();
            PhotoViewer photoViewer4 = PhotoViewer.this;
            photoViewer4.avatarStartTime = photoViewer4.videoDuration * 1000.0f * PhotoViewer.this.avatarStartProgress;
        }

        @Override // org.telegram.ui.Components.VideoTimelinePlayView.VideoTimelineViewDelegate
        public void didStartDragging(int i) {
            if (i == VideoTimelinePlayView.TYPE_PROGRESS) {
                PhotoViewer.this.cancelVideoPlayRunnable();
                if (PhotoViewer.this.sendPhotoType == 1) {
                    PhotoViewer.this.cancelFlashAnimations();
                    PhotoViewer.this.captureFrameAtTime = -1L;
                }
                boolean isVideoPlaying = PhotoViewer.this.isVideoPlaying();
                this.wasPlaying = isVideoPlaying;
                if (!isVideoPlaying) {
                    return;
                }
                PhotoViewer.this.manuallyPaused = false;
                PhotoViewer.this.pauseVideoOrWeb();
                PhotoViewer.this.containerView.invalidate();
            }
        }

        @Override // org.telegram.ui.Components.VideoTimelinePlayView.VideoTimelineViewDelegate
        public void didStopDragging(int i) {
            Runnable runnable = this.seekToRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.seekToRunnable.run();
            }
            PhotoViewer.this.cancelVideoPlayRunnable();
            if (PhotoViewer.this.sendPhotoType != 1 || PhotoViewer.this.flashView == null || i != VideoTimelinePlayView.TYPE_PROGRESS) {
                if (PhotoViewer.this.sendPhotoType != 1 && !this.wasPlaying) {
                    return;
                }
                PhotoViewer.this.manuallyPaused = false;
                PhotoViewer.this.playVideoOrWeb();
                return;
            }
            PhotoViewer.this.cancelFlashAnimations();
            PhotoViewer photoViewer = PhotoViewer.this;
            photoViewer.captureFrameAtTime = photoViewer.avatarStartTime;
            if (PhotoViewer.this.captureFrameReadyAtTime != this.seekTo) {
                return;
            }
            PhotoViewer.this.captureCurrentFrame();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$11(View view) {
        if (this.captionEditText.getCaptionLimitOffset() < 0) {
            AndroidUtilities.shakeView(this.captionLimitView);
            try {
                this.captionLimitView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (MessagesController.getInstance(this.currentAccount).premiumLocked || MessagesController.getInstance(this.currentAccount).captionLengthLimitPremium <= this.captionEditText.getCodePointCount()) {
                return;
            }
            showCaptionLimitBulletin(this.containerView);
            return;
        }
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity != null && chatActivity.isInScheduleMode() && !this.parentChatActivity.isEditingMessageMedia()) {
            showScheduleDatePickerDialog();
        } else {
            sendPressed(true, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00d2, code lost:
        if (r13 != false) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00db, code lost:
        if (org.telegram.messenger.UserObject.isUserSelf(r2) != false) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x00e0, code lost:
        if (r11 == 3) goto L95;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x00e2, code lost:
        if (r4 == false) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x00e5, code lost:
        if (r11 != 4) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x00e9, code lost:
        if (r16.isCurrentVideo == false) goto L93;
     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x00f1, code lost:
        if (r16.timeItem.getColorFilter() == null) goto L52;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x00f4, code lost:
        r14 = r16.parentActivity;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x00f8, code lost:
        if (r11 != 0) goto L74;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x00fa, code lost:
        r15 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x00fc, code lost:
        r15 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x00ff, code lost:
        if (r11 != 3) goto L58;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x0101, code lost:
        r3 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x0102, code lost:
        r13 = new org.telegram.ui.ActionBar.ActionBarMenuSubItem(r14, r15, r3, r17);
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x0105, code lost:
        if (r11 != 0) goto L66;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x010b, code lost:
        if (org.telegram.messenger.UserObject.isUserSelf(r2) == false) goto L65;
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x010d, code lost:
        r13.setTextAndIcon(org.telegram.messenger.LocaleController.getString("SetReminder", org.telegram.messenger.R.string.SetReminder), org.telegram.messenger.R.drawable.msg_calendar2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x011b, code lost:
        r13.setTextAndIcon(org.telegram.messenger.LocaleController.getString("ScheduleMessage", org.telegram.messenger.R.string.ScheduleMessage), org.telegram.messenger.R.drawable.msg_calendar2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0129, code lost:
        if (r11 != 1) goto L68;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x012b, code lost:
        r13.setTextAndIcon(org.telegram.messenger.LocaleController.getString("SendWithoutSound", org.telegram.messenger.R.string.SendWithoutSound), org.telegram.messenger.R.drawable.input_notify_off);
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x0139, code lost:
        if (r11 != 2) goto L70;
     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x013b, code lost:
        r13.setTextAndIcon(org.telegram.messenger.LocaleController.getString("ReplacePhoto", org.telegram.messenger.R.string.ReplacePhoto), org.telegram.messenger.R.drawable.msg_replace);
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x0149, code lost:
        if (r11 != 3) goto L72;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x014b, code lost:
        r13.setTextAndIcon(org.telegram.messenger.LocaleController.getString("SendAsNewPhoto", org.telegram.messenger.R.string.SendAsNewPhoto), org.telegram.messenger.R.drawable.msg_send);
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x0159, code lost:
        if (r11 != 4) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x015b, code lost:
        r13.setTextAndIcon(org.telegram.messenger.LocaleController.getString("SendWithoutCompression", org.telegram.messenger.R.string.SendWithoutCompression), org.telegram.messenger.R.drawable.msg_sendfile);
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x0168, code lost:
        r13.setMinimumWidth(org.telegram.messenger.AndroidUtilities.dp(196.0f));
        r13.setColors(-1, -1);
        r16.sendPopupLayout.addView((android.view.View) r13, org.telegram.ui.Components.LayoutHelper.createLinear(-1, 48));
        r13.setOnClickListener(new org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda42(r16, r11));
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ boolean lambda$setParentActivity$15(org.telegram.ui.ActionBar.Theme.ResourcesProvider r17, android.view.View r18) {
        /*
            Method dump skipped, instructions count: 568
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.lambda$setParentActivity$15(org.telegram.ui.ActionBar.Theme$ResourcesProvider, android.view.View):boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setParentActivity$12(View view, MotionEvent motionEvent) {
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$13(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() != 4 || keyEvent.getRepeatCount() != 0 || (actionBarPopupWindow = this.sendPopupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.sendPopupWindow.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$14(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            showScheduleDatePickerDialog();
        } else if (i == 1) {
            sendPressed(false, 0);
        } else if (i == 2) {
            replacePressed();
        } else if (i == 3) {
            sendPressed(true, 0);
        } else if (i != 4) {
        } else {
            sendPressed(true, 0, false, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$16(View view) {
        if (this.captionEditText.getTag() != null) {
            return;
        }
        if (this.isCurrentVideo) {
            if (!this.videoConvertSupported) {
                return;
            }
            TextureView textureView = this.videoTextureView;
            if (!(textureView instanceof VideoEditTextureView)) {
                return;
            }
            VideoEditTextureView videoEditTextureView = (VideoEditTextureView) textureView;
            if (videoEditTextureView.getVideoWidth() <= 0 || videoEditTextureView.getVideoHeight() <= 0) {
                return;
            }
        }
        switchToEditMode(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$17(View view) {
        cropRotate(-90.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$18(View view) {
        cropMirror();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$19(View view) {
        if (this.captionEditText.getTag() != null) {
            return;
        }
        if (this.isCurrentVideo) {
            if (!this.videoConvertSupported) {
                return;
            }
            TextureView textureView = this.videoTextureView;
            if (!(textureView instanceof VideoEditTextureView)) {
                return;
            }
            VideoEditTextureView videoEditTextureView = (VideoEditTextureView) textureView;
            if (videoEditTextureView.getVideoWidth() <= 0 || videoEditTextureView.getVideoHeight() <= 0) {
                return;
            }
        }
        switchToEditMode(3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$20(View view) {
        if (this.captionEditText.getTag() != null) {
            return;
        }
        this.muteVideo = !this.muteVideo;
        updateMuteButton();
        updateVideoInfo();
        if (this.muteVideo && !this.checkImageView.isChecked()) {
            this.checkImageView.callOnClick();
            return;
        }
        Object obj = this.imagesArrLocals.get(this.currentIndex);
        if (!(obj instanceof MediaController.MediaEditState)) {
            return;
        }
        ((MediaController.MediaEditState) obj).editedInfo = getCurrentVideoEditedInfo();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$21(View view) {
        if (this.placeProvider == null || this.captionEditText.getTag() != null) {
            return;
        }
        this.placeProvider.needAddMorePhotos();
        closePhoto(true, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$22(View view) {
        if (this.captionEditText.getTag() != null) {
            return;
        }
        if (this.isCurrentVideo) {
            if (!this.videoConvertSupported) {
                return;
            }
            TextureView textureView = this.videoTextureView;
            if (!(textureView instanceof VideoEditTextureView)) {
                return;
            }
            VideoEditTextureView videoEditTextureView = (VideoEditTextureView) textureView;
            if (videoEditTextureView.getVideoWidth() <= 0 || videoEditTextureView.getVideoHeight() <= 0) {
                return;
            }
        }
        switchToEditMode(2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$23(Activity activity, View view) {
        if (this.captionEditText.getTag() != null || this.muteVideo) {
            return;
        }
        if (this.compressItem.getTag() == null) {
            if (!this.videoConvertSupported) {
                return;
            }
            if (this.tooltip == null) {
                this.tooltip = new Tooltip(activity, this.containerView, -NUM, -1);
            }
            this.tooltip.setText(LocaleController.getString("VideoQualityIsTooLow", R.string.VideoQualityIsTooLow));
            this.tooltip.show(this.compressItem);
            return;
        }
        showQualityView(true);
        requestVideoPreview(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$29(Theme.ResourcesProvider resourcesProvider, View view) {
        int i;
        String str;
        int i2;
        if (this.parentActivity == null || this.captionEditText.getTag() != null) {
            return;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity, false, resourcesProvider, -16777216);
        builder.setUseHardwareLayer(false);
        LinearLayout linearLayout = new LinearLayout(this.parentActivity);
        linearLayout.setOrientation(1);
        builder.setCustomView(linearLayout);
        TextView textView = new TextView(this.parentActivity);
        textView.setLines(1);
        textView.setSingleLine(true);
        textView.setText(LocaleController.getString("MessageLifetime", R.string.MessageLifetime));
        textView.setTextColor(-1);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextSize(1, 20.0f);
        textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(4.0f));
        textView.setGravity(16);
        linearLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f));
        textView.setOnTouchListener(PhotoViewer$$ExternalSyntheticLambda49.INSTANCE);
        TextView textView2 = new TextView(this.parentActivity);
        if (this.isCurrentVideo) {
            i = R.string.MessageLifetimeVideo;
            str = "MessageLifetimeVideo";
        } else {
            i = R.string.MessageLifetimePhoto;
            str = "MessageLifetimePhoto";
        }
        textView2.setText(LocaleController.getString(str, i));
        textView2.setTextColor(-8355712);
        textView2.setTextSize(1, 14.0f);
        textView2.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        textView2.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
        textView2.setGravity(16);
        linearLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f));
        textView2.setOnTouchListener(PhotoViewer$$ExternalSyntheticLambda48.INSTANCE);
        final BottomSheet create = builder.create();
        final NumberPicker numberPicker = new NumberPicker(this.parentActivity, resourcesProvider);
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
        } else if (i2 >= 0 && i2 < 21) {
            numberPicker.setValue(i2);
        } else {
            numberPicker.setValue(((i2 / 5) + 21) - 5);
        }
        numberPicker.setTextColor(-1);
        numberPicker.setSelectorColor(-11711155);
        numberPicker.setFormatter(PhotoViewer$$ExternalSyntheticLambda80.INSTANCE);
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(-1, -2));
        FrameLayout frameLayout = new FrameLayout(this, this.parentActivity) { // from class: org.telegram.ui.PhotoViewer.28
            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean z, int i3, int i4, int i5, int i6) {
                int childCount = getChildCount();
                int i7 = i5 - i3;
                for (int i8 = 0; i8 < childCount; i8++) {
                    View childAt = getChildAt(i8);
                    if (((Integer) childAt.getTag()).intValue() == -1) {
                        childAt.layout((i7 - getPaddingRight()) - childAt.getMeasuredWidth(), getPaddingTop(), i7 - getPaddingRight(), getPaddingTop() + childAt.getMeasuredHeight());
                    } else if (((Integer) childAt.getTag()).intValue() == -2) {
                        int paddingLeft = getPaddingLeft();
                        childAt.layout(paddingLeft, getPaddingTop(), childAt.getMeasuredWidth() + paddingLeft, getPaddingTop() + childAt.getMeasuredHeight());
                    } else {
                        childAt.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + childAt.getMeasuredWidth(), getPaddingTop() + childAt.getMeasuredHeight());
                    }
                }
            }
        };
        frameLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 52));
        TextView textView3 = new TextView(this.parentActivity);
        textView3.setMinWidth(AndroidUtilities.dp(64.0f));
        textView3.setTag(-1);
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(getThemedColor("dialogFloatingButton"));
        textView3.setGravity(17);
        textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView3.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
        textView3.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(-11944718));
        textView3.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        frameLayout.addView(textView3, LayoutHelper.createFrame(-2, 36, 53));
        textView3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda45
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$setParentActivity$27(numberPicker, create, view2);
            }
        });
        TextView textView4 = new TextView(this.parentActivity);
        textView4.setMinWidth(AndroidUtilities.dp(64.0f));
        textView4.setTag(-2);
        textView4.setTextSize(1, 14.0f);
        textView4.setTextColor(-1);
        textView4.setGravity(17);
        textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView4.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
        textView4.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(-1));
        textView4.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        frameLayout.addView(textView4, LayoutHelper.createFrame(-2, 36, 53));
        textView4.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda17
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                BottomSheet.this.dismiss();
            }
        });
        create.setBackgroundColor(-16777216);
        create.show();
        AndroidUtilities.setNavigationBarColor(create.getWindow(), -16777216, false);
        AndroidUtilities.setLightNavigationBar(create.getWindow(), false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ String lambda$setParentActivity$26(int i) {
        if (i == 0) {
            return LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
        }
        if (i >= 1 && i < 21) {
            return LocaleController.formatTTLString(i);
        }
        return LocaleController.formatTTLString((i - 16) * 5);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$27(NumberPicker numberPicker, BottomSheet bottomSheet, View view) {
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
        this.timeItem.setColorFilter(value != 0 ? new PorterDuffColorFilter(getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY) : null);
        if (!this.checkImageView.isChecked()) {
            this.checkImageView.callOnClick();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$31(View view) {
        if (this.imageMoveAnimation != null) {
            return;
        }
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda53
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.lambda$setParentActivity$30();
            }
        };
        if (!this.previousHasTransform) {
            float stateOrientation = this.previousCropOrientation - this.photoCropView.cropView.getStateOrientation();
            if (Math.abs(stateOrientation) > 180.0f) {
                stateOrientation = stateOrientation < 0.0f ? stateOrientation + 360.0f : -(360.0f - stateOrientation);
            }
            cropRotate(stateOrientation, this.photoCropView.cropView.getStateMirror(), runnable);
            return;
        }
        runnable.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$30() {
        this.cropTransform.setViewTransform(this.previousHasTransform, this.previousCropPx, this.previousCropPy, this.previousCropRotation, this.previousCropOrientation, this.previousCropScale, 1.0f, 1.0f, this.previousCropPw, this.previousCropPh, 0.0f, 0.0f, this.previousCropMirrored);
        switchToEditMode(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$32(View view) {
        if (this.currentEditMode != 1 || this.photoCropView.isReady()) {
            applyCurrentEditMode();
            switchToEditMode(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$34(View view) {
        float f = -this.photoCropView.cropView.getStateOrientation();
        if (Math.abs(f) > 180.0f) {
            f = f < 0.0f ? f + 360.0f : -(360.0f - f);
        }
        cropRotate(f, this.photoCropView.cropView.getStateMirror(), new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda58
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.lambda$setParentActivity$33();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$33() {
        this.photoCropView.reset(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$35(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        PhotoViewerProvider photoViewerProvider;
        Bitmap bitmap;
        if (imageReceiver == this.centerImage && z && !z2) {
            if (!this.isCurrentVideo && ((this.currentEditMode == 1 || this.sendPhotoType == 1) && this.photoCropView != null && (bitmap = imageReceiver.getBitmap()) != null)) {
                this.photoCropView.setBitmap(bitmap, imageReceiver.getOrientation(), this.sendPhotoType != 1, true, this.paintingOverlay, this.cropTransform, null, null);
            }
            if (this.paintingOverlay.getVisibility() == 0) {
                this.containerView.requestLayout();
            }
            detectFaces();
        }
        if (imageReceiver != this.centerImage || !z || (photoViewerProvider = this.placeProvider) == null || !photoViewerProvider.scaleToFill() || this.ignoreDidSetImage || this.sendPhotoType == 1) {
            return;
        }
        if (!this.wasLayout) {
            this.dontResetZoomOnFirstLayout = true;
        } else {
            setScaleToFill();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$36(View view) {
        if (this.captionEditText.getTag() != null) {
            return;
        }
        setPhotoChecked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$37(View view) {
        PhotoViewerProvider photoViewerProvider;
        if (this.captionEditText.getTag() != null || (photoViewerProvider = this.placeProvider) == null || photoViewerProvider.getSelectedPhotosOrder() == null || this.placeProvider.getSelectedPhotosOrder().isEmpty()) {
            return;
        }
        togglePhotosListView(!this.isPhotosListViewVisible, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$38(View view, int i) {
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

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$33  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass33 extends RecyclerListView {
        AnonymousClass33(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            return !PhotoViewer.this.bottomTouchEnabled && super.dispatchTouchEvent(motionEvent);
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return !PhotoViewer.this.bottomTouchEnabled && super.onInterceptTouchEvent(motionEvent);
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return !PhotoViewer.this.bottomTouchEnabled && super.onTouchEvent(motionEvent);
        }

        @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            invalidate();
            if (getParent() != null) {
                ((View) getParent()).invalidate();
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
        protected void onSizeChanged(int i, int i2, int i3, int i4) {
            super.onSizeChanged(i, i2, i3, i4);
            if (PhotoViewer.this.mentionListViewVisible && getVisibility() == 0 && PhotoViewer.this.mentionListAnimation == null) {
                int i5 = i2 - i4;
                setTranslationY(i5);
                PhotoViewer.this.mentionListView.setTranslationY(MathUtils.clamp(PhotoViewer.this.mentionListView.getTranslationY(), Math.min(i5, 0), Math.max(i5, 0)));
                PhotoViewer.this.mentionListAnimation = new SpringAnimation(this, DynamicAnimation.TRANSLATION_Y).setMinValue(Math.min(i5, 0)).setMaxValue(Math.max(i5, 0)).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f));
                PhotoViewer.this.mentionListAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.PhotoViewer$33$$ExternalSyntheticLambda0
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                        PhotoViewer.AnonymousClass33.this.lambda$onSizeChanged$0(dynamicAnimation, z, f, f2);
                    }
                });
                PhotoViewer.this.mentionListAnimation.start();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSizeChanged$0(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            if (PhotoViewer.this.mentionListAnimation == dynamicAnimation) {
                PhotoViewer.this.mentionListAnimation = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$35  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass35 implements MentionsAdapter.MentionsAdapterDelegate {
        @Override // org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate
        public void onContextClick(TLRPC$BotInlineResult tLRPC$BotInlineResult) {
        }

        @Override // org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate
        public void onContextSearch(boolean z) {
        }

        @Override // org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate
        public void onItemCountUpdate(int i, int i2) {
        }

        AnonymousClass35() {
        }

        @Override // org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate
        public void needChangePanelVisibility(boolean z) {
            if (z) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PhotoViewer.this.mentionListView.getLayoutParams();
                float min = (Math.min(3, PhotoViewer.this.mentionsAdapter.getItemCount()) * 36) + (PhotoViewer.this.mentionsAdapter.getItemCount() > 3 ? 18 : 0);
                layoutParams.height = AndroidUtilities.dp(min);
                layoutParams.topMargin = -AndroidUtilities.dp(min);
                PhotoViewer.this.mentionListView.setLayoutParams(layoutParams);
                if (PhotoViewer.this.mentionListAnimation != null) {
                    PhotoViewer.this.mentionListAnimation.cancel();
                    PhotoViewer.this.mentionListAnimation = null;
                }
                if (PhotoViewer.this.mentionListView.getVisibility() == 0) {
                    PhotoViewer.this.mentionListView.setTranslationY(0.0f);
                    return;
                }
                PhotoViewer.this.mentionLayoutManager.scrollToPositionWithOffset(0, 10000);
                if (PhotoViewer.this.allowMentions) {
                    PhotoViewer.this.mentionListView.setVisibility(0);
                    PhotoViewer.this.mentionListViewVisible = true;
                    PhotoViewer.this.mentionListView.setTranslationY(AndroidUtilities.dp(min));
                    PhotoViewer.this.mentionListView.setTranslationY(MathUtils.clamp(PhotoViewer.this.mentionListView.getTranslationY(), 0.0f, AndroidUtilities.dp(min)));
                    PhotoViewer.this.mentionListAnimation = new SpringAnimation(PhotoViewer.this.mentionListView, DynamicAnimation.TRANSLATION_Y).setMinValue(0.0f).setMaxValue(AndroidUtilities.dp(min)).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f));
                    PhotoViewer.this.mentionListAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.PhotoViewer$35$$ExternalSyntheticLambda1
                        @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                        public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                            PhotoViewer.AnonymousClass35.this.lambda$needChangePanelVisibility$0(dynamicAnimation, z2, f, f2);
                        }
                    });
                    PhotoViewer.this.mentionListAnimation.start();
                    return;
                }
                PhotoViewer.this.mentionListView.setTranslationY(0.0f);
                PhotoViewer.this.mentionListView.setVisibility(4);
                return;
            }
            if (PhotoViewer.this.mentionListAnimation != null) {
                PhotoViewer.this.mentionListAnimation.cancel();
                PhotoViewer.this.mentionListAnimation = null;
            }
            if (PhotoViewer.this.mentionListView.getVisibility() == 8) {
                return;
            }
            if (PhotoViewer.this.allowMentions) {
                PhotoViewer.this.mentionListViewVisible = false;
                PhotoViewer.this.mentionListView.setTranslationY(MathUtils.clamp(PhotoViewer.this.mentionListView.getTranslationY(), 0.0f, PhotoViewer.this.mentionListView.getMeasuredHeight()));
                PhotoViewer.this.mentionListAnimation = new SpringAnimation(PhotoViewer.this.mentionListView, DynamicAnimation.TRANSLATION_Y).setMinValue(0.0f).setMaxValue(PhotoViewer.this.mentionListView.getMeasuredHeight()).setSpring(new SpringForce(PhotoViewer.this.mentionListView.getMeasuredHeight()).setStiffness(750.0f).setDampingRatio(1.0f));
                PhotoViewer.this.mentionListAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.PhotoViewer$35$$ExternalSyntheticLambda0
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                        PhotoViewer.AnonymousClass35.this.lambda$needChangePanelVisibility$1(dynamicAnimation, z2, f, f2);
                    }
                });
                PhotoViewer.this.mentionListAnimation.start();
                return;
            }
            PhotoViewer.this.mentionListView.setVisibility(8);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$needChangePanelVisibility$0(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            if (PhotoViewer.this.mentionListAnimation == dynamicAnimation) {
                PhotoViewer.this.mentionListAnimation = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$needChangePanelVisibility$1(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            if (PhotoViewer.this.mentionListAnimation == dynamicAnimation) {
                PhotoViewer.this.mentionListView.setVisibility(8);
                PhotoViewer.this.mentionListAnimation = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$39(View view, int i) {
        AnimatedEmojiSpan animatedEmojiSpan;
        Object item = this.mentionsAdapter.getItem(i);
        int resultStartPosition = this.mentionsAdapter.getResultStartPosition();
        int resultLength = this.mentionsAdapter.getResultLength();
        if (item instanceof TLRPC$User) {
            TLRPC$User tLRPC$User = (TLRPC$User) item;
            String publicUsername = UserObject.getPublicUsername(tLRPC$User);
            if (publicUsername != null) {
                PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
                photoViewerCaptionEnterView.replaceWithText(resultStartPosition, resultLength, "@" + publicUsername + " ", false);
                return;
            }
            String firstName = UserObject.getFirstName(tLRPC$User);
            SpannableString spannableString = new SpannableString(firstName + " ");
            spannableString.setSpan(new URLSpanUserMentionPhotoViewer("" + tLRPC$User.id, true), 0, spannableString.length(), 33);
            this.captionEditText.replaceWithText(resultStartPosition, resultLength, spannableString, false);
        } else if (item instanceof String) {
            PhotoViewerCaptionEnterView photoViewerCaptionEnterView2 = this.captionEditText;
            photoViewerCaptionEnterView2.replaceWithText(resultStartPosition, resultLength, item + " ", false);
        } else if (!(item instanceof MediaDataController.KeywordResult)) {
        } else {
            String str = ((MediaDataController.KeywordResult) item).emoji;
            this.captionEditText.addEmojiToRecent(str);
            if (str != null && str.startsWith("animated_")) {
                try {
                    long parseLong = Long.parseLong(str.substring(9));
                    TLRPC$Document findDocument = AnimatedEmojiDrawable.findDocument(this.currentAccount, parseLong);
                    SpannableString spannableString2 = new SpannableString(MessageObject.findAnimatedEmojiEmoticon(findDocument));
                    if (findDocument != null) {
                        animatedEmojiSpan = new AnimatedEmojiSpan(findDocument, this.captionEditText.getMessageEditText().getPaint().getFontMetricsInt());
                    } else {
                        animatedEmojiSpan = new AnimatedEmojiSpan(parseLong, this.captionEditText.getMessageEditText().getPaint().getFontMetricsInt());
                    }
                    spannableString2.setSpan(animatedEmojiSpan, 0, spannableString2.length(), 33);
                    this.captionEditText.replaceWithText(resultStartPosition, resultLength, spannableString2, false);
                    return;
                } catch (Exception unused) {
                    this.captionEditText.replaceWithText(resultStartPosition, resultLength, str, true);
                    return;
                }
            }
            this.captionEditText.replaceWithText(resultStartPosition, resultLength, str, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setParentActivity$41(Theme.ResourcesProvider resourcesProvider, View view, int i) {
        if (this.mentionsAdapter.getItem(i) instanceof String) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity, resourcesProvider);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
            builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda12
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    PhotoViewer.this.lambda$setParentActivity$40(dialogInterface, i2);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showAlertDialog(builder);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setParentActivity$40(DialogInterface dialogInterface, int i) {
        this.mentionsAdapter.clearRecentHashtags();
    }

    public void showCaptionLimitBulletin(FrameLayout frameLayout) {
        BaseFragment baseFragment = this.parentFragment;
        if (!(baseFragment instanceof ChatActivity) || !ChatObject.isChannelAndNotMegaGroup(((ChatActivity) baseFragment).getCurrentChat())) {
            return;
        }
        BulletinFactory.of(frameLayout, this.resourcesProvider).createCaptionLimitBulletin(MessagesController.getInstance(this.currentAccount).captionLengthLimitPremium, new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda61
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.lambda$showCaptionLimitBulletin$42();
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showCaptionLimitBulletin$42() {
        closePhoto(false, false);
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.dismiss(true);
        }
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            baseFragment.presentFragment(new PremiumPreviewFragment("caption_limit"));
        }
    }

    public ChatAttachAlert getParentAlert() {
        return this.parentAlert;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateNavBarColorTo(int i) {
        animateNavBarColorTo(i, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateNavBarColorTo(final int i, boolean z) {
        ValueAnimator valueAnimator = this.navBarAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        final int color = this.blackPaint.getColor();
        AndroidUtilities.setLightNavigationBar(this.windowView, ((double) AndroidUtilities.computePerceivedBrightness(i)) >= 0.721d);
        if (z) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.navBarAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda8
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    PhotoViewer.this.lambda$animateNavBarColorTo$43(color, i, valueAnimator2);
                }
            });
            this.navBarAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.36
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    PhotoViewer.this.blackPaint.setColor(i);
                    if (PhotoViewer.this.windowView != null) {
                        PhotoViewer.this.windowView.invalidate();
                    }
                }
            });
            this.navBarAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.navBarAnimator.setDuration(200L);
            this.navBarAnimator.start();
            return;
        }
        this.navBarAnimator = null;
        this.blackPaint.setColor(i);
        this.windowView.invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateNavBarColorTo$43(int i, int i2, ValueAnimator valueAnimator) {
        this.blackPaint.setColor(ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue()));
        FrameLayout frameLayout = this.windowView;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
    }

    private void showScheduleDatePickerDialog() {
        if (this.parentChatActivity == null) {
            return;
        }
        AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentChatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda79
            @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
            public final void didSelectDate(boolean z, int i) {
                PhotoViewer.this.sendPressed(z, i);
            }
        }, new AlertsCreator.ScheduleDatePickerColors(-1, -14342875, -1, NUM, -1, -NUM, NUM));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendPressed(boolean z, int i) {
        sendPressed(z, i, false, false);
    }

    private void replacePressed() {
        sendPressed(false, 0, true, false);
    }

    private void sendPressed(boolean z, int i, boolean z2, boolean z3) {
        int i2;
        ChatActivity chatActivity;
        if (this.captionEditText.getTag() == null && this.placeProvider != null && !this.doneButtonPressed) {
            if (this.sendPhotoType == 1) {
                applyCurrentEditMode();
            }
            if (!z2 && (chatActivity = this.parentChatActivity) != null) {
                TLRPC$Chat currentChat = chatActivity.getCurrentChat();
                if (this.parentChatActivity.getCurrentUser() != null || ((ChatObject.isChannel(currentChat) && currentChat.megagroup) || !ChatObject.isChannel(currentChat))) {
                    MessagesController.getNotificationsSettings(this.currentAccount).edit().putBoolean("silent_" + this.parentChatActivity.getDialogId(), !z).commit();
                }
            }
            VideoEditedInfo currentVideoEditedInfo = getCurrentVideoEditedInfo();
            if (!this.imagesArrLocals.isEmpty() && (i2 = this.currentIndex) >= 0 && i2 < this.imagesArrLocals.size()) {
                Object obj = this.imagesArrLocals.get(this.currentIndex);
                if (obj instanceof MediaController.MediaEditState) {
                    ((MediaController.MediaEditState) obj).editedInfo = currentVideoEditedInfo;
                }
            }
            this.doneButtonPressed = true;
            if (currentVideoEditedInfo != null) {
                long j = ((float) currentVideoEditedInfo.estimatedSize) * 0.9f;
                if ((j > NUM && !UserConfig.getInstance(this.currentAccount).isPremium()) || j > 4194304000L) {
                    if (this.parentAlert == null) {
                        return;
                    }
                    new LimitReachedBottomSheet(this.parentAlert.getBaseFragment(), this.parentAlert.getContainer().getContext(), 6, UserConfig.selectedAccount).show();
                    return;
                }
            }
            if (!z2) {
                this.placeProvider.sendButtonPressed(this.currentIndex, currentVideoEditedInfo, z, i, z3);
            } else {
                this.placeProvider.replaceButtonPressed(this.currentIndex, currentVideoEditedInfo);
            }
            closePhoto(false, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showShareAlert(ArrayList<MessageObject> arrayList) {
        boolean z;
        boolean z2;
        FrameLayoutDrawer frameLayoutDrawer = this.containerView;
        requestAdjustToNothing();
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity == null || chatActivity.getChatActivityEnterView() == null || this.parentChatActivity.getFragmentView() == null) {
            z = false;
        } else {
            if (this.parentChatActivity.getChatActivityEnterView().isKeyboardVisible()) {
                this.parentChatActivity.getChatActivityEnterView().showEmojiView();
                z2 = true;
            } else {
                z2 = false;
            }
            AndroidUtilities.setAdjustResizeToNothing(this.parentChatActivity.getParentActivity(), this.classGuid);
            this.parentChatActivity.getFragmentView().requestLayout();
            z = z2;
        }
        final AnonymousClass37 anonymousClass37 = new AnonymousClass37(this.parentActivity, this.parentChatActivity, arrayList, null, null, false, null, null, false, true, null, frameLayoutDrawer, z);
        anonymousClass37.setFocusable(false);
        anonymousClass37.getWindow().setSoftInputMode(48);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda68
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.lambda$showShareAlert$44(anonymousClass37);
            }
        }, 250L);
        anonymousClass37.show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$37  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass37 extends ShareAlert {
        final /* synthetic */ boolean val$finalOpenKeyboardOnShareAlertClose;
        final /* synthetic */ FrameLayout val$photoContainerView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass37(Context context, ChatActivity chatActivity, ArrayList arrayList, String str, String str2, boolean z, String str3, String str4, boolean z2, boolean z3, Theme.ResourcesProvider resourcesProvider, FrameLayout frameLayout, boolean z4) {
            super(context, chatActivity, arrayList, str, str2, z, str3, str4, z2, z3, resourcesProvider);
            this.val$photoContainerView = frameLayout;
            this.val$finalOpenKeyboardOnShareAlertClose = z4;
        }

        @Override // org.telegram.ui.Components.ShareAlert
        protected void onSend(final LongSparseArray<TLRPC$Dialog> longSparseArray, final int i, TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
            final FrameLayout frameLayout = this.val$photoContainerView;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$37$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass37.this.lambda$onSend$0(frameLayout, longSparseArray, i);
                }
            }, 250L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSend$0(FrameLayout frameLayout, LongSparseArray longSparseArray, int i) {
            BulletinFactory.createForwardedBulletin(PhotoViewer.this.parentActivity, frameLayout, longSparseArray.size(), longSparseArray.size() == 1 ? ((TLRPC$Dialog) longSparseArray.valueAt(0)).id : 0L, i, -NUM, -1).show();
        }

        @Override // org.telegram.ui.Components.ShareAlert, org.telegram.ui.ActionBar.BottomSheet
        public void dismissInternal() {
            super.dismissInternal();
            if (this.val$finalOpenKeyboardOnShareAlertClose) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$37$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass37.this.lambda$dismissInternal$1();
                    }
                }, 50L);
            }
            PhotoViewer.this.requestAdjust();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$dismissInternal$1() {
            if (PhotoViewer.this.parentChatActivity == null || PhotoViewer.this.parentChatActivity.getChatActivityEnterView() == null) {
                return;
            }
            PhotoViewer.this.parentChatActivity.getChatActivityEnterView().openKeyboard();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showShareAlert$44(ShareAlert shareAlert) {
        if (shareAlert == null || shareAlert.getWindow() == null) {
            return;
        }
        shareAlert.setFocusable(true);
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity == null || chatActivity.getChatActivityEnterView() == null) {
            return;
        }
        this.parentChatActivity.getChatActivityEnterView().hidePopup(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMenuItemIcon() {
        if (this.speedItem.getVisibility() != 0) {
            this.menuItem.setIcon(R.drawable.ic_ab_other);
            return;
        }
        if (Math.abs(this.currentVideoSpeed - 0.25f) < 0.001f) {
            this.menuItem.setIcon(R.drawable.msg_more_0_2);
            this.speedItem.setSubtext(LocaleController.getString("SpeedVerySlow", R.string.SpeedVerySlow));
        } else if (Math.abs(this.currentVideoSpeed - 0.5f) < 0.001f) {
            this.menuItem.setIcon(R.drawable.msg_more_0_5);
            this.speedItem.setSubtext(LocaleController.getString("SpeedSlow", R.string.SpeedSlow));
        } else if (Math.abs(this.currentVideoSpeed - 1.0f) < 0.001f) {
            this.menuItem.setIcon(R.drawable.ic_ab_other);
            this.speedItem.setSubtext(LocaleController.getString("SpeedNormal", R.string.SpeedNormal));
        } else if (Math.abs(this.currentVideoSpeed - 1.5f) < 0.001f) {
            this.menuItem.setIcon(R.drawable.msg_more_1_5);
            this.speedItem.setSubtext(LocaleController.getString("SpeedFast", R.string.SpeedFast));
        } else {
            this.menuItem.setIcon(R.drawable.msg_more_2);
            this.speedItem.setSubtext(LocaleController.getString("SpeedVeryFast", R.string.SpeedVeryFast));
        }
        this.chooseSpeedLayout.update(this.currentVideoSpeed);
    }

    public float getCurrentVideoSpeed() {
        return this.currentVideoSpeed;
    }

    private boolean checkInlinePermissions() {
        Activity activity = this.parentActivity;
        if (activity == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(activity)) {
            return true;
        }
        AlertsCreator.createDrawOverlayPermissionDialog(this.parentActivity, null).show();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void captureCurrentFrame() {
        TextureView textureView;
        if (this.captureFrameAtTime == -1 || (textureView = this.videoTextureView) == null) {
            return;
        }
        this.captureFrameAtTime = -1L;
        final Bitmap bitmap = textureView.getBitmap();
        this.flashView.animate().alpha(1.0f).setInterpolator(CubicBezierInterpolator.EASE_BOTH).setDuration(85L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.38
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                PhotoViewer.this.photoCropView.setVideoThumb(bitmap, 0);
                PhotoViewer.this.flashAnimator = new AnimatorSet();
                PhotoViewer.this.flashAnimator.playTogether(ObjectAnimator.ofFloat(PhotoViewer.this.flashView, PhotoViewer.this.FLASH_VIEW_VALUE, 0.0f));
                PhotoViewer.this.flashAnimator.setDuration(85L);
                PhotoViewer.this.flashAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                PhotoViewer.this.flashAnimator.addListener(new AnonymousClass1());
                PhotoViewer.this.flashAnimator.start();
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            /* renamed from: org.telegram.ui.PhotoViewer$38$1  reason: invalid class name */
            /* loaded from: classes3.dex */
            public class AnonymousClass1 extends AnimatorListenerAdapter {
                AnonymousClass1() {
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (PhotoViewer.this.flashAnimator == null) {
                        return;
                    }
                    AndroidUtilities.runOnUIThread(PhotoViewer.this.videoPlayRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$38$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.AnonymousClass38.AnonymousClass1.this.lambda$onAnimationEnd$0();
                        }
                    }, 860L);
                }

                /* JADX INFO: Access modifiers changed from: private */
                public /* synthetic */ void lambda$onAnimationEnd$0() {
                    PhotoViewer.this.manuallyPaused = false;
                    if (PhotoViewer.this.videoPlayer != null) {
                        PhotoViewer.this.videoPlayer.play();
                    }
                    PhotoViewer.this.videoPlayRunnable = null;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    PhotoViewer.this.flashAnimator = null;
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$39  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass39 extends SpoilersTextView {
        private AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiDrawables;
        private Layout lastLayout;
        private LinkSpanDrawable.LinkCollector links;
        private LinkSpanDrawable<ClickableSpan> pressedLink;

        AnonymousClass39(Context context) {
            super(context);
            this.links = new LinkSpanDrawable.LinkCollector(this);
        }

        /* JADX WARN: Removed duplicated region for block: B:30:0x00f9  */
        @Override // android.widget.TextView, android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onTouchEvent(android.view.MotionEvent r10) {
            /*
                Method dump skipped, instructions count: 305
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.AnonymousClass39.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouchEvent$1(LinkSpanDrawable linkSpanDrawable) {
            LinkSpanDrawable<ClickableSpan> linkSpanDrawable2 = this.pressedLink;
            if (linkSpanDrawable != linkSpanDrawable2 || linkSpanDrawable2 == null || !(linkSpanDrawable2.getSpan() instanceof URLSpan)) {
                return;
            }
            PhotoViewer.this.onLinkLongPress((URLSpan) this.pressedLink.getSpan(), this, new Runnable() { // from class: org.telegram.ui.PhotoViewer$39$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass39.this.lambda$onTouchEvent$0();
                }
            });
            this.pressedLink = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouchEvent$0() {
            this.links.clear();
        }

        @Override // android.view.View
        public void setPressed(boolean z) {
            boolean z2 = z != isPressed();
            super.setPressed(z);
            if (z2) {
                invalidate();
            }
        }

        @Override // org.telegram.ui.Components.spoilers.SpoilersTextView, android.widget.TextView, android.view.View
        protected void onDraw(Canvas canvas) {
            canvas.save();
            canvas.translate(getPaddingLeft(), 0.0f);
            if (this.links.draw(canvas)) {
                invalidate();
            }
            canvas.restore();
            super.onDraw(canvas);
            if (this.lastLayout != getLayout()) {
                this.animatedEmojiDrawables = AnimatedEmojiSpan.update(0, this, this.animatedEmojiDrawables, getLayout());
                this.lastLayout = getLayout();
            }
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            AnimatedEmojiSpan.release(this, this.animatedEmojiDrawables);
        }

        @Override // org.telegram.ui.Components.spoilers.SpoilersTextView, android.widget.TextView
        protected void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            super.onTextChanged(charSequence, i, i2, i3);
            this.animatedEmojiDrawables = AnimatedEmojiSpan.update(0, this, this.animatedEmojiDrawables, getLayout());
        }

        @Override // android.view.View
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop());
            canvas.clipRect(0.0f, getScrollY(), getWidth() - getPaddingRight(), (getHeight() + getScrollY()) - (getPaddingBottom() * 0.75f));
            AnimatedEmojiSpan.drawAnimatedEmojis(canvas, getLayout(), this.animatedEmojiDrawables, 0.0f, null, 0.0f, 0.0f, 0.0f, 1.0f);
            canvas.restore();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: createCaptionTextView */
    public TextView lambda$setParentActivity$6() {
        AnonymousClass39 anonymousClass39 = new AnonymousClass39(this.activityContext);
        ViewHelper.setPadding(anonymousClass39, 16.0f, 8.0f, 16.0f, 8.0f);
        anonymousClass39.setLinkTextColor(-8796932);
        anonymousClass39.setTextColor(-1);
        anonymousClass39.setHighlightColor(NUM);
        anonymousClass39.setGravity(LayoutHelper.getAbsoluteGravityStart() | 16);
        anonymousClass39.setTextSize(1, 16.0f);
        anonymousClass39.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda24
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PhotoViewer.this.lambda$createCaptionTextView$45(view);
            }
        });
        return anonymousClass39;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createCaptionTextView$45(View view) {
        if (!this.needCaptionLayout) {
            return;
        }
        openCaptionEnter();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getLeftInset() {
        Object obj = this.lastInsets;
        if (obj == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets) obj).getSystemWindowInsetLeft();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getRightInset() {
        Object obj = this.lastInsets;
        if (obj == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return ((WindowInsets) obj).getSystemWindowInsetRight();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissInternal() {
        try {
            if (this.windowView.getParent() == null) {
                return;
            }
            Activity activity = this.parentActivity;
            if (activity instanceof LaunchActivity) {
                ((LaunchActivity) activity).drawerLayoutContainer.setAllowDrawContent(true);
            }
            ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            onHideView();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchToPip(boolean z) {
        final CubicBezierInterpolator cubicBezierInterpolator;
        CubicBezierInterpolator cubicBezierInterpolator2;
        if (this.videoPlayer == null || !this.textureUploaded || !checkInlinePermissions() || this.changingTextureView || this.switchingInlineMode || this.isInline) {
            return;
        }
        if (PipInstance != null) {
            PipInstance.destroyPhotoViewer();
        }
        this.openedFullScreenVideo = false;
        PipInstance = Instance;
        Instance = null;
        this.switchingInlineMode = true;
        this.isVisible = false;
        AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
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
                if (z) {
                    this.currentPlaceObject.imageReceiver.setAlpha(0.0f);
                    final ImageReceiver imageReceiver = this.currentPlaceObject.imageReceiver;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            PhotoViewer.lambda$switchToPip$46(ImageReceiver.this, valueAnimator);
                        }
                    });
                    ofFloat.addListener(new AnimatorListenerAdapter(this) { // from class: org.telegram.ui.PhotoViewer.40
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            imageReceiver.setAlpha(1.0f);
                        }
                    });
                    ofFloat.setDuration(250L);
                    ofFloat.start();
                }
                this.currentPlaceObject.imageReceiver.setAllowStartAnimation(true);
                this.currentPlaceObject.imageReceiver.startAnimation();
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.pipAnimationInProgress = true;
            org.telegram.ui.Components.Rect pipRect = PipVideoOverlay.getPipRect(true, this.aspectRatioFrameLayout.getAspectRatio());
            final float width = pipRect.width / this.videoTextureView.getWidth();
            final ValueAnimator ofFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
            final float translationX = this.videoTextureView.getTranslationX();
            final float translationY = this.videoTextureView.getTranslationY() + this.translationY;
            final float translationY2 = this.textureImageView.getTranslationY() + this.translationY;
            final float f = pipRect.x;
            final float x = (f - this.aspectRatioFrameLayout.getX()) + getLeftInset();
            final float f2 = pipRect.y;
            final float y = f2 - this.aspectRatioFrameLayout.getY();
            this.textureImageView.setTranslationY(translationY2);
            this.videoTextureView.setTranslationY(translationY);
            FirstFrameView firstFrameView = this.firstFrameView;
            if (firstFrameView != null) {
                firstFrameView.setTranslationY(translationY);
            }
            this.translationY = 0.0f;
            this.containerView.invalidate();
            if (z) {
                if (translationY < y) {
                    cubicBezierInterpolator2 = new CubicBezierInterpolator(0.5d, 0.0d, 0.9d, 0.9d);
                } else {
                    cubicBezierInterpolator2 = new CubicBezierInterpolator(0.0d, 0.5d, 0.9d, 0.9d);
                }
                cubicBezierInterpolator = cubicBezierInterpolator2;
            } else {
                cubicBezierInterpolator = null;
            }
            ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider(this) { // from class: org.telegram.ui.PhotoViewer.41
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), ((Float) ofFloat2.getAnimatedValue()).floatValue() * AndroidUtilities.dp(10.0f) * (1.0f / width));
                }
            };
            this.videoTextureView.setOutlineProvider(viewOutlineProvider);
            this.videoTextureView.setClipToOutline(true);
            this.textureImageView.setOutlineProvider(viewOutlineProvider);
            this.textureImageView.setClipToOutline(true);
            FirstFrameView firstFrameView2 = this.firstFrameView;
            if (firstFrameView2 != null) {
                firstFrameView2.setOutlineProvider(viewOutlineProvider);
                this.firstFrameView.setClipToOutline(true);
            }
            ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda9
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PhotoViewer.this.lambda$switchToPip$47(cubicBezierInterpolator, translationX, f, translationY2, f2, x, translationY, y, valueAnimator);
                }
            });
            AnimatorSet animatorSet = new AnimatorSet();
            ValueAnimator ofFloat3 = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PhotoViewer.this.lambda$switchToPip$48(valueAnimator);
                }
            });
            animatorSet.playTogether(ofFloat3, ObjectAnimator.ofFloat(this.textureImageView, View.SCALE_X, width), ObjectAnimator.ofFloat(this.textureImageView, View.SCALE_Y, width), ObjectAnimator.ofFloat(this.videoTextureView, View.SCALE_X, width), ObjectAnimator.ofFloat(this.videoTextureView, View.SCALE_Y, width), ObjectAnimator.ofInt(this.backgroundDrawable, (Property<BackgroundDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, 0), ofFloat2);
            if (z) {
                animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                animatorSet.setDuration(300L);
            } else {
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(250L);
            }
            animatorSet.addListener(new AnonymousClass42());
            animatorSet.start();
            if (!z) {
                toggleActionBar(false, true, new ActionBarToggleParams().enableStatusBarAnimation(false).enableTranslationAnimation(false).animationDuration(250).animationInterpolator(new DecelerateInterpolator()));
            }
        } else {
            this.switchToInlineRunnable.run();
            dismissInternal();
        }
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity == null) {
            return;
        }
        chatActivity.getFragmentView().invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$switchToPip$46(ImageReceiver imageReceiver, ValueAnimator valueAnimator) {
        imageReceiver.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$switchToPip$47(CubicBezierInterpolator cubicBezierInterpolator, float f, float f2, float f3, float f4, float f5, float f6, float f7, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float interpolation = cubicBezierInterpolator == null ? floatValue : cubicBezierInterpolator.getInterpolation(floatValue);
        ImageView imageView = this.textureImageView;
        if (imageView != null) {
            imageView.setTranslationX(((1.0f - floatValue) * f) + (f2 * floatValue));
            this.textureImageView.setTranslationY((f3 * (1.0f - interpolation)) + (f4 * interpolation));
            this.textureImageView.invalidateOutline();
        }
        TextureView textureView = this.videoTextureView;
        if (textureView != null) {
            textureView.setTranslationX((f * (1.0f - floatValue)) + (f5 * floatValue));
            this.videoTextureView.setTranslationY((f6 * (1.0f - interpolation)) + (f7 * interpolation));
            this.videoTextureView.invalidateOutline();
        }
        FirstFrameView firstFrameView = this.firstFrameView;
        if (firstFrameView != null) {
            firstFrameView.setTranslationX(this.videoTextureView.getTranslationX());
            this.firstFrameView.setTranslationY(this.videoTextureView.getTranslationY());
            this.firstFrameView.setScaleX(this.videoTextureView.getScaleX());
            this.firstFrameView.setScaleY(this.videoTextureView.getScaleY());
            this.firstFrameView.invalidateOutline();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$switchToPip$48(ValueAnimator valueAnimator) {
        this.clippingImageProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$42  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass42 extends AnimatorListenerAdapter {
        AnonymousClass42() {
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            PhotoViewer.this.pipAnimationInProgress = false;
            PhotoViewer.this.switchToInlineRunnable.run();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$42$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass42.this.lambda$onAnimationEnd$0();
                }
            }, 100L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAnimationEnd$0() {
            if (PhotoViewer.this.videoTextureView != null) {
                PhotoViewer.this.videoTextureView.setOutlineProvider(null);
            }
            if (PhotoViewer.this.textureImageView != null) {
                PhotoViewer.this.textureImageView.setOutlineProvider(null);
            }
            if (PhotoViewer.this.firstFrameView != null) {
                PhotoViewer.this.firstFrameView.setOutlineProvider(null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean cropMirror() {
        if (this.imageMoveAnimation != null || this.photoCropView == null) {
            return false;
        }
        this.mirror = 0.0f;
        this.animateToMirror = 1.0f;
        this.animationStartTime = System.currentTimeMillis();
        AnimatorSet animatorSet = new AnimatorSet();
        this.imageMoveAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f));
        this.imageMoveAnimation.setDuration(250L);
        this.imageMoveAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.43
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                PhotoViewer.this.imageMoveAnimation = null;
                if (PhotoViewer.this.photoCropView == null) {
                    return;
                }
                if (PhotoViewer.this.photoCropView.mirror()) {
                    PhotoViewer.this.mirrorItem.setColorFilter(new PorterDuffColorFilter(PhotoViewer.this.getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
                } else {
                    PhotoViewer.this.mirrorItem.setColorFilter((ColorFilter) null);
                }
                PhotoViewer photoViewer = PhotoViewer.this;
                photoViewer.mirror = photoViewer.animateToMirror = 0.0f;
                PhotoViewer.this.containerView.invalidate();
            }
        });
        this.imageMoveAnimation.start();
        return !this.photoCropView.cropView.isMirrored();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean cropRotate(float f) {
        return cropRotate(f, false, null);
    }

    private boolean cropRotate(final float f, boolean z, final Runnable runnable) {
        PhotoCropView photoCropView;
        if (this.imageMoveAnimation == null && (photoCropView = this.photoCropView) != null) {
            photoCropView.cropView.maximize(true);
            this.rotate = 0.0f;
            this.animateToRotate = 0.0f + f;
            if (z) {
                this.mirror = 0.0f;
                this.animateToMirror = 1.0f;
            }
            this.animationStartTime = System.currentTimeMillis();
            this.imageMoveAnimation = new AnimatorSet();
            ImageReceiver imageReceiver = this.centerImage;
            if (imageReceiver != null) {
                int bitmapWidth = imageReceiver.getBitmapWidth();
                int bitmapHeight = this.centerImage.getBitmapHeight();
                if (Math.abs((((int) this.photoCropView.cropView.getStateOrientation()) / 90) % 2) == 1) {
                    bitmapHeight = bitmapWidth;
                    bitmapWidth = bitmapHeight;
                }
                MediaController.CropState cropState = this.editState.cropState;
                if (cropState != null) {
                    bitmapWidth = (int) (bitmapWidth * cropState.cropPw);
                    bitmapHeight = (int) (bitmapHeight * cropState.cropPh);
                }
                float f2 = bitmapWidth;
                float f3 = bitmapHeight;
                float min = Math.min(getContainerViewWidth(1) / f2, getContainerViewHeight(1) / f3);
                this.animateToScale = (Math.abs((f / 90.0f) % 2.0f) == 1.0f ? Math.min(getContainerViewWidth(1) / f3, getContainerViewHeight(1) / f2) : min) / min;
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            final float rotation = this.photoCropView.wheelView.getRotation();
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda7
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PhotoViewer.this.lambda$cropRotate$49(f, rotation, valueAnimator);
                }
            });
            this.imageMoveAnimation.playTogether(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f), ofFloat);
            this.imageMoveAnimation.setDuration(250L);
            this.imageMoveAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.44
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    PhotoViewer.this.imageMoveAnimation = null;
                    PhotoViewer photoViewer = PhotoViewer.this;
                    photoViewer.rotate = photoViewer.animateToRotate = 0.0f;
                    PhotoViewer photoViewer2 = PhotoViewer.this;
                    photoViewer2.mirror = photoViewer2.animateToMirror = 0.0f;
                    PhotoViewer photoViewer3 = PhotoViewer.this;
                    photoViewer3.scale = photoViewer3.animateToScale = 1.0f;
                    PhotoViewer.this.containerView.invalidate();
                    PhotoViewer.this.photoCropView.cropView.areaView.setRotationScaleTranslation(0.0f, 1.0f, 0.0f, 0.0f);
                    PhotoViewer.this.photoCropView.wheelView.setRotated(false);
                    if (Math.abs(f) > 0.0f) {
                        if (PhotoViewer.this.photoCropView.rotate(f)) {
                            PhotoViewer.this.rotateItem.setColorFilter(new PorterDuffColorFilter(PhotoViewer.this.getThemedColor("dialogFloatingButton"), PorterDuff.Mode.MULTIPLY));
                        } else {
                            PhotoViewer.this.rotateItem.setColorFilter((ColorFilter) null);
                        }
                    }
                    if (PhotoViewer.this.editState.cropState != null) {
                        MediaController.CropState cropState2 = PhotoViewer.this.editState.cropState;
                        PhotoViewer.this.editState.cropState.cropPy = 0.0f;
                        cropState2.cropPx = 0.0f;
                        MediaController.CropState cropState3 = PhotoViewer.this.editState.cropState;
                        PhotoViewer.this.editState.cropState.cropPh = 1.0f;
                        cropState3.cropPw = 1.0f;
                    }
                    Runnable runnable2 = runnable;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                }
            });
            this.imageMoveAnimation.start();
            return Math.abs(this.photoCropView.cropView.getStateOrientation() + f) > 0.01f;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cropRotate$49(float f, float f2, ValueAnimator valueAnimator) {
        CropAreaView cropAreaView = this.photoCropView.cropView.areaView;
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue() * f;
        float f3 = this.scale;
        cropAreaView.setRotationScaleTranslation(floatValue, f3 + ((this.animateToScale - f3) * this.animationValue), 0.0f, 0.0f);
        this.photoCropView.wheelView.setRotation(AndroidUtilities.lerp(f2, 0.0f, ((Float) valueAnimator.getAnimatedValue()).floatValue()), false);
    }

    public VideoPlayer getVideoPlayer() {
        return this.videoPlayer;
    }

    public void exitFromPip() {
        TextureView textureView;
        if (!this.isInline) {
            return;
        }
        if (Instance != null) {
            Instance.closePhoto(false, true);
        }
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView != null) {
            photoViewerWebView.exitFromPip();
        }
        Instance = PipInstance;
        PipInstance = null;
        if (this.photoViewerWebView == null) {
            this.switchingInlineMode = true;
            Bitmap bitmap = this.currentBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.currentBitmap = null;
            }
            this.changingTextureView = true;
        }
        this.isInline = false;
        if (this.photoViewerWebView == null && (textureView = this.videoTextureView) != null) {
            if (textureView.getParent() != null) {
                ((ViewGroup) this.videoTextureView.getParent()).removeView(this.videoTextureView);
            }
            this.videoTextureView.setVisibility(4);
            this.aspectRatioFrameLayout.addView(this.videoTextureView);
        }
        if (ApplicationLoader.mainInterfacePaused) {
            try {
                this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, BringAppForegroundService.class));
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        if (this.photoViewerWebView == null) {
            if (Build.VERSION.SDK_INT >= 21 && this.videoTextureView != null) {
                this.pipAnimationInProgress = true;
                org.telegram.ui.Components.Rect pipRect = PipVideoOverlay.getPipRect(false, this.aspectRatioFrameLayout.getAspectRatio());
                final float f = pipRect.width / this.textureImageView.getLayoutParams().width;
                this.textureImageView.setScaleX(f);
                this.textureImageView.setScaleY(f);
                this.textureImageView.setTranslationX(pipRect.x);
                this.textureImageView.setTranslationY(pipRect.y);
                this.videoTextureView.setScaleX(f);
                this.videoTextureView.setScaleY(f);
                this.videoTextureView.setTranslationX(pipRect.x - this.aspectRatioFrameLayout.getX());
                this.videoTextureView.setTranslationY(pipRect.y - this.aspectRatioFrameLayout.getY());
                FirstFrameView firstFrameView = this.firstFrameView;
                if (firstFrameView != null) {
                    firstFrameView.setScaleX(f);
                    this.firstFrameView.setScaleY(f);
                    this.firstFrameView.setTranslationX(this.videoTextureView.getTranslationX());
                    this.firstFrameView.setTranslationY(this.videoTextureView.getTranslationY());
                }
                this.inlineOutAnimationProgress = 0.0f;
                ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() { // from class: org.telegram.ui.PhotoViewer.45
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), (1.0f - PhotoViewer.this.inlineOutAnimationProgress) * AndroidUtilities.dp(10.0f) * (1.0f / f));
                    }
                };
                this.videoTextureView.setOutlineProvider(viewOutlineProvider);
                this.videoTextureView.setClipToOutline(true);
                this.textureImageView.setOutlineProvider(viewOutlineProvider);
                this.textureImageView.setClipToOutline(true);
                FirstFrameView firstFrameView2 = this.firstFrameView;
                if (firstFrameView2 != null) {
                    firstFrameView2.setOutlineProvider(viewOutlineProvider);
                    this.firstFrameView.setClipToOutline(true);
                }
            } else {
                PipVideoOverlay.dismiss(true);
            }
        } else {
            this.clippingImageProgress = 0.0f;
        }
        try {
            this.isVisible = true;
            ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
            onShowView();
            PlaceProviderObject placeProviderObject = this.currentPlaceObject;
            if (placeProviderObject != null) {
                placeProviderObject.imageReceiver.setVisible(false, false);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        this.waitingForDraw = 4;
    }

    private void onShowView() {
        Activity activity = this.parentActivity;
        if (activity instanceof LaunchActivity) {
            ((LaunchActivity) activity).addOnUserLeaveHintListener(this.onUserLeaveHintListener);
        }
    }

    private void onHideView() {
        Activity activity = this.parentActivity;
        if (activity instanceof LaunchActivity) {
            ((LaunchActivity) activity).removeOnUserLeaveHintListener(this.onUserLeaveHintListener);
        }
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getFragmentView() == null) {
            return;
        }
        this.clippingImageProgress = 1.0f;
        View fragmentView = this.parentFragment.getFragmentView();
        fragmentView.setScaleX(1.0f);
        fragmentView.setScaleY(1.0f);
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        if (chatAttachAlert == null) {
            return;
        }
        FrameLayout container = chatAttachAlert.getContainer();
        container.setScaleX(1.0f);
        container.setScaleY(1.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserLeaveHint() {
        if (this.pipItem.getAlpha() != 1.0f || !AndroidUtilities.checkInlinePermissions(this.parentActivity) || PipVideoOverlay.isVisible() || !this.isPlaying) {
            return;
        }
        if (this.isEmbedVideo) {
            PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
            if (photoViewerWebView == null || photoViewerWebView.isInAppOnly() || !this.photoViewerWebView.openInPip()) {
                return;
            }
            this.pipVideoOverlayAnimateFlag = false;
            if (PipInstance != null) {
                PipInstance.destroyPhotoViewer();
            }
            this.isInline = true;
            PipInstance = Instance;
            Instance = null;
            this.isVisible = false;
            PlaceProviderObject placeProviderObject = this.currentPlaceObject;
            if (placeProviderObject != null && !placeProviderObject.imageReceiver.getVisible()) {
                this.currentPlaceObject.imageReceiver.setVisible(true, true);
            }
            this.clippingImageProgress = 1.0f;
            this.containerView.invalidate();
            dismissInternal();
            return;
        }
        this.pipVideoOverlayAnimateFlag = false;
        switchToPip(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVideoSeekPreviewPosition() {
        int thumbX = (this.videoPlayerSeekbar.getThumbX() + AndroidUtilities.dp(2.0f)) - (this.videoPreviewFrame.getMeasuredWidth() / 2);
        int dp = AndroidUtilities.dp(10.0f);
        int measuredWidth = (this.videoPlayerControlFrameLayout.getMeasuredWidth() - AndroidUtilities.dp(10.0f)) - (this.videoPreviewFrame.getMeasuredWidth() / 2);
        if (thumbX < dp) {
            thumbX = dp;
        } else if (thumbX >= measuredWidth) {
            thumbX = measuredWidth;
        }
        this.videoPreviewFrame.setTranslationX(thumbX);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showVideoSeekPreviewPosition(boolean z) {
        PhotoViewerWebView photoViewerWebView;
        if (!z || this.videoPreviewFrame.getTag() == null) {
            if (!z && this.videoPreviewFrame.getTag() == null) {
                return;
            }
            if (z && !this.videoPreviewFrame.isReady() && ((photoViewerWebView = this.photoViewerWebView) == null || !photoViewerWebView.isYouTube() || !this.photoViewerWebView.hasYoutubeStoryboards())) {
                this.needShowOnReady = true;
                return;
            }
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
            this.videoPreviewFrameAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.46
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    PhotoViewer.this.videoPreviewFrameAnimation = null;
                }
            });
            this.videoPreviewFrameAnimation.setDuration(180L);
            this.videoPreviewFrameAnimation.start();
        }
    }

    private void createVideoControlsInterface() {
        VideoPlayerControlFrameLayout videoPlayerControlFrameLayout = new VideoPlayerControlFrameLayout(this.containerView.getContext());
        this.videoPlayerControlFrameLayout = videoPlayerControlFrameLayout;
        this.containerView.addView(videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, 48, 83));
        final VideoPlayerSeekBar.SeekBarDelegate seekBarDelegate = new VideoPlayerSeekBar.SeekBarDelegate() { // from class: org.telegram.ui.PhotoViewer.47
            @Override // org.telegram.ui.Components.VideoPlayerSeekBar.SeekBarDelegate
            public void onSeekBarDrag(float f) {
                if (PhotoViewer.this.videoPlayer != null || (PhotoViewer.this.photoViewerWebView != null && PhotoViewer.this.photoViewerWebView.isControllable())) {
                    if (!PhotoViewer.this.inPreview && PhotoViewer.this.videoTimelineView.getVisibility() == 0) {
                        f = PhotoViewer.this.videoTimelineView.getLeftProgress() + ((PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress()) * f);
                    }
                    long videoDuration = PhotoViewer.this.getVideoDuration();
                    if (videoDuration == -9223372036854775807L) {
                        PhotoViewer.this.seekToProgressPending = f;
                    } else {
                        PhotoViewer.this.seekVideoOrWebTo((int) (f * ((float) videoDuration)));
                    }
                    PhotoViewer.this.showVideoSeekPreviewPosition(false);
                    PhotoViewer.this.needShowOnReady = false;
                }
            }

            @Override // org.telegram.ui.Components.VideoPlayerSeekBar.SeekBarDelegate
            public void onSeekBarContinuousDrag(float f) {
                if (PhotoViewer.this.photoViewerWebView == null || !PhotoViewer.this.photoViewerWebView.isYouTube() || PhotoViewer.this.videoPreviewFrame == null) {
                    if (PhotoViewer.this.videoPlayer != null && PhotoViewer.this.videoPreviewFrame != null) {
                        PhotoViewer.this.videoPreviewFrame.setProgress(f, PhotoViewer.this.videoPlayerSeekbar.getWidth());
                    }
                } else {
                    PhotoViewer.this.videoPreviewFrame.setProgressForYouTube(PhotoViewer.this.photoViewerWebView, f, PhotoViewer.this.videoPlayerSeekbar.getWidth());
                }
                PhotoViewer.this.showVideoSeekPreviewPosition(true);
                PhotoViewer.this.updateVideoSeekPreviewPosition();
            }
        };
        FloatSeekBarAccessibilityDelegate floatSeekBarAccessibilityDelegate = new FloatSeekBarAccessibilityDelegate() { // from class: org.telegram.ui.PhotoViewer.48
            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public float getProgress() {
                return PhotoViewer.this.videoPlayerSeekbar.getProgress();
            }

            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public void setProgress(float f) {
                seekBarDelegate.onSeekBarDrag(f);
                PhotoViewer.this.videoPlayerSeekbar.setProgress(f);
                PhotoViewer.this.videoPlayerSeekbarView.invalidate();
            }

            @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
            /* renamed from: getContentDescription  reason: collision with other method in class */
            public String mo2214getContentDescription(View view) {
                return LocaleController.formatString("AccDescrPlayerDuration", R.string.AccDescrPlayerDuration, LocaleController.formatPluralString("Minutes", PhotoViewer.this.videoPlayerCurrentTime[0], new Object[0]) + ' ' + LocaleController.formatPluralString("Seconds", PhotoViewer.this.videoPlayerCurrentTime[1], new Object[0]), LocaleController.formatPluralString("Minutes", PhotoViewer.this.videoPlayerTotalTime[0], new Object[0]) + ' ' + LocaleController.formatPluralString("Seconds", PhotoViewer.this.videoPlayerTotalTime[1], new Object[0]));
            }
        };
        View view = new View(this.containerView.getContext()) { // from class: org.telegram.ui.PhotoViewer.49
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                PhotoViewer.this.videoPlayerSeekbar.draw(canvas, this);
            }
        };
        this.videoPlayerSeekbarView = view;
        view.setAccessibilityDelegate(floatSeekBarAccessibilityDelegate);
        this.videoPlayerSeekbarView.setImportantForAccessibility(1);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerSeekbarView, LayoutHelper.createFrame(-1, -1.0f));
        VideoPlayerSeekBar videoPlayerSeekBar = new VideoPlayerSeekBar(this.videoPlayerSeekbarView);
        this.videoPlayerSeekbar = videoPlayerSeekBar;
        videoPlayerSeekBar.setHorizontalPadding(AndroidUtilities.dp(2.0f));
        this.videoPlayerSeekbar.setColors(NUM, NUM, -1, -1, -1, NUM);
        this.videoPlayerSeekbar.setDelegate(seekBarDelegate);
        VideoSeekPreviewImage videoSeekPreviewImage = new VideoSeekPreviewImage(this.containerView.getContext(), new VideoSeekPreviewImage.VideoSeekPreviewImageDelegate() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda85
            @Override // org.telegram.ui.Components.VideoSeekPreviewImage.VideoSeekPreviewImageDelegate
            public final void onReady() {
                PhotoViewer.this.lambda$createVideoControlsInterface$50();
            }
        }) { // from class: org.telegram.ui.PhotoViewer.50
            @Override // android.view.View
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                PhotoViewer.this.updateVideoSeekPreviewPosition();
            }

            @Override // android.view.View
            public void setVisibility(int i) {
                super.setVisibility(i);
                if (i == 0) {
                    PhotoViewer.this.updateVideoSeekPreviewPosition();
                }
            }
        };
        this.videoPreviewFrame = videoSeekPreviewImage;
        videoSeekPreviewImage.setAlpha(0.0f);
        this.containerView.addView(this.videoPreviewFrame, LayoutHelper.createFrame(-2, -2.0f, 83, 0.0f, 0.0f, 0.0f, 58.0f));
        SimpleTextView simpleTextView = new SimpleTextView(this.containerView.getContext());
        this.videoPlayerTime = simpleTextView;
        simpleTextView.setTextColor(-1);
        this.videoPlayerTime.setGravity(53);
        this.videoPlayerTime.setTextSize(14);
        this.videoPlayerTime.setImportantForAccessibility(2);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 15.0f, 12.0f, 0.0f));
        ImageView imageView = new ImageView(this.containerView.getContext());
        this.exitFullscreenButton = imageView;
        imageView.setImageResource(R.drawable.msg_minvideo);
        this.exitFullscreenButton.setContentDescription(LocaleController.getString("AccExitFullscreen", R.string.AccExitFullscreen));
        this.exitFullscreenButton.setScaleType(ImageView.ScaleType.CENTER);
        this.exitFullscreenButton.setBackground(Theme.createSelectorDrawable(NUM));
        this.exitFullscreenButton.setVisibility(4);
        this.videoPlayerControlFrameLayout.addView(this.exitFullscreenButton, LayoutHelper.createFrame(48, 48, 53));
        this.exitFullscreenButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda22
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                PhotoViewer.this.lambda$createVideoControlsInterface$51(view2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createVideoControlsInterface$50() {
        if (this.needShowOnReady) {
            showVideoSeekPreviewPosition(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createVideoControlsInterface$51(View view) {
        Activity activity = this.parentActivity;
        if (activity == null) {
            return;
        }
        this.wasRotated = false;
        this.fullscreenedByButton = 2;
        if (this.prevOrientation == -10) {
            this.prevOrientation = activity.getRequestedOrientation();
        }
        this.parentActivity.setRequestedOrientation(1);
    }

    private void openCaptionEnter() {
        int i;
        if (this.imageMoveAnimation != null || this.changeModeAnimation != null || this.currentEditMode != 0 || (i = this.sendPhotoType) == 1 || i == 3 || i == 10) {
            return;
        }
        if (!this.windowView.isFocusable()) {
            makeFocusable();
        }
        this.keyboardAnimationEnabled = true;
        this.selectedPhotosListView.setEnabled(false);
        this.photosCounterView.setRotationX(0.0f);
        this.isPhotosListViewVisible = false;
        if (this.captionEditText.getMessageEditText() != null) {
            this.captionEditText.getMessageEditText().invalidateEffects();
            this.captionEditText.getMessageEditText().setText(this.captionEditText.getMessageEditText().getText());
        }
        this.captionEditText.setTag(1);
        this.captionEditText.openKeyboard();
        this.captionEditText.setImportantForAccessibility(0);
        this.lastTitle = this.actionBar.getTitle();
        this.captionEditText.setVisibility(0);
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0067, code lost:
        if (r4 != null) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int[] fixVideoWidthHeight(int r8, int r9) {
        /*
            r7 = this;
            java.lang.String r0 = "video/avc"
            r1 = 2
            int[] r1 = new int[r1]
            r2 = 0
            r1[r2] = r8
            r3 = 1
            r1[r3] = r9
            int r4 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r4 < r5) goto L6a
            r4 = 0
            android.media.MediaCodec r4 = android.media.MediaCodec.createEncoderByType(r0)     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            android.media.MediaCodecInfo r5 = r4.getCodecInfo()     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            android.media.MediaCodecInfo$CodecCapabilities r0 = r5.getCapabilitiesForType(r0)     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            android.media.MediaCodecInfo$VideoCapabilities r0 = r0.getVideoCapabilities()     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            android.util.Range r5 = r0.getSupportedWidths()     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            android.util.Range r0 = r0.getSupportedHeights()     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            java.lang.Comparable r5 = r5.getLower()     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            java.lang.Integer r5 = (java.lang.Integer) r5     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            int r5 = r5.intValue()     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            float r8 = (float) r8     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            r6 = 1098907648(0x41800000, float:16.0)
            float r8 = r8 / r6
            int r8 = java.lang.Math.round(r8)     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            int r8 = r8 * 16
            int r8 = java.lang.Math.max(r5, r8)     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            r1[r2] = r8     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            java.lang.Comparable r8 = r0.getLower()     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            java.lang.Integer r8 = (java.lang.Integer) r8     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            int r8 = r8.intValue()     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            float r9 = (float) r9     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            float r9 = r9 / r6
            int r9 = java.lang.Math.round(r9)     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            int r9 = r9 * 16
            int r8 = java.lang.Math.max(r8, r9)     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
            r1[r3] = r8     // Catch: java.lang.Throwable -> L60 java.lang.Exception -> L67
        L5c:
            r4.release()     // Catch: java.lang.Exception -> L6a
            goto L6a
        L60:
            r8 = move-exception
            if (r4 == 0) goto L66
            r4.release()     // Catch: java.lang.Exception -> L66
        L66:
            throw r8
        L67:
            if (r4 == 0) goto L6a
            goto L5c
        L6a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.fixVideoWidthHeight(int, int):int[]");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public VideoEditedInfo getCurrentVideoEditedInfo() {
        int i;
        long j;
        int i2 = -1;
        boolean z = false;
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = null;
        if (!this.isCurrentVideo && hasAnimatedMediaEntities() && this.centerImage.getBitmapWidth() > 0) {
            float f = this.sendPhotoType == 1 ? 800.0f : 854.0f;
            VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
            videoEditedInfo.startTime = 0L;
            videoEditedInfo.start = (float) 0;
            videoEditedInfo.endTime = Math.min(3000L, this.editState.averageDuration);
            while (true) {
                j = videoEditedInfo.endTime;
                if (j <= 0 || j >= 1000) {
                    break;
                }
                videoEditedInfo.endTime = j * 2;
            }
            videoEditedInfo.end = (float) j;
            videoEditedInfo.rotationValue = 0;
            videoEditedInfo.originalPath = this.currentImagePath;
            videoEditedInfo.estimatedSize = (int) ((((float) j) / 1000.0f) * 115200.0f);
            videoEditedInfo.estimatedDuration = j;
            videoEditedInfo.framerate = 30;
            videoEditedInfo.originalDuration = j;
            EditState editState = this.editState;
            videoEditedInfo.filterState = editState.savedFilterState;
            String str = editState.croppedPaintPath;
            if (str != null) {
                videoEditedInfo.paintPath = str;
                ArrayList<VideoEditedInfo.MediaEntity> arrayList2 = editState.croppedMediaEntities;
                if (arrayList2 != null && !arrayList2.isEmpty()) {
                    arrayList = this.editState.croppedMediaEntities;
                }
                videoEditedInfo.mediaEntities = arrayList;
            } else {
                videoEditedInfo.paintPath = editState.paintPath;
                videoEditedInfo.mediaEntities = editState.mediaEntities;
            }
            videoEditedInfo.isPhoto = true;
            int bitmapWidth = this.centerImage.getBitmapWidth();
            int bitmapHeight = this.centerImage.getBitmapHeight();
            MediaController.CropState cropState = this.editState.cropState;
            if (cropState != null) {
                int i3 = cropState.transformRotation;
                if (i3 == 90 || i3 == 270) {
                    bitmapHeight = bitmapWidth;
                    bitmapWidth = bitmapHeight;
                }
                bitmapWidth = (int) (bitmapWidth * cropState.cropPw);
                bitmapHeight = (int) (bitmapHeight * cropState.cropPh);
            }
            if (this.sendPhotoType == 1) {
                bitmapWidth = bitmapHeight;
            }
            float f2 = bitmapWidth;
            float f3 = bitmapHeight;
            float max = Math.max(f2 / f, f3 / f);
            if (max < 1.0f) {
                max = 1.0f;
            }
            int i4 = (int) (f2 / max);
            int i5 = (int) (f3 / max);
            if (i4 % 16 != 0) {
                i4 = Math.max(1, Math.round(i4 / 16.0f)) * 16;
            }
            if (i5 % 16 != 0) {
                i5 = Math.max(1, Math.round(i5 / 16.0f)) * 16;
            }
            videoEditedInfo.resultWidth = i4;
            videoEditedInfo.originalWidth = i4;
            videoEditedInfo.resultHeight = i5;
            videoEditedInfo.originalHeight = i5;
            videoEditedInfo.bitrate = -1;
            videoEditedInfo.muted = true;
            videoEditedInfo.avatarStartTime = 0L;
            return videoEditedInfo;
        } else if (!this.isCurrentVideo || this.currentPlayingVideoFile == null || this.compressionsCount == 0) {
            return null;
        } else {
            VideoEditedInfo videoEditedInfo2 = new VideoEditedInfo();
            videoEditedInfo2.startTime = this.startTime;
            videoEditedInfo2.endTime = this.endTime;
            videoEditedInfo2.start = this.videoCutStart;
            videoEditedInfo2.end = this.videoCutEnd;
            videoEditedInfo2.rotationValue = this.rotationValue;
            videoEditedInfo2.originalWidth = this.originalWidth;
            videoEditedInfo2.originalHeight = this.originalHeight;
            videoEditedInfo2.bitrate = this.bitrate;
            videoEditedInfo2.originalPath = this.currentPathObject;
            long j2 = this.estimatedSize;
            if (j2 == 0) {
                j2 = 1;
            }
            videoEditedInfo2.estimatedSize = j2;
            videoEditedInfo2.estimatedDuration = this.estimatedDuration;
            videoEditedInfo2.framerate = this.videoFramerate;
            videoEditedInfo2.originalDuration = this.videoDuration * 1000.0f;
            EditState editState2 = this.editState;
            videoEditedInfo2.filterState = editState2.savedFilterState;
            String str2 = editState2.croppedPaintPath;
            if (str2 != null) {
                videoEditedInfo2.paintPath = str2;
                ArrayList<VideoEditedInfo.MediaEntity> arrayList3 = editState2.croppedMediaEntities;
                if (arrayList3 != null && !arrayList3.isEmpty()) {
                    arrayList = this.editState.croppedMediaEntities;
                }
                videoEditedInfo2.mediaEntities = arrayList;
            } else {
                videoEditedInfo2.paintPath = editState2.paintPath;
                ArrayList<VideoEditedInfo.MediaEntity> arrayList4 = editState2.mediaEntities;
                if (arrayList4 != null && !arrayList4.isEmpty()) {
                    arrayList = this.editState.mediaEntities;
                }
                videoEditedInfo2.mediaEntities = arrayList;
            }
            if (this.sendPhotoType != 1 && !this.muteVideo && (this.compressItem.getTag() == null || (videoEditedInfo2.resultWidth == this.originalWidth && videoEditedInfo2.resultHeight == this.originalHeight))) {
                videoEditedInfo2.resultWidth = this.originalWidth;
                videoEditedInfo2.resultHeight = this.originalHeight;
                if (!this.muteVideo) {
                    i2 = this.originalBitrate;
                }
                videoEditedInfo2.bitrate = i2;
            } else {
                if (this.muteVideo || this.sendPhotoType == 1) {
                    this.selectedCompression = 1;
                    updateWidthHeightBitrateForCompression();
                }
                videoEditedInfo2.resultWidth = this.resultWidth;
                videoEditedInfo2.resultHeight = this.resultHeight;
                if (!this.muteVideo && this.sendPhotoType != 1) {
                    i2 = this.bitrate;
                }
                videoEditedInfo2.bitrate = i2;
            }
            MediaController.CropState cropState2 = this.editState.cropState;
            videoEditedInfo2.cropState = cropState2;
            if (cropState2 != null) {
                videoEditedInfo2.rotationValue += cropState2.transformRotation;
                while (true) {
                    i = videoEditedInfo2.rotationValue;
                    if (i < 360) {
                        break;
                    }
                    videoEditedInfo2.rotationValue = i - 360;
                }
                if (i == 90 || i == 270) {
                    MediaController.CropState cropState3 = videoEditedInfo2.cropState;
                    cropState3.transformWidth = (int) (videoEditedInfo2.resultWidth * cropState3.cropPh);
                    cropState3.transformHeight = (int) (videoEditedInfo2.resultHeight * cropState3.cropPw);
                } else {
                    MediaController.CropState cropState4 = videoEditedInfo2.cropState;
                    cropState4.transformWidth = (int) (videoEditedInfo2.resultWidth * cropState4.cropPw);
                    cropState4.transformHeight = (int) (videoEditedInfo2.resultHeight * cropState4.cropPh);
                }
                if (this.sendPhotoType == 1) {
                    MediaController.CropState cropState5 = videoEditedInfo2.cropState;
                    if (cropState5.transformWidth > 800) {
                        cropState5.transformWidth = 800;
                    }
                    if (cropState5.transformHeight > 800) {
                        cropState5.transformHeight = 800;
                    }
                    int min = Math.min(cropState5.transformWidth, cropState5.transformHeight);
                    cropState5.transformHeight = min;
                    cropState5.transformWidth = min;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("original transformed w = " + videoEditedInfo2.cropState.transformWidth + " h = " + videoEditedInfo2.cropState.transformHeight + " r = " + videoEditedInfo2.rotationValue);
                }
                MediaController.CropState cropState6 = videoEditedInfo2.cropState;
                int[] fixVideoWidthHeight = fixVideoWidthHeight(cropState6.transformWidth, cropState6.transformHeight);
                MediaController.CropState cropState7 = videoEditedInfo2.cropState;
                cropState7.transformWidth = fixVideoWidthHeight[0];
                cropState7.transformHeight = fixVideoWidthHeight[1];
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("fixed transformed w = " + videoEditedInfo2.cropState.transformWidth + " h = " + videoEditedInfo2.cropState.transformHeight);
                }
            }
            int i6 = this.sendPhotoType;
            if (i6 == 1) {
                videoEditedInfo2.avatarStartTime = this.avatarStartTime;
                videoEditedInfo2.originalBitrate = this.originalBitrate;
            }
            if (this.muteVideo || i6 == 1) {
                z = true;
            }
            videoEditedInfo2.muted = z;
            return videoEditedInfo2;
        }
    }

    private boolean supportsSendingNewEntities() {
        TLRPC$EncryptedChat tLRPC$EncryptedChat;
        ChatActivity chatActivity = this.parentChatActivity;
        return chatActivity != null && ((tLRPC$EncryptedChat = chatActivity.currentEncryptedChat) == null || AndroidUtilities.getPeerLayerVersion(tLRPC$EncryptedChat.layer) >= 101);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeCaptionEnter(boolean z) {
        int i = this.currentIndex;
        if (i < 0 || i >= this.imagesArrLocals.size() || this.captionEditText.getTag() == null) {
            return;
        }
        Object obj = this.imagesArrLocals.get(this.currentIndex);
        if (z) {
            CharSequence fieldCharSequence = this.captionEditText.getFieldCharSequence();
            CharSequence[] charSequenceArr = {fieldCharSequence};
            if (this.hasCaptionForAllMedia && !TextUtils.equals(this.captionForAllMedia, fieldCharSequence) && this.placeProvider.getPhotoIndex(this.currentIndex) != 0 && this.placeProvider.getSelectedCount() > 0) {
                this.hasCaptionForAllMedia = false;
            }
            ArrayList<TLRPC$MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities());
            this.captionForAllMedia = fieldCharSequence;
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
            PhotoViewerProvider photoViewerProvider = this.placeProvider;
            if (photoViewerProvider != null) {
                photoViewerProvider.onApplyCaption(fieldCharSequence);
            }
            setCurrentCaption(null, charSequenceArr[0], false);
        }
        this.captionEditText.setTag(null);
        if (this.isCurrentVideo) {
            this.actionBar.setTitleAnimated(this.lastTitle, false, 220L);
            this.actionBar.setSubtitle(this.muteVideo ? LocaleController.getString("SoundMuted", R.string.SoundMuted) : this.currentSubtitle);
        }
        updateCaptionTextForCurrentPhoto(obj);
        if (this.captionEditText.isPopupShowing()) {
            this.captionEditText.hidePopup();
        }
        this.captionEditText.closeKeyboard();
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        this.captionEditText.setImportantForAccessibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVideoPlayerTime() {
        String format;
        String format2;
        Arrays.fill(this.videoPlayerCurrentTime, 0);
        Arrays.fill(this.videoPlayerTotalTime, 0);
        VideoPlayer videoPlayer = this.videoPlayer;
        long j = 0;
        if (videoPlayer != null) {
            long max = Math.max(0L, videoPlayer.getCurrentPosition());
            if (this.shownControlsByEnd && !this.actionBarWasShownBeforeByEnd) {
                max = 0;
            }
            long max2 = Math.max(0L, this.videoPlayer.getDuration());
            if (!this.inPreview && this.videoTimelineView.getVisibility() == 0) {
                max2 = ((float) max2) * (this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress());
                max = ((float) max) - (this.videoTimelineView.getLeftProgress() * ((float) max2));
                if (max > max2) {
                    max = max2;
                }
            }
            long j2 = max / 1000;
            long j3 = max2 / 1000;
            int[] iArr = this.videoPlayerCurrentTime;
            iArr[0] = (int) (j2 / 60);
            iArr[1] = (int) (j2 % 60);
            int[] iArr2 = this.videoPlayerTotalTime;
            iArr2[0] = (int) (j3 / 60);
            iArr2[1] = (int) (j3 % 60);
        } else {
            PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
            if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
                long max3 = Math.max(0, this.photoViewerWebView.getCurrentPosition());
                if (!this.shownControlsByEnd || this.actionBarWasShownBeforeByEnd) {
                    j = max3;
                }
                long max4 = Math.max(0, this.photoViewerWebView.getVideoDuration());
                if (!this.inPreview && this.videoTimelineView.getVisibility() == 0) {
                    max4 = ((float) max4) * (this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress());
                    j = ((float) j) - (this.videoTimelineView.getLeftProgress() * ((float) max4));
                    if (j > max4) {
                        j = max4;
                    }
                }
                long j4 = j / 1000;
                long j5 = max4 / 1000;
                int[] iArr3 = this.videoPlayerCurrentTime;
                iArr3[0] = (int) (j4 / 60);
                iArr3[1] = (int) (j4 % 60);
                int[] iArr4 = this.videoPlayerTotalTime;
                iArr4[0] = (int) (j5 / 60);
                iArr4[1] = (int) (j5 % 60);
            }
        }
        int[] iArr5 = this.videoPlayerCurrentTime;
        if (iArr5[0] > 60) {
            format = String.format(Locale.ROOT, "%02d:%02d:%02d", Integer.valueOf(iArr5[0] / 60), Integer.valueOf(this.videoPlayerCurrentTime[0] % 60), Integer.valueOf(this.videoPlayerCurrentTime[1]));
        } else {
            format = String.format(Locale.ROOT, "%02d:%02d", Integer.valueOf(iArr5[0]), Integer.valueOf(this.videoPlayerCurrentTime[1]));
        }
        int[] iArr6 = this.videoPlayerTotalTime;
        if (iArr6[0] > 60) {
            format2 = String.format(Locale.ROOT, "%02d:%02d:%02d", Integer.valueOf(iArr6[0] / 60), Integer.valueOf(this.videoPlayerTotalTime[0] % 60), Integer.valueOf(this.videoPlayerTotalTime[1]));
        } else {
            format2 = String.format(Locale.ROOT, "%02d:%02d", Integer.valueOf(iArr6[0]), Integer.valueOf(this.videoPlayerTotalTime[1]));
        }
        this.videoPlayerTime.setText(String.format(Locale.ROOT, "%s / %s", format, format2));
        if (!ObjectsCompat$$ExternalSyntheticBackport0.m(this.lastControlFrameDuration, format2)) {
            this.lastControlFrameDuration = format2;
            this.videoPlayerControlFrameLayout.requestLayout();
        }
    }

    private void checkBufferedProgress(float f) {
        MessageObject messageObject;
        TLRPC$Document document;
        if (!this.isStreaming || this.parentActivity == null || this.streamingAlertShown || this.videoPlayer == null || (messageObject = this.currentMessageObject) == null || (document = messageObject.getDocument()) == null || this.currentMessageObject.getDuration() < 20) {
            return;
        }
        boolean z = false;
        boolean z2 = document.size >= 2147483648L;
        if ((DownloadController.getInstance(this.currentAccount).getAutodownloadMask() & 4) != 0) {
            z = true;
        }
        if (!z || f >= 0.9f) {
            return;
        }
        long j = document.size;
        if (((float) j) * f < 5242880.0f && (f < 0.5f || j < 2097152)) {
            return;
        }
        if (Math.abs(SystemClock.elapsedRealtime() - this.startedPlayTime) < (z2 ? 10000 : 3000)) {
            return;
        }
        if (this.videoPlayer.getDuration() == -9223372036854775807L) {
            Toast.makeText(this.parentActivity, LocaleController.getString("VideoDoesNotSupportStreaming", R.string.VideoDoesNotSupportStreaming), 1).show();
        }
        this.streamingAlertShown = true;
    }

    public void updateColors() {
        int themedColor = getThemedColor("dialogFloatingButton");
        ImageView imageView = this.pickerViewSendButton;
        if (imageView != null) {
            Drawable background = imageView.getBackground();
            Theme.setSelectorDrawableColor(background, themedColor, false);
            Theme.setSelectorDrawableColor(background, getThemedColor(Build.VERSION.SDK_INT >= 21 ? "dialogFloatingButtonPressed" : "dialogFloatingButton"), true);
            this.pickerViewSendButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
        }
        CheckBox checkBox = this.checkImageView;
        if (checkBox != null) {
            checkBox.setColor(getThemedColor("dialogFloatingButton"), -1);
        }
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(themedColor, PorterDuff.Mode.MULTIPLY);
        ImageView imageView2 = this.timeItem;
        if (imageView2 != null && imageView2.getColorFilter() != null) {
            this.timeItem.setColorFilter(porterDuffColorFilter);
        }
        ImageView imageView3 = this.paintItem;
        if (imageView3 != null && imageView3.getColorFilter() != null) {
            this.paintItem.setColorFilter(porterDuffColorFilter);
        }
        ImageView imageView4 = this.cropItem;
        if (imageView4 != null && imageView4.getColorFilter() != null) {
            this.cropItem.setColorFilter(porterDuffColorFilter);
        }
        ImageView imageView5 = this.tuneItem;
        if (imageView5 != null && imageView5.getColorFilter() != null) {
            this.tuneItem.setColorFilter(porterDuffColorFilter);
        }
        ImageView imageView6 = this.rotateItem;
        if (imageView6 != null && imageView6.getColorFilter() != null) {
            this.rotateItem.setColorFilter(porterDuffColorFilter);
        }
        ImageView imageView7 = this.mirrorItem;
        if (imageView7 != null && imageView7.getColorFilter() != null) {
            this.mirrorItem.setColorFilter(porterDuffColorFilter);
        }
        PickerBottomLayoutViewer pickerBottomLayoutViewer = this.editorDoneLayout;
        if (pickerBottomLayoutViewer != null) {
            pickerBottomLayoutViewer.doneButton.setTextColor(themedColor);
        }
        PickerBottomLayoutViewer pickerBottomLayoutViewer2 = this.qualityPicker;
        if (pickerBottomLayoutViewer2 != null) {
            pickerBottomLayoutViewer2.doneButton.setTextColor(themedColor);
        }
        PhotoPaintView photoPaintView = this.photoPaintView;
        if (photoPaintView != null) {
            photoPaintView.updateColors();
        }
        PhotoFilterView photoFilterView = this.photoFilterView;
        if (photoFilterView != null) {
            photoFilterView.updateColors();
        }
        PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
        if (photoViewerCaptionEnterView != null) {
            photoViewerCaptionEnterView.updateColors();
        }
        VideoTimelinePlayView videoTimelinePlayView = this.videoTimelineView;
        if (videoTimelinePlayView != null) {
            videoTimelinePlayView.invalidate();
        }
        SelectedPhotosListView selectedPhotosListView = this.selectedPhotosListView;
        if (selectedPhotosListView != null) {
            int childCount = selectedPhotosListView.getChildCount();
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

    public void injectVideoPlayer(VideoPlayer videoPlayer) {
        this.injectingVideoPlayer = videoPlayer;
    }

    public void injectVideoPlayerSurface(SurfaceTexture surfaceTexture) {
        this.injectingVideoPlayerSurface = surfaceTexture;
    }

    public boolean isInjectingVideoPlayer() {
        return this.injectingVideoPlayer != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleActionBarHide() {
        scheduleActionBarHide(3000);
    }

    private void scheduleActionBarHide(int i) {
        if (!isAccessibilityEnabled()) {
            AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
            AndroidUtilities.runOnUIThread(this.hideActionBarRunnable, i);
        }
    }

    private boolean isAccessibilityEnabled() {
        try {
            AccessibilityManager accessibilityManager = (AccessibilityManager) this.activityContext.getSystemService("accessibility");
            if (!accessibilityManager.isEnabled()) {
                return false;
            }
            return accessibilityManager.isTouchExplorationEnabled();
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public void updateWebPlayerState(boolean z, int i) {
        updatePlayerState(z, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePlayerState(boolean z, int i) {
        VideoPlayer videoPlayer;
        MessageObject messageObject;
        TLRPC$MessageMedia tLRPC$MessageMedia;
        TLRPC$WebPage tLRPC$WebPage;
        PhotoViewerWebView photoViewerWebView;
        if (this.videoPlayer != null || ((photoViewerWebView = this.photoViewerWebView) != null && photoViewerWebView.isControllable())) {
            PhotoViewerWebView photoViewerWebView2 = this.photoViewerWebView;
            if (photoViewerWebView2 != null && photoViewerWebView2.isControllable() && !z) {
                toggleActionBar(true, true);
            }
            PhotoViewerWebView photoViewerWebView3 = this.photoViewerWebView;
            float f = 0.0f;
            if (photoViewerWebView3 != null && photoViewerWebView3.isControllable() && i == 3 && getVideoDuration() >= 10000 && this.shouldSavePositionForCurrentVideo == null && this.shouldSavePositionForCurrentVideoShortTerm == null) {
                if (this.currentMessageObject != null) {
                    long videoDuration = getVideoDuration() / 1000;
                    TLRPC$Message tLRPC$Message = this.currentMessageObject.messageOwner;
                    String str = (tLRPC$Message == null || (tLRPC$MessageMedia = tLRPC$Message.media) == null || (tLRPC$WebPage = tLRPC$MessageMedia.webpage) == null) ? null : tLRPC$WebPage.url;
                    if (!TextUtils.isEmpty(str)) {
                        if (videoDuration >= 600) {
                            if (this.currentMessageObject.forceSeekTo < 0.0f) {
                                float f2 = ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).getFloat(str, -1.0f);
                                if (f2 > 0.0f && f2 < 0.999f) {
                                    this.currentMessageObject.forceSeekTo = f2;
                                    this.videoPlayerSeekbar.setProgress(f2);
                                }
                            }
                            this.shouldSavePositionForCurrentVideo = str;
                        } else if (videoDuration >= 10) {
                            SavedVideoPosition savedVideoPosition = null;
                            for (int size = this.savedVideoPositions.size() - 1; size >= 0; size--) {
                                SavedVideoPosition valueAt = this.savedVideoPositions.valueAt(size);
                                if (valueAt.timestamp < SystemClock.elapsedRealtime() - 5000) {
                                    this.savedVideoPositions.removeAt(size);
                                } else if (savedVideoPosition == null && this.savedVideoPositions.keyAt(size).equals(str)) {
                                    savedVideoPosition = valueAt;
                                }
                            }
                            MessageObject messageObject2 = this.currentMessageObject;
                            if (messageObject2.forceSeekTo < 0.0f && savedVideoPosition != null) {
                                float f3 = savedVideoPosition.position;
                                if (f3 > 0.0f && f3 < 0.999f) {
                                    messageObject2.forceSeekTo = f3;
                                    this.videoPlayerSeekbar.setProgress(f3);
                                }
                            }
                            this.shouldSavePositionForCurrentVideoShortTerm = str;
                        }
                    }
                }
                MessageObject messageObject3 = this.currentMessageObject;
                if (messageObject3 != null) {
                    float f4 = messageObject3.forceSeekTo;
                    if (f4 >= 0.0f) {
                        seekVideoOrWebToProgress(f4);
                        this.currentMessageObject.forceSeekTo = -1.0f;
                    }
                }
            }
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
            AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
            int i2 = 4;
            if (aspectRatioFrameLayout != null) {
                aspectRatioFrameLayout.setKeepScreenOn((!z || i == 4 || i == 1) ? false : true);
            }
            if (z && i != 4 && i != 1) {
                try {
                    this.parentActivity.getWindow().addFlags(128);
                    this.keepScreenOnFlagSet = true;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else {
                try {
                    this.parentActivity.getWindow().clearFlags(128);
                    this.keepScreenOnFlagSet = false;
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            if (i == 3 || i == 1) {
                if (this.currentMessageObject != null && (videoPlayer = this.videoPlayer) != null) {
                    this.videoPreviewFrame.open(videoPlayer.getCurrentUri());
                }
                float f5 = this.seekToProgressPending;
                if (f5 != 0.0f) {
                    seekVideoOrWebToProgress(f5);
                    this.seekToProgressPending = 0.0f;
                    MessageObject messageObject4 = this.currentMessageObject;
                    if (messageObject4 != null && !FileLoader.getInstance(messageObject4.currentAccount).isLoadingVideoAny(this.currentMessageObject.getDocument())) {
                        this.skipFirstBufferingProgress = true;
                    }
                }
            }
            if (i == 3) {
                AspectRatioFrameLayout aspectRatioFrameLayout2 = this.aspectRatioFrameLayout;
                if (aspectRatioFrameLayout2 != null && aspectRatioFrameLayout2.getVisibility() != 0) {
                    this.aspectRatioFrameLayout.setVisibility(0);
                }
                if (!this.pipItem.isEnabled() && this.pipItem.getVisibility() == 0) {
                    this.pipAvailable = true;
                    this.pipItem.setEnabled(true);
                    this.pipItem.animate().alpha(1.0f).setDuration(175L).withEndAction(null).start();
                }
                this.playerWasReady = true;
                MessageObject messageObject5 = this.currentMessageObject;
                if (messageObject5 != null && messageObject5.isVideo()) {
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(this.currentMessageObject.currentAccount).removeLoadingVideo(this.currentMessageObject.getDocument(), true, false);
                }
            } else if (i == 2 && z && (messageObject = this.currentMessageObject) != null && messageObject.isVideo()) {
                if (this.playerWasReady) {
                    this.setLoadingRunnable.run();
                } else {
                    AndroidUtilities.runOnUIThread(this.setLoadingRunnable, 1000L);
                }
            }
            VideoPlayer videoPlayer2 = this.videoPlayer;
            if (videoPlayer2 == null ? this.photoViewerWebView.isPlaying() : videoPlayer2.isPlaying()) {
                if (i != 4) {
                    if (!this.isPlaying) {
                        this.isPlaying = true;
                        PhotoProgressView photoProgressView = this.photoProgressViews[0];
                        if (this.isCurrentVideo) {
                            i2 = -1;
                        }
                        photoProgressView.setBackgroundState(i2, false, true);
                        PhotoProgressView photoProgressView2 = this.photoProgressViews[0];
                        if (this.isCurrentVideo || ((isAccessibilityEnabled() && !this.playerWasPlaying) || ((!this.playerAutoStarted || this.playerWasPlaying) && this.isActionBarVisible))) {
                            f = 1.0f;
                        }
                        photoProgressView2.setIndexedAlpha(1, f, false);
                        this.playerWasPlaying = true;
                        AndroidUtilities.runOnUIThread(this.updateProgressRunnable);
                    }
                    PipVideoOverlay.updatePlayButton();
                    this.videoPlayerSeekbar.updateTimestamps(this.currentMessageObject, getVideoDuration());
                    updateVideoPlayerTime();
                }
            }
            if (this.isPlaying || i == 4) {
                if (this.currentEditMode != 3) {
                    this.photoProgressViews[0].setIndexedAlpha(1, 1.0f, i == 4);
                    PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
                    photoProgressViewArr[0].setBackgroundState(3, false, photoProgressViewArr[0].animAlphas[1] > 0.0f);
                }
                this.isPlaying = false;
                AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
                if (i == 4) {
                    if (this.isCurrentVideo) {
                        if (!this.videoTimelineView.isDragging()) {
                            VideoTimelinePlayView videoTimelinePlayView = this.videoTimelineView;
                            videoTimelinePlayView.setProgress(videoTimelinePlayView.getLeftProgress());
                            if (!this.inPreview && (this.currentEditMode != 0 || this.videoTimelineView.getVisibility() == 0)) {
                                seekVideoOrWebToProgress(this.videoTimelineView.getLeftProgress());
                            } else {
                                seekVideoOrWebToProgress(0.0f);
                            }
                            this.manuallyPaused = false;
                            cancelVideoPlayRunnable();
                            if (this.sendPhotoType != 1 && this.currentEditMode == 0 && this.switchingToMode <= 0) {
                                pauseVideoOrWeb();
                            } else {
                                playVideoOrWeb();
                            }
                            this.containerView.invalidate();
                        }
                    } else {
                        this.videoPlayerSeekbar.setProgress(0.0f);
                        this.videoPlayerSeekbarView.invalidate();
                        if (!this.inPreview && this.videoTimelineView.getVisibility() == 0) {
                            seekVideoOrWebToProgress(this.videoTimelineView.getLeftProgress());
                        } else {
                            seekVideoOrWebToProgress(0.0f);
                        }
                        this.manuallyPaused = false;
                        pauseVideoOrWeb();
                        if (!this.isActionBarVisible) {
                            toggleActionBar(true, true);
                        }
                    }
                    PipVideoOverlay.onVideoCompleted();
                }
            }
            PipVideoOverlay.updatePlayButton();
            this.videoPlayerSeekbar.updateTimestamps(this.currentMessageObject, getVideoDuration());
            updateVideoPlayerTime();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playVideoOrWeb() {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.play();
        } else {
            this.photoViewerWebView.playVideo();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pauseVideoOrWeb() {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.pause();
        } else {
            this.photoViewerWebView.pauseVideo();
        }
    }

    private void seekVideoOrWebToProgress(float f) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.seekTo(f * ((float) videoPlayer.getDuration()));
            return;
        }
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        photoViewerWebView.seekTo(f * photoViewerWebView.getVideoDuration());
    }

    private void preparePlayer(Uri uri, boolean z, boolean z2) {
        preparePlayer(uri, z, z2, null);
    }

    private void preparePlayer(Uri uri, boolean z, boolean z2, MediaController.SavedFilterState savedFilterState) {
        boolean z3;
        if (!z2) {
            this.currentPlayingVideoFile = uri;
        }
        if (this.parentActivity == null) {
            return;
        }
        this.streamingAlertShown = false;
        this.startedPlayTime = SystemClock.elapsedRealtime();
        this.currentVideoFinishedLoading = false;
        this.lastBufferedPositionCheck = 0L;
        this.firstAnimationDelay = true;
        this.inPreview = z2;
        releasePlayer(false);
        SavedVideoPosition savedVideoPosition = null;
        if (this.imagesArrLocals.isEmpty()) {
            createVideoTextureView(null);
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
        checkFullscreenButton();
        if (this.orientationEventListener == null) {
            OrientationEventListener orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) { // from class: org.telegram.ui.PhotoViewer.51
                @Override // android.view.OrientationEventListener
                public void onOrientationChanged(int i) {
                    if (PhotoViewer.this.orientationEventListener == null || PhotoViewer.this.aspectRatioFrameLayout == null || PhotoViewer.this.aspectRatioFrameLayout.getVisibility() != 0 || PhotoViewer.this.parentActivity == null || PhotoViewer.this.fullscreenedByButton == 0) {
                        return;
                    }
                    if (PhotoViewer.this.fullscreenedByButton != 1) {
                        if (i <= 0 || (i < 330 && i > 30)) {
                            if (!PhotoViewer.this.wasRotated || i < 240 || i > 300) {
                                return;
                            }
                            PhotoViewer.this.parentActivity.setRequestedOrientation(PhotoViewer.this.prevOrientation);
                            PhotoViewer.this.fullscreenedByButton = 0;
                            PhotoViewer.this.wasRotated = false;
                            return;
                        }
                        PhotoViewer.this.wasRotated = true;
                    } else if (i < 240 || i > 300) {
                        if (!PhotoViewer.this.wasRotated || i <= 0) {
                            return;
                        }
                        if (i < 330 && i > 30) {
                            return;
                        }
                        PhotoViewer.this.parentActivity.setRequestedOrientation(PhotoViewer.this.prevOrientation);
                        PhotoViewer.this.fullscreenedByButton = 0;
                        PhotoViewer.this.wasRotated = false;
                    } else {
                        PhotoViewer.this.wasRotated = true;
                    }
                }
            };
            this.orientationEventListener = orientationEventListener;
            if (orientationEventListener.canDetectOrientation()) {
                this.orientationEventListener.enable();
            } else {
                this.orientationEventListener.disable();
                this.orientationEventListener = null;
            }
        }
        this.textureUploaded = false;
        this.videoSizeSet = false;
        this.videoCrossfadeStarted = false;
        this.playerWasReady = false;
        this.playerWasPlaying = false;
        this.captureFrameReadyAtTime = -1L;
        this.captureFrameAtTime = -1L;
        this.needCaptureFrameReadyAtTime = -1L;
        if (this.videoPlayer == null) {
            VideoPlayer videoPlayer = this.injectingVideoPlayer;
            if (videoPlayer != null) {
                this.videoPlayer = videoPlayer;
                this.injectingVideoPlayer = null;
                this.playerInjected = true;
                updatePlayerState(videoPlayer.getPlayWhenReady(), this.videoPlayer.getPlaybackState());
                z3 = false;
            } else {
                this.videoPlayer = new VideoPlayer() { // from class: org.telegram.ui.PhotoViewer.52
                    @Override // org.telegram.ui.Components.VideoPlayer
                    public void play() {
                        super.play();
                        PhotoViewer.this.playOrStopAnimatedStickers(true);
                    }

                    @Override // org.telegram.ui.Components.VideoPlayer
                    public void pause() {
                        super.pause();
                        if (PhotoViewer.this.currentEditMode == 0) {
                            PhotoViewer.this.playOrStopAnimatedStickers(false);
                        }
                    }

                    @Override // org.telegram.ui.Components.VideoPlayer
                    public void seekTo(long j) {
                        super.seekTo(j);
                        if (PhotoViewer.this.isCurrentVideo) {
                            PhotoViewer.this.seekAnimatedStickersTo(j);
                        }
                    }
                };
                z3 = true;
            }
            TextureView textureView = this.videoTextureView;
            if (textureView != null) {
                this.videoPlayer.setTextureView(textureView);
            }
            FirstFrameView firstFrameView = this.firstFrameView;
            if (firstFrameView != null) {
                firstFrameView.clear();
            }
            this.videoPlayer.setDelegate(new AnonymousClass53());
        } else {
            z3 = false;
        }
        if (!this.imagesArrLocals.isEmpty()) {
            createVideoTextureView(savedFilterState);
        }
        TextureView textureView2 = this.videoTextureView;
        this.videoCrossfadeAlpha = 0.0f;
        textureView2.setAlpha(0.0f);
        PaintingOverlay paintingOverlay = this.paintingOverlay;
        if (paintingOverlay != null) {
            paintingOverlay.setAlpha(this.videoCrossfadeAlpha);
        }
        this.shouldSavePositionForCurrentVideo = null;
        this.shouldSavePositionForCurrentVideoShortTerm = null;
        this.lastSaveTime = 0L;
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
                    if (duration >= 600) {
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
            this.videoPlayer.preparePlayer(uri, "other");
            this.videoPlayer.setPlayWhenReady(z);
        }
        MessageObject messageObject3 = this.currentMessageObject;
        boolean z4 = messageObject3 != null && messageObject3.getDuration() <= 30;
        this.playerLooping = z4;
        this.videoPlayerControlFrameLayout.setSeekBarTransitionEnabled(z4);
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
        if (tLRPC$BotInlineResult != null && (tLRPC$BotInlineResult.type.equals("video") || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
            this.bottomLayout.setVisibility(0);
            this.bottomLayout.setPadding(0, 0, AndroidUtilities.dp(84.0f), 0);
            this.pickerView.setVisibility(8);
        } else {
            this.bottomLayout.setPadding(0, 0, 0, 0);
        }
        if (this.pageBlocksAdapter != null) {
            this.bottomLayout.setVisibility(0);
        }
        setVideoPlayerControlVisible(!this.isCurrentVideo, true);
        if (!this.isCurrentVideo) {
            scheduleActionBarHide(this.playerAutoStarted ? 3000 : 1000);
        }
        if (this.currentMessageObject != null) {
            this.videoPlayer.setPlaybackSpeed(this.currentVideoSpeed);
        }
        this.inPreview = z2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$53  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass53 implements VideoPlayer.VideoPlayerDelegate {
        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
        }

        AnonymousClass53() {
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onStateChanged(boolean z, int i) {
            PhotoViewer.this.updatePlayerState(z, i);
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onError(VideoPlayer videoPlayer, Exception exc) {
            if (PhotoViewer.this.videoPlayer != videoPlayer) {
                return;
            }
            FileLog.e(exc);
            if (!PhotoViewer.this.menuItem.isSubItemVisible(11)) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(PhotoViewer.this.parentActivity, PhotoViewer.this.resourcesProvider);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("CantPlayVideo", R.string.CantPlayVideo));
            builder.setPositiveButton(LocaleController.getString("Open", R.string.Open), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$53$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PhotoViewer.AnonymousClass53.this.lambda$onError$0(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            PhotoViewer.this.showAlertDialog(builder);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$0(DialogInterface dialogInterface, int i) {
            try {
                AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity, PhotoViewer.this.resourcesProvider);
                PhotoViewer.this.closePhoto(false, false);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onVideoSizeChanged(int i, int i2, int i3, float f) {
            if (PhotoViewer.this.aspectRatioFrameLayout != null) {
                if (i3 == 90 || i3 == 270) {
                    i2 = i;
                    i = i2;
                }
                float f2 = i * f;
                int i4 = (int) f2;
                PhotoViewer.this.videoWidth = i4;
                float f3 = i2;
                PhotoViewer.this.videoHeight = (int) (f * f3);
                PhotoViewer.this.aspectRatioFrameLayout.setAspectRatio(i2 == 0 ? 1.0f : f2 / f3, i3);
                if (PhotoViewer.this.videoTextureView instanceof VideoEditTextureView) {
                    ((VideoEditTextureView) PhotoViewer.this.videoTextureView).setVideoSize(i4, i2);
                    if (PhotoViewer.this.sendPhotoType == 1) {
                        PhotoViewer.this.setCropBitmap();
                    }
                }
                PhotoViewer.this.videoSizeSet = true;
            }
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onRenderedFirstFrame() {
            if (!PhotoViewer.this.textureUploaded) {
                PhotoViewer.this.textureUploaded = true;
                PhotoViewer.this.containerView.invalidate();
            }
            if (PhotoViewer.this.firstFrameView != null) {
                if (PhotoViewer.this.videoPlayer != null && PhotoViewer.this.videoPlayer.isLooping()) {
                    return;
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$53$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass53.this.lambda$onRenderedFirstFrame$1();
                    }
                }, 64L);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onRenderedFirstFrame$1() {
            PhotoViewer.this.firstFrameView.updateAlpha();
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
            if (eventTime.eventPlaybackPositionMs == PhotoViewer.this.needCaptureFrameReadyAtTime) {
                PhotoViewer.this.captureFrameReadyAtTime = eventTime.eventPlaybackPositionMs;
                PhotoViewer.this.needCaptureFrameReadyAtTime = -1L;
                PhotoViewer.this.captureCurrentFrame();
            }
            if (PhotoViewer.this.firstFrameView != null) {
                if (PhotoViewer.this.videoPlayer != null && PhotoViewer.this.videoPlayer.isLooping()) {
                    return;
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$53$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass53.this.lambda$onRenderedFirstFrame$2();
                    }
                }, 64L);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onRenderedFirstFrame$2() {
            PhotoViewer.this.firstFrameView.updateAlpha();
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
            if (PhotoViewer.this.changingTextureView) {
                PhotoViewer.this.changingTextureView = false;
                if (PhotoViewer.this.isInline) {
                    PhotoViewer.this.waitingForFirstTextureUpload = 1;
                    PhotoViewer.this.changedTextureView.setSurfaceTexture(surfaceTexture);
                    PhotoViewer.this.changedTextureView.setSurfaceTextureListener(PhotoViewer.this.surfaceTextureListener);
                    PhotoViewer.this.changedTextureView.setVisibility(0);
                    return true;
                }
            }
            return false;
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            if (PhotoViewer.this.waitingForFirstTextureUpload == 2) {
                if (PhotoViewer.this.textureImageView != null) {
                    PhotoViewer.this.textureImageView.setVisibility(4);
                    PhotoViewer.this.textureImageView.setImageDrawable(null);
                    if (PhotoViewer.this.currentBitmap != null) {
                        PhotoViewer.this.currentBitmap.recycle();
                        PhotoViewer.this.currentBitmap = null;
                    }
                }
                PhotoViewer.this.switchingInlineMode = false;
                if (Build.VERSION.SDK_INT >= 21) {
                    PhotoViewer.this.aspectRatioFrameLayout.getLocationInWindow(PhotoViewer.this.pipPosition);
                    int[] iArr = PhotoViewer.this.pipPosition;
                    iArr[1] = (int) (iArr[1] - PhotoViewer.this.containerView.getTranslationY());
                    if (PhotoViewer.this.textureImageView != null) {
                        PhotoViewer.this.textureImageView.setTranslationX(PhotoViewer.this.textureImageView.getTranslationX() + PhotoViewer.this.getLeftInset());
                    }
                    if (PhotoViewer.this.videoTextureView != null) {
                        PhotoViewer.this.videoTextureView.setTranslationX((PhotoViewer.this.videoTextureView.getTranslationX() + PhotoViewer.this.getLeftInset()) - PhotoViewer.this.aspectRatioFrameLayout.getX());
                    }
                    if (PhotoViewer.this.firstFrameView != null) {
                        PhotoViewer.this.firstFrameView.setTranslationX(PhotoViewer.this.videoTextureView.getTranslationX());
                    }
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$53$$ExternalSyntheticLambda1
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            PhotoViewer.AnonymousClass53.this.lambda$onSurfaceTextureUpdated$3(valueAnimator);
                        }
                    });
                    AnimatorSet animatorSet = new AnimatorSet();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(ofFloat);
                    arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.SCALE_X, 1.0f));
                    arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.SCALE_Y, 1.0f));
                    arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.TRANSLATION_X, PhotoViewer.this.pipPosition[0]));
                    arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.textureImageView, View.TRANSLATION_Y, PhotoViewer.this.pipPosition[1]));
                    arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.SCALE_X, 1.0f));
                    arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.SCALE_Y, 1.0f));
                    arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.TRANSLATION_X, PhotoViewer.this.pipPosition[0] - PhotoViewer.this.aspectRatioFrameLayout.getX()));
                    arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.videoTextureView, View.TRANSLATION_Y, PhotoViewer.this.pipPosition[1] - PhotoViewer.this.aspectRatioFrameLayout.getY()));
                    arrayList.add(ObjectAnimator.ofInt(PhotoViewer.this.backgroundDrawable, (Property<BackgroundDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, 255));
                    if (PhotoViewer.this.firstFrameView != null) {
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.firstFrameView, View.SCALE_X, 1.0f));
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.firstFrameView, View.SCALE_Y, 1.0f));
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.firstFrameView, View.TRANSLATION_X, PhotoViewer.this.pipPosition[0] - PhotoViewer.this.aspectRatioFrameLayout.getX()));
                        arrayList.add(ObjectAnimator.ofFloat(PhotoViewer.this.firstFrameView, View.TRANSLATION_Y, PhotoViewer.this.pipPosition[1] - PhotoViewer.this.aspectRatioFrameLayout.getY()));
                    }
                    float f = PipVideoOverlay.getPipRect(false, PhotoViewer.this.aspectRatioFrameLayout.getAspectRatio()).width;
                    PhotoViewer.this.videoTextureView.getWidth();
                    ValueAnimator ofFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
                    ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$53$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            PhotoViewer.AnonymousClass53.this.lambda$onSurfaceTextureUpdated$4(valueAnimator);
                        }
                    });
                    arrayList.add(ofFloat2);
                    animatorSet.playTogether(arrayList);
                    DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
                    animatorSet.setInterpolator(decelerateInterpolator);
                    animatorSet.setDuration(250L);
                    animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.53.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            PhotoViewer.this.pipAnimationInProgress = false;
                            if (PhotoViewer.this.videoTextureView != null) {
                                PhotoViewer.this.videoTextureView.setOutlineProvider(null);
                            }
                            if (PhotoViewer.this.textureImageView != null) {
                                PhotoViewer.this.textureImageView.setOutlineProvider(null);
                            }
                            if (PhotoViewer.this.firstFrameView != null) {
                                PhotoViewer.this.firstFrameView.setOutlineProvider(null);
                            }
                        }
                    });
                    animatorSet.start();
                    PhotoViewer.this.toggleActionBar(true, true, new ActionBarToggleParams().enableStatusBarAnimation(false).enableTranslationAnimation(false).animationDuration(250).animationInterpolator(decelerateInterpolator));
                } else {
                    PhotoViewer.this.toggleActionBar(true, false);
                }
                PhotoViewer.this.waitingForFirstTextureUpload = 0;
            }
            if (PhotoViewer.this.firstFrameView != null) {
                PhotoViewer.this.firstFrameView.checkFromPlayer(PhotoViewer.this.videoPlayer);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSurfaceTextureUpdated$3(ValueAnimator valueAnimator) {
            PhotoViewer.this.clippingImageProgress = 1.0f - ((Float) valueAnimator.getAnimatedValue()).floatValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSurfaceTextureUpdated$4(ValueAnimator valueAnimator) {
            PhotoViewer.this.inlineOutAnimationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            if (PhotoViewer.this.videoTextureView != null) {
                PhotoViewer.this.videoTextureView.invalidateOutline();
            }
            if (PhotoViewer.this.textureImageView != null) {
                PhotoViewer.this.textureImageView.invalidateOutline();
            }
            if (PhotoViewer.this.firstFrameView != null) {
                PhotoViewer.this.firstFrameView.invalidateOutline();
            }
        }
    }

    public void checkFullscreenButton() {
        int measuredWidth;
        TextureView textureView;
        int measuredHeight;
        TextureView textureView2;
        float f;
        MessageObject messageObject;
        if (this.imagesArr.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                this.fullscreenButton[i].setVisibility(4);
            }
            return;
        }
        int i2 = 0;
        while (i2 < 3) {
            int i3 = this.currentIndex;
            if (i2 == 1) {
                i3++;
            } else if (i2 == 2) {
                i3--;
            }
            if (i3 < 0 || i3 >= this.imagesArr.size()) {
                this.fullscreenButton[i2].setVisibility(4);
            } else {
                MessageObject messageObject2 = this.imagesArr.get(i3);
                if (!messageObject2.isVideo() && !messageObject2.isYouTubeVideo()) {
                    this.fullscreenButton[i2].setVisibility(4);
                } else {
                    boolean z = messageObject2.isYouTubeVideo() && (messageObject = this.currentMessageObject) != null && messageObject.getId() == messageObject2.getId();
                    if (z) {
                        measuredWidth = messageObject2.messageOwner.media.webpage.embed_width;
                    } else {
                        measuredWidth = (i2 != 0 || (textureView = this.videoTextureView) == null) ? 0 : textureView.getMeasuredWidth();
                    }
                    if (z) {
                        measuredHeight = messageObject2.messageOwner.media.webpage.embed_height;
                    } else {
                        measuredHeight = (i2 != 0 || (textureView2 = this.videoTextureView) == null) ? 0 : textureView2.getMeasuredHeight();
                    }
                    TLRPC$Document document = messageObject2.getDocument();
                    if (document != null) {
                        int size = document.attributes.size();
                        int i4 = 0;
                        while (true) {
                            if (i4 >= size) {
                                break;
                            }
                            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = document.attributes.get(i4);
                            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) {
                                measuredWidth = tLRPC$DocumentAttribute.w;
                                measuredHeight = tLRPC$DocumentAttribute.h;
                                break;
                            }
                            i4++;
                        }
                    }
                    Point point = AndroidUtilities.displaySize;
                    if (point.y > point.x && measuredWidth > measuredHeight) {
                        if (this.fullscreenButton[i2].getVisibility() != 0) {
                            this.fullscreenButton[i2].setVisibility(0);
                        }
                        if (this.isActionBarVisible) {
                            this.fullscreenButton[i2].setAlpha(1.0f);
                        }
                        ((FrameLayout.LayoutParams) this.fullscreenButton[i2].getLayoutParams()).topMargin = ((this.containerView.getMeasuredHeight() + ((int) (measuredHeight / (measuredWidth / this.containerView.getMeasuredWidth())))) / 2) - AndroidUtilities.dp(48.0f);
                    } else if (this.fullscreenButton[i2].getVisibility() != 4) {
                        this.fullscreenButton[i2].setVisibility(4);
                    }
                    if (this.imageMoveAnimation != null) {
                        float f2 = this.translationX;
                        f = f2 + ((this.animateToX - f2) * this.animationValue);
                    } else {
                        f = this.translationX;
                    }
                    float f3 = 0.0f;
                    if (i2 != 1) {
                        if (i2 == 2) {
                            f3 = ((-AndroidUtilities.displaySize.x) - AndroidUtilities.dp(15.0f)) + (f - this.maxX);
                        } else {
                            float f4 = this.minX;
                            if (f < f4) {
                                f3 = f - f4;
                            }
                        }
                    }
                    this.fullscreenButton[i2].setTranslationX((f3 + AndroidUtilities.displaySize.x) - AndroidUtilities.dp(48.0f));
                }
            }
            i2++;
        }
    }

    private void createVideoTextureView(final MediaController.SavedFilterState savedFilterState) {
        if (this.videoTextureView != null) {
            return;
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity) { // from class: org.telegram.ui.PhotoViewer.54
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.android.exoplayer2.ui.AspectRatioFrameLayout, android.widget.FrameLayout, android.view.View
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                if (PhotoViewer.this.textureImageView != null) {
                    ViewGroup.LayoutParams layoutParams = PhotoViewer.this.textureImageView.getLayoutParams();
                    layoutParams.width = getMeasuredWidth();
                    layoutParams.height = getMeasuredHeight();
                }
                if (PhotoViewer.this.videoTextureView instanceof VideoEditTextureView) {
                    PhotoViewer.this.videoTextureView.setPivotX(PhotoViewer.this.videoTextureView.getMeasuredWidth() / 2);
                    PhotoViewer.this.firstFrameView.setPivotX(PhotoViewer.this.videoTextureView.getMeasuredWidth() / 2);
                } else {
                    PhotoViewer.this.videoTextureView.setPivotX(0.0f);
                    PhotoViewer.this.firstFrameView.setPivotX(0.0f);
                }
                PhotoViewer.this.checkFullscreenButton();
            }
        };
        this.aspectRatioFrameLayout = aspectRatioFrameLayout;
        aspectRatioFrameLayout.setWillNotDraw(false);
        this.aspectRatioFrameLayout.setVisibility(4);
        this.containerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
        if (this.imagesArrLocals.isEmpty()) {
            this.videoTextureView = new TextureView(this.parentActivity);
        } else {
            VideoEditTextureView videoEditTextureView = new VideoEditTextureView(this.parentActivity, this.videoPlayer);
            if (savedFilterState != null) {
                videoEditTextureView.setDelegate(new VideoEditTextureView.VideoEditTextureViewDelegate() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda84
                    @Override // org.telegram.ui.Components.VideoEditTextureView.VideoEditTextureViewDelegate
                    public final void onEGLThreadAvailable(FilterGLThread filterGLThread) {
                        PhotoViewer.lambda$createVideoTextureView$52(MediaController.SavedFilterState.this, filterGLThread);
                    }
                });
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
        FirstFrameView firstFrameView = new FirstFrameView(this.parentActivity);
        this.firstFrameView = firstFrameView;
        firstFrameView.setPivotX(0.0f);
        this.firstFrameView.setPivotY(0.0f);
        this.firstFrameView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.aspectRatioFrameLayout.addView(this.firstFrameView, LayoutHelper.createFrame(-1, -1, 17));
        if (this.sendPhotoType != 1) {
            return;
        }
        View view = new View(this.parentActivity);
        this.flashView = view;
        view.setBackgroundColor(-1);
        this.flashView.setAlpha(0.0f);
        this.aspectRatioFrameLayout.addView(this.flashView, LayoutHelper.createFrame(-1, -1, 17));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$createVideoTextureView$52(MediaController.SavedFilterState savedFilterState, FilterGLThread filterGLThread) {
        filterGLThread.setFilterGLThreadDelegate(FilterShaders.getFilterShadersDelegate(savedFilterState));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releasePlayer(boolean z) {
        if (this.videoPlayer != null) {
            cancelVideoPlayRunnable();
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
        if (this.photoViewerWebView != null) {
            AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
            if (this.shouldSavePositionForCurrentVideoShortTerm != null) {
                this.savedVideoPositions.put(this.shouldSavePositionForCurrentVideoShortTerm, new SavedVideoPosition(((float) getCurrentVideoPosition()) / ((float) getVideoDuration()), SystemClock.elapsedRealtime()));
            }
        }
        OrientationEventListener orientationEventListener = this.orientationEventListener;
        if (orientationEventListener != null) {
            orientationEventListener.disable();
            this.orientationEventListener = null;
        }
        this.videoPreviewFrame.close();
        toggleMiniProgress(false, false);
        this.pipAvailable = false;
        this.playerInjected = false;
        if (this.pipItem.isEnabled()) {
            this.pipItem.setEnabled(false);
            this.pipItem.animate().alpha(0.5f).setDuration(175L).withEndAction(null).start();
        }
        if (this.keepScreenOnFlagSet) {
            try {
                this.parentActivity.getWindow().clearFlags(128);
                this.keepScreenOnFlagSet = false;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null) {
            try {
                this.containerView.removeView(aspectRatioFrameLayout);
            } catch (Throwable unused) {
            }
            this.aspectRatioFrameLayout = null;
        }
        cancelFlashAnimations();
        this.flashView = null;
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
                        this.bottomButtonsLayout.setVisibility(0);
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
                ofFloat.setDuration(200L);
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda10
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PhotoViewer.this.lambda$setVideoPlayerControlVisible$53(z3, valueAnimator);
                    }
                });
                ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.55
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator2) {
                        if (!z) {
                            PhotoViewer.this.videoPlayerControlFrameLayout.setVisibility(8);
                            return;
                        }
                        PhotoViewer.this.dateTextView.setVisibility(8);
                        PhotoViewer.this.nameTextView.setVisibility(8);
                        if (!z3) {
                            return;
                        }
                        PhotoViewer.this.bottomButtonsLayout.setVisibility(8);
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
                    LinearLayout linearLayout = this.bottomButtonsLayout;
                    if (z) {
                        i = 8;
                    }
                    linearLayout.setVisibility(i);
                    LinearLayout linearLayout2 = this.bottomButtonsLayout;
                    if (z) {
                        f = 0.0f;
                    }
                    linearLayout2.setAlpha(f);
                }
            }
            if (!this.allowShare || this.pageBlocksAdapter != null) {
                return;
            }
            if (z) {
                this.menuItem.showSubItem(10);
            } else {
                this.menuItem.hideSubItem(10);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setVideoPlayerControlVisible$53(boolean z, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.videoPlayerControlFrameLayout.setAlpha(floatValue);
        float f = 1.0f - floatValue;
        this.dateTextView.setAlpha(f);
        this.nameTextView.setAlpha(f);
        if (z) {
            this.bottomButtonsLayout.setAlpha(f);
        }
    }

    private void updateCaptionTextForCurrentPhoto(Object obj) {
        CharSequence charSequence;
        if (this.hasCaptionForAllMedia) {
            charSequence = this.captionForAllMedia;
        } else if (obj instanceof MediaController.PhotoEntry) {
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
        if (this.parentActivity == null) {
            return;
        }
        try {
            AlertDialog alertDialog = this.visibleDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            AlertDialog show = builder.show();
            this.visibleDialog = show;
            show.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda15
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    PhotoViewer.this.lambda$showAlertDialog$54(dialogInterface);
                }
            });
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showAlertDialog$54(DialogInterface dialogInterface) {
        this.visibleDialog = null;
    }

    private void mergeImages(String str, String str2, Bitmap bitmap, Bitmap bitmap2, float f, boolean z) {
        boolean z2;
        if (bitmap == null) {
            try {
                bitmap = BitmapFactory.decodeFile(str2);
                z2 = true;
            } catch (Throwable th) {
                FileLog.e(th);
                return;
            }
        } else {
            z2 = false;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float f2 = width;
        if (f2 > f || height > f) {
            float max = Math.max(width, height) / f;
            height = (int) (height / max);
            width = (int) (f2 / max);
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
        createBitmap.compress(Bitmap.CompressFormat.JPEG, f == 512.0f ? 83 : 87, fileOutputStream);
        try {
            fileOutputStream.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (z2) {
            bitmap.recycle();
        }
        createBitmap.recycle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void seekAnimatedStickersTo(long j) {
        RLottieDrawable lottieAnimation;
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = this.editState.mediaEntities;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                VideoEditedInfo.MediaEntity mediaEntity = this.editState.mediaEntities.get(i);
                if (mediaEntity.type == 0 && (mediaEntity.subType & 1) != 0) {
                    View view = mediaEntity.view;
                    if ((view instanceof BackupImageView) && (lottieAnimation = ((BackupImageView) view).getImageReceiver().getLottieAnimation()) != null) {
                        long j2 = this.startTime;
                        long j3 = 0;
                        if (j2 > 0) {
                            j3 = j2 / 1000;
                        }
                        lottieAnimation.setProgressMs(j - j3);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
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

    private int getAnimatedMediaEntitiesCount(boolean z) {
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = this.editState.mediaEntities;
        if (arrayList != null) {
            int size = arrayList.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                VideoEditedInfo.MediaEntity mediaEntity = this.editState.mediaEntities.get(i2);
                if (mediaEntity.type == 0) {
                    byte b = mediaEntity.subType;
                    if ((b & 1) != 0 || (b & 4) != 0) {
                        i++;
                        if (z) {
                            break;
                        }
                    }
                }
            }
            return i;
        }
        return 0;
    }

    private boolean hasAnimatedMediaEntities() {
        return getAnimatedMediaEntitiesCount(true) != 0;
    }

    private Bitmap createCroppedBitmap(Bitmap bitmap, MediaController.CropState cropState, int[] iArr, boolean z) {
        int i;
        int i2;
        int i3;
        int i4;
        Matrix matrix;
        try {
            int i5 = cropState.transformRotation;
            int i6 = 0;
            if (iArr != null) {
                i6 = iArr[0];
            }
            int i7 = (i5 + i6) % 360;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (i7 != 90 && i7 != 270) {
                i2 = width;
                i = height;
                float f = i2;
                float f2 = i;
                Bitmap createBitmap = Bitmap.createBitmap((int) (cropState.cropPw * f), (int) (cropState.cropPh * f2), Bitmap.Config.ARGB_8888);
                matrix = new Matrix();
                matrix.postTranslate((-width) / 2, (-height) / 2);
                if (z && cropState.mirrored) {
                    if (i7 != 90 && i7 != 270) {
                        matrix.postScale(-1.0f, 1.0f);
                    }
                    matrix.postScale(1.0f, -1.0f);
                }
                matrix.postRotate(cropState.cropRotate + i7);
                matrix.postTranslate(cropState.cropPx * f, cropState.cropPy * f2);
                float f3 = cropState.cropScale;
                matrix.postScale(f3, f3);
                matrix.postTranslate(i3 / 2, i4 / 2);
                new Canvas(createBitmap).drawBitmap(bitmap, matrix, new Paint(2));
                return createBitmap;
            }
            i = width;
            i2 = height;
            float f4 = i2;
            float var_ = i;
            Bitmap createBitmap2 = Bitmap.createBitmap((int) (cropState.cropPw * f4), (int) (cropState.cropPh * var_), Bitmap.Config.ARGB_8888);
            matrix = new Matrix();
            matrix.postTranslate((-width) / 2, (-height) / 2);
            if (z) {
                if (i7 != 90) {
                    matrix.postScale(-1.0f, 1.0f);
                }
                matrix.postScale(1.0f, -1.0f);
            }
            matrix.postRotate(cropState.cropRotate + i7);
            matrix.postTranslate(cropState.cropPx * f4, cropState.cropPy * var_);
            float var_ = cropState.cropScale;
            matrix.postScale(var_, var_);
            matrix.postTranslate(i3 / 2, i4 / 2);
            new Canvas(createBitmap2).drawBitmap(bitmap, matrix, new Paint(2));
            return createBitmap2;
        } catch (Throwable th) {
            FileLog.e(th);
            return null;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:171:0x050c  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x05ab A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:181:0x05b2 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:185:0x05bc  */
    /* JADX WARN: Removed duplicated region for block: B:231:0x074a  */
    /* JADX WARN: Removed duplicated region for block: B:234:0x0757  */
    /* JADX WARN: Removed duplicated region for block: B:235:0x076a  */
    /* JADX WARN: Removed duplicated region for block: B:244:0x079b  */
    /* JADX WARN: Removed duplicated region for block: B:250:0x07b1  */
    /* JADX WARN: Removed duplicated region for block: B:253:0x07b9  */
    /* JADX WARN: Removed duplicated region for block: B:254:0x0811  */
    /* JADX WARN: Removed duplicated region for block: B:257:0x081e  */
    /* JADX WARN: Removed duplicated region for block: B:265:0x082d  */
    /* JADX WARN: Removed duplicated region for block: B:269:0x0835  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00f9 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00fa  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x02b7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void applyCurrentEditMode() {
        /*
            Method dump skipped, instructions count: 2127
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.applyCurrentEditMode():void");
    }

    private void setPhotoChecked() {
        ChatActivity chatActivity;
        TLRPC$Chat currentChat;
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        if (photoViewerProvider != null) {
            if (photoViewerProvider.getSelectedPhotos() != null && this.maxSelectedPhotos > 0 && this.placeProvider.getSelectedPhotos().size() >= this.maxSelectedPhotos && !this.placeProvider.isPhotoChecked(this.currentIndex)) {
                if (!this.allowOrder || (chatActivity = this.parentChatActivity) == null || (currentChat = chatActivity.getCurrentChat()) == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled) {
                    return;
                }
                AlertsCreator.createSimpleAlert(this.parentActivity, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSelectSendError", R.string.SlowmodeSelectSendError)).show();
                return;
            }
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
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateResetButtonVisibility(final boolean z) {
        if (this.resetButton.isClickable() != z) {
            this.resetButton.setClickable(z);
            this.resetButton.setVisibility(0);
            this.resetButton.clearAnimation();
            this.resetButton.animate().alpha(z ? 1.0f : 0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(150L).withEndAction(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda72
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.this.lambda$updateResetButtonVisibility$55(z);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateResetButtonVisibility$55(boolean z) {
        if (!z) {
            this.resetButton.setVisibility(8);
        }
    }

    private void createCropView() {
        if (this.photoCropView != null) {
            return;
        }
        PhotoCropView photoCropView = new PhotoCropView(this.activityContext, this.resourcesProvider);
        this.photoCropView = photoCropView;
        photoCropView.setVisibility(8);
        this.photoCropView.onDisappear();
        this.containerView.addView(this.photoCropView, this.containerView.indexOfChild(this.videoTimelineView) - 1, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.photoCropView.setDelegate(new AnonymousClass56());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$56  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass56 implements PhotoCropView.PhotoCropViewDelegate {
        AnonymousClass56() {
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public void onChange(boolean z) {
            PhotoViewer.this.updateResetButtonVisibility(!z);
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public void onUpdate() {
            PhotoViewer.this.containerView.invalidate();
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public void onTapUp() {
            if (PhotoViewer.this.sendPhotoType == 1) {
                PhotoViewer.this.manuallyPaused = true;
                PhotoViewer.this.toggleVideoPlayer();
            }
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public void onVideoThumbClick() {
            if (PhotoViewer.this.videoPlayer == null) {
                return;
            }
            PhotoViewer.this.videoPlayer.seekTo(((float) PhotoViewer.this.videoPlayer.getDuration()) * PhotoViewer.this.avatarStartProgress);
            PhotoViewer.this.videoPlayer.pause();
            PhotoViewer.this.videoTimelineView.setProgress(PhotoViewer.this.avatarStartProgress);
            PhotoViewer.this.cancelVideoPlayRunnable();
            AndroidUtilities.runOnUIThread(PhotoViewer.this.videoPlayRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$56$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass56.this.lambda$onVideoThumbClick$0();
                }
            }, 860L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onVideoThumbClick$0() {
            PhotoViewer.this.manuallyPaused = false;
            if (PhotoViewer.this.videoPlayer != null) {
                PhotoViewer.this.videoPlayer.play();
            }
            PhotoViewer.this.videoPlayRunnable = null;
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public boolean rotate() {
            return PhotoViewer.this.cropRotate(-90.0f);
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public boolean mirror() {
            return PhotoViewer.this.cropMirror();
        }

        @Override // org.telegram.ui.Components.PhotoCropView.PhotoCropViewDelegate
        public int getVideoThumbX() {
            return (int) (AndroidUtilities.dp(16.0f) + ((PhotoViewer.this.videoTimelineView.getMeasuredWidth() - AndroidUtilities.dp(32.0f)) * PhotoViewer.this.avatarStartProgress));
        }
    }

    private void startVideoPlayer() {
        VideoPlayer videoPlayer;
        if (!this.isCurrentVideo || (videoPlayer = this.videoPlayer) == null || videoPlayer.isPlaying()) {
            return;
        }
        if (!this.muteVideo || this.sendPhotoType == 1) {
            this.videoPlayer.setVolume(0.0f);
        }
        this.manuallyPaused = false;
        toggleVideoPlayer();
    }

    private void detectFaces() {
        if (this.centerImage.getAnimation() != null || this.imagesArrLocals.isEmpty() || this.sendPhotoType == 1) {
            return;
        }
        String imageKey = this.centerImage.getImageKey();
        String str = this.currentImageFaceKey;
        if (str != null && str.equals(imageKey)) {
            return;
        }
        this.currentImageHasFace = 0;
        detectFaces(imageKey, this.centerImage.getBitmapSafe(), this.centerImage.getOrientation());
    }

    private void detectFaces(final String str, final ImageReceiver.BitmapHolder bitmapHolder, final int i) {
        if (str == null || bitmapHolder == null || bitmapHolder.bitmap == null) {
            return;
        }
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda66
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.lambda$detectFaces$58(bitmapHolder, i, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$detectFaces$58(final ImageReceiver.BitmapHolder bitmapHolder, int i, final String str) {
        FaceDetector faceDetector = null;
        try {
            try {
                final boolean z = false;
                faceDetector = new FaceDetector.Builder(ApplicationLoader.applicationContext).setMode(0).setLandmarkType(0).setTrackingEnabled(false).build();
                if (faceDetector.isOperational()) {
                    SparseArray<Face> detect = faceDetector.detect(new Frame.Builder().setBitmap(bitmapHolder.bitmap).setRotation(i).build());
                    if (detect != null && detect.size() != 0) {
                        z = true;
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda65
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.this.lambda$detectFaces$56(str, z);
                        }
                    });
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("face detection is not operational");
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda67
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.this.lambda$detectFaces$57(bitmapHolder, str);
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e(e);
                if (0 == 0) {
                    return;
                }
            }
            faceDetector.release();
        } catch (Throwable th) {
            if (0 != 0) {
                faceDetector.release();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$detectFaces$56(String str, boolean z) {
        if (str.equals(this.centerImage.getImageKey())) {
            this.currentImageHasFace = z ? 1 : 0;
            this.currentImageFaceKey = str;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$detectFaces$57(ImageReceiver.BitmapHolder bitmapHolder, String str) {
        bitmapHolder.release();
        if (str.equals(this.centerImage.getImageKey())) {
            this.currentImageHasFace = 2;
            this.currentImageFaceKey = str;
        }
    }

    private void switchToEditMode(final int i) {
        ActionBar actionBar;
        int i2;
        ActionBar actionBar2;
        int i3;
        String str;
        MediaController.SavedFilterState savedFilterState;
        int i4;
        Bitmap decodeFile;
        Bitmap bitmap;
        int i5;
        int i6;
        MediaController.CropState cropState;
        ActionBar actionBar3;
        int i7;
        int i8;
        int i9;
        float min;
        float f;
        MediaController.CropState cropState2;
        int i10;
        int i11;
        if (this.currentEditMode != i) {
            if ((this.isCurrentVideo && this.photoProgressViews[0].backgroundState != 3 && !this.isCurrentVideo && (this.centerImage.getBitmap() == null || this.photoProgressViews[0].backgroundState != -1)) || this.changeModeAnimation != null || this.imageMoveAnimation != null || this.captionEditText.getTag() != null) {
                return;
            }
            this.windowView.setClipChildren(i == 2);
            int i12 = NUM;
            int color = this.navigationBar.getBackground() instanceof ColorDrawable ? ((ColorDrawable) this.navigationBar.getBackground()).getColor() : NUM;
            if (i == 1) {
                i12 = -NUM;
            } else if (i == 3) {
                i12 = -16777216;
            }
            this.navigationBar.setVisibility(i != 2 ? 0 : 4);
            this.switchingToMode = i;
            if (i == 0) {
                if (this.centerImage.getBitmap() != null) {
                    int bitmapWidth = this.centerImage.getBitmapWidth();
                    int bitmapHeight = this.centerImage.getBitmapHeight();
                    int i13 = this.currentEditMode;
                    if (i13 == 3) {
                        if (this.sendPhotoType == 1) {
                            if (this.cropTransform.getOrientation() == 90 || this.cropTransform.getOrientation() == 270) {
                                i11 = bitmapWidth;
                                bitmapWidth = bitmapHeight;
                                float f2 = bitmapWidth;
                                float f3 = i11;
                                min = Math.min(getContainerViewWidth(0) / f2, getContainerViewHeight(0) / f3);
                                f = Math.min(getContainerViewWidth(3) / f2, getContainerViewHeight(3) / f3);
                            }
                            i11 = bitmapHeight;
                            float var_ = bitmapWidth;
                            float var_ = i11;
                            min = Math.min(getContainerViewWidth(0) / var_, getContainerViewHeight(0) / var_);
                            f = Math.min(getContainerViewWidth(3) / var_, getContainerViewHeight(3) / var_);
                        } else {
                            MediaController.CropState cropState3 = this.editState.cropState;
                            if (cropState3 != null) {
                                int i14 = cropState3.transformRotation;
                                if (i14 == 90 || i14 == 270) {
                                    bitmapHeight = bitmapWidth;
                                    bitmapWidth = bitmapHeight;
                                }
                                bitmapWidth = (int) (bitmapWidth * cropState3.cropPw);
                                i11 = (int) (bitmapHeight * cropState3.cropPh);
                                float var_ = bitmapWidth;
                                float var_ = i11;
                                min = Math.min(getContainerViewWidth(0) / var_, getContainerViewHeight(0) / var_);
                                f = Math.min(getContainerViewWidth(3) / var_, getContainerViewHeight(3) / var_);
                            }
                            i11 = bitmapHeight;
                            float var_ = bitmapWidth;
                            float var_ = i11;
                            min = Math.min(getContainerViewWidth(0) / var_, getContainerViewHeight(0) / var_);
                            f = Math.min(getContainerViewWidth(3) / var_, getContainerViewHeight(3) / var_);
                        }
                    } else {
                        if (i13 != 1 && (cropState2 = this.editState.cropState) != null && ((i10 = cropState2.transformRotation) == 90 || i10 == 270)) {
                            float f4 = bitmapHeight;
                            float containerViewWidth = getContainerViewWidth() / f4;
                            float f5 = bitmapWidth;
                            if (containerViewWidth * f5 > getContainerViewHeight()) {
                                containerViewWidth = getContainerViewHeight() / f5;
                            }
                            this.scale = 1.0f / (containerViewWidth / Math.min(getContainerViewWidth() / f5, getContainerViewHeight() / f4));
                        } else if (this.sendPhotoType == 1 && (this.cropTransform.getOrientation() == 90 || this.cropTransform.getOrientation() == 270)) {
                            float f6 = bitmapHeight;
                            float containerViewWidth2 = getContainerViewWidth() / f6;
                            float f7 = bitmapWidth;
                            if (containerViewWidth2 * f7 > getContainerViewHeight()) {
                                containerViewWidth2 = getContainerViewHeight() / f7;
                            }
                            this.scale = 1.0f / ((((this.cropTransform.getScale() / this.cropTransform.getTrueCropScale()) * containerViewWidth2) / Math.min(getContainerViewWidth() / f7, getContainerViewHeight() / f6)) / this.cropTransform.getMinScale());
                        }
                        MediaController.CropState cropState4 = this.editState.cropState;
                        if (cropState4 != null) {
                            int i15 = cropState4.transformRotation;
                            if (i15 == 90 || i15 == 270) {
                                bitmapHeight = bitmapWidth;
                                bitmapWidth = bitmapHeight;
                            }
                            bitmapWidth = (int) (bitmapWidth * cropState4.cropPw);
                            i9 = (int) (bitmapHeight * cropState4.cropPh);
                        } else if (this.sendPhotoType == 1 && (this.cropTransform.getOrientation() == 90 || this.cropTransform.getOrientation() == 270)) {
                            i9 = bitmapWidth;
                            bitmapWidth = bitmapHeight;
                        } else {
                            i9 = bitmapHeight;
                        }
                        float f8 = bitmapWidth;
                        float f9 = i9;
                        float min2 = Math.min(getContainerViewWidth() / f8, getContainerViewHeight() / f9);
                        if (this.sendPhotoType == 1) {
                            min = getCropFillScale(this.cropTransform.getOrientation() == 90 || this.cropTransform.getOrientation() == 270);
                        } else {
                            min = Math.min(getContainerViewWidth(0) / f8, getContainerViewHeight(0) / f9);
                        }
                        f = min2;
                    }
                    this.animateToScale = min / f;
                    this.animateToX = 0.0f;
                    this.translationX = (getLeftInset() / 2) - (getRightInset() / 2);
                    if (this.sendPhotoType == 1) {
                        int i16 = this.currentEditMode;
                        if (i16 == 2) {
                            this.animateToY = AndroidUtilities.dp(36.0f);
                        } else if (i16 == 3) {
                            this.animateToY = -AndroidUtilities.dp(12.0f);
                        }
                    } else {
                        int i17 = this.currentEditMode;
                        if (i17 == 1) {
                            this.animateToY = AndroidUtilities.dp(56.0f);
                        } else if (i17 == 2) {
                            this.animateToY = AndroidUtilities.dp(93.0f);
                        } else if (i17 == 3) {
                            this.animateToY = AndroidUtilities.dp(44.0f);
                        }
                        if (isStatusBarVisible()) {
                            this.animateToY -= AndroidUtilities.statusBarHeight / 2;
                        }
                    }
                    this.animationStartTime = System.currentTimeMillis();
                    this.zoomAnimation = true;
                }
                this.padImageForHorizontalInsets = false;
                this.imageMoveAnimation = new AnimatorSet();
                ArrayList arrayList = new ArrayList(4);
                int i18 = this.currentEditMode;
                if (i18 == 1) {
                    arrayList.add(ObjectAnimator.ofFloat(this.editorDoneLayout, View.TRANSLATION_Y, AndroidUtilities.dp(48.0f)));
                    i8 = 2;
                    arrayList.add(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f));
                    arrayList.add(ObjectAnimator.ofFloat(this.photoCropView, View.ALPHA, 0.0f));
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda6
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            PhotoViewer.this.lambda$switchToEditMode$59(valueAnimator);
                        }
                    });
                    arrayList.add(ofFloat);
                } else {
                    if (i18 == 2) {
                        this.photoFilterView.shutdown();
                        arrayList.add(ObjectAnimator.ofFloat(this.photoFilterView.getToolsView(), View.TRANSLATION_Y, AndroidUtilities.dp(186.0f)));
                        arrayList.add(ObjectAnimator.ofFloat(this.photoFilterView.getCurveControl(), View.ALPHA, 0.0f));
                        arrayList.add(ObjectAnimator.ofFloat(this.photoFilterView.getBlurControl(), View.ALPHA, 0.0f));
                        arrayList.add(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f));
                    } else if (i18 == 3) {
                        this.paintingOverlay.showAll();
                        this.containerView.invalidate();
                        this.photoPaintView.shutdown();
                        arrayList.add(ObjectAnimator.ofFloat(this.photoPaintView.getToolsView(), View.TRANSLATION_Y, AndroidUtilities.dp(126.0f)));
                        arrayList.add(ObjectAnimator.ofFloat(this.photoPaintView.getColorPickerBackground(), View.TRANSLATION_Y, AndroidUtilities.dp(126.0f)));
                        arrayList.add(ObjectAnimator.ofFloat(this.photoPaintView.getColorPicker(), View.TRANSLATION_Y, AndroidUtilities.dp(126.0f)));
                        i8 = 2;
                        arrayList.add(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f));
                    }
                    i8 = 2;
                }
                View view = this.navigationBar;
                ArgbEvaluator argbEvaluator = new ArgbEvaluator();
                Object[] objArr = new Object[i8];
                objArr[0] = Integer.valueOf(color);
                objArr[1] = Integer.valueOf(i12);
                arrayList.add(ObjectAnimator.ofObject(view, "backgroundColor", argbEvaluator, objArr));
                this.imageMoveAnimation.playTogether(arrayList);
                this.imageMoveAnimation.setDuration(200L);
                this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.57
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (PhotoViewer.this.currentEditMode == 1) {
                            PhotoViewer.this.photoCropView.onDisappear();
                            PhotoViewer.this.photoCropView.onHide();
                            PhotoViewer.this.editorDoneLayout.setVisibility(8);
                            PhotoViewer.this.photoCropView.setVisibility(8);
                            PhotoViewer.this.photoCropView.cropView.areaView.setRotationScaleTranslation(0.0f, 1.0f, 0.0f, 0.0f);
                        } else if (PhotoViewer.this.currentEditMode == 2) {
                            try {
                                PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoFilterView);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            PhotoViewer.this.photoFilterView = null;
                        } else if (PhotoViewer.this.currentEditMode == 3) {
                            try {
                                PhotoViewer.this.containerView.removeView(PhotoViewer.this.photoPaintView);
                            } catch (Exception e2) {
                                FileLog.e(e2);
                            }
                            PhotoViewer.this.photoPaintView = null;
                        }
                        PhotoViewer.this.imageMoveAnimation = null;
                        PhotoViewer.this.currentEditMode = i;
                        PhotoViewer.this.switchingToMode = -1;
                        PhotoViewer.this.applying = false;
                        if (PhotoViewer.this.sendPhotoType == 1) {
                            PhotoViewer.this.photoCropView.setVisibility(0);
                        }
                        PhotoViewer.this.animateToScale = 1.0f;
                        PhotoViewer.this.animateToX = 0.0f;
                        PhotoViewer.this.animateToY = 0.0f;
                        PhotoViewer.this.scale = 1.0f;
                        PhotoViewer photoViewer = PhotoViewer.this;
                        photoViewer.updateMinMax(photoViewer.scale);
                        PhotoViewer.this.containerView.invalidate();
                        if (PhotoViewer.this.savedState != null) {
                            PhotoViewer.this.savedState.restore();
                            PhotoViewer.this.savedState = null;
                            ActionBarToggleParams enableStatusBarAnimation = new ActionBarToggleParams().enableStatusBarAnimation(false);
                            PhotoViewer.this.toggleActionBar(false, false, enableStatusBarAnimation);
                            PhotoViewer.this.toggleActionBar(true, true, enableStatusBarAnimation);
                            return;
                        }
                        AnimatorSet animatorSet = new AnimatorSet();
                        ArrayList arrayList2 = new ArrayList();
                        arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, View.TRANSLATION_Y, 0.0f));
                        arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.pickerViewSendButton, View.TRANSLATION_Y, 0.0f));
                        if (PhotoViewer.this.sendPhotoType != 1) {
                            arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.actionBar, View.TRANSLATION_Y, 0.0f));
                        }
                        if (PhotoViewer.this.needCaptionLayout) {
                            arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.captionTextViewSwitcher, View.TRANSLATION_Y, 0.0f));
                        }
                        if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4) {
                            arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.checkImageView, View.ALPHA, 1.0f));
                            arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.photosCounterView, View.ALPHA, 1.0f));
                        } else if (PhotoViewer.this.sendPhotoType == 1) {
                            arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, View.ALPHA, 1.0f));
                        }
                        if (PhotoViewer.this.cameraItem.getTag() != null) {
                            PhotoViewer.this.cameraItem.setVisibility(0);
                            arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.cameraItem, View.ALPHA, 1.0f));
                        }
                        if (PhotoViewer.this.muteItem.getTag() != null) {
                            PhotoViewer.this.muteItem.setVisibility(0);
                            arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.muteItem, View.ALPHA, 1.0f));
                        }
                        if (PhotoViewer.this.navigationBar != null) {
                            PhotoViewer.this.navigationBar.setVisibility(0);
                            arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.navigationBar, View.ALPHA, 1.0f));
                        }
                        animatorSet.playTogether(arrayList2);
                        animatorSet.setDuration(200L);
                        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.57.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationStart(Animator animator2) {
                                PhotoViewer.this.pickerView.setVisibility(0);
                                PhotoViewer.this.pickerViewSendButton.setVisibility(0);
                                PhotoViewer.this.actionBar.setVisibility(0);
                                if (PhotoViewer.this.needCaptionLayout) {
                                    PhotoViewer.this.captionTextViewSwitcher.setVisibility(PhotoViewer.this.captionTextViewSwitcher.getTag() != null ? 0 : 4);
                                }
                                if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4 || ((PhotoViewer.this.sendPhotoType == 2 || PhotoViewer.this.sendPhotoType == 5) && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                                    PhotoViewer.this.checkImageView.setVisibility(0);
                                    PhotoViewer.this.photosCounterView.setVisibility(0);
                                }
                            }
                        });
                        animatorSet.start();
                    }
                });
                this.imageMoveAnimation.start();
                return;
            }
            float var_ = 154.0f;
            if (i == 1) {
                startVideoPlayer();
                createCropView();
                this.previousHasTransform = this.cropTransform.hasViewTransform();
                this.previousCropPx = this.cropTransform.getCropPx();
                this.previousCropPy = this.cropTransform.getCropPy();
                this.previousCropScale = this.cropTransform.getScale();
                this.previousCropRotation = this.cropTransform.getRotation();
                this.previousCropOrientation = this.cropTransform.getOrientation();
                this.previousCropPw = this.cropTransform.getCropPw();
                this.previousCropPh = this.cropTransform.getCropPh();
                this.previousCropMirrored = this.cropTransform.isMirrored();
                this.photoCropView.onAppear();
                this.editorDoneLayout.doneButton.setText(LocaleController.getString("Crop", R.string.Crop));
                this.editorDoneLayout.doneButton.setTextColor(getThemedColor("dialogFloatingButton"));
                this.changeModeAnimation = new AnimatorSet();
                ArrayList arrayList2 = new ArrayList();
                FrameLayout frameLayout = this.pickerView;
                Property property = View.TRANSLATION_Y;
                float[] fArr = new float[2];
                fArr[0] = 0.0f;
                fArr[1] = AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList2.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
                ImageView imageView = this.pickerViewSendButton;
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[2];
                fArr2[0] = 0.0f;
                fArr2[1] = AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList2.add(ObjectAnimator.ofFloat(imageView, property2, fArr2));
                arrayList2.add(ObjectAnimator.ofFloat(this.actionBar, View.TRANSLATION_Y, 0.0f, -actionBar3.getHeight()));
                arrayList2.add(ObjectAnimator.ofObject(this.navigationBar, "backgroundColor", new ArgbEvaluator(), Integer.valueOf(color), Integer.valueOf(i12)));
                if (this.needCaptionLayout) {
                    CaptionTextViewSwitcher captionTextViewSwitcher = this.captionTextViewSwitcher;
                    Property property3 = View.TRANSLATION_Y;
                    float[] fArr3 = new float[2];
                    fArr3[0] = 0.0f;
                    if (!this.isCurrentVideo) {
                        var_ = 96.0f;
                    }
                    fArr3[1] = AndroidUtilities.dp(var_);
                    arrayList2.add(ObjectAnimator.ofFloat(captionTextViewSwitcher, property3, fArr3));
                }
                int i19 = this.sendPhotoType;
                if (i19 == 0 || i19 == 4) {
                    i7 = 2;
                    arrayList2.add(ObjectAnimator.ofFloat(this.checkImageView, View.ALPHA, 1.0f, 0.0f));
                    arrayList2.add(ObjectAnimator.ofFloat(this.photosCounterView, View.ALPHA, 1.0f, 0.0f));
                } else {
                    i7 = 2;
                }
                if (this.selectedPhotosListView.getVisibility() == 0) {
                    float[] fArr4 = new float[i7];
                    // fill-array-data instruction
                    fArr4[0] = 1.0f;
                    fArr4[1] = 0.0f;
                    arrayList2.add(ObjectAnimator.ofFloat(this.selectedPhotosListView, View.ALPHA, fArr4));
                }
                if (this.cameraItem.getTag() != null) {
                    float[] fArr5 = new float[i7];
                    // fill-array-data instruction
                    fArr5[0] = 1.0f;
                    fArr5[1] = 0.0f;
                    arrayList2.add(ObjectAnimator.ofFloat(this.cameraItem, View.ALPHA, fArr5));
                }
                if (this.muteItem.getTag() != null) {
                    float[] fArr6 = new float[i7];
                    // fill-array-data instruction
                    fArr6[0] = 1.0f;
                    fArr6[1] = 0.0f;
                    arrayList2.add(ObjectAnimator.ofFloat(this.muteItem, View.ALPHA, fArr6));
                }
                View view2 = this.navigationBar;
                if (view2 != null) {
                    arrayList2.add(ObjectAnimator.ofFloat(view2, View.ALPHA, 1.0f));
                }
                this.changeModeAnimation.playTogether(arrayList2);
                this.changeModeAnimation.setDuration(200L);
                this.changeModeAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.58
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        PhotoViewer photoViewer;
                        VideoEditTextureView videoEditTextureView = null;
                        PhotoViewer.this.changeModeAnimation = null;
                        PhotoViewer.this.pickerView.setVisibility(8);
                        PhotoViewer.this.pickerViewSendButton.setVisibility(8);
                        PhotoViewer.this.cameraItem.setVisibility(8);
                        PhotoViewer.this.muteItem.setVisibility(8);
                        PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                        PhotoViewer.this.selectedPhotosListView.setAlpha(0.0f);
                        PhotoViewer.this.selectedPhotosListView.setTranslationY(-AndroidUtilities.dp(10.0f));
                        PhotoViewer.this.photosCounterView.setRotationX(0.0f);
                        PhotoViewer.this.selectedPhotosListView.setEnabled(false);
                        PhotoViewer.this.isPhotosListViewVisible = false;
                        if (PhotoViewer.this.needCaptionLayout) {
                            PhotoViewer.this.captionTextViewSwitcher.setVisibility(4);
                        }
                        if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4 || ((PhotoViewer.this.sendPhotoType == 2 || PhotoViewer.this.sendPhotoType == 5) && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                            PhotoViewer.this.checkImageView.setVisibility(8);
                            PhotoViewer.this.photosCounterView.setVisibility(8);
                        }
                        Bitmap bitmap2 = PhotoViewer.this.centerImage.getBitmap();
                        if (bitmap2 != null || PhotoViewer.this.isCurrentVideo) {
                            PhotoCropView photoCropView = PhotoViewer.this.photoCropView;
                            int orientation = PhotoViewer.this.centerImage.getOrientation();
                            boolean z = PhotoViewer.this.sendPhotoType != 1;
                            PaintingOverlay paintingOverlay = PhotoViewer.this.paintingOverlay;
                            CropTransform cropTransform = PhotoViewer.this.cropTransform;
                            if (PhotoViewer.this.isCurrentVideo) {
                                videoEditTextureView = (VideoEditTextureView) PhotoViewer.this.videoTextureView;
                            }
                            photoCropView.setBitmap(bitmap2, orientation, z, false, paintingOverlay, cropTransform, videoEditTextureView, PhotoViewer.this.editState.cropState);
                            PhotoViewer.this.photoCropView.onDisappear();
                            int bitmapWidth2 = PhotoViewer.this.centerImage.getBitmapWidth();
                            int bitmapHeight2 = PhotoViewer.this.centerImage.getBitmapHeight();
                            if (PhotoViewer.this.editState.cropState != null) {
                                if (PhotoViewer.this.editState.cropState.transformRotation == 90 || PhotoViewer.this.editState.cropState.transformRotation == 270) {
                                    bitmapHeight2 = bitmapWidth2;
                                    bitmapWidth2 = bitmapHeight2;
                                }
                                bitmapWidth2 = (int) (bitmapWidth2 * PhotoViewer.this.editState.cropState.cropPw);
                                bitmapHeight2 = (int) (bitmapHeight2 * PhotoViewer.this.editState.cropState.cropPh);
                            }
                            float var_ = bitmapWidth2;
                            float var_ = bitmapHeight2;
                            float min3 = Math.min(PhotoViewer.this.getContainerViewWidth() / var_, PhotoViewer.this.getContainerViewHeight() / var_);
                            float min4 = Math.min(PhotoViewer.this.getContainerViewWidth(1) / var_, PhotoViewer.this.getContainerViewHeight(1) / var_);
                            if (PhotoViewer.this.sendPhotoType == 1) {
                                float min5 = Math.min(PhotoViewer.this.getContainerViewWidth(1), PhotoViewer.this.getContainerViewHeight(1));
                                min4 = Math.max(min5 / var_, min5 / var_);
                            }
                            PhotoViewer.this.animateToScale = min4 / min3;
                            PhotoViewer.this.animateToX = (photoViewer.getLeftInset() / 2) - (PhotoViewer.this.getRightInset() / 2);
                            PhotoViewer.this.animateToY = (-AndroidUtilities.dp(56.0f)) + (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight / 2 : 0);
                            PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                            PhotoViewer.this.zoomAnimation = true;
                        }
                        PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                        PhotoViewer.this.imageMoveAnimation.playTogether(ObjectAnimator.ofFloat(PhotoViewer.this.editorDoneLayout, View.TRANSLATION_Y, AndroidUtilities.dp(48.0f), 0.0f), ObjectAnimator.ofFloat(PhotoViewer.this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f), ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, View.ALPHA, 0.0f, 1.0f));
                        PhotoViewer.this.imageMoveAnimation.setDuration(200L);
                        PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.58.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationStart(Animator animator2) {
                                PhotoViewer.this.editorDoneLayout.setVisibility(0);
                                PhotoViewer.this.photoCropView.setVisibility(0);
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator2) {
                                PhotoViewer.this.photoCropView.onAppeared();
                                PhotoViewer.this.photoCropView.onShow();
                                PhotoViewer.this.imageMoveAnimation = null;
                                AnonymousClass58 anonymousClass58 = AnonymousClass58.this;
                                PhotoViewer.this.currentEditMode = i;
                                PhotoViewer.this.switchingToMode = -1;
                                PhotoViewer.this.animateToScale = 1.0f;
                                PhotoViewer.this.animateToX = 0.0f;
                                PhotoViewer.this.animateToY = 0.0f;
                                PhotoViewer.this.scale = 1.0f;
                                PhotoViewer photoViewer2 = PhotoViewer.this;
                                photoViewer2.updateMinMax(photoViewer2.scale);
                                PhotoViewer.this.padImageForHorizontalInsets = true;
                                PhotoViewer.this.containerView.invalidate();
                            }
                        });
                        PhotoViewer.this.imageMoveAnimation.start();
                    }
                });
                this.changeModeAnimation.start();
            } else if (i != 2) {
                if (i != 3) {
                    return;
                }
                startVideoPlayer();
                createPaintView();
                this.changeModeAnimation = new AnimatorSet();
                ArrayList arrayList3 = new ArrayList();
                FrameLayout frameLayout2 = this.pickerView;
                Property property4 = View.TRANSLATION_Y;
                float[] fArr7 = new float[1];
                fArr7[0] = AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList3.add(ObjectAnimator.ofFloat(frameLayout2, property4, fArr7));
                ImageView imageView2 = this.pickerViewSendButton;
                Property property5 = View.TRANSLATION_Y;
                float[] fArr8 = new float[1];
                fArr8[0] = AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList3.add(ObjectAnimator.ofFloat(imageView2, property5, fArr8));
                arrayList3.add(ObjectAnimator.ofFloat(this.actionBar, View.TRANSLATION_Y, -actionBar.getHeight()));
                arrayList3.add(ObjectAnimator.ofObject(this.navigationBar, "backgroundColor", new ArgbEvaluator(), Integer.valueOf(color), Integer.valueOf(i12)));
                if (this.needCaptionLayout) {
                    CaptionTextViewSwitcher captionTextViewSwitcher2 = this.captionTextViewSwitcher;
                    Property property6 = View.TRANSLATION_Y;
                    float[] fArr9 = new float[1];
                    if (!this.isCurrentVideo) {
                        var_ = 96.0f;
                    }
                    fArr9[0] = AndroidUtilities.dp(var_);
                    arrayList3.add(ObjectAnimator.ofFloat(captionTextViewSwitcher2, property6, fArr9));
                }
                int i20 = this.sendPhotoType;
                if (i20 == 0 || i20 == 4) {
                    i2 = 2;
                    arrayList3.add(ObjectAnimator.ofFloat(this.checkImageView, View.ALPHA, 1.0f, 0.0f));
                    arrayList3.add(ObjectAnimator.ofFloat(this.photosCounterView, View.ALPHA, 1.0f, 0.0f));
                } else if (i20 == 1) {
                    i2 = 2;
                    arrayList3.add(ObjectAnimator.ofFloat(this.photoCropView, View.ALPHA, 1.0f, 0.0f));
                } else {
                    i2 = 2;
                }
                if (this.selectedPhotosListView.getVisibility() == 0) {
                    float[] fArr10 = new float[i2];
                    // fill-array-data instruction
                    fArr10[0] = 1.0f;
                    fArr10[1] = 0.0f;
                    arrayList3.add(ObjectAnimator.ofFloat(this.selectedPhotosListView, View.ALPHA, fArr10));
                }
                if (this.cameraItem.getTag() != null) {
                    float[] fArr11 = new float[i2];
                    // fill-array-data instruction
                    fArr11[0] = 1.0f;
                    fArr11[1] = 0.0f;
                    arrayList3.add(ObjectAnimator.ofFloat(this.cameraItem, View.ALPHA, fArr11));
                }
                if (this.muteItem.getTag() != null) {
                    float[] fArr12 = new float[i2];
                    // fill-array-data instruction
                    fArr12[0] = 1.0f;
                    fArr12[1] = 0.0f;
                    arrayList3.add(ObjectAnimator.ofFloat(this.muteItem, View.ALPHA, fArr12));
                }
                this.changeModeAnimation.playTogether(arrayList3);
                this.changeModeAnimation.setDuration(200L);
                this.changeModeAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.60
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        PhotoViewer.this.switchToPaintMode();
                    }
                });
                this.changeModeAnimation.start();
            } else {
                startVideoPlayer();
                if (this.photoFilterView == null) {
                    PaintingOverlay paintingOverlay = null;
                    if (!this.imagesArrLocals.isEmpty()) {
                        Object obj = this.imagesArrLocals.get(this.currentIndex);
                        i4 = obj instanceof MediaController.PhotoEntry ? ((MediaController.PhotoEntry) obj).orientation : 0;
                        MediaController.MediaEditState mediaEditState = (MediaController.MediaEditState) obj;
                        MediaController.SavedFilterState savedFilterState2 = mediaEditState.savedFilterState;
                        str = mediaEditState.getPath();
                        savedFilterState = savedFilterState2;
                    } else {
                        str = null;
                        savedFilterState = null;
                        i4 = 0;
                    }
                    if (this.videoTextureView != null) {
                        bitmap = null;
                    } else {
                        if (savedFilterState == null) {
                            decodeFile = this.centerImage.getBitmap();
                            i4 = this.centerImage.getOrientation();
                        } else {
                            decodeFile = BitmapFactory.decodeFile(str);
                        }
                        bitmap = decodeFile;
                    }
                    int i21 = i4;
                    if (this.sendPhotoType == 1) {
                        i5 = 1;
                    } else if (this.isCurrentVideo || (i6 = this.currentImageHasFace) == 2) {
                        i5 = 2;
                    } else {
                        i5 = i6 == 1 ? 1 : 0;
                    }
                    Activity activity = this.parentActivity;
                    TextureView textureView = this.videoTextureView;
                    VideoEditTextureView videoEditTextureView = textureView != null ? (VideoEditTextureView) textureView : null;
                    if (!this.isCurrentVideo) {
                        paintingOverlay = this.paintingOverlay;
                    }
                    PhotoFilterView photoFilterView = new PhotoFilterView(activity, videoEditTextureView, bitmap, i21, savedFilterState, paintingOverlay, i5, textureView == null && (((cropState = this.editState.cropState) != null && cropState.mirrored) || this.cropTransform.isMirrored()), this.resourcesProvider);
                    this.photoFilterView = photoFilterView;
                    this.containerView.addView(photoFilterView, LayoutHelper.createFrame(-1, -1.0f));
                    this.photoFilterView.getDoneTextView().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda23
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view3) {
                            PhotoViewer.this.lambda$switchToEditMode$60(view3);
                        }
                    });
                    this.photoFilterView.getCancelTextView().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda38
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view3) {
                            PhotoViewer.this.lambda$switchToEditMode$62(view3);
                        }
                    });
                    this.photoFilterView.getToolsView().setTranslationY(AndroidUtilities.dp(186.0f));
                }
                this.changeModeAnimation = new AnimatorSet();
                ArrayList arrayList4 = new ArrayList();
                FrameLayout frameLayout3 = this.pickerView;
                Property property7 = View.TRANSLATION_Y;
                float[] fArr13 = new float[2];
                fArr13[0] = 0.0f;
                fArr13[1] = AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f);
                arrayList4.add(ObjectAnimator.ofFloat(frameLayout3, property7, fArr13));
                ImageView imageView3 = this.pickerViewSendButton;
                Property property8 = View.TRANSLATION_Y;
                float[] fArr14 = new float[2];
                fArr14[0] = 0.0f;
                if (!this.isCurrentVideo) {
                    var_ = 96.0f;
                }
                fArr14[1] = AndroidUtilities.dp(var_);
                arrayList4.add(ObjectAnimator.ofFloat(imageView3, property8, fArr14));
                arrayList4.add(ObjectAnimator.ofFloat(this.actionBar, View.TRANSLATION_Y, 0.0f, -actionBar2.getHeight()));
                int i22 = this.sendPhotoType;
                if (i22 == 0 || i22 == 4) {
                    i3 = 2;
                    arrayList4.add(ObjectAnimator.ofFloat(this.checkImageView, View.ALPHA, 1.0f, 0.0f));
                    arrayList4.add(ObjectAnimator.ofFloat(this.photosCounterView, View.ALPHA, 1.0f, 0.0f));
                } else if (i22 == 1) {
                    i3 = 2;
                    arrayList4.add(ObjectAnimator.ofFloat(this.photoCropView, View.ALPHA, 1.0f, 0.0f));
                } else {
                    i3 = 2;
                }
                if (this.selectedPhotosListView.getVisibility() == 0) {
                    float[] fArr15 = new float[i3];
                    // fill-array-data instruction
                    fArr15[0] = 1.0f;
                    fArr15[1] = 0.0f;
                    arrayList4.add(ObjectAnimator.ofFloat(this.selectedPhotosListView, View.ALPHA, fArr15));
                }
                if (this.cameraItem.getTag() != null) {
                    float[] fArr16 = new float[i3];
                    // fill-array-data instruction
                    fArr16[0] = 1.0f;
                    fArr16[1] = 0.0f;
                    arrayList4.add(ObjectAnimator.ofFloat(this.cameraItem, View.ALPHA, fArr16));
                }
                if (this.muteItem.getTag() != null) {
                    float[] fArr17 = new float[i3];
                    // fill-array-data instruction
                    fArr17[0] = 1.0f;
                    fArr17[1] = 0.0f;
                    arrayList4.add(ObjectAnimator.ofFloat(this.muteItem, View.ALPHA, fArr17));
                }
                View view3 = this.navigationBar;
                ArgbEvaluator argbEvaluator2 = new ArgbEvaluator();
                Object[] objArr2 = new Object[i3];
                objArr2[0] = Integer.valueOf(color);
                objArr2[1] = Integer.valueOf(i12);
                arrayList4.add(ObjectAnimator.ofObject(view3, "backgroundColor", argbEvaluator2, objArr2));
                this.changeModeAnimation.playTogether(arrayList4);
                this.changeModeAnimation.setDuration(200L);
                this.changeModeAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.59
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        float min3;
                        PhotoViewer photoViewer;
                        PhotoViewer.this.changeModeAnimation = null;
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
                        PhotoViewer.this.selectedPhotosListView.setTranslationY(-AndroidUtilities.dp(10.0f));
                        PhotoViewer.this.photosCounterView.setRotationX(0.0f);
                        PhotoViewer.this.selectedPhotosListView.setEnabled(false);
                        PhotoViewer.this.isPhotosListViewVisible = false;
                        if (PhotoViewer.this.needCaptionLayout) {
                            PhotoViewer.this.captionTextViewSwitcher.setVisibility(4);
                        }
                        if (PhotoViewer.this.sendPhotoType == 0 || PhotoViewer.this.sendPhotoType == 4 || ((PhotoViewer.this.sendPhotoType == 2 || PhotoViewer.this.sendPhotoType == 5) && PhotoViewer.this.imagesArrLocals.size() > 1)) {
                            PhotoViewer.this.checkImageView.setVisibility(8);
                            PhotoViewer.this.photosCounterView.setVisibility(8);
                        }
                        if (PhotoViewer.this.centerImage.getBitmap() != null) {
                            float bitmapWidth2 = PhotoViewer.this.centerImage.getBitmapWidth();
                            float bitmapHeight2 = PhotoViewer.this.centerImage.getBitmapHeight();
                            float min4 = Math.min(PhotoViewer.this.getContainerViewWidth(2) / bitmapWidth2, PhotoViewer.this.getContainerViewHeight(2) / bitmapHeight2);
                            if (PhotoViewer.this.sendPhotoType == 1) {
                                PhotoViewer.this.animateToY = -AndroidUtilities.dp(36.0f);
                                min3 = PhotoViewer.this.getCropFillScale(false);
                            } else {
                                PhotoViewer.this.animateToY = (-AndroidUtilities.dp(93.0f)) + (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight / 2 : 0);
                                min3 = (PhotoViewer.this.editState.cropState == null || !(PhotoViewer.this.editState.cropState.transformRotation == 90 || PhotoViewer.this.editState.cropState.transformRotation == 270)) ? Math.min(PhotoViewer.this.getContainerViewWidth() / bitmapWidth2, PhotoViewer.this.getContainerViewHeight() / bitmapHeight2) : Math.min(PhotoViewer.this.getContainerViewWidth() / bitmapHeight2, PhotoViewer.this.getContainerViewHeight() / bitmapWidth2);
                            }
                            PhotoViewer.this.animateToScale = min4 / min3;
                            PhotoViewer.this.animateToX = (photoViewer.getLeftInset() / 2) - (PhotoViewer.this.getRightInset() / 2);
                            PhotoViewer.this.animationStartTime = System.currentTimeMillis();
                            PhotoViewer.this.zoomAnimation = true;
                        }
                        PhotoViewer.this.imageMoveAnimation = new AnimatorSet();
                        PhotoViewer.this.imageMoveAnimation.playTogether(ObjectAnimator.ofFloat(PhotoViewer.this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f), ObjectAnimator.ofFloat(PhotoViewer.this.photoFilterView.getToolsView(), View.TRANSLATION_Y, AndroidUtilities.dp(186.0f), 0.0f));
                        PhotoViewer.this.imageMoveAnimation.setDuration(200L);
                        PhotoViewer.this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.59.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationStart(Animator animator2) {
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator2) {
                                PhotoViewer.this.photoFilterView.init();
                                PhotoViewer.this.imageMoveAnimation = null;
                                AnonymousClass59 anonymousClass59 = AnonymousClass59.this;
                                PhotoViewer.this.currentEditMode = i;
                                PhotoViewer.this.switchingToMode = -1;
                                PhotoViewer.this.animateToScale = 1.0f;
                                PhotoViewer.this.animateToX = 0.0f;
                                PhotoViewer.this.animateToY = 0.0f;
                                PhotoViewer.this.scale = 1.0f;
                                PhotoViewer photoViewer2 = PhotoViewer.this;
                                photoViewer2.updateMinMax(photoViewer2.scale);
                                PhotoViewer.this.padImageForHorizontalInsets = true;
                                PhotoViewer.this.containerView.invalidate();
                            }
                        });
                        PhotoViewer.this.imageMoveAnimation.start();
                    }
                });
                this.changeModeAnimation.start();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$switchToEditMode$59(ValueAnimator valueAnimator) {
        this.photoCropView.cropView.areaView.setRotationScaleTranslation(0.0f, AndroidUtilities.lerp(this.scale, this.animateToScale, this.animationValue), AndroidUtilities.lerp(this.translationX, this.animateToX, this.animationValue), AndroidUtilities.lerp(this.translationY, this.animateToY, this.animationValue));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$switchToEditMode$60(View view) {
        applyCurrentEditMode();
        switchToEditMode(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$switchToEditMode$62(View view) {
        if (this.photoFilterView.hasChanges()) {
            Activity activity = this.parentActivity;
            if (activity == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, this.resourcesProvider);
            builder.setMessage(LocaleController.getString("DiscardChanges", R.string.DiscardChanges));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda11
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PhotoViewer.this.lambda$switchToEditMode$61(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showAlertDialog(builder);
            return;
        }
        switchToEditMode(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$switchToEditMode$61(DialogInterface dialogInterface, int i) {
        switchToEditMode(0);
    }

    private void createPaintView() {
        int bitmapWidth;
        int bitmapHeight;
        MediaController.CropState cropState;
        if (this.photoPaintView == null) {
            TextureView textureView = this.videoTextureView;
            if (textureView != null) {
                VideoEditTextureView videoEditTextureView = (VideoEditTextureView) textureView;
                bitmapWidth = videoEditTextureView.getVideoWidth();
                bitmapHeight = videoEditTextureView.getVideoHeight();
                while (true) {
                    if (bitmapWidth <= 1280 && bitmapHeight <= 1280) {
                        break;
                    }
                    bitmapWidth /= 2;
                    bitmapHeight /= 2;
                }
            } else {
                bitmapWidth = this.centerImage.getBitmapWidth();
                bitmapHeight = this.centerImage.getBitmapHeight();
            }
            Bitmap bitmap = this.paintingOverlay.getBitmap();
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
            }
            Bitmap bitmap2 = bitmap;
            if (this.sendPhotoType == 1) {
                cropState = new MediaController.CropState();
                cropState.transformRotation = this.cropTransform.getOrientation();
            } else {
                cropState = this.editState.cropState;
            }
            PhotoPaintView photoPaintView = new PhotoPaintView(this.parentActivity, bitmap2, this.isCurrentVideo ? null : this.centerImage.getBitmap(), this.centerImage.getOrientation(), this.editState.mediaEntities, cropState, new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda57
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.this.lambda$createPaintView$63();
                }
            }, this.resourcesProvider) { // from class: org.telegram.ui.PhotoViewer.61
                @Override // org.telegram.ui.Components.PhotoPaintView
                protected void onOpenCloseStickersAlert(boolean z) {
                    if (PhotoViewer.this.videoPlayer == null) {
                        return;
                    }
                    PhotoViewer.this.manuallyPaused = false;
                    PhotoViewer.this.cancelVideoPlayRunnable();
                    if (z) {
                        PhotoViewer.this.videoPlayer.pause();
                    } else {
                        PhotoViewer.this.videoPlayer.play();
                    }
                }

                @Override // org.telegram.ui.Components.PhotoPaintView
                protected void didSetAnimatedSticker(RLottieDrawable rLottieDrawable) {
                    if (PhotoViewer.this.videoPlayer == null) {
                        return;
                    }
                    long currentPosition = PhotoViewer.this.videoPlayer.getCurrentPosition();
                    long j = 0;
                    if (PhotoViewer.this.startTime > 0) {
                        j = PhotoViewer.this.startTime / 1000;
                    }
                    rLottieDrawable.setProgressMs(currentPosition - j);
                }

                @Override // org.telegram.ui.Components.PhotoPaintView
                protected void onTextAdd() {
                    if (!PhotoViewer.this.windowView.isFocusable()) {
                        PhotoViewer.this.makeFocusable();
                    }
                }
            };
            this.photoPaintView = photoPaintView;
            this.containerView.addView(photoPaintView, LayoutHelper.createFrame(-1, -1.0f));
            this.photoPaintView.getDoneTextView().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda39
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$createPaintView$64(view);
                }
            });
            this.photoPaintView.getCancelTextView().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda41
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PhotoViewer.this.lambda$createPaintView$65(view);
                }
            });
            this.photoPaintView.getColorPicker().setTranslationY(AndroidUtilities.dp(126.0f));
            this.photoPaintView.getToolsView().setTranslationY(AndroidUtilities.dp(126.0f));
            this.photoPaintView.getColorPickerBackground().setTranslationY(AndroidUtilities.dp(126.0f));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createPaintView$63() {
        this.paintingOverlay.hideBitmap();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createPaintView$64(View view) {
        this.savedState = null;
        applyCurrentEditMode();
        switchToEditMode(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createPaintView$65(View view) {
        closePaintMode();
    }

    private void closePaintMode() {
        this.photoPaintView.maybeShowDismissalAlert(this, this.parentActivity, new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda59
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.lambda$closePaintMode$66();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$closePaintMode$66() {
        switchToEditMode(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchToPaintMode() {
        this.changeModeAnimation = null;
        this.pickerView.setVisibility(8);
        this.pickerViewSendButton.setVisibility(8);
        this.cameraItem.setVisibility(8);
        this.muteItem.setVisibility(8);
        PhotoCropView photoCropView = this.photoCropView;
        if (photoCropView != null) {
            photoCropView.setVisibility(4);
        }
        this.selectedPhotosListView.setVisibility(8);
        this.selectedPhotosListView.setAlpha(0.0f);
        this.selectedPhotosListView.setTranslationY(-AndroidUtilities.dp(10.0f));
        this.photosCounterView.setRotationX(0.0f);
        this.selectedPhotosListView.setEnabled(false);
        this.isPhotosListViewVisible = false;
        if (this.needCaptionLayout) {
            this.captionTextViewSwitcher.setVisibility(4);
        }
        int i = this.sendPhotoType;
        if (i == 0 || i == 4 || ((i == 2 || i == 5) && this.imagesArrLocals.size() > 1)) {
            this.checkImageView.setVisibility(8);
            this.photosCounterView.setVisibility(8);
        }
        if (this.centerImage.getBitmap() != null) {
            int bitmapWidth = this.centerImage.getBitmapWidth();
            int bitmapHeight = this.centerImage.getBitmapHeight();
            if (this.sendPhotoType == 1) {
                this.animateToY = AndroidUtilities.dp(12.0f);
                if (this.cropTransform.getOrientation() == 90 || this.cropTransform.getOrientation() == 270) {
                    bitmapHeight = bitmapWidth;
                    bitmapWidth = bitmapHeight;
                }
            } else {
                this.animateToY = (-AndroidUtilities.dp(44.0f)) + (isStatusBarVisible() ? AndroidUtilities.statusBarHeight / 2 : 0);
                MediaController.CropState cropState = this.editState.cropState;
                if (cropState != null) {
                    int i2 = cropState.transformRotation;
                    if (i2 == 90 || i2 == 270) {
                        bitmapHeight = bitmapWidth;
                        bitmapWidth = bitmapHeight;
                    }
                    bitmapWidth = (int) (bitmapWidth * cropState.cropPw);
                    bitmapHeight = (int) (bitmapHeight * cropState.cropPh);
                }
            }
            float f = bitmapWidth;
            float f2 = bitmapHeight;
            this.animateToScale = Math.min(getContainerViewWidth(3) / f, getContainerViewHeight(3) / f2) / Math.min(getContainerViewWidth() / f, getContainerViewHeight() / f2);
            this.animateToX = (getLeftInset() / 2) - (getRightInset() / 2);
            this.animationStartTime = System.currentTimeMillis();
            this.zoomAnimation = true;
        }
        this.windowView.setClipChildren(true);
        this.navigationBar.setVisibility(4);
        AnimatorSet animatorSet = new AnimatorSet();
        this.imageMoveAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.photoPaintView.getColorPicker(), View.TRANSLATION_Y, AndroidUtilities.dp(126.0f), 0.0f), ObjectAnimator.ofFloat(this.photoPaintView.getToolsView(), View.TRANSLATION_Y, AndroidUtilities.dp(126.0f), 0.0f), ObjectAnimator.ofFloat(this.photoPaintView.getColorPickerBackground(), View.TRANSLATION_Y, AndroidUtilities.dp(126.0f), 0.0f));
        this.imageMoveAnimation.setDuration(200L);
        this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.62
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                PhotoViewer.this.photoPaintView.init();
                PhotoViewer.this.paintingOverlay.hideEntities();
                PhotoViewer.this.imageMoveAnimation = null;
                PhotoViewer.this.currentEditMode = 3;
                PhotoViewer.this.switchingToMode = -1;
                PhotoViewer photoViewer = PhotoViewer.this;
                photoViewer.animateToScale = photoViewer.scale = 1.0f;
                PhotoViewer.this.animateToX = 0.0f;
                PhotoViewer.this.animateToY = 0.0f;
                PhotoViewer photoViewer2 = PhotoViewer.this;
                photoViewer2.updateMinMax(photoViewer2.scale);
                PhotoViewer.this.padImageForHorizontalInsets = true;
                PhotoViewer.this.containerView.invalidate();
            }
        });
        this.imageMoveAnimation.start();
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
        animatorSet.setDuration(200L);
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
        this.miniProgressAnimator.setDuration(200L);
        this.miniProgressAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.63
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(PhotoViewer.this.miniProgressAnimator)) {
                    if (!z) {
                        PhotoViewer.this.miniProgressView.setVisibility(4);
                    }
                    PhotoViewer.this.miniProgressAnimator = null;
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                if (animator.equals(PhotoViewer.this.miniProgressAnimator)) {
                    PhotoViewer.this.miniProgressAnimator = null;
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
                AndroidUtilities.runOnUIThread(this.miniProgressShowRunnable, 500L);
                return;
            }
            AnimatorSet animatorSet2 = this.miniProgressAnimator;
            if (animatorSet2 == null) {
                return;
            }
            animatorSet2.cancel();
            toggleMiniProgressInternal(false);
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
        if (Build.VERSION.SDK_INT < 21 || this.sendPhotoType == 1 || (frameLayoutDrawer = this.containerView) == null) {
            return;
        }
        int i = 1792;
        if (!z) {
            i = 1796;
            if (frameLayoutDrawer.getPaddingLeft() > 0 || this.containerView.getPaddingRight() > 0) {
                i = 5894;
            }
        }
        this.containerView.setSystemUiVisibility(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class ActionBarToggleParams {
        public static final ActionBarToggleParams DEFAULT = new ActionBarToggleParams();
        public Interpolator animationInterpolator;
        public int animationDuration = 200;
        public boolean enableStatusBarAnimation = true;
        public boolean enableTranslationAnimation = true;

        public ActionBarToggleParams enableStatusBarAnimation(boolean z) {
            this.enableStatusBarAnimation = z;
            return this;
        }

        public ActionBarToggleParams enableTranslationAnimation(boolean z) {
            this.enableTranslationAnimation = z;
            return this;
        }

        public ActionBarToggleParams animationDuration(int i) {
            this.animationDuration = i;
            return this;
        }

        public ActionBarToggleParams animationInterpolator(Interpolator interpolator) {
            this.animationInterpolator = interpolator;
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleActionBar(boolean z, boolean z2) {
        toggleActionBar(z, z2, ActionBarToggleParams.DEFAULT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleActionBar(final boolean z, boolean z2, ActionBarToggleParams actionBarToggleParams) {
        CaptionScrollView captionScrollView;
        CaptionScrollView captionScrollView2;
        if (this.currentEditMode == 0) {
            int i = this.switchingToMode;
            if (i != 0 && i != -1) {
                return;
            }
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
            PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
            if (photoViewerWebView != null) {
                photoViewerWebView.setTouchDisabled(z);
            }
            if (actionBarToggleParams.enableStatusBarAnimation) {
                updateContainerFlags(z);
            }
            if (this.videoPlayerControlVisible && this.isPlaying && z) {
                scheduleActionBarHide();
            } else {
                AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
            }
            if (!z) {
                Bulletin.hide(this.containerView);
            }
            float dpf2 = AndroidUtilities.dpf2(24.0f);
            this.videoPlayerControlFrameLayout.setSeekBarTransitionEnabled(actionBarToggleParams.enableTranslationAnimation && this.playerLooping);
            this.videoPlayerControlFrameLayout.setTranslationYAnimationEnabled(actionBarToggleParams.enableTranslationAnimation);
            float f = 1.0f;
            if (z2) {
                ArrayList arrayList = new ArrayList();
                ActionBar actionBar = this.actionBar;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(actionBar, property, fArr));
                if (actionBarToggleParams.enableTranslationAnimation) {
                    ActionBar actionBar2 = this.actionBar;
                    Property property2 = View.TRANSLATION_Y;
                    float[] fArr2 = new float[1];
                    fArr2[0] = z ? 0.0f : -dpf2;
                    arrayList.add(ObjectAnimator.ofFloat(actionBar2, property2, fArr2));
                } else {
                    this.actionBar.setTranslationY(0.0f);
                }
                if (this.allowShowFullscreenButton) {
                    ImageView imageView = this.fullscreenButton[0];
                    Property property3 = View.ALPHA;
                    float[] fArr3 = new float[1];
                    fArr3[0] = z ? 1.0f : 0.0f;
                    arrayList.add(ObjectAnimator.ofFloat(imageView, property3, fArr3));
                }
                for (int i2 = 1; i2 < 3; i2++) {
                    this.fullscreenButton[i2].setTranslationY(z ? 0.0f : dpf2);
                }
                if (actionBarToggleParams.enableTranslationAnimation) {
                    ImageView imageView2 = this.fullscreenButton[0];
                    Property property4 = View.TRANSLATION_Y;
                    float[] fArr4 = new float[1];
                    fArr4[0] = z ? 0.0f : dpf2;
                    arrayList.add(ObjectAnimator.ofFloat(imageView2, property4, fArr4));
                } else {
                    this.fullscreenButton[0].setTranslationY(0.0f);
                }
                FrameLayout frameLayout = this.bottomLayout;
                if (frameLayout != null) {
                    Property property5 = View.ALPHA;
                    float[] fArr5 = new float[1];
                    fArr5[0] = z ? 1.0f : 0.0f;
                    arrayList.add(ObjectAnimator.ofFloat(frameLayout, property5, fArr5));
                    if (actionBarToggleParams.enableTranslationAnimation) {
                        FrameLayout frameLayout2 = this.bottomLayout;
                        Property property6 = View.TRANSLATION_Y;
                        float[] fArr6 = new float[1];
                        fArr6[0] = z ? 0.0f : dpf2;
                        arrayList.add(ObjectAnimator.ofFloat(frameLayout2, property6, fArr6));
                    } else {
                        this.bottomLayout.setTranslationY(0.0f);
                    }
                }
                View view = this.navigationBar;
                if (view != null) {
                    Property property7 = View.ALPHA;
                    float[] fArr7 = new float[1];
                    fArr7[0] = z ? 1.0f : 0.0f;
                    arrayList.add(ObjectAnimator.ofFloat(view, property7, fArr7));
                }
                if (this.videoPlayerControlVisible) {
                    VideoPlayerControlFrameLayout videoPlayerControlFrameLayout = this.videoPlayerControlFrameLayout;
                    Property<VideoPlayerControlFrameLayout, Float> property8 = VPC_PROGRESS;
                    float[] fArr8 = new float[1];
                    fArr8[0] = z ? 1.0f : 0.0f;
                    arrayList.add(ObjectAnimator.ofFloat(videoPlayerControlFrameLayout, property8, fArr8));
                } else {
                    this.videoPlayerControlFrameLayout.setProgress(z ? 1.0f : 0.0f);
                }
                GroupedPhotosListView groupedPhotosListView = this.groupedPhotosListView;
                Property property9 = View.ALPHA;
                float[] fArr9 = new float[1];
                fArr9[0] = z ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView, property9, fArr9));
                if (actionBarToggleParams.enableTranslationAnimation) {
                    GroupedPhotosListView groupedPhotosListView2 = this.groupedPhotosListView;
                    Property property10 = View.TRANSLATION_Y;
                    float[] fArr10 = new float[1];
                    fArr10[0] = z ? 0.0f : dpf2;
                    arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView2, property10, fArr10));
                } else {
                    this.groupedPhotosListView.setTranslationY(0.0f);
                }
                if (!this.needCaptionLayout && (captionScrollView2 = this.captionScrollView) != null) {
                    Property property11 = View.ALPHA;
                    float[] fArr11 = new float[1];
                    fArr11[0] = z ? 1.0f : 0.0f;
                    arrayList.add(ObjectAnimator.ofFloat(captionScrollView2, property11, fArr11));
                    if (actionBarToggleParams.enableTranslationAnimation) {
                        CaptionScrollView captionScrollView3 = this.captionScrollView;
                        Property property12 = View.TRANSLATION_Y;
                        float[] fArr12 = new float[1];
                        if (z) {
                            dpf2 = 0.0f;
                        }
                        fArr12[0] = dpf2;
                        arrayList.add(ObjectAnimator.ofFloat(captionScrollView3, property12, fArr12));
                    } else {
                        this.captionScrollView.setTranslationY(0.0f);
                    }
                }
                if (this.videoPlayerControlVisible && this.isPlaying) {
                    float[] fArr13 = new float[2];
                    fArr13[0] = this.photoProgressViews[0].animAlphas[1];
                    fArr13[1] = z ? 1.0f : 0.0f;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr13);
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda3
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            PhotoViewer.this.lambda$toggleActionBar$67(valueAnimator);
                        }
                    });
                    arrayList.add(ofFloat);
                }
                if (this.muteItem.getTag() != null) {
                    ImageView imageView3 = this.muteItem;
                    Property property13 = View.ALPHA;
                    float[] fArr14 = new float[1];
                    if (!z) {
                        f = 0.0f;
                    }
                    fArr14[0] = f;
                    arrayList.add(ObjectAnimator.ofFloat(imageView3, property13, fArr14));
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.actionBarAnimator = animatorSet2;
                animatorSet2.playTogether(arrayList);
                this.actionBarAnimator.setDuration(actionBarToggleParams.animationDuration);
                this.actionBarAnimator.setInterpolator(actionBarToggleParams.animationInterpolator);
                this.actionBarAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.64
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
                            PhotoViewer.this.actionBarAnimator = null;
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                        if (animator.equals(PhotoViewer.this.actionBarAnimator)) {
                            PhotoViewer.this.actionBarAnimator = null;
                        }
                    }
                });
                this.actionBarAnimator.start();
                return;
            }
            this.actionBar.setAlpha(z ? 1.0f : 0.0f);
            if (this.fullscreenButton[0].getTranslationX() != 0.0f && this.allowShowFullscreenButton) {
                this.fullscreenButton[0].setAlpha(z ? 1.0f : 0.0f);
            }
            for (int i3 = 0; i3 < 3; i3++) {
                this.fullscreenButton[i3].setTranslationY(z ? 0.0f : dpf2);
            }
            this.actionBar.setTranslationY(z ? 0.0f : -dpf2);
            this.bottomLayout.setAlpha(z ? 1.0f : 0.0f);
            this.bottomLayout.setTranslationY(z ? 0.0f : dpf2);
            this.navigationBar.setAlpha(z ? 1.0f : 0.0f);
            this.groupedPhotosListView.setAlpha(z ? 1.0f : 0.0f);
            this.groupedPhotosListView.setTranslationY(z ? 0.0f : dpf2);
            if (!this.needCaptionLayout && (captionScrollView = this.captionScrollView) != null) {
                captionScrollView.setAlpha(z ? 1.0f : 0.0f);
                CaptionScrollView captionScrollView4 = this.captionScrollView;
                if (z) {
                    dpf2 = 0.0f;
                }
                captionScrollView4.setTranslationY(dpf2);
            }
            this.videoPlayerControlFrameLayout.setProgress(z ? 1.0f : 0.0f);
            if (this.muteItem.getTag() != null) {
                this.muteItem.setAlpha(z ? 1.0f : 0.0f);
            }
            if (!this.videoPlayerControlVisible || !this.isPlaying) {
                return;
            }
            PhotoProgressView photoProgressView = this.photoProgressViews[0];
            if (!z) {
                f = 0.0f;
            }
            photoProgressView.setIndexedAlpha(1, f, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$toggleActionBar$67(ValueAnimator valueAnimator) {
        this.photoProgressViews[0].setIndexedAlpha(1, ((Float) valueAnimator.getAnimatedValue()).floatValue(), false);
    }

    private void togglePhotosListView(boolean z, boolean z2) {
        if (z == this.isPhotosListViewVisible) {
            return;
        }
        if (z) {
            this.selectedPhotosListView.setVisibility(0);
        }
        this.isPhotosListViewVisible = z;
        this.selectedPhotosListView.setEnabled(z);
        float f = 1.0f;
        if (z2) {
            ArrayList arrayList = new ArrayList();
            SelectedPhotosListView selectedPhotosListView = this.selectedPhotosListView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(selectedPhotosListView, property, fArr));
            SelectedPhotosListView selectedPhotosListView2 = this.selectedPhotosListView;
            Property property2 = View.TRANSLATION_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 0.0f : -AndroidUtilities.dp(10.0f);
            arrayList.add(ObjectAnimator.ofFloat(selectedPhotosListView2, property2, fArr2));
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
                this.currentListViewAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.65
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (PhotoViewer.this.currentListViewAnimation == null || !PhotoViewer.this.currentListViewAnimation.equals(animator)) {
                            return;
                        }
                        PhotoViewer.this.selectedPhotosListView.setVisibility(8);
                        PhotoViewer.this.currentListViewAnimation = null;
                    }
                });
            }
            this.currentListViewAnimation.setDuration(200L);
            this.currentListViewAnimation.start();
            return;
        }
        this.selectedPhotosListView.setAlpha(z ? 1.0f : 0.0f);
        this.selectedPhotosListView.setTranslationY(z ? 0.0f : -AndroidUtilities.dp(10.0f));
        CounterView counterView2 = this.photosCounterView;
        if (!z) {
            f = 0.0f;
        }
        counterView2.setRotationX(f);
        if (z) {
            return;
        }
        this.selectedPhotosListView.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleVideoPlayer() {
        PhotoViewerWebView photoViewerWebView;
        if (this.videoPlayer != null || ((photoViewerWebView = this.photoViewerWebView) != null && photoViewerWebView.isControllable())) {
            boolean isPlaying = this.videoPlayer != null ? this.isPlaying : this.photoViewerWebView.isPlaying();
            cancelVideoPlayRunnable();
            AndroidUtilities.cancelRunOnUIThread(this.hideActionBarRunnable);
            if (isPlaying) {
                pauseVideoOrWeb();
            } else {
                if (this.isCurrentVideo) {
                    if (Math.abs(this.videoTimelineView.getProgress() - this.videoTimelineView.getRightProgress()) < 0.01f || this.videoPlayer.getCurrentPosition() == this.videoPlayer.getDuration()) {
                        seekVideoOrWebToProgress(this.videoTimelineView.getLeftProgress());
                    }
                } else {
                    if (Math.abs(this.videoPlayerSeekbar.getProgress() - this.videoTimelineView.getRightProgress()) < 0.01f || this.videoPlayer.getCurrentPosition() == this.videoPlayer.getDuration()) {
                        seekVideoOrWebToProgress(0.0f);
                    }
                    scheduleActionBarHide();
                }
                playVideoOrWeb();
            }
            this.containerView.invalidate();
        }
    }

    private String getFileName(int i) {
        if (i < 0) {
            return null;
        }
        if (!this.secureDocuments.isEmpty()) {
            if (i >= this.secureDocuments.size()) {
                return null;
            }
            SecureDocument secureDocument = this.secureDocuments.get(i);
            return secureDocument.secureFile.dc_id + "_" + secureDocument.secureFile.id + ".jpg";
        } else if (!this.imagesArrLocations.isEmpty() || !this.imagesArr.isEmpty()) {
            if (!this.imagesArrLocations.isEmpty()) {
                if (i >= this.imagesArrLocations.size()) {
                    return null;
                }
                ImageLocation imageLocation = this.imagesArrLocations.get(i);
                ImageLocation imageLocation2 = this.imagesArrLocationsVideo.get(i);
                if (imageLocation == null) {
                    return null;
                }
                if (imageLocation2 != imageLocation) {
                    return imageLocation2.location.volume_id + "_" + imageLocation2.location.local_id + ".mp4";
                }
                return imageLocation.location.volume_id + "_" + imageLocation.location.local_id + ".jpg";
            } else if (i < this.imagesArr.size()) {
                return FileLoader.getMessageFileName(this.imagesArr.get(i).messageOwner);
            } else {
                return null;
            }
        } else {
            if (!this.imagesArrLocals.isEmpty()) {
                if (i >= this.imagesArrLocals.size()) {
                    return null;
                }
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
            } else {
                PageBlocksAdapter pageBlocksAdapter = this.pageBlocksAdapter;
                if (pageBlocksAdapter != null) {
                    return pageBlocksAdapter.getFileName(i);
                }
            }
            return null;
        }
    }

    private ImageLocation getImageLocation(int i, long[] jArr) {
        if (i < 0) {
            return null;
        }
        if (!this.secureDocuments.isEmpty()) {
            if (i >= this.secureDocuments.size()) {
                return null;
            }
            if (jArr != null) {
                jArr[0] = this.secureDocuments.get(i).secureFile.size;
            }
            return ImageLocation.getForSecureDocument(this.secureDocuments.get(i));
        } else if (!this.imagesArrLocations.isEmpty()) {
            if (i >= this.imagesArrLocations.size()) {
                return null;
            }
            if (jArr != null) {
                jArr[0] = this.imagesArrLocationsSizes.get(i).longValue();
            }
            return this.imagesArrLocationsVideo.get(i);
        } else if (this.imagesArr.isEmpty() || i >= this.imagesArr.size()) {
            return null;
        } else {
            MessageObject messageObject = this.imagesArr.get(i);
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if (tLRPC$Message instanceof TLRPC$TL_messageService) {
                if (tLRPC$Message.action instanceof TLRPC$TL_messageActionUserUpdatedPhoto) {
                    return null;
                }
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                if (closestPhotoSizeWithSize != null) {
                    if (jArr != null) {
                        jArr[0] = closestPhotoSizeWithSize.size;
                        if (jArr[0] == 0) {
                            jArr[0] = -1;
                        }
                    }
                    return ImageLocation.getForObject(closestPhotoSizeWithSize, messageObject.photoThumbsObject);
                } else if (jArr != null) {
                    jArr[0] = -1;
                }
            } else if (((MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) && MessageObject.getMedia(messageObject.messageOwner).photo != null) || ((MessageObject.getMedia(messageObject.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) && MessageObject.getMedia(messageObject.messageOwner).webpage != null)) {
                if (messageObject.isGif()) {
                    return ImageLocation.getForDocument(messageObject.getDocument());
                }
                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize(), false, null, true);
                if (closestPhotoSizeWithSize2 != null) {
                    if (jArr != null) {
                        jArr[0] = closestPhotoSizeWithSize2.size;
                        if (jArr[0] == 0) {
                            jArr[0] = -1;
                        }
                    }
                    return ImageLocation.getForObject(closestPhotoSizeWithSize2, messageObject.photoThumbsObject);
                } else if (jArr != null) {
                    jArr[0] = -1;
                }
            } else if (MessageObject.getMedia(messageObject.messageOwner) instanceof TLRPC$TL_messageMediaInvoice) {
                return ImageLocation.getForWebFile(WebFile.createWithWebDocument(((TLRPC$TL_messageMediaInvoice) MessageObject.getMedia(messageObject.messageOwner)).photo));
            } else {
                if (messageObject.getDocument() != null) {
                    TLRPC$Document document = messageObject.getDocument();
                    if (this.sharedMediaType == 5) {
                        return ImageLocation.getForDocument(document);
                    }
                    if (MessageObject.isDocumentHasThumb(messageObject.getDocument())) {
                        TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                        if (jArr != null) {
                            jArr[0] = closestPhotoSizeWithSize3.size;
                            if (jArr[0] == 0) {
                                jArr[0] = -1;
                            }
                        }
                        return ImageLocation.getForDocument(closestPhotoSizeWithSize3, document);
                    }
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TLObject getFileLocation(int i, long[] jArr) {
        if (i < 0) {
            return null;
        }
        if (!this.secureDocuments.isEmpty()) {
            if (i >= this.secureDocuments.size()) {
                return null;
            }
            if (jArr != null) {
                jArr[0] = this.secureDocuments.get(i).secureFile.size;
            }
            return this.secureDocuments.get(i);
        } else if (!this.imagesArrLocations.isEmpty()) {
            if (i >= this.imagesArrLocations.size()) {
                return null;
            }
            if (jArr != null) {
                jArr[0] = this.imagesArrLocationsSizes.get(i).longValue();
            }
            return this.imagesArrLocationsVideo.get(i).location;
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
                    if (jArr != null) {
                        jArr[0] = closestPhotoSizeWithSize.size;
                        if (jArr[0] == 0) {
                            jArr[0] = -1;
                        }
                    }
                    return closestPhotoSizeWithSize;
                } else if (jArr != null) {
                    jArr[0] = -1;
                }
            } else if (((MessageObject.getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) && MessageObject.getMedia(messageObject.messageOwner).photo != null) || ((MessageObject.getMedia(messageObject.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) && MessageObject.getMedia(messageObject.messageOwner).webpage != null)) {
                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize(), false, null, true);
                if (closestPhotoSizeWithSize2 != null) {
                    if (jArr != null) {
                        jArr[0] = closestPhotoSizeWithSize2.size;
                        if (jArr[0] == 0) {
                            jArr[0] = -1;
                        }
                    }
                    return closestPhotoSizeWithSize2;
                } else if (jArr != null) {
                    jArr[0] = -1;
                }
            } else if (MessageObject.getMedia(messageObject.messageOwner) instanceof TLRPC$TL_messageMediaInvoice) {
                return ((TLRPC$TL_messageMediaInvoice) MessageObject.getMedia(messageObject.messageOwner)).photo;
            } else {
                if (messageObject.getDocument() != null && MessageObject.isDocumentHasThumb(messageObject.getDocument())) {
                    TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(messageObject.getDocument().thumbs, 90);
                    if (jArr != null) {
                        jArr[0] = closestPhotoSizeWithSize3.size;
                        if (jArr[0] == 0) {
                            jArr[0] = -1;
                        }
                    }
                    return closestPhotoSizeWithSize3;
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSelectedCount() {
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        if (photoViewerProvider == null) {
            return;
        }
        int selectedCount = photoViewerProvider.getSelectedCount();
        this.photosCounterView.setCount(selectedCount);
        if (selectedCount != 0) {
            return;
        }
        togglePhotosListView(false, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCurrentAvatarSet() {
        int i;
        if (this.currentAvatarLocation != null && (i = this.currentIndex) >= 0 && i < this.avatarsArr.size()) {
            TLRPC$Photo tLRPC$Photo = this.avatarsArr.get(this.currentIndex);
            ImageLocation imageLocation = this.imagesArrLocations.get(this.currentIndex);
            if (tLRPC$Photo instanceof TLRPC$TL_photoEmpty) {
                tLRPC$Photo = null;
            }
            if (tLRPC$Photo != null) {
                int size = tLRPC$Photo.sizes.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TLRPC$FileLocation tLRPC$FileLocation = tLRPC$Photo.sizes.get(i2).location;
                    if (tLRPC$FileLocation != null) {
                        int i3 = tLRPC$FileLocation.local_id;
                        TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = this.currentAvatarLocation.location;
                        if (i3 == tLRPC$TL_fileLocationToBeDeprecated.local_id && tLRPC$FileLocation.volume_id == tLRPC$TL_fileLocationToBeDeprecated.volume_id) {
                            return true;
                        }
                    }
                }
            } else {
                TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated2 = imageLocation.location;
                int i4 = tLRPC$TL_fileLocationToBeDeprecated2.local_id;
                TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated3 = this.currentAvatarLocation.location;
                if (i4 == tLRPC$TL_fileLocationToBeDeprecated3.local_id && tLRPC$TL_fileLocationToBeDeprecated2.volume_id == tLRPC$TL_fileLocationToBeDeprecated3.volume_id) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setItemVisible(View view, boolean z, boolean z2) {
        setItemVisible(view, z, z2, 1.0f);
    }

    private void setItemVisible(final View view, final boolean z, boolean z2, float f) {
        Boolean bool = this.actionBarItemsVisibility.get(view);
        if (bool == null || bool.booleanValue() != z) {
            this.actionBarItemsVisibility.put(view, Boolean.valueOf(z));
            view.animate().cancel();
            float f2 = (z ? 1.0f : 0.0f) * f;
            int i = 0;
            if (z2 && bool != null) {
                if (z) {
                    view.setVisibility(0);
                }
                view.animate().alpha(f2).setDuration(100L).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda74
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.lambda$setItemVisible$68(z, view);
                    }
                }).start();
                return;
            }
            if (!z) {
                i = 8;
            }
            view.setVisibility(i);
            view.setAlpha(f2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$setItemVisible$68(boolean z, View view) {
        if (!z) {
            view.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:339:0x083a  */
    /* JADX WARN: Removed duplicated region for block: B:357:0x0893  */
    /* JADX WARN: Type inference failed for: r11v1 */
    /* JADX WARN: Type inference failed for: r11v2, types: [android.animation.AnimatorSet, java.lang.String] */
    /* JADX WARN: Type inference failed for: r11v23 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onPhotoShow(org.telegram.messenger.MessageObject r19, org.telegram.tgnet.TLRPC$FileLocation r20, org.telegram.messenger.ImageLocation r21, org.telegram.messenger.ImageLocation r22, java.util.ArrayList<org.telegram.messenger.MessageObject> r23, java.util.ArrayList<org.telegram.messenger.SecureDocument> r24, java.util.List<java.lang.Object> r25, int r26, org.telegram.ui.PhotoViewer.PlaceProviderObject r27) {
        /*
            Method dump skipped, instructions count: 2305
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onPhotoShow(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, org.telegram.messenger.ImageLocation, org.telegram.messenger.ImageLocation, java.util.ArrayList, java.util.ArrayList, java.util.List, int, org.telegram.ui.PhotoViewer$PlaceProviderObject):void");
    }

    private boolean canSendMediaToParentChatActivity() {
        TLRPC$Chat tLRPC$Chat;
        ChatActivity chatActivity = this.parentChatActivity;
        return chatActivity != null && (chatActivity.currentUser != null || ((tLRPC$Chat = chatActivity.currentChat) != null && !ChatObject.isNotInChat(tLRPC$Chat) && ChatObject.canSendMedia(this.parentChatActivity.currentChat)));
    }

    private void setDoubleTapEnabled(boolean z) {
        this.doubleTapEnabled = z;
        this.gestureDetector.setOnDoubleTapListener(z ? this : null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setImages() {
        if (this.animationInProgress == 0) {
            setIndexToImage(this.centerImage, this.currentIndex, null);
            setIndexToPaintingOverlay(this.currentIndex, this.paintingOverlay);
            setIndexToImage(this.rightImage, this.currentIndex + 1, this.rightCropTransform);
            setIndexToPaintingOverlay(this.currentIndex + 1, this.rightPaintingOverlay);
            setIndexToImage(this.leftImage, this.currentIndex - 1, this.leftCropTransform);
            setIndexToPaintingOverlay(this.currentIndex - 1, this.leftPaintingOverlay);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:328:0x0787  */
    /* JADX WARN: Removed duplicated region for block: B:329:0x078e  */
    /* JADX WARN: Removed duplicated region for block: B:391:0x08c0  */
    /* JADX WARN: Removed duplicated region for block: B:412:0x09d1  */
    /* JADX WARN: Removed duplicated region for block: B:448:0x0a96  */
    /* JADX WARN: Removed duplicated region for block: B:461:0x0ac5  */
    /* JADX WARN: Removed duplicated region for block: B:468:0x0aeb  */
    /* JADX WARN: Removed duplicated region for block: B:471:0x0afa  */
    /* JADX WARN: Removed duplicated region for block: B:474:0x0b14  */
    /* JADX WARN: Removed duplicated region for block: B:477:0x0b3c  */
    /* JADX WARN: Removed duplicated region for block: B:488:0x0b75  */
    /* JADX WARN: Removed duplicated region for block: B:499:0x0ba8  */
    /* JADX WARN: Removed duplicated region for block: B:500:0x0baa  */
    /* JADX WARN: Removed duplicated region for block: B:503:0x0bb2  */
    /* JADX WARN: Removed duplicated region for block: B:504:0x0bb4  */
    /* JADX WARN: Removed duplicated region for block: B:507:0x0bbc  */
    /* JADX WARN: Removed duplicated region for block: B:508:0x0bbe  */
    /* JADX WARN: Removed duplicated region for block: B:511:0x0bc6  */
    /* JADX WARN: Removed duplicated region for block: B:512:0x0bc8  */
    /* JADX WARN: Removed duplicated region for block: B:515:0x0bd0  */
    /* JADX WARN: Removed duplicated region for block: B:527:0x0be9  */
    /* JADX WARN: Removed duplicated region for block: B:536:0x0bfb  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setIsAboutToSwitchToIndex(int r35, boolean r36, boolean r37) {
        /*
            Method dump skipped, instructions count: 3389
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.setIsAboutToSwitchToIndex(int, boolean, boolean):void");
    }

    private void showVideoTimeline(boolean z, boolean z2) {
        Integer num = null;
        int i = 0;
        if (!z2) {
            this.videoTimelineView.animate().setListener(null).cancel();
            VideoTimelinePlayView videoTimelinePlayView = this.videoTimelineView;
            if (!z) {
                i = 8;
            }
            videoTimelinePlayView.setVisibility(i);
            this.videoTimelineView.setTranslationY(0.0f);
            this.videoTimelineView.setAlpha(this.pickerView.getAlpha());
        } else if (z && this.videoTimelineView.getTag() == null) {
            if (this.videoTimelineView.getVisibility() != 0) {
                this.videoTimelineView.setVisibility(0);
                this.videoTimelineView.setAlpha(this.pickerView.getAlpha());
                this.videoTimelineView.setTranslationY(AndroidUtilities.dp(58.0f));
            }
            ObjectAnimator objectAnimator = this.videoTimelineAnimator;
            if (objectAnimator != null) {
                objectAnimator.removeAllListeners();
                this.videoTimelineAnimator.cancel();
            }
            VideoTimelinePlayView videoTimelinePlayView2 = this.videoTimelineView;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(videoTimelinePlayView2, View.TRANSLATION_Y, videoTimelinePlayView2.getTranslationY(), 0.0f);
            this.videoTimelineAnimator = ofFloat;
            ofFloat.setDuration(220L);
            this.videoTimelineAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.videoTimelineAnimator.start();
        } else if (!z && this.videoTimelineView.getTag() != null) {
            ObjectAnimator objectAnimator2 = this.videoTimelineAnimator;
            if (objectAnimator2 != null) {
                objectAnimator2.removeAllListeners();
                this.videoTimelineAnimator.cancel();
            }
            VideoTimelinePlayView videoTimelinePlayView3 = this.videoTimelineView;
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(videoTimelinePlayView3, View.TRANSLATION_Y, videoTimelinePlayView3.getTranslationY(), AndroidUtilities.dp(58.0f));
            this.videoTimelineAnimator = ofFloat2;
            ofFloat2.addListener(new HideViewAfterAnimation(this.videoTimelineView));
            this.videoTimelineAnimator.setDuration(220L);
            this.videoTimelineAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.videoTimelineAnimator.start();
        }
        VideoTimelinePlayView videoTimelinePlayView4 = this.videoTimelineView;
        if (z) {
            num = 1;
        }
        videoTimelinePlayView4.setTag(num);
    }

    public static TLRPC$FileLocation getFileLocation(ImageLocation imageLocation) {
        if (imageLocation == null) {
            return null;
        }
        return imageLocation.location;
    }

    public static String getFileLocationExt(ImageLocation imageLocation) {
        if (imageLocation == null || imageLocation.imageType != 2) {
            return null;
        }
        return "mp4";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setImageIndex(int i) {
        setImageIndex(i, true, false);
    }

    private void setImageIndex(int i, boolean z, boolean z2) {
        int i2;
        boolean z3;
        boolean isVideo;
        Uri uri;
        boolean z4;
        boolean z5;
        Uri uri2;
        boolean equals;
        boolean z6;
        boolean z7;
        MessageObject messageObject;
        MediaController.CropState cropState;
        ImageReceiver.BitmapHolder bitmapHolder;
        if (this.currentIndex == i || this.placeProvider == null) {
            return;
        }
        if (!z && (bitmapHolder = this.currentThumb) != null) {
            bitmapHolder.release();
            this.currentThumb = null;
        }
        this.currentFileNames[0] = getFileName(i);
        this.currentFileNames[1] = getFileName(i + 1);
        this.currentFileNames[2] = getFileName(i - 1);
        this.placeProvider.willSwitchFromPhoto(this.currentMessageObject, getFileLocation(this.currentFileLocation), this.currentIndex);
        this.lastPhotoSetTime = SystemClock.elapsedRealtime();
        int i3 = this.currentIndex;
        this.currentIndex = i;
        setIsAboutToSwitchToIndex(i, z, z2);
        CropTransform clone = this.cropTransform.clone();
        EditState editState = this.editState;
        MediaController.CropState clone2 = (editState == null || (cropState = editState.cropState) == null) ? null : cropState.clone();
        boolean z8 = this.centerImageIsVideo;
        this.editState.reset();
        if (!this.imagesArr.isEmpty()) {
            int i4 = this.currentIndex;
            if (i4 < 0 || i4 >= this.imagesArr.size()) {
                closePhoto(false, false);
                return;
            }
            MessageObject messageObject2 = this.imagesArr.get(this.currentIndex);
            z3 = z && (messageObject = this.currentMessageObject) != null && messageObject.getId() == messageObject2.getId();
            this.currentMessageObject = messageObject2;
            isVideo = messageObject2.isVideo();
            if (this.sharedMediaType == 1) {
                boolean canPreviewDocument = messageObject2.canPreviewDocument();
                this.canZoom = canPreviewDocument;
                if (canPreviewDocument) {
                    if (this.allowShare) {
                        this.menuItem.showSubItem(1);
                    } else {
                        this.menuItem.hideSubItem(1);
                    }
                    setDoubleTapEnabled(true);
                } else {
                    this.menuItem.hideSubItem(1);
                    setDoubleTapEnabled(false);
                }
            }
            if (isVideo || this.isEmbedVideo) {
                this.speedItem.setVisibility(0);
                this.speedGap.setVisibility(0);
                this.menuItem.showSubItem(19);
            } else {
                this.speedItem.setVisibility(8);
                this.speedGap.setVisibility(8);
                this.menuItem.checkHideMenuItem();
            }
            uri = null;
            i2 = i3;
        } else {
            if (!this.secureDocuments.isEmpty()) {
                if (i < 0 || i >= this.secureDocuments.size()) {
                    closePhoto(false, false);
                    return;
                } else {
                    this.currentSecureDocument = this.secureDocuments.get(i);
                    i2 = i3;
                }
            } else if (!this.imagesArrLocations.isEmpty()) {
                if (i < 0 || i >= this.imagesArrLocations.size()) {
                    closePhoto(false, false);
                    return;
                }
                ImageLocation imageLocation = this.currentFileLocation;
                ImageLocation imageLocation2 = this.imagesArrLocations.get(i);
                if (z && imageLocation != null && imageLocation2 != null) {
                    TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = imageLocation.location;
                    int i5 = tLRPC$TL_fileLocationToBeDeprecated.local_id;
                    TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated2 = imageLocation2.location;
                    if (i5 == tLRPC$TL_fileLocationToBeDeprecated2.local_id) {
                        i2 = i3;
                        if (tLRPC$TL_fileLocationToBeDeprecated.volume_id == tLRPC$TL_fileLocationToBeDeprecated2.volume_id) {
                            z6 = true;
                            this.currentFileLocation = this.imagesArrLocations.get(i);
                            this.currentFileLocationVideo = this.imagesArrLocationsVideo.get(i);
                            z3 = z6;
                            uri = null;
                            isVideo = false;
                        }
                        z6 = false;
                        this.currentFileLocation = this.imagesArrLocations.get(i);
                        this.currentFileLocationVideo = this.imagesArrLocationsVideo.get(i);
                        z3 = z6;
                        uri = null;
                        isVideo = false;
                    }
                }
                i2 = i3;
                z6 = false;
                this.currentFileLocation = this.imagesArrLocations.get(i);
                this.currentFileLocationVideo = this.imagesArrLocationsVideo.get(i);
                z3 = z6;
                uri = null;
                isVideo = false;
            } else {
                i2 = i3;
                if (!this.imagesArrLocals.isEmpty()) {
                    if (i < 0 || i >= this.imagesArrLocals.size()) {
                        closePhoto(false, false);
                        return;
                    }
                    Object obj = this.imagesArrLocals.get(i);
                    if (obj instanceof TLRPC$BotInlineResult) {
                        TLRPC$BotInlineResult tLRPC$BotInlineResult = (TLRPC$BotInlineResult) obj;
                        this.currentBotInlineResult = tLRPC$BotInlineResult;
                        if (tLRPC$BotInlineResult.document != null) {
                            this.currentPathObject = FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$BotInlineResult.document).getAbsolutePath();
                            equals = MessageObject.isVideoDocument(tLRPC$BotInlineResult.document);
                        } else {
                            if (tLRPC$BotInlineResult.photo != null) {
                                this.currentPathObject = FileLoader.getInstance(this.currentAccount).getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(tLRPC$BotInlineResult.photo.sizes, AndroidUtilities.getPhotoSize())).getAbsolutePath();
                            } else {
                                TLRPC$WebDocument tLRPC$WebDocument = tLRPC$BotInlineResult.content;
                                if (tLRPC$WebDocument instanceof TLRPC$TL_webDocument) {
                                    this.currentPathObject = tLRPC$WebDocument.url;
                                    equals = tLRPC$BotInlineResult.type.equals("video");
                                }
                            }
                            equals = false;
                        }
                        isVideo = equals;
                        uri = null;
                        z3 = false;
                    } else {
                        if (obj instanceof MediaController.PhotoEntry) {
                            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) obj;
                            String str = photoEntry.path;
                            this.currentPathObject = str;
                            if (str == null) {
                                closePhoto(false, false);
                                return;
                            }
                            z5 = photoEntry.isVideo;
                            EditState editState2 = this.editState;
                            editState2.savedFilterState = photoEntry.savedFilterState;
                            editState2.paintPath = photoEntry.paintPath;
                            editState2.croppedPaintPath = photoEntry.croppedPaintPath;
                            editState2.croppedMediaEntities = photoEntry.croppedMediaEntities;
                            editState2.averageDuration = photoEntry.averageDuration;
                            editState2.mediaEntities = photoEntry.mediaEntities;
                            editState2.cropState = photoEntry.cropState;
                            File file = new File(photoEntry.path);
                            Uri fromFile = Uri.fromFile(file);
                            if (this.isDocumentsPicker) {
                                StringBuilder sb = new StringBuilder();
                                if (photoEntry.width != 0 && photoEntry.height != 0) {
                                    if (sb.length() > 0) {
                                        sb.append(", ");
                                    }
                                    sb.append(String.format(Locale.US, "%dx%d", Integer.valueOf(photoEntry.width), Integer.valueOf(photoEntry.height)));
                                }
                                if (photoEntry.isVideo) {
                                    if (sb.length() > 0) {
                                        sb.append(", ");
                                    }
                                    sb.append(AndroidUtilities.formatShortDuration(photoEntry.duration));
                                }
                                if (photoEntry.size != 0) {
                                    if (sb.length() > 0) {
                                        sb.append(", ");
                                    }
                                    sb.append(AndroidUtilities.formatFileSize(photoEntry.size));
                                }
                                this.docNameTextView.setText(file.getName());
                                this.docInfoTextView.setText(sb);
                            }
                            z4 = this.savedState != null;
                            uri2 = fromFile;
                        } else {
                            if (obj instanceof MediaController.SearchImage) {
                                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                                this.currentPathObject = searchImage.getPathToAttach();
                                EditState editState3 = this.editState;
                                editState3.savedFilterState = searchImage.savedFilterState;
                                editState3.paintPath = searchImage.paintPath;
                                editState3.croppedPaintPath = searchImage.croppedPaintPath;
                                editState3.croppedMediaEntities = searchImage.croppedMediaEntities;
                                editState3.averageDuration = searchImage.averageDuration;
                                editState3.mediaEntities = searchImage.mediaEntities;
                                editState3.cropState = searchImage.cropState;
                            }
                            z4 = false;
                            z5 = false;
                            uri2 = null;
                        }
                        if (obj instanceof MediaController.MediaEditState) {
                            MediaController.MediaEditState mediaEditState = (MediaController.MediaEditState) obj;
                            if (hasAnimatedMediaEntities()) {
                                this.currentImagePath = mediaEditState.imagePath;
                            } else {
                                String str2 = mediaEditState.filterPath;
                                if (str2 != null) {
                                    this.currentImagePath = str2;
                                } else {
                                    this.currentImagePath = this.currentPathObject;
                                }
                            }
                        }
                        isVideo = z5;
                        uri = uri2;
                        z3 = z4;
                    }
                    MediaController.CropState cropState2 = this.editState.cropState;
                    if (cropState2 != null) {
                        this.previousHasTransform = true;
                        float f = cropState2.cropPx;
                        this.previousCropPx = f;
                        float f2 = cropState2.cropPy;
                        this.previousCropPy = f2;
                        float f3 = cropState2.cropScale;
                        this.previousCropScale = f3;
                        float f4 = cropState2.cropRotate;
                        this.previousCropRotation = f4;
                        int i6 = cropState2.transformRotation;
                        this.previousCropOrientation = i6;
                        float f5 = cropState2.cropPw;
                        this.previousCropPw = f5;
                        float f6 = cropState2.cropPh;
                        this.previousCropPh = f6;
                        boolean z9 = cropState2.mirrored;
                        this.previousCropMirrored = z9;
                        this.cropTransform.setViewTransform(true, f, f2, f4, i6, f3, 1.0f, 1.0f, f5, f6, 0.0f, 0.0f, z9);
                    } else {
                        this.previousHasTransform = false;
                        this.cropTransform.setViewTransform(false, this.previousCropPx, this.previousCropPy, this.previousCropRotation, this.previousCropOrientation, this.previousCropScale, 1.0f, 1.0f, this.previousCropPw, this.previousCropPh, 0.0f, 0.0f, this.previousCropMirrored);
                    }
                } else {
                    PageBlocksAdapter pageBlocksAdapter = this.pageBlocksAdapter;
                    if (pageBlocksAdapter != null) {
                        int i7 = this.currentIndex;
                        if (i7 < 0 || i7 >= pageBlocksAdapter.getItemsCount()) {
                            closePhoto(false, false);
                            return;
                        }
                        TLRPC$PageBlock tLRPC$PageBlock = this.pageBlocksAdapter.get(this.currentIndex);
                        TLRPC$PageBlock tLRPC$PageBlock2 = this.currentPageBlock;
                        z3 = tLRPC$PageBlock2 != null && tLRPC$PageBlock2 == tLRPC$PageBlock;
                        this.currentPageBlock = tLRPC$PageBlock;
                        isVideo = this.pageBlocksAdapter.isVideo(this.currentIndex);
                        uri = null;
                    }
                }
            }
            uri = null;
            z3 = false;
            isVideo = false;
        }
        setMenuItemIcon();
        PlaceProviderObject placeProviderObject = this.currentPlaceObject;
        if (placeProviderObject != null) {
            if (this.animationInProgress == 0) {
                placeProviderObject.imageReceiver.setVisible(true, true);
            } else {
                this.showAfterAnimation = placeProviderObject;
            }
        }
        PlaceProviderObject placeForPhoto = this.placeProvider.getPlaceForPhoto(this.currentMessageObject, getFileLocation(this.currentFileLocation), this.currentIndex, false);
        this.currentPlaceObject = placeForPhoto;
        if (placeForPhoto != null) {
            if (this.animationInProgress == 0) {
                placeForPhoto.imageReceiver.setVisible(false, true);
            } else {
                this.hideAfterAnimation = placeForPhoto;
            }
        }
        if (!z3) {
            this.draggingDown = false;
            this.translationX = 0.0f;
            this.translationY = 0.0f;
            this.scale = 1.0f;
            this.animateToX = 0.0f;
            this.animateToY = 0.0f;
            this.animateToScale = 1.0f;
            this.animateToRotate = 0.0f;
            this.animationStartTime = 0L;
            this.zoomAnimation = false;
            this.imageMoveAnimation = null;
            this.changeModeAnimation = null;
            AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
            if (aspectRatioFrameLayout != null) {
                aspectRatioFrameLayout.setVisibility(4);
            }
            this.pinchStartDistance = 0.0f;
            this.pinchStartScale = 1.0f;
            this.pinchCenterX = 0.0f;
            this.pinchCenterY = 0.0f;
            this.pinchStartX = 0.0f;
            this.pinchStartY = 0.0f;
            this.moveStartX = 0.0f;
            this.moveStartY = 0.0f;
            this.zooming = false;
            this.moving = false;
            this.paintViewTouched = 0;
            this.doubleTap = false;
            this.invalidCoords = false;
            this.canDragDown = true;
            this.changingPage = false;
            this.switchImageAfterAnimation = 0;
            if (this.sharedMediaType != 1) {
                this.canZoom = !this.isEmbedVideo && (!this.imagesArrLocals.isEmpty() || !(this.currentFileNames[0] == null || this.photoProgressViews[0].backgroundState == 0));
            }
            updateMinMax(this.scale);
            z7 = false;
            releasePlayer(false);
        } else {
            z7 = false;
        }
        if (isVideo && uri != null) {
            this.isStreaming = z7;
            preparePlayer(uri, this.sendPhotoType == 1, z7, this.editState.savedFilterState);
        }
        if (this.imagesArrLocals.isEmpty()) {
            this.editState.reset();
        }
        this.centerImageIsVideo = isVideo;
        int i8 = i2;
        if (i8 == -1) {
            setImages();
            for (int i9 = 0; i9 < 3; i9++) {
                checkProgress(i9, false, false);
            }
        } else {
            checkProgress(0, true, false);
            int i10 = this.currentIndex;
            if (i8 > i10) {
                ImageReceiver imageReceiver = this.rightImage;
                this.rightImage = this.centerImage;
                this.centerImage = this.leftImage;
                this.leftImage = imageReceiver;
                this.rightImageIsVideo = z8;
                this.rightCropTransform = clone;
                this.rightCropState = clone2;
                PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
                PhotoProgressView photoProgressView = photoProgressViewArr[0];
                photoProgressViewArr[0] = photoProgressViewArr[2];
                photoProgressViewArr[2] = photoProgressView;
                ImageView[] imageViewArr = this.fullscreenButton;
                ImageView imageView = imageViewArr[0];
                imageViewArr[0] = imageViewArr[2];
                imageViewArr[2] = imageView;
                imageViewArr[0].setTranslationY(imageView.getTranslationY());
                this.leftCropState = null;
                setIndexToPaintingOverlay(this.currentIndex - 1, this.leftPaintingOverlay);
                setIndexToPaintingOverlay(this.currentIndex, this.paintingOverlay);
                setIndexToPaintingOverlay(this.currentIndex + 1, this.rightPaintingOverlay);
                setIndexToImage(this.leftImage, this.currentIndex - 1, this.leftCropTransform);
                updateAccessibilityOverlayVisibility();
                checkProgress(1, true, false);
                checkProgress(2, true, false);
            } else if (i8 < i10) {
                ImageReceiver imageReceiver2 = this.leftImage;
                this.leftImage = this.centerImage;
                this.centerImage = this.rightImage;
                this.rightImage = imageReceiver2;
                this.leftImageIsVideo = z8;
                this.leftCropTransform = clone;
                this.leftCropState = clone2;
                PhotoProgressView[] photoProgressViewArr2 = this.photoProgressViews;
                PhotoProgressView photoProgressView2 = photoProgressViewArr2[0];
                photoProgressViewArr2[0] = photoProgressViewArr2[1];
                photoProgressViewArr2[1] = photoProgressView2;
                ImageView[] imageViewArr2 = this.fullscreenButton;
                ImageView imageView2 = imageViewArr2[0];
                imageViewArr2[0] = imageViewArr2[1];
                imageViewArr2[1] = imageView2;
                imageViewArr2[0].setTranslationY(imageView2.getTranslationY());
                this.rightCropState = null;
                setIndexToPaintingOverlay(this.currentIndex - 1, this.leftPaintingOverlay);
                setIndexToPaintingOverlay(this.currentIndex, this.paintingOverlay);
                setIndexToPaintingOverlay(this.currentIndex + 1, this.rightPaintingOverlay);
                setIndexToImage(this.rightImage, this.currentIndex + 1, this.rightCropTransform);
                updateAccessibilityOverlayVisibility();
                checkProgress(1, true, false);
                checkProgress(2, true, false);
            }
            Bitmap bitmap = this.videoFrameBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.videoFrameBitmap = null;
            }
        }
        detectFaces();
    }

    private void setCurrentCaption(MessageObject messageObject, CharSequence charSequence, boolean z) {
        int i;
        boolean z2;
        CharSequence replaceEmoji;
        int i2;
        CharSequence cloneSpans = AnimatedEmojiSpan.cloneSpans(charSequence);
        boolean z3 = true;
        int i3 = 0;
        if (this.needCaptionLayout) {
            if (this.captionTextViewSwitcher.getParent() != this.pickerView) {
                FrameLayout frameLayout = this.captionContainer;
                if (frameLayout != null) {
                    frameLayout.removeView(this.captionTextViewSwitcher);
                }
                this.captionTextViewSwitcher.setMeasureAllChildren(false);
                this.pickerView.addView(this.captionTextViewSwitcher, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 76.0f, 48.0f));
            }
        } else {
            if (this.captionScrollView == null) {
                this.captionScrollView = new CaptionScrollView(this.containerView.getContext());
                FrameLayout frameLayout2 = new FrameLayout(this.containerView.getContext());
                this.captionContainer = frameLayout2;
                frameLayout2.setClipChildren(false);
                this.captionScrollView.addView(this.captionContainer, new ViewGroup.LayoutParams(-1, -2));
                this.containerView.addView(this.captionScrollView, LayoutHelper.createFrame(-1, -1, 80));
            }
            if (this.captionTextViewSwitcher.getParent() != this.captionContainer) {
                this.pickerView.removeView(this.captionTextViewSwitcher);
                this.captionTextViewSwitcher.setMeasureAllChildren(true);
                this.captionContainer.addView(this.captionTextViewSwitcher, -1, -2);
                this.videoPreviewFrame.bringToFront();
            }
        }
        boolean isEmpty = TextUtils.isEmpty(cloneSpans);
        boolean isEmpty2 = TextUtils.isEmpty(this.captionTextViewSwitcher.getCurrentView().getText());
        CaptionTextViewSwitcher captionTextViewSwitcher = this.captionTextViewSwitcher;
        TextView nextView = z ? captionTextViewSwitcher.getNextView() : captionTextViewSwitcher.getCurrentView();
        if (this.isCurrentVideo) {
            if (nextView.getMaxLines() != 1) {
                this.captionTextViewSwitcher.getCurrentView().setMaxLines(1);
                this.captionTextViewSwitcher.getNextView().setMaxLines(1);
                this.captionTextViewSwitcher.getCurrentView().setSingleLine(true);
                this.captionTextViewSwitcher.getNextView().setSingleLine(true);
                this.captionTextViewSwitcher.getCurrentView().setEllipsize(TextUtils.TruncateAt.END);
                this.captionTextViewSwitcher.getNextView().setEllipsize(TextUtils.TruncateAt.END);
            }
        } else {
            int maxLines = nextView.getMaxLines();
            if (maxLines == 1) {
                this.captionTextViewSwitcher.getCurrentView().setSingleLine(false);
                this.captionTextViewSwitcher.getNextView().setSingleLine(false);
            }
            if (this.needCaptionLayout) {
                Point point = AndroidUtilities.displaySize;
                i = point.x > point.y ? 5 : 10;
            } else {
                i = Integer.MAX_VALUE;
            }
            if (maxLines != i) {
                this.captionTextViewSwitcher.getCurrentView().setMaxLines(i);
                this.captionTextViewSwitcher.getNextView().setMaxLines(i);
                this.captionTextViewSwitcher.getCurrentView().setEllipsize(null);
                this.captionTextViewSwitcher.getNextView().setEllipsize(null);
            }
        }
        nextView.setScrollX(0);
        boolean z4 = this.needCaptionLayout;
        this.dontChangeCaptionPosition = !z4 && z && isEmpty;
        if (!z4) {
            this.captionScrollView.dontChangeTopMargin = false;
        }
        if (z && (i2 = Build.VERSION.SDK_INT) >= 19) {
            if (i2 >= 23) {
                TransitionManager.endTransitions(this.needCaptionLayout ? this.pickerView : this.captionScrollView);
            }
            if (this.needCaptionLayout) {
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.setOrdering(0);
                transitionSet.addTransition(new ChangeBounds());
                transitionSet.addTransition(new Fade(2));
                transitionSet.addTransition(new Fade(1));
                transitionSet.setDuration(200L);
                TransitionManager.beginDelayedTransition(this.pickerView, transitionSet);
            } else {
                TransitionSet duration = new TransitionSet().addTransition(new AnonymousClass67(2, isEmpty2, isEmpty)).addTransition(new AnonymousClass66(1, isEmpty2, isEmpty)).setDuration(200L);
                if (!isEmpty2) {
                    this.captionScrollView.dontChangeTopMargin = true;
                    duration.addTransition(new AnonymousClass68());
                }
                if (isEmpty2 && !isEmpty) {
                    duration.addTarget((View) this.captionTextViewSwitcher);
                }
                TransitionManager.beginDelayedTransition(this.captionScrollView, duration);
            }
            z2 = true;
        } else {
            this.captionTextViewSwitcher.getCurrentView().setText((CharSequence) null);
            CaptionScrollView captionScrollView = this.captionScrollView;
            if (captionScrollView != null) {
                captionScrollView.scrollTo(0, 0);
            }
            z2 = false;
        }
        if (!isEmpty) {
            Theme.createChatResources(null, true);
            if (messageObject != null && !messageObject.messageOwner.entities.isEmpty()) {
                SpannableString spannableString = new SpannableString(cloneSpans);
                messageObject.addEntitiesToText(spannableString, true, false);
                if (messageObject.isVideo()) {
                    MessageObject.addUrlsByPattern(messageObject.isOutOwner(), spannableString, false, 3, messageObject.getDuration(), false);
                }
                replaceEmoji = Emoji.replaceEmoji(spannableString, nextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            } else {
                replaceEmoji = Emoji.replaceEmoji(new SpannableStringBuilder(cloneSpans), nextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
            this.captionTextViewSwitcher.setTag(replaceEmoji);
            try {
                this.captionTextViewSwitcher.setText(replaceEmoji, z);
                CaptionScrollView captionScrollView2 = this.captionScrollView;
                if (captionScrollView2 != null) {
                    captionScrollView2.updateTopMargin();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            nextView.setScrollY(0);
            nextView.setTextColor(-1);
            if (!this.isActionBarVisible || (this.bottomLayout.getVisibility() != 0 && this.pickerView.getVisibility() != 0 && this.pageBlocksAdapter == null)) {
                z3 = false;
            }
            CaptionTextViewSwitcher captionTextViewSwitcher2 = this.captionTextViewSwitcher;
            if (!z3) {
                i3 = 4;
            }
            captionTextViewSwitcher2.setVisibility(i3);
        } else if (this.needCaptionLayout) {
            this.captionTextViewSwitcher.setText(LocaleController.getString("AddCaption", R.string.AddCaption), z);
            this.captionTextViewSwitcher.getCurrentView().setTextColor(-NUM);
            this.captionTextViewSwitcher.setTag("empty");
            this.captionTextViewSwitcher.setVisibility(0);
        } else {
            this.captionTextViewSwitcher.setText(null, z);
            this.captionTextViewSwitcher.getCurrentView().setTextColor(-1);
            CaptionTextViewSwitcher captionTextViewSwitcher3 = this.captionTextViewSwitcher;
            if (z2 && !isEmpty2) {
                z3 = false;
            }
            captionTextViewSwitcher3.setVisibility(4, z3);
            this.captionTextViewSwitcher.setTag(null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$67  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass67 extends Fade {
        final /* synthetic */ boolean val$isCaptionEmpty;
        final /* synthetic */ boolean val$isCurrentCaptionEmpty;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass67(int i, boolean z, boolean z2) {
            super(i);
            this.val$isCurrentCaptionEmpty = z;
            this.val$isCaptionEmpty = z2;
        }

        @Override // android.transition.Fade, android.transition.Visibility
        public Animator onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
            Animator onDisappear = super.onDisappear(viewGroup, view, transitionValues, transitionValues2);
            if (!this.val$isCurrentCaptionEmpty && this.val$isCaptionEmpty && view == PhotoViewer.this.captionTextViewSwitcher) {
                onDisappear.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.67.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        PhotoViewer.this.captionScrollView.setVisibility(4);
                        PhotoViewer.this.captionScrollView.backgroundAlpha = 1.0f;
                    }
                });
                ((ObjectAnimator) onDisappear).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$67$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PhotoViewer.AnonymousClass67.this.lambda$onDisappear$0(valueAnimator);
                    }
                });
            }
            return onDisappear;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDisappear$0(ValueAnimator valueAnimator) {
            PhotoViewer.this.captionScrollView.backgroundAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            PhotoViewer.this.captionScrollView.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$66  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass66 extends Fade {
        final /* synthetic */ boolean val$isCaptionEmpty;
        final /* synthetic */ boolean val$isCurrentCaptionEmpty;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass66(int i, boolean z, boolean z2) {
            super(i);
            this.val$isCurrentCaptionEmpty = z;
            this.val$isCaptionEmpty = z2;
        }

        @Override // android.transition.Fade, android.transition.Visibility
        public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
            Animator onAppear = super.onAppear(viewGroup, view, transitionValues, transitionValues2);
            if (this.val$isCurrentCaptionEmpty && !this.val$isCaptionEmpty && view == PhotoViewer.this.captionTextViewSwitcher) {
                onAppear.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.66.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        PhotoViewer.this.captionScrollView.backgroundAlpha = 1.0f;
                    }
                });
                ((ObjectAnimator) onAppear).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$66$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        PhotoViewer.AnonymousClass66.this.lambda$onAppear$0(valueAnimator);
                    }
                });
            }
            return onAppear;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAppear$0(ValueAnimator valueAnimator) {
            PhotoViewer.this.captionScrollView.backgroundAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            PhotoViewer.this.captionScrollView.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$68  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass68 extends Transition {
        AnonymousClass68() {
        }

        @Override // android.transition.Transition
        public void captureStartValues(TransitionValues transitionValues) {
            if (transitionValues.view == PhotoViewer.this.captionScrollView) {
                transitionValues.values.put("scrollY", Integer.valueOf(PhotoViewer.this.captionScrollView.getScrollY()));
            }
        }

        @Override // android.transition.Transition
        public void captureEndValues(TransitionValues transitionValues) {
            if (transitionValues.view == PhotoViewer.this.captionTextViewSwitcher) {
                transitionValues.values.put("translationY", Integer.valueOf(PhotoViewer.this.captionScrollView.getPendingMarginTopDiff()));
            }
        }

        @Override // android.transition.Transition
        public Animator createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
            int intValue;
            if (transitionValues.view != PhotoViewer.this.captionScrollView) {
                if (transitionValues2.view != PhotoViewer.this.captionTextViewSwitcher || (intValue = ((Integer) transitionValues2.values.get("translationY")).intValue()) == 0) {
                    return null;
                }
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(PhotoViewer.this.captionTextViewSwitcher, View.TRANSLATION_Y, 0.0f, intValue);
                ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.68.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        PhotoViewer.this.captionTextViewSwitcher.setTranslationY(0.0f);
                    }
                });
                return ofFloat;
            }
            ValueAnimator ofInt = ValueAnimator.ofInt(((Integer) transitionValues.values.get("scrollY")).intValue(), 0);
            ofInt.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.68.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    PhotoViewer.this.captionTextViewSwitcher.getNextView().setText((CharSequence) null);
                    PhotoViewer.this.captionScrollView.applyPendingTopMargin();
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    PhotoViewer.this.captionScrollView.stopScrolling();
                }
            });
            ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$68$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PhotoViewer.AnonymousClass68.this.lambda$createAnimator$0(valueAnimator);
                }
            });
            return ofInt;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$createAnimator$0(ValueAnimator valueAnimator) {
            PhotoViewer.this.captionScrollView.scrollTo(0, ((Integer) valueAnimator.getAnimatedValue()).intValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCaptionHwLayerEnabled(boolean z) {
        if (this.captionHwLayerEnabled != z) {
            this.captionHwLayerEnabled = z;
            this.captionTextViewSwitcher.setLayerType(2, null);
            this.captionTextViewSwitcher.getCurrentView().setLayerType(2, null);
            this.captionTextViewSwitcher.getNextView().setLayerType(2, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkProgress(final int i, boolean z, final boolean z2) {
        final File file;
        File file2;
        MessageObject messageObject;
        boolean shouldIndexAutoPlayed;
        boolean z3;
        File pathToAttach;
        boolean z4;
        File file3;
        boolean z5;
        FileLoader.FileResolver fileResolver;
        AnimatedFileDrawable animatedFileDrawable;
        int i2 = this.currentIndex;
        int i3 = i == 1 ? i2 + 1 : i == 2 ? i2 - 1 : i2;
        boolean z6 = false;
        if (this.currentFileNames[i] != null) {
            boolean hasBitmap = (i == 0 && i2 == 0 && (animatedFileDrawable = this.currentAnimation) != null) ? animatedFileDrawable.hasBitmap() : false;
            FileLoader.FileResolver fileResolver2 = null;
            if (this.currentMessageObject != null) {
                if (i3 < 0 || i3 >= this.imagesArr.size()) {
                    this.photoProgressViews[i].setBackgroundState(-1, z2, true);
                    return;
                }
                MessageObject messageObject2 = this.imagesArr.get(i3);
                boolean shouldMessageObjectAutoPlayed = shouldMessageObjectAutoPlayed(messageObject2);
                if (this.sharedMediaType == 1 && !messageObject2.canPreviewDocument()) {
                    this.photoProgressViews[i].setBackgroundState(-1, z2, true);
                    return;
                }
                file2 = !TextUtils.isEmpty(messageObject2.messageOwner.attachPath) ? new File(messageObject2.messageOwner.attachPath) : null;
                if ((MessageObject.getMedia(messageObject2.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) && MessageObject.getMedia(messageObject2.messageOwner).webpage != null && MessageObject.getMedia(messageObject2.messageOwner).webpage.document == null) {
                    final TLObject fileLocation = getFileLocation(i3, null);
                    fileResolver = new FileLoader.FileResolver() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda75
                        @Override // org.telegram.messenger.FileLoader.FileResolver
                        public final File getFile() {
                            File lambda$checkProgress$69;
                            lambda$checkProgress$69 = PhotoViewer.this.lambda$checkProgress$69(fileLocation);
                            return lambda$checkProgress$69;
                        }
                    };
                } else {
                    final TLRPC$Message tLRPC$Message = messageObject2.messageOwner;
                    fileResolver = new FileLoader.FileResolver() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda76
                        @Override // org.telegram.messenger.FileLoader.FileResolver
                        public final File getFile() {
                            File lambda$checkProgress$70;
                            lambda$checkProgress$70 = PhotoViewer.this.lambda$checkProgress$70(tLRPC$Message);
                            return lambda$checkProgress$70;
                        }
                    };
                }
                if (messageObject2.isVideo()) {
                    z4 = SharedConfig.streamMedia && messageObject2.canStreamVideo() && !DialogObject.isEncryptedDialog(messageObject2.getDialogId());
                    shouldIndexAutoPlayed = shouldMessageObjectAutoPlayed;
                    file = null;
                    fileResolver2 = fileResolver;
                    z3 = true;
                } else {
                    shouldIndexAutoPlayed = shouldMessageObjectAutoPlayed;
                    file = null;
                    fileResolver2 = fileResolver;
                    z4 = false;
                    z3 = false;
                }
                messageObject = messageObject2;
            } else {
                if (this.currentBotInlineResult != null) {
                    if (i3 < 0 || i3 >= this.imagesArrLocals.size()) {
                        this.photoProgressViews[i].setBackgroundState(-1, z2, true);
                        return;
                    }
                    TLRPC$BotInlineResult tLRPC$BotInlineResult = (TLRPC$BotInlineResult) this.imagesArrLocals.get(i3);
                    if (tLRPC$BotInlineResult.type.equals("video") || MessageObject.isVideoDocument(tLRPC$BotInlineResult.document)) {
                        if (tLRPC$BotInlineResult.document != null) {
                            file3 = FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$BotInlineResult.document);
                        } else if (tLRPC$BotInlineResult.content instanceof TLRPC$TL_webDocument) {
                            file3 = new File(FileLoader.getDirectory(4), Utilities.MD5(tLRPC$BotInlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(tLRPC$BotInlineResult.content.url, "mp4"));
                        } else {
                            file3 = null;
                        }
                        z5 = true;
                    } else {
                        if (tLRPC$BotInlineResult.document != null) {
                            file3 = new File(FileLoader.getDirectory(3), this.currentFileNames[i]);
                        } else {
                            file3 = tLRPC$BotInlineResult.photo != null ? new File(FileLoader.getDirectory(0), this.currentFileNames[i]) : null;
                        }
                        z5 = false;
                    }
                    file = new File(FileLoader.getDirectory(4), this.currentFileNames[i]);
                    file2 = file3;
                    z3 = z5;
                    messageObject = null;
                    shouldIndexAutoPlayed = false;
                } else {
                    if (this.currentFileLocation != null) {
                        if (i3 < 0 || i3 >= this.imagesArrLocationsVideo.size()) {
                            this.photoProgressViews[i].setBackgroundState(-1, z2, true);
                            return;
                        } else {
                            ImageLocation imageLocation = this.imagesArrLocationsVideo.get(i3);
                            pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(imageLocation.location, getFileLocationExt(imageLocation), this.avatarsDialogId != 0 || this.isEvent);
                        }
                    } else if (this.currentSecureDocument != null) {
                        if (i3 < 0 || i3 >= this.secureDocuments.size()) {
                            this.photoProgressViews[i].setBackgroundState(-1, z2, true);
                            return;
                        }
                        pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.secureDocuments.get(i3), true);
                    } else {
                        if (this.currentPathObject != null) {
                            file2 = new File(FileLoader.getDirectory(3), this.currentFileNames[i]);
                            file = new File(FileLoader.getDirectory(4), this.currentFileNames[i]);
                            messageObject = null;
                        } else {
                            PageBlocksAdapter pageBlocksAdapter = this.pageBlocksAdapter;
                            if (pageBlocksAdapter != null) {
                                File file4 = pageBlocksAdapter.getFile(i3);
                                boolean isVideo = this.pageBlocksAdapter.isVideo(i3);
                                shouldIndexAutoPlayed = shouldIndexAutoPlayed(i3);
                                file2 = file4;
                                z3 = isVideo;
                                file = null;
                                messageObject = null;
                            } else {
                                file = null;
                                file2 = null;
                                messageObject = null;
                            }
                        }
                        shouldIndexAutoPlayed = false;
                        z4 = false;
                        z3 = false;
                    }
                    file2 = pathToAttach;
                    file = null;
                    messageObject = null;
                    shouldIndexAutoPlayed = false;
                    z4 = false;
                    z3 = false;
                }
                z4 = false;
            }
            boolean z7 = (i != 0 || !this.dontAutoPlay) && shouldIndexAutoPlayed;
            final boolean z8 = hasBitmap;
            final File file5 = file2;
            final FileLoader.FileResolver fileResolver3 = fileResolver2;
            final MessageObject messageObject3 = messageObject;
            final boolean z9 = z4;
            final boolean z10 = z3;
            final boolean z11 = z7;
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda73
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.this.lambda$checkProgress$72(z8, file5, file, fileResolver3, i, messageObject3, z9, z10, z11, z2);
                }
            });
            return;
        }
        if (!this.imagesArrLocals.isEmpty() && i3 >= 0 && i3 < this.imagesArrLocals.size()) {
            Object obj = this.imagesArrLocals.get(i3);
            if (obj instanceof MediaController.PhotoEntry) {
                z6 = ((MediaController.PhotoEntry) obj).isVideo;
            }
        }
        if (z6) {
            this.photoProgressViews[i].setBackgroundState(3, z2, true);
        } else {
            this.photoProgressViews[i].setBackgroundState(-1, z2, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ File lambda$checkProgress$69(TLObject tLObject) {
        return FileLoader.getInstance(this.currentAccount).getPathToAttach(tLObject, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ File lambda$checkProgress$70(TLRPC$Message tLRPC$Message) {
        return FileLoader.getInstance(this.currentAccount).getPathToMessage(tLRPC$Message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkProgress$72(boolean z, final File file, File file2, FileLoader.FileResolver fileResolver, final int i, MessageObject messageObject, final boolean z2, final boolean z3, final boolean z4, final boolean z5) {
        final File file3;
        ChatActivity chatActivity;
        TLRPC$Document document;
        boolean exists = (z || file == null) ? z : file.exists();
        File file4 = null;
        if (file2 != null || fileResolver == null) {
            if (fileResolver != null) {
                file4 = fileResolver.getFile();
            }
            file3 = file2;
        } else {
            file3 = fileResolver.getFile();
        }
        if (!exists && file3 != null) {
            exists = file3.exists();
        }
        if (!exists && file4 != null) {
            exists = file4.exists();
        }
        final boolean z6 = exists;
        if (!z6 && i != 0 && messageObject != null && z2 && DownloadController.getInstance(this.currentAccount).canDownloadMedia(messageObject.messageOwner) != 0 && (((chatActivity = this.parentChatActivity) == null || chatActivity.getCurrentEncryptedChat() == null) && !messageObject.shouldEncryptPhotoOrVideo() && (document = messageObject.getDocument()) != null)) {
            FileLoader.getInstance(this.currentAccount).loadFile(document, messageObject, 0, 10);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda63
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.lambda$checkProgress$71(i, file, file3, z6, z2, z3, z4, z5);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkProgress$71(int i, File file, File file2, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        boolean z6 = false;
        if (this.shownControlsByEnd && !this.actionBarWasShownBeforeByEnd && this.isPlaying) {
            this.photoProgressViews[i].setBackgroundState(3, false, false);
            return;
        }
        if ((file != null || file2 != null) && (z || z2)) {
            if (i != 0 || !this.isPlaying) {
                if (z3 && (!z4 || (i == 0 && this.playerWasPlaying))) {
                    this.photoProgressViews[i].setBackgroundState(3, z5, true);
                } else {
                    this.photoProgressViews[i].setBackgroundState(-1, z5, true);
                }
            }
            if (i == 0) {
                if (!z) {
                    if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[i])) {
                        this.menuItem.hideSubItem(7);
                    } else {
                        this.menuItem.showSubItem(7);
                    }
                } else {
                    this.menuItem.hideSubItem(7);
                }
            }
        } else {
            if (z3) {
                if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[i])) {
                    this.photoProgressViews[i].setBackgroundState(2, false, true);
                } else {
                    this.photoProgressViews[i].setBackgroundState(1, false, true);
                }
            } else {
                this.photoProgressViews[i].setBackgroundState(0, z5, true);
            }
            Float fileProgress = ImageLoader.getInstance().getFileProgress(this.currentFileNames[i]);
            if (fileProgress == null) {
                fileProgress = Float.valueOf(0.0f);
            }
            this.photoProgressViews[i].setProgress(fileProgress.floatValue(), false);
        }
        if (i != 0) {
            return;
        }
        if (!this.isEmbedVideo && (!this.imagesArrLocals.isEmpty() || (this.currentFileNames[0] != null && this.photoProgressViews[0].backgroundState != 0))) {
            z6 = true;
        }
        this.canZoom = z6;
    }

    public int getSelectiongLength() {
        PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
        if (photoViewerCaptionEnterView != null) {
            return photoViewerCaptionEnterView.getSelectionLength();
        }
        return 0;
    }

    private void setIndexToPaintingOverlay(int i, PaintingOverlay paintingOverlay) {
        ArrayList<VideoEditedInfo.MediaEntity> arrayList;
        boolean z;
        if (paintingOverlay == null) {
            return;
        }
        paintingOverlay.reset();
        paintingOverlay.setVisibility(8);
        if (this.imagesArrLocals.isEmpty() || i < 0 || i >= this.imagesArrLocals.size()) {
            return;
        }
        Object obj = this.imagesArrLocals.get(i);
        String str = null;
        if (obj instanceof MediaController.PhotoEntry) {
            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) obj;
            z = photoEntry.isVideo;
            str = photoEntry.paintPath;
            arrayList = photoEntry.mediaEntities;
        } else {
            if (obj instanceof MediaController.SearchImage) {
                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                str = searchImage.paintPath;
                arrayList = searchImage.mediaEntities;
            } else {
                arrayList = null;
            }
            z = false;
        }
        paintingOverlay.setVisibility(0);
        paintingOverlay.setData(str, arrayList, z, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:166:0x03a1  */
    /* JADX WARN: Removed duplicated region for block: B:171:0x03dc  */
    /* JADX WARN: Removed duplicated region for block: B:172:0x03e2  */
    /* JADX WARN: Removed duplicated region for block: B:356:0x070c  */
    /* JADX WARN: Removed duplicated region for block: B:368:0x0748  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setIndexToImage(org.telegram.messenger.ImageReceiver r33, int r34, org.telegram.ui.Components.Crop.CropTransform r35) {
        /*
            Method dump skipped, instructions count: 1951
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.setIndexToImage(org.telegram.messenger.ImageReceiver, int, org.telegram.ui.Components.Crop.CropTransform):void");
    }

    public static boolean isShowingImage(MessageObject messageObject) {
        boolean z;
        boolean z2 = true;
        if (Instance != null && !Instance.pipAnimationInProgress && Instance.isVisible && !Instance.disableShowCheck && messageObject != null) {
            MessageObject messageObject2 = Instance.currentMessageObject;
            if (messageObject2 == null && Instance.placeProvider != null) {
                messageObject2 = Instance.placeProvider.getEditingMessageObject();
            }
            if (messageObject2 != null && messageObject2.getId() == messageObject.getId() && messageObject2.getDialogId() == messageObject.getDialogId()) {
                z = true;
                if (z && PipInstance != null) {
                    if (!PipInstance.isVisible || PipInstance.disableShowCheck || messageObject == null || PipInstance.currentMessageObject == null || PipInstance.currentMessageObject.getId() != messageObject.getId() || PipInstance.currentMessageObject.getDialogId() != messageObject.getDialogId()) {
                        z2 = false;
                    }
                    return z2;
                }
            }
        }
        z = false;
        return z ? z : z;
    }

    public static boolean isPlayingMessageInPip(MessageObject messageObject) {
        return (PipInstance == null || messageObject == null || PipInstance.currentMessageObject == null || PipInstance.currentMessageObject.getId() != messageObject.getId() || PipInstance.currentMessageObject.getDialogId() != messageObject.getDialogId()) ? false : true;
    }

    public static boolean isPlayingMessage(MessageObject messageObject) {
        return Instance != null && !Instance.pipAnimationInProgress && Instance.isVisible && messageObject != null && Instance.currentMessageObject != null && Instance.currentMessageObject.getId() == messageObject.getId() && Instance.currentMessageObject.getDialogId() == messageObject.getDialogId();
    }

    public static boolean isShowingImage(TLRPC$FileLocation tLRPC$FileLocation) {
        if (Instance == null || !Instance.isVisible || Instance.disableShowCheck || tLRPC$FileLocation == null) {
            return false;
        }
        return (Instance.currentFileLocation != null && tLRPC$FileLocation.local_id == Instance.currentFileLocation.location.local_id && tLRPC$FileLocation.volume_id == Instance.currentFileLocation.location.volume_id && tLRPC$FileLocation.dc_id == Instance.currentFileLocation.dc_id) || (Instance.currentFileLocationVideo != null && tLRPC$FileLocation.local_id == Instance.currentFileLocationVideo.location.local_id && tLRPC$FileLocation.volume_id == Instance.currentFileLocationVideo.location.volume_id && tLRPC$FileLocation.dc_id == Instance.currentFileLocationVideo.dc_id);
    }

    public static boolean isShowingImage(TLRPC$BotInlineResult tLRPC$BotInlineResult) {
        return Instance != null && Instance.isVisible && !Instance.disableShowCheck && tLRPC$BotInlineResult != null && Instance.currentBotInlineResult != null && tLRPC$BotInlineResult.id == Instance.currentBotInlineResult.id;
    }

    public static boolean isShowingImage(String str) {
        return Instance != null && Instance.isVisible && !Instance.disableShowCheck && str != null && str.equals(Instance.currentPathObject);
    }

    public void setParentChatActivity(ChatActivity chatActivity) {
        this.parentChatActivity = chatActivity;
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        this.maxSelectedPhotos = i;
        this.allowOrder = z;
    }

    public void checkCurrentImageVisibility() {
        PlaceProviderObject placeProviderObject = this.currentPlaceObject;
        if (placeProviderObject != null) {
            placeProviderObject.imageReceiver.setVisible(true, true);
        }
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        PlaceProviderObject placeForPhoto = photoViewerProvider == null ? null : photoViewerProvider.getPlaceForPhoto(this.currentMessageObject, getFileLocation(this.currentFileLocation), this.currentIndex, false);
        this.currentPlaceObject = placeForPhoto;
        if (placeForPhoto != null) {
            placeForPhoto.imageReceiver.setVisible(false, true);
        }
    }

    public boolean openPhoto(MessageObject messageObject, ChatActivity chatActivity, long j, long j2, int i, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(messageObject, null, null, null, null, null, null, 0, photoViewerProvider, chatActivity, j, j2, i, true, null, null);
    }

    public boolean openPhoto(MessageObject messageObject, int i, ChatActivity chatActivity, long j, long j2, int i2, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(messageObject, null, null, null, null, null, null, 0, photoViewerProvider, chatActivity, j, j2, i2, true, null, Integer.valueOf(i));
    }

    public boolean openPhoto(MessageObject messageObject, long j, long j2, int i, PhotoViewerProvider photoViewerProvider, boolean z) {
        return openPhoto(messageObject, null, null, null, null, null, null, 0, photoViewerProvider, null, j, j2, i, z, null, null);
    }

    public boolean openPhoto(TLRPC$FileLocation tLRPC$FileLocation, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(null, tLRPC$FileLocation, null, null, null, null, null, 0, photoViewerProvider, null, 0L, 0L, 0, true, null, null);
    }

    public boolean openPhotoWithVideo(TLRPC$FileLocation tLRPC$FileLocation, ImageLocation imageLocation, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(null, tLRPC$FileLocation, null, imageLocation, null, null, null, 0, photoViewerProvider, null, 0L, 0L, 0, true, null, null);
    }

    public boolean openPhoto(TLRPC$FileLocation tLRPC$FileLocation, ImageLocation imageLocation, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(null, tLRPC$FileLocation, imageLocation, null, null, null, null, 0, photoViewerProvider, null, 0L, 0L, 0, true, null, null);
    }

    public boolean openPhoto(ArrayList<MessageObject> arrayList, int i, long j, long j2, int i2, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(arrayList.get(i), null, null, null, arrayList, null, null, i, photoViewerProvider, null, j, j2, i2, true, null, null);
    }

    public boolean openPhoto(ArrayList<SecureDocument> arrayList, int i, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(null, null, null, null, null, arrayList, null, i, photoViewerProvider, null, 0L, 0L, 0, true, null, null);
    }

    public boolean openPhoto(int i, PageBlocksAdapter pageBlocksAdapter, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(null, null, null, null, null, null, null, i, photoViewerProvider, null, 0L, 0L, 0, true, pageBlocksAdapter, null);
    }

    public boolean openPhotoForSelect(ArrayList<Object> arrayList, int i, int i2, boolean z, PhotoViewerProvider photoViewerProvider, ChatActivity chatActivity) {
        this.isDocumentsPicker = z;
        ImageView imageView = this.pickerViewSendButton;
        if (imageView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            if (i2 == 4 || i2 == 5) {
                this.pickerViewSendButton.setImageResource(R.drawable.attach_send);
                layoutParams.bottomMargin = AndroidUtilities.dp(19.0f);
            } else if (i2 == 1 || i2 == 3 || i2 == 10) {
                this.pickerViewSendButton.setImageResource(R.drawable.floating_check);
                this.pickerViewSendButton.setPadding(0, AndroidUtilities.dp(1.0f), 0, 0);
                layoutParams.bottomMargin = AndroidUtilities.dp(19.0f);
            } else {
                this.pickerViewSendButton.setImageResource(R.drawable.attach_send);
                layoutParams.bottomMargin = AndroidUtilities.dp(14.0f);
            }
            this.pickerViewSendButton.setLayoutParams(layoutParams);
        }
        if (this.sendPhotoType != 1 && i2 == 1 && this.isVisible) {
            this.sendPhotoType = i2;
            this.doneButtonPressed = false;
            this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, 1, 1));
            this.placeProvider = photoViewerProvider;
            this.mergeDialogId = 0L;
            this.currentDialogId = 0L;
            this.selectedPhotosAdapter.notifyDataSetChanged();
            this.pageBlocksAdapter = null;
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.isVisible = true;
            togglePhotosListView(false, false);
            this.openedFullScreenVideo = false;
            createCropView();
            toggleActionBar(false, false);
            this.seekToProgressPending2 = 0.0f;
            this.skipFirstBufferingProgress = false;
            this.playerInjected = false;
            makeFocusable();
            this.backgroundDrawable.setAlpha(255);
            this.containerView.setAlpha(1.0f);
            onPhotoShow(null, null, null, null, null, null, arrayList, i, null);
            initCropView();
            setCropBitmap();
            return true;
        }
        this.sendPhotoType = i2;
        return openPhoto(null, null, null, null, null, null, arrayList, i, photoViewerProvider, chatActivity, 0L, 0L, 0, true, null, null);
    }

    private void openCurrentPhotoInPaintModeForSelect() {
        final File file;
        final MessageObject messageObject;
        final boolean z;
        final boolean z2;
        ActionBarToggleParams enableStatusBarAnimation;
        if (!canSendMediaToParentChatActivity()) {
            return;
        }
        MessageObject messageObject2 = this.currentMessageObject;
        File file2 = null;
        if (messageObject2 != null) {
            boolean z3 = messageObject2.canEditMedia() && !this.currentMessageObject.isDocument();
            boolean isVideo = this.currentMessageObject.isVideo();
            if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
                File file3 = new File(this.currentMessageObject.messageOwner.attachPath);
                if (file3.exists()) {
                    file2 = file3;
                }
            }
            if (file2 == null) {
                file2 = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.currentMessageObject.messageOwner);
            }
            messageObject = messageObject2;
            file = file2;
            z2 = z3;
            z = isVideo;
        } else {
            file = null;
            messageObject = null;
            z = false;
            z2 = false;
        }
        if (file != null && file.exists()) {
            this.savedState = new SavedState(this.currentIndex, new ArrayList(this.imagesArr), this.placeProvider);
            toggleActionBar(false, true, new ActionBarToggleParams().enableStatusBarAnimation(false));
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda64
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.this.lambda$openCurrentPhotoInPaintModeForSelect$73(file, z, messageObject, z2);
                }
            }, enableStatusBarAnimation.animationDuration);
            return;
        }
        showDownloadAlert();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$openCurrentPhotoInPaintModeForSelect$73(File file, boolean z, final MessageObject messageObject, final boolean z2) {
        int i;
        try {
            int attributeInt = new ExifInterface(file.getAbsolutePath()).getAttributeInt("Orientation", 1);
            i = attributeInt != 3 ? attributeInt != 6 ? attributeInt != 8 ? 0 : 270 : 90 : 180;
        } catch (Exception e) {
            FileLog.e(e);
            i = 0;
        }
        int i2 = this.lastImageId;
        this.lastImageId = i2 - 1;
        final MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, i2, 0L, file.getAbsolutePath(), i, z, 0, 0, 0L);
        this.sendPhotoType = 2;
        this.doneButtonPressed = false;
        final PhotoViewerProvider photoViewerProvider = this.placeProvider;
        this.placeProvider = new EmptyPhotoViewerProvider() { // from class: org.telegram.ui.PhotoViewer.69
            private final ImageReceiver.BitmapHolder thumbHolder;

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public boolean canCaptureMorePhotos() {
                return false;
            }

            {
                this.thumbHolder = PhotoViewer.this.centerImage.getBitmapSafe();
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject2, TLRPC$FileLocation tLRPC$FileLocation, int i3, boolean z3) {
                PhotoViewerProvider photoViewerProvider2 = photoViewerProvider;
                if (photoViewerProvider2 != null) {
                    return photoViewerProvider2.getPlaceForPhoto(messageObject, null, 0, z3);
                }
                return null;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject2, TLRPC$FileLocation tLRPC$FileLocation, int i3) {
                return this.thumbHolder;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void sendButtonPressed(int i3, VideoEditedInfo videoEditedInfo, boolean z3, int i4, boolean z4) {
                sendMedia(videoEditedInfo, z3, i4, false, z4);
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void replaceButtonPressed(int i3, VideoEditedInfo videoEditedInfo) {
                MediaController.PhotoEntry photoEntry2 = photoEntry;
                if (photoEntry2.isCropped || photoEntry2.isPainted || photoEntry2.isFiltered || videoEditedInfo != null || !TextUtils.isEmpty(photoEntry2.caption)) {
                    sendMedia(videoEditedInfo, false, 0, true, false);
                }
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public boolean canReplace(int i3) {
                return photoViewerProvider != null && z2;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public MessageObject getEditingMessageObject() {
                return messageObject;
            }

            private void sendMedia(VideoEditedInfo videoEditedInfo, boolean z3, int i3, boolean z4, boolean z5) {
                if (PhotoViewer.this.parentChatActivity != null) {
                    MessageObject messageObject2 = z4 ? messageObject : null;
                    if (messageObject2 != null && !TextUtils.isEmpty(photoEntry.caption)) {
                        MediaController.PhotoEntry photoEntry2 = photoEntry;
                        messageObject2.editingMessage = photoEntry2.caption;
                        messageObject2.editingMessageEntities = photoEntry2.entities;
                    }
                    MediaController.PhotoEntry photoEntry3 = photoEntry;
                    if (photoEntry3.isVideo) {
                        if (videoEditedInfo != null) {
                            AccountInstance accountInstance = PhotoViewer.this.parentChatActivity.getAccountInstance();
                            String str = photoEntry.path;
                            long dialogId = PhotoViewer.this.parentChatActivity.getDialogId();
                            MessageObject replyMessage = PhotoViewer.this.parentChatActivity.getReplyMessage();
                            MessageObject threadMessage = PhotoViewer.this.parentChatActivity.getThreadMessage();
                            MediaController.PhotoEntry photoEntry4 = photoEntry;
                            SendMessagesHelper.prepareSendingVideo(accountInstance, str, videoEditedInfo, dialogId, replyMessage, threadMessage, photoEntry4.caption, photoEntry4.entities, photoEntry4.ttl, messageObject2, z3, i3, z5);
                            return;
                        }
                        AccountInstance accountInstance2 = PhotoViewer.this.parentChatActivity.getAccountInstance();
                        String str2 = photoEntry.path;
                        long dialogId2 = PhotoViewer.this.parentChatActivity.getDialogId();
                        MessageObject replyMessage2 = PhotoViewer.this.parentChatActivity.getReplyMessage();
                        MessageObject threadMessage2 = PhotoViewer.this.parentChatActivity.getThreadMessage();
                        MediaController.PhotoEntry photoEntry5 = photoEntry;
                        SendMessagesHelper.prepareSendingVideo(accountInstance2, str2, null, dialogId2, replyMessage2, threadMessage2, photoEntry5.caption, photoEntry5.entities, photoEntry5.ttl, messageObject2, z3, i3, z5);
                    } else if (photoEntry3.imagePath != null) {
                        AccountInstance accountInstance3 = PhotoViewer.this.parentChatActivity.getAccountInstance();
                        MediaController.PhotoEntry photoEntry6 = photoEntry;
                        String str3 = photoEntry6.imagePath;
                        String str4 = photoEntry6.thumbPath;
                        long dialogId3 = PhotoViewer.this.parentChatActivity.getDialogId();
                        MessageObject replyMessage3 = PhotoViewer.this.parentChatActivity.getReplyMessage();
                        MessageObject threadMessage3 = PhotoViewer.this.parentChatActivity.getThreadMessage();
                        MediaController.PhotoEntry photoEntry7 = photoEntry;
                        SendMessagesHelper.prepareSendingPhoto(accountInstance3, str3, str4, null, dialogId3, replyMessage3, threadMessage3, photoEntry7.caption, photoEntry7.entities, photoEntry7.stickers, null, photoEntry7.ttl, messageObject2, videoEditedInfo, z3, i3, z5);
                    } else if (photoEntry3.path == null) {
                    } else {
                        AccountInstance accountInstance4 = PhotoViewer.this.parentChatActivity.getAccountInstance();
                        MediaController.PhotoEntry photoEntry8 = photoEntry;
                        String str5 = photoEntry8.path;
                        String str6 = photoEntry8.thumbPath;
                        long dialogId4 = PhotoViewer.this.parentChatActivity.getDialogId();
                        MessageObject replyMessage4 = PhotoViewer.this.parentChatActivity.getReplyMessage();
                        MessageObject threadMessage4 = PhotoViewer.this.parentChatActivity.getThreadMessage();
                        MediaController.PhotoEntry photoEntry9 = photoEntry;
                        SendMessagesHelper.prepareSendingPhoto(accountInstance4, str5, str6, null, dialogId4, replyMessage4, threadMessage4, photoEntry9.caption, photoEntry9.entities, photoEntry9.stickers, null, photoEntry9.ttl, messageObject2, videoEditedInfo, z3, i3, z5);
                    }
                }
            }
        };
        this.selectedPhotosAdapter.notifyDataSetChanged();
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        togglePhotosListView(false, false);
        toggleActionBar(true, false);
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity != null && chatActivity.getChatActivityEnterView() != null && this.parentChatActivity.isKeyboardVisible()) {
            this.parentChatActivity.getChatActivityEnterView().closeKeyboard();
        } else {
            makeFocusable();
        }
        this.backgroundDrawable.setAlpha(255);
        this.containerView.setAlpha(1.0f);
        onPhotoShow(null, null, null, null, null, null, Collections.singletonList(photoEntry), 0, null);
        float f = 154.0f;
        this.pickerView.setTranslationY(AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f));
        this.pickerViewSendButton.setTranslationY(AndroidUtilities.dp(this.isCurrentVideo ? 154.0f : 96.0f));
        ActionBar actionBar = this.actionBar;
        actionBar.setTranslationY(-actionBar.getHeight());
        CaptionTextViewSwitcher captionTextViewSwitcher = this.captionTextViewSwitcher;
        if (!this.isCurrentVideo) {
            f = 96.0f;
        }
        captionTextViewSwitcher.setTranslationY(AndroidUtilities.dp(f));
        createPaintView();
        switchToPaintMode();
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
        return this.animationInProgress != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCropBitmap() {
        VideoEditTextureView videoEditTextureView;
        if (this.cropInitied || this.sendPhotoType != 1) {
            return;
        }
        if (this.isCurrentVideo && ((videoEditTextureView = (VideoEditTextureView) this.videoTextureView) == null || videoEditTextureView.getVideoWidth() <= 0 || videoEditTextureView.getVideoHeight() <= 0)) {
            return;
        }
        this.cropInitied = true;
        Bitmap bitmap = this.centerImage.getBitmap();
        int orientation = this.centerImage.getOrientation();
        if (bitmap == null) {
            bitmap = this.animatingImageView.getBitmap();
            orientation = this.animatingImageView.getOrientation();
        }
        Bitmap bitmap2 = bitmap;
        int i = orientation;
        if (bitmap2 == null && this.videoTextureView == null) {
            return;
        }
        this.photoCropView.setBitmap(bitmap2, i, false, false, this.paintingOverlay, this.cropTransform, this.isCurrentVideo ? (VideoEditTextureView) this.videoTextureView : null, this.editState.cropState);
    }

    private void initCropView() {
        PhotoCropView photoCropView = this.photoCropView;
        if (photoCropView == null) {
            return;
        }
        photoCropView.setBitmap(null, 0, false, false, null, null, null, null);
        if (this.sendPhotoType != 1) {
            return;
        }
        this.photoCropView.onAppear();
        this.photoCropView.setVisibility(0);
        this.photoCropView.setAlpha(1.0f);
        this.photoCropView.onAppeared();
        this.padImageForHorizontalInsets = true;
    }

    public boolean openPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, ImageLocation imageLocation, ImageLocation imageLocation2, ArrayList<MessageObject> arrayList, ArrayList<SecureDocument> arrayList2, ArrayList<Object> arrayList3, int i, final PhotoViewerProvider photoViewerProvider, ChatActivity chatActivity, long j, long j2, int i2, boolean z, PageBlocksAdapter pageBlocksAdapter, final Integer num) {
        TLRPC$Message tLRPC$Message;
        boolean z2;
        PhotoViewerProvider photoViewerProvider2;
        float f;
        if (this.parentActivity != null && !this.isVisible && ((photoViewerProvider != null || !checkAnimation()) && (messageObject != null || tLRPC$FileLocation != null || arrayList != null || arrayList3 != null || arrayList2 != null || imageLocation != null || pageBlocksAdapter != null))) {
            PlaceProviderObject placeForPhoto = photoViewerProvider.getPlaceForPhoto(messageObject, tLRPC$FileLocation, i, true);
            this.lastInsets = null;
            WindowManager windowManager = (WindowManager) this.parentActivity.getSystemService("window");
            if (this.attachedToWindow) {
                try {
                    windowManager.removeView(this.windowView);
                    onHideView();
                } catch (Exception unused) {
                }
            }
            try {
                WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
                layoutParams.type = 99;
                if (Build.VERSION.SDK_INT >= 21) {
                    layoutParams.flags = -NUM;
                } else {
                    layoutParams.flags = 131072;
                }
                if (!(chatActivity == null || chatActivity.getCurrentEncryptedChat() == null) || ((this.avatarsDialogId != 0 && MessagesController.getInstance(this.currentAccount).isChatNoForwards(-this.avatarsDialogId)) || ((messageObject != null && (MessagesController.getInstance(this.currentAccount).isChatNoForwards(messageObject.getChatId()) || ((tLRPC$Message = messageObject.messageOwner) != null && tLRPC$Message.noforwards))) || (messageObject != null && messageObject.hasRevealedExtendedMedia())))) {
                    this.windowLayoutParams.flags |= 8192;
                } else {
                    this.windowLayoutParams.flags &= -8193;
                }
                this.windowLayoutParams.softInputMode = (this.useSmoothKeyboard ? 32 : 16) | 256;
                this.windowView.setFocusable(false);
                this.containerView.setFocusable(false);
                windowManager.addView(this.windowView, this.windowLayoutParams);
                onShowView();
                this.hasCaptionForAllMedia = false;
                this.doneButtonPressed = false;
                this.allowShowFullscreenButton = true;
                this.parentChatActivity = chatActivity;
                this.lastTitle = null;
                this.isEmbedVideo = num != null;
                this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, 1, 1));
                this.actionBar.setTitleScrollNonFitText(false);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadFailed);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaCountDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogPhotosLoaded);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingFailed);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileNewChunkAvailable);
                this.placeProvider = photoViewerProvider;
                this.mergeDialogId = j2;
                this.currentDialogId = j;
                this.topicId = i2;
                this.selectedPhotosAdapter.notifyDataSetChanged();
                this.pageBlocksAdapter = pageBlocksAdapter;
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.isVisible = true;
                togglePhotosListView(false, false);
                boolean z3 = !z;
                this.openedFullScreenVideo = z3;
                if (z3) {
                    toggleActionBar(false, false);
                } else if (this.sendPhotoType == 1) {
                    createCropView();
                    toggleActionBar(false, false);
                } else {
                    toggleActionBar(true, false);
                }
                this.windowView.setClipChildren(false);
                this.navigationBar.setVisibility(0);
                this.seekToProgressPending2 = 0.0f;
                this.skipFirstBufferingProgress = false;
                this.playerInjected = false;
                if (placeForPhoto != null) {
                    this.disableShowCheck = true;
                    this.animationInProgress = 1;
                    if (messageObject != null) {
                        AnimatedFileDrawable animation = placeForPhoto.allowTakeAnimation ? placeForPhoto.imageReceiver.getAnimation() : null;
                        this.currentAnimation = animation;
                        if (animation != null) {
                            if (messageObject.isVideo()) {
                                placeForPhoto.imageReceiver.setAllowStartAnimation(false);
                                placeForPhoto.imageReceiver.stopAnimation();
                                if (MediaController.getInstance().isPlayingMessage(messageObject)) {
                                    this.seekToProgressPending2 = messageObject.audioProgress;
                                }
                                this.skipFirstBufferingProgress = this.injectingVideoPlayer == null && !FileLoader.getInstance(messageObject.currentAccount).isLoadingVideo(messageObject.getDocument(), true) && (this.currentAnimation.hasBitmap() || !FileLoader.getInstance(messageObject.currentAccount).isLoadingVideo(messageObject.getDocument(), false));
                                this.currentAnimation = null;
                            } else if (messageObject.getWebPagePhotos(null, null).size() > 1) {
                                this.currentAnimation = null;
                            }
                        }
                    } else if (pageBlocksAdapter != null) {
                        this.currentAnimation = placeForPhoto.imageReceiver.getAnimation();
                    }
                    z2 = true;
                    onPhotoShow(messageObject, tLRPC$FileLocation, imageLocation, imageLocation2, arrayList, arrayList2, arrayList3, i, placeForPhoto);
                    if (this.sendPhotoType == 1) {
                        this.photoCropView.setVisibility(0);
                        this.photoCropView.setAlpha(0.0f);
                        this.photoCropView.setFreeform(false);
                    }
                    RectF drawRegion = placeForPhoto.imageReceiver.getDrawRegion();
                    float f2 = drawRegion.left;
                    float f3 = drawRegion.top;
                    int orientation = placeForPhoto.imageReceiver.getOrientation();
                    int animatedOrientation = placeForPhoto.imageReceiver.getAnimatedOrientation();
                    if (animatedOrientation != 0) {
                        orientation = animatedOrientation;
                    }
                    ClippingImageView[] animatingImageViews = getAnimatingImageViews(placeForPhoto);
                    this.clippingImageProgress = 1.0f;
                    for (int i3 = 0; i3 < animatingImageViews.length; i3++) {
                        animatingImageViews[i3].setAnimationValues(this.animationValues);
                        animatingImageViews[i3].setVisibility(0);
                        animatingImageViews[i3].setRadius(placeForPhoto.radius);
                        animatingImageViews[i3].setOrientation(orientation);
                        animatingImageViews[i3].setImageBitmap(placeForPhoto.thumb);
                    }
                    initCropView();
                    if (this.sendPhotoType == 1) {
                        this.photoCropView.setAspectRatio(1.0f);
                    }
                    ViewGroup.LayoutParams layoutParams2 = this.animatingImageView.getLayoutParams();
                    layoutParams2.width = (int) drawRegion.width();
                    int height = (int) drawRegion.height();
                    layoutParams2.height = height;
                    if (layoutParams2.width <= 0) {
                        layoutParams2.width = 100;
                    }
                    if (height <= 0) {
                        layoutParams2.height = 100;
                    }
                    for (int i4 = 0; i4 < animatingImageViews.length; i4++) {
                        if (animatingImageViews.length > 1) {
                            f = 0.0f;
                            animatingImageViews[i4].setAlpha(0.0f);
                        } else {
                            f = 0.0f;
                            animatingImageViews[i4].setAlpha(1.0f);
                        }
                        animatingImageViews[i4].setPivotX(f);
                        animatingImageViews[i4].setPivotY(f);
                        animatingImageViews[i4].setScaleX(placeForPhoto.scale);
                        animatingImageViews[i4].setScaleY(placeForPhoto.scale);
                        animatingImageViews[i4].setTranslationX(placeForPhoto.viewX + (drawRegion.left * placeForPhoto.scale));
                        animatingImageViews[i4].setTranslationY(placeForPhoto.viewY + (drawRegion.top * placeForPhoto.scale));
                        animatingImageViews[i4].setLayoutParams(layoutParams2);
                    }
                    this.windowView.getViewTreeObserver().addOnPreDrawListener(new AnonymousClass70(animatingImageViews, layoutParams2, f2, placeForPhoto, f3, pageBlocksAdapter, arrayList3, num, photoViewerProvider));
                } else {
                    z2 = true;
                    if (arrayList3 != null && this.sendPhotoType != 3 && ((photoViewerProvider2 = this.placeProvider) == null || !photoViewerProvider2.closeKeyboard())) {
                        makeFocusable();
                    }
                    this.containerView.setAlpha(1.0f);
                    onPhotoShow(messageObject, tLRPC$FileLocation, imageLocation, imageLocation2, arrayList, arrayList2, arrayList3, i, placeForPhoto);
                    initCropView();
                    setCropBitmap();
                    ChatActivity chatActivity2 = this.parentChatActivity;
                    if (chatActivity2 != null) {
                        chatActivity2.getUndoView().hide(false, 1);
                        this.parentChatActivity.getFragmentView().invalidate();
                    }
                    this.windowView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.PhotoViewer.71
                        @Override // android.view.ViewTreeObserver.OnPreDrawListener
                        public boolean onPreDraw() {
                            PhotoViewer.this.windowView.getViewTreeObserver().removeOnPreDrawListener(this);
                            PhotoViewer.this.actionBar.setTranslationY(-AndroidUtilities.dp(32.0f));
                            ViewPropertyAnimator duration = PhotoViewer.this.actionBar.animate().alpha(1.0f).translationY(0.0f).setDuration(150L);
                            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
                            duration.setInterpolator(cubicBezierInterpolator).start();
                            PhotoViewer.this.checkImageView.setTranslationY(-AndroidUtilities.dp(32.0f));
                            PhotoViewer.this.checkImageView.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
                            PhotoViewer.this.photosCounterView.setTranslationY(-AndroidUtilities.dp(32.0f));
                            PhotoViewer.this.photosCounterView.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
                            PhotoViewer.this.pickerView.setTranslationY(AndroidUtilities.dp(32.0f));
                            PhotoViewer.this.pickerView.animate().alpha(1.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
                            PhotoViewer.this.pickerViewSendButton.setTranslationY(AndroidUtilities.dp(32.0f));
                            PhotoViewer.this.pickerViewSendButton.setAlpha(0.0f);
                            PhotoViewer.this.pickerViewSendButton.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
                            PhotoViewer.this.cameraItem.setTranslationY(AndroidUtilities.dp(32.0f));
                            PhotoViewer.this.cameraItem.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
                            PhotoViewer.this.videoPreviewFrame.setTranslationY(AndroidUtilities.dp(32.0f));
                            PhotoViewer.this.videoPreviewFrame.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
                            PhotoViewer.this.containerView.setAlpha(0.0f);
                            PhotoViewer.this.backgroundDrawable.setAlpha(0);
                            PhotoViewer.this.animationInProgress = 4;
                            PhotoViewer.this.containerView.invalidate();
                            AnimatorSet animatorSet = new AnimatorSet();
                            ObjectAnimator duration2 = ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, View.TRANSLATION_Y, PhotoViewer.this.pickerView.getTranslationY(), 0.0f).setDuration(220L);
                            duration2.setInterpolator(cubicBezierInterpolator);
                            animatorSet.playTogether(ObjectAnimator.ofFloat(PhotoViewer.this.containerView, View.ALPHA, 0.0f, 1.0f).setDuration(220L), ObjectAnimator.ofFloat(PhotoViewer.this.navigationBar, View.ALPHA, 0.0f, 1.0f).setDuration(220L), duration2);
                            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.71.1
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animator) {
                                    super.onAnimationEnd(animator);
                                    PhotoViewer.this.animationInProgress = 0;
                                    PhotoViewer.this.backgroundDrawable.setAlpha(255);
                                    PhotoViewer.this.containerView.invalidate();
                                    PhotoViewer.this.pickerView.setTranslationY(0.0f);
                                    if (PhotoViewer.this.isEmbedVideo) {
                                        AnonymousClass71 anonymousClass71 = AnonymousClass71.this;
                                        PhotoViewer.this.initEmbedVideo(num.intValue());
                                    }
                                    PhotoViewerProvider photoViewerProvider3 = photoViewerProvider;
                                    if (photoViewerProvider3 != null) {
                                        photoViewerProvider3.onOpen();
                                    }
                                }
                            });
                            animatorSet.start();
                            return true;
                        }
                    });
                }
                AccessibilityManager accessibilityManager = (AccessibilityManager) this.parentActivity.getSystemService("accessibility");
                if (accessibilityManager.isTouchExplorationEnabled()) {
                    AccessibilityEvent obtain = AccessibilityEvent.obtain();
                    obtain.setEventType(16384);
                    obtain.getText().add(LocaleController.getString("AccDescrPhotoViewer", R.string.AccDescrPhotoViewer));
                    accessibilityManager.sendAccessibilityEvent(obtain);
                }
                return z2;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$70  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass70 implements ViewTreeObserver.OnPreDrawListener {
        final /* synthetic */ ClippingImageView[] val$animatingImageViews;
        final /* synthetic */ Integer val$embedSeekTime;
        final /* synthetic */ ViewGroup.LayoutParams val$layoutParams;
        final /* synthetic */ float val$left;
        final /* synthetic */ PlaceProviderObject val$object;
        final /* synthetic */ PageBlocksAdapter val$pageBlocksAdapter;
        final /* synthetic */ ArrayList val$photos;
        final /* synthetic */ PhotoViewerProvider val$provider;
        final /* synthetic */ float val$top;

        AnonymousClass70(ClippingImageView[] clippingImageViewArr, ViewGroup.LayoutParams layoutParams, float f, PlaceProviderObject placeProviderObject, float f2, PageBlocksAdapter pageBlocksAdapter, ArrayList arrayList, Integer num, PhotoViewerProvider photoViewerProvider) {
            this.val$animatingImageViews = clippingImageViewArr;
            this.val$layoutParams = layoutParams;
            this.val$left = f;
            this.val$object = placeProviderObject;
            this.val$top = f2;
            this.val$pageBlocksAdapter = pageBlocksAdapter;
            this.val$photos = arrayList;
            this.val$embedSeekTime = num;
            this.val$provider = photoViewerProvider;
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            float min;
            float f;
            float measuredWidth;
            PlaceProviderObject placeProviderObject;
            ClippingImageView[] clippingImageViewArr;
            int i;
            ClippingImageView[] clippingImageViewArr2 = this.val$animatingImageViews;
            if (clippingImageViewArr2.length > 1) {
                clippingImageViewArr2[1].setAlpha(1.0f);
                this.val$animatingImageViews[1].setAdditionalTranslationX(-PhotoViewer.this.getLeftInset());
            }
            ClippingImageView[] clippingImageViewArr3 = this.val$animatingImageViews;
            clippingImageViewArr3[0].setTranslationX(clippingImageViewArr3[0].getTranslationX() + PhotoViewer.this.getLeftInset());
            PhotoViewer.this.windowView.getViewTreeObserver().removeOnPreDrawListener(this);
            if (PhotoViewer.this.sendPhotoType == 1) {
                float f2 = PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0;
                float measuredHeight = (PhotoViewer.this.photoCropView.getMeasuredHeight() - AndroidUtilities.dp(64.0f)) - f2;
                float measuredWidth2 = PhotoViewer.this.photoCropView.getMeasuredWidth() / 2.0f;
                float f3 = f2 + (measuredHeight / 2.0f);
                float min2 = (Math.min(PhotoViewer.this.photoCropView.getMeasuredWidth(), measuredHeight) - (AndroidUtilities.dp(16.0f) * 2)) / 2.0f;
                float f4 = f3 - min2;
                ViewGroup.LayoutParams layoutParams = this.val$layoutParams;
                float f5 = (f3 + min2) - f4;
                min = Math.max(((measuredWidth2 + min2) - (measuredWidth2 - min2)) / layoutParams.width, f5 / layoutParams.height);
                f = f4 + ((f5 - (this.val$layoutParams.height * min)) / 2.0f);
                measuredWidth = ((((PhotoViewer.this.windowView.getMeasuredWidth() - PhotoViewer.this.getLeftInset()) - PhotoViewer.this.getRightInset()) - (this.val$layoutParams.width * min)) / 2.0f) + PhotoViewer.this.getLeftInset();
            } else {
                min = Math.min(PhotoViewer.this.windowView.getMeasuredWidth() / this.val$layoutParams.width, (AndroidUtilities.displaySize.y + (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) / this.val$layoutParams.height);
                f = ((AndroidUtilities.displaySize.y + (PhotoViewer.this.isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) - (this.val$layoutParams.height * min)) / 2.0f;
                measuredWidth = (PhotoViewer.this.windowView.getMeasuredWidth() - (this.val$layoutParams.width * min)) / 2.0f;
            }
            int abs = (int) Math.abs(this.val$left - this.val$object.imageReceiver.getImageX());
            int abs2 = (int) Math.abs(this.val$top - this.val$object.imageReceiver.getImageY());
            if (this.val$pageBlocksAdapter != null && this.val$object.imageReceiver.isAspectFit()) {
                abs = 0;
            }
            int[] iArr = new int[2];
            this.val$object.parentView.getLocationInWindow(iArr);
            int i2 = iArr[1];
            int i3 = Build.VERSION.SDK_INT;
            int i4 = (i3 >= 21 || PhotoViewer.this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
            int i5 = this.val$object.viewY;
            float f6 = this.val$top;
            int i6 = (int) (((i2 - i4) - (i5 + f6)) + placeProviderObject.clipTopAddition);
            if (i6 < 0) {
                i6 = 0;
            }
            int height = (int) ((((i5 + f6) + this.val$layoutParams.height) - ((iArr[1] + placeProviderObject.parentView.getHeight()) - ((i3 >= 21 || PhotoViewer.this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight))) + this.val$object.clipBottomAddition);
            if (height < 0) {
                height = 0;
            }
            int max = Math.max(i6, abs2);
            int max2 = Math.max(height, abs2);
            PhotoViewer.this.animationValues[0][0] = PhotoViewer.this.animatingImageView.getScaleX();
            PhotoViewer.this.animationValues[0][1] = PhotoViewer.this.animatingImageView.getScaleY();
            PhotoViewer.this.animationValues[0][2] = PhotoViewer.this.animatingImageView.getTranslationX();
            int i7 = 3;
            PhotoViewer.this.animationValues[0][3] = PhotoViewer.this.animatingImageView.getTranslationY();
            float f7 = abs;
            PhotoViewer.this.animationValues[0][4] = this.val$object.scale * f7;
            PhotoViewer.this.animationValues[0][5] = max * this.val$object.scale;
            PhotoViewer.this.animationValues[0][6] = max2 * this.val$object.scale;
            int[] radius = PhotoViewer.this.animatingImageView.getRadius();
            int i8 = 0;
            while (true) {
                float f8 = 0.0f;
                if (i8 >= 4) {
                    break;
                }
                float[] fArr = PhotoViewer.this.animationValues[0];
                int i9 = i8 + 7;
                if (radius != null) {
                    f8 = radius[i8];
                }
                fArr[i9] = f8;
                i8++;
            }
            PhotoViewer.this.animationValues[0][11] = abs2 * this.val$object.scale;
            PhotoViewer.this.animationValues[0][12] = f7 * this.val$object.scale;
            PhotoViewer.this.animationValues[1][0] = min;
            PhotoViewer.this.animationValues[1][1] = min;
            PhotoViewer.this.animationValues[1][2] = measuredWidth;
            PhotoViewer.this.animationValues[1][3] = f;
            PhotoViewer.this.animationValues[1][4] = 0.0f;
            PhotoViewer.this.animationValues[1][5] = 0.0f;
            PhotoViewer.this.animationValues[1][6] = 0.0f;
            PhotoViewer.this.animationValues[1][7] = 0.0f;
            PhotoViewer.this.animationValues[1][8] = 0.0f;
            PhotoViewer.this.animationValues[1][9] = 0.0f;
            PhotoViewer.this.animationValues[1][10] = 0.0f;
            PhotoViewer.this.animationValues[1][11] = 0.0f;
            PhotoViewer.this.animationValues[1][12] = 0.0f;
            int i10 = 0;
            while (true) {
                ClippingImageView[] clippingImageViewArr4 = this.val$animatingImageViews;
                if (i10 >= clippingImageViewArr4.length) {
                    break;
                }
                clippingImageViewArr4[i10].setAnimationProgress(0.0f);
                i10++;
            }
            PhotoViewer.this.backgroundDrawable.setAlpha(0);
            PhotoViewer.this.containerView.setAlpha(0.0f);
            PhotoViewer.this.navigationBar.setAlpha(0.0f);
            PhotoViewer photoViewer = PhotoViewer.this;
            final ClippingImageView[] clippingImageViewArr5 = this.val$animatingImageViews;
            final ArrayList arrayList = this.val$photos;
            final Integer num = this.val$embedSeekTime;
            final PhotoViewerProvider photoViewerProvider = this.val$provider;
            photoViewer.animationEndRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$70$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass70.this.lambda$onPreDraw$0(clippingImageViewArr5, arrayList, num, photoViewerProvider);
                }
            };
            if (PhotoViewer.this.openedFullScreenVideo) {
                if (PhotoViewer.this.animationEndRunnable != null) {
                    PhotoViewer.this.animationEndRunnable.run();
                    PhotoViewer.this.animationEndRunnable = null;
                }
                PhotoViewer.this.containerView.setAlpha(1.0f);
                PhotoViewer.this.backgroundDrawable.setAlpha(255);
                int i11 = 0;
                while (true) {
                    ClippingImageView[] clippingImageViewArr6 = this.val$animatingImageViews;
                    if (i11 >= clippingImageViewArr6.length) {
                        break;
                    }
                    clippingImageViewArr6[i11].setAnimationProgress(1.0f);
                    i11++;
                }
                if (PhotoViewer.this.sendPhotoType == 1) {
                    PhotoViewer.this.photoCropView.setAlpha(1.0f);
                }
            } else {
                final AnimatorSet animatorSet = new AnimatorSet();
                if (PhotoViewer.this.sendPhotoType != 1) {
                    i7 = 2;
                }
                ClippingImageView[] clippingImageViewArr7 = this.val$animatingImageViews;
                ArrayList arrayList2 = new ArrayList(i7 + clippingImageViewArr7.length + (clippingImageViewArr7.length > 1 ? 1 : 0));
                int i12 = 0;
                while (true) {
                    clippingImageViewArr = this.val$animatingImageViews;
                    if (i12 >= clippingImageViewArr.length) {
                        break;
                    }
                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(clippingImageViewArr[i12], AnimationProperties.CLIPPING_IMAGE_VIEW_PROGRESS, 0.0f, 1.0f);
                    if (i12 == 0) {
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$70$$ExternalSyntheticLambda0
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                PhotoViewer.AnonymousClass70.this.lambda$onPreDraw$1(valueAnimator);
                            }
                        });
                    }
                    arrayList2.add(ofFloat);
                    i12++;
                }
                if (clippingImageViewArr.length > 1) {
                    i = 2;
                    arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.animatingImageView, View.ALPHA, 0.0f, 1.0f));
                } else {
                    i = 2;
                }
                int[] iArr2 = new int[i];
                // fill-array-data instruction
                iArr2[0] = 0;
                iArr2[1] = 255;
                arrayList2.add(ObjectAnimator.ofInt(PhotoViewer.this.backgroundDrawable, (Property<BackgroundDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, iArr2));
                float[] fArr2 = new float[i];
                // fill-array-data instruction
                fArr2[0] = 0.0f;
                fArr2[1] = 1.0f;
                arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.containerView, View.ALPHA, fArr2));
                float[] fArr3 = new float[i];
                // fill-array-data instruction
                fArr3[0] = 0.0f;
                fArr3[1] = 1.0f;
                arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.navigationBar, View.ALPHA, fArr3));
                if (PhotoViewer.this.sendPhotoType == 1) {
                    float[] fArr4 = new float[i];
                    // fill-array-data instruction
                    fArr4[0] = 0.0f;
                    fArr4[1] = 1.0f;
                    arrayList2.add(ObjectAnimator.ofFloat(PhotoViewer.this.photoCropView, View.ALPHA, fArr4));
                }
                animatorSet.playTogether(arrayList2);
                animatorSet.setDuration(200L);
                final int i13 = PhotoViewer.this.currentAccount;
                animatorSet.addListener(new AnonymousClass1(i13));
                if (Build.VERSION.SDK_INT >= 18) {
                    PhotoViewer.this.containerView.setLayerType(2, null);
                }
                PhotoViewer.this.setCaptionHwLayerEnabled(false);
                PhotoViewer.this.transitionAnimationStartTime = System.currentTimeMillis();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$70$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass70.this.lambda$onPreDraw$2(i13, animatorSet);
                    }
                });
            }
            BackgroundDrawable backgroundDrawable = PhotoViewer.this.backgroundDrawable;
            final PlaceProviderObject placeProviderObject2 = this.val$object;
            backgroundDrawable.drawRunnable = new Runnable() { // from class: org.telegram.ui.PhotoViewer$70$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass70.this.lambda$onPreDraw$3(placeProviderObject2);
                }
            };
            if (PhotoViewer.this.parentChatActivity != null && PhotoViewer.this.parentChatActivity.getFragmentView() != null) {
                PhotoViewer.this.parentChatActivity.getUndoView().hide(false, 1);
                PhotoViewer.this.parentChatActivity.getFragmentView().invalidate();
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPreDraw$0(ClippingImageView[] clippingImageViewArr, ArrayList arrayList, Integer num, PhotoViewerProvider photoViewerProvider) {
            PhotoViewer.this.animationEndRunnable = null;
            if (PhotoViewer.this.containerView == null || PhotoViewer.this.windowView == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 18) {
                PhotoViewer.this.containerView.setLayerType(0, null);
            }
            PhotoViewer.this.animationInProgress = 0;
            PhotoViewer.this.transitionAnimationStartTime = 0L;
            PhotoViewer.this.leftCropState = null;
            PhotoViewer.this.leftCropTransform.setViewTransform(false);
            PhotoViewer.this.rightCropState = null;
            PhotoViewer.this.rightCropTransform.setViewTransform(false);
            PhotoViewer.this.setImages();
            PhotoViewer.this.setCropBitmap();
            PhotoViewer.this.containerView.invalidate();
            for (ClippingImageView clippingImageView : clippingImageViewArr) {
                clippingImageView.setVisibility(8);
            }
            if (PhotoViewer.this.showAfterAnimation != null) {
                PhotoViewer.this.showAfterAnimation.imageReceiver.setVisible(true, true);
            }
            if (PhotoViewer.this.hideAfterAnimation != null) {
                PhotoViewer.this.hideAfterAnimation.imageReceiver.setVisible(false, true);
            }
            if (arrayList != null && PhotoViewer.this.sendPhotoType != 3 && (PhotoViewer.this.placeProvider == null || !PhotoViewer.this.placeProvider.closeKeyboard())) {
                PhotoViewer.this.makeFocusable();
            }
            if (PhotoViewer.this.videoPlayer != null && PhotoViewer.this.videoPlayer.isPlaying() && PhotoViewer.this.isCurrentVideo && !PhotoViewer.this.imagesArrLocals.isEmpty()) {
                PhotoViewer photoViewer = PhotoViewer.this;
                photoViewer.seekAnimatedStickersTo(photoViewer.videoPlayer.getCurrentPosition());
                PhotoViewer.this.playOrStopAnimatedStickers(true);
            }
            if (PhotoViewer.this.isEmbedVideo) {
                PhotoViewer.this.initEmbedVideo(num.intValue());
            }
            if (photoViewerProvider == null) {
                return;
            }
            photoViewerProvider.onOpen();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPreDraw$1(ValueAnimator valueAnimator) {
            PhotoViewer.this.clippingImageProgress = 1.0f - ((Float) valueAnimator.getAnimatedValue()).floatValue();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.PhotoViewer$70$1  reason: invalid class name */
        /* loaded from: classes3.dex */
        public class AnonymousClass1 extends AnimatorListenerAdapter {
            final /* synthetic */ int val$account;

            AnonymousClass1(int i) {
                this.val$account = i;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                final int i = this.val$account;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$70$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.AnonymousClass70.AnonymousClass1.this.lambda$onAnimationEnd$0(i);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onAnimationEnd$0(int i) {
                NotificationCenter.getInstance(i).onAnimationFinish(PhotoViewer.this.transitionIndex);
                if (PhotoViewer.this.animationEndRunnable != null) {
                    PhotoViewer.this.animationEndRunnable.run();
                    PhotoViewer.this.animationEndRunnable = null;
                }
                PhotoViewer.this.setCaptionHwLayerEnabled(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPreDraw$2(int i, AnimatorSet animatorSet) {
            PhotoViewer.this.transitionIndex = NotificationCenter.getInstance(i).setAnimationInProgress(PhotoViewer.this.transitionIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaDidLoad, NotificationCenter.dialogPhotosLoaded});
            animatorSet.start();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onPreDraw$3(PlaceProviderObject placeProviderObject) {
            PhotoViewer.this.disableShowCheck = false;
            placeProviderObject.imageReceiver.setVisible(false, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initEmbedVideo(int i) {
        if (!this.isEmbedVideo) {
            return;
        }
        PhotoViewerWebView photoViewerWebView = new PhotoViewerWebView(this, this.parentActivity, this.pipItem) { // from class: org.telegram.ui.PhotoViewer.72
            Rect rect = new Rect();

            @Override // org.telegram.ui.Components.PhotoViewerWebView
            protected void drawBlackBackground(Canvas canvas, int i2, int i3) {
                Bitmap bitmap = PhotoViewer.this.centerImage.getBitmap();
                if (bitmap != null) {
                    float min = Math.min(i2 / bitmap.getWidth(), i3 / bitmap.getHeight());
                    int width = (int) (bitmap.getWidth() * min);
                    int height = (int) (bitmap.getHeight() * min);
                    int i4 = (i3 - height) / 2;
                    int i5 = (i2 - width) / 2;
                    this.rect.set(i5, i4, width + i5, height + i4);
                    canvas.drawBitmap(bitmap, (Rect) null, this.rect, (Paint) null);
                }
            }

            @Override // org.telegram.ui.Components.PhotoViewerWebView
            protected void processTouch(MotionEvent motionEvent) {
                PhotoViewer.this.gestureDetector.onTouchEvent(motionEvent);
            }
        };
        this.photoViewerWebView = photoViewerWebView;
        photoViewerWebView.init(i, MessageObject.getMedia(this.currentMessageObject.messageOwner).webpage);
        this.photoViewerWebView.setPlaybackSpeed(this.currentVideoSpeed);
        this.containerView.addView(this.photoViewerWebView, 0, LayoutHelper.createFrame(-1, -1.0f));
        if (this.photoViewerWebView.isControllable()) {
            setVideoPlayerControlVisible(true, true);
        }
        this.videoPlayerSeekbar.clearTimestamps();
        updateVideoPlayerTime();
        this.shouldSavePositionForCurrentVideo = null;
        this.shouldSavePositionForCurrentVideoShortTerm = null;
        this.lastSaveTime = 0L;
        this.seekToProgressPending = this.seekToProgressPending2;
        this.videoPlayerSeekbar.setProgress(0.0f);
        this.videoTimelineView.setProgress(0.0f);
        this.videoPlayerSeekbar.setBufferedProgress(0.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void makeFocusable() {
        if (Build.VERSION.SDK_INT >= 21) {
            this.windowLayoutParams.flags = -NUM;
        } else {
            this.windowLayoutParams.flags = 0;
        }
        this.windowLayoutParams.softInputMode = (this.useSmoothKeyboard ? 32 : 16) | 256;
        try {
            ((WindowManager) this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.windowView.setFocusable(true);
        this.containerView.setFocusable(true);
    }

    private void requestAdjustToNothing() {
        this.windowLayoutParams.softInputMode = 48;
        try {
            ((WindowManager) this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestAdjust() {
        this.windowLayoutParams.softInputMode = (this.useSmoothKeyboard ? 32 : 16) | 256;
        try {
            ((WindowManager) this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
        } catch (Exception e) {
            FileLog.e(e);
        }
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

    /* JADX WARN: Removed duplicated region for block: B:113:0x01e0  */
    /* JADX WARN: Removed duplicated region for block: B:116:0x01e7  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x0216  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x021f  */
    /* JADX WARN: Removed duplicated region for block: B:128:0x0238  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void closePhoto(boolean r28, boolean r29) {
        /*
            Method dump skipped, instructions count: 1894
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.closePhoto(boolean, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$closePhoto$74(ValueAnimator valueAnimator) {
        this.clippingImageProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$closePhoto$75(ValueAnimator valueAnimator) {
        this.clippingImageProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$closePhoto$76(PlaceProviderObject placeProviderObject) {
        this.animationEndRunnable = null;
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(0, null);
        }
        this.animationInProgress = 0;
        onPhotoClosed(placeProviderObject);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$73  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass73 extends AnimatorListenerAdapter {
        AnonymousClass73() {
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$73$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass73.this.lambda$onAnimationEnd$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAnimationEnd$0() {
            if (PhotoViewer.this.animationEndRunnable != null) {
                PhotoViewer.this.animationEndRunnable.run();
                PhotoViewer.this.animationEndRunnable = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$closePhoto$77(ValueAnimator valueAnimator) {
        this.clippingImageProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$closePhoto$78(PlaceProviderObject placeProviderObject) {
        this.animationEndRunnable = null;
        FrameLayoutDrawer frameLayoutDrawer = this.containerView;
        if (frameLayoutDrawer == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 18) {
            frameLayoutDrawer.setLayerType(0, null);
        }
        this.animationInProgress = 0;
        onPhotoClosed(placeProviderObject);
        this.containerView.setScaleX(1.0f);
        this.containerView.setScaleY(1.0f);
    }

    private ClippingImageView[] getAnimatingImageViews(PlaceProviderObject placeProviderObject) {
        int i = (AndroidUtilities.isTablet() || placeProviderObject == null || placeProviderObject.animatingImageView == null) ? 0 : 1;
        ClippingImageView[] clippingImageViewArr = new ClippingImageView[i + 1];
        clippingImageViewArr[0] = this.animatingImageView;
        if (i != 0) {
            ClippingImageView clippingImageView = placeProviderObject.animatingImageView;
            clippingImageViewArr[1] = clippingImageView;
            clippingImageView.setAdditionalTranslationY(placeProviderObject.animatingImageViewYOffset);
        }
        return clippingImageViewArr;
    }

    private void removeObservers() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
        ConnectionsManager.getInstance(this.currentAccount).cancelRequestsForGuid(this.classGuid);
    }

    public void destroyPhotoViewer() {
        if (this.parentActivity == null || this.windowView == null) {
            return;
        }
        if (PipVideoOverlay.isVisible()) {
            PipVideoOverlay.dismiss();
        }
        removeObservers();
        releasePlayer(false);
        try {
            if (this.windowView.getParent() != null) {
                ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                onHideView();
            }
            this.windowView = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
        ImageReceiver.BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
            this.currentThumb = null;
        }
        this.animatingImageView.setImageBitmap(null);
        PhotoViewerCaptionEnterView photoViewerCaptionEnterView = this.captionEditText;
        if (photoViewerCaptionEnterView != null) {
            photoViewerCaptionEnterView.onDestroy();
        }
        if (this == PipInstance) {
            PipInstance = null;
        } else {
            Instance = null;
        }
        onHideView();
    }

    private void onPhotoClosed(final PlaceProviderObject placeProviderObject) {
        if (this.doneButtonPressed) {
            releasePlayer(true);
        }
        this.isVisible = false;
        this.cropInitied = false;
        this.disableShowCheck = true;
        this.currentMessageObject = null;
        this.currentBotInlineResult = null;
        this.currentFileLocation = null;
        this.currentFileLocationVideo = null;
        this.currentSecureDocument = null;
        this.currentPageBlock = null;
        this.currentPathObject = null;
        if (this.videoPlayerControlFrameLayout != null) {
            setVideoPlayerControlVisible(false, false);
        }
        CaptionScrollView captionScrollView = this.captionScrollView;
        if (captionScrollView != null) {
            captionScrollView.reset();
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
            animatedFileDrawable.removeSecondParentView(this.containerView);
            this.currentAnimation = null;
        }
        for (int i = 0; i < 3; i++) {
            PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
            if (photoProgressViewArr[i] != null) {
                photoProgressViewArr[i].setBackgroundState(-1, false, true);
            }
        }
        requestVideoPreview(0);
        VideoTimelinePlayView videoTimelinePlayView = this.videoTimelineView;
        if (videoTimelinePlayView != null) {
            videoTimelinePlayView.setBackgroundColor(NUM);
            this.videoTimelineView.destroy();
        }
        this.hintView.hide(false, 0);
        this.centerImage.setImageBitmap((Bitmap) null);
        this.leftImage.setImageBitmap((Bitmap) null);
        this.rightImage.setImageBitmap((Bitmap) null);
        this.containerView.post(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda69
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.lambda$onPhotoClosed$79(placeProviderObject);
            }
        });
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        if (photoViewerProvider != null) {
            photoViewerProvider.willHidePhotoViewer();
        }
        this.groupedPhotosListView.clear();
        PhotoViewerProvider photoViewerProvider2 = this.placeProvider;
        if (photoViewerProvider2 != null) {
            photoViewerProvider2.onClose();
        }
        this.placeProvider = null;
        this.selectedPhotosAdapter.notifyDataSetChanged();
        this.pageBlocksAdapter = null;
        this.disableShowCheck = false;
        this.shownControlsByEnd = false;
        this.videoCutStart = 0.0f;
        this.videoCutEnd = 1.0f;
        if (placeProviderObject != null) {
            placeProviderObject.imageReceiver.setVisible(true, true);
        }
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity != null) {
            chatActivity.getFragmentView().invalidate();
        }
        Bitmap bitmap = this.videoFrameBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.videoFrameBitmap = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPhotoClosed$79(PlaceProviderObject placeProviderObject) {
        ClippingImageView clippingImageView;
        this.animatingImageView.setImageBitmap(null);
        if (placeProviderObject != null && !AndroidUtilities.isTablet() && (clippingImageView = placeProviderObject.animatingImageView) != null) {
            clippingImageView.setImageBitmap(null);
        }
        try {
            if (this.windowView.getParent() == null) {
                return;
            }
            ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            onHideView();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void redraw(final int i) {
        FrameLayoutDrawer frameLayoutDrawer;
        if (i >= 6 || (frameLayoutDrawer = this.containerView) == null) {
            return;
        }
        frameLayoutDrawer.invalidate();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda62
            @Override // java.lang.Runnable
            public final void run() {
                PhotoViewer.this.lambda$redraw$80(i);
            }
        }, 100L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$redraw$80(int i) {
        redraw(i + 1);
    }

    public void onResume() {
        redraw(0);
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.seekTo(videoPlayer.getCurrentPosition() + 1);
            if (this.playerLooping) {
                this.videoPlayer.setLooping(true);
            }
        }
        PhotoPaintView photoPaintView = this.photoPaintView;
        if (photoPaintView != null) {
            photoPaintView.onResume();
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
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null || !this.playerLooping) {
            return;
        }
        videoPlayer.setLooping(false);
    }

    public boolean isVisible() {
        return this.isVisible && this.placeProvider != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMinMax(float f) {
        AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null && aspectRatioFrameLayout.getVisibility() == 0 && this.textureUploaded) {
            f *= Math.min(getContainerViewWidth() / this.videoTextureView.getMeasuredWidth(), getContainerViewHeight() / this.videoTextureView.getMeasuredHeight());
        }
        float imageWidth = this.centerImage.getImageWidth();
        float imageHeight = this.centerImage.getImageHeight();
        MediaController.CropState cropState = this.editState.cropState;
        if (cropState != null) {
            imageWidth *= cropState.cropPw;
            imageHeight *= cropState.cropPh;
        }
        int containerViewWidth = ((int) ((imageWidth * f) - getContainerViewWidth())) / 2;
        int containerViewHeight = ((int) ((imageHeight * f) - getContainerViewHeight())) / 2;
        if (containerViewWidth > 0) {
            this.minX = -containerViewWidth;
            this.maxX = containerViewWidth;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (containerViewHeight > 0) {
            this.minY = -containerViewHeight;
            this.maxY = containerViewHeight;
            return;
        }
        this.maxY = 0.0f;
        this.minY = 0.0f;
    }

    private int getAdditionX() {
        int i = this.currentEditMode;
        if (i == 1 || (i == 0 && this.sendPhotoType == 1)) {
            return AndroidUtilities.dp(16.0f);
        }
        if (i != 0 && i != 3) {
            return AndroidUtilities.dp(14.0f);
        }
        return 0;
    }

    private int getAdditionY() {
        int i = this.currentEditMode;
        int i2 = 0;
        if (i == 1 || (i == 0 && this.sendPhotoType == 1)) {
            int dp = AndroidUtilities.dp(16.0f);
            if (isStatusBarVisible()) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            return dp + i2;
        } else if (i == 3) {
            int dp2 = AndroidUtilities.dp(8.0f);
            if (isStatusBarVisible()) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            return dp2 + i2;
        } else if (i == 0) {
            return 0;
        } else {
            int dp3 = AndroidUtilities.dp(14.0f);
            if (isStatusBarVisible()) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            return dp3 + i2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getContainerViewWidth() {
        return getContainerViewWidth(this.currentEditMode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getContainerViewWidth(int i) {
        int dp;
        int width = this.containerView.getWidth();
        if (i == 1 || (i == 0 && this.sendPhotoType == 1)) {
            dp = AndroidUtilities.dp(32.0f);
        } else if (i == 0 || i == 3) {
            return width;
        } else {
            dp = AndroidUtilities.dp(28.0f);
        }
        return width - dp;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getContainerViewHeight() {
        return getContainerViewHeight(this.currentEditMode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getContainerViewHeight(int i) {
        return getContainerViewHeight(false, i);
    }

    private int getContainerViewHeight(boolean z, int i) {
        int measuredHeight;
        int dp;
        if (z || this.inBubbleMode) {
            measuredHeight = this.containerView.getMeasuredHeight();
        } else {
            measuredHeight = AndroidUtilities.displaySize.y;
            if (i == 0 && this.sendPhotoType != 1 && isStatusBarVisible()) {
                measuredHeight += AndroidUtilities.statusBarHeight;
            }
        }
        if ((i == 0 && this.sendPhotoType == 1) || i == 1) {
            dp = AndroidUtilities.dp(144.0f);
        } else if (i == 2) {
            dp = AndroidUtilities.dp(214.0f);
        } else if (i != 3) {
            return measuredHeight;
        } else {
            dp = AndroidUtilities.dp(48.0f) + ActionBar.getCurrentActionBarHeight();
        }
        return measuredHeight - dp;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:217:0x0428, code lost:
        if (r1 > r4) goto L211;
     */
    /* JADX WARN: Code restructure failed: missing block: B:223:0x0437, code lost:
        if (r2 > r4) goto L214;
     */
    /* JADX WARN: Code restructure failed: missing block: B:232:0x045c, code lost:
        if (r3 > r4) goto L224;
     */
    /* JADX WARN: Code restructure failed: missing block: B:238:0x046d, code lost:
        if (r3 > r4) goto L227;
     */
    /* JADX WARN: Code restructure failed: missing block: B:288:0x054c, code lost:
        if (r3 > r4) goto L277;
     */
    /* JADX WARN: Code restructure failed: missing block: B:294:0x055d, code lost:
        if (r3 > r4) goto L280;
     */
    /* JADX WARN: Removed duplicated region for block: B:188:0x0364  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r20) {
        /*
            Method dump skipped, instructions count: 1670
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0028, code lost:
        if (r2 > r3) goto L6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0017, code lost:
        if (r2 > r3) goto L3;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
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
            if (r4 >= 0) goto L13
        L11:
            r0 = r3
            goto L1a
        L13:
            float r3 = r5.maxX
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L1a
            goto L11
        L1a:
            float r2 = r5.translationY
            float r3 = r5.minY
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 >= 0) goto L24
        L22:
            r1 = r3
            goto L2b
        L24:
            float r3 = r5.maxY
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L2b
            goto L22
        L2b:
            float r2 = r5.scale
            r5.animateTo(r2, r0, r1, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.checkMinMax(boolean):void");
    }

    private void goToNext() {
        float containerViewWidth = this.scale != 1.0f ? ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2.0f) * this.scale : 0.0f;
        this.switchImageAfterAnimation = 1;
        animateTo(this.scale, ((this.minX - getContainerViewWidth()) - containerViewWidth) - (AndroidUtilities.dp(30.0f) / 2), this.translationY, false);
    }

    private void goToPrev() {
        float containerViewWidth = this.scale != 1.0f ? ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2.0f) * this.scale : 0.0f;
        this.switchImageAfterAnimation = 2;
        animateTo(this.scale, this.maxX + getContainerViewWidth() + containerViewWidth + (AndroidUtilities.dp(30.0f) / 2), this.translationY, false);
    }

    private void cancelMoveZoomAnimation() {
        AnimatorSet animatorSet = this.imageMoveAnimation;
        if (animatorSet == null) {
            return;
        }
        float f = this.scale;
        float f2 = this.animationValue;
        float f3 = f + ((this.animateToScale - f) * f2);
        float f4 = this.translationX;
        float f5 = f4 + ((this.animateToX - f4) * f2);
        float f6 = this.translationY;
        float f7 = f6 + ((this.animateToY - f6) * f2);
        float f8 = this.rotate;
        animatorSet.cancel();
        this.scale = f3;
        this.translationX = f5;
        this.translationY = f7;
        this.animationStartTime = 0L;
        this.rotate = f8 + ((this.animateToRotate - f8) * f2);
        updateMinMax(f3);
        this.zoomAnimation = false;
        this.containerView.invalidate();
    }

    private void animateTo(float f, float f2, float f3, boolean z) {
        animateTo(f, f2, f3, z, 250);
    }

    private void animateTo(float f, float f2, float f3, boolean z, int i) {
        if (this.scale == f && this.translationX == f2 && this.translationY == f3) {
            return;
        }
        this.zoomAnimation = z;
        this.animateToScale = f;
        this.animateToX = f2;
        this.animateToY = f3;
        this.animationStartTime = System.currentTimeMillis();
        AnimatorSet animatorSet = new AnimatorSet();
        this.imageMoveAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, AnimationProperties.PHOTO_VIEWER_ANIMATION_VALUE, 0.0f, 1.0f));
        this.imageMoveAnimation.setInterpolator(this.interpolator);
        this.imageMoveAnimation.setDuration(i);
        this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.75
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                PhotoViewer.this.imageMoveAnimation = null;
                PhotoViewer.this.containerView.invalidate();
            }
        });
        this.imageMoveAnimation.start();
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
        } else if (this.currentPageBlock != null) {
            TLObject media = this.pageBlocksAdapter.getMedia(this.currentIndex);
            if (media instanceof TLRPC$Document) {
                releasePlayer(false);
                FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC$Document) media);
            }
        }
        GroupedPhotosListView groupedPhotosListView = this.groupedPhotosListView;
        if (groupedPhotosListView != null) {
            groupedPhotosListView.setAnimateBackground(true);
        }
        this.playerAutoStarted = false;
        setImageIndex(this.currentIndex + i, z, true);
        if (shouldMessageObjectAutoPlayed(this.currentMessageObject) || shouldIndexAutoPlayed(this.currentIndex)) {
            this.playerAutoStarted = true;
            onActionClick(true);
            checkProgress(0, false, true);
        }
        checkFullscreenButton();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldMessageObjectAutoPlayed(MessageObject messageObject) {
        return messageObject != null && messageObject.isVideo() && (messageObject.mediaExists || messageObject.attachPathExists || (messageObject.canStreamVideo() && SharedConfig.streamMedia)) && SharedConfig.autoplayVideo;
    }

    private boolean shouldIndexAutoPlayed(int i) {
        File file;
        PageBlocksAdapter pageBlocksAdapter = this.pageBlocksAdapter;
        return pageBlocksAdapter != null && pageBlocksAdapter.isVideo(i) && SharedConfig.autoplayVideo && (file = this.pageBlocksAdapter.getFile(i)) != null && file.exists();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getCropFillScale(boolean z) {
        ImageReceiver imageReceiver = this.centerImage;
        int bitmapHeight = z ? imageReceiver.getBitmapHeight() : imageReceiver.getBitmapWidth();
        int bitmapWidth = z ? this.centerImage.getBitmapWidth() : this.centerImage.getBitmapHeight();
        float min = Math.min(this.photoCropView.getMeasuredWidth(), (this.photoCropView.getMeasuredHeight() - AndroidUtilities.dp(64.0f)) - (isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0)) - (AndroidUtilities.dp(16.0f) * 2);
        return Math.max(min / bitmapHeight, min / bitmapWidth);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isStatusBarVisible() {
        return Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:347:0x083a, code lost:
        if (r8 == 2) goto L351;
     */
    /* JADX WARN: Code restructure failed: missing block: B:352:0x0842, code lost:
        if (r9 == (-1)) goto L594;
     */
    /* JADX WARN: Code restructure failed: missing block: B:358:0x084b, code lost:
        if (r9 == r0) goto L351;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:125:0x02d2  */
    /* JADX WARN: Removed duplicated region for block: B:128:0x02d9  */
    /* JADX WARN: Removed duplicated region for block: B:141:0x0357 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:161:0x03a9  */
    /* JADX WARN: Removed duplicated region for block: B:164:0x03b5  */
    /* JADX WARN: Removed duplicated region for block: B:174:0x03d8  */
    /* JADX WARN: Removed duplicated region for block: B:175:0x03dd  */
    /* JADX WARN: Removed duplicated region for block: B:181:0x03f8  */
    /* JADX WARN: Removed duplicated region for block: B:182:0x03fa  */
    /* JADX WARN: Removed duplicated region for block: B:186:0x0401  */
    /* JADX WARN: Removed duplicated region for block: B:198:0x044d  */
    /* JADX WARN: Removed duplicated region for block: B:206:0x0481  */
    /* JADX WARN: Removed duplicated region for block: B:229:0x053b  */
    /* JADX WARN: Removed duplicated region for block: B:230:0x0543  */
    /* JADX WARN: Removed duplicated region for block: B:245:0x05c8  */
    /* JADX WARN: Removed duplicated region for block: B:248:0x0621  */
    /* JADX WARN: Removed duplicated region for block: B:250:0x062c  */
    /* JADX WARN: Removed duplicated region for block: B:255:0x064d  */
    /* JADX WARN: Removed duplicated region for block: B:262:0x0671  */
    /* JADX WARN: Removed duplicated region for block: B:266:0x067b  */
    /* JADX WARN: Removed duplicated region for block: B:274:0x0692 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:281:0x06c1  */
    /* JADX WARN: Removed duplicated region for block: B:282:0x06c4  */
    /* JADX WARN: Removed duplicated region for block: B:285:0x06dc  */
    /* JADX WARN: Removed duplicated region for block: B:289:0x06f1  */
    /* JADX WARN: Removed duplicated region for block: B:292:0x06fd A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:328:0x07dc  */
    /* JADX WARN: Removed duplicated region for block: B:336:0x0821  */
    /* JADX WARN: Removed duplicated region for block: B:362:0x0852  */
    /* JADX WARN: Removed duplicated region for block: B:365:0x0859  */
    /* JADX WARN: Removed duplicated region for block: B:404:0x08eb  */
    /* JADX WARN: Removed duplicated region for block: B:407:0x08f6  */
    /* JADX WARN: Removed duplicated region for block: B:415:0x090e  */
    /* JADX WARN: Removed duplicated region for block: B:419:0x092e  */
    /* JADX WARN: Removed duplicated region for block: B:429:0x0958  */
    /* JADX WARN: Removed duplicated region for block: B:450:0x099e  */
    /* JADX WARN: Removed duplicated region for block: B:459:0x09d5  */
    /* JADX WARN: Removed duplicated region for block: B:462:0x09df  */
    /* JADX WARN: Removed duplicated region for block: B:474:0x0a41  */
    /* JADX WARN: Removed duplicated region for block: B:477:0x0a49  */
    /* JADX WARN: Removed duplicated region for block: B:484:0x0a62  */
    /* JADX WARN: Removed duplicated region for block: B:487:0x0a73  */
    /* JADX WARN: Removed duplicated region for block: B:494:0x0aa0  */
    /* JADX WARN: Removed duplicated region for block: B:526:0x0b15  */
    /* JADX WARN: Removed duplicated region for block: B:529:0x0b1c  */
    /* JADX WARN: Removed duplicated region for block: B:530:0x0b39  */
    /* JADX WARN: Removed duplicated region for block: B:533:0x0b4a  */
    /* JADX WARN: Removed duplicated region for block: B:537:0x0b57  */
    /* JADX WARN: Removed duplicated region for block: B:560:0x0bdf  */
    /* JADX WARN: Removed duplicated region for block: B:562:0x0be5  */
    /* JADX WARN: Removed duplicated region for block: B:569:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:579:0x0c2c  */
    /* JADX WARN: Removed duplicated region for block: B:606:0x0ceb  */
    /* JADX WARN: Removed duplicated region for block: B:652:0x0e58  */
    /* JADX WARN: Removed duplicated region for block: B:657:0x0e6b  */
    /* JADX WARN: Removed duplicated region for block: B:677:0x0ec5  */
    /* JADX WARN: Removed duplicated region for block: B:680:0x0ecc  */
    /* JADX WARN: Removed duplicated region for block: B:690:0x0cd7 A[EDGE_INSN: B:690:0x0cd7->B:604:0x0cd7 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:695:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r15v28 */
    /* JADX WARN: Type inference failed for: r15v3 */
    /* JADX WARN: Type inference failed for: r15v37 */
    /* JADX WARN: Type inference failed for: r15v4, types: [boolean, int] */
    @android.annotation.SuppressLint({"NewApi", "DrawAllocation"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onDraw(android.graphics.Canvas r45) {
        /*
            Method dump skipped, instructions count: 3855
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onDraw(android.graphics.Canvas):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDraw$81() {
        switchToNextIndex(1, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDraw$82() {
        switchToNextIndex(-1, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x004b  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x004e  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0059  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x005e  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0067  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void drawProgress(android.graphics.Canvas r9, float r10, float r11, float r12, float r13) {
        /*
            Method dump skipped, instructions count: 234
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.drawProgress(android.graphics.Canvas, float, float, float, float):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x0097  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00b6  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0111  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x011b  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x016f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int[] applyCrop(android.graphics.Canvas r23, int r24, int r25, int r26, int r27, float r28, org.telegram.ui.Components.Crop.CropTransform r29, org.telegram.messenger.MediaController.CropState r30) {
        /*
            Method dump skipped, instructions count: 402
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.applyCrop(android.graphics.Canvas, int, int, int, int, float, org.telegram.ui.Components.Crop.CropTransform, org.telegram.messenger.MediaController$CropState):int[]");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0039, code lost:
        if (r1.exists() == false) goto L90;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0152, code lost:
        if (r1.exists() == false) goto L88;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0191, code lost:
        if (r1.exists() == false) goto L88;
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x01b1, code lost:
        if (r1.exists() == false) goto L88;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onActionClick(boolean r11) {
        /*
            Method dump skipped, instructions count: 718
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onActionClick(boolean):void");
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public boolean onDown(MotionEvent motionEvent) {
        if (!this.doubleTap && this.checkImageView.getVisibility() != 0) {
            boolean[] zArr = this.drawPressedDrawable;
            if (!zArr[0] && !zArr[1]) {
                float x = motionEvent.getX();
                int min = Math.min(135, this.containerView.getMeasuredWidth() / 8);
                if (x < min) {
                    if (this.leftImage.hasImageSet()) {
                        this.drawPressedDrawable[0] = true;
                        this.containerView.invalidate();
                    }
                } else if (x > this.containerView.getMeasuredWidth() - min && this.rightImage.hasImageSet()) {
                    this.drawPressedDrawable[1] = true;
                    this.containerView.invalidate();
                }
            }
        }
        return false;
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnDoubleTapListener
    public boolean canDoubleTap(MotionEvent motionEvent) {
        MessageObject messageObject;
        PhotoViewerWebView photoViewerWebView;
        if (this.checkImageView.getVisibility() != 0) {
            boolean[] zArr = this.drawPressedDrawable;
            if (zArr[0] || zArr[1]) {
                return true;
            }
            float x = motionEvent.getX();
            int min = Math.min(135, this.containerView.getMeasuredWidth() / 8);
            if ((x >= min && x <= this.containerView.getMeasuredWidth() - min) || (messageObject = this.currentMessageObject) == null) {
                return true;
            }
            return (messageObject.isVideo() || ((photoViewerWebView = this.photoViewerWebView) != null && photoViewerWebView.isControllable())) && SystemClock.elapsedRealtime() - this.lastPhotoSetTime >= 500 && canDoubleTapSeekVideo(motionEvent);
        }
        return true;
    }

    private void hidePressedDrawables() {
        boolean[] zArr = this.drawPressedDrawable;
        zArr[1] = false;
        zArr[0] = false;
        this.containerView.invalidate();
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public void onUp(MotionEvent motionEvent) {
        hidePressedDrawables();
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        if (!this.canZoom && !this.doubleTapEnabled) {
            return onSingleTapConfirmed(motionEvent);
        }
        if (this.containerView.getTag() != null && this.photoProgressViews[0] != null && this.containerView != null) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if ((x < ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f || x > ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f || y < ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f || y > ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) ? false : onSingleTapConfirmed(motionEvent)) {
                this.discardTap = true;
                return true;
            }
        }
        return false;
    }

    public void onLongPress() {
        boolean z;
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null || !this.videoPlayerControlVisible || this.scale > 1.1f) {
            return;
        }
        long currentPosition = videoPlayer.getCurrentPosition();
        long duration = this.videoPlayer.getDuration();
        if (currentPosition == -9223372036854775807L || duration < 15000) {
            return;
        }
        float f = this.longPressX;
        int containerViewWidth = getContainerViewWidth() / 3;
        if (f >= containerViewWidth * 2) {
            z = true;
        } else if (f >= containerViewWidth) {
            return;
        } else {
            z = false;
        }
        this.videoPlayerRewinder.startRewind(this.videoPlayer, z, this.currentVideoSpeed);
    }

    public VideoPlayerRewinder getVideoPlayerRewinder() {
        return this.videoPlayerRewinder;
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnGestureListener
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (this.scale != 1.0f) {
            this.scroller.abortAnimation();
            this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(f), Math.round(f2), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
            this.containerView.postInvalidate();
            return false;
        }
        return false;
    }

    @Override // org.telegram.ui.Components.GestureDetector2.OnDoubleTapListener
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        PhotoViewerWebView photoViewerWebView;
        MessageObject messageObject;
        if (this.discardTap) {
            return false;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (this.checkImageView.getVisibility() != 0 && y > ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight + AndroidUtilities.dp(40.0f)) {
            int min = Math.min(135, this.containerView.getMeasuredWidth() / 8);
            if (x < min) {
                if (this.leftImage.hasImageSet()) {
                    switchToNextIndex(-1, true);
                    return true;
                }
            } else if (x > this.containerView.getMeasuredWidth() - min && this.rightImage.hasImageSet()) {
                switchToNextIndex(1, true);
                return true;
            }
        }
        PhotoViewerWebView photoViewerWebView2 = this.photoViewerWebView;
        if (photoViewerWebView2 != null && photoViewerWebView2.isControllable() && this.isActionBarVisible) {
            WebView webView = this.photoViewerWebView.getWebView();
            if (x >= webView.getX() && x <= webView.getX() + webView.getWidth() && y >= webView.getY() && y <= webView.getY() + webView.getHeight()) {
                MotionEvent obtain = MotionEvent.obtain(motionEvent);
                obtain.setAction(0);
                obtain.offsetLocation(-webView.getX(), -webView.getY());
                webView.dispatchTouchEvent(obtain);
                obtain.setAction(1);
                webView.dispatchTouchEvent(obtain);
                obtain.recycle();
                scheduleActionBarHide();
                return true;
            }
        }
        if (this.containerView.getTag() != null) {
            AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
            boolean z = (aspectRatioFrameLayout != null && aspectRatioFrameLayout.getVisibility() == 0) || ((photoViewerWebView = this.photoViewerWebView) != null && photoViewerWebView.isControllable());
            if (this.sharedMediaType == 1 && (messageObject = this.currentMessageObject) != null) {
                if (!messageObject.canPreviewDocument()) {
                    float containerViewHeight = (getContainerViewHeight() - AndroidUtilities.dp(360.0f)) / 2.0f;
                    if (y >= containerViewHeight && y <= containerViewHeight + AndroidUtilities.dp(360.0f)) {
                        onActionClick(true);
                        return true;
                    }
                }
            } else {
                PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
                if (photoProgressViewArr[0] != null && this.containerView != null) {
                    int i = photoProgressViewArr[0].backgroundState;
                    if (x >= (getContainerViewWidth() - AndroidUtilities.dp(100.0f)) / 2.0f && x <= (getContainerViewWidth() + AndroidUtilities.dp(100.0f)) / 2.0f && y >= (getContainerViewHeight() - AndroidUtilities.dp(100.0f)) / 2.0f && y <= (getContainerViewHeight() + AndroidUtilities.dp(100.0f)) / 2.0f) {
                        if (!z) {
                            if (i > 0 && i <= 3) {
                                onActionClick(true);
                                checkProgress(0, false, true);
                                return true;
                            }
                        } else if ((i == 3 || i == 4) && this.photoProgressViews[0].isVisible()) {
                            this.manuallyPaused = true;
                            toggleVideoPlayer();
                            return true;
                        }
                    }
                }
            }
            PhotoViewerWebView photoViewerWebView3 = this.photoViewerWebView;
            if (photoViewerWebView3 == null || !photoViewerWebView3.isControllable() || this.photoViewerWebView.isPlaying() || !this.isActionBarVisible) {
                toggleActionBar(!this.isActionBarVisible, true);
            }
        } else {
            int i2 = this.sendPhotoType;
            if (i2 == 0 || i2 == 4) {
                if (this.isCurrentVideo) {
                    VideoPlayer videoPlayer = this.videoPlayer;
                    if (videoPlayer != null && !this.muteVideo && i2 != 1) {
                        videoPlayer.setVolume(1.0f);
                    }
                    this.manuallyPaused = true;
                    toggleVideoPlayer();
                } else {
                    this.checkImageView.performClick();
                }
            } else {
                TLRPC$BotInlineResult tLRPC$BotInlineResult = this.currentBotInlineResult;
                if (tLRPC$BotInlineResult != null && (tLRPC$BotInlineResult.type.equals("video") || MessageObject.isVideoDocument(this.currentBotInlineResult.document))) {
                    int i3 = this.photoProgressViews[0].backgroundState;
                    if (i3 > 0 && i3 <= 3 && x >= (getContainerViewWidth() - AndroidUtilities.dp(100.0f)) / 2.0f && x <= (getContainerViewWidth() + AndroidUtilities.dp(100.0f)) / 2.0f && y >= (getContainerViewHeight() - AndroidUtilities.dp(100.0f)) / 2.0f && y <= (getContainerViewHeight() + AndroidUtilities.dp(100.0f)) / 2.0f) {
                        onActionClick(true);
                        checkProgress(0, false, true);
                        return true;
                    }
                } else if (this.sendPhotoType == 2 && this.isCurrentVideo) {
                    this.manuallyPaused = true;
                    toggleVideoPlayer();
                }
            }
        }
        return true;
    }

    private boolean canDoubleTapSeekVideo(MotionEvent motionEvent) {
        PhotoViewerWebView photoViewerWebView;
        if (this.videoPlayer != null || ((photoViewerWebView = this.photoViewerWebView) != null && photoViewerWebView.isControllable())) {
            boolean z = motionEvent.getX() >= ((float) ((getContainerViewWidth() / 3) * 2));
            long currentVideoPosition = getCurrentVideoPosition();
            long videoDuration = getVideoDuration();
            if (currentVideoPosition == -9223372036854775807L || videoDuration <= 15000) {
                return false;
            }
            return !z || videoDuration - currentVideoPosition > 10000;
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:57:0x010a, code lost:
        if (r1 > r3) goto L23;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x0119, code lost:
        if (r2 > r3) goto L26;
     */
    /* JADX WARN: Removed duplicated region for block: B:32:0x005f  */
    @Override // org.telegram.ui.Components.GestureDetector2.OnDoubleTapListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onDoubleTap(android.view.MotionEvent r17) {
        /*
            Method dump skipped, instructions count: 298
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoViewer.onDoubleTap(android.view.MotionEvent):boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class QualityChooseView extends View {
        private int circleSize;
        private int gapSize;
        private String hightQualityDescription;
        private int lineSize;
        private String lowQualityDescription;
        private Paint paint;
        private int sideSide;
        private int startMovingQuality;
        private TextPaint textPaint;

        public QualityChooseView(Context context) {
            super(context);
            this.paint = new Paint(1);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(14.0f));
            this.textPaint.setColor(-3289651);
            this.lowQualityDescription = LocaleController.getString("AccDescrVideoCompressLow", R.string.AccDescrVideoCompressLow);
            this.hightQualityDescription = LocaleController.getString("AccDescrVideoCompressHigh", R.string.AccDescrVideoCompressHigh);
        }

        @Override // android.view.View
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
                    if (x <= i6 - i7 || x >= i6 + i7) {
                        i++;
                    } else if (PhotoViewer.this.selectedCompression != i) {
                        PhotoViewer.this.selectedCompression = i;
                        PhotoViewer.this.didChangedCompressionLevel(false);
                        invalidate();
                    }
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (PhotoViewer.this.selectedCompression != this.startMovingQuality) {
                    PhotoViewer.this.requestVideoPreview(1);
                }
                PhotoViewer.this.moving = false;
            }
            return true;
        }

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            this.circleSize = AndroidUtilities.dp(8.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(18.0f);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
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
                canvas.drawCircle(i5, measuredHeight, i == PhotoViewer.this.selectedCompression ? AndroidUtilities.dp(6.0f) : this.circleSize / 2, this.paint);
                if (i != 0) {
                    int i6 = ((i5 - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    float f = 0.0f;
                    float dpf2 = i == PhotoViewer.this.selectedCompression + 1 ? AndroidUtilities.dpf2(2.0f) : 0.0f;
                    if (i == PhotoViewer.this.selectedCompression) {
                        f = AndroidUtilities.dpf2(2.0f);
                    }
                    canvas.drawRect(dpf2 + i6, measuredHeight - AndroidUtilities.dp(1.0f), (i6 + this.lineSize) - f, AndroidUtilities.dp(2.0f) + measuredHeight, this.paint);
                }
                i++;
            }
            canvas.drawText(this.lowQualityDescription, this.sideSide, measuredHeight - AndroidUtilities.dp(16.0f), this.textPaint);
            canvas.drawText(this.hightQualityDescription, (getMeasuredWidth() - this.sideSide) - this.textPaint.measureText(this.hightQualityDescription), measuredHeight - AndroidUtilities.dp(16.0f), this.textPaint);
        }
    }

    public void updateMuteButton() {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.setMute(this.muteVideo);
        }
        if (!this.videoConvertSupported) {
            this.muteItem.setEnabled(false);
            this.muteItem.setClickable(false);
            this.muteItem.animate().alpha(0.5f).setDuration(180L).start();
            this.videoTimelineView.setMode(0);
            return;
        }
        this.muteItem.setEnabled(true);
        this.muteItem.setClickable(true);
        this.muteItem.animate().alpha(1.0f).setDuration(180L).start();
        if (this.muteVideo) {
            this.actionBar.setSubtitle(LocaleController.getString("SoundMuted", R.string.SoundMuted));
            this.muteItem.setImageResource(R.drawable.video_send_mute);
            if (this.compressItem.getTag() != null) {
                this.compressItem.setAlpha(0.5f);
                this.compressItem.setEnabled(false);
            }
            if (this.sendPhotoType == 1) {
                this.videoTimelineView.setMaxProgressDiff(9600.0f / this.videoDuration);
                this.videoTimelineView.setMode(1);
                updateVideoInfo();
            } else {
                this.videoTimelineView.setMaxProgressDiff(1.0f);
                this.videoTimelineView.setMode(0);
            }
            this.muteItem.setContentDescription(LocaleController.getString("NoSound", R.string.NoSound));
            return;
        }
        this.actionBar.setSubtitle(this.currentSubtitle);
        this.muteItem.setImageResource(R.drawable.video_send_unmute);
        this.muteItem.setContentDescription(LocaleController.getString("Sound", R.string.Sound));
        if (this.compressItem.getTag() != null) {
            this.compressItem.setAlpha(1.0f);
            this.compressItem.setEnabled(true);
        }
        this.videoTimelineView.setMaxProgressDiff(1.0f);
        this.videoTimelineView.setMode(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void didChangedCompressionLevel(boolean z) {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt(String.format("compress_video_%d", Integer.valueOf(this.compressionsCount)), this.selectedCompression);
        edit.commit();
        updateWidthHeightBitrateForCompression();
        updateVideoInfo();
        if (z) {
            requestVideoPreview(1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVideoInfo() {
        int i;
        int i2;
        float f;
        ActionBar actionBar = this.actionBar;
        if (actionBar == null) {
            return;
        }
        if (this.compressionsCount == 0) {
            actionBar.setSubtitle(null);
            return;
        }
        int i3 = this.selectedCompression;
        if (i3 < 2) {
            this.compressItem.setImageResource(R.drawable.video_quality1);
        } else if (i3 == 2) {
            this.compressItem.setImageResource(R.drawable.video_quality2);
        } else if (i3 == 3) {
            this.compressItem.setImageResource(R.drawable.video_quality3);
        }
        this.itemsLayout.requestLayout();
        long ceil = (long) Math.ceil((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration);
        this.estimatedDuration = ceil;
        if (this.muteVideo) {
            int i4 = this.rotationValue;
            i = (i4 == 90 || i4 == 270) ? this.resultHeight : this.resultWidth;
            i2 = (i4 == 90 || i4 == 270) ? this.resultWidth : this.resultHeight;
            long j = ((this.sendPhotoType == 1 ? ceil <= 2000 ? 2600000 : ceil <= 5000 ? 2200000 : 1560000 : 921600) / 8) * (((float) ceil) / 1000.0f);
            this.estimatedSize = j;
            this.estimatedSize = j + ((j / 32768) * 16);
        } else if (this.compressItem.getTag() == null) {
            int i5 = this.rotationValue;
            i = (i5 == 90 || i5 == 270) ? this.originalHeight : this.originalWidth;
            i2 = (i5 == 90 || i5 == 270) ? this.originalWidth : this.originalHeight;
            this.estimatedSize = ((float) this.originalSize) * (((float) this.estimatedDuration) / this.videoDuration);
        } else {
            int i6 = this.rotationValue;
            i = (i6 == 90 || i6 == 270) ? this.resultHeight : this.resultWidth;
            i2 = (i6 == 90 || i6 == 270) ? this.resultWidth : this.resultHeight;
            long j2 = ((float) ((this.sendPhotoType == 1 ? 0L : this.audioFramesSize) + this.videoFramesSize)) * (((float) this.estimatedDuration) / this.videoDuration);
            this.estimatedSize = j2;
            this.estimatedSize = j2 + ((j2 / 32768) * 16);
        }
        this.videoCutStart = this.videoTimelineView.getLeftProgress();
        float rightProgress = this.videoTimelineView.getRightProgress();
        this.videoCutEnd = rightProgress;
        if (this.videoCutStart == 0.0f) {
            this.startTime = -1L;
        } else {
            this.startTime = f * this.videoDuration * 1000;
        }
        if (rightProgress == 1.0f) {
            this.endTime = -1L;
        } else {
            this.endTime = rightProgress * this.videoDuration * 1000;
        }
        this.currentSubtitle = String.format("%s, %s", String.format("%dx%d", Integer.valueOf(i), Integer.valueOf(i2)), String.format("%s, ~%s", AndroidUtilities.formatShortDuration((int) (this.estimatedDuration / 1000)), AndroidUtilities.formatFileSize(this.estimatedSize)));
        this.actionBar.beginDelayedTransition();
        this.actionBar.setSubtitle(this.muteVideo ? LocaleController.getString("SoundMuted", R.string.SoundMuted) : this.currentSubtitle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestVideoPreview(int i) {
        if (this.videoPreviewMessageObject != null) {
            MediaController.getInstance().cancelVideoConvert(this.videoPreviewMessageObject);
        }
        boolean z = this.requestingPreview && !this.tryStartRequestPreviewOnFinish;
        this.requestingPreview = false;
        this.loadInitialVideo = false;
        this.progressView.setVisibility(4);
        if (i == 1) {
            if (this.resultHeight == this.originalHeight && this.resultWidth == this.originalWidth) {
                this.tryStartRequestPreviewOnFinish = false;
                PhotoProgressView[] photoProgressViewArr = this.photoProgressViews;
                photoProgressViewArr[0].setProgress(0.0f, photoProgressViewArr[0].backgroundState == 0 || this.photoProgressViews[0].previousBackgroundState == 0);
                this.photoProgressViews[0].setBackgroundState(3, false, true);
                if (!z) {
                    preparePlayer(this.currentPlayingVideoFile, false, false, this.editState.savedFilterState);
                    this.videoPlayer.seekTo(this.videoTimelineView.getLeftProgress() * this.videoDuration);
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
                    MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message, false, false);
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
                    j2 = this.videoDuration * 1000.0f;
                }
                if (j2 - j > 5000000) {
                    videoEditedInfo2.endTime = j + 5000000;
                }
                videoEditedInfo2.bitrate = this.bitrate;
                videoEditedInfo2.resultWidth = this.resultWidth;
                videoEditedInfo2.resultHeight = this.resultHeight;
                videoEditedInfo2.needUpdateProgress = true;
                videoEditedInfo2.originalDuration = this.videoDuration * 1000.0f;
                if (!MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true)) {
                    this.tryStartRequestPreviewOnFinish = true;
                }
                this.requestingPreview = true;
                PhotoProgressView[] photoProgressViewArr2 = this.photoProgressViews;
                photoProgressViewArr2[0].setProgress(0.0f, photoProgressViewArr2[0].backgroundState == 0 || this.photoProgressViews[0].previousBackgroundState == 0);
                this.photoProgressViews[0].setBackgroundState(0, false, true);
            }
        } else {
            this.tryStartRequestPreviewOnFinish = false;
            this.photoProgressViews[0].setBackgroundState(3, false, true);
            if (i == 2) {
                preparePlayer(this.currentPlayingVideoFile, false, false, this.editState.savedFilterState);
                this.videoPlayer.seekTo(this.videoTimelineView.getLeftProgress() * this.videoDuration);
            }
        }
        this.containerView.invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateWidthHeightBitrateForCompression() {
        int i = this.compressionsCount;
        if (i <= 0) {
            return;
        }
        if (this.selectedCompression >= i) {
            this.selectedCompression = i - 1;
        }
        if (this.sendPhotoType == 1) {
            float max = Math.max(800.0f / this.originalWidth, 800.0f / this.originalHeight);
            this.resultWidth = Math.round((this.originalWidth * max) / 2.0f) * 2;
            this.resultHeight = Math.round((this.originalHeight * max) / 2.0f) * 2;
        } else {
            int i2 = this.selectedCompression;
            float f = i2 != 0 ? i2 != 1 ? i2 != 2 ? 1920.0f : 1280.0f : 854.0f : 480.0f;
            int i3 = this.originalWidth;
            int i4 = this.originalHeight;
            float f2 = f / (i3 > i4 ? i3 : i4);
            if (i2 == i - 1 && f2 >= 1.0f) {
                this.resultWidth = i3;
                this.resultHeight = i4;
            } else {
                this.resultWidth = Math.round((i3 * f2) / 2.0f) * 2;
                this.resultHeight = Math.round((this.originalHeight * f2) / 2.0f) * 2;
            }
        }
        if (this.bitrate == 0) {
            return;
        }
        if (this.sendPhotoType == 1) {
            this.bitrate = 1560000;
        } else {
            int i5 = this.resultWidth;
            int i6 = this.originalWidth;
            if (i5 == i6 && this.resultHeight == this.originalHeight) {
                this.bitrate = this.originalBitrate;
            } else {
                this.bitrate = MediaController.makeVideoBitrate(this.originalHeight, i6, this.originalBitrate, this.resultHeight, i5);
            }
        }
        this.videoFramesSize = ((this.bitrate / 8) * this.videoDuration) / 1000.0f;
    }

    private void showQualityView(final boolean z) {
        TextureView textureView;
        if (z && this.textureUploaded && this.videoSizeSet && !this.changingTextureView && (textureView = this.videoTextureView) != null) {
            this.videoFrameBitmap = textureView.getBitmap();
        }
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
            this.qualityChooseViewAnimation.playTogether(ObjectAnimator.ofFloat(this.pickerView, View.TRANSLATION_Y, 0.0f, AndroidUtilities.dp(152.0f)), ObjectAnimator.ofFloat(this.pickerViewSendButton, View.TRANSLATION_Y, 0.0f, AndroidUtilities.dp(152.0f)), ObjectAnimator.ofFloat(this.bottomLayout, View.TRANSLATION_Y, -AndroidUtilities.dp(48.0f), AndroidUtilities.dp(104.0f)));
        } else {
            this.qualityChooseView.setTag(null);
            this.qualityChooseViewAnimation.playTogether(ObjectAnimator.ofFloat(this.qualityChooseView, View.TRANSLATION_Y, 0.0f, AndroidUtilities.dp(166.0f)), ObjectAnimator.ofFloat(this.qualityPicker, View.TRANSLATION_Y, 0.0f, AndroidUtilities.dp(166.0f)), ObjectAnimator.ofFloat(this.bottomLayout, View.TRANSLATION_Y, -AndroidUtilities.dp(48.0f), AndroidUtilities.dp(118.0f)));
        }
        this.qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.78
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (!animator.equals(PhotoViewer.this.qualityChooseViewAnimation)) {
                    return;
                }
                PhotoViewer.this.qualityChooseViewAnimation = new AnimatorSet();
                if (z) {
                    PhotoViewer.this.qualityChooseView.setVisibility(0);
                    PhotoViewer.this.qualityPicker.setVisibility(0);
                    PhotoViewer.this.qualityChooseViewAnimation.playTogether(ObjectAnimator.ofFloat(PhotoViewer.this.qualityChooseView, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(PhotoViewer.this.qualityPicker, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, View.TRANSLATION_Y, -AndroidUtilities.dp(48.0f)));
                } else {
                    PhotoViewer.this.qualityChooseView.setVisibility(4);
                    PhotoViewer.this.qualityPicker.setVisibility(4);
                    PhotoViewer.this.qualityChooseViewAnimation.playTogether(ObjectAnimator.ofFloat(PhotoViewer.this.pickerView, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(PhotoViewer.this.pickerViewSendButton, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(PhotoViewer.this.bottomLayout, View.TRANSLATION_Y, -AndroidUtilities.dp(48.0f)));
                }
                PhotoViewer.this.qualityChooseViewAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.78.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator2) {
                        if (animator2.equals(PhotoViewer.this.qualityChooseViewAnimation)) {
                            PhotoViewer.this.qualityChooseViewAnimation = null;
                        }
                    }
                });
                PhotoViewer.this.qualityChooseViewAnimation.setDuration(200L);
                PhotoViewer.this.qualityChooseViewAnimation.setInterpolator(AndroidUtilities.decelerateInterpolator);
                PhotoViewer.this.qualityChooseViewAnimation.start();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                PhotoViewer.this.qualityChooseViewAnimation = null;
            }
        });
        this.qualityChooseViewAnimation.setDuration(200L);
        this.qualityChooseViewAnimation.setInterpolator(AndroidUtilities.accelerateInterpolator);
        this.qualityChooseViewAnimation.start();
        float f2 = 0.25f;
        if (this.cameraItem.getVisibility() == 0) {
            this.cameraItem.animate().scaleX(z ? 0.25f : 1.0f).scaleY(z ? 0.25f : 1.0f).alpha(z ? 0.0f : 1.0f).setDuration(200L);
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
            scaleY.alpha(f).setDuration(200L);
        }
    }

    private void processOpenVideo(String str, boolean z, float f, float f2) {
        if (this.currentLoadingVideoRunnable != null) {
            Utilities.globalQueue.cancelRunnable(this.currentLoadingVideoRunnable);
            this.currentLoadingVideoRunnable = null;
        }
        this.videoTimelineView.setVideoPath(str, f, f2);
        this.videoPreviewMessageObject = null;
        boolean z2 = true;
        if (!z && this.sendPhotoType != 1) {
            z2 = false;
        }
        this.muteVideo = z2;
        this.compressionsCount = -1;
        this.rotationValue = 0;
        this.videoFramerate = 25;
        this.originalSize = new File(str).length();
        DispatchQueue dispatchQueue = Utilities.globalQueue;
        AnonymousClass79 anonymousClass79 = new AnonymousClass79(str);
        this.currentLoadingVideoRunnable = anonymousClass79;
        dispatchQueue.postRunnable(anonymousClass79);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.PhotoViewer$79  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass79 implements Runnable {
        final /* synthetic */ String val$videoPath;

        AnonymousClass79(String str) {
            this.val$videoPath = str;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (PhotoViewer.this.currentLoadingVideoRunnable != this) {
                return;
            }
            final int videoBitrate = MediaController.getVideoBitrate(this.val$videoPath);
            final int[] iArr = new int[11];
            AnimatedFileDrawable.getVideoInfo(this.val$videoPath, iArr);
            if (PhotoViewer.this.currentLoadingVideoRunnable != this) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$79$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoViewer.AnonymousClass79.this.lambda$run$0(this, iArr, videoBitrate);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0(Runnable runnable, int[] iArr, int i) {
            if (PhotoViewer.this.parentActivity == null || runnable != PhotoViewer.this.currentLoadingVideoRunnable) {
                return;
            }
            PhotoViewer.this.currentLoadingVideoRunnable = null;
            boolean z = iArr[10] != 0;
            PhotoViewer.this.videoConvertSupported = iArr[0] != 0 && (!z || iArr[9] != 0);
            PhotoViewer.this.audioFramesSize = iArr[5];
            PhotoViewer.this.videoDuration = iArr[4];
            if (i == -1) {
                PhotoViewer photoViewer = PhotoViewer.this;
                photoViewer.originalBitrate = photoViewer.bitrate = iArr[3];
            } else {
                PhotoViewer photoViewer2 = PhotoViewer.this;
                photoViewer2.originalBitrate = photoViewer2.bitrate = i;
            }
            PhotoViewer.this.videoFramerate = iArr[7];
            PhotoViewer photoViewer3 = PhotoViewer.this;
            photoViewer3.videoFramesSize = ((photoViewer3.bitrate / 8) * PhotoViewer.this.videoDuration) / 1000.0f;
            if (PhotoViewer.this.videoConvertSupported) {
                PhotoViewer.this.rotationValue = iArr[8];
                PhotoViewer photoViewer4 = PhotoViewer.this;
                photoViewer4.resultWidth = photoViewer4.originalWidth = iArr[1];
                PhotoViewer photoViewer5 = PhotoViewer.this;
                photoViewer5.resultHeight = photoViewer5.originalHeight = iArr[2];
                PhotoViewer photoViewer6 = PhotoViewer.this;
                photoViewer6.updateCompressionsCount(photoViewer6.originalWidth, PhotoViewer.this.originalHeight);
                PhotoViewer photoViewer7 = PhotoViewer.this;
                photoViewer7.selectedCompression = photoViewer7.selectCompression();
                PhotoViewer.this.updateWidthHeightBitrateForCompression();
                if (PhotoViewer.this.selectedCompression > PhotoViewer.this.compressionsCount - 1) {
                    PhotoViewer photoViewer8 = PhotoViewer.this;
                    photoViewer8.selectedCompression = photoViewer8.compressionsCount - 1;
                }
                PhotoViewer photoViewer9 = PhotoViewer.this;
                photoViewer9.setCompressItemEnabled(photoViewer9.compressionsCount > 1, true);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("compressionsCount = " + PhotoViewer.this.compressionsCount + " w = " + PhotoViewer.this.originalWidth + " h = " + PhotoViewer.this.originalHeight + " r = " + PhotoViewer.this.rotationValue);
                }
                if (Build.VERSION.SDK_INT < 18 && PhotoViewer.this.compressItem.getTag() != null) {
                    PhotoViewer.this.videoConvertSupported = false;
                    PhotoViewer.this.setCompressItemEnabled(false, true);
                }
                PhotoViewer.this.qualityChooseView.invalidate();
            } else {
                PhotoViewer.this.setCompressItemEnabled(false, true);
                PhotoViewer.this.compressionsCount = 0;
            }
            PhotoViewer.this.updateVideoInfo();
            PhotoViewer.this.updateMuteButton();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int selectCompression() {
        if (this.originalSize > NUM) {
            return this.compressionsCount - 1;
        }
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        int i = this.compressionsCount;
        while (i < 5) {
            int i2 = globalMainSettings.getInt(String.format(Locale.US, "compress_video_%d", Integer.valueOf(i)), -1);
            if (i2 >= 0) {
                return Math.min(i2, 2);
            }
            i++;
        }
        return Math.min(2, Math.round(DownloadController.getInstance(this.currentAccount).getMaxVideoBitrate() / (100.0f / i)) - 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
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

    /* JADX INFO: Access modifiers changed from: private */
    public void setCompressItemEnabled(boolean z, boolean z2) {
        ImageView imageView = this.compressItem;
        if (imageView == null) {
            return;
        }
        if (z && imageView.getTag() != null) {
            return;
        }
        if (!z && this.compressItem.getTag() == null) {
            return;
        }
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
            this.compressItemAnimation.setDuration(180L);
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

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAccessibilityOverlayVisibility() {
        if (this.playButtonAccessibilityOverlay != null) {
            int i = this.photoProgressViews[0].backgroundState;
            if (this.photoProgressViews[0].isVisible() && (i == 3 || i == 4 || i == 2 || i == 1)) {
                if (i == 3) {
                    this.playButtonAccessibilityOverlay.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
                } else if (i == 2) {
                    this.playButtonAccessibilityOverlay.setContentDescription(LocaleController.getString("AccActionDownload", R.string.AccActionDownload));
                } else if (i == 1) {
                    this.playButtonAccessibilityOverlay.setContentDescription(LocaleController.getString("AccActionCancelDownload", R.string.AccActionCancelDownload));
                } else {
                    this.playButtonAccessibilityOverlay.setContentDescription(LocaleController.getString("AccActionPause", R.string.AccActionPause));
                }
                this.playButtonAccessibilityOverlay.setVisibility(0);
                return;
            }
            this.playButtonAccessibilityOverlay.setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (PhotoViewer.this.placeProvider == null || PhotoViewer.this.placeProvider.getSelectedPhotosOrder() == null) {
                return 0;
            }
            return PhotoViewer.this.placeProvider.getSelectedPhotosOrder().size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1818onCreateViewHolder(ViewGroup viewGroup, int i) {
            PhotoPickerPhotoCell photoPickerPhotoCell = new PhotoPickerPhotoCell(this.mContext);
            photoPickerPhotoCell.checkFrame.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$ListAdapter$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PhotoViewer.ListAdapter.this.lambda$onCreateViewHolder$0(view);
                }
            });
            return new RecyclerListView.Holder(photoPickerPhotoCell);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$0(View view) {
            Object tag = ((View) view.getParent()).getTag();
            int indexOf = PhotoViewer.this.imagesArrLocals.indexOf(tag);
            if (indexOf >= 0) {
                int photoChecked = PhotoViewer.this.placeProvider.setPhotoChecked(indexOf, PhotoViewer.this.getCurrentVideoEditedInfo());
                boolean isPhotoChecked = PhotoViewer.this.placeProvider.isPhotoChecked(indexOf);
                if (indexOf == PhotoViewer.this.currentIndex) {
                    PhotoViewer.this.checkImageView.setChecked(-1, isPhotoChecked, true);
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
            if (photoUnchecked < 0) {
                return;
            }
            PhotoViewer.this.selectedPhotosAdapter.notifyItemRemoved(photoUnchecked);
            if (photoUnchecked == 0) {
                PhotoViewer.this.selectedPhotosAdapter.notifyItemChanged(0);
            }
            PhotoViewer.this.updateSelectedCount();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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
                    backupImageView.setImage(str, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                } else if (photoEntry.path != null) {
                    backupImageView.setOrientation(photoEntry.orientation, true);
                    if (photoEntry.isVideo) {
                        photoPickerPhotoCell.videoInfoContainer.setVisibility(0);
                        photoPickerPhotoCell.videoTextView.setText(AndroidUtilities.formatShortDuration(photoEntry.duration));
                        backupImageView.setImage("vthumb://" + photoEntry.imageId + ":" + photoEntry.path, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                    } else {
                        backupImageView.setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, null, this.mContext.getResources().getDrawable(R.drawable.nophotos));
                    }
                } else {
                    backupImageView.setImageResource(R.drawable.nophotos);
                }
                photoPickerPhotoCell.setChecked(-1, true, false);
                photoPickerPhotoCell.checkBox.setVisibility(0);
            } else if (!(obj instanceof MediaController.SearchImage)) {
            } else {
                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                photoPickerPhotoCell.setTag(searchImage);
                photoPickerPhotoCell.setImage(searchImage);
                photoPickerPhotoCell.videoInfoContainer.setVisibility(4);
                photoPickerPhotoCell.setChecked(-1, true, false);
                photoPickerPhotoCell.checkBox.setVisibility(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class FirstFrameView extends ImageView {
        private VideoPlayer currentVideoPlayer;
        private ValueAnimator fadeAnimator;
        private final TimeInterpolator fadeInterpolator;
        private boolean gettingFrame;
        private int gettingFrameIndex;
        private boolean gotError;
        private boolean hasFrame;

        public FirstFrameView(Context context) {
            super(context);
            this.gettingFrameIndex = 0;
            this.gettingFrame = false;
            this.hasFrame = false;
            this.gotError = false;
            this.fadeInterpolator = CubicBezierInterpolator.EASE_IN;
            setAlpha(0.0f);
        }

        public void clear() {
            this.hasFrame = false;
            this.gotError = false;
            if (this.gettingFrame) {
                this.gettingFrameIndex++;
                this.gettingFrame = false;
            }
            setImageResource(17170445);
        }

        public void checkFromPlayer(VideoPlayer videoPlayer) {
            if (this.currentVideoPlayer != videoPlayer) {
                this.gotError = false;
                clear();
            }
            if (videoPlayer != null) {
                long duration = videoPlayer.getDuration() - videoPlayer.getCurrentPosition();
                if (!this.hasFrame && !this.gotError && !this.gettingFrame && ((float) duration) < 5250.0f) {
                    final Uri currentUri = videoPlayer.getCurrentUri();
                    final int i = this.gettingFrameIndex + 1;
                    this.gettingFrameIndex = i;
                    Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PhotoViewer$FirstFrameView$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            PhotoViewer.FirstFrameView.this.lambda$checkFromPlayer$2(currentUri, i);
                        }
                    });
                    this.gettingFrame = true;
                }
            }
            this.currentVideoPlayer = videoPlayer;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$checkFromPlayer$2(Uri uri, final int i) {
            try {
                File file = new File(uri.getPath());
                int i2 = UserConfig.selectedAccount;
                Point point = AndroidUtilities.displaySize;
                AnimatedFileDrawable animatedFileDrawable = new AnimatedFileDrawable(file, true, 0L, null, null, null, 0L, i2, false, point.x, point.y, null);
                final Bitmap frameAtTime = animatedFileDrawable.getFrameAtTime(0L);
                animatedFileDrawable.recycle();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$FirstFrameView$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.FirstFrameView.this.lambda$checkFromPlayer$0(i, frameAtTime);
                    }
                });
            } catch (Throwable th) {
                FileLog.e(th);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$FirstFrameView$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoViewer.FirstFrameView.this.lambda$checkFromPlayer$1();
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$checkFromPlayer$0(int i, Bitmap bitmap) {
            if (i == this.gettingFrameIndex) {
                setImageBitmap(bitmap);
                this.hasFrame = true;
                this.gettingFrame = false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$checkFromPlayer$1() {
            this.gotError = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateAlpha() {
            if (PhotoViewer.this.videoPlayer != null && PhotoViewer.this.videoPlayer.getDuration() != -9223372036854775807L) {
                long max = Math.max(0L, PhotoViewer.this.videoPlayer.getDuration() - PhotoViewer.this.videoPlayer.getCurrentPosition());
                float max2 = 1.0f - Math.max(Math.min(((float) max) / 250.0f, 1.0f), 0.0f);
                if (max2 > 0.0f) {
                    if (PhotoViewer.this.videoPlayer.isPlaying()) {
                        if (this.fadeAnimator != null) {
                            return;
                        }
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(max2, 1.0f);
                        this.fadeAnimator = ofFloat;
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$FirstFrameView$$ExternalSyntheticLambda0
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                PhotoViewer.FirstFrameView.this.lambda$updateAlpha$3(valueAnimator);
                            }
                        });
                        this.fadeAnimator.setDuration(max);
                        this.fadeAnimator.setInterpolator(this.fadeInterpolator);
                        this.fadeAnimator.start();
                        setAlpha(max2);
                        return;
                    }
                    ValueAnimator valueAnimator = this.fadeAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.cancel();
                        this.fadeAnimator = null;
                    }
                    setAlpha(max2);
                    return;
                }
                ValueAnimator valueAnimator2 = this.fadeAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                    this.fadeAnimator = null;
                }
                setAlpha(0.0f);
                return;
            }
            ValueAnimator valueAnimator3 = this.fadeAnimator;
            if (valueAnimator3 != null) {
                valueAnimator3.cancel();
                this.fadeAnimator = null;
            }
            setAlpha(0.0f);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updateAlpha$3(ValueAnimator valueAnimator) {
            setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
