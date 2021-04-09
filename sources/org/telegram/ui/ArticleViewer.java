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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.DisplayCutout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManagerFixed;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$GeoPoint;
import org.telegram.tgnet.TLRPC$Page;
import org.telegram.tgnet.TLRPC$PageBlock;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$RichText;
import org.telegram.tgnet.TLRPC$TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC$TL_messages_getWebPage;
import org.telegram.tgnet.TLRPC$TL_messages_messages;
import org.telegram.tgnet.TLRPC$TL_page;
import org.telegram.tgnet.TLRPC$TL_pageBlockAudio;
import org.telegram.tgnet.TLRPC$TL_pageBlockAuthorDate;
import org.telegram.tgnet.TLRPC$TL_pageBlockBlockquote;
import org.telegram.tgnet.TLRPC$TL_pageBlockChannel;
import org.telegram.tgnet.TLRPC$TL_pageBlockCollage;
import org.telegram.tgnet.TLRPC$TL_pageBlockCover;
import org.telegram.tgnet.TLRPC$TL_pageBlockDetails;
import org.telegram.tgnet.TLRPC$TL_pageBlockDivider;
import org.telegram.tgnet.TLRPC$TL_pageBlockEmbed;
import org.telegram.tgnet.TLRPC$TL_pageBlockEmbedPost;
import org.telegram.tgnet.TLRPC$TL_pageBlockFooter;
import org.telegram.tgnet.TLRPC$TL_pageBlockHeader;
import org.telegram.tgnet.TLRPC$TL_pageBlockKicker;
import org.telegram.tgnet.TLRPC$TL_pageBlockList;
import org.telegram.tgnet.TLRPC$TL_pageBlockMap;
import org.telegram.tgnet.TLRPC$TL_pageBlockOrderedList;
import org.telegram.tgnet.TLRPC$TL_pageBlockParagraph;
import org.telegram.tgnet.TLRPC$TL_pageBlockPhoto;
import org.telegram.tgnet.TLRPC$TL_pageBlockPreformatted;
import org.telegram.tgnet.TLRPC$TL_pageBlockPullquote;
import org.telegram.tgnet.TLRPC$TL_pageBlockRelatedArticles;
import org.telegram.tgnet.TLRPC$TL_pageBlockSlideshow;
import org.telegram.tgnet.TLRPC$TL_pageBlockSubheader;
import org.telegram.tgnet.TLRPC$TL_pageBlockSubtitle;
import org.telegram.tgnet.TLRPC$TL_pageBlockTable;
import org.telegram.tgnet.TLRPC$TL_pageBlockTitle;
import org.telegram.tgnet.TLRPC$TL_pageBlockVideo;
import org.telegram.tgnet.TLRPC$TL_pageCaption;
import org.telegram.tgnet.TLRPC$TL_pageRelatedArticle;
import org.telegram.tgnet.TLRPC$TL_pageTableCell;
import org.telegram.tgnet.TLRPC$TL_pageTableRow;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_textAnchor;
import org.telegram.tgnet.TLRPC$TL_textBold;
import org.telegram.tgnet.TLRPC$TL_textConcat;
import org.telegram.tgnet.TLRPC$TL_textEmail;
import org.telegram.tgnet.TLRPC$TL_textEmpty;
import org.telegram.tgnet.TLRPC$TL_textFixed;
import org.telegram.tgnet.TLRPC$TL_textImage;
import org.telegram.tgnet.TLRPC$TL_textItalic;
import org.telegram.tgnet.TLRPC$TL_textMarked;
import org.telegram.tgnet.TLRPC$TL_textPhone;
import org.telegram.tgnet.TLRPC$TL_textPlain;
import org.telegram.tgnet.TLRPC$TL_textStrike;
import org.telegram.tgnet.TLRPC$TL_textSubscript;
import org.telegram.tgnet.TLRPC$TL_textSuperscript;
import org.telegram.tgnet.TLRPC$TL_textUnderline;
import org.telegram.tgnet.TLRPC$TL_textUrl;
import org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC$TL_user;
import org.telegram.tgnet.TLRPC$TL_webPage;
import org.telegram.tgnet.TLRPC$TL_webPageNotModified;
import org.telegram.tgnet.TLRPC$Update;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedArrowDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CloseProgressDrawable2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.TableLayout;
import org.telegram.ui.Components.TextPaintUrlSpan;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.WebPlayerView;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PinchToZoomHelper;

public class ArticleViewer implements NotificationCenter.NotificationCenterDelegate {
    public static final Property<WindowView, Float> ARTICLE_VIEWER_INNER_TRANSLATION_X = new AnimationProperties.FloatProperty<WindowView>("innerTranslationX") {
        public void setValue(WindowView windowView, float f) {
            windowView.setInnerTranslationX(f);
        }

        public Float get(WindowView windowView) {
            return Float.valueOf(windowView.getInnerTranslationX());
        }
    };
    @SuppressLint({"StaticFieldLeak"})
    private static volatile ArticleViewer Instance;
    /* access modifiers changed from: private */
    public static TextPaint audioTimePaint = new TextPaint(1);
    private static SparseArray<TextPaint> authorTextPaints = new SparseArray<>();
    private static TextPaint channelNamePaint;
    private static TextPaint channelNamePhotoPaint;
    private static SparseArray<TextPaint> detailsTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint dividerPaint;
    /* access modifiers changed from: private */
    public static Paint dotsPaint;
    private static TextPaint embedPostAuthorPaint;
    private static SparseArray<TextPaint> embedPostCaptionTextPaints = new SparseArray<>();
    private static TextPaint embedPostDatePaint;
    private static SparseArray<TextPaint> embedPostTextPaints = new SparseArray<>();
    private static TextPaint errorTextPaint;
    private static SparseArray<TextPaint> footerTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> headerTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> kickerTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static TextPaint listTextNumPaint;
    private static SparseArray<TextPaint> listTextPaints = new SparseArray<>();
    private static TextPaint listTextPointerPaint;
    private static SparseArray<TextPaint> mediaCaptionTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> mediaCreditTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> paragraphTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint photoBackgroundPaint;
    private static SparseArray<TextPaint> photoCaptionTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> photoCreditTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint preformattedBackgroundPaint;
    private static SparseArray<TextPaint> preformattedTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint quoteLinePaint;
    private static SparseArray<TextPaint> quoteTextPaints = new SparseArray<>();
    private static TextPaint relatedArticleHeaderPaint;
    private static TextPaint relatedArticleTextPaint;
    private static SparseArray<TextPaint> relatedArticleTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> subheaderTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> subtitleTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint tableHalfLinePaint;
    /* access modifiers changed from: private */
    public static Paint tableHeaderPaint;
    /* access modifiers changed from: private */
    public static Paint tableLinePaint;
    /* access modifiers changed from: private */
    public static Paint tableStripPaint;
    private static SparseArray<TextPaint> tableTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> titleTextPaints = new SparseArray<>();
    private static Paint urlPaint;
    /* access modifiers changed from: private */
    public static Paint webpageMarkPaint;
    /* access modifiers changed from: private */
    public static Paint webpageSearchPaint;
    /* access modifiers changed from: private */
    public static Paint webpageUrlPaint;
    /* access modifiers changed from: private */
    public WebpageAdapter[] adapter;
    /* access modifiers changed from: private */
    public int allowAnimationIndex = -1;
    /* access modifiers changed from: private */
    public int anchorsOffsetMeasuredWidth;
    /* access modifiers changed from: private */
    public boolean animateClear = true;
    /* access modifiers changed from: private */
    public Runnable animationEndRunnable;
    private int animationInProgress;
    /* access modifiers changed from: private */
    public boolean attachedToWindow;
    private ImageView backButton;
    /* access modifiers changed from: private */
    public BackDrawable backDrawable;
    /* access modifiers changed from: private */
    public Paint backgroundPaint;
    /* access modifiers changed from: private */
    public boolean checkingForLongPress = false;
    /* access modifiers changed from: private */
    public ImageView clearButton;
    /* access modifiers changed from: private */
    public boolean collapsed;
    /* access modifiers changed from: private */
    public FrameLayout containerView;
    /* access modifiers changed from: private */
    public ArrayList<BlockEmbedCell> createdWebViews = new ArrayList<>();
    /* access modifiers changed from: private */
    public int currentAccount;
    /* access modifiers changed from: private */
    public int currentHeaderHeight;
    /* access modifiers changed from: private */
    public WebPlayerView currentPlayingVideo;
    /* access modifiers changed from: private */
    public int currentSearchIndex;
    /* access modifiers changed from: private */
    public View customView;
    /* access modifiers changed from: private */
    public WebChromeClient.CustomViewCallback customViewCallback;
    private TextView deleteView;
    /* access modifiers changed from: private */
    public boolean drawBlockSelection;
    private FontCell[] fontCells = new FontCell[2];
    /* access modifiers changed from: private */
    public AspectRatioFrameLayout fullscreenAspectRatioView;
    /* access modifiers changed from: private */
    public TextureView fullscreenTextureView;
    /* access modifiers changed from: private */
    public FrameLayout fullscreenVideoContainer;
    /* access modifiers changed from: private */
    public WebPlayerView fullscreenedVideo;
    /* access modifiers changed from: private */
    public boolean hasCutout;
    /* access modifiers changed from: private */
    public Paint headerPaint = new Paint();
    /* access modifiers changed from: private */
    public Paint headerProgressPaint = new Paint();
    /* access modifiers changed from: private */
    public FrameLayout headerView;
    /* access modifiers changed from: private */
    public boolean ignoreOnTextChange;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    /* access modifiers changed from: private */
    public boolean isVisible;
    /* access modifiers changed from: private */
    public boolean keyboardVisible;
    /* access modifiers changed from: private */
    public int lastBlockNum = 1;
    /* access modifiers changed from: private */
    public Object lastInsets;
    private int lastReqId;
    private int lastSearchIndex = -1;
    /* access modifiers changed from: private */
    public Drawable layerShadowDrawable;
    /* access modifiers changed from: private */
    public LinearLayoutManager[] layoutManager;
    private Runnable lineProgressTickRunnable;
    private LineProgressView lineProgressView;
    /* access modifiers changed from: private */
    public BottomSheet linkSheet;
    /* access modifiers changed from: private */
    public RecyclerListView[] listView;
    /* access modifiers changed from: private */
    public TLRPC$Chat loadedChannel;
    private boolean loadingChannel;
    /* access modifiers changed from: private */
    public ActionBarMenuItem menuButton;
    private FrameLayout menuContainer;
    private int openUrlReqId;
    /* access modifiers changed from: private */
    public AnimatorSet pageSwitchAnimation;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$WebPage> pagesStack = new ArrayList<>();
    /* access modifiers changed from: private */
    public Activity parentActivity;
    /* access modifiers changed from: private */
    public BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    PinchToZoomHelper pinchToZoomHelper;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow popupWindow;
    private int pressCount = 0;
    /* access modifiers changed from: private */
    public int pressedLayoutY;
    /* access modifiers changed from: private */
    public TextPaintUrlSpan pressedLink;
    /* access modifiers changed from: private */
    public DrawingText pressedLinkOwnerLayout;
    /* access modifiers changed from: private */
    public View pressedLinkOwnerView;
    private int previewsReqId;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public AnimatorSet progressViewAnimation;
    /* access modifiers changed from: private */
    public AnimatorSet runAfterKeyboardClose;
    /* access modifiers changed from: private */
    public Paint scrimPaint;
    /* access modifiers changed from: private */
    public FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ImageView searchDownButton;
    /* access modifiers changed from: private */
    public EditTextBoldCursor searchField;
    /* access modifiers changed from: private */
    public FrameLayout searchPanel;
    /* access modifiers changed from: private */
    public ArrayList<SearchResult> searchResults = new ArrayList<>();
    private Runnable searchRunnable;
    private View searchShadow;
    /* access modifiers changed from: private */
    public String searchText;
    private ImageView searchUpButton;
    private int selectedFont = 0;
    /* access modifiers changed from: private */
    public Drawable slideDotBigDrawable;
    /* access modifiers changed from: private */
    public Drawable slideDotDrawable;
    /* access modifiers changed from: private */
    public Paint statusBarPaint = new Paint();
    TextSelectionHelper.ArticleTextSelectionHelper textSelectionHelper;
    TextSelectionHelper.ArticleTextSelectionHelper textSelectionHelperBottomSheet;
    /* access modifiers changed from: private */
    public SimpleTextView titleTextView;
    private long transitionAnimationStartTime;
    private LinkPath urlPath = new LinkPath();
    private Dialog visibleDialog;
    private WindowManager.LayoutParams windowLayoutParams;
    /* access modifiers changed from: private */
    public WindowView windowView;

    static /* synthetic */ boolean lambda$setParentActivity$20(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public void updateWindowLayoutParamsForSearch() {
    }

    static /* synthetic */ int access$13008(ArticleViewer articleViewer) {
        int i = articleViewer.lastBlockNum;
        articleViewer.lastBlockNum = i + 1;
        return i;
    }

    static /* synthetic */ int access$1704(ArticleViewer articleViewer) {
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

    private static class TL_pageBlockRelatedArticlesChild extends TLRPC$PageBlock {
        /* access modifiers changed from: private */
        public int num;
        /* access modifiers changed from: private */
        public TLRPC$TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesChild() {
        }
    }

    private static class TL_pageBlockRelatedArticlesShadow extends TLRPC$PageBlock {
        /* access modifiers changed from: private */
        public TLRPC$TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesShadow() {
        }
    }

    private static class TL_pageBlockDetailsChild extends TLRPC$PageBlock {
        /* access modifiers changed from: private */
        public TLRPC$PageBlock block;
        /* access modifiers changed from: private */
        public TLRPC$PageBlock parent;

        private TL_pageBlockDetailsChild() {
        }
    }

    private class TL_pageBlockListParent extends TLRPC$PageBlock {
        /* access modifiers changed from: private */
        public ArrayList<TL_pageBlockListItem> items;
        /* access modifiers changed from: private */
        public int lastFontSize;
        /* access modifiers changed from: private */
        public int lastMaxNumCalcWidth;
        /* access modifiers changed from: private */
        public int level;
        /* access modifiers changed from: private */
        public int maxNumWidth;
        /* access modifiers changed from: private */
        public TLRPC$TL_pageBlockList pageBlockList;

        private TL_pageBlockListParent(ArticleViewer articleViewer) {
            this.items = new ArrayList<>();
        }
    }

    private class TL_pageBlockListItem extends TLRPC$PageBlock {
        /* access modifiers changed from: private */
        public TLRPC$PageBlock blockItem;
        /* access modifiers changed from: private */
        public int index;
        /* access modifiers changed from: private */
        public String num;
        /* access modifiers changed from: private */
        public DrawingText numLayout;
        /* access modifiers changed from: private */
        public TL_pageBlockListParent parent;
        /* access modifiers changed from: private */
        public TLRPC$RichText textItem;

        private TL_pageBlockListItem(ArticleViewer articleViewer) {
            this.index = Integer.MAX_VALUE;
        }
    }

    private class TL_pageBlockOrderedListParent extends TLRPC$PageBlock {
        /* access modifiers changed from: private */
        public ArrayList<TL_pageBlockOrderedListItem> items;
        /* access modifiers changed from: private */
        public int lastFontSize;
        /* access modifiers changed from: private */
        public int lastMaxNumCalcWidth;
        /* access modifiers changed from: private */
        public int level;
        /* access modifiers changed from: private */
        public int maxNumWidth;
        /* access modifiers changed from: private */
        public TLRPC$TL_pageBlockOrderedList pageBlockOrderedList;

        private TL_pageBlockOrderedListParent(ArticleViewer articleViewer) {
            this.items = new ArrayList<>();
        }
    }

    private class TL_pageBlockOrderedListItem extends TLRPC$PageBlock {
        /* access modifiers changed from: private */
        public TLRPC$PageBlock blockItem;
        /* access modifiers changed from: private */
        public int index;
        /* access modifiers changed from: private */
        public String num;
        /* access modifiers changed from: private */
        public DrawingText numLayout;
        /* access modifiers changed from: private */
        public TL_pageBlockOrderedListParent parent;
        /* access modifiers changed from: private */
        public TLRPC$RichText textItem;

        private TL_pageBlockOrderedListItem(ArticleViewer articleViewer) {
            this.index = Integer.MAX_VALUE;
        }
    }

    private static class TL_pageBlockEmbedPostCaption extends TLRPC$TL_pageBlockEmbedPost {
        /* access modifiers changed from: private */
        public TLRPC$TL_pageBlockEmbedPost parent;

        private TL_pageBlockEmbedPostCaption() {
        }
    }

    public class DrawingText implements TextSelectionHelper.TextLayoutBlock {
        public LinkPath markPath;
        public TLRPC$PageBlock parentBlock;
        public Object parentText;
        public CharSequence prefix;
        public int row;
        public int searchIndex = -1;
        public LinkPath searchPath;
        public StaticLayout textLayout;
        public LinkPath textPath;
        public int x;
        public int y;

        public DrawingText() {
        }

        public void draw(Canvas canvas) {
            if (!ArticleViewer.this.searchResults.isEmpty()) {
                SearchResult searchResult = (SearchResult) ArticleViewer.this.searchResults.get(ArticleViewer.this.currentSearchIndex);
                if (searchResult.block != this.parentBlock || (searchResult.text != this.parentText && (!(searchResult.text instanceof String) || this.parentText != null))) {
                    this.searchIndex = -1;
                    this.searchPath = null;
                } else if (this.searchIndex != searchResult.index) {
                    LinkPath linkPath = new LinkPath(true);
                    this.searchPath = linkPath;
                    linkPath.setAllowReset(false);
                    this.searchPath.setCurrentLayout(this.textLayout, searchResult.index, 0.0f);
                    this.searchPath.setBaselineShift(0);
                    this.textLayout.getSelectionPath(searchResult.index, searchResult.index + ArticleViewer.this.searchText.length(), this.searchPath);
                    this.searchPath.setAllowReset(true);
                }
            } else {
                this.searchIndex = -1;
                this.searchPath = null;
            }
            LinkPath linkPath2 = this.searchPath;
            if (linkPath2 != null) {
                canvas.drawPath(linkPath2, ArticleViewer.webpageSearchPaint);
            }
            LinkPath linkPath3 = this.textPath;
            if (linkPath3 != null) {
                canvas.drawPath(linkPath3, ArticleViewer.webpageUrlPaint);
            }
            LinkPath linkPath4 = this.markPath;
            if (linkPath4 != null) {
                canvas.drawPath(linkPath4, ArticleViewer.webpageMarkPaint);
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

    private class TextSizeCell extends FrameLayout {
        /* access modifiers changed from: private */
        public int endFontSize = 30;
        private int lastWidth;
        /* access modifiers changed from: private */
        public SeekBarView sizeBar;
        /* access modifiers changed from: private */
        public int startFontSize = 12;
        private TextPaint textPaint;

        public TextSizeCell(Context context) {
            super(context);
            setWillNotDraw(false);
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(16.0f));
            SeekBarView seekBarView = new SeekBarView(context);
            this.sizeBar = seekBarView;
            seekBarView.setReportChanges(true);
            this.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate(ArticleViewer.this) {
                public void onSeekBarPressed(boolean z) {
                }

                public void onSeekBarDrag(boolean z, float f) {
                    int round = Math.round(((float) TextSizeCell.this.startFontSize) + (((float) (TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize)) * f));
                    if (round != SharedConfig.ivFontSize) {
                        SharedConfig.ivFontSize = round;
                        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
                        edit.putInt("iv_font_size", SharedConfig.ivFontSize);
                        edit.commit();
                        ArticleViewer.this.adapter[0].searchTextOffset.clear();
                        ArticleViewer.this.updatePaintSize();
                        TextSizeCell.this.invalidate();
                    }
                }

                public CharSequence getContentDescription() {
                    return String.valueOf(Math.round(((float) TextSizeCell.this.startFontSize) + (((float) (TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize)) * TextSizeCell.this.sizeBar.getProgress())));
                }

                public int getStepsCount() {
                    return TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize;
                }
            });
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 5.0f, 5.0f, 39.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteValueText"));
            canvas.drawText("" + SharedConfig.ivFontSize, (float) (getMeasuredWidth() - AndroidUtilities.dp(39.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            int size = View.MeasureSpec.getSize(i);
            if (this.lastWidth != size) {
                SeekBarView seekBarView = this.sizeBar;
                int i3 = SharedConfig.ivFontSize;
                int i4 = this.startFontSize;
                seekBarView.setProgress(((float) (i3 - i4)) / ((float) (this.endFontSize - i4)));
                this.lastWidth = size;
            }
        }

        public void invalidate() {
            super.invalidate();
            this.sizeBar.invalidate();
        }
    }

    public static class FontCell extends FrameLayout {
        private RadioButton radioButton;
        private TextView textView;

        public FontCell(Context context) {
            super(context);
            setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 2));
            RadioButton radioButton2 = new RadioButton(context);
            this.radioButton = radioButton2;
            radioButton2.setSize(AndroidUtilities.dp(20.0f));
            this.radioButton.setColor(Theme.getColor("dialogRadioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            RadioButton radioButton3 = this.radioButton;
            boolean z = LocaleController.isRTL;
            int i = 5;
            addView(radioButton3, LayoutHelper.createFrame(22, 22.0f, (z ? 5 : 3) | 48, (float) (z ? 0 : 22), 13.0f, (float) (z ? 22 : 0), 0.0f));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            TextView textView3 = this.textView;
            boolean z2 = LocaleController.isRTL;
            addView(textView3, LayoutHelper.createFrame(-1, -1.0f, (!z2 ? 3 : i) | 48, (float) (z2 ? 17 : 62), 0.0f, (float) (z2 ? 62 : 17), 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }

        public void select(boolean z, boolean z2) {
            this.radioButton.setChecked(z, z2);
        }

        public void setTextAndTypeface(String str, Typeface typeface) {
            this.textView.setText(str);
            this.textView.setTypeface(typeface);
            setContentDescription(str);
            invalidate();
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName(RadioButton.class.getName());
            accessibilityNodeInfo.setChecked(this.radioButton.isChecked());
            accessibilityNodeInfo.setCheckable(true);
        }
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (ArticleViewer.this.pendingCheckForLongPress == null) {
                ArticleViewer articleViewer = ArticleViewer.this;
                CheckForLongPress unused = articleViewer.pendingCheckForLongPress = new CheckForLongPress();
            }
            ArticleViewer.this.pendingCheckForLongPress.currentPressCount = ArticleViewer.access$1704(ArticleViewer.this);
            if (ArticleViewer.this.windowView != null) {
                ArticleViewer.this.windowView.postDelayed(ArticleViewer.this.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
            }
        }
    }

    private class WindowView extends FrameLayout {
        private float alpha;
        private int bHeight;
        private int bWidth;
        private int bX;
        private int bY;
        private final Paint blackPaint = new Paint();
        /* access modifiers changed from: private */
        public boolean closeAnimationInProgress;
        private float innerTranslationX;
        private boolean maybeStartTracking;
        /* access modifiers changed from: private */
        public boolean movingPage;
        private boolean selfLayout;
        /* access modifiers changed from: private */
        public int startMovingHeaderHeight;
        /* access modifiers changed from: private */
        public boolean startedTracking;
        private int startedTrackingPointerId;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker tracker;

        public WindowView(Context context) {
            super(context);
        }

        @TargetApi(21)
        public WindowInsets dispatchApplyWindowInsets(WindowInsets windowInsets) {
            DisplayCutout displayCutout;
            List<Rect> boundingRects;
            WindowInsets windowInsets2 = (WindowInsets) ArticleViewer.this.lastInsets;
            Object unused = ArticleViewer.this.lastInsets = windowInsets;
            if ((windowInsets2 == null || !windowInsets2.toString().equals(windowInsets.toString())) && ArticleViewer.this.windowView != null) {
                ArticleViewer.this.windowView.requestLayout();
            }
            if (!(Build.VERSION.SDK_INT < 28 || ArticleViewer.this.parentActivity == null || (displayCutout = ArticleViewer.this.parentActivity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout()) == null || (boundingRects = displayCutout.getBoundingRects()) == null || boundingRects.isEmpty())) {
                ArticleViewer articleViewer = ArticleViewer.this;
                boolean z = false;
                if (boundingRects.get(0).height() != 0) {
                    z = true;
                }
                boolean unused2 = articleViewer.hasCutout = z;
            }
            return super.dispatchApplyWindowInsets(windowInsets);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            int i3 = Build.VERSION.SDK_INT;
            if (i3 < 21 || ArticleViewer.this.lastInsets == null) {
                setMeasuredDimension(size, size2);
            } else {
                setMeasuredDimension(size, size2);
                WindowInsets windowInsets = (WindowInsets) ArticleViewer.this.lastInsets;
                if (AndroidUtilities.incorrectDisplaySizeFix) {
                    int i4 = AndroidUtilities.displaySize.y;
                    if (size2 > i4) {
                        size2 = i4;
                    }
                    size2 += AndroidUtilities.statusBarHeight;
                }
                int systemWindowInsetBottom = size2 - windowInsets.getSystemWindowInsetBottom();
                size -= windowInsets.getSystemWindowInsetRight() + windowInsets.getSystemWindowInsetLeft();
                if (windowInsets.getSystemWindowInsetRight() != 0) {
                    this.bWidth = windowInsets.getSystemWindowInsetRight();
                    this.bHeight = systemWindowInsetBottom;
                } else if (windowInsets.getSystemWindowInsetLeft() != 0) {
                    this.bWidth = windowInsets.getSystemWindowInsetLeft();
                    this.bHeight = systemWindowInsetBottom;
                } else {
                    this.bWidth = size;
                    this.bHeight = windowInsets.getStableInsetBottom();
                }
                size2 = systemWindowInsetBottom - windowInsets.getSystemWindowInsetTop();
            }
            boolean z = false;
            ArticleViewer.this.menuButton.setAdditionalYOffset(((-(ArticleViewer.this.currentHeaderHeight - AndroidUtilities.dp(56.0f))) / 2) + (i3 < 21 ? AndroidUtilities.statusBarHeight : 0));
            ArticleViewer articleViewer = ArticleViewer.this;
            if (size2 < AndroidUtilities.displaySize.y - AndroidUtilities.dp(100.0f)) {
                z = true;
            }
            boolean unused = articleViewer.keyboardVisible = z;
            ArticleViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
            ArticleViewer.this.fullscreenVideoContainer.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
        }

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (ArticleViewer.this.pinchToZoomHelper.isInOverlayMode()) {
                motionEvent.offsetLocation(-ArticleViewer.this.containerView.getX(), -ArticleViewer.this.containerView.getY());
                return ArticleViewer.this.pinchToZoomHelper.onTouchEvent(motionEvent);
            }
            TextSelectionHelper<Cell>.TextSelectionOverlay overlayView = ArticleViewer.this.textSelectionHelper.getOverlayView(getContext());
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

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            if (!this.selfLayout) {
                int i6 = i3 - i;
                int i7 = 0;
                if (ArticleViewer.this.anchorsOffsetMeasuredWidth != i6) {
                    for (int i8 = 0; i8 < ArticleViewer.this.listView.length; i8++) {
                        for (Map.Entry value : ArticleViewer.this.adapter[i8].anchorsOffset.entrySet()) {
                            value.setValue(-1);
                        }
                    }
                    int unused = ArticleViewer.this.anchorsOffsetMeasuredWidth = i6;
                }
                if (Build.VERSION.SDK_INT < 21 || ArticleViewer.this.lastInsets == null) {
                    i5 = 0;
                } else {
                    WindowInsets windowInsets = (WindowInsets) ArticleViewer.this.lastInsets;
                    int systemWindowInsetLeft = windowInsets.getSystemWindowInsetLeft();
                    if (windowInsets.getSystemWindowInsetRight() != 0) {
                        this.bX = i6 - this.bWidth;
                        this.bY = 0;
                    } else if (windowInsets.getSystemWindowInsetLeft() != 0) {
                        this.bX = 0;
                        this.bY = 0;
                    } else {
                        this.bX = 0;
                        this.bY = (i4 - i2) - this.bHeight;
                    }
                    i5 = 0 + windowInsets.getSystemWindowInsetTop();
                    i7 = systemWindowInsetLeft;
                }
                ArticleViewer.this.containerView.layout(i7, i5, ArticleViewer.this.containerView.getMeasuredWidth() + i7, ArticleViewer.this.containerView.getMeasuredHeight() + i5);
                ArticleViewer.this.fullscreenVideoContainer.layout(i7, i5, ArticleViewer.this.fullscreenVideoContainer.getMeasuredWidth() + i7, ArticleViewer.this.fullscreenVideoContainer.getMeasuredHeight() + i5);
                if (ArticleViewer.this.runAfterKeyboardClose != null) {
                    ArticleViewer.this.runAfterKeyboardClose.start();
                    AnimatorSet unused2 = ArticleViewer.this.runAfterKeyboardClose = null;
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            boolean unused = ArticleViewer.this.attachedToWindow = true;
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            boolean unused = ArticleViewer.this.attachedToWindow = false;
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            handleTouchEvent((MotionEvent) null);
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
                ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent((ArticleViewer.this.isVisible && this.alpha == 1.0f && this.innerTranslationX == 0.0f) ? false : true);
            }
            invalidate();
        }

        /* access modifiers changed from: protected */
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
                float max = Math.max(0.0f, Math.min(f / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                ArticleViewer.this.layerShadowDrawable.setBounds(i - ArticleViewer.this.layerShadowDrawable.getIntrinsicWidth(), view.getTop(), i, view.getBottom());
                ArticleViewer.this.layerShadowDrawable.setAlpha((int) (max * 255.0f));
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
                ArticleViewer articleViewer = ArticleViewer.this;
                articleViewer.updateInterfaceForCurrentPage((TLRPC$WebPage) articleViewer.pagesStack.get(ArticleViewer.this.pagesStack.size() - 2), true, -1);
            } else {
                this.movingPage = false;
            }
            ArticleViewer.this.cancelCheckLongPress();
        }

        public boolean handleTouchEvent(MotionEvent motionEvent) {
            if (ArticleViewer.this.pageSwitchAnimation != null || this.closeAnimationInProgress || ArticleViewer.this.fullscreenVideoContainer.getVisibility() == 0 || ArticleViewer.this.textSelectionHelper.isSelectionMode()) {
                return false;
            }
            if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                this.startedTrackingY = (int) motionEvent.getY();
                VelocityTracker velocityTracker = this.tracker;
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
                    DrawingText unused = ArticleViewer.this.pressedLinkOwnerLayout = null;
                    View unused2 = ArticleViewer.this.pressedLinkOwnerView = null;
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
                    if (!z) {
                        x = ((float) access$2500.getMeasuredWidth()) - x;
                        if (this.movingPage) {
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ArticleViewer.this.listView[0], View.TRANSLATION_X, new float[]{(float) access$2500.getMeasuredWidth()})});
                        } else {
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ArticleViewer.this.containerView, View.TRANSLATION_X, new float[]{(float) access$2500.getMeasuredWidth()}), ObjectAnimator.ofFloat(this, ArticleViewer.ARTICLE_VIEWER_INNER_TRANSLATION_X, new float[]{(float) access$2500.getMeasuredWidth()})});
                        }
                    } else if (this.movingPage) {
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ArticleViewer.this.listView[0], View.TRANSLATION_X, new float[]{0.0f})});
                    } else {
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ArticleViewer.this.containerView, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this, ArticleViewer.ARTICLE_VIEWER_INNER_TRANSLATION_X, new float[]{0.0f})});
                    }
                    animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) access$2500.getMeasuredWidth())) * x), 50));
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (WindowView.this.movingPage) {
                                ArticleViewer.this.listView[0].setBackgroundDrawable((Drawable) null);
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
                                    articleViewer.textSelectionHelper.setParentView(articleViewer.listView[0]);
                                    ArticleViewer articleViewer2 = ArticleViewer.this;
                                    articleViewer2.textSelectionHelper.layoutManager = articleViewer2.layoutManager[0];
                                    ArticleViewer.this.titleTextView.setText(ArticleViewer.this.adapter[0].currentPage.site_name == null ? "" : ArticleViewer.this.adapter[0].currentPage.site_name);
                                    ArticleViewer.this.textSelectionHelper.clear(true);
                                    ArticleViewer.this.headerView.invalidate();
                                }
                                ArticleViewer.this.listView[1].setVisibility(8);
                                ArticleViewer.this.headerView.invalidate();
                            } else if (!z) {
                                ArticleViewer.this.saveCurrentPagePosition();
                                ArticleViewer.this.onClosed();
                            }
                            boolean unused = WindowView.this.movingPage = false;
                            boolean unused2 = WindowView.this.startedTracking = false;
                            boolean unused3 = WindowView.this.closeAnimationInProgress = false;
                        }
                    });
                    animatorSet.start();
                    this.closeAnimationInProgress = true;
                } else {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                    this.movingPage = false;
                }
                VelocityTracker velocityTracker2 = this.tracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.recycle();
                    this.tracker = null;
                }
            } else if (motionEvent == null) {
                this.maybeStartTracking = false;
                this.startedTracking = false;
                this.movingPage = false;
                VelocityTracker velocityTracker3 = this.tracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.recycle();
                    this.tracker = null;
                }
                TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = ArticleViewer.this.textSelectionHelper;
                if (articleTextSelectionHelper != null && !articleTextSelectionHelper.isSelectionMode()) {
                    ArticleViewer.this.textSelectionHelper.clear();
                }
            }
            return this.startedTracking;
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            int i;
            super.dispatchDraw(canvas);
            if (this.bWidth != 0 && this.bHeight != 0) {
                this.blackPaint.setAlpha((int) (ArticleViewer.this.windowView.getAlpha() * 255.0f));
                int i2 = this.bX;
                if (i2 == 0 && (i = this.bY) == 0) {
                    canvas.drawRect((float) i2, (float) i, (float) (i2 + this.bWidth), (float) (i + this.bHeight), this.blackPaint);
                    return;
                }
                canvas.drawRect(((float) i2) - getTranslationX(), (float) this.bY, ((float) (this.bX + this.bWidth)) - getTranslationX(), (float) (this.bY + this.bHeight), this.blackPaint);
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            float f = (float) measuredWidth;
            float f2 = (float) measuredHeight;
            canvas.drawRect(this.innerTranslationX, 0.0f, f, f2, ArticleViewer.this.backgroundPaint);
            if (Build.VERSION.SDK_INT >= 21 && ArticleViewer.this.lastInsets != null) {
                WindowInsets windowInsets = (WindowInsets) ArticleViewer.this.lastInsets;
                canvas.drawRect(this.innerTranslationX, 0.0f, f, (float) windowInsets.getSystemWindowInsetTop(), ArticleViewer.this.statusBarPaint);
                if (ArticleViewer.this.hasCutout) {
                    int systemWindowInsetLeft = windowInsets.getSystemWindowInsetLeft();
                    if (systemWindowInsetLeft != 0) {
                        canvas.drawRect(0.0f, 0.0f, (float) systemWindowInsetLeft, f2, ArticleViewer.this.statusBarPaint);
                    }
                    int systemWindowInsetRight = windowInsets.getSystemWindowInsetRight();
                    if (systemWindowInsetRight != 0) {
                        canvas.drawRect((float) (getMeasuredWidth() - systemWindowInsetRight), 0.0f, (float) getMeasuredWidth(), f2, ArticleViewer.this.statusBarPaint);
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
                ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent((ArticleViewer.this.isVisible && this.alpha == 1.0f && this.innerTranslationX == 0.0f) ? false : true);
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

    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0082, code lost:
            r0 = r6.this$0;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r6 = this;
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                boolean r0 = r0.checkingForLongPress
                if (r0 == 0) goto L_0x0117
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WindowView r0 = r0.windowView
                if (r0 == 0) goto L_0x0117
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r1 = 0
                boolean unused = r0.checkingForLongPress = r1
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Components.TextPaintUrlSpan r0 = r0.pressedLink
                if (r0 == 0) goto L_0x0052
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WindowView r0 = r0.windowView
                r0.performHapticFeedback(r1)
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Components.TextPaintUrlSpan r1 = r0.pressedLink
                java.lang.String r1 = r1.getUrl()
                r0.showCopyPopup(r1)
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r1 = 0
                org.telegram.ui.Components.TextPaintUrlSpan unused = r0.pressedLink = r1
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer.DrawingText unused = r0.pressedLinkOwnerLayout = r1
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                android.view.View r0 = r0.pressedLinkOwnerView
                if (r0 == 0) goto L_0x0117
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                android.view.View r0 = r0.pressedLinkOwnerView
                r0.invalidate()
                goto L_0x0117
            L_0x0052:
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                android.view.View r0 = r0.pressedLinkOwnerView
                if (r0 == 0) goto L_0x00af
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r2 = r0.textSelectionHelper
                android.view.View r0 = r0.pressedLinkOwnerView
                boolean r0 = r2.isSelectable(r0)
                if (r0 == 0) goto L_0x00af
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                android.view.View r0 = r0.pressedLinkOwnerView
                java.lang.Object r0 = r0.getTag()
                if (r0 == 0) goto L_0x0090
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                android.view.View r0 = r0.pressedLinkOwnerView
                java.lang.Object r0 = r0.getTag()
                java.lang.String r2 = "bottomSheet"
                if (r0 != r2) goto L_0x0090
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r2 = r0.textSelectionHelperBottomSheet
                if (r2 == 0) goto L_0x0090
                android.view.View r0 = r0.pressedLinkOwnerView
                r2.trySelect(r0)
                goto L_0x009b
            L_0x0090:
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r2 = r0.textSelectionHelper
                android.view.View r0 = r0.pressedLinkOwnerView
                r2.trySelect(r0)
            L_0x009b:
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r0 = r0.textSelectionHelper
                boolean r0 = r0.isSelectionMode()
                if (r0 == 0) goto L_0x0117
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WindowView r0 = r0.windowView
                r0.performHapticFeedback(r1)
                goto L_0x0117
            L_0x00af:
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.pressedLinkOwnerLayout
                if (r0 == 0) goto L_0x0117
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                android.view.View r0 = r0.pressedLinkOwnerView
                if (r0 == 0) goto L_0x0117
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WindowView r0 = r0.windowView
                r0.performHapticFeedback(r1)
                r0 = 2
                int[] r0 = new int[r0]
                org.telegram.ui.ArticleViewer r2 = org.telegram.ui.ArticleViewer.this
                android.view.View r2 = r2.pressedLinkOwnerView
                r2.getLocationInWindow(r0)
                r2 = 1
                r0 = r0[r2]
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                int r3 = r3.pressedLayoutY
                int r0 = r0 + r3
                r3 = 1113063424(0x42580000, float:54.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r0 = r0 - r3
                if (r0 >= 0) goto L_0x00e8
                r0 = 0
            L_0x00e8:
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                android.view.View r3 = r3.pressedLinkOwnerView
                r3.invalidate()
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                boolean unused = r3.drawBlockSelection = r2
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                android.view.View r4 = r3.pressedLinkOwnerView
                r5 = 48
                r3.showPopup(r4, r5, r1, r0)
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Components.RecyclerListView[] r0 = r0.listView
                r0 = r0[r1]
                r0.setLayoutFrozen(r2)
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Components.RecyclerListView[] r0 = r0.listView
                r0 = r0[r1]
                r0.setLayoutFrozen(r1)
            L_0x0117:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.CheckForLongPress.run():void");
        }
    }

    private void createPaint(boolean z) {
        if (quoteLinePaint == null) {
            quoteLinePaint = new Paint();
            preformattedBackgroundPaint = new Paint();
            Paint paint = new Paint(1);
            tableLinePaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            tableLinePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
            Paint paint2 = new Paint();
            tableHalfLinePaint = paint2;
            paint2.setStyle(Paint.Style.STROKE);
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
        webpageUrlPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkSelection") & NUM);
        urlPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkSelection") & NUM);
        tableHalfLinePaint.setColor(Theme.getColor("windowBackgroundWhiteInputField"));
        tableLinePaint.setColor(Theme.getColor("windowBackgroundWhiteInputField"));
        photoBackgroundPaint.setColor(NUM);
        dividerPaint.setColor(Theme.getColor("divider"));
        webpageMarkPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkSelection") & NUM);
        int color2 = Theme.getColor("switchTrack");
        int red = Color.red(color2);
        int green = Color.green(color2);
        int blue = Color.blue(color2);
        tableStripPaint.setColor(Color.argb(20, red, green, blue));
        tableHeaderPaint.setColor(Color.argb(34, red, green, blue));
        int color3 = Theme.getColor("windowBackgroundWhiteLinkSelection");
        preformattedBackgroundPaint.setColor(Color.argb(20, Color.red(color3), Color.green(color3), Color.blue(color3)));
        quoteLinePaint.setColor(Theme.getColor("chat_inReplyLine"));
    }

    /* access modifiers changed from: private */
    public void showCopyPopup(String str) {
        if (this.parentActivity != null) {
            BottomSheet bottomSheet = this.linkSheet;
            if (bottomSheet != null) {
                bottomSheet.dismiss();
                this.linkSheet = null;
            }
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
            builder.setTitle(str);
            builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new DialogInterface.OnClickListener(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ArticleViewer.this.lambda$showCopyPopup$0$ArticleViewer(this.f$1, dialogInterface, i);
                }
            });
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showCopyPopup$0 */
    public /* synthetic */ void lambda$showCopyPopup$0$ArticleViewer(String str, DialogInterface dialogInterface, int i) {
        String str2;
        String str3;
        if (this.parentActivity != null) {
            if (i == 0) {
                int lastIndexOf = str.lastIndexOf(35);
                if (lastIndexOf != -1) {
                    if (!TextUtils.isEmpty(this.adapter[0].currentPage.cached_page.url)) {
                        str2 = this.adapter[0].currentPage.cached_page.url.toLowerCase();
                    } else {
                        str2 = this.adapter[0].currentPage.url.toLowerCase();
                    }
                    try {
                        str3 = URLDecoder.decode(str.substring(lastIndexOf + 1), "UTF-8");
                    } catch (Exception unused) {
                        str3 = "";
                    }
                    if (str.toLowerCase().contains(str2)) {
                        if (TextUtils.isEmpty(str3)) {
                            this.layoutManager[0].scrollToPositionWithOffset(0, 0);
                            checkScrollAnimated();
                            return;
                        }
                        scrollToAnchor(str3);
                        return;
                    }
                }
                Browser.openUrl((Context) this.parentActivity, str);
            } else if (i == 1) {
                if (str.startsWith("mailto:")) {
                    str = str.substring(7);
                } else if (str.startsWith("tel:")) {
                    str = str.substring(4);
                }
                AndroidUtilities.addToClipboard(str);
            }
        }
    }

    /* access modifiers changed from: private */
    public void showPopup(View view, int i, int i2, int i3) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity);
                this.popupLayout = actionBarPopupWindowLayout;
                actionBarPopupWindowLayout.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                this.popupLayout.setBackgroundDrawable(this.parentActivity.getResources().getDrawable(NUM));
                this.popupLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new View.OnTouchListener() {
                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        return ArticleViewer.this.lambda$showPopup$1$ArticleViewer(view, motionEvent);
                    }
                });
                this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                    public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                        ArticleViewer.this.lambda$showPopup$2$ArticleViewer(keyEvent);
                    }
                });
                this.popupLayout.setShownFromBotton(false);
                TextView textView = new TextView(this.parentActivity);
                this.deleteView = textView;
                textView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 2));
                this.deleteView.setGravity(16);
                this.deleteView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
                this.deleteView.setTextSize(1, 15.0f);
                this.deleteView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.deleteView.setText(LocaleController.getString("Copy", NUM).toUpperCase());
                this.deleteView.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        ArticleViewer.this.lambda$showPopup$3$ArticleViewer(view);
                    }
                });
                this.popupLayout.addView(this.deleteView, LayoutHelper.createFrame(-2, 48.0f));
                ActionBarPopupWindow actionBarPopupWindow2 = new ActionBarPopupWindow(this.popupLayout, -2, -2);
                this.popupWindow = actionBarPopupWindow2;
                actionBarPopupWindow2.setAnimationEnabled(false);
                this.popupWindow.setAnimationStyle(NUM);
                this.popupWindow.setOutsideTouchable(true);
                this.popupWindow.setClippingEnabled(true);
                this.popupWindow.setInputMethodMode(2);
                this.popupWindow.setSoftInputMode(0);
                this.popupWindow.getContentView().setFocusableInTouchMode(true);
                this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    public final void onDismiss() {
                        ArticleViewer.this.lambda$showPopup$4$ArticleViewer();
                    }
                });
            }
            this.deleteView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = this.popupLayout;
            if (actionBarPopupWindowLayout2 != null) {
                actionBarPopupWindowLayout2.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
            }
            this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.popupWindow.setFocusable(true);
            this.popupWindow.showAtLocation(view, i, i2, i3);
            this.popupWindow.startAnimation();
            return;
        }
        this.popupWindow.dismiss();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPopup$1 */
    public /* synthetic */ boolean lambda$showPopup$1$ArticleViewer(View view, MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (motionEvent.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        view.getHitRect(this.popupRect);
        if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            return false;
        }
        this.popupWindow.dismiss();
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPopup$2 */
    public /* synthetic */ void lambda$showPopup$2$ArticleViewer(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPopup$3 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPopup$4 */
    public /* synthetic */ void lambda$showPopup$4$ArticleViewer() {
        View view = this.pressedLinkOwnerView;
        if (view != null) {
            this.pressedLinkOwnerLayout = null;
            view.invalidate();
            this.pressedLinkOwnerView = null;
        }
    }

    /* access modifiers changed from: private */
    public TLRPC$RichText getBlockCaption(TLRPC$PageBlock tLRPC$PageBlock, int i) {
        if (i == 2) {
            TLRPC$RichText blockCaption = getBlockCaption(tLRPC$PageBlock, 0);
            if (blockCaption instanceof TLRPC$TL_textEmpty) {
                blockCaption = null;
            }
            TLRPC$RichText blockCaption2 = getBlockCaption(tLRPC$PageBlock, 1);
            if (blockCaption2 instanceof TLRPC$TL_textEmpty) {
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
            TLRPC$TL_textPlain tLRPC$TL_textPlain = new TLRPC$TL_textPlain();
            tLRPC$TL_textPlain.text = " ";
            TLRPC$TL_textConcat tLRPC$TL_textConcat = new TLRPC$TL_textConcat();
            tLRPC$TL_textConcat.texts.add(blockCaption);
            tLRPC$TL_textConcat.texts.add(tLRPC$TL_textPlain);
            tLRPC$TL_textConcat.texts.add(blockCaption2);
            return tLRPC$TL_textConcat;
        }
        if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockEmbedPost) {
            TLRPC$TL_pageBlockEmbedPost tLRPC$TL_pageBlockEmbedPost = (TLRPC$TL_pageBlockEmbedPost) tLRPC$PageBlock;
            if (i == 0) {
                return tLRPC$TL_pageBlockEmbedPost.caption.text;
            }
            if (i == 1) {
                return tLRPC$TL_pageBlockEmbedPost.caption.credit;
            }
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSlideshow) {
            TLRPC$TL_pageBlockSlideshow tLRPC$TL_pageBlockSlideshow = (TLRPC$TL_pageBlockSlideshow) tLRPC$PageBlock;
            if (i == 0) {
                return tLRPC$TL_pageBlockSlideshow.caption.text;
            }
            if (i == 1) {
                return tLRPC$TL_pageBlockSlideshow.caption.credit;
            }
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPhoto) {
            TLRPC$TL_pageBlockPhoto tLRPC$TL_pageBlockPhoto = (TLRPC$TL_pageBlockPhoto) tLRPC$PageBlock;
            if (i == 0) {
                return tLRPC$TL_pageBlockPhoto.caption.text;
            }
            if (i == 1) {
                return tLRPC$TL_pageBlockPhoto.caption.credit;
            }
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockCollage) {
            TLRPC$TL_pageBlockCollage tLRPC$TL_pageBlockCollage = (TLRPC$TL_pageBlockCollage) tLRPC$PageBlock;
            if (i == 0) {
                return tLRPC$TL_pageBlockCollage.caption.text;
            }
            if (i == 1) {
                return tLRPC$TL_pageBlockCollage.caption.credit;
            }
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockEmbed) {
            TLRPC$TL_pageBlockEmbed tLRPC$TL_pageBlockEmbed = (TLRPC$TL_pageBlockEmbed) tLRPC$PageBlock;
            if (i == 0) {
                return tLRPC$TL_pageBlockEmbed.caption.text;
            }
            if (i == 1) {
                return tLRPC$TL_pageBlockEmbed.caption.credit;
            }
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockBlockquote) {
            return ((TLRPC$TL_pageBlockBlockquote) tLRPC$PageBlock).caption;
        } else {
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockVideo) {
                TLRPC$TL_pageBlockVideo tLRPC$TL_pageBlockVideo = (TLRPC$TL_pageBlockVideo) tLRPC$PageBlock;
                if (i == 0) {
                    return tLRPC$TL_pageBlockVideo.caption.text;
                }
                if (i == 1) {
                    return tLRPC$TL_pageBlockVideo.caption.credit;
                }
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPullquote) {
                return ((TLRPC$TL_pageBlockPullquote) tLRPC$PageBlock).caption;
            } else {
                if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockAudio) {
                    TLRPC$TL_pageBlockAudio tLRPC$TL_pageBlockAudio = (TLRPC$TL_pageBlockAudio) tLRPC$PageBlock;
                    if (i == 0) {
                        return tLRPC$TL_pageBlockAudio.caption.text;
                    }
                    if (i == 1) {
                        return tLRPC$TL_pageBlockAudio.caption.credit;
                    }
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockCover) {
                    return getBlockCaption(((TLRPC$TL_pageBlockCover) tLRPC$PageBlock).cover, i);
                } else {
                    if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockMap) {
                        TLRPC$TL_pageBlockMap tLRPC$TL_pageBlockMap = (TLRPC$TL_pageBlockMap) tLRPC$PageBlock;
                        if (i == 0) {
                            return tLRPC$TL_pageBlockMap.caption.text;
                        }
                        if (i == 1) {
                            return tLRPC$TL_pageBlockMap.caption.credit;
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
            return view;
        } else if (!(view instanceof BlockOrderedListItemCell)) {
            return view;
        } else {
            BlockOrderedListItemCell blockOrderedListItemCell = (BlockOrderedListItemCell) view;
            return blockOrderedListItemCell.blockLayout != null ? getLastNonListCell(blockOrderedListItemCell.blockLayout.itemView) : view;
        }
    }

    /* access modifiers changed from: private */
    public boolean isListItemBlock(TLRPC$PageBlock tLRPC$PageBlock) {
        return (tLRPC$PageBlock instanceof TL_pageBlockListItem) || (tLRPC$PageBlock instanceof TL_pageBlockOrderedListItem);
    }

    /* access modifiers changed from: private */
    public TLRPC$PageBlock getLastNonListPageBlock(TLRPC$PageBlock tLRPC$PageBlock) {
        if (tLRPC$PageBlock instanceof TL_pageBlockListItem) {
            TL_pageBlockListItem tL_pageBlockListItem = (TL_pageBlockListItem) tLRPC$PageBlock;
            if (tL_pageBlockListItem.blockItem != null) {
                return getLastNonListPageBlock(tL_pageBlockListItem.blockItem);
            }
            return tL_pageBlockListItem.blockItem;
        } else if (!(tLRPC$PageBlock instanceof TL_pageBlockOrderedListItem)) {
            return tLRPC$PageBlock;
        } else {
            TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem = (TL_pageBlockOrderedListItem) tLRPC$PageBlock;
            if (tL_pageBlockOrderedListItem.blockItem != null) {
                return getLastNonListPageBlock(tL_pageBlockOrderedListItem.blockItem);
            }
            return tL_pageBlockOrderedListItem.blockItem;
        }
    }

    private boolean openAllParentBlocks(TL_pageBlockDetailsChild tL_pageBlockDetailsChild) {
        boolean z;
        TLRPC$PageBlock lastNonListPageBlock = getLastNonListPageBlock(tL_pageBlockDetailsChild.parent);
        if (lastNonListPageBlock instanceof TLRPC$TL_pageBlockDetails) {
            TLRPC$TL_pageBlockDetails tLRPC$TL_pageBlockDetails = (TLRPC$TL_pageBlockDetails) lastNonListPageBlock;
            if (tLRPC$TL_pageBlockDetails.open) {
                return false;
            }
            tLRPC$TL_pageBlockDetails.open = true;
            return true;
        } else if (!(lastNonListPageBlock instanceof TL_pageBlockDetailsChild)) {
            return false;
        } else {
            TL_pageBlockDetailsChild tL_pageBlockDetailsChild2 = (TL_pageBlockDetailsChild) lastNonListPageBlock;
            TLRPC$PageBlock lastNonListPageBlock2 = getLastNonListPageBlock(tL_pageBlockDetailsChild2.block);
            if (lastNonListPageBlock2 instanceof TLRPC$TL_pageBlockDetails) {
                TLRPC$TL_pageBlockDetails tLRPC$TL_pageBlockDetails2 = (TLRPC$TL_pageBlockDetails) lastNonListPageBlock2;
                if (!tLRPC$TL_pageBlockDetails2.open) {
                    tLRPC$TL_pageBlockDetails2.open = true;
                    z = true;
                    if (!openAllParentBlocks(tL_pageBlockDetailsChild2) || z) {
                        return true;
                    }
                    return false;
                }
            }
            z = false;
            if (!openAllParentBlocks(tL_pageBlockDetailsChild2)) {
            }
            return true;
        }
    }

    /* access modifiers changed from: private */
    public TLRPC$PageBlock fixListBlock(TLRPC$PageBlock tLRPC$PageBlock, TLRPC$PageBlock tLRPC$PageBlock2) {
        if (tLRPC$PageBlock instanceof TL_pageBlockListItem) {
            TLRPC$PageBlock unused = ((TL_pageBlockListItem) tLRPC$PageBlock).blockItem = tLRPC$PageBlock2;
            return tLRPC$PageBlock;
        } else if (!(tLRPC$PageBlock instanceof TL_pageBlockOrderedListItem)) {
            return tLRPC$PageBlock2;
        } else {
            TLRPC$PageBlock unused2 = ((TL_pageBlockOrderedListItem) tLRPC$PageBlock).blockItem = tLRPC$PageBlock2;
            return tLRPC$PageBlock;
        }
    }

    /* access modifiers changed from: private */
    public TLRPC$PageBlock wrapInTableBlock(TLRPC$PageBlock tLRPC$PageBlock, TLRPC$PageBlock tLRPC$PageBlock2) {
        if (tLRPC$PageBlock instanceof TL_pageBlockListItem) {
            TL_pageBlockListItem tL_pageBlockListItem = (TL_pageBlockListItem) tLRPC$PageBlock;
            TL_pageBlockListItem tL_pageBlockListItem2 = new TL_pageBlockListItem();
            TL_pageBlockListParent unused = tL_pageBlockListItem2.parent = tL_pageBlockListItem.parent;
            TLRPC$PageBlock unused2 = tL_pageBlockListItem2.blockItem = wrapInTableBlock(tL_pageBlockListItem.blockItem, tLRPC$PageBlock2);
            return tL_pageBlockListItem2;
        } else if (!(tLRPC$PageBlock instanceof TL_pageBlockOrderedListItem)) {
            return tLRPC$PageBlock2;
        } else {
            TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem = (TL_pageBlockOrderedListItem) tLRPC$PageBlock;
            TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem2 = new TL_pageBlockOrderedListItem();
            TL_pageBlockOrderedListParent unused3 = tL_pageBlockOrderedListItem2.parent = tL_pageBlockOrderedListItem.parent;
            TLRPC$PageBlock unused4 = tL_pageBlockOrderedListItem2.blockItem = wrapInTableBlock(tL_pageBlockOrderedListItem.blockItem, tLRPC$PageBlock2);
            return tL_pageBlockOrderedListItem2;
        }
    }

    /* JADX WARNING: type inference failed for: r14v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateInterfaceForCurrentPage(org.telegram.tgnet.TLRPC$WebPage r13, boolean r14, int r15) {
        /*
            r12 = this;
            if (r13 == 0) goto L_0x026b
            org.telegram.tgnet.TLRPC$Page r0 = r13.cached_page
            if (r0 != 0) goto L_0x0008
            goto L_0x026b
        L_0x0008:
            r0 = -1
            r1 = 2
            r2 = 1
            r3 = 0
            if (r14 != 0) goto L_0x012c
            if (r15 == 0) goto L_0x012c
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r4 = r12.adapter
            r5 = r4[r2]
            r6 = r4[r3]
            r4[r2] = r6
            r4[r3] = r5
            org.telegram.ui.Components.RecyclerListView[] r4 = r12.listView
            r5 = r4[r2]
            r6 = r4[r3]
            r4[r2] = r6
            r4[r3] = r5
            androidx.recyclerview.widget.LinearLayoutManager[] r5 = r12.layoutManager
            r6 = r5[r2]
            r7 = r5[r3]
            r5[r2] = r7
            r5[r3] = r6
            android.widget.FrameLayout r5 = r12.containerView
            r4 = r4[r3]
            int r4 = r5.indexOfChild(r4)
            android.widget.FrameLayout r5 = r12.containerView
            org.telegram.ui.Components.RecyclerListView[] r6 = r12.listView
            r6 = r6[r2]
            int r5 = r5.indexOfChild(r6)
            if (r15 != r2) goto L_0x0057
            if (r4 >= r5) goto L_0x006b
            android.widget.FrameLayout r4 = r12.containerView
            org.telegram.ui.Components.RecyclerListView[] r6 = r12.listView
            r6 = r6[r3]
            r4.removeView(r6)
            android.widget.FrameLayout r4 = r12.containerView
            org.telegram.ui.Components.RecyclerListView[] r6 = r12.listView
            r6 = r6[r3]
            r4.addView(r6, r5)
            goto L_0x006b
        L_0x0057:
            if (r5 >= r4) goto L_0x006b
            android.widget.FrameLayout r5 = r12.containerView
            org.telegram.ui.Components.RecyclerListView[] r6 = r12.listView
            r6 = r6[r3]
            r5.removeView(r6)
            android.widget.FrameLayout r5 = r12.containerView
            org.telegram.ui.Components.RecyclerListView[] r6 = r12.listView
            r6 = r6[r3]
            r5.addView(r6, r4)
        L_0x006b:
            android.animation.AnimatorSet r4 = new android.animation.AnimatorSet
            r4.<init>()
            r12.pageSwitchAnimation = r4
            org.telegram.ui.Components.RecyclerListView[] r4 = r12.listView
            r4 = r4[r3]
            r4.setVisibility(r3)
            if (r15 != r2) goto L_0x007d
            r4 = 0
            goto L_0x007e
        L_0x007d:
            r4 = 1
        L_0x007e:
            org.telegram.ui.Components.RecyclerListView[] r5 = r12.listView
            r5 = r5[r4]
            android.graphics.Paint r6 = r12.backgroundPaint
            int r6 = r6.getColor()
            r5.setBackgroundColor(r6)
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 18
            if (r5 < r6) goto L_0x0099
            org.telegram.ui.Components.RecyclerListView[] r5 = r12.listView
            r5 = r5[r4]
            r6 = 0
            r5.setLayerType(r1, r6)
        L_0x0099:
            r5 = 1113587712(0x42600000, float:56.0)
            r6 = 0
            if (r15 != r2) goto L_0x00ce
            android.animation.AnimatorSet r7 = r12.pageSwitchAnimation
            android.animation.Animator[] r8 = new android.animation.Animator[r1]
            org.telegram.ui.Components.RecyclerListView[] r9 = r12.listView
            r9 = r9[r3]
            android.util.Property r10 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r1]
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r11[r3] = r5
            r11[r2] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r9, r10, r11)
            r8[r3] = r5
            org.telegram.ui.Components.RecyclerListView[] r5 = r12.listView
            r5 = r5[r3]
            android.util.Property r6 = android.view.View.ALPHA
            float[] r9 = new float[r1]
            r9 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r9)
            r8[r2] = r5
            r7.playTogether(r8)
            goto L_0x010f
        L_0x00ce:
            if (r15 != r0) goto L_0x010f
            org.telegram.ui.Components.RecyclerListView[] r7 = r12.listView
            r7 = r7[r3]
            r8 = 1065353216(0x3var_, float:1.0)
            r7.setAlpha(r8)
            org.telegram.ui.Components.RecyclerListView[] r7 = r12.listView
            r7 = r7[r3]
            r7.setTranslationX(r6)
            android.animation.AnimatorSet r7 = r12.pageSwitchAnimation
            android.animation.Animator[] r8 = new android.animation.Animator[r1]
            org.telegram.ui.Components.RecyclerListView[] r9 = r12.listView
            r9 = r9[r2]
            android.util.Property r10 = android.view.View.TRANSLATION_X
            float[] r11 = new float[r1]
            r11[r3] = r6
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r11[r2] = r5
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r9, r10, r11)
            r8[r3] = r5
            org.telegram.ui.Components.RecyclerListView[] r5 = r12.listView
            r5 = r5[r2]
            android.util.Property r6 = android.view.View.ALPHA
            float[] r9 = new float[r1]
            r9 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r9)
            r8[r2] = r5
            r7.playTogether(r8)
        L_0x010f:
            android.animation.AnimatorSet r5 = r12.pageSwitchAnimation
            r6 = 150(0x96, double:7.4E-322)
            r5.setDuration(r6)
            android.animation.AnimatorSet r5 = r12.pageSwitchAnimation
            android.view.animation.DecelerateInterpolator r6 = r12.interpolator
            r5.setInterpolator(r6)
            android.animation.AnimatorSet r5 = r12.pageSwitchAnimation
            org.telegram.ui.ArticleViewer$2 r6 = new org.telegram.ui.ArticleViewer$2
            r6.<init>(r4)
            r5.addListener(r6)
            android.animation.AnimatorSet r4 = r12.pageSwitchAnimation
            r4.start()
        L_0x012c:
            if (r14 != 0) goto L_0x0143
            org.telegram.ui.ActionBar.SimpleTextView r4 = r12.titleTextView
            java.lang.String r5 = r13.site_name
            if (r5 != 0) goto L_0x0136
            java.lang.String r5 = ""
        L_0x0136:
            r4.setText(r5)
            org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r4 = r12.textSelectionHelper
            r4.clear(r2)
            android.widget.FrameLayout r4 = r12.headerView
            r4.invalidate()
        L_0x0143:
            if (r14 == 0) goto L_0x0153
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WebPage> r4 = r12.pagesStack
            int r5 = r4.size()
            int r5 = r5 - r1
            java.lang.Object r1 = r4.get(r5)
            org.telegram.tgnet.TLRPC$WebPage r1 = (org.telegram.tgnet.TLRPC$WebPage) r1
            goto L_0x0154
        L_0x0153:
            r1 = r13
        L_0x0154:
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r4 = r12.adapter
            r4 = r4[r14]
            org.telegram.tgnet.TLRPC$Page r13 = r13.cached_page
            boolean r13 = r13.rtl
            boolean unused = r4.isRtl = r13
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r13 = r12.adapter
            r13 = r13[r14]
            r13.cleanup()
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r13 = r12.adapter
            r13 = r13[r14]
            org.telegram.tgnet.TLRPC$WebPage unused = r13.currentPage = r1
            org.telegram.tgnet.TLRPC$Page r13 = r1.cached_page
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r13 = r13.blocks
            int r13 = r13.size()
            r4 = 0
        L_0x0176:
            if (r4 >= r13) goto L_0x01de
            org.telegram.tgnet.TLRPC$Page r5 = r1.cached_page
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r5 = r5.blocks
            java.lang.Object r5 = r5.get(r4)
            r8 = r5
            org.telegram.tgnet.TLRPC$PageBlock r8 = (org.telegram.tgnet.TLRPC$PageBlock) r8
            if (r4 != 0) goto L_0x01bc
            r8.first = r2
            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockCover
            if (r5 == 0) goto L_0x01c9
            r5 = r8
            org.telegram.tgnet.TLRPC$TL_pageBlockCover r5 = (org.telegram.tgnet.TLRPC$TL_pageBlockCover) r5
            org.telegram.tgnet.TLRPC$RichText r6 = r12.getBlockCaption(r5, r3)
            org.telegram.tgnet.TLRPC$RichText r5 = r12.getBlockCaption(r5, r2)
            if (r6 == 0) goto L_0x019c
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_textEmpty
            if (r6 == 0) goto L_0x01a2
        L_0x019c:
            if (r5 == 0) goto L_0x01c9
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_textEmpty
            if (r5 != 0) goto L_0x01c9
        L_0x01a2:
            if (r13 <= r2) goto L_0x01c9
            org.telegram.tgnet.TLRPC$Page r5 = r1.cached_page
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r5 = r5.blocks
            java.lang.Object r5 = r5.get(r2)
            org.telegram.tgnet.TLRPC$PageBlock r5 = (org.telegram.tgnet.TLRPC$PageBlock) r5
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockChannel
            if (r6 == 0) goto L_0x01c9
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r6 = r12.adapter
            r6 = r6[r14]
            org.telegram.tgnet.TLRPC$TL_pageBlockChannel r5 = (org.telegram.tgnet.TLRPC$TL_pageBlockChannel) r5
            org.telegram.tgnet.TLRPC$TL_pageBlockChannel unused = r6.channelBlock = r5
            goto L_0x01c9
        L_0x01bc:
            if (r4 != r2) goto L_0x01c9
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r5 = r12.adapter
            r5 = r5[r14]
            org.telegram.tgnet.TLRPC$TL_pageBlockChannel r5 = r5.channelBlock
            if (r5 == 0) goto L_0x01c9
            goto L_0x01db
        L_0x01c9:
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r5 = r12.adapter
            r6 = r5[r14]
            r7 = r5[r14]
            r9 = 0
            r10 = 0
            int r5 = r13 + -1
            if (r4 != r5) goto L_0x01d7
            r11 = r4
            goto L_0x01d8
        L_0x01d7:
            r11 = 0
        L_0x01d8:
            r6.addBlock(r7, r8, r9, r10, r11)
        L_0x01db:
            int r4 = r4 + 1
            goto L_0x0176
        L_0x01de:
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r13 = r12.adapter
            r13 = r13[r14]
            r13.notifyDataSetChanged()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WebPage> r13 = r12.pagesStack
            int r13 = r13.size()
            if (r13 == r2) goto L_0x01f8
            if (r15 != r0) goto L_0x01f0
            goto L_0x01f8
        L_0x01f0:
            androidx.recyclerview.widget.LinearLayoutManager[] r13 = r12.layoutManager
            r13 = r13[r14]
            r13.scrollToPositionWithOffset(r3, r3)
            goto L_0x0266
        L_0x01f8:
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r15 = "articles"
            android.content.SharedPreferences r13 = r13.getSharedPreferences(r15, r3)
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r4 = "article"
            r15.append(r4)
            long r4 = r1.id
            r15.append(r4)
            java.lang.String r15 = r15.toString()
            int r1 = r13.getInt(r15, r0)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r15)
            java.lang.String r5 = "r"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            boolean r4 = r13.getBoolean(r4, r2)
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r6 = r5.x
            int r5 = r5.y
            if (r6 <= r5) goto L_0x0235
            goto L_0x0236
        L_0x0235:
            r2 = 0
        L_0x0236:
            if (r4 != r2) goto L_0x0257
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r15)
            java.lang.String r15 = "o"
            r2.append(r15)
            java.lang.String r15 = r2.toString()
            int r13 = r13.getInt(r15, r3)
            org.telegram.ui.Components.RecyclerListView[] r15 = r12.listView
            r15 = r15[r14]
            int r15 = r15.getPaddingTop()
            int r13 = r13 - r15
            goto L_0x025d
        L_0x0257:
            r13 = 1092616192(0x41200000, float:10.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
        L_0x025d:
            if (r1 == r0) goto L_0x0266
            androidx.recyclerview.widget.LinearLayoutManager[] r15 = r12.layoutManager
            r15 = r15[r14]
            r15.scrollToPositionWithOffset(r1, r13)
        L_0x0266:
            if (r14 != 0) goto L_0x026b
            r12.checkScrollAnimated()
        L_0x026b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.updateInterfaceForCurrentPage(org.telegram.tgnet.TLRPC$WebPage, boolean, int):void");
    }

    private boolean addPageToStack(TLRPC$WebPage tLRPC$WebPage, String str, int i) {
        saveCurrentPagePosition();
        this.pagesStack.add(tLRPC$WebPage);
        showSearch(false);
        updateInterfaceForCurrentPage(tLRPC$WebPage, false, i);
        return scrollToAnchor(str);
    }

    private boolean scrollToAnchor(String str) {
        Integer num = 0;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String lowerCase = str.toLowerCase();
        Integer num2 = (Integer) this.adapter[0].anchors.get(lowerCase);
        if (num2 != null) {
            TLRPC$TL_textAnchor tLRPC$TL_textAnchor = (TLRPC$TL_textAnchor) this.adapter[0].anchorsParent.get(lowerCase);
            if (tLRPC$TL_textAnchor != null) {
                TLRPC$TL_pageBlockParagraph tLRPC$TL_pageBlockParagraph = new TLRPC$TL_pageBlockParagraph();
                tLRPC$TL_pageBlockParagraph.text = tLRPC$TL_textAnchor.text;
                int access$7500 = this.adapter[0].getTypeForBlock(tLRPC$TL_pageBlockParagraph);
                RecyclerView.ViewHolder onCreateViewHolder = this.adapter[0].onCreateViewHolder((ViewGroup) null, access$7500);
                this.adapter[0].bindBlockToHolder(access$7500, onCreateViewHolder, tLRPC$TL_pageBlockParagraph, 0, 0);
                BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
                builder.setApplyTopPadding(false);
                builder.setApplyBottomPadding(false);
                final LinearLayout linearLayout = new LinearLayout(this.parentActivity);
                linearLayout.setOrientation(1);
                TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = new TextSelectionHelper.ArticleTextSelectionHelper();
                this.textSelectionHelperBottomSheet = articleTextSelectionHelper;
                articleTextSelectionHelper.setParentView(linearLayout);
                this.textSelectionHelperBottomSheet.setCallback(new TextSelectionHelper.Callback() {
                    public void onStateChanged(boolean z) {
                        if (ArticleViewer.this.linkSheet != null) {
                            ArticleViewer.this.linkSheet.setDisableScroll(z);
                        }
                    }
                });
                AnonymousClass4 r3 = new TextView(this, this.parentActivity) {
                    /* access modifiers changed from: protected */
                    public void onDraw(Canvas canvas) {
                        canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
                        super.onDraw(canvas);
                    }
                };
                r3.setTextSize(1, 16.0f);
                r3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                r3.setText(LocaleController.getString("InstantViewReference", NUM));
                r3.setGravity((this.adapter[0].isRtl ? 5 : 3) | 16);
                r3.setTextColor(getTextColor());
                r3.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
                linearLayout.addView(r3, new LinearLayout.LayoutParams(-1, AndroidUtilities.dp(48.0f) + 1));
                onCreateViewHolder.itemView.setTag("bottomSheet");
                linearLayout.addView(onCreateViewHolder.itemView, LayoutHelper.createLinear(-1, -2, 0.0f, 7.0f, 0.0f, 0.0f));
                TextSelectionHelper<Cell>.TextSelectionOverlay overlayView = this.textSelectionHelperBottomSheet.getOverlayView(this.parentActivity);
                AnonymousClass5 r1 = new FrameLayout(this.parentActivity) {
                    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                        TextSelectionHelper<Cell>.TextSelectionOverlay overlayView = ArticleViewer.this.textSelectionHelperBottomSheet.getOverlayView(getContext());
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

                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, i2);
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(linearLayout.getMeasuredHeight() + AndroidUtilities.dp(8.0f), NUM));
                    }
                };
                builder.setDelegate(new BottomSheet.BottomSheetDelegate() {
                    public boolean canDismiss() {
                        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = ArticleViewer.this.textSelectionHelperBottomSheet;
                        if (articleTextSelectionHelper == null || !articleTextSelectionHelper.isSelectionMode()) {
                            return true;
                        }
                        ArticleViewer.this.textSelectionHelperBottomSheet.clear();
                        return false;
                    }
                });
                r1.addView(linearLayout, -1, -2);
                r1.addView(overlayView, -1, -2);
                builder.setCustomView(r1);
                if (this.textSelectionHelper.isSelectionMode()) {
                    this.textSelectionHelper.clear();
                }
                BottomSheet create = builder.create();
                this.linkSheet = create;
                showDialog(create);
            } else if (num2.intValue() >= 0 && num2.intValue() < this.adapter[0].blocks.size()) {
                TLRPC$PageBlock tLRPC$PageBlock = (TLRPC$PageBlock) this.adapter[0].blocks.get(num2.intValue());
                TLRPC$PageBlock lastNonListPageBlock = getLastNonListPageBlock(tLRPC$PageBlock);
                if ((lastNonListPageBlock instanceof TL_pageBlockDetailsChild) && openAllParentBlocks((TL_pageBlockDetailsChild) lastNonListPageBlock)) {
                    this.adapter[0].updateRows();
                    this.adapter[0].notifyDataSetChanged();
                }
                int indexOf = this.adapter[0].localBlocks.indexOf(tLRPC$PageBlock);
                if (indexOf != -1) {
                    num2 = Integer.valueOf(indexOf);
                }
                Integer num3 = (Integer) this.adapter[0].anchorsOffset.get(lowerCase);
                if (num3 != null) {
                    if (num3.intValue() == -1) {
                        int access$75002 = this.adapter[0].getTypeForBlock(tLRPC$PageBlock);
                        RecyclerView.ViewHolder onCreateViewHolder2 = this.adapter[0].onCreateViewHolder((ViewGroup) null, access$75002);
                        this.adapter[0].bindBlockToHolder(access$75002, onCreateViewHolder2, tLRPC$PageBlock, 0, 0);
                        onCreateViewHolder2.itemView.measure(View.MeasureSpec.makeMeasureSpec(this.listView[0].getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                        Integer num4 = (Integer) this.adapter[0].anchorsOffset.get(lowerCase);
                        if (num4.intValue() != -1) {
                            num = num4;
                        }
                    } else {
                        num = num3;
                    }
                }
                this.layoutManager[0].scrollToPositionWithOffset(num2.intValue(), (this.currentHeaderHeight - AndroidUtilities.dp(56.0f)) - num.intValue());
            }
            return true;
        }
        return false;
    }

    private boolean removeLastPageFromStack() {
        if (this.pagesStack.size() < 2) {
            return false;
        }
        ArrayList<TLRPC$WebPage> arrayList = this.pagesStack;
        arrayList.remove(arrayList.size() - 1);
        ArrayList<TLRPC$WebPage> arrayList2 = this.pagesStack;
        updateInterfaceForCurrentPage(arrayList2.get(arrayList2.size() - 1), false, -1);
        return true;
    }

    /* access modifiers changed from: protected */
    public void startCheckLongPress(float f, float f2, View view) {
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper;
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap();
            }
            if (view.getTag() == null || view.getTag() != "bottomSheet" || (articleTextSelectionHelper = this.textSelectionHelperBottomSheet) == null) {
                this.textSelectionHelper.setMaybeView((int) f, (int) f2, view);
            } else {
                articleTextSelectionHelper.setMaybeView((int) f, (int) f2, view);
            }
            this.windowView.postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    /* access modifiers changed from: protected */
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

    private int getTextFlags(TLRPC$RichText tLRPC$RichText) {
        if (tLRPC$RichText instanceof TLRPC$TL_textFixed) {
            return getTextFlags(tLRPC$RichText.parentRichText) | 4;
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textItalic) {
            return getTextFlags(tLRPC$RichText.parentRichText) | 2;
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textBold) {
            return getTextFlags(tLRPC$RichText.parentRichText) | 1;
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textUnderline) {
            return getTextFlags(tLRPC$RichText.parentRichText) | 16;
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textStrike) {
            return getTextFlags(tLRPC$RichText.parentRichText) | 32;
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textEmail) {
            return getTextFlags(tLRPC$RichText.parentRichText) | 8;
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textPhone) {
            return getTextFlags(tLRPC$RichText.parentRichText) | 8;
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textUrl) {
            if (((TLRPC$TL_textUrl) tLRPC$RichText).webpage_id != 0) {
                return getTextFlags(tLRPC$RichText.parentRichText) | 512;
            }
            return getTextFlags(tLRPC$RichText.parentRichText) | 8;
        } else if (tLRPC$RichText instanceof TLRPC$TL_textSubscript) {
            return getTextFlags(tLRPC$RichText.parentRichText) | 128;
        } else {
            if (tLRPC$RichText instanceof TLRPC$TL_textSuperscript) {
                return getTextFlags(tLRPC$RichText.parentRichText) | 256;
            }
            if (tLRPC$RichText instanceof TLRPC$TL_textMarked) {
                return getTextFlags(tLRPC$RichText.parentRichText) | 64;
            }
            if (tLRPC$RichText != null) {
                return getTextFlags(tLRPC$RichText.parentRichText);
            }
            return 0;
        }
    }

    private TLRPC$RichText getLastRichText(TLRPC$RichText tLRPC$RichText) {
        if (tLRPC$RichText == null) {
            return null;
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textFixed) {
            return getLastRichText(((TLRPC$TL_textFixed) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textItalic) {
            return getLastRichText(((TLRPC$TL_textItalic) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textBold) {
            return getLastRichText(((TLRPC$TL_textBold) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textUnderline) {
            return getLastRichText(((TLRPC$TL_textUnderline) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textStrike) {
            return getLastRichText(((TLRPC$TL_textStrike) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textEmail) {
            return getLastRichText(((TLRPC$TL_textEmail) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textUrl) {
            return getLastRichText(((TLRPC$TL_textUrl) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textAnchor) {
            getLastRichText(((TLRPC$TL_textAnchor) tLRPC$RichText).text);
            return tLRPC$RichText;
        } else if (tLRPC$RichText instanceof TLRPC$TL_textSubscript) {
            return getLastRichText(((TLRPC$TL_textSubscript) tLRPC$RichText).text);
        } else {
            if (tLRPC$RichText instanceof TLRPC$TL_textSuperscript) {
                return getLastRichText(((TLRPC$TL_textSuperscript) tLRPC$RichText).text);
            }
            if (tLRPC$RichText instanceof TLRPC$TL_textMarked) {
                return getLastRichText(((TLRPC$TL_textMarked) tLRPC$RichText).text);
            }
            return tLRPC$RichText instanceof TLRPC$TL_textPhone ? getLastRichText(((TLRPC$TL_textPhone) tLRPC$RichText).text) : tLRPC$RichText;
        }
    }

    /* access modifiers changed from: private */
    public CharSequence getText(WebpageAdapter webpageAdapter, View view, TLRPC$RichText tLRPC$RichText, TLRPC$RichText tLRPC$RichText2, TLRPC$PageBlock tLRPC$PageBlock, int i) {
        return getText(webpageAdapter.currentPage, view, tLRPC$RichText, tLRPC$RichText2, tLRPC$PageBlock, i);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v42, resolved type: android.text.SpannableStringBuilder} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.CharSequence getText(org.telegram.tgnet.TLRPC$WebPage r27, android.view.View r28, org.telegram.tgnet.TLRPC$RichText r29, org.telegram.tgnet.TLRPC$RichText r30, org.telegram.tgnet.TLRPC$PageBlock r31, int r32) {
        /*
            r26 = this;
            r8 = r26
            r0 = r29
            r9 = r30
            r10 = r31
            r11 = 0
            if (r9 != 0) goto L_0x000c
            return r11
        L_0x000c:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textFixed
            if (r1 == 0) goto L_0x0026
            r1 = r9
            org.telegram.tgnet.TLRPC$TL_textFixed r1 = (org.telegram.tgnet.TLRPC$TL_textFixed) r1
            org.telegram.tgnet.TLRPC$RichText r5 = r1.text
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            r6 = r31
            r7 = r32
            java.lang.CharSequence r0 = r1.getText((org.telegram.tgnet.TLRPC$WebPage) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$RichText) r5, (org.telegram.tgnet.TLRPC$PageBlock) r6, (int) r7)
            return r0
        L_0x0026:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textItalic
            if (r1 == 0) goto L_0x0040
            r1 = r9
            org.telegram.tgnet.TLRPC$TL_textItalic r1 = (org.telegram.tgnet.TLRPC$TL_textItalic) r1
            org.telegram.tgnet.TLRPC$RichText r5 = r1.text
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            r6 = r31
            r7 = r32
            java.lang.CharSequence r0 = r1.getText((org.telegram.tgnet.TLRPC$WebPage) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$RichText) r5, (org.telegram.tgnet.TLRPC$PageBlock) r6, (int) r7)
            return r0
        L_0x0040:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textBold
            if (r1 == 0) goto L_0x005a
            r1 = r9
            org.telegram.tgnet.TLRPC$TL_textBold r1 = (org.telegram.tgnet.TLRPC$TL_textBold) r1
            org.telegram.tgnet.TLRPC$RichText r5 = r1.text
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            r6 = r31
            r7 = r32
            java.lang.CharSequence r0 = r1.getText((org.telegram.tgnet.TLRPC$WebPage) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$RichText) r5, (org.telegram.tgnet.TLRPC$PageBlock) r6, (int) r7)
            return r0
        L_0x005a:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textUnderline
            if (r1 == 0) goto L_0x0074
            r1 = r9
            org.telegram.tgnet.TLRPC$TL_textUnderline r1 = (org.telegram.tgnet.TLRPC$TL_textUnderline) r1
            org.telegram.tgnet.TLRPC$RichText r5 = r1.text
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            r6 = r31
            r7 = r32
            java.lang.CharSequence r0 = r1.getText((org.telegram.tgnet.TLRPC$WebPage) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$RichText) r5, (org.telegram.tgnet.TLRPC$PageBlock) r6, (int) r7)
            return r0
        L_0x0074:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textStrike
            if (r1 == 0) goto L_0x008e
            r1 = r9
            org.telegram.tgnet.TLRPC$TL_textStrike r1 = (org.telegram.tgnet.TLRPC$TL_textStrike) r1
            org.telegram.tgnet.TLRPC$RichText r5 = r1.text
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            r6 = r31
            r7 = r32
            java.lang.CharSequence r0 = r1.getText((org.telegram.tgnet.TLRPC$WebPage) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$RichText) r5, (org.telegram.tgnet.TLRPC$PageBlock) r6, (int) r7)
            return r0
        L_0x008e:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textEmail
            r12 = 33
            r13 = 0
            if (r1 == 0) goto L_0x00ec
            android.text.SpannableStringBuilder r14 = new android.text.SpannableStringBuilder
            r1 = r9
            org.telegram.tgnet.TLRPC$TL_textEmail r1 = (org.telegram.tgnet.TLRPC$TL_textEmail) r1
            org.telegram.tgnet.TLRPC$RichText r5 = r1.text
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            r6 = r31
            r7 = r32
            java.lang.CharSequence r1 = r1.getText((org.telegram.tgnet.TLRPC$WebPage) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$RichText) r5, (org.telegram.tgnet.TLRPC$PageBlock) r6, (int) r7)
            r14.<init>(r1)
            int r1 = r14.length()
            java.lang.Class<android.text.style.MetricAffectingSpan> r2 = android.text.style.MetricAffectingSpan.class
            java.lang.Object[] r1 = r14.getSpans(r13, r1, r2)
            android.text.style.MetricAffectingSpan[] r1 = (android.text.style.MetricAffectingSpan[]) r1
            int r2 = r14.length()
            if (r2 == 0) goto L_0x00eb
            org.telegram.ui.Components.TextPaintUrlSpan r2 = new org.telegram.ui.Components.TextPaintUrlSpan
            if (r1 == 0) goto L_0x00c8
            int r1 = r1.length
            if (r1 != 0) goto L_0x00cc
        L_0x00c8:
            android.text.TextPaint r11 = r8.getTextPaint(r0, r9, r10)
        L_0x00cc:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "mailto:"
            r0.append(r1)
            java.lang.String r1 = getUrl(r30)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r2.<init>(r11, r0)
            int r0 = r14.length()
            r14.setSpan(r2, r13, r0, r12)
        L_0x00eb:
            return r14
        L_0x00ec:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textUrl
            r14 = 0
            if (r1 == 0) goto L_0x014f
            r7 = r9
            org.telegram.tgnet.TLRPC$TL_textUrl r7 = (org.telegram.tgnet.TLRPC$TL_textUrl) r7
            android.text.SpannableStringBuilder r6 = new android.text.SpannableStringBuilder
            org.telegram.tgnet.TLRPC$RichText r5 = r7.text
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            r11 = r6
            r6 = r31
            r12 = r7
            r7 = r32
            java.lang.CharSequence r1 = r1.getText((org.telegram.tgnet.TLRPC$WebPage) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$RichText) r5, (org.telegram.tgnet.TLRPC$PageBlock) r6, (int) r7)
            r11.<init>(r1)
            int r1 = r11.length()
            java.lang.Class<android.text.style.MetricAffectingSpan> r2 = android.text.style.MetricAffectingSpan.class
            java.lang.Object[] r1 = r11.getSpans(r13, r1, r2)
            android.text.style.MetricAffectingSpan[] r1 = (android.text.style.MetricAffectingSpan[]) r1
            if (r1 == 0) goto L_0x0122
            int r1 = r1.length
            if (r1 != 0) goto L_0x0120
            goto L_0x0122
        L_0x0120:
            r0 = 0
            goto L_0x0126
        L_0x0122:
            android.text.TextPaint r0 = r8.getTextPaint(r0, r9, r10)
        L_0x0126:
            long r1 = r12.webpage_id
            int r3 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r3 == 0) goto L_0x0136
            org.telegram.ui.Components.TextPaintWebpageUrlSpan r1 = new org.telegram.ui.Components.TextPaintWebpageUrlSpan
            java.lang.String r2 = getUrl(r30)
            r1.<init>(r0, r2)
            goto L_0x013f
        L_0x0136:
            org.telegram.ui.Components.TextPaintUrlSpan r1 = new org.telegram.ui.Components.TextPaintUrlSpan
            java.lang.String r2 = getUrl(r30)
            r1.<init>(r0, r2)
        L_0x013f:
            int r0 = r11.length()
            if (r0 == 0) goto L_0x014e
            int r0 = r11.length()
            r2 = 33
            r11.setSpan(r1, r13, r0, r2)
        L_0x014e:
            return r11
        L_0x014f:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textPlain
            if (r1 == 0) goto L_0x0159
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_textPlain r0 = (org.telegram.tgnet.TLRPC$TL_textPlain) r0
            java.lang.String r0 = r0.text
            return r0
        L_0x0159:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textAnchor
            if (r1 == 0) goto L_0x0187
            org.telegram.tgnet.TLRPC$TL_textAnchor r9 = (org.telegram.tgnet.TLRPC$TL_textAnchor) r9
            android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
            org.telegram.tgnet.TLRPC$RichText r5 = r9.text
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            r6 = r31
            r7 = r32
            java.lang.CharSequence r0 = r1.getText((org.telegram.tgnet.TLRPC$WebPage) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$RichText) r5, (org.telegram.tgnet.TLRPC$PageBlock) r6, (int) r7)
            r11.<init>(r0)
            org.telegram.ui.Components.AnchorSpan r0 = new org.telegram.ui.Components.AnchorSpan
            java.lang.String r1 = r9.name
            r0.<init>(r1)
            int r1 = r11.length()
            r2 = 17
            r11.setSpan(r0, r13, r1, r2)
            return r11
        L_0x0187:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textEmpty
            java.lang.String r2 = ""
            if (r1 == 0) goto L_0x018e
            return r2
        L_0x018e:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textConcat
            r11 = 1
            if (r1 == 0) goto L_0x028f
            android.text.SpannableStringBuilder r12 = new android.text.SpannableStringBuilder
            r12.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$RichText> r1 = r9.texts
            int r7 = r1.size()
            r6 = 0
        L_0x019f:
            if (r6 >= r7) goto L_0x028e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$RichText> r1 = r9.texts
            java.lang.Object r1 = r1.get(r6)
            r5 = r1
            org.telegram.tgnet.TLRPC$RichText r5 = (org.telegram.tgnet.TLRPC$RichText) r5
            org.telegram.tgnet.TLRPC$RichText r4 = r8.getLastRichText(r5)
            if (r32 < 0) goto L_0x01c0
            boolean r1 = r5 instanceof org.telegram.tgnet.TLRPC$TL_textUrl
            if (r1 == 0) goto L_0x01c0
            r1 = r5
            org.telegram.tgnet.TLRPC$TL_textUrl r1 = (org.telegram.tgnet.TLRPC$TL_textUrl) r1
            long r1 = r1.webpage_id
            int r3 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r3 == 0) goto L_0x01c0
            r16 = 1
            goto L_0x01c2
        L_0x01c0:
            r16 = 0
        L_0x01c2:
            java.lang.String r3 = " "
            if (r16 == 0) goto L_0x01ef
            int r1 = r12.length()
            if (r1 == 0) goto L_0x01ef
            int r1 = r12.length()
            int r1 = r1 - r11
            char r1 = r12.charAt(r1)
            r2 = 10
            if (r1 == r2) goto L_0x01ef
            r12.append(r3)
            org.telegram.ui.Cells.TextSelectionHelper$IgnoreCopySpannable r1 = new org.telegram.ui.Cells.TextSelectionHelper$IgnoreCopySpannable
            r1.<init>()
            int r2 = r12.length()
            int r2 = r2 - r11
            int r14 = r12.length()
            r15 = 33
            r12.setSpan(r1, r2, r14, r15)
        L_0x01ef:
            r1 = r26
            r2 = r27
            r14 = r3
            r3 = r28
            r15 = r4
            r4 = r29
            r17 = r5
            r13 = r6
            r6 = r31
            r18 = r7
            r7 = r32
            java.lang.CharSequence r1 = r1.getText((org.telegram.tgnet.TLRPC$WebPage) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$RichText) r5, (org.telegram.tgnet.TLRPC$PageBlock) r6, (int) r7)
            int r2 = r8.getTextFlags(r15)
            int r3 = r12.length()
            r12.append(r1)
            if (r2 == 0) goto L_0x0269
            boolean r1 = r1 instanceof android.text.SpannableStringBuilder
            if (r1 != 0) goto L_0x0269
            r1 = r2 & 8
            if (r1 != 0) goto L_0x0239
            r1 = r2 & 512(0x200, float:7.175E-43)
            if (r1 == 0) goto L_0x0220
            goto L_0x0239
        L_0x0220:
            int r1 = r12.length()
            if (r3 == r1) goto L_0x0269
            org.telegram.ui.Components.TextPaintSpan r1 = new org.telegram.ui.Components.TextPaintSpan
            android.text.TextPaint r2 = r8.getTextPaint(r0, r15, r10)
            r1.<init>(r2)
            int r2 = r12.length()
            r4 = 33
            r12.setSpan(r1, r3, r2, r4)
            goto L_0x0269
        L_0x0239:
            java.lang.String r1 = getUrl(r17)
            if (r1 != 0) goto L_0x0243
            java.lang.String r1 = getUrl(r29)
        L_0x0243:
            r2 = r2 & 512(0x200, float:7.175E-43)
            if (r2 == 0) goto L_0x0251
            org.telegram.ui.Components.TextPaintWebpageUrlSpan r2 = new org.telegram.ui.Components.TextPaintWebpageUrlSpan
            android.text.TextPaint r4 = r8.getTextPaint(r0, r15, r10)
            r2.<init>(r4, r1)
            goto L_0x025a
        L_0x0251:
            org.telegram.ui.Components.TextPaintUrlSpan r2 = new org.telegram.ui.Components.TextPaintUrlSpan
            android.text.TextPaint r4 = r8.getTextPaint(r0, r15, r10)
            r2.<init>(r4, r1)
        L_0x025a:
            int r1 = r12.length()
            if (r3 == r1) goto L_0x0269
            int r1 = r12.length()
            r4 = 33
            r12.setSpan(r2, r3, r1, r4)
        L_0x0269:
            if (r16 == 0) goto L_0x0285
            int r7 = r18 + -1
            if (r13 == r7) goto L_0x0285
            r12.append(r14)
            org.telegram.ui.Cells.TextSelectionHelper$IgnoreCopySpannable r1 = new org.telegram.ui.Cells.TextSelectionHelper$IgnoreCopySpannable
            r1.<init>()
            int r2 = r12.length()
            int r2 = r2 - r11
            int r3 = r12.length()
            r4 = 33
            r12.setSpan(r1, r2, r3, r4)
        L_0x0285:
            int r6 = r13 + 1
            r7 = r18
            r13 = 0
            r14 = 0
            goto L_0x019f
        L_0x028e:
            return r12
        L_0x028f:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textSubscript
            if (r1 == 0) goto L_0x02a9
            r1 = r9
            org.telegram.tgnet.TLRPC$TL_textSubscript r1 = (org.telegram.tgnet.TLRPC$TL_textSubscript) r1
            org.telegram.tgnet.TLRPC$RichText r5 = r1.text
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            r6 = r31
            r7 = r32
            java.lang.CharSequence r0 = r1.getText((org.telegram.tgnet.TLRPC$WebPage) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$RichText) r5, (org.telegram.tgnet.TLRPC$PageBlock) r6, (int) r7)
            return r0
        L_0x02a9:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textSuperscript
            if (r1 == 0) goto L_0x02c3
            r1 = r9
            org.telegram.tgnet.TLRPC$TL_textSuperscript r1 = (org.telegram.tgnet.TLRPC$TL_textSuperscript) r1
            org.telegram.tgnet.TLRPC$RichText r5 = r1.text
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            r6 = r31
            r7 = r32
            java.lang.CharSequence r0 = r1.getText((org.telegram.tgnet.TLRPC$WebPage) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$RichText) r5, (org.telegram.tgnet.TLRPC$PageBlock) r6, (int) r7)
            return r0
        L_0x02c3:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textMarked
            if (r1 == 0) goto L_0x0310
            android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
            r1 = r9
            org.telegram.tgnet.TLRPC$TL_textMarked r1 = (org.telegram.tgnet.TLRPC$TL_textMarked) r1
            org.telegram.tgnet.TLRPC$RichText r5 = r1.text
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            r6 = r31
            r7 = r32
            java.lang.CharSequence r1 = r1.getText((org.telegram.tgnet.TLRPC$WebPage) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$RichText) r5, (org.telegram.tgnet.TLRPC$PageBlock) r6, (int) r7)
            r11.<init>(r1)
            int r1 = r11.length()
            java.lang.Class<android.text.style.MetricAffectingSpan> r2 = android.text.style.MetricAffectingSpan.class
            r3 = 0
            java.lang.Object[] r1 = r11.getSpans(r3, r1, r2)
            android.text.style.MetricAffectingSpan[] r1 = (android.text.style.MetricAffectingSpan[]) r1
            int r2 = r11.length()
            if (r2 == 0) goto L_0x030f
            org.telegram.ui.Components.TextPaintMarkSpan r2 = new org.telegram.ui.Components.TextPaintMarkSpan
            if (r1 == 0) goto L_0x02fe
            int r1 = r1.length
            if (r1 != 0) goto L_0x02fc
            goto L_0x02fe
        L_0x02fc:
            r0 = 0
            goto L_0x0302
        L_0x02fe:
            android.text.TextPaint r0 = r8.getTextPaint(r0, r9, r10)
        L_0x0302:
            r2.<init>(r0)
            int r0 = r11.length()
            r1 = 33
            r3 = 0
            r11.setSpan(r2, r3, r0, r1)
        L_0x030f:
            return r11
        L_0x0310:
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textPhone
            if (r1 == 0) goto L_0x0372
            android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
            r1 = r9
            org.telegram.tgnet.TLRPC$TL_textPhone r1 = (org.telegram.tgnet.TLRPC$TL_textPhone) r1
            org.telegram.tgnet.TLRPC$RichText r5 = r1.text
            r1 = r26
            r2 = r27
            r3 = r28
            r4 = r29
            r6 = r31
            r7 = r32
            java.lang.CharSequence r1 = r1.getText((org.telegram.tgnet.TLRPC$WebPage) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$RichText) r5, (org.telegram.tgnet.TLRPC$PageBlock) r6, (int) r7)     // Catch:{ all -> 0x0410 }
            r11.<init>(r1)
            int r1 = r11.length()
            java.lang.Class<android.text.style.MetricAffectingSpan> r2 = android.text.style.MetricAffectingSpan.class
            r3 = 0
            java.lang.Object[] r1 = r11.getSpans(r3, r1, r2)
            android.text.style.MetricAffectingSpan[] r1 = (android.text.style.MetricAffectingSpan[]) r1
            int r2 = r11.length()
            if (r2 == 0) goto L_0x0371
            org.telegram.ui.Components.TextPaintUrlSpan r2 = new org.telegram.ui.Components.TextPaintUrlSpan
            if (r1 == 0) goto L_0x034b
            int r1 = r1.length
            if (r1 != 0) goto L_0x0349
            goto L_0x034b
        L_0x0349:
            r0 = 0
            goto L_0x034f
        L_0x034b:
            android.text.TextPaint r0 = r8.getTextPaint(r0, r9, r10)
        L_0x034f:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "tel:"
            r1.append(r3)
            java.lang.String r3 = getUrl(r30)
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            r2.<init>(r0, r1)
            int r0 = r11.length()
            r1 = 33
            r3 = 0
            r11.setSpan(r2, r3, r0, r1)
        L_0x0371:
            return r11
        L_0x0372:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textImage
            if (r0 == 0) goto L_0x03fe
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_textImage r0 = (org.telegram.tgnet.TLRPC$TL_textImage) r0
            long r3 = r0.document_id
            r1 = r27
            org.telegram.tgnet.TLRPC$Document r20 = org.telegram.ui.ArticleViewer.WebPageUtils.getDocumentWithId(r1, r3)
            if (r20 == 0) goto L_0x03fd
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            java.lang.String r3 = "*"
            r2.<init>(r3)
            int r3 = r0.w
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0.h
            float r0 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r4 = java.lang.Math.abs(r32)
            if (r3 <= r4) goto L_0x03aa
            float r5 = (float) r4
            float r3 = (float) r3
            float r5 = r5 / r3
            float r0 = (float) r0
            float r0 = r0 * r5
            int r0 = (int) r0
            r23 = r0
            r22 = r4
            goto L_0x03ae
        L_0x03aa:
            r23 = r0
            r22 = r3
        L_0x03ae:
            if (r28 == 0) goto L_0x03fd
            java.lang.String r0 = "windowBackgroundWhite"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            r3 = 1046066128(0x3e59b3d0, float:0.2126)
            int r4 = android.graphics.Color.red(r0)
            float r4 = (float) r4
            float r4 = r4 * r3
            r3 = 1060575065(0x3var_, float:0.7152)
            int r5 = android.graphics.Color.green(r0)
            float r5 = (float) r5
            float r5 = r5 * r3
            float r4 = r4 + r5
            r3 = 1033100696(0x3d93dd98, float:0.0722)
            int r0 = android.graphics.Color.blue(r0)
            float r0 = (float) r0
            float r0 = r0 * r3
            float r4 = r4 + r0
            r0 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 / r0
            org.telegram.ui.Components.TextPaintImageReceiverSpan r0 = new org.telegram.ui.Components.TextPaintImageReceiverSpan
            r24 = 0
            r3 = 1060403937(0x3var_ae1, float:0.705)
            int r3 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x03e8
            r25 = 1
            goto L_0x03ea
        L_0x03e8:
            r25 = 0
        L_0x03ea:
            r18 = r0
            r19 = r28
            r21 = r27
            r18.<init>(r19, r20, r21, r22, r23, r24, r25)
            int r1 = r2.length()
            r3 = 33
            r4 = 0
            r2.setSpan(r0, r4, r1, r3)
        L_0x03fd:
            return r2
        L_0x03fe:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not supported "
            r0.append(r1)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0410:
            r0 = move-exception
            r1 = r0
            goto L_0x0414
        L_0x0413:
            throw r1
        L_0x0414:
            goto L_0x0413
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.getText(org.telegram.tgnet.TLRPC$WebPage, android.view.View, org.telegram.tgnet.TLRPC$RichText, org.telegram.tgnet.TLRPC$RichText, org.telegram.tgnet.TLRPC$PageBlock, int):java.lang.CharSequence");
    }

    public static CharSequence getPlainText(TLRPC$RichText tLRPC$RichText) {
        if (tLRPC$RichText == null) {
            return "";
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textFixed) {
            return getPlainText(((TLRPC$TL_textFixed) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textItalic) {
            return getPlainText(((TLRPC$TL_textItalic) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textBold) {
            return getPlainText(((TLRPC$TL_textBold) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textUnderline) {
            return getPlainText(((TLRPC$TL_textUnderline) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textStrike) {
            return getPlainText(((TLRPC$TL_textStrike) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textEmail) {
            return getPlainText(((TLRPC$TL_textEmail) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textUrl) {
            return getPlainText(((TLRPC$TL_textUrl) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textPlain) {
            return ((TLRPC$TL_textPlain) tLRPC$RichText).text;
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textAnchor) {
            return getPlainText(((TLRPC$TL_textAnchor) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textEmpty) {
            return "";
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textConcat) {
            StringBuilder sb = new StringBuilder();
            int size = tLRPC$RichText.texts.size();
            for (int i = 0; i < size; i++) {
                sb.append(getPlainText(tLRPC$RichText.texts.get(i)));
            }
            return sb;
        } else if (tLRPC$RichText instanceof TLRPC$TL_textSubscript) {
            return getPlainText(((TLRPC$TL_textSubscript) tLRPC$RichText).text);
        } else {
            if (tLRPC$RichText instanceof TLRPC$TL_textSuperscript) {
                return getPlainText(((TLRPC$TL_textSuperscript) tLRPC$RichText).text);
            }
            if (tLRPC$RichText instanceof TLRPC$TL_textMarked) {
                return getPlainText(((TLRPC$TL_textMarked) tLRPC$RichText).text);
            }
            if (tLRPC$RichText instanceof TLRPC$TL_textPhone) {
                return getPlainText(((TLRPC$TL_textPhone) tLRPC$RichText).text);
            }
            if (tLRPC$RichText instanceof TLRPC$TL_textImage) {
            }
            return "";
        }
    }

    public static String getUrl(TLRPC$RichText tLRPC$RichText) {
        if (tLRPC$RichText instanceof TLRPC$TL_textFixed) {
            return getUrl(((TLRPC$TL_textFixed) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textItalic) {
            return getUrl(((TLRPC$TL_textItalic) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textBold) {
            return getUrl(((TLRPC$TL_textBold) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textUnderline) {
            return getUrl(((TLRPC$TL_textUnderline) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textStrike) {
            return getUrl(((TLRPC$TL_textStrike) tLRPC$RichText).text);
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textEmail) {
            return ((TLRPC$TL_textEmail) tLRPC$RichText).email;
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textUrl) {
            return ((TLRPC$TL_textUrl) tLRPC$RichText).url;
        }
        if (tLRPC$RichText instanceof TLRPC$TL_textPhone) {
            return ((TLRPC$TL_textPhone) tLRPC$RichText).phone;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public int getTextColor() {
        return Theme.getColor("windowBackgroundWhiteBlackText");
    }

    /* access modifiers changed from: private */
    public int getLinkTextColor() {
        return Theme.getColor("windowBackgroundWhiteLinkText");
    }

    /* access modifiers changed from: private */
    public static int getGrayTextColor() {
        return Theme.getColor("windowBackgroundWhiteGrayText");
    }

    private TextPaint getTextPaint(TLRPC$RichText tLRPC$RichText, TLRPC$RichText tLRPC$RichText2, TLRPC$PageBlock tLRPC$PageBlock) {
        int i;
        int i2;
        SparseArray<TextPaint> sparseArray;
        int i3;
        SparseArray<TextPaint> sparseArray2;
        int i4;
        SparseArray<TextPaint> sparseArray3;
        int i5;
        SparseArray<TextPaint> sparseArray4;
        int i6;
        SparseArray<TextPaint> sparseArray5;
        int i7;
        SparseArray<TextPaint> sparseArray6;
        int i8;
        SparseArray<TextPaint> sparseArray7;
        int textFlags = getTextFlags(tLRPC$RichText2);
        int dp = AndroidUtilities.dp(14.0f);
        int dp2 = AndroidUtilities.dp((float) (SharedConfig.ivFontSize - 16));
        SparseArray<TextPaint> sparseArray8 = null;
        if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPhoto) {
            TLRPC$RichText tLRPC$RichText3 = ((TLRPC$TL_pageBlockPhoto) tLRPC$PageBlock).caption.text;
            if (tLRPC$RichText3 == tLRPC$RichText2 || tLRPC$RichText3 == tLRPC$RichText) {
                sparseArray7 = photoCaptionTextPaints;
                i8 = AndroidUtilities.dp(14.0f);
            } else {
                sparseArray7 = photoCreditTextPaints;
                i8 = AndroidUtilities.dp(12.0f);
            }
            sparseArray8 = sparseArray7;
            dp = i8;
            i = getGrayTextColor();
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockMap) {
            TLRPC$RichText tLRPC$RichText4 = ((TLRPC$TL_pageBlockMap) tLRPC$PageBlock).caption.text;
            if (tLRPC$RichText4 == tLRPC$RichText2 || tLRPC$RichText4 == tLRPC$RichText) {
                sparseArray6 = photoCaptionTextPaints;
                i7 = AndroidUtilities.dp(14.0f);
            } else {
                sparseArray6 = photoCreditTextPaints;
                i7 = AndroidUtilities.dp(12.0f);
            }
            sparseArray8 = sparseArray6;
            dp = i7;
            i = getGrayTextColor();
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockTitle) {
            sparseArray8 = titleTextPaints;
            dp = AndroidUtilities.dp(23.0f);
            i = getTextColor();
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockKicker) {
            sparseArray8 = kickerTextPaints;
            dp = AndroidUtilities.dp(14.0f);
            i = getTextColor();
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockAuthorDate) {
            sparseArray8 = authorTextPaints;
            dp = AndroidUtilities.dp(14.0f);
            i = getGrayTextColor();
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockFooter) {
            sparseArray8 = footerTextPaints;
            dp = AndroidUtilities.dp(14.0f);
            i = getGrayTextColor();
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSubtitle) {
            sparseArray8 = subtitleTextPaints;
            dp = AndroidUtilities.dp(20.0f);
            i = getTextColor();
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockHeader) {
            sparseArray8 = headerTextPaints;
            dp = AndroidUtilities.dp(20.0f);
            i = getTextColor();
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSubheader) {
            sparseArray8 = subheaderTextPaints;
            dp = AndroidUtilities.dp(17.0f);
            i = getTextColor();
        } else {
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockBlockquote) {
                TLRPC$TL_pageBlockBlockquote tLRPC$TL_pageBlockBlockquote = (TLRPC$TL_pageBlockBlockquote) tLRPC$PageBlock;
                if (tLRPC$TL_pageBlockBlockquote.text == tLRPC$RichText) {
                    sparseArray8 = quoteTextPaints;
                    dp = AndroidUtilities.dp(15.0f);
                    i = getTextColor();
                } else if (tLRPC$TL_pageBlockBlockquote.caption == tLRPC$RichText) {
                    sparseArray8 = photoCaptionTextPaints;
                    dp = AndroidUtilities.dp(14.0f);
                    i = getGrayTextColor();
                }
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPullquote) {
                TLRPC$TL_pageBlockPullquote tLRPC$TL_pageBlockPullquote = (TLRPC$TL_pageBlockPullquote) tLRPC$PageBlock;
                if (tLRPC$TL_pageBlockPullquote.text == tLRPC$RichText) {
                    sparseArray8 = quoteTextPaints;
                    dp = AndroidUtilities.dp(15.0f);
                    i = getTextColor();
                } else if (tLRPC$TL_pageBlockPullquote.caption == tLRPC$RichText) {
                    sparseArray8 = photoCaptionTextPaints;
                    dp = AndroidUtilities.dp(14.0f);
                    i = getGrayTextColor();
                }
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPreformatted) {
                sparseArray8 = preformattedTextPaints;
                dp = AndroidUtilities.dp(14.0f);
                i = getTextColor();
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockParagraph) {
                sparseArray8 = paragraphTextPaints;
                dp = AndroidUtilities.dp(16.0f);
                i = getTextColor();
            } else if (isListItemBlock(tLRPC$PageBlock)) {
                sparseArray8 = listTextPaints;
                dp = AndroidUtilities.dp(16.0f);
                i = getTextColor();
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockEmbed) {
                TLRPC$RichText tLRPC$RichText5 = ((TLRPC$TL_pageBlockEmbed) tLRPC$PageBlock).caption.text;
                if (tLRPC$RichText5 == tLRPC$RichText2 || tLRPC$RichText5 == tLRPC$RichText) {
                    sparseArray5 = photoCaptionTextPaints;
                    i6 = AndroidUtilities.dp(14.0f);
                } else {
                    sparseArray5 = photoCreditTextPaints;
                    i6 = AndroidUtilities.dp(12.0f);
                }
                sparseArray8 = sparseArray5;
                dp = i6;
                i = getGrayTextColor();
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSlideshow) {
                TLRPC$RichText tLRPC$RichText6 = ((TLRPC$TL_pageBlockSlideshow) tLRPC$PageBlock).caption.text;
                if (tLRPC$RichText6 == tLRPC$RichText2 || tLRPC$RichText6 == tLRPC$RichText) {
                    sparseArray4 = photoCaptionTextPaints;
                    i5 = AndroidUtilities.dp(14.0f);
                } else {
                    sparseArray4 = photoCreditTextPaints;
                    i5 = AndroidUtilities.dp(12.0f);
                }
                sparseArray8 = sparseArray4;
                dp = i5;
                i = getGrayTextColor();
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockCollage) {
                TLRPC$RichText tLRPC$RichText7 = ((TLRPC$TL_pageBlockCollage) tLRPC$PageBlock).caption.text;
                if (tLRPC$RichText7 == tLRPC$RichText2 || tLRPC$RichText7 == tLRPC$RichText) {
                    sparseArray3 = photoCaptionTextPaints;
                    i4 = AndroidUtilities.dp(14.0f);
                } else {
                    sparseArray3 = photoCreditTextPaints;
                    i4 = AndroidUtilities.dp(12.0f);
                }
                sparseArray8 = sparseArray3;
                dp = i4;
                i = getGrayTextColor();
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockEmbedPost) {
                TLRPC$TL_pageCaption tLRPC$TL_pageCaption = ((TLRPC$TL_pageBlockEmbedPost) tLRPC$PageBlock).caption;
                if (tLRPC$RichText2 == tLRPC$TL_pageCaption.text) {
                    sparseArray8 = photoCaptionTextPaints;
                    dp = AndroidUtilities.dp(14.0f);
                    i = getGrayTextColor();
                } else if (tLRPC$RichText2 == tLRPC$TL_pageCaption.credit) {
                    sparseArray8 = photoCreditTextPaints;
                    dp = AndroidUtilities.dp(12.0f);
                    i = getGrayTextColor();
                } else if (tLRPC$RichText2 != null) {
                    sparseArray8 = embedPostTextPaints;
                    dp = AndroidUtilities.dp(14.0f);
                    i = getTextColor();
                }
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockVideo) {
                if (tLRPC$RichText2 == ((TLRPC$TL_pageBlockVideo) tLRPC$PageBlock).caption.text) {
                    sparseArray2 = mediaCaptionTextPaints;
                    i3 = AndroidUtilities.dp(14.0f);
                } else {
                    sparseArray2 = mediaCreditTextPaints;
                    i3 = AndroidUtilities.dp(12.0f);
                }
                sparseArray8 = sparseArray2;
                dp = i3;
                i = getTextColor();
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockAudio) {
                if (tLRPC$RichText2 == ((TLRPC$TL_pageBlockAudio) tLRPC$PageBlock).caption.text) {
                    sparseArray = mediaCaptionTextPaints;
                    i2 = AndroidUtilities.dp(14.0f);
                } else {
                    sparseArray = mediaCreditTextPaints;
                    i2 = AndroidUtilities.dp(12.0f);
                }
                sparseArray8 = sparseArray;
                dp = i2;
                i = getTextColor();
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockRelatedArticles) {
                sparseArray8 = relatedArticleTextPaints;
                dp = AndroidUtilities.dp(15.0f);
                i = getGrayTextColor();
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockDetails) {
                sparseArray8 = detailsTextPaints;
                dp = AndroidUtilities.dp(15.0f);
                i = getTextColor();
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockTable) {
                sparseArray8 = tableTextPaints;
                dp = AndroidUtilities.dp(15.0f);
                i = getTextColor();
            }
            i = -65536;
        }
        int i9 = textFlags & 256;
        if (!(i9 == 0 && (textFlags & 128) == 0)) {
            dp -= AndroidUtilities.dp(4.0f);
        }
        if (sparseArray8 == null) {
            if (errorTextPaint == null) {
                TextPaint textPaint = new TextPaint(1);
                errorTextPaint = textPaint;
                textPaint.setColor(-65536);
            }
            errorTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            return errorTextPaint;
        }
        TextPaint textPaint2 = sparseArray8.get(textFlags);
        if (textPaint2 == null) {
            textPaint2 = new TextPaint(1);
            if ((textFlags & 4) != 0) {
                textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockRelatedArticles) {
                textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            } else if (this.selectedFont != 1 && !(tLRPC$PageBlock instanceof TLRPC$TL_pageBlockTitle) && !(tLRPC$PageBlock instanceof TLRPC$TL_pageBlockKicker) && !(tLRPC$PageBlock instanceof TLRPC$TL_pageBlockHeader) && !(tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSubtitle) && !(tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSubheader)) {
                int i10 = textFlags & 1;
                if (i10 != 0 && (textFlags & 2) != 0) {
                    textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf"));
                } else if (i10 != 0) {
                    textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                } else if ((textFlags & 2) != 0) {
                    textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/ritalic.ttf"));
                }
            } else if ((tLRPC$PageBlock instanceof TLRPC$TL_pageBlockTitle) || (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockHeader) || (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSubtitle) || (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSubheader)) {
                textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/mw_bold.ttf"));
            } else {
                int i11 = textFlags & 1;
                if (i11 != 0 && (textFlags & 2) != 0) {
                    textPaint2.setTypeface(Typeface.create("serif", 3));
                } else if (i11 != 0) {
                    textPaint2.setTypeface(Typeface.create("serif", 1));
                } else if ((textFlags & 2) != 0) {
                    textPaint2.setTypeface(Typeface.create("serif", 2));
                } else {
                    textPaint2.setTypeface(Typeface.create("serif", 0));
                }
            }
            if ((textFlags & 32) != 0) {
                textPaint2.setFlags(textPaint2.getFlags() | 16);
            }
            if ((textFlags & 16) != 0) {
                textPaint2.setFlags(textPaint2.getFlags() | 8);
            }
            if (!((textFlags & 8) == 0 && (textFlags & 512) == 0)) {
                textPaint2.setFlags(textPaint2.getFlags());
                i = getLinkTextColor();
            }
            if (i9 != 0) {
                textPaint2.baselineShift -= AndroidUtilities.dp(6.0f);
            } else if ((textFlags & 128) != 0) {
                textPaint2.baselineShift += AndroidUtilities.dp(2.0f);
            }
            textPaint2.setColor(i);
            sparseArray8.put(textFlags, textPaint2);
        }
        textPaint2.setTextSize((float) (dp + dp2));
        return textPaint2;
    }

    /* access modifiers changed from: private */
    public DrawingText createLayoutForText(View view, CharSequence charSequence, TLRPC$RichText tLRPC$RichText, int i, int i2, TLRPC$PageBlock tLRPC$PageBlock, Layout.Alignment alignment, WebpageAdapter webpageAdapter) {
        return createLayoutForText(view, charSequence, tLRPC$RichText, i, 0, tLRPC$PageBlock, alignment, 0, webpageAdapter);
    }

    /* access modifiers changed from: private */
    public DrawingText createLayoutForText(View view, CharSequence charSequence, TLRPC$RichText tLRPC$RichText, int i, int i2, TLRPC$PageBlock tLRPC$PageBlock, WebpageAdapter webpageAdapter) {
        return createLayoutForText(view, charSequence, tLRPC$RichText, i, i2, tLRPC$PageBlock, Layout.Alignment.ALIGN_NORMAL, 0, webpageAdapter);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:138:0x035e */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x037b A[Catch:{ Exception -> 0x03b9 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.telegram.ui.ArticleViewer.DrawingText createLayoutForText(android.view.View r23, java.lang.CharSequence r24, org.telegram.tgnet.TLRPC$RichText r25, int r26, int r27, org.telegram.tgnet.TLRPC$PageBlock r28, android.text.Layout.Alignment r29, int r30, org.telegram.ui.ArticleViewer.WebpageAdapter r31) {
        /*
            r22 = this;
            r7 = r22
            r8 = r24
            r9 = r25
            r10 = r28
            r11 = 0
            if (r8 != 0) goto L_0x0012
            if (r9 == 0) goto L_0x0011
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_textEmpty
            if (r0 == 0) goto L_0x0012
        L_0x0011:
            return r11
        L_0x0012:
            if (r26 >= 0) goto L_0x001d
            r0 = 1092616192(0x41200000, float:10.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r20 = r0
            goto L_0x001f
        L_0x001d:
            r20 = r26
        L_0x001f:
            if (r8 == 0) goto L_0x0023
            r0 = r8
            goto L_0x0035
        L_0x0023:
            r0 = r22
            r1 = r31
            r2 = r23
            r3 = r25
            r4 = r25
            r5 = r28
            r6 = r20
            java.lang.CharSequence r0 = r0.getText((org.telegram.ui.ArticleViewer.WebpageAdapter) r1, (android.view.View) r2, (org.telegram.tgnet.TLRPC$RichText) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$PageBlock) r5, (int) r6)
        L_0x0035:
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x003c
            return r11
        L_0x003c:
            int r1 = org.telegram.messenger.SharedConfig.ivFontSize
            int r1 = r1 + -16
            float r1 = (float) r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            boolean r2 = r10 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockEmbedPost
            r3 = 1096810496(0x41600000, float:14.0)
            r4 = 1097859072(0x41700000, float:15.0)
            r5 = 1
            if (r2 == 0) goto L_0x0099
            if (r9 != 0) goto L_0x0099
            r2 = r10
            org.telegram.tgnet.TLRPC$TL_pageBlockEmbedPost r2 = (org.telegram.tgnet.TLRPC$TL_pageBlockEmbedPost) r2
            java.lang.String r2 = r2.author
            if (r2 != r8) goto L_0x0078
            android.text.TextPaint r2 = embedPostAuthorPaint
            if (r2 != 0) goto L_0x0069
            android.text.TextPaint r2 = new android.text.TextPaint
            r2.<init>(r5)
            embedPostAuthorPaint = r2
            int r3 = r22.getTextColor()
            r2.setColor(r3)
        L_0x0069:
            android.text.TextPaint r2 = embedPostAuthorPaint
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 + r1
            float r1 = (float) r3
            r2.setTextSize(r1)
            android.text.TextPaint r1 = embedPostAuthorPaint
            goto L_0x01ba
        L_0x0078:
            android.text.TextPaint r2 = embedPostDatePaint
            if (r2 != 0) goto L_0x008a
            android.text.TextPaint r2 = new android.text.TextPaint
            r2.<init>(r5)
            embedPostDatePaint = r2
            int r4 = getGrayTextColor()
            r2.setColor(r4)
        L_0x008a:
            android.text.TextPaint r2 = embedPostDatePaint
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r3 + r1
            float r1 = (float) r3
            r2.setTextSize(r1)
            android.text.TextPaint r1 = embedPostDatePaint
            goto L_0x01ba
        L_0x0099:
            boolean r2 = r10 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockChannel
            java.lang.String r6 = "fonts/rmedium.ttf"
            if (r2 == 0) goto L_0x00f0
            android.text.TextPaint r1 = channelNamePaint
            if (r1 != 0) goto L_0x00bf
            android.text.TextPaint r1 = new android.text.TextPaint
            r1.<init>(r5)
            channelNamePaint = r1
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r1.setTypeface(r2)
            android.text.TextPaint r1 = new android.text.TextPaint
            r1.<init>(r5)
            channelNamePhotoPaint = r1
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r1.setTypeface(r2)
        L_0x00bf:
            android.text.TextPaint r1 = channelNamePaint
            int r2 = r22.getTextColor()
            r1.setColor(r2)
            android.text.TextPaint r1 = channelNamePaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r2 = (float) r2
            r1.setTextSize(r2)
            android.text.TextPaint r1 = channelNamePhotoPaint
            r2 = -1
            r1.setColor(r2)
            android.text.TextPaint r1 = channelNamePhotoPaint
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r2 = (float) r2
            r1.setTextSize(r2)
            org.telegram.tgnet.TLRPC$TL_pageBlockChannel r1 = r31.channelBlock
            if (r1 == 0) goto L_0x00ec
            android.text.TextPaint r1 = channelNamePhotoPaint
            goto L_0x01ba
        L_0x00ec:
            android.text.TextPaint r1 = channelNamePaint
            goto L_0x01ba
        L_0x00f0:
            boolean r2 = r10 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockRelatedArticlesChild
            if (r2 == 0) goto L_0x0157
            r2 = r10
            org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesChild r2 = (org.telegram.ui.ArticleViewer.TL_pageBlockRelatedArticlesChild) r2
            org.telegram.tgnet.TLRPC$TL_pageBlockRelatedArticles r12 = r2.parent
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pageRelatedArticle> r12 = r12.articles
            int r2 = r2.num
            java.lang.Object r2 = r12.get(r2)
            org.telegram.tgnet.TLRPC$TL_pageRelatedArticle r2 = (org.telegram.tgnet.TLRPC$TL_pageRelatedArticle) r2
            java.lang.String r2 = r2.title
            if (r8 != r2) goto L_0x0135
            android.text.TextPaint r2 = relatedArticleHeaderPaint
            if (r2 != 0) goto L_0x011d
            android.text.TextPaint r2 = new android.text.TextPaint
            r2.<init>(r5)
            relatedArticleHeaderPaint = r2
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r2.setTypeface(r3)
        L_0x011d:
            android.text.TextPaint r2 = relatedArticleHeaderPaint
            int r3 = r22.getTextColor()
            r2.setColor(r3)
            android.text.TextPaint r2 = relatedArticleHeaderPaint
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 + r1
            float r1 = (float) r3
            r2.setTextSize(r1)
            android.text.TextPaint r1 = relatedArticleHeaderPaint
            goto L_0x01ba
        L_0x0135:
            android.text.TextPaint r2 = relatedArticleTextPaint
            if (r2 != 0) goto L_0x0140
            android.text.TextPaint r2 = new android.text.TextPaint
            r2.<init>(r5)
            relatedArticleTextPaint = r2
        L_0x0140:
            android.text.TextPaint r2 = relatedArticleTextPaint
            int r4 = getGrayTextColor()
            r2.setColor(r4)
            android.text.TextPaint r2 = relatedArticleTextPaint
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r3 + r1
            float r1 = (float) r3
            r2.setTextSize(r1)
            android.text.TextPaint r1 = relatedArticleTextPaint
            goto L_0x01ba
        L_0x0157:
            boolean r2 = r7.isListItemBlock(r10)
            if (r2 == 0) goto L_0x01b6
            if (r8 == 0) goto L_0x01b6
            android.text.TextPaint r2 = listTextPointerPaint
            if (r2 != 0) goto L_0x0171
            android.text.TextPaint r2 = new android.text.TextPaint
            r2.<init>(r5)
            listTextPointerPaint = r2
            int r3 = r22.getTextColor()
            r2.setColor(r3)
        L_0x0171:
            android.text.TextPaint r2 = listTextNumPaint
            if (r2 != 0) goto L_0x0183
            android.text.TextPaint r2 = new android.text.TextPaint
            r2.<init>(r5)
            listTextNumPaint = r2
            int r3 = r22.getTextColor()
            r2.setColor(r3)
        L_0x0183:
            android.text.TextPaint r2 = listTextPointerPaint
            r3 = 1100480512(0x41980000, float:19.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r3 + r1
            float r3 = (float) r3
            r2.setTextSize(r3)
            android.text.TextPaint r2 = listTextNumPaint
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r3 + r1
            float r1 = (float) r3
            r2.setTextSize(r1)
            boolean r1 = r10 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockListItem
            if (r1 == 0) goto L_0x01b3
            r1 = r10
            org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = (org.telegram.ui.ArticleViewer.TL_pageBlockListItem) r1
            org.telegram.ui.ArticleViewer$TL_pageBlockListParent r1 = r1.parent
            org.telegram.tgnet.TLRPC$TL_pageBlockList r1 = r1.pageBlockList
            boolean r1 = r1.ordered
            if (r1 != 0) goto L_0x01b3
            android.text.TextPaint r1 = listTextPointerPaint
            goto L_0x01ba
        L_0x01b3:
            android.text.TextPaint r1 = listTextNumPaint
            goto L_0x01ba
        L_0x01b6:
            android.text.TextPaint r1 = r7.getTextPaint(r9, r9, r10)
        L_0x01ba:
            r14 = r1
            r1 = 1082130432(0x40800000, float:4.0)
            r2 = 0
            if (r30 == 0) goto L_0x01f3
            boolean r3 = r10 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockPullquote
            if (r3 == 0) goto L_0x01d9
            android.text.Layout$Alignment r15 = android.text.Layout.Alignment.ALIGN_CENTER
            r16 = 1065353216(0x3var_, float:1.0)
            r17 = 0
            r18 = 0
            android.text.TextUtils$TruncateAt r19 = android.text.TextUtils.TruncateAt.END
            r12 = r0
            r13 = r14
            r14 = r20
            r21 = r30
            android.text.StaticLayout r1 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)
            goto L_0x0236
        L_0x01d9:
            r16 = 1065353216(0x3var_, float:1.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r18 = 0
            android.text.TextUtils$TruncateAt r19 = android.text.TextUtils.TruncateAt.END
            r12 = r0
            r13 = r14
            r14 = r20
            r15 = r29
            r17 = r1
            r21 = r30
            android.text.StaticLayout r1 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)
            goto L_0x0236
        L_0x01f3:
            int r3 = r0.length()
            int r3 = r3 - r5
            char r3 = r0.charAt(r3)
            r4 = 10
            if (r3 != r4) goto L_0x0209
            int r3 = r0.length()
            int r3 = r3 - r5
            java.lang.CharSequence r0 = r0.subSequence(r2, r3)
        L_0x0209:
            boolean r3 = r10 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockPullquote
            if (r3 == 0) goto L_0x021f
            android.text.StaticLayout r1 = new android.text.StaticLayout
            android.text.Layout$Alignment r16 = android.text.Layout.Alignment.ALIGN_CENTER
            r17 = 1065353216(0x3var_, float:1.0)
            r18 = 0
            r19 = 0
            r12 = r1
            r13 = r0
            r15 = r20
            r12.<init>(r13, r14, r15, r16, r17, r18, r19)
            goto L_0x0236
        L_0x021f:
            android.text.StaticLayout r3 = new android.text.StaticLayout
            r17 = 1065353216(0x3var_, float:1.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r19 = 0
            r12 = r3
            r13 = r0
            r15 = r20
            r16 = r29
            r18 = r1
            r12.<init>(r13, r14, r15, r16, r17, r18, r19)
            r1 = r3
        L_0x0236:
            if (r1 != 0) goto L_0x0239
            return r11
        L_0x0239:
            java.lang.CharSequence r3 = r1.getText()
            if (r27 < 0) goto L_0x02a5
            if (r1 == 0) goto L_0x02a5
            java.util.ArrayList<org.telegram.ui.ArticleViewer$SearchResult> r4 = r7.searchResults
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x02a5
            java.lang.String r4 = r7.searchText
            if (r4 == 0) goto L_0x02a5
            java.lang.String r0 = r0.toString()
            java.lang.String r0 = r0.toLowerCase()
            r4 = 0
        L_0x0256:
            java.lang.String r6 = r7.searchText
            int r4 = r0.indexOf(r6, r4)
            if (r4 < 0) goto L_0x02a5
            java.lang.String r6 = r7.searchText
            int r6 = r6.length()
            int r6 = r6 + r4
            if (r4 == 0) goto L_0x0273
            int r8 = r4 + -1
            char r8 = r0.charAt(r8)
            boolean r8 = org.telegram.messenger.AndroidUtilities.isPunctuationCharacter(r8)
            if (r8 == 0) goto L_0x02a3
        L_0x0273:
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r8 = r7.adapter
            r8 = r8[r2]
            java.util.HashMap r8 = r8.searchTextOffset
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = r7.searchText
            r12.append(r13)
            r12.append(r10)
            r12.append(r9)
            r12.append(r4)
            java.lang.String r12 = r12.toString()
            int r4 = r1.getLineForOffset(r4)
            int r4 = r1.getLineTop(r4)
            int r4 = r27 + r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r8.put(r12, r4)
        L_0x02a3:
            r4 = r6
            goto L_0x0256
        L_0x02a5:
            if (r1 == 0) goto L_0x03bd
            boolean r0 = r3 instanceof android.text.Spanned
            if (r0 == 0) goto L_0x03bd
            android.text.Spanned r3 = (android.text.Spanned) r3
            int r0 = r3.length()     // Catch:{ Exception -> 0x02fe }
            java.lang.Class<org.telegram.ui.Components.AnchorSpan> r4 = org.telegram.ui.Components.AnchorSpan.class
            java.lang.Object[] r0 = r3.getSpans(r2, r0, r4)     // Catch:{ Exception -> 0x02fe }
            org.telegram.ui.Components.AnchorSpan[] r0 = (org.telegram.ui.Components.AnchorSpan[]) r0     // Catch:{ Exception -> 0x02fe }
            int r4 = r1.getLineCount()     // Catch:{ Exception -> 0x02fe }
            if (r0 == 0) goto L_0x02fe
            int r6 = r0.length     // Catch:{ Exception -> 0x02fe }
            if (r6 <= 0) goto L_0x02fe
            r6 = 0
        L_0x02c3:
            int r8 = r0.length     // Catch:{ Exception -> 0x02fe }
            if (r6 >= r8) goto L_0x02fe
            if (r4 > r5) goto L_0x02da
            java.util.HashMap r8 = r31.anchorsOffset     // Catch:{ Exception -> 0x02fe }
            r12 = r0[r6]     // Catch:{ Exception -> 0x02fe }
            java.lang.String r12 = r12.getName()     // Catch:{ Exception -> 0x02fe }
            java.lang.Integer r13 = java.lang.Integer.valueOf(r27)     // Catch:{ Exception -> 0x02fe }
            r8.put(r12, r13)     // Catch:{ Exception -> 0x02fe }
            goto L_0x02fb
        L_0x02da:
            java.util.HashMap r8 = r31.anchorsOffset     // Catch:{ Exception -> 0x02fe }
            r12 = r0[r6]     // Catch:{ Exception -> 0x02fe }
            java.lang.String r12 = r12.getName()     // Catch:{ Exception -> 0x02fe }
            r13 = r0[r6]     // Catch:{ Exception -> 0x02fe }
            int r13 = r3.getSpanStart(r13)     // Catch:{ Exception -> 0x02fe }
            int r13 = r1.getLineForOffset(r13)     // Catch:{ Exception -> 0x02fe }
            int r13 = r1.getLineTop(r13)     // Catch:{ Exception -> 0x02fe }
            int r13 = r27 + r13
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x02fe }
            r8.put(r12, r13)     // Catch:{ Exception -> 0x02fe }
        L_0x02fb:
            int r6 = r6 + 1
            goto L_0x02c3
        L_0x02fe:
            r4 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r6 = 0
            int r8 = r3.length()     // Catch:{ Exception -> 0x035d }
            java.lang.Class<org.telegram.ui.Components.TextPaintWebpageUrlSpan> r12 = org.telegram.ui.Components.TextPaintWebpageUrlSpan.class
            java.lang.Object[] r8 = r3.getSpans(r2, r8, r12)     // Catch:{ Exception -> 0x035d }
            org.telegram.ui.Components.TextPaintWebpageUrlSpan[] r8 = (org.telegram.ui.Components.TextPaintWebpageUrlSpan[]) r8     // Catch:{ Exception -> 0x035d }
            if (r8 == 0) goto L_0x035d
            int r12 = r8.length     // Catch:{ Exception -> 0x035d }
            if (r12 <= 0) goto L_0x035d
            org.telegram.ui.Components.LinkPath r12 = new org.telegram.ui.Components.LinkPath     // Catch:{ Exception -> 0x035d }
            r12.<init>(r5)     // Catch:{ Exception -> 0x035d }
            r12.setAllowReset(r2)     // Catch:{ Exception -> 0x035e }
            r13 = 0
        L_0x031b:
            int r14 = r8.length     // Catch:{ Exception -> 0x035e }
            if (r13 >= r14) goto L_0x0359
            r14 = r8[r13]     // Catch:{ Exception -> 0x035e }
            int r14 = r3.getSpanStart(r14)     // Catch:{ Exception -> 0x035e }
            r15 = r8[r13]     // Catch:{ Exception -> 0x035e }
            int r15 = r3.getSpanEnd(r15)     // Catch:{ Exception -> 0x035e }
            r12.setCurrentLayout(r1, r14, r6)     // Catch:{ Exception -> 0x035e }
            r16 = r8[r13]     // Catch:{ Exception -> 0x035e }
            android.text.TextPaint r16 = r16.getTextPaint()     // Catch:{ Exception -> 0x035e }
            if (r16 == 0) goto L_0x033e
            r16 = r8[r13]     // Catch:{ Exception -> 0x035e }
            android.text.TextPaint r0 = r16.getTextPaint()     // Catch:{ Exception -> 0x035e }
            int r0 = r0.baselineShift     // Catch:{ Exception -> 0x035e }
            goto L_0x033f
        L_0x033e:
            r0 = 0
        L_0x033f:
            if (r0 == 0) goto L_0x034f
            if (r0 <= 0) goto L_0x0346
            r16 = 1084227584(0x40a00000, float:5.0)
            goto L_0x0348
        L_0x0346:
            r16 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
        L_0x0348:
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r16)     // Catch:{ Exception -> 0x035e }
            int r0 = r0 + r16
            goto L_0x0350
        L_0x034f:
            r0 = 0
        L_0x0350:
            r12.setBaselineShift(r0)     // Catch:{ Exception -> 0x035e }
            r1.getSelectionPath(r14, r15, r12)     // Catch:{ Exception -> 0x035e }
            int r13 = r13 + 1
            goto L_0x031b
        L_0x0359:
            r12.setAllowReset(r5)     // Catch:{ Exception -> 0x035e }
            goto L_0x035e
        L_0x035d:
            r12 = r11
        L_0x035e:
            int r0 = r3.length()     // Catch:{ Exception -> 0x03ba }
            java.lang.Class<org.telegram.ui.Components.TextPaintMarkSpan> r8 = org.telegram.ui.Components.TextPaintMarkSpan.class
            java.lang.Object[] r0 = r3.getSpans(r2, r0, r8)     // Catch:{ Exception -> 0x03ba }
            org.telegram.ui.Components.TextPaintMarkSpan[] r0 = (org.telegram.ui.Components.TextPaintMarkSpan[]) r0     // Catch:{ Exception -> 0x03ba }
            if (r0 == 0) goto L_0x03ba
            int r8 = r0.length     // Catch:{ Exception -> 0x03ba }
            if (r8 <= 0) goto L_0x03ba
            org.telegram.ui.Components.LinkPath r8 = new org.telegram.ui.Components.LinkPath     // Catch:{ Exception -> 0x03ba }
            r8.<init>(r5)     // Catch:{ Exception -> 0x03ba }
            r8.setAllowReset(r2)     // Catch:{ Exception -> 0x03b9 }
            r11 = 0
        L_0x0378:
            int r13 = r0.length     // Catch:{ Exception -> 0x03b9 }
            if (r11 >= r13) goto L_0x03b6
            r13 = r0[r11]     // Catch:{ Exception -> 0x03b9 }
            int r13 = r3.getSpanStart(r13)     // Catch:{ Exception -> 0x03b9 }
            r14 = r0[r11]     // Catch:{ Exception -> 0x03b9 }
            int r14 = r3.getSpanEnd(r14)     // Catch:{ Exception -> 0x03b9 }
            r8.setCurrentLayout(r1, r13, r6)     // Catch:{ Exception -> 0x03b9 }
            r15 = r0[r11]     // Catch:{ Exception -> 0x03b9 }
            android.text.TextPaint r15 = r15.getTextPaint()     // Catch:{ Exception -> 0x03b9 }
            if (r15 == 0) goto L_0x039b
            r15 = r0[r11]     // Catch:{ Exception -> 0x03b9 }
            android.text.TextPaint r15 = r15.getTextPaint()     // Catch:{ Exception -> 0x03b9 }
            int r15 = r15.baselineShift     // Catch:{ Exception -> 0x03b9 }
            goto L_0x039c
        L_0x039b:
            r15 = 0
        L_0x039c:
            if (r15 == 0) goto L_0x03ac
            if (r15 <= 0) goto L_0x03a3
            r16 = 1084227584(0x40a00000, float:5.0)
            goto L_0x03a5
        L_0x03a3:
            r16 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
        L_0x03a5:
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r16)     // Catch:{ Exception -> 0x03b9 }
            int r15 = r15 + r16
            goto L_0x03ad
        L_0x03ac:
            r15 = 0
        L_0x03ad:
            r8.setBaselineShift(r15)     // Catch:{ Exception -> 0x03b9 }
            r1.getSelectionPath(r13, r14, r8)     // Catch:{ Exception -> 0x03b9 }
            int r11 = r11 + 1
            goto L_0x0378
        L_0x03b6:
            r8.setAllowReset(r5)     // Catch:{ Exception -> 0x03b9 }
        L_0x03b9:
            r11 = r8
        L_0x03ba:
            r0 = r11
            r11 = r12
            goto L_0x03be
        L_0x03bd:
            r0 = r11
        L_0x03be:
            org.telegram.ui.ArticleViewer$DrawingText r2 = new org.telegram.ui.ArticleViewer$DrawingText
            r2.<init>()
            r2.textLayout = r1
            r2.textPath = r11
            r2.markPath = r0
            r2.parentBlock = r10
            r2.parentText = r9
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.createLayoutForText(android.view.View, java.lang.CharSequence, org.telegram.tgnet.TLRPC$RichText, int, int, org.telegram.tgnet.TLRPC$PageBlock, android.text.Layout$Alignment, int, org.telegram.ui.ArticleViewer$WebpageAdapter):org.telegram.ui.ArticleViewer$DrawingText");
    }

    /* access modifiers changed from: private */
    public void drawLayoutLink(Canvas canvas, DrawingText drawingText) {
        float f;
        float f2;
        if (canvas != null && drawingText != null && this.pressedLinkOwnerLayout == drawingText) {
            if (this.pressedLink != null) {
                canvas.drawPath(this.urlPath, urlPaint);
            } else if (this.drawBlockSelection && drawingText != null) {
                if (drawingText.getLineCount() == 1) {
                    f = drawingText.getLineWidth(0);
                    f2 = drawingText.getLineLeft(0);
                } else {
                    f = (float) drawingText.getWidth();
                    f2 = 0.0f;
                }
                canvas.drawRect(((float) (-AndroidUtilities.dp(2.0f))) + f2, 0.0f, f2 + f + ((float) AndroidUtilities.dp(2.0f)), (float) drawingText.getHeight(), urlPaint);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x01a5, code lost:
        if (r0.isShowing() == false) goto L_0x01a7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01ac  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkLayoutForLinks(org.telegram.ui.ArticleViewer.WebpageAdapter r17, android.view.MotionEvent r18, android.view.View r19, org.telegram.ui.ArticleViewer.DrawingText r20, int r21, int r22) {
        /*
            r16 = this;
            r1 = r16
            r2 = r19
            r0 = r20
            r3 = r21
            r4 = r22
            android.animation.AnimatorSet r5 = r1.pageSwitchAnimation
            r6 = 0
            if (r5 != 0) goto L_0x01df
            if (r2 == 0) goto L_0x01df
            org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r5 = r1.textSelectionHelper
            boolean r5 = r5.isSelectable(r2)
            if (r5 != 0) goto L_0x001b
            goto L_0x01df
        L_0x001b:
            r1.pressedLinkOwnerView = r2
            r5 = 1
            if (r0 == 0) goto L_0x01af
            android.text.StaticLayout r7 = r0.textLayout
            float r8 = r18.getX()
            int r8 = (int) r8
            float r9 = r18.getY()
            int r9 = (int) r9
            int r10 = r18.getAction()
            if (r10 != 0) goto L_0x0110
            r10 = 1325400064(0x4var_, float:2.14748365E9)
            int r11 = r7.getLineCount()
            r12 = 0
            r13 = 0
            r14 = 0
        L_0x003b:
            if (r13 >= r11) goto L_0x0050
            float r15 = r7.getLineWidth(r13)
            float r14 = java.lang.Math.max(r15, r14)
            float r15 = r7.getLineLeft(r13)
            float r10 = java.lang.Math.min(r15, r10)
            int r13 = r13 + 1
            goto L_0x003b
        L_0x0050:
            float r11 = (float) r8
            float r13 = (float) r3
            float r13 = r13 + r10
            int r10 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r10 < 0) goto L_0x01a9
            float r13 = r13 + r14
            int r10 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r10 > 0) goto L_0x01a9
            if (r9 < r4) goto L_0x01a9
            int r10 = r7.getHeight()
            int r10 = r10 + r4
            if (r9 > r10) goto L_0x01a9
            r1.pressedLinkOwnerLayout = r0
            r1.pressedLinkOwnerView = r2
            r1.pressedLayoutY = r4
            java.lang.CharSequence r0 = r7.getText()
            boolean r0 = r0 instanceof android.text.Spannable
            if (r0 == 0) goto L_0x01a9
            int r8 = r8 - r3
            int r9 = r9 - r4
            int r0 = r7.getLineForVertical(r9)     // Catch:{ Exception -> 0x010a }
            float r3 = (float) r8     // Catch:{ Exception -> 0x010a }
            int r4 = r7.getOffsetForHorizontal(r0, r3)     // Catch:{ Exception -> 0x010a }
            float r8 = r7.getLineLeft(r0)     // Catch:{ Exception -> 0x010a }
            int r9 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r9 > 0) goto L_0x01a9
            float r0 = r7.getLineWidth(r0)     // Catch:{ Exception -> 0x010a }
            float r8 = r8 + r0
            int r0 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r0 < 0) goto L_0x01a9
            java.lang.CharSequence r0 = r7.getText()     // Catch:{ Exception -> 0x010a }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x010a }
            java.lang.Class<org.telegram.ui.Components.TextPaintUrlSpan> r3 = org.telegram.ui.Components.TextPaintUrlSpan.class
            java.lang.Object[] r3 = r0.getSpans(r4, r4, r3)     // Catch:{ Exception -> 0x010a }
            org.telegram.ui.Components.TextPaintUrlSpan[] r3 = (org.telegram.ui.Components.TextPaintUrlSpan[]) r3     // Catch:{ Exception -> 0x010a }
            if (r3 == 0) goto L_0x01a9
            int r4 = r3.length     // Catch:{ Exception -> 0x010a }
            if (r4 <= 0) goto L_0x01a9
            r4 = r3[r6]     // Catch:{ Exception -> 0x010a }
            r1.pressedLink = r4     // Catch:{ Exception -> 0x010a }
            int r4 = r0.getSpanStart(r4)     // Catch:{ Exception -> 0x010a }
            org.telegram.ui.Components.TextPaintUrlSpan r8 = r1.pressedLink     // Catch:{ Exception -> 0x010a }
            int r8 = r0.getSpanEnd(r8)     // Catch:{ Exception -> 0x010a }
            r9 = 1
        L_0x00b1:
            int r10 = r3.length     // Catch:{ Exception -> 0x010a }
            if (r9 >= r10) goto L_0x00c9
            r10 = r3[r9]     // Catch:{ Exception -> 0x010a }
            int r11 = r0.getSpanStart(r10)     // Catch:{ Exception -> 0x010a }
            int r13 = r0.getSpanEnd(r10)     // Catch:{ Exception -> 0x010a }
            if (r4 > r11) goto L_0x00c2
            if (r13 <= r8) goto L_0x00c6
        L_0x00c2:
            r1.pressedLink = r10     // Catch:{ Exception -> 0x010a }
            r4 = r11
            r8 = r13
        L_0x00c6:
            int r9 = r9 + 1
            goto L_0x00b1
        L_0x00c9:
            org.telegram.ui.Components.LinkPath r0 = r1.urlPath     // Catch:{ Exception -> 0x0104 }
            r0.setUseRoundRect(r5)     // Catch:{ Exception -> 0x0104 }
            org.telegram.ui.Components.LinkPath r0 = r1.urlPath     // Catch:{ Exception -> 0x0104 }
            r0.setCurrentLayout(r7, r4, r12)     // Catch:{ Exception -> 0x0104 }
            org.telegram.ui.Components.TextPaintUrlSpan r0 = r1.pressedLink     // Catch:{ Exception -> 0x0104 }
            android.text.TextPaint r0 = r0.getTextPaint()     // Catch:{ Exception -> 0x0104 }
            if (r0 == 0) goto L_0x00e4
            org.telegram.ui.Components.TextPaintUrlSpan r0 = r1.pressedLink     // Catch:{ Exception -> 0x0104 }
            android.text.TextPaint r0 = r0.getTextPaint()     // Catch:{ Exception -> 0x0104 }
            int r0 = r0.baselineShift     // Catch:{ Exception -> 0x0104 }
            goto L_0x00e5
        L_0x00e4:
            r0 = 0
        L_0x00e5:
            org.telegram.ui.Components.LinkPath r3 = r1.urlPath     // Catch:{ Exception -> 0x0104 }
            if (r0 == 0) goto L_0x00f6
            if (r0 <= 0) goto L_0x00ee
            r9 = 1084227584(0x40a00000, float:5.0)
            goto L_0x00f0
        L_0x00ee:
            r9 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
        L_0x00f0:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ Exception -> 0x0104 }
            int r0 = r0 + r9
            goto L_0x00f7
        L_0x00f6:
            r0 = 0
        L_0x00f7:
            r3.setBaselineShift(r0)     // Catch:{ Exception -> 0x0104 }
            org.telegram.ui.Components.LinkPath r0 = r1.urlPath     // Catch:{ Exception -> 0x0104 }
            r7.getSelectionPath(r4, r8, r0)     // Catch:{ Exception -> 0x0104 }
            r19.invalidate()     // Catch:{ Exception -> 0x0104 }
            goto L_0x01a9
        L_0x0104:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x010a }
            goto L_0x01a9
        L_0x010a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01a9
        L_0x0110:
            int r0 = r18.getAction()
            if (r0 != r5) goto L_0x0196
            org.telegram.ui.Components.TextPaintUrlSpan r0 = r1.pressedLink
            if (r0 == 0) goto L_0x01a9
            java.lang.String r0 = r0.getUrl()
            if (r0 == 0) goto L_0x01a7
            org.telegram.ui.ActionBar.BottomSheet r3 = r1.linkSheet
            r4 = 0
            if (r3 == 0) goto L_0x012a
            r3.dismiss()
            r1.linkSheet = r4
        L_0x012a:
            r3 = 35
            int r3 = r0.lastIndexOf(r3)
            r7 = -1
            if (r3 == r7) goto L_0x0189
            org.telegram.tgnet.TLRPC$WebPage r4 = r17.currentPage
            org.telegram.tgnet.TLRPC$Page r4 = r4.cached_page
            java.lang.String r4 = r4.url
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x014e
            org.telegram.tgnet.TLRPC$WebPage r4 = r17.currentPage
            org.telegram.tgnet.TLRPC$Page r4 = r4.cached_page
            java.lang.String r4 = r4.url
            java.lang.String r4 = r4.toLowerCase()
            goto L_0x0158
        L_0x014e:
            org.telegram.tgnet.TLRPC$WebPage r4 = r17.currentPage
            java.lang.String r4 = r4.url
            java.lang.String r4 = r4.toLowerCase()
        L_0x0158:
            int r3 = r3 + r5
            java.lang.String r3 = r0.substring(r3)     // Catch:{ Exception -> 0x0164 }
            java.lang.String r7 = "UTF-8"
            java.lang.String r3 = java.net.URLDecoder.decode(r3, r7)     // Catch:{ Exception -> 0x0164 }
            goto L_0x0166
        L_0x0164:
            java.lang.String r3 = ""
        L_0x0166:
            java.lang.String r0 = r0.toLowerCase()
            boolean r0 = r0.contains(r4)
            if (r0 == 0) goto L_0x0186
            boolean r0 = android.text.TextUtils.isEmpty(r3)
            if (r0 == 0) goto L_0x0181
            androidx.recyclerview.widget.LinearLayoutManager[] r0 = r1.layoutManager
            r0 = r0[r6]
            r0.scrollToPositionWithOffset(r6, r6)
            r16.checkScrollAnimated()
            goto L_0x0184
        L_0x0181:
            r1.scrollToAnchor(r3)
        L_0x0184:
            r0 = 1
            goto L_0x0187
        L_0x0186:
            r0 = 0
        L_0x0187:
            r4 = r3
            goto L_0x018a
        L_0x0189:
            r0 = 0
        L_0x018a:
            if (r0 != 0) goto L_0x01a7
            org.telegram.ui.Components.TextPaintUrlSpan r0 = r1.pressedLink
            java.lang.String r0 = r0.getUrl()
            r1.openWebpageUrl(r0, r4)
            goto L_0x01a7
        L_0x0196:
            int r0 = r18.getAction()
            r3 = 3
            if (r0 != r3) goto L_0x01a9
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r1.popupWindow
            if (r0 == 0) goto L_0x01a7
            boolean r0 = r0.isShowing()
            if (r0 != 0) goto L_0x01a9
        L_0x01a7:
            r0 = 1
            goto L_0x01aa
        L_0x01a9:
            r0 = 0
        L_0x01aa:
            if (r0 == 0) goto L_0x01af
            r16.removePressedLink()
        L_0x01af:
            int r0 = r18.getAction()
            if (r0 != 0) goto L_0x01c0
            float r0 = r18.getX()
            float r3 = r18.getY()
            r1.startCheckLongPress(r0, r3, r2)
        L_0x01c0:
            int r0 = r18.getAction()
            if (r0 == 0) goto L_0x01d0
            int r0 = r18.getAction()
            r3 = 2
            if (r0 == r3) goto L_0x01d0
            r16.cancelCheckLongPress()
        L_0x01d0:
            boolean r0 = r2 instanceof org.telegram.ui.ArticleViewer.BlockDetailsCell
            if (r0 == 0) goto L_0x01da
            org.telegram.ui.Components.TextPaintUrlSpan r0 = r1.pressedLink
            if (r0 == 0) goto L_0x01d9
            r6 = 1
        L_0x01d9:
            return r6
        L_0x01da:
            org.telegram.ui.ArticleViewer$DrawingText r0 = r1.pressedLinkOwnerLayout
            if (r0 == 0) goto L_0x01df
            r6 = 1
        L_0x01df:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.checkLayoutForLinks(org.telegram.ui.ArticleViewer$WebpageAdapter, android.view.MotionEvent, android.view.View, org.telegram.ui.ArticleViewer$DrawingText, int, int):boolean");
    }

    /* access modifiers changed from: private */
    public void removePressedLink() {
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

    /* access modifiers changed from: private */
    public void openWebpageUrl(String str, String str2) {
        if (this.openUrlReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.openUrlReqId, false);
            this.openUrlReqId = 0;
        }
        int i = this.lastReqId + 1;
        this.lastReqId = i;
        showProgressView(true, true);
        TLRPC$TL_messages_getWebPage tLRPC$TL_messages_getWebPage = new TLRPC$TL_messages_getWebPage();
        tLRPC$TL_messages_getWebPage.url = str;
        tLRPC$TL_messages_getWebPage.hash = 0;
        this.openUrlReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getWebPage, new RequestDelegate(i, str2, tLRPC$TL_messages_getWebPage) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ String f$2;
            public final /* synthetic */ TLRPC$TL_messages_getWebPage f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ArticleViewer.this.lambda$openWebpageUrl$6$ArticleViewer(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openWebpageUrl$6 */
    public /* synthetic */ void lambda$openWebpageUrl$6$ArticleViewer(int i, String str, TLRPC$TL_messages_getWebPage tLRPC$TL_messages_getWebPage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(i, tLObject, str, tLRPC$TL_messages_getWebPage) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ String f$3;
            public final /* synthetic */ TLRPC$TL_messages_getWebPage f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                ArticleViewer.this.lambda$null$5$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$5 */
    public /* synthetic */ void lambda$null$5$ArticleViewer(int i, TLObject tLObject, String str, TLRPC$TL_messages_getWebPage tLRPC$TL_messages_getWebPage) {
        if (this.openUrlReqId != 0 && i == this.lastReqId) {
            this.openUrlReqId = 0;
            showProgressView(true, false);
            if (this.isVisible) {
                if (tLObject instanceof TLRPC$TL_webPage) {
                    TLRPC$TL_webPage tLRPC$TL_webPage = (TLRPC$TL_webPage) tLObject;
                    if (tLRPC$TL_webPage.cached_page instanceof TLRPC$TL_page) {
                        addPageToStack(tLRPC$TL_webPage, str, 1);
                        return;
                    }
                }
                Browser.openUrl((Context) this.parentActivity, tLRPC$TL_messages_getWebPage.url);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0064, code lost:
        r2 = (org.telegram.ui.ArticleViewer.BlockAudioCell) r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r7, int r8, java.lang.Object... r9) {
        /*
            r6 = this;
            int r8 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            r0 = 1
            r1 = 0
            if (r7 != r8) goto L_0x0034
            r7 = r9[r1]
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            org.telegram.ui.Components.RecyclerListView[] r7 = r6.listView
            if (r7 == 0) goto L_0x00c6
            r7 = 0
        L_0x000f:
            org.telegram.ui.Components.RecyclerListView[] r8 = r6.listView
            int r9 = r8.length
            if (r7 >= r9) goto L_0x00c6
            r8 = r8[r7]
            int r8 = r8.getChildCount()
            r9 = 0
        L_0x001b:
            if (r9 >= r8) goto L_0x0031
            org.telegram.ui.Components.RecyclerListView[] r2 = r6.listView
            r2 = r2[r7]
            android.view.View r2 = r2.getChildAt(r9)
            boolean r3 = r2 instanceof org.telegram.ui.ArticleViewer.BlockAudioCell
            if (r3 == 0) goto L_0x002e
            org.telegram.ui.ArticleViewer$BlockAudioCell r2 = (org.telegram.ui.ArticleViewer.BlockAudioCell) r2
            r2.updateButtonState(r0)
        L_0x002e:
            int r9 = r9 + 1
            goto L_0x001b
        L_0x0031:
            int r7 = r7 + 1
            goto L_0x000f
        L_0x0034:
            int r8 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            if (r7 == r8) goto L_0x0096
            int r8 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            if (r7 != r8) goto L_0x003d
            goto L_0x0096
        L_0x003d:
            int r8 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            if (r7 != r8) goto L_0x00c6
            r7 = r9[r1]
            java.lang.Integer r7 = (java.lang.Integer) r7
            org.telegram.ui.Components.RecyclerListView[] r8 = r6.listView
            if (r8 == 0) goto L_0x00c6
            r8 = 0
        L_0x004a:
            org.telegram.ui.Components.RecyclerListView[] r9 = r6.listView
            int r0 = r9.length
            if (r8 >= r0) goto L_0x00c6
            r9 = r9[r8]
            int r9 = r9.getChildCount()
            r0 = 0
        L_0x0056:
            if (r0 >= r9) goto L_0x0093
            org.telegram.ui.Components.RecyclerListView[] r2 = r6.listView
            r2 = r2[r8]
            android.view.View r2 = r2.getChildAt(r0)
            boolean r3 = r2 instanceof org.telegram.ui.ArticleViewer.BlockAudioCell
            if (r3 == 0) goto L_0x0090
            org.telegram.ui.ArticleViewer$BlockAudioCell r2 = (org.telegram.ui.ArticleViewer.BlockAudioCell) r2
            org.telegram.messenger.MessageObject r3 = r2.getMessageObject()
            if (r3 == 0) goto L_0x0090
            int r4 = r3.getId()
            int r5 = r7.intValue()
            if (r4 != r5) goto L_0x0090
            org.telegram.messenger.MediaController r9 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r9 = r9.getPlayingMessageObject()
            if (r9 == 0) goto L_0x0093
            float r0 = r9.audioProgress
            r3.audioProgress = r0
            int r0 = r9.audioProgressSec
            r3.audioProgressSec = r0
            int r9 = r9.audioPlayerDuration
            r3.audioPlayerDuration = r9
            r2.updatePlayingMessageProgress()
            goto L_0x0093
        L_0x0090:
            int r0 = r0 + 1
            goto L_0x0056
        L_0x0093:
            int r8 = r8 + 1
            goto L_0x004a
        L_0x0096:
            org.telegram.ui.Components.RecyclerListView[] r7 = r6.listView
            if (r7 == 0) goto L_0x00c6
            r7 = 0
        L_0x009b:
            org.telegram.ui.Components.RecyclerListView[] r8 = r6.listView
            int r9 = r8.length
            if (r7 >= r9) goto L_0x00c6
            r8 = r8[r7]
            int r8 = r8.getChildCount()
            r9 = 0
        L_0x00a7:
            if (r9 >= r8) goto L_0x00c3
            org.telegram.ui.Components.RecyclerListView[] r2 = r6.listView
            r2 = r2[r7]
            android.view.View r2 = r2.getChildAt(r9)
            boolean r3 = r2 instanceof org.telegram.ui.ArticleViewer.BlockAudioCell
            if (r3 == 0) goto L_0x00c0
            org.telegram.ui.ArticleViewer$BlockAudioCell r2 = (org.telegram.ui.ArticleViewer.BlockAudioCell) r2
            org.telegram.messenger.MessageObject r3 = r2.getMessageObject()
            if (r3 == 0) goto L_0x00c0
            r2.updateButtonState(r0)
        L_0x00c0:
            int r9 = r9 + 1
            goto L_0x00a7
        L_0x00c3:
            int r7 = r7 + 1
            goto L_0x009b
        L_0x00c6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.didReceivedNotification(int, int, java.lang.Object[]):void");
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

    /* access modifiers changed from: private */
    public void updatePaintSize() {
        for (int i = 0; i < 2; i++) {
            this.adapter[i].notifyDataSetChanged();
        }
    }

    private void updatePaintFonts() {
        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt("font_type", this.selectedFont).commit();
        int i = this.selectedFont;
        Typeface typeface = i == 0 ? Typeface.DEFAULT : Typeface.SERIF;
        Typeface typeface2 = i == 0 ? AndroidUtilities.getTypeface("fonts/ritalic.ttf") : Typeface.create("serif", 2);
        Typeface typeface3 = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmedium.ttf") : Typeface.create("serif", 1);
        Typeface typeface4 = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf") : Typeface.create("serif", 3);
        for (int i2 = 0; i2 < quoteTextPaints.size(); i2++) {
            updateFontEntry(quoteTextPaints.keyAt(i2), quoteTextPaints.valueAt(i2), typeface, typeface4, typeface3, typeface2);
        }
        for (int i3 = 0; i3 < preformattedTextPaints.size(); i3++) {
            updateFontEntry(preformattedTextPaints.keyAt(i3), preformattedTextPaints.valueAt(i3), typeface, typeface4, typeface3, typeface2);
        }
        for (int i4 = 0; i4 < paragraphTextPaints.size(); i4++) {
            updateFontEntry(paragraphTextPaints.keyAt(i4), paragraphTextPaints.valueAt(i4), typeface, typeface4, typeface3, typeface2);
        }
        for (int i5 = 0; i5 < listTextPaints.size(); i5++) {
            updateFontEntry(listTextPaints.keyAt(i5), listTextPaints.valueAt(i5), typeface, typeface4, typeface3, typeface2);
        }
        for (int i6 = 0; i6 < embedPostTextPaints.size(); i6++) {
            updateFontEntry(embedPostTextPaints.keyAt(i6), embedPostTextPaints.valueAt(i6), typeface, typeface4, typeface3, typeface2);
        }
        for (int i7 = 0; i7 < mediaCaptionTextPaints.size(); i7++) {
            updateFontEntry(mediaCaptionTextPaints.keyAt(i7), mediaCaptionTextPaints.valueAt(i7), typeface, typeface4, typeface3, typeface2);
        }
        for (int i8 = 0; i8 < mediaCreditTextPaints.size(); i8++) {
            updateFontEntry(mediaCreditTextPaints.keyAt(i8), mediaCreditTextPaints.valueAt(i8), typeface, typeface4, typeface3, typeface2);
        }
        for (int i9 = 0; i9 < photoCaptionTextPaints.size(); i9++) {
            updateFontEntry(photoCaptionTextPaints.keyAt(i9), photoCaptionTextPaints.valueAt(i9), typeface, typeface4, typeface3, typeface2);
        }
        for (int i10 = 0; i10 < photoCreditTextPaints.size(); i10++) {
            updateFontEntry(photoCreditTextPaints.keyAt(i10), photoCreditTextPaints.valueAt(i10), typeface, typeface4, typeface3, typeface2);
        }
        for (int i11 = 0; i11 < authorTextPaints.size(); i11++) {
            updateFontEntry(authorTextPaints.keyAt(i11), authorTextPaints.valueAt(i11), typeface, typeface4, typeface3, typeface2);
        }
        for (int i12 = 0; i12 < footerTextPaints.size(); i12++) {
            updateFontEntry(footerTextPaints.keyAt(i12), footerTextPaints.valueAt(i12), typeface, typeface4, typeface3, typeface2);
        }
        for (int i13 = 0; i13 < embedPostCaptionTextPaints.size(); i13++) {
            updateFontEntry(embedPostCaptionTextPaints.keyAt(i13), embedPostCaptionTextPaints.valueAt(i13), typeface, typeface4, typeface3, typeface2);
        }
        for (int i14 = 0; i14 < relatedArticleTextPaints.size(); i14++) {
            updateFontEntry(relatedArticleTextPaints.keyAt(i14), relatedArticleTextPaints.valueAt(i14), typeface, typeface4, typeface3, typeface2);
        }
        for (int i15 = 0; i15 < detailsTextPaints.size(); i15++) {
            updateFontEntry(detailsTextPaints.keyAt(i15), detailsTextPaints.valueAt(i15), typeface, typeface4, typeface3, typeface2);
        }
        for (int i16 = 0; i16 < tableTextPaints.size(); i16++) {
            updateFontEntry(tableTextPaints.keyAt(i16), tableTextPaints.valueAt(i16), typeface, typeface4, typeface3, typeface2);
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
        this.backgroundPaint.setColor(Theme.getColor("windowBackgroundWhite"));
        int i = 0;
        while (true) {
            RecyclerListView[] recyclerListViewArr = this.listView;
            if (i >= recyclerListViewArr.length) {
                break;
            }
            recyclerListViewArr[i].setGlowColor(Theme.getColor("windowBackgroundWhite"));
            i++;
        }
        TextPaint textPaint = listTextPointerPaint;
        if (textPaint != null) {
            textPaint.setColor(getTextColor());
        }
        TextPaint textPaint2 = listTextNumPaint;
        if (textPaint2 != null) {
            textPaint2.setColor(getTextColor());
        }
        TextPaint textPaint3 = embedPostAuthorPaint;
        if (textPaint3 != null) {
            textPaint3.setColor(getTextColor());
        }
        TextPaint textPaint4 = channelNamePaint;
        if (textPaint4 != null) {
            textPaint4.setColor(getTextColor());
        }
        TextPaint textPaint5 = channelNamePhotoPaint;
        if (textPaint5 != null) {
            textPaint5.setColor(-1);
        }
        TextPaint textPaint6 = relatedArticleHeaderPaint;
        if (textPaint6 != null) {
            textPaint6.setColor(getTextColor());
        }
        TextPaint textPaint7 = relatedArticleTextPaint;
        if (textPaint7 != null) {
            textPaint7.setColor(getGrayTextColor());
        }
        TextPaint textPaint8 = embedPostDatePaint;
        if (textPaint8 != null) {
            textPaint8.setColor(getGrayTextColor());
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
            TextPaint valueAt = sparseArray.valueAt(i);
            if ((keyAt & 8) == 0 && (keyAt & 512) == 0) {
                valueAt.setColor(getTextColor());
            } else {
                valueAt.setColor(getLinkTextColor());
            }
        }
    }

    public void setParentActivity(Activity activity, BaseFragment baseFragment) {
        Activity activity2 = activity;
        this.parentFragment = baseFragment;
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
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
        WindowView windowView2 = new WindowView(activity2);
        this.windowView = windowView2;
        windowView2.setWillNotDraw(false);
        this.windowView.setClipChildren(true);
        this.windowView.setFocusable(false);
        AnonymousClass7 r0 = new FrameLayout(activity2) {
            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:11:0x004d  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean drawChild(android.graphics.Canvas r11, android.view.View r12, long r13) {
                /*
                    r10 = this;
                    org.telegram.ui.ArticleViewer r2 = org.telegram.ui.ArticleViewer.this
                    org.telegram.ui.ArticleViewer$WindowView r2 = r2.windowView
                    boolean r2 = r2.movingPage
                    if (r2 == 0) goto L_0x00e0
                    int r2 = r10.getMeasuredWidth()
                    org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                    org.telegram.ui.Components.RecyclerListView[] r3 = r3.listView
                    r4 = 0
                    r3 = r3[r4]
                    float r3 = r3.getTranslationX()
                    int r3 = (int) r3
                    org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                    org.telegram.ui.Components.RecyclerListView[] r5 = r5.listView
                    r6 = 1
                    r5 = r5[r6]
                    if (r12 != r5) goto L_0x002b
                    r7 = r3
                    goto L_0x0038
                L_0x002b:
                    org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                    org.telegram.ui.Components.RecyclerListView[] r5 = r5.listView
                    r5 = r5[r4]
                    r7 = r2
                    if (r12 != r5) goto L_0x0038
                    r5 = r3
                    goto L_0x0039
                L_0x0038:
                    r5 = 0
                L_0x0039:
                    int r8 = r11.save()
                    int r9 = r10.getHeight()
                    r11.clipRect(r5, r4, r7, r9)
                    boolean r9 = super.drawChild(r11, r12, r13)
                    r11.restoreToCount(r8)
                    if (r3 == 0) goto L_0x00df
                    org.telegram.ui.ArticleViewer r8 = org.telegram.ui.ArticleViewer.this
                    org.telegram.ui.Components.RecyclerListView[] r8 = r8.listView
                    r4 = r8[r4]
                    r8 = 0
                    if (r12 != r4) goto L_0x00a1
                    int r2 = r2 - r3
                    float r2 = (float) r2
                    r4 = 1101004800(0x41a00000, float:20.0)
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    float r4 = (float) r4
                    float r2 = r2 / r4
                    r4 = 1065353216(0x3var_, float:1.0)
                    float r2 = java.lang.Math.min(r2, r4)
                    float r2 = java.lang.Math.max(r8, r2)
                    org.telegram.ui.ArticleViewer r4 = org.telegram.ui.ArticleViewer.this
                    android.graphics.drawable.Drawable r4 = r4.layerShadowDrawable
                    org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                    android.graphics.drawable.Drawable r5 = r5.layerShadowDrawable
                    int r5 = r5.getIntrinsicWidth()
                    int r5 = r3 - r5
                    int r6 = r12.getTop()
                    int r1 = r12.getBottom()
                    r4.setBounds(r5, r6, r3, r1)
                    org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                    android.graphics.drawable.Drawable r1 = r1.layerShadowDrawable
                    r3 = 1132396544(0x437var_, float:255.0)
                    float r2 = r2 * r3
                    int r2 = (int) r2
                    r1.setAlpha(r2)
                    org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                    android.graphics.drawable.Drawable r1 = r1.layerShadowDrawable
                    r1.draw(r11)
                    goto L_0x00df
                L_0x00a1:
                    org.telegram.ui.ArticleViewer r4 = org.telegram.ui.ArticleViewer.this
                    org.telegram.ui.Components.RecyclerListView[] r4 = r4.listView
                    r4 = r4[r6]
                    if (r12 != r4) goto L_0x00df
                    r1 = 1061997773(0x3f4ccccd, float:0.8)
                    int r3 = r2 - r3
                    float r3 = (float) r3
                    float r2 = (float) r2
                    float r3 = r3 / r2
                    float r1 = java.lang.Math.min(r1, r3)
                    int r2 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
                    if (r2 >= 0) goto L_0x00bc
                    goto L_0x00bd
                L_0x00bc:
                    r8 = r1
                L_0x00bd:
                    org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                    android.graphics.Paint r1 = r1.scrimPaint
                    r2 = 1125711872(0x43190000, float:153.0)
                    float r8 = r8 * r2
                    int r2 = (int) r8
                    int r2 = r2 << 24
                    r1.setColor(r2)
                    float r1 = (float) r5
                    r2 = 0
                    float r3 = (float) r7
                    int r4 = r10.getHeight()
                    float r4 = (float) r4
                    org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                    android.graphics.Paint r5 = r5.scrimPaint
                    r0 = r11
                    r0.drawRect(r1, r2, r3, r4, r5)
                L_0x00df:
                    return r9
                L_0x00e0:
                    boolean r0 = super.drawChild(r11, r12, r13)
                    return r0
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.AnonymousClass7.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
            }
        };
        this.containerView = r0;
        this.windowView.addView(r0, LayoutHelper.createFrame(-1, -1, 51));
        if (Build.VERSION.SDK_INT >= 21) {
            this.windowView.setFitsSystemWindows(true);
            this.containerView.setOnApplyWindowInsetsListener($$Lambda$ArticleViewer$MrG0DY2G_kHKw3iS8TOtt00Oo1o.INSTANCE);
        }
        FrameLayout frameLayout = new FrameLayout(activity2);
        this.fullscreenVideoContainer = frameLayout;
        frameLayout.setBackgroundColor(-16777216);
        this.fullscreenVideoContainer.setVisibility(4);
        this.windowView.addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
        AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(activity2);
        this.fullscreenAspectRatioView = aspectRatioFrameLayout;
        aspectRatioFrameLayout.setVisibility(8);
        this.fullscreenVideoContainer.addView(this.fullscreenAspectRatioView, LayoutHelper.createFrame(-1, -1, 17));
        this.fullscreenTextureView = new TextureView(activity2);
        this.listView = new RecyclerListView[2];
        this.adapter = new WebpageAdapter[2];
        this.layoutManager = new LinearLayoutManager[2];
        int i2 = 0;
        while (i2 < this.listView.length) {
            WebpageAdapter[] webpageAdapterArr = this.adapter;
            final WebpageAdapter webpageAdapter = new WebpageAdapter(this.parentActivity);
            webpageAdapterArr[i2] = webpageAdapter;
            this.listView[i2] = new RecyclerListView(activity2) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    int childCount = getChildCount();
                    int i5 = 0;
                    while (i5 < childCount) {
                        View childAt = getChildAt(i5);
                        if (!(childAt.getTag() instanceof Integer) || ((Integer) childAt.getTag()).intValue() != 90 || childAt.getBottom() >= getMeasuredHeight()) {
                            i5++;
                        } else {
                            int measuredHeight = getMeasuredHeight();
                            childAt.layout(0, measuredHeight - childAt.getMeasuredHeight(), childAt.getMeasuredWidth(), measuredHeight);
                            return;
                        }
                    }
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLink == null && ((ArticleViewer.this.popupWindow == null || !ArticleViewer.this.popupWindow.isShowing()) && (motionEvent.getAction() == 1 || motionEvent.getAction() == 3))) {
                        TextPaintUrlSpan unused = ArticleViewer.this.pressedLink = null;
                        DrawingText unused2 = ArticleViewer.this.pressedLinkOwnerLayout = null;
                        View unused3 = ArticleViewer.this.pressedLinkOwnerView = null;
                    } else if (!(ArticleViewer.this.pressedLinkOwnerLayout == null || ArticleViewer.this.pressedLink == null || motionEvent.getAction() != 1)) {
                        ArticleViewer articleViewer = ArticleViewer.this;
                        boolean unused4 = articleViewer.checkLayoutForLinks(webpageAdapter, motionEvent, articleViewer.pressedLinkOwnerView, ArticleViewer.this.pressedLinkOwnerLayout, 0, 0);
                    }
                    return super.onInterceptTouchEvent(motionEvent);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLink == null && ((ArticleViewer.this.popupWindow == null || !ArticleViewer.this.popupWindow.isShowing()) && (motionEvent.getAction() == 1 || motionEvent.getAction() == 3))) {
                        TextPaintUrlSpan unused = ArticleViewer.this.pressedLink = null;
                        DrawingText unused2 = ArticleViewer.this.pressedLinkOwnerLayout = null;
                        View unused3 = ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                    return super.onTouchEvent(motionEvent);
                }

                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    if (ArticleViewer.this.windowView.movingPage) {
                        ArticleViewer.this.containerView.invalidate();
                        float measuredWidth = f / ((float) getMeasuredWidth());
                        ArticleViewer articleViewer = ArticleViewer.this;
                        articleViewer.setCurrentHeaderHeight((int) (((float) articleViewer.windowView.startMovingHeaderHeight) + (((float) (AndroidUtilities.dp(56.0f) - ArticleViewer.this.windowView.startMovingHeaderHeight)) * measuredWidth)));
                    }
                }
            };
            ((DefaultItemAnimator) this.listView[i2].getItemAnimator()).setDelayAnimations(false);
            RecyclerListView recyclerListView = this.listView[i2];
            LinearLayoutManager[] linearLayoutManagerArr = this.layoutManager;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.parentActivity, 1, false);
            linearLayoutManagerArr[i2] = linearLayoutManager;
            recyclerListView.setLayoutManager(linearLayoutManager);
            this.listView[i2].setAdapter(webpageAdapter);
            this.listView[i2].setClipToPadding(false);
            this.listView[i2].setVisibility(i2 == 0 ? 0 : 8);
            this.listView[i2].setPadding(0, AndroidUtilities.dp(56.0f), 0, 0);
            this.listView[i2].setTopGlowOffset(AndroidUtilities.dp(56.0f));
            this.containerView.addView(this.listView[i2], LayoutHelper.createFrame(-1, -1.0f));
            this.listView[i2].setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
                public final boolean onItemClick(View view, int i) {
                    return ArticleViewer.this.lambda$setParentActivity$8$ArticleViewer(view, i);
                }
            });
            this.listView[i2].setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended(webpageAdapter) {
                public final /* synthetic */ ArticleViewer.WebpageAdapter f$1;

                {
                    this.f$1 = r2;
                }

                public final void onItemClick(View view, int i, float f, float f2) {
                    ArticleViewer.this.lambda$setParentActivity$11$ArticleViewer(this.f$1, view, i, f, f2);
                }
            });
            this.listView[i2].setOnScrollListener(new RecyclerView.OnScrollListener() {
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
            i2++;
        }
        this.headerPaint.setColor(-16777216);
        this.statusBarPaint.setColor(-16777216);
        this.headerProgressPaint.setColor(-14408666);
        AnonymousClass10 r02 = new FrameLayout(activity2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                View view;
                float f;
                float measuredWidth = (float) getMeasuredWidth();
                float measuredHeight = (float) getMeasuredHeight();
                canvas.drawRect(0.0f, 0.0f, measuredWidth, measuredHeight, ArticleViewer.this.headerPaint);
                if (ArticleViewer.this.layoutManager != null) {
                    int findFirstVisibleItemPosition = ArticleViewer.this.layoutManager[0].findFirstVisibleItemPosition();
                    int findLastVisibleItemPosition = ArticleViewer.this.layoutManager[0].findLastVisibleItemPosition();
                    int itemCount = ArticleViewer.this.layoutManager[0].getItemCount();
                    int i = itemCount - 2;
                    if (findLastVisibleItemPosition >= i) {
                        view = ArticleViewer.this.layoutManager[0].findViewByPosition(i);
                    } else {
                        view = ArticleViewer.this.layoutManager[0].findViewByPosition(findFirstVisibleItemPosition);
                    }
                    if (view != null) {
                        float f2 = measuredWidth / ((float) (itemCount - 1));
                        ArticleViewer.this.layoutManager[0].getChildCount();
                        float measuredHeight2 = (float) view.getMeasuredHeight();
                        if (findLastVisibleItemPosition >= i) {
                            f = ((((float) (i - findFirstVisibleItemPosition)) * f2) * ((float) (ArticleViewer.this.listView[0].getMeasuredHeight() - view.getTop()))) / measuredHeight2;
                        } else {
                            f = (1.0f - ((((float) Math.min(0, view.getTop() - ArticleViewer.this.listView[0].getPaddingTop())) + measuredHeight2) / measuredHeight2)) * f2;
                        }
                        canvas.drawRect(0.0f, 0.0f, (((float) findFirstVisibleItemPosition) * f2) + f, measuredHeight, ArticleViewer.this.headerProgressPaint);
                    }
                }
            }
        };
        this.headerView = r02;
        r02.setWillNotDraw(false);
        this.containerView.addView(this.headerView, LayoutHelper.createFrame(-1, 56.0f));
        this.headerView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ArticleViewer.this.lambda$setParentActivity$12$ArticleViewer(view);
            }
        });
        SimpleTextView simpleTextView = new SimpleTextView(activity2);
        this.titleTextView = simpleTextView;
        simpleTextView.setGravity(19);
        this.titleTextView.setTextSize(20);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setTextColor(-5000269);
        this.titleTextView.setPivotX(0.0f);
        this.titleTextView.setPivotY((float) AndroidUtilities.dp(28.0f));
        this.headerView.addView(this.titleTextView, LayoutHelper.createFrame(-1, 56.0f, 51, 72.0f, 0.0f, 96.0f, 0.0f));
        LineProgressView lineProgressView2 = new LineProgressView(activity2);
        this.lineProgressView = lineProgressView2;
        lineProgressView2.setProgressColor(-1);
        this.lineProgressView.setPivotX(0.0f);
        this.lineProgressView.setPivotY((float) AndroidUtilities.dp(2.0f));
        this.headerView.addView(this.lineProgressView, LayoutHelper.createFrame(-1, 2.0f, 83, 0.0f, 0.0f, 0.0f, 1.0f));
        this.lineProgressTickRunnable = new Runnable() {
            public final void run() {
                ArticleViewer.this.lambda$setParentActivity$13$ArticleViewer();
            }
        };
        FrameLayout frameLayout2 = new FrameLayout(activity2);
        this.menuContainer = frameLayout2;
        this.headerView.addView(frameLayout2, LayoutHelper.createFrame(48, 56, 53));
        View view = new View(activity2);
        this.searchShadow = view;
        view.setBackgroundResource(NUM);
        this.searchShadow.setAlpha(0.0f);
        this.containerView.addView(this.searchShadow, LayoutHelper.createFrame(-1, 3.0f, 51, 0.0f, 56.0f, 0.0f, 0.0f));
        FrameLayout frameLayout3 = new FrameLayout(this.parentActivity);
        this.searchContainer = frameLayout3;
        frameLayout3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.searchContainer.setVisibility(4);
        int i3 = Build.VERSION.SDK_INT;
        if (i3 < 21) {
            this.searchContainer.setAlpha(0.0f);
        }
        this.headerView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 56.0f));
        AnonymousClass11 r03 = new EditTextBoldCursor(this, this.parentActivity) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.searchField = r03;
        r03.setCursorWidth(1.5f);
        this.searchField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.searchField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.searchField.setTextSize(1, 18.0f);
        this.searchField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.searchField.setSingleLine(true);
        this.searchField.setHint(LocaleController.getString("Search", NUM));
        this.searchField.setBackgroundResource(0);
        this.searchField.setPadding(0, 0, 0, 0);
        this.searchField.setInputType(this.searchField.getInputType() | 524288);
        if (i3 < 23) {
            this.searchField.setCustomSelectionActionModeCallback(new ActionMode.Callback(this) {
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
        this.searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ArticleViewer.this.lambda$setParentActivity$14$ArticleViewer(textView, i, keyEvent);
            }
        });
        this.searchField.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (ArticleViewer.this.ignoreOnTextChange) {
                    boolean unused = ArticleViewer.this.ignoreOnTextChange = false;
                    return;
                }
                ArticleViewer.this.processSearch(charSequence.toString().toLowerCase());
                if (ArticleViewer.this.clearButton == null) {
                    return;
                }
                if (TextUtils.isEmpty(charSequence)) {
                    if (ArticleViewer.this.clearButton.getTag() != null) {
                        ArticleViewer.this.clearButton.setTag((Object) null);
                        ArticleViewer.this.clearButton.clearAnimation();
                        if (ArticleViewer.this.animateClear) {
                            ArticleViewer.this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(0.0f).setDuration(180).scaleY(0.0f).scaleX(0.0f).rotation(45.0f).withEndAction(new Runnable() {
                                public final void run() {
                                    ArticleViewer.AnonymousClass13.this.lambda$onTextChanged$0$ArticleViewer$13();
                                }
                            }).start();
                            return;
                        }
                        ArticleViewer.this.clearButton.setAlpha(0.0f);
                        ArticleViewer.this.clearButton.setRotation(45.0f);
                        ArticleViewer.this.clearButton.setScaleX(0.0f);
                        ArticleViewer.this.clearButton.setScaleY(0.0f);
                        ArticleViewer.this.clearButton.setVisibility(4);
                        boolean unused2 = ArticleViewer.this.animateClear = true;
                    }
                } else if (ArticleViewer.this.clearButton.getTag() == null) {
                    ArticleViewer.this.clearButton.setTag(1);
                    ArticleViewer.this.clearButton.clearAnimation();
                    ArticleViewer.this.clearButton.setVisibility(0);
                    if (ArticleViewer.this.animateClear) {
                        ArticleViewer.this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(1.0f).setDuration(180).scaleY(1.0f).scaleX(1.0f).rotation(0.0f).start();
                        return;
                    }
                    ArticleViewer.this.clearButton.setAlpha(1.0f);
                    ArticleViewer.this.clearButton.setRotation(0.0f);
                    ArticleViewer.this.clearButton.setScaleX(1.0f);
                    ArticleViewer.this.clearButton.setScaleY(1.0f);
                    boolean unused3 = ArticleViewer.this.animateClear = true;
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onTextChanged$0 */
            public /* synthetic */ void lambda$onTextChanged$0$ArticleViewer$13() {
                ArticleViewer.this.clearButton.setVisibility(4);
            }
        });
        this.searchField.setImeOptions(33554435);
        this.searchField.setTextIsSelectable(false);
        this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1, 36.0f, 16, 72.0f, 0.0f, 48.0f, 0.0f));
        AnonymousClass14 r04 = new ImageView(this.parentActivity) {
            /* access modifiers changed from: protected */
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
        this.clearButton = r04;
        r04.setImageDrawable(new CloseProgressDrawable2());
        this.clearButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
        this.clearButton.setScaleType(ImageView.ScaleType.CENTER);
        this.clearButton.setAlpha(0.0f);
        this.clearButton.setRotation(45.0f);
        this.clearButton.setScaleX(0.0f);
        this.clearButton.setScaleY(0.0f);
        this.clearButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ArticleViewer.this.lambda$setParentActivity$15$ArticleViewer(view);
            }
        });
        this.clearButton.setContentDescription(LocaleController.getString("ClearButton", NUM));
        this.searchContainer.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
        ImageView imageView = new ImageView(activity2);
        this.backButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        BackDrawable backDrawable2 = new BackDrawable(false);
        this.backDrawable = backDrawable2;
        backDrawable2.setAnimationTime(200.0f);
        this.backDrawable.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.backDrawable.setRotatedColor(-5000269);
        this.backDrawable.setRotation(1.0f, false);
        this.backButton.setImageDrawable(this.backDrawable);
        this.backButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.headerView.addView(this.backButton, LayoutHelper.createFrame(54, 56.0f));
        this.backButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ArticleViewer.this.lambda$setParentActivity$16$ArticleViewer(view);
            }
        });
        this.backButton.setContentDescription(LocaleController.getString("AccDescrGoBack", NUM));
        AnonymousClass15 r05 = new ActionBarMenuItem(this.parentActivity, (ActionBarMenu) null, NUM, -5000269) {
            public void toggleSubMenu() {
                super.toggleSubMenu();
                ArticleViewer.this.listView[0].stopScroll();
                ArticleViewer.this.checkScrollAnimated();
            }
        };
        this.menuButton = r05;
        r05.setLayoutInScreen(true);
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
        ContextProgressView contextProgressView = new ContextProgressView(activity2, 2);
        this.progressView = contextProgressView;
        contextProgressView.setVisibility(8);
        this.menuContainer.addView(this.progressView, LayoutHelper.createFrame(48, 56.0f));
        this.menuButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ArticleViewer.this.lambda$setParentActivity$17$ArticleViewer(view);
            }
        });
        this.menuButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() {
            public final void onItemClick(int i) {
                ArticleViewer.this.lambda$setParentActivity$19$ArticleViewer(i);
            }
        });
        AnonymousClass16 r06 = new FrameLayout(this, this.parentActivity) {
            public void onDraw(Canvas canvas) {
                int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.searchPanel = r06;
        r06.setOnTouchListener($$Lambda$ArticleViewer$Kuuu2Ox0hGcNV7t5kbpR3N2L418.INSTANCE);
        this.searchPanel.setWillNotDraw(false);
        this.searchPanel.setVisibility(4);
        this.searchPanel.setFocusable(true);
        this.searchPanel.setFocusableInTouchMode(true);
        this.searchPanel.setClickable(true);
        this.searchPanel.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.containerView.addView(this.searchPanel, LayoutHelper.createFrame(-1, 51, 80));
        ImageView imageView2 = new ImageView(this.parentActivity);
        this.searchUpButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.searchUpButton.setImageResource(NUM);
        this.searchUpButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
        this.searchUpButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("actionBarActionModeDefaultSelector"), 1));
        this.searchPanel.addView(this.searchUpButton, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, 0.0f, 48.0f, 0.0f));
        this.searchUpButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ArticleViewer.this.lambda$setParentActivity$21$ArticleViewer(view);
            }
        });
        this.searchUpButton.setContentDescription(LocaleController.getString("AccDescrSearchNext", NUM));
        ImageView imageView3 = new ImageView(this.parentActivity);
        this.searchDownButton = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.searchDownButton.setImageResource(NUM);
        this.searchDownButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
        this.searchDownButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("actionBarActionModeDefaultSelector"), 1));
        this.searchPanel.addView(this.searchDownButton, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, 0.0f, 0.0f, 0.0f));
        this.searchDownButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ArticleViewer.this.lambda$setParentActivity$22$ArticleViewer(view);
            }
        });
        this.searchDownButton.setContentDescription(LocaleController.getString("AccDescrSearchPrev", NUM));
        SimpleTextView simpleTextView2 = new SimpleTextView(this.parentActivity);
        this.searchCountText = simpleTextView2;
        simpleTextView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.searchCountText.setTextSize(15);
        this.searchCountText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.searchCountText.setGravity(3);
        this.searchPanel.addView(this.searchCountText, LayoutHelper.createFrame(-2, -2.0f, 19, 18.0f, 0.0f, 108.0f, 0.0f));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.windowLayoutParams = layoutParams;
        layoutParams.height = -1;
        layoutParams.format = -3;
        layoutParams.width = -1;
        layoutParams.gravity = 51;
        layoutParams.type = 98;
        layoutParams.softInputMode = 48;
        layoutParams.flags = 131072;
        if (i3 >= 21) {
            layoutParams.flags = 131072 | -NUM;
            if (i3 >= 28) {
                layoutParams.layoutInDisplayCutoutMode = 1;
            }
        }
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = new TextSelectionHelper.ArticleTextSelectionHelper();
        this.textSelectionHelper = articleTextSelectionHelper;
        articleTextSelectionHelper.setParentView(this.listView[0]);
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper2 = this.textSelectionHelper;
        articleTextSelectionHelper2.layoutManager = this.layoutManager[0];
        articleTextSelectionHelper2.setCallback(new TextSelectionHelper.Callback() {
            public void onStateChanged(boolean z) {
                if (z) {
                    ArticleViewer.this.showSearch(false);
                }
            }

            public void onTextCopied() {
                BulletinFactory.of(ArticleViewer.this.containerView).createCopyBulletin(LocaleController.getString("TextCopied", NUM)).show();
            }
        });
        this.containerView.addView(this.textSelectionHelper.getOverlayView(activity2));
        PinchToZoomHelper pinchToZoomHelper2 = new PinchToZoomHelper(this.containerView);
        this.pinchToZoomHelper = pinchToZoomHelper2;
        pinchToZoomHelper2.setClipBoundsListener(new PinchToZoomHelper.ClipBoundsListener() {
            public void getClipTopBottom(float[] fArr) {
                fArr[0] = (float) ArticleViewer.this.currentHeaderHeight;
                fArr[1] = (float) ArticleViewer.this.listView[0].getMeasuredHeight();
            }
        });
        updatePaintColors();
    }

    static /* synthetic */ WindowInsets lambda$setParentActivity$7(View view, WindowInsets windowInsets) {
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return windowInsets.consumeSystemWindowInsets();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setParentActivity$8 */
    public /* synthetic */ boolean lambda$setParentActivity$8$ArticleViewer(View view, int i) {
        if (!(view instanceof BlockRelatedArticlesCell)) {
            return false;
        }
        BlockRelatedArticlesCell blockRelatedArticlesCell = (BlockRelatedArticlesCell) view;
        showCopyPopup(blockRelatedArticlesCell.currentBlock.parent.articles.get(blockRelatedArticlesCell.currentBlock.num).url);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setParentActivity$11 */
    public /* synthetic */ void lambda$setParentActivity$11$ArticleViewer(WebpageAdapter webpageAdapter, View view, int i, float f, float f2) {
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = this.textSelectionHelper;
        if (articleTextSelectionHelper != null) {
            if (articleTextSelectionHelper.isSelectionMode()) {
                this.textSelectionHelper.clear();
                return;
            }
            this.textSelectionHelper.clear();
        }
        if ((view instanceof ReportCell) && webpageAdapter.currentPage != null) {
            ReportCell reportCell = (ReportCell) view;
            if (this.previewsReqId != 0) {
                return;
            }
            if (!reportCell.hasViews || f >= ((float) (view.getMeasuredWidth() / 2))) {
                TLObject userOrChat = MessagesController.getInstance(this.currentAccount).getUserOrChat("previews");
                if (userOrChat instanceof TLRPC$TL_user) {
                    openPreviewsChat((TLRPC$User) userOrChat, webpageAdapter.currentPage.id);
                    return;
                }
                int i2 = UserConfig.selectedAccount;
                long j = webpageAdapter.currentPage.id;
                showProgressView(true, true);
                TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
                tLRPC$TL_contacts_resolveUsername.username = "previews";
                this.previewsReqId = ConnectionsManager.getInstance(i2).sendRequest(tLRPC$TL_contacts_resolveUsername, new RequestDelegate(i2, j) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ long f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        ArticleViewer.this.lambda$null$10$ArticleViewer(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
            }
        } else if (i >= 0 && i < webpageAdapter.localBlocks.size()) {
            TLRPC$PageBlock tLRPC$PageBlock = (TLRPC$PageBlock) webpageAdapter.localBlocks.get(i);
            TLRPC$PageBlock lastNonListPageBlock = getLastNonListPageBlock(tLRPC$PageBlock);
            if (lastNonListPageBlock instanceof TL_pageBlockDetailsChild) {
                lastNonListPageBlock = ((TL_pageBlockDetailsChild) lastNonListPageBlock).block;
            }
            if (lastNonListPageBlock instanceof TLRPC$TL_pageBlockChannel) {
                MessagesController.getInstance(this.currentAccount).openByUserName(((TLRPC$TL_pageBlockChannel) lastNonListPageBlock).channel.username, this.parentFragment, 2);
                close(false, true);
            } else if (lastNonListPageBlock instanceof TL_pageBlockRelatedArticlesChild) {
                TL_pageBlockRelatedArticlesChild tL_pageBlockRelatedArticlesChild = (TL_pageBlockRelatedArticlesChild) lastNonListPageBlock;
                openWebpageUrl(tL_pageBlockRelatedArticlesChild.parent.articles.get(tL_pageBlockRelatedArticlesChild.num).url, (String) null);
            } else if (lastNonListPageBlock instanceof TLRPC$TL_pageBlockDetails) {
                View lastNonListCell = getLastNonListCell(view);
                if (lastNonListCell instanceof BlockDetailsCell) {
                    this.pressedLinkOwnerLayout = null;
                    this.pressedLinkOwnerView = null;
                    if (webpageAdapter.blocks.indexOf(tLRPC$PageBlock) >= 0) {
                        TLRPC$TL_pageBlockDetails tLRPC$TL_pageBlockDetails = (TLRPC$TL_pageBlockDetails) lastNonListPageBlock;
                        tLRPC$TL_pageBlockDetails.open = !tLRPC$TL_pageBlockDetails.open;
                        int itemCount = webpageAdapter.getItemCount();
                        webpageAdapter.updateRows();
                        int abs = Math.abs(webpageAdapter.getItemCount() - itemCount);
                        BlockDetailsCell blockDetailsCell = (BlockDetailsCell) lastNonListCell;
                        blockDetailsCell.arrow.setAnimationProgressAnimated(tLRPC$TL_pageBlockDetails.open ? 0.0f : 1.0f);
                        blockDetailsCell.invalidate();
                        if (abs == 0) {
                            return;
                        }
                        if (tLRPC$TL_pageBlockDetails.open) {
                            webpageAdapter.notifyItemRangeInserted(i + 1, abs);
                        } else {
                            webpageAdapter.notifyItemRangeRemoved(i + 1, abs);
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$10 */
    public /* synthetic */ void lambda$null$10$ArticleViewer(int i, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, i, j) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ long f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ArticleViewer.this.lambda$null$9$ArticleViewer(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$9 */
    public /* synthetic */ void lambda$null$9$ArticleViewer(TLObject tLObject, int i, long j) {
        if (this.previewsReqId != 0) {
            this.previewsReqId = 0;
            showProgressView(true, false);
            if (tLObject != null) {
                TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
                MessagesController.getInstance(i).putUsers(tLRPC$TL_contacts_resolvedPeer.users, false);
                MessagesStorage.getInstance(i).putUsersAndChats(tLRPC$TL_contacts_resolvedPeer.users, tLRPC$TL_contacts_resolvedPeer.chats, false, true);
                if (!tLRPC$TL_contacts_resolvedPeer.users.isEmpty()) {
                    openPreviewsChat(tLRPC$TL_contacts_resolvedPeer.users.get(0), j);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setParentActivity$12 */
    public /* synthetic */ void lambda$setParentActivity$12$ArticleViewer(View view) {
        this.listView[0].smoothScrollToPosition(0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setParentActivity$13 */
    public /* synthetic */ void lambda$setParentActivity$13$ArticleViewer() {
        float currentProgress = 0.7f - this.lineProgressView.getCurrentProgress();
        if (currentProgress > 0.0f) {
            float f = currentProgress < 0.25f ? 0.01f : 0.02f;
            LineProgressView lineProgressView2 = this.lineProgressView;
            lineProgressView2.setProgress(lineProgressView2.getCurrentProgress() + f, true);
            AndroidUtilities.runOnUIThread(this.lineProgressTickRunnable, 100);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setParentActivity$14 */
    public /* synthetic */ boolean lambda$setParentActivity$14$ArticleViewer(TextView textView, int i, KeyEvent keyEvent) {
        if (keyEvent == null) {
            return false;
        }
        if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.searchField);
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setParentActivity$15 */
    public /* synthetic */ void lambda$setParentActivity$15$ArticleViewer(View view) {
        if (this.searchField.length() != 0) {
            this.searchField.setText("");
        }
        this.searchField.requestFocus();
        AndroidUtilities.showKeyboard(this.searchField);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setParentActivity$16 */
    public /* synthetic */ void lambda$setParentActivity$16$ArticleViewer(View view) {
        if (this.searchContainer.getTag() != null) {
            showSearch(false);
        } else {
            close(true, true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setParentActivity$17 */
    public /* synthetic */ void lambda$setParentActivity$17$ArticleViewer(View view) {
        this.menuButton.toggleSubMenu();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setParentActivity$19 */
    public /* synthetic */ void lambda$setParentActivity$19$ArticleViewer(int i) {
        Activity activity;
        String str;
        if (this.adapter[0].currentPage != null && (activity = this.parentActivity) != null) {
            if (i == 1) {
                showSearch(true);
            } else if (i == 2) {
                showDialog(new ShareAlert(this.parentActivity, (ArrayList<MessageObject>) null, this.adapter[0].currentPage.url, false, this.adapter[0].currentPage.url, false));
            } else if (i == 3) {
                if (!TextUtils.isEmpty(this.adapter[0].currentPage.cached_page.url)) {
                    str = this.adapter[0].currentPage.cached_page.url;
                } else {
                    str = this.adapter[0].currentPage.url;
                }
                Browser.openUrl((Context) this.parentActivity, str, true, false);
            } else if (i == 4) {
                BottomSheet.Builder builder = new BottomSheet.Builder(activity);
                builder.setApplyTopPadding(false);
                LinearLayout linearLayout = new LinearLayout(this.parentActivity);
                linearLayout.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
                linearLayout.setOrientation(1);
                HeaderCell headerCell = new HeaderCell(this.parentActivity);
                headerCell.setText(LocaleController.getString("FontSize", NUM));
                linearLayout.addView(headerCell, LayoutHelper.createLinear(-2, -2, 51, 3, 1, 3, 0));
                linearLayout.addView(new TextSizeCell(this.parentActivity), LayoutHelper.createLinear(-1, -2, 51, 3, 0, 3, 0));
                HeaderCell headerCell2 = new HeaderCell(this.parentActivity);
                headerCell2.setText(LocaleController.getString("FontType", NUM));
                linearLayout.addView(headerCell2, LayoutHelper.createLinear(-2, -2, 51, 3, 4, 3, 2));
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
                    this.fontCells[i2].setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            ArticleViewer.this.lambda$null$18$ArticleViewer(view);
                        }
                    });
                    linearLayout.addView(this.fontCells[i2], LayoutHelper.createLinear(-1, 50));
                    i2++;
                }
                builder.setCustomView(linearLayout);
                BottomSheet create = builder.create();
                this.linkSheet = create;
                showDialog(create);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$18 */
    public /* synthetic */ void lambda$null$18$ArticleViewer(View view) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$setParentActivity$21 */
    public /* synthetic */ void lambda$setParentActivity$21$ArticleViewer(View view) {
        scrollToSearchIndex(this.currentSearchIndex - 1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setParentActivity$22 */
    public /* synthetic */ void lambda$setParentActivity$22$ArticleViewer(View view) {
        scrollToSearchIndex(this.currentSearchIndex + 1);
    }

    /* access modifiers changed from: private */
    public void showSearch(final boolean z) {
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null) {
            if ((frameLayout.getTag() != null) != z) {
                this.searchContainer.setTag(z ? 1 : null);
                this.searchResults.clear();
                this.searchText = null;
                this.adapter[0].searchTextOffset.clear();
                this.currentSearchIndex = 0;
                float f = 1.0f;
                if (this.attachedToWindow) {
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
                    if (Build.VERSION.SDK_INT >= 21) {
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
                        Property property = View.ALPHA;
                        float[] fArr = new float[1];
                        fArr[0] = z ? 1.0f : 0.0f;
                        arrayList.add(ObjectAnimator.ofFloat(frameLayout3, property, fArr));
                    }
                    if (!z) {
                        arrayList.add(ObjectAnimator.ofFloat(this.searchPanel, View.ALPHA, new float[]{0.0f}));
                    }
                    View view = this.searchShadow;
                    Property property2 = View.ALPHA;
                    float[] fArr2 = new float[1];
                    if (!z) {
                        f = 0.0f;
                    }
                    fArr2[0] = f;
                    arrayList.add(ObjectAnimator.ofFloat(view, property2, fArr2));
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
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            ArticleViewer.this.lambda$showSearch$23$ArticleViewer();
                        }
                    }, 300);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSearch$23 */
    public /* synthetic */ void lambda$showSearch$23$ArticleViewer() {
        AnimatorSet animatorSet = this.runAfterKeyboardClose;
        if (animatorSet != null) {
            animatorSet.start();
            this.runAfterKeyboardClose = null;
        }
    }

    private void updateSearchButtons() {
        ArrayList<SearchResult> arrayList = this.searchResults;
        if (arrayList != null) {
            this.searchUpButton.setEnabled(!arrayList.isEmpty() && this.currentSearchIndex != 0);
            this.searchDownButton.setEnabled(!this.searchResults.isEmpty() && this.currentSearchIndex != this.searchResults.size() - 1);
            ImageView imageView = this.searchUpButton;
            float f = 1.0f;
            imageView.setAlpha(imageView.isEnabled() ? 1.0f : 0.5f);
            ImageView imageView2 = this.searchDownButton;
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

    private static class SearchResult {
        /* access modifiers changed from: private */
        public TLRPC$PageBlock block;
        /* access modifiers changed from: private */
        public int index;
        /* access modifiers changed from: private */
        public Object text;

        private SearchResult() {
        }
    }

    /* access modifiers changed from: private */
    public void processSearch(String str) {
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
        $$Lambda$ArticleViewer$EfVhDedJc7X00LkU4TPk0iGiljA r1 = new Runnable(str, i) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ArticleViewer.this.lambda$processSearch$26$ArticleViewer(this.f$1, this.f$2);
            }
        };
        this.searchRunnable = r1;
        AndroidUtilities.runOnUIThread(r1, 400);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processSearch$26 */
    public /* synthetic */ void lambda$processSearch$26$ArticleViewer(String str, int i) {
        HashMap hashMap = new HashMap(this.adapter[0].textToBlocks);
        ArrayList arrayList = new ArrayList(this.adapter[0].textBlocks);
        this.searchRunnable = null;
        Utilities.searchQueue.postRunnable(new Runnable(arrayList, hashMap, str, i) {
            public final /* synthetic */ ArrayList f$1;
            public final /* synthetic */ HashMap f$2;
            public final /* synthetic */ String f$3;
            public final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                ArticleViewer.this.lambda$null$25$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: org.telegram.ui.ArticleViewer$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v4, resolved type: org.telegram.ui.ArticleViewer$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: org.telegram.ui.ArticleViewer$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: org.telegram.ui.ArticleViewer$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: java.lang.String} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x005e  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0093 A[SYNTHETIC] */
    /* renamed from: lambda$null$25 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$25$ArticleViewer(java.util.ArrayList r19, java.util.HashMap r20, java.lang.String r21, int r22) {
        /*
            r18 = this;
            r7 = r18
            r8 = r21
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            int r10 = r19.size()
            r11 = 0
            r12 = 0
        L_0x000f:
            if (r12 >= r10) goto L_0x0098
            r13 = r19
            java.lang.Object r14 = r13.get(r12)
            r15 = r20
            java.lang.Object r0 = r15.get(r14)
            r6 = r0
            org.telegram.tgnet.TLRPC$PageBlock r6 = (org.telegram.tgnet.TLRPC$PageBlock) r6
            boolean r0 = r14 instanceof org.telegram.tgnet.TLRPC$RichText
            r5 = 0
            if (r0 == 0) goto L_0x004c
            r4 = r14
            org.telegram.tgnet.TLRPC$RichText r4 = (org.telegram.tgnet.TLRPC$RichText) r4
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r0 = r7.adapter
            r1 = r0[r11]
            r2 = 0
            r16 = 1000(0x3e8, float:1.401E-42)
            r0 = r18
            r3 = r4
            r11 = r5
            r5 = r6
            r17 = r6
            r6 = r16
            java.lang.CharSequence r0 = r0.getText((org.telegram.ui.ArticleViewer.WebpageAdapter) r1, (android.view.View) r2, (org.telegram.tgnet.TLRPC$RichText) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$PageBlock) r5, (int) r6)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x005b
            java.lang.String r0 = r0.toString()
            java.lang.String r0 = r0.toLowerCase()
            r5 = r0
            goto L_0x005c
        L_0x004c:
            r11 = r5
            r17 = r6
            boolean r0 = r14 instanceof java.lang.String
            if (r0 == 0) goto L_0x005b
            r0 = r14
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r5 = r0.toLowerCase()
            goto L_0x005c
        L_0x005b:
            r5 = r11
        L_0x005c:
            if (r5 == 0) goto L_0x0093
            r0 = 0
        L_0x005f:
            int r0 = r5.indexOf(r8, r0)
            if (r0 < 0) goto L_0x0093
            int r1 = r21.length()
            int r1 = r1 + r0
            if (r0 == 0) goto L_0x007c
            int r2 = r0 + -1
            char r2 = r5.charAt(r2)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isPunctuationCharacter(r2)
            if (r2 == 0) goto L_0x0079
            goto L_0x007c
        L_0x0079:
            r0 = r17
            goto L_0x008f
        L_0x007c:
            org.telegram.ui.ArticleViewer$SearchResult r2 = new org.telegram.ui.ArticleViewer$SearchResult
            r2.<init>()
            int unused = r2.index = r0
            r0 = r17
            org.telegram.tgnet.TLRPC$PageBlock unused = r2.block = r0
            java.lang.Object unused = r2.text = r14
            r9.add(r2)
        L_0x008f:
            r17 = r0
            r0 = r1
            goto L_0x005f
        L_0x0093:
            int r12 = r12 + 1
            r11 = 0
            goto L_0x000f
        L_0x0098:
            org.telegram.ui.-$$Lambda$ArticleViewer$fJoNw0j89o5XH3A2YViTC_yZsqQ r0 = new org.telegram.ui.-$$Lambda$ArticleViewer$fJoNw0j89o5XH3A2YViTC_yZsqQ
            r1 = r22
            r0.<init>(r1, r9, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.lambda$null$25$ArticleViewer(java.util.ArrayList, java.util.HashMap, java.lang.String, int):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$24 */
    public /* synthetic */ void lambda$null$24$ArticleViewer(int i, ArrayList arrayList, String str) {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v31, resolved type: java.lang.Integer} */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0059, code lost:
        if (openAllParentBlocks(r4) == false) goto L_0x006d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x005b, code lost:
        org.telegram.ui.ArticleViewer.WebpageAdapter.access$8000(r11.adapter[0]);
        r11.adapter[0].notifyDataSetChanged();
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void scrollToSearchIndex(int r12) {
        /*
            r11 = this;
            if (r12 < 0) goto L_0x0172
            java.util.ArrayList<org.telegram.ui.ArticleViewer$SearchResult> r0 = r11.searchResults
            int r0 = r0.size()
            if (r12 < r0) goto L_0x000c
            goto L_0x0172
        L_0x000c:
            r11.currentSearchIndex = r12
            r11.updateSearchButtons()
            java.util.ArrayList<org.telegram.ui.ArticleViewer$SearchResult> r0 = r11.searchResults
            java.lang.Object r12 = r0.get(r12)
            org.telegram.ui.ArticleViewer$SearchResult r12 = (org.telegram.ui.ArticleViewer.SearchResult) r12
            org.telegram.tgnet.TLRPC$PageBlock r0 = r12.block
            org.telegram.tgnet.TLRPC$PageBlock r0 = r11.getLastNonListPageBlock(r0)
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r1 = r11.adapter
            r2 = 0
            r1 = r1[r2]
            java.util.ArrayList r1 = r1.blocks
            int r1 = r1.size()
            r3 = 0
        L_0x002f:
            if (r3 >= r1) goto L_0x006d
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r4 = r11.adapter
            r4 = r4[r2]
            java.util.ArrayList r4 = r4.blocks
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$PageBlock r4 = (org.telegram.tgnet.TLRPC$PageBlock) r4
            boolean r5 = r4 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild
            if (r5 == 0) goto L_0x006a
            org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r4 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r4
            org.telegram.tgnet.TLRPC$PageBlock r5 = r4.block
            org.telegram.tgnet.TLRPC$PageBlock r6 = r12.block
            if (r5 == r6) goto L_0x0055
            org.telegram.tgnet.TLRPC$PageBlock r5 = r4.block
            if (r5 != r0) goto L_0x006a
        L_0x0055:
            boolean r1 = r11.openAllParentBlocks(r4)
            if (r1 == 0) goto L_0x006d
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r1 = r11.adapter
            r1 = r1[r2]
            r1.updateRows()
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r1 = r11.adapter
            r1 = r1[r2]
            r1.notifyDataSetChanged()
            goto L_0x006d
        L_0x006a:
            int r3 = r3 + 1
            goto L_0x002f
        L_0x006d:
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r1 = r11.adapter
            r1 = r1[r2]
            java.util.ArrayList r1 = r1.localBlocks
            int r1 = r1.size()
            r3 = 0
        L_0x007a:
            r4 = -1
            if (r3 >= r1) goto L_0x00ae
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r5 = r11.adapter
            r5 = r5[r2]
            java.util.ArrayList r5 = r5.localBlocks
            java.lang.Object r5 = r5.get(r3)
            org.telegram.tgnet.TLRPC$PageBlock r5 = (org.telegram.tgnet.TLRPC$PageBlock) r5
            org.telegram.tgnet.TLRPC$PageBlock r6 = r12.block
            if (r5 == r6) goto L_0x00af
            if (r5 != r0) goto L_0x0094
            goto L_0x00af
        L_0x0094:
            boolean r6 = r5 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild
            if (r6 == 0) goto L_0x00ab
            org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r5 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r5
            org.telegram.tgnet.TLRPC$PageBlock r6 = r5.block
            org.telegram.tgnet.TLRPC$PageBlock r7 = r12.block
            if (r6 == r7) goto L_0x00af
            org.telegram.tgnet.TLRPC$PageBlock r5 = r5.block
            if (r5 != r0) goto L_0x00ab
            goto L_0x00af
        L_0x00ab:
            int r3 = r3 + 1
            goto L_0x007a
        L_0x00ae:
            r3 = -1
        L_0x00af:
            if (r3 != r4) goto L_0x00b2
            return
        L_0x00b2:
            boolean r1 = r0 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild
            if (r1 == 0) goto L_0x00cc
            org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r0 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r0
            boolean r0 = r11.openAllParentBlocks(r0)
            if (r0 == 0) goto L_0x00cc
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r0 = r11.adapter
            r0 = r0[r2]
            r0.updateRows()
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r0 = r11.adapter
            r0 = r0[r2]
            r0.notifyDataSetChanged()
        L_0x00cc:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r11.searchText
            r0.append(r1)
            org.telegram.tgnet.TLRPC$PageBlock r1 = r12.block
            r0.append(r1)
            java.lang.Object r1 = r12.text
            r0.append(r1)
            int r1 = r12.index
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r1 = r11.adapter
            r1 = r1[r2]
            java.util.HashMap r1 = r1.searchTextOffset
            java.lang.Object r1 = r1.get(r0)
            java.lang.Integer r1 = (java.lang.Integer) r1
            if (r1 != 0) goto L_0x014e
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r1 = r11.adapter
            r1 = r1[r2]
            org.telegram.tgnet.TLRPC$PageBlock r4 = r12.block
            int r6 = r1.getTypeForBlock(r4)
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r1 = r11.adapter
            r1 = r1[r2]
            r4 = 0
            androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r1.onCreateViewHolder(r4, r6)
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r4 = r11.adapter
            r5 = r4[r2]
            org.telegram.tgnet.TLRPC$PageBlock r8 = r12.block
            r9 = 0
            r10 = 0
            r7 = r1
            r5.bindBlockToHolder(r6, r7, r8, r9, r10)
            android.view.View r12 = r1.itemView
            org.telegram.ui.Components.RecyclerListView[] r1 = r11.listView
            r1 = r1[r2]
            int r1 = r1.getMeasuredWidth()
            r4 = 1073741824(0x40000000, float:2.0)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r4)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r2)
            r12.measure(r1, r4)
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r12 = r11.adapter
            r12 = r12[r2]
            java.util.HashMap r12 = r12.searchTextOffset
            java.lang.Object r12 = r12.get(r0)
            r1 = r12
            java.lang.Integer r1 = (java.lang.Integer) r1
            if (r1 != 0) goto L_0x014e
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
        L_0x014e:
            androidx.recyclerview.widget.LinearLayoutManager[] r12 = r11.layoutManager
            r12 = r12[r2]
            int r0 = r11.currentHeaderHeight
            r4 = 1113587712(0x42600000, float:56.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            int r1 = r1.intValue()
            int r0 = r0 - r1
            r1 = 1120403456(0x42CLASSNAME, float:100.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 + r1
            r12.scrollToPositionWithOffset(r3, r0)
            org.telegram.ui.Components.RecyclerListView[] r12 = r11.listView
            r12 = r12[r2]
            r12.invalidateViews()
            return
        L_0x0172:
            r11.updateSearchButtons()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.scrollToSearchIndex(int):void");
    }

    /* access modifiers changed from: private */
    public void checkScrollAnimated() {
        if (this.currentHeaderHeight != AndroidUtilities.dp(56.0f)) {
            ValueAnimator duration = ValueAnimator.ofObject(new IntEvaluator(), new Object[]{Integer.valueOf(this.currentHeaderHeight), Integer.valueOf(AndroidUtilities.dp(56.0f))}).setDuration(180);
            duration.setInterpolator(new DecelerateInterpolator());
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ArticleViewer.this.lambda$checkScrollAnimated$27$ArticleViewer(valueAnimator);
                }
            });
            duration.start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkScrollAnimated$27 */
    public /* synthetic */ void lambda$checkScrollAnimated$27$ArticleViewer(ValueAnimator valueAnimator) {
        setCurrentHeaderHeight(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    /* access modifiers changed from: private */
    public void setCurrentHeaderHeight(int i) {
        if (this.searchContainer.getTag() == null) {
            int dp = AndroidUtilities.dp(56.0f);
            int max = Math.max(AndroidUtilities.statusBarHeight, AndroidUtilities.dp(24.0f));
            if (i < max) {
                i = max;
            } else if (i > dp) {
                i = dp;
            }
            float f = (float) (dp - max);
            if (f == 0.0f) {
                f = 1.0f;
            }
            this.currentHeaderHeight = i;
            float f2 = ((((float) (i - max)) / f) * 0.2f) + 0.8f;
            this.backButton.setScaleX(f2);
            this.backButton.setScaleY(f2);
            this.backButton.setTranslationY((float) ((dp - this.currentHeaderHeight) / 2));
            this.menuContainer.setScaleX(f2);
            this.menuContainer.setScaleY(f2);
            this.titleTextView.setScaleX(f2);
            this.titleTextView.setScaleY(f2);
            this.lineProgressView.setScaleY(((((float) (i - max)) / f) * 0.5f) + 0.5f);
            this.menuContainer.setTranslationY((float) ((dp - this.currentHeaderHeight) / 2));
            this.titleTextView.setTranslationY((float) ((dp - this.currentHeaderHeight) / 2));
            this.headerView.setTranslationY((float) (this.currentHeaderHeight - dp));
            this.searchShadow.setTranslationY((float) (this.currentHeaderHeight - dp));
            int i2 = 0;
            this.menuButton.setAdditionalYOffset(((-(this.currentHeaderHeight - dp)) / 2) + (Build.VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0));
            this.textSelectionHelper.setTopOffset(this.currentHeaderHeight);
            while (true) {
                RecyclerListView[] recyclerListViewArr = this.listView;
                if (i2 < recyclerListViewArr.length) {
                    recyclerListViewArr[i2].setTopGlowOffset(this.currentHeaderHeight);
                    i2++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkScroll(int i) {
        setCurrentHeaderHeight(this.currentHeaderHeight - i);
    }

    private void openPreviewsChat(TLRPC$User tLRPC$User, long j) {
        if (tLRPC$User != null && (this.parentActivity instanceof LaunchActivity)) {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", tLRPC$User.id);
            bundle.putString("botUser", "webpage" + j);
            ((LaunchActivity) this.parentActivity).presentFragment(new ChatActivity(bundle), false, true);
            close(false, true);
        }
    }

    public boolean open(MessageObject messageObject) {
        return open(messageObject, (TLRPC$WebPage) null, (String) null, true);
    }

    public boolean open(TLRPC$TL_webPage tLRPC$TL_webPage, String str) {
        return open((MessageObject) null, tLRPC$TL_webPage, str, true);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(6:56|(2:58|59)|60|61|(2:63|(1:65))|66) */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0159, code lost:
        r12 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x015a, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x015d, code lost:
        return false;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:60:0x0134 */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00e7  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x013a A[Catch:{ Exception -> 0x0159 }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01ef  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean open(org.telegram.messenger.MessageObject r12, org.telegram.tgnet.TLRPC$WebPage r13, java.lang.String r14, boolean r15) {
        /*
            r11 = this;
            android.app.Activity r0 = r11.parentActivity
            r1 = 0
            if (r0 == 0) goto L_0x01f5
            boolean r0 = r11.isVisible
            if (r0 == 0) goto L_0x000d
            boolean r0 = r11.collapsed
            if (r0 == 0) goto L_0x01f5
        L_0x000d:
            if (r12 != 0) goto L_0x0013
            if (r13 != 0) goto L_0x0013
            goto L_0x01f5
        L_0x0013:
            r0 = -1
            r2 = 35
            r3 = 0
            r4 = 1
            if (r12 == 0) goto L_0x0085
            org.telegram.tgnet.TLRPC$Message r13 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r13 = r13.media
            org.telegram.tgnet.TLRPC$WebPage r13 = r13.webpage
            r14 = 0
        L_0x0021:
            org.telegram.tgnet.TLRPC$Message r5 = r12.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r5.entities
            int r5 = r5.size()
            if (r14 >= r5) goto L_0x0083
            org.telegram.tgnet.TLRPC$Message r5 = r12.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r5.entities
            java.lang.Object r5 = r5.get(r14)
            org.telegram.tgnet.TLRPC$MessageEntity r5 = (org.telegram.tgnet.TLRPC$MessageEntity) r5
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUrl
            if (r6 == 0) goto L_0x0080
            org.telegram.tgnet.TLRPC$Message r6 = r12.messageOwner     // Catch:{ Exception -> 0x007c }
            java.lang.String r6 = r6.message     // Catch:{ Exception -> 0x007c }
            int r7 = r5.offset     // Catch:{ Exception -> 0x007c }
            int r5 = r5.length     // Catch:{ Exception -> 0x007c }
            int r5 = r5 + r7
            java.lang.String r5 = r6.substring(r7, r5)     // Catch:{ Exception -> 0x007c }
            java.lang.String r5 = r5.toLowerCase()     // Catch:{ Exception -> 0x007c }
            org.telegram.tgnet.TLRPC$Page r6 = r13.cached_page     // Catch:{ Exception -> 0x007c }
            java.lang.String r6 = r6.url     // Catch:{ Exception -> 0x007c }
            boolean r6 = android.text.TextUtils.isEmpty(r6)     // Catch:{ Exception -> 0x007c }
            if (r6 != 0) goto L_0x005d
            org.telegram.tgnet.TLRPC$Page r6 = r13.cached_page     // Catch:{ Exception -> 0x007c }
            java.lang.String r6 = r6.url     // Catch:{ Exception -> 0x007c }
            java.lang.String r6 = r6.toLowerCase()     // Catch:{ Exception -> 0x007c }
            goto L_0x0063
        L_0x005d:
            java.lang.String r6 = r13.url     // Catch:{ Exception -> 0x007c }
            java.lang.String r6 = r6.toLowerCase()     // Catch:{ Exception -> 0x007c }
        L_0x0063:
            boolean r7 = r5.contains(r6)     // Catch:{ Exception -> 0x007c }
            if (r7 != 0) goto L_0x006f
            boolean r6 = r6.contains(r5)     // Catch:{ Exception -> 0x007c }
            if (r6 == 0) goto L_0x0080
        L_0x006f:
            int r6 = r5.lastIndexOf(r2)     // Catch:{ Exception -> 0x007c }
            if (r6 == r0) goto L_0x0083
            int r6 = r6 + 1
            java.lang.String r14 = r5.substring(r6)     // Catch:{ Exception -> 0x007c }
            goto L_0x0092
        L_0x007c:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x0080:
            int r14 = r14 + 1
            goto L_0x0021
        L_0x0083:
            r14 = r3
            goto L_0x0092
        L_0x0085:
            if (r14 == 0) goto L_0x0094
            int r2 = r14.lastIndexOf(r2)
            if (r2 == r0) goto L_0x0094
            int r2 = r2 + r4
            java.lang.String r14 = r14.substring(r2)
        L_0x0092:
            r7 = r13
            goto L_0x0096
        L_0x0094:
            r7 = r13
            r14 = r3
        L_0x0096:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WebPage> r13 = r11.pagesStack
            r13.clear()
            r11.collapsed = r1
            android.widget.FrameLayout r13 = r11.containerView
            r0 = 0
            r13.setTranslationX(r0)
            android.widget.FrameLayout r13 = r11.containerView
            r13.setTranslationY(r0)
            org.telegram.ui.Components.RecyclerListView[] r13 = r11.listView
            r13 = r13[r1]
            r13.setTranslationY(r0)
            org.telegram.ui.Components.RecyclerListView[] r13 = r11.listView
            r13 = r13[r1]
            r13.setTranslationX(r0)
            org.telegram.ui.Components.RecyclerListView[] r13 = r11.listView
            r13 = r13[r4]
            r13.setTranslationX(r0)
            org.telegram.ui.Components.RecyclerListView[] r13 = r11.listView
            r13 = r13[r1]
            r2 = 1065353216(0x3var_, float:1.0)
            r13.setAlpha(r2)
            org.telegram.ui.ArticleViewer$WindowView r13 = r11.windowView
            r13.setInnerTranslationX(r0)
            androidx.recyclerview.widget.LinearLayoutManager[] r13 = r11.layoutManager
            r13 = r13[r1]
            r13.scrollToPositionWithOffset(r1, r1)
            r13 = 1113587712(0x42600000, float:56.0)
            if (r15 == 0) goto L_0x00de
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r11.setCurrentHeaderHeight(r2)
            goto L_0x00e1
        L_0x00de:
            r11.checkScrollAnimated()
        L_0x00e1:
            boolean r2 = r11.addPageToStack(r7, r14, r1)
            if (r15 == 0) goto L_0x011a
            if (r2 != 0) goto L_0x00ed
            if (r14 == 0) goto L_0x00ed
            r10 = r14
            goto L_0x00ee
        L_0x00ed:
            r10 = r3
        L_0x00ee:
            org.telegram.tgnet.TLRPC$TL_messages_getWebPage r14 = new org.telegram.tgnet.TLRPC$TL_messages_getWebPage
            r14.<init>()
            java.lang.String r15 = r7.url
            r14.url = r15
            org.telegram.tgnet.TLRPC$Page r15 = r7.cached_page
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_pagePart_layer82
            if (r2 != 0) goto L_0x0107
            boolean r15 = r15.part
            if (r15 == 0) goto L_0x0102
            goto L_0x0107
        L_0x0102:
            int r15 = r7.hash
            r14.hash = r15
            goto L_0x0109
        L_0x0107:
            r14.hash = r1
        L_0x0109:
            int r9 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.tgnet.ConnectionsManager r15 = org.telegram.tgnet.ConnectionsManager.getInstance(r9)
            org.telegram.ui.-$$Lambda$ArticleViewer$4AfJkJkz0NK508Ebn15gbIP5gKQ r2 = new org.telegram.ui.-$$Lambda$ArticleViewer$4AfJkJkz0NK508Ebn15gbIP5gKQ
            r5 = r2
            r6 = r11
            r8 = r12
            r5.<init>(r7, r8, r9, r10)
            r15.sendRequest(r14, r2)
        L_0x011a:
            r11.lastInsets = r3
            boolean r12 = r11.isVisible
            java.lang.String r14 = "window"
            if (r12 != 0) goto L_0x015e
            android.app.Activity r12 = r11.parentActivity
            java.lang.Object r12 = r12.getSystemService(r14)
            android.view.WindowManager r12 = (android.view.WindowManager) r12
            boolean r14 = r11.attachedToWindow
            if (r14 == 0) goto L_0x0134
            org.telegram.ui.ArticleViewer$WindowView r14 = r11.windowView     // Catch:{ Exception -> 0x0134 }
            r12.removeView(r14)     // Catch:{ Exception -> 0x0134 }
        L_0x0134:
            int r14 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0159 }
            r15 = 21
            if (r14 < r15) goto L_0x0147
            android.view.WindowManager$LayoutParams r15 = r11.windowLayoutParams     // Catch:{ Exception -> 0x0159 }
            r2 = -2147417856(0xfffffffvar_, float:-9.2194E-41)
            r15.flags = r2     // Catch:{ Exception -> 0x0159 }
            r2 = 28
            if (r14 < r2) goto L_0x0147
            r15.layoutInDisplayCutoutMode = r4     // Catch:{ Exception -> 0x0159 }
        L_0x0147:
            org.telegram.ui.ArticleViewer$WindowView r14 = r11.windowView     // Catch:{ Exception -> 0x0159 }
            r14.setFocusable(r1)     // Catch:{ Exception -> 0x0159 }
            android.widget.FrameLayout r14 = r11.containerView     // Catch:{ Exception -> 0x0159 }
            r14.setFocusable(r1)     // Catch:{ Exception -> 0x0159 }
            org.telegram.ui.ArticleViewer$WindowView r14 = r11.windowView     // Catch:{ Exception -> 0x0159 }
            android.view.WindowManager$LayoutParams r15 = r11.windowLayoutParams     // Catch:{ Exception -> 0x0159 }
            r12.addView(r14, r15)     // Catch:{ Exception -> 0x0159 }
            goto L_0x0175
        L_0x0159:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
            return r1
        L_0x015e:
            android.view.WindowManager$LayoutParams r12 = r11.windowLayoutParams
            int r15 = r12.flags
            r15 = r15 & -17
            r12.flags = r15
            android.app.Activity r12 = r11.parentActivity
            java.lang.Object r12 = r12.getSystemService(r14)
            android.view.WindowManager r12 = (android.view.WindowManager) r12
            org.telegram.ui.ArticleViewer$WindowView r14 = r11.windowView
            android.view.WindowManager$LayoutParams r15 = r11.windowLayoutParams
            r12.updateViewLayout(r14, r15)
        L_0x0175:
            r11.isVisible = r4
            r11.animationInProgress = r4
            org.telegram.ui.ArticleViewer$WindowView r12 = r11.windowView
            r12.setAlpha(r0)
            android.widget.FrameLayout r12 = r11.containerView
            r12.setAlpha(r0)
            android.animation.AnimatorSet r12 = new android.animation.AnimatorSet
            r12.<init>()
            r14 = 3
            android.animation.Animator[] r14 = new android.animation.Animator[r14]
            org.telegram.ui.ArticleViewer$WindowView r15 = r11.windowView
            android.util.Property r2 = android.view.View.ALPHA
            r5 = 2
            float[] r6 = new float[r5]
            r6 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r15 = android.animation.ObjectAnimator.ofFloat(r15, r2, r6)
            r14[r1] = r15
            android.widget.FrameLayout r15 = r11.containerView
            android.util.Property r2 = android.view.View.ALPHA
            float[] r6 = new float[r5]
            r6 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r15 = android.animation.ObjectAnimator.ofFloat(r15, r2, r6)
            r14[r4] = r15
            org.telegram.ui.ArticleViewer$WindowView r15 = r11.windowView
            android.util.Property r2 = android.view.View.TRANSLATION_X
            float[] r6 = new float[r5]
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r6[r1] = r13
            r6[r4] = r0
            android.animation.ObjectAnimator r13 = android.animation.ObjectAnimator.ofFloat(r15, r2, r6)
            r14[r5] = r13
            r12.playTogether(r14)
            org.telegram.ui.-$$Lambda$ArticleViewer$I5Tcq0ejNA7CLASSNAMEZOWpwXmOVuVYw r13 = new org.telegram.ui.-$$Lambda$ArticleViewer$I5Tcq0ejNA7CLASSNAMEZOWpwXmOVuVYw
            r13.<init>()
            r11.animationEndRunnable = r13
            r13 = 150(0x96, double:7.4E-322)
            r12.setDuration(r13)
            android.view.animation.DecelerateInterpolator r13 = r11.interpolator
            r12.setInterpolator(r13)
            org.telegram.ui.ArticleViewer$21 r13 = new org.telegram.ui.ArticleViewer$21
            r13.<init>()
            r12.addListener(r13)
            long r13 = java.lang.System.currentTimeMillis()
            r11.transitionAnimationStartTime = r13
            org.telegram.ui.-$$Lambda$ArticleViewer$rsbqU7H-DmovNgxBJ2nvyERXU04 r13 = new org.telegram.ui.-$$Lambda$ArticleViewer$rsbqU7H-DmovNgxBJ2nvyERXU04
            r13.<init>(r12)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r13)
            int r12 = android.os.Build.VERSION.SDK_INT
            r13 = 18
            if (r12 < r13) goto L_0x01f4
            android.widget.FrameLayout r12 = r11.containerView
            r12.setLayerType(r5, r3)
        L_0x01f4:
            return r4
        L_0x01f5:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.open(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, java.lang.String, boolean):boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$open$29 */
    public /* synthetic */ void lambda$open$29$ArticleViewer(TLRPC$WebPage tLRPC$WebPage, MessageObject messageObject, int i, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$WebPage, messageObject, i, str) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ TLRPC$WebPage f$2;
            public final /* synthetic */ MessageObject f$3;
            public final /* synthetic */ int f$4;
            public final /* synthetic */ String f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                ArticleViewer.this.lambda$null$28$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$28 */
    public /* synthetic */ void lambda$null$28$ArticleViewer(TLObject tLObject, TLRPC$WebPage tLRPC$WebPage, MessageObject messageObject, int i, String str) {
        TLRPC$Page tLRPC$Page;
        int i2;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        TLObject tLObject2 = tLObject;
        TLRPC$WebPage tLRPC$WebPage2 = tLRPC$WebPage;
        MessageObject messageObject2 = messageObject;
        String str2 = str;
        int i3 = 0;
        if (tLObject2 instanceof TLRPC$TL_webPage) {
            TLRPC$TL_webPage tLRPC$TL_webPage = (TLRPC$TL_webPage) tLObject2;
            if (tLRPC$TL_webPage.cached_page != null) {
                if (!this.pagesStack.isEmpty() && this.pagesStack.get(0) == tLRPC$WebPage2) {
                    if (messageObject2 != null) {
                        messageObject2.messageOwner.media.webpage = tLRPC$TL_webPage;
                        TLRPC$TL_messages_messages tLRPC$TL_messages_messages = new TLRPC$TL_messages_messages();
                        tLRPC$TL_messages_messages.messages.add(messageObject2.messageOwner);
                        MessagesStorage.getInstance(i).putMessages((TLRPC$messages_Messages) tLRPC$TL_messages_messages, messageObject.getDialogId(), -2, 0, false, messageObject2.scheduled);
                    }
                    this.pagesStack.set(0, tLRPC$TL_webPage);
                    if (this.pagesStack.size() == 1) {
                        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().remove("article" + tLRPC$TL_webPage.id).commit();
                        updateInterfaceForCurrentPage(tLRPC$TL_webPage, false, 0);
                        if (str2 != null) {
                            scrollToAnchor(str2);
                        }
                    }
                }
                LongSparseArray longSparseArray = new LongSparseArray(1);
                longSparseArray.put(tLRPC$TL_webPage.id, tLRPC$TL_webPage);
                MessagesStorage.getInstance(i).putWebPages(longSparseArray);
            }
        } else if (tLObject2 instanceof TLRPC$TL_webPageNotModified) {
            TLRPC$TL_webPageNotModified tLRPC$TL_webPageNotModified = (TLRPC$TL_webPageNotModified) tLObject2;
            if (tLRPC$WebPage2 != null && (tLRPC$Page = tLRPC$WebPage2.cached_page) != null && tLRPC$Page.views != (i2 = tLRPC$TL_webPageNotModified.cached_page_views)) {
                tLRPC$Page.views = i2;
                tLRPC$Page.flags |= 8;
                while (true) {
                    WebpageAdapter[] webpageAdapterArr = this.adapter;
                    if (i3 >= webpageAdapterArr.length) {
                        break;
                    }
                    if (webpageAdapterArr[i3].currentPage == tLRPC$WebPage2 && (findViewHolderForAdapterPosition = this.listView[i3].findViewHolderForAdapterPosition(this.adapter[i3].getItemCount() - 1)) != null) {
                        this.adapter[i3].onViewAttachedToWindow(findViewHolderForAdapterPosition);
                    }
                    i3++;
                }
                if (messageObject2 != null) {
                    TLRPC$TL_messages_messages tLRPC$TL_messages_messages2 = new TLRPC$TL_messages_messages();
                    tLRPC$TL_messages_messages2.messages.add(messageObject2.messageOwner);
                    MessagesStorage.getInstance(i).putMessages((TLRPC$messages_Messages) tLRPC$TL_messages_messages2, messageObject.getDialogId(), -2, 0, false, messageObject2.scheduled);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$open$30 */
    public /* synthetic */ void lambda$open$30$ArticleViewer() {
        FrameLayout frameLayout = this.containerView;
        if (frameLayout != null && this.windowView != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                frameLayout.setLayerType(0, (Paint) null);
            }
            this.animationInProgress = 0;
            AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$open$31 */
    public /* synthetic */ void lambda$open$31$ArticleViewer(AnimatorSet animatorSet) {
        this.allowAnimationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.allowAnimationIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats});
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
        if (z2) {
            this.progressView.setVisibility(0);
            this.menuContainer.setEnabled(false);
            this.progressViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.menuButton, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.menuButton, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.menuButton, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f})});
        } else {
            this.menuButton.setVisibility(0);
            this.menuContainer.setEnabled(true);
            this.progressViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.menuButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.menuButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.menuButton, View.ALPHA, new float[]{1.0f})});
        }
        this.progressViewAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animator)) {
                    if (!z2) {
                        ArticleViewer.this.progressView.setVisibility(4);
                    } else {
                        ArticleViewer.this.menuButton.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animator)) {
                    AnimatorSet unused = ArticleViewer.this.progressViewAnimation = null;
                }
            }
        });
        this.progressViewAnimation.setDuration(150);
        this.progressViewAnimation.start();
    }

    /* access modifiers changed from: private */
    public void saveCurrentPagePosition() {
        int findFirstVisibleItemPosition;
        boolean z = false;
        if (this.adapter[0].currentPage != null && (findFirstVisibleItemPosition = this.layoutManager[0].findFirstVisibleItemPosition()) != -1) {
            View findViewByPosition = this.layoutManager[0].findViewByPosition(findFirstVisibleItemPosition);
            int top = findViewByPosition != null ? findViewByPosition.getTop() : 0;
            String str = "article" + this.adapter[0].currentPage.id;
            SharedPreferences.Editor putInt = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt(str, findFirstVisibleItemPosition).putInt(str + "o", top);
            String str2 = str + "r";
            Point point = AndroidUtilities.displaySize;
            if (point.x > point.y) {
                z = true;
            }
            putInt.putBoolean(str2, z).commit();
        }
    }

    private void refreshThemeColors() {
        TextView textView = this.deleteView;
        if (textView != null) {
            textView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 2));
            this.deleteView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
        }
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null) {
            frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
        EditTextBoldCursor editTextBoldCursor = this.searchField;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.searchField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.searchField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        }
        ImageView imageView = this.searchUpButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
            this.searchUpButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("actionBarActionModeDefaultSelector"), 1));
        }
        ImageView imageView2 = this.searchDownButton;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
            this.searchDownButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("actionBarActionModeDefaultSelector"), 1));
        }
        SimpleTextView simpleTextView = this.searchCountText;
        if (simpleTextView != null) {
            simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        }
        ActionBarMenuItem actionBarMenuItem = this.menuButton;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
            this.menuButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
            this.menuButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItemIcon"), true);
        }
        ImageView imageView3 = this.clearButton;
        if (imageView3 != null) {
            imageView3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
        }
        BackDrawable backDrawable2 = this.backDrawable;
        if (backDrawable2 != null) {
            backDrawable2.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        }
    }

    public void close(boolean z, boolean z2) {
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
                if (!z2) {
                    return;
                }
            }
            if (this.textSelectionHelper.isSelectionMode()) {
                this.textSelectionHelper.clear();
            } else if (this.searchContainer.getTag() != null) {
                showSearch(false);
            } else {
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
                        Dialog dialog = this.visibleDialog;
                        if (dialog != null) {
                            dialog.dismiss();
                            this.visibleDialog = null;
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.containerView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.windowView, View.TRANSLATION_X, new float[]{0.0f, (float) AndroidUtilities.dp(56.0f)})});
                    this.animationInProgress = 2;
                    this.animationEndRunnable = new Runnable() {
                        public final void run() {
                            ArticleViewer.this.lambda$close$34$ArticleViewer();
                        }
                    };
                    animatorSet.setDuration(150);
                    animatorSet.setInterpolator(this.interpolator);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (ArticleViewer.this.animationEndRunnable != null) {
                                ArticleViewer.this.animationEndRunnable.run();
                                Runnable unused = ArticleViewer.this.animationEndRunnable = null;
                            }
                        }
                    });
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    if (Build.VERSION.SDK_INT >= 18) {
                        this.containerView.setLayerType(2, (Paint) null);
                    }
                    animatorSet.start();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$close$34 */
    public /* synthetic */ void lambda$close$34$ArticleViewer() {
        FrameLayout frameLayout = this.containerView;
        if (frameLayout != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                frameLayout.setLayerType(0, (Paint) null);
            }
            this.animationInProgress = 0;
            onClosed();
        }
    }

    /* access modifiers changed from: private */
    public void onClosed() {
        this.isVisible = false;
        for (int i = 0; i < this.listView.length; i++) {
            this.adapter[i].cleanup();
        }
        try {
            this.parentActivity.getWindow().clearFlags(128);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        for (int i2 = 0; i2 < this.createdWebViews.size(); i2++) {
            this.createdWebViews.get(i2).destroyWebView(false);
        }
        this.containerView.post(new Runnable() {
            public final void run() {
                ArticleViewer.this.lambda$onClosed$35$ArticleViewer();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onClosed$35 */
    public /* synthetic */ void lambda$onClosed$35$ArticleViewer() {
        try {
            if (this.windowView.getParent() != null) {
                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void loadChannel(BlockChannelCell blockChannelCell, WebpageAdapter webpageAdapter, TLRPC$Chat tLRPC$Chat) {
        if (!this.loadingChannel && !TextUtils.isEmpty(tLRPC$Chat.username)) {
            this.loadingChannel = true;
            TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
            tLRPC$TL_contacts_resolveUsername.username = tLRPC$Chat.username;
            int i = UserConfig.selectedAccount;
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_contacts_resolveUsername, new RequestDelegate(webpageAdapter, i, blockChannelCell) {
                public final /* synthetic */ ArticleViewer.WebpageAdapter f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ ArticleViewer.BlockChannelCell f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ArticleViewer.this.lambda$loadChannel$37$ArticleViewer(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadChannel$37 */
    public /* synthetic */ void lambda$loadChannel$37$ArticleViewer(WebpageAdapter webpageAdapter, int i, BlockChannelCell blockChannelCell, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(webpageAdapter, tLRPC$TL_error, tLObject, i, blockChannelCell) {
            public final /* synthetic */ ArticleViewer.WebpageAdapter f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ TLObject f$3;
            public final /* synthetic */ int f$4;
            public final /* synthetic */ ArticleViewer.BlockChannelCell f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                ArticleViewer.this.lambda$null$36$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$36 */
    public /* synthetic */ void lambda$null$36$ArticleViewer(WebpageAdapter webpageAdapter, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i, BlockChannelCell blockChannelCell) {
        this.loadingChannel = false;
        if (this.parentFragment != null && !webpageAdapter.blocks.isEmpty()) {
            if (tLRPC$TL_error == null) {
                TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
                if (!tLRPC$TL_contacts_resolvedPeer.chats.isEmpty()) {
                    MessagesController.getInstance(i).putUsers(tLRPC$TL_contacts_resolvedPeer.users, false);
                    MessagesController.getInstance(i).putChats(tLRPC$TL_contacts_resolvedPeer.chats, false);
                    MessagesStorage.getInstance(i).putUsersAndChats(tLRPC$TL_contacts_resolvedPeer.users, tLRPC$TL_contacts_resolvedPeer.chats, false, true);
                    TLRPC$Chat tLRPC$Chat = tLRPC$TL_contacts_resolvedPeer.chats.get(0);
                    this.loadedChannel = tLRPC$Chat;
                    if (!tLRPC$Chat.left || tLRPC$Chat.kicked) {
                        blockChannelCell.setState(4, false);
                    } else {
                        blockChannelCell.setState(0, false);
                    }
                } else {
                    blockChannelCell.setState(4, false);
                }
            } else {
                blockChannelCell.setState(4, false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void joinChannel(BlockChannelCell blockChannelCell, TLRPC$Chat tLRPC$Chat) {
        TLRPC$TL_channels_joinChannel tLRPC$TL_channels_joinChannel = new TLRPC$TL_channels_joinChannel();
        tLRPC$TL_channels_joinChannel.channel = MessagesController.getInputChannel(tLRPC$Chat);
        int i = UserConfig.selectedAccount;
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_channels_joinChannel, new RequestDelegate(blockChannelCell, i, tLRPC$TL_channels_joinChannel, tLRPC$Chat) {
            public final /* synthetic */ ArticleViewer.BlockChannelCell f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ TLRPC$TL_channels_joinChannel f$3;
            public final /* synthetic */ TLRPC$Chat f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ArticleViewer.this.lambda$joinChannel$41$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$joinChannel$41 */
    public /* synthetic */ void lambda$joinChannel$41$ArticleViewer(BlockChannelCell blockChannelCell, int i, TLRPC$TL_channels_joinChannel tLRPC$TL_channels_joinChannel, TLRPC$Chat tLRPC$Chat, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        boolean z;
        if (tLRPC$TL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable(blockChannelCell, i, tLRPC$TL_error, tLRPC$TL_channels_joinChannel) {
                public final /* synthetic */ ArticleViewer.BlockChannelCell f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ TLRPC$TL_error f$3;
                public final /* synthetic */ TLRPC$TL_channels_joinChannel f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    ArticleViewer.this.lambda$null$38$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
            return;
        }
        TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
        int i2 = 0;
        while (true) {
            if (i2 >= tLRPC$Updates.updates.size()) {
                z = false;
                break;
            }
            TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i2);
            if ((tLRPC$Update instanceof TLRPC$TL_updateNewChannelMessage) && (((TLRPC$TL_updateNewChannelMessage) tLRPC$Update).message.action instanceof TLRPC$TL_messageActionChatAddUser)) {
                z = true;
                break;
            }
            i2++;
        }
        MessagesController.getInstance(i).processUpdates(tLRPC$Updates, false);
        if (!z) {
            MessagesController.getInstance(i).generateJoinMessage(tLRPC$Chat.id, true);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                ArticleViewer.BlockChannelCell.this.setState(2, false);
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable(i, tLRPC$Chat) {
            public final /* synthetic */ int f$0;
            public final /* synthetic */ TLRPC$Chat f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                MessagesController.getInstance(this.f$0).loadFullChat(this.f$1.id, 0, true);
            }
        }, 1000);
        MessagesStorage.getInstance(i).updateDialogsWithDeletedMessages(new ArrayList(), (ArrayList<Long>) null, true, tLRPC$Chat.id);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$38 */
    public /* synthetic */ void lambda$null$38$ArticleViewer(BlockChannelCell blockChannelCell, int i, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_channels_joinChannel tLRPC$TL_channels_joinChannel) {
        blockChannelCell.setState(0, false);
        AlertsCreator.processError(i, tLRPC$TL_error, this.parentFragment, tLRPC$TL_channels_joinChannel, Boolean.TRUE);
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
        WindowView windowView2;
        if (this.parentActivity != null && (windowView2 = this.windowView) != null) {
            try {
                if (windowView2.getParent() != null) {
                    ((WindowManager) this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
                }
                this.windowView = null;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            for (int i = 0; i < this.createdWebViews.size(); i++) {
                this.createdWebViews.get(i).destroyWebView(true);
            }
            this.createdWebViews.clear();
            try {
                this.parentActivity.getWindow().clearFlags(128);
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
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
                Dialog dialog2 = this.visibleDialog;
                if (dialog2 != null) {
                    dialog2.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                this.visibleDialog = dialog;
                dialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        ArticleViewer.this.lambda$showDialog$42$ArticleViewer(dialogInterface);
                    }
                });
                dialog.show();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showDialog$42 */
    public /* synthetic */ void lambda$showDialog$42$ArticleViewer(DialogInterface dialogInterface) {
        this.visibleDialog = null;
    }

    private static final class WebPageUtils {
        public static TLRPC$Photo getPhotoWithId(TLRPC$WebPage tLRPC$WebPage, long j) {
            if (!(tLRPC$WebPage == null || tLRPC$WebPage.cached_page == null)) {
                TLRPC$Photo tLRPC$Photo = tLRPC$WebPage.photo;
                if (tLRPC$Photo != null && tLRPC$Photo.id == j) {
                    return tLRPC$Photo;
                }
                for (int i = 0; i < tLRPC$WebPage.cached_page.photos.size(); i++) {
                    TLRPC$Photo tLRPC$Photo2 = tLRPC$WebPage.cached_page.photos.get(i);
                    if (tLRPC$Photo2.id == j) {
                        return tLRPC$Photo2;
                    }
                }
            }
            return null;
        }

        public static TLRPC$Document getDocumentWithId(TLRPC$WebPage tLRPC$WebPage, long j) {
            if (!(tLRPC$WebPage == null || tLRPC$WebPage.cached_page == null)) {
                TLRPC$Document tLRPC$Document = tLRPC$WebPage.document;
                if (tLRPC$Document != null && tLRPC$Document.id == j) {
                    return tLRPC$Document;
                }
                for (int i = 0; i < tLRPC$WebPage.cached_page.documents.size(); i++) {
                    TLRPC$Document tLRPC$Document2 = tLRPC$WebPage.cached_page.documents.get(i);
                    if (tLRPC$Document2.id == j) {
                        return tLRPC$Document2;
                    }
                }
            }
            return null;
        }

        public static boolean isVideo(TLRPC$WebPage tLRPC$WebPage, TLRPC$PageBlock tLRPC$PageBlock) {
            TLRPC$Document documentWithId;
            if (!(tLRPC$PageBlock instanceof TLRPC$TL_pageBlockVideo) || (documentWithId = getDocumentWithId(tLRPC$WebPage, ((TLRPC$TL_pageBlockVideo) tLRPC$PageBlock).video_id)) == null) {
                return false;
            }
            return MessageObject.isVideoDocument(documentWithId);
        }

        public static TLObject getMedia(TLRPC$WebPage tLRPC$WebPage, TLRPC$PageBlock tLRPC$PageBlock) {
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPhoto) {
                return getPhotoWithId(tLRPC$WebPage, ((TLRPC$TL_pageBlockPhoto) tLRPC$PageBlock).photo_id);
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockVideo) {
                return getDocumentWithId(tLRPC$WebPage, ((TLRPC$TL_pageBlockVideo) tLRPC$PageBlock).video_id);
            }
            return null;
        }

        public static File getMediaFile(TLRPC$WebPage tLRPC$WebPage, TLRPC$PageBlock tLRPC$PageBlock) {
            TLRPC$Document documentWithId;
            TLRPC$PhotoSize closestPhotoSizeWithSize;
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPhoto) {
                TLRPC$Photo photoWithId = getPhotoWithId(tLRPC$WebPage, ((TLRPC$TL_pageBlockPhoto) tLRPC$PageBlock).photo_id);
                if (photoWithId == null || (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photoWithId.sizes, AndroidUtilities.getPhotoSize())) == null) {
                    return null;
                }
                return FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
            } else if (!(tLRPC$PageBlock instanceof TLRPC$TL_pageBlockVideo) || (documentWithId = getDocumentWithId(tLRPC$WebPage, ((TLRPC$TL_pageBlockVideo) tLRPC$PageBlock).video_id)) == null) {
                return null;
            } else {
                return FileLoader.getPathToAttach(documentWithId, true);
            }
        }
    }

    private class WebpageAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public HashMap<String, Integer> anchors = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<String, Integer> anchorsOffset = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<String, TLRPC$TL_textAnchor> anchorsParent = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<TLRPC$TL_pageBlockAudio, MessageObject> audioBlocks = new HashMap<>();
        /* access modifiers changed from: private */
        public ArrayList<MessageObject> audioMessages = new ArrayList<>();
        /* access modifiers changed from: private */
        public ArrayList<TLRPC$PageBlock> blocks = new ArrayList<>();
        /* access modifiers changed from: private */
        public TLRPC$TL_pageBlockChannel channelBlock;
        private Context context;
        /* access modifiers changed from: private */
        public TLRPC$WebPage currentPage;
        /* access modifiers changed from: private */
        public boolean isRtl;
        /* access modifiers changed from: private */
        public ArrayList<TLRPC$PageBlock> localBlocks = new ArrayList<>();
        /* access modifiers changed from: private */
        public ArrayList<TLRPC$PageBlock> photoBlocks = new ArrayList<>();
        /* access modifiers changed from: private */
        public HashMap<String, Integer> searchTextOffset = new HashMap<>();
        /* access modifiers changed from: private */
        public ArrayList<Object> textBlocks = new ArrayList<>();
        /* access modifiers changed from: private */
        public HashMap<Object, TLRPC$PageBlock> textToBlocks = new HashMap<>();

        public WebpageAdapter(Context context2) {
            this.context = context2;
        }

        /* access modifiers changed from: private */
        public TLRPC$Photo getPhotoWithId(long j) {
            return WebPageUtils.getPhotoWithId(this.currentPage, j);
        }

        /* access modifiers changed from: private */
        public TLRPC$Document getDocumentWithId(long j) {
            return WebPageUtils.getDocumentWithId(this.currentPage, j);
        }

        private void setRichTextParents(TLRPC$RichText tLRPC$RichText, TLRPC$RichText tLRPC$RichText2) {
            if (tLRPC$RichText2 != null) {
                tLRPC$RichText2.parentRichText = tLRPC$RichText;
                if (tLRPC$RichText2 instanceof TLRPC$TL_textFixed) {
                    setRichTextParents(tLRPC$RichText2, ((TLRPC$TL_textFixed) tLRPC$RichText2).text);
                } else if (tLRPC$RichText2 instanceof TLRPC$TL_textItalic) {
                    setRichTextParents(tLRPC$RichText2, ((TLRPC$TL_textItalic) tLRPC$RichText2).text);
                } else if (tLRPC$RichText2 instanceof TLRPC$TL_textBold) {
                    setRichTextParents(tLRPC$RichText2, ((TLRPC$TL_textBold) tLRPC$RichText2).text);
                } else if (tLRPC$RichText2 instanceof TLRPC$TL_textUnderline) {
                    setRichTextParents(tLRPC$RichText2, ((TLRPC$TL_textUnderline) tLRPC$RichText2).text);
                } else if (tLRPC$RichText2 instanceof TLRPC$TL_textStrike) {
                    setRichTextParents(tLRPC$RichText2, ((TLRPC$TL_textStrike) tLRPC$RichText2).text);
                } else if (tLRPC$RichText2 instanceof TLRPC$TL_textEmail) {
                    setRichTextParents(tLRPC$RichText2, ((TLRPC$TL_textEmail) tLRPC$RichText2).text);
                } else if (tLRPC$RichText2 instanceof TLRPC$TL_textPhone) {
                    setRichTextParents(tLRPC$RichText2, ((TLRPC$TL_textPhone) tLRPC$RichText2).text);
                } else if (tLRPC$RichText2 instanceof TLRPC$TL_textUrl) {
                    setRichTextParents(tLRPC$RichText2, ((TLRPC$TL_textUrl) tLRPC$RichText2).text);
                } else if (tLRPC$RichText2 instanceof TLRPC$TL_textConcat) {
                    int size = tLRPC$RichText2.texts.size();
                    for (int i = 0; i < size; i++) {
                        setRichTextParents(tLRPC$RichText2, tLRPC$RichText2.texts.get(i));
                    }
                } else if (tLRPC$RichText2 instanceof TLRPC$TL_textSubscript) {
                    setRichTextParents(tLRPC$RichText2, ((TLRPC$TL_textSubscript) tLRPC$RichText2).text);
                } else if (tLRPC$RichText2 instanceof TLRPC$TL_textSuperscript) {
                    setRichTextParents(tLRPC$RichText2, ((TLRPC$TL_textSuperscript) tLRPC$RichText2).text);
                } else if (tLRPC$RichText2 instanceof TLRPC$TL_textMarked) {
                    setRichTextParents(tLRPC$RichText2, ((TLRPC$TL_textMarked) tLRPC$RichText2).text);
                } else if (tLRPC$RichText2 instanceof TLRPC$TL_textAnchor) {
                    TLRPC$TL_textAnchor tLRPC$TL_textAnchor = (TLRPC$TL_textAnchor) tLRPC$RichText2;
                    setRichTextParents(tLRPC$RichText2, tLRPC$TL_textAnchor.text);
                    String lowerCase = tLRPC$TL_textAnchor.name.toLowerCase();
                    this.anchors.put(lowerCase, Integer.valueOf(this.blocks.size()));
                    TLRPC$RichText tLRPC$RichText3 = tLRPC$TL_textAnchor.text;
                    if (tLRPC$RichText3 instanceof TLRPC$TL_textPlain) {
                        if (!TextUtils.isEmpty(((TLRPC$TL_textPlain) tLRPC$RichText3).text)) {
                            this.anchorsParent.put(lowerCase, tLRPC$TL_textAnchor);
                        }
                    } else if (!(tLRPC$RichText3 instanceof TLRPC$TL_textEmpty)) {
                        this.anchorsParent.put(lowerCase, tLRPC$TL_textAnchor);
                    }
                    this.anchorsOffset.put(lowerCase, -1);
                }
            }
        }

        private void addTextBlock(Object obj, TLRPC$PageBlock tLRPC$PageBlock) {
            if (!(obj instanceof TLRPC$TL_textEmpty) && !this.textToBlocks.containsKey(obj)) {
                this.textToBlocks.put(obj, tLRPC$PageBlock);
                this.textBlocks.add(obj);
            }
        }

        private void setRichTextParents(TLRPC$PageBlock tLRPC$PageBlock) {
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockEmbedPost) {
                TLRPC$TL_pageBlockEmbedPost tLRPC$TL_pageBlockEmbedPost = (TLRPC$TL_pageBlockEmbedPost) tLRPC$PageBlock;
                setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockEmbedPost.caption.text);
                setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockEmbedPost.caption.credit);
                addTextBlock(tLRPC$TL_pageBlockEmbedPost.caption.text, tLRPC$TL_pageBlockEmbedPost);
                addTextBlock(tLRPC$TL_pageBlockEmbedPost.caption.credit, tLRPC$TL_pageBlockEmbedPost);
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockParagraph) {
                TLRPC$TL_pageBlockParagraph tLRPC$TL_pageBlockParagraph = (TLRPC$TL_pageBlockParagraph) tLRPC$PageBlock;
                setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockParagraph.text);
                addTextBlock(tLRPC$TL_pageBlockParagraph.text, tLRPC$TL_pageBlockParagraph);
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockKicker) {
                TLRPC$TL_pageBlockKicker tLRPC$TL_pageBlockKicker = (TLRPC$TL_pageBlockKicker) tLRPC$PageBlock;
                setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockKicker.text);
                addTextBlock(tLRPC$TL_pageBlockKicker.text, tLRPC$TL_pageBlockKicker);
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockFooter) {
                TLRPC$TL_pageBlockFooter tLRPC$TL_pageBlockFooter = (TLRPC$TL_pageBlockFooter) tLRPC$PageBlock;
                setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockFooter.text);
                addTextBlock(tLRPC$TL_pageBlockFooter.text, tLRPC$TL_pageBlockFooter);
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockHeader) {
                TLRPC$TL_pageBlockHeader tLRPC$TL_pageBlockHeader = (TLRPC$TL_pageBlockHeader) tLRPC$PageBlock;
                setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockHeader.text);
                addTextBlock(tLRPC$TL_pageBlockHeader.text, tLRPC$TL_pageBlockHeader);
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPreformatted) {
                TLRPC$TL_pageBlockPreformatted tLRPC$TL_pageBlockPreformatted = (TLRPC$TL_pageBlockPreformatted) tLRPC$PageBlock;
                setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockPreformatted.text);
                addTextBlock(tLRPC$TL_pageBlockPreformatted.text, tLRPC$TL_pageBlockPreformatted);
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSubheader) {
                TLRPC$TL_pageBlockSubheader tLRPC$TL_pageBlockSubheader = (TLRPC$TL_pageBlockSubheader) tLRPC$PageBlock;
                setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockSubheader.text);
                addTextBlock(tLRPC$TL_pageBlockSubheader.text, tLRPC$TL_pageBlockSubheader);
            } else {
                int i = 0;
                if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSlideshow) {
                    TLRPC$TL_pageBlockSlideshow tLRPC$TL_pageBlockSlideshow = (TLRPC$TL_pageBlockSlideshow) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockSlideshow.caption.text);
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockSlideshow.caption.credit);
                    addTextBlock(tLRPC$TL_pageBlockSlideshow.caption.text, tLRPC$TL_pageBlockSlideshow);
                    addTextBlock(tLRPC$TL_pageBlockSlideshow.caption.credit, tLRPC$TL_pageBlockSlideshow);
                    int size = tLRPC$TL_pageBlockSlideshow.items.size();
                    while (i < size) {
                        setRichTextParents(tLRPC$TL_pageBlockSlideshow.items.get(i));
                        i++;
                    }
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPhoto) {
                    TLRPC$TL_pageBlockPhoto tLRPC$TL_pageBlockPhoto = (TLRPC$TL_pageBlockPhoto) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockPhoto.caption.text);
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockPhoto.caption.credit);
                    addTextBlock(tLRPC$TL_pageBlockPhoto.caption.text, tLRPC$TL_pageBlockPhoto);
                    addTextBlock(tLRPC$TL_pageBlockPhoto.caption.credit, tLRPC$TL_pageBlockPhoto);
                } else if (tLRPC$PageBlock instanceof TL_pageBlockListItem) {
                    TL_pageBlockListItem tL_pageBlockListItem = (TL_pageBlockListItem) tLRPC$PageBlock;
                    if (tL_pageBlockListItem.textItem != null) {
                        setRichTextParents((TLRPC$RichText) null, tL_pageBlockListItem.textItem);
                        addTextBlock(tL_pageBlockListItem.textItem, tL_pageBlockListItem);
                    } else if (tL_pageBlockListItem.blockItem != null) {
                        setRichTextParents(tL_pageBlockListItem.blockItem);
                    }
                } else if (tLRPC$PageBlock instanceof TL_pageBlockOrderedListItem) {
                    TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem = (TL_pageBlockOrderedListItem) tLRPC$PageBlock;
                    if (tL_pageBlockOrderedListItem.textItem != null) {
                        setRichTextParents((TLRPC$RichText) null, tL_pageBlockOrderedListItem.textItem);
                        addTextBlock(tL_pageBlockOrderedListItem.textItem, tL_pageBlockOrderedListItem);
                    } else if (tL_pageBlockOrderedListItem.blockItem != null) {
                        setRichTextParents(tL_pageBlockOrderedListItem.blockItem);
                    }
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockCollage) {
                    TLRPC$TL_pageBlockCollage tLRPC$TL_pageBlockCollage = (TLRPC$TL_pageBlockCollage) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockCollage.caption.text);
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockCollage.caption.credit);
                    addTextBlock(tLRPC$TL_pageBlockCollage.caption.text, tLRPC$TL_pageBlockCollage);
                    addTextBlock(tLRPC$TL_pageBlockCollage.caption.credit, tLRPC$TL_pageBlockCollage);
                    int size2 = tLRPC$TL_pageBlockCollage.items.size();
                    while (i < size2) {
                        setRichTextParents(tLRPC$TL_pageBlockCollage.items.get(i));
                        i++;
                    }
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockEmbed) {
                    TLRPC$TL_pageBlockEmbed tLRPC$TL_pageBlockEmbed = (TLRPC$TL_pageBlockEmbed) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockEmbed.caption.text);
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockEmbed.caption.credit);
                    addTextBlock(tLRPC$TL_pageBlockEmbed.caption.text, tLRPC$TL_pageBlockEmbed);
                    addTextBlock(tLRPC$TL_pageBlockEmbed.caption.credit, tLRPC$TL_pageBlockEmbed);
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSubtitle) {
                    TLRPC$TL_pageBlockSubtitle tLRPC$TL_pageBlockSubtitle = (TLRPC$TL_pageBlockSubtitle) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockSubtitle.text);
                    addTextBlock(tLRPC$TL_pageBlockSubtitle.text, tLRPC$TL_pageBlockSubtitle);
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockBlockquote) {
                    TLRPC$TL_pageBlockBlockquote tLRPC$TL_pageBlockBlockquote = (TLRPC$TL_pageBlockBlockquote) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockBlockquote.text);
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockBlockquote.caption);
                    addTextBlock(tLRPC$TL_pageBlockBlockquote.text, tLRPC$TL_pageBlockBlockquote);
                    addTextBlock(tLRPC$TL_pageBlockBlockquote.caption, tLRPC$TL_pageBlockBlockquote);
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockDetails) {
                    TLRPC$TL_pageBlockDetails tLRPC$TL_pageBlockDetails = (TLRPC$TL_pageBlockDetails) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockDetails.title);
                    addTextBlock(tLRPC$TL_pageBlockDetails.title, tLRPC$TL_pageBlockDetails);
                    int size3 = tLRPC$TL_pageBlockDetails.blocks.size();
                    while (i < size3) {
                        setRichTextParents(tLRPC$TL_pageBlockDetails.blocks.get(i));
                        i++;
                    }
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockVideo) {
                    TLRPC$TL_pageBlockVideo tLRPC$TL_pageBlockVideo = (TLRPC$TL_pageBlockVideo) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockVideo.caption.text);
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockVideo.caption.credit);
                    addTextBlock(tLRPC$TL_pageBlockVideo.caption.text, tLRPC$TL_pageBlockVideo);
                    addTextBlock(tLRPC$TL_pageBlockVideo.caption.credit, tLRPC$TL_pageBlockVideo);
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPullquote) {
                    TLRPC$TL_pageBlockPullquote tLRPC$TL_pageBlockPullquote = (TLRPC$TL_pageBlockPullquote) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockPullquote.text);
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockPullquote.caption);
                    addTextBlock(tLRPC$TL_pageBlockPullquote.text, tLRPC$TL_pageBlockPullquote);
                    addTextBlock(tLRPC$TL_pageBlockPullquote.caption, tLRPC$TL_pageBlockPullquote);
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockAudio) {
                    TLRPC$TL_pageBlockAudio tLRPC$TL_pageBlockAudio = (TLRPC$TL_pageBlockAudio) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockAudio.caption.text);
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockAudio.caption.credit);
                    addTextBlock(tLRPC$TL_pageBlockAudio.caption.text, tLRPC$TL_pageBlockAudio);
                    addTextBlock(tLRPC$TL_pageBlockAudio.caption.credit, tLRPC$TL_pageBlockAudio);
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockTable) {
                    TLRPC$TL_pageBlockTable tLRPC$TL_pageBlockTable = (TLRPC$TL_pageBlockTable) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockTable.title);
                    addTextBlock(tLRPC$TL_pageBlockTable.title, tLRPC$TL_pageBlockTable);
                    int size4 = tLRPC$TL_pageBlockTable.rows.size();
                    for (int i2 = 0; i2 < size4; i2++) {
                        TLRPC$TL_pageTableRow tLRPC$TL_pageTableRow = tLRPC$TL_pageBlockTable.rows.get(i2);
                        int size5 = tLRPC$TL_pageTableRow.cells.size();
                        for (int i3 = 0; i3 < size5; i3++) {
                            TLRPC$TL_pageTableCell tLRPC$TL_pageTableCell = tLRPC$TL_pageTableRow.cells.get(i3);
                            setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageTableCell.text);
                            addTextBlock(tLRPC$TL_pageTableCell.text, tLRPC$TL_pageBlockTable);
                        }
                    }
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockTitle) {
                    TLRPC$TL_pageBlockTitle tLRPC$TL_pageBlockTitle = (TLRPC$TL_pageBlockTitle) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockTitle.text);
                    addTextBlock(tLRPC$TL_pageBlockTitle.text, tLRPC$TL_pageBlockTitle);
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockCover) {
                    setRichTextParents(((TLRPC$TL_pageBlockCover) tLRPC$PageBlock).cover);
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockAuthorDate) {
                    TLRPC$TL_pageBlockAuthorDate tLRPC$TL_pageBlockAuthorDate = (TLRPC$TL_pageBlockAuthorDate) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockAuthorDate.author);
                    addTextBlock(tLRPC$TL_pageBlockAuthorDate.author, tLRPC$TL_pageBlockAuthorDate);
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockMap) {
                    TLRPC$TL_pageBlockMap tLRPC$TL_pageBlockMap = (TLRPC$TL_pageBlockMap) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockMap.caption.text);
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockMap.caption.credit);
                    addTextBlock(tLRPC$TL_pageBlockMap.caption.text, tLRPC$TL_pageBlockMap);
                    addTextBlock(tLRPC$TL_pageBlockMap.caption.credit, tLRPC$TL_pageBlockMap);
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockRelatedArticles) {
                    TLRPC$TL_pageBlockRelatedArticles tLRPC$TL_pageBlockRelatedArticles = (TLRPC$TL_pageBlockRelatedArticles) tLRPC$PageBlock;
                    setRichTextParents((TLRPC$RichText) null, tLRPC$TL_pageBlockRelatedArticles.title);
                    addTextBlock(tLRPC$TL_pageBlockRelatedArticles.title, tLRPC$TL_pageBlockRelatedArticles);
                }
            }
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:143:0x04e3  */
        /* JADX WARNING: Removed duplicated region for block: B:144:0x050d  */
        /* JADX WARNING: Removed duplicated region for block: B:151:0x0530  */
        /* JADX WARNING: Removed duplicated region for block: B:176:0x05a6 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void addBlock(org.telegram.ui.ArticleViewer.WebpageAdapter r25, org.telegram.tgnet.TLRPC$PageBlock r26, int r27, int r28, int r29) {
            /*
                r24 = this;
                r7 = r24
                r0 = r25
                r8 = r26
                r9 = r28
                boolean r10 = r8 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild
                if (r10 == 0) goto L_0x0014
                r1 = r8
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r1 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r1
                org.telegram.tgnet.TLRPC$PageBlock r1 = r1.block
                goto L_0x0015
            L_0x0014:
                r1 = r8
            L_0x0015:
                boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockList
                if (r2 != 0) goto L_0x0023
                boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockOrderedList
                if (r2 != 0) goto L_0x0023
                r7.setRichTextParents(r1)
                r7.addAllMediaFromBlock(r0, r1)
            L_0x0023:
                org.telegram.ui.ArticleViewer r2 = org.telegram.ui.ArticleViewer.this
                org.telegram.tgnet.TLRPC$PageBlock r1 = r2.getLastNonListPageBlock(r1)
                boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockUnsupported
                if (r2 == 0) goto L_0x002e
                return
            L_0x002e:
                boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockAnchor
                if (r2 == 0) goto L_0x004a
                java.util.HashMap<java.lang.String, java.lang.Integer> r0 = r7.anchors
                org.telegram.tgnet.TLRPC$TL_pageBlockAnchor r1 = (org.telegram.tgnet.TLRPC$TL_pageBlockAnchor) r1
                java.lang.String r1 = r1.name
                java.lang.String r1 = r1.toLowerCase()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r2 = r7.blocks
                int r2 = r2.size()
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                r0.put(r1, r2)
                return
            L_0x004a:
                boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockList
                if (r2 != 0) goto L_0x0057
                boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockOrderedList
                if (r3 != 0) goto L_0x0057
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r3 = r7.blocks
                r3.add(r8)
            L_0x0057:
                boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockAudio
                r11 = 0
                r12 = 1
                if (r3 == 0) goto L_0x0118
                r0 = r1
                org.telegram.tgnet.TLRPC$TL_pageBlockAudio r0 = (org.telegram.tgnet.TLRPC$TL_pageBlockAudio) r0
                org.telegram.tgnet.TLRPC$TL_message r2 = new org.telegram.tgnet.TLRPC$TL_message
                r2.<init>()
                r2.out = r12
                long r3 = r0.audio_id
                java.lang.Long r3 = java.lang.Long.valueOf(r3)
                int r3 = r3.hashCode()
                int r3 = -r3
                r1.mid = r3
                r2.id = r3
                org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser
                r3.<init>()
                r2.peer_id = r3
                org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser
                r3.<init>()
                r2.from_id = r3
                org.telegram.tgnet.TLRPC$Peer r4 = r2.peer_id
                org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                int r5 = r5.currentAccount
                org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
                int r5 = r5.getClientUserId()
                r4.user_id = r5
                r3.user_id = r5
                long r3 = java.lang.System.currentTimeMillis()
                r5 = 1000(0x3e8, double:4.94E-321)
                long r3 = r3 / r5
                int r4 = (int) r3
                r2.date = r4
                java.lang.String r3 = ""
                r2.message = r3
                org.telegram.tgnet.TLRPC$TL_messageMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument
                r3.<init>()
                r2.media = r3
                org.telegram.tgnet.TLRPC$WebPage r4 = r7.currentPage
                r3.webpage = r4
                int r4 = r3.flags
                r4 = r4 | 3
                r3.flags = r4
                long r4 = r0.audio_id
                org.telegram.tgnet.TLRPC$Document r4 = r7.getDocumentWithId(r4)
                r3.document = r4
                int r3 = r2.flags
                r3 = r3 | 768(0x300, float:1.076E-42)
                r2.flags = r3
                org.telegram.messenger.MessageObject r3 = new org.telegram.messenger.MessageObject
                int r4 = org.telegram.messenger.UserConfig.selectedAccount
                r3.<init>(r4, r2, r11, r12)
                java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r7.audioMessages
                r2.add(r3)
                java.util.HashMap<org.telegram.tgnet.TLRPC$TL_pageBlockAudio, org.telegram.messenger.MessageObject> r2 = r7.audioBlocks
                r2.put(r0, r3)
                java.lang.String r0 = r3.getMusicAuthor(r11)
                java.lang.String r2 = r3.getMusicTitle(r11)
                boolean r3 = android.text.TextUtils.isEmpty(r2)
                if (r3 == 0) goto L_0x00ea
                boolean r3 = android.text.TextUtils.isEmpty(r0)
                if (r3 != 0) goto L_0x05ae
            L_0x00ea:
                boolean r3 = android.text.TextUtils.isEmpty(r2)
                if (r3 != 0) goto L_0x0108
                boolean r3 = android.text.TextUtils.isEmpty(r0)
                if (r3 != 0) goto L_0x0108
                r3 = 2
                java.lang.Object[] r3 = new java.lang.Object[r3]
                r3[r11] = r0
                r3[r12] = r2
                java.lang.String r0 = "%s - %s"
                java.lang.String r0 = java.lang.String.format(r0, r3)
                r7.addTextBlock(r0, r1)
                goto L_0x05ae
            L_0x0108:
                boolean r3 = android.text.TextUtils.isEmpty(r2)
                if (r3 != 0) goto L_0x0113
                r7.addTextBlock(r2, r1)
                goto L_0x05ae
            L_0x0113:
                r7.addTextBlock(r0, r1)
                goto L_0x05ae
            L_0x0118:
                boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockEmbedPost
                r13 = 0
                if (r3 == 0) goto L_0x01a3
                r2 = r1
                org.telegram.tgnet.TLRPC$TL_pageBlockEmbedPost r2 = (org.telegram.tgnet.TLRPC$TL_pageBlockEmbedPost) r2
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r3 = r2.blocks
                boolean r3 = r3.isEmpty()
                if (r3 != 0) goto L_0x05ae
                r3 = -1
                r1.level = r3
            L_0x012b:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r1 = r2.blocks
                int r1 = r1.size()
                if (r11 >= r1) goto L_0x0174
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r1 = r2.blocks
                java.lang.Object r1 = r1.get(r11)
                org.telegram.tgnet.TLRPC$PageBlock r1 = (org.telegram.tgnet.TLRPC$PageBlock) r1
                boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockUnsupported
                if (r3 == 0) goto L_0x0140
                goto L_0x0171
            L_0x0140:
                boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockAnchor
                if (r3 == 0) goto L_0x015c
                org.telegram.tgnet.TLRPC$TL_pageBlockAnchor r1 = (org.telegram.tgnet.TLRPC$TL_pageBlockAnchor) r1
                java.util.HashMap<java.lang.String, java.lang.Integer> r3 = r7.anchors
                java.lang.String r1 = r1.name
                java.lang.String r1 = r1.toLowerCase()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r4 = r7.blocks
                int r4 = r4.size()
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r3.put(r1, r4)
                goto L_0x0171
            L_0x015c:
                r1.level = r12
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r3 = r2.blocks
                int r3 = r3.size()
                int r3 = r3 - r12
                if (r11 != r3) goto L_0x0169
                r1.bottom = r12
            L_0x0169:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r3 = r7.blocks
                r3.add(r1)
                r7.addAllMediaFromBlock(r0, r1)
            L_0x0171:
                int r11 = r11 + 1
                goto L_0x012b
            L_0x0174:
                org.telegram.tgnet.TLRPC$TL_pageCaption r0 = r2.caption
                org.telegram.tgnet.TLRPC$RichText r0 = r0.text
                java.lang.CharSequence r0 = org.telegram.ui.ArticleViewer.getPlainText(r0)
                boolean r0 = android.text.TextUtils.isEmpty(r0)
                if (r0 == 0) goto L_0x0190
                org.telegram.tgnet.TLRPC$TL_pageCaption r0 = r2.caption
                org.telegram.tgnet.TLRPC$RichText r0 = r0.credit
                java.lang.CharSequence r0 = org.telegram.ui.ArticleViewer.getPlainText(r0)
                boolean r0 = android.text.TextUtils.isEmpty(r0)
                if (r0 != 0) goto L_0x05ae
            L_0x0190:
                org.telegram.ui.ArticleViewer$TL_pageBlockEmbedPostCaption r0 = new org.telegram.ui.ArticleViewer$TL_pageBlockEmbedPostCaption
                r0.<init>()
                org.telegram.tgnet.TLRPC$TL_pageBlockEmbedPost unused = r0.parent = r2
                org.telegram.tgnet.TLRPC$TL_pageCaption r1 = r2.caption
                r0.caption = r1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r1 = r7.blocks
                r1.add(r0)
                goto L_0x05ae
            L_0x01a3:
                boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockRelatedArticles
                if (r3 == 0) goto L_0x01e7
                org.telegram.tgnet.TLRPC$TL_pageBlockRelatedArticles r1 = (org.telegram.tgnet.TLRPC$TL_pageBlockRelatedArticles) r1
                org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesShadow r0 = new org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesShadow
                r0.<init>()
                org.telegram.tgnet.TLRPC$TL_pageBlockRelatedArticles unused = r0.parent = r1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r2 = r7.blocks
                int r3 = r2.size()
                int r3 = r3 - r12
                r2.add(r3, r0)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pageRelatedArticle> r0 = r1.articles
                int r0 = r0.size()
            L_0x01c1:
                if (r11 >= r0) goto L_0x01d6
                org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesChild r2 = new org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesChild
                r2.<init>()
                org.telegram.tgnet.TLRPC$TL_pageBlockRelatedArticles unused = r2.parent = r1
                int unused = r2.num = r11
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r3 = r7.blocks
                r3.add(r2)
                int r11 = r11 + 1
                goto L_0x01c1
            L_0x01d6:
                if (r29 != 0) goto L_0x05ae
                org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesShadow r0 = new org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesShadow
                r0.<init>()
                org.telegram.tgnet.TLRPC$TL_pageBlockRelatedArticles unused = r0.parent = r1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r1 = r7.blocks
                r1.add(r0)
                goto L_0x05ae
            L_0x01e7:
                boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockDetails
                if (r3 == 0) goto L_0x021f
                r10 = r1
                org.telegram.tgnet.TLRPC$TL_pageBlockDetails r10 = (org.telegram.tgnet.TLRPC$TL_pageBlockDetails) r10
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r1 = r10.blocks
                int r14 = r1.size()
            L_0x01f4:
                if (r11 >= r14) goto L_0x05ae
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r1 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild
                r1.<init>()
                org.telegram.tgnet.TLRPC$PageBlock unused = r1.parent = r8
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r2 = r10.blocks
                java.lang.Object r2 = r2.get(r11)
                org.telegram.tgnet.TLRPC$PageBlock r2 = (org.telegram.tgnet.TLRPC$PageBlock) r2
                org.telegram.tgnet.TLRPC$PageBlock unused = r1.block = r2
                org.telegram.ui.ArticleViewer r2 = org.telegram.ui.ArticleViewer.this
                org.telegram.tgnet.TLRPC$PageBlock r3 = r2.wrapInTableBlock(r8, r1)
                int r4 = r27 + 1
                r1 = r24
                r2 = r25
                r5 = r28
                r6 = r29
                r1.addBlock(r2, r3, r4, r5, r6)
                int r11 = r11 + 1
                goto L_0x01f4
            L_0x021f:
                java.lang.String r14 = " "
                java.lang.String r15 = ".%d"
                java.lang.String r6 = "%d."
                if (r2 == 0) goto L_0x0394
                r5 = r1
                org.telegram.tgnet.TLRPC$TL_pageBlockList r5 = (org.telegram.tgnet.TLRPC$TL_pageBlockList) r5
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r4 = new org.telegram.ui.ArticleViewer$TL_pageBlockListParent
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                r4.<init>()
                org.telegram.tgnet.TLRPC$TL_pageBlockList unused = r4.pageBlockList = r5
                int unused = r4.level = r9
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageListItem> r1 = r5.items
                int r3 = r1.size()
                r2 = 0
            L_0x023e:
                if (r2 >= r3) goto L_0x05ae
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageListItem> r1 = r5.items
                java.lang.Object r1 = r1.get(r2)
                org.telegram.tgnet.TLRPC$PageListItem r1 = (org.telegram.tgnet.TLRPC$PageListItem) r1
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r11 = new org.telegram.ui.ArticleViewer$TL_pageBlockListItem
                org.telegram.ui.ArticleViewer r12 = org.telegram.ui.ArticleViewer.this
                r11.<init>()
                int unused = r11.index = r2
                org.telegram.ui.ArticleViewer.TL_pageBlockListParent unused = r11.parent = r4
                boolean r12 = r5.ordered
                if (r12 == 0) goto L_0x0287
                boolean r12 = r7.isRtl
                if (r12 == 0) goto L_0x0272
                r12 = 1
                java.lang.Object[] r13 = new java.lang.Object[r12]
                int r17 = r2 + 1
                java.lang.Integer r17 = java.lang.Integer.valueOf(r17)
                r16 = 0
                r13[r16] = r17
                java.lang.String r13 = java.lang.String.format(r15, r13)
                java.lang.String unused = r11.num = r13
                goto L_0x028d
            L_0x0272:
                r12 = 1
                r16 = 0
                java.lang.Object[] r13 = new java.lang.Object[r12]
                int r12 = r2 + 1
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                r13[r16] = r12
                java.lang.String r12 = java.lang.String.format(r6, r13)
                java.lang.String unused = r11.num = r12
                goto L_0x028d
            L_0x0287:
                java.lang.String r12 = "•"
                java.lang.String unused = r11.num = r12
            L_0x028d:
                java.util.ArrayList r12 = r4.items
                r12.add(r11)
                boolean r12 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageListItemText
                if (r12 == 0) goto L_0x02a1
                r12 = r1
                org.telegram.tgnet.TLRPC$TL_pageListItemText r12 = (org.telegram.tgnet.TLRPC$TL_pageListItemText) r12
                org.telegram.tgnet.TLRPC$RichText r12 = r12.text
                org.telegram.tgnet.TLRPC$RichText unused = r11.textItem = r12
                goto L_0x02cb
            L_0x02a1:
                boolean r12 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageListItemBlocks
                if (r12 == 0) goto L_0x02cb
                r12 = r1
                org.telegram.tgnet.TLRPC$TL_pageListItemBlocks r12 = (org.telegram.tgnet.TLRPC$TL_pageListItemBlocks) r12
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r13 = r12.blocks
                boolean r13 = r13.isEmpty()
                if (r13 != 0) goto L_0x02bd
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r12 = r12.blocks
                r13 = 0
                java.lang.Object r12 = r12.get(r13)
                org.telegram.tgnet.TLRPC$PageBlock r12 = (org.telegram.tgnet.TLRPC$PageBlock) r12
                org.telegram.tgnet.TLRPC$PageBlock unused = r11.blockItem = r12
                goto L_0x02cb
            L_0x02bd:
                org.telegram.tgnet.TLRPC$TL_pageListItemText r1 = new org.telegram.tgnet.TLRPC$TL_pageListItemText
                r1.<init>()
                org.telegram.tgnet.TLRPC$TL_textPlain r12 = new org.telegram.tgnet.TLRPC$TL_textPlain
                r12.<init>()
                r12.text = r14
                r1.text = r12
            L_0x02cb:
                r12 = r1
                if (r10 == 0) goto L_0x02f9
                r1 = r8
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r1 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r1
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r13 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild
                r0 = 0
                r13.<init>()
                org.telegram.tgnet.TLRPC$PageBlock r0 = r1.parent
                org.telegram.tgnet.TLRPC$PageBlock unused = r13.parent = r0
                org.telegram.tgnet.TLRPC$PageBlock unused = r13.block = r11
                int r0 = r9 + 1
                r1 = r24
                r19 = r2
                r2 = r25
                r20 = r3
                r3 = r13
                r13 = r4
                r4 = r27
                r21 = r5
                r5 = r0
                r0 = r6
                r6 = r29
                r1.addBlock(r2, r3, r4, r5, r6)
                goto L_0x0319
            L_0x02f9:
                r19 = r2
                r20 = r3
                r13 = r4
                r21 = r5
                r0 = r6
                if (r19 != 0) goto L_0x030b
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                org.telegram.tgnet.TLRPC$PageBlock r1 = r1.fixListBlock(r8, r11)
                r3 = r1
                goto L_0x030c
            L_0x030b:
                r3 = r11
            L_0x030c:
                int r5 = r9 + 1
                r1 = r24
                r2 = r25
                r4 = r27
                r6 = r29
                r1.addBlock(r2, r3, r4, r5, r6)
            L_0x0319:
                boolean r1 = r12 instanceof org.telegram.tgnet.TLRPC$TL_pageListItemBlocks
                if (r1 == 0) goto L_0x0385
                org.telegram.tgnet.TLRPC$TL_pageListItemBlocks r12 = (org.telegram.tgnet.TLRPC$TL_pageListItemBlocks) r12
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r1 = r12.blocks
                int r11 = r1.size()
                r6 = 1
            L_0x0326:
                if (r6 >= r11) goto L_0x0385
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r5 = new org.telegram.ui.ArticleViewer$TL_pageBlockListItem
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                r5.<init>()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r1 = r12.blocks
                java.lang.Object r1 = r1.get(r6)
                org.telegram.tgnet.TLRPC$PageBlock r1 = (org.telegram.tgnet.TLRPC$PageBlock) r1
                org.telegram.tgnet.TLRPC$PageBlock unused = r5.blockItem = r1
                org.telegram.ui.ArticleViewer.TL_pageBlockListParent unused = r5.parent = r13
                if (r10 == 0) goto L_0x0366
                r1 = r8
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r1 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r1
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r3 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild
                r3.<init>()
                org.telegram.tgnet.TLRPC$PageBlock r1 = r1.parent
                org.telegram.tgnet.TLRPC$PageBlock unused = r3.parent = r1
                org.telegram.tgnet.TLRPC$PageBlock unused = r3.block = r5
                int r22 = r9 + 1
                r1 = r24
                r2 = r25
                r4 = r27
                r23 = r5
                r5 = r22
                r22 = r6
                r6 = r29
                r1.addBlock(r2, r3, r4, r5, r6)
                goto L_0x0379
            L_0x0366:
                r23 = r5
                r22 = r6
                int r5 = r9 + 1
                r1 = r24
                r2 = r25
                r3 = r23
                r4 = r27
                r6 = r29
                r1.addBlock(r2, r3, r4, r5, r6)
            L_0x0379:
                java.util.ArrayList r1 = r13.items
                r2 = r23
                r1.add(r2)
                int r6 = r22 + 1
                goto L_0x0326
            L_0x0385:
                int r2 = r19 + 1
                r6 = r0
                r4 = r13
                r3 = r20
                r5 = r21
                r11 = 0
                r12 = 1
                r13 = 0
                r0 = r25
                goto L_0x023e
            L_0x0394:
                r0 = r6
                boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockOrderedList
                if (r2 == 0) goto L_0x05ae
                r11 = r1
                org.telegram.tgnet.TLRPC$TL_pageBlockOrderedList r11 = (org.telegram.tgnet.TLRPC$TL_pageBlockOrderedList) r11
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r12 = new org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                r12.<init>()
                org.telegram.tgnet.TLRPC$TL_pageBlockOrderedList unused = r12.pageBlockOrderedList = r11
                int unused = r12.level = r9
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageListOrderedItem> r1 = r11.items
                int r13 = r1.size()
                r6 = 0
            L_0x03b1:
                if (r6 >= r13) goto L_0x05ae
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageListOrderedItem> r1 = r11.items
                java.lang.Object r1 = r1.get(r6)
                org.telegram.tgnet.TLRPC$PageListOrderedItem r1 = (org.telegram.tgnet.TLRPC$PageListOrderedItem) r1
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r2 = new org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                r4 = 0
                r2.<init>()
                int unused = r2.index = r6
                org.telegram.ui.ArticleViewer.TL_pageBlockOrderedListParent unused = r2.parent = r12
                java.util.ArrayList r3 = r12.items
                r3.add(r2)
                boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageListOrderedItemText
                java.lang.String r4 = "."
                if (r3 == 0) goto L_0x0446
                r3 = r1
                org.telegram.tgnet.TLRPC$TL_pageListOrderedItemText r3 = (org.telegram.tgnet.TLRPC$TL_pageListOrderedItemText) r3
                org.telegram.tgnet.TLRPC$RichText r5 = r3.text
                org.telegram.tgnet.TLRPC$RichText unused = r2.textItem = r5
                java.lang.String r5 = r3.num
                boolean r5 = android.text.TextUtils.isEmpty(r5)
                if (r5 == 0) goto L_0x0416
                boolean r3 = r7.isRtl
                if (r3 == 0) goto L_0x0400
                r3 = 1
                java.lang.Object[] r4 = new java.lang.Object[r3]
                int r5 = r6 + 1
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r16 = 0
                r4[r16] = r5
                java.lang.String r4 = java.lang.String.format(r15, r4)
                java.lang.String unused = r2.num = r4
                goto L_0x04db
            L_0x0400:
                r3 = 1
                r16 = 0
                java.lang.Object[] r4 = new java.lang.Object[r3]
                int r3 = r6 + 1
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                r4[r16] = r3
                java.lang.String r3 = java.lang.String.format(r0, r4)
                java.lang.String unused = r2.num = r3
                goto L_0x04db
            L_0x0416:
                boolean r5 = r7.isRtl
                if (r5 == 0) goto L_0x0430
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r4)
                java.lang.String r3 = r3.num
                r5.append(r3)
                java.lang.String r3 = r5.toString()
                java.lang.String unused = r2.num = r3
                goto L_0x04db
            L_0x0430:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r3 = r3.num
                r5.append(r3)
                r5.append(r4)
                java.lang.String r3 = r5.toString()
                java.lang.String unused = r2.num = r3
                goto L_0x04db
            L_0x0446:
                boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageListOrderedItemBlocks
                if (r3 == 0) goto L_0x04db
                r3 = r1
                org.telegram.tgnet.TLRPC$TL_pageListOrderedItemBlocks r3 = (org.telegram.tgnet.TLRPC$TL_pageListOrderedItemBlocks) r3
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r5 = r3.blocks
                boolean r5 = r5.isEmpty()
                if (r5 != 0) goto L_0x0466
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r5 = r3.blocks
                r19 = r1
                r1 = 0
                java.lang.Object r5 = r5.get(r1)
                org.telegram.tgnet.TLRPC$PageBlock r5 = (org.telegram.tgnet.TLRPC$PageBlock) r5
                org.telegram.tgnet.TLRPC$PageBlock unused = r2.blockItem = r5
                r1 = r19
                goto L_0x0474
            L_0x0466:
                org.telegram.tgnet.TLRPC$TL_pageListOrderedItemText r1 = new org.telegram.tgnet.TLRPC$TL_pageListOrderedItemText
                r1.<init>()
                org.telegram.tgnet.TLRPC$TL_textPlain r5 = new org.telegram.tgnet.TLRPC$TL_textPlain
                r5.<init>()
                r5.text = r14
                r1.text = r5
            L_0x0474:
                java.lang.String r5 = r3.num
                boolean r5 = android.text.TextUtils.isEmpty(r5)
                if (r5 == 0) goto L_0x04aa
                boolean r3 = r7.isRtl
                if (r3 == 0) goto L_0x0495
                r5 = 1
                java.lang.Object[] r3 = new java.lang.Object[r5]
                int r4 = r6 + 1
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r16 = 0
                r3[r16] = r4
                java.lang.String r3 = java.lang.String.format(r15, r3)
                java.lang.String unused = r2.num = r3
                goto L_0x04d9
            L_0x0495:
                r5 = 1
                r16 = 0
                java.lang.Object[] r3 = new java.lang.Object[r5]
                int r4 = r6 + 1
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r3[r16] = r4
                java.lang.String r3 = java.lang.String.format(r0, r3)
                java.lang.String unused = r2.num = r3
                goto L_0x04d9
            L_0x04aa:
                r16 = 0
                boolean r5 = r7.isRtl
                if (r5 == 0) goto L_0x04c5
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r4)
                java.lang.String r3 = r3.num
                r5.append(r3)
                java.lang.String r3 = r5.toString()
                java.lang.String unused = r2.num = r3
                goto L_0x04d9
            L_0x04c5:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r3 = r3.num
                r5.append(r3)
                r5.append(r4)
                java.lang.String r3 = r5.toString()
                java.lang.String unused = r2.num = r3
            L_0x04d9:
                r5 = r1
                goto L_0x04e1
            L_0x04db:
                r19 = r1
                r16 = 0
                r5 = r19
            L_0x04e1:
                if (r10 == 0) goto L_0x050d
                r1 = r8
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r1 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r1
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r3 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild
                r4 = 0
                r3.<init>()
                org.telegram.tgnet.TLRPC$PageBlock r1 = r1.parent
                org.telegram.tgnet.TLRPC$PageBlock unused = r3.parent = r1
                org.telegram.tgnet.TLRPC$PageBlock unused = r3.block = r2
                int r19 = r9 + 1
                r1 = r24
                r2 = r25
                r4 = r27
                r20 = r0
                r0 = r5
                r17 = 1
                r5 = r19
                r19 = r6
                r6 = r29
                r1.addBlock(r2, r3, r4, r5, r6)
                goto L_0x052c
            L_0x050d:
                r20 = r0
                r0 = r5
                r19 = r6
                r17 = 1
                if (r19 != 0) goto L_0x051e
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                org.telegram.tgnet.TLRPC$PageBlock r1 = r1.fixListBlock(r8, r2)
                r3 = r1
                goto L_0x051f
            L_0x051e:
                r3 = r2
            L_0x051f:
                int r5 = r9 + 1
                r1 = r24
                r2 = r25
                r4 = r27
                r6 = r29
                r1.addBlock(r2, r3, r4, r5, r6)
            L_0x052c:
                boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_pageListOrderedItemBlocks
                if (r1 == 0) goto L_0x05a6
                org.telegram.tgnet.TLRPC$TL_pageListOrderedItemBlocks r0 = (org.telegram.tgnet.TLRPC$TL_pageListOrderedItemBlocks) r0
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r1 = r0.blocks
                int r6 = r1.size()
                r5 = 1
            L_0x0539:
                if (r5 >= r6) goto L_0x05a6
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r4 = new org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                r3 = 0
                r4.<init>()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r1 = r0.blocks
                java.lang.Object r1 = r1.get(r5)
                org.telegram.tgnet.TLRPC$PageBlock r1 = (org.telegram.tgnet.TLRPC$PageBlock) r1
                org.telegram.tgnet.TLRPC$PageBlock unused = r4.blockItem = r1
                org.telegram.ui.ArticleViewer.TL_pageBlockOrderedListParent unused = r4.parent = r12
                if (r10 == 0) goto L_0x0581
                r1 = r8
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r1 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r1
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r2 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild
                r2.<init>()
                org.telegram.tgnet.TLRPC$PageBlock r1 = r1.parent
                org.telegram.tgnet.TLRPC$PageBlock unused = r2.parent = r1
                org.telegram.tgnet.TLRPC$PageBlock unused = r2.block = r4
                int r18 = r9 + 1
                r1 = r24
                r21 = r2
                r2 = r25
                r22 = r3
                r3 = r21
                r21 = r4
                r4 = r27
                r23 = r5
                r5 = r18
                r18 = r6
                r6 = r29
                r1.addBlock(r2, r3, r4, r5, r6)
                goto L_0x0598
            L_0x0581:
                r22 = r3
                r21 = r4
                r23 = r5
                r18 = r6
                int r5 = r9 + 1
                r1 = r24
                r2 = r25
                r3 = r21
                r4 = r27
                r6 = r29
                r1.addBlock(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x05af }
            L_0x0598:
                java.util.ArrayList r1 = r12.items
                r2 = r21
                r1.add(r2)
                int r5 = r23 + 1
                r6 = r18
                goto L_0x0539
            L_0x05a6:
                r22 = 0
                int r6 = r19 + 1
                r0 = r20
                goto L_0x03b1
            L_0x05ae:
                return
            L_0x05af:
                r0 = move-exception
                r1 = r0
                goto L_0x05b3
            L_0x05b2:
                throw r1
            L_0x05b3:
                goto L_0x05b2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.WebpageAdapter.addBlock(org.telegram.ui.ArticleViewer$WebpageAdapter, org.telegram.tgnet.TLRPC$PageBlock, int, int, int):void");
        }

        private void addAllMediaFromBlock(WebpageAdapter webpageAdapter, TLRPC$PageBlock tLRPC$PageBlock) {
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPhoto) {
                TLRPC$TL_pageBlockPhoto tLRPC$TL_pageBlockPhoto = (TLRPC$TL_pageBlockPhoto) tLRPC$PageBlock;
                TLRPC$Photo photoWithId = getPhotoWithId(tLRPC$TL_pageBlockPhoto.photo_id);
                if (photoWithId != null) {
                    tLRPC$TL_pageBlockPhoto.thumb = FileLoader.getClosestPhotoSizeWithSize(photoWithId.sizes, 56, true);
                    tLRPC$TL_pageBlockPhoto.thumbObject = photoWithId;
                    this.photoBlocks.add(tLRPC$PageBlock);
                }
            } else if (!(tLRPC$PageBlock instanceof TLRPC$TL_pageBlockVideo) || !WebPageUtils.isVideo(webpageAdapter.currentPage, tLRPC$PageBlock)) {
                int i = 0;
                if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSlideshow) {
                    TLRPC$TL_pageBlockSlideshow tLRPC$TL_pageBlockSlideshow = (TLRPC$TL_pageBlockSlideshow) tLRPC$PageBlock;
                    int size = tLRPC$TL_pageBlockSlideshow.items.size();
                    while (i < size) {
                        TLRPC$PageBlock tLRPC$PageBlock2 = tLRPC$TL_pageBlockSlideshow.items.get(i);
                        tLRPC$PageBlock2.groupId = ArticleViewer.this.lastBlockNum;
                        addAllMediaFromBlock(webpageAdapter, tLRPC$PageBlock2);
                        i++;
                    }
                    ArticleViewer.access$13008(ArticleViewer.this);
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockCollage) {
                    TLRPC$TL_pageBlockCollage tLRPC$TL_pageBlockCollage = (TLRPC$TL_pageBlockCollage) tLRPC$PageBlock;
                    int size2 = tLRPC$TL_pageBlockCollage.items.size();
                    while (i < size2) {
                        TLRPC$PageBlock tLRPC$PageBlock3 = tLRPC$TL_pageBlockCollage.items.get(i);
                        tLRPC$PageBlock3.groupId = ArticleViewer.this.lastBlockNum;
                        addAllMediaFromBlock(webpageAdapter, tLRPC$PageBlock3);
                        i++;
                    }
                    ArticleViewer.access$13008(ArticleViewer.this);
                } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockCover) {
                    addAllMediaFromBlock(webpageAdapter, ((TLRPC$TL_pageBlockCover) tLRPC$PageBlock).cover);
                }
            } else {
                TLRPC$TL_pageBlockVideo tLRPC$TL_pageBlockVideo = (TLRPC$TL_pageBlockVideo) tLRPC$PageBlock;
                TLRPC$Document documentWithId = getDocumentWithId(tLRPC$TL_pageBlockVideo.video_id);
                if (documentWithId != null) {
                    tLRPC$TL_pageBlockVideo.thumb = FileLoader.getClosestPhotoSizeWithSize(documentWithId.thumbs, 56, true);
                    tLRPC$TL_pageBlockVideo.thumbObject = documentWithId;
                    this.photoBlocks.add(tLRPC$PageBlock);
                }
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextView textView;
            View view;
            if (i != 90) {
                switch (i) {
                    case 0:
                        textView = new BlockParagraphCell(this.context, this);
                        break;
                    case 1:
                        textView = new BlockHeaderCell(this.context, this);
                        break;
                    case 2:
                        textView = new BlockDividerCell(this.context);
                        break;
                    case 3:
                        textView = new BlockEmbedCell(this.context, this);
                        break;
                    case 4:
                        textView = new BlockSubtitleCell(this.context, this);
                        break;
                    case 5:
                        view = new BlockVideoCell(this.context, this, 0);
                        break;
                    case 6:
                        textView = new BlockPullquoteCell(this.context, this);
                        break;
                    case 7:
                        textView = new BlockBlockquoteCell(this.context, this);
                        break;
                    case 8:
                        textView = new BlockSlideshowCell(this.context, this);
                        break;
                    case 9:
                        view = new BlockPhotoCell(this.context, this, 0);
                        break;
                    case 10:
                        textView = new BlockAuthorDateCell(this.context, this);
                        break;
                    case 11:
                        textView = new BlockTitleCell(this.context, this);
                        break;
                    case 12:
                        textView = new BlockListItemCell(this.context, this);
                        break;
                    case 13:
                        textView = new BlockFooterCell(this.context, this);
                        break;
                    case 14:
                        textView = new BlockPreformattedCell(this.context, this);
                        break;
                    case 15:
                        textView = new BlockSubheaderCell(this.context, this);
                        break;
                    case 16:
                        textView = new BlockEmbedPostCell(this.context, this);
                        break;
                    case 17:
                        textView = new BlockCollageCell(this.context, this);
                        break;
                    case 18:
                        view = new BlockChannelCell(this.context, this, 0);
                        break;
                    case 19:
                        textView = new BlockAudioCell(this.context, this);
                        break;
                    case 20:
                        textView = new BlockKickerCell(this.context, this);
                        break;
                    case 21:
                        textView = new BlockOrderedListItemCell(this.context, this);
                        break;
                    case 22:
                        view = new BlockMapCell(this.context, this, 0);
                        break;
                    case 23:
                        textView = new BlockRelatedArticlesCell(this.context, this);
                        break;
                    case 24:
                        textView = new BlockDetailsCell(this.context, this);
                        break;
                    case 25:
                        textView = new BlockTableCell(this.context, this);
                        break;
                    case 26:
                        textView = new BlockRelatedArticlesHeaderCell(this.context, this);
                        break;
                    case 27:
                        textView = new BlockDetailsBottomCell(this.context);
                        break;
                    case 28:
                        textView = new BlockRelatedArticlesShadowCell(this.context);
                        break;
                    default:
                        TextView textView2 = new TextView(this.context);
                        textView2.setBackgroundColor(-65536);
                        textView2.setTextColor(-16777216);
                        textView2.setTextSize(1, 20.0f);
                        textView = textView2;
                        break;
                }
                textView = view;
            } else {
                textView = new ReportCell(this.context);
            }
            textView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            textView.setFocusable(true);
            return new RecyclerListView.Holder(textView);
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 23 || itemViewType == 24;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (i < this.localBlocks.size()) {
                bindBlockToHolder(viewHolder.getItemViewType(), viewHolder, this.localBlocks.get(i), i, this.localBlocks.size());
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 90) {
                ReportCell reportCell = (ReportCell) viewHolder.itemView;
                TLRPC$Page tLRPC$Page = this.currentPage.cached_page;
                reportCell.setViews(tLRPC$Page != null ? tLRPC$Page.views : 0);
            }
        }

        /* access modifiers changed from: private */
        public void bindBlockToHolder(int i, RecyclerView.ViewHolder viewHolder, TLRPC$PageBlock tLRPC$PageBlock, int i2, int i3) {
            TLRPC$PageBlock tLRPC$PageBlock2;
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockCover) {
                tLRPC$PageBlock2 = ((TLRPC$TL_pageBlockCover) tLRPC$PageBlock).cover;
            } else if (tLRPC$PageBlock instanceof TL_pageBlockDetailsChild) {
                tLRPC$PageBlock2 = ((TL_pageBlockDetailsChild) tLRPC$PageBlock).block;
            } else {
                tLRPC$PageBlock2 = tLRPC$PageBlock;
            }
            if (i != 100) {
                boolean z = false;
                switch (i) {
                    case 0:
                        ((BlockParagraphCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockParagraph) tLRPC$PageBlock2);
                        return;
                    case 1:
                        ((BlockHeaderCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockHeader) tLRPC$PageBlock2);
                        return;
                    case 2:
                        BlockDividerCell blockDividerCell = (BlockDividerCell) viewHolder.itemView;
                        return;
                    case 3:
                        ((BlockEmbedCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockEmbed) tLRPC$PageBlock2);
                        return;
                    case 4:
                        ((BlockSubtitleCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockSubtitle) tLRPC$PageBlock2);
                        return;
                    case 5:
                        BlockVideoCell blockVideoCell = (BlockVideoCell) viewHolder.itemView;
                        TLRPC$TL_pageBlockVideo tLRPC$TL_pageBlockVideo = (TLRPC$TL_pageBlockVideo) tLRPC$PageBlock2;
                        boolean z2 = i2 == 0;
                        if (i2 == i3 - 1) {
                            z = true;
                        }
                        blockVideoCell.setBlock(tLRPC$TL_pageBlockVideo, z2, z);
                        blockVideoCell.setParentBlock(this.channelBlock, tLRPC$PageBlock);
                        return;
                    case 6:
                        ((BlockPullquoteCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockPullquote) tLRPC$PageBlock2);
                        return;
                    case 7:
                        ((BlockBlockquoteCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockBlockquote) tLRPC$PageBlock2);
                        return;
                    case 8:
                        ((BlockSlideshowCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockSlideshow) tLRPC$PageBlock2);
                        return;
                    case 9:
                        BlockPhotoCell blockPhotoCell = (BlockPhotoCell) viewHolder.itemView;
                        TLRPC$TL_pageBlockPhoto tLRPC$TL_pageBlockPhoto = (TLRPC$TL_pageBlockPhoto) tLRPC$PageBlock2;
                        boolean z3 = i2 == 0;
                        if (i2 == i3 - 1) {
                            z = true;
                        }
                        blockPhotoCell.setBlock(tLRPC$TL_pageBlockPhoto, z3, z);
                        blockPhotoCell.setParentBlock(tLRPC$PageBlock);
                        return;
                    case 10:
                        ((BlockAuthorDateCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockAuthorDate) tLRPC$PageBlock2);
                        return;
                    case 11:
                        ((BlockTitleCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockTitle) tLRPC$PageBlock2);
                        return;
                    case 12:
                        ((BlockListItemCell) viewHolder.itemView).setBlock((TL_pageBlockListItem) tLRPC$PageBlock2);
                        return;
                    case 13:
                        ((BlockFooterCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockFooter) tLRPC$PageBlock2);
                        return;
                    case 14:
                        ((BlockPreformattedCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockPreformatted) tLRPC$PageBlock2);
                        return;
                    case 15:
                        ((BlockSubheaderCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockSubheader) tLRPC$PageBlock2);
                        return;
                    case 16:
                        ((BlockEmbedPostCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockEmbedPost) tLRPC$PageBlock2);
                        return;
                    case 17:
                        ((BlockCollageCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockCollage) tLRPC$PageBlock2);
                        return;
                    case 18:
                        ((BlockChannelCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockChannel) tLRPC$PageBlock2);
                        return;
                    case 19:
                        BlockAudioCell blockAudioCell = (BlockAudioCell) viewHolder.itemView;
                        TLRPC$TL_pageBlockAudio tLRPC$TL_pageBlockAudio = (TLRPC$TL_pageBlockAudio) tLRPC$PageBlock2;
                        boolean z4 = i2 == 0;
                        if (i2 == i3 - 1) {
                            z = true;
                        }
                        blockAudioCell.setBlock(tLRPC$TL_pageBlockAudio, z4, z);
                        return;
                    case 20:
                        ((BlockKickerCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockKicker) tLRPC$PageBlock2);
                        return;
                    case 21:
                        ((BlockOrderedListItemCell) viewHolder.itemView).setBlock((TL_pageBlockOrderedListItem) tLRPC$PageBlock2);
                        return;
                    case 22:
                        BlockMapCell blockMapCell = (BlockMapCell) viewHolder.itemView;
                        TLRPC$TL_pageBlockMap tLRPC$TL_pageBlockMap = (TLRPC$TL_pageBlockMap) tLRPC$PageBlock2;
                        boolean z5 = i2 == 0;
                        if (i2 == i3 - 1) {
                            z = true;
                        }
                        blockMapCell.setBlock(tLRPC$TL_pageBlockMap, z5, z);
                        return;
                    case 23:
                        ((BlockRelatedArticlesCell) viewHolder.itemView).setBlock((TL_pageBlockRelatedArticlesChild) tLRPC$PageBlock2);
                        return;
                    case 24:
                        ((BlockDetailsCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockDetails) tLRPC$PageBlock2);
                        return;
                    case 25:
                        ((BlockTableCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockTable) tLRPC$PageBlock2);
                        return;
                    case 26:
                        ((BlockRelatedArticlesHeaderCell) viewHolder.itemView).setBlock((TLRPC$TL_pageBlockRelatedArticles) tLRPC$PageBlock2);
                        return;
                    case 27:
                        BlockDetailsBottomCell blockDetailsBottomCell = (BlockDetailsBottomCell) viewHolder.itemView;
                        return;
                    default:
                        return;
                }
            } else {
                ((TextView) viewHolder.itemView).setText("unsupported block " + tLRPC$PageBlock2);
            }
        }

        /* access modifiers changed from: private */
        public int getTypeForBlock(TLRPC$PageBlock tLRPC$PageBlock) {
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockParagraph) {
                return 0;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockHeader) {
                return 1;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockDivider) {
                return 2;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockEmbed) {
                return 3;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSubtitle) {
                return 4;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockVideo) {
                return 5;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPullquote) {
                return 6;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockBlockquote) {
                return 7;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSlideshow) {
                return 8;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPhoto) {
                return 9;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockAuthorDate) {
                return 10;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockTitle) {
                return 11;
            }
            if (tLRPC$PageBlock instanceof TL_pageBlockListItem) {
                return 12;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockFooter) {
                return 13;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPreformatted) {
                return 14;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSubheader) {
                return 15;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockEmbedPost) {
                return 16;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockCollage) {
                return 17;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockChannel) {
                return 18;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockAudio) {
                return 19;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockKicker) {
                return 20;
            }
            if (tLRPC$PageBlock instanceof TL_pageBlockOrderedListItem) {
                return 21;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockMap) {
                return 22;
            }
            if (tLRPC$PageBlock instanceof TL_pageBlockRelatedArticlesChild) {
                return 23;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockDetails) {
                return 24;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockTable) {
                return 25;
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockRelatedArticles) {
                return 26;
            }
            if (tLRPC$PageBlock instanceof TL_pageBlockRelatedArticlesShadow) {
                return 28;
            }
            if (tLRPC$PageBlock instanceof TL_pageBlockDetailsChild) {
                return getTypeForBlock(((TL_pageBlockDetailsChild) tLRPC$PageBlock).block);
            }
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockCover) {
                return getTypeForBlock(((TLRPC$TL_pageBlockCover) tLRPC$PageBlock).cover);
            }
            return 100;
        }

        public int getItemViewType(int i) {
            if (i == this.localBlocks.size()) {
                return 90;
            }
            return getTypeForBlock(this.localBlocks.get(i));
        }

        public int getItemCount() {
            TLRPC$WebPage tLRPC$WebPage = this.currentPage;
            if (tLRPC$WebPage == null || tLRPC$WebPage.cached_page == null) {
                return 0;
            }
            return this.localBlocks.size() + 1;
        }

        private boolean isBlockOpened(TL_pageBlockDetailsChild tL_pageBlockDetailsChild) {
            TLRPC$PageBlock access$11000 = ArticleViewer.this.getLastNonListPageBlock(tL_pageBlockDetailsChild.parent);
            if (access$11000 instanceof TLRPC$TL_pageBlockDetails) {
                return ((TLRPC$TL_pageBlockDetails) access$11000).open;
            }
            if (!(access$11000 instanceof TL_pageBlockDetailsChild)) {
                return false;
            }
            TL_pageBlockDetailsChild tL_pageBlockDetailsChild2 = (TL_pageBlockDetailsChild) access$11000;
            TLRPC$PageBlock access$110002 = ArticleViewer.this.getLastNonListPageBlock(tL_pageBlockDetailsChild2.block);
            if (!(access$110002 instanceof TLRPC$TL_pageBlockDetails) || ((TLRPC$TL_pageBlockDetails) access$110002).open) {
                return isBlockOpened(tL_pageBlockDetailsChild2);
            }
            return false;
        }

        /* access modifiers changed from: private */
        public void updateRows() {
            this.localBlocks.clear();
            int size = this.blocks.size();
            for (int i = 0; i < size; i++) {
                TLRPC$PageBlock tLRPC$PageBlock = this.blocks.get(i);
                TLRPC$PageBlock access$11000 = ArticleViewer.this.getLastNonListPageBlock(tLRPC$PageBlock);
                if (!(access$11000 instanceof TL_pageBlockDetailsChild) || isBlockOpened((TL_pageBlockDetailsChild) access$11000)) {
                    this.localBlocks.add(tLRPC$PageBlock);
                }
            }
        }

        /* access modifiers changed from: private */
        public void cleanup() {
            this.currentPage = null;
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

        public void notifyItemRangeChanged(int i, int i2, Object obj) {
            updateRows();
            super.notifyItemRangeChanged(i, i2, obj);
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

    private class BlockVideoCell extends FrameLayout implements DownloadController.FileDownloadProgressListener, TextSelectionHelper.ArticleSelectableView {
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
        /* access modifiers changed from: private */
        public TLRPC$TL_pageBlockVideo currentBlock;
        private TLRPC$Document currentDocument;
        private int currentType;
        /* access modifiers changed from: private */
        public MessageObject.GroupedMessagePosition groupPosition;
        /* access modifiers changed from: private */
        public ImageReceiver imageView;
        private boolean isFirst;
        private boolean isGif;
        private WebpageAdapter parentAdapter;
        private TLRPC$PageBlock parentBlock;
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
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.imageView = imageReceiver;
            imageReceiver.setNeedsQualityThumb(true);
            this.imageView.setShouldGenerateQualityThumb(true);
            this.currentType = i;
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setProgressColor(-1);
            this.radialProgress.setColors(NUM, NUM, -1, -2500135);
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            BlockChannelCell blockChannelCell = new BlockChannelCell(context, this.parentAdapter, 1);
            this.channelCell = blockChannelCell;
            addView(blockChannelCell, LayoutHelper.createFrame(-1, -2.0f));
        }

        public void setBlock(TLRPC$TL_pageBlockVideo tLRPC$TL_pageBlockVideo, boolean z, boolean z2) {
            this.currentBlock = tLRPC$TL_pageBlockVideo;
            this.parentBlock = null;
            TLRPC$Document access$13100 = this.parentAdapter.getDocumentWithId(tLRPC$TL_pageBlockVideo.video_id);
            this.currentDocument = access$13100;
            this.isGif = MessageObject.isGifDocument(access$13100);
            this.isFirst = z;
            this.channelCell.setVisibility(4);
            updateButtonState(false);
            requestLayout();
        }

        public void setParentBlock(TLRPC$TL_pageBlockChannel tLRPC$TL_pageBlockChannel, TLRPC$PageBlock tLRPC$PageBlock) {
            this.parentBlock = tLRPC$PageBlock;
            if (tLRPC$TL_pageBlockChannel != null && (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockCover)) {
                this.channelCell.setBlock(tLRPC$TL_pageBlockChannel);
                this.channelCell.setVisibility(0);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a3, code lost:
            if (r2 <= ((float) (r0 + org.telegram.messenger.AndroidUtilities.dp(48.0f)))) goto L_0x00a9;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r13) {
            /*
                r12 = this;
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.PinchToZoomHelper r0 = r0.pinchToZoomHelper
                org.telegram.messenger.ImageReceiver r1 = r12.imageView
                r2 = 0
                boolean r0 = r0.checkPinchToZoom(r13, r12, r1, r2)
                r1 = 1
                if (r0 == 0) goto L_0x000f
                return r1
            L_0x000f:
                float r0 = r13.getX()
                float r2 = r13.getY()
                org.telegram.ui.ArticleViewer$BlockChannelCell r3 = r12.channelCell
                int r3 = r3.getVisibility()
                r4 = 0
                if (r3 != 0) goto L_0x006e
                org.telegram.ui.ArticleViewer$BlockChannelCell r3 = r12.channelCell
                float r3 = r3.getTranslationY()
                int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r3 <= 0) goto L_0x006e
                org.telegram.ui.ArticleViewer$BlockChannelCell r3 = r12.channelCell
                float r3 = r3.getTranslationY()
                r5 = 1109131264(0x421CLASSNAME, float:39.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                float r3 = r3 + r5
                int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r3 >= 0) goto L_0x006e
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r12.parentAdapter
                org.telegram.tgnet.TLRPC$TL_pageBlockChannel r0 = r0.channelBlock
                if (r0 == 0) goto L_0x006d
                int r13 = r13.getAction()
                if (r13 != r1) goto L_0x006d
                org.telegram.ui.ArticleViewer r13 = org.telegram.ui.ArticleViewer.this
                int r13 = r13.currentAccount
                org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r12.parentAdapter
                org.telegram.tgnet.TLRPC$TL_pageBlockChannel r0 = r0.channelBlock
                org.telegram.tgnet.TLRPC$Chat r0 = r0.channel
                java.lang.String r0 = r0.username
                org.telegram.ui.ArticleViewer r2 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ActionBar.BaseFragment r2 = r2.parentFragment
                r3 = 2
                r13.openByUserName(r0, r2, r3)
                org.telegram.ui.ArticleViewer r13 = org.telegram.ui.ArticleViewer.this
                r13.close(r4, r1)
            L_0x006d:
                return r1
            L_0x006e:
                int r3 = r13.getAction()
                if (r3 != 0) goto L_0x00b2
                org.telegram.messenger.ImageReceiver r3 = r12.imageView
                boolean r3 = r3.isInsideImage(r0, r2)
                if (r3 == 0) goto L_0x00b2
                int r3 = r12.buttonState
                r5 = -1
                if (r3 == r5) goto L_0x00a5
                int r3 = r12.buttonX
                float r5 = (float) r3
                int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r5 < 0) goto L_0x00a5
                r5 = 1111490560(0x42400000, float:48.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r3 = r3 + r6
                float r3 = (float) r3
                int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
                if (r0 > 0) goto L_0x00a5
                int r0 = r12.buttonY
                float r3 = (float) r0
                int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r3 < 0) goto L_0x00a5
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r0 = r0 + r3
                float r0 = (float) r0
                int r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r0 <= 0) goto L_0x00a9
            L_0x00a5:
                int r0 = r12.buttonState
                if (r0 != 0) goto L_0x00af
            L_0x00a9:
                r12.buttonPressed = r1
                r12.invalidate()
                goto L_0x00e1
            L_0x00af:
                r12.photoPressed = r1
                goto L_0x00e1
            L_0x00b2:
                int r0 = r13.getAction()
                if (r0 != r1) goto L_0x00d8
                boolean r0 = r12.photoPressed
                if (r0 == 0) goto L_0x00c8
                r12.photoPressed = r4
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.tgnet.TLRPC$TL_pageBlockVideo r2 = r12.currentBlock
                org.telegram.ui.ArticleViewer$WebpageAdapter r3 = r12.parentAdapter
                r0.openPhoto(r2, r3)
                goto L_0x00e1
            L_0x00c8:
                int r0 = r12.buttonPressed
                if (r0 != r1) goto L_0x00e1
                r12.buttonPressed = r4
                r12.playSoundEffect(r4)
                r12.didPressedButton(r1)
                r12.invalidate()
                goto L_0x00e1
            L_0x00d8:
                int r0 = r13.getAction()
                r2 = 3
                if (r0 != r2) goto L_0x00e1
                r12.photoPressed = r4
            L_0x00e1:
                boolean r0 = r12.photoPressed
                if (r0 != 0) goto L_0x0119
                int r0 = r12.buttonPressed
                if (r0 != 0) goto L_0x0119
                org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r12.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r9 = r12.captionLayout
                int r10 = r12.textX
                int r11 = r12.textY
                r7 = r13
                r8 = r12
                boolean r0 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10, r11)
                if (r0 != 0) goto L_0x0119
                org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r12.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r9 = r12.creditLayout
                int r10 = r12.textX
                int r0 = r12.textY
                int r2 = r12.creditOffset
                int r11 = r0 + r2
                r7 = r13
                r8 = r12
                boolean r0 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10, r11)
                if (r0 != 0) goto L_0x0119
                boolean r13 = super.onTouchEvent(r13)
                if (r13 == 0) goto L_0x0118
                goto L_0x0119
            L_0x0118:
                r1 = 0
            L_0x0119:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockVideoCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:64:0x0164  */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x01f2  */
        /* JADX WARNING: Removed duplicated region for block: B:9:0x0046  */
        @android.annotation.SuppressLint({"NewApi"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r31, int r32) {
            /*
                r30 = this;
                r9 = r30
                int r0 = android.view.View.MeasureSpec.getSize(r31)
                int r1 = r9.currentType
                r11 = 2
                r12 = 1
                if (r1 != r12) goto L_0x0022
                android.view.ViewParent r0 = r30.getParent()
                android.view.View r0 = (android.view.View) r0
                int r0 = r0.getMeasuredWidth()
                android.view.ViewParent r1 = r30.getParent()
                android.view.View r1 = (android.view.View) r1
                int r1 = r1.getMeasuredHeight()
            L_0x0020:
                r13 = r0
                goto L_0x0042
            L_0x0022:
                if (r1 != r11) goto L_0x0040
                org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = r9.groupPosition
                float r1 = r1.ph
                android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
                int r3 = r2.x
                int r2 = r2.y
                int r2 = java.lang.Math.max(r3, r2)
                float r2 = (float) r2
                float r1 = r1 * r2
                r2 = 1056964608(0x3var_, float:0.5)
                float r1 = r1 * r2
                double r1 = (double) r1
                double r1 = java.lang.Math.ceil(r1)
                int r1 = (int) r1
                goto L_0x0020
            L_0x0040:
                r13 = r0
                r1 = 0
            L_0x0042:
                org.telegram.tgnet.TLRPC$TL_pageBlockVideo r0 = r9.currentBlock
                if (r0 == 0) goto L_0x032c
                int r2 = r9.currentType
                r3 = 1099956224(0x41900000, float:18.0)
                if (r2 != 0) goto L_0x0067
                int r0 = r0.level
                if (r0 <= 0) goto L_0x0067
                int r0 = r0 * 14
                float r0 = (float) r0
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r0 = r0 + r2
                r9.textX = r0
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r2 = r2 + r0
                int r2 = r13 - r2
                r8 = r2
                goto L_0x0078
            L_0x0067:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r9.textX = r0
                r0 = 1108344832(0x42100000, float:36.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r0 = r13 - r0
                r8 = r0
                r2 = r13
                r0 = 0
            L_0x0078:
                org.telegram.tgnet.TLRPC$Document r3 = r9.currentDocument
                if (r3 == 0) goto L_0x024c
                r3 = 1111490560(0x42400000, float:48.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                org.telegram.tgnet.TLRPC$Document r4 = r9.currentDocument
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r4.thumbs
                r5 = 48
                org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5)
                int r5 = r9.currentType
                r6 = 1073741824(0x40000000, float:2.0)
                if (r5 != 0) goto L_0x0111
                org.telegram.tgnet.TLRPC$Document r5 = r9.currentDocument
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r5.attributes
                int r5 = r5.size()
                r7 = 0
            L_0x009b:
                if (r7 >= r5) goto L_0x00bb
                org.telegram.tgnet.TLRPC$Document r15 = r9.currentDocument
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r15 = r15.attributes
                java.lang.Object r15 = r15.get(r7)
                org.telegram.tgnet.TLRPC$DocumentAttribute r15 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r15
                boolean r10 = r15 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
                if (r10 == 0) goto L_0x00b8
                float r1 = (float) r2
                int r5 = r15.w
                float r5 = (float) r5
                float r1 = r1 / r5
                int r5 = r15.h
                float r5 = (float) r5
                float r1 = r1 * r5
                int r1 = (int) r1
                r5 = 1
                goto L_0x00bc
            L_0x00b8:
                int r7 = r7 + 1
                goto L_0x009b
            L_0x00bb:
                r5 = 0
            L_0x00bc:
                r7 = 1120403456(0x42CLASSNAME, float:100.0)
                if (r4 == 0) goto L_0x00c4
                int r10 = r4.w
                float r10 = (float) r10
                goto L_0x00c6
            L_0x00c4:
                r10 = 1120403456(0x42CLASSNAME, float:100.0)
            L_0x00c6:
                if (r4 == 0) goto L_0x00cc
                int r15 = r4.h
                float r15 = (float) r15
                goto L_0x00ce
            L_0x00cc:
                r15 = 1120403456(0x42CLASSNAME, float:100.0)
            L_0x00ce:
                if (r5 != 0) goto L_0x00d5
                float r1 = (float) r2
                float r1 = r1 / r10
                float r1 = r1 * r15
                int r1 = (int) r1
            L_0x00d5:
                org.telegram.tgnet.TLRPC$PageBlock r5 = r9.parentBlock
                boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockCover
                if (r5 == 0) goto L_0x00e0
                int r1 = java.lang.Math.min(r1, r2)
                goto L_0x0106
            L_0x00e0:
                android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                int r14 = r5.x
                int r5 = r5.y
                int r5 = java.lang.Math.max(r14, r5)
                r14 = 1113587712(0x42600000, float:56.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r5 = r5 - r14
                float r5 = (float) r5
                r14 = 1063675494(0x3var_, float:0.9)
                float r5 = r5 * r14
                int r5 = (int) r5
                if (r1 <= r5) goto L_0x0106
                float r1 = (float) r5
                float r1 = r1 / r15
                float r1 = r1 * r10
                int r1 = (int) r1
                int r2 = r13 - r0
                int r2 = r2 - r1
                int r2 = r2 / r11
                int r0 = r0 + r2
                r2 = r1
                r1 = r5
            L_0x0106:
                if (r1 != 0) goto L_0x010d
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r7)
                goto L_0x0134
            L_0x010d:
                if (r1 >= r3) goto L_0x0134
                r1 = r3
                goto L_0x0134
            L_0x0111:
                if (r5 != r11) goto L_0x0134
                org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = r9.groupPosition
                int r5 = r5.flags
                r5 = r5 & r11
                if (r5 != 0) goto L_0x011f
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r2 = r2 - r5
            L_0x011f:
                org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = r9.groupPosition
                int r5 = r5.flags
                r5 = r5 & 8
                if (r5 != 0) goto L_0x0134
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r5 = r1 - r5
                r29 = r2
                r2 = r1
                r1 = r5
                r5 = r29
                goto L_0x0136
            L_0x0134:
                r5 = r2
                r2 = r1
            L_0x0136:
                org.telegram.messenger.ImageReceiver r7 = r9.imageView
                org.telegram.tgnet.TLRPC$Document r10 = r9.currentDocument
                r7.setQualityThumbDocument(r10)
                org.telegram.messenger.ImageReceiver r7 = r9.imageView
                float r0 = (float) r0
                boolean r10 = r9.isFirst
                if (r10 != 0) goto L_0x0159
                int r10 = r9.currentType
                if (r10 == r12) goto L_0x0159
                if (r10 == r11) goto L_0x0159
                org.telegram.tgnet.TLRPC$TL_pageBlockVideo r10 = r9.currentBlock
                int r10 = r10.level
                if (r10 <= 0) goto L_0x0151
                goto L_0x0159
            L_0x0151:
                r10 = 1090519040(0x41000000, float:8.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r10 = (float) r14
                goto L_0x015a
            L_0x0159:
                r10 = 0
            L_0x015a:
                float r5 = (float) r5
                float r1 = (float) r1
                r7.setImageCoords(r0, r10, r5, r1)
                boolean r0 = r9.isGif
                r1 = 0
                if (r0 == 0) goto L_0x01f2
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                int r0 = r0.currentAccount
                org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
                r5 = 4
                org.telegram.tgnet.TLRPC$Document r7 = r9.currentDocument
                int r7 = r7.size
                boolean r0 = r0.canDownloadMedia(r5, r7)
                r9.autoDownload = r0
                org.telegram.tgnet.TLRPC$Document r0 = r9.currentDocument
                java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r12)
                boolean r5 = r9.autoDownload
                if (r5 != 0) goto L_0x01bf
                boolean r0 = r0.exists()
                if (r0 == 0) goto L_0x018a
                goto L_0x01bf
            L_0x018a:
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                org.telegram.tgnet.TLRPC$Document r1 = r9.currentDocument
                org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForDocument(r1)
                r0.setStrippedLocation(r1)
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                r18 = 0
                r19 = 0
                r20 = 0
                r21 = 0
                org.telegram.tgnet.TLRPC$Document r1 = r9.currentDocument
                org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r4, (org.telegram.tgnet.TLRPC$Document) r1)
                r24 = 0
                org.telegram.tgnet.TLRPC$Document r1 = r9.currentDocument
                int r1 = r1.size
                r26 = 0
                org.telegram.ui.ArticleViewer$WebpageAdapter r4 = r9.parentAdapter
                org.telegram.tgnet.TLRPC$WebPage r27 = r4.currentPage
                r28 = 1
                java.lang.String r23 = "80_80_b"
                r17 = r0
                r25 = r1
                r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28)
                goto L_0x0216
            L_0x01bf:
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                r0.setStrippedLocation(r1)
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                org.telegram.tgnet.TLRPC$Document r1 = r9.currentDocument
                org.telegram.messenger.ImageLocation r18 = org.telegram.messenger.ImageLocation.getForDocument(r1)
                r19 = 0
                r20 = 0
                r21 = 0
                org.telegram.tgnet.TLRPC$Document r1 = r9.currentDocument
                org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r4, (org.telegram.tgnet.TLRPC$Document) r1)
                r24 = 0
                org.telegram.tgnet.TLRPC$Document r1 = r9.currentDocument
                int r1 = r1.size
                r26 = 0
                org.telegram.ui.ArticleViewer$WebpageAdapter r4 = r9.parentAdapter
                org.telegram.tgnet.TLRPC$WebPage r27 = r4.currentPage
                r28 = 1
                java.lang.String r23 = "80_80_b"
                r17 = r0
                r25 = r1
                r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r28)
                goto L_0x0216
            L_0x01f2:
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                r0.setStrippedLocation(r1)
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                r18 = 0
                r19 = 0
                org.telegram.tgnet.TLRPC$Document r1 = r9.currentDocument
                org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r4, (org.telegram.tgnet.TLRPC$Document) r1)
                r22 = 0
                r23 = 0
                org.telegram.ui.ArticleViewer$WebpageAdapter r1 = r9.parentAdapter
                org.telegram.tgnet.TLRPC$WebPage r24 = r1.currentPage
                r25 = 1
                java.lang.String r21 = "80_80_b"
                r17 = r0
                r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25)
            L_0x0216:
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                r0.setAspectFit(r12)
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                float r0 = r0.getImageX()
                org.telegram.messenger.ImageReceiver r1 = r9.imageView
                float r1 = r1.getImageWidth()
                float r4 = (float) r3
                float r1 = r1 - r4
                float r1 = r1 / r6
                float r0 = r0 + r1
                int r0 = (int) r0
                r9.buttonX = r0
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                float r0 = r0.getImageY()
                org.telegram.messenger.ImageReceiver r1 = r9.imageView
                float r1 = r1.getImageHeight()
                float r1 = r1 - r4
                float r1 = r1 / r6
                float r0 = r0 + r1
                int r0 = (int) r0
                r9.buttonY = r0
                org.telegram.ui.Components.RadialProgress2 r1 = r9.radialProgress
                int r4 = r9.buttonX
                int r5 = r4 + r3
                int r3 = r3 + r0
                r1.setProgressRect(r4, r0, r5, r3)
                r10 = r2
                goto L_0x024d
            L_0x024c:
                r10 = r1
            L_0x024d:
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                float r0 = r0.getImageY()
                org.telegram.messenger.ImageReceiver r1 = r9.imageView
                float r1 = r1.getImageHeight()
                float r0 = r0 + r1
                r1 = 1090519040(0x41000000, float:8.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r2
                float r0 = r0 + r1
                int r5 = (int) r0
                r9.textY = r5
                int r0 = r9.currentType
                if (r0 != 0) goto L_0x02e4
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                org.telegram.tgnet.TLRPC$TL_pageBlockVideo r6 = r9.currentBlock
                org.telegram.tgnet.TLRPC$TL_pageCaption r1 = r6.caption
                org.telegram.tgnet.TLRPC$RichText r3 = r1.text
                org.telegram.ui.ArticleViewer$WebpageAdapter r7 = r9.parentAdapter
                r1 = r30
                r4 = r8
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7)
                r9.captionLayout = r0
                r14 = 1082130432(0x40800000, float:4.0)
                if (r0 == 0) goto L_0x029e
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
                org.telegram.ui.ArticleViewer$DrawingText r1 = r9.captionLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                r9.creditOffset = r0
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r0 = r0 + r1
                int r10 = r10 + r0
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.captionLayout
                int r1 = r9.textX
                r0.x = r1
                int r1 = r9.textY
                r0.y = r1
            L_0x029e:
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                org.telegram.tgnet.TLRPC$TL_pageBlockVideo r6 = r9.currentBlock
                org.telegram.tgnet.TLRPC$TL_pageCaption r1 = r6.caption
                org.telegram.tgnet.TLRPC$RichText r3 = r1.credit
                int r1 = r9.textY
                int r4 = r9.creditOffset
                int r5 = r1 + r4
                org.telegram.ui.ArticleViewer$WebpageAdapter r1 = r9.parentAdapter
                boolean r1 = r1.isRtl
                if (r1 == 0) goto L_0x02ba
                android.text.Layout$Alignment r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT()
                goto L_0x02bc
            L_0x02ba:
                android.text.Layout$Alignment r1 = android.text.Layout.Alignment.ALIGN_NORMAL
            L_0x02bc:
                r7 = r1
                org.telegram.ui.ArticleViewer$WebpageAdapter r15 = r9.parentAdapter
                r1 = r30
                r4 = r8
                r8 = r15
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8)
                r9.creditLayout = r0
                if (r0 == 0) goto L_0x02e4
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
                org.telegram.ui.ArticleViewer$DrawingText r1 = r9.creditLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                int r10 = r10 + r0
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.creditLayout
                int r1 = r9.textX
                r0.x = r1
                int r1 = r9.textY
                int r2 = r9.creditOffset
                int r1 = r1 + r2
                r0.y = r1
            L_0x02e4:
                boolean r0 = r9.isFirst
                if (r0 != 0) goto L_0x02f9
                int r0 = r9.currentType
                if (r0 != 0) goto L_0x02f9
                org.telegram.tgnet.TLRPC$TL_pageBlockVideo r0 = r9.currentBlock
                int r0 = r0.level
                if (r0 > 0) goto L_0x02f9
                r0 = 1090519040(0x41000000, float:8.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r10 = r10 + r1
            L_0x02f9:
                org.telegram.tgnet.TLRPC$PageBlock r0 = r9.parentBlock
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockCover
                if (r0 == 0) goto L_0x031c
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r9.parentAdapter
                java.util.ArrayList r0 = r0.blocks
                int r0 = r0.size()
                if (r0 <= r12) goto L_0x031c
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r9.parentAdapter
                java.util.ArrayList r0 = r0.blocks
                java.lang.Object r0 = r0.get(r12)
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockChannel
                if (r0 == 0) goto L_0x031c
                r16 = 1
                goto L_0x031e
            L_0x031c:
                r16 = 0
            L_0x031e:
                int r0 = r9.currentType
                if (r0 == r11) goto L_0x032b
                if (r16 != 0) goto L_0x032b
                r0 = 1090519040(0x41000000, float:8.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r10 = r10 + r0
            L_0x032b:
                r12 = r10
            L_0x032c:
                org.telegram.ui.ArticleViewer$BlockChannelCell r0 = r9.channelCell
                r1 = r31
                r2 = r32
                r0.measure(r1, r2)
                org.telegram.ui.ArticleViewer$BlockChannelCell r0 = r9.channelCell
                org.telegram.messenger.ImageReceiver r1 = r9.imageView
                float r1 = r1.getImageHeight()
                r2 = 1109131264(0x421CLASSNAME, float:39.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                float r1 = r1 - r2
                r0.setTranslationY(r1)
                r9.setMeasuredDimension(r13, r12)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockVideoCell.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            if (this.currentBlock != null) {
                if (!this.imageView.hasBitmapImage() || this.imageView.getCurrentAlpha() != 1.0f) {
                    canvas.drawRect(this.imageView.getDrawRegion(), ArticleViewer.photoBackgroundPaint);
                }
                if (!ArticleViewer.this.pinchToZoomHelper.isInOverlayModeFor(this)) {
                    this.imageView.draw(canvas);
                    if (this.imageView.getVisible()) {
                        this.radialProgress.draw(canvas);
                    }
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
                if (!this.isGif) {
                    this.buttonState = 3;
                } else {
                    this.buttonState = -1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), false, z);
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(attachFileName, (MessageObject) null, this);
                float f = 0.0f;
                if (FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(attachFileName)) {
                    this.buttonState = 1;
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                    if (fileProgress != null) {
                        f = fileProgress.floatValue();
                    }
                } else if (this.cancelLoading || !this.autoDownload || !this.isGif) {
                    this.buttonState = 0;
                    z2 = false;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), z2, z);
                this.radialProgress.setProgress(f, false);
            }
            invalidate();
        }

        private void didPressedButton(boolean z) {
            int i = this.buttonState;
            if (i == 0) {
                this.cancelLoading = false;
                this.radialProgress.setProgress(0.0f, false);
                if (this.isGif) {
                    this.imageView.setImage(ImageLocation.getForDocument(this.currentDocument), (String) null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(this.currentDocument.thumbs, 40), this.currentDocument), "80_80_b", this.currentDocument.size, (String) null, this.parentAdapter.currentPage, 1);
                } else {
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).loadFile(this.currentDocument, this.parentAdapter.currentPage, 1, 1);
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
                ArticleViewer.this.openPhoto(this.currentBlock, this.parentAdapter);
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageView.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        /* access modifiers changed from: protected */
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
            StringBuilder sb = new StringBuilder(LocaleController.getString("AttachVideo", NUM));
            if (this.captionLayout != null) {
                sb.append(", ");
                sb.append(this.captionLayout.getText());
            }
            accessibilityNodeInfo.setText(sb.toString());
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                arrayList.add(drawingText2);
            }
        }
    }

    private class BlockAudioCell extends View implements DownloadController.FileDownloadProgressListener, TextSelectionHelper.ArticleSelectableView {
        private int TAG;
        private int buttonPressed;
        private int buttonState;
        private int buttonX;
        private int buttonY;
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC$TL_pageBlockAudio currentBlock;
        private TLRPC$Document currentDocument;
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
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setCircleRadius(AndroidUtilities.dp(24.0f));
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            SeekBar seekBar2 = new SeekBar(this);
            this.seekBar = seekBar2;
            seekBar2.setDelegate(new SeekBar.SeekBarDelegate() {
                public /* synthetic */ void onSeekBarContinuousDrag(float f) {
                    SeekBar.SeekBarDelegate.CC.$default$onSeekBarContinuousDrag(this, f);
                }

                public final void onSeekBarDrag(float f) {
                    ArticleViewer.BlockAudioCell.this.lambda$new$0$ArticleViewer$BlockAudioCell(f);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ArticleViewer$BlockAudioCell(float f) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject != null) {
                messageObject.audioProgress = f;
                MediaController.getInstance().seekToProgress(this.currentMessageObject, f);
            }
        }

        public void setBlock(TLRPC$TL_pageBlockAudio tLRPC$TL_pageBlockAudio, boolean z, boolean z2) {
            this.currentBlock = tLRPC$TL_pageBlockAudio;
            MessageObject messageObject = (MessageObject) this.parentAdapter.audioBlocks.get(this.currentBlock);
            this.currentMessageObject = messageObject;
            if (messageObject != null) {
                this.currentDocument = messageObject.getDocument();
            }
            this.isFirst = z;
            this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioCacheSeekbar"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarSelected"));
            updateButtonState(false);
            requestLayout();
        }

        public MessageObject getMessageObject() {
            return this.currentMessageObject;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0064, code lost:
            if (r1 <= ((float) (r0 + org.telegram.messenger.AndroidUtilities.dp(48.0f)))) goto L_0x006a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0068, code lost:
            if (r12.buttonState == 0) goto L_0x006a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x006a, code lost:
            r12.buttonPressed = 1;
            invalidate();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r13) {
            /*
                r12 = this;
                float r0 = r13.getX()
                float r1 = r13.getY()
                org.telegram.ui.Components.SeekBar r2 = r12.seekBar
                int r3 = r13.getAction()
                float r4 = r13.getX()
                int r5 = r12.seekBarX
                float r5 = (float) r5
                float r4 = r4 - r5
                float r5 = r13.getY()
                int r6 = r12.seekBarY
                float r6 = (float) r6
                float r5 = r5 - r6
                boolean r2 = r2.onTouch(r3, r4, r5)
                r3 = 1
                if (r2 == 0) goto L_0x0036
                int r13 = r13.getAction()
                if (r13 != 0) goto L_0x0032
                android.view.ViewParent r13 = r12.getParent()
                r13.requestDisallowInterceptTouchEvent(r3)
            L_0x0032:
                r12.invalidate()
                return r3
            L_0x0036:
                int r2 = r13.getAction()
                r4 = 0
                if (r2 != 0) goto L_0x0070
                int r2 = r12.buttonState
                r5 = -1
                if (r2 == r5) goto L_0x0066
                int r2 = r12.buttonX
                float r5 = (float) r2
                int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r5 < 0) goto L_0x0066
                r5 = 1111490560(0x42400000, float:48.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r2 = r2 + r6
                float r2 = (float) r2
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 > 0) goto L_0x0066
                int r0 = r12.buttonY
                float r2 = (float) r0
                int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r2 < 0) goto L_0x0066
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r0 = r0 + r2
                float r0 = (float) r0
                int r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1))
                if (r0 <= 0) goto L_0x006a
            L_0x0066:
                int r0 = r12.buttonState
                if (r0 != 0) goto L_0x008f
            L_0x006a:
                r12.buttonPressed = r3
                r12.invalidate()
                goto L_0x008f
            L_0x0070:
                int r0 = r13.getAction()
                if (r0 != r3) goto L_0x0086
                int r0 = r12.buttonPressed
                if (r0 != r3) goto L_0x008f
                r12.buttonPressed = r4
                r12.playSoundEffect(r4)
                r12.didPressedButton(r3)
                r12.invalidate()
                goto L_0x008f
            L_0x0086:
                int r0 = r13.getAction()
                r1 = 3
                if (r0 != r1) goto L_0x008f
                r12.buttonPressed = r4
            L_0x008f:
                int r0 = r12.buttonPressed
                if (r0 != 0) goto L_0x00c3
                org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r12.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r9 = r12.captionLayout
                int r10 = r12.textX
                int r11 = r12.textY
                r7 = r13
                r8 = r12
                boolean r0 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10, r11)
                if (r0 != 0) goto L_0x00c3
                org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r12.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r9 = r12.creditLayout
                int r10 = r12.textX
                int r0 = r12.textY
                int r1 = r12.creditOffset
                int r11 = r0 + r1
                r7 = r13
                r8 = r12
                boolean r0 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10, r11)
                if (r0 != 0) goto L_0x00c3
                boolean r13 = super.onTouchEvent(r13)
                if (r13 == 0) goto L_0x00c2
                goto L_0x00c3
            L_0x00c2:
                r3 = 0
            L_0x00c3:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockAudioCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"DrawAllocation", "NewApi"})
        public void onMeasure(int i, int i2) {
            SpannableStringBuilder spannableStringBuilder;
            int size = View.MeasureSpec.getSize(i);
            int dp = AndroidUtilities.dp(54.0f);
            TLRPC$TL_pageBlockAudio tLRPC$TL_pageBlockAudio = this.currentBlock;
            int i3 = 1;
            if (tLRPC$TL_pageBlockAudio != null) {
                int i4 = tLRPC$TL_pageBlockAudio.level;
                if (i4 > 0) {
                    this.textX = AndroidUtilities.dp((float) (i4 * 14)) + AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = AndroidUtilities.dp(18.0f);
                }
                int dp2 = (size - this.textX) - AndroidUtilities.dp(18.0f);
                int dp3 = AndroidUtilities.dp(44.0f);
                this.buttonX = AndroidUtilities.dp(16.0f);
                int dp4 = AndroidUtilities.dp(5.0f);
                this.buttonY = dp4;
                RadialProgress2 radialProgress2 = this.radialProgress;
                int i5 = this.buttonX;
                radialProgress2.setProgressRect(i5, dp4, i5 + dp3, dp4 + dp3);
                ArticleViewer articleViewer = ArticleViewer.this;
                TLRPC$TL_pageBlockAudio tLRPC$TL_pageBlockAudio2 = this.currentBlock;
                DrawingText access$13300 = articleViewer.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockAudio2.caption.text, dp2, this.textY, tLRPC$TL_pageBlockAudio2, this.parentAdapter);
                this.captionLayout = access$13300;
                if (access$13300 != null) {
                    int dp5 = AndroidUtilities.dp(8.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp5;
                    dp += dp5 + AndroidUtilities.dp(8.0f);
                }
                int i6 = dp;
                ArticleViewer articleViewer2 = ArticleViewer.this;
                TLRPC$TL_pageBlockAudio tLRPC$TL_pageBlockAudio3 = this.currentBlock;
                DrawingText access$13400 = articleViewer2.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockAudio3.caption.credit, dp2, this.textY + this.creditOffset, tLRPC$TL_pageBlockAudio3, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = access$13400;
                if (access$13400 != null) {
                    i6 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                if (!this.isFirst && this.currentBlock.level <= 0) {
                    i6 += AndroidUtilities.dp(8.0f);
                }
                String musicAuthor = this.currentMessageObject.getMusicAuthor(false);
                String musicTitle = this.currentMessageObject.getMusicTitle(false);
                int dp6 = this.buttonX + AndroidUtilities.dp(50.0f) + dp3;
                this.seekBarX = dp6;
                int dp7 = (size - dp6) - AndroidUtilities.dp(18.0f);
                if (!TextUtils.isEmpty(musicTitle) || !TextUtils.isEmpty(musicAuthor)) {
                    if (!TextUtils.isEmpty(musicTitle) && !TextUtils.isEmpty(musicAuthor)) {
                        spannableStringBuilder = new SpannableStringBuilder(String.format("%s - %s", new Object[]{musicAuthor, musicTitle}));
                    } else if (!TextUtils.isEmpty(musicTitle)) {
                        spannableStringBuilder = new SpannableStringBuilder(musicTitle);
                    } else {
                        spannableStringBuilder = new SpannableStringBuilder(musicAuthor);
                    }
                    if (!TextUtils.isEmpty(musicAuthor)) {
                        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, musicAuthor.length(), 18);
                    }
                    CharSequence ellipsize = TextUtils.ellipsize(spannableStringBuilder, Theme.chat_audioTitlePaint, (float) dp7, TextUtils.TruncateAt.END);
                    DrawingText drawingText = new DrawingText();
                    this.titleLayout = drawingText;
                    drawingText.textLayout = new StaticLayout(ellipsize, ArticleViewer.audioTimePaint, dp7, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.titleLayout.parentBlock = this.currentBlock;
                    this.seekBarY = this.buttonY + ((dp3 - AndroidUtilities.dp(30.0f)) / 2) + AndroidUtilities.dp(11.0f);
                } else {
                    this.titleLayout = null;
                    this.seekBarY = this.buttonY + ((dp3 - AndroidUtilities.dp(30.0f)) / 2);
                }
                this.seekBar.setSize(dp7, AndroidUtilities.dp(30.0f));
                i3 = i6;
            }
            setMeasuredDimension(size, i3);
            updatePlayingMessageProgress();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            if (this.currentBlock != null) {
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
                int i2 = 0;
                if (this.titleLayout != null) {
                    canvas.save();
                    this.titleLayout.x = this.buttonX + AndroidUtilities.dp(54.0f);
                    this.titleLayout.y = this.seekBarY - AndroidUtilities.dp(16.0f);
                    DrawingText drawingText = this.titleLayout;
                    canvas.translate((float) drawingText.x, (float) drawingText.y);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.titleLayout.draw(canvas);
                    canvas.restore();
                    i = 1;
                } else {
                    i = 0;
                }
                if (this.captionLayout != null) {
                    canvas.save();
                    DrawingText drawingText2 = this.captionLayout;
                    int i3 = this.textX;
                    drawingText2.x = i3;
                    int i4 = this.textY;
                    drawingText2.y = i4;
                    canvas.translate((float) i3, (float) i4);
                    ArticleViewer.this.drawTextSelection(canvas, this, i);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                    i++;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    DrawingText drawingText3 = this.creditLayout;
                    int i5 = this.textX;
                    drawingText3.x = i5;
                    int i6 = this.textY;
                    int i7 = this.creditOffset;
                    drawingText3.y = i6 + i7;
                    canvas.translate((float) i5, (float) (i6 + i7));
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
                if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= this.currentDocument.attributes.size()) {
                            break;
                        }
                        TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.currentDocument.attributes.get(i2);
                        if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                            i = tLRPC$DocumentAttribute.duration;
                            break;
                        }
                        i2++;
                    }
                } else {
                    i = this.currentMessageObject.audioProgressSec;
                }
                String formatShortDuration = AndroidUtilities.formatShortDuration(i);
                String str = this.lastTimeString;
                if (str == null || (str != null && !str.equals(formatShortDuration))) {
                    this.lastTimeString = formatShortDuration;
                    ArticleViewer.audioTimePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
                    this.durationLayout = new StaticLayout(formatShortDuration, ArticleViewer.audioTimePaint, (int) Math.ceil((double) ArticleViewer.audioTimePaint.measureText(formatShortDuration)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(attachFileName, (MessageObject) null, this);
                if (!FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(attachFileName)) {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, z);
                    this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                } else {
                    this.buttonState = 3;
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                    if (fileProgress != null) {
                        this.radialProgress.setProgress(fileProgress.floatValue(), z);
                    } else {
                        this.radialProgress.setProgress(0.0f, z);
                    }
                    this.radialProgress.setIcon(getIconForCurrentState(), true, z);
                }
            }
            updatePlayingMessageProgress();
        }

        private void didPressedButton(boolean z) {
            int i = this.buttonState;
            if (i == 0) {
                if (MediaController.getInstance().setPlaylist(this.parentAdapter.audioMessages, this.currentMessageObject, 0, false, (MediaController.PlaylistGlobalSearchParams) null)) {
                    this.buttonState = 1;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                    invalidate();
                }
            } else if (i == 1) {
                if (MediaController.getInstance().lambda$startAudioAgain$7(this.currentMessageObject)) {
                    this.buttonState = 0;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, z);
                    invalidate();
                }
            } else if (i == 2) {
                this.radialProgress.setProgress(0.0f, false);
                FileLoader.getInstance(ArticleViewer.this.currentAccount).loadFile(this.currentDocument, this.parentAdapter.currentPage, 1, 1);
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

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        /* access modifiers changed from: protected */
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.titleLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            DrawingText drawingText2 = this.captionLayout;
            if (drawingText2 != null) {
                arrayList.add(drawingText2);
            }
            DrawingText drawingText3 = this.creditLayout;
            if (drawingText3 != null) {
                arrayList.add(drawingText3);
            }
        }
    }

    private class BlockEmbedPostCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        private ImageReceiver avatarImageView;
        private boolean avatarVisible;
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC$TL_pageBlockEmbedPost currentBlock;
        private DrawingText dateLayout;
        private int lineHeight;
        private DrawingText nameLayout;
        private WebpageAdapter parentAdapter;
        private int textX;
        private int textY;

        public BlockEmbedPostCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.avatarImageView = imageReceiver;
            imageReceiver.setRoundRadius(AndroidUtilities.dp(20.0f));
            this.avatarImageView.setImageCoords((float) AndroidUtilities.dp(32.0f), (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(40.0f), (float) AndroidUtilities.dp(40.0f));
        }

        public void setBlock(TLRPC$TL_pageBlockEmbedPost tLRPC$TL_pageBlockEmbedPost) {
            this.currentBlock = tLRPC$TL_pageBlockEmbedPost;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.captionLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(motionEvent);
            }
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            TLRPC$TL_pageBlockEmbedPost tLRPC$TL_pageBlockEmbedPost = this.currentBlock;
            int i3 = 1;
            if (tLRPC$TL_pageBlockEmbedPost != null) {
                int i4 = 0;
                if (tLRPC$TL_pageBlockEmbedPost instanceof TL_pageBlockEmbedPostCaption) {
                    this.textX = AndroidUtilities.dp(18.0f);
                    this.textY = AndroidUtilities.dp(4.0f);
                    int dp = size - AndroidUtilities.dp(50.0f);
                    ArticleViewer articleViewer = ArticleViewer.this;
                    TLRPC$TL_pageBlockEmbedPost tLRPC$TL_pageBlockEmbedPost2 = this.currentBlock;
                    DrawingText access$13300 = articleViewer.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockEmbedPost2.caption.text, dp, this.textY, tLRPC$TL_pageBlockEmbedPost2, this.parentAdapter);
                    this.captionLayout = access$13300;
                    if (access$13300 != null) {
                        int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                        this.creditOffset = dp2;
                        i4 = 0 + dp2 + AndroidUtilities.dp(4.0f);
                    }
                    ArticleViewer articleViewer2 = ArticleViewer.this;
                    TLRPC$TL_pageBlockEmbedPost tLRPC$TL_pageBlockEmbedPost3 = this.currentBlock;
                    DrawingText access$13400 = articleViewer2.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockEmbedPost3.caption.credit, dp, this.textY + this.creditOffset, tLRPC$TL_pageBlockEmbedPost3, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                    this.creditLayout = access$13400;
                    if (access$13400 != null) {
                        i4 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    }
                    i3 = i4;
                } else {
                    long j = tLRPC$TL_pageBlockEmbedPost.author_photo_id;
                    boolean z = j != 0;
                    this.avatarVisible = z;
                    if (z) {
                        TLRPC$Photo access$14200 = this.parentAdapter.getPhotoWithId(j);
                        boolean z2 = access$14200 instanceof TLRPC$TL_photo;
                        this.avatarVisible = z2;
                        if (z2) {
                            this.avatarDrawable.setInfo(0, this.currentBlock.author, (String) null);
                            this.avatarImageView.setImage(ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(access$14200.sizes, AndroidUtilities.dp(40.0f), true), access$14200), "40_40", (Drawable) this.avatarDrawable, 0, (String) null, (Object) this.parentAdapter.currentPage, 1);
                        }
                    }
                    DrawingText access$14300 = ArticleViewer.this.createLayoutForText(this, this.currentBlock.author, (TLRPC$RichText) null, size - AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 50)), 0, this.currentBlock, Layout.Alignment.ALIGN_NORMAL, 1, this.parentAdapter);
                    this.nameLayout = access$14300;
                    if (access$14300 != null) {
                        access$14300.x = AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 32));
                        this.nameLayout.y = AndroidUtilities.dp(this.dateLayout != null ? 10.0f : 19.0f);
                    }
                    if (this.currentBlock.date != 0) {
                        this.dateLayout = ArticleViewer.this.createLayoutForText(this, LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.date) * 1000), (TLRPC$RichText) null, size - AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 50)), AndroidUtilities.dp(29.0f), this.currentBlock, this.parentAdapter);
                    } else {
                        this.dateLayout = null;
                    }
                    int dp3 = AndroidUtilities.dp(56.0f);
                    if (this.currentBlock.blocks.isEmpty()) {
                        this.textX = AndroidUtilities.dp(32.0f);
                        this.textY = AndroidUtilities.dp(56.0f);
                        int dp4 = size - AndroidUtilities.dp(50.0f);
                        ArticleViewer articleViewer3 = ArticleViewer.this;
                        TLRPC$TL_pageBlockEmbedPost tLRPC$TL_pageBlockEmbedPost4 = this.currentBlock;
                        DrawingText access$133002 = articleViewer3.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockEmbedPost4.caption.text, dp4, this.textY, tLRPC$TL_pageBlockEmbedPost4, this.parentAdapter);
                        this.captionLayout = access$133002;
                        if (access$133002 != null) {
                            int dp5 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                            this.creditOffset = dp5;
                            dp3 += dp5 + AndroidUtilities.dp(4.0f);
                        }
                        int i5 = dp3;
                        ArticleViewer articleViewer4 = ArticleViewer.this;
                        TLRPC$TL_pageBlockEmbedPost tLRPC$TL_pageBlockEmbedPost5 = this.currentBlock;
                        DrawingText access$134002 = articleViewer4.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockEmbedPost5.caption.credit, dp4, this.textY + this.creditOffset, tLRPC$TL_pageBlockEmbedPost5, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                        this.creditLayout = access$134002;
                        if (access$134002 != null) {
                            i5 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                        }
                        dp3 = i5;
                    } else {
                        this.captionLayout = null;
                        this.creditLayout = null;
                    }
                    DrawingText drawingText = this.dateLayout;
                    if (drawingText != null) {
                        if (this.avatarVisible) {
                            i4 = 54;
                        }
                        drawingText.x = AndroidUtilities.dp((float) (i4 + 32));
                        this.dateLayout.y = AndroidUtilities.dp(29.0f);
                    }
                    DrawingText drawingText2 = this.captionLayout;
                    if (drawingText2 != null) {
                        drawingText2.x = this.textX;
                        drawingText2.y = this.textY;
                    }
                    DrawingText drawingText3 = this.creditLayout;
                    if (drawingText3 != null) {
                        drawingText3.x = this.textX;
                        drawingText3.y = this.textY;
                    }
                    i3 = dp3;
                }
                this.lineHeight = i3;
            }
            setMeasuredDimension(size, i3);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            TLRPC$TL_pageBlockEmbedPost tLRPC$TL_pageBlockEmbedPost = this.currentBlock;
            if (tLRPC$TL_pageBlockEmbedPost != null) {
                int i2 = 0;
                if (!(tLRPC$TL_pageBlockEmbedPost instanceof TL_pageBlockEmbedPostCaption)) {
                    if (this.avatarVisible) {
                        this.avatarImageView.draw(canvas);
                    }
                    int i3 = 54;
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
                            i3 = 0;
                        }
                        canvas.translate((float) AndroidUtilities.dp((float) (i3 + 32)), (float) AndroidUtilities.dp(29.0f));
                        ArticleViewer.this.drawTextSelection(canvas, this, i);
                        this.dateLayout.draw(canvas);
                        canvas.restore();
                        i++;
                    }
                    float dp = (float) AndroidUtilities.dp(18.0f);
                    float dp2 = (float) AndroidUtilities.dp(6.0f);
                    float dp3 = (float) AndroidUtilities.dp(20.0f);
                    int i4 = this.lineHeight;
                    if (this.currentBlock.level == 0) {
                        i2 = AndroidUtilities.dp(6.0f);
                    }
                    canvas.drawRect(dp, dp2, dp3, (float) (i4 - i2), ArticleViewer.quoteLinePaint);
                    i2 = i;
                }
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, i2);
                    this.captionLayout.draw(canvas);
                    canvas.restore();
                    i2++;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, i2);
                    this.creditLayout.draw(canvas);
                    canvas.restore();
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.nameLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            DrawingText drawingText2 = this.dateLayout;
            if (drawingText2 != null) {
                arrayList.add(drawingText2);
            }
            DrawingText drawingText3 = this.captionLayout;
            if (drawingText3 != null) {
                arrayList.add(drawingText3);
            }
            DrawingText drawingText4 = this.creditLayout;
            if (drawingText4 != null) {
                arrayList.add(drawingText4);
            }
        }
    }

    public class BlockParagraphCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC$TL_pageBlockParagraph currentBlock;
        private WebpageAdapter parentAdapter;
        public DrawingText textLayout;
        public int textX;
        public int textY;

        public BlockParagraphCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TLRPC$TL_pageBlockParagraph tLRPC$TL_pageBlockParagraph) {
            this.currentBlock = tLRPC$TL_pageBlockParagraph;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int i3;
            int size = View.MeasureSpec.getSize(i);
            TLRPC$TL_pageBlockParagraph tLRPC$TL_pageBlockParagraph = this.currentBlock;
            int i4 = 0;
            if (tLRPC$TL_pageBlockParagraph != null) {
                int i5 = tLRPC$TL_pageBlockParagraph.level;
                if (i5 == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((float) ((i5 * 14) + 18));
                }
                DrawingText access$14300 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.text, (size - AndroidUtilities.dp(18.0f)) - this.textX, this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, 0, this.parentAdapter);
                this.textLayout = access$14300;
                if (access$14300 != null) {
                    int height = access$14300.getHeight();
                    if (this.currentBlock.level > 0) {
                        i3 = AndroidUtilities.dp(8.0f);
                    } else {
                        i3 = AndroidUtilities.dp(16.0f);
                    }
                    i4 = height + i3;
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i4 = 1;
            }
            setMeasuredDimension(size, i4);
        }

        /* access modifiers changed from: protected */
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockEmbedCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        /* access modifiers changed from: private */
        public TLRPC$TL_pageBlockEmbed currentBlock;
        /* access modifiers changed from: private */
        public int exactWebViewHeight;
        private int listX;
        private WebpageAdapter parentAdapter;
        private int textX;
        private int textY;
        /* access modifiers changed from: private */
        public WebPlayerView videoView;
        /* access modifiers changed from: private */
        public boolean wasUserInteraction;
        /* access modifiers changed from: private */
        public TouchyWebView webView;

        private class TelegramWebviewProxy {
            private TelegramWebviewProxy() {
            }

            @JavascriptInterface
            public void postEvent(String str, String str2) {
                AndroidUtilities.runOnUIThread(
                /*  JADX ERROR: Method code generation error
                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0005: INVOKE  
                      (wrap: org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$IHjeoOiOuEa6rDeLfMlKacw1zGc : 0x0002: CONSTRUCTOR  (r0v0 org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$IHjeoOiOuEa6rDeLfMlKacw1zGc) = 
                      (r1v0 'this' org.telegram.ui.ArticleViewer$BlockEmbedCell$TelegramWebviewProxy A[THIS])
                      (r2v0 'str' java.lang.String)
                      (r3v0 'str2' java.lang.String)
                     call: org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$IHjeoOiOuEa6rDeLfMlKacw1zGc.<init>(org.telegram.ui.ArticleViewer$BlockEmbedCell$TelegramWebviewProxy, java.lang.String, java.lang.String):void type: CONSTRUCTOR)
                     org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.ArticleViewer.BlockEmbedCell.TelegramWebviewProxy.postEvent(java.lang.String, java.lang.String):void, dex: classes.dex
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
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
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
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
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
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
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
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0002: CONSTRUCTOR  (r0v0 org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$IHjeoOiOuEa6rDeLfMlKacw1zGc) = 
                      (r1v0 'this' org.telegram.ui.ArticleViewer$BlockEmbedCell$TelegramWebviewProxy A[THIS])
                      (r2v0 'str' java.lang.String)
                      (r3v0 'str2' java.lang.String)
                     call: org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$IHjeoOiOuEa6rDeLfMlKacw1zGc.<init>(org.telegram.ui.ArticleViewer$BlockEmbedCell$TelegramWebviewProxy, java.lang.String, java.lang.String):void type: CONSTRUCTOR in method: org.telegram.ui.ArticleViewer.BlockEmbedCell.TelegramWebviewProxy.postEvent(java.lang.String, java.lang.String):void, dex: classes.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	... 59 more
                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$IHjeoOiOuEa6rDeLfMlKacw1zGc, state: NOT_LOADED
                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 65 more
                    */
                /*
                    this = this;
                    org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$IHjeoOiOuEa6rDeLfMlKacw1zGc r0 = new org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$IHjeoOiOuEa6rDeLfMlKacw1zGc
                    r0.<init>(r1, r2, r3)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockEmbedCell.TelegramWebviewProxy.postEvent(java.lang.String, java.lang.String):void");
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$postEvent$0 */
            public /* synthetic */ void lambda$postEvent$0$ArticleViewer$BlockEmbedCell$TelegramWebviewProxy(String str, String str2) {
                if ("resize_frame".equals(str)) {
                    try {
                        int unused = BlockEmbedCell.this.exactWebViewHeight = Utilities.parseInt(new JSONObject(str2).getString("height")).intValue();
                        BlockEmbedCell.this.requestLayout();
                    } catch (Throwable unused2) {
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
                boolean unused = BlockEmbedCell.this.wasUserInteraction = true;
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
            WebPlayerView webPlayerView = new WebPlayerView(context, false, false, new WebPlayerView.WebPlayerViewDelegate(ArticleViewer.this) {
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
                    BlockEmbedCell.this.videoView.loadVideo((String) null, (TLRPC$Photo) null, (Object) null, (String) null, false);
                    HashMap hashMap = new HashMap();
                    hashMap.put("Referer", ApplicationLoader.applicationContext.getPackageName());
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
                        WebPlayerView unused = ArticleViewer.this.fullscreenedVideo = blockEmbedCell.videoView;
                        ArticleViewer.this.fullscreenVideoContainer.addView(view, LayoutHelper.createFrame(-1, -1.0f));
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(0);
                    } else {
                        ArticleViewer.this.fullscreenAspectRatioView.removeView(ArticleViewer.this.fullscreenTextureView);
                        WebPlayerView unused2 = ArticleViewer.this.fullscreenedVideo = null;
                        ArticleViewer.this.fullscreenAspectRatioView.setVisibility(8);
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(4);
                    }
                    return ArticleViewer.this.fullscreenTextureView;
                }

                public void onSharePressed() {
                    if (ArticleViewer.this.parentActivity != null) {
                        ArticleViewer.this.showDialog(new ShareAlert(ArticleViewer.this.parentActivity, (ArrayList<MessageObject>) null, BlockEmbedCell.this.currentBlock.url, false, BlockEmbedCell.this.currentBlock.url, false));
                    }
                }

                public void onPlayStateChanged(WebPlayerView webPlayerView, boolean z) {
                    if (z) {
                        if (!(ArticleViewer.this.currentPlayingVideo == null || ArticleViewer.this.currentPlayingVideo == webPlayerView)) {
                            ArticleViewer.this.currentPlayingVideo.pause();
                        }
                        WebPlayerView unused = ArticleViewer.this.currentPlayingVideo = webPlayerView;
                        try {
                            ArticleViewer.this.parentActivity.getWindow().addFlags(128);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else {
                        if (ArticleViewer.this.currentPlayingVideo == webPlayerView) {
                            WebPlayerView unused2 = ArticleViewer.this.currentPlayingVideo = null;
                        }
                        try {
                            ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    }
                }
            });
            this.videoView = webPlayerView;
            addView(webPlayerView);
            ArticleViewer.this.createdWebViews.add(this);
            TouchyWebView touchyWebView = new TouchyWebView(context);
            this.webView = touchyWebView;
            touchyWebView.getSettings().setJavaScriptEnabled(true);
            this.webView.getSettings().setDomStorageEnabled(true);
            this.webView.getSettings().setAllowContentAccess(true);
            int i = Build.VERSION.SDK_INT;
            if (i >= 17) {
                this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
                this.webView.addJavascriptInterface(new TelegramWebviewProxy(), "TelegramWebviewProxy");
            }
            if (i >= 21) {
                this.webView.getSettings().setMixedContentMode(0);
                CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
            }
            this.webView.setWebChromeClient(new WebChromeClient(ArticleViewer.this) {
                public void onShowCustomView(View view, int i, WebChromeClient.CustomViewCallback customViewCallback) {
                    onShowCustomView(view, customViewCallback);
                }

                public void onShowCustomView(View view, WebChromeClient.CustomViewCallback customViewCallback) {
                    if (ArticleViewer.this.customView != null) {
                        customViewCallback.onCustomViewHidden();
                        return;
                    }
                    View unused = ArticleViewer.this.customView = view;
                    WebChromeClient.CustomViewCallback unused2 = ArticleViewer.this.customViewCallback = customViewCallback;
                    AndroidUtilities.runOnUIThread(
                    /*  JADX ERROR: Method code generation error
                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0023: INVOKE  
                          (wrap: org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$85g4whAE3OUh7JtZTCXxDPPKGEY : 0x001e: CONSTRUCTOR  (r3v3 org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$85g4whAE3OUh7JtZTCXxDPPKGEY) = 
                          (r2v0 'this' org.telegram.ui.ArticleViewer$BlockEmbedCell$2 A[THIS])
                         call: org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$85g4whAE3OUh7JtZTCXxDPPKGEY.<init>(org.telegram.ui.ArticleViewer$BlockEmbedCell$2):void type: CONSTRUCTOR)
                          (100 long)
                         org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable, long):void type: STATIC in method: org.telegram.ui.ArticleViewer.BlockEmbedCell.2.onShowCustomView(android.view.View, android.webkit.WebChromeClient$CustomViewCallback):void, dex: classes.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
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
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
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
                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
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
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
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
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x001e: CONSTRUCTOR  (r3v3 org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$85g4whAE3OUh7JtZTCXxDPPKGEY) = 
                          (r2v0 'this' org.telegram.ui.ArticleViewer$BlockEmbedCell$2 A[THIS])
                         call: org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$85g4whAE3OUh7JtZTCXxDPPKGEY.<init>(org.telegram.ui.ArticleViewer$BlockEmbedCell$2):void type: CONSTRUCTOR in method: org.telegram.ui.ArticleViewer.BlockEmbedCell.2.onShowCustomView(android.view.View, android.webkit.WebChromeClient$CustomViewCallback):void, dex: classes.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                        	... 80 more
                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$85g4whAE3OUh7JtZTCXxDPPKGEY, state: NOT_LOADED
                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                        	... 86 more
                        */
                    /*
                        this = this;
                        org.telegram.ui.ArticleViewer$BlockEmbedCell r0 = org.telegram.ui.ArticleViewer.BlockEmbedCell.this
                        org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                        android.view.View r0 = r0.customView
                        if (r0 == 0) goto L_0x000e
                        r4.onCustomViewHidden()
                        return
                    L_0x000e:
                        org.telegram.ui.ArticleViewer$BlockEmbedCell r0 = org.telegram.ui.ArticleViewer.BlockEmbedCell.this
                        org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                        android.view.View unused = r0.customView = r3
                        org.telegram.ui.ArticleViewer$BlockEmbedCell r3 = org.telegram.ui.ArticleViewer.BlockEmbedCell.this
                        org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                        android.webkit.WebChromeClient.CustomViewCallback unused = r3.customViewCallback = r4
                        org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$85g4whAE3OUh7JtZTCXxDPPKGEY r3 = new org.telegram.ui.-$$Lambda$ArticleViewer$BlockEmbedCell$2$85g4whAE3OUh7JtZTCXxDPPKGEY
                        r3.<init>(r2)
                        r0 = 100
                        org.telegram.messenger.AndroidUtilities.runOnUIThread(r3, r0)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockEmbedCell.AnonymousClass2.onShowCustomView(android.view.View, android.webkit.WebChromeClient$CustomViewCallback):void");
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$onShowCustomView$0 */
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
                        if (ArticleViewer.this.customViewCallback != null && !ArticleViewer.this.customViewCallback.getClass().getName().contains(".chromium.")) {
                            ArticleViewer.this.customViewCallback.onCustomViewHidden();
                        }
                        View unused = ArticleViewer.this.customView = null;
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
                    Browser.openUrl((Context) ArticleViewer.this.parentActivity, str);
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
                FileLog.e((Throwable) e);
            }
            this.videoView.destroy();
        }

        public void setBlock(TLRPC$TL_pageBlockEmbed tLRPC$TL_pageBlockEmbed) {
            TLRPC$TL_pageBlockEmbed tLRPC$TL_pageBlockEmbed2 = this.currentBlock;
            this.currentBlock = tLRPC$TL_pageBlockEmbed;
            this.webView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            TLRPC$TL_pageBlockEmbed tLRPC$TL_pageBlockEmbed3 = this.currentBlock;
            if (tLRPC$TL_pageBlockEmbed2 != tLRPC$TL_pageBlockEmbed3) {
                this.wasUserInteraction = false;
                if (tLRPC$TL_pageBlockEmbed3.allow_scrolling) {
                    this.webView.setVerticalScrollBarEnabled(true);
                    this.webView.setHorizontalScrollBarEnabled(true);
                } else {
                    this.webView.setVerticalScrollBarEnabled(false);
                    this.webView.setHorizontalScrollBarEnabled(false);
                }
                this.exactWebViewHeight = 0;
                try {
                    this.webView.loadUrl("about:blank");
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                try {
                    TLRPC$TL_pageBlockEmbed tLRPC$TL_pageBlockEmbed4 = this.currentBlock;
                    String str = tLRPC$TL_pageBlockEmbed4.html;
                    if (str != null) {
                        this.webView.loadDataWithBaseURL("https://telegram.org/embed", str, "text/html", "UTF-8", (String) null);
                        this.videoView.setVisibility(4);
                        this.videoView.loadVideo((String) null, (TLRPC$Photo) null, (Object) null, (String) null, false);
                        this.webView.setVisibility(0);
                    } else {
                        long j = tLRPC$TL_pageBlockEmbed4.poster_photo_id;
                        if (this.videoView.loadVideo(tLRPC$TL_pageBlockEmbed.url, j != 0 ? this.parentAdapter.getPhotoWithId(j) : null, this.parentAdapter.currentPage, (String) null, false)) {
                            this.webView.setVisibility(4);
                            this.videoView.setVisibility(0);
                            this.webView.stopLoading();
                            this.webView.loadUrl("about:blank");
                        } else {
                            this.webView.setVisibility(0);
                            this.videoView.setVisibility(4);
                            this.videoView.loadVideo((String) null, (TLRPC$Photo) null, (Object) null, (String) null, false);
                            HashMap hashMap = new HashMap();
                            hashMap.put("Referer", ApplicationLoader.applicationContext.getPackageName());
                            this.webView.loadUrl(this.currentBlock.url, hashMap);
                        }
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            requestLayout();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (!ArticleViewer.this.isVisible) {
                this.currentBlock = null;
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.captionLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(motionEvent);
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x0144  */
        @android.annotation.SuppressLint({"NewApi"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r13, int r14) {
            /*
                r12 = this;
                int r13 = android.view.View.MeasureSpec.getSize(r13)
                org.telegram.tgnet.TLRPC$TL_pageBlockEmbed r14 = r12.currentBlock
                if (r14 == 0) goto L_0x014d
                int r14 = r14.level
                r0 = 0
                r1 = 1099956224(0x41900000, float:18.0)
                if (r14 <= 0) goto L_0x0028
                int r14 = r14 * 14
                float r14 = (float) r14
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r14 = r14 + r2
                r12.listX = r14
                r12.textX = r14
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r14 = r14 + r1
                int r14 = r13 - r14
                r9 = r14
                goto L_0x0050
            L_0x0028:
                r12.listX = r0
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r12.textX = r14
                r14 = 1108344832(0x42100000, float:36.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r2 = r13 - r2
                org.telegram.tgnet.TLRPC$TL_pageBlockEmbed r3 = r12.currentBlock
                boolean r3 = r3.full_width
                if (r3 != 0) goto L_0x004e
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r14 = r13 - r14
                int r3 = r12.listX
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r3 = r3 + r1
                r12.listX = r3
                goto L_0x004f
            L_0x004e:
                r14 = r13
            L_0x004f:
                r9 = r2
            L_0x0050:
                org.telegram.tgnet.TLRPC$TL_pageBlockEmbed r1 = r12.currentBlock
                int r2 = r1.w
                if (r2 != 0) goto L_0x0059
                r3 = 1065353216(0x3var_, float:1.0)
                goto L_0x005c
            L_0x0059:
                float r3 = (float) r13
                float r4 = (float) r2
                float r3 = r3 / r4
            L_0x005c:
                int r4 = r12.exactWebViewHeight
                if (r4 == 0) goto L_0x0066
                float r1 = (float) r4
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                goto L_0x0073
            L_0x0066:
                int r1 = r1.h
                float r1 = (float) r1
                if (r2 != 0) goto L_0x0070
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r1
            L_0x0070:
                float r1 = r1 * r3
                int r1 = (int) r1
            L_0x0073:
                r2 = 1092616192(0x41200000, float:10.0)
                if (r1 != 0) goto L_0x007b
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            L_0x007b:
                r10 = r1
                org.telegram.ui.ArticleViewer$BlockEmbedCell$TouchyWebView r1 = r12.webView
                r3 = 1073741824(0x40000000, float:2.0)
                int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r14, r3)
                int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r10, r3)
                r1.measure(r4, r5)
                org.telegram.ui.Components.WebPlayerView r1 = r12.videoView
                android.view.ViewParent r1 = r1.getParent()
                if (r1 != r12) goto L_0x00a5
                org.telegram.ui.Components.WebPlayerView r1 = r12.videoView
                int r14 = android.view.View.MeasureSpec.makeMeasureSpec(r14, r3)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = r2 + r10
                int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r3)
                r1.measure(r14, r2)
            L_0x00a5:
                r14 = 1090519040(0x41000000, float:8.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r6 = r1 + r10
                r12.textY = r6
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                r3 = 0
                org.telegram.tgnet.TLRPC$TL_pageBlockEmbed r7 = r12.currentBlock
                org.telegram.tgnet.TLRPC$TL_pageCaption r2 = r7.caption
                org.telegram.tgnet.TLRPC$RichText r4 = r2.text
                org.telegram.ui.ArticleViewer$WebpageAdapter r8 = r12.parentAdapter
                r2 = r12
                r5 = r9
                org.telegram.ui.ArticleViewer$DrawingText r1 = r1.createLayoutForText(r2, r3, r4, r5, r6, r7, r8)
                r12.captionLayout = r1
                r11 = 1082130432(0x40800000, float:4.0)
                if (r1 == 0) goto L_0x00da
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r11)
                org.telegram.ui.ArticleViewer$DrawingText r1 = r12.captionLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                r12.creditOffset = r0
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)
                int r0 = r0 + r1
                int r10 = r10 + r0
                goto L_0x00dc
            L_0x00da:
                r12.creditOffset = r0
            L_0x00dc:
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                r3 = 0
                org.telegram.tgnet.TLRPC$TL_pageBlockEmbed r7 = r12.currentBlock
                org.telegram.tgnet.TLRPC$TL_pageCaption r0 = r7.caption
                org.telegram.tgnet.TLRPC$RichText r4 = r0.credit
                int r0 = r12.textY
                int r2 = r12.creditOffset
                int r6 = r0 + r2
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r12.parentAdapter
                boolean r0 = r0.isRtl
                if (r0 == 0) goto L_0x00f8
                android.text.Layout$Alignment r0 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT()
                goto L_0x00fa
            L_0x00f8:
                android.text.Layout$Alignment r0 = android.text.Layout.Alignment.ALIGN_NORMAL
            L_0x00fa:
                r8 = r0
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r12.parentAdapter
                r2 = r12
                r5 = r9
                r9 = r0
                org.telegram.ui.ArticleViewer$DrawingText r0 = r1.createLayoutForText(r2, r3, r4, r5, r6, r7, r8, r9)
                r12.creditLayout = r0
                if (r0 == 0) goto L_0x011e
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r11)
                org.telegram.ui.ArticleViewer$DrawingText r1 = r12.creditLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                int r10 = r10 + r0
                org.telegram.ui.ArticleViewer$DrawingText r0 = r12.creditLayout
                int r1 = r12.textX
                r0.x = r1
                int r1 = r12.creditOffset
                r0.y = r1
            L_0x011e:
                r0 = 1084227584(0x40a00000, float:5.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r10 = r10 + r0
                org.telegram.tgnet.TLRPC$TL_pageBlockEmbed r0 = r12.currentBlock
                int r1 = r0.level
                if (r1 <= 0) goto L_0x0135
                boolean r0 = r0.bottom
                if (r0 != 0) goto L_0x0135
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            L_0x0133:
                int r10 = r10 + r14
                goto L_0x0140
            L_0x0135:
                if (r1 != 0) goto L_0x0140
                org.telegram.ui.ArticleViewer$DrawingText r0 = r12.captionLayout
                if (r0 == 0) goto L_0x0140
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
                goto L_0x0133
            L_0x0140:
                org.telegram.ui.ArticleViewer$DrawingText r14 = r12.captionLayout
                if (r14 == 0) goto L_0x014e
                int r0 = r12.textX
                r14.x = r0
                int r0 = r12.textY
                r14.y = r0
                goto L_0x014e
            L_0x014d:
                r10 = 1
            L_0x014e:
                r12.setMeasuredDimension(r13, r10)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockEmbedCell.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            TouchyWebView touchyWebView = this.webView;
            int i5 = this.listX;
            touchyWebView.layout(i5, 0, touchyWebView.getMeasuredWidth() + i5, this.webView.getMeasuredHeight());
            if (this.videoView.getParent() == this) {
                WebPlayerView webPlayerView = this.videoView;
                int i6 = this.listX;
                webPlayerView.layout(i6, 0, webPlayerView.getMeasuredWidth() + i6, this.videoView.getMeasuredHeight());
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            if (this.currentBlock != null) {
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                arrayList.add(drawingText2);
            }
        }
    }

    public class BlockTableCell extends FrameLayout implements TableLayout.TableLayoutDelegate, TextSelectionHelper.ArticleSelectableView {
        private TLRPC$TL_pageBlockTable currentBlock;
        private boolean firstLayout;
        private int listX;
        private int listY;
        private WebpageAdapter parentAdapter;
        private HorizontalScrollView scrollView;
        /* access modifiers changed from: private */
        public TableLayout tableLayout;
        private int textX;
        private int textY;
        private DrawingText titleLayout;

        public BlockTableCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            AnonymousClass1 r6 = new HorizontalScrollView(context, ArticleViewer.this) {
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    boolean onInterceptTouchEvent = super.onInterceptTouchEvent(motionEvent);
                    if (BlockTableCell.this.tableLayout.getMeasuredWidth() > getMeasuredWidth() - AndroidUtilities.dp(36.0f) && onInterceptTouchEvent) {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                    return onInterceptTouchEvent;
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (BlockTableCell.this.tableLayout.getMeasuredWidth() <= getMeasuredWidth() - AndroidUtilities.dp(36.0f)) {
                        return false;
                    }
                    return super.onTouchEvent(motionEvent);
                }

                /* access modifiers changed from: protected */
                public void onScrollChanged(int i, int i2, int i3, int i4) {
                    super.onScrollChanged(i, i2, i3, i4);
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null) {
                        DrawingText unused = ArticleViewer.this.pressedLinkOwnerLayout = null;
                        View unused2 = ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                    BlockTableCell.this.updateChildTextPositions();
                    TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = ArticleViewer.this.textSelectionHelper;
                    if (articleTextSelectionHelper != null && articleTextSelectionHelper.isSelectionMode()) {
                        ArticleViewer.this.textSelectionHelper.invalidate();
                    }
                }

                /* access modifiers changed from: protected */
                public boolean overScrollBy(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
                    ArticleViewer.this.removePressedLink();
                    return super.overScrollBy(i, i2, i3, i4, i5, i6, i7, i8, z);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    BlockTableCell.this.tableLayout.measure(View.MeasureSpec.makeMeasureSpec((View.MeasureSpec.getSize(i) - getPaddingLeft()) - getPaddingRight(), 0), i2);
                    setMeasuredDimension(View.MeasureSpec.getSize(i), BlockTableCell.this.tableLayout.getMeasuredHeight());
                }
            };
            this.scrollView = r6;
            r6.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.scrollView.setClipToPadding(false);
            addView(this.scrollView, LayoutHelper.createFrame(-1, -2.0f));
            TableLayout tableLayout2 = new TableLayout(context, this, ArticleViewer.this.textSelectionHelper);
            this.tableLayout = tableLayout2;
            tableLayout2.setOrientation(0);
            this.tableLayout.setRowOrderPreserved(true);
            this.scrollView.addView(this.tableLayout, new FrameLayout.LayoutParams(-2, -2));
            setWillNotDraw(false);
        }

        public DrawingText createTextLayout(TLRPC$TL_pageTableCell tLRPC$TL_pageTableCell, int i) {
            Layout.Alignment alignment;
            if (tLRPC$TL_pageTableCell == null) {
                return null;
            }
            if (tLRPC$TL_pageTableCell.align_right) {
                alignment = Layout.Alignment.ALIGN_OPPOSITE;
            } else if (tLRPC$TL_pageTableCell.align_center) {
                alignment = Layout.Alignment.ALIGN_CENTER;
            } else {
                alignment = Layout.Alignment.ALIGN_NORMAL;
            }
            return ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageTableCell.text, i, -1, this.currentBlock, alignment, 0, this.parentAdapter);
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
                String lowerCase = drawingText.textLayout.getText().toString().toLowerCase();
                int i3 = 0;
                while (true) {
                    int indexOf = lowerCase.indexOf(ArticleViewer.this.searchText, i3);
                    if (indexOf >= 0) {
                        int length = ArticleViewer.this.searchText.length() + indexOf;
                        if (indexOf == 0 || AndroidUtilities.isPunctuationCharacter(lowerCase.charAt(indexOf - 1))) {
                            StaticLayout staticLayout = drawingText.textLayout;
                            ArticleViewer.this.adapter[0].searchTextOffset.put(ArticleViewer.this.searchText + this.currentBlock + drawingText.parentText + indexOf, Integer.valueOf(staticLayout.getLineTop(staticLayout.getLineForOffset(indexOf)) + i2));
                        }
                        i3 = length;
                    } else {
                        return;
                    }
                }
            }
        }

        public void setBlock(TLRPC$TL_pageBlockTable tLRPC$TL_pageBlockTable) {
            int i;
            this.currentBlock = tLRPC$TL_pageBlockTable;
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("windowBackgroundWhite"));
            this.tableLayout.removeAllChildrens();
            this.tableLayout.setDrawLines(this.currentBlock.bordered);
            this.tableLayout.setStriped(this.currentBlock.striped);
            this.tableLayout.setRtl(this.parentAdapter.isRtl);
            if (!this.currentBlock.rows.isEmpty()) {
                TLRPC$TL_pageTableRow tLRPC$TL_pageTableRow = this.currentBlock.rows.get(0);
                int size = tLRPC$TL_pageTableRow.cells.size();
                i = 0;
                for (int i2 = 0; i2 < size; i2++) {
                    int i3 = tLRPC$TL_pageTableRow.cells.get(i2).colspan;
                    if (i3 == 0) {
                        i3 = 1;
                    }
                    i += i3;
                }
            } else {
                i = 0;
            }
            int size2 = this.currentBlock.rows.size();
            for (int i4 = 0; i4 < size2; i4++) {
                TLRPC$TL_pageTableRow tLRPC$TL_pageTableRow2 = this.currentBlock.rows.get(i4);
                int size3 = tLRPC$TL_pageTableRow2.cells.size();
                int i5 = 0;
                for (int i6 = 0; i6 < size3; i6++) {
                    TLRPC$TL_pageTableCell tLRPC$TL_pageTableCell = tLRPC$TL_pageTableRow2.cells.get(i6);
                    int i7 = tLRPC$TL_pageTableCell.colspan;
                    if (i7 == 0) {
                        i7 = 1;
                    }
                    int i8 = tLRPC$TL_pageTableCell.rowspan;
                    if (i8 == 0) {
                        i8 = 1;
                    }
                    if (tLRPC$TL_pageTableCell.text != null) {
                        this.tableLayout.addChild(tLRPC$TL_pageTableCell, i5, i4, i7);
                    } else {
                        this.tableLayout.addChild(i5, i4, i7, i8);
                    }
                    i5 += i7;
                }
            }
            this.tableLayout.setColumnCount(i);
            this.firstLayout = true;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            int childCount = this.tableLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                TableLayout.Child childAt = this.tableLayout.getChildAt(i);
                if (ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, childAt.textLayout, (this.scrollView.getPaddingLeft() - this.scrollView.getScrollX()) + this.listX + childAt.getTextX(), this.listY + childAt.getTextY())) {
                    return true;
                }
            }
            if (ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.titleLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent)) {
                return true;
            }
            return false;
        }

        public void invalidate() {
            super.invalidate();
            this.tableLayout.invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int i3;
            int i4;
            int i5;
            int size = View.MeasureSpec.getSize(i);
            TLRPC$TL_pageBlockTable tLRPC$TL_pageBlockTable = this.currentBlock;
            if (tLRPC$TL_pageBlockTable != null) {
                int i6 = tLRPC$TL_pageBlockTable.level;
                if (i6 > 0) {
                    int dp = AndroidUtilities.dp((float) (i6 * 14));
                    this.listX = dp;
                    i4 = dp + AndroidUtilities.dp(18.0f);
                    this.textX = i4;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    i4 = AndroidUtilities.dp(36.0f);
                }
                ArticleViewer articleViewer = ArticleViewer.this;
                TLRPC$TL_pageBlockTable tLRPC$TL_pageBlockTable2 = this.currentBlock;
                DrawingText access$14300 = articleViewer.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockTable2.title, size - i4, 0, tLRPC$TL_pageBlockTable2, Layout.Alignment.ALIGN_CENTER, 0, this.parentAdapter);
                this.titleLayout = access$14300;
                if (access$14300 != null) {
                    this.textY = 0;
                    i5 = access$14300.getHeight() + AndroidUtilities.dp(8.0f) + 0;
                    this.listY = i5;
                    DrawingText drawingText = this.titleLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                } else {
                    this.listY = AndroidUtilities.dp(8.0f);
                    i5 = 0;
                }
                this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(size - this.listX, NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                i3 = i5 + this.scrollView.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                TLRPC$TL_pageBlockTable tLRPC$TL_pageBlockTable3 = this.currentBlock;
                if (tLRPC$TL_pageBlockTable3.level > 0 && !tLRPC$TL_pageBlockTable3.bottom) {
                    i3 += AndroidUtilities.dp(8.0f);
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(size, i3);
            updateChildTextPositions();
        }

        /* access modifiers changed from: private */
        public void updateChildTextPositions() {
            int i = this.titleLayout == null ? 0 : 1;
            int childCount = this.tableLayout.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                TableLayout.Child childAt = this.tableLayout.getChildAt(i2);
                DrawingText drawingText = childAt.textLayout;
                if (drawingText != null) {
                    drawingText.x = ((childAt.getTextX() + this.listX) + AndroidUtilities.dp(18.0f)) - this.scrollView.getScrollX();
                    childAt.textLayout.y = childAt.getTextY() + this.listY;
                    childAt.textLayout.row = childAt.getRow();
                    childAt.setSelectionIndex(i);
                    i++;
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            HorizontalScrollView horizontalScrollView = this.scrollView;
            int i5 = this.listX;
            horizontalScrollView.layout(i5, this.listY, horizontalScrollView.getMeasuredWidth() + i5, this.listY + this.scrollView.getMeasuredHeight());
            if (this.firstLayout) {
                if (this.parentAdapter.isRtl) {
                    this.scrollView.setScrollX((this.tableLayout.getMeasuredWidth() - this.scrollView.getMeasuredWidth()) + AndroidUtilities.dp(36.0f));
                } else {
                    this.scrollView.setScrollX(0);
                }
                this.firstLayout = false;
            }
        }

        /* access modifiers changed from: protected */
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
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

    private class BlockCollageCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        /* access modifiers changed from: private */
        public TLRPC$TL_pageBlockCollage currentBlock;
        /* access modifiers changed from: private */
        public GroupedMessages group = new GroupedMessages();
        /* access modifiers changed from: private */
        public boolean inLayout;
        private RecyclerView.Adapter innerAdapter;
        /* access modifiers changed from: private */
        public RecyclerListView innerListView;
        private int listX;
        /* access modifiers changed from: private */
        public WebpageAdapter parentAdapter;
        private int textX;
        private int textY;

        public class GroupedMessages {
            private int maxSizeWidth = 1000;
            public ArrayList<MessageObject.GroupedMessagePosition> posArray = new ArrayList<>();
            public HashMap<TLObject, MessageObject.GroupedMessagePosition> positions = new HashMap<>();

            public GroupedMessages() {
            }

            private class MessageGroupedLayoutAttempt {
                public float[] heights;
                public int[] lineCounts;

                public MessageGroupedLayoutAttempt(GroupedMessages groupedMessages, int i, int i2, float f, float f2) {
                    this.lineCounts = new int[]{i, i2};
                    this.heights = new float[]{f, f2};
                }

                public MessageGroupedLayoutAttempt(GroupedMessages groupedMessages, int i, int i2, int i3, float f, float f2, float f3) {
                    this.lineCounts = new int[]{i, i2, i3};
                    this.heights = new float[]{f, f2, f3};
                }

                public MessageGroupedLayoutAttempt(GroupedMessages groupedMessages, int i, int i2, int i3, int i4, float f, float f2, float f3, float f4) {
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
                int i;
                int i2;
                float f;
                int i3;
                int i4;
                int i5;
                int i6;
                int i7;
                int i8;
                int i9;
                int i10;
                TLRPC$PhotoSize tLRPC$PhotoSize;
                float f2;
                TLRPC$Document access$13100;
                this.posArray.clear();
                this.positions.clear();
                int size = BlockCollageCell.this.currentBlock.items.size();
                if (size > 1) {
                    StringBuilder sb = new StringBuilder();
                    int i11 = 0;
                    float f3 = 1.0f;
                    boolean z = false;
                    while (i11 < size) {
                        TLObject tLObject = BlockCollageCell.this.currentBlock.items.get(i11);
                        if (tLObject instanceof TLRPC$TL_pageBlockPhoto) {
                            TLRPC$Photo access$14200 = BlockCollageCell.this.parentAdapter.getPhotoWithId(((TLRPC$TL_pageBlockPhoto) tLObject).photo_id);
                            if (access$14200 == null) {
                                i11++;
                            } else {
                                tLRPC$PhotoSize = FileLoader.getClosestPhotoSizeWithSize(access$14200.sizes, AndroidUtilities.getPhotoSize());
                            }
                        } else {
                            if ((tLObject instanceof TLRPC$TL_pageBlockVideo) && (access$13100 = BlockCollageCell.this.parentAdapter.getDocumentWithId(((TLRPC$TL_pageBlockVideo) tLObject).video_id)) != null) {
                                tLRPC$PhotoSize = FileLoader.getClosestPhotoSizeWithSize(access$13100.thumbs, 90);
                            }
                            i11++;
                        }
                        MessageObject.GroupedMessagePosition groupedMessagePosition = new MessageObject.GroupedMessagePosition();
                        groupedMessagePosition.last = i11 == size + -1;
                        if (tLRPC$PhotoSize == null) {
                            f2 = 1.0f;
                        } else {
                            f2 = ((float) tLRPC$PhotoSize.w) / ((float) tLRPC$PhotoSize.h);
                        }
                        groupedMessagePosition.aspectRatio = f2;
                        if (f2 > 1.2f) {
                            sb.append("w");
                        } else if (f2 < 0.8f) {
                            sb.append("n");
                        } else {
                            sb.append("q");
                        }
                        float f4 = groupedMessagePosition.aspectRatio;
                        f3 += f4;
                        if (f4 > 2.0f) {
                            z = true;
                        }
                        this.positions.put(tLObject, groupedMessagePosition);
                        this.posArray.add(groupedMessagePosition);
                        i11++;
                    }
                    int dp = AndroidUtilities.dp(120.0f);
                    Point point = AndroidUtilities.displaySize;
                    int dp2 = (int) (((float) AndroidUtilities.dp(120.0f)) / (((float) Math.min(point.x, point.y)) / ((float) this.maxSizeWidth)));
                    Point point2 = AndroidUtilities.displaySize;
                    int i12 = this.maxSizeWidth;
                    int dp3 = (int) (((float) AndroidUtilities.dp(40.0f)) / (((float) Math.min(point2.x, point2.y)) / ((float) i12)));
                    float f5 = ((float) i12) / 814.0f;
                    float f6 = f3 / ((float) size);
                    int i13 = 3;
                    if (z || !(size == 2 || size == 3 || size == 4)) {
                        int size2 = this.posArray.size();
                        float[] fArr = new float[size2];
                        for (int i14 = 0; i14 < size; i14++) {
                            if (f6 > 1.1f) {
                                fArr[i14] = Math.max(1.0f, this.posArray.get(i14).aspectRatio);
                            } else {
                                fArr[i14] = Math.min(1.0f, this.posArray.get(i14).aspectRatio);
                            }
                            fArr[i14] = Math.max(0.66667f, Math.min(1.7f, fArr[i14]));
                        }
                        ArrayList arrayList = new ArrayList();
                        for (int i15 = 1; i15 < size2; i15++) {
                            int i16 = size2 - i15;
                            if (i15 <= 3 && i16 <= 3) {
                                MessageGroupedLayoutAttempt messageGroupedLayoutAttempt = r0;
                                MessageGroupedLayoutAttempt messageGroupedLayoutAttempt2 = new MessageGroupedLayoutAttempt(this, i15, i16, multiHeight(fArr, 0, i15), multiHeight(fArr, i15, size2));
                                arrayList.add(messageGroupedLayoutAttempt);
                            }
                        }
                        int i17 = 1;
                        while (i17 < size2 - 1) {
                            int i18 = 1;
                            while (true) {
                                int i19 = size2 - i17;
                                if (i18 >= i19) {
                                    break;
                                }
                                int i20 = i19 - i18;
                                if (i17 <= i13) {
                                    if (i18 <= (f6 < 0.85f ? 4 : 3) && i20 <= i13) {
                                        int i21 = i17 + i18;
                                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt3 = r0;
                                        i10 = i18;
                                        i9 = i17;
                                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt4 = new MessageGroupedLayoutAttempt(this, i17, i18, i20, multiHeight(fArr, 0, i17), multiHeight(fArr, i17, i21), multiHeight(fArr, i21, size2));
                                        arrayList.add(messageGroupedLayoutAttempt3);
                                        i18 = i10 + 1;
                                        i17 = i9;
                                        i13 = 3;
                                    }
                                }
                                i10 = i18;
                                i9 = i17;
                                i18 = i10 + 1;
                                i17 = i9;
                                i13 = 3;
                            }
                            i17++;
                            i13 = 3;
                        }
                        int i22 = 1;
                        while (i22 < size2 - 2) {
                            int i23 = 1;
                            while (true) {
                                int i24 = size2 - i22;
                                if (i23 >= i24) {
                                    break;
                                }
                                int i25 = 1;
                                while (true) {
                                    int i26 = i24 - i23;
                                    if (i25 >= i26) {
                                        break;
                                    }
                                    int i27 = i26 - i25;
                                    if (i22 > 3 || i23 > 3 || i25 > 3 || i27 > 3) {
                                        i3 = i25;
                                        i7 = i24;
                                        i6 = i23;
                                        i5 = size;
                                        i4 = size2;
                                        i8 = dp2;
                                    } else {
                                        float multiHeight = multiHeight(fArr, 0, i22);
                                        int i28 = i22 + i23;
                                        float multiHeight2 = multiHeight(fArr, i22, i28);
                                        int i29 = i28 + i25;
                                        float multiHeight3 = multiHeight(fArr, i28, i29);
                                        float multiHeight4 = multiHeight(fArr, i29, size2);
                                        int i30 = i23;
                                        i4 = size2;
                                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt5 = r0;
                                        int i31 = i25;
                                        i3 = i25;
                                        float f7 = multiHeight;
                                        i7 = i24;
                                        float f8 = multiHeight2;
                                        i6 = i23;
                                        float f9 = multiHeight3;
                                        i5 = size;
                                        i8 = dp2;
                                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt6 = new MessageGroupedLayoutAttempt(this, i22, i30, i31, i27, f7, f8, f9, multiHeight4);
                                        arrayList.add(messageGroupedLayoutAttempt5);
                                    }
                                    i25 = i3 + 1;
                                    dp2 = i8;
                                    i24 = i7;
                                    i23 = i6;
                                    size = i5;
                                    size2 = i4;
                                }
                                int i32 = size;
                                int i33 = size2;
                                int i34 = dp2;
                                i23++;
                                size = i32;
                            }
                            int i35 = size;
                            int i36 = size2;
                            int i37 = dp2;
                            i22++;
                            size = i35;
                        }
                        i = size;
                        int i38 = dp2;
                        float var_ = (float) ((this.maxSizeWidth / 3) * 4);
                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt7 = null;
                        float var_ = 0.0f;
                        for (int i39 = 0; i39 < arrayList.size(); i39++) {
                            MessageGroupedLayoutAttempt messageGroupedLayoutAttempt8 = (MessageGroupedLayoutAttempt) arrayList.get(i39);
                            int i40 = 0;
                            float var_ = Float.MAX_VALUE;
                            float var_ = 0.0f;
                            while (true) {
                                float[] fArr2 = messageGroupedLayoutAttempt8.heights;
                                if (i40 >= fArr2.length) {
                                    break;
                                }
                                var_ += fArr2[i40];
                                if (fArr2[i40] < var_) {
                                    var_ = fArr2[i40];
                                }
                                i40++;
                            }
                            float abs = Math.abs(var_ - var_);
                            int[] iArr = messageGroupedLayoutAttempt8.lineCounts;
                            if (iArr.length > 1) {
                                if (iArr[0] <= iArr[1]) {
                                    if (iArr.length > 2 && iArr[1] > iArr[2]) {
                                        f = 1.2f;
                                        abs *= f;
                                    } else if (iArr.length <= 3 || iArr[2] <= iArr[3]) {
                                    }
                                }
                                f = 1.2f;
                                abs *= f;
                            }
                            if (var_ < ((float) i38)) {
                                abs *= 1.5f;
                            }
                            if (messageGroupedLayoutAttempt7 == null || abs < var_) {
                                messageGroupedLayoutAttempt7 = messageGroupedLayoutAttempt8;
                                var_ = abs;
                            }
                        }
                        if (messageGroupedLayoutAttempt7 != null) {
                            int i41 = 0;
                            int i42 = 0;
                            while (true) {
                                int[] iArr2 = messageGroupedLayoutAttempt7.lineCounts;
                                if (i42 >= iArr2.length) {
                                    break;
                                }
                                int i43 = iArr2[i42];
                                float var_ = messageGroupedLayoutAttempt7.heights[i42];
                                int i44 = this.maxSizeWidth;
                                MessageObject.GroupedMessagePosition groupedMessagePosition2 = null;
                                for (int i45 = 0; i45 < i43; i45++) {
                                    int i46 = (int) (fArr[i41] * var_);
                                    i44 -= i46;
                                    MessageObject.GroupedMessagePosition groupedMessagePosition3 = this.posArray.get(i41);
                                    int i47 = i42 == 0 ? 4 : 0;
                                    if (i42 == messageGroupedLayoutAttempt7.lineCounts.length - 1) {
                                        i47 |= 8;
                                    }
                                    if (i45 == 0) {
                                        i47 |= 1;
                                    }
                                    if (i45 == i43 - 1) {
                                        i2 = i47 | 2;
                                        groupedMessagePosition2 = groupedMessagePosition3;
                                    } else {
                                        i2 = i47;
                                    }
                                    groupedMessagePosition3.set(i45, i45, i42, i42, i46, var_ / 814.0f, i2);
                                    i41++;
                                }
                                groupedMessagePosition2.pw += i44;
                                groupedMessagePosition2.spanSize += i44;
                                i42++;
                            }
                        } else {
                            return;
                        }
                    } else {
                        if (size == 2) {
                            MessageObject.GroupedMessagePosition groupedMessagePosition4 = this.posArray.get(0);
                            MessageObject.GroupedMessagePosition groupedMessagePosition5 = this.posArray.get(1);
                            String sb2 = sb.toString();
                            if (sb2.equals("ww")) {
                                double d = (double) f5;
                                Double.isNaN(d);
                                if (((double) f6) > d * 1.4d) {
                                    float var_ = groupedMessagePosition4.aspectRatio;
                                    float var_ = groupedMessagePosition5.aspectRatio;
                                    if (((double) (var_ - var_)) < 0.2d) {
                                        int i48 = this.maxSizeWidth;
                                        float round = ((float) Math.round(Math.min(((float) i48) / var_, Math.min(((float) i48) / var_, 407.0f)))) / 814.0f;
                                        groupedMessagePosition4.set(0, 0, 0, 0, this.maxSizeWidth, round, 7);
                                        groupedMessagePosition5.set(0, 0, 1, 1, this.maxSizeWidth, round, 11);
                                    }
                                }
                            }
                            if (sb2.equals("ww") || sb2.equals("qq")) {
                                int i49 = this.maxSizeWidth / 2;
                                float var_ = (float) i49;
                                float round2 = ((float) Math.round(Math.min(var_ / groupedMessagePosition4.aspectRatio, Math.min(var_ / groupedMessagePosition5.aspectRatio, 814.0f)))) / 814.0f;
                                groupedMessagePosition4.set(0, 0, 0, 0, i49, round2, 13);
                                groupedMessagePosition5.set(1, 1, 0, 0, i49, round2, 14);
                            } else {
                                int i50 = this.maxSizeWidth;
                                float var_ = groupedMessagePosition4.aspectRatio;
                                int max = (int) Math.max(((float) i50) * 0.4f, (float) Math.round((((float) i50) / var_) / ((1.0f / var_) + (1.0f / groupedMessagePosition5.aspectRatio))));
                                int i51 = this.maxSizeWidth - max;
                                if (i51 < dp2) {
                                    max -= dp2 - i51;
                                    i51 = dp2;
                                }
                                float min = Math.min(814.0f, (float) Math.round(Math.min(((float) i51) / groupedMessagePosition4.aspectRatio, ((float) max) / groupedMessagePosition5.aspectRatio))) / 814.0f;
                                groupedMessagePosition4.set(0, 0, 0, 0, i51, min, 13);
                                groupedMessagePosition5.set(1, 1, 0, 0, max, min, 14);
                            }
                        } else if (size == 3) {
                            MessageObject.GroupedMessagePosition groupedMessagePosition6 = this.posArray.get(0);
                            MessageObject.GroupedMessagePosition groupedMessagePosition7 = this.posArray.get(1);
                            MessageObject.GroupedMessagePosition groupedMessagePosition8 = this.posArray.get(2);
                            if (sb.charAt(0) == 'n') {
                                float var_ = groupedMessagePosition7.aspectRatio;
                                float min2 = Math.min(407.0f, (float) Math.round((((float) this.maxSizeWidth) * var_) / (groupedMessagePosition8.aspectRatio + var_)));
                                float var_ = 814.0f - min2;
                                int max2 = (int) Math.max((float) dp2, Math.min(((float) this.maxSizeWidth) * 0.5f, (float) Math.round(Math.min(groupedMessagePosition8.aspectRatio * min2, groupedMessagePosition7.aspectRatio * var_))));
                                int round3 = Math.round(Math.min((groupedMessagePosition6.aspectRatio * 814.0f) + ((float) dp3), (float) (this.maxSizeWidth - max2)));
                                groupedMessagePosition6.set(0, 0, 0, 1, round3, 1.0f, 13);
                                float var_ = var_ / 814.0f;
                                int i52 = max2;
                                groupedMessagePosition7.set(1, 1, 0, 0, i52, var_, 6);
                                float var_ = min2 / 814.0f;
                                groupedMessagePosition8.set(0, 1, 1, 1, i52, var_, 10);
                                int i53 = this.maxSizeWidth;
                                groupedMessagePosition8.spanSize = i53;
                                groupedMessagePosition6.siblingHeights = new float[]{var_, var_};
                                groupedMessagePosition7.spanSize = i53 - round3;
                                groupedMessagePosition8.leftSpanOffset = round3;
                            } else {
                                float round4 = ((float) Math.round(Math.min(((float) this.maxSizeWidth) / groupedMessagePosition6.aspectRatio, 537.24005f))) / 814.0f;
                                groupedMessagePosition6.set(0, 1, 0, 0, this.maxSizeWidth, round4, 7);
                                int i54 = this.maxSizeWidth / 2;
                                float var_ = 814.0f - round4;
                                float var_ = (float) i54;
                                int i55 = i54;
                                float min3 = Math.min(var_, (float) Math.round(Math.min(var_ / groupedMessagePosition7.aspectRatio, var_ / groupedMessagePosition8.aspectRatio))) / 814.0f;
                                groupedMessagePosition7.set(0, 0, 1, 1, i55, min3, 9);
                                groupedMessagePosition8.set(1, 1, 1, 1, i55, min3, 10);
                            }
                        } else if (size == 4) {
                            MessageObject.GroupedMessagePosition groupedMessagePosition9 = this.posArray.get(0);
                            MessageObject.GroupedMessagePosition groupedMessagePosition10 = this.posArray.get(1);
                            MessageObject.GroupedMessagePosition groupedMessagePosition11 = this.posArray.get(2);
                            MessageObject.GroupedMessagePosition groupedMessagePosition12 = this.posArray.get(3);
                            if (sb.charAt(0) == 'w') {
                                float round5 = ((float) Math.round(Math.min(((float) this.maxSizeWidth) / groupedMessagePosition9.aspectRatio, 537.24005f))) / 814.0f;
                                groupedMessagePosition9.set(0, 2, 0, 0, this.maxSizeWidth, round5, 7);
                                float round6 = (float) Math.round(((float) this.maxSizeWidth) / ((groupedMessagePosition10.aspectRatio + groupedMessagePosition11.aspectRatio) + groupedMessagePosition12.aspectRatio));
                                float var_ = (float) dp2;
                                int max3 = (int) Math.max(var_, Math.min(((float) this.maxSizeWidth) * 0.4f, groupedMessagePosition10.aspectRatio * round6));
                                int max4 = (int) Math.max(Math.max(var_, ((float) this.maxSizeWidth) * 0.33f), groupedMessagePosition12.aspectRatio * round6);
                                float min4 = Math.min(814.0f - round5, round6) / 814.0f;
                                groupedMessagePosition10.set(0, 0, 1, 1, max3, min4, 9);
                                groupedMessagePosition11.set(1, 1, 1, 1, (this.maxSizeWidth - max3) - max4, min4, 8);
                                groupedMessagePosition12.set(2, 2, 1, 1, max4, min4, 10);
                            } else {
                                int max5 = Math.max(dp2, Math.round(814.0f / (((1.0f / groupedMessagePosition10.aspectRatio) + (1.0f / groupedMessagePosition11.aspectRatio)) + (1.0f / this.posArray.get(3).aspectRatio))));
                                float var_ = (float) dp;
                                float var_ = (float) max5;
                                float min5 = Math.min(0.33f, Math.max(var_, var_ / groupedMessagePosition10.aspectRatio) / 814.0f);
                                float min6 = Math.min(0.33f, Math.max(var_, var_ / groupedMessagePosition11.aspectRatio) / 814.0f);
                                float var_ = (1.0f - min5) - min6;
                                int round7 = Math.round(Math.min((814.0f * groupedMessagePosition9.aspectRatio) + ((float) dp3), (float) (this.maxSizeWidth - max5)));
                                groupedMessagePosition9.set(0, 0, 0, 2, round7, min5 + min6 + var_, 13);
                                int i56 = max5;
                                groupedMessagePosition10.set(1, 1, 0, 0, i56, min5, 6);
                                groupedMessagePosition11.set(0, 1, 1, 1, i56, min6, 2);
                                groupedMessagePosition11.spanSize = this.maxSizeWidth;
                                groupedMessagePosition12.set(0, 1, 2, 2, i56, var_, 10);
                                int i57 = this.maxSizeWidth;
                                groupedMessagePosition12.spanSize = i57;
                                groupedMessagePosition10.spanSize = i57 - round7;
                                groupedMessagePosition11.leftSpanOffset = round7;
                                groupedMessagePosition12.leftSpanOffset = round7;
                                groupedMessagePosition9.siblingHeights = new float[]{min5, min6, var_};
                            }
                        }
                        i = size;
                    }
                    int i58 = i;
                    for (int i59 = 0; i59 < i58; i59++) {
                        MessageObject.GroupedMessagePosition groupedMessagePosition13 = this.posArray.get(i59);
                        if ((groupedMessagePosition13.flags & 1) != 0) {
                            groupedMessagePosition13.edge = true;
                        }
                    }
                }
            }
        }

        public BlockCollageCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            AnonymousClass1 r11 = new RecyclerListView(context, ArticleViewer.this) {
                public void requestLayout() {
                    if (!BlockCollageCell.this.inLayout) {
                        super.requestLayout();
                    }
                }
            };
            this.innerListView = r11;
            r11.addItemDecoration(new RecyclerView.ItemDecoration(ArticleViewer.this) {
                public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                    MessageObject.GroupedMessagePosition groupedMessagePosition;
                    int i = 0;
                    rect.bottom = 0;
                    if (view instanceof BlockPhotoCell) {
                        groupedMessagePosition = BlockCollageCell.this.group.positions.get(((BlockPhotoCell) view).currentBlock);
                    } else {
                        groupedMessagePosition = view instanceof BlockVideoCell ? BlockCollageCell.this.group.positions.get(((BlockVideoCell) view).currentBlock) : null;
                    }
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
                        int dp2 = i3 + ((groupedMessagePosition.maxY - groupedMessagePosition.minY) * AndroidUtilities.dp2(11.0f));
                        int size = BlockCollageCell.this.group.posArray.size();
                        while (true) {
                            if (i < size) {
                                MessageObject.GroupedMessagePosition groupedMessagePosition2 = BlockCollageCell.this.group.posArray.get(i);
                                byte b = groupedMessagePosition2.minY;
                                byte b2 = groupedMessagePosition.minY;
                                if (b == b2 && ((groupedMessagePosition2.minX != groupedMessagePosition.minX || groupedMessagePosition2.maxX != groupedMessagePosition.maxX || b != b2 || groupedMessagePosition2.maxY != groupedMessagePosition.maxY) && b == b2)) {
                                    dp2 -= ((int) Math.ceil((double) (max * groupedMessagePosition2.ph))) - AndroidUtilities.dp(4.0f);
                                    break;
                                }
                                i++;
                            } else {
                                break;
                            }
                        }
                        rect.bottom = -dp2;
                    }
                }
            });
            AnonymousClass3 r1 = new GridLayoutManagerFixed(context, 1000, 1, true, ArticleViewer.this) {
                public boolean shouldLayoutChildFromOpositeSide(View view) {
                    return false;
                }

                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                /* access modifiers changed from: protected */
                public boolean hasSiblingChild(int i) {
                    byte b;
                    byte b2;
                    MessageObject.GroupedMessagePosition groupedMessagePosition = BlockCollageCell.this.group.positions.get(BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - i) - 1));
                    if (!(groupedMessagePosition.minX == groupedMessagePosition.maxX || (b = groupedMessagePosition.minY) != groupedMessagePosition.maxY || b == 0)) {
                        int size = BlockCollageCell.this.group.posArray.size();
                        for (int i2 = 0; i2 < size; i2++) {
                            MessageObject.GroupedMessagePosition groupedMessagePosition2 = BlockCollageCell.this.group.posArray.get(i2);
                            if (groupedMessagePosition2 != groupedMessagePosition && groupedMessagePosition2.minY <= (b2 = groupedMessagePosition.minY) && groupedMessagePosition2.maxY >= b2) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            };
            r1.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(ArticleViewer.this) {
                public int getSpanSize(int i) {
                    return BlockCollageCell.this.group.positions.get(BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - i) - 1)).spanSize;
                }
            });
            this.innerListView.setLayoutManager(r1);
            RecyclerListView recyclerListView = this.innerListView;
            AnonymousClass5 r112 = new RecyclerView.Adapter(ArticleViewer.this) {
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View view;
                    if (i != 0) {
                        BlockCollageCell blockCollageCell = BlockCollageCell.this;
                        view = new BlockVideoCell(blockCollageCell.getContext(), BlockCollageCell.this.parentAdapter, 2);
                    } else {
                        BlockCollageCell blockCollageCell2 = BlockCollageCell.this;
                        view = new BlockPhotoCell(blockCollageCell2.getContext(), BlockCollageCell.this.parentAdapter, 2);
                    }
                    return new RecyclerListView.Holder(view);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    TLRPC$PageBlock tLRPC$PageBlock = BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - i) - 1);
                    if (viewHolder.getItemViewType() != 0) {
                        BlockVideoCell blockVideoCell = (BlockVideoCell) viewHolder.itemView;
                        MessageObject.GroupedMessagePosition unused = blockVideoCell.groupPosition = BlockCollageCell.this.group.positions.get(tLRPC$PageBlock);
                        blockVideoCell.setBlock((TLRPC$TL_pageBlockVideo) tLRPC$PageBlock, true, true);
                        return;
                    }
                    BlockPhotoCell blockPhotoCell = (BlockPhotoCell) viewHolder.itemView;
                    MessageObject.GroupedMessagePosition unused2 = blockPhotoCell.groupPosition = BlockCollageCell.this.group.positions.get(tLRPC$PageBlock);
                    blockPhotoCell.setBlock((TLRPC$TL_pageBlockPhoto) tLRPC$PageBlock, true, true);
                }

                public int getItemCount() {
                    if (BlockCollageCell.this.currentBlock == null) {
                        return 0;
                    }
                    return BlockCollageCell.this.currentBlock.items.size();
                }

                public int getItemViewType(int i) {
                    if (BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - i) - 1) instanceof TLRPC$TL_pageBlockPhoto) {
                        return 0;
                    }
                    return 1;
                }
            };
            this.innerAdapter = r112;
            recyclerListView.setAdapter(r112);
            addView(this.innerListView, LayoutHelper.createFrame(-1, -2.0f));
            setWillNotDraw(false);
        }

        public void setBlock(TLRPC$TL_pageBlockCollage tLRPC$TL_pageBlockCollage) {
            if (this.currentBlock != tLRPC$TL_pageBlockCollage) {
                this.currentBlock = tLRPC$TL_pageBlockCollage;
                this.group.calculate();
            }
            this.innerAdapter.notifyDataSetChanged();
            this.innerListView.setGlowColor(Theme.getColor("windowBackgroundWhite"));
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.captionLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(motionEvent);
            }
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int i3;
            int i4;
            int i5 = 1;
            this.inLayout = true;
            int size = View.MeasureSpec.getSize(i);
            TLRPC$TL_pageBlockCollage tLRPC$TL_pageBlockCollage = this.currentBlock;
            if (tLRPC$TL_pageBlockCollage != null) {
                int i6 = tLRPC$TL_pageBlockCollage.level;
                if (i6 > 0) {
                    int dp = AndroidUtilities.dp((float) (i6 * 14)) + AndroidUtilities.dp(18.0f);
                    this.listX = dp;
                    this.textX = dp;
                    i3 = size - (dp + AndroidUtilities.dp(18.0f));
                    i4 = i3;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    i4 = size - AndroidUtilities.dp(36.0f);
                    i3 = size;
                }
                this.innerListView.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                int measuredHeight = this.innerListView.getMeasuredHeight();
                int dp2 = measuredHeight + AndroidUtilities.dp(8.0f);
                this.textY = dp2;
                ArticleViewer articleViewer = ArticleViewer.this;
                TLRPC$TL_pageBlockCollage tLRPC$TL_pageBlockCollage2 = this.currentBlock;
                DrawingText access$13300 = articleViewer.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockCollage2.caption.text, i4, dp2, tLRPC$TL_pageBlockCollage2, this.parentAdapter);
                this.captionLayout = access$13300;
                if (access$13300 != null) {
                    int dp3 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp3;
                    measuredHeight += dp3 + AndroidUtilities.dp(4.0f);
                    DrawingText drawingText = this.captionLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                } else {
                    this.creditOffset = 0;
                }
                ArticleViewer articleViewer2 = ArticleViewer.this;
                TLRPC$TL_pageBlockCollage tLRPC$TL_pageBlockCollage3 = this.currentBlock;
                DrawingText access$13400 = articleViewer2.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockCollage3.caption.credit, i4, this.textY + this.creditOffset, tLRPC$TL_pageBlockCollage3, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = access$13400;
                if (access$13400 != null) {
                    measuredHeight += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    DrawingText drawingText2 = this.creditLayout;
                    drawingText2.x = this.textX;
                    drawingText2.y = this.textY + this.creditOffset;
                }
                i5 = measuredHeight + AndroidUtilities.dp(16.0f);
                TLRPC$TL_pageBlockCollage tLRPC$TL_pageBlockCollage4 = this.currentBlock;
                if (tLRPC$TL_pageBlockCollage4.level > 0 && !tLRPC$TL_pageBlockCollage4.bottom) {
                    i5 += AndroidUtilities.dp(8.0f);
                }
            }
            setMeasuredDimension(size, i5);
            this.inLayout = false;
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            this.innerListView.layout(this.listX, AndroidUtilities.dp(8.0f), this.listX + this.innerListView.getMeasuredWidth(), this.innerListView.getMeasuredHeight() + AndroidUtilities.dp(8.0f));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            if (this.currentBlock != null) {
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                arrayList.add(drawingText2);
            }
        }
    }

    private class BlockSlideshowCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        /* access modifiers changed from: private */
        public TLRPC$TL_pageBlockSlideshow currentBlock;
        /* access modifiers changed from: private */
        public int currentPage;
        /* access modifiers changed from: private */
        public View dotsContainer;
        /* access modifiers changed from: private */
        public PagerAdapter innerAdapter;
        /* access modifiers changed from: private */
        public ViewPager innerListView;
        /* access modifiers changed from: private */
        public float pageOffset;
        /* access modifiers changed from: private */
        public WebpageAdapter parentAdapter;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockSlideshowCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            if (ArticleViewer.dotsPaint == null) {
                Paint unused = ArticleViewer.dotsPaint = new Paint(1);
                ArticleViewer.dotsPaint.setColor(-1);
            }
            AnonymousClass1 r4 = new ViewPager(context, ArticleViewer.this) {
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return super.onTouchEvent(motionEvent);
                }

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    ArticleViewer.this.cancelCheckLongPress();
                    return super.onInterceptTouchEvent(motionEvent);
                }
            };
            this.innerListView = r4;
            r4.addOnPageChangeListener(new ViewPager.OnPageChangeListener(ArticleViewer.this) {
                public void onPageScrollStateChanged(int i) {
                }

                public void onPageScrolled(int i, float f, int i2) {
                    float measuredWidth = (float) BlockSlideshowCell.this.innerListView.getMeasuredWidth();
                    if (measuredWidth != 0.0f) {
                        BlockSlideshowCell blockSlideshowCell = BlockSlideshowCell.this;
                        float unused = blockSlideshowCell.pageOffset = (((((float) i) * measuredWidth) + ((float) i2)) - (((float) blockSlideshowCell.currentPage) * measuredWidth)) / measuredWidth;
                        BlockSlideshowCell.this.dotsContainer.invalidate();
                    }
                }

                public void onPageSelected(int i) {
                    int unused = BlockSlideshowCell.this.currentPage = i;
                    BlockSlideshowCell.this.dotsContainer.invalidate();
                }
            });
            ViewPager viewPager = this.innerListView;
            AnonymousClass3 r0 = new PagerAdapter(ArticleViewer.this) {

                /* renamed from: org.telegram.ui.ArticleViewer$BlockSlideshowCell$3$ObjectContainer */
                class ObjectContainer {
                    /* access modifiers changed from: private */
                    public TLRPC$PageBlock block;
                    /* access modifiers changed from: private */
                    public View view;

                    ObjectContainer(AnonymousClass3 r1) {
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

                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.ui.ArticleViewer$BlockVideoCell} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.ui.ArticleViewer$BlockPhotoCell} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.ui.ArticleViewer$BlockVideoCell} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.ui.ArticleViewer$BlockVideoCell} */
                /* JADX WARNING: Multi-variable type inference failed */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public java.lang.Object instantiateItem(android.view.ViewGroup r6, int r7) {
                    /*
                        r5 = this;
                        org.telegram.ui.ArticleViewer$BlockSlideshowCell r0 = org.telegram.ui.ArticleViewer.BlockSlideshowCell.this
                        org.telegram.tgnet.TLRPC$TL_pageBlockSlideshow r0 = r0.currentBlock
                        java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r0 = r0.items
                        java.lang.Object r7 = r0.get(r7)
                        org.telegram.tgnet.TLRPC$PageBlock r7 = (org.telegram.tgnet.TLRPC$PageBlock) r7
                        boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockPhoto
                        r1 = 1
                        if (r0 == 0) goto L_0x002d
                        org.telegram.ui.ArticleViewer$BlockPhotoCell r0 = new org.telegram.ui.ArticleViewer$BlockPhotoCell
                        org.telegram.ui.ArticleViewer$BlockSlideshowCell r2 = org.telegram.ui.ArticleViewer.BlockSlideshowCell.this
                        org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                        android.content.Context r2 = r2.getContext()
                        org.telegram.ui.ArticleViewer$BlockSlideshowCell r4 = org.telegram.ui.ArticleViewer.BlockSlideshowCell.this
                        org.telegram.ui.ArticleViewer$WebpageAdapter r4 = r4.parentAdapter
                        r0.<init>(r2, r4, r1)
                        r2 = r7
                        org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r2 = (org.telegram.tgnet.TLRPC$TL_pageBlockPhoto) r2
                        r0.setBlock(r2, r1, r1)
                        goto L_0x0046
                    L_0x002d:
                        org.telegram.ui.ArticleViewer$BlockVideoCell r0 = new org.telegram.ui.ArticleViewer$BlockVideoCell
                        org.telegram.ui.ArticleViewer$BlockSlideshowCell r2 = org.telegram.ui.ArticleViewer.BlockSlideshowCell.this
                        org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                        android.content.Context r2 = r2.getContext()
                        org.telegram.ui.ArticleViewer$BlockSlideshowCell r4 = org.telegram.ui.ArticleViewer.BlockSlideshowCell.this
                        org.telegram.ui.ArticleViewer$WebpageAdapter r4 = r4.parentAdapter
                        r0.<init>(r2, r4, r1)
                        r2 = r7
                        org.telegram.tgnet.TLRPC$TL_pageBlockVideo r2 = (org.telegram.tgnet.TLRPC$TL_pageBlockVideo) r2
                        r0.setBlock(r2, r1, r1)
                    L_0x0046:
                        r6.addView(r0)
                        org.telegram.ui.ArticleViewer$BlockSlideshowCell$3$ObjectContainer r6 = new org.telegram.ui.ArticleViewer$BlockSlideshowCell$3$ObjectContainer
                        r6.<init>(r5)
                        android.view.View unused = r6.view = r0
                        org.telegram.tgnet.TLRPC$PageBlock unused = r6.block = r7
                        return r6
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockSlideshowCell.AnonymousClass3.instantiateItem(android.view.ViewGroup, int):java.lang.Object");
                }

                public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
                    viewGroup.removeView(((ObjectContainer) obj).view);
                }
            };
            this.innerAdapter = r0;
            viewPager.setAdapter(r0);
            AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, Theme.getColor("windowBackgroundWhite"));
            addView(this.innerListView);
            AnonymousClass4 r42 = new View(context, ArticleViewer.this) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    int i;
                    int access$17500;
                    if (BlockSlideshowCell.this.currentBlock != null) {
                        int count = BlockSlideshowCell.this.innerAdapter.getCount();
                        int dp = (AndroidUtilities.dp(7.0f) * count) + ((count - 1) * AndroidUtilities.dp(6.0f)) + AndroidUtilities.dp(4.0f);
                        if (dp < getMeasuredWidth()) {
                            i = (getMeasuredWidth() - dp) / 2;
                        } else {
                            int dp2 = AndroidUtilities.dp(4.0f);
                            int dp3 = AndroidUtilities.dp(13.0f);
                            int measuredWidth = ((getMeasuredWidth() - AndroidUtilities.dp(8.0f)) / 2) / dp3;
                            int i2 = (count - measuredWidth) - 1;
                            if (BlockSlideshowCell.this.currentPage != i2 || BlockSlideshowCell.this.pageOffset >= 0.0f) {
                                if (BlockSlideshowCell.this.currentPage >= i2) {
                                    access$17500 = ((count - (measuredWidth * 2)) - 1) * dp3;
                                } else if (BlockSlideshowCell.this.currentPage > measuredWidth) {
                                    access$17500 = ((int) (BlockSlideshowCell.this.pageOffset * ((float) dp3))) + ((BlockSlideshowCell.this.currentPage - measuredWidth) * dp3);
                                } else if (BlockSlideshowCell.this.currentPage != measuredWidth || BlockSlideshowCell.this.pageOffset <= 0.0f) {
                                    i = dp2;
                                } else {
                                    access$17500 = (int) (BlockSlideshowCell.this.pageOffset * ((float) dp3));
                                }
                                i = dp2 - access$17500;
                            } else {
                                i = dp2 - (((int) (BlockSlideshowCell.this.pageOffset * ((float) dp3))) + (((count - (measuredWidth * 2)) - 1) * dp3));
                            }
                        }
                        int i3 = 0;
                        while (i3 < BlockSlideshowCell.this.currentBlock.items.size()) {
                            int dp4 = AndroidUtilities.dp(4.0f) + i + (AndroidUtilities.dp(13.0f) * i3);
                            Drawable access$18300 = BlockSlideshowCell.this.currentPage == i3 ? ArticleViewer.this.slideDotBigDrawable : ArticleViewer.this.slideDotDrawable;
                            access$18300.setBounds(dp4 - AndroidUtilities.dp(5.0f), 0, dp4 + AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f));
                            access$18300.draw(canvas);
                            i3++;
                        }
                    }
                }
            };
            this.dotsContainer = r42;
            addView(r42);
            setWillNotDraw(false);
        }

        public void setBlock(TLRPC$TL_pageBlockSlideshow tLRPC$TL_pageBlockSlideshow) {
            this.currentBlock = tLRPC$TL_pageBlockSlideshow;
            this.innerAdapter.notifyDataSetChanged();
            this.innerListView.setCurrentItem(0, false);
            this.innerListView.forceLayout();
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.captionLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(motionEvent);
            }
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int i3;
            int size = View.MeasureSpec.getSize(i);
            if (this.currentBlock != null) {
                int dp = AndroidUtilities.dp(310.0f);
                this.innerListView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(dp, NUM));
                this.currentBlock.items.size();
                this.dotsContainer.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f), NUM));
                int dp2 = size - AndroidUtilities.dp(36.0f);
                int dp3 = dp + AndroidUtilities.dp(16.0f);
                this.textY = dp3;
                ArticleViewer articleViewer = ArticleViewer.this;
                TLRPC$TL_pageBlockSlideshow tLRPC$TL_pageBlockSlideshow = this.currentBlock;
                DrawingText access$13300 = articleViewer.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockSlideshow.caption.text, dp2, dp3, tLRPC$TL_pageBlockSlideshow, this.parentAdapter);
                this.captionLayout = access$13300;
                if (access$13300 != null) {
                    int dp4 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp4;
                    dp += dp4 + AndroidUtilities.dp(4.0f);
                    DrawingText drawingText = this.captionLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                } else {
                    this.creditOffset = 0;
                }
                ArticleViewer articleViewer2 = ArticleViewer.this;
                TLRPC$TL_pageBlockSlideshow tLRPC$TL_pageBlockSlideshow2 = this.currentBlock;
                DrawingText access$13400 = articleViewer2.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockSlideshow2.caption.credit, dp2, this.textY + this.creditOffset, tLRPC$TL_pageBlockSlideshow2, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = access$13400;
                if (access$13400 != null) {
                    dp += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    DrawingText drawingText2 = this.creditLayout;
                    drawingText2.x = this.textX;
                    drawingText2.y = this.textY + this.creditOffset;
                }
                i3 = dp + AndroidUtilities.dp(16.0f);
            } else {
                i3 = 1;
            }
            setMeasuredDimension(size, i3);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            this.innerListView.layout(0, AndroidUtilities.dp(8.0f), this.innerListView.getMeasuredWidth(), AndroidUtilities.dp(8.0f) + this.innerListView.getMeasuredHeight());
            int bottom = this.innerListView.getBottom() - AndroidUtilities.dp(23.0f);
            View view = this.dotsContainer;
            view.layout(0, bottom, view.getMeasuredWidth(), this.dotsContainer.getMeasuredHeight() + bottom);
        }

        /* access modifiers changed from: protected */
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                arrayList.add(drawingText2);
            }
        }
    }

    private class BlockListItemCell extends ViewGroup implements TextSelectionHelper.ArticleSelectableView {
        /* access modifiers changed from: private */
        public RecyclerView.ViewHolder blockLayout;
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
        /* access modifiers changed from: private */
        public boolean verticalAlign;

        public BlockListItemCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockListItem tL_pageBlockListItem) {
            if (this.currentBlock != tL_pageBlockListItem) {
                this.currentBlock = tL_pageBlockListItem;
                RecyclerView.ViewHolder viewHolder = this.blockLayout;
                if (viewHolder != null) {
                    removeView(viewHolder.itemView);
                    this.blockLayout = null;
                }
                if (this.currentBlock.blockItem != null) {
                    int access$7500 = this.parentAdapter.getTypeForBlock(this.currentBlock.blockItem);
                    this.currentBlockType = access$7500;
                    RecyclerView.ViewHolder onCreateViewHolder = this.parentAdapter.onCreateViewHolder(this, access$7500);
                    this.blockLayout = onCreateViewHolder;
                    addView(onCreateViewHolder.itemView);
                }
            }
            if (this.currentBlock.blockItem != null) {
                this.parentAdapter.bindBlockToHolder(this.currentBlockType, this.blockLayout, this.currentBlock.blockItem, 0, 0);
            }
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:113:0x0398  */
        /* JADX WARNING: Removed duplicated region for block: B:121:0x03bb  */
        /* JADX WARNING: Removed duplicated region for block: B:129:0x03f5  */
        @android.annotation.SuppressLint({"NewApi"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r17, int r18) {
            /*
                r16 = this;
                r9 = r16
                int r10 = android.view.View.MeasureSpec.getSize(r17)
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                r11 = 1
                if (r0 == 0) goto L_0x0410
                r1 = 0
                r9.textLayout = r1
                int r0 = r0.index
                r12 = 1092616192(0x41200000, float:10.0)
                r13 = 0
                if (r0 != 0) goto L_0x0028
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                int r0 = r0.level
                if (r0 != 0) goto L_0x0028
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r12)
                goto L_0x0029
            L_0x0028:
                r0 = 0
            L_0x0029:
                r9.textY = r0
                r9.numOffsetY = r13
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                int r0 = r0.lastMaxNumCalcWidth
                if (r0 != r10) goto L_0x0047
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                int r0 = r0.lastFontSize
                int r1 = org.telegram.messenger.SharedConfig.ivFontSize
                if (r0 == r1) goto L_0x00fa
            L_0x0047:
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                int unused = r0.lastMaxNumCalcWidth = r10
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                int r1 = org.telegram.messenger.SharedConfig.ivFontSize
                int unused = r0.lastFontSize = r1
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                int unused = r0.maxNumWidth = r13
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                java.util.ArrayList r0 = r0.items
                int r8 = r0.size()
                r14 = 0
            L_0x0073:
                if (r14 >= r8) goto L_0x00d3
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                java.util.ArrayList r0 = r0.items
                java.lang.Object r0 = r0.get(r14)
                r15 = r0
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r15 = (org.telegram.ui.ArticleViewer.TL_pageBlockListItem) r15
                java.lang.String r0 = r15.num
                if (r0 != 0) goto L_0x008d
                goto L_0x00d0
            L_0x008d:
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                java.lang.String r2 = r15.num
                r3 = 0
                r1 = 1113063424(0x42580000, float:54.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r4 = r10 - r1
                int r5 = r9.textY
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r6 = r9.currentBlock
                org.telegram.ui.ArticleViewer$WebpageAdapter r7 = r9.parentAdapter
                r1 = r16
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7)
                org.telegram.ui.ArticleViewer.DrawingText unused = r15.numLayout = r0
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r1 = r1.parent
                int r1 = r1.maxNumWidth
                org.telegram.ui.ArticleViewer$DrawingText r2 = r15.numLayout
                float r2 = r2.getLineWidth(r13)
                double r2 = (double) r2
                double r2 = java.lang.Math.ceil(r2)
                int r2 = (int) r2
                int r1 = java.lang.Math.max(r1, r2)
                int unused = r0.maxNumWidth = r1
            L_0x00d0:
                int r14 = r14 + 1
                goto L_0x0073
            L_0x00d3:
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r1 = r1.parent
                int r1 = r1.maxNumWidth
                android.text.TextPaint r2 = org.telegram.ui.ArticleViewer.listTextNumPaint
                java.lang.String r3 = "00."
                float r2 = r2.measureText(r3)
                double r2 = (double) r2
                double r2 = java.lang.Math.ceil(r2)
                int r2 = (int) r2
                int r1 = java.lang.Math.max(r1, r2)
                int unused = r0.maxNumWidth = r1
            L_0x00fa:
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                org.telegram.tgnet.TLRPC$TL_pageBlockList r0 = r0.pageBlockList
                boolean r0 = r0.ordered
                r0 = r0 ^ r11
                r9.drawDot = r0
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r9.parentAdapter
                boolean r0 = r0.isRtl
                r1 = 1094713344(0x41400000, float:12.0)
                r2 = 1099956224(0x41900000, float:18.0)
                if (r0 == 0) goto L_0x011c
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r9.textX = r0
                goto L_0x0140
            L_0x011c:
                r0 = 1103101952(0x41CLASSNAME, float:24.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r3 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r3 = r3.parent
                int r3 = r3.maxNumWidth
                int r0 = r0 + r3
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r3 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r3 = r3.parent
                int r3 = r3.level
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r3 = r3 * r4
                int r0 = r0 + r3
                r9.textX = r0
            L_0x0140:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r0 = r10 - r0
                int r3 = r9.textX
                int r0 = r0 - r3
                org.telegram.ui.ArticleViewer$WebpageAdapter r3 = r9.parentAdapter
                boolean r3 = r3.isRtl
                if (r3 == 0) goto L_0x0174
                r3 = 1086324736(0x40CLASSNAME, float:6.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r4 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r4 = r4.parent
                int r4 = r4.maxNumWidth
                int r3 = r3 + r4
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r4 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r4 = r4.parent
                int r4 = r4.level
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r4 = r4 * r1
                int r3 = r3 + r4
                int r0 = r0 - r3
            L_0x0174:
                r4 = r0
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.tgnet.TLRPC$RichText r0 = r0.textItem
                r14 = 1075838976(0x40200000, float:2.5)
                r15 = 1090519040(0x41000000, float:8.0)
                if (r0 == 0) goto L_0x01e9
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = r9.currentBlock
                org.telegram.tgnet.TLRPC$RichText r3 = r1.textItem
                int r5 = r9.textY
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r6 = r9.currentBlock
                org.telegram.ui.ArticleViewer$WebpageAdapter r1 = r9.parentAdapter
                boolean r1 = r1.isRtl
                if (r1 == 0) goto L_0x019b
                android.text.Layout$Alignment r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT()
                goto L_0x019d
            L_0x019b:
                android.text.Layout$Alignment r1 = android.text.Layout.Alignment.ALIGN_NORMAL
            L_0x019d:
                r7 = r1
                org.telegram.ui.ArticleViewer$WebpageAdapter r8 = r9.parentAdapter
                r1 = r16
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8)
                r9.textLayout = r0
                if (r0 == 0) goto L_0x0377
                int r0 = r0.getLineCount()
                if (r0 <= 0) goto L_0x0377
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.numLayout
                if (r0 == 0) goto L_0x01dc
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.numLayout
                int r0 = r0.getLineCount()
                if (r0 <= 0) goto L_0x01dc
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.textLayout
                int r0 = r0.getLineAscent(r13)
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r1 = r1.numLayout
                int r1 = r1.getLineAscent(r13)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r1 = r1 + r2
                int r1 = r1 - r0
                r9.numOffsetY = r1
            L_0x01dc:
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.textLayout
                int r0 = r0.getHeight()
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r0 = r0 + r1
                goto L_0x0376
            L_0x01e9:
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.tgnet.TLRPC$PageBlock r0 = r0.blockItem
                if (r0 == 0) goto L_0x0377
                int r0 = r9.textX
                r9.blockX = r0
                int r0 = r9.textY
                r9.blockY = r0
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                if (r1 == 0) goto L_0x0372
                android.view.View r1 = r1.itemView
                boolean r3 = r1 instanceof org.telegram.ui.ArticleViewer.BlockParagraphCell
                if (r3 == 0) goto L_0x0228
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r0 = r0 - r1
                r9.blockY = r0
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r9.parentAdapter
                boolean r0 = r0.isRtl
                if (r0 != 0) goto L_0x021b
                int r0 = r9.blockX
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r0 = r0 - r1
                r9.blockX = r0
            L_0x021b:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r4 = r4 + r0
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r0 = 0 - r0
                goto L_0x029f
            L_0x0228:
                boolean r0 = r1 instanceof org.telegram.ui.ArticleViewer.BlockHeaderCell
                if (r0 != 0) goto L_0x0288
                boolean r0 = r1 instanceof org.telegram.ui.ArticleViewer.BlockSubheaderCell
                if (r0 != 0) goto L_0x0288
                boolean r0 = r1 instanceof org.telegram.ui.ArticleViewer.BlockTitleCell
                if (r0 != 0) goto L_0x0288
                boolean r0 = r1 instanceof org.telegram.ui.ArticleViewer.BlockSubtitleCell
                if (r0 == 0) goto L_0x0239
                goto L_0x0288
            L_0x0239:
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = r9.currentBlock
                org.telegram.tgnet.TLRPC$PageBlock r1 = r1.blockItem
                boolean r0 = r0.isListItemBlock(r1)
                if (r0 == 0) goto L_0x0270
                r9.blockX = r13
                r9.blockY = r13
                r9.textY = r13
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                int r0 = r0.index
                if (r0 != 0) goto L_0x0268
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                int r0 = r0.level
                if (r0 != 0) goto L_0x0268
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r0 = 0 - r0
                goto L_0x0269
            L_0x0268:
                r0 = 0
            L_0x0269:
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r0 = r0 - r1
                r4 = r10
                goto L_0x029f
            L_0x0270:
                androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r9.blockLayout
                android.view.View r0 = r0.itemView
                boolean r0 = r0 instanceof org.telegram.ui.ArticleViewer.BlockTableCell
                if (r0 == 0) goto L_0x029e
                int r0 = r9.blockX
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r0 = r0 - r1
                r9.blockX = r0
                r0 = 1108344832(0x42100000, float:36.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                goto L_0x029d
            L_0x0288:
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r9.parentAdapter
                boolean r0 = r0.isRtl
                if (r0 != 0) goto L_0x0299
                int r0 = r9.blockX
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r0 = r0 - r1
                r9.blockX = r0
            L_0x0299:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            L_0x029d:
                int r4 = r4 + r0
            L_0x029e:
                r0 = 0
            L_0x029f:
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                android.view.View r1 = r1.itemView
                r2 = 1073741824(0x40000000, float:2.0)
                int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r2)
                int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r13)
                r1.measure(r2, r3)
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                android.view.View r1 = r1.itemView
                boolean r1 = r1 instanceof org.telegram.ui.ArticleViewer.BlockParagraphCell
                if (r1 == 0) goto L_0x02f4
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r1 = r1.numLayout
                if (r1 == 0) goto L_0x02f4
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r1 = r1.numLayout
                int r1 = r1.getLineCount()
                if (r1 <= 0) goto L_0x02f4
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                android.view.View r1 = r1.itemView
                org.telegram.ui.ArticleViewer$BlockParagraphCell r1 = (org.telegram.ui.ArticleViewer.BlockParagraphCell) r1
                org.telegram.ui.ArticleViewer$DrawingText r2 = r1.textLayout
                if (r2 == 0) goto L_0x02f4
                int r2 = r2.getLineCount()
                if (r2 <= 0) goto L_0x02f4
                org.telegram.ui.ArticleViewer$DrawingText r1 = r1.textLayout
                int r1 = r1.getLineAscent(r13)
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r2 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r2 = r2.numLayout
                int r2 = r2.getLineAscent(r13)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r2 = r2 + r3
                int r2 = r2 - r1
                r9.numOffsetY = r2
            L_0x02f4:
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = r9.currentBlock
                org.telegram.tgnet.TLRPC$PageBlock r1 = r1.blockItem
                boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockDetails
                if (r1 == 0) goto L_0x0321
                r9.verticalAlign = r11
                r9.blockY = r13
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = r9.currentBlock
                int r1 = r1.index
                if (r1 != 0) goto L_0x031b
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r1 = r1.parent
                int r1 = r1.level
                if (r1 != 0) goto L_0x031b
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r0 = r0 - r1
            L_0x031b:
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r0 = r0 - r1
                goto L_0x033c
            L_0x0321:
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                android.view.View r1 = r1.itemView
                boolean r2 = r1 instanceof org.telegram.ui.ArticleViewer.BlockOrderedListItemCell
                if (r2 == 0) goto L_0x0332
                org.telegram.ui.ArticleViewer$BlockOrderedListItemCell r1 = (org.telegram.ui.ArticleViewer.BlockOrderedListItemCell) r1
                boolean r1 = r1.verticalAlign
                r9.verticalAlign = r1
                goto L_0x033c
            L_0x0332:
                boolean r2 = r1 instanceof org.telegram.ui.ArticleViewer.BlockListItemCell
                if (r2 == 0) goto L_0x033c
                org.telegram.ui.ArticleViewer$BlockListItemCell r1 = (org.telegram.ui.ArticleViewer.BlockListItemCell) r1
                boolean r1 = r1.verticalAlign
                r9.verticalAlign = r1
            L_0x033c:
                boolean r1 = r9.verticalAlign
                if (r1 == 0) goto L_0x0368
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r1 = r1.numLayout
                if (r1 == 0) goto L_0x0368
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                android.view.View r1 = r1.itemView
                int r1 = r1.getMeasuredHeight()
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r2 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r2 = r2.numLayout
                int r2 = r2.getHeight()
                int r1 = r1 - r2
                int r1 = r1 / 2
                r2 = 1082130432(0x40800000, float:4.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r1 = r1 - r2
                r9.textY = r1
                r9.drawDot = r13
            L_0x0368:
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                android.view.View r1 = r1.itemView
                int r1 = r1.getMeasuredHeight()
                int r13 = r0 + r1
            L_0x0372:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
            L_0x0376:
                int r13 = r13 + r0
            L_0x0377:
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                java.util.ArrayList r0 = r0.items
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r1 = r1.parent
                java.util.ArrayList r1 = r1.items
                int r1 = r1.size()
                int r1 = r1 - r11
                java.lang.Object r0 = r0.get(r1)
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r1 = r9.currentBlock
                if (r0 != r1) goto L_0x039d
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r13 = r13 + r0
            L_0x039d:
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                int r0 = r0.index
                if (r0 != 0) goto L_0x03b6
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r0 = r0.parent
                int r0 = r0.level
                if (r0 != 0) goto L_0x03b6
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r13 = r13 + r0
            L_0x03b6:
                r11 = r13
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.textLayout
                if (r0 == 0) goto L_0x03c3
                int r1 = r9.textX
                r0.x = r1
                int r1 = r9.textY
                r0.y = r1
            L_0x03c3:
                androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r9.blockLayout
                if (r0 == 0) goto L_0x0410
                android.view.View r0 = r0.itemView
                boolean r0 = r0 instanceof org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
                if (r0 == 0) goto L_0x0410
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r0 = r0.textSelectionHelper
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r0 = r0.arrayList
                r0.clear()
                androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r9.blockLayout
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.TextSelectionHelper$ArticleSelectableView r0 = (org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r0
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r1 = r1.textSelectionHelper
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r1 = r1.arrayList
                r0.fillTextLayoutBlocks(r1)
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r0 = r0.textSelectionHelper
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r0 = r0.arrayList
                java.util.Iterator r0 = r0.iterator()
            L_0x03ef:
                boolean r1 = r0.hasNext()
                if (r1 == 0) goto L_0x0410
                java.lang.Object r1 = r0.next()
                org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock r1 = (org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock) r1
                boolean r2 = r1 instanceof org.telegram.ui.ArticleViewer.DrawingText
                if (r2 == 0) goto L_0x03ef
                org.telegram.ui.ArticleViewer$DrawingText r1 = (org.telegram.ui.ArticleViewer.DrawingText) r1
                int r2 = r1.x
                int r3 = r9.blockX
                int r2 = r2 + r3
                r1.x = r2
                int r2 = r1.y
                int r3 = r9.blockY
                int r2 = r2 + r3
                r1.y = r2
                goto L_0x03ef
            L_0x0410:
                r9.setMeasuredDimension(r10, r11)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockListItemCell.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                int i5 = this.blockX;
                view.layout(i5, this.blockY, view.getMeasuredWidth() + i5, this.blockY + this.blockLayout.itemView.getMeasuredHeight());
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int measuredWidth = getMeasuredWidth();
                if (this.currentBlock.numLayout != null) {
                    canvas.save();
                    int i = 0;
                    if (this.parentAdapter.isRtl) {
                        float dp = (float) (((measuredWidth - AndroidUtilities.dp(15.0f)) - this.currentBlock.parent.maxNumWidth) - (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f)));
                        int i2 = this.textY + this.numOffsetY;
                        if (this.drawDot) {
                            i = AndroidUtilities.dp(1.0f);
                        }
                        canvas.translate(dp, (float) (i2 - i));
                    } else {
                        float dp2 = (float) (((AndroidUtilities.dp(15.0f) + this.currentBlock.parent.maxNumWidth) - ((int) Math.ceil((double) this.currentBlock.numLayout.getLineWidth(0)))) + (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f)));
                        int i3 = this.textY + this.numOffsetY;
                        if (this.drawDot) {
                            i = AndroidUtilities.dp(1.0f);
                        }
                        canvas.translate(dp2, (float) (i3 - i));
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
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                if (view instanceof TextSelectionHelper.ArticleSelectableView) {
                    ((TextSelectionHelper.ArticleSelectableView) view).fillTextLayoutBlocks(arrayList);
                }
            }
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockOrderedListItemCell extends ViewGroup implements TextSelectionHelper.ArticleSelectableView {
        /* access modifiers changed from: private */
        public RecyclerView.ViewHolder blockLayout;
        private int blockX;
        private int blockY;
        private TL_pageBlockOrderedListItem currentBlock;
        private int currentBlockType;
        private int numOffsetY;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX;
        private int textY;
        /* access modifiers changed from: private */
        public boolean verticalAlign;

        public BlockOrderedListItemCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem) {
            if (this.currentBlock != tL_pageBlockOrderedListItem) {
                this.currentBlock = tL_pageBlockOrderedListItem;
                RecyclerView.ViewHolder viewHolder = this.blockLayout;
                if (viewHolder != null) {
                    removeView(viewHolder.itemView);
                    this.blockLayout = null;
                }
                if (this.currentBlock.blockItem != null) {
                    int access$7500 = this.parentAdapter.getTypeForBlock(this.currentBlock.blockItem);
                    this.currentBlockType = access$7500;
                    RecyclerView.ViewHolder onCreateViewHolder = this.parentAdapter.onCreateViewHolder(this, access$7500);
                    this.blockLayout = onCreateViewHolder;
                    addView(onCreateViewHolder.itemView);
                }
            }
            if (this.currentBlock.blockItem != null) {
                this.parentAdapter.bindBlockToHolder(this.currentBlockType, this.blockLayout, this.currentBlock.blockItem, 0, 0);
            }
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:102:0x0341  */
        /* JADX WARNING: Removed duplicated region for block: B:110:0x0364  */
        /* JADX WARNING: Removed duplicated region for block: B:120:0x03b6  */
        @android.annotation.SuppressLint({"NewApi"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r17, int r18) {
            /*
                r16 = this;
                r9 = r16
                int r10 = android.view.View.MeasureSpec.getSize(r17)
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                r11 = 1
                if (r0 == 0) goto L_0x03d1
                r1 = 0
                r9.textLayout = r1
                int r0 = r0.index
                r12 = 1092616192(0x41200000, float:10.0)
                r13 = 0
                if (r0 != 0) goto L_0x0028
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r0 = r0.parent
                int r0 = r0.level
                if (r0 != 0) goto L_0x0028
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r12)
                goto L_0x0029
            L_0x0028:
                r0 = 0
            L_0x0029:
                r9.textY = r0
                r9.numOffsetY = r13
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r0 = r0.parent
                int r0 = r0.lastMaxNumCalcWidth
                if (r0 != r10) goto L_0x0047
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r0 = r0.parent
                int r0 = r0.lastFontSize
                int r1 = org.telegram.messenger.SharedConfig.ivFontSize
                if (r0 == r1) goto L_0x00fa
            L_0x0047:
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r0 = r0.parent
                int unused = r0.lastMaxNumCalcWidth = r10
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r0 = r0.parent
                int r1 = org.telegram.messenger.SharedConfig.ivFontSize
                int unused = r0.lastFontSize = r1
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r0 = r0.parent
                int unused = r0.maxNumWidth = r13
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r0 = r0.parent
                java.util.ArrayList r0 = r0.items
                int r8 = r0.size()
                r14 = 0
            L_0x0073:
                if (r14 >= r8) goto L_0x00d3
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r0 = r0.parent
                java.util.ArrayList r0 = r0.items
                java.lang.Object r0 = r0.get(r14)
                r15 = r0
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r15 = (org.telegram.ui.ArticleViewer.TL_pageBlockOrderedListItem) r15
                java.lang.String r0 = r15.num
                if (r0 != 0) goto L_0x008d
                goto L_0x00d0
            L_0x008d:
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                java.lang.String r2 = r15.num
                r3 = 0
                r1 = 1113063424(0x42580000, float:54.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r4 = r10 - r1
                int r5 = r9.textY
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r6 = r9.currentBlock
                org.telegram.ui.ArticleViewer$WebpageAdapter r7 = r9.parentAdapter
                r1 = r16
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7)
                org.telegram.ui.ArticleViewer.DrawingText unused = r15.numLayout = r0
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r0 = r0.parent
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r1 = r1.parent
                int r1 = r1.maxNumWidth
                org.telegram.ui.ArticleViewer$DrawingText r2 = r15.numLayout
                float r2 = r2.getLineWidth(r13)
                double r2 = (double) r2
                double r2 = java.lang.Math.ceil(r2)
                int r2 = (int) r2
                int r1 = java.lang.Math.max(r1, r2)
                int unused = r0.maxNumWidth = r1
            L_0x00d0:
                int r14 = r14 + 1
                goto L_0x0073
            L_0x00d3:
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r0 = r0.parent
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r1 = r1.parent
                int r1 = r1.maxNumWidth
                android.text.TextPaint r2 = org.telegram.ui.ArticleViewer.listTextNumPaint
                java.lang.String r3 = "00."
                float r2 = r2.measureText(r3)
                double r2 = (double) r2
                double r2 = java.lang.Math.ceil(r2)
                int r2 = (int) r2
                int r1 = java.lang.Math.max(r1, r2)
                int unused = r0.maxNumWidth = r1
            L_0x00fa:
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r9.parentAdapter
                boolean r0 = r0.isRtl
                r1 = 1101004800(0x41a00000, float:20.0)
                r2 = 1099956224(0x41900000, float:18.0)
                if (r0 == 0) goto L_0x010d
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r9.textX = r0
                goto L_0x0131
            L_0x010d:
                r0 = 1103101952(0x41CLASSNAME, float:24.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r3 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r3 = r3.parent
                int r3 = r3.maxNumWidth
                int r0 = r0 + r3
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r3 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r3 = r3.parent
                int r3 = r3.level
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r3 = r3 * r4
                int r0 = r0 + r3
                r9.textX = r0
            L_0x0131:
                r9.verticalAlign = r13
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r0 = r10 - r0
                int r3 = r9.textX
                int r0 = r0 - r3
                org.telegram.ui.ArticleViewer$WebpageAdapter r3 = r9.parentAdapter
                boolean r3 = r3.isRtl
                if (r3 == 0) goto L_0x0167
                r3 = 1086324736(0x40CLASSNAME, float:6.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r4 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r4 = r4.parent
                int r4 = r4.maxNumWidth
                int r3 = r3 + r4
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r4 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r4 = r4.parent
                int r4 = r4.level
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r4 = r4 * r1
                int r3 = r3 + r4
                int r0 = r0 - r3
            L_0x0167:
                r4 = r0
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.tgnet.TLRPC$RichText r0 = r0.textItem
                r14 = 1090519040(0x41000000, float:8.0)
                if (r0 == 0) goto L_0x01d5
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r1 = r9.currentBlock
                org.telegram.tgnet.TLRPC$RichText r3 = r1.textItem
                int r5 = r9.textY
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r6 = r9.currentBlock
                org.telegram.ui.ArticleViewer$WebpageAdapter r1 = r9.parentAdapter
                boolean r1 = r1.isRtl
                if (r1 == 0) goto L_0x018c
                android.text.Layout$Alignment r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT()
                goto L_0x018e
            L_0x018c:
                android.text.Layout$Alignment r1 = android.text.Layout.Alignment.ALIGN_NORMAL
            L_0x018e:
                r7 = r1
                org.telegram.ui.ArticleViewer$WebpageAdapter r8 = r9.parentAdapter
                r1 = r16
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8)
                r9.textLayout = r0
                if (r0 == 0) goto L_0x0320
                int r0 = r0.getLineCount()
                if (r0 <= 0) goto L_0x0320
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.numLayout
                if (r0 == 0) goto L_0x01c8
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.numLayout
                int r0 = r0.getLineCount()
                if (r0 <= 0) goto L_0x01c8
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.textLayout
                int r0 = r0.getLineAscent(r13)
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r1 = r1.numLayout
                int r1 = r1.getLineAscent(r13)
                int r1 = r1 - r0
                r9.numOffsetY = r1
            L_0x01c8:
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.textLayout
                int r0 = r0.getHeight()
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r0 = r0 + r1
                goto L_0x031f
            L_0x01d5:
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.tgnet.TLRPC$PageBlock r0 = r0.blockItem
                if (r0 == 0) goto L_0x0320
                int r0 = r9.textX
                r9.blockX = r0
                int r0 = r9.textY
                r9.blockY = r0
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                if (r1 == 0) goto L_0x031b
                android.view.View r1 = r1.itemView
                boolean r3 = r1 instanceof org.telegram.ui.ArticleViewer.BlockParagraphCell
                if (r3 == 0) goto L_0x0213
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r0 = r0 - r1
                r9.blockY = r0
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r9.parentAdapter
                boolean r0 = r0.isRtl
                if (r0 != 0) goto L_0x0207
                int r0 = r9.blockX
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r0 = r0 - r1
                r9.blockX = r0
            L_0x0207:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r4 = r4 + r0
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r0 = 0 - r0
                goto L_0x026f
            L_0x0213:
                boolean r0 = r1 instanceof org.telegram.ui.ArticleViewer.BlockHeaderCell
                if (r0 != 0) goto L_0x0258
                boolean r0 = r1 instanceof org.telegram.ui.ArticleViewer.BlockSubheaderCell
                if (r0 != 0) goto L_0x0258
                boolean r0 = r1 instanceof org.telegram.ui.ArticleViewer.BlockTitleCell
                if (r0 != 0) goto L_0x0258
                boolean r0 = r1 instanceof org.telegram.ui.ArticleViewer.BlockSubtitleCell
                if (r0 == 0) goto L_0x0224
                goto L_0x0258
            L_0x0224:
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r1 = r9.currentBlock
                org.telegram.tgnet.TLRPC$PageBlock r1 = r1.blockItem
                boolean r0 = r0.isListItemBlock(r1)
                if (r0 == 0) goto L_0x0240
                r9.blockX = r13
                r9.blockY = r13
                r9.textY = r13
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r0 = 0 - r0
                r4 = r10
                goto L_0x026f
            L_0x0240:
                androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r9.blockLayout
                android.view.View r0 = r0.itemView
                boolean r0 = r0 instanceof org.telegram.ui.ArticleViewer.BlockTableCell
                if (r0 == 0) goto L_0x026e
                int r0 = r9.blockX
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r0 = r0 - r1
                r9.blockX = r0
                r0 = 1108344832(0x42100000, float:36.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                goto L_0x026d
            L_0x0258:
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r9.parentAdapter
                boolean r0 = r0.isRtl
                if (r0 != 0) goto L_0x0269
                int r0 = r9.blockX
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r0 = r0 - r1
                r9.blockX = r0
            L_0x0269:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            L_0x026d:
                int r4 = r4 + r0
            L_0x026e:
                r0 = 0
            L_0x026f:
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                android.view.View r1 = r1.itemView
                r2 = 1073741824(0x40000000, float:2.0)
                int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r2)
                int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r13)
                r1.measure(r2, r3)
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                android.view.View r1 = r1.itemView
                boolean r1 = r1 instanceof org.telegram.ui.ArticleViewer.BlockParagraphCell
                if (r1 == 0) goto L_0x02bf
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r1 = r1.numLayout
                if (r1 == 0) goto L_0x02bf
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r1 = r1.numLayout
                int r1 = r1.getLineCount()
                if (r1 <= 0) goto L_0x02bf
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                android.view.View r1 = r1.itemView
                org.telegram.ui.ArticleViewer$BlockParagraphCell r1 = (org.telegram.ui.ArticleViewer.BlockParagraphCell) r1
                org.telegram.ui.ArticleViewer$DrawingText r2 = r1.textLayout
                if (r2 == 0) goto L_0x02bf
                int r2 = r2.getLineCount()
                if (r2 <= 0) goto L_0x02bf
                org.telegram.ui.ArticleViewer$DrawingText r1 = r1.textLayout
                int r1 = r1.getLineAscent(r13)
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r2 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r2 = r2.numLayout
                int r2 = r2.getLineAscent(r13)
                int r2 = r2 - r1
                r9.numOffsetY = r2
            L_0x02bf:
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r1 = r9.currentBlock
                org.telegram.tgnet.TLRPC$PageBlock r1 = r1.blockItem
                boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockDetails
                if (r1 == 0) goto L_0x02d3
                r9.verticalAlign = r11
                r9.blockY = r13
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r0 = r0 - r1
                goto L_0x02ee
            L_0x02d3:
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                android.view.View r1 = r1.itemView
                boolean r2 = r1 instanceof org.telegram.ui.ArticleViewer.BlockOrderedListItemCell
                if (r2 == 0) goto L_0x02e2
                org.telegram.ui.ArticleViewer$BlockOrderedListItemCell r1 = (org.telegram.ui.ArticleViewer.BlockOrderedListItemCell) r1
                boolean r1 = r1.verticalAlign
                r9.verticalAlign = r1
                goto L_0x02ee
            L_0x02e2:
                boolean r2 = r1 instanceof org.telegram.ui.ArticleViewer.BlockListItemCell
                if (r2 == 0) goto L_0x02ee
                org.telegram.ui.ArticleViewer$BlockListItemCell r1 = (org.telegram.ui.ArticleViewer.BlockListItemCell) r1
                boolean r1 = r1.verticalAlign
                r9.verticalAlign = r1
            L_0x02ee:
                boolean r1 = r9.verticalAlign
                if (r1 == 0) goto L_0x0311
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r1 = r1.numLayout
                if (r1 == 0) goto L_0x0311
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                android.view.View r1 = r1.itemView
                int r1 = r1.getMeasuredHeight()
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r2 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r2 = r2.numLayout
                int r2 = r2.getHeight()
                int r1 = r1 - r2
                int r1 = r1 / 2
                r9.textY = r1
            L_0x0311:
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.blockLayout
                android.view.View r1 = r1.itemView
                int r1 = r1.getMeasuredHeight()
                int r13 = r0 + r1
            L_0x031b:
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            L_0x031f:
                int r13 = r13 + r0
            L_0x0320:
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r0 = r0.parent
                java.util.ArrayList r0 = r0.items
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r1 = r1.parent
                java.util.ArrayList r1 = r1.items
                int r1 = r1.size()
                int r1 = r1 - r11
                java.lang.Object r0 = r0.get(r1)
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r1 = r9.currentBlock
                if (r0 != r1) goto L_0x0346
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r13 = r13 + r0
            L_0x0346:
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                int r0 = r0.index
                if (r0 != 0) goto L_0x035f
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r0 = r0.parent
                int r0 = r0.level
                if (r0 != 0) goto L_0x035f
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r13 = r13 + r0
            L_0x035f:
                r11 = r13
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.textLayout
                if (r0 == 0) goto L_0x0384
                int r1 = r9.textX
                r0.x = r1
                int r1 = r9.textY
                r0.y = r1
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r0 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.numLayout
                if (r0 == 0) goto L_0x0384
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.textLayout
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r1 = r9.currentBlock
                org.telegram.ui.ArticleViewer$DrawingText r1 = r1.numLayout
                android.text.StaticLayout r1 = r1.textLayout
                java.lang.CharSequence r1 = r1.getText()
                r0.prefix = r1
            L_0x0384:
                androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r9.blockLayout
                if (r0 == 0) goto L_0x03d1
                android.view.View r0 = r0.itemView
                boolean r0 = r0 instanceof org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView
                if (r0 == 0) goto L_0x03d1
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r0 = r0.textSelectionHelper
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r0 = r0.arrayList
                r0.clear()
                androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r9.blockLayout
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.TextSelectionHelper$ArticleSelectableView r0 = (org.telegram.ui.Cells.TextSelectionHelper.ArticleSelectableView) r0
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r1 = r1.textSelectionHelper
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r1 = r1.arrayList
                r0.fillTextLayoutBlocks(r1)
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r0 = r0.textSelectionHelper
                java.util.ArrayList<org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock> r0 = r0.arrayList
                java.util.Iterator r0 = r0.iterator()
            L_0x03b0:
                boolean r1 = r0.hasNext()
                if (r1 == 0) goto L_0x03d1
                java.lang.Object r1 = r0.next()
                org.telegram.ui.Cells.TextSelectionHelper$TextLayoutBlock r1 = (org.telegram.ui.Cells.TextSelectionHelper.TextLayoutBlock) r1
                boolean r2 = r1 instanceof org.telegram.ui.ArticleViewer.DrawingText
                if (r2 == 0) goto L_0x03b0
                org.telegram.ui.ArticleViewer$DrawingText r1 = (org.telegram.ui.ArticleViewer.DrawingText) r1
                int r2 = r1.x
                int r3 = r9.blockX
                int r2 = r2 + r3
                r1.x = r2
                int r2 = r1.y
                int r3 = r9.blockY
                int r2 = r2 + r3
                r1.y = r2
                goto L_0x03b0
            L_0x03d1:
                r9.setMeasuredDimension(r10, r11)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockOrderedListItemCell.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                int i5 = this.blockX;
                view.layout(i5, this.blockY, view.getMeasuredWidth() + i5, this.blockY + this.blockLayout.itemView.getMeasuredHeight());
            }
        }

        /* access modifiers changed from: protected */
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
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                if (view instanceof TextSelectionHelper.ArticleSelectableView) {
                    ((TextSelectionHelper.ArticleSelectableView) view).fillTextLayoutBlocks(arrayList);
                }
            }
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockDetailsCell extends View implements Drawable.Callback, TextSelectionHelper.ArticleSelectableView {
        /* access modifiers changed from: private */
        public AnimatedArrowDrawable arrow;
        private TLRPC$TL_pageBlockDetails currentBlock;
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
            this.arrow = new AnimatedArrowDrawable(ArticleViewer.getGrayTextColor(), true);
        }

        public void invalidateDrawable(Drawable drawable) {
            invalidate();
        }

        public void setBlock(TLRPC$TL_pageBlockDetails tLRPC$TL_pageBlockDetails) {
            this.currentBlock = tLRPC$TL_pageBlockDetails;
            this.arrow.setAnimationProgress(tLRPC$TL_pageBlockDetails.open ? 0.0f : 1.0f);
            this.arrow.setCallback(this);
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            int dp = AndroidUtilities.dp(39.0f);
            TLRPC$TL_pageBlockDetails tLRPC$TL_pageBlockDetails = this.currentBlock;
            if (tLRPC$TL_pageBlockDetails != null) {
                DrawingText access$13400 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockDetails.title, size - AndroidUtilities.dp(52.0f), 0, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13400;
                if (access$13400 != null) {
                    dp = Math.max(dp, AndroidUtilities.dp(21.0f) + this.textLayout.getHeight());
                    int height = ((this.textLayout.getHeight() + AndroidUtilities.dp(21.0f)) - this.textLayout.getHeight()) / 2;
                    this.textY = height;
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = height;
                }
            }
            setMeasuredDimension(size, dp + 1);
        }

        /* access modifiers changed from: protected */
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private static class BlockDetailsBottomCell extends View {
        public BlockDetailsBottomCell(Context context) {
            super(context);
            new RectF();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(4.0f) + 1);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            canvas.drawLine(0.0f, 0.0f, (float) getMeasuredWidth(), 0.0f, ArticleViewer.dividerPaint);
        }
    }

    private static class BlockRelatedArticlesShadowCell extends View {
        private CombinedDrawable shadowDrawable;

        public BlockRelatedArticlesShadowCell(Context context) {
            super(context);
            CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(context, NUM, -16777216));
            this.shadowDrawable = combinedDrawable;
            combinedDrawable.setFullsize(true);
            setBackgroundDrawable(this.shadowDrawable);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(12.0f));
            Theme.setCombinedDrawableColor(this.shadowDrawable, Theme.getColor("windowBackgroundGray"), false);
        }
    }

    private class BlockRelatedArticlesHeaderCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC$TL_pageBlockRelatedArticles currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockRelatedArticlesHeaderCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TLRPC$TL_pageBlockRelatedArticles tLRPC$TL_pageBlockRelatedArticles) {
            this.currentBlock = tLRPC$TL_pageBlockRelatedArticles;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            TLRPC$TL_pageBlockRelatedArticles tLRPC$TL_pageBlockRelatedArticles = this.currentBlock;
            if (tLRPC$TL_pageBlockRelatedArticles != null) {
                DrawingText access$14300 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockRelatedArticles.title, size - AndroidUtilities.dp(52.0f), 0, this.currentBlock, Layout.Alignment.ALIGN_NORMAL, 1, this.parentAdapter);
                this.textLayout = access$14300;
                if (access$14300 != null) {
                    this.textY = AndroidUtilities.dp(6.0f) + ((AndroidUtilities.dp(32.0f) - this.textLayout.getHeight()) / 2);
                }
            }
            if (this.textLayout != null) {
                setMeasuredDimension(size, AndroidUtilities.dp(38.0f));
                DrawingText drawingText = this.textLayout;
                drawingText.x = this.textX;
                drawingText.y = this.textY;
                return;
            }
            setMeasuredDimension(size, 1);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockRelatedArticlesCell extends View implements TextSelectionHelper.ArticleSelectableView {
        /* access modifiers changed from: private */
        public TL_pageBlockRelatedArticlesChild currentBlock;
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
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.imageView = imageReceiver;
            imageReceiver.setRoundRadius(AndroidUtilities.dp(6.0f));
        }

        public void setBlock(TL_pageBlockRelatedArticlesChild tL_pageBlockRelatedArticlesChild) {
            this.currentBlock = tL_pageBlockRelatedArticlesChild;
            requestLayout();
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"DrawAllocation", "NewApi"})
        public void onMeasure(int i, int i2) {
            int i3;
            int i4;
            int i5;
            boolean z;
            String str;
            int size = View.MeasureSpec.getSize(i);
            this.divider = this.currentBlock.num != this.currentBlock.parent.articles.size() - 1;
            TLRPC$TL_pageRelatedArticle tLRPC$TL_pageRelatedArticle = this.currentBlock.parent.articles.get(this.currentBlock.num);
            int dp = AndroidUtilities.dp((float) (SharedConfig.ivFontSize - 16));
            long j = tLRPC$TL_pageRelatedArticle.photo_id;
            TLRPC$PhotoSize tLRPC$PhotoSize = null;
            TLRPC$Photo access$14200 = j != 0 ? this.parentAdapter.getPhotoWithId(j) : null;
            if (access$14200 != null) {
                this.drawImage = true;
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(access$14200.sizes, AndroidUtilities.getPhotoSize());
                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(access$14200.sizes, 80, true);
                if (closestPhotoSizeWithSize != closestPhotoSizeWithSize2) {
                    tLRPC$PhotoSize = closestPhotoSizeWithSize2;
                }
                this.imageView.setImage(ImageLocation.getForPhoto(closestPhotoSizeWithSize, access$14200), "64_64", ImageLocation.getForPhoto(tLRPC$PhotoSize, access$14200), "64_64_b", closestPhotoSizeWithSize.size, (String) null, this.parentAdapter.currentPage, 1);
            } else {
                this.drawImage = false;
            }
            int dp2 = AndroidUtilities.dp(60.0f);
            int dp3 = size - AndroidUtilities.dp(36.0f);
            if (this.drawImage) {
                int dp4 = AndroidUtilities.dp(44.0f);
                float f = (float) dp4;
                this.imageView.setImageCoords((float) ((size - dp4) - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(8.0f), f, f);
                dp3 = (int) (((float) dp3) - (this.imageView.getImageWidth() + ((float) AndroidUtilities.dp(6.0f))));
            }
            int i6 = dp3;
            int dp5 = AndroidUtilities.dp(18.0f);
            String str2 = tLRPC$TL_pageRelatedArticle.title;
            if (str2 != null) {
                i3 = dp2;
                this.textLayout = ArticleViewer.this.createLayoutForText(this, str2, (TLRPC$RichText) null, i6, this.textY, this.currentBlock, Layout.Alignment.ALIGN_NORMAL, 3, this.parentAdapter);
            } else {
                i3 = dp2;
            }
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                int lineCount = drawingText.getLineCount();
                int i7 = 4 - lineCount;
                this.textOffset = this.textLayout.getHeight() + AndroidUtilities.dp(6.0f) + dp;
                dp5 += this.textLayout.getHeight();
                int i8 = 0;
                while (true) {
                    if (i8 >= lineCount) {
                        z = false;
                        break;
                    } else if (this.textLayout.getLineLeft(i8) != 0.0f) {
                        z = true;
                        break;
                    } else {
                        i8++;
                    }
                }
                DrawingText drawingText2 = this.textLayout;
                drawingText2.x = this.textX;
                drawingText2.y = this.textY;
                i5 = i7;
            } else {
                this.textOffset = 0;
                z = false;
                i5 = 4;
            }
            if (tLRPC$TL_pageRelatedArticle.published_date != 0 && !TextUtils.isEmpty(tLRPC$TL_pageRelatedArticle.author)) {
                str = LocaleController.formatString("ArticleDateByAuthor", NUM, LocaleController.getInstance().chatFullDate.format(((long) tLRPC$TL_pageRelatedArticle.published_date) * 1000), tLRPC$TL_pageRelatedArticle.author);
            } else if (!TextUtils.isEmpty(tLRPC$TL_pageRelatedArticle.author)) {
                str = LocaleController.formatString("ArticleByAuthor", NUM, tLRPC$TL_pageRelatedArticle.author);
            } else if (tLRPC$TL_pageRelatedArticle.published_date != 0) {
                str = LocaleController.getInstance().chatFullDate.format(((long) tLRPC$TL_pageRelatedArticle.published_date) * 1000);
            } else if (!TextUtils.isEmpty(tLRPC$TL_pageRelatedArticle.description)) {
                str = tLRPC$TL_pageRelatedArticle.description;
            } else {
                str = tLRPC$TL_pageRelatedArticle.url;
            }
            DrawingText access$14300 = ArticleViewer.this.createLayoutForText(this, str, (TLRPC$RichText) null, i6, this.textOffset + this.textY, this.currentBlock, (this.parentAdapter.isRtl || z) ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, i5, this.parentAdapter);
            this.textLayout2 = access$14300;
            if (access$14300 != null) {
                i4 += access$14300.getHeight();
                if (this.textLayout != null) {
                    i4 += AndroidUtilities.dp(6.0f) + dp;
                }
                DrawingText drawingText3 = this.textLayout2;
                drawingText3.x = this.textX;
                drawingText3.y = this.textY + this.textOffset;
            }
            setMeasuredDimension(size, Math.max(i3, i4) + (this.divider ? 1 : 0));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            if (this.currentBlock != null) {
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
                    int measuredWidth = getMeasuredWidth();
                    if (this.parentAdapter.isRtl) {
                        i2 = AndroidUtilities.dp(17.0f);
                    }
                    canvas.drawLine(dp, measuredHeight, (float) (measuredWidth - i2), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            DrawingText drawingText2 = this.textLayout2;
            if (drawingText2 != null) {
                arrayList.add(drawingText2);
            }
        }
    }

    private class BlockHeaderCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC$TL_pageBlockHeader currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockHeaderCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TLRPC$TL_pageBlockHeader tLRPC$TL_pageBlockHeader) {
            this.currentBlock = tLRPC$TL_pageBlockHeader;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            TLRPC$TL_pageBlockHeader tLRPC$TL_pageBlockHeader = this.currentBlock;
            int i3 = 0;
            if (tLRPC$TL_pageBlockHeader != null) {
                DrawingText access$13400 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockHeader.text, size - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13400;
                if (access$13400 != null) {
                    i3 = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(size, i3);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
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
                accessibilityNodeInfo.setText(this.textLayout.getText() + ", " + LocaleController.getString("AccDescrIVHeading", NUM));
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private static class BlockDividerCell extends View {
        private RectF rect = new RectF();

        public BlockDividerCell(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(18.0f));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int measuredWidth = getMeasuredWidth() / 3;
            this.rect.set((float) measuredWidth, (float) AndroidUtilities.dp(8.0f), (float) (measuredWidth * 2), (float) AndroidUtilities.dp(10.0f));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), ArticleViewer.dividerPaint);
        }
    }

    private class BlockSubtitleCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC$TL_pageBlockSubtitle currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockSubtitleCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TLRPC$TL_pageBlockSubtitle tLRPC$TL_pageBlockSubtitle) {
            this.currentBlock = tLRPC$TL_pageBlockSubtitle;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            TLRPC$TL_pageBlockSubtitle tLRPC$TL_pageBlockSubtitle = this.currentBlock;
            int i3 = 0;
            if (tLRPC$TL_pageBlockSubtitle != null) {
                DrawingText access$13400 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockSubtitle.text, size - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13400;
                if (access$13400 != null) {
                    i3 = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(size, i3);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
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
                accessibilityNodeInfo.setText(this.textLayout.getText() + ", " + LocaleController.getString("AccDescrIVHeading", NUM));
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockPullquoteCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC$TL_pageBlockPullquote currentBlock;
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

        public void setBlock(TLRPC$TL_pageBlockPullquote tLRPC$TL_pageBlockPullquote) {
            this.currentBlock = tLRPC$TL_pageBlockPullquote;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(motionEvent);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int i3;
            int size = View.MeasureSpec.getSize(i);
            TLRPC$TL_pageBlockPullquote tLRPC$TL_pageBlockPullquote = this.currentBlock;
            if (tLRPC$TL_pageBlockPullquote != null) {
                DrawingText access$13300 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockPullquote.text, size - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter);
                this.textLayout = access$13300;
                i3 = 0;
                if (access$13300 != null) {
                    i3 = 0 + AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
                this.textY2 = AndroidUtilities.dp(2.0f) + i3;
                DrawingText access$133002 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption, size - AndroidUtilities.dp(36.0f), this.textY2, this.currentBlock, this.parentAdapter);
                this.textLayout2 = access$133002;
                if (access$133002 != null) {
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
            setMeasuredDimension(size, i3);
        }

        /* access modifiers changed from: protected */
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            DrawingText drawingText2 = this.textLayout2;
            if (drawingText2 != null) {
                arrayList.add(drawingText2);
            }
        }
    }

    private class BlockBlockquoteCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC$TL_pageBlockBlockquote currentBlock;
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

        public void setBlock(TLRPC$TL_pageBlockBlockquote tLRPC$TL_pageBlockBlockquote) {
            this.currentBlock = tLRPC$TL_pageBlockBlockquote;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(motionEvent);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int i3;
            int size = View.MeasureSpec.getSize(i);
            if (this.currentBlock != null) {
                int dp = size - AndroidUtilities.dp(50.0f);
                int i4 = this.currentBlock.level;
                if (i4 > 0) {
                    dp -= AndroidUtilities.dp((float) (i4 * 14));
                }
                ArticleViewer articleViewer = ArticleViewer.this;
                TLRPC$TL_pageBlockBlockquote tLRPC$TL_pageBlockBlockquote = this.currentBlock;
                DrawingText access$13300 = articleViewer.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockBlockquote.text, dp, this.textY, tLRPC$TL_pageBlockBlockquote, this.parentAdapter);
                this.textLayout = access$13300;
                i3 = access$13300 != null ? 0 + AndroidUtilities.dp(8.0f) + this.textLayout.getHeight() : 0;
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
                int dp2 = i3 + AndroidUtilities.dp(8.0f);
                this.textY2 = dp2;
                ArticleViewer articleViewer2 = ArticleViewer.this;
                TLRPC$TL_pageBlockBlockquote tLRPC$TL_pageBlockBlockquote2 = this.currentBlock;
                DrawingText access$133002 = articleViewer2.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockBlockquote2.caption, dp, dp2, tLRPC$TL_pageBlockBlockquote2, this.parentAdapter);
                this.textLayout2 = access$133002;
                if (access$133002 != null) {
                    i3 += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                }
                if (i3 != 0) {
                    i3 += AndroidUtilities.dp(8.0f);
                }
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
                DrawingText drawingText2 = this.textLayout2;
                if (drawingText2 != null) {
                    drawingText2.x = this.textX;
                    drawingText2.y = this.textY2;
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(size, i3);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            if (this.currentBlock != null) {
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
                    int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(20.0f);
                    canvas.drawRect((float) measuredWidth, (float) AndroidUtilities.dp(6.0f), (float) (measuredWidth + AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), ArticleViewer.quoteLinePaint);
                } else {
                    canvas.drawRect((float) AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 18)), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 20)), (float) (getMeasuredHeight() - AndroidUtilities.dp(6.0f)), ArticleViewer.quoteLinePaint);
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            DrawingText drawingText2 = this.textLayout2;
            if (drawingText2 != null) {
                arrayList.add(drawingText2);
            }
        }
    }

    private class BlockPhotoCell extends FrameLayout implements DownloadController.FileDownloadProgressListener, TextSelectionHelper.ArticleSelectableView {
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
        /* access modifiers changed from: private */
        public TLRPC$TL_pageBlockPhoto currentBlock;
        private String currentFilter;
        private TLRPC$Photo currentPhoto;
        private TLRPC$PhotoSize currentPhotoObject;
        private TLRPC$PhotoSize currentPhotoObjectThumb;
        private String currentThumbFilter;
        private int currentType;
        /* access modifiers changed from: private */
        public MessageObject.GroupedMessagePosition groupPosition;
        /* access modifiers changed from: private */
        public ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private Drawable linkDrawable;
        private WebpageAdapter parentAdapter;
        private TLRPC$PageBlock parentBlock;
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
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setProgressColor(-1);
            this.radialProgress.setColors(NUM, NUM, -1, -2500135);
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            addView(this.channelCell, LayoutHelper.createFrame(-1, -2.0f));
            this.currentType = i;
        }

        public void setBlock(TLRPC$TL_pageBlockPhoto tLRPC$TL_pageBlockPhoto, boolean z, boolean z2) {
            this.parentBlock = null;
            this.currentBlock = tLRPC$TL_pageBlockPhoto;
            this.isFirst = z;
            this.channelCell.setVisibility(4);
            if (!TextUtils.isEmpty(this.currentBlock.url)) {
                this.linkDrawable = getResources().getDrawable(NUM);
            }
            TLRPC$TL_pageBlockPhoto tLRPC$TL_pageBlockPhoto2 = this.currentBlock;
            if (tLRPC$TL_pageBlockPhoto2 != null) {
                TLRPC$Photo access$14200 = this.parentAdapter.getPhotoWithId(tLRPC$TL_pageBlockPhoto2.photo_id);
                if (access$14200 != null) {
                    this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(access$14200.sizes, AndroidUtilities.getPhotoSize());
                } else {
                    this.currentPhotoObject = null;
                }
            } else {
                this.currentPhotoObject = null;
            }
            updateButtonState(false);
            requestLayout();
        }

        public void setParentBlock(TLRPC$PageBlock tLRPC$PageBlock) {
            this.parentBlock = tLRPC$PageBlock;
            if (this.parentAdapter.channelBlock != null && (this.parentBlock instanceof TLRPC$TL_pageBlockCover)) {
                this.channelCell.setBlock(this.parentAdapter.channelBlock);
                this.channelCell.setVisibility(0);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a3, code lost:
            if (r2 <= ((float) (r0 + org.telegram.messenger.AndroidUtilities.dp(48.0f)))) goto L_0x00a9;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r13) {
            /*
                r12 = this;
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.PinchToZoomHelper r0 = r0.pinchToZoomHelper
                org.telegram.messenger.ImageReceiver r1 = r12.imageView
                r2 = 0
                boolean r0 = r0.checkPinchToZoom(r13, r12, r1, r2)
                r1 = 1
                if (r0 == 0) goto L_0x000f
                return r1
            L_0x000f:
                float r0 = r13.getX()
                float r2 = r13.getY()
                org.telegram.ui.ArticleViewer$BlockChannelCell r3 = r12.channelCell
                int r3 = r3.getVisibility()
                r4 = 0
                if (r3 != 0) goto L_0x006e
                org.telegram.ui.ArticleViewer$BlockChannelCell r3 = r12.channelCell
                float r3 = r3.getTranslationY()
                int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r3 <= 0) goto L_0x006e
                org.telegram.ui.ArticleViewer$BlockChannelCell r3 = r12.channelCell
                float r3 = r3.getTranslationY()
                r5 = 1109131264(0x421CLASSNAME, float:39.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                float r3 = r3 + r5
                int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r3 >= 0) goto L_0x006e
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r12.parentAdapter
                org.telegram.tgnet.TLRPC$TL_pageBlockChannel r0 = r0.channelBlock
                if (r0 == 0) goto L_0x006d
                int r13 = r13.getAction()
                if (r13 != r1) goto L_0x006d
                org.telegram.ui.ArticleViewer r13 = org.telegram.ui.ArticleViewer.this
                int r13 = r13.currentAccount
                org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r12.parentAdapter
                org.telegram.tgnet.TLRPC$TL_pageBlockChannel r0 = r0.channelBlock
                org.telegram.tgnet.TLRPC$Chat r0 = r0.channel
                java.lang.String r0 = r0.username
                org.telegram.ui.ArticleViewer r2 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ActionBar.BaseFragment r2 = r2.parentFragment
                r3 = 2
                r13.openByUserName(r0, r2, r3)
                org.telegram.ui.ArticleViewer r13 = org.telegram.ui.ArticleViewer.this
                r13.close(r4, r1)
            L_0x006d:
                return r1
            L_0x006e:
                int r3 = r13.getAction()
                if (r3 != 0) goto L_0x00b2
                org.telegram.messenger.ImageReceiver r3 = r12.imageView
                boolean r3 = r3.isInsideImage(r0, r2)
                if (r3 == 0) goto L_0x00b2
                int r3 = r12.buttonState
                r5 = -1
                if (r3 == r5) goto L_0x00a5
                int r3 = r12.buttonX
                float r5 = (float) r3
                int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r5 < 0) goto L_0x00a5
                r5 = 1111490560(0x42400000, float:48.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r3 = r3 + r6
                float r3 = (float) r3
                int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
                if (r0 > 0) goto L_0x00a5
                int r0 = r12.buttonY
                float r3 = (float) r0
                int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r3 < 0) goto L_0x00a5
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r0 = r0 + r3
                float r0 = (float) r0
                int r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r0 <= 0) goto L_0x00a9
            L_0x00a5:
                int r0 = r12.buttonState
                if (r0 != 0) goto L_0x00af
            L_0x00a9:
                r12.buttonPressed = r1
                r12.invalidate()
                goto L_0x00e3
            L_0x00af:
                r12.photoPressed = r1
                goto L_0x00e3
            L_0x00b2:
                int r0 = r13.getAction()
                if (r0 != r1) goto L_0x00d8
                boolean r0 = r12.photoPressed
                if (r0 == 0) goto L_0x00c8
                r12.photoPressed = r4
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r2 = r12.currentBlock
                org.telegram.ui.ArticleViewer$WebpageAdapter r3 = r12.parentAdapter
                r0.openPhoto(r2, r3)
                goto L_0x00e3
            L_0x00c8:
                int r0 = r12.buttonPressed
                if (r0 != r1) goto L_0x00e3
                r12.buttonPressed = r4
                r12.playSoundEffect(r4)
                r12.didPressedButton(r1)
                r12.invalidate()
                goto L_0x00e3
            L_0x00d8:
                int r0 = r13.getAction()
                r2 = 3
                if (r0 != r2) goto L_0x00e3
                r12.photoPressed = r4
                r12.buttonPressed = r4
            L_0x00e3:
                boolean r0 = r12.photoPressed
                if (r0 != 0) goto L_0x011b
                int r0 = r12.buttonPressed
                if (r0 != 0) goto L_0x011b
                org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r12.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r9 = r12.captionLayout
                int r10 = r12.textX
                int r11 = r12.textY
                r7 = r13
                r8 = r12
                boolean r0 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10, r11)
                if (r0 != 0) goto L_0x011b
                org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r12.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r9 = r12.creditLayout
                int r10 = r12.textX
                int r0 = r12.textY
                int r2 = r12.creditOffset
                int r11 = r0 + r2
                r7 = r13
                r8 = r12
                boolean r0 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10, r11)
                if (r0 != 0) goto L_0x011b
                boolean r13 = super.onTouchEvent(r13)
                if (r13 == 0) goto L_0x011a
                goto L_0x011b
            L_0x011a:
                r1 = 0
            L_0x011b:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockPhotoCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x0157  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x015a  */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x0187  */
        /* JADX WARNING: Removed duplicated region for block: B:62:0x0189  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x019d  */
        /* JADX WARNING: Removed duplicated region for block: B:68:0x01d4  */
        /* JADX WARNING: Removed duplicated region for block: B:9:0x0047  */
        @android.annotation.SuppressLint({"NewApi"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r28, int r29) {
            /*
                r27 = this;
                r10 = r27
                int r0 = android.view.View.MeasureSpec.getSize(r28)
                int r1 = r10.currentType
                r11 = 0
                r12 = 2
                r13 = 1
                if (r1 != r13) goto L_0x0023
                android.view.ViewParent r0 = r27.getParent()
                android.view.View r0 = (android.view.View) r0
                int r0 = r0.getMeasuredWidth()
                android.view.ViewParent r1 = r27.getParent()
                android.view.View r1 = (android.view.View) r1
                int r1 = r1.getMeasuredHeight()
            L_0x0021:
                r14 = r0
                goto L_0x0043
            L_0x0023:
                if (r1 != r12) goto L_0x0041
                org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = r10.groupPosition
                float r1 = r1.ph
                android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
                int r3 = r2.x
                int r2 = r2.y
                int r2 = java.lang.Math.max(r3, r2)
                float r2 = (float) r2
                float r1 = r1 * r2
                r2 = 1056964608(0x3var_, float:0.5)
                float r1 = r1 * r2
                double r1 = (double) r1
                double r1 = java.lang.Math.ceil(r1)
                int r1 = (int) r1
                goto L_0x0021
            L_0x0041:
                r14 = r0
                r1 = 0
            L_0x0043:
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r0 = r10.currentBlock
                if (r0 == 0) goto L_0x032e
                org.telegram.ui.ArticleViewer$WebpageAdapter r2 = r10.parentAdapter
                long r3 = r0.photo_id
                org.telegram.tgnet.TLRPC$Photo r0 = r2.getPhotoWithId(r3)
                r10.currentPhoto = r0
                r0 = 1111490560(0x42400000, float:48.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r2 = r10.currentType
                r3 = 1099956224(0x41900000, float:18.0)
                if (r2 != 0) goto L_0x007a
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r2 = r10.currentBlock
                int r2 = r2.level
                if (r2 <= 0) goto L_0x007a
                int r2 = r2 * 14
                float r2 = (float) r2
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r2 = r2 + r4
                r10.textX = r2
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = r3 + r2
                int r3 = r14 - r3
                r8 = r3
                goto L_0x008b
            L_0x007a:
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r10.textX = r2
                r2 = 1108344832(0x42100000, float:36.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = r14 - r2
                r8 = r2
                r3 = r14
                r2 = 0
            L_0x008b:
                org.telegram.tgnet.TLRPC$Photo r4 = r10.currentPhoto
                r15 = 1090519040(0x41000000, float:8.0)
                if (r4 == 0) goto L_0x023b
                org.telegram.tgnet.TLRPC$PhotoSize r5 = r10.currentPhotoObject
                if (r5 == 0) goto L_0x023b
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r4.sizes
                r5 = 40
                org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5, r13)
                r10.currentPhotoObjectThumb = r4
                org.telegram.tgnet.TLRPC$PhotoSize r5 = r10.currentPhotoObject
                r6 = 0
                if (r5 != r4) goto L_0x00a6
                r10.currentPhotoObjectThumb = r6
            L_0x00a6:
                int r4 = r10.currentType
                r7 = 1073741824(0x40000000, float:2.0)
                if (r4 != 0) goto L_0x00f2
                float r1 = (float) r3
                int r4 = r5.w
                float r4 = (float) r4
                float r1 = r1 / r4
                int r4 = r5.h
                float r4 = (float) r4
                float r1 = r1 * r4
                int r1 = (int) r1
                org.telegram.tgnet.TLRPC$PageBlock r4 = r10.parentBlock
                boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockCover
                if (r4 == 0) goto L_0x00c2
                int r1 = java.lang.Math.min(r1, r3)
                goto L_0x00ef
            L_0x00c2:
                android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
                int r5 = r4.x
                int r4 = r4.y
                int r4 = java.lang.Math.max(r5, r4)
                r5 = 1113587712(0x42600000, float:56.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r4 = r4 - r5
                float r4 = (float) r4
                r5 = 1063675494(0x3var_, float:0.9)
                float r4 = r4 * r5
                int r4 = (int) r4
                if (r1 <= r4) goto L_0x00ef
                float r1 = (float) r4
                org.telegram.tgnet.TLRPC$PhotoSize r3 = r10.currentPhotoObject
                int r5 = r3.h
                float r5 = (float) r5
                float r1 = r1 / r5
                int r3 = r3.w
                float r3 = (float) r3
                float r1 = r1 * r3
                int r3 = (int) r1
                int r1 = r14 - r2
                int r1 = r1 - r3
                int r1 = r1 / r12
                int r2 = r2 + r1
                r1 = r4
            L_0x00ef:
                r4 = r3
                r9 = r8
                goto L_0x0131
            L_0x00f2:
                if (r4 != r12) goto L_0x012f
                org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = r10.groupPosition
                int r4 = r4.flags
                r4 = r4 & r12
                if (r4 != 0) goto L_0x0100
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r3 = r3 - r4
            L_0x0100:
                org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = r10.groupPosition
                int r4 = r4.flags
                r4 = r4 & 8
                if (r4 != 0) goto L_0x010f
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r4 = r1 - r4
                goto L_0x0110
            L_0x010f:
                r4 = r1
            L_0x0110:
                org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = r10.groupPosition
                int r5 = r5.leftSpanOffset
                if (r5 == 0) goto L_0x0126
                int r5 = r5 * r14
                float r5 = (float) r5
                r9 = 1148846080(0x447a0000, float:1000.0)
                float r5 = r5 / r9
                r9 = r8
                double r7 = (double) r5
                double r7 = java.lang.Math.ceil(r7)
                int r5 = (int) r7
                int r3 = r3 - r5
                int r2 = r2 + r5
                goto L_0x0127
            L_0x0126:
                r9 = r8
            L_0x0127:
                r26 = r2
                r2 = r1
                r1 = r4
                r4 = r3
                r3 = r26
                goto L_0x0133
            L_0x012f:
                r9 = r8
                r4 = r3
            L_0x0131:
                r3 = r2
                r2 = r1
            L_0x0133:
                org.telegram.messenger.ImageReceiver r5 = r10.imageView
                float r3 = (float) r3
                boolean r7 = r10.isFirst
                if (r7 != 0) goto L_0x014d
                int r7 = r10.currentType
                if (r7 == r13) goto L_0x014d
                if (r7 == r12) goto L_0x014d
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r7 = r10.currentBlock
                int r7 = r7.level
                if (r7 <= 0) goto L_0x0147
                goto L_0x014d
            L_0x0147:
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r15)
                float r7 = (float) r7
                goto L_0x014e
            L_0x014d:
                r7 = 0
            L_0x014e:
                float r8 = (float) r4
                float r15 = (float) r1
                r5.setImageCoords(r3, r7, r8, r15)
                int r3 = r10.currentType
                if (r3 != 0) goto L_0x015a
                r10.currentFilter = r6
                goto L_0x0172
            L_0x015a:
                java.util.Locale r3 = java.util.Locale.US
                java.lang.Object[] r5 = new java.lang.Object[r12]
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r5[r11] = r4
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                r5[r13] = r1
                java.lang.String r1 = "%d_%d"
                java.lang.String r1 = java.lang.String.format(r3, r1, r5)
                r10.currentFilter = r1
            L_0x0172:
                java.lang.String r1 = "80_80_b"
                r10.currentThumbFilter = r1
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                int r1 = r1.currentAccount
                org.telegram.messenger.DownloadController r1 = org.telegram.messenger.DownloadController.getInstance(r1)
                int r1 = r1.getCurrentDownloadMask()
                r1 = r1 & r13
                if (r1 == 0) goto L_0x0189
                r1 = 1
                goto L_0x018a
            L_0x0189:
                r1 = 0
            L_0x018a:
                r10.autoDownload = r1
                org.telegram.tgnet.TLRPC$PhotoSize r1 = r10.currentPhotoObject
                java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r13)
                boolean r3 = r10.autoDownload
                if (r3 != 0) goto L_0x01d4
                boolean r1 = r1.exists()
                if (r1 == 0) goto L_0x019d
                goto L_0x01d4
            L_0x019d:
                org.telegram.messenger.ImageReceiver r1 = r10.imageView
                org.telegram.tgnet.TLRPC$PhotoSize r3 = r10.currentPhotoObject
                org.telegram.tgnet.TLRPC$Photo r4 = r10.currentPhoto
                org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r3, (org.telegram.tgnet.TLRPC$Photo) r4)
                r1.setStrippedLocation(r3)
                org.telegram.messenger.ImageReceiver r1 = r10.imageView
                r18 = 0
                java.lang.String r3 = r10.currentFilter
                org.telegram.tgnet.TLRPC$PhotoSize r4 = r10.currentPhotoObjectThumb
                org.telegram.tgnet.TLRPC$Photo r5 = r10.currentPhoto
                org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r4, (org.telegram.tgnet.TLRPC$Photo) r5)
                java.lang.String r4 = r10.currentThumbFilter
                org.telegram.tgnet.TLRPC$PhotoSize r5 = r10.currentPhotoObject
                int r5 = r5.size
                r23 = 0
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r10.parentAdapter
                org.telegram.tgnet.TLRPC$WebPage r24 = r6.currentPage
                r25 = 1
                r17 = r1
                r19 = r3
                r21 = r4
                r22 = r5
                r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25)
                goto L_0x0208
            L_0x01d4:
                org.telegram.messenger.ImageReceiver r1 = r10.imageView
                r1.setStrippedLocation(r6)
                org.telegram.messenger.ImageReceiver r1 = r10.imageView
                org.telegram.tgnet.TLRPC$PhotoSize r3 = r10.currentPhotoObject
                org.telegram.tgnet.TLRPC$Photo r4 = r10.currentPhoto
                org.telegram.messenger.ImageLocation r18 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r3, (org.telegram.tgnet.TLRPC$Photo) r4)
                java.lang.String r3 = r10.currentFilter
                org.telegram.tgnet.TLRPC$PhotoSize r4 = r10.currentPhotoObjectThumb
                org.telegram.tgnet.TLRPC$Photo r5 = r10.currentPhoto
                org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r4, (org.telegram.tgnet.TLRPC$Photo) r5)
                java.lang.String r4 = r10.currentThumbFilter
                org.telegram.tgnet.TLRPC$PhotoSize r5 = r10.currentPhotoObject
                int r5 = r5.size
                r23 = 0
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r10.parentAdapter
                org.telegram.tgnet.TLRPC$WebPage r24 = r6.currentPage
                r25 = 1
                r17 = r1
                r19 = r3
                r21 = r4
                r22 = r5
                r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25)
            L_0x0208:
                org.telegram.messenger.ImageReceiver r1 = r10.imageView
                float r1 = r1.getImageX()
                org.telegram.messenger.ImageReceiver r3 = r10.imageView
                float r3 = r3.getImageWidth()
                float r4 = (float) r0
                float r3 = r3 - r4
                r5 = 1073741824(0x40000000, float:2.0)
                float r3 = r3 / r5
                float r1 = r1 + r3
                int r1 = (int) r1
                r10.buttonX = r1
                org.telegram.messenger.ImageReceiver r1 = r10.imageView
                float r1 = r1.getImageY()
                org.telegram.messenger.ImageReceiver r3 = r10.imageView
                float r3 = r3.getImageHeight()
                float r3 = r3 - r4
                float r3 = r3 / r5
                float r1 = r1 + r3
                int r1 = (int) r1
                r10.buttonY = r1
                org.telegram.ui.Components.RadialProgress2 r3 = r10.radialProgress
                int r4 = r10.buttonX
                int r5 = r4 + r0
                int r0 = r0 + r1
                r3.setProgressRect(r4, r1, r5, r0)
                r8 = r2
                goto L_0x023d
            L_0x023b:
                r9 = r8
                r8 = r1
            L_0x023d:
                org.telegram.messenger.ImageReceiver r0 = r10.imageView
                float r0 = r0.getImageY()
                org.telegram.messenger.ImageReceiver r1 = r10.imageView
                float r1 = r1.getImageHeight()
                float r0 = r0 + r1
                r1 = 1090519040(0x41000000, float:8.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r2
                float r0 = r0 + r1
                int r5 = (int) r0
                r10.textY = r5
                int r0 = r10.currentType
                if (r0 != 0) goto L_0x02c7
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r6 = r10.currentBlock
                org.telegram.tgnet.TLRPC$TL_pageCaption r1 = r6.caption
                org.telegram.tgnet.TLRPC$RichText r3 = r1.text
                org.telegram.ui.ArticleViewer$WebpageAdapter r7 = r10.parentAdapter
                r1 = r27
                r4 = r9
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7)
                r10.captionLayout = r0
                r15 = 1082130432(0x40800000, float:4.0)
                if (r0 == 0) goto L_0x0284
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
                org.telegram.ui.ArticleViewer$DrawingText r1 = r10.captionLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                r10.creditOffset = r0
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r0 = r0 + r1
                int r8 = r8 + r0
            L_0x0284:
                r16 = r8
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r6 = r10.currentBlock
                org.telegram.tgnet.TLRPC$TL_pageCaption r1 = r6.caption
                org.telegram.tgnet.TLRPC$RichText r3 = r1.credit
                int r1 = r10.textY
                int r4 = r10.creditOffset
                int r5 = r1 + r4
                org.telegram.ui.ArticleViewer$WebpageAdapter r1 = r10.parentAdapter
                boolean r1 = r1.isRtl
                if (r1 == 0) goto L_0x02a2
                android.text.Layout$Alignment r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT()
                goto L_0x02a4
            L_0x02a2:
                android.text.Layout$Alignment r1 = android.text.Layout.Alignment.ALIGN_NORMAL
            L_0x02a4:
                r7 = r1
                r8 = 0
                org.telegram.ui.ArticleViewer$WebpageAdapter r4 = r10.parentAdapter
                r1 = r27
                r17 = r4
                r4 = r9
                r9 = r17
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8, r9)
                r10.creditLayout = r0
                if (r0 == 0) goto L_0x02c5
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
                org.telegram.ui.ArticleViewer$DrawingText r1 = r10.creditLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                int r8 = r16 + r0
                goto L_0x02c7
            L_0x02c5:
                r8 = r16
            L_0x02c7:
                boolean r0 = r10.isFirst
                if (r0 != 0) goto L_0x02dc
                int r0 = r10.currentType
                if (r0 != 0) goto L_0x02dc
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r0 = r10.currentBlock
                int r0 = r0.level
                if (r0 > 0) goto L_0x02dc
                r0 = 1090519040(0x41000000, float:8.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r8 = r8 + r1
            L_0x02dc:
                org.telegram.tgnet.TLRPC$PageBlock r0 = r10.parentBlock
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockCover
                if (r0 == 0) goto L_0x0305
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r10.parentAdapter
                java.util.ArrayList r0 = r0.blocks
                if (r0 == 0) goto L_0x0305
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r10.parentAdapter
                java.util.ArrayList r0 = r0.blocks
                int r0 = r0.size()
                if (r0 <= r13) goto L_0x0305
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r10.parentAdapter
                java.util.ArrayList r0 = r0.blocks
                java.lang.Object r0 = r0.get(r13)
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockChannel
                if (r0 == 0) goto L_0x0305
                r11 = 1
            L_0x0305:
                int r0 = r10.currentType
                if (r0 == r12) goto L_0x0312
                if (r11 != 0) goto L_0x0312
                r0 = 1090519040(0x41000000, float:8.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r8 = r8 + r0
            L_0x0312:
                r13 = r8
                org.telegram.ui.ArticleViewer$DrawingText r0 = r10.captionLayout
                if (r0 == 0) goto L_0x031f
                int r1 = r10.textX
                r0.x = r1
                int r1 = r10.textY
                r0.y = r1
            L_0x031f:
                org.telegram.ui.ArticleViewer$DrawingText r0 = r10.creditLayout
                if (r0 == 0) goto L_0x032e
                int r1 = r10.textX
                r0.x = r1
                int r1 = r10.textY
                int r2 = r10.creditOffset
                int r1 = r1 + r2
                r0.y = r1
            L_0x032e:
                org.telegram.ui.ArticleViewer$BlockChannelCell r0 = r10.channelCell
                r1 = r28
                r2 = r29
                r0.measure(r1, r2)
                org.telegram.ui.ArticleViewer$BlockChannelCell r0 = r10.channelCell
                org.telegram.messenger.ImageReceiver r1 = r10.imageView
                float r1 = r1.getImageHeight()
                r2 = 1109131264(0x421CLASSNAME, float:39.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                float r1 = r1 - r2
                r0.setTranslationY(r1)
                r10.setMeasuredDimension(r14, r13)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockPhotoCell.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            if (this.currentBlock != null) {
                if (!this.imageView.hasBitmapImage() || this.imageView.getCurrentAlpha() != 1.0f) {
                    canvas.drawRect(this.imageView.getImageX(), this.imageView.getImageY(), this.imageView.getImageX2(), this.imageView.getImageY2(), ArticleViewer.photoBackgroundPaint);
                }
                if (!ArticleViewer.this.pinchToZoomHelper.isInOverlayModeFor(this)) {
                    this.imageView.draw(canvas);
                    if (this.imageView.getVisible()) {
                        this.radialProgress.draw(canvas);
                    }
                }
                if (!TextUtils.isEmpty(this.currentBlock.url)) {
                    int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(35.0f);
                    int imageY = (int) (this.imageView.getImageY() + ((float) AndroidUtilities.dp(11.0f)));
                    this.linkDrawable.setBounds(measuredWidth, imageY, AndroidUtilities.dp(24.0f) + measuredWidth, AndroidUtilities.dp(24.0f) + imageY);
                    this.linkDrawable.draw(canvas);
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
            return i == 1 ? 3 : 4;
        }

        private void didPressedButton(boolean z) {
            int i = this.buttonState;
            if (i == 0) {
                this.radialProgress.setProgress(0.0f, z);
                this.imageView.setImage(ImageLocation.getForPhoto(this.currentPhotoObject, this.currentPhoto), this.currentFilter, ImageLocation.getForPhoto(this.currentPhotoObjectThumb, this.currentPhoto), this.currentThumbFilter, this.currentPhotoObject.size, (String) null, this.parentAdapter.currentPage, 1);
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
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(attachFileName, (MessageObject) null, this);
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
            }
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageView.onDetachedFromWindow();
            DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
        }

        /* access modifiers changed from: protected */
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
            StringBuilder sb = new StringBuilder(LocaleController.getString("AttachPhoto", NUM));
            if (this.captionLayout != null) {
                sb.append(", ");
                sb.append(this.captionLayout.getText());
            }
            accessibilityNodeInfo.setText(sb.toString());
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                arrayList.add(drawingText2);
            }
        }
    }

    private class BlockMapCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC$TL_pageBlockMap currentBlock;
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

        public void setBlock(TLRPC$TL_pageBlockMap tLRPC$TL_pageBlockMap, boolean z, boolean z2) {
            this.currentBlock = tLRPC$TL_pageBlockMap;
            this.isFirst = z;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if (motionEvent.getAction() == 0 && this.imageView.isInsideImage(x, y)) {
                this.photoPressed = true;
            } else if (motionEvent.getAction() == 1 && this.photoPressed) {
                this.photoPressed = false;
                try {
                    TLRPC$GeoPoint tLRPC$GeoPoint = this.currentBlock.geo;
                    double d = tLRPC$GeoPoint.lat;
                    double d2 = tLRPC$GeoPoint._long;
                    Activity access$2000 = ArticleViewer.this.parentActivity;
                    access$2000.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + d + "," + d2 + "?q=" + d + "," + d2)));
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (motionEvent.getAction() == 3) {
                this.photoPressed = false;
            }
            if (!this.photoPressed) {
                if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.captionLayout, this.textX, this.textY)) {
                    if (ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(motionEvent)) {
                        return true;
                    }
                    return false;
                }
            }
            return true;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x00a7, code lost:
            r4 = r9.currentType;
         */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0111  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x012d  */
        /* JADX WARNING: Removed duplicated region for block: B:37:0x0156  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x01e8  */
        @android.annotation.SuppressLint({"NewApi"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r25, int r26) {
            /*
                r24 = this;
                r9 = r24
                int r0 = android.view.View.MeasureSpec.getSize(r25)
                int r1 = r9.currentType
                r2 = 0
                r3 = 1
                r10 = 2
                if (r1 != r3) goto L_0x0024
                android.view.ViewParent r0 = r24.getParent()
                android.view.View r0 = (android.view.View) r0
                int r0 = r0.getMeasuredWidth()
                android.view.ViewParent r1 = r24.getParent()
                android.view.View r1 = (android.view.View) r1
                int r1 = r1.getMeasuredHeight()
                r11 = r0
                r0 = r1
                goto L_0x0029
            L_0x0024:
                r11 = r0
                if (r1 != r10) goto L_0x0028
                goto L_0x0029
            L_0x0028:
                r0 = 0
            L_0x0029:
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r1 = r9.currentBlock
                if (r1 == 0) goto L_0x01ee
                int r4 = r9.currentType
                r5 = 1099956224(0x41900000, float:18.0)
                if (r4 != 0) goto L_0x004e
                int r1 = r1.level
                if (r1 <= 0) goto L_0x004e
                int r1 = r1 * 14
                float r1 = (float) r1
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r2 = r2 + r1
                r9.textX = r2
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r1 = r1 + r2
                int r1 = r11 - r1
                r8 = r1
                goto L_0x005e
            L_0x004e:
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r9.textX = r1
                r1 = 1108344832(0x42100000, float:36.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r1 = r11 - r1
                r8 = r1
                r1 = r11
            L_0x005e:
                int r4 = r9.currentType
                if (r4 != 0) goto L_0x009d
                float r0 = (float) r1
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r4 = r9.currentBlock
                int r5 = r4.w
                float r5 = (float) r5
                float r0 = r0 / r5
                int r4 = r4.h
                float r4 = (float) r4
                float r0 = r0 * r4
                int r0 = (int) r0
                android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
                int r5 = r4.x
                int r4 = r4.y
                int r4 = java.lang.Math.max(r5, r4)
                r5 = 1113587712(0x42600000, float:56.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r4 = r4 - r5
                float r4 = (float) r4
                r5 = 1063675494(0x3var_, float:0.9)
                float r4 = r4 * r5
                int r4 = (int) r4
                if (r0 <= r4) goto L_0x009d
                float r0 = (float) r4
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r1 = r9.currentBlock
                int r5 = r1.h
                float r5 = (float) r5
                float r0 = r0 / r5
                int r1 = r1.w
                float r1 = (float) r1
                float r0 = r0 * r1
                int r1 = (int) r0
                int r0 = r11 - r2
                int r0 = r0 - r1
                int r0 = r0 / r10
                int r2 = r2 + r0
                r12 = r4
                goto L_0x009e
            L_0x009d:
                r12 = r0
            L_0x009e:
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                float r2 = (float) r2
                boolean r4 = r9.isFirst
                r13 = 1090519040(0x41000000, float:8.0)
                if (r4 != 0) goto L_0x00ba
                int r4 = r9.currentType
                if (r4 == r3) goto L_0x00ba
                if (r4 == r10) goto L_0x00ba
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r3 = r9.currentBlock
                int r3 = r3.level
                if (r3 <= 0) goto L_0x00b4
                goto L_0x00ba
            L_0x00b4:
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
                float r3 = (float) r3
                goto L_0x00bb
            L_0x00ba:
                r3 = 0
            L_0x00bb:
                float r1 = (float) r1
                float r4 = (float) r12
                r0.setImageCoords(r2, r3, r1, r4)
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                int r14 = r0.currentAccount
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r0 = r9.currentBlock
                org.telegram.tgnet.TLRPC$GeoPoint r0 = r0.geo
                double r2 = r0.lat
                double r5 = r0._long
                float r0 = org.telegram.messenger.AndroidUtilities.density
                float r7 = r1 / r0
                int r7 = (int) r7
                float r0 = r4 / r0
                int r0 = (int) r0
                r21 = 1
                r22 = 15
                r23 = -1
                r15 = r2
                r17 = r5
                r19 = r7
                r20 = r0
                java.lang.String r16 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r14, r15, r17, r19, r20, r21, r22, r23)
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r0 = r9.currentBlock
                org.telegram.tgnet.TLRPC$GeoPoint r0 = r0.geo
                float r2 = org.telegram.messenger.AndroidUtilities.density
                float r1 = r1 / r2
                int r1 = (int) r1
                float r4 = r4 / r2
                int r3 = (int) r4
                r4 = 15
                double r5 = (double) r2
                double r5 = java.lang.Math.ceil(r5)
                int r2 = (int) r5
                int r2 = java.lang.Math.min(r10, r2)
                org.telegram.messenger.WebFile r0 = org.telegram.messenger.WebFile.createWithGeoPoint(r0, r1, r3, r4, r2)
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                int r1 = r1.currentAccount
                org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
                int r1 = r1.mapProvider
                r9.currentMapProvider = r1
                if (r1 != r10) goto L_0x012d
                if (r0 == 0) goto L_0x013c
                org.telegram.messenger.ImageReceiver r1 = r9.imageView
                org.telegram.messenger.ImageLocation r18 = org.telegram.messenger.ImageLocation.getForWebFile(r0)
                r19 = 0
                r20 = 0
                r21 = 0
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r9.parentAdapter
                org.telegram.tgnet.TLRPC$WebPage r22 = r0.currentPage
                r23 = 0
                r17 = r1
                r17.setImage(r18, r19, r20, r21, r22, r23)
                goto L_0x013c
            L_0x012d:
                if (r16 == 0) goto L_0x013c
                org.telegram.messenger.ImageReceiver r15 = r9.imageView
                r17 = 0
                r18 = 0
                r19 = 0
                r20 = 0
                r15.setImage(r16, r17, r18, r19, r20)
            L_0x013c:
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                float r0 = r0.getImageY()
                org.telegram.messenger.ImageReceiver r1 = r9.imageView
                float r1 = r1.getImageHeight()
                float r0 = r0 + r1
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r13)
                float r1 = (float) r1
                float r0 = r0 + r1
                int r5 = (int) r0
                r9.textY = r5
                int r0 = r9.currentType
                if (r0 != 0) goto L_0x01d1
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r6 = r9.currentBlock
                org.telegram.tgnet.TLRPC$TL_pageCaption r1 = r6.caption
                org.telegram.tgnet.TLRPC$RichText r3 = r1.text
                org.telegram.ui.ArticleViewer$WebpageAdapter r7 = r9.parentAdapter
                r1 = r24
                r4 = r8
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7)
                r9.captionLayout = r0
                r14 = 1082130432(0x40800000, float:4.0)
                if (r0 == 0) goto L_0x018b
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
                org.telegram.ui.ArticleViewer$DrawingText r1 = r9.captionLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                r9.creditOffset = r0
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
                int r0 = r0 + r1
                int r12 = r12 + r0
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.captionLayout
                int r1 = r9.textX
                r0.x = r1
                int r1 = r9.textY
                r0.y = r1
            L_0x018b:
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r6 = r9.currentBlock
                org.telegram.tgnet.TLRPC$TL_pageCaption r1 = r6.caption
                org.telegram.tgnet.TLRPC$RichText r3 = r1.credit
                int r1 = r9.textY
                int r4 = r9.creditOffset
                int r5 = r1 + r4
                org.telegram.ui.ArticleViewer$WebpageAdapter r1 = r9.parentAdapter
                boolean r1 = r1.isRtl
                if (r1 == 0) goto L_0x01a7
                android.text.Layout$Alignment r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT()
                goto L_0x01a9
            L_0x01a7:
                android.text.Layout$Alignment r1 = android.text.Layout.Alignment.ALIGN_NORMAL
            L_0x01a9:
                r7 = r1
                org.telegram.ui.ArticleViewer$WebpageAdapter r15 = r9.parentAdapter
                r1 = r24
                r4 = r8
                r8 = r15
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8)
                r9.creditLayout = r0
                if (r0 == 0) goto L_0x01d1
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
                org.telegram.ui.ArticleViewer$DrawingText r1 = r9.creditLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                int r12 = r12 + r0
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.creditLayout
                int r1 = r9.textX
                r0.x = r1
                int r1 = r9.textY
                int r2 = r9.creditOffset
                int r1 = r1 + r2
                r0.y = r1
            L_0x01d1:
                boolean r0 = r9.isFirst
                if (r0 != 0) goto L_0x01e4
                int r0 = r9.currentType
                if (r0 != 0) goto L_0x01e4
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r0 = r9.currentBlock
                int r0 = r0.level
                if (r0 > 0) goto L_0x01e4
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r12 = r12 + r0
            L_0x01e4:
                int r0 = r9.currentType
                if (r0 == r10) goto L_0x01ed
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r12 = r12 + r0
            L_0x01ed:
                r3 = r12
            L_0x01ee:
                r9.setMeasuredDimension(r11, r3)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockMapCell.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            if (this.currentBlock != null) {
                Theme.chat_docBackPaint.setColor(Theme.getColor("chat_inLocationBackground"));
                canvas.drawRect(this.imageView.getImageX(), this.imageView.getImageY(), this.imageView.getImageX2(), this.imageView.getImageY2(), Theme.chat_docBackPaint);
                int i2 = 0;
                int centerX = (int) (this.imageView.getCenterX() - ((float) (Theme.chat_locationDrawable[0].getIntrinsicWidth() / 2)));
                int centerY = (int) (this.imageView.getCenterY() - ((float) (Theme.chat_locationDrawable[0].getIntrinsicHeight() / 2)));
                Drawable[] drawableArr = Theme.chat_locationDrawable;
                drawableArr[0].setBounds(centerX, centerY, drawableArr[0].getIntrinsicWidth() + centerX, Theme.chat_locationDrawable[0].getIntrinsicHeight() + centerY);
                Theme.chat_locationDrawable[0].draw(canvas);
                this.imageView.draw(canvas);
                if (this.currentMapProvider == 2 && this.imageView.hasNotThumb()) {
                    int intrinsicWidth = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicWidth()) * 0.8f);
                    int intrinsicHeight = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicHeight()) * 0.8f);
                    int imageX = (int) (this.imageView.getImageX() + ((this.imageView.getImageWidth() - ((float) intrinsicWidth)) / 2.0f));
                    int imageY = (int) (this.imageView.getImageY() + ((this.imageView.getImageHeight() / 2.0f) - ((float) intrinsicHeight)));
                    Theme.chat_redLocationIcon.setAlpha((int) (this.imageView.getCurrentAlpha() * 255.0f));
                    Theme.chat_redLocationIcon.setBounds(imageX, imageY, intrinsicWidth + imageX, intrinsicHeight + imageY);
                    Theme.chat_redLocationIcon.draw(canvas);
                }
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            StringBuilder sb = new StringBuilder(LocaleController.getString("Map", NUM));
            if (this.captionLayout != null) {
                sb.append(", ");
                sb.append(this.captionLayout.getText());
            }
            accessibilityNodeInfo.setText(sb.toString());
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                arrayList.add(drawingText2);
            }
        }
    }

    private class BlockChannelCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private Paint backgroundPaint;
        private int buttonWidth;
        private AnimatorSet currentAnimation;
        private TLRPC$TL_pageBlockChannel currentBlock;
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
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setText(LocaleController.getString("ChannelJoin", NUM));
            this.textView.setGravity(19);
            addView(this.textView, LayoutHelper.createFrame(-2, 39, 53));
            this.textView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ArticleViewer.BlockChannelCell.this.lambda$new$0$ArticleViewer$BlockChannelCell(view);
                }
            });
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setImageResource(NUM);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(39, 39, 53));
            ContextProgressView contextProgressView = new ContextProgressView(context, 0);
            this.progressView = contextProgressView;
            addView(contextProgressView, LayoutHelper.createFrame(39, 39, 53));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ArticleViewer$BlockChannelCell(View view) {
            if (this.currentState == 0) {
                setState(1, true);
                ArticleViewer articleViewer = ArticleViewer.this;
                articleViewer.joinChannel(this, articleViewer.loadedChannel);
            }
        }

        public void setBlock(TLRPC$TL_pageBlockChannel tLRPC$TL_pageBlockChannel) {
            this.currentBlock = tLRPC$TL_pageBlockChannel;
            if (this.currentType == 0) {
                int color = Theme.getColor("switchTrack");
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                this.textView.setTextColor(ArticleViewer.this.getLinkTextColor());
                this.backgroundPaint.setColor(Color.argb(34, red, green, blue));
                this.imageView.setColorFilter(new PorterDuffColorFilter(ArticleViewer.getGrayTextColor(), PorterDuff.Mode.MULTIPLY));
            } else {
                this.textView.setTextColor(-1);
                this.backgroundPaint.setColor(NUM);
                this.imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
            }
            TLRPC$Chat chat = MessagesController.getInstance(ArticleViewer.this.currentAccount).getChat(Integer.valueOf(tLRPC$TL_pageBlockChannel.channel.id));
            if (chat == null || chat.min) {
                ArticleViewer.this.loadChannel(this, this.parentAdapter, tLRPC$TL_pageBlockChannel.channel);
                setState(1, false);
            } else {
                TLRPC$Chat unused = ArticleViewer.this.loadedChannel = chat;
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
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[9];
                TextView textView2 = this.textView;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = i == 0 ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(textView2, property, fArr);
                TextView textView3 = this.textView;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = i == 0 ? 1.0f : 0.1f;
                animatorArr[1] = ObjectAnimator.ofFloat(textView3, property2, fArr2);
                TextView textView4 = this.textView;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                fArr3[0] = i == 0 ? 1.0f : 0.1f;
                animatorArr[2] = ObjectAnimator.ofFloat(textView4, property3, fArr3);
                ContextProgressView contextProgressView = this.progressView;
                Property property4 = View.ALPHA;
                float[] fArr4 = new float[1];
                fArr4[0] = i == 1 ? 1.0f : 0.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(contextProgressView, property4, fArr4);
                ContextProgressView contextProgressView2 = this.progressView;
                Property property5 = View.SCALE_X;
                float[] fArr5 = new float[1];
                fArr5[0] = i == 1 ? 1.0f : 0.1f;
                animatorArr[4] = ObjectAnimator.ofFloat(contextProgressView2, property5, fArr5);
                ContextProgressView contextProgressView3 = this.progressView;
                Property property6 = View.SCALE_Y;
                float[] fArr6 = new float[1];
                fArr6[0] = i == 1 ? 1.0f : 0.1f;
                animatorArr[5] = ObjectAnimator.ofFloat(contextProgressView3, property6, fArr6);
                ImageView imageView2 = this.imageView;
                Property property7 = View.ALPHA;
                float[] fArr7 = new float[1];
                if (i == 2) {
                    f = 1.0f;
                }
                fArr7[0] = f;
                animatorArr[6] = ObjectAnimator.ofFloat(imageView2, property7, fArr7);
                ImageView imageView3 = this.imageView;
                Property property8 = View.SCALE_X;
                float[] fArr8 = new float[1];
                fArr8[0] = i == 2 ? 1.0f : 0.1f;
                animatorArr[7] = ObjectAnimator.ofFloat(imageView3, property8, fArr8);
                ImageView imageView4 = this.imageView;
                Property property9 = View.SCALE_Y;
                float[] fArr9 = new float[1];
                if (i == 2) {
                    f2 = 1.0f;
                }
                fArr9[0] = f2;
                animatorArr[8] = ObjectAnimator.ofFloat(imageView4, property9, fArr9);
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
            ImageView imageView5 = this.imageView;
            if (i == 2) {
                f = 1.0f;
            }
            imageView5.setAlpha(f);
            this.imageView.setScaleX(i == 2 ? 1.0f : 0.1f);
            ImageView imageView6 = this.imageView;
            if (i == 2) {
                f2 = 1.0f;
            }
            imageView6.setScaleY(f2);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.currentType != 0) {
                return super.onTouchEvent(motionEvent);
            }
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            setMeasuredDimension(size, AndroidUtilities.dp(48.0f));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            this.buttonWidth = this.textView.getMeasuredWidth();
            this.progressView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            TLRPC$TL_pageBlockChannel tLRPC$TL_pageBlockChannel = this.currentBlock;
            if (tLRPC$TL_pageBlockChannel != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, tLRPC$TL_pageBlockChannel.channel.title, (TLRPC$RichText) null, (size - AndroidUtilities.dp(52.0f)) - this.buttonWidth, this.textY, this.currentBlock, StaticLayoutEx.ALIGN_LEFT(), this.parentAdapter);
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

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            this.imageView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, this.textX2 + (this.buttonWidth / 2) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            this.progressView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, this.textX2 + (this.buttonWidth / 2) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            TextView textView2 = this.textView;
            int i5 = this.textX2;
            textView2.layout(i5, 0, textView2.getMeasuredWidth() + i5, this.textView.getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockAuthorDateCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC$TL_pageBlockAuthorDate currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX;
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockAuthorDateCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TLRPC$TL_pageBlockAuthorDate tLRPC$TL_pageBlockAuthorDate) {
            this.currentBlock = tLRPC$TL_pageBlockAuthorDate;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Failed to insert additional move for type inference */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r14, int r15) {
            /*
                r13 = this;
                int r14 = android.view.View.MeasureSpec.getSize(r14)
                org.telegram.tgnet.TLRPC$TL_pageBlockAuthorDate r5 = r13.currentBlock
                r15 = 1
                if (r5 == 0) goto L_0x0116
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r1 = r13.parentAdapter
                org.telegram.tgnet.TLRPC$RichText r4 = r5.author
                r2 = r13
                r3 = r4
                r6 = r14
                java.lang.CharSequence r0 = r0.getText((org.telegram.ui.ArticleViewer.WebpageAdapter) r1, (android.view.View) r2, (org.telegram.tgnet.TLRPC$RichText) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$PageBlock) r5, (int) r6)
                boolean r1 = r0 instanceof android.text.Spannable
                r2 = 0
                r3 = 0
                if (r1 == 0) goto L_0x002f
                r2 = r0
                android.text.Spannable r2 = (android.text.Spannable) r2
                int r1 = r0.length()
                java.lang.Class<android.text.style.MetricAffectingSpan> r4 = android.text.style.MetricAffectingSpan.class
                java.lang.Object[] r1 = r2.getSpans(r3, r1, r4)
                android.text.style.MetricAffectingSpan[] r1 = (android.text.style.MetricAffectingSpan[]) r1
                r12 = r2
                r2 = r1
                r1 = r12
                goto L_0x0030
            L_0x002f:
                r1 = r2
            L_0x0030:
                org.telegram.tgnet.TLRPC$TL_pageBlockAuthorDate r4 = r13.currentBlock
                int r4 = r4.published_date
                r5 = 1000(0x3e8, double:4.94E-321)
                if (r4 == 0) goto L_0x0060
                boolean r4 = android.text.TextUtils.isEmpty(r0)
                if (r4 != 0) goto L_0x0060
                r4 = 2131624368(0x7f0e01b0, float:1.8875914E38)
                r7 = 2
                java.lang.Object[] r7 = new java.lang.Object[r7]
                org.telegram.messenger.LocaleController r8 = org.telegram.messenger.LocaleController.getInstance()
                org.telegram.messenger.time.FastDateFormat r8 = r8.chatFullDate
                org.telegram.tgnet.TLRPC$TL_pageBlockAuthorDate r9 = r13.currentBlock
                int r9 = r9.published_date
                long r9 = (long) r9
                long r9 = r9 * r5
                java.lang.String r5 = r8.format((long) r9)
                r7[r3] = r5
                r7[r15] = r0
                java.lang.String r15 = "ArticleDateByAuthor"
                java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r15, r4, r7)
                goto L_0x0085
            L_0x0060:
                boolean r4 = android.text.TextUtils.isEmpty(r0)
                if (r4 != 0) goto L_0x0074
                r4 = 2131624367(0x7f0e01af, float:1.8875912E38)
                java.lang.Object[] r15 = new java.lang.Object[r15]
                r15[r3] = r0
                java.lang.String r5 = "ArticleByAuthor"
                java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r5, r4, r15)
                goto L_0x0085
            L_0x0074:
                org.telegram.messenger.LocaleController r15 = org.telegram.messenger.LocaleController.getInstance()
                org.telegram.messenger.time.FastDateFormat r15 = r15.chatFullDate
                org.telegram.tgnet.TLRPC$TL_pageBlockAuthorDate r4 = r13.currentBlock
                int r4 = r4.published_date
                long r7 = (long) r4
                long r7 = r7 * r5
                java.lang.String r15 = r15.format((long) r7)
            L_0x0085:
                if (r2 == 0) goto L_0x00b9
                int r4 = r2.length     // Catch:{ Exception -> 0x00b5 }
                if (r4 <= 0) goto L_0x00b9
                int r0 = android.text.TextUtils.indexOf(r15, r0)     // Catch:{ Exception -> 0x00b5 }
                r4 = -1
                if (r0 == r4) goto L_0x00b9
                android.text.Spannable$Factory r4 = android.text.Spannable.Factory.getInstance()     // Catch:{ Exception -> 0x00b5 }
                android.text.Spannable r15 = r4.newSpannable(r15)     // Catch:{ Exception -> 0x00b5 }
                r4 = 0
            L_0x009a:
                int r5 = r2.length     // Catch:{ Exception -> 0x00b5 }
                if (r4 >= r5) goto L_0x00b9
                r5 = r2[r4]     // Catch:{ Exception -> 0x00b5 }
                r6 = r2[r4]     // Catch:{ Exception -> 0x00b5 }
                int r6 = r1.getSpanStart(r6)     // Catch:{ Exception -> 0x00b5 }
                int r6 = r6 + r0
                r7 = r2[r4]     // Catch:{ Exception -> 0x00b5 }
                int r7 = r1.getSpanEnd(r7)     // Catch:{ Exception -> 0x00b5 }
                int r7 = r7 + r0
                r8 = 33
                r15.setSpan(r5, r6, r7, r8)     // Catch:{ Exception -> 0x00b5 }
                int r4 = r4 + 1
                goto L_0x009a
            L_0x00b5:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00b9:
                r6 = r15
                org.telegram.ui.ArticleViewer r4 = org.telegram.ui.ArticleViewer.this
                r7 = 0
                r15 = 1108344832(0x42100000, float:36.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r8 = r14 - r15
                int r9 = r13.textY
                org.telegram.tgnet.TLRPC$TL_pageBlockAuthorDate r10 = r13.currentBlock
                org.telegram.ui.ArticleViewer$WebpageAdapter r11 = r13.parentAdapter
                r5 = r13
                org.telegram.ui.ArticleViewer$DrawingText r15 = r4.createLayoutForText(r5, r6, r7, r8, r9, r10, r11)
                r13.textLayout = r15
                if (r15 == 0) goto L_0x0115
                r15 = 1098907648(0x41800000, float:16.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
                org.telegram.ui.ArticleViewer$DrawingText r1 = r13.textLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                int r0 = r0 + r3
                org.telegram.ui.ArticleViewer$WebpageAdapter r1 = r13.parentAdapter
                boolean r1 = r1.isRtl
                if (r1 == 0) goto L_0x0101
                float r1 = (float) r14
                org.telegram.ui.ArticleViewer$DrawingText r2 = r13.textLayout
                float r2 = r2.getLineWidth(r3)
                float r1 = r1 - r2
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
                float r15 = (float) r15
                float r1 = r1 - r15
                double r1 = (double) r1
                double r1 = java.lang.Math.floor(r1)
                int r15 = (int) r1
                r13.textX = r15
                goto L_0x0109
            L_0x0101:
                r15 = 1099956224(0x41900000, float:18.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
                r13.textX = r15
            L_0x0109:
                org.telegram.ui.ArticleViewer$DrawingText r15 = r13.textLayout
                int r1 = r13.textX
                r15.x = r1
                int r1 = r13.textY
                r15.y = r1
                r15 = r0
                goto L_0x0116
            L_0x0115:
                r15 = 0
            L_0x0116:
                r13.setMeasuredDimension(r14, r15)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockAuthorDateCell.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockTitleCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC$TL_pageBlockTitle currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockTitleCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TLRPC$TL_pageBlockTitle tLRPC$TL_pageBlockTitle) {
            this.currentBlock = tLRPC$TL_pageBlockTitle;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int i3;
            int size = View.MeasureSpec.getSize(i);
            TLRPC$TL_pageBlockTitle tLRPC$TL_pageBlockTitle = this.currentBlock;
            if (tLRPC$TL_pageBlockTitle != null) {
                i3 = 0;
                if (tLRPC$TL_pageBlockTitle.first) {
                    i3 = 0 + AndroidUtilities.dp(8.0f);
                    this.textY = AndroidUtilities.dp(16.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
                }
                DrawingText access$13400 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.text, size - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13400;
                if (access$13400 != null) {
                    i3 += AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(size, i3);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
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
                accessibilityNodeInfo.setText(this.textLayout.getText() + ", " + LocaleController.getString("AccDescrIVTitle", NUM));
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockKickerCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC$TL_pageBlockKicker currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockKickerCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TLRPC$TL_pageBlockKicker tLRPC$TL_pageBlockKicker) {
            this.currentBlock = tLRPC$TL_pageBlockKicker;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int i3;
            int size = View.MeasureSpec.getSize(i);
            TLRPC$TL_pageBlockKicker tLRPC$TL_pageBlockKicker = this.currentBlock;
            if (tLRPC$TL_pageBlockKicker != null) {
                i3 = 0;
                if (tLRPC$TL_pageBlockKicker.first) {
                    this.textY = AndroidUtilities.dp(16.0f);
                    i3 = 0 + AndroidUtilities.dp(8.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
                }
                DrawingText access$13400 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.text, size - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13400;
                if (access$13400 != null) {
                    i3 += AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(size, i3);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockFooterCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC$TL_pageBlockFooter currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockFooterCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TLRPC$TL_pageBlockFooter tLRPC$TL_pageBlockFooter) {
            this.currentBlock = tLRPC$TL_pageBlockFooter;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int i3;
            int size = View.MeasureSpec.getSize(i);
            TLRPC$TL_pageBlockFooter tLRPC$TL_pageBlockFooter = this.currentBlock;
            int i4 = 0;
            if (tLRPC$TL_pageBlockFooter != null) {
                int i5 = tLRPC$TL_pageBlockFooter.level;
                if (i5 == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((float) ((i5 * 14) + 18));
                }
                DrawingText access$13400 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.text, (size - AndroidUtilities.dp(18.0f)) - this.textX, this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13400;
                if (access$13400 != null) {
                    int height = access$13400.getHeight();
                    if (this.currentBlock.level > 0) {
                        i3 = AndroidUtilities.dp(8.0f);
                    } else {
                        i3 = AndroidUtilities.dp(16.0f);
                    }
                    i4 = height + i3;
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i4 = 1;
            }
            setMeasuredDimension(size, i4);
        }

        /* access modifiers changed from: protected */
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private class BlockPreformattedCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        /* access modifiers changed from: private */
        public TLRPC$TL_pageBlockPreformatted currentBlock;
        /* access modifiers changed from: private */
        public WebpageAdapter parentAdapter;
        private HorizontalScrollView scrollView;
        /* access modifiers changed from: private */
        public View textContainer;
        /* access modifiers changed from: private */
        public DrawingText textLayout;

        public BlockPreformattedCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
            AnonymousClass1 r6 = new HorizontalScrollView(context, ArticleViewer.this) {
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (BlockPreformattedCell.this.textContainer.getMeasuredWidth() > getMeasuredWidth()) {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                    return super.onInterceptTouchEvent(motionEvent);
                }

                /* access modifiers changed from: protected */
                public void onScrollChanged(int i, int i2, int i3, int i4) {
                    super.onScrollChanged(i, i2, i3, i4);
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null) {
                        DrawingText unused = ArticleViewer.this.pressedLinkOwnerLayout = null;
                        View unused2 = ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                }
            };
            this.scrollView = r6;
            r6.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            addView(this.scrollView, LayoutHelper.createFrame(-1, -2.0f));
            this.textContainer = new View(context, ArticleViewer.this) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int i3 = 0;
                    int i4 = 1;
                    if (BlockPreformattedCell.this.currentBlock != null) {
                        BlockPreformattedCell blockPreformattedCell = BlockPreformattedCell.this;
                        DrawingText unused = blockPreformattedCell.textLayout = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, blockPreformattedCell.currentBlock.text, AndroidUtilities.dp(5000.0f), 0, BlockPreformattedCell.this.currentBlock, BlockPreformattedCell.this.parentAdapter);
                        if (BlockPreformattedCell.this.textLayout != null) {
                            int height = BlockPreformattedCell.this.textLayout.getHeight() + 0;
                            int lineCount = BlockPreformattedCell.this.textLayout.getLineCount();
                            while (i3 < lineCount) {
                                i4 = Math.max((int) Math.ceil((double) BlockPreformattedCell.this.textLayout.getLineWidth(i3)), i4);
                                i3++;
                            }
                            i3 = height;
                        }
                    } else {
                        i3 = 1;
                    }
                    setMeasuredDimension(i4 + AndroidUtilities.dp(32.0f), i3);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    BlockPreformattedCell blockPreformattedCell = BlockPreformattedCell.this;
                    ArticleViewer articleViewer = ArticleViewer.this;
                    WebpageAdapter access$20600 = blockPreformattedCell.parentAdapter;
                    BlockPreformattedCell blockPreformattedCell2 = BlockPreformattedCell.this;
                    return articleViewer.checkLayoutForLinks(access$20600, motionEvent, blockPreformattedCell2, blockPreformattedCell2.textLayout, 0, 0) || super.onTouchEvent(motionEvent);
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (BlockPreformattedCell.this.textLayout != null) {
                        canvas.save();
                        BlockPreformattedCell blockPreformattedCell = BlockPreformattedCell.this;
                        ArticleViewer.this.drawTextSelection(canvas, blockPreformattedCell);
                        BlockPreformattedCell.this.textLayout.draw(canvas);
                        canvas.restore();
                        BlockPreformattedCell.this.textLayout.x = (int) getX();
                        BlockPreformattedCell.this.textLayout.y = (int) getY();
                    }
                }
            };
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -1);
            int dp = AndroidUtilities.dp(16.0f);
            layoutParams.rightMargin = dp;
            layoutParams.leftMargin = dp;
            int dp2 = AndroidUtilities.dp(12.0f);
            layoutParams.bottomMargin = dp2;
            layoutParams.topMargin = dp2;
            this.scrollView.addView(this.textContainer, layoutParams);
            if (Build.VERSION.SDK_INT >= 23) {
                this.scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    public final void onScrollChange(View view, int i, int i2, int i3, int i4) {
                        ArticleViewer.BlockPreformattedCell.this.lambda$new$0$ArticleViewer$BlockPreformattedCell(view, i, i2, i3, i4);
                    }
                });
            }
            setWillNotDraw(false);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ArticleViewer$BlockPreformattedCell(View view, int i, int i2, int i3, int i4) {
            TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = ArticleViewer.this.textSelectionHelper;
            if (articleTextSelectionHelper != null && articleTextSelectionHelper.isSelectionMode()) {
                ArticleViewer.this.textSelectionHelper.invalidate();
            }
        }

        public void setBlock(TLRPC$TL_pageBlockPreformatted tLRPC$TL_pageBlockPreformatted) {
            this.currentBlock = tLRPC$TL_pageBlockPreformatted;
            this.scrollView.setScrollX(0);
            this.textContainer.requestLayout();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
            setMeasuredDimension(size, this.scrollView.getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.drawRect(0.0f, (float) AndroidUtilities.dp(8.0f), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(8.0f)), ArticleViewer.preformattedBackgroundPaint);
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }

        public void invalidate() {
            this.textContainer.invalidate();
            super.invalidate();
        }
    }

    private class BlockSubheaderCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC$TL_pageBlockSubheader currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockSubheaderCell(Context context, WebpageAdapter webpageAdapter) {
            super(context);
            this.parentAdapter = webpageAdapter;
        }

        public void setBlock(TLRPC$TL_pageBlockSubheader tLRPC$TL_pageBlockSubheader) {
            this.currentBlock = tLRPC$TL_pageBlockSubheader;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, motionEvent, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(motionEvent);
        }

        /* access modifiers changed from: protected */
        @SuppressLint({"NewApi"})
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            TLRPC$TL_pageBlockSubheader tLRPC$TL_pageBlockSubheader = this.currentBlock;
            int i3 = 0;
            if (tLRPC$TL_pageBlockSubheader != null) {
                DrawingText access$13400 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tLRPC$TL_pageBlockSubheader.text, size - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13400;
                if (access$13400 != null) {
                    i3 = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    DrawingText drawingText = this.textLayout;
                    drawingText.x = this.textX;
                    drawingText.y = this.textY;
                }
            } else {
                i3 = 1;
            }
            setMeasuredDimension(size, i3);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
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
                accessibilityNodeInfo.setText(this.textLayout.getText() + ", " + LocaleController.getString("AccDescrIVHeading", NUM));
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> arrayList) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                arrayList.add(drawingText);
            }
        }
    }

    private static class ReportCell extends FrameLayout {
        /* access modifiers changed from: private */
        public boolean hasViews;
        private TextView textView;
        private TextView viewsTextView;

        public ReportCell(Context context) {
            super(context);
            setTag(90);
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setText(LocaleController.getString("PreviewFeedback2", NUM));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setGravity(17);
            this.textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            addView(this.textView, LayoutHelper.createFrame(-1, 34.0f, 51, 0.0f, 10.0f, 0.0f, 0.0f));
            TextView textView3 = new TextView(context);
            this.viewsTextView = textView3;
            textView3.setTextSize(1, 12.0f);
            this.viewsTextView.setGravity(19);
            this.viewsTextView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            addView(this.viewsTextView, LayoutHelper.createFrame(-1, 34.0f, 51, 0.0f, 10.0f, 0.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
        }

        public void setViews(int i) {
            if (i == 0) {
                this.hasViews = false;
                this.viewsTextView.setVisibility(8);
                this.textView.setGravity(17);
            } else {
                this.hasViews = true;
                this.viewsTextView.setVisibility(0);
                this.textView.setGravity(21);
                this.viewsTextView.setText(LocaleController.formatPluralStringComma("Views", i));
            }
            int color = Theme.getColor("switchTrack");
            this.textView.setTextColor(ArticleViewer.getGrayTextColor());
            this.viewsTextView.setTextColor(ArticleViewer.getGrayTextColor());
            this.textView.setBackgroundColor(Color.argb(34, Color.red(color), Color.green(color), Color.blue(color)));
        }
    }

    /* access modifiers changed from: private */
    public void drawTextSelection(Canvas canvas, TextSelectionHelper.ArticleSelectableView articleSelectableView) {
        drawTextSelection(canvas, articleSelectableView, 0);
    }

    /* access modifiers changed from: private */
    public void drawTextSelection(Canvas canvas, TextSelectionHelper.ArticleSelectableView articleSelectableView, int i) {
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper;
        View view = (View) articleSelectableView;
        if (view.getTag() == null || view.getTag() != "bottomSheet" || (articleTextSelectionHelper = this.textSelectionHelperBottomSheet) == null) {
            this.textSelectionHelper.draw(canvas, articleSelectableView, i);
        } else {
            articleTextSelectionHelper.draw(canvas, articleSelectableView, i);
        }
    }

    public boolean openPhoto(TLRPC$PageBlock tLRPC$PageBlock, WebpageAdapter webpageAdapter) {
        List list;
        int i;
        if (!(tLRPC$PageBlock instanceof TLRPC$TL_pageBlockVideo) || WebPageUtils.isVideo(webpageAdapter.currentPage, tLRPC$PageBlock)) {
            ArrayList arrayList = new ArrayList(webpageAdapter.photoBlocks);
            i = webpageAdapter.photoBlocks.indexOf(tLRPC$PageBlock);
            list = arrayList;
        } else {
            list = Collections.singletonList(tLRPC$PageBlock);
            i = 0;
        }
        PhotoViewer instance = PhotoViewer.getInstance();
        instance.setParentActivity(this.parentActivity);
        return instance.openPhoto(i, (PhotoViewer.PageBlocksAdapter) new RealPageBlocksAdapter(webpageAdapter.currentPage, list), (PhotoViewer.PhotoViewerProvider) new PageBlocksPhotoViewerProvider(list));
    }

    private class RealPageBlocksAdapter implements PhotoViewer.PageBlocksAdapter {
        private final TLRPC$WebPage page;
        private final List<TLRPC$PageBlock> pageBlocks;

        private RealPageBlocksAdapter(TLRPC$WebPage tLRPC$WebPage, List<TLRPC$PageBlock> list) {
            this.page = tLRPC$WebPage;
            this.pageBlocks = list;
        }

        public int getItemsCount() {
            return this.pageBlocks.size();
        }

        public TLRPC$PageBlock get(int i) {
            return this.pageBlocks.get(i);
        }

        public List<TLRPC$PageBlock> getAll() {
            return this.pageBlocks;
        }

        public boolean isVideo(int i) {
            return i < this.pageBlocks.size() && i >= 0 && WebPageUtils.isVideo(this.page, get(i));
        }

        public TLObject getMedia(int i) {
            if (i >= this.pageBlocks.size() || i < 0) {
                return null;
            }
            return WebPageUtils.getMedia(this.page, get(i));
        }

        public File getFile(int i) {
            if (i >= this.pageBlocks.size() || i < 0) {
                return null;
            }
            return WebPageUtils.getMediaFile(this.page, get(i));
        }

        public String getFileName(int i) {
            TLObject media = getMedia(i);
            if (media instanceof TLRPC$Photo) {
                media = FileLoader.getClosestPhotoSizeWithSize(((TLRPC$Photo) media).sizes, AndroidUtilities.getPhotoSize());
            }
            return FileLoader.getAttachFileName(media);
        }

        /* JADX WARNING: Removed duplicated region for block: B:19:? A[RETURN, SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:7:0x002b  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.CharSequence getCaption(int r9) {
            /*
                r8 = this;
                org.telegram.tgnet.TLRPC$PageBlock r5 = r8.get(r9)
                boolean r9 = r5 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockPhoto
                r7 = 0
                if (r9 == 0) goto L_0x0028
                r9 = r5
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r9 = (org.telegram.tgnet.TLRPC$TL_pageBlockPhoto) r9
                java.lang.String r9 = r9.url
                boolean r0 = android.text.TextUtils.isEmpty(r9)
                if (r0 != 0) goto L_0x0028
                android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
                r0.<init>(r9)
                org.telegram.ui.ArticleViewer$RealPageBlocksAdapter$1 r1 = new org.telegram.ui.ArticleViewer$RealPageBlocksAdapter$1
                r1.<init>(r9)
                int r9 = r9.length()
                r2 = 34
                r0.setSpan(r1, r7, r9, r2)
                goto L_0x0029
            L_0x0028:
                r0 = 0
            L_0x0029:
                if (r0 != 0) goto L_0x0087
                org.telegram.ui.ArticleViewer r9 = org.telegram.ui.ArticleViewer.this
                r0 = 2
                org.telegram.tgnet.TLRPC$RichText r4 = r9.getBlockCaption(r5, r0)
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                org.telegram.tgnet.TLRPC$WebPage r1 = r8.page
                r2 = 0
                r9 = 1120403456(0x42CLASSNAME, float:100.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                int r6 = -r9
                r3 = r4
                java.lang.CharSequence r0 = r0.getText((org.telegram.tgnet.TLRPC$WebPage) r1, (android.view.View) r2, (org.telegram.tgnet.TLRPC$RichText) r3, (org.telegram.tgnet.TLRPC$RichText) r4, (org.telegram.tgnet.TLRPC$PageBlock) r5, (int) r6)
                boolean r9 = r0 instanceof android.text.Spannable
                if (r9 == 0) goto L_0x0087
                r9 = r0
                android.text.Spannable r9 = (android.text.Spannable) r9
                int r1 = r0.length()
                java.lang.Class<org.telegram.ui.Components.TextPaintUrlSpan> r2 = org.telegram.ui.Components.TextPaintUrlSpan.class
                java.lang.Object[] r1 = r9.getSpans(r7, r1, r2)
                org.telegram.ui.Components.TextPaintUrlSpan[] r1 = (org.telegram.ui.Components.TextPaintUrlSpan[]) r1
                android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
                java.lang.String r0 = r0.toString()
                r2.<init>(r0)
                if (r1 == 0) goto L_0x0086
                int r0 = r1.length
                if (r0 <= 0) goto L_0x0086
            L_0x0064:
                int r0 = r1.length
                if (r7 >= r0) goto L_0x0086
                org.telegram.ui.ArticleViewer$RealPageBlocksAdapter$2 r0 = new org.telegram.ui.ArticleViewer$RealPageBlocksAdapter$2
                r3 = r1[r7]
                java.lang.String r3 = r3.getUrl()
                r0.<init>(r3)
                r3 = r1[r7]
                int r3 = r9.getSpanStart(r3)
                r4 = r1[r7]
                int r4 = r9.getSpanEnd(r4)
                r5 = 33
                r2.setSpan(r0, r3, r4, r5)
                int r7 = r7 + 1
                goto L_0x0064
            L_0x0086:
                r0 = r2
            L_0x0087:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.RealPageBlocksAdapter.getCaption(int):java.lang.CharSequence");
        }

        public TLRPC$PhotoSize getFileLocation(TLObject tLObject, int[] iArr) {
            TLRPC$PhotoSize closestPhotoSizeWithSize;
            if (tLObject instanceof TLRPC$Photo) {
                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(((TLRPC$Photo) tLObject).sizes, AndroidUtilities.getPhotoSize());
                if (closestPhotoSizeWithSize2 != null) {
                    iArr[0] = closestPhotoSizeWithSize2.size;
                    if (iArr[0] == 0) {
                        iArr[0] = -1;
                    }
                    return closestPhotoSizeWithSize2;
                }
                iArr[0] = -1;
                return null;
            } else if (!(tLObject instanceof TLRPC$Document) || (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(((TLRPC$Document) tLObject).thumbs, 90)) == null) {
                return null;
            } else {
                iArr[0] = closestPhotoSizeWithSize.size;
                if (iArr[0] == 0) {
                    iArr[0] = -1;
                }
                return closestPhotoSizeWithSize;
            }
        }

        public void updateSlideshowCell(TLRPC$PageBlock tLRPC$PageBlock) {
            int childCount = ArticleViewer.this.listView[0].getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = ArticleViewer.this.listView[0].getChildAt(i);
                if (childAt instanceof BlockSlideshowCell) {
                    BlockSlideshowCell blockSlideshowCell = (BlockSlideshowCell) childAt;
                    int indexOf = blockSlideshowCell.currentBlock.items.indexOf(tLRPC$PageBlock);
                    if (indexOf != -1) {
                        blockSlideshowCell.innerListView.setCurrentItem(indexOf, false);
                        return;
                    }
                }
            }
        }

        public Object getParentObject() {
            return this.page;
        }
    }

    private class PageBlocksPhotoViewerProvider extends PhotoViewer.EmptyPhotoViewerProvider {
        private final List<TLRPC$PageBlock> pageBlocks;
        private final int[] tempArr = new int[2];

        public PageBlocksPhotoViewerProvider(List<TLRPC$PageBlock> list) {
            this.pageBlocks = list;
        }

        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
            ImageReceiver imageReceiverFromListView;
            if (i < 0 || i >= this.pageBlocks.size() || (imageReceiverFromListView = getImageReceiverFromListView(ArticleViewer.this.listView[0], this.pageBlocks.get(i), this.tempArr)) == null) {
                return null;
            }
            PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
            int[] iArr = this.tempArr;
            placeProviderObject.viewX = iArr[0];
            placeProviderObject.viewY = iArr[1];
            placeProviderObject.parentView = ArticleViewer.this.listView[0];
            placeProviderObject.imageReceiver = imageReceiverFromListView;
            placeProviderObject.thumb = imageReceiverFromListView.getBitmapSafe();
            placeProviderObject.radius = imageReceiverFromListView.getRoundRadius();
            placeProviderObject.clipTopAddition = ArticleViewer.this.currentHeaderHeight;
            return placeProviderObject;
        }

        private ImageReceiver getImageReceiverFromListView(ViewGroup viewGroup, TLRPC$PageBlock tLRPC$PageBlock, int[] iArr) {
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                ImageReceiver imageReceiverView = getImageReceiverView(viewGroup.getChildAt(i), tLRPC$PageBlock, iArr);
                if (imageReceiverView != null) {
                    return imageReceiverView;
                }
            }
            return null;
        }

        private ImageReceiver getImageReceiverView(View view, TLRPC$PageBlock tLRPC$PageBlock, int[] iArr) {
            ImageReceiver imageReceiverView;
            ImageReceiver imageReceiverView2;
            if (view instanceof BlockPhotoCell) {
                BlockPhotoCell blockPhotoCell = (BlockPhotoCell) view;
                if (blockPhotoCell.currentBlock != tLRPC$PageBlock) {
                    return null;
                }
                view.getLocationInWindow(iArr);
                return blockPhotoCell.imageView;
            } else if (view instanceof BlockVideoCell) {
                BlockVideoCell blockVideoCell = (BlockVideoCell) view;
                if (blockVideoCell.currentBlock != tLRPC$PageBlock) {
                    return null;
                }
                view.getLocationInWindow(iArr);
                return blockVideoCell.imageView;
            } else if (view instanceof BlockCollageCell) {
                ImageReceiver imageReceiverFromListView = getImageReceiverFromListView(((BlockCollageCell) view).innerListView, tLRPC$PageBlock, iArr);
                if (imageReceiverFromListView != null) {
                    return imageReceiverFromListView;
                }
                return null;
            } else if (view instanceof BlockSlideshowCell) {
                ImageReceiver imageReceiverFromListView2 = getImageReceiverFromListView(((BlockSlideshowCell) view).innerListView, tLRPC$PageBlock, iArr);
                if (imageReceiverFromListView2 != null) {
                    return imageReceiverFromListView2;
                }
                return null;
            } else if (view instanceof BlockListItemCell) {
                BlockListItemCell blockListItemCell = (BlockListItemCell) view;
                if (blockListItemCell.blockLayout == null || (imageReceiverView2 = getImageReceiverView(blockListItemCell.blockLayout.itemView, tLRPC$PageBlock, iArr)) == null) {
                    return null;
                }
                return imageReceiverView2;
            } else if (!(view instanceof BlockOrderedListItemCell)) {
                return null;
            } else {
                BlockOrderedListItemCell blockOrderedListItemCell = (BlockOrderedListItemCell) view;
                if (blockOrderedListItemCell.blockLayout == null || (imageReceiverView = getImageReceiverView(blockOrderedListItemCell.blockLayout.itemView, tLRPC$PageBlock, iArr)) == null) {
                    return null;
                }
                return imageReceiverView;
            }
        }
    }
}
