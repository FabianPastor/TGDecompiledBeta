package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Point;
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
import android.text.Editable;
import android.text.Layout.Alignment;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.MetricAffectingSpan;
import android.text.style.URLSpan;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.DisplayCutout;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityNodeInfo;
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
import androidx.annotation.Keep;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.GridLayoutManagerFixed;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.File;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessagePosition;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.PageBlock;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.RichText;
import org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPage;
import org.telegram.tgnet.TLRPC.TL_page;
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
import org.telegram.tgnet.TLRPC.TL_pageBlockVideo;
import org.telegram.tgnet.TLRPC.TL_pageCaption;
import org.telegram.tgnet.TLRPC.TL_pageRelatedArticle;
import org.telegram.tgnet.TLRPC.TL_pageTableCell;
import org.telegram.tgnet.TLRPC.TL_pageTableRow;
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
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegate;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView;
import org.telegram.ui.Cells.TextSelectionHelper.ArticleTextSelectionHelper;
import org.telegram.ui.Cells.TextSelectionHelper.IgnoreCopySpannable;
import org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock;
import org.telegram.ui.Cells.TextSelectionHelper.TextSelectionOverlay;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnchorSpan;
import org.telegram.ui.Components.AnimatedArrowDrawable;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AnimationProperties.FloatProperty;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.CloseProgressDrawable2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.GroupedPhotosListView;
import org.telegram.ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.Scroller;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;
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

public class ArticleViewer implements NotificationCenterDelegate, OnGestureListener, OnDoubleTapListener {
    public static final Property<WindowView, Float> ARTICLE_VIEWER_INNER_TRANSLATION_X = new FloatProperty<WindowView>("innerTranslationX") {
        public void setValue(WindowView windowView, float f) {
            windowView.setInnerTranslationX(f);
        }

        public Float get(WindowView windowView) {
            return Float.valueOf(windowView.getInnerTranslationX());
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
    private static TextPaint channelNamePhotoPaint = null;
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
    private static final int gallery_menu_savegif = 4;
    private static final int gallery_menu_share = 2;
    private static SparseArray<TextPaint> headerTextPaints = new SparseArray();
    private static SparseArray<TextPaint> kickerTextPaints = new SparseArray();
    private static TextPaint listTextNumPaint = null;
    private static SparseArray<TextPaint> listTextPaints = new SparseArray();
    private static TextPaint listTextPointerPaint = null;
    private static SparseArray<TextPaint> mediaCaptionTextPaints = new SparseArray();
    private static SparseArray<TextPaint> mediaCreditTextPaints = new SparseArray();
    private static final int open_item = 3;
    private static SparseArray<TextPaint> paragraphTextPaints = new SparseArray();
    private static Paint photoBackgroundPaint = null;
    private static SparseArray<TextPaint> photoCaptionTextPaints = new SparseArray();
    private static SparseArray<TextPaint> photoCreditTextPaints = new SparseArray();
    private static Paint preformattedBackgroundPaint = null;
    private static SparseArray<TextPaint> preformattedTextPaints = new SparseArray();
    private static Drawable[] progressDrawables = null;
    private static Paint progressPaint = null;
    private static Paint quoteLinePaint = null;
    private static SparseArray<TextPaint> quoteTextPaints = new SparseArray();
    private static TextPaint relatedArticleHeaderPaint = null;
    private static TextPaint relatedArticleTextPaint = null;
    private static SparseArray<TextPaint> relatedArticleTextPaints = new SparseArray();
    private static final int search_item = 1;
    private static final int settings_item = 4;
    private static final int share_item = 2;
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
    private static Paint webpageSearchPaint;
    private static Paint webpageUrlPaint;
    private final String BOTTOM_SHEET_VIEW_TAG = "bottomSheet";
    private ActionBar actionBar;
    private WebpageAdapter[] adapter;
    private int anchorsOffsetMeasuredWidth;
    private boolean animateClear = true;
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    private ClippingImageView animatingImageView;
    private Runnable animationEndRunnable;
    private int animationInProgress;
    private long animationStartTime;
    private float animationValue;
    private float[][] animationValues = ((float[][]) Array.newInstance(float.class, new int[]{2, 13}));
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
    private boolean checkingForLongPress = false;
    private ImageView clearButton;
    private boolean collapsed;
    private FrameLayout containerView;
    private int[] coords = new int[2];
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
    private int currentSearchIndex;
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
    private AspectRatioFrameLayout fullscreenAspectRatioView;
    private TextureView fullscreenTextureView;
    private FrameLayout fullscreenVideoContainer;
    private WebPlayerView fullscreenedVideo;
    private GestureDetector gestureDetector;
    private GroupedPhotosListView groupedPhotosListView;
    private boolean hasCutout;
    private Paint headerPaint = new Paint();
    private Paint headerProgressPaint = new Paint();
    private FrameLayout headerView;
    private PlaceProviderObject hideAfterAnimation;
    private boolean ignoreOnTextChange;
    private AnimatorSet imageMoveAnimation;
    private ArrayList<PageBlock> imagesArr = new ArrayList();
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private boolean invalidCoords;
    private boolean isActionBarVisible = true;
    private boolean isPhotoVisible;
    private boolean isPlaying;
    private boolean isVisible;
    private boolean keyboardVisible;
    private int lastBlockNum = 1;
    private Object lastInsets;
    private int lastReqId;
    private int lastSearchIndex = -1;
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
    private ActionBarMenuItem menuButton;
    private FrameLayout menuContainer;
    private ActionBarMenuItem menuItem;
    private float minX;
    private float minY;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
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
    private AnimatorSet runAfterKeyboardClose;
    private float scale = 1.0f;
    private Paint scrimPaint;
    private Scroller scroller;
    private FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ImageView searchDownButton;
    private EditTextBoldCursor searchField;
    private FrameLayout searchPanel;
    private ArrayList<SearchResult> searchResults = new ArrayList();
    private Runnable searchRunnable;
    private View searchShadow;
    private String searchText;
    private ImageView searchUpButton;
    private int selectedFont = 0;
    private PlaceProviderObject showAfterAnimation;
    private Drawable slideDotBigDrawable;
    private Drawable slideDotDrawable;
    private Paint statusBarPaint = new Paint();
    private int switchImageAfterAnimation;
    ArticleTextSelectionHelper textSelectionHelper;
    ArticleTextSelectionHelper textSelectionHelperBottomSheet;
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

    private class BlockDetailsBottomCell extends View {
        private RectF rect = new RectF();

        public BlockDetailsBottomCell(Context context) {
            super(context);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(4.0f) + 1);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            canvas.drawLine(0.0f, 0.0f, (float) getMeasuredWidth(), 0.0f, ArticleViewer.dividerPaint);
        }
    }

    private class BlockDividerCell extends View {
        private RectF rect = new RectF();

        public BlockDividerCell(Context context) {
            super(context);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(18.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            int measuredWidth = getMeasuredWidth() / 3;
            this.rect.set((float) measuredWidth, (float) AndroidUtilities.dp(8.0f), (float) (measuredWidth * 2), (float) AndroidUtilities.dp(10.0f));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), ArticleViewer.dividerPaint);
        }
    }

    private class BlockRelatedArticlesShadowCell extends View {
        private CombinedDrawable shadowDrawable;

        public BlockRelatedArticlesShadowCell(Context context) {
            super(context);
            this.shadowDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(context, NUM, -16777216));
            this.shadowDrawable.setFullsize(true);
            setBackgroundDrawable(this.shadowDrawable);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(12.0f));
            Theme.setCombinedDrawableColor(this.shadowDrawable, Theme.getColor("windowBackgroundGray"), false);
        }
    }

    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
        }

        public void run() {
            if (ArticleViewer.this.checkingForLongPress && ArticleViewer.this.windowView != null) {
                ArticleViewer.this.checkingForLongPress = false;
                ArticleViewer articleViewer;
                if (ArticleViewer.this.pressedLink != null) {
                    ArticleViewer.this.windowView.performHapticFeedback(0);
                    articleViewer = ArticleViewer.this;
                    articleViewer.showCopyPopup(articleViewer.pressedLink.getUrl());
                    ArticleViewer.this.pressedLink = null;
                    ArticleViewer.this.pressedLinkOwnerLayout = null;
                    if (ArticleViewer.this.pressedLinkOwnerView != null) {
                        ArticleViewer.this.pressedLinkOwnerView.invalidate();
                        return;
                    }
                    return;
                }
                if (ArticleViewer.this.pressedLinkOwnerView != null) {
                    articleViewer = ArticleViewer.this;
                    if (articleViewer.textSelectionHelper.isSelectable(articleViewer.pressedLinkOwnerView)) {
                        ArticleViewer.this.windowView.performHapticFeedback(0);
                        if (ArticleViewer.this.pressedLinkOwnerView.getTag() != null && ArticleViewer.this.pressedLinkOwnerView.getTag() == "bottomSheet") {
                            articleViewer = ArticleViewer.this;
                            ArticleTextSelectionHelper articleTextSelectionHelper = articleViewer.textSelectionHelperBottomSheet;
                            if (articleTextSelectionHelper != null) {
                                articleTextSelectionHelper.trySelect(articleViewer.pressedLinkOwnerView);
                                return;
                            }
                        }
                        articleViewer = ArticleViewer.this;
                        articleViewer.textSelectionHelper.trySelect(articleViewer.pressedLinkOwnerView);
                        return;
                    }
                }
                if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLinkOwnerView != null) {
                    ArticleViewer.this.windowView.performHapticFeedback(0);
                    int[] iArr = new int[2];
                    ArticleViewer.this.pressedLinkOwnerView.getLocationInWindow(iArr);
                    int access$6100 = (iArr[1] + ArticleViewer.this.pressedLayoutY) - AndroidUtilities.dp(54.0f);
                    if (access$6100 < 0) {
                        access$6100 = 0;
                    }
                    ArticleViewer.this.pressedLinkOwnerView.invalidate();
                    ArticleViewer.this.drawBlockSelection = true;
                    ArticleViewer articleViewer2 = ArticleViewer.this;
                    articleViewer2.showPopup(articleViewer2.pressedLinkOwnerView, 48, 0, access$6100);
                    ArticleViewer.this.listView[0].setLayoutFrozen(true);
                    ArticleViewer.this.listView[0].setLayoutFrozen(false);
                }
            }
        }
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        /* synthetic */ CheckForTap(ArticleViewer articleViewer, AnonymousClass1 anonymousClass1) {
            this();
        }

        public void run() {
            if (ArticleViewer.this.pendingCheckForLongPress == null) {
                ArticleViewer articleViewer = ArticleViewer.this;
                articleViewer.pendingCheckForLongPress = new CheckForLongPress();
            }
            ArticleViewer.this.pendingCheckForLongPress.currentPressCount = ArticleViewer.access$1904(ArticleViewer.this);
            if (ArticleViewer.this.windowView != null) {
                ArticleViewer.this.windowView.postDelayed(ArticleViewer.this.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
            }
        }
    }

    public class FontCell extends FrameLayout {
        private RadioButton radioButton;
        private TextView textView;

        public FontCell(Context context) {
            super(context);
            setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 2));
            this.radioButton = new RadioButton(context);
            this.radioButton.setSize(AndroidUtilities.dp(20.0f));
            this.radioButton.setColor(Theme.getColor("dialogRadioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            RadioButton radioButton = this.radioButton;
            int i = 5;
            int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i3 = 0;
            float f = (float) (LocaleController.isRTL ? 0 : 22);
            if (LocaleController.isRTL) {
                i3 = 22;
            }
            addView(radioButton, LayoutHelper.createFrame(22, 22.0f, i2, f, 13.0f, (float) i3, 0.0f));
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            TextView textView = this.textView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            i2 = i | 48;
            int i4 = 17;
            f = (float) (LocaleController.isRTL ? 17 : 62);
            if (LocaleController.isRTL) {
                i4 = 62;
            }
            addView(textView, LayoutHelper.createFrame(-1, -1.0f, i2, f, 0.0f, (float) i4, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }

        public void select(boolean z, boolean z2) {
            this.radioButton.setChecked(z, z2);
        }

        public void setTextAndTypeface(String str, Typeface typeface) {
            this.textView.setText(str);
            this.textView.setTypeface(typeface);
            invalidate();
        }
    }

    private class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            ArticleViewer.this.processTouchEvent(motionEvent);
            return true;
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            ArticleViewer.this.drawContent(canvas);
        }

        /* Access modifiers changed, original: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            return view != ArticleViewer.this.aspectRatioFrameLayout && super.drawChild(canvas, view, j);
        }
    }

    private class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        /* synthetic */ LinkMovementMethodMy(ArticleViewer articleViewer, AnonymousClass1 anonymousClass1) {
            this();
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    private class PhotoBackgroundDrawable extends ColorDrawable {
        private Runnable drawRunnable;

        public PhotoBackgroundDrawable(int i) {
            super(i);
        }

        @Keep
        public void setAlpha(int i) {
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                DrawerLayoutContainer drawerLayoutContainer = ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer;
                boolean z = (ArticleViewer.this.isPhotoVisible && i == 255) ? false : true;
                drawerLayoutContainer.setAllowDrawContent(z);
            }
            super.setAlpha(i);
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (getAlpha() != 0) {
                Runnable runnable = this.drawRunnable;
                if (runnable != null) {
                    runnable.run();
                    this.drawRunnable = null;
                }
            }
        }
    }

    public static class PlaceProviderObject {
        public int clipBottomAddition;
        public int clipTopAddition;
        public ImageReceiver imageReceiver;
        public int index;
        public View parentView;
        public int[] radius;
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

        public RadialProgressView(Context context, View view) {
            if (ArticleViewer.decelerateInterpolator == null) {
                ArticleViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                ArticleViewer.progressPaint = new Paint(1);
                ArticleViewer.progressPaint.setStyle(Style.STROKE);
                ArticleViewer.progressPaint.setStrokeCap(Cap.ROUND);
                ArticleViewer.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
                ArticleViewer.progressPaint.setColor(-1);
            }
            this.parent = view;
        }

        private void updateAnimation() {
            long currentTimeMillis = System.currentTimeMillis();
            long j = currentTimeMillis - this.lastUpdateTime;
            this.lastUpdateTime = currentTimeMillis;
            if (this.animatedProgressValue != 1.0f) {
                this.radOffset += ((float) (360 * j)) / 3000.0f;
                float f = this.currentProgress;
                float f2 = this.animationProgressStart;
                float f3 = f - f2;
                if (f3 > 0.0f) {
                    this.currentProgressTime += j;
                    if (this.currentProgressTime >= 300) {
                        this.animatedProgressValue = f;
                        this.animationProgressStart = f;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = f2 + (f3 * ArticleViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f));
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
            if (z) {
                this.animationProgressStart = this.animatedProgressValue;
            } else {
                this.animatedProgressValue = f;
                this.animationProgressStart = f;
            }
            this.currentProgress = f;
            this.currentProgressTime = 0;
        }

        public void setBackgroundState(int i, boolean z) {
            this.lastUpdateTime = System.currentTimeMillis();
            if (z) {
                int i2 = this.backgroundState;
                if (i2 != i) {
                    this.previousBackgroundState = i2;
                    this.animatedAlphaValue = 1.0f;
                    this.backgroundState = i;
                    this.parent.invalidate();
                }
            }
            this.previousBackgroundState = -2;
            this.backgroundState = i;
            this.parent.invalidate();
        }

        public void setAlpha(float f) {
            this.alpha = f;
        }

        public void setScale(float f) {
            this.scale = f;
        }

        public void onDraw(Canvas canvas) {
            Drawable drawable;
            int i = (int) (((float) this.size) * this.scale);
            int access$23700 = (ArticleViewer.this.getContainerViewWidth() - i) / 2;
            int access$23800 = (ArticleViewer.this.getContainerViewHeight() - i) / 2;
            int i2 = this.previousBackgroundState;
            if (i2 >= 0 && i2 < 4) {
                drawable = ArticleViewer.progressDrawables[this.previousBackgroundState];
                if (drawable != null) {
                    drawable.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                    drawable.setBounds(access$23700, access$23800, access$23700 + i, access$23800 + i);
                    drawable.draw(canvas);
                }
            }
            i2 = this.backgroundState;
            if (i2 >= 0 && i2 < 4) {
                drawable = ArticleViewer.progressDrawables[this.backgroundState];
                if (drawable != null) {
                    if (this.previousBackgroundState != -2) {
                        drawable.setAlpha((int) (((1.0f - this.animatedAlphaValue) * 255.0f) * this.alpha));
                    } else {
                        drawable.setAlpha((int) (this.alpha * 255.0f));
                    }
                    drawable.setBounds(access$23700, access$23800, access$23700 + i, access$23800 + i);
                    drawable.draw(canvas);
                }
            }
            i2 = this.backgroundState;
            if (!(i2 == 0 || i2 == 1)) {
                i2 = this.previousBackgroundState;
                if (!(i2 == 0 || i2 == 1)) {
                    return;
                }
            }
            int dp = AndroidUtilities.dp(4.0f);
            if (this.previousBackgroundState != -2) {
                ArticleViewer.progressPaint.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
            } else {
                ArticleViewer.progressPaint.setAlpha((int) (this.alpha * 255.0f));
            }
            this.progressRect.set((float) (access$23700 + dp), (float) (access$23800 + dp), (float) ((access$23700 + i) - dp), (float) ((access$23800 + i) - dp));
            canvas.drawArc(this.progressRect, this.radOffset - 0.049804688f, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, ArticleViewer.progressPaint);
            updateAnimation();
        }
    }

    public class ScrollEvaluator extends IntEvaluator {
        public Integer evaluate(float f, Integer num, Integer num2) {
            return super.evaluate(f, num, num2);
        }
    }

    private class SearchResult {
        private PageBlock block;
        private int index;
        private Object text;

        private SearchResult() {
        }

        /* synthetic */ SearchResult(ArticleViewer articleViewer, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private class TextSizeCell extends FrameLayout {
        private int endFontSize = 30;
        private int lastWidth;
        private SeekBarView sizeBar;
        private int startFontSize = 12;
        private TextPaint textPaint;

        public TextSizeCell(Context context) {
            super(context);
            setWillNotDraw(false);
            this.textPaint = new TextPaint(1);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
            this.sizeBar = new SeekBarView(context);
            this.sizeBar.setReportChanges(true);
            this.sizeBar.setDelegate(new SeekBarViewDelegate(ArticleViewer.this) {
                public void onSeekBarPressed(boolean z) {
                }

                public void onSeekBarDrag(boolean z, float f) {
                    int round = Math.round(((float) TextSizeCell.this.startFontSize) + (((float) (TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize)) * f));
                    if (round != SharedConfig.ivFontSize) {
                        SharedConfig.ivFontSize = round;
                        Editor edit = MessagesController.getGlobalMainSettings().edit();
                        edit.putInt("iv_font_size", SharedConfig.ivFontSize);
                        edit.commit();
                        ArticleViewer.this.adapter[0].searchTextOffset.clear();
                        ArticleViewer.this.updatePaintSize();
                        TextSizeCell.this.invalidate();
                    }
                }
            });
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 9.0f, 5.0f, 43.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteValueText"));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(SharedConfig.ivFontSize);
            canvas.drawText(stringBuilder.toString(), (float) (getMeasuredWidth() - AndroidUtilities.dp(39.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            i = MeasureSpec.getSize(i);
            if (this.lastWidth != i) {
                SeekBarView seekBarView = this.sizeBar;
                int i3 = SharedConfig.ivFontSize;
                int i4 = this.startFontSize;
                seekBarView.setProgress(((float) (i3 - i4)) / ((float) (this.endFontSize - i4)));
                this.lastWidth = i;
            }
        }

        public void invalidate() {
            super.invalidate();
            this.sizeBar.invalidate();
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

        @TargetApi(21)
        public WindowInsets dispatchApplyWindowInsets(WindowInsets windowInsets) {
            WindowInsets windowInsets2 = (WindowInsets) ArticleViewer.this.lastInsets;
            ArticleViewer.this.lastInsets = windowInsets;
            if (windowInsets2 == null || !windowInsets2.toString().equals(windowInsets.toString())) {
                ArticleViewer.this.windowView.requestLayout();
            }
            if (VERSION.SDK_INT >= 28) {
                DisplayCutout displayCutout = ArticleViewer.this.parentActivity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
                if (displayCutout != null) {
                    List boundingRects = displayCutout.getBoundingRects();
                    if (!(boundingRects == null || boundingRects.isEmpty())) {
                        ArticleViewer articleViewer = ArticleViewer.this;
                        boolean z = false;
                        if (((Rect) boundingRects.get(0)).height() != 0) {
                            z = true;
                        }
                        articleViewer.hasCutout = z;
                    }
                }
            }
            return super.dispatchApplyWindowInsets(windowInsets);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            i2 = MeasureSpec.getSize(i2);
            if (VERSION.SDK_INT < 21 || ArticleViewer.this.lastInsets == null) {
                setMeasuredDimension(i, i2);
            } else {
                setMeasuredDimension(i, i2);
                WindowInsets windowInsets = (WindowInsets) ArticleViewer.this.lastInsets;
                if (AndroidUtilities.incorrectDisplaySizeFix) {
                    int i3 = AndroidUtilities.displaySize.y;
                    if (i2 > i3) {
                        i2 = i3;
                    }
                    i2 += AndroidUtilities.statusBarHeight;
                }
                i2 -= windowInsets.getSystemWindowInsetBottom();
                i -= windowInsets.getSystemWindowInsetRight() + windowInsets.getSystemWindowInsetLeft();
                if (windowInsets.getSystemWindowInsetRight() != 0) {
                    this.bWidth = windowInsets.getSystemWindowInsetRight();
                    this.bHeight = i2;
                } else if (windowInsets.getSystemWindowInsetLeft() != 0) {
                    this.bWidth = windowInsets.getSystemWindowInsetLeft();
                    this.bHeight = i2;
                } else {
                    this.bWidth = i;
                    this.bHeight = windowInsets.getStableInsetBottom();
                }
                i2 -= windowInsets.getSystemWindowInsetTop();
            }
            ArticleViewer.this.keyboardVisible = i2 < AndroidUtilities.displaySize.y - AndroidUtilities.dp(100.0f);
            ArticleViewer.this.containerView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            ArticleViewer.this.photoContainerView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            ArticleViewer.this.photoContainerBackground.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            ArticleViewer.this.fullscreenVideoContainer.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            ViewGroup.LayoutParams layoutParams = ArticleViewer.this.animatingImageView.getLayoutParams();
            ArticleViewer.this.animatingImageView.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(layoutParams.height, Integer.MIN_VALUE));
        }

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            TextSelectionOverlay overlayView = ArticleViewer.this.textSelectionHelper.getOverlayView(getContext());
            MotionEvent obtain = MotionEvent.obtain(motionEvent);
            obtain.offsetLocation(-ArticleViewer.this.containerView.getX(), -ArticleViewer.this.containerView.getY());
            if (ArticleViewer.this.textSelectionHelper.isSelectionMode() && ArticleViewer.this.textSelectionHelper.getOverlayView(getContext()).onTouchEvent(obtain)) {
                return true;
            }
            if (overlayView.checkOnTap(motionEvent)) {
                motionEvent.setAction(3);
            }
            if (motionEvent.getAction() != 0 || !ArticleViewer.this.textSelectionHelper.isSelectionMode() || (motionEvent.getY() >= ((float) ArticleViewer.this.containerView.getTop()) && motionEvent.getY() <= ((float) ArticleViewer.this.containerView.getBottom()))) {
                return super.dispatchTouchEvent(motionEvent);
            }
            if (ArticleViewer.this.textSelectionHelper.getOverlayView(getContext()).onTouchEvent(obtain)) {
                return super.dispatchTouchEvent(motionEvent);
            }
            return true;
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            if (!this.selfLayout) {
                int i5;
                int i6;
                i3 -= i;
                if (ArticleViewer.this.anchorsOffsetMeasuredWidth != i3) {
                    for (i5 = 0; i5 < ArticleViewer.this.listView.length; i5++) {
                        for (Entry value : ArticleViewer.this.adapter[i5].anchorsOffset.entrySet()) {
                            value.setValue(Integer.valueOf(-1));
                        }
                    }
                    ArticleViewer.this.anchorsOffsetMeasuredWidth = i3;
                }
                if (VERSION.SDK_INT < 21 || ArticleViewer.this.lastInsets == null) {
                    i5 = 0;
                    i6 = 0;
                } else {
                    WindowInsets windowInsets = (WindowInsets) ArticleViewer.this.lastInsets;
                    i6 = windowInsets.getSystemWindowInsetLeft();
                    if (windowInsets.getSystemWindowInsetRight() != 0) {
                        this.bX = i3 - this.bWidth;
                        this.bY = 0;
                    } else if (windowInsets.getSystemWindowInsetLeft() != 0) {
                        this.bX = 0;
                        this.bY = 0;
                    } else {
                        this.bX = 0;
                        this.bY = (i4 - i2) - this.bHeight;
                    }
                    i5 = windowInsets.getSystemWindowInsetTop() + 0;
                }
                ArticleViewer.this.containerView.layout(i6, i5, ArticleViewer.this.containerView.getMeasuredWidth() + i6, ArticleViewer.this.containerView.getMeasuredHeight() + i5);
                ArticleViewer.this.photoContainerView.layout(i6, i5, ArticleViewer.this.photoContainerView.getMeasuredWidth() + i6, ArticleViewer.this.photoContainerView.getMeasuredHeight() + i5);
                ArticleViewer.this.photoContainerBackground.layout(i6, i5, ArticleViewer.this.photoContainerBackground.getMeasuredWidth() + i6, ArticleViewer.this.photoContainerBackground.getMeasuredHeight() + i5);
                ArticleViewer.this.fullscreenVideoContainer.layout(i6, i5, ArticleViewer.this.fullscreenVideoContainer.getMeasuredWidth() + i6, ArticleViewer.this.fullscreenVideoContainer.getMeasuredHeight() + i5);
                ArticleViewer.this.animatingImageView.layout(0, 0, ArticleViewer.this.animatingImageView.getMeasuredWidth(), ArticleViewer.this.animatingImageView.getMeasuredHeight());
                if (ArticleViewer.this.runAfterKeyboardClose != null) {
                    ArticleViewer.this.runAfterKeyboardClose.start();
                    ArticleViewer.this.runAfterKeyboardClose = null;
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            ArticleViewer.this.attachedToWindow = true;
        }

        /* Access modifiers changed, original: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            ArticleViewer.this.attachedToWindow = false;
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            handleTouchEvent(null);
            super.requestDisallowInterceptTouchEvent(z);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return !ArticleViewer.this.collapsed && (handleTouchEvent(motionEvent) || super.onInterceptTouchEvent(motionEvent));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return !ArticleViewer.this.collapsed && (handleTouchEvent(motionEvent) || super.onTouchEvent(motionEvent));
        }

        @Keep
        public void setInnerTranslationX(float f) {
            this.innerTranslationX = f;
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                DrawerLayoutContainer drawerLayoutContainer = ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer;
                boolean z = (ArticleViewer.this.isVisible && this.alpha == 1.0f && this.innerTranslationX == 0.0f) ? false : true;
                drawerLayoutContainer.setAllowDrawContent(z);
            }
            invalidate();
        }

        /* Access modifiers changed, original: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            int measuredWidth = getMeasuredWidth();
            int i = (int) this.innerTranslationX;
            int save = canvas.save();
            canvas.clipRect(i, 0, measuredWidth, getHeight());
            boolean drawChild = super.drawChild(canvas, view, j);
            canvas.restoreToCount(save);
            if (i != 0 && view == ArticleViewer.this.containerView) {
                float f = (float) (measuredWidth - i);
                float min = Math.min(0.8f, f / ((float) measuredWidth));
                if (min < 0.0f) {
                    min = 0.0f;
                }
                ArticleViewer.this.scrimPaint.setColor(((int) (min * 153.0f)) << 24);
                canvas.drawRect(0.0f, 0.0f, (float) i, (float) getHeight(), ArticleViewer.this.scrimPaint);
                min = Math.max(0.0f, Math.min(f / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                ArticleViewer.this.layerShadowDrawable.setBounds(i - ArticleViewer.this.layerShadowDrawable.getIntrinsicWidth(), view.getTop(), i, view.getBottom());
                ArticleViewer.this.layerShadowDrawable.setAlpha((int) (min * 255.0f));
                ArticleViewer.this.layerShadowDrawable.draw(canvas);
            }
            return drawChild;
        }

        @Keep
        public float getInnerTranslationX() {
            return this.innerTranslationX;
        }

        private void prepareForMoving(MotionEvent motionEvent) {
            this.maybeStartTracking = false;
            this.startedTracking = true;
            this.startedTrackingX = (int) motionEvent.getX();
            if (ArticleViewer.this.pagesStack.size() > 1) {
                this.movingPage = true;
                this.startMovingHeaderHeight = ArticleViewer.this.currentHeaderHeight;
                ArticleViewer.this.listView[1].setVisibility(0);
                ArticleViewer.this.listView[1].setAlpha(1.0f);
                ArticleViewer.this.listView[1].setTranslationX(0.0f);
                ArticleViewer.this.listView[0].setBackgroundColor(ArticleViewer.this.backgroundPaint.getColor());
                ArticleViewer.this.updateInterfaceForCurrentPage(true, -1);
            } else {
                this.movingPage = false;
            }
            ArticleViewer.this.cancelCheckLongPress();
        }

        public boolean handleTouchEvent(MotionEvent motionEvent) {
            if (ArticleViewer.this.isPhotoVisible || this.closeAnimationInProgress || ArticleViewer.this.fullscreenVideoContainer.getVisibility() == 0 || ArticleViewer.this.textSelectionHelper.isSelectionMode()) {
                return false;
            }
            VelocityTracker velocityTracker;
            if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                this.startedTrackingY = (int) motionEvent.getY();
                velocityTracker = this.tracker;
                if (velocityTracker != null) {
                    velocityTracker.clear();
                }
            } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.tracker == null) {
                    this.tracker = VelocityTracker.obtain();
                }
                int max = Math.max(0, (int) (motionEvent.getX() - ((float) this.startedTrackingX)));
                int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                this.tracker.addMovement(motionEvent);
                if (this.maybeStartTracking && !this.startedTracking && ((float) max) >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(max) / 3 > abs) {
                    prepareForMoving(motionEvent);
                } else if (this.startedTracking) {
                    ArticleViewer.this.pressedLinkOwnerLayout = null;
                    ArticleViewer.this.pressedLinkOwnerView = null;
                    if (this.movingPage) {
                        ArticleViewer.this.listView[0].setTranslationX((float) max);
                    } else {
                        float f = (float) max;
                        ArticleViewer.this.containerView.setTranslationX(f);
                        setInnerTranslationX(f);
                    }
                }
            } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6)) {
                if (this.tracker == null) {
                    this.tracker = VelocityTracker.obtain();
                }
                this.tracker.computeCurrentVelocity(1000);
                float xVelocity = this.tracker.getXVelocity();
                float yVelocity = this.tracker.getYVelocity();
                if (!this.startedTracking && xVelocity >= 3500.0f && xVelocity > Math.abs(yVelocity)) {
                    prepareForMoving(motionEvent);
                }
                if (this.startedTracking) {
                    View access$2500 = this.movingPage ? ArticleViewer.this.listView[0] : ArticleViewer.this.containerView;
                    float x = access$2500.getX();
                    final boolean z = x < ((float) access$2500.getMeasuredWidth()) / 3.0f && (xVelocity < 3500.0f || xVelocity < yVelocity);
                    AnimatorSet animatorSet = new AnimatorSet();
                    Animator[] animatorArr;
                    if (!z) {
                        x = ((float) access$2500.getMeasuredWidth()) - x;
                        if (this.movingPage) {
                            animatorArr = new Animator[1];
                            animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.listView[0], View.TRANSLATION_X, new float[]{(float) access$2500.getMeasuredWidth()});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.containerView, View.TRANSLATION_X, new float[]{(float) access$2500.getMeasuredWidth()});
                            animatorArr[1] = ObjectAnimator.ofFloat(this, ArticleViewer.ARTICLE_VIEWER_INNER_TRANSLATION_X, new float[]{(float) access$2500.getMeasuredWidth()});
                            animatorSet.playTogether(animatorArr);
                        }
                    } else if (this.movingPage) {
                        animatorArr = new Animator[1];
                        animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.listView[0], View.TRANSLATION_X, new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                    } else {
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.containerView, View.TRANSLATION_X, new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this, ArticleViewer.ARTICLE_VIEWER_INNER_TRANSLATION_X, new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                    }
                    animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) access$2500.getMeasuredWidth())) * x), 50));
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (WindowView.this.movingPage) {
                                ArticleViewer.this.listView[0].setBackgroundDrawable(null);
                                if (!z) {
                                    WebpageAdapter webpageAdapter = ArticleViewer.this.adapter[1];
                                    ArticleViewer.this.adapter[1] = ArticleViewer.this.adapter[0];
                                    ArticleViewer.this.adapter[0] = webpageAdapter;
                                    RecyclerListView recyclerListView = ArticleViewer.this.listView[1];
                                    ArticleViewer.this.listView[1] = ArticleViewer.this.listView[0];
                                    ArticleViewer.this.listView[0] = recyclerListView;
                                    LinearLayoutManager linearLayoutManager = ArticleViewer.this.layoutManager[1];
                                    ArticleViewer.this.layoutManager[1] = ArticleViewer.this.layoutManager[0];
                                    ArticleViewer.this.layoutManager[0] = linearLayoutManager;
                                    ArticleViewer.this.pagesStack.remove(ArticleViewer.this.pagesStack.size() - 1);
                                    ArticleViewer articleViewer = ArticleViewer.this;
                                    articleViewer.currentPage = (WebPage) articleViewer.pagesStack.get(ArticleViewer.this.pagesStack.size() - 1);
                                    articleViewer = ArticleViewer.this;
                                    articleViewer.textSelectionHelper.setParentView(articleViewer.listView[0]);
                                    articleViewer = ArticleViewer.this;
                                    articleViewer.textSelectionHelper.layoutManager = articleViewer.layoutManager[0];
                                    ArticleViewer.this.titleTextView.setText(ArticleViewer.this.currentPage.site_name == null ? "" : ArticleViewer.this.currentPage.site_name);
                                    ArticleViewer.this.textSelectionHelper.clear(true);
                                    ArticleViewer.this.headerView.invalidate();
                                }
                                ArticleViewer.this.listView[1].setVisibility(8);
                                ArticleViewer.this.headerView.invalidate();
                            } else if (!z) {
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
                velocityTracker = this.tracker;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    this.tracker = null;
                }
            } else if (motionEvent == null) {
                this.maybeStartTracking = false;
                this.startedTracking = false;
                this.movingPage = false;
                velocityTracker = this.tracker;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    this.tracker = null;
                }
                ArticleTextSelectionHelper articleTextSelectionHelper = ArticleViewer.this.textSelectionHelper;
                if (!(articleTextSelectionHelper == null || articleTextSelectionHelper.isSelectionMode())) {
                    ArticleViewer.this.textSelectionHelper.clear();
                }
            }
            return this.startedTracking;
        }

        /* Access modifiers changed, original: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (this.bWidth != 0 && this.bHeight != 0) {
                ArticleViewer.this.blackPaint.setAlpha((int) (ArticleViewer.this.windowView.getAlpha() * 255.0f));
                int i = this.bX;
                if (i == 0) {
                    int i2 = this.bY;
                    if (i2 == 0) {
                        canvas.drawRect((float) i, (float) i2, (float) (i + this.bWidth), (float) (i2 + this.bHeight), ArticleViewer.this.blackPaint);
                        return;
                    }
                }
                canvas.drawRect(((float) this.bX) - getTranslationX(), (float) this.bY, ((float) (this.bX + this.bWidth)) - getTranslationX(), (float) (this.bY + this.bHeight), ArticleViewer.this.blackPaint);
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            float f = (float) measuredWidth;
            float f2 = (float) measuredHeight;
            canvas.drawRect(this.innerTranslationX, 0.0f, f, f2, ArticleViewer.this.backgroundPaint);
            if (VERSION.SDK_INT >= 21 && ArticleViewer.this.lastInsets != null) {
                WindowInsets windowInsets = (WindowInsets) ArticleViewer.this.lastInsets;
                canvas.drawRect(this.innerTranslationX, 0.0f, f, (float) windowInsets.getSystemWindowInsetTop(), ArticleViewer.this.statusBarPaint);
                if (ArticleViewer.this.hasCutout) {
                    measuredWidth = windowInsets.getSystemWindowInsetLeft();
                    if (measuredWidth != 0) {
                        canvas.drawRect(0.0f, 0.0f, (float) measuredWidth, f2, ArticleViewer.this.statusBarPaint);
                    }
                    measuredWidth = windowInsets.getSystemWindowInsetRight();
                    if (measuredWidth != 0) {
                        canvas.drawRect((float) (getMeasuredWidth() - measuredWidth), 0.0f, (float) getMeasuredWidth(), f2, ArticleViewer.this.statusBarPaint);
                    }
                }
            }
        }

        @Keep
        public void setAlpha(float f) {
            int i = (int) (255.0f * f);
            ArticleViewer.this.backgroundPaint.setAlpha(i);
            ArticleViewer.this.statusBarPaint.setAlpha(i);
            this.alpha = f;
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

        public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
            if (keyEvent == null || keyEvent.getKeyCode() != 4 || keyEvent.getAction() != 1) {
                return super.dispatchKeyEventPreIme(keyEvent);
            }
            if (ArticleViewer.this.searchField.isFocused()) {
                ArticleViewer.this.searchField.clearFocus();
                AndroidUtilities.hideKeyboard(ArticleViewer.this.searchField);
            } else {
                ArticleViewer.this.close(true, false);
            }
            return true;
        }
    }

    public class DrawingText implements TextLayoutBlock {
        public LinkPath markPath;
        public PageBlock parentBlock;
        public Object parentText;
        public CharSequence prefix;
        public int row;
        public int searchIndex = -1;
        public LinkPath searchPath;
        public StaticLayout textLayout;
        public LinkPath textPath;
        public int x;
        public int y;

        public void draw(Canvas canvas) {
            if (ArticleViewer.this.searchResults.isEmpty()) {
                this.searchIndex = -1;
                this.searchPath = null;
            } else {
                SearchResult searchResult = (SearchResult) ArticleViewer.this.searchResults.get(ArticleViewer.this.currentSearchIndex);
                if (searchResult.block != this.parentBlock || (searchResult.text != this.parentText && (!(searchResult.text instanceof String) || this.parentText != null))) {
                    this.searchIndex = -1;
                    this.searchPath = null;
                } else if (this.searchIndex != searchResult.index) {
                    this.searchPath = new LinkPath(true);
                    this.searchPath.setAllowReset(false);
                    this.searchPath.setCurrentLayout(this.textLayout, searchResult.index, 0.0f);
                    this.searchPath.setBaselineShift(0);
                    this.textLayout.getSelectionPath(searchResult.index, searchResult.index + ArticleViewer.this.searchText.length(), this.searchPath);
                    this.searchPath.setAllowReset(true);
                }
            }
            LinkPath linkPath = this.searchPath;
            if (linkPath != null) {
                canvas.drawPath(linkPath, ArticleViewer.webpageSearchPaint);
            }
            linkPath = this.textPath;
            if (linkPath != null) {
                canvas.drawPath(linkPath, ArticleViewer.webpageUrlPaint);
            }
            linkPath = this.markPath;
            if (linkPath != null) {
                canvas.drawPath(linkPath, ArticleViewer.webpageMarkPaint);
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

        public int getLineAscent(int i) {
            return this.textLayout.getLineAscent(i);
        }

        public float getLineLeft(int i) {
            return this.textLayout.getLineLeft(i);
        }

        public float getLineWidth(int i) {
            return this.textLayout.getLineWidth(i);
        }

        public int getHeight() {
            return this.textLayout.getHeight();
        }

        public int getWidth() {
            return this.textLayout.getWidth();
        }

        public StaticLayout getLayout() {
            return this.textLayout;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getRow() {
            return this.row;
        }

        public CharSequence getPrefix() {
            return this.prefix;
        }
    }

    private class BlockAudioCell extends View implements FileDownloadProgressListener, ArticleSelectableView {
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
        private String lastTimeString;
        private WebpageAdapter parentAdapter;
        private RadialProgress2 radialProgress;
        private SeekBar seekBar;
        private int seekBarX;
        private int seekBarY;
        private int textX;
        private int textY = AndroidUtilities.dp(58.0f);
        private DrawingText titleLayout;

        public void onProgressUpload(String str, long j, long j2, boolean z) {
        }

        public BlockAudioCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            this.radialProgress = new RadialProgress2(this);
            this.radialProgress.setCircleRadius(AndroidUtilities.dp(24.0f));
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            this.seekBar = new SeekBar(context);
            this.seekBar.setDelegate(new -$$Lambda$ArticleViewer$BlockAudioCell$WgP383-edJ263u4ZKo-pJ9bitM4(this));
        }

        public /* synthetic */ void lambda$new$0$ArticleViewer$BlockAudioCell(float f) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject != null) {
                messageObject.audioProgress = f;
                MediaController.getInstance().seekToProgress(this.currentMessageObject, f);
            }
        }

        public void setBlock(TL_pageBlockAudio tL_pageBlockAudio, boolean z, boolean z2) {
            this.currentBlock = tL_pageBlockAudio;
            this.currentMessageObject = (MessageObject) this.parentAdapter.audioBlocks.get(this.currentBlock);
            this.currentDocument = this.currentMessageObject.getDocument();
            this.isFirst = z;
            String str = "chat_inAudioSeekbarFill";
            this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioCacheSeekbar"), Theme.getColor(str), Theme.getColor(str), Theme.getColor("chat_inAudioSeekbarSelected"));
            updateButtonState(false);
            requestLayout();
        }

        public MessageObject getMessageObject() {
            return this.currentMessageObject;
        }

        /* JADX WARNING: Missing block: B:18:0x0064, code skipped:
            if (r1 <= ((float) (r0 + org.telegram.messenger.AndroidUtilities.dp(48.0f)))) goto L_0x006a;
     */
        /* JADX WARNING: Missing block: B:20:0x0068, code skipped:
            if (r11.buttonState == 0) goto L_0x006a;
     */
        /* JADX WARNING: Missing block: B:21:0x006a, code skipped:
            r11.buttonPressed = 1;
            invalidate();
     */
        public boolean onTouchEvent(android.view.MotionEvent r12) {
            /*
            r11 = this;
            r0 = r12.getX();
            r1 = r12.getY();
            r2 = r11.seekBar;
            r3 = r12.getAction();
            r4 = r12.getX();
            r5 = r11.seekBarX;
            r5 = (float) r5;
            r4 = r4 - r5;
            r5 = r12.getY();
            r6 = r11.seekBarY;
            r6 = (float) r6;
            r5 = r5 - r6;
            r2 = r2.onTouch(r3, r4, r5);
            r3 = 1;
            if (r2 == 0) goto L_0x0036;
        L_0x0025:
            r12 = r12.getAction();
            if (r12 != 0) goto L_0x0032;
        L_0x002b:
            r12 = r11.getParent();
            r12.requestDisallowInterceptTouchEvent(r3);
        L_0x0032:
            r11.invalidate();
            return r3;
        L_0x0036:
            r2 = r12.getAction();
            r4 = 0;
            if (r2 != 0) goto L_0x0070;
        L_0x003d:
            r2 = r11.buttonState;
            r5 = -1;
            if (r2 == r5) goto L_0x0066;
        L_0x0042:
            r2 = r11.buttonX;
            r5 = (float) r2;
            r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1));
            if (r5 < 0) goto L_0x0066;
        L_0x0049:
            r5 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r2 = r2 + r6;
            r2 = (float) r2;
            r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
            if (r0 > 0) goto L_0x0066;
        L_0x0055:
            r0 = r11.buttonY;
            r2 = (float) r0;
            r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
            if (r2 < 0) goto L_0x0066;
        L_0x005c:
            r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r0 = r0 + r2;
            r0 = (float) r0;
            r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
            if (r0 <= 0) goto L_0x006a;
        L_0x0066:
            r0 = r11.buttonState;
            if (r0 != 0) goto L_0x008f;
        L_0x006a:
            r11.buttonPressed = r3;
            r11.invalidate();
            goto L_0x008f;
        L_0x0070:
            r0 = r12.getAction();
            if (r0 != r3) goto L_0x0086;
        L_0x0076:
            r0 = r11.buttonPressed;
            if (r0 != r3) goto L_0x008f;
        L_0x007a:
            r11.buttonPressed = r4;
            r11.playSoundEffect(r4);
            r11.didPressedButton(r3);
            r11.invalidate();
            goto L_0x008f;
        L_0x0086:
            r0 = r12.getAction();
            r1 = 3;
            if (r0 != r1) goto L_0x008f;
        L_0x008d:
            r11.buttonPressed = r4;
        L_0x008f:
            r0 = r11.buttonPressed;
            if (r0 != 0) goto L_0x00bf;
        L_0x0093:
            r5 = org.telegram.ui.ArticleViewer.this;
            r8 = r11.captionLayout;
            r9 = r11.textX;
            r10 = r11.textY;
            r6 = r12;
            r7 = r11;
            r0 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10);
            if (r0 != 0) goto L_0x00bf;
        L_0x00a3:
            r5 = org.telegram.ui.ArticleViewer.this;
            r8 = r11.creditLayout;
            r9 = r11.textX;
            r0 = r11.textY;
            r1 = r11.creditOffset;
            r10 = r0 + r1;
            r6 = r12;
            r7 = r11;
            r0 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10);
            if (r0 != 0) goto L_0x00bf;
        L_0x00b7:
            r12 = super.onTouchEvent(r12);
            if (r12 == 0) goto L_0x00be;
        L_0x00bd:
            goto L_0x00bf;
        L_0x00be:
            r3 = 0;
        L_0x00bf:
            return r3;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$BlockAudioCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"DrawAllocation", "NewApi"})
        public void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i);
            int dp = AndroidUtilities.dp(54.0f);
            TL_pageBlockAudio tL_pageBlockAudio = this.currentBlock;
            int i3 = 1;
            if (tL_pageBlockAudio != null) {
                int i4 = tL_pageBlockAudio.level;
                if (i4 > 0) {
                    this.textX = AndroidUtilities.dp((float) (i4 * 14)) + AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = AndroidUtilities.dp(18.0f);
                }
                int dp2 = (size - this.textX) - AndroidUtilities.dp(18.0f);
                int dp3 = AndroidUtilities.dp(44.0f);
                this.buttonX = AndroidUtilities.dp(16.0f);
                this.buttonY = AndroidUtilities.dp(5.0f);
                RadialProgress2 radialProgress2 = this.radialProgress;
                int i5 = this.buttonX;
                int i6 = this.buttonY;
                radialProgress2.setProgressRect(i5, i6, i5 + dp3, i6 + dp3);
                ArticleViewer articleViewer = ArticleViewer.this;
                TL_pageBlockAudio tL_pageBlockAudio2 = this.currentBlock;
                this.captionLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockAudio2.caption.text, dp2, this.textY, tL_pageBlockAudio2, this.parentAdapter);
                if (this.captionLayout != null) {
                    this.creditOffset = AndroidUtilities.dp(8.0f) + this.captionLayout.getHeight();
                    dp += this.creditOffset + AndroidUtilities.dp(8.0f);
                }
                int i7 = dp;
                articleViewer = ArticleViewer.this;
                tL_pageBlockAudio2 = this.currentBlock;
                this.creditLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockAudio2.caption.credit, dp2, this.textY + this.creditOffset, tL_pageBlockAudio2, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.creditLayout != null) {
                    i7 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                if (!this.isFirst && this.currentBlock.level <= 0) {
                    i7 += AndroidUtilities.dp(8.0f);
                }
                String musicAuthor = this.currentMessageObject.getMusicAuthor(false);
                String musicTitle = this.currentMessageObject.getMusicTitle(false);
                this.seekBarX = (this.buttonX + AndroidUtilities.dp(50.0f)) + dp3;
                int dp4 = (size - this.seekBarX) - AndroidUtilities.dp(18.0f);
                if (TextUtils.isEmpty(musicTitle) && TextUtils.isEmpty(musicAuthor)) {
                    this.titleLayout = null;
                    this.seekBarY = this.buttonY + ((dp3 - AndroidUtilities.dp(30.0f)) / 2);
                } else {
                    SpannableStringBuilder spannableStringBuilder;
                    if (!TextUtils.isEmpty(musicTitle) && !TextUtils.isEmpty(musicAuthor)) {
                        spannableStringBuilder = new SpannableStringBuilder(String.format("%s - %s", new Object[]{musicAuthor, musicTitle}));
                    } else if (TextUtils.isEmpty(musicTitle)) {
                        spannableStringBuilder = new SpannableStringBuilder(musicAuthor);
                    } else {
                        spannableStringBuilder = new SpannableStringBuilder(musicTitle);
                    }
                    if (!TextUtils.isEmpty(musicAuthor)) {
                        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, musicAuthor.length(), 18);
                    }
                    CharSequence ellipsize = TextUtils.ellipsize(spannableStringBuilder, Theme.chat_audioTitlePaint, (float) dp4, TruncateAt.END);
                    this.titleLayout = new DrawingText();
                    this.titleLayout.textLayout = new StaticLayout(ellipsize, ArticleViewer.audioTimePaint, dp4, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.titleLayout.parentBlock = this.currentBlock;
                    this.seekBarY = (this.buttonY + ((dp3 - AndroidUtilities.dp(30.0f)) / 2)) + AndroidUtilities.dp(11.0f);
                }
                this.seekBar.setSize(dp4, AndroidUtilities.dp(30.0f));
                i3 = i7;
            }
            setMeasuredDimension(size, i3);
            updatePlayingMessageProgress();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int i;
                DrawingText drawingText;
                int i2;
                int i3;
                this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
                this.radialProgress.setProgressColor(Theme.getColor("chat_inFileProgress"));
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
                int i4 = 0;
                if (this.titleLayout != null) {
                    canvas.save();
                    this.titleLayout.x = this.buttonX + AndroidUtilities.dp(54.0f);
                    this.titleLayout.y = this.seekBarY - AndroidUtilities.dp(16.0f);
                    DrawingText drawingText2 = this.titleLayout;
                    canvas.translate((float) drawingText2.x, (float) drawingText2.y);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.titleLayout.draw(canvas);
                    canvas.restore();
                    i = 1;
                } else {
                    i = 0;
                }
                if (this.captionLayout != null) {
                    canvas.save();
                    drawingText = this.captionLayout;
                    i2 = this.textX;
                    drawingText.x = i2;
                    i3 = this.textY;
                    drawingText.y = i3;
                    canvas.translate((float) i2, (float) i3);
                    i2 = i + 1;
                    ArticleViewer.this.drawTextSelection(canvas, this, i);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                    i = i2;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    drawingText = this.creditLayout;
                    i2 = this.textX;
                    drawingText.x = i2;
                    i3 = this.textY;
                    int i5 = this.creditOffset;
                    drawingText.y = i3 + i5;
                    canvas.translate((float) i2, (float) (i3 + i5));
                    ArticleViewer.this.drawTextSelection(canvas, this, i);
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    float dp = (float) AndroidUtilities.dp(18.0f);
                    float dp2 = (float) AndroidUtilities.dp(20.0f);
                    i = getMeasuredHeight();
                    if (this.currentBlock.bottom) {
                        i4 = AndroidUtilities.dp(6.0f);
                    }
                    canvas.drawRect(dp, 0.0f, dp2, (float) (i - i4), ArticleViewer.quoteLinePaint);
                }
            }
        }

        private int getIconForCurrentState() {
            int i = this.buttonState;
            if (i == 1) {
                return 1;
            }
            if (i == 2) {
                return 2;
            }
            return i == 3 ? 3 : 0;
        }

        public void updatePlayingMessageProgress() {
            if (this.currentDocument != null && this.currentMessageObject != null) {
                if (!this.seekBar.isDragging()) {
                    this.seekBar.setProgress(this.currentMessageObject.audioProgress);
                }
                int i = 0;
                if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    i = this.currentMessageObject.audioProgressSec;
                } else {
                    for (int i2 = 0; i2 < this.currentDocument.attributes.size(); i2++) {
                        DocumentAttribute documentAttribute = (DocumentAttribute) this.currentDocument.attributes.get(i2);
                        if (documentAttribute instanceof TL_documentAttributeAudio) {
                            i = documentAttribute.duration;
                            break;
                        }
                    }
                }
                String formatShortDuration = AndroidUtilities.formatShortDuration(i);
                String str = this.lastTimeString;
                if (str == null || !(str == null || str.equals(formatShortDuration))) {
                    this.lastTimeString = formatShortDuration;
                    ArticleViewer.audioTimePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
                    this.durationLayout = new StaticLayout(formatShortDuration, ArticleViewer.audioTimePaint, (int) Math.ceil((double) ArticleViewer.audioTimePaint.measureText(formatShortDuration)), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
                ArticleViewer.audioTimePaint.setColor(ArticleViewer.this.getTextColor());
                invalidate();
            }
        }

        public void updateButtonState(boolean z) {
            String attachFileName = FileLoader.getAttachFileName(this.currentDocument);
            boolean exists = FileLoader.getPathToAttach(this.currentDocument, true).exists();
            if (TextUtils.isEmpty(attachFileName)) {
                this.radialProgress.setIcon(4, false, false);
                return;
            }
            if (exists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!isPlayingMessage || (isPlayingMessage && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), false, z);
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(attachFileName, null, this);
                if (FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(attachFileName)) {
                    this.buttonState = 3;
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                    if (fileProgress != null) {
                        this.radialProgress.setProgress(fileProgress.floatValue(), z);
                    } else {
                        this.radialProgress.setProgress(0.0f, z);
                    }
                    this.radialProgress.setIcon(getIconForCurrentState(), true, z);
                } else {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, z);
                    this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                }
            }
            updatePlayingMessageProgress();
        }

        private void didPressedButton(boolean z) {
            int i = this.buttonState;
            if (i == 0) {
                if (MediaController.getInstance().setPlaylist(this.parentAdapter.audioMessages, this.currentMessageObject, false)) {
                    this.buttonState = 1;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                    invalidate();
                }
            } else if (i == 1) {
                if (MediaController.getInstance().lambda$startAudioAgain$6$MediaController(this.currentMessageObject)) {
                    this.buttonState = 0;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                    invalidate();
                }
            } else if (i == 2) {
                this.radialProgress.setProgress(0.0f, false);
                FileLoader.getInstance(ArticleViewer.this.currentAccount).loadFile(this.currentDocument, ArticleViewer.this.currentPage, 1, 1);
                this.buttonState = 3;
                this.radialProgress.setIcon(getIconForCurrentState(), true, z);
                invalidate();
            } else if (i == 3) {
                FileLoader.getInstance(ArticleViewer.this.currentAccount).cancelLoadFile(this.currentDocument);
                this.buttonState = 2;
                this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                invalidate();
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        /* Access modifiers changed, original: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateButtonState(false);
        }

        public void onFailedDownload(String str, boolean z) {
            updateButtonState(true);
        }

        public void onSuccessDownload(String str) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        public void onProgressDownload(String str, long j, long j2) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
            if (this.buttonState != 3) {
                updateButtonState(true);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.titleLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.creditLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockAuthorDateCell extends View implements ArticleSelectableView {
        private TL_pageBlockAuthorDate currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX;
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockAuthorDateCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TL_pageBlockAuthorDate tL_pageBlockAuthorDate) {
            this.currentBlock = tL_pageBlockAuthorDate;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            TL_pageBlockAuthorDate tL_pageBlockAuthorDate = this.currentBlock;
            i2 = 1;
            if (tL_pageBlockAuthorDate != null) {
                MetricAffectingSpan[] metricAffectingSpanArr;
                CharSequence formatString;
                int indexOf;
                ArticleViewer articleViewer = ArticleViewer.this;
                RichText richText = tL_pageBlockAuthorDate.author;
                CharSequence access$22500 = articleViewer.getText(this, richText, richText, tL_pageBlockAuthorDate, i);
                Spannable spannable = null;
                if (access$22500 instanceof Spannable) {
                    spannable = (Spannable) access$22500;
                    metricAffectingSpanArr = (MetricAffectingSpan[]) spannable.getSpans(0, access$22500.length(), MetricAffectingSpan.class);
                } else {
                    metricAffectingSpanArr = null;
                }
                if (this.currentBlock.published_date != 0 && !TextUtils.isEmpty(access$22500)) {
                    formatString = LocaleController.formatString("ArticleDateByAuthor", NUM, LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000), access$22500);
                } else if (TextUtils.isEmpty(access$22500)) {
                    formatString = LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000);
                } else {
                    formatString = LocaleController.formatString("ArticleByAuthor", NUM, access$22500);
                }
                if (metricAffectingSpanArr != null) {
                    try {
                        if (metricAffectingSpanArr.length > 0) {
                            indexOf = TextUtils.indexOf(formatString, access$22500);
                            if (indexOf != -1) {
                                formatString = Factory.getInstance().newSpannable(formatString);
                                for (int i3 = 0; i3 < metricAffectingSpanArr.length; i3++) {
                                    formatString.setSpan(metricAffectingSpanArr[i3], spannable.getSpanStart(metricAffectingSpanArr[i3]) + indexOf, spannable.getSpanEnd(metricAffectingSpanArr[i3]) + indexOf, 33);
                                }
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                this.textLayout = ArticleViewer.this.createLayoutForText(this, formatString, null, i - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter);
                if (this.textLayout != null) {
                    indexOf = (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight()) + 0;
                    if (this.parentAdapter.isRtl) {
                        this.textX = (int) Math.floor((double) ((((float) i) - this.textLayout.getLineWidth(0)) - ((float) AndroidUtilities.dp(16.0f))));
                    } else {
                        this.textX = AndroidUtilities.dp(18.0f);
                    }
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                    i2 = indexOf;
                } else {
                    i2 = 0;
                }
            }
            setMeasuredDimension(i, i2);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                accessibilityNodeInfo.setText(drawingText.getText());
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockBlockquoteCell extends View implements ArticleSelectableView {
        private TL_pageBlockBlockquote currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textX;
        private int textY = AndroidUtilities.dp(8.0f);
        private int textY2;

        public BlockBlockquoteCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TL_pageBlockBlockquote tL_pageBlockBlockquote) {
            this.currentBlock = tL_pageBlockBlockquote;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (!(ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(motionEvent))) {
                    return false;
                }
            }
            return true;
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            int dp;
            i = MeasureSpec.getSize(i);
            if (this.currentBlock != null) {
                i2 = i - AndroidUtilities.dp(50.0f);
                int i3 = this.currentBlock.level;
                if (i3 > 0) {
                    i2 -= AndroidUtilities.dp((float) (i3 * 14));
                }
                ArticleViewer articleViewer = ArticleViewer.this;
                TL_pageBlockBlockquote tL_pageBlockBlockquote = this.currentBlock;
                this.textLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockBlockquote.text, i2, this.textY, tL_pageBlockBlockquote, this.parentAdapter);
                dp = this.textLayout != null ? 0 + (AndroidUtilities.dp(8.0f) + this.textLayout.getHeight()) : 0;
                if (this.currentBlock.level > 0) {
                    if (this.parentAdapter.isRtl) {
                        this.textX = AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 14));
                    } else {
                        this.textX = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(32.0f);
                    }
                } else if (this.parentAdapter.isRtl) {
                    this.textX = AndroidUtilities.dp(14.0f);
                } else {
                    this.textX = AndroidUtilities.dp(32.0f);
                }
                this.textY2 = AndroidUtilities.dp(8.0f) + dp;
                articleViewer = ArticleViewer.this;
                tL_pageBlockBlockquote = this.currentBlock;
                this.textLayout2 = articleViewer.createLayoutForText(this, null, tL_pageBlockBlockquote.caption, i2, this.textY2, tL_pageBlockBlockquote, this.parentAdapter);
                if (this.textLayout2 != null) {
                    dp += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                }
                if (dp != 0) {
                    dp += AndroidUtilities.dp(8.0f);
                }
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
                drawingText = this.textLayout2;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    drawingText.y = this.textY2;
                }
            } else {
                dp = 1;
            }
            setMeasuredDimension(i, dp);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int i;
                int measuredWidth;
                int i2 = 0;
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    i = 1;
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                } else {
                    i = 0;
                }
                if (this.textLayout2 != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY2);
                    ArticleViewer.this.drawTextSelection(canvas, this, i);
                    this.textLayout2.draw(canvas);
                    canvas.restore();
                }
                if (this.parentAdapter.isRtl) {
                    measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(20.0f);
                    canvas.drawRect((float) measuredWidth, (float) AndroidUtilities.dp(6.0f), (float) (measuredWidth + AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), ArticleViewer.quoteLinePaint);
                } else {
                    canvas.drawRect((float) AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 18)), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 20)), (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), ArticleViewer.quoteLinePaint);
                }
                if (this.currentBlock.level > 0) {
                    float dp = (float) AndroidUtilities.dp(18.0f);
                    float dp2 = (float) AndroidUtilities.dp(20.0f);
                    measuredWidth = getMeasuredHeight();
                    if (this.currentBlock.bottom) {
                        i2 = AndroidUtilities.dp(6.0f);
                    }
                    canvas.drawRect(dp, 0.0f, dp2, (float) (measuredWidth - i2), ArticleViewer.quoteLinePaint);
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.textLayout2;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockChannelCell extends FrameLayout implements ArticleSelectableView {
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

        public BlockChannelCell(Context context, WebpageAdapter webpageAdapter, int i) {
            super(context);
            this.parentAdapter = webpageAdapter;
            setWillNotDraw(false);
            this.backgroundPaint = new Paint();
            this.currentType = i;
            this.textView = new TextView(context);
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setText(LocaleController.getString("ChannelJoin", NUM));
            this.textView.setGravity(19);
            addView(this.textView, LayoutHelper.createFrame(-2, 39, 53));
            this.textView.setOnClickListener(new -$$Lambda$ArticleViewer$BlockChannelCell$yVpHWSEz8bUl3txryOPbfk9oO0M(this));
            this.imageView = new ImageView(context);
            this.imageView.setImageResource(NUM);
            this.imageView.setScaleType(ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(39, 39, 53));
            this.progressView = new ContextProgressView(context, 0);
            addView(this.progressView, LayoutHelper.createFrame(39, 39, 53));
        }

        public /* synthetic */ void lambda$new$0$ArticleViewer$BlockChannelCell(View view) {
            if (this.currentState == 0) {
                setState(1, true);
                ArticleViewer articleViewer = ArticleViewer.this;
                articleViewer.joinChannel(this, articleViewer.loadedChannel);
            }
        }

        public void setBlock(TL_pageBlockChannel tL_pageBlockChannel) {
            this.currentBlock = tL_pageBlockChannel;
            if (this.currentType == 0) {
                int color = Theme.getColor("switchTrack");
                int red = Color.red(color);
                int green = Color.green(color);
                color = Color.blue(color);
                this.textView.setTextColor(ArticleViewer.this.getLinkTextColor());
                this.backgroundPaint.setColor(Color.argb(34, red, green, color));
                this.imageView.setColorFilter(new PorterDuffColorFilter(ArticleViewer.this.getGrayTextColor(), Mode.MULTIPLY));
            } else {
                this.textView.setTextColor(-1);
                this.backgroundPaint.setColor(NUM);
                this.imageView.setColorFilter(new PorterDuffColorFilter(-1, Mode.MULTIPLY));
            }
            Chat chat = MessagesController.getInstance(ArticleViewer.this.currentAccount).getChat(Integer.valueOf(tL_pageBlockChannel.channel.id));
            if (chat == null || chat.min) {
                ArticleViewer.this.loadChannel(this, this.parentAdapter, tL_pageBlockChannel.channel);
                setState(1, false);
            } else {
                ArticleViewer.this.loadedChannel = chat;
                if (!chat.left || chat.kicked) {
                    setState(4, false);
                } else {
                    setState(0, false);
                }
            }
            requestLayout();
        }

        public void setState(int i, boolean z) {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.currentState = i;
            float f = 0.0f;
            float f2 = 0.1f;
            if (z) {
                this.currentAnimation = new AnimatorSet();
                AnimatorSet animatorSet2 = this.currentAnimation;
                Animator[] animatorArr = new Animator[9];
                TextView textView = this.textView;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = i == 0 ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(textView, property, fArr);
                textView = this.textView;
                property = View.SCALE_X;
                fArr = new float[1];
                fArr[0] = i == 0 ? 1.0f : 0.1f;
                animatorArr[1] = ObjectAnimator.ofFloat(textView, property, fArr);
                textView = this.textView;
                property = View.SCALE_Y;
                fArr = new float[1];
                fArr[0] = i == 0 ? 1.0f : 0.1f;
                animatorArr[2] = ObjectAnimator.ofFloat(textView, property, fArr);
                ContextProgressView contextProgressView = this.progressView;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = i == 1 ? 1.0f : 0.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(contextProgressView, property2, fArr2);
                contextProgressView = this.progressView;
                property2 = View.SCALE_X;
                fArr2 = new float[1];
                fArr2[0] = i == 1 ? 1.0f : 0.1f;
                animatorArr[4] = ObjectAnimator.ofFloat(contextProgressView, property2, fArr2);
                contextProgressView = this.progressView;
                property2 = View.SCALE_Y;
                fArr2 = new float[1];
                fArr2[0] = i == 1 ? 1.0f : 0.1f;
                animatorArr[5] = ObjectAnimator.ofFloat(contextProgressView, property2, fArr2);
                ImageView imageView = this.imageView;
                property2 = View.ALPHA;
                fArr2 = new float[1];
                if (i == 2) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                animatorArr[6] = ObjectAnimator.ofFloat(imageView, property2, fArr2);
                ImageView imageView2 = this.imageView;
                property = View.SCALE_X;
                fArr = new float[1];
                fArr[0] = i == 2 ? 1.0f : 0.1f;
                animatorArr[7] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                imageView2 = this.imageView;
                property = View.SCALE_Y;
                float[] fArr3 = new float[1];
                if (i == 2) {
                    f2 = 1.0f;
                }
                fArr3[0] = f2;
                animatorArr[8] = ObjectAnimator.ofFloat(imageView2, property, fArr3);
                animatorSet2.playTogether(animatorArr);
                this.currentAnimation.setDuration(150);
                this.currentAnimation.start();
                return;
            }
            this.textView.setAlpha(i == 0 ? 1.0f : 0.0f);
            this.textView.setScaleX(i == 0 ? 1.0f : 0.1f);
            this.textView.setScaleY(i == 0 ? 1.0f : 0.1f);
            this.progressView.setAlpha(i == 1 ? 1.0f : 0.0f);
            this.progressView.setScaleX(i == 1 ? 1.0f : 0.1f);
            this.progressView.setScaleY(i == 1 ? 1.0f : 0.1f);
            ImageView imageView3 = this.imageView;
            if (i == 2) {
                f = 1.0f;
            }
            imageView3.setAlpha(f);
            this.imageView.setScaleX(i == 2 ? 1.0f : 0.1f);
            imageView3 = this.imageView;
            if (i == 2) {
                f2 = 1.0f;
            }
            imageView3.setScaleY(f2);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.currentType != 0) {
                return super.onTouchEvent(motionEvent);
            }
            boolean z = ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
            return z;
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            i2 = MeasureSpec.getSize(i);
            setMeasuredDimension(i2, AndroidUtilities.dp(48.0f));
            this.textView.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            this.buttonWidth = this.textView.getMeasuredWidth();
            this.progressView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            this.imageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            TL_pageBlockChannel tL_pageBlockChannel = this.currentBlock;
            if (tL_pageBlockChannel != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, tL_pageBlockChannel.channel.title, null, (i2 - AndroidUtilities.dp(52.0f)) - this.buttonWidth, this.textY, this.currentBlock, StaticLayoutEx.ALIGN_LEFT(), this.parentAdapter);
                if (this.parentAdapter.isRtl) {
                    this.textX2 = this.textX;
                } else {
                    this.textX2 = (getMeasuredWidth() - this.textX) - this.buttonWidth;
                }
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            this.imageView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, (this.textX2 + (this.buttonWidth / 2)) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            this.progressView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, (this.textX2 + (this.buttonWidth / 2)) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            TextView textView = this.textView;
            i = this.textX2;
            textView.layout(i, 0, textView.getMeasuredWidth() + i, this.textView.getMeasuredHeight());
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(39.0f), this.backgroundPaint);
                DrawingText drawingText = this.textLayout;
                if (drawingText != null && drawingText.getLineCount() > 0) {
                    canvas.save();
                    if (this.parentAdapter.isRtl) {
                        canvas.translate((((float) getMeasuredWidth()) - this.textLayout.getLineWidth(0)) - ((float) this.textX), (float) this.textY);
                    } else {
                        canvas.translate((float) this.textX, (float) this.textY);
                    }
                    if (this.currentType == 0) {
                        ArticleViewer.this.drawTextSelection(canvas, this);
                    }
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockCollageCell extends FrameLayout implements ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockCollage currentBlock;
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

                public MessageGroupedLayoutAttempt(int i, int i2, float f, float f2) {
                    this.lineCounts = new int[]{i, i2};
                    this.heights = new float[]{f, f2};
                }

                public MessageGroupedLayoutAttempt(int i, int i2, int i3, float f, float f2, float f3) {
                    this.lineCounts = new int[]{i, i2, i3};
                    this.heights = new float[]{f, f2, f3};
                }

                public MessageGroupedLayoutAttempt(int i, int i2, int i3, int i4, float f, float f2, float f3, float f4) {
                    this.lineCounts = new int[]{i, i2, i3, i4};
                    this.heights = new float[]{f, f2, f3, f4};
                }
            }

            private float multiHeight(float[] fArr, int i, int i2) {
                float f = 0.0f;
                while (i < i2) {
                    f += fArr[i];
                    i++;
                }
                return ((float) this.maxSizeWidth) / f;
            }

            public void calculate() {
                this.posArray.clear();
                this.positions.clear();
                int size = BlockCollageCell.this.currentBlock.items.size();
                if (size > 1) {
                    GroupedMessagePosition groupedMessagePosition;
                    float f;
                    int i;
                    int i2;
                    GroupedMessagePosition groupedMessagePosition2;
                    StringBuilder stringBuilder = new StringBuilder();
                    int i3 = false;
                    this.hasSibling = false;
                    int i4 = 0;
                    float f2 = 1.0f;
                    Object obj = null;
                    while (i4 < size) {
                        PhotoSize closestPhotoSizeWithSize;
                        TLObject tLObject = (TLObject) BlockCollageCell.this.currentBlock.items.get(i4);
                        if (tLObject instanceof TL_pageBlockPhoto) {
                            Photo access$15300 = ArticleViewer.this.getPhotoWithId(((TL_pageBlockPhoto) tLObject).photo_id);
                            if (access$15300 == null) {
                                i4++;
                            } else {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(access$15300.sizes, AndroidUtilities.getPhotoSize());
                            }
                        } else {
                            if (tLObject instanceof TL_pageBlockVideo) {
                                Document access$13300 = ArticleViewer.this.getDocumentWithId(((TL_pageBlockVideo) tLObject).video_id);
                                if (access$13300 != null) {
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(access$13300.thumbs, 90);
                                }
                            }
                            i4++;
                        }
                        groupedMessagePosition = new GroupedMessagePosition();
                        groupedMessagePosition.last = i4 == size + -1;
                        if (closestPhotoSizeWithSize == null) {
                            f = 1.0f;
                        } else {
                            f = ((float) closestPhotoSizeWithSize.w) / ((float) closestPhotoSizeWithSize.h);
                        }
                        groupedMessagePosition.aspectRatio = f;
                        f = groupedMessagePosition.aspectRatio;
                        if (f > 1.2f) {
                            stringBuilder.append("w");
                        } else if (f < 0.8f) {
                            stringBuilder.append("n");
                        } else {
                            stringBuilder.append("q");
                        }
                        f = groupedMessagePosition.aspectRatio;
                        f2 += f;
                        if (f > 2.0f) {
                            obj = 1;
                        }
                        this.positions.put(tLObject, groupedMessagePosition);
                        this.posArray.add(groupedMessagePosition);
                        i4++;
                    }
                    int dp = AndroidUtilities.dp(120.0f);
                    float dp2 = (float) AndroidUtilities.dp(120.0f);
                    Point point = AndroidUtilities.displaySize;
                    int min = (int) (dp2 / (((float) Math.min(point.x, point.y)) / ((float) this.maxSizeWidth)));
                    dp2 = (float) AndroidUtilities.dp(40.0f);
                    point = AndroidUtilities.displaySize;
                    f = (float) Math.min(point.x, point.y);
                    int i5 = this.maxSizeWidth;
                    i4 = (int) (dp2 / (f / ((float) i5)));
                    f = ((float) i5) / 814.0f;
                    float f3 = f2 / ((float) size);
                    int i6 = 3;
                    int length;
                    int i7;
                    float f4;
                    float f5;
                    int i8;
                    float f6;
                    float abs;
                    float f7;
                    GroupedMessagePosition groupedMessagePosition3;
                    if (obj != null || (size != 2 && size != 3 && size != 4)) {
                        int i9;
                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt;
                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt2;
                        int i10;
                        int i11;
                        float[] fArr = new float[this.posArray.size()];
                        for (i = 0; i < size; i++) {
                            if (f3 > 1.1f) {
                                fArr[i] = Math.max(1.0f, ((GroupedMessagePosition) this.posArray.get(i)).aspectRatio);
                            } else {
                                fArr[i] = Math.min(1.0f, ((GroupedMessagePosition) this.posArray.get(i)).aspectRatio);
                            }
                            fArr[i] = Math.max(0.66667f, Math.min(1.7f, fArr[i]));
                        }
                        ArrayList arrayList = new ArrayList();
                        for (i9 = 1; i9 < fArr.length; i9++) {
                            length = fArr.length - i9;
                            if (i9 <= 3 && length <= 3) {
                                arrayList.add(new MessageGroupedLayoutAttempt(i9, length, multiHeight(fArr, 0, i9), multiHeight(fArr, i9, fArr.length)));
                            }
                        }
                        i3 = 1;
                        while (i3 < fArr.length - 1) {
                            i9 = 1;
                            while (i9 < fArr.length - i3) {
                                length = (fArr.length - i3) - i9;
                                if (i3 <= i6) {
                                    if (i9 <= (f3 < 0.85f ? 4 : 3) && length <= i6) {
                                        i = i3 + i9;
                                        messageGroupedLayoutAttempt = messageGroupedLayoutAttempt2;
                                        i10 = i9;
                                        messageGroupedLayoutAttempt2 = new MessageGroupedLayoutAttempt(i3, i9, length, multiHeight(fArr, 0, i3), multiHeight(fArr, i3, i), multiHeight(fArr, i, fArr.length));
                                        arrayList.add(messageGroupedLayoutAttempt);
                                        i9 = i10 + 1;
                                        i6 = 3;
                                    }
                                }
                                i10 = i9;
                                i9 = i10 + 1;
                                i6 = 3;
                            }
                            i3++;
                            i6 = 3;
                        }
                        i3 = 1;
                        while (i3 < fArr.length - 2) {
                            i11 = 1;
                            while (i11 < fArr.length - i3) {
                                i5 = 1;
                                while (i5 < (fArr.length - i3) - i11) {
                                    int i12;
                                    dp = ((fArr.length - i3) - i11) - i5;
                                    if (i3 > 3 || i11 > 3 || i5 > 3 || dp > 3) {
                                        i10 = i5;
                                        i12 = i11;
                                        i2 = size;
                                        size = min;
                                    } else {
                                        i = i3 + i11;
                                        i7 = i + i5;
                                        messageGroupedLayoutAttempt = messageGroupedLayoutAttempt2;
                                        f = multiHeight(fArr, 0, i3);
                                        i10 = i5;
                                        i12 = i11;
                                        i2 = size;
                                        size = min;
                                        messageGroupedLayoutAttempt2 = new MessageGroupedLayoutAttempt(i3, i11, i5, dp, f, multiHeight(fArr, i3, i), multiHeight(fArr, i, i7), multiHeight(fArr, i7, fArr.length));
                                        arrayList.add(messageGroupedLayoutAttempt);
                                    }
                                    i5 = i10 + 1;
                                    min = size;
                                    i11 = i12;
                                    size = i2;
                                }
                                i2 = size;
                                size = min;
                                i11++;
                                size = i2;
                            }
                            i2 = size;
                            size = min;
                            i3++;
                            size = i2;
                        }
                        i2 = size;
                        size = min;
                        f4 = (float) ((this.maxSizeWidth / 3) * 4);
                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt3 = null;
                        f5 = 0.0f;
                        for (i8 = 0; i8 < arrayList.size(); i8++) {
                            MessageGroupedLayoutAttempt messageGroupedLayoutAttempt4 = (MessageGroupedLayoutAttempt) arrayList.get(i8);
                            i5 = 0;
                            f3 = 0.0f;
                            f6 = Float.MAX_VALUE;
                            while (true) {
                                float[] fArr2 = messageGroupedLayoutAttempt4.heights;
                                if (i5 >= fArr2.length) {
                                    break;
                                }
                                f3 += fArr2[i5];
                                if (fArr2[i5] < f6) {
                                    f6 = fArr2[i5];
                                }
                                i5++;
                            }
                            abs = Math.abs(f3 - f4);
                            int[] iArr = messageGroupedLayoutAttempt4.lineCounts;
                            if (iArr.length > 1) {
                                if (iArr[0] <= iArr[1]) {
                                    if (iArr.length <= 2 || iArr[1] <= iArr[2]) {
                                        int[] iArr2 = messageGroupedLayoutAttempt4.lineCounts;
                                        if (iArr2.length <= 3 || iArr2[2] <= iArr2[3]) {
                                        }
                                    } else {
                                        f7 = 1.2f;
                                        abs *= f7;
                                    }
                                }
                                f7 = 1.2f;
                                abs *= f7;
                            }
                            if (f6 < ((float) size)) {
                                abs *= 1.5f;
                            }
                            if (messageGroupedLayoutAttempt3 == null || abs < f5) {
                                messageGroupedLayoutAttempt3 = messageGroupedLayoutAttempt4;
                                f5 = abs;
                            }
                        }
                        i3 = false;
                        if (messageGroupedLayoutAttempt3 != null) {
                            i = 0;
                            i7 = 0;
                            while (true) {
                                int[] iArr3 = messageGroupedLayoutAttempt3.lineCounts;
                                if (i >= iArr3.length) {
                                    break;
                                }
                                i4 = iArr3[i];
                                f2 = messageGroupedLayoutAttempt3.heights[i];
                                i5 = i7;
                                i9 = this.maxSizeWidth;
                                groupedMessagePosition3 = null;
                                for (i7 = 0; i7 < i4; i7++) {
                                    int i13;
                                    i11 = (int) (fArr[i5] * f2);
                                    i9 -= i11;
                                    GroupedMessagePosition groupedMessagePosition4 = (GroupedMessagePosition) this.posArray.get(i5);
                                    min = i == 0 ? 4 : 0;
                                    if (i == messageGroupedLayoutAttempt3.lineCounts.length - 1) {
                                        min |= 8;
                                    }
                                    if (i7 == 0) {
                                        min |= 1;
                                    }
                                    if (i7 == i4 - 1) {
                                        i13 = min | 2;
                                        groupedMessagePosition3 = groupedMessagePosition4;
                                    } else {
                                        i13 = min;
                                    }
                                    groupedMessagePosition4.set(i7, i7, i, i, i11, f2 / 814.0f, i13);
                                    i5++;
                                }
                                groupedMessagePosition3.pw += i9;
                                groupedMessagePosition3.spanSize += i9;
                                i++;
                                i7 = i5;
                            }
                        } else {
                            return;
                        }
                    }
                    GroupedMessagePosition groupedMessagePosition5;
                    int i14;
                    float min2;
                    if (size == 2) {
                        float f8;
                        GroupedMessagePosition groupedMessagePosition6 = (GroupedMessagePosition) this.posArray.get(0);
                        GroupedMessagePosition groupedMessagePosition7 = (GroupedMessagePosition) this.posArray.get(1);
                        String stringBuilder2 = stringBuilder.toString();
                        String str = "ww";
                        if (stringBuilder2.equals(str)) {
                            double d = (double) f3;
                            double d2 = (double) f;
                            Double.isNaN(d2);
                            if (d > d2 * 1.4d) {
                                f2 = groupedMessagePosition6.aspectRatio;
                                f8 = groupedMessagePosition7.aspectRatio;
                                if (((double) (f2 - f8)) < 0.2d) {
                                    i = this.maxSizeWidth;
                                    f4 = ((float) Math.round(Math.min(((float) i) / f2, Math.min(((float) i) / f8, 407.0f)))) / 814.0f;
                                    groupedMessagePosition6.set(0, 0, 0, 0, this.maxSizeWidth, f4, 7);
                                    groupedMessagePosition7.set(0, 0, 1, 1, this.maxSizeWidth, f4, 11);
                                }
                            }
                        }
                        if (stringBuilder2.equals(str) || stringBuilder2.equals("qq")) {
                            i = this.maxSizeWidth / 2;
                            f7 = (float) i;
                            f7 = ((float) Math.round(Math.min(f7 / groupedMessagePosition6.aspectRatio, Math.min(f7 / groupedMessagePosition7.aspectRatio, 814.0f)))) / 814.0f;
                            groupedMessagePosition6.set(0, 0, 0, 0, i, f7, 13);
                            groupedMessagePosition7.set(1, 1, 0, 0, i, f7, 14);
                        } else {
                            i = this.maxSizeWidth;
                            f2 = ((float) i) * 0.4f;
                            f4 = (float) i;
                            f8 = groupedMessagePosition6.aspectRatio;
                            i = (int) Math.max(f2, (float) Math.round((f4 / f8) / ((1.0f / f8) + (1.0f / groupedMessagePosition7.aspectRatio))));
                            i7 = this.maxSizeWidth - i;
                            if (i7 < min) {
                                i -= min - i7;
                                i7 = min;
                            }
                            f2 = Math.min(814.0f, (float) Math.round(Math.min(((float) i7) / groupedMessagePosition6.aspectRatio, ((float) i) / groupedMessagePosition7.aspectRatio))) / 814.0f;
                            groupedMessagePosition6.set(0, 0, 0, 0, i7, f2, 13);
                            groupedMessagePosition7.set(1, 1, 0, 0, i, f2, 14);
                        }
                    } else if (size == 3) {
                        groupedMessagePosition2 = (GroupedMessagePosition) this.posArray.get(0);
                        groupedMessagePosition5 = (GroupedMessagePosition) this.posArray.get(1);
                        groupedMessagePosition3 = (GroupedMessagePosition) this.posArray.get(2);
                        float f9;
                        if (stringBuilder.charAt(0) == 'n') {
                            f4 = groupedMessagePosition5.aspectRatio;
                            f4 = Math.min(407.0f, (float) Math.round((((float) this.maxSizeWidth) * f4) / (groupedMessagePosition3.aspectRatio + f4)));
                            f9 = 814.0f - f4;
                            i8 = (int) Math.max((float) min, Math.min(((float) this.maxSizeWidth) * 0.5f, (float) Math.round(Math.min(groupedMessagePosition3.aspectRatio * f4, groupedMessagePosition5.aspectRatio * f9))));
                            i4 = Math.round(Math.min((groupedMessagePosition2.aspectRatio * 814.0f) + ((float) i4), (float) (this.maxSizeWidth - i8)));
                            groupedMessagePosition2.set(0, 0, 0, 1, i4, 1.0f, 13);
                            i14 = i8;
                            groupedMessagePosition5.set(1, 1, 0, 0, i14, f9 / 814.0f, 6);
                            groupedMessagePosition3.set(0, 1, 1, 1, i14, f4 / 814.0f, 10);
                            i8 = this.maxSizeWidth;
                            groupedMessagePosition3.spanSize = i8;
                            groupedMessagePosition2.siblingHeights = new float[]{f4, abs};
                            groupedMessagePosition5.spanSize = i8 - i4;
                            groupedMessagePosition3.leftSpanOffset = i4;
                            this.hasSibling = true;
                        } else {
                            f4 = ((float) Math.round(Math.min(((float) this.maxSizeWidth) / groupedMessagePosition2.aspectRatio, 537.24005f))) / 814.0f;
                            groupedMessagePosition2.set(0, 1, 0, 0, this.maxSizeWidth, f4, 7);
                            i7 = this.maxSizeWidth / 2;
                            f9 = 814.0f - f4;
                            f4 = (float) i7;
                            i14 = i7;
                            min2 = Math.min(f9, (float) Math.round(Math.min(f4 / groupedMessagePosition5.aspectRatio, f4 / groupedMessagePosition3.aspectRatio))) / 814.0f;
                            groupedMessagePosition5.set(0, 0, 1, 1, i14, min2, 9);
                            groupedMessagePosition3.set(1, 1, 1, 1, i14, min2, 10);
                        }
                    } else if (size == 4) {
                        groupedMessagePosition5 = (GroupedMessagePosition) this.posArray.get(0);
                        GroupedMessagePosition groupedMessagePosition8 = (GroupedMessagePosition) this.posArray.get(1);
                        groupedMessagePosition = (GroupedMessagePosition) this.posArray.get(2);
                        GroupedMessagePosition groupedMessagePosition9 = (GroupedMessagePosition) this.posArray.get(3);
                        if (stringBuilder.charAt(0) == 'w') {
                            f4 = ((float) Math.round(Math.min(((float) this.maxSizeWidth) / groupedMessagePosition5.aspectRatio, 537.24005f))) / 814.0f;
                            groupedMessagePosition5.set(0, 2, 0, 0, this.maxSizeWidth, f4, 7);
                            f7 = (float) Math.round(((float) this.maxSizeWidth) / ((groupedMessagePosition8.aspectRatio + groupedMessagePosition.aspectRatio) + groupedMessagePosition9.aspectRatio));
                            dp2 = (float) min;
                            i8 = (int) Math.max(dp2, Math.min(((float) this.maxSizeWidth) * 0.4f, groupedMessagePosition8.aspectRatio * f7));
                            i4 = (int) Math.max(Math.max(dp2, ((float) this.maxSizeWidth) * 0.33f), groupedMessagePosition9.aspectRatio * f7);
                            length = (this.maxSizeWidth - i8) - i4;
                            min2 = Math.min(814.0f - f4, f7) / 814.0f;
                            groupedMessagePosition8.set(0, 0, 1, 1, i8, min2, 9);
                            groupedMessagePosition.set(1, 1, 1, 1, length, min2, 8);
                            groupedMessagePosition9.set(2, 2, 1, 1, i4, min2, 10);
                        } else {
                            i = Math.max(min, Math.round(814.0f / (((1.0f / groupedMessagePosition8.aspectRatio) + (1.0f / groupedMessagePosition.aspectRatio)) + (1.0f / ((GroupedMessagePosition) this.posArray.get(3)).aspectRatio))));
                            f5 = (float) dp;
                            f6 = (float) i;
                            float min3 = Math.min(0.33f, Math.max(f5, f6 / groupedMessagePosition8.aspectRatio) / 814.0f);
                            f5 = Math.min(0.33f, Math.max(f5, f6 / groupedMessagePosition.aspectRatio) / 814.0f);
                            f7 = (1.0f - min3) - f5;
                            i4 = Math.round(Math.min((814.0f * groupedMessagePosition5.aspectRatio) + ((float) i4), (float) (this.maxSizeWidth - i)));
                            groupedMessagePosition5.set(0, 0, 0, 2, i4, (min3 + f5) + f7, 13);
                            i14 = i;
                            groupedMessagePosition8.set(1, 1, 0, 0, i14, min3, 6);
                            groupedMessagePosition.set(0, 1, 1, 1, i14, f5, 2);
                            groupedMessagePosition.spanSize = this.maxSizeWidth;
                            groupedMessagePosition9.set(0, 1, 2, 2, i14, f7, 10);
                            i = this.maxSizeWidth;
                            groupedMessagePosition9.spanSize = i;
                            groupedMessagePosition8.spanSize = i - i4;
                            groupedMessagePosition.leftSpanOffset = i4;
                            groupedMessagePosition9.leftSpanOffset = i4;
                            groupedMessagePosition5.siblingHeights = new float[]{min3, f5, f7};
                            this.hasSibling = true;
                        }
                        i2 = size;
                        i3 = false;
                    }
                    i2 = size;
                    i = i2;
                    for (i3 = 
/*
Method generation error in method: org.telegram.ui.ArticleViewer.BlockCollageCell.GroupedMessages.calculate():void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r14_15 'i3' int) = (r14_2 'i3' int), (r14_0 'i3' int), (r14_14 'i3' int) binds: {(r14_2 'i3' int)=B:71:0x04f4, (r14_0 'i3' int)=B:72:0x04f9, (r14_14 'i3' int)=B:218:0x0740} in method: org.telegram.ui.ArticleViewer.BlockCollageCell.GroupedMessages.calculate():void, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:228)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:185)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:321)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:259)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:221)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.addInnerClasses(ClassGen.java:234)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.addInnerClasses(ClassGen.java:234)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:77)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:10)
	at jadx.core.ProcessClass.process(ProcessClass.java:38)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:539)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:511)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:222)
	... 28 more

*/
        }

        public BlockCollageCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            this.innerListView = new RecyclerListView(context, ArticleViewer.this) {
                public void requestLayout() {
                    if (!BlockCollageCell.this.inLayout) {
                        super.requestLayout();
                    }
                }
            };
            this.innerListView.addItemDecoration(new ItemDecoration(ArticleViewer.this) {
                public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
                    int i = 0;
                    rect.bottom = 0;
                    GroupedMessagePosition groupedMessagePosition = view instanceof BlockPhotoCell ? (GroupedMessagePosition) BlockCollageCell.this.group.positions.get(((BlockPhotoCell) view).currentBlock) : view instanceof BlockVideoCell ? (GroupedMessagePosition) BlockCollageCell.this.group.positions.get(((BlockVideoCell) view).currentBlock) : null;
                    if (groupedMessagePosition != null && groupedMessagePosition.siblingHeights != null) {
                        Point point = AndroidUtilities.displaySize;
                        float max = ((float) Math.max(point.x, point.y)) * 0.5f;
                        int i2 = 0;
                        int i3 = 0;
                        while (true) {
                            float[] fArr = groupedMessagePosition.siblingHeights;
                            if (i2 >= fArr.length) {
                                break;
                            }
                            i3 += (int) Math.ceil((double) (fArr[i2] * max));
                            i2++;
                        }
                        i3 += (groupedMessagePosition.maxY - groupedMessagePosition.minY) * AndroidUtilities.dp2(11.0f);
                        i2 = BlockCollageCell.this.group.posArray.size();
                        while (i < i2) {
                            GroupedMessagePosition groupedMessagePosition2 = (GroupedMessagePosition) BlockCollageCell.this.group.posArray.get(i);
                            byte b = groupedMessagePosition2.minY;
                            byte b2 = groupedMessagePosition.minY;
                            if (b == b2 && ((groupedMessagePosition2.minX != groupedMessagePosition.minX || groupedMessagePosition2.maxX != groupedMessagePosition.maxX || b != b2 || groupedMessagePosition2.maxY != groupedMessagePosition.maxY) && groupedMessagePosition2.minY == groupedMessagePosition.minY)) {
                                i3 -= ((int) Math.ceil((double) (max * groupedMessagePosition2.ph))) - AndroidUtilities.dp(4.0f);
                                break;
                            }
                            i++;
                        }
                        rect.bottom = -i3;
                    }
                }
            });
            final ArticleViewer articleViewer = ArticleViewer.this;
            AnonymousClass3 anonymousClass3 = new GridLayoutManagerFixed(context, 1000, 1, true) {
                public boolean shouldLayoutChildFromOpositeSide(View view) {
                    return false;
                }

                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                /* Access modifiers changed, original: protected */
                public boolean hasSiblingChild(int i) {
                    GroupedMessagePosition groupedMessagePosition = (GroupedMessagePosition) BlockCollageCell.this.group.positions.get((TLObject) BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - i) - 1));
                    if (groupedMessagePosition.minX != groupedMessagePosition.maxX) {
                        byte b = groupedMessagePosition.minY;
                        if (b == groupedMessagePosition.maxY && b != (byte) 0) {
                            int size = BlockCollageCell.this.group.posArray.size();
                            for (int i2 = 0; i2 < size; i2++) {
                                GroupedMessagePosition groupedMessagePosition2 = (GroupedMessagePosition) BlockCollageCell.this.group.posArray.get(i2);
                                if (groupedMessagePosition2 != groupedMessagePosition) {
                                    byte b2 = groupedMessagePosition2.minY;
                                    byte b3 = groupedMessagePosition.minY;
                                    if (b2 <= b3 && groupedMessagePosition2.maxY >= b3) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    return false;
                }
            };
            anonymousClass3.setSpanSizeLookup(new SpanSizeLookup(ArticleViewer.this) {
                public int getSpanSize(int i) {
                    return ((GroupedMessagePosition) BlockCollageCell.this.group.positions.get((TLObject) BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - i) - 1))).spanSize;
                }
            });
            this.innerListView.setLayoutManager(anonymousClass3);
            RecyclerListView recyclerListView = this.innerListView;
            AnonymousClass5 anonymousClass5 = new Adapter(ArticleViewer.this) {
                public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View blockVideoCell;
                    BlockCollageCell blockCollageCell;
                    if (i != 0) {
                        blockCollageCell = BlockCollageCell.this;
                        blockVideoCell = new BlockVideoCell(blockCollageCell.getContext(), BlockCollageCell.this.parentAdapter, 2);
                    } else {
                        blockCollageCell = BlockCollageCell.this;
                        blockVideoCell = new BlockPhotoCell(blockCollageCell.getContext(), BlockCollageCell.this.parentAdapter, 2);
                    }
                    return new Holder(blockVideoCell);
                }

                public void onBindViewHolder(ViewHolder viewHolder, int i) {
                    PageBlock pageBlock = (PageBlock) BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - i) - 1);
                    if (viewHolder.getItemViewType() != 0) {
                        BlockVideoCell blockVideoCell = (BlockVideoCell) viewHolder.itemView;
                        blockVideoCell.groupPosition = (GroupedMessagePosition) BlockCollageCell.this.group.positions.get(pageBlock);
                        blockVideoCell.setBlock((TL_pageBlockVideo) pageBlock, true, true);
                        return;
                    }
                    BlockPhotoCell blockPhotoCell = (BlockPhotoCell) viewHolder.itemView;
                    blockPhotoCell.groupPosition = (GroupedMessagePosition) BlockCollageCell.this.group.positions.get(pageBlock);
                    blockPhotoCell.setBlock((TL_pageBlockPhoto) pageBlock, true, true);
                }

                public int getItemCount() {
                    if (BlockCollageCell.this.currentBlock == null) {
                        return 0;
                    }
                    return BlockCollageCell.this.currentBlock.items.size();
                }

                public int getItemViewType(int i) {
                    if (((PageBlock) BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - i) - 1)) instanceof TL_pageBlockPhoto) {
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

        public void setBlock(TL_pageBlockCollage tL_pageBlockCollage) {
            if (this.currentBlock != tL_pageBlockCollage) {
                this.currentBlock = tL_pageBlockCollage;
                this.group.calculate();
            }
            this.innerAdapter.notifyDataSetChanged();
            this.innerListView.setGlowColor(Theme.getColor("windowBackgroundWhite"));
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.captionLayout, this.textX, this.textY)) {
                if (!(ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(motionEvent))) {
                    return false;
                }
            }
            return true;
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            i2 = 1;
            this.inLayout = true;
            i = MeasureSpec.getSize(i);
            TL_pageBlockCollage tL_pageBlockCollage = this.currentBlock;
            if (tL_pageBlockCollage != null) {
                int i3;
                i2 = tL_pageBlockCollage.level;
                if (i2 > 0) {
                    i2 = AndroidUtilities.dp((float) (i2 * 14)) + AndroidUtilities.dp(18.0f);
                    this.listX = i2;
                    this.textX = i2;
                    i2 = i - (this.listX + AndroidUtilities.dp(18.0f));
                    i3 = i2;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    i3 = i - AndroidUtilities.dp(36.0f);
                    i2 = i;
                }
                this.innerListView.measure(MeasureSpec.makeMeasureSpec(i2, NUM), MeasureSpec.makeMeasureSpec(0, 0));
                i2 = this.innerListView.getMeasuredHeight();
                this.textY = AndroidUtilities.dp(8.0f) + i2;
                ArticleViewer articleViewer = ArticleViewer.this;
                TL_pageBlockCollage tL_pageBlockCollage2 = this.currentBlock;
                this.captionLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockCollage2.caption.text, i3, this.textY, tL_pageBlockCollage2, this.parentAdapter);
                if (this.captionLayout != null) {
                    this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    i2 += this.creditOffset + AndroidUtilities.dp(4.0f);
                    DrawingText drawingText = this.captionLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                } else {
                    this.creditOffset = 0;
                }
                articleViewer = ArticleViewer.this;
                tL_pageBlockCollage2 = this.currentBlock;
                this.creditLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockCollage2.caption.credit, i3, this.textY + this.creditOffset, tL_pageBlockCollage2, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.creditLayout != null) {
                    i2 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    DrawingText drawingText2 = this.creditLayout;
                    drawingText2.x = this.textX;
                    drawingText2.y = this.textY + this.creditOffset;
                }
                i2 += AndroidUtilities.dp(16.0f);
                tL_pageBlockCollage = this.currentBlock;
                if (tL_pageBlockCollage.level > 0 && !tL_pageBlockCollage.bottom) {
                    i2 += AndroidUtilities.dp(8.0f);
                }
            }
            setMeasuredDimension(i, i2);
            this.inLayout = false;
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            this.innerListView.layout(this.listX, AndroidUtilities.dp(8.0f), this.listX + this.innerListView.getMeasuredWidth(), this.innerListView.getMeasuredHeight() + AndroidUtilities.dp(8.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int i;
                int i2 = 0;
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    i = 1;
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                } else {
                    i = 0;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, i);
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    float dp = (float) AndroidUtilities.dp(18.0f);
                    float dp2 = (float) AndroidUtilities.dp(20.0f);
                    int measuredHeight = getMeasuredHeight();
                    if (this.currentBlock.bottom) {
                        i2 = AndroidUtilities.dp(6.0f);
                    }
                    canvas.drawRect(dp, 0.0f, dp2, (float) (measuredHeight - i2), ArticleViewer.quoteLinePaint);
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.creditLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockDetailsCell extends View implements Callback, ArticleSelectableView {
        private AnimatedArrowDrawable arrow;
        private TL_pageBlockDetails currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(50.0f);
        private int textY = (AndroidUtilities.dp(11.0f) + 1);

        public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        }

        public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        }

        public BlockDetailsCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            this.arrow = new AnimatedArrowDrawable(ArticleViewer.this.getGrayTextColor(), true);
        }

        public void invalidateDrawable(Drawable drawable) {
            invalidate();
        }

        public void setBlock(TL_pageBlockDetails tL_pageBlockDetails) {
            this.currentBlock = tL_pageBlockDetails;
            this.arrow.setAnimationProgress(tL_pageBlockDetails.open ? 0.0f : 1.0f);
            this.arrow.setCallback(this);
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            i2 = AndroidUtilities.dp(39.0f);
            TL_pageBlockDetails tL_pageBlockDetails = this.currentBlock;
            if (tL_pageBlockDetails != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, tL_pageBlockDetails.title, i - AndroidUtilities.dp(52.0f), 0, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    i2 = Math.max(i2, AndroidUtilities.dp(21.0f) + this.textLayout.getHeight());
                    this.textY = ((this.textLayout.getHeight() + AndroidUtilities.dp(21.0f)) - this.textLayout.getHeight()) / 2;
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            }
            setMeasuredDimension(i, i2 + 1);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(18.0f), (float) (((getMeasuredHeight() - AndroidUtilities.dp(13.0f)) - 1) / 2));
                this.arrow.draw(canvas);
                canvas.restore();
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                float measuredHeight = (float) (getMeasuredHeight() - 1);
                canvas.drawLine(0.0f, measuredHeight, (float) getMeasuredWidth(), measuredHeight, ArticleViewer.dividerPaint);
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockEmbedCell extends FrameLayout implements ArticleSelectableView {
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

            /* synthetic */ TelegramWebviewProxy(BlockEmbedCell blockEmbedCell, AnonymousClass1 anonymousClass1) {
                this();
            }

            @JavascriptInterface
            public void postEvent(String str, String str2) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$9fT-04iuc3H2EZHCwJyjCPpU95o(this, str, str2));
            }

            public /* synthetic */ void lambda$postEvent$0$ArticleViewer$BlockEmbedCell$TelegramWebviewProxy(String str, String str2) {
                if ("resize_frame".equals(str)) {
                    try {
                        BlockEmbedCell.this.exactWebViewHeight = Utilities.parseInt(new JSONObject(str2).getString("height")).intValue();
                        BlockEmbedCell.this.requestLayout();
                    } catch (Throwable unused) {
                    }
                }
            }
        }

        public class TouchyWebView extends WebView {
            public TouchyWebView(Context context) {
                super(context);
                setFocusable(false);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                BlockEmbedCell.this.wasUserInteraction = true;
                if (BlockEmbedCell.this.currentBlock != null) {
                    if (BlockEmbedCell.this.currentBlock.allow_scrolling) {
                        requestDisallowInterceptTouchEvent(true);
                    } else {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                }
                return super.onTouchEvent(motionEvent);
            }
        }

        @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
        public BlockEmbedCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            setWillNotDraw(false);
            this.videoView = new WebPlayerView(context, false, false, new WebPlayerViewDelegate(ArticleViewer.this) {
                public boolean checkInlinePermissions() {
                    return false;
                }

                public ViewGroup getTextureViewContainer() {
                    return null;
                }

                public void onInlineSurfaceTextureReady() {
                }

                public TextureView onSwitchInlineMode(View view, boolean z, float f, int i, boolean z2) {
                    return null;
                }

                public void prepareToSwitchInlineMode(boolean z, Runnable runnable, float f, boolean z2) {
                }

                public void onInitFailed() {
                    BlockEmbedCell.this.webView.setVisibility(0);
                    BlockEmbedCell.this.videoView.setVisibility(4);
                    BlockEmbedCell.this.videoView.loadVideo(null, null, null, null, false);
                    HashMap hashMap = new HashMap();
                    hashMap.put("Referer", "http://youtube.com");
                    BlockEmbedCell.this.webView.loadUrl(BlockEmbedCell.this.currentBlock.url, hashMap);
                }

                public void onVideoSizeChanged(float f, int i) {
                    ArticleViewer.this.fullscreenAspectRatioView.setAspectRatio(f, i);
                }

                public TextureView onSwitchToFullscreen(View view, boolean z, float f, int i, boolean z2) {
                    if (z) {
                        ArticleViewer.this.fullscreenAspectRatioView.addView(ArticleViewer.this.fullscreenTextureView, LayoutHelper.createFrame(-1, -1.0f));
                        ArticleViewer.this.fullscreenAspectRatioView.setVisibility(0);
                        ArticleViewer.this.fullscreenAspectRatioView.setAspectRatio(f, i);
                        BlockEmbedCell blockEmbedCell = BlockEmbedCell.this;
                        ArticleViewer.this.fullscreenedVideo = blockEmbedCell.videoView;
                        ArticleViewer.this.fullscreenVideoContainer.addView(view, LayoutHelper.createFrame(-1, -1.0f));
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(0);
                    } else {
                        ArticleViewer.this.fullscreenAspectRatioView.removeView(ArticleViewer.this.fullscreenTextureView);
                        ArticleViewer.this.fullscreenedVideo = null;
                        ArticleViewer.this.fullscreenAspectRatioView.setVisibility(8);
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(4);
                    }
                    return ArticleViewer.this.fullscreenTextureView;
                }

                public void onSharePressed() {
                    if (ArticleViewer.this.parentActivity != null) {
                        ArticleViewer articleViewer = ArticleViewer.this;
                        articleViewer.showDialog(new ShareAlert(articleViewer.parentActivity, null, BlockEmbedCell.this.currentBlock.url, false, BlockEmbedCell.this.currentBlock.url, false));
                    }
                }

                public void onPlayStateChanged(WebPlayerView webPlayerView, boolean z) {
                    if (z) {
                        if (!(ArticleViewer.this.currentPlayingVideo == null || ArticleViewer.this.currentPlayingVideo == webPlayerView)) {
                            ArticleViewer.this.currentPlayingVideo.pause();
                        }
                        ArticleViewer.this.currentPlayingVideo = webPlayerView;
                        try {
                            ArticleViewer.this.parentActivity.getWindow().addFlags(128);
                            return;
                        } catch (Exception e) {
                            FileLog.e(e);
                            return;
                        }
                    }
                    if (ArticleViewer.this.currentPlayingVideo == webPlayerView) {
                        ArticleViewer.this.currentPlayingVideo = null;
                    }
                    try {
                        ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
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
                public void onShowCustomView(View view, int i, CustomViewCallback customViewCallback) {
                    onShowCustomView(view, customViewCallback);
                }

                public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
                    if (ArticleViewer.this.customView != null) {
                        customViewCallback.onCustomViewHidden();
                        return;
                    }
                    ArticleViewer.this.customView = view;
                    ArticleViewer.this.customViewCallback = customViewCallback;
                    AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$BlockEmbedCell$2$BQ0m5DiCWR6D1N-jvzFvaMkwwdA(this), 100);
                }

                public /* synthetic */ void lambda$onShowCustomView$0$ArticleViewer$BlockEmbedCell$2() {
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
                public void onLoadResource(WebView webView, String str) {
                    super.onLoadResource(webView, str);
                }

                public void onPageFinished(WebView webView, String str) {
                    super.onPageFinished(webView, str);
                }

                public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                    if (!BlockEmbedCell.this.wasUserInteraction) {
                        return false;
                    }
                    Browser.openUrl(ArticleViewer.this.parentActivity, str);
                    return true;
                }
            });
            addView(this.webView);
        }

        public void destroyWebView(boolean z) {
            try {
                this.webView.stopLoading();
                this.webView.loadUrl("about:blank");
                if (z) {
                    this.webView.destroy();
                }
                this.currentBlock = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.videoView.destroy();
        }

        public void setBlock(TL_pageBlockEmbed tL_pageBlockEmbed) {
            String str = "about:blank";
            TL_pageBlockEmbed tL_pageBlockEmbed2 = this.currentBlock;
            this.currentBlock = tL_pageBlockEmbed;
            this.webView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            TL_pageBlockEmbed tL_pageBlockEmbed3 = this.currentBlock;
            if (tL_pageBlockEmbed2 != tL_pageBlockEmbed3) {
                this.wasUserInteraction = false;
                if (tL_pageBlockEmbed3.allow_scrolling) {
                    this.webView.setVerticalScrollBarEnabled(true);
                    this.webView.setHorizontalScrollBarEnabled(true);
                } else {
                    this.webView.setVerticalScrollBarEnabled(false);
                    this.webView.setHorizontalScrollBarEnabled(false);
                }
                this.exactWebViewHeight = 0;
                try {
                    this.webView.loadUrl(str);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                try {
                    if (this.currentBlock.html != null) {
                        this.webView.loadDataWithBaseURL("https://telegram.org/embed", this.currentBlock.html, "text/html", "UTF-8", null);
                        this.videoView.setVisibility(4);
                        this.videoView.loadVideo(null, null, null, null, false);
                        this.webView.setVisibility(0);
                    } else {
                        if (this.videoView.loadVideo(tL_pageBlockEmbed.url, this.currentBlock.poster_photo_id != 0 ? ArticleViewer.this.getPhotoWithId(this.currentBlock.poster_photo_id) : null, ArticleViewer.this.currentPage, null, false)) {
                            this.webView.setVisibility(4);
                            this.videoView.setVisibility(0);
                            this.webView.stopLoading();
                            this.webView.loadUrl(str);
                        } else {
                            this.webView.setVisibility(0);
                            this.videoView.setVisibility(4);
                            this.videoView.loadVideo(null, null, null, null, false);
                            HashMap hashMap = new HashMap();
                            hashMap.put("Referer", "http://youtube.com");
                            this.webView.loadUrl(this.currentBlock.url, hashMap);
                        }
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            requestLayout();
        }

        /* Access modifiers changed, original: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (!ArticleViewer.this.isVisible) {
                this.currentBlock = null;
            }
        }

        /* Access modifiers changed, original: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.captionLayout, this.textX, this.textY)) {
                if (!(ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(motionEvent))) {
                    return false;
                }
            }
            return true;
        }

        /* Access modifiers changed, original: protected */
        /* JADX WARNING: Removed duplicated region for block: B:51:0x0156  */
        @android.annotation.SuppressLint({"NewApi"})
        public void onMeasure(int r13, int r14) {
            /*
            r12 = this;
            r13 = android.view.View.MeasureSpec.getSize(r13);
            r14 = r12.currentBlock;
            if (r14 == 0) goto L_0x015f;
        L_0x0008:
            r14 = r14.level;
            r0 = 0;
            r1 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
            if (r14 <= 0) goto L_0x002a;
        L_0x000f:
            r14 = r14 * 14;
            r14 = (float) r14;
            r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r14 = r14 + r2;
            r12.listX = r14;
            r12.textX = r14;
            r14 = r12.listX;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r14 = r14 + r1;
            r14 = r13 - r14;
            r9 = r14;
            goto L_0x0052;
        L_0x002a:
            r12.listX = r0;
            r14 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r12.textX = r14;
            r14 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r2 = r13 - r2;
            r3 = r12.currentBlock;
            r3 = r3.full_width;
            if (r3 != 0) goto L_0x0050;
        L_0x0040:
            r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r14 = r13 - r14;
            r3 = r12.listX;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r3 = r3 + r1;
            r12.listX = r3;
            goto L_0x0051;
        L_0x0050:
            r14 = r13;
        L_0x0051:
            r9 = r2;
        L_0x0052:
            r1 = r12.currentBlock;
            r1 = r1.w;
            if (r1 != 0) goto L_0x005b;
        L_0x0058:
            r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            goto L_0x005f;
        L_0x005b:
            r2 = (float) r13;
            r1 = (float) r1;
            r1 = r2 / r1;
        L_0x005f:
            r2 = r12.exactWebViewHeight;
            if (r2 == 0) goto L_0x0069;
        L_0x0063:
            r1 = (float) r2;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            goto L_0x007a;
        L_0x0069:
            r2 = r12.currentBlock;
            r3 = r2.w;
            r2 = r2.h;
            r2 = (float) r2;
            if (r3 != 0) goto L_0x0077;
        L_0x0072:
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r2 = (float) r2;
        L_0x0077:
            r2 = r2 * r1;
            r1 = (int) r2;
        L_0x007a:
            r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
            if (r1 != 0) goto L_0x0082;
        L_0x007e:
            r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
        L_0x0082:
            r10 = r1;
            r1 = r12.webView;
            r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            r4 = android.view.View.MeasureSpec.makeMeasureSpec(r14, r3);
            r5 = android.view.View.MeasureSpec.makeMeasureSpec(r10, r3);
            r1.measure(r4, r5);
            r1 = r12.videoView;
            r1 = r1.getParent();
            if (r1 != r12) goto L_0x00ac;
        L_0x009a:
            r1 = r12.videoView;
            r14 = android.view.View.MeasureSpec.makeMeasureSpec(r14, r3);
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r2 = r2 + r10;
            r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r3);
            r1.measure(r14, r2);
        L_0x00ac:
            r14 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r1 = r1 + r10;
            r12.textY = r1;
            r1 = org.telegram.ui.ArticleViewer.this;
            r3 = 0;
            r7 = r12.currentBlock;
            r2 = r7.caption;
            r4 = r2.text;
            r6 = r12.textY;
            r8 = r12.parentAdapter;
            r2 = r12;
            r5 = r9;
            r1 = r1.createLayoutForText(r2, r3, r4, r5, r6, r7, r8);
            r12.captionLayout = r1;
            r1 = r12.captionLayout;
            r11 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
            if (r1 == 0) goto L_0x00e6;
        L_0x00d0:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r11);
            r1 = r12.captionLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r12.creditOffset = r0;
            r0 = r12.creditOffset;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r11);
            r0 = r0 + r1;
            r10 = r10 + r0;
            goto L_0x00e8;
        L_0x00e6:
            r12.creditOffset = r0;
        L_0x00e8:
            r1 = org.telegram.ui.ArticleViewer.this;
            r3 = 0;
            r7 = r12.currentBlock;
            r0 = r7.caption;
            r4 = r0.credit;
            r0 = r12.textY;
            r2 = r12.creditOffset;
            r6 = r0 + r2;
            r0 = r12.parentAdapter;
            r0 = r0.isRtl;
            if (r0 == 0) goto L_0x0104;
        L_0x00ff:
            r0 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT();
            goto L_0x0106;
        L_0x0104:
            r0 = android.text.Layout.Alignment.ALIGN_NORMAL;
        L_0x0106:
            r8 = r0;
            r0 = r12.parentAdapter;
            r2 = r12;
            r5 = r9;
            r9 = r0;
            r0 = r1.createLayoutForText(r2, r3, r4, r5, r6, r7, r8, r9);
            r12.creditLayout = r0;
            r0 = r12.creditLayout;
            if (r0 == 0) goto L_0x012c;
        L_0x0116:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r11);
            r1 = r12.creditLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r10 = r10 + r0;
            r0 = r12.creditLayout;
            r1 = r12.textX;
            r0.x = r1;
            r1 = r12.creditOffset;
            r0.y = r1;
        L_0x012c:
            r0 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r10 = r10 + r0;
            r0 = r12.currentBlock;
            r1 = r0.level;
            if (r1 <= 0) goto L_0x0143;
        L_0x0139:
            r0 = r0.bottom;
            if (r0 != 0) goto L_0x0143;
        L_0x013d:
            r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        L_0x0141:
            r10 = r10 + r14;
            goto L_0x0152;
        L_0x0143:
            r0 = r12.currentBlock;
            r0 = r0.level;
            if (r0 != 0) goto L_0x0152;
        L_0x0149:
            r0 = r12.captionLayout;
            if (r0 == 0) goto L_0x0152;
        L_0x014d:
            r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
            goto L_0x0141;
        L_0x0152:
            r14 = r12.captionLayout;
            if (r14 == 0) goto L_0x0160;
        L_0x0156:
            r0 = r12.textX;
            r14.x = r0;
            r0 = r12.textY;
            r14.y = r0;
            goto L_0x0160;
        L_0x015f:
            r10 = 1;
        L_0x0160:
            r12.setMeasuredDimension(r13, r10);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$BlockEmbedCell.onMeasure(int, int):void");
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            TouchyWebView touchyWebView = this.webView;
            i = this.listX;
            touchyWebView.layout(i, 0, touchyWebView.getMeasuredWidth() + i, this.webView.getMeasuredHeight());
            if (this.videoView.getParent() == this) {
                WebPlayerView webPlayerView = this.videoView;
                i = this.listX;
                webPlayerView.layout(i, 0, webPlayerView.getMeasuredWidth() + i, this.videoView.getMeasuredHeight());
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int i;
                int i2 = 0;
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    i = 1;
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                } else {
                    i = 0;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, i);
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    float dp = (float) AndroidUtilities.dp(18.0f);
                    float dp2 = (float) AndroidUtilities.dp(20.0f);
                    int measuredHeight = getMeasuredHeight();
                    if (this.currentBlock.bottom) {
                        i2 = AndroidUtilities.dp(6.0f);
                    }
                    canvas.drawRect(dp, 0.0f, dp2, (float) (measuredHeight - i2), ArticleViewer.quoteLinePaint);
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.creditLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockEmbedPostCell extends View implements ArticleSelectableView {
        private AvatarDrawable avatarDrawable;
        private ImageReceiver avatarImageView = new ImageReceiver(this);
        private boolean avatarVisible;
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockEmbedPost currentBlock;
        private DrawingText dateLayout;
        private int dateX;
        private int lineHeight;
        private DrawingText nameLayout;
        private int nameX;
        private WebpageAdapter parentAdapter;
        private int textX;
        private int textY;

        public BlockEmbedPostCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            this.avatarImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            this.avatarImageView.setImageCoords(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            this.avatarDrawable = new AvatarDrawable();
        }

        public void setBlock(TL_pageBlockEmbedPost tL_pageBlockEmbedPost) {
            this.currentBlock = tL_pageBlockEmbedPost;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.captionLayout, this.textX, this.textY)) {
                if (!(ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(motionEvent))) {
                    return false;
                }
            }
            return true;
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i);
            TL_pageBlockEmbedPost tL_pageBlockEmbedPost = this.currentBlock;
            int i3 = 1;
            if (tL_pageBlockEmbedPost != null) {
                int i4 = 0;
                int dp;
                ArticleViewer articleViewer;
                TL_pageBlockEmbedPost tL_pageBlockEmbedPost2;
                if (tL_pageBlockEmbedPost instanceof TL_pageBlockEmbedPostCaption) {
                    this.textX = AndroidUtilities.dp(18.0f);
                    this.textY = AndroidUtilities.dp(4.0f);
                    dp = size - AndroidUtilities.dp(50.0f);
                    articleViewer = ArticleViewer.this;
                    tL_pageBlockEmbedPost2 = this.currentBlock;
                    this.captionLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockEmbedPost2.caption.text, dp, this.textY, tL_pageBlockEmbedPost2, this.parentAdapter);
                    if (this.captionLayout != null) {
                        this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                        i4 = 0 + (this.creditOffset + AndroidUtilities.dp(4.0f));
                    }
                    articleViewer = ArticleViewer.this;
                    tL_pageBlockEmbedPost2 = this.currentBlock;
                    this.creditLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockEmbedPost2.caption.credit, dp, this.textY + this.creditOffset, tL_pageBlockEmbedPost2, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                    if (this.creditLayout != null) {
                        i4 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    }
                    i3 = i4;
                } else {
                    boolean z = tL_pageBlockEmbedPost.author_photo_id != 0;
                    this.avatarVisible = z;
                    if (z) {
                        Photo access$15300 = ArticleViewer.this.getPhotoWithId(this.currentBlock.author_photo_id);
                        boolean z2 = access$15300 instanceof TL_photo;
                        this.avatarVisible = z2;
                        if (z2) {
                            this.avatarDrawable.setInfo(0, this.currentBlock.author, null);
                            this.avatarImageView.setImage(ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(access$15300.sizes, AndroidUtilities.dp(40.0f), true), access$15300), "40_40", this.avatarDrawable, 0, null, ArticleViewer.this.currentPage, 1);
                        }
                    }
                    this.nameLayout = ArticleViewer.this.createLayoutForText(this, this.currentBlock.author, null, size - AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 50)), 0, this.currentBlock, Alignment.ALIGN_NORMAL, 1, this.parentAdapter);
                    DrawingText drawingText = this.nameLayout;
                    if (drawingText != null) {
                        drawingText.x = AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 32));
                        this.nameLayout.y = AndroidUtilities.dp(this.dateLayout != null ? 10.0f : 19.0f);
                    }
                    if (this.currentBlock.date != 0) {
                        this.dateLayout = ArticleViewer.this.createLayoutForText(this, LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.date) * 1000), null, size - AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 50)), AndroidUtilities.dp(29.0f), this.currentBlock, this.parentAdapter);
                    } else {
                        this.dateLayout = null;
                    }
                    dp = AndroidUtilities.dp(56.0f);
                    if (this.currentBlock.blocks.isEmpty()) {
                        this.textX = AndroidUtilities.dp(32.0f);
                        this.textY = AndroidUtilities.dp(56.0f);
                        int dp2 = size - AndroidUtilities.dp(50.0f);
                        articleViewer = ArticleViewer.this;
                        tL_pageBlockEmbedPost2 = this.currentBlock;
                        this.captionLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockEmbedPost2.caption.text, dp2, this.textY, tL_pageBlockEmbedPost2, this.parentAdapter);
                        if (this.captionLayout != null) {
                            this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                            dp += this.creditOffset + AndroidUtilities.dp(4.0f);
                        }
                        int i5 = dp;
                        articleViewer = ArticleViewer.this;
                        tL_pageBlockEmbedPost2 = this.currentBlock;
                        this.creditLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockEmbedPost2.caption.credit, dp2, this.textY + this.creditOffset, tL_pageBlockEmbedPost2, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                        if (this.creditLayout != null) {
                            i5 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                        }
                        dp = i5;
                    } else {
                        this.captionLayout = null;
                        this.creditLayout = null;
                    }
                    drawingText = this.dateLayout;
                    if (drawingText != null) {
                        if (this.avatarVisible) {
                            i4 = 54;
                        }
                        drawingText.x = AndroidUtilities.dp((float) (i4 + 32));
                        this.dateLayout.y = AndroidUtilities.dp(29.0f);
                    }
                    drawingText = this.captionLayout;
                    if (drawingText != null) {
                        drawingText.x = this.textX;
                        drawingText.y = this.textY;
                    }
                    drawingText = this.creditLayout;
                    if (drawingText != null) {
                        drawingText.x = this.textX;
                        drawingText.y = this.textY;
                    }
                    i3 = dp;
                }
                this.lineHeight = i3;
            }
            setMeasuredDimension(size, i3);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            TL_pageBlockEmbedPost tL_pageBlockEmbedPost = this.currentBlock;
            if (tL_pageBlockEmbedPost != null) {
                int i;
                int i2;
                int i3 = 0;
                if (tL_pageBlockEmbedPost instanceof TL_pageBlockEmbedPostCaption) {
                    i = 0;
                } else {
                    int i4;
                    if (this.avatarVisible) {
                        this.avatarImageView.draw(canvas);
                    }
                    i2 = 54;
                    if (this.nameLayout != null) {
                        canvas.save();
                        canvas.translate((float) AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 32)), (float) AndroidUtilities.dp(this.dateLayout != null ? 10.0f : 19.0f));
                        ArticleViewer.this.drawTextSelection(canvas, this, 0);
                        this.nameLayout.draw(canvas);
                        canvas.restore();
                        i = 1;
                    } else {
                        i = 0;
                    }
                    if (this.dateLayout != null) {
                        canvas.save();
                        if (!this.avatarVisible) {
                            i2 = 0;
                        }
                        canvas.translate((float) AndroidUtilities.dp((float) (i2 + 32)), (float) AndroidUtilities.dp(29.0f));
                        i4 = i + 1;
                        ArticleViewer.this.drawTextSelection(canvas, this, i);
                        this.dateLayout.draw(canvas);
                        canvas.restore();
                        i = i4;
                    }
                    float dp = (float) AndroidUtilities.dp(18.0f);
                    float dp2 = (float) AndroidUtilities.dp(6.0f);
                    float dp3 = (float) AndroidUtilities.dp(20.0f);
                    i4 = this.lineHeight;
                    if (this.currentBlock.level == 0) {
                        i3 = AndroidUtilities.dp(6.0f);
                    }
                    canvas.drawRect(dp, dp2, dp3, (float) (i4 - i3), ArticleViewer.quoteLinePaint);
                }
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    i2 = i + 1;
                    ArticleViewer.this.drawTextSelection(canvas, this, i);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                    i = i2;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, i);
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.nameLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.dateLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.creditLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockFooterCell extends View implements ArticleSelectableView {
        private TL_pageBlockFooter currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockFooterCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TL_pageBlockFooter tL_pageBlockFooter) {
            this.currentBlock = tL_pageBlockFooter;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            TL_pageBlockFooter tL_pageBlockFooter = this.currentBlock;
            int i3 = 0;
            if (tL_pageBlockFooter != null) {
                i2 = tL_pageBlockFooter.level;
                if (i2 == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((float) ((i2 * 14) + 18));
                }
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, (i - AndroidUtilities.dp(18.0f)) - this.textX, this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    i2 = drawingText.getHeight();
                    if (this.currentBlock.level > 0) {
                        i3 = AndroidUtilities.dp(8.0f);
                    } else {
                        i3 = AndroidUtilities.dp(16.0f);
                    }
                    i3 = i2 + i3;
                    drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(i, i3);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockHeaderCell extends View implements ArticleSelectableView {
        private TL_pageBlockHeader currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockHeaderCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TL_pageBlockHeader tL_pageBlockHeader) {
            this.currentBlock = tL_pageBlockHeader;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            TL_pageBlockHeader tL_pageBlockHeader = this.currentBlock;
            int i3 = 0;
            if (tL_pageBlockHeader != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, tL_pageBlockHeader.text, i - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    i3 = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(i, i3);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            if (this.textLayout != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.textLayout.getText());
                stringBuilder.append(", ");
                stringBuilder.append(LocaleController.getString("AccDescrIVHeading", NUM));
                accessibilityNodeInfo.setText(stringBuilder.toString());
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockKickerCell extends View implements ArticleSelectableView {
        private TL_pageBlockKicker currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockKickerCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TL_pageBlockKicker tL_pageBlockKicker) {
            this.currentBlock = tL_pageBlockKicker;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int i3;
            i = MeasureSpec.getSize(i);
            TL_pageBlockKicker tL_pageBlockKicker = this.currentBlock;
            if (tL_pageBlockKicker != null) {
                i3 = 0;
                if (tL_pageBlockKicker.first) {
                    this.textY = AndroidUtilities.dp(16.0f);
                    i3 = 0 + AndroidUtilities.dp(8.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
                }
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, i - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    i3 += AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(i, i3);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockListItemCell extends ViewGroup implements ArticleSelectableView {
        private ViewHolder blockLayout;
        private int blockX;
        private int blockY;
        private TL_pageBlockListItem currentBlock;
        private int currentBlockType;
        private boolean drawDot;
        private int numOffsetY;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX;
        private int textY;
        private boolean verticalAlign;

        public BlockListItemCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockListItem tL_pageBlockListItem) {
            if (this.currentBlock != tL_pageBlockListItem) {
                this.currentBlock = tL_pageBlockListItem;
                ViewHolder viewHolder = this.blockLayout;
                if (viewHolder != null) {
                    removeView(viewHolder.itemView);
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

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(motionEvent);
        }

        /* Access modifiers changed, original: protected */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x039f  */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x03c2  */
        /* JADX WARNING: Removed duplicated region for block: B:130:0x03fc  */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x039f  */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x03c2  */
        /* JADX WARNING: Removed duplicated region for block: B:130:0x03fc  */
        @android.annotation.SuppressLint({"NewApi"})
        public void onMeasure(int r17, int r18) {
            /*
            r16 = this;
            r9 = r16;
            r10 = android.view.View.MeasureSpec.getSize(r17);
            r0 = r9.currentBlock;
            r11 = 1;
            if (r0 == 0) goto L_0x0417;
        L_0x000b:
            r1 = 0;
            r9.textLayout = r1;
            r0 = r0.index;
            r12 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
            r13 = 0;
            if (r0 != 0) goto L_0x0028;
        L_0x0017:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.level;
            if (r0 != 0) goto L_0x0028;
        L_0x0023:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r12);
            goto L_0x0029;
        L_0x0028:
            r0 = 0;
        L_0x0029:
            r9.textY = r0;
            r9.numOffsetY = r13;
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.lastMaxNumCalcWidth;
            if (r0 != r10) goto L_0x0047;
        L_0x0039:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.lastFontSize;
            r1 = org.telegram.messenger.SharedConfig.ivFontSize;
            if (r0 == r1) goto L_0x00fa;
        L_0x0047:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0.lastMaxNumCalcWidth = r10;
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r1 = org.telegram.messenger.SharedConfig.ivFontSize;
            r0.lastFontSize = r1;
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0.maxNumWidth = r13;
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.items;
            r8 = r0.size();
            r14 = 0;
        L_0x0073:
            if (r14 >= r8) goto L_0x00d3;
        L_0x0075:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.items;
            r0 = r0.get(r14);
            r15 = r0;
            r15 = (org.telegram.ui.ArticleViewer.TL_pageBlockListItem) r15;
            r0 = r15.num;
            if (r0 != 0) goto L_0x008d;
        L_0x008c:
            goto L_0x00d0;
        L_0x008d:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = r15.num;
            r3 = 0;
            r1 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r4 = r10 - r1;
            r5 = r9.textY;
            r6 = r9.currentBlock;
            r7 = r9.parentAdapter;
            r1 = r16;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7);
            r15.numLayout = r0;
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r1 = r9.currentBlock;
            r1 = r1.parent;
            r1 = r1.maxNumWidth;
            r2 = r15.numLayout;
            r2 = r2.getLineWidth(r13);
            r2 = (double) r2;
            r2 = java.lang.Math.ceil(r2);
            r2 = (int) r2;
            r1 = java.lang.Math.max(r1, r2);
            r0.maxNumWidth = r1;
        L_0x00d0:
            r14 = r14 + 1;
            goto L_0x0073;
        L_0x00d3:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r1 = r9.currentBlock;
            r1 = r1.parent;
            r1 = r1.maxNumWidth;
            r2 = org.telegram.ui.ArticleViewer.listTextNumPaint;
            r3 = "00.";
            r2 = r2.measureText(r3);
            r2 = (double) r2;
            r2 = java.lang.Math.ceil(r2);
            r2 = (int) r2;
            r1 = java.lang.Math.max(r1, r2);
            r0.maxNumWidth = r1;
        L_0x00fa:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.pageBlockList;
            r0 = r0.ordered;
            r0 = r0 ^ r11;
            r9.drawDot = r0;
            r0 = r9.parentAdapter;
            r0 = r0.isRtl;
            r1 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
            r2 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
            if (r0 == 0) goto L_0x011c;
        L_0x0115:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r9.textX = r0;
            goto L_0x0140;
        L_0x011c:
            r0 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r3 = r9.currentBlock;
            r3 = r3.parent;
            r3 = r3.maxNumWidth;
            r0 = r0 + r3;
            r3 = r9.currentBlock;
            r3 = r3.parent;
            r3 = r3.level;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r3 = r3 * r4;
            r0 = r0 + r3;
            r9.textX = r0;
        L_0x0140:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r10 - r0;
            r3 = r9.textX;
            r0 = r0 - r3;
            r3 = r9.parentAdapter;
            r3 = r3.isRtl;
            if (r3 == 0) goto L_0x0174;
        L_0x0151:
            r3 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r4 = r9.currentBlock;
            r4 = r4.parent;
            r4 = r4.maxNumWidth;
            r3 = r3 + r4;
            r4 = r9.currentBlock;
            r4 = r4.parent;
            r4 = r4.level;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r4 = r4 * r1;
            r3 = r3 + r4;
            r0 = r0 - r3;
        L_0x0174:
            r4 = r0;
            r0 = r9.currentBlock;
            r0 = r0.textItem;
            r14 = NUM; // 0x40200000 float:2.5 double:5.315350785E-315;
            r15 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            if (r0 == 0) goto L_0x01eb;
        L_0x0181:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r1 = r9.currentBlock;
            r3 = r1.textItem;
            r5 = r9.textY;
            r6 = r9.currentBlock;
            r1 = r9.parentAdapter;
            r1 = r1.isRtl;
            if (r1 == 0) goto L_0x019b;
        L_0x0196:
            r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT();
            goto L_0x019d;
        L_0x019b:
            r1 = android.text.Layout.Alignment.ALIGN_NORMAL;
        L_0x019d:
            r7 = r1;
            r8 = r9.parentAdapter;
            r1 = r16;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8);
            r9.textLayout = r0;
            r0 = r9.textLayout;
            if (r0 == 0) goto L_0x037e;
        L_0x01ac:
            r0 = r0.getLineCount();
            if (r0 <= 0) goto L_0x037e;
        L_0x01b2:
            r0 = r9.currentBlock;
            r0 = r0.numLayout;
            if (r0 == 0) goto L_0x01de;
        L_0x01ba:
            r0 = r9.currentBlock;
            r0 = r0.numLayout;
            r0 = r0.getLineCount();
            if (r0 <= 0) goto L_0x01de;
        L_0x01c6:
            r0 = r9.textLayout;
            r0 = r0.getLineAscent(r13);
            r1 = r9.currentBlock;
            r1 = r1.numLayout;
            r1 = r1.getLineAscent(r13);
            r2 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r1 = r1 + r2;
            r1 = r1 - r0;
            r9.numOffsetY = r1;
        L_0x01de:
            r0 = r9.textLayout;
            r0 = r0.getHeight();
            r1 = org.telegram.messenger.AndroidUtilities.dp(r15);
            r0 = r0 + r1;
            goto L_0x037d;
        L_0x01eb:
            r0 = r9.currentBlock;
            r0 = r0.blockItem;
            if (r0 == 0) goto L_0x037e;
        L_0x01f3:
            r0 = r9.textX;
            r9.blockX = r0;
            r0 = r9.textY;
            r9.blockY = r0;
            r0 = r9.blockLayout;
            if (r0 == 0) goto L_0x0379;
        L_0x01ff:
            r0 = r0.itemView;
            r1 = r0 instanceof org.telegram.ui.ArticleViewer.BlockParagraphCell;
            if (r1 == 0) goto L_0x022c;
        L_0x0205:
            r0 = r9.blockY;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r15);
            r0 = r0 - r1;
            r9.blockY = r0;
            r0 = r9.parentAdapter;
            r0 = r0.isRtl;
            if (r0 != 0) goto L_0x021f;
        L_0x0216:
            r0 = r9.blockX;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 - r1;
            r9.blockX = r0;
        L_0x021f:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 + r4;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r15);
            r1 = 0 - r1;
            goto L_0x02a6;
        L_0x022c:
            r1 = r0 instanceof org.telegram.ui.ArticleViewer.BlockHeaderCell;
            if (r1 != 0) goto L_0x028f;
        L_0x0230:
            r1 = r0 instanceof org.telegram.ui.ArticleViewer.BlockSubheaderCell;
            if (r1 != 0) goto L_0x028f;
        L_0x0234:
            r1 = r0 instanceof org.telegram.ui.ArticleViewer.BlockTitleCell;
            if (r1 != 0) goto L_0x028f;
        L_0x0238:
            r0 = r0 instanceof org.telegram.ui.ArticleViewer.BlockSubtitleCell;
            if (r0 == 0) goto L_0x023d;
        L_0x023c:
            goto L_0x028f;
        L_0x023d:
            r0 = org.telegram.ui.ArticleViewer.this;
            r1 = r9.currentBlock;
            r1 = r1.blockItem;
            r0 = r0.isListItemBlock(r1);
            if (r0 == 0) goto L_0x0275;
        L_0x024b:
            r9.blockX = r13;
            r9.blockY = r13;
            r9.textY = r13;
            r0 = r9.currentBlock;
            r0 = r0.index;
            if (r0 != 0) goto L_0x026c;
        L_0x0259:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.level;
            if (r0 != 0) goto L_0x026c;
        L_0x0265:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r12);
            r0 = 0 - r0;
            goto L_0x026d;
        L_0x026c:
            r0 = 0;
        L_0x026d:
            r1 = org.telegram.messenger.AndroidUtilities.dp(r15);
            r0 = r0 - r1;
            r1 = r0;
            r0 = r10;
            goto L_0x02a6;
        L_0x0275:
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r0 = r0 instanceof org.telegram.ui.ArticleViewer.BlockTableCell;
            if (r0 == 0) goto L_0x028d;
        L_0x027d:
            r0 = r9.blockX;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 - r1;
            r9.blockX = r0;
            r0 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            goto L_0x02a4;
        L_0x028d:
            r0 = r4;
            goto L_0x02a5;
        L_0x028f:
            r0 = r9.parentAdapter;
            r0 = r0.isRtl;
            if (r0 != 0) goto L_0x02a0;
        L_0x0297:
            r0 = r9.blockX;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 - r1;
            r9.blockX = r0;
        L_0x02a0:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
        L_0x02a4:
            r0 = r0 + r4;
        L_0x02a5:
            r1 = 0;
        L_0x02a6:
            r2 = r9.blockLayout;
            r2 = r2.itemView;
            r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3);
            r3 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r13);
            r2.measure(r0, r3);
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r0 = r0 instanceof org.telegram.ui.ArticleViewer.BlockParagraphCell;
            if (r0 == 0) goto L_0x02fb;
        L_0x02bf:
            r0 = r9.currentBlock;
            r0 = r0.numLayout;
            if (r0 == 0) goto L_0x02fb;
        L_0x02c7:
            r0 = r9.currentBlock;
            r0 = r0.numLayout;
            r0 = r0.getLineCount();
            if (r0 <= 0) goto L_0x02fb;
        L_0x02d3:
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r0 = (org.telegram.ui.ArticleViewer.BlockParagraphCell) r0;
            r2 = r0.textLayout;
            if (r2 == 0) goto L_0x02fb;
        L_0x02dd:
            r2 = r2.getLineCount();
            if (r2 <= 0) goto L_0x02fb;
        L_0x02e3:
            r0 = r0.textLayout;
            r0 = r0.getLineAscent(r13);
            r2 = r9.currentBlock;
            r2 = r2.numLayout;
            r2 = r2.getLineAscent(r13);
            r3 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r2 = r2 + r3;
            r2 = r2 - r0;
            r9.numOffsetY = r2;
        L_0x02fb:
            r0 = r9.currentBlock;
            r0 = r0.blockItem;
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockDetails;
            if (r0 == 0) goto L_0x0328;
        L_0x0305:
            r9.verticalAlign = r11;
            r9.blockY = r13;
            r0 = r9.currentBlock;
            r0 = r0.index;
            if (r0 != 0) goto L_0x0322;
        L_0x0311:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.level;
            if (r0 != 0) goto L_0x0322;
        L_0x031d:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r12);
            r1 = r1 - r0;
        L_0x0322:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
            r1 = r1 - r0;
            goto L_0x0343;
        L_0x0328:
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r2 = r0 instanceof org.telegram.ui.ArticleViewer.BlockOrderedListItemCell;
            if (r2 == 0) goto L_0x0339;
        L_0x0330:
            r0 = (org.telegram.ui.ArticleViewer.BlockOrderedListItemCell) r0;
            r0 = r0.verticalAlign;
            r9.verticalAlign = r0;
            goto L_0x0343;
        L_0x0339:
            r2 = r0 instanceof org.telegram.ui.ArticleViewer.BlockListItemCell;
            if (r2 == 0) goto L_0x0343;
        L_0x033d:
            r0 = (org.telegram.ui.ArticleViewer.BlockListItemCell) r0;
            r0 = r0.verticalAlign;
            r9.verticalAlign = r0;
        L_0x0343:
            r0 = r9.verticalAlign;
            if (r0 == 0) goto L_0x036f;
        L_0x0347:
            r0 = r9.currentBlock;
            r0 = r0.numLayout;
            if (r0 == 0) goto L_0x036f;
        L_0x034f:
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r0 = r0.getMeasuredHeight();
            r2 = r9.currentBlock;
            r2 = r2.numLayout;
            r2 = r2.getHeight();
            r0 = r0 - r2;
            r0 = r0 / 2;
            r2 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 - r2;
            r9.textY = r0;
            r9.drawDot = r13;
        L_0x036f:
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r0 = r0.getMeasuredHeight();
            r13 = r1 + r0;
        L_0x0379:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
        L_0x037d:
            r13 = r13 + r0;
        L_0x037e:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.items;
            r1 = r9.currentBlock;
            r1 = r1.parent;
            r1 = r1.items;
            r1 = r1.size();
            r1 = r1 - r11;
            r0 = r0.get(r1);
            r1 = r9.currentBlock;
            if (r0 != r1) goto L_0x03a4;
        L_0x039f:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
            r13 = r13 + r0;
        L_0x03a4:
            r0 = r9.currentBlock;
            r0 = r0.index;
            if (r0 != 0) goto L_0x03bd;
        L_0x03ac:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.level;
            if (r0 != 0) goto L_0x03bd;
        L_0x03b8:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r12);
            r13 = r13 + r0;
        L_0x03bd:
            r11 = r13;
            r0 = r9.textLayout;
            if (r0 == 0) goto L_0x03ca;
        L_0x03c2:
            r1 = r9.textX;
            r0.x = r1;
            r1 = r9.textY;
            r0.y = r1;
        L_0x03ca:
            r0 = r9.blockLayout;
            if (r0 == 0) goto L_0x0417;
        L_0x03ce:
            r0 = r0.itemView;
            r0 = r0 instanceof org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView;
            if (r0 == 0) goto L_0x0417;
        L_0x03d4:
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.textSelectionHelper;
            r0 = r0.arrayList;
            r0.clear();
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r0 = (org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r0;
            r1 = org.telegram.ui.ArticleViewer.this;
            r1 = r1.textSelectionHelper;
            r1 = r1.arrayList;
            r0.fillTextLayoutBlocks(r1);
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.textSelectionHelper;
            r0 = r0.arrayList;
            r0 = r0.iterator();
        L_0x03f6:
            r1 = r0.hasNext();
            if (r1 == 0) goto L_0x0417;
        L_0x03fc:
            r1 = r0.next();
            r1 = (org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock) r1;
            r2 = r1 instanceof org.telegram.ui.ArticleViewer.DrawingText;
            if (r2 == 0) goto L_0x03f6;
        L_0x0406:
            r1 = (org.telegram.ui.ArticleViewer.DrawingText) r1;
            r2 = r1.x;
            r3 = r9.blockX;
            r2 = r2 + r3;
            r1.x = r2;
            r2 = r1.y;
            r3 = r9.blockY;
            r2 = r2 + r3;
            r1.y = r2;
            goto L_0x03f6;
        L_0x0417:
            r9.setMeasuredDimension(r10, r11);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$BlockListItemCell.onMeasure(int, int):void");
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                i = this.blockX;
                view.layout(i, this.blockY, view.getMeasuredWidth() + i, this.blockY + this.blockLayout.itemView.getMeasuredHeight());
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int measuredWidth = getMeasuredWidth();
                if (this.currentBlock.numLayout != null) {
                    canvas.save();
                    int i = 0;
                    float dp;
                    int i2;
                    if (this.parentAdapter.isRtl) {
                        dp = (float) (((measuredWidth - AndroidUtilities.dp(15.0f)) - this.currentBlock.parent.maxNumWidth) - (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f)));
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
                    ArticleViewer.this.drawTextSelection(canvas, this);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }

        public void invalidate() {
            super.invalidate();
            ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                viewHolder.itemView.invalidate();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                accessibilityNodeInfo.setText(drawingText.getText());
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                if (view instanceof ArticleSelectableView) {
                    ((ArticleSelectableView) view).fillTextLayoutBlocks(arrayList);
                }
            }
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockMapCell extends FrameLayout implements ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockMap currentBlock;
        private int currentMapProvider;
        private int currentType;
        private ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private WebpageAdapter parentAdapter;
        private boolean photoPressed;
        private int textX;
        private int textY;

        public BlockMapCell(Context context, WebpageAdapter webpageAdapter, int i) {
            super(context);
            this.parentAdapter = webpageAdapter;
            setWillNotDraw(false);
            this.currentType = i;
        }

        public void setBlock(TL_pageBlockMap tL_pageBlockMap, boolean z, boolean z2) {
            this.currentBlock = tL_pageBlockMap;
            this.isFirst = z;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            String str = ",";
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if (motionEvent.getAction() == 0 && this.imageView.isInsideImage(x, y)) {
                this.photoPressed = true;
            } else if (motionEvent.getAction() == 1 && this.photoPressed) {
                this.photoPressed = false;
                try {
                    double d = this.currentBlock.geo.lat;
                    double d2 = this.currentBlock.geo._long;
                    Activity access$2200 = ArticleViewer.this.parentActivity;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("geo:");
                    stringBuilder.append(d);
                    stringBuilder.append(str);
                    stringBuilder.append(d2);
                    stringBuilder.append("?q=");
                    stringBuilder.append(d);
                    stringBuilder.append(str);
                    stringBuilder.append(d2);
                    access$2200.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else if (motionEvent.getAction() == 3) {
                this.photoPressed = false;
            }
            if (!this.photoPressed) {
                if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.captionLayout, this.textX, this.textY)) {
                    if (!(ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(motionEvent))) {
                        return false;
                    }
                }
            }
            return true;
        }

        /* Access modifiers changed, original: protected */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x00a7  */
        /* JADX WARNING: Removed duplicated region for block: B:32:0x012c  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x0110  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x0153  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x01ed  */
        /* JADX WARNING: Removed duplicated region for block: B:16:0x0063  */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x00a7  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x0110  */
        /* JADX WARNING: Removed duplicated region for block: B:32:0x012c  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x0153  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x01ed  */
        @android.annotation.SuppressLint({"NewApi"})
        public void onMeasure(int r25, int r26) {
            /*
            r24 = this;
            r9 = r24;
            r0 = android.view.View.MeasureSpec.getSize(r25);
            r1 = r9.currentType;
            r2 = 1;
            r3 = 0;
            r10 = 2;
            if (r1 != r2) goto L_0x0024;
        L_0x000d:
            r0 = r24.getParent();
            r0 = (android.view.View) r0;
            r0 = r0.getMeasuredWidth();
            r1 = r24.getParent();
            r1 = (android.view.View) r1;
            r1 = r1.getMeasuredHeight();
            r11 = r0;
            r0 = r1;
            goto L_0x0029;
        L_0x0024:
            r11 = r0;
            if (r1 != r10) goto L_0x0028;
        L_0x0027:
            goto L_0x0029;
        L_0x0028:
            r0 = 0;
        L_0x0029:
            r1 = r9.currentBlock;
            if (r1 == 0) goto L_0x01f3;
        L_0x002d:
            r4 = r9.currentType;
            r5 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
            if (r4 != 0) goto L_0x004e;
        L_0x0033:
            r1 = r1.level;
            if (r1 <= 0) goto L_0x004e;
        L_0x0037:
            r1 = r1 * 14;
            r1 = (float) r1;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r1 = r1 + r4;
            r9.textX = r1;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r4 = r4 + r1;
            r4 = r11 - r4;
            r8 = r4;
            goto L_0x005f;
        L_0x004e:
            r1 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r9.textX = r1;
            r1 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r1 = r11 - r1;
            r8 = r1;
            r4 = r11;
            r1 = 0;
        L_0x005f:
            r5 = r9.currentType;
            if (r5 != 0) goto L_0x009e;
        L_0x0063:
            r0 = (float) r4;
            r5 = r9.currentBlock;
            r6 = r5.w;
            r6 = (float) r6;
            r0 = r0 / r6;
            r5 = r5.h;
            r5 = (float) r5;
            r0 = r0 * r5;
            r0 = (int) r0;
            r5 = org.telegram.messenger.AndroidUtilities.displaySize;
            r6 = r5.x;
            r5 = r5.y;
            r5 = java.lang.Math.max(r6, r5);
            r6 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
            r5 = r5 - r6;
            r5 = (float) r5;
            r6 = NUM; // 0x3var_ float:0.9 double:5.2552552E-315;
            r5 = r5 * r6;
            r5 = (int) r5;
            if (r0 <= r5) goto L_0x009e;
        L_0x008a:
            r0 = (float) r5;
            r4 = r9.currentBlock;
            r6 = r4.h;
            r6 = (float) r6;
            r0 = r0 / r6;
            r4 = r4.w;
            r4 = (float) r4;
            r0 = r0 * r4;
            r4 = (int) r0;
            r0 = r11 - r1;
            r0 = r0 - r4;
            r0 = r0 / r10;
            r1 = r1 + r0;
            r12 = r5;
            goto L_0x009f;
        L_0x009e:
            r12 = r0;
        L_0x009f:
            r0 = r9.imageView;
            r5 = r9.isFirst;
            r13 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            if (r5 != 0) goto L_0x00b8;
        L_0x00a7:
            r5 = r9.currentType;
            if (r5 == r2) goto L_0x00b8;
        L_0x00ab:
            if (r5 == r10) goto L_0x00b8;
        L_0x00ad:
            r2 = r9.currentBlock;
            r2 = r2.level;
            if (r2 <= 0) goto L_0x00b4;
        L_0x00b3:
            goto L_0x00b8;
        L_0x00b4:
            r3 = org.telegram.messenger.AndroidUtilities.dp(r13);
        L_0x00b8:
            r0.setImageCoords(r1, r3, r4, r12);
            r0 = org.telegram.ui.ArticleViewer.this;
            r14 = r0.currentAccount;
            r0 = r9.currentBlock;
            r0 = r0.geo;
            r1 = r0.lat;
            r5 = r0._long;
            r0 = (float) r4;
            r3 = org.telegram.messenger.AndroidUtilities.density;
            r4 = r0 / r3;
            r4 = (int) r4;
            r7 = (float) r12;
            r3 = r7 / r3;
            r3 = (int) r3;
            r21 = 1;
            r22 = 15;
            r23 = -1;
            r15 = r1;
            r17 = r5;
            r19 = r4;
            r20 = r3;
            r16 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r14, r15, r17, r19, r20, r21, r22, r23);
            r1 = r9.currentBlock;
            r1 = r1.geo;
            r2 = org.telegram.messenger.AndroidUtilities.density;
            r0 = r0 / r2;
            r0 = (int) r0;
            r7 = r7 / r2;
            r3 = (int) r7;
            r4 = 15;
            r5 = (double) r2;
            r5 = java.lang.Math.ceil(r5);
            r2 = (int) r5;
            r2 = java.lang.Math.min(r10, r2);
            r0 = org.telegram.messenger.WebFile.createWithGeoPoint(r1, r0, r3, r4, r2);
            r1 = org.telegram.ui.ArticleViewer.this;
            r1 = r1.currentAccount;
            r1 = org.telegram.messenger.MessagesController.getInstance(r1);
            r1 = r1.mapProvider;
            r9.currentMapProvider = r1;
            r1 = r9.currentMapProvider;
            if (r1 != r10) goto L_0x012c;
        L_0x0110:
            if (r0 == 0) goto L_0x013b;
        L_0x0112:
            r1 = r9.imageView;
            r18 = org.telegram.messenger.ImageLocation.getForWebFile(r0);
            r19 = 0;
            r20 = 0;
            r21 = 0;
            r0 = org.telegram.ui.ArticleViewer.this;
            r22 = r0.currentPage;
            r23 = 0;
            r17 = r1;
            r17.setImage(r18, r19, r20, r21, r22, r23);
            goto L_0x013b;
        L_0x012c:
            if (r16 == 0) goto L_0x013b;
        L_0x012e:
            r15 = r9.imageView;
            r17 = 0;
            r18 = 0;
            r19 = 0;
            r20 = 0;
            r15.setImage(r16, r17, r18, r19, r20);
        L_0x013b:
            r0 = r9.imageView;
            r0 = r0.getImageY();
            r1 = r9.imageView;
            r1 = r1.getImageHeight();
            r0 = r0 + r1;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r13);
            r0 = r0 + r1;
            r9.textY = r0;
            r0 = r9.currentType;
            if (r0 != 0) goto L_0x01d6;
        L_0x0153:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r6 = r9.currentBlock;
            r1 = r6.caption;
            r3 = r1.text;
            r5 = r9.textY;
            r7 = r9.parentAdapter;
            r1 = r24;
            r4 = r8;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7);
            r9.captionLayout = r0;
            r0 = r9.captionLayout;
            r14 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
            if (r0 == 0) goto L_0x018e;
        L_0x016f:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r1 = r9.captionLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r9.creditOffset = r0;
            r0 = r9.creditOffset;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r0 = r0 + r1;
            r12 = r12 + r0;
            r0 = r9.captionLayout;
            r1 = r9.textX;
            r0.x = r1;
            r1 = r9.textY;
            r0.y = r1;
        L_0x018e:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r6 = r9.currentBlock;
            r1 = r6.caption;
            r3 = r1.credit;
            r1 = r9.textY;
            r4 = r9.creditOffset;
            r5 = r1 + r4;
            r1 = r9.parentAdapter;
            r1 = r1.isRtl;
            if (r1 == 0) goto L_0x01aa;
        L_0x01a5:
            r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT();
            goto L_0x01ac;
        L_0x01aa:
            r1 = android.text.Layout.Alignment.ALIGN_NORMAL;
        L_0x01ac:
            r7 = r1;
            r15 = r9.parentAdapter;
            r1 = r24;
            r4 = r8;
            r8 = r15;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8);
            r9.creditLayout = r0;
            r0 = r9.creditLayout;
            if (r0 == 0) goto L_0x01d6;
        L_0x01bd:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r1 = r9.creditLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r12 = r12 + r0;
            r0 = r9.creditLayout;
            r1 = r9.textX;
            r0.x = r1;
            r1 = r9.textY;
            r2 = r9.creditOffset;
            r1 = r1 + r2;
            r0.y = r1;
        L_0x01d6:
            r0 = r9.isFirst;
            if (r0 != 0) goto L_0x01e9;
        L_0x01da:
            r0 = r9.currentType;
            if (r0 != 0) goto L_0x01e9;
        L_0x01de:
            r0 = r9.currentBlock;
            r0 = r0.level;
            if (r0 > 0) goto L_0x01e9;
        L_0x01e4:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
            r12 = r12 + r0;
        L_0x01e9:
            r0 = r9.currentType;
            if (r0 == r10) goto L_0x01f2;
        L_0x01ed:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
            r12 = r12 + r0;
        L_0x01f2:
            r2 = r12;
        L_0x01f3:
            r9.setMeasuredDimension(r11, r2);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$BlockMapCell.onMeasure(int, int):void");
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                Theme.chat_docBackPaint.setColor(Theme.getColor("chat_inLocationBackground"));
                canvas.drawRect((float) this.imageView.getImageX(), (float) this.imageView.getImageY(), (float) this.imageView.getImageX2(), (float) this.imageView.getImageY2(), Theme.chat_docBackPaint);
                int i = 0;
                int centerX = (int) (this.imageView.getCenterX() - ((float) (Theme.chat_locationDrawable[0].getIntrinsicWidth() / 2)));
                int centerY = (int) (this.imageView.getCenterY() - ((float) (Theme.chat_locationDrawable[0].getIntrinsicHeight() / 2)));
                Drawable[] drawableArr = Theme.chat_locationDrawable;
                drawableArr[0].setBounds(centerX, centerY, drawableArr[0].getIntrinsicWidth() + centerX, Theme.chat_locationDrawable[0].getIntrinsicHeight() + centerY);
                Theme.chat_locationDrawable[0].draw(canvas);
                this.imageView.draw(canvas);
                if (this.currentMapProvider == 2 && this.imageView.hasNotThumb()) {
                    centerX = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicWidth()) * 0.8f);
                    centerY = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicHeight()) * 0.8f);
                    int imageX = this.imageView.getImageX() + ((this.imageView.getImageWidth() - centerX) / 2);
                    int imageY = this.imageView.getImageY() + ((this.imageView.getImageHeight() / 2) - centerY);
                    Theme.chat_redLocationIcon.setAlpha((int) (this.imageView.getCurrentAlpha() * 255.0f));
                    Theme.chat_redLocationIcon.setBounds(imageX, imageY, centerX + imageX, centerY + imageY);
                    Theme.chat_redLocationIcon.draw(canvas);
                }
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    centerY = 1;
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                } else {
                    centerY = 0;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, centerY);
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    float dp = (float) AndroidUtilities.dp(18.0f);
                    float dp2 = (float) AndroidUtilities.dp(20.0f);
                    centerX = getMeasuredHeight();
                    if (this.currentBlock.bottom) {
                        i = AndroidUtilities.dp(6.0f);
                    }
                    canvas.drawRect(dp, 0.0f, dp2, (float) (centerX - i), ArticleViewer.quoteLinePaint);
                }
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            StringBuilder stringBuilder = new StringBuilder(LocaleController.getString("Map", NUM));
            if (this.captionLayout != null) {
                stringBuilder.append(", ");
                stringBuilder.append(this.captionLayout.getText());
            }
            accessibilityNodeInfo.setText(stringBuilder.toString());
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.creditLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockOrderedListItemCell extends ViewGroup implements ArticleSelectableView {
        private ViewHolder blockLayout;
        private int blockX;
        private int blockY;
        private TL_pageBlockOrderedListItem currentBlock;
        private int currentBlockType;
        private int numOffsetY;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX;
        private int textY;
        private boolean verticalAlign;

        public BlockOrderedListItemCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem) {
            if (this.currentBlock != tL_pageBlockOrderedListItem) {
                this.currentBlock = tL_pageBlockOrderedListItem;
                ViewHolder viewHolder = this.blockLayout;
                if (viewHolder != null) {
                    removeView(viewHolder.itemView);
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

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(motionEvent);
        }

        /* Access modifiers changed, original: protected */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0348  */
        /* JADX WARNING: Removed duplicated region for block: B:111:0x036b  */
        /* JADX WARNING: Removed duplicated region for block: B:121:0x03bd  */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0348  */
        /* JADX WARNING: Removed duplicated region for block: B:111:0x036b  */
        /* JADX WARNING: Removed duplicated region for block: B:121:0x03bd  */
        @android.annotation.SuppressLint({"NewApi"})
        public void onMeasure(int r17, int r18) {
            /*
            r16 = this;
            r9 = r16;
            r10 = android.view.View.MeasureSpec.getSize(r17);
            r0 = r9.currentBlock;
            r11 = 1;
            if (r0 == 0) goto L_0x03d8;
        L_0x000b:
            r1 = 0;
            r9.textLayout = r1;
            r0 = r0.index;
            r12 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
            r13 = 0;
            if (r0 != 0) goto L_0x0028;
        L_0x0017:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.level;
            if (r0 != 0) goto L_0x0028;
        L_0x0023:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r12);
            goto L_0x0029;
        L_0x0028:
            r0 = 0;
        L_0x0029:
            r9.textY = r0;
            r9.numOffsetY = r13;
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.lastMaxNumCalcWidth;
            if (r0 != r10) goto L_0x0047;
        L_0x0039:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.lastFontSize;
            r1 = org.telegram.messenger.SharedConfig.ivFontSize;
            if (r0 == r1) goto L_0x00fa;
        L_0x0047:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0.lastMaxNumCalcWidth = r10;
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r1 = org.telegram.messenger.SharedConfig.ivFontSize;
            r0.lastFontSize = r1;
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0.maxNumWidth = r13;
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.items;
            r8 = r0.size();
            r14 = 0;
        L_0x0073:
            if (r14 >= r8) goto L_0x00d3;
        L_0x0075:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.items;
            r0 = r0.get(r14);
            r15 = r0;
            r15 = (org.telegram.ui.ArticleViewer.TL_pageBlockOrderedListItem) r15;
            r0 = r15.num;
            if (r0 != 0) goto L_0x008d;
        L_0x008c:
            goto L_0x00d0;
        L_0x008d:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = r15.num;
            r3 = 0;
            r1 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r4 = r10 - r1;
            r5 = r9.textY;
            r6 = r9.currentBlock;
            r7 = r9.parentAdapter;
            r1 = r16;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7);
            r15.numLayout = r0;
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r1 = r9.currentBlock;
            r1 = r1.parent;
            r1 = r1.maxNumWidth;
            r2 = r15.numLayout;
            r2 = r2.getLineWidth(r13);
            r2 = (double) r2;
            r2 = java.lang.Math.ceil(r2);
            r2 = (int) r2;
            r1 = java.lang.Math.max(r1, r2);
            r0.maxNumWidth = r1;
        L_0x00d0:
            r14 = r14 + 1;
            goto L_0x0073;
        L_0x00d3:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r1 = r9.currentBlock;
            r1 = r1.parent;
            r1 = r1.maxNumWidth;
            r2 = org.telegram.ui.ArticleViewer.listTextNumPaint;
            r3 = "00.";
            r2 = r2.measureText(r3);
            r2 = (double) r2;
            r2 = java.lang.Math.ceil(r2);
            r2 = (int) r2;
            r1 = java.lang.Math.max(r1, r2);
            r0.maxNumWidth = r1;
        L_0x00fa:
            r0 = r9.parentAdapter;
            r0 = r0.isRtl;
            r1 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r2 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
            if (r0 == 0) goto L_0x010d;
        L_0x0106:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r9.textX = r0;
            goto L_0x0131;
        L_0x010d:
            r0 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r3 = r9.currentBlock;
            r3 = r3.parent;
            r3 = r3.maxNumWidth;
            r0 = r0 + r3;
            r3 = r9.currentBlock;
            r3 = r3.parent;
            r3 = r3.level;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r3 = r3 * r4;
            r0 = r0 + r3;
            r9.textX = r0;
        L_0x0131:
            r9.verticalAlign = r13;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r10 - r0;
            r3 = r9.textX;
            r0 = r0 - r3;
            r3 = r9.parentAdapter;
            r3 = r3.isRtl;
            if (r3 == 0) goto L_0x0167;
        L_0x0144:
            r3 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r4 = r9.currentBlock;
            r4 = r4.parent;
            r4 = r4.maxNumWidth;
            r3 = r3 + r4;
            r4 = r9.currentBlock;
            r4 = r4.parent;
            r4 = r4.level;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r4 = r4 * r1;
            r3 = r3 + r4;
            r0 = r0 - r3;
        L_0x0167:
            r4 = r0;
            r0 = r9.currentBlock;
            r0 = r0.textItem;
            r14 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            if (r0 == 0) goto L_0x01d7;
        L_0x0172:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r1 = r9.currentBlock;
            r3 = r1.textItem;
            r5 = r9.textY;
            r6 = r9.currentBlock;
            r1 = r9.parentAdapter;
            r1 = r1.isRtl;
            if (r1 == 0) goto L_0x018c;
        L_0x0187:
            r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT();
            goto L_0x018e;
        L_0x018c:
            r1 = android.text.Layout.Alignment.ALIGN_NORMAL;
        L_0x018e:
            r7 = r1;
            r8 = r9.parentAdapter;
            r1 = r16;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8);
            r9.textLayout = r0;
            r0 = r9.textLayout;
            if (r0 == 0) goto L_0x0327;
        L_0x019d:
            r0 = r0.getLineCount();
            if (r0 <= 0) goto L_0x0327;
        L_0x01a3:
            r0 = r9.currentBlock;
            r0 = r0.numLayout;
            if (r0 == 0) goto L_0x01ca;
        L_0x01ab:
            r0 = r9.currentBlock;
            r0 = r0.numLayout;
            r0 = r0.getLineCount();
            if (r0 <= 0) goto L_0x01ca;
        L_0x01b7:
            r0 = r9.textLayout;
            r0 = r0.getLineAscent(r13);
            r1 = r9.currentBlock;
            r1 = r1.numLayout;
            r1 = r1.getLineAscent(r13);
            r1 = r1 - r0;
            r9.numOffsetY = r1;
        L_0x01ca:
            r0 = r9.textLayout;
            r0 = r0.getHeight();
            r1 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r0 = r0 + r1;
            goto L_0x0326;
        L_0x01d7:
            r0 = r9.currentBlock;
            r0 = r0.blockItem;
            if (r0 == 0) goto L_0x0327;
        L_0x01df:
            r0 = r9.textX;
            r9.blockX = r0;
            r0 = r9.textY;
            r9.blockY = r0;
            r0 = r9.blockLayout;
            if (r0 == 0) goto L_0x0322;
        L_0x01eb:
            r0 = r0.itemView;
            r1 = r0 instanceof org.telegram.ui.ArticleViewer.BlockParagraphCell;
            if (r1 == 0) goto L_0x0217;
        L_0x01f1:
            r0 = r9.blockY;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r0 = r0 - r1;
            r9.blockY = r0;
            r0 = r9.parentAdapter;
            r0 = r0.isRtl;
            if (r0 != 0) goto L_0x020b;
        L_0x0202:
            r0 = r9.blockX;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 - r1;
            r9.blockX = r0;
        L_0x020b:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 + r4;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r1 = 0 - r1;
            goto L_0x0276;
        L_0x0217:
            r1 = r0 instanceof org.telegram.ui.ArticleViewer.BlockHeaderCell;
            if (r1 != 0) goto L_0x025f;
        L_0x021b:
            r1 = r0 instanceof org.telegram.ui.ArticleViewer.BlockSubheaderCell;
            if (r1 != 0) goto L_0x025f;
        L_0x021f:
            r1 = r0 instanceof org.telegram.ui.ArticleViewer.BlockTitleCell;
            if (r1 != 0) goto L_0x025f;
        L_0x0223:
            r0 = r0 instanceof org.telegram.ui.ArticleViewer.BlockSubtitleCell;
            if (r0 == 0) goto L_0x0228;
        L_0x0227:
            goto L_0x025f;
        L_0x0228:
            r0 = org.telegram.ui.ArticleViewer.this;
            r1 = r9.currentBlock;
            r1 = r1.blockItem;
            r0 = r0.isListItemBlock(r1);
            if (r0 == 0) goto L_0x0245;
        L_0x0236:
            r9.blockX = r13;
            r9.blockY = r13;
            r9.textY = r13;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r0 = 0 - r0;
            r1 = r0;
            r0 = r10;
            goto L_0x0276;
        L_0x0245:
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r0 = r0 instanceof org.telegram.ui.ArticleViewer.BlockTableCell;
            if (r0 == 0) goto L_0x025d;
        L_0x024d:
            r0 = r9.blockX;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 - r1;
            r9.blockX = r0;
            r0 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            goto L_0x0274;
        L_0x025d:
            r0 = r4;
            goto L_0x0275;
        L_0x025f:
            r0 = r9.parentAdapter;
            r0 = r0.isRtl;
            if (r0 != 0) goto L_0x0270;
        L_0x0267:
            r0 = r9.blockX;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 - r1;
            r9.blockX = r0;
        L_0x0270:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
        L_0x0274:
            r0 = r0 + r4;
        L_0x0275:
            r1 = 0;
        L_0x0276:
            r2 = r9.blockLayout;
            r2 = r2.itemView;
            r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3);
            r3 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r13);
            r2.measure(r0, r3);
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r0 = r0 instanceof org.telegram.ui.ArticleViewer.BlockParagraphCell;
            if (r0 == 0) goto L_0x02c6;
        L_0x028f:
            r0 = r9.currentBlock;
            r0 = r0.numLayout;
            if (r0 == 0) goto L_0x02c6;
        L_0x0297:
            r0 = r9.currentBlock;
            r0 = r0.numLayout;
            r0 = r0.getLineCount();
            if (r0 <= 0) goto L_0x02c6;
        L_0x02a3:
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r0 = (org.telegram.ui.ArticleViewer.BlockParagraphCell) r0;
            r2 = r0.textLayout;
            if (r2 == 0) goto L_0x02c6;
        L_0x02ad:
            r2 = r2.getLineCount();
            if (r2 <= 0) goto L_0x02c6;
        L_0x02b3:
            r0 = r0.textLayout;
            r0 = r0.getLineAscent(r13);
            r2 = r9.currentBlock;
            r2 = r2.numLayout;
            r2 = r2.getLineAscent(r13);
            r2 = r2 - r0;
            r9.numOffsetY = r2;
        L_0x02c6:
            r0 = r9.currentBlock;
            r0 = r0.blockItem;
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockDetails;
            if (r0 == 0) goto L_0x02da;
        L_0x02d0:
            r9.verticalAlign = r11;
            r9.blockY = r13;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r1 = r1 - r0;
            goto L_0x02f5;
        L_0x02da:
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r2 = r0 instanceof org.telegram.ui.ArticleViewer.BlockOrderedListItemCell;
            if (r2 == 0) goto L_0x02e9;
        L_0x02e2:
            r0 = (org.telegram.ui.ArticleViewer.BlockOrderedListItemCell) r0;
            r0 = r0.verticalAlign;
            r9.verticalAlign = r0;
            goto L_0x02f5;
        L_0x02e9:
            r2 = r0 instanceof org.telegram.ui.ArticleViewer.BlockListItemCell;
            if (r2 == 0) goto L_0x02f5;
        L_0x02ed:
            r0 = (org.telegram.ui.ArticleViewer.BlockListItemCell) r0;
            r0 = r0.verticalAlign;
            r9.verticalAlign = r0;
        L_0x02f5:
            r0 = r9.verticalAlign;
            if (r0 == 0) goto L_0x0318;
        L_0x02f9:
            r0 = r9.currentBlock;
            r0 = r0.numLayout;
            if (r0 == 0) goto L_0x0318;
        L_0x0301:
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r0 = r0.getMeasuredHeight();
            r2 = r9.currentBlock;
            r2 = r2.numLayout;
            r2 = r2.getHeight();
            r0 = r0 - r2;
            r0 = r0 / 2;
            r9.textY = r0;
        L_0x0318:
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r0 = r0.getMeasuredHeight();
            r13 = r1 + r0;
        L_0x0322:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
        L_0x0326:
            r13 = r13 + r0;
        L_0x0327:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.items;
            r1 = r9.currentBlock;
            r1 = r1.parent;
            r1 = r1.items;
            r1 = r1.size();
            r1 = r1 - r11;
            r0 = r0.get(r1);
            r1 = r9.currentBlock;
            if (r0 != r1) goto L_0x034d;
        L_0x0348:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r13 = r13 + r0;
        L_0x034d:
            r0 = r9.currentBlock;
            r0 = r0.index;
            if (r0 != 0) goto L_0x0366;
        L_0x0355:
            r0 = r9.currentBlock;
            r0 = r0.parent;
            r0 = r0.level;
            if (r0 != 0) goto L_0x0366;
        L_0x0361:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r12);
            r13 = r13 + r0;
        L_0x0366:
            r11 = r13;
            r0 = r9.textLayout;
            if (r0 == 0) goto L_0x038b;
        L_0x036b:
            r1 = r9.textX;
            r0.x = r1;
            r1 = r9.textY;
            r0.y = r1;
            r0 = r9.currentBlock;
            r0 = r0.numLayout;
            if (r0 == 0) goto L_0x038b;
        L_0x037b:
            r0 = r9.textLayout;
            r1 = r9.currentBlock;
            r1 = r1.numLayout;
            r1 = r1.textLayout;
            r1 = r1.getText();
            r0.prefix = r1;
        L_0x038b:
            r0 = r9.blockLayout;
            if (r0 == 0) goto L_0x03d8;
        L_0x038f:
            r0 = r0.itemView;
            r0 = r0 instanceof org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView;
            if (r0 == 0) goto L_0x03d8;
        L_0x0395:
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.textSelectionHelper;
            r0 = r0.arrayList;
            r0.clear();
            r0 = r9.blockLayout;
            r0 = r0.itemView;
            r0 = (org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r0;
            r1 = org.telegram.ui.ArticleViewer.this;
            r1 = r1.textSelectionHelper;
            r1 = r1.arrayList;
            r0.fillTextLayoutBlocks(r1);
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.textSelectionHelper;
            r0 = r0.arrayList;
            r0 = r0.iterator();
        L_0x03b7:
            r1 = r0.hasNext();
            if (r1 == 0) goto L_0x03d8;
        L_0x03bd:
            r1 = r0.next();
            r1 = (org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock) r1;
            r2 = r1 instanceof org.telegram.ui.ArticleViewer.DrawingText;
            if (r2 == 0) goto L_0x03b7;
        L_0x03c7:
            r1 = (org.telegram.ui.ArticleViewer.DrawingText) r1;
            r2 = r1.x;
            r3 = r9.blockX;
            r2 = r2 + r3;
            r1.x = r2;
            r2 = r1.y;
            r3 = r9.blockY;
            r2 = r2 + r3;
            r1.y = r2;
            goto L_0x03b7;
        L_0x03d8:
            r9.setMeasuredDimension(r10, r11);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$BlockOrderedListItemCell.onMeasure(int, int):void");
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                i = this.blockX;
                view.layout(i, this.blockY, view.getMeasuredWidth() + i, this.blockY + this.blockLayout.itemView.getMeasuredHeight());
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int measuredWidth = getMeasuredWidth();
                if (this.currentBlock.numLayout != null) {
                    canvas.save();
                    if (this.parentAdapter.isRtl) {
                        canvas.translate((float) (((measuredWidth - AndroidUtilities.dp(18.0f)) - this.currentBlock.parent.maxNumWidth) - (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f))), (float) (this.textY + this.numOffsetY));
                    } else {
                        canvas.translate((float) (((AndroidUtilities.dp(18.0f) + this.currentBlock.parent.maxNumWidth) - ((int) Math.ceil((double) this.currentBlock.numLayout.getLineWidth(0)))) + (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f))), (float) (this.textY + this.numOffsetY));
                    }
                    this.currentBlock.numLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }

        public void invalidate() {
            super.invalidate();
            ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                viewHolder.itemView.invalidate();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                accessibilityNodeInfo.setText(drawingText.getText());
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                if (view instanceof ArticleSelectableView) {
                    ((ArticleSelectableView) view).fillTextLayoutBlocks(arrayList);
                }
            }
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    public class BlockParagraphCell extends View implements ArticleSelectableView {
        private TL_pageBlockParagraph currentBlock;
        private WebpageAdapter parentAdapter;
        public DrawingText textLayout;
        public int textX;
        public int textY;

        public BlockParagraphCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TL_pageBlockParagraph tL_pageBlockParagraph) {
            this.currentBlock = tL_pageBlockParagraph;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            TL_pageBlockParagraph tL_pageBlockParagraph = this.currentBlock;
            int i3 = 0;
            if (tL_pageBlockParagraph != null) {
                i2 = tL_pageBlockParagraph.level;
                if (i2 == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((float) ((i2 * 14) + 18));
                }
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, (i - AndroidUtilities.dp(18.0f)) - this.textX, this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, 0, this.parentAdapter);
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    i2 = drawingText.getHeight();
                    if (this.currentBlock.level > 0) {
                        i3 = AndroidUtilities.dp(8.0f);
                    } else {
                        i3 = AndroidUtilities.dp(16.0f);
                    }
                    i3 = i2 + i3;
                    drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(i, i3);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                accessibilityNodeInfo.setText(drawingText.getText());
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockPhotoCell extends FrameLayout implements FileDownloadProgressListener, ArticleSelectableView {
        private int TAG;
        boolean autoDownload;
        private int buttonPressed;
        private int buttonState;
        private int buttonX;
        private int buttonY;
        private DrawingText captionLayout;
        private BlockChannelCell channelCell;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockPhoto currentBlock;
        private String currentFilter;
        private Photo currentPhoto;
        private PhotoSize currentPhotoObject;
        private PhotoSize currentPhotoObjectThumb;
        private String currentThumbFilter;
        private int currentType;
        private GroupedMessagePosition groupPosition;
        private ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private Drawable linkDrawable;
        private WebpageAdapter parentAdapter;
        private PageBlock parentBlock;
        private boolean photoPressed;
        private RadialProgress2 radialProgress;
        private int textX;
        private int textY;

        public void onProgressUpload(String str, long j, long j2, boolean z) {
        }

        public BlockPhotoCell(Context context, WebpageAdapter webpageAdapter, int i) {
            super(context);
            this.parentAdapter = webpageAdapter;
            setWillNotDraw(false);
            this.channelCell = new BlockChannelCell(context, this.parentAdapter, 1);
            this.radialProgress = new RadialProgress2(this);
            this.radialProgress.setProgressColor(-1);
            this.radialProgress.setColors(NUM, NUM, -1, -2500135);
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            addView(this.channelCell, LayoutHelper.createFrame(-1, -2.0f));
            this.currentType = i;
        }

        public void setBlock(TL_pageBlockPhoto tL_pageBlockPhoto, boolean z, boolean z2) {
            this.parentBlock = null;
            this.currentBlock = tL_pageBlockPhoto;
            this.isFirst = z;
            this.channelCell.setVisibility(4);
            if (!TextUtils.isEmpty(this.currentBlock.url)) {
                this.linkDrawable = getResources().getDrawable(NUM);
            }
            tL_pageBlockPhoto = this.currentBlock;
            if (tL_pageBlockPhoto != null) {
                Photo access$15300 = ArticleViewer.this.getPhotoWithId(tL_pageBlockPhoto.photo_id);
                if (access$15300 != null) {
                    this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(access$15300.sizes, AndroidUtilities.getPhotoSize());
                } else {
                    this.currentPhotoObject = null;
                }
            } else {
                this.currentPhotoObject = null;
            }
            updateButtonState(false);
            requestLayout();
        }

        public void setParentBlock(PageBlock pageBlock) {
            this.parentBlock = pageBlock;
            if (this.parentAdapter.channelBlock != null && (this.parentBlock instanceof TL_pageBlockCover)) {
                this.channelCell.setBlock(this.parentAdapter.channelBlock);
                this.channelCell.setVisibility(0);
            }
        }

        public View getChannelCell() {
            return this.channelCell;
        }

        /* JADX WARNING: Missing block: B:25:0x0095, code skipped:
            if (r1 <= ((float) (r0 + org.telegram.messenger.AndroidUtilities.dp(48.0f)))) goto L_0x009b;
     */
        /* JADX WARNING: Missing block: B:50:0x0103, code skipped:
            if (super.onTouchEvent(r12) == false) goto L_0x0106;
     */
        public boolean onTouchEvent(android.view.MotionEvent r12) {
            /*
            r11 = this;
            r0 = r12.getX();
            r1 = r12.getY();
            r2 = r11.channelCell;
            r2 = r2.getVisibility();
            r3 = 0;
            r4 = 1;
            if (r2 != 0) goto L_0x0060;
        L_0x0012:
            r2 = r11.channelCell;
            r2 = r2.getTranslationY();
            r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
            if (r2 <= 0) goto L_0x0060;
        L_0x001c:
            r2 = r11.channelCell;
            r2 = r2.getTranslationY();
            r5 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r5 = (float) r5;
            r2 = r2 + r5;
            r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
            if (r2 >= 0) goto L_0x0060;
        L_0x002e:
            r0 = r11.parentAdapter;
            r0 = r0.channelBlock;
            if (r0 == 0) goto L_0x005f;
        L_0x0036:
            r12 = r12.getAction();
            if (r12 != r4) goto L_0x005f;
        L_0x003c:
            r12 = org.telegram.ui.ArticleViewer.this;
            r12 = r12.currentAccount;
            r12 = org.telegram.messenger.MessagesController.getInstance(r12);
            r0 = r11.parentAdapter;
            r0 = r0.channelBlock;
            r0 = r0.channel;
            r0 = r0.username;
            r1 = org.telegram.ui.ArticleViewer.this;
            r1 = r1.parentFragment;
            r2 = 2;
            r12.openByUserName(r0, r1, r2);
            r12 = org.telegram.ui.ArticleViewer.this;
            r12.close(r3, r4);
        L_0x005f:
            return r4;
        L_0x0060:
            r2 = r12.getAction();
            if (r2 != 0) goto L_0x00a4;
        L_0x0066:
            r2 = r11.imageView;
            r2 = r2.isInsideImage(r0, r1);
            if (r2 == 0) goto L_0x00a4;
        L_0x006e:
            r2 = r11.buttonState;
            r5 = -1;
            if (r2 == r5) goto L_0x0097;
        L_0x0073:
            r2 = r11.buttonX;
            r5 = (float) r2;
            r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1));
            if (r5 < 0) goto L_0x0097;
        L_0x007a:
            r5 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r2 = r2 + r6;
            r2 = (float) r2;
            r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
            if (r0 > 0) goto L_0x0097;
        L_0x0086:
            r0 = r11.buttonY;
            r2 = (float) r0;
            r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
            if (r2 < 0) goto L_0x0097;
        L_0x008d:
            r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r0 = r0 + r2;
            r0 = (float) r0;
            r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
            if (r0 <= 0) goto L_0x009b;
        L_0x0097:
            r0 = r11.buttonState;
            if (r0 != 0) goto L_0x00a1;
        L_0x009b:
            r11.buttonPressed = r4;
            r11.invalidate();
            goto L_0x00d3;
        L_0x00a1:
            r11.photoPressed = r4;
            goto L_0x00d3;
        L_0x00a4:
            r0 = r12.getAction();
            if (r0 != r4) goto L_0x00c8;
        L_0x00aa:
            r0 = r11.photoPressed;
            if (r0 == 0) goto L_0x00b8;
        L_0x00ae:
            r11.photoPressed = r3;
            r0 = org.telegram.ui.ArticleViewer.this;
            r1 = r11.currentBlock;
            r0.openPhoto(r1);
            goto L_0x00d3;
        L_0x00b8:
            r0 = r11.buttonPressed;
            if (r0 != r4) goto L_0x00d3;
        L_0x00bc:
            r11.buttonPressed = r3;
            r11.playSoundEffect(r3);
            r11.didPressedButton(r4);
            r11.invalidate();
            goto L_0x00d3;
        L_0x00c8:
            r0 = r12.getAction();
            r1 = 3;
            if (r0 != r1) goto L_0x00d3;
        L_0x00cf:
            r11.photoPressed = r3;
            r11.buttonPressed = r3;
        L_0x00d3:
            r0 = r11.photoPressed;
            if (r0 != 0) goto L_0x0105;
        L_0x00d7:
            r0 = r11.buttonPressed;
            if (r0 != 0) goto L_0x0105;
        L_0x00db:
            r5 = org.telegram.ui.ArticleViewer.this;
            r8 = r11.captionLayout;
            r9 = r11.textX;
            r10 = r11.textY;
            r6 = r12;
            r7 = r11;
            r0 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10);
            if (r0 != 0) goto L_0x0105;
        L_0x00eb:
            r5 = org.telegram.ui.ArticleViewer.this;
            r8 = r11.creditLayout;
            r9 = r11.textX;
            r0 = r11.textY;
            r1 = r11.creditOffset;
            r10 = r0 + r1;
            r6 = r12;
            r7 = r11;
            r0 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10);
            if (r0 != 0) goto L_0x0105;
        L_0x00ff:
            r12 = super.onTouchEvent(r12);
            if (r12 == 0) goto L_0x0106;
        L_0x0105:
            r3 = 1;
        L_0x0106:
            return r3;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$BlockPhotoCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* Access modifiers changed, original: protected */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x0151  */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x014d  */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x0180  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x017e  */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x0133  */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x014d  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x0151  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x017e  */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x0180  */
        /* JADX WARNING: Removed duplicated region for block: B:62:0x018d  */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x0251  */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0315  */
        /* JADX WARNING: Removed duplicated region for block: B:106:0x0321  */
        /* JADX WARNING: Removed duplicated region for block: B:9:0x0047  */
        @android.annotation.SuppressLint({"NewApi"})
        public void onMeasure(int r28, int r29) {
            /*
            r27 = this;
            r10 = r27;
            r0 = android.view.View.MeasureSpec.getSize(r28);
            r1 = r10.currentType;
            r11 = 0;
            r12 = 2;
            r13 = 1;
            if (r1 != r13) goto L_0x0023;
        L_0x000d:
            r0 = r27.getParent();
            r0 = (android.view.View) r0;
            r0 = r0.getMeasuredWidth();
            r1 = r27.getParent();
            r1 = (android.view.View) r1;
            r1 = r1.getMeasuredHeight();
        L_0x0021:
            r14 = r0;
            goto L_0x0043;
        L_0x0023:
            if (r1 != r12) goto L_0x0041;
        L_0x0025:
            r1 = r10.groupPosition;
            r1 = r1.ph;
            r2 = org.telegram.messenger.AndroidUtilities.displaySize;
            r3 = r2.x;
            r2 = r2.y;
            r2 = java.lang.Math.max(r3, r2);
            r2 = (float) r2;
            r1 = r1 * r2;
            r2 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
            r1 = r1 * r2;
            r1 = (double) r1;
            r1 = java.lang.Math.ceil(r1);
            r1 = (int) r1;
            goto L_0x0021;
        L_0x0041:
            r14 = r0;
            r1 = 0;
        L_0x0043:
            r0 = r10.currentBlock;
            if (r0 == 0) goto L_0x032c;
        L_0x0047:
            r2 = org.telegram.ui.ArticleViewer.this;
            r3 = r0.photo_id;
            r0 = r2.getPhotoWithId(r3);
            r10.currentPhoto = r0;
            r0 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r2 = r10.currentType;
            r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
            if (r2 != 0) goto L_0x007a;
        L_0x005d:
            r2 = r10.currentBlock;
            r2 = r2.level;
            if (r2 <= 0) goto L_0x007a;
        L_0x0063:
            r2 = r2 * 14;
            r2 = (float) r2;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r2 = r2 + r4;
            r10.textX = r2;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r3 = r3 + r2;
            r3 = r14 - r3;
            r8 = r3;
            goto L_0x008b;
        L_0x007a:
            r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r10.textX = r2;
            r2 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r2 = r14 - r2;
            r8 = r2;
            r3 = r14;
            r2 = 0;
        L_0x008b:
            r4 = r10.currentPhoto;
            r15 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            if (r4 == 0) goto L_0x0238;
        L_0x0091:
            r5 = r10.currentPhotoObject;
            if (r5 == 0) goto L_0x0238;
        L_0x0095:
            r4 = r4.sizes;
            r5 = 40;
            r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5, r13);
            r10.currentPhotoObjectThumb = r4;
            r4 = r10.currentPhotoObject;
            r5 = r10.currentPhotoObjectThumb;
            r6 = 0;
            if (r4 != r5) goto L_0x00a8;
        L_0x00a6:
            r10.currentPhotoObjectThumb = r6;
        L_0x00a8:
            r4 = r10.currentType;
            r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            if (r4 != 0) goto L_0x00f4;
        L_0x00ae:
            r1 = (float) r3;
            r4 = r10.currentPhotoObject;
            r7 = r4.w;
            r7 = (float) r7;
            r1 = r1 / r7;
            r4 = r4.h;
            r4 = (float) r4;
            r1 = r1 * r4;
            r1 = (int) r1;
            r4 = r10.parentBlock;
            r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCover;
            if (r4 == 0) goto L_0x00c6;
        L_0x00c1:
            r1 = java.lang.Math.min(r1, r3);
            goto L_0x012c;
        L_0x00c6:
            r4 = org.telegram.messenger.AndroidUtilities.displaySize;
            r7 = r4.x;
            r4 = r4.y;
            r4 = java.lang.Math.max(r7, r4);
            r7 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
            r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
            r4 = r4 - r7;
            r4 = (float) r4;
            r7 = NUM; // 0x3var_ float:0.9 double:5.2552552E-315;
            r4 = r4 * r7;
            r4 = (int) r4;
            if (r1 <= r4) goto L_0x012c;
        L_0x00e0:
            r1 = (float) r4;
            r3 = r10.currentPhotoObject;
            r7 = r3.h;
            r7 = (float) r7;
            r1 = r1 / r7;
            r3 = r3.w;
            r3 = (float) r3;
            r1 = r1 * r3;
            r3 = (int) r1;
            r1 = r14 - r2;
            r1 = r1 - r3;
            r1 = r1 / r12;
            r2 = r2 + r1;
            r1 = r4;
            goto L_0x012c;
        L_0x00f4:
            if (r4 != r12) goto L_0x012c;
        L_0x00f6:
            r4 = r10.groupPosition;
            r4 = r4.flags;
            r4 = r4 & r12;
            if (r4 != 0) goto L_0x0102;
        L_0x00fd:
            r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r3 = r3 - r4;
        L_0x0102:
            r4 = r10.groupPosition;
            r4 = r4.flags;
            r4 = r4 & 8;
            if (r4 != 0) goto L_0x0111;
        L_0x010a:
            r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r4 = r1 - r4;
            goto L_0x0112;
        L_0x0111:
            r4 = r1;
        L_0x0112:
            r7 = r10.groupPosition;
            r7 = r7.leftSpanOffset;
            if (r7 == 0) goto L_0x0126;
        L_0x0118:
            r7 = r7 * r14;
            r7 = (float) r7;
            r9 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
            r7 = r7 / r9;
            r5 = (double) r7;
            r5 = java.lang.Math.ceil(r5);
            r5 = (int) r5;
            r3 = r3 - r5;
            r2 = r2 + r5;
        L_0x0126:
            r26 = r4;
            r4 = r1;
            r1 = r26;
            goto L_0x012d;
        L_0x012c:
            r4 = r1;
        L_0x012d:
            r5 = r10.imageView;
            r6 = r10.isFirst;
            if (r6 != 0) goto L_0x0145;
        L_0x0133:
            r6 = r10.currentType;
            if (r6 == r13) goto L_0x0145;
        L_0x0137:
            if (r6 == r12) goto L_0x0145;
        L_0x0139:
            r6 = r10.currentBlock;
            r6 = r6.level;
            if (r6 <= 0) goto L_0x0140;
        L_0x013f:
            goto L_0x0145;
        L_0x0140:
            r6 = org.telegram.messenger.AndroidUtilities.dp(r15);
            goto L_0x0146;
        L_0x0145:
            r6 = 0;
        L_0x0146:
            r5.setImageCoords(r2, r6, r3, r1);
            r2 = r10.currentType;
            if (r2 != 0) goto L_0x0151;
        L_0x014d:
            r2 = 0;
            r10.currentFilter = r2;
            goto L_0x0169;
        L_0x0151:
            r2 = java.util.Locale.US;
            r5 = new java.lang.Object[r12];
            r3 = java.lang.Integer.valueOf(r3);
            r5[r11] = r3;
            r1 = java.lang.Integer.valueOf(r1);
            r5[r13] = r1;
            r1 = "%d_%d";
            r1 = java.lang.String.format(r2, r1, r5);
            r10.currentFilter = r1;
        L_0x0169:
            r1 = "80_80_b";
            r10.currentThumbFilter = r1;
            r1 = org.telegram.ui.ArticleViewer.this;
            r1 = r1.currentAccount;
            r1 = org.telegram.messenger.DownloadController.getInstance(r1);
            r1 = r1.getCurrentDownloadMask();
            r1 = r1 & r13;
            if (r1 == 0) goto L_0x0180;
        L_0x017e:
            r1 = 1;
            goto L_0x0181;
        L_0x0180:
            r1 = 0;
        L_0x0181:
            r10.autoDownload = r1;
            r1 = r10.currentPhotoObject;
            r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r13);
            r2 = r10.autoDownload;
            if (r2 != 0) goto L_0x01cb;
        L_0x018d:
            r1 = r1.exists();
            if (r1 == 0) goto L_0x0194;
        L_0x0193:
            goto L_0x01cb;
        L_0x0194:
            r1 = r10.imageView;
            r2 = r10.currentPhotoObject;
            r3 = r10.currentPhoto;
            r2 = org.telegram.messenger.ImageLocation.getForPhoto(r2, r3);
            r1.setStrippedLocation(r2);
            r1 = r10.imageView;
            r18 = 0;
            r2 = r10.currentFilter;
            r3 = r10.currentPhotoObjectThumb;
            r5 = r10.currentPhoto;
            r20 = org.telegram.messenger.ImageLocation.getForPhoto(r3, r5);
            r3 = r10.currentThumbFilter;
            r5 = r10.currentPhotoObject;
            r5 = r5.size;
            r23 = 0;
            r6 = org.telegram.ui.ArticleViewer.this;
            r24 = r6.currentPage;
            r25 = 1;
            r17 = r1;
            r19 = r2;
            r21 = r3;
            r22 = r5;
            r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25);
            goto L_0x0200;
        L_0x01cb:
            r1 = r10.imageView;
            r2 = 0;
            r1.setStrippedLocation(r2);
            r1 = r10.imageView;
            r2 = r10.currentPhotoObject;
            r3 = r10.currentPhoto;
            r18 = org.telegram.messenger.ImageLocation.getForPhoto(r2, r3);
            r2 = r10.currentFilter;
            r3 = r10.currentPhotoObjectThumb;
            r5 = r10.currentPhoto;
            r20 = org.telegram.messenger.ImageLocation.getForPhoto(r3, r5);
            r3 = r10.currentThumbFilter;
            r5 = r10.currentPhotoObject;
            r5 = r5.size;
            r23 = 0;
            r6 = org.telegram.ui.ArticleViewer.this;
            r24 = r6.currentPage;
            r25 = 1;
            r17 = r1;
            r19 = r2;
            r21 = r3;
            r22 = r5;
            r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25);
        L_0x0200:
            r1 = r10.imageView;
            r1 = r1.getImageX();
            r1 = (float) r1;
            r2 = r10.imageView;
            r2 = r2.getImageWidth();
            r2 = r2 - r0;
            r2 = (float) r2;
            r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            r2 = r2 / r3;
            r1 = r1 + r2;
            r1 = (int) r1;
            r10.buttonX = r1;
            r1 = r10.imageView;
            r1 = r1.getImageY();
            r1 = (float) r1;
            r2 = r10.imageView;
            r2 = r2.getImageHeight();
            r2 = r2 - r0;
            r2 = (float) r2;
            r2 = r2 / r3;
            r1 = r1 + r2;
            r1 = (int) r1;
            r10.buttonY = r1;
            r1 = r10.radialProgress;
            r2 = r10.buttonX;
            r3 = r10.buttonY;
            r5 = r2 + r0;
            r0 = r0 + r3;
            r1.setProgressRect(r2, r3, r5, r0);
            r9 = r4;
            goto L_0x0239;
        L_0x0238:
            r9 = r1;
        L_0x0239:
            r0 = r10.imageView;
            r0 = r0.getImageY();
            r1 = r10.imageView;
            r1 = r1.getImageHeight();
            r0 = r0 + r1;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r15);
            r0 = r0 + r1;
            r10.textY = r0;
            r0 = r10.currentType;
            if (r0 != 0) goto L_0x02c8;
        L_0x0251:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r6 = r10.currentBlock;
            r1 = r6.caption;
            r3 = r1.text;
            r5 = r10.textY;
            r7 = r10.parentAdapter;
            r1 = r27;
            r4 = r8;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7);
            r10.captionLayout = r0;
            r0 = r10.captionLayout;
            r16 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
            if (r0 == 0) goto L_0x0282;
        L_0x026d:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
            r1 = r10.captionLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r10.creditOffset = r0;
            r0 = r10.creditOffset;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r16);
            r0 = r0 + r1;
            r9 = r9 + r0;
        L_0x0282:
            r17 = r9;
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r6 = r10.currentBlock;
            r1 = r6.caption;
            r3 = r1.credit;
            r1 = r10.textY;
            r4 = r10.creditOffset;
            r5 = r1 + r4;
            r1 = r10.parentAdapter;
            r1 = r1.isRtl;
            if (r1 == 0) goto L_0x02a0;
        L_0x029b:
            r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT();
            goto L_0x02a2;
        L_0x02a0:
            r1 = android.text.Layout.Alignment.ALIGN_NORMAL;
        L_0x02a2:
            r7 = r1;
            r9 = 0;
            r4 = r10.parentAdapter;
            r1 = r27;
            r18 = r4;
            r4 = r8;
            r8 = r9;
            r9 = r18;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8, r9);
            r10.creditLayout = r0;
            r0 = r10.creditLayout;
            if (r0 == 0) goto L_0x02c6;
        L_0x02b8:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r16);
            r1 = r10.creditLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r9 = r17 + r0;
            goto L_0x02c8;
        L_0x02c6:
            r9 = r17;
        L_0x02c8:
            r0 = r10.isFirst;
            if (r0 != 0) goto L_0x02db;
        L_0x02cc:
            r0 = r10.currentType;
            if (r0 != 0) goto L_0x02db;
        L_0x02d0:
            r0 = r10.currentBlock;
            r0 = r0.level;
            if (r0 > 0) goto L_0x02db;
        L_0x02d6:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
            r9 = r9 + r0;
        L_0x02db:
            r0 = r10.parentBlock;
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCover;
            if (r0 == 0) goto L_0x0304;
        L_0x02e1:
            r0 = r10.parentAdapter;
            r0 = r0.blocks;
            if (r0 == 0) goto L_0x0304;
        L_0x02e9:
            r0 = r10.parentAdapter;
            r0 = r0.blocks;
            r0 = r0.size();
            if (r0 <= r13) goto L_0x0304;
        L_0x02f5:
            r0 = r10.parentAdapter;
            r0 = r0.blocks;
            r0 = r0.get(r13);
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockChannel;
            if (r0 == 0) goto L_0x0304;
        L_0x0303:
            goto L_0x0305;
        L_0x0304:
            r13 = 0;
        L_0x0305:
            r0 = r10.currentType;
            if (r0 == r12) goto L_0x0310;
        L_0x0309:
            if (r13 != 0) goto L_0x0310;
        L_0x030b:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
            r9 = r9 + r0;
        L_0x0310:
            r13 = r9;
            r0 = r10.captionLayout;
            if (r0 == 0) goto L_0x031d;
        L_0x0315:
            r1 = r10.textX;
            r0.x = r1;
            r1 = r10.textY;
            r0.y = r1;
        L_0x031d:
            r0 = r10.creditLayout;
            if (r0 == 0) goto L_0x032c;
        L_0x0321:
            r1 = r10.textX;
            r0.x = r1;
            r1 = r10.textY;
            r2 = r10.creditOffset;
            r1 = r1 + r2;
            r0.y = r1;
        L_0x032c:
            r0 = r10.channelCell;
            r1 = r28;
            r2 = r29;
            r0.measure(r1, r2);
            r0 = r10.channelCell;
            r1 = r10.imageView;
            r1 = r1.getImageHeight();
            r2 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r1 = r1 - r2;
            r1 = (float) r1;
            r0.setTranslationY(r1);
            r10.setMeasuredDimension(r14, r13);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$BlockPhotoCell.onMeasure(int, int):void");
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int measuredWidth;
                int imageY;
                int i;
                if (!(this.imageView.hasBitmapImage() && this.imageView.getCurrentAlpha() == 1.0f)) {
                    canvas.drawRect((float) this.imageView.getImageX(), (float) this.imageView.getImageY(), (float) this.imageView.getImageX2(), (float) this.imageView.getImageY2(), ArticleViewer.photoBackgroundPaint);
                }
                this.imageView.draw(canvas);
                if (this.imageView.getVisible()) {
                    this.radialProgress.draw(canvas);
                }
                if (!TextUtils.isEmpty(this.currentBlock.url)) {
                    measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(35.0f);
                    imageY = this.imageView.getImageY() + AndroidUtilities.dp(11.0f);
                    this.linkDrawable.setBounds(measuredWidth, imageY, AndroidUtilities.dp(24.0f) + measuredWidth, AndroidUtilities.dp(24.0f) + imageY);
                    this.linkDrawable.draw(canvas);
                }
                imageY = 0;
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    i = 1;
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                } else {
                    i = 0;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, i);
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    float dp = (float) AndroidUtilities.dp(18.0f);
                    float dp2 = (float) AndroidUtilities.dp(20.0f);
                    measuredWidth = getMeasuredHeight();
                    if (this.currentBlock.bottom) {
                        imageY = AndroidUtilities.dp(6.0f);
                    }
                    canvas.drawRect(dp, 0.0f, dp2, (float) (measuredWidth - imageY), ArticleViewer.quoteLinePaint);
                }
            }
        }

        private int getIconForCurrentState() {
            int i = this.buttonState;
            if (i == 0) {
                return 2;
            }
            return i == 1 ? 3 : 4;
        }

        private void didPressedButton(boolean z) {
            int i = this.buttonState;
            if (i == 0) {
                this.radialProgress.setProgress(0.0f, z);
                this.imageView.setImage(ImageLocation.getForPhoto(this.currentPhotoObject, this.currentPhoto), this.currentFilter, ImageLocation.getForPhoto(this.currentPhotoObjectThumb, this.currentPhoto), this.currentThumbFilter, this.currentPhotoObject.size, null, ArticleViewer.this.currentPage, 1);
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), true, z);
                invalidate();
            } else if (i == 1) {
                this.imageView.cancelLoadImage();
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                invalidate();
            }
        }

        public void updateButtonState(boolean z) {
            String attachFileName = FileLoader.getAttachFileName(this.currentPhotoObject);
            boolean exists = FileLoader.getPathToAttach(this.currentPhotoObject, true).exists();
            if (TextUtils.isEmpty(attachFileName)) {
                this.radialProgress.setIcon(4, false, false);
                return;
            }
            if (exists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                this.buttonState = -1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                invalidate();
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(attachFileName, null, this);
                float f = 0.0f;
                if (this.autoDownload || FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(attachFileName)) {
                    this.buttonState = 1;
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                    if (fileProgress != null) {
                        f = fileProgress.floatValue();
                    }
                } else {
                    this.buttonState = 0;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), true, z);
                this.radialProgress.setProgress(f, false);
                invalidate();
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageView.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        /* Access modifiers changed, original: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.imageView.onAttachedToWindow();
            updateButtonState(false);
        }

        public void onFailedDownload(String str, boolean z) {
            updateButtonState(false);
        }

        public void onSuccessDownload(String str) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        public void onProgressDownload(String str, long j, long j2) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
            if (this.buttonState != 1) {
                updateButtonState(true);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            StringBuilder stringBuilder = new StringBuilder(LocaleController.getString("AttachPhoto", NUM));
            if (this.captionLayout != null) {
                stringBuilder.append(", ");
                stringBuilder.append(this.captionLayout.getText());
            }
            accessibilityNodeInfo.setText(stringBuilder.toString());
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.creditLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockPreformattedCell extends FrameLayout implements ArticleSelectableView {
        private TL_pageBlockPreformatted currentBlock;
        private WebpageAdapter parentAdapter;
        private HorizontalScrollView scrollView;
        private View textContainer;
        private DrawingText textLayout;

        public BlockPreformattedCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            this.scrollView = new HorizontalScrollView(context, ArticleViewer.this) {
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (BlockPreformattedCell.this.textContainer.getMeasuredWidth() > getMeasuredWidth()) {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                    return super.onInterceptTouchEvent(motionEvent);
                }

                /* Access modifiers changed, original: protected */
                public void onScrollChanged(int i, int i2, int i3, int i4) {
                    super.onScrollChanged(i, i2, i3, i4);
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null) {
                        ArticleViewer.this.pressedLinkOwnerLayout = null;
                        ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                }
            };
            this.scrollView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            addView(this.scrollView, LayoutHelper.createFrame(-1, -2.0f));
            this.textContainer = new View(context, ArticleViewer.this) {
                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    i2 = 0;
                    int i3 = 1;
                    if (BlockPreformattedCell.this.currentBlock != null) {
                        BlockPreformattedCell blockPreformattedCell = BlockPreformattedCell.this;
                        blockPreformattedCell.textLayout = ArticleViewer.this.createLayoutForText(this, null, blockPreformattedCell.currentBlock.text, AndroidUtilities.dp(5000.0f), 0, BlockPreformattedCell.this.currentBlock, BlockPreformattedCell.this.parentAdapter);
                        if (BlockPreformattedCell.this.textLayout != null) {
                            i = BlockPreformattedCell.this.textLayout.getHeight() + 0;
                            int lineCount = BlockPreformattedCell.this.textLayout.getLineCount();
                            while (i2 < lineCount) {
                                i3 = Math.max((int) Math.ceil((double) BlockPreformattedCell.this.textLayout.getLineWidth(i2)), i3);
                                i2++;
                            }
                            BlockPreformattedCell.this.textLayout.x = (int) getX();
                            BlockPreformattedCell.this.textLayout.y = (int) getY();
                        } else {
                            i = 0;
                        }
                    } else {
                        i = 1;
                    }
                    setMeasuredDimension(i3 + AndroidUtilities.dp(32.0f), i);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    BlockPreformattedCell blockPreformattedCell = BlockPreformattedCell.this;
                    return ArticleViewer.this.checkLayoutForLinks(motionEvent, this, blockPreformattedCell.textLayout, 0, 0) || super.onTouchEvent(motionEvent);
                }

                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    if (BlockPreformattedCell.this.textLayout != null) {
                        canvas.save();
                        BlockPreformattedCell blockPreformattedCell = BlockPreformattedCell.this;
                        ArticleViewer.this.drawTextSelection(canvas, blockPreformattedCell);
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

        public void setBlock(TL_pageBlockPreformatted tL_pageBlockPreformatted) {
            this.currentBlock = tL_pageBlockPreformatted;
            this.scrollView.setScrollX(0);
            this.textContainer.requestLayout();
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            this.scrollView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(0, 0));
            setMeasuredDimension(i, this.scrollView.getMeasuredHeight());
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.drawRect(0.0f, (float) AndroidUtilities.dp(8.0f), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(8.0f)), ArticleViewer.preformattedBackgroundPaint);
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockPullquoteCell extends View implements ArticleSelectableView {
        private TL_pageBlockPullquote currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);
        private int textY2;

        public BlockPullquoteCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TL_pageBlockPullquote tL_pageBlockPullquote) {
            this.currentBlock = tL_pageBlockPullquote;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (!(ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(motionEvent))) {
                    return false;
                }
            }
            return true;
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            int i3;
            i = MeasureSpec.getSize(i);
            TL_pageBlockPullquote tL_pageBlockPullquote = this.currentBlock;
            if (tL_pageBlockPullquote != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, tL_pageBlockPullquote.text, i - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter);
                i3 = 0;
                if (this.textLayout != null) {
                    i3 = 0 + (AndroidUtilities.dp(8.0f) + this.textLayout.getHeight());
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
                this.textY2 = AndroidUtilities.dp(2.0f) + i3;
                this.textLayout2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption, i - AndroidUtilities.dp(36.0f), this.textY2, this.currentBlock, this.parentAdapter);
                if (this.textLayout2 != null) {
                    i3 += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                    DrawingText drawingText2 = this.textLayout2;
                    drawingText2.x = this.textX;
                    drawingText2.y = this.textY2;
                }
                if (i3 != 0) {
                    i3 += AndroidUtilities.dp(8.0f);
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(i, i3);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int i = 0;
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                    i = 1;
                }
                if (this.textLayout2 != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY2);
                    ArticleViewer.this.drawTextSelection(canvas, this, i);
                    this.textLayout2.draw(canvas);
                    canvas.restore();
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.textLayout2;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockRelatedArticlesCell extends View implements ArticleSelectableView {
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

        public BlockRelatedArticlesCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            this.imageView = new ImageReceiver(this);
            this.imageView.setRoundRadius(AndroidUtilities.dp(6.0f));
        }

        public void setBlock(TL_pageBlockRelatedArticlesChild tL_pageBlockRelatedArticlesChild) {
            this.currentBlock = tL_pageBlockRelatedArticlesChild;
            requestLayout();
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"DrawAllocation", "NewApi"})
        public void onMeasure(int i, int i2) {
            int dp;
            int i3;
            Object obj;
            int i4;
            String formatString;
            int size = MeasureSpec.getSize(i);
            this.divider = this.currentBlock.num != this.currentBlock.parent.articles.size() - 1;
            TL_pageRelatedArticle tL_pageRelatedArticle = (TL_pageRelatedArticle) this.currentBlock.parent.articles.get(this.currentBlock.num);
            int dp2 = AndroidUtilities.dp((float) (SharedConfig.ivFontSize - 16));
            long j = tL_pageRelatedArticle.photo_id;
            Photo access$15300 = j != 0 ? ArticleViewer.this.getPhotoWithId(j) : null;
            if (access$15300 != null) {
                this.drawImage = true;
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(access$15300.sizes, AndroidUtilities.getPhotoSize());
                PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(access$15300.sizes, 80, true);
                if (closestPhotoSizeWithSize == closestPhotoSizeWithSize2) {
                    closestPhotoSizeWithSize2 = null;
                }
                this.imageView.setImage(ImageLocation.getForPhoto(closestPhotoSizeWithSize, access$15300), "64_64", ImageLocation.getForPhoto(closestPhotoSizeWithSize2, access$15300), "64_64_b", closestPhotoSizeWithSize.size, null, ArticleViewer.this.currentPage, 1);
            } else {
                this.drawImage = false;
            }
            int dp3 = AndroidUtilities.dp(60.0f);
            int dp4 = size - AndroidUtilities.dp(36.0f);
            if (this.drawImage) {
                dp = AndroidUtilities.dp(44.0f);
                this.imageView.setImageCoords((size - dp) - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), dp, dp);
                dp4 -= this.imageView.getImageWidth() + AndroidUtilities.dp(6.0f);
            }
            int i5 = dp4;
            int dp5 = AndroidUtilities.dp(18.0f);
            String str = tL_pageRelatedArticle.title;
            if (str != null) {
                i3 = dp3;
                this.textLayout = ArticleViewer.this.createLayoutForText(this, str, null, i5, this.textY, this.currentBlock, Alignment.ALIGN_NORMAL, 3, this.parentAdapter);
            } else {
                i3 = dp3;
            }
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                dp = drawingText.getLineCount();
                dp4 = 4 - dp;
                this.textOffset = (this.textLayout.getHeight() + AndroidUtilities.dp(6.0f)) + dp2;
                dp5 += this.textLayout.getHeight();
                for (int i6 = 0; i6 < dp; i6++) {
                    if (this.textLayout.getLineLeft(i6) != 0.0f) {
                        obj = 1;
                        break;
                    }
                }
                obj = null;
                DrawingText drawingText2 = this.textLayout;
                drawingText2.x = this.textX;
                drawingText2.y = this.textY;
                i4 = dp4;
            } else {
                this.textOffset = 0;
                obj = null;
                i4 = 4;
            }
            if (tL_pageRelatedArticle.published_date != 0 && !TextUtils.isEmpty(tL_pageRelatedArticle.author)) {
                formatString = LocaleController.formatString("ArticleDateByAuthor", NUM, LocaleController.getInstance().chatFullDate.format(((long) tL_pageRelatedArticle.published_date) * 1000), tL_pageRelatedArticle.author);
            } else if (!TextUtils.isEmpty(tL_pageRelatedArticle.author)) {
                formatString = LocaleController.formatString("ArticleByAuthor", NUM, tL_pageRelatedArticle.author);
            } else if (tL_pageRelatedArticle.published_date != 0) {
                formatString = LocaleController.getInstance().chatFullDate.format(((long) tL_pageRelatedArticle.published_date) * 1000);
            } else if (TextUtils.isEmpty(tL_pageRelatedArticle.description)) {
                formatString = tL_pageRelatedArticle.url;
            } else {
                formatString = tL_pageRelatedArticle.description;
            }
            str = formatString;
            ArticleViewer articleViewer = ArticleViewer.this;
            int i7 = this.textOffset + this.textY;
            TL_pageBlockRelatedArticlesChild tL_pageBlockRelatedArticlesChild = this.currentBlock;
            Alignment ALIGN_RIGHT = (this.parentAdapter.isRtl || obj != null) ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL;
            this.textLayout2 = articleViewer.createLayoutForText(this, str, null, i5, i7, tL_pageBlockRelatedArticlesChild, ALIGN_RIGHT, i4, this.parentAdapter);
            DrawingText drawingText3 = this.textLayout2;
            if (drawingText3 != null) {
                dp5 += drawingText3.getHeight();
                if (this.textLayout != null) {
                    dp5 += AndroidUtilities.dp(6.0f) + dp2;
                }
                drawingText3 = this.textLayout2;
                drawingText3.x = this.textX;
                drawingText3.y = this.textY + this.textOffset;
            }
            setMeasuredDimension(size, Math.max(i3, dp5) + this.divider);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int i;
                if (this.drawImage) {
                    this.imageView.draw(canvas);
                }
                canvas.save();
                canvas.translate((float) this.textX, (float) AndroidUtilities.dp(10.0f));
                int i2 = 0;
                if (this.textLayout != null) {
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.textLayout.draw(canvas);
                    i = 1;
                } else {
                    i = 0;
                }
                if (this.textLayout2 != null) {
                    canvas.translate(0.0f, (float) this.textOffset);
                    ArticleViewer.this.drawTextSelection(canvas, this, i);
                    this.textLayout2.draw(canvas);
                }
                canvas.restore();
                if (this.divider) {
                    float dp = this.parentAdapter.isRtl ? 0.0f : (float) AndroidUtilities.dp(17.0f);
                    float measuredHeight = (float) (getMeasuredHeight() - 1);
                    i = getMeasuredWidth();
                    if (this.parentAdapter.isRtl) {
                        i2 = AndroidUtilities.dp(17.0f);
                    }
                    canvas.drawLine(dp, measuredHeight, (float) (i - i2), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.textLayout2;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockRelatedArticlesHeaderCell extends View implements ArticleSelectableView {
        private TL_pageBlockRelatedArticles currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockRelatedArticlesHeaderCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TL_pageBlockRelatedArticles tL_pageBlockRelatedArticles) {
            this.currentBlock = tL_pageBlockRelatedArticles;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            TL_pageBlockRelatedArticles tL_pageBlockRelatedArticles = this.currentBlock;
            if (tL_pageBlockRelatedArticles != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, tL_pageBlockRelatedArticles.title, i - AndroidUtilities.dp(52.0f), 0, this.currentBlock, Alignment.ALIGN_NORMAL, 1, this.parentAdapter);
                if (this.textLayout != null) {
                    this.textY = AndroidUtilities.dp(6.0f) + ((AndroidUtilities.dp(32.0f) - this.textLayout.getHeight()) / 2);
                }
            }
            if (this.textLayout != null) {
                setMeasuredDimension(i, AndroidUtilities.dp(38.0f));
                DrawingText drawingText = this.textLayout;
                drawingText.x = this.textX;
                drawingText.y = this.textY;
                return;
            }
            setMeasuredDimension(i, 1);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockSlideshowCell extends FrameLayout implements ArticleSelectableView {
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

        public BlockSlideshowCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            if (ArticleViewer.dotsPaint == null) {
                ArticleViewer.dotsPaint = new Paint(1);
                ArticleViewer.dotsPaint.setColor(-1);
            }
            this.innerListView = new ViewPager(context, ArticleViewer.this) {
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return super.onTouchEvent(motionEvent);
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    return super.onInterceptTouchEvent(motionEvent);
                }
            };
            this.innerListView.addOnPageChangeListener(new OnPageChangeListener(ArticleViewer.this) {
                public void onPageScrollStateChanged(int i) {
                }

                public void onPageScrolled(int i, float f, int i2) {
                    f = (float) BlockSlideshowCell.this.innerListView.getMeasuredWidth();
                    if (f != 0.0f) {
                        BlockSlideshowCell blockSlideshowCell = BlockSlideshowCell.this;
                        blockSlideshowCell.pageOffset = (((((float) i) * f) + ((float) i2)) - (((float) blockSlideshowCell.currentPage) * f)) / f;
                        BlockSlideshowCell.this.dotsContainer.invalidate();
                    }
                }

                public void onPageSelected(int i) {
                    BlockSlideshowCell.this.currentPage = i;
                    BlockSlideshowCell.this.dotsContainer.invalidate();
                }
            });
            ViewPager viewPager = this.innerListView;
            AnonymousClass3 anonymousClass3 = new PagerAdapter(ArticleViewer.this) {

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

                public boolean isViewFromObject(View view, Object obj) {
                    return ((ObjectContainer) obj).view == view;
                }

                public int getItemPosition(Object obj) {
                    return BlockSlideshowCell.this.currentBlock.items.contains(((ObjectContainer) obj).block) ? -1 : -2;
                }

                public Object instantiateItem(ViewGroup viewGroup, int i) {
                    View blockPhotoCell;
                    PageBlock pageBlock = (PageBlock) BlockSlideshowCell.this.currentBlock.items.get(i);
                    BlockSlideshowCell blockSlideshowCell;
                    if (pageBlock instanceof TL_pageBlockPhoto) {
                        blockSlideshowCell = BlockSlideshowCell.this;
                        blockPhotoCell = new BlockPhotoCell(blockSlideshowCell.getContext(), BlockSlideshowCell.this.parentAdapter, 1);
                        blockPhotoCell.setBlock((TL_pageBlockPhoto) pageBlock, true, true);
                    } else {
                        blockSlideshowCell = BlockSlideshowCell.this;
                        blockPhotoCell = new BlockVideoCell(blockSlideshowCell.getContext(), BlockSlideshowCell.this.parentAdapter, 1);
                        blockPhotoCell.setBlock((TL_pageBlockVideo) pageBlock, true, true);
                    }
                    viewGroup.addView(blockPhotoCell);
                    ObjectContainer objectContainer = new ObjectContainer();
                    objectContainer.view = blockPhotoCell;
                    objectContainer.block = pageBlock;
                    return objectContainer;
                }

                public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
                    viewGroup.removeView(((ObjectContainer) obj).view);
                }

                public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
                    if (dataSetObserver != null) {
                        super.unregisterDataSetObserver(dataSetObserver);
                    }
                }
            };
            this.innerAdapter = anonymousClass3;
            viewPager.setAdapter(anonymousClass3);
            AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, Theme.getColor("windowBackgroundWhite"));
            addView(this.innerListView);
            this.dotsContainer = new View(context, ArticleViewer.this) {
                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    if (BlockSlideshowCell.this.currentBlock != null) {
                        int dp;
                        int measuredWidth;
                        int count = BlockSlideshowCell.this.innerAdapter.getCount();
                        int dp2 = ((AndroidUtilities.dp(7.0f) * count) + ((count - 1) * AndroidUtilities.dp(6.0f))) + AndroidUtilities.dp(4.0f);
                        if (dp2 < getMeasuredWidth()) {
                            count = (getMeasuredWidth() - dp2) / 2;
                        } else {
                            dp2 = AndroidUtilities.dp(4.0f);
                            dp = AndroidUtilities.dp(13.0f);
                            measuredWidth = ((getMeasuredWidth() - AndroidUtilities.dp(8.0f)) / 2) / dp;
                            int i = (count - measuredWidth) - 1;
                            if (BlockSlideshowCell.this.currentPage != i || BlockSlideshowCell.this.pageOffset >= 0.0f) {
                                if (BlockSlideshowCell.this.currentPage >= i) {
                                    count = ((count - (measuredWidth * 2)) - 1) * dp;
                                } else if (BlockSlideshowCell.this.currentPage > measuredWidth) {
                                    count = ((int) (BlockSlideshowCell.this.pageOffset * ((float) dp))) + ((BlockSlideshowCell.this.currentPage - measuredWidth) * dp);
                                } else {
                                    count = (BlockSlideshowCell.this.currentPage != measuredWidth || BlockSlideshowCell.this.pageOffset <= 0.0f) ? dp2 : (int) (BlockSlideshowCell.this.pageOffset * ((float) dp));
                                }
                                count = dp2 - count;
                            } else {
                                count = dp2 - (((int) (BlockSlideshowCell.this.pageOffset * ((float) dp))) + (((count - (measuredWidth * 2)) - 1) * dp));
                            }
                        }
                        dp = 0;
                        while (dp < BlockSlideshowCell.this.currentBlock.items.size()) {
                            measuredWidth = (AndroidUtilities.dp(4.0f) + count) + (AndroidUtilities.dp(13.0f) * dp);
                            Drawable access$20700 = BlockSlideshowCell.this.currentPage == dp ? ArticleViewer.this.slideDotBigDrawable : ArticleViewer.this.slideDotDrawable;
                            access$20700.setBounds(measuredWidth - AndroidUtilities.dp(5.0f), 0, measuredWidth + AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f));
                            access$20700.draw(canvas);
                            dp++;
                        }
                    }
                }
            };
            addView(this.dotsContainer);
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockSlideshow tL_pageBlockSlideshow) {
            this.currentBlock = tL_pageBlockSlideshow;
            this.innerAdapter.notifyDataSetChanged();
            this.innerListView.setCurrentItem(0, false);
            this.innerListView.forceLayout();
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.captionLayout, this.textX, this.textY)) {
                if (!(ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(motionEvent))) {
                    return false;
                }
            }
            return true;
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            if (this.currentBlock != null) {
                i2 = AndroidUtilities.dp(310.0f);
                this.innerListView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
                this.currentBlock.items.size();
                this.dotsContainer.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f), NUM));
                int dp = i - AndroidUtilities.dp(36.0f);
                this.textY = AndroidUtilities.dp(16.0f) + i2;
                ArticleViewer articleViewer = ArticleViewer.this;
                TL_pageBlockSlideshow tL_pageBlockSlideshow = this.currentBlock;
                this.captionLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockSlideshow.caption.text, dp, this.textY, tL_pageBlockSlideshow, this.parentAdapter);
                if (this.captionLayout != null) {
                    this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    i2 += this.creditOffset + AndroidUtilities.dp(4.0f);
                    DrawingText drawingText = this.captionLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                } else {
                    this.creditOffset = 0;
                }
                articleViewer = ArticleViewer.this;
                tL_pageBlockSlideshow = this.currentBlock;
                this.creditLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockSlideshow.caption.credit, dp, this.textY + this.creditOffset, tL_pageBlockSlideshow, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.creditLayout != null) {
                    i2 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    DrawingText drawingText2 = this.creditLayout;
                    drawingText2.x = this.textX;
                    drawingText2.y = this.textY + this.creditOffset;
                }
                i2 += AndroidUtilities.dp(16.0f);
            } else {
                i2 = 1;
            }
            setMeasuredDimension(i, i2);
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            this.innerListView.layout(0, AndroidUtilities.dp(8.0f), this.innerListView.getMeasuredWidth(), AndroidUtilities.dp(8.0f) + this.innerListView.getMeasuredHeight());
            int bottom = this.innerListView.getBottom() - AndroidUtilities.dp(23.0f);
            View view = this.dotsContainer;
            view.layout(0, bottom, view.getMeasuredWidth(), this.dotsContainer.getMeasuredHeight() + bottom);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int i = 0;
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                    i = 1;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, i);
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.creditLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockSubheaderCell extends View implements ArticleSelectableView {
        private TL_pageBlockSubheader currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockSubheaderCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TL_pageBlockSubheader tL_pageBlockSubheader) {
            this.currentBlock = tL_pageBlockSubheader;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            TL_pageBlockSubheader tL_pageBlockSubheader = this.currentBlock;
            int i3 = 0;
            if (tL_pageBlockSubheader != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, tL_pageBlockSubheader.text, i - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    i3 = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(i, i3);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            if (this.textLayout != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.textLayout.getText());
                stringBuilder.append(", ");
                stringBuilder.append(LocaleController.getString("AccDescrIVHeading", NUM));
                accessibilityNodeInfo.setText(stringBuilder.toString());
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockSubtitleCell extends View implements ArticleSelectableView {
        private TL_pageBlockSubtitle currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockSubtitleCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TL_pageBlockSubtitle tL_pageBlockSubtitle) {
            this.currentBlock = tL_pageBlockSubtitle;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            TL_pageBlockSubtitle tL_pageBlockSubtitle = this.currentBlock;
            int i3 = 0;
            if (tL_pageBlockSubtitle != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, tL_pageBlockSubtitle.text, i - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    i3 = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(i, i3);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            if (this.textLayout != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.textLayout.getText());
                stringBuilder.append(", ");
                stringBuilder.append(LocaleController.getString("AccDescrIVHeading", NUM));
                accessibilityNodeInfo.setText(stringBuilder.toString());
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    public class BlockTableCell extends FrameLayout implements TableLayoutDelegate, ArticleSelectableView {
        private TL_pageBlockTable currentBlock;
        private boolean firstLayout;
        private int listX;
        private int listY;
        private WebpageAdapter parentAdapter;
        private HorizontalScrollView scrollView;
        private TableLayout tableLayout;
        private int textX;
        private int textY;
        private DrawingText titleLayout;

        public BlockTableCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            this.scrollView = new HorizontalScrollView(context, ArticleViewer.this) {
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    boolean onInterceptTouchEvent = super.onInterceptTouchEvent(motionEvent);
                    if (BlockTableCell.this.tableLayout.getMeasuredWidth() > getMeasuredWidth() - AndroidUtilities.dp(36.0f) && onInterceptTouchEvent) {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                    return onInterceptTouchEvent;
                }

                /* Access modifiers changed, original: protected */
                public void onScrollChanged(int i, int i2, int i3, int i4) {
                    super.onScrollChanged(i, i2, i3, i4);
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null) {
                        ArticleViewer.this.pressedLinkOwnerLayout = null;
                        ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                    BlockTableCell.this.updateChildTextPositions();
                    ArticleTextSelectionHelper articleTextSelectionHelper = ArticleViewer.this.textSelectionHelper;
                    if (articleTextSelectionHelper != null && articleTextSelectionHelper.isSelectionMode()) {
                        ArticleViewer.this.textSelectionHelper.invalidate();
                    }
                }

                /* Access modifiers changed, original: protected */
                public boolean overScrollBy(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
                    ArticleViewer.this.removePressedLink();
                    return super.overScrollBy(i, i2, i3, i4, i5, i6, i7, i8, z);
                }

                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    BlockTableCell.this.tableLayout.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(i) - getPaddingLeft()) - getPaddingRight(), 0), i2);
                    setMeasuredDimension(MeasureSpec.getSize(i), BlockTableCell.this.tableLayout.getMeasuredHeight());
                }
            };
            this.scrollView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.scrollView.setClipToPadding(false);
            addView(this.scrollView, LayoutHelper.createFrame(-1, -2.0f));
            this.tableLayout = new TableLayout(context, this, ArticleViewer.this.textSelectionHelper);
            this.tableLayout.setOrientation(0);
            this.tableLayout.setRowOrderPreserved(true);
            this.scrollView.addView(this.tableLayout, new FrameLayout.LayoutParams(-2, -2));
            setWillNotDraw(false);
        }

        public DrawingText createTextLayout(TL_pageTableCell tL_pageTableCell, int i) {
            if (tL_pageTableCell == null) {
                return null;
            }
            Alignment alignment;
            if (tL_pageTableCell.align_right) {
                alignment = Alignment.ALIGN_OPPOSITE;
            } else if (tL_pageTableCell.align_center) {
                alignment = Alignment.ALIGN_CENTER;
            } else {
                alignment = Alignment.ALIGN_NORMAL;
            }
            return ArticleViewer.this.createLayoutForText(this, null, tL_pageTableCell.text, i, -1, this.currentBlock, alignment, 0, this.parentAdapter);
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

        public void onLayoutChild(DrawingText drawingText, int i, int i2) {
            if (drawingText != null && !ArticleViewer.this.searchResults.isEmpty() && ArticleViewer.this.searchText != null) {
                String toLowerCase = drawingText.textLayout.getText().toString().toLowerCase();
                int i3 = 0;
                while (true) {
                    i3 = toLowerCase.indexOf(ArticleViewer.this.searchText, i3);
                    if (i3 >= 0) {
                        int length = ArticleViewer.this.searchText.length() + i3;
                        if (i3 == 0 || AndroidUtilities.isPunctuationCharacter(toLowerCase.charAt(i3 - 1))) {
                            HashMap access$1300 = ArticleViewer.this.adapter[0].searchTextOffset;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(ArticleViewer.this.searchText);
                            stringBuilder.append(this.currentBlock);
                            stringBuilder.append(drawingText.parentText);
                            stringBuilder.append(i3);
                            String stringBuilder2 = stringBuilder.toString();
                            StaticLayout staticLayout = drawingText.textLayout;
                            access$1300.put(stringBuilder2, Integer.valueOf(staticLayout.getLineTop(staticLayout.getLineForOffset(i3)) + i2));
                        }
                        i3 = length;
                    } else {
                        return;
                    }
                }
            }
        }

        public void setBlock(TL_pageBlockTable tL_pageBlockTable) {
            int i;
            int size;
            int i2;
            this.currentBlock = tL_pageBlockTable;
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("windowBackgroundWhite"));
            this.tableLayout.removeAllChildrens();
            this.tableLayout.setDrawLines(this.currentBlock.bordered);
            this.tableLayout.setStriped(this.currentBlock.striped);
            this.tableLayout.setRtl(this.parentAdapter.isRtl);
            if (this.currentBlock.rows.isEmpty()) {
                i = 0;
            } else {
                TL_pageTableRow tL_pageTableRow = (TL_pageTableRow) this.currentBlock.rows.get(0);
                size = tL_pageTableRow.cells.size();
                i = 0;
                for (int i3 = 0; i3 < size; i3++) {
                    i2 = ((TL_pageTableCell) tL_pageTableRow.cells.get(i3)).colspan;
                    if (i2 == 0) {
                        i2 = 1;
                    }
                    i += i2;
                }
            }
            int size2 = this.currentBlock.rows.size();
            for (size = 0; size < size2; size++) {
                TL_pageTableRow tL_pageTableRow2 = (TL_pageTableRow) this.currentBlock.rows.get(size);
                i2 = tL_pageTableRow2.cells.size();
                int i4 = 0;
                for (int i5 = 0; i5 < i2; i5++) {
                    TL_pageTableCell tL_pageTableCell = (TL_pageTableCell) tL_pageTableRow2.cells.get(i5);
                    int i6 = tL_pageTableCell.colspan;
                    if (i6 == 0) {
                        i6 = 1;
                    }
                    int i7 = tL_pageTableCell.rowspan;
                    if (i7 == 0) {
                        i7 = 1;
                    }
                    if (tL_pageTableCell.text != null) {
                        this.tableLayout.addChild(tL_pageTableCell, i4, size, i6);
                    } else {
                        this.tableLayout.addChild(i4, size, i6, i7);
                    }
                    i4 += i6;
                }
            }
            this.tableLayout.setColumnCount(i);
            this.firstLayout = true;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            int childCount = this.tableLayout.getChildCount();
            boolean z = false;
            for (int i = 0; i < childCount; i++) {
                Child childAt = this.tableLayout.getChildAt(i);
                if (ArticleViewer.this.checkLayoutForLinks(motionEvent, this, childAt.textLayout, ((this.scrollView.getPaddingLeft() - this.scrollView.getScrollX()) + this.listX) + childAt.getTextX(), this.listY + childAt.getTextY())) {
                    return true;
                }
            }
            if (ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.titleLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent)) {
                z = true;
            }
            return z;
        }

        public void invalidate() {
            super.invalidate();
            this.tableLayout.invalidate();
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            TL_pageBlockTable tL_pageBlockTable = this.currentBlock;
            if (tL_pageBlockTable != null) {
                i2 = tL_pageBlockTable.level;
                if (i2 > 0) {
                    this.listX = AndroidUtilities.dp((float) (i2 * 14));
                    this.textX = this.listX + AndroidUtilities.dp(18.0f);
                    i2 = this.textX;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    i2 = AndroidUtilities.dp(36.0f);
                }
                int i3 = i - i2;
                ArticleViewer articleViewer = ArticleViewer.this;
                TL_pageBlockTable tL_pageBlockTable2 = this.currentBlock;
                this.titleLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockTable2.title, i3, 0, tL_pageBlockTable2, Alignment.ALIGN_CENTER, 0, this.parentAdapter);
                DrawingText drawingText = this.titleLayout;
                if (drawingText != null) {
                    this.textY = 0;
                    i2 = (drawingText.getHeight() + AndroidUtilities.dp(8.0f)) + 0;
                    this.listY = i2;
                    DrawingText drawingText2 = this.titleLayout;
                    drawingText2.x = this.textX;
                    drawingText2.y = this.textY;
                } else {
                    this.listY = AndroidUtilities.dp(8.0f);
                    i2 = 0;
                }
                this.scrollView.measure(MeasureSpec.makeMeasureSpec(i - this.listX, NUM), MeasureSpec.makeMeasureSpec(0, 0));
                i2 += this.scrollView.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                TL_pageBlockTable tL_pageBlockTable3 = this.currentBlock;
                if (tL_pageBlockTable3.level > 0 && !tL_pageBlockTable3.bottom) {
                    i2 += AndroidUtilities.dp(8.0f);
                }
            } else {
                i2 = 1;
            }
            setMeasuredDimension(i, i2);
            updateChildTextPositions();
        }

        private void updateChildTextPositions() {
            int i = this.titleLayout == null ? 0 : 1;
            int childCount = this.tableLayout.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                Child childAt = this.tableLayout.getChildAt(i2);
                DrawingText drawingText = childAt.textLayout;
                if (drawingText != null) {
                    drawingText.x = ((childAt.getTextX() + this.listX) + AndroidUtilities.dp(18.0f)) - this.scrollView.getScrollX();
                    childAt.textLayout.y = childAt.getTextY() + this.listY;
                    childAt.textLayout.row = childAt.getRow();
                    int i3 = i + 1;
                    childAt.setSelectionIndex(i);
                    i = i3;
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            HorizontalScrollView horizontalScrollView = this.scrollView;
            i = this.listX;
            horizontalScrollView.layout(i, this.listY, horizontalScrollView.getMeasuredWidth() + i, this.listY + this.scrollView.getMeasuredHeight());
            if (this.firstLayout) {
                if (this.parentAdapter.isRtl) {
                    this.scrollView.setScrollX((this.tableLayout.getMeasuredWidth() - this.scrollView.getMeasuredWidth()) + AndroidUtilities.dp(36.0f));
                } else {
                    this.scrollView.setScrollX(0);
                }
                this.firstLayout = false;
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int i = 0;
                if (this.titleLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.titleLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    float dp = (float) AndroidUtilities.dp(18.0f);
                    float dp2 = (float) AndroidUtilities.dp(20.0f);
                    int measuredHeight = getMeasuredHeight();
                    if (this.currentBlock.bottom) {
                        i = AndroidUtilities.dp(6.0f);
                    }
                    canvas.drawRect(dp, 0.0f, dp2, (float) (measuredHeight - i), ArticleViewer.quoteLinePaint);
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.titleLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            int childCount = this.tableLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                DrawingText drawingText2 = this.tableLayout.getChildAt(i).textLayout;
                if (drawingText2 != null) {
                    arrayList.add(drawingText2);
                }
            }
        }
    }

    private class BlockTitleCell extends View implements ArticleSelectableView {
        private TL_pageBlockTitle currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockTitleCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TL_pageBlockTitle tL_pageBlockTitle) {
            this.currentBlock = tL_pageBlockTitle;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* Access modifiers changed, original: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int i3;
            i = MeasureSpec.getSize(i);
            TL_pageBlockTitle tL_pageBlockTitle = this.currentBlock;
            if (tL_pageBlockTitle != null) {
                i3 = 0;
                if (tL_pageBlockTitle.first) {
                    i3 = 0 + AndroidUtilities.dp(8.0f);
                    this.textY = AndroidUtilities.dp(16.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
                }
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, i - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    i3 += AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(i, i3);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            if (this.textLayout != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.textLayout.getText());
                stringBuilder.append(", ");
                stringBuilder.append(LocaleController.getString("AccDescrIVTitle", NUM));
                accessibilityNodeInfo.setText(stringBuilder.toString());
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockVideoCell extends FrameLayout implements FileDownloadProgressListener, ArticleSelectableView {
        private int TAG;
        private boolean autoDownload;
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
        private WebpageAdapter parentAdapter;
        private PageBlock parentBlock;
        private boolean photoPressed;
        private RadialProgress2 radialProgress;
        private int textX;
        private int textY;

        public void onProgressUpload(String str, long j, long j2, boolean z) {
        }

        public BlockVideoCell(Context context, WebpageAdapter webpageAdapter, int i) {
            super(context);
            this.parentAdapter = webpageAdapter;
            setWillNotDraw(false);
            this.imageView.setNeedsQualityThumb(true);
            this.imageView.setShouldGenerateQualityThumb(true);
            this.currentType = i;
            this.radialProgress = new RadialProgress2(this);
            this.radialProgress.setProgressColor(-1);
            this.radialProgress.setColors(NUM, NUM, -1, -2500135);
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            this.channelCell = new BlockChannelCell(context, this.parentAdapter, 1);
            addView(this.channelCell, LayoutHelper.createFrame(-1, -2.0f));
        }

        public void setBlock(TL_pageBlockVideo tL_pageBlockVideo, boolean z, boolean z2) {
            this.currentBlock = tL_pageBlockVideo;
            this.parentBlock = null;
            this.currentDocument = ArticleViewer.this.getDocumentWithId(this.currentBlock.video_id);
            this.isGif = MessageObject.isGifDocument(this.currentDocument);
            this.isFirst = z;
            this.channelCell.setVisibility(4);
            updateButtonState(false);
            requestLayout();
        }

        public void setParentBlock(TL_pageBlockChannel tL_pageBlockChannel, PageBlock pageBlock) {
            this.parentBlock = pageBlock;
            if (tL_pageBlockChannel != null && (this.parentBlock instanceof TL_pageBlockCover)) {
                this.channelCell.setBlock(tL_pageBlockChannel);
                this.channelCell.setVisibility(0);
            }
        }

        public View getChannelCell() {
            return this.channelCell;
        }

        /* JADX WARNING: Missing block: B:25:0x0095, code skipped:
            if (r1 <= ((float) (r0 + org.telegram.messenger.AndroidUtilities.dp(48.0f)))) goto L_0x009b;
     */
        /* JADX WARNING: Missing block: B:50:0x0101, code skipped:
            if (super.onTouchEvent(r12) == false) goto L_0x0104;
     */
        public boolean onTouchEvent(android.view.MotionEvent r12) {
            /*
            r11 = this;
            r0 = r12.getX();
            r1 = r12.getY();
            r2 = r11.channelCell;
            r2 = r2.getVisibility();
            r3 = 0;
            r4 = 1;
            if (r2 != 0) goto L_0x0060;
        L_0x0012:
            r2 = r11.channelCell;
            r2 = r2.getTranslationY();
            r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
            if (r2 <= 0) goto L_0x0060;
        L_0x001c:
            r2 = r11.channelCell;
            r2 = r2.getTranslationY();
            r5 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r5 = (float) r5;
            r2 = r2 + r5;
            r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
            if (r2 >= 0) goto L_0x0060;
        L_0x002e:
            r0 = r11.parentAdapter;
            r0 = r0.channelBlock;
            if (r0 == 0) goto L_0x005f;
        L_0x0036:
            r12 = r12.getAction();
            if (r12 != r4) goto L_0x005f;
        L_0x003c:
            r12 = org.telegram.ui.ArticleViewer.this;
            r12 = r12.currentAccount;
            r12 = org.telegram.messenger.MessagesController.getInstance(r12);
            r0 = r11.parentAdapter;
            r0 = r0.channelBlock;
            r0 = r0.channel;
            r0 = r0.username;
            r1 = org.telegram.ui.ArticleViewer.this;
            r1 = r1.parentFragment;
            r2 = 2;
            r12.openByUserName(r0, r1, r2);
            r12 = org.telegram.ui.ArticleViewer.this;
            r12.close(r3, r4);
        L_0x005f:
            return r4;
        L_0x0060:
            r2 = r12.getAction();
            if (r2 != 0) goto L_0x00a4;
        L_0x0066:
            r2 = r11.imageView;
            r2 = r2.isInsideImage(r0, r1);
            if (r2 == 0) goto L_0x00a4;
        L_0x006e:
            r2 = r11.buttonState;
            r5 = -1;
            if (r2 == r5) goto L_0x0097;
        L_0x0073:
            r2 = r11.buttonX;
            r5 = (float) r2;
            r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1));
            if (r5 < 0) goto L_0x0097;
        L_0x007a:
            r5 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r2 = r2 + r6;
            r2 = (float) r2;
            r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
            if (r0 > 0) goto L_0x0097;
        L_0x0086:
            r0 = r11.buttonY;
            r2 = (float) r0;
            r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
            if (r2 < 0) goto L_0x0097;
        L_0x008d:
            r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r0 = r0 + r2;
            r0 = (float) r0;
            r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
            if (r0 <= 0) goto L_0x009b;
        L_0x0097:
            r0 = r11.buttonState;
            if (r0 != 0) goto L_0x00a1;
        L_0x009b:
            r11.buttonPressed = r4;
            r11.invalidate();
            goto L_0x00d1;
        L_0x00a1:
            r11.photoPressed = r4;
            goto L_0x00d1;
        L_0x00a4:
            r0 = r12.getAction();
            if (r0 != r4) goto L_0x00c8;
        L_0x00aa:
            r0 = r11.photoPressed;
            if (r0 == 0) goto L_0x00b8;
        L_0x00ae:
            r11.photoPressed = r3;
            r0 = org.telegram.ui.ArticleViewer.this;
            r1 = r11.currentBlock;
            r0.openPhoto(r1);
            goto L_0x00d1;
        L_0x00b8:
            r0 = r11.buttonPressed;
            if (r0 != r4) goto L_0x00d1;
        L_0x00bc:
            r11.buttonPressed = r3;
            r11.playSoundEffect(r3);
            r11.didPressedButton(r4);
            r11.invalidate();
            goto L_0x00d1;
        L_0x00c8:
            r0 = r12.getAction();
            r1 = 3;
            if (r0 != r1) goto L_0x00d1;
        L_0x00cf:
            r11.photoPressed = r3;
        L_0x00d1:
            r0 = r11.photoPressed;
            if (r0 != 0) goto L_0x0103;
        L_0x00d5:
            r0 = r11.buttonPressed;
            if (r0 != 0) goto L_0x0103;
        L_0x00d9:
            r5 = org.telegram.ui.ArticleViewer.this;
            r8 = r11.captionLayout;
            r9 = r11.textX;
            r10 = r11.textY;
            r6 = r12;
            r7 = r11;
            r0 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10);
            if (r0 != 0) goto L_0x0103;
        L_0x00e9:
            r5 = org.telegram.ui.ArticleViewer.this;
            r8 = r11.creditLayout;
            r9 = r11.textX;
            r0 = r11.textY;
            r1 = r11.creditOffset;
            r10 = r0 + r1;
            r6 = r12;
            r7 = r11;
            r0 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10);
            if (r0 != 0) goto L_0x0103;
        L_0x00fd:
            r12 = super.onTouchEvent(r12);
            if (r12 == 0) goto L_0x0104;
        L_0x0103:
            r3 = 1;
        L_0x0104:
            return r3;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$BlockVideoCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* Access modifiers changed, original: protected */
        /* JADX WARNING: Removed duplicated region for block: B:71:0x01ef  */
        /* JADX WARNING: Removed duplicated region for block: B:65:0x0161  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x0144  */
        /* JADX WARNING: Removed duplicated region for block: B:65:0x0161  */
        /* JADX WARNING: Removed duplicated region for block: B:71:0x01ef  */
        /* JADX WARNING: Removed duplicated region for block: B:73:0x024e  */
        /* JADX WARNING: Removed duplicated region for block: B:17:0x007c  */
        /* JADX WARNING: Removed duplicated region for block: B:76:0x0269  */
        /* JADX WARNING: Removed duplicated region for block: B:9:0x0046  */
        @android.annotation.SuppressLint({"NewApi"})
        public void onMeasure(int r31, int r32) {
            /*
            r30 = this;
            r9 = r30;
            r0 = android.view.View.MeasureSpec.getSize(r31);
            r1 = r9.currentType;
            r10 = 2;
            r12 = 1;
            if (r1 != r12) goto L_0x0022;
        L_0x000c:
            r0 = r30.getParent();
            r0 = (android.view.View) r0;
            r0 = r0.getMeasuredWidth();
            r1 = r30.getParent();
            r1 = (android.view.View) r1;
            r1 = r1.getMeasuredHeight();
        L_0x0020:
            r13 = r0;
            goto L_0x0042;
        L_0x0022:
            if (r1 != r10) goto L_0x0040;
        L_0x0024:
            r1 = r9.groupPosition;
            r1 = r1.ph;
            r2 = org.telegram.messenger.AndroidUtilities.displaySize;
            r3 = r2.x;
            r2 = r2.y;
            r2 = java.lang.Math.max(r3, r2);
            r2 = (float) r2;
            r1 = r1 * r2;
            r2 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
            r1 = r1 * r2;
            r1 = (double) r1;
            r1 = java.lang.Math.ceil(r1);
            r1 = (int) r1;
            goto L_0x0020;
        L_0x0040:
            r13 = r0;
            r1 = 0;
        L_0x0042:
            r0 = r9.currentBlock;
            if (r0 == 0) goto L_0x0334;
        L_0x0046:
            r2 = r9.currentType;
            r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
            if (r2 != 0) goto L_0x0067;
        L_0x004c:
            r0 = r0.level;
            if (r0 <= 0) goto L_0x0067;
        L_0x0050:
            r0 = r0 * 14;
            r0 = (float) r0;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r0 = r0 + r2;
            r9.textX = r0;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r2 = r2 + r0;
            r2 = r13 - r2;
            r8 = r2;
            goto L_0x0078;
        L_0x0067:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r9.textX = r0;
            r0 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r0 = r13 - r0;
            r8 = r0;
            r2 = r13;
            r0 = 0;
        L_0x0078:
            r3 = r9.currentDocument;
            if (r3 == 0) goto L_0x024e;
        L_0x007c:
            r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r4 = r9.currentDocument;
            r4 = r4.thumbs;
            r5 = 48;
            r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5);
            r5 = r9.currentType;
            r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            if (r5 != 0) goto L_0x0114;
        L_0x0092:
            r5 = r9.currentDocument;
            r5 = r5.attributes;
            r5 = r5.size();
            r7 = 0;
        L_0x009b:
            if (r7 >= r5) goto L_0x00bc;
        L_0x009d:
            r15 = r9.currentDocument;
            r15 = r15.attributes;
            r15 = r15.get(r7);
            r15 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r15;
            r11 = r15 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
            if (r11 == 0) goto L_0x00b9;
        L_0x00ab:
            r1 = (float) r2;
            r5 = r15.w;
            r5 = (float) r5;
            r1 = r1 / r5;
            r5 = r15.h;
            r5 = (float) r5;
            r1 = r1 * r5;
            r1 = (int) r1;
            r5 = r1;
            r1 = 1;
            goto L_0x00be;
        L_0x00b9:
            r7 = r7 + 1;
            goto L_0x009b;
        L_0x00bc:
            r5 = r1;
            r1 = 0;
        L_0x00be:
            r7 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            if (r4 == 0) goto L_0x00c6;
        L_0x00c2:
            r11 = r4.w;
            r11 = (float) r11;
            goto L_0x00c8;
        L_0x00c6:
            r11 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        L_0x00c8:
            if (r4 == 0) goto L_0x00ce;
        L_0x00ca:
            r15 = r4.h;
            r15 = (float) r15;
            goto L_0x00d0;
        L_0x00ce:
            r15 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        L_0x00d0:
            if (r1 != 0) goto L_0x00d7;
        L_0x00d2:
            r1 = (float) r2;
            r1 = r1 / r11;
            r1 = r1 * r15;
            r5 = (int) r1;
        L_0x00d7:
            r1 = r9.parentBlock;
            r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCover;
            if (r1 == 0) goto L_0x00e2;
        L_0x00dd:
            r5 = java.lang.Math.min(r5, r2);
            goto L_0x0107;
        L_0x00e2:
            r1 = org.telegram.messenger.AndroidUtilities.displaySize;
            r14 = r1.x;
            r1 = r1.y;
            r1 = java.lang.Math.max(r14, r1);
            r14 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
            r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r1 = r1 - r14;
            r1 = (float) r1;
            r14 = NUM; // 0x3var_ float:0.9 double:5.2552552E-315;
            r1 = r1 * r14;
            r1 = (int) r1;
            if (r5 <= r1) goto L_0x0107;
        L_0x00fc:
            r2 = (float) r1;
            r2 = r2 / r15;
            r2 = r2 * r11;
            r2 = (int) r2;
            r5 = r13 - r0;
            r5 = r5 - r2;
            r5 = r5 / r10;
            r0 = r0 + r5;
            r5 = r1;
        L_0x0107:
            if (r5 != 0) goto L_0x010e;
        L_0x0109:
            r1 = org.telegram.messenger.AndroidUtilities.dp(r7);
            goto L_0x0136;
        L_0x010e:
            if (r5 >= r3) goto L_0x0112;
        L_0x0110:
            r1 = r3;
            goto L_0x0136;
        L_0x0112:
            r1 = r5;
            goto L_0x0136;
        L_0x0114:
            if (r5 != r10) goto L_0x0136;
        L_0x0116:
            r5 = r9.groupPosition;
            r5 = r5.flags;
            r5 = r5 & r10;
            if (r5 != 0) goto L_0x0122;
        L_0x011d:
            r5 = org.telegram.messenger.AndroidUtilities.dp(r6);
            r2 = r2 - r5;
        L_0x0122:
            r5 = r9.groupPosition;
            r5 = r5.flags;
            r5 = r5 & 8;
            if (r5 != 0) goto L_0x0136;
        L_0x012a:
            r5 = org.telegram.messenger.AndroidUtilities.dp(r6);
            r5 = r1 - r5;
            r29 = r5;
            r5 = r1;
            r1 = r29;
            goto L_0x0137;
        L_0x0136:
            r5 = r1;
        L_0x0137:
            r7 = r9.imageView;
            r11 = r9.currentDocument;
            r7.setQualityThumbDocument(r11);
            r7 = r9.imageView;
            r11 = r9.isFirst;
            if (r11 != 0) goto L_0x0158;
        L_0x0144:
            r11 = r9.currentType;
            if (r11 == r12) goto L_0x0158;
        L_0x0148:
            if (r11 == r10) goto L_0x0158;
        L_0x014a:
            r11 = r9.currentBlock;
            r11 = r11.level;
            if (r11 <= 0) goto L_0x0151;
        L_0x0150:
            goto L_0x0158;
        L_0x0151:
            r11 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            r14 = org.telegram.messenger.AndroidUtilities.dp(r11);
            goto L_0x0159;
        L_0x0158:
            r14 = 0;
        L_0x0159:
            r7.setImageCoords(r0, r14, r2, r1);
            r0 = r9.isGif;
            r1 = 0;
            if (r0 == 0) goto L_0x01ef;
        L_0x0161:
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.currentAccount;
            r0 = org.telegram.messenger.DownloadController.getInstance(r0);
            r2 = 4;
            r7 = r9.currentDocument;
            r7 = r7.size;
            r0 = r0.canDownloadMedia(r2, r7);
            r9.autoDownload = r0;
            r0 = r9.currentDocument;
            r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r12);
            r2 = r9.autoDownload;
            if (r2 != 0) goto L_0x01bc;
        L_0x0180:
            r0 = r0.exists();
            if (r0 == 0) goto L_0x0187;
        L_0x0186:
            goto L_0x01bc;
        L_0x0187:
            r0 = r9.imageView;
            r1 = r9.currentDocument;
            r1 = org.telegram.messenger.ImageLocation.getForDocument(r1);
            r0.setStrippedLocation(r1);
            r0 = r9.imageView;
            r18 = 0;
            r19 = 0;
            r20 = 0;
            r21 = 0;
            r1 = r9.currentDocument;
            r22 = org.telegram.messenger.ImageLocation.getForDocument(r4, r1);
            r24 = 0;
            r1 = r9.currentDocument;
            r1 = r1.size;
            r26 = 0;
            r2 = org.telegram.ui.ArticleViewer.this;
            r27 = r2.currentPage;
            r28 = 1;
            r23 = "80_80_b";
            r17 = r0;
            r25 = r1;
            r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28);
            goto L_0x0213;
        L_0x01bc:
            r0 = r9.imageView;
            r0.setStrippedLocation(r1);
            r0 = r9.imageView;
            r1 = r9.currentDocument;
            r18 = org.telegram.messenger.ImageLocation.getForDocument(r1);
            r19 = 0;
            r20 = 0;
            r21 = 0;
            r1 = r9.currentDocument;
            r22 = org.telegram.messenger.ImageLocation.getForDocument(r4, r1);
            r24 = 0;
            r1 = r9.currentDocument;
            r1 = r1.size;
            r26 = 0;
            r2 = org.telegram.ui.ArticleViewer.this;
            r27 = r2.currentPage;
            r28 = 1;
            r23 = "80_80_b";
            r17 = r0;
            r25 = r1;
            r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28);
            goto L_0x0213;
        L_0x01ef:
            r0 = r9.imageView;
            r0.setStrippedLocation(r1);
            r0 = r9.imageView;
            r18 = 0;
            r19 = 0;
            r1 = r9.currentDocument;
            r20 = org.telegram.messenger.ImageLocation.getForDocument(r4, r1);
            r22 = 0;
            r23 = 0;
            r1 = org.telegram.ui.ArticleViewer.this;
            r24 = r1.currentPage;
            r25 = 1;
            r21 = "80_80_b";
            r17 = r0;
            r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25);
        L_0x0213:
            r0 = r9.imageView;
            r0.setAspectFit(r12);
            r0 = r9.imageView;
            r0 = r0.getImageX();
            r0 = (float) r0;
            r1 = r9.imageView;
            r1 = r1.getImageWidth();
            r1 = r1 - r3;
            r1 = (float) r1;
            r1 = r1 / r6;
            r0 = r0 + r1;
            r0 = (int) r0;
            r9.buttonX = r0;
            r0 = r9.imageView;
            r0 = r0.getImageY();
            r0 = (float) r0;
            r1 = r9.imageView;
            r1 = r1.getImageHeight();
            r1 = r1 - r3;
            r1 = (float) r1;
            r1 = r1 / r6;
            r0 = r0 + r1;
            r0 = (int) r0;
            r9.buttonY = r0;
            r0 = r9.radialProgress;
            r1 = r9.buttonX;
            r2 = r9.buttonY;
            r4 = r1 + r3;
            r3 = r3 + r2;
            r0.setProgressRect(r1, r2, r4, r3);
            r11 = r5;
            goto L_0x024f;
        L_0x024e:
            r11 = r1;
        L_0x024f:
            r0 = r9.imageView;
            r0 = r0.getImageY();
            r1 = r9.imageView;
            r1 = r1.getImageHeight();
            r0 = r0 + r1;
            r1 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r0 = r0 + r2;
            r9.textY = r0;
            r0 = r9.currentType;
            if (r0 != 0) goto L_0x02ec;
        L_0x0269:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r6 = r9.currentBlock;
            r1 = r6.caption;
            r3 = r1.text;
            r5 = r9.textY;
            r7 = r9.parentAdapter;
            r1 = r30;
            r4 = r8;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7);
            r9.captionLayout = r0;
            r0 = r9.captionLayout;
            r14 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
            if (r0 == 0) goto L_0x02a4;
        L_0x0285:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r1 = r9.captionLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r9.creditOffset = r0;
            r0 = r9.creditOffset;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r0 = r0 + r1;
            r11 = r11 + r0;
            r0 = r9.captionLayout;
            r1 = r9.textX;
            r0.x = r1;
            r1 = r9.textY;
            r0.y = r1;
        L_0x02a4:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r6 = r9.currentBlock;
            r1 = r6.caption;
            r3 = r1.credit;
            r1 = r9.textY;
            r4 = r9.creditOffset;
            r5 = r1 + r4;
            r1 = r9.parentAdapter;
            r1 = r1.isRtl;
            if (r1 == 0) goto L_0x02c0;
        L_0x02bb:
            r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT();
            goto L_0x02c2;
        L_0x02c0:
            r1 = android.text.Layout.Alignment.ALIGN_NORMAL;
        L_0x02c2:
            r7 = r1;
            r15 = r9.parentAdapter;
            r1 = r30;
            r4 = r8;
            r8 = r15;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8);
            r9.creditLayout = r0;
            r0 = r9.creditLayout;
            if (r0 == 0) goto L_0x02ec;
        L_0x02d3:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r1 = r9.creditLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r11 = r11 + r0;
            r0 = r9.creditLayout;
            r1 = r9.textX;
            r0.x = r1;
            r1 = r9.textY;
            r2 = r9.creditOffset;
            r1 = r1 + r2;
            r0.y = r1;
        L_0x02ec:
            r0 = r9.isFirst;
            if (r0 != 0) goto L_0x0301;
        L_0x02f0:
            r0 = r9.currentType;
            if (r0 != 0) goto L_0x0301;
        L_0x02f4:
            r0 = r9.currentBlock;
            r0 = r0.level;
            if (r0 > 0) goto L_0x0301;
        L_0x02fa:
            r0 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r11 = r11 + r1;
        L_0x0301:
            r0 = r9.parentBlock;
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCover;
            if (r0 == 0) goto L_0x0324;
        L_0x0307:
            r0 = r9.parentAdapter;
            r0 = r0.blocks;
            r0 = r0.size();
            if (r0 <= r12) goto L_0x0324;
        L_0x0313:
            r0 = r9.parentAdapter;
            r0 = r0.blocks;
            r0 = r0.get(r12);
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockChannel;
            if (r0 == 0) goto L_0x0324;
        L_0x0321:
            r16 = 1;
            goto L_0x0326;
        L_0x0324:
            r16 = 0;
        L_0x0326:
            r0 = r9.currentType;
            if (r0 == r10) goto L_0x0333;
        L_0x032a:
            if (r16 != 0) goto L_0x0333;
        L_0x032c:
            r0 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r11 = r11 + r0;
        L_0x0333:
            r12 = r11;
        L_0x0334:
            r0 = r9.channelCell;
            r1 = r31;
            r2 = r32;
            r0.measure(r1, r2);
            r0 = r9.channelCell;
            r1 = r9.imageView;
            r1 = r1.getImageHeight();
            r2 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r1 = r1 - r2;
            r1 = (float) r1;
            r0.setTranslationY(r1);
            r9.setMeasuredDimension(r13, r12);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$BlockVideoCell.onMeasure(int, int):void");
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int i;
                if (!(this.imageView.hasBitmapImage() && this.imageView.getCurrentAlpha() == 1.0f)) {
                    canvas.drawRect(this.imageView.getDrawRegion(), ArticleViewer.photoBackgroundPaint);
                }
                this.imageView.draw(canvas);
                if (this.imageView.getVisible()) {
                    this.radialProgress.draw(canvas);
                }
                int i2 = 0;
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    i = 1;
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                } else {
                    i = 0;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, i);
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    float dp = (float) AndroidUtilities.dp(18.0f);
                    float dp2 = (float) AndroidUtilities.dp(20.0f);
                    int measuredHeight = getMeasuredHeight();
                    if (this.currentBlock.bottom) {
                        i2 = AndroidUtilities.dp(6.0f);
                    }
                    canvas.drawRect(dp, 0.0f, dp2, (float) (measuredHeight - i2), ArticleViewer.quoteLinePaint);
                }
            }
        }

        private int getIconForCurrentState() {
            int i = this.buttonState;
            if (i == 0) {
                return 2;
            }
            if (i == 1) {
                return 3;
            }
            if (i == 2) {
                return 8;
            }
            return i == 3 ? 0 : 4;
        }

        public void updateButtonState(boolean z) {
            String attachFileName = FileLoader.getAttachFileName(this.currentDocument);
            boolean z2 = true;
            boolean exists = FileLoader.getPathToAttach(this.currentDocument, true).exists();
            if (TextUtils.isEmpty(attachFileName)) {
                this.radialProgress.setIcon(4, false, false);
                return;
            }
            if (exists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                if (this.isGif) {
                    this.buttonState = -1;
                } else {
                    this.buttonState = 3;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                invalidate();
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(attachFileName, null, this);
                float f = 0.0f;
                if (FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(attachFileName)) {
                    this.buttonState = 1;
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                    if (fileProgress != null) {
                        f = fileProgress.floatValue();
                    }
                } else if (!this.cancelLoading && this.autoDownload && this.isGif) {
                    this.buttonState = 1;
                } else {
                    this.buttonState = 0;
                    z2 = false;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), z2, z);
                this.radialProgress.setProgress(f, false);
                invalidate();
            }
        }

        private void didPressedButton(boolean z) {
            int i = this.buttonState;
            if (i == 0) {
                this.cancelLoading = false;
                this.radialProgress.setProgress(0.0f, false);
                if (this.isGif) {
                    String str = "80_80_b";
                    this.imageView.setImage(ImageLocation.getForDocument(this.currentDocument), null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(this.currentDocument.thumbs, 40), this.currentDocument), str, this.currentDocument.size, null, ArticleViewer.this.currentPage, 1);
                } else {
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).loadFile(this.currentDocument, ArticleViewer.this.currentPage, 1, 1);
                }
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), true, z);
                invalidate();
            } else if (i == 1) {
                this.cancelLoading = true;
                if (this.isGif) {
                    this.imageView.cancelLoadImage();
                } else {
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).cancelLoadFile(this.currentDocument);
                }
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                invalidate();
            } else if (i == 2) {
                this.imageView.setAllowStartAnimation(true);
                this.imageView.startAnimation();
                this.buttonState = -1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, z);
            } else if (i == 3) {
                ArticleViewer.this.openPhoto(this.currentBlock);
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageView.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        /* Access modifiers changed, original: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.imageView.onAttachedToWindow();
            updateButtonState(false);
        }

        public void onFailedDownload(String str, boolean z) {
            updateButtonState(false);
        }

        public void onSuccessDownload(String str) {
            this.radialProgress.setProgress(1.0f, true);
            if (this.isGif) {
                this.buttonState = 2;
                didPressedButton(true);
                return;
            }
            updateButtonState(true);
        }

        public void onProgressDownload(String str, long j, long j2) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
            if (this.buttonState != 1) {
                updateButtonState(true);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            StringBuilder stringBuilder = new StringBuilder(LocaleController.getString("AttachVideo", NUM));
            if (this.captionLayout != null) {
                stringBuilder.append(", ");
                stringBuilder.append(this.captionLayout.getText());
            }
            accessibilityNodeInfo.setText(stringBuilder.toString());
        }

        public void fillTextLayoutBlocks(ArrayList<TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            drawingText = this.creditLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
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

        /* synthetic */ TL_pageBlockDetailsChild(ArticleViewer articleViewer, AnonymousClass1 anonymousClass1) {
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

        /* synthetic */ TL_pageBlockListItem(ArticleViewer articleViewer, AnonymousClass1 anonymousClass1) {
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

        /* synthetic */ TL_pageBlockListParent(ArticleViewer articleViewer, AnonymousClass1 anonymousClass1) {
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

        /* synthetic */ TL_pageBlockOrderedListItem(ArticleViewer articleViewer, AnonymousClass1 anonymousClass1) {
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

        /* synthetic */ TL_pageBlockOrderedListParent(ArticleViewer articleViewer, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private class TL_pageBlockRelatedArticlesChild extends PageBlock {
        private int num;
        private TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesChild() {
        }

        /* synthetic */ TL_pageBlockRelatedArticlesChild(ArticleViewer articleViewer, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private class TL_pageBlockRelatedArticlesShadow extends PageBlock {
        private TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesShadow() {
        }

        /* synthetic */ TL_pageBlockRelatedArticlesShadow(ArticleViewer articleViewer, AnonymousClass1 anonymousClass1) {
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
        private TL_pageBlockChannel channelBlock;
        private Context context;
        private boolean isRtl;
        private ArrayList<PageBlock> localBlocks = new ArrayList();
        private ArrayList<PageBlock> photoBlocks = new ArrayList();
        private HashMap<String, Integer> searchTextOffset = new HashMap();
        private ArrayList<Object> textBlocks = new ArrayList();
        private HashMap<Object, PageBlock> textToBlocks = new HashMap();

        public WebpageAdapter(Context context) {
            this.context = context;
        }

        private void setRichTextParents(RichText richText, RichText richText2) {
            if (richText2 != null) {
                richText2.parentRichText = richText;
                if (richText2 instanceof TL_textFixed) {
                    setRichTextParents(richText2, ((TL_textFixed) richText2).text);
                } else if (richText2 instanceof TL_textItalic) {
                    setRichTextParents(richText2, ((TL_textItalic) richText2).text);
                } else if (richText2 instanceof TL_textBold) {
                    setRichTextParents(richText2, ((TL_textBold) richText2).text);
                } else if (richText2 instanceof TL_textUnderline) {
                    setRichTextParents(richText2, ((TL_textUnderline) richText2).text);
                } else if (richText2 instanceof TL_textStrike) {
                    setRichTextParents(richText2, ((TL_textStrike) richText2).text);
                } else if (richText2 instanceof TL_textEmail) {
                    setRichTextParents(richText2, ((TL_textEmail) richText2).text);
                } else if (richText2 instanceof TL_textPhone) {
                    setRichTextParents(richText2, ((TL_textPhone) richText2).text);
                } else if (richText2 instanceof TL_textUrl) {
                    setRichTextParents(richText2, ((TL_textUrl) richText2).text);
                } else if (richText2 instanceof TL_textConcat) {
                    int size = richText2.texts.size();
                    for (int i = 0; i < size; i++) {
                        setRichTextParents(richText2, (RichText) richText2.texts.get(i));
                    }
                } else if (richText2 instanceof TL_textSubscript) {
                    setRichTextParents(richText2, ((TL_textSubscript) richText2).text);
                } else if (richText2 instanceof TL_textSuperscript) {
                    setRichTextParents(richText2, ((TL_textSuperscript) richText2).text);
                } else if (richText2 instanceof TL_textMarked) {
                    setRichTextParents(richText2, ((TL_textMarked) richText2).text);
                } else if (richText2 instanceof TL_textAnchor) {
                    TL_textAnchor tL_textAnchor = (TL_textAnchor) richText2;
                    setRichTextParents(richText2, tL_textAnchor.text);
                    String toLowerCase = tL_textAnchor.name.toLowerCase();
                    this.anchors.put(toLowerCase, Integer.valueOf(this.blocks.size()));
                    RichText richText3 = tL_textAnchor.text;
                    if (richText3 instanceof TL_textPlain) {
                        if (!TextUtils.isEmpty(((TL_textPlain) richText3).text)) {
                            this.anchorsParent.put(toLowerCase, tL_textAnchor);
                        }
                    } else if (!(richText3 instanceof TL_textEmpty)) {
                        this.anchorsParent.put(toLowerCase, tL_textAnchor);
                    }
                    this.anchorsOffset.put(toLowerCase, Integer.valueOf(-1));
                }
            }
        }

        private void addTextBlock(Object obj, PageBlock pageBlock) {
            if (!(obj instanceof TL_textEmpty) && !this.textToBlocks.containsKey(obj)) {
                this.textToBlocks.put(obj, pageBlock);
                this.textBlocks.add(obj);
            }
        }

        private void setRichTextParents(PageBlock pageBlock) {
            if (pageBlock instanceof TL_pageBlockEmbedPost) {
                TL_pageBlockEmbedPost tL_pageBlockEmbedPost = (TL_pageBlockEmbedPost) pageBlock;
                setRichTextParents(null, tL_pageBlockEmbedPost.caption.text);
                setRichTextParents(null, tL_pageBlockEmbedPost.caption.credit);
                addTextBlock(tL_pageBlockEmbedPost.caption.text, tL_pageBlockEmbedPost);
                addTextBlock(tL_pageBlockEmbedPost.caption.credit, tL_pageBlockEmbedPost);
            } else if (pageBlock instanceof TL_pageBlockParagraph) {
                TL_pageBlockParagraph tL_pageBlockParagraph = (TL_pageBlockParagraph) pageBlock;
                setRichTextParents(null, tL_pageBlockParagraph.text);
                addTextBlock(tL_pageBlockParagraph.text, tL_pageBlockParagraph);
            } else if (pageBlock instanceof TL_pageBlockKicker) {
                TL_pageBlockKicker tL_pageBlockKicker = (TL_pageBlockKicker) pageBlock;
                setRichTextParents(null, tL_pageBlockKicker.text);
                addTextBlock(tL_pageBlockKicker.text, tL_pageBlockKicker);
            } else if (pageBlock instanceof TL_pageBlockFooter) {
                TL_pageBlockFooter tL_pageBlockFooter = (TL_pageBlockFooter) pageBlock;
                setRichTextParents(null, tL_pageBlockFooter.text);
                addTextBlock(tL_pageBlockFooter.text, tL_pageBlockFooter);
            } else if (pageBlock instanceof TL_pageBlockHeader) {
                TL_pageBlockHeader tL_pageBlockHeader = (TL_pageBlockHeader) pageBlock;
                setRichTextParents(null, tL_pageBlockHeader.text);
                addTextBlock(tL_pageBlockHeader.text, tL_pageBlockHeader);
            } else if (pageBlock instanceof TL_pageBlockPreformatted) {
                TL_pageBlockPreformatted tL_pageBlockPreformatted = (TL_pageBlockPreformatted) pageBlock;
                setRichTextParents(null, tL_pageBlockPreformatted.text);
                addTextBlock(tL_pageBlockPreformatted.text, tL_pageBlockPreformatted);
            } else if (pageBlock instanceof TL_pageBlockSubheader) {
                TL_pageBlockSubheader tL_pageBlockSubheader = (TL_pageBlockSubheader) pageBlock;
                setRichTextParents(null, tL_pageBlockSubheader.text);
                addTextBlock(tL_pageBlockSubheader.text, tL_pageBlockSubheader);
            } else {
                int i = 0;
                int size;
                if (pageBlock instanceof TL_pageBlockSlideshow) {
                    TL_pageBlockSlideshow tL_pageBlockSlideshow = (TL_pageBlockSlideshow) pageBlock;
                    setRichTextParents(null, tL_pageBlockSlideshow.caption.text);
                    setRichTextParents(null, tL_pageBlockSlideshow.caption.credit);
                    addTextBlock(tL_pageBlockSlideshow.caption.text, tL_pageBlockSlideshow);
                    addTextBlock(tL_pageBlockSlideshow.caption.credit, tL_pageBlockSlideshow);
                    size = tL_pageBlockSlideshow.items.size();
                    while (i < size) {
                        setRichTextParents((PageBlock) tL_pageBlockSlideshow.items.get(i));
                        i++;
                    }
                } else if (pageBlock instanceof TL_pageBlockPhoto) {
                    TL_pageBlockPhoto tL_pageBlockPhoto = (TL_pageBlockPhoto) pageBlock;
                    setRichTextParents(null, tL_pageBlockPhoto.caption.text);
                    setRichTextParents(null, tL_pageBlockPhoto.caption.credit);
                    addTextBlock(tL_pageBlockPhoto.caption.text, tL_pageBlockPhoto);
                    addTextBlock(tL_pageBlockPhoto.caption.credit, tL_pageBlockPhoto);
                } else if (pageBlock instanceof TL_pageBlockListItem) {
                    TL_pageBlockListItem tL_pageBlockListItem = (TL_pageBlockListItem) pageBlock;
                    if (tL_pageBlockListItem.textItem != null) {
                        setRichTextParents(null, tL_pageBlockListItem.textItem);
                        addTextBlock(tL_pageBlockListItem.textItem, tL_pageBlockListItem);
                    } else if (tL_pageBlockListItem.blockItem != null) {
                        setRichTextParents(tL_pageBlockListItem.blockItem);
                    }
                } else if (pageBlock instanceof TL_pageBlockOrderedListItem) {
                    TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem = (TL_pageBlockOrderedListItem) pageBlock;
                    if (tL_pageBlockOrderedListItem.textItem != null) {
                        setRichTextParents(null, tL_pageBlockOrderedListItem.textItem);
                        addTextBlock(tL_pageBlockOrderedListItem.textItem, tL_pageBlockOrderedListItem);
                    } else if (tL_pageBlockOrderedListItem.blockItem != null) {
                        setRichTextParents(tL_pageBlockOrderedListItem.blockItem);
                    }
                } else if (pageBlock instanceof TL_pageBlockCollage) {
                    TL_pageBlockCollage tL_pageBlockCollage = (TL_pageBlockCollage) pageBlock;
                    setRichTextParents(null, tL_pageBlockCollage.caption.text);
                    setRichTextParents(null, tL_pageBlockCollage.caption.credit);
                    addTextBlock(tL_pageBlockCollage.caption.text, tL_pageBlockCollage);
                    addTextBlock(tL_pageBlockCollage.caption.credit, tL_pageBlockCollage);
                    size = tL_pageBlockCollage.items.size();
                    while (i < size) {
                        setRichTextParents((PageBlock) tL_pageBlockCollage.items.get(i));
                        i++;
                    }
                } else if (pageBlock instanceof TL_pageBlockEmbed) {
                    TL_pageBlockEmbed tL_pageBlockEmbed = (TL_pageBlockEmbed) pageBlock;
                    setRichTextParents(null, tL_pageBlockEmbed.caption.text);
                    setRichTextParents(null, tL_pageBlockEmbed.caption.credit);
                    addTextBlock(tL_pageBlockEmbed.caption.text, tL_pageBlockEmbed);
                    addTextBlock(tL_pageBlockEmbed.caption.credit, tL_pageBlockEmbed);
                } else if (pageBlock instanceof TL_pageBlockSubtitle) {
                    TL_pageBlockSubtitle tL_pageBlockSubtitle = (TL_pageBlockSubtitle) pageBlock;
                    setRichTextParents(null, tL_pageBlockSubtitle.text);
                    addTextBlock(tL_pageBlockSubtitle.text, tL_pageBlockSubtitle);
                } else if (pageBlock instanceof TL_pageBlockBlockquote) {
                    TL_pageBlockBlockquote tL_pageBlockBlockquote = (TL_pageBlockBlockquote) pageBlock;
                    setRichTextParents(null, tL_pageBlockBlockquote.text);
                    setRichTextParents(null, tL_pageBlockBlockquote.caption);
                    addTextBlock(tL_pageBlockBlockquote.text, tL_pageBlockBlockquote);
                    addTextBlock(tL_pageBlockBlockquote.caption, tL_pageBlockBlockquote);
                } else if (pageBlock instanceof TL_pageBlockDetails) {
                    TL_pageBlockDetails tL_pageBlockDetails = (TL_pageBlockDetails) pageBlock;
                    setRichTextParents(null, tL_pageBlockDetails.title);
                    addTextBlock(tL_pageBlockDetails.title, tL_pageBlockDetails);
                    size = tL_pageBlockDetails.blocks.size();
                    while (i < size) {
                        setRichTextParents((PageBlock) tL_pageBlockDetails.blocks.get(i));
                        i++;
                    }
                } else if (pageBlock instanceof TL_pageBlockVideo) {
                    TL_pageBlockVideo tL_pageBlockVideo = (TL_pageBlockVideo) pageBlock;
                    setRichTextParents(null, tL_pageBlockVideo.caption.text);
                    setRichTextParents(null, tL_pageBlockVideo.caption.credit);
                    addTextBlock(tL_pageBlockVideo.caption.text, tL_pageBlockVideo);
                    addTextBlock(tL_pageBlockVideo.caption.credit, tL_pageBlockVideo);
                } else if (pageBlock instanceof TL_pageBlockPullquote) {
                    TL_pageBlockPullquote tL_pageBlockPullquote = (TL_pageBlockPullquote) pageBlock;
                    setRichTextParents(null, tL_pageBlockPullquote.text);
                    setRichTextParents(null, tL_pageBlockPullquote.caption);
                    addTextBlock(tL_pageBlockPullquote.text, tL_pageBlockPullquote);
                    addTextBlock(tL_pageBlockPullquote.caption, tL_pageBlockPullquote);
                } else if (pageBlock instanceof TL_pageBlockAudio) {
                    TL_pageBlockAudio tL_pageBlockAudio = (TL_pageBlockAudio) pageBlock;
                    setRichTextParents(null, tL_pageBlockAudio.caption.text);
                    setRichTextParents(null, tL_pageBlockAudio.caption.credit);
                    addTextBlock(tL_pageBlockAudio.caption.text, tL_pageBlockAudio);
                    addTextBlock(tL_pageBlockAudio.caption.credit, tL_pageBlockAudio);
                } else if (pageBlock instanceof TL_pageBlockTable) {
                    TL_pageBlockTable tL_pageBlockTable = (TL_pageBlockTable) pageBlock;
                    setRichTextParents(null, tL_pageBlockTable.title);
                    addTextBlock(tL_pageBlockTable.title, tL_pageBlockTable);
                    size = tL_pageBlockTable.rows.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        TL_pageTableRow tL_pageTableRow = (TL_pageTableRow) tL_pageBlockTable.rows.get(i2);
                        int size2 = tL_pageTableRow.cells.size();
                        for (int i3 = 0; i3 < size2; i3++) {
                            TL_pageTableCell tL_pageTableCell = (TL_pageTableCell) tL_pageTableRow.cells.get(i3);
                            setRichTextParents(null, tL_pageTableCell.text);
                            addTextBlock(tL_pageTableCell.text, tL_pageBlockTable);
                        }
                    }
                } else if (pageBlock instanceof TL_pageBlockTitle) {
                    TL_pageBlockTitle tL_pageBlockTitle = (TL_pageBlockTitle) pageBlock;
                    setRichTextParents(null, tL_pageBlockTitle.text);
                    addTextBlock(tL_pageBlockTitle.text, tL_pageBlockTitle);
                } else if (pageBlock instanceof TL_pageBlockCover) {
                    setRichTextParents(((TL_pageBlockCover) pageBlock).cover);
                } else if (pageBlock instanceof TL_pageBlockAuthorDate) {
                    TL_pageBlockAuthorDate tL_pageBlockAuthorDate = (TL_pageBlockAuthorDate) pageBlock;
                    setRichTextParents(null, tL_pageBlockAuthorDate.author);
                    addTextBlock(tL_pageBlockAuthorDate.author, tL_pageBlockAuthorDate);
                } else if (pageBlock instanceof TL_pageBlockMap) {
                    TL_pageBlockMap tL_pageBlockMap = (TL_pageBlockMap) pageBlock;
                    setRichTextParents(null, tL_pageBlockMap.caption.text);
                    setRichTextParents(null, tL_pageBlockMap.caption.credit);
                    addTextBlock(tL_pageBlockMap.caption.text, tL_pageBlockMap);
                    addTextBlock(tL_pageBlockMap.caption.credit, tL_pageBlockMap);
                } else if (pageBlock instanceof TL_pageBlockRelatedArticles) {
                    TL_pageBlockRelatedArticles tL_pageBlockRelatedArticles = (TL_pageBlockRelatedArticles) pageBlock;
                    setRichTextParents(null, tL_pageBlockRelatedArticles.title);
                    addTextBlock(tL_pageBlockRelatedArticles.title, tL_pageBlockRelatedArticles);
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:141:0x04e0  */
        /* JADX WARNING: Removed duplicated region for block: B:140:0x04c3  */
        /* JADX WARNING: Removed duplicated region for block: B:173:0x0547 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:147:0x04f3  */
        private void addBlock(org.telegram.tgnet.TLRPC.PageBlock r25, int r26, int r27, int r28) {
            /*
            r24 = this;
            r1 = r24;
            r0 = r25;
            r2 = r26;
            r3 = r27;
            r4 = r28;
            r5 = r0 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild;
            if (r5 == 0) goto L_0x0016;
        L_0x000e:
            r6 = r0;
            r6 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r6;
            r6 = r6.block;
            goto L_0x0017;
        L_0x0016:
            r6 = r0;
        L_0x0017:
            r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockList;
            if (r7 != 0) goto L_0x0025;
        L_0x001b:
            r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockOrderedList;
            if (r7 != 0) goto L_0x0025;
        L_0x001f:
            r1.setRichTextParents(r6);
            r1.addAllMediaFromBlock(r6);
        L_0x0025:
            r7 = org.telegram.ui.ArticleViewer.this;
            r6 = r7.getLastNonListPageBlock(r6);
            r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockUnsupported;
            if (r7 == 0) goto L_0x0030;
        L_0x002f:
            return;
        L_0x0030:
            r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockAnchor;
            if (r7 == 0) goto L_0x004c;
        L_0x0034:
            r0 = r1.anchors;
            r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockAnchor) r6;
            r2 = r6.name;
            r2 = r2.toLowerCase();
            r3 = r1.blocks;
            r3 = r3.size();
            r3 = java.lang.Integer.valueOf(r3);
            r0.put(r2, r3);
            return;
        L_0x004c:
            r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockList;
            if (r7 != 0) goto L_0x0059;
        L_0x0050:
            r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockOrderedList;
            if (r8 != 0) goto L_0x0059;
        L_0x0054:
            r8 = r1.blocks;
            r8.add(r0);
        L_0x0059:
            r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockAudio;
            r9 = 0;
            r10 = 1;
            if (r8 == 0) goto L_0x011d;
        L_0x005f:
            r0 = r6;
            r0 = (org.telegram.tgnet.TLRPC.TL_pageBlockAudio) r0;
            r2 = new org.telegram.tgnet.TLRPC$TL_message;
            r2.<init>();
            r2.out = r10;
            r3 = r0.audio_id;
            r3 = java.lang.Long.valueOf(r3);
            r3 = r3.hashCode();
            r3 = -r3;
            r6.mid = r3;
            r2.id = r3;
            r3 = new org.telegram.tgnet.TLRPC$TL_peerUser;
            r3.<init>();
            r2.to_id = r3;
            r3 = r2.to_id;
            r4 = org.telegram.ui.ArticleViewer.this;
            r4 = r4.currentAccount;
            r4 = org.telegram.messenger.UserConfig.getInstance(r4);
            r4 = r4.getClientUserId();
            r2.from_id = r4;
            r3.user_id = r4;
            r3 = java.lang.System.currentTimeMillis();
            r7 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            r3 = r3 / r7;
            r4 = (int) r3;
            r2.date = r4;
            r3 = "";
            r2.message = r3;
            r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
            r3.<init>();
            r2.media = r3;
            r3 = r2.media;
            r4 = org.telegram.ui.ArticleViewer.this;
            r4 = r4.currentPage;
            r3.webpage = r4;
            r3 = r2.media;
            r4 = r3.flags;
            r4 = r4 | 3;
            r3.flags = r4;
            r4 = org.telegram.ui.ArticleViewer.this;
            r7 = r0.audio_id;
            r4 = r4.getDocumentWithId(r7);
            r3.document = r4;
            r3 = r2.flags;
            r3 = r3 | 768;
            r2.flags = r3;
            r3 = new org.telegram.messenger.MessageObject;
            r4 = org.telegram.messenger.UserConfig.selectedAccount;
            r3.<init>(r4, r2, r9);
            r2 = r1.audioMessages;
            r2.add(r3);
            r2 = r1.audioBlocks;
            r2.put(r0, r3);
            r0 = r3.getMusicAuthor(r9);
            r2 = r3.getMusicTitle(r9);
            r3 = android.text.TextUtils.isEmpty(r2);
            if (r3 == 0) goto L_0x00ef;
        L_0x00e9:
            r3 = android.text.TextUtils.isEmpty(r0);
            if (r3 != 0) goto L_0x055a;
        L_0x00ef:
            r3 = android.text.TextUtils.isEmpty(r2);
            if (r3 != 0) goto L_0x010d;
        L_0x00f5:
            r3 = android.text.TextUtils.isEmpty(r0);
            if (r3 != 0) goto L_0x010d;
        L_0x00fb:
            r3 = 2;
            r3 = new java.lang.Object[r3];
            r3[r9] = r0;
            r3[r10] = r2;
            r0 = "%s - %s";
            r0 = java.lang.String.format(r0, r3);
            r1.addTextBlock(r0, r6);
            goto L_0x055a;
        L_0x010d:
            r3 = android.text.TextUtils.isEmpty(r2);
            if (r3 != 0) goto L_0x0118;
        L_0x0113:
            r1.addTextBlock(r2, r6);
            goto L_0x055a;
        L_0x0118:
            r1.addTextBlock(r0, r6);
            goto L_0x055a;
        L_0x011d:
            r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost;
            r11 = 0;
            if (r8 == 0) goto L_0x01aa;
        L_0x0122:
            r0 = r6;
            r0 = (org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost) r0;
            r2 = r0.blocks;
            r2 = r2.isEmpty();
            if (r2 != 0) goto L_0x055a;
        L_0x012d:
            r2 = -1;
            r6.level = r2;
        L_0x0130:
            r2 = r0.blocks;
            r2 = r2.size();
            if (r9 >= r2) goto L_0x0179;
        L_0x0138:
            r2 = r0.blocks;
            r2 = r2.get(r9);
            r2 = (org.telegram.tgnet.TLRPC.PageBlock) r2;
            r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockUnsupported;
            if (r3 == 0) goto L_0x0145;
        L_0x0144:
            goto L_0x0176;
        L_0x0145:
            r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockAnchor;
            if (r3 == 0) goto L_0x0161;
        L_0x0149:
            r2 = (org.telegram.tgnet.TLRPC.TL_pageBlockAnchor) r2;
            r3 = r1.anchors;
            r2 = r2.name;
            r2 = r2.toLowerCase();
            r4 = r1.blocks;
            r4 = r4.size();
            r4 = java.lang.Integer.valueOf(r4);
            r3.put(r2, r4);
            goto L_0x0176;
        L_0x0161:
            r2.level = r10;
            r3 = r0.blocks;
            r3 = r3.size();
            r3 = r3 - r10;
            if (r9 != r3) goto L_0x016e;
        L_0x016c:
            r2.bottom = r10;
        L_0x016e:
            r3 = r1.blocks;
            r3.add(r2);
            r1.addAllMediaFromBlock(r2);
        L_0x0176:
            r9 = r9 + 1;
            goto L_0x0130;
        L_0x0179:
            r2 = r0.caption;
            r2 = r2.text;
            r2 = org.telegram.ui.ArticleViewer.getPlainText(r2);
            r2 = android.text.TextUtils.isEmpty(r2);
            if (r2 == 0) goto L_0x0195;
        L_0x0187:
            r2 = r0.caption;
            r2 = r2.credit;
            r2 = org.telegram.ui.ArticleViewer.getPlainText(r2);
            r2 = android.text.TextUtils.isEmpty(r2);
            if (r2 != 0) goto L_0x055a;
        L_0x0195:
            r2 = new org.telegram.ui.ArticleViewer$TL_pageBlockEmbedPostCaption;
            r3 = org.telegram.ui.ArticleViewer.this;
            r2.<init>(r3, r11);
            r2.parent = r0;
            r0 = r0.caption;
            r2.caption = r0;
            r0 = r1.blocks;
            r0.add(r2);
            goto L_0x055a;
        L_0x01aa:
            r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockRelatedArticles;
            if (r8 == 0) goto L_0x01f4;
        L_0x01ae:
            r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockRelatedArticles) r6;
            r0 = new org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesShadow;
            r2 = org.telegram.ui.ArticleViewer.this;
            r0.<init>(r2, r11);
            r0.parent = r6;
            r2 = r1.blocks;
            r3 = r2.size();
            r3 = r3 - r10;
            r2.add(r3, r0);
            r0 = r6.articles;
            r0 = r0.size();
        L_0x01ca:
            if (r9 >= r0) goto L_0x01e1;
        L_0x01cc:
            r2 = new org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesChild;
            r3 = org.telegram.ui.ArticleViewer.this;
            r2.<init>(r3, r11);
            r2.parent = r6;
            r2.num = r9;
            r3 = r1.blocks;
            r3.add(r2);
            r9 = r9 + 1;
            goto L_0x01ca;
        L_0x01e1:
            if (r4 != 0) goto L_0x055a;
        L_0x01e3:
            r0 = new org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesShadow;
            r2 = org.telegram.ui.ArticleViewer.this;
            r0.<init>(r2, r11);
            r0.parent = r6;
            r2 = r1.blocks;
            r2.add(r0);
            goto L_0x055a;
        L_0x01f4:
            r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockDetails;
            if (r8 == 0) goto L_0x0225;
        L_0x01f8:
            r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockDetails) r6;
            r5 = r6.blocks;
            r5 = r5.size();
        L_0x0200:
            if (r9 >= r5) goto L_0x055a;
        L_0x0202:
            r7 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild;
            r8 = org.telegram.ui.ArticleViewer.this;
            r7.<init>(r8, r11);
            r7.parent = r0;
            r8 = r6.blocks;
            r8 = r8.get(r9);
            r8 = (org.telegram.tgnet.TLRPC.PageBlock) r8;
            r7.block = r8;
            r8 = org.telegram.ui.ArticleViewer.this;
            r7 = r8.wrapInTableBlock(r0, r7);
            r8 = r2 + 1;
            r1.addBlock(r7, r8, r3, r4);
            r9 = r9 + 1;
            goto L_0x0200;
        L_0x0225:
            r8 = " ";
            r12 = ".%d";
            r13 = "%d.";
            if (r7 == 0) goto L_0x036d;
        L_0x022d:
            r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockList) r6;
            r7 = new org.telegram.ui.ArticleViewer$TL_pageBlockListParent;
            r14 = org.telegram.ui.ArticleViewer.this;
            r7.<init>(r14, r11);
            r7.pageBlockList = r6;
            r7.level = r3;
            r14 = r6.items;
            r14 = r14.size();
            r15 = 0;
        L_0x0243:
            if (r15 >= r14) goto L_0x055a;
        L_0x0245:
            r9 = r6.items;
            r9 = r9.get(r15);
            r9 = (org.telegram.tgnet.TLRPC.PageListItem) r9;
            r10 = new org.telegram.ui.ArticleViewer$TL_pageBlockListItem;
            r18 = r14;
            r14 = org.telegram.ui.ArticleViewer.this;
            r10.<init>(r14, r11);
            r10.index = r15;
            r10.parent = r7;
            r14 = r6.ordered;
            if (r14 == 0) goto L_0x028e;
        L_0x0260:
            r14 = r1.isRtl;
            if (r14 == 0) goto L_0x0279;
        L_0x0264:
            r14 = 1;
            r11 = new java.lang.Object[r14];
            r17 = r15 + 1;
            r17 = java.lang.Integer.valueOf(r17);
            r16 = 0;
            r11[r16] = r17;
            r11 = java.lang.String.format(r12, r11);
            r10.num = r11;
            goto L_0x0294;
        L_0x0279:
            r14 = 1;
            r16 = 0;
            r11 = new java.lang.Object[r14];
            r14 = r15 + 1;
            r14 = java.lang.Integer.valueOf(r14);
            r11[r16] = r14;
            r11 = java.lang.String.format(r13, r11);
            r10.num = r11;
            goto L_0x0294;
        L_0x028e:
            r11 = "•";
            r10.num = r11;
        L_0x0294:
            r11 = r7.items;
            r11.add(r10);
            r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageListItemText;
            if (r11 == 0) goto L_0x02a8;
        L_0x029f:
            r11 = r9;
            r11 = (org.telegram.tgnet.TLRPC.TL_pageListItemText) r11;
            r11 = r11.text;
            r10.textItem = r11;
            goto L_0x02d2;
        L_0x02a8:
            r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageListItemBlocks;
            if (r11 == 0) goto L_0x02d2;
        L_0x02ac:
            r11 = r9;
            r11 = (org.telegram.tgnet.TLRPC.TL_pageListItemBlocks) r11;
            r14 = r11.blocks;
            r14 = r14.isEmpty();
            if (r14 != 0) goto L_0x02c4;
        L_0x02b7:
            r11 = r11.blocks;
            r14 = 0;
            r11 = r11.get(r14);
            r11 = (org.telegram.tgnet.TLRPC.PageBlock) r11;
            r10.blockItem = r11;
            goto L_0x02d2;
        L_0x02c4:
            r9 = new org.telegram.tgnet.TLRPC$TL_pageListItemText;
            r9.<init>();
            r11 = new org.telegram.tgnet.TLRPC$TL_textPlain;
            r11.<init>();
            r11.text = r8;
            r9.text = r11;
        L_0x02d2:
            if (r5 == 0) goto L_0x02f3;
        L_0x02d4:
            r11 = r0;
            r11 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r11;
            r14 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild;
            r20 = r6;
            r6 = org.telegram.ui.ArticleViewer.this;
            r21 = r8;
            r8 = 0;
            r14.<init>(r6, r8);
            r6 = r11.parent;
            r14.parent = r6;
            r14.block = r10;
            r6 = r3 + 1;
            r1.addBlock(r14, r2, r6, r4);
            goto L_0x0304;
        L_0x02f3:
            r20 = r6;
            r21 = r8;
            if (r15 != 0) goto L_0x02ff;
        L_0x02f9:
            r6 = org.telegram.ui.ArticleViewer.this;
            r10 = r6.fixListBlock(r0, r10);
        L_0x02ff:
            r6 = r3 + 1;
            r1.addBlock(r10, r2, r6, r4);
        L_0x0304:
            r6 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageListItemBlocks;
            if (r6 == 0) goto L_0x0360;
        L_0x0308:
            r9 = (org.telegram.tgnet.TLRPC.TL_pageListItemBlocks) r9;
            r6 = r9.blocks;
            r6 = r6.size();
            r8 = 1;
        L_0x0311:
            if (r8 >= r6) goto L_0x0360;
        L_0x0313:
            r10 = new org.telegram.ui.ArticleViewer$TL_pageBlockListItem;
            r11 = org.telegram.ui.ArticleViewer.this;
            r14 = 0;
            r10.<init>(r11, r14);
            r11 = r9.blocks;
            r11 = r11.get(r8);
            r11 = (org.telegram.tgnet.TLRPC.PageBlock) r11;
            r10.blockItem = r11;
            r10.parent = r7;
            if (r5 == 0) goto L_0x0349;
        L_0x032b:
            r11 = r0;
            r11 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r11;
            r22 = r6;
            r6 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild;
            r23 = r9;
            r9 = org.telegram.ui.ArticleViewer.this;
            r6.<init>(r9, r14);
            r9 = r11.parent;
            r6.parent = r9;
            r6.block = r10;
            r9 = r3 + 1;
            r1.addBlock(r6, r2, r9, r4);
            goto L_0x0352;
        L_0x0349:
            r22 = r6;
            r23 = r9;
            r6 = r3 + 1;
            r1.addBlock(r10, r2, r6, r4);
        L_0x0352:
            r6 = r7.items;
            r6.add(r10);
            r8 = r8 + 1;
            r6 = r22;
            r9 = r23;
            goto L_0x0311;
        L_0x0360:
            r15 = r15 + 1;
            r14 = r18;
            r6 = r20;
            r8 = r21;
            r9 = 0;
            r10 = 1;
            r11 = 0;
            goto L_0x0243;
        L_0x036d:
            r21 = r8;
            r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockOrderedList;
            if (r7 == 0) goto L_0x055a;
        L_0x0373:
            r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockOrderedList) r6;
            r7 = new org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent;
            r8 = org.telegram.ui.ArticleViewer.this;
            r9 = 0;
            r7.<init>(r8, r9);
            r7.pageBlockOrderedList = r6;
            r7.level = r3;
            r8 = r6.items;
            r8 = r8.size();
            r9 = 0;
        L_0x038a:
            if (r9 >= r8) goto L_0x055a;
        L_0x038c:
            r10 = r6.items;
            r10 = r10.get(r9);
            r10 = (org.telegram.tgnet.TLRPC.PageListOrderedItem) r10;
            r11 = new org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem;
            r14 = org.telegram.ui.ArticleViewer.this;
            r15 = 0;
            r11.<init>(r14, r15);
            r11.index = r9;
            r11.parent = r7;
            r14 = r7.items;
            r14.add(r11);
            r14 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageListOrderedItemText;
            r15 = ".";
            if (r14 == 0) goto L_0x0421;
        L_0x03af:
            r14 = r10;
            r14 = (org.telegram.tgnet.TLRPC.TL_pageListOrderedItemText) r14;
            r18 = r6;
            r6 = r14.text;
            r11.textItem = r6;
            r6 = r14.num;
            r6 = android.text.TextUtils.isEmpty(r6);
            if (r6 == 0) goto L_0x03f1;
        L_0x03c1:
            r6 = r1.isRtl;
            if (r6 == 0) goto L_0x03db;
        L_0x03c5:
            r6 = 1;
            r14 = new java.lang.Object[r6];
            r15 = r9 + 1;
            r15 = java.lang.Integer.valueOf(r15);
            r16 = 0;
            r14[r16] = r15;
            r14 = java.lang.String.format(r12, r14);
            r11.num = r14;
            goto L_0x04bb;
        L_0x03db:
            r6 = 1;
            r16 = 0;
            r14 = new java.lang.Object[r6];
            r6 = r9 + 1;
            r6 = java.lang.Integer.valueOf(r6);
            r14[r16] = r6;
            r6 = java.lang.String.format(r13, r14);
            r11.num = r6;
            goto L_0x04bb;
        L_0x03f1:
            r6 = r1.isRtl;
            if (r6 == 0) goto L_0x040b;
        L_0x03f5:
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r6.append(r15);
            r14 = r14.num;
            r6.append(r14);
            r6 = r6.toString();
            r11.num = r6;
            goto L_0x04bb;
        L_0x040b:
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r14 = r14.num;
            r6.append(r14);
            r6.append(r15);
            r6 = r6.toString();
            r11.num = r6;
            goto L_0x04bb;
        L_0x0421:
            r18 = r6;
            r6 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageListOrderedItemBlocks;
            if (r6 == 0) goto L_0x04bb;
        L_0x0427:
            r6 = r10;
            r6 = (org.telegram.tgnet.TLRPC.TL_pageListOrderedItemBlocks) r6;
            r14 = r6.blocks;
            r14 = r14.isEmpty();
            if (r14 != 0) goto L_0x0443;
        L_0x0432:
            r14 = r6.blocks;
            r20 = r8;
            r8 = 0;
            r14 = r14.get(r8);
            r14 = (org.telegram.tgnet.TLRPC.PageBlock) r14;
            r11.blockItem = r14;
            r14 = r21;
            goto L_0x0455;
        L_0x0443:
            r20 = r8;
            r10 = new org.telegram.tgnet.TLRPC$TL_pageListOrderedItemText;
            r10.<init>();
            r8 = new org.telegram.tgnet.TLRPC$TL_textPlain;
            r8.<init>();
            r14 = r21;
            r8.text = r14;
            r10.text = r8;
        L_0x0455:
            r8 = r6.num;
            r8 = android.text.TextUtils.isEmpty(r8);
            if (r8 == 0) goto L_0x048b;
        L_0x045d:
            r6 = r1.isRtl;
            if (r6 == 0) goto L_0x0476;
        L_0x0461:
            r8 = 1;
            r6 = new java.lang.Object[r8];
            r15 = r9 + 1;
            r15 = java.lang.Integer.valueOf(r15);
            r16 = 0;
            r6[r16] = r15;
            r6 = java.lang.String.format(r12, r6);
            r11.num = r6;
            goto L_0x04c1;
        L_0x0476:
            r8 = 1;
            r16 = 0;
            r6 = new java.lang.Object[r8];
            r15 = r9 + 1;
            r15 = java.lang.Integer.valueOf(r15);
            r6[r16] = r15;
            r6 = java.lang.String.format(r13, r6);
            r11.num = r6;
            goto L_0x04c1;
        L_0x048b:
            r16 = 0;
            r8 = r1.isRtl;
            if (r8 == 0) goto L_0x04a6;
        L_0x0491:
            r8 = new java.lang.StringBuilder;
            r8.<init>();
            r8.append(r15);
            r6 = r6.num;
            r8.append(r6);
            r6 = r8.toString();
            r11.num = r6;
            goto L_0x04c1;
        L_0x04a6:
            r8 = new java.lang.StringBuilder;
            r8.<init>();
            r6 = r6.num;
            r8.append(r6);
            r8.append(r15);
            r6 = r8.toString();
            r11.num = r6;
            goto L_0x04c1;
        L_0x04bb:
            r20 = r8;
            r14 = r21;
            r16 = 0;
        L_0x04c1:
            if (r5 == 0) goto L_0x04e0;
        L_0x04c3:
            r6 = r0;
            r6 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r6;
            r8 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild;
            r15 = org.telegram.ui.ArticleViewer.this;
            r21 = r12;
            r12 = 0;
            r8.<init>(r15, r12);
            r6 = r6.parent;
            r8.parent = r6;
            r8.block = r11;
            r6 = r3 + 1;
            r1.addBlock(r8, r2, r6, r4);
            goto L_0x04ef;
        L_0x04e0:
            r21 = r12;
            if (r9 != 0) goto L_0x04ea;
        L_0x04e4:
            r6 = org.telegram.ui.ArticleViewer.this;
            r11 = r6.fixListBlock(r0, r11);
        L_0x04ea:
            r6 = r3 + 1;
            r1.addBlock(r11, r2, r6, r4);
        L_0x04ef:
            r6 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageListOrderedItemBlocks;
            if (r6 == 0) goto L_0x0547;
        L_0x04f3:
            r10 = (org.telegram.tgnet.TLRPC.TL_pageListOrderedItemBlocks) r10;
            r6 = r10.blocks;
            r6 = r6.size();
            r8 = 1;
        L_0x04fc:
            if (r8 >= r6) goto L_0x0547;
        L_0x04fe:
            r11 = new org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem;
            r12 = org.telegram.ui.ArticleViewer.this;
            r15 = 0;
            r11.<init>(r12, r15);
            r12 = r10.blocks;
            r12 = r12.get(r8);
            r12 = (org.telegram.tgnet.TLRPC.PageBlock) r12;
            r11.blockItem = r12;
            r11.parent = r7;
            if (r5 == 0) goto L_0x0532;
        L_0x0516:
            r12 = r0;
            r12 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r12;
            r0 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild;
            r19 = r5;
            r5 = org.telegram.ui.ArticleViewer.this;
            r0.<init>(r5, r15);
            r5 = r12.parent;
            r0.parent = r5;
            r0.block = r11;
            r5 = r3 + 1;
            r1.addBlock(r0, r2, r5, r4);
            goto L_0x0539;
        L_0x0532:
            r19 = r5;
            r0 = r3 + 1;
            r1.addBlock(r11, r2, r0, r4);	 Catch:{ all -> 0x055b }
        L_0x0539:
            r0 = r7.items;
            r0.add(r11);
            r8 = r8 + 1;
            r0 = r25;
            r5 = r19;
            goto L_0x04fc;
        L_0x0547:
            r19 = r5;
            r15 = 0;
            r9 = r9 + 1;
            r0 = r25;
            r6 = r18;
            r5 = r19;
            r8 = r20;
            r12 = r21;
            r21 = r14;
            goto L_0x038a;
        L_0x055a:
            return;
        L_0x055b:
            r0 = move-exception;
            r2 = r0;
            goto L_0x055f;
        L_0x055e:
            throw r2;
        L_0x055f:
            goto L_0x055e;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$WebpageAdapter.addBlock(org.telegram.tgnet.TLRPC$PageBlock, int, int, int):void");
        }

        private void addAllMediaFromBlock(PageBlock pageBlock) {
            if (pageBlock instanceof TL_pageBlockPhoto) {
                TL_pageBlockPhoto tL_pageBlockPhoto = (TL_pageBlockPhoto) pageBlock;
                Photo access$15300 = ArticleViewer.this.getPhotoWithId(tL_pageBlockPhoto.photo_id);
                if (access$15300 != null) {
                    tL_pageBlockPhoto.thumb = FileLoader.getClosestPhotoSizeWithSize(access$15300.sizes, 56, true);
                    tL_pageBlockPhoto.thumbObject = access$15300;
                    this.photoBlocks.add(pageBlock);
                }
            } else if ((pageBlock instanceof TL_pageBlockVideo) && ArticleViewer.this.isVideoBlock(pageBlock)) {
                TL_pageBlockVideo tL_pageBlockVideo = (TL_pageBlockVideo) pageBlock;
                Document access$13300 = ArticleViewer.this.getDocumentWithId(tL_pageBlockVideo.video_id);
                if (access$13300 != null) {
                    tL_pageBlockVideo.thumb = FileLoader.getClosestPhotoSizeWithSize(access$13300.thumbs, 56, true);
                    tL_pageBlockVideo.thumbObject = access$13300;
                    this.photoBlocks.add(pageBlock);
                }
            } else {
                int i = 0;
                int size;
                PageBlock pageBlock2;
                if (pageBlock instanceof TL_pageBlockSlideshow) {
                    TL_pageBlockSlideshow tL_pageBlockSlideshow = (TL_pageBlockSlideshow) pageBlock;
                    size = tL_pageBlockSlideshow.items.size();
                    while (i < size) {
                        pageBlock2 = (PageBlock) tL_pageBlockSlideshow.items.get(i);
                        pageBlock2.groupId = ArticleViewer.this.lastBlockNum;
                        addAllMediaFromBlock(pageBlock2);
                        i++;
                    }
                    ArticleViewer.this.lastBlockNum = ArticleViewer.this.lastBlockNum + 1;
                } else if (pageBlock instanceof TL_pageBlockCollage) {
                    TL_pageBlockCollage tL_pageBlockCollage = (TL_pageBlockCollage) pageBlock;
                    size = tL_pageBlockCollage.items.size();
                    while (i < size) {
                        pageBlock2 = (PageBlock) tL_pageBlockCollage.items.get(i);
                        pageBlock2.groupId = ArticleViewer.this.lastBlockNum;
                        addAllMediaFromBlock(pageBlock2);
                        i++;
                    }
                    ArticleViewer.this.lastBlockNum = ArticleViewer.this.lastBlockNum + 1;
                } else if (pageBlock instanceof TL_pageBlockCover) {
                    addAllMediaFromBlock(((TL_pageBlockCover) pageBlock).cover);
                }
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View blockParagraphCell;
            View blockVideoCell;
            if (i != 90) {
                switch (i) {
                    case 0:
                        blockParagraphCell = new BlockParagraphCell(this.context, this);
                        break;
                    case 1:
                        blockParagraphCell = new BlockHeaderCell(this.context, this);
                        break;
                    case 2:
                        blockParagraphCell = new BlockDividerCell(this.context);
                        break;
                    case 3:
                        blockParagraphCell = new BlockEmbedCell(this.context, this);
                        break;
                    case 4:
                        blockParagraphCell = new BlockSubtitleCell(this.context, this);
                        break;
                    case 5:
                        blockVideoCell = new BlockVideoCell(this.context, this, 0);
                        break;
                    case 6:
                        blockParagraphCell = new BlockPullquoteCell(this.context, this);
                        break;
                    case 7:
                        blockParagraphCell = new BlockBlockquoteCell(this.context, this);
                        break;
                    case 8:
                        blockParagraphCell = new BlockSlideshowCell(this.context, this);
                        break;
                    case 9:
                        blockVideoCell = new BlockPhotoCell(this.context, this, 0);
                        break;
                    case 10:
                        blockParagraphCell = new BlockAuthorDateCell(this.context, this);
                        break;
                    case 11:
                        blockParagraphCell = new BlockTitleCell(this.context, this);
                        break;
                    case 12:
                        blockParagraphCell = new BlockListItemCell(this.context, this);
                        break;
                    case 13:
                        blockParagraphCell = new BlockFooterCell(this.context, this);
                        break;
                    case 14:
                        blockParagraphCell = new BlockPreformattedCell(this.context, this);
                        break;
                    case 15:
                        blockParagraphCell = new BlockSubheaderCell(this.context, this);
                        break;
                    case 16:
                        blockParagraphCell = new BlockEmbedPostCell(this.context, this);
                        break;
                    case 17:
                        blockParagraphCell = new BlockCollageCell(this.context, this);
                        break;
                    case 18:
                        blockVideoCell = new BlockChannelCell(this.context, this, 0);
                        break;
                    case 19:
                        blockParagraphCell = new BlockAudioCell(this.context, this);
                        break;
                    case 20:
                        blockParagraphCell = new BlockKickerCell(this.context, this);
                        break;
                    case 21:
                        blockParagraphCell = new BlockOrderedListItemCell(this.context, this);
                        break;
                    case 22:
                        blockVideoCell = new BlockMapCell(this.context, this, 0);
                        break;
                    case 23:
                        blockParagraphCell = new BlockRelatedArticlesCell(this.context, this);
                        break;
                    case 24:
                        blockParagraphCell = new BlockDetailsCell(this.context, this);
                        break;
                    case 25:
                        blockParagraphCell = new BlockTableCell(this.context, this);
                        break;
                    case 26:
                        blockParagraphCell = new BlockRelatedArticlesHeaderCell(this.context, this);
                        break;
                    case 27:
                        blockParagraphCell = new BlockDetailsBottomCell(this.context);
                        break;
                    case 28:
                        blockParagraphCell = new BlockRelatedArticlesShadowCell(this.context);
                        break;
                    default:
                        blockParagraphCell = new TextView(this.context);
                        blockParagraphCell.setBackgroundColor(-65536);
                        blockParagraphCell.setTextColor(-16777216);
                        blockParagraphCell.setTextSize(1, 20.0f);
                        break;
                }
            }
            blockVideoCell = new FrameLayout(this.context) {
                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
                }
            };
            blockVideoCell.setTag(Integer.valueOf(90));
            TextView textView = new TextView(this.context);
            blockVideoCell.addView(textView, LayoutHelper.createFrame(-1, 34.0f, 51, 0.0f, 10.0f, 0.0f, 0.0f));
            textView.setText(LocaleController.getString("PreviewFeedback", NUM));
            textView.setTextSize(1, 12.0f);
            textView.setGravity(17);
            blockParagraphCell = blockVideoCell;
            blockParagraphCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            blockParagraphCell.setFocusable(true);
            return new Holder(blockParagraphCell);
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 23 || itemViewType == 24;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (i < this.localBlocks.size()) {
                ViewHolder viewHolder2 = viewHolder;
                bindBlockToHolder(viewHolder.getItemViewType(), viewHolder2, (PageBlock) this.localBlocks.get(i), i, this.localBlocks.size());
            } else if (viewHolder.getItemViewType() == 90) {
                TextView textView = (TextView) ((ViewGroup) viewHolder.itemView).getChildAt(0);
                i = Theme.getColor("switchTrack");
                int red = Color.red(i);
                int green = Color.green(i);
                i = Color.blue(i);
                textView.setTextColor(ArticleViewer.this.getGrayTextColor());
                textView.setBackgroundColor(Color.argb(34, red, green, i));
            }
        }

        private void bindBlockToHolder(int i, ViewHolder viewHolder, PageBlock pageBlock, int i2, int i3) {
            Object obj;
            if (pageBlock instanceof TL_pageBlockCover) {
                obj = ((TL_pageBlockCover) pageBlock).cover;
            } else if (pageBlock instanceof TL_pageBlockDetailsChild) {
                obj = ((TL_pageBlockDetailsChild) pageBlock).block;
            } else {
                obj = pageBlock;
            }
            if (i != 100) {
                boolean z = false;
                boolean z2;
                switch (i) {
                    case 0:
                        ((BlockParagraphCell) viewHolder.itemView).setBlock((TL_pageBlockParagraph) obj);
                        return;
                    case 1:
                        ((BlockHeaderCell) viewHolder.itemView).setBlock((TL_pageBlockHeader) obj);
                        return;
                    case 2:
                        BlockDividerCell blockDividerCell = (BlockDividerCell) viewHolder.itemView;
                        return;
                    case 3:
                        ((BlockEmbedCell) viewHolder.itemView).setBlock((TL_pageBlockEmbed) obj);
                        return;
                    case 4:
                        ((BlockSubtitleCell) viewHolder.itemView).setBlock((TL_pageBlockSubtitle) obj);
                        return;
                    case 5:
                        BlockVideoCell blockVideoCell = (BlockVideoCell) viewHolder.itemView;
                        TL_pageBlockVideo tL_pageBlockVideo = (TL_pageBlockVideo) obj;
                        z2 = i2 == 0;
                        if (i2 == i3 - 1) {
                            z = true;
                        }
                        blockVideoCell.setBlock(tL_pageBlockVideo, z2, z);
                        blockVideoCell.setParentBlock(this.channelBlock, pageBlock);
                        return;
                    case 6:
                        ((BlockPullquoteCell) viewHolder.itemView).setBlock((TL_pageBlockPullquote) obj);
                        return;
                    case 7:
                        ((BlockBlockquoteCell) viewHolder.itemView).setBlock((TL_pageBlockBlockquote) obj);
                        return;
                    case 8:
                        ((BlockSlideshowCell) viewHolder.itemView).setBlock((TL_pageBlockSlideshow) obj);
                        return;
                    case 9:
                        BlockPhotoCell blockPhotoCell = (BlockPhotoCell) viewHolder.itemView;
                        TL_pageBlockPhoto tL_pageBlockPhoto = (TL_pageBlockPhoto) obj;
                        z2 = i2 == 0;
                        if (i2 == i3 - 1) {
                            z = true;
                        }
                        blockPhotoCell.setBlock(tL_pageBlockPhoto, z2, z);
                        blockPhotoCell.setParentBlock(pageBlock);
                        return;
                    case 10:
                        ((BlockAuthorDateCell) viewHolder.itemView).setBlock((TL_pageBlockAuthorDate) obj);
                        return;
                    case 11:
                        ((BlockTitleCell) viewHolder.itemView).setBlock((TL_pageBlockTitle) obj);
                        return;
                    case 12:
                        ((BlockListItemCell) viewHolder.itemView).setBlock((TL_pageBlockListItem) obj);
                        return;
                    case 13:
                        ((BlockFooterCell) viewHolder.itemView).setBlock((TL_pageBlockFooter) obj);
                        return;
                    case 14:
                        ((BlockPreformattedCell) viewHolder.itemView).setBlock((TL_pageBlockPreformatted) obj);
                        return;
                    case 15:
                        ((BlockSubheaderCell) viewHolder.itemView).setBlock((TL_pageBlockSubheader) obj);
                        return;
                    case 16:
                        ((BlockEmbedPostCell) viewHolder.itemView).setBlock((TL_pageBlockEmbedPost) obj);
                        return;
                    case 17:
                        ((BlockCollageCell) viewHolder.itemView).setBlock((TL_pageBlockCollage) obj);
                        return;
                    case 18:
                        ((BlockChannelCell) viewHolder.itemView).setBlock((TL_pageBlockChannel) obj);
                        return;
                    case 19:
                        BlockAudioCell blockAudioCell = (BlockAudioCell) viewHolder.itemView;
                        TL_pageBlockAudio tL_pageBlockAudio = (TL_pageBlockAudio) obj;
                        z2 = i2 == 0;
                        if (i2 == i3 - 1) {
                            z = true;
                        }
                        blockAudioCell.setBlock(tL_pageBlockAudio, z2, z);
                        return;
                    case 20:
                        ((BlockKickerCell) viewHolder.itemView).setBlock((TL_pageBlockKicker) obj);
                        return;
                    case 21:
                        ((BlockOrderedListItemCell) viewHolder.itemView).setBlock((TL_pageBlockOrderedListItem) obj);
                        return;
                    case 22:
                        BlockMapCell blockMapCell = (BlockMapCell) viewHolder.itemView;
                        TL_pageBlockMap tL_pageBlockMap = (TL_pageBlockMap) obj;
                        z2 = i2 == 0;
                        if (i2 == i3 - 1) {
                            z = true;
                        }
                        blockMapCell.setBlock(tL_pageBlockMap, z2, z);
                        return;
                    case 23:
                        ((BlockRelatedArticlesCell) viewHolder.itemView).setBlock((TL_pageBlockRelatedArticlesChild) obj);
                        return;
                    case 24:
                        ((BlockDetailsCell) viewHolder.itemView).setBlock((TL_pageBlockDetails) obj);
                        return;
                    case 25:
                        ((BlockTableCell) viewHolder.itemView).setBlock((TL_pageBlockTable) obj);
                        return;
                    case 26:
                        ((BlockRelatedArticlesHeaderCell) viewHolder.itemView).setBlock((TL_pageBlockRelatedArticles) obj);
                        return;
                    case 27:
                        BlockDetailsBottomCell blockDetailsBottomCell = (BlockDetailsBottomCell) viewHolder.itemView;
                        return;
                    default:
                        return;
                }
            }
            TextView textView = (TextView) viewHolder.itemView;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("unsupported block ");
            stringBuilder.append(obj);
            textView.setText(stringBuilder.toString());
        }

        private int getTypeForBlock(PageBlock pageBlock) {
            if (pageBlock instanceof TL_pageBlockParagraph) {
                return 0;
            }
            if (pageBlock instanceof TL_pageBlockHeader) {
                return 1;
            }
            if (pageBlock instanceof TL_pageBlockDivider) {
                return 2;
            }
            if (pageBlock instanceof TL_pageBlockEmbed) {
                return 3;
            }
            if (pageBlock instanceof TL_pageBlockSubtitle) {
                return 4;
            }
            if (pageBlock instanceof TL_pageBlockVideo) {
                return 5;
            }
            if (pageBlock instanceof TL_pageBlockPullquote) {
                return 6;
            }
            if (pageBlock instanceof TL_pageBlockBlockquote) {
                return 7;
            }
            if (pageBlock instanceof TL_pageBlockSlideshow) {
                return 8;
            }
            if (pageBlock instanceof TL_pageBlockPhoto) {
                return 9;
            }
            if (pageBlock instanceof TL_pageBlockAuthorDate) {
                return 10;
            }
            if (pageBlock instanceof TL_pageBlockTitle) {
                return 11;
            }
            if (pageBlock instanceof TL_pageBlockListItem) {
                return 12;
            }
            if (pageBlock instanceof TL_pageBlockFooter) {
                return 13;
            }
            if (pageBlock instanceof TL_pageBlockPreformatted) {
                return 14;
            }
            if (pageBlock instanceof TL_pageBlockSubheader) {
                return 15;
            }
            if (pageBlock instanceof TL_pageBlockEmbedPost) {
                return 16;
            }
            if (pageBlock instanceof TL_pageBlockCollage) {
                return 17;
            }
            if (pageBlock instanceof TL_pageBlockChannel) {
                return 18;
            }
            if (pageBlock instanceof TL_pageBlockAudio) {
                return 19;
            }
            if (pageBlock instanceof TL_pageBlockKicker) {
                return 20;
            }
            if (pageBlock instanceof TL_pageBlockOrderedListItem) {
                return 21;
            }
            if (pageBlock instanceof TL_pageBlockMap) {
                return 22;
            }
            if (pageBlock instanceof TL_pageBlockRelatedArticlesChild) {
                return 23;
            }
            if (pageBlock instanceof TL_pageBlockDetails) {
                return 24;
            }
            if (pageBlock instanceof TL_pageBlockTable) {
                return 25;
            }
            if (pageBlock instanceof TL_pageBlockRelatedArticles) {
                return 26;
            }
            if (pageBlock instanceof TL_pageBlockDetailsBottom) {
                return 27;
            }
            if (pageBlock instanceof TL_pageBlockRelatedArticlesShadow) {
                return 28;
            }
            if (pageBlock instanceof TL_pageBlockDetailsChild) {
                return getTypeForBlock(((TL_pageBlockDetailsChild) pageBlock).block);
            }
            return pageBlock instanceof TL_pageBlockCover ? getTypeForBlock(((TL_pageBlockCover) pageBlock).cover) : 100;
        }

        public int getItemViewType(int i) {
            if (i == this.localBlocks.size()) {
                return 90;
            }
            return getTypeForBlock((PageBlock) this.localBlocks.get(i));
        }

        public PageBlock getItem(int i) {
            return (PageBlock) this.localBlocks.get(i);
        }

        public int getItemCount() {
            return (ArticleViewer.this.currentPage == null || ArticleViewer.this.currentPage.cached_page == null) ? 0 : this.localBlocks.size() + 1;
        }

        private boolean isBlockOpened(TL_pageBlockDetailsChild tL_pageBlockDetailsChild) {
            PageBlock access$13200 = ArticleViewer.this.getLastNonListPageBlock(tL_pageBlockDetailsChild.parent);
            if (access$13200 instanceof TL_pageBlockDetails) {
                return ((TL_pageBlockDetails) access$13200).open;
            }
            if (!(access$13200 instanceof TL_pageBlockDetailsChild)) {
                return false;
            }
            tL_pageBlockDetailsChild = (TL_pageBlockDetailsChild) access$13200;
            PageBlock access$132002 = ArticleViewer.this.getLastNonListPageBlock(tL_pageBlockDetailsChild.block);
            if (!(access$132002 instanceof TL_pageBlockDetails) || ((TL_pageBlockDetails) access$132002).open) {
                return isBlockOpened(tL_pageBlockDetailsChild);
            }
            return false;
        }

        private void updateRows() {
            this.localBlocks.clear();
            int size = this.blocks.size();
            for (int i = 0; i < size; i++) {
                PageBlock pageBlock = (PageBlock) this.blocks.get(i);
                PageBlock access$13200 = ArticleViewer.this.getLastNonListPageBlock(pageBlock);
                if (!(access$13200 instanceof TL_pageBlockDetailsChild) || isBlockOpened((TL_pageBlockDetailsChild) access$13200)) {
                    this.localBlocks.add(pageBlock);
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
            this.textBlocks.clear();
            this.textToBlocks.clear();
            this.channelBlock = null;
            notifyDataSetChanged();
        }

        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        public void notifyItemChanged(int i) {
            updateRows();
            super.notifyItemChanged(i);
        }

        public void notifyItemChanged(int i, Object obj) {
            updateRows();
            super.notifyItemChanged(i, obj);
        }

        public void notifyItemRangeChanged(int i, int i2) {
            updateRows();
            super.notifyItemRangeChanged(i, i2);
        }

        public void notifyItemRangeChanged(int i, int i2, Object obj) {
            updateRows();
            super.notifyItemRangeChanged(i, i2, obj);
        }

        public void notifyItemInserted(int i) {
            updateRows();
            super.notifyItemInserted(i);
        }

        public void notifyItemMoved(int i, int i2) {
            updateRows();
            super.notifyItemMoved(i, i2);
        }

        public void notifyItemRangeInserted(int i, int i2) {
            updateRows();
            super.notifyItemRangeInserted(i, i2);
        }

        public void notifyItemRemoved(int i) {
            updateRows();
            super.notifyItemRemoved(i);
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            updateRows();
            super.notifyItemRangeRemoved(i, i2);
        }
    }

    private class TL_pageBlockEmbedPostCaption extends TL_pageBlockEmbedPost {
        private TL_pageBlockEmbedPost parent;

        private TL_pageBlockEmbedPostCaption() {
        }

        /* synthetic */ TL_pageBlockEmbedPostCaption(ArticleViewer articleViewer, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private void updateWindowLayoutParamsForSearch() {
    }

    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    public void onLongPress(MotionEvent motionEvent) {
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    static /* synthetic */ int access$1904(ArticleViewer articleViewer) {
        int i = articleViewer.pressCount + 1;
        articleViewer.pressCount = i;
        return i;
    }

    public static ArticleViewer getInstance() {
        ArticleViewer articleViewer = Instance;
        if (articleViewer == null) {
            synchronized (ArticleViewer.class) {
                articleViewer = Instance;
                if (articleViewer == null) {
                    articleViewer = new ArticleViewer();
                    Instance = articleViewer;
                }
            }
        }
        return articleViewer;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    private void createPaint(boolean z) {
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
            webpageSearchPaint = new Paint(1);
            photoBackgroundPaint = new Paint();
            dividerPaint = new Paint();
            webpageMarkPaint = new Paint(1);
        } else if (!z) {
            return;
        }
        int color = Theme.getColor("windowBackgroundWhite");
        webpageSearchPaint.setColor((((((float) Color.red(color)) * 0.2126f) + (((float) Color.green(color)) * 0.7152f)) + (((float) Color.blue(color)) * 0.0722f)) / 255.0f <= 0.705f ? -3041234 : -6551);
        String str = "windowBackgroundWhiteLinkSelection";
        webpageUrlPaint.setColor(Theme.getColor(str) & NUM);
        urlPaint.setColor(Theme.getColor(str) & NUM);
        String str2 = "windowBackgroundWhiteInputField";
        tableHalfLinePaint.setColor(Theme.getColor(str2));
        tableLinePaint.setColor(Theme.getColor(str2));
        photoBackgroundPaint.setColor(NUM);
        dividerPaint.setColor(Theme.getColor("divider"));
        webpageMarkPaint.setColor(Theme.getColor(str) & NUM);
        color = Theme.getColor("switchTrack");
        int red = Color.red(color);
        int green = Color.green(color);
        color = Color.blue(color);
        tableStripPaint.setColor(Color.argb(20, red, green, color));
        tableHeaderPaint.setColor(Color.argb(34, red, green, color));
        color = Theme.getColor(str);
        preformattedBackgroundPaint.setColor(Color.argb(20, Color.red(color), Color.green(color), Color.blue(color)));
        quoteLinePaint.setColor(Theme.getColor("chat_inReplyLine"));
    }

    private void showCopyPopup(String str) {
        if (this.parentActivity != null) {
            BottomSheet bottomSheet = this.linkSheet;
            if (bottomSheet != null) {
                bottomSheet.dismiss();
                this.linkSheet = null;
            }
            Builder builder = new Builder(this.parentActivity);
            builder.setTitle(str);
            builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new -$$Lambda$ArticleViewer$OR-FYCAXpGUOR5Uvrpul7uvfnQI(this, str));
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showCopyPopup$0$ArticleViewer(String str, DialogInterface dialogInterface, int i) {
        if (this.parentActivity != null) {
            if (i == 0) {
                i = str.lastIndexOf(35);
                if (i != -1) {
                    CharSequence toLowerCase;
                    CharSequence decode;
                    if (TextUtils.isEmpty(this.currentPage.cached_page.url)) {
                        toLowerCase = this.currentPage.url.toLowerCase();
                    } else {
                        toLowerCase = this.currentPage.cached_page.url.toLowerCase();
                    }
                    try {
                        decode = URLDecoder.decode(str.substring(i + 1), "UTF-8");
                    } catch (Exception unused) {
                        decode = "";
                    }
                    if (str.toLowerCase().contains(toLowerCase)) {
                        if (TextUtils.isEmpty(decode)) {
                            this.layoutManager[0].scrollToPositionWithOffset(0, 0);
                            checkScrollAnimated();
                        } else {
                            scrollToAnchor(decode);
                        }
                        return;
                    }
                }
                Browser.openUrl(this.parentActivity, str);
            } else if (i == 1) {
                CharSequence str2;
                if (str2.startsWith("mailto:")) {
                    str2 = str2.substring(7);
                } else if (str2.startsWith("tel:")) {
                    str2 = str2.substring(4);
                }
                AndroidUtilities.addToClipboard(str2);
            }
        }
    }

    private void showPopup(View view, int i, int i2, int i3) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                this.popupLayout = new ActionBarPopupWindowLayout(this.parentActivity);
                this.popupLayout.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                this.popupLayout.setBackgroundDrawable(this.parentActivity.getResources().getDrawable(NUM));
                this.popupLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new -$$Lambda$ArticleViewer$MGSGAQzWCEU3w9PF3AH-evurh-k(this));
                this.popupLayout.setDispatchKeyEventListener(new -$$Lambda$ArticleViewer$qD5uCRo_niW9s97tmnD3kf5paxo(this));
                this.popupLayout.setShowedFromBotton(false);
                this.deleteView = new TextView(this.parentActivity);
                this.deleteView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 2));
                this.deleteView.setGravity(16);
                this.deleteView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
                this.deleteView.setTextSize(1, 15.0f);
                this.deleteView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.deleteView.setText(LocaleController.getString("Copy", NUM).toUpperCase());
                this.deleteView.setOnClickListener(new -$$Lambda$ArticleViewer$RDJJaoFjXrUN-_l9em3k_Gv7twk(this));
                this.popupLayout.addView(this.deleteView, LayoutHelper.createFrame(-2, 48.0f));
                this.popupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                this.popupWindow.setAnimationEnabled(false);
                this.popupWindow.setAnimationStyle(NUM);
                this.popupWindow.setOutsideTouchable(true);
                this.popupWindow.setClippingEnabled(true);
                this.popupWindow.setInputMethodMode(2);
                this.popupWindow.setSoftInputMode(0);
                this.popupWindow.getContentView().setFocusableInTouchMode(true);
                this.popupWindow.setOnDismissListener(new -$$Lambda$ArticleViewer$IPD6DMulJZDu02GPHHVkQa-Seko(this));
            }
            this.deleteView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
            if (actionBarPopupWindowLayout != null) {
                actionBarPopupWindowLayout.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
            }
            this.popupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.popupWindow.setFocusable(true);
            this.popupWindow.showAtLocation(view, i, i2, i3);
            this.popupWindow.startAnimation();
            return;
        }
        this.popupWindow.dismiss();
    }

    public /* synthetic */ boolean lambda$showPopup$1$ArticleViewer(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                view.getHitRect(this.popupRect);
                if (!this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    this.popupWindow.dismiss();
                }
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$showPopup$2$ArticleViewer(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.popupWindow.dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$showPopup$3$ArticleViewer(View view) {
        DrawingText drawingText = this.pressedLinkOwnerLayout;
        if (drawingText != null) {
            AndroidUtilities.addToClipboard(drawingText.getText());
            Toast.makeText(this.parentActivity, LocaleController.getString("TextCopied", NUM), 0).show();
        }
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    public /* synthetic */ void lambda$showPopup$4$ArticleViewer() {
        View view = this.pressedLinkOwnerView;
        if (view != null) {
            this.pressedLinkOwnerLayout = null;
            view.invalidate();
            this.pressedLinkOwnerView = null;
        }
    }

    private RichText getBlockCaption(PageBlock pageBlock, int i) {
        if (i == 2) {
            RichText blockCaption = getBlockCaption(pageBlock, 0);
            if (blockCaption instanceof TL_textEmpty) {
                blockCaption = null;
            }
            RichText blockCaption2 = getBlockCaption(pageBlock, 1);
            if (blockCaption2 instanceof TL_textEmpty) {
                blockCaption2 = null;
            }
            if (blockCaption != null && blockCaption2 == null) {
                return blockCaption;
            }
            if (blockCaption == null && blockCaption2 != null) {
                return blockCaption2;
            }
            if (blockCaption == null || blockCaption2 == null) {
                return null;
            }
            TL_textPlain tL_textPlain = new TL_textPlain();
            tL_textPlain.text = " ";
            TL_textConcat tL_textConcat = new TL_textConcat();
            tL_textConcat.texts.add(blockCaption);
            tL_textConcat.texts.add(tL_textPlain);
            tL_textConcat.texts.add(blockCaption2);
            return tL_textConcat;
        }
        if (pageBlock instanceof TL_pageBlockEmbedPost) {
            TL_pageBlockEmbedPost tL_pageBlockEmbedPost = (TL_pageBlockEmbedPost) pageBlock;
            if (i == 0) {
                return tL_pageBlockEmbedPost.caption.text;
            }
            if (i == 1) {
                return tL_pageBlockEmbedPost.caption.credit;
            }
        } else if (pageBlock instanceof TL_pageBlockSlideshow) {
            TL_pageBlockSlideshow tL_pageBlockSlideshow = (TL_pageBlockSlideshow) pageBlock;
            if (i == 0) {
                return tL_pageBlockSlideshow.caption.text;
            }
            if (i == 1) {
                return tL_pageBlockSlideshow.caption.credit;
            }
        } else if (pageBlock instanceof TL_pageBlockPhoto) {
            TL_pageBlockPhoto tL_pageBlockPhoto = (TL_pageBlockPhoto) pageBlock;
            if (i == 0) {
                return tL_pageBlockPhoto.caption.text;
            }
            if (i == 1) {
                return tL_pageBlockPhoto.caption.credit;
            }
        } else if (pageBlock instanceof TL_pageBlockCollage) {
            TL_pageBlockCollage tL_pageBlockCollage = (TL_pageBlockCollage) pageBlock;
            if (i == 0) {
                return tL_pageBlockCollage.caption.text;
            }
            if (i == 1) {
                return tL_pageBlockCollage.caption.credit;
            }
        } else if (pageBlock instanceof TL_pageBlockEmbed) {
            TL_pageBlockEmbed tL_pageBlockEmbed = (TL_pageBlockEmbed) pageBlock;
            if (i == 0) {
                return tL_pageBlockEmbed.caption.text;
            }
            if (i == 1) {
                return tL_pageBlockEmbed.caption.credit;
            }
        } else if (pageBlock instanceof TL_pageBlockBlockquote) {
            return ((TL_pageBlockBlockquote) pageBlock).caption;
        } else {
            if (pageBlock instanceof TL_pageBlockVideo) {
                TL_pageBlockVideo tL_pageBlockVideo = (TL_pageBlockVideo) pageBlock;
                if (i == 0) {
                    return tL_pageBlockVideo.caption.text;
                }
                if (i == 1) {
                    return tL_pageBlockVideo.caption.credit;
                }
            } else if (pageBlock instanceof TL_pageBlockPullquote) {
                return ((TL_pageBlockPullquote) pageBlock).caption;
            } else {
                if (pageBlock instanceof TL_pageBlockAudio) {
                    TL_pageBlockAudio tL_pageBlockAudio = (TL_pageBlockAudio) pageBlock;
                    if (i == 0) {
                        return tL_pageBlockAudio.caption.text;
                    }
                    if (i == 1) {
                        return tL_pageBlockAudio.caption.credit;
                    }
                } else if (pageBlock instanceof TL_pageBlockCover) {
                    return getBlockCaption(((TL_pageBlockCover) pageBlock).cover, i);
                } else {
                    if (pageBlock instanceof TL_pageBlockMap) {
                        TL_pageBlockMap tL_pageBlockMap = (TL_pageBlockMap) pageBlock;
                        if (i == 0) {
                            return tL_pageBlockMap.caption.text;
                        }
                        if (i == 1) {
                            return tL_pageBlockMap.caption.credit;
                        }
                    }
                }
            }
        }
        return null;
    }

    private View getLastNonListCell(View view) {
        if (view instanceof BlockListItemCell) {
            BlockListItemCell blockListItemCell = (BlockListItemCell) view;
            if (blockListItemCell.blockLayout != null) {
                return getLastNonListCell(blockListItemCell.blockLayout.itemView);
            }
        } else if (view instanceof BlockOrderedListItemCell) {
            BlockOrderedListItemCell blockOrderedListItemCell = (BlockOrderedListItemCell) view;
            if (blockOrderedListItemCell.blockLayout != null) {
                view = getLastNonListCell(blockOrderedListItemCell.blockLayout.itemView);
            }
        }
        return view;
    }

    private boolean isListItemBlock(PageBlock pageBlock) {
        return (pageBlock instanceof TL_pageBlockListItem) || (pageBlock instanceof TL_pageBlockOrderedListItem);
    }

    private PageBlock getLastNonListPageBlock(PageBlock pageBlock) {
        if (pageBlock instanceof TL_pageBlockListItem) {
            TL_pageBlockListItem tL_pageBlockListItem = (TL_pageBlockListItem) pageBlock;
            if (tL_pageBlockListItem.blockItem != null) {
                return getLastNonListPageBlock(tL_pageBlockListItem.blockItem);
            }
            return tL_pageBlockListItem.blockItem;
        }
        if (pageBlock instanceof TL_pageBlockOrderedListItem) {
            TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem = (TL_pageBlockOrderedListItem) pageBlock;
            if (tL_pageBlockOrderedListItem.blockItem != null) {
                return getLastNonListPageBlock(tL_pageBlockOrderedListItem.blockItem);
            }
            pageBlock = tL_pageBlockOrderedListItem.blockItem;
        }
        return pageBlock;
    }

    private boolean openAllParentBlocks(TL_pageBlockDetailsChild tL_pageBlockDetailsChild) {
        PageBlock lastNonListPageBlock = getLastNonListPageBlock(tL_pageBlockDetailsChild.parent);
        boolean z = false;
        if (lastNonListPageBlock instanceof TL_pageBlockDetails) {
            TL_pageBlockDetails tL_pageBlockDetails = (TL_pageBlockDetails) lastNonListPageBlock;
            if (tL_pageBlockDetails.open) {
                return false;
            }
            tL_pageBlockDetails.open = true;
            return true;
        }
        if (lastNonListPageBlock instanceof TL_pageBlockDetailsChild) {
            Object obj;
            tL_pageBlockDetailsChild = (TL_pageBlockDetailsChild) lastNonListPageBlock;
            PageBlock lastNonListPageBlock2 = getLastNonListPageBlock(tL_pageBlockDetailsChild.block);
            if (lastNonListPageBlock2 instanceof TL_pageBlockDetails) {
                TL_pageBlockDetails tL_pageBlockDetails2 = (TL_pageBlockDetails) lastNonListPageBlock2;
                if (!tL_pageBlockDetails2.open) {
                    tL_pageBlockDetails2.open = true;
                    obj = 1;
                    if (openAllParentBlocks(tL_pageBlockDetailsChild) || obj != null) {
                        z = true;
                    }
                }
            }
            obj = null;
            z = true;
        }
        return z;
    }

    private PageBlock fixListBlock(PageBlock pageBlock, PageBlock pageBlock2) {
        if (pageBlock instanceof TL_pageBlockListItem) {
            ((TL_pageBlockListItem) pageBlock).blockItem = pageBlock2;
            return pageBlock;
        } else if (!(pageBlock instanceof TL_pageBlockOrderedListItem)) {
            return pageBlock2;
        } else {
            ((TL_pageBlockOrderedListItem) pageBlock).blockItem = pageBlock2;
            return pageBlock;
        }
    }

    private PageBlock wrapInTableBlock(PageBlock pageBlock, PageBlock pageBlock2) {
        if (pageBlock instanceof TL_pageBlockListItem) {
            TL_pageBlockListItem tL_pageBlockListItem = (TL_pageBlockListItem) pageBlock;
            TL_pageBlockListItem tL_pageBlockListItem2 = new TL_pageBlockListItem(this, null);
            tL_pageBlockListItem2.parent = tL_pageBlockListItem.parent;
            tL_pageBlockListItem2.blockItem = wrapInTableBlock(tL_pageBlockListItem.blockItem, pageBlock2);
            return tL_pageBlockListItem2;
        } else if (!(pageBlock instanceof TL_pageBlockOrderedListItem)) {
            return pageBlock2;
        } else {
            TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem = (TL_pageBlockOrderedListItem) pageBlock;
            TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem2 = new TL_pageBlockOrderedListItem(this, null);
            tL_pageBlockOrderedListItem2.parent = tL_pageBlockOrderedListItem.parent;
            tL_pageBlockOrderedListItem2.blockItem = wrapInTableBlock(tL_pageBlockOrderedListItem.blockItem, pageBlock2);
            return tL_pageBlockOrderedListItem2;
        }
    }

    private void updateInterfaceForCurrentPage(boolean z, int i) {
        WebPage webPage = this.currentPage;
        if (webPage != null && webPage.cached_page != null) {
            int indexOfChild;
            int indexOfChild2;
            WebPage webPage2;
            boolean z2 = true;
            if (!(z || i == 0)) {
                WebpageAdapter[] webpageAdapterArr = this.adapter;
                WebpageAdapter webpageAdapter = webpageAdapterArr[1];
                webpageAdapterArr[1] = webpageAdapterArr[0];
                webpageAdapterArr[0] = webpageAdapter;
                RecyclerListView[] recyclerListViewArr = this.listView;
                RecyclerListView recyclerListView = recyclerListViewArr[1];
                recyclerListViewArr[1] = recyclerListViewArr[0];
                recyclerListViewArr[0] = recyclerListView;
                LinearLayoutManager[] linearLayoutManagerArr = this.layoutManager;
                LinearLayoutManager linearLayoutManager = linearLayoutManagerArr[1];
                linearLayoutManagerArr[1] = linearLayoutManagerArr[0];
                linearLayoutManagerArr[0] = linearLayoutManager;
                indexOfChild = this.containerView.indexOfChild(recyclerListViewArr[0]);
                indexOfChild2 = this.containerView.indexOfChild(this.listView[1]);
                if (i == 1) {
                    if (indexOfChild < indexOfChild2) {
                        this.containerView.removeView(this.listView[0]);
                        this.containerView.addView(this.listView[0], indexOfChild2);
                    }
                } else if (indexOfChild2 < indexOfChild) {
                    this.containerView.removeView(this.listView[0]);
                    this.containerView.addView(this.listView[0], indexOfChild);
                }
                this.pageSwitchAnimation = new AnimatorSet();
                this.listView[0].setVisibility(0);
                indexOfChild = i == 1 ? 0 : 1;
                this.listView[indexOfChild].setBackgroundColor(this.backgroundPaint.getColor());
                if (VERSION.SDK_INT >= 18) {
                    this.listView[indexOfChild].setLayerType(2, null);
                }
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (i == 1) {
                    animatorSet = this.pageSwitchAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.listView[0], View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(56.0f), 0.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.listView[0], View.ALPHA, new float[]{0.0f, 1.0f});
                    animatorSet.playTogether(animatorArr);
                } else if (i == -1) {
                    this.listView[0].setAlpha(1.0f);
                    this.listView[0].setTranslationX(0.0f);
                    animatorSet = this.pageSwitchAnimation;
                    animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.listView[1], View.TRANSLATION_X, new float[]{0.0f, (float) AndroidUtilities.dp(56.0f)});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.listView[1], View.ALPHA, new float[]{1.0f, 0.0f});
                    animatorSet.playTogether(animatorArr);
                }
                this.pageSwitchAnimation.setDuration(150);
                this.pageSwitchAnimation.setInterpolator(this.interpolator);
                this.pageSwitchAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ArticleViewer.this.listView[1].setVisibility(8);
                        ArticleViewer articleViewer = ArticleViewer.this;
                        articleViewer.textSelectionHelper.setParentView(articleViewer.listView[0]);
                        articleViewer = ArticleViewer.this;
                        articleViewer.textSelectionHelper.layoutManager = articleViewer.layoutManager[0];
                        ArticleViewer.this.listView[indexOfChild].setBackgroundDrawable(null);
                        if (VERSION.SDK_INT >= 18) {
                            ArticleViewer.this.listView[indexOfChild].setLayerType(0, null);
                        }
                        ArticleViewer.this.pageSwitchAnimation = null;
                    }
                });
                this.pageSwitchAnimation.start();
            }
            if (!z) {
                SimpleTextView simpleTextView = this.titleTextView;
                CharSequence charSequence = this.currentPage.site_name;
                if (charSequence == null) {
                    charSequence = "";
                }
                simpleTextView.setText(charSequence);
                this.textSelectionHelper.clear(true);
                this.headerView.invalidate();
            }
            if (z) {
                ArrayList arrayList = this.pagesStack;
                webPage2 = (WebPage) arrayList.get(arrayList.size() - 2);
            } else {
                webPage2 = this.currentPage;
            }
            this.adapter[z].isRtl = this.currentPage.cached_page.rtl;
            this.adapter[z].cleanup();
            indexOfChild = webPage2.cached_page.blocks.size();
            indexOfChild2 = 0;
            while (indexOfChild2 < indexOfChild) {
                PageBlock pageBlock = (PageBlock) webPage2.cached_page.blocks.get(indexOfChild2);
                if (indexOfChild2 == 0) {
                    pageBlock.first = true;
                    if (pageBlock instanceof TL_pageBlockCover) {
                        TL_pageBlockCover tL_pageBlockCover = (TL_pageBlockCover) pageBlock;
                        RichText blockCaption = getBlockCaption(tL_pageBlockCover, 0);
                        RichText blockCaption2 = getBlockCaption(tL_pageBlockCover, 1);
                        if (!((blockCaption == null || (blockCaption instanceof TL_textEmpty)) && (blockCaption2 == null || (blockCaption2 instanceof TL_textEmpty))) && indexOfChild > 1) {
                            PageBlock pageBlock2 = (PageBlock) webPage2.cached_page.blocks.get(1);
                            if (pageBlock2 instanceof TL_pageBlockChannel) {
                                this.adapter[z].channelBlock = (TL_pageBlockChannel) pageBlock2;
                            }
                        }
                    }
                } else if (indexOfChild2 == 1 && this.adapter[z].channelBlock != null) {
                    indexOfChild2++;
                }
                this.adapter[z].addBlock(pageBlock, 0, 0, indexOfChild2 == indexOfChild + -1 ? indexOfChild2 : 0);
                indexOfChild2++;
            }
            this.adapter[z].notifyDataSetChanged();
            if (this.pagesStack.size() == 1 || i == -1) {
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("article");
                stringBuilder.append(webPage2.id);
                String stringBuilder2 = stringBuilder.toString();
                indexOfChild = sharedPreferences.getInt(stringBuilder2, -1);
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(stringBuilder2);
                stringBuilder3.append("r");
                boolean z3 = sharedPreferences.getBoolean(stringBuilder3.toString(), true);
                Point point = AndroidUtilities.displaySize;
                if (point.x <= point.y) {
                    z2 = false;
                }
                if (z3 == z2) {
                    StringBuilder stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(stringBuilder2);
                    stringBuilder4.append("o");
                    i = sharedPreferences.getInt(stringBuilder4.toString(), 0) - this.listView[z].getPaddingTop();
                } else {
                    i = AndroidUtilities.dp(10.0f);
                }
                if (indexOfChild != -1) {
                    this.layoutManager[z].scrollToPositionWithOffset(indexOfChild, i);
                }
            } else {
                this.layoutManager[z].scrollToPositionWithOffset(0, 0);
            }
            checkScrollAnimated();
        }
    }

    private boolean addPageToStack(WebPage webPage, String str, int i) {
        saveCurrentPagePosition();
        this.currentPage = webPage;
        this.pagesStack.add(webPage);
        showSearch(false);
        updateInterfaceForCurrentPage(false, i);
        return scrollToAnchor(str);
    }

    private boolean scrollToAnchor(String str) {
        boolean isEmpty = TextUtils.isEmpty(str);
        Integer valueOf = Integer.valueOf(0);
        if (isEmpty) {
            return false;
        }
        str = str.toLowerCase();
        Integer num = (Integer) this.adapter[0].anchors.get(str);
        if (num != null) {
            TL_textAnchor tL_textAnchor = (TL_textAnchor) this.adapter[0].anchorsParent.get(str);
            int access$8100;
            if (tL_textAnchor != null) {
                TL_pageBlockParagraph tL_pageBlockParagraph = new TL_pageBlockParagraph();
                tL_pageBlockParagraph.text = tL_textAnchor.text;
                access$8100 = this.adapter[0].getTypeForBlock(tL_pageBlockParagraph);
                ViewHolder onCreateViewHolder = this.adapter[0].onCreateViewHolder(null, access$8100);
                this.adapter[0].bindBlockToHolder(access$8100, onCreateViewHolder, tL_pageBlockParagraph, 0, 0);
                Builder builder = new Builder(this.parentActivity);
                builder.setApplyTopPadding(false);
                builder.setApplyBottomPadding(false);
                final LinearLayout linearLayout = new LinearLayout(this.parentActivity);
                linearLayout.setOrientation(1);
                this.textSelectionHelperBottomSheet = new ArticleTextSelectionHelper();
                this.textSelectionHelperBottomSheet.setParentView(linearLayout);
                this.textSelectionHelperBottomSheet.setCallback(new TextSelectionHelper.Callback() {
                    public void onStateChanged(boolean z) {
                        if (ArticleViewer.this.linkSheet != null) {
                            ArticleViewer.this.linkSheet.setDisableScroll(z);
                        }
                    }
                });
                AnonymousClass4 anonymousClass4 = new TextView(this.parentActivity) {
                    /* Access modifiers changed, original: protected */
                    public void onDraw(Canvas canvas) {
                        canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
                        super.onDraw(canvas);
                    }
                };
                anonymousClass4.setTextSize(1, 16.0f);
                anonymousClass4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                anonymousClass4.setText(LocaleController.getString("InstantViewReference", NUM));
                anonymousClass4.setGravity((this.adapter[0].isRtl ? 5 : 3) | 16);
                anonymousClass4.setTextColor(getTextColor());
                anonymousClass4.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
                linearLayout.addView(anonymousClass4, new LinearLayout.LayoutParams(-1, AndroidUtilities.dp(48.0f) + 1));
                onCreateViewHolder.itemView.setTag("bottomSheet");
                linearLayout.addView(onCreateViewHolder.itemView, LayoutHelper.createLinear(-1, -2, 0.0f, 7.0f, 0.0f, 0.0f));
                TextSelectionOverlay overlayView = this.textSelectionHelperBottomSheet.getOverlayView(this.parentActivity);
                AnonymousClass5 anonymousClass5 = new FrameLayout(this.parentActivity) {
                    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                        TextSelectionOverlay overlayView = ArticleViewer.this.textSelectionHelperBottomSheet.getOverlayView(getContext());
                        MotionEvent obtain = MotionEvent.obtain(motionEvent);
                        obtain.offsetLocation(-linearLayout.getX(), -linearLayout.getY());
                        if (ArticleViewer.this.textSelectionHelperBottomSheet.isSelectionMode() && ArticleViewer.this.textSelectionHelperBottomSheet.getOverlayView(getContext()).onTouchEvent(obtain)) {
                            return true;
                        }
                        if (overlayView.checkOnTap(motionEvent)) {
                            motionEvent.setAction(3);
                        }
                        if (motionEvent.getAction() != 0 || !ArticleViewer.this.textSelectionHelperBottomSheet.isSelectionMode() || (motionEvent.getY() >= ((float) linearLayout.getTop()) && motionEvent.getY() <= ((float) linearLayout.getBottom()))) {
                            return super.dispatchTouchEvent(motionEvent);
                        }
                        if (ArticleViewer.this.textSelectionHelperBottomSheet.getOverlayView(getContext()).onTouchEvent(obtain)) {
                            return super.dispatchTouchEvent(motionEvent);
                        }
                        return true;
                    }

                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, i2);
                        super.onMeasure(i, MeasureSpec.makeMeasureSpec(linearLayout.getMeasuredHeight() + AndroidUtilities.dp(8.0f), NUM));
                    }
                };
                builder.setDelegate(new BottomSheetDelegate() {
                    public boolean canDismiss() {
                        ArticleTextSelectionHelper articleTextSelectionHelper = ArticleViewer.this.textSelectionHelperBottomSheet;
                        if (articleTextSelectionHelper == null || !articleTextSelectionHelper.isSelectionMode()) {
                            return true;
                        }
                        ArticleViewer.this.textSelectionHelperBottomSheet.clear();
                        return false;
                    }
                });
                anonymousClass5.addView(linearLayout, -1, -2);
                anonymousClass5.addView(overlayView, -1, -2);
                builder.setCustomView(anonymousClass5);
                if (this.textSelectionHelper.isSelectionMode()) {
                    this.textSelectionHelper.clear();
                }
                BottomSheet create = builder.create();
                this.linkSheet = create;
                showDialog(create);
                return true;
            } else if (num.intValue() >= 0 && num.intValue() < this.adapter[0].blocks.size()) {
                PageBlock pageBlock = (PageBlock) this.adapter[0].blocks.get(num.intValue());
                PageBlock lastNonListPageBlock = getLastNonListPageBlock(pageBlock);
                if ((lastNonListPageBlock instanceof TL_pageBlockDetailsChild) && openAllParentBlocks((TL_pageBlockDetailsChild) lastNonListPageBlock)) {
                    this.adapter[0].updateRows();
                    this.adapter[0].notifyDataSetChanged();
                }
                int indexOf = this.adapter[0].localBlocks.indexOf(pageBlock);
                if (indexOf != -1) {
                    num = Integer.valueOf(indexOf);
                }
                Integer num2 = (Integer) this.adapter[0].anchorsOffset.get(str);
                if (num2 != null) {
                    if (num2.intValue() == -1) {
                        access$8100 = this.adapter[0].getTypeForBlock(pageBlock);
                        ViewHolder onCreateViewHolder2 = this.adapter[0].onCreateViewHolder(null, access$8100);
                        this.adapter[0].bindBlockToHolder(access$8100, onCreateViewHolder2, pageBlock, 0, 0);
                        onCreateViewHolder2.itemView.measure(MeasureSpec.makeMeasureSpec(this.listView[0].getMeasuredWidth(), NUM), MeasureSpec.makeMeasureSpec(0, 0));
                        Integer num3 = (Integer) this.adapter[0].anchorsOffset.get(str);
                        if (num3.intValue() != -1) {
                            valueOf = num3;
                        }
                    } else {
                        valueOf = num2;
                    }
                }
                this.layoutManager[0].scrollToPositionWithOffset(num.intValue(), (this.currentHeaderHeight - AndroidUtilities.dp(56.0f)) - valueOf.intValue());
                return true;
            }
        }
        return false;
    }

    private boolean removeLastPageFromStack() {
        if (this.pagesStack.size() < 2) {
            return false;
        }
        ArrayList arrayList = this.pagesStack;
        arrayList.remove(arrayList.size() - 1);
        arrayList = this.pagesStack;
        this.currentPage = (WebPage) arrayList.get(arrayList.size() - 1);
        updateInterfaceForCurrentPage(false, -1);
        return true;
    }

    /* Access modifiers changed, original: protected */
    public void startCheckLongPress(float f, float f2, View view) {
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap(this, null);
            }
            if (view.getTag() != null && view.getTag() == "bottomSheet") {
                ArticleTextSelectionHelper articleTextSelectionHelper = this.textSelectionHelperBottomSheet;
                if (articleTextSelectionHelper != null) {
                    articleTextSelectionHelper.setMaybeView((int) f, (int) f2, view);
                    this.windowView.postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
                }
            }
            this.textSelectionHelper.setMaybeView((int) f, (int) f2, view);
            this.windowView.postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    /* Access modifiers changed, original: protected */
    public void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        CheckForLongPress checkForLongPress = this.pendingCheckForLongPress;
        if (checkForLongPress != null) {
            this.windowView.removeCallbacks(checkForLongPress);
            this.pendingCheckForLongPress = null;
        }
        CheckForTap checkForTap = this.pendingCheckForTap;
        if (checkForTap != null) {
            this.windowView.removeCallbacks(checkForTap);
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
            return richText != null ? getTextFlags(richText.parentRichText) : 0;
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
                richText = getLastRichText(((TL_textPhone) richText).text);
            }
        }
        return richText;
    }

    private CharSequence getText(View view, RichText richText, RichText richText2, PageBlock pageBlock, int i) {
        RichText richText3 = richText;
        RichText richText4 = richText2;
        PageBlock pageBlock2 = pageBlock;
        TextPaint textPaint = null;
        if (richText4 == null) {
            return null;
        }
        MetricAffectingSpan[] metricAffectingSpanArr;
        StringBuilder stringBuilder;
        if (richText4 instanceof TL_textFixed) {
            return getText(view, richText, ((TL_textFixed) richText4).text, pageBlock, i);
        } else if (richText4 instanceof TL_textItalic) {
            return getText(view, richText, ((TL_textItalic) richText4).text, pageBlock, i);
        } else if (richText4 instanceof TL_textBold) {
            return getText(view, richText, ((TL_textBold) richText4).text, pageBlock, i);
        } else if (richText4 instanceof TL_textUnderline) {
            return getText(view, richText, ((TL_textUnderline) richText4).text, pageBlock, i);
        } else if (richText4 instanceof TL_textStrike) {
            return getText(view, richText, ((TL_textStrike) richText4).text, pageBlock, i);
        } else if (richText4 instanceof TL_textEmail) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getText(view, richText, ((TL_textEmail) richText4).text, pageBlock, i));
            metricAffectingSpanArr = (MetricAffectingSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), MetricAffectingSpan.class);
            if (spannableStringBuilder.length() != 0) {
                if (metricAffectingSpanArr == null || metricAffectingSpanArr.length == 0) {
                    textPaint = getTextPaint(richText3, richText4, pageBlock2);
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("mailto:");
                stringBuilder.append(getUrl(richText2));
                spannableStringBuilder.setSpan(new TextPaintUrlSpan(textPaint, stringBuilder.toString()), 0, spannableStringBuilder.length(), 33);
            }
            return spannableStringBuilder;
        } else {
            long j = 0;
            SpannableStringBuilder spannableStringBuilder2;
            TextPaint textPaint2;
            if (richText4 instanceof TL_textUrl) {
                Object textPaintWebpageUrlSpan;
                TL_textUrl tL_textUrl = (TL_textUrl) richText4;
                spannableStringBuilder2 = new SpannableStringBuilder(getText(view, richText, tL_textUrl.text, pageBlock, i));
                metricAffectingSpanArr = (MetricAffectingSpan[]) spannableStringBuilder2.getSpans(0, spannableStringBuilder2.length(), MetricAffectingSpan.class);
                textPaint2 = (metricAffectingSpanArr == null || metricAffectingSpanArr.length == 0) ? getTextPaint(richText3, richText4, pageBlock2) : null;
                if (tL_textUrl.webpage_id != 0) {
                    textPaintWebpageUrlSpan = new TextPaintWebpageUrlSpan(textPaint2, getUrl(richText2));
                } else {
                    textPaintWebpageUrlSpan = new TextPaintUrlSpan(textPaint2, getUrl(richText2));
                }
                if (spannableStringBuilder2.length() != 0) {
                    spannableStringBuilder2.setSpan(textPaintWebpageUrlSpan, 0, spannableStringBuilder2.length(), 33);
                }
                return spannableStringBuilder2;
            } else if (richText4 instanceof TL_textPlain) {
                return ((TL_textPlain) richText4).text;
            } else {
                if (richText4 instanceof TL_textAnchor) {
                    TL_textAnchor tL_textAnchor = (TL_textAnchor) richText4;
                    spannableStringBuilder2 = new SpannableStringBuilder(getText(view, richText, tL_textAnchor.text, pageBlock, i));
                    spannableStringBuilder2.setSpan(new AnchorSpan(tL_textAnchor.name), 0, spannableStringBuilder2.length(), 17);
                    return spannableStringBuilder2;
                }
                String str = "";
                if (richText4 instanceof TL_textEmpty) {
                    return str;
                }
                int textFlags;
                int length;
                if (richText4 instanceof TL_textConcat) {
                    SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder();
                    int size = richText4.texts.size();
                    int i2 = 0;
                    while (i2 < size) {
                        RichText richText5 = (RichText) richText4.texts.get(i2);
                        RichText lastRichText = getLastRichText(richText5);
                        Object obj = (i < 0 || !(richText5 instanceof TL_textUrl) || ((TL_textUrl) richText5).webpage_id == j) ? null : 1;
                        str = " ";
                        if (!(obj == null || spannableStringBuilder3.length() == 0 || spannableStringBuilder3.charAt(spannableStringBuilder3.length() - 1) == 10)) {
                            spannableStringBuilder3.append(str);
                            spannableStringBuilder3.setSpan(new IgnoreCopySpannable(), spannableStringBuilder3.length() - 1, spannableStringBuilder3.length(), 33);
                        }
                        String str2 = str;
                        RichText richText6 = lastRichText;
                        RichText richText7 = richText5;
                        int i3 = i2;
                        int i4 = size;
                        CharSequence text = getText(view, richText, richText5, pageBlock, i);
                        textFlags = getTextFlags(richText6);
                        length = spannableStringBuilder3.length();
                        spannableStringBuilder3.append(text);
                        if (!(textFlags == 0 || (text instanceof SpannableStringBuilder))) {
                            if ((textFlags & 8) != 0 || (textFlags & 512) != 0) {
                                Object textPaintWebpageUrlSpan2;
                                String url = getUrl(richText7);
                                if (url == null) {
                                    url = getUrl(richText);
                                }
                                if ((textFlags & 512) != 0) {
                                    textPaintWebpageUrlSpan2 = new TextPaintWebpageUrlSpan(getTextPaint(richText3, richText6, pageBlock2), url);
                                } else {
                                    textPaintWebpageUrlSpan2 = new TextPaintUrlSpan(getTextPaint(richText3, richText6, pageBlock2), url);
                                }
                                if (length != spannableStringBuilder3.length()) {
                                    spannableStringBuilder3.setSpan(textPaintWebpageUrlSpan2, length, spannableStringBuilder3.length(), 33);
                                }
                            } else if (length != spannableStringBuilder3.length()) {
                                spannableStringBuilder3.setSpan(new TextPaintSpan(getTextPaint(richText3, richText6, pageBlock2)), length, spannableStringBuilder3.length(), 33);
                            }
                        }
                        if (!(obj == null || i3 == i4 - 1)) {
                            spannableStringBuilder3.append(str2);
                            spannableStringBuilder3.setSpan(new IgnoreCopySpannable(), spannableStringBuilder3.length() - 1, spannableStringBuilder3.length(), 33);
                        }
                        i2 = i3 + 1;
                        size = i4;
                        j = 0;
                    }
                    return spannableStringBuilder3;
                } else if (richText4 instanceof TL_textSubscript) {
                    return getText(view, richText, ((TL_textSubscript) richText4).text, pageBlock, i);
                } else if (richText4 instanceof TL_textSuperscript) {
                    return getText(view, richText, ((TL_textSuperscript) richText4).text, pageBlock, i);
                } else if (richText4 instanceof TL_textMarked) {
                    spannableStringBuilder2 = new SpannableStringBuilder(getText(view, richText, ((TL_textMarked) richText4).text, pageBlock, i));
                    metricAffectingSpanArr = (MetricAffectingSpan[]) spannableStringBuilder2.getSpans(0, spannableStringBuilder2.length(), MetricAffectingSpan.class);
                    if (spannableStringBuilder2.length() != 0) {
                        textPaint2 = (metricAffectingSpanArr == null || metricAffectingSpanArr.length == 0) ? getTextPaint(richText3, richText4, pageBlock2) : null;
                        spannableStringBuilder2.setSpan(new TextPaintMarkSpan(textPaint2), 0, spannableStringBuilder2.length(), 33);
                    }
                    return spannableStringBuilder2;
                } else if (richText4 instanceof TL_textPhone) {
                    try {
                        spannableStringBuilder2 = new SpannableStringBuilder(getText(view, richText, ((TL_textPhone) richText4).text, pageBlock, i));
                        metricAffectingSpanArr = (MetricAffectingSpan[]) spannableStringBuilder2.getSpans(0, spannableStringBuilder2.length(), MetricAffectingSpan.class);
                        if (spannableStringBuilder2.length() != 0) {
                            textPaint2 = (metricAffectingSpanArr == null || metricAffectingSpanArr.length == 0) ? getTextPaint(richText3, richText4, pageBlock2) : null;
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("tel:");
                            stringBuilder2.append(getUrl(richText2));
                            spannableStringBuilder2.setSpan(new TextPaintUrlSpan(textPaint2, stringBuilder2.toString()), 0, spannableStringBuilder2.length(), 33);
                        }
                        return spannableStringBuilder2;
                    } catch (Throwable th) {
                        Throwable th2 = th;
                    }
                } else if (richText4 instanceof TL_textImage) {
                    TL_textImage tL_textImage = (TL_textImage) richText4;
                    Document documentWithId = getDocumentWithId(tL_textImage.document_id);
                    if (documentWithId == null) {
                        return str;
                    }
                    int i5;
                    int i6;
                    SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder("*");
                    textFlags = AndroidUtilities.dp((float) tL_textImage.w);
                    int dp = AndroidUtilities.dp((float) tL_textImage.h);
                    length = Math.abs(i);
                    if (textFlags > length) {
                        i5 = (int) (((float) dp) * (((float) length) / ((float) textFlags)));
                        i6 = length;
                    } else {
                        i5 = dp;
                        i6 = textFlags;
                    }
                    if (view != null) {
                        dp = Theme.getColor("windowBackgroundWhite");
                        spannableStringBuilder4.setSpan(new TextPaintImageReceiverSpan(view, documentWithId, this.currentPage, i6, i5, false, (((((float) Color.red(dp)) * 0.2126f) + (((float) Color.green(dp)) * 0.7152f)) + (((float) Color.blue(dp)) * 0.0722f)) / 255.0f <= 0.705f), 0, spannableStringBuilder4.length(), 33);
                    }
                    return spannableStringBuilder4;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("not supported ");
                    stringBuilder.append(richText4);
                    return stringBuilder.toString();
                }
            }
        }
    }

    public static CharSequence getPlainText(RichText richText) {
        String str = "";
        if (richText == null) {
            return str;
        }
        if (richText instanceof TL_textFixed) {
            return getPlainText(((TL_textFixed) richText).text);
        }
        if (richText instanceof TL_textItalic) {
            return getPlainText(((TL_textItalic) richText).text);
        }
        if (richText instanceof TL_textBold) {
            return getPlainText(((TL_textBold) richText).text);
        }
        if (richText instanceof TL_textUnderline) {
            return getPlainText(((TL_textUnderline) richText).text);
        }
        if (richText instanceof TL_textStrike) {
            return getPlainText(((TL_textStrike) richText).text);
        }
        if (richText instanceof TL_textEmail) {
            return getPlainText(((TL_textEmail) richText).text);
        }
        if (richText instanceof TL_textUrl) {
            return getPlainText(((TL_textUrl) richText).text);
        }
        if (richText instanceof TL_textPlain) {
            return ((TL_textPlain) richText).text;
        }
        if (richText instanceof TL_textAnchor) {
            return getPlainText(((TL_textAnchor) richText).text);
        }
        if (richText instanceof TL_textEmpty) {
            return str;
        }
        if (richText instanceof TL_textConcat) {
            StringBuilder stringBuilder = new StringBuilder();
            int size = richText.texts.size();
            for (int i = 0; i < size; i++) {
                stringBuilder.append(getPlainText((RichText) richText.texts.get(i)));
            }
            return stringBuilder;
        } else if (richText instanceof TL_textSubscript) {
            return getPlainText(((TL_textSubscript) richText).text);
        } else {
            if (richText instanceof TL_textSuperscript) {
                return getPlainText(((TL_textSuperscript) richText).text);
            }
            if (richText instanceof TL_textMarked) {
                return getPlainText(((TL_textMarked) richText).text);
            }
            if (richText instanceof TL_textPhone) {
                return getPlainText(((TL_textPhone) richText).text);
            }
            if (richText instanceof TL_textImage) {
            }
            return str;
        }
    }

    public static String getUrl(RichText richText) {
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
        return richText instanceof TL_textPhone ? ((TL_textPhone) richText).phone : null;
    }

    private int getTextColor() {
        return Theme.getColor("windowBackgroundWhiteBlackText");
    }

    private int getLinkTextColor() {
        return Theme.getColor("windowBackgroundWhiteLinkText");
    }

    private int getGrayTextColor() {
        return Theme.getColor("windowBackgroundWhiteGrayText");
    }

    private TextPaint getTextPaint(RichText richText, RichText richText2, PageBlock pageBlock) {
        int dp;
        int grayTextColor;
        int textFlags = getTextFlags(richText2);
        int dp2 = AndroidUtilities.dp(14.0f);
        int dp3 = AndroidUtilities.dp((float) (SharedConfig.ivFontSize - 16));
        SparseArray sparseArray = null;
        RichText richText3;
        SparseArray sparseArray2;
        if (pageBlock instanceof TL_pageBlockPhoto) {
            richText3 = ((TL_pageBlockPhoto) pageBlock).caption.text;
            if (richText3 == richText2 || richText3 == richText) {
                sparseArray2 = photoCaptionTextPaints;
                dp = AndroidUtilities.dp(14.0f);
            } else {
                sparseArray2 = photoCreditTextPaints;
                dp = AndroidUtilities.dp(12.0f);
            }
            sparseArray = sparseArray2;
            dp2 = dp;
            grayTextColor = getGrayTextColor();
        } else if (pageBlock instanceof TL_pageBlockMap) {
            richText3 = ((TL_pageBlockMap) pageBlock).caption.text;
            if (richText3 == richText2 || richText3 == richText) {
                sparseArray2 = photoCaptionTextPaints;
                dp = AndroidUtilities.dp(14.0f);
            } else {
                sparseArray2 = photoCreditTextPaints;
                dp = AndroidUtilities.dp(12.0f);
            }
            sparseArray = sparseArray2;
            dp2 = dp;
            grayTextColor = getGrayTextColor();
        } else if (pageBlock instanceof TL_pageBlockTitle) {
            sparseArray = titleTextPaints;
            dp2 = AndroidUtilities.dp(23.0f);
            grayTextColor = getTextColor();
        } else if (pageBlock instanceof TL_pageBlockKicker) {
            sparseArray = kickerTextPaints;
            dp2 = AndroidUtilities.dp(14.0f);
            grayTextColor = getTextColor();
        } else if (pageBlock instanceof TL_pageBlockAuthorDate) {
            sparseArray = authorTextPaints;
            dp2 = AndroidUtilities.dp(14.0f);
            grayTextColor = getGrayTextColor();
        } else if (pageBlock instanceof TL_pageBlockFooter) {
            sparseArray = footerTextPaints;
            dp2 = AndroidUtilities.dp(14.0f);
            grayTextColor = getGrayTextColor();
        } else if (pageBlock instanceof TL_pageBlockSubtitle) {
            sparseArray = subtitleTextPaints;
            dp2 = AndroidUtilities.dp(20.0f);
            grayTextColor = getTextColor();
        } else if (pageBlock instanceof TL_pageBlockHeader) {
            sparseArray = headerTextPaints;
            dp2 = AndroidUtilities.dp(20.0f);
            grayTextColor = getTextColor();
        } else if (pageBlock instanceof TL_pageBlockSubheader) {
            sparseArray = subheaderTextPaints;
            dp2 = AndroidUtilities.dp(17.0f);
            grayTextColor = getTextColor();
        } else {
            if (pageBlock instanceof TL_pageBlockBlockquote) {
                TL_pageBlockBlockquote tL_pageBlockBlockquote = (TL_pageBlockBlockquote) pageBlock;
                if (tL_pageBlockBlockquote.text == richText) {
                    sparseArray = quoteTextPaints;
                    dp2 = AndroidUtilities.dp(15.0f);
                    grayTextColor = getTextColor();
                } else if (tL_pageBlockBlockquote.caption == richText) {
                    sparseArray = photoCaptionTextPaints;
                    dp2 = AndroidUtilities.dp(14.0f);
                    grayTextColor = getGrayTextColor();
                }
            } else if (pageBlock instanceof TL_pageBlockPullquote) {
                TL_pageBlockPullquote tL_pageBlockPullquote = (TL_pageBlockPullquote) pageBlock;
                if (tL_pageBlockPullquote.text == richText) {
                    sparseArray = quoteTextPaints;
                    dp2 = AndroidUtilities.dp(15.0f);
                    grayTextColor = getTextColor();
                } else if (tL_pageBlockPullquote.caption == richText) {
                    sparseArray = photoCaptionTextPaints;
                    dp2 = AndroidUtilities.dp(14.0f);
                    grayTextColor = getGrayTextColor();
                }
            } else if (pageBlock instanceof TL_pageBlockPreformatted) {
                sparseArray = preformattedTextPaints;
                dp2 = AndroidUtilities.dp(14.0f);
                grayTextColor = getTextColor();
            } else if (pageBlock instanceof TL_pageBlockParagraph) {
                sparseArray = paragraphTextPaints;
                dp2 = AndroidUtilities.dp(16.0f);
                grayTextColor = getTextColor();
            } else if (isListItemBlock(pageBlock)) {
                sparseArray = listTextPaints;
                dp2 = AndroidUtilities.dp(16.0f);
                grayTextColor = getTextColor();
            } else if (pageBlock instanceof TL_pageBlockEmbed) {
                richText3 = ((TL_pageBlockEmbed) pageBlock).caption.text;
                if (richText3 == richText2 || richText3 == richText) {
                    sparseArray2 = photoCaptionTextPaints;
                    dp = AndroidUtilities.dp(14.0f);
                } else {
                    sparseArray2 = photoCreditTextPaints;
                    dp = AndroidUtilities.dp(12.0f);
                }
                sparseArray = sparseArray2;
                dp2 = dp;
                grayTextColor = getGrayTextColor();
            } else if (pageBlock instanceof TL_pageBlockSlideshow) {
                richText3 = ((TL_pageBlockSlideshow) pageBlock).caption.text;
                if (richText3 == richText2 || richText3 == richText) {
                    sparseArray2 = photoCaptionTextPaints;
                    dp = AndroidUtilities.dp(14.0f);
                } else {
                    sparseArray2 = photoCreditTextPaints;
                    dp = AndroidUtilities.dp(12.0f);
                }
                sparseArray = sparseArray2;
                dp2 = dp;
                grayTextColor = getGrayTextColor();
            } else if (pageBlock instanceof TL_pageBlockCollage) {
                richText3 = ((TL_pageBlockCollage) pageBlock).caption.text;
                if (richText3 == richText2 || richText3 == richText) {
                    sparseArray2 = photoCaptionTextPaints;
                    dp = AndroidUtilities.dp(14.0f);
                } else {
                    sparseArray2 = photoCreditTextPaints;
                    dp = AndroidUtilities.dp(12.0f);
                }
                sparseArray = sparseArray2;
                dp2 = dp;
                grayTextColor = getGrayTextColor();
            } else if (pageBlock instanceof TL_pageBlockEmbedPost) {
                TL_pageCaption tL_pageCaption = ((TL_pageBlockEmbedPost) pageBlock).caption;
                if (richText2 == tL_pageCaption.text) {
                    sparseArray = photoCaptionTextPaints;
                    dp2 = AndroidUtilities.dp(14.0f);
                    grayTextColor = getGrayTextColor();
                } else if (richText2 == tL_pageCaption.credit) {
                    sparseArray = photoCreditTextPaints;
                    dp2 = AndroidUtilities.dp(12.0f);
                    grayTextColor = getGrayTextColor();
                } else if (richText2 != null) {
                    sparseArray = embedPostTextPaints;
                    dp2 = AndroidUtilities.dp(14.0f);
                    grayTextColor = getTextColor();
                }
            } else {
                if (pageBlock instanceof TL_pageBlockVideo) {
                    if (richText2 == ((TL_pageBlockVideo) pageBlock).caption.text) {
                        sparseArray2 = mediaCaptionTextPaints;
                        dp = AndroidUtilities.dp(14.0f);
                        dp2 = getTextColor();
                    } else {
                        sparseArray2 = mediaCreditTextPaints;
                        dp = AndroidUtilities.dp(12.0f);
                        dp2 = getTextColor();
                    }
                } else if (pageBlock instanceof TL_pageBlockAudio) {
                    if (richText2 == ((TL_pageBlockAudio) pageBlock).caption.text) {
                        sparseArray2 = mediaCaptionTextPaints;
                        dp = AndroidUtilities.dp(14.0f);
                        dp2 = getTextColor();
                    } else {
                        sparseArray2 = mediaCreditTextPaints;
                        dp = AndroidUtilities.dp(12.0f);
                        dp2 = getTextColor();
                    }
                } else if (pageBlock instanceof TL_pageBlockRelatedArticles) {
                    sparseArray = relatedArticleTextPaints;
                    dp2 = AndroidUtilities.dp(15.0f);
                    grayTextColor = getGrayTextColor();
                } else if (pageBlock instanceof TL_pageBlockDetails) {
                    sparseArray = detailsTextPaints;
                    dp2 = AndroidUtilities.dp(15.0f);
                    grayTextColor = getTextColor();
                } else if (pageBlock instanceof TL_pageBlockTable) {
                    sparseArray = tableTextPaints;
                    dp2 = AndroidUtilities.dp(15.0f);
                    grayTextColor = getTextColor();
                }
                sparseArray = sparseArray2;
                grayTextColor = dp2;
                dp2 = dp;
            }
            grayTextColor = -65536;
        }
        dp = textFlags & 256;
        if (!(dp == 0 && (textFlags & 128) == 0)) {
            dp2 -= AndroidUtilities.dp(4.0f);
        }
        if (sparseArray == null) {
            if (errorTextPaint == null) {
                errorTextPaint = new TextPaint(1);
                errorTextPaint.setColor(-65536);
            }
            errorTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            return errorTextPaint;
        }
        TextPaint textPaint = (TextPaint) sparseArray.get(textFlags);
        if (textPaint == null) {
            textPaint = new TextPaint(1);
            if ((textFlags & 4) != 0) {
                textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
            } else {
                String str = "fonts/rmedium.ttf";
                int i;
                if (pageBlock instanceof TL_pageBlockRelatedArticles) {
                    textPaint.setTypeface(AndroidUtilities.getTypeface(str));
                } else if (this.selectedFont != 1 && !(pageBlock instanceof TL_pageBlockTitle) && !(pageBlock instanceof TL_pageBlockKicker) && !(pageBlock instanceof TL_pageBlockHeader) && !(pageBlock instanceof TL_pageBlockSubtitle) && !(pageBlock instanceof TL_pageBlockSubheader)) {
                    i = textFlags & 1;
                    if (i != 0 && (textFlags & 2) != 0) {
                        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf"));
                    } else if (i != 0) {
                        textPaint.setTypeface(AndroidUtilities.getTypeface(str));
                    } else if ((textFlags & 2) != 0) {
                        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/ritalic.ttf"));
                    }
                } else if ((pageBlock instanceof TL_pageBlockTitle) || (pageBlock instanceof TL_pageBlockHeader) || (pageBlock instanceof TL_pageBlockSubtitle) || (pageBlock instanceof TL_pageBlockSubheader)) {
                    textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/mw_bold.ttf"));
                } else {
                    i = textFlags & 1;
                    String str2 = "serif";
                    if (i != 0 && (textFlags & 2) != 0) {
                        textPaint.setTypeface(Typeface.create(str2, 3));
                    } else if (i != 0) {
                        textPaint.setTypeface(Typeface.create(str2, 1));
                    } else if ((textFlags & 2) != 0) {
                        textPaint.setTypeface(Typeface.create(str2, 2));
                    } else {
                        textPaint.setTypeface(Typeface.create(str2, 0));
                    }
                }
            }
            if ((textFlags & 32) != 0) {
                textPaint.setFlags(textPaint.getFlags() | 16);
            }
            if ((textFlags & 16) != 0) {
                textPaint.setFlags(textPaint.getFlags() | 8);
            }
            if (!((textFlags & 8) == 0 && (textFlags & 512) == 0)) {
                textPaint.setFlags(textPaint.getFlags());
                grayTextColor = getLinkTextColor();
            }
            if (dp != 0) {
                textPaint.baselineShift -= AndroidUtilities.dp(6.0f);
            } else if ((textFlags & 128) != 0) {
                textPaint.baselineShift += AndroidUtilities.dp(2.0f);
            }
            textPaint.setColor(grayTextColor);
            sparseArray.put(textFlags, textPaint);
        }
        textPaint.setTextSize((float) (dp2 + dp3));
        return textPaint;
    }

    private DrawingText createLayoutForText(View view, CharSequence charSequence, RichText richText, int i, int i2, PageBlock pageBlock, Alignment alignment, WebpageAdapter webpageAdapter) {
        return createLayoutForText(view, charSequence, richText, i, 0, pageBlock, alignment, 0, webpageAdapter);
    }

    private DrawingText createLayoutForText(View view, CharSequence charSequence, RichText richText, int i, int i2, PageBlock pageBlock, WebpageAdapter webpageAdapter) {
        return createLayoutForText(view, charSequence, richText, i, i2, pageBlock, Alignment.ALIGN_NORMAL, 0, webpageAdapter);
    }

    /* JADX WARNING: Removed duplicated region for block: B:148:0x0387 A:{Catch:{ Exception -> 0x03c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0387 A:{Catch:{ Exception -> 0x03c5 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:138:0x036a */
    /* JADX WARNING: Failed to process nested try/catch */
    private org.telegram.ui.ArticleViewer.DrawingText createLayoutForText(android.view.View r22, java.lang.CharSequence r23, org.telegram.tgnet.TLRPC.RichText r24, int r25, int r26, org.telegram.tgnet.TLRPC.PageBlock r27, android.text.Layout.Alignment r28, int r29, org.telegram.ui.ArticleViewer.WebpageAdapter r30) {
        /*
        r21 = this;
        r6 = r21;
        r7 = r23;
        r8 = r24;
        r9 = r27;
        r10 = 0;
        if (r7 != 0) goto L_0x0012;
    L_0x000b:
        if (r8 == 0) goto L_0x0011;
    L_0x000d:
        r0 = r8 instanceof org.telegram.tgnet.TLRPC.TL_textEmpty;
        if (r0 == 0) goto L_0x0012;
    L_0x0011:
        return r10;
    L_0x0012:
        if (r25 >= 0) goto L_0x001d;
    L_0x0014:
        r0 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r19 = r0;
        goto L_0x001f;
    L_0x001d:
        r19 = r25;
    L_0x001f:
        if (r7 == 0) goto L_0x0023;
    L_0x0021:
        r0 = r7;
        goto L_0x0033;
    L_0x0023:
        r0 = r21;
        r1 = r22;
        r2 = r24;
        r3 = r24;
        r4 = r27;
        r5 = r19;
        r0 = r0.getText(r1, r2, r3, r4, r5);
    L_0x0033:
        r1 = android.text.TextUtils.isEmpty(r0);
        if (r1 == 0) goto L_0x003a;
    L_0x0039:
        return r10;
    L_0x003a:
        r1 = org.telegram.messenger.SharedConfig.ivFontSize;
        r1 = r1 + -16;
        r1 = (float) r1;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost;
        r3 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r5 = 1;
        if (r2 == 0) goto L_0x009b;
    L_0x004c:
        if (r8 != 0) goto L_0x009b;
    L_0x004e:
        r2 = r9;
        r2 = (org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost) r2;
        r2 = r2.author;
        if (r2 != r7) goto L_0x0078;
    L_0x0055:
        r2 = embedPostAuthorPaint;
        if (r2 != 0) goto L_0x0069;
    L_0x0059:
        r2 = new android.text.TextPaint;
        r2.<init>(r5);
        embedPostAuthorPaint = r2;
        r2 = embedPostAuthorPaint;
        r3 = r21.getTextColor();
        r2.setColor(r3);
    L_0x0069:
        r2 = embedPostAuthorPaint;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 + r1;
        r1 = (float) r3;
        r2.setTextSize(r1);
        r1 = embedPostAuthorPaint;
        goto L_0x01c6;
    L_0x0078:
        r2 = embedPostDatePaint;
        if (r2 != 0) goto L_0x008c;
    L_0x007c:
        r2 = new android.text.TextPaint;
        r2.<init>(r5);
        embedPostDatePaint = r2;
        r2 = embedPostDatePaint;
        r4 = r21.getGrayTextColor();
        r2.setColor(r4);
    L_0x008c:
        r2 = embedPostDatePaint;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r1;
        r1 = (float) r3;
        r2.setTextSize(r1);
        r1 = embedPostDatePaint;
        goto L_0x01c6;
    L_0x009b:
        r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockChannel;
        r11 = "fonts/rmedium.ttf";
        if (r2 == 0) goto L_0x00f6;
    L_0x00a1:
        r1 = channelNamePaint;
        if (r1 != 0) goto L_0x00c5;
    L_0x00a5:
        r1 = new android.text.TextPaint;
        r1.<init>(r5);
        channelNamePaint = r1;
        r1 = channelNamePaint;
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r11);
        r1.setTypeface(r2);
        r1 = new android.text.TextPaint;
        r1.<init>(r5);
        channelNamePhotoPaint = r1;
        r1 = channelNamePhotoPaint;
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r11);
        r1.setTypeface(r2);
    L_0x00c5:
        r1 = channelNamePaint;
        r2 = r21.getTextColor();
        r1.setColor(r2);
        r1 = channelNamePaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = (float) r2;
        r1.setTextSize(r2);
        r1 = channelNamePhotoPaint;
        r2 = -1;
        r1.setColor(r2);
        r1 = channelNamePhotoPaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = (float) r2;
        r1.setTextSize(r2);
        r1 = r30.channelBlock;
        if (r1 == 0) goto L_0x00f2;
    L_0x00ee:
        r1 = channelNamePhotoPaint;
        goto L_0x01c6;
    L_0x00f2:
        r1 = channelNamePaint;
        goto L_0x01c6;
    L_0x00f6:
        r2 = r9 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockRelatedArticlesChild;
        if (r2 == 0) goto L_0x015f;
    L_0x00fa:
        r2 = r9;
        r2 = (org.telegram.ui.ArticleViewer.TL_pageBlockRelatedArticlesChild) r2;
        r12 = r2.parent;
        r12 = r12.articles;
        r2 = r2.num;
        r2 = r12.get(r2);
        r2 = (org.telegram.tgnet.TLRPC.TL_pageRelatedArticle) r2;
        r2 = r2.title;
        if (r7 != r2) goto L_0x013d;
    L_0x0111:
        r2 = relatedArticleHeaderPaint;
        if (r2 != 0) goto L_0x0125;
    L_0x0115:
        r2 = new android.text.TextPaint;
        r2.<init>(r5);
        relatedArticleHeaderPaint = r2;
        r2 = relatedArticleHeaderPaint;
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r11);
        r2.setTypeface(r3);
    L_0x0125:
        r2 = relatedArticleHeaderPaint;
        r3 = r21.getTextColor();
        r2.setColor(r3);
        r2 = relatedArticleHeaderPaint;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 + r1;
        r1 = (float) r3;
        r2.setTextSize(r1);
        r1 = relatedArticleHeaderPaint;
        goto L_0x01c6;
    L_0x013d:
        r2 = relatedArticleTextPaint;
        if (r2 != 0) goto L_0x0148;
    L_0x0141:
        r2 = new android.text.TextPaint;
        r2.<init>(r5);
        relatedArticleTextPaint = r2;
    L_0x0148:
        r2 = relatedArticleTextPaint;
        r4 = r21.getGrayTextColor();
        r2.setColor(r4);
        r2 = relatedArticleTextPaint;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r1;
        r1 = (float) r3;
        r2.setTextSize(r1);
        r1 = relatedArticleTextPaint;
        goto L_0x01c6;
    L_0x015f:
        r2 = r6.isListItemBlock(r9);
        if (r2 == 0) goto L_0x01c2;
    L_0x0165:
        if (r7 == 0) goto L_0x01c2;
    L_0x0167:
        r2 = listTextPointerPaint;
        if (r2 != 0) goto L_0x017b;
    L_0x016b:
        r2 = new android.text.TextPaint;
        r2.<init>(r5);
        listTextPointerPaint = r2;
        r2 = listTextPointerPaint;
        r3 = r21.getTextColor();
        r2.setColor(r3);
    L_0x017b:
        r2 = listTextNumPaint;
        if (r2 != 0) goto L_0x018f;
    L_0x017f:
        r2 = new android.text.TextPaint;
        r2.<init>(r5);
        listTextNumPaint = r2;
        r2 = listTextNumPaint;
        r3 = r21.getTextColor();
        r2.setColor(r3);
    L_0x018f:
        r2 = listTextPointerPaint;
        r3 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r1;
        r3 = (float) r3;
        r2.setTextSize(r3);
        r2 = listTextNumPaint;
        r3 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r1;
        r1 = (float) r3;
        r2.setTextSize(r1);
        r1 = r9 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockListItem;
        if (r1 == 0) goto L_0x01bf;
    L_0x01ad:
        r1 = r9;
        r1 = (org.telegram.ui.ArticleViewer.TL_pageBlockListItem) r1;
        r1 = r1.parent;
        r1 = r1.pageBlockList;
        r1 = r1.ordered;
        if (r1 != 0) goto L_0x01bf;
    L_0x01bc:
        r1 = listTextPointerPaint;
        goto L_0x01c6;
    L_0x01bf:
        r1 = listTextNumPaint;
        goto L_0x01c6;
    L_0x01c2:
        r1 = r6.getTextPaint(r8, r8, r9);
    L_0x01c6:
        r13 = r1;
        r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r2 = 0;
        if (r29 == 0) goto L_0x01ff;
    L_0x01cc:
        r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockPullquote;
        if (r3 == 0) goto L_0x01e5;
    L_0x01d0:
        r14 = android.text.Layout.Alignment.ALIGN_CENTER;
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r16 = 0;
        r17 = 0;
        r18 = android.text.TextUtils.TruncateAt.END;
        r11 = r0;
        r12 = r13;
        r13 = r19;
        r20 = r29;
        r1 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        goto L_0x0242;
    L_0x01e5:
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r17 = 0;
        r18 = android.text.TextUtils.TruncateAt.END;
        r11 = r0;
        r12 = r13;
        r13 = r19;
        r14 = r28;
        r16 = r1;
        r20 = r29;
        r1 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        goto L_0x0242;
    L_0x01ff:
        r3 = r0.length();
        r3 = r3 - r5;
        r3 = r0.charAt(r3);
        r4 = 10;
        if (r3 != r4) goto L_0x0215;
    L_0x020c:
        r3 = r0.length();
        r3 = r3 - r5;
        r0 = r0.subSequence(r2, r3);
    L_0x0215:
        r3 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockPullquote;
        if (r3 == 0) goto L_0x022b;
    L_0x0219:
        r1 = new android.text.StaticLayout;
        r15 = android.text.Layout.Alignment.ALIGN_CENTER;
        r16 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r17 = 0;
        r18 = 0;
        r11 = r1;
        r12 = r0;
        r14 = r19;
        r11.<init>(r12, r13, r14, r15, r16, r17, r18);
        goto L_0x0242;
    L_0x022b:
        r3 = new android.text.StaticLayout;
        r16 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r18 = 0;
        r11 = r3;
        r12 = r0;
        r14 = r19;
        r15 = r28;
        r17 = r1;
        r11.<init>(r12, r13, r14, r15, r16, r17, r18);
        r1 = r3;
    L_0x0242:
        if (r1 != 0) goto L_0x0245;
    L_0x0244:
        return r10;
    L_0x0245:
        r3 = r1.getText();
        if (r26 < 0) goto L_0x02b1;
    L_0x024b:
        if (r1 == 0) goto L_0x02b1;
    L_0x024d:
        r4 = r6.searchResults;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x02b1;
    L_0x0255:
        r4 = r6.searchText;
        if (r4 == 0) goto L_0x02b1;
    L_0x0259:
        r0 = r0.toString();
        r0 = r0.toLowerCase();
        r4 = 0;
    L_0x0262:
        r7 = r6.searchText;
        r4 = r0.indexOf(r7, r4);
        if (r4 < 0) goto L_0x02b1;
    L_0x026a:
        r7 = r6.searchText;
        r7 = r7.length();
        r7 = r7 + r4;
        if (r4 == 0) goto L_0x027f;
    L_0x0273:
        r11 = r4 + -1;
        r11 = r0.charAt(r11);
        r11 = org.telegram.messenger.AndroidUtilities.isPunctuationCharacter(r11);
        if (r11 == 0) goto L_0x02af;
    L_0x027f:
        r11 = r6.adapter;
        r11 = r11[r2];
        r11 = r11.searchTextOffset;
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r13 = r6.searchText;
        r12.append(r13);
        r12.append(r9);
        r12.append(r8);
        r12.append(r4);
        r12 = r12.toString();
        r4 = r1.getLineForOffset(r4);
        r4 = r1.getLineTop(r4);
        r4 = r26 + r4;
        r4 = java.lang.Integer.valueOf(r4);
        r11.put(r12, r4);
    L_0x02af:
        r4 = r7;
        goto L_0x0262;
    L_0x02b1:
        if (r1 == 0) goto L_0x03c7;
    L_0x02b3:
        r0 = r3 instanceof android.text.Spanned;
        if (r0 == 0) goto L_0x03c7;
    L_0x02b7:
        r3 = (android.text.Spanned) r3;
        r0 = r3.length();	 Catch:{ Exception -> 0x030a }
        r4 = org.telegram.ui.Components.AnchorSpan.class;
        r0 = r3.getSpans(r2, r0, r4);	 Catch:{ Exception -> 0x030a }
        r0 = (org.telegram.ui.Components.AnchorSpan[]) r0;	 Catch:{ Exception -> 0x030a }
        r4 = r1.getLineCount();	 Catch:{ Exception -> 0x030a }
        if (r0 == 0) goto L_0x030a;
    L_0x02cb:
        r7 = r0.length;	 Catch:{ Exception -> 0x030a }
        if (r7 <= 0) goto L_0x030a;
    L_0x02ce:
        r7 = 0;
    L_0x02cf:
        r11 = r0.length;	 Catch:{ Exception -> 0x030a }
        if (r7 >= r11) goto L_0x030a;
    L_0x02d2:
        if (r4 > r5) goto L_0x02e6;
    L_0x02d4:
        r11 = r30.anchorsOffset;	 Catch:{ Exception -> 0x030a }
        r12 = r0[r7];	 Catch:{ Exception -> 0x030a }
        r12 = r12.getName();	 Catch:{ Exception -> 0x030a }
        r13 = java.lang.Integer.valueOf(r26);	 Catch:{ Exception -> 0x030a }
        r11.put(r12, r13);	 Catch:{ Exception -> 0x030a }
        goto L_0x0307;
    L_0x02e6:
        r11 = r30.anchorsOffset;	 Catch:{ Exception -> 0x030a }
        r12 = r0[r7];	 Catch:{ Exception -> 0x030a }
        r12 = r12.getName();	 Catch:{ Exception -> 0x030a }
        r13 = r0[r7];	 Catch:{ Exception -> 0x030a }
        r13 = r3.getSpanStart(r13);	 Catch:{ Exception -> 0x030a }
        r13 = r1.getLineForOffset(r13);	 Catch:{ Exception -> 0x030a }
        r13 = r1.getLineTop(r13);	 Catch:{ Exception -> 0x030a }
        r13 = r26 + r13;
        r13 = java.lang.Integer.valueOf(r13);	 Catch:{ Exception -> 0x030a }
        r11.put(r12, r13);	 Catch:{ Exception -> 0x030a }
    L_0x0307:
        r7 = r7 + 1;
        goto L_0x02cf;
    L_0x030a:
        r4 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r7 = 0;
        r11 = r3.length();	 Catch:{ Exception -> 0x0369 }
        r12 = org.telegram.ui.Components.TextPaintWebpageUrlSpan.class;
        r11 = r3.getSpans(r2, r11, r12);	 Catch:{ Exception -> 0x0369 }
        r11 = (org.telegram.ui.Components.TextPaintWebpageUrlSpan[]) r11;	 Catch:{ Exception -> 0x0369 }
        if (r11 == 0) goto L_0x0369;
    L_0x031b:
        r12 = r11.length;	 Catch:{ Exception -> 0x0369 }
        if (r12 <= 0) goto L_0x0369;
    L_0x031e:
        r12 = new org.telegram.ui.Components.LinkPath;	 Catch:{ Exception -> 0x0369 }
        r12.<init>(r5);	 Catch:{ Exception -> 0x0369 }
        r12.setAllowReset(r2);	 Catch:{ Exception -> 0x036a }
        r13 = 0;
    L_0x0327:
        r14 = r11.length;	 Catch:{ Exception -> 0x036a }
        if (r13 >= r14) goto L_0x0365;
    L_0x032a:
        r14 = r11[r13];	 Catch:{ Exception -> 0x036a }
        r14 = r3.getSpanStart(r14);	 Catch:{ Exception -> 0x036a }
        r15 = r11[r13];	 Catch:{ Exception -> 0x036a }
        r15 = r3.getSpanEnd(r15);	 Catch:{ Exception -> 0x036a }
        r12.setCurrentLayout(r1, r14, r7);	 Catch:{ Exception -> 0x036a }
        r16 = r11[r13];	 Catch:{ Exception -> 0x036a }
        r16 = r16.getTextPaint();	 Catch:{ Exception -> 0x036a }
        if (r16 == 0) goto L_0x034a;
    L_0x0341:
        r16 = r11[r13];	 Catch:{ Exception -> 0x036a }
        r0 = r16.getTextPaint();	 Catch:{ Exception -> 0x036a }
        r0 = r0.baselineShift;	 Catch:{ Exception -> 0x036a }
        goto L_0x034b;
    L_0x034a:
        r0 = 0;
    L_0x034b:
        if (r0 == 0) goto L_0x035b;
    L_0x034d:
        if (r0 <= 0) goto L_0x0352;
    L_0x034f:
        r16 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        goto L_0x0354;
    L_0x0352:
        r16 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
    L_0x0354:
        r16 = org.telegram.messenger.AndroidUtilities.dp(r16);	 Catch:{ Exception -> 0x036a }
        r0 = r0 + r16;
        goto L_0x035c;
    L_0x035b:
        r0 = 0;
    L_0x035c:
        r12.setBaselineShift(r0);	 Catch:{ Exception -> 0x036a }
        r1.getSelectionPath(r14, r15, r12);	 Catch:{ Exception -> 0x036a }
        r13 = r13 + 1;
        goto L_0x0327;
    L_0x0365:
        r12.setAllowReset(r5);	 Catch:{ Exception -> 0x036a }
        goto L_0x036a;
    L_0x0369:
        r12 = r10;
    L_0x036a:
        r0 = r3.length();	 Catch:{ Exception -> 0x03c8 }
        r11 = org.telegram.ui.Components.TextPaintMarkSpan.class;
        r0 = r3.getSpans(r2, r0, r11);	 Catch:{ Exception -> 0x03c8 }
        r0 = (org.telegram.ui.Components.TextPaintMarkSpan[]) r0;	 Catch:{ Exception -> 0x03c8 }
        if (r0 == 0) goto L_0x03c8;
    L_0x0378:
        r11 = r0.length;	 Catch:{ Exception -> 0x03c8 }
        if (r11 <= 0) goto L_0x03c8;
    L_0x037b:
        r11 = new org.telegram.ui.Components.LinkPath;	 Catch:{ Exception -> 0x03c8 }
        r11.<init>(r5);	 Catch:{ Exception -> 0x03c8 }
        r11.setAllowReset(r2);	 Catch:{ Exception -> 0x03c5 }
        r10 = 0;
    L_0x0384:
        r13 = r0.length;	 Catch:{ Exception -> 0x03c5 }
        if (r10 >= r13) goto L_0x03c2;
    L_0x0387:
        r13 = r0[r10];	 Catch:{ Exception -> 0x03c5 }
        r13 = r3.getSpanStart(r13);	 Catch:{ Exception -> 0x03c5 }
        r14 = r0[r10];	 Catch:{ Exception -> 0x03c5 }
        r14 = r3.getSpanEnd(r14);	 Catch:{ Exception -> 0x03c5 }
        r11.setCurrentLayout(r1, r13, r7);	 Catch:{ Exception -> 0x03c5 }
        r15 = r0[r10];	 Catch:{ Exception -> 0x03c5 }
        r15 = r15.getTextPaint();	 Catch:{ Exception -> 0x03c5 }
        if (r15 == 0) goto L_0x03a7;
    L_0x039e:
        r15 = r0[r10];	 Catch:{ Exception -> 0x03c5 }
        r15 = r15.getTextPaint();	 Catch:{ Exception -> 0x03c5 }
        r15 = r15.baselineShift;	 Catch:{ Exception -> 0x03c5 }
        goto L_0x03a8;
    L_0x03a7:
        r15 = 0;
    L_0x03a8:
        if (r15 == 0) goto L_0x03b8;
    L_0x03aa:
        if (r15 <= 0) goto L_0x03af;
    L_0x03ac:
        r16 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        goto L_0x03b1;
    L_0x03af:
        r16 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
    L_0x03b1:
        r16 = org.telegram.messenger.AndroidUtilities.dp(r16);	 Catch:{ Exception -> 0x03c5 }
        r15 = r15 + r16;
        goto L_0x03b9;
    L_0x03b8:
        r15 = 0;
    L_0x03b9:
        r11.setBaselineShift(r15);	 Catch:{ Exception -> 0x03c5 }
        r1.getSelectionPath(r13, r14, r11);	 Catch:{ Exception -> 0x03c5 }
        r10 = r10 + 1;
        goto L_0x0384;
    L_0x03c2:
        r11.setAllowReset(r5);	 Catch:{ Exception -> 0x03c5 }
    L_0x03c5:
        r10 = r11;
        goto L_0x03c8;
    L_0x03c7:
        r12 = r10;
    L_0x03c8:
        r0 = new org.telegram.ui.ArticleViewer$DrawingText;
        r0.<init>();
        r0.textLayout = r1;
        r0.textPath = r12;
        r0.markPath = r10;
        r0.parentBlock = r9;
        r0.parentText = r8;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.createLayoutForText(android.view.View, java.lang.CharSequence, org.telegram.tgnet.TLRPC$RichText, int, int, org.telegram.tgnet.TLRPC$PageBlock, android.text.Layout$Alignment, int, org.telegram.ui.ArticleViewer$WebpageAdapter):org.telegram.ui.ArticleViewer$DrawingText");
    }

    private void drawLayoutLink(Canvas canvas, DrawingText drawingText) {
        if (canvas != null && drawingText != null && this.pressedLinkOwnerLayout == drawingText) {
            if (this.pressedLink != null) {
                canvas.drawPath(this.urlPath, urlPaint);
            } else if (this.drawBlockSelection && drawingText != null) {
                float lineWidth;
                float lineLeft;
                if (drawingText.getLineCount() == 1) {
                    lineWidth = drawingText.getLineWidth(0);
                    lineLeft = drawingText.getLineLeft(0);
                } else {
                    lineWidth = (float) drawingText.getWidth();
                    lineLeft = 0.0f;
                }
                canvas.drawRect(((float) (-AndroidUtilities.dp(2.0f))) + lineLeft, 0.0f, (lineLeft + lineWidth) + ((float) AndroidUtilities.dp(2.0f)), (float) drawingText.getHeight(), urlPaint);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:88:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01aa  */
    /* JADX WARNING: Missing block: B:94:0x01a3, code skipped:
            if (r0.isShowing() != false) goto L_0x01a7;
     */
    private boolean checkLayoutForLinks(android.view.MotionEvent r17, android.view.View r18, org.telegram.ui.ArticleViewer.DrawingText r19, int r20, int r21) {
        /*
        r16 = this;
        r1 = r16;
        r2 = r18;
        r0 = r19;
        r3 = r20;
        r4 = r21;
        r5 = r1.pageSwitchAnimation;
        r6 = 0;
        if (r5 != 0) goto L_0x01e0;
    L_0x000f:
        if (r2 == 0) goto L_0x01e0;
    L_0x0011:
        r5 = r1.textSelectionHelper;
        r5 = r5.isSelectable(r2);
        if (r5 != 0) goto L_0x001b;
    L_0x0019:
        goto L_0x01e0;
    L_0x001b:
        r1.pressedLinkOwnerView = r2;
        r5 = 1;
        if (r0 == 0) goto L_0x01ad;
    L_0x0020:
        r7 = r0.textLayout;
        r8 = r17.getX();
        r8 = (int) r8;
        r9 = r17.getY();
        r9 = (int) r9;
        r10 = r17.getAction();
        if (r10 != 0) goto L_0x0116;
    L_0x0032:
        r10 = NUM; // 0x4var_ float:2.14748365E9 double:6.548346386E-315;
        r11 = r7.getLineCount();
        r12 = 0;
        r10 = 0;
        r13 = NUM; // 0x4var_ float:2.14748365E9 double:6.548346386E-315;
        r14 = 0;
    L_0x003d:
        if (r10 >= r11) goto L_0x0052;
    L_0x003f:
        r15 = r7.getLineWidth(r10);
        r14 = java.lang.Math.max(r15, r14);
        r15 = r7.getLineLeft(r10);
        r13 = java.lang.Math.min(r15, r13);
        r10 = r10 + 1;
        goto L_0x003d;
    L_0x0052:
        r10 = (float) r8;
        r11 = (float) r3;
        r11 = r11 + r13;
        r13 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1));
        if (r13 < 0) goto L_0x01a7;
    L_0x0059:
        r11 = r11 + r14;
        r10 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1));
        if (r10 > 0) goto L_0x01a7;
    L_0x005e:
        if (r9 < r4) goto L_0x01a7;
    L_0x0060:
        r10 = r7.getHeight();
        r10 = r10 + r4;
        if (r9 > r10) goto L_0x01a7;
    L_0x0067:
        r1.pressedLinkOwnerLayout = r0;
        r1.pressedLinkOwnerView = r2;
        r1.pressedLayoutY = r4;
        r0 = r7.getText();
        r0 = r0 instanceof android.text.Spannable;
        if (r0 == 0) goto L_0x01a7;
    L_0x0075:
        r8 = r8 - r3;
        r9 = r9 - r4;
        r0 = r7.getLineForVertical(r9);	 Catch:{ Exception -> 0x0110 }
        r3 = (float) r8;	 Catch:{ Exception -> 0x0110 }
        r4 = r7.getOffsetForHorizontal(r0, r3);	 Catch:{ Exception -> 0x0110 }
        r8 = r7.getLineLeft(r0);	 Catch:{ Exception -> 0x0110 }
        r9 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1));
        if (r9 > 0) goto L_0x01a7;
    L_0x0088:
        r0 = r7.getLineWidth(r0);	 Catch:{ Exception -> 0x0110 }
        r8 = r8 + r0;
        r0 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1));
        if (r0 < 0) goto L_0x01a7;
    L_0x0091:
        r0 = r7.getText();	 Catch:{ Exception -> 0x0110 }
        r0 = (android.text.Spannable) r0;	 Catch:{ Exception -> 0x0110 }
        r3 = org.telegram.ui.Components.TextPaintUrlSpan.class;
        r3 = r0.getSpans(r4, r4, r3);	 Catch:{ Exception -> 0x0110 }
        r3 = (org.telegram.ui.Components.TextPaintUrlSpan[]) r3;	 Catch:{ Exception -> 0x0110 }
        if (r3 == 0) goto L_0x01a7;
    L_0x00a1:
        r4 = r3.length;	 Catch:{ Exception -> 0x0110 }
        if (r4 <= 0) goto L_0x01a7;
    L_0x00a4:
        r4 = r3[r6];	 Catch:{ Exception -> 0x0110 }
        r1.pressedLink = r4;	 Catch:{ Exception -> 0x0110 }
        r4 = r1.pressedLink;	 Catch:{ Exception -> 0x0110 }
        r4 = r0.getSpanStart(r4);	 Catch:{ Exception -> 0x0110 }
        r8 = r1.pressedLink;	 Catch:{ Exception -> 0x0110 }
        r8 = r0.getSpanEnd(r8);	 Catch:{ Exception -> 0x0110 }
        r9 = r8;
        r8 = r4;
        r4 = 1;
    L_0x00b7:
        r10 = r3.length;	 Catch:{ Exception -> 0x0110 }
        if (r4 >= r10) goto L_0x00cf;
    L_0x00ba:
        r10 = r3[r4];	 Catch:{ Exception -> 0x0110 }
        r11 = r0.getSpanStart(r10);	 Catch:{ Exception -> 0x0110 }
        r13 = r0.getSpanEnd(r10);	 Catch:{ Exception -> 0x0110 }
        if (r8 > r11) goto L_0x00c8;
    L_0x00c6:
        if (r13 <= r9) goto L_0x00cc;
    L_0x00c8:
        r1.pressedLink = r10;	 Catch:{ Exception -> 0x0110 }
        r8 = r11;
        r9 = r13;
    L_0x00cc:
        r4 = r4 + 1;
        goto L_0x00b7;
    L_0x00cf:
        r0 = r1.urlPath;	 Catch:{ Exception -> 0x010a }
        r0.setUseRoundRect(r5);	 Catch:{ Exception -> 0x010a }
        r0 = r1.urlPath;	 Catch:{ Exception -> 0x010a }
        r0.setCurrentLayout(r7, r8, r12);	 Catch:{ Exception -> 0x010a }
        r0 = r1.pressedLink;	 Catch:{ Exception -> 0x010a }
        r0 = r0.getTextPaint();	 Catch:{ Exception -> 0x010a }
        if (r0 == 0) goto L_0x00ea;
    L_0x00e1:
        r0 = r1.pressedLink;	 Catch:{ Exception -> 0x010a }
        r0 = r0.getTextPaint();	 Catch:{ Exception -> 0x010a }
        r0 = r0.baselineShift;	 Catch:{ Exception -> 0x010a }
        goto L_0x00eb;
    L_0x00ea:
        r0 = 0;
    L_0x00eb:
        r3 = r1.urlPath;	 Catch:{ Exception -> 0x010a }
        if (r0 == 0) goto L_0x00fc;
    L_0x00ef:
        if (r0 <= 0) goto L_0x00f4;
    L_0x00f1:
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        goto L_0x00f6;
    L_0x00f4:
        r4 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
    L_0x00f6:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x010a }
        r0 = r0 + r4;
        goto L_0x00fd;
    L_0x00fc:
        r0 = 0;
    L_0x00fd:
        r3.setBaselineShift(r0);	 Catch:{ Exception -> 0x010a }
        r0 = r1.urlPath;	 Catch:{ Exception -> 0x010a }
        r7.getSelectionPath(r8, r9, r0);	 Catch:{ Exception -> 0x010a }
        r18.invalidate();	 Catch:{ Exception -> 0x010a }
        goto L_0x01a7;
    L_0x010a:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0110 }
        goto L_0x01a7;
    L_0x0110:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x01a7;
    L_0x0116:
        r0 = r17.getAction();
        if (r0 != r5) goto L_0x0194;
    L_0x011c:
        r0 = r1.pressedLink;
        if (r0 == 0) goto L_0x01a7;
    L_0x0120:
        r0 = r0.getUrl();
        if (r0 == 0) goto L_0x01a5;
    L_0x0126:
        r3 = r1.linkSheet;
        r4 = 0;
        if (r3 == 0) goto L_0x0130;
    L_0x012b:
        r3.dismiss();
        r1.linkSheet = r4;
    L_0x0130:
        r3 = 35;
        r3 = r0.lastIndexOf(r3);
        r7 = -1;
        if (r3 == r7) goto L_0x0186;
    L_0x0139:
        r4 = r1.currentPage;
        r4 = r4.cached_page;
        r4 = r4.url;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x0150;
    L_0x0145:
        r4 = r1.currentPage;
        r4 = r4.cached_page;
        r4 = r4.url;
        r4 = r4.toLowerCase();
        goto L_0x0158;
    L_0x0150:
        r4 = r1.currentPage;
        r4 = r4.url;
        r4 = r4.toLowerCase();
    L_0x0158:
        r3 = r3 + r5;
        r3 = r0.substring(r3);	 Catch:{ Exception -> 0x0164 }
        r7 = "UTF-8";
        r3 = java.net.URLDecoder.decode(r3, r7);	 Catch:{ Exception -> 0x0164 }
        goto L_0x0166;
    L_0x0164:
        r3 = "";
    L_0x0166:
        r0 = r0.toLowerCase();
        r0 = r0.contains(r4);
        if (r0 == 0) goto L_0x0187;
    L_0x0170:
        r0 = android.text.TextUtils.isEmpty(r3);
        if (r0 == 0) goto L_0x0181;
    L_0x0176:
        r0 = r1.layoutManager;
        r0 = r0[r6];
        r0.scrollToPositionWithOffset(r6, r6);
        r16.checkScrollAnimated();
        goto L_0x0184;
    L_0x0181:
        r1.scrollToAnchor(r3);
    L_0x0184:
        r0 = 1;
        goto L_0x0188;
    L_0x0186:
        r3 = r4;
    L_0x0187:
        r0 = 0;
    L_0x0188:
        if (r0 != 0) goto L_0x01a5;
    L_0x018a:
        r0 = r1.pressedLink;
        r0 = r0.getUrl();
        r1.openWebpageUrl(r0, r3);
        goto L_0x01a5;
    L_0x0194:
        r0 = r17.getAction();
        r3 = 3;
        if (r0 != r3) goto L_0x01a7;
    L_0x019b:
        r0 = r1.popupWindow;
        if (r0 == 0) goto L_0x01a5;
    L_0x019f:
        r0 = r0.isShowing();
        if (r0 != 0) goto L_0x01a7;
    L_0x01a5:
        r0 = 1;
        goto L_0x01a8;
    L_0x01a7:
        r0 = 0;
    L_0x01a8:
        if (r0 == 0) goto L_0x01ad;
    L_0x01aa:
        r16.removePressedLink();
    L_0x01ad:
        r0 = r17.getAction();
        if (r0 != 0) goto L_0x01be;
    L_0x01b3:
        r0 = r17.getX();
        r3 = r17.getY();
        r1.startCheckLongPress(r0, r3, r2);
    L_0x01be:
        r0 = r17.getAction();
        if (r0 == 0) goto L_0x01ce;
    L_0x01c4:
        r0 = r17.getAction();
        r3 = 2;
        if (r0 == r3) goto L_0x01ce;
    L_0x01cb:
        r16.cancelCheckLongPress();
    L_0x01ce:
        r0 = r2 instanceof org.telegram.ui.ArticleViewer.BlockDetailsCell;
        if (r0 == 0) goto L_0x01d9;
    L_0x01d2:
        r0 = r1.pressedLink;
        if (r0 == 0) goto L_0x01d7;
    L_0x01d6:
        goto L_0x01d8;
    L_0x01d7:
        r5 = 0;
    L_0x01d8:
        return r5;
    L_0x01d9:
        r0 = r1.pressedLinkOwnerLayout;
        if (r0 == 0) goto L_0x01de;
    L_0x01dd:
        goto L_0x01df;
    L_0x01de:
        r5 = 0;
    L_0x01df:
        return r5;
    L_0x01e0:
        return r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.checkLayoutForLinks(android.view.MotionEvent, android.view.View, org.telegram.ui.ArticleViewer$DrawingText, int, int):boolean");
    }

    private void removePressedLink() {
        if (this.pressedLink != null || this.pressedLinkOwnerView != null) {
            View view = this.pressedLinkOwnerView;
            this.pressedLink = null;
            this.pressedLinkOwnerLayout = null;
            this.pressedLinkOwnerView = null;
            if (view != null) {
                view.invalidate();
            }
        }
    }

    private void openWebpageUrl(String str, String str2) {
        if (this.openUrlReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.openUrlReqId, false);
            this.openUrlReqId = 0;
        }
        int i = this.lastReqId + 1;
        this.lastReqId = i;
        closePhoto(false);
        showProgressView(true, true);
        TL_messages_getWebPage tL_messages_getWebPage = new TL_messages_getWebPage();
        tL_messages_getWebPage.url = str;
        tL_messages_getWebPage.hash = 0;
        this.openUrlReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getWebPage, new -$$Lambda$ArticleViewer$CIJ3NzkK6eZMINaSMugn6BEySDI(this, i, str2, tL_messages_getWebPage));
    }

    public /* synthetic */ void lambda$openWebpageUrl$6$ArticleViewer(int i, String str, TL_messages_getWebPage tL_messages_getWebPage, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$wFBfqyMKleu-xH4t-92wfKx_WCw(this, i, tLObject, str, tL_messages_getWebPage));
    }

    public /* synthetic */ void lambda$null$5$ArticleViewer(int i, TLObject tLObject, String str, TL_messages_getWebPage tL_messages_getWebPage) {
        if (this.openUrlReqId != 0 && i == this.lastReqId) {
            this.openUrlReqId = 0;
            showProgressView(true, false);
            if (this.isVisible) {
                if (tLObject instanceof TL_webPage) {
                    TL_webPage tL_webPage = (TL_webPage) tLObject;
                    if (tL_webPage.cached_page instanceof TL_page) {
                        addPageToStack(tL_webPage, str, 1);
                        return;
                    }
                }
                Browser.openUrl(this.parentActivity, tL_messages_getWebPage.url);
            }
        }
    }

    private Photo getPhotoWithId(long j) {
        WebPage webPage = this.currentPage;
        if (!(webPage == null || webPage.cached_page == null)) {
            Photo photo = webPage.photo;
            if (photo != null && photo.id == j) {
                return photo;
            }
            for (int i = 0; i < this.currentPage.cached_page.photos.size(); i++) {
                Photo photo2 = (Photo) this.currentPage.cached_page.photos.get(i);
                if (photo2.id == j) {
                    return photo2;
                }
            }
        }
        return null;
    }

    private Document getDocumentWithId(long j) {
        WebPage webPage = this.currentPage;
        if (!(webPage == null || webPage.cached_page == null)) {
            Document document = webPage.document;
            if (document != null && document.id == j) {
                return document;
            }
            for (int i = 0; i < this.currentPage.cached_page.documents.size(); i++) {
                Document document2 = (Document) this.currentPage.cached_page.documents.get(i);
                if (document2.id == j) {
                    return document2;
                }
            }
        }
        return null;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        String str;
        String[] strArr;
        RecyclerListView[] recyclerListViewArr;
        int i4;
        View childAt;
        if (i == NotificationCenter.fileDidFailToLoad) {
            str = (String) objArr[0];
            while (i3 < 3) {
                strArr = this.currentFileNames;
                if (strArr[i3] == null || !strArr[i3].equals(str)) {
                    i3++;
                } else {
                    this.radialProgressViews[i3].setProgress(1.0f, true);
                    checkProgress(i3, true);
                    return;
                }
            }
        } else if (i == NotificationCenter.fileDidLoad) {
            str = (String) objArr[0];
            i2 = 0;
            while (i2 < 3) {
                String[] strArr2 = this.currentFileNames;
                if (strArr2[i2] == null || !strArr2[i2].equals(str)) {
                    i2++;
                } else {
                    this.radialProgressViews[i2].setProgress(1.0f, true);
                    checkProgress(i2, true);
                    if (i2 == 0 && isMediaVideo(this.currentIndex)) {
                        onActionClick(false);
                        return;
                    }
                    return;
                }
            }
        } else if (i == NotificationCenter.FileLoadProgressChanged) {
            str = (String) objArr[0];
            while (i3 < 3) {
                strArr = this.currentFileNames;
                if (strArr[i3] != null && strArr[i3].equals(str)) {
                    this.radialProgressViews[i3].setProgress(Math.min(1.0f, ((float) ((Long) objArr[1]).longValue()) / ((float) ((Long) objArr[2]).longValue())), true);
                }
                i3++;
            }
        } else if (i == NotificationCenter.emojiDidLoad) {
            TextView textView = this.captionTextView;
            if (textView != null) {
                textView.invalidate();
            }
        } else if (i == NotificationCenter.messagePlayingDidStart) {
            MessageObject messageObject = (MessageObject) objArr[0];
            if (this.listView != null) {
                i = 0;
                while (true) {
                    recyclerListViewArr = this.listView;
                    if (i < recyclerListViewArr.length) {
                        i2 = recyclerListViewArr[i].getChildCount();
                        for (i4 = 0; i4 < i2; i4++) {
                            childAt = this.listView[i].getChildAt(i4);
                            if (childAt instanceof BlockAudioCell) {
                                ((BlockAudioCell) childAt).updateButtonState(true);
                            }
                        }
                        i++;
                    } else {
                        return;
                    }
                }
            }
        } else if (i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.messagePlayingPlayStateChanged) {
            if (this.listView != null) {
                i = 0;
                while (true) {
                    recyclerListViewArr = this.listView;
                    if (i < recyclerListViewArr.length) {
                        i2 = recyclerListViewArr[i].getChildCount();
                        for (i4 = 0; i4 < i2; i4++) {
                            childAt = this.listView[i].getChildAt(i4);
                            if (childAt instanceof BlockAudioCell) {
                                BlockAudioCell blockAudioCell = (BlockAudioCell) childAt;
                                if (blockAudioCell.getMessageObject() != null) {
                                    blockAudioCell.updateButtonState(true);
                                }
                            }
                        }
                        i++;
                    } else {
                        return;
                    }
                }
            }
        } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer num = (Integer) objArr[0];
            if (this.listView != null) {
                i2 = 0;
                while (true) {
                    RecyclerListView[] recyclerListViewArr2 = this.listView;
                    if (i2 < recyclerListViewArr2.length) {
                        i4 = recyclerListViewArr2[i2].getChildCount();
                        for (int i5 = 0; i5 < i4; i5++) {
                            View childAt2 = this.listView[i2].getChildAt(i5);
                            if (childAt2 instanceof BlockAudioCell) {
                                BlockAudioCell blockAudioCell2 = (BlockAudioCell) childAt2;
                                MessageObject messageObject2 = blockAudioCell2.getMessageObject();
                                if (messageObject2 != null && messageObject2.getId() == num.intValue()) {
                                    MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                                    if (playingMessageObject != null) {
                                        messageObject2.audioProgress = playingMessageObject.audioProgress;
                                        messageObject2.audioProgressSec = playingMessageObject.audioProgressSec;
                                        messageObject2.audioPlayerDuration = playingMessageObject.audioPlayerDuration;
                                        blockAudioCell2.updatePlayingMessageProgress();
                                    }
                                    i2++;
                                }
                            }
                        }
                        i2++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public void updateThemeColors(float f) {
        refreshThemeColors();
        updatePaintColors();
        if (this.windowView != null) {
            this.listView[0].invalidateViews();
            this.listView[1].invalidateViews();
            this.windowView.invalidate();
            this.searchPanel.invalidate();
            if (f == 1.0f) {
                this.adapter[0].notifyDataSetChanged();
                this.adapter[1].notifyDataSetChanged();
            }
        }
    }

    private void updatePaintSize() {
        for (int i = 0; i < 2; i++) {
            this.adapter[i].notifyDataSetChanged();
        }
    }

    private void updatePaintFonts() {
        int i;
        int i2 = 0;
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_type", this.selectedFont).commit();
        Typeface typeface = this.selectedFont == 0 ? Typeface.DEFAULT : Typeface.SERIF;
        String str = "serif";
        Typeface typeface2 = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/ritalic.ttf") : Typeface.create(str, 2);
        Typeface typeface3 = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmedium.ttf") : Typeface.create(str, 1);
        Typeface typeface4 = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf") : Typeface.create(str, 3);
        for (i = 0; i < quoteTextPaints.size(); i++) {
            updateFontEntry(quoteTextPaints.keyAt(i), (TextPaint) quoteTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < preformattedTextPaints.size(); i++) {
            updateFontEntry(preformattedTextPaints.keyAt(i), (TextPaint) preformattedTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < paragraphTextPaints.size(); i++) {
            updateFontEntry(paragraphTextPaints.keyAt(i), (TextPaint) paragraphTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < listTextPaints.size(); i++) {
            updateFontEntry(listTextPaints.keyAt(i), (TextPaint) listTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < embedPostTextPaints.size(); i++) {
            updateFontEntry(embedPostTextPaints.keyAt(i), (TextPaint) embedPostTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < mediaCaptionTextPaints.size(); i++) {
            updateFontEntry(mediaCaptionTextPaints.keyAt(i), (TextPaint) mediaCaptionTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < mediaCreditTextPaints.size(); i++) {
            updateFontEntry(mediaCreditTextPaints.keyAt(i), (TextPaint) mediaCreditTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < photoCaptionTextPaints.size(); i++) {
            updateFontEntry(photoCaptionTextPaints.keyAt(i), (TextPaint) photoCaptionTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < photoCreditTextPaints.size(); i++) {
            updateFontEntry(photoCreditTextPaints.keyAt(i), (TextPaint) photoCreditTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < authorTextPaints.size(); i++) {
            updateFontEntry(authorTextPaints.keyAt(i), (TextPaint) authorTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < footerTextPaints.size(); i++) {
            updateFontEntry(footerTextPaints.keyAt(i), (TextPaint) footerTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < embedPostCaptionTextPaints.size(); i++) {
            updateFontEntry(embedPostCaptionTextPaints.keyAt(i), (TextPaint) embedPostCaptionTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < relatedArticleTextPaints.size(); i++) {
            updateFontEntry(relatedArticleTextPaints.keyAt(i), (TextPaint) relatedArticleTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < detailsTextPaints.size(); i++) {
            updateFontEntry(detailsTextPaints.keyAt(i), (TextPaint) detailsTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        while (i2 < tableTextPaints.size()) {
            updateFontEntry(tableTextPaints.keyAt(i2), (TextPaint) tableTextPaints.valueAt(i2), typeface, typeface4, typeface3, typeface2);
            i2++;
        }
    }

    private void updateFontEntry(int i, TextPaint textPaint, Typeface typeface, Typeface typeface2, Typeface typeface3, Typeface typeface4) {
        int i2 = i & 1;
        if (i2 != 0 && (i & 2) != 0) {
            textPaint.setTypeface(typeface2);
        } else if (i2 != 0) {
            textPaint.setTypeface(typeface3);
        } else if ((i & 2) != 0) {
            textPaint.setTypeface(typeface4);
        } else if ((i & 4) == 0) {
            textPaint.setTypeface(typeface);
        }
    }

    private void updatePaintColors() {
        String str = "windowBackgroundWhite";
        this.backgroundPaint.setColor(Theme.getColor(str));
        int i = 0;
        while (true) {
            RecyclerListView[] recyclerListViewArr = this.listView;
            if (i >= recyclerListViewArr.length) {
                break;
            }
            recyclerListViewArr[i].setGlowColor(Theme.getColor(str));
            i++;
        }
        TextPaint textPaint = listTextPointerPaint;
        if (textPaint != null) {
            textPaint.setColor(getTextColor());
        }
        textPaint = listTextNumPaint;
        if (textPaint != null) {
            textPaint.setColor(getTextColor());
        }
        textPaint = embedPostAuthorPaint;
        if (textPaint != null) {
            textPaint.setColor(getTextColor());
        }
        textPaint = channelNamePaint;
        if (textPaint != null) {
            textPaint.setColor(getTextColor());
        }
        textPaint = channelNamePhotoPaint;
        if (textPaint != null) {
            textPaint.setColor(-1);
        }
        textPaint = relatedArticleHeaderPaint;
        if (textPaint != null) {
            textPaint.setColor(getTextColor());
        }
        textPaint = relatedArticleTextPaint;
        if (textPaint != null) {
            textPaint.setColor(getGrayTextColor());
        }
        textPaint = embedPostDatePaint;
        if (textPaint != null) {
            textPaint.setColor(getGrayTextColor());
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

    private void setMapColors(SparseArray<TextPaint> sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            TextPaint textPaint = (TextPaint) sparseArray.valueAt(i);
            if ((keyAt & 8) == 0 && (keyAt & 512) == 0) {
                textPaint.setColor(getTextColor());
            } else {
                textPaint.setColor(getLinkTextColor());
            }
        }
    }

    public void setParentActivity(Activity activity, BaseFragment baseFragment) {
        Activity activity2 = activity;
        this.parentFragment = baseFragment;
        this.currentAccount = UserConfig.selectedAccount;
        this.leftImage.setCurrentAccount(this.currentAccount);
        this.rightImage.setCurrentAccount(this.currentAccount);
        this.centerImage.setCurrentAccount(this.currentAccount);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        if (this.parentActivity == activity2) {
            updatePaintColors();
            refreshThemeColors();
            return;
        }
        this.parentActivity = activity2;
        this.selectedFont = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).getInt("font_type", 0);
        createPaint(false);
        this.backgroundPaint = new Paint();
        this.layerShadowDrawable = activity.getResources().getDrawable(NUM);
        this.slideDotDrawable = activity.getResources().getDrawable(NUM);
        this.slideDotBigDrawable = activity.getResources().getDrawable(NUM);
        this.scrimPaint = new Paint();
        this.windowView = new WindowView(activity2);
        this.windowView.setWillNotDraw(false);
        this.windowView.setClipChildren(true);
        this.windowView.setFocusable(false);
        this.containerView = new FrameLayout(activity2) {
            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:11:0x004d  */
            public boolean drawChild(android.graphics.Canvas r11, android.view.View r12, long r13) {
                /*
                r10 = this;
                r2 = org.telegram.ui.ArticleViewer.this;
                r2 = r2.windowView;
                r2 = r2.movingPage;
                if (r2 == 0) goto L_0x00df;
            L_0x000c:
                r2 = r10.getMeasuredWidth();
                r3 = org.telegram.ui.ArticleViewer.this;
                r3 = r3.listView;
                r4 = 0;
                r3 = r3[r4];
                r3 = r3.getTranslationX();
                r3 = (int) r3;
                r5 = org.telegram.ui.ArticleViewer.this;
                r5 = r5.listView;
                r6 = 1;
                r5 = r5[r6];
                if (r12 != r5) goto L_0x002b;
            L_0x0029:
                r7 = r3;
                goto L_0x0038;
            L_0x002b:
                r5 = org.telegram.ui.ArticleViewer.this;
                r5 = r5.listView;
                r5 = r5[r4];
                r7 = r2;
                if (r12 != r5) goto L_0x0038;
            L_0x0036:
                r5 = r3;
                goto L_0x0039;
            L_0x0038:
                r5 = 0;
            L_0x0039:
                r8 = r11.save();
                r9 = r10.getHeight();
                r11.clipRect(r5, r4, r7, r9);
                r9 = super.drawChild(r11, r12, r13);
                r11.restoreToCount(r8);
                if (r3 == 0) goto L_0x00de;
            L_0x004d:
                r8 = org.telegram.ui.ArticleViewer.this;
                r8 = r8.listView;
                r4 = r8[r4];
                r8 = 0;
                if (r12 != r4) goto L_0x00a1;
            L_0x0058:
                r2 = r2 - r3;
                r2 = (float) r2;
                r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r4 = (float) r4;
                r2 = r2 / r4;
                r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                r2 = java.lang.Math.min(r2, r4);
                r2 = java.lang.Math.max(r8, r2);
                r4 = org.telegram.ui.ArticleViewer.this;
                r4 = r4.layerShadowDrawable;
                r5 = org.telegram.ui.ArticleViewer.this;
                r5 = r5.layerShadowDrawable;
                r5 = r5.getIntrinsicWidth();
                r5 = r3 - r5;
                r6 = r12.getTop();
                r1 = r12.getBottom();
                r4.setBounds(r5, r6, r3, r1);
                r1 = org.telegram.ui.ArticleViewer.this;
                r1 = r1.layerShadowDrawable;
                r3 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
                r2 = r2 * r3;
                r2 = (int) r2;
                r1.setAlpha(r2);
                r1 = org.telegram.ui.ArticleViewer.this;
                r1 = r1.layerShadowDrawable;
                r1.draw(r11);
                goto L_0x00de;
            L_0x00a1:
                r4 = org.telegram.ui.ArticleViewer.this;
                r4 = r4.listView;
                r4 = r4[r6];
                if (r12 != r4) goto L_0x00de;
            L_0x00ab:
                r1 = NUM; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
                r3 = r2 - r3;
                r3 = (float) r3;
                r2 = (float) r2;
                r3 = r3 / r2;
                r1 = java.lang.Math.min(r1, r3);
                r2 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1));
                if (r2 >= 0) goto L_0x00bc;
            L_0x00bb:
                r1 = 0;
            L_0x00bc:
                r2 = org.telegram.ui.ArticleViewer.this;
                r2 = r2.scrimPaint;
                r3 = NUM; // 0x43190000 float:153.0 double:5.56175563E-315;
                r1 = r1 * r3;
                r1 = (int) r1;
                r1 = r1 << 24;
                r2.setColor(r1);
                r1 = (float) r5;
                r2 = 0;
                r3 = (float) r7;
                r4 = r10.getHeight();
                r4 = (float) r4;
                r5 = org.telegram.ui.ArticleViewer.this;
                r5 = r5.scrimPaint;
                r0 = r11;
                r0.drawRect(r1, r2, r3, r4, r5);
            L_0x00de:
                return r9;
            L_0x00df:
                r0 = super.drawChild(r11, r12, r13);
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$AnonymousClass7.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
            }
        };
        this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
        if (VERSION.SDK_INT >= 21) {
            this.windowView.setFitsSystemWindows(true);
            this.containerView.setOnApplyWindowInsetsListener(-$$Lambda$ArticleViewer$FZdCJFRpcBR9RAnJZatdXmTQ2fs.INSTANCE);
        }
        this.photoContainerBackground = new View(activity2);
        this.photoContainerBackground.setVisibility(4);
        this.photoContainerBackground.setBackgroundDrawable(this.photoBackgroundDrawable);
        this.windowView.addView(this.photoContainerBackground, LayoutHelper.createFrame(-1, -1, 51));
        this.animatingImageView = new ClippingImageView(activity2);
        this.animatingImageView.setAnimationValues(this.animationValues);
        this.animatingImageView.setVisibility(8);
        this.windowView.addView(this.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
        this.photoContainerView = new FrameLayoutDrawer(activity2) {
            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                i4 -= i2;
                int measuredHeight = i4 - ArticleViewer.this.captionTextView.getMeasuredHeight();
                i4 -= ArticleViewer.this.groupedPhotosListView.getMeasuredHeight();
                if (ArticleViewer.this.bottomLayout.getVisibility() == 0) {
                    measuredHeight -= ArticleViewer.this.bottomLayout.getMeasuredHeight();
                    i4 -= ArticleViewer.this.bottomLayout.getMeasuredHeight();
                }
                if (!ArticleViewer.this.groupedPhotosListView.currentPhotos.isEmpty()) {
                    measuredHeight -= ArticleViewer.this.groupedPhotosListView.getMeasuredHeight();
                }
                ArticleViewer.this.captionTextView.layout(0, measuredHeight, ArticleViewer.this.captionTextView.getMeasuredWidth(), ArticleViewer.this.captionTextView.getMeasuredHeight() + measuredHeight);
                ArticleViewer.this.captionTextViewNext.layout(0, measuredHeight, ArticleViewer.this.captionTextViewNext.getMeasuredWidth(), ArticleViewer.this.captionTextViewNext.getMeasuredHeight() + measuredHeight);
                ArticleViewer.this.groupedPhotosListView.layout(0, i4, ArticleViewer.this.groupedPhotosListView.getMeasuredWidth(), ArticleViewer.this.groupedPhotosListView.getMeasuredHeight() + i4);
            }
        };
        this.photoContainerView.setVisibility(4);
        this.photoContainerView.setWillNotDraw(false);
        this.windowView.addView(this.photoContainerView, LayoutHelper.createFrame(-1, -1, 51));
        this.fullscreenVideoContainer = new FrameLayout(activity2);
        this.fullscreenVideoContainer.setBackgroundColor(-16777216);
        this.fullscreenVideoContainer.setVisibility(4);
        this.windowView.addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.fullscreenAspectRatioView = new AspectRatioFrameLayout(activity2);
        this.fullscreenAspectRatioView.setVisibility(8);
        this.fullscreenVideoContainer.addView(this.fullscreenAspectRatioView, LayoutHelper.createFrame(-1, -1, 17));
        this.fullscreenTextureView = new TextureView(activity2);
        this.listView = new RecyclerListView[2];
        this.adapter = new WebpageAdapter[2];
        this.layoutManager = new LinearLayoutManager[2];
        int i = 0;
        while (true) {
            RecyclerListView[] recyclerListViewArr = this.listView;
            if (i >= recyclerListViewArr.length) {
                break;
            }
            recyclerListViewArr[i] = new RecyclerListView(activity2) {
                /* Access modifiers changed, original: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    int childCount = getChildCount();
                    for (i2 = 0; i2 < childCount; i2++) {
                        View childAt = getChildAt(i2);
                        if ((childAt.getTag() instanceof Integer) && ((Integer) childAt.getTag()).intValue() == 90 && childAt.getBottom() < getMeasuredHeight()) {
                            childCount = getMeasuredHeight();
                            childAt.layout(0, childCount - childAt.getMeasuredHeight(), childAt.getMeasuredWidth(), childCount);
                            return;
                        }
                    }
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLink == null && ((ArticleViewer.this.popupWindow == null || !ArticleViewer.this.popupWindow.isShowing()) && (motionEvent.getAction() == 1 || motionEvent.getAction() == 3))) {
                        ArticleViewer.this.pressedLink = null;
                        ArticleViewer.this.pressedLinkOwnerLayout = null;
                        ArticleViewer.this.pressedLinkOwnerView = null;
                    } else if (!(ArticleViewer.this.pressedLinkOwnerLayout == null || ArticleViewer.this.pressedLink == null || motionEvent.getAction() != 1)) {
                        ArticleViewer articleViewer = ArticleViewer.this;
                        articleViewer.checkLayoutForLinks(motionEvent, articleViewer.pressedLinkOwnerView, ArticleViewer.this.pressedLinkOwnerLayout, 0, 0);
                    }
                    return super.onInterceptTouchEvent(motionEvent);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLink == null && ((ArticleViewer.this.popupWindow == null || !ArticleViewer.this.popupWindow.isShowing()) && (motionEvent.getAction() == 1 || motionEvent.getAction() == 3))) {
                        ArticleViewer.this.pressedLink = null;
                        ArticleViewer.this.pressedLinkOwnerLayout = null;
                        ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                    return super.onTouchEvent(motionEvent);
                }

                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    if (ArticleViewer.this.windowView.movingPage) {
                        ArticleViewer.this.containerView.invalidate();
                        f /= (float) getMeasuredWidth();
                        ArticleViewer articleViewer = ArticleViewer.this;
                        articleViewer.setCurrentHeaderHeight((int) (((float) articleViewer.windowView.startMovingHeaderHeight) + (((float) (AndroidUtilities.dp(56.0f) - ArticleViewer.this.windowView.startMovingHeaderHeight)) * f)));
                    }
                }
            };
            ((DefaultItemAnimator) this.listView[i].getItemAnimator()).setDelayAnimations(false);
            RecyclerView recyclerView = this.listView[i];
            LinearLayoutManager[] linearLayoutManagerArr = this.layoutManager;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.parentActivity, 1, false);
            linearLayoutManagerArr[i] = linearLayoutManager;
            recyclerView.setLayoutManager(linearLayoutManager);
            WebpageAdapter[] webpageAdapterArr = this.adapter;
            WebpageAdapter webpageAdapter = new WebpageAdapter(this.parentActivity);
            webpageAdapterArr[i] = webpageAdapter;
            this.listView[i].setAdapter(webpageAdapter);
            this.listView[i].setClipToPadding(false);
            this.listView[i].setVisibility(i == 0 ? 0 : 8);
            this.listView[i].setPadding(0, AndroidUtilities.dp(56.0f), 0, 0);
            this.listView[i].setTopGlowOffset(AndroidUtilities.dp(56.0f));
            this.containerView.addView(this.listView[i], LayoutHelper.createFrame(-1, -1.0f));
            this.listView[i].setOnItemLongClickListener(new -$$Lambda$ArticleViewer$gN3ysqedASIoOc6B-30_HuSHg6g(this));
            this.listView[i].setOnItemClickListener(new -$$Lambda$ArticleViewer$D7cgjgLrXEd0Ps-6XAby7DZGrbY(this, webpageAdapter));
            this.listView[i].setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i == 0) {
                        ArticleViewer.this.textSelectionHelper.stopScrolling();
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    if (recyclerView.getChildCount() != 0) {
                        ArticleViewer.this.textSelectionHelper.onParentScrolled();
                        ArticleViewer.this.headerView.invalidate();
                        ArticleViewer.this.checkScroll(i2);
                    }
                }
            });
            i++;
        }
        this.headerPaint.setColor(-16777216);
        this.statusBarPaint.setColor(-16777216);
        this.headerProgressPaint.setColor(-14408666);
        this.headerView = new FrameLayout(activity2) {
            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                float measuredWidth = (float) getMeasuredWidth();
                float measuredHeight = (float) getMeasuredHeight();
                canvas.drawRect(0.0f, 0.0f, measuredWidth, measuredHeight, ArticleViewer.this.headerPaint);
                if (ArticleViewer.this.layoutManager != null) {
                    View findViewByPosition;
                    int findFirstVisibleItemPosition = ArticleViewer.this.layoutManager[0].findFirstVisibleItemPosition();
                    int findLastVisibleItemPosition = ArticleViewer.this.layoutManager[0].findLastVisibleItemPosition();
                    int itemCount = ArticleViewer.this.layoutManager[0].getItemCount();
                    int i = itemCount - 2;
                    if (findLastVisibleItemPosition >= i) {
                        findViewByPosition = ArticleViewer.this.layoutManager[0].findViewByPosition(i);
                    } else {
                        findViewByPosition = ArticleViewer.this.layoutManager[0].findViewByPosition(findFirstVisibleItemPosition);
                    }
                    if (findViewByPosition != null) {
                        float measuredHeight2;
                        measuredWidth /= (float) (itemCount - 1);
                        ArticleViewer.this.layoutManager[0].getChildCount();
                        float measuredHeight3 = (float) findViewByPosition.getMeasuredHeight();
                        if (findLastVisibleItemPosition >= i) {
                            measuredHeight2 = ((((float) (i - findFirstVisibleItemPosition)) * measuredWidth) * ((float) (ArticleViewer.this.listView[0].getMeasuredHeight() - findViewByPosition.getTop()))) / measuredHeight3;
                        } else {
                            measuredHeight2 = (1.0f - ((((float) Math.min(0, findViewByPosition.getTop() - ArticleViewer.this.listView[0].getPaddingTop())) + measuredHeight3) / measuredHeight3)) * measuredWidth;
                        }
                        canvas.drawRect(0.0f, 0.0f, (((float) findFirstVisibleItemPosition) * measuredWidth) + measuredHeight2, measuredHeight, ArticleViewer.this.headerProgressPaint);
                    }
                }
            }
        };
        this.headerView.setWillNotDraw(false);
        this.containerView.addView(this.headerView, LayoutHelper.createFrame(-1, 56.0f));
        this.headerView.setOnClickListener(new -$$Lambda$ArticleViewer$6j9jyXVY-ffoLEP_jak-OCrDKe8(this));
        this.titleTextView = new SimpleTextView(activity2);
        this.titleTextView.setGravity(19);
        this.titleTextView.setTextSize(20);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setTextColor(-5000269);
        this.titleTextView.setPivotX(0.0f);
        this.titleTextView.setPivotY((float) AndroidUtilities.dp(28.0f));
        this.headerView.addView(this.titleTextView, LayoutHelper.createFrame(-1, 56.0f, 51, 72.0f, 0.0f, 96.0f, 0.0f));
        this.lineProgressView = new LineProgressView(activity2);
        this.lineProgressView.setProgressColor(-1);
        this.lineProgressView.setPivotX(0.0f);
        this.lineProgressView.setPivotY((float) AndroidUtilities.dp(2.0f));
        this.headerView.addView(this.lineProgressView, LayoutHelper.createFrame(-1, 2.0f, 83, 0.0f, 0.0f, 0.0f, 1.0f));
        this.lineProgressTickRunnable = new -$$Lambda$ArticleViewer$jHqjSwEP5uCWcqeTC4XpAgqfGWg(this);
        this.menuContainer = new FrameLayout(activity2);
        this.headerView.addView(this.menuContainer, LayoutHelper.createFrame(48, 56, 53));
        this.searchShadow = new View(activity2);
        this.searchShadow.setBackgroundResource(NUM);
        this.searchShadow.setAlpha(0.0f);
        this.containerView.addView(this.searchShadow, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 56.0f, 0.0f, 0.0f));
        this.searchContainer = new FrameLayout(this.parentActivity);
        this.searchContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.searchContainer.setVisibility(4);
        if (VERSION.SDK_INT < 21) {
            this.searchContainer.setAlpha(0.0f);
        }
        this.headerView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 56.0f));
        this.searchField = new EditTextBoldCursor(this.parentActivity) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.searchField.setCursorWidth(1.5f);
        String str = "windowBackgroundWhiteBlackText";
        this.searchField.setTextColor(Theme.getColor(str));
        this.searchField.setCursorColor(Theme.getColor(str));
        this.searchField.setTextSize(1, 18.0f);
        this.searchField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.searchField.setSingleLine(true);
        this.searchField.setHint(LocaleController.getString("Search", NUM));
        this.searchField.setBackgroundResource(0);
        this.searchField.setPadding(0, 0, 0, 0);
        this.searchField.setInputType(this.searchField.getInputType() | 524288);
        if (VERSION.SDK_INT < 23) {
            this.searchField.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    return false;
                }

                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode actionMode) {
                }

                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }
            });
        }
        this.searchField.setOnEditorActionListener(new -$$Lambda$ArticleViewer$8zB_D_KTS2A3fmuMNDyRkHeqC2U(this));
        this.searchField.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (ArticleViewer.this.ignoreOnTextChange) {
                    ArticleViewer.this.ignoreOnTextChange = false;
                    return;
                }
                ArticleViewer.this.processSearch(charSequence.toString().toLowerCase());
                if (ArticleViewer.this.clearButton != null) {
                    if (TextUtils.isEmpty(charSequence)) {
                        if (ArticleViewer.this.clearButton.getTag() != null) {
                            ArticleViewer.this.clearButton.setTag(null);
                            ArticleViewer.this.clearButton.clearAnimation();
                            if (ArticleViewer.this.animateClear) {
                                ArticleViewer.this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(0.0f).setDuration(180).scaleY(0.0f).scaleX(0.0f).rotation(45.0f).withEndAction(new -$$Lambda$ArticleViewer$14$uv1bLccQQi3qagMhYNvvRqSCKEI(this)).start();
                            } else {
                                ArticleViewer.this.clearButton.setAlpha(0.0f);
                                ArticleViewer.this.clearButton.setRotation(45.0f);
                                ArticleViewer.this.clearButton.setScaleX(0.0f);
                                ArticleViewer.this.clearButton.setScaleY(0.0f);
                                ArticleViewer.this.clearButton.setVisibility(4);
                                ArticleViewer.this.animateClear = true;
                            }
                        }
                    } else if (ArticleViewer.this.clearButton.getTag() == null) {
                        ArticleViewer.this.clearButton.setTag(Integer.valueOf(1));
                        ArticleViewer.this.clearButton.clearAnimation();
                        ArticleViewer.this.clearButton.setVisibility(0);
                        if (ArticleViewer.this.animateClear) {
                            ArticleViewer.this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(1.0f).setDuration(180).scaleY(1.0f).scaleX(1.0f).rotation(0.0f).start();
                        } else {
                            ArticleViewer.this.clearButton.setAlpha(1.0f);
                            ArticleViewer.this.clearButton.setRotation(0.0f);
                            ArticleViewer.this.clearButton.setScaleX(1.0f);
                            ArticleViewer.this.clearButton.setScaleY(1.0f);
                            ArticleViewer.this.animateClear = true;
                        }
                    }
                }
            }

            public /* synthetic */ void lambda$onTextChanged$0$ArticleViewer$14() {
                ArticleViewer.this.clearButton.setVisibility(4);
            }
        });
        this.searchField.setImeOptions(33554435);
        this.searchField.setTextIsSelectable(false);
        this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1, 36.0f, 16, 72.0f, 0.0f, 48.0f, 0.0f));
        this.clearButton = new ImageView(this.parentActivity) {
            /* Access modifiers changed, original: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                clearAnimation();
                if (getTag() == null) {
                    ArticleViewer.this.clearButton.setVisibility(4);
                    ArticleViewer.this.clearButton.setAlpha(0.0f);
                    ArticleViewer.this.clearButton.setRotation(45.0f);
                    ArticleViewer.this.clearButton.setScaleX(0.0f);
                    ArticleViewer.this.clearButton.setScaleY(0.0f);
                    return;
                }
                ArticleViewer.this.clearButton.setAlpha(1.0f);
                ArticleViewer.this.clearButton.setRotation(0.0f);
                ArticleViewer.this.clearButton.setScaleX(1.0f);
                ArticleViewer.this.clearButton.setScaleY(1.0f);
            }
        };
        this.clearButton.setImageDrawable(new CloseProgressDrawable2());
        this.clearButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
        this.clearButton.setScaleType(ScaleType.CENTER);
        this.clearButton.setAlpha(0.0f);
        this.clearButton.setRotation(45.0f);
        this.clearButton.setScaleX(0.0f);
        this.clearButton.setScaleY(0.0f);
        this.clearButton.setOnClickListener(new -$$Lambda$ArticleViewer$R_6rwlwqps7RWpLQ78R9g2TJAag(this));
        this.clearButton.setContentDescription(LocaleController.getString("ClearButton", NUM));
        this.searchContainer.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
        this.backButton = new ImageView(activity2);
        this.backButton.setScaleType(ScaleType.CENTER);
        this.backDrawable = new BackDrawable(false);
        this.backDrawable.setAnimationTime(200.0f);
        this.backDrawable.setColor(Theme.getColor(str));
        this.backDrawable.setRotatedColor(-5000269);
        this.backDrawable.setRotation(1.0f, false);
        this.backButton.setImageDrawable(this.backDrawable);
        this.backButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.headerView.addView(this.backButton, LayoutHelper.createFrame(54, 56.0f));
        this.backButton.setOnClickListener(new -$$Lambda$ArticleViewer$H1Bc9M26tuZkWwbtuUTUyFFEPMA(this));
        this.backButton.setContentDescription(LocaleController.getString("AccDescrGoBack", NUM));
        ActionBarMenuItem actionBarMenuItem = r0;
        ActionBarMenuItem anonymousClass16 = new ActionBarMenuItem(this.parentActivity, null, NUM, -5000269) {
            public void toggleSubMenu() {
                super.toggleSubMenu();
                ArticleViewer.this.listView[0].stopScroll();
                ArticleViewer.this.checkScrollAnimated();
            }
        };
        this.menuButton = actionBarMenuItem;
        this.menuButton.setLayoutInScreen(true);
        this.menuButton.setDuplicateParentStateEnabled(false);
        this.menuButton.setClickable(true);
        this.menuButton.setIcon(NUM);
        this.menuButton.addSubItem(1, NUM, LocaleController.getString("Search", NUM));
        this.menuButton.addSubItem(2, NUM, LocaleController.getString("ShareFile", NUM));
        this.menuButton.addSubItem(3, NUM, LocaleController.getString("OpenInExternalApp", NUM));
        this.menuButton.addSubItem(4, NUM, LocaleController.getString("Settings", NUM));
        this.menuButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.menuButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.menuContainer.addView(this.menuButton, LayoutHelper.createFrame(48, 56.0f));
        this.progressView = new ContextProgressView(activity2, 2);
        this.progressView.setVisibility(8);
        this.menuContainer.addView(this.progressView, LayoutHelper.createFrame(48, 56.0f));
        this.menuButton.setDelegate(new -$$Lambda$ArticleViewer$XaN769qBL3svXCRBy8tevHun1Jo(this));
        this.searchPanel = new FrameLayout(this.parentActivity) {
            public void onDraw(Canvas canvas) {
                int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.searchPanel.setOnTouchListener(-$$Lambda$ArticleViewer$YB3m-73S2qh9VHADZc3-WdtltD8.INSTANCE);
        this.searchPanel.setWillNotDraw(false);
        this.searchPanel.setVisibility(4);
        this.searchPanel.setFocusable(true);
        this.searchPanel.setFocusableInTouchMode(true);
        this.searchPanel.setClickable(true);
        this.searchPanel.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.containerView.addView(this.searchPanel, LayoutHelper.createFrame(-1, 51, 80));
        this.searchUpButton = new ImageView(this.parentActivity);
        this.searchUpButton.setScaleType(ScaleType.CENTER);
        this.searchUpButton.setImageResource(NUM);
        this.searchUpButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
        this.searchUpButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("actionBarActionModeDefaultSelector"), 1));
        this.searchPanel.addView(this.searchUpButton, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, 0.0f, 48.0f, 0.0f));
        this.searchUpButton.setOnClickListener(new -$$Lambda$ArticleViewer$lPU_BVNRUajP3efspuXk-ru9iJ0(this));
        this.searchUpButton.setContentDescription(LocaleController.getString("AccDescrSearchNext", NUM));
        this.searchDownButton = new ImageView(this.parentActivity);
        this.searchDownButton.setScaleType(ScaleType.CENTER);
        this.searchDownButton.setImageResource(NUM);
        this.searchDownButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
        this.searchDownButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("actionBarActionModeDefaultSelector"), 1));
        this.searchPanel.addView(this.searchDownButton, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, 0.0f, 0.0f, 0.0f));
        this.searchDownButton.setOnClickListener(new -$$Lambda$ArticleViewer$tR4YiOcFGZsbGlxlf2Ssk6GWR0s(this));
        this.searchDownButton.setContentDescription(LocaleController.getString("AccDescrSearchPrev", NUM));
        this.searchCountText = new SimpleTextView(this.parentActivity);
        this.searchCountText.setTextColor(Theme.getColor(str));
        this.searchCountText.setTextSize(15);
        this.searchCountText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.searchCountText.setGravity(3);
        this.searchPanel.addView(this.searchCountText, LayoutHelper.createFrame(-2, -2.0f, 19, 18.0f, 0.0f, 108.0f, 0.0f));
        this.windowLayoutParams = new LayoutParams();
        LayoutParams layoutParams = this.windowLayoutParams;
        layoutParams.height = -1;
        layoutParams.format = -3;
        layoutParams.width = -1;
        layoutParams.gravity = 51;
        layoutParams.type = 99;
        layoutParams.softInputMode = 16;
        layoutParams.flags = 131072;
        int i2 = VERSION.SDK_INT;
        if (i2 >= 21) {
            layoutParams.flags |= -NUM;
            if (i2 >= 28) {
                layoutParams.layoutInDisplayCutoutMode = 1;
            }
        }
        if (progressDrawables == null) {
            progressDrawables = new Drawable[4];
            progressDrawables[0] = this.parentActivity.getResources().getDrawable(NUM);
            progressDrawables[1] = this.parentActivity.getResources().getDrawable(NUM);
            progressDrawables[2] = this.parentActivity.getResources().getDrawable(NUM);
            progressDrawables[3] = this.parentActivity.getResources().getDrawable(NUM);
        }
        this.scroller = new Scroller(activity2);
        this.blackPaint.setColor(-16777216);
        this.actionBar = new ActionBar(activity2);
        this.actionBar.setBackgroundColor(NUM);
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsBackgroundColor(NUM, false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setTitle(LocaleController.formatString("Of", NUM, Integer.valueOf(1), Integer.valueOf(1)));
        this.photoContainerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                ArticleViewer articleViewer;
                if (i == -1) {
                    ArticleViewer.this.closePhoto(true);
                } else if (i == 1) {
                    if (VERSION.SDK_INT >= 23) {
                        if (ArticleViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                            ArticleViewer.this.parentActivity.requestPermissions(new String[]{r3}, 4);
                            return;
                        }
                    }
                    articleViewer = ArticleViewer.this;
                    File access$10900 = articleViewer.getMediaFile(articleViewer.currentIndex);
                    if (access$10900 == null || !access$10900.exists()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ArticleViewer.this.parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                        builder.setMessage(LocaleController.getString("PleaseDownload", NUM));
                        ArticleViewer.this.showDialog(builder.create());
                    } else {
                        String file = access$10900.toString();
                        Activity access$2200 = ArticleViewer.this.parentActivity;
                        ArticleViewer articleViewer2 = ArticleViewer.this;
                        MediaController.saveFile(file, access$2200, articleViewer2.isMediaVideo(articleViewer2.currentIndex), null, null);
                    }
                } else if (i == 2) {
                    ArticleViewer.this.onSharePressed();
                } else if (i == 3) {
                    try {
                        AndroidUtilities.openForView(ArticleViewer.this.getMedia(ArticleViewer.this.currentIndex), ArticleViewer.this.parentActivity);
                        ArticleViewer.this.closePhoto(false);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (i == 4) {
                    articleViewer = ArticleViewer.this;
                    TLObject access$11200 = articleViewer.getMedia(articleViewer.currentIndex);
                    if (access$11200 instanceof Document) {
                        Document document = (Document) access$11200;
                        MediaDataController.getInstance(ArticleViewer.this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
                        MessagesController.getInstance(ArticleViewer.this.currentAccount).saveGif(ArticleViewer.this.currentPage, document);
                    }
                }
            }

            public boolean canOpenMenu() {
                ArticleViewer articleViewer = ArticleViewer.this;
                File access$10900 = articleViewer.getMediaFile(articleViewer.currentIndex);
                return access$10900 != null && access$10900.exists();
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.addItem(2, NUM);
        this.menuItem = createMenu.addItem(0, NUM);
        this.menuItem.setLayoutInScreen(true);
        this.menuItem.addSubItem(3, NUM, LocaleController.getString("OpenInExternalApp", NUM)).setColors(-328966, -328966);
        this.menuItem.addSubItem(1, NUM, LocaleController.getString("SaveToGallery", NUM)).setColors(-328966, -328966);
        this.menuItem.addSubItem(4, NUM, LocaleController.getString("SaveToGIFs", NUM)).setColors(-328966, -328966);
        this.menuItem.redrawPopup(-NUM);
        this.bottomLayout = new FrameLayout(this.parentActivity);
        this.bottomLayout.setBackgroundColor(NUM);
        this.photoContainerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        this.groupedPhotosListView = new GroupedPhotosListView(this.parentActivity);
        this.photoContainerView.addView(this.groupedPhotosListView, LayoutHelper.createFrame(-1, 62.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
        this.groupedPhotosListView.setDelegate(new GroupedPhotosListViewDelegate() {
            public int getAvatarsDialogId() {
                return 0;
            }

            public ArrayList<MessageObject> getImagesArr() {
                return null;
            }

            public ArrayList<ImageLocation> getImagesArrLocations() {
                return null;
            }

            public int getSlideshowMessageId() {
                return 0;
            }

            public int getCurrentIndex() {
                return ArticleViewer.this.currentIndex;
            }

            public int getCurrentAccount() {
                return ArticleViewer.this.currentAccount;
            }

            public ArrayList<PageBlock> getPageBlockArr() {
                return ArticleViewer.this.imagesArr;
            }

            public Object getParentObject() {
                return ArticleViewer.this.currentPage;
            }

            public void setCurrentIndex(int i) {
                ArticleViewer.this.currentIndex = -1;
                if (ArticleViewer.this.currentThumb != null) {
                    ArticleViewer.this.currentThumb.release();
                    ArticleViewer.this.currentThumb = null;
                }
                ArticleViewer.this.setImageIndex(i, true);
            }
        });
        this.captionTextViewNext = new TextView(activity2);
        this.captionTextViewNext.setMaxLines(10);
        this.captionTextViewNext.setBackgroundColor(NUM);
        this.captionTextViewNext.setMovementMethod(new LinkMovementMethodMy(this, null));
        this.captionTextViewNext.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
        this.captionTextViewNext.setLinkTextColor(-1);
        this.captionTextViewNext.setTextColor(-1);
        this.captionTextViewNext.setHighlightColor(NUM);
        this.captionTextViewNext.setGravity(19);
        this.captionTextViewNext.setTextSize(1, 16.0f);
        this.captionTextViewNext.setVisibility(8);
        this.photoContainerView.addView(this.captionTextViewNext, LayoutHelper.createFrame(-1, -2, 83));
        this.captionTextView = new TextView(activity2);
        this.captionTextView.setMaxLines(10);
        this.captionTextView.setBackgroundColor(NUM);
        this.captionTextView.setMovementMethod(new LinkMovementMethodMy(this, null));
        this.captionTextView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
        this.captionTextView.setLinkTextColor(-1);
        this.captionTextView.setTextColor(-1);
        this.captionTextView.setHighlightColor(NUM);
        this.captionTextView.setGravity(19);
        this.captionTextView.setTextSize(1, 16.0f);
        this.captionTextView.setVisibility(8);
        this.photoContainerView.addView(this.captionTextView, LayoutHelper.createFrame(-1, -2, 83));
        this.radialProgressViews[0] = new RadialProgressView(activity2, this.photoContainerView);
        this.radialProgressViews[0].setBackgroundState(0, false);
        this.radialProgressViews[1] = new RadialProgressView(activity2, this.photoContainerView);
        this.radialProgressViews[1].setBackgroundState(0, false);
        this.radialProgressViews[2] = new RadialProgressView(activity2, this.photoContainerView);
        this.radialProgressViews[2].setBackgroundState(0, false);
        this.videoPlayerSeekbar = new SeekBar(activity2);
        this.videoPlayerSeekbar.setColors(NUM, NUM, -2764585, -1, -1);
        this.videoPlayerSeekbar.setDelegate(new -$$Lambda$ArticleViewer$r2lkfi3DVCgGL1Psph_wyl_RwRo(this));
        this.videoPlayerControlFrameLayout = new FrameLayout(activity2) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                motionEvent.getX();
                motionEvent.getY();
                if (!ArticleViewer.this.videoPlayerSeekbar.onTouch(motionEvent.getAction(), motionEvent.getX() - ((float) AndroidUtilities.dp(48.0f)), motionEvent.getY())) {
                    return super.onTouchEvent(motionEvent);
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
                return true;
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                long j = 0;
                if (ArticleViewer.this.videoPlayer != null) {
                    long duration = ArticleViewer.this.videoPlayer.getDuration();
                    if (duration != -9223372036854775807L) {
                        j = duration;
                    }
                }
                i2 = (int) (j / 1000);
                ArticleViewer.this.videoPlayerSeekbar.setSize((getMeasuredWidth() - AndroidUtilities.dp(64.0f)) - ((int) Math.ceil((double) ArticleViewer.this.videoPlayerTime.getPaint().measureText(AndroidUtilities.formatLongDuration(i2, i2)))), getMeasuredHeight());
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                ArticleViewer.this.videoPlayerSeekbar.setProgress(ArticleViewer.this.videoPlayer != null ? ((float) ArticleViewer.this.videoPlayer.getCurrentPosition()) / ((float) ArticleViewer.this.videoPlayer.getDuration()) : 0.0f);
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(48.0f), 0.0f);
                ArticleViewer.this.videoPlayerSeekbar.draw(canvas);
                canvas.restore();
            }
        };
        this.videoPlayerControlFrameLayout.setWillNotDraw(false);
        this.bottomLayout.addView(this.videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
        this.videoPlayButton = new ImageView(activity2);
        this.videoPlayButton.setScaleType(ScaleType.CENTER);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayButton, LayoutHelper.createFrame(48, 48, 51));
        this.videoPlayButton.setOnClickListener(new -$$Lambda$ArticleViewer$Lqnz6Q-7rkkuFWmDumcpUSIg4Eg(this));
        this.videoPlayerTime = new TextView(activity2);
        this.videoPlayerTime.setTextColor(-1);
        this.videoPlayerTime.setGravity(16);
        this.videoPlayerTime.setTextSize(1, 13.0f);
        this.videoPlayerControlFrameLayout.addView(this.videoPlayerTime, LayoutHelper.createFrame(-2, -1.0f, 53, 0.0f, 0.0f, 8.0f, 0.0f));
        this.gestureDetector = new GestureDetector(activity2, this);
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
        this.textSelectionHelper = new ArticleTextSelectionHelper();
        this.textSelectionHelper.setParentView(this.listView[0]);
        ArticleTextSelectionHelper articleTextSelectionHelper = this.textSelectionHelper;
        articleTextSelectionHelper.layoutManager = this.layoutManager[0];
        articleTextSelectionHelper.setCallback(new TextSelectionHelper.Callback() {
            public void onTextCopied() {
            }

            public void onStateChanged(boolean z) {
                if (z) {
                    ArticleViewer.this.showSearch(false);
                }
            }
        });
        this.containerView.addView(this.textSelectionHelper.getOverlayView(activity2));
        updatePaintColors();
    }

    public /* synthetic */ boolean lambda$setParentActivity$8$ArticleViewer(View view, int i) {
        if (!(view instanceof BlockRelatedArticlesCell)) {
            return false;
        }
        BlockRelatedArticlesCell blockRelatedArticlesCell = (BlockRelatedArticlesCell) view;
        showCopyPopup(((TL_pageRelatedArticle) blockRelatedArticlesCell.currentBlock.parent.articles.get(blockRelatedArticlesCell.currentBlock.num)).url);
        return true;
    }

    public /* synthetic */ void lambda$setParentActivity$11$ArticleViewer(WebpageAdapter webpageAdapter, View view, int i) {
        ArticleTextSelectionHelper articleTextSelectionHelper = this.textSelectionHelper;
        if (articleTextSelectionHelper != null) {
            if (articleTextSelectionHelper.isSelectionMode()) {
                this.textSelectionHelper.clear();
                return;
            }
            this.textSelectionHelper.clear();
        }
        if (i != webpageAdapter.localBlocks.size() || this.currentPage == null) {
            if (i >= 0 && i < webpageAdapter.localBlocks.size()) {
                PageBlock pageBlock = (PageBlock) webpageAdapter.localBlocks.get(i);
                PageBlock lastNonListPageBlock = getLastNonListPageBlock(pageBlock);
                if (lastNonListPageBlock instanceof TL_pageBlockDetailsChild) {
                    lastNonListPageBlock = ((TL_pageBlockDetailsChild) lastNonListPageBlock).block;
                }
                if (lastNonListPageBlock instanceof TL_pageBlockChannel) {
                    MessagesController.getInstance(this.currentAccount).openByUserName(((TL_pageBlockChannel) lastNonListPageBlock).channel.username, this.parentFragment, 2);
                    close(false, true);
                } else if (lastNonListPageBlock instanceof TL_pageBlockRelatedArticlesChild) {
                    TL_pageBlockRelatedArticlesChild tL_pageBlockRelatedArticlesChild = (TL_pageBlockRelatedArticlesChild) lastNonListPageBlock;
                    openWebpageUrl(((TL_pageRelatedArticle) tL_pageBlockRelatedArticlesChild.parent.articles.get(tL_pageBlockRelatedArticlesChild.num)).url, null);
                } else if (lastNonListPageBlock instanceof TL_pageBlockDetails) {
                    view = getLastNonListCell(view);
                    if (view instanceof BlockDetailsCell) {
                        this.pressedLinkOwnerLayout = null;
                        this.pressedLinkOwnerView = null;
                        if (webpageAdapter.blocks.indexOf(pageBlock) >= 0) {
                            TL_pageBlockDetails tL_pageBlockDetails = (TL_pageBlockDetails) lastNonListPageBlock;
                            tL_pageBlockDetails.open ^= 1;
                            int itemCount = webpageAdapter.getItemCount();
                            webpageAdapter.updateRows();
                            itemCount = Math.abs(webpageAdapter.getItemCount() - itemCount);
                            BlockDetailsCell blockDetailsCell = (BlockDetailsCell) view;
                            blockDetailsCell.arrow.setAnimationProgressAnimated(tL_pageBlockDetails.open ? 0.0f : 1.0f);
                            blockDetailsCell.invalidate();
                            if (itemCount != 0) {
                                if (tL_pageBlockDetails.open) {
                                    webpageAdapter.notifyItemRangeInserted(i + 1, itemCount);
                                } else {
                                    webpageAdapter.notifyItemRangeRemoved(i + 1, itemCount);
                                }
                            }
                        }
                    }
                }
            }
        } else if (this.previewsReqId == 0) {
            String str = "previews";
            TLObject userOrChat = MessagesController.getInstance(this.currentAccount).getUserOrChat(str);
            if (userOrChat instanceof TL_user) {
                openPreviewsChat((User) userOrChat, this.currentPage.id);
            } else {
                int i2 = UserConfig.selectedAccount;
                long j = this.currentPage.id;
                showProgressView(true, true);
                TL_contacts_resolveUsername tL_contacts_resolveUsername = new TL_contacts_resolveUsername();
                tL_contacts_resolveUsername.username = str;
                this.previewsReqId = ConnectionsManager.getInstance(i2).sendRequest(tL_contacts_resolveUsername, new -$$Lambda$ArticleViewer$ggiuvz6vZ37WaSls81dRSwQOWdc(this, i2, j));
            }
        }
    }

    public /* synthetic */ void lambda$null$10$ArticleViewer(int i, long j, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$RMBAO5TCJgoWk-O4MzZOP5_N2n4(this, tLObject, i, j));
    }

    public /* synthetic */ void lambda$null$9$ArticleViewer(TLObject tLObject, int i, long j) {
        if (this.previewsReqId != 0) {
            this.previewsReqId = 0;
            showProgressView(true, false);
            if (tLObject != null) {
                TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) tLObject;
                MessagesController.getInstance(i).putUsers(tL_contacts_resolvedPeer.users, false);
                MessagesStorage.getInstance(i).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, false, true);
                if (!tL_contacts_resolvedPeer.users.isEmpty()) {
                    openPreviewsChat((User) tL_contacts_resolvedPeer.users.get(0), j);
                }
            }
        }
    }

    public /* synthetic */ void lambda$setParentActivity$12$ArticleViewer(View view) {
        this.listView[0].smoothScrollToPosition(0);
    }

    public /* synthetic */ void lambda$setParentActivity$13$ArticleViewer() {
        float currentProgress = 0.7f - this.lineProgressView.getCurrentProgress();
        if (currentProgress > 0.0f) {
            float f = currentProgress < 0.25f ? 0.01f : 0.02f;
            LineProgressView lineProgressView = this.lineProgressView;
            lineProgressView.setProgress(lineProgressView.getCurrentProgress() + f, true);
            AndroidUtilities.runOnUIThread(this.lineProgressTickRunnable, 100);
        }
    }

    public /* synthetic */ boolean lambda$setParentActivity$14$ArticleViewer(TextView textView, int i, KeyEvent keyEvent) {
        if (keyEvent != null && ((keyEvent.getAction() == 1 && keyEvent.getKeyCode() == 84) || (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 66))) {
            AndroidUtilities.hideKeyboard(this.searchField);
        }
        return false;
    }

    public /* synthetic */ void lambda$setParentActivity$15$ArticleViewer(View view) {
        if (this.searchField.length() != 0) {
            this.searchField.setText("");
        }
        this.searchField.requestFocus();
        AndroidUtilities.showKeyboard(this.searchField);
    }

    public /* synthetic */ void lambda$setParentActivity$16$ArticleViewer(View view) {
        if (this.searchContainer.getTag() != null) {
            showSearch(false);
        } else {
            close(true, true);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$18$ArticleViewer(int i) {
        WebPage webPage = this.currentPage;
        if (webPage != null) {
            Activity activity = this.parentActivity;
            if (activity != null) {
                if (i == 1) {
                    showSearch(true);
                } else if (i == 2) {
                    String str = webPage.url;
                    showDialog(new ShareAlert(activity, null, str, false, str, false));
                } else if (i == 3) {
                    String str2;
                    if (TextUtils.isEmpty(webPage.cached_page.url)) {
                        str2 = this.currentPage.url;
                    } else {
                        str2 = this.currentPage.cached_page.url;
                    }
                    Browser.openUrl(this.parentActivity, str2, true, false);
                } else if (i == 4) {
                    Builder builder = new Builder(activity);
                    builder.setApplyTopPadding(false);
                    LinearLayout linearLayout = new LinearLayout(this.parentActivity);
                    linearLayout.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
                    linearLayout.setOrientation(1);
                    HeaderCell headerCell = new HeaderCell(this.parentActivity);
                    headerCell.setText(LocaleController.getString("FontSize", NUM));
                    linearLayout.addView(headerCell, LayoutHelper.createLinear(-2, -2, 51, 3, 1, 3, 0));
                    linearLayout.addView(new TextSizeCell(this.parentActivity), LayoutHelper.createLinear(-1, -2, 51, 3, 0, 3, 0));
                    headerCell = new HeaderCell(this.parentActivity);
                    headerCell.setText(LocaleController.getString("FontType", NUM));
                    linearLayout.addView(headerCell, LayoutHelper.createLinear(-2, -2, 51, 3, 4, 3, 2));
                    int i2 = 0;
                    while (i2 < 2) {
                        this.fontCells[i2] = new FontCell(this.parentActivity);
                        if (i2 == 0) {
                            this.fontCells[i2].setTextAndTypeface(LocaleController.getString("Default", NUM), Typeface.DEFAULT);
                        } else if (i2 == 1) {
                            this.fontCells[i2].setTextAndTypeface("Serif", Typeface.SERIF);
                        }
                        this.fontCells[i2].select(i2 == this.selectedFont, false);
                        this.fontCells[i2].setTag(Integer.valueOf(i2));
                        this.fontCells[i2].setOnClickListener(new -$$Lambda$ArticleViewer$H_m9xR4pJel0HUauXvFuuwhQPTE(this));
                        linearLayout.addView(this.fontCells[i2], LayoutHelper.createLinear(-1, 50));
                        i2++;
                    }
                    TextView textView = new TextView(this.parentActivity);
                    textView.setTextColor(-14606047);
                    textView.setTextSize(1, 16.0f);
                    builder.setCustomView(linearLayout);
                    BottomSheet create = builder.create();
                    this.linkSheet = create;
                    showDialog(create);
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$17$ArticleViewer(View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        this.selectedFont = intValue;
        int i = 0;
        while (i < 2) {
            this.fontCells[i].select(i == intValue, true);
            i++;
        }
        updatePaintFonts();
        for (int i2 = 0; i2 < this.listView.length; i2++) {
            this.adapter[i2].notifyDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$20$ArticleViewer(View view) {
        scrollToSearchIndex(this.currentSearchIndex - 1);
    }

    public /* synthetic */ void lambda$setParentActivity$21$ArticleViewer(View view) {
        scrollToSearchIndex(this.currentSearchIndex + 1);
    }

    public /* synthetic */ void lambda$setParentActivity$22$ArticleViewer(float f) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.seekTo((long) ((int) (f * ((float) videoPlayer.getDuration()))));
        }
    }

    public /* synthetic */ void lambda$setParentActivity$23$ArticleViewer(View view) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return;
        }
        if (this.isPlaying) {
            videoPlayer.pause();
        } else {
            videoPlayer.play();
        }
    }

    private void showSearch(final boolean z) {
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null) {
            if ((frameLayout.getTag() != null) != z) {
                this.searchContainer.setTag(z ? Integer.valueOf(1) : null);
                this.searchResults.clear();
                this.searchText = null;
                this.adapter[0].searchTextOffset.clear();
                this.currentSearchIndex = 0;
                float f = 1.0f;
                if (this.attachedToWindow) {
                    Property property;
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.setDuration(250);
                    if (z) {
                        this.searchContainer.setVisibility(0);
                        this.backDrawable.setRotation(0.0f, true);
                    } else {
                        this.menuButton.setVisibility(0);
                        this.listView[0].invalidateViews();
                        AndroidUtilities.hideKeyboard(this.searchField);
                        updateWindowLayoutParamsForSearch();
                    }
                    ArrayList arrayList = new ArrayList();
                    if (VERSION.SDK_INT >= 21) {
                        if (z) {
                            this.searchContainer.setAlpha(1.0f);
                        }
                        int left = this.menuContainer.getLeft() + (this.menuContainer.getMeasuredWidth() / 2);
                        int top = this.menuContainer.getTop() + (this.menuContainer.getMeasuredHeight() / 2);
                        float sqrt = (float) Math.sqrt((double) ((left * left) + (top * top)));
                        FrameLayout frameLayout2 = this.searchContainer;
                        float f2 = z ? 0.0f : sqrt;
                        if (!z) {
                            sqrt = 0.0f;
                        }
                        Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(frameLayout2, left, top, f2, sqrt);
                        arrayList.add(createCircularReveal);
                        createCircularReveal.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (!z) {
                                    ArticleViewer.this.searchContainer.setAlpha(0.0f);
                                }
                            }
                        });
                    } else {
                        FrameLayout frameLayout3 = this.searchContainer;
                        property = View.ALPHA;
                        float[] fArr = new float[1];
                        fArr[0] = z ? 1.0f : 0.0f;
                        arrayList.add(ObjectAnimator.ofFloat(frameLayout3, property, fArr));
                    }
                    if (!z) {
                        arrayList.add(ObjectAnimator.ofFloat(this.searchPanel, View.ALPHA, new float[]{0.0f}));
                    }
                    View view = this.searchShadow;
                    property = View.ALPHA;
                    float[] fArr2 = new float[1];
                    if (!z) {
                        f = 0.0f;
                    }
                    fArr2[0] = f;
                    arrayList.add(ObjectAnimator.ofFloat(view, property, fArr2));
                    animatorSet.playTogether(arrayList);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (z) {
                                ArticleViewer.this.updateWindowLayoutParamsForSearch();
                                ArticleViewer.this.searchField.requestFocus();
                                AndroidUtilities.showKeyboard(ArticleViewer.this.searchField);
                                ArticleViewer.this.menuButton.setVisibility(4);
                                return;
                            }
                            ArticleViewer.this.searchContainer.setVisibility(4);
                            ArticleViewer.this.searchPanel.setVisibility(4);
                            ArticleViewer.this.searchField.setText("");
                        }

                        public void onAnimationStart(Animator animator) {
                            if (!z) {
                                ArticleViewer.this.backDrawable.setRotation(1.0f, true);
                            }
                        }
                    });
                    animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    if (z || AndroidUtilities.usingHardwareInput || !this.keyboardVisible) {
                        animatorSet.start();
                        return;
                    }
                    this.runAfterKeyboardClose = animatorSet;
                    AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$_Imh5yyHOf6cbO1993bJYVTtKOI(this), 300);
                    return;
                }
                this.searchContainer.setAlpha(z ? 1.0f : 0.0f);
                this.menuButton.setVisibility(z ? 4 : 0);
                this.backDrawable.setRotation(z ? 0.0f : 1.0f, false);
                View view2 = this.searchShadow;
                if (!z) {
                    f = 0.0f;
                }
                view2.setAlpha(f);
                if (z) {
                    this.searchContainer.setVisibility(0);
                } else {
                    this.searchContainer.setVisibility(4);
                    this.searchPanel.setVisibility(4);
                    this.searchField.setText("");
                }
                updateWindowLayoutParamsForSearch();
            }
        }
    }

    public /* synthetic */ void lambda$showSearch$24$ArticleViewer() {
        AnimatorSet animatorSet = this.runAfterKeyboardClose;
        if (animatorSet != null) {
            animatorSet.start();
            this.runAfterKeyboardClose = null;
        }
    }

    private void updateSearchButtons() {
        ArrayList arrayList = this.searchResults;
        if (arrayList != null) {
            ImageView imageView = this.searchUpButton;
            boolean z = (arrayList.isEmpty() || this.currentSearchIndex == 0) ? false : true;
            imageView.setEnabled(z);
            ImageView imageView2 = this.searchDownButton;
            boolean z2 = (this.searchResults.isEmpty() || this.currentSearchIndex == this.searchResults.size() - 1) ? false : true;
            imageView2.setEnabled(z2);
            imageView2 = this.searchUpButton;
            float f = 1.0f;
            imageView2.setAlpha(imageView2.isEnabled() ? 1.0f : 0.5f);
            imageView2 = this.searchDownButton;
            if (!imageView2.isEnabled()) {
                f = 0.5f;
            }
            imageView2.setAlpha(f);
            int size = this.searchResults.size();
            if (size < 0) {
                this.searchCountText.setText("");
            } else if (size == 0) {
                this.searchCountText.setText(LocaleController.getString("NoResult", NUM));
            } else if (size == 1) {
                this.searchCountText.setText(LocaleController.getString("OneResult", NUM));
            } else {
                this.searchCountText.setText(String.format(LocaleController.getPluralString("CountOfResults", size), new Object[]{Integer.valueOf(this.currentSearchIndex + 1), Integer.valueOf(size)}));
            }
        }
    }

    private void processSearch(String str) {
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.searchRunnable = null;
        }
        if (TextUtils.isEmpty(str)) {
            this.searchResults.clear();
            this.searchText = str;
            this.adapter[0].searchTextOffset.clear();
            this.searchPanel.setVisibility(4);
            this.listView[0].invalidateViews();
            scrollToSearchIndex(0);
            this.lastSearchIndex = -1;
            return;
        }
        int i = this.lastSearchIndex + 1;
        this.lastSearchIndex = i;
        -$$Lambda$ArticleViewer$I6bzDXDrl0XfMiA5cIv3THs_RgU -__lambda_articleviewer_i6bzdxdrl0xfmia5civ3ths_rgu = new -$$Lambda$ArticleViewer$I6bzDXDrl0XfMiA5cIv3THs_RgU(this, str, i);
        this.searchRunnable = -__lambda_articleviewer_i6bzdxdrl0xfmia5civ3ths_rgu;
        AndroidUtilities.runOnUIThread(-__lambda_articleviewer_i6bzdxdrl0xfmia5civ3ths_rgu, 400);
    }

    public /* synthetic */ void lambda$processSearch$27$ArticleViewer(String str, int i) {
        HashMap hashMap = new HashMap(this.adapter[0].textToBlocks);
        ArrayList arrayList = new ArrayList(this.adapter[0].textBlocks);
        this.searchRunnable = null;
        Utilities.searchQueue.postRunnable(new -$$Lambda$ArticleViewer$SVxLl1HAI90UeX51z7YgECphIyY(this, arrayList, hashMap, str, i));
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0082 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0082 A:{SYNTHETIC} */
    public /* synthetic */ void lambda$null$26$ArticleViewer(java.util.ArrayList r18, java.util.HashMap r19, java.lang.String r20, int r21) {
        /*
        r17 = this;
        r6 = r17;
        r7 = r20;
        r8 = new java.util.ArrayList;
        r8.<init>();
        r9 = r18.size();
        r11 = 0;
    L_0x000e:
        if (r11 >= r9) goto L_0x0085;
    L_0x0010:
        r12 = r18;
        r13 = r12.get(r11);
        r14 = r19;
        r0 = r14.get(r13);
        r15 = r0;
        r15 = (org.telegram.tgnet.TLRPC.PageBlock) r15;
        r0 = r13 instanceof org.telegram.tgnet.TLRPC.RichText;
        r5 = 0;
        if (r0 == 0) goto L_0x0045;
    L_0x0024:
        r3 = r13;
        r3 = (org.telegram.tgnet.TLRPC.RichText) r3;
        r1 = 0;
        r16 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = r17;
        r2 = r3;
        r4 = r15;
        r10 = r5;
        r5 = r16;
        r0 = r0.getText(r1, r2, r3, r4, r5);
        r1 = android.text.TextUtils.isEmpty(r0);
        if (r1 != 0) goto L_0x0052;
    L_0x003b:
        r0 = r0.toString();
        r0 = r0.toLowerCase();
        r5 = r0;
        goto L_0x0053;
    L_0x0045:
        r10 = r5;
        r0 = r13 instanceof java.lang.String;
        if (r0 == 0) goto L_0x0052;
    L_0x004a:
        r0 = r13;
        r0 = (java.lang.String) r0;
        r5 = r0.toLowerCase();
        goto L_0x0053;
    L_0x0052:
        r5 = r10;
    L_0x0053:
        if (r5 == 0) goto L_0x0082;
    L_0x0055:
        r0 = 0;
    L_0x0056:
        r0 = r5.indexOf(r7, r0);
        if (r0 < 0) goto L_0x0082;
    L_0x005c:
        r1 = r20.length();
        r1 = r1 + r0;
        if (r0 == 0) goto L_0x006f;
    L_0x0063:
        r2 = r0 + -1;
        r2 = r5.charAt(r2);
        r2 = org.telegram.messenger.AndroidUtilities.isPunctuationCharacter(r2);
        if (r2 == 0) goto L_0x0080;
    L_0x006f:
        r2 = new org.telegram.ui.ArticleViewer$SearchResult;
        r2.<init>(r6, r10);
        r2.index = r0;
        r2.block = r15;
        r2.text = r13;
        r8.add(r2);
    L_0x0080:
        r0 = r1;
        goto L_0x0056;
    L_0x0082:
        r11 = r11 + 1;
        goto L_0x000e;
    L_0x0085:
        r0 = new org.telegram.ui.-$$Lambda$ArticleViewer$Wmpxm-0NLWx6nLUPLI8sqcP0eeY;
        r1 = r21;
        r0.<init>(r6, r1, r8, r7);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.lambda$null$26$ArticleViewer(java.util.ArrayList, java.util.HashMap, java.lang.String, int):void");
    }

    public /* synthetic */ void lambda$null$25$ArticleViewer(int i, ArrayList arrayList, String str) {
        if (i == this.lastSearchIndex) {
            this.searchPanel.setAlpha(1.0f);
            this.searchPanel.setVisibility(0);
            this.searchResults = arrayList;
            this.searchText = str;
            this.adapter[0].searchTextOffset.clear();
            this.listView[0].invalidateViews();
            scrollToSearchIndex(0);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00b1 A:{RETURN} */
    private void scrollToSearchIndex(int r12) {
        /*
        r11 = this;
        if (r12 < 0) goto L_0x0172;
    L_0x0002:
        r0 = r11.searchResults;
        r0 = r0.size();
        if (r12 < r0) goto L_0x000c;
    L_0x000a:
        goto L_0x0172;
    L_0x000c:
        r11.currentSearchIndex = r12;
        r11.updateSearchButtons();
        r0 = r11.searchResults;
        r12 = r0.get(r12);
        r12 = (org.telegram.ui.ArticleViewer.SearchResult) r12;
        r0 = r12.block;
        r0 = r11.getLastNonListPageBlock(r0);
        r1 = r11.adapter;
        r2 = 0;
        r1 = r1[r2];
        r1 = r1.blocks;
        r1 = r1.size();
        r3 = 0;
    L_0x002f:
        if (r3 >= r1) goto L_0x006d;
    L_0x0031:
        r4 = r11.adapter;
        r4 = r4[r2];
        r4 = r4.blocks;
        r4 = r4.get(r3);
        r4 = (org.telegram.tgnet.TLRPC.PageBlock) r4;
        r5 = r4 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild;
        if (r5 == 0) goto L_0x006a;
    L_0x0043:
        r4 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r4;
        r5 = r4.block;
        r6 = r12.block;
        if (r5 == r6) goto L_0x0055;
    L_0x004f:
        r5 = r4.block;
        if (r5 != r0) goto L_0x006a;
    L_0x0055:
        r1 = r11.openAllParentBlocks(r4);
        if (r1 == 0) goto L_0x006d;
    L_0x005b:
        r1 = r11.adapter;
        r1 = r1[r2];
        r1.updateRows();
        r1 = r11.adapter;
        r1 = r1[r2];
        r1.notifyDataSetChanged();
        goto L_0x006d;
    L_0x006a:
        r3 = r3 + 1;
        goto L_0x002f;
    L_0x006d:
        r1 = r11.adapter;
        r1 = r1[r2];
        r1 = r1.localBlocks;
        r1 = r1.size();
        r3 = 0;
    L_0x007a:
        r4 = -1;
        if (r3 >= r1) goto L_0x00ae;
    L_0x007d:
        r5 = r11.adapter;
        r5 = r5[r2];
        r5 = r5.localBlocks;
        r5 = r5.get(r3);
        r5 = (org.telegram.tgnet.TLRPC.PageBlock) r5;
        r6 = r12.block;
        if (r5 == r6) goto L_0x00af;
    L_0x0091:
        if (r5 != r0) goto L_0x0094;
    L_0x0093:
        goto L_0x00af;
    L_0x0094:
        r6 = r5 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild;
        if (r6 == 0) goto L_0x00ab;
    L_0x0098:
        r5 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r5;
        r6 = r5.block;
        r7 = r12.block;
        if (r6 == r7) goto L_0x00af;
    L_0x00a4:
        r5 = r5.block;
        if (r5 != r0) goto L_0x00ab;
    L_0x00aa:
        goto L_0x00af;
    L_0x00ab:
        r3 = r3 + 1;
        goto L_0x007a;
    L_0x00ae:
        r3 = -1;
    L_0x00af:
        if (r3 != r4) goto L_0x00b2;
    L_0x00b1:
        return;
    L_0x00b2:
        r1 = r0 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild;
        if (r1 == 0) goto L_0x00cc;
    L_0x00b6:
        r0 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r0;
        r0 = r11.openAllParentBlocks(r0);
        if (r0 == 0) goto L_0x00cc;
    L_0x00be:
        r0 = r11.adapter;
        r0 = r0[r2];
        r0.updateRows();
        r0 = r11.adapter;
        r0 = r0[r2];
        r0.notifyDataSetChanged();
    L_0x00cc:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r11.searchText;
        r0.append(r1);
        r1 = r12.block;
        r0.append(r1);
        r1 = r12.text;
        r0.append(r1);
        r1 = r12.index;
        r0.append(r1);
        r0 = r0.toString();
        r1 = r11.adapter;
        r1 = r1[r2];
        r1 = r1.searchTextOffset;
        r1 = r1.get(r0);
        r1 = (java.lang.Integer) r1;
        if (r1 != 0) goto L_0x014e;
    L_0x00ff:
        r1 = r11.adapter;
        r1 = r1[r2];
        r4 = r12.block;
        r6 = r1.getTypeForBlock(r4);
        r1 = r11.adapter;
        r1 = r1[r2];
        r4 = 0;
        r1 = r1.onCreateViewHolder(r4, r6);
        r4 = r11.adapter;
        r5 = r4[r2];
        r8 = r12.block;
        r9 = 0;
        r10 = 0;
        r7 = r1;
        r5.bindBlockToHolder(r6, r7, r8, r9, r10);
        r12 = r1.itemView;
        r1 = r11.listView;
        r1 = r1[r2];
        r1 = r1.getMeasuredWidth();
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r4);
        r4 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r2);
        r12.measure(r1, r4);
        r12 = r11.adapter;
        r12 = r12[r2];
        r12 = r12.searchTextOffset;
        r12 = r12.get(r0);
        r1 = r12;
        r1 = (java.lang.Integer) r1;
        if (r1 != 0) goto L_0x014e;
    L_0x014a:
        r1 = java.lang.Integer.valueOf(r2);
    L_0x014e:
        r12 = r11.layoutManager;
        r12 = r12[r2];
        r0 = r11.currentHeaderHeight;
        r4 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r0 - r4;
        r1 = r1.intValue();
        r0 = r0 - r1;
        r1 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0 = r0 + r1;
        r12.scrollToPositionWithOffset(r3, r0);
        r12 = r11.listView;
        r12 = r12[r2];
        r12.invalidateViews();
        return;
    L_0x0172:
        r11.updateSearchButtons();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.scrollToSearchIndex(int):void");
    }

    private void checkScrollAnimated() {
        if (this.currentHeaderHeight != AndroidUtilities.dp(56.0f)) {
            ValueAnimator duration = ValueAnimator.ofObject(new IntEvaluator(), new Object[]{Integer.valueOf(this.currentHeaderHeight), Integer.valueOf(AndroidUtilities.dp(56.0f))}).setDuration(180);
            duration.setInterpolator(new DecelerateInterpolator());
            duration.addUpdateListener(new -$$Lambda$ArticleViewer$WMv_VOdqIZjIHez5VOYiBR-PhLU(this));
            duration.start();
        }
    }

    public /* synthetic */ void lambda$checkScrollAnimated$28$ArticleViewer(ValueAnimator valueAnimator) {
        setCurrentHeaderHeight(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    private void setCurrentHeaderHeight(int i) {
        if (this.searchContainer.getTag() == null) {
            int dp = AndroidUtilities.dp(56.0f);
            int max = Math.max(AndroidUtilities.statusBarHeight, AndroidUtilities.dp(24.0f));
            if (i < max) {
                i = max;
            } else if (i > dp) {
                i = dp;
            }
            float f = (float) (dp - max);
            this.currentHeaderHeight = i;
            int i2 = this.currentHeaderHeight;
            float f2 = ((((float) (i2 - max)) / f) * 0.2f) + 0.8f;
            float f3 = ((((float) (i2 - max)) / f) * 0.5f) + 0.5f;
            this.backButton.setScaleX(f2);
            this.backButton.setScaleY(f2);
            this.backButton.setTranslationY((float) ((dp - this.currentHeaderHeight) / 2));
            this.menuContainer.setScaleX(f2);
            this.menuContainer.setScaleY(f2);
            this.titleTextView.setScaleX(f2);
            this.titleTextView.setScaleY(f2);
            this.lineProgressView.setScaleY(f3);
            this.menuContainer.setTranslationY((float) ((dp - this.currentHeaderHeight) / 2));
            this.titleTextView.setTranslationY((float) ((dp - this.currentHeaderHeight) / 2));
            this.headerView.setTranslationY((float) (this.currentHeaderHeight - dp));
            this.searchShadow.setTranslationY((float) (this.currentHeaderHeight - dp));
            this.menuButton.setAdditionalYOffset((-(this.currentHeaderHeight - dp)) / 2);
            this.textSelectionHelper.setTopOffset(this.currentHeaderHeight);
            i = 0;
            while (true) {
                RecyclerListView[] recyclerListViewArr = this.listView;
                if (i < recyclerListViewArr.length) {
                    recyclerListViewArr[i].setTopGlowOffset(this.currentHeaderHeight);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    private void checkScroll(int i) {
        setCurrentHeaderHeight(this.currentHeaderHeight - i);
    }

    private void openPreviewsChat(User user, long j) {
        if (user != null && this.parentActivity != null) {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", user.id);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("webpage");
            stringBuilder.append(j);
            bundle.putString("botUser", stringBuilder.toString());
            ((LaunchActivity) this.parentActivity).presentFragment(new ChatActivity(bundle), false, true);
            close(false, true);
        }
    }

    public boolean open(MessageObject messageObject) {
        return open(messageObject, null, null, true);
    }

    public boolean open(TL_webPage tL_webPage, String str) {
        return open(null, tL_webPage, str, true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:42:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00f6  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0182  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0143  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0213  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x015a A:{Catch:{ Exception -> 0x017d }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:62:0x0154 */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Can't wrap try/catch for region: R(6:58|(2:60|61)|62|63|(2:65|(1:67))|68) */
    /* JADX WARNING: Missing block: B:69:0x017d, code skipped:
            r12 = move-exception;
     */
    /* JADX WARNING: Missing block: B:70:0x017e, code skipped:
            org.telegram.messenger.FileLog.e(r12);
     */
    /* JADX WARNING: Missing block: B:71:0x0181, code skipped:
            return false;
     */
    private boolean open(org.telegram.messenger.MessageObject r12, org.telegram.tgnet.TLRPC.WebPage r13, java.lang.String r14, boolean r15) {
        /*
        r11 = this;
        r0 = r11.parentActivity;
        r1 = 0;
        if (r0 == 0) goto L_0x0219;
    L_0x0005:
        r0 = r11.isVisible;
        if (r0 == 0) goto L_0x000d;
    L_0x0009:
        r0 = r11.collapsed;
        if (r0 == 0) goto L_0x0219;
    L_0x000d:
        if (r12 != 0) goto L_0x0013;
    L_0x000f:
        if (r13 != 0) goto L_0x0013;
    L_0x0011:
        goto L_0x0219;
    L_0x0013:
        if (r12 == 0) goto L_0x001b;
    L_0x0015:
        r13 = r12.messageOwner;
        r13 = r13.media;
        r13 = r13.webpage;
    L_0x001b:
        r0 = -1;
        r2 = 35;
        r3 = 0;
        r4 = 1;
        if (r12 == 0) goto L_0x008f;
    L_0x0022:
        r13 = r12.messageOwner;
        r13 = r13.media;
        r13 = r13.webpage;
        r14 = 0;
    L_0x0029:
        r5 = r12.messageOwner;
        r5 = r5.entities;
        r5 = r5.size();
        if (r14 >= r5) goto L_0x008d;
    L_0x0033:
        r5 = r12.messageOwner;
        r5 = r5.entities;
        r5 = r5.get(r14);
        r5 = (org.telegram.tgnet.TLRPC.MessageEntity) r5;
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
        if (r6 == 0) goto L_0x008a;
    L_0x0041:
        r6 = r12.messageOwner;	 Catch:{ Exception -> 0x0086 }
        r6 = r6.message;	 Catch:{ Exception -> 0x0086 }
        r7 = r5.offset;	 Catch:{ Exception -> 0x0086 }
        r8 = r5.offset;	 Catch:{ Exception -> 0x0086 }
        r5 = r5.length;	 Catch:{ Exception -> 0x0086 }
        r8 = r8 + r5;
        r5 = r6.substring(r7, r8);	 Catch:{ Exception -> 0x0086 }
        r5 = r5.toLowerCase();	 Catch:{ Exception -> 0x0086 }
        r6 = r13.cached_page;	 Catch:{ Exception -> 0x0086 }
        r6 = r6.url;	 Catch:{ Exception -> 0x0086 }
        r6 = android.text.TextUtils.isEmpty(r6);	 Catch:{ Exception -> 0x0086 }
        if (r6 != 0) goto L_0x0067;
    L_0x005e:
        r6 = r13.cached_page;	 Catch:{ Exception -> 0x0086 }
        r6 = r6.url;	 Catch:{ Exception -> 0x0086 }
        r6 = r6.toLowerCase();	 Catch:{ Exception -> 0x0086 }
        goto L_0x006d;
    L_0x0067:
        r6 = r13.url;	 Catch:{ Exception -> 0x0086 }
        r6 = r6.toLowerCase();	 Catch:{ Exception -> 0x0086 }
    L_0x006d:
        r7 = r5.contains(r6);	 Catch:{ Exception -> 0x0086 }
        if (r7 != 0) goto L_0x0079;
    L_0x0073:
        r6 = r6.contains(r5);	 Catch:{ Exception -> 0x0086 }
        if (r6 == 0) goto L_0x008a;
    L_0x0079:
        r6 = r5.lastIndexOf(r2);	 Catch:{ Exception -> 0x0086 }
        if (r6 == r0) goto L_0x008d;
    L_0x007f:
        r6 = r6 + 1;
        r14 = r5.substring(r6);	 Catch:{ Exception -> 0x0086 }
        goto L_0x009c;
    L_0x0086:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);
    L_0x008a:
        r14 = r14 + 1;
        goto L_0x0029;
    L_0x008d:
        r14 = r3;
        goto L_0x009c;
    L_0x008f:
        if (r14 == 0) goto L_0x009e;
    L_0x0091:
        r2 = r14.lastIndexOf(r2);
        if (r2 == r0) goto L_0x009e;
    L_0x0097:
        r2 = r2 + r4;
        r14 = r14.substring(r2);
    L_0x009c:
        r7 = r13;
        goto L_0x00a0;
    L_0x009e:
        r7 = r13;
        r14 = r3;
    L_0x00a0:
        r13 = r11.pagesStack;
        r13.clear();
        r11.collapsed = r1;
        r13 = r11.containerView;
        r0 = 0;
        r13.setTranslationX(r0);
        r13 = r11.containerView;
        r13.setTranslationY(r0);
        r13 = r11.listView;
        r13 = r13[r1];
        r13.setTranslationY(r0);
        r13 = r11.listView;
        r13 = r13[r1];
        r13.setTranslationX(r0);
        r13 = r11.listView;
        r13 = r13[r4];
        r13.setTranslationX(r0);
        r13 = r11.listView;
        r13 = r13[r1];
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13.setAlpha(r2);
        r13 = r11.windowView;
        r13.setInnerTranslationX(r0);
        r13 = r11.actionBar;
        r2 = 8;
        r13.setVisibility(r2);
        r13 = r11.bottomLayout;
        r13.setVisibility(r2);
        r13 = r11.captionTextView;
        r13.setVisibility(r2);
        r13 = r11.captionTextViewNext;
        r13.setVisibility(r2);
        r13 = r11.layoutManager;
        r13 = r13[r1];
        r13.scrollToPositionWithOffset(r1, r1);
        r13 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        if (r15 == 0) goto L_0x00fe;
    L_0x00f6:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r11.setCurrentHeaderHeight(r2);
        goto L_0x0101;
    L_0x00fe:
        r11.checkScrollAnimated();
    L_0x0101:
        r2 = r11.addPageToStack(r7, r14, r1);
        if (r15 == 0) goto L_0x013a;
    L_0x0107:
        if (r2 != 0) goto L_0x010d;
    L_0x0109:
        if (r14 == 0) goto L_0x010d;
    L_0x010b:
        r9 = r14;
        goto L_0x010e;
    L_0x010d:
        r9 = r3;
    L_0x010e:
        r14 = new org.telegram.tgnet.TLRPC$TL_messages_getWebPage;
        r14.<init>();
        r15 = r7.url;
        r14.url = r15;
        r15 = r7.cached_page;
        r2 = r15 instanceof org.telegram.tgnet.TLRPC.TL_pagePart_layer82;
        if (r2 != 0) goto L_0x0127;
    L_0x011d:
        r15 = r15.part;
        if (r15 == 0) goto L_0x0122;
    L_0x0121:
        goto L_0x0127;
    L_0x0122:
        r15 = r7.hash;
        r14.hash = r15;
        goto L_0x0129;
    L_0x0127:
        r14.hash = r1;
    L_0x0129:
        r10 = org.telegram.messenger.UserConfig.selectedAccount;
        r15 = org.telegram.tgnet.ConnectionsManager.getInstance(r10);
        r2 = new org.telegram.ui.-$$Lambda$ArticleViewer$1g_6CgvnuqTpor0e0vfaVxR6-U4;
        r5 = r2;
        r6 = r11;
        r8 = r12;
        r5.<init>(r6, r7, r8, r9, r10);
        r15.sendRequest(r14, r2);
    L_0x013a:
        r11.lastInsets = r3;
        r12 = r11.isVisible;
        r14 = "window";
        if (r12 != 0) goto L_0x0182;
    L_0x0143:
        r12 = r11.parentActivity;
        r12 = r12.getSystemService(r14);
        r12 = (android.view.WindowManager) r12;
        r14 = r11.attachedToWindow;
        if (r14 == 0) goto L_0x0154;
    L_0x014f:
        r14 = r11.windowView;	 Catch:{ Exception -> 0x0154 }
        r12.removeView(r14);	 Catch:{ Exception -> 0x0154 }
    L_0x0154:
        r14 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x017d }
        r15 = 21;
        if (r14 < r15) goto L_0x016b;
    L_0x015a:
        r14 = r11.windowLayoutParams;	 Catch:{ Exception -> 0x017d }
        r15 = -NUM; // 0xfffffffvar_ float:-9.2194E-41 double:NaN;
        r14.flags = r15;	 Catch:{ Exception -> 0x017d }
        r14 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x017d }
        r15 = 28;
        if (r14 < r15) goto L_0x016b;
    L_0x0167:
        r14 = r11.windowLayoutParams;	 Catch:{ Exception -> 0x017d }
        r14.layoutInDisplayCutoutMode = r4;	 Catch:{ Exception -> 0x017d }
    L_0x016b:
        r14 = r11.windowView;	 Catch:{ Exception -> 0x017d }
        r14.setFocusable(r1);	 Catch:{ Exception -> 0x017d }
        r14 = r11.containerView;	 Catch:{ Exception -> 0x017d }
        r14.setFocusable(r1);	 Catch:{ Exception -> 0x017d }
        r14 = r11.windowView;	 Catch:{ Exception -> 0x017d }
        r15 = r11.windowLayoutParams;	 Catch:{ Exception -> 0x017d }
        r12.addView(r14, r15);	 Catch:{ Exception -> 0x017d }
        goto L_0x0199;
    L_0x017d:
        r12 = move-exception;
        org.telegram.messenger.FileLog.e(r12);
        return r1;
    L_0x0182:
        r12 = r11.windowLayoutParams;
        r15 = r12.flags;
        r15 = r15 & -17;
        r12.flags = r15;
        r12 = r11.parentActivity;
        r12 = r12.getSystemService(r14);
        r12 = (android.view.WindowManager) r12;
        r14 = r11.windowView;
        r15 = r11.windowLayoutParams;
        r12.updateViewLayout(r14, r15);
    L_0x0199:
        r11.isVisible = r4;
        r11.animationInProgress = r4;
        r12 = r11.windowView;
        r12.setAlpha(r0);
        r12 = r11.containerView;
        r12.setAlpha(r0);
        r12 = new android.animation.AnimatorSet;
        r12.<init>();
        r14 = 3;
        r14 = new android.animation.Animator[r14];
        r15 = r11.windowView;
        r2 = android.view.View.ALPHA;
        r5 = 2;
        r6 = new float[r5];
        r6 = {0, NUM};
        r15 = android.animation.ObjectAnimator.ofFloat(r15, r2, r6);
        r14[r1] = r15;
        r15 = r11.containerView;
        r2 = android.view.View.ALPHA;
        r6 = new float[r5];
        r6 = {0, NUM};
        r15 = android.animation.ObjectAnimator.ofFloat(r15, r2, r6);
        r14[r4] = r15;
        r15 = r11.windowView;
        r2 = android.view.View.TRANSLATION_X;
        r6 = new float[r5];
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = (float) r13;
        r6[r1] = r13;
        r6[r4] = r0;
        r13 = android.animation.ObjectAnimator.ofFloat(r15, r2, r6);
        r14[r5] = r13;
        r12.playTogether(r14);
        r13 = new org.telegram.ui.-$$Lambda$ArticleViewer$ulRZ_0KWQ9qZjxYQUrE1JagxKhI;
        r13.<init>(r11);
        r11.animationEndRunnable = r13;
        r13 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r12.setDuration(r13);
        r13 = r11.interpolator;
        r12.setInterpolator(r13);
        r13 = new org.telegram.ui.ArticleViewer$24;
        r13.<init>();
        r12.addListener(r13);
        r13 = java.lang.System.currentTimeMillis();
        r11.transitionAnimationStartTime = r13;
        r13 = new org.telegram.ui.-$$Lambda$ArticleViewer$9oVjSwuxGSyFuHG_RiDKjC0tihA;
        r13.<init>(r11, r12);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r13);
        r12 = android.os.Build.VERSION.SDK_INT;
        r13 = 18;
        if (r12 < r13) goto L_0x0218;
    L_0x0213:
        r12 = r11.containerView;
        r12.setLayerType(r5, r3);
    L_0x0218:
        return r4;
    L_0x0219:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.open(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, java.lang.String, boolean):boolean");
    }

    public /* synthetic */ void lambda$open$30$ArticleViewer(WebPage webPage, MessageObject messageObject, String str, int i, TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_webPage) {
            TL_webPage tL_webPage = (TL_webPage) tLObject;
            if (tL_webPage.cached_page != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$Q6MXW72qxuy86kcTGbIKq6Qqrf0(this, webPage, tL_webPage, messageObject, str));
                LongSparseArray longSparseArray = new LongSparseArray(1);
                longSparseArray.put(tL_webPage.id, tL_webPage);
                MessagesStorage.getInstance(i).putWebPages(longSparseArray);
            }
        }
    }

    public /* synthetic */ void lambda$null$29$ArticleViewer(WebPage webPage, TL_webPage tL_webPage, MessageObject messageObject, String str) {
        if (!this.pagesStack.isEmpty() && this.pagesStack.get(0) == webPage && tL_webPage.cached_page != null) {
            if (messageObject != null) {
                messageObject.messageOwner.media.webpage = tL_webPage;
            }
            this.pagesStack.set(0, tL_webPage);
            if (this.pagesStack.size() == 1) {
                this.currentPage = tL_webPage;
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("article");
                stringBuilder.append(this.currentPage.id);
                edit.remove(stringBuilder.toString()).commit();
                updateInterfaceForCurrentPage(false, 0);
                if (str != null) {
                    scrollToAnchor(str);
                }
            }
        }
    }

    public /* synthetic */ void lambda$open$31$ArticleViewer() {
        FrameLayout frameLayout = this.containerView;
        if (frameLayout != null && this.windowView != null) {
            if (VERSION.SDK_INT >= 18) {
                frameLayout.setLayerType(0, null);
            }
            this.animationInProgress = 0;
            AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
        }
    }

    public /* synthetic */ void lambda$open$32$ArticleViewer(AnimatorSet animatorSet) {
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        animatorSet.start();
    }

    private void showProgressView(boolean z, final boolean z2) {
        if (z) {
            AndroidUtilities.cancelRunOnUIThread(this.lineProgressTickRunnable);
            if (z2) {
                this.lineProgressView.setProgress(0.0f, false);
                this.lineProgressView.setProgress(0.3f, true);
                AndroidUtilities.runOnUIThread(this.lineProgressTickRunnable, 100);
                return;
            }
            this.lineProgressView.setProgress(1.0f, true);
            return;
        }
        AnimatorSet animatorSet = this.progressViewAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.progressViewAnimation = new AnimatorSet();
        AnimatorSet animatorSet2;
        Animator[] animatorArr;
        if (z2) {
            this.progressView.setVisibility(0);
            this.menuContainer.setEnabled(false);
            animatorSet2 = this.progressViewAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.menuButton, View.SCALE_X, new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.menuButton, View.SCALE_Y, new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.menuButton, View.ALPHA, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f});
            animatorSet2.playTogether(animatorArr);
        } else {
            this.menuButton.setVisibility(0);
            this.menuContainer.setEnabled(true);
            animatorSet2 = this.progressViewAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.menuButton, View.SCALE_X, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.menuButton, View.SCALE_Y, new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.menuButton, View.ALPHA, new float[]{1.0f});
            animatorSet2.playTogether(animatorArr);
        }
        this.progressViewAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animator)) {
                    if (z2) {
                        ArticleViewer.this.menuButton.setVisibility(4);
                    } else {
                        ArticleViewer.this.progressView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animator)) {
                    ArticleViewer.this.progressViewAnimation = null;
                }
            }
        });
        this.progressViewAnimation.setDuration(150);
        this.progressViewAnimation.start();
    }

    public void collapse() {
        if (this.parentActivity != null && this.isVisible && !checkAnimation()) {
            if (this.fullscreenVideoContainer.getVisibility() == 0) {
                if (this.customView != null) {
                    this.fullscreenVideoContainer.setVisibility(4);
                    this.customViewCallback.onCustomViewHidden();
                    this.fullscreenVideoContainer.removeView(this.customView);
                    this.customView = null;
                } else {
                    WebPlayerView webPlayerView = this.fullscreenedVideo;
                    if (webPlayerView != null) {
                        webPlayerView.exitFullscreen();
                    }
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
            } catch (Exception e) {
                FileLog.e(e);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[12];
            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, new float[]{(float) (r5.getMeasuredWidth() - AndroidUtilities.dp(56.0f))});
            FrameLayout frameLayout = this.containerView;
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[1];
            fArr[0] = (float) (ActionBar.getCurrentActionBarHeight() + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
            animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
            animatorArr[2] = ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.listView[0], View.ALPHA, new float[]{0.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.listView[0], View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(56.0f))});
            animatorArr[5] = ObjectAnimator.ofFloat(this.headerView, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[6] = ObjectAnimator.ofFloat(this.backButton, View.SCALE_X, new float[]{1.0f});
            animatorArr[7] = ObjectAnimator.ofFloat(this.backButton, View.SCALE_Y, new float[]{1.0f});
            animatorArr[8] = ObjectAnimator.ofFloat(this.backButton, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[9] = ObjectAnimator.ofFloat(this.menuContainer, View.SCALE_X, new float[]{1.0f});
            animatorArr[10] = ObjectAnimator.ofFloat(this.menuContainer, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[11] = ObjectAnimator.ofFloat(this.menuContainer, View.SCALE_Y, new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
            this.collapsed = true;
            this.animationInProgress = 2;
            this.animationEndRunnable = new -$$Lambda$ArticleViewer$nkINJ_lZEIwK6AERx7rhUuanUf8(this);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(250);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
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

    public /* synthetic */ void lambda$collapse$33$ArticleViewer() {
        FrameLayout frameLayout = this.containerView;
        if (frameLayout != null) {
            if (VERSION.SDK_INT >= 18) {
                frameLayout.setLayerType(0, null);
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
            animatorArr[9] = ObjectAnimator.ofFloat(this.menuContainer, View.SCALE_X, new float[]{1.0f});
            animatorArr[10] = ObjectAnimator.ofFloat(this.menuContainer, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[11] = ObjectAnimator.ofFloat(this.menuContainer, View.SCALE_Y, new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
            this.collapsed = false;
            this.animationInProgress = 2;
            this.animationEndRunnable = new -$$Lambda$ArticleViewer$bgTX3yq1ThPc5x5wesNR1qJvU30(this);
            animatorSet.setDuration(250);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
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

    public /* synthetic */ void lambda$uncollapse$34$ArticleViewer() {
        FrameLayout frameLayout = this.containerView;
        if (frameLayout != null) {
            if (VERSION.SDK_INT >= 18) {
                frameLayout.setLayerType(0, null);
            }
            this.animationInProgress = 0;
        }
    }

    private void saveCurrentPagePosition() {
        if (this.currentPage != null) {
            boolean z = false;
            int findFirstVisibleItemPosition = this.layoutManager[0].findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition != -1) {
                View findViewByPosition = this.layoutManager[0].findViewByPosition(findFirstVisibleItemPosition);
                int top = findViewByPosition != null ? findViewByPosition.getTop() : 0;
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("article");
                stringBuilder.append(this.currentPage.id);
                String stringBuilder2 = stringBuilder.toString();
                Editor putInt = edit.putInt(stringBuilder2, findFirstVisibleItemPosition);
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(stringBuilder2);
                stringBuilder3.append("o");
                putInt = putInt.putInt(stringBuilder3.toString(), top);
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append(stringBuilder2);
                stringBuilder4.append("r");
                String stringBuilder5 = stringBuilder4.toString();
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    z = true;
                }
                putInt.putBoolean(stringBuilder5, z).commit();
            }
        }
    }

    private void refreshThemeColors() {
        TextView textView = this.deleteView;
        String str = "actionBarDefaultSubmenuItem";
        if (textView != null) {
            textView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 2));
            this.deleteView.setTextColor(Theme.getColor(str));
        }
        ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        String str2 = "actionBarDefaultSubmenuBackground";
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.setBackgroundColor(Theme.getColor(str2));
        }
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null) {
            frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
        EditTextBoldCursor editTextBoldCursor = this.searchField;
        String str3 = "windowBackgroundWhiteBlackText";
        if (editTextBoldCursor != null) {
            editTextBoldCursor.setTextColor(Theme.getColor(str3));
            this.searchField.setCursorColor(Theme.getColor(str3));
            this.searchField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        }
        ImageView imageView = this.searchUpButton;
        String str4 = "actionBarActionModeDefaultSelector";
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
            this.searchUpButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(str4), 1));
        }
        imageView = this.searchDownButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
            this.searchDownButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(str4), 1));
        }
        SimpleTextView simpleTextView = this.searchCountText;
        if (simpleTextView != null) {
            simpleTextView.setTextColor(Theme.getColor(str3));
        }
        ActionBarMenuItem actionBarMenuItem = this.menuButton;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.redrawPopup(Theme.getColor(str2));
            this.menuButton.setPopupItemsColor(Theme.getColor(str), false);
            this.menuButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItemIcon"), true);
        }
        imageView = this.clearButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
        }
        BackDrawable backDrawable = this.backDrawable;
        if (backDrawable != null) {
            backDrawable.setColor(Theme.getColor(str3));
        }
    }

    public void close(boolean z, boolean z2) {
        if (!(this.parentActivity == null || !this.isVisible || checkAnimation())) {
            if (this.fullscreenVideoContainer.getVisibility() == 0) {
                if (this.customView != null) {
                    this.fullscreenVideoContainer.setVisibility(4);
                    this.customViewCallback.onCustomViewHidden();
                    this.fullscreenVideoContainer.removeView(this.customView);
                    this.customView = null;
                } else {
                    WebPlayerView webPlayerView = this.fullscreenedVideo;
                    if (webPlayerView != null) {
                        webPlayerView.exitFullscreen();
                    }
                }
                if (!z2) {
                    return;
                }
            }
            if (this.textSelectionHelper.isSelectionMode()) {
                this.textSelectionHelper.clear();
                return;
            }
            if (this.isPhotoVisible) {
                closePhoto(z2 ^ 1);
                if (!z2) {
                    return;
                }
            }
            if (this.searchContainer.getTag() != null) {
                showSearch(false);
                return;
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
            if (!z || z2 || !removeLastPageFromStack()) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
                this.parentFragment = null;
                try {
                    if (this.visibleDialog != null) {
                        this.visibleDialog.dismiss();
                        this.visibleDialog = null;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[3];
                animatorArr[0] = ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.containerView, View.ALPHA, new float[]{0.0f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.windowView, View.TRANSLATION_X, new float[]{0.0f, (float) AndroidUtilities.dp(56.0f)});
                animatorSet.playTogether(animatorArr);
                this.animationInProgress = 2;
                this.animationEndRunnable = new -$$Lambda$ArticleViewer$Q1yHwN1KbgdkICRdENzqyGfMe_A(this);
                animatorSet.setDuration(150);
                animatorSet.setInterpolator(this.interpolator);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
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

    public /* synthetic */ void lambda$close$35$ArticleViewer() {
        FrameLayout frameLayout = this.containerView;
        if (frameLayout != null) {
            if (VERSION.SDK_INT >= 18) {
                frameLayout.setLayerType(0, null);
            }
            this.animationInProgress = 0;
            onClosed();
        }
    }

    private void onClosed() {
        int i;
        this.isVisible = false;
        this.currentPage = null;
        for (i = 0; i < this.listView.length; i++) {
            this.adapter[i].cleanup();
        }
        try {
            this.parentActivity.getWindow().clearFlags(128);
        } catch (Exception e) {
            FileLog.e(e);
        }
        for (i = 0; i < this.createdWebViews.size(); i++) {
            ((BlockEmbedCell) this.createdWebViews.get(i)).destroyWebView(false);
        }
        this.containerView.post(new -$$Lambda$ArticleViewer$gS7aOo6YrZLoO-3HN_x7Mmftdf8(this));
    }

    public /* synthetic */ void lambda$onClosed$36$ArticleViewer() {
        try {
            if (this.windowView.getParent() != null) {
                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void loadChannel(BlockChannelCell blockChannelCell, WebpageAdapter webpageAdapter, Chat chat) {
        if (!this.loadingChannel && !TextUtils.isEmpty(chat.username)) {
            this.loadingChannel = true;
            TL_contacts_resolveUsername tL_contacts_resolveUsername = new TL_contacts_resolveUsername();
            tL_contacts_resolveUsername.username = chat.username;
            int i = UserConfig.selectedAccount;
            ConnectionsManager.getInstance(i).sendRequest(tL_contacts_resolveUsername, new -$$Lambda$ArticleViewer$6a8oWBgsq_FBcDQQmy6Bg_2xtf4(this, webpageAdapter, i, blockChannelCell));
        }
    }

    public /* synthetic */ void lambda$loadChannel$38$ArticleViewer(WebpageAdapter webpageAdapter, int i, BlockChannelCell blockChannelCell, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$AqwFnXDGVXp8swqYtm08_p7sQmc(this, webpageAdapter, tL_error, tLObject, i, blockChannelCell));
    }

    public /* synthetic */ void lambda$null$37$ArticleViewer(WebpageAdapter webpageAdapter, TL_error tL_error, TLObject tLObject, int i, BlockChannelCell blockChannelCell) {
        this.loadingChannel = false;
        if (this.parentFragment != null && !webpageAdapter.blocks.isEmpty()) {
            if (tL_error == null) {
                TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) tLObject;
                if (tL_contacts_resolvedPeer.chats.isEmpty()) {
                    blockChannelCell.setState(4, false);
                    return;
                }
                MessagesController.getInstance(i).putUsers(tL_contacts_resolvedPeer.users, false);
                MessagesController.getInstance(i).putChats(tL_contacts_resolvedPeer.chats, false);
                MessagesStorage.getInstance(i).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, false, true);
                this.loadedChannel = (Chat) tL_contacts_resolvedPeer.chats.get(0);
                Chat chat = this.loadedChannel;
                if (!chat.left || chat.kicked) {
                    blockChannelCell.setState(4, false);
                    return;
                } else {
                    blockChannelCell.setState(0, false);
                    return;
                }
            }
            blockChannelCell.setState(4, false);
        }
    }

    private void joinChannel(BlockChannelCell blockChannelCell, Chat chat) {
        TL_channels_joinChannel tL_channels_joinChannel = new TL_channels_joinChannel();
        tL_channels_joinChannel.channel = MessagesController.getInputChannel(chat);
        int i = UserConfig.selectedAccount;
        ConnectionsManager.getInstance(i).sendRequest(tL_channels_joinChannel, new -$$Lambda$ArticleViewer$J4CcJqFYEtyhRPZdMCLASSNAMExF0Y6Q(this, blockChannelCell, i, tL_channels_joinChannel, chat));
    }

    public /* synthetic */ void lambda$joinChannel$42$ArticleViewer(BlockChannelCell blockChannelCell, int i, TL_channels_joinChannel tL_channels_joinChannel, Chat chat, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$KL5ojs4E_Y_euUYEdihKJ6aRiFE(this, blockChannelCell, i, tL_error, tL_channels_joinChannel));
            return;
        }
        Object obj;
        Updates updates = (Updates) tLObject;
        for (int i2 = 0; i2 < updates.updates.size(); i2++) {
            Update update = (Update) updates.updates.get(i2);
            if ((update instanceof TL_updateNewChannelMessage) && (((TL_updateNewChannelMessage) update).message.action instanceof TL_messageActionChatAddUser)) {
                obj = 1;
                break;
            }
        }
        obj = null;
        MessagesController.getInstance(i).processUpdates(updates, false);
        if (obj == null) {
            MessagesController.getInstance(i).generateJoinMessage(chat.id, true);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$PQ6bn-iXgABOFVN4iRhRgfN6bMY(blockChannelCell));
        AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$qq2kZH3F6npvUfpYTbNsCTMTTs0(i, chat), 1000);
        MessagesStorage.getInstance(i).updateDialogsWithDeletedMessages(new ArrayList(), null, true, chat.id);
    }

    public /* synthetic */ void lambda$null$39$ArticleViewer(BlockChannelCell blockChannelCell, int i, TL_error tL_error, TL_channels_joinChannel tL_channels_joinChannel) {
        blockChannelCell.setState(0, false);
        AlertsCreator.processError(i, tL_error, this.parentFragment, tL_channels_joinChannel, Boolean.valueOf(true));
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

    public void destroyArticleViewer() {
        if (this.parentActivity != null && this.windowView != null) {
            releasePlayer();
            try {
                if (this.windowView.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
            for (int i = 0; i < this.createdWebViews.size(); i++) {
                ((BlockEmbedCell) this.createdWebViews.get(i)).destroyWebView(true);
            }
            this.createdWebViews.clear();
            try {
                this.parentActivity.getWindow().clearFlags(128);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            BitmapHolder bitmapHolder = this.currentThumb;
            if (bitmapHolder != null) {
                bitmapHolder.release();
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
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                this.visibleDialog = dialog;
                this.visibleDialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new -$$Lambda$ArticleViewer$nJoC7yMoDKiw4e6hcjn0sFxye0I(this));
                dialog.show();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    public /* synthetic */ void lambda$showDialog$43$ArticleViewer(DialogInterface dialogInterface) {
        this.visibleDialog = null;
    }

    private void drawTextSelection(Canvas canvas, ArticleSelectableView articleSelectableView) {
        drawTextSelection(canvas, articleSelectableView, 0);
    }

    private void drawTextSelection(Canvas canvas, ArticleSelectableView articleSelectableView, int i) {
        View view = (View) articleSelectableView;
        if (view.getTag() != null && view.getTag() == "bottomSheet") {
            ArticleTextSelectionHelper articleTextSelectionHelper = this.textSelectionHelperBottomSheet;
            if (articleTextSelectionHelper != null) {
                articleTextSelectionHelper.draw(canvas, articleSelectableView, i);
                return;
            }
        }
        this.textSelectionHelper.draw(canvas, articleSelectableView, i);
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:14:0x0040 */
    /* JADX WARNING: Can't wrap try/catch for region: R(4:12|13|14|15) */
    private void onSharePressed() {
        /*
        r5 = this;
        r0 = r5.parentActivity;
        if (r0 == 0) goto L_0x009c;
    L_0x0004:
        r0 = r5.currentMedia;
        if (r0 != 0) goto L_0x000a;
    L_0x0008:
        goto L_0x009c;
    L_0x000a:
        r0 = r5.currentIndex;	 Catch:{ Exception -> 0x0098 }
        r0 = r5.getMediaFile(r0);	 Catch:{ Exception -> 0x0098 }
        if (r0 == 0) goto L_0x0064;
    L_0x0012:
        r1 = r0.exists();	 Catch:{ Exception -> 0x0098 }
        if (r1 == 0) goto L_0x0064;
    L_0x0018:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x0098 }
        r2 = "android.intent.action.SEND";
        r1.<init>(r2);	 Catch:{ Exception -> 0x0098 }
        r2 = r5.currentIndex;	 Catch:{ Exception -> 0x0098 }
        r2 = r5.getMediaMime(r2);	 Catch:{ Exception -> 0x0098 }
        r1.setType(r2);	 Catch:{ Exception -> 0x0098 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0098 }
        r3 = 24;
        r4 = "android.intent.extra.STREAM";
        if (r2 < r3) goto L_0x0048;
    L_0x0030:
        r2 = r5.parentActivity;	 Catch:{ Exception -> 0x0040 }
        r3 = "org.telegram.messenger.beta.provider";
        r2 = androidx.core.content.FileProvider.getUriForFile(r2, r3, r0);	 Catch:{ Exception -> 0x0040 }
        r1.putExtra(r4, r2);	 Catch:{ Exception -> 0x0040 }
        r2 = 1;
        r1.setFlags(r2);	 Catch:{ Exception -> 0x0040 }
        goto L_0x004f;
    L_0x0040:
        r0 = android.net.Uri.fromFile(r0);	 Catch:{ Exception -> 0x0098 }
        r1.putExtra(r4, r0);	 Catch:{ Exception -> 0x0098 }
        goto L_0x004f;
    L_0x0048:
        r0 = android.net.Uri.fromFile(r0);	 Catch:{ Exception -> 0x0098 }
        r1.putExtra(r4, r0);	 Catch:{ Exception -> 0x0098 }
    L_0x004f:
        r0 = r5.parentActivity;	 Catch:{ Exception -> 0x0098 }
        r2 = "ShareFile";
        r3 = NUM; // 0x7f0e0a4e float:1.8880388E38 double:1.05316346E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Exception -> 0x0098 }
        r1 = android.content.Intent.createChooser(r1, r2);	 Catch:{ Exception -> 0x0098 }
        r2 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r0.startActivityForResult(r1, r2);	 Catch:{ Exception -> 0x0098 }
        goto L_0x009c;
    L_0x0064:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;	 Catch:{ Exception -> 0x0098 }
        r1 = r5.parentActivity;	 Catch:{ Exception -> 0x0098 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x0098 }
        r1 = "AppName";
        r2 = NUM; // 0x7f0e00ff float:1.8875555E38 double:1.0531622826E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);	 Catch:{ Exception -> 0x0098 }
        r0.setTitle(r1);	 Catch:{ Exception -> 0x0098 }
        r1 = "OK";
        r2 = NUM; // 0x7f0e076e float:1.8878895E38 double:1.0531630963E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);	 Catch:{ Exception -> 0x0098 }
        r2 = 0;
        r0.setPositiveButton(r1, r2);	 Catch:{ Exception -> 0x0098 }
        r1 = "PleaseDownload";
        r2 = NUM; // 0x7f0e08e9 float:1.8879664E38 double:1.0531632836E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);	 Catch:{ Exception -> 0x0098 }
        r0.setMessage(r1);	 Catch:{ Exception -> 0x0098 }
        r0 = r0.create();	 Catch:{ Exception -> 0x0098 }
        r5.showDialog(r0);	 Catch:{ Exception -> 0x0098 }
        goto L_0x009c;
    L_0x0098:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x009c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.onSharePressed():void");
    }

    private void setScaleToFill() {
        float bitmapWidth = (float) this.centerImage.getBitmapWidth();
        float containerViewWidth = (float) getContainerViewWidth();
        float bitmapHeight = (float) this.centerImage.getBitmapHeight();
        float containerViewHeight = (float) getContainerViewHeight();
        float min = Math.min(containerViewHeight / bitmapHeight, containerViewWidth / bitmapWidth);
        this.scale = Math.max(containerViewWidth / ((float) ((int) (bitmapWidth * min))), containerViewHeight / ((float) ((int) (bitmapHeight * min))));
        updateMinMax(this.scale);
    }

    private void updateVideoPlayerTime() {
        CharSequence formatLongDuration;
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            formatLongDuration = AndroidUtilities.formatLongDuration(0, 0);
        } else {
            long currentPosition = videoPlayer.getCurrentPosition() / 1000;
            long duration = this.videoPlayer.getDuration() / 1000;
            if (duration == -9223372036854775807L || currentPosition == -9223372036854775807L) {
                formatLongDuration = AndroidUtilities.formatLongDuration(0, 0);
            } else {
                formatLongDuration = AndroidUtilities.formatLongDuration((int) currentPosition, (int) duration);
            }
        }
        if (!TextUtils.equals(this.videoPlayerTime.getText(), formatLongDuration)) {
            this.videoPlayerTime.setText(formatLongDuration);
        }
    }

    @SuppressLint({"NewApi"})
    private void preparePlayer(File file, boolean z) {
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
            this.videoPlayButton.setImageResource(NUM);
            if (this.videoPlayer == null) {
                this.videoPlayer = new VideoPlayer();
                this.videoPlayer.setTextureView(this.videoTextureView);
                this.videoPlayer.setDelegate(new VideoPlayerDelegate() {
                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }

                    public void onStateChanged(boolean z, int i) {
                        if (ArticleViewer.this.videoPlayer != null) {
                            if (i == 4 || i == 1) {
                                try {
                                    ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                                } catch (Exception e) {
                                    FileLog.e(e);
                                }
                            } else {
                                try {
                                    ArticleViewer.this.parentActivity.getWindow().addFlags(128);
                                } catch (Exception e2) {
                                    FileLog.e(e2);
                                }
                            }
                            if (i == 3 && ArticleViewer.this.aspectRatioFrameLayout.getVisibility() != 0) {
                                ArticleViewer.this.aspectRatioFrameLayout.setVisibility(0);
                            }
                            if (!ArticleViewer.this.videoPlayer.isPlaying() || i == 4) {
                                if (ArticleViewer.this.isPlaying) {
                                    ArticleViewer.this.isPlaying = false;
                                    ArticleViewer.this.videoPlayButton.setImageResource(NUM);
                                    AndroidUtilities.cancelRunOnUIThread(ArticleViewer.this.updateProgressRunnable);
                                    if (i == 4 && !ArticleViewer.this.videoPlayerSeekbar.isDragging()) {
                                        ArticleViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                                        ArticleViewer.this.videoPlayerControlFrameLayout.invalidate();
                                        ArticleViewer.this.videoPlayer.seekTo(0);
                                        ArticleViewer.this.videoPlayer.pause();
                                    }
                                }
                            } else if (!ArticleViewer.this.isPlaying) {
                                ArticleViewer.this.isPlaying = true;
                                ArticleViewer.this.videoPlayButton.setImageResource(NUM);
                                AndroidUtilities.runOnUIThread(ArticleViewer.this.updateProgressRunnable);
                            }
                            ArticleViewer.this.updateVideoPlayerTime();
                        }
                    }

                    public void onError(Exception exception) {
                        FileLog.e((Throwable) exception);
                    }

                    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                        if (ArticleViewer.this.aspectRatioFrameLayout != null) {
                            if (!(i3 == 90 || i3 == 270)) {
                                int i4 = i2;
                                i2 = i;
                                i = i4;
                            }
                            ArticleViewer.this.aspectRatioFrameLayout.setAspectRatio(i == 0 ? 1.0f : (((float) i2) * f) / ((float) i), i3);
                        }
                    }

                    public void onRenderedFirstFrame() {
                        if (!ArticleViewer.this.textureUploaded) {
                            ArticleViewer.this.textureUploaded = true;
                            ArticleViewer.this.containerView.invalidate();
                        }
                    }
                });
            }
            this.videoPlayer.preparePlayer(Uri.fromFile(file), "other");
            this.bottomLayout.setVisibility(0);
            this.videoPlayer.setPlayWhenReady(z);
        }
    }

    private void releasePlayer() {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.releasePlayer(true);
            this.videoPlayer = null;
        }
        try {
            this.parentActivity.getWindow().clearFlags(128);
        } catch (Exception e) {
            FileLog.e(e);
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null) {
            this.photoContainerView.removeView(aspectRatioFrameLayout);
            this.aspectRatioFrameLayout = null;
        }
        if (this.videoTextureView != null) {
            this.videoTextureView = null;
        }
        if (this.isPlaying) {
            this.isPlaying = false;
            this.videoPlayButton.setImageResource(NUM);
            AndroidUtilities.cancelRunOnUIThread(this.updateProgressRunnable);
        }
        this.bottomLayout.setVisibility(8);
    }

    private void toggleActionBar(boolean z, boolean z2) {
        if (z) {
            this.actionBar.setVisibility(0);
            if (this.videoPlayer != null) {
                this.bottomLayout.setVisibility(0);
            }
            if (this.captionTextView.getTag() != null) {
                this.captionTextView.setVisibility(0);
            }
        }
        this.isActionBarVisible = z;
        this.actionBar.setEnabled(z);
        this.bottomLayout.setEnabled(z);
        float f = 1.0f;
        if (z2) {
            ArrayList arrayList = new ArrayList();
            ActionBar actionBar = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar, property, fArr));
            GroupedPhotosListView groupedPhotosListView = this.groupedPhotosListView;
            property = View.ALPHA;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView, property, fArr));
            FrameLayout frameLayout = this.bottomLayout;
            property = View.ALPHA;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            if (this.captionTextView.getTag() != null) {
                TextView textView = this.captionTextView;
                property = View.ALPHA;
                float[] fArr2 = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(textView, property, fArr2));
            }
            this.currentActionBarAnimation = new AnimatorSet();
            this.currentActionBarAnimation.playTogether(arrayList);
            if (!z) {
                this.currentActionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ArticleViewer.this.currentActionBarAnimation != null && ArticleViewer.this.currentActionBarAnimation.equals(animator)) {
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
        this.actionBar.setAlpha(z ? 1.0f : 0.0f);
        this.bottomLayout.setAlpha(z ? 1.0f : 0.0f);
        if (this.captionTextView.getTag() != null) {
            TextView textView2 = this.captionTextView;
            if (!z) {
                f = 0.0f;
            }
            textView2.setAlpha(f);
        }
        if (!z) {
            this.actionBar.setVisibility(8);
            if (this.videoPlayer != null) {
                this.bottomLayout.setVisibility(8);
            }
            if (this.captionTextView.getTag() != null) {
                this.captionTextView.setVisibility(8);
            }
        }
    }

    private String getFileName(int i) {
        TLObject media = getMedia(i);
        if (media instanceof Photo) {
            media = FileLoader.getClosestPhotoSizeWithSize(((Photo) media).sizes, AndroidUtilities.getPhotoSize());
        }
        return FileLoader.getAttachFileName(media);
    }

    private TLObject getMedia(int i) {
        if (!this.imagesArr.isEmpty() && i < this.imagesArr.size() && i >= 0) {
            PageBlock pageBlock = (PageBlock) this.imagesArr.get(i);
            if (pageBlock instanceof TL_pageBlockPhoto) {
                return getPhotoWithId(((TL_pageBlockPhoto) pageBlock).photo_id);
            }
            if (pageBlock instanceof TL_pageBlockVideo) {
                return getDocumentWithId(((TL_pageBlockVideo) pageBlock).video_id);
            }
        }
        return null;
    }

    private File getMediaFile(int i) {
        if (!this.imagesArr.isEmpty() && i < this.imagesArr.size() && i >= 0) {
            PageBlock pageBlock = (PageBlock) this.imagesArr.get(i);
            if (pageBlock instanceof TL_pageBlockPhoto) {
                Photo photoWithId = getPhotoWithId(((TL_pageBlockPhoto) pageBlock).photo_id);
                if (photoWithId != null) {
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photoWithId.sizes, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        return FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
                    }
                }
            } else if (pageBlock instanceof TL_pageBlockVideo) {
                Document documentWithId = getDocumentWithId(((TL_pageBlockVideo) pageBlock).video_id);
                if (documentWithId != null) {
                    return FileLoader.getPathToAttach(documentWithId, true);
                }
            }
        }
        return null;
    }

    private boolean isVideoBlock(PageBlock pageBlock) {
        if (pageBlock instanceof TL_pageBlockVideo) {
            Document documentWithId = getDocumentWithId(((TL_pageBlockVideo) pageBlock).video_id);
            if (documentWithId != null) {
                return MessageObject.isVideoDocument(documentWithId);
            }
        }
        return false;
    }

    private boolean isMediaVideo(int i) {
        return !this.imagesArr.isEmpty() && i < this.imagesArr.size() && i >= 0 && isVideoBlock((PageBlock) this.imagesArr.get(i));
    }

    private String getMediaMime(int i) {
        String str = "image/jpeg";
        if (i < this.imagesArr.size() && i >= 0) {
            PageBlock pageBlock = (PageBlock) this.imagesArr.get(i);
            if (pageBlock instanceof TL_pageBlockVideo) {
                Document documentWithId = getDocumentWithId(((TL_pageBlockVideo) pageBlock).video_id);
                if (documentWithId != null) {
                    return documentWithId.mime_type;
                }
            }
        }
        return str;
    }

    private PhotoSize getFileLocation(TLObject tLObject, int[] iArr) {
        PhotoSize closestPhotoSizeWithSize;
        if (tLObject instanceof Photo) {
            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(((Photo) tLObject).sizes, AndroidUtilities.getPhotoSize());
            if (closestPhotoSizeWithSize != null) {
                iArr[0] = closestPhotoSizeWithSize.size;
                if (iArr[0] == 0) {
                    iArr[0] = -1;
                }
                return closestPhotoSizeWithSize;
            }
            iArr[0] = -1;
        } else if (tLObject instanceof Document) {
            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(((Document) tLObject).thumbs, 90);
            if (closestPhotoSizeWithSize != null) {
                iArr[0] = closestPhotoSizeWithSize.size;
                if (iArr[0] == 0) {
                    iArr[0] = -1;
                }
                return closestPhotoSizeWithSize;
            }
        }
        return null;
    }

    private void onPhotoShow(int i, PlaceProviderObject placeProviderObject) {
        this.currentIndex = -1;
        String[] strArr = this.currentFileNames;
        strArr[0] = null;
        strArr[1] = null;
        strArr[2] = null;
        BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
        }
        this.currentThumb = placeProviderObject != null ? placeProviderObject.thumb : null;
        this.menuItem.setVisibility(0);
        this.menuItem.hideSubItem(3);
        this.actionBar.setTranslationY(0.0f);
        this.captionTextView.setTag(null);
        this.captionTextView.setVisibility(8);
        for (int i2 = 0; i2 < 3; i2++) {
            RadialProgressView[] radialProgressViewArr = this.radialProgressViews;
            if (radialProgressViewArr[i2] != null) {
                radialProgressViewArr[i2].setBackgroundState(-1, false);
            }
        }
        setImageIndex(i, true);
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

    /* JADX WARNING: Removed duplicated region for block: B:30:0x00b6  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00dc  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00be  */
    private void setImageIndex(int r18, boolean r19) {
        /*
        r17 = this;
        r6 = r17;
        r0 = r18;
        r1 = r6.currentIndex;
        if (r1 != r0) goto L_0x0009;
    L_0x0008:
        return;
    L_0x0009:
        r7 = 0;
        if (r19 != 0) goto L_0x0015;
    L_0x000c:
        r1 = r6.currentThumb;
        if (r1 == 0) goto L_0x0015;
    L_0x0010:
        r1.release();
        r6.currentThumb = r7;
    L_0x0015:
        r1 = r6.currentFileNames;
        r2 = r17.getFileName(r18);
        r8 = 0;
        r1[r8] = r2;
        r1 = r6.currentFileNames;
        r2 = r0 + 1;
        r2 = r6.getFileName(r2);
        r9 = 1;
        r1[r9] = r2;
        r1 = r6.currentFileNames;
        r2 = r0 + -1;
        r2 = r6.getFileName(r2);
        r10 = 2;
        r1[r10] = r2;
        r11 = r6.currentIndex;
        r6.currentIndex = r0;
        r0 = r6.imagesArr;
        r0 = r0.isEmpty();
        r12 = 3;
        r13 = 4;
        if (r0 != 0) goto L_0x0142;
    L_0x0042:
        r0 = r6.currentIndex;
        if (r0 < 0) goto L_0x013e;
    L_0x0046:
        r1 = r6.imagesArr;
        r1 = r1.size();
        if (r0 < r1) goto L_0x0050;
    L_0x004e:
        goto L_0x013e;
    L_0x0050:
        r0 = r6.imagesArr;
        r1 = r6.currentIndex;
        r0 = r0.get(r1);
        r0 = (org.telegram.tgnet.TLRPC.PageBlock) r0;
        r1 = r6.currentMedia;
        if (r1 == 0) goto L_0x0062;
    L_0x005e:
        if (r1 != r0) goto L_0x0062;
    L_0x0060:
        r14 = 1;
        goto L_0x0063;
    L_0x0062:
        r14 = 0;
    L_0x0063:
        r6.currentMedia = r0;
        r1 = r6.currentIndex;
        r15 = r6.isMediaVideo(r1);
        if (r15 == 0) goto L_0x0072;
    L_0x006d:
        r1 = r6.menuItem;
        r1.showSubItem(r12);
    L_0x0072:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockPhoto;
        if (r1 == 0) goto L_0x0095;
    L_0x0076:
        r0 = (org.telegram.tgnet.TLRPC.TL_pageBlockPhoto) r0;
        r0 = r0.url;
        r1 = android.text.TextUtils.isEmpty(r0);
        if (r1 != 0) goto L_0x0095;
    L_0x0080:
        r1 = new android.text.SpannableStringBuilder;
        r1.<init>(r0);
        r2 = new org.telegram.ui.ArticleViewer$32;
        r2.<init>(r0);
        r0 = r0.length();
        r3 = 34;
        r1.setSpan(r2, r8, r0, r3);
        r5 = 1;
        goto L_0x0097;
    L_0x0095:
        r1 = r7;
        r5 = 0;
    L_0x0097:
        if (r1 != 0) goto L_0x00b6;
    L_0x0099:
        r0 = r6.currentMedia;
        r3 = r6.getBlockCaption(r0, r10);
        r1 = 0;
        r4 = r6.currentMedia;
        r0 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r2 = -r0;
        r0 = r17;
        r16 = r2;
        r2 = r3;
        r12 = r5;
        r5 = r16;
        r1 = r0.getText(r1, r2, r3, r4, r5);
        goto L_0x00b7;
    L_0x00b6:
        r12 = r5;
    L_0x00b7:
        r6.setCurrentCaption(r1, r12);
        r0 = r6.currentAnimation;
        if (r0 == 0) goto L_0x00dc;
    L_0x00be:
        r0 = r6.menuItem;
        r0.setVisibility(r8);
        r0 = r6.menuItem;
        r0.hideSubItem(r9);
        r0 = r6.menuItem;
        r0.showSubItem(r13);
        r0 = r6.actionBar;
        r1 = NUM; // 0x7f0e0156 float:1.8875731E38 double:1.0531623256E-314;
        r2 = "AttachGif";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        goto L_0x0138;
    L_0x00dc:
        r0 = r6.menuItem;
        r0.setVisibility(r8);
        r0 = r6.imagesArr;
        r0 = r0.size();
        if (r0 != r9) goto L_0x0109;
    L_0x00e9:
        if (r15 == 0) goto L_0x00fa;
    L_0x00eb:
        r0 = r6.actionBar;
        r1 = NUM; // 0x7f0e0167 float:1.8875766E38 double:1.053162334E-314;
        r2 = "AttachVideo";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        goto L_0x012e;
    L_0x00fa:
        r0 = r6.actionBar;
        r1 = NUM; // 0x7f0e0161 float:1.8875754E38 double:1.053162331E-314;
        r2 = "AttachPhoto";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        goto L_0x012e;
    L_0x0109:
        r0 = r6.actionBar;
        r1 = NUM; // 0x7f0e0770 float:1.88789E38 double:1.0531630973E-314;
        r2 = new java.lang.Object[r10];
        r3 = r6.currentIndex;
        r3 = r3 + r9;
        r3 = java.lang.Integer.valueOf(r3);
        r2[r8] = r3;
        r3 = r6.imagesArr;
        r3 = r3.size();
        r3 = java.lang.Integer.valueOf(r3);
        r2[r9] = r3;
        r3 = "Of";
        r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2);
        r0.setTitle(r1);
    L_0x012e:
        r0 = r6.menuItem;
        r0.showSubItem(r9);
        r0 = r6.menuItem;
        r0.hideSubItem(r13);
    L_0x0138:
        r0 = r6.groupedPhotosListView;
        r0.fillList();
        goto L_0x0144;
    L_0x013e:
        r6.closePhoto(r8);
        return;
    L_0x0142:
        r14 = 0;
        r15 = 0;
    L_0x0144:
        r0 = r6.listView;
        r0 = r0[r8];
        r0 = r0.getChildCount();
        r1 = 0;
    L_0x014d:
        r2 = -1;
        if (r1 >= r0) goto L_0x0177;
    L_0x0150:
        r3 = r6.listView;
        r3 = r3[r8];
        r3 = r3.getChildAt(r1);
        r4 = r3 instanceof org.telegram.ui.ArticleViewer.BlockSlideshowCell;
        if (r4 == 0) goto L_0x0174;
    L_0x015c:
        r3 = (org.telegram.ui.ArticleViewer.BlockSlideshowCell) r3;
        r4 = r3.currentBlock;
        r4 = r4.items;
        r5 = r6.currentMedia;
        r4 = r4.indexOf(r5);
        if (r4 == r2) goto L_0x0174;
    L_0x016c:
        r0 = r3.innerListView;
        r0.setCurrentItem(r4, r8);
        goto L_0x0177;
    L_0x0174:
        r1 = r1 + 1;
        goto L_0x014d;
    L_0x0177:
        r0 = r6.currentPlaceObject;
        if (r0 == 0) goto L_0x0187;
    L_0x017b:
        r1 = r6.photoAnimationInProgress;
        if (r1 != 0) goto L_0x0185;
    L_0x017f:
        r0 = r0.imageReceiver;
        r0.setVisible(r9, r9);
        goto L_0x0187;
    L_0x0185:
        r6.showAfterAnimation = r0;
    L_0x0187:
        r0 = r6.currentMedia;
        r0 = r6.getPlaceForPhoto(r0);
        r6.currentPlaceObject = r0;
        r0 = r6.currentPlaceObject;
        if (r0 == 0) goto L_0x019f;
    L_0x0193:
        r1 = r6.photoAnimationInProgress;
        if (r1 != 0) goto L_0x019d;
    L_0x0197:
        r0 = r0.imageReceiver;
        r0.setVisible(r8, r9);
        goto L_0x019f;
    L_0x019d:
        r6.hideAfterAnimation = r0;
    L_0x019f:
        if (r14 != 0) goto L_0x01fc;
    L_0x01a1:
        r6.draggingDown = r8;
        r0 = 0;
        r6.translationX = r0;
        r6.translationY = r0;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6.scale = r1;
        r6.animateToX = r0;
        r6.animateToY = r0;
        r6.animateToScale = r1;
        r3 = 0;
        r6.animationStartTime = r3;
        r6.imageMoveAnimation = r7;
        r3 = r6.aspectRatioFrameLayout;
        if (r3 == 0) goto L_0x01bf;
    L_0x01bc:
        r3.setVisibility(r13);
    L_0x01bf:
        r17.releasePlayer();
        r6.pinchStartDistance = r0;
        r6.pinchStartScale = r1;
        r6.pinchCenterX = r0;
        r6.pinchCenterY = r0;
        r6.pinchStartX = r0;
        r6.pinchStartY = r0;
        r6.moveStartX = r0;
        r6.moveStartY = r0;
        r6.zooming = r8;
        r6.moving = r8;
        r6.doubleTap = r8;
        r6.invalidCoords = r8;
        r6.canDragDown = r9;
        r6.changingPage = r8;
        r6.switchImageAfterAnimation = r8;
        r0 = r6.currentFileNames;
        r0 = r0[r8];
        if (r0 == 0) goto L_0x01f4;
    L_0x01e6:
        if (r15 != 0) goto L_0x01f4;
    L_0x01e8:
        r0 = r6.radialProgressViews;
        r0 = r0[r8];
        r0 = r0.backgroundState;
        if (r0 == 0) goto L_0x01f4;
    L_0x01f2:
        r0 = 1;
        goto L_0x01f5;
    L_0x01f4:
        r0 = 0;
    L_0x01f5:
        r6.canZoom = r0;
        r0 = r6.scale;
        r6.updateMinMax(r0);
    L_0x01fc:
        if (r11 != r2) goto L_0x020b;
    L_0x01fe:
        r17.setImages();
        r0 = 0;
        r1 = 3;
    L_0x0203:
        if (r0 >= r1) goto L_0x0259;
    L_0x0205:
        r6.checkProgress(r0, r8);
        r0 = r0 + 1;
        goto L_0x0203;
    L_0x020b:
        r6.checkProgress(r8, r8);
        r0 = r6.currentIndex;
        if (r11 <= r0) goto L_0x0235;
    L_0x0212:
        r1 = r6.rightImage;
        r2 = r6.centerImage;
        r6.rightImage = r2;
        r2 = r6.leftImage;
        r6.centerImage = r2;
        r6.leftImage = r1;
        r1 = r6.radialProgressViews;
        r2 = r1[r8];
        r3 = r1[r10];
        r1[r8] = r3;
        r1[r10] = r2;
        r1 = r6.leftImage;
        r0 = r0 - r9;
        r6.setIndexToImage(r1, r0);
        r6.checkProgress(r9, r8);
        r6.checkProgress(r10, r8);
        goto L_0x0259;
    L_0x0235:
        if (r11 >= r0) goto L_0x0259;
    L_0x0237:
        r1 = r6.leftImage;
        r2 = r6.centerImage;
        r6.leftImage = r2;
        r2 = r6.rightImage;
        r6.centerImage = r2;
        r6.rightImage = r1;
        r1 = r6.radialProgressViews;
        r2 = r1[r8];
        r3 = r1[r9];
        r1[r8] = r3;
        r1[r9] = r2;
        r1 = r6.rightImage;
        r0 = r0 + r9;
        r6.setIndexToImage(r1, r0);
        r6.checkProgress(r9, r8);
        r6.checkProgress(r10, r8);
    L_0x0259:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.setImageIndex(int, boolean):void");
    }

    private void setCurrentCaption(CharSequence charSequence, boolean z) {
        if (TextUtils.isEmpty(charSequence)) {
            this.captionTextView.setTag(null);
            this.captionTextView.setVisibility(8);
            return;
        }
        Theme.createChatResources(null, true);
        if (!z) {
            if (charSequence instanceof Spannable) {
                Spannable spannable = (Spannable) charSequence;
                TextPaintUrlSpan[] textPaintUrlSpanArr = (TextPaintUrlSpan[]) spannable.getSpans(0, charSequence.length(), TextPaintUrlSpan.class);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence.toString());
                if (textPaintUrlSpanArr != null && textPaintUrlSpanArr.length > 0) {
                    for (int i = 0; i < textPaintUrlSpanArr.length; i++) {
                        spannableStringBuilder.setSpan(new URLSpan(textPaintUrlSpanArr[i].getUrl()) {
                            public void onClick(View view) {
                                ArticleViewer.this.openWebpageUrl(getURL(), null);
                            }
                        }, spannable.getSpanStart(textPaintUrlSpanArr[i]), spannable.getSpanEnd(textPaintUrlSpanArr[i]), 33);
                    }
                }
                charSequence = spannableStringBuilder;
            } else {
                charSequence = new SpannableStringBuilder(charSequence.toString());
            }
        }
        charSequence = Emoji.replaceEmoji(charSequence, this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        this.captionTextView.setTag(charSequence);
        this.captionTextView.setText(charSequence);
        this.captionTextView.setVisibility(0);
    }

    private void checkProgress(int i, boolean z) {
        if (this.currentFileNames[i] != null) {
            int i2 = this.currentIndex;
            boolean z2 = true;
            if (i == 1) {
                i2++;
            } else if (i == 2) {
                i2--;
            }
            File mediaFile = getMediaFile(i2);
            boolean isMediaVideo = isMediaVideo(i2);
            if (mediaFile == null || !mediaFile.exists()) {
                if (!isMediaVideo) {
                    this.radialProgressViews[i].setBackgroundState(0, z);
                } else if (FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[i])) {
                    this.radialProgressViews[i].setBackgroundState(1, false);
                } else {
                    this.radialProgressViews[i].setBackgroundState(2, false);
                }
                Float fileProgress = ImageLoader.getInstance().getFileProgress(this.currentFileNames[i]);
                if (fileProgress == null) {
                    fileProgress = Float.valueOf(0.0f);
                }
                this.radialProgressViews[i].setProgress(fileProgress.floatValue(), false);
            } else if (isMediaVideo) {
                this.radialProgressViews[i].setBackgroundState(3, z);
            } else {
                this.radialProgressViews[i].setBackgroundState(-1, z);
            }
            if (i == 0) {
                if (this.currentFileNames[0] == null || isMediaVideo || this.radialProgressViews[0].backgroundState == 0) {
                    z2 = false;
                }
                this.canZoom = z2;
                return;
            }
            return;
        }
        this.radialProgressViews[i].setBackgroundState(-1, z);
    }

    private void setIndexToImage(ImageReceiver imageReceiver, int i) {
        ImageReceiver imageReceiver2 = imageReceiver;
        int i2 = i;
        imageReceiver.setOrientation(0, false);
        int[] iArr = new int[1];
        TLObject media = getMedia(i2);
        PhotoSize fileLocation = getFileLocation(media, iArr);
        if (fileLocation != null) {
            BitmapHolder bitmapHolder;
            if (media instanceof Photo) {
                Photo photo = (Photo) media;
                bitmapHolder = this.currentThumb;
                if (bitmapHolder == null || imageReceiver2 != this.centerImage) {
                    bitmapHolder = null;
                }
                if (iArr[0] == 0) {
                    iArr[0] = -1;
                }
                imageReceiver.setImage(ImageLocation.getForPhoto(fileLocation, photo), null, ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80), photo), "b", bitmapHolder != null ? new BitmapDrawable(bitmapHolder.bitmap) : null, iArr[0], null, this.currentPage, 1);
            } else if (!isMediaVideo(i2)) {
                Drawable drawable = this.currentAnimation;
                if (drawable != null) {
                    imageReceiver.setImageBitmap(drawable);
                    this.currentAnimation.setSecondParentView(this.photoContainerView);
                }
            } else if (fileLocation.location instanceof TL_fileLocationUnavailable) {
                imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(NUM));
            } else {
                bitmapHolder = this.currentThumb;
                if (bitmapHolder == null || imageReceiver2 != this.centerImage) {
                    bitmapHolder = null;
                }
                imageReceiver.setImage(null, null, ImageLocation.getForDocument(fileLocation, (Document) media), "b", bitmapHolder != null ? new BitmapDrawable(bitmapHolder.bitmap) : null, 0, null, this.currentPage, 1);
            }
        } else if (iArr[0] == 0) {
            imageReceiver.setImageBitmap(null);
        } else {
            imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(NUM));
        }
    }

    public boolean isShowingImage(PageBlock pageBlock) {
        return this.isPhotoVisible && !this.disableShowCheck && pageBlock != null && this.currentMedia == pageBlock;
    }

    private boolean checkPhotoAnimation() {
        if (this.photoAnimationInProgress != 0 && Math.abs(this.photoTransitionAnimationStartTime - System.currentTimeMillis()) >= 500) {
            Runnable runnable = this.photoAnimationEndRunnable;
            if (runnable != null) {
                runnable.run();
                this.photoAnimationEndRunnable = null;
            }
            this.photoAnimationInProgress = 0;
        }
        if (this.photoAnimationInProgress != 0) {
            return true;
        }
        return false;
    }

    public boolean openPhoto(PageBlock pageBlock) {
        PageBlock pageBlock2 = pageBlock;
        if (this.pageSwitchAnimation != null || this.parentActivity == null || this.isPhotoVisible || checkPhotoAnimation() || pageBlock2 == null) {
            return false;
        }
        PlaceProviderObject placeForPhoto = getPlaceForPhoto(pageBlock);
        if (placeForPhoto == null) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.searchField);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailToLoad);
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
        if (pageBlock2 != null) {
            this.currentAnimation = placeForPhoto.imageReceiver.getAnimation();
        }
        int indexOf = this.adapter[0].photoBlocks.indexOf(pageBlock2);
        this.imagesArr.clear();
        if (!(pageBlock2 instanceof TL_pageBlockVideo) || isVideoBlock(pageBlock)) {
            this.imagesArr.addAll(this.adapter[0].photoBlocks);
        } else {
            this.imagesArr.add(pageBlock2);
            indexOf = 0;
        }
        onPhotoShow(indexOf, placeForPhoto);
        RectF drawRegion = placeForPhoto.imageReceiver.getDrawRegion();
        indexOf = placeForPhoto.imageReceiver.getOrientation();
        int animatedOrientation = placeForPhoto.imageReceiver.getAnimatedOrientation();
        if (animatedOrientation != 0) {
            indexOf = animatedOrientation;
        }
        this.animatingImageView.setVisibility(0);
        this.animatingImageView.setRadius(placeForPhoto.radius);
        this.animatingImageView.setOrientation(indexOf);
        this.animatingImageView.setImageBitmap(placeForPhoto.thumb);
        this.animatingImageView.setAlpha(1.0f);
        this.animatingImageView.setPivotX(0.0f);
        this.animatingImageView.setPivotY(0.0f);
        this.animatingImageView.setScaleX(placeForPhoto.scale);
        this.animatingImageView.setScaleY(placeForPhoto.scale);
        this.animatingImageView.setTranslationX(((float) placeForPhoto.viewX) + (drawRegion.left * placeForPhoto.scale));
        this.animatingImageView.setTranslationY(((float) placeForPhoto.viewY) + (drawRegion.top * placeForPhoto.scale));
        ViewGroup.LayoutParams layoutParams = this.animatingImageView.getLayoutParams();
        layoutParams.width = (int) drawRegion.width();
        layoutParams.height = (int) drawRegion.height();
        if (layoutParams.width == 0) {
            layoutParams.width = 1;
        }
        if (layoutParams.height == 0) {
            layoutParams.height = 1;
        }
        this.animatingImageView.setLayoutParams(layoutParams);
        Point point = AndroidUtilities.displaySize;
        float f = ((float) point.x) / ((float) layoutParams.width);
        float f2 = ((float) point.y) / ((float) layoutParams.height);
        if (f <= f2) {
            f2 = f;
        }
        float f3 = ((float) layoutParams.height) * f2;
        float f4 = (((float) AndroidUtilities.displaySize.x) - (((float) layoutParams.width) * f2)) / 2.0f;
        if (VERSION.SDK_INT >= 21) {
            Object obj = this.lastInsets;
            if (obj != null) {
                f4 += (float) ((WindowInsets) obj).getSystemWindowInsetLeft();
            }
        }
        float f5 = ((((float) AndroidUtilities.displaySize.y) - f3) / 2.0f) + ((float) AndroidUtilities.statusBarHeight);
        if (placeForPhoto.imageReceiver.isAspectFit()) {
            animatedOrientation = 0;
        } else {
            animatedOrientation = (int) Math.abs(drawRegion.left - ((float) placeForPhoto.imageReceiver.getImageX()));
        }
        int abs = (int) Math.abs(drawRegion.top - ((float) placeForPhoto.imageReceiver.getImageY()));
        int[] iArr = new int[2];
        placeForPhoto.parentView.getLocationInWindow(iArr);
        int i = (int) ((((float) iArr[1]) - (((float) placeForPhoto.viewY) + drawRegion.top)) + ((float) placeForPhoto.clipTopAddition));
        if (i < 0) {
            i = 0;
        }
        int height = (int) ((((((float) placeForPhoto.viewY) + drawRegion.top) + ((float) layoutParams.height)) - ((float) (iArr[1] + placeForPhoto.parentView.getHeight()))) + ((float) placeForPhoto.clipBottomAddition));
        if (height < 0) {
            height = 0;
        }
        indexOf = Math.max(i, abs);
        height = Math.max(height, abs);
        this.animationValues[0][0] = this.animatingImageView.getScaleX();
        this.animationValues[0][1] = this.animatingImageView.getScaleY();
        this.animationValues[0][2] = this.animatingImageView.getTranslationX();
        this.animationValues[0][3] = this.animatingImageView.getTranslationY();
        float[][] fArr = this.animationValues;
        float[] fArr2 = fArr[0];
        f = (float) animatedOrientation;
        float f6 = placeForPhoto.scale;
        fArr2[4] = f * f6;
        fArr[0][5] = ((float) indexOf) * f6;
        fArr[0][6] = ((float) height) * f6;
        int[] radius = this.animatingImageView.getRadius();
        for (indexOf = 0; indexOf < 4; indexOf++) {
            this.animationValues[0][indexOf + 7] = radius != null ? (float) radius[indexOf] : 0.0f;
        }
        float[][] fArr3 = this.animationValues;
        float[] fArr4 = fArr3[0];
        float f7 = (float) abs;
        f3 = placeForPhoto.scale;
        fArr4[11] = f7 * f3;
        fArr3[0][12] = f * f3;
        fArr3[1][0] = f2;
        fArr3[1][1] = f2;
        fArr3[1][2] = f4;
        fArr3[1][3] = f5;
        fArr3[1][4] = 0.0f;
        fArr3[1][5] = 0.0f;
        fArr3[1][6] = 0.0f;
        fArr3[1][7] = 0.0f;
        fArr3[1][8] = 0.0f;
        fArr3[1][9] = 0.0f;
        fArr3[1][10] = 0.0f;
        fArr3[1][11] = 0.0f;
        fArr3[1][12] = 0.0f;
        this.photoContainerView.setVisibility(0);
        this.photoContainerBackground.setVisibility(0);
        this.animatingImageView.setAnimationProgress(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        r5 = new Animator[6];
        r5[0] = ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f});
        r5[1] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, new int[]{0, 255});
        r5[2] = ObjectAnimator.ofFloat(this.actionBar, View.ALPHA, new float[]{0.0f, 1.0f});
        r5[3] = ObjectAnimator.ofFloat(this.bottomLayout, View.ALPHA, new float[]{0.0f, 1.0f});
        r5[4] = ObjectAnimator.ofFloat(this.captionTextView, View.ALPHA, new float[]{0.0f, 1.0f});
        r5[5] = ObjectAnimator.ofFloat(this.groupedPhotosListView, View.ALPHA, new float[]{0.0f, 1.0f});
        animatorSet.playTogether(r5);
        this.photoAnimationEndRunnable = new -$$Lambda$ArticleViewer$wjgr--NlytFL5AA3PNu0tBf0N7k(this);
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$34$ZoCkrtmlqMoaOJjBk1AdF0rd5qk(this));
            }

            public /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$34() {
                NotificationCenter.getInstance(ArticleViewer.this.currentAccount).setAnimationInProgress(false);
                if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                    ArticleViewer.this.photoAnimationEndRunnable.run();
                    ArticleViewer.this.photoAnimationEndRunnable = null;
                }
            }
        });
        this.photoTransitionAnimationStartTime = System.currentTimeMillis();
        AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$4EJ_1gZomS2QCHmPBRPzaljZRFs(this, animatorSet));
        if (VERSION.SDK_INT >= 18) {
            this.photoContainerView.setLayerType(2, null);
        }
        this.photoBackgroundDrawable.drawRunnable = new -$$Lambda$ArticleViewer$uz8EqFJ0mLINMBkYhdVE3EcZ4aI(this, placeForPhoto);
        return true;
    }

    public /* synthetic */ void lambda$openPhoto$44$ArticleViewer() {
        FrameLayoutDrawer frameLayoutDrawer = this.photoContainerView;
        if (frameLayoutDrawer != null) {
            if (VERSION.SDK_INT >= 18) {
                frameLayoutDrawer.setLayerType(0, null);
            }
            this.photoAnimationInProgress = 0;
            this.photoTransitionAnimationStartTime = 0;
            setImages();
            this.photoContainerView.invalidate();
            this.animatingImageView.setVisibility(8);
            PlaceProviderObject placeProviderObject = this.showAfterAnimation;
            if (placeProviderObject != null) {
                placeProviderObject.imageReceiver.setVisible(true, true);
            }
            placeProviderObject = this.hideAfterAnimation;
            if (placeProviderObject != null) {
                placeProviderObject.imageReceiver.setVisible(false, true);
            }
        }
    }

    public /* synthetic */ void lambda$openPhoto$45$ArticleViewer(AnimatorSet animatorSet) {
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        animatorSet.start();
    }

    public /* synthetic */ void lambda$openPhoto$46$ArticleViewer(PlaceProviderObject placeProviderObject) {
        this.disableShowCheck = false;
        placeProviderObject.imageReceiver.setVisible(false, true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00c4  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0106  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0122  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x02e8  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0153  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0387  */
    public void closePhoto(boolean r18) {
        /*
        r17 = this;
        r0 = r17;
        r1 = r0.parentActivity;
        if (r1 == 0) goto L_0x03bd;
    L_0x0006:
        r1 = r0.isPhotoVisible;
        if (r1 == 0) goto L_0x03bd;
    L_0x000a:
        r1 = r17.checkPhotoAnimation();
        if (r1 == 0) goto L_0x0012;
    L_0x0010:
        goto L_0x03bd;
    L_0x0012:
        r17.releasePlayer();
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.NotificationCenter.getInstance(r1);
        r2 = org.telegram.messenger.NotificationCenter.fileDidFailToLoad;
        r1.removeObserver(r0, r2);
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.NotificationCenter.getInstance(r1);
        r2 = org.telegram.messenger.NotificationCenter.fileDidLoad;
        r1.removeObserver(r0, r2);
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.NotificationCenter.getInstance(r1);
        r2 = org.telegram.messenger.NotificationCenter.FileLoadProgressChanged;
        r1.removeObserver(r0, r2);
        r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r2 = org.telegram.messenger.NotificationCenter.emojiDidLoad;
        r1.removeObserver(r0, r2);
        r1 = 0;
        r0.isActionBarVisible = r1;
        r2 = r0.velocityTracker;
        r3 = 0;
        if (r2 == 0) goto L_0x004c;
    L_0x0047:
        r2.recycle();
        r0.velocityTracker = r3;
    L_0x004c:
        r2 = r0.currentMedia;
        r2 = r0.getPlaceForPhoto(r2);
        if (r18 == 0) goto L_0x0391;
    L_0x0054:
        r5 = 1;
        r0.photoAnimationInProgress = r5;
        r6 = r0.animatingImageView;
        r6.setVisibility(r1);
        r6 = r0.photoContainerView;
        r6.invalidate();
        r6 = new android.animation.AnimatorSet;
        r6.<init>();
        r7 = r0.animatingImageView;
        r7 = r7.getLayoutParams();
        r8 = r0.centerImage;
        r8 = r8.getOrientation();
        if (r2 == 0) goto L_0x007d;
    L_0x0074:
        r9 = r2.imageReceiver;
        if (r9 == 0) goto L_0x007d;
    L_0x0078:
        r9 = r9.getAnimatedOrientation();
        goto L_0x007e;
    L_0x007d:
        r9 = 0;
    L_0x007e:
        if (r9 == 0) goto L_0x0081;
    L_0x0080:
        r8 = r9;
    L_0x0081:
        r9 = r0.animatingImageView;
        r9.setOrientation(r8);
        if (r2 == 0) goto L_0x00a4;
    L_0x0088:
        r8 = r2.imageReceiver;
        r8 = r8.getDrawRegion();
        r9 = r8.width();
        r9 = (int) r9;
        r7.width = r9;
        r9 = r8.height();
        r9 = (int) r9;
        r7.height = r9;
        r9 = r0.animatingImageView;
        r10 = r2.thumb;
        r9.setImageBitmap(r10);
        goto L_0x00c0;
    L_0x00a4:
        r8 = r0.centerImage;
        r8 = r8.getImageWidth();
        r7.width = r8;
        r8 = r0.centerImage;
        r8 = r8.getImageHeight();
        r7.height = r8;
        r8 = r0.animatingImageView;
        r9 = r0.centerImage;
        r9 = r9.getBitmapSafe();
        r8.setImageBitmap(r9);
        r8 = r3;
    L_0x00c0:
        r9 = r7.width;
        if (r9 != 0) goto L_0x00c6;
    L_0x00c4:
        r7.width = r5;
    L_0x00c6:
        r9 = r7.height;
        if (r9 != 0) goto L_0x00cc;
    L_0x00ca:
        r7.height = r5;
    L_0x00cc:
        r9 = r0.animatingImageView;
        r9.setLayoutParams(r7);
        r9 = org.telegram.messenger.AndroidUtilities.displaySize;
        r10 = r9.x;
        r10 = (float) r10;
        r11 = r7.width;
        r11 = (float) r11;
        r10 = r10 / r11;
        r9 = r9.y;
        r9 = (float) r9;
        r11 = r7.height;
        r11 = (float) r11;
        r9 = r9 / r11;
        r11 = (r10 > r9 ? 1 : (r10 == r9 ? 0 : -1));
        if (r11 <= 0) goto L_0x00e6;
    L_0x00e5:
        goto L_0x00e7;
    L_0x00e6:
        r9 = r10;
    L_0x00e7:
        r10 = r7.width;
        r10 = (float) r10;
        r11 = r0.scale;
        r10 = r10 * r11;
        r10 = r10 * r9;
        r7 = r7.height;
        r7 = (float) r7;
        r7 = r7 * r11;
        r7 = r7 * r9;
        r11 = org.telegram.messenger.AndroidUtilities.displaySize;
        r11 = r11.x;
        r11 = (float) r11;
        r11 = r11 - r10;
        r10 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r11 = r11 / r10;
        r12 = android.os.Build.VERSION.SDK_INT;
        r13 = 21;
        if (r12 < r13) goto L_0x0112;
    L_0x0106:
        r12 = r0.lastInsets;
        if (r12 == 0) goto L_0x0112;
    L_0x010a:
        r12 = (android.view.WindowInsets) r12;
        r12 = r12.getSystemWindowInsetLeft();
        r12 = (float) r12;
        r11 = r11 + r12;
    L_0x0112:
        r12 = r0.hasCutout;
        if (r12 == 0) goto L_0x0122;
    L_0x0116:
        r12 = org.telegram.messenger.AndroidUtilities.displaySize;
        r12 = r12.y;
        r12 = (float) r12;
        r12 = r12 - r7;
        r12 = r12 / r10;
        r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r7 = (float) r7;
        r12 = r12 + r7;
        goto L_0x012c;
    L_0x0122:
        r12 = org.telegram.messenger.AndroidUtilities.displaySize;
        r12 = r12.y;
        r13 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r12 = r12 + r13;
        r12 = (float) r12;
        r12 = r12 - r7;
        r12 = r12 / r10;
    L_0x012c:
        r7 = r0.animatingImageView;
        r10 = r0.translationX;
        r11 = r11 + r10;
        r7.setTranslationX(r11);
        r7 = r0.animatingImageView;
        r10 = r0.translationY;
        r12 = r12 + r10;
        r7.setTranslationY(r12);
        r7 = r0.animatingImageView;
        r10 = r0.scale;
        r10 = r10 * r9;
        r7.setScaleX(r10);
        r7 = r0.animatingImageView;
        r10 = r0.scale;
        r10 = r10 * r9;
        r7.setScaleY(r10);
        r11 = 3;
        r12 = 2;
        r13 = 0;
        if (r2 == 0) goto L_0x02e8;
    L_0x0153:
        r14 = r2.imageReceiver;
        r14.setVisible(r1, r5);
        r14 = r2.imageReceiver;
        r14 = r14.isAspectFit();
        if (r14 == 0) goto L_0x0162;
    L_0x0160:
        r14 = 0;
        goto L_0x0171;
    L_0x0162:
        r14 = r8.left;
        r15 = r2.imageReceiver;
        r15 = r15.getImageX();
        r15 = (float) r15;
        r14 = r14 - r15;
        r14 = java.lang.Math.abs(r14);
        r14 = (int) r14;
    L_0x0171:
        r15 = r8.top;
        r3 = r2.imageReceiver;
        r3 = r3.getImageY();
        r3 = (float) r3;
        r15 = r15 - r3;
        r3 = java.lang.Math.abs(r15);
        r3 = (int) r3;
        r15 = new int[r12];
        r7 = r2.parentView;
        r7.getLocationInWindow(r15);
        r7 = r15[r5];
        r7 = (float) r7;
        r9 = r2.viewY;
        r9 = (float) r9;
        r10 = r8.top;
        r9 = r9 + r10;
        r7 = r7 - r9;
        r9 = r2.clipTopAddition;
        r9 = (float) r9;
        r7 = r7 + r9;
        r7 = (int) r7;
        if (r7 >= 0) goto L_0x0199;
    L_0x0198:
        r7 = 0;
    L_0x0199:
        r9 = r2.viewY;
        r9 = (float) r9;
        r10 = r8.top;
        r9 = r9 + r10;
        r4 = r8.bottom;
        r4 = r4 - r10;
        r9 = r9 + r4;
        r4 = r15[r5];
        r10 = r2.parentView;
        r10 = r10.getHeight();
        r4 = r4 + r10;
        r4 = (float) r4;
        r9 = r9 - r4;
        r4 = r2.clipBottomAddition;
        r4 = (float) r4;
        r9 = r9 + r4;
        r4 = (int) r9;
        if (r4 >= 0) goto L_0x01b6;
    L_0x01b5:
        r4 = 0;
    L_0x01b6:
        r7 = java.lang.Math.max(r7, r3);
        r4 = java.lang.Math.max(r4, r3);
        r9 = r0.animationValues;
        r9 = r9[r1];
        r10 = r0.animatingImageView;
        r10 = r10.getScaleX();
        r9[r1] = r10;
        r9 = r0.animationValues;
        r9 = r9[r1];
        r10 = r0.animatingImageView;
        r10 = r10.getScaleY();
        r9[r5] = r10;
        r9 = r0.animationValues;
        r9 = r9[r1];
        r10 = r0.animatingImageView;
        r10 = r10.getTranslationX();
        r9[r12] = r10;
        r9 = r0.animationValues;
        r9 = r9[r1];
        r10 = r0.animatingImageView;
        r10 = r10.getTranslationY();
        r9[r11] = r10;
        r9 = r0.animationValues;
        r10 = r9[r1];
        r15 = 4;
        r10[r15] = r13;
        r10 = r9[r1];
        r15 = 5;
        r10[r15] = r13;
        r10 = r9[r1];
        r15 = 6;
        r10[r15] = r13;
        r10 = r9[r1];
        r15 = 7;
        r10[r15] = r13;
        r10 = r9[r1];
        r15 = 8;
        r10[r15] = r13;
        r10 = r9[r1];
        r15 = 9;
        r10[r15] = r13;
        r10 = r9[r1];
        r15 = 10;
        r10[r15] = r13;
        r10 = r9[r1];
        r15 = 11;
        r10[r15] = r13;
        r10 = r9[r1];
        r16 = 12;
        r10[r16] = r13;
        r10 = r9[r5];
        r13 = r2.scale;
        r10[r1] = r13;
        r10 = r9[r5];
        r10[r5] = r13;
        r10 = r9[r5];
        r1 = r2.viewX;
        r1 = (float) r1;
        r15 = r8.left;
        r15 = r15 * r13;
        r1 = r1 + r15;
        r10[r12] = r1;
        r1 = r9[r5];
        r10 = r2.viewY;
        r10 = (float) r10;
        r8 = r8.top;
        r8 = r8 * r13;
        r10 = r10 + r8;
        r1[r11] = r10;
        r1 = r9[r5];
        r8 = (float) r14;
        r10 = r8 * r13;
        r14 = 4;
        r1[r14] = r10;
        r1 = r9[r5];
        r7 = (float) r7;
        r7 = r7 * r13;
        r10 = 5;
        r1[r10] = r7;
        r1 = r9[r5];
        r4 = (float) r4;
        r4 = r4 * r13;
        r7 = 6;
        r1[r7] = r4;
        r1 = 0;
    L_0x025d:
        if (r1 >= r14) goto L_0x0274;
    L_0x025f:
        r4 = r0.animationValues;
        r4 = r4[r5];
        r7 = r1 + 7;
        r9 = r2.radius;
        if (r9 == 0) goto L_0x026d;
    L_0x0269:
        r9 = r9[r1];
        r13 = (float) r9;
        goto L_0x026e;
    L_0x026d:
        r13 = 0;
    L_0x026e:
        r4[r7] = r13;
        r1 = r1 + 1;
        r14 = 4;
        goto L_0x025d;
    L_0x0274:
        r1 = r0.animationValues;
        r4 = r1[r5];
        r3 = (float) r3;
        r7 = r2.scale;
        r3 = r3 * r7;
        r9 = 11;
        r4[r9] = r3;
        r1 = r1[r5];
        r8 = r8 * r7;
        r1[r16] = r8;
        r1 = 6;
        r1 = new android.animation.Animator[r1];
        r3 = r0.animatingImageView;
        r4 = new float[r12];
        r4 = {0, NUM};
        r7 = "animationProgress";
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r4);
        r4 = 0;
        r1[r4] = r3;
        r3 = r0.photoBackgroundDrawable;
        r7 = org.telegram.ui.Components.AnimationProperties.COLOR_DRAWABLE_ALPHA;
        r8 = new int[r5];
        r8[r4] = r4;
        r3 = android.animation.ObjectAnimator.ofInt(r3, r7, r8);
        r1[r5] = r3;
        r3 = r0.actionBar;
        r7 = android.view.View.ALPHA;
        r8 = new float[r5];
        r9 = 0;
        r8[r4] = r9;
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r8);
        r1[r12] = r3;
        r3 = r0.bottomLayout;
        r7 = android.view.View.ALPHA;
        r8 = new float[r5];
        r8[r4] = r9;
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r8);
        r1[r11] = r3;
        r3 = r0.captionTextView;
        r7 = android.view.View.ALPHA;
        r8 = new float[r5];
        r8[r4] = r9;
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r8);
        r7 = 4;
        r1[r7] = r3;
        r3 = r0.groupedPhotosListView;
        r7 = android.view.View.ALPHA;
        r5 = new float[r5];
        r5[r4] = r9;
        r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r5);
        r4 = 5;
        r1[r4] = r3;
        r6.playTogether(r1);
        goto L_0x0367;
    L_0x02e8:
        r1 = org.telegram.messenger.AndroidUtilities.displaySize;
        r1 = r1.y;
        r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r1 = r1 + r3;
        r3 = 7;
        r3 = new android.animation.Animator[r3];
        r4 = r0.photoBackgroundDrawable;
        r7 = org.telegram.ui.Components.AnimationProperties.COLOR_DRAWABLE_ALPHA;
        r8 = new int[r5];
        r9 = 0;
        r8[r9] = r9;
        r4 = android.animation.ObjectAnimator.ofInt(r4, r7, r8);
        r3[r9] = r4;
        r4 = r0.animatingImageView;
        r7 = android.view.View.ALPHA;
        r8 = new float[r5];
        r10 = 0;
        r8[r9] = r10;
        r4 = android.animation.ObjectAnimator.ofFloat(r4, r7, r8);
        r3[r5] = r4;
        r4 = r0.animatingImageView;
        r7 = android.view.View.TRANSLATION_Y;
        r8 = new float[r5];
        r9 = r0.translationY;
        r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1));
        if (r9 < 0) goto L_0x031d;
    L_0x031c:
        goto L_0x031e;
    L_0x031d:
        r1 = -r1;
    L_0x031e:
        r1 = (float) r1;
        r9 = 0;
        r8[r9] = r1;
        r1 = android.animation.ObjectAnimator.ofFloat(r4, r7, r8);
        r3[r12] = r1;
        r1 = r0.actionBar;
        r4 = android.view.View.ALPHA;
        r7 = new float[r5];
        r8 = 0;
        r7[r9] = r8;
        r1 = android.animation.ObjectAnimator.ofFloat(r1, r4, r7);
        r3[r11] = r1;
        r1 = r0.bottomLayout;
        r4 = android.view.View.ALPHA;
        r7 = new float[r5];
        r7[r9] = r8;
        r1 = android.animation.ObjectAnimator.ofFloat(r1, r4, r7);
        r4 = 4;
        r3[r4] = r1;
        r1 = r0.captionTextView;
        r4 = android.view.View.ALPHA;
        r7 = new float[r5];
        r7[r9] = r8;
        r1 = android.animation.ObjectAnimator.ofFloat(r1, r4, r7);
        r4 = 5;
        r3[r4] = r1;
        r1 = r0.groupedPhotosListView;
        r4 = android.view.View.ALPHA;
        r5 = new float[r5];
        r5[r9] = r8;
        r1 = android.animation.ObjectAnimator.ofFloat(r1, r4, r5);
        r4 = 6;
        r3[r4] = r1;
        r6.playTogether(r3);
    L_0x0367:
        r1 = new org.telegram.ui.-$$Lambda$ArticleViewer$e_C3rrv325-N7tDHp4aZh-o9khc;
        r1.<init>(r0, r2);
        r0.photoAnimationEndRunnable = r1;
        r1 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r6.setDuration(r1);
        r1 = new org.telegram.ui.ArticleViewer$35;
        r1.<init>();
        r6.addListener(r1);
        r1 = java.lang.System.currentTimeMillis();
        r0.photoTransitionAnimationStartTime = r1;
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 18;
        if (r1 < r2) goto L_0x038d;
    L_0x0387:
        r1 = r0.photoContainerView;
        r2 = 0;
        r1.setLayerType(r12, r2);
    L_0x038d:
        r6.start();
        goto L_0x03ae;
    L_0x0391:
        r1 = r0.photoContainerView;
        r3 = 4;
        r1.setVisibility(r3);
        r1 = r0.photoContainerBackground;
        r1.setVisibility(r3);
        r1 = 0;
        r0.photoAnimationInProgress = r1;
        r0.onPhotoClosed(r2);
        r1 = r0.photoContainerView;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1.setScaleX(r2);
        r1 = r0.photoContainerView;
        r1.setScaleY(r2);
    L_0x03ae:
        r1 = r0.currentAnimation;
        if (r1 == 0) goto L_0x03bd;
    L_0x03b2:
        r2 = 0;
        r1.setSecondParentView(r2);
        r0.currentAnimation = r2;
        r1 = r0.centerImage;
        r1.setImageBitmap(r2);
    L_0x03bd:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.closePhoto(boolean):void");
    }

    public /* synthetic */ void lambda$closePhoto$47$ArticleViewer(PlaceProviderObject placeProviderObject) {
        if (VERSION.SDK_INT >= 18) {
            this.photoContainerView.setLayerType(0, null);
        }
        this.photoContainerView.setVisibility(4);
        this.photoContainerBackground.setVisibility(4);
        this.photoAnimationInProgress = 0;
        onPhotoClosed(placeProviderObject);
    }

    private void onPhotoClosed(PlaceProviderObject placeProviderObject) {
        this.isPhotoVisible = false;
        this.disableShowCheck = true;
        this.currentMedia = null;
        BitmapHolder bitmapHolder = this.currentThumb;
        if (bitmapHolder != null) {
            bitmapHolder.release();
            this.currentThumb = null;
        }
        AnimatedFileDrawable animatedFileDrawable = this.currentAnimation;
        if (animatedFileDrawable != null) {
            animatedFileDrawable.setSecondParentView(null);
            this.currentAnimation = null;
        }
        for (int i = 0; i < 3; i++) {
            RadialProgressView[] radialProgressViewArr = this.radialProgressViews;
            if (radialProgressViewArr[i] != null) {
                radialProgressViewArr[i].setBackgroundState(-1, false);
            }
        }
        this.centerImage.setImageBitmap(null);
        this.leftImage.setImageBitmap(null);
        this.rightImage.setImageBitmap(null);
        this.photoContainerView.post(new -$$Lambda$ArticleViewer$KFgO7ulXm7M2ldz3US4i6x5HZGI(this));
        this.disableShowCheck = false;
        if (placeProviderObject != null) {
            placeProviderObject.imageReceiver.setVisible(true, true);
        }
        this.groupedPhotosListView.clear();
    }

    public /* synthetic */ void lambda$onPhotoClosed$48$ArticleViewer() {
        this.animatingImageView.setImageBitmap(null);
    }

    public void onPause() {
        if (this.currentAnimation != null) {
            closePhoto(false);
        }
    }

    private void updateMinMax(float f) {
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

    private int getContainerViewWidth() {
        return this.photoContainerView.getWidth();
    }

    private int getContainerViewHeight() {
        return this.photoContainerView.getHeight();
    }

    /* JADX WARNING: Removed duplicated region for block: B:98:0x01fc  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01fc  */
    private boolean processTouchEvent(android.view.MotionEvent r13) {
        /*
        r12 = this;
        r0 = r12.photoAnimationInProgress;
        r1 = 0;
        if (r0 != 0) goto L_0x03f3;
    L_0x0005:
        r2 = r12.animationStartTime;
        r4 = 0;
        r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r0 == 0) goto L_0x000f;
    L_0x000d:
        goto L_0x03f3;
    L_0x000f:
        r0 = r13.getPointerCount();
        r2 = 1;
        if (r0 != r2) goto L_0x002c;
    L_0x0016:
        r0 = r12.gestureDetector;
        r0 = r0.onTouchEvent(r13);
        if (r0 == 0) goto L_0x002c;
    L_0x001e:
        r0 = r12.doubleTap;
        if (r0 == 0) goto L_0x002c;
    L_0x0022:
        r12.doubleTap = r1;
        r12.moving = r1;
        r12.zooming = r1;
        r12.checkMinMax(r1);
        return r2;
    L_0x002c:
        r0 = r13.getActionMasked();
        r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = 2;
        if (r0 == 0) goto L_0x0368;
    L_0x0035:
        r0 = r13.getActionMasked();
        r7 = 5;
        if (r0 != r7) goto L_0x003e;
    L_0x003c:
        goto L_0x0368;
    L_0x003e:
        r0 = r13.getActionMasked();
        r7 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = 0;
        if (r0 != r6) goto L_0x0218;
    L_0x0049:
        r0 = r12.canZoom;
        if (r0 == 0) goto L_0x00cc;
    L_0x004d:
        r0 = r13.getPointerCount();
        if (r0 != r6) goto L_0x00cc;
    L_0x0053:
        r0 = r12.draggingDown;
        if (r0 != 0) goto L_0x00cc;
    L_0x0057:
        r0 = r12.zooming;
        if (r0 == 0) goto L_0x00cc;
    L_0x005b:
        r0 = r12.changingPage;
        if (r0 != 0) goto L_0x00cc;
    L_0x005f:
        r12.discardTap = r2;
        r0 = r13.getX(r2);
        r3 = r13.getX(r1);
        r0 = r0 - r3;
        r3 = (double) r0;
        r0 = r13.getY(r2);
        r13 = r13.getY(r1);
        r0 = r0 - r13;
        r7 = (double) r0;
        r2 = java.lang.Math.hypot(r3, r7);
        r13 = (float) r2;
        r0 = r12.pinchStartDistance;
        r13 = r13 / r0;
        r0 = r12.pinchStartScale;
        r13 = r13 * r0;
        r12.scale = r13;
        r13 = r12.pinchCenterX;
        r0 = r12.getContainerViewWidth();
        r0 = r0 / r6;
        r0 = (float) r0;
        r13 = r13 - r0;
        r0 = r12.pinchCenterX;
        r2 = r12.getContainerViewWidth();
        r2 = r2 / r6;
        r2 = (float) r2;
        r0 = r0 - r2;
        r2 = r12.pinchStartX;
        r0 = r0 - r2;
        r2 = r12.scale;
        r3 = r12.pinchStartScale;
        r2 = r2 / r3;
        r0 = r0 * r2;
        r13 = r13 - r0;
        r12.translationX = r13;
        r13 = r12.pinchCenterY;
        r0 = r12.getContainerViewHeight();
        r0 = r0 / r6;
        r0 = (float) r0;
        r13 = r13 - r0;
        r0 = r12.pinchCenterY;
        r2 = r12.getContainerViewHeight();
        r2 = r2 / r6;
        r2 = (float) r2;
        r0 = r0 - r2;
        r2 = r12.pinchStartY;
        r0 = r0 - r2;
        r2 = r12.scale;
        r3 = r12.pinchStartScale;
        r3 = r2 / r3;
        r0 = r0 * r3;
        r13 = r13 - r0;
        r12.translationY = r13;
        r12.updateMinMax(r2);
        r13 = r12.photoContainerView;
        r13.invalidate();
        goto L_0x03f3;
    L_0x00cc:
        r0 = r13.getPointerCount();
        if (r0 != r2) goto L_0x03f3;
    L_0x00d2:
        r0 = r12.velocityTracker;
        if (r0 == 0) goto L_0x00d9;
    L_0x00d6:
        r0.addMovement(r13);
    L_0x00d9:
        r0 = r13.getX();
        r6 = r12.moveStartX;
        r0 = r0 - r6;
        r0 = java.lang.Math.abs(r0);
        r6 = r13.getY();
        r10 = r12.dragY;
        r6 = r6 - r10;
        r6 = java.lang.Math.abs(r6);
        r10 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r10 = (float) r10;
        r10 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1));
        if (r10 > 0) goto L_0x0101;
    L_0x00f8:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r10 = (float) r10;
        r10 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r10 <= 0) goto L_0x0103;
    L_0x0101:
        r12.discardTap = r2;
    L_0x0103:
        r10 = r12.canDragDown;
        if (r10 == 0) goto L_0x0133;
    L_0x0107:
        r10 = r12.draggingDown;
        if (r10 != 0) goto L_0x0133;
    L_0x010b:
        r10 = r12.scale;
        r10 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1));
        if (r10 != 0) goto L_0x0133;
    L_0x0111:
        r10 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r10;
        r10 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r10 < 0) goto L_0x0133;
    L_0x011c:
        r6 = r6 / r3;
        r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1));
        if (r0 <= 0) goto L_0x0133;
    L_0x0121:
        r12.draggingDown = r2;
        r12.moving = r1;
        r13 = r13.getY();
        r12.dragY = r13;
        r13 = r12.isActionBarVisible;
        if (r13 == 0) goto L_0x0132;
    L_0x012f:
        r12.toggleActionBar(r1, r2);
    L_0x0132:
        return r2;
    L_0x0133:
        r0 = r12.draggingDown;
        if (r0 == 0) goto L_0x0147;
    L_0x0137:
        r13 = r13.getY();
        r0 = r12.dragY;
        r13 = r13 - r0;
        r12.translationY = r13;
        r13 = r12.photoContainerView;
        r13.invalidate();
        goto L_0x03f3;
    L_0x0147:
        r0 = r12.invalidCoords;
        if (r0 != 0) goto L_0x0208;
    L_0x014b:
        r10 = r12.animationStartTime;
        r0 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x0208;
    L_0x0151:
        r0 = r12.moveStartX;
        r3 = r13.getX();
        r0 = r0 - r3;
        r3 = r12.moveStartY;
        r4 = r13.getY();
        r3 = r3 - r4;
        r4 = r12.moving;
        if (r4 != 0) goto L_0x0183;
    L_0x0163:
        r4 = r12.scale;
        r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x017d;
    L_0x0169:
        r4 = java.lang.Math.abs(r3);
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r4 = r4 + r5;
        r5 = java.lang.Math.abs(r0);
        r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1));
        if (r4 < 0) goto L_0x0183;
    L_0x017d:
        r4 = r12.scale;
        r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x03f3;
    L_0x0183:
        r4 = r12.moving;
        if (r4 != 0) goto L_0x018d;
    L_0x0187:
        r12.moving = r2;
        r12.canDragDown = r1;
        r0 = 0;
        r3 = 0;
    L_0x018d:
        r2 = r13.getX();
        r12.moveStartX = r2;
        r13 = r13.getY();
        r12.moveStartY = r13;
        r13 = r12.scale;
        r12.updateMinMax(r13);
        r13 = r12.translationX;
        r2 = r12.minX;
        r13 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r13 >= 0) goto L_0x01ae;
    L_0x01a6:
        r13 = r12.rightImage;
        r13 = r13.hasImageSet();
        if (r13 == 0) goto L_0x01be;
    L_0x01ae:
        r13 = r12.translationX;
        r2 = r12.maxX;
        r13 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r13 <= 0) goto L_0x01bf;
    L_0x01b6:
        r13 = r12.leftImage;
        r13 = r13.hasImageSet();
        if (r13 != 0) goto L_0x01bf;
    L_0x01be:
        r0 = r0 / r7;
    L_0x01bf:
        r13 = r12.maxY;
        r2 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1));
        if (r2 != 0) goto L_0x01de;
    L_0x01c5:
        r2 = r12.minY;
        r4 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r4 != 0) goto L_0x01de;
    L_0x01cb:
        r4 = r12.translationY;
        r5 = r4 - r3;
        r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1));
        if (r5 >= 0) goto L_0x01d6;
    L_0x01d3:
        r12.translationY = r2;
        goto L_0x01f1;
    L_0x01d6:
        r4 = r4 - r3;
        r2 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1));
        if (r2 <= 0) goto L_0x01ed;
    L_0x01db:
        r12.translationY = r13;
        goto L_0x01f1;
    L_0x01de:
        r13 = r12.translationY;
        r2 = r12.minY;
        r2 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x01ef;
    L_0x01e6:
        r2 = r12.maxY;
        r13 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r13 <= 0) goto L_0x01ed;
    L_0x01ec:
        goto L_0x01ef;
    L_0x01ed:
        r9 = r3;
        goto L_0x01f1;
    L_0x01ef:
        r9 = r3 / r7;
    L_0x01f1:
        r13 = r12.translationX;
        r13 = r13 - r0;
        r12.translationX = r13;
        r13 = r12.scale;
        r13 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1));
        if (r13 == 0) goto L_0x0201;
    L_0x01fc:
        r13 = r12.translationY;
        r13 = r13 - r9;
        r12.translationY = r13;
    L_0x0201:
        r13 = r12.photoContainerView;
        r13.invalidate();
        goto L_0x03f3;
    L_0x0208:
        r12.invalidCoords = r1;
        r0 = r13.getX();
        r12.moveStartX = r0;
        r13 = r13.getY();
        r12.moveStartY = r13;
        goto L_0x03f3;
    L_0x0218:
        r0 = r13.getActionMasked();
        r3 = 3;
        if (r0 == r3) goto L_0x022c;
    L_0x021f:
        r0 = r13.getActionMasked();
        if (r0 == r2) goto L_0x022c;
    L_0x0225:
        r0 = r13.getActionMasked();
        r4 = 6;
        if (r0 != r4) goto L_0x03f3;
    L_0x022c:
        r0 = r12.zooming;
        if (r0 == 0) goto L_0x02a7;
    L_0x0230:
        r12.invalidCoords = r2;
        r13 = r12.scale;
        r0 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1));
        if (r0 >= 0) goto L_0x023f;
    L_0x0238:
        r12.updateMinMax(r8);
        r12.animateTo(r8, r9, r9, r2);
        goto L_0x02a3;
    L_0x023f:
        r13 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1));
        if (r13 <= 0) goto L_0x02a0;
    L_0x0243:
        r13 = r12.pinchCenterX;
        r0 = r12.getContainerViewWidth();
        r0 = r0 / r6;
        r0 = (float) r0;
        r13 = r13 - r0;
        r0 = r12.pinchCenterX;
        r3 = r12.getContainerViewWidth();
        r3 = r3 / r6;
        r3 = (float) r3;
        r0 = r0 - r3;
        r3 = r12.pinchStartX;
        r0 = r0 - r3;
        r3 = r12.pinchStartScale;
        r3 = r7 / r3;
        r0 = r0 * r3;
        r13 = r13 - r0;
        r0 = r12.pinchCenterY;
        r3 = r12.getContainerViewHeight();
        r3 = r3 / r6;
        r3 = (float) r3;
        r0 = r0 - r3;
        r3 = r12.pinchCenterY;
        r4 = r12.getContainerViewHeight();
        r4 = r4 / r6;
        r4 = (float) r4;
        r3 = r3 - r4;
        r4 = r12.pinchStartY;
        r3 = r3 - r4;
        r4 = r12.pinchStartScale;
        r4 = r7 / r4;
        r3 = r3 * r4;
        r0 = r0 - r3;
        r12.updateMinMax(r7);
        r3 = r12.minX;
        r4 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1));
        if (r4 >= 0) goto L_0x0285;
    L_0x0284:
        goto L_0x028d;
    L_0x0285:
        r3 = r12.maxX;
        r4 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1));
        if (r4 <= 0) goto L_0x028c;
    L_0x028b:
        goto L_0x028d;
    L_0x028c:
        r3 = r13;
    L_0x028d:
        r13 = r12.minY;
        r4 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r4 >= 0) goto L_0x0294;
    L_0x0293:
        goto L_0x029c;
    L_0x0294:
        r13 = r12.maxY;
        r4 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r4 <= 0) goto L_0x029b;
    L_0x029a:
        goto L_0x029c;
    L_0x029b:
        r13 = r0;
    L_0x029c:
        r12.animateTo(r7, r3, r13, r2);
        goto L_0x02a3;
    L_0x02a0:
        r12.checkMinMax(r2);
    L_0x02a3:
        r12.zooming = r1;
        goto L_0x03f3;
    L_0x02a7:
        r0 = r12.draggingDown;
        if (r0 == 0) goto L_0x02cd;
    L_0x02ab:
        r0 = r12.dragY;
        r13 = r13.getY();
        r0 = r0 - r13;
        r13 = java.lang.Math.abs(r0);
        r0 = r12.getContainerViewHeight();
        r0 = (float) r0;
        r3 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r0 = r0 / r3;
        r13 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1));
        if (r13 <= 0) goto L_0x02c6;
    L_0x02c2:
        r12.closePhoto(r2);
        goto L_0x02c9;
    L_0x02c6:
        r12.animateTo(r8, r9, r9, r1);
    L_0x02c9:
        r12.draggingDown = r1;
        goto L_0x03f3;
    L_0x02cd:
        r13 = r12.moving;
        if (r13 == 0) goto L_0x03f3;
    L_0x02d1:
        r13 = r12.translationX;
        r0 = r12.translationY;
        r4 = r12.scale;
        r12.updateMinMax(r4);
        r12.moving = r1;
        r12.canDragDown = r2;
        r4 = r12.velocityTracker;
        if (r4 == 0) goto L_0x02f3;
    L_0x02e2:
        r5 = r12.scale;
        r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1));
        if (r5 != 0) goto L_0x02f3;
    L_0x02e8:
        r5 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r4.computeCurrentVelocity(r5);
        r4 = r12.velocityTracker;
        r9 = r4.getXVelocity();
    L_0x02f3:
        r4 = r12.translationX;
        r5 = r12.minX;
        r6 = r12.getContainerViewWidth();
        r6 = r6 / r3;
        r6 = (float) r6;
        r5 = r5 - r6;
        r6 = NUM; // 0x44228000 float:650.0 double:5.647722104E-315;
        r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1));
        if (r4 < 0) goto L_0x030f;
    L_0x0305:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = -r4;
        r4 = (float) r4;
        r4 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x031b;
    L_0x030f:
        r4 = r12.rightImage;
        r4 = r4.hasImageSet();
        if (r4 == 0) goto L_0x031b;
    L_0x0317:
        r12.goToNext();
        return r2;
    L_0x031b:
        r4 = r12.translationX;
        r5 = r12.maxX;
        r7 = r12.getContainerViewWidth();
        r7 = r7 / r3;
        r3 = (float) r7;
        r5 = r5 + r3;
        r3 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1));
        if (r3 > 0) goto L_0x0333;
    L_0x032a:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r3 = (float) r3;
        r3 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1));
        if (r3 <= 0) goto L_0x033f;
    L_0x0333:
        r3 = r12.leftImage;
        r3 = r3.hasImageSet();
        if (r3 == 0) goto L_0x033f;
    L_0x033b:
        r12.goToPrev();
        return r2;
    L_0x033f:
        r2 = r12.translationX;
        r3 = r12.minX;
        r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r4 >= 0) goto L_0x0348;
    L_0x0347:
        goto L_0x0350;
    L_0x0348:
        r3 = r12.maxX;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 <= 0) goto L_0x034f;
    L_0x034e:
        goto L_0x0350;
    L_0x034f:
        r3 = r13;
    L_0x0350:
        r13 = r12.translationY;
        r2 = r12.minY;
        r4 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r4 >= 0) goto L_0x0359;
    L_0x0358:
        goto L_0x0361;
    L_0x0359:
        r2 = r12.maxY;
        r13 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1));
        if (r13 <= 0) goto L_0x0360;
    L_0x035f:
        goto L_0x0361;
    L_0x0360:
        r2 = r0;
    L_0x0361:
        r13 = r12.scale;
        r12.animateTo(r13, r3, r2, r1);
        goto L_0x03f3;
    L_0x0368:
        r12.discardTap = r1;
        r0 = r12.scroller;
        r0 = r0.isFinished();
        if (r0 != 0) goto L_0x0377;
    L_0x0372:
        r0 = r12.scroller;
        r0.abortAnimation();
    L_0x0377:
        r0 = r12.draggingDown;
        if (r0 != 0) goto L_0x03f3;
    L_0x037b:
        r0 = r12.changingPage;
        if (r0 != 0) goto L_0x03f3;
    L_0x037f:
        r0 = r12.canZoom;
        if (r0 == 0) goto L_0x03d4;
    L_0x0383:
        r0 = r13.getPointerCount();
        if (r0 != r6) goto L_0x03d4;
    L_0x0389:
        r0 = r13.getX(r2);
        r4 = r13.getX(r1);
        r0 = r0 - r4;
        r4 = (double) r0;
        r0 = r13.getY(r2);
        r6 = r13.getY(r1);
        r0 = r0 - r6;
        r6 = (double) r0;
        r4 = java.lang.Math.hypot(r4, r6);
        r0 = (float) r4;
        r12.pinchStartDistance = r0;
        r0 = r12.scale;
        r12.pinchStartScale = r0;
        r0 = r13.getX(r1);
        r4 = r13.getX(r2);
        r0 = r0 + r4;
        r0 = r0 / r3;
        r12.pinchCenterX = r0;
        r0 = r13.getY(r1);
        r13 = r13.getY(r2);
        r0 = r0 + r13;
        r0 = r0 / r3;
        r12.pinchCenterY = r0;
        r13 = r12.translationX;
        r12.pinchStartX = r13;
        r13 = r12.translationY;
        r12.pinchStartY = r13;
        r12.zooming = r2;
        r12.moving = r1;
        r13 = r12.velocityTracker;
        if (r13 == 0) goto L_0x03f3;
    L_0x03d0:
        r13.clear();
        goto L_0x03f3;
    L_0x03d4:
        r0 = r13.getPointerCount();
        if (r0 != r2) goto L_0x03f3;
    L_0x03da:
        r0 = r13.getX();
        r12.moveStartX = r0;
        r13 = r13.getY();
        r12.moveStartY = r13;
        r12.dragY = r13;
        r12.draggingDown = r1;
        r12.canDragDown = r2;
        r13 = r12.velocityTracker;
        if (r13 == 0) goto L_0x03f3;
    L_0x03f0:
        r13.clear();
    L_0x03f3:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.processTouchEvent(android.view.MotionEvent):boolean");
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
        f = this.translationY;
        f3 = this.minY;
        if (f >= f3) {
            f3 = this.maxY;
            if (f <= f3) {
                f3 = f2;
            }
        }
        animateTo(this.scale, f4, f3, z);
    }

    private void goToNext() {
        float containerViewWidth = this.scale != 1.0f ? ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale : 0.0f;
        this.switchImageAfterAnimation = 1;
        animateTo(this.scale, ((this.minX - ((float) getContainerViewWidth())) - containerViewWidth) - ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
    }

    private void goToPrev() {
        float containerViewWidth = this.scale != 1.0f ? ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale : 0.0f;
        this.switchImageAfterAnimation = 2;
        animateTo(this.scale, ((this.maxX + ((float) getContainerViewWidth())) + containerViewWidth) + ((float) (AndroidUtilities.dp(30.0f) / 2)), this.translationY, false);
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
            AnimatorSet animatorSet = this.imageMoveAnimation;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f});
            animatorSet.playTogether(animatorArr);
            this.imageMoveAnimation.setInterpolator(this.interpolator);
            this.imageMoveAnimation.setDuration((long) i);
            this.imageMoveAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ArticleViewer.this.imageMoveAnimation = null;
                    ArticleViewer.this.photoContainerView.invalidate();
                }
            });
            this.imageMoveAnimation.start();
        }
    }

    @Keep
    public void setAnimationValue(float f) {
        this.animationValue = f;
        this.photoContainerView.invalidate();
    }

    @Keep
    public float getAnimationValue() {
        return this.animationValue;
    }

    /* JADX WARNING: Removed duplicated region for block: B:83:0x01b0  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:151:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x03a9  */
    private void drawContent(android.graphics.Canvas r19) {
        /*
        r18 = this;
        r0 = r18;
        r1 = r19;
        r2 = r0.photoAnimationInProgress;
        r3 = 1;
        if (r2 == r3) goto L_0x045b;
    L_0x0009:
        r4 = r0.isPhotoVisible;
        r5 = 2;
        if (r4 != 0) goto L_0x0012;
    L_0x000e:
        if (r2 == r5) goto L_0x0012;
    L_0x0010:
        goto L_0x045b;
    L_0x0012:
        r2 = r0.imageMoveAnimation;
        r4 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r6 = 0;
        r7 = 0;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r2 == 0) goto L_0x005c;
    L_0x001c:
        r2 = r0.scroller;
        r2 = r2.isFinished();
        if (r2 != 0) goto L_0x0029;
    L_0x0024:
        r2 = r0.scroller;
        r2.abortAnimation();
    L_0x0029:
        r2 = r0.scale;
        r9 = r0.animateToScale;
        r10 = r9 - r2;
        r11 = r0.animationValue;
        r10 = r10 * r11;
        r10 = r10 + r2;
        r12 = r0.translationX;
        r13 = r0.animateToX;
        r13 = r13 - r12;
        r13 = r13 * r11;
        r13 = r13 + r12;
        r14 = r0.translationY;
        r15 = r0.animateToY;
        r15 = r15 - r14;
        r15 = r15 * r11;
        r11 = r14 + r15;
        r9 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1));
        if (r9 != 0) goto L_0x0053;
    L_0x0049:
        r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
        if (r2 != 0) goto L_0x0053;
    L_0x004d:
        r2 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1));
        if (r2 != 0) goto L_0x0053;
    L_0x0051:
        r2 = r11;
        goto L_0x0055;
    L_0x0053:
        r2 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
    L_0x0055:
        r9 = r0.photoContainerView;
        r9.invalidate();
        goto L_0x00fd;
    L_0x005c:
        r9 = r0.animationStartTime;
        r11 = 0;
        r2 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
        if (r2 == 0) goto L_0x0079;
    L_0x0064:
        r2 = r0.animateToX;
        r0.translationX = r2;
        r2 = r0.animateToY;
        r0.translationY = r2;
        r2 = r0.animateToScale;
        r0.scale = r2;
        r0.animationStartTime = r11;
        r2 = r0.scale;
        r0.updateMinMax(r2);
        r0.zoomAnimation = r7;
    L_0x0079:
        r2 = r0.scroller;
        r2 = r2.isFinished();
        if (r2 != 0) goto L_0x00d4;
    L_0x0081:
        r2 = r0.scroller;
        r2 = r2.computeScrollOffset();
        if (r2 == 0) goto L_0x00d4;
    L_0x0089:
        r2 = r0.scroller;
        r2 = r2.getStartX();
        r2 = (float) r2;
        r9 = r0.maxX;
        r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r2 >= 0) goto L_0x00ac;
    L_0x0096:
        r2 = r0.scroller;
        r2 = r2.getStartX();
        r2 = (float) r2;
        r9 = r0.minX;
        r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r2 <= 0) goto L_0x00ac;
    L_0x00a3:
        r2 = r0.scroller;
        r2 = r2.getCurrX();
        r2 = (float) r2;
        r0.translationX = r2;
    L_0x00ac:
        r2 = r0.scroller;
        r2 = r2.getStartY();
        r2 = (float) r2;
        r9 = r0.maxY;
        r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r2 >= 0) goto L_0x00cf;
    L_0x00b9:
        r2 = r0.scroller;
        r2 = r2.getStartY();
        r2 = (float) r2;
        r9 = r0.minY;
        r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r2 <= 0) goto L_0x00cf;
    L_0x00c6:
        r2 = r0.scroller;
        r2 = r2.getCurrY();
        r2 = (float) r2;
        r0.translationY = r2;
    L_0x00cf:
        r2 = r0.photoContainerView;
        r2.invalidate();
    L_0x00d4:
        r2 = r0.switchImageAfterAnimation;
        if (r2 == 0) goto L_0x00ef;
    L_0x00d8:
        if (r2 != r3) goto L_0x00e3;
    L_0x00da:
        r2 = new org.telegram.ui.-$$Lambda$ArticleViewer$C9__HRnKC3mNKZu-CoBnDk-VDcY;
        r2.<init>(r0);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
        goto L_0x00ed;
    L_0x00e3:
        if (r2 != r5) goto L_0x00ed;
    L_0x00e5:
        r2 = new org.telegram.ui.-$$Lambda$ArticleViewer$XHT4lMELACLASSNAMETU_NKXLDnneb7DY;
        r2.<init>(r0);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
    L_0x00ed:
        r0.switchImageAfterAnimation = r7;
    L_0x00ef:
        r10 = r0.scale;
        r2 = r0.translationY;
        r13 = r0.translationX;
        r9 = r0.moving;
        r11 = r2;
        if (r9 != 0) goto L_0x00fb;
    L_0x00fa:
        goto L_0x00fd;
    L_0x00fb:
        r2 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
    L_0x00fd:
        r9 = r0.photoAnimationInProgress;
        if (r9 == r5) goto L_0x013a;
    L_0x0101:
        r9 = r0.scale;
        r9 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1));
        if (r9 != 0) goto L_0x0133;
    L_0x0107:
        r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r4 == 0) goto L_0x0133;
    L_0x010b:
        r4 = r0.zoomAnimation;
        if (r4 != 0) goto L_0x0133;
    L_0x010f:
        r4 = r18.getContainerViewHeight();
        r4 = (float) r4;
        r9 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = r4 / r9;
        r9 = r0.photoBackgroundDrawable;
        r12 = NUM; // 0x42fe0000 float:127.0 double:5.553013277E-315;
        r14 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r2 = java.lang.Math.abs(r2);
        r2 = java.lang.Math.min(r2, r4);
        r2 = r2 / r4;
        r2 = r8 - r2;
        r2 = r2 * r14;
        r2 = java.lang.Math.max(r12, r2);
        r2 = (int) r2;
        r9.setAlpha(r2);
        goto L_0x013a;
    L_0x0133:
        r2 = r0.photoBackgroundDrawable;
        r4 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r2.setAlpha(r4);
    L_0x013a:
        r2 = 0;
        r4 = r0.scale;
        r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r4 < 0) goto L_0x016e;
    L_0x0141:
        r4 = r0.zoomAnimation;
        if (r4 != 0) goto L_0x016e;
    L_0x0145:
        r4 = r0.zooming;
        if (r4 != 0) goto L_0x016e;
    L_0x0149:
        r4 = r0.maxX;
        r9 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r12 = (float) r12;
        r4 = r4 + r12;
        r4 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1));
        if (r4 <= 0) goto L_0x015a;
    L_0x0157:
        r2 = r0.leftImage;
        goto L_0x016e;
    L_0x015a:
        r4 = r0.minX;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = (float) r9;
        r4 = r4 - r9;
        r4 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1));
        if (r4 >= 0) goto L_0x0169;
    L_0x0166:
        r2 = r0.rightImage;
        goto L_0x016e;
    L_0x0169:
        r4 = r0.groupedPhotosListView;
        r4.setMoveProgress(r6);
    L_0x016e:
        if (r2 == 0) goto L_0x0172;
    L_0x0170:
        r4 = 1;
        goto L_0x0173;
    L_0x0172:
        r4 = 0;
    L_0x0173:
        r0.changingPage = r4;
        r4 = r0.rightImage;
        r9 = NUM; // 0x3e99999a float:0.3 double:5.188942835E-315;
        r14 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        if (r2 != r4) goto L_0x024a;
    L_0x017e:
        r4 = r0.zoomAnimation;
        if (r4 != 0) goto L_0x01a6;
    L_0x0182:
        r4 = r0.minX;
        r15 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1));
        if (r15 >= 0) goto L_0x01a6;
    L_0x0188:
        r4 = r4 - r13;
        r15 = r19.getWidth();
        r15 = (float) r15;
        r4 = r4 / r15;
        r4 = java.lang.Math.min(r8, r4);
        r15 = r8 - r4;
        r15 = r15 * r9;
        r7 = r19.getWidth();
        r7 = -r7;
        r16 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r16 = r16 / 2;
        r7 = r7 - r16;
        r7 = (float) r7;
        goto L_0x01aa;
    L_0x01a6:
        r7 = r13;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r15 = 0;
    L_0x01aa:
        r16 = r2.hasBitmapImage();
        if (r16 == 0) goto L_0x0209;
    L_0x01b0:
        r19.save();
        r16 = r18.getContainerViewWidth();
        r9 = r16 / 2;
        r9 = (float) r9;
        r16 = r18.getContainerViewHeight();
        r3 = r16 / 2;
        r3 = (float) r3;
        r1.translate(r9, r3);
        r3 = r19.getWidth();
        r9 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r9 = r9 / r5;
        r3 = r3 + r9;
        r3 = (float) r3;
        r3 = r3 + r7;
        r1.translate(r3, r6);
        r3 = r8 - r15;
        r1.scale(r3, r3);
        r3 = r2.getBitmapWidth();
        r9 = r2.getBitmapHeight();
        r6 = r18.getContainerViewWidth();
        r6 = (float) r6;
        r3 = (float) r3;
        r6 = r6 / r3;
        r12 = r18.getContainerViewHeight();
        r12 = (float) r12;
        r9 = (float) r9;
        r12 = r12 / r9;
        r17 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1));
        if (r17 <= 0) goto L_0x01f3;
    L_0x01f2:
        r6 = r12;
    L_0x01f3:
        r3 = r3 * r6;
        r3 = (int) r3;
        r9 = r9 * r6;
        r6 = (int) r9;
        r2.setAlpha(r4);
        r9 = -r3;
        r9 = r9 / r5;
        r12 = -r6;
        r12 = r12 / r5;
        r2.setImageCoords(r9, r12, r3, r6);
        r2.draw(r1);
        r19.restore();
    L_0x0209:
        r3 = r0.groupedPhotosListView;
        r6 = -r4;
        r3.setMoveProgress(r6);
        r19.save();
        r3 = r11 / r10;
        r1.translate(r7, r3);
        r3 = r19.getWidth();
        r3 = (float) r3;
        r6 = r0.scale;
        r6 = r6 + r8;
        r3 = r3 * r6;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r6 = (float) r6;
        r3 = r3 + r6;
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r3 = r3 / r6;
        r6 = -r11;
        r6 = r6 / r10;
        r1.translate(r3, r6);
        r3 = r0.radialProgressViews;
        r6 = 1;
        r3 = r3[r6];
        r7 = r8 - r15;
        r3.setScale(r7);
        r3 = r0.radialProgressViews;
        r3 = r3[r6];
        r3.setAlpha(r4);
        r3 = r0.radialProgressViews;
        r3 = r3[r6];
        r3.onDraw(r1);
        r19.restore();
    L_0x024a:
        r3 = r0.zoomAnimation;
        if (r3 != 0) goto L_0x026a;
    L_0x024e:
        r3 = r0.maxX;
        r4 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1));
        if (r4 <= 0) goto L_0x026a;
    L_0x0254:
        r3 = r13 - r3;
        r4 = r19.getWidth();
        r4 = (float) r4;
        r3 = r3 / r4;
        r3 = java.lang.Math.min(r8, r3);
        r4 = NUM; // 0x3e99999a float:0.3 double:5.188942835E-315;
        r6 = r3 * r4;
        r3 = r8 - r3;
        r4 = r0.maxX;
        goto L_0x026e;
    L_0x026a:
        r4 = r13;
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = 0;
    L_0x026e:
        r7 = r0.aspectRatioFrameLayout;
        if (r7 == 0) goto L_0x027a;
    L_0x0272:
        r7 = r7.getVisibility();
        if (r7 != 0) goto L_0x027a;
    L_0x0278:
        r7 = 1;
        goto L_0x027b;
    L_0x027a:
        r7 = 0;
    L_0x027b:
        r9 = r0.centerImage;
        r9 = r9.hasBitmapImage();
        if (r9 == 0) goto L_0x0376;
    L_0x0283:
        r19.save();
        r9 = r18.getContainerViewWidth();
        r9 = r9 / r5;
        r9 = (float) r9;
        r12 = r18.getContainerViewHeight();
        r12 = r12 / r5;
        r12 = (float) r12;
        r1.translate(r9, r12);
        r1.translate(r4, r11);
        r9 = r10 - r6;
        r1.scale(r9, r9);
        r9 = r0.centerImage;
        r9 = r9.getBitmapWidth();
        r12 = r0.centerImage;
        r12 = r12.getBitmapHeight();
        if (r7 == 0) goto L_0x02d9;
    L_0x02ab:
        r15 = r0.textureUploaded;
        if (r15 == 0) goto L_0x02d9;
    L_0x02af:
        r15 = (float) r9;
        r14 = (float) r12;
        r15 = r15 / r14;
        r14 = r0.videoTextureView;
        r14 = r14.getMeasuredWidth();
        r14 = (float) r14;
        r5 = r0.videoTextureView;
        r5 = r5.getMeasuredHeight();
        r5 = (float) r5;
        r14 = r14 / r5;
        r15 = r15 - r14;
        r5 = java.lang.Math.abs(r15);
        r14 = NUM; // 0x3CLASSNAMEd70a float:0.01 double:4.9850323E-315;
        r5 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1));
        if (r5 <= 0) goto L_0x02d9;
    L_0x02cd:
        r5 = r0.videoTextureView;
        r9 = r5.getMeasuredWidth();
        r5 = r0.videoTextureView;
        r12 = r5.getMeasuredHeight();
    L_0x02d9:
        r5 = r18.getContainerViewWidth();
        r5 = (float) r5;
        r9 = (float) r9;
        r5 = r5 / r9;
        r14 = r18.getContainerViewHeight();
        r14 = (float) r14;
        r12 = (float) r12;
        r14 = r14 / r12;
        r15 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1));
        if (r15 <= 0) goto L_0x02ec;
    L_0x02eb:
        goto L_0x02ed;
    L_0x02ec:
        r14 = r5;
    L_0x02ed:
        r9 = r9 * r14;
        r5 = (int) r9;
        r12 = r12 * r14;
        r9 = (int) r12;
        if (r7 == 0) goto L_0x0303;
    L_0x02f5:
        r12 = r0.textureUploaded;
        if (r12 == 0) goto L_0x0303;
    L_0x02f9:
        r12 = r0.videoCrossfadeStarted;
        if (r12 == 0) goto L_0x0303;
    L_0x02fd:
        r12 = r0.videoCrossfadeAlpha;
        r12 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1));
        if (r12 == 0) goto L_0x0317;
    L_0x0303:
        r12 = r0.centerImage;
        r12.setAlpha(r3);
        r12 = r0.centerImage;
        r14 = -r5;
        r15 = 2;
        r14 = r14 / r15;
        r8 = -r9;
        r8 = r8 / r15;
        r12.setImageCoords(r14, r8, r5, r9);
        r8 = r0.centerImage;
        r8.draw(r1);
    L_0x0317:
        if (r7 == 0) goto L_0x0373;
    L_0x0319:
        r8 = r0.videoCrossfadeStarted;
        if (r8 != 0) goto L_0x032d;
    L_0x031d:
        r8 = r0.textureUploaded;
        if (r8 == 0) goto L_0x032d;
    L_0x0321:
        r8 = 1;
        r0.videoCrossfadeStarted = r8;
        r8 = 0;
        r0.videoCrossfadeAlpha = r8;
        r14 = java.lang.System.currentTimeMillis();
        r0.videoCrossfadeAlphaLastTime = r14;
    L_0x032d:
        r5 = -r5;
        r8 = 2;
        r5 = r5 / r8;
        r5 = (float) r5;
        r9 = -r9;
        r9 = r9 / r8;
        r8 = (float) r9;
        r1.translate(r5, r8);
        r5 = r0.videoTextureView;
        r8 = r0.videoCrossfadeAlpha;
        r8 = r8 * r3;
        r5.setAlpha(r8);
        r5 = r0.aspectRatioFrameLayout;
        r5.draw(r1);
        r5 = r0.videoCrossfadeStarted;
        if (r5 == 0) goto L_0x0373;
    L_0x0349:
        r5 = r0.videoCrossfadeAlpha;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1));
        if (r5 >= 0) goto L_0x0373;
    L_0x0351:
        r8 = java.lang.System.currentTimeMillis();
        r14 = r0.videoCrossfadeAlphaLastTime;
        r14 = r8 - r14;
        r0.videoCrossfadeAlphaLastTime = r8;
        r5 = r0.videoCrossfadeAlpha;
        r8 = (float) r14;
        r9 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r8 = r8 / r9;
        r5 = r5 + r8;
        r0.videoCrossfadeAlpha = r5;
        r5 = r0.photoContainerView;
        r5.invalidate();
        r5 = r0.videoCrossfadeAlpha;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1));
        if (r5 <= 0) goto L_0x0373;
    L_0x0371:
        r0.videoCrossfadeAlpha = r8;
    L_0x0373:
        r19.restore();
    L_0x0376:
        if (r7 != 0) goto L_0x03a5;
    L_0x0378:
        r5 = r0.bottomLayout;
        r5 = r5.getVisibility();
        if (r5 == 0) goto L_0x03a5;
    L_0x0380:
        r19.save();
        r5 = r11 / r10;
        r1.translate(r4, r5);
        r4 = r0.radialProgressViews;
        r5 = 0;
        r4 = r4[r5];
        r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r8 = r7 - r6;
        r4.setScale(r8);
        r4 = r0.radialProgressViews;
        r4 = r4[r5];
        r4.setAlpha(r3);
        r4 = r0.radialProgressViews;
        r4 = r4[r5];
        r4.onDraw(r1);
        r19.restore();
    L_0x03a5:
        r4 = r0.leftImage;
        if (r2 != r4) goto L_0x045b;
    L_0x03a9:
        r4 = r2.hasBitmapImage();
        if (r4 == 0) goto L_0x0414;
    L_0x03af:
        r19.save();
        r4 = r18.getContainerViewWidth();
        r5 = 2;
        r4 = r4 / r5;
        r4 = (float) r4;
        r6 = r18.getContainerViewHeight();
        r6 = r6 / r5;
        r5 = (float) r6;
        r1.translate(r4, r5);
        r4 = r19.getWidth();
        r4 = (float) r4;
        r5 = r0.scale;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = r5 + r6;
        r4 = r4 * r5;
        r5 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r6;
        r4 = r4 + r5;
        r4 = -r4;
        r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = r4 / r5;
        r4 = r4 + r13;
        r5 = 0;
        r1.translate(r4, r5);
        r4 = r2.getBitmapWidth();
        r5 = r2.getBitmapHeight();
        r6 = r18.getContainerViewWidth();
        r6 = (float) r6;
        r4 = (float) r4;
        r6 = r6 / r4;
        r7 = r18.getContainerViewHeight();
        r7 = (float) r7;
        r5 = (float) r5;
        r7 = r7 / r5;
        r8 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1));
        if (r8 <= 0) goto L_0x03fa;
    L_0x03f9:
        r6 = r7;
    L_0x03fa:
        r4 = r4 * r6;
        r4 = (int) r4;
        r5 = r5 * r6;
        r5 = (int) r5;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2.setAlpha(r6);
        r7 = -r4;
        r8 = 2;
        r7 = r7 / r8;
        r9 = -r5;
        r9 = r9 / r8;
        r2.setImageCoords(r7, r9, r4, r5);
        r2.draw(r1);
        r19.restore();
        goto L_0x0416;
    L_0x0414:
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0416:
        r2 = r0.groupedPhotosListView;
        r8 = r6 - r3;
        r2.setMoveProgress(r8);
        r19.save();
        r2 = r11 / r10;
        r1.translate(r13, r2);
        r2 = r19.getWidth();
        r2 = (float) r2;
        r3 = r0.scale;
        r3 = r3 + r6;
        r2 = r2 * r3;
        r3 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r2 = r2 + r3;
        r2 = -r2;
        r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r2 = r2 / r3;
        r3 = -r11;
        r3 = r3 / r10;
        r1.translate(r2, r3);
        r2 = r0.radialProgressViews;
        r3 = 2;
        r2 = r2[r3];
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2.setScale(r4);
        r2 = r0.radialProgressViews;
        r2 = r2[r3];
        r2.setAlpha(r4);
        r2 = r0.radialProgressViews;
        r2 = r2[r3];
        r2.onDraw(r1);
        r19.restore();
    L_0x045b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.drawContent(android.graphics.Canvas):void");
    }

    public /* synthetic */ void lambda$drawContent$49$ArticleViewer() {
        setImageIndex(this.currentIndex + 1, false);
    }

    public /* synthetic */ void lambda$drawContent$50$ArticleViewer() {
        setImageIndex(this.currentIndex - 1, false);
    }

    private void onActionClick(boolean z) {
        TLObject media = getMedia(this.currentIndex);
        if ((media instanceof Document) && this.currentFileNames[0] != null) {
            Document document = (Document) media;
            File file = null;
            if (this.currentMedia != null) {
                File mediaFile = getMediaFile(this.currentIndex);
                if (mediaFile == null || mediaFile.exists()) {
                    file = mediaFile;
                }
            }
            if (file != null) {
                preparePlayer(file, true);
            } else if (!z) {
            } else {
                if (FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[0])) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(document);
                } else {
                    FileLoader.getInstance(this.currentAccount).loadFile(document, this.currentPage, 1, 1);
                }
            }
        }
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (this.scale != 1.0f) {
            this.scroller.abortAnimation();
            this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(f), Math.round(f2), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
            this.photoContainerView.postInvalidate();
        }
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if (this.discardTap) {
            return false;
        }
        AspectRatioFrameLayout aspectRatioFrameLayout = this.aspectRatioFrameLayout;
        Object obj = (aspectRatioFrameLayout == null || aspectRatioFrameLayout.getVisibility() != 0) ? null : 1;
        RadialProgressView[] radialProgressViewArr = this.radialProgressViews;
        if (!(radialProgressViewArr[0] == null || this.photoContainerView == null || obj != null)) {
            int access$24500 = radialProgressViewArr[0].backgroundState;
            if (access$24500 > 0 && access$24500 <= 3) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                if (x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && y >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && y <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
                    onActionClick(true);
                    checkProgress(0, true);
                    return true;
                }
            }
        }
        toggleActionBar(this.isActionBarVisible ^ 1, true);
        return true;
    }

    public boolean onDoubleTap(MotionEvent motionEvent) {
        boolean z = false;
        if (this.canZoom && ((this.scale != 1.0f || (this.translationY == 0.0f && this.translationX == 0.0f)) && this.animationStartTime == 0 && this.photoAnimationInProgress == 0)) {
            z = true;
            if (this.scale == 1.0f) {
                float x = (motionEvent.getX() - ((float) (getContainerViewWidth() / 2))) - (((motionEvent.getX() - ((float) (getContainerViewWidth() / 2))) - this.translationX) * (3.0f / this.scale));
                float y = (motionEvent.getY() - ((float) (getContainerViewHeight() / 2))) - (((motionEvent.getY() - ((float) (getContainerViewHeight() / 2))) - this.translationY) * (3.0f / this.scale));
                updateMinMax(3.0f);
                float f = this.minX;
                if (x >= f) {
                    f = this.maxX;
                    if (x <= f) {
                        f = x;
                    }
                }
                x = this.minY;
                if (y >= x) {
                    x = this.maxY;
                    if (y <= x) {
                        x = y;
                    }
                }
                animateTo(3.0f, f, x, true);
            } else {
                animateTo(1.0f, 0.0f, 0.0f, true);
            }
            this.doubleTap = true;
        }
        return z;
    }

    private ImageReceiver getImageReceiverView(View view, PageBlock pageBlock, int[] iArr) {
        ImageReceiver imageReceiverFromListView;
        if (view instanceof BlockPhotoCell) {
            BlockPhotoCell blockPhotoCell = (BlockPhotoCell) view;
            if (blockPhotoCell.currentBlock == pageBlock) {
                view.getLocationInWindow(iArr);
                return blockPhotoCell.imageView;
            }
        } else if (view instanceof BlockVideoCell) {
            BlockVideoCell blockVideoCell = (BlockVideoCell) view;
            if (blockVideoCell.currentBlock == pageBlock) {
                view.getLocationInWindow(iArr);
                return blockVideoCell.imageView;
            }
        } else if (view instanceof BlockCollageCell) {
            imageReceiverFromListView = getImageReceiverFromListView(((BlockCollageCell) view).innerListView, pageBlock, iArr);
            if (imageReceiverFromListView != null) {
                return imageReceiverFromListView;
            }
        } else if (view instanceof BlockSlideshowCell) {
            imageReceiverFromListView = getImageReceiverFromListView(((BlockSlideshowCell) view).innerListView, pageBlock, iArr);
            if (imageReceiverFromListView != null) {
                return imageReceiverFromListView;
            }
        } else if (view instanceof BlockListItemCell) {
            BlockListItemCell blockListItemCell = (BlockListItemCell) view;
            if (blockListItemCell.blockLayout != null) {
                imageReceiverFromListView = getImageReceiverView(blockListItemCell.blockLayout.itemView, pageBlock, iArr);
                if (imageReceiverFromListView != null) {
                    return imageReceiverFromListView;
                }
            }
        } else if (view instanceof BlockOrderedListItemCell) {
            BlockOrderedListItemCell blockOrderedListItemCell = (BlockOrderedListItemCell) view;
            if (blockOrderedListItemCell.blockLayout != null) {
                imageReceiverFromListView = getImageReceiverView(blockOrderedListItemCell.blockLayout.itemView, pageBlock, iArr);
                if (imageReceiverFromListView != null) {
                    return imageReceiverFromListView;
                }
            }
        }
        return null;
    }

    private ImageReceiver getImageReceiverFromListView(ViewGroup viewGroup, PageBlock pageBlock, int[] iArr) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageReceiver imageReceiverView = getImageReceiverView(viewGroup.getChildAt(i), pageBlock, iArr);
            if (imageReceiverView != null) {
                return imageReceiverView;
            }
        }
        return null;
    }

    private PlaceProviderObject getPlaceForPhoto(PageBlock pageBlock) {
        ImageReceiver imageReceiverFromListView = getImageReceiverFromListView(this.listView[0], pageBlock, this.coords);
        if (imageReceiverFromListView == null) {
            return null;
        }
        PlaceProviderObject placeProviderObject = new PlaceProviderObject();
        int[] iArr = this.coords;
        placeProviderObject.viewX = iArr[0];
        placeProviderObject.viewY = iArr[1];
        placeProviderObject.parentView = this.listView[0];
        placeProviderObject.imageReceiver = imageReceiverFromListView;
        placeProviderObject.thumb = imageReceiverFromListView.getBitmapSafe();
        placeProviderObject.radius = imageReceiverFromListView.getRoundRadius();
        placeProviderObject.clipTopAddition = this.currentHeaderHeight;
        return placeProviderObject;
    }
}
