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
import android.graphics.Canvas;
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
import android.text.Layout.Alignment;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
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
import java.util.Calendar;
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
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessagePosition;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.Page;
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
import org.telegram.ui.Components.RadialProgress2;
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
    private float[][] animationValues = ((float[][]) Array.newInstance(float.class, new int[]{2, 10}));
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
    boolean hasCutout;
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
    private Paint statusBarPaint = new Paint();
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

    private class BlockAuthorDateCell extends View {
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
                CharSequence access$20000 = articleViewer.getText(this, richText, richText, tL_pageBlockAuthorDate, i);
                Spannable spannable = null;
                if (access$20000 instanceof Spannable) {
                    spannable = (Spannable) access$20000;
                    metricAffectingSpanArr = (MetricAffectingSpan[]) spannable.getSpans(0, access$20000.length(), MetricAffectingSpan.class);
                } else {
                    metricAffectingSpanArr = null;
                }
                if (this.currentBlock.published_date != 0 && !TextUtils.isEmpty(access$20000)) {
                    formatString = LocaleController.formatString("ArticleDateByAuthor", NUM, LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000), access$20000);
                } else if (TextUtils.isEmpty(access$20000)) {
                    formatString = LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000);
                } else {
                    formatString = LocaleController.formatString("ArticleByAuthor", NUM, access$20000);
                }
                if (metricAffectingSpanArr != null) {
                    try {
                        if (metricAffectingSpanArr.length > 0) {
                            indexOf = TextUtils.indexOf(formatString, access$20000);
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
                this.textLayout = ArticleViewer.this.createLayoutForText(this, formatString, null, i - AndroidUtilities.dp(36.0f), this.currentBlock, this.parentAdapter);
                if (this.textLayout != null) {
                    indexOf = (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight()) + 0;
                    if (ArticleViewer.this.isRtl) {
                        this.textX = (int) Math.floor((double) ((((float) i) - this.textLayout.getLineWidth(0)) - ((float) AndroidUtilities.dp(16.0f))));
                    } else {
                        this.textX = AndroidUtilities.dp(18.0f);
                    }
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
    }

    private class BlockBlockquoteCell extends View {
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
                this.textLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockBlockquote.text, i2, tL_pageBlockBlockquote, this.parentAdapter);
                dp = this.textLayout != null ? 0 + (AndroidUtilities.dp(8.0f) + this.textLayout.getHeight()) : 0;
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
                articleViewer = ArticleViewer.this;
                tL_pageBlockBlockquote = this.currentBlock;
                this.textLayout2 = articleViewer.createLayoutForText(this, null, tL_pageBlockBlockquote.caption, i2, tL_pageBlockBlockquote, this.parentAdapter);
                if (this.textLayout2 != null) {
                    this.textY2 = AndroidUtilities.dp(8.0f) + dp;
                    dp += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                }
                if (dp != 0) {
                    dp += AndroidUtilities.dp(8.0f);
                }
            } else {
                dp = 1;
            }
            setMeasuredDimension(i, dp);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
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
                    int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(20.0f);
                    canvas.drawRect((float) measuredWidth, (float) AndroidUtilities.dp(6.0f), (float) (measuredWidth + AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), ArticleViewer.quoteLinePaint);
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
            int access$13200 = ArticleViewer.this.getSelectedColor();
            if (this.currentType == 0) {
                this.textView.setTextColor(-14840360);
                if (access$13200 == 0) {
                    this.backgroundPaint.setColor(-526345);
                } else if (access$13200 == 1) {
                    this.backgroundPaint.setColor(-1712440);
                } else if (access$13200 == 2) {
                    this.backgroundPaint.setColor(-15000805);
                }
                this.imageView.setColorFilter(new PorterDuffColorFilter(-6710887, Mode.MULTIPLY));
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
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, (CharSequence) tL_pageBlockChannel.channel.title, null, (i2 - AndroidUtilities.dp(52.0f)) - this.buttonWidth, (PageBlock) this.currentBlock, StaticLayoutEx.ALIGN_LEFT(), this.parentAdapter);
                if (ArticleViewer.this.isRtl) {
                    this.textX2 = this.textX;
                } else {
                    this.textX2 = (getMeasuredWidth() - this.textX) - this.buttonWidth;
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
                            Photo access$12900 = ArticleViewer.this.getPhotoWithId(((TL_pageBlockPhoto) tLObject).photo_id);
                            if (access$12900 == null) {
                                i4++;
                            } else {
                                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(access$12900.sizes, AndroidUtilities.getPhotoSize());
                            }
                        } else {
                            if (tLObject instanceof TL_pageBlockVideo) {
                                Document access$10800 = ArticleViewer.this.getDocumentWithId(((TL_pageBlockVideo) tLObject).video_id);
                                if (access$10800 != null) {
                                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(access$10800.thumbs, 90);
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
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r14_15 'i3' int) = (r14_2 'i3' int), (r14_0 'i3' int), (r14_14 'i3' int) binds: {(r14_2 'i3' int)=B:71:0x04f3, (r14_0 'i3' int)=B:72:0x04f8, (r14_14 'i3' int)=B:218:0x073f} in method: org.telegram.ui.ArticleViewer.BlockCollageCell.GroupedMessages.calculate():void, dex: classes.dex
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
            int access$13200 = ArticleViewer.this.getSelectedColor();
            if (access$13200 == 0) {
                this.innerListView.setGlowColor(-657673);
            } else if (access$13200 == 1) {
                this.innerListView.setGlowColor(-659492);
            } else if (access$13200 == 2) {
                this.innerListView.setGlowColor(-15461356);
            }
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
                ArticleViewer articleViewer = ArticleViewer.this;
                TL_pageBlockCollage tL_pageBlockCollage2 = this.currentBlock;
                this.captionLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockCollage2.caption.text, i3, tL_pageBlockCollage2, this.parentAdapter);
                if (this.captionLayout != null) {
                    this.textY = AndroidUtilities.dp(8.0f) + i2;
                    this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    i2 += this.creditOffset + AndroidUtilities.dp(4.0f);
                }
                articleViewer = ArticleViewer.this;
                tL_pageBlockCollage2 = this.currentBlock;
                this.creditLayout = articleViewer.createLayoutForText((View) this, null, tL_pageBlockCollage2.caption.credit, i3, (PageBlock) tL_pageBlockCollage2, articleViewer.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.creditLayout != null) {
                    i2 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
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

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(4.0f) + 1);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
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
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, tL_pageBlockDetails.title, i - AndroidUtilities.dp(52.0f), (PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    i2 = Math.max(i2, AndroidUtilities.dp(21.0f) + this.textLayout.getHeight());
                    this.textY = ((this.textLayout.getHeight() + AndroidUtilities.dp(21.0f)) - this.textLayout.getHeight()) / 2;
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
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                float measuredHeight = (float) (getMeasuredHeight() - 1);
                canvas.drawLine(0.0f, measuredHeight, (float) getMeasuredWidth(), measuredHeight, ArticleViewer.dividerPaint);
            }
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
                        articleViewer.showDialog(new ShareAlert(articleViewer.parentActivity, null, BlockEmbedCell.this.currentBlock.url, false, BlockEmbedCell.this.currentBlock.url, true));
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
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int i3;
            i = MeasureSpec.getSize(i);
            TL_pageBlockEmbed tL_pageBlockEmbed = this.currentBlock;
            if (tL_pageBlockEmbed != null) {
                int i4;
                int dp;
                i2 = tL_pageBlockEmbed.level;
                if (i2 > 0) {
                    i2 = AndroidUtilities.dp((float) (i2 * 14)) + AndroidUtilities.dp(18.0f);
                    this.listX = i2;
                    this.textX = i2;
                    i2 = i - (this.listX + AndroidUtilities.dp(18.0f));
                    i4 = i2;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    dp = i - AndroidUtilities.dp(36.0f);
                    if (this.currentBlock.full_width) {
                        i2 = i;
                    } else {
                        i2 = i - AndroidUtilities.dp(36.0f);
                        this.listX += AndroidUtilities.dp(18.0f);
                    }
                    i4 = dp;
                }
                int i5 = this.currentBlock.w;
                float f = i5 == 0 ? 1.0f : ((float) i) / ((float) i5);
                dp = this.exactWebViewHeight;
                if (dp != 0) {
                    i5 = AndroidUtilities.dp((float) dp);
                } else {
                    TL_pageBlockEmbed tL_pageBlockEmbed2 = this.currentBlock;
                    int i6 = tL_pageBlockEmbed2.w;
                    float f2 = (float) tL_pageBlockEmbed2.h;
                    if (i6 == 0) {
                        f2 = (float) AndroidUtilities.dp(f2);
                    }
                    i5 = (int) (f2 * f);
                }
                if (i5 == 0) {
                    i5 = AndroidUtilities.dp(10.0f);
                }
                i3 = i5;
                this.webView.measure(MeasureSpec.makeMeasureSpec(i2, NUM), MeasureSpec.makeMeasureSpec(i3, NUM));
                if (this.videoView.getParent() == this) {
                    this.videoView.measure(MeasureSpec.makeMeasureSpec(i2, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f) + i3, NUM));
                }
                ArticleViewer articleViewer = ArticleViewer.this;
                TL_pageBlockEmbed tL_pageBlockEmbed3 = this.currentBlock;
                this.captionLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockEmbed3.caption.text, i4, tL_pageBlockEmbed3, this.parentAdapter);
                if (this.captionLayout != null) {
                    this.textY = AndroidUtilities.dp(8.0f) + i3;
                    this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    i3 += this.creditOffset + AndroidUtilities.dp(4.0f);
                }
                articleViewer = ArticleViewer.this;
                tL_pageBlockEmbed3 = this.currentBlock;
                this.creditLayout = articleViewer.createLayoutForText((View) this, null, tL_pageBlockEmbed3.caption.credit, i4, (PageBlock) tL_pageBlockEmbed3, articleViewer.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.creditLayout != null) {
                    i3 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                i3 += AndroidUtilities.dp(5.0f);
                tL_pageBlockEmbed = this.currentBlock;
                if (tL_pageBlockEmbed.level > 0 && !tL_pageBlockEmbed.bottom) {
                    i2 = AndroidUtilities.dp(8.0f);
                } else if (this.currentBlock.level == 0 && this.captionLayout != null) {
                    i2 = AndroidUtilities.dp(8.0f);
                }
                i3 += i2;
            } else {
                i3 = 1;
            }
            setMeasuredDimension(i, i3);
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
                    dp = size - AndroidUtilities.dp(50.0f);
                    articleViewer = ArticleViewer.this;
                    tL_pageBlockEmbedPost2 = this.currentBlock;
                    this.captionLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockEmbedPost2.caption.text, dp, tL_pageBlockEmbedPost2, this.parentAdapter);
                    if (this.captionLayout != null) {
                        this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                        i4 = 0 + (this.creditOffset + AndroidUtilities.dp(4.0f));
                    }
                    articleViewer = ArticleViewer.this;
                    tL_pageBlockEmbedPost2 = this.currentBlock;
                    this.creditLayout = articleViewer.createLayoutForText((View) this, null, tL_pageBlockEmbedPost2.caption.credit, dp, (PageBlock) tL_pageBlockEmbedPost2, articleViewer.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                    if (this.creditLayout != null) {
                        i4 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    }
                    this.textX = AndroidUtilities.dp(18.0f);
                    this.textY = AndroidUtilities.dp(4.0f);
                    i3 = i4;
                } else {
                    boolean z = tL_pageBlockEmbedPost.author_photo_id != 0;
                    this.avatarVisible = z;
                    if (z) {
                        Photo access$12900 = ArticleViewer.this.getPhotoWithId(this.currentBlock.author_photo_id);
                        boolean z2 = access$12900 instanceof TL_photo;
                        this.avatarVisible = z2;
                        if (z2) {
                            this.avatarDrawable.setInfo(0, this.currentBlock.author, null);
                            this.avatarImageView.setImage(ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(access$12900.sizes, AndroidUtilities.dp(40.0f), true), access$12900), "40_40", this.avatarDrawable, 0, null, ArticleViewer.this.currentPage, 1);
                        }
                    }
                    this.nameLayout = ArticleViewer.this.createLayoutForText(this, this.currentBlock.author, null, size - AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 50)), 0, this.currentBlock, Alignment.ALIGN_NORMAL, 1, this.parentAdapter);
                    if (this.currentBlock.date != 0) {
                        articleViewer = ArticleViewer.this;
                        String format = LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.date) * 1000);
                        if (this.avatarVisible) {
                            i4 = 54;
                        }
                        this.dateLayout = articleViewer.createLayoutForText(this, format, null, size - AndroidUtilities.dp((float) (i4 + 50)), this.currentBlock, this.parentAdapter);
                    } else {
                        this.dateLayout = null;
                    }
                    dp = AndroidUtilities.dp(56.0f);
                    if (this.currentBlock.blocks.isEmpty()) {
                        int dp2 = size - AndroidUtilities.dp(50.0f);
                        articleViewer = ArticleViewer.this;
                        tL_pageBlockEmbedPost2 = this.currentBlock;
                        this.captionLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockEmbedPost2.caption.text, dp2, tL_pageBlockEmbedPost2, this.parentAdapter);
                        if (this.captionLayout != null) {
                            this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                            dp += this.creditOffset + AndroidUtilities.dp(4.0f);
                        }
                        int i5 = dp;
                        articleViewer = ArticleViewer.this;
                        tL_pageBlockEmbedPost2 = this.currentBlock;
                        this.creditLayout = articleViewer.createLayoutForText((View) this, null, tL_pageBlockEmbedPost2.caption.credit, dp2, (PageBlock) tL_pageBlockEmbedPost2, articleViewer.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                        if (this.creditLayout != null) {
                            i5 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                        }
                        this.textX = AndroidUtilities.dp(32.0f);
                        this.textY = AndroidUtilities.dp(56.0f);
                        i3 = i5;
                    } else {
                        this.captionLayout = null;
                        this.creditLayout = null;
                        i3 = dp;
                    }
                }
                this.lineHeight = i3;
            }
            setMeasuredDimension(size, i3);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            TL_pageBlockEmbedPost tL_pageBlockEmbedPost = this.currentBlock;
            if (tL_pageBlockEmbedPost != null) {
                if (!(tL_pageBlockEmbedPost instanceof TL_pageBlockEmbedPostCaption)) {
                    if (this.avatarVisible) {
                        this.avatarImageView.draw(canvas);
                    }
                    int i = 54;
                    int i2 = 0;
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
                    float dp = (float) AndroidUtilities.dp(18.0f);
                    float dp2 = (float) AndroidUtilities.dp(6.0f);
                    float dp3 = (float) AndroidUtilities.dp(20.0f);
                    i = this.lineHeight;
                    if (this.currentBlock.level == 0) {
                        i2 = AndroidUtilities.dp(6.0f);
                    }
                    canvas.drawRect(dp, dp2, dp3, (float) (i - i2), ArticleViewer.quoteLinePaint);
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
            }
        }
    }

    private class BlockFooterCell extends View {
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
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, this.currentBlock.text, (i - AndroidUtilities.dp(18.0f)) - this.textX, (PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    i2 = drawingText.getHeight();
                    if (this.currentBlock.level > 0) {
                        i3 = AndroidUtilities.dp(8.0f);
                    } else {
                        i3 = AndroidUtilities.dp(16.0f);
                    }
                    i3 += i2;
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
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, tL_pageBlockHeader.text, i - AndroidUtilities.dp(36.0f), (PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    i3 = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
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
    }

    private class BlockKickerCell extends View {
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
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, tL_pageBlockKicker.text, i - AndroidUtilities.dp(36.0f), (PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                i3 = 0;
                if (this.textLayout != null) {
                    i3 = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                }
                if (this.currentBlock.first) {
                    i3 += AndroidUtilities.dp(8.0f);
                    this.textY = AndroidUtilities.dp(16.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
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
        /* JADX WARNING: Removed duplicated region for block: B:114:0x03a8  */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x03a8  */
        @android.annotation.SuppressLint({"NewApi"})
        public void onMeasure(int r16, int r17) {
            /*
            r15 = this;
            r8 = r15;
            r9 = android.view.View.MeasureSpec.getSize(r16);
            r0 = r8.currentBlock;
            r10 = 1;
            if (r0 == 0) goto L_0x03c7;
        L_0x000a:
            r1 = 0;
            r8.textLayout = r1;
            r0 = r0.index;
            r11 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
            r12 = 0;
            if (r0 != 0) goto L_0x0027;
        L_0x0016:
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r0 = r0.level;
            if (r0 != 0) goto L_0x0027;
        L_0x0022:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r11);
            goto L_0x0028;
        L_0x0027:
            r0 = 0;
        L_0x0028:
            r8.textY = r0;
            r8.numOffsetY = r12;
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r0 = r0.lastMaxNumCalcWidth;
            if (r0 != r9) goto L_0x004a;
        L_0x0038:
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r0 = r0.lastFontSize;
            r1 = org.telegram.ui.ArticleViewer.this;
            r1 = r1.selectedFontSize;
            if (r0 == r1) goto L_0x00fe;
        L_0x004a:
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r0.lastMaxNumCalcWidth = r9;
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r1 = org.telegram.ui.ArticleViewer.this;
            r1 = r1.selectedFontSize;
            r0.lastFontSize = r1;
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r0.maxNumWidth = r12;
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r0 = r0.items;
            r7 = r0.size();
            r13 = 0;
        L_0x007a:
            if (r13 >= r7) goto L_0x00d7;
        L_0x007c:
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r0 = r0.items;
            r0 = r0.get(r13);
            r14 = r0;
            r14 = (org.telegram.ui.ArticleViewer.TL_pageBlockListItem) r14;
            r0 = r14.num;
            if (r0 != 0) goto L_0x0094;
        L_0x0093:
            goto L_0x00d4;
        L_0x0094:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = r14.num;
            r3 = 0;
            r1 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r4 = r9 - r1;
            r5 = r8.currentBlock;
            r6 = r8.parentAdapter;
            r1 = r15;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6);
            r14.numLayout = r0;
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r1 = r8.currentBlock;
            r1 = r1.parent;
            r1 = r1.maxNumWidth;
            r2 = r14.numLayout;
            r2 = r2.getLineWidth(r12);
            r2 = (double) r2;
            r2 = java.lang.Math.ceil(r2);
            r2 = (int) r2;
            r1 = java.lang.Math.max(r1, r2);
            r0.maxNumWidth = r1;
        L_0x00d4:
            r13 = r13 + 1;
            goto L_0x007a;
        L_0x00d7:
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r1 = r8.currentBlock;
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
        L_0x00fe:
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r0 = r0.pageBlockList;
            r0 = r0.ordered;
            r0 = r0 ^ r10;
            r8.drawDot = r0;
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.isRtl;
            r1 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
            r2 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
            if (r0 == 0) goto L_0x0120;
        L_0x0119:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r8.textX = r0;
            goto L_0x0144;
        L_0x0120:
            r0 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r3 = r8.currentBlock;
            r3 = r3.parent;
            r3 = r3.maxNumWidth;
            r0 = r0 + r3;
            r3 = r8.currentBlock;
            r3 = r3.parent;
            r3 = r3.level;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r3 = r3 * r4;
            r0 = r0 + r3;
            r8.textX = r0;
        L_0x0144:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r9 - r0;
            r3 = r8.textX;
            r0 = r0 - r3;
            r3 = org.telegram.ui.ArticleViewer.this;
            r3 = r3.isRtl;
            if (r3 == 0) goto L_0x0178;
        L_0x0155:
            r3 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r4 = r8.currentBlock;
            r4 = r4.parent;
            r4 = r4.maxNumWidth;
            r3 = r3 + r4;
            r4 = r8.currentBlock;
            r4 = r4.parent;
            r4 = r4.level;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r4 = r4 * r1;
            r3 = r3 + r4;
            r0 = r0 - r3;
        L_0x0178:
            r4 = r0;
            r0 = r8.currentBlock;
            r0 = r0.textItem;
            r13 = NUM; // 0x40200000 float:2.5 double:5.315350785E-315;
            r14 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            if (r0 == 0) goto L_0x01ec;
        L_0x0185:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r1 = r8.currentBlock;
            r3 = r1.textItem;
            r5 = r8.currentBlock;
            r1 = org.telegram.ui.ArticleViewer.this;
            r1 = r1.isRtl;
            if (r1 == 0) goto L_0x019d;
        L_0x0198:
            r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT();
            goto L_0x019f;
        L_0x019d:
            r1 = android.text.Layout.Alignment.ALIGN_NORMAL;
        L_0x019f:
            r6 = r1;
            r7 = r8.parentAdapter;
            r1 = r15;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7);
            r8.textLayout = r0;
            r0 = r8.textLayout;
            if (r0 == 0) goto L_0x0387;
        L_0x01ad:
            r0 = r0.getLineCount();
            if (r0 <= 0) goto L_0x0387;
        L_0x01b3:
            r0 = r8.currentBlock;
            r0 = r0.numLayout;
            if (r0 == 0) goto L_0x01df;
        L_0x01bb:
            r0 = r8.currentBlock;
            r0 = r0.numLayout;
            r0 = r0.getLineCount();
            if (r0 <= 0) goto L_0x01df;
        L_0x01c7:
            r0 = r8.textLayout;
            r0 = r0.getLineAscent(r12);
            r1 = r8.currentBlock;
            r1 = r1.numLayout;
            r1 = r1.getLineAscent(r12);
            r2 = org.telegram.messenger.AndroidUtilities.dp(r13);
            r1 = r1 + r2;
            r1 = r1 - r0;
            r8.numOffsetY = r1;
        L_0x01df:
            r0 = r8.textLayout;
            r0 = r0.getHeight();
            r1 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r0 = r0 + r1;
            goto L_0x0386;
        L_0x01ec:
            r0 = r8.currentBlock;
            r0 = r0.blockItem;
            if (r0 == 0) goto L_0x0387;
        L_0x01f4:
            r0 = r8.textX;
            r8.blockX = r0;
            r0 = r8.textY;
            r8.blockY = r0;
            r0 = r8.blockLayout;
            if (r0 == 0) goto L_0x0382;
        L_0x0200:
            r0 = r0.itemView;
            r1 = r0 instanceof org.telegram.ui.ArticleViewer.BlockParagraphCell;
            if (r1 == 0) goto L_0x022d;
        L_0x0206:
            r0 = r8.blockY;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r0 = r0 - r1;
            r8.blockY = r0;
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.isRtl;
            if (r0 != 0) goto L_0x0220;
        L_0x0217:
            r0 = r8.blockX;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 - r1;
            r8.blockX = r0;
        L_0x0220:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 + r4;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r1 = 0 - r1;
            goto L_0x02a7;
        L_0x022d:
            r1 = r0 instanceof org.telegram.ui.ArticleViewer.BlockHeaderCell;
            if (r1 != 0) goto L_0x0290;
        L_0x0231:
            r1 = r0 instanceof org.telegram.ui.ArticleViewer.BlockSubheaderCell;
            if (r1 != 0) goto L_0x0290;
        L_0x0235:
            r1 = r0 instanceof org.telegram.ui.ArticleViewer.BlockTitleCell;
            if (r1 != 0) goto L_0x0290;
        L_0x0239:
            r0 = r0 instanceof org.telegram.ui.ArticleViewer.BlockSubtitleCell;
            if (r0 == 0) goto L_0x023e;
        L_0x023d:
            goto L_0x0290;
        L_0x023e:
            r0 = org.telegram.ui.ArticleViewer.this;
            r1 = r8.currentBlock;
            r1 = r1.blockItem;
            r0 = r0.isListItemBlock(r1);
            if (r0 == 0) goto L_0x0276;
        L_0x024c:
            r8.blockX = r12;
            r8.blockY = r12;
            r8.textY = r12;
            r0 = r8.currentBlock;
            r0 = r0.index;
            if (r0 != 0) goto L_0x026d;
        L_0x025a:
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r0 = r0.level;
            if (r0 != 0) goto L_0x026d;
        L_0x0266:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r11);
            r0 = 0 - r0;
            goto L_0x026e;
        L_0x026d:
            r0 = 0;
        L_0x026e:
            r1 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r0 = r0 - r1;
            r1 = r0;
            r0 = r9;
            goto L_0x02a7;
        L_0x0276:
            r0 = r8.blockLayout;
            r0 = r0.itemView;
            r0 = r0 instanceof org.telegram.ui.ArticleViewer.BlockTableCell;
            if (r0 == 0) goto L_0x028e;
        L_0x027e:
            r0 = r8.blockX;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 - r1;
            r8.blockX = r0;
            r0 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            goto L_0x02a5;
        L_0x028e:
            r0 = r4;
            goto L_0x02a6;
        L_0x0290:
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.isRtl;
            if (r0 != 0) goto L_0x02a1;
        L_0x0298:
            r0 = r8.blockX;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 - r1;
            r8.blockX = r0;
        L_0x02a1:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
        L_0x02a5:
            r0 = r0 + r4;
        L_0x02a6:
            r1 = 0;
        L_0x02a7:
            r2 = r8.blockLayout;
            r2 = r2.itemView;
            r3 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r3);
            r3 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r12);
            r2.measure(r0, r3);
            r0 = r8.blockLayout;
            r0 = r0.itemView;
            r0 = r0 instanceof org.telegram.ui.ArticleViewer.BlockParagraphCell;
            if (r0 == 0) goto L_0x0304;
        L_0x02c0:
            r0 = r8.currentBlock;
            r0 = r0.numLayout;
            if (r0 == 0) goto L_0x0304;
        L_0x02c8:
            r0 = r8.currentBlock;
            r0 = r0.numLayout;
            r0 = r0.getLineCount();
            if (r0 <= 0) goto L_0x0304;
        L_0x02d4:
            r0 = r8.blockLayout;
            r0 = r0.itemView;
            r0 = (org.telegram.ui.ArticleViewer.BlockParagraphCell) r0;
            r2 = r0.textLayout;
            if (r2 == 0) goto L_0x0304;
        L_0x02e0:
            r2 = r0.textLayout;
            r2 = r2.getLineCount();
            if (r2 <= 0) goto L_0x0304;
        L_0x02ea:
            r0 = r0.textLayout;
            r0 = r0.getLineAscent(r12);
            r2 = r8.currentBlock;
            r2 = r2.numLayout;
            r2 = r2.getLineAscent(r12);
            r3 = org.telegram.messenger.AndroidUtilities.dp(r13);
            r2 = r2 + r3;
            r2 = r2 - r0;
            r8.numOffsetY = r2;
        L_0x0304:
            r0 = r8.currentBlock;
            r0 = r0.blockItem;
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockDetails;
            if (r0 == 0) goto L_0x0331;
        L_0x030e:
            r8.verticalAlign = r10;
            r8.blockY = r12;
            r0 = r8.currentBlock;
            r0 = r0.index;
            if (r0 != 0) goto L_0x032b;
        L_0x031a:
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r0 = r0.level;
            if (r0 != 0) goto L_0x032b;
        L_0x0326:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r11);
            r1 = r1 - r0;
        L_0x032b:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r1 = r1 - r0;
            goto L_0x034c;
        L_0x0331:
            r0 = r8.blockLayout;
            r0 = r0.itemView;
            r2 = r0 instanceof org.telegram.ui.ArticleViewer.BlockOrderedListItemCell;
            if (r2 == 0) goto L_0x0342;
        L_0x0339:
            r0 = (org.telegram.ui.ArticleViewer.BlockOrderedListItemCell) r0;
            r0 = r0.verticalAlign;
            r8.verticalAlign = r0;
            goto L_0x034c;
        L_0x0342:
            r2 = r0 instanceof org.telegram.ui.ArticleViewer.BlockListItemCell;
            if (r2 == 0) goto L_0x034c;
        L_0x0346:
            r0 = (org.telegram.ui.ArticleViewer.BlockListItemCell) r0;
            r0 = r0.verticalAlign;
            r8.verticalAlign = r0;
        L_0x034c:
            r0 = r8.verticalAlign;
            if (r0 == 0) goto L_0x0378;
        L_0x0350:
            r0 = r8.currentBlock;
            r0 = r0.numLayout;
            if (r0 == 0) goto L_0x0378;
        L_0x0358:
            r0 = r8.blockLayout;
            r0 = r0.itemView;
            r0 = r0.getMeasuredHeight();
            r2 = r8.currentBlock;
            r2 = r2.numLayout;
            r2 = r2.getHeight();
            r0 = r0 - r2;
            r0 = r0 / 2;
            r2 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r0 = r0 - r2;
            r8.textY = r0;
            r8.drawDot = r12;
        L_0x0378:
            r0 = r8.blockLayout;
            r0 = r0.itemView;
            r0 = r0.getMeasuredHeight();
            r12 = r1 + r0;
        L_0x0382:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
        L_0x0386:
            r12 = r12 + r0;
        L_0x0387:
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r0 = r0.items;
            r1 = r8.currentBlock;
            r1 = r1.parent;
            r1 = r1.items;
            r1 = r1.size();
            r1 = r1 - r10;
            r0 = r0.get(r1);
            r1 = r8.currentBlock;
            if (r0 != r1) goto L_0x03ad;
        L_0x03a8:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r12 = r12 + r0;
        L_0x03ad:
            r0 = r8.currentBlock;
            r0 = r0.index;
            if (r0 != 0) goto L_0x03c6;
        L_0x03b5:
            r0 = r8.currentBlock;
            r0 = r0.parent;
            r0 = r0.level;
            if (r0 != 0) goto L_0x03c6;
        L_0x03c1:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r11);
            r12 = r12 + r0;
        L_0x03c6:
            r10 = r12;
        L_0x03c7:
            r15.setMeasuredDimension(r9, r10);
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
                    if (ArticleViewer.this.isRtl) {
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
                    this.textLayout.draw(canvas);
                    canvas.restore();
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
                    Activity access$2500 = ArticleViewer.this.parentActivity;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("geo:");
                    stringBuilder.append(d);
                    stringBuilder.append(str);
                    stringBuilder.append(d2);
                    stringBuilder.append("?q=");
                    stringBuilder.append(d);
                    stringBuilder.append(str);
                    stringBuilder.append(d2);
                    access$2500.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
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
        /* JADX WARNING: Removed duplicated region for block: B:33:0x013e  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0123  */
        /* JADX WARNING: Removed duplicated region for block: B:37:0x0155  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x01ce  */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x00b9  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0123  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x013e  */
        /* JADX WARNING: Removed duplicated region for block: B:37:0x0155  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x01ce  */
        /* JADX WARNING: Removed duplicated region for block: B:16:0x0063  */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x00b9  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x013e  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0123  */
        /* JADX WARNING: Removed duplicated region for block: B:37:0x0155  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x01ce  */
        @android.annotation.SuppressLint({"NewApi"})
        public void onMeasure(int r30, int r31) {
            /*
            r29 = this;
            r8 = r29;
            r0 = android.view.View.MeasureSpec.getSize(r30);
            r1 = r8.currentType;
            r2 = 1;
            r9 = 2;
            r3 = 0;
            if (r1 != r2) goto L_0x0024;
        L_0x000d:
            r0 = r29.getParent();
            r0 = (android.view.View) r0;
            r0 = r0.getMeasuredWidth();
            r1 = r29.getParent();
            r1 = (android.view.View) r1;
            r1 = r1.getMeasuredHeight();
            r10 = r0;
            r0 = r1;
            goto L_0x0029;
        L_0x0024:
            r10 = r0;
            if (r1 != r9) goto L_0x0028;
        L_0x0027:
            goto L_0x0029;
        L_0x0028:
            r0 = 0;
        L_0x0029:
            r1 = r8.currentBlock;
            if (r1 == 0) goto L_0x01d4;
        L_0x002d:
            r4 = r8.currentType;
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
            r8.textX = r1;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r4 = r4 + r1;
            r4 = r10 - r4;
            r7 = r4;
            goto L_0x005f;
        L_0x004e:
            r1 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r8.textX = r1;
            r1 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r1 = r10 - r1;
            r7 = r1;
            r4 = r10;
            r1 = 0;
        L_0x005f:
            r5 = r8.currentType;
            if (r5 != 0) goto L_0x00b0;
        L_0x0063:
            r0 = (float) r4;
            r5 = r8.currentBlock;
            r6 = r5.w;
            r6 = (float) r6;
            r0 = r0 / r6;
            r5 = r5.h;
            r5 = (float) r5;
            r0 = r0 * r5;
            r0 = (int) r0;
            r5 = org.telegram.ui.ArticleViewer.this;
            r5 = r5.listView;
            r5 = r5[r3];
            r5 = r5.getMeasuredWidth();
            r6 = org.telegram.ui.ArticleViewer.this;
            r6 = r6.listView;
            r6 = r6[r3];
            r6 = r6.getMeasuredHeight();
            r5 = java.lang.Math.max(r5, r6);
            r6 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
            r5 = r5 - r6;
            r5 = (float) r5;
            r6 = NUM; // 0x3var_ float:0.9 double:5.2552552E-315;
            r5 = r5 * r6;
            r5 = (int) r5;
            if (r0 <= r5) goto L_0x00b0;
        L_0x009c:
            r0 = (float) r5;
            r4 = r8.currentBlock;
            r6 = r4.h;
            r6 = (float) r6;
            r0 = r0 / r6;
            r4 = r4.w;
            r4 = (float) r4;
            r0 = r0 * r4;
            r4 = (int) r0;
            r0 = r10 - r1;
            r0 = r0 - r4;
            r0 = r0 / r9;
            r1 = r1 + r0;
            r11 = r5;
            goto L_0x00b1;
        L_0x00b0:
            r11 = r0;
        L_0x00b1:
            r0 = r8.imageView;
            r5 = r8.isFirst;
            r12 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            if (r5 != 0) goto L_0x00cb;
        L_0x00b9:
            r5 = r8.currentType;
            if (r5 == r2) goto L_0x00cb;
        L_0x00bd:
            if (r5 == r9) goto L_0x00cb;
        L_0x00bf:
            r2 = r8.currentBlock;
            r2 = r2.level;
            if (r2 <= 0) goto L_0x00c6;
        L_0x00c5:
            goto L_0x00cb;
        L_0x00c6:
            r2 = org.telegram.messenger.AndroidUtilities.dp(r12);
            goto L_0x00cc;
        L_0x00cb:
            r2 = 0;
        L_0x00cc:
            r0.setImageCoords(r1, r2, r4, r11);
            r0 = org.telegram.ui.ArticleViewer.this;
            r13 = r0.currentAccount;
            r0 = r8.currentBlock;
            r0 = r0.geo;
            r14 = r0.lat;
            r0 = r0._long;
            r2 = (float) r4;
            r4 = org.telegram.messenger.AndroidUtilities.density;
            r5 = r2 / r4;
            r5 = (int) r5;
            r6 = (float) r11;
            r4 = r6 / r4;
            r4 = (int) r4;
            r20 = 1;
            r21 = 15;
            r22 = -1;
            r16 = r0;
            r18 = r5;
            r19 = r4;
            r24 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r13, r14, r16, r18, r19, r20, r21, r22);
            r0 = r8.currentBlock;
            r0 = r0.geo;
            r1 = org.telegram.messenger.AndroidUtilities.density;
            r2 = r2 / r1;
            r2 = (int) r2;
            r6 = r6 / r1;
            r4 = (int) r6;
            r5 = 15;
            r13 = (double) r1;
            r13 = java.lang.Math.ceil(r13);
            r1 = (int) r13;
            r1 = java.lang.Math.min(r9, r1);
            r0 = org.telegram.messenger.WebFile.createWithGeoPoint(r0, r2, r4, r5, r1);
            r1 = org.telegram.ui.ArticleViewer.this;
            r1 = r1.currentAccount;
            r1 = org.telegram.messenger.MessagesController.getInstance(r1);
            r1 = r1.mapProvider;
            r8.currentMapProvider = r1;
            r1 = r8.currentMapProvider;
            if (r1 != r9) goto L_0x013e;
        L_0x0123:
            if (r0 == 0) goto L_0x0151;
        L_0x0125:
            r13 = r8.imageView;
            r14 = org.telegram.messenger.ImageLocation.getForWebFile(r0);
            r15 = 0;
            r0 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
            r16 = r0[r3];
            r17 = 0;
            r0 = org.telegram.ui.ArticleViewer.this;
            r18 = r0.currentPage;
            r19 = 0;
            r13.setImage(r14, r15, r16, r17, r18, r19);
            goto L_0x0151;
        L_0x013e:
            if (r24 == 0) goto L_0x0151;
        L_0x0140:
            r0 = r8.imageView;
            r25 = 0;
            r1 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
            r26 = r1[r3];
            r27 = 0;
            r28 = 0;
            r23 = r0;
            r23.setImage(r24, r25, r26, r27, r28);
        L_0x0151:
            r0 = r8.currentType;
            if (r0 != 0) goto L_0x01b7;
        L_0x0155:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r5 = r8.currentBlock;
            r1 = r5.caption;
            r3 = r1.text;
            r6 = r8.parentAdapter;
            r1 = r29;
            r4 = r7;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6);
            r8.captionLayout = r0;
            r0 = r8.captionLayout;
            r13 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
            if (r0 == 0) goto L_0x0184;
        L_0x016f:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
            r1 = r8.captionLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r8.creditOffset = r0;
            r0 = r8.creditOffset;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r13);
            r0 = r0 + r1;
            r11 = r11 + r0;
        L_0x0184:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r5 = r8.currentBlock;
            r1 = r5.caption;
            r3 = r1.credit;
            r1 = r0.isRtl;
            if (r1 == 0) goto L_0x0198;
        L_0x0193:
            r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT();
            goto L_0x019a;
        L_0x0198:
            r1 = android.text.Layout.Alignment.ALIGN_NORMAL;
        L_0x019a:
            r6 = r1;
            r14 = r8.parentAdapter;
            r1 = r29;
            r4 = r7;
            r7 = r14;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7);
            r8.creditLayout = r0;
            r0 = r8.creditLayout;
            if (r0 == 0) goto L_0x01b7;
        L_0x01ab:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
            r1 = r8.creditLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r11 = r11 + r0;
        L_0x01b7:
            r0 = r8.isFirst;
            if (r0 != 0) goto L_0x01ca;
        L_0x01bb:
            r0 = r8.currentType;
            if (r0 != 0) goto L_0x01ca;
        L_0x01bf:
            r0 = r8.currentBlock;
            r0 = r0.level;
            if (r0 > 0) goto L_0x01ca;
        L_0x01c5:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r12);
            r11 = r11 + r0;
        L_0x01ca:
            r0 = r8.currentType;
            if (r0 == r9) goto L_0x01d3;
        L_0x01ce:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r12);
            r11 = r11 + r0;
        L_0x01d3:
            r2 = r11;
        L_0x01d4:
            r8.setMeasuredDimension(r10, r2);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$BlockMapCell.onMeasure(int, int):void");
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                this.imageView.draw(canvas);
                if (this.currentMapProvider == 2 && this.imageView.hasNotThumb()) {
                    int intrinsicWidth = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicWidth()) * 0.8f);
                    int intrinsicHeight = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicHeight()) * 0.8f);
                    int imageX = this.imageView.getImageX() + ((this.imageView.getImageWidth() - intrinsicWidth) / 2);
                    int imageY = this.imageView.getImageY() + ((this.imageView.getImageHeight() / 2) - intrinsicHeight);
                    Theme.chat_redLocationIcon.setAlpha((int) (this.imageView.getCurrentAlpha() * 255.0f));
                    Theme.chat_redLocationIcon.setBounds(imageX, imageY, intrinsicWidth + imageX, intrinsicHeight + imageY);
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
    }

    private class BlockOrderedListItemCell extends ViewGroup {
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
        /* JADX WARNING: Removed duplicated region for block: B:103:0x034f  */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x034f  */
        @android.annotation.SuppressLint({"NewApi"})
        public void onMeasure(int r14, int r15) {
            /*
            r13 = this;
            r14 = android.view.View.MeasureSpec.getSize(r14);
            r15 = r13.currentBlock;
            r0 = 1;
            if (r15 == 0) goto L_0x036e;
        L_0x0009:
            r1 = 0;
            r13.textLayout = r1;
            r15 = r15.index;
            r1 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
            r2 = 0;
            if (r15 != 0) goto L_0x0026;
        L_0x0015:
            r15 = r13.currentBlock;
            r15 = r15.parent;
            r15 = r15.level;
            if (r15 != 0) goto L_0x0026;
        L_0x0021:
            r15 = org.telegram.messenger.AndroidUtilities.dp(r1);
            goto L_0x0027;
        L_0x0026:
            r15 = 0;
        L_0x0027:
            r13.textY = r15;
            r13.numOffsetY = r2;
            r15 = r13.currentBlock;
            r15 = r15.parent;
            r15 = r15.lastMaxNumCalcWidth;
            if (r15 != r14) goto L_0x0049;
        L_0x0037:
            r15 = r13.currentBlock;
            r15 = r15.parent;
            r15 = r15.lastFontSize;
            r3 = org.telegram.ui.ArticleViewer.this;
            r3 = r3.selectedFontSize;
            if (r15 == r3) goto L_0x00fc;
        L_0x0049:
            r15 = r13.currentBlock;
            r15 = r15.parent;
            r15.lastMaxNumCalcWidth = r14;
            r15 = r13.currentBlock;
            r15 = r15.parent;
            r3 = org.telegram.ui.ArticleViewer.this;
            r3 = r3.selectedFontSize;
            r15.lastFontSize = r3;
            r15 = r13.currentBlock;
            r15 = r15.parent;
            r15.maxNumWidth = r2;
            r15 = r13.currentBlock;
            r15 = r15.parent;
            r15 = r15.items;
            r15 = r15.size();
            r3 = 0;
        L_0x0079:
            if (r3 >= r15) goto L_0x00d5;
        L_0x007b:
            r4 = r13.currentBlock;
            r4 = r4.parent;
            r4 = r4.items;
            r4 = r4.get(r3);
            r4 = (org.telegram.ui.ArticleViewer.TL_pageBlockOrderedListItem) r4;
            r5 = r4.num;
            if (r5 != 0) goto L_0x0092;
        L_0x0091:
            goto L_0x00d2;
        L_0x0092:
            r6 = org.telegram.ui.ArticleViewer.this;
            r8 = r4.num;
            r9 = 0;
            r5 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r10 = r14 - r5;
            r11 = r13.currentBlock;
            r12 = r13.parentAdapter;
            r7 = r13;
            r5 = r6.createLayoutForText(r7, r8, r9, r10, r11, r12);
            r4.numLayout = r5;
            r5 = r13.currentBlock;
            r5 = r5.parent;
            r6 = r13.currentBlock;
            r6 = r6.parent;
            r6 = r6.maxNumWidth;
            r4 = r4.numLayout;
            r4 = r4.getLineWidth(r2);
            r7 = (double) r4;
            r7 = java.lang.Math.ceil(r7);
            r4 = (int) r7;
            r4 = java.lang.Math.max(r6, r4);
            r5.maxNumWidth = r4;
        L_0x00d2:
            r3 = r3 + 1;
            goto L_0x0079;
        L_0x00d5:
            r15 = r13.currentBlock;
            r15 = r15.parent;
            r3 = r13.currentBlock;
            r3 = r3.parent;
            r3 = r3.maxNumWidth;
            r4 = org.telegram.ui.ArticleViewer.listTextNumPaint;
            r5 = "00.";
            r4 = r4.measureText(r5);
            r4 = (double) r4;
            r4 = java.lang.Math.ceil(r4);
            r4 = (int) r4;
            r3 = java.lang.Math.max(r3, r4);
            r15.maxNumWidth = r3;
        L_0x00fc:
            r15 = org.telegram.ui.ArticleViewer.this;
            r15 = r15.isRtl;
            r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r4 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
            if (r15 == 0) goto L_0x010f;
        L_0x0108:
            r15 = org.telegram.messenger.AndroidUtilities.dp(r4);
            r13.textX = r15;
            goto L_0x0133;
        L_0x010f:
            r15 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
            r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
            r5 = r13.currentBlock;
            r5 = r5.parent;
            r5 = r5.maxNumWidth;
            r15 = r15 + r5;
            r5 = r13.currentBlock;
            r5 = r5.parent;
            r5 = r5.level;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r5 = r5 * r6;
            r15 = r15 + r5;
            r13.textX = r15;
        L_0x0133:
            r13.verticalAlign = r2;
            r15 = org.telegram.messenger.AndroidUtilities.dp(r4);
            r15 = r14 - r15;
            r5 = r13.textX;
            r15 = r15 - r5;
            r5 = org.telegram.ui.ArticleViewer.this;
            r5 = r5.isRtl;
            if (r5 == 0) goto L_0x0169;
        L_0x0146:
            r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r6 = r13.currentBlock;
            r6 = r6.parent;
            r6 = r6.maxNumWidth;
            r5 = r5 + r6;
            r6 = r13.currentBlock;
            r6 = r6.parent;
            r6 = r6.level;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r6 = r6 * r3;
            r5 = r5 + r6;
            r15 = r15 - r5;
        L_0x0169:
            r9 = r15;
            r15 = r13.currentBlock;
            r15 = r15.textItem;
            r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            if (r15 == 0) goto L_0x01d6;
        L_0x0174:
            r5 = org.telegram.ui.ArticleViewer.this;
            r7 = 0;
            r15 = r13.currentBlock;
            r8 = r15.textItem;
            r10 = r13.currentBlock;
            r15 = org.telegram.ui.ArticleViewer.this;
            r15 = r15.isRtl;
            if (r15 == 0) goto L_0x018c;
        L_0x0187:
            r15 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT();
            goto L_0x018e;
        L_0x018c:
            r15 = android.text.Layout.Alignment.ALIGN_NORMAL;
        L_0x018e:
            r11 = r15;
            r12 = r13.parentAdapter;
            r6 = r13;
            r15 = r5.createLayoutForText(r6, r7, r8, r9, r10, r11, r12);
            r13.textLayout = r15;
            r15 = r13.textLayout;
            if (r15 == 0) goto L_0x032e;
        L_0x019c:
            r15 = r15.getLineCount();
            if (r15 <= 0) goto L_0x032e;
        L_0x01a2:
            r15 = r13.currentBlock;
            r15 = r15.numLayout;
            if (r15 == 0) goto L_0x01c9;
        L_0x01aa:
            r15 = r13.currentBlock;
            r15 = r15.numLayout;
            r15 = r15.getLineCount();
            if (r15 <= 0) goto L_0x01c9;
        L_0x01b6:
            r15 = r13.textLayout;
            r15 = r15.getLineAscent(r2);
            r4 = r13.currentBlock;
            r4 = r4.numLayout;
            r4 = r4.getLineAscent(r2);
            r4 = r4 - r15;
            r13.numOffsetY = r4;
        L_0x01c9:
            r15 = r13.textLayout;
            r15 = r15.getHeight();
            r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r15 = r15 + r4;
            goto L_0x032d;
        L_0x01d6:
            r15 = r13.currentBlock;
            r15 = r15.blockItem;
            if (r15 == 0) goto L_0x032e;
        L_0x01de:
            r15 = r13.textX;
            r13.blockX = r15;
            r15 = r13.textY;
            r13.blockY = r15;
            r15 = r13.blockLayout;
            if (r15 == 0) goto L_0x0329;
        L_0x01ea:
            r15 = r15.itemView;
            r5 = r15 instanceof org.telegram.ui.ArticleViewer.BlockParagraphCell;
            if (r5 == 0) goto L_0x0216;
        L_0x01f0:
            r15 = r13.blockY;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r15 = r15 - r5;
            r13.blockY = r15;
            r15 = org.telegram.ui.ArticleViewer.this;
            r15 = r15.isRtl;
            if (r15 != 0) goto L_0x020a;
        L_0x0201:
            r15 = r13.blockX;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
            r15 = r15 - r5;
            r13.blockX = r15;
        L_0x020a:
            r15 = org.telegram.messenger.AndroidUtilities.dp(r4);
            r15 = r15 + r9;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r4 = 0 - r4;
            goto L_0x0275;
        L_0x0216:
            r5 = r15 instanceof org.telegram.ui.ArticleViewer.BlockHeaderCell;
            if (r5 != 0) goto L_0x025e;
        L_0x021a:
            r5 = r15 instanceof org.telegram.ui.ArticleViewer.BlockSubheaderCell;
            if (r5 != 0) goto L_0x025e;
        L_0x021e:
            r5 = r15 instanceof org.telegram.ui.ArticleViewer.BlockTitleCell;
            if (r5 != 0) goto L_0x025e;
        L_0x0222:
            r15 = r15 instanceof org.telegram.ui.ArticleViewer.BlockSubtitleCell;
            if (r15 == 0) goto L_0x0227;
        L_0x0226:
            goto L_0x025e;
        L_0x0227:
            r15 = org.telegram.ui.ArticleViewer.this;
            r5 = r13.currentBlock;
            r5 = r5.blockItem;
            r15 = r15.isListItemBlock(r5);
            if (r15 == 0) goto L_0x0244;
        L_0x0235:
            r13.blockX = r2;
            r13.blockY = r2;
            r13.textY = r2;
            r15 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r15 = 0 - r15;
            r4 = r15;
            r15 = r14;
            goto L_0x0275;
        L_0x0244:
            r15 = r13.blockLayout;
            r15 = r15.itemView;
            r15 = r15 instanceof org.telegram.ui.ArticleViewer.BlockTableCell;
            if (r15 == 0) goto L_0x025c;
        L_0x024c:
            r15 = r13.blockX;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
            r15 = r15 - r4;
            r13.blockX = r15;
            r15 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
            goto L_0x0273;
        L_0x025c:
            r15 = r9;
            goto L_0x0274;
        L_0x025e:
            r15 = org.telegram.ui.ArticleViewer.this;
            r15 = r15.isRtl;
            if (r15 != 0) goto L_0x026f;
        L_0x0266:
            r15 = r13.blockX;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
            r15 = r15 - r5;
            r13.blockX = r15;
        L_0x026f:
            r15 = org.telegram.messenger.AndroidUtilities.dp(r4);
        L_0x0273:
            r15 = r15 + r9;
        L_0x0274:
            r4 = 0;
        L_0x0275:
            r5 = r13.blockLayout;
            r5 = r5.itemView;
            r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            r15 = android.view.View.MeasureSpec.makeMeasureSpec(r15, r6);
            r6 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r2);
            r5.measure(r15, r6);
            r15 = r13.blockLayout;
            r15 = r15.itemView;
            r15 = r15 instanceof org.telegram.ui.ArticleViewer.BlockParagraphCell;
            if (r15 == 0) goto L_0x02cd;
        L_0x028e:
            r15 = r13.currentBlock;
            r15 = r15.numLayout;
            if (r15 == 0) goto L_0x02cd;
        L_0x0296:
            r15 = r13.currentBlock;
            r15 = r15.numLayout;
            r15 = r15.getLineCount();
            if (r15 <= 0) goto L_0x02cd;
        L_0x02a2:
            r15 = r13.blockLayout;
            r15 = r15.itemView;
            r15 = (org.telegram.ui.ArticleViewer.BlockParagraphCell) r15;
            r5 = r15.textLayout;
            if (r5 == 0) goto L_0x02cd;
        L_0x02ae:
            r5 = r15.textLayout;
            r5 = r5.getLineCount();
            if (r5 <= 0) goto L_0x02cd;
        L_0x02b8:
            r15 = r15.textLayout;
            r15 = r15.getLineAscent(r2);
            r5 = r13.currentBlock;
            r5 = r5.numLayout;
            r5 = r5.getLineAscent(r2);
            r5 = r5 - r15;
            r13.numOffsetY = r5;
        L_0x02cd:
            r15 = r13.currentBlock;
            r15 = r15.blockItem;
            r15 = r15 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockDetails;
            if (r15 == 0) goto L_0x02e1;
        L_0x02d7:
            r13.verticalAlign = r0;
            r13.blockY = r2;
            r15 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r4 = r4 - r15;
            goto L_0x02fc;
        L_0x02e1:
            r15 = r13.blockLayout;
            r15 = r15.itemView;
            r2 = r15 instanceof org.telegram.ui.ArticleViewer.BlockOrderedListItemCell;
            if (r2 == 0) goto L_0x02f0;
        L_0x02e9:
            r15 = (org.telegram.ui.ArticleViewer.BlockOrderedListItemCell) r15;
            r15 = r15.verticalAlign;
            r13.verticalAlign = r15;
            goto L_0x02fc;
        L_0x02f0:
            r2 = r15 instanceof org.telegram.ui.ArticleViewer.BlockListItemCell;
            if (r2 == 0) goto L_0x02fc;
        L_0x02f4:
            r15 = (org.telegram.ui.ArticleViewer.BlockListItemCell) r15;
            r15 = r15.verticalAlign;
            r13.verticalAlign = r15;
        L_0x02fc:
            r15 = r13.verticalAlign;
            if (r15 == 0) goto L_0x031f;
        L_0x0300:
            r15 = r13.currentBlock;
            r15 = r15.numLayout;
            if (r15 == 0) goto L_0x031f;
        L_0x0308:
            r15 = r13.blockLayout;
            r15 = r15.itemView;
            r15 = r15.getMeasuredHeight();
            r2 = r13.currentBlock;
            r2 = r2.numLayout;
            r2 = r2.getHeight();
            r15 = r15 - r2;
            r15 = r15 / 2;
            r13.textY = r15;
        L_0x031f:
            r15 = r13.blockLayout;
            r15 = r15.itemView;
            r15 = r15.getMeasuredHeight();
            r2 = r4 + r15;
        L_0x0329:
            r15 = org.telegram.messenger.AndroidUtilities.dp(r3);
        L_0x032d:
            r2 = r2 + r15;
        L_0x032e:
            r15 = r13.currentBlock;
            r15 = r15.parent;
            r15 = r15.items;
            r4 = r13.currentBlock;
            r4 = r4.parent;
            r4 = r4.items;
            r4 = r4.size();
            r4 = r4 - r0;
            r15 = r15.get(r4);
            r0 = r13.currentBlock;
            if (r15 != r0) goto L_0x0354;
        L_0x034f:
            r15 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r2 = r2 + r15;
        L_0x0354:
            r15 = r13.currentBlock;
            r15 = r15.index;
            if (r15 != 0) goto L_0x036d;
        L_0x035c:
            r15 = r13.currentBlock;
            r15 = r15.parent;
            r15 = r15.level;
            if (r15 != 0) goto L_0x036d;
        L_0x0368:
            r15 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r2 = r2 + r15;
        L_0x036d:
            r0 = r2;
        L_0x036e:
            r13.setMeasuredDimension(r14, r0);
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
                    if (ArticleViewer.this.isRtl) {
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
                    this.textLayout.draw(canvas);
                    canvas.restore();
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
    }

    private class BlockParagraphCell extends View {
        private TL_pageBlockParagraph currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX;
        private int textY;

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
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.text, (i - AndroidUtilities.dp(18.0f)) - this.textX, this.textY, this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, 0, this.parentAdapter);
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    i2 = drawingText.getHeight();
                    if (this.currentBlock.level > 0) {
                        i3 = AndroidUtilities.dp(8.0f);
                    } else {
                        i3 = AndroidUtilities.dp(16.0f);
                    }
                    i3 += i2;
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
    }

    private class BlockPreformattedCell extends FrameLayout {
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
                        blockPreformattedCell.textLayout = ArticleViewer.this.createLayoutForText(this, null, blockPreformattedCell.currentBlock.text, AndroidUtilities.dp(5000.0f), BlockPreformattedCell.this.currentBlock, BlockPreformattedCell.this.parentAdapter);
                        if (BlockPreformattedCell.this.textLayout != null) {
                            i = BlockPreformattedCell.this.textLayout.getHeight() + 0;
                            int lineCount = BlockPreformattedCell.this.textLayout.getLineCount();
                            while (i2 < lineCount) {
                                i3 = Math.max((int) Math.ceil((double) BlockPreformattedCell.this.textLayout.getLineWidth(i2)), i3);
                                i2++;
                            }
                        } else {
                            i = 0;
                        }
                    } else {
                        i = 1;
                    }
                    setMeasuredDimension(i3 + AndroidUtilities.dp(32.0f), i);
                }

                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
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
    }

    private class BlockPullquoteCell extends View {
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
                this.textLayout = ArticleViewer.this.createLayoutForText(this, null, tL_pageBlockPullquote.text, i - AndroidUtilities.dp(36.0f), this.currentBlock, this.parentAdapter);
                i3 = 0;
                if (this.textLayout != null) {
                    i3 = 0 + (AndroidUtilities.dp(8.0f) + this.textLayout.getHeight());
                }
                this.textLayout2 = ArticleViewer.this.createLayoutForText(this, null, this.currentBlock.caption, i - AndroidUtilities.dp(36.0f), this.currentBlock, this.parentAdapter);
                if (this.textLayout2 != null) {
                    this.textY2 = AndroidUtilities.dp(2.0f) + i3;
                    i3 += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
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
        /* JADX WARNING: Removed duplicated region for block: B:22:0x0086  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x007f  */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x00c2  */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x0089  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x00d8  */
        /* JADX WARNING: Removed duplicated region for block: B:35:0x0125  */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x0108  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x015d  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x012b  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x01aa  */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x0199  */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x01f8  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x007f  */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x0086  */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x0089  */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x00c2  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x00d8  */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x0108  */
        /* JADX WARNING: Removed duplicated region for block: B:35:0x0125  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x012b  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x015d  */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x0167  */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x0199  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x01aa  */
        /* JADX WARNING: Removed duplicated region for block: B:65:0x01dc A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x01f8  */
        @android.annotation.SuppressLint({"DrawAllocation", "NewApi"})
        public void onMeasure(int r28, int r29) {
            /*
            r27 = this;
            r10 = r27;
            r11 = android.view.View.MeasureSpec.getSize(r28);
            r0 = r10.currentBlock;
            r0 = r0.num;
            r1 = r10.currentBlock;
            r1 = r1.parent;
            r1 = r1.articles;
            r1 = r1.size();
            r12 = 1;
            r1 = r1 - r12;
            r13 = 0;
            if (r0 == r1) goto L_0x001f;
        L_0x001d:
            r0 = 1;
            goto L_0x0020;
        L_0x001f:
            r0 = 0;
        L_0x0020:
            r10.divider = r0;
            r0 = r10.currentBlock;
            r0 = r0.parent;
            r0 = r0.articles;
            r1 = r10.currentBlock;
            r1 = r1.num;
            r0 = r0.get(r1);
            r14 = r0;
            r14 = (org.telegram.tgnet.TLRPC.TL_pageRelatedArticle) r14;
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.selectedFontSize;
            r15 = 4;
            r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
            if (r0 != 0) goto L_0x004a;
        L_0x0042:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r1);
        L_0x0046:
            r0 = -r0;
        L_0x0047:
            r16 = r0;
            goto L_0x0076;
        L_0x004a:
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.selectedFontSize;
            r2 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            if (r0 != r12) goto L_0x0059;
        L_0x0054:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
            goto L_0x0046;
        L_0x0059:
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.selectedFontSize;
            r3 = 3;
            if (r0 != r3) goto L_0x0067;
        L_0x0062:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
            goto L_0x0047;
        L_0x0067:
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.selectedFontSize;
            if (r0 != r15) goto L_0x0074;
        L_0x006f:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r1);
            goto L_0x0047;
        L_0x0074:
            r16 = 0;
        L_0x0076:
            r0 = r14.photo_id;
            r2 = 0;
            r4 = 0;
            r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
            if (r5 == 0) goto L_0x0086;
        L_0x007f:
            r2 = org.telegram.ui.ArticleViewer.this;
            r0 = r2.getPhotoWithId(r0);
            goto L_0x0087;
        L_0x0086:
            r0 = r4;
        L_0x0087:
            if (r0 == 0) goto L_0x00c2;
        L_0x0089:
            r10.drawImage = r12;
            r1 = r0.sizes;
            r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
            r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r2);
            r2 = r0.sizes;
            r3 = 80;
            r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3, r12);
            if (r1 != r2) goto L_0x00a0;
        L_0x009f:
            r2 = r4;
        L_0x00a0:
            r3 = r10.imageView;
            r18 = org.telegram.messenger.ImageLocation.getForPhoto(r1, r0);
            r20 = org.telegram.messenger.ImageLocation.getForPhoto(r2, r0);
            r0 = r1.size;
            r23 = 0;
            r1 = org.telegram.ui.ArticleViewer.this;
            r24 = r1.currentPage;
            r25 = 1;
            r19 = "64_64";
            r21 = "64_64_b";
            r17 = r3;
            r22 = r0;
            r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25);
            goto L_0x00c4;
        L_0x00c2:
            r10.drawImage = r13;
        L_0x00c4:
            r0 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
            r9 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r0 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r0 = r11 - r0;
            r1 = r10.drawImage;
            r17 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
            if (r1 == 0) goto L_0x00fc;
        L_0x00d8:
            r1 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r2 = r10.imageView;
            r3 = r11 - r1;
            r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
            r3 = r3 - r5;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
            r2.setImageCoords(r3, r4, r1, r1);
            r1 = r10.imageView;
            r1 = r1.getImageWidth();
            r2 = org.telegram.messenger.AndroidUtilities.dp(r17);
            r1 = r1 + r2;
            r0 = r0 - r1;
        L_0x00fc:
            r18 = r0;
            r0 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
            r19 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r2 = r14.title;
            if (r2 == 0) goto L_0x0125;
        L_0x0108:
            r0 = org.telegram.ui.ArticleViewer.this;
            r3 = 0;
            r5 = r10.textY;
            r6 = r10.currentBlock;
            r7 = android.text.Layout.Alignment.ALIGN_NORMAL;
            r8 = 3;
            r4 = r10.parentAdapter;
            r1 = r27;
            r20 = r4;
            r4 = r18;
            r26 = r9;
            r9 = r20;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8, r9);
            r10.textLayout = r0;
            goto L_0x0127;
        L_0x0125:
            r26 = r9;
        L_0x0127:
            r0 = r10.textLayout;
            if (r0 == 0) goto L_0x015d;
        L_0x012b:
            r0 = r0.getLineCount();
            r15 = r15 - r0;
            r1 = r10.textLayout;
            r1 = r1.getHeight();
            r2 = org.telegram.messenger.AndroidUtilities.dp(r17);
            r1 = r1 + r2;
            r1 = r1 + r16;
            r10.textOffset = r1;
            r1 = r10.textLayout;
            r1 = r1.getHeight();
            r19 = r19 + r1;
            r1 = 0;
        L_0x0148:
            if (r1 >= r0) goto L_0x015a;
        L_0x014a:
            r2 = r10.textLayout;
            r2 = r2.getLineLeft(r1);
            r3 = 0;
            r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
            if (r2 == 0) goto L_0x0157;
        L_0x0155:
            r0 = 1;
            goto L_0x015b;
        L_0x0157:
            r1 = r1 + 1;
            goto L_0x0148;
        L_0x015a:
            r0 = 0;
        L_0x015b:
            r8 = r15;
            goto L_0x0161;
        L_0x015d:
            r10.textOffset = r13;
            r0 = 0;
            r8 = 4;
        L_0x0161:
            r1 = r14.published_date;
            r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            if (r1 == 0) goto L_0x0191;
        L_0x0167:
            r1 = r14.author;
            r1 = android.text.TextUtils.isEmpty(r1);
            if (r1 != 0) goto L_0x0191;
        L_0x016f:
            r1 = NUM; // 0x7f0e0140 float:1.8875687E38 double:1.0531623147E-314;
            r4 = 2;
            r4 = new java.lang.Object[r4];
            r5 = org.telegram.messenger.LocaleController.getInstance();
            r5 = r5.chatFullDate;
            r6 = r14.published_date;
            r6 = (long) r6;
            r6 = r6 * r2;
            r2 = r5.format(r6);
            r4[r13] = r2;
            r2 = r14.author;
            r4[r12] = r2;
            r2 = "ArticleDateByAuthor";
            r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r4);
            goto L_0x01a8;
        L_0x0191:
            r1 = r14.author;
            r1 = android.text.TextUtils.isEmpty(r1);
            if (r1 != 0) goto L_0x01aa;
        L_0x0199:
            r1 = NUM; // 0x7f0e013f float:1.8875685E38 double:1.053162314E-314;
            r2 = new java.lang.Object[r12];
            r3 = r14.author;
            r2[r13] = r3;
            r3 = "ArticleByAuthor";
            r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2);
        L_0x01a8:
            r2 = r1;
            goto L_0x01cc;
        L_0x01aa:
            r1 = r14.published_date;
            if (r1 == 0) goto L_0x01be;
        L_0x01ae:
            r1 = org.telegram.messenger.LocaleController.getInstance();
            r1 = r1.chatFullDate;
            r4 = r14.published_date;
            r4 = (long) r4;
            r4 = r4 * r2;
            r1 = r1.format(r4);
            goto L_0x01a8;
        L_0x01be:
            r1 = r14.description;
            r1 = android.text.TextUtils.isEmpty(r1);
            if (r1 != 0) goto L_0x01c9;
        L_0x01c6:
            r1 = r14.description;
            goto L_0x01a8;
        L_0x01c9:
            r1 = r14.url;
            goto L_0x01a8;
        L_0x01cc:
            r1 = org.telegram.ui.ArticleViewer.this;
            r3 = 0;
            r4 = r10.textY;
            r5 = r10.textOffset;
            r5 = r5 + r4;
            r6 = r10.currentBlock;
            r4 = r1.isRtl;
            if (r4 != 0) goto L_0x01e2;
        L_0x01dc:
            if (r0 == 0) goto L_0x01df;
        L_0x01de:
            goto L_0x01e2;
        L_0x01df:
            r0 = android.text.Layout.Alignment.ALIGN_NORMAL;
            goto L_0x01e6;
        L_0x01e2:
            r0 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT();
        L_0x01e6:
            r7 = r0;
            r9 = r10.parentAdapter;
            r0 = r1;
            r1 = r27;
            r4 = r18;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8, r9);
            r10.textLayout2 = r0;
            r0 = r10.textLayout2;
            if (r0 == 0) goto L_0x020a;
        L_0x01f8:
            r0 = r0.getHeight();
            r19 = r19 + r0;
            r0 = r10.textLayout;
            if (r0 == 0) goto L_0x020a;
        L_0x0202:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
            r0 = r0 + r16;
            r19 = r19 + r0;
        L_0x020a:
            r1 = r19;
            r0 = r26;
            r0 = java.lang.Math.max(r0, r1);
            r1 = r10.divider;
            r0 = r0 + r1;
            r10.setMeasuredDimension(r11, r0);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$BlockRelatedArticlesCell.onMeasure(int, int):void");
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.drawImage) {
                    this.imageView.draw(canvas);
                }
                canvas.save();
                canvas.translate((float) this.textX, (float) AndroidUtilities.dp(10.0f));
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    drawingText.draw(canvas);
                }
                if (this.textLayout2 != null) {
                    canvas.translate(0.0f, (float) this.textOffset);
                    this.textLayout2.draw(canvas);
                }
                canvas.restore();
                if (this.divider) {
                    canvas.drawLine(ArticleViewer.this.isRtl ? 0.0f : (float) AndroidUtilities.dp(17.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (ArticleViewer.this.isRtl ? AndroidUtilities.dp(17.0f) : 0)), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
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
            } else {
                setMeasuredDimension(i, 1);
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
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
            this.shadowDrawable = new CombinedDrawable(new ColorDrawable(-986896), Theme.getThemedDrawable(context, NUM, -16777216));
            this.shadowDrawable.setFullsize(true);
            setBackgroundDrawable(this.shadowDrawable);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(12.0f));
            i = ArticleViewer.this.getSelectedColor();
            if (i == 0) {
                Theme.setCombinedDrawableColor(this.shadowDrawable, -986896, false);
            } else if (i == 1) {
                Theme.setCombinedDrawableColor(this.shadowDrawable, -1712440, false);
            } else if (i == 2) {
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
            int access$13200 = ArticleViewer.this.getSelectedColor();
            if (access$13200 == 0) {
                AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, -657673);
            } else if (access$13200 == 1) {
                AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, -659492);
            } else if (access$13200 == 2) {
                AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, -15461356);
            }
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
                            Drawable access$18200 = BlockSlideshowCell.this.currentPage == dp ? ArticleViewer.this.slideDotBigDrawable : ArticleViewer.this.slideDotDrawable;
                            access$18200.setBounds(measuredWidth - AndroidUtilities.dp(5.0f), 0, measuredWidth + AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f));
                            access$18200.draw(canvas);
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
                this.captionLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockSlideshow.caption.text, dp, tL_pageBlockSlideshow, this.parentAdapter);
                if (this.captionLayout != null) {
                    this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    i2 += this.creditOffset + AndroidUtilities.dp(4.0f);
                }
                articleViewer = ArticleViewer.this;
                tL_pageBlockSlideshow = this.currentBlock;
                this.creditLayout = articleViewer.createLayoutForText((View) this, null, tL_pageBlockSlideshow.caption.credit, dp, (PageBlock) tL_pageBlockSlideshow, articleViewer.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.creditLayout != null) {
                    i2 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
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
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, tL_pageBlockSubheader.text, i - AndroidUtilities.dp(36.0f), (PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    i3 = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
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
    }

    private class BlockSubtitleCell extends View {
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
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, tL_pageBlockSubtitle.text, i - AndroidUtilities.dp(36.0f), (PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.textLayout != null) {
                    i3 = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
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
    }

    private class BlockTitleCell extends View {
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
                this.textLayout = ArticleViewer.this.createLayoutForText((View) this, null, tL_pageBlockTitle.text, i - AndroidUtilities.dp(36.0f), (PageBlock) this.currentBlock, ArticleViewer.this.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                i3 = 0;
                if (this.textLayout != null) {
                    i3 = 0 + (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight());
                }
                if (this.currentBlock.first) {
                    i3 += AndroidUtilities.dp(8.0f);
                    this.textY = AndroidUtilities.dp(16.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
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
                    ArticleViewer articleViewer = ArticleViewer.this;
                    articleViewer.showCopyPopup(articleViewer.pressedLink.getUrl());
                    ArticleViewer.this.pressedLink = null;
                    ArticleViewer.this.pressedLinkOwnerLayout = null;
                    if (ArticleViewer.this.pressedLinkOwnerView != null) {
                        ArticleViewer.this.pressedLinkOwnerView.invalidate();
                    }
                } else if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLinkOwnerView != null) {
                    ArticleViewer.this.windowView.performHapticFeedback(0);
                    int[] iArr = new int[2];
                    ArticleViewer.this.pressedLinkOwnerView.getLocationInWindow(iArr);
                    int access$4800 = (iArr[1] + ArticleViewer.this.pressedLayoutY) - AndroidUtilities.dp(54.0f);
                    if (access$4800 < 0) {
                        access$4800 = 0;
                    }
                    ArticleViewer.this.pressedLinkOwnerView.invalidate();
                    ArticleViewer.this.drawBlockSelection = true;
                    ArticleViewer articleViewer2 = ArticleViewer.this;
                    articleViewer2.showPopup(articleViewer2.pressedLinkOwnerView, 48, 0, access$4800);
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

        public ColorCell(Context context) {
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
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0f));
            TextView textView = this.textView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            int i2 = i | 48;
            int i3 = 17;
            float f = (float) (LocaleController.isRTL ? 17 : 53);
            if (LocaleController.isRTL) {
                i3 = 53;
            }
            addView(textView, LayoutHelper.createFrame(-1, -1.0f, i2, f, 0.0f, (float) i3, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }

        public void setTextAndColor(String str, int i) {
            this.textView.setText(str);
            this.currentColor = i;
            invalidate();
        }

        public void select(boolean z) {
            if (this.selected != z) {
                this.selected = z;
                invalidate();
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            ArticleViewer.colorPaint.setColor(this.currentColor);
            canvas.drawCircle((float) (!LocaleController.isRTL ? AndroidUtilities.dp(28.0f) : getMeasuredWidth() - AndroidUtilities.dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(10.0f), ArticleViewer.colorPaint);
            if (this.selected) {
                ArticleViewer.selectorPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
                ArticleViewer.selectorPaint.setColor(-15428119);
                canvas.drawCircle((float) (!LocaleController.isRTL ? AndroidUtilities.dp(28.0f) : getMeasuredWidth() - AndroidUtilities.dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(10.0f), ArticleViewer.selectorPaint);
            } else if (this.currentColor == -1) {
                ArticleViewer.selectorPaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
                ArticleViewer.selectorPaint.setColor(-4539718);
                canvas.drawCircle((float) (!LocaleController.isRTL ? AndroidUtilities.dp(28.0f) : getMeasuredWidth() - AndroidUtilities.dp(28.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(9.0f), ArticleViewer.selectorPaint);
            }
        }
    }

    public class DrawingText {
        public LinkPath markPath;
        public StaticLayout textLayout;
        public LinkPath textPath;

        public void draw(Canvas canvas) {
            LinkPath linkPath = this.textPath;
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
    }

    public class FontCell extends FrameLayout {
        private TextView textView;
        private TextView textView2;
        final /* synthetic */ ArticleViewer this$0;

        public FontCell(ArticleViewer articleViewer, Context context) {
            Context context2 = context;
            this.this$0 = articleViewer;
            super(context2);
            setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 2));
            this.textView = new TextView(context2);
            this.textView.setTextColor(-14606047);
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            TextView textView = this.textView;
            int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i3 = 53;
            float f = (float) (LocaleController.isRTL ? 17 : 53);
            if (!LocaleController.isRTL) {
                i3 = 17;
            }
            addView(textView, LayoutHelper.createFrame(-1, -1.0f, i2, f, 0.0f, (float) i3, 0.0f));
            this.textView2 = new TextView(context2);
            this.textView2.setTextColor(-14606047);
            this.textView2.setTextSize(1, 16.0f);
            this.textView2.setLines(1);
            this.textView2.setMaxLines(1);
            this.textView2.setSingleLine(true);
            this.textView2.setText("Aa");
            this.textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView = this.textView2;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            addView(textView, LayoutHelper.createFrame(-1, -1.0f, i | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }

        public void select(boolean z) {
            this.textView2.setTextColor(z ? -15428119 : -14606047);
        }

        public void setTextAndTypeface(String str, Typeface typeface) {
            this.textView.setText(str);
            this.textView.setTypeface(typeface);
            this.textView2.setTypeface(typeface);
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
            int access$21200 = (ArticleViewer.this.getContainerViewWidth() - i) / 2;
            int access$21300 = (ArticleViewer.this.getContainerViewHeight() - i) / 2;
            int i2 = this.previousBackgroundState;
            if (i2 >= 0 && i2 < 4) {
                drawable = ArticleViewer.progressDrawables[this.previousBackgroundState];
                if (drawable != null) {
                    drawable.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                    drawable.setBounds(access$21200, access$21300, access$21200 + i, access$21300 + i);
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
                    drawable.setBounds(access$21200, access$21300, access$21200 + i, access$21300 + i);
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
            this.progressRect.set((float) (access$21200 + dp), (float) (access$21300 + dp), (float) ((access$21200 + i) - dp), (float) ((access$21300 + i) - dp));
            canvas.drawArc(this.progressRect, this.radOffset - 0.049804688f, Math.max(4.0f, this.animatedProgressValue * 360.0f), false, ArticleViewer.progressPaint);
            updateAnimation();
        }
    }

    public class ScrollEvaluator extends IntEvaluator {
        public Integer evaluate(float f, Integer num, Integer num2) {
            return super.evaluate(f, num, num2);
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

        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            int i = 0;
            int i2;
            int i3;
            int i4;
            int i5;
            if (motionEvent.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                i2 = 0;
                while (i2 < 5) {
                    i3 = this.sideSide;
                    i4 = this.lineSize + (this.gapSize * 2);
                    i5 = this.circleSize;
                    i3 = (i3 + ((i4 + i5) * i2)) + (i5 / 2);
                    if (x <= ((float) (i3 - AndroidUtilities.dp(15.0f))) || x >= ((float) (i3 + AndroidUtilities.dp(15.0f)))) {
                        i2++;
                    } else {
                        boolean z;
                        if (i2 == ArticleViewer.this.selectedFontSize) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingQuality = ArticleViewer.this.selectedFontSize;
                    }
                }
            } else if (motionEvent.getAction() == 2) {
                if (this.startMoving) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving) {
                    while (i < 5) {
                        i2 = this.sideSide;
                        i3 = this.lineSize;
                        int i6 = this.gapSize;
                        i4 = (i6 * 2) + i3;
                        i5 = this.circleSize;
                        i2 = (i2 + ((i4 + i5) * i)) + (i5 / 2);
                        i3 = ((i3 / 2) + (i5 / 2)) + i6;
                        if (x <= ((float) (i2 - i3)) || x >= ((float) (i2 + i3))) {
                            i++;
                        } else if (ArticleViewer.this.selectedFontSize != i) {
                            ArticleViewer.this.selectedFontSize = i;
                            ArticleViewer.this.updatePaintSize();
                            invalidate();
                        }
                    }
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (!this.moving) {
                    i2 = 0;
                    while (i2 < 5) {
                        i3 = this.sideSide;
                        i4 = this.lineSize + (this.gapSize * 2);
                        i5 = this.circleSize;
                        i3 = (i3 + ((i4 + i5) * i2)) + (i5 / 2);
                        if (x <= ((float) (i3 - AndroidUtilities.dp(15.0f))) || x >= ((float) (i3 + AndroidUtilities.dp(15.0f)))) {
                            i2++;
                        } else if (ArticleViewer.this.selectedFontSize != i2) {
                            ArticleViewer.this.selectedFontSize = i2;
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

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            MeasureSpec.getSize(i);
            this.circleSize = AndroidUtilities.dp(5.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(17.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * 5)) - ((this.gapSize * 2) * 4)) - (this.sideSide * 2)) / 4;
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            int measuredHeight = getMeasuredHeight() / 2;
            int i = 0;
            while (i < 5) {
                int i2 = this.sideSide;
                int i3 = this.lineSize + (this.gapSize * 2);
                int i4 = this.circleSize;
                i2 = (i2 + ((i3 + i4) * i)) + (i4 / 2);
                if (i <= ArticleViewer.this.selectedFontSize) {
                    this.paint.setColor(-15428119);
                } else {
                    this.paint.setColor(-3355444);
                }
                canvas.drawCircle((float) i2, (float) measuredHeight, (float) (i == ArticleViewer.this.selectedFontSize ? AndroidUtilities.dp(4.0f) : this.circleSize / 2), this.paint);
                if (i != 0) {
                    i2 = ((i2 - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    canvas.drawRect((float) i2, (float) (measuredHeight - AndroidUtilities.dp(1.0f)), (float) (i2 + this.lineSize), (float) (AndroidUtilities.dp(1.0f) + measuredHeight), this.paint);
                }
                i++;
            }
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
                    this.bHeight = windowInsets.getSystemWindowInsetBottom();
                }
                i2 -= windowInsets.getSystemWindowInsetTop();
            }
            ArticleViewer.this.containerView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            ArticleViewer.this.photoContainerView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            ArticleViewer.this.photoContainerBackground.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            ArticleViewer.this.fullscreenVideoContainer.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            ViewGroup.LayoutParams layoutParams = ArticleViewer.this.animatingImageView.getLayoutParams();
            ArticleViewer.this.animatingImageView.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(layoutParams.height, Integer.MIN_VALUE));
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
                    i5 = VERSION.SDK_INT >= 28 ? windowInsets.getSystemWindowInsetTop() + 0 : 0;
                }
                ArticleViewer.this.containerView.layout(i6, i5, ArticleViewer.this.containerView.getMeasuredWidth() + i6, ArticleViewer.this.containerView.getMeasuredHeight() + i5);
                ArticleViewer.this.photoContainerView.layout(i6, i5, ArticleViewer.this.photoContainerView.getMeasuredWidth() + i6, ArticleViewer.this.photoContainerView.getMeasuredHeight() + i5);
                ArticleViewer.this.photoContainerBackground.layout(i6, i5, ArticleViewer.this.photoContainerBackground.getMeasuredWidth() + i6, ArticleViewer.this.photoContainerBackground.getMeasuredHeight() + i5);
                ArticleViewer.this.fullscreenVideoContainer.layout(i6, i5, ArticleViewer.this.fullscreenVideoContainer.getMeasuredWidth() + i6, ArticleViewer.this.fullscreenVideoContainer.getMeasuredHeight() + i5);
                ArticleViewer.this.animatingImageView.layout(0, 0, ArticleViewer.this.animatingImageView.getMeasuredWidth(), ArticleViewer.this.animatingImageView.getMeasuredHeight());
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
            } else {
                this.movingPage = false;
            }
            ArticleViewer.this.cancelCheckLongPress();
        }

        public boolean handleTouchEvent(MotionEvent motionEvent) {
            if (ArticleViewer.this.isPhotoVisible || this.closeAnimationInProgress || ArticleViewer.this.fullscreenVideoContainer.getVisibility() == 0) {
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
                    View access$1400 = this.movingPage ? ArticleViewer.this.listView[0] : ArticleViewer.this.containerView;
                    float x = access$1400.getX();
                    final boolean z = x < ((float) access$1400.getMeasuredWidth()) / 3.0f && (xVelocity < 3500.0f || xVelocity < yVelocity);
                    AnimatorSet animatorSet = new AnimatorSet();
                    Animator[] animatorArr;
                    if (!z) {
                        x = ((float) access$1400.getMeasuredWidth()) - x;
                        if (this.movingPage) {
                            animatorArr = new Animator[1];
                            animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.listView[0], View.TRANSLATION_X, new float[]{(float) access$1400.getMeasuredWidth()});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.containerView, View.TRANSLATION_X, new float[]{(float) access$1400.getMeasuredWidth()});
                            animatorArr[1] = ObjectAnimator.ofFloat(this, ArticleViewer.ARTICLE_VIEWER_INNER_TRANSLATION_X, new float[]{(float) access$1400.getMeasuredWidth()});
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
                    animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) access$1400.getMeasuredWidth())) * x), 50));
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
            }
            return this.startedTracking;
        }

        /* Access modifiers changed, original: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            int i = this.bWidth;
            if (i != 0) {
                int i2 = this.bHeight;
                if (i2 != 0) {
                    int i3 = this.bX;
                    if (i3 == 0) {
                        int i4 = this.bY;
                        if (i4 == 0) {
                            canvas.drawRect((float) i3, (float) i4, (float) (i3 + i), (float) (i4 + i2), ArticleViewer.this.blackPaint);
                            return;
                        }
                    }
                    canvas.drawRect(((float) this.bX) - getTranslationX(), (float) this.bY, ((float) (this.bX + this.bWidth)) - getTranslationX(), (float) (this.bY + this.bHeight), ArticleViewer.this.blackPaint);
                }
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            canvas.drawRect(this.innerTranslationX, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), ArticleViewer.this.backgroundPaint);
            if (VERSION.SDK_INT >= 21) {
                ArticleViewer articleViewer = ArticleViewer.this;
                if (articleViewer.hasCutout && articleViewer.lastInsets != null) {
                    Canvas canvas2 = canvas;
                    canvas2.drawRect(this.innerTranslationX, 0.0f, (float) getMeasuredWidth(), (float) ((WindowInsets) ArticleViewer.this.lastInsets).getSystemWindowInsetBottom(), ArticleViewer.this.statusBarPaint);
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
        private String lastTimeString;
        private WebpageAdapter parentAdapter;
        private RadialProgress2 radialProgress;
        private SeekBar seekBar;
        private int seekBarX;
        private int seekBarY;
        private int textX;
        private int textY = AndroidUtilities.dp(54.0f);
        private StaticLayout titleLayout;

        public void onProgressUpload(String str, float f, boolean z) {
        }

        public BlockAudioCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            this.radialProgress = new RadialProgress2(this);
            this.radialProgress.setBackgroundStroke(AndroidUtilities.dp(3.0f));
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
            this.radialProgress.setProgressColor(ArticleViewer.this.getTextColor());
            this.seekBar.setColors(ArticleViewer.this.getTextColor() & NUM, ArticleViewer.this.getTextColor() & NUM, ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor());
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
            int i3;
            int size = MeasureSpec.getSize(i);
            int dp = AndroidUtilities.dp(54.0f);
            TL_pageBlockAudio tL_pageBlockAudio = this.currentBlock;
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
                this.captionLayout = articleViewer.createLayoutForText(this, null, tL_pageBlockAudio2.caption.text, dp2, tL_pageBlockAudio2, this.parentAdapter);
                if (this.captionLayout != null) {
                    this.creditOffset = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    dp += this.creditOffset + AndroidUtilities.dp(4.0f);
                }
                i3 = dp;
                articleViewer = ArticleViewer.this;
                tL_pageBlockAudio2 = this.currentBlock;
                this.creditLayout = articleViewer.createLayoutForText((View) this, null, tL_pageBlockAudio2.caption.credit, dp2, (PageBlock) tL_pageBlockAudio2, articleViewer.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Alignment.ALIGN_NORMAL, this.parentAdapter);
                if (this.creditLayout != null) {
                    i3 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                if (!this.isFirst && this.currentBlock.level <= 0) {
                    i3 += AndroidUtilities.dp(8.0f);
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
                    this.titleLayout = new StaticLayout(TextUtils.ellipsize(spannableStringBuilder, Theme.chat_audioTitlePaint, (float) dp4, TruncateAt.END), ArticleViewer.audioTimePaint, dp4, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.seekBarY = (this.buttonY + ((dp3 - AndroidUtilities.dp(30.0f)) / 2)) + AndroidUtilities.dp(11.0f);
                }
                this.seekBar.setSize(dp4, AndroidUtilities.dp(30.0f));
            } else {
                i3 = 1;
            }
            setMeasuredDimension(size, i3);
            updatePlayingMessageProgress();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                this.radialProgress.setColors(ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor());
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
                if (MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.currentMessageObject)) {
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

        public void onProgressDownload(String str, float f) {
            this.radialProgress.setProgress(f, true);
            if (this.buttonState != 3) {
                updateButtonState(true);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    private class BlockPhotoCell extends FrameLayout implements FileDownloadProgressListener {
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

        public void onProgressUpload(String str, float f, boolean z) {
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
                Photo access$12900 = ArticleViewer.this.getPhotoWithId(tL_pageBlockPhoto.photo_id);
                if (access$12900 != null) {
                    this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(access$12900.sizes, AndroidUtilities.getPhotoSize());
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
            if (ArticleViewer.this.channelBlock != null && (this.parentBlock instanceof TL_pageBlockCover)) {
                this.channelCell.setBlock(ArticleViewer.this.channelBlock);
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
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.channelBlock;
            if (r0 == 0) goto L_0x005f;
        L_0x0036:
            r12 = r12.getAction();
            if (r12 != r4) goto L_0x005f;
        L_0x003c:
            r12 = org.telegram.ui.ArticleViewer.this;
            r12 = r12.currentAccount;
            r12 = org.telegram.messenger.MessagesController.getInstance(r12);
            r0 = org.telegram.ui.ArticleViewer.this;
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
        /* JADX WARNING: Removed duplicated region for block: B:55:0x0163  */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x0160  */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x0192  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x0190  */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x0146  */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x0160  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x0163  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x0190  */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x0192  */
        /* JADX WARNING: Removed duplicated region for block: B:62:0x019f  */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x0247  */
        /* JADX WARNING: Removed duplicated region for block: B:9:0x0047  */
        @android.annotation.SuppressLint({"NewApi"})
        public void onMeasure(int r25, int r26) {
            /*
            r24 = this;
            r8 = r24;
            r0 = android.view.View.MeasureSpec.getSize(r25);
            r1 = r8.currentType;
            r9 = 2;
            r10 = 0;
            r11 = 1;
            if (r1 != r11) goto L_0x0023;
        L_0x000d:
            r0 = r24.getParent();
            r0 = (android.view.View) r0;
            r0 = r0.getMeasuredWidth();
            r1 = r24.getParent();
            r1 = (android.view.View) r1;
            r1 = r1.getMeasuredHeight();
        L_0x0021:
            r12 = r0;
            goto L_0x0043;
        L_0x0023:
            if (r1 != r9) goto L_0x0041;
        L_0x0025:
            r1 = r8.groupPosition;
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
            r12 = r0;
            r1 = 0;
        L_0x0043:
            r0 = r8.currentBlock;
            if (r0 == 0) goto L_0x02f4;
        L_0x0047:
            r2 = org.telegram.ui.ArticleViewer.this;
            r3 = r0.photo_id;
            r0 = r2.getPhotoWithId(r3);
            r8.currentPhoto = r0;
            r0 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r2 = r8.currentType;
            r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
            if (r2 != 0) goto L_0x007a;
        L_0x005d:
            r2 = r8.currentBlock;
            r2 = r2.level;
            if (r2 <= 0) goto L_0x007a;
        L_0x0063:
            r2 = r2 * 14;
            r2 = (float) r2;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r2 = r2 + r4;
            r8.textX = r2;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r3 = r3 + r2;
            r3 = r12 - r3;
            r7 = r3;
            goto L_0x008b;
        L_0x007a:
            r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r8.textX = r2;
            r2 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r2 = r12 - r2;
            r7 = r2;
            r3 = r12;
            r2 = 0;
        L_0x008b:
            r4 = r8.currentPhoto;
            r13 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            if (r4 == 0) goto L_0x0242;
        L_0x0091:
            r5 = r8.currentPhotoObject;
            if (r5 == 0) goto L_0x0242;
        L_0x0095:
            r4 = r4.sizes;
            r5 = 40;
            r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5, r11);
            r8.currentPhotoObjectThumb = r4;
            r4 = r8.currentPhotoObject;
            r5 = r8.currentPhotoObjectThumb;
            r6 = 0;
            if (r4 != r5) goto L_0x00a8;
        L_0x00a6:
            r8.currentPhotoObjectThumb = r6;
        L_0x00a8:
            r4 = r8.currentType;
            r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            if (r4 != 0) goto L_0x0107;
        L_0x00ae:
            r1 = (float) r3;
            r4 = r8.currentPhotoObject;
            r14 = r4.w;
            r14 = (float) r14;
            r1 = r1 / r14;
            r4 = r4.h;
            r4 = (float) r4;
            r1 = r1 * r4;
            r1 = (int) r1;
            r4 = r8.parentBlock;
            r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCover;
            if (r4 == 0) goto L_0x00c7;
        L_0x00c1:
            r1 = java.lang.Math.min(r1, r3);
            goto L_0x013f;
        L_0x00c7:
            r4 = org.telegram.ui.ArticleViewer.this;
            r4 = r4.listView;
            r4 = r4[r10];
            r4 = r4.getMeasuredWidth();
            r14 = org.telegram.ui.ArticleViewer.this;
            r14 = r14.listView;
            r14 = r14[r10];
            r14 = r14.getMeasuredHeight();
            r4 = java.lang.Math.max(r4, r14);
            r14 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
            r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
            r4 = r4 - r14;
            r4 = (float) r4;
            r14 = NUM; // 0x3var_ float:0.9 double:5.2552552E-315;
            r4 = r4 * r14;
            r4 = (int) r4;
            if (r1 <= r4) goto L_0x013f;
        L_0x00f3:
            r1 = (float) r4;
            r3 = r8.currentPhotoObject;
            r14 = r3.h;
            r14 = (float) r14;
            r1 = r1 / r14;
            r3 = r3.w;
            r3 = (float) r3;
            r1 = r1 * r3;
            r3 = (int) r1;
            r1 = r12 - r2;
            r1 = r1 - r3;
            r1 = r1 / r9;
            r2 = r2 + r1;
            r1 = r4;
            goto L_0x013f;
        L_0x0107:
            if (r4 != r9) goto L_0x013f;
        L_0x0109:
            r4 = r8.groupPosition;
            r4 = r4.flags;
            r4 = r4 & r9;
            if (r4 != 0) goto L_0x0115;
        L_0x0110:
            r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r3 = r3 - r4;
        L_0x0115:
            r4 = r8.groupPosition;
            r4 = r4.flags;
            r4 = r4 & 8;
            if (r4 != 0) goto L_0x0124;
        L_0x011d:
            r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r4 = r1 - r4;
            goto L_0x0125;
        L_0x0124:
            r4 = r1;
        L_0x0125:
            r14 = r8.groupPosition;
            r14 = r14.leftSpanOffset;
            if (r14 == 0) goto L_0x0139;
        L_0x012b:
            r14 = r14 * r12;
            r14 = (float) r14;
            r15 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
            r14 = r14 / r15;
            r14 = (double) r14;
            r14 = java.lang.Math.ceil(r14);
            r14 = (int) r14;
            r3 = r3 - r14;
            r2 = r2 + r14;
        L_0x0139:
            r23 = r4;
            r4 = r1;
            r1 = r23;
            goto L_0x0140;
        L_0x013f:
            r4 = r1;
        L_0x0140:
            r14 = r8.imageView;
            r15 = r8.isFirst;
            if (r15 != 0) goto L_0x0158;
        L_0x0146:
            r15 = r8.currentType;
            if (r15 == r11) goto L_0x0158;
        L_0x014a:
            if (r15 == r9) goto L_0x0158;
        L_0x014c:
            r15 = r8.currentBlock;
            r15 = r15.level;
            if (r15 <= 0) goto L_0x0153;
        L_0x0152:
            goto L_0x0158;
        L_0x0153:
            r15 = org.telegram.messenger.AndroidUtilities.dp(r13);
            goto L_0x0159;
        L_0x0158:
            r15 = 0;
        L_0x0159:
            r14.setImageCoords(r2, r15, r3, r1);
            r2 = r8.currentType;
            if (r2 != 0) goto L_0x0163;
        L_0x0160:
            r8.currentFilter = r6;
            goto L_0x017b;
        L_0x0163:
            r2 = java.util.Locale.US;
            r14 = new java.lang.Object[r9];
            r3 = java.lang.Integer.valueOf(r3);
            r14[r10] = r3;
            r1 = java.lang.Integer.valueOf(r1);
            r14[r11] = r1;
            r1 = "%d_%d";
            r1 = java.lang.String.format(r2, r1, r14);
            r8.currentFilter = r1;
        L_0x017b:
            r1 = "80_80_b";
            r8.currentThumbFilter = r1;
            r1 = org.telegram.ui.ArticleViewer.this;
            r1 = r1.currentAccount;
            r1 = org.telegram.messenger.DownloadController.getInstance(r1);
            r1 = r1.getCurrentDownloadMask();
            r1 = r1 & r11;
            if (r1 == 0) goto L_0x0192;
        L_0x0190:
            r1 = 1;
            goto L_0x0193;
        L_0x0192:
            r1 = 0;
        L_0x0193:
            r8.autoDownload = r1;
            r1 = r8.currentPhotoObject;
            r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r11);
            r2 = r8.autoDownload;
            if (r2 != 0) goto L_0x01da;
        L_0x019f:
            r1 = r1.exists();
            if (r1 == 0) goto L_0x01a6;
        L_0x01a5:
            goto L_0x01da;
        L_0x01a6:
            r1 = r8.imageView;
            r2 = r8.currentPhotoObject;
            r3 = r8.currentPhoto;
            r2 = org.telegram.messenger.ImageLocation.getForPhoto(r2, r3);
            r1.setStrippedLocation(r2);
            r14 = r8.imageView;
            r15 = 0;
            r1 = r8.currentFilter;
            r2 = r8.currentPhotoObjectThumb;
            r3 = r8.currentPhoto;
            r17 = org.telegram.messenger.ImageLocation.getForPhoto(r2, r3);
            r2 = r8.currentThumbFilter;
            r3 = r8.currentPhotoObject;
            r3 = r3.size;
            r20 = 0;
            r6 = org.telegram.ui.ArticleViewer.this;
            r21 = r6.currentPage;
            r22 = 1;
            r16 = r1;
            r18 = r2;
            r19 = r3;
            r14.setImage(r15, r16, r17, r18, r19, r20, r21, r22);
            goto L_0x020c;
        L_0x01da:
            r1 = r8.imageView;
            r1.setStrippedLocation(r6);
            r14 = r8.imageView;
            r1 = r8.currentPhotoObject;
            r2 = r8.currentPhoto;
            r15 = org.telegram.messenger.ImageLocation.getForPhoto(r1, r2);
            r1 = r8.currentFilter;
            r2 = r8.currentPhotoObjectThumb;
            r3 = r8.currentPhoto;
            r17 = org.telegram.messenger.ImageLocation.getForPhoto(r2, r3);
            r2 = r8.currentThumbFilter;
            r3 = r8.currentPhotoObject;
            r3 = r3.size;
            r20 = 0;
            r6 = org.telegram.ui.ArticleViewer.this;
            r21 = r6.currentPage;
            r22 = 1;
            r16 = r1;
            r18 = r2;
            r19 = r3;
            r14.setImage(r15, r16, r17, r18, r19, r20, r21, r22);
        L_0x020c:
            r1 = r8.imageView;
            r1 = r1.getImageX();
            r1 = (float) r1;
            r2 = r8.imageView;
            r2 = r2.getImageWidth();
            r2 = r2 - r0;
            r2 = (float) r2;
            r2 = r2 / r5;
            r1 = r1 + r2;
            r1 = (int) r1;
            r8.buttonX = r1;
            r1 = r8.imageView;
            r1 = r1.getImageY();
            r1 = (float) r1;
            r2 = r8.imageView;
            r2 = r2.getImageHeight();
            r2 = r2 - r0;
            r2 = (float) r2;
            r2 = r2 / r5;
            r1 = r1 + r2;
            r1 = (int) r1;
            r8.buttonY = r1;
            r1 = r8.radialProgress;
            r2 = r8.buttonX;
            r3 = r8.buttonY;
            r5 = r2 + r0;
            r0 = r0 + r3;
            r1.setProgressRect(r2, r3, r5, r0);
            r14 = r4;
            goto L_0x0243;
        L_0x0242:
            r14 = r1;
        L_0x0243:
            r0 = r8.currentType;
            if (r0 != 0) goto L_0x02ac;
        L_0x0247:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r5 = r8.currentBlock;
            r1 = r5.caption;
            r3 = r1.text;
            r6 = r8.parentAdapter;
            r1 = r24;
            r4 = r7;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6);
            r8.captionLayout = r0;
            r0 = r8.captionLayout;
            r15 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
            if (r0 == 0) goto L_0x0276;
        L_0x0261:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
            r1 = r8.captionLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r8.creditOffset = r0;
            r0 = r8.creditOffset;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r15);
            r0 = r0 + r1;
            r14 = r14 + r0;
        L_0x0276:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r5 = r8.currentBlock;
            r1 = r5.caption;
            r3 = r1.credit;
            r1 = r0.isRtl;
            if (r1 == 0) goto L_0x028a;
        L_0x0285:
            r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT();
            goto L_0x028c;
        L_0x028a:
            r1 = android.text.Layout.Alignment.ALIGN_NORMAL;
        L_0x028c:
            r6 = r1;
            r4 = r8.parentAdapter;
            r1 = r24;
            r16 = r4;
            r4 = r7;
            r7 = r16;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7);
            r8.creditLayout = r0;
            r0 = r8.creditLayout;
            if (r0 == 0) goto L_0x02ac;
        L_0x02a0:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r15);
            r1 = r8.creditLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r14 = r14 + r0;
        L_0x02ac:
            r0 = r8.isFirst;
            if (r0 != 0) goto L_0x02bf;
        L_0x02b0:
            r0 = r8.currentType;
            if (r0 != 0) goto L_0x02bf;
        L_0x02b4:
            r0 = r8.currentBlock;
            r0 = r0.level;
            if (r0 > 0) goto L_0x02bf;
        L_0x02ba:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
            r14 = r14 + r0;
        L_0x02bf:
            r0 = r8.parentBlock;
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCover;
            if (r0 == 0) goto L_0x02e8;
        L_0x02c5:
            r0 = r8.parentAdapter;
            r0 = r0.blocks;
            if (r0 == 0) goto L_0x02e8;
        L_0x02cd:
            r0 = r8.parentAdapter;
            r0 = r0.blocks;
            r0 = r0.size();
            if (r0 <= r11) goto L_0x02e8;
        L_0x02d9:
            r0 = r8.parentAdapter;
            r0 = r0.blocks;
            r0 = r0.get(r11);
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockChannel;
            if (r0 == 0) goto L_0x02e8;
        L_0x02e7:
            r10 = 1;
        L_0x02e8:
            r0 = r8.currentType;
            if (r0 == r9) goto L_0x02f3;
        L_0x02ec:
            if (r10 != 0) goto L_0x02f3;
        L_0x02ee:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
            r14 = r14 + r0;
        L_0x02f3:
            r11 = r14;
        L_0x02f4:
            r0 = r8.channelCell;
            r1 = r25;
            r2 = r26;
            r0.measure(r1, r2);
            r0 = r8.channelCell;
            r1 = r8.imageView;
            r1 = r1.getImageHeight();
            r2 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r1 = r1 - r2;
            r1 = (float) r1;
            r0.setTranslationY(r1);
            r8.setMeasuredDimension(r12, r11);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$BlockPhotoCell.onMeasure(int, int):void");
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (!(this.imageView.hasBitmapImage() && this.imageView.getCurrentAlpha() == 1.0f)) {
                    canvas.drawRect((float) this.imageView.getImageX(), (float) this.imageView.getImageY(), (float) this.imageView.getImageX2(), (float) this.imageView.getImageY2(), ArticleViewer.photoBackgroundPaint);
                }
                this.imageView.draw(canvas);
                if (this.imageView.getVisible()) {
                    this.radialProgress.draw(canvas);
                }
                if (!TextUtils.isEmpty(this.currentBlock.url)) {
                    int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(35.0f);
                    int imageY = this.imageView.getImageY() + AndroidUtilities.dp(11.0f);
                    this.linkDrawable.setBounds(measuredWidth, imageY, AndroidUtilities.dp(24.0f) + measuredWidth, AndroidUtilities.dp(24.0f) + imageY);
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

        public void onProgressDownload(String str, float f) {
            this.radialProgress.setProgress(f, true);
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
    }

    private class BlockTableCell extends FrameLayout implements TableLayoutDelegate {
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
                    if (BlockTableCell.this.tableLayout.getMeasuredWidth() > getMeasuredWidth() - AndroidUtilities.dp(36.0f)) {
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
            this.tableLayout = new TableLayout(context, this);
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
            return ArticleViewer.this.createLayoutForText(this, null, tL_pageTableCell.text, i, 0, this.currentBlock, alignment, 0, this.parentAdapter);
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

        public void setBlock(TL_pageBlockTable tL_pageBlockTable) {
            int i;
            int size;
            int i2;
            this.currentBlock = tL_pageBlockTable;
            int access$13200 = ArticleViewer.this.getSelectedColor();
            if (access$13200 == 0) {
                AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, -657673);
            } else if (access$13200 == 1) {
                AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, -659492);
            } else if (access$13200 == 2) {
                AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, -15461356);
            }
            this.tableLayout.removeAllChildrens();
            this.tableLayout.setDrawLines(this.currentBlock.bordered);
            this.tableLayout.setStriped(this.currentBlock.striped);
            this.tableLayout.setRtl(ArticleViewer.this.isRtl);
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
            access$13200 = this.currentBlock.rows.size();
            for (size = 0; size < access$13200; size++) {
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
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            HorizontalScrollView horizontalScrollView = this.scrollView;
            i = this.listX;
            horizontalScrollView.layout(i, this.listY, horizontalScrollView.getMeasuredWidth() + i, this.listY + this.scrollView.getMeasuredHeight());
            if (this.firstLayout) {
                if (ArticleViewer.this.isRtl) {
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

    private class BlockVideoCell extends FrameLayout implements FileDownloadProgressListener {
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

        public void onProgressUpload(String str, float f, boolean z) {
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

        public void setParentBlock(PageBlock pageBlock) {
            this.parentBlock = pageBlock;
            if (ArticleViewer.this.channelBlock != null && (this.parentBlock instanceof TL_pageBlockCover)) {
                this.channelCell.setBlock(ArticleViewer.this.channelBlock);
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
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.channelBlock;
            if (r0 == 0) goto L_0x005f;
        L_0x0036:
            r12 = r12.getAction();
            if (r12 != r4) goto L_0x005f;
        L_0x003c:
            r12 = org.telegram.ui.ArticleViewer.this;
            r12 = r12.currentAccount;
            r12 = org.telegram.messenger.MessagesController.getInstance(r12);
            r0 = org.telegram.ui.ArticleViewer.this;
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
        /* JADX WARNING: Removed duplicated region for block: B:71:0x0204  */
        /* JADX WARNING: Removed duplicated region for block: B:65:0x0175  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x0157  */
        /* JADX WARNING: Removed duplicated region for block: B:65:0x0175  */
        /* JADX WARNING: Removed duplicated region for block: B:71:0x0204  */
        /* JADX WARNING: Removed duplicated region for block: B:73:0x0264  */
        /* JADX WARNING: Removed duplicated region for block: B:17:0x007d  */
        /* JADX WARNING: Removed duplicated region for block: B:76:0x0269  */
        /* JADX WARNING: Removed duplicated region for block: B:95:0x02e6  */
        /* JADX WARNING: Removed duplicated region for block: B:105:0x0312  */
        /* JADX WARNING: Removed duplicated region for block: B:9:0x0047  */
        /* JADX WARNING: Missing block: B:98:0x02ff, code skipped:
            if ((org.telegram.ui.ArticleViewer.WebpageAdapter.access$6900(r8.parentAdapter).get(1) instanceof org.telegram.tgnet.TLRPC.TL_pageBlockChannel) != false) goto L_0x0303;
     */
        @android.annotation.SuppressLint({"NewApi"})
        public void onMeasure(int r30, int r31) {
            /*
            r29 = this;
            r8 = r29;
            r0 = android.view.View.MeasureSpec.getSize(r30);
            r1 = r8.currentType;
            r9 = 2;
            r10 = 0;
            r11 = 1;
            if (r1 != r11) goto L_0x0023;
        L_0x000d:
            r0 = r29.getParent();
            r0 = (android.view.View) r0;
            r0 = r0.getMeasuredWidth();
            r1 = r29.getParent();
            r1 = (android.view.View) r1;
            r1 = r1.getMeasuredHeight();
        L_0x0021:
            r12 = r0;
            goto L_0x0043;
        L_0x0023:
            if (r1 != r9) goto L_0x0041;
        L_0x0025:
            r1 = r8.groupPosition;
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
            r12 = r0;
            r1 = 0;
        L_0x0043:
            r0 = r8.currentBlock;
            if (r0 == 0) goto L_0x0312;
        L_0x0047:
            r2 = r8.currentType;
            r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
            if (r2 != 0) goto L_0x0068;
        L_0x004d:
            r0 = r0.level;
            if (r0 <= 0) goto L_0x0068;
        L_0x0051:
            r0 = r0 * 14;
            r0 = (float) r0;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r0 = r0 + r2;
            r8.textX = r0;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r2 = r2 + r0;
            r2 = r12 - r2;
            r7 = r2;
            goto L_0x0079;
        L_0x0068:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r8.textX = r0;
            r0 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r0 = r12 - r0;
            r7 = r0;
            r2 = r12;
            r0 = 0;
        L_0x0079:
            r3 = r8.currentDocument;
            if (r3 == 0) goto L_0x0264;
        L_0x007d:
            r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r4 = r8.currentDocument;
            r4 = r4.thumbs;
            r5 = 48;
            r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5);
            r5 = r8.currentType;
            r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            if (r5 != 0) goto L_0x0127;
        L_0x0093:
            r5 = r8.currentDocument;
            r5 = r5.attributes;
            r5 = r5.size();
            r14 = 0;
        L_0x009c:
            if (r14 >= r5) goto L_0x00bd;
        L_0x009e:
            r15 = r8.currentDocument;
            r15 = r15.attributes;
            r15 = r15.get(r14);
            r15 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r15;
            r13 = r15 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
            if (r13 == 0) goto L_0x00ba;
        L_0x00ac:
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
            goto L_0x00bf;
        L_0x00ba:
            r14 = r14 + 1;
            goto L_0x009c;
        L_0x00bd:
            r5 = r1;
            r1 = 0;
        L_0x00bf:
            r13 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            if (r4 == 0) goto L_0x00c7;
        L_0x00c3:
            r14 = r4.w;
            r14 = (float) r14;
            goto L_0x00c9;
        L_0x00c7:
            r14 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        L_0x00c9:
            if (r4 == 0) goto L_0x00cf;
        L_0x00cb:
            r15 = r4.h;
            r15 = (float) r15;
            goto L_0x00d1;
        L_0x00cf:
            r15 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        L_0x00d1:
            if (r1 != 0) goto L_0x00d8;
        L_0x00d3:
            r1 = (float) r2;
            r1 = r1 / r14;
            r1 = r1 * r15;
            r5 = (int) r1;
        L_0x00d8:
            r1 = r8.parentBlock;
            r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCover;
            if (r1 == 0) goto L_0x00e3;
        L_0x00de:
            r5 = java.lang.Math.min(r5, r2);
            goto L_0x011a;
        L_0x00e3:
            r1 = org.telegram.ui.ArticleViewer.this;
            r1 = r1.listView;
            r1 = r1[r10];
            r1 = r1.getMeasuredWidth();
            r11 = org.telegram.ui.ArticleViewer.this;
            r11 = r11.listView;
            r11 = r11[r10];
            r11 = r11.getMeasuredHeight();
            r1 = java.lang.Math.max(r1, r11);
            r11 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
            r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
            r1 = r1 - r11;
            r1 = (float) r1;
            r11 = NUM; // 0x3var_ float:0.9 double:5.2552552E-315;
            r1 = r1 * r11;
            r1 = (int) r1;
            if (r5 <= r1) goto L_0x011a;
        L_0x010f:
            r2 = (float) r1;
            r2 = r2 / r15;
            r2 = r2 * r14;
            r2 = (int) r2;
            r5 = r12 - r0;
            r5 = r5 - r2;
            r5 = r5 / r9;
            r0 = r0 + r5;
            r5 = r1;
        L_0x011a:
            if (r5 != 0) goto L_0x0121;
        L_0x011c:
            r1 = org.telegram.messenger.AndroidUtilities.dp(r13);
            goto L_0x0149;
        L_0x0121:
            if (r5 >= r3) goto L_0x0125;
        L_0x0123:
            r1 = r3;
            goto L_0x0149;
        L_0x0125:
            r1 = r5;
            goto L_0x0149;
        L_0x0127:
            if (r5 != r9) goto L_0x0149;
        L_0x0129:
            r5 = r8.groupPosition;
            r5 = r5.flags;
            r5 = r5 & r9;
            if (r5 != 0) goto L_0x0135;
        L_0x0130:
            r5 = org.telegram.messenger.AndroidUtilities.dp(r6);
            r2 = r2 - r5;
        L_0x0135:
            r5 = r8.groupPosition;
            r5 = r5.flags;
            r5 = r5 & 8;
            if (r5 != 0) goto L_0x0149;
        L_0x013d:
            r5 = org.telegram.messenger.AndroidUtilities.dp(r6);
            r5 = r1 - r5;
            r28 = r5;
            r5 = r1;
            r1 = r28;
            goto L_0x014a;
        L_0x0149:
            r5 = r1;
        L_0x014a:
            r11 = r8.imageView;
            r13 = r8.currentDocument;
            r11.setQualityThumbDocument(r13);
            r11 = r8.imageView;
            r13 = r8.isFirst;
            if (r13 != 0) goto L_0x016c;
        L_0x0157:
            r13 = r8.currentType;
            r14 = 1;
            if (r13 == r14) goto L_0x016c;
        L_0x015c:
            if (r13 == r9) goto L_0x016c;
        L_0x015e:
            r13 = r8.currentBlock;
            r13 = r13.level;
            if (r13 <= 0) goto L_0x0165;
        L_0x0164:
            goto L_0x016c;
        L_0x0165:
            r13 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            r14 = org.telegram.messenger.AndroidUtilities.dp(r13);
            goto L_0x016d;
        L_0x016c:
            r14 = 0;
        L_0x016d:
            r11.setImageCoords(r0, r14, r2, r1);
            r0 = r8.isGif;
            r1 = 0;
            if (r0 == 0) goto L_0x0204;
        L_0x0175:
            r0 = org.telegram.ui.ArticleViewer.this;
            r0 = r0.currentAccount;
            r0 = org.telegram.messenger.DownloadController.getInstance(r0);
            r2 = 4;
            r11 = r8.currentDocument;
            r11 = r11.size;
            r0 = r0.canDownloadMedia(r2, r11);
            r8.autoDownload = r0;
            r0 = r8.currentDocument;
            r2 = 1;
            r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r2);
            r2 = r8.autoDownload;
            if (r2 != 0) goto L_0x01d1;
        L_0x0195:
            r0 = r0.exists();
            if (r0 == 0) goto L_0x019c;
        L_0x019b:
            goto L_0x01d1;
        L_0x019c:
            r0 = r8.imageView;
            r1 = r8.currentDocument;
            r1 = org.telegram.messenger.ImageLocation.getForDocument(r1);
            r0.setStrippedLocation(r1);
            r0 = r8.imageView;
            r17 = 0;
            r18 = 0;
            r19 = 0;
            r20 = 0;
            r1 = r8.currentDocument;
            r21 = org.telegram.messenger.ImageLocation.getForDocument(r4, r1);
            r23 = 0;
            r1 = r8.currentDocument;
            r1 = r1.size;
            r25 = 0;
            r2 = org.telegram.ui.ArticleViewer.this;
            r26 = r2.currentPage;
            r27 = 1;
            r22 = "80_80_b";
            r16 = r0;
            r24 = r1;
            r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27);
            goto L_0x0228;
        L_0x01d1:
            r0 = r8.imageView;
            r0.setStrippedLocation(r1);
            r0 = r8.imageView;
            r1 = r8.currentDocument;
            r17 = org.telegram.messenger.ImageLocation.getForDocument(r1);
            r18 = 0;
            r19 = 0;
            r20 = 0;
            r1 = r8.currentDocument;
            r21 = org.telegram.messenger.ImageLocation.getForDocument(r4, r1);
            r23 = 0;
            r1 = r8.currentDocument;
            r1 = r1.size;
            r25 = 0;
            r2 = org.telegram.ui.ArticleViewer.this;
            r26 = r2.currentPage;
            r27 = 1;
            r22 = "80_80_b";
            r16 = r0;
            r24 = r1;
            r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27);
            goto L_0x0228;
        L_0x0204:
            r0 = r8.imageView;
            r0.setStrippedLocation(r1);
            r0 = r8.imageView;
            r17 = 0;
            r18 = 0;
            r1 = r8.currentDocument;
            r19 = org.telegram.messenger.ImageLocation.getForDocument(r4, r1);
            r21 = 0;
            r22 = 0;
            r1 = org.telegram.ui.ArticleViewer.this;
            r23 = r1.currentPage;
            r24 = 1;
            r20 = "80_80_b";
            r16 = r0;
            r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24);
        L_0x0228:
            r0 = r8.imageView;
            r1 = 1;
            r0.setAspectFit(r1);
            r0 = r8.imageView;
            r0 = r0.getImageX();
            r0 = (float) r0;
            r1 = r8.imageView;
            r1 = r1.getImageWidth();
            r1 = r1 - r3;
            r1 = (float) r1;
            r1 = r1 / r6;
            r0 = r0 + r1;
            r0 = (int) r0;
            r8.buttonX = r0;
            r0 = r8.imageView;
            r0 = r0.getImageY();
            r0 = (float) r0;
            r1 = r8.imageView;
            r1 = r1.getImageHeight();
            r1 = r1 - r3;
            r1 = (float) r1;
            r1 = r1 / r6;
            r0 = r0 + r1;
            r0 = (int) r0;
            r8.buttonY = r0;
            r0 = r8.radialProgress;
            r1 = r8.buttonX;
            r2 = r8.buttonY;
            r4 = r1 + r3;
            r3 = r3 + r2;
            r0.setProgressRect(r1, r2, r4, r3);
            r11 = r5;
            goto L_0x0265;
        L_0x0264:
            r11 = r1;
        L_0x0265:
            r0 = r8.currentType;
            if (r0 != 0) goto L_0x02cb;
        L_0x0269:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r5 = r8.currentBlock;
            r1 = r5.caption;
            r3 = r1.text;
            r6 = r8.parentAdapter;
            r1 = r29;
            r4 = r7;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6);
            r8.captionLayout = r0;
            r0 = r8.captionLayout;
            r13 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
            if (r0 == 0) goto L_0x0298;
        L_0x0283:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
            r1 = r8.captionLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r8.creditOffset = r0;
            r0 = r8.creditOffset;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r13);
            r0 = r0 + r1;
            r11 = r11 + r0;
        L_0x0298:
            r0 = org.telegram.ui.ArticleViewer.this;
            r2 = 0;
            r5 = r8.currentBlock;
            r1 = r5.caption;
            r3 = r1.credit;
            r1 = r0.isRtl;
            if (r1 == 0) goto L_0x02ac;
        L_0x02a7:
            r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT();
            goto L_0x02ae;
        L_0x02ac:
            r1 = android.text.Layout.Alignment.ALIGN_NORMAL;
        L_0x02ae:
            r6 = r1;
            r14 = r8.parentAdapter;
            r1 = r29;
            r4 = r7;
            r7 = r14;
            r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7);
            r8.creditLayout = r0;
            r0 = r8.creditLayout;
            if (r0 == 0) goto L_0x02cb;
        L_0x02bf:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
            r1 = r8.creditLayout;
            r1 = r1.getHeight();
            r0 = r0 + r1;
            r11 = r11 + r0;
        L_0x02cb:
            r0 = r8.isFirst;
            if (r0 != 0) goto L_0x02e0;
        L_0x02cf:
            r0 = r8.currentType;
            if (r0 != 0) goto L_0x02e0;
        L_0x02d3:
            r0 = r8.currentBlock;
            r0 = r0.level;
            if (r0 > 0) goto L_0x02e0;
        L_0x02d9:
            r0 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r11 = r11 + r1;
        L_0x02e0:
            r0 = r8.parentBlock;
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCover;
            if (r0 == 0) goto L_0x0302;
        L_0x02e6:
            r0 = r8.parentAdapter;
            r0 = r0.blocks;
            r0 = r0.size();
            r14 = 1;
            if (r0 <= r14) goto L_0x0302;
        L_0x02f3:
            r0 = r8.parentAdapter;
            r0 = r0.blocks;
            r0 = r0.get(r14);
            r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockChannel;
            if (r0 == 0) goto L_0x0302;
        L_0x0301:
            goto L_0x0303;
        L_0x0302:
            r14 = 0;
        L_0x0303:
            r0 = r8.currentType;
            if (r0 == r9) goto L_0x0310;
        L_0x0307:
            if (r14 != 0) goto L_0x0310;
        L_0x0309:
            r0 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r11 = r11 + r0;
        L_0x0310:
            r14 = r11;
            goto L_0x0313;
        L_0x0312:
            r14 = 1;
        L_0x0313:
            r0 = r8.channelCell;
            r1 = r30;
            r2 = r31;
            r0.measure(r1, r2);
            r0 = r8.channelCell;
            r1 = r8.imageView;
            r1 = r1.getImageHeight();
            r2 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r1 = r1 - r2;
            r1 = (float) r1;
            r0.setTranslationY(r1);
            r8.setMeasuredDimension(r12, r14);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$BlockVideoCell.onMeasure(int, int):void");
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
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

        public void onProgressDownload(String str, float f) {
            this.radialProgress.setProgress(f, true);
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
        private Context context;
        private ArrayList<PageBlock> localBlocks = new ArrayList();
        private ArrayList<PageBlock> photoBlocks = new ArrayList();

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

        private void setRichTextParents(PageBlock pageBlock) {
            if (pageBlock instanceof TL_pageBlockEmbedPost) {
                TL_pageBlockEmbedPost tL_pageBlockEmbedPost = (TL_pageBlockEmbedPost) pageBlock;
                setRichTextParents(null, tL_pageBlockEmbedPost.caption.text);
                setRichTextParents(null, tL_pageBlockEmbedPost.caption.credit);
            } else if (pageBlock instanceof TL_pageBlockParagraph) {
                setRichTextParents(null, ((TL_pageBlockParagraph) pageBlock).text);
            } else if (pageBlock instanceof TL_pageBlockKicker) {
                setRichTextParents(null, ((TL_pageBlockKicker) pageBlock).text);
            } else if (pageBlock instanceof TL_pageBlockFooter) {
                setRichTextParents(null, ((TL_pageBlockFooter) pageBlock).text);
            } else if (pageBlock instanceof TL_pageBlockHeader) {
                setRichTextParents(null, ((TL_pageBlockHeader) pageBlock).text);
            } else if (pageBlock instanceof TL_pageBlockPreformatted) {
                setRichTextParents(null, ((TL_pageBlockPreformatted) pageBlock).text);
            } else if (pageBlock instanceof TL_pageBlockSubheader) {
                setRichTextParents(null, ((TL_pageBlockSubheader) pageBlock).text);
            } else {
                int i = 0;
                int size;
                if (pageBlock instanceof TL_pageBlockSlideshow) {
                    TL_pageBlockSlideshow tL_pageBlockSlideshow = (TL_pageBlockSlideshow) pageBlock;
                    setRichTextParents(null, tL_pageBlockSlideshow.caption.text);
                    setRichTextParents(null, tL_pageBlockSlideshow.caption.credit);
                    size = tL_pageBlockSlideshow.items.size();
                    while (i < size) {
                        setRichTextParents((PageBlock) tL_pageBlockSlideshow.items.get(i));
                        i++;
                    }
                } else if (pageBlock instanceof TL_pageBlockPhoto) {
                    TL_pageBlockPhoto tL_pageBlockPhoto = (TL_pageBlockPhoto) pageBlock;
                    setRichTextParents(null, tL_pageBlockPhoto.caption.text);
                    setRichTextParents(null, tL_pageBlockPhoto.caption.credit);
                } else if (pageBlock instanceof TL_pageBlockListItem) {
                    TL_pageBlockListItem tL_pageBlockListItem = (TL_pageBlockListItem) pageBlock;
                    if (tL_pageBlockListItem.textItem != null) {
                        setRichTextParents(null, tL_pageBlockListItem.textItem);
                    } else if (tL_pageBlockListItem.blockItem != null) {
                        setRichTextParents(tL_pageBlockListItem.blockItem);
                    }
                } else if (pageBlock instanceof TL_pageBlockOrderedListItem) {
                    TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem = (TL_pageBlockOrderedListItem) pageBlock;
                    if (tL_pageBlockOrderedListItem.textItem != null) {
                        setRichTextParents(null, tL_pageBlockOrderedListItem.textItem);
                    } else if (tL_pageBlockOrderedListItem.blockItem != null) {
                        setRichTextParents(tL_pageBlockOrderedListItem.blockItem);
                    }
                } else if (pageBlock instanceof TL_pageBlockCollage) {
                    TL_pageBlockCollage tL_pageBlockCollage = (TL_pageBlockCollage) pageBlock;
                    setRichTextParents(null, tL_pageBlockCollage.caption.text);
                    setRichTextParents(null, tL_pageBlockCollage.caption.credit);
                    size = tL_pageBlockCollage.items.size();
                    while (i < size) {
                        setRichTextParents((PageBlock) tL_pageBlockCollage.items.get(i));
                        i++;
                    }
                } else if (pageBlock instanceof TL_pageBlockEmbed) {
                    TL_pageBlockEmbed tL_pageBlockEmbed = (TL_pageBlockEmbed) pageBlock;
                    setRichTextParents(null, tL_pageBlockEmbed.caption.text);
                    setRichTextParents(null, tL_pageBlockEmbed.caption.credit);
                } else if (pageBlock instanceof TL_pageBlockSubtitle) {
                    setRichTextParents(null, ((TL_pageBlockSubtitle) pageBlock).text);
                } else if (pageBlock instanceof TL_pageBlockBlockquote) {
                    TL_pageBlockBlockquote tL_pageBlockBlockquote = (TL_pageBlockBlockquote) pageBlock;
                    setRichTextParents(null, tL_pageBlockBlockquote.text);
                    setRichTextParents(null, tL_pageBlockBlockquote.caption);
                } else if (pageBlock instanceof TL_pageBlockDetails) {
                    TL_pageBlockDetails tL_pageBlockDetails = (TL_pageBlockDetails) pageBlock;
                    setRichTextParents(null, tL_pageBlockDetails.title);
                    size = tL_pageBlockDetails.blocks.size();
                    while (i < size) {
                        setRichTextParents((PageBlock) tL_pageBlockDetails.blocks.get(i));
                        i++;
                    }
                } else if (pageBlock instanceof TL_pageBlockVideo) {
                    TL_pageBlockVideo tL_pageBlockVideo = (TL_pageBlockVideo) pageBlock;
                    setRichTextParents(null, tL_pageBlockVideo.caption.text);
                    setRichTextParents(null, tL_pageBlockVideo.caption.credit);
                } else if (pageBlock instanceof TL_pageBlockPullquote) {
                    TL_pageBlockPullquote tL_pageBlockPullquote = (TL_pageBlockPullquote) pageBlock;
                    setRichTextParents(null, tL_pageBlockPullquote.text);
                    setRichTextParents(null, tL_pageBlockPullquote.caption);
                } else if (pageBlock instanceof TL_pageBlockAudio) {
                    TL_pageBlockAudio tL_pageBlockAudio = (TL_pageBlockAudio) pageBlock;
                    setRichTextParents(null, tL_pageBlockAudio.caption.text);
                    setRichTextParents(null, tL_pageBlockAudio.caption.credit);
                } else if (pageBlock instanceof TL_pageBlockTable) {
                    TL_pageBlockTable tL_pageBlockTable = (TL_pageBlockTable) pageBlock;
                    setRichTextParents(null, tL_pageBlockTable.title);
                    size = tL_pageBlockTable.rows.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        TL_pageTableRow tL_pageTableRow = (TL_pageTableRow) tL_pageBlockTable.rows.get(i2);
                        int size2 = tL_pageTableRow.cells.size();
                        for (int i3 = 0; i3 < size2; i3++) {
                            setRichTextParents(null, ((TL_pageTableCell) tL_pageTableRow.cells.get(i3)).text);
                        }
                    }
                } else if (pageBlock instanceof TL_pageBlockTitle) {
                    setRichTextParents(null, ((TL_pageBlockTitle) pageBlock).text);
                } else if (pageBlock instanceof TL_pageBlockCover) {
                    setRichTextParents(((TL_pageBlockCover) pageBlock).cover);
                } else if (pageBlock instanceof TL_pageBlockAuthorDate) {
                    setRichTextParents(null, ((TL_pageBlockAuthorDate) pageBlock).author);
                } else if (pageBlock instanceof TL_pageBlockMap) {
                    TL_pageBlockMap tL_pageBlockMap = (TL_pageBlockMap) pageBlock;
                    setRichTextParents(null, tL_pageBlockMap.caption.text);
                    setRichTextParents(null, tL_pageBlockMap.caption.credit);
                } else if (pageBlock instanceof TL_pageBlockRelatedArticles) {
                    setRichTextParents(null, ((TL_pageBlockRelatedArticles) pageBlock).title);
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:129:0x04b4  */
        /* JADX WARNING: Removed duplicated region for block: B:128:0x0497  */
        /* JADX WARNING: Removed duplicated region for block: B:161:0x051b A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:135:0x04c7  */
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
            if (r8 == 0) goto L_0x00dd;
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
            r5 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            r3 = r3 / r5;
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
            r5 = r0.audio_id;
            r4 = r4.getDocumentWithId(r5);
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
            goto L_0x052e;
        L_0x00dd:
            r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost;
            r11 = 0;
            if (r8 == 0) goto L_0x016a;
        L_0x00e2:
            r0 = r6;
            r0 = (org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost) r0;
            r2 = r0.blocks;
            r2 = r2.isEmpty();
            if (r2 != 0) goto L_0x052e;
        L_0x00ed:
            r2 = -1;
            r6.level = r2;
        L_0x00f0:
            r2 = r0.blocks;
            r2 = r2.size();
            if (r9 >= r2) goto L_0x0139;
        L_0x00f8:
            r2 = r0.blocks;
            r2 = r2.get(r9);
            r2 = (org.telegram.tgnet.TLRPC.PageBlock) r2;
            r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockUnsupported;
            if (r3 == 0) goto L_0x0105;
        L_0x0104:
            goto L_0x0136;
        L_0x0105:
            r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockAnchor;
            if (r3 == 0) goto L_0x0121;
        L_0x0109:
            r2 = (org.telegram.tgnet.TLRPC.TL_pageBlockAnchor) r2;
            r3 = r1.anchors;
            r2 = r2.name;
            r2 = r2.toLowerCase();
            r4 = r1.blocks;
            r4 = r4.size();
            r4 = java.lang.Integer.valueOf(r4);
            r3.put(r2, r4);
            goto L_0x0136;
        L_0x0121:
            r2.level = r10;
            r3 = r0.blocks;
            r3 = r3.size();
            r3 = r3 - r10;
            if (r9 != r3) goto L_0x012e;
        L_0x012c:
            r2.bottom = r10;
        L_0x012e:
            r3 = r1.blocks;
            r3.add(r2);
            r1.addAllMediaFromBlock(r2);
        L_0x0136:
            r9 = r9 + 1;
            goto L_0x00f0;
        L_0x0139:
            r2 = r0.caption;
            r2 = r2.text;
            r2 = org.telegram.ui.ArticleViewer.getPlainText(r2);
            r2 = android.text.TextUtils.isEmpty(r2);
            if (r2 == 0) goto L_0x0155;
        L_0x0147:
            r2 = r0.caption;
            r2 = r2.credit;
            r2 = org.telegram.ui.ArticleViewer.getPlainText(r2);
            r2 = android.text.TextUtils.isEmpty(r2);
            if (r2 != 0) goto L_0x052e;
        L_0x0155:
            r2 = new org.telegram.ui.ArticleViewer$TL_pageBlockEmbedPostCaption;
            r3 = org.telegram.ui.ArticleViewer.this;
            r2.<init>(r3, r11);
            r2.parent = r0;
            r0 = r0.caption;
            r2.caption = r0;
            r0 = r1.blocks;
            r0.add(r2);
            goto L_0x052e;
        L_0x016a:
            r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockRelatedArticles;
            if (r8 == 0) goto L_0x01b4;
        L_0x016e:
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
        L_0x018a:
            if (r9 >= r0) goto L_0x01a1;
        L_0x018c:
            r2 = new org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesChild;
            r3 = org.telegram.ui.ArticleViewer.this;
            r2.<init>(r3, r11);
            r2.parent = r6;
            r2.num = r9;
            r3 = r1.blocks;
            r3.add(r2);
            r9 = r9 + 1;
            goto L_0x018a;
        L_0x01a1:
            if (r4 != 0) goto L_0x052e;
        L_0x01a3:
            r0 = new org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesShadow;
            r2 = org.telegram.ui.ArticleViewer.this;
            r0.<init>(r2, r11);
            r0.parent = r6;
            r2 = r1.blocks;
            r2.add(r0);
            goto L_0x052e;
        L_0x01b4:
            r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockDetails;
            if (r8 == 0) goto L_0x01e5;
        L_0x01b8:
            r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockDetails) r6;
            r5 = r6.blocks;
            r5 = r5.size();
        L_0x01c0:
            if (r9 >= r5) goto L_0x052e;
        L_0x01c2:
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
            goto L_0x01c0;
        L_0x01e5:
            r8 = " ";
            r12 = ".%d";
            r13 = "%d.";
            if (r7 == 0) goto L_0x0331;
        L_0x01ed:
            r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockList) r6;
            r7 = new org.telegram.ui.ArticleViewer$TL_pageBlockListParent;
            r14 = org.telegram.ui.ArticleViewer.this;
            r7.<init>(r14, r11);
            r7.pageBlockList = r6;
            r7.level = r3;
            r14 = r6.items;
            r14 = r14.size();
            r15 = 0;
        L_0x0203:
            if (r15 >= r14) goto L_0x052e;
        L_0x0205:
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
            if (r14 == 0) goto L_0x0252;
        L_0x0220:
            r14 = org.telegram.ui.ArticleViewer.this;
            r14 = r14.isRtl;
            if (r14 == 0) goto L_0x023d;
        L_0x0228:
            r14 = 1;
            r11 = new java.lang.Object[r14];
            r17 = r15 + 1;
            r17 = java.lang.Integer.valueOf(r17);
            r16 = 0;
            r11[r16] = r17;
            r11 = java.lang.String.format(r12, r11);
            r10.num = r11;
            goto L_0x0258;
        L_0x023d:
            r14 = 1;
            r16 = 0;
            r11 = new java.lang.Object[r14];
            r14 = r15 + 1;
            r14 = java.lang.Integer.valueOf(r14);
            r11[r16] = r14;
            r11 = java.lang.String.format(r13, r11);
            r10.num = r11;
            goto L_0x0258;
        L_0x0252:
            r11 = "•";
            r10.num = r11;
        L_0x0258:
            r11 = r7.items;
            r11.add(r10);
            r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageListItemText;
            if (r11 == 0) goto L_0x026c;
        L_0x0263:
            r11 = r9;
            r11 = (org.telegram.tgnet.TLRPC.TL_pageListItemText) r11;
            r11 = r11.text;
            r10.textItem = r11;
            goto L_0x0296;
        L_0x026c:
            r11 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageListItemBlocks;
            if (r11 == 0) goto L_0x0296;
        L_0x0270:
            r11 = r9;
            r11 = (org.telegram.tgnet.TLRPC.TL_pageListItemBlocks) r11;
            r14 = r11.blocks;
            r14 = r14.isEmpty();
            if (r14 != 0) goto L_0x0288;
        L_0x027b:
            r11 = r11.blocks;
            r14 = 0;
            r11 = r11.get(r14);
            r11 = (org.telegram.tgnet.TLRPC.PageBlock) r11;
            r10.blockItem = r11;
            goto L_0x0296;
        L_0x0288:
            r9 = new org.telegram.tgnet.TLRPC$TL_pageListItemText;
            r9.<init>();
            r11 = new org.telegram.tgnet.TLRPC$TL_textPlain;
            r11.<init>();
            r11.text = r8;
            r9.text = r11;
        L_0x0296:
            if (r5 == 0) goto L_0x02b7;
        L_0x0298:
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
            goto L_0x02c8;
        L_0x02b7:
            r20 = r6;
            r21 = r8;
            if (r15 != 0) goto L_0x02c3;
        L_0x02bd:
            r6 = org.telegram.ui.ArticleViewer.this;
            r10 = r6.fixListBlock(r0, r10);
        L_0x02c3:
            r6 = r3 + 1;
            r1.addBlock(r10, r2, r6, r4);
        L_0x02c8:
            r6 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageListItemBlocks;
            if (r6 == 0) goto L_0x0324;
        L_0x02cc:
            r9 = (org.telegram.tgnet.TLRPC.TL_pageListItemBlocks) r9;
            r6 = r9.blocks;
            r6 = r6.size();
            r8 = 1;
        L_0x02d5:
            if (r8 >= r6) goto L_0x0324;
        L_0x02d7:
            r10 = new org.telegram.ui.ArticleViewer$TL_pageBlockListItem;
            r11 = org.telegram.ui.ArticleViewer.this;
            r14 = 0;
            r10.<init>(r11, r14);
            r11 = r9.blocks;
            r11 = r11.get(r8);
            r11 = (org.telegram.tgnet.TLRPC.PageBlock) r11;
            r10.blockItem = r11;
            r10.parent = r7;
            if (r5 == 0) goto L_0x030d;
        L_0x02ef:
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
            goto L_0x0316;
        L_0x030d:
            r22 = r6;
            r23 = r9;
            r6 = r3 + 1;
            r1.addBlock(r10, r2, r6, r4);
        L_0x0316:
            r6 = r7.items;
            r6.add(r10);
            r8 = r8 + 1;
            r6 = r22;
            r9 = r23;
            goto L_0x02d5;
        L_0x0324:
            r15 = r15 + 1;
            r14 = r18;
            r6 = r20;
            r8 = r21;
            r9 = 0;
            r10 = 1;
            r11 = 0;
            goto L_0x0203;
        L_0x0331:
            r21 = r8;
            r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockOrderedList;
            if (r7 == 0) goto L_0x052e;
        L_0x0337:
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
        L_0x034e:
            if (r9 >= r8) goto L_0x052e;
        L_0x0350:
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
            if (r14 == 0) goto L_0x03ed;
        L_0x0373:
            r14 = r10;
            r14 = (org.telegram.tgnet.TLRPC.TL_pageListOrderedItemText) r14;
            r18 = r6;
            r6 = r14.text;
            r11.textItem = r6;
            r6 = r14.num;
            r6 = android.text.TextUtils.isEmpty(r6);
            if (r6 == 0) goto L_0x03b9;
        L_0x0385:
            r6 = org.telegram.ui.ArticleViewer.this;
            r6 = r6.isRtl;
            if (r6 == 0) goto L_0x03a3;
        L_0x038d:
            r6 = 1;
            r14 = new java.lang.Object[r6];
            r15 = r9 + 1;
            r15 = java.lang.Integer.valueOf(r15);
            r16 = 0;
            r14[r16] = r15;
            r14 = java.lang.String.format(r12, r14);
            r11.num = r14;
            goto L_0x048f;
        L_0x03a3:
            r6 = 1;
            r16 = 0;
            r14 = new java.lang.Object[r6];
            r6 = r9 + 1;
            r6 = java.lang.Integer.valueOf(r6);
            r14[r16] = r6;
            r6 = java.lang.String.format(r13, r14);
            r11.num = r6;
            goto L_0x048f;
        L_0x03b9:
            r6 = org.telegram.ui.ArticleViewer.this;
            r6 = r6.isRtl;
            if (r6 == 0) goto L_0x03d7;
        L_0x03c1:
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r6.append(r15);
            r14 = r14.num;
            r6.append(r14);
            r6 = r6.toString();
            r11.num = r6;
            goto L_0x048f;
        L_0x03d7:
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r14 = r14.num;
            r6.append(r14);
            r6.append(r15);
            r6 = r6.toString();
            r11.num = r6;
            goto L_0x048f;
        L_0x03ed:
            r18 = r6;
            r6 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageListOrderedItemBlocks;
            if (r6 == 0) goto L_0x048f;
        L_0x03f3:
            r6 = r10;
            r6 = (org.telegram.tgnet.TLRPC.TL_pageListOrderedItemBlocks) r6;
            r14 = r6.blocks;
            r14 = r14.isEmpty();
            if (r14 != 0) goto L_0x040f;
        L_0x03fe:
            r14 = r6.blocks;
            r20 = r8;
            r8 = 0;
            r14 = r14.get(r8);
            r14 = (org.telegram.tgnet.TLRPC.PageBlock) r14;
            r11.blockItem = r14;
            r14 = r21;
            goto L_0x0421;
        L_0x040f:
            r20 = r8;
            r10 = new org.telegram.tgnet.TLRPC$TL_pageListOrderedItemText;
            r10.<init>();
            r8 = new org.telegram.tgnet.TLRPC$TL_textPlain;
            r8.<init>();
            r14 = r21;
            r8.text = r14;
            r10.text = r8;
        L_0x0421:
            r8 = r6.num;
            r8 = android.text.TextUtils.isEmpty(r8);
            if (r8 == 0) goto L_0x045b;
        L_0x0429:
            r6 = org.telegram.ui.ArticleViewer.this;
            r6 = r6.isRtl;
            if (r6 == 0) goto L_0x0446;
        L_0x0431:
            r8 = 1;
            r6 = new java.lang.Object[r8];
            r15 = r9 + 1;
            r15 = java.lang.Integer.valueOf(r15);
            r16 = 0;
            r6[r16] = r15;
            r6 = java.lang.String.format(r12, r6);
            r11.num = r6;
            goto L_0x0495;
        L_0x0446:
            r8 = 1;
            r16 = 0;
            r6 = new java.lang.Object[r8];
            r15 = r9 + 1;
            r15 = java.lang.Integer.valueOf(r15);
            r6[r16] = r15;
            r6 = java.lang.String.format(r13, r6);
            r11.num = r6;
            goto L_0x0495;
        L_0x045b:
            r16 = 0;
            r8 = org.telegram.ui.ArticleViewer.this;
            r8 = r8.isRtl;
            if (r8 == 0) goto L_0x047a;
        L_0x0465:
            r8 = new java.lang.StringBuilder;
            r8.<init>();
            r8.append(r15);
            r6 = r6.num;
            r8.append(r6);
            r6 = r8.toString();
            r11.num = r6;
            goto L_0x0495;
        L_0x047a:
            r8 = new java.lang.StringBuilder;
            r8.<init>();
            r6 = r6.num;
            r8.append(r6);
            r8.append(r15);
            r6 = r8.toString();
            r11.num = r6;
            goto L_0x0495;
        L_0x048f:
            r20 = r8;
            r14 = r21;
            r16 = 0;
        L_0x0495:
            if (r5 == 0) goto L_0x04b4;
        L_0x0497:
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
            goto L_0x04c3;
        L_0x04b4:
            r21 = r12;
            if (r9 != 0) goto L_0x04be;
        L_0x04b8:
            r6 = org.telegram.ui.ArticleViewer.this;
            r11 = r6.fixListBlock(r0, r11);
        L_0x04be:
            r6 = r3 + 1;
            r1.addBlock(r11, r2, r6, r4);
        L_0x04c3:
            r6 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageListOrderedItemBlocks;
            if (r6 == 0) goto L_0x051b;
        L_0x04c7:
            r10 = (org.telegram.tgnet.TLRPC.TL_pageListOrderedItemBlocks) r10;
            r6 = r10.blocks;
            r6 = r6.size();
            r8 = 1;
        L_0x04d0:
            if (r8 >= r6) goto L_0x051b;
        L_0x04d2:
            r11 = new org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem;
            r12 = org.telegram.ui.ArticleViewer.this;
            r15 = 0;
            r11.<init>(r12, r15);
            r12 = r10.blocks;
            r12 = r12.get(r8);
            r12 = (org.telegram.tgnet.TLRPC.PageBlock) r12;
            r11.blockItem = r12;
            r11.parent = r7;
            if (r5 == 0) goto L_0x0506;
        L_0x04ea:
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
            goto L_0x050d;
        L_0x0506:
            r19 = r5;
            r0 = r3 + 1;
            r1.addBlock(r11, r2, r0, r4);	 Catch:{ all -> 0x052f }
        L_0x050d:
            r0 = r7.items;
            r0.add(r11);
            r8 = r8 + 1;
            r0 = r25;
            r5 = r19;
            goto L_0x04d0;
        L_0x051b:
            r19 = r5;
            r15 = 0;
            r9 = r9 + 1;
            r0 = r25;
            r6 = r18;
            r5 = r19;
            r8 = r20;
            r12 = r21;
            r21 = r14;
            goto L_0x034e;
        L_0x052e:
            return;
        L_0x052f:
            r0 = move-exception;
            r2 = r0;
            goto L_0x0533;
        L_0x0532:
            throw r2;
        L_0x0533:
            goto L_0x0532;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$WebpageAdapter.addBlock(org.telegram.tgnet.TLRPC$PageBlock, int, int, int):void");
        }

        private void addAllMediaFromBlock(PageBlock pageBlock) {
            if (pageBlock instanceof TL_pageBlockPhoto) {
                TL_pageBlockPhoto tL_pageBlockPhoto = (TL_pageBlockPhoto) pageBlock;
                Photo access$12900 = ArticleViewer.this.getPhotoWithId(tL_pageBlockPhoto.photo_id);
                if (access$12900 != null) {
                    tL_pageBlockPhoto.thumb = FileLoader.getClosestPhotoSizeWithSize(access$12900.sizes, 56, true);
                    tL_pageBlockPhoto.thumbObject = access$12900;
                    this.photoBlocks.add(pageBlock);
                }
            } else if ((pageBlock instanceof TL_pageBlockVideo) && ArticleViewer.this.isVideoBlock(pageBlock)) {
                TL_pageBlockVideo tL_pageBlockVideo = (TL_pageBlockVideo) pageBlock;
                Document access$10800 = ArticleViewer.this.getDocumentWithId(tL_pageBlockVideo.video_id);
                if (access$10800 != null) {
                    tL_pageBlockVideo.thumb = FileLoader.getClosestPhotoSizeWithSize(access$10800.thumbs, 56, true);
                    tL_pageBlockVideo.thumbObject = access$10800;
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
                i = ArticleViewer.this.getSelectedColor();
                if (i == 0) {
                    textView.setTextColor(-8879475);
                    textView.setBackgroundColor(-1183760);
                } else if (i == 1) {
                    textView.setTextColor(ArticleViewer.this.getGrayTextColor());
                    textView.setBackgroundColor(-1712440);
                } else if (i == 2) {
                    textView.setTextColor(ArticleViewer.this.getGrayTextColor());
                    textView.setBackgroundColor(-15000805);
                }
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
                        blockVideoCell.setParentBlock(pageBlock);
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
            PageBlock access$10700 = ArticleViewer.this.getLastNonListPageBlock(tL_pageBlockDetailsChild.parent);
            if (access$10700 instanceof TL_pageBlockDetails) {
                return ((TL_pageBlockDetails) access$10700).open;
            }
            if (!(access$10700 instanceof TL_pageBlockDetailsChild)) {
                return false;
            }
            tL_pageBlockDetailsChild = (TL_pageBlockDetailsChild) access$10700;
            PageBlock access$107002 = ArticleViewer.this.getLastNonListPageBlock(tL_pageBlockDetailsChild.block);
            if (!(access$107002 instanceof TL_pageBlockDetails) || ((TL_pageBlockDetails) access$107002).open) {
                return isBlockOpened(tL_pageBlockDetailsChild);
            }
            return false;
        }

        private void updateRows() {
            this.localBlocks.clear();
            int size = this.blocks.size();
            for (int i = 0; i < size; i++) {
                PageBlock pageBlock = (PageBlock) this.blocks.get(i);
                PageBlock access$10700 = ArticleViewer.this.getLastNonListPageBlock(pageBlock);
                if (!(access$10700 instanceof TL_pageBlockDetailsChild) || isBlockOpened((TL_pageBlockDetailsChild) access$10700)) {
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

    static /* synthetic */ int access$1104(ArticleViewer articleViewer) {
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
            photoBackgroundPaint = new Paint();
            dividerPaint = new Paint();
            webpageMarkPaint = new Paint(1);
        } else if (!z) {
            return;
        }
        int selectedColor = getSelectedColor();
        if (selectedColor == 0) {
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
        } else if (selectedColor == 1) {
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
        } else if (selectedColor == 2) {
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

    private void showCopyPopup(String str) {
        if (this.parentActivity != null) {
            BottomSheet bottomSheet = this.linkSheet;
            if (bottomSheet != null) {
                bottomSheet.dismiss();
                this.linkSheet = null;
            }
            Builder builder = new Builder(this.parentActivity);
            builder.setUseFullscreen(true);
            builder.setTitle(str);
            CharSequence[] charSequenceArr = new CharSequence[2];
            int i = 0;
            charSequenceArr[0] = LocaleController.getString("Open", NUM);
            charSequenceArr[1] = LocaleController.getString("Copy", NUM);
            builder.setItems(charSequenceArr, new -$$Lambda$ArticleViewer$OR-FYCAXpGUOR5Uvrpul7uvfnQI(this, str));
            BottomSheet create = builder.create();
            showDialog(create);
            while (i < 2) {
                create.setItemColor(i, getTextColor(), getTextColor());
                i++;
            }
            create.setTitleColor(getGrayTextColor());
            int i2 = this.selectedColor;
            if (i2 == 0) {
                create.setBackgroundColor(-1);
            } else if (i2 == 1) {
                create.setBackgroundColor(-659492);
            } else if (i2 == 2) {
                create.setBackgroundColor(-15461356);
            }
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
                ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
                Drawable drawable = this.parentActivity.getResources().getDrawable(NUM);
                this.copyBackgroundDrawable = drawable;
                actionBarPopupWindowLayout.setBackgroundDrawable(drawable);
                this.popupLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new -$$Lambda$ArticleViewer$MGSGAQzWCEU3w9PF3AH-evurh-k(this));
                this.popupLayout.setDispatchKeyEventListener(new -$$Lambda$ArticleViewer$qD5uCRo_niW9s97tmnD3kf5paxo(this));
                this.popupLayout.setShowedFromBotton(false);
                this.deleteView = new TextView(this.parentActivity);
                this.deleteView.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 2));
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
            Drawable drawable2;
            if (this.selectedColor == 2) {
                this.deleteView.setTextColor(-5723992);
                drawable2 = this.copyBackgroundDrawable;
                if (drawable2 != null) {
                    drawable2.setColorFilter(new PorterDuffColorFilter(-14408668, Mode.MULTIPLY));
                }
            } else {
                this.deleteView.setTextColor(-14606047);
                drawable2 = this.copyBackgroundDrawable;
                if (drawable2 != null) {
                    drawable2.setColorFilter(new PorterDuffColorFilter(-1, Mode.MULTIPLY));
                }
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

    private void updateInterfaceForCurrentPage(int i) {
        WebPage webPage = this.currentPage;
        if (webPage != null) {
            Page page = webPage.cached_page;
            if (page != null) {
                int indexOfChild;
                this.isRtl = page.rtl;
                this.channelBlock = null;
                SimpleTextView simpleTextView = this.titleTextView;
                CharSequence charSequence = webPage.site_name;
                if (charSequence == null) {
                    charSequence = "";
                }
                simpleTextView.setText(charSequence);
                boolean z = true;
                if (i != 0) {
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
                    int indexOfChild2 = this.containerView.indexOfChild(this.listView[1]);
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
                            ArticleViewer.this.listView[indexOfChild].setBackgroundDrawable(null);
                            if (VERSION.SDK_INT >= 18) {
                                ArticleViewer.this.listView[indexOfChild].setLayerType(0, null);
                            }
                            ArticleViewer.this.pageSwitchAnimation = null;
                        }
                    });
                    this.pageSwitchAnimation.start();
                }
                this.headerView.invalidate();
                this.adapter[0].cleanup();
                int size = this.currentPage.cached_page.blocks.size();
                indexOfChild = 0;
                while (indexOfChild < size) {
                    PageBlock pageBlock = (PageBlock) this.currentPage.cached_page.blocks.get(indexOfChild);
                    if (indexOfChild == 0) {
                        pageBlock.first = true;
                        if (pageBlock instanceof TL_pageBlockCover) {
                            TL_pageBlockCover tL_pageBlockCover = (TL_pageBlockCover) pageBlock;
                            RichText blockCaption = getBlockCaption(tL_pageBlockCover, 0);
                            RichText blockCaption2 = getBlockCaption(tL_pageBlockCover, 1);
                            if (!((blockCaption == null || (blockCaption instanceof TL_textEmpty)) && (blockCaption2 == null || (blockCaption2 instanceof TL_textEmpty))) && size > 1) {
                                PageBlock pageBlock2 = (PageBlock) this.currentPage.cached_page.blocks.get(1);
                                if (pageBlock2 instanceof TL_pageBlockChannel) {
                                    this.channelBlock = (TL_pageBlockChannel) pageBlock2;
                                }
                            }
                        }
                    } else if (indexOfChild == 1 && this.channelBlock != null) {
                        indexOfChild++;
                    }
                    this.adapter[0].addBlock(pageBlock, 0, 0, indexOfChild == size + -1 ? indexOfChild : 0);
                    indexOfChild++;
                }
                this.adapter[0].notifyDataSetChanged();
                if (this.pagesStack.size() == 1 || i == -1) {
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("article");
                    stringBuilder.append(this.currentPage.id);
                    String stringBuilder2 = stringBuilder.toString();
                    indexOfChild = sharedPreferences.getInt(stringBuilder2, -1);
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(stringBuilder2);
                    stringBuilder3.append("r");
                    boolean z2 = sharedPreferences.getBoolean(stringBuilder3.toString(), true);
                    Point point = AndroidUtilities.displaySize;
                    if (point.x <= point.y) {
                        z = false;
                    }
                    if (z2 == z) {
                        StringBuilder stringBuilder4 = new StringBuilder();
                        stringBuilder4.append(stringBuilder2);
                        stringBuilder4.append("o");
                        i = sharedPreferences.getInt(stringBuilder4.toString(), 0) - this.listView[0].getPaddingTop();
                    } else {
                        i = AndroidUtilities.dp(10.0f);
                    }
                    if (indexOfChild != -1) {
                        this.layoutManager[0].scrollToPositionWithOffset(indexOfChild, i);
                    }
                } else {
                    this.layoutManager[0].scrollToPositionWithOffset(0, 0);
                }
                checkScrollAnimated();
            }
        }
    }

    private boolean addPageToStack(WebPage webPage, String str, int i) {
        saveCurrentPagePosition();
        this.currentPage = webPage;
        this.pagesStack.add(webPage);
        updateInterfaceForCurrentPage(i);
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
            int access$6600;
            if (tL_textAnchor != null) {
                TL_pageBlockParagraph tL_pageBlockParagraph = new TL_pageBlockParagraph();
                tL_pageBlockParagraph.text = tL_textAnchor.text;
                access$6600 = this.adapter[0].getTypeForBlock(tL_pageBlockParagraph);
                ViewHolder onCreateViewHolder = this.adapter[0].onCreateViewHolder(null, access$6600);
                this.adapter[0].bindBlockToHolder(access$6600, onCreateViewHolder, tL_pageBlockParagraph, 0, 0);
                Builder builder = new Builder(this.parentActivity);
                builder.setUseFullscreen(true);
                builder.setApplyTopPadding(false);
                LinearLayout linearLayout = new LinearLayout(this.parentActivity);
                linearLayout.setOrientation(1);
                AnonymousClass3 anonymousClass3 = new TextView(this.parentActivity) {
                    /* Access modifiers changed, original: protected */
                    public void onDraw(Canvas canvas) {
                        canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
                        super.onDraw(canvas);
                    }
                };
                anonymousClass3.setTextSize(1, 16.0f);
                anonymousClass3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                anonymousClass3.setText(LocaleController.getString("InstantViewReference", NUM));
                anonymousClass3.setGravity((this.isRtl ? 5 : 3) | 16);
                anonymousClass3.setTextColor(getTextColor());
                anonymousClass3.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
                linearLayout.addView(anonymousClass3, new LinearLayout.LayoutParams(-1, AndroidUtilities.dp(48.0f) + 1));
                linearLayout.addView(onCreateViewHolder.itemView, LayoutHelper.createLinear(-1, -2, 0.0f, 7.0f, 0.0f, 0.0f));
                builder.setCustomView(linearLayout);
                this.linkSheet = builder.create();
                int i = this.selectedColor;
                if (i == 0) {
                    this.linkSheet.setBackgroundColor(-1);
                } else if (i == 1) {
                    this.linkSheet.setBackgroundColor(-659492);
                } else if (i == 2) {
                    this.linkSheet.setBackgroundColor(-15461356);
                }
                showDialog(this.linkSheet);
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
                        access$6600 = this.adapter[0].getTypeForBlock(pageBlock);
                        ViewHolder onCreateViewHolder2 = this.adapter[0].onCreateViewHolder(null, access$6600);
                        this.adapter[0].bindBlockToHolder(access$6600, onCreateViewHolder2, pageBlock, 0, 0);
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
        updateInterfaceForCurrentPage(-1);
        return true;
    }

    /* Access modifiers changed, original: protected */
    public void startCheckLongPress() {
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap(this, null);
            }
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
                int i2 = 1;
                int textFlags;
                int length;
                if (richText4 instanceof TL_textConcat) {
                    SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder();
                    int size = richText4.texts.size();
                    int i3 = 0;
                    while (i3 < size) {
                        RichText richText5 = (RichText) richText4.texts.get(i3);
                        RichText lastRichText = getLastRichText(richText5);
                        Object obj = (i < 0 || !(richText5 instanceof TL_textUrl) || ((TL_textUrl) richText5).webpage_id == j) ? null : 1;
                        str = " ";
                        if (!(obj == null || spannableStringBuilder3.length() == 0 || spannableStringBuilder3.charAt(spannableStringBuilder3.length() - i2) == 10)) {
                            spannableStringBuilder3.append(str);
                        }
                        String str2 = str;
                        RichText richText6 = lastRichText;
                        RichText richText7 = richText5;
                        int i4 = i3;
                        int i5 = size;
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
                        if (!(obj == null || i4 == i5 - 1)) {
                            spannableStringBuilder3.append(str2);
                        }
                        i3 = i4 + 1;
                        size = i5;
                        i2 = 1;
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
                    int i6;
                    int i7;
                    SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder("*");
                    textFlags = AndroidUtilities.dp((float) tL_textImage.w);
                    int dp = AndroidUtilities.dp((float) tL_textImage.h);
                    length = Math.abs(i);
                    if (textFlags > length) {
                        i6 = (int) (((float) dp) * (((float) length) / ((float) textFlags)));
                        i7 = length;
                    } else {
                        i6 = dp;
                        i7 = textFlags;
                    }
                    spannableStringBuilder4.setSpan(new TextPaintImageReceiverSpan(view, documentWithId, this.currentPage, i7, i6, false, this.selectedColor == 2), 0, spannableStringBuilder4.length(), 33);
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
        int selectedColor = getSelectedColor();
        return (selectedColor == 0 || selectedColor == 1) ? -14606047 : -6710887;
    }

    private int getInstantLinkBackgroundColor() {
        int selectedColor = getSelectedColor();
        if (selectedColor != 0) {
            return selectedColor != 1 ? -14536904 : -2498337;
        } else {
            return -1707782;
        }
    }

    private int getLinkTextColor() {
        int selectedColor = getSelectedColor();
        if (selectedColor != 0) {
            return selectedColor != 1 ? -10838585 : -13471296;
        } else {
            return -15435321;
        }
    }

    private int getGrayTextColor() {
        int selectedColor = getSelectedColor();
        if (selectedColor != 0) {
            return selectedColor != 1 ? -10066330 : -11711675;
        } else {
            return -8156010;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:144:0x02da  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02bd  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0044  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02bd  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x02da  */
    private android.text.TextPaint getTextPaint(org.telegram.tgnet.TLRPC.RichText r19, org.telegram.tgnet.TLRPC.RichText r20, org.telegram.tgnet.TLRPC.PageBlock r21) {
        /*
        r18 = this;
        r0 = r18;
        r1 = r19;
        r2 = r20;
        r3 = r21;
        r4 = r0.getTextFlags(r2);
        r5 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r7 = r0.selectedFontSize;
        r8 = 0;
        r9 = 3;
        r10 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r11 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r12 = 1;
        if (r7 != 0) goto L_0x0023;
    L_0x001d:
        r7 = org.telegram.messenger.AndroidUtilities.dp(r11);
    L_0x0021:
        r7 = -r7;
        goto L_0x003a;
    L_0x0023:
        if (r7 != r12) goto L_0x002a;
    L_0x0025:
        r7 = org.telegram.messenger.AndroidUtilities.dp(r10);
        goto L_0x0021;
    L_0x002a:
        if (r7 != r9) goto L_0x0031;
    L_0x002c:
        r7 = org.telegram.messenger.AndroidUtilities.dp(r10);
        goto L_0x003a;
    L_0x0031:
        r13 = 4;
        if (r7 != r13) goto L_0x0039;
    L_0x0034:
        r7 = org.telegram.messenger.AndroidUtilities.dp(r11);
        goto L_0x003a;
    L_0x0039:
        r7 = 0;
    L_0x003a:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockPhoto;
        r14 = -65536; // 0xfffffffffffvar_ float:NaN double:NaN;
        r15 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r16 = 0;
        if (r13 == 0) goto L_0x0065;
    L_0x0044:
        r6 = r3;
        r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockPhoto) r6;
        r6 = r6.caption;
        r6 = r6.text;
        if (r6 == r2) goto L_0x0057;
    L_0x004d:
        if (r6 != r1) goto L_0x0050;
    L_0x004f:
        goto L_0x0057;
    L_0x0050:
        r1 = photoCreditTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        goto L_0x005d;
    L_0x0057:
        r1 = photoCaptionTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
    L_0x005d:
        r16 = r1;
        r6 = r2;
        r1 = r18.getGrayTextColor();
        goto L_0x009a;
    L_0x0065:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockMap;
        if (r13 == 0) goto L_0x008a;
    L_0x0069:
        r6 = r3;
        r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockMap) r6;
        r6 = r6.caption;
        r6 = r6.text;
        if (r6 == r2) goto L_0x007c;
    L_0x0072:
        if (r6 != r1) goto L_0x0075;
    L_0x0074:
        goto L_0x007c;
    L_0x0075:
        r1 = photoCreditTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        goto L_0x0082;
    L_0x007c:
        r1 = photoCaptionTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
    L_0x0082:
        r16 = r1;
        r6 = r2;
        r1 = r18.getGrayTextColor();
        goto L_0x009a;
    L_0x008a:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockTitle;
        if (r13 == 0) goto L_0x009f;
    L_0x008e:
        r16 = titleTextPaints;
        r1 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r18.getTextColor();
    L_0x009a:
        r2 = r1;
        r1 = r16;
        goto L_0x02ae;
    L_0x009f:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockKicker;
        if (r13 == 0) goto L_0x00ae;
    L_0x00a3:
        r16 = kickerTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1 = r18.getTextColor();
        goto L_0x009a;
    L_0x00ae:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockAuthorDate;
        if (r13 == 0) goto L_0x00bd;
    L_0x00b2:
        r16 = authorTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1 = r18.getGrayTextColor();
        goto L_0x009a;
    L_0x00bd:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockFooter;
        if (r13 == 0) goto L_0x00cc;
    L_0x00c1:
        r16 = footerTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1 = r18.getGrayTextColor();
        goto L_0x009a;
    L_0x00cc:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockSubtitle;
        r17 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        if (r13 == 0) goto L_0x00dd;
    L_0x00d2:
        r16 = subtitleTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1 = r18.getTextColor();
        goto L_0x009a;
    L_0x00dd:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockHeader;
        if (r13 == 0) goto L_0x00ec;
    L_0x00e1:
        r16 = headerTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1 = r18.getTextColor();
        goto L_0x009a;
    L_0x00ec:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockSubheader;
        if (r13 == 0) goto L_0x00fd;
    L_0x00f0:
        r16 = subheaderTextPaints;
        r1 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r18.getTextColor();
        goto L_0x009a;
    L_0x00fd:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockBlockquote;
        r17 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        if (r13 == 0) goto L_0x0129;
    L_0x0103:
        r2 = r3;
        r2 = (org.telegram.tgnet.TLRPC.TL_pageBlockBlockquote) r2;
        r13 = r2.text;
        if (r13 != r1) goto L_0x0115;
    L_0x010a:
        r16 = quoteTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1 = r18.getTextColor();
        goto L_0x009a;
    L_0x0115:
        r2 = r2.caption;
        if (r2 != r1) goto L_0x0125;
    L_0x0119:
        r16 = photoCaptionTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1 = r18.getGrayTextColor();
        goto L_0x009a;
    L_0x0125:
        r1 = -65536; // 0xfffffffffffvar_ float:NaN double:NaN;
        goto L_0x009a;
    L_0x0129:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockPullquote;
        if (r13 == 0) goto L_0x0150;
    L_0x012d:
        r2 = r3;
        r2 = (org.telegram.tgnet.TLRPC.TL_pageBlockPullquote) r2;
        r13 = r2.text;
        if (r13 != r1) goto L_0x0140;
    L_0x0134:
        r16 = quoteTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1 = r18.getTextColor();
        goto L_0x009a;
    L_0x0140:
        r2 = r2.caption;
        if (r2 != r1) goto L_0x0125;
    L_0x0144:
        r16 = photoCaptionTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1 = r18.getGrayTextColor();
        goto L_0x009a;
    L_0x0150:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockPreformatted;
        if (r13 == 0) goto L_0x0160;
    L_0x0154:
        r16 = preformattedTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1 = r18.getTextColor();
        goto L_0x009a;
    L_0x0160:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockParagraph;
        if (r13 == 0) goto L_0x0172;
    L_0x0164:
        r16 = paragraphTextPaints;
        r1 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r18.getTextColor();
        goto L_0x009a;
    L_0x0172:
        r13 = r0.isListItemBlock(r3);
        if (r13 == 0) goto L_0x0186;
    L_0x0178:
        r16 = listTextPaints;
        r1 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r18.getTextColor();
        goto L_0x009a;
    L_0x0186:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockEmbed;
        if (r13 == 0) goto L_0x01ac;
    L_0x018a:
        r6 = r3;
        r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockEmbed) r6;
        r6 = r6.caption;
        r6 = r6.text;
        if (r6 == r2) goto L_0x019d;
    L_0x0193:
        if (r6 != r1) goto L_0x0196;
    L_0x0195:
        goto L_0x019d;
    L_0x0196:
        r1 = photoCreditTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        goto L_0x01a3;
    L_0x019d:
        r1 = photoCaptionTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
    L_0x01a3:
        r16 = r1;
        r6 = r2;
        r1 = r18.getGrayTextColor();
        goto L_0x009a;
    L_0x01ac:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow;
        if (r13 == 0) goto L_0x01d2;
    L_0x01b0:
        r6 = r3;
        r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow) r6;
        r6 = r6.caption;
        r6 = r6.text;
        if (r6 == r2) goto L_0x01c3;
    L_0x01b9:
        if (r6 != r1) goto L_0x01bc;
    L_0x01bb:
        goto L_0x01c3;
    L_0x01bc:
        r1 = photoCreditTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        goto L_0x01c9;
    L_0x01c3:
        r1 = photoCaptionTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
    L_0x01c9:
        r16 = r1;
        r6 = r2;
        r1 = r18.getGrayTextColor();
        goto L_0x009a;
    L_0x01d2:
        r13 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCollage;
        if (r13 == 0) goto L_0x01f8;
    L_0x01d6:
        r6 = r3;
        r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockCollage) r6;
        r6 = r6.caption;
        r6 = r6.text;
        if (r6 == r2) goto L_0x01e9;
    L_0x01df:
        if (r6 != r1) goto L_0x01e2;
    L_0x01e1:
        goto L_0x01e9;
    L_0x01e2:
        r1 = photoCreditTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        goto L_0x01ef;
    L_0x01e9:
        r1 = photoCaptionTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
    L_0x01ef:
        r16 = r1;
        r6 = r2;
        r1 = r18.getGrayTextColor();
        goto L_0x009a;
    L_0x01f8:
        r1 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost;
        if (r1 == 0) goto L_0x022f;
    L_0x01fc:
        r1 = r3;
        r1 = (org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost) r1;
        r1 = r1.caption;
        r13 = r1.text;
        if (r2 != r13) goto L_0x0211;
    L_0x0205:
        r16 = photoCaptionTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1 = r18.getGrayTextColor();
        goto L_0x009a;
    L_0x0211:
        r1 = r1.credit;
        if (r2 != r1) goto L_0x0221;
    L_0x0215:
        r16 = photoCreditTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r1 = r18.getGrayTextColor();
        goto L_0x009a;
    L_0x0221:
        if (r2 == 0) goto L_0x0125;
    L_0x0223:
        r16 = embedPostTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1 = r18.getTextColor();
        goto L_0x009a;
    L_0x022f:
        r1 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockVideo;
        if (r1 == 0) goto L_0x0257;
    L_0x0233:
        r1 = r3;
        r1 = (org.telegram.tgnet.TLRPC.TL_pageBlockVideo) r1;
        r1 = r1.caption;
        r1 = r1.text;
        if (r2 != r1) goto L_0x0247;
    L_0x023c:
        r1 = mediaCaptionTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = r18.getTextColor();
        goto L_0x0251;
    L_0x0247:
        r1 = mediaCreditTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r6 = r18.getTextColor();
    L_0x0251:
        r16 = r1;
        r1 = r6;
        r6 = r2;
        goto L_0x009a;
    L_0x0257:
        r1 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockAudio;
        if (r1 == 0) goto L_0x027a;
    L_0x025b:
        r1 = r3;
        r1 = (org.telegram.tgnet.TLRPC.TL_pageBlockAudio) r1;
        r1 = r1.caption;
        r1 = r1.text;
        if (r2 != r1) goto L_0x026f;
    L_0x0264:
        r1 = mediaCaptionTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = r18.getTextColor();
        goto L_0x0251;
    L_0x026f:
        r1 = mediaCreditTextPaints;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r6 = r18.getTextColor();
        goto L_0x0251;
    L_0x027a:
        r1 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockRelatedArticles;
        if (r1 == 0) goto L_0x028a;
    L_0x027e:
        r16 = relatedArticleTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1 = r18.getGrayTextColor();
        goto L_0x009a;
    L_0x028a:
        r1 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockDetails;
        if (r1 == 0) goto L_0x029a;
    L_0x028e:
        r16 = detailsTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1 = r18.getTextColor();
        goto L_0x009a;
    L_0x029a:
        r1 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockTable;
        if (r1 == 0) goto L_0x02aa;
    L_0x029e:
        r16 = tableTextPaints;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1 = r18.getTextColor();
        goto L_0x009a;
    L_0x02aa:
        r1 = r16;
        r2 = -65536; // 0xfffffffffffvar_ float:NaN double:NaN;
    L_0x02ae:
        r13 = r4 & 256;
        if (r13 != 0) goto L_0x02b6;
    L_0x02b2:
        r15 = r4 & 128;
        if (r15 == 0) goto L_0x02bb;
    L_0x02b6:
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r6 = r6 - r11;
    L_0x02bb:
        if (r1 != 0) goto L_0x02da;
    L_0x02bd:
        r1 = errorTextPaint;
        if (r1 != 0) goto L_0x02cd;
    L_0x02c1:
        r1 = new android.text.TextPaint;
        r1.<init>(r12);
        errorTextPaint = r1;
        r1 = errorTextPaint;
        r1.setColor(r14);
    L_0x02cd:
        r1 = errorTextPaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = (float) r2;
        r1.setTextSize(r2);
        r1 = errorTextPaint;
        return r1;
    L_0x02da:
        r5 = r1.get(r4);
        r5 = (android.text.TextPaint) r5;
        if (r5 != 0) goto L_0x03c6;
    L_0x02e2:
        r5 = new android.text.TextPaint;
        r5.<init>(r12);
        r11 = r4 & 4;
        if (r11 == 0) goto L_0x02f6;
    L_0x02eb:
        r3 = "fonts/rmono.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r5.setTypeface(r3);
        goto L_0x0378;
    L_0x02f6:
        r11 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockRelatedArticles;
        r14 = "fonts/rmedium.ttf";
        if (r11 == 0) goto L_0x0305;
    L_0x02fc:
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r14);
        r5.setTypeface(r3);
        goto L_0x0378;
    L_0x0305:
        r11 = r0.selectedFont;
        if (r11 == r12) goto L_0x0348;
    L_0x0309:
        r11 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockTitle;
        if (r11 != 0) goto L_0x0348;
    L_0x030d:
        r11 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockKicker;
        if (r11 != 0) goto L_0x0348;
    L_0x0311:
        r11 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockHeader;
        if (r11 != 0) goto L_0x0348;
    L_0x0315:
        r11 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockSubtitle;
        if (r11 != 0) goto L_0x0348;
    L_0x0319:
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockSubheader;
        if (r3 == 0) goto L_0x031e;
    L_0x031d:
        goto L_0x0348;
    L_0x031e:
        r3 = r4 & 1;
        if (r3 == 0) goto L_0x0330;
    L_0x0322:
        r8 = r4 & 2;
        if (r8 == 0) goto L_0x0330;
    L_0x0326:
        r3 = "fonts/rmediumitalic.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r5.setTypeface(r3);
        goto L_0x0378;
    L_0x0330:
        if (r3 == 0) goto L_0x033a;
    L_0x0332:
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r14);
        r5.setTypeface(r3);
        goto L_0x0378;
    L_0x033a:
        r3 = r4 & 2;
        if (r3 == 0) goto L_0x0378;
    L_0x033e:
        r3 = "fonts/ritalic.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r5.setTypeface(r3);
        goto L_0x0378;
    L_0x0348:
        r3 = r4 & 1;
        r11 = "serif";
        if (r3 == 0) goto L_0x035a;
    L_0x034e:
        r14 = r4 & 2;
        if (r14 == 0) goto L_0x035a;
    L_0x0352:
        r3 = android.graphics.Typeface.create(r11, r9);
        r5.setTypeface(r3);
        goto L_0x0378;
    L_0x035a:
        if (r3 == 0) goto L_0x0364;
    L_0x035c:
        r3 = android.graphics.Typeface.create(r11, r12);
        r5.setTypeface(r3);
        goto L_0x0378;
    L_0x0364:
        r3 = r4 & 2;
        if (r3 == 0) goto L_0x0371;
    L_0x0368:
        r3 = 2;
        r3 = android.graphics.Typeface.create(r11, r3);
        r5.setTypeface(r3);
        goto L_0x0378;
    L_0x0371:
        r3 = android.graphics.Typeface.create(r11, r8);
        r5.setTypeface(r3);
    L_0x0378:
        r3 = r4 & 32;
        if (r3 == 0) goto L_0x0385;
    L_0x037c:
        r3 = r5.getFlags();
        r3 = r3 | 16;
        r5.setFlags(r3);
    L_0x0385:
        r3 = r4 & 16;
        if (r3 == 0) goto L_0x0392;
    L_0x0389:
        r3 = r5.getFlags();
        r3 = r3 | 8;
        r5.setFlags(r3);
    L_0x0392:
        r3 = r4 & 8;
        if (r3 != 0) goto L_0x039a;
    L_0x0396:
        r3 = r4 & 512;
        if (r3 == 0) goto L_0x03a5;
    L_0x039a:
        r2 = r5.getFlags();
        r5.setFlags(r2);
        r2 = r18.getLinkTextColor();
    L_0x03a5:
        if (r13 == 0) goto L_0x03b3;
    L_0x03a7:
        r3 = r5.baselineShift;
        r8 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r3 = r3 - r8;
        r5.baselineShift = r3;
        goto L_0x03c0;
    L_0x03b3:
        r3 = r4 & 128;
        if (r3 == 0) goto L_0x03c0;
    L_0x03b7:
        r3 = r5.baselineShift;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r3 = r3 + r8;
        r5.baselineShift = r3;
    L_0x03c0:
        r5.setColor(r2);
        r1.put(r4, r5);
    L_0x03c6:
        r6 = r6 + r7;
        r1 = (float) r6;
        r5.setTextSize(r1);
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.getTextPaint(org.telegram.tgnet.TLRPC$RichText, org.telegram.tgnet.TLRPC$RichText, org.telegram.tgnet.TLRPC$PageBlock):android.text.TextPaint");
    }

    private DrawingText createLayoutForText(View view, CharSequence charSequence, RichText richText, int i, PageBlock pageBlock, WebpageAdapter webpageAdapter) {
        return createLayoutForText(view, charSequence, richText, i, 0, pageBlock, Alignment.ALIGN_NORMAL, 0, webpageAdapter);
    }

    private DrawingText createLayoutForText(View view, CharSequence charSequence, RichText richText, int i, PageBlock pageBlock, Alignment alignment, WebpageAdapter webpageAdapter) {
        return createLayoutForText(view, charSequence, richText, i, 0, pageBlock, alignment, 0, webpageAdapter);
    }

    private DrawingText createLayoutForText(View view, CharSequence charSequence, RichText richText, int i, int i2, PageBlock pageBlock, WebpageAdapter webpageAdapter) {
        return createLayoutForText(view, charSequence, richText, i, i2, pageBlock, Alignment.ALIGN_NORMAL, 0, webpageAdapter);
    }

    /* JADX WARNING: Removed duplicated region for block: B:152:0x0329 A:{Catch:{ Exception -> 0x0366 }} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x020a  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x01d7  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x024e  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x024d A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0329 A:{Catch:{ Exception -> 0x0366 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:142:0x030c */
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
        r11 = r21.getSelectedColor();
        if (r7 == 0) goto L_0x0027;
    L_0x0025:
        r0 = r7;
        goto L_0x0037;
    L_0x0027:
        r0 = r21;
        r1 = r22;
        r2 = r24;
        r3 = r24;
        r4 = r27;
        r5 = r19;
        r0 = r0.getText(r1, r2, r3, r4, r5);
    L_0x0037:
        r1 = android.text.TextUtils.isEmpty(r0);
        if (r1 == 0) goto L_0x003e;
    L_0x003d:
        return r10;
    L_0x003e:
        r1 = r6.selectedFontSize;
        r2 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = 0;
        r4 = 1;
        if (r1 != 0) goto L_0x004c;
    L_0x0046:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
    L_0x004a:
        r1 = -r1;
        goto L_0x0066;
    L_0x004c:
        r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        if (r1 != r4) goto L_0x0055;
    L_0x0050:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r5);
        goto L_0x004a;
    L_0x0055:
        r12 = 3;
        if (r1 != r12) goto L_0x005d;
    L_0x0058:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r5);
        goto L_0x0066;
    L_0x005d:
        r5 = 4;
        if (r1 != r5) goto L_0x0065;
    L_0x0060:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
        goto L_0x0066;
    L_0x0065:
        r1 = 0;
    L_0x0066:
        r5 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost;
        r12 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r13 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        if (r5 == 0) goto L_0x00c8;
    L_0x006e:
        if (r8 != 0) goto L_0x00c8;
    L_0x0070:
        r5 = r9;
        r5 = (org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost) r5;
        r5 = r5.author;
        if (r5 != r7) goto L_0x009a;
    L_0x0077:
        r5 = embedPostAuthorPaint;
        if (r5 != 0) goto L_0x008b;
    L_0x007b:
        r5 = new android.text.TextPaint;
        r5.<init>(r4);
        embedPostAuthorPaint = r5;
        r5 = embedPostAuthorPaint;
        r7 = r21.getTextColor();
        r5.setColor(r7);
    L_0x008b:
        r5 = embedPostAuthorPaint;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r7 = r7 + r1;
        r1 = (float) r7;
        r5.setTextSize(r1);
        r1 = embedPostAuthorPaint;
        goto L_0x01d4;
    L_0x009a:
        r5 = embedPostDatePaint;
        if (r5 != 0) goto L_0x00b9;
    L_0x009e:
        r5 = new android.text.TextPaint;
        r5.<init>(r4);
        embedPostDatePaint = r5;
        if (r11 != 0) goto L_0x00b0;
    L_0x00a7:
        r5 = embedPostDatePaint;
        r7 = -7366752; // 0xffffffffff8var_a0 float:NaN double:NaN;
        r5.setColor(r7);
        goto L_0x00b9;
    L_0x00b0:
        r5 = embedPostDatePaint;
        r7 = r21.getGrayTextColor();
        r5.setColor(r7);
    L_0x00b9:
        r5 = embedPostDatePaint;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r7 = r7 + r1;
        r1 = (float) r7;
        r5.setTextSize(r1);
        r1 = embedPostDatePaint;
        goto L_0x01d4;
    L_0x00c8:
        r5 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockChannel;
        r11 = "fonts/rmedium.ttf";
        if (r5 == 0) goto L_0x0104;
    L_0x00ce:
        r1 = channelNamePaint;
        if (r1 != 0) goto L_0x00e2;
    L_0x00d2:
        r1 = new android.text.TextPaint;
        r1.<init>(r4);
        channelNamePaint = r1;
        r1 = channelNamePaint;
        r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r11);
        r1.setTypeface(r5);
    L_0x00e2:
        r1 = r6.channelBlock;
        if (r1 != 0) goto L_0x00f0;
    L_0x00e6:
        r1 = channelNamePaint;
        r5 = r21.getTextColor();
        r1.setColor(r5);
        goto L_0x00f6;
    L_0x00f0:
        r1 = channelNamePaint;
        r5 = -1;
        r1.setColor(r5);
    L_0x00f6:
        r1 = channelNamePaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r5 = (float) r5;
        r1.setTextSize(r5);
        r1 = channelNamePaint;
        goto L_0x01d4;
    L_0x0104:
        r5 = r9 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockRelatedArticlesChild;
        if (r5 == 0) goto L_0x016d;
    L_0x0108:
        r5 = r9;
        r5 = (org.telegram.ui.ArticleViewer.TL_pageBlockRelatedArticlesChild) r5;
        r8 = r5.parent;
        r8 = r8.articles;
        r5 = r5.num;
        r5 = r8.get(r5);
        r5 = (org.telegram.tgnet.TLRPC.TL_pageRelatedArticle) r5;
        r5 = r5.title;
        if (r7 != r5) goto L_0x014b;
    L_0x011f:
        r5 = relatedArticleHeaderPaint;
        if (r5 != 0) goto L_0x0133;
    L_0x0123:
        r5 = new android.text.TextPaint;
        r5.<init>(r4);
        relatedArticleHeaderPaint = r5;
        r5 = relatedArticleHeaderPaint;
        r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r11);
        r5.setTypeface(r7);
    L_0x0133:
        r5 = relatedArticleHeaderPaint;
        r7 = r21.getTextColor();
        r5.setColor(r7);
        r5 = relatedArticleHeaderPaint;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r7 = r7 + r1;
        r1 = (float) r7;
        r5.setTextSize(r1);
        r1 = relatedArticleHeaderPaint;
        goto L_0x01d4;
    L_0x014b:
        r5 = relatedArticleTextPaint;
        if (r5 != 0) goto L_0x0156;
    L_0x014f:
        r5 = new android.text.TextPaint;
        r5.<init>(r4);
        relatedArticleTextPaint = r5;
    L_0x0156:
        r5 = relatedArticleTextPaint;
        r7 = r21.getGrayTextColor();
        r5.setColor(r7);
        r5 = relatedArticleTextPaint;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r7 = r7 + r1;
        r1 = (float) r7;
        r5.setTextSize(r1);
        r1 = relatedArticleTextPaint;
        goto L_0x01d4;
    L_0x016d:
        r5 = r6.isListItemBlock(r9);
        if (r5 == 0) goto L_0x01d0;
    L_0x0173:
        if (r7 == 0) goto L_0x01d0;
    L_0x0175:
        r5 = listTextPointerPaint;
        if (r5 != 0) goto L_0x0189;
    L_0x0179:
        r5 = new android.text.TextPaint;
        r5.<init>(r4);
        listTextPointerPaint = r5;
        r5 = listTextPointerPaint;
        r7 = r21.getTextColor();
        r5.setColor(r7);
    L_0x0189:
        r5 = listTextNumPaint;
        if (r5 != 0) goto L_0x019d;
    L_0x018d:
        r5 = new android.text.TextPaint;
        r5.<init>(r4);
        listTextNumPaint = r5;
        r5 = listTextNumPaint;
        r7 = r21.getTextColor();
        r5.setColor(r7);
    L_0x019d:
        r5 = listTextPointerPaint;
        r7 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = r7 + r1;
        r7 = (float) r7;
        r5.setTextSize(r7);
        r5 = listTextNumPaint;
        r7 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = r7 + r1;
        r1 = (float) r7;
        r5.setTextSize(r1);
        r1 = r9 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockListItem;
        if (r1 == 0) goto L_0x01cd;
    L_0x01bb:
        r1 = r9;
        r1 = (org.telegram.ui.ArticleViewer.TL_pageBlockListItem) r1;
        r1 = r1.parent;
        r1 = r1.pageBlockList;
        r1 = r1.ordered;
        if (r1 != 0) goto L_0x01cd;
    L_0x01ca:
        r1 = listTextPointerPaint;
        goto L_0x01d4;
    L_0x01cd:
        r1 = listTextNumPaint;
        goto L_0x01d4;
    L_0x01d0:
        r1 = r6.getTextPaint(r8, r8, r9);
    L_0x01d4:
        r13 = r1;
        if (r29 == 0) goto L_0x020a;
    L_0x01d7:
        r1 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockPullquote;
        if (r1 == 0) goto L_0x01f0;
    L_0x01db:
        r14 = android.text.Layout.Alignment.ALIGN_CENTER;
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r16 = 0;
        r17 = 0;
        r18 = android.text.TextUtils.TruncateAt.END;
        r11 = r0;
        r12 = r13;
        r13 = r19;
        r20 = r29;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        goto L_0x024b;
    L_0x01f0:
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = (float) r1;
        r17 = 0;
        r18 = android.text.TextUtils.TruncateAt.END;
        r11 = r0;
        r12 = r13;
        r13 = r19;
        r14 = r28;
        r16 = r1;
        r20 = r29;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        goto L_0x024b;
    L_0x020a:
        r1 = r0.length();
        r1 = r1 - r4;
        r1 = r0.charAt(r1);
        r5 = 10;
        if (r1 != r5) goto L_0x0220;
    L_0x0217:
        r1 = r0.length();
        r1 = r1 - r4;
        r0 = r0.subSequence(r3, r1);
    L_0x0220:
        r12 = r0;
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockPullquote;
        if (r0 == 0) goto L_0x0236;
    L_0x0225:
        r0 = new android.text.StaticLayout;
        r15 = android.text.Layout.Alignment.ALIGN_CENTER;
        r16 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r17 = 0;
        r18 = 0;
        r11 = r0;
        r14 = r19;
        r11.<init>(r12, r13, r14, r15, r16, r17, r18);
        goto L_0x024b;
    L_0x0236:
        r0 = new android.text.StaticLayout;
        r16 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = (float) r1;
        r18 = 0;
        r11 = r0;
        r14 = r19;
        r15 = r28;
        r17 = r1;
        r11.<init>(r12, r13, r14, r15, r16, r17, r18);
    L_0x024b:
        if (r0 != 0) goto L_0x024e;
    L_0x024d:
        return r10;
    L_0x024e:
        r1 = r0.getText();
        if (r0 == 0) goto L_0x0368;
    L_0x0254:
        r2 = r1 instanceof android.text.Spanned;
        if (r2 == 0) goto L_0x0368;
    L_0x0258:
        r1 = (android.text.Spanned) r1;
        r2 = r1.length();	 Catch:{ Exception -> 0x02ab }
        r5 = org.telegram.ui.Components.AnchorSpan.class;
        r2 = r1.getSpans(r3, r2, r5);	 Catch:{ Exception -> 0x02ab }
        r2 = (org.telegram.ui.Components.AnchorSpan[]) r2;	 Catch:{ Exception -> 0x02ab }
        r5 = r0.getLineCount();	 Catch:{ Exception -> 0x02ab }
        if (r2 == 0) goto L_0x02ab;
    L_0x026c:
        r7 = r2.length;	 Catch:{ Exception -> 0x02ab }
        if (r7 <= 0) goto L_0x02ab;
    L_0x026f:
        r7 = 0;
    L_0x0270:
        r8 = r2.length;	 Catch:{ Exception -> 0x02ab }
        if (r7 >= r8) goto L_0x02ab;
    L_0x0273:
        if (r5 > r4) goto L_0x0287;
    L_0x0275:
        r8 = r30.anchorsOffset;	 Catch:{ Exception -> 0x02ab }
        r9 = r2[r7];	 Catch:{ Exception -> 0x02ab }
        r9 = r9.getName();	 Catch:{ Exception -> 0x02ab }
        r11 = java.lang.Integer.valueOf(r26);	 Catch:{ Exception -> 0x02ab }
        r8.put(r9, r11);	 Catch:{ Exception -> 0x02ab }
        goto L_0x02a8;
    L_0x0287:
        r8 = r30.anchorsOffset;	 Catch:{ Exception -> 0x02ab }
        r9 = r2[r7];	 Catch:{ Exception -> 0x02ab }
        r9 = r9.getName();	 Catch:{ Exception -> 0x02ab }
        r11 = r2[r7];	 Catch:{ Exception -> 0x02ab }
        r11 = r1.getSpanStart(r11);	 Catch:{ Exception -> 0x02ab }
        r11 = r0.getLineForOffset(r11);	 Catch:{ Exception -> 0x02ab }
        r11 = r0.getLineTop(r11);	 Catch:{ Exception -> 0x02ab }
        r11 = r26 + r11;
        r11 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x02ab }
        r8.put(r9, r11);	 Catch:{ Exception -> 0x02ab }
    L_0x02a8:
        r7 = r7 + 1;
        goto L_0x0270;
    L_0x02ab:
        r2 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r5 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r7 = 0;
        r8 = r1.length();	 Catch:{ Exception -> 0x030b }
        r9 = org.telegram.ui.Components.TextPaintWebpageUrlSpan.class;
        r8 = r1.getSpans(r3, r8, r9);	 Catch:{ Exception -> 0x030b }
        r8 = (org.telegram.ui.Components.TextPaintWebpageUrlSpan[]) r8;	 Catch:{ Exception -> 0x030b }
        if (r8 == 0) goto L_0x030b;
    L_0x02be:
        r9 = r8.length;	 Catch:{ Exception -> 0x030b }
        if (r9 <= 0) goto L_0x030b;
    L_0x02c1:
        r9 = new org.telegram.ui.Components.LinkPath;	 Catch:{ Exception -> 0x030b }
        r9.<init>(r4);	 Catch:{ Exception -> 0x030b }
        r9.setAllowReset(r3);	 Catch:{ Exception -> 0x030c }
        r11 = 0;
    L_0x02ca:
        r12 = r8.length;	 Catch:{ Exception -> 0x030c }
        if (r11 >= r12) goto L_0x0307;
    L_0x02cd:
        r12 = r8[r11];	 Catch:{ Exception -> 0x030c }
        r12 = r1.getSpanStart(r12);	 Catch:{ Exception -> 0x030c }
        r13 = r8[r11];	 Catch:{ Exception -> 0x030c }
        r13 = r1.getSpanEnd(r13);	 Catch:{ Exception -> 0x030c }
        r9.setCurrentLayout(r0, r12, r7);	 Catch:{ Exception -> 0x030c }
        r14 = r8[r11];	 Catch:{ Exception -> 0x030c }
        r14 = r14.getTextPaint();	 Catch:{ Exception -> 0x030c }
        if (r14 == 0) goto L_0x02ed;
    L_0x02e4:
        r14 = r8[r11];	 Catch:{ Exception -> 0x030c }
        r14 = r14.getTextPaint();	 Catch:{ Exception -> 0x030c }
        r14 = r14.baselineShift;	 Catch:{ Exception -> 0x030c }
        goto L_0x02ee;
    L_0x02ed:
        r14 = 0;
    L_0x02ee:
        if (r14 == 0) goto L_0x02fd;
    L_0x02f0:
        if (r14 <= 0) goto L_0x02f5;
    L_0x02f2:
        r15 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        goto L_0x02f7;
    L_0x02f5:
        r15 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
    L_0x02f7:
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);	 Catch:{ Exception -> 0x030c }
        r14 = r14 + r15;
        goto L_0x02fe;
    L_0x02fd:
        r14 = 0;
    L_0x02fe:
        r9.setBaselineShift(r14);	 Catch:{ Exception -> 0x030c }
        r0.getSelectionPath(r12, r13, r9);	 Catch:{ Exception -> 0x030c }
        r11 = r11 + 1;
        goto L_0x02ca;
    L_0x0307:
        r9.setAllowReset(r4);	 Catch:{ Exception -> 0x030c }
        goto L_0x030c;
    L_0x030b:
        r9 = r10;
    L_0x030c:
        r8 = r1.length();	 Catch:{ Exception -> 0x0369 }
        r11 = org.telegram.ui.Components.TextPaintMarkSpan.class;
        r8 = r1.getSpans(r3, r8, r11);	 Catch:{ Exception -> 0x0369 }
        r8 = (org.telegram.ui.Components.TextPaintMarkSpan[]) r8;	 Catch:{ Exception -> 0x0369 }
        if (r8 == 0) goto L_0x0369;
    L_0x031a:
        r11 = r8.length;	 Catch:{ Exception -> 0x0369 }
        if (r11 <= 0) goto L_0x0369;
    L_0x031d:
        r11 = new org.telegram.ui.Components.LinkPath;	 Catch:{ Exception -> 0x0369 }
        r11.<init>(r4);	 Catch:{ Exception -> 0x0369 }
        r11.setAllowReset(r3);	 Catch:{ Exception -> 0x0366 }
        r10 = 0;
    L_0x0326:
        r12 = r8.length;	 Catch:{ Exception -> 0x0366 }
        if (r10 >= r12) goto L_0x0363;
    L_0x0329:
        r12 = r8[r10];	 Catch:{ Exception -> 0x0366 }
        r12 = r1.getSpanStart(r12);	 Catch:{ Exception -> 0x0366 }
        r13 = r8[r10];	 Catch:{ Exception -> 0x0366 }
        r13 = r1.getSpanEnd(r13);	 Catch:{ Exception -> 0x0366 }
        r11.setCurrentLayout(r0, r12, r7);	 Catch:{ Exception -> 0x0366 }
        r14 = r8[r10];	 Catch:{ Exception -> 0x0366 }
        r14 = r14.getTextPaint();	 Catch:{ Exception -> 0x0366 }
        if (r14 == 0) goto L_0x0349;
    L_0x0340:
        r14 = r8[r10];	 Catch:{ Exception -> 0x0366 }
        r14 = r14.getTextPaint();	 Catch:{ Exception -> 0x0366 }
        r14 = r14.baselineShift;	 Catch:{ Exception -> 0x0366 }
        goto L_0x034a;
    L_0x0349:
        r14 = 0;
    L_0x034a:
        if (r14 == 0) goto L_0x0359;
    L_0x034c:
        if (r14 <= 0) goto L_0x0351;
    L_0x034e:
        r15 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        goto L_0x0353;
    L_0x0351:
        r15 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
    L_0x0353:
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);	 Catch:{ Exception -> 0x0366 }
        r14 = r14 + r15;
        goto L_0x035a;
    L_0x0359:
        r14 = 0;
    L_0x035a:
        r11.setBaselineShift(r14);	 Catch:{ Exception -> 0x0366 }
        r0.getSelectionPath(r12, r13, r11);	 Catch:{ Exception -> 0x0366 }
        r10 = r10 + 1;
        goto L_0x0326;
    L_0x0363:
        r11.setAllowReset(r4);	 Catch:{ Exception -> 0x0366 }
    L_0x0366:
        r10 = r11;
        goto L_0x0369;
    L_0x0368:
        r9 = r10;
    L_0x0369:
        r1 = new org.telegram.ui.ArticleViewer$DrawingText;
        r1.<init>();
        r1.textLayout = r0;
        r1.textPath = r9;
        r1.markPath = r10;
        return r1;
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

    /* JADX WARNING: Removed duplicated region for block: B:85:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01a9  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x01c7  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x01c0  */
    /* JADX WARNING: Missing block: B:91:0x0199, code skipped:
            if (r0.isShowing() != false) goto L_0x019d;
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
        if (r5 != 0) goto L_0x01ce;
    L_0x000f:
        if (r2 == 0) goto L_0x01ce;
    L_0x0011:
        if (r0 != 0) goto L_0x0015;
    L_0x0013:
        goto L_0x01ce;
    L_0x0015:
        r5 = r0.textLayout;
        r7 = r17.getX();
        r7 = (int) r7;
        r8 = r17.getY();
        r8 = (int) r8;
        r9 = r17.getAction();
        r10 = 1;
        if (r9 != 0) goto L_0x010c;
    L_0x0028:
        r9 = NUM; // 0x4var_ float:2.14748365E9 double:6.548346386E-315;
        r11 = r5.getLineCount();
        r12 = 0;
        r9 = 0;
        r13 = NUM; // 0x4var_ float:2.14748365E9 double:6.548346386E-315;
        r14 = 0;
    L_0x0033:
        if (r9 >= r11) goto L_0x0048;
    L_0x0035:
        r15 = r5.getLineWidth(r9);
        r14 = java.lang.Math.max(r15, r14);
        r15 = r5.getLineLeft(r9);
        r13 = java.lang.Math.min(r15, r13);
        r9 = r9 + 1;
        goto L_0x0033;
    L_0x0048:
        r9 = (float) r7;
        r11 = (float) r3;
        r11 = r11 + r13;
        r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
        if (r13 < 0) goto L_0x019d;
    L_0x004f:
        r11 = r11 + r14;
        r9 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1));
        if (r9 > 0) goto L_0x019d;
    L_0x0054:
        if (r8 < r4) goto L_0x019d;
    L_0x0056:
        r9 = r5.getHeight();
        r9 = r9 + r4;
        if (r8 > r9) goto L_0x019d;
    L_0x005d:
        r1.pressedLinkOwnerLayout = r0;
        r1.pressedLinkOwnerView = r2;
        r1.pressedLayoutY = r4;
        r0 = r5.getText();
        r0 = r0 instanceof android.text.Spannable;
        if (r0 == 0) goto L_0x019d;
    L_0x006b:
        r7 = r7 - r3;
        r8 = r8 - r4;
        r0 = r5.getLineForVertical(r8);	 Catch:{ Exception -> 0x0106 }
        r3 = (float) r7;	 Catch:{ Exception -> 0x0106 }
        r4 = r5.getOffsetForHorizontal(r0, r3);	 Catch:{ Exception -> 0x0106 }
        r7 = r5.getLineLeft(r0);	 Catch:{ Exception -> 0x0106 }
        r8 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1));
        if (r8 > 0) goto L_0x019d;
    L_0x007e:
        r0 = r5.getLineWidth(r0);	 Catch:{ Exception -> 0x0106 }
        r7 = r7 + r0;
        r0 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1));
        if (r0 < 0) goto L_0x019d;
    L_0x0087:
        r0 = r5.getText();	 Catch:{ Exception -> 0x0106 }
        r0 = (android.text.Spannable) r0;	 Catch:{ Exception -> 0x0106 }
        r3 = org.telegram.ui.Components.TextPaintUrlSpan.class;
        r3 = r0.getSpans(r4, r4, r3);	 Catch:{ Exception -> 0x0106 }
        r3 = (org.telegram.ui.Components.TextPaintUrlSpan[]) r3;	 Catch:{ Exception -> 0x0106 }
        if (r3 == 0) goto L_0x019d;
    L_0x0097:
        r4 = r3.length;	 Catch:{ Exception -> 0x0106 }
        if (r4 <= 0) goto L_0x019d;
    L_0x009a:
        r4 = r3[r6];	 Catch:{ Exception -> 0x0106 }
        r1.pressedLink = r4;	 Catch:{ Exception -> 0x0106 }
        r4 = r1.pressedLink;	 Catch:{ Exception -> 0x0106 }
        r4 = r0.getSpanStart(r4);	 Catch:{ Exception -> 0x0106 }
        r7 = r1.pressedLink;	 Catch:{ Exception -> 0x0106 }
        r7 = r0.getSpanEnd(r7);	 Catch:{ Exception -> 0x0106 }
        r8 = r7;
        r7 = r4;
        r4 = 1;
    L_0x00ad:
        r9 = r3.length;	 Catch:{ Exception -> 0x0106 }
        if (r4 >= r9) goto L_0x00c5;
    L_0x00b0:
        r9 = r3[r4];	 Catch:{ Exception -> 0x0106 }
        r11 = r0.getSpanStart(r9);	 Catch:{ Exception -> 0x0106 }
        r13 = r0.getSpanEnd(r9);	 Catch:{ Exception -> 0x0106 }
        if (r7 > r11) goto L_0x00be;
    L_0x00bc:
        if (r13 <= r8) goto L_0x00c2;
    L_0x00be:
        r1.pressedLink = r9;	 Catch:{ Exception -> 0x0106 }
        r7 = r11;
        r8 = r13;
    L_0x00c2:
        r4 = r4 + 1;
        goto L_0x00ad;
    L_0x00c5:
        r0 = r1.urlPath;	 Catch:{ Exception -> 0x0100 }
        r0.setUseRoundRect(r10);	 Catch:{ Exception -> 0x0100 }
        r0 = r1.urlPath;	 Catch:{ Exception -> 0x0100 }
        r0.setCurrentLayout(r5, r7, r12);	 Catch:{ Exception -> 0x0100 }
        r0 = r1.pressedLink;	 Catch:{ Exception -> 0x0100 }
        r0 = r0.getTextPaint();	 Catch:{ Exception -> 0x0100 }
        if (r0 == 0) goto L_0x00e0;
    L_0x00d7:
        r0 = r1.pressedLink;	 Catch:{ Exception -> 0x0100 }
        r0 = r0.getTextPaint();	 Catch:{ Exception -> 0x0100 }
        r0 = r0.baselineShift;	 Catch:{ Exception -> 0x0100 }
        goto L_0x00e1;
    L_0x00e0:
        r0 = 0;
    L_0x00e1:
        r3 = r1.urlPath;	 Catch:{ Exception -> 0x0100 }
        if (r0 == 0) goto L_0x00f2;
    L_0x00e5:
        if (r0 <= 0) goto L_0x00ea;
    L_0x00e7:
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        goto L_0x00ec;
    L_0x00ea:
        r4 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
    L_0x00ec:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0100 }
        r0 = r0 + r4;
        goto L_0x00f3;
    L_0x00f2:
        r0 = 0;
    L_0x00f3:
        r3.setBaselineShift(r0);	 Catch:{ Exception -> 0x0100 }
        r0 = r1.urlPath;	 Catch:{ Exception -> 0x0100 }
        r5.getSelectionPath(r7, r8, r0);	 Catch:{ Exception -> 0x0100 }
        r18.invalidate();	 Catch:{ Exception -> 0x0100 }
        goto L_0x019d;
    L_0x0100:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0106 }
        goto L_0x019d;
    L_0x0106:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x019d;
    L_0x010c:
        r0 = r17.getAction();
        if (r0 != r10) goto L_0x018a;
    L_0x0112:
        r0 = r1.pressedLink;
        if (r0 == 0) goto L_0x019d;
    L_0x0116:
        r0 = r0.getUrl();
        if (r0 == 0) goto L_0x019b;
    L_0x011c:
        r3 = r1.linkSheet;
        r4 = 0;
        if (r3 == 0) goto L_0x0126;
    L_0x0121:
        r3.dismiss();
        r1.linkSheet = r4;
    L_0x0126:
        r3 = 35;
        r3 = r0.lastIndexOf(r3);
        r5 = -1;
        if (r3 == r5) goto L_0x017c;
    L_0x012f:
        r4 = r1.currentPage;
        r4 = r4.cached_page;
        r4 = r4.url;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x0146;
    L_0x013b:
        r4 = r1.currentPage;
        r4 = r4.cached_page;
        r4 = r4.url;
        r4 = r4.toLowerCase();
        goto L_0x014e;
    L_0x0146:
        r4 = r1.currentPage;
        r4 = r4.url;
        r4 = r4.toLowerCase();
    L_0x014e:
        r3 = r3 + r10;
        r3 = r0.substring(r3);	 Catch:{ Exception -> 0x015a }
        r5 = "UTF-8";
        r3 = java.net.URLDecoder.decode(r3, r5);	 Catch:{ Exception -> 0x015a }
        goto L_0x015c;
    L_0x015a:
        r3 = "";
    L_0x015c:
        r0 = r0.toLowerCase();
        r0 = r0.contains(r4);
        if (r0 == 0) goto L_0x017d;
    L_0x0166:
        r0 = android.text.TextUtils.isEmpty(r3);
        if (r0 == 0) goto L_0x0177;
    L_0x016c:
        r0 = r1.layoutManager;
        r0 = r0[r6];
        r0.scrollToPositionWithOffset(r6, r6);
        r16.checkScrollAnimated();
        goto L_0x017a;
    L_0x0177:
        r1.scrollToAnchor(r3);
    L_0x017a:
        r0 = 1;
        goto L_0x017e;
    L_0x017c:
        r3 = r4;
    L_0x017d:
        r0 = 0;
    L_0x017e:
        if (r0 != 0) goto L_0x019b;
    L_0x0180:
        r0 = r1.pressedLink;
        r0 = r0.getUrl();
        r1.openWebpageUrl(r0, r3);
        goto L_0x019b;
    L_0x018a:
        r0 = r17.getAction();
        r3 = 3;
        if (r0 != r3) goto L_0x019d;
    L_0x0191:
        r0 = r1.popupWindow;
        if (r0 == 0) goto L_0x019b;
    L_0x0195:
        r0 = r0.isShowing();
        if (r0 != 0) goto L_0x019d;
    L_0x019b:
        r0 = 1;
        goto L_0x019e;
    L_0x019d:
        r0 = 0;
    L_0x019e:
        if (r0 == 0) goto L_0x01a3;
    L_0x01a0:
        r16.removePressedLink();
    L_0x01a3:
        r0 = r17.getAction();
        if (r0 != 0) goto L_0x01ac;
    L_0x01a9:
        r16.startCheckLongPress();
    L_0x01ac:
        r0 = r17.getAction();
        if (r0 == 0) goto L_0x01bc;
    L_0x01b2:
        r0 = r17.getAction();
        r3 = 2;
        if (r0 == r3) goto L_0x01bc;
    L_0x01b9:
        r16.cancelCheckLongPress();
    L_0x01bc:
        r0 = r2 instanceof org.telegram.ui.ArticleViewer.BlockDetailsCell;
        if (r0 == 0) goto L_0x01c7;
    L_0x01c0:
        r0 = r1.pressedLink;
        if (r0 == 0) goto L_0x01c5;
    L_0x01c4:
        goto L_0x01c6;
    L_0x01c5:
        r10 = 0;
    L_0x01c6:
        return r10;
    L_0x01c7:
        r0 = r1.pressedLinkOwnerLayout;
        if (r0 == 0) goto L_0x01cc;
    L_0x01cb:
        goto L_0x01cd;
    L_0x01cc:
        r10 = 0;
    L_0x01cd:
        return r10;
    L_0x01ce:
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
                    this.radialProgressViews[i3].setProgress(((Float) objArr[1]).floatValue(), true);
                }
                i3++;
            }
        } else if (i == NotificationCenter.emojiDidLoad) {
            TextView textView = this.captionTextView;
            if (textView != null) {
                textView.invalidate();
            }
        } else if (i == NotificationCenter.needSetDayNightTheme) {
            if (this.nightModeEnabled && this.selectedColor != 2 && this.adapter != null) {
                updatePaintColors();
                while (i3 < this.listView.length) {
                    this.adapter[i3].notifyDataSetChanged();
                    i3++;
                }
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

    private void updatePaintSize() {
        int i = 0;
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_size", this.selectedFontSize).commit();
        while (i < 2) {
            this.adapter[i].notifyDataSetChanged();
            i++;
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

    private int getSelectedColor() {
        int i = this.selectedColor;
        if (this.nightModeEnabled && i != 2) {
            if (Theme.selectedAutoNightType == 0) {
                int i2 = Calendar.getInstance().get(11);
                if (i2 >= 22 && i2 <= 24) {
                    return 2;
                }
                if (i2 >= 0 && i2 <= 6) {
                    return 2;
                }
            } else if (Theme.isCurrentThemeNight()) {
                return 2;
            }
        }
        return i;
    }

    private void updatePaintColors() {
        int i = 0;
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_color", this.selectedColor).commit();
        int selectedColor = getSelectedColor();
        RecyclerListView[] recyclerListViewArr;
        if (selectedColor == 0) {
            this.backgroundPaint.setColor(-1);
            while (true) {
                recyclerListViewArr = this.listView;
                if (i >= recyclerListViewArr.length) {
                    break;
                }
                recyclerListViewArr[i].setGlowColor(-657673);
                i++;
            }
        } else if (selectedColor == 1) {
            this.backgroundPaint.setColor(-659492);
            while (true) {
                recyclerListViewArr = this.listView;
                if (i >= recyclerListViewArr.length) {
                    break;
                }
                recyclerListViewArr[i].setGlowColor(-659492);
                i++;
            }
        } else if (selectedColor == 2) {
            this.backgroundPaint.setColor(-15461356);
            while (true) {
                recyclerListViewArr = this.listView;
                if (i >= recyclerListViewArr.length) {
                    break;
                }
                recyclerListViewArr[i].setGlowColor(-15461356);
                i++;
            }
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
            if (this.channelBlock == null) {
                textPaint.setColor(getTextColor());
            } else {
                textPaint.setColor(-1);
            }
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
            if (selectedColor == 0) {
                textPaint.setColor(-7366752);
            } else {
                textPaint.setColor(getGrayTextColor());
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
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme);
        if (this.parentActivity == activity2) {
            updatePaintColors();
            return;
        }
        this.parentActivity = activity2;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0);
        this.selectedFontSize = sharedPreferences.getInt("font_size", 2);
        this.selectedFont = sharedPreferences.getInt("font_type", 0);
        this.selectedColor = sharedPreferences.getInt("font_color", 0);
        this.nightModeEnabled = sharedPreferences.getBoolean("nightModeEnabled", false);
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
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer$AnonymousClass4.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
            }
        };
        this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
        this.containerView.setFitsSystemWindows(true);
        if (VERSION.SDK_INT >= 21) {
            this.containerView.setOnApplyWindowInsetsListener(new -$$Lambda$ArticleViewer$vp7UXKOYIsKBRVoLXvi04N7sU1U(this));
        }
        this.containerView.setSystemUiVisibility(1028);
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
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    if (recyclerView.getChildCount() != 0) {
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
        this.headerView.setOnTouchListener(-$$Lambda$ArticleViewer$_HFJyvtR4MvWBLU-ptyI8bKyRKk.INSTANCE);
        this.headerView.setWillNotDraw(false);
        this.containerView.addView(this.headerView, LayoutHelper.createFrame(-1, 56.0f));
        this.backButton = new ImageView(activity2);
        this.backButton.setScaleType(ScaleType.CENTER);
        this.backDrawable = new BackDrawable(false);
        this.backDrawable.setAnimationTime(200.0f);
        this.backDrawable.setColor(-5000269);
        this.backDrawable.setRotated(false);
        this.backButton.setImageDrawable(this.backDrawable);
        this.backButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.headerView.addView(this.backButton, LayoutHelper.createFrame(54, 56.0f));
        this.backButton.setOnClickListener(new -$$Lambda$ArticleViewer$-BOkadygLKaLCM7xHgqDylRJDCI(this));
        this.backButton.setContentDescription(LocaleController.getString("AccDescrGoBack", NUM));
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
        this.lineProgressTickRunnable = new -$$Lambda$ArticleViewer$ldm2w9awvjqovar_LBYx2CGXsH0c(this);
        View linearLayout = new LinearLayout(this.parentActivity);
        linearLayout.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        linearLayout.setOrientation(1);
        int i2 = 0;
        while (i2 < 3) {
            this.colorCells[i2] = new ColorCell(this.parentActivity);
            if (i2 == 0) {
                this.nightModeImageView = new ImageView(this.parentActivity);
                this.nightModeImageView.setScaleType(ScaleType.CENTER);
                this.nightModeImageView.setImageResource(NUM);
                ImageView imageView = this.nightModeImageView;
                int i3 = (!this.nightModeEnabled || this.selectedColor == 2) ? -3355444 : -15428119;
                imageView.setColorFilter(new PorterDuffColorFilter(i3, Mode.MULTIPLY));
                this.nightModeImageView.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
                this.colorCells[i2].addView(this.nightModeImageView, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : 5) | 48));
                this.nightModeImageView.setOnClickListener(new -$$Lambda$ArticleViewer$R_6rwlwqps7RWpLQ78R9g2TJAag(this));
                this.colorCells[i2].setTextAndColor(LocaleController.getString("ColorWhite", NUM), -1);
            } else if (i2 == 1) {
                this.colorCells[i2].setTextAndColor(LocaleController.getString("ColorSepia", NUM), -1382967);
            } else if (i2 == 2) {
                this.colorCells[i2].setTextAndColor(LocaleController.getString("ColorDark", NUM), -14474461);
            }
            this.colorCells[i2].select(i2 == this.selectedColor);
            this.colorCells[i2].setTag(Integer.valueOf(i2));
            this.colorCells[i2].setOnClickListener(new -$$Lambda$ArticleViewer$H1Bc9M26tuZkWwbtuUTUyFFEPMA(this));
            linearLayout.addView(this.colorCells[i2], LayoutHelper.createLinear(-1, 50));
            i2++;
        }
        updateNightModeButton();
        View view = new View(this.parentActivity);
        view.setBackgroundColor(-2039584);
        linearLayout.addView(view, LayoutHelper.createLinear(-1, 1, 15.0f, 4.0f, 15.0f, 4.0f));
        view.getLayoutParams().height = 1;
        int i4 = 0;
        while (i4 < 2) {
            this.fontCells[i4] = new FontCell(this, this.parentActivity);
            if (i4 == 0) {
                this.fontCells[i4].setTextAndTypeface("Roboto", Typeface.DEFAULT);
            } else if (i4 == 1) {
                this.fontCells[i4].setTextAndTypeface("Serif", Typeface.SERIF);
            }
            this.fontCells[i4].select(i4 == this.selectedFont);
            this.fontCells[i4].setTag(Integer.valueOf(i4));
            this.fontCells[i4].setOnClickListener(new -$$Lambda$ArticleViewer$bbOV83-_XjOP0aIG8YMQY9bn2Gc(this));
            linearLayout.addView(this.fontCells[i4], LayoutHelper.createLinear(-1, 50));
            i4++;
        }
        view = new View(this.parentActivity);
        view.setBackgroundColor(-2039584);
        linearLayout.addView(view, LayoutHelper.createLinear(-1, 1, 15.0f, 4.0f, 15.0f, 4.0f));
        view.getLayoutParams().height = 1;
        TextView textView = new TextView(this.parentActivity);
        textView.setTextColor(-14606047);
        textView.setTextSize(1, 16.0f);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        textView.setText(LocaleController.getString("FontSize", NUM));
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 17, 12, 17, 0));
        linearLayout.addView(new SizeChooseView(this.parentActivity), LayoutHelper.createLinear(-1, 38, 0.0f, 0.0f, 0.0f, 1.0f));
        this.settingsButton = new ActionBarMenuItem(this.parentActivity, null, NUM, -1);
        this.settingsButton.setPopupAnimationEnabled(false);
        this.settingsButton.setLayoutInScreen(true);
        textView = new TextView(this.parentActivity);
        textView.setTextSize(1, 18.0f);
        textView.setText("Aa");
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextColor(-5000269);
        textView.setGravity(17);
        textView.setImportantForAccessibility(2);
        this.settingsButton.addView(textView, LayoutHelper.createFrame(-1, -1.0f));
        this.settingsButton.addSubItem(linearLayout, AndroidUtilities.dp(220.0f), -2);
        this.settingsButton.redrawPopup(-1);
        this.settingsButton.setContentDescription(LocaleController.getString("Settings", NUM));
        this.headerView.addView(this.settingsButton, LayoutHelper.createFrame(48, 56.0f, 53, 0.0f, 0.0f, 56.0f, 0.0f));
        this.shareContainer = new FrameLayout(activity2);
        this.headerView.addView(this.shareContainer, LayoutHelper.createFrame(48, 56, 53));
        this.shareContainer.setOnClickListener(new -$$Lambda$ArticleViewer$T6B8FEUYsYpA0hIBLlSeg6NN1X0(this));
        this.shareButton = new ImageView(activity2);
        this.shareButton.setScaleType(ScaleType.CENTER);
        this.shareButton.setImageResource(NUM);
        this.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.shareButton.setContentDescription(LocaleController.getString("ShareFile", NUM));
        this.shareContainer.addView(this.shareButton, LayoutHelper.createFrame(48, 56.0f));
        this.progressView = new ContextProgressView(activity2, 2);
        this.progressView.setVisibility(8);
        this.shareContainer.addView(this.progressView, LayoutHelper.createFrame(48, 56.0f));
        this.windowLayoutParams = new LayoutParams();
        LayoutParams layoutParams = this.windowLayoutParams;
        layoutParams.height = -1;
        layoutParams.format = -3;
        layoutParams.width = -1;
        layoutParams.gravity = 51;
        layoutParams.type = 99;
        i4 = VERSION.SDK_INT;
        if (i4 >= 21) {
            layoutParams.flags = -NUM;
            if (i4 >= 28) {
                layoutParams.layoutInDisplayCutoutMode = 1;
            }
        } else {
            layoutParams.flags = 8;
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
                if (i == -1) {
                    ArticleViewer.this.closePhoto(true);
                } else if (i == 1) {
                    if (VERSION.SDK_INT >= 23) {
                        if (ArticleViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                            ArticleViewer.this.parentActivity.requestPermissions(new String[]{r2}, 4);
                            return;
                        }
                    }
                    ArticleViewer articleViewer = ArticleViewer.this;
                    File access$8800 = articleViewer.getMediaFile(articleViewer.currentIndex);
                    if (access$8800 == null || !access$8800.exists()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ArticleViewer.this.parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                        builder.setMessage(LocaleController.getString("PleaseDownload", NUM));
                        ArticleViewer.this.showDialog(builder.create());
                    } else {
                        String file = access$8800.toString();
                        Activity access$2500 = ArticleViewer.this.parentActivity;
                        ArticleViewer articleViewer2 = ArticleViewer.this;
                        MediaController.saveFile(file, access$2500, articleViewer2.isMediaVideo(articleViewer2.currentIndex), null, null);
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
                }
            }

            public boolean canOpenMenu() {
                ArticleViewer articleViewer = ArticleViewer.this;
                File access$8800 = articleViewer.getMediaFile(articleViewer.currentIndex);
                return access$8800 != null && access$8800.exists();
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.addItem(2, NUM);
        this.menuItem = createMenu.addItem(0, NUM);
        this.menuItem.setLayoutInScreen(true);
        this.menuItem.addSubItem(3, NUM, LocaleController.getString("OpenInExternalApp", NUM)).setColors(-328966, -328966);
        this.menuItem.addSubItem(1, NUM, LocaleController.getString("SaveToGallery", NUM)).setColors(-328966, -328966);
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
        this.videoPlayerSeekbar.setDelegate(new -$$Lambda$ArticleViewer$_GVmz26hlBkXHajDH6vxhTab9Js(this));
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
        this.videoPlayButton.setOnClickListener(new -$$Lambda$ArticleViewer$lPU_BVNRUajP3efspuXk-ru9iJ0(this));
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
        updatePaintColors();
    }

    public /* synthetic */ WindowInsets lambda$setParentActivity$7$ArticleViewer(View view, WindowInsets windowInsets) {
        WindowInsets windowInsets2 = (WindowInsets) this.lastInsets;
        this.lastInsets = windowInsets;
        if (windowInsets2 == null || !windowInsets2.toString().equals(windowInsets.toString())) {
            this.windowView.requestLayout();
        }
        if (VERSION.SDK_INT >= 28) {
            DisplayCutout displayCutout = this.parentActivity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
            if (displayCutout != null) {
                List boundingRects = displayCutout.getBoundingRects();
                if (!(boundingRects == null || boundingRects.isEmpty())) {
                    boolean z = false;
                    if (((Rect) boundingRects.get(0)).height() != 0) {
                        z = true;
                    }
                    this.hasCutout = z;
                }
            }
        }
        return windowInsets.consumeSystemWindowInsets();
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

    public /* synthetic */ void lambda$setParentActivity$13$ArticleViewer(View view) {
        close(true, true);
    }

    public /* synthetic */ void lambda$setParentActivity$14$ArticleViewer() {
        float currentProgress = 0.7f - this.lineProgressView.getCurrentProgress();
        if (currentProgress > 0.0f) {
            float f = currentProgress < 0.25f ? 0.01f : 0.02f;
            LineProgressView lineProgressView = this.lineProgressView;
            lineProgressView.setProgress(lineProgressView.getCurrentProgress() + f, true);
            AndroidUtilities.runOnUIThread(this.lineProgressTickRunnable, 100);
        }
    }

    public /* synthetic */ void lambda$setParentActivity$15$ArticleViewer(View view) {
        this.nightModeEnabled ^= 1;
        int i = 0;
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putBoolean("nightModeEnabled", this.nightModeEnabled).commit();
        updateNightModeButton();
        updatePaintColors();
        while (i < this.listView.length) {
            this.adapter[i].notifyDataSetChanged();
            i++;
        }
        if (this.nightModeEnabled) {
            showNightModeHint();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$16$ArticleViewer(View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        this.selectedColor = intValue;
        int i = 0;
        while (i < 3) {
            this.colorCells[i].select(i == intValue);
            i++;
        }
        updateNightModeButton();
        updatePaintColors();
        for (int i2 = 0; i2 < this.listView.length; i2++) {
            this.adapter[i2].notifyDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$17$ArticleViewer(View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        this.selectedFont = intValue;
        int i = 0;
        while (i < 2) {
            this.fontCells[i].select(i == intValue);
            i++;
        }
        updatePaintFonts();
        for (int i2 = 0; i2 < this.listView.length; i2++) {
            this.adapter[i2].notifyDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$setParentActivity$18$ArticleViewer(View view) {
        WebPage webPage = this.currentPage;
        if (webPage != null) {
            Activity activity = this.parentActivity;
            if (activity != null) {
                String str = webPage.url;
                showDialog(new ShareAlert(activity, null, str, false, str, true));
            }
        }
    }

    public /* synthetic */ void lambda$setParentActivity$19$ArticleViewer(float f) {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.seekTo((long) ((int) (f * ((float) videoPlayer.getDuration()))));
        }
    }

    public /* synthetic */ void lambda$setParentActivity$20$ArticleViewer(View view) {
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

    private void showNightModeHint() {
        Activity activity = this.parentActivity;
        if (activity != null && this.nightModeHintView == null && this.nightModeEnabled) {
            this.nightModeHintView = new FrameLayout(activity);
            this.nightModeHintView.setBackgroundColor(-13421773);
            this.containerView.addView(this.nightModeHintView, LayoutHelper.createFrame(-1, -2, 83));
            ImageView imageView = new ImageView(this.parentActivity);
            imageView.setScaleType(ScaleType.CENTER);
            imageView.setImageResource(NUM);
            int i = 5;
            this.nightModeHintView.addView(imageView, LayoutHelper.createFrame(56, 56, (LocaleController.isRTL ? 5 : 3) | 16));
            TextView textView = new TextView(this.parentActivity);
            textView.setText(LocaleController.getString("InstantViewNightMode", NUM));
            textView.setTextColor(-1);
            textView.setTextSize(1, 15.0f);
            FrameLayout frameLayout = this.nightModeHintView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            int i2 = i | 48;
            int i3 = 10;
            float f = (float) (LocaleController.isRTL ? 10 : 56);
            if (LocaleController.isRTL) {
                i3 = 56;
            }
            frameLayout.addView(textView, LayoutHelper.createFrame(-1, -1.0f, i2, f, 11.0f, (float) i3, 12.0f));
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.nightModeHintView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(100.0f), 0.0f});
            animatorSet.playTogether(animatorArr);
            animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$12$IKrvar_A2pjQihanm36OwYzYcJGQ(this), 3000);
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$12() {
                    AnimatorSet animatorSet = new AnimatorSet();
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.nightModeHintView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(100.0f)});
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
                    animatorSet.setDuration(250);
                    animatorSet.start();
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
            ValueAnimator duration = ValueAnimator.ofObject(new IntEvaluator(), new Object[]{Integer.valueOf(this.currentHeaderHeight), Integer.valueOf(AndroidUtilities.dp(56.0f))}).setDuration(180);
            duration.setInterpolator(new DecelerateInterpolator());
            duration.addUpdateListener(new -$$Lambda$ArticleViewer$sxYzbPn-gQQmb-6tSvBl0G3LOAw(this));
            duration.start();
        }
    }

    public /* synthetic */ void lambda$checkScrollAnimated$21$ArticleViewer(ValueAnimator valueAnimator) {
        setCurrentHeaderHeight(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    private void setCurrentHeaderHeight(int i) {
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
        this.shareContainer.setScaleX(f2);
        this.shareContainer.setScaleY(f2);
        this.settingsButton.setScaleX(f2);
        this.settingsButton.setScaleY(f2);
        this.titleTextView.setScaleX(f2);
        this.titleTextView.setScaleY(f2);
        this.lineProgressView.setScaleY(f3);
        this.shareContainer.setTranslationY((float) ((dp - this.currentHeaderHeight) / 2));
        this.settingsButton.setTranslationY((float) ((dp - this.currentHeaderHeight) / 2));
        this.titleTextView.setTranslationY((float) ((dp - this.currentHeaderHeight) / 2));
        this.headerView.setTranslationY((float) (this.currentHeaderHeight - dp));
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

    /* JADX WARNING: Removed duplicated region for block: B:42:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00fb  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x010c  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x018e  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0147  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x021f  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x015e A:{Catch:{ Exception -> 0x0189 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:62:0x0158 */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Can't wrap try/catch for region: R(6:58|(2:60|61)|62|63|(2:65|(1:67))|68) */
    /* JADX WARNING: Missing block: B:69:0x0189, code skipped:
            r12 = move-exception;
     */
    /* JADX WARNING: Missing block: B:70:0x018a, code skipped:
            org.telegram.messenger.FileLog.e(r12);
     */
    /* JADX WARNING: Missing block: B:71:0x018d, code skipped:
            return false;
     */
    private boolean open(org.telegram.messenger.MessageObject r12, org.telegram.tgnet.TLRPC.WebPage r13, java.lang.String r14, boolean r15) {
        /*
        r11 = this;
        r0 = r11.parentActivity;
        r1 = 0;
        if (r0 == 0) goto L_0x0225;
    L_0x0005:
        r0 = r11.isVisible;
        if (r0 == 0) goto L_0x000d;
    L_0x0009:
        r0 = r11.collapsed;
        if (r0 == 0) goto L_0x0225;
    L_0x000d:
        if (r12 != 0) goto L_0x0013;
    L_0x000f:
        if (r13 != 0) goto L_0x0013;
    L_0x0011:
        goto L_0x0225;
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
        r13 = r11.backDrawable;
        r0 = 0;
        r13.setRotation(r0, r1);
        r13 = r11.containerView;
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
        if (r15 == 0) goto L_0x0103;
    L_0x00fb:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r11.setCurrentHeaderHeight(r2);
        goto L_0x0106;
    L_0x0103:
        r11.checkScrollAnimated();
    L_0x0106:
        r2 = r11.addPageToStack(r7, r14, r1);
        if (r15 == 0) goto L_0x013f;
    L_0x010c:
        if (r2 != 0) goto L_0x0112;
    L_0x010e:
        if (r14 == 0) goto L_0x0112;
    L_0x0110:
        r9 = r14;
        goto L_0x0113;
    L_0x0112:
        r9 = r3;
    L_0x0113:
        r14 = new org.telegram.tgnet.TLRPC$TL_messages_getWebPage;
        r14.<init>();
        r15 = r7.url;
        r14.url = r15;
        r15 = r7.cached_page;
        r2 = r15 instanceof org.telegram.tgnet.TLRPC.TL_pagePart_layer82;
        if (r2 != 0) goto L_0x012c;
    L_0x0122:
        r15 = r15.part;
        if (r15 == 0) goto L_0x0127;
    L_0x0126:
        goto L_0x012c;
    L_0x0127:
        r15 = r7.hash;
        r14.hash = r15;
        goto L_0x012e;
    L_0x012c:
        r14.hash = r1;
    L_0x012e:
        r10 = org.telegram.messenger.UserConfig.selectedAccount;
        r15 = org.telegram.tgnet.ConnectionsManager.getInstance(r10);
        r2 = new org.telegram.ui.-$$Lambda$ArticleViewer$z8m1_SXnv-7lkJNDb6qlQTHMjFc;
        r5 = r2;
        r6 = r11;
        r8 = r12;
        r5.<init>(r6, r7, r8, r9, r10);
        r15.sendRequest(r14, r2);
    L_0x013f:
        r11.lastInsets = r3;
        r12 = r11.isVisible;
        r14 = "window";
        if (r12 != 0) goto L_0x018e;
    L_0x0147:
        r12 = r11.parentActivity;
        r12 = r12.getSystemService(r14);
        r12 = (android.view.WindowManager) r12;
        r14 = r11.attachedToWindow;
        if (r14 == 0) goto L_0x0158;
    L_0x0153:
        r14 = r11.windowView;	 Catch:{ Exception -> 0x0158 }
        r12.removeView(r14);	 Catch:{ Exception -> 0x0158 }
    L_0x0158:
        r14 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0189 }
        r15 = 21;
        if (r14 < r15) goto L_0x016f;
    L_0x015e:
        r14 = r11.windowLayoutParams;	 Catch:{ Exception -> 0x0189 }
        r15 = -NUM; // 0xfffffffvar_ float:-9.2194E-41 double:NaN;
        r14.flags = r15;	 Catch:{ Exception -> 0x0189 }
        r14 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0189 }
        r15 = 28;
        if (r14 < r15) goto L_0x016f;
    L_0x016b:
        r14 = r11.windowLayoutParams;	 Catch:{ Exception -> 0x0189 }
        r14.layoutInDisplayCutoutMode = r4;	 Catch:{ Exception -> 0x0189 }
    L_0x016f:
        r14 = r11.windowLayoutParams;	 Catch:{ Exception -> 0x0189 }
        r15 = r14.flags;	 Catch:{ Exception -> 0x0189 }
        r15 = r15 | 1032;
        r14.flags = r15;	 Catch:{ Exception -> 0x0189 }
        r14 = r11.windowView;	 Catch:{ Exception -> 0x0189 }
        r14.setFocusable(r1);	 Catch:{ Exception -> 0x0189 }
        r14 = r11.containerView;	 Catch:{ Exception -> 0x0189 }
        r14.setFocusable(r1);	 Catch:{ Exception -> 0x0189 }
        r14 = r11.windowView;	 Catch:{ Exception -> 0x0189 }
        r15 = r11.windowLayoutParams;	 Catch:{ Exception -> 0x0189 }
        r12.addView(r14, r15);	 Catch:{ Exception -> 0x0189 }
        goto L_0x01a5;
    L_0x0189:
        r12 = move-exception;
        org.telegram.messenger.FileLog.e(r12);
        return r1;
    L_0x018e:
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
    L_0x01a5:
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
        r13 = new org.telegram.ui.-$$Lambda$ArticleViewer$DFl-wS8sRaC6wF2T9h1iNHqI_KI;
        r13.<init>(r11);
        r11.animationEndRunnable = r13;
        r13 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r12.setDuration(r13);
        r13 = r11.interpolator;
        r12.setInterpolator(r13);
        r13 = new org.telegram.ui.ArticleViewer$13;
        r13.<init>();
        r12.addListener(r13);
        r13 = java.lang.System.currentTimeMillis();
        r11.transitionAnimationStartTime = r13;
        r13 = new org.telegram.ui.-$$Lambda$ArticleViewer$AbuoJEBmR86qlkjB83QNafOTaB8;
        r13.<init>(r11, r12);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r13);
        r12 = android.os.Build.VERSION.SDK_INT;
        r13 = 18;
        if (r12 < r13) goto L_0x0224;
    L_0x021f:
        r12 = r11.containerView;
        r12.setLayerType(r5, r3);
    L_0x0224:
        return r4;
    L_0x0225:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.open(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, java.lang.String, boolean):boolean");
    }

    public /* synthetic */ void lambda$open$23$ArticleViewer(WebPage webPage, MessageObject messageObject, String str, int i, TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_webPage) {
            TL_webPage tL_webPage = (TL_webPage) tLObject;
            if (tL_webPage.cached_page != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$l3_iiPHXy4_uEQn5oag7li6c_dQ(this, webPage, tL_webPage, messageObject, str));
                LongSparseArray longSparseArray = new LongSparseArray(1);
                longSparseArray.put(tL_webPage.id, tL_webPage);
                MessagesStorage.getInstance(i).putWebPages(longSparseArray);
            }
        }
    }

    public /* synthetic */ void lambda$null$22$ArticleViewer(WebPage webPage, TL_webPage tL_webPage, MessageObject messageObject, String str) {
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
                updateInterfaceForCurrentPage(0);
                if (str != null) {
                    scrollToAnchor(str);
                }
            }
        }
    }

    public /* synthetic */ void lambda$open$24$ArticleViewer() {
        FrameLayout frameLayout = this.containerView;
        if (frameLayout != null && this.windowView != null) {
            if (VERSION.SDK_INT >= 18) {
                frameLayout.setLayerType(0, null);
            }
            this.animationInProgress = 0;
            AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
        }
    }

    public /* synthetic */ void lambda$open$25$ArticleViewer(AnimatorSet animatorSet) {
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
            this.shareContainer.setEnabled(false);
            animatorSet2 = this.progressViewAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.shareButton, View.SCALE_X, new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.shareButton, View.SCALE_Y, new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.shareButton, View.ALPHA, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f});
            animatorSet2.playTogether(animatorArr);
        } else {
            this.shareButton.setVisibility(0);
            this.shareContainer.setEnabled(true);
            animatorSet2 = this.progressViewAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.shareButton, View.SCALE_X, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.shareButton, View.SCALE_Y, new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.shareButton, View.ALPHA, new float[]{1.0f});
            animatorSet2.playTogether(animatorArr);
        }
        this.progressViewAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animator)) {
                    if (z2) {
                        ArticleViewer.this.shareButton.setVisibility(4);
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
            animatorArr[9] = ObjectAnimator.ofFloat(this.shareContainer, View.SCALE_X, new float[]{1.0f});
            animatorArr[10] = ObjectAnimator.ofFloat(this.shareContainer, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[11] = ObjectAnimator.ofFloat(this.shareContainer, View.SCALE_Y, new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
            this.collapsed = true;
            this.animationInProgress = 2;
            this.animationEndRunnable = new -$$Lambda$ArticleViewer$o2CofdEeGng_-h9rKJjob7RhMPo(this);
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

    public /* synthetic */ void lambda$collapse$26$ArticleViewer() {
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
            animatorArr[9] = ObjectAnimator.ofFloat(this.shareContainer, View.SCALE_X, new float[]{1.0f});
            animatorArr[10] = ObjectAnimator.ofFloat(this.shareContainer, View.TRANSLATION_Y, new float[]{0.0f});
            animatorArr[11] = ObjectAnimator.ofFloat(this.shareContainer, View.SCALE_Y, new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
            this.collapsed = false;
            this.animationInProgress = 2;
            this.animationEndRunnable = new -$$Lambda$ArticleViewer$C6ZUTa2rGrDYlXYood81SR54cQg(this);
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

    public /* synthetic */ void lambda$uncollapse$27$ArticleViewer() {
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
            if (this.isPhotoVisible) {
                closePhoto(z2 ^ 1);
                if (!z2) {
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
            if (!z || z2 || !removeLastPageFromStack()) {
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
                this.animationEndRunnable = new -$$Lambda$ArticleViewer$d55YwG7qykKR-6ZPmUqu81Dn_rc(this);
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

    public /* synthetic */ void lambda$close$28$ArticleViewer() {
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
        this.containerView.post(new -$$Lambda$ArticleViewer$7Sl00UfgZjsIK9QPR7pwpUGqEew(this));
    }

    public /* synthetic */ void lambda$onClosed$29$ArticleViewer() {
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
            ConnectionsManager.getInstance(i).sendRequest(tL_contacts_resolveUsername, new -$$Lambda$ArticleViewer$OTnRhboivaBmKveLaMpnewhsYJg(this, webpageAdapter, i, blockChannelCell));
        }
    }

    public /* synthetic */ void lambda$loadChannel$31$ArticleViewer(WebpageAdapter webpageAdapter, int i, BlockChannelCell blockChannelCell, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$OU-4EBuNPSDwj-f4lawwZA9jcMI(this, webpageAdapter, tL_error, tLObject, i, blockChannelCell));
    }

    public /* synthetic */ void lambda$null$30$ArticleViewer(WebpageAdapter webpageAdapter, TL_error tL_error, TLObject tLObject, int i, BlockChannelCell blockChannelCell) {
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
        ConnectionsManager.getInstance(i).sendRequest(tL_channels_joinChannel, new -$$Lambda$ArticleViewer$tOg7TGz-CemIZKoQ4SmkPVL-N7w(this, blockChannelCell, i, tL_channels_joinChannel, chat));
    }

    public /* synthetic */ void lambda$joinChannel$35$ArticleViewer(BlockChannelCell blockChannelCell, int i, TL_channels_joinChannel tL_channels_joinChannel, Chat chat, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$-hnQGBrCa7S7qlSM2IdUcfGIHl4(this, blockChannelCell, i, tL_error, tL_channels_joinChannel));
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$8J_5MDu2I5a8EY-PWwIvLvSitBE(blockChannelCell));
        AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$Cq82CY1mMDVABDekDXqZnXYfB8o(i, chat), 1000);
        MessagesStorage.getInstance(i).updateDialogsWithDeletedMessages(new ArrayList(), null, true, chat.id);
    }

    public /* synthetic */ void lambda$null$32$ArticleViewer(BlockChannelCell blockChannelCell, int i, TL_error tL_error, TL_channels_joinChannel tL_channels_joinChannel) {
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
                this.visibleDialog.setOnDismissListener(new -$$Lambda$ArticleViewer$akGDRTg_CpAhs2-Lmpycw0irNsA(this));
                dialog.show();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    public /* synthetic */ void lambda$showDialog$36$ArticleViewer(DialogInterface dialogInterface) {
        this.visibleDialog = null;
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
        r3 = NUM; // 0x7f0e09f9 float:1.8880216E38 double:1.053163418E-314;
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
        r2 = NUM; // 0x7f0e00f4 float:1.8875532E38 double:1.053162277E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);	 Catch:{ Exception -> 0x0098 }
        r0.setTitle(r1);	 Catch:{ Exception -> 0x0098 }
        r1 = "OK";
        r2 = NUM; // 0x7f0e0731 float:1.8878772E38 double:1.053163066E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);	 Catch:{ Exception -> 0x0098 }
        r2 = 0;
        r0.setPositiveButton(r1, r2);	 Catch:{ Exception -> 0x0098 }
        r1 = "PleaseDownload";
        r2 = NUM; // 0x7f0e08a8 float:1.8879532E38 double:1.0531632515E-314;
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

    /* JADX WARNING: Removed duplicated region for block: B:29:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00d1  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00b6  */
    private void setImageIndex(int r17, boolean r18) {
        /*
        r16 = this;
        r6 = r16;
        r0 = r17;
        r1 = r6.currentIndex;
        if (r1 != r0) goto L_0x0009;
    L_0x0008:
        return;
    L_0x0009:
        r7 = 0;
        if (r18 != 0) goto L_0x0015;
    L_0x000c:
        r1 = r6.currentThumb;
        if (r1 == 0) goto L_0x0015;
    L_0x0010:
        r1.release();
        r6.currentThumb = r7;
    L_0x0015:
        r1 = r6.currentFileNames;
        r2 = r16.getFileName(r17);
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
        if (r0 != 0) goto L_0x0132;
    L_0x0041:
        r0 = r6.currentIndex;
        if (r0 < 0) goto L_0x012e;
    L_0x0045:
        r1 = r6.imagesArr;
        r1 = r1.size();
        if (r0 < r1) goto L_0x004f;
    L_0x004d:
        goto L_0x012e;
    L_0x004f:
        r0 = r6.imagesArr;
        r1 = r6.currentIndex;
        r0 = r0.get(r1);
        r0 = (org.telegram.tgnet.TLRPC.PageBlock) r0;
        r1 = r6.currentMedia;
        if (r1 == 0) goto L_0x0061;
    L_0x005d:
        if (r1 != r0) goto L_0x0061;
    L_0x005f:
        r13 = 1;
        goto L_0x0062;
    L_0x0061:
        r13 = 0;
    L_0x0062:
        r6.currentMedia = r0;
        r1 = r6.currentIndex;
        r14 = r6.isMediaVideo(r1);
        if (r14 == 0) goto L_0x0071;
    L_0x006c:
        r1 = r6.menuItem;
        r1.showSubItem(r12);
    L_0x0071:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockPhoto;
        if (r1 == 0) goto L_0x0094;
    L_0x0075:
        r0 = (org.telegram.tgnet.TLRPC.TL_pageBlockPhoto) r0;
        r0 = r0.url;
        r1 = android.text.TextUtils.isEmpty(r0);
        if (r1 != 0) goto L_0x0094;
    L_0x007f:
        r1 = new android.text.SpannableStringBuilder;
        r1.<init>(r0);
        r2 = new org.telegram.ui.ArticleViewer$21;
        r2.<init>(r0);
        r0 = r0.length();
        r3 = 34;
        r1.setSpan(r2, r8, r0, r3);
        r15 = 1;
        goto L_0x0096;
    L_0x0094:
        r1 = r7;
        r15 = 0;
    L_0x0096:
        if (r1 != 0) goto L_0x00af;
    L_0x0098:
        r0 = r6.currentMedia;
        r3 = r6.getBlockCaption(r0, r10);
        r1 = 0;
        r4 = r6.currentMedia;
        r0 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r5 = -r0;
        r0 = r16;
        r2 = r3;
        r1 = r0.getText(r1, r2, r3, r4, r5);
    L_0x00af:
        r6.setCurrentCaption(r1, r15);
        r0 = r6.currentAnimation;
        if (r0 == 0) goto L_0x00d1;
    L_0x00b6:
        r0 = r6.menuItem;
        r1 = 8;
        r0.setVisibility(r1);
        r0 = r6.menuItem;
        r0.hideSubItem(r9);
        r0 = r6.actionBar;
        r1 = NUM; // 0x7f0e014b float:1.8875709E38 double:1.05316232E-314;
        r2 = "AttachGif";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        goto L_0x0128;
    L_0x00d1:
        r0 = r6.menuItem;
        r0.setVisibility(r8);
        r0 = r6.imagesArr;
        r0 = r0.size();
        if (r0 != r9) goto L_0x00fe;
    L_0x00de:
        if (r14 == 0) goto L_0x00ef;
    L_0x00e0:
        r0 = r6.actionBar;
        r1 = NUM; // 0x7f0e015c float:1.8875743E38 double:1.0531623286E-314;
        r2 = "AttachVideo";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        goto L_0x0123;
    L_0x00ef:
        r0 = r6.actionBar;
        r1 = NUM; // 0x7f0e0156 float:1.8875731E38 double:1.0531623256E-314;
        r2 = "AttachPhoto";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        goto L_0x0123;
    L_0x00fe:
        r0 = r6.actionBar;
        r1 = NUM; // 0x7f0e0733 float:1.8878776E38 double:1.053163067E-314;
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
    L_0x0123:
        r0 = r6.menuItem;
        r0.showSubItem(r9);
    L_0x0128:
        r0 = r6.groupedPhotosListView;
        r0.fillList();
        goto L_0x0134;
    L_0x012e:
        r6.closePhoto(r8);
        return;
    L_0x0132:
        r13 = 0;
        r14 = 0;
    L_0x0134:
        r0 = r6.listView;
        r0 = r0[r8];
        r0 = r0.getChildCount();
        r1 = 0;
    L_0x013d:
        r2 = -1;
        if (r1 >= r0) goto L_0x0167;
    L_0x0140:
        r3 = r6.listView;
        r3 = r3[r8];
        r3 = r3.getChildAt(r1);
        r4 = r3 instanceof org.telegram.ui.ArticleViewer.BlockSlideshowCell;
        if (r4 == 0) goto L_0x0164;
    L_0x014c:
        r3 = (org.telegram.ui.ArticleViewer.BlockSlideshowCell) r3;
        r4 = r3.currentBlock;
        r4 = r4.items;
        r5 = r6.currentMedia;
        r4 = r4.indexOf(r5);
        if (r4 == r2) goto L_0x0164;
    L_0x015c:
        r0 = r3.innerListView;
        r0.setCurrentItem(r4, r8);
        goto L_0x0167;
    L_0x0164:
        r1 = r1 + 1;
        goto L_0x013d;
    L_0x0167:
        r0 = r6.currentPlaceObject;
        if (r0 == 0) goto L_0x0177;
    L_0x016b:
        r1 = r6.photoAnimationInProgress;
        if (r1 != 0) goto L_0x0175;
    L_0x016f:
        r0 = r0.imageReceiver;
        r0.setVisible(r9, r9);
        goto L_0x0177;
    L_0x0175:
        r6.showAfterAnimation = r0;
    L_0x0177:
        r0 = r6.currentMedia;
        r0 = r6.getPlaceForPhoto(r0);
        r6.currentPlaceObject = r0;
        r0 = r6.currentPlaceObject;
        if (r0 == 0) goto L_0x018f;
    L_0x0183:
        r1 = r6.photoAnimationInProgress;
        if (r1 != 0) goto L_0x018d;
    L_0x0187:
        r0 = r0.imageReceiver;
        r0.setVisible(r8, r9);
        goto L_0x018f;
    L_0x018d:
        r6.hideAfterAnimation = r0;
    L_0x018f:
        if (r13 != 0) goto L_0x01ed;
    L_0x0191:
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
        if (r3 == 0) goto L_0x01b0;
    L_0x01ac:
        r4 = 4;
        r3.setVisibility(r4);
    L_0x01b0:
        r16.releasePlayer();
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
        if (r0 == 0) goto L_0x01e5;
    L_0x01d7:
        if (r14 != 0) goto L_0x01e5;
    L_0x01d9:
        r0 = r6.radialProgressViews;
        r0 = r0[r8];
        r0 = r0.backgroundState;
        if (r0 == 0) goto L_0x01e5;
    L_0x01e3:
        r0 = 1;
        goto L_0x01e6;
    L_0x01e5:
        r0 = 0;
    L_0x01e6:
        r6.canZoom = r0;
        r0 = r6.scale;
        r6.updateMinMax(r0);
    L_0x01ed:
        if (r11 != r2) goto L_0x01fb;
    L_0x01ef:
        r16.setImages();
        r0 = 0;
    L_0x01f3:
        if (r0 >= r12) goto L_0x0249;
    L_0x01f5:
        r6.checkProgress(r0, r8);
        r0 = r0 + 1;
        goto L_0x01f3;
    L_0x01fb:
        r6.checkProgress(r8, r8);
        r0 = r6.currentIndex;
        if (r11 <= r0) goto L_0x0225;
    L_0x0202:
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
        goto L_0x0249;
    L_0x0225:
        if (r11 >= r0) goto L_0x0249;
    L_0x0227:
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
    L_0x0249:
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
        this.animatingImageView.setNeedRadius(placeForPhoto.radius != 0);
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
        float f2 = ((float) (point.y + AndroidUtilities.statusBarHeight)) / ((float) layoutParams.height);
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
        float f5 = (((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) - f3) / 2.0f;
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
        fArr[0][7] = (float) this.animatingImageView.getRadius();
        float[][] fArr3 = this.animationValues;
        float[] fArr4 = fArr3[0];
        f3 = (float) abs;
        f6 = placeForPhoto.scale;
        fArr4[8] = f3 * f6;
        fArr3[0][9] = f * f6;
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
        this.photoAnimationEndRunnable = new -$$Lambda$ArticleViewer$8pPxKBf7qST2OsNyPVRs9Feebz8(this);
        animatorSet.setDuration(200);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$23$VcLMsIelHoGYSebxKyhxv0wAYFw(this));
            }

            public /* synthetic */ void lambda$onAnimationEnd$0$ArticleViewer$23() {
                NotificationCenter.getInstance(ArticleViewer.this.currentAccount).setAnimationInProgress(false);
                if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                    ArticleViewer.this.photoAnimationEndRunnable.run();
                    ArticleViewer.this.photoAnimationEndRunnable = null;
                }
            }
        });
        this.photoTransitionAnimationStartTime = System.currentTimeMillis();
        AndroidUtilities.runOnUIThread(new -$$Lambda$ArticleViewer$mSJxhXEaOnXrvVt6G3mknr82Hqo(this, animatorSet));
        if (VERSION.SDK_INT >= 18) {
            this.photoContainerView.setLayerType(2, null);
        }
        this.photoBackgroundDrawable.drawRunnable = new -$$Lambda$ArticleViewer$8Z92wELF_pGETcAKmhLAU-sdkG8(this, placeForPhoto);
        return true;
    }

    public /* synthetic */ void lambda$openPhoto$37$ArticleViewer() {
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

    public /* synthetic */ void lambda$openPhoto$38$ArticleViewer(AnimatorSet animatorSet) {
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        animatorSet.start();
    }

    public /* synthetic */ void lambda$openPhoto$39$ArticleViewer(PlaceProviderObject placeProviderObject) {
        this.disableShowCheck = false;
        placeProviderObject.imageReceiver.setVisible(false, true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00b9  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x02cf  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0160  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x036e  */
    public void closePhoto(boolean r18) {
        /*
        r17 = this;
        r0 = r17;
        r1 = r0.parentActivity;
        if (r1 == 0) goto L_0x03a4;
    L_0x0006:
        r1 = r0.isPhotoVisible;
        if (r1 == 0) goto L_0x03a4;
    L_0x000a:
        r1 = r17.checkPhotoAnimation();
        if (r1 == 0) goto L_0x0012;
    L_0x0010:
        goto L_0x03a4;
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
        r2 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme;
        r1.removeObserver(r0, r2);
        r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r2 = org.telegram.messenger.NotificationCenter.emojiDidLoad;
        r1.removeObserver(r0, r2);
        r1 = 0;
        r0.isActionBarVisible = r1;
        r2 = r0.velocityTracker;
        r3 = 0;
        if (r2 == 0) goto L_0x0055;
    L_0x0050:
        r2.recycle();
        r0.velocityTracker = r3;
    L_0x0055:
        r2 = r0.currentMedia;
        r2 = r0.getPlaceForPhoto(r2);
        if (r18 == 0) goto L_0x0378;
    L_0x005d:
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
        if (r2 == 0) goto L_0x0086;
    L_0x007d:
        r9 = r2.imageReceiver;
        if (r9 == 0) goto L_0x0086;
    L_0x0081:
        r9 = r9.getAnimatedOrientation();
        goto L_0x0087;
    L_0x0086:
        r9 = 0;
    L_0x0087:
        if (r9 == 0) goto L_0x008a;
    L_0x0089:
        r8 = r9;
    L_0x008a:
        r9 = r0.animatingImageView;
        r9.setOrientation(r8);
        if (r2 == 0) goto L_0x00b9;
    L_0x0091:
        r8 = r0.animatingImageView;
        r9 = r2.radius;
        if (r9 == 0) goto L_0x0099;
    L_0x0097:
        r9 = 1;
        goto L_0x009a;
    L_0x0099:
        r9 = 0;
    L_0x009a:
        r8.setNeedRadius(r9);
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
        goto L_0x00da;
    L_0x00b9:
        r8 = r0.animatingImageView;
        r8.setNeedRadius(r1);
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
    L_0x00da:
        r9 = r7.width;
        if (r9 != 0) goto L_0x00e0;
    L_0x00de:
        r7.width = r5;
    L_0x00e0:
        r9 = r7.height;
        if (r9 != 0) goto L_0x00e6;
    L_0x00e4:
        r7.height = r5;
    L_0x00e6:
        r9 = r0.animatingImageView;
        r9.setLayoutParams(r7);
        r9 = org.telegram.messenger.AndroidUtilities.displaySize;
        r10 = r9.x;
        r10 = (float) r10;
        r11 = r7.width;
        r11 = (float) r11;
        r10 = r10 / r11;
        r9 = r9.y;
        r11 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r9 = r9 + r11;
        r9 = (float) r9;
        r11 = r7.height;
        r11 = (float) r11;
        r9 = r9 / r11;
        r11 = (r10 > r9 ? 1 : (r10 == r9 ? 0 : -1));
        if (r11 <= 0) goto L_0x0103;
    L_0x0102:
        goto L_0x0104;
    L_0x0103:
        r9 = r10;
    L_0x0104:
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
        if (r12 < r13) goto L_0x012f;
    L_0x0123:
        r12 = r0.lastInsets;
        if (r12 == 0) goto L_0x012f;
    L_0x0127:
        r12 = (android.view.WindowInsets) r12;
        r12 = r12.getSystemWindowInsetLeft();
        r12 = (float) r12;
        r11 = r11 + r12;
    L_0x012f:
        r12 = org.telegram.messenger.AndroidUtilities.displaySize;
        r12 = r12.y;
        r13 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        r12 = r12 + r13;
        r12 = (float) r12;
        r12 = r12 - r7;
        r12 = r12 / r10;
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
        if (r2 == 0) goto L_0x02cf;
    L_0x0160:
        r14 = r2.imageReceiver;
        r14.setVisible(r1, r5);
        r14 = r2.imageReceiver;
        r14 = r14.isAspectFit();
        if (r14 == 0) goto L_0x016f;
    L_0x016d:
        r14 = 0;
        goto L_0x017e;
    L_0x016f:
        r14 = r8.left;
        r15 = r2.imageReceiver;
        r15 = r15.getImageX();
        r15 = (float) r15;
        r14 = r14 - r15;
        r14 = java.lang.Math.abs(r14);
        r14 = (int) r14;
    L_0x017e:
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
        if (r7 >= 0) goto L_0x01a6;
    L_0x01a5:
        r7 = 0;
    L_0x01a6:
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
        if (r4 >= 0) goto L_0x01c3;
    L_0x01c2:
        r4 = 0;
    L_0x01c3:
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
        r16 = 9;
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
        r1 = r9[r5];
        r4 = r2.radius;
        r4 = (float) r4;
        r7 = 7;
        r1[r7] = r4;
        r1 = r9[r5];
        r3 = (float) r3;
        r3 = r3 * r13;
        r4 = 8;
        r1[r4] = r3;
        r1 = r9[r5];
        r8 = r8 * r13;
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
        goto L_0x034e;
    L_0x02cf:
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
        if (r9 < 0) goto L_0x0304;
    L_0x0303:
        goto L_0x0305;
    L_0x0304:
        r1 = -r1;
    L_0x0305:
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
    L_0x034e:
        r1 = new org.telegram.ui.-$$Lambda$ArticleViewer$bddzRYkreqx-hk8WrDDgX9mS_Fw;
        r1.<init>(r0, r2);
        r0.photoAnimationEndRunnable = r1;
        r1 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r6.setDuration(r1);
        r1 = new org.telegram.ui.ArticleViewer$24;
        r1.<init>();
        r6.addListener(r1);
        r1 = java.lang.System.currentTimeMillis();
        r0.photoTransitionAnimationStartTime = r1;
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 18;
        if (r1 < r2) goto L_0x0374;
    L_0x036e:
        r1 = r0.photoContainerView;
        r2 = 0;
        r1.setLayerType(r12, r2);
    L_0x0374:
        r6.start();
        goto L_0x0395;
    L_0x0378:
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
    L_0x0395:
        r1 = r0.currentAnimation;
        if (r1 == 0) goto L_0x03a4;
    L_0x0399:
        r2 = 0;
        r1.setSecondParentView(r2);
        r0.currentAnimation = r2;
        r1 = r0.centerImage;
        r1.setImageBitmap(r2);
    L_0x03a4:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.closePhoto(boolean):void");
    }

    public /* synthetic */ void lambda$closePhoto$40$ArticleViewer(PlaceProviderObject placeProviderObject) {
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
        this.photoContainerView.post(new -$$Lambda$ArticleViewer$RbiUQ9E-b8OvcTg7QvMaKqKT5rU(this));
        this.disableShowCheck = false;
        if (placeProviderObject != null) {
            placeProviderObject.imageReceiver.setVisible(true, true);
        }
        this.groupedPhotosListView.clear();
    }

    public /* synthetic */ void lambda$onPhotoClosed$41$ArticleViewer() {
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
        r2 = new org.telegram.ui.-$$Lambda$ArticleViewer$sVetLHzILrx_miSeiNhv3ir2iLY;
        r2.<init>(r0);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
        goto L_0x00ed;
    L_0x00e3:
        if (r2 != r5) goto L_0x00ed;
    L_0x00e5:
        r2 = new org.telegram.ui.-$$Lambda$ArticleViewer$oXQW1t29RA_lRZqbkS6wEfWeLck;
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

    public /* synthetic */ void lambda$drawContent$42$ArticleViewer() {
        setImageIndex(this.currentIndex + 1, false);
    }

    public /* synthetic */ void lambda$drawContent$43$ArticleViewer() {
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
            int access$22000 = radialProgressViewArr[0].backgroundState;
            if (access$22000 > 0 && access$22000 <= 3) {
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
