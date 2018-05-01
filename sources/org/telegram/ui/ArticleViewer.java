package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.MetricAffectingSpan;
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
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
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
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PageBlock;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.RichText;
import org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPage;
import org.telegram.tgnet.TLRPC.TL_pageBlockAnchor;
import org.telegram.tgnet.TLRPC.TL_pageBlockAudio;
import org.telegram.tgnet.TLRPC.TL_pageBlockAuthorDate;
import org.telegram.tgnet.TLRPC.TL_pageBlockBlockquote;
import org.telegram.tgnet.TLRPC.TL_pageBlockChannel;
import org.telegram.tgnet.TLRPC.TL_pageBlockCollage;
import org.telegram.tgnet.TLRPC.TL_pageBlockCover;
import org.telegram.tgnet.TLRPC.TL_pageBlockDivider;
import org.telegram.tgnet.TLRPC.TL_pageBlockEmbed;
import org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost;
import org.telegram.tgnet.TLRPC.TL_pageBlockFooter;
import org.telegram.tgnet.TLRPC.TL_pageBlockHeader;
import org.telegram.tgnet.TLRPC.TL_pageBlockList;
import org.telegram.tgnet.TLRPC.TL_pageBlockParagraph;
import org.telegram.tgnet.TLRPC.TL_pageBlockPhoto;
import org.telegram.tgnet.TLRPC.TL_pageBlockPreformatted;
import org.telegram.tgnet.TLRPC.TL_pageBlockPullquote;
import org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow;
import org.telegram.tgnet.TLRPC.TL_pageBlockSubheader;
import org.telegram.tgnet.TLRPC.TL_pageBlockSubtitle;
import org.telegram.tgnet.TLRPC.TL_pageBlockTitle;
import org.telegram.tgnet.TLRPC.TL_pageBlockUnsupported;
import org.telegram.tgnet.TLRPC.TL_pageBlockVideo;
import org.telegram.tgnet.TLRPC.TL_pageFull;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_textBold;
import org.telegram.tgnet.TLRPC.TL_textConcat;
import org.telegram.tgnet.TLRPC.TL_textEmail;
import org.telegram.tgnet.TLRPC.TL_textEmpty;
import org.telegram.tgnet.TLRPC.TL_textFixed;
import org.telegram.tgnet.TLRPC.TL_textItalic;
import org.telegram.tgnet.TLRPC.TL_textPlain;
import org.telegram.tgnet.TLRPC.TL_textStrike;
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
import org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.Scroller;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.TextPaintSpan;
import org.telegram.ui.Components.TextPaintUrlSpan;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;
import org.telegram.ui.Components.WebPlayerView;
import org.telegram.ui.Components.WebPlayerView.WebPlayerViewDelegate;

public class ArticleViewer implements OnDoubleTapListener, OnGestureListener, NotificationCenterDelegate {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile ArticleViewer Instance = null;
    private static final int TEXT_FLAG_ITALIC = 2;
    private static final int TEXT_FLAG_MEDIUM = 1;
    private static final int TEXT_FLAG_MONO = 4;
    private static final int TEXT_FLAG_REGULAR = 0;
    private static final int TEXT_FLAG_STRIKE = 32;
    private static final int TEXT_FLAG_UNDERLINE = 16;
    private static final int TEXT_FLAG_URL = 8;
    private static TextPaint audioTimePaint = new TextPaint(1);
    private static SparseArray<TextPaint> authorTextPaints = new SparseArray();
    private static SparseArray<TextPaint> captionTextPaints = new SparseArray();
    private static TextPaint channelNamePaint = null;
    private static Paint colorPaint = null;
    private static DecelerateInterpolator decelerateInterpolator = null;
    private static Paint dividerPaint = null;
    private static Paint dotsPaint = null;
    private static TextPaint embedPostAuthorPaint = null;
    private static SparseArray<TextPaint> embedPostCaptionTextPaints = new SparseArray();
    private static TextPaint embedPostDatePaint = null;
    private static SparseArray<TextPaint> embedPostTextPaints = new SparseArray();
    private static SparseArray<TextPaint> embedTextPaints = new SparseArray();
    private static TextPaint errorTextPaint = null;
    private static SparseArray<TextPaint> footerTextPaints = new SparseArray();
    private static final int gallery_menu_openin = 3;
    private static final int gallery_menu_save = 1;
    private static final int gallery_menu_share = 2;
    private static SparseArray<TextPaint> headerTextPaints = new SparseArray();
    private static SparseArray<TextPaint> listTextPaints = new SparseArray();
    private static TextPaint listTextPointerPaint;
    private static SparseArray<TextPaint> paragraphTextPaints = new SparseArray();
    private static Paint preformattedBackgroundPaint;
    private static SparseArray<TextPaint> preformattedTextPaints = new SparseArray();
    private static Drawable[] progressDrawables;
    private static Paint progressPaint;
    private static Paint quoteLinePaint;
    private static SparseArray<TextPaint> quoteTextPaints = new SparseArray();
    private static Paint selectorPaint;
    private static SparseArray<TextPaint> slideshowTextPaints = new SparseArray();
    private static SparseArray<TextPaint> subheaderTextPaints = new SparseArray();
    private static SparseArray<TextPaint> subquoteTextPaints = new SparseArray();
    private static SparseArray<TextPaint> subtitleTextPaints = new SparseArray();
    private static SparseArray<TextPaint> titleTextPaints = new SparseArray();
    private static Paint urlPaint;
    private static SparseArray<TextPaint> videoTextPaints = new SparseArray();
    private ActionBar actionBar;
    private WebpageAdapter adapter;
    private HashMap<String, Integer> anchors = new HashMap();
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    private ClippingImageView animatingImageView;
    private Runnable animationEndRunnable;
    private int animationInProgress;
    private long animationStartTime;
    private float animationValue;
    private float[][] animationValues = ((float[][]) Array.newInstance(float.class, new int[]{2, 8}));
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private boolean attachedToWindow;
    private HashMap<TL_pageBlockAudio, MessageObject> audioBlocks = new HashMap();
    private ArrayList<MessageObject> audioMessages = new ArrayList();
    private ImageView backButton;
    private BackDrawable backDrawable;
    private Paint backgroundPaint;
    private View barBackground;
    private Paint blackPaint = new Paint();
    private ArrayList<PageBlock> blocks = new ArrayList();
    private FrameLayout bottomLayout;
    private boolean canDragDown = true;
    private boolean canZoom = true;
    private TextView captionTextView;
    private TextView captionTextViewNew;
    private TextView captionTextViewOld;
    private ImageReceiver centerImage = new ImageReceiver();
    private boolean changingPage;
    private TL_pageBlockChannel channelBlock;
    private boolean checkingForLongPress = false;
    private boolean collapsed;
    private ColorCell[] colorCells = new ColorCell[3];
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
    private BitmapHolder currentThumb;
    private View customView;
    private CustomViewCallback customViewCallback;
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
    private int isRtl = -1;
    private boolean isVisible;
    private Object lastInsets;
    private Drawable layerShadowDrawable;
    private LinearLayoutManager layoutManager;
    private ImageReceiver leftImage = new ImageReceiver();
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
    private StaticLayout pressedLinkOwnerLayout;
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

