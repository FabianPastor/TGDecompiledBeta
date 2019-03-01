package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.MetricAffectingSpan;
import android.text.style.URLSpan;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
import android.view.DisplayCutout;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.File;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessagePosition;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.DefaultItemAnimator;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.telegram.messenger.support.widget.GridLayoutManagerFixed;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PageBlock;
import org.telegram.tgnet.TLRPC.PageListItem;
import org.telegram.tgnet.TLRPC.PageListOrderedItem;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.RichText;
import org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPage;
import org.telegram.tgnet.TLRPC.TL_page;
import org.telegram.tgnet.TLRPC.TL_pageBlockAnchor;
import org.telegram.tgnet.TLRPC.TL_pageBlockAudio;
import org.telegram.tgnet.TLRPC.TL_pageBlockAuthorDate;
import org.telegram.tgnet.TLRPC.TL_pageBlockBlockquote;
import org.telegram.tgnet.TLRPC.TL_pageBlockChannel;
import org.telegram.tgnet.TLRPC.TL_pageBlockCollage;
import org.telegram.tgnet.TLRPC.TL_pageBlockCover;
import org.telegram.tgnet.TLRPC.TL_pageBlockDetails;
import org.telegram.tgnet.TLRPC.TL_pageBlockDivider;
import org.telegram.tgnet.TLRPC.TL_pageBlockEmbed;
import org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost;
import org.telegram.tgnet.TLRPC.TL_pageBlockFooter;
import org.telegram.tgnet.TLRPC.TL_pageBlockHeader;
import org.telegram.tgnet.TLRPC.TL_pageBlockKicker;
import org.telegram.tgnet.TLRPC.TL_pageBlockList;
import org.telegram.tgnet.TLRPC.TL_pageBlockMap;
import org.telegram.tgnet.TLRPC.TL_pageBlockOrderedList;
import org.telegram.tgnet.TLRPC.TL_pageBlockParagraph;
import org.telegram.tgnet.TLRPC.TL_pageBlockPhoto;
import org.telegram.tgnet.TLRPC.TL_pageBlockPreformatted;
import org.telegram.tgnet.TLRPC.TL_pageBlockPullquote;
import org.telegram.tgnet.TLRPC.TL_pageBlockRelatedArticles;
import org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow;
import org.telegram.tgnet.TLRPC.TL_pageBlockSubheader;
import org.telegram.tgnet.TLRPC.TL_pageBlockSubtitle;
import org.telegram.tgnet.TLRPC.TL_pageBlockTable;
import org.telegram.tgnet.TLRPC.TL_pageBlockTitle;
import org.telegram.tgnet.TLRPC.TL_pageBlockUnsupported;
import org.telegram.tgnet.TLRPC.TL_pageBlockVideo;
import org.telegram.tgnet.TLRPC.TL_pageListItemBlocks;
import org.telegram.tgnet.TLRPC.TL_pageListItemText;
import org.telegram.tgnet.TLRPC.TL_pageListOrderedItemBlocks;
import org.telegram.tgnet.TLRPC.TL_pageListOrderedItemText;
import org.telegram.tgnet.TLRPC.TL_pagePart_layer82;
import org.telegram.tgnet.TLRPC.TL_pageRelatedArticle;
import org.telegram.tgnet.TLRPC.TL_pageTableCell;
import org.telegram.tgnet.TLRPC.TL_pageTableRow;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_textAnchor;
import org.telegram.tgnet.TLRPC.TL_textBold;
import org.telegram.tgnet.TLRPC.TL_textConcat;
import org.telegram.tgnet.TLRPC.TL_textEmail;
import org.telegram.tgnet.TLRPC.TL_textEmpty;
import org.telegram.tgnet.TLRPC.TL_textFixed;
import org.telegram.tgnet.TLRPC.TL_textImage;
import org.telegram.tgnet.TLRPC.TL_textItalic;
import org.telegram.tgnet.TLRPC.TL_textMarked;
import org.telegram.tgnet.TLRPC.TL_textPhone;
import org.telegram.tgnet.TLRPC.TL_textPlain;
import org.telegram.tgnet.TLRPC.TL_textStrike;
import org.telegram.tgnet.TLRPC.TL_textSubscript;
import org.telegram.tgnet.TLRPC.TL_textSuperscript;
import org.telegram.tgnet.TLRPC.TL_textUnderline;
import org.telegram.tgnet.TLRPC.TL_textUrl;
import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC.TL_user;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnchorSpan;
import org.telegram.ui.Components.AnimatedArrowDrawable;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AnimationProperties.FloatProperty;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.GroupedPhotosListView;
import org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.Scroller;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.TableLayout;
import org.telegram.ui.Components.TableLayout.Child;
import org.telegram.ui.Components.TableLayout.TableLayoutDelegate;
import org.telegram.ui.Components.TextPaintImageReceiverSpan;
import org.telegram.ui.Components.TextPaintMarkSpan;
import org.telegram.ui.Components.TextPaintSpan;
import org.telegram.ui.Components.TextPaintUrlSpan;
import org.telegram.ui.Components.TextPaintWebpageUrlSpan;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;
import org.telegram.ui.Components.WebPlayerView;
import org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate;
import org.telegram.ui.PhotoViewer.LinkMovementMethodMy;

public class ArticleViewer implements OnDoubleTapListener, OnGestureListener, NotificationCenterDelegate {
    public static final Property<WindowView, Float> ARTICLE_VIEWER_INNER_TRANSLATION_X = new FloatProperty<WindowView>("innerTranslationX") {
        public void setValue(WindowView object, float value) {
            object.setInnerTranslationX(value);
        }

        public Float get(WindowView object) {
            return Float.valueOf(object.getInnerTranslationX());
        }
    };
    @SuppressLint({"StaticFieldLeak"})
    private static volatile ArticleViewer Instance = null;
    private static final int TEXT_FLAG_ITALIC = 2;
    private static final int TEXT_FLAG_MARKED = 64;
    private static final int TEXT_FLAG_MEDIUM = 1;
    private static final int TEXT_FLAG_MONO = 4;
    private static final int TEXT_FLAG_REGULAR = 0;
    private static final int TEXT_FLAG_STRIKE = 32;
    private static final int TEXT_FLAG_SUB = 128;
    private static final int TEXT_FLAG_SUP = 256;
    private static final int TEXT_FLAG_UNDERLINE = 16;
    private static final int TEXT_FLAG_URL = 8;
    private static final int TEXT_FLAG_WEBPAGE_URL = 512;
    private static TextPaint audioTimePaint = new TextPaint(1);
    private static SparseArray<TextPaint> authorTextPaints = new SparseArray();
    private static TextPaint channelNamePaint = null;
    private static Paint colorPaint = null;
    private static DecelerateInterpolator decelerateInterpolator = null;
    private static SparseArray<TextPaint> detailsTextPaints = new SparseArray();
    private static Paint dividerPaint = null;
    private static Paint dotsPaint = null;
    private static TextPaint embedPostAuthorPaint = null;
    private static SparseArray<TextPaint> embedPostCaptionTextPaints = new SparseArray();
    private static TextPaint embedPostDatePaint = null;
    private static SparseArray<TextPaint> embedPostTextPaints = new SparseArray();
    private static TextPaint errorTextPaint = null;
    private static SparseArray<TextPaint> footerTextPaints = new SparseArray();
    private static final int gallery_menu_openin = 3;
    private static final int gallery_menu_save = 1;
    private static final int gallery_menu_share = 2;
    private static SparseArray<TextPaint> headerTextPaints = new SparseArray();
    private static SparseArray<TextPaint> kickerTextPaints = new SparseArray();
    private static TextPaint listTextNumPaint;
    private static SparseArray<TextPaint> listTextPaints = new SparseArray();
    private static TextPaint listTextPointerPaint;
    private static SparseArray<TextPaint> mediaCaptionTextPaints = new SparseArray();
    private static SparseArray<TextPaint> mediaCreditTextPaints = new SparseArray();
    private static SparseArray<TextPaint> paragraphTextPaints = new SparseArray();
    private static Paint photoBackgroundPaint;
    private static SparseArray<TextPaint> photoCaptionTextPaints = new SparseArray();
    private static SparseArray<TextPaint> photoCreditTextPaints = new SparseArray();
    private static Paint preformattedBackgroundPaint;
    private static SparseArray<TextPaint> preformattedTextPaints = new SparseArray();
    private static Drawable[] progressDrawables;
    private static Paint progressPaint;
    private static Paint quoteLinePaint;
    private static SparseArray<TextPaint> quoteTextPaints = new SparseArray();
    private static TextPaint relatedArticleHeaderPaint;
    private static TextPaint relatedArticleTextPaint;
    private static SparseArray<TextPaint> relatedArticleTextPaints = new SparseArray();
    private static Paint selectorPaint;
    private static SparseArray<TextPaint> subheaderTextPaints = new SparseArray();
    private static SparseArray<TextPaint> subtitleTextPaints = new SparseArray();
    private static Paint tableHalfLinePaint;
    private static Paint tableHeaderPaint;
    private static Paint tableLinePaint;
    private static Paint tableStripPaint;
    private static SparseArray<TextPaint> tableTextPaints = new SparseArray();
    private static SparseArray<TextPaint> titleTextPaints = new SparseArray();
    private static Paint urlPaint;
    private static Paint webpageMarkPaint;
    private static Paint webpageUrlPaint;
    private ActionBar actionBar;
    private WebpageAdapter[] adapter;
    private int anchorsOffsetMeasuredWidth;
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    private ClippingImageView animatingImageView;
    private Runnable animationEndRunnable;
    private int animationInProgress;
    private long animationStartTime;
    private float animationValue;
    private float[][] animationValues = ((float[][]) Array.newInstance(Float.TYPE, new int[]{2, 10}));
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private boolean attachedToWindow;
    private ImageView backButton;
    private BackDrawable backDrawable;
    private Paint backgroundPaint;
    private Paint blackPaint = new Paint();
    private FrameLayout bottomLayout;
    private boolean canDragDown = true;
    private boolean canZoom = true;
    private TextView captionTextView;
    private TextView captionTextViewNext;
    private ImageReceiver centerImage = new ImageReceiver();
    private boolean changingPage;
    private TL_pageBlockChannel channelBlock;
    private boolean checkingForLongPress = false;
    private boolean collapsed;
    private ColorCell[] colorCells = new ColorCell[3];
    private FrameLayout containerView;
    private int[] coords = new int[2];
    private Drawable copyBackgroundDrawable;
    private ArrayList<BlockEmbedCell> createdWebViews = new ArrayList();
    private int currentAccount;
    private AnimatorSet currentActionBarAnimation;
    private AnimatedFileDrawable currentAnimation;
    private String[] currentFileNames = new String[3];
    private int currentHeaderHeight;
    private int currentIndex;
    private PageBlock currentMedia;
    private WebPage currentPage;
    private PlaceProviderObject currentPlaceObject;
    private WebPlayerView currentPlayingVideo;
    private int currentRotation;
    private BitmapHolder currentThumb;
    private View customView;
    private CustomViewCallback customViewCallback;
    private TextView deleteView;
    private boolean disableShowCheck;
    private boolean discardTap;
    private boolean dontResetZoomOnFirstLayout;
    private boolean doubleTap;
    private float dragY;
    private boolean draggingDown;
    private boolean drawBlockSelection;
    private FontCell[] fontCells = new FontCell[2];
    private final int fontSizeCount = 5;
    private AspectRatioFrameLayout fullscreenAspectRatioView;
    private TextureView fullscreenTextureView;
    private FrameLayout fullscreenVideoContainer;
    private WebPlayerView fullscreenedVideo;
    private GestureDetector gestureDetector;
    private GroupedPhotosListView groupedPhotosListView;
    private Paint headerPaint = new Paint();
    private Paint headerProgressPaint = new Paint();
    private FrameLayout headerView;
    private PlaceProviderObject hideAfterAnimation;
    private AnimatorSet imageMoveAnimation;
    private ArrayList<PageBlock> imagesArr = new ArrayList();
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private boolean invalidCoords;
    private boolean isActionBarVisible = true;
    private boolean isPhotoVisible;
    private boolean isPlaying;
    private boolean isRtl;
    private boolean isVisible;
    private int lastBlockNum = 1;
    private Object lastInsets;
    private int lastReqId;
    private Drawable layerShadowDrawable;
    private LinearLayoutManager[] layoutManager;
    private ImageReceiver leftImage = new ImageReceiver();
    private Runnable lineProgressTickRunnable;
    private LineProgressView lineProgressView;
    private BottomSheet linkSheet;
    private RecyclerListView[] listView;
    private Chat loadedChannel;
    private boolean loadingChannel;
    private float maxX;
    private float maxY;
    private ActionBarMenuItem menuItem;
    private float minX;
    private float minY;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
    private boolean nightModeEnabled;
    private FrameLayout nightModeHintView;
    private ImageView nightModeImageView;
    private int openUrlReqId;
    private AnimatorSet pageSwitchAnimation;
    private ArrayList<WebPage> pagesStack = new ArrayList();
    private Activity parentActivity;
    private BaseFragment parentFragment;
    private CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    private Runnable photoAnimationEndRunnable;
    private int photoAnimationInProgress;
    private PhotoBackgroundDrawable photoBackgroundDrawable = new PhotoBackgroundDrawable(-16777216);
    private View photoContainerBackground;
    private FrameLayoutDrawer photoContainerView;
    private long photoTransitionAnimationStartTime;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartDistance;
    private float pinchStartScale = 1.0f;
    private float pinchStartX;
    private float pinchStartY;
    private ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    private ActionBarPopupWindow popupWindow;
    private int pressCount = 0;
    private int pressedLayoutY;
    private TextPaintUrlSpan pressedLink;
    private DrawingText pressedLinkOwnerLayout;
    private View pressedLinkOwnerView;
    private int previewsReqId;
    private ContextProgressView progressView;
    private AnimatorSet progressViewAnimation;
    private RadialProgressView[] radialProgressViews = new RadialProgressView[3];
    private ImageReceiver rightImage = new ImageReceiver();
    private float scale = 1.0f;
    private Paint scrimPaint;
    private Scroller scroller;
    private int selectedColor = 0;
    private int selectedFont = 0;
    private int selectedFontSize = 2;
    private ActionBarMenuItem settingsButton;
    private ImageView shareButton;
    private FrameLayout shareContainer;
    private PlaceProviderObject showAfterAnimation;
    private Drawable slideDotBigDrawable;
    private Drawable slideDotDrawable;
    private int switchImageAfterAnimation;
    private boolean textureUploaded;
    private SimpleTextView titleTextView;
    private long transitionAnimationStartTime;
    private float translationX;
    private float translationY;
    private Runnable updateProgressRunnable = new Runnable() {
        public void run() {
            if (!(ArticleViewer.this.videoPlayer == null || ArticleViewer.this.videoPlayerSeekbar == null || ArticleViewer.this.videoPlayerSeekbar.isDragging())) {
                ArticleViewer.this.videoPlayerSeekbar.setProgress(((float) ArticleViewer.this.videoPlayer.getCurrentPosition()) / ((float) ArticleViewer.this.videoPlayer.getDuration()));
                ArticleViewer.this.videoPlayerControlFrameLayout.invalidate();
                ArticleViewer.this.updateVideoPlayerTime();
            }
            if (ArticleViewer.this.isPlaying) {
                AndroidUtilities.runOnUIThread(ArticleViewer.this.updateProgressRunnable, 100);
            }
        }
    };
    private LinkPath urlPath = new LinkPath();
    private VelocityTracker velocityTracker;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    private ImageView videoPlayButton;
    private VideoPlayer videoPlayer;
    private FrameLayout videoPlayerControlFrameLayout;
    private SeekBar videoPlayerSeekbar;
    private TextView videoPlayerTime;
    private TextureView videoTextureView;
    private Dialog visibleDialog;
    private boolean wasLayout;
    private LayoutParams windowLayoutParams;
    private WindowView windowView;
    private boolean zoomAnimation;
    private boolean zooming;

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
        }

        public boolean onTouchEvent(MotionEvent event) {
            ArticleViewer.this.processTouchEvent(event);
            return true;
        }

        protected void onDraw(Canvas canvas) {
            ArticleViewer.this.drawContent(canvas);
        }

        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            return child != ArticleViewer.this.aspectRatioFrameLayout && super.drawChild(canvas, child, drawingTime);
        }
    }

    private class BlockAudioCell extends View implements FileDownloadProgressListener {
        private int TAG;
        private int buttonPressed;
        private int buttonState;
        private int buttonX;
        private int buttonY;
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockAudio currentBlock;
        private Document currentDocument;
        private MessageObject currentMessageObject;
        private StaticLayout durationLayout;
        private boolean isFirst;
        private boolean isLast;
        private String lastTimeString;
        private WebpageAdapter parentAdapter;
        private RadialProgress radialProgress;
        private SeekBar seekBar;
        private int seekBarX;
        private int seekBarY;
        private int textX;
        private int textY = AndroidUtilities.dp(54.0f);
        private StaticLayout titleLayout;

        public BlockAudioCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            this.radialProgress = new RadialProgress(this);
            this.radialProgress.setAlphaForPrevious(true);
            this.radialProgress.setDiff(AndroidUtilities.dp(0.0f));
            this.radialProgress.setStrokeWidth(AndroidUtilities.dp(2.0f));
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            this.seekBar = new SeekBar(context);
            this.seekBar.setDelegate(new ArticleViewer$BlockAudioCell$$Lambda$0(this));
        }

        final /* synthetic */ void lambda$new$0$ArticleViewer$BlockAudioCell(float progress) {
            if (this.currentMessageObject != null) {
                this.currentMessageObject.audioProgress = progress;
                MediaController.getInstance().seekToProgress(this.currentMessageObject, progress);
            }
        }

        public void setBlock(TL_pageBlockAudio block, boolean first, boolean last) {
            this.currentBlock = block;
            this.currentMessageObject = (MessageObject) this.parentAdapter.audioBlocks.get(this.currentBlock);
            this.currentDocument = this.currentMessageObject.getDocument();
            this.isFirst = first;
            this.isLast = last;
            this.radialProgress.setProgressColor(ArticleViewer.this.getTextColor());
            this.seekBar.setColors(ArticleViewer.this.getTextColor() & NUM, ArticleViewer.this.getTextColor() & NUM, ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor());
            updateButtonState(false);
            requestLayout();
        }

        public MessageObject getMessageObject() {
            return this.currentMessageObject;
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            if (this.seekBar.onTouch(event.getAction(), event.getX() - ((float) this.seekBarX), event.getY() - ((float) this.seekBarY))) {
                if (event.getAction() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                invalidate();
                return true;
            }
            boolean z;
            if (event.getAction() == 0) {
                if ((this.buttonState != -1 && x >= ((float) this.buttonX) && x <= ((float) (this.buttonX + AndroidUtilities.dp(48.0f))) && y >= ((float) this.buttonY) && y <= ((float) (this.buttonY + AndroidUtilities.dp(48.0f)))) || this.buttonState == 0) {
                    this.buttonPressed = 1;
                    invalidate();
                }
            } else if (event.getAction() == 1) {
                if (this.buttonPressed == 1) {
                    this.buttonPressed = 0;
                    playSoundEffect(0);
                    didPressedButton(false);
                    this.radialProgress.swapBackground(getDrawableForCurrentState());
                    invalidate();
                }
            } else if (event.getAction() == 3) {
                this.buttonPressed = 0;
            }
            if (this.buttonPressed == 0) {
                if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.captionLayout, this.textX, this.textY)) {
                    if (!(ArticleViewer.this.checkLayoutForLinks(event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event))) {
                        z = false;
                        return z;
                    }
                }
            }
            z = true;
            return z;
        }

        @SuppressLint({"DrawAllocation", "NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = AndroidUtilities.dp(54.0f);
            if (this.currentBlock != null) {
                if (this.currentBlock.level > 0) {
                    this.textX = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = AndroidUtilities.dp(18.0f);
                }
                int textWidth = (width - this.textX) - AndroidUtilities.dp(18.0f);
                int size = AndroidUtilities.dp(40.0f);
                this.buttonX = AndroidUtilities.dp(16.0f);
                this.buttonY = AndroidUtilities.dp(7.0f);
                this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + size, this.buttonY + size);
                this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock, this.parentAdapter);
                if (this.captionLayout != null) {
                    this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    height += this.creditOffset + AndroidUtilities.dp(4.0f);
                }
                this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.creditLayout != null) {
                    height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                if (!this.isFirst && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                String author = this.currentMessageObject.getMusicAuthor(false);
                String title = this.currentMessageObject.getMusicTitle(false);
                this.seekBarX = (this.buttonX + AndroidUtilities.dp(50.0f)) + size;
                int w = (width - this.seekBarX) - AndroidUtilities.dp(18.0f);
                if (TextUtils.isEmpty(title) && TextUtils.isEmpty(author)) {
                    this.titleLayout = null;
                    this.seekBarY = this.buttonY + ((size - AndroidUtilities.dp(30.0f)) / 2);
                } else {
                    SpannableStringBuilder stringBuilder;
                    SpannableStringBuilder spannableStringBuilder;
                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(author)) {
                        spannableStringBuilder = new SpannableStringBuilder(String.format("%s - %s", new Object[]{author, title}));
                    } else if (TextUtils.isEmpty(title)) {
                        spannableStringBuilder = new SpannableStringBuilder(author);
                    } else {
                        stringBuilder = new SpannableStringBuilder(title);
                    }
                    if (!TextUtils.isEmpty(author)) {
                        stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, author.length(), 18);
                    }
                    this.titleLayout = new StaticLayout(TextUtils.ellipsize(stringBuilder, Theme.chat_audioTitlePaint, (float) w, TruncateAt.END), ArticleViewer.audioTimePaint, w, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.seekBarY = (this.buttonY + ((size - AndroidUtilities.dp(30.0f)) / 2)) + AndroidUtilities.dp(11.0f);
                }
                this.seekBar.setSize(w, AndroidUtilities.dp(30.0f));
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            updatePlayingMessageProgress();
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                this.radialProgress.draw(canvas);
                canvas.save();
                canvas.translate((float) this.seekBarX, (float) this.seekBarY);
                this.seekBar.draw(canvas);
                canvas.restore();
                if (this.durationLayout != null) {
                    canvas.save();
                    canvas.translate((float) (this.buttonX + AndroidUtilities.dp(54.0f)), (float) (this.seekBarY + AndroidUtilities.dp(6.0f)));
                    this.durationLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.titleLayout != null) {
                    canvas.save();
                    canvas.translate((float) (this.buttonX + AndroidUtilities.dp(54.0f)), (float) (this.seekBarY - AndroidUtilities.dp(16.0f)));
                    this.titleLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        private Drawable getDrawableForCurrentState() {
            return Theme.chat_ivStatesDrawable[this.buttonState][this.buttonPressed != 0 ? 1 : 0];
        }

        public void updatePlayingMessageProgress() {
            if (this.currentDocument != null && this.currentMessageObject != null) {
                if (!this.seekBar.isDragging()) {
                    this.seekBar.setProgress(this.currentMessageObject.audioProgress);
                }
                int duration = 0;
                if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    duration = this.currentMessageObject.audioProgressSec;
                } else {
                    for (int a = 0; a < this.currentDocument.attributes.size(); a++) {
                        DocumentAttribute attribute = (DocumentAttribute) this.currentDocument.attributes.get(a);
                        if (attribute instanceof TL_documentAttributeAudio) {
                            duration = attribute.duration;
                            break;
                        }
                    }
                }
                String timeString = String.format("%d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)});
                if (this.lastTimeString == null || !(this.lastTimeString == null || this.lastTimeString.equals(timeString))) {
                    this.lastTimeString = timeString;
                    ArticleViewer.audioTimePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
                    this.durationLayout = new StaticLayout(timeString, ArticleViewer.audioTimePaint, (int) Math.ceil((double) ArticleViewer.audioTimePaint.measureText(timeString)), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
                ArticleViewer.audioTimePaint.setColor(ArticleViewer.this.getTextColor());
                invalidate();
            }
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentDocument);
            boolean fileExists = FileLoader.getPathToAttach(this.currentDocument, true).exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setBackground(null, false, false);
                return;
            }
            if (fileExists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(fileName, null, this);
                if (FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 3;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress != null) {
                        this.radialProgress.setProgress(progress.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                    this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                } else {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, animated);
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                }
            }
            updatePlayingMessageProgress();
        }

        private void didPressedButton(boolean animated) {
            if (this.buttonState == 0) {
                if (MediaController.getInstance().setPlaylist(this.parentAdapter.audioMessages, this.currentMessageObject, false)) {
                    this.buttonState = 1;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                    invalidate();
                }
            } else if (this.buttonState == 1) {
                if (MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.currentMessageObject)) {
                    this.buttonState = 0;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                    invalidate();
                }
            } else if (this.buttonState == 2) {
                this.radialProgress.setProgress(0.0f, false);
                FileLoader.getInstance(ArticleViewer.this.currentAccount).loadFile(this.currentDocument, ArticleViewer.this.currentPage, 1, 1);
                this.buttonState = 3;
                this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
                invalidate();
            } else if (this.buttonState == 3) {
                FileLoader.getInstance(ArticleViewer.this.currentAccount).cancelLoadFile(this.currentDocument);
                this.buttonState = 2;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateButtonState(false);
        }

        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(true);
        }

        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
        }

        public void onProgressDownload(String fileName, float progress) {
            this.radialProgress.setProgress(progress, true);
            if (this.buttonState != 3) {
                updateButtonState(false);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    private class BlockAuthorDateCell extends View {
        private TL_pageBlockAuthorDate currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX;
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockAuthorDateCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TL_pageBlockAuthorDate block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                Spannable spannableAuthor;
                MetricAffectingSpan[] spans;
                CharSequence text;
                CharSequence author = ArticleViewer.this.getText(this, this.currentBlock.author, this.currentBlock.author, this.currentBlock, width);
                if (author instanceof Spannable) {
                    spannableAuthor = (Spannable) author;
                    spans = (MetricAffectingSpan[]) spannableAuthor.getSpans(0, author.length(), MetricAffectingSpan.class);
                } else {
                    spannableAuthor = null;
                    spans = null;
                }
                if (this.currentBlock.published_date != 0 && !TextUtils.isEmpty(author)) {
                    text = LocaleController.formatString("ArticleDateByAuthor", R.string.ArticleDateByAuthor, LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000), author);
                } else if (TextUtils.isEmpty(author)) {
                    text = LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000);
                } else {
                    text = LocaleController.formatString("ArticleByAuthor", R.string.ArticleByAuthor, author);
                }
                if (spans != null) {
                    try {
                        if (spans.length > 0) {
                            int idx = TextUtils.indexOf(text, author);
                            if (idx != -1) {
                                Spannable spannable = Factory.getInstance().newSpannable(text);
                                text = spannable;
                                for (int a = 0; a < spans.length; a++) {
                                    Spannable spannable2 = spannable;
                                    spannable2.setSpan(spans[a], spannableAuthor.getSpanStart(spans[a]) + idx, spannableAuthor.getSpanEnd(spans[a]) + idx, 33);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                this.textLayout = ArticleViewer.this.createLayoutForText(this, text, null, width - AndroidUtilities.dp(36.0f), this.currentBlock, this.parentAdapter);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                    if (ArticleViewer.this.isRtl) {
                        this.textX = (int) Math.floor((double) ((((float) width) - this.textLayout.getLineWidth(0)) - ((float) AndroidUtilities.dp(16.0f))));
                    } else {
                        this.textX = AndroidUtilities.dp(18.0f);
                    }
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockBlockquoteCell extends View {
        private TL_pageBlockBlockquote currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textX;
        private int textY = AndroidUtilities.dp(8.0f);
        private int textY2;

        public BlockBlockquoteCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TL_pageBlockBlockquote block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY)) {
                if (!(ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(event))) {
                    return false;
                }
            }
            return true;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                int textWidth = width - AndroidUtilities.dp(50.0f);
                if (this.currentBlock.level > 0) {
                    textWidth -= AndroidUtilities.dp((float) (this.currentBlock.level * 14));
                }
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, textWidth, this.currentBlock, this.parentAdapter);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(8.0f) + this.textLayout.getHeight());
                }
                if (this.currentBlock.level > 0) {
                    if (ArticleViewer.this.isRtl) {
                        this.textX = AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 14));
                    } else {
                        this.textX = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(32.0f);
                    }
                } else if (ArticleViewer.this.isRtl) {
                    this.textX = AndroidUtilities.dp(14.0f);
                } else {
                    this.textX = AndroidUtilities.dp(32.0f);
                }
                this.textLayout2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption, textWidth, this.currentBlock, this.parentAdapter);
                if (this.textLayout2 != null) {
                    this.textY2 = AndroidUtilities.dp(8.0f) + height;
                    height += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                }
                if (height != 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout2 != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY2);
                    this.textLayout2.draw(canvas);
                    canvas.restore();
                }
                if (ArticleViewer.this.isRtl) {
                    int x = getMeasuredWidth() - AndroidUtilities.dp(20.0f);
                    canvas.drawRect((float) x, (float) AndroidUtilities.dp(6.0f), (float) (AndroidUtilities.dp(2.0f) + x), (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), ArticleViewer.quoteLinePaint);
                } else {
                    canvas.drawRect((float) AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 18)), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 20)), (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), ArticleViewer.quoteLinePaint);
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockChannelCell extends FrameLayout {
        private Paint backgroundPaint;
        private int buttonWidth;
        private AnimatorSet currentAnimation;
        private TL_pageBlockChannel currentBlock;
        private int currentState;
        private int currentType;
        private ImageView imageView;
        private WebpageAdapter parentAdapter;
        private ContextProgressView progressView;
        private DrawingText textLayout;
        private TextView textView;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textX2;
        private int textY = AndroidUtilities.dp(11.0f);

        public BlockChannelCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            this.backgroundPaint = new Paint();
            this.currentType = type;
            this.textView = new TextView(context);
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setText(LocaleController.getString("ChannelJoin", R.string.ChannelJoin));
            this.textView.setGravity(19);
            addView(this.textView, LayoutHelper.createFrame(-2, 39, 53));
            this.textView.setOnClickListener(new ArticleViewer$BlockChannelCell$$Lambda$0(this));
            this.imageView = new ImageView(context);
            this.imageView.setImageResource(R.drawable.list_check);
            this.imageView.setScaleType(ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(39, 39, 53));
            this.progressView = new ContextProgressView(context, 0);
            addView(this.progressView, LayoutHelper.createFrame(39, 39, 53));
        }

        final /* synthetic */ void lambda$new$0$ArticleViewer$BlockChannelCell(View v) {
            if (this.currentState == 0) {
                setState(1, true);
                ArticleViewer.this.joinChannel(this, ArticleViewer.this.loadedChannel);
            }
        }

        public void setBlock(TL_pageBlockChannel block) {
            this.currentBlock = block;
            int color = ArticleViewer.this.getSelectedColor();
            if (this.currentType == 0) {
                this.textView.setTextColor(-14840360);
                if (color == 0) {
                    this.backgroundPaint.setColor(-526345);
                } else if (color == 1) {
                    this.backgroundPaint.setColor(-1712440);
                } else if (color == 2) {
                    this.backgroundPaint.setColor(-15000805);
                }
                this.imageView.setColorFilter(new PorterDuffColorFilter(-6710887, Mode.MULTIPLY));
            } else {
                this.textView.setTextColor(-1);
                this.backgroundPaint.setColor(NUM);
                this.imageView.setColorFilter(new PorterDuffColorFilter(-1, Mode.MULTIPLY));
            }
            Chat channel = MessagesController.getInstance(ArticleViewer.this.currentAccount).getChat(Integer.valueOf(block.channel.id));
            if (channel == null || channel.min) {
                ArticleViewer.this.loadChannel(this, this.parentAdapter, block.channel);
                setState(1, false);
            } else {
                ArticleViewer.this.loadedChannel = channel;
                if (!channel.left || channel.kicked) {
                    setState(4, false);
                } else {
                    setState(0, false);
                }
            }
            requestLayout();
        }

        public void setState(int state, boolean animated) {
            float f = 1.0f;
            if (this.currentAnimation != null) {
                this.currentAnimation.cancel();
            }
            this.currentState = state;
            float f2;
            if (animated) {
                this.currentAnimation = new AnimatorSet();
                AnimatorSet animatorSet = this.currentAnimation;
                Animator[] animatorArr = new Animator[9];
                TextView textView = this.textView;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = state == 0 ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(textView, property, fArr);
                textView = this.textView;
                property = View.SCALE_X;
                fArr = new float[1];
                if (state == 0) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.1f;
                }
                fArr[0] = f2;
                animatorArr[1] = ObjectAnimator.ofFloat(textView, property, fArr);
                textView = this.textView;
                property = View.SCALE_Y;
                fArr = new float[1];
                if (state == 0) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.1f;
                }
                fArr[0] = f2;
                animatorArr[2] = ObjectAnimator.ofFloat(textView, property, fArr);
                ContextProgressView contextProgressView = this.progressView;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = state == 1 ? 1.0f : 0.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(contextProgressView, property2, fArr2);
                contextProgressView = this.progressView;
                property2 = View.SCALE_X;
                fArr2 = new float[1];
                if (state == 1) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.1f;
                }
                fArr2[0] = f2;
                animatorArr[4] = ObjectAnimator.ofFloat(contextProgressView, property2, fArr2);
                contextProgressView = this.progressView;
                property2 = View.SCALE_Y;
                fArr2 = new float[1];
                if (state == 1) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.1f;
                }
                fArr2[0] = f2;
                animatorArr[5] = ObjectAnimator.ofFloat(contextProgressView, property2, fArr2);
                ImageView imageView = this.imageView;
                property2 = View.ALPHA;
                fArr2 = new float[1];
                fArr2[0] = state == 2 ? 1.0f : 0.0f;
                animatorArr[6] = ObjectAnimator.ofFloat(imageView, property2, fArr2);
                imageView = this.imageView;
                property2 = View.SCALE_X;
                fArr2 = new float[1];
                if (state == 2) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.1f;
                }
                fArr2[0] = f2;
                animatorArr[7] = ObjectAnimator.ofFloat(imageView, property2, fArr2);
                ImageView imageView2 = this.imageView;
                property = View.SCALE_Y;
                fArr = new float[1];
                if (state != 2) {
                    f = 0.1f;
                }
                fArr[0] = f;
                animatorArr[8] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                animatorSet.playTogether(animatorArr);
                this.currentAnimation.setDuration(150);
                this.currentAnimation.start();
                return;
            }
            this.textView.setAlpha(state == 0 ? 1.0f : 0.0f);
            TextView textView2 = this.textView;
            if (state == 0) {
                f2 = 1.0f;
            } else {
                f2 = 0.1f;
            }
            textView2.setScaleX(f2);
            textView2 = this.textView;
            if (state == 0) {
                f2 = 1.0f;
            } else {
                f2 = 0.1f;
            }
            textView2.setScaleY(f2);
            this.progressView.setAlpha(state == 1 ? 1.0f : 0.0f);
            ContextProgressView contextProgressView2 = this.progressView;
            if (state == 1) {
                f2 = 1.0f;
            } else {
                f2 = 0.1f;
            }
            contextProgressView2.setScaleX(f2);
            contextProgressView2 = this.progressView;
            if (state == 1) {
                f2 = 1.0f;
            } else {
                f2 = 0.1f;
            }
            contextProgressView2.setScaleY(f2);
            this.imageView.setAlpha(state == 2 ? 1.0f : 0.0f);
            ImageView imageView3 = this.imageView;
            if (state == 2) {
                f2 = 1.0f;
            } else {
                f2 = 0.1f;
            }
            imageView3.setScaleX(f2);
            ImageView imageView4 = this.imageView;
            if (state != 2) {
                f = 0.1f;
            }
            imageView4.setScaleY(f);
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (this.currentType != 0) {
                return super.onTouchEvent(event);
            }
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width, AndroidUtilities.dp(48.0f));
            this.textView.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            this.buttonWidth = this.textView.getMeasuredWidth();
            this.progressView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            this.imageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            if (this.currentBlock != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, this.currentBlock.channel.title, null, (width - AndroidUtilities.dp(52.0f)) - this.buttonWidth, this.currentBlock, Alignment.ALIGN_LEFT, this.parentAdapter);
                if (ArticleViewer.this.isRtl) {
                    this.textX2 = this.textX;
                } else {
                    this.textX2 = (getMeasuredWidth() - this.textX) - this.buttonWidth;
                }
            }
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.imageView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, (this.textX2 + (this.buttonWidth / 2)) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            this.progressView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, (this.textX2 + (this.buttonWidth / 2)) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            this.textView.layout(this.textX2, 0, this.textX2 + this.textView.getMeasuredWidth(), this.textView.getMeasuredHeight());
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentType == 0) {
            }
            if (this.currentBlock != null) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(39.0f), this.backgroundPaint);
                if (this.textLayout != null && this.textLayout.getLineCount() > 0) {
                    canvas.save();
                    if (ArticleViewer.this.isRtl) {
                        canvas.translate((((float) getMeasuredWidth()) - this.textLayout.getLineWidth(0)) - ((float) this.textX), (float) this.textY);
                    } else {
                        canvas.translate((float) this.textX, (float) this.textY);
                    }
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockCollageCell extends FrameLayout {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockCollage currentBlock;
        private GridLayoutManager gridLayoutManager;
        private GroupedMessages group = new GroupedMessages();
        private boolean inLayout;
        private Adapter innerAdapter;
        private RecyclerListView innerListView;
        private int listX;
        private WebpageAdapter parentAdapter;
        private int textX;
        private int textY;

        public class GroupedMessages {
            public long groupId;
            public boolean hasSibling;
            private int maxSizeWidth = 1000;
            public ArrayList<GroupedMessagePosition> posArray = new ArrayList();
            public HashMap<TLObject, GroupedMessagePosition> positions = new HashMap();

            private class MessageGroupedLayoutAttempt {
                public float[] heights;
                public int[] lineCounts;

                public MessageGroupedLayoutAttempt(int i1, int i2, float f1, float f2) {
                    this.lineCounts = new int[]{i1, i2};
                    this.heights = new float[]{f1, f2};
                }

                public MessageGroupedLayoutAttempt(int i1, int i2, int i3, float f1, float f2, float f3) {
                    this.lineCounts = new int[]{i1, i2, i3};
                    this.heights = new float[]{f1, f2, f3};
                }

                public MessageGroupedLayoutAttempt(int i1, int i2, int i3, int i4, float f1, float f2, float f3, float f4) {
                    this.lineCounts = new int[]{i1, i2, i3, i4};
                    this.heights = new float[]{f1, f2, f3, f4};
                }
            }

            private float multiHeight(float[] array, int start, int end) {
                float sum = 0.0f;
                for (int a = start; a < end; a++) {
                    sum += array[a];
                }
                return ((float) this.maxSizeWidth) / sum;
            }

            /* JADX WARNING: Removed duplicated region for block: B:29:0x00ed  */
            /* JADX WARNING: Removed duplicated region for block: B:13:0x0081  */
            /* JADX WARNING: Removed duplicated region for block: B:30:0x00ef  */
            /* JADX WARNING: Removed duplicated region for block: B:16:0x0088  */
            /* JADX WARNING: Removed duplicated region for block: B:31:0x00fb  */
            /* JADX WARNING: Removed duplicated region for block: B:19:0x0099  */
            /* JADX WARNING: Removed duplicated region for block: B:22:0x00b1  */
            public void calculate() {
                /*
                r129 = this;
                r0 = r129;
                r5 = r0.posArray;
                r5.clear();
                r0 = r129;
                r5 = r0.positions;
                r5.clear();
                r0 = r129;
                r5 = org.telegram.ui.ArticleViewer.BlockCollageCell.this;
                r5 = r5.currentBlock;
                r5 = r5.items;
                r95 = r5.size();
                r5 = 1;
                r0 = r95;
                if (r0 > r5) goto L_0x0022;
            L_0x0021:
                return;
            L_0x0022:
                r106 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
                r124 = new java.lang.StringBuilder;
                r124.<init>();
                r92 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r99 = 0;
                r5 = 0;
                r0 = r129;
                r0.hasSibling = r5;
                r89 = 0;
            L_0x0035:
                r0 = r89;
                r1 = r95;
                if (r0 >= r1) goto L_0x0118;
            L_0x003b:
                r0 = r129;
                r5 = org.telegram.ui.ArticleViewer.BlockCollageCell.this;
                r5 = r5.currentBlock;
                r5 = r5.items;
                r0 = r89;
                r110 = r5.get(r0);
                r110 = (org.telegram.tgnet.TLObject) r110;
                r0 = r110;
                r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockPhoto;
                if (r5 == 0) goto L_0x00c8;
            L_0x0053:
                r115 = r110;
                r115 = (org.telegram.tgnet.TLRPC.TL_pageBlockPhoto) r115;
                r0 = r129;
                r5 = org.telegram.ui.ArticleViewer.BlockCollageCell.this;
                r5 = org.telegram.ui.ArticleViewer.this;
                r0 = r115;
                r6 = r0.photo_id;
                r117 = r5.getPhotoWithId(r6);
                if (r117 != 0) goto L_0x006a;
            L_0x0067:
                r89 = r89 + 1;
                goto L_0x0035;
            L_0x006a:
                r0 = r117;
                r5 = r0.sizes;
                r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
                r118 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6);
            L_0x0076:
                r120 = new org.telegram.messenger.MessageObject$GroupedMessagePosition;
                r120.<init>();
                r5 = r95 + -1;
                r0 = r89;
                if (r0 != r5) goto L_0x00ed;
            L_0x0081:
                r5 = 1;
            L_0x0082:
                r0 = r120;
                r0.last = r5;
                if (r118 != 0) goto L_0x00ef;
            L_0x0088:
                r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            L_0x008a:
                r0 = r120;
                r0.aspectRatio = r5;
                r0 = r120;
                r5 = r0.aspectRatio;
                r6 = NUM; // 0x3var_a float:1.2 double:5.271833295E-315;
                r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1));
                if (r5 <= 0) goto L_0x00fb;
            L_0x0099:
                r5 = "w";
                r0 = r124;
                r0.append(r5);
            L_0x00a1:
                r0 = r120;
                r5 = r0.aspectRatio;
                r92 = r92 + r5;
                r0 = r120;
                r5 = r0.aspectRatio;
                r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1));
                if (r5 <= 0) goto L_0x00b3;
            L_0x00b1:
                r99 = 1;
            L_0x00b3:
                r0 = r129;
                r5 = r0.positions;
                r0 = r110;
                r1 = r120;
                r5.put(r0, r1);
                r0 = r129;
                r5 = r0.posArray;
                r0 = r120;
                r5.add(r0);
                goto L_0x0067;
            L_0x00c8:
                r0 = r110;
                r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockVideo;
                if (r5 == 0) goto L_0x0067;
            L_0x00ce:
                r116 = r110;
                r116 = (org.telegram.tgnet.TLRPC.TL_pageBlockVideo) r116;
                r0 = r129;
                r5 = org.telegram.ui.ArticleViewer.BlockCollageCell.this;
                r5 = org.telegram.ui.ArticleViewer.this;
                r0 = r116;
                r6 = r0.video_id;
                r98 = r5.getDocumentWithId(r6);
                if (r98 == 0) goto L_0x0067;
            L_0x00e2:
                r0 = r98;
                r5 = r0.thumbs;
                r6 = 90;
                r118 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6);
                goto L_0x0076;
            L_0x00ed:
                r5 = 0;
                goto L_0x0082;
            L_0x00ef:
                r0 = r118;
                r5 = r0.w;
                r5 = (float) r5;
                r0 = r118;
                r6 = r0.h;
                r6 = (float) r6;
                r5 = r5 / r6;
                goto L_0x008a;
            L_0x00fb:
                r0 = r120;
                r5 = r0.aspectRatio;
                r6 = NUM; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
                r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1));
                if (r5 >= 0) goto L_0x010f;
            L_0x0106:
                r5 = "n";
                r0 = r124;
                r0.append(r5);
                goto L_0x00a1;
            L_0x010f:
                r5 = "q";
                r0 = r124;
                r0.append(r5);
                goto L_0x00a1;
            L_0x0118:
                r5 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
                r107 = org.telegram.messenger.AndroidUtilities.dp(r5);
                r5 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
                r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                r5 = (float) r5;
                r6 = org.telegram.messenger.AndroidUtilities.displaySize;
                r6 = r6.x;
                r7 = org.telegram.messenger.AndroidUtilities.displaySize;
                r7 = r7.y;
                r6 = java.lang.Math.min(r6, r7);
                r6 = (float) r6;
                r0 = r129;
                r7 = r0.maxSizeWidth;
                r7 = (float) r7;
                r6 = r6 / r7;
                r5 = r5 / r6;
                r0 = (int) r5;
                r109 = r0;
                r5 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
                r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                r5 = (float) r5;
                r6 = org.telegram.messenger.AndroidUtilities.displaySize;
                r6 = r6.x;
                r7 = org.telegram.messenger.AndroidUtilities.displaySize;
                r7 = r7.y;
                r6 = java.lang.Math.min(r6, r7);
                r6 = (float) r6;
                r0 = r129;
                r7 = r0.maxSizeWidth;
                r7 = (float) r7;
                r6 = r6 / r7;
                r5 = r5 / r6;
                r0 = (int) r5;
                r114 = r0;
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r5 = (float) r5;
                r104 = r5 / r106;
                r0 = r95;
                r5 = (float) r0;
                r92 = r92 / r5;
                if (r99 != 0) goto L_0x0662;
            L_0x0168:
                r5 = 2;
                r0 = r95;
                if (r0 == r5) goto L_0x0177;
            L_0x016d:
                r5 = 3;
                r0 = r95;
                if (r0 == r5) goto L_0x0177;
            L_0x0172:
                r5 = 4;
                r0 = r95;
                if (r0 != r5) goto L_0x0662;
            L_0x0177:
                r5 = 2;
                r0 = r95;
                if (r0 != r5) goto L_0x02f5;
            L_0x017c:
                r0 = r129;
                r5 = r0.posArray;
                r6 = 0;
                r4 = r5.get(r6);
                r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4;
                r0 = r129;
                r5 = r0.posArray;
                r6 = 1;
                r121 = r5.get(r6);
                r121 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r121;
                r113 = r124.toString();
                r5 = "ww";
                r0 = r113;
                r5 = r0.equals(r5);
                if (r5 == 0) goto L_0x022a;
            L_0x01a1:
                r0 = r92;
                r6 = (double) r0;
                r12 = NUM; // 0x3ffNUM float:2.720083E23 double:1.4;
                r0 = r104;
                r14 = (double) r0;
                r12 = r12 * r14;
                r5 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1));
                if (r5 <= 0) goto L_0x022a;
            L_0x01b1:
                r5 = r4.aspectRatio;
                r0 = r121;
                r6 = r0.aspectRatio;
                r5 = r5 - r6;
                r6 = (double) r5;
                r12 = NUM; // 0x3fCLASSNAMEa float:-1.5881868E-23 double:0.2;
                r5 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1));
                if (r5 >= 0) goto L_0x022a;
            L_0x01c2:
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r5 = (float) r5;
                r6 = r4.aspectRatio;
                r5 = r5 / r6;
                r0 = r129;
                r6 = r0.maxSizeWidth;
                r6 = (float) r6;
                r0 = r121;
                r7 = r0.aspectRatio;
                r6 = r6 / r7;
                r7 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                r7 = r106 / r7;
                r6 = java.lang.Math.min(r6, r7);
                r5 = java.lang.Math.min(r5, r6);
                r5 = java.lang.Math.round(r5);
                r5 = (float) r5;
                r10 = r5 / r106;
                r5 = 0;
                r6 = 0;
                r7 = 0;
                r8 = 0;
                r0 = r129;
                r9 = r0.maxSizeWidth;
                r11 = 7;
                r4.set(r5, r6, r7, r8, r9, r10, r11);
                r12 = 0;
                r13 = 0;
                r14 = 1;
                r15 = 1;
                r0 = r129;
                r0 = r0.maxSizeWidth;
                r16 = r0;
                r18 = 11;
                r11 = r121;
                r17 = r10;
                r11.set(r12, r13, r14, r15, r16, r17, r18);
            L_0x0206:
                r89 = 0;
            L_0x0208:
                r0 = r89;
                r1 = r95;
                if (r0 >= r1) goto L_0x0021;
            L_0x020e:
                r0 = r129;
                r5 = r0.posArray;
                r0 = r89;
                r81 = r5.get(r0);
                r81 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r81;
                r0 = r81;
                r5 = r0.flags;
                r5 = r5 & 1;
                if (r5 == 0) goto L_0x0227;
            L_0x0222:
                r5 = 1;
                r0 = r81;
                r0.edge = r5;
            L_0x0227:
                r89 = r89 + 1;
                goto L_0x0208;
            L_0x022a:
                r5 = "ww";
                r0 = r113;
                r5 = r0.equals(r5);
                if (r5 != 0) goto L_0x0240;
            L_0x0235:
                r5 = "qq";
                r0 = r113;
                r5 = r0.equals(r5);
                if (r5 == 0) goto L_0x027a;
            L_0x0240:
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r9 = r5 / 2;
                r5 = (float) r9;
                r6 = r4.aspectRatio;
                r5 = r5 / r6;
                r6 = (float) r9;
                r0 = r121;
                r7 = r0.aspectRatio;
                r6 = r6 / r7;
                r0 = r106;
                r6 = java.lang.Math.min(r6, r0);
                r5 = java.lang.Math.min(r5, r6);
                r5 = java.lang.Math.round(r5);
                r5 = (float) r5;
                r10 = r5 / r106;
                r5 = 0;
                r6 = 0;
                r7 = 0;
                r8 = 0;
                r11 = 13;
                r4.set(r5, r6, r7, r8, r9, r10, r11);
                r12 = 1;
                r13 = 1;
                r14 = 0;
                r15 = 0;
                r18 = 14;
                r11 = r121;
                r16 = r9;
                r17 = r10;
                r11.set(r12, r13, r14, r15, r16, r17, r18);
                goto L_0x0206;
            L_0x027a:
                r5 = NUM; // 0x3ecccccd float:0.4 double:5.205520926E-315;
                r0 = r129;
                r6 = r0.maxSizeWidth;
                r6 = (float) r6;
                r5 = r5 * r6;
                r0 = r129;
                r6 = r0.maxSizeWidth;
                r6 = (float) r6;
                r7 = r4.aspectRatio;
                r6 = r6 / r7;
                r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r8 = r4.aspectRatio;
                r7 = r7 / r8;
                r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r0 = r121;
                r11 = r0.aspectRatio;
                r8 = r8 / r11;
                r7 = r7 + r8;
                r6 = r6 / r7;
                r6 = java.lang.Math.round(r6);
                r6 = (float) r6;
                r5 = java.lang.Math.max(r5, r6);
                r0 = (int) r5;
                r22 = r0;
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r16 = r5 - r22;
                r0 = r16;
                r1 = r109;
                if (r0 >= r1) goto L_0x02b7;
            L_0x02b1:
                r97 = r109 - r16;
                r16 = r109;
                r22 = r22 - r97;
            L_0x02b7:
                r0 = r16;
                r5 = (float) r0;
                r6 = r4.aspectRatio;
                r5 = r5 / r6;
                r0 = r22;
                r6 = (float) r0;
                r0 = r121;
                r7 = r0.aspectRatio;
                r6 = r6 / r7;
                r5 = java.lang.Math.min(r5, r6);
                r5 = java.lang.Math.round(r5);
                r5 = (float) r5;
                r0 = r106;
                r5 = java.lang.Math.min(r0, r5);
                r10 = r5 / r106;
                r12 = 0;
                r13 = 0;
                r14 = 0;
                r15 = 0;
                r18 = 13;
                r11 = r4;
                r17 = r10;
                r11.set(r12, r13, r14, r15, r16, r17, r18);
                r18 = 1;
                r19 = 1;
                r20 = 0;
                r21 = 0;
                r24 = 14;
                r17 = r121;
                r23 = r10;
                r17.set(r18, r19, r20, r21, r22, r23, r24);
                goto L_0x0206;
            L_0x02f5:
                r5 = 3;
                r0 = r95;
                if (r0 != r5) goto L_0x0460;
            L_0x02fa:
                r0 = r129;
                r5 = r0.posArray;
                r6 = 0;
                r4 = r5.get(r6);
                r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4;
                r0 = r129;
                r5 = r0.posArray;
                r6 = 1;
                r121 = r5.get(r6);
                r121 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r121;
                r0 = r129;
                r5 = r0.posArray;
                r6 = 2;
                r122 = r5.get(r6);
                r122 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r122;
                r5 = 0;
                r0 = r124;
                r5 = r0.charAt(r5);
                r6 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
                if (r5 != r6) goto L_0x03ec;
            L_0x0326:
                r5 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
                r5 = r5 * r106;
                r0 = r121;
                r6 = r0.aspectRatio;
                r0 = r129;
                r7 = r0.maxSizeWidth;
                r7 = (float) r7;
                r6 = r6 * r7;
                r0 = r122;
                r7 = r0.aspectRatio;
                r0 = r121;
                r8 = r0.aspectRatio;
                r7 = r7 + r8;
                r6 = r6 / r7;
                r6 = java.lang.Math.round(r6);
                r6 = (float) r6;
                r127 = java.lang.Math.min(r5, r6);
                r48 = r106 - r127;
                r0 = r109;
                r5 = (float) r0;
                r0 = r129;
                r6 = r0.maxSizeWidth;
                r6 = (float) r6;
                r7 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
                r6 = r6 * r7;
                r0 = r122;
                r7 = r0.aspectRatio;
                r7 = r7 * r127;
                r0 = r121;
                r8 = r0.aspectRatio;
                r8 = r8 * r48;
                r7 = java.lang.Math.min(r7, r8);
                r7 = java.lang.Math.round(r7);
                r7 = (float) r7;
                r6 = java.lang.Math.min(r6, r7);
                r5 = java.lang.Math.max(r5, r6);
                r0 = (int) r5;
                r34 = r0;
                r5 = r4.aspectRatio;
                r5 = r5 * r106;
                r0 = r114;
                r6 = (float) r0;
                r5 = r5 + r6;
                r0 = r129;
                r6 = r0.maxSizeWidth;
                r6 = r6 - r34;
                r6 = (float) r6;
                r5 = java.lang.Math.min(r5, r6);
                r28 = java.lang.Math.round(r5);
                r24 = 0;
                r25 = 0;
                r26 = 0;
                r27 = 1;
                r29 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r30 = 13;
                r23 = r4;
                r23.set(r24, r25, r26, r27, r28, r29, r30);
                r30 = 1;
                r31 = 1;
                r32 = 0;
                r33 = 0;
                r35 = r48 / r106;
                r36 = 6;
                r29 = r121;
                r29.set(r30, r31, r32, r33, r34, r35, r36);
                r30 = 0;
                r31 = 1;
                r32 = 1;
                r33 = 1;
                r35 = r127 / r106;
                r36 = 10;
                r29 = r122;
                r29.set(r30, r31, r32, r33, r34, r35, r36);
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r0 = r122;
                r0.spanSize = r5;
                r5 = 2;
                r5 = new float[r5];
                r6 = 0;
                r7 = r127 / r106;
                r5[r6] = r7;
                r6 = 1;
                r7 = r48 / r106;
                r5[r6] = r7;
                r4.siblingHeights = r5;
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r5 = r5 - r28;
                r0 = r121;
                r0.spanSize = r5;
                r0 = r28;
                r1 = r122;
                r1.leftSpanOffset = r0;
                r5 = 1;
                r0 = r129;
                r0.hasSibling = r5;
                goto L_0x0206;
            L_0x03ec:
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r5 = (float) r5;
                r6 = r4.aspectRatio;
                r5 = r5 / r6;
                r6 = NUM; // 0x3var_f5c3 float:0.66 double:5.235361493E-315;
                r6 = r6 * r106;
                r5 = java.lang.Math.min(r5, r6);
                r5 = java.lang.Math.round(r5);
                r5 = (float) r5;
                r41 = r5 / r106;
                r36 = 0;
                r37 = 1;
                r38 = 0;
                r39 = 0;
                r0 = r129;
                r0 = r0.maxSizeWidth;
                r40 = r0;
                r42 = 7;
                r35 = r4;
                r35.set(r36, r37, r38, r39, r40, r41, r42);
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r9 = r5 / 2;
                r5 = r106 - r41;
                r6 = (float) r9;
                r0 = r121;
                r7 = r0.aspectRatio;
                r6 = r6 / r7;
                r7 = (float) r9;
                r0 = r122;
                r8 = r0.aspectRatio;
                r7 = r7 / r8;
                r6 = java.lang.Math.min(r6, r7);
                r6 = java.lang.Math.round(r6);
                r6 = (float) r6;
                r5 = java.lang.Math.min(r5, r6);
                r48 = r5 / r106;
                r43 = 0;
                r44 = 0;
                r45 = 1;
                r46 = 1;
                r49 = 9;
                r42 = r121;
                r47 = r9;
                r42.set(r43, r44, r45, r46, r47, r48, r49);
                r43 = 1;
                r44 = 1;
                r45 = 1;
                r46 = 1;
                r49 = 10;
                r42 = r122;
                r47 = r9;
                r42.set(r43, r44, r45, r46, r47, r48, r49);
                goto L_0x0206;
            L_0x0460:
                r5 = 4;
                r0 = r95;
                if (r0 != r5) goto L_0x0206;
            L_0x0465:
                r0 = r129;
                r5 = r0.posArray;
                r6 = 0;
                r4 = r5.get(r6);
                r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4;
                r0 = r129;
                r5 = r0.posArray;
                r6 = 1;
                r121 = r5.get(r6);
                r121 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r121;
                r0 = r129;
                r5 = r0.posArray;
                r6 = 2;
                r122 = r5.get(r6);
                r122 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r122;
                r0 = r129;
                r5 = r0.posArray;
                r6 = 3;
                r123 = r5.get(r6);
                r123 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r123;
                r5 = 0;
                r0 = r124;
                r5 = r0.charAt(r5);
                r6 = 119; // 0x77 float:1.67E-43 double:5.9E-322;
                if (r5 != r6) goto L_0x0563;
            L_0x049c:
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r5 = (float) r5;
                r6 = r4.aspectRatio;
                r5 = r5 / r6;
                r6 = NUM; // 0x3var_f5c3 float:0.66 double:5.235361493E-315;
                r6 = r6 * r106;
                r5 = java.lang.Math.min(r5, r6);
                r5 = java.lang.Math.round(r5);
                r5 = (float) r5;
                r55 = r5 / r106;
                r50 = 0;
                r51 = 2;
                r52 = 0;
                r53 = 0;
                r0 = r129;
                r0 = r0.maxSizeWidth;
                r54 = r0;
                r56 = 7;
                r49 = r4;
                r49.set(r50, r51, r52, r53, r54, r55, r56);
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r5 = (float) r5;
                r0 = r121;
                r6 = r0.aspectRatio;
                r0 = r122;
                r7 = r0.aspectRatio;
                r6 = r6 + r7;
                r0 = r123;
                r7 = r0.aspectRatio;
                r6 = r6 + r7;
                r5 = r5 / r6;
                r5 = java.lang.Math.round(r5);
                r0 = (float) r5;
                r62 = r0;
                r0 = r109;
                r5 = (float) r0;
                r0 = r129;
                r6 = r0.maxSizeWidth;
                r6 = (float) r6;
                r7 = NUM; // 0x3ecccccd float:0.4 double:5.205520926E-315;
                r6 = r6 * r7;
                r0 = r121;
                r7 = r0.aspectRatio;
                r7 = r7 * r62;
                r6 = java.lang.Math.min(r6, r7);
                r5 = java.lang.Math.max(r5, r6);
                r0 = (int) r5;
                r61 = r0;
                r0 = r109;
                r5 = (float) r0;
                r0 = r129;
                r6 = r0.maxSizeWidth;
                r6 = (float) r6;
                r7 = NUM; // 0x3ea8f5c3 float:0.33 double:5.19391626E-315;
                r6 = r6 * r7;
                r5 = java.lang.Math.max(r5, r6);
                r0 = r123;
                r6 = r0.aspectRatio;
                r6 = r6 * r62;
                r5 = java.lang.Math.max(r5, r6);
                r0 = (int) r5;
                r74 = r0;
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r5 = r5 - r61;
                r68 = r5 - r74;
                r5 = r106 - r55;
                r0 = r62;
                r62 = java.lang.Math.min(r5, r0);
                r62 = r62 / r106;
                r57 = 0;
                r58 = 0;
                r59 = 1;
                r60 = 1;
                r63 = 9;
                r56 = r121;
                r56.set(r57, r58, r59, r60, r61, r62, r63);
                r64 = 1;
                r65 = 1;
                r66 = 1;
                r67 = 1;
                r70 = 8;
                r63 = r122;
                r69 = r62;
                r63.set(r64, r65, r66, r67, r68, r69, r70);
                r70 = 2;
                r71 = 2;
                r72 = 1;
                r73 = 1;
                r76 = 10;
                r69 = r123;
                r75 = r62;
                r69.set(r70, r71, r72, r73, r74, r75, r76);
                goto L_0x0206;
            L_0x0563:
                r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r0 = r121;
                r6 = r0.aspectRatio;
                r5 = r5 / r6;
                r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r0 = r122;
                r7 = r0.aspectRatio;
                r6 = r6 / r7;
                r6 = r6 + r5;
                r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r0 = r129;
                r5 = r0.posArray;
                r8 = 3;
                r5 = r5.get(r8);
                r5 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r5;
                r5 = r5.aspectRatio;
                r5 = r7 / r5;
                r5 = r5 + r6;
                r5 = r106 / r5;
                r5 = java.lang.Math.round(r5);
                r0 = r109;
                r54 = java.lang.Math.max(r0, r5);
                r5 = NUM; // 0x3ea8f5c3 float:0.33 double:5.19391626E-315;
                r0 = r107;
                r6 = (float) r0;
                r0 = r54;
                r7 = (float) r0;
                r0 = r121;
                r8 = r0.aspectRatio;
                r7 = r7 / r8;
                r6 = java.lang.Math.max(r6, r7);
                r6 = r6 / r106;
                r55 = java.lang.Math.min(r5, r6);
                r5 = NUM; // 0x3ea8f5c3 float:0.33 double:5.19391626E-315;
                r0 = r107;
                r6 = (float) r0;
                r0 = r54;
                r7 = (float) r0;
                r0 = r122;
                r8 = r0.aspectRatio;
                r7 = r7 / r8;
                r6 = java.lang.Math.max(r6, r7);
                r6 = r6 / r106;
                r100 = java.lang.Math.min(r5, r6);
                r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r5 = r5 - r55;
                r101 = r5 - r100;
                r5 = r4.aspectRatio;
                r5 = r5 * r106;
                r0 = r114;
                r6 = (float) r0;
                r5 = r5 + r6;
                r0 = r129;
                r6 = r0.maxSizeWidth;
                r6 = r6 - r54;
                r6 = (float) r6;
                r5 = java.lang.Math.min(r5, r6);
                r61 = java.lang.Math.round(r5);
                r76 = 0;
                r77 = 0;
                r78 = 0;
                r79 = 2;
                r5 = r55 + r100;
                r81 = r5 + r101;
                r82 = 13;
                r75 = r4;
                r80 = r61;
                r75.set(r76, r77, r78, r79, r80, r81, r82);
                r50 = 1;
                r51 = 1;
                r52 = 0;
                r53 = 0;
                r56 = 6;
                r49 = r121;
                r49.set(r50, r51, r52, r53, r54, r55, r56);
                r76 = 0;
                r77 = 1;
                r78 = 1;
                r79 = 1;
                r82 = 2;
                r75 = r122;
                r80 = r54;
                r81 = r100;
                r75.set(r76, r77, r78, r79, r80, r81, r82);
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r0 = r122;
                r0.spanSize = r5;
                r76 = 0;
                r77 = 1;
                r78 = 2;
                r79 = 2;
                r82 = 10;
                r75 = r123;
                r80 = r54;
                r81 = r101;
                r75.set(r76, r77, r78, r79, r80, r81, r82);
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r0 = r123;
                r0.spanSize = r5;
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r5 = r5 - r61;
                r0 = r121;
                r0.spanSize = r5;
                r0 = r61;
                r1 = r122;
                r1.leftSpanOffset = r0;
                r0 = r61;
                r1 = r123;
                r1.leftSpanOffset = r0;
                r5 = 3;
                r5 = new float[r5];
                r6 = 0;
                r5[r6] = r55;
                r6 = 1;
                r5[r6] = r100;
                r6 = 2;
                r5[r6] = r101;
                r4.siblingHeights = r5;
                r5 = 1;
                r0 = r129;
                r0.hasSibling = r5;
                goto L_0x0206;
            L_0x0662:
                r0 = r129;
                r5 = r0.posArray;
                r5 = r5.size();
                r0 = new float[r5];
                r96 = r0;
                r89 = 0;
            L_0x0670:
                r0 = r89;
                r1 = r95;
                if (r0 >= r1) goto L_0x06bf;
            L_0x0676:
                r5 = NUM; // 0x3f8ccccd float:1.1 double:5.26768877E-315;
                r5 = (r92 > r5 ? 1 : (r92 == r5 ? 0 : -1));
                if (r5 <= 0) goto L_0x06a8;
            L_0x067d:
                r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r0 = r129;
                r5 = r0.posArray;
                r0 = r89;
                r5 = r5.get(r0);
                r5 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r5;
                r5 = r5.aspectRatio;
                r5 = java.lang.Math.max(r6, r5);
                r96[r89] = r5;
            L_0x0693:
                r5 = NUM; // 0x3f2aaae3 float:0.66667 double:5.23591437E-315;
                r6 = NUM; // 0x3fd9999a float:1.7 double:5.29255591E-315;
                r7 = r96[r89];
                r6 = java.lang.Math.min(r6, r7);
                r5 = java.lang.Math.max(r5, r6);
                r96[r89] = r5;
                r89 = r89 + 1;
                goto L_0x0670;
            L_0x06a8:
                r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r0 = r129;
                r5 = r0.posArray;
                r0 = r89;
                r5 = r5.get(r0);
                r5 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r5;
                r5 = r5.aspectRatio;
                r5 = java.lang.Math.min(r6, r5);
                r96[r89] = r5;
                goto L_0x0693;
            L_0x06bf:
                r91 = new java.util.ArrayList;
                r91.<init>();
                r37 = 1;
            L_0x06c6:
                r0 = r96;
                r5 = r0.length;
                r0 = r37;
                if (r0 >= r5) goto L_0x0706;
            L_0x06cd:
                r0 = r96;
                r5 = r0.length;
                r38 = r5 - r37;
                r5 = 3;
                r0 = r37;
                if (r0 > r5) goto L_0x06dc;
            L_0x06d7:
                r5 = 3;
                r0 = r38;
                if (r0 <= r5) goto L_0x06df;
            L_0x06dc:
                r37 = r37 + 1;
                goto L_0x06c6;
            L_0x06df:
                r35 = new org.telegram.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt;
                r5 = 0;
                r0 = r129;
                r1 = r96;
                r2 = r37;
                r39 = r0.multiHeight(r1, r5, r2);
                r0 = r96;
                r5 = r0.length;
                r0 = r129;
                r1 = r96;
                r2 = r37;
                r40 = r0.multiHeight(r1, r2, r5);
                r36 = r129;
                r35.<init>(r37, r38, r39, r40);
                r0 = r91;
                r1 = r35;
                r0.add(r1);
                goto L_0x06dc;
            L_0x0706:
                r37 = 1;
            L_0x0708:
                r0 = r96;
                r5 = r0.length;
                r5 = r5 + -1;
                r0 = r37;
                if (r0 >= r5) goto L_0x0778;
            L_0x0711:
                r38 = 1;
            L_0x0713:
                r0 = r96;
                r5 = r0.length;
                r5 = r5 - r37;
                r0 = r38;
                if (r0 >= r5) goto L_0x0775;
            L_0x071c:
                r0 = r96;
                r5 = r0.length;
                r5 = r5 - r37;
                r79 = r5 - r38;
                r5 = 3;
                r0 = r37;
                if (r0 > r5) goto L_0x0739;
            L_0x0728:
                r5 = NUM; // 0x3var_a float:0.85 double:5.25111068E-315;
                r5 = (r92 > r5 ? 1 : (r92 == r5 ? 0 : -1));
                if (r5 >= 0) goto L_0x073c;
            L_0x072f:
                r5 = 4;
            L_0x0730:
                r0 = r38;
                if (r0 > r5) goto L_0x0739;
            L_0x0734:
                r5 = 3;
                r0 = r79;
                if (r0 <= r5) goto L_0x073e;
            L_0x0739:
                r38 = r38 + 1;
                goto L_0x0713;
            L_0x073c:
                r5 = 3;
                goto L_0x0730;
            L_0x073e:
                r75 = new org.telegram.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt;
                r5 = 0;
                r0 = r129;
                r1 = r96;
                r2 = r37;
                r80 = r0.multiHeight(r1, r5, r2);
                r5 = r37 + r38;
                r0 = r129;
                r1 = r96;
                r2 = r37;
                r81 = r0.multiHeight(r1, r2, r5);
                r5 = r37 + r38;
                r0 = r96;
                r6 = r0.length;
                r0 = r129;
                r1 = r96;
                r82 = r0.multiHeight(r1, r5, r6);
                r76 = r129;
                r77 = r37;
                r78 = r38;
                r75.<init>(r77, r78, r79, r80, r81, r82);
                r0 = r91;
                r1 = r75;
                r0.add(r1);
                goto L_0x0739;
            L_0x0775:
                r37 = r37 + 1;
                goto L_0x0708;
            L_0x0778:
                r37 = 1;
            L_0x077a:
                r0 = r96;
                r5 = r0.length;
                r5 = r5 + -2;
                r0 = r37;
                if (r0 >= r5) goto L_0x0809;
            L_0x0783:
                r38 = 1;
            L_0x0785:
                r0 = r96;
                r5 = r0.length;
                r5 = r5 - r37;
                r0 = r38;
                if (r0 >= r5) goto L_0x0805;
            L_0x078e:
                r79 = 1;
            L_0x0790:
                r0 = r96;
                r5 = r0.length;
                r5 = r5 - r37;
                r5 = r5 - r38;
                r0 = r79;
                if (r0 >= r5) goto L_0x0802;
            L_0x079b:
                r0 = r96;
                r5 = r0.length;
                r5 = r5 - r37;
                r5 = r5 - r38;
                r80 = r5 - r79;
                r5 = 3;
                r0 = r37;
                if (r0 > r5) goto L_0x07b8;
            L_0x07a9:
                r5 = 3;
                r0 = r38;
                if (r0 > r5) goto L_0x07b8;
            L_0x07ae:
                r5 = 3;
                r0 = r79;
                if (r0 > r5) goto L_0x07b8;
            L_0x07b3:
                r5 = 3;
                r0 = r80;
                if (r0 <= r5) goto L_0x07bb;
            L_0x07b8:
                r79 = r79 + 1;
                goto L_0x0790;
            L_0x07bb:
                r75 = new org.telegram.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt;
                r5 = 0;
                r0 = r129;
                r1 = r96;
                r2 = r37;
                r81 = r0.multiHeight(r1, r5, r2);
                r5 = r37 + r38;
                r0 = r129;
                r1 = r96;
                r2 = r37;
                r82 = r0.multiHeight(r1, r2, r5);
                r5 = r37 + r38;
                r6 = r37 + r38;
                r6 = r6 + r79;
                r0 = r129;
                r1 = r96;
                r83 = r0.multiHeight(r1, r5, r6);
                r5 = r37 + r38;
                r5 = r5 + r79;
                r0 = r96;
                r6 = r0.length;
                r0 = r129;
                r1 = r96;
                r84 = r0.multiHeight(r1, r5, r6);
                r76 = r129;
                r77 = r37;
                r78 = r38;
                r75.<init>(r77, r78, r79, r80, r81, r82, r83, r84);
                r0 = r91;
                r1 = r75;
                r0.add(r1);
                goto L_0x07b8;
            L_0x0802:
                r38 = r38 + 1;
                goto L_0x0785;
            L_0x0805:
                r37 = r37 + 1;
                goto L_0x077a;
            L_0x0809:
                r111 = 0;
                r112 = 0;
                r0 = r129;
                r5 = r0.maxSizeWidth;
                r5 = r5 / 3;
                r5 = r5 * 4;
                r0 = (float) r5;
                r105 = r0;
                r89 = 0;
            L_0x081a:
                r5 = r91.size();
                r0 = r89;
                if (r0 >= r5) goto L_0x08c1;
            L_0x0822:
                r0 = r91;
                r1 = r89;
                r90 = r0.get(r1);
                r90 = (org.telegram.ui.ArticleViewer.BlockCollageCell.GroupedMessages.MessageGroupedLayoutAttempt) r90;
                r10 = 0;
                r108 = NUM; // 0x7f7fffff float:3.4028235E38 double:1.056853372E-314;
                r93 = 0;
            L_0x0832:
                r0 = r90;
                r5 = r0.heights;
                r5 = r5.length;
                r0 = r93;
                if (r0 >= r5) goto L_0x0855;
            L_0x083b:
                r0 = r90;
                r5 = r0.heights;
                r5 = r5[r93];
                r10 = r10 + r5;
                r0 = r90;
                r5 = r0.heights;
                r5 = r5[r93];
                r5 = (r5 > r108 ? 1 : (r5 == r108 ? 0 : -1));
                if (r5 >= 0) goto L_0x0852;
            L_0x084c:
                r0 = r90;
                r5 = r0.heights;
                r108 = r5[r93];
            L_0x0852:
                r93 = r93 + 1;
                goto L_0x0832;
            L_0x0855:
                r5 = r10 - r105;
                r97 = java.lang.Math.abs(r5);
                r0 = r90;
                r5 = r0.lineCounts;
                r5 = r5.length;
                r6 = 1;
                if (r5 <= r6) goto L_0x08a8;
            L_0x0863:
                r0 = r90;
                r5 = r0.lineCounts;
                r6 = 0;
                r5 = r5[r6];
                r0 = r90;
                r6 = r0.lineCounts;
                r7 = 1;
                r6 = r6[r7];
                if (r5 > r6) goto L_0x08a3;
            L_0x0873:
                r0 = r90;
                r5 = r0.lineCounts;
                r5 = r5.length;
                r6 = 2;
                if (r5 <= r6) goto L_0x088b;
            L_0x087b:
                r0 = r90;
                r5 = r0.lineCounts;
                r6 = 1;
                r5 = r5[r6];
                r0 = r90;
                r6 = r0.lineCounts;
                r7 = 2;
                r6 = r6[r7];
                if (r5 > r6) goto L_0x08a3;
            L_0x088b:
                r0 = r90;
                r5 = r0.lineCounts;
                r5 = r5.length;
                r6 = 3;
                if (r5 <= r6) goto L_0x08a8;
            L_0x0893:
                r0 = r90;
                r5 = r0.lineCounts;
                r6 = 2;
                r5 = r5[r6];
                r0 = r90;
                r6 = r0.lineCounts;
                r7 = 3;
                r6 = r6[r7];
                if (r5 <= r6) goto L_0x08a8;
            L_0x08a3:
                r5 = NUM; // 0x3var_a float:1.2 double:5.271833295E-315;
                r97 = r97 * r5;
            L_0x08a8:
                r0 = r109;
                r5 = (float) r0;
                r5 = (r108 > r5 ? 1 : (r108 == r5 ? 0 : -1));
                if (r5 >= 0) goto L_0x08b3;
            L_0x08af:
                r5 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
                r97 = r97 * r5;
            L_0x08b3:
                if (r111 == 0) goto L_0x08b9;
            L_0x08b5:
                r5 = (r97 > r112 ? 1 : (r97 == r112 ? 0 : -1));
                if (r5 >= 0) goto L_0x08bd;
            L_0x08b9:
                r111 = r90;
                r112 = r97;
            L_0x08bd:
                r89 = r89 + 1;
                goto L_0x081a;
            L_0x08c1:
                if (r111 == 0) goto L_0x0021;
            L_0x08c3:
                r102 = 0;
                r128 = 0;
                r84 = 0;
            L_0x08c9:
                r0 = r111;
                r5 = r0.lineCounts;
                r5 = r5.length;
                r0 = r84;
                if (r0 >= r5) goto L_0x0206;
            L_0x08d2:
                r0 = r111;
                r5 = r0.lineCounts;
                r94 = r5[r84];
                r0 = r111;
                r5 = r0.heights;
                r103 = r5[r84];
                r0 = r129;
                r0 = r0.maxSizeWidth;
                r126 = r0;
                r119 = 0;
                r82 = 0;
            L_0x08e8:
                r0 = r82;
                r1 = r94;
                if (r0 >= r1) goto L_0x0932;
            L_0x08ee:
                r125 = r96[r102];
                r5 = r125 * r103;
                r9 = (int) r5;
                r126 = r126 - r9;
                r0 = r129;
                r5 = r0.posArray;
                r0 = r102;
                r81 = r5.get(r0);
                r81 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r81;
                r88 = 0;
                if (r84 != 0) goto L_0x0907;
            L_0x0905:
                r88 = r88 | 4;
            L_0x0907:
                r0 = r111;
                r5 = r0.lineCounts;
                r5 = r5.length;
                r5 = r5 + -1;
                r0 = r84;
                if (r0 != r5) goto L_0x0914;
            L_0x0912:
                r88 = r88 | 8;
            L_0x0914:
                if (r82 != 0) goto L_0x0918;
            L_0x0916:
                r88 = r88 | 1;
            L_0x0918:
                r5 = r94 + -1;
                r0 = r82;
                if (r0 != r5) goto L_0x0922;
            L_0x091e:
                r88 = r88 | 2;
                r119 = r81;
            L_0x0922:
                r87 = r103 / r106;
                r83 = r82;
                r85 = r84;
                r86 = r9;
                r81.set(r82, r83, r84, r85, r86, r87, r88);
                r102 = r102 + 1;
                r82 = r82 + 1;
                goto L_0x08e8;
            L_0x0932:
                r0 = r119;
                r5 = r0.pw;
                r5 = r5 + r126;
                r0 = r119;
                r0.pw = r5;
                r0 = r119;
                r5 = r0.spanSize;
                r5 = r5 + r126;
                r0 = r119;
                r0.spanSize = r5;
                r128 = r128 + r103;
                r84 = r84 + 1;
                goto L_0x08c9;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockCollageCell.GroupedMessages.calculate():void");
            }
        }

        public BlockCollageCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            this.innerListView = new RecyclerListView(context, ArticleViewer.this) {
                public void requestLayout() {
                    if (!BlockCollageCell.this.inLayout) {
                        super.requestLayout();
                    }
                }
            };
            this.innerListView.addItemDecoration(new ItemDecoration(ArticleViewer.this) {
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                    GroupedMessagePosition position;
                    outRect.bottom = 0;
                    if (view instanceof BlockPhotoCell) {
                        position = (GroupedMessagePosition) BlockCollageCell.this.group.positions.get(((BlockPhotoCell) view).currentBlock);
                    } else if (view instanceof BlockVideoCell) {
                        position = (GroupedMessagePosition) BlockCollageCell.this.group.positions.get(((BlockVideoCell) view).currentBlock);
                    } else {
                        position = null;
                    }
                    if (position != null && position.siblingHeights != null) {
                        int a;
                        float maxHeight = ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f;
                        int h = 0;
                        for (float f : position.siblingHeights) {
                            h += (int) Math.ceil((double) (f * maxHeight));
                        }
                        h += (position.maxY - position.minY) * AndroidUtilities.dp2(11.0f);
                        int count = BlockCollageCell.this.group.posArray.size();
                        for (a = 0; a < count; a++) {
                            GroupedMessagePosition pos = (GroupedMessagePosition) BlockCollageCell.this.group.posArray.get(a);
                            if (pos.minY == position.minY && ((pos.minX != position.minX || pos.maxX != position.maxX || pos.minY != position.minY || pos.maxY != position.maxY) && pos.minY == position.minY)) {
                                h -= ((int) Math.ceil((double) (pos.ph * maxHeight))) - AndroidUtilities.dp(4.0f);
                                break;
                            }
                        }
                        outRect.bottom = -h;
                    }
                }
            });
            final ArticleViewer articleViewer = ArticleViewer.this;
            this.gridLayoutManager = new GridLayoutManagerFixed(context, 1000, 1, true) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                public boolean shouldLayoutChildFromOpositeSide(View child) {
                    return false;
                }

                protected boolean hasSiblingChild(int position) {
                    GroupedMessagePosition pos = (GroupedMessagePosition) BlockCollageCell.this.group.positions.get((TLObject) BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1));
                    if (pos.minX == pos.maxX || pos.minY != pos.maxY || pos.minY == (byte) 0) {
                        return false;
                    }
                    int count = BlockCollageCell.this.group.posArray.size();
                    for (int a = 0; a < count; a++) {
                        GroupedMessagePosition p = (GroupedMessagePosition) BlockCollageCell.this.group.posArray.get(a);
                        if (p != pos && p.minY <= pos.minY && p.maxY >= pos.minY) {
                            return true;
                        }
                    }
                    return false;
                }
            };
            this.gridLayoutManager.setSpanSizeLookup(new SpanSizeLookup(ArticleViewer.this) {
                public int getSpanSize(int position) {
                    return ((GroupedMessagePosition) BlockCollageCell.this.group.positions.get((TLObject) BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1))).spanSize;
                }
            });
            this.innerListView.setLayoutManager(this.gridLayoutManager);
            RecyclerListView recyclerListView = this.innerListView;
            Adapter anonymousClass5 = new Adapter(ArticleViewer.this) {
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view;
                    switch (viewType) {
                        case 0:
                            view = new BlockPhotoCell(BlockCollageCell.this.getContext(), BlockCollageCell.this.parentAdapter, 2);
                            break;
                        default:
                            view = new BlockVideoCell(BlockCollageCell.this.getContext(), BlockCollageCell.this.parentAdapter, 2);
                            break;
                    }
                    return new Holder(view);
                }

                public void onBindViewHolder(ViewHolder holder, int position) {
                    PageBlock pageBlock = (PageBlock) BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1);
                    switch (holder.getItemViewType()) {
                        case 0:
                            BlockPhotoCell cell = holder.itemView;
                            cell.groupPosition = (GroupedMessagePosition) BlockCollageCell.this.group.positions.get(pageBlock);
                            cell.setBlock((TL_pageBlockPhoto) pageBlock, true, true);
                            return;
                        default:
                            BlockVideoCell cell2 = holder.itemView;
                            cell2.groupPosition = (GroupedMessagePosition) BlockCollageCell.this.group.positions.get(pageBlock);
                            cell2.setBlock((TL_pageBlockVideo) pageBlock, true, true);
                            return;
                    }
                }

                public int getItemCount() {
                    if (BlockCollageCell.this.currentBlock == null) {
                        return 0;
                    }
                    return BlockCollageCell.this.currentBlock.items.size();
                }

                public int getItemViewType(int position) {
                    if (((PageBlock) BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1)) instanceof TL_pageBlockPhoto) {
                        return 0;
                    }
                    return 1;
                }
            };
            this.innerAdapter = anonymousClass5;
            recyclerListView.setAdapter(anonymousClass5);
            addView(this.innerListView, LayoutHelper.createFrame(-1, -2.0f));
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockCollage block) {
            if (this.currentBlock != block) {
                this.currentBlock = block;
                this.group.calculate();
            }
            this.innerAdapter.notifyDataSetChanged();
            int color = ArticleViewer.this.getSelectedColor();
            if (color == 0) {
                this.innerListView.setGlowColor(-657673);
            } else if (color == 1) {
                this.innerListView.setGlowColor(-659492);
            } else if (color == 2) {
                this.innerListView.setGlowColor(-15461356);
            }
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.captionLayout, this.textX, this.textY)) {
                if (!(ArticleViewer.this.checkLayoutForLinks(event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event))) {
                    return false;
                }
            }
            return true;
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            this.inLayout = true;
            int width = MeasureSpec.getSize(widthMeasureSpec);
            if (this.currentBlock != null) {
                int textWidth;
                int listWidth = width;
                if (this.currentBlock.level > 0) {
                    int dp = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                    this.listX = dp;
                    this.textX = dp;
                    listWidth -= this.listX + AndroidUtilities.dp(18.0f);
                    textWidth = listWidth;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                }
                this.innerListView.measure(MeasureSpec.makeMeasureSpec(listWidth, NUM), MeasureSpec.makeMeasureSpec(0, 0));
                height = this.innerListView.getMeasuredHeight();
                this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock, this.parentAdapter);
                if (this.captionLayout != null) {
                    this.textY = AndroidUtilities.dp(8.0f) + height;
                    this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    height += this.creditOffset + AndroidUtilities.dp(4.0f);
                }
                this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.creditLayout != null) {
                    height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                height += AndroidUtilities.dp(16.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            this.inLayout = false;
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.innerListView.layout(this.listX, AndroidUtilities.dp(8.0f), this.listX + this.innerListView.getMeasuredWidth(), this.innerListView.getMeasuredHeight() + AndroidUtilities.dp(8.0f));
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockDetailsBottomCell extends View {
        private RectF rect = new RectF();

        public BlockDetailsBottomCell(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(4.0f) + 1);
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawLine(0.0f, 0.0f, (float) getMeasuredWidth(), 0.0f, ArticleViewer.dividerPaint);
        }
    }

    private class BlockDetailsCell extends View implements Callback {
        private AnimatedArrowDrawable arrow;
        private TL_pageBlockDetails currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(50.0f);
        private int textY = (AndroidUtilities.dp(11.0f) + 1);

        public BlockDetailsCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            this.arrow = new AnimatedArrowDrawable(ArticleViewer.this.getGrayTextColor(), true);
        }

        public void invalidateDrawable(Drawable drawable) {
            invalidate();
        }

        public void scheduleDrawable(Drawable drawable, Runnable runnable, long l) {
        }

        public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        }

        public void setBlock(TL_pageBlockDetails block) {
            this.currentBlock = block;
            this.arrow.setAnimationProgress(block.open ? 0.0f : 1.0f);
            this.arrow.setCallback(this);
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int h = AndroidUtilities.dp(39.0f);
            if (this.currentBlock != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.title, width - AndroidUtilities.dp(52.0f), this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    h = Math.max(h, AndroidUtilities.dp(21.0f) + this.textLayout.getHeight());
                    this.textY = ((this.textLayout.getHeight() + AndroidUtilities.dp(21.0f)) - this.textLayout.getHeight()) / 2;
                }
            }
            setMeasuredDimension(width, h + 1);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(18.0f), (float) (((getMeasuredHeight() - AndroidUtilities.dp(13.0f)) - 1) / 2));
                this.arrow.draw(canvas);
                canvas.restore();
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                int y = getMeasuredHeight() - 1;
                canvas.drawLine(0.0f, (float) y, (float) getMeasuredWidth(), (float) y, ArticleViewer.dividerPaint);
            }
        }
    }

    private class BlockDividerCell extends View {
        private RectF rect = new RectF();

        public BlockDividerCell(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(18.0f));
        }

        protected void onDraw(Canvas canvas) {
            int width = getMeasuredWidth() / 3;
            this.rect.set((float) width, (float) AndroidUtilities.dp(8.0f), (float) (width * 2), (float) AndroidUtilities.dp(10.0f));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), ArticleViewer.dividerPaint);
        }
    }

    private class BlockEmbedCell extends FrameLayout {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockEmbed currentBlock;
        private int exactWebViewHeight;
        private int listX;
        private WebpageAdapter parentAdapter;
        private int textX;
        private int textY;
        private WebPlayerView videoView;
        private boolean wasUserInteraction;
        private TouchyWebView webView;

        private class TelegramWebviewProxy {
            private TelegramWebviewProxy() {
            }

            /* synthetic */ TelegramWebviewProxy(BlockEmbedCell x0, AnonymousClass1 x1) {
                this();
            }

            @JavascriptInterface
            public void postEvent(String eventName, String eventData) {
                AndroidUtilities.runOnUIThread(new ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$$Lambda$0(this, eventName, eventData));
            }

            final /* synthetic */ void lambda$postEvent$0$ArticleViewer$BlockEmbedCell$TelegramWebviewProxy(String eventName, String eventData) {
                Object obj = -1;
                switch (eventName.hashCode()) {
                    case -1065790942:
                        if (eventName.equals("resize_frame")) {
                            obj = null;
                            break;
                        }
                        break;
                }
                switch (obj) {
                    case null:
                        try {
                            BlockEmbedCell.this.exactWebViewHeight = Utilities.parseInt(new JSONObject(eventData).getString("height")).intValue();
                            BlockEmbedCell.this.requestLayout();
                            return;
                        } catch (Throwable th) {
                            return;
                        }
                    default:
                        return;
                }
            }
        }

        public class TouchyWebView extends WebView {
            public TouchyWebView(Context context) {
                super(context);
                setFocusable(false);
            }

            public boolean onTouchEvent(MotionEvent event) {
                BlockEmbedCell.this.wasUserInteraction = true;
                if (BlockEmbedCell.this.currentBlock != null) {
                    if (BlockEmbedCell.this.currentBlock.allow_scrolling) {
                        requestDisallowInterceptTouchEvent(true);
                    } else {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                }
                return super.onTouchEvent(event);
            }
        }

        @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
        public BlockEmbedCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            this.videoView = new WebPlayerView(context, false, false, new WebPlayerViewDelegate(ArticleViewer.this) {
                public void onInitFailed() {
                    BlockEmbedCell.this.webView.setVisibility(0);
                    BlockEmbedCell.this.videoView.setVisibility(4);
                    BlockEmbedCell.this.videoView.loadVideo(null, null, null, null, false);
                    HashMap<String, String> args = new HashMap();
                    args.put("Referer", "http://youtube.com");
                    BlockEmbedCell.this.webView.loadUrl(BlockEmbedCell.this.currentBlock.url, args);
                }

                public void onVideoSizeChanged(float aspectRatio, int rotation) {
                    ArticleViewer.this.fullscreenAspectRatioView.setAspectRatio(aspectRatio, rotation);
                }

                public void onInlineSurfaceTextureReady() {
                }

                public TextureView onSwitchToFullscreen(View controlsView, boolean fullscreen, float aspectRatio, int rotation, boolean byButton) {
                    if (fullscreen) {
                        ArticleViewer.this.fullscreenAspectRatioView.addView(ArticleViewer.this.fullscreenTextureView, LayoutHelper.createFrame(-1, -1.0f));
                        ArticleViewer.this.fullscreenAspectRatioView.setVisibility(0);
                        ArticleViewer.this.fullscreenAspectRatioView.setAspectRatio(aspectRatio, rotation);
                        ArticleViewer.this.fullscreenedVideo = BlockEmbedCell.this.videoView;
                        ArticleViewer.this.fullscreenVideoContainer.addView(controlsView, LayoutHelper.createFrame(-1, -1.0f));
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(0);
                    } else {
                        ArticleViewer.this.fullscreenAspectRatioView.removeView(ArticleViewer.this.fullscreenTextureView);
                        ArticleViewer.this.fullscreenedVideo = null;
                        ArticleViewer.this.fullscreenAspectRatioView.setVisibility(8);
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(4);
                    }
                    return ArticleViewer.this.fullscreenTextureView;
                }

                public void prepareToSwitchInlineMode(boolean inline, Runnable switchInlineModeRunnable, float aspectRatio, boolean animated) {
                }

                public TextureView onSwitchInlineMode(View controlsView, boolean inline, float aspectRatio, int rotation, boolean animated) {
                    return null;
                }

                public void onSharePressed() {
                    if (ArticleViewer.this.parentActivity != null) {
                        ArticleViewer.this.showDialog(new ShareAlert(ArticleViewer.this.parentActivity, null, BlockEmbedCell.this.currentBlock.url, false, BlockEmbedCell.this.currentBlock.url, true));
                    }
                }

                public void onPlayStateChanged(WebPlayerView playerView, boolean playing) {
                    if (playing) {
                        if (!(ArticleViewer.this.currentPlayingVideo == null || ArticleViewer.this.currentPlayingVideo == playerView)) {
                            ArticleViewer.this.currentPlayingVideo.pause();
                        }
                        ArticleViewer.this.currentPlayingVideo = playerView;
                        try {
                            ArticleViewer.this.parentActivity.getWindow().addFlags(128);
                            return;
                        } catch (Throwable e) {
                            FileLog.e(e);
                            return;
                        }
                    }
                    if (ArticleViewer.this.currentPlayingVideo == playerView) {
                        ArticleViewer.this.currentPlayingVideo = null;
                    }
                    try {
                        ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                    }
                }

                public boolean checkInlinePermissions() {
                    return false;
                }

                public ViewGroup getTextureViewContainer() {
                    return null;
                }
            });
            addView(this.videoView);
            ArticleViewer.this.createdWebViews.add(this);
            this.webView = new TouchyWebView(context);
            this.webView.getSettings().setJavaScriptEnabled(true);
            this.webView.getSettings().setDomStorageEnabled(true);
            this.webView.getSettings().setAllowContentAccess(true);
            if (VERSION.SDK_INT >= 17) {
                this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
                this.webView.addJavascriptInterface(new TelegramWebviewProxy(this, null), "TelegramWebviewProxy");
            }
            if (VERSION.SDK_INT >= 21) {
                this.webView.getSettings().setMixedContentMode(0);
                CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
            }
            this.webView.setWebChromeClient(new WebChromeClient(ArticleViewer.this) {
                public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                    onShowCustomView(view, callback);
                }

                public void onShowCustomView(View view, CustomViewCallback callback) {
                    if (ArticleViewer.this.customView != null) {
                        callback.onCustomViewHidden();
                        return;
                    }
                    ArticleViewer.this.customView = view;
                    ArticleViewer.this.customViewCallback = callback;
                    AndroidUtilities.runOnUIThread(new ArticleViewer$BlockEmbedCell$2$$Lambda$0(this), 100);
                }

                final /* synthetic */ void lambda$onShowCustomView$0$ArticleViewer$BlockEmbedCell$2() {
                    if (ArticleViewer.this.customView != null) {
                        ArticleViewer.this.fullscreenVideoContainer.addView(ArticleViewer.this.customView, LayoutHelper.createFrame(-1, -1.0f));
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(0);
                    }
                }

                public void onHideCustomView() {
                    super.onHideCustomView();
                    if (ArticleViewer.this.customView != null) {
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(4);
                        ArticleViewer.this.fullscreenVideoContainer.removeView(ArticleViewer.this.customView);
                        if (!(ArticleViewer.this.customViewCallback == null || ArticleViewer.this.customViewCallback.getClass().getName().contains(".chromium."))) {
                            ArticleViewer.this.customViewCallback.onCustomViewHidden();
                        }
                        ArticleViewer.this.customView = null;
                    }
                }
            });
            this.webView.setWebViewClient(new WebViewClient(ArticleViewer.this) {
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                }

                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }

                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (!BlockEmbedCell.this.wasUserInteraction) {
                        return false;
                    }
                    Browser.openUrl(ArticleViewer.this.parentActivity, url);
                    return true;
                }
            });
            addView(this.webView);
        }

        public void destroyWebView(boolean completely) {
            try {
                this.webView.stopLoading();
                this.webView.loadUrl("about:blank");
                if (completely) {
                    this.webView.destroy();
                }
                this.currentBlock = null;
            } catch (Throwable e) {
                FileLog.e(e);
            }
            this.videoView.destroy();
        }

        public void setBlock(TL_pageBlockEmbed block) {
            Photo thumb = null;
            TL_pageBlockEmbed previousBlock = this.currentBlock;
            this.currentBlock = block;
            if (previousBlock != this.currentBlock) {
                this.wasUserInteraction = false;
                if (this.currentBlock.allow_scrolling) {
                    this.webView.setVerticalScrollBarEnabled(true);
                    this.webView.setHorizontalScrollBarEnabled(true);
                } else {
                    this.webView.setVerticalScrollBarEnabled(false);
                    this.webView.setHorizontalScrollBarEnabled(false);
                }
                this.exactWebViewHeight = 0;
                try {
                    this.webView.loadUrl("about:blank");
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                try {
                    if (this.currentBlock.html != null) {
                        this.webView.loadDataWithBaseURL("https://telegram.org/embed", this.currentBlock.html, "text/html", "UTF-8", null);
                        this.videoView.setVisibility(4);
                        this.videoView.loadVideo(null, null, null, null, false);
                        this.webView.setVisibility(0);
                    } else {
                        if (this.currentBlock.poster_photo_id != 0) {
                            thumb = ArticleViewer.this.getPhotoWithId(this.currentBlock.poster_photo_id);
                        }
                        if (this.videoView.loadVideo(block.url, thumb, ArticleViewer.this.currentPage, null, false)) {
                            this.webView.setVisibility(4);
                            this.videoView.setVisibility(0);
                            this.webView.stopLoading();
                            this.webView.loadUrl("about:blank");
                        } else {
                            this.webView.setVisibility(0);
                            this.videoView.setVisibility(4);
                            this.videoView.loadVideo(null, null, null, null, false);
                            HashMap<String, String> args = new HashMap();
                            args.put("Referer", "http://youtube.com");
                            this.webView.loadUrl(this.currentBlock.url, args);
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
            requestLayout();
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (!ArticleViewer.this.isVisible) {
                this.currentBlock = null;
            }
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.captionLayout, this.textX, this.textY)) {
                if (!(ArticleViewer.this.checkLayoutForLinks(event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event))) {
                    return false;
                }
            }
            return true;
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int width = MeasureSpec.getSize(widthMeasureSpec);
            if (this.currentBlock != null) {
                int textWidth;
                float scale;
                int listWidth = width;
                if (this.currentBlock.level > 0) {
                    int dp = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                    this.listX = dp;
                    this.textX = dp;
                    listWidth -= this.listX + AndroidUtilities.dp(18.0f);
                    textWidth = listWidth;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                    if (!this.currentBlock.full_width) {
                        listWidth -= AndroidUtilities.dp(36.0f);
                        this.listX += AndroidUtilities.dp(18.0f);
                    }
                }
                if (this.currentBlock.w == 0) {
                    scale = 1.0f;
                } else {
                    scale = ((float) width) / ((float) this.currentBlock.w);
                }
                if (this.exactWebViewHeight != 0) {
                    height = AndroidUtilities.dp((float) this.exactWebViewHeight);
                } else {
                    float dp2;
                    if (this.currentBlock.w == 0) {
                        dp2 = ((float) AndroidUtilities.dp((float) this.currentBlock.h)) * scale;
                    } else {
                        dp2 = ((float) this.currentBlock.h) * scale;
                    }
                    height = (int) dp2;
                }
                if (height == 0) {
                    height = AndroidUtilities.dp(10.0f);
                }
                this.webView.measure(MeasureSpec.makeMeasureSpec(listWidth, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                if (this.videoView.getParent() == this) {
                    this.videoView.measure(MeasureSpec.makeMeasureSpec(listWidth, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f) + height, NUM));
                }
                this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock, this.parentAdapter);
                if (this.captionLayout != null) {
                    this.textY = AndroidUtilities.dp(8.0f) + height;
                    this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    height += this.creditOffset + AndroidUtilities.dp(4.0f);
                }
                this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.creditLayout != null) {
                    height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                height += AndroidUtilities.dp(5.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.dp(8.0f);
                } else if (this.currentBlock.level == 0 && this.captionLayout != null) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.webView.layout(this.listX, 0, this.listX + this.webView.getMeasuredWidth(), this.webView.getMeasuredHeight());
            if (this.videoView.getParent() == this) {
                this.videoView.layout(this.listX, 0, this.listX + this.videoView.getMeasuredWidth(), this.videoView.getMeasuredHeight());
            }
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockEmbedPostCell extends View {
        private AvatarDrawable avatarDrawable;
        private ImageReceiver avatarImageView;
        private boolean avatarVisible;
        private DrawingText captionLayout;
        private int captionX = AndroidUtilities.dp(18.0f);
        private int captionY;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockEmbedPost currentBlock;
        private DrawingText dateLayout;
        private int dateX;
        private int lineHeight;
        private DrawingText nameLayout;
        private int nameX;
        private WebpageAdapter parentAdapter;
        private int textX = AndroidUtilities.dp(32.0f);
        private int textY = AndroidUtilities.dp(56.0f);

        public BlockEmbedPostCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            this.avatarImageView = new ImageReceiver(this);
            this.avatarImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            this.avatarImageView.setImageCoords(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            this.avatarDrawable = new AvatarDrawable();
        }

        public void setBlock(TL_pageBlockEmbedPost block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.captionLayout, this.textX, this.textY)) {
                if (!(ArticleViewer.this.checkLayoutForLinks(event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event))) {
                    return false;
                }
            }
            return true;
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int width = MeasureSpec.getSize(widthMeasureSpec);
            if (this.currentBlock != null) {
                boolean z = this.currentBlock.author_photo_id != 0;
                this.avatarVisible = z;
                if (z) {
                    Photo photo = ArticleViewer.this.getPhotoWithId(this.currentBlock.author_photo_id);
                    z = photo instanceof TL_photo;
                    this.avatarVisible = z;
                    if (z) {
                        this.avatarDrawable.setInfo(0, this.currentBlock.author, null, false);
                        this.avatarImageView.setImage(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.dp(40.0f), true), String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(40), Integer.valueOf(40)}), this.avatarDrawable, 0, null, ArticleViewer.this.currentPage, 1);
                    }
                }
                this.nameLayout = ArticleViewer.this.createLayoutForText(this, this.currentBlock.author, null, width - AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 50)), 0, this.currentBlock, Alignment.ALIGN_NORMAL, 1, this.parentAdapter);
                if (this.currentBlock.date != 0) {
                    this.dateLayout = ArticleViewer.this.createLayoutForText(this, LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.date) * 1000), null, width - AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 50)), this.currentBlock, this.parentAdapter);
                } else {
                    this.dateLayout = null;
                }
                height = AndroidUtilities.dp(56.0f);
                int textWidth = width - AndroidUtilities.dp(50.0f);
                this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock, this.parentAdapter);
                if (this.captionLayout != null) {
                    this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    height += this.creditOffset + AndroidUtilities.dp(4.0f);
                }
                this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.creditLayout != null) {
                    height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                this.lineHeight = height;
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            int i = 54;
            int i2 = 0;
            if (this.currentBlock != null) {
                if (this.avatarVisible) {
                    this.avatarImageView.draw(canvas);
                }
                if (this.nameLayout != null) {
                    canvas.save();
                    canvas.translate((float) AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 32)), (float) AndroidUtilities.dp(this.dateLayout != null ? 10.0f : 19.0f));
                    this.nameLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.dateLayout != null) {
                    canvas.save();
                    if (!this.avatarVisible) {
                        i = 0;
                    }
                    canvas.translate((float) AndroidUtilities.dp((float) (i + 32)), (float) AndroidUtilities.dp(29.0f));
                    this.dateLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                float dp = (float) AndroidUtilities.dp(18.0f);
                float dp2 = (float) AndroidUtilities.dp(6.0f);
                float dp3 = (float) AndroidUtilities.dp(20.0f);
                int i3 = this.lineHeight;
                if (this.currentBlock.level == 0) {
                    i2 = AndroidUtilities.dp(6.0f);
                }
                canvas.drawRect(dp, dp2, dp3, (float) (i3 - i2), ArticleViewer.quoteLinePaint);
            }
        }
    }

    private class BlockFooterCell extends View {
        private TL_pageBlockFooter currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockFooterCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TL_pageBlockFooter block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                if (this.currentBlock.level == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 18));
                }
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.text, (width - AndroidUtilities.dp(18.0f)) - this.textX, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    height = this.textLayout.getHeight();
                    if (this.currentBlock.level > 0) {
                        height += AndroidUtilities.dp(8.0f);
                    } else {
                        height += AndroidUtilities.dp(16.0f);
                    }
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockHeaderCell extends View {
        private TL_pageBlockHeader currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockHeaderCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TL_pageBlockHeader block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockKickerCell extends View {
        private TL_pageBlockKicker currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockKickerCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TL_pageBlockKicker block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                }
                if (this.currentBlock.first) {
                    height += AndroidUtilities.dp(8.0f);
                    this.textY = AndroidUtilities.dp(16.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockListItemCell extends ViewGroup {
        private ViewHolder blockLayout;
        private int blockX;
        private int blockY;
        private TL_pageBlockListItem currentBlock;
        private int currentBlockType;
        private boolean drawDot;
        private int numOffsetY;
        private WebpageAdapter parentAdapter;
        private boolean parentIsList;
        private DrawingText textLayout;
        private int textX;
        private int textY;
        private boolean verticalAlign;

        public BlockListItemCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockListItem block) {
            if (this.currentBlock != block) {
                this.currentBlock = block;
                if (this.blockLayout != null) {
                    removeView(this.blockLayout.itemView);
                    this.blockLayout = null;
                }
                if (this.currentBlock.blockItem != null) {
                    this.currentBlockType = this.parentAdapter.getTypeForBlock(this.currentBlock.blockItem);
                    this.blockLayout = this.parentAdapter.onCreateViewHolder(this, this.currentBlockType);
                    addView(this.blockLayout.itemView);
                }
            }
            if (this.currentBlock.blockItem != null) {
                this.parentAdapter.bindBlockToHolder(this.currentBlockType, this.blockLayout, this.currentBlock.blockItem, 0, 0);
            }
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(event);
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                this.textLayout = null;
                int dp = (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) ? AndroidUtilities.dp(10.0f) : 0;
                this.textY = dp;
                this.numOffsetY = 0;
                if (!(this.currentBlock.parent.lastMaxNumCalcWidth == width && this.currentBlock.parent.lastFontSize == ArticleViewer.this.selectedFontSize)) {
                    this.currentBlock.parent.lastMaxNumCalcWidth = width;
                    this.currentBlock.parent.lastFontSize = ArticleViewer.this.selectedFontSize;
                    this.currentBlock.parent.maxNumWidth = 0;
                    int size = this.currentBlock.parent.items.size();
                    for (int a = 0; a < size; a++) {
                        TL_pageBlockListItem item = (TL_pageBlockListItem) this.currentBlock.parent.items.get(a);
                        if (item.num != null) {
                            item.numLayout = ArticleViewer.this.createLayoutForText(this, item.num, null, width - AndroidUtilities.dp(54.0f), this.currentBlock, this.parentAdapter);
                            this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) item.numLayout.getLineWidth(0)));
                        }
                    }
                    this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) ArticleViewer.listTextNumPaint.measureText("00.")));
                }
                this.drawDot = !this.currentBlock.parent.pageBlockList.ordered;
                boolean z = (getParent() instanceof BlockListItemCell) || (getParent() instanceof BlockOrderedListItemCell);
                this.parentIsList = z;
                if (ArticleViewer.this.isRtl) {
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = (AndroidUtilities.dp(24.0f) + this.currentBlock.parent.maxNumWidth) + (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f));
                }
                int maxWidth = (width - AndroidUtilities.dp(18.0f)) - this.textX;
                if (ArticleViewer.this.isRtl) {
                    maxWidth -= (AndroidUtilities.dp(6.0f) + this.currentBlock.parent.maxNumWidth) + (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f));
                }
                if (this.currentBlock.textItem != null) {
                    this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.textItem, maxWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                    if (this.textLayout != null && this.textLayout.getLineCount() > 0) {
                        if (this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            this.numOffsetY = (this.currentBlock.numLayout.getLineAscent(0) + AndroidUtilities.dp(2.5f)) - this.textLayout.getLineAscent(0);
                        }
                        height = 0 + (this.textLayout.getHeight() + AndroidUtilities.dp(8.0f));
                    }
                } else if (this.currentBlock.blockItem != null) {
                    this.blockX = this.textX;
                    this.blockY = this.textY;
                    if (this.blockLayout != null) {
                        if (this.blockLayout.itemView instanceof BlockParagraphCell) {
                            this.blockY -= AndroidUtilities.dp(8.0f);
                            if (!ArticleViewer.this.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                            height = 0 - AndroidUtilities.dp(8.0f);
                        } else if ((this.blockLayout.itemView instanceof BlockHeaderCell) || (this.blockLayout.itemView instanceof BlockSubheaderCell) || (this.blockLayout.itemView instanceof BlockTitleCell) || (this.blockLayout.itemView instanceof BlockSubtitleCell)) {
                            if (!ArticleViewer.this.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                        } else if (ArticleViewer.this.isListItemBlock(this.currentBlock.blockItem)) {
                            this.blockX = 0;
                            this.blockY = 0;
                            this.textY = 0;
                            if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                                height = 0 - AndroidUtilities.dp(10.0f);
                            }
                            maxWidth = width;
                            height -= AndroidUtilities.dp(8.0f);
                        } else if (this.blockLayout.itemView instanceof BlockTableCell) {
                            this.blockX -= AndroidUtilities.dp(18.0f);
                            maxWidth += AndroidUtilities.dp(36.0f);
                        }
                        this.blockLayout.itemView.measure(MeasureSpec.makeMeasureSpec(maxWidth, NUM), MeasureSpec.makeMeasureSpec(0, 0));
                        if ((this.blockLayout.itemView instanceof BlockParagraphCell) && this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            BlockParagraphCell paragraphCell = this.blockLayout.itemView;
                            if (paragraphCell.textLayout != null && paragraphCell.textLayout.getLineCount() > 0) {
                                this.numOffsetY = (this.currentBlock.numLayout.getLineAscent(0) + AndroidUtilities.dp(2.5f)) - paragraphCell.textLayout.getLineAscent(0);
                            }
                        }
                        if (this.currentBlock.blockItem instanceof TL_pageBlockDetails) {
                            this.verticalAlign = true;
                            this.blockY = 0;
                            if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                                height -= AndroidUtilities.dp(10.0f);
                            }
                            height -= AndroidUtilities.dp(8.0f);
                        } else if (this.blockLayout.itemView instanceof BlockOrderedListItemCell) {
                            this.verticalAlign = ((BlockOrderedListItemCell) this.blockLayout.itemView).verticalAlign;
                        } else if (this.blockLayout.itemView instanceof BlockListItemCell) {
                            this.verticalAlign = ((BlockListItemCell) this.blockLayout.itemView).verticalAlign;
                        }
                        if (this.verticalAlign && this.currentBlock.numLayout != null) {
                            this.textY = ((this.blockLayout.itemView.getMeasuredHeight() - this.currentBlock.numLayout.getHeight()) / 2) - AndroidUtilities.dp(4.0f);
                            this.drawDot = false;
                        }
                        height += this.blockLayout.itemView.getMeasuredHeight();
                    }
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentBlock.parent.items.get(this.currentBlock.parent.items.size() - 1) == this.currentBlock) {
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                    height += AndroidUtilities.dp(10.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (this.blockLayout != null) {
                this.blockLayout.itemView.layout(this.blockX, this.blockY, this.blockX + this.blockLayout.itemView.getMeasuredWidth(), this.blockY + this.blockLayout.itemView.getMeasuredHeight());
            }
        }

        protected void onDraw(Canvas canvas) {
            int i = 0;
            if (this.currentBlock != null) {
                int width = getMeasuredWidth();
                if (this.currentBlock.numLayout != null) {
                    canvas.save();
                    float dp;
                    int i2;
                    if (ArticleViewer.this.isRtl) {
                        dp = (float) (((width - AndroidUtilities.dp(15.0f)) - this.currentBlock.parent.maxNumWidth) - (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f)));
                        i2 = this.textY + this.numOffsetY;
                        if (this.drawDot) {
                            i = AndroidUtilities.dp(1.0f);
                        }
                        canvas.translate(dp, (float) (i2 - i));
                    } else {
                        dp = (float) (((AndroidUtilities.dp(15.0f) + this.currentBlock.parent.maxNumWidth) - ((int) Math.ceil((double) this.currentBlock.numLayout.getLineWidth(0)))) + (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f)));
                        i2 = this.textY + this.numOffsetY;
                        if (this.drawDot) {
                            i = AndroidUtilities.dp(1.0f);
                        }
                        canvas.translate(dp, (float) (i2 - i));
                    }
                    this.currentBlock.numLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockMapCell extends FrameLayout {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockMap currentBlock;
        private int currentMapProvider;
        private int currentType;
        private ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private boolean isLast;
        private WebpageAdapter parentAdapter;
        private boolean photoPressed;
        private int textX;
        private int textY;

        public BlockMapCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            this.currentType = type;
        }

        public void setBlock(TL_pageBlockMap block, boolean first, boolean last) {
            this.currentBlock = block;
            this.isFirst = first;
            this.isLast = last;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            if (event.getAction() == 0 && this.imageView.isInsideImage(x, y)) {
                this.photoPressed = true;
            } else if (event.getAction() == 1 && this.photoPressed) {
                this.photoPressed = false;
                try {
                    double lat = this.currentBlock.geo.lat;
                    double lon = this.currentBlock.geo._long;
                    ArticleViewer.this.parentActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon)));
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            } else if (event.getAction() == 3) {
                this.photoPressed = false;
            }
            if (!this.photoPressed) {
                if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.captionLayout, this.textX, this.textY)) {
                    if (!(ArticleViewer.this.checkLayoutForLinks(event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event))) {
                        return false;
                    }
                }
            }
            return true;
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentType == 1) {
                width = ((View) getParent()).getMeasuredWidth();
                height = ((View) getParent()).getMeasuredHeight();
            } else if (this.currentType == 2) {
                height = width;
            }
            if (this.currentBlock != null) {
                int photoX;
                int textWidth;
                int photoWidth = width;
                if (this.currentType != 0 || this.currentBlock.level <= 0) {
                    photoX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                } else {
                    photoX = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                    this.textX = photoX;
                    photoWidth -= AndroidUtilities.dp(18.0f) + photoX;
                    textWidth = photoWidth;
                }
                if (this.currentType == 0) {
                    height = (int) (((float) this.currentBlock.h) * (((float) photoWidth) / ((float) this.currentBlock.w)));
                    int maxHeight = (int) (((float) (Math.max(ArticleViewer.this.listView[0].getMeasuredWidth(), ArticleViewer.this.listView[0].getMeasuredHeight()) - AndroidUtilities.dp(56.0f))) * 0.9f);
                    if (height > maxHeight) {
                        height = maxHeight;
                        photoWidth = (int) (((float) this.currentBlock.w) * (((float) height) / ((float) this.currentBlock.h)));
                        photoX += ((width - photoX) - photoWidth) / 2;
                    }
                }
                ImageReceiver imageReceiver = this.imageView;
                int dp = (this.isFirst || this.currentType == 1 || this.currentType == 2 || this.currentBlock.level > 0) ? 0 : AndroidUtilities.dp(8.0f);
                imageReceiver.setImageCoords(photoX, dp, photoWidth, height);
                String currentUrl = AndroidUtilities.formapMapUrl(ArticleViewer.this.currentAccount, this.currentBlock.geo.lat, this.currentBlock.geo._long, (int) (((float) photoWidth) / AndroidUtilities.density), (int) (((float) height) / AndroidUtilities.density), true, 15);
                WebFile currentWebFile = WebFile.createWithGeoPoint(this.currentBlock.geo, (int) (((float) photoWidth) / AndroidUtilities.density), (int) (((float) height) / AndroidUtilities.density), 15, Math.min(2, (int) Math.ceil((double) AndroidUtilities.density)));
                this.currentMapProvider = MessagesController.getInstance(ArticleViewer.this.currentAccount).mapProvider;
                if (this.currentMapProvider == 2) {
                    if (currentWebFile != null) {
                        this.imageView.setImage(currentWebFile, null, Theme.chat_locationDrawable[0], null, ArticleViewer.this.currentPage, 0);
                    }
                } else if (currentUrl != null) {
                    this.imageView.setImage(currentUrl, null, Theme.chat_locationDrawable[0], null, 0);
                }
                if (this.currentType == 0) {
                    this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock, this.parentAdapter);
                    if (this.captionLayout != null) {
                        this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                        height += this.creditOffset + AndroidUtilities.dp(4.0f);
                    }
                    this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                    if (this.creditLayout != null) {
                        height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    }
                }
                if (!this.isFirst && this.currentType == 0 && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentType != 2) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentType == 0) {
            }
            if (this.currentBlock != null) {
                this.imageView.draw(canvas);
                if (this.currentMapProvider == 2 && this.imageView.hasNotThumb()) {
                    int w = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicWidth()) * 0.8f);
                    int h = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicHeight()) * 0.8f);
                    int x = this.imageView.getImageX() + ((this.imageView.getImageWidth() - w) / 2);
                    int y = this.imageView.getImageY() + ((this.imageView.getImageHeight() / 2) - h);
                    Theme.chat_redLocationIcon.setAlpha((int) (255.0f * this.imageView.getCurrentAlpha()));
                    Theme.chat_redLocationIcon.setBounds(x, y, x + w, y + h);
                    Theme.chat_redLocationIcon.draw(canvas);
                }
                this.textY = (this.imageView.getImageY() + this.imageView.getImageHeight()) + AndroidUtilities.dp(8.0f);
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockOrderedListItemCell extends ViewGroup {
        private ViewHolder blockLayout;
        private int blockX;
        private int blockY;
        private TL_pageBlockOrderedListItem currentBlock;
        private int currentBlockType;
        private int numOffsetY;
        private WebpageAdapter parentAdapter;
        private boolean parentIsList;
        private DrawingText textLayout;
        private int textX;
        private int textY;
        private boolean verticalAlign;

        public BlockOrderedListItemCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockOrderedListItem block) {
            if (this.currentBlock != block) {
                this.currentBlock = block;
                if (this.blockLayout != null) {
                    removeView(this.blockLayout.itemView);
                    this.blockLayout = null;
                }
                if (this.currentBlock.blockItem != null) {
                    this.currentBlockType = this.parentAdapter.getTypeForBlock(this.currentBlock.blockItem);
                    this.blockLayout = this.parentAdapter.onCreateViewHolder(this, this.currentBlockType);
                    addView(this.blockLayout.itemView);
                }
            }
            if (this.currentBlock.blockItem != null) {
                this.parentAdapter.bindBlockToHolder(this.currentBlockType, this.blockLayout, this.currentBlock.blockItem, 0, 0);
            }
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(event);
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                this.textLayout = null;
                int dp = (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) ? AndroidUtilities.dp(10.0f) : 0;
                this.textY = dp;
                this.numOffsetY = 0;
                if (!(this.currentBlock.parent.lastMaxNumCalcWidth == width && this.currentBlock.parent.lastFontSize == ArticleViewer.this.selectedFontSize)) {
                    this.currentBlock.parent.lastMaxNumCalcWidth = width;
                    this.currentBlock.parent.lastFontSize = ArticleViewer.this.selectedFontSize;
                    this.currentBlock.parent.maxNumWidth = 0;
                    int size = this.currentBlock.parent.items.size();
                    for (int a = 0; a < size; a++) {
                        TL_pageBlockOrderedListItem item = (TL_pageBlockOrderedListItem) this.currentBlock.parent.items.get(a);
                        if (item.num != null) {
                            item.numLayout = ArticleViewer.this.createLayoutForText(this, item.num, null, width - AndroidUtilities.dp(54.0f), this.currentBlock, this.parentAdapter);
                            this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) item.numLayout.getLineWidth(0)));
                        }
                    }
                    this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) ArticleViewer.listTextNumPaint.measureText("00.")));
                }
                if (ArticleViewer.this.isRtl) {
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = (AndroidUtilities.dp(24.0f) + this.currentBlock.parent.maxNumWidth) + (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f));
                }
                this.verticalAlign = false;
                int maxWidth = (width - AndroidUtilities.dp(18.0f)) - this.textX;
                if (ArticleViewer.this.isRtl) {
                    maxWidth -= (AndroidUtilities.dp(6.0f) + this.currentBlock.parent.maxNumWidth) + (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f));
                }
                if (this.currentBlock.textItem != null) {
                    this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.textItem, maxWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                    if (this.textLayout != null && this.textLayout.getLineCount() > 0) {
                        if (this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            this.numOffsetY = this.currentBlock.numLayout.getLineAscent(0) - this.textLayout.getLineAscent(0);
                        }
                        height = 0 + (this.textLayout.getHeight() + AndroidUtilities.dp(8.0f));
                    }
                } else if (this.currentBlock.blockItem != null) {
                    this.blockX = this.textX;
                    this.blockY = this.textY;
                    if (this.blockLayout != null) {
                        if (this.blockLayout.itemView instanceof BlockParagraphCell) {
                            this.blockY -= AndroidUtilities.dp(8.0f);
                            if (!ArticleViewer.this.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                            height = 0 - AndroidUtilities.dp(8.0f);
                        } else if ((this.blockLayout.itemView instanceof BlockHeaderCell) || (this.blockLayout.itemView instanceof BlockSubheaderCell) || (this.blockLayout.itemView instanceof BlockTitleCell) || (this.blockLayout.itemView instanceof BlockSubtitleCell)) {
                            if (!ArticleViewer.this.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                        } else if (ArticleViewer.this.isListItemBlock(this.currentBlock.blockItem)) {
                            this.blockX = 0;
                            this.blockY = 0;
                            this.textY = 0;
                            maxWidth = width;
                            height = 0 - AndroidUtilities.dp(8.0f);
                        } else if (this.blockLayout.itemView instanceof BlockTableCell) {
                            this.blockX -= AndroidUtilities.dp(18.0f);
                            maxWidth += AndroidUtilities.dp(36.0f);
                        }
                        this.blockLayout.itemView.measure(MeasureSpec.makeMeasureSpec(maxWidth, NUM), MeasureSpec.makeMeasureSpec(0, 0));
                        if ((this.blockLayout.itemView instanceof BlockParagraphCell) && this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            BlockParagraphCell paragraphCell = this.blockLayout.itemView;
                            if (paragraphCell.textLayout != null && paragraphCell.textLayout.getLineCount() > 0) {
                                this.numOffsetY = this.currentBlock.numLayout.getLineAscent(0) - paragraphCell.textLayout.getLineAscent(0);
                            }
                        }
                        if (this.currentBlock.blockItem instanceof TL_pageBlockDetails) {
                            this.verticalAlign = true;
                            this.blockY = 0;
                            height -= AndroidUtilities.dp(8.0f);
                        } else if (this.blockLayout.itemView instanceof BlockOrderedListItemCell) {
                            this.verticalAlign = ((BlockOrderedListItemCell) this.blockLayout.itemView).verticalAlign;
                        } else if (this.blockLayout.itemView instanceof BlockListItemCell) {
                            this.verticalAlign = ((BlockListItemCell) this.blockLayout.itemView).verticalAlign;
                        }
                        if (this.verticalAlign && this.currentBlock.numLayout != null) {
                            this.textY = (this.blockLayout.itemView.getMeasuredHeight() - this.currentBlock.numLayout.getHeight()) / 2;
                        }
                        height += this.blockLayout.itemView.getMeasuredHeight();
                    }
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentBlock.parent.items.get(this.currentBlock.parent.items.size() - 1) == this.currentBlock) {
                    height += AndroidUtilities.dp(8.0f);
                }
                if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                    height += AndroidUtilities.dp(10.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (this.blockLayout != null) {
                this.blockLayout.itemView.layout(this.blockX, this.blockY, this.blockX + this.blockLayout.itemView.getMeasuredWidth(), this.blockY + this.blockLayout.itemView.getMeasuredHeight());
            }
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int width = getMeasuredWidth();
                if (this.currentBlock.numLayout != null) {
                    canvas.save();
                    if (ArticleViewer.this.isRtl) {
                        canvas.translate((float) (((width - AndroidUtilities.dp(18.0f)) - this.currentBlock.parent.maxNumWidth) - (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f))), (float) (this.textY + this.numOffsetY));
                    } else {
                        canvas.translate((float) (((AndroidUtilities.dp(18.0f) + this.currentBlock.parent.maxNumWidth) - ((int) Math.ceil((double) this.currentBlock.numLayout.getLineWidth(0)))) + (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f))), (float) (this.textY + this.numOffsetY));
                    }
                    this.currentBlock.numLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockParagraphCell extends View {
        private TL_pageBlockParagraph currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX;
        private int textY;

        public BlockParagraphCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TL_pageBlockParagraph block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                if (this.currentBlock.level == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 18));
                }
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, (width - AndroidUtilities.dp(18.0f)) - this.textX, this.textY, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, 0, this.parentAdapter);
                if (this.textLayout != null) {
                    height = this.textLayout.getHeight();
                    if (this.currentBlock.level > 0) {
                        height += AndroidUtilities.dp(8.0f);
                    } else {
                        height += AndroidUtilities.dp(16.0f);
                    }
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockPhotoCell extends FrameLayout implements FileDownloadProgressListener {
        private int TAG;
        private int buttonPressed;
        private int buttonState;
        private int buttonX;
        private int buttonY;
        private boolean cancelLoading;
        private DrawingText captionLayout;
        private BlockChannelCell channelCell;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockPhoto currentBlock;
        private PhotoSize currentPhotoObject;
        private int currentType;
        private GroupedMessagePosition groupPosition;
        private ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private boolean isLast;
        private Drawable linkDrawable;
        private WebpageAdapter parentAdapter;
        private PageBlock parentBlock;
        private boolean photoPressed;
        private RadialProgress radialProgress;
        private int textX;
        private int textY;

        public BlockPhotoCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            this.channelCell = new BlockChannelCell(context, this.parentAdapter, 1);
            this.radialProgress = new RadialProgress(this);
            this.radialProgress.setAlphaForPrevious(true);
            this.radialProgress.setProgressColor(-1);
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            addView(this.channelCell, LayoutHelper.createFrame(-1, -2.0f));
            this.currentType = type;
        }

        public void setBlock(TL_pageBlockPhoto block, boolean first, boolean last) {
            this.parentBlock = null;
            this.currentBlock = block;
            this.isFirst = first;
            this.isLast = last;
            this.channelCell.setVisibility(4);
            if (!TextUtils.isEmpty(this.currentBlock.url)) {
                this.linkDrawable = getResources().getDrawable(R.drawable.instant_link);
            }
            if (this.currentBlock != null) {
                Photo photo = ArticleViewer.this.getPhotoWithId(this.currentBlock.photo_id);
                if (photo != null) {
                    this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                } else {
                    this.currentPhotoObject = null;
                }
            } else {
                this.currentPhotoObject = null;
            }
            updateButtonState(false);
            requestLayout();
        }

        public void setParentBlock(PageBlock block) {
            this.parentBlock = block;
            if (ArticleViewer.this.channelBlock != null && (this.parentBlock instanceof TL_pageBlockCover)) {
                this.channelCell.setBlock(ArticleViewer.this.channelBlock);
                this.channelCell.setVisibility(0);
            }
        }

        public View getChannelCell() {
            return this.channelCell;
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            if (this.channelCell.getVisibility() != 0 || y <= this.channelCell.getTranslationY() || y >= this.channelCell.getTranslationY() + ((float) AndroidUtilities.dp(39.0f))) {
                boolean z;
                if (event.getAction() == 0 && this.imageView.isInsideImage(x, y)) {
                    this.photoPressed = true;
                } else if (event.getAction() == 1 && this.photoPressed) {
                    this.photoPressed = false;
                    ArticleViewer.this.openPhoto(this.currentBlock);
                } else if (event.getAction() == 3) {
                    this.photoPressed = false;
                }
                if (!this.photoPressed) {
                    if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.captionLayout, this.textX, this.textY)) {
                        if (!(ArticleViewer.this.checkLayoutForLinks(event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event))) {
                            z = false;
                            return z;
                        }
                    }
                }
                z = true;
                return z;
            } else if (ArticleViewer.this.channelBlock == null || event.getAction() != 1) {
                return true;
            } else {
                MessagesController.getInstance(ArticleViewer.this.currentAccount).openByUserName(ArticleViewer.this.channelBlock.channel.username, ArticleViewer.this.parentFragment, 2);
                ArticleViewer.this.close(false, true);
                return true;
            }
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentType == 1) {
                width = ((View) getParent()).getMeasuredWidth();
                height = ((View) getParent()).getMeasuredHeight();
            } else if (this.currentType == 2) {
                height = (int) Math.ceil((double) ((this.groupPosition.ph * ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y))) * 0.5f));
            }
            if (this.currentBlock != null) {
                int photoX;
                int textWidth;
                Photo photo = ArticleViewer.this.getPhotoWithId(this.currentBlock.photo_id);
                int size = AndroidUtilities.dp(48.0f);
                int photoWidth = width;
                int photoHeight = height;
                if (this.currentType != 0 || this.currentBlock.level <= 0) {
                    photoX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                } else {
                    photoX = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                    this.textX = photoX;
                    photoWidth -= AndroidUtilities.dp(18.0f) + photoX;
                    textWidth = photoWidth;
                }
                if (!(photo == null || this.currentPhotoObject == null)) {
                    String filter;
                    PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80, true);
                    if (this.currentPhotoObject == thumb) {
                        thumb = null;
                    }
                    if (this.currentType == 0) {
                        height = (int) (((float) this.currentPhotoObject.h) * (((float) photoWidth) / ((float) this.currentPhotoObject.w)));
                        if (this.parentBlock instanceof TL_pageBlockCover) {
                            height = Math.min(height, photoWidth);
                        } else {
                            int maxHeight = (int) (((float) (Math.max(ArticleViewer.this.listView[0].getMeasuredWidth(), ArticleViewer.this.listView[0].getMeasuredHeight()) - AndroidUtilities.dp(56.0f))) * 0.9f);
                            if (height > maxHeight) {
                                height = maxHeight;
                                photoWidth = (int) (((float) this.currentPhotoObject.w) * (((float) height) / ((float) this.currentPhotoObject.h)));
                                photoX += ((width - photoX) - photoWidth) / 2;
                            }
                        }
                        photoHeight = height;
                    } else if (this.currentType == 2) {
                        if ((this.groupPosition.flags & 2) == 0) {
                            photoWidth -= AndroidUtilities.dp(2.0f);
                        }
                        if ((this.groupPosition.flags & 8) == 0) {
                            photoHeight -= AndroidUtilities.dp(2.0f);
                        }
                        if (this.groupPosition.leftSpanOffset != 0) {
                            int offset = (int) Math.ceil((double) (((float) (this.groupPosition.leftSpanOffset * width)) / 1000.0f));
                            photoWidth -= offset;
                            photoX += offset;
                        }
                    }
                    ImageReceiver imageReceiver = this.imageView;
                    int dp = (this.isFirst || this.currentType == 1 || this.currentType == 2 || this.currentBlock.level > 0) ? 0 : AndroidUtilities.dp(8.0f);
                    imageReceiver.setImageCoords(photoX, dp, photoWidth, photoHeight);
                    if (this.currentType == 0) {
                        filter = null;
                    } else {
                        filter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoWidth), Integer.valueOf(photoHeight)});
                    }
                    this.imageView.setImage(this.currentPhotoObject, filter, thumb, "80_80_b", this.currentPhotoObject.size, null, ArticleViewer.this.currentPage, 1);
                    this.buttonX = (int) (((float) this.imageView.getImageX()) + (((float) (this.imageView.getImageWidth() - size)) / 2.0f));
                    this.buttonY = (int) (((float) this.imageView.getImageY()) + (((float) (this.imageView.getImageHeight() - size)) / 2.0f));
                    this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + size, this.buttonY + size);
                }
                if (this.currentType == 0) {
                    this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock, this.parentAdapter);
                    if (this.captionLayout != null) {
                        this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                        height += this.creditOffset + AndroidUtilities.dp(4.0f);
                    }
                    this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                    if (this.creditLayout != null) {
                        height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    }
                }
                if (!this.isFirst && this.currentType == 0 && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                boolean nextIsChannel = (this.parentBlock instanceof TL_pageBlockCover) && this.parentAdapter.blocks != null && this.parentAdapter.blocks.size() > 1 && (this.parentAdapter.blocks.get(1) instanceof TL_pageBlockChannel);
                if (!(this.currentType == 2 || nextIsChannel)) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            this.channelCell.measure(widthMeasureSpec, heightMeasureSpec);
            this.channelCell.setTranslationY((float) (this.imageView.getImageHeight() - AndroidUtilities.dp(39.0f)));
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentType == 0) {
            }
            if (this.currentBlock != null) {
                if (!(this.imageView.hasBitmapImage() && this.imageView.getCurrentAlpha() == 1.0f)) {
                    canvas.drawRect((float) this.imageView.getImageX(), (float) this.imageView.getImageY(), (float) this.imageView.getImageX2(), (float) this.imageView.getImageY2(), ArticleViewer.photoBackgroundPaint);
                }
                this.imageView.draw(canvas);
                if (this.imageView.getVisible()) {
                    this.radialProgress.draw(canvas);
                }
                if (!TextUtils.isEmpty(this.currentBlock.url)) {
                    int x = getMeasuredWidth() - AndroidUtilities.dp(35.0f);
                    int y = this.imageView.getImageY() + AndroidUtilities.dp(11.0f);
                    this.linkDrawable.setBounds(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                    this.linkDrawable.draw(canvas);
                }
                this.textY = (this.imageView.getImageY() + this.imageView.getImageHeight()) + AndroidUtilities.dp(8.0f);
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        private Drawable getDrawableForCurrentState() {
            if (this.buttonState < 0 || this.buttonState >= 6) {
                return null;
            }
            return Theme.chat_photoStatesDrawables[this.buttonState][this.buttonPressed];
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
            boolean fileExists = FileLoader.getPathToAttach(this.currentPhotoObject, true).exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setBackground(null, false, false);
            } else if (fileExists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                this.buttonState = -1;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                invalidate();
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(fileName, null, this);
                float setProgress = 0.0f;
                this.buttonState = 5;
                if (FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(fileName)) {
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    setProgress = progress != null ? progress.floatValue() : 0.0f;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                this.radialProgress.setProgress(setProgress, false);
                invalidate();
            }
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageView.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.imageView.onAttachedToWindow();
            updateButtonState(false);
        }

        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(false);
        }

        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
        }

        public void onProgressDownload(String fileName, float progress) {
            this.radialProgress.setProgress(progress, true);
            if (this.buttonState != 1) {
                updateButtonState(false);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    private class BlockPreformattedCell extends FrameLayout {
        private TL_pageBlockPreformatted currentBlock;
        private WebpageAdapter parentAdapter;
        private HorizontalScrollView scrollView;
        private View textContainer;
        private DrawingText textLayout;

        public BlockPreformattedCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            this.scrollView = new HorizontalScrollView(context, ArticleViewer.this) {
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (BlockPreformattedCell.this.textContainer.getMeasuredWidth() > getMeasuredWidth()) {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                    return super.onInterceptTouchEvent(ev);
                }

                protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                    super.onScrollChanged(l, t, oldl, oldt);
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null) {
                        ArticleViewer.this.pressedLinkOwnerLayout = null;
                        ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                }
            };
            this.scrollView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            addView(this.scrollView, LayoutHelper.createFrame(-1, -2.0f));
            this.textContainer = new View(context, ArticleViewer.this) {
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int height = 0;
                    int width = 1;
                    if (BlockPreformattedCell.this.currentBlock != null) {
                        BlockPreformattedCell.this.textLayout = ArticleViewer.this.createLayoutForText(this, null, BlockPreformattedCell.this.currentBlock.text, AndroidUtilities.dp(5000.0f), BlockPreformattedCell.this.currentBlock, BlockPreformattedCell.this.parentAdapter);
                        if (BlockPreformattedCell.this.textLayout != null) {
                            height = 0 + BlockPreformattedCell.this.textLayout.getHeight();
                            int count = BlockPreformattedCell.this.textLayout.getLineCount();
                            for (int a = 0; a < count; a++) {
                                width = Math.max((int) Math.ceil((double) BlockPreformattedCell.this.textLayout.getLineWidth(a)), width);
                            }
                        }
                    } else {
                        height = 1;
                    }
                    setMeasuredDimension(AndroidUtilities.dp(32.0f) + width, height);
                }

                protected void onDraw(Canvas canvas) {
                    if (BlockPreformattedCell.this.textLayout != null) {
                        canvas.save();
                        BlockPreformattedCell.this.textLayout.draw(canvas);
                        canvas.restore();
                    }
                }
            };
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -1);
            int dp = AndroidUtilities.dp(16.0f);
            layoutParams.rightMargin = dp;
            layoutParams.leftMargin = dp;
            dp = AndroidUtilities.dp(12.0f);
            layoutParams.bottomMargin = dp;
            layoutParams.topMargin = dp;
            this.scrollView.addView(this.textContainer, layoutParams);
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockPreformatted block) {
            this.currentBlock = block;
            this.scrollView.setScrollX(0);
            this.textContainer.requestLayout();
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            this.scrollView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(0, 0));
            setMeasuredDimension(width, this.scrollView.getMeasuredHeight());
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.drawRect(0.0f, (float) AndroidUtilities.dp(8.0f), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(8.0f)), ArticleViewer.preformattedBackgroundPaint);
            }
        }
    }

    private class BlockPullquoteCell extends View {
        private TL_pageBlockPullquote currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);
        private int textY2;

        public BlockPullquoteCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TL_pageBlockPullquote block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY)) {
                if (!(ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(event))) {
                    return false;
                }
            }
            return true;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.currentBlock, this.parentAdapter);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(8.0f) + this.textLayout.getHeight());
                }
                this.textLayout2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption, width - AndroidUtilities.dp(36.0f), this.currentBlock, this.parentAdapter);
                if (this.textLayout2 != null) {
                    this.textY2 = AndroidUtilities.dp(2.0f) + height;
                    height += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                }
                if (height != 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout2 != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY2);
                    this.textLayout2.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockRelatedArticlesCell extends View {
        private int additionalHeight;
        private TL_pageBlockRelatedArticlesChild currentBlock;
        private boolean divider;
        private boolean drawImage;
        private ImageReceiver imageView;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textOffset;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(10.0f);

        public BlockRelatedArticlesCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            this.imageView = new ImageReceiver(this);
            this.imageView.setRoundRadius(AndroidUtilities.dp(6.0f));
        }

        public void setBlock(TL_pageBlockRelatedArticlesChild block) {
            this.currentBlock = block;
            requestLayout();
        }

        @SuppressLint({"DrawAllocation", "NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            String description;
            int width = MeasureSpec.getSize(widthMeasureSpec);
            this.divider = this.currentBlock.num != this.currentBlock.parent.articles.size() + -1;
            TL_pageRelatedArticle item = (TL_pageRelatedArticle) this.currentBlock.parent.articles.get(this.currentBlock.num);
            this.additionalHeight = 0;
            if (ArticleViewer.this.selectedFontSize == 0) {
                this.additionalHeight = -AndroidUtilities.dp(4.0f);
            } else if (ArticleViewer.this.selectedFontSize == 1) {
                this.additionalHeight = -AndroidUtilities.dp(2.0f);
            } else if (ArticleViewer.this.selectedFontSize == 3) {
                this.additionalHeight = AndroidUtilities.dp(2.0f);
            } else if (ArticleViewer.this.selectedFontSize == 4) {
                this.additionalHeight = AndroidUtilities.dp(4.0f);
            }
            Photo photo = item.photo_id != 0 ? ArticleViewer.this.getPhotoWithId(item.photo_id) : null;
            if (photo != null) {
                this.drawImage = true;
                PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80, true);
                if (image == thumb) {
                    thumb = null;
                }
                this.imageView.setImage(image, "64_64", thumb, "64_64_b", image.size, null, ArticleViewer.this.currentPage, 1);
            } else {
                this.drawImage = false;
            }
            int layoutHeight = AndroidUtilities.dp(60.0f);
            int availableWidth = width - AndroidUtilities.dp(36.0f);
            if (this.drawImage) {
                int imageWidth = AndroidUtilities.dp(44.0f);
                this.imageView.setImageCoords((width - imageWidth) - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), imageWidth, imageWidth);
                availableWidth -= this.imageView.getImageWidth() + AndroidUtilities.dp(6.0f);
            }
            int height = AndroidUtilities.dp(18.0f);
            boolean isTitleRtl = false;
            if (item.title != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, item.title, null, availableWidth, this.textY, this.currentBlock, Alignment.ALIGN_NORMAL, 3, this.parentAdapter);
            }
            int lineCount = 4;
            if (this.textLayout != null) {
                int count = this.textLayout.getLineCount();
                lineCount = 4 - count;
                this.textOffset = (this.textLayout.getHeight() + AndroidUtilities.dp(6.0f)) + this.additionalHeight;
                height += this.textLayout.getHeight();
                for (int a = 0; a < count; a++) {
                    if (this.textLayout.getLineLeft(a) != 0.0f) {
                        isTitleRtl = true;
                        break;
                    }
                }
            } else {
                this.textOffset = 0;
            }
            if (item.published_date != 0 && !TextUtils.isEmpty(item.author)) {
                description = LocaleController.formatString("ArticleDateByAuthor", R.string.ArticleDateByAuthor, LocaleController.getInstance().chatFullDate.format(((long) item.published_date) * 1000), item.author);
            } else if (!TextUtils.isEmpty(item.author)) {
                description = LocaleController.formatString("ArticleByAuthor", R.string.ArticleByAuthor, item.author);
            } else if (item.published_date != 0) {
                description = LocaleController.getInstance().chatFullDate.format(((long) item.published_date) * 1000);
            } else if (TextUtils.isEmpty(item.description)) {
                description = item.url;
            } else {
                description = item.description;
            }
            ArticleViewer articleViewer = ArticleViewer.this;
            int i = this.textY + this.textOffset;
            PageBlock pageBlock = this.currentBlock;
            Alignment alignment = (ArticleViewer.this.isRtl || isTitleRtl) ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL;
            this.textLayout2 = articleViewer.createLayoutForText(this, description, null, availableWidth, i, pageBlock, alignment, lineCount, this.parentAdapter);
            if (this.textLayout2 != null) {
                height += this.textLayout2.getHeight();
                if (this.textLayout != null) {
                    height += AndroidUtilities.dp(6.0f) + this.additionalHeight;
                }
            }
            setMeasuredDimension(width, (this.divider ? 1 : 0) + Math.max(layoutHeight, height));
        }

        protected void onDraw(Canvas canvas) {
            float f = 0.0f;
            if (this.currentBlock != null) {
                if (this.drawImage) {
                    this.imageView.draw(canvas);
                }
                canvas.save();
                canvas.translate((float) this.textX, (float) AndroidUtilities.dp(10.0f));
                if (this.textLayout != null) {
                    this.textLayout.draw(canvas);
                }
                if (this.textLayout2 != null) {
                    canvas.translate(0.0f, (float) this.textOffset);
                    this.textLayout2.draw(canvas);
                }
                canvas.restore();
                if (this.divider) {
                    if (!ArticleViewer.this.isRtl) {
                        f = (float) AndroidUtilities.dp(17.0f);
                    }
                    canvas.drawLine(f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (ArticleViewer.this.isRtl ? AndroidUtilities.dp(17.0f) : 0)), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
                }
            }
        }
    }

    private class BlockRelatedArticlesHeaderCell extends View {
        private TL_pageBlockRelatedArticles currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockRelatedArticlesHeaderCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TL_pageBlockRelatedArticles block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            if (this.currentBlock != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.title, width - AndroidUtilities.dp(52.0f), 0, this.currentBlock, Alignment.ALIGN_NORMAL, 1, this.parentAdapter);
                if (this.textLayout != null) {
                    this.textY = AndroidUtilities.dp(6.0f) + ((AndroidUtilities.dp(32.0f) - this.textLayout.getHeight()) / 2);
                }
            }
            if (this.textLayout != null) {
                setMeasuredDimension(width, AndroidUtilities.dp(38.0f));
            } else {
                setMeasuredDimension(width, 1);
            }
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockRelatedArticlesShadowCell extends View {
        private CombinedDrawable shadowDrawable;

        public BlockRelatedArticlesShadowCell(Context context) {
            super(context);
            this.shadowDrawable = new CombinedDrawable(new ColorDrawable(-986896), Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, -16777216));
            this.shadowDrawable.setFullsize(true);
            setBackgroundDrawable(this.shadowDrawable);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(12.0f));
            int color = ArticleViewer.this.getSelectedColor();
            if (color == 0) {
                Theme.setCombinedDrawableColor(this.shadowDrawable, -986896, false);
            } else if (color == 1) {
                Theme.setCombinedDrawableColor(this.shadowDrawable, -1712440, false);
            } else if (color == 2) {
                Theme.setCombinedDrawableColor(this.shadowDrawable, -15000805, false);
            }
        }
    }

    private class BlockSlideshowCell extends FrameLayout {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockSlideshow currentBlock;
        private int currentPage;
        private View dotsContainer;
        private PagerAdapter innerAdapter;
        private ViewPager innerListView;
        private float pageOffset;
        private WebpageAdapter parentAdapter;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockSlideshowCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            if (ArticleViewer.dotsPaint == null) {
                ArticleViewer.dotsPaint = new Paint(1);
                ArticleViewer.dotsPaint.setColor(-1);
            }
            this.innerListView = new ViewPager(context, ArticleViewer.this) {
                public boolean onTouchEvent(MotionEvent ev) {
                    return super.onTouchEvent(ev);
                }

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    return super.onInterceptTouchEvent(ev);
                }
            };
            this.innerListView.addOnPageChangeListener(new OnPageChangeListener(ArticleViewer.this) {
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    float width = (float) BlockSlideshowCell.this.innerListView.getMeasuredWidth();
                    if (width != 0.0f) {
                        BlockSlideshowCell.this.pageOffset = (((((float) position) * width) + ((float) positionOffsetPixels)) - (((float) BlockSlideshowCell.this.currentPage) * width)) / width;
                        BlockSlideshowCell.this.dotsContainer.invalidate();
                    }
                }

                public void onPageSelected(int position) {
                    BlockSlideshowCell.this.currentPage = position;
                    BlockSlideshowCell.this.dotsContainer.invalidate();
                }

                public void onPageScrollStateChanged(int state) {
                }
            });
            ViewPager viewPager = this.innerListView;
            PagerAdapter anonymousClass3 = new PagerAdapter(ArticleViewer.this) {

                class ObjectContainer {
                    private PageBlock block;
                    private View view;

                    ObjectContainer() {
                    }
                }

                public int getCount() {
                    if (BlockSlideshowCell.this.currentBlock == null) {
                        return 0;
                    }
                    return BlockSlideshowCell.this.currentBlock.items.size();
                }

                public boolean isViewFromObject(View view, Object object) {
                    return ((ObjectContainer) object).view == view;
                }

                public int getItemPosition(Object object) {
                    if (BlockSlideshowCell.this.currentBlock.items.contains(((ObjectContainer) object).block)) {
                        return -1;
                    }
                    return -2;
                }

                public Object instantiateItem(ViewGroup container, int position) {
                    View view;
                    PageBlock block = (PageBlock) BlockSlideshowCell.this.currentBlock.items.get(position);
                    if (block instanceof TL_pageBlockPhoto) {
                        view = new BlockPhotoCell(BlockSlideshowCell.this.getContext(), BlockSlideshowCell.this.parentAdapter, 1);
                        ((BlockPhotoCell) view).setBlock((TL_pageBlockPhoto) block, true, true);
                    } else {
                        view = new BlockVideoCell(BlockSlideshowCell.this.getContext(), BlockSlideshowCell.this.parentAdapter, 1);
                        ((BlockVideoCell) view).setBlock((TL_pageBlockVideo) block, true, true);
                    }
                    container.addView(view);
                    ObjectContainer objectContainer = new ObjectContainer();
                    objectContainer.view = view;
                    objectContainer.block = block;
                    return objectContainer;
                }

                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView(((ObjectContainer) object).view);
                }

                public void unregisterDataSetObserver(DataSetObserver observer) {
                    if (observer != null) {
                        super.unregisterDataSetObserver(observer);
                    }
                }
            };
            this.innerAdapter = anonymousClass3;
            viewPager.setAdapter(anonymousClass3);
            int color = ArticleViewer.this.getSelectedColor();
            if (color == 0) {
                AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, -657673);
            } else if (color == 1) {
                AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, -659492);
            } else if (color == 2) {
                AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, -15461356);
            }
            addView(this.innerListView);
            this.dotsContainer = new View(context, ArticleViewer.this) {
                protected void onDraw(Canvas canvas) {
                    if (BlockSlideshowCell.this.currentBlock != null) {
                        int xOffset;
                        int count = BlockSlideshowCell.this.innerAdapter.getCount();
                        int totalWidth = ((AndroidUtilities.dp(7.0f) * count) + ((count - 1) * AndroidUtilities.dp(6.0f))) + AndroidUtilities.dp(4.0f);
                        if (totalWidth < getMeasuredWidth()) {
                            xOffset = (getMeasuredWidth() - totalWidth) / 2;
                        } else {
                            xOffset = AndroidUtilities.dp(4.0f);
                            int size = AndroidUtilities.dp(13.0f);
                            int halfCount = ((getMeasuredWidth() - AndroidUtilities.dp(8.0f)) / 2) / size;
                            if (BlockSlideshowCell.this.currentPage == (count - halfCount) - 1 && BlockSlideshowCell.this.pageOffset < 0.0f) {
                                xOffset -= ((int) (BlockSlideshowCell.this.pageOffset * ((float) size))) + (((count - (halfCount * 2)) - 1) * size);
                            } else if (BlockSlideshowCell.this.currentPage >= (count - halfCount) - 1) {
                                xOffset -= ((count - (halfCount * 2)) - 1) * size;
                            } else if (BlockSlideshowCell.this.currentPage > halfCount) {
                                xOffset -= ((int) (BlockSlideshowCell.this.pageOffset * ((float) size))) + ((BlockSlideshowCell.this.currentPage - halfCount) * size);
                            } else if (BlockSlideshowCell.this.currentPage == halfCount && BlockSlideshowCell.this.pageOffset > 0.0f) {
                                xOffset -= (int) (BlockSlideshowCell.this.pageOffset * ((float) size));
                            }
                        }
                        int a = 0;
                        while (a < BlockSlideshowCell.this.currentBlock.items.size()) {
                            int cx = (AndroidUtilities.dp(4.0f) + xOffset) + (AndroidUtilities.dp(13.0f) * a);
                            Drawable drawable = BlockSlideshowCell.this.currentPage == a ? ArticleViewer.this.slideDotBigDrawable : ArticleViewer.this.slideDotDrawable;
                            drawable.setBounds(cx - AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f) + cx, AndroidUtilities.dp(10.0f));
                            drawable.draw(canvas);
                            a++;
                        }
                    }
                }
            };
            addView(this.dotsContainer);
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockSlideshow block) {
            this.currentBlock = block;
            this.innerAdapter.notifyDataSetChanged();
            this.innerListView.setCurrentItem(0, false);
            this.innerListView.forceLayout();
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.captionLayout, this.textX, this.textY)) {
                if (!(ArticleViewer.this.checkLayoutForLinks(event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event))) {
                    return false;
                }
            }
            return true;
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int width = MeasureSpec.getSize(widthMeasureSpec);
            if (this.currentBlock != null) {
                height = AndroidUtilities.dp(310.0f);
                this.innerListView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                int count = this.currentBlock.items.size();
                this.dotsContainer.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f), NUM));
                int textWidth = width - AndroidUtilities.dp(36.0f);
                this.textY = AndroidUtilities.dp(16.0f) + height;
                this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock, this.parentAdapter);
                if (this.captionLayout != null) {
                    this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    height += this.creditOffset + AndroidUtilities.dp(4.0f);
                }
                this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.creditLayout != null) {
                    height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                height += AndroidUtilities.dp(16.0f);
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.innerListView.layout(0, AndroidUtilities.dp(8.0f), this.innerListView.getMeasuredWidth(), AndroidUtilities.dp(8.0f) + this.innerListView.getMeasuredHeight());
            int y = this.innerListView.getBottom() - AndroidUtilities.dp(23.0f);
            this.dotsContainer.layout(0, y, this.dotsContainer.getMeasuredWidth(), this.dotsContainer.getMeasuredHeight() + y);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockSubheaderCell extends View {
        private TL_pageBlockSubheader currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockSubheaderCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TL_pageBlockSubheader block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockSubtitleCell extends View {
        private TL_pageBlockSubtitle currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockSubtitleCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TL_pageBlockSubtitle block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockTableCell extends FrameLayout implements TableLayoutDelegate {
        private TL_pageBlockTable currentBlock;
        private boolean firstLayout;
        private boolean inLayout;
        private int listX;
        private int listY;
        private WebpageAdapter parentAdapter;
        private HorizontalScrollView scrollView;
        private TableLayout tableLayout;
        private int textX;
        private int textY;
        private DrawingText titleLayout;

        public BlockTableCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            this.scrollView = new HorizontalScrollView(context, ArticleViewer.this) {
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (BlockTableCell.this.tableLayout.getMeasuredWidth() > getMeasuredWidth() - AndroidUtilities.dp(36.0f)) {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                    return super.onInterceptTouchEvent(ev);
                }

                protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                    super.onScrollChanged(l, t, oldl, oldt);
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null) {
                        ArticleViewer.this.pressedLinkOwnerLayout = null;
                        ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                }

                protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
                    ArticleViewer.this.removePressedLink();
                    return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
                }

                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    BlockTableCell.this.tableLayout.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()) - getPaddingRight(), 0), heightMeasureSpec);
                    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), BlockTableCell.this.tableLayout.getMeasuredHeight());
                }
            };
            this.scrollView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.scrollView.setClipToPadding(false);
            addView(this.scrollView, LayoutHelper.createFrame(-1, -2.0f));
            this.tableLayout = new TableLayout(context, this);
            this.tableLayout.setOrientation(0);
            this.tableLayout.setRowOrderPreserved(true);
            this.scrollView.addView(this.tableLayout, new FrameLayout.LayoutParams(-2, -2));
            setWillNotDraw(false);
        }

        public DrawingText createTextLayout(TL_pageTableCell cell, int maxWidth) {
            if (cell == null) {
                return null;
            }
            Alignment alignment;
            if (cell.align_right) {
                alignment = Alignment.ALIGN_OPPOSITE;
            } else if (cell.align_center) {
                alignment = Alignment.ALIGN_CENTER;
            } else {
                alignment = Alignment.ALIGN_NORMAL;
            }
            return ArticleViewer.this.createLayoutForText(this, null, cell.text, maxWidth, 0, this.currentBlock, alignment, 0, this.parentAdapter);
        }

        public Paint getLinePaint() {
            return ArticleViewer.tableLinePaint;
        }

        public Paint getHalfLinePaint() {
            return ArticleViewer.tableHalfLinePaint;
        }

        public Paint getHeaderPaint() {
            return ArticleViewer.tableHeaderPaint;
        }

        public Paint getStripPaint() {
            return ArticleViewer.tableStripPaint;
        }

        public void setBlock(TL_pageBlockTable block) {
            TL_pageTableRow row;
            int size2;
            int c;
            TL_pageTableCell cell;
            this.currentBlock = block;
            int color = ArticleViewer.this.getSelectedColor();
            if (color == 0) {
                AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, -657673);
            } else if (color == 1) {
                AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, -659492);
            } else if (color == 2) {
                AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, -15461356);
            }
            this.tableLayout.removeAllChildrens();
            this.tableLayout.setDrawLines(this.currentBlock.bordered);
            this.tableLayout.setStriped(this.currentBlock.striped);
            this.tableLayout.setRtl(ArticleViewer.this.isRtl);
            int maxCols = 0;
            if (!this.currentBlock.rows.isEmpty()) {
                row = (TL_pageTableRow) this.currentBlock.rows.get(0);
                size2 = row.cells.size();
                for (c = 0; c < size2; c++) {
                    int i;
                    cell = (TL_pageTableCell) row.cells.get(c);
                    if (cell.colspan != 0) {
                        i = cell.colspan;
                    } else {
                        i = 1;
                    }
                    maxCols += i;
                }
            }
            int size = this.currentBlock.rows.size();
            for (int r = 0; r < size; r++) {
                row = (TL_pageTableRow) this.currentBlock.rows.get(r);
                int cols = 0;
                size2 = row.cells.size();
                for (c = 0; c < size2; c++) {
                    int colspan;
                    int rowspan;
                    cell = (TL_pageTableCell) row.cells.get(c);
                    if (cell.colspan != 0) {
                        colspan = cell.colspan;
                    } else {
                        colspan = 1;
                    }
                    if (cell.rowspan != 0) {
                        rowspan = cell.rowspan;
                    } else {
                        rowspan = 1;
                    }
                    if (cell.text != null) {
                        this.tableLayout.addChild(cell, cols, r, colspan);
                    } else {
                        this.tableLayout.addChild(cols, r, colspan, rowspan);
                    }
                    cols += colspan;
                }
            }
            this.tableLayout.setColumnCount(maxCols);
            this.firstLayout = true;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            int N = this.tableLayout.getChildCount();
            for (int i = 0; i < N; i++) {
                Child c = this.tableLayout.getChildAt(i);
                if (ArticleViewer.this.checkLayoutForLinks(event, this, c.textLayout, ((this.scrollView.getPaddingLeft() - this.scrollView.getScrollX()) + this.listX) + c.getTextX(), this.listY + c.getTextY())) {
                    return true;
                }
            }
            boolean z = ArticleViewer.this.checkLayoutForLinks(event, this, this.titleLayout, this.textX, this.textY) || super.onTouchEvent(event);
            return z;
        }

        public void invalidate() {
            super.invalidate();
            this.tableLayout.invalidate();
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.inLayout = true;
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                int textWidth;
                if (this.currentBlock.level > 0) {
                    this.listX = AndroidUtilities.dp((float) (this.currentBlock.level * 14));
                    this.textX = this.listX + AndroidUtilities.dp(18.0f);
                    textWidth = width - this.textX;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                }
                this.titleLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.title, textWidth, 0, this.currentBlock, Alignment.ALIGN_CENTER, 0, this.parentAdapter);
                if (this.titleLayout != null) {
                    this.textY = 0;
                    height = 0 + (this.titleLayout.getHeight() + AndroidUtilities.dp(8.0f));
                    this.listY = height;
                } else {
                    this.listY = AndroidUtilities.dp(8.0f);
                }
                this.scrollView.measure(MeasureSpec.makeMeasureSpec(width - this.listX, NUM), MeasureSpec.makeMeasureSpec(0, 0));
                height += this.scrollView.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            this.inLayout = false;
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.scrollView.layout(this.listX, this.listY, this.listX + this.scrollView.getMeasuredWidth(), this.listY + this.scrollView.getMeasuredHeight());
            if (this.firstLayout) {
                if (ArticleViewer.this.isRtl) {
                    this.scrollView.setScrollX((this.tableLayout.getMeasuredWidth() - this.scrollView.getMeasuredWidth()) + AndroidUtilities.dp(36.0f));
                } else {
                    this.scrollView.setScrollX(0);
                }
                this.firstLayout = false;
            }
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.titleLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.titleLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockTitleCell extends View {
        private TL_pageBlockTitle currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockTitleCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TL_pageBlockTitle block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                }
                if (this.currentBlock.first) {
                    height += AndroidUtilities.dp(8.0f);
                    this.textY = AndroidUtilities.dp(16.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockVideoCell extends FrameLayout implements FileDownloadProgressListener {
        private int TAG;
        private int buttonPressed;
        private int buttonState;
        private int buttonX;
        private int buttonY;
        private boolean cancelLoading;
        private DrawingText captionLayout;
        private BlockChannelCell channelCell;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockVideo currentBlock;
        private Document currentDocument;
        private int currentType;
        private GroupedMessagePosition groupPosition;
        private ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private boolean isGif;
        private boolean isLast;
        private WebpageAdapter parentAdapter;
        private PageBlock parentBlock;
        private boolean photoPressed;
        private RadialProgress radialProgress;
        private int textX;
        private int textY;

        public BlockVideoCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            this.imageView.setNeedsQualityThumb(true);
            this.imageView.setShouldGenerateQualityThumb(true);
            this.currentType = type;
            this.radialProgress = new RadialProgress(this);
            this.radialProgress.setAlphaForPrevious(true);
            this.radialProgress.setProgressColor(-1);
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            this.channelCell = new BlockChannelCell(context, this.parentAdapter, 1);
            addView(this.channelCell, LayoutHelper.createFrame(-1, -2.0f));
        }

        public void setBlock(TL_pageBlockVideo block, boolean first, boolean last) {
            this.currentBlock = block;
            this.parentBlock = null;
            this.cancelLoading = false;
            this.currentDocument = ArticleViewer.this.getDocumentWithId(this.currentBlock.video_id);
            this.isGif = MessageObject.isGifDocument(this.currentDocument);
            this.isFirst = first;
            this.isLast = last;
            this.channelCell.setVisibility(4);
            updateButtonState(false);
            requestLayout();
        }

        public void setParentBlock(PageBlock block) {
            this.parentBlock = block;
            if (ArticleViewer.this.channelBlock != null && (this.parentBlock instanceof TL_pageBlockCover)) {
                this.channelCell.setBlock(ArticleViewer.this.channelBlock);
                this.channelCell.setVisibility(0);
            }
        }

        public View getChannelCell() {
            return this.channelCell;
        }

        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            if (this.channelCell.getVisibility() != 0 || y <= this.channelCell.getTranslationY() || y >= this.channelCell.getTranslationY() + ((float) AndroidUtilities.dp(39.0f))) {
                boolean z;
                if (event.getAction() == 0 && this.imageView.isInsideImage(x, y)) {
                    if ((this.buttonState == -1 || x < ((float) this.buttonX) || x > ((float) (this.buttonX + AndroidUtilities.dp(48.0f))) || y < ((float) this.buttonY) || y > ((float) (this.buttonY + AndroidUtilities.dp(48.0f)))) && this.buttonState != 0) {
                        this.photoPressed = true;
                    } else {
                        this.buttonPressed = 1;
                        invalidate();
                    }
                } else if (event.getAction() == 1) {
                    if (this.photoPressed) {
                        this.photoPressed = false;
                        ArticleViewer.this.openPhoto(this.currentBlock);
                    } else if (this.buttonPressed == 1) {
                        this.buttonPressed = 0;
                        playSoundEffect(0);
                        didPressedButton(false);
                        this.radialProgress.swapBackground(getDrawableForCurrentState());
                        invalidate();
                    }
                } else if (event.getAction() == 3) {
                    this.photoPressed = false;
                }
                if (!this.photoPressed && this.buttonPressed == 0) {
                    if (!ArticleViewer.this.checkLayoutForLinks(event, this, this.captionLayout, this.textX, this.textY)) {
                        if (!(ArticleViewer.this.checkLayoutForLinks(event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event))) {
                            z = false;
                            return z;
                        }
                    }
                }
                z = true;
                return z;
            } else if (ArticleViewer.this.channelBlock == null || event.getAction() != 1) {
                return true;
            } else {
                MessagesController.getInstance(ArticleViewer.this.currentAccount).openByUserName(ArticleViewer.this.channelBlock.channel.username, ArticleViewer.this.parentFragment, 2);
                ArticleViewer.this.close(false, true);
                return true;
            }
        }

        @SuppressLint({"NewApi"})
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentType == 1) {
                width = ((View) getParent()).getMeasuredWidth();
                height = ((View) getParent()).getMeasuredHeight();
            } else if (this.currentType == 2) {
                height = (int) Math.ceil((double) ((this.groupPosition.ph * ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y))) * 0.5f));
            }
            if (this.currentBlock != null) {
                int photoX;
                int textWidth;
                int photoWidth = width;
                int photoHeight = height;
                if (this.currentType != 0 || this.currentBlock.level <= 0) {
                    photoX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                } else {
                    photoX = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                    this.textX = photoX;
                    photoWidth -= AndroidUtilities.dp(18.0f) + photoX;
                    textWidth = photoWidth;
                }
                if (this.currentDocument != null) {
                    int size = AndroidUtilities.dp(48.0f);
                    PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(this.currentDocument.thumbs, 48);
                    if (this.currentType == 0) {
                        boolean found = false;
                        int count = this.currentDocument.attributes.size();
                        for (int a = 0; a < count; a++) {
                            DocumentAttribute attribute = (DocumentAttribute) this.currentDocument.attributes.get(a);
                            if (attribute instanceof TL_documentAttributeVideo) {
                                height = (int) (((float) attribute.h) * (((float) photoWidth) / ((float) attribute.w)));
                                found = true;
                                break;
                            }
                        }
                        float w = thumb != null ? (float) thumb.w : 100.0f;
                        float h = thumb != null ? (float) thumb.h : 100.0f;
                        if (!found) {
                            height = (int) ((((float) photoWidth) / w) * h);
                        }
                        if (this.parentBlock instanceof TL_pageBlockCover) {
                            height = Math.min(height, photoWidth);
                        } else {
                            int maxHeight = (int) (((float) (Math.max(ArticleViewer.this.listView[0].getMeasuredWidth(), ArticleViewer.this.listView[0].getMeasuredHeight()) - AndroidUtilities.dp(56.0f))) * 0.9f);
                            if (height > maxHeight) {
                                height = maxHeight;
                                photoWidth = (int) ((((float) height) / h) * w);
                                photoX += ((width - photoX) - photoWidth) / 2;
                            }
                        }
                        if (height == 0) {
                            height = AndroidUtilities.dp(100.0f);
                        } else if (height < size) {
                            height = size;
                        }
                        photoHeight = height;
                    } else if (this.currentType == 2) {
                        if ((this.groupPosition.flags & 2) == 0) {
                            photoWidth -= AndroidUtilities.dp(2.0f);
                        }
                        if ((this.groupPosition.flags & 8) == 0) {
                            photoHeight -= AndroidUtilities.dp(2.0f);
                        }
                    }
                    this.imageView.setQualityThumbDocument(this.currentDocument);
                    ImageReceiver imageReceiver = this.imageView;
                    int dp = (this.isFirst || this.currentType == 1 || this.currentType == 2 || this.currentBlock.level > 0) ? 0 : AndroidUtilities.dp(8.0f);
                    imageReceiver.setImageCoords(photoX, dp, photoWidth, photoHeight);
                    if (this.isGif) {
                        String filter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoWidth), Integer.valueOf(photoHeight)});
                        this.imageView.setImage(this.currentDocument, null, null, null, null, thumb, "80_80_b", this.currentDocument.size, null, ArticleViewer.this.currentPage, 1);
                    } else {
                        this.imageView.setImage(null, null, thumb, "80_80_b", 0, null, ArticleViewer.this.currentPage, 1);
                    }
                    this.imageView.setAspectFit(true);
                    this.buttonX = (int) (((float) this.imageView.getImageX()) + (((float) (this.imageView.getImageWidth() - size)) / 2.0f));
                    this.buttonY = (int) (((float) this.imageView.getImageY()) + (((float) (this.imageView.getImageHeight() - size)) / 2.0f));
                    this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + size, this.buttonY + size);
                }
                if (this.currentType == 0) {
                    this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock, this.parentAdapter);
                    if (this.captionLayout != null) {
                        this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                        height += this.creditOffset + AndroidUtilities.dp(4.0f);
                    }
                    this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL, this.parentAdapter);
                    if (this.creditLayout != null) {
                        height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    }
                }
                if (!this.isFirst && this.currentType == 0 && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                boolean nextIsChannel = (this.parentBlock instanceof TL_pageBlockCover) && this.parentAdapter.blocks.size() > 1 && (this.parentAdapter.blocks.get(1) instanceof TL_pageBlockChannel);
                if (!(this.currentType == 2 || nextIsChannel)) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            this.channelCell.measure(widthMeasureSpec, heightMeasureSpec);
            this.channelCell.setTranslationY((float) (this.imageView.getImageHeight() - AndroidUtilities.dp(39.0f)));
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentType == 0) {
            }
            if (this.currentBlock != null) {
                if (!(this.imageView.hasBitmapImage() && this.imageView.getCurrentAlpha() == 1.0f)) {
                    canvas.drawRect(this.imageView.getDrawRegion(), ArticleViewer.photoBackgroundPaint);
                }
                this.imageView.draw(canvas);
                if (this.imageView.getVisible()) {
                    this.radialProgress.draw(canvas);
                }
                this.textY = (this.imageView.getImageY() + this.imageView.getImageHeight()) + AndroidUtilities.dp(8.0f);
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        private Drawable getDrawableForCurrentState() {
            if (this.buttonState < 0 || this.buttonState >= 4) {
                return null;
            }
            return Theme.chat_photoStatesDrawables[this.buttonState][this.buttonPressed];
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentDocument);
            boolean fileExists = FileLoader.getPathToAttach(this.currentDocument, true).exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setBackground(null, false, false);
            } else if (fileExists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                if (this.isGif) {
                    this.buttonState = -1;
                } else {
                    this.buttonState = 3;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                invalidate();
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(fileName, null, this);
                float setProgress = 0.0f;
                boolean progressVisible = false;
                if (FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(fileName)) {
                    progressVisible = true;
                    this.buttonState = 1;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    setProgress = progress != null ? progress.floatValue() : 0.0f;
                } else if (this.cancelLoading || !this.isGif) {
                    this.buttonState = 0;
                } else {
                    progressVisible = true;
                    this.buttonState = 1;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), progressVisible, animated);
                this.radialProgress.setProgress(setProgress, false);
                invalidate();
            }
        }

        private void didPressedButton(boolean animated) {
            if (this.buttonState == 0) {
                this.cancelLoading = false;
                this.radialProgress.setProgress(0.0f, false);
                if (this.isGif) {
                    this.imageView.setImage(this.currentDocument, null, FileLoader.getClosestPhotoSizeWithSize(this.currentDocument.thumbs, 90), "80_80_b", this.currentDocument.size, null, ArticleViewer.this.currentPage, 1);
                } else {
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).loadFile(this.currentDocument, ArticleViewer.this.currentPage, 1, 1);
                }
                this.buttonState = 1;
                this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                invalidate();
            } else if (this.buttonState == 1) {
                this.cancelLoading = true;
                if (this.isGif) {
                    this.imageView.cancelLoadImage();
                } else {
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).cancelLoadFile(this.currentDocument);
                }
                this.buttonState = 0;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                invalidate();
            } else if (this.buttonState == 2) {
                this.imageView.setAllowStartAnimation(true);
                this.imageView.startAnimation();
                this.buttonState = -1;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
            } else if (this.buttonState == 3) {
                ArticleViewer.this.openPhoto(this.currentBlock);
            }
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageView.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.imageView.onAttachedToWindow();
            updateButtonState(false);
        }

        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(false);
        }

        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            if (this.isGif) {
                this.buttonState = 2;
                didPressedButton(true);
                return;
            }
            updateButtonState(true);
        }

        public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
        }

        public void onProgressDownload(String fileName, float progress) {
            this.radialProgress.setProgress(progress, true);
            if (this.buttonState != 1) {
                updateButtonState(false);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
        }

        public void run() {
            if (ArticleViewer.this.checkingForLongPress && ArticleViewer.this.windowView != null) {
                ArticleViewer.this.checkingForLongPress = false;
                if (ArticleViewer.this.pressedLink != null) {
                    ArticleViewer.this.windowView.performHapticFeedback(0);
                    ArticleViewer.this.showCopyPopup(ArticleViewer.this.pressedLink.getUrl());
                    ArticleViewer.this.pressedLink = null;
                    ArticleViewer.this.pressedLinkOwnerLayout = null;
                    if (ArticleViewer.this.pressedLinkOwnerView != null) {
                        ArticleViewer.this.pressedLinkOwnerView.invalidate();
                    }
                } else if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLinkOwnerView != null) {
                    ArticleViewer.this.windowView.performHapticFeedback(0);
                    int[] location = new int[2];
                    ArticleViewer.this.pressedLinkOwnerView.getLocationInWindow(location);
                    int y = (location[1] + ArticleViewer.this.pressedLayoutY) - AndroidUtilities.dp(54.0f);
                    if (y < 0) {
                        y = 0;
                    }
                    ArticleViewer.this.pressedLinkOwnerView.invalidate();
                    ArticleViewer.this.drawBlockSelection = true;
                    ArticleViewer.this.showPopup(ArticleViewer.this.pressedLinkOwnerView, 48, 0, y);
                    ArticleViewer.this.listView[0].setLayoutFrozen(true);
                    ArticleViewer.this.listView[0].setLayoutFrozen(false);
                }
            }
        }
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        /* synthetic */ CheckForTap(ArticleViewer x0, AnonymousClass1 x1) {
            this();
        }

        public void run() {
            if (ArticleViewer.this.pendingCheckForLongPress == null) {
                ArticleViewer.this.pendingCheckForLongPress = new CheckForLongPress();
            }
            ArticleViewer.this.pendingCheckForLongPress.currentPressCount = ArticleViewer.access$1104(ArticleViewer.this);
            if (ArticleViewer.this.windowView != null) {
                ArticleViewer.this.windowView.postDelayed(ArticleViewer.this.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
            }
        }
    }

    public class ColorCell extends FrameLayout {
        private int currentColor;
        private boolean selected;
        private TextView textView;
        final /* synthetic */ ArticleViewer this$0;

        public ColorCell(ArticleViewer this$0, Context context) {
            int i;
            int i2 = 5;
            this.this$0 = this$0;
            super(context);
            if (ArticleViewer.colorPaint == null) {
                ArticleViewer.colorPaint = new Paint(1);
                ArticleViewer.selectorPaint = new Paint(1);
                ArticleViewer.selectorPaint.setColor(-15428119);
                ArticleViewer.selectorPaint.setStyle(Style.STROKE);
                ArticleViewer.selectorPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            }
            setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 2));
            setWillNotDraw(false);
            this.textView = new TextView(context);
            this.textView.setTextColor(-14606047);
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            TextView textView = this.textView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            textView.setGravity(i | 16);
            this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0f));
            View view = this.textView;
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            addView(view, LayoutHelper.createFrame(-1, -1.0f, i2 | 48, (float) (LocaleController.isRTL ? 17 : 53), 0.0f, (float) (LocaleController.isRTL ? 53 : 17), 0.0f));
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }

        public void setTextAndColor(String text, int color) {
            this.textView.setText(text);
            this.currentColor = color;
            invalidate();
        }

        public void select(boolean value) {
            if (this.selected != value) {
                this.selected = value;
                invalidate();
            }
        }

        protected void onDraw(Canvas canvas) {
            ArticleViewer.colorPaint.setColor(this.currentColor);
            canvas.drawCircle(!LocaleController.isRTL ? (float) AndroidUtilities.dp(28.0f) : (float) (getMeasuredWidth() - AndroidUtilities.dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(10.0f), ArticleViewer.colorPaint);
            if (this.selected) {
                ArticleViewer.selectorPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
                ArticleViewer.selectorPaint.setColor(-15428119);
                canvas.drawCircle(!LocaleController.isRTL ? (float) AndroidUtilities.dp(28.0f) : (float) (getMeasuredWidth() - AndroidUtilities.dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(10.0f), ArticleViewer.selectorPaint);
            } else if (this.currentColor == -1) {
                ArticleViewer.selectorPaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
                ArticleViewer.selectorPaint.setColor(-4539718);
                canvas.drawCircle(!LocaleController.isRTL ? (float) AndroidUtilities.dp(28.0f) : (float) (getMeasuredWidth() - AndroidUtilities.dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(9.0f), ArticleViewer.selectorPaint);
            }
        }
    }

    public class DrawingText {
        public LinkPath markPath;
        public StaticLayout textLayout;
        public LinkPath textPath;

        public void draw(Canvas canvas) {
            if (this.textPath != null) {
                canvas.drawPath(this.textPath, ArticleViewer.webpageUrlPaint);
            }
            if (this.markPath != null) {
                canvas.drawPath(this.markPath, ArticleViewer.webpageMarkPaint);
            }
            ArticleViewer.this.drawLayoutLink(canvas, this);
            this.textLayout.draw(canvas);
        }

        public CharSequence getText() {
            return this.textLayout.getText();
        }

        public int getLineCount() {
            return this.textLayout.getLineCount();
        }

        public int getLineAscent(int line) {
            return this.textLayout.getLineAscent(line);
        }

        public float getLineLeft(int line) {
            return this.textLayout.getLineLeft(line);
        }

        public float getLineWidth(int line) {
            return this.textLayout.getLineWidth(line);
        }

        public int getHeight() {
            return this.textLayout.getHeight();
        }

        public int getWidth() {
            return this.textLayout.getWidth();
        }
    }

    public class FontCell extends FrameLayout {
        private TextView textView;
        private TextView textView2;
        final /* synthetic */ ArticleViewer this$0;

        public FontCell(ArticleViewer this$0, Context context) {
            int i;
            int i2;
            int i3 = 5;
            this.this$0 = this$0;
            super(context);
            setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 2));
            this.textView = new TextView(context);
            this.textView.setTextColor(-14606047);
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            View view = this.textView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            addView(view, LayoutHelper.createFrame(-1, -1.0f, i | 48, (float) (LocaleController.isRTL ? 17 : 53), 0.0f, (float) (LocaleController.isRTL ? 53 : 17), 0.0f));
            this.textView2 = new TextView(context);
            this.textView2.setTextColor(-14606047);
            this.textView2.setTextSize(1, 16.0f);
            this.textView2.setLines(1);
            this.textView2.setMaxLines(1);
            this.textView2.setSingleLine(true);
            this.textView2.setText("Aa");
            TextView textView = this.textView2;
            if (LocaleController.isRTL) {
                i2 = 5;
            } else {
                i2 = 3;
            }
            textView.setGravity(i2 | 16);
            view = this.textView2;
            if (!LocaleController.isRTL) {
                i3 = 3;
            }
            addView(view, LayoutHelper.createFrame(-1, -1.0f, i3 | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }

        public void select(boolean value) {
            this.textView2.setTextColor(value ? -15428119 : -14606047);
        }

        public void setTextAndTypeface(String text, Typeface typeface) {
            this.textView.setText(text);
            this.textView.setTypeface(typeface);
            this.textView2.setTypeface(typeface);
            invalidate();
        }
    }

    private class PhotoBackgroundDrawable extends ColorDrawable {
        private Runnable drawRunnable;

        public PhotoBackgroundDrawable(int color) {
            super(color);
        }

        @Keep
        public void setAlpha(int alpha) {
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                DrawerLayoutContainer drawerLayoutContainer = ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer;
                boolean z = (ArticleViewer.this.isPhotoVisible && alpha == 255) ? false : true;
                drawerLayoutContainer.setAllowDrawContent(z);
            }
            super.setAlpha(alpha);
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (getAlpha() != 0 && this.drawRunnable != null) {
                this.drawRunnable.run();
                this.drawRunnable = null;
            }
        }
    }

    public static class PlaceProviderObject {
        public int clipBottomAddition;
        public int clipTopAddition;
        public ImageReceiver imageReceiver;
        public int index;
        public View parentView;
        public int radius;
        public float scale = 1.0f;
        public int size;
        public BitmapHolder thumb;
        public int viewX;
        public int viewY;
    }

    private class RadialProgressView {
        private float alpha = 1.0f;
        private float animatedAlphaValue = 1.0f;
        private float animatedProgressValue = 0.0f;
        private float animationProgressStart = 0.0f;
        private int backgroundState = -1;
        private float currentProgress = 0.0f;
        private long currentProgressTime = 0;
        private long lastUpdateTime = 0;
        private View parent;
        private int previousBackgroundState = -2;
        private RectF progressRect = new RectF();
        private float radOffset = 0.0f;
        private float scale = 1.0f;
        private int size = AndroidUtilities.dp(64.0f);

        public RadialProgressView(Context context, View parentView) {
            if (ArticleViewer.decelerateInterpolator == null) {
                ArticleViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                ArticleViewer.progressPaint = new Paint(1);
                ArticleViewer.progressPaint.setStyle(Style.STROKE);
                ArticleViewer.progressPaint.setStrokeCap(Cap.ROUND);
                ArticleViewer.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
                ArticleViewer.progressPaint.setColor(-1);
            }
            this.parent = parentView;
        }

        private void updateAnimation() {
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            if (this.animatedProgressValue != 1.0f) {
                this.radOffset += ((float) (360 * dt)) / 3000.0f;
                float progressDiff = this.currentProgress - this.animationProgressStart;
                if (progressDiff > 0.0f) {
                    this.currentProgressTime += dt;
                    if (this.currentProgressTime >= 300) {
                        this.animatedProgressValue = this.currentProgress;
                        this.animationProgressStart = this.currentProgress;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = this.animationProgressStart + (ArticleViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f) * progressDiff);
                    }
                }
                this.parent.invalidate();
            }
            if (this.animatedProgressValue >= 1.0f && this.previousBackgroundState != -2) {
                this.animatedAlphaValue -= ((float) dt) / 200.0f;
                if (this.animatedAlphaValue <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                    this.previousBackgroundState = -2;
                }
                this.parent.invalidate();
            }
        }

        public void setProgress(float value, boolean animated) {
            if (animated) {
                this.animationProgressStart = this.animatedProgressValue;
            } else {
                this.animatedProgressValue = value;
                this.animationProgressStart = value;
            }
            this.currentProgress = value;
            this.currentProgressTime = 0;
        }

        public void setBackgroundState(int state, boolean animated) {
            this.lastUpdateTime = System.currentTimeMillis();
            if (!animated || this.backgroundState == state) {
                this.previousBackgroundState = -2;
            } else {
                this.previousBackgroundState = this.backgroundState;
                this.animatedAlphaValue = 1.0f;
            }
            this.backgroundState = state;
            this.parent.invalidate();
        }

        public void setAlpha(float value) {
            this.alpha = value;
        }

        public void setScale(float value) {
            this.scale = value;
        }

        public void onDraw(Canvas canvas) {
            Drawable drawable;
            int sizeScaled = (int) (((float) this.size) * this.scale);
            int x = (ArticleViewer.this.getContainerViewWidth() - sizeScaled) / 2;
            int y = (ArticleViewer.this.getContainerViewHeight() - sizeScaled) / 2;
            if (this.previousBackgroundState >= 0 && this.previousBackgroundState < 4) {
                drawable = ArticleViewer.progressDrawables[this.previousBackgroundState];
                if (drawable != null) {
                    drawable.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                    drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable.draw(canvas);
                }
            }
            if (this.backgroundState >= 0 && this.backgroundState < 4) {
                drawable = ArticleViewer.progressDrawables[this.backgroundState];
                if (drawable != null) {
                    if (this.previousBackgroundState != -2) {
                        drawable.setAlpha((int) (((1.0f - this.animatedAlphaValue) * 255.0f) * this.alpha));
                    } else {
                        drawable.setAlpha((int) (this.alpha * 255.0f));
                    }
                    drawable.setBounds(x, y, x + sizeScaled, y + sizeScaled);
                    drawable.draw(canvas);
                }
            }
            if (this.backgroundState == 0 || this.backgroundState == 1 || this.previousBackgroundState == 0 || this.previousBackgroundState == 1) {
                int diff = AndroidUtilities.dp(4.0f);
                if (this.previousBackgroundState != -2) {
                    ArticleViewer.progressPaint.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                } else {
                    ArticleViewer.progressPaint.setAlpha((int) (this.alpha * 255.0f));
                }
                this.progressRect.set((float) (x + diff), (float) (y + diff), (float) ((x + sizeScaled) - diff), (float) ((y + sizeScaled) - diff));
                canvas.drawArc(this.progressRect, this.radOffset - 0.049804688f, Math.max(4.0f, 360.0f * this.animatedProgressValue), false, ArticleViewer.progressPaint);
                updateAnimation();
            }
        }
    }

    public class ScrollEvaluator extends IntEvaluator {
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            return super.evaluate(fraction, startValue, endValue);
        }
    }

    private class SizeChooseView extends View {
        private int circleSize;
        private int gapSize;
        private int lineSize;
        private boolean moving;
        private Paint paint = new Paint(1);
        private int sideSide;
        private boolean startMoving;
        private int startMovingQuality;
        private float startX;

        public SizeChooseView(Context context) {
            super(context);
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean z = false;
            float x = event.getX();
            int a;
            int cx;
            if (event.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                a = 0;
                while (a < 5) {
                    cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                    if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                        a++;
                    } else {
                        if (a == ArticleViewer.this.selectedFontSize) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingQuality = ArticleViewer.this.selectedFontSize;
                    }
                }
            } else if (event.getAction() == 2) {
                if (this.startMoving) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving) {
                    a = 0;
                    while (a < 5) {
                        cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                        int diff = ((this.lineSize / 2) + (this.circleSize / 2)) + this.gapSize;
                        if (x <= ((float) (cx - diff)) || x >= ((float) (cx + diff))) {
                            a++;
                        } else if (ArticleViewer.this.selectedFontSize != a) {
                            ArticleViewer.this.selectedFontSize = a;
                            ArticleViewer.this.updatePaintSize();
                            invalidate();
                        }
                    }
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (!this.moving) {
                    a = 0;
                    while (a < 5) {
                        cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                        if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                            a++;
                        } else if (ArticleViewer.this.selectedFontSize != a) {
                            ArticleViewer.this.selectedFontSize = a;
                            ArticleViewer.this.updatePaintSize();
                            invalidate();
                        }
                    }
                } else if (ArticleViewer.this.selectedFontSize != this.startMovingQuality) {
                    ArticleViewer.this.updatePaintSize();
                }
                this.startMoving = false;
                this.moving = false;
            }
            return true;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            this.circleSize = AndroidUtilities.dp(5.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(17.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * 5)) - ((this.gapSize * 2) * 4)) - (this.sideSide * 2)) / 4;
        }

        protected void onDraw(Canvas canvas) {
            int cy = getMeasuredHeight() / 2;
            int a = 0;
            while (a < 5) {
                int cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                if (a <= ArticleViewer.this.selectedFontSize) {
                    this.paint.setColor(-15428119);
                } else {
                    this.paint.setColor(-3355444);
                }
                canvas.drawCircle((float) cx, (float) cy, a == ArticleViewer.this.selectedFontSize ? (float) AndroidUtilities.dp(4.0f) : (float) (this.circleSize / 2), this.paint);
                if (a != 0) {
                    int x = ((cx - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    canvas.drawRect((float) x, (float) (cy - AndroidUtilities.dp(1.0f)), (float) (this.lineSize + x), (float) (AndroidUtilities.dp(1.0f) + cy), this.paint);
                }
                a++;
            }
        }
    }

    private class TL_pageBlockDetailsBottom extends PageBlock {
        private TL_pageBlockDetails parent;

        private TL_pageBlockDetailsBottom() {
        }
    }

    private class TL_pageBlockDetailsChild extends PageBlock {
        private PageBlock block;
        private PageBlock parent;

        private TL_pageBlockDetailsChild() {
        }

        /* synthetic */ TL_pageBlockDetailsChild(ArticleViewer x0, AnonymousClass1 x1) {
            this();
        }
    }

    private class TL_pageBlockListItem extends PageBlock {
        private PageBlock blockItem;
        private int index;
        private String num;
        private DrawingText numLayout;
        private TL_pageBlockListParent parent;
        private RichText textItem;

        private TL_pageBlockListItem() {
            this.index = Integer.MAX_VALUE;
        }

        /* synthetic */ TL_pageBlockListItem(ArticleViewer x0, AnonymousClass1 x1) {
            this();
        }
    }

    private class TL_pageBlockListParent extends PageBlock {
        private ArrayList<TL_pageBlockListItem> items;
        private int lastFontSize;
        private int lastMaxNumCalcWidth;
        private int level;
        private int maxNumWidth;
        private TL_pageBlockList pageBlockList;

        private TL_pageBlockListParent() {
            this.items = new ArrayList();
        }

        /* synthetic */ TL_pageBlockListParent(ArticleViewer x0, AnonymousClass1 x1) {
            this();
        }
    }

    private class TL_pageBlockOrderedListItem extends PageBlock {
        private PageBlock blockItem;
        private int index;
        private String num;
        private DrawingText numLayout;
        private TL_pageBlockOrderedListParent parent;
        private RichText textItem;

        private TL_pageBlockOrderedListItem() {
            this.index = Integer.MAX_VALUE;
        }

        /* synthetic */ TL_pageBlockOrderedListItem(ArticleViewer x0, AnonymousClass1 x1) {
            this();
        }
    }

    private class TL_pageBlockOrderedListParent extends PageBlock {
        private ArrayList<TL_pageBlockOrderedListItem> items;
        private int lastFontSize;
        private int lastMaxNumCalcWidth;
        private int level;
        private int maxNumWidth;
        private TL_pageBlockOrderedList pageBlockOrderedList;

        private TL_pageBlockOrderedListParent() {
            this.items = new ArrayList();
        }

        /* synthetic */ TL_pageBlockOrderedListParent(ArticleViewer x0, AnonymousClass1 x1) {
            this();
        }
    }

    private class TL_pageBlockRelatedArticlesChild extends PageBlock {
        private int num;
        private TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesChild() {
        }

        /* synthetic */ TL_pageBlockRelatedArticlesChild(ArticleViewer x0, AnonymousClass1 x1) {
            this();
        }
    }

    private class TL_pageBlockRelatedArticlesShadow extends PageBlock {
        private TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesShadow() {
        }

        /* synthetic */ TL_pageBlockRelatedArticlesShadow(ArticleViewer x0, AnonymousClass1 x1) {
            this();
        }
    }

    private class WebpageAdapter extends SelectionAdapter {
        private HashMap<String, Integer> anchors = new HashMap();
        private HashMap<String, Integer> anchorsOffset = new HashMap();
        private HashMap<String, TL_textAnchor> anchorsParent = new HashMap();
        private HashMap<TL_pageBlockAudio, MessageObject> audioBlocks = new HashMap();
        private ArrayList<MessageObject> audioMessages = new ArrayList();
        private ArrayList<PageBlock> blocks = new ArrayList();
        private Context context;
        private ArrayList<PageBlock> localBlocks = new ArrayList();
        private ArrayList<PageBlock> photoBlocks = new ArrayList();

        public WebpageAdapter(Context ctx) {
            this.context = ctx;
        }

        private void setRichTextParents(RichText parentRichText, RichText richText) {
            if (richText != null) {
                richText.parentRichText = parentRichText;
                if (richText instanceof TL_textFixed) {
                    setRichTextParents(richText, ((TL_textFixed) richText).text);
                } else if (richText instanceof TL_textItalic) {
                    setRichTextParents(richText, ((TL_textItalic) richText).text);
                } else if (richText instanceof TL_textBold) {
                    setRichTextParents(richText, ((TL_textBold) richText).text);
                } else if (richText instanceof TL_textUnderline) {
                    setRichTextParents(richText, ((TL_textUnderline) richText).text);
                } else if (richText instanceof TL_textStrike) {
                    setRichTextParents(parentRichText, ((TL_textStrike) richText).text);
                } else if (richText instanceof TL_textEmail) {
                    setRichTextParents(richText, ((TL_textEmail) richText).text);
                } else if (richText instanceof TL_textPhone) {
                    setRichTextParents(richText, ((TL_textPhone) richText).text);
                } else if (richText instanceof TL_textUrl) {
                    setRichTextParents(richText, ((TL_textUrl) richText).text);
                } else if (richText instanceof TL_textConcat) {
                    int count = richText.texts.size();
                    for (int a = 0; a < count; a++) {
                        setRichTextParents(richText, (RichText) richText.texts.get(a));
                    }
                } else if (richText instanceof TL_textSubscript) {
                    setRichTextParents(richText, ((TL_textSubscript) richText).text);
                } else if (richText instanceof TL_textSuperscript) {
                    setRichTextParents(richText, ((TL_textSuperscript) richText).text);
                } else if (richText instanceof TL_textMarked) {
                    setRichTextParents(richText, ((TL_textMarked) richText).text);
                } else if (richText instanceof TL_textAnchor) {
                    TL_textAnchor textAnchor = (TL_textAnchor) richText;
                    setRichTextParents(richText, textAnchor.text);
                    String name = textAnchor.name.toLowerCase();
                    this.anchors.put(name, Integer.valueOf(this.blocks.size()));
                    if (textAnchor.text instanceof TL_textPlain) {
                        if (!TextUtils.isEmpty(textAnchor.text.text)) {
                            this.anchorsParent.put(name, textAnchor);
                        }
                    } else if (!(textAnchor.text instanceof TL_textEmpty)) {
                        this.anchorsParent.put(name, textAnchor);
                    }
                    this.anchorsOffset.put(name, Integer.valueOf(-1));
                }
            }
        }

        private void setRichTextParents(PageBlock block) {
            int size;
            int a;
            if (block instanceof TL_pageBlockEmbedPost) {
                TL_pageBlockEmbedPost blockEmbedPost = (TL_pageBlockEmbedPost) block;
                setRichTextParents(null, blockEmbedPost.caption.text);
                setRichTextParents(null, blockEmbedPost.caption.credit);
            } else if (block instanceof TL_pageBlockParagraph) {
                setRichTextParents(null, ((TL_pageBlockParagraph) block).text);
            } else if (block instanceof TL_pageBlockKicker) {
                setRichTextParents(null, ((TL_pageBlockKicker) block).text);
            } else if (block instanceof TL_pageBlockFooter) {
                setRichTextParents(null, ((TL_pageBlockFooter) block).text);
            } else if (block instanceof TL_pageBlockHeader) {
                setRichTextParents(null, ((TL_pageBlockHeader) block).text);
            } else if (block instanceof TL_pageBlockPreformatted) {
                setRichTextParents(null, ((TL_pageBlockPreformatted) block).text);
            } else if (block instanceof TL_pageBlockSubheader) {
                setRichTextParents(null, ((TL_pageBlockSubheader) block).text);
            } else if (block instanceof TL_pageBlockSlideshow) {
                TL_pageBlockSlideshow pageBlockSlideshow = (TL_pageBlockSlideshow) block;
                setRichTextParents(null, pageBlockSlideshow.caption.text);
                setRichTextParents(null, pageBlockSlideshow.caption.credit);
                size = pageBlockSlideshow.items.size();
                for (a = 0; a < size; a++) {
                    setRichTextParents((PageBlock) pageBlockSlideshow.items.get(a));
                }
            } else if (block instanceof TL_pageBlockPhoto) {
                TL_pageBlockPhoto pageBlockPhoto = (TL_pageBlockPhoto) block;
                setRichTextParents(null, pageBlockPhoto.caption.text);
                setRichTextParents(null, pageBlockPhoto.caption.credit);
            } else if (block instanceof TL_pageBlockListItem) {
                TL_pageBlockListItem pageBlockListItem = (TL_pageBlockListItem) block;
                if (pageBlockListItem.textItem != null) {
                    setRichTextParents(null, pageBlockListItem.textItem);
                } else if (pageBlockListItem.blockItem != null) {
                    setRichTextParents(pageBlockListItem.blockItem);
                }
            } else if (block instanceof TL_pageBlockOrderedListItem) {
                TL_pageBlockOrderedListItem pageBlockOrderedListItem = (TL_pageBlockOrderedListItem) block;
                if (pageBlockOrderedListItem.textItem != null) {
                    setRichTextParents(null, pageBlockOrderedListItem.textItem);
                } else if (pageBlockOrderedListItem.blockItem != null) {
                    setRichTextParents(pageBlockOrderedListItem.blockItem);
                }
            } else if (block instanceof TL_pageBlockCollage) {
                TL_pageBlockCollage pageBlockCollage = (TL_pageBlockCollage) block;
                setRichTextParents(null, pageBlockCollage.caption.text);
                setRichTextParents(null, pageBlockCollage.caption.credit);
                size = pageBlockCollage.items.size();
                for (a = 0; a < size; a++) {
                    setRichTextParents((PageBlock) pageBlockCollage.items.get(a));
                }
            } else if (block instanceof TL_pageBlockEmbed) {
                TL_pageBlockEmbed pageBlockEmbed = (TL_pageBlockEmbed) block;
                setRichTextParents(null, pageBlockEmbed.caption.text);
                setRichTextParents(null, pageBlockEmbed.caption.credit);
            } else if (block instanceof TL_pageBlockSubtitle) {
                setRichTextParents(null, ((TL_pageBlockSubtitle) block).text);
            } else if (block instanceof TL_pageBlockBlockquote) {
                TL_pageBlockBlockquote pageBlockBlockquote = (TL_pageBlockBlockquote) block;
                setRichTextParents(null, pageBlockBlockquote.text);
                setRichTextParents(null, pageBlockBlockquote.caption);
            } else if (block instanceof TL_pageBlockDetails) {
                TL_pageBlockDetails pageBlockDetails = (TL_pageBlockDetails) block;
                setRichTextParents(null, pageBlockDetails.title);
                size = pageBlockDetails.blocks.size();
                for (a = 0; a < size; a++) {
                    setRichTextParents((PageBlock) pageBlockDetails.blocks.get(a));
                }
            } else if (block instanceof TL_pageBlockVideo) {
                TL_pageBlockVideo pageBlockVideo = (TL_pageBlockVideo) block;
                setRichTextParents(null, pageBlockVideo.caption.text);
                setRichTextParents(null, pageBlockVideo.caption.credit);
            } else if (block instanceof TL_pageBlockPullquote) {
                TL_pageBlockPullquote pageBlockPullquote = (TL_pageBlockPullquote) block;
                setRichTextParents(null, pageBlockPullquote.text);
                setRichTextParents(null, pageBlockPullquote.caption);
            } else if (block instanceof TL_pageBlockAudio) {
                TL_pageBlockAudio pageBlockAudio = (TL_pageBlockAudio) block;
                setRichTextParents(null, pageBlockAudio.caption.text);
                setRichTextParents(null, pageBlockAudio.caption.credit);
            } else if (block instanceof TL_pageBlockTable) {
                TL_pageBlockTable pageBlockTable = (TL_pageBlockTable) block;
                setRichTextParents(null, pageBlockTable.title);
                size = pageBlockTable.rows.size();
                for (a = 0; a < size; a++) {
                    TL_pageTableRow row = (TL_pageTableRow) pageBlockTable.rows.get(a);
                    int size2 = row.cells.size();
                    for (int b = 0; b < size2; b++) {
                        setRichTextParents(null, ((TL_pageTableCell) row.cells.get(b)).text);
                    }
                }
            } else if (block instanceof TL_pageBlockTitle) {
                setRichTextParents(null, ((TL_pageBlockTitle) block).text);
            } else if (block instanceof TL_pageBlockCover) {
                setRichTextParents(((TL_pageBlockCover) block).cover);
            } else if (block instanceof TL_pageBlockAuthorDate) {
                setRichTextParents(null, ((TL_pageBlockAuthorDate) block).author);
            } else if (block instanceof TL_pageBlockMap) {
                TL_pageBlockMap pageBlockMap = (TL_pageBlockMap) block;
                setRichTextParents(null, pageBlockMap.caption.text);
                setRichTextParents(null, pageBlockMap.caption.credit);
            } else if (block instanceof TL_pageBlockRelatedArticles) {
                setRichTextParents(null, ((TL_pageBlockRelatedArticles) block).title);
            }
        }

        private void addBlock(PageBlock block, int level, int listLevel, int position) {
            PageBlock originalBlock = block;
            if (block instanceof TL_pageBlockDetailsChild) {
                block = ((TL_pageBlockDetailsChild) block).block;
            }
            if (!((block instanceof TL_pageBlockList) || (block instanceof TL_pageBlockOrderedList))) {
                setRichTextParents(block);
                addAllMediaFromBlock(block);
            }
            block = ArticleViewer.this.getLastNonListPageBlock(block);
            if (!(block instanceof TL_pageBlockUnsupported)) {
                if (block instanceof TL_pageBlockAnchor) {
                    this.anchors.put(((TL_pageBlockAnchor) block).name.toLowerCase(), Integer.valueOf(this.blocks.size()));
                    return;
                }
                if (!((block instanceof TL_pageBlockList) || (block instanceof TL_pageBlockOrderedList))) {
                    this.blocks.add(originalBlock);
                }
                int b;
                int size;
                TL_pageBlockDetailsChild child;
                PageBlock tL_pageBlockListItem;
                RichText textPlain;
                TL_pageBlockDetailsChild pageBlockDetailsChild;
                PageBlock finalBlock;
                int size2;
                int c;
                if (block instanceof TL_pageBlockAudio) {
                    TL_pageBlockAudio blockAudio = (TL_pageBlockAudio) block;
                    TL_message message = new TL_message();
                    message.out = true;
                    int i = -Long.valueOf(blockAudio.audio_id).hashCode();
                    block.mid = i;
                    message.id = i;
                    message.to_id = new TL_peerUser();
                    Peer peer = message.to_id;
                    int clientUserId = UserConfig.getInstance(ArticleViewer.this.currentAccount).getClientUserId();
                    message.from_id = clientUserId;
                    peer.user_id = clientUserId;
                    message.date = (int) (System.currentTimeMillis() / 1000);
                    message.message = "";
                    message.media = new TL_messageMediaDocument();
                    message.media.webpage = ArticleViewer.this.currentPage;
                    MessageMedia messageMedia = message.media;
                    messageMedia.flags |= 3;
                    message.media.document = ArticleViewer.this.getDocumentWithId(blockAudio.audio_id);
                    message.flags |= 768;
                    MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, message, false);
                    this.audioMessages.add(messageObject);
                    this.audioBlocks.put(blockAudio, messageObject);
                } else if (block instanceof TL_pageBlockEmbedPost) {
                    TL_pageBlockEmbedPost pageBlockEmbedPost = (TL_pageBlockEmbedPost) block;
                    if (!pageBlockEmbedPost.blocks.isEmpty()) {
                        block.level = -1;
                        for (b = 0; b < pageBlockEmbedPost.blocks.size(); b++) {
                            PageBlock innerBlock = (PageBlock) pageBlockEmbedPost.blocks.get(b);
                            if (!(innerBlock instanceof TL_pageBlockUnsupported)) {
                                if (innerBlock instanceof TL_pageBlockAnchor) {
                                    this.anchors.put(((TL_pageBlockAnchor) innerBlock).name.toLowerCase(), Integer.valueOf(this.blocks.size()));
                                } else {
                                    innerBlock.level = 1;
                                    if (b == pageBlockEmbedPost.blocks.size() - 1) {
                                        innerBlock.bottom = true;
                                    }
                                    this.blocks.add(innerBlock);
                                    addAllMediaFromBlock(innerBlock);
                                }
                            }
                        }
                    }
                } else if (block instanceof TL_pageBlockRelatedArticles) {
                    TL_pageBlockRelatedArticles pageBlockRelatedArticles = (TL_pageBlockRelatedArticles) block;
                    TL_pageBlockRelatedArticlesShadow tL_pageBlockRelatedArticlesShadow = new TL_pageBlockRelatedArticlesShadow(ArticleViewer.this, null);
                    tL_pageBlockRelatedArticlesShadow.parent = pageBlockRelatedArticles;
                    this.blocks.add(this.blocks.size() - 1, tL_pageBlockRelatedArticlesShadow);
                    size = pageBlockRelatedArticles.articles.size();
                    for (b = 0; b < size; b++) {
                        TL_pageBlockRelatedArticlesChild child2 = new TL_pageBlockRelatedArticlesChild(ArticleViewer.this, null);
                        child2.parent = pageBlockRelatedArticles;
                        child2.num = b;
                        this.blocks.add(child2);
                    }
                    if (position == 0) {
                        tL_pageBlockRelatedArticlesShadow = new TL_pageBlockRelatedArticlesShadow(ArticleViewer.this, null);
                        tL_pageBlockRelatedArticlesShadow.parent = pageBlockRelatedArticles;
                        this.blocks.add(tL_pageBlockRelatedArticlesShadow);
                    }
                } else if (block instanceof TL_pageBlockDetails) {
                    TL_pageBlockDetails pageBlockDetails = (TL_pageBlockDetails) block;
                    size = pageBlockDetails.blocks.size();
                    for (b = 0; b < size; b++) {
                        child = new TL_pageBlockDetailsChild(ArticleViewer.this, null);
                        child.parent = originalBlock;
                        child.block = (PageBlock) pageBlockDetails.blocks.get(b);
                        addBlock(ArticleViewer.this.wrapInTableBlock(originalBlock, child), level + 1, listLevel, position);
                    }
                } else if (block instanceof TL_pageBlockList) {
                    TL_pageBlockList pageBlockList = (TL_pageBlockList) block;
                    TL_pageBlockListParent tL_pageBlockListParent = new TL_pageBlockListParent(ArticleViewer.this, null);
                    tL_pageBlockListParent.pageBlockList = pageBlockList;
                    tL_pageBlockListParent.level = listLevel;
                    size = pageBlockList.items.size();
                    for (b = 0; b < size; b++) {
                        TL_pageListItemBlocks pageListItemBlocks;
                        PageListItem item = (PageListItem) pageBlockList.items.get(b);
                        tL_pageBlockListItem = new TL_pageBlockListItem(ArticleViewer.this, null);
                        tL_pageBlockListItem.index = b;
                        tL_pageBlockListItem.parent = tL_pageBlockListParent;
                        if (!pageBlockList.ordered) {
                            tL_pageBlockListItem.num = "•";
                        } else if (ArticleViewer.this.isRtl) {
                            tL_pageBlockListItem.num = String.format(".%d", new Object[]{Integer.valueOf(b + 1)});
                        } else {
                            tL_pageBlockListItem.num = String.format("%d.", new Object[]{Integer.valueOf(b + 1)});
                        }
                        tL_pageBlockListParent.items.add(tL_pageBlockListItem);
                        if (item instanceof TL_pageListItemText) {
                            tL_pageBlockListItem.textItem = ((TL_pageListItemText) item).text;
                        } else if (item instanceof TL_pageListItemBlocks) {
                            pageListItemBlocks = (TL_pageListItemBlocks) item;
                            if (pageListItemBlocks.blocks.isEmpty()) {
                                PageListItem text = new TL_pageListItemText();
                                textPlain = new TL_textPlain();
                                textPlain.text = " ";
                                text.text = textPlain;
                                item = text;
                            } else {
                                tL_pageBlockListItem.blockItem = (PageBlock) pageListItemBlocks.blocks.get(0);
                            }
                        }
                        if (originalBlock instanceof TL_pageBlockDetailsChild) {
                            pageBlockDetailsChild = (TL_pageBlockDetailsChild) originalBlock;
                            child = new TL_pageBlockDetailsChild(ArticleViewer.this, null);
                            child.parent = pageBlockDetailsChild.parent;
                            child.block = tL_pageBlockListItem;
                            addBlock(child, level, listLevel + 1, position);
                        } else {
                            if (b == 0) {
                                finalBlock = ArticleViewer.this.fixListBlock(originalBlock, tL_pageBlockListItem);
                            } else {
                                finalBlock = tL_pageBlockListItem;
                            }
                            addBlock(finalBlock, level, listLevel + 1, position);
                        }
                        if (item instanceof TL_pageListItemBlocks) {
                            pageListItemBlocks = (TL_pageListItemBlocks) item;
                            size2 = pageListItemBlocks.blocks.size();
                            for (c = 1; c < size2; c++) {
                                tL_pageBlockListItem = new TL_pageBlockListItem(ArticleViewer.this, null);
                                tL_pageBlockListItem.blockItem = (PageBlock) pageListItemBlocks.blocks.get(c);
                                tL_pageBlockListItem.parent = tL_pageBlockListParent;
                                if (originalBlock instanceof TL_pageBlockDetailsChild) {
                                    pageBlockDetailsChild = (TL_pageBlockDetailsChild) originalBlock;
                                    child = new TL_pageBlockDetailsChild(ArticleViewer.this, null);
                                    child.parent = pageBlockDetailsChild.parent;
                                    child.block = tL_pageBlockListItem;
                                    addBlock(child, level, listLevel + 1, position);
                                } else {
                                    addBlock(tL_pageBlockListItem, level, listLevel + 1, position);
                                }
                                tL_pageBlockListParent.items.add(tL_pageBlockListItem);
                            }
                        }
                    }
                } else if (block instanceof TL_pageBlockOrderedList) {
                    TL_pageBlockOrderedList pageBlockOrderedList = (TL_pageBlockOrderedList) block;
                    TL_pageBlockOrderedListParent tL_pageBlockOrderedListParent = new TL_pageBlockOrderedListParent(ArticleViewer.this, null);
                    tL_pageBlockOrderedListParent.pageBlockOrderedList = pageBlockOrderedList;
                    tL_pageBlockOrderedListParent.level = listLevel;
                    size = pageBlockOrderedList.items.size();
                    for (b = 0; b < size; b++) {
                        TL_pageListOrderedItemBlocks pageListOrderedItemBlocks;
                        PageListOrderedItem item2 = (PageListOrderedItem) pageBlockOrderedList.items.get(b);
                        tL_pageBlockListItem = new TL_pageBlockOrderedListItem(ArticleViewer.this, null);
                        tL_pageBlockListItem.index = b;
                        tL_pageBlockListItem.parent = tL_pageBlockOrderedListParent;
                        tL_pageBlockOrderedListParent.items.add(tL_pageBlockListItem);
                        if (item2 instanceof TL_pageListOrderedItemText) {
                            TL_pageListOrderedItemText pageListOrderedItemText = (TL_pageListOrderedItemText) item2;
                            tL_pageBlockListItem.textItem = pageListOrderedItemText.text;
                            if (TextUtils.isEmpty(pageListOrderedItemText.num)) {
                                if (ArticleViewer.this.isRtl) {
                                    tL_pageBlockListItem.num = String.format(".%d", new Object[]{Integer.valueOf(b + 1)});
                                } else {
                                    tL_pageBlockListItem.num = String.format("%d.", new Object[]{Integer.valueOf(b + 1)});
                                }
                            } else if (ArticleViewer.this.isRtl) {
                                tL_pageBlockListItem.num = "." + pageListOrderedItemText.num;
                            } else {
                                tL_pageBlockListItem.num = pageListOrderedItemText.num + ".";
                            }
                        } else if (item2 instanceof TL_pageListOrderedItemBlocks) {
                            pageListOrderedItemBlocks = (TL_pageListOrderedItemBlocks) item2;
                            if (pageListOrderedItemBlocks.blocks.isEmpty()) {
                                PageListOrderedItem text2 = new TL_pageListOrderedItemText();
                                textPlain = new TL_textPlain();
                                textPlain.text = " ";
                                text2.text = textPlain;
                                item2 = text2;
                            } else {
                                tL_pageBlockListItem.blockItem = (PageBlock) pageListOrderedItemBlocks.blocks.get(0);
                            }
                            if (TextUtils.isEmpty(pageListOrderedItemBlocks.num)) {
                                if (ArticleViewer.this.isRtl) {
                                    tL_pageBlockListItem.num = String.format(".%d", new Object[]{Integer.valueOf(b + 1)});
                                } else {
                                    tL_pageBlockListItem.num = String.format("%d.", new Object[]{Integer.valueOf(b + 1)});
                                }
                            } else if (ArticleViewer.this.isRtl) {
                                tL_pageBlockListItem.num = "." + pageListOrderedItemBlocks.num;
                            } else {
                                tL_pageBlockListItem.num = pageListOrderedItemBlocks.num + ".";
                            }
                        }
                        if (originalBlock instanceof TL_pageBlockDetailsChild) {
                            pageBlockDetailsChild = (TL_pageBlockDetailsChild) originalBlock;
                            child = new TL_pageBlockDetailsChild(ArticleViewer.this, null);
                            child.parent = pageBlockDetailsChild.parent;
                            child.block = tL_pageBlockListItem;
                            addBlock(child, level, listLevel + 1, position);
                        } else {
                            if (b == 0) {
                                finalBlock = ArticleViewer.this.fixListBlock(originalBlock, tL_pageBlockListItem);
                            } else {
                                finalBlock = tL_pageBlockListItem;
                            }
                            addBlock(finalBlock, level, listLevel + 1, position);
                        }
                        if (item2 instanceof TL_pageListOrderedItemBlocks) {
                            pageListOrderedItemBlocks = (TL_pageListOrderedItemBlocks) item2;
                            size2 = pageListOrderedItemBlocks.blocks.size();
                            for (c = 1; c < size2; c++) {
                                tL_pageBlockListItem = new TL_pageBlockOrderedListItem(ArticleViewer.this, null);
                                tL_pageBlockListItem.blockItem = (PageBlock) pageListOrderedItemBlocks.blocks.get(c);
                                tL_pageBlockListItem.parent = tL_pageBlockOrderedListParent;
                                if (originalBlock instanceof TL_pageBlockDetailsChild) {
                                    pageBlockDetailsChild = (TL_pageBlockDetailsChild) originalBlock;
                                    child = new TL_pageBlockDetailsChild(ArticleViewer.this, null);
                                    child.parent = pageBlockDetailsChild.parent;
                                    child.block = tL_pageBlockListItem;
                                    addBlock(child, level, listLevel + 1, position);
                                } else {
                                    addBlock(tL_pageBlockListItem, level, listLevel + 1, position);
                                }
                                tL_pageBlockOrderedListParent.items.add(tL_pageBlockListItem);
                            }
                        }
                    }
                }
            }
        }

        private void addAllMediaFromBlock(PageBlock block) {
            int count;
            int a;
            PageBlock innerBlock;
            if (block instanceof TL_pageBlockPhoto) {
                TL_pageBlockPhoto pageBlockPhoto = (TL_pageBlockPhoto) block;
                Photo photo = ArticleViewer.this.getPhotoWithId(pageBlockPhoto.photo_id);
                if (photo != null) {
                    pageBlockPhoto.thumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 56, true);
                    this.photoBlocks.add(block);
                }
            } else if ((block instanceof TL_pageBlockVideo) && ArticleViewer.this.isVideoBlock(block)) {
                TL_pageBlockVideo pageBlockVideo = (TL_pageBlockVideo) block;
                Document document = ArticleViewer.this.getDocumentWithId(pageBlockVideo.video_id);
                if (document != null) {
                    pageBlockVideo.thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 56, true);
                    this.photoBlocks.add(block);
                }
            } else if (block instanceof TL_pageBlockSlideshow) {
                TL_pageBlockSlideshow slideshow = (TL_pageBlockSlideshow) block;
                count = slideshow.items.size();
                for (a = 0; a < count; a++) {
                    innerBlock = (PageBlock) slideshow.items.get(a);
                    innerBlock.groupId = ArticleViewer.this.lastBlockNum;
                    addAllMediaFromBlock(innerBlock);
                }
                ArticleViewer.this.lastBlockNum = ArticleViewer.this.lastBlockNum + 1;
            } else if (block instanceof TL_pageBlockCollage) {
                TL_pageBlockCollage collage = (TL_pageBlockCollage) block;
                count = collage.items.size();
                for (a = 0; a < count; a++) {
                    innerBlock = (PageBlock) collage.items.get(a);
                    innerBlock.groupId = ArticleViewer.this.lastBlockNum;
                    addAllMediaFromBlock(innerBlock);
                }
                ArticleViewer.this.lastBlockNum = ArticleViewer.this.lastBlockNum + 1;
            } else if (block instanceof TL_pageBlockCover) {
                addAllMediaFromBlock(((TL_pageBlockCover) block).cover);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new BlockParagraphCell(this.context, this);
                    break;
                case 1:
                    view = new BlockHeaderCell(this.context, this);
                    break;
                case 2:
                    view = new BlockDividerCell(this.context);
                    break;
                case 3:
                    view = new BlockEmbedCell(this.context, this);
                    break;
                case 4:
                    view = new BlockSubtitleCell(this.context, this);
                    break;
                case 5:
                    view = new BlockVideoCell(this.context, this, 0);
                    break;
                case 6:
                    view = new BlockPullquoteCell(this.context, this);
                    break;
                case 7:
                    view = new BlockBlockquoteCell(this.context, this);
                    break;
                case 8:
                    view = new BlockSlideshowCell(this.context, this);
                    break;
                case 9:
                    view = new BlockPhotoCell(this.context, this, 0);
                    break;
                case 10:
                    view = new BlockAuthorDateCell(this.context, this);
                    break;
                case 11:
                    view = new BlockTitleCell(this.context, this);
                    break;
                case 12:
                    view = new BlockListItemCell(this.context, this);
                    break;
                case 13:
                    view = new BlockFooterCell(this.context, this);
                    break;
                case 14:
                    view = new BlockPreformattedCell(this.context, this);
                    break;
                case 15:
                    view = new BlockSubheaderCell(this.context, this);
                    break;
                case 16:
                    view = new BlockEmbedPostCell(this.context, this);
                    break;
                case 17:
                    view = new BlockCollageCell(this.context, this);
                    break;
                case 18:
                    view = new BlockChannelCell(this.context, this, 0);
                    break;
                case 19:
                    view = new BlockAudioCell(this.context, this);
                    break;
                case 20:
                    view = new BlockKickerCell(this.context, this);
                    break;
                case 21:
                    view = new BlockOrderedListItemCell(this.context, this);
                    break;
                case 22:
                    view = new BlockMapCell(this.context, this, 0);
                    break;
                case 23:
                    view = new BlockRelatedArticlesCell(this.context, this);
                    break;
                case 24:
                    view = new BlockDetailsCell(this.context, this);
                    break;
                case 25:
                    view = new BlockTableCell(this.context, this);
                    break;
                case 26:
                    view = new BlockRelatedArticlesHeaderCell(this.context, this);
                    break;
                case 27:
                    view = new BlockDetailsBottomCell(this.context);
                    break;
                case 28:
                    view = new BlockRelatedArticlesShadowCell(this.context);
                    break;
                case 90:
                    View frameLayout = new FrameLayout(this.context) {
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
                        }
                    };
                    frameLayout.setTag(Integer.valueOf(90));
                    TextView textView = new TextView(this.context);
                    frameLayout.addView(textView, LayoutHelper.createFrame(-1, 34.0f, 51, 0.0f, 10.0f, 0.0f, 0.0f));
                    textView.setText(LocaleController.getString("PreviewFeedback", R.string.PreviewFeedback));
                    textView.setTextSize(1, 12.0f);
                    textView.setGravity(17);
                    view = frameLayout;
                    break;
                default:
                    View textView2 = new TextView(this.context);
                    textView2.setBackgroundColor(-65536);
                    textView2.setTextColor(-16777216);
                    textView2.setTextSize(1, 20.0f);
                    view = textView2;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new Holder(view);
        }

        public boolean isEnabled(ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 23 || type == 24) {
                return true;
            }
            return false;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position < this.localBlocks.size()) {
                PageBlock block = (PageBlock) this.localBlocks.get(position);
                bindBlockToHolder(holder.getItemViewType(), holder, block, position, this.localBlocks.size());
                return;
            }
            switch (holder.getItemViewType()) {
                case 90:
                    TextView textView = (TextView) ((ViewGroup) holder.itemView).getChildAt(0);
                    int color = ArticleViewer.this.getSelectedColor();
                    if (color == 0) {
                        textView.setTextColor(-8879475);
                        textView.setBackgroundColor(-1183760);
                        return;
                    } else if (color == 1) {
                        textView.setTextColor(ArticleViewer.this.getGrayTextColor());
                        textView.setBackgroundColor(-1712440);
                        return;
                    } else if (color == 2) {
                        textView.setTextColor(ArticleViewer.this.getGrayTextColor());
                        textView.setBackgroundColor(-15000805);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        private void bindBlockToHolder(int type, ViewHolder holder, PageBlock block, int position, int total) {
            boolean z = true;
            PageBlock originalBlock = block;
            if (block instanceof TL_pageBlockCover) {
                block = ((TL_pageBlockCover) block).cover;
            } else if (block instanceof TL_pageBlockDetailsChild) {
                block = ((TL_pageBlockDetailsChild) block).block;
            }
            boolean z2;
            switch (type) {
                case 0:
                    holder.itemView.setBlock((TL_pageBlockParagraph) block);
                    return;
                case 1:
                    holder.itemView.setBlock((TL_pageBlockHeader) block);
                    return;
                case 2:
                    BlockDividerCell cell = holder.itemView;
                    return;
                case 3:
                    holder.itemView.setBlock((TL_pageBlockEmbed) block);
                    return;
                case 4:
                    holder.itemView.setBlock((TL_pageBlockSubtitle) block);
                    return;
                case 5:
                    BlockVideoCell cell2 = holder.itemView;
                    TL_pageBlockVideo tL_pageBlockVideo = (TL_pageBlockVideo) block;
                    if (position == 0) {
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    if (position != total - 1) {
                        z = false;
                    }
                    cell2.setBlock(tL_pageBlockVideo, z2, z);
                    cell2.setParentBlock(originalBlock);
                    return;
                case 6:
                    holder.itemView.setBlock((TL_pageBlockPullquote) block);
                    return;
                case 7:
                    holder.itemView.setBlock((TL_pageBlockBlockquote) block);
                    return;
                case 8:
                    holder.itemView.setBlock((TL_pageBlockSlideshow) block);
                    return;
                case 9:
                    BlockPhotoCell cell3 = holder.itemView;
                    TL_pageBlockPhoto tL_pageBlockPhoto = (TL_pageBlockPhoto) block;
                    if (position == 0) {
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    if (position != total - 1) {
                        z = false;
                    }
                    cell3.setBlock(tL_pageBlockPhoto, z2, z);
                    cell3.setParentBlock(originalBlock);
                    return;
                case 10:
                    holder.itemView.setBlock((TL_pageBlockAuthorDate) block);
                    return;
                case 11:
                    holder.itemView.setBlock((TL_pageBlockTitle) block);
                    return;
                case 12:
                    holder.itemView.setBlock((TL_pageBlockListItem) block);
                    return;
                case 13:
                    holder.itemView.setBlock((TL_pageBlockFooter) block);
                    return;
                case 14:
                    holder.itemView.setBlock((TL_pageBlockPreformatted) block);
                    return;
                case 15:
                    holder.itemView.setBlock((TL_pageBlockSubheader) block);
                    return;
                case 16:
                    holder.itemView.setBlock((TL_pageBlockEmbedPost) block);
                    return;
                case 17:
                    holder.itemView.setBlock((TL_pageBlockCollage) block);
                    return;
                case 18:
                    holder.itemView.setBlock((TL_pageBlockChannel) block);
                    return;
                case 19:
                    BlockAudioCell cell4 = holder.itemView;
                    TL_pageBlockAudio tL_pageBlockAudio = (TL_pageBlockAudio) block;
                    z2 = position == 0;
                    if (position != total - 1) {
                        z = false;
                    }
                    cell4.setBlock(tL_pageBlockAudio, z2, z);
                    return;
                case 20:
                    holder.itemView.setBlock((TL_pageBlockKicker) block);
                    return;
                case 21:
                    holder.itemView.setBlock((TL_pageBlockOrderedListItem) block);
                    return;
                case 22:
                    BlockMapCell cell5 = holder.itemView;
                    TL_pageBlockMap tL_pageBlockMap = (TL_pageBlockMap) block;
                    z2 = position == 0;
                    if (position != total - 1) {
                        z = false;
                    }
                    cell5.setBlock(tL_pageBlockMap, z2, z);
                    return;
                case 23:
                    holder.itemView.setBlock((TL_pageBlockRelatedArticlesChild) block);
                    return;
                case 24:
                    holder.itemView.setBlock((TL_pageBlockDetails) block);
                    return;
                case 25:
                    holder.itemView.setBlock((TL_pageBlockTable) block);
                    return;
                case 26:
                    holder.itemView.setBlock((TL_pageBlockRelatedArticles) block);
                    return;
                case 27:
                    BlockDetailsBottomCell cell6 = holder.itemView;
                    return;
                case 100:
                    holder.itemView.setText("unsupported block " + block);
                    return;
                default:
                    return;
            }
        }

        private int getTypeForBlock(PageBlock block) {
            if (block instanceof TL_pageBlockParagraph) {
                return 0;
            }
            if (block instanceof TL_pageBlockHeader) {
                return 1;
            }
            if (block instanceof TL_pageBlockDivider) {
                return 2;
            }
            if (block instanceof TL_pageBlockEmbed) {
                return 3;
            }
            if (block instanceof TL_pageBlockSubtitle) {
                return 4;
            }
            if (block instanceof TL_pageBlockVideo) {
                return 5;
            }
            if (block instanceof TL_pageBlockPullquote) {
                return 6;
            }
            if (block instanceof TL_pageBlockBlockquote) {
                return 7;
            }
            if (block instanceof TL_pageBlockSlideshow) {
                return 8;
            }
            if (block instanceof TL_pageBlockPhoto) {
                return 9;
            }
            if (block instanceof TL_pageBlockAuthorDate) {
                return 10;
            }
            if (block instanceof TL_pageBlockTitle) {
                return 11;
            }
            if (block instanceof TL_pageBlockListItem) {
                return 12;
            }
            if (block instanceof TL_pageBlockFooter) {
                return 13;
            }
            if (block instanceof TL_pageBlockPreformatted) {
                return 14;
            }
            if (block instanceof TL_pageBlockSubheader) {
                return 15;
            }
            if (block instanceof TL_pageBlockEmbedPost) {
                return 16;
            }
            if (block instanceof TL_pageBlockCollage) {
                return 17;
            }
            if (block instanceof TL_pageBlockChannel) {
                return 18;
            }
            if (block instanceof TL_pageBlockAudio) {
                return 19;
            }
            if (block instanceof TL_pageBlockKicker) {
                return 20;
            }
            if (block instanceof TL_pageBlockOrderedListItem) {
                return 21;
            }
            if (block instanceof TL_pageBlockMap) {
                return 22;
            }
            if (block instanceof TL_pageBlockRelatedArticlesChild) {
                return 23;
            }
            if (block instanceof TL_pageBlockDetails) {
                return 24;
            }
            if (block instanceof TL_pageBlockTable) {
                return 25;
            }
            if (block instanceof TL_pageBlockRelatedArticles) {
                return 26;
            }
            if (block instanceof TL_pageBlockDetailsBottom) {
                return 27;
            }
            if (block instanceof TL_pageBlockRelatedArticlesShadow) {
                return 28;
            }
            if (block instanceof TL_pageBlockDetailsChild) {
                return getTypeForBlock(((TL_pageBlockDetailsChild) block).block);
            }
            if (block instanceof TL_pageBlockCover) {
                return getTypeForBlock(((TL_pageBlockCover) block).cover);
            }
            return 100;
        }

        public int getItemViewType(int position) {
            if (position == this.localBlocks.size()) {
                return 90;
            }
            return getTypeForBlock((PageBlock) this.localBlocks.get(position));
        }

        public PageBlock getItem(int position) {
            return (PageBlock) this.localBlocks.get(position);
        }

        public int getItemCount() {
            return (ArticleViewer.this.currentPage == null || ArticleViewer.this.currentPage.cached_page == null) ? 0 : this.localBlocks.size() + 1;
        }

        private boolean isBlockOpened(TL_pageBlockDetailsChild child) {
            PageBlock parentBlock = ArticleViewer.this.getLastNonListPageBlock(child.parent);
            if (parentBlock instanceof TL_pageBlockDetails) {
                return ((TL_pageBlockDetails) parentBlock).open;
            }
            if (!(parentBlock instanceof TL_pageBlockDetailsChild)) {
                return false;
            }
            TL_pageBlockDetailsChild parent = (TL_pageBlockDetailsChild) parentBlock;
            parentBlock = ArticleViewer.this.getLastNonListPageBlock(parent.block);
            if (!(parentBlock instanceof TL_pageBlockDetails) || ((TL_pageBlockDetails) parentBlock).open) {
                return isBlockOpened(parent);
            }
            return false;
        }

        private void updateRows() {
            this.localBlocks.clear();
            int size = this.blocks.size();
            for (int a = 0; a < size; a++) {
                PageBlock originalBlock = (PageBlock) this.blocks.get(a);
                PageBlock block = ArticleViewer.this.getLastNonListPageBlock(originalBlock);
                if (!(block instanceof TL_pageBlockDetailsChild) || isBlockOpened((TL_pageBlockDetailsChild) block)) {
                    this.localBlocks.add(originalBlock);
                }
            }
        }

        private void cleanup() {
            this.blocks.clear();
            this.photoBlocks.clear();
            this.audioBlocks.clear();
            this.audioMessages.clear();
            this.anchors.clear();
            this.anchorsParent.clear();
            this.anchorsOffset.clear();
            notifyDataSetChanged();
        }

        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        public void notifyItemChanged(int position) {
            updateRows();
            super.notifyItemChanged(position);
        }

        public void notifyItemChanged(int position, Object payload) {
            updateRows();
            super.notifyItemChanged(position, payload);
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeChanged(positionStart, itemCount);
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount, Object payload) {
            updateRows();
            super.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        public void notifyItemInserted(int position) {
            updateRows();
            super.notifyItemInserted(position);
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            super.notifyItemMoved(fromPosition, toPosition);
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeInserted(positionStart, itemCount);
        }

        public void notifyItemRemoved(int position) {
            updateRows();
            super.notifyItemRemoved(position);
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    private class WindowView extends FrameLayout {
        private float alpha;
        private Runnable attachRunnable;
        private int bHeight;
        private int bWidth;
        private int bX;
        private int bY;
        private boolean closeAnimationInProgress;
        private float innerTranslationX;
        private boolean maybeStartTracking;
        private boolean movingPage;
        private boolean selfLayout;
        private int startMovingHeaderHeight;
        private boolean startedTracking;
        private int startedTrackingPointerId;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker tracker;

        public WindowView(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            if (VERSION.SDK_INT < 21 || ArticleViewer.this.lastInsets == null) {
                setMeasuredDimension(widthSize, heightSize);
            } else {
                setMeasuredDimension(widthSize, heightSize);
                WindowInsets insets = (WindowInsets) ArticleViewer.this.lastInsets;
                if (AndroidUtilities.incorrectDisplaySizeFix) {
                    if (heightSize > AndroidUtilities.displaySize.y) {
                        heightSize = AndroidUtilities.displaySize.y;
                    }
                    heightSize += AndroidUtilities.statusBarHeight;
                }
                heightSize -= insets.getSystemWindowInsetBottom();
                widthSize -= insets.getSystemWindowInsetRight() + insets.getSystemWindowInsetLeft();
                if (insets.getSystemWindowInsetRight() != 0) {
                    this.bWidth = insets.getSystemWindowInsetRight();
                    this.bHeight = heightSize;
                } else if (insets.getSystemWindowInsetLeft() != 0) {
                    this.bWidth = insets.getSystemWindowInsetLeft();
                    this.bHeight = heightSize;
                } else {
                    this.bWidth = widthSize;
                    this.bHeight = insets.getSystemWindowInsetBottom();
                }
            }
            ArticleViewer.this.containerView.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(heightSize, NUM));
            ArticleViewer.this.photoContainerView.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(heightSize, NUM));
            ArticleViewer.this.photoContainerBackground.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(heightSize, NUM));
            ArticleViewer.this.fullscreenVideoContainer.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(heightSize, NUM));
            ViewGroup.LayoutParams layoutParams = ArticleViewer.this.animatingImageView.getLayoutParams();
            ArticleViewer.this.animatingImageView.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(layoutParams.height, Integer.MIN_VALUE));
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            if (!this.selfLayout) {
                int x;
                int width = right - left;
                if (ArticleViewer.this.anchorsOffsetMeasuredWidth != width) {
                    for (int i = 0; i < ArticleViewer.this.listView.length; i++) {
                        for (Entry<String, Integer> entry : ArticleViewer.this.adapter[i].anchorsOffset.entrySet()) {
                            entry.setValue(Integer.valueOf(-1));
                        }
                    }
                    ArticleViewer.this.anchorsOffsetMeasuredWidth = width;
                }
                if (VERSION.SDK_INT < 21 || ArticleViewer.this.lastInsets == null) {
                    x = 0;
                } else {
                    WindowInsets insets = (WindowInsets) ArticleViewer.this.lastInsets;
                    x = insets.getSystemWindowInsetLeft();
                    if (insets.getSystemWindowInsetRight() != 0) {
                        this.bX = width - this.bWidth;
                        this.bY = 0;
                    } else if (insets.getSystemWindowInsetLeft() != 0) {
                        this.bX = 0;
                        this.bY = 0;
                    } else {
                        this.bX = 0;
                        this.bY = (bottom - top) - this.bHeight;
                    }
                }
                ArticleViewer.this.containerView.layout(x, 0, ArticleViewer.this.containerView.getMeasuredWidth() + x, ArticleViewer.this.containerView.getMeasuredHeight());
                ArticleViewer.this.photoContainerView.layout(x, 0, ArticleViewer.this.photoContainerView.getMeasuredWidth() + x, ArticleViewer.this.photoContainerView.getMeasuredHeight());
                ArticleViewer.this.photoContainerBackground.layout(x, 0, ArticleViewer.this.photoContainerBackground.getMeasuredWidth() + x, ArticleViewer.this.photoContainerBackground.getMeasuredHeight());
                ArticleViewer.this.fullscreenVideoContainer.layout(x, 0, ArticleViewer.this.fullscreenVideoContainer.getMeasuredWidth() + x, ArticleViewer.this.fullscreenVideoContainer.getMeasuredHeight());
                ArticleViewer.this.animatingImageView.layout(0, 0, ArticleViewer.this.animatingImageView.getMeasuredWidth(), ArticleViewer.this.animatingImageView.getMeasuredHeight());
            }
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            ArticleViewer.this.attachedToWindow = true;
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            ArticleViewer.this.attachedToWindow = false;
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            handleTouchEvent(null);
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return !ArticleViewer.this.collapsed && (handleTouchEvent(ev) || super.onInterceptTouchEvent(ev));
        }

        public boolean onTouchEvent(MotionEvent event) {
            return !ArticleViewer.this.collapsed && (handleTouchEvent(event) || super.onTouchEvent(event));
        }

        @Keep
        public void setInnerTranslationX(float value) {
            this.innerTranslationX = value;
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                DrawerLayoutContainer drawerLayoutContainer = ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer;
                boolean z = (ArticleViewer.this.isVisible && this.alpha == 1.0f && this.innerTranslationX == 0.0f) ? false : true;
                drawerLayoutContainer.setAllowDrawContent(z);
            }
            invalidate();
        }

        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            int width = getMeasuredWidth();
            int translationX = (int) this.innerTranslationX;
            int restoreCount = canvas.save();
            canvas.clipRect(translationX, 0, width, getHeight());
            boolean result = super.drawChild(canvas, child, drawingTime);
            canvas.restoreToCount(restoreCount);
            if (translationX != 0 && child == ArticleViewer.this.containerView) {
                float opacity = Math.min(0.8f, ((float) (width - translationX)) / ((float) width));
                if (opacity < 0.0f) {
                    opacity = 0.0f;
                }
                ArticleViewer.this.scrimPaint.setColor(((int) (153.0f * opacity)) << 24);
                canvas.drawRect(0.0f, 0.0f, (float) translationX, (float) getHeight(), ArticleViewer.this.scrimPaint);
                float alpha = Math.max(0.0f, Math.min(((float) (width - translationX)) / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                ArticleViewer.this.layerShadowDrawable.setBounds(translationX - ArticleViewer.this.layerShadowDrawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                ArticleViewer.this.layerShadowDrawable.setAlpha((int) (255.0f * alpha));
                ArticleViewer.this.layerShadowDrawable.draw(canvas);
            }
            return result;
        }

        @Keep
        public float getInnerTranslationX() {
            return this.innerTranslationX;
        }

        private void prepareForMoving(MotionEvent ev) {
            this.maybeStartTracking = false;
            this.startedTracking = true;
            this.startedTrackingX = (int) ev.getX();
            if (ArticleViewer.this.pagesStack.size() > 1) {
                this.movingPage = true;
                this.startMovingHeaderHeight = ArticleViewer.this.currentHeaderHeight;
                ArticleViewer.this.listView[1].setVisibility(0);
                ArticleViewer.this.listView[1].setAlpha(1.0f);
                ArticleViewer.this.listView[1].setTranslationX(0.0f);
                ArticleViewer.this.listView[0].setBackgroundColor(ArticleViewer.this.backgroundPaint.getColor());
            } else {
                this.movingPage = false;
            }
            ArticleViewer.this.cancelCheckLongPress();
        }

        public boolean handleTouchEvent(MotionEvent event) {
            if (ArticleViewer.this.isPhotoVisible || this.closeAnimationInProgress || ArticleViewer.this.fullscreenVideoContainer.getVisibility() == 0) {
                return false;
            }
            if (event != null && event.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                this.startedTrackingPointerId = event.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) event.getX();
                this.startedTrackingY = (int) event.getY();
                if (this.tracker != null) {
                    this.tracker.clear();
                }
            } else if (event != null && event.getAction() == 2 && event.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.tracker == null) {
                    this.tracker = VelocityTracker.obtain();
                }
                int dx = Math.max(0, (int) (event.getX() - ((float) this.startedTrackingX)));
                int dy = Math.abs(((int) event.getY()) - this.startedTrackingY);
                this.tracker.addMovement(event);
                if (this.maybeStartTracking && !this.startedTracking && ((float) dx) >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(dx) / 3 > dy) {
                    prepareForMoving(event);
                } else if (this.startedTracking) {
                    ArticleViewer.this.pressedLinkOwnerLayout = null;
                    ArticleViewer.this.pressedLinkOwnerView = null;
                    if (this.movingPage) {
                        ArticleViewer.this.listView[0].setTranslationX((float) dx);
                    } else {
                        ArticleViewer.this.containerView.setTranslationX((float) dx);
                        setInnerTranslationX((float) dx);
                    }
                }
            } else if (event != null && event.getPointerId(0) == this.startedTrackingPointerId && (event.getAction() == 3 || event.getAction() == 1 || event.getAction() == 6)) {
                if (this.tracker == null) {
                    this.tracker = VelocityTracker.obtain();
                }
                this.tracker.computeCurrentVelocity(1000);
                float velX = this.tracker.getXVelocity();
                float velY = this.tracker.getYVelocity();
                if (!this.startedTracking && velX >= 3500.0f && velX > Math.abs(velY)) {
                    prepareForMoving(event);
                }
                if (this.startedTracking) {
                    float distToMove;
                    View movingView = this.movingPage ? ArticleViewer.this.listView[0] : ArticleViewer.this.containerView;
                    float x = movingView.getX();
                    final boolean backAnimation = x < ((float) movingView.getMeasuredWidth()) / 3.0f && (velX < 3500.0f || velX < velY);
                    AnimatorSet animatorSet = new AnimatorSet();
                    Animator[] animatorArr;
                    if (backAnimation) {
                        distToMove = x;
                        if (this.movingPage) {
                            animatorArr = new Animator[1];
                            animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.listView[0], View.TRANSLATION_X, new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.containerView, View.TRANSLATION_X, new float[]{0.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this, ArticleViewer.ARTICLE_VIEWER_INNER_TRANSLATION_X, new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        }
                    } else {
                        distToMove = ((float) movingView.getMeasuredWidth()) - x;
                        if (this.movingPage) {
                            animatorArr = new Animator[1];
                            animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.listView[0], View.TRANSLATION_X, new float[]{(float) movingView.getMeasuredWidth()});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.containerView, View.TRANSLATION_X, new float[]{(float) movingView.getMeasuredWidth()});
                            animatorArr[1] = ObjectAnimator.ofFloat(this, ArticleViewer.ARTICLE_VIEWER_INNER_TRANSLATION_X, new float[]{(float) movingView.getMeasuredWidth()});
                            animatorSet.playTogether(animatorArr);
                        }
                    }
                    animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) movingView.getMeasuredWidth())) * distToMove), 50));
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (WindowView.this.movingPage) {
                                ArticleViewer.this.listView[0].setBackgroundDrawable(null);
                                if (!backAnimation) {
                                    WebpageAdapter adapterToUpdate = ArticleViewer.this.adapter[1];
                                    ArticleViewer.this.adapter[1] = ArticleViewer.this.adapter[0];
                                    ArticleViewer.this.adapter[0] = adapterToUpdate;
                                    RecyclerListView listToUpdate = ArticleViewer.this.listView[1];
                                    ArticleViewer.this.listView[1] = ArticleViewer.this.listView[0];
                                    ArticleViewer.this.listView[0] = listToUpdate;
                                    LinearLayoutManager layoutManagerToUpdate = ArticleViewer.this.layoutManager[1];
                                    ArticleViewer.this.layoutManager[1] = ArticleViewer.this.layoutManager[0];
                                    ArticleViewer.this.layoutManager[0] = layoutManagerToUpdate;
                                    ArticleViewer.this.pagesStack.remove(ArticleViewer.this.pagesStack.size() - 1);
                                    ArticleViewer.this.currentPage = (WebPage) ArticleViewer.this.pagesStack.get(ArticleViewer.this.pagesStack.size() - 1);
                                }
                                ArticleViewer.this.listView[1].setVisibility(8);
                                ArticleViewer.this.headerView.invalidate();
                            } else if (!backAnimation) {
                                ArticleViewer.this.saveCurrentPagePosition();
                                ArticleViewer.this.onClosed();
                            }
                            WindowView.this.movingPage = false;
                            WindowView.this.startedTracking = false;
                            WindowView.this.closeAnimationInProgress = false;
                        }
                    });
                    animatorSet.start();
                    this.closeAnimationInProgress = true;
                } else {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                    this.movingPage = false;
                }
                if (this.tracker != null) {
                    this.tracker.recycle();
                    this.tracker = null;
                }
            } else if (event == null) {
                this.maybeStartTracking = false;
                this.startedTracking = false;
                this.movingPage = false;
                if (this.tracker != null) {
                    this.tracker.recycle();
                    this.tracker = null;
                }
            }
            return this.startedTracking;
        }

        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (this.bWidth != 0 && this.bHeight != 0) {
                if (this.bX == 0 && this.bY == 0) {
                    canvas.drawRect((float) this.bX, (float) this.bY, (float) (this.bX + this.bWidth), (float) (this.bY + this.bHeight), ArticleViewer.this.blackPaint);
                    return;
                }
                canvas.drawRect(((float) this.bX) - getTranslationX(), (float) this.bY, ((float) (this.bX + this.bWidth)) - getTranslationX(), (float) (this.bY + this.bHeight), ArticleViewer.this.blackPaint);
            }
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawRect(this.innerTranslationX, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), ArticleViewer.this.backgroundPaint);
        }

        @Keep
        public void setAlpha(float value) {
            ArticleViewer.this.backgroundPaint.setAlpha((int) (255.0f * value));
            this.alpha = value;
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                DrawerLayoutContainer drawerLayoutContainer = ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer;
                boolean z = (ArticleViewer.this.isVisible && this.alpha == 1.0f && this.innerTranslationX == 0.0f) ? false : true;
                drawerLayoutContainer.setAllowDrawContent(z);
            }
            invalidate();
        }

        @Keep
        public float getAlpha() {
            return this.alpha;
        }
    }

    static /* synthetic */ int access$1104(ArticleViewer x0) {
        int i = x0.pressCount + 1;
        x0.pressCount = i;
        return i;
    }

    public static ArticleViewer getInstance() {
        Throwable th;
        ArticleViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (ArticleViewer.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        ArticleViewer localInstance2 = new ArticleViewer();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    private void createPaint(boolean update) {
        if (quoteLinePaint == null) {
            quoteLinePaint = new Paint();
            preformattedBackgroundPaint = new Paint();
            tableLinePaint = new Paint(1);
            tableLinePaint.setStyle(Style.STROKE);
            tableLinePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
            tableHalfLinePaint = new Paint();
            tableHalfLinePaint.setStyle(Style.STROKE);
            tableHalfLinePaint.setStrokeWidth(((float) AndroidUtilities.dp(1.0f)) / 2.0f);
            tableHeaderPaint = new Paint();
            tableStripPaint = new Paint();
            urlPaint = new Paint();
            webpageUrlPaint = new Paint(1);
            photoBackgroundPaint = new Paint();
            dividerPaint = new Paint();
            webpageMarkPaint = new Paint(1);
        } else if (!update) {
            return;
        }
        int color = getSelectedColor();
        if (color == 0) {
            preformattedBackgroundPaint.setColor(-657156);
            webpageUrlPaint.setColor(-1313798);
            urlPaint.setColor(-2299145);
            tableHalfLinePaint.setColor(-2039584);
            tableLinePaint.setColor(-2039584);
            tableHeaderPaint.setColor(-723724);
            tableStripPaint.setColor(-526345);
            photoBackgroundPaint.setColor(-723724);
            dividerPaint.setColor(-3288619);
            webpageMarkPaint.setColor(-68676);
        } else if (color == 1) {
            preformattedBackgroundPaint.setColor(-1712440);
            webpageUrlPaint.setColor(-2365721);
            urlPaint.setColor(-3481882);
            tableHalfLinePaint.setColor(-3620432);
            tableLinePaint.setColor(-3620432);
            tableHeaderPaint.setColor(-1120560);
            tableStripPaint.setColor(-1120560);
            photoBackgroundPaint.setColor(-1120560);
            dividerPaint.setColor(-4080987);
            webpageMarkPaint.setColor(-1712691);
        } else if (color == 2) {
            preformattedBackgroundPaint.setColor(-15000805);
            webpageUrlPaint.setColor(-14536904);
            urlPaint.setColor(-14469050);
            tableHalfLinePaint.setColor(-13750738);
            tableLinePaint.setColor(-13750738);
            tableHeaderPaint.setColor(-15066598);
            tableStripPaint.setColor(-15066598);
            photoBackgroundPaint.setColor(-14935012);
            dividerPaint.setColor(-12303292);
            webpageMarkPaint.setColor(-14408668);
        }
        quoteLinePaint.setColor(getTextColor());
    }

    private void showCopyPopup(String urlFinal) {
        if (this.parentActivity != null) {
            if (this.linkSheet != null) {
                this.linkSheet.lambda$new$1$ThemeEditorView$EditorAlert();
                this.linkSheet = null;
            }
            Builder builder = new Builder(this.parentActivity);
            builder.setUseFullscreen(true);
            builder.setTitle(urlFinal);
            builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new ArticleViewer$$Lambda$0(this, urlFinal));
            BottomSheet sheet = builder.create();
            showDialog(sheet);
            for (int a = 0; a < 2; a++) {
                sheet.setItemColor(a, getTextColor(), getTextColor());
            }
            sheet.setTitleColor(getGrayTextColor());
            if (this.selectedColor == 0) {
                sheet.setBackgroundColor(-1);
            } else if (this.selectedColor == 1) {
                sheet.setBackgroundColor(-659492);
            } else if (this.selectedColor == 2) {
                sheet.setBackgroundColor(-15461356);
            }
        }
    }

    final /* synthetic */ void lambda$showCopyPopup$0$ArticleViewer(String urlFinal, DialogInterface dialog, int which) {
        if (this.parentActivity != null) {
            if (which == 0) {
                Browser.openUrl(this.parentActivity, urlFinal);
            } else if (which == 1) {
                String url = urlFinal;
                if (url.startsWith("mailto:")) {
                    url = url.substring(7);
                } else if (url.startsWith("tel:")) {
                    url = url.substring(4);
                }
                AndroidUtilities.addToClipboard(url);
            }
        }
    }

    private void showPopup(View parent, int gravity, int x, int y) {
        if (this.popupWindow == null || !this.popupWindow.isShowing()) {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                this.popupLayout = new ActionBarPopupWindowLayout(this.parentActivity);
                this.popupLayout.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
                Drawable drawable = this.parentActivity.getResources().getDrawable(R.drawable.menu_copy);
                this.copyBackgroundDrawable = drawable;
                actionBarPopupWindowLayout.setBackgroundDrawable(drawable);
                this.popupLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new ArticleViewer$$Lambda$1(this));
                this.popupLayout.setDispatchKeyEventListener(new ArticleViewer$$Lambda$2(this));
                this.popupLayout.setShowedFromBotton(false);
                this.deleteView = new TextView(this.parentActivity);
                this.deleteView.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 2));
                this.deleteView.setGravity(16);
                this.deleteView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
                this.deleteView.setTextSize(1, 15.0f);
                this.deleteView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.deleteView.setText(LocaleController.getString("Copy", R.string.Copy).toUpperCase());
                this.deleteView.setOnClickListener(new ArticleViewer$$Lambda$3(this));
                this.popupLayout.addView(this.deleteView, LayoutHelper.createFrame(-2, 48.0f));
                this.popupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                this.popupWindow.setAnimationEnabled(false);
                this.popupWindow.setAnimationStyle(R.style.PopupAnimation);
                this.popupWindow.setOutsideTouchable(true);
                this.popupWindow.setClippingEnabled(true);
                this.popupWindow.setInputMethodMode(2);
                this.popupWindow.setSoftInputMode(0);
                this.popupWindow.getContentView().setFocusableInTouchMode(true);
                this.popupWindow.setOnDismissListener(new ArticleViewer$$Lambda$4(this));
            }
            if (this.selectedColor == 2) {
                this.deleteView.setTextColor(-5723992);
                if (this.copyBackgroundDrawable != null) {
                    this.copyBackgroundDrawable.setColorFilter(new PorterDuffColorFilter(-14408668, Mode.MULTIPLY));
                }
            } else {
                this.deleteView.setTextColor(-14606047);
                if (this.copyBackgroundDrawable != null) {
                    this.copyBackgroundDrawable.setColorFilter(new PorterDuffColorFilter(-1, Mode.MULTIPLY));
                }
            }
            this.popupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.popupWindow.setFocusable(true);
            this.popupWindow.showAtLocation(parent, gravity, x, y);
            this.popupWindow.startAnimation();
            return;
        }
        this.popupWindow.dismiss();
    }

    final /* synthetic */ boolean lambda$showPopup$1$ArticleViewer(View v, MotionEvent event) {
        if (event.getActionMasked() == 0 && this.popupWindow != null && this.popupWindow.isShowing()) {
            v.getHitRect(this.popupRect);
            if (!this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                this.popupWindow.dismiss();
            }
        }
        return false;
    }

    final /* synthetic */ void lambda$showPopup$2$ArticleViewer(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && this.popupWindow != null && this.popupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    final /* synthetic */ void lambda$showPopup$3$ArticleViewer(View v) {
        if (this.pressedLinkOwnerLayout != null) {
            AndroidUtilities.addToClipboard(this.pressedLinkOwnerLayout.getText());
            Toast.makeText(this.parentActivity, LocaleController.getString("TextCopied", R.string.TextCopied), 0).show();
        }
        if (this.popupWindow != null && this.popupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    final /* synthetic */ void lambda$showPopup$4$ArticleViewer() {
        if (this.pressedLinkOwnerView != null) {
            this.pressedLinkOwnerLayout = null;
            this.pressedLinkOwnerView.invalidate();
            this.pressedLinkOwnerView = null;
        }
    }

    private RichText getBlockCaption(PageBlock block, int type) {
        if (type == 2) {
            RichText text1 = getBlockCaption(block, 0);
            if (text1 instanceof TL_textEmpty) {
                text1 = null;
            }
            RichText text2 = getBlockCaption(block, 1);
            if (text2 instanceof TL_textEmpty) {
                text2 = null;
            }
            if (text1 != null && text2 == null) {
                return text1;
            }
            if (text1 == null && text2 != null) {
                return text2;
            }
            if (text1 == null || text2 == null) {
                return null;
            }
            TL_textPlain text3 = new TL_textPlain();
            text3.text = " ";
            RichText textConcat = new TL_textConcat();
            textConcat.texts.add(text1);
            textConcat.texts.add(text3);
            textConcat.texts.add(text2);
            return textConcat;
        }
        if (block instanceof TL_pageBlockEmbedPost) {
            TL_pageBlockEmbedPost blockEmbedPost = (TL_pageBlockEmbedPost) block;
            if (type == 0) {
                return blockEmbedPost.caption.text;
            }
            if (type == 1) {
                return blockEmbedPost.caption.credit;
            }
        } else if (block instanceof TL_pageBlockSlideshow) {
            TL_pageBlockSlideshow pageBlockSlideshow = (TL_pageBlockSlideshow) block;
            if (type == 0) {
                return pageBlockSlideshow.caption.text;
            }
            if (type == 1) {
                return pageBlockSlideshow.caption.credit;
            }
        } else if (block instanceof TL_pageBlockPhoto) {
            TL_pageBlockPhoto pageBlockPhoto = (TL_pageBlockPhoto) block;
            if (type == 0) {
                return pageBlockPhoto.caption.text;
            }
            if (type == 1) {
                return pageBlockPhoto.caption.credit;
            }
        } else if (block instanceof TL_pageBlockCollage) {
            TL_pageBlockCollage pageBlockCollage = (TL_pageBlockCollage) block;
            if (type == 0) {
                return pageBlockCollage.caption.text;
            }
            if (type == 1) {
                return pageBlockCollage.caption.credit;
            }
        } else if (block instanceof TL_pageBlockEmbed) {
            TL_pageBlockEmbed pageBlockEmbed = (TL_pageBlockEmbed) block;
            if (type == 0) {
                return pageBlockEmbed.caption.text;
            }
            if (type == 1) {
                return pageBlockEmbed.caption.credit;
            }
        } else if (block instanceof TL_pageBlockBlockquote) {
            return ((TL_pageBlockBlockquote) block).caption;
        } else {
            if (block instanceof TL_pageBlockVideo) {
                TL_pageBlockVideo pageBlockVideo = (TL_pageBlockVideo) block;
                if (type == 0) {
                    return pageBlockVideo.caption.text;
                }
                if (type == 1) {
                    return pageBlockVideo.caption.credit;
                }
            } else if (block instanceof TL_pageBlockPullquote) {
                return ((TL_pageBlockPullquote) block).caption;
            } else {
                if (block instanceof TL_pageBlockAudio) {
                    TL_pageBlockAudio pageBlockAudio = (TL_pageBlockAudio) block;
                    if (type == 0) {
                        return pageBlockAudio.caption.text;
                    }
                    if (type == 1) {
                        return pageBlockAudio.caption.credit;
                    }
                } else if (block instanceof TL_pageBlockCover) {
                    return getBlockCaption(((TL_pageBlockCover) block).cover, type);
                } else if (block instanceof TL_pageBlockMap) {
                    TL_pageBlockMap pageBlockMap = (TL_pageBlockMap) block;
                    if (type == 0) {
                        return pageBlockMap.caption.text;
                    }
                    if (type == 1) {
                        return pageBlockMap.caption.credit;
                    }
                }
            }
        }
        return null;
    }

    private View getLastNonListCell(View view) {
        if (view instanceof BlockListItemCell) {
            return getLastNonListCell(((BlockListItemCell) view).blockLayout.itemView);
        }
        if (view instanceof BlockOrderedListItemCell) {
            return getLastNonListCell(((BlockOrderedListItemCell) view).blockLayout.itemView);
        }
        return view;
    }

    private boolean isListItemBlock(PageBlock block) {
        return (block instanceof TL_pageBlockListItem) || (block instanceof TL_pageBlockOrderedListItem);
    }

    private PageBlock getLastNonListPageBlock(PageBlock block) {
        if (block instanceof TL_pageBlockListItem) {
            TL_pageBlockListItem blockListItem = (TL_pageBlockListItem) block;
            if (blockListItem.blockItem != null) {
                return getLastNonListPageBlock(blockListItem.blockItem);
            }
            return blockListItem.blockItem;
        } else if (!(block instanceof TL_pageBlockOrderedListItem)) {
            return block;
        } else {
            TL_pageBlockOrderedListItem blockListItem2 = (TL_pageBlockOrderedListItem) block;
            if (blockListItem2.blockItem != null) {
                return getLastNonListPageBlock(blockListItem2.blockItem);
            }
            return blockListItem2.blockItem;
        }
    }

    private boolean openAllParentBlocks(TL_pageBlockDetailsChild child) {
        PageBlock parentBlock = getLastNonListPageBlock(child.parent);
        TL_pageBlockDetails blockDetails;
        if (parentBlock instanceof TL_pageBlockDetails) {
            blockDetails = (TL_pageBlockDetails) parentBlock;
            if (blockDetails.open) {
                return false;
            }
            blockDetails.open = true;
            return true;
        } else if (!(parentBlock instanceof TL_pageBlockDetailsChild)) {
            return false;
        } else {
            TL_pageBlockDetailsChild parent = (TL_pageBlockDetailsChild) parentBlock;
            parentBlock = getLastNonListPageBlock(parent.block);
            boolean opened = false;
            if (parentBlock instanceof TL_pageBlockDetails) {
                blockDetails = (TL_pageBlockDetails) parentBlock;
                if (!blockDetails.open) {
                    blockDetails.open = true;
                    opened = true;
                }
            }
            if (openAllParentBlocks(parent) || opened) {
                return true;
            }
            return false;
        }
    }

    private PageBlock fixListBlock(PageBlock parentBlock, PageBlock childBlock) {
        if (parentBlock instanceof TL_pageBlockListItem) {
            ((TL_pageBlockListItem) parentBlock).blockItem = childBlock;
            return parentBlock;
        } else if (!(parentBlock instanceof TL_pageBlockOrderedListItem)) {
            return childBlock;
        } else {
            ((TL_pageBlockOrderedListItem) parentBlock).blockItem = childBlock;
            return parentBlock;
        }
    }

    private PageBlock wrapInTableBlock(PageBlock parentBlock, PageBlock childBlock) {
        TL_pageBlockListItem item;
        if (parentBlock instanceof TL_pageBlockListItem) {
            TL_pageBlockListItem parent = (TL_pageBlockListItem) parentBlock;
            item = new TL_pageBlockListItem(this, null);
            item.parent = parent.parent;
            item.blockItem = wrapInTableBlock(parent.blockItem, childBlock);
            return item;
        } else if (!(parentBlock instanceof TL_pageBlockOrderedListItem)) {
            return childBlock;
        } else {
            TL_pageBlockOrderedListItem parent2 = (TL_pageBlockOrderedListItem) parentBlock;
            item = new TL_pageBlockOrderedListItem(this, null);
            item.parent = parent2.parent;
            item.blockItem = wrapInTableBlock(parent2.blockItem, childBlock);
            return item;
        }
    }

    private void updateInterfaceForCurrentPage(int order) {
        if (this.currentPage != null && this.currentPage.cached_page != null) {
            String str;
            this.isRtl = this.currentPage.cached_page.rtl;
            this.channelBlock = null;
            SimpleTextView simpleTextView = this.titleTextView;
            if (this.currentPage.site_name == null) {
                str = "";
            } else {
                str = this.currentPage.site_name;
            }
            simpleTextView.setText(str);
            if (order != 0) {
                WebpageAdapter adapterToUpdate = this.adapter[1];
                this.adapter[1] = this.adapter[0];
                this.adapter[0] = adapterToUpdate;
                RecyclerListView listToUpdate = this.listView[1];
                this.listView[1] = this.listView[0];
                this.listView[0] = listToUpdate;
                LinearLayoutManager layoutManagerToUpdate = this.layoutManager[1];
                this.layoutManager[1] = this.layoutManager[0];
                this.layoutManager[0] = layoutManagerToUpdate;
                int index1 = this.containerView.indexOfChild(this.listView[0]);
                int index2 = this.containerView.indexOfChild(this.listView[1]);
                if (order == 1) {
                    if (index1 < index2) {
                        this.containerView.removeView(this.listView[0]);
                        this.containerView.addView(this.listView[0], index2);
                    }
                } else if (index2 < index1) {
                    this.containerView.removeView(this.listView[0]);
                    this.containerView.addView(this.listView[0], index1);
                }
                this.pageSwitchAnimation = new AnimatorSet();
                this.listView[0].setVisibility(0);
                final int index = order == 1 ? 0 : 1;
                this.listView[index].setBackgroundColor(this.backgroundPaint.getColor());
                if (VERSION.SDK_INT >= 18) {
                    this.listView[index].setLayerType(2, null);
                }
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                float[] fArr;
                if (order == 1) {
                    animatorSet = this.pageSwitchAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.listView[0], View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(56.0f), 0.0f});
                    fArr = new float[2];
                    animatorArr[1] = ObjectAnimator.ofFloat(this.listView[0], View.ALPHA, new float[]{0.0f, 1.0f});
                    animatorSet.playTogether(animatorArr);
                } else if (order == -1) {
                    this.listView[0].setAlpha(1.0f);
                    this.listView[0].setTranslationX(0.0f);
                    animatorSet = this.pageSwitchAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.listView[1], View.TRANSLATION_X, new float[]{0.0f, (float) AndroidUtilities.dp(56.0f)});
                    fArr = new float[2];
                    animatorArr[1] = ObjectAnimator.ofFloat(this.listView[1], View.ALPHA, new float[]{1.0f, 0.0f});
                    animatorSet.playTogether(animatorArr);
                }
                this.pageSwitchAnimation.setDuration(150);
                this.pageSwitchAnimation.setInterpolator(this.interpolator);
                this.pageSwitchAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ArticleViewer.this.listView[1].setVisibility(8);
                        ArticleViewer.this.listView[index].setBackgroundDrawable(null);
                        if (VERSION.SDK_INT >= 18) {
                            ArticleViewer.this.listView[index].setLayerType(0, null);
                        }
                        ArticleViewer.this.pageSwitchAnimation = null;
                    }
                });
                this.pageSwitchAnimation.start();
            }
            this.headerView.invalidate();
            this.adapter[0].cleanup();
            int count = this.currentPage.cached_page.blocks.size();
            for (int a = 0; a < count; a++) {
                int i;
                PageBlock block = (PageBlock) this.currentPage.cached_page.blocks.get(a);
                if (a == 0) {
                    block.first = true;
                    if (block instanceof TL_pageBlockCover) {
                        PageBlock pageBlockCover = (TL_pageBlockCover) block;
                        RichText caption = getBlockCaption(pageBlockCover, 0);
                        RichText credit = getBlockCaption(pageBlockCover, 1);
                        if (!((caption == null || (caption instanceof TL_textEmpty)) && (credit == null || (credit instanceof TL_textEmpty))) && count > 1) {
                            PageBlock next = (PageBlock) this.currentPage.cached_page.blocks.get(1);
                            if (next instanceof TL_pageBlockChannel) {
                                this.channelBlock = (TL_pageBlockChannel) next;
                            }
                        }
                    }
                } else if (a == 1 && this.channelBlock != null) {
                }
                WebpageAdapter webpageAdapter = this.adapter[0];
                if (a == count - 1) {
                    i = a;
                } else {
                    i = 0;
                }
                webpageAdapter.addBlock(block, 0, 0, i);
            }
            this.adapter[0].notifyDataSetChanged();
            if (this.pagesStack.size() == 1 || order == -1) {
                int offset;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0);
                String key = "article" + this.currentPage.id;
                int position = preferences.getInt(key, -1);
                if (preferences.getBoolean(key + "r", true) == (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y)) {
                    offset = preferences.getInt(key + "o", 0) - this.listView[0].getPaddingTop();
                } else {
                    offset = AndroidUtilities.dp(10.0f);
                }
                if (position != -1) {
                    this.layoutManager[0].scrollToPositionWithOffset(position, offset);
                }
            } else {
                this.layoutManager[0].scrollToPositionWithOffset(0, 0);
            }
            checkScrollAnimated();
        }
    }

    private boolean addPageToStack(WebPage webPage, String anchor, int order) {
        saveCurrentPagePosition();
        this.currentPage = webPage;
        this.pagesStack.add(webPage);
        updateInterfaceForCurrentPage(order);
        return scrollToAnchor(anchor);
    }

    private boolean scrollToAnchor(String anchor) {
        if (TextUtils.isEmpty(anchor)) {
            return false;
        }
        anchor = anchor.toLowerCase();
        Integer row = (Integer) this.adapter[0].anchors.get(anchor);
        if (row == null) {
            return false;
        }
        TL_textAnchor textAnchor = (TL_textAnchor) this.adapter[0].anchorsParent.get(anchor);
        int type;
        ViewHolder holder;
        if (textAnchor != null) {
            TL_pageBlockParagraph paragraph = new TL_pageBlockParagraph();
            paragraph.text = textAnchor.text;
            type = this.adapter[0].getTypeForBlock(paragraph);
            holder = this.adapter[0].onCreateViewHolder(null, type);
            this.adapter[0].bindBlockToHolder(type, holder, paragraph, 0, 0);
            Builder builder = new Builder(this.parentActivity);
            builder.setUseFullscreen(true);
            builder.setApplyTopPadding(false);
            LinearLayout linearLayout = new LinearLayout(this.parentActivity);
            linearLayout.setOrientation(1);
            View anonymousClass3 = new TextView(this.parentActivity) {
                protected void onDraw(Canvas canvas) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
                    super.onDraw(canvas);
                }
            };
            anonymousClass3.setTextSize(1, 16.0f);
            anonymousClass3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            anonymousClass3.setText(LocaleController.getString("InstantViewReference", R.string.InstantViewReference));
            anonymousClass3.setGravity((this.isRtl ? 5 : 3) | 16);
            anonymousClass3.setTextColor(getTextColor());
            anonymousClass3.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            linearLayout.addView(anonymousClass3, new LinearLayout.LayoutParams(-1, AndroidUtilities.dp(48.0f) + 1));
            linearLayout.addView(holder.itemView, LayoutHelper.createLinear(-1, -2, 0.0f, 7.0f, 0.0f, 0.0f));
            builder.setCustomView(linearLayout);
            this.linkSheet = builder.create();
            if (this.selectedColor == 0) {
                this.linkSheet.setBackgroundColor(-1);
            } else if (this.selectedColor == 1) {
                this.linkSheet.setBackgroundColor(-659492);
            } else if (this.selectedColor == 2) {
                this.linkSheet.setBackgroundColor(-15461356);
            }
            showDialog(this.linkSheet);
            return true;
        } else if (row.intValue() < 0 || row.intValue() >= this.adapter[0].blocks.size()) {
            return false;
        } else {
            PageBlock originalBlock = (PageBlock) this.adapter[0].blocks.get(row.intValue());
            PageBlock block = getLastNonListPageBlock(originalBlock);
            if (block instanceof TL_pageBlockDetailsChild) {
                if (openAllParentBlocks((TL_pageBlockDetailsChild) block)) {
                    this.adapter[0].updateRows();
                    this.adapter[0].notifyDataSetChanged();
                }
            }
            int position = this.adapter[0].localBlocks.indexOf(originalBlock);
            if (position != -1) {
                row = Integer.valueOf(position);
            }
            Integer offset = (Integer) this.adapter[0].anchorsOffset.get(anchor);
            if (offset == null) {
                offset = Integer.valueOf(0);
            } else if (offset.intValue() == -1) {
                type = this.adapter[0].getTypeForBlock(originalBlock);
                holder = this.adapter[0].onCreateViewHolder(null, type);
                this.adapter[0].bindBlockToHolder(type, holder, originalBlock, 0, 0);
                holder.itemView.measure(MeasureSpec.makeMeasureSpec(this.listView[0].getMeasuredWidth(), NUM), MeasureSpec.makeMeasureSpec(0, 0));
                offset = (Integer) this.adapter[0].anchorsOffset.get(anchor);
                if (offset.intValue() == -1) {
                    offset = Integer.valueOf(0);
                }
            }
            this.layoutManager[0].scrollToPositionWithOffset(row.intValue(), (this.currentHeaderHeight - AndroidUtilities.dp(56.0f)) - offset.intValue());
            return true;
        }
    }

    private boolean removeLastPageFromStack() {
        if (this.pagesStack.size() < 2) {
            return false;
        }
        this.pagesStack.remove(this.pagesStack.size() - 1);
        this.currentPage = (WebPage) this.pagesStack.get(this.pagesStack.size() - 1);
        updateInterfaceForCurrentPage(-1);
        return true;
    }

    protected void startCheckLongPress() {
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap(this, null);
            }
            this.windowView.postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    protected void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        if (this.pendingCheckForLongPress != null) {
            this.windowView.removeCallbacks(this.pendingCheckForLongPress);
            this.pendingCheckForLongPress = null;
        }
        if (this.pendingCheckForTap != null) {
            this.windowView.removeCallbacks(this.pendingCheckForTap);
            this.pendingCheckForTap = null;
        }
    }

    private int getTextFlags(RichText richText) {
        if (richText instanceof TL_textFixed) {
            return getTextFlags(richText.parentRichText) | 4;
        }
        if (richText instanceof TL_textItalic) {
            return getTextFlags(richText.parentRichText) | 2;
        }
        if (richText instanceof TL_textBold) {
            return getTextFlags(richText.parentRichText) | 1;
        }
        if (richText instanceof TL_textUnderline) {
            return getTextFlags(richText.parentRichText) | 16;
        }
        if (richText instanceof TL_textStrike) {
            return getTextFlags(richText.parentRichText) | 32;
        }
        if (richText instanceof TL_textEmail) {
            return getTextFlags(richText.parentRichText) | 8;
        }
        if (richText instanceof TL_textPhone) {
            return getTextFlags(richText.parentRichText) | 8;
        }
        if (richText instanceof TL_textUrl) {
            if (((TL_textUrl) richText).webpage_id != 0) {
                return getTextFlags(richText.parentRichText) | 512;
            }
            return getTextFlags(richText.parentRichText) | 8;
        } else if (richText instanceof TL_textSubscript) {
            return getTextFlags(richText.parentRichText) | 128;
        } else {
            if (richText instanceof TL_textSuperscript) {
                return getTextFlags(richText.parentRichText) | 256;
            }
            if (richText instanceof TL_textMarked) {
                return getTextFlags(richText.parentRichText) | 64;
            }
            if (richText != null) {
                return getTextFlags(richText.parentRichText);
            }
            return 0;
        }
    }

    private RichText getLastRichText(RichText richText) {
        if (richText == null) {
            return null;
        }
        if (richText instanceof TL_textFixed) {
            return getLastRichText(((TL_textFixed) richText).text);
        }
        if (richText instanceof TL_textItalic) {
            return getLastRichText(((TL_textItalic) richText).text);
        }
        if (richText instanceof TL_textBold) {
            return getLastRichText(((TL_textBold) richText).text);
        }
        if (richText instanceof TL_textUnderline) {
            return getLastRichText(((TL_textUnderline) richText).text);
        }
        if (richText instanceof TL_textStrike) {
            return getLastRichText(((TL_textStrike) richText).text);
        }
        if (richText instanceof TL_textEmail) {
            return getLastRichText(((TL_textEmail) richText).text);
        }
        if (richText instanceof TL_textUrl) {
            return getLastRichText(((TL_textUrl) richText).text);
        }
        if (richText instanceof TL_textAnchor) {
            getLastRichText(((TL_textAnchor) richText).text);
            return richText;
        } else if (richText instanceof TL_textSubscript) {
            return getLastRichText(((TL_textSubscript) richText).text);
        } else {
            if (richText instanceof TL_textSuperscript) {
                return getLastRichText(((TL_textSuperscript) richText).text);
            }
            if (richText instanceof TL_textMarked) {
                return getLastRichText(((TL_textMarked) richText).text);
            }
            if (richText instanceof TL_textPhone) {
                return getLastRichText(((TL_textPhone) richText).text);
            }
            return richText;
        }
    }

    private CharSequence getText(View parentView, RichText parentRichText, RichText richText, PageBlock parentBlock, int maxWidth) {
        if (richText == null) {
            return null;
        }
        CharSequence spannableStringBuilder;
        MetricAffectingSpan[] innerSpans;
        TextPaint textPaint;
        MetricAffectingSpan textPaintWebpageUrlSpan;
        if (richText instanceof TL_textFixed) {
            return getText(parentView, parentRichText, ((TL_textFixed) richText).text, parentBlock, maxWidth);
        } else if (richText instanceof TL_textItalic) {
            return getText(parentView, parentRichText, ((TL_textItalic) richText).text, parentBlock, maxWidth);
        } else if (richText instanceof TL_textBold) {
            return getText(parentView, parentRichText, ((TL_textBold) richText).text, parentBlock, maxWidth);
        } else if (richText instanceof TL_textUnderline) {
            return getText(parentView, parentRichText, ((TL_textUnderline) richText).text, parentBlock, maxWidth);
        } else if (richText instanceof TL_textStrike) {
            return getText(parentView, parentRichText, ((TL_textStrike) richText).text, parentBlock, maxWidth);
        } else if (richText instanceof TL_textEmail) {
            spannableStringBuilder = new SpannableStringBuilder(getText(parentView, parentRichText, ((TL_textEmail) richText).text, parentBlock, maxWidth));
            innerSpans = (MetricAffectingSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), MetricAffectingSpan.class);
            if (spannableStringBuilder.length() == 0) {
                return spannableStringBuilder;
            }
            textPaint = (innerSpans == null || innerSpans.length == 0) ? getTextPaint(parentRichText, richText, parentBlock) : null;
            spannableStringBuilder.setSpan(new TextPaintUrlSpan(textPaint, "mailto:" + getUrl(richText)), 0, spannableStringBuilder.length(), 33);
            return spannableStringBuilder;
        } else if (richText instanceof TL_textUrl) {
            TL_textUrl textUrl = (TL_textUrl) richText;
            spannableStringBuilder = new SpannableStringBuilder(getText(parentView, parentRichText, ((TL_textUrl) richText).text, parentBlock, maxWidth));
            innerSpans = (MetricAffectingSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), MetricAffectingSpan.class);
            TextPaint paint = (innerSpans == null || innerSpans.length == 0) ? getTextPaint(parentRichText, richText, parentBlock) : null;
            if (textUrl.webpage_id != 0) {
                textPaintWebpageUrlSpan = new TextPaintWebpageUrlSpan(paint, getUrl(richText));
            } else {
                textPaintWebpageUrlSpan = new TextPaintUrlSpan(paint, getUrl(richText));
            }
            if (spannableStringBuilder.length() == 0) {
                return spannableStringBuilder;
            }
            spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), 33);
            return spannableStringBuilder;
        } else if (richText instanceof TL_textPlain) {
            return ((TL_textPlain) richText).text;
        } else {
            if (richText instanceof TL_textAnchor) {
                TL_textAnchor textAnchor = (TL_textAnchor) richText;
                spannableStringBuilder = new SpannableStringBuilder(getText(parentView, parentRichText, textAnchor.text, parentBlock, maxWidth));
                spannableStringBuilder.setSpan(new AnchorSpan(textAnchor.name), 0, spannableStringBuilder.length(), 17);
                return spannableStringBuilder;
            } else if (richText instanceof TL_textEmpty) {
                return "";
            } else {
                if (richText instanceof TL_textConcat) {
                    CharSequence spannableStringBuilder2 = new SpannableStringBuilder();
                    int count = richText.texts.size();
                    int a = 0;
                    while (a < count) {
                        RichText innerRichText = (RichText) richText.texts.get(a);
                        RichText lastRichText = getLastRichText(innerRichText);
                        boolean extraSpace = maxWidth >= 0 && (innerRichText instanceof TL_textUrl) && ((TL_textUrl) innerRichText).webpage_id != 0;
                        if (extraSpace && spannableStringBuilder2.length() != 0) {
                            if (spannableStringBuilder2.charAt(spannableStringBuilder2.length() - 1) != 10) {
                                spannableStringBuilder2.append(" ");
                            }
                        }
                        CharSequence innerText = getText(parentView, parentRichText, innerRichText, parentBlock, maxWidth);
                        int flags = getTextFlags(lastRichText);
                        int startLength = spannableStringBuilder2.length();
                        spannableStringBuilder2.append(innerText);
                        if (!(flags == 0 || (innerText instanceof SpannableStringBuilder))) {
                            if ((flags & 8) != 0 || (flags & 512) != 0) {
                                String url = getUrl(innerRichText);
                                if (url == null) {
                                    url = getUrl(parentRichText);
                                }
                                if ((flags & 512) != 0) {
                                    textPaintWebpageUrlSpan = new TextPaintWebpageUrlSpan(getTextPaint(parentRichText, lastRichText, parentBlock), url);
                                } else {
                                    textPaintWebpageUrlSpan = new TextPaintUrlSpan(getTextPaint(parentRichText, lastRichText, parentBlock), url);
                                }
                                if (startLength != spannableStringBuilder2.length()) {
                                    spannableStringBuilder2.setSpan(span, startLength, spannableStringBuilder2.length(), 33);
                                }
                            } else if (startLength != spannableStringBuilder2.length()) {
                                spannableStringBuilder2.setSpan(new TextPaintSpan(getTextPaint(parentRichText, lastRichText, parentBlock)), startLength, spannableStringBuilder2.length(), 33);
                            }
                        }
                        if (extraSpace && a != count - 1) {
                            spannableStringBuilder2.append(" ");
                        }
                        a++;
                    }
                    return spannableStringBuilder2;
                } else if (richText instanceof TL_textSubscript) {
                    return getText(parentView, parentRichText, ((TL_textSubscript) richText).text, parentBlock, maxWidth);
                } else if (richText instanceof TL_textSuperscript) {
                    return getText(parentView, parentRichText, ((TL_textSuperscript) richText).text, parentBlock, maxWidth);
                } else if (richText instanceof TL_textMarked) {
                    spannableStringBuilder = new SpannableStringBuilder(getText(parentView, parentRichText, ((TL_textMarked) richText).text, parentBlock, maxWidth));
                    innerSpans = (MetricAffectingSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), MetricAffectingSpan.class);
                    if (spannableStringBuilder.length() == 0) {
                        return spannableStringBuilder;
                    }
                    textPaint = (innerSpans == null || innerSpans.length == 0) ? getTextPaint(parentRichText, richText, parentBlock) : null;
                    spannableStringBuilder.setSpan(new TextPaintMarkSpan(textPaint), 0, spannableStringBuilder.length(), 33);
                    return spannableStringBuilder;
                } else if (richText instanceof TL_textPhone) {
                    spannableStringBuilder = new SpannableStringBuilder(getText(parentView, parentRichText, ((TL_textPhone) richText).text, parentBlock, maxWidth));
                    innerSpans = (MetricAffectingSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), MetricAffectingSpan.class);
                    if (spannableStringBuilder.length() == 0) {
                        return spannableStringBuilder;
                    }
                    textPaint = (innerSpans == null || innerSpans.length == 0) ? getTextPaint(parentRichText, richText, parentBlock) : null;
                    spannableStringBuilder.setSpan(new TextPaintUrlSpan(textPaint, "tel:" + getUrl(richText)), 0, spannableStringBuilder.length(), 33);
                    return spannableStringBuilder;
                } else if (!(richText instanceof TL_textImage)) {
                    return "not supported " + richText;
                } else {
                    TL_textImage textImage = (TL_textImage) richText;
                    Document document = getDocumentWithId(textImage.document_id);
                    if (document == null) {
                        return "";
                    }
                    spannableStringBuilder = new SpannableStringBuilder("*");
                    int w = AndroidUtilities.dp((float) textImage.w);
                    int h = AndroidUtilities.dp((float) textImage.h);
                    maxWidth = Math.abs(maxWidth);
                    if (w > maxWidth) {
                        float scale = ((float) maxWidth) / ((float) w);
                        w = maxWidth;
                        h = (int) (((float) h) * scale);
                    }
                    spannableStringBuilder.setSpan(new TextPaintImageReceiverSpan(parentView, document, this.currentPage, w, h, false, this.selectedColor == 2), 0, spannableStringBuilder.length(), 33);
                    return spannableStringBuilder;
                }
            }
        }
    }

    private String getUrl(RichText richText) {
        if (richText instanceof TL_textFixed) {
            return getUrl(((TL_textFixed) richText).text);
        }
        if (richText instanceof TL_textItalic) {
            return getUrl(((TL_textItalic) richText).text);
        }
        if (richText instanceof TL_textBold) {
            return getUrl(((TL_textBold) richText).text);
        }
        if (richText instanceof TL_textUnderline) {
            return getUrl(((TL_textUnderline) richText).text);
        }
        if (richText instanceof TL_textStrike) {
            return getUrl(((TL_textStrike) richText).text);
        }
        if (richText instanceof TL_textEmail) {
            return ((TL_textEmail) richText).email;
        }
        if (richText instanceof TL_textUrl) {
            return ((TL_textUrl) richText).url;
        }
        if (richText instanceof TL_textPhone) {
            return ((TL_textPhone) richText).phone;
        }
        return null;
    }

    private int getTextColor() {
        switch (getSelectedColor()) {
            case 0:
            case 1:
                return -14606047;
            default:
                return -6710887;
        }
    }

    private int getInstantLinkBackgroundColor() {
        switch (getSelectedColor()) {
            case 0:
                return -1707782;
            case 1:
                return -2498337;
            default:
                return -14536904;
        }
    }

    private int getLinkTextColor() {
        switch (getSelectedColor()) {
            case 0:
                return -15435321;
            case 1:
                return -13471296;
            default:
                return -10838585;
        }
    }

    private int getGrayTextColor() {
        switch (getSelectedColor()) {
            case 0:
                return -8156010;
            case 1:
                return -11711675;
            default:
                return -10066330;
        }
    }

    private TextPaint getTextPaint(RichText parentRichText, RichText richText, PageBlock parentBlock) {
        int additionalSize;
        int flags = getTextFlags(richText);
        SparseArray<TextPaint> currentMap = null;
        int textSize = AndroidUtilities.dp(14.0f);
        int textColor = -65536;
        if (this.selectedFontSize == 0) {
            additionalSize = -AndroidUtilities.dp(4.0f);
        } else if (this.selectedFontSize == 1) {
            additionalSize = -AndroidUtilities.dp(2.0f);
        } else if (this.selectedFontSize == 3) {
            additionalSize = AndroidUtilities.dp(2.0f);
        } else if (this.selectedFontSize == 4) {
            additionalSize = AndroidUtilities.dp(4.0f);
        } else {
            additionalSize = 0;
        }
        if (parentBlock instanceof TL_pageBlockPhoto) {
            TL_pageBlockPhoto pageBlockPhoto = (TL_pageBlockPhoto) parentBlock;
            if (pageBlockPhoto.caption.text == richText || pageBlockPhoto.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockMap) {
            TL_pageBlockMap pageBlockMap = (TL_pageBlockMap) parentBlock;
            if (pageBlockMap.caption.text == richText || pageBlockMap.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockTitle) {
            currentMap = titleTextPaints;
            textSize = AndroidUtilities.dp(24.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockKicker) {
            currentMap = kickerTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockAuthorDate) {
            currentMap = authorTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockFooter) {
            currentMap = footerTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockSubtitle) {
            currentMap = subtitleTextPaints;
            textSize = AndroidUtilities.dp(21.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockHeader) {
            currentMap = headerTextPaints;
            textSize = AndroidUtilities.dp(21.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockSubheader) {
            currentMap = subheaderTextPaints;
            textSize = AndroidUtilities.dp(18.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockBlockquote) {
            TL_pageBlockBlockquote pageBlockBlockquote = (TL_pageBlockBlockquote) parentBlock;
            if (pageBlockBlockquote.text == parentRichText) {
                currentMap = quoteTextPaints;
                textSize = AndroidUtilities.dp(15.0f);
                textColor = getTextColor();
            } else if (pageBlockBlockquote.caption == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
                textColor = getGrayTextColor();
            }
        } else if (parentBlock instanceof TL_pageBlockPullquote) {
            TL_pageBlockPullquote pageBlockBlockquote2 = (TL_pageBlockPullquote) parentBlock;
            if (pageBlockBlockquote2.text == parentRichText) {
                currentMap = quoteTextPaints;
                textSize = AndroidUtilities.dp(15.0f);
                textColor = getTextColor();
            } else if (pageBlockBlockquote2.caption == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
                textColor = getGrayTextColor();
            }
        } else if (parentBlock instanceof TL_pageBlockPreformatted) {
            currentMap = preformattedTextPaints;
            textSize = AndroidUtilities.dp(14.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockParagraph) {
            currentMap = paragraphTextPaints;
            textSize = AndroidUtilities.dp(16.0f);
            textColor = getTextColor();
        } else if (isListItemBlock(parentBlock)) {
            currentMap = listTextPaints;
            textSize = AndroidUtilities.dp(16.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockEmbed) {
            TL_pageBlockEmbed pageBlockEmbed = (TL_pageBlockEmbed) parentBlock;
            if (pageBlockEmbed.caption.text == richText || pageBlockEmbed.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockSlideshow) {
            TL_pageBlockSlideshow pageBlockSlideshow = (TL_pageBlockSlideshow) parentBlock;
            if (pageBlockSlideshow.caption.text == richText || pageBlockSlideshow.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockCollage) {
            TL_pageBlockCollage pageBlockCollage = (TL_pageBlockCollage) parentBlock;
            if (pageBlockCollage.caption.text == richText || pageBlockCollage.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockEmbedPost) {
            TL_pageBlockEmbedPost pageBlockEmbedPost = (TL_pageBlockEmbedPost) parentBlock;
            if (richText == pageBlockEmbedPost.caption.text) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
                textColor = getGrayTextColor();
            } else {
                if (richText == pageBlockEmbedPost.caption.credit) {
                    currentMap = photoCreditTextPaints;
                    textSize = AndroidUtilities.dp(12.0f);
                    textColor = getGrayTextColor();
                } else if (richText != null) {
                    currentMap = embedPostTextPaints;
                    textSize = AndroidUtilities.dp(14.0f);
                    textColor = getTextColor();
                }
            }
        } else if (parentBlock instanceof TL_pageBlockVideo) {
            if (richText == ((TL_pageBlockVideo) parentBlock).caption.text) {
                currentMap = mediaCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
                textColor = getTextColor();
            } else {
                currentMap = mediaCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
                textColor = getTextColor();
            }
        } else if (parentBlock instanceof TL_pageBlockAudio) {
            if (richText == ((TL_pageBlockAudio) parentBlock).caption.text) {
                currentMap = mediaCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
                textColor = getTextColor();
            } else {
                currentMap = mediaCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
                textColor = getTextColor();
            }
        } else if (parentBlock instanceof TL_pageBlockRelatedArticles) {
            currentMap = relatedArticleTextPaints;
            textSize = AndroidUtilities.dp(15.0f);
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockDetails) {
            currentMap = detailsTextPaints;
            textSize = AndroidUtilities.dp(15.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockTable) {
            currentMap = tableTextPaints;
            textSize = AndroidUtilities.dp(15.0f);
            textColor = getTextColor();
        }
        if (!((flags & 256) == 0 && (flags & 128) == 0)) {
            textSize -= AndroidUtilities.dp(4.0f);
        }
        if (currentMap == null) {
            if (errorTextPaint == null) {
                errorTextPaint = new TextPaint(1);
                errorTextPaint.setColor(-65536);
            }
            errorTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            return errorTextPaint;
        }
        TextPaint paint = (TextPaint) currentMap.get(flags);
        if (paint == null) {
            paint = new TextPaint(1);
            if ((flags & 4) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
            } else if (parentBlock instanceof TL_pageBlockRelatedArticles) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            } else if (this.selectedFont == 1 || (parentBlock instanceof TL_pageBlockTitle) || (parentBlock instanceof TL_pageBlockKicker) || (parentBlock instanceof TL_pageBlockHeader) || (parentBlock instanceof TL_pageBlockSubtitle) || (parentBlock instanceof TL_pageBlockSubheader)) {
                if ((flags & 1) != 0 && (flags & 2) != 0) {
                    paint.setTypeface(Typeface.create("serif", 3));
                } else if ((flags & 1) != 0) {
                    paint.setTypeface(Typeface.create("serif", 1));
                } else if ((flags & 2) != 0) {
                    paint.setTypeface(Typeface.create("serif", 2));
                } else {
                    paint.setTypeface(Typeface.create("serif", 0));
                }
            } else if ((flags & 1) != 0 && (flags & 2) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf"));
            } else if ((flags & 1) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            } else if ((flags & 2) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/ritalic.ttf"));
            }
            if ((flags & 32) != 0) {
                paint.setFlags(paint.getFlags() | 16);
            }
            if ((flags & 16) != 0) {
                paint.setFlags(paint.getFlags() | 8);
            }
            if (!((flags & 8) == 0 && (flags & 512) == 0)) {
                paint.setFlags(paint.getFlags());
                textColor = getLinkTextColor();
            }
            if ((flags & 256) != 0) {
                paint.baselineShift -= AndroidUtilities.dp(6.0f);
            } else if ((flags & 128) != 0) {
                paint.baselineShift += AndroidUtilities.dp(2.0f);
            }
            paint.setColor(textColor);
            currentMap.put(flags, paint);
        }
        paint.setTextSize((float) (textSize + additionalSize));
        return paint;
    }

    private DrawingText createLayoutForText(View parentView, CharSequence plainText, RichText richText, int width, PageBlock parentBlock, WebpageAdapter parentAdapter) {
        return createLayoutForText(parentView, plainText, richText, width, 0, parentBlock, Alignment.ALIGN_NORMAL, 0, parentAdapter);
    }

    private DrawingText createLayoutForText(View parentView, CharSequence plainText, RichText richText, int width, PageBlock parentBlock, Alignment align, WebpageAdapter parentAdapter) {
        return createLayoutForText(parentView, plainText, richText, width, 0, parentBlock, align, 0, parentAdapter);
    }

    private DrawingText createLayoutForText(View parentView, CharSequence plainText, RichText richText, int width, int textY, PageBlock parentBlock, WebpageAdapter parentAdapter) {
        return createLayoutForText(parentView, plainText, richText, width, textY, parentBlock, Alignment.ALIGN_NORMAL, 0, parentAdapter);
    }

    private DrawingText createLayoutForText(View parentView, CharSequence plainText, RichText richText, int width, int textY, PageBlock parentBlock, Alignment align, int maxLines, WebpageAdapter parentAdapter) {
        if (plainText == null && (richText == null || (richText instanceof TL_textEmpty))) {
            return null;
        }
        CharSequence text;
        if (width < 0) {
            width = AndroidUtilities.dp(10.0f);
        }
        int color = getSelectedColor();
        if (plainText != null) {
            text = plainText;
        } else {
            text = getText(parentView, richText, richText, parentBlock, width);
        }
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        int additionalSize;
        TextPaint paint;
        StaticLayout result;
        if (this.selectedFontSize == 0) {
            additionalSize = -AndroidUtilities.dp(4.0f);
        } else if (this.selectedFontSize == 1) {
            additionalSize = -AndroidUtilities.dp(2.0f);
        } else if (this.selectedFontSize == 3) {
            additionalSize = AndroidUtilities.dp(2.0f);
        } else if (this.selectedFontSize == 4) {
            additionalSize = AndroidUtilities.dp(4.0f);
        } else {
            additionalSize = 0;
        }
        if ((parentBlock instanceof TL_pageBlockEmbedPost) && richText == null) {
            if (((TL_pageBlockEmbedPost) parentBlock).author == plainText) {
                if (embedPostAuthorPaint == null) {
                    embedPostAuthorPaint = new TextPaint(1);
                    embedPostAuthorPaint.setColor(getTextColor());
                }
                embedPostAuthorPaint.setTextSize((float) (AndroidUtilities.dp(15.0f) + additionalSize));
                paint = embedPostAuthorPaint;
            } else {
                if (embedPostDatePaint == null) {
                    embedPostDatePaint = new TextPaint(1);
                    if (color == 0) {
                        embedPostDatePaint.setColor(-7366752);
                    } else {
                        embedPostDatePaint.setColor(getGrayTextColor());
                    }
                }
                embedPostDatePaint.setTextSize((float) (AndroidUtilities.dp(14.0f) + additionalSize));
                paint = embedPostDatePaint;
            }
        } else if (parentBlock instanceof TL_pageBlockChannel) {
            if (channelNamePaint == null) {
                channelNamePaint = new TextPaint(1);
                channelNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            }
            if (this.channelBlock == null) {
                channelNamePaint.setColor(getTextColor());
            } else {
                channelNamePaint.setColor(-1);
            }
            channelNamePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            paint = channelNamePaint;
        } else if (parentBlock instanceof TL_pageBlockRelatedArticlesChild) {
            TL_pageBlockRelatedArticlesChild pageBlockRelatedArticlesChild = (TL_pageBlockRelatedArticlesChild) parentBlock;
            if (plainText == ((TL_pageRelatedArticle) pageBlockRelatedArticlesChild.parent.articles.get(pageBlockRelatedArticlesChild.num)).title) {
                if (relatedArticleHeaderPaint == null) {
                    relatedArticleHeaderPaint = new TextPaint(1);
                    relatedArticleHeaderPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                }
                relatedArticleHeaderPaint.setColor(getTextColor());
                relatedArticleHeaderPaint.setTextSize((float) (AndroidUtilities.dp(15.0f) + additionalSize));
                paint = relatedArticleHeaderPaint;
            } else {
                if (relatedArticleTextPaint == null) {
                    relatedArticleTextPaint = new TextPaint(1);
                }
                relatedArticleTextPaint.setColor(getGrayTextColor());
                relatedArticleTextPaint.setTextSize((float) (AndroidUtilities.dp(14.0f) + additionalSize));
                paint = relatedArticleTextPaint;
            }
        } else if (!isListItemBlock(parentBlock) || plainText == null) {
            paint = getTextPaint(richText, richText, parentBlock);
        } else {
            if (listTextPointerPaint == null) {
                listTextPointerPaint = new TextPaint(1);
                listTextPointerPaint.setColor(getTextColor());
            }
            if (listTextNumPaint == null) {
                listTextNumPaint = new TextPaint(1);
                listTextNumPaint.setColor(getTextColor());
            }
            listTextPointerPaint.setTextSize((float) (AndroidUtilities.dp(19.0f) + additionalSize));
            listTextNumPaint.setTextSize((float) (AndroidUtilities.dp(16.0f) + additionalSize));
            if (!(parentBlock instanceof TL_pageBlockListItem) || ((TL_pageBlockListItem) parentBlock).parent.pageBlockList.ordered) {
                paint = listTextNumPaint;
            } else {
                paint = listTextPointerPaint;
            }
        }
        if (maxLines == 0) {
            if (text.charAt(text.length() - 1) == 10) {
                text = text.subSequence(0, text.length() - 1);
            }
            if (parentBlock instanceof TL_pageBlockPullquote) {
                result = new StaticLayout(text, paint, width, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            } else {
                result = new StaticLayout(text, paint, width, align, 1.0f, (float) AndroidUtilities.dp(4.0f), false);
            }
        } else if (parentBlock instanceof TL_pageBlockPullquote) {
            result = StaticLayoutEx.createStaticLayout(text, paint, width, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false, TruncateAt.END, width, maxLines);
        } else {
            result = StaticLayoutEx.createStaticLayout(text, paint, width, align, 1.0f, (float) AndroidUtilities.dp(4.0f), false, TruncateAt.END, width, maxLines);
        }
        if (result == null) {
            return null;
        }
        CharSequence finalText = result.getText();
        LinkPath textPath = null;
        LinkPath markPath = null;
        if (result != null && (finalText instanceof Spanned)) {
            int a;
            Path linkPath;
            int start;
            int end;
            int shift;
            int dp;
            Spanned spanned = (Spanned) finalText;
            try {
                AnchorSpan[] innerSpans = (AnchorSpan[]) spanned.getSpans(0, spanned.length(), AnchorSpan.class);
                int linesCount = result.getLineCount();
                if (innerSpans != null && innerSpans.length > 0) {
                    for (a = 0; a < innerSpans.length; a++) {
                        if (linesCount <= 1) {
                            parentAdapter.anchorsOffset.put(innerSpans[a].getName(), Integer.valueOf(textY));
                        } else {
                            parentAdapter.anchorsOffset.put(innerSpans[a].getName(), Integer.valueOf(result.getLineTop(result.getLineForOffset(spanned.getSpanStart(innerSpans[a]))) + textY));
                        }
                    }
                }
            } catch (Exception e) {
            }
            try {
                TextPaintWebpageUrlSpan[] innerSpans2 = (TextPaintWebpageUrlSpan[]) spanned.getSpans(0, spanned.length(), TextPaintWebpageUrlSpan.class);
                if (innerSpans2 != null && innerSpans2.length > 0) {
                    linkPath = new LinkPath(true);
                    try {
                        linkPath.setAllowReset(false);
                        for (a = 0; a < innerSpans2.length; a++) {
                            start = spanned.getSpanStart(innerSpans2[a]);
                            end = spanned.getSpanEnd(innerSpans2[a]);
                            linkPath.setCurrentLayout(result, start, 0.0f);
                            shift = innerSpans2[a].getTextPaint() != null ? innerSpans2[a].getTextPaint().baselineShift : 0;
                            if (shift != 0) {
                                dp = AndroidUtilities.dp(shift > 0 ? 5.0f : -2.0f) + shift;
                            } else {
                                dp = 0;
                            }
                            linkPath.setBaselineShift(dp);
                            result.getSelectionPath(start, end, linkPath);
                        }
                        linkPath.setAllowReset(true);
                        textPath = linkPath;
                    } catch (Exception e2) {
                        Path textPath2 = linkPath;
                    }
                }
            } catch (Exception e3) {
            }
            try {
                TextPaintMarkSpan[] innerSpans3 = (TextPaintMarkSpan[]) spanned.getSpans(0, spanned.length(), TextPaintMarkSpan.class);
                if (innerSpans3 != null && innerSpans3.length > 0) {
                    linkPath = new LinkPath(true);
                    try {
                        linkPath.setAllowReset(false);
                        for (a = 0; a < innerSpans3.length; a++) {
                            start = spanned.getSpanStart(innerSpans3[a]);
                            end = spanned.getSpanEnd(innerSpans3[a]);
                            linkPath.setCurrentLayout(result, start, 0.0f);
                            shift = innerSpans3[a].getTextPaint() != null ? innerSpans3[a].getTextPaint().baselineShift : 0;
                            if (shift != 0) {
                                dp = AndroidUtilities.dp(shift > 0 ? 5.0f : -2.0f) + shift;
                            } else {
                                dp = 0;
                            }
                            linkPath.setBaselineShift(dp);
                            result.getSelectionPath(start, end, linkPath);
                        }
                        linkPath.setAllowReset(true);
                        markPath = linkPath;
                    } catch (Exception e4) {
                        Path markPath2 = linkPath;
                    }
                }
            } catch (Exception e5) {
            }
        }
        DrawingText drawingText = new DrawingText();
        drawingText.textLayout = result;
        drawingText.textPath = textPath;
        drawingText.markPath = markPath2;
        return drawingText;
    }

    private void drawLayoutLink(Canvas canvas, DrawingText layout) {
        if (canvas != null && layout != null && this.pressedLinkOwnerLayout == layout) {
            if (this.pressedLink != null) {
                canvas.drawPath(this.urlPath, urlPaint);
            } else if (this.drawBlockSelection && layout != null) {
                float width;
                float x;
                if (layout.getLineCount() == 1) {
                    width = layout.getLineWidth(0);
                    x = layout.getLineLeft(0);
                } else {
                    width = (float) layout.getWidth();
                    x = 0.0f;
                }
                Canvas canvas2 = canvas;
                canvas2.drawRect(((float) (-AndroidUtilities.dp(2.0f))) + x, 0.0f, ((float) AndroidUtilities.dp(2.0f)) + (x + width), (float) layout.getHeight(), urlPaint);
            }
        }
    }

    private boolean checkLayoutForLinks(MotionEvent event, View parentView, DrawingText drawingText, int layoutX, int layoutY) {
        if (this.pageSwitchAnimation != null || parentView == null || drawingText == null) {
            return false;
        }
        StaticLayout layout = drawingText.textLayout;
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean removeLink = false;
        if (event.getAction() == 0) {
            int a;
            float width = 0.0f;
            float left = 2.14748365E9f;
            int N = layout.getLineCount();
            for (a = 0; a < N; a++) {
                width = Math.max(layout.getLineWidth(a), width);
                left = Math.min(layout.getLineLeft(a), left);
            }
            if (((float) x) >= ((float) layoutX) + left && ((float) x) <= (((float) layoutX) + left) + width && y >= layoutY && y <= layout.getHeight() + layoutY) {
                this.pressedLinkOwnerLayout = drawingText;
                this.pressedLinkOwnerView = parentView;
                this.pressedLayoutY = layoutY;
                if (layout.getText() instanceof Spannable) {
                    int checkX = x - layoutX;
                    try {
                        int line = layout.getLineForVertical(y - layoutY);
                        int off = layout.getOffsetForHorizontal(line, (float) checkX);
                        left = layout.getLineLeft(line);
                        if (left <= ((float) checkX) && layout.getLineWidth(line) + left >= ((float) checkX)) {
                            Spannable buffer = (Spannable) layout.getText();
                            TextPaintUrlSpan[] link = (TextPaintUrlSpan[]) buffer.getSpans(off, off, TextPaintUrlSpan.class);
                            if (link != null && link.length > 0) {
                                this.pressedLink = link[0];
                                int pressedStart = buffer.getSpanStart(this.pressedLink);
                                int pressedEnd = buffer.getSpanEnd(this.pressedLink);
                                for (a = 1; a < link.length; a++) {
                                    TextPaintUrlSpan span = link[a];
                                    int start = buffer.getSpanStart(span);
                                    int end = buffer.getSpanEnd(span);
                                    if (pressedStart > start || end > pressedEnd) {
                                        this.pressedLink = span;
                                        pressedStart = start;
                                        pressedEnd = end;
                                    }
                                }
                                try {
                                    int dp;
                                    this.urlPath.setUseRoundRect(true);
                                    this.urlPath.setCurrentLayout(layout, pressedStart, 0.0f);
                                    int shift = this.pressedLink.getTextPaint() != null ? this.pressedLink.getTextPaint().baselineShift : 0;
                                    LinkPath linkPath = this.urlPath;
                                    if (shift != 0) {
                                        dp = AndroidUtilities.dp(shift > 0 ? 5.0f : -2.0f) + shift;
                                    } else {
                                        dp = 0;
                                    }
                                    linkPath.setBaselineShift(dp);
                                    layout.getSelectionPath(pressedStart, pressedEnd, this.urlPath);
                                    parentView.invalidate();
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                            }
                        }
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                    }
                }
            }
        } else if (event.getAction() == 1) {
            if (this.pressedLink != null) {
                removeLink = true;
                String url = this.pressedLink.getUrl();
                if (url != null) {
                    String anchor;
                    if (this.linkSheet != null) {
                        this.linkSheet.lambda$new$1$ThemeEditorView$EditorAlert();
                        this.linkSheet = null;
                    }
                    boolean isAnchor = false;
                    int index = url.lastIndexOf(35);
                    if (index != -1) {
                        String webPageUrl;
                        if (TextUtils.isEmpty(this.currentPage.cached_page.url)) {
                            webPageUrl = this.currentPage.url.toLowerCase();
                        } else {
                            webPageUrl = this.currentPage.cached_page.url.toLowerCase();
                        }
                        try {
                            anchor = URLDecoder.decode(url.substring(index + 1), "UTF-8");
                        } catch (Exception e3) {
                            anchor = "";
                        }
                        if (url.toLowerCase().contains(webPageUrl)) {
                            if (TextUtils.isEmpty(anchor)) {
                                this.layoutManager[0].scrollToPositionWithOffset(0, 0);
                                checkScrollAnimated();
                            } else {
                                scrollToAnchor(anchor);
                            }
                            isAnchor = true;
                        }
                    } else {
                        anchor = null;
                    }
                    if (!isAnchor) {
                        openWebpageUrl(this.pressedLink.getUrl(), anchor);
                    }
                }
            }
        } else if (event.getAction() == 3 && (this.popupWindow == null || !this.popupWindow.isShowing())) {
            removeLink = true;
        }
        if (removeLink) {
            removePressedLink();
        }
        if (event.getAction() == 0) {
            startCheckLongPress();
        }
        if (!(event.getAction() == 0 || event.getAction() == 2)) {
            cancelCheckLongPress();
        }
        if (!(parentView instanceof BlockDetailsCell)) {
            return this.pressedLinkOwnerLayout != null;
        } else {
            if (this.pressedLink != null) {
                return true;
            }
            return false;
        }
    }

    private void removePressedLink() {
        if (this.pressedLink != null || this.pressedLinkOwnerView != null) {
            View parentView = this.pressedLinkOwnerView;
            this.pressedLink = null;
            this.pressedLinkOwnerLayout = null;
            this.pressedLinkOwnerView = null;
            if (parentView != null) {
                parentView.invalidate();
            }
        }
    }

    private void openWebpageUrl(String url, String anchor) {
        if (this.openUrlReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.openUrlReqId, false);
            this.openUrlReqId = 0;
        }
        int reqId = this.lastReqId + 1;
        this.lastReqId = reqId;
        closePhoto(false);
        showProgressView(true, true);
        TL_messages_getWebPage req = new TL_messages_getWebPage();
        req.url = url;
        req.hash = 0;
        this.openUrlReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ArticleViewer$$Lambda$5(this, reqId, anchor, req));
    }

    final /* synthetic */ void lambda$openWebpageUrl$6$ArticleViewer(int reqId, String anchor, TL_messages_getWebPage req, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$43(this, reqId, response, anchor, req));
    }

    final /* synthetic */ void lambda$null$5$ArticleViewer(int reqId, TLObject response, String anchor, TL_messages_getWebPage req) {
        if (this.openUrlReqId != 0 && reqId == this.lastReqId) {
            this.openUrlReqId = 0;
            showProgressView(true, false);
            if (!this.isVisible) {
                return;
            }
            if ((response instanceof TL_webPage) && (((TL_webPage) response).cached_page instanceof TL_page)) {
                addPageToStack((TL_webPage) response, anchor, 1);
            } else {
                Browser.openUrl(this.parentActivity, req.url);
            }
        }
    }

    private Photo getPhotoWithId(long id) {
        if (this.currentPage == null || this.currentPage.cached_page == null) {
            return null;
        }
        if (this.currentPage.photo != null && this.currentPage.photo.id == id) {
            return this.currentPage.photo;
        }
        for (int a = 0; a < this.currentPage.cached_page.photos.size(); a++) {
            Photo photo = (Photo) this.currentPage.cached_page.photos.get(a);
            if (photo.id == id) {
                return photo;
            }
        }
        return null;
    }

    private Document getDocumentWithId(long id) {
        if (this.currentPage == null || this.currentPage.cached_page == null) {
            return null;
        }
        if (this.currentPage.document != null && this.currentPage.document.id == id) {
            return this.currentPage.document;
        }
        for (int a = 0; a < this.currentPage.cached_page.documents.size(); a++) {
            Document document = (Document) this.currentPage.cached_page.documents.get(a);
            if (document.id == id) {
                return document;
            }
        }
        return null;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        String location;
        int a;
        int i;
        int count;
        View view;
        BlockAudioCell cell;
        if (id == NotificationCenter.fileDidFailedLoad) {
            location = args[0];
            a = 0;
            while (a < 3) {
                if (this.currentFileNames[a] == null || !this.currentFileNames[a].equals(location)) {
                    a++;
                } else {
                    this.radialProgressViews[a].setProgress(1.0f, true);
                    checkProgress(a, true);
                    return;
                }
            }
        } else if (id == NotificationCenter.fileDidLoad) {
            location = (String) args[0];
            a = 0;
            while (a < 3) {
                if (this.currentFileNames[a] == null || !this.currentFileNames[a].equals(location)) {
                    a++;
                } else {
                    this.radialProgressViews[a].setProgress(1.0f, true);
                    checkProgress(a, true);
                    if (a == 0 && isMediaVideo(this.currentIndex)) {
                        onActionClick(false);
                        return;
                    }
                    return;
                }
            }
        } else if (id == NotificationCenter.FileLoadProgressChanged) {
            location = (String) args[0];
            a = 0;
            while (a < 3) {
                if (this.currentFileNames[a] != null && this.currentFileNames[a].equals(location)) {
                    this.radialProgressViews[a].setProgress(args[1].floatValue(), true);
                }
                a++;
            }
        } else if (id == NotificationCenter.emojiDidLoad) {
            if (this.captionTextView != null) {
                this.captionTextView.invalidate();
            }
        } else if (id == NotificationCenter.needSetDayNightTheme) {
            if (this.nightModeEnabled && this.selectedColor != 2 && this.adapter != null) {
                updatePaintColors();
                for (i = 0; i < this.listView.length; i++) {
                    this.adapter[i].notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidStart) {
            MessageObject messageObject = args[0];
            if (this.listView != null) {
                for (i = 0; i < this.listView.length; i++) {
                    count = this.listView[i].getChildCount();
                    for (a = 0; a < count; a++) {
                        view = this.listView[i].getChildAt(a);
                        if (view instanceof BlockAudioCell) {
                            ((BlockAudioCell) view).updateButtonState(false);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
            if (this.listView != null) {
                for (i = 0; i < this.listView.length; i++) {
                    count = this.listView[i].getChildCount();
                    for (a = 0; a < count; a++) {
                        view = this.listView[i].getChildAt(a);
                        if (view instanceof BlockAudioCell) {
                            cell = (BlockAudioCell) view;
                            if (cell.getMessageObject() != null) {
                                cell.updateButtonState(false);
                            }
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer mid = args[0];
            if (this.listView != null) {
                for (i = 0; i < this.listView.length; i++) {
                    count = this.listView[i].getChildCount();
                    for (a = 0; a < count; a++) {
                        view = this.listView[i].getChildAt(a);
                        if (view instanceof BlockAudioCell) {
                            cell = (BlockAudioCell) view;
                            MessageObject playing = cell.getMessageObject();
                            if (playing != null && playing.getId() == mid.intValue()) {
                                MessageObject player = MediaController.getInstance().getPlayingMessageObject();
                                if (player != null) {
                                    playing.audioProgress = player.audioProgress;
                                    playing.audioProgressSec = player.audioProgressSec;
                                    playing.audioPlayerDuration = player.audioPlayerDuration;
                                    cell.updatePlayingMessageProgress();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void updatePaintSize() {
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_size", this.selectedFontSize).commit();
        for (int i = 0; i < 2; i++) {
            this.adapter[i].notifyDataSetChanged();
        }
    }

    private void updatePaintFonts() {
        int a;
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_type", this.selectedFont).commit();
        Typeface typefaceNormal = this.selectedFont == 0 ? Typeface.DEFAULT : Typeface.SERIF;
        Typeface typefaceItalic = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/ritalic.ttf") : Typeface.create("serif", 2);
        Typeface typefaceBold = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmedium.ttf") : Typeface.create("serif", 1);
        Typeface typefaceBoldItalic = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf") : Typeface.create("serif", 3);
        for (a = 0; a < quoteTextPaints.size(); a++) {
            updateFontEntry(quoteTextPaints.keyAt(a), (TextPaint) quoteTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < preformattedTextPaints.size(); a++) {
            updateFontEntry(preformattedTextPaints.keyAt(a), (TextPaint) preformattedTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < paragraphTextPaints.size(); a++) {
            updateFontEntry(paragraphTextPaints.keyAt(a), (TextPaint) paragraphTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < listTextPaints.size(); a++) {
            updateFontEntry(listTextPaints.keyAt(a), (TextPaint) listTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < embedPostTextPaints.size(); a++) {
            updateFontEntry(embedPostTextPaints.keyAt(a), (TextPaint) embedPostTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < mediaCaptionTextPaints.size(); a++) {
            updateFontEntry(mediaCaptionTextPaints.keyAt(a), (TextPaint) mediaCaptionTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < mediaCreditTextPaints.size(); a++) {
            updateFontEntry(mediaCreditTextPaints.keyAt(a), (TextPaint) mediaCreditTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < photoCaptionTextPaints.size(); a++) {
            updateFontEntry(photoCaptionTextPaints.keyAt(a), (TextPaint) photoCaptionTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < photoCreditTextPaints.size(); a++) {
            updateFontEntry(photoCreditTextPaints.keyAt(a), (TextPaint) photoCreditTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < authorTextPaints.size(); a++) {
            updateFontEntry(authorTextPaints.keyAt(a), (TextPaint) authorTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < footerTextPaints.size(); a++) {
            updateFontEntry(footerTextPaints.keyAt(a), (TextPaint) footerTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < embedPostCaptionTextPaints.size(); a++) {
            updateFontEntry(embedPostCaptionTextPaints.keyAt(a), (TextPaint) embedPostCaptionTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < relatedArticleTextPaints.size(); a++) {
            updateFontEntry(relatedArticleTextPaints.keyAt(a), (TextPaint) relatedArticleTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < detailsTextPaints.size(); a++) {
            updateFontEntry(detailsTextPaints.keyAt(a), (TextPaint) detailsTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (a = 0; a < tableTextPaints.size(); a++) {
            updateFontEntry(tableTextPaints.keyAt(a), (TextPaint) tableTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
    }

    private void updateFontEntry(int flags, TextPaint paint, Typeface typefaceNormal, Typeface typefaceBoldItalic, Typeface typefaceBold, Typeface typefaceItalic) {
        if ((flags & 1) != 0 && (flags & 2) != 0) {
            paint.setTypeface(typefaceBoldItalic);
        } else if ((flags & 1) != 0) {
            paint.setTypeface(typefaceBold);
        } else if ((flags & 2) != 0) {
            paint.setTypeface(typefaceItalic);
        } else if ((flags & 4) == 0) {
            paint.setTypeface(typefaceNormal);
        }
    }

    private int getSelectedColor() {
        int currentColor = this.selectedColor;
        if (!this.nightModeEnabled || currentColor == 2) {
            return currentColor;
        }
        if (Theme.selectedAutoNightType == 0) {
            int hour = Calendar.getInstance().get(11);
            if ((hour < 22 || hour > 24) && (hour < 0 || hour > 6)) {
                return currentColor;
            }
            return 2;
        } else if (Theme.isCurrentThemeNight()) {
            return 2;
        } else {
            return currentColor;
        }
    }

    private void updatePaintColors() {
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_color", this.selectedColor).commit();
        int currentColor = getSelectedColor();
        if (currentColor == 0) {
            this.backgroundPaint.setColor(-1);
            for (RecyclerListView glowColor : this.listView) {
                glowColor.setGlowColor(-657673);
            }
        } else if (currentColor == 1) {
            this.backgroundPaint.setColor(-659492);
            for (RecyclerListView glowColor2 : this.listView) {
                glowColor2.setGlowColor(-659492);
            }
        } else if (currentColor == 2) {
            this.backgroundPaint.setColor(-15461356);
            for (RecyclerListView glowColor22 : this.listView) {
                glowColor22.setGlowColor(-15461356);
            }
        }
        for (int a = 0; a < Theme.chat_ivStatesDrawable.length; a++) {
            Theme.setCombinedDrawableColor(Theme.chat_ivStatesDrawable[a][0], getTextColor(), false);
            Theme.setCombinedDrawableColor(Theme.chat_ivStatesDrawable[a][0], getTextColor(), true);
            Theme.setCombinedDrawableColor(Theme.chat_ivStatesDrawable[a][1], getTextColor(), false);
            Theme.setCombinedDrawableColor(Theme.chat_ivStatesDrawable[a][1], getTextColor(), true);
        }
        if (listTextPointerPaint != null) {
            listTextPointerPaint.setColor(getTextColor());
        }
        if (listTextNumPaint != null) {
            listTextNumPaint.setColor(getTextColor());
        }
        if (embedPostAuthorPaint != null) {
            embedPostAuthorPaint.setColor(getTextColor());
        }
        if (channelNamePaint != null) {
            if (this.channelBlock == null) {
                channelNamePaint.setColor(getTextColor());
            } else {
                channelNamePaint.setColor(-1);
            }
        }
        if (relatedArticleHeaderPaint != null) {
            relatedArticleHeaderPaint.setColor(getTextColor());
        }
        if (relatedArticleTextPaint != null) {
            relatedArticleTextPaint.setColor(getGrayTextColor());
        }
        if (embedPostDatePaint != null) {
            if (currentColor == 0) {
                embedPostDatePaint.setColor(-7366752);
            } else {
                embedPostDatePaint.setColor(getGrayTextColor());
            }
        }
        createPaint(true);
        setMapColors(titleTextPaints);
        setMapColors(kickerTextPaints);
        setMapColors(subtitleTextPaints);
        setMapColors(headerTextPaints);
        setMapColors(subheaderTextPaints);
        setMapColors(quoteTextPaints);
        setMapColors(preformattedTextPaints);
        setMapColors(paragraphTextPaints);
        setMapColors(listTextPaints);
        setMapColors(embedPostTextPaints);
        setMapColors(mediaCaptionTextPaints);
        setMapColors(mediaCreditTextPaints);
        setMapColors(photoCaptionTextPaints);
        setMapColors(photoCreditTextPaints);
        setMapColors(authorTextPaints);
        setMapColors(footerTextPaints);
        setMapColors(embedPostCaptionTextPaints);
        setMapColors(relatedArticleTextPaints);
        setMapColors(detailsTextPaints);
        setMapColors(tableTextPaints);
    }

    private void setMapColors(SparseArray<TextPaint> map) {
        for (int a = 0; a < map.size(); a++) {
            int flags = map.keyAt(a);
            TextPaint paint = (TextPaint) map.valueAt(a);
            if ((flags & 8) == 0 && (flags & 512) == 0) {
                paint.setColor(getTextColor());
            } else {
                paint.setColor(getLinkTextColor());
            }
        }
    }

    public void setParentActivity(Activity activity, BaseFragment fragment) {
        this.parentFragment = fragment;
        this.currentAccount = UserConfig.selectedAccount;
        this.leftImage.setCurrentAccount(this.currentAccount);
        this.rightImage.setCurrentAccount(this.currentAccount);
        this.centerImage.setCurrentAccount(this.currentAccount);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme);
        if (this.parentActivity == activity) {
            updatePaintColors();
            return;
        }
        this.parentActivity = activity;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0);
        this.selectedFontSize = sharedPreferences.getInt("font_size", 2);
        this.selectedFont = sharedPreferences.getInt("font_type", 0);
        this.selectedColor = sharedPreferences.getInt("font_color", 0);
        this.nightModeEnabled = sharedPreferences.getBoolean("nightModeEnabled", false);
        createPaint(false);
        this.backgroundPaint = new Paint();
        this.layerShadowDrawable = activity.getResources().getDrawable(R.drawable.layer_shadow);
        this.slideDotDrawable = activity.getResources().getDrawable(R.drawable.slide_dot_small);
        this.slideDotBigDrawable = activity.getResources().getDrawable(R.drawable.slide_dot_big);
        this.scrimPaint = new Paint();
        this.windowView = new WindowView(activity);
        this.windowView.setWillNotDraw(false);
        this.windowView.setClipChildren(true);
        this.windowView.setFocusable(false);
        this.containerView = new FrameLayout(activity) {
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (!ArticleViewer.this.windowView.movingPage) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                int width = getMeasuredWidth();
                int translationX = (int) ArticleViewer.this.listView[0].getTranslationX();
                int clipLeft = 0;
                int clipRight = width;
                if (child == ArticleViewer.this.listView[1]) {
                    clipRight = translationX;
                } else if (child == ArticleViewer.this.listView[0]) {
                    clipLeft = translationX;
                }
                int restoreCount = canvas.save();
                canvas.clipRect(clipLeft, 0, clipRight, getHeight());
                boolean result = super.drawChild(canvas, child, drawingTime);
                canvas.restoreToCount(restoreCount);
                if (translationX == 0) {
                    return result;
                }
                if (child == ArticleViewer.this.listView[0]) {
                    float alpha = Math.max(0.0f, Math.min(((float) (width - translationX)) / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                    ArticleViewer.this.layerShadowDrawable.setBounds(translationX - ArticleViewer.this.layerShadowDrawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                    ArticleViewer.this.layerShadowDrawable.setAlpha((int) (255.0f * alpha));
                    ArticleViewer.this.layerShadowDrawable.draw(canvas);
                    return result;
                } else if (child != ArticleViewer.this.listView[1]) {
                    return result;
                } else {
                    float opacity = Math.min(0.8f, ((float) (width - translationX)) / ((float) width));
                    if (opacity < 0.0f) {
                        opacity = 0.0f;
                    }
                    ArticleViewer.this.scrimPaint.setColor(((int) (153.0f * opacity)) << 24);
                    canvas.drawRect((float) clipLeft, 0.0f, (float) clipRight, (float) getHeight(), ArticleViewer.this.scrimPaint);
                    return result;
                }
            }
        };
        this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
        this.containerView.setFitsSystemWindows(true);
        if (VERSION.SDK_INT >= 21) {
            this.containerView.setOnApplyWindowInsetsListener(new ArticleViewer$$Lambda$6(this));
        }
        this.containerView.setSystemUiVisibility(1028);
        this.photoContainerBackground = new View(activity);
        this.photoContainerBackground.setVisibility(4);
        this.photoContainerBackground.setBackgroundDrawable(this.photoBackgroundDrawable);
        this.windowView.addView(this.photoContainerBackground, LayoutHelper.createFrame(-1, -1, 51));
        this.animatingImageView = new ClippingImageView(activity);
        this.animatingImageView.setAnimationValues(this.animationValues);
        this.animatingImageView.setVisibility(8);
        this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
        this.photoContainerView = new FrameLayoutDrawer(activity) {
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int y = (bottom - top) - ArticleViewer.this.captionTextView.getMeasuredHeight();
                int y2 = (bottom - top) - ArticleViewer.this.groupedPhotosListView.getMeasuredHeight();
                if (ArticleViewer.this.bottomLayout.getVisibility() == 0) {
                    y -= ArticleViewer.this.bottomLayout.getMeasuredHeight();
                    y2 -= ArticleViewer.this.bottomLayout.getMeasuredHeight();
                }
                if (!ArticleViewer.this.groupedPhotosListView.currentPhotos.isEmpty()) {
                    y -= ArticleViewer.this.groupedPhotosListView.getMeasuredHeight();
                }
                ArticleViewer.this.captionTextView.layout(0, y, ArticleViewer.this.captionTextView.getMeasuredWidth(), ArticleViewer.this.captionTextView.getMeasuredHeight() + y);
                ArticleViewer.this.captionTextViewNext.layout(0, y, ArticleViewer.this.captionTextViewNext.getMeasuredWidth(), ArticleViewer.this.captionTextViewNext.getMeasuredHeight() + y);
                ArticleViewer.this.groupedPhotosListView.layout(0, y2, ArticleViewer.this.groupedPhotosListView.getMeasuredWidth(), ArticleViewer.this.groupedPhotosListView.getMeasuredHeight() + y2);
            }
        };
        this.photoContainerView.setVisibility(4);
        this.photoContainerView.setWillNotDraw(false);
        this.windowView.addView(this.photoContainerView, LayoutHelper.createFrame(-1, -1, 51));
        this.fullscreenVideoContainer = new FrameLayout(activity);
        this.fullscreenVideoContainer.setBackgroundColor(-16777216);
        this.fullscreenVideoContainer.setVisibility(4);
        this.windowView.addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.fullscreenAspectRatioView = new AspectRatioFrameLayout(activity);
        this.fullscreenAspectRatioView.setVisibility(8);
        this.fullscreenVideoContainer.addView(this.fullscreenAspectRatioView, LayoutHelper.createFrame(-1, -1, 17));
        this.fullscreenTextureView = new TextureView(activity);
        this.listView = new RecyclerListView[2];
        this.adapter = new WebpageAdapter[2];
        this.layoutManager = new LinearLayoutManager[2];
        int i = 0;
        while (i < this.listView.length) {
            this.listView[i] = new RecyclerListView(activity) {
                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    super.onLayout(changed, l, t, r, b);
                    int count = getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = getChildAt(a);
                        if ((child.getTag() instanceof Integer) && ((Integer) child.getTag()).intValue() == 90 && child.getBottom() < getMeasuredHeight()) {
                            int height = getMeasuredHeight();
                            child.layout(0, height - child.getMeasuredHeight(), child.getMeasuredWidth(), height);
                            return;
                        }
                    }
                }

                public boolean onInterceptTouchEvent(MotionEvent e) {
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLink == null && ((ArticleViewer.this.popupWindow == null || !ArticleViewer.this.popupWindow.isShowing()) && (e.getAction() == 1 || e.getAction() == 3))) {
                        ArticleViewer.this.pressedLink = null;
                        ArticleViewer.this.pressedLinkOwnerLayout = null;
                        ArticleViewer.this.pressedLinkOwnerView = null;
                    } else if (!(ArticleViewer.this.pressedLinkOwnerLayout == null || ArticleViewer.this.pressedLink == null || e.getAction() != 1)) {
                        ArticleViewer.this.checkLayoutForLinks(e, ArticleViewer.this.pressedLinkOwnerView, ArticleViewer.this.pressedLinkOwnerLayout, 0, 0);
                    }
                    return super.onInterceptTouchEvent(e);
                }

                public boolean onTouchEvent(MotionEvent e) {
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLink == null && ((ArticleViewer.this.popupWindow == null || !ArticleViewer.this.popupWindow.isShowing()) && (e.getAction() == 1 || e.getAction() == 3))) {
                        ArticleViewer.this.pressedLink = null;
                        ArticleViewer.this.pressedLinkOwnerLayout = null;
                        ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                    return super.onTouchEvent(e);
                }

                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    if (ArticleViewer.this.windowView.movingPage) {
                        ArticleViewer.this.containerView.invalidate();
                        ArticleViewer.this.setCurrentHeaderHeight((int) (((float) ArticleViewer.this.windowView.startMovingHeaderHeight) + (((float) (AndroidUtilities.dp(56.0f) - ArticleViewer.this.windowView.startMovingHeaderHeight)) * (translationX / ((float) getMeasuredWidth())))));
                    }
                }
            };
            ((DefaultItemAnimator) this.listView[i].getItemAnimator()).setDelayAnimations(false);
            RecyclerListView recyclerListView = this.listView[i];
            LinearLayoutManager[] linearLayoutManagerArr = this.layoutManager;
            LayoutManager linearLayoutManager = new LinearLayoutManager(this.parentActivity, 1, false);
            linearLayoutManagerArr[i] = linearLayoutManager;
            recyclerListView.setLayoutManager(linearLayoutManager);
            WebpageAdapter[] webpageAdapterArr = this.adapter;
            Adapter webpageAdapter = new WebpageAdapter(this.parentActivity);
            webpageAdapterArr[i] = webpageAdapter;
            this.listView[i].setAdapter(webpageAdapter);
            this.listView[i].setClipToPadding(false);
            this.listView[i].setVisibility(i == 0 ? 0 : 8);
            this.listView[i].setPadding(0, AndroidUtilities.dp(56.0f), 0, 0);
            this.listView[i].setTopGlowOffset(AndroidUtilities.dp(56.0f));
            this.containerView.addView(this.listView[i], LayoutHelper.createFrame(-1, -1.0f));
            this.listView[i].setOnItemLongClickListener(new ArticleViewer$$Lambda$7(this));
            this.listView[i].setOnItemClickListener(new ArticleViewer$$Lambda$8(this, webpageAdapter));
            this.listView[i].setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (recyclerView.getChildCount() != 0) {
                        ArticleViewer.this.headerView.invalidate();
                        ArticleViewer.this.checkScroll(dy);
                    }
                }
            });
            i++;
        }
        this.headerPaint.setColor(-16777216);
        this.headerProgressPaint.setColor(-14408666);
        this.headerView = new FrameLayout(activity) {
            protected void onDraw(Canvas canvas) {
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                canvas.drawRect(0.0f, 0.0f, (float) width, (float) height, ArticleViewer.this.headerPaint);
                if (ArticleViewer.this.layoutManager != null) {
                    View view;
                    int first = ArticleViewer.this.layoutManager[0].findFirstVisibleItemPosition();
                    int last = ArticleViewer.this.layoutManager[0].findLastVisibleItemPosition();
                    int count = ArticleViewer.this.layoutManager[0].getItemCount();
                    if (last >= count - 2) {
                        view = ArticleViewer.this.layoutManager[0].findViewByPosition(count - 2);
                    } else {
                        view = ArticleViewer.this.layoutManager[0].findViewByPosition(first);
                    }
                    if (view != null) {
                        float viewProgress;
                        float itemProgress = ((float) width) / ((float) (count - 1));
                        int childCount = ArticleViewer.this.layoutManager[0].getChildCount();
                        float viewHeight = (float) view.getMeasuredHeight();
                        if (last >= count - 2) {
                            viewProgress = ((((float) ((count - 2) - first)) * itemProgress) * ((float) (ArticleViewer.this.listView[0].getMeasuredHeight() - view.getTop()))) / viewHeight;
                        } else {
                            viewProgress = itemProgress * (1.0f - ((((float) Math.min(0, view.getTop() - ArticleViewer.this.listView[0].getPaddingTop())) + viewHeight) / viewHeight));
                        }
                        canvas.drawRect(0.0f, 0.0f, (((float) first) * itemProgress) + viewProgress, (float) height, ArticleViewer.this.headerProgressPaint);
                    }
                }
            }
        };
        this.headerView.setOnTouchListener(ArticleViewer$$Lambda$9.$instance);
        this.headerView.setWillNotDraw(false);
        this.containerView.addView(this.headerView, LayoutHelper.createFrame(-1, 56.0f));
        this.backButton = new ImageView(activity);
        this.backButton.setScaleType(ScaleType.CENTER);
        this.backDrawable = new BackDrawable(false);
        this.backDrawable.setAnimationTime(200.0f);
        this.backDrawable.setColor(-5000269);
        this.backDrawable.setRotated(false);
        this.backButton.setImageDrawable(this.backDrawable);
        this.backButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.headerView.addView(this.backButton, LayoutHelper.createFrame(54, 56.0f));
        this.backButton.setOnClickListener(new ArticleViewer$$Lambda$10(this));
        this.titleTextView = new SimpleTextView(activity);
        this.titleTextView.setGravity(19);
        this.titleTextView.setTextSize(20);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setTextColor(-5000269);
        this.titleTextView.setPivotX(0.0f);
        this.titleTextView.setPivotY((float) AndroidUtilities.dp(28.0f));
        this.headerView.addView(this.titleTextView, LayoutHelper.createFrame(-1, 56.0f, 51, 72.0f, 0.0f, 96.0f, 0.0f));
        this.lineProgressView = new LineProgressView(activity);
        this.lineProgressView.setProgressColor(-1);
        this.lineProgressView.setPivotX(0.0f);
        this.lineProgressView.setPivotY((float) AndroidUtilities.dp(2.0f));
        this.headerView.addView(this.lineProgressView, LayoutHelper.createFrame(-1, 2.0f, 83, 0.0f, 0.0f, 0.0f, 1.0f));
        this.lineProgressTickRunnable = new ArticleViewer$$Lambda$11(this);
        LinearLayout settingsContainer = new LinearLayout(this.parentActivity);
        settingsContainer.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        settingsContainer.setOrientation(1);
        int a = 0;
        while (a < 3) {
            this.colorCells[a] = new ColorCell(this, this.parentActivity);
            switch (a) {
                case 0:
                    this.nightModeImageView = new ImageView(this.parentActivity);
                    this.nightModeImageView.setScaleType(ScaleType.CENTER);
                    this.nightModeImageView.setImageResource(R.drawable.moon);
                    ImageView imageView = this.nightModeImageView;
                    int i2 = (!this.nightModeEnabled || this.selectedColor == 2) ? -3355444 : -15428119;
                    imageView.setColorFilter(new PorterDuffColorFilter(i2, Mode.MULTIPLY));
                    this.nightModeImageView.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
                    this.colorCells[a].addView(this.nightModeImageView, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : 5) | 48));
                    this.nightModeImageView.setOnClickListener(new ArticleViewer$$Lambda$12(this));
                    this.colorCells[a].setTextAndColor(LocaleController.getString("ColorWhite", R.string.ColorWhite), -1);
                    break;
                case 1:
                    this.colorCells[a].setTextAndColor(LocaleController.getString("ColorSepia", R.string.ColorSepia), -1382967);
                    break;
                case 2:
                    this.colorCells[a].setTextAndColor(LocaleController.getString("ColorDark", R.string.ColorDark), -14474461);
                    break;
            }
            this.colorCells[a].select(a == this.selectedColor);
            this.colorCells[a].setTag(Integer.valueOf(a));
            this.colorCells[a].setOnClickListener(new ArticleViewer$$Lambda$13(this));
            settingsContainer.addView(this.colorCells[a], LayoutHelper.createLinear(-1, 50));
            a++;
        }
        updateNightModeButton();
        View divider = new View(this.parentActivity);
        divider.setBackgroundColor(-2039584);
        settingsContainer.addView(divider, LayoutHelper.createLinear(-1, 1, 15.0f, 4.0f, 15.0f, 4.0f));
        divider.getLayoutParams().height = 1;
        a = 0;
        while (a < 2) {
            this.fontCells[a] = new FontCell(this, this.parentActivity);
            switch (a) {
                case 0:
                    this.fontCells[a].setTextAndTypeface("Roboto", Typeface.DEFAULT);
                    break;
                case 1:
                    this.fontCells[a].setTextAndTypeface("Serif", Typeface.SERIF);
                    break;
            }
            this.fontCells[a].select(a == this.selectedFont);
            this.fontCells[a].setTag(Integer.valueOf(a));
            this.fontCells[a].setOnClickListener(new ArticleViewer$$Lambda$14(this));
            settingsContainer.addView(this.fontCells[a], LayoutHelper.createLinear(-1, 50));
            a++;
        }
        divider = new View(this.parentActivity);
        divider.setBackgroundColor(-2039584);
        settingsContainer.addView(divider, LayoutHelper.createLinear(-1, 1, 15.0f, 4.0f, 15.0f, 4.0f));
        divider.getLayoutParams().height = 1;
        View textView = new TextView(this.parentActivity);
        textView.setTextColor(-14606047);
        textView.setTextSize(1, 16.0f);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        textView.setText(LocaleController.getString("FontSize", R.string.FontSize));
        settingsContainer.addView(textView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 17, 12, 17, 0));
        settingsContainer.addView(new SizeChooseView(this.parentActivity), LayoutHelper.createLinear(-1, 38, 0.0f, 0.0f, 0.0f, 1.0f));
        this.settingsButton = new ActionBarMenuItem(this.parentActivity, null, NUM, -1);
        this.settingsButton.setPopupAnimationEnabled(false);
        this.settingsButton.setLayoutInScreen(true);
        textView = new TextView(this.parentActivity);
        textView.setTextSize(1, 18.0f);
        textView.setText("Aa");
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextColor(-5000269);
        textView.setGravity(17);
        this.settingsButton.addView(textView, LayoutHelper.createFrame(-1, -1.0f));
        this.settingsButton.addSubItem(settingsContainer, AndroidUtilities.dp(220.0f), -2);
        this.settingsButton.redrawPopup(-1);
        this.headerView.addView(this.settingsButton, LayoutHelper.createFrame(48, 56.0f, 53, 0.0f, 0.0f, 56.0f, 0.0f));
        this.shareContainer = new FrameLayout(activity);
        this.headerView.addView(this.shareContainer, LayoutHelper.createFrame(48, 56, 53));
        this.shareContainer.setOnClickListener(new ArticleViewer$$Lambda$15(this));
        this.shareButton = new ImageView(activity);
        this.shareButton.setScaleType(ScaleType.CENTER);
        this.shareButton.setImageResource(R.drawable.ic_share_article);
        this.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.shareContainer.addView(this.shareButton, LayoutHelper.createFrame(48, 56.0f));
        this.progressView = new ContextProgressView(activity, 2);
        this.progressView.setVisibility(8);
        this.shareContainer.addView(this.progressView, LayoutHelper.createFrame(48, 56.0f));
        this.windowLayoutParams = new LayoutParams();
        this.windowLayoutParams.height = -1;
        this.windowLayoutParams.format = -3;
        this.windowLayoutParams.width = -1;
        this.windowLayoutParams.gravity = 51;
        this.windowLayoutParams.type = 99;
        if (VERSION.SDK_INT >= 21) {
            this.windowLayoutParams.flags = -NUM;
        } else {
            this.windowLayoutParams.flags = 8;
        }
        if (progressDrawables == null) {
            progressDrawables = new Drawable[4];
            progressDrawables[0] = this.parentActivity.getResources().getDrawable(R.drawable.circle_big);
            progressDrawables[1] = this.parentActivity.getResources().getDrawable(R.drawable.cancel_big);
            progressDrawables[2] = this.parentActivity.getResources().getDrawable(R.drawable.load_big);
            progressDrawables[3] = this.parentActivity.getResources().getDrawable(R.drawable.play_big);
        }
        this.scroller = new Scroller(activity);
        this.blackPaint.setColor(-16777216);
        this.actionBar = new ActionBar(activity);
        this.actionBar.setBackgroundColor(NUM);
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsBackgroundColor(NUM, false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(1), Integer.valueOf(1)));
        this.photoContainerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                int i = 1;
                if (id == -1) {
                    ArticleViewer.this.closePhoto(true);
                } else if (id == 1) {
                    if (VERSION.SDK_INT < 23 || ArticleViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        File f = ArticleViewer.this.getMediaFile(ArticleViewer.this.currentIndex);
                        if (f == null || !f.exists()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ArticleViewer.this.parentActivity);
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                            builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
                            ArticleViewer.this.showDialog(builder.create());
                            return;
                        }
                        String file = f.toString();
                        Context access$2500 = ArticleViewer.this.parentActivity;
                        if (!ArticleViewer.this.isMediaVideo(ArticleViewer.this.currentIndex)) {
                            i = 0;
                        }
                        MediaController.saveFile(file, access$2500, i, null, null);
                        return;
                    }
                    ArticleViewer.this.parentActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                } else if (id == 2) {
                    ArticleViewer.this.onSharePressed();
                } else if (id == 3) {
                    try {
                        AndroidUtilities.openForView(ArticleViewer.this.getMedia(ArticleViewer.this.currentIndex), ArticleViewer.this.parentActivity);
                        ArticleViewer.this.closePhoto(false);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            }

            public boolean canOpenMenu() {
                File f = ArticleViewer.this.getMediaFile(ArticleViewer.this.currentIndex);
                return f != null && f.exists();
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.addItem(2, (int) R.drawable.share);
        this.menuItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
        this.menuItem.setLayoutInScreen(true);
        this.menuItem.addSubItem(3, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp));
        this.menuItem.addSubItem(1, LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
        this.bottomLayout = new FrameLayout(this.parentActivity);
        this.bottomLayout.setBackgroundColor(NUM);
        this.photoContainerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        this.groupedPhotosListView = new GroupedPhotosListView(this.parentActivity);
        this.photoContainerView.addView(this.groupedPhotosListView, LayoutHelper.createFrame(-1, 62.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
        this.groupedPhotosListView.setDelegate(new GroupedPhotosListViewDelegate() {
            public int getCurrentIndex() {
                return ArticleViewer.this.currentIndex;
            }

            public int getCurrentAccount() {
                return ArticleViewer.this.currentAccount;
            }

            public int getAvatarsDialogId() {
                return 0;
            }

            public int getSlideshowMessageId() {
                return 0;
            }

            public ArrayList<FileLocation> getImagesArrLocations() {
                return null;
            }

            public ArrayList<MessageObject> getImagesArr() {
                return null;
            }

            public ArrayList<PageBlock> getPageBlockArr() {
                return ArticleViewer.this.imagesArr;
            }

            public Object getParentObject() {
                return ArticleViewer.this.currentPage;
            }

            public void setCurrentIndex(int index) {
                ArticleViewer.this.currentIndex = -1;
                if (ArticleViewer.this.currentThumb != null) {
                    ArticleViewer.this.currentThumb.release();
                    ArticleViewer.this.currentThumb = null;
                }
                ArticleViewer.this.setImageIndex(index, true);
            }
        });
        this.captionTextViewNext = new TextView(activity);
        this.captionTextViewNext.setMaxLines(10);
        this.captionTextViewNext.setBackgroundColor(NUM);
        this.captionTextViewNext.setMovementMethod(new LinkMovementMethodMy());
        this.captionTextViewNext.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
        this.captionTextViewNext.setLinkTextColor(-1);
        this.captionTextViewNext.setTextColor(-1);
        this.captionTextViewNext.setHighlightColor(NUM);
        this.captionTextViewNext.setGravity(19);
        this.captionTextViewNext.setTextSize(1, 16.0f);
        this.captionTextViewNext.setVisibility(8);
        this.photoContainerView.addView(this.captionTextViewNext, LayoutHelper.createFrame(-1, -2, 83));
        this.captionTextView = new TextView(activity);
        this.captionTextView.setMaxLines(10);
        this.captionTextView.setBackgroundColor(NUM);
        this.captionTextView.setMovementMethod(new LinkMovementMethodMy());
        this.captionTextView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
        this.captionTextView.setLinkTextColor(-1);
        this.captionTextView.setTextColor(-1);
        this.captionTextView.setHighlightColor(NUM);
        this.captionTextView.setGravity(19);
        this.captionTextView.setTextSize(1, 16.0f);
        this.captionTextView.setVisibility(8);
        this.photoContainerView.addView(this.captionTextView, LayoutHelper.createFrame(-1, -2, 83));
        this.radialProgressViews[0] = new RadialProgressView(activity, this.photoContainerView);
        this.radialProgressViews[0].setBackgroundState(0, false);
        this.radialProgressViews[1] = new RadialProgressView(activity, this.photoContainerView);
        this.radialProgressViews[1].setBackgroundState(0, false);
        this.radialProgressViews[2] = new RadialProgressView(activity, this.photoContainerView);
        this.radialProgressViews[2].setBackgroundState(0, false);
        this.videoPlayerSeekbar = new SeekBar(activity);
        this.videoPlayerSeekbar.setColors(NUM, NUM, -2764585, -1, -1);
        this.videoPlayerSeekbar.setDelegate(new ArticleViewer$$Lambda$16(this));
        this.videoPlayerControlFrameLayout = new FrameLayout(activity) {
            public boolean onTouchEvent(MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (!ArticleViewer.this.videoPlayerSeekbar.onTouch(event.getAction(), event.getX() - ((float) AndroidUtilities.dp(48.0f)), event.getY())) {
                    return super.onTouchEvent(event);
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                long duration;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (ArticleViewer.this.videoPlayer != null) {
                    duration = ArticleViewer.this.videoPlayer.getDuration();
                    if (duration == -9223372036854775807L) {
                        duration = 0;
                    }
                } else {
                    duration = 0;
                }
                duration /= 1000;
                ArticleViewer.this.videoPlayerSeekbar.setSize((getMeasuredWidth() - AndroidUtilities.dp(64.0f)) - ((int) Math.ceil((double) ArticleViewer.this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(duration / 60), Long.valueOf(duration % 60), Long.valueOf(duration / 60), Long.valueOf(duration % 60)})))), getMeasuredHeight());
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                float progress = 0.0f;
                if (ArticleViewer.this.videoPlayer != null) {
                    progress = ((float) ArticleViewer.this.videoPlayer.getCurrentPosition()) / ((float) ArticleViewer.this.videoPlayer.getDuration());
                }
                ArticleViewer.this.videoPlayerSeekbar.setProgress(progress);
            }

            protected void onDraw(Canvas canvas) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(48.0f), 0.0f);
                ArticleViewer.this.videoPlayerSeekbar.draw(canvas);
                canvas.restore();
            }
        };
        this.videoPlayerControlFrameLayout.setWillNotDraw(false);
        this.bottomLayout.addView(this.videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
        this.videoPlayButton = new ImageView(activity);
        this.videoPlayButton.setScaleType(ScaleType.CENTER);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48, 48, 51));
        this.videoPlayButton.setOnClickListener(new ArticleViewer$$Lambda$17(this));
        this.videoPlayerTime = new TextView(activity);
        this.videoPlayerTime.setTextColor(-1);
        this.videoPlayerTime.setGravity(16);
        this.videoPlayerTime.setTextSize(1, 13.0f);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -1.0f, 53, 0.0f, 0.0f, 8.0f, 0.0f));
        this.gestureDetector = new GestureDetector(activity, this);
        this.gestureDetector.setOnDoubleTapListener(this);
        this.centerImage.setParentView(this.photoContainerView);
        this.centerImage.setCrossfadeAlpha((byte) 2);
        this.centerImage.setInvalidateAll(true);
        this.leftImage.setParentView(this.photoContainerView);
        this.leftImage.setCrossfadeAlpha((byte) 2);
        this.leftImage.setInvalidateAll(true);
        this.rightImage.setParentView(this.photoContainerView);
        this.rightImage.setCrossfadeAlpha((byte) 2);
        this.rightImage.setInvalidateAll(true);
        updatePaintColors();
    }

    final /* synthetic */ WindowInsets lambda$setParentActivity$7$ArticleViewer(View v, WindowInsets insets) {
        WindowInsets oldInsets = this.lastInsets;
        this.lastInsets = insets;
        if (oldInsets == null || !oldInsets.toString().equals(insets.toString())) {
            this.windowView.requestLayout();
        }
        if (VERSION.SDK_INT >= 28) {
            DisplayCutout cutout = insets.getDisplayCutout();
            if (cutout != null) {
                List<Rect> rects = cutout.getBoundingRects();
                if (!(rects == null || rects.isEmpty())) {
                    this.settingsButton.setMenuYOffset(Math.abs(((Rect) rects.get(0)).height()));
                }
            }
        }
        return insets.consumeSystemWindowInsets();
    }

    final /* synthetic */ boolean lambda$setParentActivity$8$ArticleViewer(View view, int position) {
        if (!(view instanceof BlockRelatedArticlesCell)) {
            return false;
        }
        BlockRelatedArticlesCell cell = (BlockRelatedArticlesCell) view;
        showCopyPopup(((TL_pageRelatedArticle) cell.currentBlock.parent.articles.get(cell.currentBlock.num)).url);
        return true;
    }

    final /* synthetic */ void lambda$setParentActivity$11$ArticleViewer(WebpageAdapter webpageAdapter, View view, int position) {
        if (position != webpageAdapter.localBlocks.size() || this.currentPage == null) {
            if (position >= 0 && position < webpageAdapter.localBlocks.size()) {
                PageBlock pageBlock = (PageBlock) webpageAdapter.localBlocks.get(position);
                PageBlock originalBlock = pageBlock;
                pageBlock = getLastNonListPageBlock(pageBlock);
                if (pageBlock instanceof TL_pageBlockDetailsChild) {
                    pageBlock = ((TL_pageBlockDetailsChild) pageBlock).block;
                }
                if (pageBlock instanceof TL_pageBlockChannel) {
                    MessagesController.getInstance(this.currentAccount).openByUserName(((TL_pageBlockChannel) pageBlock).channel.username, this.parentFragment, 2);
                    close(false, true);
                } else if (pageBlock instanceof TL_pageBlockRelatedArticlesChild) {
                    TL_pageBlockRelatedArticlesChild pageBlockRelatedArticlesChild = (TL_pageBlockRelatedArticlesChild) pageBlock;
                    openWebpageUrl(((TL_pageRelatedArticle) pageBlockRelatedArticlesChild.parent.articles.get(pageBlockRelatedArticlesChild.num)).url, null);
                } else if (pageBlock instanceof TL_pageBlockDetails) {
                    view = getLastNonListCell(view);
                    if (view instanceof BlockDetailsCell) {
                        this.pressedLinkOwnerLayout = null;
                        this.pressedLinkOwnerView = null;
                        if (webpageAdapter.blocks.indexOf(originalBlock) >= 0) {
                            TL_pageBlockDetails pageBlockDetails = (TL_pageBlockDetails) pageBlock;
                            pageBlockDetails.open = !pageBlockDetails.open;
                            int oldCount = webpageAdapter.getItemCount();
                            webpageAdapter.updateRows();
                            int changeCount = Math.abs(webpageAdapter.getItemCount() - oldCount);
                            BlockDetailsCell cell = (BlockDetailsCell) view;
                            cell.arrow.setAnimationProgressAnimated(pageBlockDetails.open ? 0.0f : 1.0f);
                            cell.invalidate();
                            if (changeCount == 0) {
                                return;
                            }
                            if (pageBlockDetails.open) {
                                webpageAdapter.notifyItemRangeInserted(position + 1, changeCount);
                            } else {
                                webpageAdapter.notifyItemRangeRemoved(position + 1, changeCount);
                            }
                        }
                    }
                }
            }
        } else if (this.previewsReqId == 0) {
            TLObject object = MessagesController.getInstance(this.currentAccount).getUserOrChat("previews");
            if (object instanceof TL_user) {
                openPreviewsChat((User) object, this.currentPage.id);
                return;
            }
            int currentAccount = UserConfig.selectedAccount;
            long pageId = this.currentPage.id;
            showProgressView(true, true);
            TLObject req = new TL_contacts_resolveUsername();
            req.username = "previews";
            this.previewsReqId = ConnectionsManager.getInstance(currentAccount).sendRequest(req, new ArticleViewer$$Lambda$41(this, currentAccount, pageId));
        }
    }

    final /* synthetic */ void lambda$null$10$ArticleViewer(int currentAccount, long pageId, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$42(this, response, currentAccount, pageId));
    }

    final /* synthetic */ void lambda$null$9$ArticleViewer(TLObject response, int currentAccount, long pageId) {
        if (this.previewsReqId != 0) {
            this.previewsReqId = 0;
            showProgressView(true, false);
            if (response != null) {
                TL_contacts_resolvedPeer res = (TL_contacts_resolvedPeer) response;
                MessagesController.getInstance(currentAccount).putUsers(res.users, false);
                MessagesStorage.getInstance(currentAccount).putUsersAndChats(res.users, res.chats, false, true);
                if (!res.users.isEmpty()) {
                    openPreviewsChat((User) res.users.get(0), pageId);
                }
            }
        }
    }

    final /* synthetic */ void lambda$setParentActivity$13$ArticleViewer(View v) {
        close(true, true);
    }

    final /* synthetic */ void lambda$setParentActivity$14$ArticleViewer() {
        float progressLeft = 0.7f - this.lineProgressView.getCurrentProgress();
        if (progressLeft > 0.0f) {
            float tick;
            if (progressLeft < 0.25f) {
                tick = 0.01f;
            } else {
                tick = 0.02f;
            }
            this.lineProgressView.setProgress(this.lineProgressView.getCurrentProgress() + tick, true);
            AndroidUtilities.runOnUIThread(this.lineProgressTickRunnable, 100);
        }
    }

    final /* synthetic */ void lambda$setParentActivity$15$ArticleViewer(View v) {
        boolean z;
        if (this.nightModeEnabled) {
            z = false;
        } else {
            z = true;
        }
        this.nightModeEnabled = z;
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putBoolean("nightModeEnabled", this.nightModeEnabled).commit();
        updateNightModeButton();
        updatePaintColors();
        for (int i = 0; i < this.listView.length; i++) {
            this.adapter[i].notifyDataSetChanged();
        }
        if (this.nightModeEnabled) {
            showNightModeHint();
        }
    }

    final /* synthetic */ void lambda$setParentActivity$16$ArticleViewer(View v) {
        int num = ((Integer) v.getTag()).intValue();
        this.selectedColor = num;
        int a12 = 0;
        while (a12 < 3) {
            this.colorCells[a12].select(a12 == num);
            a12++;
        }
        updateNightModeButton();
        updatePaintColors();
        for (int i = 0; i < this.listView.length; i++) {
            this.adapter[i].notifyDataSetChanged();
        }
    }

    final /* synthetic */ void lambda$setParentActivity$17$ArticleViewer(View v) {
        int num = ((Integer) v.getTag()).intValue();
        this.selectedFont = num;
        int a1 = 0;
        while (a1 < 2) {
            this.fontCells[a1].select(a1 == num);
            a1++;
        }
        updatePaintFonts();
        for (int i = 0; i < this.listView.length; i++) {
            this.adapter[i].notifyDataSetChanged();
        }
    }

    final /* synthetic */ void lambda$setParentActivity$18$ArticleViewer(View v) {
        if (this.currentPage != null && this.parentActivity != null) {
            showDialog(new ShareAlert(this.parentActivity, null, this.currentPage.url, false, this.currentPage.url, true));
        }
    }

    final /* synthetic */ void lambda$setParentActivity$19$ArticleViewer(float progress) {
        if (this.videoPlayer != null) {
            this.videoPlayer.seekTo((long) ((int) (((float) this.videoPlayer.getDuration()) * progress)));
        }
    }

    final /* synthetic */ void lambda$setParentActivity$20$ArticleViewer(View v) {
        if (this.videoPlayer == null) {
            return;
        }
        if (this.isPlaying) {
            this.videoPlayer.pause();
        } else {
            this.videoPlayer.play();
        }
    }

    private void showNightModeHint() {
        int i = 3;
        int i2 = 56;
        if (this.parentActivity != null && this.nightModeHintView == null && this.nightModeEnabled) {
            int i3;
            int i4;
            this.nightModeHintView = new FrameLayout(this.parentActivity);
            this.nightModeHintView.setBackgroundColor(-13421773);
            this.containerView.addView(this.nightModeHintView, LayoutHelper.createFrame(-1, -2, 83));
            ImageView nightModeImageView = new ImageView(this.parentActivity);
            nightModeImageView.setScaleType(ScaleType.CENTER);
            nightModeImageView.setImageResource(R.drawable.moon);
            FrameLayout frameLayout = this.nightModeHintView;
            if (LocaleController.isRTL) {
                i3 = 5;
            } else {
                i3 = 3;
            }
            frameLayout.addView(nightModeImageView, LayoutHelper.createFrame(56, 56, i3 | 16));
            TextView textView = new TextView(this.parentActivity);
            textView.setText(LocaleController.getString("InstantViewNightMode", R.string.InstantViewNightMode));
            textView.setTextColor(-1);
            textView.setTextSize(1, 15.0f);
            FrameLayout frameLayout2 = this.nightModeHintView;
            if (LocaleController.isRTL) {
                i = 5;
            }
            i |= 48;
            if (LocaleController.isRTL) {
                i4 = 10;
            } else {
                i4 = 56;
            }
            float f = (float) i4;
            if (!LocaleController.isRTL) {
                i2 = 10;
            }
            frameLayout2.addView(textView, LayoutHelper.createFrame(-1, -1.0f, i, f, 11.0f, (float) i2, 12.0f));
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.nightModeHintView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(100.0f), 0.0f});
            animatorSet.playTogether(animatorArr);
            animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AndroidUtilities.runOnUIThread(new ArticleViewer$12$$Lambda$0(this), 3000);
                }

                final /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$12() {
                    AnimatorSet animatorSet1 = new AnimatorSet();
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.nightModeHintView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(100.0f)});
                    animatorSet1.playTogether(animatorArr);
                    animatorSet1.setInterpolator(new DecelerateInterpolator(1.5f));
                    animatorSet1.setDuration(250);
                    animatorSet1.start();
                }
            });
            animatorSet.setDuration(250);
            animatorSet.start();
        }
    }

    private void updateNightModeButton() {
        this.nightModeImageView.setEnabled(this.selectedColor != 2);
        this.nightModeImageView.setAlpha(this.selectedColor == 2 ? 0.5f : 1.0f);
        ImageView imageView = this.nightModeImageView;
        int i = (!this.nightModeEnabled || this.selectedColor == 2) ? -3355444 : -15428119;
        imageView.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
    }

    private void checkScrollAnimated() {
        if (this.currentHeaderHeight != AndroidUtilities.dp(56.0f)) {
            ValueAnimator va = ValueAnimator.ofObject(new IntEvaluator(), new Object[]{Integer.valueOf(this.currentHeaderHeight), Integer.valueOf(AndroidUtilities.dp(56.0f))}).setDuration(180);
            va.setInterpolator(new DecelerateInterpolator());
            va.addUpdateListener(new ArticleViewer$$Lambda$18(this));
            va.start();
        }
    }

    final /* synthetic */ void lambda$checkScrollAnimated$21$ArticleViewer(ValueAnimator animation) {
        setCurrentHeaderHeight(((Integer) animation.getAnimatedValue()).intValue());
    }

    private void setCurrentHeaderHeight(int newHeight) {
        int maxHeight = AndroidUtilities.dp(56.0f);
        int minHeight = Math.max(AndroidUtilities.statusBarHeight, AndroidUtilities.dp(24.0f));
        if (newHeight < minHeight) {
            newHeight = minHeight;
        } else if (newHeight > maxHeight) {
            newHeight = maxHeight;
        }
        float heightDiff = (float) (maxHeight - minHeight);
        this.currentHeaderHeight = newHeight;
        float scale = 0.8f + ((((float) (this.currentHeaderHeight - minHeight)) / heightDiff) * 0.2f);
        float scale2 = 0.5f + ((((float) (this.currentHeaderHeight - minHeight)) / heightDiff) * 0.5f);
        this.backButton.setScaleX(scale);
        this.backButton.setScaleY(scale);
        this.backButton.setTranslationY((float) ((maxHeight - this.currentHeaderHeight) / 2));
        this.shareContainer.setScaleX(scale);
        this.shareContainer.setScaleY(scale);
        this.settingsButton.setScaleX(scale);
        this.settingsButton.setScaleY(scale);
        this.titleTextView.setScaleX(scale);
        this.titleTextView.setScaleY(scale);
        this.lineProgressView.setScaleY(scale2);
        this.shareContainer.setTranslationY((float) ((maxHeight - this.currentHeaderHeight) / 2));
        this.settingsButton.setTranslationY((float) ((maxHeight - this.currentHeaderHeight) / 2));
        this.titleTextView.setTranslationY((float) ((maxHeight - this.currentHeaderHeight) / 2));
        this.headerView.setTranslationY((float) (this.currentHeaderHeight - maxHeight));
        for (RecyclerListView topGlowOffset : this.listView) {
            topGlowOffset.setTopGlowOffset(this.currentHeaderHeight);
        }
    }

    private void checkScroll(int dy) {
        setCurrentHeaderHeight(this.currentHeaderHeight - dy);
    }

    private void openPreviewsChat(User user, long wid) {
        if (user != null && this.parentActivity != null) {
            Bundle args = new Bundle();
            args.putInt("user_id", user.id);
            args.putString("botUser", "webpage" + wid);
            ((LaunchActivity) this.parentActivity).presentFragment(new ChatActivity(args), false, true);
            close(false, true);
        }
    }

    public boolean open(MessageObject messageObject) {
        return open(messageObject, null, null, true);
    }

    public boolean open(TL_webPage webpage, String url) {
        return open(null, webpage, url, true);
    }

    private boolean open(MessageObject messageObject, WebPage webpage, String url, boolean first) {
        if (this.parentActivity == null || ((this.isVisible && !this.collapsed) || (messageObject == null && webpage == null))) {
            return false;
        }
        if (messageObject != null) {
            webpage = messageObject.messageOwner.media.webpage;
        }
        String anchor = null;
        int index;
        if (messageObject != null) {
            webpage = messageObject.messageOwner.media.webpage;
            for (int a = 0; a < messageObject.messageOwner.entities.size(); a++) {
                MessageEntity entity = (MessageEntity) messageObject.messageOwner.entities.get(a);
                if (entity instanceof TL_messageEntityUrl) {
                    try {
                        String webPageUrl;
                        url = messageObject.messageOwner.message.substring(entity.offset, entity.offset + entity.length).toLowerCase();
                        if (TextUtils.isEmpty(webpage.cached_page.url)) {
                            webPageUrl = webpage.url.toLowerCase();
                        } else {
                            webPageUrl = webpage.cached_page.url.toLowerCase();
                        }
                        if (url.contains(webPageUrl) || webPageUrl.contains(url)) {
                            index = url.lastIndexOf(35);
                            if (index != -1) {
                                anchor = url.substring(index + 1);
                            }
                        }
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            }
        } else if (url != null) {
            index = url.lastIndexOf(35);
            if (index != -1) {
                anchor = url.substring(index + 1);
            }
        }
        this.pagesStack.clear();
        this.collapsed = false;
        this.backDrawable.setRotation(0.0f, false);
        this.containerView.setTranslationX(0.0f);
        this.containerView.setTranslationY(0.0f);
        this.listView[0].setTranslationY(0.0f);
        this.listView[0].setTranslationX(0.0f);
        this.listView[1].setTranslationX(0.0f);
        this.listView[0].setAlpha(1.0f);
        this.windowView.setInnerTranslationX(0.0f);
        this.actionBar.setVisibility(8);
        this.bottomLayout.setVisibility(8);
        this.captionTextView.setVisibility(8);
        this.captionTextViewNext.setVisibility(8);
        this.layoutManager[0].scrollToPositionWithOffset(0, 0);
        checkScrollAnimated();
        boolean scrolledToAnchor = addPageToStack(webpage, anchor, 0);
        if (first) {
            String anchorFinal = (scrolledToAnchor || anchor == null) ? null : anchor;
            TL_messages_getWebPage req = new TL_messages_getWebPage();
            req.url = webpage.url;
            if ((webpage.cached_page instanceof TL_pagePart_layer82) || webpage.cached_page.part) {
                req.hash = 0;
            } else {
                req.hash = webpage.hash;
            }
            WebPage webPageFinal = webpage;
            int currentAccount = UserConfig.selectedAccount;
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, new ArticleViewer$$Lambda$19(this, webPageFinal, messageObject, anchorFinal, currentAccount));
        }
        this.lastInsets = null;
        LayoutParams layoutParams;
        if (this.isVisible) {
            layoutParams = this.windowLayoutParams;
            layoutParams.flags &= -17;
            ((WindowManager) this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
        } else {
            WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
            if (this.attachedToWindow) {
                try {
                    wm.removeView(this.windowView);
                } catch (Exception e2) {
                }
            }
            try {
                if (VERSION.SDK_INT >= 21) {
                    this.windowLayoutParams.flags = -NUM;
                }
                layoutParams = this.windowLayoutParams;
                layoutParams.flags |= 1032;
                this.windowView.setFocusable(false);
                this.containerView.setFocusable(false);
                wm.addView(this.windowView, this.windowLayoutParams);
            } catch (Throwable e3) {
                FileLog.e(e3);
                return false;
            }
        }
        this.isVisible = true;
        this.animationInProgress = 1;
        this.windowView.setAlpha(0.0f);
        this.containerView.setAlpha(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        r2 = new Animator[3];
        float[] fArr = new float[2];
        r2[0] = ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f, 1.0f});
        fArr = new float[2];
        r2[1] = ObjectAnimator.ofFloat(this.containerView, View.ALPHA, new float[]{0.0f, 1.0f});
        r2[2] = ObjectAnimator.ofFloat(this.windowView, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(56.0f), 0.0f});
        animatorSet.playTogether(r2);
        this.animationEndRunnable = new ArticleViewer$$Lambda$20(this);
        animatorSet.setDuration(150);
        animatorSet.setInterpolator(this.interpolator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                AndroidUtilities.runOnUIThread(new ArticleViewer$13$$Lambda$0(this));
            }

            final /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$13() {
                NotificationCenter.getInstance(ArticleViewer.this.currentAccount).setAnimationInProgress(false);
                if (ArticleViewer.this.animationEndRunnable != null) {
                    ArticleViewer.this.animationEndRunnable.run();
                    ArticleViewer.this.animationEndRunnable = null;
                }
            }
        });
        this.transitionAnimationStartTime = System.currentTimeMillis();
        AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$21(this, animatorSet));
        if (VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(2, null);
        }
        return true;
    }

    final /* synthetic */ void lambda$open$23$ArticleViewer(WebPage webPageFinal, MessageObject messageObject, String anchorFinal, int currentAccount, TLObject response, TL_error error) {
        if (response instanceof TL_webPage) {
            TL_webPage webPage = (TL_webPage) response;
            if (webPage.cached_page != null) {
                AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$40(this, webPageFinal, webPage, messageObject, anchorFinal));
                LongSparseArray<WebPage> webpages = new LongSparseArray(1);
                webpages.put(webPage.id, webPage);
                MessagesStorage.getInstance(currentAccount).putWebPages(webpages);
            }
        }
    }

    final /* synthetic */ void lambda$null$22$ArticleViewer(WebPage webPageFinal, TL_webPage webPage, MessageObject messageObject, String anchorFinal) {
        if (!this.pagesStack.isEmpty() && this.pagesStack.get(0) == webPageFinal && webPage.cached_page != null) {
            if (messageObject != null) {
                messageObject.messageOwner.media.webpage = webPage;
            }
            this.pagesStack.set(0, webPage);
            if (this.pagesStack.size() == 1) {
                this.currentPage = webPage;
                ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().remove("article" + this.currentPage.id).commit();
                updateInterfaceForCurrentPage(0);
                if (anchorFinal != null) {
                    scrollToAnchor(anchorFinal);
                }
            }
        }
    }

    final /* synthetic */ void lambda$open$24$ArticleViewer() {
        if (this.containerView != null && this.windowView != null) {
            if (VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, null);
            }
            this.animationInProgress = 0;
            AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
        }
    }

    final /* synthetic */ void lambda$open$25$ArticleViewer(AnimatorSet animatorSet) {
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        animatorSet.start();
    }

    private void showProgressView(boolean useLine, final boolean show) {
        if (useLine) {
            AndroidUtilities.cancelRunOnUIThread(this.lineProgressTickRunnable);
            if (show) {
                this.lineProgressView.setProgress(0.0f, false);
                this.lineProgressView.setProgress(0.3f, true);
                AndroidUtilities.runOnUIThread(this.lineProgressTickRunnable, 100);
                return;
            }
            this.lineProgressView.setProgress(1.0f, true);
            return;
        }
        if (this.progressViewAnimation != null) {
            this.progressViewAnimation.cancel();
        }
        this.progressViewAnimation = new AnimatorSet();
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (show) {
            this.progressView.setVisibility(0);
            this.shareContainer.setEnabled(false);
            animatorSet = this.progressViewAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.shareButton, View.SCALE_X, new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.shareButton, View.SCALE_Y, new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.shareButton, View.ALPHA, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
        } else {
            this.shareButton.setVisibility(0);
            this.shareContainer.setEnabled(true);
            animatorSet = this.progressViewAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.shareButton, View.SCALE_X, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.shareButton, View.SCALE_Y, new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.shareButton, View.ALPHA, new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
        }
        this.progressViewAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animation)) {
                    if (show) {
                        ArticleViewer.this.shareButton.setVisibility(4);
                    } else {
                        ArticleViewer.this.progressView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animation)) {
                    ArticleViewer.this.progressViewAnimation = null;
                }
            }
        });
        this.progressViewAnimation.setDuration(150);
        this.progressViewAnimation.start();
    }

    public void collapse() {
        if (this.parentActivity != null && this.isVisible && !checkAnimation()) {
            int i;
            if (this.fullscreenVideoContainer.getVisibility() == 0) {
                if (this.customView != null) {
                    this.fullscreenVideoContainer.setVisibility(4);
                    this.customViewCallback.onCustomViewHidden();
                    this.fullscreenVideoContainer.removeView(this.customView);
                    this.customView = null;
                } else if (this.fullscreenedVideo != null) {
                    this.fullscreenedVideo.exitFullscreen();
                }
            }
            if (this.isPhotoVisible) {
                closePhoto(false);
            }
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[12];
            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, new float[]{(float) (this.containerView.getMeasuredWidth() - AndroidUtilities.dp(56.0f))});
            FrameLayout frameLayout = this.containerView;
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[1];
            int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
            if (VERSION.SDK_INT >= 21) {
                i = AndroidUtilities.statusBarHeight;
            } else {
                i = 0;
            }
            fArr[0] = (float) (i + currentActionBarHeight);
            animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
            animatorArr[2] = ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.listView[0], View.ALPHA, new float[]{0.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.listView[0], View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(56.0f))});
            animatorArr[5] = ObjectAnimator.ofFloat(this.headerView, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[6] = ObjectAnimator.ofFloat(this.backButton, View.SCALE_X, new float[]{1.0f});
            animatorArr[7] = ObjectAnimator.ofFloat(this.backButton, View.SCALE_Y, new float[]{1.0f});
            animatorArr[8] = ObjectAnimator.ofFloat(this.backButton, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[9] = ObjectAnimator.ofFloat(this.shareContainer, View.SCALE_X, new float[]{1.0f});
            animatorArr[10] = ObjectAnimator.ofFloat(this.shareContainer, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[11] = ObjectAnimator.ofFloat(this.shareContainer, View.SCALE_Y, new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
            this.collapsed = true;
            this.animationInProgress = 2;
            this.animationEndRunnable = new ArticleViewer$$Lambda$22(this);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(250);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ArticleViewer.this.animationEndRunnable != null) {
                        ArticleViewer.this.animationEndRunnable.run();
                        ArticleViewer.this.animationEndRunnable = null;
                    }
                }
            });
            this.transitionAnimationStartTime = System.currentTimeMillis();
            if (VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(2, null);
            }
            this.backDrawable.setRotation(1.0f, true);
            animatorSet.start();
        }
    }

    final /* synthetic */ void lambda$collapse$26$ArticleViewer() {
        if (this.containerView != null) {
            if (VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, null);
            }
            this.animationInProgress = 0;
            ((WindowManager) this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }

    public void uncollapse() {
        if (this.parentActivity != null && this.isVisible && !checkAnimation()) {
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[12];
            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{1.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.listView[0], View.ALPHA, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.listView[0], View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.headerView, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[6] = ObjectAnimator.ofFloat(this.backButton, View.SCALE_X, new float[]{1.0f});
            animatorArr[7] = ObjectAnimator.ofFloat(this.backButton, View.SCALE_Y, new float[]{1.0f});
            animatorArr[8] = ObjectAnimator.ofFloat(this.backButton, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[9] = ObjectAnimator.ofFloat(this.shareContainer, View.SCALE_X, new float[]{1.0f});
            animatorArr[10] = ObjectAnimator.ofFloat(this.shareContainer, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[11] = ObjectAnimator.ofFloat(this.shareContainer, View.SCALE_Y, new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
            this.collapsed = false;
            this.animationInProgress = 2;
            this.animationEndRunnable = new ArticleViewer$$Lambda$23(this);
            animatorSet.setDuration(250);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ArticleViewer.this.animationEndRunnable != null) {
                        ArticleViewer.this.animationEndRunnable.run();
                        ArticleViewer.this.animationEndRunnable = null;
                    }
                }
            });
            this.transitionAnimationStartTime = System.currentTimeMillis();
            if (VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(2, null);
            }
            this.backDrawable.setRotation(0.0f, true);
            animatorSet.start();
        }
    }

    final /* synthetic */ void lambda$uncollapse$27$ArticleViewer() {
        if (this.containerView != null) {
            if (VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, null);
            }
            this.animationInProgress = 0;
        }
    }

    private void saveCurrentPagePosition() {
        boolean z = false;
        if (this.currentPage != null) {
            int position = this.layoutManager[0].findFirstVisibleItemPosition();
            if (position != -1) {
                int offset;
                View view = this.layoutManager[0].findViewByPosition(position);
                if (view != null) {
                    offset = view.getTop();
                } else {
                    offset = 0;
                }
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit();
                String key = "article" + this.currentPage.id;
                Editor putInt = editor.putInt(key, position).putInt(key + "o", offset);
                String str = key + "r";
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    z = true;
                }
                putInt.putBoolean(str, z).commit();
            }
        }
    }

    public void close(boolean byBackPress, boolean force) {
        if (this.parentActivity != null && this.isVisible && !checkAnimation()) {
            if (this.fullscreenVideoContainer.getVisibility() == 0) {
                if (this.customView != null) {
                    this.fullscreenVideoContainer.setVisibility(4);
                    this.customViewCallback.onCustomViewHidden();
                    this.fullscreenVideoContainer.removeView(this.customView);
                    this.customView = null;
                } else if (this.fullscreenedVideo != null) {
                    this.fullscreenedVideo.exitFullscreen();
                }
                if (!force) {
                    return;
                }
            }
            if (this.isPhotoVisible) {
                boolean z;
                if (force) {
                    z = false;
                } else {
                    z = true;
                }
                closePhoto(z);
                if (!force) {
                    return;
                }
            }
            if (this.openUrlReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.openUrlReqId, true);
                this.openUrlReqId = 0;
                showProgressView(true, false);
            }
            if (this.previewsReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.previewsReqId, true);
                this.previewsReqId = 0;
                showProgressView(true, false);
            }
            saveCurrentPagePosition();
            if (!byBackPress || force || !removeLastPageFromStack()) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
                this.parentFragment = null;
                try {
                    if (this.visibleDialog != null) {
                        this.visibleDialog.dismiss();
                        this.visibleDialog = null;
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.containerView, View.ALPHA, new float[]{0.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.windowView, View.TRANSLATION_X, new float[]{0.0f, (float) AndroidUtilities.dp(56.0f)});
                animatorSet.playTogether(animatorArr);
                this.animationInProgress = 2;
                this.animationEndRunnable = new ArticleViewer$$Lambda$24(this);
                animatorSet.setDuration(150);
                animatorSet.setInterpolator(this.interpolator);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ArticleViewer.this.animationEndRunnable != null) {
                            ArticleViewer.this.animationEndRunnable.run();
                            ArticleViewer.this.animationEndRunnable = null;
                        }
                    }
                });
                this.transitionAnimationStartTime = System.currentTimeMillis();
                if (VERSION.SDK_INT >= 18) {
                    this.containerView.setLayerType(2, null);
                }
                animatorSet.start();
            }
        }
    }

    final /* synthetic */ void lambda$close$28$ArticleViewer() {
        if (this.containerView != null) {
            if (VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, null);
            }
            this.animationInProgress = 0;
            onClosed();
        }
    }

    private void onClosed() {
        this.isVisible = false;
        this.currentPage = null;
        for (int i = 0; i < this.listView.length; i++) {
            this.adapter[i].cleanup();
        }
        try {
            this.parentActivity.getWindow().clearFlags(128);
        } catch (Throwable e) {
            FileLog.e(e);
        }
        for (int a = 0; a < this.createdWebViews.size(); a++) {
            ((BlockEmbedCell) this.createdWebViews.get(a)).destroyWebView(false);
        }
        this.containerView.post(new ArticleViewer$$Lambda$25(this));
    }

    final /* synthetic */ void lambda$onClosed$29$ArticleViewer() {
        try {
            if (this.windowView.getParent() != null) {
                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    private void loadChannel(BlockChannelCell cell, WebpageAdapter adapter, Chat channel) {
        if (!this.loadingChannel && !TextUtils.isEmpty(channel.username)) {
            this.loadingChannel = true;
            TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
            req.username = channel.username;
            int currentAccount = UserConfig.selectedAccount;
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, new ArticleViewer$$Lambda$26(this, adapter, currentAccount, cell));
        }
    }

    final /* synthetic */ void lambda$loadChannel$31$ArticleViewer(WebpageAdapter adapter, int currentAccount, BlockChannelCell cell, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$39(this, adapter, error, response, currentAccount, cell));
    }

    final /* synthetic */ void lambda$null$30$ArticleViewer(WebpageAdapter adapter, TL_error error, TLObject response, int currentAccount, BlockChannelCell cell) {
        this.loadingChannel = false;
        if (this.parentFragment != null && !adapter.blocks.isEmpty()) {
            if (error == null) {
                TL_contacts_resolvedPeer res = (TL_contacts_resolvedPeer) response;
                if (res.chats.isEmpty()) {
                    cell.setState(4, false);
                    return;
                }
                MessagesController.getInstance(currentAccount).putUsers(res.users, false);
                MessagesController.getInstance(currentAccount).putChats(res.chats, false);
                MessagesStorage.getInstance(currentAccount).putUsersAndChats(res.users, res.chats, false, true);
                this.loadedChannel = (Chat) res.chats.get(0);
                if (!this.loadedChannel.left || this.loadedChannel.kicked) {
                    cell.setState(4, false);
                    return;
                } else {
                    cell.setState(0, false);
                    return;
                }
            }
            cell.setState(4, false);
        }
    }

    private void joinChannel(BlockChannelCell cell, Chat channel) {
        TL_channels_joinChannel req = new TL_channels_joinChannel();
        req.channel = MessagesController.getInputChannel(channel);
        int currentAccount = UserConfig.selectedAccount;
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, new ArticleViewer$$Lambda$27(this, cell, currentAccount, req, channel));
    }

    final /* synthetic */ void lambda$joinChannel$35$ArticleViewer(BlockChannelCell cell, int currentAccount, TL_channels_joinChannel req, Chat channel, TLObject response, TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$36(this, cell, currentAccount, error, req));
            return;
        }
        boolean hasJoinMessage = false;
        Updates updates = (Updates) response;
        for (int a = 0; a < updates.updates.size(); a++) {
            Update update = (Update) updates.updates.get(a);
            if ((update instanceof TL_updateNewChannelMessage) && (((TL_updateNewChannelMessage) update).message.action instanceof TL_messageActionChatAddUser)) {
                hasJoinMessage = true;
                break;
            }
        }
        MessagesController.getInstance(currentAccount).processUpdates(updates, false);
        if (!hasJoinMessage) {
            MessagesController.getInstance(currentAccount).generateJoinMessage(channel.id, true);
        }
        AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$37(cell));
        AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$38(currentAccount, channel), 1000);
        MessagesStorage.getInstance(currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), null, true, channel.id);
    }

    final /* synthetic */ void lambda$null$32$ArticleViewer(BlockChannelCell cell, int currentAccount, TL_error error, TL_channels_joinChannel req) {
        cell.setState(0, false);
        AlertsCreator.processError(currentAccount, error, this.parentFragment, req, Boolean.valueOf(true));
    }

    private boolean checkAnimation() {
        if (this.animationInProgress != 0 && Math.abs(this.transitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            if (this.animationEndRunnable != null) {
                this.animationEndRunnable.run();
                this.animationEndRunnable = null;
            }
            this.animationInProgress = 0;
        }
        if (this.animationInProgress != 0) {
            return true;
        }
        return false;
    }

    public void destroyArticleViewer() {
        if (this.parentActivity != null && this.windowView != null) {
            releasePlayer();
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Throwable e) {
                FileLog.e(e);
            }
            for (int a = 0; a < this.createdWebViews.size(); a++) {
                ((BlockEmbedCell) this.createdWebViews.get(a)).destroyWebView(true);
            }
            this.createdWebViews.clear();
            try {
                this.parentActivity.getWindow().clearFlags(128);
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
            if (this.currentThumb != null) {
                this.currentThumb.release();
                this.currentThumb = null;
            }
            this.animatingImageView.setImageBitmap(null);
            this.parentActivity = null;
            this.parentFragment = null;
            Instance = null;
        }
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void showDialog(Dialog dialog) {
        if (this.parentActivity != null) {
            try {
                if (this.visibleDialog != null) {
                    this.visibleDialog.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            try {
                this.visibleDialog = dialog;
                this.visibleDialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new ArticleViewer$$Lambda$28(this));
                dialog.show();
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
        }
    }

    final /* synthetic */ void lambda$showDialog$36$ArticleViewer(DialogInterface dialog1) {
        this.visibleDialog = null;
    }

    private void onSharePressed() {
        if (this.parentActivity != null && this.currentMedia != null) {
            try {
                File f = getMediaFile(this.currentIndex);
                if (f == null || !f.exists()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity);
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
                    showDialog(builder.create());
                    return;
                }
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType(getMediaMime(this.currentIndex));
                if (VERSION.SDK_INT >= 24) {
                    try {
                        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this.parentActivity, "org.telegram.messenger.provider", f));
                        intent.setFlags(1);
                    } catch (Exception e) {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                    }
                } else {
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                }
                this.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
        }
    }

    private void setScaleToFill() {
        float bitmapWidth = (float) this.centerImage.getBitmapWidth();
        float containerWidth = (float) getContainerViewWidth();
        float bitmapHeight = (float) this.centerImage.getBitmapHeight();
        float containerHeight = (float) getContainerViewHeight();
        float scaleFit = Math.min(containerHeight / bitmapHeight, containerWidth / bitmapWidth);
        this.scale = Math.max(containerWidth / ((float) ((int) (bitmapWidth * scaleFit))), containerHeight / ((float) ((int) (bitmapHeight * scaleFit))));
        updateMinMax(this.scale);
    }

    private void updateVideoPlayerTime() {
        String newText;
        if (this.videoPlayer == null) {
            newText = String.format("%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0)});
        } else {
            long current = this.videoPlayer.getCurrentPosition() / 1000;
            if (this.videoPlayer.getDuration() / 1000 == -9223372036854775807L || current == -9223372036854775807L) {
                newText = String.format("%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0)});
            } else {
                newText = String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(current / 60), Long.valueOf(current % 60), Long.valueOf(total / 60), Long.valueOf(total % 60)});
            }
        }
        if (!TextUtils.equals(this.videoPlayerTime.getText(), newText)) {
            this.videoPlayerTime.setText(newText);
        }
    }

    @SuppressLint({"NewApi"})
    private void preparePlayer(File file, boolean playWhenReady) {
        if (this.parentActivity != null) {
            releasePlayer();
            if (this.videoTextureView == null) {
                this.aspectRatioFrameLayout = new AspectRatioFrameLayout(this.parentActivity);
                this.aspectRatioFrameLayout.setVisibility(4);
                this.photoContainerView.addView(this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
                this.videoTextureView = new TextureView(this.parentActivity);
                this.videoTextureView.setOpaque(false);
                this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1, 17));
            }
            this.textureUploaded = false;
            this.videoCrossfadeStarted = false;
            TextureView textureView = this.videoTextureView;
            this.videoCrossfadeAlpha = 0.0f;
            textureView.setAlpha(0.0f);
            this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
            if (this.videoPlayer == null) {
                long duration;
                this.videoPlayer = new VideoPlayer();
                this.videoPlayer.setTextureView(this.videoTextureView);
                this.videoPlayer.setDelegate(new VideoPlayerDelegate() {
                    public void onStateChanged(boolean playWhenReady, int playbackState) {
                        if (ArticleViewer.this.videoPlayer != null) {
                            if (playbackState == 4 || playbackState == 1) {
                                try {
                                    ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                            } else {
                                try {
                                    ArticleViewer.this.parentActivity.getWindow().addFlags(128);
                                } catch (Throwable e2) {
                                    FileLog.e(e2);
                                }
                            }
                            if (playbackState == 3 && ArticleViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                                ArticleViewer.this.aspectRatioFrameLayout.setVisibility(0);
                            }
                            if (!ArticleViewer.this.videoPlayer.isPlaying() || playbackState == 4) {
                                if (ArticleViewer.this.isPlaying) {
                                    ArticleViewer.this.isPlaying = false;
                                    ArticleViewer.this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
                                    AndroidUtilities.cancelRunOnUIThread(ArticleViewer.this.updateProgressRunnable);
                                    if (playbackState == 4 && !ArticleViewer.this.videoPlayerSeekbar.isDragging()) {
                                        ArticleViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                                        ArticleViewer.this.videoPlayerControlFrameLayout.invalidate();
                                        ArticleViewer.this.videoPlayer.seekTo(0);
                                        ArticleViewer.this.videoPlayer.pause();
                                    }
                                }
                            } else if (!ArticleViewer.this.isPlaying) {
                                ArticleViewer.this.isPlaying = true;
                                ArticleViewer.this.videoPlayButton.setImageResource(R.drawable.inline_video_pause);
                                AndroidUtilities.runOnUIThread(ArticleViewer.this.updateProgressRunnable);
                            }
                            ArticleViewer.this.updateVideoPlayerTime();
                        }
                    }

                    public void onError(Exception e) {
                        FileLog.e((Throwable) e);
                    }

                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                        if (ArticleViewer.this.aspectRatioFrameLayout != null) {
                            if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                                int temp = width;
                                width = height;
                                height = temp;
                            }
                            ArticleViewer.this.aspectRatioFrameLayout.setAspectRatio(height == 0 ? 1.0f : (((float) width) * pixelWidthHeightRatio) / ((float) height), unappliedRotationDegrees);
                        }
                    }

                    public void onRenderedFirstFrame() {
                        if (!ArticleViewer.this.textureUploaded) {
                            ArticleViewer.this.textureUploaded = true;
                            ArticleViewer.this.containerView.invalidate();
                        }
                    }

                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }
                });
                if (this.videoPlayer != null) {
                    duration = this.videoPlayer.getDuration();
                    if (duration == -9223372036854775807L) {
                        duration = 0;
                    }
                } else {
                    duration = 0;
                }
                duration /= 1000;
                int ceil = (int) Math.ceil((double) this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(duration / 60), Long.valueOf(duration % 60), Long.valueOf(duration / 60), Long.valueOf(duration % 60)})));
            }
            this.videoPlayer.preparePlayer(Uri.fromFile(file), "other");
            this.bottomLayout.setVisibility(0);
            this.videoPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    private void releasePlayer() {
        if (this.videoPlayer != null) {
            this.videoPlayer.releasePlayer(true);
            this.videoPlayer = null;
        }
        try {
            this.parentActivity.getWindow().clearFlags(128);
        } catch (Throwable e) {
            FileLog.e(e);
        }
        if (this.aspectRatioFrameLayout != null) {
            this.photoContainerView.removeView(this.aspectRatioFrameLayout);
            this.aspectRatioFrameLayout = null;
        }
        if (this.videoTextureView != null) {
            this.videoTextureView = null;
        }
        if (this.isPlaying) {
            this.isPlaying = false;
            this.videoPlayButton.setImageResource(R.drawable.inline_video_play);
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
        }
        this.bottomLayout.setVisibility(8);
    }

    private void toggleActionBar(boolean show, boolean animated) {
        float f = 1.0f;
        if (show) {
            this.actionBar.setVisibility(0);
            if (this.videoPlayer != null) {
                this.bottomLayout.setVisibility(0);
            }
            if (this.captionTextView.getTag() != null) {
                this.captionTextView.setVisibility(0);
            }
        }
        this.isActionBarVisible = show;
        this.actionBar.setEnabled(show);
        this.bottomLayout.setEnabled(show);
        float f2;
        FrameLayout frameLayout;
        TextView textView;
        if (animated) {
            ArrayList<Animator> arrayList = new ArrayList();
            ActionBar actionBar = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            if (show) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            fArr[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(actionBar, property, fArr));
            GroupedPhotosListView groupedPhotosListView = this.groupedPhotosListView;
            property = View.ALPHA;
            fArr = new float[1];
            if (show) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            fArr[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView, property, fArr));
            frameLayout = this.bottomLayout;
            property = View.ALPHA;
            fArr = new float[1];
            if (show) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            fArr[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            if (this.captionTextView.getTag() != null) {
                textView = this.captionTextView;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(textView, property2, fArr2));
            }
            this.currentActionBarAnimation = new AnimatorSet();
            this.currentActionBarAnimation.playTogether(arrayList);
            if (!show) {
                this.currentActionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ArticleViewer.this.currentActionBarAnimation != null && ArticleViewer.this.currentActionBarAnimation.equals(animation)) {
                            ArticleViewer.this.actionBar.setVisibility(8);
                            if (ArticleViewer.this.videoPlayer != null) {
                                ArticleViewer.this.bottomLayout.setVisibility(8);
                            }
                            if (ArticleViewer.this.captionTextView.getTag() != null) {
                                ArticleViewer.this.captionTextView.setVisibility(8);
                            }
                            ArticleViewer.this.currentActionBarAnimation = null;
                        }
                    }
                });
            }
            this.currentActionBarAnimation.setDuration(200);
            this.currentActionBarAnimation.start();
            return;
        }
        this.actionBar.setAlpha(show ? 1.0f : 0.0f);
        frameLayout = this.bottomLayout;
        if (show) {
            f2 = 1.0f;
        } else {
            f2 = 0.0f;
        }
        frameLayout.setAlpha(f2);
        if (this.captionTextView.getTag() != null) {
            textView = this.captionTextView;
            if (!show) {
                f = 0.0f;
            }
            textView.setAlpha(f);
        }
        if (!show) {
            this.actionBar.setVisibility(8);
            if (this.videoPlayer != null) {
                this.bottomLayout.setVisibility(8);
            }
            if (this.captionTextView.getTag() != null) {
                this.captionTextView.setVisibility(8);
            }
        }
    }

    private String getFileName(int index) {
        TLObject media = getMedia(index);
        if (media instanceof Photo) {
            media = FileLoader.getClosestPhotoSizeWithSize(((Photo) media).sizes, AndroidUtilities.getPhotoSize());
        }
        return FileLoader.getAttachFileName(media);
    }

    private TLObject getMedia(int index) {
        if (this.imagesArr.isEmpty() || index >= this.imagesArr.size() || index < 0) {
            return null;
        }
        PageBlock block = (PageBlock) this.imagesArr.get(index);
        if (block instanceof TL_pageBlockPhoto) {
            return getPhotoWithId(((TL_pageBlockPhoto) block).photo_id);
        }
        if (block instanceof TL_pageBlockVideo) {
            return getDocumentWithId(((TL_pageBlockVideo) block).video_id);
        }
        return null;
    }

    private File getMediaFile(int index) {
        if (this.imagesArr.isEmpty() || index >= this.imagesArr.size() || index < 0) {
            return null;
        }
        PageBlock block = (PageBlock) this.imagesArr.get(index);
        if (block instanceof TL_pageBlockPhoto) {
            Photo photo = getPhotoWithId(((TL_pageBlockPhoto) block).photo_id);
            if (photo == null) {
                return null;
            }
            PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
            if (sizeFull != null) {
                return FileLoader.getPathToAttach(sizeFull, true);
            }
            return null;
        } else if (!(block instanceof TL_pageBlockVideo)) {
            return null;
        } else {
            Document document = getDocumentWithId(((TL_pageBlockVideo) block).video_id);
            if (document != null) {
                return FileLoader.getPathToAttach(document, true);
            }
            return null;
        }
    }

    private boolean isVideoBlock(PageBlock block) {
        if (block != null && (block instanceof TL_pageBlockVideo)) {
            Document document = getDocumentWithId(((TL_pageBlockVideo) block).video_id);
            if (document != null) {
                return MessageObject.isVideoDocument(document);
            }
        }
        return false;
    }

    private boolean isMediaVideo(int index) {
        return !this.imagesArr.isEmpty() && index < this.imagesArr.size() && index >= 0 && isVideoBlock((PageBlock) this.imagesArr.get(index));
    }

    private String getMediaMime(int index) {
        if (index >= this.imagesArr.size() || index < 0) {
            return "image/jpeg";
        }
        PageBlock block = (PageBlock) this.imagesArr.get(index);
        if (block instanceof TL_pageBlockVideo) {
            Document document = getDocumentWithId(((TL_pageBlockVideo) block).video_id);
            if (document != null) {
                return document.mime_type;
            }
        }
        return "image/jpeg";
    }

    private PhotoSize getFileLocation(int index, int[] size) {
        if (index < 0 || index >= this.imagesArr.size()) {
            return null;
        }
        TLObject media = getMedia(index);
        if (media instanceof Photo) {
            PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(((Photo) media).sizes, AndroidUtilities.getPhotoSize());
            if (sizeFull != null) {
                size[0] = sizeFull.size;
                if (size[0] != 0) {
                    return sizeFull;
                }
                size[0] = -1;
                return sizeFull;
            }
            size[0] = -1;
        } else if (media instanceof Document) {
            PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(((Document) media).thumbs, 90);
            if (thumb != null) {
                size[0] = thumb.size;
                if (size[0] == 0) {
                    size[0] = -1;
                }
                return thumb;
            }
        }
        return null;
    }

    private void onPhotoShow(int index, PlaceProviderObject object) {
        BitmapHolder bitmapHolder;
        this.currentIndex = -1;
        this.currentFileNames[0] = null;
        this.currentFileNames[1] = null;
        this.currentFileNames[2] = null;
        if (this.currentThumb != null) {
            this.currentThumb.release();
        }
        if (object != null) {
            bitmapHolder = object.thumb;
        } else {
            bitmapHolder = null;
        }
        this.currentThumb = bitmapHolder;
        this.menuItem.setVisibility(0);
        this.menuItem.hideSubItem(3);
        this.actionBar.setTranslationY(0.0f);
        this.captionTextView.setTag(null);
        this.captionTextView.setVisibility(8);
        for (int a = 0; a < 3; a++) {
            if (this.radialProgressViews[a] != null) {
                this.radialProgressViews[a].setBackgroundState(-1, false);
            }
        }
        setImageIndex(index, true);
        if (this.currentMedia != null && isMediaVideo(this.currentIndex)) {
            onActionClick(false);
        }
    }

    private void setImages() {
        if (this.photoAnimationInProgress == 0) {
            setIndexToImage(this.centerImage, this.currentIndex);
            setIndexToImage(this.rightImage, this.currentIndex + 1);
            setIndexToImage(this.leftImage, this.currentIndex - 1);
        }
    }

    private void setImageIndex(int index, boolean init) {
        if (this.currentIndex != index) {
            int a;
            if (!(init || this.currentThumb == null)) {
                this.currentThumb.release();
                this.currentThumb = null;
            }
            this.currentFileNames[0] = getFileName(index);
            this.currentFileNames[1] = getFileName(index + 1);
            this.currentFileNames[2] = getFileName(index - 1);
            int prevIndex = this.currentIndex;
            this.currentIndex = index;
            boolean isVideo = false;
            boolean sameImage = false;
            if (!this.imagesArr.isEmpty()) {
                if (this.currentIndex < 0 || this.currentIndex >= this.imagesArr.size()) {
                    closePhoto(false);
                    return;
                }
                PageBlock newMedia = (PageBlock) this.imagesArr.get(this.currentIndex);
                sameImage = this.currentMedia != null && this.currentMedia == newMedia;
                this.currentMedia = newMedia;
                isVideo = isMediaVideo(this.currentIndex);
                if (isVideo) {
                    this.menuItem.showSubItem(3);
                }
                CharSequence captionToSet = null;
                boolean setAsIs = false;
                if (newMedia instanceof TL_pageBlockPhoto) {
                    String url = ((TL_pageBlockPhoto) newMedia).url;
                    if (!TextUtils.isEmpty(url)) {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(url);
                        spannableStringBuilder.setSpan(new URLSpan(url) {
                            public void onClick(View widget) {
                                ArticleViewer.this.openWebpageUrl(getURL(), null);
                            }
                        }, 0, url.length(), 34);
                        captionToSet = spannableStringBuilder;
                        setAsIs = true;
                    }
                }
                if (captionToSet == null) {
                    RichText caption = getBlockCaption(this.currentMedia, 2);
                    captionToSet = getText(null, caption, caption, this.currentMedia, -AndroidUtilities.dp(100.0f));
                }
                setCurrentCaption(captionToSet, setAsIs);
                if (this.currentAnimation != null) {
                    this.menuItem.setVisibility(8);
                    this.menuItem.hideSubItem(1);
                    this.actionBar.setTitle(LocaleController.getString("AttachGif", R.string.AttachGif));
                } else {
                    this.menuItem.setVisibility(0);
                    if (this.imagesArr.size() != 1) {
                        this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.imagesArr.size())));
                    } else if (isVideo) {
                        this.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
                    } else {
                        this.actionBar.setTitle(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
                    }
                    this.menuItem.showSubItem(1);
                }
                this.groupedPhotosListView.fillList();
            }
            int count = this.listView[0].getChildCount();
            for (a = 0; a < count; a++) {
                View child = this.listView[0].getChildAt(a);
                if (child instanceof BlockSlideshowCell) {
                    BlockSlideshowCell cell = (BlockSlideshowCell) child;
                    int idx = cell.currentBlock.items.indexOf(this.currentMedia);
                    if (idx != -1) {
                        cell.innerListView.setCurrentItem(idx, false);
                        break;
                    }
                }
            }
            if (this.currentPlaceObject != null) {
                if (this.photoAnimationInProgress == 0) {
                    this.currentPlaceObject.imageReceiver.setVisible(true, true);
                } else {
                    this.showAfterAnimation = this.currentPlaceObject;
                }
            }
            this.currentPlaceObject = getPlaceForPhoto(this.currentMedia);
            if (this.currentPlaceObject != null) {
                if (this.photoAnimationInProgress == 0) {
                    this.currentPlaceObject.imageReceiver.setVisible(false, true);
                } else {
                    this.hideAfterAnimation = this.currentPlaceObject;
                }
            }
            if (!sameImage) {
                this.draggingDown = false;
                this.translationX = 0.0f;
                this.translationY = 0.0f;
                this.scale = 1.0f;
                this.animateToX = 0.0f;
                this.animateToY = 0.0f;
                this.animateToScale = 1.0f;
                this.animationStartTime = 0;
                this.imageMoveAnimation = null;
                if (this.aspectRatioFrameLayout != null) {
                    this.aspectRatioFrameLayout.setVisibility(4);
                }
                releasePlayer();
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
                this.doubleTap = false;
                this.invalidCoords = false;
                this.canDragDown = true;
                this.changingPage = false;
                this.switchImageAfterAnimation = 0;
                boolean z = (this.currentFileNames[0] == null || isVideo || this.radialProgressViews[0].backgroundState == 0) ? false : true;
                this.canZoom = z;
                updateMinMax(this.scale);
            }
            if (prevIndex == -1) {
                setImages();
                for (a = 0; a < 3; a++) {
                    checkProgress(a, false);
                }
                return;
            }
            checkProgress(0, false);
            ImageReceiver temp;
            RadialProgressView tempProgress;
            if (prevIndex > this.currentIndex) {
                temp = this.rightImage;
                this.rightImage = this.centerImage;
                this.centerImage = this.leftImage;
                this.leftImage = temp;
                tempProgress = this.radialProgressViews[0];
                this.radialProgressViews[0] = this.radialProgressViews[2];
                this.radialProgressViews[2] = tempProgress;
                setIndexToImage(this.leftImage, this.currentIndex - 1);
                checkProgress(1, false);
                checkProgress(2, false);
            } else if (prevIndex < this.currentIndex) {
                temp = this.leftImage;
                this.leftImage = this.centerImage;
                this.centerImage = this.rightImage;
                this.rightImage = temp;
                tempProgress = this.radialProgressViews[0];
                this.radialProgressViews[0] = this.radialProgressViews[1];
                this.radialProgressViews[1] = tempProgress;
                setIndexToImage(this.rightImage, this.currentIndex + 1);
                checkProgress(1, false);
                checkProgress(2, false);
            }
        }
    }

    private void setCurrentCaption(CharSequence caption, boolean setAsIs) {
        if (TextUtils.isEmpty(caption)) {
            this.captionTextView.setTag(null);
            this.captionTextView.setVisibility(8);
            return;
        }
        CharSequence result;
        Theme.createChatResources(null, true);
        if (setAsIs) {
            result = caption;
        } else if (caption instanceof Spannable) {
            Spannable spannable = (Spannable) caption;
            TextPaintUrlSpan[] spans = (TextPaintUrlSpan[]) spannable.getSpans(0, caption.length(), TextPaintUrlSpan.class);
            SpannableStringBuilder builder = new SpannableStringBuilder(caption.toString());
            Object result2 = builder;
            if (spans != null && spans.length > 0) {
                for (int a = 0; a < spans.length; a++) {
                    builder.setSpan(new URLSpan(spans[a].getUrl()) {
                        public void onClick(View widget) {
                            ArticleViewer.this.openWebpageUrl(getURL(), null);
                        }
                    }, spannable.getSpanStart(spans[a]), spannable.getSpanEnd(spans[a]), 33);
                }
            }
        } else {
            result2 = new SpannableStringBuilder(caption.toString());
        }
        CharSequence str = Emoji.replaceEmoji(result2, this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        this.captionTextView.setTag(str);
        this.captionTextView.setText(str);
        this.captionTextView.setVisibility(0);
    }

    private void checkProgress(int a, boolean animated) {
        boolean z = true;
        if (this.currentFileNames[a] != null) {
            int index = this.currentIndex;
            if (a == 1) {
                index++;
            } else if (a == 2) {
                index--;
            }
            File f = getMediaFile(index);
            boolean isVideo = isMediaVideo(index);
            if (f == null || !f.exists()) {
                if (!isVideo) {
                    this.radialProgressViews[a].setBackgroundState(0, animated);
                } else if (FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[a])) {
                    this.radialProgressViews[a].setBackgroundState(1, false);
                } else {
                    this.radialProgressViews[a].setBackgroundState(2, false);
                }
                Float progress = ImageLoader.getInstance().getFileProgress(this.currentFileNames[a]);
                if (progress == null) {
                    progress = Float.valueOf(0.0f);
                }
                this.radialProgressViews[a].setProgress(progress.floatValue(), false);
            } else if (isVideo) {
                this.radialProgressViews[a].setBackgroundState(3, animated);
            } else {
                this.radialProgressViews[a].setBackgroundState(-1, animated);
            }
            if (a == 0) {
                if (this.currentFileNames[0] == null || isVideo || this.radialProgressViews[0].backgroundState == 0) {
                    z = false;
                }
                this.canZoom = z;
                return;
            }
            return;
        }
        this.radialProgressViews[a].setBackgroundState(-1, animated);
    }

    private void setIndexToImage(ImageReceiver imageReceiver, int index) {
        imageReceiver.setOrientation(0, false);
        int[] size = new int[1];
        PhotoSize fileLocation = getFileLocation(index, size);
        if (fileLocation != null) {
            TLObject media = getMedia(index);
            BitmapHolder placeHolder;
            if (media instanceof Photo) {
                Drawable bitmapDrawable;
                Photo photo = (Photo) media;
                placeHolder = null;
                if (this.currentThumb != null && imageReceiver == this.centerImage) {
                    placeHolder = this.currentThumb;
                }
                if (size[0] == 0) {
                    size[0] = -1;
                }
                PhotoSize thumbLocation = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80);
                if (placeHolder != null) {
                    bitmapDrawable = new BitmapDrawable(placeHolder.bitmap);
                } else {
                    bitmapDrawable = null;
                }
                imageReceiver.setImage(fileLocation, null, bitmapDrawable, thumbLocation, "b", size[0], null, this.currentPage, 1);
            } else if (isMediaVideo(index)) {
                if (fileLocation.location instanceof TL_fileLocationUnavailable) {
                    imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
                    return;
                }
                placeHolder = null;
                if (this.currentThumb != null && imageReceiver == this.centerImage) {
                    placeHolder = this.currentThumb;
                }
                imageReceiver.setImage(null, null, placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null, fileLocation, "b", 0, null, this.currentPage, 1);
            } else if (this.currentAnimation != null) {
                imageReceiver.setImageBitmap(this.currentAnimation);
                this.currentAnimation.setSecondParentView(this.photoContainerView);
            }
        } else if (size[0] == 0) {
            imageReceiver.setImageBitmap((Bitmap) null);
        } else {
            imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
        }
    }

    public boolean isShowingImage(PageBlock object) {
        return this.isPhotoVisible && !this.disableShowCheck && object != null && this.currentMedia == object;
    }

    private boolean checkPhotoAnimation() {
        if (this.photoAnimationInProgress != 0 && Math.abs(this.photoTransitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            if (this.photoAnimationEndRunnable != null) {
                this.photoAnimationEndRunnable.run();
                this.photoAnimationEndRunnable = null;
            }
            this.photoAnimationInProgress = 0;
        }
        if (this.photoAnimationInProgress != 0) {
            return true;
        }
        return false;
    }

    public boolean openPhoto(PageBlock block) {
        if (this.pageSwitchAnimation != null || this.parentActivity == null || this.isPhotoVisible || checkPhotoAnimation() || block == null) {
            return false;
        }
        PlaceProviderObject object = getPlaceForPhoto(block);
        if (object == null) {
            return false;
        }
        float scale;
        int clipHorizontal;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailedLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileLoadProgressChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.isPhotoVisible = true;
        toggleActionBar(true, false);
        this.actionBar.setAlpha(0.0f);
        this.bottomLayout.setAlpha(0.0f);
        this.captionTextView.setAlpha(0.0f);
        this.photoBackgroundDrawable.setAlpha(0);
        this.groupedPhotosListView.setAlpha(0.0f);
        this.photoContainerView.setAlpha(1.0f);
        this.disableShowCheck = true;
        this.photoAnimationInProgress = 1;
        if (block != null) {
            this.currentAnimation = object.imageReceiver.getAnimation();
        }
        int index = this.adapter[0].photoBlocks.indexOf(block);
        this.imagesArr.clear();
        if (!(block instanceof TL_pageBlockVideo) || isVideoBlock(block)) {
            this.imagesArr.addAll(this.adapter[0].photoBlocks);
        } else {
            this.imagesArr.add(block);
            index = 0;
        }
        onPhotoShow(index, object);
        Rect drawRegion = object.imageReceiver.getDrawRegion();
        int orientation = object.imageReceiver.getOrientation();
        int animatedOrientation = object.imageReceiver.getAnimatedOrientation();
        if (animatedOrientation != 0) {
            orientation = animatedOrientation;
        }
        this.animatingImageView.setVisibility(0);
        this.animatingImageView.setRadius(object.radius);
        this.animatingImageView.setOrientation(orientation);
        this.animatingImageView.setNeedRadius(object.radius != 0);
        this.animatingImageView.setImageBitmap(object.thumb);
        this.animatingImageView.setAlpha(1.0f);
        this.animatingImageView.setPivotX(0.0f);
        this.animatingImageView.setPivotY(0.0f);
        this.animatingImageView.setScaleX(object.scale);
        this.animatingImageView.setScaleY(object.scale);
        this.animatingImageView.setTranslationX(((float) object.viewX) + (((float) drawRegion.left) * object.scale));
        this.animatingImageView.setTranslationY(((float) object.viewY) + (((float) drawRegion.top) * object.scale));
        ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
        layoutParams.width = drawRegion.right - drawRegion.left;
        layoutParams.height = drawRegion.bottom - drawRegion.top;
        this.animatingImageView.setLayoutParams(layoutParams);
        float scaleX = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
        float scaleY = ((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) / ((float) layoutParams.height);
        if (scaleX > scaleY) {
            scale = scaleY;
        } else {
            scale = scaleX;
        }
        float height = ((float) layoutParams.height) * scale;
        float xPos = (((float) AndroidUtilities.displaySize.x) - (((float) layoutParams.width) * scale)) / 2.0f;
        if (VERSION.SDK_INT >= 21 && this.lastInsets != null) {
            xPos += (float) ((WindowInsets) this.lastInsets).getSystemWindowInsetLeft();
        }
        float yPos = (((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) - height) / 2.0f;
        if (object.imageReceiver.isAspectFit()) {
            clipHorizontal = 0;
        } else {
            clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
        }
        int clipVertical = Math.abs(drawRegion.top - object.imageReceiver.getImageY());
        int[] coords2 = new int[2];
        object.parentView.getLocationInWindow(coords2);
        int clipTop = (coords2[1] - (object.viewY + drawRegion.top)) + object.clipTopAddition;
        if (clipTop < 0) {
            clipTop = 0;
        }
        int clipBottom = (((object.viewY + drawRegion.top) + layoutParams.height) - (coords2[1] + object.parentView.getHeight())) + object.clipBottomAddition;
        if (clipBottom < 0) {
            clipBottom = 0;
        }
        clipTop = Math.max(clipTop, clipVertical);
        clipBottom = Math.max(clipBottom, clipVertical);
        this.animationValues[0][0] = this.animatingImageView.getScaleX();
        this.animationValues[0][1] = this.animatingImageView.getScaleY();
        this.animationValues[0][2] = this.animatingImageView.getTranslationX();
        this.animationValues[0][3] = this.animatingImageView.getTranslationY();
        this.animationValues[0][4] = ((float) clipHorizontal) * object.scale;
        this.animationValues[0][5] = ((float) clipTop) * object.scale;
        this.animationValues[0][6] = ((float) clipBottom) * object.scale;
        this.animationValues[0][7] = (float) this.animatingImageView.getRadius();
        this.animationValues[0][8] = ((float) clipVertical) * object.scale;
        this.animationValues[0][9] = ((float) clipHorizontal) * object.scale;
        this.animationValues[1][0] = scale;
        this.animationValues[1][1] = scale;
        this.animationValues[1][2] = xPos;
        this.animationValues[1][3] = yPos;
        this.animationValues[1][4] = 0.0f;
        this.animationValues[1][5] = 0.0f;
        this.animationValues[1][6] = 0.0f;
        this.animationValues[1][7] = 0.0f;
        this.animationValues[1][8] = 0.0f;
        this.animationValues[1][9] = 0.0f;
        this.photoContainerView.setVisibility(0);
        this.photoContainerBackground.setVisibility(0);
        this.animatingImageView.setAnimationProgress(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        r23 = new Animator[6];
        float[] fArr = new float[2];
        r23[0] = ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f});
        int[] iArr = new int[2];
        r23[1] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0, 255});
        fArr = new float[2];
        r23[2] = ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f, 1.0f});
        fArr = new float[2];
        r23[3] = ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f, 1.0f});
        fArr = new float[2];
        r23[4] = ObjectAnimator.ofFloat(this.captionTextView, View.ALPHA, new float[]{0.0f, 1.0f});
        fArr = new float[2];
        r23[5] = ObjectAnimator.ofFloat(this.groupedPhotosListView, View.ALPHA, new float[]{0.0f, 1.0f});
        animatorSet.playTogether(r23);
        this.photoAnimationEndRunnable = new ArticleViewer$$Lambda$29(this);
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                AndroidUtilities.runOnUIThread(new ArticleViewer$23$$Lambda$0(this));
            }

            final /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$23() {
                NotificationCenter.getInstance(ArticleViewer.this.currentAccount).setAnimationInProgress(false);
                if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                    ArticleViewer.this.photoAnimationEndRunnable.run();
                    ArticleViewer.this.photoAnimationEndRunnable = null;
                }
            }
        });
        this.photoTransitionAnimationStartTime = System.currentTimeMillis();
        AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$30(this, animatorSet));
        if (VERSION.SDK_INT >= 18) {
            this.photoContainerView.setLayerType(2, null);
        }
        this.photoBackgroundDrawable.drawRunnable = new ArticleViewer$$Lambda$31(this, object);
        return true;
    }

    final /* synthetic */ void lambda$openPhoto$37$ArticleViewer() {
        if (this.photoContainerView != null) {
            if (VERSION.SDK_INT >= 18) {
                this.photoContainerView.setLayerType(0, null);
            }
            this.photoAnimationInProgress = 0;
            this.photoTransitionAnimationStartTime = 0;
            setImages();
            this.photoContainerView.invalidate();
            this.animatingImageView.setVisibility(8);
            if (this.showAfterAnimation != null) {
                this.showAfterAnimation.imageReceiver.setVisible(true, true);
            }
            if (this.hideAfterAnimation != null) {
                this.hideAfterAnimation.imageReceiver.setVisible(false, true);
            }
        }
    }

    final /* synthetic */ void lambda$openPhoto$38$ArticleViewer(AnimatorSet animatorSet) {
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        animatorSet.start();
    }

    final /* synthetic */ void lambda$openPhoto$39$ArticleViewer(PlaceProviderObject object) {
        this.disableShowCheck = false;
        object.imageReceiver.setVisible(false, true);
    }

    public void closePhoto(boolean animated) {
        if (this.parentActivity != null && this.isPhotoVisible && !checkPhotoAnimation()) {
            releasePlayer();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailedLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
            this.isActionBarVisible = false;
            if (this.velocityTracker != null) {
                this.velocityTracker.recycle();
                this.velocityTracker = null;
            }
            PlaceProviderObject object = getPlaceForPhoto(this.currentMedia);
            if (animated) {
                float scale2;
                this.photoAnimationInProgress = 1;
                this.animatingImageView.setVisibility(0);
                this.photoContainerView.invalidate();
                AnimatorSet animatorSet = new AnimatorSet();
                ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
                Rect drawRegion = null;
                int orientation = this.centerImage.getOrientation();
                int animatedOrientation = 0;
                if (!(object == null || object.imageReceiver == null)) {
                    animatedOrientation = object.imageReceiver.getAnimatedOrientation();
                }
                if (animatedOrientation != 0) {
                    orientation = animatedOrientation;
                }
                this.animatingImageView.setOrientation(orientation);
                if (object != null) {
                    this.animatingImageView.setNeedRadius(object.radius != 0);
                    drawRegion = object.imageReceiver.getDrawRegion();
                    layoutParams.width = drawRegion.right - drawRegion.left;
                    layoutParams.height = drawRegion.bottom - drawRegion.top;
                    this.animatingImageView.setImageBitmap(object.thumb);
                } else {
                    this.animatingImageView.setNeedRadius(false);
                    layoutParams.width = this.centerImage.getImageWidth();
                    layoutParams.height = this.centerImage.getImageHeight();
                    this.animatingImageView.setImageBitmap(this.centerImage.getBitmapSafe());
                }
                this.animatingImageView.setLayoutParams(layoutParams);
                float scaleX = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
                float scaleY = ((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) / ((float) layoutParams.height);
                if (scaleX > scaleY) {
                    scale2 = scaleY;
                } else {
                    scale2 = scaleX;
                }
                float height = (((float) layoutParams.height) * this.scale) * scale2;
                float xPos = (((float) AndroidUtilities.displaySize.x) - ((((float) layoutParams.width) * this.scale) * scale2)) / 2.0f;
                if (VERSION.SDK_INT >= 21 && this.lastInsets != null) {
                    xPos += (float) ((WindowInsets) this.lastInsets).getSystemWindowInsetLeft();
                }
                float yPos = (((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) - height) / 2.0f;
                this.animatingImageView.setTranslationX(this.translationX + xPos);
                this.animatingImageView.setTranslationY(this.translationY + yPos);
                this.animatingImageView.setScaleX(this.scale * scale2);
                this.animatingImageView.setScaleY(this.scale * scale2);
                if (object != null) {
                    int clipHorizontal;
                    object.imageReceiver.setVisible(false, true);
                    if (object.imageReceiver.isAspectFit()) {
                        clipHorizontal = 0;
                    } else {
                        clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
                    }
                    int clipVertical = Math.abs(drawRegion.top - object.imageReceiver.getImageY());
                    int[] coords2 = new int[2];
                    object.parentView.getLocationInWindow(coords2);
                    int clipTop = (coords2[1] - (object.viewY + drawRegion.top)) + object.clipTopAddition;
                    if (clipTop < 0) {
                        clipTop = 0;
                    }
                    int clipBottom = (((object.viewY + drawRegion.top) + (drawRegion.bottom - drawRegion.top)) - (coords2[1] + object.parentView.getHeight())) + object.clipBottomAddition;
                    if (clipBottom < 0) {
                        clipBottom = 0;
                    }
                    clipTop = Math.max(clipTop, clipVertical);
                    clipBottom = Math.max(clipBottom, clipVertical);
                    this.animationValues[0][0] = this.animatingImageView.getScaleX();
                    this.animationValues[0][1] = this.animatingImageView.getScaleY();
                    this.animationValues[0][2] = this.animatingImageView.getTranslationX();
                    this.animationValues[0][3] = this.animatingImageView.getTranslationY();
                    this.animationValues[0][4] = 0.0f;
                    this.animationValues[0][5] = 0.0f;
                    this.animationValues[0][6] = 0.0f;
                    this.animationValues[0][7] = 0.0f;
                    this.animationValues[0][8] = 0.0f;
                    this.animationValues[0][9] = 0.0f;
                    this.animationValues[1][0] = object.scale;
                    this.animationValues[1][1] = object.scale;
                    this.animationValues[1][2] = ((float) object.viewX) + (((float) drawRegion.left) * object.scale);
                    this.animationValues[1][3] = ((float) object.viewY) + (((float) drawRegion.top) * object.scale);
                    this.animationValues[1][4] = ((float) clipHorizontal) * object.scale;
                    this.animationValues[1][5] = ((float) clipTop) * object.scale;
                    this.animationValues[1][6] = ((float) clipBottom) * object.scale;
                    this.animationValues[1][7] = (float) object.radius;
                    this.animationValues[1][8] = ((float) clipVertical) * object.scale;
                    this.animationValues[1][9] = ((float) clipHorizontal) * object.scale;
                    r23 = new Animator[6];
                    float[] fArr = new float[2];
                    r23[0] = ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f});
                    r23[1] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0});
                    r23[2] = ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f});
                    r23[3] = ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f});
                    r23[4] = ObjectAnimator.ofFloat(this.captionTextView, View.ALPHA, new float[]{0.0f});
                    r23[5] = ObjectAnimator.ofFloat(this.groupedPhotosListView, View.ALPHA, new float[]{0.0f});
                    animatorSet.playTogether(r23);
                } else {
                    float f;
                    int h = AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight;
                    Animator[] animatorArr = new Animator[7];
                    animatorArr[0] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.animatingImageView, View.ALPHA, new float[]{0.0f});
                    ClippingImageView clippingImageView = this.animatingImageView;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr2 = new float[1];
                    if (this.translationY >= 0.0f) {
                        f = (float) h;
                    } else {
                        f = (float) (-h);
                    }
                    fArr2[0] = f;
                    animatorArr[2] = ObjectAnimator.ofFloat(clippingImageView, property, fArr2);
                    animatorArr[3] = ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f});
                    animatorArr[4] = ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f});
                    animatorArr[5] = ObjectAnimator.ofFloat(this.captionTextView, View.ALPHA, new float[]{0.0f});
                    animatorArr[6] = ObjectAnimator.ofFloat(this.groupedPhotosListView, View.ALPHA, new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                }
                this.photoAnimationEndRunnable = new ArticleViewer$$Lambda$32(this, object);
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AndroidUtilities.runOnUIThread(new ArticleViewer$24$$Lambda$0(this));
                    }

                    final /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$24() {
                        if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                            ArticleViewer.this.photoAnimationEndRunnable.run();
                            ArticleViewer.this.photoAnimationEndRunnable = null;
                        }
                    }
                });
                this.photoTransitionAnimationStartTime = System.currentTimeMillis();
                if (VERSION.SDK_INT >= 18) {
                    this.photoContainerView.setLayerType(2, null);
                }
                animatorSet.start();
            } else {
                this.photoContainerView.setVisibility(4);
                this.photoContainerBackground.setVisibility(4);
                this.photoAnimationInProgress = 0;
                onPhotoClosed(object);
                this.photoContainerView.setScaleX(1.0f);
                this.photoContainerView.setScaleY(1.0f);
            }
            if (this.currentAnimation != null) {
                this.currentAnimation.setSecondParentView(null);
                this.currentAnimation = null;
                this.centerImage.setImageBitmap((Drawable) null);
            }
        }
    }

    final /* synthetic */ void lambda$closePhoto$40$ArticleViewer(PlaceProviderObject object) {
        if (VERSION.SDK_INT >= 18) {
            this.photoContainerView.setLayerType(0, null);
        }
        this.photoContainerView.setVisibility(4);
        this.photoContainerBackground.setVisibility(4);
        this.photoAnimationInProgress = 0;
        onPhotoClosed(object);
    }

    private void onPhotoClosed(PlaceProviderObject object) {
        this.isPhotoVisible = false;
        this.disableShowCheck = true;
        this.currentMedia = null;
        if (this.currentThumb != null) {
            this.currentThumb.release();
            this.currentThumb = null;
        }
        if (this.currentAnimation != null) {
            this.currentAnimation.setSecondParentView(null);
            this.currentAnimation = null;
        }
        for (int a = 0; a < 3; a++) {
            if (this.radialProgressViews[a] != null) {
                this.radialProgressViews[a].setBackgroundState(-1, false);
            }
        }
        this.centerImage.setImageBitmap((Bitmap) null);
        this.leftImage.setImageBitmap((Bitmap) null);
        this.rightImage.setImageBitmap((Bitmap) null);
        this.photoContainerView.post(new ArticleViewer$$Lambda$33(this));
        this.disableShowCheck = false;
        if (object != null) {
            object.imageReceiver.setVisible(true, true);
        }
        this.groupedPhotosListView.clear();
    }

    final /* synthetic */ void lambda$onPhotoClosed$41$ArticleViewer() {
        this.animatingImageView.setImageBitmap(null);
    }

    public void onPause() {
        if (this.currentAnimation != null) {
            closePhoto(false);
        }
    }

    private void updateMinMax(float scale) {
        int maxW = ((int) ((((float) this.centerImage.getImageWidth()) * scale) - ((float) getContainerViewWidth()))) / 2;
        int maxH = ((int) ((((float) this.centerImage.getImageHeight()) * scale) - ((float) getContainerViewHeight()))) / 2;
        if (maxW > 0) {
            this.minX = (float) (-maxW);
            this.maxX = (float) maxW;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (maxH > 0) {
            this.minY = (float) (-maxH);
            this.maxY = (float) maxH;
            return;
        }
        this.maxY = 0.0f;
        this.minY = 0.0f;
    }

    private int getContainerViewWidth() {
        return this.photoContainerView.getWidth();
    }

    private int getContainerViewHeight() {
        return this.photoContainerView.getHeight();
    }

    private boolean processTouchEvent(MotionEvent ev) {
        if (this.photoAnimationInProgress != 0 || this.animationStartTime != 0) {
            return false;
        }
        if (ev.getPointerCount() == 1 && this.gestureDetector.onTouchEvent(ev) && this.doubleTap) {
            this.doubleTap = false;
            this.moving = false;
            this.zooming = false;
            checkMinMax(false);
            return true;
        }
        if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
            this.discardTap = false;
            if (!this.scroller.isFinished()) {
                this.scroller.abortAnimation();
            }
            if (!(this.draggingDown || this.changingPage)) {
                if (this.canZoom && ev.getPointerCount() == 2) {
                    this.pinchStartDistance = (float) Math.hypot((double) (ev.getX(1) - ev.getX(0)), (double) (ev.getY(1) - ev.getY(0)));
                    this.pinchStartScale = this.scale;
                    this.pinchCenterX = (ev.getX(0) + ev.getX(1)) / 2.0f;
                    this.pinchCenterY = (ev.getY(0) + ev.getY(1)) / 2.0f;
                    this.pinchStartX = this.translationX;
                    this.pinchStartY = this.translationY;
                    this.zooming = true;
                    this.moving = false;
                    if (this.velocityTracker != null) {
                        this.velocityTracker.clear();
                    }
                } else if (ev.getPointerCount() == 1) {
                    this.moveStartX = ev.getX();
                    float y = ev.getY();
                    this.moveStartY = y;
                    this.dragY = y;
                    this.draggingDown = false;
                    this.canDragDown = true;
                    if (this.velocityTracker != null) {
                        this.velocityTracker.clear();
                    }
                }
            }
        } else if (ev.getActionMasked() == 2) {
            if (this.canZoom && ev.getPointerCount() == 2 && !this.draggingDown && this.zooming && !this.changingPage) {
                this.discardTap = true;
                this.scale = (((float) Math.hypot((double) (ev.getX(1) - ev.getX(0)), (double) (ev.getY(1) - ev.getY(0)))) / this.pinchStartDistance) * this.pinchStartScale;
                this.translationX = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (this.scale / this.pinchStartScale));
                this.translationY = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (this.scale / this.pinchStartScale));
                updateMinMax(this.scale);
                this.photoContainerView.invalidate();
            } else if (ev.getPointerCount() == 1) {
                if (this.velocityTracker != null) {
                    this.velocityTracker.addMovement(ev);
                }
                float dx = Math.abs(ev.getX() - this.moveStartX);
                float dy = Math.abs(ev.getY() - this.dragY);
                if (dx > ((float) AndroidUtilities.dp(3.0f)) || dy > ((float) AndroidUtilities.dp(3.0f))) {
                    this.discardTap = true;
                }
                if (this.canDragDown && !this.draggingDown && this.scale == 1.0f && dy >= ((float) AndroidUtilities.dp(30.0f)) && dy / 2.0f > dx) {
                    this.draggingDown = true;
                    this.moving = false;
                    this.dragY = ev.getY();
                    if (this.isActionBarVisible) {
                        toggleActionBar(false, true);
                    }
                    return true;
                } else if (this.draggingDown) {
                    this.translationY = ev.getY() - this.dragY;
                    this.photoContainerView.invalidate();
                } else if (this.invalidCoords || this.animationStartTime != 0) {
                    this.invalidCoords = false;
                    this.moveStartX = ev.getX();
                    this.moveStartY = ev.getY();
                } else {
                    float moveDx = this.moveStartX - ev.getX();
                    float moveDy = this.moveStartY - ev.getY();
                    if (this.moving || ((this.scale == 1.0f && Math.abs(moveDy) + ((float) AndroidUtilities.dp(12.0f)) < Math.abs(moveDx)) || this.scale != 1.0f)) {
                        if (!this.moving) {
                            moveDx = 0.0f;
                            moveDy = 0.0f;
                            this.moving = true;
                            this.canDragDown = false;
                        }
                        this.moveStartX = ev.getX();
                        this.moveStartY = ev.getY();
                        updateMinMax(this.scale);
                        if ((this.translationX < this.minX && !this.rightImage.hasImageSet()) || (this.translationX > this.maxX && !this.leftImage.hasImageSet())) {
                            moveDx /= 3.0f;
                        }
                        if (this.maxY == 0.0f && this.minY == 0.0f) {
                            if (this.translationY - moveDy < this.minY) {
                                this.translationY = this.minY;
                                moveDy = 0.0f;
                            } else if (this.translationY - moveDy > this.maxY) {
                                this.translationY = this.maxY;
                                moveDy = 0.0f;
                            }
                        } else if (this.translationY < this.minY || this.translationY > this.maxY) {
                            moveDy /= 3.0f;
                        }
                        this.translationX -= moveDx;
                        if (this.scale != 1.0f) {
                            this.translationY -= moveDy;
                        }
                        this.photoContainerView.invalidate();
                    }
                }
            }
        } else if (ev.getActionMasked() == 3 || ev.getActionMasked() == 1 || ev.getActionMasked() == 6) {
            if (this.zooming) {
                this.invalidCoords = true;
                if (this.scale < 1.0f) {
                    updateMinMax(1.0f);
                    animateTo(1.0f, 0.0f, 0.0f, true);
                } else if (this.scale > 3.0f) {
                    float atx = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (3.0f / this.pinchStartScale));
                    float aty = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (3.0f / this.pinchStartScale));
                    updateMinMax(3.0f);
                    if (atx < this.minX) {
                        atx = this.minX;
                    } else if (atx > this.maxX) {
                        atx = this.maxX;
                    }
                    if (aty < this.minY) {
                        aty = this.minY;
                    } else if (aty > this.maxY) {
                        aty = this.maxY;
                    }
                    animateTo(3.0f, atx, aty, true);
                } else {
                    checkMinMax(true);
                }
                this.zooming = false;
            } else if (this.draggingDown) {
                if (Math.abs(this.dragY - ev.getY()) > ((float) getContainerViewHeight()) / 6.0f) {
                    closePhoto(true);
                } else {
                    animateTo(1.0f, 0.0f, 0.0f, false);
                }
                this.draggingDown = false;
            } else if (this.moving) {
                float moveToX = this.translationX;
                float moveToY = this.translationY;
                updateMinMax(this.scale);
                this.moving = false;
                this.canDragDown = true;
                float velocity = 0.0f;
                if (this.velocityTracker != null && this.scale == 1.0f) {
                    this.velocityTracker.computeCurrentVelocity(1000);
                    velocity = this.velocityTracker.getXVelocity();
                }
                if ((this.translationX < this.minX - ((float) (getContainerViewWidth() / 3)) || velocity < ((float) (-AndroidUtilities.dp(650.0f)))) && this.rightImage.hasImageSet()) {
                    goToNext();
                    return true;
                } else if ((this.translationX > this.maxX + ((float) (getContainerViewWidth() / 3)) || velocity > ((float) AndroidUtilities.dp(650.0f))) && this.leftImage.hasImageSet()) {
                    goToPrev();
                    return true;
                } else {
                    if (this.translationX < this.minX) {
                        moveToX = this.minX;
                    } else if (this.translationX > this.maxX) {
                        moveToX = this.maxX;
                    }
                    if (this.translationY < this.minY) {
                        moveToY = this.minY;
                    } else if (this.translationY > this.maxY) {
                        moveToY = this.maxY;
                    }
                    animateTo(this.scale, moveToX, moveToY, false);
                }
            }
        }
        return false;
    }

    private void checkMinMax(boolean zoom) {
        float moveToX = this.translationX;
        float moveToY = this.translationY;
        updateMinMax(this.scale);
        if (this.translationX < this.minX) {
            moveToX = this.minX;
        } else if (this.translationX > this.maxX) {
            moveToX = this.maxX;
        }
        if (this.translationY < this.minY) {
            moveToY = this.minY;
        } else if (this.translationY > this.maxY) {
            moveToY = this.maxY;
        }
        animateTo(this.scale, moveToX, moveToY, zoom);
    }

    private void goToNext() {
        float extra = 0.0f;
        if (this.scale != 1.0f) {
            extra = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale;
        }
        this.switchImageAfterAnimation = 1;
        animateTo(this.scale, ((this.minX - ((float) getContainerViewWidth())) - extra) - ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
    }

    private void goToPrev() {
        float extra = 0.0f;
        if (this.scale != 1.0f) {
            extra = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale;
        }
        this.switchImageAfterAnimation = 2;
        animateTo(this.scale, ((this.maxX + ((float) getContainerViewWidth())) + extra) + ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom) {
        animateTo(newScale, newTx, newTy, isZoom, 250);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom, int duration) {
        if (this.scale != newScale || this.translationX != newTx || this.translationY != newTy) {
            this.zoomAnimation = isZoom;
            this.animateToScale = newScale;
            this.animateToX = newTx;
            this.animateToY = newTy;
            this.animationStartTime = System.currentTimeMillis();
            this.imageMoveAnimation = new AnimatorSet();
            this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f})});
            this.imageMoveAnimation.setInterpolator(this.interpolator);
            this.imageMoveAnimation.setDuration((long) duration);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ArticleViewer.this.imageMoveAnimation = null;
                    ArticleViewer.this.photoContainerView.invalidate();
                }
            });
            this.imageMoveAnimation.start();
        }
    }

    @Keep
    public void setAnimationValue(float value) {
        this.animationValue = value;
        this.photoContainerView.invalidate();
    }

    @Keep
    public float getAnimationValue() {
        return this.animationValue;
    }

    private void drawContent(Canvas canvas) {
        if (this.photoAnimationInProgress == 1) {
            return;
        }
        if (this.isPhotoVisible || this.photoAnimationInProgress == 2) {
            float currentScale;
            float currentTranslationY;
            float currentTranslationX;
            float scaleDiff;
            float alpha;
            int bitmapWidth;
            int bitmapHeight;
            float scaleX;
            float scaleY;
            float scale;
            int width;
            int height;
            float aty = -1.0f;
            if (this.imageMoveAnimation != null) {
                if (!this.scroller.isFinished()) {
                    this.scroller.abortAnimation();
                }
                float ts = this.scale + ((this.animateToScale - this.scale) * this.animationValue);
                float tx = this.translationX + ((this.animateToX - this.translationX) * this.animationValue);
                float ty = this.translationY + ((this.animateToY - this.translationY) * this.animationValue);
                if (this.animateToScale == 1.0f && this.scale == 1.0f && this.translationX == 0.0f) {
                    aty = ty;
                }
                currentScale = ts;
                currentTranslationY = ty;
                currentTranslationX = tx;
                this.photoContainerView.invalidate();
            } else {
                if (this.animationStartTime != 0) {
                    this.translationX = this.animateToX;
                    this.translationY = this.animateToY;
                    this.scale = this.animateToScale;
                    this.animationStartTime = 0;
                    updateMinMax(this.scale);
                    this.zoomAnimation = false;
                }
                if (!this.scroller.isFinished() && this.scroller.computeScrollOffset()) {
                    if (((float) this.scroller.getStartX()) < this.maxX && ((float) this.scroller.getStartX()) > this.minX) {
                        this.translationX = (float) this.scroller.getCurrX();
                    }
                    if (((float) this.scroller.getStartY()) < this.maxY && ((float) this.scroller.getStartY()) > this.minY) {
                        this.translationY = (float) this.scroller.getCurrY();
                    }
                    this.photoContainerView.invalidate();
                }
                if (this.switchImageAfterAnimation != 0) {
                    if (this.switchImageAfterAnimation == 1) {
                        AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$34(this));
                    } else if (this.switchImageAfterAnimation == 2) {
                        AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$35(this));
                    }
                    this.switchImageAfterAnimation = 0;
                }
                currentScale = this.scale;
                currentTranslationY = this.translationY;
                currentTranslationX = this.translationX;
                if (!this.moving) {
                    aty = this.translationY;
                }
            }
            if (this.photoAnimationInProgress != 2) {
                if (this.scale != 1.0f || aty == -1.0f || this.zoomAnimation) {
                    this.photoBackgroundDrawable.setAlpha(255);
                } else {
                    float maxValue = ((float) getContainerViewHeight()) / 4.0f;
                    this.photoBackgroundDrawable.setAlpha((int) Math.max(127.0f, 255.0f * (1.0f - (Math.min(Math.abs(aty), maxValue) / maxValue))));
                }
            }
            ImageReceiver sideImage = null;
            if (!(this.scale < 1.0f || this.zoomAnimation || this.zooming)) {
                if (currentTranslationX > this.maxX + ((float) AndroidUtilities.dp(5.0f))) {
                    sideImage = this.leftImage;
                } else if (currentTranslationX < this.minX - ((float) AndroidUtilities.dp(5.0f))) {
                    sideImage = this.rightImage;
                } else {
                    this.groupedPhotosListView.setMoveProgress(0.0f);
                }
            }
            this.changingPage = sideImage != null;
            if (sideImage == this.rightImage) {
                float tranlateX = currentTranslationX;
                scaleDiff = 0.0f;
                alpha = 1.0f;
                if (!this.zoomAnimation && tranlateX < this.minX) {
                    alpha = Math.min(1.0f, (this.minX - tranlateX) / ((float) canvas.getWidth()));
                    scaleDiff = (1.0f - alpha) * 0.3f;
                    tranlateX = (float) ((-canvas.getWidth()) - (AndroidUtilities.dp(30.0f) / 2));
                }
                if (sideImage.hasBitmapImage()) {
                    canvas.save();
                    canvas.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                    canvas.translate(((float) (canvas.getWidth() + (AndroidUtilities.dp(30.0f) / 2))) + tranlateX, 0.0f);
                    canvas.scale(1.0f - scaleDiff, 1.0f - scaleDiff);
                    bitmapWidth = sideImage.getBitmapWidth();
                    bitmapHeight = sideImage.getBitmapHeight();
                    scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                    scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                    if (scaleX > scaleY) {
                        scale = scaleY;
                    } else {
                        scale = scaleX;
                    }
                    width = (int) (((float) bitmapWidth) * scale);
                    height = (int) (((float) bitmapHeight) * scale);
                    sideImage.setAlpha(alpha);
                    sideImage.setImageCoords((-width) / 2, (-height) / 2, width, height);
                    sideImage.draw(canvas);
                    canvas.restore();
                }
                this.groupedPhotosListView.setMoveProgress(-alpha);
                canvas.save();
                canvas.translate(tranlateX, currentTranslationY / currentScale);
                canvas.translate(((((float) canvas.getWidth()) * (this.scale + 1.0f)) + ((float) AndroidUtilities.dp(30.0f))) / 2.0f, (-currentTranslationY) / currentScale);
                this.radialProgressViews[1].setScale(1.0f - scaleDiff);
                this.radialProgressViews[1].setAlpha(alpha);
                this.radialProgressViews[1].onDraw(canvas);
                canvas.restore();
            }
            float translateX = currentTranslationX;
            scaleDiff = 0.0f;
            alpha = 1.0f;
            if (!this.zoomAnimation && translateX > this.maxX) {
                alpha = Math.min(1.0f, (translateX - this.maxX) / ((float) canvas.getWidth()));
                scaleDiff = alpha * 0.3f;
                alpha = 1.0f - alpha;
                translateX = this.maxX;
            }
            boolean drawTextureView = this.aspectRatioFrameLayout != null && this.aspectRatioFrameLayout.getVisibility() == 0;
            if (this.centerImage.hasBitmapImage()) {
                canvas.save();
                canvas.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                canvas.translate(translateX, currentTranslationY);
                canvas.scale(currentScale - scaleDiff, currentScale - scaleDiff);
                bitmapWidth = this.centerImage.getBitmapWidth();
                bitmapHeight = this.centerImage.getBitmapHeight();
                if (drawTextureView && this.textureUploaded && Math.abs((((float) bitmapWidth) / ((float) bitmapHeight)) - (((float) this.videoTextureView.getMeasuredWidth()) / ((float) this.videoTextureView.getMeasuredHeight()))) > 0.01f) {
                    bitmapWidth = this.videoTextureView.getMeasuredWidth();
                    bitmapHeight = this.videoTextureView.getMeasuredHeight();
                }
                scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                if (scaleX > scaleY) {
                    scale = scaleY;
                } else {
                    scale = scaleX;
                }
                width = (int) (((float) bitmapWidth) * scale);
                height = (int) (((float) bitmapHeight) * scale);
                if (!(drawTextureView && this.textureUploaded && this.videoCrossfadeStarted && this.videoCrossfadeAlpha == 1.0f)) {
                    this.centerImage.setAlpha(alpha);
                    this.centerImage.setImageCoords((-width) / 2, (-height) / 2, width, height);
                    this.centerImage.draw(canvas);
                }
                if (drawTextureView) {
                    if (!this.videoCrossfadeStarted && this.textureUploaded) {
                        this.videoCrossfadeStarted = true;
                        this.videoCrossfadeAlpha = 0.0f;
                        this.videoCrossfadeAlphaLastTime = System.currentTimeMillis();
                    }
                    canvas.translate((float) ((-width) / 2), (float) ((-height) / 2));
                    this.videoTextureView.setAlpha(this.videoCrossfadeAlpha * alpha);
                    this.aspectRatioFrameLayout.draw(canvas);
                    if (this.videoCrossfadeStarted && this.videoCrossfadeAlpha < 1.0f) {
                        long newUpdateTime = System.currentTimeMillis();
                        long dt = newUpdateTime - this.videoCrossfadeAlphaLastTime;
                        this.videoCrossfadeAlphaLastTime = newUpdateTime;
                        this.videoCrossfadeAlpha += ((float) dt) / 300.0f;
                        this.photoContainerView.invalidate();
                        if (this.videoCrossfadeAlpha > 1.0f) {
                            this.videoCrossfadeAlpha = 1.0f;
                        }
                    }
                }
                canvas.restore();
            }
            if (!(drawTextureView || this.bottomLayout.getVisibility() == 0)) {
                canvas.save();
                canvas.translate(translateX, currentTranslationY / currentScale);
                this.radialProgressViews[0].setScale(1.0f - scaleDiff);
                this.radialProgressViews[0].setAlpha(alpha);
                this.radialProgressViews[0].onDraw(canvas);
                canvas.restore();
            }
            if (sideImage == this.leftImage) {
                if (sideImage.hasBitmapImage()) {
                    canvas.save();
                    canvas.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                    canvas.translate(((-((((float) canvas.getWidth()) * (this.scale + 1.0f)) + ((float) AndroidUtilities.dp(30.0f)))) / 2.0f) + currentTranslationX, 0.0f);
                    bitmapWidth = sideImage.getBitmapWidth();
                    bitmapHeight = sideImage.getBitmapHeight();
                    scaleX = ((float) getContainerViewWidth()) / ((float) bitmapWidth);
                    scaleY = ((float) getContainerViewHeight()) / ((float) bitmapHeight);
                    scale = scaleX > scaleY ? scaleY : scaleX;
                    width = (int) (((float) bitmapWidth) * scale);
                    height = (int) (((float) bitmapHeight) * scale);
                    sideImage.setAlpha(1.0f);
                    sideImage.setImageCoords((-width) / 2, (-height) / 2, width, height);
                    sideImage.draw(canvas);
                    canvas.restore();
                }
                this.groupedPhotosListView.setMoveProgress(1.0f - alpha);
                canvas.save();
                canvas.translate(currentTranslationX, currentTranslationY / currentScale);
                canvas.translate((-((((float) canvas.getWidth()) * (this.scale + 1.0f)) + ((float) AndroidUtilities.dp(30.0f)))) / 2.0f, (-currentTranslationY) / currentScale);
                this.radialProgressViews[2].setScale(1.0f);
                this.radialProgressViews[2].setAlpha(1.0f);
                this.radialProgressViews[2].onDraw(canvas);
                canvas.restore();
            }
        }
    }

    final /* synthetic */ void lambda$drawContent$42$ArticleViewer() {
        setImageIndex(this.currentIndex + 1, false);
    }

    final /* synthetic */ void lambda$drawContent$43$ArticleViewer() {
        setImageIndex(this.currentIndex - 1, false);
    }

    private void onActionClick(boolean download) {
        TLObject media = getMedia(this.currentIndex);
        if ((media instanceof Document) && this.currentFileNames[0] != null) {
            Document document = (Document) media;
            File file = null;
            if (this.currentMedia != null) {
                file = getMediaFile(this.currentIndex);
                if (!(file == null || file.exists())) {
                    file = null;
                }
            }
            if (file != null) {
                preparePlayer(file, true);
            } else if (!download) {
            } else {
                if (FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[0])) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(document);
                } else {
                    FileLoader.getInstance(this.currentAccount).loadFile(document, this.currentPage, 1, 1);
                }
            }
        }
    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (this.scale != 1.0f) {
            this.scroller.abortAnimation();
            this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(velocityX), Math.round(velocityY), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
            this.photoContainerView.postInvalidate();
        }
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        boolean z = false;
        if (this.discardTap) {
            return false;
        }
        boolean drawTextureView;
        if (this.aspectRatioFrameLayout == null || this.aspectRatioFrameLayout.getVisibility() != 0) {
            drawTextureView = false;
        } else {
            drawTextureView = true;
        }
        if (!(this.radialProgressViews[0] == null || this.photoContainerView == null || drawTextureView)) {
            int state = this.radialProgressViews[0].backgroundState;
            if (state > 0 && state <= 3) {
                float x = e.getX();
                float y = e.getY();
                if (x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                    onActionClick(true);
                    checkProgress(0, true);
                    return true;
                }
            }
        }
        if (!this.isActionBarVisible) {
            z = true;
        }
        toggleActionBar(z, true);
        return true;
    }

    public boolean onDoubleTap(MotionEvent e) {
        if (!this.canZoom || (this.scale == 1.0f && (this.translationY != 0.0f || this.translationX != 0.0f))) {
            return false;
        }
        if (this.animationStartTime != 0 || this.photoAnimationInProgress != 0) {
            return false;
        }
        if (this.scale == 1.0f) {
            float atx = (e.getX() - ((float) (getContainerViewWidth() / 2))) - (((e.getX() - ((float) (getContainerViewWidth() / 2))) - this.translationX) * (3.0f / this.scale));
            float aty = (e.getY() - ((float) (getContainerViewHeight() / 2))) - (((e.getY() - ((float) (getContainerViewHeight() / 2))) - this.translationY) * (3.0f / this.scale));
            updateMinMax(3.0f);
            if (atx < this.minX) {
                atx = this.minX;
            } else if (atx > this.maxX) {
                atx = this.maxX;
            }
            if (aty < this.minY) {
                aty = this.minY;
            } else if (aty > this.maxY) {
                aty = this.maxY;
            }
            animateTo(3.0f, atx, aty, true);
        } else {
            animateTo(1.0f, 0.0f, 0.0f, true);
        }
        this.doubleTap = true;
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    private ImageReceiver getImageReceiverView(View view, PageBlock pageBlock, int[] coords) {
        ImageReceiver imageReceiver;
        if (view instanceof BlockPhotoCell) {
            BlockPhotoCell cell = (BlockPhotoCell) view;
            if (cell.currentBlock == pageBlock) {
                view.getLocationInWindow(coords);
                return cell.imageView;
            }
        } else if (view instanceof BlockVideoCell) {
            BlockVideoCell cell2 = (BlockVideoCell) view;
            if (cell2.currentBlock == pageBlock) {
                view.getLocationInWindow(coords);
                return cell2.imageView;
            }
        } else if (view instanceof BlockCollageCell) {
            imageReceiver = getImageReceiverFromListView(((BlockCollageCell) view).innerListView, pageBlock, coords);
            if (imageReceiver != null) {
                return imageReceiver;
            }
        } else if (view instanceof BlockSlideshowCell) {
            imageReceiver = getImageReceiverFromListView(((BlockSlideshowCell) view).innerListView, pageBlock, coords);
            if (imageReceiver != null) {
                return imageReceiver;
            }
        } else if (view instanceof BlockListItemCell) {
            BlockListItemCell blockListItemCell = (BlockListItemCell) view;
            if (blockListItemCell.blockLayout != null) {
                imageReceiver = getImageReceiverView(blockListItemCell.blockLayout.itemView, pageBlock, coords);
                if (imageReceiver != null) {
                    return imageReceiver;
                }
            }
        } else if (view instanceof BlockOrderedListItemCell) {
            BlockOrderedListItemCell blockOrderedListItemCell = (BlockOrderedListItemCell) view;
            if (blockOrderedListItemCell.blockLayout != null) {
                imageReceiver = getImageReceiverView(blockOrderedListItemCell.blockLayout.itemView, pageBlock, coords);
                if (imageReceiver != null) {
                    return imageReceiver;
                }
            }
        }
        return null;
    }

    private ImageReceiver getImageReceiverFromListView(ViewGroup listView, PageBlock pageBlock, int[] coords) {
        int count = listView.getChildCount();
        for (int a = 0; a < count; a++) {
            ImageReceiver imageReceiver = getImageReceiverView(listView.getChildAt(a), pageBlock, coords);
            if (imageReceiver != null) {
                return imageReceiver;
            }
        }
        return null;
    }

    private PlaceProviderObject getPlaceForPhoto(PageBlock pageBlock) {
        ImageReceiver imageReceiver = getImageReceiverFromListView(this.listView[0], pageBlock, this.coords);
        if (imageReceiver == null) {
            return null;
        }
        PlaceProviderObject object = new PlaceProviderObject();
        object.viewX = this.coords[0];
        object.viewY = this.coords[1];
        object.parentView = this.listView[0];
        object.imageReceiver = imageReceiver;
        object.thumb = imageReceiver.getBitmapSafe();
        object.radius = imageReceiver.getRoundRadius();
        object.clipTopAddition = this.currentHeaderHeight;
        return object;
    }
}
