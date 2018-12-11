package org.telegram.p005ui;

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
import android.support.p000v4.content.FileProvider;
import android.support.p000v4.view.PagerAdapter;
import android.support.p000v4.view.ViewPager;
import android.support.p000v4.view.ViewPager.OnPageChangeListener;
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
import android.util.SparseArray;
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
import com.google.android.exoplayer2.CLASSNAMEC;
import com.google.android.exoplayer2.p004ui.AspectRatioFrameLayout;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import java.io.File;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.beta.R;
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
import org.telegram.messenger.support.widget.helper.ItemTouchHelper;
import org.telegram.p005ui.ActionBar.ActionBarMenu;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem;
import org.telegram.p005ui.ActionBar.ActionBarPopupWindow;
import org.telegram.p005ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BackDrawable;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.BottomSheet;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.DrawerLayoutContainer;
import org.telegram.p005ui.ActionBar.SimpleTextView;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.AnchorSpan;
import org.telegram.p005ui.Components.AnimatedArrowDrawable;
import org.telegram.p005ui.Components.AnimatedFileDrawable;
import org.telegram.p005ui.Components.AvatarDrawable;
import org.telegram.p005ui.Components.ClippingImageView;
import org.telegram.p005ui.Components.CombinedDrawable;
import org.telegram.p005ui.Components.ContextProgressView;
import org.telegram.p005ui.Components.GroupedPhotosListView;
import org.telegram.p005ui.Components.GroupedPhotosListView.GroupedPhotosListViewDelegate;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.LineProgressView;
import org.telegram.p005ui.Components.LinkPath;
import org.telegram.p005ui.Components.RadialProgress;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.p005ui.Components.Scroller;
import org.telegram.p005ui.Components.SeekBar;
import org.telegram.p005ui.Components.ShareAlert;
import org.telegram.p005ui.Components.StaticLayoutEx;
import org.telegram.p005ui.Components.TableLayout;
import org.telegram.p005ui.Components.TableLayout.Child;
import org.telegram.p005ui.Components.TableLayout.TableLayoutDelegate;
import org.telegram.p005ui.Components.TextPaintImageReceiverSpan;
import org.telegram.p005ui.Components.TextPaintMarkSpan;
import org.telegram.p005ui.Components.TextPaintSpan;
import org.telegram.p005ui.Components.TextPaintUrlSpan;
import org.telegram.p005ui.Components.TextPaintWebpageUrlSpan;
import org.telegram.p005ui.Components.TypefaceSpan;
import org.telegram.p005ui.Components.VideoPlayer;
import org.telegram.p005ui.Components.VideoPlayer.VideoPlayerDelegate;
import org.telegram.p005ui.Components.WebPlayerView;
import org.telegram.p005ui.Components.WebPlayerView.WebPlayerViewDelegate;
import org.telegram.p005ui.PhotoViewer.LinkMovementMethodMy;
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