    /* renamed from: org.telegram.ui.ArticleViewer$1 */
    class C08121 implements OnTouchListener {
        C08121() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getActionMasked() == 0 && ArticleViewer.this.popupWindow != null && ArticleViewer.this.popupWindow.isShowing()) {
                view.getHitRect(ArticleViewer.this.popupRect);
                if (ArticleViewer.this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY()) == null) {
                    ArticleViewer.this.popupWindow.dismiss();
                }
            }
            return null;
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$3 */
    class C08203 implements OnClickListener {
        C08203() {
        }

        public void onClick(View view) {
            if (ArticleViewer.this.pressedLinkOwnerLayout != null) {
                AndroidUtilities.addToClipboard(ArticleViewer.this.pressedLinkOwnerLayout.getText());
                Toast.makeText(ArticleViewer.this.parentActivity, LocaleController.getString("TextCopied", C0446R.string.TextCopied), 0).show();
                if (!(ArticleViewer.this.popupWindow == null || ArticleViewer.this.popupWindow.isShowing() == null)) {
                    ArticleViewer.this.popupWindow.dismiss(true);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$4 */
    class C08234 implements OnDismissListener {
        C08234() {
        }

        public void onDismiss() {
            if (ArticleViewer.this.pressedLinkOwnerView != null) {
                ArticleViewer.this.pressedLinkOwnerLayout = null;
                ArticleViewer.this.pressedLinkOwnerView.invalidate();
                ArticleViewer.this.pressedLinkOwnerView = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$6 */
    class C08256 implements OnApplyWindowInsetsListener {
        C08256() {
        }

        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
            WindowInsets windowInsets2 = (WindowInsets) ArticleViewer.this.lastInsets;
            ArticleViewer.this.lastInsets = windowInsets;
            if (windowInsets2 == null || windowInsets2.toString().equals(windowInsets.toString()) == null) {
                ArticleViewer.this.windowView.requestLayout();
            }
            return windowInsets.consumeSystemWindowInsets();
        }
    }

    private class BlockAuthorDateCell extends View {
        private TL_pageBlockAuthorDate currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private int textX;
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockAuthorDateCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockAuthorDate tL_pageBlockAuthorDate) {
            this.currentBlock = tL_pageBlockAuthorDate;
            this.lastCreatedWidth = null;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (super.onTouchEvent(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            Throwable e;
            i = MeasureSpec.getSize(i);
            int i3 = 1;
            if (this.currentBlock != 0) {
                if (this.lastCreatedWidth != i) {
                    Spannable spannable;
                    MetricAffectingSpan[] metricAffectingSpanArr;
                    CharSequence formatString;
                    CharSequence newSpannable;
                    int dp;
                    i2 = ArticleViewer.this.getText(this.currentBlock.author, this.currentBlock.author, this.currentBlock);
                    if (i2 instanceof Spannable) {
                        spannable = (Spannable) i2;
                        metricAffectingSpanArr = (MetricAffectingSpan[]) spannable.getSpans(0, i2.length(), MetricAffectingSpan.class);
                    } else {
                        spannable = null;
                        metricAffectingSpanArr = spannable;
                    }
                    if (this.currentBlock.published_date != 0 && !TextUtils.isEmpty(i2)) {
                        formatString = LocaleController.formatString("ArticleDateByAuthor", C0446R.string.ArticleDateByAuthor, LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000), i2);
                    } else if (TextUtils.isEmpty(i2)) {
                        formatString = LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000);
                    } else {
                        formatString = LocaleController.formatString("ArticleByAuthor", C0446R.string.ArticleByAuthor, i2);
                    }
                    if (metricAffectingSpanArr != null) {
                        try {
                            if (metricAffectingSpanArr.length > 0) {
                                i2 = TextUtils.indexOf(formatString, i2);
                                if (i2 != -1) {
                                    newSpannable = Factory.getInstance().newSpannable(formatString);
                                    int i4 = 0;
                                    while (i4 < metricAffectingSpanArr.length) {
                                        try {
                                            newSpannable.setSpan(metricAffectingSpanArr[i4], spannable.getSpanStart(metricAffectingSpanArr[i4]) + i2, spannable.getSpanEnd(metricAffectingSpanArr[i4]) + i2, 33);
                                            i4++;
                                        } catch (Exception e2) {
                                            e = e2;
                                        }
                                    }
                                    this.textLayout = ArticleViewer.this.createLayoutForText(newSpannable, null, i - AndroidUtilities.dp(36.0f), this.currentBlock);
                                    if (this.textLayout != 0) {
                                        dp = (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight()) + 0;
                                        if (ArticleViewer.this.isRtl == 1) {
                                            this.textX = (int) Math.floor((double) ((((float) i) - this.textLayout.getLineWidth(0)) - ((float) AndroidUtilities.dp(16.0f))));
                                        } else {
                                            this.textX = AndroidUtilities.dp(NUM);
                                        }
                                        i3 = dp;
                                    }
                                }
                            }
                        } catch (Exception e3) {
                            e = e3;
                            newSpannable = formatString;
                            FileLog.m3e(e);
                            this.textLayout = ArticleViewer.this.createLayoutForText(newSpannable, null, i - AndroidUtilities.dp(36.0f), this.currentBlock);
                            if (this.textLayout != 0) {
                                dp = (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight()) + 0;
                                if (ArticleViewer.this.isRtl == 1) {
                                    this.textX = (int) Math.floor((double) ((((float) i) - this.textLayout.getLineWidth(0)) - ((float) AndroidUtilities.dp(16.0f))));
                                } else {
                                    this.textX = AndroidUtilities.dp(NUM);
                                }
                                i3 = dp;
                                setMeasuredDimension(i, i3);
                            }
                            i3 = 0;
                            setMeasuredDimension(i, i3);
                        }
                    }
                    newSpannable = formatString;
                    this.textLayout = ArticleViewer.this.createLayoutForText(newSpannable, null, i - AndroidUtilities.dp(36.0f), this.currentBlock);
                    if (this.textLayout != 0) {
                        dp = (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight()) + 0;
                        if (ArticleViewer.this.isRtl == 1) {
                            this.textX = AndroidUtilities.dp(NUM);
                        } else {
                            this.textX = (int) Math.floor((double) ((((float) i) - this.textLayout.getLineWidth(0)) - ((float) AndroidUtilities.dp(16.0f))));
                        }
                        i3 = dp;
                    }
                }
                i3 = 0;
            }
            setMeasuredDimension(i, i3);
        }

        protected void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockBlockquoteCell extends View {
        private TL_pageBlockBlockquote currentBlock;
        private boolean hasRtl;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private StaticLayout textLayout2;
        private int textX;
        private int textY = AndroidUtilities.dp(8.0f);
        private int textY2;

        public BlockBlockquoteCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockBlockquote tL_pageBlockBlockquote) {
            this.currentBlock = tL_pageBlockBlockquote;
            this.lastCreatedWidth = null;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!(ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout2, this.textX, this.textY2))) {
                if (super.onTouchEvent(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            int i3 = 0;
            if (this.currentBlock == 0) {
                i3 = 1;
            } else if (this.lastCreatedWidth != i) {
                i2 = i - AndroidUtilities.dp(NUM);
                if (this.currentBlock.level > 0) {
                    i2 -= AndroidUtilities.dp((float) (this.currentBlock.level * 14));
                }
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i2, this.currentBlock);
                this.hasRtl = false;
                if (this.textLayout != null) {
                    int dp = (AndroidUtilities.dp(8.0f) + this.textLayout.getHeight()) + 0;
                    int lineCount = this.textLayout.getLineCount();
                    while (i3 < lineCount) {
                        if (this.textLayout.getLineLeft(i3) > 0.0f) {
                            ArticleViewer.this.isRtl = 1;
                            this.hasRtl = true;
                            break;
                        }
                        i3++;
                    }
                    i3 = dp;
                }
                if (this.currentBlock.level > 0) {
                    if (this.hasRtl) {
                        this.textX = AndroidUtilities.dp((float) (14 + (this.currentBlock.level * 14)));
                    } else {
                        this.textX = AndroidUtilities.dp((float) (14 * this.currentBlock.level)) + AndroidUtilities.dp(32.0f);
                    }
                } else if (this.hasRtl) {
                    this.textX = AndroidUtilities.dp(14.0f);
                } else {
                    this.textX = AndroidUtilities.dp(32.0f);
                }
                this.textLayout2 = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, i2, this.currentBlock);
                if (this.textLayout2 != 0) {
                    this.textY2 = AndroidUtilities.dp(8.0f) + i3;
                    i3 += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                }
                if (i3 != 0) {
                    i3 += AndroidUtilities.dp(8.0f);
                }
            }
            setMeasuredDimension(i, i3);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout2 != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY2);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout2);
                    this.textLayout2.draw(canvas);
                    canvas.restore();
                }
                if (this.hasRtl) {
                    int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(20.0f);
                    canvas.drawRect((float) measuredWidth, (float) AndroidUtilities.dp(6.0f), (float) (measuredWidth + AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), ArticleViewer.quoteLinePaint);
                } else {
                    canvas.drawRect((float) AndroidUtilities.dp((float) (18 + (this.currentBlock.level * 14))), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp((float) (20 + (this.currentBlock.level * 14))), (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), ArticleViewer.quoteLinePaint);
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
        private int lastCreatedWidth;
        private ContextProgressView progressView;
        private StaticLayout textLayout;
        private TextView textView;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textX2;
        private int textY = AndroidUtilities.dp(11.0f);
        private int textY2 = AndroidUtilities.dp(11.5f);

        public BlockChannelCell(Context context, int i) {
            super(context);
            setWillNotDraw(false);
            this.backgroundPaint = new Paint();
            this.currentType = i;
            this.textView = new TextView(context);
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setText(LocaleController.getString("ChannelJoin", C0446R.string.ChannelJoin));
            this.textView.setGravity(19);
            addView(this.textView, LayoutHelper.createFrame(-2, 39, 53));
            this.textView.setOnClickListener(new OnClickListener(ArticleViewer.this) {
                public void onClick(View view) {
                    if (BlockChannelCell.this.currentState == null) {
                        BlockChannelCell.this.setState(1, true);
                        ArticleViewer.this.joinChannel(BlockChannelCell.this, ArticleViewer.this.loadedChannel);
                    }
                }
            });
            this.imageView = new ImageView(context);
            this.imageView.setImageResource(C0446R.drawable.list_check);
            this.imageView.setScaleType(ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(39, 39, 53));
            this.progressView = new ContextProgressView(context, 0);
            addView(this.progressView, LayoutHelper.createFrame(39, 39, 53));
        }

        public void setBlock(TL_pageBlockChannel tL_pageBlockChannel) {
            this.currentBlock = tL_pageBlockChannel;
            int access$8900 = ArticleViewer.this.getSelectedColor();
            if (this.currentType == 0) {
                this.textView.setTextColor(-14840360);
                if (access$8900 == 0) {
                    this.backgroundPaint.setColor(-526345);
                } else if (access$8900 == 1) {
                    this.backgroundPaint.setColor(-1712440);
                } else if (access$8900 == 2) {
                    this.backgroundPaint.setColor(-14277082);
                }
                this.imageView.setColorFilter(new PorterDuffColorFilter(-6710887, Mode.MULTIPLY));
            } else {
                this.textView.setTextColor(-1);
                this.backgroundPaint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                this.imageView.setColorFilter(new PorterDuffColorFilter(-1, Mode.MULTIPLY));
            }
            this.lastCreatedWidth = 0;
            Chat chat = MessagesController.getInstance(ArticleViewer.this.currentAccount).getChat(Integer.valueOf(tL_pageBlockChannel.channel.id));
            if (chat != null) {
                if (!chat.min) {
                    ArticleViewer.this.loadedChannel = chat;
                    if (chat.left == null || chat.kicked != null) {
                        setState(4, false);
                    } else {
                        setState(0, false);
                    }
                    requestLayout();
                }
            }
            ArticleViewer.this.loadChannel(this, tL_pageBlockChannel.channel);
            setState(1, false);
            requestLayout();
        }

        public void setState(int i, boolean z) {
            if (this.currentAnimation != null) {
                this.currentAnimation.cancel();
            }
            this.currentState = i;
            float f = 0.0f;
            float f2 = 0.1f;
            if (z) {
                this.currentAnimation = new AnimatorSet();
                z = this.currentAnimation;
                Animator[] animatorArr = new Animator[9];
                TextView textView = this.textView;
                String str = "alpha";
                float[] fArr = new float[1];
                fArr[0] = i == 0 ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(textView, str, fArr);
                textView = this.textView;
                str = "scaleX";
                fArr = new float[1];
                fArr[0] = i == 0 ? 1.0f : 0.1f;
                animatorArr[1] = ObjectAnimator.ofFloat(textView, str, fArr);
                textView = this.textView;
                str = "scaleY";
                fArr = new float[1];
                fArr[0] = i == 0 ? 1.0f : 0.1f;
                animatorArr[2] = ObjectAnimator.ofFloat(textView, str, fArr);
                ContextProgressView contextProgressView = this.progressView;
                String str2 = "alpha";
                float[] fArr2 = new float[1];
                fArr2[0] = i == 1 ? 1.0f : 0.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(contextProgressView, str2, fArr2);
                contextProgressView = this.progressView;
                str2 = "scaleX";
                fArr2 = new float[1];
                fArr2[0] = i == 1 ? 1.0f : 0.1f;
                animatorArr[4] = ObjectAnimator.ofFloat(contextProgressView, str2, fArr2);
                contextProgressView = this.progressView;
                str2 = "scaleY";
                fArr2 = new float[1];
                fArr2[0] = i == 1 ? 1.0f : 0.1f;
                animatorArr[5] = ObjectAnimator.ofFloat(contextProgressView, str2, fArr2);
                ImageView imageView = this.imageView;
                str2 = "alpha";
                fArr2 = new float[1];
                if (i == 2) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                animatorArr[6] = ObjectAnimator.ofFloat(imageView, str2, fArr2);
                ImageView imageView2 = this.imageView;
                str = "scaleX";
                fArr = new float[1];
                fArr[0] = i == 2 ? 1.0f : 0.1f;
                animatorArr[7] = ObjectAnimator.ofFloat(imageView2, str, fArr);
                imageView2 = this.imageView;
                str = "scaleY";
                float[] fArr3 = new float[1];
                if (i == 2) {
                    f2 = 1.0f;
                }
                fArr3[0] = f2;
                animatorArr[8] = ObjectAnimator.ofFloat(imageView2, str, fArr3);
                z.playTogether(animatorArr);
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
            z = this.imageView;
            if (i == 2) {
                f = 1.0f;
            }
            z.setAlpha(f);
            this.imageView.setScaleX(i == 2 ? 1.0f : 0.1f);
            z = this.imageView;
            if (i == 2) {
                f2 = 1.0f;
            }
            z.setScaleY(f2);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.currentType != 0) {
                return super.onTouchEvent(motionEvent);
            }
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (super.onTouchEvent(motionEvent) == null) {
                    motionEvent = null;
                    return motionEvent;
                }
            }
            motionEvent = true;
            return motionEvent;
        }

        protected void onMeasure(int i, int i2) {
            i2 = MeasureSpec.getSize(i);
            setMeasuredDimension(i2, AndroidUtilities.dp(48.0f));
            this.textView.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            this.buttonWidth = this.textView.getMeasuredWidth();
            this.progressView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            this.imageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            if (this.currentBlock != 0 && this.lastCreatedWidth != i2) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this.currentBlock.channel.title, null, (i2 - AndroidUtilities.dp(52.0f)) - this.buttonWidth, this.currentBlock);
                this.textX2 = (getMeasuredWidth() - this.textX) - this.buttonWidth;
            }
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            this.imageView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, (this.textX2 + (this.buttonWidth / 2)) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            this.progressView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, (this.textX2 + (this.buttonWidth / 2)) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            this.textView.layout(this.textX2, 0, this.textX2 + this.textView.getMeasuredWidth(), this.textView.getMeasuredHeight());
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(39.0f), this.backgroundPaint);
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockCollageCell extends FrameLayout {
        private Adapter adapter;
        private TL_pageBlockCollage currentBlock;
        private GridLayoutManager gridLayoutManager;
        private boolean inLayout;
        private RecyclerListView innerListView;
        private int lastCreatedWidth;
        private int listX;
        private StaticLayout textLayout;
        private int textX;
        private int textY;

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
                public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
                    rect.left = 0;
                    rect.top = 0;
                    view = AndroidUtilities.dp(2.0f);
                    rect.right = view;
                    rect.bottom = view;
                }
            });
            RecyclerListView recyclerListView = this.innerListView;
            LayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            this.gridLayoutManager = gridLayoutManager;
            recyclerListView.setLayoutManager(gridLayoutManager);
            context = this.innerListView;
            Adapter c19163 = new Adapter(ArticleViewer.this) {
                public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    return new Holder(i != 0 ? new BlockVideoCell(BlockCollageCell.this.getContext(), 2) : new BlockPhotoCell(BlockCollageCell.this.getContext(), 2));
                }

                public void onBindViewHolder(ViewHolder viewHolder, int i) {
                    if (viewHolder.getItemViewType() != 0) {
                        ((BlockVideoCell) viewHolder.itemView).setBlock((TL_pageBlockVideo) BlockCollageCell.this.currentBlock.items.get(i), true, true);
                    } else {
                        ((BlockPhotoCell) viewHolder.itemView).setBlock((TL_pageBlockPhoto) BlockCollageCell.this.currentBlock.items.get(i), true, true);
                    }
                }

                public int getItemCount() {
                    if (BlockCollageCell.this.currentBlock == null) {
                        return 0;
                    }
                    return BlockCollageCell.this.currentBlock.items.size();
                }

                public int getItemViewType(int i) {
                    return (((PageBlock) BlockCollageCell.this.currentBlock.items.get(i)) instanceof TL_pageBlockPhoto) != 0 ? 0 : 1;
                }
            };
            this.adapter = c19163;
            context.setAdapter(c19163);
            addView(this.innerListView, LayoutHelper.createFrame(-1, -2.0f));
            setWillNotDraw(null);
        }

        public void setBlock(TL_pageBlockCollage tL_pageBlockCollage) {
            this.currentBlock = tL_pageBlockCollage;
            this.lastCreatedWidth = null;
            this.adapter.notifyDataSetChanged();
            tL_pageBlockCollage = ArticleViewer.this.getSelectedColor();
            if (tL_pageBlockCollage == null) {
                this.innerListView.setGlowColor(-657673);
            } else if (tL_pageBlockCollage == 1) {
                this.innerListView.setGlowColor(-659492);
            } else if (tL_pageBlockCollage == 2) {
                this.innerListView.setGlowColor(-15461356);
            }
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (super.onTouchEvent(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            i2 = 1;
            this.inLayout = true;
            i = MeasureSpec.getSize(i);
            if (this.currentBlock != null) {
                int i3;
                if (this.currentBlock.level > 0) {
                    i2 = AndroidUtilities.dp((float) (14 * this.currentBlock.level)) + AndroidUtilities.dp(18.0f);
                    this.listX = i2;
                    this.textX = i2;
                    i2 = i - (this.listX + AndroidUtilities.dp(18.0f));
                    i3 = i2;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    i3 = i - AndroidUtilities.dp(NUM);
                    i2 = i;
                }
                int dp = i2 / AndroidUtilities.dp(100.0f);
                int ceil = (int) Math.ceil((double) (((float) this.currentBlock.items.size()) / ((float) dp)));
                i2 /= dp;
                this.gridLayoutManager.setSpanCount(dp);
                i2 *= ceil;
                this.innerListView.measure(MeasureSpec.makeMeasureSpec((dp * i2) + AndroidUtilities.dp(2.0f), NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
                i2 -= AndroidUtilities.dp(2.0f);
                if (this.lastCreatedWidth != i) {
                    this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, i3, this.currentBlock);
                    if (this.textLayout != null) {
                        this.textY = AndroidUtilities.dp(8.0f) + i2;
                        i2 += AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                    }
                }
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    i2 += AndroidUtilities.dp(8.0f);
                }
            }
            setMeasuredDimension(i, i2);
            this.inLayout = false;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            this.innerListView.layout(this.listX, 0, this.listX + this.innerListView.getMeasuredWidth(), this.innerListView.getMeasuredHeight());
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockDividerCell extends View {
        private RectF rect = new RectF();

        public BlockDividerCell(Context context) {
            super(context);
            if (ArticleViewer.dividerPaint == null) {
                ArticleViewer.dividerPaint = new Paint();
                ArticleViewer access$8900 = ArticleViewer.this.getSelectedColor();
                if (access$8900 == null) {
                    ArticleViewer.dividerPaint.setColor(-3288619);
                } else if (access$8900 == 1) {
                    ArticleViewer.dividerPaint.setColor(-4080987);
                } else if (access$8900 == 2) {
                    ArticleViewer.dividerPaint.setColor(-12303292);
                }
            }
        }

        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(NUM));
        }

        protected void onDraw(Canvas canvas) {
            int measuredWidth = getMeasuredWidth() / 3;
            this.rect.set((float) measuredWidth, (float) AndroidUtilities.dp(8.0f), (float) (measuredWidth * 2), (float) AndroidUtilities.dp(10.0f));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), ArticleViewer.dividerPaint);
        }
    }

    private class BlockEmbedCell extends FrameLayout {
        private TL_pageBlockEmbed currentBlock;
        private int lastCreatedWidth;
        private int listX;
        private StaticLayout textLayout;
        private int textX;
        private int textY;
        private WebPlayerView videoView;
        private TouchyWebView webView;

        public class TouchyWebView extends WebView {
            public TouchyWebView(Context context) {
                super(context);
                setFocusable(null);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
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

        @SuppressLint({"SetJavaScriptEnabled"})
        public BlockEmbedCell(Context context) {
            super(context);
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
                    BlockEmbedCell.this.videoView.loadVideo(null, null, null, false);
                    Map hashMap = new HashMap();
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
                        ArticleViewer.this.fullscreenedVideo = BlockEmbedCell.this.videoView;
                        ArticleViewer.this.fullscreenVideoContainer.addView(view, LayoutHelper.createFrame(-1, -1.0f));
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(0);
                    } else {
                        ArticleViewer.this.fullscreenAspectRatioView.removeView(ArticleViewer.this.fullscreenTextureView);
                        ArticleViewer.this.fullscreenedVideo = false;
                        ArticleViewer.this.fullscreenAspectRatioView.setVisibility(true);
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(true);
                    }
                    return ArticleViewer.this.fullscreenTextureView;
                }

                public void onSharePressed() {
                    if (ArticleViewer.this.parentActivity != null) {
                        ArticleViewer.this.showDialog(new ShareAlert(ArticleViewer.this.parentActivity, null, BlockEmbedCell.this.currentBlock.url, false, BlockEmbedCell.this.currentBlock.url, true));
                    }
                }

                public void onPlayStateChanged(WebPlayerView webPlayerView, boolean z) {
                    if (z) {
                        if (ArticleViewer.this.currentPlayingVideo && ArticleViewer.this.currentPlayingVideo != webPlayerView) {
                            ArticleViewer.this.currentPlayingVideo.pause();
                        }
                        ArticleViewer.this.currentPlayingVideo = webPlayerView;
                        try {
                            ArticleViewer.this.parentActivity.getWindow().addFlags(128);
                            return;
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                            return;
                        }
                    }
                    if (ArticleViewer.this.currentPlayingVideo == webPlayerView) {
                        ArticleViewer.this.currentPlayingVideo = false;
                    }
                    try {
                        ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
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
            }
            if (VERSION.SDK_INT >= 21) {
                this.webView.getSettings().setMixedContentMode(0);
                CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
            }
            this.webView.setWebChromeClient(new WebChromeClient(ArticleViewer.this) {

                /* renamed from: org.telegram.ui.ArticleViewer$BlockEmbedCell$2$1 */
                class C08271 implements Runnable {
                    C08271() {
                    }

                    public void run() {
                        if (ArticleViewer.this.customView != null) {
                            ArticleViewer.this.fullscreenVideoContainer.addView(ArticleViewer.this.customView, LayoutHelper.createFrame(-1, -1.0f));
                            ArticleViewer.this.fullscreenVideoContainer.setVisibility(0);
                        }
                    }
                }

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
                    AndroidUtilities.runOnUIThread(new C08271(), 100);
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
                this.currentBlock = false;
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            this.videoView.destroy();
        }

        public void setBlock(TL_pageBlockEmbed tL_pageBlockEmbed) {
            TL_pageBlockEmbed tL_pageBlockEmbed2 = this.currentBlock;
            this.currentBlock = tL_pageBlockEmbed;
            this.lastCreatedWidth = 0;
            if (tL_pageBlockEmbed2 != this.currentBlock) {
                try {
                    this.webView.loadUrl("about:blank");
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                try {
                    if (this.currentBlock.html != null) {
                        this.webView.loadDataWithBaseURL("https://telegram.org/embed", this.currentBlock.html, "text/html", C0542C.UTF8_NAME, null);
                        this.videoView.setVisibility(4);
                        this.videoView.loadVideo(null, null, null, false);
                        this.webView.setVisibility(0);
                    } else {
                        if (this.videoView.loadVideo(tL_pageBlockEmbed.url, this.currentBlock.poster_photo_id != 0 ? ArticleViewer.this.getPhotoWithId(this.currentBlock.poster_photo_id) : null, null, this.currentBlock.autoplay) != null) {
                            this.webView.setVisibility(4);
                            this.videoView.setVisibility(0);
                            this.webView.stopLoading();
                            this.webView.loadUrl("about:blank");
                        } else {
                            this.webView.setVisibility(0);
                            this.videoView.setVisibility(4);
                            this.videoView.loadVideo(null, null, null, false);
                            tL_pageBlockEmbed = new HashMap();
                            tL_pageBlockEmbed.put("Referer", "http://youtube.com");
                            this.webView.loadUrl(this.currentBlock.url, tL_pageBlockEmbed);
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
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

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (super.onTouchEvent(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            int dp;
            i = MeasureSpec.getSize(i);
            if (this.currentBlock != 0) {
                int i3;
                float f;
                if (this.currentBlock.level > 0) {
                    i2 = AndroidUtilities.dp((float) (14 * this.currentBlock.level)) + AndroidUtilities.dp(18.0f);
                    this.listX = i2;
                    this.textX = i2;
                    i2 = i - (this.listX + AndroidUtilities.dp(18.0f));
                    i3 = i2;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    i3 = i - AndroidUtilities.dp(NUM);
                    i2 = i;
                }
                if (this.currentBlock.w == 0) {
                    f = 1.0f;
                } else {
                    f = ((float) i) / ((float) this.currentBlock.w);
                }
                dp = (int) (((float) (this.currentBlock.w == 0 ? AndroidUtilities.dp((float) this.currentBlock.h) : this.currentBlock.h)) * f);
                this.webView.measure(MeasureSpec.makeMeasureSpec(i2, NUM), MeasureSpec.makeMeasureSpec(dp, NUM));
                if (this.videoView.getParent() == this) {
                    this.videoView.measure(MeasureSpec.makeMeasureSpec(i2, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f) + dp, NUM));
                }
                if (this.lastCreatedWidth != i) {
                    this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, i3, this.currentBlock);
                    if (this.textLayout != 0) {
                        this.textY = AndroidUtilities.dp(8.0f) + dp;
                        dp += AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                    }
                }
                dp += AndroidUtilities.dp(NUM);
                if (this.currentBlock.level > 0 && this.currentBlock.bottom == 0) {
                    dp += AndroidUtilities.dp(8.0f);
                } else if (this.currentBlock.level == 0 && this.textLayout != 0) {
                    dp += AndroidUtilities.dp(8.0f);
                }
            } else {
                dp = 1;
            }
            setMeasuredDimension(i, dp);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            this.webView.layout(this.listX, 0, this.listX + this.webView.getMeasuredWidth(), this.webView.getMeasuredHeight());
            if (this.videoView.getParent() == this) {
                this.videoView.layout(this.listX, 0, this.listX + this.videoView.getMeasuredWidth(), this.videoView.getMeasuredHeight());
            }
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
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
        private int captionX = AndroidUtilities.dp(18.0f);
        private int captionY;
        private TL_pageBlockEmbedPost currentBlock;
        private StaticLayout dateLayout;
        private int dateX;
        private int lastCreatedWidth;
        private int lineHeight;
        private StaticLayout nameLayout;
        private int nameX;
        private StaticLayout textLayout;
        private int textX = AndroidUtilities.dp(32.0f);
        private int textY = AndroidUtilities.dp(56.0f);

        public BlockEmbedPostCell(Context context) {
            super(context);
            this.avatarImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            this.avatarImageView.setImageCoords(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            this.avatarDrawable = new AvatarDrawable();
        }

        public void setBlock(TL_pageBlockEmbedPost tL_pageBlockEmbedPost) {
            this.currentBlock = tL_pageBlockEmbedPost;
            this.lastCreatedWidth = null;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (super.onTouchEvent(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            int i3 = 1;
            int i4 = 0;
            if (this.currentBlock != 0) {
                if (this.lastCreatedWidth != i) {
                    i2 = this.currentBlock.author_photo_id != 0 ? 1 : 0;
                    this.avatarVisible = i2;
                    if (i2 != 0) {
                        i2 = ArticleViewer.this.getPhotoWithId(this.currentBlock.author_photo_id);
                        boolean z = i2 instanceof TL_photo;
                        this.avatarVisible = z;
                        if (z) {
                            this.avatarDrawable.setInfo(0, this.currentBlock.author, null, false);
                            i2 = FileLoader.getClosestPhotoSizeWithSize(i2.sizes, AndroidUtilities.dp(40.0f), true);
                            this.avatarImageView.setImage(i2 != 0 ? i2.location : null, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(40), Integer.valueOf(40)}), this.avatarDrawable, 0, null, 1);
                        }
                    }
                    this.nameLayout = ArticleViewer.this.createLayoutForText(this.currentBlock.author, null, i - AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 50)), this.currentBlock);
                    if (this.currentBlock.date != 0) {
                        i2 = ArticleViewer.this;
                        CharSequence format = LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.date) * 1000);
                        if (this.avatarVisible) {
                            i4 = 54;
                        }
                        this.dateLayout = i2.createLayoutForText(format, null, i - AndroidUtilities.dp((float) (50 + i4)), this.currentBlock);
                    } else {
                        this.dateLayout = null;
                    }
                    i2 = AndroidUtilities.dp(NUM);
                    if (this.currentBlock.text != null) {
                        this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(50.0f), this.currentBlock);
                        if (this.textLayout != null) {
                            i2 += AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                        }
                    }
                    i3 = i2;
                    this.lineHeight = i3;
                } else {
                    i3 = 0;
                }
            }
            setMeasuredDimension(i, i3);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
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
                    canvas.translate((float) AndroidUtilities.dp((float) (32 + i)), (float) AndroidUtilities.dp(29.0f));
                    this.dateLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
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
        }
    }

    private class BlockFooterCell extends View {
        private TL_pageBlockFooter currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockFooterCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockFooter tL_pageBlockFooter) {
            this.currentBlock = tL_pageBlockFooter;
            this.lastCreatedWidth = null;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (super.onTouchEvent(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            int i3 = 0;
            if (this.currentBlock != 0) {
                if (this.currentBlock.level == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((float) (18 + (14 * this.currentBlock.level)));
                }
                if (this.lastCreatedWidth != i) {
                    this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, (i - AndroidUtilities.dp(18.0f)) - this.textX, this.currentBlock);
                    if (this.textLayout != 0) {
                        i2 = this.textLayout.getHeight();
                        i3 = this.currentBlock.level > 0 ? AndroidUtilities.dp(8.0f) + i2 : AndroidUtilities.dp(16.0f) + i2;
                    }
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(i, i3);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
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
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockHeaderCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockHeader tL_pageBlockHeader) {
            this.currentBlock = tL_pageBlockHeader;
            this.lastCreatedWidth = null;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (super.onTouchEvent(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            int i3 = 0;
            if (this.currentBlock == 0) {
                i3 = 1;
            } else if (this.lastCreatedWidth != i) {
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(36.0f), this.currentBlock);
                if (this.textLayout != 0) {
                    i3 = 0 + (AndroidUtilities.dp(NUM) + this.textLayout.getHeight());
                }
            }
            setMeasuredDimension(i, i3);
        }

        protected void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockListCell extends View {
        private TL_pageBlockList currentBlock;
        private boolean hasRtl;
        private int lastCreatedWidth;
        private int maxLetterWidth;
        private ArrayList<StaticLayout> textLayouts = new ArrayList();
        private ArrayList<StaticLayout> textNumLayouts = new ArrayList();
        private int textX;
        private ArrayList<Integer> textYLayouts = new ArrayList();

        public BlockListCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockList tL_pageBlockList) {
            this.currentBlock = tL_pageBlockList;
            this.lastCreatedWidth = null;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            int size = this.textLayouts.size();
            int dp = AndroidUtilities.dp(36.0f);
            for (int i = 0; i < size; i++) {
                StaticLayout staticLayout = (StaticLayout) this.textLayouts.get(i);
                if (ArticleViewer.this.checkLayoutForLinks(motionEvent, this, staticLayout, dp, ((Integer) this.textYLayouts.get(i)).intValue())) {
                    return true;
                }
            }
            return super.onTouchEvent(motionEvent);
        }

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            i2 = 0;
            this.hasRtl = false;
            this.maxLetterWidth = 0;
            if (this.currentBlock == null) {
                i2 = 1;
            } else if (this.lastCreatedWidth != i) {
                this.textLayouts.clear();
                this.textYLayouts.clear();
                this.textNumLayouts.clear();
                int size = this.currentBlock.items.size();
                for (int i3 = 0; i3 < size; i3++) {
                    StaticLayout access$9400;
                    CharSequence charSequence;
                    RichText richText = (RichText) this.currentBlock.items.get(i3);
                    if (i3 == 0) {
                        access$9400 = ArticleViewer.this.createLayoutForText(null, richText, (i - AndroidUtilities.dp(24.0f)) - this.maxLetterWidth, this.currentBlock);
                        if (access$9400 != null) {
                            int lineCount = access$9400.getLineCount();
                            for (int i4 = 0; i4 < lineCount; i4++) {
                                if (access$9400.getLineLeft(i4) > 0.0f) {
                                    this.hasRtl = true;
                                    ArticleViewer.this.isRtl = 1;
                                    break;
                                }
                            }
                        }
                    }
                    if (!this.currentBlock.ordered) {
                        charSequence = "\u2022";
                    } else if (this.hasRtl) {
                        charSequence = String.format(".%d", new Object[]{Integer.valueOf(i3 + 1)});
                    } else {
                        charSequence = String.format("%d.", new Object[]{Integer.valueOf(i3 + 1)});
                    }
                    access$9400 = ArticleViewer.this.createLayoutForText(charSequence, richText, i - AndroidUtilities.dp(54.0f), this.currentBlock);
                    this.textNumLayouts.add(access$9400);
                    if (this.currentBlock.ordered) {
                        if (access$9400 != null) {
                            this.maxLetterWidth = Math.max(this.maxLetterWidth, (int) Math.ceil((double) access$9400.getLineWidth(0)));
                        }
                    } else if (i3 == 0) {
                        this.maxLetterWidth = AndroidUtilities.dp(12.0f);
                    }
                }
                int i5 = 0;
                while (i2 < size) {
                    i5 += AndroidUtilities.dp(8.0f);
                    StaticLayout access$94002 = ArticleViewer.this.createLayoutForText(null, (RichText) this.currentBlock.items.get(i2), (i - AndroidUtilities.dp(42.0f)) - this.maxLetterWidth, this.currentBlock);
                    this.textYLayouts.add(Integer.valueOf(i5));
                    this.textLayouts.add(access$94002);
                    if (access$94002 != null) {
                        i5 += access$94002.getHeight();
                    }
                    i2++;
                }
                if (this.hasRtl != 0) {
                    this.textX = AndroidUtilities.dp(NUM);
                } else {
                    this.textX = AndroidUtilities.dp(24.0f) + this.maxLetterWidth;
                }
                i2 = AndroidUtilities.dp(8.0f) + i5;
            }
            setMeasuredDimension(i, i2);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int size = this.textLayouts.size();
                int measuredWidth = getMeasuredWidth();
                for (int i = 0; i < size; i++) {
                    StaticLayout staticLayout = (StaticLayout) this.textLayouts.get(i);
                    StaticLayout staticLayout2 = (StaticLayout) this.textNumLayouts.get(i);
                    canvas.save();
                    if (this.hasRtl) {
                        canvas.translate((float) ((measuredWidth - AndroidUtilities.dp(18.0f)) - ((int) Math.ceil((double) staticLayout2.getLineWidth(0)))), (float) ((Integer) this.textYLayouts.get(i)).intValue());
                    } else {
                        canvas.translate((float) AndroidUtilities.dp(18.0f), (float) ((Integer) this.textYLayouts.get(i)).intValue());
                    }
                    if (staticLayout2 != null) {
                        staticLayout2.draw(canvas);
                    }
                    canvas.restore();
                    canvas.save();
                    canvas.translate((float) this.textX, (float) ((Integer) this.textYLayouts.get(i)).intValue());
                    ArticleViewer.this.drawLayoutLink(canvas, staticLayout);
                    if (staticLayout != null) {
                        staticLayout.draw(canvas);
                    }
                    canvas.restore();
                }
            }
        }
    }

    private class BlockParagraphCell extends View {
        private TL_pageBlockParagraph currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private int textX;
        private int textY;

        public BlockParagraphCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockParagraph tL_pageBlockParagraph) {
            this.currentBlock = tL_pageBlockParagraph;
            this.lastCreatedWidth = null;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (super.onTouchEvent(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            int i3 = 0;
            if (this.currentBlock != 0) {
                if (this.currentBlock.level == 0) {
                    if (this.currentBlock.caption != 0) {
                        this.textY = AndroidUtilities.dp(NUM);
                    } else {
                        this.textY = AndroidUtilities.dp(8.0f);
                    }
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((float) (18 + (14 * this.currentBlock.level)));
                }
                if (this.lastCreatedWidth != i) {
                    if (this.currentBlock.text != 0) {
                        this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, (i - AndroidUtilities.dp(18.0f)) - this.textX, this.currentBlock);
                    } else if (this.currentBlock.caption != 0) {
                        this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, (i - AndroidUtilities.dp(18.0f)) - this.textX, this.currentBlock);
                    }
                    if (this.textLayout != 0) {
                        i2 = this.textLayout.getHeight();
                        i3 = this.currentBlock.level > 0 ? AndroidUtilities.dp(8.0f) + i2 : AndroidUtilities.dp(16.0f) + i2;
                    }
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(i, i3);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockPhotoCell extends FrameLayout {
        private BlockChannelCell channelCell;
        private TL_pageBlockPhoto currentBlock;
        private int currentType;
        private ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private boolean isLast;
        private int lastCreatedWidth;
        private PageBlock parentBlock;
        private boolean photoPressed;
        private StaticLayout textLayout;
        private int textX;
        private int textY;

        public BlockPhotoCell(Context context, int i) {
            super(context);
            setWillNotDraw(false);
            this.channelCell = new BlockChannelCell(context, 1);
            addView(this.channelCell, LayoutHelper.createFrame(-1, -2.0f));
            this.currentType = i;
        }

        public void setBlock(TL_pageBlockPhoto tL_pageBlockPhoto, boolean z, boolean z2) {
            this.parentBlock = null;
            this.currentBlock = tL_pageBlockPhoto;
            this.lastCreatedWidth = null;
            this.isFirst = z;
            this.isLast = z2;
            this.channelCell.setVisibility(true);
            requestLayout();
        }

        public void setParentBlock(PageBlock pageBlock) {
            this.parentBlock = pageBlock;
            if (ArticleViewer.this.channelBlock != null && (this.parentBlock instanceof TL_pageBlockCover) != null) {
                this.channelCell.setBlock(ArticleViewer.this.channelBlock);
                this.channelCell.setVisibility(0);
            }
        }

        public View getChannelCell() {
            return this.channelCell;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            boolean z = false;
            if (this.channelCell.getVisibility() != 0 || y <= this.channelCell.getTranslationY() || y >= this.channelCell.getTranslationY() + ((float) AndroidUtilities.dp(39.0f))) {
                if (motionEvent.getAction() == 0 && this.imageView.isInsideImage(x, y)) {
                    this.photoPressed = true;
                } else if (motionEvent.getAction() == 1 && this.photoPressed) {
                    this.photoPressed = false;
                    ArticleViewer.this.openPhoto(this.currentBlock);
                } else if (motionEvent.getAction() == 3) {
                    this.photoPressed = false;
                }
                if (this.photoPressed || ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent) != null) {
                    z = true;
                }
                return z;
            }
            if (ArticleViewer.this.channelBlock != null && motionEvent.getAction() == 1) {
                MessagesController.getInstance(ArticleViewer.this.currentAccount).openByUserName(ArticleViewer.this.channelBlock.channel.username, ArticleViewer.this.parentFragment, 2);
                ArticleViewer.this.close(false, true);
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            int measuredHeight;
            int size = MeasureSpec.getSize(i);
            int i3 = 0;
            int i4 = 1;
            if (this.currentType == 1) {
                size = ArticleViewer.this.listView.getWidth();
                measuredHeight = ((View) getParent()).getMeasuredHeight();
            } else {
                measuredHeight = r0.currentType == 2 ? size : 0;
            }
            if (r0.currentBlock != null) {
                int i5;
                int dp;
                int i6;
                Photo access$10200 = ArticleViewer.this.getPhotoWithId(r0.currentBlock.photo_id);
                if (r0.currentType != 0 || r0.currentBlock.level <= 0) {
                    r0.textX = AndroidUtilities.dp(18.0f);
                    i5 = 0;
                    dp = size - AndroidUtilities.dp(36.0f);
                    i6 = size;
                } else {
                    i5 = AndroidUtilities.dp((float) (14 * r0.currentBlock.level)) + AndroidUtilities.dp(18.0f);
                    r0.textX = i5;
                    i6 = size - (AndroidUtilities.dp(18.0f) + i5);
                    dp = i6;
                }
                if (access$10200 != null) {
                    int dp2;
                    String str;
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(access$10200.sizes, AndroidUtilities.getPhotoSize());
                    PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(access$10200.sizes, 80, true);
                    if (closestPhotoSizeWithSize == closestPhotoSizeWithSize2) {
                        closestPhotoSizeWithSize2 = null;
                    }
                    if (r0.currentType == 0) {
                        measuredHeight = (int) ((((float) i6) / ((float) closestPhotoSizeWithSize.f43w)) * ((float) closestPhotoSizeWithSize.f42h));
                        if (r0.parentBlock instanceof TL_pageBlockCover) {
                            measuredHeight = Math.min(measuredHeight, i6);
                        } else {
                            int max = (int) (((float) (Math.max(ArticleViewer.this.listView.getMeasuredWidth(), ArticleViewer.this.listView.getMeasuredHeight()) - AndroidUtilities.dp(56.0f))) * 0.9f);
                            if (measuredHeight > max) {
                                i6 = (int) ((((float) max) / ((float) closestPhotoSizeWithSize.f42h)) * ((float) closestPhotoSizeWithSize.f43w));
                                i5 += ((size - i5) - i6) / 2;
                                measuredHeight = max;
                            }
                        }
                    }
                    ImageReceiver imageReceiver = r0.imageView;
                    if (!(r0.isFirst || r0.currentType == 1 || r0.currentType == 2)) {
                        if (r0.currentBlock.level <= 0) {
                            dp2 = AndroidUtilities.dp(8.0f);
                            imageReceiver.setImageCoords(i5, dp2, i6, measuredHeight);
                            if (r0.currentType != 0) {
                                str = null;
                            } else {
                                str = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(i6), Integer.valueOf(measuredHeight)});
                            }
                            r0.imageView.setImage(closestPhotoSizeWithSize.location, str, closestPhotoSizeWithSize2 == null ? closestPhotoSizeWithSize2.location : null, closestPhotoSizeWithSize2 == null ? "80_80_b" : null, closestPhotoSizeWithSize.size, null, 1);
                        }
                    }
                    dp2 = 0;
                    imageReceiver.setImageCoords(i5, dp2, i6, measuredHeight);
                    if (r0.currentType != 0) {
                        str = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(i6), Integer.valueOf(measuredHeight)});
                    } else {
                        str = null;
                    }
                    if (closestPhotoSizeWithSize2 == null) {
                    }
                    if (closestPhotoSizeWithSize2 == null) {
                    }
                    r0.imageView.setImage(closestPhotoSizeWithSize.location, str, closestPhotoSizeWithSize2 == null ? closestPhotoSizeWithSize2.location : null, closestPhotoSizeWithSize2 == null ? "80_80_b" : null, closestPhotoSizeWithSize.size, null, 1);
                }
                if (r0.currentType == 0 && r0.lastCreatedWidth != size) {
                    r0.textLayout = ArticleViewer.this.createLayoutForText(null, r0.currentBlock.caption, dp, r0.currentBlock);
                    if (r0.textLayout != null) {
                        measuredHeight += AndroidUtilities.dp(8.0f) + r0.textLayout.getHeight();
                    }
                }
                if (!r0.isFirst && r0.currentType == 0 && r0.currentBlock.level <= 0) {
                    measuredHeight += AndroidUtilities.dp(8.0f);
                }
                if ((r0.parentBlock instanceof TL_pageBlockCover) && ArticleViewer.this.blocks != null && ArticleViewer.this.blocks.size() > 1 && (ArticleViewer.this.blocks.get(1) instanceof TL_pageBlockChannel)) {
                    i3 = 1;
                }
                if (r0.currentType != 2 && r4 == 0) {
                    measuredHeight += AndroidUtilities.dp(8.0f);
                }
                i4 = measuredHeight;
            }
            r0.channelCell.measure(i, i2);
            r0.channelCell.setTranslationY((float) (r0.imageView.getImageHeight() - AndroidUtilities.dp(39.0f)));
            setMeasuredDimension(size, i4);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                this.imageView.draw(canvas);
                if (this.textLayout != null) {
                    canvas.save();
                    float f = (float) this.textX;
                    int imageY = (this.imageView.getImageY() + this.imageView.getImageHeight()) + AndroidUtilities.dp(8.0f);
                    this.textY = imageY;
                    canvas.translate(f, (float) imageY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }
    }

    private class BlockPreformattedCell extends View {
        private TL_pageBlockPreformatted currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;

        public BlockPreformattedCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockPreformatted tL_pageBlockPreformatted) {
            this.currentBlock = tL_pageBlockPreformatted;
            this.lastCreatedWidth = null;
            requestLayout();
        }

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            int i3 = 0;
            if (this.currentBlock == 0) {
                i3 = 1;
            } else if (this.lastCreatedWidth != i) {
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(24.0f), this.currentBlock);
                if (this.textLayout != 0) {
                    i3 = 0 + (AndroidUtilities.dp(NUM) + this.textLayout.getHeight());
                }
            }
            setMeasuredDimension(i, i3);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.drawRect(0.0f, (float) AndroidUtilities.dp(8.0f), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(8.0f)), ArticleViewer.preformattedBackgroundPaint);
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(16.0f));
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockPullquoteCell extends View {
        private TL_pageBlockPullquote currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private StaticLayout textLayout2;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);
        private int textY2;

        public BlockPullquoteCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockPullquote tL_pageBlockPullquote) {
            this.currentBlock = tL_pageBlockPullquote;
            this.lastCreatedWidth = null;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!(ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout2, this.textX, this.textY2))) {
                if (super.onTouchEvent(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            int i3 = 0;
            if (this.currentBlock == 0) {
                i3 = 1;
            } else if (this.lastCreatedWidth != i) {
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(36.0f), this.currentBlock);
                if (this.textLayout != 0) {
                    i3 = 0 + (AndroidUtilities.dp(8.0f) + this.textLayout.getHeight());
                }
                this.textLayout2 = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, i - AndroidUtilities.dp(36.0f), this.currentBlock);
                if (this.textLayout2 != 0) {
                    this.textY2 = AndroidUtilities.dp(NUM) + i3;
                    i3 += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                }
                if (i3 != 0) {
                    i3 += AndroidUtilities.dp(8.0f);
                }
            }
            setMeasuredDimension(i, i3);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.textLayout2 != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY2);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout2);
                    this.textLayout2.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private class BlockSlideshowCell extends FrameLayout {
        private PagerAdapter adapter;
        private TL_pageBlockSlideshow currentBlock;
        private View dotsContainer;
        private ViewPager innerListView;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockSlideshowCell(Context context) {
            super(context);
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
                }

                public void onPageSelected(int i) {
                    BlockSlideshowCell.this.dotsContainer.invalidate();
                }
            });
            ViewPager viewPager = this.innerListView;
            PagerAdapter c19203 = new PagerAdapter(ArticleViewer.this) {

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

                public boolean isViewFromObject(View view, Object obj) {
                    return ((ObjectContainer) obj).view == view ? true : null;
                }

                public int getItemPosition(Object obj) {
                    return BlockSlideshowCell.this.currentBlock.items.contains(((ObjectContainer) obj).block) != null ? -1 : -2;
                }

                public Object instantiateItem(ViewGroup viewGroup, int i) {
                    View blockPhotoCell;
                    PageBlock pageBlock = (PageBlock) BlockSlideshowCell.this.currentBlock.items.get(i);
                    if (pageBlock instanceof TL_pageBlockPhoto) {
                        blockPhotoCell = new BlockPhotoCell(BlockSlideshowCell.this.getContext(), 1);
                        ((BlockPhotoCell) blockPhotoCell).setBlock((TL_pageBlockPhoto) pageBlock, true, true);
                    } else {
                        blockPhotoCell = new BlockVideoCell(BlockSlideshowCell.this.getContext(), 1);
                        ((BlockVideoCell) blockPhotoCell).setBlock((TL_pageBlockVideo) pageBlock, true, true);
                    }
                    viewGroup.addView(blockPhotoCell);
                    viewGroup = new ObjectContainer();
                    viewGroup.view = blockPhotoCell;
                    viewGroup.block = pageBlock;
                    return viewGroup;
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
            this.adapter = c19203;
            viewPager.setAdapter(c19203);
            int access$8900 = ArticleViewer.this.getSelectedColor();
            if (access$8900 == 0) {
                AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, -657673);
            } else if (access$8900 == 1) {
                AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, -659492);
            } else if (access$8900 == 2) {
                AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, -15461356);
            }
            addView(this.innerListView);
            this.dotsContainer = new View(context, ArticleViewer.this) {
                protected void onDraw(Canvas canvas) {
                    if (BlockSlideshowCell.this.currentBlock != null) {
                        int currentItem = BlockSlideshowCell.this.innerListView.getCurrentItem();
                        int i = 0;
                        while (i < BlockSlideshowCell.this.currentBlock.items.size()) {
                            int dp = AndroidUtilities.dp(4.0f) + (AndroidUtilities.dp(13.0f) * i);
                            Drawable access$12100 = currentItem == i ? ArticleViewer.this.slideDotBigDrawable : ArticleViewer.this.slideDotDrawable;
                            access$12100.setBounds(dp - AndroidUtilities.dp(5.0f), 0, dp + AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f));
                            access$12100.draw(canvas);
                            i++;
                        }
                    }
                }
            };
            addView(this.dotsContainer);
            setWillNotDraw(null);
        }

        public void setBlock(TL_pageBlockSlideshow tL_pageBlockSlideshow) {
            this.currentBlock = tL_pageBlockSlideshow;
            this.lastCreatedWidth = 0;
            this.innerListView.setCurrentItem(0, false);
            this.adapter.notifyDataSetChanged();
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (super.onTouchEvent(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            int i3 = 1;
            if (this.currentBlock != 0) {
                i2 = AndroidUtilities.dp(NUM);
                this.innerListView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
                int size = this.currentBlock.items.size();
                this.dotsContainer.measure(MeasureSpec.makeMeasureSpec(((AndroidUtilities.dp(7.0f) * size) + ((size - 1) * AndroidUtilities.dp(6.0f))) + AndroidUtilities.dp(4.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f), NUM));
                if (this.lastCreatedWidth != i) {
                    this.textY = AndroidUtilities.dp(16.0f) + i2;
                    this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.caption, i - AndroidUtilities.dp(36.0f), this.currentBlock);
                    if (this.textLayout != null) {
                        i2 += AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                    }
                }
                i3 = AndroidUtilities.dp(16.0f) + i2;
            }
            setMeasuredDimension(i, i3);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            this.innerListView.layout(0, AndroidUtilities.dp(8.0f), this.innerListView.getMeasuredWidth(), AndroidUtilities.dp(8.0f) + this.innerListView.getMeasuredHeight());
            z = this.innerListView.getBottom() - AndroidUtilities.dp(NUM);
            i3 = ((i3 - i) - this.dotsContainer.getMeasuredWidth()) / 2;
            this.dotsContainer.layout(i3, z, this.dotsContainer.getMeasuredWidth() + i3, this.dotsContainer.getMeasuredHeight() + z);
        }

        protected void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockSubheaderCell extends View {
        private TL_pageBlockSubheader currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockSubheaderCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockSubheader tL_pageBlockSubheader) {
            this.currentBlock = tL_pageBlockSubheader;
            this.lastCreatedWidth = null;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (super.onTouchEvent(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            int i3 = 0;
            if (this.currentBlock == 0) {
                i3 = 1;
            } else if (this.lastCreatedWidth != i) {
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(36.0f), this.currentBlock);
                if (this.textLayout != 0) {
                    i3 = 0 + (AndroidUtilities.dp(NUM) + this.textLayout.getHeight());
                }
            }
            setMeasuredDimension(i, i3);
        }

        protected void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockSubtitleCell extends View {
        private TL_pageBlockSubtitle currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockSubtitleCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockSubtitle tL_pageBlockSubtitle) {
            this.currentBlock = tL_pageBlockSubtitle;
            this.lastCreatedWidth = null;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (super.onTouchEvent(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            int i3 = 0;
            if (this.currentBlock == 0) {
                i3 = 1;
            } else if (this.lastCreatedWidth != i) {
                this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(36.0f), this.currentBlock);
                if (this.textLayout != 0) {
                    i3 = 0 + (AndroidUtilities.dp(NUM) + this.textLayout.getHeight());
                }
            }
            setMeasuredDimension(i, i3);
        }

        protected void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }
    }

    private class BlockTitleCell extends View {
        private TL_pageBlockTitle currentBlock;
        private int lastCreatedWidth;
        private StaticLayout textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockTitleCell(Context context) {
            super(context);
        }

        public void setBlock(TL_pageBlockTitle tL_pageBlockTitle) {
            this.currentBlock = tL_pageBlockTitle;
            this.lastCreatedWidth = null;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (super.onTouchEvent(motionEvent) == null) {
                    return null;
                }
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            int i3 = 1;
            if (this.currentBlock != 0) {
                if (this.lastCreatedWidth != i) {
                    this.textLayout = ArticleViewer.this.createLayoutForText(null, this.currentBlock.text, i - AndroidUtilities.dp(36.0f), this.currentBlock);
                    if (this.textLayout != 0) {
                        i2 = (AndroidUtilities.dp(16.0f) + this.textLayout.getHeight()) + 0;
                        if (ArticleViewer.this.isRtl == -1) {
                            int lineCount = this.textLayout.getLineCount();
                            for (int i4 = 0; i4 < lineCount; i4++) {
                                if (this.textLayout.getLineLeft(i4) > 0.0f) {
                                    ArticleViewer.this.isRtl = 1;
                                    break;
                                }
                            }
                            if (ArticleViewer.this.isRtl == -1) {
                                ArticleViewer.this.isRtl = 0;
                            }
                        }
                        i3 = i2;
                    } else {
                        i3 = 0;
                    }
                    if (this.currentBlock.first != 0) {
                        i3 += AndroidUtilities.dp(8.0f);
                        this.textY = AndroidUtilities.dp(16.0f);
                    } else {
                        this.textY = AndroidUtilities.dp(8.0f);
                    }
                } else {
                    i3 = 0;
                }
            }
            setMeasuredDimension(i, i3);
        }

        protected void onDraw(Canvas canvas) {
            if (!(this.currentBlock == null || this.textLayout == null)) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                this.textLayout.draw(canvas);
                canvas.restore();
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
                ArticleViewer.this.windowView.performHapticFeedback(0);
                if (ArticleViewer.this.pressedLink != null) {
                    final Object url = ArticleViewer.this.pressedLink.getUrl();
                    Builder builder = new Builder(ArticleViewer.this.parentActivity);
                    builder.setTitle(url);
                    builder.setItems(new CharSequence[]{LocaleController.getString("Open", C0446R.string.Open), LocaleController.getString("Copy", C0446R.string.Copy)}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (ArticleViewer.this.parentActivity != null) {
                                if (i == 0) {
                                    Browser.openUrl(ArticleViewer.this.parentActivity, url);
                                } else if (i == 1) {
                                    dialogInterface = url;
                                    if (dialogInterface.startsWith("mailto:") != 0) {
                                        dialogInterface = dialogInterface.substring(7);
                                    } else if (dialogInterface.startsWith("tel:") != 0) {
                                        dialogInterface = dialogInterface.substring(4);
                                    }
                                    AndroidUtilities.addToClipboard(dialogInterface);
                                }
                            }
                        }
                    });
                    ArticleViewer.this.showDialog(builder.create());
                    ArticleViewer.this.hideActionBar();
                    ArticleViewer.this.pressedLink = null;
                    ArticleViewer.this.pressedLinkOwnerLayout = null;
                    ArticleViewer.this.pressedLinkOwnerView.invalidate();
                } else if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLinkOwnerView != null) {
                    int top = (ArticleViewer.this.pressedLinkOwnerView.getTop() - AndroidUtilities.dp(54.0f)) + ArticleViewer.this.pressedLayoutY;
                    if (top < 0) {
                        top *= -1;
                    }
                    ArticleViewer.this.pressedLinkOwnerView.invalidate();
                    ArticleViewer.this.drawBlockSelection = true;
                    ArticleViewer.this.showPopup(ArticleViewer.this.pressedLinkOwnerView, 48, 0, top);
                    ArticleViewer.this.listView.setLayoutFrozen(true);
                    ArticleViewer.this.listView.setLayoutFrozen(false);
                }
            }
        }
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (ArticleViewer.this.pendingCheckForLongPress == null) {
                ArticleViewer.this.pendingCheckForLongPress = new CheckForLongPress();
            }
            ArticleViewer.this.pendingCheckForLongPress.currentPressCount = ArticleViewer.access$804(ArticleViewer.this);
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
            setBackgroundDrawable(Theme.createSelectorDrawable(251658240, 2));
            setWillNotDraw(false);
            this.textView = new TextView(context);
            this.textView.setTextColor(-14606047);
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            int i = 3;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0f));
            ArticleViewer articleViewer = this.textView;
            if (LocaleController.isRTL != null) {
                i = 5;
            }
            int i2 = i | 48;
            int i3 = 53;
            float f = (float) (LocaleController.isRTL != null ? 17 : 53);
            if (LocaleController.isRTL == null) {
                i3 = 17;
            }
            addView(articleViewer, LayoutHelper.createFrame(-1, -1.0f, i2, f, 0.0f, (float) i3, 0.0f));
        }

        protected void onMeasure(int i, int i2) {
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

        protected void onDraw(Canvas canvas) {
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

    public class FontCell extends FrameLayout {
        private TextView textView;
        private TextView textView2;
        final /* synthetic */ ArticleViewer this$0;

        public FontCell(ArticleViewer articleViewer, Context context) {
            Context context2 = context;
            this.this$0 = articleViewer;
            super(context2);
            setBackgroundDrawable(Theme.createSelectorDrawable(251658240, 2));
            this.textView = new TextView(context2);
            this.textView.setTextColor(-14606047);
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            int i = 3;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            View view = r0.textView;
            int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i3 = 53;
            float f = (float) (LocaleController.isRTL ? 17 : 53);
            if (!LocaleController.isRTL) {
                i3 = 17;
            }
            addView(view, LayoutHelper.createFrame(-1, -1.0f, i2, f, 0.0f, (float) i3, 0.0f));
            r0.textView2 = new TextView(context2);
            r0.textView2.setTextColor(-14606047);
            r0.textView2.setTextSize(1, 16.0f);
            r0.textView2.setLines(1);
            r0.textView2.setMaxLines(1);
            r0.textView2.setSingleLine(true);
            r0.textView2.setText("Aa");
            r0.textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            view = r0.textView2;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(view, LayoutHelper.createFrame(-1, -1.0f, i | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        }

        protected void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }

        public void select(boolean z) {
            this.textView2.setTextColor(z ? true : true);
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

        protected void onDraw(Canvas canvas) {
            ArticleViewer.this.drawContent(canvas);
        }

        protected boolean drawChild(Canvas canvas, View view, long j) {
            return (view == ArticleViewer.this.aspectRatioFrameLayout || super.drawChild(canvas, view, j) == null) ? null : true;
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
                boolean z;
                DrawerLayoutContainer drawerLayoutContainer = ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer;
                if (ArticleViewer.this.isPhotoVisible) {
                    if (i == 255) {
                        z = false;
                        drawerLayoutContainer.setAllowDrawContent(z);
                    }
                }
                z = true;
                drawerLayoutContainer.setAllowDrawContent(z);
            }
            super.setAlpha(i);
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (getAlpha() != null && this.drawRunnable != null) {
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
        private View parent = null;
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
                float f = this.currentProgress - this.animationProgressStart;
                if (f > 0.0f) {
                    this.currentProgressTime += j;
                    if (this.currentProgressTime >= 300) {
                        this.animatedProgressValue = this.currentProgress;
                        this.animationProgressStart = this.currentProgress;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = this.animationProgressStart + (f * ArticleViewer.decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / 300.0f));
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
            this.currentProgressTime = 0.0f;
        }

        public void setBackgroundState(int i, boolean z) {
            this.lastUpdateTime = System.currentTimeMillis();
            if (!z || this.backgroundState == i) {
                this.previousBackgroundState = true;
            } else {
                this.previousBackgroundState = this.backgroundState;
                this.animatedAlphaValue = true;
            }
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
            int access$13500 = (ArticleViewer.this.getContainerViewWidth() - i) / 2;
            int access$13600 = (ArticleViewer.this.getContainerViewHeight() - i) / 2;
            if (this.previousBackgroundState >= 0 && this.previousBackgroundState < 4) {
                drawable = ArticleViewer.progressDrawables[this.previousBackgroundState];
                if (drawable != null) {
                    drawable.setAlpha((int) ((this.animatedAlphaValue * 255.0f) * this.alpha));
                    drawable.setBounds(access$13500, access$13600, access$13500 + i, access$13600 + i);
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
                    drawable.setBounds(access$13500, access$13600, access$13500 + i, access$13600 + i);
                    drawable.draw(canvas);
                }
            }
            if (this.backgroundState == 0 || this.backgroundState == 1 || this.previousBackgroundState == 0 || this.previousBackgroundState == 1) {
                int dp = AndroidUtilities.dp(4.0f);
                if (this.previousBackgroundState != -2) {
                    ArticleViewer.progressPaint.setAlpha((int) ((255.0f * this.animatedAlphaValue) * this.alpha));
                } else {
                    ArticleViewer.progressPaint.setAlpha((int) (255.0f * this.alpha));
                }
                this.progressRect.set((float) (access$13500 + dp), (float) (access$13600 + dp), (float) ((access$13500 + i) - dp), (float) ((access$13600 + i) - dp));
                canvas.drawArc(this.progressRect, -90.0f + this.radOffset, Math.max(4.0f, 360.0f * this.animatedProgressValue), false, ArticleViewer.progressPaint);
                updateAnimation();
            }
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
            if (motionEvent.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                motionEvent = null;
                while (motionEvent < 5) {
                    i2 = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * motionEvent)) + (this.circleSize / 2);
                    if (x <= ((float) (i2 - AndroidUtilities.dp(15.0f))) || x >= ((float) (i2 + AndroidUtilities.dp(15.0f)))) {
                        motionEvent++;
                    } else {
                        boolean z;
                        if (motionEvent == ArticleViewer.this.selectedFontSize) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingQuality = ArticleViewer.this.selectedFontSize;
                    }
                }
            } else if (motionEvent.getAction() == 2) {
                if (this.startMoving != null) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving != null) {
                    while (i < 5) {
                        motionEvent = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * i)) + (this.circleSize / 2);
                        i2 = ((this.lineSize / 2) + (this.circleSize / 2)) + this.gapSize;
                        if (x <= ((float) (motionEvent - i2)) || x >= ((float) (motionEvent + i2))) {
                            i++;
                        } else if (ArticleViewer.this.selectedFontSize != i) {
                            ArticleViewer.this.selectedFontSize = i;
                            ArticleViewer.this.updatePaintSize();
                            invalidate();
                        }
                    }
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (this.moving == null) {
                    motionEvent = null;
                    while (motionEvent < 5) {
                        i2 = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * motionEvent)) + (this.circleSize / 2);
                        if (x <= ((float) (i2 - AndroidUtilities.dp(15.0f))) || x >= ((float) (i2 + AndroidUtilities.dp(15.0f)))) {
                            motionEvent++;
                        } else if (ArticleViewer.this.selectedFontSize != motionEvent) {
                            ArticleViewer.this.selectedFontSize = motionEvent;
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

        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            MeasureSpec.getSize(i);
            this.circleSize = AndroidUtilities.dp(NUM);
            this.gapSize = AndroidUtilities.dp(NUM);
            this.sideSide = AndroidUtilities.dp(NUM);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * 5)) - (this.gapSize * 8)) - (this.sideSide * 2)) / 4;
        }

        protected void onDraw(Canvas canvas) {
            int measuredHeight = getMeasuredHeight() / 2;
            int i = 0;
            while (i < 5) {
                int i2 = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * i)) + (this.circleSize / 2);
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

        protected void onMeasure(int i, int i2) {
            i = MeasureSpec.getSize(i);
            i2 = MeasureSpec.getSize(i2);
            if (VERSION.SDK_INT < 21 || ArticleViewer.this.lastInsets == null) {
                setMeasuredDimension(i, i2);
            } else {
                setMeasuredDimension(i, i2);
                WindowInsets windowInsets = (WindowInsets) ArticleViewer.this.lastInsets;
                if (AndroidUtilities.incorrectDisplaySizeFix) {
                    if (i2 > AndroidUtilities.displaySize.y) {
                        i2 = AndroidUtilities.displaySize.y;
                    }
                    i2 += AndroidUtilities.statusBarHeight;
                }
                i2 -= windowInsets.getSystemWindowInsetBottom();
                i -= windowInsets.getSystemWindowInsetRight() + windowInsets.getSystemWindowInsetLeft();
                if (windowInsets.getSystemWindowInsetRight() != 0) {
                    ArticleViewer.this.barBackground.measure(MeasureSpec.makeMeasureSpec(windowInsets.getSystemWindowInsetRight(), NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
                } else if (windowInsets.getSystemWindowInsetLeft() != 0) {
                    ArticleViewer.this.barBackground.measure(MeasureSpec.makeMeasureSpec(windowInsets.getSystemWindowInsetLeft(), NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
                } else {
                    ArticleViewer.this.barBackground.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(windowInsets.getSystemWindowInsetBottom(), NUM));
                }
            }
            ArticleViewer.this.containerView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            ArticleViewer.this.photoContainerView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            ArticleViewer.this.photoContainerBackground.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            ArticleViewer.this.fullscreenVideoContainer.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            i = ArticleViewer.this.animatingImageView.getLayoutParams();
            ArticleViewer.this.animatingImageView.measure(MeasureSpec.makeMeasureSpec(i.width, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(i.height, Integer.MIN_VALUE));
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            if (!this.selfLayout) {
                int i5;
                if (VERSION.SDK_INT < true || !ArticleViewer.this.lastInsets) {
                    i5 = 0;
                } else {
                    WindowInsets windowInsets = (WindowInsets) ArticleViewer.this.lastInsets;
                    i5 = windowInsets.getSystemWindowInsetLeft();
                    if (windowInsets.getSystemWindowInsetRight() != 0) {
                        i3 -= i;
                        ArticleViewer.this.barBackground.layout(i3 - windowInsets.getSystemWindowInsetRight(), 0, i3, i4 - i2);
                    } else if (windowInsets.getSystemWindowInsetLeft() != 0) {
                        ArticleViewer.this.barBackground.layout(0, 0, windowInsets.getSystemWindowInsetLeft(), i4 - i2);
                    } else {
                        i4 -= i2;
                        ArticleViewer.this.barBackground.layout(0, i4 - windowInsets.getStableInsetBottom(), i3 - i, i4);
                    }
                }
                ArticleViewer.this.containerView.layout(i5, 0, ArticleViewer.this.containerView.getMeasuredWidth() + i5, ArticleViewer.this.containerView.getMeasuredHeight());
                ArticleViewer.this.photoContainerView.layout(i5, 0, ArticleViewer.this.photoContainerView.getMeasuredWidth() + i5, ArticleViewer.this.photoContainerView.getMeasuredHeight());
                ArticleViewer.this.photoContainerBackground.layout(i5, 0, ArticleViewer.this.photoContainerBackground.getMeasuredWidth() + i5, ArticleViewer.this.photoContainerBackground.getMeasuredHeight());
                ArticleViewer.this.fullscreenVideoContainer.layout(i5, 0, ArticleViewer.this.fullscreenVideoContainer.getMeasuredWidth() + i5, ArticleViewer.this.fullscreenVideoContainer.getMeasuredHeight());
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

        public void requestDisallowInterceptTouchEvent(boolean z) {
            handleTouchEvent(null);
            super.requestDisallowInterceptTouchEvent(z);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return (ArticleViewer.this.collapsed || (!handleTouchEvent(motionEvent) && super.onInterceptTouchEvent(motionEvent) == null)) ? null : true;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return (ArticleViewer.this.collapsed || (!handleTouchEvent(motionEvent) && super.onTouchEvent(motionEvent) == null)) ? null : true;
        }

        @Keep
        public void setInnerTranslationX(float f) {
            this.innerTranslationX = f;
            if ((ArticleViewer.this.parentActivity instanceof LaunchActivity) != null) {
                boolean z;
                f = ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer;
                if (ArticleViewer.this.isVisible && this.alpha == 1.0f) {
                    if (this.innerTranslationX == 0.0f) {
                        z = false;
                        f.setAllowDrawContent(z);
                    }
                }
                z = true;
                f.setAllowDrawContent(z);
            }
            invalidate();
        }

        protected boolean drawChild(Canvas canvas, View view, long j) {
            int measuredWidth = getMeasuredWidth();
            int i = (int) this.innerTranslationX;
            int save = canvas.save();
            canvas.clipRect(i, 0, measuredWidth, getHeight());
            j = super.drawChild(canvas, view, j);
            canvas.restoreToCount(save);
            if (i != 0 && view == ArticleViewer.this.containerView) {
                float f = (float) (measuredWidth - i);
                float min = Math.min(0.8f, f / ((float) measuredWidth));
                if (min < 0.0f) {
                    min = 0.0f;
                }
                ArticleViewer.this.scrimPaint.setColor(((int) (153.0f * min)) << 24);
                canvas.drawRect(0.0f, 0.0f, (float) i, (float) getHeight(), ArticleViewer.this.scrimPaint);
                min = Math.max(0.0f, Math.min(f / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                ArticleViewer.this.layerShadowDrawable.setBounds(i - ArticleViewer.this.layerShadowDrawable.getIntrinsicWidth(), view.getTop(), i, view.getBottom());
                ArticleViewer.this.layerShadowDrawable.setAlpha((int) (255.0f * min));
                ArticleViewer.this.layerShadowDrawable.draw(canvas);
            }
            return j;
        }

        @Keep
        public float getInnerTranslationX() {
            return this.innerTranslationX;
        }

        private void prepareForMoving(MotionEvent motionEvent) {
            this.maybeStartTracking = false;
            this.startedTracking = true;
            this.startedTrackingX = (int) motionEvent.getX();
        }

        public boolean handleTouchEvent(MotionEvent motionEvent) {
            if (ArticleViewer.this.isPhotoVisible || this.closeAnimationInProgress || ArticleViewer.this.fullscreenVideoContainer.getVisibility() == 0) {
                return false;
            }
            if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                this.startedTrackingY = (int) motionEvent.getY();
                if (this.tracker != null) {
                    this.tracker.clear();
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
                } else if (this.startedTracking != null) {
                    float f = (float) max;
                    ArticleViewer.this.containerView.setTranslationX(f);
                    setInnerTranslationX(f);
                }
            } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6)) {
                float yVelocity;
                if (this.tracker == null) {
                    this.tracker = VelocityTracker.obtain();
                }
                this.tracker.computeCurrentVelocity(1000);
                if (!this.startedTracking) {
                    float xVelocity = this.tracker.getXVelocity();
                    yVelocity = this.tracker.getYVelocity();
                    if (xVelocity >= 3500.0f && xVelocity > Math.abs(yVelocity)) {
                        prepareForMoving(motionEvent);
                    }
                }
                if (this.startedTracking != null) {
                    motionEvent = ArticleViewer.this.containerView.getX();
                    AnimatorSet animatorSet = new AnimatorSet();
                    yVelocity = this.tracker.getXVelocity();
                    final boolean z = motionEvent < ((float) ArticleViewer.this.containerView.getMeasuredWidth()) / 3.0f && (yVelocity < 3500.0f || yVelocity < this.tracker.getYVelocity());
                    Animator[] animatorArr;
                    if (z) {
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.containerView, "translationX", new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                    } else {
                        motionEvent = ((float) ArticleViewer.this.containerView.getMeasuredWidth()) - motionEvent;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.containerView, "translationX", new float[]{(float) ArticleViewer.this.containerView.getMeasuredWidth()});
                        animatorArr[1] = ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{(float) ArticleViewer.this.containerView.getMeasuredWidth()});
                        animatorSet.playTogether(animatorArr);
                    }
                    animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) ArticleViewer.this.containerView.getMeasuredWidth())) * motionEvent), 50));
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (z == null) {
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
            } else if (motionEvent == null) {
                this.maybeStartTracking = false;
                this.startedTracking = false;
                if (this.tracker != null) {
                    this.tracker.recycle();
                    this.tracker = null;
                }
            }
            return this.startedTracking;
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawRect(this.innerTranslationX, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), ArticleViewer.this.backgroundPaint);
        }

        @Keep
        public void setAlpha(float f) {
            ArticleViewer.this.backgroundPaint.setAlpha((int) (255.0f * f));
            this.alpha = f;
            if ((ArticleViewer.this.parentActivity instanceof LaunchActivity) != null) {
                boolean z;
                f = ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer;
                if (ArticleViewer.this.isVisible && this.alpha == 1.0f) {
                    if (this.innerTranslationX == 0.0f) {
                        z = false;
                        f.setAllowDrawContent(z);
                    }
                }
                z = true;
                f.setAllowDrawContent(z);
            }
            invalidate();
        }

        @Keep
        public float getAlpha() {
            return this.alpha;
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$2 */
    class C19102 implements OnDispatchKeyEventListener {
        C19102() {
        }

        public void onDispatchKeyEvent(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == null && ArticleViewer.this.popupWindow != null && ArticleViewer.this.popupWindow.isShowing() != null) {
                ArticleViewer.this.popupWindow.dismiss();
            }
        }
    }

    /* renamed from: org.telegram.ui.ArticleViewer$9 */
    class C19139 implements OnItemLongClickListener {
        public boolean onItemClick(View view, int i) {
            return false;
        }

        C19139() {
        }
    }

    private class BlockAudioCell extends View implements FileDownloadProgressListener {
        private int TAG;
        private int buttonPressed;
        private int buttonState;
        private int buttonX;
        private int buttonY;
        private TL_pageBlockAudio currentBlock;
        private Document currentDocument;
        private MessageObject currentMessageObject;
        private StaticLayout durationLayout;
        private boolean isFirst;
        private boolean isLast;
        private int lastCreatedWidth;
        private String lastTimeString;
        private RadialProgress radialProgress = new RadialProgress(this);
        private SeekBar seekBar;
        private int seekBarX;
        private int seekBarY;
        private StaticLayout textLayout;
        private int textX;
        private int textY = AndroidUtilities.dp(54.0f);
        private StaticLayout titleLayout;

        public void onProgressUpload(String str, float f, boolean z) {
        }

        public BlockAudioCell(Context context) {
            super(context);
            this.radialProgress.setAlphaForPrevious(true);
            this.radialProgress.setDiff(AndroidUtilities.dp(0.0f));
            this.radialProgress.setStrikeWidth(AndroidUtilities.dp(2.0f));
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            this.seekBar = new SeekBar(context);
            this.seekBar.setDelegate(new SeekBarDelegate(ArticleViewer.this) {
                public void onSeekBarDrag(float f) {
                    if (BlockAudioCell.this.currentMessageObject != null) {
                        BlockAudioCell.this.currentMessageObject.audioProgress = f;
                        MediaController.getInstance().seekToProgress(BlockAudioCell.this.currentMessageObject, f);
                    }
                }
            });
        }

        public void setBlock(TL_pageBlockAudio tL_pageBlockAudio, boolean z, boolean z2) {
            this.currentBlock = tL_pageBlockAudio;
            this.currentMessageObject = (MessageObject) ArticleViewer.this.audioBlocks.get(this.currentBlock);
            this.currentDocument = this.currentMessageObject.getDocument();
            this.lastCreatedWidth = 0;
            this.isFirst = z;
            this.isLast = z2;
            this.radialProgress.setProgressColor(ArticleViewer.this.getTextColor());
            this.seekBar.setColors(ArticleViewer.this.getTextColor() & NUM, ArticleViewer.this.getTextColor() & NUM, ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor(), ArticleViewer.this.getTextColor());
            updateButtonState(false);
            requestLayout();
        }

        public MessageObject getMessageObject() {
            return this.currentMessageObject;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            boolean z = true;
            if (this.seekBar.onTouch(motionEvent.getAction(), motionEvent.getX() - ((float) this.seekBarX), motionEvent.getY() - ((float) this.seekBarY))) {
                if (motionEvent.getAction() == null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                invalidate();
                return true;
            }
            if (motionEvent.getAction() == 0) {
                if ((this.buttonState != -1 && x >= ((float) this.buttonX) && x <= ((float) (this.buttonX + AndroidUtilities.dp(48.0f))) && y >= ((float) this.buttonY) && y <= ((float) (this.buttonY + AndroidUtilities.dp(48.0f)))) || this.buttonState == 0) {
                    this.buttonPressed = 1;
                    invalidate();
                }
            } else if (motionEvent.getAction() == 1) {
                if (this.buttonPressed == 1) {
                    this.buttonPressed = 0;
                    playSoundEffect(0);
                    didPressedButton(false);
                    this.radialProgress.swapBackground(getDrawableForCurrentState());
                    invalidate();
                }
            } else if (motionEvent.getAction() == 3) {
                this.buttonPressed = 0;
            }
            if (this.buttonPressed == 0 && !ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY)) {
                if (super.onTouchEvent(motionEvent) == null) {
                    z = false;
                }
            }
            return z;
        }

        @SuppressLint({"DrawAllocation"})
        protected void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i);
            int dp = AndroidUtilities.dp(54.0f);
            if (this.currentBlock != null) {
                float f;
                CharSequence spannableStringBuilder;
                if (r0.currentBlock.level > 0) {
                    r0.textX = AndroidUtilities.dp((float) (14 * r0.currentBlock.level)) + AndroidUtilities.dp(18.0f);
                } else {
                    r0.textX = AndroidUtilities.dp(18.0f);
                }
                int dp2 = (size - r0.textX) - AndroidUtilities.dp(18.0f);
                int dp3 = AndroidUtilities.dp(40.0f);
                r0.buttonX = AndroidUtilities.dp(16.0f);
                r0.buttonY = AndroidUtilities.dp(7.0f);
                r0.currentBlock.caption = new TL_textPlain();
                r0.radialProgress.setProgressRect(r0.buttonX, r0.buttonY, r0.buttonX + dp3, r0.buttonY + dp3);
                if (r0.lastCreatedWidth != size) {
                    r0.textLayout = ArticleViewer.this.createLayoutForText(null, r0.currentBlock.caption, dp2, r0.currentBlock);
                    if (r0.textLayout != null) {
                        dp += AndroidUtilities.dp(8.0f) + r0.textLayout.getHeight();
                    }
                }
                if (!r0.isFirst && r0.currentBlock.level <= 0) {
                    dp += AndroidUtilities.dp(8.0f);
                }
                Object musicAuthor = r0.currentMessageObject.getMusicAuthor(false);
                CharSequence musicTitle = r0.currentMessageObject.getMusicTitle(false);
                r0.seekBarX = (r0.buttonX + AndroidUtilities.dp(50.0f)) + dp3;
                int dp4 = (size - r0.seekBarX) - AndroidUtilities.dp(18.0f);
                if (TextUtils.isEmpty(musicTitle)) {
                    if (TextUtils.isEmpty(musicAuthor)) {
                        r0.titleLayout = null;
                        r0.seekBarY = r0.buttonY + ((dp3 - AndroidUtilities.dp(30.0f)) / 2);
                        f = 30.0f;
                        r0.seekBar.setSize(dp4, AndroidUtilities.dp(f));
                    }
                }
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
                f = 30.0f;
                int i3 = 2;
                r0.titleLayout = new StaticLayout(TextUtils.ellipsize(spannableStringBuilder, Theme.chat_audioTitlePaint, (float) dp4, TruncateAt.END), ArticleViewer.audioTimePaint, dp4, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                r0.seekBarY = (r0.buttonY + ((dp3 - AndroidUtilities.dp(f)) / i3)) + AndroidUtilities.dp(11.0f);
                r0.seekBar.setSize(dp4, AndroidUtilities.dp(f));
            } else {
                dp = 1;
            }
            setMeasuredDimension(size, dp);
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
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
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
            if (this.currentDocument != null) {
                if (this.currentMessageObject != null) {
                    int i;
                    if (!this.seekBar.isDragging()) {
                        this.seekBar.setProgress(this.currentMessageObject.audioProgress);
                    }
                    if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                        i = this.currentMessageObject.audioProgressSec;
                    } else {
                        for (i = 0; i < this.currentDocument.attributes.size(); i++) {
                            DocumentAttribute documentAttribute = (DocumentAttribute) this.currentDocument.attributes.get(i);
                            if (documentAttribute instanceof TL_documentAttributeAudio) {
                                i = documentAttribute.duration;
                                break;
                            }
                        }
                        i = 0;
                    }
                    CharSequence format = String.format("%d:%02d", new Object[]{Integer.valueOf(i / 60), Integer.valueOf(i % 60)});
                    if (this.lastTimeString == null || !(this.lastTimeString == null || this.lastTimeString.equals(format))) {
                        this.lastTimeString = format;
                        ArticleViewer.audioTimePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
                        this.durationLayout = new StaticLayout(format, ArticleViewer.audioTimePaint, (int) Math.ceil((double) ArticleViewer.audioTimePaint.measureText(format)), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    }
                    ArticleViewer.audioTimePaint.setColor(ArticleViewer.this.getTextColor());
                    invalidate();
                }
            }
        }

        public void updateButtonState(boolean z) {
            String attachFileName = FileLoader.getAttachFileName(this.currentDocument);
            boolean exists = FileLoader.getPathToAttach(this.currentDocument, true).exists();
            if (TextUtils.isEmpty(attachFileName)) {
                this.radialProgress.setBackground(null, false, false);
                return;
            }
            if (exists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (isPlayingMessage) {
                    if (!isPlayingMessage || !MediaController.getInstance().isMessagePaused()) {
                        this.buttonState = 1;
                        this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                    }
                }
                this.buttonState = 0;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
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
                    this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
                } else {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, z);
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                }
            }
            updatePlayingMessageProgress();
        }

        private void didPressedButton(boolean z) {
            if (this.buttonState) {
                if (this.buttonState) {
                    if (MediaController.getInstance().pauseMessage(this.currentMessageObject)) {
                        this.buttonState = 0;
                        this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                        invalidate();
                    }
                } else if (this.buttonState) {
                    this.radialProgress.setProgress(0.0f, false);
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).loadFile(this.currentDocument, true, 1);
                    this.buttonState = 3;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
                    invalidate();
                } else if (this.buttonState) {
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).cancelLoadFile(this.currentDocument);
                    this.buttonState = 2;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                    invalidate();
                }
            } else if (MediaController.getInstance().setPlaylist(ArticleViewer.this.audioMessages, this.currentMessageObject, false)) {
                this.buttonState = 1;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        }

        public void onFailedDownload(String str) {
            updateButtonState(true);
        }

        public void onSuccessDownload(String str) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        public void onProgressDownload(String str, float f) {
            this.radialProgress.setProgress(f, true);
            if (this.buttonState != 4.2E-45f) {
                updateButtonState(null);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    private class BlockVideoCell extends FrameLayout implements FileDownloadProgressListener {
        private int TAG;
        private int buttonPressed;
        private int buttonState;
        private int buttonX;
        private int buttonY;
        private boolean cancelLoading;
        private BlockChannelCell channelCell;
        private TL_pageBlockVideo currentBlock;
        private Document currentDocument;
        private int currentType;
        private ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private boolean isGif;
        private boolean isLast;
        private int lastCreatedWidth;
        private PageBlock parentBlock;
        private boolean photoPressed;
        private RadialProgress radialProgress;
        private StaticLayout textLayout;
        private int textX;
        private int textY;

        public void onProgressUpload(String str, float f, boolean z) {
        }

        public BlockVideoCell(Context context, int i) {
            super(context);
            setWillNotDraw(false);
            this.currentType = i;
            this.radialProgress = new RadialProgress(this);
            this.radialProgress.setAlphaForPrevious(true);
            this.radialProgress.setProgressColor(-1);
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            this.channelCell = new BlockChannelCell(context, 1);
            addView(this.channelCell, LayoutHelper.createFrame(-1, -2.0f));
        }

        public void setBlock(TL_pageBlockVideo tL_pageBlockVideo, boolean z, boolean z2) {
            this.currentBlock = tL_pageBlockVideo;
            this.parentBlock = null;
            this.cancelLoading = false;
            this.currentDocument = ArticleViewer.this.getDocumentWithId(this.currentBlock.video_id);
            this.isGif = MessageObject.isGifDocument(this.currentDocument);
            this.lastCreatedWidth = 0;
            this.isFirst = z;
            this.isLast = z2;
            this.channelCell.setVisibility(true);
            updateButtonState(false);
            requestLayout();
        }

        public void setParentBlock(PageBlock pageBlock) {
            this.parentBlock = pageBlock;
            if (ArticleViewer.this.channelBlock != null && (this.parentBlock instanceof TL_pageBlockCover) != null) {
                this.channelCell.setBlock(ArticleViewer.this.channelBlock);
                this.channelCell.setVisibility(0);
            }
        }

        public View getChannelCell() {
            return this.channelCell;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            boolean z = false;
            if (this.channelCell.getVisibility() != 0 || y <= this.channelCell.getTranslationY() || y >= this.channelCell.getTranslationY() + ((float) AndroidUtilities.dp(39.0f))) {
                if (motionEvent.getAction() == 0 && this.imageView.isInsideImage(x, y)) {
                    if ((this.buttonState == -1 || x < ((float) this.buttonX) || x > ((float) (this.buttonX + AndroidUtilities.dp(48.0f))) || y < ((float) this.buttonY) || y > ((float) (this.buttonY + AndroidUtilities.dp(48.0f)))) && this.buttonState != 0) {
                        this.photoPressed = true;
                    } else {
                        this.buttonPressed = 1;
                        invalidate();
                    }
                } else if (motionEvent.getAction() == 1) {
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
                } else if (motionEvent.getAction() == 3) {
                    this.photoPressed = false;
                }
                if (this.photoPressed || this.buttonPressed != 0 || ArticleViewer.this.checkLayoutForLinks(motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent) != null) {
                    z = true;
                }
                return z;
            }
            if (ArticleViewer.this.channelBlock != null && motionEvent.getAction() == 1) {
                MessagesController.getInstance(ArticleViewer.this.currentAccount).openByUserName(ArticleViewer.this.channelBlock.channel.username, ArticleViewer.this.parentFragment, 2);
                ArticleViewer.this.close(false, true);
            }
            return true;
        }

        protected void onMeasure(int i, int i2) {
            int measuredHeight;
            int size = MeasureSpec.getSize(i);
            int i3 = 0;
            int i4 = 1;
            if (this.currentType == 1) {
                size = ArticleViewer.this.listView.getWidth();
                measuredHeight = ((View) getParent()).getMeasuredHeight();
            } else {
                measuredHeight = r0.currentType == 2 ? size : 0;
            }
            if (r0.currentBlock != null) {
                int i5;
                int dp;
                int i6;
                if (r0.currentType != 0 || r0.currentBlock.level <= 0) {
                    r0.textX = AndroidUtilities.dp(18.0f);
                    i5 = 0;
                    dp = size - AndroidUtilities.dp(36.0f);
                    i6 = size;
                } else {
                    i5 = AndroidUtilities.dp((float) (14 * r0.currentBlock.level)) + AndroidUtilities.dp(18.0f);
                    r0.textX = i5;
                    i6 = size - (AndroidUtilities.dp(18.0f) + i5);
                    dp = i6;
                }
                if (r0.currentDocument != null) {
                    int dp2;
                    PhotoSize photoSize = r0.currentDocument.thumb;
                    if (r0.currentType == 0) {
                        measuredHeight = (int) ((((float) i6) / ((float) photoSize.f43w)) * ((float) photoSize.f42h));
                        if (r0.parentBlock instanceof TL_pageBlockCover) {
                            measuredHeight = Math.min(measuredHeight, i6);
                        } else {
                            int max = (int) (((float) (Math.max(ArticleViewer.this.listView.getMeasuredWidth(), ArticleViewer.this.listView.getMeasuredHeight()) - AndroidUtilities.dp(56.0f))) * 0.9f);
                            if (measuredHeight > max) {
                                i6 = (int) ((((float) max) / ((float) photoSize.f42h)) * ((float) photoSize.f43w));
                                i5 += ((size - i5) - i6) / 2;
                                measuredHeight = max;
                            }
                        }
                    }
                    ImageReceiver imageReceiver = r0.imageView;
                    if (!(r0.isFirst || r0.currentType == 1 || r0.currentType == 2)) {
                        if (r0.currentBlock.level <= 0) {
                            dp2 = AndroidUtilities.dp(8.0f);
                            imageReceiver.setImageCoords(i5, dp2, i6, measuredHeight);
                            if (r0.isGif) {
                                r0.imageView.setImage(null, null, photoSize == null ? photoSize.location : null, photoSize == null ? "80_80_b" : null, 0, null, 1);
                            } else {
                                r0.imageView.setImage(r0.currentDocument, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(i6), Integer.valueOf(measuredHeight)}), photoSize == null ? photoSize.location : null, photoSize == null ? "80_80_b" : null, r0.currentDocument.size, null, 1);
                            }
                            i5 = AndroidUtilities.dp(48.0f);
                            r0.buttonX = (int) (((float) r0.imageView.getImageX()) + (((float) (r0.imageView.getImageWidth() - i5)) / 2.0f));
                            r0.buttonY = (int) (((float) r0.imageView.getImageY()) + (((float) (r0.imageView.getImageHeight() - i5)) / 2.0f));
                            r0.radialProgress.setProgressRect(r0.buttonX, r0.buttonY, r0.buttonX + i5, r0.buttonY + i5);
                        }
                    }
                    dp2 = 0;
                    imageReceiver.setImageCoords(i5, dp2, i6, measuredHeight);
                    if (r0.isGif) {
                        if (photoSize == null) {
                        }
                        if (photoSize == null) {
                        }
                        r0.imageView.setImage(null, null, photoSize == null ? photoSize.location : null, photoSize == null ? "80_80_b" : null, 0, null, 1);
                    } else {
                        if (photoSize == null) {
                        }
                        if (photoSize == null) {
                        }
                        r0.imageView.setImage(r0.currentDocument, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(i6), Integer.valueOf(measuredHeight)}), photoSize == null ? photoSize.location : null, photoSize == null ? "80_80_b" : null, r0.currentDocument.size, null, 1);
                    }
                    i5 = AndroidUtilities.dp(48.0f);
                    r0.buttonX = (int) (((float) r0.imageView.getImageX()) + (((float) (r0.imageView.getImageWidth() - i5)) / 2.0f));
                    r0.buttonY = (int) (((float) r0.imageView.getImageY()) + (((float) (r0.imageView.getImageHeight() - i5)) / 2.0f));
                    r0.radialProgress.setProgressRect(r0.buttonX, r0.buttonY, r0.buttonX + i5, r0.buttonY + i5);
                }
                if (r0.currentType == 0 && r0.lastCreatedWidth != size) {
                    r0.textLayout = ArticleViewer.this.createLayoutForText(null, r0.currentBlock.caption, dp, r0.currentBlock);
                    if (r0.textLayout != null) {
                        measuredHeight += AndroidUtilities.dp(8.0f) + r0.textLayout.getHeight();
                    }
                }
                if (!r0.isFirst && r0.currentType == 0 && r0.currentBlock.level <= 0) {
                    measuredHeight += AndroidUtilities.dp(8.0f);
                }
                if ((r0.parentBlock instanceof TL_pageBlockCover) && ArticleViewer.this.blocks != null && ArticleViewer.this.blocks.size() > 1 && (ArticleViewer.this.blocks.get(1) instanceof TL_pageBlockChannel)) {
                    i3 = 1;
                }
                if (r0.currentType != 2 && r4 == 0) {
                    measuredHeight += AndroidUtilities.dp(8.0f);
                }
                i4 = measuredHeight;
            }
            r0.channelCell.measure(i, i2);
            r0.channelCell.setTranslationY((float) (r0.imageView.getImageHeight() - AndroidUtilities.dp(39.0f)));
            setMeasuredDimension(size, i4);
        }

        protected void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                this.imageView.draw(canvas);
                if (this.imageView.getVisible()) {
                    this.radialProgress.draw(canvas);
                }
                if (this.textLayout != null) {
                    canvas.save();
                    float f = (float) this.textX;
                    int imageY = (this.imageView.getImageY() + this.imageView.getImageHeight()) + AndroidUtilities.dp(8.0f);
                    this.textY = imageY;
                    canvas.translate(f, (float) imageY);
                    ArticleViewer.this.drawLayoutLink(canvas, this.textLayout);
                    this.textLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        private Drawable getDrawableForCurrentState() {
            return (this.buttonState < 0 || this.buttonState >= 4) ? null : Theme.chat_photoStatesDrawables[this.buttonState][this.buttonPressed];
        }

        public void updateButtonState(boolean z) {
            String attachFileName = FileLoader.getAttachFileName(this.currentDocument);
            boolean z2 = true;
            boolean exists = FileLoader.getPathToAttach(this.currentDocument, true).exists();
            if (TextUtils.isEmpty(attachFileName)) {
                this.radialProgress.setBackground(null, false, false);
                return;
            }
            if (exists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                if (this.isGif) {
                    this.buttonState = -1;
                } else {
                    this.buttonState = 3;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
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
                } else if (this.cancelLoading || !this.isGif) {
                    this.buttonState = 0;
                    z2 = false;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), z2, z);
                this.radialProgress.setProgress(f, false);
                invalidate();
            }
        }

        private void didPressedButton(boolean z) {
            if (this.buttonState == 0) {
                this.cancelLoading = false;
                this.radialProgress.setProgress(0.0f, false);
                if (this.isGif) {
                    this.imageView.setImage(this.currentDocument, null, this.currentDocument.thumb != null ? this.currentDocument.thumb.location : null, "80_80_b", this.currentDocument.size, null, 1);
                } else {
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).loadFile(this.currentDocument, true, 1);
                }
                this.buttonState = 1;
                this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
                invalidate();
            } else if (this.buttonState == 1) {
                this.cancelLoading = true;
                if (this.isGif) {
                    this.imageView.cancelLoadImage();
                } else {
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).cancelLoadFile(this.currentDocument);
                }
                this.buttonState = 0;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                invalidate();
            } else if (this.buttonState == 2) {
                this.imageView.setAllowStartAnimation(true);
                this.imageView.startAnimation();
                this.buttonState = -1;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
            } else if (this.buttonState) {
                ArticleViewer.this.openPhoto(this.currentBlock);
            }
        }

        public void onFailedDownload(String str) {
            updateButtonState(null);
        }

        public void onSuccessDownload(String str) {
            this.radialProgress.setProgress(1.0f, true);
            if (this.isGif != null) {
                this.buttonState = 2;
                didPressedButton(true);
                return;
            }
            updateButtonState(true);
        }

        public void onProgressDownload(String str, float f) {
            this.radialProgress.setProgress(f, true);
            if (this.buttonState != 1) {
                updateButtonState(null);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }
    }

    private class WebpageAdapter extends SelectionAdapter {
        private Context context;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public WebpageAdapter(Context context) {
            this.context = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new BlockParagraphCell(this.context);
                    break;
                case 1:
                    viewGroup = new BlockHeaderCell(this.context);
                    break;
                case 2:
                    viewGroup = new BlockDividerCell(this.context);
                    break;
                case 3:
                    viewGroup = new BlockEmbedCell(this.context);
                    break;
                case 4:
                    viewGroup = new BlockSubtitleCell(this.context);
                    break;
                case 5:
                    i = new BlockVideoCell(this.context, 0);
                    break;
                case 6:
                    viewGroup = new BlockPullquoteCell(this.context);
                    break;
                case 7:
                    viewGroup = new BlockBlockquoteCell(this.context);
                    break;
                case 8:
                    viewGroup = new BlockSlideshowCell(this.context);
                    break;
                case 9:
                    i = new BlockPhotoCell(this.context, 0);
                    break;
                case 10:
                    viewGroup = new BlockAuthorDateCell(this.context);
                    break;
                case 11:
                    viewGroup = new BlockTitleCell(this.context);
                    break;
                case 12:
                    viewGroup = new BlockListCell(this.context);
                    break;
                case 13:
                    viewGroup = new BlockFooterCell(this.context);
                    break;
                case 14:
                    viewGroup = new BlockPreformattedCell(this.context);
                    break;
                case 15:
                    viewGroup = new BlockSubheaderCell(this.context);
                    break;
                case 16:
                    viewGroup = new BlockEmbedPostCell(this.context);
                    break;
                case 17:
                    viewGroup = new BlockCollageCell(this.context);
                    break;
                case 18:
                    i = new BlockChannelCell(this.context, 0);
                    break;
                case 19:
                    viewGroup = new BlockAudioCell(this.context);
                    break;
                default:
                    viewGroup = new FrameLayout(this.context) {
                        protected void onMeasure(int i, int i2) {
                            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM));
                        }
                    };
                    viewGroup.setTag(Integer.valueOf(90));
                    i = new TextView(this.context);
                    viewGroup.addView(i, LayoutHelper.createFrame(-1, 34.0f, 51, 0.0f, 10.0f, 0.0f, 0.0f));
                    i.setText(LocaleController.getString("PreviewFeedback", C0446R.string.PreviewFeedback));
                    i.setTextSize(1, 12.0f);
                    i.setGravity(17);
                    break;
            }
            viewGroup = i;
            viewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            if (i < ArticleViewer.this.blocks.size()) {
                PageBlock pageBlock = (PageBlock) ArticleViewer.this.blocks.get(i);
                PageBlock pageBlock2 = pageBlock instanceof TL_pageBlockCover ? pageBlock.cover : pageBlock;
                boolean z2;
                switch (viewHolder.getItemViewType()) {
                    case 0:
                        ((BlockParagraphCell) viewHolder.itemView).setBlock((TL_pageBlockParagraph) pageBlock2);
                        return;
                    case 1:
                        ((BlockHeaderCell) viewHolder.itemView).setBlock((TL_pageBlockHeader) pageBlock2);
                        return;
                    case 2:
                        BlockDividerCell blockDividerCell = (BlockDividerCell) viewHolder.itemView;
                        return;
                    case 3:
                        ((BlockEmbedCell) viewHolder.itemView).setBlock((TL_pageBlockEmbed) pageBlock2);
                        return;
                    case 4:
                        ((BlockSubtitleCell) viewHolder.itemView).setBlock((TL_pageBlockSubtitle) pageBlock2);
                        return;
                    case 5:
                        BlockVideoCell blockVideoCell = (BlockVideoCell) viewHolder.itemView;
                        TL_pageBlockVideo tL_pageBlockVideo = (TL_pageBlockVideo) pageBlock2;
                        z2 = i == 0;
                        if (i == ArticleViewer.this.blocks.size() - 1) {
                            z = true;
                        }
                        blockVideoCell.setBlock(tL_pageBlockVideo, z2, z);
                        blockVideoCell.setParentBlock(pageBlock);
                        return;
                    case 6:
                        ((BlockPullquoteCell) viewHolder.itemView).setBlock((TL_pageBlockPullquote) pageBlock2);
                        return;
                    case 7:
                        ((BlockBlockquoteCell) viewHolder.itemView).setBlock((TL_pageBlockBlockquote) pageBlock2);
                        return;
                    case 8:
                        ((BlockSlideshowCell) viewHolder.itemView).setBlock((TL_pageBlockSlideshow) pageBlock2);
                        return;
                    case 9:
                        BlockPhotoCell blockPhotoCell = (BlockPhotoCell) viewHolder.itemView;
                        TL_pageBlockPhoto tL_pageBlockPhoto = (TL_pageBlockPhoto) pageBlock2;
                        z2 = i == 0;
                        if (i == ArticleViewer.this.blocks.size() - 1) {
                            z = true;
                        }
                        blockPhotoCell.setBlock(tL_pageBlockPhoto, z2, z);
                        blockPhotoCell.setParentBlock(pageBlock);
                        return;
                    case 10:
                        ((BlockAuthorDateCell) viewHolder.itemView).setBlock((TL_pageBlockAuthorDate) pageBlock2);
                        return;
                    case 11:
                        ((BlockTitleCell) viewHolder.itemView).setBlock((TL_pageBlockTitle) pageBlock2);
                        return;
                    case 12:
                        ((BlockListCell) viewHolder.itemView).setBlock((TL_pageBlockList) pageBlock2);
                        return;
                    case 13:
                        ((BlockFooterCell) viewHolder.itemView).setBlock((TL_pageBlockFooter) pageBlock2);
                        return;
                    case 14:
                        ((BlockPreformattedCell) viewHolder.itemView).setBlock((TL_pageBlockPreformatted) pageBlock2);
                        return;
                    case 15:
                        ((BlockSubheaderCell) viewHolder.itemView).setBlock((TL_pageBlockSubheader) pageBlock2);
                        return;
                    case 16:
                        ((BlockEmbedPostCell) viewHolder.itemView).setBlock((TL_pageBlockEmbedPost) pageBlock2);
                        return;
                    case 17:
                        ((BlockCollageCell) viewHolder.itemView).setBlock((TL_pageBlockCollage) pageBlock2);
                        return;
                    case 18:
                        ((BlockChannelCell) viewHolder.itemView).setBlock((TL_pageBlockChannel) pageBlock2);
                        return;
                    case 19:
                        BlockAudioCell blockAudioCell = (BlockAudioCell) viewHolder.itemView;
                        TL_pageBlockAudio tL_pageBlockAudio = (TL_pageBlockAudio) pageBlock2;
                        boolean z3 = i == 0;
                        if (i == ArticleViewer.this.blocks.size() - 1) {
                            z = true;
                        }
                        blockAudioCell.setBlock(tL_pageBlockAudio, z3, z);
                        return;
                    default:
                        return;
                }
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
                    textView.setBackgroundColor(-14277082);
                }
            }
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
            if (pageBlock instanceof TL_pageBlockList) {
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
            if (pageBlock instanceof TL_pageBlockCover) {
                return getTypeForBlock(pageBlock.cover);
            }
            return 0;
        }

        public int getItemViewType(int i) {
            if (i == ArticleViewer.this.blocks.size()) {
                return 90;
            }
            return getTypeForBlock((PageBlock) ArticleViewer.this.blocks.get(i));
        }

        public int getItemCount() {
            return (ArticleViewer.this.currentPage == null || ArticleViewer.this.currentPage.cached_page == null) ? 0 : ArticleViewer.this.blocks.size() + 1;
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

    static /* synthetic */ int access$804(ArticleViewer articleViewer) {
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

    private void showPopup(View view, int i, int i2, int i3) {
        if (this.popupWindow == null || !this.popupWindow.isShowing()) {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                this.popupLayout = new ActionBarPopupWindowLayout(this.parentActivity);
                this.popupLayout.setBackgroundDrawable(this.parentActivity.getResources().getDrawable(C0446R.drawable.menu_copy));
                this.popupLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new C08121());
                this.popupLayout.setDispatchKeyEventListener(new C19102());
                this.popupLayout.setShowedFromBotton(false);
                View textView = new TextView(this.parentActivity);
                textView.setTextColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
                textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                textView.setGravity(16);
                textView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
                textView.setTextSize(1, 15.0f);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setText(LocaleController.getString("Copy", C0446R.string.Copy).toUpperCase());
                textView.setOnClickListener(new C08203());
                this.popupLayout.addView(textView, LayoutHelper.createFrame(-2, 38.0f));
                this.popupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                this.popupWindow.setAnimationEnabled(false);
                this.popupWindow.setAnimationStyle(C0446R.style.PopupAnimation);
                this.popupWindow.setOutsideTouchable(true);
                this.popupWindow.setClippingEnabled(true);
                this.popupWindow.setInputMethodMode(2);
                this.popupWindow.setSoftInputMode(0);
                this.popupWindow.getContentView().setFocusableInTouchMode(true);
                this.popupWindow.setOnDismissListener(new C08234());
            }
            this.popupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.popupWindow.setFocusable(true);
            this.popupWindow.showAtLocation(view, i, i2, i3);
            this.popupWindow.startAnimation();
            return;
        }
        this.popupWindow.dismiss();
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
                setRichTextParents(richText, ((TL_textStrike) richText2).text);
            } else if ((richText2 instanceof TL_textEmail) != null) {
                setRichTextParents(richText2, ((TL_textEmail) richText2).text);
            } else if ((richText2 instanceof TL_textUrl) != null) {
                setRichTextParents(richText2, ((TL_textUrl) richText2).text);
            } else if ((richText2 instanceof TL_textConcat) != null) {
                richText = richText2.texts.size();
                for (int i = 0; i < richText; i++) {
                    setRichTextParents(richText2, (RichText) richText2.texts.get(i));
                }
            }
        }
    }

    private void updateInterfaceForCurrentPage(boolean z) {
        if (this.currentPage != null) {
            if (this.currentPage.cached_page != null) {
                boolean z2;
                this.isRtl = -1;
                this.channelBlock = null;
                this.blocks.clear();
                this.photoBlocks.clear();
                this.audioBlocks.clear();
                this.audioMessages.clear();
                int size = this.currentPage.cached_page.blocks.size();
                int i = 0;
                while (true) {
                    z2 = true;
                    if (i >= size) {
                        break;
                    }
                    PageBlock pageBlock = (PageBlock) this.currentPage.cached_page.blocks.get(i);
                    if (!(pageBlock instanceof TL_pageBlockUnsupported)) {
                        if (pageBlock instanceof TL_pageBlockAnchor) {
                            this.anchors.put(pageBlock.name.toLowerCase(), Integer.valueOf(this.blocks.size()));
                        } else {
                            int i2;
                            if (pageBlock instanceof TL_pageBlockAudio) {
                                Message tL_message = new TL_message();
                                tL_message.out = true;
                                i2 = ((int) this.currentPage.id) + i;
                                pageBlock.mid = i2;
                                tL_message.id = i2;
                                tL_message.to_id = new TL_peerUser();
                                Peer peer = tL_message.to_id;
                                int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                                tL_message.from_id = clientUserId;
                                peer.user_id = clientUserId;
                                tL_message.date = (int) (System.currentTimeMillis() / 1000);
                                tL_message.message = TtmlNode.ANONYMOUS_REGION_ID;
                                tL_message.media = new TL_messageMediaDocument();
                                MessageMedia messageMedia = tL_message.media;
                                messageMedia.flags |= 3;
                                tL_message.media.document = getDocumentWithId(pageBlock.audio_id);
                                tL_message.flags |= 768;
                                MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, tL_message, false);
                                this.audioMessages.add(messageObject);
                                this.audioBlocks.put((TL_pageBlockAudio) pageBlock, messageObject);
                            }
                            setRichTextParents(null, pageBlock.text);
                            setRichTextParents(null, pageBlock.caption);
                            if (pageBlock instanceof TL_pageBlockAuthorDate) {
                                setRichTextParents(null, ((TL_pageBlockAuthorDate) pageBlock).author);
                            } else if (pageBlock instanceof TL_pageBlockCollage) {
                                TL_pageBlockCollage tL_pageBlockCollage = (TL_pageBlockCollage) pageBlock;
                                for (i2 = 0; i2 < tL_pageBlockCollage.items.size(); i2++) {
                                    setRichTextParents(null, ((PageBlock) tL_pageBlockCollage.items.get(i2)).text);
                                    setRichTextParents(null, ((PageBlock) tL_pageBlockCollage.items.get(i2)).caption);
                                }
                            } else if (pageBlock instanceof TL_pageBlockList) {
                                TL_pageBlockList tL_pageBlockList = (TL_pageBlockList) pageBlock;
                                for (i2 = 0; i2 < tL_pageBlockList.items.size(); i2++) {
                                    setRichTextParents(null, (RichText) tL_pageBlockList.items.get(i2));
                                }
                            } else if (pageBlock instanceof TL_pageBlockSlideshow) {
                                TL_pageBlockSlideshow tL_pageBlockSlideshow = (TL_pageBlockSlideshow) pageBlock;
                                for (i2 = 0; i2 < tL_pageBlockSlideshow.items.size(); i2++) {
                                    setRichTextParents(null, ((PageBlock) tL_pageBlockSlideshow.items.get(i2)).text);
                                    setRichTextParents(null, ((PageBlock) tL_pageBlockSlideshow.items.get(i2)).caption);
                                }
                            }
                            if (i == 0) {
                                pageBlock.first = true;
                                if ((pageBlock instanceof TL_pageBlockCover) && pageBlock.cover.caption != null && !(pageBlock.cover.caption instanceof TL_textEmpty) && size > 1) {
                                    PageBlock pageBlock2 = (PageBlock) this.currentPage.cached_page.blocks.get(1);
                                    if (pageBlock2 instanceof TL_pageBlockChannel) {
                                        this.channelBlock = (TL_pageBlockChannel) pageBlock2;
                                    }
                                }
                            } else if (i == 1 && this.channelBlock != null) {
                            }
                            addAllMediaFromBlock(pageBlock);
                            this.blocks.add(pageBlock);
                            if (pageBlock instanceof TL_pageBlockEmbedPost) {
                                if (!pageBlock.blocks.isEmpty()) {
                                    pageBlock.level = -1;
                                    for (int i3 = 0; i3 < pageBlock.blocks.size(); i3++) {
                                        PageBlock pageBlock3 = (PageBlock) pageBlock.blocks.get(i3);
                                        if (!(pageBlock3 instanceof TL_pageBlockUnsupported)) {
                                            if (pageBlock3 instanceof TL_pageBlockAnchor) {
                                                this.anchors.put(pageBlock3.name.toLowerCase(), Integer.valueOf(this.blocks.size()));
                                            } else {
                                                pageBlock3.level = 1;
                                                if (i3 == pageBlock.blocks.size() - 1) {
                                                    pageBlock3.bottom = true;
                                                }
                                                this.blocks.add(pageBlock3);
                                                addAllMediaFromBlock(pageBlock3);
                                            }
                                        }
                                    }
                                }
                                if (!(pageBlock.caption instanceof TL_textEmpty)) {
                                    TL_pageBlockParagraph tL_pageBlockParagraph = new TL_pageBlockParagraph();
                                    tL_pageBlockParagraph.caption = pageBlock.caption;
                                    this.blocks.add(tL_pageBlockParagraph);
                                }
                            }
                        }
                    }
                    i++;
                }
                this.adapter.notifyDataSetChanged();
                if (this.pagesStack.size() != 1) {
                    if (!z) {
                        this.layoutManager.scrollToPositionWithOffset(0, 0);
                    }
                }
                z = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("article");
                stringBuilder.append(this.currentPage.id);
                String stringBuilder2 = stringBuilder.toString();
                size = z.getInt(stringBuilder2, -1);
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(stringBuilder2);
                stringBuilder3.append("r");
                boolean z3 = z.getBoolean(stringBuilder3.toString(), true);
                if (AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
                    z2 = false;
                }
                if (z3 == z2) {
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(stringBuilder2);
                    stringBuilder3.append("o");
                    z = z.getInt(stringBuilder3.toString(), 0) - this.listView.getPaddingTop();
                } else {
                    z = AndroidUtilities.dp(true);
                }
                if (size != -1) {
                    this.layoutManager.scrollToPositionWithOffset(size, z);
                }
            }
        }
    }

    private void addPageToStack(WebPage webPage, String str) {
        saveCurrentPagePosition();
        this.currentPage = webPage;
        this.pagesStack.add(webPage);
        updateInterfaceForCurrentPage(false);
        if (str != null) {
            Integer num = (Integer) this.anchors.get(str.toLowerCase());
            if (num != null) {
                this.layoutManager.scrollToPositionWithOffset(num.intValue(), 0);
            }
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
                this.pendingCheckForTap = new CheckForTap();
            }
            this.windowView.postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    protected void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        if (this.pendingCheckForLongPress != null) {
            this.windowView.removeCallbacks(this.pendingCheckForLongPress);
        }
        if (this.pendingCheckForTap != null) {
            this.windowView.removeCallbacks(this.pendingCheckForTap);
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
        if (richText instanceof TL_textUrl) {
            return getTextFlags(richText.parentRichText) | 8;
        }
        return richText != null ? getTextFlags(richText.parentRichText) : null;
    }

    private CharSequence getText(RichText richText, RichText richText2, PageBlock pageBlock) {
        if (richText2 instanceof TL_textFixed) {
            return getText(richText, ((TL_textFixed) richText2).text, pageBlock);
        }
        if (richText2 instanceof TL_textItalic) {
            return getText(richText, ((TL_textItalic) richText2).text, pageBlock);
        }
        if (richText2 instanceof TL_textBold) {
            return getText(richText, ((TL_textBold) richText2).text, pageBlock);
        }
        if (richText2 instanceof TL_textUnderline) {
            return getText(richText, ((TL_textUnderline) richText2).text, pageBlock);
        }
        if (richText2 instanceof TL_textStrike) {
            return getText(richText, ((TL_textStrike) richText2).text, pageBlock);
        }
        TextPaint textPaint = null;
        int i = 0;
        CharSequence spannableStringBuilder;
        MetricAffectingSpan[] metricAffectingSpanArr;
        if (richText2 instanceof TL_textEmail) {
            spannableStringBuilder = new SpannableStringBuilder(getText(richText, ((TL_textEmail) richText2).text, pageBlock));
            metricAffectingSpanArr = (MetricAffectingSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), MetricAffectingSpan.class);
            if (metricAffectingSpanArr == null || metricAffectingSpanArr.length == 0) {
                textPaint = getTextPaint(richText, richText2, pageBlock);
            }
            spannableStringBuilder.setSpan(new TextPaintUrlSpan(textPaint, getUrl(richText2)), 0, spannableStringBuilder.length(), 33);
            return spannableStringBuilder;
        } else if (richText2 instanceof TL_textUrl) {
            spannableStringBuilder = new SpannableStringBuilder(getText(richText, ((TL_textUrl) richText2).text, pageBlock));
            metricAffectingSpanArr = (MetricAffectingSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), MetricAffectingSpan.class);
            if (metricAffectingSpanArr == null || metricAffectingSpanArr.length == 0) {
                textPaint = getTextPaint(richText, richText2, pageBlock);
            }
            spannableStringBuilder.setSpan(new TextPaintUrlSpan(textPaint, getUrl(richText2)), 0, spannableStringBuilder.length(), 33);
            return spannableStringBuilder;
        } else if (richText2 instanceof TL_textPlain) {
            return ((TL_textPlain) richText2).text;
        } else {
            if (richText2 instanceof TL_textEmpty) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            if (richText2 instanceof TL_textConcat) {
                spannableStringBuilder = new SpannableStringBuilder();
                int size = richText2.texts.size();
                while (i < size) {
                    RichText richText3 = (RichText) richText2.texts.get(i);
                    CharSequence text = getText(richText, richText3, pageBlock);
                    int textFlags = getTextFlags(richText3);
                    int length = spannableStringBuilder.length();
                    spannableStringBuilder.append(text);
                    if (!(textFlags == 0 || (text instanceof SpannableStringBuilder))) {
                        if ((textFlags & 8) != 0) {
                            String url = getUrl(richText3);
                            if (url == null) {
                                url = getUrl(richText);
                            }
                            spannableStringBuilder.setSpan(new TextPaintUrlSpan(getTextPaint(richText, richText3, pageBlock), url), length, spannableStringBuilder.length(), 33);
                        } else {
                            spannableStringBuilder.setSpan(new TextPaintSpan(getTextPaint(richText, richText3, pageBlock)), length, spannableStringBuilder.length(), 33);
                        }
                    }
                    i++;
                }
                return spannableStringBuilder;
            }
            richText = new StringBuilder();
            richText.append("not supported ");
            richText.append(richText2);
            return richText.toString();
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
        return richText instanceof TL_textUrl ? ((TL_textUrl) richText).url : null;
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

    private TextPaint getTextPaint(RichText richText, RichText richText2, PageBlock pageBlock) {
        int textFlags = getTextFlags(richText2);
        int dp = AndroidUtilities.dp(14.0f);
        int dp2 = this.selectedFontSize == 0 ? -AndroidUtilities.dp(4.0f) : this.selectedFontSize == 1 ? -AndroidUtilities.dp(2.0f) : this.selectedFontSize == 3 ? AndroidUtilities.dp(2.0f) : this.selectedFontSize == 4 ? AndroidUtilities.dp(4.0f) : 0;
        if (pageBlock instanceof TL_pageBlockPhoto) {
            richText = captionTextPaints;
            dp = AndroidUtilities.dp(14.0f);
            richText2 = getGrayTextColor();
        } else if (pageBlock instanceof TL_pageBlockTitle) {
            richText = titleTextPaints;
            dp = AndroidUtilities.dp(24.0f);
            richText2 = getTextColor();
        } else if (pageBlock instanceof TL_pageBlockAuthorDate) {
            richText = authorTextPaints;
            dp = AndroidUtilities.dp(14.0f);
            richText2 = getGrayTextColor();
        } else if (pageBlock instanceof TL_pageBlockFooter) {
            richText = footerTextPaints;
            dp = AndroidUtilities.dp(14.0f);
            richText2 = getGrayTextColor();
        } else if (pageBlock instanceof TL_pageBlockSubtitle) {
            richText = subtitleTextPaints;
            dp = AndroidUtilities.dp(21.0f);
            richText2 = getTextColor();
        } else if (pageBlock instanceof TL_pageBlockHeader) {
            richText = headerTextPaints;
            dp = AndroidUtilities.dp(21.0f);
            richText2 = getTextColor();
        } else if (pageBlock instanceof TL_pageBlockSubheader) {
            richText = subheaderTextPaints;
            dp = AndroidUtilities.dp(18.0f);
            richText2 = getTextColor();
        } else {
            if (!(pageBlock instanceof TL_pageBlockBlockquote)) {
                if (!(pageBlock instanceof TL_pageBlockPullquote)) {
                    if (pageBlock instanceof TL_pageBlockPreformatted) {
                        richText = preformattedTextPaints;
                        dp = AndroidUtilities.dp(14.0f);
                        richText2 = getTextColor();
                    } else if (pageBlock instanceof TL_pageBlockParagraph) {
                        if (pageBlock.caption == richText) {
                            richText = embedPostCaptionTextPaints;
                            dp = AndroidUtilities.dp(14.0f);
                            richText2 = getGrayTextColor();
                        } else {
                            richText = paragraphTextPaints;
                            dp = AndroidUtilities.dp(16.0f);
                            richText2 = getTextColor();
                        }
                    } else if ((pageBlock instanceof TL_pageBlockList) != null) {
                        richText = listTextPaints;
                        dp = AndroidUtilities.dp(15.0f);
                        richText2 = getTextColor();
                    } else if ((pageBlock instanceof TL_pageBlockEmbed) != null) {
                        richText = embedTextPaints;
                        dp = AndroidUtilities.dp(14.0f);
                        richText2 = getGrayTextColor();
                    } else if ((pageBlock instanceof TL_pageBlockSlideshow) != null) {
                        richText = slideshowTextPaints;
                        dp = AndroidUtilities.dp(14.0f);
                        richText2 = getGrayTextColor();
                    } else if ((pageBlock instanceof TL_pageBlockEmbedPost) != null) {
                        if (richText2 != null) {
                            richText = embedPostTextPaints;
                            dp = AndroidUtilities.dp(14.0f);
                            richText2 = getTextColor();
                        }
                        richText = null;
                        richText2 = -65536;
                    } else {
                        if (!((pageBlock instanceof TL_pageBlockVideo) == null && (pageBlock instanceof TL_pageBlockAudio) == null)) {
                            richText = videoTextPaints;
                            dp = AndroidUtilities.dp(14.0f);
                            richText2 = getTextColor();
                        }
                        richText = null;
                        richText2 = -65536;
                    }
                }
            }
            if (pageBlock.text == richText) {
                richText = quoteTextPaints;
                dp = AndroidUtilities.dp(15.0f);
                richText2 = getTextColor();
            } else {
                if (pageBlock.caption == richText) {
                    richText = subquoteTextPaints;
                    dp = AndroidUtilities.dp(14.0f);
                    richText2 = getGrayTextColor();
                }
                richText = null;
                richText2 = -65536;
            }
        }
        if (richText == null) {
            if (errorTextPaint == null) {
                errorTextPaint = new TextPaint(1);
                errorTextPaint.setColor(-65536);
            }
            errorTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            return errorTextPaint;
        }
        TextPaint textPaint = (TextPaint) richText.get(textFlags);
        if (textPaint == null) {
            textPaint = new TextPaint(1);
            if ((textFlags & 4) != 0) {
                textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
            } else {
                if (!(this.selectedFont == 1 || (pageBlock instanceof TL_pageBlockTitle) || (pageBlock instanceof TL_pageBlockHeader) || (pageBlock instanceof TL_pageBlockSubtitle))) {
                    if ((pageBlock instanceof TL_pageBlockSubheader) == null) {
                        pageBlock = textFlags & 1;
                        if (pageBlock != null && (textFlags & 2) != 0) {
                            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf"));
                        } else if (pageBlock != null) {
                            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        } else if ((textFlags & 2) != null) {
                            textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/ritalic.ttf"));
                        }
                    }
                }
                pageBlock = textFlags & 1;
                if (pageBlock != null && (textFlags & 2) != 0) {
                    textPaint.setTypeface(Typeface.create(C0542C.SERIF_NAME, 3));
                } else if (pageBlock != null) {
                    textPaint.setTypeface(Typeface.create(C0542C.SERIF_NAME, 1));
                } else if ((textFlags & 2) != null) {
                    textPaint.setTypeface(Typeface.create(C0542C.SERIF_NAME, 2));
                } else {
                    textPaint.setTypeface(Typeface.create(C0542C.SERIF_NAME, 0));
                }
            }
            if ((textFlags & 32) != null) {
                textPaint.setFlags(textPaint.getFlags() | 16);
            }
            if ((textFlags & 16) != null) {
                textPaint.setFlags(textPaint.getFlags() | 8);
            }
            if ((textFlags & 8) != null) {
                textPaint.setFlags(textPaint.getFlags() | 8);
                richText2 = getTextColor();
            }
            textPaint.setColor(richText2);
            richText.put(textFlags, textPaint);
        }
        textPaint.setTextSize((float) (dp + dp2));
        return textPaint;
    }

    private StaticLayout createLayoutForText(CharSequence charSequence, RichText richText, int i, PageBlock pageBlock) {
        ArticleViewer articleViewer = this;
        Object obj = charSequence;
        RichText richText2 = richText;
        PageBlock pageBlock2 = pageBlock;
        if (obj == null && (richText2 == null || (richText2 instanceof TL_textEmpty))) {
            return null;
        }
        CharSequence charSequence2;
        int selectedColor = getSelectedColor();
        if (quoteLinePaint == null) {
            quoteLinePaint = new Paint();
            quoteLinePaint.setColor(getTextColor());
            preformattedBackgroundPaint = new Paint();
            if (selectedColor == 0) {
                preformattedBackgroundPaint.setColor(-657156);
            } else if (selectedColor == 1) {
                preformattedBackgroundPaint.setColor(-1712440);
            } else if (selectedColor == 2) {
                preformattedBackgroundPaint.setColor(-14277082);
            }
            urlPaint = new Paint();
            if (selectedColor == 0) {
                urlPaint.setColor(-1315861);
            } else if (selectedColor == 1) {
                urlPaint.setColor(-1712440);
            } else if (selectedColor == 2) {
                urlPaint.setColor(-14277082);
            }
        }
        if (obj != null) {
            charSequence2 = obj;
        } else {
            charSequence2 = getText(richText2, richText2, pageBlock2);
        }
        if (TextUtils.isEmpty(charSequence2)) {
            return null;
        }
        TextPaint textPaint;
        if ((pageBlock2 instanceof TL_pageBlockEmbedPost) && richText2 == null) {
            if (pageBlock2.author == obj) {
                if (embedPostAuthorPaint == null) {
                    embedPostAuthorPaint = new TextPaint(1);
                    embedPostAuthorPaint.setColor(getTextColor());
                }
                embedPostAuthorPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
                textPaint = embedPostAuthorPaint;
            } else {
                if (embedPostDatePaint == null) {
                    embedPostDatePaint = new TextPaint(1);
                    if (selectedColor == 0) {
                        embedPostDatePaint.setColor(-7366752);
                    } else if (selectedColor == 1) {
                        embedPostDatePaint.setColor(-11711675);
                    } else if (selectedColor == 2) {
                        embedPostDatePaint.setColor(-10066330);
                    }
                }
                embedPostDatePaint.setTextSize((float) AndroidUtilities.dp(14.0f));
                textPaint = embedPostDatePaint;
            }
        } else if (pageBlock2 instanceof TL_pageBlockChannel) {
            if (channelNamePaint == null) {
                channelNamePaint = new TextPaint(1);
                channelNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            }
            if (articleViewer.channelBlock == null) {
                channelNamePaint.setColor(getTextColor());
            } else {
                channelNamePaint.setColor(-1);
            }
            channelNamePaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            textPaint = channelNamePaint;
        } else if (!(pageBlock2 instanceof TL_pageBlockList) || obj == null) {
            textPaint = getTextPaint(richText2, richText2, pageBlock2);
        } else {
            if (listTextPointerPaint == null) {
                listTextPointerPaint = new TextPaint(1);
                listTextPointerPaint.setColor(getTextColor());
            }
            int dp = articleViewer.selectedFontSize == 0 ? -AndroidUtilities.dp(4.0f) : articleViewer.selectedFontSize == 1 ? -AndroidUtilities.dp(2.0f) : articleViewer.selectedFontSize == 3 ? AndroidUtilities.dp(2.0f) : articleViewer.selectedFontSize == 4 ? AndroidUtilities.dp(4.0f) : 0;
            listTextPointerPaint.setTextSize((float) (AndroidUtilities.dp(15.0f) + dp));
            textPaint = listTextPointerPaint;
        }
        TextPaint textPaint2 = textPaint;
        if (!(pageBlock2 instanceof TL_pageBlockPullquote)) {
            if (richText2 == null || pageBlock2 == null || (pageBlock2 instanceof TL_pageBlockBlockquote) || richText2 != pageBlock2.caption) {
                return new StaticLayout(charSequence2, textPaint2, i, Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.dp(4.0f), false);
            }
        }
        return new StaticLayout(charSequence2, textPaint2, i, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
    }

    private void drawLayoutLink(Canvas canvas, StaticLayout staticLayout) {
        if (canvas != null) {
            if (this.pressedLinkOwnerLayout == staticLayout) {
                if (this.pressedLink != null) {
                    canvas.drawPath(this.urlPath, urlPaint);
                } else if (this.drawBlockSelection && staticLayout != null) {
                    float lineWidth;
                    float lineLeft;
                    if (staticLayout.getLineCount() == 1) {
                        lineWidth = staticLayout.getLineWidth(0);
                        lineLeft = staticLayout.getLineLeft(0);
                    } else {
                        lineWidth = (float) staticLayout.getWidth();
                        lineLeft = 0.0f;
                    }
                    canvas.drawRect(((float) (-AndroidUtilities.dp(2.0f))) + lineLeft, 0.0f, (lineLeft + lineWidth) + ((float) AndroidUtilities.dp(2.0f)), (float) staticLayout.getHeight(), urlPaint);
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkLayoutForLinks(MotionEvent motionEvent, View view, StaticLayout staticLayout, int i, int i2) {
        boolean z = false;
        if (view != null) {
            if (staticLayout != null) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() != 0) {
                    if (motionEvent.getAction() == 1) {
                        if (this.pressedLink != null) {
                            staticLayout = this.pressedLink.getUrl();
                            if (staticLayout != null) {
                                i = staticLayout.lastIndexOf(35);
                                if (i != -1) {
                                    i = staticLayout.substring(i + 1);
                                    if (staticLayout.toLowerCase().contains(this.currentPage.url.toLowerCase()) != null) {
                                        Integer num = (Integer) this.anchors.get(i);
                                        if (num != null) {
                                            this.layoutManager.scrollToPositionWithOffset(num.intValue(), 0);
                                            staticLayout = 1;
                                        }
                                    }
                                    staticLayout = null;
                                } else {
                                    staticLayout = null;
                                    i = 0;
                                }
                                if (staticLayout == null && this.openUrlReqId == null) {
                                    showProgressView(true);
                                    staticLayout = new TL_messages_getWebPage();
                                    staticLayout.url = this.pressedLink.getUrl();
                                    staticLayout.hash = 0;
                                    this.openUrlReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(staticLayout, new RequestDelegate() {
                                        public void run(final TLObject tLObject, TL_error tL_error) {
                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                public void run() {
                                                    if (ArticleViewer.this.openUrlReqId != 0) {
                                                        ArticleViewer.this.openUrlReqId = 0;
                                                        ArticleViewer.this.showProgressView(false);
                                                        if (ArticleViewer.this.isVisible) {
                                                            if ((tLObject instanceof TL_webPage) && (((TL_webPage) tLObject).cached_page instanceof TL_pageFull)) {
                                                                ArticleViewer.this.addPageToStack((TL_webPage) tLObject, i);
                                                            } else {
                                                                Browser.openUrl(ArticleViewer.this.parentActivity, staticLayout.url);
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    }
                    staticLayout = 1;
                    if (staticLayout != null) {
                        this.pressedLink = null;
                        this.pressedLinkOwnerLayout = null;
                        this.pressedLinkOwnerView = null;
                        view.invalidate();
                    }
                    if (motionEvent.getAction() == null) {
                        startCheckLongPress();
                    }
                    if (!(motionEvent.getAction() == null || motionEvent.getAction() == 2)) {
                        cancelCheckLongPress();
                    }
                    if (this.pressedLinkOwnerLayout != null) {
                        z = true;
                    }
                    return z;
                } else if (x >= i && x <= staticLayout.getWidth() + i && y >= i2 && y <= staticLayout.getHeight() + i2) {
                    this.pressedLinkOwnerLayout = staticLayout;
                    this.pressedLinkOwnerView = view;
                    this.pressedLayoutY = i2;
                    if (staticLayout.getText() instanceof Spannable) {
                        x -= i;
                        try {
                            i = staticLayout.getLineForVertical(y - i2);
                            i2 = (float) x;
                            x = staticLayout.getOffsetForHorizontal(i, i2);
                            float lineLeft = staticLayout.getLineLeft(i);
                            if (lineLeft <= i2 && lineLeft + staticLayout.getLineWidth(i) >= i2) {
                                Spannable spannable = (Spannable) staticLayout.getText();
                                TextPaintUrlSpan[] textPaintUrlSpanArr = (TextPaintUrlSpan[]) spannable.getSpans(x, x, TextPaintUrlSpan.class);
                                if (textPaintUrlSpanArr != null && textPaintUrlSpanArr.length > 0) {
                                    this.pressedLink = textPaintUrlSpanArr[0];
                                    x = spannable.getSpanStart(this.pressedLink);
                                    int spanEnd = spannable.getSpanEnd(this.pressedLink);
                                    y = x;
                                    for (x = 1; x < textPaintUrlSpanArr.length; x++) {
                                        TextPaintUrlSpan textPaintUrlSpan = textPaintUrlSpanArr[x];
                                        int spanStart = spannable.getSpanStart(textPaintUrlSpan);
                                        int spanEnd2 = spannable.getSpanEnd(textPaintUrlSpan);
                                        if (y > spanStart || spanEnd2 > spanEnd) {
                                            this.pressedLink = textPaintUrlSpan;
                                            y = spanStart;
                                            spanEnd = spanEnd2;
                                        }
                                    }
                                    try {
                                        this.urlPath.setCurrentLayout(staticLayout, y, 0);
                                        staticLayout.getSelectionPath(y, spanEnd, this.urlPath);
                                        view.invalidate();
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                }
                            }
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    }
                }
                staticLayout = null;
                if (staticLayout != null) {
                    this.pressedLink = null;
                    this.pressedLinkOwnerLayout = null;
                    this.pressedLinkOwnerView = null;
                    view.invalidate();
                }
                if (motionEvent.getAction() == null) {
                    startCheckLongPress();
                }
                cancelCheckLongPress();
                if (this.pressedLinkOwnerLayout != null) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }

    private Photo getPhotoWithId(long j) {
        if (this.currentPage != null) {
            if (this.currentPage.cached_page != null) {
                if (this.currentPage.photo != null && this.currentPage.photo.id == j) {
                    return this.currentPage.photo;
                }
                for (int i = 0; i < this.currentPage.cached_page.photos.size(); i++) {
                    Photo photo = (Photo) this.currentPage.cached_page.photos.get(i);
                    if (photo.id == j) {
                        return photo;
                    }
                }
                return null;
            }
        }
        return null;
    }

    private Document getDocumentWithId(long j) {
        if (this.currentPage != null) {
            if (this.currentPage.cached_page != null) {
                if (this.currentPage.document != null && this.currentPage.document.id == j) {
                    return this.currentPage.document;
                }
                for (int i = 0; i < this.currentPage.cached_page.documents.size(); i++) {
                    Document document = (Document) this.currentPage.cached_page.documents.get(i);
                    if (document.id == j) {
                        return document;
                    }
                }
                return null;
            }
        }
        return null;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        String str;
        if (i == NotificationCenter.FileDidFailedLoad) {
            str = (String) objArr[0];
            while (i3 < 3) {
                if (this.currentFileNames[i3] == 0 || this.currentFileNames[i3].equals(str) == 0) {
                    i3++;
                } else {
                    this.radialProgressViews[i3].setProgress(1.0f, true);
                    checkProgress(i3, true);
                    return;
                }
            }
        } else if (i == NotificationCenter.FileDidLoaded) {
            str = (String) objArr[0];
            i2 = 0;
            while (i2 < 3) {
                if (this.currentFileNames[i2] == null || this.currentFileNames[i2].equals(str) == null) {
                    i2++;
                } else {
                    this.radialProgressViews[i2].setProgress(1.0f, true);
                    checkProgress(i2, true);
                    if (i2 == 0 && isMediaVideo(this.currentIndex) != 0) {
                        onActionClick(false);
                        return;
                    }
                    return;
                }
            }
        } else if (i == NotificationCenter.FileLoadProgressChanged) {
            str = (String) objArr[0];
            while (i3 < 3) {
                if (!(this.currentFileNames[i3] == 0 || this.currentFileNames[i3].equals(str) == 0)) {
                    this.radialProgressViews[i3].setProgress(((Float) objArr[1]).floatValue(), true);
                }
                i3++;
            }
        } else if (i == NotificationCenter.emojiDidLoaded) {
            if (this.captionTextView != 0) {
                this.captionTextView.invalidate();
            }
        } else if (i == NotificationCenter.needSetDayNightTheme) {
            if (this.nightModeEnabled != 0 && this.selectedColor != 2 && this.adapter != 0) {
                updatePaintColors();
                this.adapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.messagePlayingDidStarted) {
            MessageObject messageObject = (MessageObject) objArr[0];
            if (this.listView != 0) {
                i = this.listView.getChildCount();
                for (i2 = 0; i2 < i; i2++) {
                    objArr = this.listView.getChildAt(i2);
                    if (objArr instanceof BlockAudioCell) {
                        ((BlockAudioCell) objArr).updateButtonState(false);
                    }
                }
            }
        } else {
            BlockAudioCell blockAudioCell;
            if (i != NotificationCenter.messagePlayingDidReset) {
                if (i != NotificationCenter.messagePlayingPlayStateChanged) {
                    if (i == NotificationCenter.messagePlayingProgressDidChanged) {
                        Integer num = (Integer) objArr[0];
                        if (this.listView != 0) {
                            i2 = this.listView.getChildCount();
                            while (i3 < i2) {
                                objArr = this.listView.getChildAt(i3);
                                if (objArr instanceof BlockAudioCell) {
                                    blockAudioCell = (BlockAudioCell) objArr;
                                    MessageObject messageObject2 = blockAudioCell.getMessageObject();
                                    if (messageObject2 != null && messageObject2.getId() == num.intValue()) {
                                        i = MediaController.getInstance().getPlayingMessageObject();
                                        if (i != 0) {
                                            messageObject2.audioProgress = i.audioProgress;
                                            messageObject2.audioProgressSec = i.audioProgressSec;
                                            messageObject2.audioPlayerDuration = i.audioPlayerDuration;
                                            blockAudioCell.updatePlayingMessageProgress();
                                            return;
                                        }
                                        return;
                                    }
                                }
                                i3++;
                            }
                            return;
                        }
                        return;
                    }
                    return;
                }
            }
            if (this.listView != 0) {
                i = this.listView.getChildCount();
                for (i2 = 0; i2 < i; i2++) {
                    objArr = this.listView.getChildAt(i2);
                    if (objArr instanceof BlockAudioCell) {
                        blockAudioCell = (BlockAudioCell) objArr;
                        if (blockAudioCell.getMessageObject() != null) {
                            blockAudioCell.updateButtonState(false);
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
        int i;
        int i2 = 0;
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_type", this.selectedFont).commit();
        Typeface typeface = this.selectedFont == 0 ? Typeface.DEFAULT : Typeface.SERIF;
        Typeface typeface2 = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/ritalic.ttf") : Typeface.create(C0542C.SERIF_NAME, 2);
        Typeface typeface3 = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmedium.ttf") : Typeface.create(C0542C.SERIF_NAME, 1);
        Typeface typeface4 = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf") : Typeface.create(C0542C.SERIF_NAME, 3);
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
        for (i = 0; i < videoTextPaints.size(); i++) {
            updateFontEntry(videoTextPaints.keyAt(i), (TextPaint) videoTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < captionTextPaints.size(); i++) {
            updateFontEntry(captionTextPaints.keyAt(i), (TextPaint) captionTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < authorTextPaints.size(); i++) {
            updateFontEntry(authorTextPaints.keyAt(i), (TextPaint) authorTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < footerTextPaints.size(); i++) {
            updateFontEntry(footerTextPaints.keyAt(i), (TextPaint) footerTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < subquoteTextPaints.size(); i++) {
            updateFontEntry(subquoteTextPaints.keyAt(i), (TextPaint) subquoteTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < embedPostCaptionTextPaints.size(); i++) {
            updateFontEntry(embedPostCaptionTextPaints.keyAt(i), (TextPaint) embedPostCaptionTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        for (i = 0; i < embedTextPaints.size(); i++) {
            updateFontEntry(embedTextPaints.keyAt(i), (TextPaint) embedTextPaints.valueAt(i), typeface, typeface4, typeface3, typeface2);
        }
        while (i2 < slideshowTextPaints.size()) {
            updateFontEntry(slideshowTextPaints.keyAt(i2), (TextPaint) slideshowTextPaints.valueAt(i2), typeface, typeface4, typeface3, typeface2);
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
        } else {
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
        if (selectedColor == 0) {
            this.backgroundPaint.setColor(-1);
            this.listView.setGlowColor(-657673);
        } else if (selectedColor == 1) {
            this.backgroundPaint.setColor(-659492);
            this.listView.setGlowColor(-659492);
        } else if (selectedColor == 2) {
            this.backgroundPaint.setColor(-15461356);
            this.listView.setGlowColor(-15461356);
        }
        for (int i2 = 0; i2 < Theme.chat_ivStatesDrawable.length; i2++) {
            Theme.setCombinedDrawableColor(Theme.chat_ivStatesDrawable[i2][0], getTextColor(), false);
            Theme.setCombinedDrawableColor(Theme.chat_ivStatesDrawable[i2][0], getTextColor(), true);
            Theme.setCombinedDrawableColor(Theme.chat_ivStatesDrawable[i2][1], getTextColor(), false);
            Theme.setCombinedDrawableColor(Theme.chat_ivStatesDrawable[i2][1], getTextColor(), true);
        }
        if (quoteLinePaint != null) {
            quoteLinePaint.setColor(getTextColor());
        }
        if (listTextPointerPaint != null) {
            listTextPointerPaint.setColor(getTextColor());
        }
        if (preformattedBackgroundPaint != null) {
            if (selectedColor == 0) {
                preformattedBackgroundPaint.setColor(-657156);
            } else if (selectedColor == 1) {
                preformattedBackgroundPaint.setColor(-1712440);
            } else if (selectedColor == 2) {
                preformattedBackgroundPaint.setColor(-14277082);
            }
        }
        if (urlPaint != null) {
            if (selectedColor == 0) {
                urlPaint.setColor(-1315861);
            } else if (selectedColor == 1) {
                urlPaint.setColor(-1712440);
            } else if (selectedColor == 2) {
                urlPaint.setColor(-14277082);
            }
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
        if (embedPostDatePaint != null) {
            if (selectedColor == 0) {
                embedPostDatePaint.setColor(-7366752);
            } else if (selectedColor == 1) {
                embedPostDatePaint.setColor(-11711675);
            } else if (selectedColor == 2) {
                embedPostDatePaint.setColor(-10066330);
            }
        }
        if (dividerPaint != null) {
            if (selectedColor == 0) {
                dividerPaint.setColor(-3288619);
            } else if (selectedColor == 1) {
                dividerPaint.setColor(-4080987);
            } else if (selectedColor == 2) {
                dividerPaint.setColor(-12303292);
            }
        }
        for (selectedColor = 0; selectedColor < titleTextPaints.size(); selectedColor++) {
            ((TextPaint) titleTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < subtitleTextPaints.size(); selectedColor++) {
            ((TextPaint) subtitleTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < headerTextPaints.size(); selectedColor++) {
            ((TextPaint) headerTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < subheaderTextPaints.size(); selectedColor++) {
            ((TextPaint) subheaderTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < quoteTextPaints.size(); selectedColor++) {
            ((TextPaint) quoteTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < preformattedTextPaints.size(); selectedColor++) {
            ((TextPaint) preformattedTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < paragraphTextPaints.size(); selectedColor++) {
            ((TextPaint) paragraphTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < listTextPaints.size(); selectedColor++) {
            ((TextPaint) listTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < embedPostTextPaints.size(); selectedColor++) {
            ((TextPaint) embedPostTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < videoTextPaints.size(); selectedColor++) {
            ((TextPaint) videoTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < captionTextPaints.size(); selectedColor++) {
            ((TextPaint) captionTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < authorTextPaints.size(); selectedColor++) {
            ((TextPaint) authorTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < footerTextPaints.size(); selectedColor++) {
            ((TextPaint) footerTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < subquoteTextPaints.size(); selectedColor++) {
            ((TextPaint) subquoteTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < embedPostCaptionTextPaints.size(); selectedColor++) {
            ((TextPaint) embedPostCaptionTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        for (selectedColor = 0; selectedColor < embedTextPaints.size(); selectedColor++) {
            ((TextPaint) embedTextPaints.valueAt(selectedColor)).setColor(getTextColor());
        }
        while (i < slideshowTextPaints.size()) {
            ((TextPaint) slideshowTextPaints.valueAt(i)).setColor(getTextColor());
            i++;
        }
    }

    public void setParentActivity(Activity activity, BaseFragment baseFragment) {
        Context context = activity;
        this.parentFragment = baseFragment;
        this.currentAccount = UserConfig.selectedAccount;
        this.leftImage.setCurrentAccount(this.currentAccount);
        this.rightImage.setCurrentAccount(this.currentAccount);
        this.centerImage.setCurrentAccount(this.currentAccount);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStarted);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme);
        if (this.parentActivity == context) {
            updatePaintColors();
            return;
        }
        r0.parentActivity = context;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0);
        r0.selectedFontSize = sharedPreferences.getInt("font_size", 2);
        r0.selectedFont = sharedPreferences.getInt("font_type", 0);
        r0.selectedColor = sharedPreferences.getInt("font_color", 0);
        r0.nightModeEnabled = sharedPreferences.getBoolean("nightModeEnabled", false);
        r0.backgroundPaint = new Paint();
        r0.layerShadowDrawable = activity.getResources().getDrawable(C0446R.drawable.layer_shadow);
        r0.slideDotDrawable = activity.getResources().getDrawable(C0446R.drawable.slide_dot_small);
        r0.slideDotBigDrawable = activity.getResources().getDrawable(C0446R.drawable.slide_dot_big);
        r0.scrimPaint = new Paint();
        r0.windowView = new WindowView(context);
        r0.windowView.setWillNotDraw(false);
        r0.windowView.setClipChildren(true);
        r0.windowView.setFocusable(false);
        r0.containerView = new FrameLayout(context);
        r0.windowView.addView(r0.containerView, LayoutHelper.createFrame(-1, -1, 51));
        r0.containerView.setFitsSystemWindows(true);
        if (VERSION.SDK_INT >= 21) {
            r0.containerView.setOnApplyWindowInsetsListener(new C08256());
        }
        r0.containerView.setSystemUiVisibility(1028);
        r0.photoContainerBackground = new View(context);
        r0.photoContainerBackground.setVisibility(4);
        r0.photoContainerBackground.setBackgroundDrawable(r0.photoBackgroundDrawable);
        r0.windowView.addView(r0.photoContainerBackground, LayoutHelper.createFrame(-1, -1, 51));
        r0.animatingImageView = new ClippingImageView(context);
        r0.animatingImageView.setAnimationValues(r0.animationValues);
        r0.animatingImageView.setVisibility(8);
        r0.windowView.addView(r0.animatingImageView, LayoutHelper.createFrame(40, 40.0f));
        r0.photoContainerView = new FrameLayoutDrawer(context) {
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                i4 = (i4 - i2) - ArticleViewer.this.captionTextView.getMeasuredHeight();
                if (!ArticleViewer.this.bottomLayout.getVisibility()) {
                    i4 -= ArticleViewer.this.bottomLayout.getMeasuredHeight();
                }
                ArticleViewer.this.captionTextView.layout(0, i4, ArticleViewer.this.captionTextView.getMeasuredWidth(), ArticleViewer.this.captionTextView.getMeasuredHeight() + i4);
            }
        };
        r0.photoContainerView.setVisibility(4);
        r0.photoContainerView.setWillNotDraw(false);
        r0.windowView.addView(r0.photoContainerView, LayoutHelper.createFrame(-1, -1, 51));
        r0.fullscreenVideoContainer = new FrameLayout(context);
        r0.fullscreenVideoContainer.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        r0.fullscreenVideoContainer.setVisibility(4);
        r0.windowView.addView(r0.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
        r0.fullscreenAspectRatioView = new AspectRatioFrameLayout(context);
        r0.fullscreenAspectRatioView.setVisibility(8);
        r0.fullscreenVideoContainer.addView(r0.fullscreenAspectRatioView, LayoutHelper.createFrame(-1, -1, 17));
        r0.fullscreenTextureView = new TextureView(context);
        if (VERSION.SDK_INT >= 21) {
            r0.barBackground = new View(context);
            r0.barBackground.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
            r0.windowView.addView(r0.barBackground);
        }
        r0.listView = new RecyclerListView(context) {
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                z = getChildCount();
                boolean z2 = false;
                while (z2 < z) {
                    i3 = getChildAt(z2);
                    if ((i3.getTag() instanceof Integer) == 0 || ((Integer) i3.getTag()).intValue() != 90 || i3.getBottom() >= getMeasuredHeight()) {
                        z2++;
                    } else {
                        z = getMeasuredHeight();
                        i3.layout(0, z - i3.getMeasuredHeight(), i3.getMeasuredWidth(), z);
                        return;
                    }
                }
            }
        };
        RecyclerListView recyclerListView = r0.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(r0.parentActivity, 1, false);
        r0.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        recyclerListView = r0.listView;
        Adapter webpageAdapter = new WebpageAdapter(r0.parentActivity);
        r0.adapter = webpageAdapter;
        recyclerListView.setAdapter(webpageAdapter);
        r0.listView.setClipToPadding(false);
        r0.listView.setPadding(0, AndroidUtilities.dp(56.0f), 0, 0);
        r0.listView.setTopGlowOffset(AndroidUtilities.dp(56.0f));
        r0.containerView.addView(r0.listView, LayoutHelper.createFrame(-1, -1.0f));
        r0.listView.setOnItemLongClickListener(new C19139());
        r0.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int i) {
                if (i != ArticleViewer.this.blocks.size() || ArticleViewer.this.currentPage == null) {
                    if (i >= 0 && i < ArticleViewer.this.blocks.size()) {
                        PageBlock pageBlock = (PageBlock) ArticleViewer.this.blocks.get(i);
                        if ((pageBlock instanceof TL_pageBlockChannel) != 0) {
                            MessagesController.getInstance(ArticleViewer.this.currentAccount).openByUserName(pageBlock.channel.username, ArticleViewer.this.parentFragment, 2);
                            ArticleViewer.this.close(0, true);
                        }
                    }
                } else if (ArticleViewer.this.previewsReqId == null) {
                    view = MessagesController.getInstance(ArticleViewer.this.currentAccount).getUserOrChat("previews");
                    if ((view instanceof TL_user) != 0) {
                        ArticleViewer.this.openPreviewsChat((User) view, ArticleViewer.this.currentPage.id);
                    } else {
                        view = UserConfig.selectedAccount;
                        final long j = ArticleViewer.this.currentPage.id;
                        ArticleViewer.this.showProgressView(true);
                        i = new TL_contacts_resolveUsername();
                        i.username = "previews";
                        ArticleViewer.this.previewsReqId = ConnectionsManager.getInstance(view).sendRequest(i, new RequestDelegate() {
                            public void run(final TLObject tLObject, TL_error tL_error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (ArticleViewer.this.previewsReqId != 0) {
                                            ArticleViewer.this.previewsReqId = 0;
                                            ArticleViewer.this.showProgressView(false);
                                            if (tLObject != null) {
                                                TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) tLObject;
                                                MessagesController.getInstance(view).putUsers(tL_contacts_resolvedPeer.users, false);
                                                MessagesStorage.getInstance(view).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, false, true);
                                                if (!tL_contacts_resolvedPeer.users.isEmpty()) {
                                                    ArticleViewer.this.openPreviewsChat((User) tL_contacts_resolvedPeer.users.get(0), j);
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
        r0.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (ArticleViewer.this.listView.getChildCount() != null) {
                    ArticleViewer.this.headerView.invalidate();
                    ArticleViewer.this.checkScroll(i2);
                }
            }
        });
        r0.headerPaint.setColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        r0.headerProgressPaint.setColor(-14408666);
        r0.headerView = new FrameLayout(context) {
            protected void onDraw(Canvas canvas) {
                float measuredWidth = (float) getMeasuredWidth();
                float measuredHeight = (float) getMeasuredHeight();
                canvas.drawRect(0.0f, 0.0f, measuredWidth, measuredHeight, ArticleViewer.this.headerPaint);
                if (ArticleViewer.this.layoutManager != null) {
                    View findViewByPosition;
                    int findFirstVisibleItemPosition = ArticleViewer.this.layoutManager.findFirstVisibleItemPosition();
                    int findLastVisibleItemPosition = ArticleViewer.this.layoutManager.findLastVisibleItemPosition();
                    int itemCount = ArticleViewer.this.layoutManager.getItemCount();
                    int i = itemCount - 2;
                    if (findLastVisibleItemPosition >= i) {
                        findViewByPosition = ArticleViewer.this.layoutManager.findViewByPosition(i);
                    } else {
                        findViewByPosition = ArticleViewer.this.layoutManager.findViewByPosition(findFirstVisibleItemPosition);
                    }
                    if (findViewByPosition != null) {
                        float measuredHeight2;
                        measuredWidth /= (float) (itemCount - 1);
                        ArticleViewer.this.layoutManager.getChildCount();
                        float measuredHeight3 = (float) findViewByPosition.getMeasuredHeight();
                        if (findLastVisibleItemPosition >= i) {
                            measuredHeight2 = ((((float) (i - findFirstVisibleItemPosition)) * measuredWidth) * ((float) (ArticleViewer.this.listView.getMeasuredHeight() - findViewByPosition.getTop()))) / measuredHeight3;
                        } else {
                            measuredHeight2 = (1.0f - ((((float) Math.min(0, findViewByPosition.getTop() - ArticleViewer.this.listView.getPaddingTop())) + measuredHeight3) / measuredHeight3)) * measuredWidth;
                        }
                        canvas.drawRect(0.0f, 0.0f, (((float) findFirstVisibleItemPosition) * measuredWidth) + measuredHeight2, measuredHeight, ArticleViewer.this.headerProgressPaint);
                    }
                }
            }
        };
        r0.headerView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        r0.headerView.setWillNotDraw(false);
        r0.containerView.addView(r0.headerView, LayoutHelper.createFrame(-1, 56.0f));
        r0.backButton = new ImageView(context);
        r0.backButton.setScaleType(ScaleType.CENTER);
        r0.backDrawable = new BackDrawable(false);
        r0.backDrawable.setAnimationTime(200.0f);
        r0.backDrawable.setColor(-5000269);
        r0.backDrawable.setRotated(false);
        r0.backButton.setImageDrawable(r0.backDrawable);
        r0.backButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        r0.headerView.addView(r0.backButton, LayoutHelper.createFrame(54, 56.0f));
        r0.backButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ArticleViewer.this.close(true, true);
            }
        });
        View linearLayout = new LinearLayout(r0.parentActivity);
        linearLayout.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        linearLayout.setOrientation(1);
        int i = 0;
        while (i < 3) {
            r0.colorCells[i] = new ColorCell(r0.parentActivity);
            switch (i) {
                case 0:
                    r0.nightModeImageView = new ImageView(r0.parentActivity);
                    r0.nightModeImageView.setScaleType(ScaleType.CENTER);
                    r0.nightModeImageView.setImageResource(C0446R.drawable.moon);
                    ImageView imageView = r0.nightModeImageView;
                    int i2 = (!r0.nightModeEnabled || r0.selectedColor == 2) ? -3355444 : -15428119;
                    imageView.setColorFilter(new PorterDuffColorFilter(i2, Mode.MULTIPLY));
                    r0.nightModeImageView.setBackgroundDrawable(Theme.createSelectorDrawable(251658240));
                    r0.colorCells[i].addView(r0.nightModeImageView, LayoutHelper.createFrame(48, 48, 48 | (LocaleController.isRTL ? 3 : 5)));
                    r0.nightModeImageView.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            ArticleViewer.this.nightModeEnabled = ArticleViewer.this.nightModeEnabled ^ 1;
                            ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putBoolean("nightModeEnabled", ArticleViewer.this.nightModeEnabled).commit();
                            ArticleViewer.this.updateNightModeButton();
                            ArticleViewer.this.updatePaintColors();
                            ArticleViewer.this.adapter.notifyDataSetChanged();
                            if (ArticleViewer.this.nightModeEnabled != null) {
                                ArticleViewer.this.showNightModeHint();
                            }
                        }
                    });
                    r0.colorCells[i].setTextAndColor(LocaleController.getString("ColorWhite", C0446R.string.ColorWhite), -1);
                    break;
                case 1:
                    r0.colorCells[i].setTextAndColor(LocaleController.getString("ColorSepia", C0446R.string.ColorSepia), -1382967);
                    break;
                case 2:
                    r0.colorCells[i].setTextAndColor(LocaleController.getString("ColorDark", C0446R.string.ColorDark), -14474461);
                    break;
                default:
                    break;
            }
            r0.colorCells[i].select(i == r0.selectedColor);
            r0.colorCells[i].setTag(Integer.valueOf(i));
            r0.colorCells[i].setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    view = ((Integer) view.getTag()).intValue();
                    ArticleViewer.this.selectedColor = view;
                    int i = 0;
                    while (i < 3) {
                        ArticleViewer.this.colorCells[i].select(i == view);
                        i++;
                    }
                    ArticleViewer.this.updateNightModeButton();
                    ArticleViewer.this.updatePaintColors();
                    ArticleViewer.this.adapter.notifyDataSetChanged();
                }
            });
            linearLayout.addView(r0.colorCells[i], LayoutHelper.createLinear(-1, 48));
            i++;
        }
        updateNightModeButton();
        View view = new View(r0.parentActivity);
        view.setBackgroundColor(-2039584);
        linearLayout.addView(view, LayoutHelper.createLinear(-1, 1, 15.0f, 4.0f, 15.0f, 4.0f));
        view.getLayoutParams().height = 1;
        int i3 = 0;
        while (i3 < 2) {
            r0.fontCells[i3] = new FontCell(r0, r0.parentActivity);
            switch (i3) {
                case 0:
                    r0.fontCells[i3].setTextAndTypeface("Roboto", Typeface.DEFAULT);
                    break;
                case 1:
                    r0.fontCells[i3].setTextAndTypeface("Serif", Typeface.SERIF);
                    break;
                default:
                    break;
            }
            r0.fontCells[i3].select(i3 == r0.selectedFont);
            r0.fontCells[i3].setTag(Integer.valueOf(i3));
            r0.fontCells[i3].setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    view = ((Integer) view.getTag()).intValue();
                    ArticleViewer.this.selectedFont = view;
                    int i = 0;
                    while (i < 2) {
                        ArticleViewer.this.fontCells[i].select(i == view);
                        i++;
                    }
                    ArticleViewer.this.updatePaintFonts();
                    ArticleViewer.this.adapter.notifyDataSetChanged();
                }
            });
            linearLayout.addView(r0.fontCells[i3], LayoutHelper.createLinear(-1, 48));
            i3++;
        }
        view = new View(r0.parentActivity);
        view.setBackgroundColor(-2039584);
        linearLayout.addView(view, LayoutHelper.createLinear(-1, 1, 15.0f, 4.0f, 15.0f, 4.0f));
        view.getLayoutParams().height = 1;
        view = new TextView(r0.parentActivity);
        view.setTextColor(-14606047);
        view.setTextSize(1, 16.0f);
        view.setLines(1);
        view.setMaxLines(1);
        view.setSingleLine(true);
        view.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        view.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        view.setText(LocaleController.getString("FontSize", C0446R.string.FontSize));
        linearLayout.addView(view, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 17, 12, 17, 0));
        linearLayout.addView(new SizeChooseView(r0.parentActivity), LayoutHelper.createLinear(-1, 38, 0.0f, 0.0f, 0.0f, 1.0f));
        r0.settingsButton = new ActionBarMenuItem(r0.parentActivity, null, Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, -1);
        r0.settingsButton.setPopupAnimationEnabled(false);
        r0.settingsButton.setLayoutInScreen(true);
        view = new TextView(r0.parentActivity);
        view.setTextSize(1, 18.0f);
        view.setText("Aa");
        view.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        view.setTextColor(-5000269);
        view.setGravity(17);
        r0.settingsButton.addView(view, LayoutHelper.createFrame(-1, -1.0f));
        r0.settingsButton.addSubItem(linearLayout, AndroidUtilities.dp(220.0f), -2);
        r0.settingsButton.redrawPopup(-1);
        r0.headerView.addView(r0.settingsButton, LayoutHelper.createFrame(48, 56.0f, 53, 0.0f, 0.0f, 56.0f, 0.0f));
        r0.shareContainer = new FrameLayout(context);
        r0.headerView.addView(r0.shareContainer, LayoutHelper.createFrame(48, 56, 53));
        r0.shareContainer.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (ArticleViewer.this.currentPage != null) {
                    if (ArticleViewer.this.parentActivity != null) {
                        ArticleViewer.this.showDialog(new ShareAlert(ArticleViewer.this.parentActivity, null, ArticleViewer.this.currentPage.url, false, ArticleViewer.this.currentPage.url, true));
                        ArticleViewer.this.hideActionBar();
                    }
                }
            }
        });
        r0.shareButton = new ImageView(context);
        r0.shareButton.setScaleType(ScaleType.CENTER);
        r0.shareButton.setImageResource(C0446R.drawable.ic_share_article);
        r0.shareButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        r0.shareContainer.addView(r0.shareButton, LayoutHelper.createFrame(48, 56.0f));
        r0.progressView = new ContextProgressView(context, 2);
        r0.progressView.setVisibility(8);
        r0.shareContainer.addView(r0.progressView, LayoutHelper.createFrame(48, 56.0f));
        r0.windowLayoutParams = new LayoutParams();
        r0.windowLayoutParams.height = -1;
        r0.windowLayoutParams.format = -3;
        r0.windowLayoutParams.width = -1;
        r0.windowLayoutParams.gravity = 51;
        r0.windowLayoutParams.type = 99;
        if (VERSION.SDK_INT >= 21) {
            r0.windowLayoutParams.flags = -NUM;
        } else {
            r0.windowLayoutParams.flags = 8;
        }
        if (progressDrawables == null) {
            progressDrawables = new Drawable[4];
            progressDrawables[0] = r0.parentActivity.getResources().getDrawable(C0446R.drawable.circle_big);
            progressDrawables[1] = r0.parentActivity.getResources().getDrawable(C0446R.drawable.cancel_big);
            progressDrawables[2] = r0.parentActivity.getResources().getDrawable(C0446R.drawable.load_big);
            progressDrawables[3] = r0.parentActivity.getResources().getDrawable(C0446R.drawable.play_big);
        }
        r0.scroller = new Scroller(context);
        r0.blackPaint.setColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
        r0.actionBar = new ActionBar(context);
        r0.actionBar.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        r0.actionBar.setOccupyStatusBar(false);
        r0.actionBar.setTitleColor(-1);
        r0.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
        r0.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        r0.actionBar.setTitle(LocaleController.formatString("Of", C0446R.string.Of, Integer.valueOf(1), Integer.valueOf(1)));
        r0.photoContainerView.addView(r0.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        r0.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ArticleViewer.this.closePhoto(true);
                } else if (i == 1) {
                    if (VERSION.SDK_INT < 23 || ArticleViewer.this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        i = ArticleViewer.this.getMediaFile(ArticleViewer.this.currentIndex);
                        if (i == 0 || !i.exists()) {
                            i = new AlertDialog.Builder(ArticleViewer.this.parentActivity);
                            i.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                            i.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
                            i.setMessage(LocaleController.getString("PleaseDownload", C0446R.string.PleaseDownload));
                            ArticleViewer.this.showDialog(i.create());
                        } else {
                            MediaController.saveFile(i.toString(), ArticleViewer.this.parentActivity, ArticleViewer.this.isMediaVideo(ArticleViewer.this.currentIndex), null, null);
                        }
                    } else {
                        ArticleViewer.this.parentActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    }
                } else if (i == 2) {
                    ArticleViewer.this.onSharePressed();
                } else if (i == 3) {
                    try {
                        AndroidUtilities.openForView(ArticleViewer.this.getMedia(ArticleViewer.this.currentIndex), ArticleViewer.this.parentActivity);
                        ArticleViewer.this.closePhoto(false);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            }

            public boolean canOpenMenu() {
                File access$6800 = ArticleViewer.this.getMediaFile(ArticleViewer.this.currentIndex);
                return access$6800 != null && access$6800.exists();
            }
        });
        ActionBarMenu createMenu = r0.actionBar.createMenu();
        createMenu.addItem(2, (int) C0446R.drawable.share);
        r0.menuItem = createMenu.addItem(0, (int) C0446R.drawable.ic_ab_other);
        r0.menuItem.setLayoutInScreen(true);
        r0.menuItem.addSubItem(3, LocaleController.getString("OpenInExternalApp", C0446R.string.OpenInExternalApp));
        r0.menuItem.addSubItem(1, LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
        r0.bottomLayout = new FrameLayout(r0.parentActivity);
        r0.bottomLayout.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        r0.photoContainerView.addView(r0.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        r0.captionTextViewOld = new TextView(context);
        r0.captionTextViewOld.setMaxLines(10);
        r0.captionTextViewOld.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        r0.captionTextViewOld.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
        r0.captionTextViewOld.setLinkTextColor(-1);
        r0.captionTextViewOld.setTextColor(-1);
        r0.captionTextViewOld.setGravity(19);
        r0.captionTextViewOld.setTextSize(1, 16.0f);
        r0.captionTextViewOld.setVisibility(4);
        r0.photoContainerView.addView(r0.captionTextViewOld, LayoutHelper.createFrame(-1, -2, 83));
        TextView textView = new TextView(context);
        r0.captionTextViewNew = textView;
        r0.captionTextView = textView;
        r0.captionTextViewNew.setMaxLines(10);
        r0.captionTextViewNew.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        r0.captionTextViewNew.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f));
        r0.captionTextViewNew.setLinkTextColor(-1);
        r0.captionTextViewNew.setTextColor(-1);
        r0.captionTextViewNew.setGravity(19);
        r0.captionTextViewNew.setTextSize(1, 16.0f);
        r0.captionTextViewNew.setVisibility(4);
        r0.photoContainerView.addView(r0.captionTextViewNew, LayoutHelper.createFrame(-1, -2, 83));
        r0.radialProgressViews[0] = new RadialProgressView(context, r0.photoContainerView);
        r0.radialProgressViews[0].setBackgroundState(0, false);
        r0.radialProgressViews[1] = new RadialProgressView(context, r0.photoContainerView);
        r0.radialProgressViews[1].setBackgroundState(0, false);
        r0.radialProgressViews[2] = new RadialProgressView(context, r0.photoContainerView);
        r0.radialProgressViews[2].setBackgroundState(0, false);
        r0.videoPlayerSeekbar = new SeekBar(context);
        r0.videoPlayerSeekbar.setColors(NUM, NUM, -2764585, -1, -1);
        r0.videoPlayerSeekbar.setDelegate(new SeekBarDelegate() {
            public void onSeekBarDrag(float f) {
                if (ArticleViewer.this.videoPlayer != null) {
                    ArticleViewer.this.videoPlayer.seekTo((long) ((int) (f * ((float) ArticleViewer.this.videoPlayer.getDuration()))));
                }
            }
        });
        r0.videoPlayerControlFrameLayout = new FrameLayout(context) {
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

            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                long j = 0;
                if (ArticleViewer.this.videoPlayer != 0) {
                    i = ArticleViewer.this.videoPlayer.getDuration();
                    if (i != C0542C.TIME_UNSET) {
                        j = i;
                    }
                }
                j /= 1000;
                i = ArticleViewer.this.videoPlayerTime.getPaint();
                r2 = new Object[4];
                long j2 = j / 60;
                r2[0] = Long.valueOf(j2);
                j %= 60;
                r2[1] = Long.valueOf(j);
                r2[2] = Long.valueOf(j2);
                r2[3] = Long.valueOf(j);
                ArticleViewer.this.videoPlayerSeekbar.setSize((getMeasuredWidth() - AndroidUtilities.dp(64.0f)) - ((int) Math.ceil((double) i.measureText(String.format("%02d:%02d / %02d:%02d", r2)))), getMeasuredHeight());
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                ArticleViewer.this.videoPlayerSeekbar.setProgress(ArticleViewer.this.videoPlayer ? ((float) ArticleViewer.this.videoPlayer.getCurrentPosition()) / ((float) ArticleViewer.this.videoPlayer.getDuration()) : false);
            }

            protected void onDraw(Canvas canvas) {
                canvas.save();
                canvas.translate((float) AndroidUtilities.dp(48.0f), 0.0f);
                ArticleViewer.this.videoPlayerSeekbar.draw(canvas);
                canvas.restore();
            }
        };
        r0.videoPlayerControlFrameLayout.setWillNotDraw(false);
        r0.bottomLayout.addView(r0.videoPlayerControlFrameLayout, LayoutHelper.createFrame(-1, -1, 51));
        r0.videoPlayButton = new ImageView(context);
        r0.videoPlayButton.setScaleType(ScaleType.CENTER);
        r0.videoPlayerControlFrameLayout.addView(r0.videoPlayButton, LayoutHelper.createFrame(48, 48, 51));
        r0.videoPlayButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (ArticleViewer.this.videoPlayer == null) {
                    return;
                }
                if (ArticleViewer.this.isPlaying != null) {
                    ArticleViewer.this.videoPlayer.pause();
                } else {
                    ArticleViewer.this.videoPlayer.play();
                }
            }
        });
        r0.videoPlayerTime = new TextView(context);
        r0.videoPlayerTime.setTextColor(-1);
        r0.videoPlayerTime.setGravity(16);
        r0.videoPlayerTime.setTextSize(1, 13.0f);
        r0.videoPlayerControlFrameLayout.addView(r0.videoPlayerTime, LayoutHelper.createFrame(-2, -1.0f, 53, 0.0f, 0.0f, 8.0f, 0.0f));
        r0.gestureDetector = new GestureDetector(context, r0);
        r0.gestureDetector.setOnDoubleTapListener(r0);
        r0.centerImage.setParentView(r0.photoContainerView);
        r0.centerImage.setCrossfadeAlpha((byte) 2);
        r0.centerImage.setInvalidateAll(true);
        r0.leftImage.setParentView(r0.photoContainerView);
        r0.leftImage.setCrossfadeAlpha((byte) 2);
        r0.leftImage.setInvalidateAll(true);
        r0.rightImage.setParentView(r0.photoContainerView);
        r0.rightImage.setCrossfadeAlpha((byte) 2);
        r0.rightImage.setInvalidateAll(true);
        updatePaintColors();
    }

    private void showNightModeHint() {
        if (this.parentActivity != null && this.nightModeHintView == null) {
            if (this.nightModeEnabled) {
                this.nightModeHintView = new FrameLayout(this.parentActivity);
                this.nightModeHintView.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
                this.containerView.addView(this.nightModeHintView, LayoutHelper.createFrame(-1, -2, 83));
                View imageView = new ImageView(this.parentActivity);
                imageView.setScaleType(ScaleType.CENTER);
                imageView.setImageResource(C0446R.drawable.moon);
                int i = 3;
                this.nightModeHintView.addView(imageView, LayoutHelper.createFrame(56, 56, (LocaleController.isRTL ? 5 : 3) | 16));
                imageView = new TextView(this.parentActivity);
                imageView.setText(LocaleController.getString("InstantViewNightMode", C0446R.string.InstantViewNightMode));
                imageView.setTextColor(-1);
                imageView.setTextSize(1, 15.0f);
                FrameLayout frameLayout = this.nightModeHintView;
                if (LocaleController.isRTL) {
                    i = 5;
                }
                int i2 = i | 48;
                int i3 = 10;
                float f = (float) (LocaleController.isRTL ? 10 : 56);
                if (LocaleController.isRTL) {
                    i3 = 56;
                }
                frameLayout.addView(imageView, LayoutHelper.createFrame(-1, -1.0f, i2, f, 11.0f, (float) i3, 12.0f));
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this.nightModeHintView, "translationY", new float[]{(float) AndroidUtilities.dp(100.0f), 0.0f});
                animatorSet.playTogether(animatorArr);
                animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
                animatorSet.addListener(new AnimatorListenerAdapter() {

                    /* renamed from: org.telegram.ui.ArticleViewer$23$1 */
                    class C08131 implements Runnable {
                        C08131() {
                        }

                        public void run() {
                            AnimatorSet animatorSet = new AnimatorSet();
                            Animator[] animatorArr = new Animator[1];
                            animatorArr[0] = ObjectAnimator.ofFloat(ArticleViewer.this.nightModeHintView, "translationY", new float[]{(float) AndroidUtilities.dp(100.0f)});
                            animatorSet.playTogether(animatorArr);
                            animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
                            animatorSet.setDuration(250);
                            animatorSet.start();
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        AndroidUtilities.runOnUIThread(new C08131(), 3000);
                    }
                });
                animatorSet.setDuration(250);
                animatorSet.start();
            }
        }
    }

    private void updateNightModeButton() {
        this.nightModeImageView.setEnabled(this.selectedColor != 2);
        this.nightModeImageView.setAlpha(this.selectedColor == 2 ? 0.5f : 1.0f);
        ImageView imageView = this.nightModeImageView;
        int i = (!this.nightModeEnabled || this.selectedColor == 2) ? -3355444 : -15428119;
        imageView.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
    }

    private void checkScroll(int i) {
        int dp = AndroidUtilities.dp(56.0f);
        int max = Math.max(AndroidUtilities.statusBarHeight, AndroidUtilities.dp(24.0f));
        float f = (float) (dp - max);
        i = this.currentHeaderHeight - i;
        if (i < max) {
            i = max;
        } else if (i > dp) {
            i = dp;
        }
        this.currentHeaderHeight = i;
        i = NUM + ((((float) (this.currentHeaderHeight - max)) / f) * 0.2f);
        this.backButton.setScaleX(i);
        this.backButton.setScaleY(i);
        this.backButton.setTranslationY((float) ((dp - this.currentHeaderHeight) / 2));
        this.shareContainer.setScaleX(i);
        this.shareContainer.setScaleY(i);
        this.settingsButton.setScaleX(i);
        this.settingsButton.setScaleY(i);
        this.shareContainer.setTranslationY((float) ((dp - this.currentHeaderHeight) / 2));
        this.settingsButton.setTranslationY((float) ((dp - this.currentHeaderHeight) / 2));
        this.headerView.setTranslationY((float) (this.currentHeaderHeight - dp));
        this.listView.setTopGlowOffset(this.currentHeaderHeight);
    }

    private void openPreviewsChat(User user, long j) {
        if (user != null) {
            if (this.parentActivity != null) {
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
    }

    private void addAllMediaFromBlock(PageBlock pageBlock) {
        if (!(pageBlock instanceof TL_pageBlockPhoto)) {
            if (!(pageBlock instanceof TL_pageBlockVideo) || !isVideoBlock(pageBlock)) {
                int i = 0;
                int size;
                PageBlock pageBlock2;
                if (pageBlock instanceof TL_pageBlockSlideshow) {
                    TL_pageBlockSlideshow tL_pageBlockSlideshow = (TL_pageBlockSlideshow) pageBlock;
                    size = tL_pageBlockSlideshow.items.size();
                    while (i < size) {
                        pageBlock2 = (PageBlock) tL_pageBlockSlideshow.items.get(i);
                        if ((pageBlock2 instanceof TL_pageBlockPhoto) || ((pageBlock2 instanceof TL_pageBlockVideo) && isVideoBlock(pageBlock2))) {
                            this.photoBlocks.add(pageBlock2);
                        }
                        i++;
                    }
                    return;
                } else if (pageBlock instanceof TL_pageBlockCollage) {
                    TL_pageBlockCollage tL_pageBlockCollage = (TL_pageBlockCollage) pageBlock;
                    size = tL_pageBlockCollage.items.size();
                    while (i < size) {
                        pageBlock2 = (PageBlock) tL_pageBlockCollage.items.get(i);
                        if ((pageBlock2 instanceof TL_pageBlockPhoto) || ((pageBlock2 instanceof TL_pageBlockVideo) && isVideoBlock(pageBlock2))) {
                            this.photoBlocks.add(pageBlock2);
                        }
                        i++;
                    }
                    return;
                } else if (!(pageBlock instanceof TL_pageBlockCover)) {
                    return;
                } else {
                    if ((pageBlock.cover instanceof TL_pageBlockPhoto) || ((pageBlock.cover instanceof TL_pageBlockVideo) && isVideoBlock(pageBlock.cover))) {
                        this.photoBlocks.add(pageBlock.cover);
                        return;
                    }
                    return;
                }
            }
        }
        this.photoBlocks.add(pageBlock);
    }

    public boolean open(MessageObject messageObject) {
        return open(messageObject, null, null, true);
    }

    public boolean open(TL_webPage tL_webPage, String str) {
        return open(null, tL_webPage, str, true);
    }

    private boolean open(final org.telegram.messenger.MessageObject r12, org.telegram.tgnet.TLRPC.WebPage r13, java.lang.String r14, boolean r15) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r11 = this;
        r0 = r11.parentActivity;
        r1 = 0;
        if (r0 == 0) goto L_0x01f4;
    L_0x0005:
        r0 = r11.isVisible;
        if (r0 == 0) goto L_0x000d;
    L_0x0009:
        r0 = r11.collapsed;
        if (r0 == 0) goto L_0x01f4;
    L_0x000d:
        if (r12 != 0) goto L_0x0013;
    L_0x000f:
        if (r13 != 0) goto L_0x0013;
    L_0x0011:
        goto L_0x01f4;
    L_0x0013:
        if (r12 == 0) goto L_0x001b;
    L_0x0015:
        r13 = r12.messageOwner;
        r13 = r13.media;
        r13 = r13.webpage;
    L_0x001b:
        if (r15 == 0) goto L_0x0041;
    L_0x001d:
        r15 = new org.telegram.tgnet.TLRPC$TL_messages_getWebPage;
        r15.<init>();
        r0 = r13.url;
        r15.url = r0;
        r0 = r13.cached_page;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pagePart;
        if (r0 == 0) goto L_0x002f;
    L_0x002c:
        r15.hash = r1;
        goto L_0x0033;
    L_0x002f:
        r0 = r13.hash;
        r15.hash = r0;
    L_0x0033:
        r0 = org.telegram.messenger.UserConfig.selectedAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r3 = new org.telegram.ui.ArticleViewer$24;
        r3.<init>(r13, r12, r0);
        r2.sendRequest(r15, r3);
    L_0x0041:
        r15 = r11.pagesStack;
        r15.clear();
        r11.collapsed = r1;
        r15 = r11.backDrawable;
        r0 = 0;
        r15.setRotation(r0, r1);
        r15 = r11.containerView;
        r15.setTranslationX(r0);
        r15 = r11.containerView;
        r15.setTranslationY(r0);
        r15 = r11.listView;
        r15.setTranslationY(r0);
        r15 = r11.listView;
        r2 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r15.setAlpha(r2);
        r15 = r11.windowView;
        r15.setInnerTranslationX(r0);
        r15 = r11.actionBar;
        r2 = 8;
        r15.setVisibility(r2);
        r15 = r11.bottomLayout;
        r15.setVisibility(r2);
        r15 = r11.captionTextViewNew;
        r15.setVisibility(r2);
        r15 = r11.captionTextViewOld;
        r15.setVisibility(r2);
        r15 = r11.shareContainer;
        r15.setAlpha(r0);
        r15 = r11.backButton;
        r15.setAlpha(r0);
        r15 = r11.settingsButton;
        r15.setAlpha(r0);
        r15 = r11.layoutManager;
        r15.scrollToPositionWithOffset(r1, r1);
        r15 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r2 = -r2;
        r11.checkScroll(r2);
        r2 = -1;
        r3 = 35;
        r4 = 0;
        r5 = 1;
        if (r12 == 0) goto L_0x00ff;
    L_0x00a4:
        r13 = r12.messageOwner;
        r13 = r13.media;
        r13 = r13.webpage;
        r14 = r13.url;
        r14 = r14.toLowerCase();
        r6 = r1;
    L_0x00b1:
        r7 = r12.messageOwner;
        r7 = r7.entities;
        r7 = r7.size();
        if (r6 >= r7) goto L_0x00fc;
    L_0x00bb:
        r7 = r12.messageOwner;
        r7 = r7.entities;
        r7 = r7.get(r6);
        r7 = (org.telegram.tgnet.TLRPC.MessageEntity) r7;
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
        if (r8 == 0) goto L_0x00f9;
    L_0x00c9:
        r8 = r12.messageOwner;	 Catch:{ Exception -> 0x00f5 }
        r8 = r8.message;	 Catch:{ Exception -> 0x00f5 }
        r9 = r7.offset;	 Catch:{ Exception -> 0x00f5 }
        r10 = r7.offset;	 Catch:{ Exception -> 0x00f5 }
        r7 = r7.length;	 Catch:{ Exception -> 0x00f5 }
        r10 = r10 + r7;	 Catch:{ Exception -> 0x00f5 }
        r7 = r8.substring(r9, r10);	 Catch:{ Exception -> 0x00f5 }
        r7 = r7.toLowerCase();	 Catch:{ Exception -> 0x00f5 }
        r8 = r7.contains(r14);	 Catch:{ Exception -> 0x00f5 }
        if (r8 != 0) goto L_0x00e8;	 Catch:{ Exception -> 0x00f5 }
    L_0x00e2:
        r8 = r14.contains(r7);	 Catch:{ Exception -> 0x00f5 }
        if (r8 == 0) goto L_0x00f9;	 Catch:{ Exception -> 0x00f5 }
    L_0x00e8:
        r8 = r7.lastIndexOf(r3);	 Catch:{ Exception -> 0x00f5 }
        if (r8 == r2) goto L_0x00fc;	 Catch:{ Exception -> 0x00f5 }
    L_0x00ee:
        r8 = r8 + 1;	 Catch:{ Exception -> 0x00f5 }
        r7 = r7.substring(r8);	 Catch:{ Exception -> 0x00f5 }
        goto L_0x00fd;
    L_0x00f5:
        r7 = move-exception;
        org.telegram.messenger.FileLog.m3e(r7);
    L_0x00f9:
        r6 = r6 + 1;
        goto L_0x00b1;
    L_0x00fc:
        r7 = r4;
    L_0x00fd:
        r12 = r7;
        goto L_0x010e;
    L_0x00ff:
        if (r14 == 0) goto L_0x010d;
    L_0x0101:
        r12 = r14.lastIndexOf(r3);
        if (r12 == r2) goto L_0x010d;
    L_0x0107:
        r12 = r12 + r5;
        r12 = r14.substring(r12);
        goto L_0x010e;
    L_0x010d:
        r12 = r4;
    L_0x010e:
        r11.addPageToStack(r13, r12);
        r11.lastInsets = r4;
        r12 = r11.isVisible;
        if (r12 != 0) goto L_0x0156;
    L_0x0117:
        r12 = r11.parentActivity;
        r13 = "window";
        r12 = r12.getSystemService(r13);
        r12 = (android.view.WindowManager) r12;
        r13 = r11.attachedToWindow;
        if (r13 == 0) goto L_0x012a;
    L_0x0125:
        r13 = r11.windowView;	 Catch:{ Exception -> 0x012a }
        r12.removeView(r13);	 Catch:{ Exception -> 0x012a }
    L_0x012a:
        r13 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0151 }
        r14 = 21;	 Catch:{ Exception -> 0x0151 }
        if (r13 < r14) goto L_0x0137;	 Catch:{ Exception -> 0x0151 }
    L_0x0130:
        r13 = r11.windowLayoutParams;	 Catch:{ Exception -> 0x0151 }
        r14 = -NUM; // 0xffffffff80010100 float:-9.2194E-41 double:NaN;	 Catch:{ Exception -> 0x0151 }
        r13.flags = r14;	 Catch:{ Exception -> 0x0151 }
    L_0x0137:
        r13 = r11.windowLayoutParams;	 Catch:{ Exception -> 0x0151 }
        r14 = r13.flags;	 Catch:{ Exception -> 0x0151 }
        r14 = r14 | 1032;	 Catch:{ Exception -> 0x0151 }
        r13.flags = r14;	 Catch:{ Exception -> 0x0151 }
        r13 = r11.windowView;	 Catch:{ Exception -> 0x0151 }
        r13.setFocusable(r1);	 Catch:{ Exception -> 0x0151 }
        r13 = r11.containerView;	 Catch:{ Exception -> 0x0151 }
        r13.setFocusable(r1);	 Catch:{ Exception -> 0x0151 }
        r13 = r11.windowView;	 Catch:{ Exception -> 0x0151 }
        r14 = r11.windowLayoutParams;	 Catch:{ Exception -> 0x0151 }
        r12.addView(r13, r14);	 Catch:{ Exception -> 0x0151 }
        goto L_0x016f;
    L_0x0151:
        r12 = move-exception;
        org.telegram.messenger.FileLog.m3e(r12);
        return r1;
    L_0x0156:
        r12 = r11.windowLayoutParams;
        r13 = r12.flags;
        r13 = r13 & -17;
        r12.flags = r13;
        r12 = r11.parentActivity;
        r13 = "window";
        r12 = r12.getSystemService(r13);
        r12 = (android.view.WindowManager) r12;
        r13 = r11.windowView;
        r14 = r11.windowLayoutParams;
        r12.updateViewLayout(r13, r14);
    L_0x016f:
        r11.isVisible = r5;
        r11.animationInProgress = r5;
        r12 = r11.windowView;
        r12.setAlpha(r0);
        r12 = r11.containerView;
        r12.setAlpha(r0);
        r12 = new android.animation.AnimatorSet;
        r12.<init>();
        r13 = 3;
        r13 = new android.animation.Animator[r13];
        r14 = r11.windowView;
        r2 = "alpha";
        r3 = 2;
        r6 = new float[r3];
        r6 = {0, NUM};
        r14 = android.animation.ObjectAnimator.ofFloat(r14, r2, r6);
        r13[r1] = r14;
        r14 = r11.containerView;
        r2 = "alpha";
        r6 = new float[r3];
        r6 = {0, NUM};
        r14 = android.animation.ObjectAnimator.ofFloat(r14, r2, r6);
        r13[r5] = r14;
        r14 = r11.windowView;
        r2 = "translationX";
        r6 = new float[r3];
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r15 = (float) r15;
        r6[r1] = r15;
        r6[r5] = r0;
        r14 = android.animation.ObjectAnimator.ofFloat(r14, r2, r6);
        r13[r3] = r14;
        r12.playTogether(r13);
        r13 = new org.telegram.ui.ArticleViewer$25;
        r13.<init>();
        r11.animationEndRunnable = r13;
        r13 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r12.setDuration(r13);
        r13 = r11.interpolator;
        r12.setInterpolator(r13);
        r13 = new org.telegram.ui.ArticleViewer$26;
        r13.<init>();
        r12.addListener(r13);
        r13 = java.lang.System.currentTimeMillis();
        r11.transitionAnimationStartTime = r13;
        r13 = new org.telegram.ui.ArticleViewer$27;
        r13.<init>(r12);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r13);
        r12 = android.os.Build.VERSION.SDK_INT;
        r13 = 18;
        if (r12 < r13) goto L_0x01ee;
    L_0x01e9:
        r12 = r11.containerView;
        r12.setLayerType(r3, r4);
    L_0x01ee:
        r12 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r11.showActionBar(r12);
        return r5;
    L_0x01f4:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.open(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, java.lang.String, boolean):boolean");
    }

    private void hideActionBar() {
        AnimatorSet animatorSet = new AnimatorSet();
        r1 = new Animator[3];
        r1[0] = ObjectAnimator.ofFloat(this.backButton, "alpha", new float[]{0.0f});
        r1[1] = ObjectAnimator.ofFloat(this.shareContainer, "alpha", new float[]{0.0f});
        r1[2] = ObjectAnimator.ofFloat(this.settingsButton, "alpha", new float[]{0.0f});
        animatorSet.playTogether(r1);
        animatorSet.setDuration(250);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    private void showActionBar(int i) {
        AnimatorSet animatorSet = new AnimatorSet();
        r1 = new Animator[3];
        r1[0] = ObjectAnimator.ofFloat(this.backButton, "alpha", new float[]{1.0f});
        r1[1] = ObjectAnimator.ofFloat(this.shareContainer, "alpha", new float[]{1.0f});
        r1[2] = ObjectAnimator.ofFloat(this.settingsButton, "alpha", new float[]{1.0f});
        animatorSet.playTogether(r1);
        animatorSet.setDuration(150);
        animatorSet.setStartDelay((long) i);
        animatorSet.start();
    }

    private void showProgressView(final boolean z) {
        if (this.progressViewAnimation != null) {
            this.progressViewAnimation.cancel();
        }
        this.progressViewAnimation = new AnimatorSet();
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (z) {
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
            public void onAnimationEnd(Animator animator) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animator) != null) {
                    if (z == null) {
                        ArticleViewer.this.progressView.setVisibility(4);
                    } else {
                        ArticleViewer.this.shareButton.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animator) != null) {
                    ArticleViewer.this.progressViewAnimation = null;
                }
            }
        });
        this.progressViewAnimation.setDuration(150);
        this.progressViewAnimation.start();
    }

    public void collapse() {
        if (this.parentActivity != null && this.isVisible) {
            if (!checkAnimation()) {
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
                    FileLog.m3e(e);
                }
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[12];
                animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "translationX", new float[]{(float) (this.containerView.getMeasuredWidth() - AndroidUtilities.dp(56.0f))});
                FrameLayout frameLayout = this.containerView;
                String str = "translationY";
                float[] fArr = new float[1];
                fArr[0] = (float) (ActionBar.getCurrentActionBarHeight() + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
                animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, str, fArr);
                animatorArr[2] = ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.listView, "alpha", new float[]{0.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.listView, "translationY", new float[]{(float) (-AndroidUtilities.dp(56.0f))});
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
                this.animationEndRunnable = new Runnable() {
                    public void run() {
                        if (ArticleViewer.this.containerView != null) {
                            if (VERSION.SDK_INT >= 18) {
                                ArticleViewer.this.containerView.setLayerType(0, null);
                            }
                            ArticleViewer.this.animationInProgress = 0;
                            ((WindowManager) ArticleViewer.this.parentActivity.getSystemService("window")).updateViewLayout(ArticleViewer.this.windowView, ArticleViewer.this.windowLayoutParams);
                        }
                    }
                };
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
    }

    public void uncollapse() {
        if (this.parentActivity != null && this.isVisible) {
            if (!checkAnimation()) {
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
                this.animationEndRunnable = new Runnable() {
                    public void run() {
                        if (ArticleViewer.this.containerView != null) {
                            if (VERSION.SDK_INT >= 18) {
                                ArticleViewer.this.containerView.setLayerType(0, null);
                            }
                            ArticleViewer.this.animationInProgress = 0;
                        }
                    }
                };
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
    }

    private void saveCurrentPagePosition() {
        if (this.currentPage != null) {
            int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition != -1) {
                View findViewByPosition = this.layoutManager.findViewByPosition(findFirstVisibleItemPosition);
                boolean z = false;
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
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    z = true;
                }
                putInt.putBoolean(stringBuilder5, z).commit();
            }
        }
    }

    public void close(boolean z, boolean z2) {
        if (this.parentActivity != null && this.isVisible) {
            if (!checkAnimation()) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStarted);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
                if (this.fullscreenVideoContainer.getVisibility() == 0) {
                    if (this.customView != null) {
                        this.fullscreenVideoContainer.setVisibility(4);
                        this.customViewCallback.onCustomViewHidden();
                        this.fullscreenVideoContainer.removeView(this.customView);
                        this.customView = null;
                    } else if (this.fullscreenedVideo != null) {
                        this.fullscreenedVideo.exitFullscreen();
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
                    showProgressView(false);
                }
                if (this.previewsReqId != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.previewsReqId, true);
                    this.previewsReqId = 0;
                    showProgressView(false);
                }
                saveCurrentPagePosition();
                if (!z || z2 || !removeLastPageFromStack()) {
                    this.parentFragment = null;
                    try {
                        if (this.visibleDialog) {
                            this.visibleDialog.dismiss();
                            this.visibleDialog = null;
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    z = new AnimatorSet();
                    z2 = new Animator[true];
                    z2[0] = ObjectAnimator.ofFloat(this.windowView, "alpha", new float[]{0.0f});
                    z2[1] = ObjectAnimator.ofFloat(this.containerView, "alpha", new float[]{0.0f});
                    z2[2] = ObjectAnimator.ofFloat(this.windowView, "translationX", new float[]{0.0f, (float) AndroidUtilities.dp(56.0f)});
                    z.playTogether(z2);
                    this.animationInProgress = 2;
                    this.animationEndRunnable = new Runnable() {
                        public void run() {
                            if (ArticleViewer.this.containerView != null) {
                                if (VERSION.SDK_INT >= 18) {
                                    ArticleViewer.this.containerView.setLayerType(0, null);
                                }
                                ArticleViewer.this.animationInProgress = 0;
                                ArticleViewer.this.onClosed();
                            }
                        }
                    };
                    z.setDuration(150);
                    z.setInterpolator(this.interpolator);
                    z.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (ArticleViewer.this.animationEndRunnable != null) {
                                ArticleViewer.this.animationEndRunnable.run();
                                ArticleViewer.this.animationEndRunnable = null;
                            }
                        }
                    });
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    if (VERSION.SDK_INT >= true) {
                        this.containerView.setLayerType(2, null);
                    }
                    z.start();
                }
            }
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
            FileLog.m3e(e);
        }
        for (int i = 0; i < this.createdWebViews.size(); i++) {
            ((BlockEmbedCell) this.createdWebViews.get(i)).destroyWebView(false);
        }
        this.containerView.post(new Runnable() {
            public void run() {
                try {
                    if (ArticleViewer.this.windowView.getParent() != null) {
                        ((WindowManager) ArticleViewer.this.parentActivity.getSystemService("window")).removeView(ArticleViewer.this.windowView);
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    private void loadChannel(final BlockChannelCell blockChannelCell, Chat chat) {
        if (!this.loadingChannel) {
            if (!TextUtils.isEmpty(chat.username)) {
                this.loadingChannel = true;
                TLObject tL_contacts_resolveUsername = new TL_contacts_resolveUsername();
                tL_contacts_resolveUsername.username = chat.username;
                chat = UserConfig.selectedAccount;
                ConnectionsManager.getInstance(chat).sendRequest(tL_contacts_resolveUsername, new RequestDelegate() {
                    public void run(final TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                ArticleViewer.this.loadingChannel = false;
                                if (!(ArticleViewer.this.parentFragment == null || ArticleViewer.this.blocks == null)) {
                                    if (!ArticleViewer.this.blocks.isEmpty()) {
                                        if (tL_error == null) {
                                            TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) tLObject;
                                            if (tL_contacts_resolvedPeer.chats.isEmpty()) {
                                                blockChannelCell.setState(4, false);
                                            } else {
                                                MessagesController.getInstance(chat).putUsers(tL_contacts_resolvedPeer.users, false);
                                                MessagesController.getInstance(chat).putChats(tL_contacts_resolvedPeer.chats, false);
                                                MessagesStorage.getInstance(chat).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, false, true);
                                                ArticleViewer.this.loadedChannel = (Chat) tL_contacts_resolvedPeer.chats.get(0);
                                                if (!ArticleViewer.this.loadedChannel.left || ArticleViewer.this.loadedChannel.kicked) {
                                                    blockChannelCell.setState(4, false);
                                                } else {
                                                    blockChannelCell.setState(0, false);
                                                }
                                            }
                                        } else {
                                            blockChannelCell.setState(4, false);
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private void joinChannel(BlockChannelCell blockChannelCell, Chat chat) {
        TLObject tL_channels_joinChannel = new TL_channels_joinChannel();
        tL_channels_joinChannel.channel = MessagesController.getInputChannel(chat);
        final int i = UserConfig.selectedAccount;
        final BlockChannelCell blockChannelCell2 = blockChannelCell;
        final TLObject tLObject = tL_channels_joinChannel;
        final Chat chat2 = chat;
        ConnectionsManager.getInstance(i).sendRequest(tL_channels_joinChannel, new RequestDelegate() {

            /* renamed from: org.telegram.ui.ArticleViewer$37$2 */
            class C08182 implements Runnable {
                C08182() {
                }

                public void run() {
                    blockChannelCell2.setState(2, false);
                }
            }

            /* renamed from: org.telegram.ui.ArticleViewer$37$3 */
            class C08193 implements Runnable {
                C08193() {
                }

                public void run() {
                    MessagesController.getInstance(i).loadFullChat(chat2.id, 0, true);
                }
            }

            public void run(TLObject tLObject, final TL_error tL_error) {
                if (tL_error != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            blockChannelCell2.setState(0, false);
                            AlertsCreator.processError(i, tL_error, ArticleViewer.this.parentFragment, tLObject, Boolean.valueOf(true));
                        }
                    });
                    return;
                }
                boolean z;
                Updates updates = (Updates) tLObject;
                for (int i = 0; i < updates.updates.size(); i++) {
                    Update update = (Update) updates.updates.get(i);
                    if ((update instanceof TL_updateNewChannelMessage) && (((TL_updateNewChannelMessage) update).message.action instanceof TL_messageActionChatAddUser)) {
                        z = true;
                        break;
                    }
                }
                z = null;
                MessagesController.getInstance(i).processUpdates(updates, false);
                if (!z) {
                    MessagesController.getInstance(i).generateJoinMessage(chat2.id, true);
                }
                AndroidUtilities.runOnUIThread(new C08182());
                AndroidUtilities.runOnUIThread(new C08193(), 1000);
                MessagesStorage.getInstance(i).updateDialogsWithDeletedMessages(new ArrayList(), null, true, chat2.id);
            }
        });
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
        if (this.parentActivity != null) {
            if (this.windowView != null) {
                releasePlayer();
                try {
                    if (this.windowView.getParent() != null) {
                        ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                    }
                    this.windowView = null;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                for (int i = 0; i < this.createdWebViews.size(); i++) {
                    ((BlockEmbedCell) this.createdWebViews.get(i)).destroyWebView(true);
                }
                this.createdWebViews.clear();
                try {
                    this.parentActivity.getWindow().clearFlags(128);
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
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
                FileLog.m3e(e);
            }
            try {
                this.visibleDialog = dialog;
                this.visibleDialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialogInterface) {
                        ArticleViewer.this.showActionBar(120);
                        ArticleViewer.this.visibleDialog = null;
                    }
                });
                dialog.show();
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
        }
    }

    private void onSharePressed() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r5 = this;
        r0 = r5.parentActivity;
        if (r0 == 0) goto L_0x00a1;
    L_0x0004:
        r0 = r5.currentMedia;
        if (r0 != 0) goto L_0x000a;
    L_0x0008:
        goto L_0x00a1;
    L_0x000a:
        r0 = r5.currentIndex;	 Catch:{ Exception -> 0x009c }
        r0 = r5.getMediaFile(r0);	 Catch:{ Exception -> 0x009c }
        if (r0 == 0) goto L_0x0068;	 Catch:{ Exception -> 0x009c }
    L_0x0012:
        r1 = r0.exists();	 Catch:{ Exception -> 0x009c }
        if (r1 == 0) goto L_0x0068;	 Catch:{ Exception -> 0x009c }
    L_0x0018:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x009c }
        r2 = "android.intent.action.SEND";	 Catch:{ Exception -> 0x009c }
        r1.<init>(r2);	 Catch:{ Exception -> 0x009c }
        r2 = r5.currentIndex;	 Catch:{ Exception -> 0x009c }
        r2 = r5.getMediaMime(r2);	 Catch:{ Exception -> 0x009c }
        r1.setType(r2);	 Catch:{ Exception -> 0x009c }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x009c }
        r3 = 24;
        if (r2 < r3) goto L_0x004a;
    L_0x002e:
        r2 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x0040 }
        r3 = r5.parentActivity;	 Catch:{ Exception -> 0x0040 }
        r4 = "org.telegram.messenger.provider";	 Catch:{ Exception -> 0x0040 }
        r3 = android.support.v4.content.FileProvider.getUriForFile(r3, r4, r0);	 Catch:{ Exception -> 0x0040 }
        r1.putExtra(r2, r3);	 Catch:{ Exception -> 0x0040 }
        r2 = 1;	 Catch:{ Exception -> 0x0040 }
        r1.setFlags(r2);	 Catch:{ Exception -> 0x0040 }
        goto L_0x0053;
    L_0x0040:
        r2 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x009c }
        r0 = android.net.Uri.fromFile(r0);	 Catch:{ Exception -> 0x009c }
        r1.putExtra(r2, r0);	 Catch:{ Exception -> 0x009c }
        goto L_0x0053;	 Catch:{ Exception -> 0x009c }
    L_0x004a:
        r2 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x009c }
        r0 = android.net.Uri.fromFile(r0);	 Catch:{ Exception -> 0x009c }
        r1.putExtra(r2, r0);	 Catch:{ Exception -> 0x009c }
    L_0x0053:
        r0 = r5.parentActivity;	 Catch:{ Exception -> 0x009c }
        r2 = "ShareFile";	 Catch:{ Exception -> 0x009c }
        r3 = NUM; // 0x7f0c05ef float:1.8612273E38 double:1.053098149E-314;	 Catch:{ Exception -> 0x009c }
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);	 Catch:{ Exception -> 0x009c }
        r1 = android.content.Intent.createChooser(r1, r2);	 Catch:{ Exception -> 0x009c }
        r2 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;	 Catch:{ Exception -> 0x009c }
        r0.startActivityForResult(r1, r2);	 Catch:{ Exception -> 0x009c }
        goto L_0x00a0;	 Catch:{ Exception -> 0x009c }
    L_0x0068:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;	 Catch:{ Exception -> 0x009c }
        r1 = r5.parentActivity;	 Catch:{ Exception -> 0x009c }
        r0.<init>(r1);	 Catch:{ Exception -> 0x009c }
        r1 = "AppName";	 Catch:{ Exception -> 0x009c }
        r2 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;	 Catch:{ Exception -> 0x009c }
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);	 Catch:{ Exception -> 0x009c }
        r0.setTitle(r1);	 Catch:{ Exception -> 0x009c }
        r1 = "OK";	 Catch:{ Exception -> 0x009c }
        r2 = NUM; // 0x7f0c048c float:1.8611553E38 double:1.0530979736E-314;	 Catch:{ Exception -> 0x009c }
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);	 Catch:{ Exception -> 0x009c }
        r2 = 0;	 Catch:{ Exception -> 0x009c }
        r0.setPositiveButton(r1, r2);	 Catch:{ Exception -> 0x009c }
        r1 = "PleaseDownload";	 Catch:{ Exception -> 0x009c }
        r2 = NUM; // 0x7f0c051d float:1.8611847E38 double:1.053098045E-314;	 Catch:{ Exception -> 0x009c }
        r1 = org.telegram.messenger.LocaleController.getString(r1, r2);	 Catch:{ Exception -> 0x009c }
        r0.setMessage(r1);	 Catch:{ Exception -> 0x009c }
        r0 = r0.create();	 Catch:{ Exception -> 0x009c }
        r5.showDialog(r0);	 Catch:{ Exception -> 0x009c }
        goto L_0x00a0;
    L_0x009c:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
    L_0x00a0:
        return;
    L_0x00a1:
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
        CharSequence format;
        if (this.videoPlayer == null) {
            format = String.format("%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0)});
        } else {
            long currentPosition = this.videoPlayer.getCurrentPosition() / 1000;
            if (this.videoPlayer.getDuration() / 1000 == C0542C.TIME_UNSET || currentPosition == C0542C.TIME_UNSET) {
                format = String.format("%02d:%02d / %02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0)});
            } else {
                format = String.format("%02d:%02d / %02d:%02d", new Object[]{Long.valueOf(currentPosition / 60), Long.valueOf(currentPosition % 60), Long.valueOf(r10 / 60), Long.valueOf(r10 % 60)});
            }
        }
        if (!TextUtils.equals(this.videoPlayerTime.getText(), format)) {
            this.videoPlayerTime.setText(format);
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
            this.videoPlayButton.setImageResource(C0446R.drawable.inline_video_play);
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
                        if (ArticleViewer.this.videoPlayer) {
                            if (i == 4 || i == 1) {
                                try {
                                    ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            } else {
                                try {
                                    ArticleViewer.this.parentActivity.getWindow().addFlags(128);
                                } catch (Throwable e2) {
                                    FileLog.m3e(e2);
                                }
                            }
                            if (i == true && ArticleViewer.this.aspectRatioFrameLayout.getVisibility()) {
                                ArticleViewer.this.aspectRatioFrameLayout.setVisibility(0);
                            }
                            if (!ArticleViewer.this.videoPlayer.isPlaying() || i == 4) {
                                if (ArticleViewer.this.isPlaying) {
                                    ArticleViewer.this.isPlaying = false;
                                    ArticleViewer.this.videoPlayButton.setImageResource(C0446R.drawable.inline_video_play);
                                    AndroidUtilities.cancelRunOnUIThread(ArticleViewer.this.updateProgressRunnable);
                                    if (i == 4 && !ArticleViewer.this.videoPlayerSeekbar.isDragging()) {
                                        ArticleViewer.this.videoPlayerSeekbar.setProgress(0);
                                        ArticleViewer.this.videoPlayerControlFrameLayout.invalidate();
                                        ArticleViewer.this.videoPlayer.seekTo(0);
                                        ArticleViewer.this.videoPlayer.pause();
                                    }
                                }
                            } else if (!ArticleViewer.this.isPlaying) {
                                ArticleViewer.this.isPlaying = true;
                                ArticleViewer.this.videoPlayButton.setImageResource(C0446R.drawable.inline_video_pause);
                                AndroidUtilities.runOnUIThread(ArticleViewer.this.updateProgressRunnable);
                            }
                            ArticleViewer.this.updateVideoPlayerTime();
                        }
                    }

                    public void onError(Exception exception) {
                        FileLog.m3e((Throwable) exception);
                    }

                    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                        if (ArticleViewer.this.aspectRatioFrameLayout != null) {
                            if (i3 != 90) {
                                if (i3 != 270) {
                                    int i4 = i2;
                                    i2 = i;
                                    i = i4;
                                }
                            }
                            ArticleViewer.this.aspectRatioFrameLayout.setAspectRatio(i == 0 ? NUM : (((float) i2) * f) / ((float) i), i3);
                        }
                    }

                    public void onRenderedFirstFrame() {
                        if (!ArticleViewer.this.textureUploaded) {
                            ArticleViewer.this.textureUploaded = true;
                            ArticleViewer.this.containerView.invalidate();
                        }
                    }
                });
                long j = 0;
                if (this.videoPlayer != null) {
                    long duration = this.videoPlayer.getDuration();
                    if (duration != C0542C.TIME_UNSET) {
                        j = duration;
                    }
                }
                j /= 1000;
                TextPaint paint = this.videoPlayerTime.getPaint();
                r1 = new Object[4];
                long j2 = j / 60;
                r1[0] = Long.valueOf(j2);
                j %= 60;
                r1[1] = Long.valueOf(j);
                r1[2] = Long.valueOf(j2);
                r1[3] = Long.valueOf(j);
                Math.ceil((double) paint.measureText(String.format("%02d:%02d / %02d:%02d", r1)));
            }
            this.videoPlayer.preparePlayer(Uri.fromFile(file), "other");
            this.bottomLayout.setVisibility(0);
            this.videoPlayer.setPlayWhenReady(z);
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
            FileLog.m3e(e);
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
            this.videoPlayButton.setImageResource(C0446R.drawable.inline_video_play);
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
        float f = 0.0f;
        if (z2) {
            z2 = new ArrayList();
            ActionBar actionBar = this.actionBar;
            String str = "alpha";
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            z2.add(ObjectAnimator.ofFloat(actionBar, str, fArr));
            FrameLayout frameLayout = this.bottomLayout;
            str = "alpha";
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            z2.add(ObjectAnimator.ofFloat(frameLayout, str, fArr));
            if (this.captionTextView.getTag() != null) {
                TextView textView = this.captionTextView;
                str = "alpha";
                float[] fArr2 = new float[1];
                if (z) {
                    f = 1.0f;
                }
                fArr2[0] = f;
                z2.add(ObjectAnimator.ofFloat(textView, str, fArr2));
            }
            this.currentActionBarAnimation = new AnimatorSet();
            this.currentActionBarAnimation.playTogether(z2);
            if (!z) {
                this.currentActionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ArticleViewer.this.currentActionBarAnimation != null && ArticleViewer.this.currentActionBarAnimation.equals(animator) != null) {
                            ArticleViewer.this.actionBar.setVisibility(8);
                            if (ArticleViewer.this.videoPlayer != null) {
                                ArticleViewer.this.bottomLayout.setVisibility(8);
                            }
                            if (ArticleViewer.this.captionTextView.getTag() != null) {
                                ArticleViewer.this.captionTextView.setVisibility(4);
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
        if (this.captionTextView.getTag()) {
            z2 = this.captionTextView;
            if (z) {
                f = 1.0f;
            }
            z2.setAlpha(f);
        }
        if (!z) {
            this.actionBar.setVisibility(8);
            if (this.videoPlayer) {
                this.bottomLayout.setVisibility(8);
            }
            if (this.captionTextView.getTag()) {
                this.captionTextView.setVisibility(true);
            }
        }
    }

    private String getFileName(int i) {
        i = getMedia(i);
        if (i instanceof Photo) {
            i = FileLoader.getClosestPhotoSizeWithSize(((Photo) i).sizes, AndroidUtilities.getPhotoSize());
        }
        return FileLoader.getAttachFileName(i);
    }

    private TLObject getMedia(int i) {
        if (!this.imagesArr.isEmpty() && i < this.imagesArr.size()) {
            if (i >= 0) {
                PageBlock pageBlock = (PageBlock) this.imagesArr.get(i);
                if (pageBlock.photo_id != 0) {
                    return getPhotoWithId(pageBlock.photo_id);
                }
                if (pageBlock.video_id != 0) {
                    return getDocumentWithId(pageBlock.video_id);
                }
                return null;
            }
        }
        return null;
    }

    private File getMediaFile(int i) {
        if (!this.imagesArr.isEmpty() && i < this.imagesArr.size()) {
            if (i >= 0) {
                PageBlock pageBlock = (PageBlock) this.imagesArr.get(i);
                if (pageBlock.photo_id != 0) {
                    i = getPhotoWithId(pageBlock.photo_id);
                    if (i != 0) {
                        i = FileLoader.getClosestPhotoSizeWithSize(i.sizes, AndroidUtilities.getPhotoSize());
                        if (i != 0) {
                            return FileLoader.getPathToAttach(i, true);
                        }
                    }
                } else if (pageBlock.video_id != 0) {
                    i = getDocumentWithId(pageBlock.video_id);
                    if (i != 0) {
                        return FileLoader.getPathToAttach(i, true);
                    }
                }
                return null;
            }
        }
        return null;
    }

    private boolean isVideoBlock(PageBlock pageBlock) {
        if (!(pageBlock == null || pageBlock.video_id == 0)) {
            pageBlock = getDocumentWithId(pageBlock.video_id);
            if (pageBlock != null) {
                return MessageObject.isVideoDocument(pageBlock);
            }
        }
        return null;
    }

    private boolean isMediaVideo(int i) {
        return !this.imagesArr.isEmpty() && i < this.imagesArr.size() && i >= 0 && isVideoBlock((PageBlock) this.imagesArr.get(i)) != 0;
    }

    private String getMediaMime(int i) {
        if (i < this.imagesArr.size()) {
            if (i >= 0) {
                PageBlock pageBlock = (PageBlock) this.imagesArr.get(i);
                if (pageBlock instanceof TL_pageBlockVideo) {
                    i = getDocumentWithId(pageBlock.video_id);
                    if (i != 0) {
                        return i.mime_type;
                    }
                }
                return "image/jpeg";
            }
        }
        return "image/jpeg";
    }

    private FileLocation getFileLocation(int i, int[] iArr) {
        if (i >= 0) {
            if (i < this.imagesArr.size()) {
                i = getMedia(i);
                if (i instanceof Photo) {
                    i = FileLoader.getClosestPhotoSizeWithSize(((Photo) i).sizes, AndroidUtilities.getPhotoSize());
                    if (i != 0) {
                        iArr[0] = i.size;
                        if (iArr[0] == 0) {
                            iArr[0] = -1;
                        }
                        return i.location;
                    }
                    iArr[0] = -1;
                } else if (i instanceof Document) {
                    Document document = (Document) i;
                    if (document.thumb != null) {
                        iArr[0] = document.thumb.size;
                        if (iArr[0] == 0) {
                            iArr[0] = -1;
                        }
                        return document.thumb.location;
                    }
                }
                return null;
            }
        }
        return null;
    }

    private void onPhotoShow(int i, PlaceProviderObject placeProviderObject) {
        this.currentIndex = -1;
        this.currentFileNames[0] = null;
        this.currentFileNames[1] = null;
        this.currentFileNames[2] = null;
        if (this.currentThumb != null) {
            this.currentThumb.release();
        }
        this.currentThumb = placeProviderObject != null ? placeProviderObject.thumb : null;
        this.menuItem.setVisibility(0);
        this.menuItem.hideSubItem(3);
        this.actionBar.setTranslationY(0.0f);
        this.captionTextView.setTag(null);
        this.captionTextView.setVisibility(4);
        for (placeProviderObject = null; placeProviderObject < 3; placeProviderObject++) {
            if (this.radialProgressViews[placeProviderObject] != null) {
                this.radialProgressViews[placeProviderObject].setBackgroundState(-1, false);
            }
        }
        setImageIndex(i, true);
        if (this.currentMedia != 0 && isMediaVideo(this.currentIndex) != 0) {
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

    private void setImageIndex(int i, boolean z) {
        if (this.currentIndex != i) {
            boolean z2;
            if (!z && this.currentThumb) {
                this.currentThumb.release();
                this.currentThumb = null;
            }
            this.currentFileNames[0] = getFileName(i);
            this.currentFileNames[1] = getFileName(i + 1);
            this.currentFileNames[2] = getFileName(i - 1);
            z = this.currentIndex;
            this.currentIndex = i;
            if (this.imagesArr.isEmpty() == 0) {
                if (this.currentIndex >= 0) {
                    if (this.currentIndex < this.imagesArr.size()) {
                        PageBlock pageBlock = (PageBlock) this.imagesArr.get(this.currentIndex);
                        z2 = this.currentMedia != null && this.currentMedia == pageBlock;
                        this.currentMedia = pageBlock;
                        i = isMediaVideo(this.currentIndex);
                        if (i != 0) {
                            this.menuItem.showSubItem(3);
                        }
                        setCurrentCaption(getText(this.currentMedia.caption, this.currentMedia.caption, this.currentMedia));
                        if (this.currentAnimation != null) {
                            this.menuItem.setVisibility(8);
                            this.menuItem.hideSubItem(1);
                            this.actionBar.setTitle(LocaleController.getString("AttachGif", C0446R.string.AttachGif));
                        } else {
                            this.menuItem.setVisibility(0);
                            if (this.imagesArr.size() != 1) {
                                this.actionBar.setTitle(LocaleController.formatString("Of", C0446R.string.Of, Integer.valueOf(this.currentIndex + 1), Integer.valueOf(this.imagesArr.size())));
                            } else if (i != 0) {
                                this.actionBar.setTitle(LocaleController.getString("AttachVideo", C0446R.string.AttachVideo));
                            } else {
                                this.actionBar.setTitle(LocaleController.getString("AttachPhoto", C0446R.string.AttachPhoto));
                            }
                            this.menuItem.showSubItem(1);
                        }
                    }
                }
                closePhoto(false);
                return;
            }
            i = 0;
            z2 = i;
            int childCount = this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof BlockSlideshowCell) {
                    BlockSlideshowCell blockSlideshowCell = (BlockSlideshowCell) childAt;
                    int indexOf = blockSlideshowCell.currentBlock.items.indexOf(this.currentMedia);
                    if (indexOf != -1) {
                        blockSlideshowCell.innerListView.setCurrentItem(indexOf, false);
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
            if (!z2) {
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
                i = (this.currentFileNames[0] == null || i != 0 || this.radialProgressViews[0].backgroundState == 0) ? 0 : 1;
                this.canZoom = i;
                updateMinMax(this.scale);
            }
            if (z) {
                setImages();
                for (i = 0; i < 3; i++) {
                    checkProgress(i, false);
                }
            } else {
                checkProgress(0, false);
                if (z > this.currentIndex) {
                    i = this.rightImage;
                    this.rightImage = this.centerImage;
                    this.centerImage = this.leftImage;
                    this.leftImage = i;
                    i = this.radialProgressViews[0];
                    this.radialProgressViews[0] = this.radialProgressViews[2];
                    this.radialProgressViews[2] = i;
                    setIndexToImage(this.leftImage, this.currentIndex - true);
                    checkProgress(1, false);
                    checkProgress(2, false);
                } else if (z < this.currentIndex) {
                    i = this.leftImage;
                    this.leftImage = this.centerImage;
                    this.centerImage = this.rightImage;
                    this.rightImage = i;
                    i = this.radialProgressViews[0];
                    this.radialProgressViews[0] = this.radialProgressViews[1];
                    this.radialProgressViews[1] = i;
                    setIndexToImage(this.rightImage, this.currentIndex + true);
                    checkProgress(1, false);
                    checkProgress(2, false);
                }
            }
        }
    }

    private void setCurrentCaption(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            this.captionTextView.setTextColor(-1);
            this.captionTextView.setTag(null);
            this.captionTextView.setVisibility(4);
            return;
        }
        this.captionTextView = this.captionTextViewOld;
        this.captionTextViewOld = this.captionTextViewNew;
        this.captionTextViewNew = this.captionTextView;
        Theme.createChatResources(null, true);
        charSequence = Emoji.replaceEmoji(new SpannableStringBuilder(charSequence.toString()), this.captionTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        this.captionTextView.setTag(charSequence);
        this.captionTextView.setText(charSequence);
        this.captionTextView.setTextColor(-1);
        this.captionTextView.setAlpha(this.actionBar.getVisibility() == 0 ? 1.0f : 0.0f);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                ArticleViewer.this.captionTextViewOld.setTag(null);
                int i = 4;
                ArticleViewer.this.captionTextViewOld.setVisibility(4);
                TextView access$14400 = ArticleViewer.this.captionTextViewNew;
                if (ArticleViewer.this.actionBar.getVisibility() == 0) {
                    i = 0;
                }
                access$14400.setVisibility(i);
            }
        });
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
                z = ImageLoader.getInstance().getFileProgress(this.currentFileNames[i]);
                if (!z) {
                    z = Float.valueOf(false);
                }
                this.radialProgressViews[i].setProgress(z.floatValue(), false);
            } else if (isMediaVideo) {
                this.radialProgressViews[i].setBackgroundState(3, z);
            } else {
                this.radialProgressViews[i].setBackgroundState(-1, z);
            }
            if (i == 0) {
                if (this.currentFileNames[0] == 0 || isMediaVideo || this.radialProgressViews[0].backgroundState == 0) {
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
        imageReceiver.setOrientation(0, false);
        int[] iArr = new int[1];
        TLObject fileLocation = getFileLocation(i, iArr);
        if (fileLocation != null) {
            TLObject media = getMedia(i);
            if (media instanceof Photo) {
                Photo photo = (Photo) media;
                i = (this.currentThumb == 0 || imageReceiver != this.centerImage) ? 0 : this.currentThumb;
                if (iArr[0] == 0) {
                    iArr[0] = -1;
                }
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80);
                imageReceiver.setImage(fileLocation, null, null, i != 0 ? new BitmapDrawable(i.bitmap) : null, closestPhotoSizeWithSize != null ? closestPhotoSizeWithSize.location : 0, "b", iArr[0], null, 1);
            } else if (isMediaVideo(i) != 0) {
                if ((fileLocation instanceof TL_fileLocationUnavailable) == 0) {
                    i = (this.currentThumb == 0 || imageReceiver != this.centerImage) ? 0 : this.currentThumb;
                    imageReceiver.setImage(null, null, null, i != 0 ? new BitmapDrawable(i.bitmap) : null, fileLocation, "b", 0, null, 1);
                    return;
                }
                imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(C0446R.drawable.photoview_placeholder));
            } else if (this.currentAnimation != 0) {
                imageReceiver.setImageBitmap(this.currentAnimation);
                this.currentAnimation.setSecondParentView(this.photoContainerView);
            }
        } else if (iArr[0] == 0) {
            imageReceiver.setImageBitmap((Bitmap) null);
        } else {
            imageReceiver.setImageBitmap(this.parentActivity.getResources().getDrawable(C0446R.drawable.photoview_placeholder));
        }
    }

    public boolean isShowingImage(PageBlock pageBlock) {
        return (!this.isPhotoVisible || this.disableShowCheck || pageBlock == null || this.currentMedia != pageBlock) ? null : true;
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

    public boolean openPhoto(PageBlock pageBlock) {
        PageBlock pageBlock2 = pageBlock;
        if (!(this.parentActivity == null || r0.isPhotoVisible || checkPhotoAnimation())) {
            if (pageBlock2 != null) {
                final PlaceProviderObject placeForPhoto = getPlaceForPhoto(pageBlock);
                if (placeForPhoto == null) {
                    return false;
                }
                Rect drawRegion;
                int animatedOrientation;
                ViewGroup.LayoutParams layoutParams;
                float f;
                float f2;
                float f3;
                float f4;
                float f5;
                int abs;
                int abs2;
                int[] iArr;
                int i;
                int height;
                final AnimatorSet animatorSet;
                NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.FileDidFailedLoad);
                NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.FileDidLoaded);
                NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.FileLoadProgressChanged);
                NotificationCenter.getGlobalInstance().addObserver(r0, NotificationCenter.emojiDidLoaded);
                if (r0.velocityTracker == null) {
                    r0.velocityTracker = VelocityTracker.obtain();
                }
                r0.isPhotoVisible = true;
                toggleActionBar(true, false);
                r0.actionBar.setAlpha(0.0f);
                r0.bottomLayout.setAlpha(0.0f);
                r0.captionTextView.setAlpha(0.0f);
                r0.photoBackgroundDrawable.setAlpha(0);
                r0.disableShowCheck = true;
                r0.photoAnimationInProgress = 1;
                if (pageBlock2 != null) {
                    r0.currentAnimation = placeForPhoto.imageReceiver.getAnimation();
                }
                int indexOf = r0.photoBlocks.indexOf(pageBlock2);
                r0.imagesArr.clear();
                if (pageBlock2 instanceof TL_pageBlockVideo) {
                    if (!isVideoBlock(pageBlock)) {
                        r0.imagesArr.add(pageBlock2);
                        indexOf = 0;
                        onPhotoShow(indexOf, placeForPhoto);
                        drawRegion = placeForPhoto.imageReceiver.getDrawRegion();
                        indexOf = placeForPhoto.imageReceiver.getOrientation();
                        animatedOrientation = placeForPhoto.imageReceiver.getAnimatedOrientation();
                        if (animatedOrientation != 0) {
                            indexOf = animatedOrientation;
                        }
                        r0.animatingImageView.setVisibility(0);
                        r0.animatingImageView.setRadius(placeForPhoto.radius);
                        r0.animatingImageView.setOrientation(indexOf);
                        r0.animatingImageView.setNeedRadius(placeForPhoto.radius == 0);
                        r0.animatingImageView.setImageBitmap(placeForPhoto.thumb);
                        r0.animatingImageView.setAlpha(1.0f);
                        r0.animatingImageView.setPivotX(0.0f);
                        r0.animatingImageView.setPivotY(0.0f);
                        r0.animatingImageView.setScaleX(placeForPhoto.scale);
                        r0.animatingImageView.setScaleY(placeForPhoto.scale);
                        r0.animatingImageView.setTranslationX(((float) placeForPhoto.viewX) + (((float) drawRegion.left) * placeForPhoto.scale));
                        r0.animatingImageView.setTranslationY(((float) placeForPhoto.viewY) + (((float) drawRegion.top) * placeForPhoto.scale));
                        layoutParams = r0.animatingImageView.getLayoutParams();
                        layoutParams.width = drawRegion.right - drawRegion.left;
                        layoutParams.height = drawRegion.bottom - drawRegion.top;
                        r0.animatingImageView.setLayoutParams(layoutParams);
                        f = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
                        f2 = ((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) / ((float) layoutParams.height);
                        if (f > f2) {
                            f = f2;
                        }
                        f3 = ((float) layoutParams.height) * f;
                        f4 = (((float) AndroidUtilities.displaySize.x) - (((float) layoutParams.width) * f)) / 2.0f;
                        if (VERSION.SDK_INT >= 21 && r0.lastInsets != null) {
                            f4 += (float) ((WindowInsets) r0.lastInsets).getSystemWindowInsetLeft();
                        }
                        f5 = (((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) - f3) / 2.0f;
                        abs = Math.abs(drawRegion.left - placeForPhoto.imageReceiver.getImageX());
                        abs2 = Math.abs(drawRegion.top - placeForPhoto.imageReceiver.getImageY());
                        iArr = new int[2];
                        placeForPhoto.parentView.getLocationInWindow(iArr);
                        i = placeForPhoto.clipTopAddition + (iArr[1] - (placeForPhoto.viewY + drawRegion.top));
                        if (i < 0) {
                            i = 0;
                        }
                        height = placeForPhoto.clipBottomAddition + (((placeForPhoto.viewY + drawRegion.top) + layoutParams.height) - (iArr[1] + placeForPhoto.parentView.getHeight()));
                        if (height < 0) {
                            height = 0;
                        }
                        indexOf = Math.max(i, abs2);
                        height = Math.max(height, abs2);
                        r0.animationValues[0][0] = r0.animatingImageView.getScaleX();
                        r0.animationValues[0][1] = r0.animatingImageView.getScaleY();
                        r0.animationValues[0][2] = r0.animatingImageView.getTranslationX();
                        r0.animationValues[0][3] = r0.animatingImageView.getTranslationY();
                        r0.animationValues[0][4] = ((float) abs) * placeForPhoto.scale;
                        r0.animationValues[0][5] = ((float) indexOf) * placeForPhoto.scale;
                        r0.animationValues[0][6] = ((float) height) * placeForPhoto.scale;
                        r0.animationValues[0][7] = (float) r0.animatingImageView.getRadius();
                        r0.animationValues[1][0] = f;
                        r0.animationValues[1][1] = f;
                        r0.animationValues[1][2] = f4;
                        r0.animationValues[1][3] = f5;
                        r0.animationValues[1][4] = 0.0f;
                        r0.animationValues[1][5] = 0.0f;
                        r0.animationValues[1][6] = 0.0f;
                        r0.animationValues[1][7] = 0.0f;
                        r0.photoContainerView.setVisibility(0);
                        r0.photoContainerBackground.setVisibility(0);
                        r0.animatingImageView.setAnimationProgress(0.0f);
                        animatorSet = new AnimatorSet();
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(r0.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f}), ObjectAnimator.ofInt(r0.photoBackgroundDrawable, "alpha", new int[]{0, 255}), ObjectAnimator.ofFloat(r0.actionBar, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(r0.bottomLayout, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(r0.captionTextView, "alpha", new float[]{0.0f, 1.0f})});
                        r0.photoAnimationEndRunnable = new Runnable() {
                            public void run() {
                                if (ArticleViewer.this.photoContainerView != null) {
                                    if (VERSION.SDK_INT >= 18) {
                                        ArticleViewer.this.photoContainerView.setLayerType(0, null);
                                    }
                                    ArticleViewer.this.photoAnimationInProgress = 0;
                                    ArticleViewer.this.photoTransitionAnimationStartTime = 0;
                                    ArticleViewer.this.setImages();
                                    ArticleViewer.this.photoContainerView.invalidate();
                                    ArticleViewer.this.animatingImageView.setVisibility(8);
                                    if (ArticleViewer.this.showAfterAnimation != null) {
                                        ArticleViewer.this.showAfterAnimation.imageReceiver.setVisible(true, true);
                                    }
                                    if (ArticleViewer.this.hideAfterAnimation != null) {
                                        ArticleViewer.this.hideAfterAnimation.imageReceiver.setVisible(false, true);
                                    }
                                }
                            }
                        };
                        animatorSet.setDuration(200);
                        animatorSet.addListener(new AnimatorListenerAdapter() {

                            /* renamed from: org.telegram.ui.ArticleViewer$44$1 */
                            class C08211 implements Runnable {
                                C08211() {
                                }

                                public void run() {
                                    NotificationCenter.getInstance(ArticleViewer.this.currentAccount).setAnimationInProgress(false);
                                    if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                                        ArticleViewer.this.photoAnimationEndRunnable.run();
                                        ArticleViewer.this.photoAnimationEndRunnable = null;
                                    }
                                }
                            }

                            public void onAnimationEnd(Animator animator) {
                                AndroidUtilities.runOnUIThread(new C08211());
                            }
                        });
                        r0.photoTransitionAnimationStartTime = System.currentTimeMillis();
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(ArticleViewer.this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats});
                                NotificationCenter.getInstance(ArticleViewer.this.currentAccount).setAnimationInProgress(true);
                                animatorSet.start();
                            }
                        });
                        if (VERSION.SDK_INT >= 18) {
                            r0.photoContainerView.setLayerType(2, null);
                        }
                        r0.photoBackgroundDrawable.drawRunnable = new Runnable() {
                            public void run() {
                                ArticleViewer.this.disableShowCheck = false;
                                placeForPhoto.imageReceiver.setVisible(false, true);
                            }
                        };
                        return true;
                    }
                }
                r0.imagesArr.addAll(r0.photoBlocks);
                onPhotoShow(indexOf, placeForPhoto);
                drawRegion = placeForPhoto.imageReceiver.getDrawRegion();
                indexOf = placeForPhoto.imageReceiver.getOrientation();
                animatedOrientation = placeForPhoto.imageReceiver.getAnimatedOrientation();
                if (animatedOrientation != 0) {
                    indexOf = animatedOrientation;
                }
                r0.animatingImageView.setVisibility(0);
                r0.animatingImageView.setRadius(placeForPhoto.radius);
                r0.animatingImageView.setOrientation(indexOf);
                if (placeForPhoto.radius == 0) {
                }
                r0.animatingImageView.setNeedRadius(placeForPhoto.radius == 0);
                r0.animatingImageView.setImageBitmap(placeForPhoto.thumb);
                r0.animatingImageView.setAlpha(1.0f);
                r0.animatingImageView.setPivotX(0.0f);
                r0.animatingImageView.setPivotY(0.0f);
                r0.animatingImageView.setScaleX(placeForPhoto.scale);
                r0.animatingImageView.setScaleY(placeForPhoto.scale);
                r0.animatingImageView.setTranslationX(((float) placeForPhoto.viewX) + (((float) drawRegion.left) * placeForPhoto.scale));
                r0.animatingImageView.setTranslationY(((float) placeForPhoto.viewY) + (((float) drawRegion.top) * placeForPhoto.scale));
                layoutParams = r0.animatingImageView.getLayoutParams();
                layoutParams.width = drawRegion.right - drawRegion.left;
                layoutParams.height = drawRegion.bottom - drawRegion.top;
                r0.animatingImageView.setLayoutParams(layoutParams);
                f = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
                f2 = ((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) / ((float) layoutParams.height);
                if (f > f2) {
                    f = f2;
                }
                f3 = ((float) layoutParams.height) * f;
                f4 = (((float) AndroidUtilities.displaySize.x) - (((float) layoutParams.width) * f)) / 2.0f;
                f4 += (float) ((WindowInsets) r0.lastInsets).getSystemWindowInsetLeft();
                f5 = (((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) - f3) / 2.0f;
                abs = Math.abs(drawRegion.left - placeForPhoto.imageReceiver.getImageX());
                abs2 = Math.abs(drawRegion.top - placeForPhoto.imageReceiver.getImageY());
                iArr = new int[2];
                placeForPhoto.parentView.getLocationInWindow(iArr);
                i = placeForPhoto.clipTopAddition + (iArr[1] - (placeForPhoto.viewY + drawRegion.top));
                if (i < 0) {
                    i = 0;
                }
                height = placeForPhoto.clipBottomAddition + (((placeForPhoto.viewY + drawRegion.top) + layoutParams.height) - (iArr[1] + placeForPhoto.parentView.getHeight()));
                if (height < 0) {
                    height = 0;
                }
                indexOf = Math.max(i, abs2);
                height = Math.max(height, abs2);
                r0.animationValues[0][0] = r0.animatingImageView.getScaleX();
                r0.animationValues[0][1] = r0.animatingImageView.getScaleY();
                r0.animationValues[0][2] = r0.animatingImageView.getTranslationX();
                r0.animationValues[0][3] = r0.animatingImageView.getTranslationY();
                r0.animationValues[0][4] = ((float) abs) * placeForPhoto.scale;
                r0.animationValues[0][5] = ((float) indexOf) * placeForPhoto.scale;
                r0.animationValues[0][6] = ((float) height) * placeForPhoto.scale;
                r0.animationValues[0][7] = (float) r0.animatingImageView.getRadius();
                r0.animationValues[1][0] = f;
                r0.animationValues[1][1] = f;
                r0.animationValues[1][2] = f4;
                r0.animationValues[1][3] = f5;
                r0.animationValues[1][4] = 0.0f;
                r0.animationValues[1][5] = 0.0f;
                r0.animationValues[1][6] = 0.0f;
                r0.animationValues[1][7] = 0.0f;
                r0.photoContainerView.setVisibility(0);
                r0.photoContainerBackground.setVisibility(0);
                r0.animatingImageView.setAnimationProgress(0.0f);
                animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(r0.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f}), ObjectAnimator.ofInt(r0.photoBackgroundDrawable, "alpha", new int[]{0, 255}), ObjectAnimator.ofFloat(r0.actionBar, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(r0.bottomLayout, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(r0.captionTextView, "alpha", new float[]{0.0f, 1.0f})});
                r0.photoAnimationEndRunnable = /* anonymous class already generated */;
                animatorSet.setDuration(200);
                animatorSet.addListener(/* anonymous class already generated */);
                r0.photoTransitionAnimationStartTime = System.currentTimeMillis();
                AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                if (VERSION.SDK_INT >= 18) {
                    r0.photoContainerView.setLayerType(2, null);
                }
                r0.photoBackgroundDrawable.drawRunnable = /* anonymous class already generated */;
                return true;
            }
        }
        return false;
    }

    public void closePhoto(boolean z) {
        if (this.parentActivity != null && r0.isPhotoVisible) {
            if (!checkPhotoAnimation()) {
                Object obj;
                releasePlayer();
                NotificationCenter.getInstance(r0.currentAccount).removeObserver(r0, NotificationCenter.FileDidFailedLoad);
                NotificationCenter.getInstance(r0.currentAccount).removeObserver(r0, NotificationCenter.FileDidLoaded);
                NotificationCenter.getInstance(r0.currentAccount).removeObserver(r0, NotificationCenter.FileLoadProgressChanged);
                NotificationCenter.getGlobalInstance().removeObserver(r0, NotificationCenter.needSetDayNightTheme);
                NotificationCenter.getGlobalInstance().removeObserver(r0, NotificationCenter.emojiDidLoaded);
                r0.isActionBarVisible = false;
                if (r0.velocityTracker != null) {
                    r0.velocityTracker.recycle();
                    r0.velocityTracker = null;
                }
                final PlaceProviderObject placeForPhoto = getPlaceForPhoto(r0.currentMedia);
                Animator[] animatorArr;
                if (z) {
                    Rect drawRegion;
                    r0.photoAnimationInProgress = 1;
                    r0.animatingImageView.setVisibility(0);
                    r0.photoContainerView.invalidate();
                    AnimatorSet animatorSet = new AnimatorSet();
                    ViewGroup.LayoutParams layoutParams = r0.animatingImageView.getLayoutParams();
                    int orientation = r0.centerImage.getOrientation();
                    int animatedOrientation = (placeForPhoto == null || placeForPhoto.imageReceiver == null) ? 0 : placeForPhoto.imageReceiver.getAnimatedOrientation();
                    if (animatedOrientation != 0) {
                        orientation = animatedOrientation;
                    }
                    r0.animatingImageView.setOrientation(orientation);
                    if (placeForPhoto != null) {
                        r0.animatingImageView.setNeedRadius(placeForPhoto.radius != 0);
                        drawRegion = placeForPhoto.imageReceiver.getDrawRegion();
                        layoutParams.width = drawRegion.right - drawRegion.left;
                        layoutParams.height = drawRegion.bottom - drawRegion.top;
                        r0.animatingImageView.setImageBitmap(placeForPhoto.thumb);
                    } else {
                        r0.animatingImageView.setNeedRadius(false);
                        layoutParams.width = r0.centerImage.getImageWidth();
                        layoutParams.height = r0.centerImage.getImageHeight();
                        r0.animatingImageView.setImageBitmap(r0.centerImage.getBitmapSafe());
                        drawRegion = null;
                    }
                    r0.animatingImageView.setLayoutParams(layoutParams);
                    float f = ((float) AndroidUtilities.displaySize.x) / ((float) layoutParams.width);
                    float f2 = ((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) / ((float) layoutParams.height);
                    if (f > f2) {
                        f = f2;
                    }
                    float f3 = (((float) layoutParams.height) * r0.scale) * f;
                    float f4 = (((float) AndroidUtilities.displaySize.x) - ((((float) layoutParams.width) * r0.scale) * f)) / 2.0f;
                    if (VERSION.SDK_INT >= 21 && r0.lastInsets != null) {
                        f4 += (float) ((WindowInsets) r0.lastInsets).getSystemWindowInsetLeft();
                    }
                    float f5 = (((float) (AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)) - f3) / 2.0f;
                    r0.animatingImageView.setTranslationX(f4 + r0.translationX);
                    r0.animatingImageView.setTranslationY(f5 + r0.translationY);
                    r0.animatingImageView.setScaleX(r0.scale * f);
                    r0.animatingImageView.setScaleY(r0.scale * f);
                    if (placeForPhoto != null) {
                        placeForPhoto.imageReceiver.setVisible(false, true);
                        animatedOrientation = Math.abs(drawRegion.left - placeForPhoto.imageReceiver.getImageX());
                        int abs = Math.abs(drawRegion.top - placeForPhoto.imageReceiver.getImageY());
                        int[] iArr = new int[2];
                        placeForPhoto.parentView.getLocationInWindow(iArr);
                        int i = (iArr[1] - (placeForPhoto.viewY + drawRegion.top)) + placeForPhoto.clipTopAddition;
                        if (i < 0) {
                            i = 0;
                        }
                        int height = placeForPhoto.clipBottomAddition + (((placeForPhoto.viewY + drawRegion.top) + (drawRegion.bottom - drawRegion.top)) - (iArr[1] + placeForPhoto.parentView.getHeight()));
                        if (height < 0) {
                            height = 0;
                        }
                        i = Math.max(i, abs);
                        abs = Math.max(height, abs);
                        r0.animationValues[0][0] = r0.animatingImageView.getScaleX();
                        r0.animationValues[0][1] = r0.animatingImageView.getScaleY();
                        r0.animationValues[0][2] = r0.animatingImageView.getTranslationX();
                        r0.animationValues[0][3] = r0.animatingImageView.getTranslationY();
                        r0.animationValues[0][4] = 0.0f;
                        r0.animationValues[0][5] = 0.0f;
                        r0.animationValues[0][6] = 0.0f;
                        r0.animationValues[0][7] = 0.0f;
                        r0.animationValues[1][0] = placeForPhoto.scale;
                        r0.animationValues[1][1] = placeForPhoto.scale;
                        r0.animationValues[1][2] = ((float) placeForPhoto.viewX) + (((float) drawRegion.left) * placeForPhoto.scale);
                        r0.animationValues[1][3] = ((float) placeForPhoto.viewY) + (((float) drawRegion.top) * placeForPhoto.scale);
                        r0.animationValues[1][4] = ((float) animatedOrientation) * placeForPhoto.scale;
                        r0.animationValues[1][5] = ((float) i) * placeForPhoto.scale;
                        r0.animationValues[1][6] = ((float) abs) * placeForPhoto.scale;
                        r0.animationValues[1][7] = (float) placeForPhoto.radius;
                        Animator[] animatorArr2 = new Animator[5];
                        animatorArr2[0] = ObjectAnimator.ofFloat(r0.animatingImageView, "animationProgress", new float[]{0.0f, 1.0f});
                        animatorArr2[1] = ObjectAnimator.ofInt(r0.photoBackgroundDrawable, "alpha", new int[]{0});
                        animatorArr2[2] = ObjectAnimator.ofFloat(r0.actionBar, "alpha", new float[]{0.0f});
                        animatorArr2[3] = ObjectAnimator.ofFloat(r0.bottomLayout, "alpha", new float[]{0.0f});
                        animatorArr2[4] = ObjectAnimator.ofFloat(r0.captionTextView, "alpha", new float[]{0.0f});
                        animatorSet.playTogether(animatorArr2);
                    } else {
                        orientation = AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight;
                        animatorArr = new Animator[6];
                        animatorArr[0] = ObjectAnimator.ofInt(r0.photoBackgroundDrawable, "alpha", new int[]{0});
                        animatorArr[1] = ObjectAnimator.ofFloat(r0.animatingImageView, "alpha", new float[]{0.0f});
                        ClippingImageView clippingImageView = r0.animatingImageView;
                        String str = "translationY";
                        float[] fArr = new float[1];
                        if (r0.translationY < 0.0f) {
                            orientation = -orientation;
                        }
                        fArr[0] = (float) orientation;
                        animatorArr[2] = ObjectAnimator.ofFloat(clippingImageView, str, fArr);
                        animatorArr[3] = ObjectAnimator.ofFloat(r0.actionBar, "alpha", new float[]{0.0f});
                        animatorArr[4] = ObjectAnimator.ofFloat(r0.bottomLayout, "alpha", new float[]{0.0f});
                        animatorArr[5] = ObjectAnimator.ofFloat(r0.captionTextView, "alpha", new float[]{0.0f});
                        animatorSet.playTogether(animatorArr);
                    }
                    r0.photoAnimationEndRunnable = new Runnable() {
                        public void run() {
                            if (VERSION.SDK_INT >= 18) {
                                ArticleViewer.this.photoContainerView.setLayerType(0, null);
                            }
                            ArticleViewer.this.photoContainerView.setVisibility(4);
                            ArticleViewer.this.photoContainerBackground.setVisibility(4);
                            ArticleViewer.this.photoAnimationInProgress = 0;
                            ArticleViewer.this.onPhotoClosed(placeForPhoto);
                        }
                    };
                    animatorSet.setDuration(200);
                    animatorSet.addListener(new AnimatorListenerAdapter() {

                        /* renamed from: org.telegram.ui.ArticleViewer$48$1 */
                        class C08221 implements Runnable {
                            C08221() {
                            }

                            public void run() {
                                if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                                    ArticleViewer.this.photoAnimationEndRunnable.run();
                                    ArticleViewer.this.photoAnimationEndRunnable = null;
                                }
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
                            AndroidUtilities.runOnUIThread(new C08221());
                        }
                    });
                    r0.photoTransitionAnimationStartTime = System.currentTimeMillis();
                    if (VERSION.SDK_INT >= 18) {
                        r0.photoContainerView.setLayerType(2, null);
                    }
                    animatorSet.start();
                    obj = null;
                } else {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    animatorArr = new Animator[6];
                    animatorArr[0] = ObjectAnimator.ofFloat(r0.photoContainerView, "scaleX", new float[]{0.9f});
                    animatorArr[1] = ObjectAnimator.ofFloat(r0.photoContainerView, "scaleY", new float[]{0.9f});
                    animatorArr[2] = ObjectAnimator.ofInt(r0.photoBackgroundDrawable, "alpha", new int[]{0});
                    animatorArr[3] = ObjectAnimator.ofFloat(r0.actionBar, "alpha", new float[]{0.0f});
                    animatorArr[4] = ObjectAnimator.ofFloat(r0.bottomLayout, "alpha", new float[]{0.0f});
                    animatorArr[5] = ObjectAnimator.ofFloat(r0.captionTextView, "alpha", new float[]{0.0f});
                    animatorSet2.playTogether(animatorArr);
                    r0.photoAnimationInProgress = 2;
                    r0.photoAnimationEndRunnable = new Runnable() {
                        public void run() {
                            if (ArticleViewer.this.photoContainerView != null) {
                                if (VERSION.SDK_INT >= 18) {
                                    ArticleViewer.this.photoContainerView.setLayerType(0, null);
                                }
                                ArticleViewer.this.photoContainerView.setVisibility(4);
                                ArticleViewer.this.photoContainerBackground.setVisibility(4);
                                ArticleViewer.this.photoAnimationInProgress = 0;
                                ArticleViewer.this.onPhotoClosed(placeForPhoto);
                                ArticleViewer.this.photoContainerView.setScaleX(1.0f);
                                ArticleViewer.this.photoContainerView.setScaleY(1.0f);
                            }
                        }
                    };
                    animatorSet2.setDuration(200);
                    animatorSet2.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (ArticleViewer.this.photoAnimationEndRunnable != null) {
                                ArticleViewer.this.photoAnimationEndRunnable.run();
                                ArticleViewer.this.photoAnimationEndRunnable = null;
                            }
                        }
                    });
                    r0.photoTransitionAnimationStartTime = System.currentTimeMillis();
                    if (VERSION.SDK_INT >= 18) {
                        obj = null;
                        r0.photoContainerView.setLayerType(2, null);
                    } else {
                        obj = null;
                    }
                    animatorSet2.start();
                }
                if (r0.currentAnimation != null) {
                    r0.currentAnimation.setSecondParentView(obj);
                    r0.currentAnimation = obj;
                    r0.centerImage.setImageBitmap((Drawable) obj);
                }
            }
        }
    }

    private void onPhotoClosed(PlaceProviderObject placeProviderObject) {
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
        for (int i = 0; i < 3; i++) {
            if (this.radialProgressViews[i] != null) {
                this.radialProgressViews[i].setBackgroundState(-1, false);
            }
        }
        Bitmap bitmap = (Bitmap) null;
        this.centerImage.setImageBitmap(bitmap);
        this.leftImage.setImageBitmap(bitmap);
        this.rightImage.setImageBitmap(bitmap);
        this.photoContainerView.post(new Runnable() {
            public void run() {
                ArticleViewer.this.animatingImageView.setImageBitmap(null);
            }
        });
        this.disableShowCheck = false;
        if (placeProviderObject != null) {
            placeProviderObject.imageReceiver.setVisible(true, true);
        }
    }

    public void onPause() {
        if (this.currentAnimation != null) {
            closePhoto(false);
        }
    }

    private void updateMinMax(float f) {
        int imageWidth = ((int) ((((float) this.centerImage.getImageWidth()) * f) - ((float) getContainerViewWidth()))) / 2;
        f = ((int) ((((float) this.centerImage.getImageHeight()) * f) - ((float) getContainerViewHeight()))) / 2;
        if (imageWidth > 0) {
            this.minX = (float) (-imageWidth);
            this.maxX = (float) imageWidth;
        } else {
            this.maxX = 0.0f;
            this.minX = 0.0f;
        }
        if (f > null) {
            this.minY = (float) (-f);
            this.maxY = (float) f;
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

    private boolean processTouchEvent(MotionEvent motionEvent) {
        if (this.photoAnimationInProgress == 0) {
            if (this.animationStartTime == 0) {
                if (motionEvent.getPointerCount() == 1 && this.gestureDetector.onTouchEvent(motionEvent) && this.doubleTap) {
                    this.doubleTap = false;
                    this.moving = false;
                    this.zooming = false;
                    checkMinMax(false);
                    return true;
                }
                if (motionEvent.getActionMasked() != 0) {
                    if (motionEvent.getActionMasked() != 5) {
                        float f = 0.0f;
                        float abs;
                        if (motionEvent.getActionMasked() == 2) {
                            if (this.canZoom && motionEvent.getPointerCount() == 2 && !this.draggingDown && this.zooming && !this.changingPage) {
                                this.discardTap = true;
                                this.scale = (((float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)))) / this.pinchStartDistance) * this.pinchStartScale;
                                this.translationX = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (this.scale / this.pinchStartScale));
                                this.translationY = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (this.scale / this.pinchStartScale));
                                updateMinMax(this.scale);
                                this.photoContainerView.invalidate();
                            } else if (motionEvent.getPointerCount() == 1) {
                                if (this.velocityTracker != null) {
                                    this.velocityTracker.addMovement(motionEvent);
                                }
                                abs = Math.abs(motionEvent.getX() - this.moveStartX);
                                float abs2 = Math.abs(motionEvent.getY() - this.dragY);
                                if (abs > ((float) AndroidUtilities.dp(3.0f)) || abs2 > ((float) AndroidUtilities.dp(3.0f))) {
                                    this.discardTap = true;
                                }
                                if (this.canDragDown && !this.draggingDown && this.scale == 1.0f && abs2 >= ((float) AndroidUtilities.dp(30.0f)) && abs2 / 2.0f > abs) {
                                    this.draggingDown = true;
                                    this.moving = false;
                                    this.dragY = motionEvent.getY();
                                    if (this.isActionBarVisible != null) {
                                        toggleActionBar(false, true);
                                    }
                                    return true;
                                } else if (this.draggingDown) {
                                    this.translationY = motionEvent.getY() - this.dragY;
                                    this.photoContainerView.invalidate();
                                } else if (this.invalidCoords || this.animationStartTime != 0) {
                                    this.invalidCoords = false;
                                    this.moveStartX = motionEvent.getX();
                                    this.moveStartY = motionEvent.getY();
                                } else {
                                    abs = this.moveStartX - motionEvent.getX();
                                    float y = this.moveStartY - motionEvent.getY();
                                    if (this.moving || ((this.scale == 1.0f && Math.abs(y) + ((float) AndroidUtilities.dp(12.0f)) < Math.abs(abs)) || this.scale != 1.0f)) {
                                        if (!this.moving) {
                                            this.moving = true;
                                            this.canDragDown = false;
                                            abs = 0.0f;
                                            y = abs;
                                        }
                                        this.moveStartX = motionEvent.getX();
                                        this.moveStartY = motionEvent.getY();
                                        updateMinMax(this.scale);
                                        if ((this.translationX < this.minX && this.rightImage.hasImage() == null) || (this.translationX > this.maxX && this.leftImage.hasImage() == null)) {
                                            abs /= 3.0f;
                                        }
                                        if (this.maxY == null && this.minY == null) {
                                            if (this.translationY - y < this.minY) {
                                                this.translationY = this.minY;
                                            } else if (this.translationY - y > this.maxY) {
                                                this.translationY = this.maxY;
                                            }
                                            this.translationX -= abs;
                                            if (this.scale != NUM) {
                                                this.translationY -= f;
                                            }
                                            this.photoContainerView.invalidate();
                                        } else {
                                            if (this.translationY >= this.minY) {
                                                if (this.translationY > this.maxY) {
                                                }
                                            }
                                            f = y / 3.0f;
                                            this.translationX -= abs;
                                            if (this.scale != NUM) {
                                                this.translationY -= f;
                                            }
                                            this.photoContainerView.invalidate();
                                        }
                                        f = y;
                                        this.translationX -= abs;
                                        if (this.scale != NUM) {
                                            this.translationY -= f;
                                        }
                                        this.photoContainerView.invalidate();
                                    }
                                }
                            }
                        } else if (motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
                            if (this.zooming) {
                                this.invalidCoords = true;
                                if (this.scale < NUM) {
                                    updateMinMax(1.0f);
                                    animateTo(1.0f, 0.0f, 0.0f, true);
                                } else if (this.scale > NUM) {
                                    motionEvent = (this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - (((this.pinchCenterX - ((float) (getContainerViewWidth() / 2))) - this.pinchStartX) * (3.0f / this.pinchStartScale));
                                    abs = (this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - (((this.pinchCenterY - ((float) (getContainerViewHeight() / 2))) - this.pinchStartY) * (3.0f / this.pinchStartScale));
                                    updateMinMax(3.0f);
                                    if (motionEvent < this.minX) {
                                        motionEvent = this.minX;
                                    } else if (motionEvent > this.maxX) {
                                        motionEvent = this.maxX;
                                    }
                                    if (abs < this.minY) {
                                        abs = this.minY;
                                    } else if (abs > this.maxY) {
                                        abs = this.maxY;
                                    }
                                    animateTo(3.0f, motionEvent, abs, true);
                                } else {
                                    checkMinMax(true);
                                }
                                this.zooming = false;
                            } else if (this.draggingDown) {
                                if (Math.abs(this.dragY - motionEvent.getY()) > ((float) getContainerViewHeight()) / 6.0f) {
                                    closePhoto(true);
                                } else {
                                    animateTo(1.0f, 0.0f, 0.0f, false);
                                }
                                this.draggingDown = false;
                            } else if (this.moving != null) {
                                motionEvent = this.translationX;
                                abs = this.translationY;
                                updateMinMax(this.scale);
                                this.moving = false;
                                this.canDragDown = true;
                                if (this.velocityTracker != null && this.scale == 1.0f) {
                                    this.velocityTracker.computeCurrentVelocity(1000);
                                    f = this.velocityTracker.getXVelocity();
                                }
                                if ((this.translationX < this.minX - ((float) (getContainerViewWidth() / 3)) || r9 < ((float) (-AndroidUtilities.dp(650.0f)))) && this.rightImage.hasImage()) {
                                    goToNext();
                                    return true;
                                } else if ((this.translationX > this.maxX + ((float) (getContainerViewWidth() / 3)) || r9 > ((float) AndroidUtilities.dp(650.0f))) && this.leftImage.hasImage()) {
                                    goToPrev();
                                    return true;
                                } else {
                                    if (this.translationX < this.minX) {
                                        motionEvent = this.minX;
                                    } else if (this.translationX > this.maxX) {
                                        motionEvent = this.maxX;
                                    }
                                    if (this.translationY < this.minY) {
                                        abs = this.minY;
                                    } else if (this.translationY > this.maxY) {
                                        abs = this.maxY;
                                    }
                                    animateTo(this.scale, motionEvent, abs, false);
                                }
                            }
                        }
                        return false;
                    }
                }
                this.discardTap = false;
                if (!this.scroller.isFinished()) {
                    this.scroller.abortAnimation();
                }
                if (!(this.draggingDown || this.changingPage)) {
                    if (this.canZoom && motionEvent.getPointerCount() == 2) {
                        this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)));
                        this.pinchStartScale = this.scale;
                        this.pinchCenterX = (motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f;
                        this.pinchCenterY = (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f;
                        this.pinchStartX = this.translationX;
                        this.pinchStartY = this.translationY;
                        this.zooming = true;
                        this.moving = false;
                        if (this.velocityTracker != null) {
                            this.velocityTracker.clear();
                        }
                    } else if (motionEvent.getPointerCount() == 1) {
                        this.moveStartX = motionEvent.getX();
                        motionEvent = motionEvent.getY();
                        this.moveStartY = motionEvent;
                        this.dragY = motionEvent;
                        this.draggingDown = false;
                        this.canDragDown = true;
                        if (this.velocityTracker != null) {
                            this.velocityTracker.clear();
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }

    private void checkMinMax(boolean z) {
        float f = this.translationX;
        float f2 = this.translationY;
        updateMinMax(this.scale);
        if (this.translationX < this.minX) {
            f = this.minX;
        } else if (this.translationX > this.maxX) {
            f = this.maxX;
        }
        if (this.translationY < this.minY) {
            f2 = this.minY;
        } else if (this.translationY > this.maxY) {
            f2 = this.maxY;
        }
        animateTo(this.scale, f, f2, z);
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
        animateTo(f, f2, f3, z, Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
    }

    private void animateTo(float f, float f2, float f3, boolean z, int i) {
        if (this.scale != f || this.translationX != f2 || this.translationY != f3) {
            this.zoomAnimation = z;
            this.animateToScale = f;
            this.animateToX = f2;
            this.animateToY = f3;
            this.animationStartTime = System.currentTimeMillis();
            this.imageMoveAnimation = new AnimatorSet();
            this.imageMoveAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "animationValue", new float[]{0.0f, 1.0f})});
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

    private void drawContent(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (this.photoAnimationInProgress != 1) {
            if (r0.isPhotoVisible || r0.photoAnimationInProgress == 2) {
                float f;
                float f2;
                float f3;
                float f4;
                float f5;
                float f6;
                float containerViewWidth;
                float bitmapHeight;
                float containerViewHeight;
                int i;
                float containerViewWidth2;
                int i2;
                float f7;
                if (r0.imageMoveAnimation != null) {
                    if (!r0.scroller.isFinished()) {
                        r0.scroller.abortAnimation();
                    }
                    f = r0.scale + ((r0.animateToScale - r0.scale) * r0.animationValue);
                    f2 = r0.translationX + ((r0.animateToX - r0.translationX) * r0.animationValue);
                    f3 = r0.translationY + ((r0.animateToY - r0.translationY) * r0.animationValue);
                    f4 = (r0.animateToScale == 1.0f && r0.scale == 1.0f && r0.translationX == 0.0f) ? f3 : -1.0f;
                    r0.photoContainerView.invalidate();
                } else {
                    if (r0.animationStartTime != 0) {
                        r0.translationX = r0.animateToX;
                        r0.translationY = r0.animateToY;
                        r0.scale = r0.animateToScale;
                        r0.animationStartTime = 0;
                        updateMinMax(r0.scale);
                        r0.zoomAnimation = false;
                    }
                    if (!r0.scroller.isFinished() && r0.scroller.computeScrollOffset()) {
                        if (((float) r0.scroller.getStartX()) < r0.maxX && ((float) r0.scroller.getStartX()) > r0.minX) {
                            r0.translationX = (float) r0.scroller.getCurrX();
                        }
                        if (((float) r0.scroller.getStartY()) < r0.maxY && ((float) r0.scroller.getStartY()) > r0.minY) {
                            r0.translationY = (float) r0.scroller.getCurrY();
                        }
                        r0.photoContainerView.invalidate();
                    }
                    if (r0.switchImageAfterAnimation != 0) {
                        if (r0.switchImageAfterAnimation == 1) {
                            setImageIndex(r0.currentIndex + 1, false);
                        } else if (r0.switchImageAfterAnimation == 2) {
                            setImageIndex(r0.currentIndex - 1, false);
                        }
                        r0.switchImageAfterAnimation = 0;
                    }
                    f = r0.scale;
                    f3 = r0.translationY;
                    f2 = r0.translationX;
                    f4 = !r0.moving ? r0.translationY : -1.0f;
                }
                if (r0.scale != 1.0f || f4 == -1.0f || r0.zoomAnimation) {
                    r0.photoBackgroundDrawable.setAlpha(255);
                } else {
                    float containerViewHeight2 = ((float) getContainerViewHeight()) / 4.0f;
                    r0.photoBackgroundDrawable.setAlpha((int) Math.max(127.0f, 255.0f * (1.0f - (Math.min(Math.abs(f4), containerViewHeight2) / containerViewHeight2))));
                }
                ImageReceiver imageReceiver = null;
                if (!(r0.scale < 1.0f || r0.zoomAnimation || r0.zooming)) {
                    if (f2 > r0.maxX + ((float) AndroidUtilities.dp(5.0f))) {
                        imageReceiver = r0.leftImage;
                    } else if (f2 < r0.minX - ((float) AndroidUtilities.dp(5.0f))) {
                        imageReceiver = r0.rightImage;
                    }
                }
                r0.changingPage = imageReceiver != null;
                if (imageReceiver == r0.rightImage) {
                    float f8;
                    if (r0.zoomAnimation || f2 >= r0.minX) {
                        f8 = 0.0f;
                        f4 = 1.0f;
                        f5 = f2;
                    } else {
                        f4 = Math.min(1.0f, (r0.minX - f2) / ((float) canvas.getWidth()));
                        f8 = (1.0f - f4) * 0.3f;
                        f5 = (float) ((-canvas.getWidth()) - (AndroidUtilities.dp(30.0f) / 2));
                    }
                    if (imageReceiver.hasBitmapImage()) {
                        canvas.save();
                        canvas2.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                        canvas2.translate(((float) (canvas.getWidth() + (AndroidUtilities.dp(30.0f) / 2))) + f5, 0.0f);
                        f6 = 1.0f - f8;
                        canvas2.scale(f6, f6);
                        f6 = (float) imageReceiver.getBitmapWidth();
                        containerViewWidth = ((float) getContainerViewWidth()) / f6;
                        bitmapHeight = (float) imageReceiver.getBitmapHeight();
                        containerViewHeight = ((float) getContainerViewHeight()) / bitmapHeight;
                        if (containerViewWidth <= containerViewHeight) {
                            containerViewHeight = containerViewWidth;
                        }
                        i = (int) (f6 * containerViewHeight);
                        int i3 = (int) (bitmapHeight * containerViewHeight);
                        imageReceiver.setAlpha(f4);
                        imageReceiver.setImageCoords((-i) / 2, (-i3) / 2, i, i3);
                        imageReceiver.draw(canvas2);
                        canvas.restore();
                    }
                    canvas.save();
                    canvas2.translate(f5, f3 / f);
                    canvas2.translate(((((float) canvas.getWidth()) * (r0.scale + 1.0f)) + ((float) AndroidUtilities.dp(30.0f))) / 2.0f, (-f3) / f);
                    r0.radialProgressViews[1].setScale(1.0f - f8);
                    r0.radialProgressViews[1].setAlpha(f4);
                    r0.radialProgressViews[1].onDraw(canvas2);
                    canvas.restore();
                }
                if (r0.zoomAnimation || f2 <= r0.maxX) {
                    f6 = 1.0f;
                    f5 = f2;
                    containerViewWidth = 0.0f;
                } else {
                    f6 = Math.min(1.0f, (f2 - r0.maxX) / ((float) canvas.getWidth()));
                    containerViewWidth = 0.3f * f6;
                    f6 = 1.0f - f6;
                    f5 = r0.maxX;
                }
                Object obj = (r0.aspectRatioFrameLayout == null || r0.aspectRatioFrameLayout.getVisibility() != 0) ? null : 1;
                if (r0.centerImage.hasBitmapImage()) {
                    canvas.save();
                    canvas2.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                    canvas2.translate(f5, f3);
                    bitmapHeight = f - containerViewWidth;
                    canvas2.scale(bitmapHeight, bitmapHeight);
                    int bitmapWidth = r0.centerImage.getBitmapWidth();
                    int bitmapHeight2 = r0.centerImage.getBitmapHeight();
                    if (obj != null && r0.textureUploaded && Math.abs((((float) bitmapWidth) / ((float) bitmapHeight2)) - (((float) r0.videoTextureView.getMeasuredWidth()) / ((float) r0.videoTextureView.getMeasuredHeight()))) > 0.01f) {
                        bitmapWidth = r0.videoTextureView.getMeasuredWidth();
                        bitmapHeight2 = r0.videoTextureView.getMeasuredHeight();
                    }
                    bitmapHeight = (float) bitmapWidth;
                    containerViewWidth2 = ((float) getContainerViewWidth()) / bitmapHeight;
                    containerViewHeight = (float) bitmapHeight2;
                    float containerViewHeight3 = ((float) getContainerViewHeight()) / containerViewHeight;
                    if (containerViewWidth2 <= containerViewHeight3) {
                        containerViewHeight3 = containerViewWidth2;
                    }
                    i2 = (int) (bitmapHeight * containerViewHeight3);
                    bitmapWidth = (int) (containerViewHeight * containerViewHeight3);
                    if (!(obj != null && r0.textureUploaded && r0.videoCrossfadeStarted && r0.videoCrossfadeAlpha == 1.0f)) {
                        r0.centerImage.setAlpha(f6);
                        r0.centerImage.setImageCoords((-i2) / 2, (-bitmapWidth) / 2, i2, bitmapWidth);
                        r0.centerImage.draw(canvas2);
                    }
                    if (obj != null) {
                        if (!r0.videoCrossfadeStarted && r0.textureUploaded) {
                            r0.videoCrossfadeStarted = true;
                            r0.videoCrossfadeAlpha = 0.0f;
                            r0.videoCrossfadeAlphaLastTime = System.currentTimeMillis();
                        }
                        canvas2.translate((float) ((-i2) / 2), (float) ((-bitmapWidth) / 2));
                        r0.videoTextureView.setAlpha(r0.videoCrossfadeAlpha * f6);
                        r0.aspectRatioFrameLayout.draw(canvas2);
                        if (r0.videoCrossfadeStarted && r0.videoCrossfadeAlpha < 1.0f) {
                            long currentTimeMillis = System.currentTimeMillis();
                            f7 = f2;
                            long j = currentTimeMillis - r0.videoCrossfadeAlphaLastTime;
                            r0.videoCrossfadeAlphaLastTime = currentTimeMillis;
                            r0.videoCrossfadeAlpha += ((float) j) / 300.0f;
                            r0.photoContainerView.invalidate();
                            if (r0.videoCrossfadeAlpha > 1.0f) {
                                r0.videoCrossfadeAlpha = 1.0f;
                            }
                            canvas.restore();
                        }
                    }
                    f7 = f2;
                    canvas.restore();
                } else {
                    f7 = f2;
                }
                if (obj == null && r0.bottomLayout.getVisibility() != 0) {
                    canvas.save();
                    canvas2.translate(f5, f3 / f);
                    r0.radialProgressViews[0].setScale(1.0f - containerViewWidth);
                    r0.radialProgressViews[0].setAlpha(f6);
                    r0.radialProgressViews[0].onDraw(canvas2);
                    canvas.restore();
                }
                if (imageReceiver == r0.leftImage) {
                    if (imageReceiver.hasBitmapImage()) {
                        canvas.save();
                        canvas2.translate((float) (getContainerViewWidth() / 2), (float) (getContainerViewHeight() / 2));
                        canvas2.translate(((-((((float) canvas.getWidth()) * (r0.scale + 1.0f)) + ((float) AndroidUtilities.dp(30.0f)))) / 2.0f) + f7, 0.0f);
                        f6 = (float) imageReceiver.getBitmapWidth();
                        containerViewWidth = ((float) getContainerViewWidth()) / f6;
                        containerViewWidth2 = (float) imageReceiver.getBitmapHeight();
                        f5 = ((float) getContainerViewHeight()) / containerViewWidth2;
                        if (containerViewWidth > f5) {
                            containerViewWidth = f5;
                        }
                        i = (int) (f6 * containerViewWidth);
                        i2 = (int) (containerViewWidth2 * containerViewWidth);
                        imageReceiver.setAlpha(1.0f);
                        imageReceiver.setImageCoords((-i) / 2, (-i2) / 2, i, i2);
                        imageReceiver.draw(canvas2);
                        canvas.restore();
                    }
                    canvas.save();
                    canvas2.translate(f7, f3 / f);
                    canvas2.translate((-((((float) canvas.getWidth()) * (r0.scale + 1.0f)) + ((float) AndroidUtilities.dp(30.0f)))) / 2.0f, (-f3) / f);
                    r0.radialProgressViews[2].setScale(1.0f);
                    r0.radialProgressViews[2].setAlpha(1.0f);
                    r0.radialProgressViews[2].onDraw(canvas2);
                    canvas.restore();
                }
            }
        }
    }

    private void onActionClick(boolean z) {
        TLObject media = getMedia(this.currentIndex);
        if (media instanceof Document) {
            if (this.currentFileNames[0] != null) {
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
                } else if (z) {
                    if (FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentFileNames[0])) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(document);
                    } else {
                        FileLoader.getInstance(this.currentAccount).loadFile(document, true, 1);
                    }
                }
            }
        }
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (this.scale != NUM) {
            this.scroller.abortAnimation();
            this.scroller.fling(Math.round(this.translationX), Math.round(this.translationY), Math.round(f), Math.round(f2), (int) this.minX, (int) this.maxX, (int) this.minY, (int) this.maxY);
            this.photoContainerView.postInvalidate();
        }
        return null;
    }

    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if (this.discardTap) {
            return false;
        }
        boolean z = this.aspectRatioFrameLayout != null && this.aspectRatioFrameLayout.getVisibility() == 0;
        if (!(this.radialProgressViews[0] == null || this.photoContainerView == null || z)) {
            int access$14200 = this.radialProgressViews[0].backgroundState;
            if (access$14200 > 0 && access$14200 <= 3) {
                float x = motionEvent.getX();
                motionEvent = motionEvent.getY();
                if (x >= ((float) (getContainerViewWidth() - AndroidUtilities.dp(100.0f))) / 2.0f && x <= ((float) (getContainerViewWidth() + AndroidUtilities.dp(100.0f))) / 2.0f && motionEvent >= ((float) (getContainerViewHeight() - AndroidUtilities.dp(100.0f))) / 2.0f && motionEvent <= ((float) (getContainerViewHeight() + AndroidUtilities.dp(100.0f))) / 2.0f) {
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
        if (this.canZoom) {
            if (this.scale == 1.0f) {
                if (this.translationY == 0.0f) {
                    if (this.translationX != 0.0f) {
                    }
                }
            }
            if (this.animationStartTime == 0) {
                if (this.photoAnimationInProgress == 0) {
                    if (this.scale == 1.0f) {
                        float x = (motionEvent.getX() - ((float) (getContainerViewWidth() / 2))) - (((motionEvent.getX() - ((float) (getContainerViewWidth() / 2))) - this.translationX) * (3.0f / this.scale));
                        float y = (motionEvent.getY() - ((float) (getContainerViewHeight() / 2))) - (((motionEvent.getY() - ((float) (getContainerViewHeight() / 2))) - this.translationY) * (3.0f / this.scale));
                        updateMinMax(3.0f);
                        if (x < this.minX) {
                            x = this.minX;
                        } else if (x > this.maxX) {
                            x = this.maxX;
                        }
                        if (y < this.minY) {
                            y = this.minY;
                        } else if (y > this.maxY) {
                            y = this.maxY;
                        }
                        animateTo(3.0f, x, y, true);
                    } else {
                        animateTo(1.0f, 0.0f, 0.0f, true);
                    }
                    this.doubleTap = true;
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private ImageReceiver getImageReceiverFromListView(ViewGroup viewGroup, PageBlock pageBlock, int[] iArr) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof BlockPhotoCell) {
                BlockPhotoCell blockPhotoCell = (BlockPhotoCell) childAt;
                if (blockPhotoCell.currentBlock == pageBlock) {
                    childAt.getLocationInWindow(iArr);
                    return blockPhotoCell.imageView;
                }
            } else if (childAt instanceof BlockVideoCell) {
                BlockVideoCell blockVideoCell = (BlockVideoCell) childAt;
                if (blockVideoCell.currentBlock == pageBlock) {
                    childAt.getLocationInWindow(iArr);
                    return blockVideoCell.imageView;
                }
            } else if (childAt instanceof BlockCollageCell) {
                r2 = getImageReceiverFromListView(((BlockCollageCell) childAt).innerListView, pageBlock, iArr);
                if (r2 != null) {
                    return r2;
                }
            } else if (childAt instanceof BlockSlideshowCell) {
                r2 = getImageReceiverFromListView(((BlockSlideshowCell) childAt).innerListView, pageBlock, iArr);
                if (r2 != null) {
                    return r2;
                }
            } else {
                continue;
            }
        }
        return null;
    }

    private PlaceProviderObject getPlaceForPhoto(PageBlock pageBlock) {
        pageBlock = getImageReceiverFromListView(this.listView, pageBlock, this.coords);
        if (pageBlock == null) {
            return null;
        }
        PlaceProviderObject placeProviderObject = new PlaceProviderObject();
        placeProviderObject.viewX = this.coords[0];
        placeProviderObject.viewY = this.coords[1];
        placeProviderObject.parentView = this.listView;
        placeProviderObject.imageReceiver = pageBlock;
        placeProviderObject.thumb = pageBlock.getBitmapSafe();
        placeProviderObject.radius = pageBlock.getRoundRadius();
        placeProviderObject.clipTopAddition = this.currentHeaderHeight;
        return placeProviderObject;
    }
}