/* renamed from: org.telegram.ui.ArticleViewer */
public class ArticleViewer implements OnDoubleTapListener, OnGestureListener, NotificationCenterDelegate {
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
    private CLASSNAMEActionBar actionBar;
    private WebpageAdapter adapter;
    private HashMap<String, Integer> anchors = new HashMap();
    private HashMap<String, Integer> anchorsOffset = new HashMap();
    private int anchorsOffsetMeasuredWidth;
    private HashMap<String, TL_textAnchor> anchorsParent = new HashMap();
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    private ClippingImageView animatingImageView;
    private Runnable animationEndRunnable;
    private int animationInProgress;
    private long animationStartTime;
    private float animationValue;
    private float[][] animationValues = ((float[][]) Array.newInstance(Float.TYPE, new int[]{2, 8}));
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private boolean attachedToWindow;
    private HashMap<TL_pageBlockAudio, MessageObject> audioBlocks = new HashMap();
    private ArrayList<MessageObject> audioMessages = new ArrayList();
    private ImageView backButton;
    private BackDrawable backDrawable;
    private Paint backgroundPaint;
    private Paint blackPaint = new Paint();
    private ArrayList<PageBlock> blocks = new ArrayList();
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
    private LinearLayoutManager layoutManager;
    private ImageReceiver leftImage = new ImageReceiver();
    private Runnable lineProgressTickRunnable;
    private LineProgressView lineProgressView;
    private BottomSheet linkSheet;
    private RecyclerListView listView;
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
    private ArrayList<WebPage> pagesStack = new ArrayList();
    private Activity parentActivity;
    private BaseFragment parentFragment;
    private CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    private Runnable photoAnimationEndRunnable;
    private int photoAnimationInProgress;
    private PhotoBackgroundDrawable photoBackgroundDrawable = new PhotoBackgroundDrawable(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
    private ArrayList<PageBlock> photoBlocks = new ArrayList();
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
    private Runnable updateProgressRunnable = new CLASSNAME();
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

    /* renamed from: org.telegram.ui.ArticleViewer$10 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            AndroidUtilities.runOnUIThread(new ArticleViewer$10$$Lambda$0(this));
        }

        final /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$10() {
            NotificationCenter.getInstance(ArticleViewer.this.currentAccount).setAnimationInProgress(false);
            if (ArticleViewer.this.animationEndRunnable != null) {
                ArticleViewer.this.animationEndRunnable.run();
                ArticleViewer.this.animationEndRunnable = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$12 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            if (ArticleViewer.this.animationEndRunnable != null) {
                ArticleViewer.this.animationEndRunnable.run();
                ArticleViewer.this.animationEndRunnable = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$13 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            if (ArticleViewer.this.animationEndRunnable != null) {
                ArticleViewer.this.animationEndRunnable.run();
                ArticleViewer.this.animationEndRunnable = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$14 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            if (ArticleViewer.this.animationEndRunnable != null) {
                ArticleViewer.this.animationEndRunnable.run();
                ArticleViewer.this.animationEndRunnable = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$15 */
    class CLASSNAME implements Runnable {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.ArticleViewer$17 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.ArticleViewer$20 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            AndroidUtilities.runOnUIThread(new ArticleViewer$20$$Lambda$0(this));
        }

        final /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$20() {
            NotificationCenter.getInstance(ArticleViewer.this.currentAccount).setAnimationInProgress(false);
            if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                ArticleViewer.this.photoAnimationEndRunnable.run();
                ArticleViewer.this.photoAnimationEndRunnable = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$21 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            AndroidUtilities.runOnUIThread(new ArticleViewer$21$$Lambda$0(this));
        }

        final /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$21() {
            if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                ArticleViewer.this.photoAnimationEndRunnable.run();
                ArticleViewer.this.photoAnimationEndRunnable = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$22 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            ArticleViewer.this.imageMoveAnimation = null;
            ArticleViewer.this.photoContainerView.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$9 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            AndroidUtilities.runOnUIThread(new ArticleViewer$9$$Lambda$0(this), 3000);
        }

        final /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$9() {
            AnimatorSet animatorSet1 = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.nightModeHintView, "translationY", new float[]{(float) AndroidUtilities.m9dp(100.0f)});
            animatorSet1.playTogether(animatorArr);
            animatorSet1.setInterpolator(new DecelerateInterpolator(1.5f));
            animatorSet1.setDuration(250);
            animatorSet1.start();
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockAuthorDateCell */
    private class BlockAuthorDateCell extends View {
        private TL_pageBlockAuthorDate currentBlock;
        private DrawingText textLayout;
        private int textX;
        private int textY = AndroidUtilities.m9dp(8.0f);

        public BlockAuthorDateCell(Context context) {
            super(context);
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
                        FileLog.m13e(e);
                    }
                }
                this.textLayout = ArticleViewer.this.createLayoutForText(this, text, null, width - AndroidUtilities.m9dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.m9dp(16.0f) + this.textLayout.getHeight());
                    if (ArticleViewer.this.isRtl) {
                        this.textX = (int) Math.floor((double) ((((float) width) - this.textLayout.getLineWidth(0)) - ((float) AndroidUtilities.m9dp(16.0f))));
                    } else {
                        this.textX = AndroidUtilities.m9dp(18.0f);
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockBlockquoteCell */
    private class BlockBlockquoteCell extends View {
        private TL_pageBlockBlockquote currentBlock;
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textX;
        private int textY = AndroidUtilities.m9dp(8.0f);
        private int textY2;

        public BlockBlockquoteCell(Context context) {
            super(context);
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
                int textWidth = width - AndroidUtilities.m9dp(50.0f);
                if (this.currentBlock.level > 0) {
                    textWidth -= AndroidUtilities.m9dp((float) (this.currentBlock.level * 14));
                }
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, textWidth, this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.m9dp(8.0f) + this.textLayout.getHeight());
                }
                if (this.currentBlock.level > 0) {
                    if (ArticleViewer.this.isRtl) {
                        this.textX = AndroidUtilities.m9dp((float) ((this.currentBlock.level * 14) + 14));
                    } else {
                        this.textX = AndroidUtilities.m9dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.m9dp(32.0f);
                    }
                } else if (ArticleViewer.this.isRtl) {
                    this.textX = AndroidUtilities.m9dp(14.0f);
                } else {
                    this.textX = AndroidUtilities.m9dp(32.0f);
                }
                this.textLayout2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption, textWidth, this.currentBlock);
                if (this.textLayout2 != null) {
                    this.textY2 = AndroidUtilities.m9dp(8.0f) + height;
                    height += AndroidUtilities.m9dp(8.0f) + this.textLayout2.getHeight();
                }
                if (height != 0) {
                    height += AndroidUtilities.m9dp(8.0f);
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
                    int x = getMeasuredWidth() - AndroidUtilities.m9dp(20.0f);
                    canvas.drawRect((float) x, (float) AndroidUtilities.m9dp(6.0f), (float) (AndroidUtilities.m9dp(2.0f) + x), (float) (getMeasuredHeight() - AndroidUtilities.m9dp(6.0f)), ArticleViewer.quoteLinePaint);
                } else {
                    canvas.drawRect((float) AndroidUtilities.m9dp((float) ((this.currentBlock.level * 14) + 18)), (float) AndroidUtilities.m9dp(6.0f), (float) AndroidUtilities.m9dp((float) ((this.currentBlock.level * 14) + 20)), (float) (getMeasuredHeight() - AndroidUtilities.m9dp(6.0f)), ArticleViewer.quoteLinePaint);
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.m9dp(18.0f), 0.0f, (float) AndroidUtilities.m9dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.m9dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockChannelCell */
    private class BlockChannelCell extends FrameLayout {
        private Paint backgroundPaint;
        private int buttonWidth;
        private AnimatorSet currentAnimation;
        private TL_pageBlockChannel currentBlock;
        private int currentState;
        private int currentType;
        private ImageView imageView;
        private ContextProgressView progressView;
        private DrawingText textLayout;
        private TextView textView;
        private int textX = AndroidUtilities.m9dp(18.0f);
        private int textX2;
        private int textY = AndroidUtilities.m9dp(11.0f);

        public BlockChannelCell(Context context, int type) {
            super(context);
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
                this.backgroundPaint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                this.imageView.setColorFilter(new PorterDuffColorFilter(-1, Mode.MULTIPLY));
            }
            Chat channel = MessagesController.getInstance(ArticleViewer.this.currentAccount).getChat(Integer.valueOf(block.channel.var_id));
            if (channel == null || channel.min) {
                ArticleViewer.this.loadChannel(this, block.channel);
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
                String str = "alpha";
                float[] fArr = new float[1];
                fArr[0] = state == 0 ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(textView, str, fArr);
                textView = this.textView;
                str = "scaleX";
                fArr = new float[1];
                if (state == 0) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.1f;
                }
                fArr[0] = f2;
                animatorArr[1] = ObjectAnimator.ofFloat(textView, str, fArr);
                textView = this.textView;
                str = "scaleY";
                fArr = new float[1];
                if (state == 0) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.1f;
                }
                fArr[0] = f2;
                animatorArr[2] = ObjectAnimator.ofFloat(textView, str, fArr);
                ContextProgressView contextProgressView = this.progressView;
                String str2 = "alpha";
                float[] fArr2 = new float[1];
                fArr2[0] = state == 1 ? 1.0f : 0.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(contextProgressView, str2, fArr2);
                contextProgressView = this.progressView;
                str2 = "scaleX";
                fArr2 = new float[1];
                if (state == 1) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.1f;
                }
                fArr2[0] = f2;
                animatorArr[4] = ObjectAnimator.ofFloat(contextProgressView, str2, fArr2);
                contextProgressView = this.progressView;
                str2 = "scaleY";
                fArr2 = new float[1];
                if (state == 1) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.1f;
                }
                fArr2[0] = f2;
                animatorArr[5] = ObjectAnimator.ofFloat(contextProgressView, str2, fArr2);
                ImageView imageView = this.imageView;
                str2 = "alpha";
                fArr2 = new float[1];
                fArr2[0] = state == 2 ? 1.0f : 0.0f;
                animatorArr[6] = ObjectAnimator.ofFloat(imageView, str2, fArr2);
                imageView = this.imageView;
                str2 = "scaleX";
                fArr2 = new float[1];
                if (state == 2) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.1f;
                }
                fArr2[0] = f2;
                animatorArr[7] = ObjectAnimator.ofFloat(imageView, str2, fArr2);
                ImageView imageView2 = this.imageView;
                str = "scaleY";
                fArr = new float[1];
                if (state != 2) {
                    f = 0.1f;
                }
                fArr[0] = f;
                animatorArr[8] = ObjectAnimator.ofFloat(imageView2, str, fArr);
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

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width, AndroidUtilities.m9dp(48.0f));
            this.textView.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(39.0f), NUM));
            this.buttonWidth = this.textView.getMeasuredWidth();
            this.progressView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(39.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(39.0f), NUM));
            this.imageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(39.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(39.0f), NUM));
            if (this.currentBlock != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, this.currentBlock.channel.title, null, (width - AndroidUtilities.m9dp(52.0f)) - this.buttonWidth, this.currentBlock);
                this.textX2 = (getMeasuredWidth() - this.textX) - this.buttonWidth;
            }
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.imageView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.m9dp(19.0f), 0, (this.textX2 + (this.buttonWidth / 2)) + AndroidUtilities.m9dp(20.0f), AndroidUtilities.m9dp(39.0f));
            this.progressView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.m9dp(19.0f), 0, (this.textX2 + (this.buttonWidth / 2)) + AndroidUtilities.m9dp(20.0f), AndroidUtilities.m9dp(39.0f));
            this.textView.layout(this.textX2, 0, this.textX2 + this.textView.getMeasuredWidth(), this.textView.getMeasuredHeight());
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.m9dp(39.0f), this.backgroundPaint);
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockCollageCell */
    private class BlockCollageCell extends FrameLayout {
        private Adapter adapter;
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockCollage currentBlock;
        private GridLayoutManager gridLayoutManager;
        private GroupedMessages group = new GroupedMessages();
        private boolean inLayout;
        private RecyclerListView innerListView;
        private int listX;
        private int textX;
        private int textY;

        /* renamed from: org.telegram.ui.ArticleViewer$BlockCollageCell$GroupedMessages */
        public class GroupedMessages {
            public long groupId;
            public boolean hasSibling;
            private int maxSizeWidth = 1000;
            public ArrayList<GroupedMessagePosition> posArray = new ArrayList();
            public HashMap<TLObject, GroupedMessagePosition> positions = new HashMap();

            /* renamed from: org.telegram.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt */
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

            /* JADX WARNING: Removed duplicated region for block: B:29:0x00e9  */
            /* JADX WARNING: Removed duplicated region for block: B:13:0x0081  */
            /* JADX WARNING: Removed duplicated region for block: B:30:0x00eb  */
            /* JADX WARNING: Removed duplicated region for block: B:16:0x0088  */
            /* JADX WARNING: Removed duplicated region for block: B:31:0x00f7  */
            /* JADX WARNING: Removed duplicated region for block: B:19:0x0099  */
            /* JADX WARNING: Removed duplicated region for block: B:22:0x00b1  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void calculate() {
                this.posArray.clear();
                this.positions.clear();
                int count = BlockCollageCell.this.currentBlock.items.size();
                if (count > 1) {
                    GroupedMessagePosition pos;
                    StringBuilder proportions = new StringBuilder();
                    float averageAspectRatio = 1.0f;
                    boolean forceCalc = false;
                    this.hasSibling = false;
                    int a = 0;
                    while (a < count) {
                        TLObject object = (TLObject) BlockCollageCell.this.currentBlock.items.get(a);
                        PhotoSize photoSize;
                        GroupedMessagePosition position;
                        if (object instanceof TL_pageBlockPhoto) {
                            Photo photo = ArticleViewer.this.getPhotoWithId(((TL_pageBlockPhoto) object).photo_id);
                            if (photo != null) {
                                photoSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                                position = new GroupedMessagePosition();
                                position.last = a != count + -1;
                                position.aspectRatio = photoSize != null ? 1.0f : ((float) photoSize.var_w) / ((float) photoSize.var_h);
                                if (position.aspectRatio <= 1.2f) {
                                    proportions.append("w");
                                } else if (position.aspectRatio < 0.8f) {
                                    proportions.append("n");
                                } else {
                                    proportions.append("q");
                                }
                                averageAspectRatio += position.aspectRatio;
                                if (position.aspectRatio > 2.0f) {
                                    forceCalc = true;
                                }
                                this.positions.put(object, position);
                                this.posArray.add(position);
                            }
                        } else if (object instanceof TL_pageBlockVideo) {
                            Document document = ArticleViewer.this.getDocumentWithId(((TL_pageBlockVideo) object).video_id);
                            if (document != null) {
                                photoSize = document.thumb;
                                position = new GroupedMessagePosition();
                                if (a != count + -1) {
                                }
                                position.last = a != count + -1;
                                if (photoSize != null) {
                                }
                                position.aspectRatio = photoSize != null ? 1.0f : ((float) photoSize.var_w) / ((float) photoSize.var_h);
                                if (position.aspectRatio <= 1.2f) {
                                }
                                averageAspectRatio += position.aspectRatio;
                                if (position.aspectRatio > 2.0f) {
                                }
                                this.positions.put(object, position);
                                this.posArray.add(position);
                            }
                        }
                        a++;
                    }
                    int minHeight = AndroidUtilities.m9dp(120.0f);
                    int minWidth = (int) (((float) AndroidUtilities.m9dp(120.0f)) / (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) / ((float) this.maxSizeWidth)));
                    int paddingsWidth = (int) (((float) AndroidUtilities.m9dp(40.0f)) / (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) / ((float) this.maxSizeWidth)));
                    float maxAspectRatio = ((float) this.maxSizeWidth) / 814.0f;
                    averageAspectRatio /= (float) count;
                    float height;
                    int width;
                    GroupedMessagePosition position1;
                    GroupedMessagePosition position2;
                    GroupedMessagePosition position3;
                    if (forceCalc || !(count == 2 || count == 3 || count == 4)) {
                        int firstLine;
                        int secondLine;
                        int thirdLine;
                        float[] croppedRatios = new float[this.posArray.size()];
                        for (a = 0; a < count; a++) {
                            if (averageAspectRatio > 1.1f) {
                                croppedRatios[a] = Math.max(1.0f, ((GroupedMessagePosition) this.posArray.get(a)).aspectRatio);
                            } else {
                                croppedRatios[a] = Math.min(1.0f, ((GroupedMessagePosition) this.posArray.get(a)).aspectRatio);
                            }
                            croppedRatios[a] = Math.max(0.66667f, Math.min(1.7f, croppedRatios[a]));
                        }
                        ArrayList<MessageGroupedLayoutAttempt> attempts = new ArrayList();
                        for (firstLine = 1; firstLine < croppedRatios.length; firstLine++) {
                            secondLine = croppedRatios.length - firstLine;
                            if (firstLine <= 3 && secondLine <= 3) {
                                attempts.add(new MessageGroupedLayoutAttempt(firstLine, secondLine, multiHeight(croppedRatios, 0, firstLine), multiHeight(croppedRatios, firstLine, croppedRatios.length)));
                            }
                        }
                        for (firstLine = 1; firstLine < croppedRatios.length - 1; firstLine++) {
                            for (secondLine = 1; secondLine < croppedRatios.length - firstLine; secondLine++) {
                                thirdLine = (croppedRatios.length - firstLine) - secondLine;
                                if (firstLine <= 3) {
                                    if (secondLine <= (averageAspectRatio < 0.85f ? 4 : 3) && thirdLine <= 3) {
                                        attempts.add(new MessageGroupedLayoutAttempt(firstLine, secondLine, thirdLine, multiHeight(croppedRatios, 0, firstLine), multiHeight(croppedRatios, firstLine, firstLine + secondLine), multiHeight(croppedRatios, firstLine + secondLine, croppedRatios.length)));
                                    }
                                }
                            }
                        }
                        for (firstLine = 1; firstLine < croppedRatios.length - 2; firstLine++) {
                            secondLine = 1;
                            while (secondLine < croppedRatios.length - firstLine) {
                                thirdLine = 1;
                                while (thirdLine < (croppedRatios.length - firstLine) - secondLine) {
                                    int fourthLine = ((croppedRatios.length - firstLine) - secondLine) - thirdLine;
                                    if (firstLine <= 3 && secondLine <= 3 && thirdLine <= 3 && fourthLine <= 3) {
                                        attempts.add(new MessageGroupedLayoutAttempt(firstLine, secondLine, thirdLine, fourthLine, multiHeight(croppedRatios, 0, firstLine), multiHeight(croppedRatios, firstLine, firstLine + secondLine), multiHeight(croppedRatios, firstLine + secondLine, (firstLine + secondLine) + thirdLine), multiHeight(croppedRatios, (firstLine + secondLine) + thirdLine, croppedRatios.length)));
                                    }
                                    thirdLine++;
                                }
                                secondLine++;
                            }
                        }
                        MessageGroupedLayoutAttempt optimal = null;
                        float optimalDiff = 0.0f;
                        float maxHeight = (float) ((this.maxSizeWidth / 3) * 4);
                        for (a = 0; a < attempts.size(); a++) {
                            MessageGroupedLayoutAttempt attempt = (MessageGroupedLayoutAttempt) attempts.get(a);
                            height = 0.0f;
                            float minLineHeight = Float.MAX_VALUE;
                            for (int b = 0; b < attempt.heights.length; b++) {
                                height += attempt.heights[b];
                                if (attempt.heights[b] < minLineHeight) {
                                    minLineHeight = attempt.heights[b];
                                }
                            }
                            float diff = Math.abs(height - maxHeight);
                            if (attempt.lineCounts.length > 1 && (attempt.lineCounts[0] > attempt.lineCounts[1] || ((attempt.lineCounts.length > 2 && attempt.lineCounts[1] > attempt.lineCounts[2]) || (attempt.lineCounts.length > 3 && attempt.lineCounts[2] > attempt.lineCounts[3])))) {
                                diff *= 1.2f;
                            }
                            if (minLineHeight < ((float) minWidth)) {
                                diff *= 1.5f;
                            }
                            if (optimal == null || diff < optimalDiff) {
                                optimal = attempt;
                                optimalDiff = diff;
                            }
                        }
                        if (optimal != null) {
                            int index = 0;
                            float y = 0.0f;
                            for (int i = 0; i < optimal.lineCounts.length; i++) {
                                int c = optimal.lineCounts[i];
                                float lineHeight = optimal.heights[i];
                                int spanLeft = this.maxSizeWidth;
                                GroupedMessagePosition posToFix = null;
                                for (int k = 0; k < c; k++) {
                                    width = (int) (croppedRatios[index] * lineHeight);
                                    spanLeft -= width;
                                    pos = (GroupedMessagePosition) this.posArray.get(index);
                                    int flags = 0;
                                    if (i == 0) {
                                        flags = 0 | 4;
                                    }
                                    if (i == optimal.lineCounts.length - 1) {
                                        flags |= 8;
                                    }
                                    if (k == 0) {
                                        flags |= 1;
                                    }
                                    if (k == c - 1) {
                                        flags |= 2;
                                        posToFix = pos;
                                    }
                                    pos.set(k, k, i, i, width, lineHeight / 814.0f, flags);
                                    index++;
                                }
                                posToFix.var_pw += spanLeft;
                                posToFix.spanSize += spanLeft;
                                y += lineHeight;
                            }
                        } else {
                            return;
                        }
                    } else if (count == 2) {
                        position1 = (GroupedMessagePosition) this.posArray.get(0);
                        position2 = (GroupedMessagePosition) this.posArray.get(1);
                        String pString = proportions.toString();
                        if (!pString.equals("ww") || ((double) averageAspectRatio) <= 1.4d * ((double) maxAspectRatio) || ((double) (position1.aspectRatio - position2.aspectRatio)) >= 0.2d) {
                            if (!pString.equals("ww")) {
                                if (!pString.equals("qq")) {
                                    int secondWidth = (int) Math.max(0.4f * ((float) this.maxSizeWidth), (float) Math.round((((float) this.maxSizeWidth) / position1.aspectRatio) / ((1.0f / position1.aspectRatio) + (1.0f / position2.aspectRatio))));
                                    int firstWidth = this.maxSizeWidth - secondWidth;
                                    if (firstWidth < minWidth) {
                                        int diff2 = minWidth - firstWidth;
                                        firstWidth = minWidth;
                                        secondWidth -= diff2;
                                    }
                                    height = Math.min(814.0f, (float) Math.round(Math.min(((float) firstWidth) / position1.aspectRatio, ((float) secondWidth) / position2.aspectRatio))) / 814.0f;
                                    position1.set(0, 0, 0, 0, firstWidth, height, 13);
                                    position2.set(1, 1, 0, 0, secondWidth, height, 14);
                                }
                            }
                            width = this.maxSizeWidth / 2;
                            height = ((float) Math.round(Math.min(((float) width) / position1.aspectRatio, Math.min(((float) width) / position2.aspectRatio, 814.0f)))) / 814.0f;
                            position1.set(0, 0, 0, 0, width, height, 13);
                            position2.set(1, 1, 0, 0, width, height, 14);
                        } else {
                            height = ((float) Math.round(Math.min(((float) this.maxSizeWidth) / position1.aspectRatio, Math.min(((float) this.maxSizeWidth) / position2.aspectRatio, 814.0f / 2.0f)))) / 814.0f;
                            position1.set(0, 0, 0, 0, this.maxSizeWidth, height, 7);
                            position2.set(0, 0, 1, 1, this.maxSizeWidth, height, 11);
                        }
                    } else if (count == 3) {
                        position1 = (GroupedMessagePosition) this.posArray.get(0);
                        position2 = (GroupedMessagePosition) this.posArray.get(1);
                        position3 = (GroupedMessagePosition) this.posArray.get(2);
                        float secondHeight;
                        if (proportions.charAt(0) == 'n') {
                            float thirdHeight = Math.min(0.5f * 814.0f, (float) Math.round((position2.aspectRatio * ((float) this.maxSizeWidth)) / (position3.aspectRatio + position2.aspectRatio)));
                            secondHeight = 814.0f - thirdHeight;
                            int rightWidth = (int) Math.max((float) minWidth, Math.min(((float) this.maxSizeWidth) * 0.5f, (float) Math.round(Math.min(position3.aspectRatio * thirdHeight, position2.aspectRatio * secondHeight))));
                            int leftWidth = Math.round(Math.min((position1.aspectRatio * 814.0f) + ((float) paddingsWidth), (float) (this.maxSizeWidth - rightWidth)));
                            position1.set(0, 0, 0, 1, leftWidth, 1.0f, 13);
                            position2.set(1, 1, 0, 0, rightWidth, secondHeight / 814.0f, 6);
                            position3.set(0, 1, 1, 1, rightWidth, thirdHeight / 814.0f, 10);
                            position3.spanSize = this.maxSizeWidth;
                            position1.siblingHeights = new float[]{thirdHeight / 814.0f, secondHeight / 814.0f};
                            position2.spanSize = this.maxSizeWidth - leftWidth;
                            position3.leftSpanOffset = leftWidth;
                            this.hasSibling = true;
                        } else {
                            float firstHeight = ((float) Math.round(Math.min(((float) this.maxSizeWidth) / position1.aspectRatio, 0.66f * 814.0f))) / 814.0f;
                            position1.set(0, 1, 0, 0, this.maxSizeWidth, firstHeight, 7);
                            width = this.maxSizeWidth / 2;
                            secondHeight = Math.min(814.0f - firstHeight, (float) Math.round(Math.min(((float) width) / position2.aspectRatio, ((float) width) / position3.aspectRatio))) / 814.0f;
                            position2.set(0, 0, 1, 1, width, secondHeight, 9);
                            position3.set(1, 1, 1, 1, width, secondHeight, 10);
                        }
                    } else if (count == 4) {
                        position1 = (GroupedMessagePosition) this.posArray.get(0);
                        position2 = (GroupedMessagePosition) this.posArray.get(1);
                        position3 = (GroupedMessagePosition) this.posArray.get(2);
                        GroupedMessagePosition position4 = (GroupedMessagePosition) this.posArray.get(3);
                        float h0;
                        int w0;
                        if (proportions.charAt(0) == 'w') {
                            h0 = ((float) Math.round(Math.min(((float) this.maxSizeWidth) / position1.aspectRatio, 0.66f * 814.0f))) / 814.0f;
                            position1.set(0, 2, 0, 0, this.maxSizeWidth, h0, 7);
                            float h = (float) Math.round(((float) this.maxSizeWidth) / ((position2.aspectRatio + position3.aspectRatio) + position4.aspectRatio));
                            w0 = (int) Math.max((float) minWidth, Math.min(((float) this.maxSizeWidth) * 0.4f, position2.aspectRatio * h));
                            int w2 = (int) Math.max(Math.max((float) minWidth, ((float) this.maxSizeWidth) * 0.33f), position4.aspectRatio * h);
                            int w1 = (this.maxSizeWidth - w0) - w2;
                            h = Math.min(814.0f - h0, h) / 814.0f;
                            position2.set(0, 0, 1, 1, w0, h, 9);
                            position3.set(1, 1, 1, 1, w1, h, 8);
                            position4.set(2, 2, 1, 1, w2, h, 10);
                        } else {
                            int w = Math.max(minWidth, Math.round(814.0f / ((1.0f / ((GroupedMessagePosition) this.posArray.get(3)).aspectRatio) + ((1.0f / position3.aspectRatio) + (1.0f / position2.aspectRatio)))));
                            h0 = Math.min(0.33f, Math.max((float) minHeight, ((float) w) / position2.aspectRatio) / 814.0f);
                            float h1 = Math.min(0.33f, Math.max((float) minHeight, ((float) w) / position3.aspectRatio) / 814.0f);
                            float h2 = (1.0f - h0) - h1;
                            w0 = Math.round(Math.min((position1.aspectRatio * 814.0f) + ((float) paddingsWidth), (float) (this.maxSizeWidth - w)));
                            position1.set(0, 0, 0, 2, w0, (h0 + h1) + h2, 13);
                            position2.set(1, 1, 0, 0, w, h0, 6);
                            position3.set(0, 1, 1, 1, w, h1, 2);
                            position3.spanSize = this.maxSizeWidth;
                            position4.set(0, 1, 2, 2, w, h2, 10);
                            position4.spanSize = this.maxSizeWidth;
                            position2.spanSize = this.maxSizeWidth - w0;
                            position3.leftSpanOffset = w0;
                            position4.leftSpanOffset = w0;
                            position1.siblingHeights = new float[]{h0, h1, h2};
                            this.hasSibling = true;
                        }
                    }
                    for (a = 0; a < count; a++) {
                        pos = (GroupedMessagePosition) this.posArray.get(a);
                        if ((pos.flags & 1) != 0) {
                            pos.edge = true;
                        }
                    }
                }
            }
        }

        public BlockCollageCell(Context context) {
            super(context);
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
                                h -= ((int) Math.ceil((double) (pos.var_ph * maxHeight))) - AndroidUtilities.m9dp(4.0f);
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
            Adapter CLASSNAME = new Adapter(ArticleViewer.this) {
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view;
                    switch (viewType) {
                        case 0:
                            view = new BlockPhotoCell(BlockCollageCell.this.getContext(), 2);
                            break;
                        default:
                            view = new BlockVideoCell(BlockCollageCell.this.getContext(), 2);
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
            this.adapter = CLASSNAME;
            recyclerListView.setAdapter(CLASSNAME);
            addView(this.innerListView, LayoutHelper.createFrame(-1, -2.0f));
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockCollage block) {
            if (this.currentBlock != block) {
                this.currentBlock = block;
                this.group.calculate();
            }
            this.adapter.notifyDataSetChanged();
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
                    int dp = AndroidUtilities.m9dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.m9dp(18.0f);
                    this.listX = dp;
                    this.textX = dp;
                    listWidth -= this.listX + AndroidUtilities.m9dp(18.0f);
                    textWidth = listWidth;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.m9dp(18.0f);
                    textWidth = width - AndroidUtilities.m9dp(36.0f);
                }
                this.innerListView.measure(MeasureSpec.makeMeasureSpec(listWidth, NUM), MeasureSpec.makeMeasureSpec(0, 0));
                height = this.innerListView.getMeasuredHeight();
                this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock);
                if (this.captionLayout != null) {
                    this.textY = AndroidUtilities.m9dp(8.0f) + height;
                    this.creditOffset = AndroidUtilities.m9dp(4.0f) + this.captionLayout.getHeight();
                    height += this.creditOffset + AndroidUtilities.m9dp(4.0f);
                }
                this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL);
                if (this.creditLayout != null) {
                    height += AndroidUtilities.m9dp(4.0f) + this.creditLayout.getHeight();
                }
                height += AndroidUtilities.m9dp(16.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.m9dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            this.inLayout = false;
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.innerListView.layout(this.listX, AndroidUtilities.m9dp(8.0f), this.listX + this.innerListView.getMeasuredWidth(), this.innerListView.getMeasuredHeight() + AndroidUtilities.m9dp(8.0f));
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
                    canvas.drawRect((float) AndroidUtilities.m9dp(18.0f), 0.0f, (float) AndroidUtilities.m9dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.m9dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockDetailsBottomCell */
    private class BlockDetailsBottomCell extends View {
        private RectF rect = new RectF();

        public BlockDetailsBottomCell(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.m9dp(4.0f) + 1);
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawLine(0.0f, 0.0f, (float) getMeasuredWidth(), 0.0f, ArticleViewer.dividerPaint);
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockDetailsCell */
    private class BlockDetailsCell extends View implements Callback {
        private AnimatedArrowDrawable arrow;
        private TL_pageBlockDetails currentBlock;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.m9dp(50.0f);
        private int textY = (AndroidUtilities.m9dp(11.0f) + 1);

        public BlockDetailsCell(Context context) {
            super(context);
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

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int h = AndroidUtilities.m9dp(39.0f);
            if (this.currentBlock != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.title, width - AndroidUtilities.m9dp(52.0f), this.currentBlock);
                if (this.textLayout != null) {
                    h = Math.max(h, AndroidUtilities.m9dp(21.0f) + this.textLayout.getHeight());
                    this.textY = ((this.textLayout.getHeight() + AndroidUtilities.m9dp(21.0f)) - this.textLayout.getHeight()) / 2;
                }
            }
            setMeasuredDimension(width, h + 1);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.m9dp(18.0f), (float) (((getMeasuredHeight() - AndroidUtilities.m9dp(13.0f)) - 1) / 2));
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockDividerCell */
    private class BlockDividerCell extends View {
        private RectF rect = new RectF();

        public BlockDividerCell(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.m9dp(18.0f));
        }

        protected void onDraw(Canvas canvas) {
            int width = getMeasuredWidth() / 3;
            this.rect.set((float) width, (float) AndroidUtilities.m9dp(8.0f), (float) (width * 2), (float) AndroidUtilities.m9dp(10.0f));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m9dp(1.0f), (float) AndroidUtilities.m9dp(1.0f), ArticleViewer.dividerPaint);
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockEmbedCell */
    private class BlockEmbedCell extends FrameLayout {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockEmbed currentBlock;
        private int exactWebViewHeight;
        private int listX;
        private int textX;
        private int textY;
        private WebPlayerView videoView;
        private TouchyWebView webView;

        /* renamed from: org.telegram.ui.ArticleViewer$BlockEmbedCell$TelegramWebviewProxy */
        private class TelegramWebviewProxy {
            private TelegramWebviewProxy() {
            }

            /* synthetic */ TelegramWebviewProxy(BlockEmbedCell x0, CLASSNAME x1) {
                this();
            }

            @JavascriptInterface
            public void postEvent(String eventName, String eventData) {
                AndroidUtilities.runOnUIThread(new ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$$Lambda$0(this, eventName, eventData));
            }

            /* renamed from: lambda$postEvent$0$ArticleViewer$BlockEmbedCell$TelegramWebviewProxy */
            final /* synthetic */ void mo9499x2b8da535(String eventName, String eventData) {
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

        /* renamed from: org.telegram.ui.ArticleViewer$BlockEmbedCell$TouchyWebView */
        public class TouchyWebView extends WebView {
            public TouchyWebView(Context context) {
                super(context);
                setFocusable(false);
            }

            public boolean onTouchEvent(MotionEvent event) {
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
        public BlockEmbedCell(Context context) {
            super(context);
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
                            FileLog.m13e(e);
                            return;
                        }
                    }
                    if (ArticleViewer.this.currentPlayingVideo == playerView) {
                        ArticleViewer.this.currentPlayingVideo = null;
                    }
                    try {
                        ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                    } catch (Throwable e2) {
                        FileLog.m13e(e2);
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
                FileLog.m13e(e);
            }
            this.videoView.destroy();
        }

        public void setBlock(TL_pageBlockEmbed block) {
            Photo thumb = null;
            TL_pageBlockEmbed previousBlock = this.currentBlock;
            this.currentBlock = block;
            if (previousBlock != this.currentBlock) {
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
                    FileLog.m13e(e);
                }
                try {
                    if (this.currentBlock.html != null) {
                        this.webView.loadDataWithBaseURL("https://telegram.org/embed", this.currentBlock.html, "text/html", CLASSNAMEC.UTF8_NAME, null);
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
                    FileLog.m13e(e2);
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
                    int dp = AndroidUtilities.m9dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.m9dp(18.0f);
                    this.listX = dp;
                    this.textX = dp;
                    listWidth -= this.listX + AndroidUtilities.m9dp(18.0f);
                    textWidth = listWidth;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.m9dp(18.0f);
                    textWidth = width - AndroidUtilities.m9dp(36.0f);
                    if (!this.currentBlock.full_width) {
                        listWidth -= AndroidUtilities.m9dp(36.0f);
                        this.listX += AndroidUtilities.m9dp(18.0f);
                    }
                }
                if (this.currentBlock.var_w == 0) {
                    scale = 1.0f;
                } else {
                    scale = ((float) width) / ((float) this.currentBlock.var_w);
                }
                if (this.exactWebViewHeight != 0) {
                    height = AndroidUtilities.m9dp((float) this.exactWebViewHeight);
                } else {
                    float dp2;
                    if (this.currentBlock.var_w == 0) {
                        dp2 = ((float) AndroidUtilities.m9dp((float) this.currentBlock.var_h)) * scale;
                    } else {
                        dp2 = ((float) this.currentBlock.var_h) * scale;
                    }
                    height = (int) dp2;
                }
                if (height == 0) {
                    height = AndroidUtilities.m9dp(10.0f);
                }
                this.webView.measure(MeasureSpec.makeMeasureSpec(listWidth, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                if (this.videoView.getParent() == this) {
                    this.videoView.measure(MeasureSpec.makeMeasureSpec(listWidth, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(10.0f) + height, NUM));
                }
                this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock);
                if (this.captionLayout != null) {
                    this.textY = AndroidUtilities.m9dp(8.0f) + height;
                    this.creditOffset = AndroidUtilities.m9dp(4.0f) + this.captionLayout.getHeight();
                    height += this.creditOffset + AndroidUtilities.m9dp(4.0f);
                }
                this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL);
                if (this.creditLayout != null) {
                    height += AndroidUtilities.m9dp(4.0f) + this.creditLayout.getHeight();
                }
                height += AndroidUtilities.m9dp(5.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.m9dp(8.0f);
                } else if (this.currentBlock.level == 0 && this.captionLayout != null) {
                    height += AndroidUtilities.m9dp(8.0f);
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
                    canvas.drawRect((float) AndroidUtilities.m9dp(18.0f), 0.0f, (float) AndroidUtilities.m9dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.m9dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockEmbedPostCell */
    private class BlockEmbedPostCell extends View {
        private AvatarDrawable avatarDrawable;
        private ImageReceiver avatarImageView = new ImageReceiver(this);
        private boolean avatarVisible;
        private DrawingText captionLayout;
        private int captionX = AndroidUtilities.m9dp(18.0f);
        private int captionY;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockEmbedPost currentBlock;
        private DrawingText dateLayout;
        private int dateX;
        private int lineHeight;
        private DrawingText nameLayout;
        private int nameX;
        private int textX = AndroidUtilities.m9dp(32.0f);
        private int textY = AndroidUtilities.m9dp(56.0f);

        public BlockEmbedPostCell(Context context) {
            super(context);
            this.avatarImageView.setRoundRadius(AndroidUtilities.m9dp(20.0f));
            this.avatarImageView.setImageCoords(AndroidUtilities.m9dp(32.0f), AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(40.0f), AndroidUtilities.m9dp(40.0f));
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
                        PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.m9dp(40.0f), true);
                        this.avatarImageView.setImage(image != null ? image.location : null, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(40), Integer.valueOf(40)}), this.avatarDrawable, 0, null, ArticleViewer.this.currentPage, 1);
                    }
                }
                this.nameLayout = ArticleViewer.this.createLayoutForText(this, this.currentBlock.author, null, width - AndroidUtilities.m9dp((float) ((this.avatarVisible ? 54 : 0) + 50)), this.currentBlock);
                if (this.currentBlock.date != 0) {
                    this.dateLayout = ArticleViewer.this.createLayoutForText(this, LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.date) * 1000), null, width - AndroidUtilities.m9dp((float) ((this.avatarVisible ? 54 : 0) + 50)), this.currentBlock);
                } else {
                    this.dateLayout = null;
                }
                height = AndroidUtilities.m9dp(56.0f);
                int textWidth = width - AndroidUtilities.m9dp(50.0f);
                this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock);
                if (this.captionLayout != null) {
                    this.creditOffset = AndroidUtilities.m9dp(4.0f) + this.captionLayout.getHeight();
                    height += this.creditOffset + AndroidUtilities.m9dp(4.0f);
                }
                this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL);
                if (this.creditLayout != null) {
                    height += AndroidUtilities.m9dp(4.0f) + this.creditLayout.getHeight();
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
                    canvas.translate((float) AndroidUtilities.m9dp((float) ((this.avatarVisible ? 54 : 0) + 32)), (float) AndroidUtilities.m9dp(this.dateLayout != null ? 10.0f : 19.0f));
                    this.nameLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.dateLayout != null) {
                    canvas.save();
                    if (!this.avatarVisible) {
                        i = 0;
                    }
                    canvas.translate((float) AndroidUtilities.m9dp((float) (i + 32)), (float) AndroidUtilities.m9dp(29.0f));
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
                float dp = (float) AndroidUtilities.m9dp(18.0f);
                float dp2 = (float) AndroidUtilities.m9dp(6.0f);
                float dp3 = (float) AndroidUtilities.m9dp(20.0f);
                int i3 = this.lineHeight;
                if (this.currentBlock.level == 0) {
                    i2 = AndroidUtilities.m9dp(6.0f);
                }
                canvas.drawRect(dp, dp2, dp3, (float) (i3 - i2), ArticleViewer.quoteLinePaint);
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockFooterCell */
    private class BlockFooterCell extends View {
        private TL_pageBlockFooter currentBlock;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.m9dp(18.0f);
        private int textY = AndroidUtilities.m9dp(8.0f);

        public BlockFooterCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockFooter block) {
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
                if (this.currentBlock.level == 0) {
                    this.textY = AndroidUtilities.m9dp(8.0f);
                    this.textX = AndroidUtilities.m9dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.m9dp((float) ((this.currentBlock.level * 14) + 18));
                }
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, (width - AndroidUtilities.m9dp(18.0f)) - this.textX, this.currentBlock);
                if (this.textLayout != null) {
                    height = this.textLayout.getHeight();
                    if (this.currentBlock.level > 0) {
                        height += AndroidUtilities.m9dp(8.0f);
                    } else {
                        height += AndroidUtilities.m9dp(16.0f);
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
                    canvas.drawRect((float) AndroidUtilities.m9dp(18.0f), 0.0f, (float) AndroidUtilities.m9dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.m9dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockHeaderCell */
    private class BlockHeaderCell extends View {
        private TL_pageBlockHeader currentBlock;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.m9dp(18.0f);
        private int textY = AndroidUtilities.m9dp(8.0f);

        public BlockHeaderCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockHeader block) {
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
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, width - AndroidUtilities.m9dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.m9dp(16.0f) + this.textLayout.getHeight());
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockKickerCell */
    private class BlockKickerCell extends View {
        private TL_pageBlockKicker currentBlock;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.m9dp(18.0f);
        private int textY;

        public BlockKickerCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockKicker block) {
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
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, width - AndroidUtilities.m9dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.m9dp(16.0f) + this.textLayout.getHeight());
                }
                if (this.currentBlock.first) {
                    height += AndroidUtilities.m9dp(8.0f);
                    this.textY = AndroidUtilities.m9dp(16.0f);
                } else {
                    this.textY = AndroidUtilities.m9dp(8.0f);
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockListItemCell */
    private class BlockListItemCell extends ViewGroup {
        private ViewHolder blockLayout;
        private int blockX;
        private int blockY;
        private TL_pageBlockListItem currentBlock;
        private int currentBlockType;
        private boolean drawDot;
        private int numOffsetY;
        private boolean parentIsList;
        private DrawingText textLayout;
        private int textX;
        private int textY;
        private boolean verticalAlign;

        public BlockListItemCell(Context context) {
            super(context);
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
                    this.currentBlockType = ArticleViewer.this.adapter.getTypeForBlock(this.currentBlock.blockItem);
                    this.blockLayout = ArticleViewer.this.adapter.onCreateViewHolder(this, this.currentBlockType);
                    addView(this.blockLayout.itemView);
                }
            }
            if (this.currentBlock.blockItem != null) {
                ArticleViewer.this.adapter.bindBlockToHolder(this.currentBlockType, this.blockLayout, this.currentBlock.blockItem, 0, 0);
            }
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(event);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                this.textLayout = null;
                int dp = (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) ? AndroidUtilities.m9dp(10.0f) : 0;
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
                            item.numLayout = ArticleViewer.this.createLayoutForText(this, item.num, null, width - AndroidUtilities.m9dp(54.0f), this.currentBlock);
                            this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) item.numLayout.getLineWidth(0)));
                        }
                    }
                    this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) ArticleViewer.listTextNumPaint.measureText("00.")));
                }
                this.drawDot = !this.currentBlock.parent.pageBlockList.ordered;
                boolean z = (getParent() instanceof BlockListItemCell) || (getParent() instanceof BlockOrderedListItemCell);
                this.parentIsList = z;
                if (ArticleViewer.this.isRtl) {
                    this.textX = AndroidUtilities.m9dp(18.0f);
                } else {
                    this.textX = (AndroidUtilities.m9dp(24.0f) + this.currentBlock.parent.maxNumWidth) + (this.currentBlock.parent.level * AndroidUtilities.m9dp(12.0f));
                }
                int maxWidth = (width - AndroidUtilities.m9dp(18.0f)) - this.textX;
                if (this.currentBlock.textItem != null) {
                    this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.textItem, maxWidth, this.currentBlock);
                    if (this.textLayout != null && this.textLayout.getLineCount() > 0) {
                        if (this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            this.numOffsetY = (this.currentBlock.numLayout.getLineAscent(0) + AndroidUtilities.m9dp(2.5f)) - this.textLayout.getLineAscent(0);
                        }
                        height = 0 + (this.textLayout.getHeight() + AndroidUtilities.m9dp(8.0f));
                    }
                } else if (this.currentBlock.blockItem != null) {
                    this.blockX = this.textX;
                    this.blockY = this.textY;
                    if (this.blockLayout != null) {
                        if (this.blockLayout.itemView instanceof BlockParagraphCell) {
                            this.blockY -= AndroidUtilities.m9dp(8.0f);
                            if (!ArticleViewer.this.isRtl) {
                                this.blockX -= AndroidUtilities.m9dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.m9dp(18.0f);
                            height = 0 - AndroidUtilities.m9dp(8.0f);
                        } else if ((this.blockLayout.itemView instanceof BlockHeaderCell) || (this.blockLayout.itemView instanceof BlockSubheaderCell) || (this.blockLayout.itemView instanceof BlockTitleCell) || (this.blockLayout.itemView instanceof BlockSubtitleCell)) {
                            if (!ArticleViewer.this.isRtl) {
                                this.blockX -= AndroidUtilities.m9dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.m9dp(18.0f);
                        } else if (ArticleViewer.this.isListItemBlock(this.currentBlock.blockItem)) {
                            this.blockX = 0;
                            this.blockY = 0;
                            this.textY = 0;
                            if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                                height = 0 - AndroidUtilities.m9dp(10.0f);
                            }
                            maxWidth = width;
                            height -= AndroidUtilities.m9dp(8.0f);
                        } else if (this.blockLayout.itemView instanceof BlockTableCell) {
                            this.blockX -= AndroidUtilities.m9dp(18.0f);
                            maxWidth += AndroidUtilities.m9dp(36.0f);
                        }
                        this.blockLayout.itemView.measure(MeasureSpec.makeMeasureSpec(maxWidth, NUM), MeasureSpec.makeMeasureSpec(0, 0));
                        if ((this.blockLayout.itemView instanceof BlockParagraphCell) && this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            BlockParagraphCell paragraphCell = this.blockLayout.itemView;
                            if (paragraphCell.textLayout != null && paragraphCell.textLayout.getLineCount() > 0) {
                                this.numOffsetY = (this.currentBlock.numLayout.getLineAscent(0) + AndroidUtilities.m9dp(2.5f)) - paragraphCell.textLayout.getLineAscent(0);
                            }
                        }
                        if (this.currentBlock.blockItem instanceof TL_pageBlockDetails) {
                            this.verticalAlign = true;
                            this.blockY = 0;
                            if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                                height -= AndroidUtilities.m9dp(10.0f);
                            }
                            height -= AndroidUtilities.m9dp(8.0f);
                        } else if (this.blockLayout.itemView instanceof BlockOrderedListItemCell) {
                            this.verticalAlign = ((BlockOrderedListItemCell) this.blockLayout.itemView).verticalAlign;
                        } else if (this.blockLayout.itemView instanceof BlockListItemCell) {
                            this.verticalAlign = ((BlockListItemCell) this.blockLayout.itemView).verticalAlign;
                        }
                        if (this.verticalAlign && this.currentBlock.numLayout != null) {
                            this.textY = ((this.blockLayout.itemView.getMeasuredHeight() - this.currentBlock.numLayout.getHeight()) / 2) - AndroidUtilities.m9dp(4.0f);
                            this.drawDot = false;
                        }
                        height += this.blockLayout.itemView.getMeasuredHeight();
                    }
                    height += AndroidUtilities.m9dp(8.0f);
                }
                if (this.currentBlock.parent.items.get(this.currentBlock.parent.items.size() - 1) == this.currentBlock) {
                    height += AndroidUtilities.m9dp(8.0f);
                }
                if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                    height += AndroidUtilities.m9dp(10.0f);
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
                        dp = (float) ((width - AndroidUtilities.m9dp(18.0f)) - ((int) Math.ceil((double) this.currentBlock.numLayout.getLineWidth(0))));
                        i2 = this.textY + this.numOffsetY;
                        if (this.drawDot) {
                            i = AndroidUtilities.m9dp(1.0f);
                        }
                        canvas.translate(dp, (float) (i2 - i));
                    } else {
                        dp = (float) (((AndroidUtilities.m9dp(15.0f) + this.currentBlock.parent.maxNumWidth) - ((int) Math.ceil((double) this.currentBlock.numLayout.getLineWidth(0)))) + (this.currentBlock.parent.level * AndroidUtilities.m9dp(12.0f)));
                        i2 = this.textY + this.numOffsetY;
                        if (this.drawDot) {
                            i = AndroidUtilities.m9dp(1.0f);
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockMapCell */
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
        private boolean photoPressed;
        private int textX;
        private int textY;

        public BlockMapCell(Context context, int type) {
            super(context);
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
                    FileLog.m13e(e);
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
                    this.textX = AndroidUtilities.m9dp(18.0f);
                    textWidth = width - AndroidUtilities.m9dp(36.0f);
                } else {
                    photoX = AndroidUtilities.m9dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.m9dp(18.0f);
                    this.textX = photoX;
                    photoWidth -= AndroidUtilities.m9dp(18.0f) + photoX;
                    textWidth = photoWidth;
                }
                if (this.currentType == 0) {
                    height = (int) (((float) this.currentBlock.var_h) * (((float) photoWidth) / ((float) this.currentBlock.var_w)));
                    int maxHeight = (int) (((float) (Math.max(ArticleViewer.this.listView.getMeasuredWidth(), ArticleViewer.this.listView.getMeasuredHeight()) - AndroidUtilities.m9dp(56.0f))) * 0.9f);
                    if (height > maxHeight) {
                        height = maxHeight;
                        photoWidth = (int) (((float) this.currentBlock.var_w) * (((float) height) / ((float) this.currentBlock.var_h)));
                        photoX += ((width - photoX) - photoWidth) / 2;
                    }
                }
                ImageReceiver imageReceiver = this.imageView;
                int dp = (this.isFirst || this.currentType == 1 || this.currentType == 2 || this.currentBlock.level > 0) ? 0 : AndroidUtilities.m9dp(8.0f);
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
                    this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock);
                    if (this.captionLayout != null) {
                        this.creditOffset = AndroidUtilities.m9dp(4.0f) + this.captionLayout.getHeight();
                        height += this.creditOffset + AndroidUtilities.m9dp(4.0f);
                    }
                    this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL);
                    if (this.creditLayout != null) {
                        height += AndroidUtilities.m9dp(4.0f) + this.creditLayout.getHeight();
                    }
                }
                if (!this.isFirst && this.currentType == 0 && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.m9dp(8.0f);
                }
                if (this.currentType != 2) {
                    height += AndroidUtilities.m9dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
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
                this.textY = (this.imageView.getImageY() + this.imageView.getImageHeight()) + AndroidUtilities.m9dp(8.0f);
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
                    canvas.drawRect((float) AndroidUtilities.m9dp(18.0f), 0.0f, (float) AndroidUtilities.m9dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.m9dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockOrderedListItemCell */
    private class BlockOrderedListItemCell extends ViewGroup {
        private ViewHolder blockLayout;
        private int blockX;
        private int blockY;
        private TL_pageBlockOrderedListItem currentBlock;
        private int currentBlockType;
        private int numOffsetY;
        private boolean parentIsList;
        private DrawingText textLayout;
        private int textX;
        private int textY;
        private boolean verticalAlign;

        public BlockOrderedListItemCell(Context context) {
            super(context);
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
                    this.currentBlockType = ArticleViewer.this.adapter.getTypeForBlock(this.currentBlock.blockItem);
                    this.blockLayout = ArticleViewer.this.adapter.onCreateViewHolder(this, this.currentBlockType);
                    addView(this.blockLayout.itemView);
                }
            }
            if (this.currentBlock.blockItem != null) {
                ArticleViewer.this.adapter.bindBlockToHolder(this.currentBlockType, this.blockLayout, this.currentBlock.blockItem, 0, 0);
            }
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (ArticleViewer.this.checkLayoutForLinks(event, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(event);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                this.textLayout = null;
                int dp = (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) ? AndroidUtilities.m9dp(10.0f) : 0;
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
                            item.numLayout = ArticleViewer.this.createLayoutForText(this, item.num, null, width - AndroidUtilities.m9dp(54.0f), this.currentBlock);
                            this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) item.numLayout.getLineWidth(0)));
                        }
                    }
                    this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) ArticleViewer.listTextNumPaint.measureText("00.")));
                }
                if (ArticleViewer.this.isRtl) {
                    this.textX = AndroidUtilities.m9dp(18.0f);
                } else {
                    this.textX = (AndroidUtilities.m9dp(24.0f) + this.currentBlock.parent.maxNumWidth) + (this.currentBlock.parent.level * AndroidUtilities.m9dp(20.0f));
                }
                this.verticalAlign = false;
                int maxWidth = (width - AndroidUtilities.m9dp(18.0f)) - this.textX;
                if (this.currentBlock.textItem != null) {
                    this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.textItem, maxWidth, this.currentBlock);
                    if (this.textLayout != null && this.textLayout.getLineCount() > 0) {
                        if (this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            this.numOffsetY = this.currentBlock.numLayout.getLineAscent(0) - this.textLayout.getLineAscent(0);
                        }
                        height = 0 + (this.textLayout.getHeight() + AndroidUtilities.m9dp(8.0f));
                    }
                } else if (this.currentBlock.blockItem != null) {
                    this.blockX = this.textX;
                    this.blockY = this.textY;
                    if (this.blockLayout != null) {
                        if (this.blockLayout.itemView instanceof BlockParagraphCell) {
                            this.blockY -= AndroidUtilities.m9dp(8.0f);
                            if (!ArticleViewer.this.isRtl) {
                                this.blockX -= AndroidUtilities.m9dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.m9dp(18.0f);
                            height = 0 - AndroidUtilities.m9dp(8.0f);
                        } else if ((this.blockLayout.itemView instanceof BlockHeaderCell) || (this.blockLayout.itemView instanceof BlockSubheaderCell) || (this.blockLayout.itemView instanceof BlockTitleCell) || (this.blockLayout.itemView instanceof BlockSubtitleCell)) {
                            if (!ArticleViewer.this.isRtl) {
                                this.blockX -= AndroidUtilities.m9dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.m9dp(18.0f);
                        } else if (ArticleViewer.this.isListItemBlock(this.currentBlock.blockItem)) {
                            this.blockX = 0;
                            this.blockY = 0;
                            this.textY = 0;
                            maxWidth = width;
                            height = 0 - AndroidUtilities.m9dp(8.0f);
                        } else if (this.blockLayout.itemView instanceof BlockTableCell) {
                            this.blockX -= AndroidUtilities.m9dp(18.0f);
                            maxWidth += AndroidUtilities.m9dp(36.0f);
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
                            height -= AndroidUtilities.m9dp(8.0f);
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
                    height += AndroidUtilities.m9dp(8.0f);
                }
                if (this.currentBlock.parent.items.get(this.currentBlock.parent.items.size() - 1) == this.currentBlock) {
                    height += AndroidUtilities.m9dp(8.0f);
                }
                if (this.currentBlock.index == 0 && this.currentBlock.parent.level == 0) {
                    height += AndroidUtilities.m9dp(10.0f);
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
                        canvas.translate((float) ((width - AndroidUtilities.m9dp(18.0f)) - ((int) Math.ceil((double) this.currentBlock.numLayout.getLineWidth(0)))), (float) (this.textY + this.numOffsetY));
                    } else {
                        canvas.translate((float) (((AndroidUtilities.m9dp(18.0f) + this.currentBlock.parent.maxNumWidth) - ((int) Math.ceil((double) this.currentBlock.numLayout.getLineWidth(0)))) + (this.currentBlock.parent.level * AndroidUtilities.m9dp(20.0f))), (float) (this.textY + this.numOffsetY));
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockParagraphCell */
    private class BlockParagraphCell extends View {
        private TL_pageBlockParagraph currentBlock;
        private DrawingText textLayout;
        private int textX;
        private int textY;

        public BlockParagraphCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockParagraph block) {
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
                if (this.currentBlock.level == 0) {
                    this.textY = AndroidUtilities.m9dp(8.0f);
                    this.textX = AndroidUtilities.m9dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.m9dp((float) ((this.currentBlock.level * 14) + 18));
                }
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.text, (width - AndroidUtilities.m9dp(18.0f)) - this.textX, this.textY, this.currentBlock);
                if (this.textLayout != null) {
                    height = this.textLayout.getHeight();
                    if (this.currentBlock.level > 0) {
                        height += AndroidUtilities.m9dp(8.0f);
                    } else {
                        height += AndroidUtilities.m9dp(16.0f);
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
                    canvas.drawRect((float) AndroidUtilities.m9dp(18.0f), 0.0f, (float) AndroidUtilities.m9dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.m9dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockPreformattedCell */
    private class BlockPreformattedCell extends FrameLayout {
        private TL_pageBlockPreformatted currentBlock;
        private HorizontalScrollView scrollView;
        private View textContainer;
        private DrawingText textLayout;

        public BlockPreformattedCell(Context context) {
            super(context);
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
            this.scrollView.setPadding(0, AndroidUtilities.m9dp(8.0f), 0, AndroidUtilities.m9dp(8.0f));
            addView(this.scrollView, LayoutHelper.createFrame(-1, -2.0f));
            this.textContainer = new View(context, ArticleViewer.this) {
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int height = 0;
                    int width = 1;
                    if (BlockPreformattedCell.this.currentBlock != null) {
                        BlockPreformattedCell.this.textLayout = ArticleViewer.this.createLayoutForText(this, null, BlockPreformattedCell.this.currentBlock.text, AndroidUtilities.m9dp(5000.0f), BlockPreformattedCell.this.currentBlock);
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
                    setMeasuredDimension(AndroidUtilities.m9dp(32.0f) + width, height);
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
            int dp = AndroidUtilities.m9dp(16.0f);
            layoutParams.rightMargin = dp;
            layoutParams.leftMargin = dp;
            dp = AndroidUtilities.m9dp(12.0f);
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
                canvas.drawRect(0.0f, (float) AndroidUtilities.m9dp(8.0f), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.m9dp(8.0f)), ArticleViewer.preformattedBackgroundPaint);
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockPullquoteCell */
    private class BlockPullquoteCell extends View {
        private TL_pageBlockPullquote currentBlock;
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textX = AndroidUtilities.m9dp(18.0f);
        private int textY = AndroidUtilities.m9dp(8.0f);
        private int textY2;

        public BlockPullquoteCell(Context context) {
            super(context);
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
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, width - AndroidUtilities.m9dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.m9dp(8.0f) + this.textLayout.getHeight());
                }
                this.textLayout2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption, width - AndroidUtilities.m9dp(36.0f), this.currentBlock);
                if (this.textLayout2 != null) {
                    this.textY2 = AndroidUtilities.m9dp(2.0f) + height;
                    height += AndroidUtilities.m9dp(8.0f) + this.textLayout2.getHeight();
                }
                if (height != 0) {
                    height += AndroidUtilities.m9dp(8.0f);
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockRelatedArticlesCell */
    private class BlockRelatedArticlesCell extends View {
        private int additionalHeight;
        private TL_pageBlockRelatedArticlesChild currentBlock;
        private boolean divider;
        private boolean drawImage;
        private ImageReceiver imageView = new ImageReceiver(this);
        private DrawingText textLayout;
        private DrawingText textLayout2;
        private int textOffset;
        private int textX = AndroidUtilities.m9dp(18.0f);
        private int textY = AndroidUtilities.m9dp(10.0f);

        public BlockRelatedArticlesCell(Context context) {
            super(context);
            this.imageView.setRoundRadius(AndroidUtilities.m9dp(6.0f));
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
                this.additionalHeight = -AndroidUtilities.m9dp(4.0f);
            } else if (ArticleViewer.this.selectedFontSize == 1) {
                this.additionalHeight = -AndroidUtilities.m9dp(2.0f);
            } else if (ArticleViewer.this.selectedFontSize == 3) {
                this.additionalHeight = AndroidUtilities.m9dp(2.0f);
            } else if (ArticleViewer.this.selectedFontSize == 4) {
                this.additionalHeight = AndroidUtilities.m9dp(4.0f);
            }
            Photo photo = item.photo_id != 0 ? ArticleViewer.this.getPhotoWithId(item.photo_id) : null;
            if (photo != null) {
                this.drawImage = true;
                PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80, true);
                if (image == thumb) {
                    thumb = null;
                }
                this.imageView.setImage(image.location, "64_64", thumb != null ? thumb.location : null, thumb != null ? "64_64_b" : null, image.size, null, ArticleViewer.this.currentPage, 1);
            } else {
                this.drawImage = false;
            }
            int layoutHeight = AndroidUtilities.m9dp(60.0f);
            int availableWidth = width - AndroidUtilities.m9dp(36.0f);
            if (this.drawImage) {
                int imageWidth = AndroidUtilities.m9dp(44.0f);
                this.imageView.setImageCoords((width - imageWidth) - AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(8.0f), imageWidth, imageWidth);
                availableWidth -= this.imageView.getImageWidth() + AndroidUtilities.m9dp(6.0f);
            }
            int height = AndroidUtilities.m9dp(18.0f);
            boolean isTitleRtl = false;
            if (item.title != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, item.title, null, availableWidth, this.textY, this.currentBlock, Alignment.ALIGN_NORMAL, 3);
            }
            int lineCount = 4;
            if (this.textLayout != null) {
                int count = this.textLayout.getLineCount();
                lineCount = 4 - count;
                this.textOffset = (this.textLayout.getHeight() + AndroidUtilities.m9dp(6.0f)) + this.additionalHeight;
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
            int i = this.textOffset + this.textY;
            PageBlock pageBlock = this.currentBlock;
            Alignment alignment = (ArticleViewer.this.isRtl || isTitleRtl) ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL;
            this.textLayout2 = articleViewer.createLayoutForText(this, description, null, availableWidth, i, pageBlock, alignment, lineCount);
            if (this.textLayout2 != null) {
                height += this.textLayout2.getHeight();
                if (this.textLayout != null) {
                    height += AndroidUtilities.m9dp(6.0f) + this.additionalHeight;
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
                canvas.translate((float) this.textX, (float) AndroidUtilities.m9dp(10.0f));
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
                        f = (float) AndroidUtilities.m9dp(17.0f);
                    }
                    canvas.drawLine(f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (ArticleViewer.this.isRtl ? AndroidUtilities.m9dp(17.0f) : 0)), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockRelatedArticlesHeaderCell */
    private class BlockRelatedArticlesHeaderCell extends View {
        private TL_pageBlockRelatedArticles currentBlock;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.m9dp(18.0f);
        private int textY;

        public BlockRelatedArticlesHeaderCell(Context context) {
            super(context);
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
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.title, width - AndroidUtilities.m9dp(52.0f), 0, this.currentBlock, Alignment.ALIGN_NORMAL, 1);
                if (this.textLayout != null) {
                    this.textY = AndroidUtilities.m9dp(6.0f) + ((AndroidUtilities.m9dp(32.0f) - this.textLayout.getHeight()) / 2);
                }
            }
            if (this.textLayout != null) {
                setMeasuredDimension(width, AndroidUtilities.m9dp(38.0f));
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockRelatedArticlesShadowCell */
    private class BlockRelatedArticlesShadowCell extends View {
        private CombinedDrawable shadowDrawable;

        public BlockRelatedArticlesShadowCell(Context context) {
            super(context);
            this.shadowDrawable = new CombinedDrawable(new ColorDrawable(-986896), Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, (int) Theme.ACTION_BAR_VIDEO_EDIT_COLOR));
            this.shadowDrawable.setFullsize(true);
            setBackgroundDrawable(this.shadowDrawable);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.m9dp(12.0f));
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockSlideshowCell */
    private class BlockSlideshowCell extends FrameLayout {
        private PagerAdapter adapter;
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TL_pageBlockSlideshow currentBlock;
        private int currentPage;
        private View dotsContainer;
        private ViewPager innerListView;
        private float pageOffset;
        private int textX = AndroidUtilities.m9dp(18.0f);
        private int textY;

        public BlockSlideshowCell(Context context) {
            super(context);
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
            PagerAdapter CLASSNAME = new PagerAdapter(ArticleViewer.this) {

                /* renamed from: org.telegram.ui.ArticleViewer$BlockSlideshowCell$3$ObjectContainer */
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
                        view = new BlockPhotoCell(BlockSlideshowCell.this.getContext(), 1);
                        ((BlockPhotoCell) view).setBlock((TL_pageBlockPhoto) block, true, true);
                    } else {
                        view = new BlockVideoCell(BlockSlideshowCell.this.getContext(), 1);
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
            this.adapter = CLASSNAME;
            viewPager.setAdapter(CLASSNAME);
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
                        int count = BlockSlideshowCell.this.adapter.getCount();
                        int totalWidth = ((AndroidUtilities.m9dp(7.0f) * count) + ((count - 1) * AndroidUtilities.m9dp(6.0f))) + AndroidUtilities.m9dp(4.0f);
                        if (totalWidth < getMeasuredWidth()) {
                            xOffset = (getMeasuredWidth() - totalWidth) / 2;
                        } else {
                            xOffset = AndroidUtilities.m9dp(4.0f);
                            int size = AndroidUtilities.m9dp(13.0f);
                            int halfCount = ((getMeasuredWidth() - AndroidUtilities.m9dp(8.0f)) / 2) / size;
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
                            int cx = (AndroidUtilities.m9dp(4.0f) + xOffset) + (AndroidUtilities.m9dp(13.0f) * a);
                            Drawable drawable = BlockSlideshowCell.this.currentPage == a ? ArticleViewer.this.slideDotBigDrawable : ArticleViewer.this.slideDotDrawable;
                            drawable.setBounds(cx - AndroidUtilities.m9dp(5.0f), 0, AndroidUtilities.m9dp(5.0f) + cx, AndroidUtilities.m9dp(10.0f));
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
            this.adapter.notifyDataSetChanged();
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
                height = AndroidUtilities.m9dp(310.0f);
                this.innerListView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                int count = this.currentBlock.items.size();
                this.dotsContainer.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(10.0f), NUM));
                int textWidth = width - AndroidUtilities.m9dp(36.0f);
                this.textY = AndroidUtilities.m9dp(16.0f) + height;
                this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock);
                if (this.captionLayout != null) {
                    this.creditOffset = AndroidUtilities.m9dp(4.0f) + this.captionLayout.getHeight();
                    height += this.creditOffset + AndroidUtilities.m9dp(4.0f);
                }
                this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL);
                if (this.creditLayout != null) {
                    height += AndroidUtilities.m9dp(4.0f) + this.creditLayout.getHeight();
                }
                height += AndroidUtilities.m9dp(16.0f);
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.innerListView.layout(0, AndroidUtilities.m9dp(8.0f), this.innerListView.getMeasuredWidth(), AndroidUtilities.m9dp(8.0f) + this.innerListView.getMeasuredHeight());
            int y = this.innerListView.getBottom() - AndroidUtilities.m9dp(23.0f);
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockSubheaderCell */
    private class BlockSubheaderCell extends View {
        private TL_pageBlockSubheader currentBlock;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.m9dp(18.0f);
        private int textY = AndroidUtilities.m9dp(8.0f);

        public BlockSubheaderCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockSubheader block) {
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
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, width - AndroidUtilities.m9dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.m9dp(16.0f) + this.textLayout.getHeight());
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockSubtitleCell */
    private class BlockSubtitleCell extends View {
        private TL_pageBlockSubtitle currentBlock;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.m9dp(18.0f);
        private int textY = AndroidUtilities.m9dp(8.0f);

        public BlockSubtitleCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockSubtitle block) {
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
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, width - AndroidUtilities.m9dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.m9dp(16.0f) + this.textLayout.getHeight());
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockTitleCell */
    private class BlockTitleCell extends View {
        private TL_pageBlockTitle currentBlock;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.m9dp(18.0f);
        private int textY;

        public BlockTitleCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockTitle block) {
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
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, width - AndroidUtilities.m9dp(36.0f), this.currentBlock);
                if (this.textLayout != null) {
                    height = 0 + (AndroidUtilities.m9dp(16.0f) + this.textLayout.getHeight());
                }
                if (this.currentBlock.first) {
                    height += AndroidUtilities.m9dp(8.0f);
                    this.textY = AndroidUtilities.m9dp(16.0f);
                } else {
                    this.textY = AndroidUtilities.m9dp(8.0f);
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

    /* renamed from: org.telegram.ui.ArticleViewer$CheckForLongPress */
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
                    int y = (location[1] + ArticleViewer.this.pressedLayoutY) - AndroidUtilities.m9dp(54.0f);
                    if (y < 0) {
                        y = 0;
                    }
                    ArticleViewer.this.pressedLinkOwnerView.invalidate();
                    ArticleViewer.this.drawBlockSelection = true;
                    ArticleViewer.this.showPopup(ArticleViewer.this.pressedLinkOwnerView, 48, 0, y);
                    ArticleViewer.this.listView.setLayoutFrozen(true);
                    ArticleViewer.this.listView.setLayoutFrozen(false);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$CheckForTap */
    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        /* synthetic */ CheckForTap(ArticleViewer x0, CLASSNAME x1) {
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

    /* renamed from: org.telegram.ui.ArticleViewer$ColorCell */
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
                ArticleViewer.selectorPaint.setStrokeWidth((float) AndroidUtilities.m9dp(2.0f));
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
            this.textView.setPadding(0, 0, 0, AndroidUtilities.m9dp(1.0f));
            View view = this.textView;
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            addView(view, LayoutHelper.createFrame(-1, -1.0f, i2 | 48, (float) (LocaleController.isRTL ? 17 : 53), 0.0f, (float) (LocaleController.isRTL ? 53 : 17), 0.0f));
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(48.0f), NUM));
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
            canvas.drawCircle(!LocaleController.isRTL ? (float) AndroidUtilities.m9dp(28.0f) : (float) (getMeasuredWidth() - AndroidUtilities.m9dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.m9dp(10.0f), ArticleViewer.colorPaint);
            if (this.selected) {
                ArticleViewer.selectorPaint.setStrokeWidth((float) AndroidUtilities.m9dp(2.0f));
                ArticleViewer.selectorPaint.setColor(-15428119);
                canvas.drawCircle(!LocaleController.isRTL ? (float) AndroidUtilities.m9dp(28.0f) : (float) (getMeasuredWidth() - AndroidUtilities.m9dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.m9dp(10.0f), ArticleViewer.selectorPaint);
            } else if (this.currentColor == -1) {
                ArticleViewer.selectorPaint.setStrokeWidth((float) AndroidUtilities.m9dp(1.0f));
                ArticleViewer.selectorPaint.setColor(-4539718);
                canvas.drawCircle(!LocaleController.isRTL ? (float) AndroidUtilities.m9dp(28.0f) : (float) (getMeasuredWidth() - AndroidUtilities.m9dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.m9dp(9.0f), ArticleViewer.selectorPaint);
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$DrawingText */
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

    /* renamed from: org.telegram.ui.ArticleViewer$FontCell */
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
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(48.0f), NUM));
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

    /* renamed from: org.telegram.ui.ArticleViewer$FrameLayoutDrawer */
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

    /* renamed from: org.telegram.ui.ArticleViewer$PhotoBackgroundDrawable */
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

    /* renamed from: org.telegram.ui.ArticleViewer$PlaceProviderObject */
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

    /* renamed from: org.telegram.ui.ArticleViewer$RadialProgressView */
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
        private int size = AndroidUtilities.m9dp(64.0f);

        public RadialProgressView(Context context, View parentView) {
            if (ArticleViewer.decelerateInterpolator == null) {
                ArticleViewer.decelerateInterpolator = new DecelerateInterpolator(1.5f);
                ArticleViewer.progressPaint = new Paint(1);
                ArticleViewer.progressPaint.setStyle(Style.STROKE);
                ArticleViewer.progressPaint.setStrokeCap(Cap.ROUND);
                ArticleViewer.progressPaint.setStrokeWidth((float) AndroidUtilities.m9dp(3.0f));
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
                int diff = AndroidUtilities.m9dp(4.0f);
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

    /* renamed from: org.telegram.ui.ArticleViewer$ScrollEvaluator */
    public class ScrollEvaluator extends IntEvaluator {
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            return super.evaluate(fraction, startValue, endValue);
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$SizeChooseView */
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
                    if (x <= ((float) (cx - AndroidUtilities.m9dp(15.0f))) || x >= ((float) (AndroidUtilities.m9dp(15.0f) + cx))) {
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
                        if (x <= ((float) (cx - AndroidUtilities.m9dp(15.0f))) || x >= ((float) (AndroidUtilities.m9dp(15.0f) + cx))) {
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
            this.circleSize = AndroidUtilities.m9dp(5.0f);
            this.gapSize = AndroidUtilities.m9dp(2.0f);
            this.sideSide = AndroidUtilities.m9dp(17.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * 5)) - (this.gapSize * 8)) - (this.sideSide * 2)) / 4;
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
                canvas.drawCircle((float) cx, (float) cy, a == ArticleViewer.this.selectedFontSize ? (float) AndroidUtilities.m9dp(4.0f) : (float) (this.circleSize / 2), this.paint);
                if (a != 0) {
                    int x = ((cx - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    canvas.drawRect((float) x, (float) (cy - AndroidUtilities.m9dp(1.0f)), (float) (this.lineSize + x), (float) (AndroidUtilities.m9dp(1.0f) + cy), this.paint);
                }
                a++;
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$WindowView */
    private class WindowView extends FrameLayout {
        private float alpha;
        private Runnable attachRunnable;
        private int bHeight;
        private int bWidth;
        /* renamed from: bX */
        private int var_bX;
        /* renamed from: bY */
        private int var_bY;
        private boolean closeAnimationInProgress;
        private float innerTranslationX;
        private boolean maybeStartTracking;
        private boolean selfLayout;
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
                    for (Entry<String, Integer> entry : ArticleViewer.this.anchorsOffset.entrySet()) {
                        entry.setValue(Integer.valueOf(-1));
                    }
                    ArticleViewer.this.anchorsOffsetMeasuredWidth = width;
                }
                if (VERSION.SDK_INT < 21 || ArticleViewer.this.lastInsets == null) {
                    x = 0;
                } else {
                    WindowInsets insets = (WindowInsets) ArticleViewer.this.lastInsets;
                    x = insets.getSystemWindowInsetLeft();
                    if (insets.getSystemWindowInsetRight() != 0) {
                        this.var_bX = width - this.bWidth;
                        this.var_bY = 0;
                    } else if (insets.getSystemWindowInsetLeft() != 0) {
                        this.var_bX = 0;
                        this.var_bY = 0;
                    } else {
                        this.var_bX = 0;
                        this.var_bY = (bottom - top) - this.bHeight;
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
                float alpha = Math.max(0.0f, Math.min(((float) (width - translationX)) / ((float) AndroidUtilities.m9dp(20.0f)), 1.0f));
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
                    ArticleViewer.this.containerView.setTranslationX((float) dx);
                    setInnerTranslationX((float) dx);
                }
            } else if (event != null && event.getPointerId(0) == this.startedTrackingPointerId && (event.getAction() == 3 || event.getAction() == 1 || event.getAction() == 6)) {
                float velX;
                if (this.tracker == null) {
                    this.tracker = VelocityTracker.obtain();
                }
                this.tracker.computeCurrentVelocity(1000);
                if (!this.startedTracking) {
                    velX = this.tracker.getXVelocity();
                    float velY = this.tracker.getYVelocity();
                    if (velX >= 3500.0f && velX > Math.abs(velY)) {
                        prepareForMoving(event);
                    }
                }
                if (this.startedTracking) {
                    float distToMove;
                    float x = ArticleViewer.this.containerView.getX();
                    AnimatorSet animatorSet = new AnimatorSet();
                    velX = this.tracker.getXVelocity();
                    final boolean backAnimation = x < ((float) ArticleViewer.this.containerView.getMeasuredWidth()) / 3.0f && (velX < 3500.0f || velX < this.tracker.getYVelocity());
                    Animator[] animatorArr;
                    if (backAnimation) {
                        distToMove = x;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.containerView, "translationX", new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                    } else {
                        distToMove = ((float) ArticleViewer.this.containerView.getMeasuredWidth()) - x;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.containerView, "translationX", new float[]{(float) ArticleViewer.this.containerView.getMeasuredWidth()});
                        animatorArr[1] = ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{(float) ArticleViewer.this.containerView.getMeasuredWidth()});
                        animatorSet.playTogether(animatorArr);
                    }
                    animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) ArticleViewer.this.containerView.getMeasuredWidth())) * distToMove), 50));
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (!backAnimation) {
                                ArticleViewer.this.saveCurrentPagePosition();
                                ArticleViewer.this.onClosed();
                            }
                            WindowView.this.startedTracking = false;
                            WindowView.this.closeAnimationInProgress = false;
                        }
                    });
                    animatorSet.start();
                    this.closeAnimationInProgress = true;
                } else {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                }
                if (this.tracker != null) {
                    this.tracker.recycle();
                    this.tracker = null;
                }
            } else if (event == null) {
                this.maybeStartTracking = false;
                this.startedTracking = false;
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
                if (this.var_bX == 0 && this.var_bY == 0) {
                    canvas.drawRect((float) this.var_bX, (float) this.var_bY, (float) (this.var_bX + this.bWidth), (float) (this.var_bY + this.bHeight), ArticleViewer.this.blackPaint);
                    return;
                }
                canvas.drawRect(((float) this.var_bX) - getTranslationX(), (float) this.var_bY, ((float) (this.var_bX + this.bWidth)) - getTranslationX(), (float) (this.var_bY + this.bHeight), ArticleViewer.this.blackPaint);
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

    /* renamed from: org.telegram.ui.ArticleViewer$16 */
    class CLASSNAME implements VideoPlayerDelegate {
        CLASSNAME() {
        }

        public void onStateChanged(boolean playWhenReady, int playbackState) {
            if (ArticleViewer.this.videoPlayer != null) {
                if (playbackState == 4 || playbackState == 1) {
                    try {
                        ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                    } catch (Throwable e) {
                        FileLog.m13e(e);
                    }
                } else {
                    try {
                        ArticleViewer.this.parentActivity.getWindow().addFlags(128);
                    } catch (Throwable e2) {
                        FileLog.m13e(e2);
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
            FileLog.m13e((Throwable) e);
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
    }

    /* renamed from: org.telegram.ui.ArticleViewer$4 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (ArticleViewer.this.listView.getChildCount() != 0) {
                ArticleViewer.this.headerView.invalidate();
                ArticleViewer.this.checkScroll(dy);
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$6 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            int i = 1;
            if (id == -1) {
                ArticleViewer.this.closePhoto(true);
            } else if (id == 1) {
                if (VERSION.SDK_INT < 23 || ArticleViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                    File f = ArticleViewer.this.getMediaFile(ArticleViewer.this.currentIndex);
                    if (f == null || !f.exists()) {
                        Builder builder = new Builder(ArticleViewer.this.parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        builder.setMessage(LocaleController.getString("PleaseDownload", R.string.PleaseDownload));
                        ArticleViewer.this.showDialog(builder.create());
                        return;
                    }
                    String file = f.toString();
                    Context access$2300 = ArticleViewer.this.parentActivity;
                    if (!ArticleViewer.this.isMediaVideo(ArticleViewer.this.currentIndex)) {
                        i = 0;
                    }
                    MediaController.saveFile(file, access$2300, i, null, null);
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
                    FileLog.m13e(e);
                }
            }
        }

        public boolean canOpenMenu() {
            File f = ArticleViewer.this.getMediaFile(ArticleViewer.this.currentIndex);
            return f != null && f.exists();
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$7 */
    class CLASSNAME implements GroupedPhotosListViewDelegate {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockAudioCell */
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
        private RadialProgress radialProgress = new RadialProgress(this);
        private SeekBar seekBar;
        private int seekBarX;
        private int seekBarY;
        private int textX;
        private int textY = AndroidUtilities.m9dp(54.0f);
        private StaticLayout titleLayout;

        public BlockAudioCell(Context context) {
            super(context);
            this.radialProgress.setAlphaForPrevious(true);
            this.radialProgress.setDiff(AndroidUtilities.m9dp(0.0f));
            this.radialProgress.setStrokeWidth(AndroidUtilities.m9dp(2.0f));
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
            this.currentMessageObject = (MessageObject) ArticleViewer.this.audioBlocks.get(this.currentBlock);
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
                if ((this.buttonState != -1 && x >= ((float) this.buttonX) && x <= ((float) (this.buttonX + AndroidUtilities.m9dp(48.0f))) && y >= ((float) this.buttonY) && y <= ((float) (this.buttonY + AndroidUtilities.m9dp(48.0f)))) || this.buttonState == 0) {
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
            int height = AndroidUtilities.m9dp(54.0f);
            if (this.currentBlock != null) {
                if (this.currentBlock.level > 0) {
                    this.textX = AndroidUtilities.m9dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.m9dp(18.0f);
                } else {
                    this.textX = AndroidUtilities.m9dp(18.0f);
                }
                int textWidth = (width - this.textX) - AndroidUtilities.m9dp(18.0f);
                int size = AndroidUtilities.m9dp(40.0f);
                this.buttonX = AndroidUtilities.m9dp(16.0f);
                this.buttonY = AndroidUtilities.m9dp(7.0f);
                this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + size, this.buttonY + size);
                this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock);
                if (this.captionLayout != null) {
                    this.creditOffset = AndroidUtilities.m9dp(4.0f) + this.captionLayout.getHeight();
                    height += this.creditOffset + AndroidUtilities.m9dp(4.0f);
                }
                this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL);
                if (this.creditLayout != null) {
                    height += AndroidUtilities.m9dp(4.0f) + this.creditLayout.getHeight();
                }
                if (!this.isFirst && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.m9dp(8.0f);
                }
                String author = this.currentMessageObject.getMusicAuthor(false);
                String title = this.currentMessageObject.getMusicTitle(false);
                this.seekBarX = (this.buttonX + AndroidUtilities.m9dp(50.0f)) + size;
                int w = (width - this.seekBarX) - AndroidUtilities.m9dp(18.0f);
                if (TextUtils.isEmpty(title) && TextUtils.isEmpty(author)) {
                    this.titleLayout = null;
                    this.seekBarY = this.buttonY + ((size - AndroidUtilities.m9dp(30.0f)) / 2);
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
                    this.seekBarY = (this.buttonY + ((size - AndroidUtilities.m9dp(30.0f)) / 2)) + AndroidUtilities.m9dp(11.0f);
                }
                this.seekBar.setSize(w, AndroidUtilities.m9dp(30.0f));
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
                    canvas.translate((float) (this.buttonX + AndroidUtilities.m9dp(54.0f)), (float) (this.seekBarY + AndroidUtilities.m9dp(6.0f)));
                    this.durationLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.titleLayout != null) {
                    canvas.save();
                    canvas.translate((float) (this.buttonX + AndroidUtilities.m9dp(54.0f)), (float) (this.seekBarY - AndroidUtilities.m9dp(16.0f)));
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
                    canvas.drawRect((float) AndroidUtilities.m9dp(18.0f), 0.0f, (float) AndroidUtilities.m9dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.m9dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
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
                    ArticleViewer.audioTimePaint.setTextSize((float) AndroidUtilities.m9dp(16.0f));
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
                if (MediaController.getInstance().setPlaylist(ArticleViewer.this.audioMessages, this.currentMessageObject, false)) {
                    this.buttonState = 1;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                    invalidate();
                }
            } else if (this.buttonState == 1) {
                if (MediaController.getInstance().lambda$startAudioAgain$6$MediaController(this.currentMessageObject)) {
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockPhotoCell */
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
        private PageBlock parentBlock;
        private boolean photoPressed;
        private RadialProgress radialProgress;
        private int textX;
        private int textY;

        public BlockPhotoCell(Context context, int type) {
            super(context);
            setWillNotDraw(false);
            this.channelCell = new BlockChannelCell(context, 1);
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
            if (this.channelCell.getVisibility() != 0 || y <= this.channelCell.getTranslationY() || y >= this.channelCell.getTranslationY() + ((float) AndroidUtilities.m9dp(39.0f))) {
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
                height = (int) Math.ceil((double) ((this.groupPosition.var_ph * ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y))) * 0.5f));
            }
            if (this.currentBlock != null) {
                int photoX;
                int textWidth;
                Photo photo = ArticleViewer.this.getPhotoWithId(this.currentBlock.photo_id);
                int size = AndroidUtilities.m9dp(48.0f);
                int photoWidth = width;
                int photoHeight = height;
                if (this.currentType != 0 || this.currentBlock.level <= 0) {
                    photoX = 0;
                    this.textX = AndroidUtilities.m9dp(18.0f);
                    textWidth = width - AndroidUtilities.m9dp(36.0f);
                } else {
                    photoX = AndroidUtilities.m9dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.m9dp(18.0f);
                    this.textX = photoX;
                    photoWidth -= AndroidUtilities.m9dp(18.0f) + photoX;
                    textWidth = photoWidth;
                }
                if (!(photo == null || this.currentPhotoObject == null)) {
                    String filter;
                    String str;
                    PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80, true);
                    if (this.currentPhotoObject == thumb) {
                        thumb = null;
                    }
                    if (this.currentType == 0) {
                        height = (int) (((float) this.currentPhotoObject.var_h) * (((float) photoWidth) / ((float) this.currentPhotoObject.var_w)));
                        if (this.parentBlock instanceof TL_pageBlockCover) {
                            height = Math.min(height, photoWidth);
                        } else {
                            int maxHeight = (int) (((float) (Math.max(ArticleViewer.this.listView.getMeasuredWidth(), ArticleViewer.this.listView.getMeasuredHeight()) - AndroidUtilities.m9dp(56.0f))) * 0.9f);
                            if (height > maxHeight) {
                                height = maxHeight;
                                photoWidth = (int) (((float) this.currentPhotoObject.var_w) * (((float) height) / ((float) this.currentPhotoObject.var_h)));
                                photoX += ((width - photoX) - photoWidth) / 2;
                            }
                        }
                        photoHeight = height;
                    } else if (this.currentType == 2) {
                        if ((this.groupPosition.flags & 2) == 0) {
                            photoWidth -= AndroidUtilities.m9dp(2.0f);
                        }
                        if ((this.groupPosition.flags & 8) == 0) {
                            photoHeight -= AndroidUtilities.m9dp(2.0f);
                        }
                        if (this.groupPosition.leftSpanOffset != 0) {
                            int offset = (int) Math.ceil((double) (((float) (this.groupPosition.leftSpanOffset * width)) / 1000.0f));
                            photoWidth -= offset;
                            photoX += offset;
                        }
                    }
                    ImageReceiver imageReceiver = this.imageView;
                    int dp = (this.isFirst || this.currentType == 1 || this.currentType == 2 || this.currentBlock.level > 0) ? 0 : AndroidUtilities.m9dp(8.0f);
                    imageReceiver.setImageCoords(photoX, dp, photoWidth, photoHeight);
                    if (this.currentType == 0) {
                        filter = null;
                    } else {
                        filter = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoWidth), Integer.valueOf(photoHeight)});
                    }
                    ImageReceiver imageReceiver2 = this.imageView;
                    TLObject tLObject = this.currentPhotoObject.location;
                    FileLocation fileLocation = thumb != null ? thumb.location : null;
                    if (thumb != null) {
                        str = "80_80_b";
                    } else {
                        str = null;
                    }
                    imageReceiver2.setImage(tLObject, filter, fileLocation, str, this.currentPhotoObject.size, null, ArticleViewer.this.currentPage, 1);
                    this.buttonX = (int) (((float) this.imageView.getImageX()) + (((float) (this.imageView.getImageWidth() - size)) / 2.0f));
                    this.buttonY = (int) (((float) this.imageView.getImageY()) + (((float) (this.imageView.getImageHeight() - size)) / 2.0f));
                    this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + size, this.buttonY + size);
                }
                if (this.currentType == 0) {
                    this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock);
                    if (this.captionLayout != null) {
                        this.creditOffset = AndroidUtilities.m9dp(4.0f) + this.captionLayout.getHeight();
                        height += this.creditOffset + AndroidUtilities.m9dp(4.0f);
                    }
                    this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL);
                    if (this.creditLayout != null) {
                        height += AndroidUtilities.m9dp(4.0f) + this.creditLayout.getHeight();
                    }
                }
                if (!this.isFirst && this.currentType == 0 && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.m9dp(8.0f);
                }
                boolean nextIsChannel = (this.parentBlock instanceof TL_pageBlockCover) && ArticleViewer.this.blocks != null && ArticleViewer.this.blocks.size() > 1 && (ArticleViewer.this.blocks.get(1) instanceof TL_pageBlockChannel);
                if (!(this.currentType == 2 || nextIsChannel)) {
                    height += AndroidUtilities.m9dp(8.0f);
                }
            } else {
                height = 1;
            }
            this.channelCell.measure(widthMeasureSpec, heightMeasureSpec);
            this.channelCell.setTranslationY((float) (this.imageView.getImageHeight() - AndroidUtilities.m9dp(39.0f)));
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (!(this.imageView.hasBitmapImage() && ((float) this.imageView.getcurrentAccount()) == 1.0f)) {
                    canvas.drawRect((float) this.imageView.getImageX(), (float) this.imageView.getImageY(), (float) this.imageView.getImageX2(), (float) this.imageView.getImageY2(), ArticleViewer.photoBackgroundPaint);
                }
                this.imageView.draw(canvas);
                if (this.imageView.getVisible()) {
                    this.radialProgress.draw(canvas);
                }
                if (!TextUtils.isEmpty(this.currentBlock.url)) {
                    int x = getMeasuredWidth() - AndroidUtilities.m9dp(35.0f);
                    int y = this.imageView.getImageY() + AndroidUtilities.m9dp(11.0f);
                    this.linkDrawable.setBounds(x, y, AndroidUtilities.m9dp(24.0f) + x, AndroidUtilities.m9dp(24.0f) + y);
                    this.linkDrawable.draw(canvas);
                }
                this.textY = (this.imageView.getImageY() + this.imageView.getImageHeight()) + AndroidUtilities.m9dp(8.0f);
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
                    canvas.drawRect((float) AndroidUtilities.m9dp(18.0f), 0.0f, (float) AndroidUtilities.m9dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.m9dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
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

    /* renamed from: org.telegram.ui.ArticleViewer$BlockTableCell */
    private class BlockTableCell extends FrameLayout implements TableLayoutDelegate {
        private TL_pageBlockTable currentBlock;
        private boolean firstLayout;
        private boolean inLayout;
        private int listX;
        private int listY;
        private HorizontalScrollView scrollView;
        private TableLayout tableLayout;
        private int textX;
        private int textY;
        private DrawingText titleLayout;

        public BlockTableCell(Context context) {
            super(context);
            this.scrollView = new HorizontalScrollView(context, ArticleViewer.this) {
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (BlockTableCell.this.tableLayout.getMeasuredWidth() > getMeasuredWidth() - AndroidUtilities.m9dp(36.0f)) {
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
            this.scrollView.setPadding(AndroidUtilities.m9dp(18.0f), 0, AndroidUtilities.m9dp(18.0f), 0);
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
            return ArticleViewer.this.createLayoutForText(this, null, cell.text, maxWidth, 0, this.currentBlock, alignment, 0);
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
                    this.listX = AndroidUtilities.m9dp((float) (this.currentBlock.level * 14));
                    this.textX = this.listX + AndroidUtilities.m9dp(18.0f);
                    textWidth = width - this.textX;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.m9dp(18.0f);
                    textWidth = width - AndroidUtilities.m9dp(36.0f);
                }
                this.titleLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.title, textWidth, 0, this.currentBlock, Alignment.ALIGN_CENTER, 0);
                if (this.titleLayout != null) {
                    this.textY = 0;
                    height = 0 + (this.titleLayout.getHeight() + AndroidUtilities.m9dp(8.0f));
                    this.listY = height;
                } else {
                    this.listY = AndroidUtilities.m9dp(8.0f);
                }
                this.scrollView.measure(MeasureSpec.makeMeasureSpec(width - this.listX, NUM), MeasureSpec.makeMeasureSpec(0, 0));
                height += this.scrollView.getMeasuredHeight() + AndroidUtilities.m9dp(8.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.m9dp(8.0f);
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
                    this.scrollView.setScrollX((this.tableLayout.getMeasuredWidth() - this.scrollView.getMeasuredWidth()) + AndroidUtilities.m9dp(36.0f));
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
                    canvas.drawRect((float) AndroidUtilities.m9dp(18.0f), 0.0f, (float) AndroidUtilities.m9dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.m9dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$BlockVideoCell */
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
        private PageBlock parentBlock;
        private boolean photoPressed;
        private RadialProgress radialProgress;
        private int textX;
        private int textY;

        public BlockVideoCell(Context context, int type) {
            super(context);
            setWillNotDraw(false);
            this.currentType = type;
            this.radialProgress = new RadialProgress(this);
            this.radialProgress.setAlphaForPrevious(true);
            this.radialProgress.setProgressColor(-1);
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            this.channelCell = new BlockChannelCell(context, 1);
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
            if (this.channelCell.getVisibility() != 0 || y <= this.channelCell.getTranslationY() || y >= this.channelCell.getTranslationY() + ((float) AndroidUtilities.m9dp(39.0f))) {
                boolean z;
                if (event.getAction() == 0 && this.imageView.isInsideImage(x, y)) {
                    if ((this.buttonState == -1 || x < ((float) this.buttonX) || x > ((float) (this.buttonX + AndroidUtilities.m9dp(48.0f))) || y < ((float) this.buttonY) || y > ((float) (this.buttonY + AndroidUtilities.m9dp(48.0f)))) && this.buttonState != 0) {
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
                height = (int) Math.ceil((double) ((this.groupPosition.var_ph * ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y))) * 0.5f));
            }
            if (this.currentBlock != null) {
                int photoX;
                int textWidth;
                int photoWidth = width;
                int photoHeight = height;
                if (this.currentType != 0 || this.currentBlock.level <= 0) {
                    photoX = 0;
                    this.textX = AndroidUtilities.m9dp(18.0f);
                    textWidth = width - AndroidUtilities.m9dp(36.0f);
                } else {
                    photoX = AndroidUtilities.m9dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.m9dp(18.0f);
                    this.textX = photoX;
                    photoWidth -= AndroidUtilities.m9dp(18.0f) + photoX;
                    textWidth = photoWidth;
                }
                if (this.currentDocument != null) {
                    int size = AndroidUtilities.m9dp(48.0f);
                    PhotoSize thumb = this.currentDocument.thumb;
                    if (this.currentType == 0) {
                        boolean found = false;
                        int count = this.currentDocument.attributes.size();
                        for (int a = 0; a < count; a++) {
                            DocumentAttribute attribute = (DocumentAttribute) this.currentDocument.attributes.get(a);
                            if (attribute instanceof TL_documentAttributeVideo) {
                                height = (int) (((float) attribute.var_h) * (((float) photoWidth) / ((float) attribute.var_w)));
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            height = (int) (((float) thumb.var_h) * (((float) photoWidth) / ((float) thumb.var_w)));
                        }
                        if (this.parentBlock instanceof TL_pageBlockCover) {
                            height = Math.min(height, photoWidth);
                        } else {
                            int maxHeight = (int) (((float) (Math.max(ArticleViewer.this.listView.getMeasuredWidth(), ArticleViewer.this.listView.getMeasuredHeight()) - AndroidUtilities.m9dp(56.0f))) * 0.9f);
                            if (height > maxHeight) {
                                height = maxHeight;
                                photoWidth = (int) (((float) thumb.var_w) * (((float) height) / ((float) thumb.var_h)));
                                photoX += ((width - photoX) - photoWidth) / 2;
                            }
                        }
                        if (height == 0) {
                            height = AndroidUtilities.m9dp(100.0f);
                        } else if (height < size) {
                            height = size;
                        }
                        photoHeight = height;
                    } else if (this.currentType == 2) {
                        if ((this.groupPosition.flags & 2) == 0) {
                            photoWidth -= AndroidUtilities.m9dp(2.0f);
                        }
                        if ((this.groupPosition.flags & 8) == 0) {
                            photoHeight -= AndroidUtilities.m9dp(2.0f);
                        }
                    }
                    ImageReceiver imageReceiver = this.imageView;
                    int dp = (this.isFirst || this.currentType == 1 || this.currentType == 2 || this.currentBlock.level > 0) ? 0 : AndroidUtilities.m9dp(8.0f);
                    imageReceiver.setImageCoords(photoX, dp, photoWidth, photoHeight);
                    if (this.isGif) {
                        this.imageView.setImage(this.currentDocument, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(photoWidth), Integer.valueOf(photoHeight)}), thumb != null ? thumb.location : null, thumb != null ? "80_80_b" : null, this.currentDocument.size, null, ArticleViewer.this.currentPage, 1);
                    } else {
                        this.imageView.setImage(null, null, thumb != null ? thumb.location : null, thumb != null ? "80_80_b" : null, 0, null, ArticleViewer.this.currentPage, 1);
                    }
                    this.buttonX = (int) (((float) this.imageView.getImageX()) + (((float) (this.imageView.getImageWidth() - size)) / 2.0f));
                    this.buttonY = (int) (((float) this.imageView.getImageY()) + (((float) (this.imageView.getImageHeight() - size)) / 2.0f));
                    this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + size, this.buttonY + size);
                }
                if (this.currentType == 0) {
                    this.captionLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption.text, textWidth, this.currentBlock);
                    if (this.captionLayout != null) {
                        this.creditOffset = AndroidUtilities.m9dp(4.0f) + this.captionLayout.getHeight();
                        height += this.creditOffset + AndroidUtilities.m9dp(4.0f);
                    }
                    this.creditLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.caption.credit, textWidth, this.currentBlock, ArticleViewer.this.isRtl ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_NORMAL);
                    if (this.creditLayout != null) {
                        height += AndroidUtilities.m9dp(4.0f) + this.creditLayout.getHeight();
                    }
                }
                if (!this.isFirst && this.currentType == 0 && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.m9dp(8.0f);
                }
                boolean nextIsChannel = (this.parentBlock instanceof TL_pageBlockCover) && ArticleViewer.this.blocks != null && ArticleViewer.this.blocks.size() > 1 && (ArticleViewer.this.blocks.get(1) instanceof TL_pageBlockChannel);
                if (!(this.currentType == 2 || nextIsChannel)) {
                    height += AndroidUtilities.m9dp(8.0f);
                }
            } else {
                height = 1;
            }
            this.channelCell.measure(widthMeasureSpec, heightMeasureSpec);
            this.channelCell.setTranslationY((float) (this.imageView.getImageHeight() - AndroidUtilities.m9dp(39.0f)));
            setMeasuredDimension(width, height);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (!(this.imageView.hasBitmapImage() && ((float) this.imageView.getcurrentAccount()) == 1.0f)) {
                    canvas.drawRect((float) this.imageView.getImageX(), (float) this.imageView.getImageY(), (float) this.imageView.getImageX2(), (float) this.imageView.getImageY2(), ArticleViewer.photoBackgroundPaint);
                }
                this.imageView.draw(canvas);
                if (this.imageView.getVisible()) {
                    this.radialProgress.draw(canvas);
                }
                this.textY = (this.imageView.getImageY() + this.imageView.getImageHeight()) + AndroidUtilities.m9dp(8.0f);
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
                    canvas.drawRect((float) AndroidUtilities.m9dp(18.0f), 0.0f, (float) AndroidUtilities.m9dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.m9dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
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
                    this.imageView.setImage(this.currentDocument, null, this.currentDocument.thumb != null ? this.currentDocument.thumb.location : null, "80_80_b", this.currentDocument.size, null, ArticleViewer.this.currentPage, 1);
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

    /* renamed from: org.telegram.ui.ArticleViewer$TL_pageBlockDetailsBottom */
    private class TL_pageBlockDetailsBottom extends PageBlock {
        private TL_pageBlockDetails parent;

        private TL_pageBlockDetailsBottom() {
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild */
    private class TL_pageBlockDetailsChild extends PageBlock {
        private PageBlock block;
        private PageBlock parent;

        private TL_pageBlockDetailsChild() {
        }

        /* synthetic */ TL_pageBlockDetailsChild(ArticleViewer x0, CLASSNAME x1) {
            this();
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$TL_pageBlockListItem */
    private class TL_pageBlockListItem extends PageBlock {
        private PageBlock blockItem;
        private int index;
        private String num;
        private DrawingText numLayout;
        private TL_pageBlockListParent parent;
        private RichText textItem;

        private TL_pageBlockListItem() {
            this.index = ConnectionsManager.DEFAULT_DATACENTER_ID;
        }

        /* synthetic */ TL_pageBlockListItem(ArticleViewer x0, CLASSNAME x1) {
            this();
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$TL_pageBlockListParent */
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

        /* synthetic */ TL_pageBlockListParent(ArticleViewer x0, CLASSNAME x1) {
            this();
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem */
    private class TL_pageBlockOrderedListItem extends PageBlock {
        private PageBlock blockItem;
        private int index;
        private String num;
        private DrawingText numLayout;
        private TL_pageBlockOrderedListParent parent;
        private RichText textItem;

        private TL_pageBlockOrderedListItem() {
            this.index = ConnectionsManager.DEFAULT_DATACENTER_ID;
        }

        /* synthetic */ TL_pageBlockOrderedListItem(ArticleViewer x0, CLASSNAME x1) {
            this();
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent */
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

        /* synthetic */ TL_pageBlockOrderedListParent(ArticleViewer x0, CLASSNAME x1) {
            this();
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesChild */
    private class TL_pageBlockRelatedArticlesChild extends PageBlock {
        private int num;
        private TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesChild() {
        }

        /* synthetic */ TL_pageBlockRelatedArticlesChild(ArticleViewer x0, CLASSNAME x1) {
            this();
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesShadow */
    private class TL_pageBlockRelatedArticlesShadow extends PageBlock {
        private TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesShadow() {
        }

        /* synthetic */ TL_pageBlockRelatedArticlesShadow(ArticleViewer x0, CLASSNAME x1) {
            this();
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$WebpageAdapter */
    private class WebpageAdapter extends SelectionAdapter {
        private Context context;
        private ArrayList<PageBlock> localBlocks = new ArrayList();

        public WebpageAdapter(Context ctx) {
            this.context = ctx;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new BlockParagraphCell(this.context);
                    break;
                case 1:
                    view = new BlockHeaderCell(this.context);
                    break;
                case 2:
                    view = new BlockDividerCell(this.context);
                    break;
                case 3:
                    view = new BlockEmbedCell(this.context);
                    break;
                case 4:
                    view = new BlockSubtitleCell(this.context);
                    break;
                case 5:
                    view = new BlockVideoCell(this.context, 0);
                    break;
                case 6:
                    view = new BlockPullquoteCell(this.context);
                    break;
                case 7:
                    view = new BlockBlockquoteCell(this.context);
                    break;
                case 8:
                    view = new BlockSlideshowCell(this.context);
                    break;
                case 9:
                    view = new BlockPhotoCell(this.context, 0);
                    break;
                case 10:
                    view = new BlockAuthorDateCell(this.context);
                    break;
                case 11:
                    view = new BlockTitleCell(this.context);
                    break;
                case 12:
                    view = new BlockListItemCell(this.context);
                    break;
                case 13:
                    view = new BlockFooterCell(this.context);
                    break;
                case 14:
                    view = new BlockPreformattedCell(this.context);
                    break;
                case 15:
                    view = new BlockSubheaderCell(this.context);
                    break;
                case 16:
                    view = new BlockEmbedPostCell(this.context);
                    break;
                case 17:
                    view = new BlockCollageCell(this.context);
                    break;
                case 18:
                    view = new BlockChannelCell(this.context, 0);
                    break;
                case 19:
                    view = new BlockAudioCell(this.context);
                    break;
                case 20:
                    view = new BlockKickerCell(this.context);
                    break;
                case 21:
                    view = new BlockOrderedListItemCell(this.context);
                    break;
                case 22:
                    view = new BlockMapCell(this.context, 0);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_IRAP_VCL23 /*23*/:
                    view = new BlockRelatedArticlesCell(this.context);
                    break;
                case 24:
                    view = new BlockDetailsCell(this.context);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL25 /*25*/:
                    view = new BlockTableCell(this.context);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
                    view = new BlockRelatedArticlesHeaderCell(this.context);
                    break;
                case 27:
                    view = new BlockDetailsBottomCell(this.context);
                    break;
                case NalUnitTypes.NAL_TYPE_RSV_VCL28 /*28*/:
                    view = new BlockRelatedArticlesShadowCell(this.context);
                    break;
                case 90:
                    View frameLayout = new FrameLayout(this.context) {
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(44.0f), NUM));
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
                    textView2.setTextColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
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
                case NalUnitTypes.NAL_TYPE_RSV_IRAP_VCL23 /*23*/:
                    holder.itemView.setBlock((TL_pageBlockRelatedArticlesChild) block);
                    return;
                case 24:
                    holder.itemView.setBlock((TL_pageBlockDetails) block);
                    return;
                case NalUnitTypes.NAL_TYPE_RSV_VCL25 /*25*/:
                    holder.itemView.setBlock((TL_pageBlockTable) block);
                    return;
                case NalUnitTypes.NAL_TYPE_RSV_VCL26 /*26*/:
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
            int size = ArticleViewer.this.blocks.size();
            for (int a = 0; a < size; a++) {
                PageBlock originalBlock = (PageBlock) ArticleViewer.this.blocks.get(a);
                PageBlock block = ArticleViewer.this.getLastNonListPageBlock(originalBlock);
                if (!(block instanceof TL_pageBlockDetailsChild) || isBlockOpened((TL_pageBlockDetailsChild) block)) {
                    this.localBlocks.add(originalBlock);
                }
            }
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
            tableLinePaint.setStrokeWidth((float) AndroidUtilities.m9dp(1.0f));
            tableHalfLinePaint = new Paint();
            tableHalfLinePaint.setStyle(Style.STROKE);
            tableHalfLinePaint.setStrokeWidth(((float) AndroidUtilities.m9dp(1.0f)) / 2.0f);
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
                this.linkSheet.lambda$new$4$EmbedBottomSheet();
                this.linkSheet = null;
            }
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
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
                this.popupLayout.setPadding(AndroidUtilities.m9dp(1.0f), AndroidUtilities.m9dp(1.0f), AndroidUtilities.m9dp(1.0f), AndroidUtilities.m9dp(1.0f));
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
                this.deleteView.setPadding(AndroidUtilities.m9dp(20.0f), 0, AndroidUtilities.m9dp(20.0f), 0);
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
            this.popupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(1000.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(1000.0f), Integer.MIN_VALUE));
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

    private void addBlock(PageBlock block, int level, int listLevel, int position) {
        PageBlock originalBlock = block;
        if (block instanceof TL_pageBlockDetailsChild) {
            block = ((TL_pageBlockDetailsChild) block).block;
        }
        if (!((block instanceof TL_pageBlockList) || (block instanceof TL_pageBlockOrderedList))) {
            setRichTextParents(block);
            addAllMediaFromBlock(block);
        }
        block = getLastNonListPageBlock(block);
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
                message.var_id = i;
                message.to_id = new TL_peerUser();
                Peer peer = message.to_id;
                int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                message.from_id = clientUserId;
                peer.user_id = clientUserId;
                message.date = (int) (System.currentTimeMillis() / 1000);
                message.message = TtmlNode.ANONYMOUS_REGION_ID;
                message.media = new TL_messageMediaDocument();
                message.media.webpage = this.currentPage;
                MessageMedia messageMedia = message.media;
                messageMedia.flags |= 3;
                message.media.document = getDocumentWithId(blockAudio.audio_id);
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
                TL_pageBlockRelatedArticlesShadow tL_pageBlockRelatedArticlesShadow = new TL_pageBlockRelatedArticlesShadow(this, null);
                tL_pageBlockRelatedArticlesShadow.parent = pageBlockRelatedArticles;
                this.blocks.add(this.blocks.size() - 1, tL_pageBlockRelatedArticlesShadow);
                size = pageBlockRelatedArticles.articles.size();
                for (b = 0; b < size; b++) {
                    TL_pageBlockRelatedArticlesChild child2 = new TL_pageBlockRelatedArticlesChild(this, null);
                    child2.parent = pageBlockRelatedArticles;
                    child2.num = b;
                    this.blocks.add(child2);
                }
                if (position == 0) {
                    tL_pageBlockRelatedArticlesShadow = new TL_pageBlockRelatedArticlesShadow(this, null);
                    tL_pageBlockRelatedArticlesShadow.parent = pageBlockRelatedArticles;
                    this.blocks.add(tL_pageBlockRelatedArticlesShadow);
                }
            } else if (block instanceof TL_pageBlockDetails) {
                TL_pageBlockDetails pageBlockDetails = (TL_pageBlockDetails) block;
                size = pageBlockDetails.blocks.size();
                for (b = 0; b < size; b++) {
                    child = new TL_pageBlockDetailsChild(this, null);
                    child.parent = originalBlock;
                    child.block = (PageBlock) pageBlockDetails.blocks.get(b);
                    addBlock(wrapInTableBlock(originalBlock, child), level + 1, listLevel, position);
                }
            } else if (block instanceof TL_pageBlockList) {
                TL_pageBlockList pageBlockList = (TL_pageBlockList) block;
                TL_pageBlockListParent tL_pageBlockListParent = new TL_pageBlockListParent(this, null);
                tL_pageBlockListParent.pageBlockList = pageBlockList;
                tL_pageBlockListParent.level = listLevel;
                size = pageBlockList.items.size();
                for (b = 0; b < size; b++) {
                    TL_pageListItemBlocks pageListItemBlocks;
                    PageListItem item = (PageListItem) pageBlockList.items.get(b);
                    tL_pageBlockListItem = new TL_pageBlockListItem(this, null);
                    tL_pageBlockListItem.index = b;
                    tL_pageBlockListItem.parent = tL_pageBlockListParent;
                    if (!pageBlockList.ordered) {
                        tL_pageBlockListItem.num = "\u2022";
                    } else if (this.isRtl) {
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
                        child = new TL_pageBlockDetailsChild(this, null);
                        child.parent = pageBlockDetailsChild.parent;
                        child.block = tL_pageBlockListItem;
                        addBlock(child, level, listLevel + 1, position);
                    } else {
                        if (b == 0) {
                            finalBlock = fixListBlock(originalBlock, tL_pageBlockListItem);
                        } else {
                            finalBlock = tL_pageBlockListItem;
                        }
                        addBlock(finalBlock, level, listLevel + 1, position);
                    }
                    if (item instanceof TL_pageListItemBlocks) {
                        pageListItemBlocks = (TL_pageListItemBlocks) item;
                        size2 = pageListItemBlocks.blocks.size();
                        for (c = 1; c < size2; c++) {
                            tL_pageBlockListItem = new TL_pageBlockListItem(this, null);
                            tL_pageBlockListItem.blockItem = (PageBlock) pageListItemBlocks.blocks.get(c);
                            tL_pageBlockListItem.parent = tL_pageBlockListParent;
                            if (originalBlock instanceof TL_pageBlockDetailsChild) {
                                pageBlockDetailsChild = (TL_pageBlockDetailsChild) originalBlock;
                                child = new TL_pageBlockDetailsChild(this, null);
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
                TL_pageBlockOrderedListParent tL_pageBlockOrderedListParent = new TL_pageBlockOrderedListParent(this, null);
                tL_pageBlockOrderedListParent.pageBlockOrderedList = pageBlockOrderedList;
                tL_pageBlockOrderedListParent.level = listLevel;
                size = pageBlockOrderedList.items.size();
                for (b = 0; b < size; b++) {
                    TL_pageListOrderedItemBlocks pageListOrderedItemBlocks;
                    PageListOrderedItem item2 = (PageListOrderedItem) pageBlockOrderedList.items.get(b);
                    tL_pageBlockListItem = new TL_pageBlockOrderedListItem(this, null);
                    tL_pageBlockListItem.index = b;
                    tL_pageBlockListItem.parent = tL_pageBlockOrderedListParent;
                    tL_pageBlockOrderedListParent.items.add(tL_pageBlockListItem);
                    if (item2 instanceof TL_pageListOrderedItemText) {
                        TL_pageListOrderedItemText pageListOrderedItemText = (TL_pageListOrderedItemText) item2;
                        tL_pageBlockListItem.textItem = pageListOrderedItemText.text;
                        if (TextUtils.isEmpty(pageListOrderedItemText.num)) {
                            tL_pageBlockListItem.num = String.format("%d.", new Object[]{Integer.valueOf(b + 1)});
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
                            tL_pageBlockListItem.num = String.format("%d.", new Object[]{Integer.valueOf(b + 1)});
                        } else {
                            tL_pageBlockListItem.num = tL_pageBlockListItem.num + pageListOrderedItemBlocks.num + ".";
                        }
                    }
                    if (originalBlock instanceof TL_pageBlockDetailsChild) {
                        pageBlockDetailsChild = (TL_pageBlockDetailsChild) originalBlock;
                        child = new TL_pageBlockDetailsChild(this, null);
                        child.parent = pageBlockDetailsChild.parent;
                        child.block = tL_pageBlockListItem;
                        addBlock(child, level, listLevel + 1, position);
                    } else {
                        if (b == 0) {
                            finalBlock = fixListBlock(originalBlock, tL_pageBlockListItem);
                        } else {
                            finalBlock = tL_pageBlockListItem;
                        }
                        addBlock(finalBlock, level, listLevel + 1, position);
                    }
                    if (item2 instanceof TL_pageListOrderedItemBlocks) {
                        pageListOrderedItemBlocks = (TL_pageListOrderedItemBlocks) item2;
                        size2 = pageListOrderedItemBlocks.blocks.size();
                        for (c = 1; c < size2; c++) {
                            tL_pageBlockListItem = new TL_pageBlockOrderedListItem(this, null);
                            tL_pageBlockListItem.blockItem = (PageBlock) pageListOrderedItemBlocks.blocks.get(c);
                            tL_pageBlockListItem.parent = tL_pageBlockOrderedListParent;
                            if (originalBlock instanceof TL_pageBlockDetailsChild) {
                                pageBlockDetailsChild = (TL_pageBlockDetailsChild) originalBlock;
                                child = new TL_pageBlockDetailsChild(this, null);
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

    private void updateInterfaceForCurrentPage(boolean back) {
        if (this.currentPage != null && this.currentPage.cached_page != null) {
            CharSequence charSequence;
            this.isRtl = this.currentPage.cached_page.rtl;
            this.channelBlock = null;
            this.blocks.clear();
            this.photoBlocks.clear();
            this.audioBlocks.clear();
            this.audioMessages.clear();
            this.anchors.clear();
            this.anchorsParent.clear();
            this.anchorsOffset.clear();
            SimpleTextView simpleTextView = this.titleTextView;
            if (this.currentPage.site_name == null) {
                charSequence = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                charSequence = this.currentPage.site_name;
            }
            simpleTextView.setText(charSequence);
            int count = this.currentPage.cached_page.blocks.size();
            for (int a = 0; a < count; a++) {
                int i;
                PageBlock block = (PageBlock) this.currentPage.cached_page.blocks.get(a);
                if (a == 0) {
                    block.first = true;
                    if (block instanceof TL_pageBlockCover) {
                        TL_pageBlockCover pageBlockCover = (TL_pageBlockCover) block;
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
                if (a == count - 1) {
                    i = a;
                } else {
                    i = 0;
                }
                addBlock(block, 0, 0, i);
            }
            this.adapter.notifyDataSetChanged();
            if (this.pagesStack.size() == 1 || back) {
                int offset;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0);
                String key = "article" + this.currentPage.var_id;
                int position = preferences.getInt(key, -1);
                if (preferences.getBoolean(key + "r", true) == (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y)) {
                    offset = preferences.getInt(key + "o", 0) - this.listView.getPaddingTop();
                } else {
                    offset = AndroidUtilities.m9dp(10.0f);
                }
                if (position != -1) {
                    this.layoutManager.scrollToPositionWithOffset(position, offset);
                }
            } else {
                this.layoutManager.scrollToPositionWithOffset(0, 0);
            }
            checkScrollAnimated();
        }
    }

    private boolean addPageToStack(WebPage webPage, String anchor) {
        saveCurrentPagePosition();
        this.currentPage = webPage;
        this.pagesStack.add(webPage);
        updateInterfaceForCurrentPage(false);
        return scrollToAnchor(anchor);
    }

    private boolean scrollToAnchor(String anchor) {
        if (TextUtils.isEmpty(anchor)) {
            return false;
        }
        anchor = anchor.toLowerCase();
        Integer row = (Integer) this.anchors.get(anchor);
        if (row == null) {
            return false;
        }
        TL_textAnchor textAnchor = (TL_textAnchor) this.anchorsParent.get(anchor);
        int type;
        ViewHolder holder;
        if (textAnchor != null) {
            TL_pageBlockParagraph paragraph = new TL_pageBlockParagraph();
            paragraph.text = textAnchor.text;
            type = this.adapter.getTypeForBlock(paragraph);
            holder = this.adapter.onCreateViewHolder(null, type);
            this.adapter.bindBlockToHolder(type, holder, paragraph, 0, 0);
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
            builder.setUseFullscreen(true);
            builder.setApplyTopPadding(false);
            LinearLayout linearLayout = new LinearLayout(this.parentActivity);
            linearLayout.setOrientation(1);
            View CLASSNAME = new TextView(this.parentActivity) {
                protected void onDraw(Canvas canvas) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
                    super.onDraw(canvas);
                }
            };
            CLASSNAME.setTextSize(1, 16.0f);
            CLASSNAME.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            CLASSNAME.setText(LocaleController.getString("InstantViewReference", R.string.InstantViewReference));
            CLASSNAME.setGravity((this.isRtl ? 5 : 3) | 16);
            CLASSNAME.setTextColor(getTextColor());
            CLASSNAME.setPadding(AndroidUtilities.m9dp(18.0f), 0, AndroidUtilities.m9dp(18.0f), 0);
            linearLayout.addView(CLASSNAME, new LinearLayout.LayoutParams(-1, AndroidUtilities.m9dp(48.0f) + 1));
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
        } else if (row.intValue() < 0 || row.intValue() >= this.blocks.size()) {
            return false;
        } else {
            PageBlock originalBlock = (PageBlock) this.blocks.get(row.intValue());
            PageBlock block = getLastNonListPageBlock(originalBlock);
            if (block instanceof TL_pageBlockDetailsChild) {
                if (openAllParentBlocks((TL_pageBlockDetailsChild) block)) {
                    this.adapter.updateRows();
                    this.adapter.notifyDataSetChanged();
                }
            }
            int position = this.adapter.localBlocks.indexOf(originalBlock);
            if (position != -1) {
                row = Integer.valueOf(position);
            }
            Integer offset = (Integer) this.anchorsOffset.get(anchor);
            if (offset == null) {
                offset = Integer.valueOf(0);
            } else if (offset.intValue() == -1) {
                type = this.adapter.getTypeForBlock(originalBlock);
                holder = this.adapter.onCreateViewHolder(null, type);
                this.adapter.bindBlockToHolder(type, holder, originalBlock, 0, 0);
                holder.itemView.measure(MeasureSpec.makeMeasureSpec(this.listView.getMeasuredWidth(), NUM), MeasureSpec.makeMeasureSpec(0, 0));
                offset = (Integer) this.anchorsOffset.get(anchor);
                if (offset.intValue() == -1) {
                    offset = Integer.valueOf(0);
                }
            }
            this.layoutManager.scrollToPositionWithOffset(row.intValue(), (this.currentHeaderHeight - AndroidUtilities.m9dp(56.0f)) - offset.intValue());
            return true;
        }
    }

    private boolean removeLastPageFromStack() {
        if (this.pagesStack.size() < 2) {
            return false;
        }
        this.pagesStack.remove(this.pagesStack.size() - 1);
        this.currentPage = (WebPage) this.pagesStack.get(this.pagesStack.size() - 1);
        updateInterfaceForCurrentPage(true);
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
                return TtmlNode.ANONYMOUS_REGION_ID;
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
                        return TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    spannableStringBuilder = new SpannableStringBuilder("*");
                    int w = AndroidUtilities.m9dp((float) textImage.var_w);
                    int h = AndroidUtilities.m9dp((float) textImage.var_h);
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
        int textSize = AndroidUtilities.m9dp(14.0f);
        int textColor = -65536;
        if (this.selectedFontSize == 0) {
            additionalSize = -AndroidUtilities.m9dp(4.0f);
        } else if (this.selectedFontSize == 1) {
            additionalSize = -AndroidUtilities.m9dp(2.0f);
        } else if (this.selectedFontSize == 3) {
            additionalSize = AndroidUtilities.m9dp(2.0f);
        } else if (this.selectedFontSize == 4) {
            additionalSize = AndroidUtilities.m9dp(4.0f);
        } else {
            additionalSize = 0;
        }
        if (parentBlock instanceof TL_pageBlockPhoto) {
            TL_pageBlockPhoto pageBlockPhoto = (TL_pageBlockPhoto) parentBlock;
            if (pageBlockPhoto.caption.text == richText || pageBlockPhoto.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.m9dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.m9dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockMap) {
            TL_pageBlockMap pageBlockMap = (TL_pageBlockMap) parentBlock;
            if (pageBlockMap.caption.text == richText || pageBlockMap.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.m9dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.m9dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockTitle) {
            currentMap = titleTextPaints;
            textSize = AndroidUtilities.m9dp(24.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockKicker) {
            currentMap = kickerTextPaints;
            textSize = AndroidUtilities.m9dp(14.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockAuthorDate) {
            currentMap = authorTextPaints;
            textSize = AndroidUtilities.m9dp(14.0f);
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockFooter) {
            currentMap = footerTextPaints;
            textSize = AndroidUtilities.m9dp(14.0f);
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockSubtitle) {
            currentMap = subtitleTextPaints;
            textSize = AndroidUtilities.m9dp(21.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockHeader) {
            currentMap = headerTextPaints;
            textSize = AndroidUtilities.m9dp(21.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockSubheader) {
            currentMap = subheaderTextPaints;
            textSize = AndroidUtilities.m9dp(18.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockBlockquote) {
            TL_pageBlockBlockquote pageBlockBlockquote = (TL_pageBlockBlockquote) parentBlock;
            if (pageBlockBlockquote.text == parentRichText) {
                currentMap = quoteTextPaints;
                textSize = AndroidUtilities.m9dp(15.0f);
                textColor = getTextColor();
            } else if (pageBlockBlockquote.caption == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.m9dp(14.0f);
                textColor = getGrayTextColor();
            }
        } else if (parentBlock instanceof TL_pageBlockPullquote) {
            TL_pageBlockPullquote pageBlockBlockquote2 = (TL_pageBlockPullquote) parentBlock;
            if (pageBlockBlockquote2.text == parentRichText) {
                currentMap = quoteTextPaints;
                textSize = AndroidUtilities.m9dp(15.0f);
                textColor = getTextColor();
            } else if (pageBlockBlockquote2.caption == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.m9dp(14.0f);
                textColor = getGrayTextColor();
            }
        } else if (parentBlock instanceof TL_pageBlockPreformatted) {
            currentMap = preformattedTextPaints;
            textSize = AndroidUtilities.m9dp(14.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockParagraph) {
            currentMap = paragraphTextPaints;
            textSize = AndroidUtilities.m9dp(16.0f);
            textColor = getTextColor();
        } else if (isListItemBlock(parentBlock)) {
            currentMap = listTextPaints;
            textSize = AndroidUtilities.m9dp(16.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockEmbed) {
            TL_pageBlockEmbed pageBlockEmbed = (TL_pageBlockEmbed) parentBlock;
            if (pageBlockEmbed.caption.text == richText || pageBlockEmbed.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.m9dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.m9dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockSlideshow) {
            TL_pageBlockSlideshow pageBlockSlideshow = (TL_pageBlockSlideshow) parentBlock;
            if (pageBlockSlideshow.caption.text == richText || pageBlockSlideshow.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.m9dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.m9dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockCollage) {
            TL_pageBlockCollage pageBlockCollage = (TL_pageBlockCollage) parentBlock;
            if (pageBlockCollage.caption.text == richText || pageBlockCollage.caption.text == parentRichText) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.m9dp(14.0f);
            } else {
                currentMap = photoCreditTextPaints;
                textSize = AndroidUtilities.m9dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockEmbedPost) {
            TL_pageBlockEmbedPost pageBlockEmbedPost = (TL_pageBlockEmbedPost) parentBlock;
            if (richText == pageBlockEmbedPost.caption.text) {
                currentMap = photoCaptionTextPaints;
                textSize = AndroidUtilities.m9dp(14.0f);
                textColor = getGrayTextColor();
            } else {
                if (richText == pageBlockEmbedPost.caption.credit) {
                    currentMap = photoCreditTextPaints;
                    textSize = AndroidUtilities.m9dp(12.0f);
                    textColor = getGrayTextColor();
                } else if (richText != null) {
                    currentMap = embedPostTextPaints;
                    textSize = AndroidUtilities.m9dp(14.0f);
                    textColor = getTextColor();
                }
            }
        } else if (parentBlock instanceof TL_pageBlockVideo) {
            if (richText == ((TL_pageBlockVideo) parentBlock).caption.text) {
                currentMap = mediaCaptionTextPaints;
                textSize = AndroidUtilities.m9dp(14.0f);
                textColor = getTextColor();
            } else {
                currentMap = mediaCreditTextPaints;
                textSize = AndroidUtilities.m9dp(12.0f);
                textColor = getTextColor();
            }
        } else if (parentBlock instanceof TL_pageBlockAudio) {
            if (richText == ((TL_pageBlockAudio) parentBlock).caption.text) {
                currentMap = mediaCaptionTextPaints;
                textSize = AndroidUtilities.m9dp(14.0f);
                textColor = getTextColor();
            } else {
                currentMap = mediaCreditTextPaints;
                textSize = AndroidUtilities.m9dp(12.0f);
                textColor = getTextColor();
            }
        } else if (parentBlock instanceof TL_pageBlockRelatedArticles) {
            currentMap = relatedArticleTextPaints;
            textSize = AndroidUtilities.m9dp(15.0f);
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TL_pageBlockDetails) {
            currentMap = detailsTextPaints;
            textSize = AndroidUtilities.m9dp(15.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TL_pageBlockTable) {
            currentMap = tableTextPaints;
            textSize = AndroidUtilities.m9dp(15.0f);
            textColor = getTextColor();
        }
        if (!((flags & 256) == 0 && (flags & 128) == 0)) {
            textSize -= AndroidUtilities.m9dp(4.0f);
        }
        if (currentMap == null) {
            if (errorTextPaint == null) {
                errorTextPaint = new TextPaint(1);
                errorTextPaint.setColor(-65536);
            }
            errorTextPaint.setTextSize((float) AndroidUtilities.m9dp(14.0f));
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
                    paint.setTypeface(Typeface.create(CLASSNAMEC.SERIF_NAME, 3));
                } else if ((flags & 1) != 0) {
                    paint.setTypeface(Typeface.create(CLASSNAMEC.SERIF_NAME, 1));
                } else if ((flags & 2) != 0) {
                    paint.setTypeface(Typeface.create(CLASSNAMEC.SERIF_NAME, 2));
                } else {
                    paint.setTypeface(Typeface.create(CLASSNAMEC.SERIF_NAME, 0));
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
                paint.baselineShift -= AndroidUtilities.m9dp(6.0f);
            } else if ((flags & 128) != 0) {
                paint.baselineShift += AndroidUtilities.m9dp(2.0f);
            }
            paint.setColor(textColor);
            currentMap.put(flags, paint);
        }
        paint.setTextSize((float) (textSize + additionalSize));
        return paint;
    }

    private DrawingText createLayoutForText(View parentView, CharSequence plainText, RichText richText, int width, PageBlock parentBlock) {
        return createLayoutForText(parentView, plainText, richText, width, 0, parentBlock, Alignment.ALIGN_NORMAL, 0);
    }

    private DrawingText createLayoutForText(View parentView, CharSequence plainText, RichText richText, int width, PageBlock parentBlock, Alignment align) {
        return createLayoutForText(parentView, plainText, richText, width, 0, parentBlock, align, 0);
    }

    private DrawingText createLayoutForText(View parentView, CharSequence plainText, RichText richText, int width, int textY, PageBlock parentBlock) {
        return createLayoutForText(parentView, plainText, richText, width, textY, parentBlock, Alignment.ALIGN_NORMAL, 0);
    }

    private DrawingText createLayoutForText(View parentView, CharSequence plainText, RichText richText, int width, int textY, PageBlock parentBlock, Alignment align, int maxLines) {
        if (plainText == null && (richText == null || (richText instanceof TL_textEmpty))) {
            return null;
        }
        CharSequence text;
        if (width < 0) {
            width = AndroidUtilities.m9dp(10.0f);
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
            additionalSize = -AndroidUtilities.m9dp(4.0f);
        } else if (this.selectedFontSize == 1) {
            additionalSize = -AndroidUtilities.m9dp(2.0f);
        } else if (this.selectedFontSize == 3) {
            additionalSize = AndroidUtilities.m9dp(2.0f);
        } else if (this.selectedFontSize == 4) {
            additionalSize = AndroidUtilities.m9dp(4.0f);
        } else {
            additionalSize = 0;
        }
        if ((parentBlock instanceof TL_pageBlockEmbedPost) && richText == null) {
            if (((TL_pageBlockEmbedPost) parentBlock).author == plainText) {
                if (embedPostAuthorPaint == null) {
                    embedPostAuthorPaint = new TextPaint(1);
                    embedPostAuthorPaint.setColor(getTextColor());
                }
                embedPostAuthorPaint.setTextSize((float) (AndroidUtilities.m9dp(15.0f) + additionalSize));
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
                embedPostDatePaint.setTextSize((float) (AndroidUtilities.m9dp(14.0f) + additionalSize));
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
            channelNamePaint.setTextSize((float) AndroidUtilities.m9dp(15.0f));
            paint = channelNamePaint;
        } else if (parentBlock instanceof TL_pageBlockRelatedArticlesChild) {
            TL_pageBlockRelatedArticlesChild pageBlockRelatedArticlesChild = (TL_pageBlockRelatedArticlesChild) parentBlock;
            if (plainText == ((TL_pageRelatedArticle) pageBlockRelatedArticlesChild.parent.articles.get(pageBlockRelatedArticlesChild.num)).title) {
                if (relatedArticleHeaderPaint == null) {
                    relatedArticleHeaderPaint = new TextPaint(1);
                    relatedArticleHeaderPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                }
                relatedArticleHeaderPaint.setColor(getTextColor());
                relatedArticleHeaderPaint.setTextSize((float) (AndroidUtilities.m9dp(15.0f) + additionalSize));
                paint = relatedArticleHeaderPaint;
            } else {
                if (relatedArticleTextPaint == null) {
                    relatedArticleTextPaint = new TextPaint(1);
                }
                relatedArticleTextPaint.setColor(getGrayTextColor());
                relatedArticleTextPaint.setTextSize((float) (AndroidUtilities.m9dp(14.0f) + additionalSize));
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
            listTextPointerPaint.setTextSize((float) (AndroidUtilities.m9dp(19.0f) + additionalSize));
            listTextNumPaint.setTextSize((float) (AndroidUtilities.m9dp(16.0f) + additionalSize));
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
                result = new StaticLayout(text, paint, width, align, 1.0f, (float) AndroidUtilities.m9dp(4.0f), false);
            }
        } else if (parentBlock instanceof TL_pageBlockPullquote) {
            result = StaticLayoutEx.createStaticLayout(text, paint, width, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false, TruncateAt.END, width, maxLines);
        } else {
            result = StaticLayoutEx.createStaticLayout(text, paint, width, align, 1.0f, (float) AndroidUtilities.m9dp(4.0f), false, TruncateAt.END, width, maxLines);
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
            Spanned spanned = (Spanned) finalText;
            try {
                AnchorSpan[] innerSpans = (AnchorSpan[]) spanned.getSpans(0, spanned.length(), AnchorSpan.class);
                int linesCount = result.getLineCount();
                if (innerSpans != null && innerSpans.length > 0) {
                    for (a = 0; a < innerSpans.length; a++) {
                        if (linesCount <= 1) {
                            this.anchorsOffset.put(innerSpans[a].getName(), Integer.valueOf(textY));
                        } else {
                            this.anchorsOffset.put(innerSpans[a].getName(), Integer.valueOf(result.getLineTop(result.getLineForOffset(spanned.getSpanStart(innerSpans[a]))) + textY));
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
                                null.setBaselineShift(AndroidUtilities.m9dp(shift > 0 ? 5.0f : -2.0f) + shift);
                            }
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
                                linkPath.setBaselineShift(AndroidUtilities.m9dp(shift > 0 ? 5.0f : -2.0f) + shift);
                            }
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
                canvas2.drawRect(((float) (-AndroidUtilities.m9dp(2.0f))) + x, 0.0f, ((float) AndroidUtilities.m9dp(2.0f)) + (x + width), (float) layout.getHeight(), urlPaint);
            }
        }
    }

    private boolean checkLayoutForLinks(MotionEvent event, View parentView, DrawingText drawingText, int layoutX, int layoutY) {
        if (parentView == null || drawingText == null) {
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
                                        dp = AndroidUtilities.m9dp(shift > 0 ? 5.0f : -2.0f) + shift;
                                    } else {
                                        dp = 0;
                                    }
                                    linkPath.setBaselineShift(dp);
                                    layout.getSelectionPath(pressedStart, pressedEnd, this.urlPath);
                                    parentView.invalidate();
                                } catch (Throwable e) {
                                    FileLog.m13e(e);
                                }
                            }
                        }
                    } catch (Throwable e2) {
                        FileLog.m13e(e2);
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
                        this.linkSheet.lambda$new$4$EmbedBottomSheet();
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
                            anchor = URLDecoder.decode(url.substring(index + 1), CLASSNAMEC.UTF8_NAME);
                        } catch (Exception e3) {
                            anchor = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        if (url.toLowerCase().contains(webPageUrl)) {
                            if (TextUtils.isEmpty(anchor)) {
                                this.layoutManager.scrollToPositionWithOffset(0, 0);
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
                addPageToStack((TL_webPage) response, anchor);
            } else {
                Browser.openUrl(this.parentActivity, req.url);
            }
        }
    }

    private Photo getPhotoWithId(long id) {
        if (this.currentPage == null || this.currentPage.cached_page == null) {
            return null;
        }
        if (this.currentPage.photo != null && this.currentPage.photo.var_id == id) {
            return this.currentPage.photo;
        }
        for (int a = 0; a < this.currentPage.cached_page.photos.size(); a++) {
            Photo photo = (Photo) this.currentPage.cached_page.photos.get(a);
            if (photo.var_id == id) {
                return photo;
            }
        }
        return null;
    }

    private Document getDocumentWithId(long id) {
        if (this.currentPage == null || this.currentPage.cached_page == null) {
            return null;
        }
        if (this.currentPage.document != null && this.currentPage.document.var_id == id) {
            return this.currentPage.document;
        }
        for (int a = 0; a < this.currentPage.cached_page.documents.size(); a++) {
            Document document = (Document) this.currentPage.cached_page.documents.get(a);
            if (document.var_id == id) {
                return document;
            }
        }
        return null;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        String location;
        int a;
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
                this.adapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.messagePlayingDidStart) {
            MessageObject messageObject = args[0];
            if (this.listView != null) {
                count = this.listView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.listView.getChildAt(a);
                    if (view instanceof BlockAudioCell) {
                        ((BlockAudioCell) view).updateButtonState(false);
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
            if (this.listView != null) {
                count = this.listView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.listView.getChildAt(a);
                    if (view instanceof BlockAudioCell) {
                        cell = (BlockAudioCell) view;
                        if (cell.getMessageObject() != null) {
                            cell.updateButtonState(false);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer mid = args[0];
            if (this.listView != null) {
                count = this.listView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.listView.getChildAt(a);
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
                                return;
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    private void updatePaintSize() {
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_size", this.selectedFontSize).commit();
        this.adapter.notifyDataSetChanged();
    }

    private void updatePaintFonts() {
        int a;
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_type", this.selectedFont).commit();
        Typeface typefaceNormal = this.selectedFont == 0 ? Typeface.DEFAULT : Typeface.SERIF;
        Typeface typefaceItalic = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/ritalic.ttf") : Typeface.create(CLASSNAMEC.SERIF_NAME, 2);
        Typeface typefaceBold = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmedium.ttf") : Typeface.create(CLASSNAMEC.SERIF_NAME, 1);
        Typeface typefaceBoldItalic = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf") : Typeface.create(CLASSNAMEC.SERIF_NAME, 3);
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
            this.listView.setGlowColor(-657673);
        } else if (currentColor == 1) {
            this.backgroundPaint.setColor(-659492);
            this.listView.setGlowColor(-659492);
        } else if (currentColor == 2) {
            this.backgroundPaint.setColor(-15461356);
            this.listView.setGlowColor(-15461356);
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
        this.containerView = new FrameLayout(activity);
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
        this.fullscreenVideoContainer.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        this.fullscreenVideoContainer.setVisibility(4);
        this.windowView.addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.fullscreenAspectRatioView = new AspectRatioFrameLayout(activity);
        this.fullscreenAspectRatioView.setVisibility(8);
        this.fullscreenVideoContainer.addView(this.fullscreenAspectRatioView, LayoutHelper.createFrame(-1, -1, 17));
        this.fullscreenTextureView = new TextureView(activity);
        this.listView = new RecyclerListView(activity) {
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
        };
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(this.parentActivity, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        recyclerListView = this.listView;
        Adapter webpageAdapter = new WebpageAdapter(this.parentActivity);
        this.adapter = webpageAdapter;
        recyclerListView.setAdapter(webpageAdapter);
        this.listView.setClipToPadding(false);
        this.listView.setPadding(0, AndroidUtilities.m9dp(56.0f), 0, 0);
        this.listView.setTopGlowOffset(AndroidUtilities.m9dp(56.0f));
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemLongClickListener(new ArticleViewer$$Lambda$7(this));
        this.listView.setOnItemClickListener(new ArticleViewer$$Lambda$8(this));
        this.listView.setOnScrollListener(new CLASSNAME());
        this.headerPaint.setColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        this.headerProgressPaint.setColor(-14408666);
        this.headerView = new FrameLayout(activity) {
            protected void onDraw(Canvas canvas) {
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                canvas.drawRect(0.0f, 0.0f, (float) width, (float) height, ArticleViewer.this.headerPaint);
                if (ArticleViewer.this.layoutManager != null) {
                    View view;
                    int first = ArticleViewer.this.layoutManager.findFirstVisibleItemPosition();
                    int last = ArticleViewer.this.layoutManager.findLastVisibleItemPosition();
                    int count = ArticleViewer.this.layoutManager.getItemCount();
                    if (last >= count - 2) {
                        view = ArticleViewer.this.layoutManager.findViewByPosition(count - 2);
                    } else {
                        view = ArticleViewer.this.layoutManager.findViewByPosition(first);
                    }
                    if (view != null) {
                        float viewProgress;
                        float itemProgress = ((float) width) / ((float) (count - 1));
                        int childCount = ArticleViewer.this.layoutManager.getChildCount();
                        float viewHeight = (float) view.getMeasuredHeight();
                        if (last >= count - 2) {
                            viewProgress = ((((float) ((count - 2) - first)) * itemProgress) * ((float) (ArticleViewer.this.listView.getMeasuredHeight() - view.getTop()))) / viewHeight;
                        } else {
                            viewProgress = itemProgress * (1.0f - ((((float) Math.min(0, view.getTop() - ArticleViewer.this.listView.getPaddingTop())) + viewHeight) / viewHeight));
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
        this.backButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.headerView.addView(this.backButton, LayoutHelper.createFrame(54, 56.0f));
        this.backButton.setOnClickListener(new ArticleViewer$$Lambda$10(this));
        this.titleTextView = new SimpleTextView(activity);
        this.titleTextView.setGravity(19);
        this.titleTextView.setTextSize(20);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setTextColor(-5000269);
        this.titleTextView.setPivotX(0.0f);
        this.titleTextView.setPivotY((float) AndroidUtilities.m9dp(28.0f));
        this.headerView.addView(this.titleTextView, LayoutHelper.createFrame(-1, 56.0f, 51, 72.0f, 0.0f, 96.0f, 0.0f));
        this.lineProgressView = new LineProgressView(activity);
        this.lineProgressView.setProgressColor(-1);
        this.lineProgressView.setPivotX(0.0f);
        this.lineProgressView.setPivotY((float) AndroidUtilities.m9dp(2.0f));
        this.headerView.addView(this.lineProgressView, LayoutHelper.createFrame(-1, 2.0f, 83, 0.0f, 0.0f, 0.0f, 1.0f));
        this.lineProgressTickRunnable = new ArticleViewer$$Lambda$11(this);
        LinearLayout settingsContainer = new LinearLayout(this.parentActivity);
        settingsContainer.setPadding(0, AndroidUtilities.m9dp(4.0f), 0, AndroidUtilities.m9dp(4.0f));
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
                    int i = (!this.nightModeEnabled || this.selectedColor == 2) ? -3355444 : -15428119;
                    imageView.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
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
        TextView textView = new TextView(this.parentActivity);
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
        this.settingsButton = new ActionBarMenuItem(this.parentActivity, null, Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, -1);
        this.settingsButton.setPopupAnimationEnabled(false);
        this.settingsButton.setLayoutInScreen(true);
        textView = new TextView(this.parentActivity);
        textView.setTextSize(1, 18.0f);
        textView.setText("Aa");
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextColor(-5000269);
        textView.setGravity(17);
        this.settingsButton.addView(textView, LayoutHelper.createFrame(-1, -1.0f));
        this.settingsButton.addSubItem(settingsContainer, AndroidUtilities.m9dp(220.0f), -2);
        this.settingsButton.redrawPopup(-1);
        this.headerView.addView(this.settingsButton, LayoutHelper.createFrame(48, 56.0f, 53, 0.0f, 0.0f, 56.0f, 0.0f));
        this.shareContainer = new FrameLayout(activity);
        this.headerView.addView(this.shareContainer, LayoutHelper.createFrame(48, 56, 53));
        this.shareContainer.setOnClickListener(new ArticleViewer$$Lambda$15(this));
        this.shareButton = new ImageView(activity);
        this.shareButton.setScaleType(ScaleType.CENTER);
        this.shareButton.setImageResource(R.drawable.ic_share_article);
        this.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
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
        this.blackPaint.setColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        this.actionBar = new CLASSNAMEActionBar(activity);
        this.actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.formatString("Of", R.string.Of, Integer.valueOf(1), Integer.valueOf(1)));
        this.photoContainerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.addItem(2, (int) R.drawable.share);
        this.menuItem = menu.addItem(0, (int) R.drawable.ic_ab_other);
        this.menuItem.setLayoutInScreen(true);
        this.menuItem.addSubItem(3, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp));
        this.menuItem.addSubItem(1, LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
        this.bottomLayout = new FrameLayout(this.parentActivity);
        this.bottomLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.photoContainerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        this.groupedPhotosListView = new GroupedPhotosListView(this.parentActivity);
        this.photoContainerView.addView(this.groupedPhotosListView, LayoutHelper.createFrame(-1, 62.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
        this.groupedPhotosListView.setDelegate(new CLASSNAME());
        this.captionTextViewNext = new TextView(activity);
        this.captionTextViewNext.setMaxLines(10);
        this.captionTextViewNext.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.captionTextViewNext.setMovementMethod(new LinkMovementMethodMy());
        this.captionTextViewNext.setPadding(AndroidUtilities.m9dp(20.0f), AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(20.0f), AndroidUtilities.m9dp(8.0f));
        this.captionTextViewNext.setLinkTextColor(-1);
        this.captionTextViewNext.setTextColor(-1);
        this.captionTextViewNext.setHighlightColor(NUM);
        this.captionTextViewNext.setGravity(19);
        this.captionTextViewNext.setTextSize(1, 16.0f);
        this.captionTextViewNext.setVisibility(8);
        this.photoContainerView.addView(this.captionTextViewNext, LayoutHelper.createFrame(-1, -2, 83));
        this.captionTextView = new TextView(activity);
        this.captionTextView.setMaxLines(10);
        this.captionTextView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.captionTextView.setMovementMethod(new LinkMovementMethodMy());
        this.captionTextView.setPadding(AndroidUtilities.m9dp(20.0f), AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(20.0f), AndroidUtilities.m9dp(8.0f));
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
                if (!ArticleViewer.this.videoPlayerSeekbar.onTouch(event.getAction(), event.getX() - ((float) AndroidUtilities.m9dp(48.0f)), event.getY())) {
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
                    if (duration == CLASSNAMEC.TIME_UNSET) {
                        duration = 0;
                    }
                } else {
                    duration = 0;
                }
                duration /= 1000;
                ArticleViewer.this.videoPlayerSeekbar.setSize((getMeasuredWidth() - AndroidUtilities.m9dp(64.0f)) - ((int) Math.ceil((double) ArticleViewer.this.videoPlayerTime.getPaint().measureText(String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(duration / 60), Long.valueOf(duration % 60), Long.valueOf(duration / 60), Long.valueOf(duration % 60)})))), getMeasuredHeight());
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
                canvas.translate((float) AndroidUtilities.m9dp(48.0f), 0.0f);
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

    final /* synthetic */ void lambda$setParentActivity$11$ArticleViewer(View view, int position) {
        if (position != this.adapter.localBlocks.size() || this.currentPage == null) {
            if (position >= 0 && position < this.adapter.localBlocks.size()) {
                PageBlock pageBlock = (PageBlock) this.adapter.localBlocks.get(position);
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
                        if (this.blocks.indexOf(originalBlock) >= 0) {
                            TL_pageBlockDetails pageBlockDetails = (TL_pageBlockDetails) pageBlock;
                            pageBlockDetails.open = !pageBlockDetails.open;
                            int oldCount = this.adapter.getItemCount();
                            this.adapter.updateRows();
                            int changeCount = Math.abs(this.adapter.getItemCount() - oldCount);
                            BlockDetailsCell cell = (BlockDetailsCell) view;
                            cell.arrow.setAnimationProgressAnimated(pageBlockDetails.open ? 0.0f : 1.0f);
                            cell.invalidate();
                            if (changeCount == 0) {
                                return;
                            }
                            if (pageBlockDetails.open) {
                                this.adapter.notifyItemRangeInserted(position + 1, changeCount);
                            } else {
                                this.adapter.notifyItemRangeRemoved(position + 1, changeCount);
                            }
                        }
                    }
                }
            }
        } else if (this.previewsReqId == 0) {
            TLObject object = MessagesController.getInstance(this.currentAccount).getUserOrChat("previews");
            if (object instanceof TL_user) {
                openPreviewsChat((User) object, this.currentPage.var_id);
                return;
            }
            int currentAccount = UserConfig.selectedAccount;
            long pageId = this.currentPage.var_id;
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
        this.nightModeEnabled = !this.nightModeEnabled;
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putBoolean("nightModeEnabled", this.nightModeEnabled).commit();
        updateNightModeButton();
        updatePaintColors();
        this.adapter.notifyDataSetChanged();
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
        this.adapter.notifyDataSetChanged();
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
        this.adapter.notifyDataSetChanged();
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
            this.nightModeHintView.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
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
            animatorArr[0] = ObjectAnimator.ofFloat(this.nightModeHintView, "translationY", new float[]{(float) AndroidUtilities.m9dp(100.0f), 0.0f});
            animatorSet.playTogether(animatorArr);
            animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
            animatorSet.addListener(new CLASSNAME());
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
        if (this.currentHeaderHeight != AndroidUtilities.m9dp(56.0f)) {
            ValueAnimator va = ValueAnimator.ofObject(new IntEvaluator(), new Object[]{Integer.valueOf(this.currentHeaderHeight), Integer.valueOf(AndroidUtilities.m9dp(56.0f))}).setDuration(180);
            va.setInterpolator(new DecelerateInterpolator());
            va.addUpdateListener(new ArticleViewer$$Lambda$18(this));
            va.start();
        }
    }

    final /* synthetic */ void lambda$checkScrollAnimated$21$ArticleViewer(ValueAnimator animation) {
        setCurrentHeaderHeight(((Integer) animation.getAnimatedValue()).intValue());
    }

    private void setCurrentHeaderHeight(int newHeight) {
        int maxHeight = AndroidUtilities.m9dp(56.0f);
        int minHeight = Math.max(AndroidUtilities.statusBarHeight, AndroidUtilities.m9dp(24.0f));
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
        this.listView.setTopGlowOffset(this.currentHeaderHeight);
    }

    private void checkScroll(int dy) {
        setCurrentHeaderHeight(this.currentHeaderHeight - dy);
    }

    private void openPreviewsChat(User user, long wid) {
        if (user != null && this.parentActivity != null) {
            Bundle args = new Bundle();
            args.putInt("user_id", user.var_id);
            args.putString("botUser", "webpage" + wid);
            ((LaunchActivity) this.parentActivity).presentFragment(new ChatActivity(args), false, true);
            close(false, true);
        }
    }

    private void addAllMediaFromBlock(PageBlock block) {
        int count;
        int a;
        PageBlock innerBlock;
        if (block instanceof TL_pageBlockPhoto) {
            TL_pageBlockPhoto pageBlockPhoto = (TL_pageBlockPhoto) block;
            Photo photo = getPhotoWithId(pageBlockPhoto.photo_id);
            if (photo != null) {
                pageBlockPhoto.thumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 56, true);
                this.photoBlocks.add(block);
            }
        } else if ((block instanceof TL_pageBlockVideo) && isVideoBlock(block)) {
            TL_pageBlockVideo pageBlockVideo = (TL_pageBlockVideo) block;
            Document document = getDocumentWithId(pageBlockVideo.video_id);
            if (document != null) {
                pageBlockVideo.thumb = document.thumb;
                this.photoBlocks.add(block);
            }
        } else if (block instanceof TL_pageBlockSlideshow) {
            TL_pageBlockSlideshow slideshow = (TL_pageBlockSlideshow) block;
            count = slideshow.items.size();
            for (a = 0; a < count; a++) {
                innerBlock = (PageBlock) slideshow.items.get(a);
                innerBlock.groupId = this.lastBlockNum;
                addAllMediaFromBlock(innerBlock);
            }
            this.lastBlockNum++;
        } else if (block instanceof TL_pageBlockCollage) {
            TL_pageBlockCollage collage = (TL_pageBlockCollage) block;
            count = collage.items.size();
            for (a = 0; a < count; a++) {
                innerBlock = (PageBlock) collage.items.get(a);
                innerBlock.groupId = this.lastBlockNum;
                addAllMediaFromBlock(innerBlock);
            }
            this.lastBlockNum++;
        } else if (block instanceof TL_pageBlockCover) {
            addAllMediaFromBlock(((TL_pageBlockCover) block).cover);
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
                        FileLog.m13e(e);
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
        this.listView.setTranslationY(0.0f);
        this.listView.setAlpha(1.0f);
        this.windowView.setInnerTranslationX(0.0f);
        this.actionBar.setVisibility(8);
        this.bottomLayout.setVisibility(8);
        this.captionTextView.setVisibility(8);
        this.captionTextViewNext.setVisibility(8);
        this.layoutManager.scrollToPositionWithOffset(0, 0);
        checkScrollAnimated();
        boolean scrolledToAnchor = addPageToStack(webpage, anchor);
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
                FileLog.m13e(e3);
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
        r2[0] = ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f, 1.0f});
        fArr = new float[2];
        r2[1] = ObjectAnimator.ofFloat(this.containerView, "alpha", new float[]{0.0f, 1.0f});
        r2[2] = ObjectAnimator.ofFloat(this.windowView, "translationX", new float[]{(float) AndroidUtilities.m9dp(56.0f), 0.0f});
        animatorSet.playTogether(r2);
        this.animationEndRunnable = new ArticleViewer$$Lambda$20(this);
        animatorSet.setDuration(150);
        animatorSet.setInterpolator(this.interpolator);
        animatorSet.addListener(new CLASSNAME());
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
                webpages.put(webPage.var_id, webPage);
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
                ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().remove("article" + this.currentPage.var_id).commit();
                updateInterfaceForCurrentPage(false);
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
            animatorArr[0] = ObjectAnimator.ofFloat(this.shareButton, "scaleX", new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.shareButton, "scaleY", new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.shareButton, "alpha", new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
        } else {
            this.shareButton.setVisibility(0);
            this.shareContainer.setEnabled(true);
            animatorSet = this.progressViewAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.shareButton, "scaleX", new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.shareButton, "scaleY", new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.shareButton, "alpha", new float[]{1.0f});
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
                FileLog.m13e(e);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[12];
            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "translationX", new float[]{(float) (this.containerView.getMeasuredWidth() - AndroidUtilities.m9dp(56.0f))});
            FrameLayout frameLayout = this.containerView;
            String str = "translationY";
            float[] fArr = new float[1];
            int currentActionBarHeight = CLASSNAMEActionBar.getCurrentActionBarHeight();
            if (VERSION.SDK_INT >= 21) {
                i = AndroidUtilities.statusBarHeight;
            } else {
                i = 0;
            }
            fArr[0] = (float) (i + currentActionBarHeight);
            animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
            animatorArr[2] = ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.listView, "alpha", new float[]{0.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.listView, "translationY", new float[]{(float) (-AndroidUtilities.m9dp(56.0f))});
            animatorArr[5] = ObjectAnimator.ofFloat(this.headerView, "translationY", new float[]{0.0f});
            animatorArr[6] = ObjectAnimator.ofFloat(this.backButton, "scaleX", new float[]{1.0f});
            animatorArr[7] = ObjectAnimator.ofFloat(this.backButton, "scaleY", new float[]{1.0f});
            animatorArr[8] = ObjectAnimator.ofFloat(this.backButton, "translationY", new float[]{0.0f});
            animatorArr[9] = ObjectAnimator.ofFloat(this.shareContainer, "scaleX", new float[]{1.0f});
            animatorArr[10] = ObjectAnimator.ofFloat(this.shareContainer, "translationY", new float[]{0.0f});
            animatorArr[11] = ObjectAnimator.ofFloat(this.shareContainer, "scaleY", new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
            this.collapsed = true;
            this.animationInProgress = 2;
            this.animationEndRunnable = new ArticleViewer$$Lambda$22(this);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(250);
            animatorSet.addListener(new CLASSNAME());
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
            r1 = new Animator[12];
            r1[0] = ObjectAnimator.ofFloat(this.containerView, "translationX", new float[]{0.0f});
            r1[1] = ObjectAnimator.ofFloat(this.containerView, "translationY", new float[]{0.0f});
            r1[2] = ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{1.0f});
            r1[3] = ObjectAnimator.ofFloat(this.listView, "alpha", new float[]{1.0f});
            r1[4] = ObjectAnimator.ofFloat(this.listView, "translationY", new float[]{0.0f});
            r1[5] = ObjectAnimator.ofFloat(this.headerView, "translationY", new float[]{0.0f});
            r1[6] = ObjectAnimator.ofFloat(this.backButton, "scaleX", new float[]{1.0f});
            r1[7] = ObjectAnimator.ofFloat(this.backButton, "scaleY", new float[]{1.0f});
            r1[8] = ObjectAnimator.ofFloat(this.backButton, "translationY", new float[]{0.0f});
            r1[9] = ObjectAnimator.ofFloat(this.shareContainer, "scaleX", new float[]{1.0f});
            r1[10] = ObjectAnimator.ofFloat(this.shareContainer, "translationY", new float[]{0.0f});
            r1[11] = ObjectAnimator.ofFloat(this.shareContainer, "scaleY", new float[]{1.0f});
            animatorSet.playTogether(r1);
            this.collapsed = false;
            this.animationInProgress = 2;
            this.animationEndRunnable = new ArticleViewer$$Lambda$23(this);
            animatorSet.setDuration(250);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.addListener(new CLASSNAME());
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
            int position = this.layoutManager.findFirstVisibleItemPosition();
            if (position != -1) {
                int offset;
                View view = this.layoutManager.findViewByPosition(position);
                if (view != null) {
                    offset = view.getTop();
                } else {
                    offset = 0;
                }
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit();
                String key = "article" + this.currentPage.var_id;
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
                    FileLog.m13e(e);
                }
                AnimatorSet animatorSet = new AnimatorSet();
                r2 = new Animator[3];
                r2[0] = ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f});
                r2[1] = ObjectAnimator.ofFloat(this.containerView, "alpha", new float[]{0.0f});
                r2[2] = ObjectAnimator.ofFloat(this.windowView, "translationX", new float[]{0.0f, (float) AndroidUtilities.m9dp(56.0f)});
                animatorSet.playTogether(r2);
                this.animationInProgress = 2;
                this.animationEndRunnable = new ArticleViewer$$Lambda$24(this);
                animatorSet.setDuration(150);
                animatorSet.setInterpolator(this.interpolator);
                animatorSet.addListener(new CLASSNAME());
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
        this.blocks.clear();
        this.photoBlocks.clear();
        this.adapter.notifyDataSetChanged();
        try {
            this.parentActivity.getWindow().clearFlags(128);
        } catch (Throwable e) {
            FileLog.m13e(e);
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
            FileLog.m13e(e);
        }
    }

    private void loadChannel(BlockChannelCell cell, Chat channel) {
        if (!this.loadingChannel && !TextUtils.isEmpty(channel.username)) {
            this.loadingChannel = true;
            TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
            req.username = channel.username;
            int currentAccount = UserConfig.selectedAccount;
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, new ArticleViewer$$Lambda$26(this, currentAccount, cell));
        }
    }

    final /* synthetic */ void lambda$loadChannel$31$ArticleViewer(int currentAccount, BlockChannelCell cell, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$39(this, error, response, currentAccount, cell));
    }

    final /* synthetic */ void lambda$null$30$ArticleViewer(TL_error error, TLObject response, int currentAccount, BlockChannelCell cell) {
        this.loadingChannel = false;
        if (this.parentFragment != null && this.blocks != null && !this.blocks.isEmpty()) {
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
            MessagesController.getInstance(currentAccount).generateJoinMessage(channel.var_id, true);
        }
        AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$37(cell));
        AndroidUtilities.runOnUIThread(new ArticleViewer$$Lambda$38(currentAccount, channel), 1000);
        MessagesStorage.getInstance(currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), null, true, channel.var_id);
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
                FileLog.m13e(e);
            }
            for (int a = 0; a < this.createdWebViews.size(); a++) {
                ((BlockEmbedCell) this.createdWebViews.get(a)).destroyWebView(true);
            }
            this.createdWebViews.clear();
            try {
                this.parentActivity.getWindow().clearFlags(128);
            } catch (Throwable e2) {
                FileLog.m13e(e2);
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
                FileLog.m13e(e);
            }
            try {
                this.visibleDialog = dialog;
                this.visibleDialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new ArticleViewer$$Lambda$28(this));
                dialog.show();
            } catch (Throwable e2) {
                FileLog.m13e(e2);
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
                    Builder builder = new Builder(this.parentActivity);
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
                        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this.parentActivity, "org.telegram.messenger.beta.provider", f));
                        intent.setFlags(1);
                    } catch (Exception e) {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                    }
                } else {
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                }
                this.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
            } catch (Throwable e2) {
                FileLog.m13e(e2);
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
            if (this.videoPlayer.getDuration() / 1000 == CLASSNAMEC.TIME_UNSET || current == CLASSNAMEC.TIME_UNSET) {
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
                this.videoPlayer.setDelegate(new CLASSNAME());
                if (this.videoPlayer != null) {
                    duration = this.videoPlayer.getDuration();
                    if (duration == CLASSNAMEC.TIME_UNSET) {
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
            this.videoPlayer.releasePlayer();
            this.videoPlayer = null;
        }
        try {
            this.parentActivity.getWindow().clearFlags(128);
        } catch (Throwable e) {
            FileLog.m13e(e);
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
            CLASSNAMEActionBar CLASSNAMEActionBar = this.actionBar;
            String str = "alpha";
            float[] fArr = new float[1];
            if (show) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            fArr[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(CLASSNAMEActionBar, str, fArr));
            GroupedPhotosListView groupedPhotosListView = this.groupedPhotosListView;
            str = "alpha";
            fArr = new float[1];
            if (show) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            fArr[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(groupedPhotosListView, str, fArr));
            frameLayout = this.bottomLayout;
            str = "alpha";
            fArr = new float[1];
            if (show) {
                f2 = 1.0f;
            } else {
                f2 = 0.0f;
            }
            fArr[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, str, fArr));
            if (this.captionTextView.getTag() != null) {
                textView = this.captionTextView;
                String str2 = "alpha";
                float[] fArr2 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(textView, str2, fArr2));
            }
            this.currentActionBarAnimation = new AnimatorSet();
            this.currentActionBarAnimation.playTogether(arrayList);
            if (!show) {
                this.currentActionBarAnimation.addListener(new CLASSNAME());
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

    private FileLocation getFileLocation(int index, int[] size) {
        if (index < 0 || index >= this.imagesArr.size()) {
            return null;
        }
        TLObject media = getMedia(index);
        if (media instanceof Photo) {
            PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(((Photo) media).sizes, AndroidUtilities.getPhotoSize());
            if (sizeFull != null) {
                size[0] = sizeFull.size;
                if (size[0] == 0) {
                    size[0] = -1;
                }
                return sizeFull.location;
            }
            size[0] = -1;
            return null;
        } else if (!(media instanceof Document)) {
            return null;
        } else {
            Document document = (Document) media;
            if (document.thumb == null) {
                return null;
            }
            size[0] = document.thumb.size;
            if (size[0] == 0) {
                size[0] = -1;
            }
            return document.thumb.location;
        }
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
                    captionToSet = getText(null, caption, caption, this.currentMedia, -AndroidUtilities.m9dp(100.0f));
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
            int count = this.listView.getChildCount();
            for (a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
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
        CharSequence str = Emoji.replaceEmoji(result2, this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.m9dp(20.0f), false);
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
        FileLocation fileLocation = getFileLocation(index, size);
        if (fileLocation != null) {
            TLObject media = getMedia(index);
            BitmapHolder placeHolder;
            if (media instanceof Photo) {
                Drawable bitmapDrawable;
                FileLocation fileLocation2;
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
                if (thumbLocation != null) {
                    fileLocation2 = thumbLocation.location;
                } else {
                    fileLocation2 = null;
                }
                imageReceiver.setImage(fileLocation, null, null, bitmapDrawable, fileLocation2, "b", size[0], null, this.currentPage, 1);
            } else if (isMediaVideo(index)) {
                if (fileLocation instanceof TL_fileLocationUnavailable) {
                    imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(R.drawable.photoview_placeholder));
                    return;
                }
                placeHolder = null;
                if (this.currentThumb != null && imageReceiver == this.centerImage) {
                    placeHolder = this.currentThumb;
                }
                imageReceiver.setImage(null, null, null, placeHolder != null ? new BitmapDrawable(placeHolder.bitmap) : null, fileLocation, "b", 0, null, this.currentPage, 1);
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
        if (this.parentActivity == null || this.isPhotoVisible || checkPhotoAnimation() || block == null) {
            return false;
        }
        PlaceProviderObject object = getPlaceForPhoto(block);
        if (object == null) {
            return false;
        }
        float scale;
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
        int index = this.photoBlocks.indexOf(block);
        this.imagesArr.clear();
        if (!(block instanceof TL_pageBlockVideo) || isVideoBlock(block)) {
            this.imagesArr.addAll(this.photoBlocks);
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
        int clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
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
        this.animationValues[1][0] = scale;
        this.animationValues[1][1] = scale;
        this.animationValues[1][2] = xPos;
        this.animationValues[1][3] = yPos;
        this.animationValues[1][4] = 0.0f;
        this.animationValues[1][5] = 0.0f;
        this.animationValues[1][6] = 0.0f;
        this.animationValues[1][7] = 0.0f;
        this.photoContainerView.setVisibility(0);
        this.photoContainerBackground.setVisibility(0);
        this.animatingImageView.setAnimationProgress(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        r23 = new Animator[6];
        float[] fArr = new float[2];
        r23[0] = ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f});
        int[] iArr = new int[2];
        r23[1] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, "alpha", new int[]{0, 255});
        fArr = new float[2];
        r23[2] = ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{0.0f, 1.0f});
        fArr = new float[2];
        r23[3] = ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[]{0.0f, 1.0f});
        fArr = new float[2];
        r23[4] = ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[]{0.0f, 1.0f});
        fArr = new float[2];
        r23[5] = ObjectAnimator.ofFloat(this.groupedPhotosListView, "alpha", new float[]{0.0f, 1.0f});
        animatorSet.playTogether(r23);
        this.photoAnimationEndRunnable = new ArticleViewer$$Lambda$29(this);
        animatorSet.setDuration(200);
        animatorSet.addListener(new CLASSNAME());
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
                    object.imageReceiver.setVisible(false, true);
                    int clipHorizontal = Math.abs(drawRegion.left - object.imageReceiver.getImageX());
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
                    this.animationValues[1][0] = object.scale;
                    this.animationValues[1][1] = object.scale;
                    this.animationValues[1][2] = ((float) object.viewX) + (((float) drawRegion.left) * object.scale);
                    this.animationValues[1][3] = ((float) object.viewY) + (((float) drawRegion.top) * object.scale);
                    this.animationValues[1][4] = ((float) clipHorizontal) * object.scale;
                    this.animationValues[1][5] = ((float) clipTop) * object.scale;
                    this.animationValues[1][6] = ((float) clipBottom) * object.scale;
                    this.animationValues[1][7] = (float) object.radius;
                    Animator[] animatorArr = new Animator[6];
                    float[] fArr = new float[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f});
                    animatorArr[1] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, "alpha", new int[]{0});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{0.0f});
                    animatorArr[3] = ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[]{0.0f});
                    animatorArr[4] = ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[]{0.0f});
                    animatorArr[5] = ObjectAnimator.ofFloat(this.groupedPhotosListView, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                } else {
                    float f;
                    int h = AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight;
                    Animator[] animatorArr2 = new Animator[7];
                    animatorArr2[0] = ObjectAnimator.ofInt(this.photoBackgroundDrawable, "alpha", new int[]{0});
                    animatorArr2[1] = ObjectAnimator.ofFloat(this.animatingImageView, "alpha", new float[]{0.0f});
                    ClippingImageView clippingImageView = this.animatingImageView;
                    String str = "translationY";
                    float[] fArr2 = new float[1];
                    if (this.translationY >= 0.0f) {
                        f = (float) h;
                    } else {
                        f = (float) (-h);
                    }
                    fArr2[0] = f;
                    animatorArr2[2] = ObjectAnimator.ofFloat(clippingImageView, str, fArr2);
                    animatorArr2[3] = ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{0.0f});
                    animatorArr2[4] = ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[]{0.0f});
                    animatorArr2[5] = ObjectAnimator.ofFloat(this.captionTextView, "alpha", new float[]{0.0f});
                    animatorArr2[6] = ObjectAnimator.ofFloat(this.groupedPhotosListView, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr2);
                }
                this.photoAnimationEndRunnable = new ArticleViewer$$Lambda$32(this, object);
                animatorSet.setDuration(200);
                animatorSet.addListener(new CLASSNAME());
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
                if (dx > ((float) AndroidUtilities.m9dp(3.0f)) || dy > ((float) AndroidUtilities.m9dp(3.0f))) {
                    this.discardTap = true;
                }
                if (this.canDragDown && !this.draggingDown && this.scale == 1.0f && dy >= ((float) AndroidUtilities.m9dp(30.0f)) && dy / 2.0f > dx) {
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
                    if (this.moving || ((this.scale == 1.0f && Math.abs(moveDy) + ((float) AndroidUtilities.m9dp(12.0f)) < Math.abs(moveDx)) || this.scale != 1.0f)) {
                        if (!this.moving) {
                            moveDx = 0.0f;
                            moveDy = 0.0f;
                            this.moving = true;
                            this.canDragDown = false;
                        }
                        this.moveStartX = ev.getX();
                        this.moveStartY = ev.getY();
                        updateMinMax(this.scale);
                        if ((this.translationX < this.minX && !this.rightImage.hasImage()) || (this.translationX > this.maxX && !this.leftImage.hasImage())) {
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
                if ((this.translationX < this.minX - ((float) (getContainerViewWidth() / 3)) || velocity < ((float) (-AndroidUtilities.m9dp(650.0f)))) && this.rightImage.hasImage()) {
                    goToNext();
                    return true;
                } else if ((this.translationX > this.maxX + ((float) (getContainerViewWidth() / 3)) || velocity > ((float) AndroidUtilities.m9dp(650.0f))) && this.leftImage.hasImage()) {
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
        animateTo(this.scale, ((this.minX - ((float) getContainerViewWidth())) - extra) - ((float) (AndroidUtilities.m9dp(30.0f) / 2)), this.translationY, false);
    }

    private void goToPrev() {
        float extra = 0.0f;
        if (this.scale != 1.0f) {
            extra = ((float) ((getContainerViewWidth() - this.centerImage.getImageWidth()) / 2)) * this.scale;
        }
        this.switchImageAfterAnimation = 2;
        animateTo(this.scale, ((this.maxX + ((float) getContainerViewWidth())) + extra) + ((float) (AndroidUtilities.m9dp(30.0f) / 2)), this.translationY, false);
    }

    private void animateTo(float newScale, float newTx, float newTy, boolean isZoom) {
        animateTo(newScale, newTx, newTy, isZoom, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
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
            this.imageMoveAnimation.addListener(new CLASSNAME());
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
                if (currentTranslationX > this.maxX + ((float) AndroidUtilities.m9dp(5.0f))) {
                    sideImage = this.leftImage;
                } else if (currentTranslationX < this.minX - ((float) AndroidUtilities.m9dp(5.0f))) {
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
                    tranlateX = (float) ((-canvas.getWidth()) - (AndroidUtilities.m9dp(30.0f) / 2));
                }
                if (sideImage.hasBitmapImage()) {
                    canvas.save();
                    canvas.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                    canvas.translate(((float) (canvas.getWidth() + (AndroidUtilities.m9dp(30.0f) / 2))) + tranlateX, 0.0f);
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
                canvas.translate(((((float) canvas.getWidth()) * (this.scale + 1.0f)) + ((float) AndroidUtilities.m9dp(30.0f))) / 2.0f, (-currentTranslationY) / currentScale);
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
                    canvas.translate(((-((((float) canvas.getWidth()) * (this.scale + 1.0f)) + ((float) AndroidUtilities.m9dp(30.0f)))) / 2.0f) + currentTranslationX, 0.0f);
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
                canvas.translate((-((((float) canvas.getWidth()) * (this.scale + 1.0f)) + ((float) AndroidUtilities.m9dp(30.0f)))) / 2.0f, (-currentTranslationY) / currentScale);
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
                if (x >= ((float) (getContainerViewWidth() - AndroidUtilities.m9dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.m9dp(100.0f))) / 2.0f && y >= ((float) (getContainerViewHeight() - AndroidUtilities.m9dp(100.0f))) / 2.0f && y <= ((float) (getContainerViewHeight() + AndroidUtilities.m9dp(100.0f))) / 2.0f) {
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
        ImageReceiver imageReceiver = getImageReceiverFromListView(this.listView, pageBlock, this.coords);
        if (imageReceiver == null) {
            return null;
        }
        PlaceProviderObject object = new PlaceProviderObject();
        object.viewX = this.coords[0];
        object.viewY = this.coords[1];
        object.parentView = this.listView;
        object.imageReceiver = imageReceiver;
        object.thumb = imageReceiver.getBitmapSafe();
        object.radius = imageReceiver.getRoundRadius();
        object.clipTopAddition = this.currentHeaderHeight;
        return object;
    }
}
