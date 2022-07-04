package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.MetricAffectingSpan;
import android.text.style.URLSpan;
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
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
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
import java.util.Iterator;
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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnchorSpan;
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
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.TableLayout;
import org.telegram.ui.Components.TextPaintImageReceiverSpan;
import org.telegram.ui.Components.TextPaintMarkSpan;
import org.telegram.ui.Components.TextPaintSpan;
import org.telegram.ui.Components.TextPaintUrlSpan;
import org.telegram.ui.Components.TextPaintWebpageUrlSpan;
import org.telegram.ui.Components.TranslateAlert;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.WebPlayerView;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PinchToZoomHelper;

public class ArticleViewer implements NotificationCenter.NotificationCenterDelegate {
    public static final Property<WindowView, Float> ARTICLE_VIEWER_INNER_TRANSLATION_X = new AnimationProperties.FloatProperty<WindowView>("innerTranslationX") {
        public void setValue(WindowView object, float value) {
            object.setInnerTranslationX(value);
        }

        public Float get(WindowView object) {
            return Float.valueOf(object.getInnerTranslationX());
        }
    };
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
    /* access modifiers changed from: private */
    public static TextPaint audioTimePaint = new TextPaint(1);
    private static SparseArray<TextPaint> authorTextPaints = new SparseArray<>();
    private static TextPaint channelNamePaint = null;
    private static TextPaint channelNamePhotoPaint = null;
    private static SparseArray<TextPaint> detailsTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint dividerPaint = null;
    /* access modifiers changed from: private */
    public static Paint dotsPaint = null;
    private static TextPaint embedPostAuthorPaint = null;
    private static SparseArray<TextPaint> embedPostCaptionTextPaints = new SparseArray<>();
    private static TextPaint embedPostDatePaint = null;
    private static SparseArray<TextPaint> embedPostTextPaints = new SparseArray<>();
    private static TextPaint errorTextPaint = null;
    private static SparseArray<TextPaint> footerTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> headerTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> kickerTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static TextPaint listTextNumPaint = null;
    private static SparseArray<TextPaint> listTextPaints = new SparseArray<>();
    private static TextPaint listTextPointerPaint = null;
    private static SparseArray<TextPaint> mediaCaptionTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> mediaCreditTextPaints = new SparseArray<>();
    private static final int open_item = 3;
    private static SparseArray<TextPaint> paragraphTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint photoBackgroundPaint = null;
    private static SparseArray<TextPaint> photoCaptionTextPaints = new SparseArray<>();
    private static SparseArray<TextPaint> photoCreditTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint preformattedBackgroundPaint = null;
    private static SparseArray<TextPaint> preformattedTextPaints = new SparseArray<>();
    /* access modifiers changed from: private */
    public static Paint quoteLinePaint = null;
    private static SparseArray<TextPaint> quoteTextPaints = new SparseArray<>();
    private static TextPaint relatedArticleHeaderPaint = null;
    private static TextPaint relatedArticleTextPaint = null;
    private static SparseArray<TextPaint> relatedArticleTextPaints = new SparseArray<>();
    private static final int search_item = 1;
    private static final int settings_item = 4;
    private static final int share_item = 2;
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
    /* access modifiers changed from: private */
    public static Paint urlPaint;
    /* access modifiers changed from: private */
    public static Paint webpageMarkPaint;
    /* access modifiers changed from: private */
    public static Paint webpageSearchPaint;
    /* access modifiers changed from: private */
    public static Paint webpageUrlPaint;
    private final String BOTTOM_SHEET_VIEW_TAG = "bottomSheet";
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
    public boolean closeAnimationInProgress;
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
    public LinkSpanDrawable.LinkCollector links = new LinkSpanDrawable.LinkCollector();
    /* access modifiers changed from: private */
    public RecyclerListView[] listView;
    /* access modifiers changed from: private */
    public TLRPC.Chat loadedChannel;
    private boolean loadingChannel;
    /* access modifiers changed from: private */
    public ActionBarMenuItem menuButton;
    private FrameLayout menuContainer;
    /* access modifiers changed from: private */
    public Paint navigationBarPaint = new Paint();
    private int openUrlReqId;
    /* access modifiers changed from: private */
    public AnimatorSet pageSwitchAnimation;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.WebPage> pagesStack = new ArrayList<>();
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
    public LinkSpanDrawable<TextPaintUrlSpan> pressedLink;
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

    static /* synthetic */ int access$13208(ArticleViewer x0) {
        int i = x0.lastBlockNum;
        x0.lastBlockNum = i + 1;
        return i;
    }

    static /* synthetic */ int access$2104(ArticleViewer x0) {
        int i = x0.pressCount + 1;
        x0.pressCount = i;
        return i;
    }

    public static ArticleViewer getInstance() {
        ArticleViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (ArticleViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    ArticleViewer articleViewer = new ArticleViewer();
                    localInstance = articleViewer;
                    Instance = articleViewer;
                }
            }
        }
        return localInstance;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    private static class TL_pageBlockRelatedArticlesChild extends TLRPC.PageBlock {
        /* access modifiers changed from: private */
        public int num;
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesChild() {
        }
    }

    private static class TL_pageBlockRelatedArticlesShadow extends TLRPC.PageBlock {
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockRelatedArticles parent;

        private TL_pageBlockRelatedArticlesShadow() {
        }
    }

    private static class TL_pageBlockDetailsChild extends TLRPC.PageBlock {
        /* access modifiers changed from: private */
        public TLRPC.PageBlock block;
        /* access modifiers changed from: private */
        public TLRPC.PageBlock parent;

        private TL_pageBlockDetailsChild() {
        }
    }

    private static class TL_pageBlockDetailsBottom extends TLRPC.PageBlock {
        private TLRPC.TL_pageBlockDetails parent;

        private TL_pageBlockDetailsBottom() {
        }
    }

    private class TL_pageBlockListParent extends TLRPC.PageBlock {
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
        public TLRPC.TL_pageBlockList pageBlockList;

        private TL_pageBlockListParent() {
            this.items = new ArrayList<>();
        }
    }

    private class TL_pageBlockListItem extends TLRPC.PageBlock {
        /* access modifiers changed from: private */
        public TLRPC.PageBlock blockItem;
        /* access modifiers changed from: private */
        public int index;
        /* access modifiers changed from: private */
        public String num;
        /* access modifiers changed from: private */
        public DrawingText numLayout;
        /* access modifiers changed from: private */
        public TL_pageBlockListParent parent;
        /* access modifiers changed from: private */
        public TLRPC.RichText textItem;

        private TL_pageBlockListItem() {
            this.index = Integer.MAX_VALUE;
        }
    }

    private class TL_pageBlockOrderedListParent extends TLRPC.PageBlock {
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
        public TLRPC.TL_pageBlockOrderedList pageBlockOrderedList;

        private TL_pageBlockOrderedListParent() {
            this.items = new ArrayList<>();
        }
    }

    private class TL_pageBlockOrderedListItem extends TLRPC.PageBlock {
        /* access modifiers changed from: private */
        public TLRPC.PageBlock blockItem;
        /* access modifiers changed from: private */
        public int index;
        /* access modifiers changed from: private */
        public String num;
        /* access modifiers changed from: private */
        public DrawingText numLayout;
        /* access modifiers changed from: private */
        public TL_pageBlockOrderedListParent parent;
        /* access modifiers changed from: private */
        public TLRPC.RichText textItem;

        private TL_pageBlockOrderedListItem() {
            this.index = Integer.MAX_VALUE;
        }
    }

    private static class TL_pageBlockEmbedPostCaption extends TLRPC.TL_pageBlockEmbedPost {
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockEmbedPost parent;

        private TL_pageBlockEmbedPostCaption() {
        }
    }

    public class DrawingText implements TextSelectionHelper.TextLayoutBlock {
        public View latestParentView;
        public LinkPath markPath;
        public TLRPC.PageBlock parentBlock;
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

        public void draw(Canvas canvas, View view) {
            float x2;
            float width;
            this.latestParentView = view;
            if (!ArticleViewer.this.searchResults.isEmpty()) {
                SearchResult result = (SearchResult) ArticleViewer.this.searchResults.get(ArticleViewer.this.currentSearchIndex);
                if (result.block != this.parentBlock || (result.text != this.parentText && (!(result.text instanceof String) || this.parentText != null))) {
                    this.searchIndex = -1;
                    this.searchPath = null;
                } else if (this.searchIndex != result.index) {
                    LinkPath linkPath = new LinkPath(true);
                    this.searchPath = linkPath;
                    linkPath.setAllowReset(false);
                    this.searchPath.setCurrentLayout(this.textLayout, result.index, 0.0f);
                    this.searchPath.setBaselineShift(0);
                    this.textLayout.getSelectionPath(result.index, result.index + ArticleViewer.this.searchText.length(), this.searchPath);
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
            if (ArticleViewer.this.links.draw(canvas, this)) {
                view.invalidate();
            }
            if (ArticleViewer.this.pressedLinkOwnerLayout == this && ArticleViewer.this.pressedLink == null && ArticleViewer.this.drawBlockSelection) {
                if (getLineCount() == 1) {
                    width = getLineWidth(0);
                    x2 = getLineLeft(0);
                } else {
                    width = (float) getWidth();
                    x2 = 0.0f;
                }
                canvas.drawRect(((float) (-AndroidUtilities.dp(2.0f))) + x2, 0.0f, x2 + width + ((float) AndroidUtilities.dp(2.0f)), (float) getHeight(), ArticleViewer.urlPaint);
            }
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
                public void onSeekBarDrag(boolean stop, float progress) {
                    int fontSize = Math.round(((float) TextSizeCell.this.startFontSize) + (((float) (TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize)) * progress));
                    if (fontSize != SharedConfig.ivFontSize) {
                        SharedConfig.ivFontSize = fontSize;
                        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                        editor.putInt("iv_font_size", SharedConfig.ivFontSize);
                        editor.commit();
                        ArticleViewer.this.adapter[0].searchTextOffset.clear();
                        ArticleViewer.this.updatePaintSize();
                        TextSizeCell.this.invalidate();
                    }
                }

                public void onSeekBarPressed(boolean pressed) {
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int w = View.MeasureSpec.getSize(widthMeasureSpec);
            if (this.lastWidth != w) {
                SeekBarView seekBarView = this.sizeBar;
                int i = SharedConfig.ivFontSize;
                int i2 = this.startFontSize;
                seekBarView.setProgress(((float) (i - i2)) / ((float) (this.endFontSize - i2)));
                this.lastWidth = w;
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
            int i = 5;
            addView(this.radioButton, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 22), 13.0f, (float) (LocaleController.isRTL ? 22 : 0), 0.0f));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (!LocaleController.isRTL ? 3 : i) | 48, (float) (LocaleController.isRTL ? 17 : 62), 0.0f, (float) (LocaleController.isRTL ? 62 : 17), 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }

        public void select(boolean value, boolean animated) {
            this.radioButton.setChecked(value, animated);
        }

        public void setTextAndTypeface(String text, Typeface typeface) {
            this.textView.setText(text);
            this.textView.setTypeface(typeface);
            setContentDescription(text);
            invalidate();
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName(RadioButton.class.getName());
            info.setChecked(this.radioButton.isChecked());
            info.setCheckable(true);
        }
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (ArticleViewer.this.pendingCheckForLongPress == null) {
                CheckForLongPress unused = ArticleViewer.this.pendingCheckForLongPress = new CheckForLongPress();
            }
            ArticleViewer.this.pendingCheckForLongPress.currentPressCount = ArticleViewer.access$2104(ArticleViewer.this);
            if (ArticleViewer.this.windowView != null) {
                ArticleViewer.this.windowView.postDelayed(ArticleViewer.this.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
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
        private final Paint blackPaint = new Paint();
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

        public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
            DisplayCutout cutout;
            List<Rect> rects;
            WindowInsets oldInsets = (WindowInsets) ArticleViewer.this.lastInsets;
            Object unused = ArticleViewer.this.lastInsets = insets;
            if ((oldInsets == null || !oldInsets.toString().equals(insets.toString())) && ArticleViewer.this.windowView != null) {
                ArticleViewer.this.windowView.requestLayout();
            }
            if (!(Build.VERSION.SDK_INT < 28 || ArticleViewer.this.parentActivity == null || (cutout = ArticleViewer.this.parentActivity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout()) == null || (rects = cutout.getBoundingRects()) == null || rects.isEmpty())) {
                ArticleViewer articleViewer = ArticleViewer.this;
                boolean z = false;
                if (rects.get(0).height() != 0) {
                    z = true;
                }
                boolean unused2 = articleViewer.hasCutout = z;
            }
            return super.dispatchApplyWindowInsets(insets);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            if (Build.VERSION.SDK_INT < 21 || ArticleViewer.this.lastInsets == null) {
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
                int heightSize2 = heightSize - insets.getSystemWindowInsetBottom();
                widthSize -= insets.getSystemWindowInsetRight() + insets.getSystemWindowInsetLeft();
                if (insets.getSystemWindowInsetRight() != 0) {
                    this.bWidth = insets.getSystemWindowInsetRight();
                    this.bHeight = heightSize2;
                } else if (insets.getSystemWindowInsetLeft() != 0) {
                    this.bWidth = insets.getSystemWindowInsetLeft();
                    this.bHeight = heightSize2;
                } else {
                    this.bWidth = widthSize;
                    this.bHeight = insets.getStableInsetBottom();
                }
                heightSize = heightSize2 - insets.getSystemWindowInsetTop();
            }
            boolean z = false;
            ArticleViewer.this.menuButton.setAdditionalYOffset(((-(ArticleViewer.this.currentHeaderHeight - AndroidUtilities.dp(56.0f))) / 2) + (Build.VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0));
            ArticleViewer articleViewer = ArticleViewer.this;
            if (heightSize < AndroidUtilities.displaySize.y - AndroidUtilities.dp(100.0f)) {
                z = true;
            }
            boolean unused = articleViewer.keyboardVisible = z;
            ArticleViewer.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(heightSize, NUM));
            ArticleViewer.this.fullscreenVideoContainer.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(heightSize, NUM));
        }

        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (ArticleViewer.this.pinchToZoomHelper.isInOverlayMode()) {
                ev.offsetLocation(-ArticleViewer.this.containerView.getX(), -ArticleViewer.this.containerView.getY());
                return ArticleViewer.this.pinchToZoomHelper.onTouchEvent(ev);
            }
            TextSelectionHelper.TextSelectionOverlay selectionOverlay = ArticleViewer.this.textSelectionHelper.getOverlayView(getContext());
            MotionEvent textSelectionEv = MotionEvent.obtain(ev);
            textSelectionEv.offsetLocation(-ArticleViewer.this.containerView.getX(), -ArticleViewer.this.containerView.getY());
            if (ArticleViewer.this.textSelectionHelper.isSelectionMode() && ArticleViewer.this.textSelectionHelper.getOverlayView(getContext()).onTouchEvent(textSelectionEv)) {
                return true;
            }
            if (selectionOverlay.checkOnTap(ev)) {
                ev.setAction(3);
            }
            if (ev.getAction() != 0 || !ArticleViewer.this.textSelectionHelper.isSelectionMode() || (ev.getY() >= ((float) ArticleViewer.this.containerView.getTop()) && ev.getY() <= ((float) ArticleViewer.this.containerView.getBottom()))) {
                return super.dispatchTouchEvent(ev);
            }
            if (ArticleViewer.this.textSelectionHelper.getOverlayView(getContext()).onTouchEvent(textSelectionEv)) {
                return super.dispatchTouchEvent(ev);
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int x;
            if (!this.selfLayout) {
                int width = right - left;
                if (ArticleViewer.this.anchorsOffsetMeasuredWidth != width) {
                    for (int i = 0; i < ArticleViewer.this.listView.length; i++) {
                        for (Map.Entry<String, Integer> entry : ArticleViewer.this.adapter[i].anchorsOffset.entrySet()) {
                            entry.setValue(-1);
                        }
                    }
                    int unused = ArticleViewer.this.anchorsOffsetMeasuredWidth = width;
                }
                int y = 0;
                if (Build.VERSION.SDK_INT < 21 || ArticleViewer.this.lastInsets == null) {
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
                    y = 0 + insets.getSystemWindowInsetTop();
                }
                ArticleViewer.this.containerView.layout(x, y, ArticleViewer.this.containerView.getMeasuredWidth() + x, ArticleViewer.this.containerView.getMeasuredHeight() + y);
                ArticleViewer.this.fullscreenVideoContainer.layout(x, y, ArticleViewer.this.fullscreenVideoContainer.getMeasuredWidth() + x, ArticleViewer.this.fullscreenVideoContainer.getMeasuredHeight() + y);
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

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            handleTouchEvent((MotionEvent) null);
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return !ArticleViewer.this.collapsed && (handleTouchEvent(ev) || super.onInterceptTouchEvent(ev));
        }

        public boolean onTouchEvent(MotionEvent event) {
            return !ArticleViewer.this.collapsed && (handleTouchEvent(event) || super.onTouchEvent(event));
        }

        public void setInnerTranslationX(float value) {
            this.innerTranslationX = value;
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent((ArticleViewer.this.isVisible && this.alpha == 1.0f && this.innerTranslationX == 0.0f) ? false : true);
            }
            invalidate();
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            float opacity;
            Canvas canvas2 = canvas;
            int width = getMeasuredWidth();
            int translationX = (int) this.innerTranslationX;
            int restoreCount = canvas.save();
            canvas2.clipRect(translationX, 0, width, getHeight());
            boolean result = super.drawChild(canvas, child, drawingTime);
            canvas2.restoreToCount(restoreCount);
            if (translationX == 0) {
                View view = child;
            } else if (child == ArticleViewer.this.containerView) {
                float opacity2 = Math.min(0.8f, ((float) (width - translationX)) / ((float) width));
                if (opacity2 < 0.0f) {
                    opacity = 0.0f;
                } else {
                    opacity = opacity2;
                }
                ArticleViewer.this.scrimPaint.setColor(((int) (153.0f * opacity)) << 24);
                canvas.drawRect(0.0f, 0.0f, (float) translationX, (float) getHeight(), ArticleViewer.this.scrimPaint);
                float alpha2 = Math.max(0.0f, Math.min(((float) (width - translationX)) / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                ArticleViewer.this.layerShadowDrawable.setBounds(translationX - ArticleViewer.this.layerShadowDrawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                ArticleViewer.this.layerShadowDrawable.setAlpha((int) (255.0f * alpha2));
                ArticleViewer.this.layerShadowDrawable.draw(canvas2);
            }
            return result;
        }

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
                ArticleViewer articleViewer = ArticleViewer.this;
                articleViewer.updateInterfaceForCurrentPage((TLRPC.WebPage) articleViewer.pagesStack.get(ArticleViewer.this.pagesStack.size() - 2), true, -1);
            } else {
                this.movingPage = false;
            }
            ArticleViewer.this.cancelCheckLongPress();
        }

        public boolean handleTouchEvent(MotionEvent event) {
            float distToMove;
            MotionEvent motionEvent = event;
            if (ArticleViewer.this.pageSwitchAnimation != null || ArticleViewer.this.closeAnimationInProgress || ArticleViewer.this.fullscreenVideoContainer.getVisibility() == 0 || ArticleViewer.this.textSelectionHelper.isSelectionMode()) {
                return false;
            }
            if (motionEvent != null && event.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) event.getX();
                this.startedTrackingY = (int) event.getY();
                VelocityTracker velocityTracker = this.tracker;
                if (velocityTracker != null) {
                    velocityTracker.clear();
                }
            } else if (motionEvent != null && event.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.tracker == null) {
                    this.tracker = VelocityTracker.obtain();
                }
                int dx = Math.max(0, (int) (event.getX() - ((float) this.startedTrackingX)));
                int dy = Math.abs(((int) event.getY()) - this.startedTrackingY);
                this.tracker.addMovement(motionEvent);
                if (this.maybeStartTracking && !this.startedTracking && ((float) dx) >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(dx) / 3 > dy) {
                    prepareForMoving(event);
                } else if (this.startedTracking) {
                    DrawingText unused = ArticleViewer.this.pressedLinkOwnerLayout = null;
                    View unused2 = ArticleViewer.this.pressedLinkOwnerView = null;
                    if (this.movingPage) {
                        ArticleViewer.this.listView[0].setTranslationX((float) dx);
                    } else {
                        ArticleViewer.this.containerView.setTranslationX((float) dx);
                        setInnerTranslationX((float) dx);
                    }
                }
            } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (event.getAction() == 3 || event.getAction() == 1 || event.getAction() == 6)) {
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
                    View movingView = this.movingPage ? ArticleViewer.this.listView[0] : ArticleViewer.this.containerView;
                    float x = movingView.getX();
                    final boolean backAnimation = x < ((float) movingView.getMeasuredWidth()) / 3.0f && (velX < 3500.0f || velX < velY);
                    AnimatorSet animatorSet = new AnimatorSet();
                    if (!backAnimation) {
                        distToMove = ((float) movingView.getMeasuredWidth()) - x;
                        if (this.movingPage) {
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ArticleViewer.this.listView[0], View.TRANSLATION_X, new float[]{(float) movingView.getMeasuredWidth()})});
                        } else {
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ArticleViewer.this.containerView, View.TRANSLATION_X, new float[]{(float) movingView.getMeasuredWidth()}), ObjectAnimator.ofFloat(this, ArticleViewer.ARTICLE_VIEWER_INNER_TRANSLATION_X, new float[]{(float) movingView.getMeasuredWidth()})});
                        }
                    } else {
                        distToMove = x;
                        if (this.movingPage) {
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ArticleViewer.this.listView[0], View.TRANSLATION_X, new float[]{0.0f})});
                        } else {
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(ArticleViewer.this.containerView, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this, ArticleViewer.ARTICLE_VIEWER_INNER_TRANSLATION_X, new float[]{0.0f})});
                        }
                    }
                    animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) movingView.getMeasuredWidth())) * distToMove), 50));
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (WindowView.this.movingPage) {
                                ArticleViewer.this.listView[0].setBackgroundDrawable((Drawable) null);
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
                                    ArticleViewer.this.textSelectionHelper.setParentView(ArticleViewer.this.listView[0]);
                                    ArticleViewer.this.textSelectionHelper.layoutManager = ArticleViewer.this.layoutManager[0];
                                    ArticleViewer.this.titleTextView.setText(ArticleViewer.this.adapter[0].currentPage.site_name == null ? "" : ArticleViewer.this.adapter[0].currentPage.site_name);
                                    ArticleViewer.this.textSelectionHelper.clear(true);
                                    ArticleViewer.this.headerView.invalidate();
                                }
                                ArticleViewer.this.listView[1].setVisibility(8);
                                ArticleViewer.this.headerView.invalidate();
                            } else if (!backAnimation) {
                                ArticleViewer.this.saveCurrentPagePosition();
                                ArticleViewer.this.onClosed();
                            }
                            boolean unused = WindowView.this.movingPage = false;
                            boolean unused2 = WindowView.this.startedTracking = false;
                            boolean unused3 = ArticleViewer.this.closeAnimationInProgress = false;
                        }
                    });
                    animatorSet.start();
                    boolean unused3 = ArticleViewer.this.closeAnimationInProgress = true;
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
                if (ArticleViewer.this.textSelectionHelper != null && !ArticleViewer.this.textSelectionHelper.isSelectionMode()) {
                    ArticleViewer.this.textSelectionHelper.clear();
                }
            }
            return this.startedTracking;
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            int i;
            super.dispatchDraw(canvas);
            if ((Build.VERSION.SDK_INT < 21 || ArticleViewer.this.lastInsets == null) && this.bWidth != 0 && this.bHeight != 0) {
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
            int w = getMeasuredWidth();
            int h = getMeasuredHeight();
            Canvas canvas2 = canvas;
            canvas2.drawRect(this.innerTranslationX, 0.0f, (float) w, (float) h, ArticleViewer.this.backgroundPaint);
            if (Build.VERSION.SDK_INT >= 21 && ArticleViewer.this.lastInsets != null) {
                WindowInsets insets = (WindowInsets) ArticleViewer.this.lastInsets;
                Canvas canvas3 = canvas;
                canvas3.drawRect(this.innerTranslationX, 0.0f, (float) w, (float) insets.getSystemWindowInsetTop(), ArticleViewer.this.statusBarPaint);
                if (ArticleViewer.this.hasCutout) {
                    int left = insets.getSystemWindowInsetLeft();
                    if (left != 0) {
                        Canvas canvas4 = canvas;
                        canvas4.drawRect(0.0f, 0.0f, (float) left, (float) h, ArticleViewer.this.statusBarPaint);
                    }
                    int right = insets.getSystemWindowInsetRight();
                    if (right != 0) {
                        canvas.drawRect((float) (w - right), 0.0f, (float) w, (float) h, ArticleViewer.this.statusBarPaint);
                    }
                }
                Canvas canvas5 = canvas;
                canvas5.drawRect(0.0f, (float) (h - insets.getStableInsetBottom()), (float) w, (float) h, ArticleViewer.this.navigationBarPaint);
            }
        }

        public void setAlpha(float value) {
            ArticleViewer.this.backgroundPaint.setAlpha((int) (value * 255.0f));
            ArticleViewer.this.statusBarPaint.setAlpha((int) (255.0f * value));
            this.alpha = value;
            if (ArticleViewer.this.parentActivity instanceof LaunchActivity) {
                ((LaunchActivity) ArticleViewer.this.parentActivity).drawerLayoutContainer.setAllowDrawContent((ArticleViewer.this.isVisible && this.alpha == 1.0f && this.innerTranslationX == 0.0f) ? false : true);
            }
            invalidate();
        }

        public float getAlpha() {
            return this.alpha;
        }

        public boolean dispatchKeyEventPreIme(KeyEvent event) {
            if (event == null || event.getKeyCode() != 4 || event.getAction() != 1) {
                return super.dispatchKeyEventPreIme(event);
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

        public void run() {
            if (ArticleViewer.this.checkingForLongPress && ArticleViewer.this.windowView != null) {
                boolean unused = ArticleViewer.this.checkingForLongPress = false;
                if (ArticleViewer.this.pressedLink != null) {
                    ArticleViewer.this.windowView.performHapticFeedback(0, 2);
                    ArticleViewer articleViewer = ArticleViewer.this;
                    articleViewer.showCopyPopup(((TextPaintUrlSpan) articleViewer.pressedLink.getSpan()).getUrl());
                    LinkSpanDrawable unused2 = ArticleViewer.this.pressedLink = null;
                    DrawingText unused3 = ArticleViewer.this.pressedLinkOwnerLayout = null;
                    if (ArticleViewer.this.pressedLinkOwnerView != null) {
                        ArticleViewer.this.pressedLinkOwnerView.invalidate();
                    }
                } else if (ArticleViewer.this.pressedLinkOwnerView != null && ArticleViewer.this.textSelectionHelper.isSelectable(ArticleViewer.this.pressedLinkOwnerView)) {
                    if (ArticleViewer.this.pressedLinkOwnerView.getTag() == null || ArticleViewer.this.pressedLinkOwnerView.getTag() != "bottomSheet" || ArticleViewer.this.textSelectionHelperBottomSheet == null) {
                        ArticleViewer.this.textSelectionHelper.trySelect(ArticleViewer.this.pressedLinkOwnerView);
                    } else {
                        ArticleViewer.this.textSelectionHelperBottomSheet.trySelect(ArticleViewer.this.pressedLinkOwnerView);
                    }
                    if (ArticleViewer.this.textSelectionHelper.isSelectionMode()) {
                        ArticleViewer.this.windowView.performHapticFeedback(0, 2);
                    }
                } else if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLinkOwnerView != null) {
                    ArticleViewer.this.windowView.performHapticFeedback(0, 2);
                    int[] location = new int[2];
                    ArticleViewer.this.pressedLinkOwnerView.getLocationInWindow(location);
                    int y = (location[1] + ArticleViewer.this.pressedLayoutY) - AndroidUtilities.dp(54.0f);
                    if (y < 0) {
                        y = 0;
                    }
                    ArticleViewer.this.pressedLinkOwnerView.invalidate();
                    boolean unused4 = ArticleViewer.this.drawBlockSelection = true;
                    ArticleViewer articleViewer2 = ArticleViewer.this;
                    articleViewer2.showPopup(articleViewer2.pressedLinkOwnerView, 48, 0, y);
                    ArticleViewer.this.listView[0].setLayoutFrozen(true);
                    ArticleViewer.this.listView[0].setLayoutFrozen(false);
                }
            }
        }
    }

    private void createPaint(boolean update) {
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
        } else if (!update) {
            return;
        }
        int color2 = Theme.getColor("windowBackgroundWhite");
        webpageSearchPaint.setColor((((((float) Color.red(color2)) * 0.2126f) + (((float) Color.green(color2)) * 0.7152f)) + (((float) Color.blue(color2)) * 0.0722f)) / 255.0f <= 0.705f ? -3041234 : -6551);
        webpageUrlPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkSelection") & NUM);
        webpageUrlPaint.setPathEffect(LinkPath.getRoundedEffect());
        urlPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkSelection") & NUM);
        urlPaint.setPathEffect(LinkPath.getRoundedEffect());
        tableHalfLinePaint.setColor(Theme.getColor("windowBackgroundWhiteInputField"));
        tableLinePaint.setColor(Theme.getColor("windowBackgroundWhiteInputField"));
        photoBackgroundPaint.setColor(NUM);
        dividerPaint.setColor(Theme.getColor("divider"));
        webpageMarkPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkSelection") & NUM);
        webpageMarkPaint.setPathEffect(LinkPath.getRoundedEffect());
        int color = Theme.getColor("switchTrack");
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        tableStripPaint.setColor(Color.argb(20, r, g, b));
        tableHeaderPaint.setColor(Color.argb(34, r, g, b));
        int color3 = Theme.getColor("windowBackgroundWhiteLinkSelection");
        preformattedBackgroundPaint.setColor(Color.argb(20, Color.red(color3), Color.green(color3), Color.blue(color3)));
        quoteLinePaint.setColor(Theme.getColor("chat_inReplyLine"));
    }

    /* access modifiers changed from: private */
    public void showCopyPopup(String urlFinal) {
        if (this.parentActivity != null) {
            BottomSheet bottomSheet = this.linkSheet;
            if (bottomSheet != null) {
                bottomSheet.dismiss();
                this.linkSheet = null;
            }
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
            builder.setTitle(urlFinal);
            builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new ArticleViewer$$ExternalSyntheticLambda11(this, urlFinal));
            builder.setOnPreDismissListener(new ArticleViewer$$ExternalSyntheticLambda22(this));
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$showCopyPopup$0$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2697lambda$showCopyPopup$0$orgtelegramuiArticleViewer(String urlFinal, DialogInterface dialog, int which) {
        String webPageUrl;
        String anchor;
        if (this.parentActivity != null) {
            if (which == 0) {
                int lastIndexOf = urlFinal.lastIndexOf(35);
                int index = lastIndexOf;
                if (lastIndexOf != -1) {
                    if (!TextUtils.isEmpty(this.adapter[0].currentPage.cached_page.url)) {
                        webPageUrl = this.adapter[0].currentPage.cached_page.url.toLowerCase();
                    } else {
                        webPageUrl = this.adapter[0].currentPage.url.toLowerCase();
                    }
                    try {
                        anchor = URLDecoder.decode(urlFinal.substring(index + 1), "UTF-8");
                    } catch (Exception e) {
                        anchor = "";
                    }
                    if (urlFinal.toLowerCase().contains(webPageUrl)) {
                        if (TextUtils.isEmpty(anchor)) {
                            this.layoutManager[0].scrollToPositionWithOffset(0, 0);
                            checkScrollAnimated();
                            return;
                        }
                        scrollToAnchor(anchor);
                        return;
                    }
                }
                Browser.openUrl((Context) this.parentActivity, urlFinal);
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

    /* renamed from: lambda$showCopyPopup$1$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2698lambda$showCopyPopup$1$orgtelegramuiArticleViewer(DialogInterface di) {
        this.links.clear();
    }

    /* access modifiers changed from: private */
    public void showPopup(View parent, int gravity, int x, int y) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            if (this.popupLayout == null) {
                this.popupRect = new Rect();
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity);
                this.popupLayout = actionBarPopupWindowLayout;
                actionBarPopupWindowLayout.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                this.popupLayout.setBackgroundDrawable(this.parentActivity.getResources().getDrawable(NUM));
                this.popupLayout.setAnimationEnabled(false);
                this.popupLayout.setOnTouchListener(new ArticleViewer$$ExternalSyntheticLambda4(this));
                this.popupLayout.setDispatchKeyEventListener(new ArticleViewer$$ExternalSyntheticLambda35(this));
                this.popupLayout.setShownFromBottom(false);
                TextView textView = new TextView(this.parentActivity);
                this.deleteView = textView;
                textView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 2));
                this.deleteView.setGravity(16);
                this.deleteView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
                this.deleteView.setTextSize(1, 15.0f);
                this.deleteView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.deleteView.setText(LocaleController.getString("Copy", NUM).toUpperCase());
                this.deleteView.setOnClickListener(new ArticleViewer$$ExternalSyntheticLambda3(this));
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
                this.popupWindow.setOnDismissListener(new ArticleViewer$$ExternalSyntheticLambda6(this));
            }
            this.deleteView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = this.popupLayout;
            if (actionBarPopupWindowLayout2 != null) {
                actionBarPopupWindowLayout2.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
            }
            this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.popupWindow.setFocusable(true);
            this.popupWindow.showAtLocation(parent, gravity, x, y);
            this.popupWindow.startAnimation();
            return;
        }
        this.popupWindow.dismiss();
    }

    /* renamed from: lambda$showPopup$2$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ boolean m2700lambda$showPopup$2$orgtelegramuiArticleViewer(View v, MotionEvent event) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (event.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        v.getHitRect(this.popupRect);
        if (this.popupRect.contains((int) event.getX(), (int) event.getY())) {
            return false;
        }
        this.popupWindow.dismiss();
        return false;
    }

    /* renamed from: lambda$showPopup$3$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2701lambda$showPopup$3$orgtelegramuiArticleViewer(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    /* renamed from: lambda$showPopup$4$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2702lambda$showPopup$4$orgtelegramuiArticleViewer(View v) {
        DrawingText drawingText = this.pressedLinkOwnerLayout;
        if (drawingText != null) {
            AndroidUtilities.addToClipboard(drawingText.getText());
            if (Build.VERSION.SDK_INT < 31) {
                Toast.makeText(this.parentActivity, LocaleController.getString("TextCopied", NUM), 0).show();
            }
        }
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss(true);
        }
    }

    /* renamed from: lambda$showPopup$5$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2703lambda$showPopup$5$orgtelegramuiArticleViewer() {
        View view = this.pressedLinkOwnerView;
        if (view != null) {
            this.pressedLinkOwnerLayout = null;
            view.invalidate();
            this.pressedLinkOwnerView = null;
        }
    }

    /* access modifiers changed from: private */
    public TLRPC.RichText getBlockCaption(TLRPC.PageBlock block, int type) {
        if (type == 2) {
            TLRPC.RichText text1 = getBlockCaption(block, 0);
            if (text1 instanceof TLRPC.TL_textEmpty) {
                text1 = null;
            }
            TLRPC.RichText text2 = getBlockCaption(block, 1);
            if (text2 instanceof TLRPC.TL_textEmpty) {
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
            TLRPC.TL_textPlain text3 = new TLRPC.TL_textPlain();
            text3.text = " ";
            TLRPC.TL_textConcat textConcat = new TLRPC.TL_textConcat();
            textConcat.texts.add(text1);
            textConcat.texts.add(text3);
            textConcat.texts.add(text2);
            return textConcat;
        }
        if (block instanceof TLRPC.TL_pageBlockEmbedPost) {
            TLRPC.TL_pageBlockEmbedPost blockEmbedPost = (TLRPC.TL_pageBlockEmbedPost) block;
            if (type == 0) {
                return blockEmbedPost.caption.text;
            }
            if (type == 1) {
                return blockEmbedPost.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockSlideshow) {
            TLRPC.TL_pageBlockSlideshow pageBlockSlideshow = (TLRPC.TL_pageBlockSlideshow) block;
            if (type == 0) {
                return pageBlockSlideshow.caption.text;
            }
            if (type == 1) {
                return pageBlockSlideshow.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockPhoto) {
            TLRPC.TL_pageBlockPhoto pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) block;
            if (type == 0) {
                return pageBlockPhoto.caption.text;
            }
            if (type == 1) {
                return pageBlockPhoto.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockCollage) {
            TLRPC.TL_pageBlockCollage pageBlockCollage = (TLRPC.TL_pageBlockCollage) block;
            if (type == 0) {
                return pageBlockCollage.caption.text;
            }
            if (type == 1) {
                return pageBlockCollage.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockEmbed) {
            TLRPC.TL_pageBlockEmbed pageBlockEmbed = (TLRPC.TL_pageBlockEmbed) block;
            if (type == 0) {
                return pageBlockEmbed.caption.text;
            }
            if (type == 1) {
                return pageBlockEmbed.caption.credit;
            }
        } else if (block instanceof TLRPC.TL_pageBlockBlockquote) {
            return ((TLRPC.TL_pageBlockBlockquote) block).caption;
        } else {
            if (block instanceof TLRPC.TL_pageBlockVideo) {
                TLRPC.TL_pageBlockVideo pageBlockVideo = (TLRPC.TL_pageBlockVideo) block;
                if (type == 0) {
                    return pageBlockVideo.caption.text;
                }
                if (type == 1) {
                    return pageBlockVideo.caption.credit;
                }
            } else if (block instanceof TLRPC.TL_pageBlockPullquote) {
                return ((TLRPC.TL_pageBlockPullquote) block).caption;
            } else {
                if (block instanceof TLRPC.TL_pageBlockAudio) {
                    TLRPC.TL_pageBlockAudio pageBlockAudio = (TLRPC.TL_pageBlockAudio) block;
                    if (type == 0) {
                        return pageBlockAudio.caption.text;
                    }
                    if (type == 1) {
                        return pageBlockAudio.caption.credit;
                    }
                } else if (block instanceof TLRPC.TL_pageBlockCover) {
                    return getBlockCaption(((TLRPC.TL_pageBlockCover) block).cover, type);
                } else {
                    if (block instanceof TLRPC.TL_pageBlockMap) {
                        TLRPC.TL_pageBlockMap pageBlockMap = (TLRPC.TL_pageBlockMap) block;
                        if (type == 0) {
                            return pageBlockMap.caption.text;
                        }
                        if (type == 1) {
                            return pageBlockMap.caption.credit;
                        }
                    }
                }
            }
        }
        return null;
    }

    private View getLastNonListCell(View view) {
        if (view instanceof BlockListItemCell) {
            BlockListItemCell cell = (BlockListItemCell) view;
            if (cell.blockLayout != null) {
                return getLastNonListCell(cell.blockLayout.itemView);
            }
        } else if (view instanceof BlockOrderedListItemCell) {
            BlockOrderedListItemCell cell2 = (BlockOrderedListItemCell) view;
            if (cell2.blockLayout != null) {
                return getLastNonListCell(cell2.blockLayout.itemView);
            }
        }
        return view;
    }

    /* access modifiers changed from: private */
    public boolean isListItemBlock(TLRPC.PageBlock block) {
        return (block instanceof TL_pageBlockListItem) || (block instanceof TL_pageBlockOrderedListItem);
    }

    /* access modifiers changed from: private */
    public TLRPC.PageBlock getLastNonListPageBlock(TLRPC.PageBlock block) {
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
        TLRPC.PageBlock parentBlock = getLastNonListPageBlock(child.parent);
        if (parentBlock instanceof TLRPC.TL_pageBlockDetails) {
            TLRPC.TL_pageBlockDetails blockDetails = (TLRPC.TL_pageBlockDetails) parentBlock;
            if (blockDetails.open) {
                return false;
            }
            blockDetails.open = true;
            return true;
        } else if (!(parentBlock instanceof TL_pageBlockDetailsChild)) {
            return false;
        } else {
            TL_pageBlockDetailsChild parent = (TL_pageBlockDetailsChild) parentBlock;
            TLRPC.PageBlock parentBlock2 = getLastNonListPageBlock(parent.block);
            boolean opened = false;
            if (parentBlock2 instanceof TLRPC.TL_pageBlockDetails) {
                TLRPC.TL_pageBlockDetails blockDetails2 = (TLRPC.TL_pageBlockDetails) parentBlock2;
                if (!blockDetails2.open) {
                    blockDetails2.open = true;
                    opened = true;
                }
            }
            if (openAllParentBlocks(parent) || opened) {
                return true;
            }
            return false;
        }
    }

    /* access modifiers changed from: private */
    public TLRPC.PageBlock fixListBlock(TLRPC.PageBlock parentBlock, TLRPC.PageBlock childBlock) {
        if (parentBlock instanceof TL_pageBlockListItem) {
            TLRPC.PageBlock unused = ((TL_pageBlockListItem) parentBlock).blockItem = childBlock;
            return parentBlock;
        } else if (!(parentBlock instanceof TL_pageBlockOrderedListItem)) {
            return childBlock;
        } else {
            TLRPC.PageBlock unused2 = ((TL_pageBlockOrderedListItem) parentBlock).blockItem = childBlock;
            return parentBlock;
        }
    }

    /* access modifiers changed from: private */
    public TLRPC.PageBlock wrapInTableBlock(TLRPC.PageBlock parentBlock, TLRPC.PageBlock childBlock) {
        if (parentBlock instanceof TL_pageBlockListItem) {
            TL_pageBlockListItem parent = (TL_pageBlockListItem) parentBlock;
            TL_pageBlockListItem item = new TL_pageBlockListItem();
            TL_pageBlockListParent unused = item.parent = parent.parent;
            TLRPC.PageBlock unused2 = item.blockItem = wrapInTableBlock(parent.blockItem, childBlock);
            return item;
        } else if (!(parentBlock instanceof TL_pageBlockOrderedListItem)) {
            return childBlock;
        } else {
            TL_pageBlockOrderedListItem parent2 = (TL_pageBlockOrderedListItem) parentBlock;
            TL_pageBlockOrderedListItem item2 = new TL_pageBlockOrderedListItem();
            TL_pageBlockOrderedListParent unused3 = item2.parent = parent2.parent;
            TLRPC.PageBlock unused4 = item2.blockItem = wrapInTableBlock(parent2.blockItem, childBlock);
            return item2;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x029b  */
    /* JADX WARNING: Removed duplicated region for block: B:88:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateInterfaceForCurrentPage(org.telegram.tgnet.TLRPC.WebPage r20, boolean r21, int r22) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = r22
            if (r1 == 0) goto L_0x029f
            org.telegram.tgnet.TLRPC$Page r3 = r1.cached_page
            if (r3 != 0) goto L_0x000e
            goto L_0x029f
        L_0x000e:
            r4 = 2
            r5 = 1
            r6 = 0
            if (r21 != 0) goto L_0x0143
            if (r2 == 0) goto L_0x0143
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r7 = r0.adapter
            r8 = r7[r5]
            r9 = r7[r6]
            r7[r5] = r9
            r7[r6] = r8
            org.telegram.ui.Components.RecyclerListView[] r7 = r0.listView
            r9 = r7[r5]
            r10 = r7[r6]
            r7[r5] = r10
            r7[r6] = r9
            androidx.recyclerview.widget.LinearLayoutManager[] r10 = r0.layoutManager
            r11 = r10[r5]
            r12 = r10[r6]
            r10[r5] = r12
            r10[r6] = r11
            android.widget.FrameLayout r10 = r0.containerView
            r7 = r7[r6]
            int r7 = r10.indexOfChild(r7)
            android.widget.FrameLayout r10 = r0.containerView
            org.telegram.ui.Components.RecyclerListView[] r12 = r0.listView
            r12 = r12[r5]
            int r10 = r10.indexOfChild(r12)
            if (r2 != r5) goto L_0x005c
            if (r7 >= r10) goto L_0x0070
            android.widget.FrameLayout r12 = r0.containerView
            org.telegram.ui.Components.RecyclerListView[] r13 = r0.listView
            r13 = r13[r6]
            r12.removeView(r13)
            android.widget.FrameLayout r12 = r0.containerView
            org.telegram.ui.Components.RecyclerListView[] r13 = r0.listView
            r13 = r13[r6]
            r12.addView(r13, r10)
            goto L_0x0070
        L_0x005c:
            if (r10 >= r7) goto L_0x0070
            android.widget.FrameLayout r12 = r0.containerView
            org.telegram.ui.Components.RecyclerListView[] r13 = r0.listView
            r13 = r13[r6]
            r12.removeView(r13)
            android.widget.FrameLayout r12 = r0.containerView
            org.telegram.ui.Components.RecyclerListView[] r13 = r0.listView
            r13 = r13[r6]
            r12.addView(r13, r7)
        L_0x0070:
            android.animation.AnimatorSet r12 = new android.animation.AnimatorSet
            r12.<init>()
            r0.pageSwitchAnimation = r12
            org.telegram.ui.Components.RecyclerListView[] r12 = r0.listView
            r12 = r12[r6]
            r12.setVisibility(r6)
            if (r2 != r5) goto L_0x0082
            r12 = 0
            goto L_0x0083
        L_0x0082:
            r12 = 1
        L_0x0083:
            org.telegram.ui.Components.RecyclerListView[] r13 = r0.listView
            r13 = r13[r12]
            android.graphics.Paint r14 = r0.backgroundPaint
            int r14 = r14.getColor()
            r13.setBackgroundColor(r14)
            int r13 = android.os.Build.VERSION.SDK_INT
            r14 = 18
            if (r13 < r14) goto L_0x009e
            org.telegram.ui.Components.RecyclerListView[] r13 = r0.listView
            r13 = r13[r12]
            r14 = 0
            r13.setLayerType(r4, r14)
        L_0x009e:
            r13 = 1113587712(0x42600000, float:56.0)
            if (r2 != r5) goto L_0x00d9
            android.animation.AnimatorSet r15 = r0.pageSwitchAnimation
            android.animation.Animator[] r3 = new android.animation.Animator[r4]
            org.telegram.ui.Components.RecyclerListView[] r5 = r0.listView
            r5 = r5[r6]
            android.util.Property r14 = android.view.View.TRANSLATION_X
            float[] r6 = new float[r4]
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r17 = 0
            r6[r17] = r13
            r13 = 0
            r16 = 1
            r6[r16] = r13
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r14, r6)
            r3[r17] = r5
            org.telegram.ui.Components.RecyclerListView[] r5 = r0.listView
            r5 = r5[r17]
            android.util.Property r6 = android.view.View.ALPHA
            float[] r13 = new float[r4]
            r13 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r13)
            r3[r16] = r5
            r15.playTogether(r3)
            r18 = r7
            goto L_0x0126
        L_0x00d9:
            r3 = -1
            if (r2 != r3) goto L_0x0124
            org.telegram.ui.Components.RecyclerListView[] r3 = r0.listView
            r5 = 0
            r3 = r3[r5]
            r6 = 1065353216(0x3var_, float:1.0)
            r3.setAlpha(r6)
            org.telegram.ui.Components.RecyclerListView[] r3 = r0.listView
            r3 = r3[r5]
            r6 = 0
            r3.setTranslationX(r6)
            android.animation.AnimatorSet r3 = r0.pageSwitchAnimation
            android.animation.Animator[] r14 = new android.animation.Animator[r4]
            org.telegram.ui.Components.RecyclerListView[] r15 = r0.listView
            r16 = 1
            r15 = r15[r16]
            android.util.Property r13 = android.view.View.TRANSLATION_X
            r18 = r7
            float[] r7 = new float[r4]
            r7[r5] = r6
            r6 = 1113587712(0x42600000, float:56.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            r7[r16] = r6
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r15, r13, r7)
            r14[r5] = r6
            org.telegram.ui.Components.RecyclerListView[] r5 = r0.listView
            r5 = r5[r16]
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r4]
            r7 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r14[r16] = r5
            r3.playTogether(r14)
            goto L_0x0126
        L_0x0124:
            r18 = r7
        L_0x0126:
            android.animation.AnimatorSet r3 = r0.pageSwitchAnimation
            r5 = 150(0x96, double:7.4E-322)
            r3.setDuration(r5)
            android.animation.AnimatorSet r3 = r0.pageSwitchAnimation
            android.view.animation.DecelerateInterpolator r5 = r0.interpolator
            r3.setInterpolator(r5)
            android.animation.AnimatorSet r3 = r0.pageSwitchAnimation
            org.telegram.ui.ArticleViewer$2 r5 = new org.telegram.ui.ArticleViewer$2
            r5.<init>(r12)
            r3.addListener(r5)
            android.animation.AnimatorSet r3 = r0.pageSwitchAnimation
            r3.start()
        L_0x0143:
            if (r21 != 0) goto L_0x015e
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.titleTextView
            java.lang.String r5 = r1.site_name
            if (r5 != 0) goto L_0x014e
            java.lang.String r5 = ""
            goto L_0x0150
        L_0x014e:
            java.lang.String r5 = r1.site_name
        L_0x0150:
            r3.setText(r5)
            org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r3 = r0.textSelectionHelper
            r5 = 1
            r3.clear(r5)
            android.widget.FrameLayout r3 = r0.headerView
            r3.invalidate()
        L_0x015e:
            r3 = r21
            if (r21 == 0) goto L_0x0170
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WebPage> r5 = r0.pagesStack
            int r6 = r5.size()
            int r6 = r6 - r4
            java.lang.Object r4 = r5.get(r6)
            org.telegram.tgnet.TLRPC$WebPage r4 = (org.telegram.tgnet.TLRPC.WebPage) r4
            goto L_0x0171
        L_0x0170:
            r4 = r1
        L_0x0171:
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r5 = r0.adapter
            r5 = r5[r3]
            org.telegram.tgnet.TLRPC$Page r6 = r1.cached_page
            boolean r6 = r6.rtl
            boolean unused = r5.isRtl = r6
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r5 = r0.adapter
            r5 = r5[r3]
            r5.cleanup()
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r5 = r0.adapter
            r5 = r5[r3]
            org.telegram.tgnet.TLRPC.WebPage unused = r5.currentPage = r4
            r5 = 0
            org.telegram.tgnet.TLRPC$Page r6 = r4.cached_page
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r6 = r6.blocks
            int r6 = r6.size()
            r7 = 0
        L_0x0194:
            if (r7 >= r6) goto L_0x0204
            org.telegram.tgnet.TLRPC$Page r8 = r4.cached_page
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r8 = r8.blocks
            java.lang.Object r8 = r8.get(r7)
            org.telegram.tgnet.TLRPC$PageBlock r8 = (org.telegram.tgnet.TLRPC.PageBlock) r8
            if (r7 != 0) goto L_0x01de
            r9 = 1
            r8.first = r9
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCover
            if (r10 == 0) goto L_0x01ec
            r10 = r8
            org.telegram.tgnet.TLRPC$TL_pageBlockCover r10 = (org.telegram.tgnet.TLRPC.TL_pageBlockCover) r10
            r11 = 0
            org.telegram.tgnet.TLRPC$RichText r12 = r0.getBlockCaption(r10, r11)
            org.telegram.tgnet.TLRPC$RichText r11 = r0.getBlockCaption(r10, r9)
            if (r12 == 0) goto L_0x01bb
            boolean r9 = r12 instanceof org.telegram.tgnet.TLRPC.TL_textEmpty
            if (r9 == 0) goto L_0x01c1
        L_0x01bb:
            if (r11 == 0) goto L_0x01dd
            boolean r9 = r11 instanceof org.telegram.tgnet.TLRPC.TL_textEmpty
            if (r9 != 0) goto L_0x01dd
        L_0x01c1:
            r9 = 1
            if (r6 <= r9) goto L_0x01dd
            org.telegram.tgnet.TLRPC$Page r13 = r4.cached_page
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r13 = r13.blocks
            java.lang.Object r13 = r13.get(r9)
            r9 = r13
            org.telegram.tgnet.TLRPC$PageBlock r9 = (org.telegram.tgnet.TLRPC.PageBlock) r9
            boolean r13 = r9 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockChannel
            if (r13 == 0) goto L_0x01dd
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r13 = r0.adapter
            r13 = r13[r3]
            r14 = r9
            org.telegram.tgnet.TLRPC$TL_pageBlockChannel r14 = (org.telegram.tgnet.TLRPC.TL_pageBlockChannel) r14
            org.telegram.tgnet.TLRPC.TL_pageBlockChannel unused = r13.channelBlock = r14
        L_0x01dd:
            goto L_0x01ec
        L_0x01de:
            r9 = 1
            if (r7 != r9) goto L_0x01ec
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r9 = r0.adapter
            r9 = r9[r3]
            org.telegram.tgnet.TLRPC$TL_pageBlockChannel r9 = r9.channelBlock
            if (r9 == 0) goto L_0x01ec
            goto L_0x0201
        L_0x01ec:
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r9 = r0.adapter
            r10 = r9[r3]
            r11 = r9[r3]
            r12 = 0
            r13 = 0
            int r9 = r6 + -1
            if (r7 != r9) goto L_0x01fa
            r14 = r7
            goto L_0x01fb
        L_0x01fa:
            r14 = 0
        L_0x01fb:
            r9 = r10
            r10 = r11
            r11 = r8
            r9.addBlock(r10, r11, r12, r13, r14)
        L_0x0201:
            int r7 = r7 + 1
            goto L_0x0194
        L_0x0204:
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r7 = r0.adapter
            r7 = r7[r3]
            r7.notifyDataSetChanged()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WebPage> r7 = r0.pagesStack
            int r7 = r7.size()
            r8 = 1
            if (r7 == r8) goto L_0x0223
            r7 = -1
            if (r2 != r7) goto L_0x0219
            r8 = 0
            goto L_0x0224
        L_0x0219:
            androidx.recyclerview.widget.LinearLayoutManager[] r7 = r0.layoutManager
            r7 = r7[r3]
            r8 = 0
            r7.scrollToPositionWithOffset(r8, r8)
            goto L_0x0299
        L_0x0223:
            r8 = 0
        L_0x0224:
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r9 = "articles"
            android.content.SharedPreferences r7 = r7.getSharedPreferences(r9, r8)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "article"
            r8.append(r9)
            long r9 = r4.id
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r9 = -1
            int r10 = r7.getInt(r8, r9)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r8)
            java.lang.String r11 = "r"
            r9.append(r11)
            java.lang.String r9 = r9.toString()
            r11 = 1
            boolean r9 = r7.getBoolean(r9, r11)
            android.graphics.Point r12 = org.telegram.messenger.AndroidUtilities.displaySize
            int r12 = r12.x
            android.graphics.Point r13 = org.telegram.messenger.AndroidUtilities.displaySize
            int r13 = r13.y
            if (r12 <= r13) goto L_0x0265
            goto L_0x0266
        L_0x0265:
            r11 = 0
        L_0x0266:
            if (r9 != r11) goto L_0x0288
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r8)
            java.lang.String r11 = "o"
            r9.append(r11)
            java.lang.String r9 = r9.toString()
            r11 = 0
            int r9 = r7.getInt(r9, r11)
            org.telegram.ui.Components.RecyclerListView[] r11 = r0.listView
            r11 = r11[r3]
            int r11 = r11.getPaddingTop()
            int r9 = r9 - r11
            goto L_0x028e
        L_0x0288:
            r9 = 1092616192(0x41200000, float:10.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
        L_0x028e:
            r11 = -1
            if (r10 == r11) goto L_0x0298
            androidx.recyclerview.widget.LinearLayoutManager[] r11 = r0.layoutManager
            r11 = r11[r3]
            r11.scrollToPositionWithOffset(r10, r9)
        L_0x0298:
        L_0x0299:
            if (r21 != 0) goto L_0x029e
            r19.checkScrollAnimated()
        L_0x029e:
            return
        L_0x029f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.updateInterfaceForCurrentPage(org.telegram.tgnet.TLRPC$WebPage, boolean, int):void");
    }

    private boolean addPageToStack(TLRPC.WebPage webPage, String anchor, int order) {
        saveCurrentPagePosition();
        this.pagesStack.add(webPage);
        showSearch(false);
        updateInterfaceForCurrentPage(webPage, false, order);
        return scrollToAnchor(anchor);
    }

    private boolean scrollToAnchor(String anchor) {
        if (TextUtils.isEmpty(anchor)) {
            return false;
        }
        String anchor2 = anchor.toLowerCase();
        Integer row = (Integer) this.adapter[0].anchors.get(anchor2);
        if (row == null) {
            return false;
        }
        TLRPC.TL_textAnchor textAnchor = (TLRPC.TL_textAnchor) this.adapter[0].anchorsParent.get(anchor2);
        if (textAnchor != null) {
            TLRPC.TL_pageBlockParagraph paragraph = new TLRPC.TL_pageBlockParagraph();
            paragraph.text = textAnchor.text;
            int type = this.adapter[0].getTypeForBlock(paragraph);
            RecyclerView.ViewHolder holder = this.adapter[0].onCreateViewHolder((ViewGroup) null, type);
            this.adapter[0].bindBlockToHolder(type, holder, paragraph, 0, 0);
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
            builder.setApplyTopPadding(false);
            builder.setApplyBottomPadding(false);
            final LinearLayout linearLayout = new LinearLayout(this.parentActivity);
            linearLayout.setOrientation(1);
            TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = new TextSelectionHelper.ArticleTextSelectionHelper();
            this.textSelectionHelperBottomSheet = articleTextSelectionHelper;
            articleTextSelectionHelper.setParentView(linearLayout);
            this.textSelectionHelperBottomSheet.setCallback(new TextSelectionHelper.Callback() {
                public void onStateChanged(boolean isSelected) {
                    if (ArticleViewer.this.linkSheet != null) {
                        ArticleViewer.this.linkSheet.setDisableScroll(isSelected);
                    }
                }
            });
            TextView textView = new TextView(this.parentActivity) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
                    super.onDraw(canvas);
                }
            };
            textView.setTextSize(1, 16.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setText(LocaleController.getString("InstantViewReference", NUM));
            textView.setGravity((this.adapter[0].isRtl ? 5 : 3) | 16);
            textView.setTextColor(getTextColor());
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            linearLayout.addView(textView, new LinearLayout.LayoutParams(-1, AndroidUtilities.dp(48.0f) + 1));
            holder.itemView.setTag("bottomSheet");
            linearLayout.addView(holder.itemView, LayoutHelper.createLinear(-1, -2, 0.0f, 7.0f, 0.0f, 0.0f));
            View overlayView = this.textSelectionHelperBottomSheet.getOverlayView(this.parentActivity);
            FrameLayout frameLayout = new FrameLayout(this.parentActivity) {
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    TextSelectionHelper.TextSelectionOverlay selectionOverlay = ArticleViewer.this.textSelectionHelperBottomSheet.getOverlayView(getContext());
                    MotionEvent textSelectionEv = MotionEvent.obtain(ev);
                    textSelectionEv.offsetLocation(-linearLayout.getX(), -linearLayout.getY());
                    if (ArticleViewer.this.textSelectionHelperBottomSheet.isSelectionMode() && ArticleViewer.this.textSelectionHelperBottomSheet.getOverlayView(getContext()).onTouchEvent(textSelectionEv)) {
                        return true;
                    }
                    if (selectionOverlay.checkOnTap(ev)) {
                        ev.setAction(3);
                    }
                    if (ev.getAction() != 0 || !ArticleViewer.this.textSelectionHelperBottomSheet.isSelectionMode() || (ev.getY() >= ((float) linearLayout.getTop()) && ev.getY() <= ((float) linearLayout.getBottom()))) {
                        return super.dispatchTouchEvent(ev);
                    }
                    if (ArticleViewer.this.textSelectionHelperBottomSheet.getOverlayView(getContext()).onTouchEvent(textSelectionEv)) {
                        return super.dispatchTouchEvent(ev);
                    }
                    return true;
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(linearLayout.getMeasuredHeight() + AndroidUtilities.dp(8.0f), NUM));
                }
            };
            builder.setDelegate(new BottomSheet.BottomSheetDelegate() {
                public boolean canDismiss() {
                    if (ArticleViewer.this.textSelectionHelperBottomSheet == null || !ArticleViewer.this.textSelectionHelperBottomSheet.isSelectionMode()) {
                        return true;
                    }
                    ArticleViewer.this.textSelectionHelperBottomSheet.clear();
                    return false;
                }
            });
            frameLayout.addView(linearLayout, -1, -2);
            frameLayout.addView(overlayView, -1, -2);
            builder.setCustomView(frameLayout);
            if (this.textSelectionHelper.isSelectionMode()) {
                this.textSelectionHelper.clear();
            }
            BottomSheet create = builder.create();
            this.linkSheet = create;
            showDialog(create);
        } else if (row.intValue() < 0 || row.intValue() >= this.adapter[0].blocks.size()) {
            return false;
        } else {
            TLRPC.PageBlock originalBlock = (TLRPC.PageBlock) this.adapter[0].blocks.get(row.intValue());
            TLRPC.PageBlock block = getLastNonListPageBlock(originalBlock);
            if ((block instanceof TL_pageBlockDetailsChild) && openAllParentBlocks((TL_pageBlockDetailsChild) block)) {
                this.adapter[0].updateRows();
                this.adapter[0].notifyDataSetChanged();
            }
            int position = this.adapter[0].localBlocks.indexOf(originalBlock);
            if (position != -1) {
                row = Integer.valueOf(position);
            }
            Integer offset = (Integer) this.adapter[0].anchorsOffset.get(anchor2);
            if (offset == null) {
                offset = 0;
            } else if (offset.intValue() == -1) {
                int type2 = this.adapter[0].getTypeForBlock(originalBlock);
                RecyclerView.ViewHolder holder2 = this.adapter[0].onCreateViewHolder((ViewGroup) null, type2);
                int i = type2;
                int i2 = position;
                this.adapter[0].bindBlockToHolder(type2, holder2, originalBlock, 0, 0);
                holder2.itemView.measure(View.MeasureSpec.makeMeasureSpec(this.listView[0].getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                Integer offset2 = (Integer) this.adapter[0].anchorsOffset.get(anchor2);
                if (offset2.intValue() == -1) {
                    offset = 0;
                } else {
                    offset = offset2;
                }
            }
            this.layoutManager[0].scrollToPositionWithOffset(row.intValue(), (this.currentHeaderHeight - AndroidUtilities.dp(56.0f)) - offset.intValue());
        }
        return true;
    }

    private boolean removeLastPageFromStack() {
        if (this.pagesStack.size() < 2) {
            return false;
        }
        ArrayList<TLRPC.WebPage> arrayList = this.pagesStack;
        arrayList.remove(arrayList.size() - 1);
        ArrayList<TLRPC.WebPage> arrayList2 = this.pagesStack;
        updateInterfaceForCurrentPage(arrayList2.get(arrayList2.size() - 1), false, -1);
        return true;
    }

    /* access modifiers changed from: protected */
    public void startCheckLongPress(float x, float y, View parentView) {
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper;
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap();
            }
            if (parentView.getTag() == null || parentView.getTag() != "bottomSheet" || (articleTextSelectionHelper = this.textSelectionHelperBottomSheet) == null) {
                this.textSelectionHelper.setMaybeView((int) x, (int) y, parentView);
            } else {
                articleTextSelectionHelper.setMaybeView((int) x, (int) y, parentView);
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

    private int getTextFlags(TLRPC.RichText richText) {
        if (richText instanceof TLRPC.TL_textFixed) {
            return getTextFlags(richText.parentRichText) | 4;
        }
        if (richText instanceof TLRPC.TL_textItalic) {
            return getTextFlags(richText.parentRichText) | 2;
        }
        if (richText instanceof TLRPC.TL_textBold) {
            return getTextFlags(richText.parentRichText) | 1;
        }
        if (richText instanceof TLRPC.TL_textUnderline) {
            return getTextFlags(richText.parentRichText) | 16;
        }
        if (richText instanceof TLRPC.TL_textStrike) {
            return getTextFlags(richText.parentRichText) | 32;
        }
        if (richText instanceof TLRPC.TL_textEmail) {
            return getTextFlags(richText.parentRichText) | 8;
        }
        if (richText instanceof TLRPC.TL_textPhone) {
            return getTextFlags(richText.parentRichText) | 8;
        }
        if (richText instanceof TLRPC.TL_textUrl) {
            if (((TLRPC.TL_textUrl) richText).webpage_id != 0) {
                return getTextFlags(richText.parentRichText) | 512;
            }
            return getTextFlags(richText.parentRichText) | 8;
        } else if (richText instanceof TLRPC.TL_textSubscript) {
            return getTextFlags(richText.parentRichText) | 128;
        } else {
            if (richText instanceof TLRPC.TL_textSuperscript) {
                return getTextFlags(richText.parentRichText) | 256;
            }
            if (richText instanceof TLRPC.TL_textMarked) {
                return getTextFlags(richText.parentRichText) | 64;
            }
            if (richText != null) {
                return getTextFlags(richText.parentRichText);
            }
            return 0;
        }
    }

    private TLRPC.RichText getLastRichText(TLRPC.RichText richText) {
        if (richText == null) {
            return null;
        }
        if (richText instanceof TLRPC.TL_textFixed) {
            return getLastRichText(((TLRPC.TL_textFixed) richText).text);
        }
        if (richText instanceof TLRPC.TL_textItalic) {
            return getLastRichText(((TLRPC.TL_textItalic) richText).text);
        }
        if (richText instanceof TLRPC.TL_textBold) {
            return getLastRichText(((TLRPC.TL_textBold) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUnderline) {
            return getLastRichText(((TLRPC.TL_textUnderline) richText).text);
        }
        if (richText instanceof TLRPC.TL_textStrike) {
            return getLastRichText(((TLRPC.TL_textStrike) richText).text);
        }
        if (richText instanceof TLRPC.TL_textEmail) {
            return getLastRichText(((TLRPC.TL_textEmail) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUrl) {
            return getLastRichText(((TLRPC.TL_textUrl) richText).text);
        }
        if (richText instanceof TLRPC.TL_textAnchor) {
            getLastRichText(((TLRPC.TL_textAnchor) richText).text);
        } else if (richText instanceof TLRPC.TL_textSubscript) {
            return getLastRichText(((TLRPC.TL_textSubscript) richText).text);
        } else {
            if (richText instanceof TLRPC.TL_textSuperscript) {
                return getLastRichText(((TLRPC.TL_textSuperscript) richText).text);
            }
            if (richText instanceof TLRPC.TL_textMarked) {
                return getLastRichText(((TLRPC.TL_textMarked) richText).text);
            }
            if (richText instanceof TLRPC.TL_textPhone) {
                return getLastRichText(((TLRPC.TL_textPhone) richText).text);
            }
        }
        return richText;
    }

    /* access modifiers changed from: private */
    public CharSequence getText(WebpageAdapter adapter2, View parentView, TLRPC.RichText parentRichText, TLRPC.RichText richText, TLRPC.PageBlock parentBlock, int maxWidth) {
        return getText(adapter2.currentPage, parentView, parentRichText, richText, parentBlock, maxWidth);
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: private */
    public CharSequence getText(TLRPC.WebPage page, View parentView, TLRPC.RichText parentRichText, TLRPC.RichText richText, TLRPC.PageBlock parentBlock, int maxWidth) {
        MetricAffectingSpan span;
        MetricAffectingSpan span2;
        TLRPC.RichText richText2 = parentRichText;
        TLRPC.RichText richText3 = richText;
        TLRPC.PageBlock pageBlock = parentBlock;
        TextPaint textPaint = null;
        if (richText3 == null) {
            return null;
        }
        if (richText3 instanceof TLRPC.TL_textFixed) {
            return getText(page, parentView, parentRichText, ((TLRPC.TL_textFixed) richText3).text, parentBlock, maxWidth);
        } else if (richText3 instanceof TLRPC.TL_textItalic) {
            return getText(page, parentView, parentRichText, ((TLRPC.TL_textItalic) richText3).text, parentBlock, maxWidth);
        } else if (richText3 instanceof TLRPC.TL_textBold) {
            return getText(page, parentView, parentRichText, ((TLRPC.TL_textBold) richText3).text, parentBlock, maxWidth);
        } else if (richText3 instanceof TLRPC.TL_textUnderline) {
            return getText(page, parentView, parentRichText, ((TLRPC.TL_textUnderline) richText3).text, parentBlock, maxWidth);
        } else if (richText3 instanceof TLRPC.TL_textStrike) {
            return getText(page, parentView, parentRichText, ((TLRPC.TL_textStrike) richText3).text, parentBlock, maxWidth);
        } else if (richText3 instanceof TLRPC.TL_textEmail) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getText(page, parentView, parentRichText, ((TLRPC.TL_textEmail) richText3).text, parentBlock, maxWidth));
            MetricAffectingSpan[] innerSpans = (MetricAffectingSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), MetricAffectingSpan.class);
            if (spannableStringBuilder.length() != 0) {
                if (innerSpans == null || innerSpans.length == 0) {
                    textPaint = getTextPaint(richText2, richText3, pageBlock);
                }
                spannableStringBuilder.setSpan(new TextPaintUrlSpan(textPaint, "mailto:" + getUrl(richText)), 0, spannableStringBuilder.length(), 33);
            }
            return spannableStringBuilder;
        } else {
            long j = 0;
            if (richText3 instanceof TLRPC.TL_textUrl) {
                TLRPC.TL_textUrl textUrl = (TLRPC.TL_textUrl) richText3;
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(getText(page, parentView, parentRichText, ((TLRPC.TL_textUrl) richText3).text, parentBlock, maxWidth));
                MetricAffectingSpan[] innerSpans2 = (MetricAffectingSpan[]) spannableStringBuilder2.getSpans(0, spannableStringBuilder2.length(), MetricAffectingSpan.class);
                TextPaint paint = (innerSpans2 == null || innerSpans2.length == 0) ? getTextPaint(richText2, richText3, pageBlock) : null;
                if (textUrl.webpage_id != 0) {
                    span2 = new TextPaintWebpageUrlSpan(paint, getUrl(richText));
                } else {
                    span2 = new TextPaintUrlSpan(paint, getUrl(richText));
                }
                if (spannableStringBuilder2.length() != 0) {
                    spannableStringBuilder2.setSpan(span2, 0, spannableStringBuilder2.length(), 33);
                }
                return spannableStringBuilder2;
            } else if (richText3 instanceof TLRPC.TL_textPlain) {
                return ((TLRPC.TL_textPlain) richText3).text;
            } else {
                if (richText3 instanceof TLRPC.TL_textAnchor) {
                    TLRPC.TL_textAnchor textAnchor = (TLRPC.TL_textAnchor) richText3;
                    SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(getText(page, parentView, parentRichText, textAnchor.text, parentBlock, maxWidth));
                    spannableStringBuilder3.setSpan(new AnchorSpan(textAnchor.name), 0, spannableStringBuilder3.length(), 17);
                    return spannableStringBuilder3;
                } else if (richText3 instanceof TLRPC.TL_textEmpty) {
                    return "";
                } else {
                    if (richText3 instanceof TLRPC.TL_textConcat) {
                        SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder();
                        int count = richText3.texts.size();
                        int a = 0;
                        while (a < count) {
                            TLRPC.RichText innerRichText = richText3.texts.get(a);
                            TLRPC.RichText lastRichText = getLastRichText(innerRichText);
                            boolean extraSpace = maxWidth >= 0 && (innerRichText instanceof TLRPC.TL_textUrl) && ((TLRPC.TL_textUrl) innerRichText).webpage_id != j;
                            if (!(!extraSpace || spannableStringBuilder4.length() == 0 || spannableStringBuilder4.charAt(spannableStringBuilder4.length() - 1) == 10)) {
                                spannableStringBuilder4.append(" ");
                                spannableStringBuilder4.setSpan(new TextSelectionHelper.IgnoreCopySpannable(), spannableStringBuilder4.length() - 1, spannableStringBuilder4.length(), 33);
                            }
                            CharSequence charSequence = " ";
                            TLRPC.RichText lastRichText2 = lastRichText;
                            TLRPC.RichText innerRichText2 = innerRichText;
                            int a2 = a;
                            int count2 = count;
                            CharSequence innerText = getText(page, parentView, parentRichText, innerRichText, parentBlock, maxWidth);
                            int flags = getTextFlags(lastRichText2);
                            int startLength = spannableStringBuilder4.length();
                            spannableStringBuilder4.append(innerText);
                            if (flags != 0 && !(innerText instanceof SpannableStringBuilder)) {
                                if ((flags & 8) != 0 || (flags & 512) != 0) {
                                    String url = getUrl(innerRichText2);
                                    if (url == null) {
                                        url = getUrl(parentRichText);
                                    }
                                    if ((flags & 512) != 0) {
                                        span = new TextPaintWebpageUrlSpan(getTextPaint(richText2, lastRichText2, pageBlock), url);
                                    } else {
                                        span = new TextPaintUrlSpan(getTextPaint(richText2, lastRichText2, pageBlock), url);
                                    }
                                    if (startLength != spannableStringBuilder4.length()) {
                                        spannableStringBuilder4.setSpan(span, startLength, spannableStringBuilder4.length(), 33);
                                    }
                                } else if (startLength != spannableStringBuilder4.length()) {
                                    spannableStringBuilder4.setSpan(new TextPaintSpan(getTextPaint(richText2, lastRichText2, pageBlock)), startLength, spannableStringBuilder4.length(), 33);
                                }
                            }
                            if (extraSpace && a2 != count2 - 1) {
                                spannableStringBuilder4.append(charSequence);
                                spannableStringBuilder4.setSpan(new TextSelectionHelper.IgnoreCopySpannable(), spannableStringBuilder4.length() - 1, spannableStringBuilder4.length(), 33);
                            }
                            a = a2 + 1;
                            count = count2;
                            j = 0;
                        }
                        return spannableStringBuilder4;
                    } else if (richText3 instanceof TLRPC.TL_textSubscript) {
                        return getText(page, parentView, parentRichText, ((TLRPC.TL_textSubscript) richText3).text, parentBlock, maxWidth);
                    } else if (richText3 instanceof TLRPC.TL_textSuperscript) {
                        return getText(page, parentView, parentRichText, ((TLRPC.TL_textSuperscript) richText3).text, parentBlock, maxWidth);
                    } else if (richText3 instanceof TLRPC.TL_textMarked) {
                        SpannableStringBuilder spannableStringBuilder5 = new SpannableStringBuilder(getText(page, parentView, parentRichText, ((TLRPC.TL_textMarked) richText3).text, parentBlock, maxWidth));
                        MetricAffectingSpan[] innerSpans3 = (MetricAffectingSpan[]) spannableStringBuilder5.getSpans(0, spannableStringBuilder5.length(), MetricAffectingSpan.class);
                        if (spannableStringBuilder5.length() != 0) {
                            spannableStringBuilder5.setSpan(new TextPaintMarkSpan((innerSpans3 == null || innerSpans3.length == 0) ? getTextPaint(richText2, richText3, pageBlock) : null), 0, spannableStringBuilder5.length(), 33);
                        }
                        return spannableStringBuilder5;
                    } else if (richText3 instanceof TLRPC.TL_textPhone) {
                        try {
                            SpannableStringBuilder spannableStringBuilder6 = new SpannableStringBuilder(getText(page, parentView, parentRichText, ((TLRPC.TL_textPhone) richText3).text, parentBlock, maxWidth));
                            MetricAffectingSpan[] innerSpans4 = (MetricAffectingSpan[]) spannableStringBuilder6.getSpans(0, spannableStringBuilder6.length(), MetricAffectingSpan.class);
                            if (spannableStringBuilder6.length() != 0) {
                                spannableStringBuilder6.setSpan(new TextPaintUrlSpan((innerSpans4 == null || innerSpans4.length == 0) ? getTextPaint(richText2, richText3, pageBlock) : null, "tel:" + getUrl(richText)), 0, spannableStringBuilder6.length(), 33);
                            }
                            return spannableStringBuilder6;
                        } catch (Throwable th) {
                            throw th;
                        }
                    } else if (richText3 instanceof TLRPC.TL_textImage) {
                        TLRPC.TL_textImage textImage = (TLRPC.TL_textImage) richText3;
                        TLRPC.Document document = WebPageUtils.getDocumentWithId(page, textImage.document_id);
                        if (document == null) {
                            return "";
                        }
                        SpannableStringBuilder spannableStringBuilder7 = new SpannableStringBuilder("*");
                        int w = AndroidUtilities.dp((float) textImage.w);
                        int h = AndroidUtilities.dp((float) textImage.h);
                        int maxWidth2 = Math.abs(maxWidth);
                        if (w > maxWidth2) {
                            float scale = ((float) maxWidth2) / ((float) w);
                            w = maxWidth2;
                            h = (int) (((float) h) * scale);
                        }
                        if (parentView != null) {
                            int color = Theme.getColor("windowBackgroundWhite");
                            spannableStringBuilder7.setSpan(new TextPaintImageReceiverSpan(parentView, document, page, w, h, false, (((((float) Color.red(color)) * 0.2126f) + (((float) Color.green(color)) * 0.7152f)) + (((float) Color.blue(color)) * 0.0722f)) / 255.0f <= 0.705f), 0, spannableStringBuilder7.length(), 33);
                        }
                        return spannableStringBuilder7;
                    } else {
                        TLRPC.WebPage webPage = page;
                        return "not supported " + richText3;
                    }
                }
            }
        }
    }

    public static CharSequence getPlainText(TLRPC.RichText richText) {
        if (richText == null) {
            return "";
        }
        if (richText instanceof TLRPC.TL_textFixed) {
            return getPlainText(((TLRPC.TL_textFixed) richText).text);
        }
        if (richText instanceof TLRPC.TL_textItalic) {
            return getPlainText(((TLRPC.TL_textItalic) richText).text);
        }
        if (richText instanceof TLRPC.TL_textBold) {
            return getPlainText(((TLRPC.TL_textBold) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUnderline) {
            return getPlainText(((TLRPC.TL_textUnderline) richText).text);
        }
        if (richText instanceof TLRPC.TL_textStrike) {
            return getPlainText(((TLRPC.TL_textStrike) richText).text);
        }
        if (richText instanceof TLRPC.TL_textEmail) {
            return getPlainText(((TLRPC.TL_textEmail) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUrl) {
            return getPlainText(((TLRPC.TL_textUrl) richText).text);
        }
        if (richText instanceof TLRPC.TL_textPlain) {
            return ((TLRPC.TL_textPlain) richText).text;
        }
        if (richText instanceof TLRPC.TL_textAnchor) {
            return getPlainText(((TLRPC.TL_textAnchor) richText).text);
        }
        if (richText instanceof TLRPC.TL_textEmpty) {
            return "";
        }
        if (richText instanceof TLRPC.TL_textConcat) {
            StringBuilder stringBuilder = new StringBuilder();
            int count = richText.texts.size();
            for (int a = 0; a < count; a++) {
                stringBuilder.append(getPlainText(richText.texts.get(a)));
            }
            return stringBuilder;
        } else if ((richText instanceof TLRPC.TL_textSubscript) != 0) {
            return getPlainText(((TLRPC.TL_textSubscript) richText).text);
        } else {
            if (richText instanceof TLRPC.TL_textSuperscript) {
                return getPlainText(((TLRPC.TL_textSuperscript) richText).text);
            }
            if (richText instanceof TLRPC.TL_textMarked) {
                return getPlainText(((TLRPC.TL_textMarked) richText).text);
            }
            if (richText instanceof TLRPC.TL_textPhone) {
                return getPlainText(((TLRPC.TL_textPhone) richText).text);
            }
            boolean z = richText instanceof TLRPC.TL_textImage;
            return "";
        }
    }

    public static String getUrl(TLRPC.RichText richText) {
        if (richText instanceof TLRPC.TL_textFixed) {
            return getUrl(((TLRPC.TL_textFixed) richText).text);
        }
        if (richText instanceof TLRPC.TL_textItalic) {
            return getUrl(((TLRPC.TL_textItalic) richText).text);
        }
        if (richText instanceof TLRPC.TL_textBold) {
            return getUrl(((TLRPC.TL_textBold) richText).text);
        }
        if (richText instanceof TLRPC.TL_textUnderline) {
            return getUrl(((TLRPC.TL_textUnderline) richText).text);
        }
        if (richText instanceof TLRPC.TL_textStrike) {
            return getUrl(((TLRPC.TL_textStrike) richText).text);
        }
        if (richText instanceof TLRPC.TL_textEmail) {
            return ((TLRPC.TL_textEmail) richText).email;
        }
        if (richText instanceof TLRPC.TL_textUrl) {
            return ((TLRPC.TL_textUrl) richText).url;
        }
        if (richText instanceof TLRPC.TL_textPhone) {
            return ((TLRPC.TL_textPhone) richText).phone;
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

    private TextPaint getTextPaint(TLRPC.RichText parentRichText, TLRPC.RichText richText, TLRPC.PageBlock parentBlock) {
        SparseArray<TextPaint> currentMap;
        int textSize;
        SparseArray<TextPaint> currentMap2;
        int textSize2;
        SparseArray<TextPaint> currentMap3;
        int textSize3;
        SparseArray<TextPaint> currentMap4;
        int textSize4;
        SparseArray<TextPaint> currentMap5;
        int textSize5;
        SparseArray<TextPaint> currentMap6;
        int textSize6;
        int flags = getTextFlags(richText);
        SparseArray<TextPaint> currentMap7 = null;
        int textSize7 = AndroidUtilities.dp(14.0f);
        int textColor = -65536;
        int additionalSize = AndroidUtilities.dp((float) (SharedConfig.ivFontSize - 16));
        if (parentBlock instanceof TLRPC.TL_pageBlockPhoto) {
            TLRPC.TL_pageBlockPhoto pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) parentBlock;
            if (pageBlockPhoto.caption.text == richText || pageBlockPhoto.caption.text == parentRichText) {
                currentMap7 = photoCaptionTextPaints;
                textSize7 = AndroidUtilities.dp(14.0f);
            } else {
                currentMap7 = photoCreditTextPaints;
                textSize7 = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockMap) {
            TLRPC.TL_pageBlockMap pageBlockMap = (TLRPC.TL_pageBlockMap) parentBlock;
            if (pageBlockMap.caption.text == richText || pageBlockMap.caption.text == parentRichText) {
                currentMap6 = photoCaptionTextPaints;
                textSize6 = AndroidUtilities.dp(14.0f);
            } else {
                currentMap6 = photoCreditTextPaints;
                textSize6 = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockTitle) {
            currentMap7 = titleTextPaints;
            textSize7 = AndroidUtilities.dp(23.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockKicker) {
            currentMap7 = kickerTextPaints;
            textSize7 = AndroidUtilities.dp(14.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockAuthorDate) {
            currentMap7 = authorTextPaints;
            textSize7 = AndroidUtilities.dp(14.0f);
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockFooter) {
            currentMap7 = footerTextPaints;
            textSize7 = AndroidUtilities.dp(14.0f);
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockSubtitle) {
            currentMap7 = subtitleTextPaints;
            textSize7 = AndroidUtilities.dp(20.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockHeader) {
            currentMap7 = headerTextPaints;
            textSize7 = AndroidUtilities.dp(20.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockSubheader) {
            currentMap7 = subheaderTextPaints;
            textSize7 = AndroidUtilities.dp(17.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockBlockquote) {
            TLRPC.TL_pageBlockBlockquote pageBlockBlockquote = (TLRPC.TL_pageBlockBlockquote) parentBlock;
            if (pageBlockBlockquote.text == parentRichText) {
                currentMap7 = quoteTextPaints;
                textSize7 = AndroidUtilities.dp(15.0f);
                textColor = getTextColor();
            } else if (pageBlockBlockquote.caption == parentRichText) {
                currentMap7 = photoCaptionTextPaints;
                textSize7 = AndroidUtilities.dp(14.0f);
                textColor = getGrayTextColor();
            }
        } else if (parentBlock instanceof TLRPC.TL_pageBlockPullquote) {
            TLRPC.TL_pageBlockPullquote pageBlockBlockquote2 = (TLRPC.TL_pageBlockPullquote) parentBlock;
            if (pageBlockBlockquote2.text == parentRichText) {
                currentMap7 = quoteTextPaints;
                textSize7 = AndroidUtilities.dp(15.0f);
                textColor = getTextColor();
            } else if (pageBlockBlockquote2.caption == parentRichText) {
                currentMap7 = photoCaptionTextPaints;
                textSize7 = AndroidUtilities.dp(14.0f);
                textColor = getGrayTextColor();
            }
        } else if (parentBlock instanceof TLRPC.TL_pageBlockPreformatted) {
            currentMap7 = preformattedTextPaints;
            textSize7 = AndroidUtilities.dp(14.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockParagraph) {
            currentMap7 = paragraphTextPaints;
            textSize7 = AndroidUtilities.dp(16.0f);
            textColor = getTextColor();
        } else if (isListItemBlock(parentBlock)) {
            currentMap7 = listTextPaints;
            textSize7 = AndroidUtilities.dp(16.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockEmbed) {
            TLRPC.TL_pageBlockEmbed pageBlockEmbed = (TLRPC.TL_pageBlockEmbed) parentBlock;
            if (pageBlockEmbed.caption.text == richText || pageBlockEmbed.caption.text == parentRichText) {
                currentMap5 = photoCaptionTextPaints;
                textSize5 = AndroidUtilities.dp(14.0f);
            } else {
                currentMap5 = photoCreditTextPaints;
                textSize5 = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockSlideshow) {
            TLRPC.TL_pageBlockSlideshow pageBlockSlideshow = (TLRPC.TL_pageBlockSlideshow) parentBlock;
            if (pageBlockSlideshow.caption.text == richText || pageBlockSlideshow.caption.text == parentRichText) {
                currentMap4 = photoCaptionTextPaints;
                textSize4 = AndroidUtilities.dp(14.0f);
            } else {
                currentMap4 = photoCreditTextPaints;
                textSize4 = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockCollage) {
            TLRPC.TL_pageBlockCollage pageBlockCollage = (TLRPC.TL_pageBlockCollage) parentBlock;
            if (pageBlockCollage.caption.text == richText || pageBlockCollage.caption.text == parentRichText) {
                currentMap3 = photoCaptionTextPaints;
                textSize3 = AndroidUtilities.dp(14.0f);
            } else {
                currentMap3 = photoCreditTextPaints;
                textSize3 = AndroidUtilities.dp(12.0f);
            }
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockEmbedPost) {
            TLRPC.TL_pageBlockEmbedPost pageBlockEmbedPost = (TLRPC.TL_pageBlockEmbedPost) parentBlock;
            if (richText == pageBlockEmbedPost.caption.text) {
                currentMap7 = photoCaptionTextPaints;
                textSize7 = AndroidUtilities.dp(14.0f);
                textColor = getGrayTextColor();
            } else if (richText == pageBlockEmbedPost.caption.credit) {
                currentMap7 = photoCreditTextPaints;
                textSize7 = AndroidUtilities.dp(12.0f);
                textColor = getGrayTextColor();
            } else if (richText != null) {
                currentMap7 = embedPostTextPaints;
                textSize7 = AndroidUtilities.dp(14.0f);
                textColor = getTextColor();
            }
        } else if (parentBlock instanceof TLRPC.TL_pageBlockVideo) {
            if (richText == ((TLRPC.TL_pageBlockVideo) parentBlock).caption.text) {
                currentMap2 = mediaCaptionTextPaints;
                textSize2 = AndroidUtilities.dp(14.0f);
            } else {
                currentMap2 = mediaCreditTextPaints;
                textSize2 = AndroidUtilities.dp(12.0f);
            }
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockAudio) {
            if (richText == ((TLRPC.TL_pageBlockAudio) parentBlock).caption.text) {
                currentMap = mediaCaptionTextPaints;
                textSize = AndroidUtilities.dp(14.0f);
            } else {
                currentMap = mediaCreditTextPaints;
                textSize = AndroidUtilities.dp(12.0f);
            }
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockRelatedArticles) {
            currentMap7 = relatedArticleTextPaints;
            textSize7 = AndroidUtilities.dp(15.0f);
            textColor = getGrayTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockDetails) {
            currentMap7 = detailsTextPaints;
            textSize7 = AndroidUtilities.dp(15.0f);
            textColor = getTextColor();
        } else if (parentBlock instanceof TLRPC.TL_pageBlockTable) {
            currentMap7 = tableTextPaints;
            textSize7 = AndroidUtilities.dp(15.0f);
            textColor = getTextColor();
        }
        if (!((flags & 256) == 0 && (flags & 128) == 0)) {
            textSize7 -= AndroidUtilities.dp(4.0f);
        }
        if (currentMap7 == null) {
            if (errorTextPaint == null) {
                TextPaint textPaint = new TextPaint(1);
                errorTextPaint = textPaint;
                textPaint.setColor(-65536);
            }
            errorTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            return errorTextPaint;
        }
        TextPaint paint = currentMap7.get(flags);
        if (paint == null) {
            paint = new TextPaint(1);
            if ((flags & 4) != 0) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
            } else if (parentBlock instanceof TLRPC.TL_pageBlockRelatedArticles) {
                paint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            } else if (this.selectedFont == 1 || (parentBlock instanceof TLRPC.TL_pageBlockTitle) || (parentBlock instanceof TLRPC.TL_pageBlockKicker) || (parentBlock instanceof TLRPC.TL_pageBlockHeader) || (parentBlock instanceof TLRPC.TL_pageBlockSubtitle) || (parentBlock instanceof TLRPC.TL_pageBlockSubheader)) {
                if ((parentBlock instanceof TLRPC.TL_pageBlockTitle) || (parentBlock instanceof TLRPC.TL_pageBlockHeader) || (parentBlock instanceof TLRPC.TL_pageBlockSubtitle) || (parentBlock instanceof TLRPC.TL_pageBlockSubheader)) {
                    paint.setTypeface(AndroidUtilities.getTypeface("fonts/mw_bold.ttf"));
                } else if ((flags & 1) != 0 && (flags & 2) != 0) {
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
            currentMap7.put(flags, paint);
        }
        paint.setTextSize((float) (textSize7 + additionalSize));
        return paint;
    }

    /* access modifiers changed from: private */
    public DrawingText createLayoutForText(View parentView, CharSequence plainText, TLRPC.RichText richText, int width, int textY, TLRPC.PageBlock parentBlock, Layout.Alignment align, WebpageAdapter parentAdapter) {
        return createLayoutForText(parentView, plainText, richText, width, 0, parentBlock, align, 0, parentAdapter);
    }

    /* access modifiers changed from: private */
    public DrawingText createLayoutForText(View parentView, CharSequence plainText, TLRPC.RichText richText, int width, int textY, TLRPC.PageBlock parentBlock, WebpageAdapter parentAdapter) {
        return createLayoutForText(parentView, plainText, richText, width, textY, parentBlock, Layout.Alignment.ALIGN_NORMAL, 0, parentAdapter);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x035d A[Catch:{ Exception -> 0x03a0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03c1 A[Catch:{ Exception -> 0x0406 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.telegram.ui.ArticleViewer.DrawingText createLayoutForText(android.view.View r24, java.lang.CharSequence r25, org.telegram.tgnet.TLRPC.RichText r26, int r27, int r28, org.telegram.tgnet.TLRPC.PageBlock r29, android.text.Layout.Alignment r30, int r31, org.telegram.ui.ArticleViewer.WebpageAdapter r32) {
        /*
            r23 = this;
            r8 = r23
            r9 = r25
            r10 = r26
            r11 = r29
            r0 = 0
            if (r9 != 0) goto L_0x0012
            if (r10 == 0) goto L_0x0011
            boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.TL_textEmpty
            if (r1 == 0) goto L_0x0012
        L_0x0011:
            return r0
        L_0x0012:
            if (r27 >= 0) goto L_0x001d
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r22 = r1
            goto L_0x001f
        L_0x001d:
            r22 = r27
        L_0x001f:
            if (r9 == 0) goto L_0x0024
            r1 = r25
            goto L_0x0036
        L_0x0024:
            r1 = r23
            r2 = r32
            r3 = r24
            r4 = r26
            r5 = r26
            r6 = r29
            r7 = r22
            java.lang.CharSequence r1 = r1.getText((org.telegram.ui.ArticleViewer.WebpageAdapter) r2, (android.view.View) r3, (org.telegram.tgnet.TLRPC.RichText) r4, (org.telegram.tgnet.TLRPC.RichText) r5, (org.telegram.tgnet.TLRPC.PageBlock) r6, (int) r7)
        L_0x0036:
            boolean r2 = android.text.TextUtils.isEmpty(r1)
            if (r2 == 0) goto L_0x003d
            return r0
        L_0x003d:
            int r2 = org.telegram.messenger.SharedConfig.ivFontSize
            int r2 = r2 + -16
            float r2 = (float) r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            boolean r3 = r11 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost
            r4 = 1096810496(0x41600000, float:14.0)
            r5 = 1097859072(0x41700000, float:15.0)
            r6 = 1
            if (r3 == 0) goto L_0x0099
            if (r10 != 0) goto L_0x0099
            r3 = r11
            org.telegram.tgnet.TLRPC$TL_pageBlockEmbedPost r3 = (org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost) r3
            java.lang.String r7 = r3.author
            if (r7 != r9) goto L_0x0078
            android.text.TextPaint r4 = embedPostAuthorPaint
            if (r4 != 0) goto L_0x006a
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r6)
            embedPostAuthorPaint = r4
            int r7 = r23.getTextColor()
            r4.setColor(r7)
        L_0x006a:
            android.text.TextPaint r4 = embedPostAuthorPaint
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r5 + r2
            float r5 = (float) r5
            r4.setTextSize(r5)
            android.text.TextPaint r4 = embedPostAuthorPaint
            goto L_0x0097
        L_0x0078:
            android.text.TextPaint r5 = embedPostDatePaint
            if (r5 != 0) goto L_0x008a
            android.text.TextPaint r5 = new android.text.TextPaint
            r5.<init>(r6)
            embedPostDatePaint = r5
            int r7 = getGrayTextColor()
            r5.setColor(r7)
        L_0x008a:
            android.text.TextPaint r5 = embedPostDatePaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r2
            float r4 = (float) r4
            r5.setTextSize(r4)
            android.text.TextPaint r4 = embedPostDatePaint
        L_0x0097:
            goto L_0x01b9
        L_0x0099:
            boolean r3 = r11 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockChannel
            java.lang.String r7 = "fonts/rmedium.ttf"
            if (r3 == 0) goto L_0x00f0
            android.text.TextPaint r3 = channelNamePaint
            if (r3 != 0) goto L_0x00bf
            android.text.TextPaint r3 = new android.text.TextPaint
            r3.<init>(r6)
            channelNamePaint = r3
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r3.setTypeface(r4)
            android.text.TextPaint r3 = new android.text.TextPaint
            r3.<init>(r6)
            channelNamePhotoPaint = r3
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r3.setTypeface(r4)
        L_0x00bf:
            android.text.TextPaint r3 = channelNamePaint
            int r4 = r23.getTextColor()
            r3.setColor(r4)
            android.text.TextPaint r3 = channelNamePaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r4 = (float) r4
            r3.setTextSize(r4)
            android.text.TextPaint r3 = channelNamePhotoPaint
            r4 = -1
            r3.setColor(r4)
            android.text.TextPaint r3 = channelNamePhotoPaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r4 = (float) r4
            r3.setTextSize(r4)
            org.telegram.tgnet.TLRPC$TL_pageBlockChannel r3 = r32.channelBlock
            if (r3 == 0) goto L_0x00eb
            android.text.TextPaint r3 = channelNamePhotoPaint
            goto L_0x00ed
        L_0x00eb:
            android.text.TextPaint r3 = channelNamePaint
        L_0x00ed:
            r4 = r3
            goto L_0x01b9
        L_0x00f0:
            boolean r3 = r11 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockRelatedArticlesChild
            if (r3 == 0) goto L_0x0156
            r3 = r11
            org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesChild r3 = (org.telegram.ui.ArticleViewer.TL_pageBlockRelatedArticlesChild) r3
            org.telegram.tgnet.TLRPC$TL_pageBlockRelatedArticles r12 = r3.parent
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pageRelatedArticle> r12 = r12.articles
            int r13 = r3.num
            java.lang.Object r12 = r12.get(r13)
            org.telegram.tgnet.TLRPC$TL_pageRelatedArticle r12 = (org.telegram.tgnet.TLRPC.TL_pageRelatedArticle) r12
            java.lang.String r12 = r12.title
            if (r9 != r12) goto L_0x0134
            android.text.TextPaint r4 = relatedArticleHeaderPaint
            if (r4 != 0) goto L_0x011d
            android.text.TextPaint r4 = new android.text.TextPaint
            r4.<init>(r6)
            relatedArticleHeaderPaint = r4
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r4.setTypeface(r7)
        L_0x011d:
            android.text.TextPaint r4 = relatedArticleHeaderPaint
            int r7 = r23.getTextColor()
            r4.setColor(r7)
            android.text.TextPaint r4 = relatedArticleHeaderPaint
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r5 + r2
            float r5 = (float) r5
            r4.setTextSize(r5)
            android.text.TextPaint r4 = relatedArticleHeaderPaint
            goto L_0x0155
        L_0x0134:
            android.text.TextPaint r5 = relatedArticleTextPaint
            if (r5 != 0) goto L_0x013f
            android.text.TextPaint r5 = new android.text.TextPaint
            r5.<init>(r6)
            relatedArticleTextPaint = r5
        L_0x013f:
            android.text.TextPaint r5 = relatedArticleTextPaint
            int r7 = getGrayTextColor()
            r5.setColor(r7)
            android.text.TextPaint r5 = relatedArticleTextPaint
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r2
            float r4 = (float) r4
            r5.setTextSize(r4)
            android.text.TextPaint r4 = relatedArticleTextPaint
        L_0x0155:
            goto L_0x01b9
        L_0x0156:
            boolean r3 = r8.isListItemBlock(r11)
            if (r3 == 0) goto L_0x01b5
            if (r9 == 0) goto L_0x01b5
            android.text.TextPaint r3 = listTextPointerPaint
            if (r3 != 0) goto L_0x0170
            android.text.TextPaint r3 = new android.text.TextPaint
            r3.<init>(r6)
            listTextPointerPaint = r3
            int r4 = r23.getTextColor()
            r3.setColor(r4)
        L_0x0170:
            android.text.TextPaint r3 = listTextNumPaint
            if (r3 != 0) goto L_0x0182
            android.text.TextPaint r3 = new android.text.TextPaint
            r3.<init>(r6)
            listTextNumPaint = r3
            int r4 = r23.getTextColor()
            r3.setColor(r4)
        L_0x0182:
            android.text.TextPaint r3 = listTextPointerPaint
            r4 = 1100480512(0x41980000, float:19.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r2
            float r4 = (float) r4
            r3.setTextSize(r4)
            android.text.TextPaint r3 = listTextNumPaint
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r2
            float r4 = (float) r4
            r3.setTextSize(r4)
            boolean r3 = r11 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockListItem
            if (r3 == 0) goto L_0x01b2
            r3 = r11
            org.telegram.ui.ArticleViewer$TL_pageBlockListItem r3 = (org.telegram.ui.ArticleViewer.TL_pageBlockListItem) r3
            org.telegram.ui.ArticleViewer$TL_pageBlockListParent r3 = r3.parent
            org.telegram.tgnet.TLRPC$TL_pageBlockList r3 = r3.pageBlockList
            boolean r3 = r3.ordered
            if (r3 != 0) goto L_0x01b2
            android.text.TextPaint r4 = listTextPointerPaint
            goto L_0x01b9
        L_0x01b2:
            android.text.TextPaint r4 = listTextNumPaint
            goto L_0x01b9
        L_0x01b5:
            android.text.TextPaint r4 = r8.getTextPaint(r10, r10, r11)
        L_0x01b9:
            r3 = 1082130432(0x40800000, float:4.0)
            r5 = 0
            if (r31 == 0) goto L_0x01f6
            boolean r7 = r11 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockPullquote
            if (r7 == 0) goto L_0x01da
            android.text.Layout$Alignment r15 = android.text.Layout.Alignment.ALIGN_CENTER
            r16 = 1065353216(0x3var_, float:1.0)
            r17 = 0
            r18 = 0
            android.text.TextUtils$TruncateAt r19 = android.text.TextUtils.TruncateAt.END
            r12 = r1
            r13 = r4
            r14 = r22
            r20 = r22
            r21 = r31
            android.text.StaticLayout r3 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)
            goto L_0x023b
        L_0x01da:
            r16 = 1065353216(0x3var_, float:1.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r18 = 0
            android.text.TextUtils$TruncateAt r19 = android.text.TextUtils.TruncateAt.END
            r12 = r1
            r13 = r4
            r14 = r22
            r15 = r30
            r17 = r3
            r20 = r22
            r21 = r31
            android.text.StaticLayout r3 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)
            goto L_0x023b
        L_0x01f6:
            int r7 = r1.length()
            int r7 = r7 - r6
            char r7 = r1.charAt(r7)
            r12 = 10
            if (r7 != r12) goto L_0x020c
            int r7 = r1.length()
            int r7 = r7 - r6
            java.lang.CharSequence r1 = r1.subSequence(r5, r7)
        L_0x020c:
            boolean r7 = r11 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockPullquote
            if (r7 == 0) goto L_0x0223
            android.text.StaticLayout r3 = new android.text.StaticLayout
            android.text.Layout$Alignment r16 = android.text.Layout.Alignment.ALIGN_CENTER
            r17 = 1065353216(0x3var_, float:1.0)
            r18 = 0
            r19 = 0
            r12 = r3
            r13 = r1
            r14 = r4
            r15 = r22
            r12.<init>(r13, r14, r15, r16, r17, r18, r19)
            goto L_0x023b
        L_0x0223:
            android.text.StaticLayout r7 = new android.text.StaticLayout
            r17 = 1065353216(0x3var_, float:1.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r19 = 0
            r12 = r7
            r13 = r1
            r14 = r4
            r15 = r22
            r16 = r30
            r18 = r3
            r12.<init>(r13, r14, r15, r16, r17, r18, r19)
            r3 = r7
        L_0x023b:
            if (r3 != 0) goto L_0x023e
            return r0
        L_0x023e:
            java.lang.CharSequence r7 = r3.getText()
            r12 = 0
            r13 = 0
            if (r28 < 0) goto L_0x02be
            if (r3 == 0) goto L_0x02be
            java.util.ArrayList<org.telegram.ui.ArticleViewer$SearchResult> r0 = r8.searchResults
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x02be
            java.lang.String r0 = r8.searchText
            if (r0 == 0) goto L_0x02be
            java.lang.String r0 = r1.toString()
            java.lang.String r0 = r0.toLowerCase()
            r14 = 0
        L_0x025d:
            java.lang.String r15 = r8.searchText
            int r15 = r0.indexOf(r15, r14)
            r27 = r15
            if (r15 < 0) goto L_0x02ba
            java.lang.String r15 = r8.searchText
            int r15 = r15.length()
            r6 = r27
            int r14 = r6 + r15
            if (r6 == 0) goto L_0x0283
            int r15 = r6 + -1
            char r15 = r0.charAt(r15)
            boolean r15 = org.telegram.messenger.AndroidUtilities.isPunctuationCharacter(r15)
            if (r15 == 0) goto L_0x0280
            goto L_0x0283
        L_0x0280:
            r17 = r0
            goto L_0x02b5
        L_0x0283:
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r15 = r8.adapter
            r15 = r15[r5]
            java.util.HashMap r15 = r15.searchTextOffset
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r17 = r0
            java.lang.String r0 = r8.searchText
            r5.append(r0)
            r5.append(r11)
            r5.append(r10)
            r5.append(r6)
            java.lang.String r0 = r5.toString()
            int r5 = r3.getLineForOffset(r6)
            int r5 = r3.getLineTop(r5)
            int r5 = r28 + r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r15.put(r0, r5)
        L_0x02b5:
            r0 = r17
            r5 = 0
            r6 = 1
            goto L_0x025d
        L_0x02ba:
            r6 = r27
            r17 = r0
        L_0x02be:
            if (r3 == 0) goto L_0x040c
            boolean r0 = r7 instanceof android.text.Spanned
            if (r0 == 0) goto L_0x040c
            r5 = r7
            android.text.Spanned r5 = (android.text.Spanned) r5
            int r0 = r5.length()     // Catch:{ Exception -> 0x0336 }
            java.lang.Class<org.telegram.ui.Components.AnchorSpan> r6 = org.telegram.ui.Components.AnchorSpan.class
            r14 = 0
            java.lang.Object[] r0 = r5.getSpans(r14, r0, r6)     // Catch:{ Exception -> 0x0336 }
            org.telegram.ui.Components.AnchorSpan[] r0 = (org.telegram.ui.Components.AnchorSpan[]) r0     // Catch:{ Exception -> 0x0336 }
            int r6 = r3.getLineCount()     // Catch:{ Exception -> 0x0336 }
            if (r0 == 0) goto L_0x0331
            int r14 = r0.length     // Catch:{ Exception -> 0x0336 }
            if (r14 <= 0) goto L_0x0331
            r14 = 0
        L_0x02de:
            int r15 = r0.length     // Catch:{ Exception -> 0x0336 }
            if (r14 >= r15) goto L_0x032c
            r15 = 1
            if (r6 > r15) goto L_0x02fe
            java.util.HashMap r15 = r32.anchorsOffset     // Catch:{ Exception -> 0x0336 }
            r17 = r0[r14]     // Catch:{ Exception -> 0x0336 }
            r18 = r1
            java.lang.String r1 = r17.getName()     // Catch:{ Exception -> 0x02fa }
            r17 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r28)     // Catch:{ Exception -> 0x032a }
            r15.put(r1, r2)     // Catch:{ Exception -> 0x032a }
            goto L_0x0323
        L_0x02fa:
            r0 = move-exception
            r17 = r2
            goto L_0x033b
        L_0x02fe:
            r18 = r1
            r17 = r2
            java.util.HashMap r1 = r32.anchorsOffset     // Catch:{ Exception -> 0x032a }
            r2 = r0[r14]     // Catch:{ Exception -> 0x032a }
            java.lang.String r2 = r2.getName()     // Catch:{ Exception -> 0x032a }
            r15 = r0[r14]     // Catch:{ Exception -> 0x032a }
            int r15 = r5.getSpanStart(r15)     // Catch:{ Exception -> 0x032a }
            int r15 = r3.getLineForOffset(r15)     // Catch:{ Exception -> 0x032a }
            int r15 = r3.getLineTop(r15)     // Catch:{ Exception -> 0x032a }
            int r15 = r28 + r15
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)     // Catch:{ Exception -> 0x032a }
            r1.put(r2, r15)     // Catch:{ Exception -> 0x032a }
        L_0x0323:
            int r14 = r14 + 1
            r2 = r17
            r1 = r18
            goto L_0x02de
        L_0x032a:
            r0 = move-exception
            goto L_0x033b
        L_0x032c:
            r18 = r1
            r17 = r2
            goto L_0x0335
        L_0x0331:
            r18 = r1
            r17 = r2
        L_0x0335:
            goto L_0x033b
        L_0x0336:
            r0 = move-exception
            r18 = r1
            r17 = r2
        L_0x033b:
            r6 = 0
            int r0 = r5.length()     // Catch:{ Exception -> 0x03a0 }
            java.lang.Class<org.telegram.ui.Components.TextPaintWebpageUrlSpan> r14 = org.telegram.ui.Components.TextPaintWebpageUrlSpan.class
            r15 = 0
            java.lang.Object[] r0 = r5.getSpans(r15, r0, r14)     // Catch:{ Exception -> 0x03a0 }
            org.telegram.ui.Components.TextPaintWebpageUrlSpan[] r0 = (org.telegram.ui.Components.TextPaintWebpageUrlSpan[]) r0     // Catch:{ Exception -> 0x03a0 }
            if (r0 == 0) goto L_0x039f
            int r14 = r0.length     // Catch:{ Exception -> 0x03a0 }
            if (r14 <= 0) goto L_0x039f
            org.telegram.ui.Components.LinkPath r14 = new org.telegram.ui.Components.LinkPath     // Catch:{ Exception -> 0x03a0 }
            r15 = 1
            r14.<init>(r15)     // Catch:{ Exception -> 0x03a0 }
            r12 = r14
            r14 = 0
            r12.setAllowReset(r14)     // Catch:{ Exception -> 0x03a0 }
            r14 = 0
        L_0x035a:
            int r15 = r0.length     // Catch:{ Exception -> 0x03a0 }
            if (r14 >= r15) goto L_0x039b
            r15 = r0[r14]     // Catch:{ Exception -> 0x03a0 }
            int r15 = r5.getSpanStart(r15)     // Catch:{ Exception -> 0x03a0 }
            r1 = r0[r14]     // Catch:{ Exception -> 0x03a0 }
            int r1 = r5.getSpanEnd(r1)     // Catch:{ Exception -> 0x03a0 }
            r12.setCurrentLayout(r3, r15, r6)     // Catch:{ Exception -> 0x03a0 }
            r20 = r0[r14]     // Catch:{ Exception -> 0x03a0 }
            android.text.TextPaint r20 = r20.getTextPaint()     // Catch:{ Exception -> 0x03a0 }
            if (r20 == 0) goto L_0x037d
            r20 = r0[r14]     // Catch:{ Exception -> 0x03a0 }
            android.text.TextPaint r2 = r20.getTextPaint()     // Catch:{ Exception -> 0x03a0 }
            int r2 = r2.baselineShift     // Catch:{ Exception -> 0x03a0 }
            goto L_0x037e
        L_0x037d:
            r2 = 0
        L_0x037e:
            if (r2 == 0) goto L_0x0390
            if (r2 <= 0) goto L_0x0385
            r20 = 1084227584(0x40a00000, float:5.0)
            goto L_0x0387
        L_0x0385:
            r20 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
        L_0x0387:
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x03a0 }
            int r20 = r2 + r20
            r6 = r20
            goto L_0x0391
        L_0x0390:
            r6 = 0
        L_0x0391:
            r12.setBaselineShift(r6)     // Catch:{ Exception -> 0x03a0 }
            r3.getSelectionPath(r15, r1, r12)     // Catch:{ Exception -> 0x03a0 }
            int r14 = r14 + 1
            r6 = 0
            goto L_0x035a
        L_0x039b:
            r1 = 1
            r12.setAllowReset(r1)     // Catch:{ Exception -> 0x03a0 }
        L_0x039f:
            goto L_0x03a1
        L_0x03a0:
            r0 = move-exception
        L_0x03a1:
            int r0 = r5.length()     // Catch:{ Exception -> 0x040a }
            java.lang.Class<org.telegram.ui.Components.TextPaintMarkSpan> r1 = org.telegram.ui.Components.TextPaintMarkSpan.class
            r2 = 0
            java.lang.Object[] r0 = r5.getSpans(r2, r0, r1)     // Catch:{ Exception -> 0x040a }
            org.telegram.ui.Components.TextPaintMarkSpan[] r0 = (org.telegram.ui.Components.TextPaintMarkSpan[]) r0     // Catch:{ Exception -> 0x040a }
            if (r0 == 0) goto L_0x0409
            int r1 = r0.length     // Catch:{ Exception -> 0x040a }
            if (r1 <= 0) goto L_0x0409
            org.telegram.ui.Components.LinkPath r1 = new org.telegram.ui.Components.LinkPath     // Catch:{ Exception -> 0x040a }
            r2 = 1
            r1.<init>(r2)     // Catch:{ Exception -> 0x040a }
            r14 = 0
            r1.setAllowReset(r14)     // Catch:{ Exception -> 0x0406 }
            r2 = 0
        L_0x03be:
            int r6 = r0.length     // Catch:{ Exception -> 0x0406 }
            if (r2 >= r6) goto L_0x0400
            r6 = r0[r2]     // Catch:{ Exception -> 0x0406 }
            int r6 = r5.getSpanStart(r6)     // Catch:{ Exception -> 0x0406 }
            r13 = r0[r2]     // Catch:{ Exception -> 0x0406 }
            int r13 = r5.getSpanEnd(r13)     // Catch:{ Exception -> 0x0406 }
            r15 = 0
            r1.setCurrentLayout(r3, r6, r15)     // Catch:{ Exception -> 0x0406 }
            r20 = r0[r2]     // Catch:{ Exception -> 0x0406 }
            android.text.TextPaint r20 = r20.getTextPaint()     // Catch:{ Exception -> 0x0406 }
            if (r20 == 0) goto L_0x03e2
            r20 = r0[r2]     // Catch:{ Exception -> 0x0406 }
            android.text.TextPaint r14 = r20.getTextPaint()     // Catch:{ Exception -> 0x0406 }
            int r14 = r14.baselineShift     // Catch:{ Exception -> 0x0406 }
            goto L_0x03e3
        L_0x03e2:
            r14 = 0
        L_0x03e3:
            if (r14 == 0) goto L_0x03f5
            if (r14 <= 0) goto L_0x03ea
            r20 = 1084227584(0x40a00000, float:5.0)
            goto L_0x03ec
        L_0x03ea:
            r20 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
        L_0x03ec:
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x0406 }
            int r20 = r14 + r20
            r15 = r20
            goto L_0x03f6
        L_0x03f5:
            r15 = 0
        L_0x03f6:
            r1.setBaselineShift(r15)     // Catch:{ Exception -> 0x0406 }
            r3.getSelectionPath(r6, r13, r1)     // Catch:{ Exception -> 0x0406 }
            int r2 = r2 + 1
            r14 = 0
            goto L_0x03be
        L_0x0400:
            r2 = 1
            r1.setAllowReset(r2)     // Catch:{ Exception -> 0x0406 }
            r13 = r1
            goto L_0x0409
        L_0x0406:
            r0 = move-exception
            r13 = r1
            goto L_0x0410
        L_0x0409:
            goto L_0x0410
        L_0x040a:
            r0 = move-exception
            goto L_0x0410
        L_0x040c:
            r18 = r1
            r17 = r2
        L_0x0410:
            org.telegram.ui.ArticleViewer$DrawingText r0 = new org.telegram.ui.ArticleViewer$DrawingText
            r0.<init>()
            r0.textLayout = r3
            r0.textPath = r12
            r0.markPath = r13
            r0.parentBlock = r11
            r0.parentText = r10
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.createLayoutForText(android.view.View, java.lang.CharSequence, org.telegram.tgnet.TLRPC$RichText, int, int, org.telegram.tgnet.TLRPC$PageBlock, android.text.Layout$Alignment, int, org.telegram.ui.ArticleViewer$WebpageAdapter):org.telegram.ui.ArticleViewer$DrawingText");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0243  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x024e  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x026d  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0275  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkLayoutForLinks(org.telegram.ui.ArticleViewer.WebpageAdapter r27, android.view.MotionEvent r28, android.view.View r29, org.telegram.ui.ArticleViewer.DrawingText r30, int r31, int r32) {
        /*
            r26 = this;
            r1 = r26
            r2 = r29
            r3 = r30
            r4 = r31
            r5 = r32
            android.animation.AnimatorSet r0 = r1.pageSwitchAnimation
            if (r0 != 0) goto L_0x027d
            if (r2 == 0) goto L_0x027d
            org.telegram.ui.Cells.TextSelectionHelper$ArticleTextSelectionHelper r0 = r1.textSelectionHelper
            boolean r0 = r0.isSelectable(r2)
            if (r0 != 0) goto L_0x001a
            goto L_0x027d
        L_0x001a:
            r1.pressedLinkOwnerView = r2
            if (r3 == 0) goto L_0x0247
            android.text.StaticLayout r8 = r3.textLayout
            float r0 = r28.getX()
            int r9 = (int) r0
            float r0 = r28.getY()
            int r10 = (int) r0
            r11 = 0
            int r0 = r28.getAction()
            if (r0 != 0) goto L_0x0191
            r0 = 0
            r13 = 1325400064(0x4var_, float:2.14748365E9)
            r14 = 0
            int r15 = r8.getLineCount()
            r24 = r13
            r13 = r0
            r0 = r14
            r14 = r24
        L_0x003f:
            if (r0 >= r15) goto L_0x0054
            float r7 = r8.getLineWidth(r0)
            float r13 = java.lang.Math.max(r7, r13)
            float r7 = r8.getLineLeft(r0)
            float r14 = java.lang.Math.min(r7, r14)
            int r0 = r0 + 1
            goto L_0x003f
        L_0x0054:
            float r0 = (float) r9
            float r7 = (float) r4
            float r7 = r7 + r14
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x018c
            float r0 = (float) r9
            float r7 = (float) r4
            float r7 = r7 + r14
            float r7 = r7 + r13
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 > 0) goto L_0x018c
            if (r10 < r5) goto L_0x018c
            int r0 = r8.getHeight()
            int r0 = r0 + r5
            if (r10 > r0) goto L_0x018c
            r1.pressedLinkOwnerLayout = r3
            r1.pressedLayoutY = r5
            java.lang.CharSequence r7 = r8.getText()
            boolean r0 = r7 instanceof android.text.Spannable
            if (r0 == 0) goto L_0x0187
            int r15 = r9 - r4
            int r12 = r10 - r5
            int r16 = r8.getLineForVertical(r12)     // Catch:{ Exception -> 0x017e }
            r17 = r16
            float r0 = (float) r15     // Catch:{ Exception -> 0x017e }
            r6 = r17
            int r0 = r8.getOffsetForHorizontal(r6, r0)     // Catch:{ Exception -> 0x017e }
            r17 = r0
            float r0 = r8.getLineLeft(r6)     // Catch:{ Exception -> 0x017e }
            r14 = r0
            float r0 = (float) r15     // Catch:{ Exception -> 0x017e }
            int r0 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x0175
            float r0 = r8.getLineWidth(r6)     // Catch:{ Exception -> 0x017e }
            float r0 = r0 + r14
            float r3 = (float) r15     // Catch:{ Exception -> 0x017e }
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 < 0) goto L_0x0175
            java.lang.CharSequence r0 = r8.getText()     // Catch:{ Exception -> 0x017e }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x017e }
            r3 = r0
            java.lang.Class<org.telegram.ui.Components.TextPaintUrlSpan> r0 = org.telegram.ui.Components.TextPaintUrlSpan.class
            r4 = r17
            java.lang.Object[] r0 = r3.getSpans(r4, r4, r0)     // Catch:{ Exception -> 0x017e }
            org.telegram.ui.Components.TextPaintUrlSpan[] r0 = (org.telegram.ui.Components.TextPaintUrlSpan[]) r0     // Catch:{ Exception -> 0x017e }
            r17 = r0
            r18 = r4
            r4 = r17
            if (r4 == 0) goto L_0x016a
            int r0 = r4.length     // Catch:{ Exception -> 0x017e }
            if (r0 <= 0) goto L_0x016a
            r17 = 0
            r0 = r4[r17]     // Catch:{ Exception -> 0x017e }
            int r17 = r3.getSpanStart(r0)     // Catch:{ Exception -> 0x017e }
            int r19 = r3.getSpanEnd(r0)     // Catch:{ Exception -> 0x017e }
            r20 = 1
            r5 = r0
            r0 = r20
            r24 = r17
            r17 = r6
            r6 = r24
            r25 = r19
            r19 = r7
            r7 = r25
        L_0x00d8:
            r20 = r11
            int r11 = r4.length     // Catch:{ Exception -> 0x0168 }
            if (r0 >= r11) goto L_0x0108
            r11 = r4[r0]     // Catch:{ Exception -> 0x0168 }
            int r21 = r3.getSpanStart(r11)     // Catch:{ Exception -> 0x0168 }
            r22 = r21
            int r21 = r3.getSpanEnd(r11)     // Catch:{ Exception -> 0x0168 }
            r23 = r21
            r21 = r3
            r3 = r22
            if (r6 > r3) goto L_0x00f8
            r22 = r4
            r4 = r23
            if (r4 <= r7) goto L_0x00ff
            goto L_0x00fc
        L_0x00f8:
            r22 = r4
            r4 = r23
        L_0x00fc:
            r5 = r11
            r6 = r3
            r7 = r4
        L_0x00ff:
            int r0 = r0 + 1
            r11 = r20
            r3 = r21
            r4 = r22
            goto L_0x00d8
        L_0x0108:
            r21 = r3
            r22 = r4
            org.telegram.ui.Components.LinkSpanDrawable<org.telegram.ui.Components.TextPaintUrlSpan> r0 = r1.pressedLink     // Catch:{ Exception -> 0x0168 }
            if (r0 == 0) goto L_0x0115
            org.telegram.ui.Components.LinkSpanDrawable$LinkCollector r3 = r1.links     // Catch:{ Exception -> 0x0168 }
            r3.removeLink(r0)     // Catch:{ Exception -> 0x0168 }
        L_0x0115:
            org.telegram.ui.Components.LinkSpanDrawable r0 = new org.telegram.ui.Components.LinkSpanDrawable     // Catch:{ Exception -> 0x0168 }
            float r3 = (float) r9     // Catch:{ Exception -> 0x0168 }
            float r4 = (float) r10     // Catch:{ Exception -> 0x0168 }
            r11 = 0
            r0.<init>(r5, r11, r3, r4)     // Catch:{ Exception -> 0x0168 }
            r1.pressedLink = r0     // Catch:{ Exception -> 0x0168 }
            java.lang.String r3 = "windowBackgroundWhiteLinkSelection"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)     // Catch:{ Exception -> 0x0168 }
            r4 = 872415231(0x33ffffff, float:1.1920928E-7)
            r3 = r3 & r4
            r0.setColor(r3)     // Catch:{ Exception -> 0x0168 }
            org.telegram.ui.Components.LinkSpanDrawable$LinkCollector r0 = r1.links     // Catch:{ Exception -> 0x0168 }
            org.telegram.ui.Components.LinkSpanDrawable<org.telegram.ui.Components.TextPaintUrlSpan> r3 = r1.pressedLink     // Catch:{ Exception -> 0x0168 }
            org.telegram.ui.ArticleViewer$DrawingText r4 = r1.pressedLinkOwnerLayout     // Catch:{ Exception -> 0x0168 }
            r0.addLink(r3, r4)     // Catch:{ Exception -> 0x0168 }
            org.telegram.ui.Components.LinkSpanDrawable<org.telegram.ui.Components.TextPaintUrlSpan> r0 = r1.pressedLink     // Catch:{ Exception -> 0x0163 }
            org.telegram.ui.Components.LinkPath r0 = r0.obtainNewPath()     // Catch:{ Exception -> 0x0163 }
            r3 = 0
            r0.setCurrentLayout(r8, r6, r3)     // Catch:{ Exception -> 0x0163 }
            android.text.TextPaint r3 = r5.getTextPaint()     // Catch:{ Exception -> 0x0163 }
            if (r3 == 0) goto L_0x0148
            int r4 = r3.baselineShift     // Catch:{ Exception -> 0x0163 }
            goto L_0x0149
        L_0x0148:
            r4 = 0
        L_0x0149:
            if (r4 == 0) goto L_0x0158
            if (r4 <= 0) goto L_0x0150
            r11 = 1084227584(0x40a00000, float:5.0)
            goto L_0x0152
        L_0x0150:
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
        L_0x0152:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ Exception -> 0x0163 }
            int r11 = r11 + r4
            goto L_0x0159
        L_0x0158:
            r11 = 0
        L_0x0159:
            r0.setBaselineShift(r11)     // Catch:{ Exception -> 0x0163 }
            r8.getSelectionPath(r6, r7, r0)     // Catch:{ Exception -> 0x0163 }
            r29.invalidate()     // Catch:{ Exception -> 0x0163 }
            goto L_0x017d
        L_0x0163:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0168 }
            goto L_0x017d
        L_0x0168:
            r0 = move-exception
            goto L_0x0183
        L_0x016a:
            r21 = r3
            r22 = r4
            r17 = r6
            r19 = r7
            r20 = r11
            goto L_0x017d
        L_0x0175:
            r19 = r7
            r20 = r11
            r18 = r17
            r17 = r6
        L_0x017d:
            goto L_0x018e
        L_0x017e:
            r0 = move-exception
            r19 = r7
            r20 = r11
        L_0x0183:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x018e
        L_0x0187:
            r19 = r7
            r20 = r11
            goto L_0x018e
        L_0x018c:
            r20 = r11
        L_0x018e:
            r4 = 1
            goto L_0x023f
        L_0x0191:
            r20 = r11
            int r3 = r28.getAction()
            r4 = 1
            if (r3 != r4) goto L_0x022c
            org.telegram.ui.Components.LinkSpanDrawable<org.telegram.ui.Components.TextPaintUrlSpan> r3 = r1.pressedLink
            if (r3 == 0) goto L_0x023f
            r11 = 1
            android.text.style.CharacterStyle r3 = r3.getSpan()
            org.telegram.ui.Components.TextPaintUrlSpan r3 = (org.telegram.ui.Components.TextPaintUrlSpan) r3
            java.lang.String r3 = r3.getUrl()
            if (r3 == 0) goto L_0x022b
            org.telegram.ui.ActionBar.BottomSheet r5 = r1.linkSheet
            if (r5 == 0) goto L_0x01b5
            r5.dismiss()
            r0 = 0
            r1.linkSheet = r0
        L_0x01b5:
            r5 = 0
            r0 = 35
            int r0 = r3.lastIndexOf(r0)
            r6 = r0
            r7 = -1
            if (r0 == r7) goto L_0x0219
            org.telegram.tgnet.TLRPC$WebPage r0 = r27.currentPage
            org.telegram.tgnet.TLRPC$Page r0 = r0.cached_page
            java.lang.String r0 = r0.url
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x01dc
            org.telegram.tgnet.TLRPC$WebPage r0 = r27.currentPage
            org.telegram.tgnet.TLRPC$Page r0 = r0.cached_page
            java.lang.String r0 = r0.url
            java.lang.String r0 = r0.toLowerCase()
            r7 = r0
            goto L_0x01e7
        L_0x01dc:
            org.telegram.tgnet.TLRPC$WebPage r0 = r27.currentPage
            java.lang.String r0 = r0.url
            java.lang.String r0 = r0.toLowerCase()
            r7 = r0
        L_0x01e7:
            int r0 = r6 + 1
            java.lang.String r0 = r3.substring(r0)     // Catch:{ Exception -> 0x01f4 }
            java.lang.String r12 = "UTF-8"
            java.lang.String r0 = java.net.URLDecoder.decode(r0, r12)     // Catch:{ Exception -> 0x01f4 }
            goto L_0x01f8
        L_0x01f4:
            r0 = move-exception
            java.lang.String r12 = ""
            r0 = r12
        L_0x01f8:
            java.lang.String r12 = r3.toLowerCase()
            boolean r12 = r12.contains(r7)
            if (r12 == 0) goto L_0x0218
            boolean r12 = android.text.TextUtils.isEmpty(r0)
            if (r12 == 0) goto L_0x0214
            androidx.recyclerview.widget.LinearLayoutManager[] r12 = r1.layoutManager
            r13 = 0
            r12 = r12[r13]
            r12.scrollToPositionWithOffset(r13, r13)
            r26.checkScrollAnimated()
            goto L_0x0217
        L_0x0214:
            r1.scrollToAnchor(r0)
        L_0x0217:
            r5 = 1
        L_0x0218:
            goto L_0x021a
        L_0x0219:
            r0 = 0
        L_0x021a:
            if (r5 != 0) goto L_0x022b
            org.telegram.ui.Components.LinkSpanDrawable<org.telegram.ui.Components.TextPaintUrlSpan> r7 = r1.pressedLink
            android.text.style.CharacterStyle r7 = r7.getSpan()
            org.telegram.ui.Components.TextPaintUrlSpan r7 = (org.telegram.ui.Components.TextPaintUrlSpan) r7
            java.lang.String r7 = r7.getUrl()
            r1.openWebpageUrl(r7, r0)
        L_0x022b:
            goto L_0x0241
        L_0x022c:
            int r0 = r28.getAction()
            r3 = 3
            if (r0 != r3) goto L_0x023f
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r1.popupWindow
            if (r0 == 0) goto L_0x023d
            boolean r0 = r0.isShowing()
            if (r0 != 0) goto L_0x023f
        L_0x023d:
            r11 = 1
            goto L_0x0241
        L_0x023f:
            r11 = r20
        L_0x0241:
            if (r11 == 0) goto L_0x0248
            r26.removePressedLink()
            goto L_0x0248
        L_0x0247:
            r4 = 1
        L_0x0248:
            int r0 = r28.getAction()
            if (r0 != 0) goto L_0x0259
            float r0 = r28.getX()
            float r3 = r28.getY()
            r1.startCheckLongPress(r0, r3, r2)
        L_0x0259:
            int r0 = r28.getAction()
            if (r0 == 0) goto L_0x0269
            int r0 = r28.getAction()
            r3 = 2
            if (r0 == r3) goto L_0x0269
            r26.cancelCheckLongPress()
        L_0x0269:
            boolean r0 = r2 instanceof org.telegram.ui.ArticleViewer.BlockDetailsCell
            if (r0 == 0) goto L_0x0275
            org.telegram.ui.Components.LinkSpanDrawable<org.telegram.ui.Components.TextPaintUrlSpan> r0 = r1.pressedLink
            if (r0 == 0) goto L_0x0273
            r6 = 1
            goto L_0x0274
        L_0x0273:
            r6 = 0
        L_0x0274:
            return r6
        L_0x0275:
            org.telegram.ui.ArticleViewer$DrawingText r0 = r1.pressedLinkOwnerLayout
            if (r0 == 0) goto L_0x027b
            r6 = 1
            goto L_0x027c
        L_0x027b:
            r6 = 0
        L_0x027c:
            return r6
        L_0x027d:
            r3 = 0
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.checkLayoutForLinks(org.telegram.ui.ArticleViewer$WebpageAdapter, android.view.MotionEvent, android.view.View, org.telegram.ui.ArticleViewer$DrawingText, int, int):boolean");
    }

    /* access modifiers changed from: private */
    public void removePressedLink() {
        if (this.pressedLink != null || this.pressedLinkOwnerView != null) {
            View parentView = this.pressedLinkOwnerView;
            this.links.clear();
            this.pressedLink = null;
            this.pressedLinkOwnerLayout = null;
            this.pressedLinkOwnerView = null;
            if (parentView != null) {
                parentView.invalidate();
            }
        }
    }

    /* access modifiers changed from: private */
    public void openWebpageUrl(String url, String anchor) {
        if (this.openUrlReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.openUrlReqId, false);
            this.openUrlReqId = 0;
        }
        int reqId = this.lastReqId + 1;
        this.lastReqId = reqId;
        showProgressView(true, true);
        TLRPC.TL_messages_getWebPage req = new TLRPC.TL_messages_getWebPage();
        req.url = url;
        req.hash = 0;
        this.openUrlReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ArticleViewer$$ExternalSyntheticLambda29(this, reqId, anchor, req));
    }

    /* renamed from: lambda$openWebpageUrl$7$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2678lambda$openWebpageUrl$7$orgtelegramuiArticleViewer(int reqId, String anchor, TLRPC.TL_messages_getWebPage req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ArticleViewer$$ExternalSyntheticLambda19(this, reqId, response, anchor, req));
    }

    /* renamed from: lambda$openWebpageUrl$6$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2677lambda$openWebpageUrl$6$orgtelegramuiArticleViewer(int reqId, TLObject response, String anchor, TLRPC.TL_messages_getWebPage req) {
        if (this.openUrlReqId != 0 && reqId == this.lastReqId) {
            this.openUrlReqId = 0;
            showProgressView(true, false);
            if (!this.isVisible) {
                return;
            }
            if (!(response instanceof TLRPC.TL_webPage) || !(((TLRPC.TL_webPage) response).cached_page instanceof TLRPC.TL_page)) {
                Browser.openUrl((Context) this.parentActivity, req.url);
            } else {
                addPageToStack((TLRPC.TL_webPage) response, anchor, 1);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0067, code lost:
        r5 = (org.telegram.ui.ArticleViewer.BlockAudioCell) r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r10, int r11, java.lang.Object... r12) {
        /*
            r9 = this;
            int r0 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            r1 = 0
            r2 = 1
            if (r10 != r0) goto L_0x0037
            r0 = r12[r1]
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            org.telegram.ui.Components.RecyclerListView[] r1 = r9.listView
            if (r1 == 0) goto L_0x0035
            r1 = 0
        L_0x000f:
            org.telegram.ui.Components.RecyclerListView[] r3 = r9.listView
            int r4 = r3.length
            if (r1 >= r4) goto L_0x0035
            r3 = r3[r1]
            int r3 = r3.getChildCount()
            r4 = 0
        L_0x001b:
            if (r4 >= r3) goto L_0x0032
            org.telegram.ui.Components.RecyclerListView[] r5 = r9.listView
            r5 = r5[r1]
            android.view.View r5 = r5.getChildAt(r4)
            boolean r6 = r5 instanceof org.telegram.ui.ArticleViewer.BlockAudioCell
            if (r6 == 0) goto L_0x002f
            r6 = r5
            org.telegram.ui.ArticleViewer$BlockAudioCell r6 = (org.telegram.ui.ArticleViewer.BlockAudioCell) r6
            r6.updateButtonState(r2)
        L_0x002f:
            int r4 = r4 + 1
            goto L_0x001b
        L_0x0032:
            int r1 = r1 + 1
            goto L_0x000f
        L_0x0035:
            goto L_0x00cb
        L_0x0037:
            int r0 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            if (r10 == r0) goto L_0x009a
            int r0 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            if (r10 != r0) goto L_0x0040
            goto L_0x009a
        L_0x0040:
            int r0 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            if (r10 != r0) goto L_0x00cb
            r0 = r12[r1]
            java.lang.Integer r0 = (java.lang.Integer) r0
            org.telegram.ui.Components.RecyclerListView[] r1 = r9.listView
            if (r1 == 0) goto L_0x00cb
            r1 = 0
        L_0x004d:
            org.telegram.ui.Components.RecyclerListView[] r2 = r9.listView
            int r3 = r2.length
            if (r1 >= r3) goto L_0x00cb
            r2 = r2[r1]
            int r2 = r2.getChildCount()
            r3 = 0
        L_0x0059:
            if (r3 >= r2) goto L_0x0097
            org.telegram.ui.Components.RecyclerListView[] r4 = r9.listView
            r4 = r4[r1]
            android.view.View r4 = r4.getChildAt(r3)
            boolean r5 = r4 instanceof org.telegram.ui.ArticleViewer.BlockAudioCell
            if (r5 == 0) goto L_0x0094
            r5 = r4
            org.telegram.ui.ArticleViewer$BlockAudioCell r5 = (org.telegram.ui.ArticleViewer.BlockAudioCell) r5
            org.telegram.messenger.MessageObject r6 = r5.getMessageObject()
            if (r6 == 0) goto L_0x0094
            int r7 = r6.getId()
            int r8 = r0.intValue()
            if (r7 != r8) goto L_0x0094
            org.telegram.messenger.MediaController r7 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r7 = r7.getPlayingMessageObject()
            if (r7 == 0) goto L_0x0097
            float r8 = r7.audioProgress
            r6.audioProgress = r8
            int r8 = r7.audioProgressSec
            r6.audioProgressSec = r8
            int r8 = r7.audioPlayerDuration
            r6.audioPlayerDuration = r8
            r5.updatePlayingMessageProgress()
            goto L_0x0097
        L_0x0094:
            int r3 = r3 + 1
            goto L_0x0059
        L_0x0097:
            int r1 = r1 + 1
            goto L_0x004d
        L_0x009a:
            org.telegram.ui.Components.RecyclerListView[] r0 = r9.listView
            if (r0 == 0) goto L_0x00cb
            r0 = 0
        L_0x009f:
            org.telegram.ui.Components.RecyclerListView[] r1 = r9.listView
            int r3 = r1.length
            if (r0 >= r3) goto L_0x00cb
            r1 = r1[r0]
            int r1 = r1.getChildCount()
            r3 = 0
        L_0x00ab:
            if (r3 >= r1) goto L_0x00c8
            org.telegram.ui.Components.RecyclerListView[] r4 = r9.listView
            r4 = r4[r0]
            android.view.View r4 = r4.getChildAt(r3)
            boolean r5 = r4 instanceof org.telegram.ui.ArticleViewer.BlockAudioCell
            if (r5 == 0) goto L_0x00c5
            r5 = r4
            org.telegram.ui.ArticleViewer$BlockAudioCell r5 = (org.telegram.ui.ArticleViewer.BlockAudioCell) r5
            org.telegram.messenger.MessageObject r6 = r5.getMessageObject()
            if (r6 == 0) goto L_0x00c5
            r5.updateButtonState(r2)
        L_0x00c5:
            int r3 = r3 + 1
            goto L_0x00ab
        L_0x00c8:
            int r0 = r0 + 1
            goto L_0x009f
        L_0x00cb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    public void updateThemeColors(float progress) {
        refreshThemeColors();
        updatePaintColors();
        if (this.windowView != null) {
            this.listView[0].invalidateViews();
            this.listView[1].invalidateViews();
            this.windowView.invalidate();
            this.searchPanel.invalidate();
            if (progress == 1.0f) {
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
        Typeface typefaceNormal = this.selectedFont == 0 ? Typeface.DEFAULT : Typeface.SERIF;
        Typeface typefaceItalic = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/ritalic.ttf") : Typeface.create("serif", 2);
        Typeface typefaceBold = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmedium.ttf") : Typeface.create("serif", 1);
        Typeface typefaceBoldItalic = this.selectedFont == 0 ? AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf") : Typeface.create("serif", 3);
        for (int a = 0; a < quoteTextPaints.size(); a++) {
            updateFontEntry(quoteTextPaints.keyAt(a), quoteTextPaints.valueAt(a), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a2 = 0; a2 < preformattedTextPaints.size(); a2++) {
            updateFontEntry(preformattedTextPaints.keyAt(a2), preformattedTextPaints.valueAt(a2), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a3 = 0; a3 < paragraphTextPaints.size(); a3++) {
            updateFontEntry(paragraphTextPaints.keyAt(a3), paragraphTextPaints.valueAt(a3), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a4 = 0; a4 < listTextPaints.size(); a4++) {
            updateFontEntry(listTextPaints.keyAt(a4), listTextPaints.valueAt(a4), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a5 = 0; a5 < embedPostTextPaints.size(); a5++) {
            updateFontEntry(embedPostTextPaints.keyAt(a5), embedPostTextPaints.valueAt(a5), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a6 = 0; a6 < mediaCaptionTextPaints.size(); a6++) {
            updateFontEntry(mediaCaptionTextPaints.keyAt(a6), mediaCaptionTextPaints.valueAt(a6), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a7 = 0; a7 < mediaCreditTextPaints.size(); a7++) {
            updateFontEntry(mediaCreditTextPaints.keyAt(a7), mediaCreditTextPaints.valueAt(a7), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a8 = 0; a8 < photoCaptionTextPaints.size(); a8++) {
            updateFontEntry(photoCaptionTextPaints.keyAt(a8), photoCaptionTextPaints.valueAt(a8), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a9 = 0; a9 < photoCreditTextPaints.size(); a9++) {
            updateFontEntry(photoCreditTextPaints.keyAt(a9), photoCreditTextPaints.valueAt(a9), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a10 = 0; a10 < authorTextPaints.size(); a10++) {
            updateFontEntry(authorTextPaints.keyAt(a10), authorTextPaints.valueAt(a10), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a11 = 0; a11 < footerTextPaints.size(); a11++) {
            updateFontEntry(footerTextPaints.keyAt(a11), footerTextPaints.valueAt(a11), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a12 = 0; a12 < embedPostCaptionTextPaints.size(); a12++) {
            updateFontEntry(embedPostCaptionTextPaints.keyAt(a12), embedPostCaptionTextPaints.valueAt(a12), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a13 = 0; a13 < relatedArticleTextPaints.size(); a13++) {
            updateFontEntry(relatedArticleTextPaints.keyAt(a13), relatedArticleTextPaints.valueAt(a13), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a14 = 0; a14 < detailsTextPaints.size(); a14++) {
            updateFontEntry(detailsTextPaints.keyAt(a14), detailsTextPaints.valueAt(a14), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
        }
        for (int a15 = 0; a15 < tableTextPaints.size(); a15++) {
            updateFontEntry(tableTextPaints.keyAt(a15), tableTextPaints.valueAt(a15), typefaceNormal, typefaceBoldItalic, typefaceBold, typefaceItalic);
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

    private void setMapColors(SparseArray<TextPaint> map) {
        for (int a = 0; a < map.size(); a++) {
            int flags = map.keyAt(a);
            TextPaint paint = map.valueAt(a);
            if ((flags & 8) == 0 && (flags & 512) == 0) {
                paint.setColor(getTextColor());
            } else {
                paint.setColor(getLinkTextColor());
            }
        }
    }

    public void setParentActivity(Activity activity, BaseFragment fragment) {
        Activity activity2 = activity;
        this.parentFragment = fragment;
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
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                int clipRight;
                int clipLeft;
                float opacity;
                Canvas canvas2 = canvas;
                View view = child;
                if (!ArticleViewer.this.windowView.movingPage) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                int width = getMeasuredWidth();
                int translationX = (int) ArticleViewer.this.listView[0].getTranslationX();
                int clipRight2 = width;
                if (view == ArticleViewer.this.listView[1]) {
                    clipLeft = 0;
                    clipRight = translationX;
                } else if (view == ArticleViewer.this.listView[0]) {
                    clipLeft = translationX;
                    clipRight = clipRight2;
                } else {
                    clipLeft = 0;
                    clipRight = clipRight2;
                }
                int restoreCount = canvas.save();
                canvas2.clipRect(clipLeft, 0, clipRight, getHeight());
                boolean result = super.drawChild(canvas, child, drawingTime);
                canvas2.restoreToCount(restoreCount);
                if (translationX != 0) {
                    if (view == ArticleViewer.this.listView[0]) {
                        float alpha = Math.max(0.0f, Math.min(((float) (width - translationX)) / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                        ArticleViewer.this.layerShadowDrawable.setBounds(translationX - ArticleViewer.this.layerShadowDrawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                        ArticleViewer.this.layerShadowDrawable.setAlpha((int) (255.0f * alpha));
                        ArticleViewer.this.layerShadowDrawable.draw(canvas2);
                    } else if (view == ArticleViewer.this.listView[1]) {
                        float opacity2 = Math.min(0.8f, ((float) (width - translationX)) / ((float) width));
                        if (opacity2 < 0.0f) {
                            opacity = 0.0f;
                        } else {
                            opacity = opacity2;
                        }
                        ArticleViewer.this.scrimPaint.setColor(((int) (153.0f * opacity)) << 24);
                        canvas.drawRect((float) clipLeft, 0.0f, (float) clipRight, (float) getHeight(), ArticleViewer.this.scrimPaint);
                    }
                }
                return result;
            }
        };
        this.containerView = r0;
        this.windowView.addView(r0, LayoutHelper.createFrame(-1, -1, 51));
        if (Build.VERSION.SDK_INT >= 21) {
            this.windowView.setFitsSystemWindows(true);
            this.containerView.setOnApplyWindowInsetsListener(ArticleViewer$$ExternalSyntheticLambda39.INSTANCE);
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
            WebpageAdapter webpageAdapter = new WebpageAdapter(this.parentActivity);
            webpageAdapterArr[i2] = webpageAdapter;
            final WebpageAdapter webpageAdapter2 = webpageAdapter;
            this.listView[i2] = new RecyclerListView(activity2) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    super.onLayout(changed, l, t, r, b);
                    int count = getChildCount();
                    int a = 0;
                    while (a < count) {
                        View child = getChildAt(a);
                        if (!(child.getTag() instanceof Integer) || ((Integer) child.getTag()).intValue() != 90 || child.getBottom() >= getMeasuredHeight()) {
                            a++;
                        } else {
                            int height = getMeasuredHeight();
                            child.layout(0, height - child.getMeasuredHeight(), child.getMeasuredWidth(), height);
                            return;
                        }
                    }
                }

                public boolean onInterceptTouchEvent(MotionEvent e) {
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLink == null && ((ArticleViewer.this.popupWindow == null || !ArticleViewer.this.popupWindow.isShowing()) && (e.getAction() == 1 || e.getAction() == 3))) {
                        LinkSpanDrawable unused = ArticleViewer.this.pressedLink = null;
                        DrawingText unused2 = ArticleViewer.this.pressedLinkOwnerLayout = null;
                        View unused3 = ArticleViewer.this.pressedLinkOwnerView = null;
                    } else if (!(ArticleViewer.this.pressedLinkOwnerLayout == null || ArticleViewer.this.pressedLink == null || e.getAction() != 1)) {
                        ArticleViewer articleViewer = ArticleViewer.this;
                        boolean unused4 = articleViewer.checkLayoutForLinks(webpageAdapter2, e, articleViewer.pressedLinkOwnerView, ArticleViewer.this.pressedLinkOwnerLayout, 0, 0);
                    }
                    return super.onInterceptTouchEvent(e);
                }

                public boolean onTouchEvent(MotionEvent e) {
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null && ArticleViewer.this.pressedLink == null && ((ArticleViewer.this.popupWindow == null || !ArticleViewer.this.popupWindow.isShowing()) && (e.getAction() == 1 || e.getAction() == 3))) {
                        LinkSpanDrawable unused = ArticleViewer.this.pressedLink = null;
                        DrawingText unused2 = ArticleViewer.this.pressedLinkOwnerLayout = null;
                        View unused3 = ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                    return super.onTouchEvent(e);
                }

                public void setTranslationX(float translationX) {
                    super.setTranslationX(translationX);
                    if (ArticleViewer.this.windowView.movingPage) {
                        ArticleViewer.this.containerView.invalidate();
                        ArticleViewer articleViewer = ArticleViewer.this;
                        articleViewer.setCurrentHeaderHeight((int) (((float) articleViewer.windowView.startMovingHeaderHeight) + (((float) (AndroidUtilities.dp(56.0f) - ArticleViewer.this.windowView.startMovingHeaderHeight)) * (translationX / ((float) getMeasuredWidth())))));
                    }
                }
            };
            ((DefaultItemAnimator) this.listView[i2].getItemAnimator()).setDelayAnimations(false);
            RecyclerListView recyclerListView = this.listView[i2];
            LinearLayoutManager[] linearLayoutManagerArr = this.layoutManager;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.parentActivity, 1, false);
            linearLayoutManagerArr[i2] = linearLayoutManager;
            recyclerListView.setLayoutManager(linearLayoutManager);
            this.listView[i2].setAdapter(webpageAdapter2);
            this.listView[i2].setClipToPadding(false);
            this.listView[i2].setVisibility(i2 == 0 ? 0 : 8);
            this.listView[i2].setPadding(0, AndroidUtilities.dp(56.0f), 0, 0);
            this.listView[i2].setTopGlowOffset(AndroidUtilities.dp(56.0f));
            this.containerView.addView(this.listView[i2], LayoutHelper.createFrame(-1, -1.0f));
            this.listView[i2].setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new ArticleViewer$$ExternalSyntheticLambda38(this));
            this.listView[i2].setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new ArticleViewer$$ExternalSyntheticLambda37(this, webpageAdapter2));
            this.listView[i2].setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 0) {
                        ArticleViewer.this.textSelectionHelper.stopScrolling();
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (recyclerView.getChildCount() != 0) {
                        ArticleViewer.this.textSelectionHelper.onParentScrolled();
                        ArticleViewer.this.headerView.invalidate();
                        ArticleViewer.this.checkScroll(dy);
                    }
                }
            });
            i2++;
        }
        this.headerPaint.setColor(-16777216);
        this.statusBarPaint.setColor(-16777216);
        this.headerProgressPaint.setColor(-14408666);
        this.navigationBarPaint.setColor(-16777216);
        AnonymousClass10 r02 = new FrameLayout(activity2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                View view;
                float viewProgress;
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                Canvas canvas2 = canvas;
                canvas2.drawRect(0.0f, 0.0f, (float) width, (float) height, ArticleViewer.this.headerPaint);
                if (ArticleViewer.this.layoutManager != null) {
                    int first = ArticleViewer.this.layoutManager[0].findFirstVisibleItemPosition();
                    int last = ArticleViewer.this.layoutManager[0].findLastVisibleItemPosition();
                    int count = ArticleViewer.this.layoutManager[0].getItemCount();
                    if (last >= count - 2) {
                        view = ArticleViewer.this.layoutManager[0].findViewByPosition(count - 2);
                    } else {
                        view = ArticleViewer.this.layoutManager[0].findViewByPosition(first);
                    }
                    if (view != null) {
                        float itemProgress = ((float) width) / ((float) (count - 1));
                        int childCount = ArticleViewer.this.layoutManager[0].getChildCount();
                        float viewHeight = (float) view.getMeasuredHeight();
                        if (last >= count - 2) {
                            viewProgress = ((((float) ((count - 2) - first)) * itemProgress) * ((float) (ArticleViewer.this.listView[0].getMeasuredHeight() - view.getTop()))) / viewHeight;
                        } else {
                            viewProgress = (1.0f - ((((float) Math.min(0, view.getTop() - ArticleViewer.this.listView[0].getPaddingTop())) + viewHeight) / viewHeight)) * itemProgress;
                        }
                        float f = (float) height;
                        canvas.drawRect(0.0f, 0.0f, (((float) first) * itemProgress) + viewProgress, f, ArticleViewer.this.headerProgressPaint);
                    }
                }
            }
        };
        this.headerView = r02;
        r02.setWillNotDraw(false);
        this.containerView.addView(this.headerView, LayoutHelper.createFrame(-1, 56.0f));
        this.headerView.setOnClickListener(new ArticleViewer$$ExternalSyntheticLambda40(this));
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
        this.lineProgressTickRunnable = new ArticleViewer$$ExternalSyntheticLambda15(this);
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
        if (Build.VERSION.SDK_INT < 21) {
            this.searchContainer.setAlpha(0.0f);
        }
        this.headerView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 56.0f));
        AnonymousClass11 r03 = new EditTextBoldCursor(this.parentActivity) {
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(event);
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
        int inputType = 524288 | this.searchField.getInputType();
        this.searchField.setInputType(inputType);
        if (Build.VERSION.SDK_INT < 23) {
            this.searchField.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                }

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }
            });
        }
        this.searchField.setOnEditorActionListener(new ArticleViewer$$ExternalSyntheticLambda7(this));
        this.searchField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ArticleViewer.this.ignoreOnTextChange) {
                    boolean unused = ArticleViewer.this.ignoreOnTextChange = false;
                    return;
                }
                ArticleViewer.this.processSearch(s.toString().toLowerCase());
                if (ArticleViewer.this.clearButton == null) {
                    return;
                }
                if (TextUtils.isEmpty(s)) {
                    if (ArticleViewer.this.clearButton.getTag() != null) {
                        ArticleViewer.this.clearButton.setTag((Object) null);
                        ArticleViewer.this.clearButton.clearAnimation();
                        if (ArticleViewer.this.animateClear) {
                            ArticleViewer.this.clearButton.animate().setInterpolator(new DecelerateInterpolator()).alpha(0.0f).setDuration(180).scaleY(0.0f).scaleX(0.0f).rotation(45.0f).withEndAction(new ArticleViewer$13$$ExternalSyntheticLambda0(this)).start();
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

            /* renamed from: lambda$onTextChanged$0$org-telegram-ui-ArticleViewer$13  reason: not valid java name */
            public /* synthetic */ void m2706lambda$onTextChanged$0$orgtelegramuiArticleViewer$13() {
                ArticleViewer.this.clearButton.setVisibility(4);
            }

            public void afterTextChanged(Editable s) {
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
        r04.setImageDrawable(new CloseProgressDrawable2() {
            /* access modifiers changed from: protected */
            public int getCurrentColor() {
                return Theme.getColor("windowBackgroundWhiteBlackText");
            }
        });
        this.clearButton.setScaleType(ImageView.ScaleType.CENTER);
        this.clearButton.setAlpha(0.0f);
        this.clearButton.setRotation(45.0f);
        this.clearButton.setScaleX(0.0f);
        this.clearButton.setScaleY(0.0f);
        this.clearButton.setOnClickListener(new ArticleViewer$$ExternalSyntheticLambda41(this));
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
        this.backButton.setOnClickListener(new ArticleViewer$$ExternalSyntheticLambda42(this));
        this.backButton.setContentDescription(LocaleController.getString("AccDescrGoBack", NUM));
        int i3 = inputType;
        AnonymousClass16 r12 = r0;
        AnonymousClass16 r05 = new ActionBarMenuItem(this.parentActivity, (ActionBarMenu) null, NUM, -5000269) {
            public void toggleSubMenu() {
                super.toggleSubMenu();
                ArticleViewer.this.listView[0].stopScroll();
                ArticleViewer.this.checkScrollAnimated();
            }
        };
        this.menuButton = r12;
        r12.setLayoutInScreen(true);
        this.menuButton.setDuplicateParentStateEnabled(false);
        this.menuButton.setClickable(true);
        this.menuButton.setIcon(NUM);
        this.menuButton.addSubItem(1, NUM, (CharSequence) LocaleController.getString("Search", NUM));
        this.menuButton.addSubItem(2, NUM, (CharSequence) LocaleController.getString("ShareFile", NUM));
        this.menuButton.addSubItem(3, NUM, (CharSequence) LocaleController.getString("OpenInExternalApp", NUM));
        this.menuButton.addSubItem(4, NUM, (CharSequence) LocaleController.getString("Settings", NUM));
        this.menuButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM));
        this.menuButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.menuContainer.addView(this.menuButton, LayoutHelper.createFrame(48, 56.0f));
        ContextProgressView contextProgressView = new ContextProgressView(activity2, 2);
        this.progressView = contextProgressView;
        contextProgressView.setVisibility(8);
        this.menuContainer.addView(this.progressView, LayoutHelper.createFrame(48, 56.0f));
        this.menuButton.setOnClickListener(new ArticleViewer$$ExternalSyntheticLambda43(this));
        this.menuButton.setDelegate(new ArticleViewer$$ExternalSyntheticLambda34(this));
        AnonymousClass17 r06 = new FrameLayout(this.parentActivity) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.searchPanel = r06;
        r06.setOnTouchListener(ArticleViewer$$ExternalSyntheticLambda5.INSTANCE);
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
        this.searchUpButton.setOnClickListener(new ArticleViewer$$ExternalSyntheticLambda1(this));
        this.searchUpButton.setContentDescription(LocaleController.getString("AccDescrSearchNext", NUM));
        ImageView imageView3 = new ImageView(this.parentActivity);
        this.searchDownButton = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        this.searchDownButton.setImageResource(NUM);
        this.searchDownButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
        this.searchDownButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("actionBarActionModeDefaultSelector"), 1));
        this.searchPanel.addView(this.searchDownButton, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, 0.0f, 0.0f, 0.0f));
        this.searchDownButton.setOnClickListener(new ArticleViewer$$ExternalSyntheticLambda2(this));
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
        this.windowLayoutParams.format = -3;
        this.windowLayoutParams.width = -1;
        this.windowLayoutParams.gravity = 51;
        this.windowLayoutParams.type = 98;
        this.windowLayoutParams.softInputMode = 48;
        this.windowLayoutParams.flags = 131072;
        int uiFlags = 1792;
        int navigationColor = Theme.getColor("windowBackgroundGray", (boolean[]) null, true);
        boolean isLightNavigation = AndroidUtilities.computePerceivedBrightness(navigationColor) >= 0.721f;
        if (isLightNavigation && Build.VERSION.SDK_INT >= 26) {
            uiFlags = 1792 | 16;
            this.navigationBarPaint.setColor(navigationColor);
        } else if (!isLightNavigation) {
            this.navigationBarPaint.setColor(navigationColor);
        }
        this.windowLayoutParams.systemUiVisibility = uiFlags;
        if (Build.VERSION.SDK_INT >= 21) {
            this.windowLayoutParams.flags |= -NUM;
            if (Build.VERSION.SDK_INT >= 28) {
                this.windowLayoutParams.layoutInDisplayCutoutMode = 1;
            }
        }
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = new TextSelectionHelper.ArticleTextSelectionHelper();
        this.textSelectionHelper = articleTextSelectionHelper;
        articleTextSelectionHelper.setParentView(this.listView[0]);
        if (MessagesController.getGlobalMainSettings().getBoolean("translate_button", false)) {
            this.textSelectionHelper.setOnTranslate(new ArticleViewer$$ExternalSyntheticLambda36(this));
        }
        this.textSelectionHelper.layoutManager = this.layoutManager[0];
        this.textSelectionHelper.setCallback(new TextSelectionHelper.Callback() {
            public void onStateChanged(boolean isSelected) {
                if (isSelected) {
                    ArticleViewer.this.showSearch(false);
                }
            }

            public void onTextCopied() {
                if (Build.VERSION.SDK_INT < 31) {
                    BulletinFactory.of(ArticleViewer.this.containerView, (Theme.ResourcesProvider) null).createCopyBulletin(LocaleController.getString("TextCopied", NUM)).show();
                }
            }
        });
        this.containerView.addView(this.textSelectionHelper.getOverlayView(activity2));
        PinchToZoomHelper pinchToZoomHelper2 = new PinchToZoomHelper(this.containerView, this.windowView);
        this.pinchToZoomHelper = pinchToZoomHelper2;
        pinchToZoomHelper2.setClipBoundsListener(new PinchToZoomHelper.ClipBoundsListener() {
            public void getClipTopBottom(float[] topBottom) {
                topBottom[0] = (float) ArticleViewer.this.currentHeaderHeight;
                topBottom[1] = (float) ArticleViewer.this.listView[0].getMeasuredHeight();
            }
        });
        this.pinchToZoomHelper.setCallback(new PinchToZoomHelper.Callback() {
            public /* synthetic */ TextureView getCurrentTextureView() {
                return PinchToZoomHelper.Callback.CC.$default$getCurrentTextureView(this);
            }

            public /* synthetic */ void onZoomFinished(MessageObject messageObject) {
                PinchToZoomHelper.Callback.CC.$default$onZoomFinished(this, messageObject);
            }

            public void onZoomStarted(MessageObject messageObject) {
                if (ArticleViewer.this.listView[0] != null) {
                    ArticleViewer.this.listView[0].cancelClickRunnables(true);
                }
            }
        });
        updatePaintColors();
    }

    static /* synthetic */ WindowInsets lambda$setParentActivity$8(View v, WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= 30) {
            return WindowInsets.CONSUMED;
        }
        return insets.consumeSystemWindowInsets();
    }

    /* renamed from: lambda$setParentActivity$9$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ boolean m2696lambda$setParentActivity$9$orgtelegramuiArticleViewer(View view, int position) {
        if (!(view instanceof BlockRelatedArticlesCell)) {
            return false;
        }
        BlockRelatedArticlesCell cell = (BlockRelatedArticlesCell) view;
        showCopyPopup(cell.currentBlock.parent.articles.get(cell.currentBlock.num).url);
        return true;
    }

    /* renamed from: lambda$setParentActivity$12$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2684lambda$setParentActivity$12$orgtelegramuiArticleViewer(WebpageAdapter webpageAdapter, View view, int position, float x, float y) {
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper = this.textSelectionHelper;
        if (articleTextSelectionHelper != null) {
            if (articleTextSelectionHelper.isSelectionMode()) {
                this.textSelectionHelper.clear();
                return;
            }
            this.textSelectionHelper.clear();
        }
        if ((view instanceof ReportCell) && webpageAdapter.currentPage != null) {
            ReportCell cell = (ReportCell) view;
            if (this.previewsReqId != 0) {
                return;
            }
            if (!cell.hasViews || x >= ((float) (view.getMeasuredWidth() / 2))) {
                TLObject object = MessagesController.getInstance(this.currentAccount).getUserOrChat("previews");
                if (object instanceof TLRPC.TL_user) {
                    openPreviewsChat((TLRPC.User) object, webpageAdapter.currentPage.id);
                    return;
                }
                int currentAccount2 = UserConfig.selectedAccount;
                long pageId = webpageAdapter.currentPage.id;
                showProgressView(true, true);
                TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                req.username = "previews";
                this.previewsReqId = ConnectionsManager.getInstance(currentAccount2).sendRequest(req, new ArticleViewer$$ExternalSyntheticLambda28(this, currentAccount2, pageId));
            }
        } else if (position >= 0 && position < webpageAdapter.localBlocks.size()) {
            TLRPC.PageBlock pageBlock = (TLRPC.PageBlock) webpageAdapter.localBlocks.get(position);
            TLRPC.PageBlock originalBlock = pageBlock;
            TLRPC.PageBlock pageBlock2 = getLastNonListPageBlock(pageBlock);
            if (pageBlock2 instanceof TL_pageBlockDetailsChild) {
                pageBlock2 = ((TL_pageBlockDetailsChild) pageBlock2).block;
            }
            if (pageBlock2 instanceof TLRPC.TL_pageBlockChannel) {
                MessagesController.getInstance(this.currentAccount).openByUserName(((TLRPC.TL_pageBlockChannel) pageBlock2).channel.username, this.parentFragment, 2);
                close(false, true);
            } else if (pageBlock2 instanceof TL_pageBlockRelatedArticlesChild) {
                TL_pageBlockRelatedArticlesChild pageBlockRelatedArticlesChild = (TL_pageBlockRelatedArticlesChild) pageBlock2;
                openWebpageUrl(pageBlockRelatedArticlesChild.parent.articles.get(pageBlockRelatedArticlesChild.num).url, (String) null);
            } else if (pageBlock2 instanceof TLRPC.TL_pageBlockDetails) {
                View view2 = getLastNonListCell(view);
                if (view2 instanceof BlockDetailsCell) {
                    this.pressedLinkOwnerLayout = null;
                    this.pressedLinkOwnerView = null;
                    if (webpageAdapter.blocks.indexOf(originalBlock) >= 0) {
                        TLRPC.TL_pageBlockDetails pageBlockDetails = (TLRPC.TL_pageBlockDetails) pageBlock2;
                        pageBlockDetails.open = true ^ pageBlockDetails.open;
                        int oldCount = webpageAdapter.getItemCount();
                        webpageAdapter.updateRows();
                        int changeCount = Math.abs(webpageAdapter.getItemCount() - oldCount);
                        BlockDetailsCell cell2 = (BlockDetailsCell) view2;
                        cell2.arrow.setAnimationProgressAnimated(pageBlockDetails.open ? 0.0f : 1.0f);
                        cell2.invalidate();
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
    }

    /* renamed from: lambda$setParentActivity$11$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2683lambda$setParentActivity$11$orgtelegramuiArticleViewer(int currentAccount2, long pageId, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ArticleViewer$$ExternalSyntheticLambda24(this, response, currentAccount2, pageId));
    }

    /* renamed from: lambda$setParentActivity$10$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2682lambda$setParentActivity$10$orgtelegramuiArticleViewer(TLObject response, int currentAccount2, long pageId) {
        if (this.previewsReqId != 0) {
            this.previewsReqId = 0;
            showProgressView(true, false);
            if (response != null) {
                TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
                MessagesController.getInstance(currentAccount2).putUsers(res.users, false);
                MessagesStorage.getInstance(currentAccount2).putUsersAndChats(res.users, res.chats, false, true);
                if (!res.users.isEmpty()) {
                    openPreviewsChat(res.users.get(0), pageId);
                }
            }
        }
    }

    /* renamed from: lambda$setParentActivity$13$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2685lambda$setParentActivity$13$orgtelegramuiArticleViewer(View v) {
        this.listView[0].smoothScrollToPosition(0);
    }

    /* renamed from: lambda$setParentActivity$14$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2686lambda$setParentActivity$14$orgtelegramuiArticleViewer() {
        float tick;
        float progressLeft = 0.7f - this.lineProgressView.getCurrentProgress();
        if (progressLeft > 0.0f) {
            if (progressLeft < 0.25f) {
                tick = 0.01f;
            } else {
                tick = 0.02f;
            }
            LineProgressView lineProgressView2 = this.lineProgressView;
            lineProgressView2.setProgress(lineProgressView2.getCurrentProgress() + tick, true);
            AndroidUtilities.runOnUIThread(this.lineProgressTickRunnable, 100);
        }
    }

    /* renamed from: lambda$setParentActivity$15$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ boolean m2687lambda$setParentActivity$15$orgtelegramuiArticleViewer(TextView v, int actionId, KeyEvent event) {
        if (event == null) {
            return false;
        }
        if ((event.getAction() != 1 || event.getKeyCode() != 84) && (event.getAction() != 0 || event.getKeyCode() != 66)) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.searchField);
        return false;
    }

    /* renamed from: lambda$setParentActivity$16$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2688lambda$setParentActivity$16$orgtelegramuiArticleViewer(View v) {
        if (this.searchField.length() != 0) {
            this.searchField.setText("");
        }
        this.searchField.requestFocus();
        AndroidUtilities.showKeyboard(this.searchField);
    }

    /* renamed from: lambda$setParentActivity$17$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2689lambda$setParentActivity$17$orgtelegramuiArticleViewer(View v) {
        if (this.searchContainer.getTag() != null) {
            showSearch(false);
        } else {
            close(true, true);
        }
    }

    /* renamed from: lambda$setParentActivity$18$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2690lambda$setParentActivity$18$orgtelegramuiArticleViewer(View v) {
        this.menuButton.toggleSubMenu();
    }

    /* renamed from: lambda$setParentActivity$20$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2692lambda$setParentActivity$20$orgtelegramuiArticleViewer(int id) {
        String webPageUrl;
        int i = id;
        if (this.adapter[0].currentPage != null && this.parentActivity != null) {
            if (i == 1) {
                showSearch(true);
            } else if (i == 2) {
                showDialog(new ShareAlert(this.parentActivity, (ArrayList<MessageObject>) null, this.adapter[0].currentPage.url, false, this.adapter[0].currentPage.url, false));
            } else if (i == 3) {
                if (!TextUtils.isEmpty(this.adapter[0].currentPage.cached_page.url)) {
                    webPageUrl = this.adapter[0].currentPage.cached_page.url;
                } else {
                    webPageUrl = this.adapter[0].currentPage.url;
                }
                Browser.openUrl((Context) this.parentActivity, webPageUrl, true, false);
            } else if (i == 4) {
                BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
                builder.setApplyTopPadding(false);
                LinearLayout settingsContainer = new LinearLayout(this.parentActivity);
                settingsContainer.setPadding(0, 0, 0, AndroidUtilities.dp(4.0f));
                settingsContainer.setOrientation(1);
                HeaderCell headerCell = new HeaderCell(this.parentActivity);
                headerCell.setText(LocaleController.getString("FontSize", NUM));
                settingsContainer.addView(headerCell, LayoutHelper.createLinear(-2, -2, 51, 3, 1, 3, 0));
                settingsContainer.addView(new TextSizeCell(this.parentActivity), LayoutHelper.createLinear(-1, -2, 51, 3, 0, 3, 0));
                HeaderCell headerCell2 = new HeaderCell(this.parentActivity);
                headerCell2.setText(LocaleController.getString("FontType", NUM));
                settingsContainer.addView(headerCell2, LayoutHelper.createLinear(-2, -2, 51, 3, 4, 3, 2));
                int a = 0;
                while (a < 2) {
                    this.fontCells[a] = new FontCell(this.parentActivity);
                    switch (a) {
                        case 0:
                            this.fontCells[a].setTextAndTypeface(LocaleController.getString("Default", NUM), Typeface.DEFAULT);
                            break;
                        case 1:
                            this.fontCells[a].setTextAndTypeface("Serif", Typeface.SERIF);
                            break;
                    }
                    this.fontCells[a].select(a == this.selectedFont, false);
                    this.fontCells[a].setTag(Integer.valueOf(a));
                    this.fontCells[a].setOnClickListener(new ArticleViewer$$ExternalSyntheticLambda44(this));
                    settingsContainer.addView(this.fontCells[a], LayoutHelper.createLinear(-1, 50));
                    a++;
                }
                builder.setCustomView(settingsContainer);
                BottomSheet create = builder.create();
                this.linkSheet = create;
                showDialog(create);
            }
        }
    }

    /* renamed from: lambda$setParentActivity$19$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2691lambda$setParentActivity$19$orgtelegramuiArticleViewer(View v) {
        int num = ((Integer) v.getTag()).intValue();
        this.selectedFont = num;
        int a1 = 0;
        while (a1 < 2) {
            this.fontCells[a1].select(a1 == num, true);
            a1++;
        }
        updatePaintFonts();
        for (int i = 0; i < this.listView.length; i++) {
            this.adapter[i].notifyDataSetChanged();
        }
    }

    static /* synthetic */ boolean lambda$setParentActivity$21(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$setParentActivity$22$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2693lambda$setParentActivity$22$orgtelegramuiArticleViewer(View view) {
        scrollToSearchIndex(this.currentSearchIndex - 1);
    }

    /* renamed from: lambda$setParentActivity$23$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2694lambda$setParentActivity$23$orgtelegramuiArticleViewer(View view) {
        scrollToSearchIndex(this.currentSearchIndex + 1);
    }

    /* renamed from: lambda$setParentActivity$24$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2695lambda$setParentActivity$24$orgtelegramuiArticleViewer(CharSequence text, String fromLang, String toLang, Runnable onAlertDismiss) {
        TranslateAlert.showAlert(this.parentActivity, this.parentFragment, fromLang, toLang, text, false, (TranslateAlert.OnLinkPress) null, onAlertDismiss);
    }

    /* access modifiers changed from: private */
    public void showSearch(final boolean show) {
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null) {
            if ((frameLayout.getTag() != null) != show) {
                this.searchContainer.setTag(show ? 1 : null);
                this.searchResults.clear();
                this.searchText = null;
                this.adapter[0].searchTextOffset.clear();
                this.currentSearchIndex = 0;
                float f = 1.0f;
                if (this.attachedToWindow) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.setDuration(250);
                    if (show) {
                        this.searchContainer.setVisibility(0);
                        this.backDrawable.setRotation(0.0f, true);
                    } else {
                        this.menuButton.setVisibility(0);
                        this.listView[0].invalidateViews();
                        AndroidUtilities.hideKeyboard(this.searchField);
                        updateWindowLayoutParamsForSearch();
                    }
                    ArrayList<Animator> animators = new ArrayList<>();
                    if (Build.VERSION.SDK_INT >= 21) {
                        if (show) {
                            this.searchContainer.setAlpha(1.0f);
                        }
                        int x = this.menuContainer.getLeft() + (this.menuContainer.getMeasuredWidth() / 2);
                        int y = this.menuContainer.getTop() + (this.menuContainer.getMeasuredHeight() / 2);
                        float rad = (float) Math.sqrt((double) ((x * x) + (y * y)));
                        Animator animator = ViewAnimationUtils.createCircularReveal(this.searchContainer, x, y, show ? 0.0f : rad, show ? rad : 0.0f);
                        animators.add(animator);
                        animator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (!show) {
                                    ArticleViewer.this.searchContainer.setAlpha(0.0f);
                                }
                            }
                        });
                    } else {
                        FrameLayout frameLayout2 = this.searchContainer;
                        Property property = View.ALPHA;
                        float[] fArr = new float[1];
                        fArr[0] = show ? 1.0f : 0.0f;
                        animators.add(ObjectAnimator.ofFloat(frameLayout2, property, fArr));
                    }
                    if (!show) {
                        animators.add(ObjectAnimator.ofFloat(this.searchPanel, View.ALPHA, new float[]{0.0f}));
                    }
                    View view = this.searchShadow;
                    Property property2 = View.ALPHA;
                    float[] fArr2 = new float[1];
                    if (!show) {
                        f = 0.0f;
                    }
                    fArr2[0] = f;
                    animators.add(ObjectAnimator.ofFloat(view, property2, fArr2));
                    animatorSet.playTogether(animators);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (show) {
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

                        public void onAnimationStart(Animator animation) {
                            if (!show) {
                                ArticleViewer.this.backDrawable.setRotation(1.0f, true);
                            }
                        }
                    });
                    animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    if (show || AndroidUtilities.usingHardwareInput || !this.keyboardVisible) {
                        animatorSet.start();
                        return;
                    }
                    this.runAfterKeyboardClose = animatorSet;
                    AndroidUtilities.runOnUIThread(new ArticleViewer$$ExternalSyntheticLambda16(this), 300);
                    return;
                }
                this.searchContainer.setAlpha(show ? 1.0f : 0.0f);
                this.menuButton.setVisibility(show ? 4 : 0);
                this.backDrawable.setRotation(show ? 0.0f : 1.0f, false);
                View view2 = this.searchShadow;
                if (!show) {
                    f = 0.0f;
                }
                view2.setAlpha(f);
                if (show) {
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

    /* renamed from: lambda$showSearch$25$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2704lambda$showSearch$25$orgtelegramuiArticleViewer() {
        AnimatorSet animatorSet = this.runAfterKeyboardClose;
        if (animatorSet != null) {
            animatorSet.start();
            this.runAfterKeyboardClose = null;
        }
    }

    /* access modifiers changed from: private */
    public void updateWindowLayoutParamsForSearch() {
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
            int count = this.searchResults.size();
            if (count < 0) {
                this.searchCountText.setText("");
            } else if (count == 0) {
                this.searchCountText.setText(LocaleController.getString("NoResult", NUM));
            } else if (count == 1) {
                this.searchCountText.setText(LocaleController.getString("OneResult", NUM));
            } else {
                this.searchCountText.setText(String.format(LocaleController.getPluralString("CountOfResults", count), new Object[]{Integer.valueOf(this.currentSearchIndex + 1), Integer.valueOf(count)}));
            }
        }
    }

    private static class SearchResult {
        /* access modifiers changed from: private */
        public TLRPC.PageBlock block;
        /* access modifiers changed from: private */
        public int index;
        /* access modifiers changed from: private */
        public Object text;

        private SearchResult() {
        }
    }

    /* access modifiers changed from: private */
    public void processSearch(String text) {
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.searchRunnable = null;
        }
        if (TextUtils.isEmpty(text)) {
            this.searchResults.clear();
            this.searchText = text;
            this.adapter[0].searchTextOffset.clear();
            this.searchPanel.setVisibility(4);
            this.listView[0].invalidateViews();
            scrollToSearchIndex(0);
            this.lastSearchIndex = -1;
            return;
        }
        int searchIndex = this.lastSearchIndex + 1;
        this.lastSearchIndex = searchIndex;
        ArticleViewer$$ExternalSyntheticLambda21 articleViewer$$ExternalSyntheticLambda21 = new ArticleViewer$$ExternalSyntheticLambda21(this, text, searchIndex);
        this.searchRunnable = articleViewer$$ExternalSyntheticLambda21;
        AndroidUtilities.runOnUIThread(articleViewer$$ExternalSyntheticLambda21, 400);
    }

    /* renamed from: lambda$processSearch$28$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2681lambda$processSearch$28$orgtelegramuiArticleViewer(String text, int searchIndex) {
        HashMap<Object, TLRPC.PageBlock> copy = new HashMap<>(this.adapter[0].textToBlocks);
        ArrayList<Object> array = new ArrayList<>(this.adapter[0].textBlocks);
        this.searchRunnable = null;
        Utilities.searchQueue.postRunnable(new ArticleViewer$$ExternalSyntheticLambda23(this, array, copy, text, searchIndex));
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0061  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0090 A[SYNTHETIC] */
    /* renamed from: lambda$processSearch$27$org-telegram-ui-ArticleViewer  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m2680lambda$processSearch$27$orgtelegramuiArticleViewer(java.util.ArrayList r19, java.util.HashMap r20, java.lang.String r21, int r22) {
        /*
            r18 = this;
            r7 = r18
            r8 = r21
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r9 = r0
            r0 = 0
            int r10 = r19.size()
            r11 = r0
        L_0x0010:
            if (r11 >= r10) goto L_0x0094
            r12 = r19
            java.lang.Object r13 = r12.get(r11)
            r14 = r20
            java.lang.Object r0 = r14.get(r13)
            r15 = r0
            org.telegram.tgnet.TLRPC$PageBlock r15 = (org.telegram.tgnet.TLRPC.PageBlock) r15
            r16 = 0
            boolean r0 = r13 instanceof org.telegram.tgnet.TLRPC.RichText
            if (r0 == 0) goto L_0x004f
            r17 = r13
            org.telegram.tgnet.TLRPC$RichText r17 = (org.telegram.tgnet.TLRPC.RichText) r17
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r0 = r7.adapter
            r1 = 0
            r1 = r0[r1]
            r2 = 0
            r6 = 1000(0x3e8, float:1.401E-42)
            r0 = r18
            r3 = r17
            r4 = r17
            r5 = r15
            java.lang.CharSequence r0 = r0.getText((org.telegram.ui.ArticleViewer.WebpageAdapter) r1, (android.view.View) r2, (org.telegram.tgnet.TLRPC.RichText) r3, (org.telegram.tgnet.TLRPC.RichText) r4, (org.telegram.tgnet.TLRPC.PageBlock) r5, (int) r6)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x005d
            java.lang.String r1 = r0.toString()
            java.lang.String r1 = r1.toLowerCase()
            r16 = r1
            goto L_0x005d
        L_0x004f:
            boolean r0 = r13 instanceof java.lang.String
            if (r0 == 0) goto L_0x005d
            r0 = r13
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r16 = r0.toLowerCase()
            r0 = r16
            goto L_0x005f
        L_0x005d:
            r0 = r16
        L_0x005f:
            if (r0 == 0) goto L_0x0090
            r1 = 0
        L_0x0062:
            int r2 = r0.indexOf(r8, r1)
            r3 = r2
            if (r2 < 0) goto L_0x0090
            int r2 = r21.length()
            int r1 = r3 + r2
            if (r3 == 0) goto L_0x007d
            int r2 = r3 + -1
            char r2 = r0.charAt(r2)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isPunctuationCharacter(r2)
            if (r2 == 0) goto L_0x0062
        L_0x007d:
            org.telegram.ui.ArticleViewer$SearchResult r2 = new org.telegram.ui.ArticleViewer$SearchResult
            r4 = 0
            r2.<init>()
            int unused = r2.index = r3
            org.telegram.tgnet.TLRPC.PageBlock unused = r2.block = r15
            java.lang.Object unused = r2.text = r13
            r9.add(r2)
            goto L_0x0062
        L_0x0090:
            int r11 = r11 + 1
            goto L_0x0010
        L_0x0094:
            r12 = r19
            r14 = r20
            org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda18 r0 = new org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda18
            r1 = r22
            r0.<init>(r7, r1, r9, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.m2680lambda$processSearch$27$orgtelegramuiArticleViewer(java.util.ArrayList, java.util.HashMap, java.lang.String, int):void");
    }

    /* renamed from: lambda$processSearch$26$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2679lambda$processSearch$26$orgtelegramuiArticleViewer(int searchIndex, ArrayList results, String text) {
        if (searchIndex == this.lastSearchIndex) {
            this.searchPanel.setAlpha(1.0f);
            this.searchPanel.setVisibility(0);
            this.searchResults = results;
            this.searchText = text;
            this.adapter[0].searchTextOffset.clear();
            this.listView[0].invalidateViews();
            scrollToSearchIndex(0);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v18, resolved type: java.lang.Integer} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void scrollToSearchIndex(int r15) {
        /*
            r14 = this;
            if (r15 < 0) goto L_0x0178
            java.util.ArrayList<org.telegram.ui.ArticleViewer$SearchResult> r0 = r14.searchResults
            int r0 = r0.size()
            if (r15 < r0) goto L_0x000c
            goto L_0x0178
        L_0x000c:
            r14.currentSearchIndex = r15
            r14.updateSearchButtons()
            java.util.ArrayList<org.telegram.ui.ArticleViewer$SearchResult> r0 = r14.searchResults
            java.lang.Object r0 = r0.get(r15)
            org.telegram.ui.ArticleViewer$SearchResult r0 = (org.telegram.ui.ArticleViewer.SearchResult) r0
            org.telegram.tgnet.TLRPC$PageBlock r1 = r0.block
            org.telegram.tgnet.TLRPC$PageBlock r1 = r14.getLastNonListPageBlock(r1)
            r2 = -1
            r3 = 0
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r4 = r14.adapter
            r5 = 0
            r4 = r4[r5]
            java.util.ArrayList r4 = r4.blocks
            int r4 = r4.size()
        L_0x0030:
            if (r3 >= r4) goto L_0x006f
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r6 = r14.adapter
            r6 = r6[r5]
            java.util.ArrayList r6 = r6.blocks
            java.lang.Object r6 = r6.get(r3)
            org.telegram.tgnet.TLRPC$PageBlock r6 = (org.telegram.tgnet.TLRPC.PageBlock) r6
            boolean r7 = r6 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild
            if (r7 == 0) goto L_0x006c
            r7 = r6
            org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r7 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r7
            org.telegram.tgnet.TLRPC$PageBlock r8 = r7.block
            org.telegram.tgnet.TLRPC$PageBlock r9 = r0.block
            if (r8 == r9) goto L_0x0057
            org.telegram.tgnet.TLRPC$PageBlock r8 = r7.block
            if (r8 != r1) goto L_0x006c
        L_0x0057:
            boolean r8 = r14.openAllParentBlocks(r7)
            if (r8 == 0) goto L_0x006f
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r8 = r14.adapter
            r8 = r8[r5]
            r8.updateRows()
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r8 = r14.adapter
            r8 = r8[r5]
            r8.notifyDataSetChanged()
            goto L_0x006f
        L_0x006c:
            int r3 = r3 + 1
            goto L_0x0030
        L_0x006f:
            r3 = 0
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r4 = r14.adapter
            r4 = r4[r5]
            java.util.ArrayList r4 = r4.localBlocks
            int r4 = r4.size()
        L_0x007c:
            if (r3 >= r4) goto L_0x00b2
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r6 = r14.adapter
            r6 = r6[r5]
            java.util.ArrayList r6 = r6.localBlocks
            java.lang.Object r6 = r6.get(r3)
            org.telegram.tgnet.TLRPC$PageBlock r6 = (org.telegram.tgnet.TLRPC.PageBlock) r6
            org.telegram.tgnet.TLRPC$PageBlock r7 = r0.block
            if (r6 == r7) goto L_0x00b1
            if (r6 != r1) goto L_0x0095
            goto L_0x00b1
        L_0x0095:
            boolean r7 = r6 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild
            if (r7 == 0) goto L_0x00ae
            r7 = r6
            org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r7 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r7
            org.telegram.tgnet.TLRPC$PageBlock r8 = r7.block
            org.telegram.tgnet.TLRPC$PageBlock r9 = r0.block
            if (r8 == r9) goto L_0x00ac
            org.telegram.tgnet.TLRPC$PageBlock r8 = r7.block
            if (r8 != r1) goto L_0x00ae
        L_0x00ac:
            r2 = r3
            goto L_0x00b2
        L_0x00ae:
            int r3 = r3 + 1
            goto L_0x007c
        L_0x00b1:
            r2 = r3
        L_0x00b2:
            r3 = -1
            if (r2 != r3) goto L_0x00b6
            return
        L_0x00b6:
            boolean r3 = r1 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild
            if (r3 == 0) goto L_0x00d1
            r3 = r1
            org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r3 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r3
            boolean r3 = r14.openAllParentBlocks(r3)
            if (r3 == 0) goto L_0x00d1
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r3 = r14.adapter
            r3 = r3[r5]
            r3.updateRows()
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r3 = r14.adapter
            r3 = r3[r5]
            r3.notifyDataSetChanged()
        L_0x00d1:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = r14.searchText
            r3.append(r4)
            org.telegram.tgnet.TLRPC$PageBlock r4 = r0.block
            r3.append(r4)
            java.lang.Object r4 = r0.text
            r3.append(r4)
            int r4 = r0.index
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r4 = r14.adapter
            r4 = r4[r5]
            java.util.HashMap r4 = r4.searchTextOffset
            java.lang.Object r4 = r4.get(r3)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 != 0) goto L_0x0154
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r6 = r14.adapter
            r6 = r6[r5]
            org.telegram.tgnet.TLRPC$PageBlock r7 = r0.block
            int r6 = r6.getTypeForBlock(r7)
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r7 = r14.adapter
            r7 = r7[r5]
            r8 = 0
            androidx.recyclerview.widget.RecyclerView$ViewHolder r7 = r7.onCreateViewHolder(r8, r6)
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r8 = r14.adapter
            r8 = r8[r5]
            org.telegram.tgnet.TLRPC$PageBlock r11 = r0.block
            r12 = 0
            r13 = 0
            r9 = r6
            r10 = r7
            r8.bindBlockToHolder(r9, r10, r11, r12, r13)
            android.view.View r8 = r7.itemView
            org.telegram.ui.Components.RecyclerListView[] r9 = r14.listView
            r9 = r9[r5]
            int r9 = r9.getMeasuredWidth()
            r10 = 1073741824(0x40000000, float:2.0)
            int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r10)
            int r10 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r5)
            r8.measure(r9, r10)
            org.telegram.ui.ArticleViewer$WebpageAdapter[] r8 = r14.adapter
            r8 = r8[r5]
            java.util.HashMap r8 = r8.searchTextOffset
            java.lang.Object r8 = r8.get(r3)
            r4 = r8
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 != 0) goto L_0x0154
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
        L_0x0154:
            androidx.recyclerview.widget.LinearLayoutManager[] r6 = r14.layoutManager
            r6 = r6[r5]
            int r7 = r14.currentHeaderHeight
            r8 = 1113587712(0x42600000, float:56.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 - r8
            int r8 = r4.intValue()
            int r7 = r7 - r8
            r8 = 1120403456(0x42CLASSNAME, float:100.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 + r8
            r6.scrollToPositionWithOffset(r2, r7)
            org.telegram.ui.Components.RecyclerListView[] r6 = r14.listView
            r5 = r6[r5]
            r5.invalidateViews()
            return
        L_0x0178:
            r14.updateSearchButtons()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.scrollToSearchIndex(int):void");
    }

    public static class ScrollEvaluator extends IntEvaluator {
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            return super.evaluate(fraction, startValue, endValue);
        }
    }

    /* access modifiers changed from: private */
    public void checkScrollAnimated() {
        if (this.currentHeaderHeight != AndroidUtilities.dp(56.0f)) {
            ValueAnimator va = ValueAnimator.ofObject(new IntEvaluator(), new Object[]{Integer.valueOf(this.currentHeaderHeight), Integer.valueOf(AndroidUtilities.dp(56.0f))}).setDuration(180);
            va.setInterpolator(new DecelerateInterpolator());
            va.addUpdateListener(new ArticleViewer$$ExternalSyntheticLambda0(this));
            va.start();
        }
    }

    /* renamed from: lambda$checkScrollAnimated$29$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2665lambda$checkScrollAnimated$29$orgtelegramuiArticleViewer(ValueAnimator animation) {
        setCurrentHeaderHeight(((Integer) animation.getAnimatedValue()).intValue());
    }

    /* access modifiers changed from: private */
    public void setCurrentHeaderHeight(int newHeight) {
        if (this.searchContainer.getTag() == null) {
            int maxHeight = AndroidUtilities.dp(56.0f);
            int minHeight = Math.max(AndroidUtilities.statusBarHeight, AndroidUtilities.dp(24.0f));
            if (newHeight < minHeight) {
                newHeight = minHeight;
            } else if (newHeight > maxHeight) {
                newHeight = maxHeight;
            }
            float heightDiff = (float) (maxHeight - minHeight);
            if (heightDiff == 0.0f) {
                heightDiff = 1.0f;
            }
            this.currentHeaderHeight = newHeight;
            float scale = ((((float) (newHeight - minHeight)) / heightDiff) * 0.2f) + 0.8f;
            this.backButton.setScaleX(scale);
            this.backButton.setScaleY(scale);
            this.backButton.setTranslationY((float) ((maxHeight - this.currentHeaderHeight) / 2));
            this.menuContainer.setScaleX(scale);
            this.menuContainer.setScaleY(scale);
            this.titleTextView.setScaleX(scale);
            this.titleTextView.setScaleY(scale);
            this.lineProgressView.setScaleY(((((float) (newHeight - minHeight)) / heightDiff) * 0.5f) + 0.5f);
            this.menuContainer.setTranslationY((float) ((maxHeight - this.currentHeaderHeight) / 2));
            this.titleTextView.setTranslationY((float) ((maxHeight - this.currentHeaderHeight) / 2));
            this.headerView.setTranslationY((float) (this.currentHeaderHeight - maxHeight));
            this.searchShadow.setTranslationY((float) (this.currentHeaderHeight - maxHeight));
            this.menuButton.setAdditionalYOffset(((-(this.currentHeaderHeight - maxHeight)) / 2) + (Build.VERSION.SDK_INT < 21 ? AndroidUtilities.statusBarHeight : 0));
            this.textSelectionHelper.setTopOffset(this.currentHeaderHeight);
            int i = 0;
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

    /* access modifiers changed from: private */
    public void checkScroll(int dy) {
        setCurrentHeaderHeight(this.currentHeaderHeight - dy);
    }

    private void openPreviewsChat(TLRPC.User user, long wid) {
        if (user != null && (this.parentActivity instanceof LaunchActivity)) {
            Bundle args = new Bundle();
            args.putLong("user_id", user.id);
            args.putString("botUser", "webpage" + wid);
            ((LaunchActivity) this.parentActivity).presentFragment(new ChatActivity(args), false, true);
            close(false, true);
        }
    }

    public boolean open(MessageObject messageObject) {
        return open(messageObject, (TLRPC.WebPage) null, (String) null, true);
    }

    public boolean open(TLRPC.TL_webPage webpage, String url) {
        return open((MessageObject) null, webpage, url, true);
    }

    private boolean open(MessageObject messageObject, TLRPC.WebPage webpage, String url, boolean first) {
        String anchor;
        TLRPC.WebPage webpage2;
        Paint paint;
        String webPageUrl;
        MessageObject messageObject2 = messageObject;
        String str = url;
        if (this.parentActivity == null) {
            return false;
        }
        if (this.isVisible && !this.collapsed) {
            return false;
        }
        if (messageObject2 == null && webpage == null) {
            return false;
        }
        String anchor2 = null;
        if (messageObject2 != null) {
            TLRPC.WebPage webpage3 = messageObject2.messageOwner.media.webpage;
            int a = 0;
            String url2 = str;
            while (true) {
                if (a >= messageObject2.messageOwner.entities.size()) {
                    break;
                }
                TLRPC.MessageEntity entity = messageObject2.messageOwner.entities.get(a);
                if (entity instanceof TLRPC.TL_messageEntityUrl) {
                    try {
                        url2 = messageObject2.messageOwner.message.substring(entity.offset, entity.offset + entity.length).toLowerCase();
                        if (!TextUtils.isEmpty(webpage3.cached_page.url)) {
                            webPageUrl = webpage3.cached_page.url.toLowerCase();
                        } else {
                            webPageUrl = webpage3.url.toLowerCase();
                        }
                        if (url2.contains(webPageUrl)) {
                            break;
                        } else if (webPageUrl.contains(url2)) {
                            break;
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                a++;
            }
            int lastIndexOf = url2.lastIndexOf(35);
            int index = lastIndexOf;
            if (lastIndexOf != -1) {
                anchor2 = url2.substring(index + 1);
            }
            anchor = anchor2;
            webpage2 = webpage3;
            String str2 = url2;
        } else {
            if (str != null) {
                int lastIndexOf2 = str.lastIndexOf(35);
                int index2 = lastIndexOf2;
                if (lastIndexOf2 != -1) {
                    webpage2 = webpage;
                    String str3 = str;
                    anchor = str.substring(index2 + 1);
                }
            }
            webpage2 = webpage;
            String str4 = str;
            anchor = null;
        }
        this.pagesStack.clear();
        this.collapsed = false;
        this.containerView.setTranslationX(0.0f);
        this.containerView.setTranslationY(0.0f);
        this.listView[0].setTranslationY(0.0f);
        this.listView[0].setTranslationX(0.0f);
        this.listView[1].setTranslationX(0.0f);
        this.listView[0].setAlpha(1.0f);
        this.windowView.setInnerTranslationX(0.0f);
        this.layoutManager[0].scrollToPositionWithOffset(0, 0);
        if (first) {
            setCurrentHeaderHeight(AndroidUtilities.dp(56.0f));
        } else {
            checkScrollAnimated();
        }
        boolean scrolledToAnchor = addPageToStack(webpage2, anchor, 0);
        if (first) {
            String anchorFinal = (scrolledToAnchor || anchor == null) ? null : anchor;
            TLRPC.TL_messages_getWebPage req = new TLRPC.TL_messages_getWebPage();
            req.url = webpage2.url;
            if ((webpage2.cached_page instanceof TLRPC.TL_pagePart_layer82) || webpage2.cached_page.part) {
                req.hash = 0;
            } else {
                req.hash = webpage2.hash;
            }
            int currentAccount2 = UserConfig.selectedAccount;
            ArticleViewer$$ExternalSyntheticLambda30 articleViewer$$ExternalSyntheticLambda30 = r1;
            ConnectionsManager instance = ConnectionsManager.getInstance(currentAccount2);
            paint = null;
            ArticleViewer$$ExternalSyntheticLambda30 articleViewer$$ExternalSyntheticLambda302 = new ArticleViewer$$ExternalSyntheticLambda30(this, webpage2, messageObject, currentAccount2, anchorFinal);
            instance.sendRequest(req, articleViewer$$ExternalSyntheticLambda30);
        } else {
            paint = null;
        }
        this.lastInsets = paint;
        if (!this.isVisible) {
            WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
            if (this.attachedToWindow) {
                try {
                    wm.removeView(this.windowView);
                } catch (Exception e2) {
                }
            }
            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    this.windowLayoutParams.flags = -NUM;
                    if (Build.VERSION.SDK_INT >= 28) {
                        this.windowLayoutParams.layoutInDisplayCutoutMode = 1;
                    }
                }
                this.windowView.setFocusable(false);
                this.containerView.setFocusable(false);
                wm.addView(this.windowView, this.windowLayoutParams);
            } catch (Exception e3) {
                FileLog.e((Throwable) e3);
                return false;
            }
        } else {
            this.windowLayoutParams.flags &= -17;
            ((WindowManager) this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
        }
        this.isVisible = true;
        this.animationInProgress = 1;
        this.windowView.setAlpha(0.0f);
        this.containerView.setAlpha(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.containerView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.windowView, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(56.0f), 0.0f})});
        this.animationEndRunnable = new ArticleViewer$$ExternalSyntheticLambda14(this);
        animatorSet.setDuration(150);
        animatorSet.setInterpolator(this.interpolator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                AndroidUtilities.runOnUIThread(new ArticleViewer$23$$ExternalSyntheticLambda0(this));
            }

            /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-ArticleViewer$23  reason: not valid java name */
            public /* synthetic */ void m2707lambda$onAnimationEnd$0$orgtelegramuiArticleViewer$23() {
                NotificationCenter.getInstance(ArticleViewer.this.currentAccount).onAnimationFinish(ArticleViewer.this.allowAnimationIndex);
                if (ArticleViewer.this.animationEndRunnable != null) {
                    ArticleViewer.this.animationEndRunnable.run();
                    Runnable unused = ArticleViewer.this.animationEndRunnable = null;
                }
            }
        });
        this.transitionAnimationStartTime = System.currentTimeMillis();
        AndroidUtilities.runOnUIThread(new ArticleViewer$$ExternalSyntheticLambda20(this, animatorSet));
        if (Build.VERSION.SDK_INT >= 18) {
            this.containerView.setLayerType(2, paint);
        }
        return true;
    }

    /* renamed from: lambda$open$31$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2674lambda$open$31$orgtelegramuiArticleViewer(TLRPC.WebPage webPageFinal, MessageObject messageObject, int currentAccount2, String anchorFinal, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ArticleViewer$$ExternalSyntheticLambda25(this, response, webPageFinal, messageObject, currentAccount2, anchorFinal));
    }

    /* renamed from: lambda$open$30$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2673lambda$open$30$orgtelegramuiArticleViewer(TLObject response, TLRPC.WebPage webPageFinal, MessageObject messageObject, int currentAccount2, String anchorFinal) {
        RecyclerView.ViewHolder holder;
        TLObject tLObject = response;
        TLRPC.WebPage webPage = webPageFinal;
        MessageObject messageObject2 = messageObject;
        String str = anchorFinal;
        if (tLObject instanceof TLRPC.TL_webPage) {
            TLRPC.TL_webPage webPage2 = (TLRPC.TL_webPage) tLObject;
            if (webPage2.cached_page != null) {
                if (!this.pagesStack.isEmpty() && this.pagesStack.get(0) == webPage) {
                    if (messageObject2 != null) {
                        messageObject2.messageOwner.media.webpage = webPage2;
                        TLRPC.TL_messages_messages messagesRes = new TLRPC.TL_messages_messages();
                        messagesRes.messages.add(messageObject2.messageOwner);
                        MessagesStorage.getInstance(currentAccount2).putMessages((TLRPC.messages_Messages) messagesRes, messageObject.getDialogId(), -2, 0, false, messageObject2.scheduled);
                    }
                    this.pagesStack.set(0, webPage2);
                    if (this.pagesStack.size() == 1) {
                        ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().remove("article" + webPage2.id).commit();
                        updateInterfaceForCurrentPage(webPage2, false, 0);
                        if (str != null) {
                            scrollToAnchor(str);
                        }
                    }
                }
                LongSparseArray longSparseArray = new LongSparseArray(1);
                longSparseArray.put(webPage2.id, webPage2);
                MessagesStorage.getInstance(currentAccount2).putWebPages(longSparseArray);
            }
        } else if (tLObject instanceof TLRPC.TL_webPageNotModified) {
            TLRPC.TL_webPageNotModified webPage3 = (TLRPC.TL_webPageNotModified) tLObject;
            if (webPage != null && webPage.cached_page != null && webPage.cached_page.views != webPage3.cached_page_views) {
                webPage.cached_page.views = webPage3.cached_page_views;
                webPage.cached_page.flags |= 8;
                int a = 0;
                while (true) {
                    WebpageAdapter[] webpageAdapterArr = this.adapter;
                    if (a >= webpageAdapterArr.length) {
                        break;
                    }
                    if (webpageAdapterArr[a].currentPage == webPage && (holder = this.listView[a].findViewHolderForAdapterPosition(this.adapter[a].getItemCount() - 1)) != null) {
                        this.adapter[a].onViewAttachedToWindow(holder);
                    }
                    a++;
                }
                if (messageObject2 != null) {
                    TLRPC.TL_messages_messages messagesRes2 = new TLRPC.TL_messages_messages();
                    messagesRes2.messages.add(messageObject2.messageOwner);
                    MessagesStorage.getInstance(currentAccount2).putMessages((TLRPC.messages_Messages) messagesRes2, messageObject.getDialogId(), -2, 0, false, messageObject2.scheduled);
                }
            }
        }
    }

    /* renamed from: lambda$open$32$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2675lambda$open$32$orgtelegramuiArticleViewer() {
        if (this.containerView != null && this.windowView != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, (Paint) null);
            }
            this.animationInProgress = 0;
            AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
        }
    }

    /* renamed from: lambda$open$33$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2676lambda$open$33$orgtelegramuiArticleViewer(AnimatorSet animatorSet) {
        this.allowAnimationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.allowAnimationIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats});
        animatorSet.start();
    }

    private void showProgressView(boolean useLine, boolean show) {
        final boolean z = show;
        if (useLine) {
            AndroidUtilities.cancelRunOnUIThread(this.lineProgressTickRunnable);
            if (z) {
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
        if (z) {
            this.progressView.setVisibility(0);
            this.menuContainer.setEnabled(false);
            this.progressViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.menuButton, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.menuButton, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.menuButton, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{1.0f})});
        } else {
            this.menuButton.setVisibility(0);
            this.menuContainer.setEnabled(true);
            this.progressViewAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.menuButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.menuButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.menuButton, View.ALPHA, new float[]{1.0f})});
        }
        this.progressViewAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animation)) {
                    if (!z) {
                        ArticleViewer.this.progressView.setVisibility(4);
                    } else {
                        ArticleViewer.this.menuButton.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (ArticleViewer.this.progressViewAnimation != null && ArticleViewer.this.progressViewAnimation.equals(animation)) {
                    AnimatorSet unused = ArticleViewer.this.progressViewAnimation = null;
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
            Animator[] animatorArr = new Animator[12];
            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, new float[]{(float) (this.containerView.getMeasuredWidth() - AndroidUtilities.dp(56.0f))});
            FrameLayout frameLayout = this.containerView;
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[1];
            fArr[0] = (float) (ActionBar.getCurrentActionBarHeight() + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0));
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
            this.animationEndRunnable = new ArticleViewer$$ExternalSyntheticLambda12(this);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(250);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
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
            this.backDrawable.setRotation(1.0f, true);
            animatorSet.start();
        }
    }

    /* renamed from: lambda$collapse$34$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2667lambda$collapse$34$orgtelegramuiArticleViewer() {
        if (this.containerView != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, (Paint) null);
            }
            this.animationInProgress = 0;
            ((WindowManager) this.parentActivity.getSystemService("window")).updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }

    public void uncollapse() {
        if (this.parentActivity != null && this.isVisible && !checkAnimation()) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.windowView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.listView[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.listView[0], View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.headerView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.backButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.backButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.backButton, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.menuContainer, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.menuContainer, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.menuContainer, View.SCALE_Y, new float[]{1.0f})});
            this.collapsed = false;
            this.animationInProgress = 2;
            this.animationEndRunnable = new ArticleViewer$$ExternalSyntheticLambda17(this);
            animatorSet.setDuration(250);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
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
            this.backDrawable.setRotation(0.0f, true);
            animatorSet.start();
        }
    }

    /* renamed from: lambda$uncollapse$35$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2705lambda$uncollapse$35$orgtelegramuiArticleViewer() {
        if (this.containerView != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, (Paint) null);
            }
            this.animationInProgress = 0;
        }
    }

    /* access modifiers changed from: private */
    public void saveCurrentPagePosition() {
        int position;
        int offset;
        boolean z = false;
        if (this.adapter[0].currentPage != null && (position = this.layoutManager[0].findFirstVisibleItemPosition()) != -1) {
            View view = this.layoutManager[0].findViewByPosition(position);
            if (view != null) {
                offset = view.getTop();
            } else {
                offset = 0;
            }
            String key = "article" + this.adapter[0].currentPage.id;
            SharedPreferences.Editor putInt = ApplicationLoader.applicationContext.getSharedPreferences("articles", 0).edit().putInt(key, position).putInt(key + "o", offset);
            String str = key + "r";
            if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                z = true;
            }
            putInt.putBoolean(str, z).commit();
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

    public void close(boolean byBackPress, boolean force) {
        if (this.parentActivity != null && !this.closeAnimationInProgress && this.isVisible && !checkAnimation()) {
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
                if (!force) {
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
                if (!byBackPress || force || !removeLastPageFromStack()) {
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
                    this.animationEndRunnable = new ArticleViewer$$ExternalSyntheticLambda10(this);
                    animatorSet.setDuration(150);
                    animatorSet.setInterpolator(this.interpolator);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
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

    /* renamed from: lambda$close$36$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2666lambda$close$36$orgtelegramuiArticleViewer() {
        if (this.containerView != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                this.containerView.setLayerType(0, (Paint) null);
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
        for (int a = 0; a < this.createdWebViews.size(); a++) {
            this.createdWebViews.get(a).destroyWebView(false);
        }
        this.containerView.post(new ArticleViewer$$ExternalSyntheticLambda13(this));
    }

    /* renamed from: lambda$onClosed$37$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2672lambda$onClosed$37$orgtelegramuiArticleViewer() {
        try {
            if (this.windowView.getParent() != null) {
                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void loadChannel(BlockChannelCell cell, WebpageAdapter adapter2, TLRPC.Chat channel) {
        if (!this.loadingChannel && !TextUtils.isEmpty(channel.username)) {
            this.loadingChannel = true;
            TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
            req.username = channel.username;
            int currentAccount2 = UserConfig.selectedAccount;
            ConnectionsManager.getInstance(currentAccount2).sendRequest(req, new ArticleViewer$$ExternalSyntheticLambda32(this, adapter2, currentAccount2, cell));
        }
    }

    /* renamed from: lambda$loadChannel$39$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2671lambda$loadChannel$39$orgtelegramuiArticleViewer(WebpageAdapter adapter2, int currentAccount2, BlockChannelCell cell, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ArticleViewer$$ExternalSyntheticLambda27(this, adapter2, error, response, currentAccount2, cell));
    }

    /* renamed from: lambda$loadChannel$38$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2670lambda$loadChannel$38$orgtelegramuiArticleViewer(WebpageAdapter adapter2, TLRPC.TL_error error, TLObject response, int currentAccount2, BlockChannelCell cell) {
        this.loadingChannel = false;
        if (this.parentFragment != null && !adapter2.blocks.isEmpty()) {
            if (error == null) {
                TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
                if (!res.chats.isEmpty()) {
                    MessagesController.getInstance(currentAccount2).putUsers(res.users, false);
                    MessagesController.getInstance(currentAccount2).putChats(res.chats, false);
                    MessagesStorage.getInstance(currentAccount2).putUsersAndChats(res.users, res.chats, false, true);
                    TLRPC.Chat chat = res.chats.get(0);
                    this.loadedChannel = chat;
                    if (!chat.left || this.loadedChannel.kicked) {
                        cell.setState(4, false);
                    } else {
                        cell.setState(0, false);
                    }
                } else {
                    cell.setState(4, false);
                }
            } else {
                cell.setState(4, false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void joinChannel(BlockChannelCell cell, TLRPC.Chat channel) {
        TLRPC.TL_channels_joinChannel req = new TLRPC.TL_channels_joinChannel();
        req.channel = MessagesController.getInputChannel(channel);
        int currentAccount2 = UserConfig.selectedAccount;
        ConnectionsManager.getInstance(currentAccount2).sendRequest(req, new ArticleViewer$$ExternalSyntheticLambda31(this, cell, currentAccount2, req, channel));
    }

    /* renamed from: lambda$joinChannel$43$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2669lambda$joinChannel$43$orgtelegramuiArticleViewer(BlockChannelCell cell, int currentAccount2, TLRPC.TL_channels_joinChannel req, TLRPC.Chat channel, TLObject response, TLRPC.TL_error error) {
        TLRPC.Chat chat = channel;
        if (error != null) {
            AndroidUtilities.runOnUIThread(new ArticleViewer$$ExternalSyntheticLambda26(this, cell, currentAccount2, error, req));
            return;
        }
        boolean hasJoinMessage = false;
        TLRPC.Updates updates = (TLRPC.Updates) response;
        int a = 0;
        while (true) {
            if (a >= updates.updates.size()) {
                break;
            }
            TLRPC.Update update = updates.updates.get(a);
            if ((update instanceof TLRPC.TL_updateNewChannelMessage) && (((TLRPC.TL_updateNewChannelMessage) update).message.action instanceof TLRPC.TL_messageActionChatAddUser)) {
                hasJoinMessage = true;
                break;
            }
            a++;
        }
        MessagesController.getInstance(currentAccount2).processUpdates(updates, false);
        if (!hasJoinMessage) {
            MessagesController.getInstance(currentAccount2).generateJoinMessage(chat.id, true);
        }
        AndroidUtilities.runOnUIThread(new ArticleViewer$$ExternalSyntheticLambda9(cell));
        AndroidUtilities.runOnUIThread(new ArticleViewer$$ExternalSyntheticLambda8(currentAccount2, chat), 1000);
        MessagesStorage.getInstance(currentAccount2).updateDialogsWithDeletedMessages(-chat.id, chat.id, new ArrayList(), (ArrayList<Long>) null, true);
    }

    /* renamed from: lambda$joinChannel$40$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2668lambda$joinChannel$40$orgtelegramuiArticleViewer(BlockChannelCell cell, int currentAccount2, TLRPC.TL_error error, TLRPC.TL_channels_joinChannel req) {
        cell.setState(0, false);
        AlertsCreator.processError(currentAccount2, error, this.parentFragment, req, true);
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
            for (int a = 0; a < this.createdWebViews.size(); a++) {
                this.createdWebViews.get(a).destroyWebView(true);
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
                this.visibleDialog.setOnDismissListener(new ArticleViewer$$ExternalSyntheticLambda33(this));
                dialog.show();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    /* renamed from: lambda$showDialog$44$org-telegram-ui-ArticleViewer  reason: not valid java name */
    public /* synthetic */ void m2699lambda$showDialog$44$orgtelegramuiArticleViewer(DialogInterface dialog1) {
        this.visibleDialog = null;
    }

    private static final class WebPageUtils {
        private WebPageUtils() {
        }

        public static TLRPC.Photo getPhotoWithId(TLRPC.WebPage page, long id) {
            if (page == null || page.cached_page == null) {
                return null;
            }
            if (page.photo != null && page.photo.id == id) {
                return page.photo;
            }
            for (int a = 0; a < page.cached_page.photos.size(); a++) {
                TLRPC.Photo photo = page.cached_page.photos.get(a);
                if (photo.id == id) {
                    return photo;
                }
            }
            return null;
        }

        public static TLRPC.Document getDocumentWithId(TLRPC.WebPage page, long id) {
            if (page == null || page.cached_page == null) {
                return null;
            }
            if (page.document != null && page.document.id == id) {
                return page.document;
            }
            for (int a = 0; a < page.cached_page.documents.size(); a++) {
                TLRPC.Document document = page.cached_page.documents.get(a);
                if (document.id == id) {
                    return document;
                }
            }
            return null;
        }

        public static boolean isVideo(TLRPC.WebPage page, TLRPC.PageBlock block) {
            TLRPC.Document document;
            if (!(block instanceof TLRPC.TL_pageBlockVideo) || (document = getDocumentWithId(page, ((TLRPC.TL_pageBlockVideo) block).video_id)) == null) {
                return false;
            }
            return MessageObject.isVideoDocument(document);
        }

        public static TLObject getMedia(TLRPC.WebPage page, TLRPC.PageBlock block) {
            if (block instanceof TLRPC.TL_pageBlockPhoto) {
                return getPhotoWithId(page, ((TLRPC.TL_pageBlockPhoto) block).photo_id);
            }
            if (block instanceof TLRPC.TL_pageBlockVideo) {
                return getDocumentWithId(page, ((TLRPC.TL_pageBlockVideo) block).video_id);
            }
            return null;
        }

        public static File getMediaFile(TLRPC.WebPage page, TLRPC.PageBlock block) {
            TLRPC.Document document;
            TLRPC.PhotoSize sizeFull;
            if (block instanceof TLRPC.TL_pageBlockPhoto) {
                TLRPC.Photo photo = getPhotoWithId(page, ((TLRPC.TL_pageBlockPhoto) block).photo_id);
                if (photo == null || (sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize())) == null) {
                    return null;
                }
                return FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(sizeFull, true);
            } else if (!(block instanceof TLRPC.TL_pageBlockVideo) || (document = getDocumentWithId(page, ((TLRPC.TL_pageBlockVideo) block).video_id)) == null) {
                return null;
            } else {
                return FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true);
            }
        }
    }

    private class WebpageAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public HashMap<String, Integer> anchors = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<String, Integer> anchorsOffset = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<String, TLRPC.TL_textAnchor> anchorsParent = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<TLRPC.TL_pageBlockAudio, MessageObject> audioBlocks = new HashMap<>();
        /* access modifiers changed from: private */
        public ArrayList<MessageObject> audioMessages = new ArrayList<>();
        /* access modifiers changed from: private */
        public ArrayList<TLRPC.PageBlock> blocks = new ArrayList<>();
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockChannel channelBlock;
        private Context context;
        /* access modifiers changed from: private */
        public TLRPC.WebPage currentPage;
        /* access modifiers changed from: private */
        public boolean isRtl;
        /* access modifiers changed from: private */
        public ArrayList<TLRPC.PageBlock> localBlocks = new ArrayList<>();
        /* access modifiers changed from: private */
        public ArrayList<TLRPC.PageBlock> photoBlocks = new ArrayList<>();
        /* access modifiers changed from: private */
        public HashMap<String, Integer> searchTextOffset = new HashMap<>();
        /* access modifiers changed from: private */
        public ArrayList<Object> textBlocks = new ArrayList<>();
        /* access modifiers changed from: private */
        public HashMap<Object, TLRPC.PageBlock> textToBlocks = new HashMap<>();

        public WebpageAdapter(Context ctx) {
            this.context = ctx;
        }

        /* access modifiers changed from: private */
        public TLRPC.Photo getPhotoWithId(long id) {
            return WebPageUtils.getPhotoWithId(this.currentPage, id);
        }

        /* access modifiers changed from: private */
        public TLRPC.Document getDocumentWithId(long id) {
            return WebPageUtils.getDocumentWithId(this.currentPage, id);
        }

        private void setRichTextParents(TLRPC.RichText parentRichText, TLRPC.RichText richText) {
            if (richText != null) {
                richText.parentRichText = parentRichText;
                if (richText instanceof TLRPC.TL_textFixed) {
                    setRichTextParents(richText, ((TLRPC.TL_textFixed) richText).text);
                } else if (richText instanceof TLRPC.TL_textItalic) {
                    setRichTextParents(richText, ((TLRPC.TL_textItalic) richText).text);
                } else if (richText instanceof TLRPC.TL_textBold) {
                    setRichTextParents(richText, ((TLRPC.TL_textBold) richText).text);
                } else if (richText instanceof TLRPC.TL_textUnderline) {
                    setRichTextParents(richText, ((TLRPC.TL_textUnderline) richText).text);
                } else if (richText instanceof TLRPC.TL_textStrike) {
                    setRichTextParents(richText, ((TLRPC.TL_textStrike) richText).text);
                } else if (richText instanceof TLRPC.TL_textEmail) {
                    setRichTextParents(richText, ((TLRPC.TL_textEmail) richText).text);
                } else if (richText instanceof TLRPC.TL_textPhone) {
                    setRichTextParents(richText, ((TLRPC.TL_textPhone) richText).text);
                } else if (richText instanceof TLRPC.TL_textUrl) {
                    setRichTextParents(richText, ((TLRPC.TL_textUrl) richText).text);
                } else if (richText instanceof TLRPC.TL_textConcat) {
                    int count = richText.texts.size();
                    for (int a = 0; a < count; a++) {
                        setRichTextParents(richText, richText.texts.get(a));
                    }
                } else if (richText instanceof TLRPC.TL_textSubscript) {
                    setRichTextParents(richText, ((TLRPC.TL_textSubscript) richText).text);
                } else if (richText instanceof TLRPC.TL_textSuperscript) {
                    setRichTextParents(richText, ((TLRPC.TL_textSuperscript) richText).text);
                } else if (richText instanceof TLRPC.TL_textMarked) {
                    setRichTextParents(richText, ((TLRPC.TL_textMarked) richText).text);
                } else if (richText instanceof TLRPC.TL_textAnchor) {
                    TLRPC.TL_textAnchor textAnchor = (TLRPC.TL_textAnchor) richText;
                    setRichTextParents(richText, textAnchor.text);
                    String name = textAnchor.name.toLowerCase();
                    this.anchors.put(name, Integer.valueOf(this.blocks.size()));
                    if (textAnchor.text instanceof TLRPC.TL_textPlain) {
                        if (!TextUtils.isEmpty(((TLRPC.TL_textPlain) textAnchor.text).text)) {
                            this.anchorsParent.put(name, textAnchor);
                        }
                    } else if (!(textAnchor.text instanceof TLRPC.TL_textEmpty)) {
                        this.anchorsParent.put(name, textAnchor);
                    }
                    this.anchorsOffset.put(name, -1);
                }
            }
        }

        private void addTextBlock(Object text, TLRPC.PageBlock block) {
            if (!(text instanceof TLRPC.TL_textEmpty) && !this.textToBlocks.containsKey(text)) {
                this.textToBlocks.put(text, block);
                this.textBlocks.add(text);
            }
        }

        private void setRichTextParents(TLRPC.PageBlock block) {
            if (block instanceof TLRPC.TL_pageBlockEmbedPost) {
                TLRPC.TL_pageBlockEmbedPost blockEmbedPost = (TLRPC.TL_pageBlockEmbedPost) block;
                setRichTextParents((TLRPC.RichText) null, blockEmbedPost.caption.text);
                setRichTextParents((TLRPC.RichText) null, blockEmbedPost.caption.credit);
                addTextBlock(blockEmbedPost.caption.text, blockEmbedPost);
                addTextBlock(blockEmbedPost.caption.credit, blockEmbedPost);
            } else if (block instanceof TLRPC.TL_pageBlockParagraph) {
                TLRPC.TL_pageBlockParagraph pageBlockParagraph = (TLRPC.TL_pageBlockParagraph) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockParagraph.text);
                addTextBlock(pageBlockParagraph.text, pageBlockParagraph);
            } else if (block instanceof TLRPC.TL_pageBlockKicker) {
                TLRPC.TL_pageBlockKicker pageBlockKicker = (TLRPC.TL_pageBlockKicker) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockKicker.text);
                addTextBlock(pageBlockKicker.text, pageBlockKicker);
            } else if (block instanceof TLRPC.TL_pageBlockFooter) {
                TLRPC.TL_pageBlockFooter pageBlockFooter = (TLRPC.TL_pageBlockFooter) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockFooter.text);
                addTextBlock(pageBlockFooter.text, pageBlockFooter);
            } else if (block instanceof TLRPC.TL_pageBlockHeader) {
                TLRPC.TL_pageBlockHeader pageBlockHeader = (TLRPC.TL_pageBlockHeader) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockHeader.text);
                addTextBlock(pageBlockHeader.text, pageBlockHeader);
            } else if (block instanceof TLRPC.TL_pageBlockPreformatted) {
                TLRPC.TL_pageBlockPreformatted pageBlockPreformatted = (TLRPC.TL_pageBlockPreformatted) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockPreformatted.text);
                addTextBlock(pageBlockPreformatted.text, pageBlockPreformatted);
            } else if (block instanceof TLRPC.TL_pageBlockSubheader) {
                TLRPC.TL_pageBlockSubheader pageBlockTitle = (TLRPC.TL_pageBlockSubheader) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockTitle.text);
                addTextBlock(pageBlockTitle.text, pageBlockTitle);
            } else if (block instanceof TLRPC.TL_pageBlockSlideshow) {
                TLRPC.TL_pageBlockSlideshow pageBlockSlideshow = (TLRPC.TL_pageBlockSlideshow) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockSlideshow.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockSlideshow.caption.credit);
                addTextBlock(pageBlockSlideshow.caption.text, pageBlockSlideshow);
                addTextBlock(pageBlockSlideshow.caption.credit, pageBlockSlideshow);
                int size = pageBlockSlideshow.items.size();
                for (int a = 0; a < size; a++) {
                    setRichTextParents(pageBlockSlideshow.items.get(a));
                }
            } else if (block instanceof TLRPC.TL_pageBlockPhoto) {
                TLRPC.TL_pageBlockPhoto pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockPhoto.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockPhoto.caption.credit);
                addTextBlock(pageBlockPhoto.caption.text, pageBlockPhoto);
                addTextBlock(pageBlockPhoto.caption.credit, pageBlockPhoto);
            } else if (block instanceof TL_pageBlockListItem) {
                TL_pageBlockListItem pageBlockListItem = (TL_pageBlockListItem) block;
                if (pageBlockListItem.textItem != null) {
                    setRichTextParents((TLRPC.RichText) null, pageBlockListItem.textItem);
                    addTextBlock(pageBlockListItem.textItem, pageBlockListItem);
                } else if (pageBlockListItem.blockItem != null) {
                    setRichTextParents(pageBlockListItem.blockItem);
                }
            } else if (block instanceof TL_pageBlockOrderedListItem) {
                TL_pageBlockOrderedListItem pageBlockOrderedListItem = (TL_pageBlockOrderedListItem) block;
                if (pageBlockOrderedListItem.textItem != null) {
                    setRichTextParents((TLRPC.RichText) null, pageBlockOrderedListItem.textItem);
                    addTextBlock(pageBlockOrderedListItem.textItem, pageBlockOrderedListItem);
                } else if (pageBlockOrderedListItem.blockItem != null) {
                    setRichTextParents(pageBlockOrderedListItem.blockItem);
                }
            } else if (block instanceof TLRPC.TL_pageBlockCollage) {
                TLRPC.TL_pageBlockCollage pageBlockCollage = (TLRPC.TL_pageBlockCollage) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockCollage.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockCollage.caption.credit);
                addTextBlock(pageBlockCollage.caption.text, pageBlockCollage);
                addTextBlock(pageBlockCollage.caption.credit, pageBlockCollage);
                int size2 = pageBlockCollage.items.size();
                for (int a2 = 0; a2 < size2; a2++) {
                    setRichTextParents(pageBlockCollage.items.get(a2));
                }
            } else if (block instanceof TLRPC.TL_pageBlockEmbed) {
                TLRPC.TL_pageBlockEmbed pageBlockEmbed = (TLRPC.TL_pageBlockEmbed) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockEmbed.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockEmbed.caption.credit);
                addTextBlock(pageBlockEmbed.caption.text, pageBlockEmbed);
                addTextBlock(pageBlockEmbed.caption.credit, pageBlockEmbed);
            } else if (block instanceof TLRPC.TL_pageBlockSubtitle) {
                TLRPC.TL_pageBlockSubtitle pageBlockSubtitle = (TLRPC.TL_pageBlockSubtitle) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockSubtitle.text);
                addTextBlock(pageBlockSubtitle.text, pageBlockSubtitle);
            } else if (block instanceof TLRPC.TL_pageBlockBlockquote) {
                TLRPC.TL_pageBlockBlockquote pageBlockBlockquote = (TLRPC.TL_pageBlockBlockquote) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockBlockquote.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockBlockquote.caption);
                addTextBlock(pageBlockBlockquote.text, pageBlockBlockquote);
                addTextBlock(pageBlockBlockquote.caption, pageBlockBlockquote);
            } else if (block instanceof TLRPC.TL_pageBlockDetails) {
                TLRPC.TL_pageBlockDetails pageBlockDetails = (TLRPC.TL_pageBlockDetails) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockDetails.title);
                addTextBlock(pageBlockDetails.title, pageBlockDetails);
                int size3 = pageBlockDetails.blocks.size();
                for (int a3 = 0; a3 < size3; a3++) {
                    setRichTextParents(pageBlockDetails.blocks.get(a3));
                }
            } else if (block instanceof TLRPC.TL_pageBlockVideo) {
                TLRPC.TL_pageBlockVideo pageBlockVideo = (TLRPC.TL_pageBlockVideo) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockVideo.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockVideo.caption.credit);
                addTextBlock(pageBlockVideo.caption.text, pageBlockVideo);
                addTextBlock(pageBlockVideo.caption.credit, pageBlockVideo);
            } else if (block instanceof TLRPC.TL_pageBlockPullquote) {
                TLRPC.TL_pageBlockPullquote pageBlockPullquote = (TLRPC.TL_pageBlockPullquote) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockPullquote.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockPullquote.caption);
                addTextBlock(pageBlockPullquote.text, pageBlockPullquote);
                addTextBlock(pageBlockPullquote.caption, pageBlockPullquote);
            } else if (block instanceof TLRPC.TL_pageBlockAudio) {
                TLRPC.TL_pageBlockAudio pageBlockAudio = (TLRPC.TL_pageBlockAudio) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockAudio.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockAudio.caption.credit);
                addTextBlock(pageBlockAudio.caption.text, pageBlockAudio);
                addTextBlock(pageBlockAudio.caption.credit, pageBlockAudio);
            } else if (block instanceof TLRPC.TL_pageBlockTable) {
                TLRPC.TL_pageBlockTable pageBlockTable = (TLRPC.TL_pageBlockTable) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockTable.title);
                addTextBlock(pageBlockTable.title, pageBlockTable);
                int size4 = pageBlockTable.rows.size();
                for (int a4 = 0; a4 < size4; a4++) {
                    TLRPC.TL_pageTableRow row = pageBlockTable.rows.get(a4);
                    int size22 = row.cells.size();
                    for (int b = 0; b < size22; b++) {
                        TLRPC.TL_pageTableCell cell = row.cells.get(b);
                        setRichTextParents((TLRPC.RichText) null, cell.text);
                        addTextBlock(cell.text, pageBlockTable);
                    }
                }
            } else if (block instanceof TLRPC.TL_pageBlockTitle) {
                TLRPC.TL_pageBlockTitle pageBlockTitle2 = (TLRPC.TL_pageBlockTitle) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockTitle2.text);
                addTextBlock(pageBlockTitle2.text, pageBlockTitle2);
            } else if (block instanceof TLRPC.TL_pageBlockCover) {
                setRichTextParents(((TLRPC.TL_pageBlockCover) block).cover);
            } else if (block instanceof TLRPC.TL_pageBlockAuthorDate) {
                TLRPC.TL_pageBlockAuthorDate pageBlockAuthorDate = (TLRPC.TL_pageBlockAuthorDate) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockAuthorDate.author);
                addTextBlock(pageBlockAuthorDate.author, pageBlockAuthorDate);
            } else if (block instanceof TLRPC.TL_pageBlockMap) {
                TLRPC.TL_pageBlockMap pageBlockMap = (TLRPC.TL_pageBlockMap) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockMap.caption.text);
                setRichTextParents((TLRPC.RichText) null, pageBlockMap.caption.credit);
                addTextBlock(pageBlockMap.caption.text, pageBlockMap);
                addTextBlock(pageBlockMap.caption.credit, pageBlockMap);
            } else if (block instanceof TLRPC.TL_pageBlockRelatedArticles) {
                TLRPC.TL_pageBlockRelatedArticles pageBlockRelatedArticles = (TLRPC.TL_pageBlockRelatedArticles) block;
                setRichTextParents((TLRPC.RichText) null, pageBlockRelatedArticles.title);
                addTextBlock(pageBlockRelatedArticles.title, pageBlockRelatedArticles);
            }
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:104:0x0348  */
        /* JADX WARNING: Removed duplicated region for block: B:182:0x03cb A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:96:0x02ee  */
        /* JADX WARNING: Removed duplicated region for block: B:97:0x031f  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void addBlock(org.telegram.ui.ArticleViewer.WebpageAdapter r28, org.telegram.tgnet.TLRPC.PageBlock r29, int r30, int r31, int r32) {
            /*
                r27 = this;
                r7 = r27
                r0 = r28
                r1 = r29
                r8 = r31
                r9 = r29
                boolean r2 = r1 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild
                if (r2 == 0) goto L_0x0015
                r2 = r1
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r2 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r2
                org.telegram.tgnet.TLRPC$PageBlock r1 = r2.block
            L_0x0015:
                boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockList
                if (r2 != 0) goto L_0x0023
                boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockOrderedList
                if (r2 != 0) goto L_0x0023
                r7.setRichTextParents(r1)
                r7.addAllMediaFromBlock(r0, r1)
            L_0x0023:
                org.telegram.ui.ArticleViewer r2 = org.telegram.ui.ArticleViewer.this
                org.telegram.tgnet.TLRPC$PageBlock r10 = r2.getLastNonListPageBlock(r1)
                boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockUnsupported
                if (r1 == 0) goto L_0x002e
                return
            L_0x002e:
                boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockAnchor
                if (r1 == 0) goto L_0x004b
                java.util.HashMap<java.lang.String, java.lang.Integer> r1 = r7.anchors
                r2 = r10
                org.telegram.tgnet.TLRPC$TL_pageBlockAnchor r2 = (org.telegram.tgnet.TLRPC.TL_pageBlockAnchor) r2
                java.lang.String r2 = r2.name
                java.lang.String r2 = r2.toLowerCase()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r3 = r7.blocks
                int r3 = r3.size()
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                r1.put(r2, r3)
                return
            L_0x004b:
                boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockList
                if (r1 != 0) goto L_0x0058
                boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockOrderedList
                if (r1 != 0) goto L_0x0058
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r1 = r7.blocks
                r1.add(r9)
            L_0x0058:
                boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockAudio
                r11 = 0
                r12 = 1
                if (r1 == 0) goto L_0x0121
                r1 = r10
                org.telegram.tgnet.TLRPC$TL_pageBlockAudio r1 = (org.telegram.tgnet.TLRPC.TL_pageBlockAudio) r1
                org.telegram.tgnet.TLRPC$TL_message r2 = new org.telegram.tgnet.TLRPC$TL_message
                r2.<init>()
                r2.out = r12
                long r3 = r1.audio_id
                java.lang.Long r3 = java.lang.Long.valueOf(r3)
                int r3 = r3.hashCode()
                int r3 = -r3
                r10.mid = r3
                r2.id = r3
                org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser
                r3.<init>()
                r2.peer_id = r3
                org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser
                r3.<init>()
                r2.from_id = r3
                org.telegram.tgnet.TLRPC$Peer r3 = r2.from_id
                org.telegram.tgnet.TLRPC$Peer r4 = r2.peer_id
                org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                int r5 = r5.currentAccount
                org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
                long r5 = r5.getClientUserId()
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
                org.telegram.tgnet.TLRPC$MessageMedia r3 = r2.media
                org.telegram.tgnet.TLRPC$WebPage r4 = r7.currentPage
                r3.webpage = r4
                org.telegram.tgnet.TLRPC$MessageMedia r3 = r2.media
                int r4 = r3.flags
                r4 = r4 | 3
                r3.flags = r4
                org.telegram.tgnet.TLRPC$MessageMedia r3 = r2.media
                long r4 = r1.audio_id
                org.telegram.tgnet.TLRPC$Document r4 = r7.getDocumentWithId(r4)
                r3.document = r4
                int r3 = r2.flags
                r3 = r3 | 768(0x300, float:1.076E-42)
                r2.flags = r3
                org.telegram.messenger.MessageObject r3 = new org.telegram.messenger.MessageObject
                int r4 = org.telegram.messenger.UserConfig.selectedAccount
                r3.<init>(r4, r2, r11, r12)
                java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r7.audioMessages
                r4.add(r3)
                java.util.HashMap<org.telegram.tgnet.TLRPC$TL_pageBlockAudio, org.telegram.messenger.MessageObject> r4 = r7.audioBlocks
                r4.put(r1, r3)
                java.lang.String r4 = r3.getMusicAuthor(r11)
                java.lang.String r5 = r3.getMusicTitle(r11)
                boolean r6 = android.text.TextUtils.isEmpty(r5)
                if (r6 == 0) goto L_0x00f3
                boolean r6 = android.text.TextUtils.isEmpty(r4)
                if (r6 != 0) goto L_0x011d
            L_0x00f3:
                boolean r6 = android.text.TextUtils.isEmpty(r5)
                if (r6 != 0) goto L_0x0110
                boolean r6 = android.text.TextUtils.isEmpty(r4)
                if (r6 != 0) goto L_0x0110
                r6 = 2
                java.lang.Object[] r6 = new java.lang.Object[r6]
                r6[r11] = r4
                r6[r12] = r5
                java.lang.String r11 = "%s - %s"
                java.lang.String r6 = java.lang.String.format(r11, r6)
                r7.addTextBlock(r6, r10)
                goto L_0x011d
            L_0x0110:
                boolean r6 = android.text.TextUtils.isEmpty(r5)
                if (r6 != 0) goto L_0x011a
                r7.addTextBlock(r5, r10)
                goto L_0x011d
            L_0x011a:
                r7.addTextBlock(r4, r10)
            L_0x011d:
                r29 = r10
                goto L_0x0632
            L_0x0121:
                boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost
                r13 = 0
                if (r1 == 0) goto L_0x01b0
                r1 = r10
                org.telegram.tgnet.TLRPC$TL_pageBlockEmbedPost r1 = (org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost) r1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r2 = r1.blocks
                boolean r2 = r2.isEmpty()
                if (r2 != 0) goto L_0x01ac
                r2 = -1
                r10.level = r2
                r2 = 0
            L_0x0135:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r3 = r1.blocks
                int r3 = r3.size()
                if (r2 >= r3) goto L_0x017f
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r3 = r1.blocks
                java.lang.Object r3 = r3.get(r2)
                org.telegram.tgnet.TLRPC$PageBlock r3 = (org.telegram.tgnet.TLRPC.PageBlock) r3
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockUnsupported
                if (r4 == 0) goto L_0x014a
                goto L_0x017c
            L_0x014a:
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockAnchor
                if (r4 == 0) goto L_0x0167
                r4 = r3
                org.telegram.tgnet.TLRPC$TL_pageBlockAnchor r4 = (org.telegram.tgnet.TLRPC.TL_pageBlockAnchor) r4
                java.util.HashMap<java.lang.String, java.lang.Integer> r5 = r7.anchors
                java.lang.String r6 = r4.name
                java.lang.String r6 = r6.toLowerCase()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r11 = r7.blocks
                int r11 = r11.size()
                java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
                r5.put(r6, r11)
                goto L_0x017c
            L_0x0167:
                r3.level = r12
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r4 = r1.blocks
                int r4 = r4.size()
                int r4 = r4 - r12
                if (r2 != r4) goto L_0x0174
                r3.bottom = r12
            L_0x0174:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r4 = r7.blocks
                r4.add(r3)
                r7.addAllMediaFromBlock(r0, r3)
            L_0x017c:
                int r2 = r2 + 1
                goto L_0x0135
            L_0x017f:
                org.telegram.tgnet.TLRPC$TL_pageCaption r2 = r1.caption
                org.telegram.tgnet.TLRPC$RichText r2 = r2.text
                java.lang.CharSequence r2 = org.telegram.ui.ArticleViewer.getPlainText(r2)
                boolean r2 = android.text.TextUtils.isEmpty(r2)
                if (r2 == 0) goto L_0x019b
                org.telegram.tgnet.TLRPC$TL_pageCaption r2 = r1.caption
                org.telegram.tgnet.TLRPC$RichText r2 = r2.credit
                java.lang.CharSequence r2 = org.telegram.ui.ArticleViewer.getPlainText(r2)
                boolean r2 = android.text.TextUtils.isEmpty(r2)
                if (r2 != 0) goto L_0x01ac
            L_0x019b:
                org.telegram.ui.ArticleViewer$TL_pageBlockEmbedPostCaption r2 = new org.telegram.ui.ArticleViewer$TL_pageBlockEmbedPostCaption
                r2.<init>()
                org.telegram.tgnet.TLRPC.TL_pageBlockEmbedPost unused = r2.parent = r1
                org.telegram.tgnet.TLRPC$TL_pageCaption r3 = r1.caption
                r2.caption = r3
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r3 = r7.blocks
                r3.add(r2)
            L_0x01ac:
                r29 = r10
                goto L_0x0632
            L_0x01b0:
                boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockRelatedArticles
                if (r1 == 0) goto L_0x01f9
                r1 = r10
                org.telegram.tgnet.TLRPC$TL_pageBlockRelatedArticles r1 = (org.telegram.tgnet.TLRPC.TL_pageBlockRelatedArticles) r1
                org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesShadow r2 = new org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesShadow
                r2.<init>()
                org.telegram.tgnet.TLRPC.TL_pageBlockRelatedArticles unused = r2.parent = r1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r3 = r7.blocks
                int r4 = r3.size()
                int r4 = r4 - r12
                r3.add(r4, r2)
                r3 = 0
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pageRelatedArticle> r4 = r1.articles
                int r4 = r4.size()
            L_0x01d0:
                if (r3 >= r4) goto L_0x01e5
                org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesChild r5 = new org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesChild
                r5.<init>()
                org.telegram.tgnet.TLRPC.TL_pageBlockRelatedArticles unused = r5.parent = r1
                int unused = r5.num = r3
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r6 = r7.blocks
                r6.add(r5)
                int r3 = r3 + 1
                goto L_0x01d0
            L_0x01e5:
                if (r32 != 0) goto L_0x01f5
                org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesShadow r3 = new org.telegram.ui.ArticleViewer$TL_pageBlockRelatedArticlesShadow
                r3.<init>()
                r2 = r3
                org.telegram.tgnet.TLRPC.TL_pageBlockRelatedArticles unused = r2.parent = r1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r3 = r7.blocks
                r3.add(r2)
            L_0x01f5:
                r29 = r10
                goto L_0x0632
            L_0x01f9:
                boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockDetails
                if (r1 == 0) goto L_0x0238
                r11 = r10
                org.telegram.tgnet.TLRPC$TL_pageBlockDetails r11 = (org.telegram.tgnet.TLRPC.TL_pageBlockDetails) r11
                r1 = 0
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r2 = r11.blocks
                int r12 = r2.size()
                r14 = r1
            L_0x0208:
                if (r14 >= r12) goto L_0x0234
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r1 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild
                r1.<init>()
                r15 = r1
                org.telegram.tgnet.TLRPC.PageBlock unused = r15.parent = r9
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r1 = r11.blocks
                java.lang.Object r1 = r1.get(r14)
                org.telegram.tgnet.TLRPC$PageBlock r1 = (org.telegram.tgnet.TLRPC.PageBlock) r1
                org.telegram.tgnet.TLRPC.PageBlock unused = r15.block = r1
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                org.telegram.tgnet.TLRPC$PageBlock r3 = r1.wrapInTableBlock(r9, r15)
                int r4 = r30 + 1
                r1 = r27
                r2 = r28
                r5 = r31
                r6 = r32
                r1.addBlock(r2, r3, r4, r5, r6)
                int r14 = r14 + 1
                goto L_0x0208
            L_0x0234:
                r29 = r10
                goto L_0x0632
            L_0x0238:
                boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockList
                java.lang.String r14 = " "
                java.lang.String r15 = ".%d"
                java.lang.String r6 = "%d."
                if (r1 == 0) goto L_0x03e5
                r5 = r10
                org.telegram.tgnet.TLRPC$TL_pageBlockList r5 = (org.telegram.tgnet.TLRPC.TL_pageBlockList) r5
                org.telegram.ui.ArticleViewer$TL_pageBlockListParent r1 = new org.telegram.ui.ArticleViewer$TL_pageBlockListParent
                org.telegram.ui.ArticleViewer r2 = org.telegram.ui.ArticleViewer.this
                r1.<init>()
                r4 = r1
                org.telegram.tgnet.TLRPC.TL_pageBlockList unused = r4.pageBlockList = r5
                int unused = r4.level = r8
                r1 = 0
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageListItem> r2 = r5.items
                int r3 = r2.size()
                r2 = r1
            L_0x025b:
                if (r2 >= r3) goto L_0x03da
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageListItem> r1 = r5.items
                java.lang.Object r1 = r1.get(r2)
                org.telegram.tgnet.TLRPC$PageListItem r1 = (org.telegram.tgnet.TLRPC.PageListItem) r1
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r11 = new org.telegram.ui.ArticleViewer$TL_pageBlockListItem
                org.telegram.ui.ArticleViewer r12 = org.telegram.ui.ArticleViewer.this
                r11.<init>()
                int unused = r11.index = r2
                org.telegram.ui.ArticleViewer.TL_pageBlockListParent unused = r11.parent = r4
                boolean r12 = r5.ordered
                if (r12 == 0) goto L_0x02a4
                boolean r12 = r7.isRtl
                if (r12 == 0) goto L_0x028f
                r12 = 1
                java.lang.Object[] r13 = new java.lang.Object[r12]
                int r16 = r2 + 1
                java.lang.Integer r16 = java.lang.Integer.valueOf(r16)
                r18 = 0
                r13[r18] = r16
                java.lang.String r13 = java.lang.String.format(r15, r13)
                java.lang.String unused = r11.num = r13
                goto L_0x02a9
            L_0x028f:
                r12 = 1
                r18 = 0
                java.lang.Object[] r13 = new java.lang.Object[r12]
                int r12 = r2 + 1
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                r13[r18] = r12
                java.lang.String r12 = java.lang.String.format(r6, r13)
                java.lang.String unused = r11.num = r12
                goto L_0x02a9
            L_0x02a4:
                java.lang.String r12 = "•"
                java.lang.String unused = r11.num = r12
            L_0x02a9:
                java.util.ArrayList r12 = r4.items
                r12.add(r11)
                boolean r12 = r1 instanceof org.telegram.tgnet.TLRPC.TL_pageListItemText
                if (r12 == 0) goto L_0x02bd
                r12 = r1
                org.telegram.tgnet.TLRPC$TL_pageListItemText r12 = (org.telegram.tgnet.TLRPC.TL_pageListItemText) r12
                org.telegram.tgnet.TLRPC$RichText r12 = r12.text
                org.telegram.tgnet.TLRPC.RichText unused = r11.textItem = r12
                goto L_0x02e9
            L_0x02bd:
                boolean r12 = r1 instanceof org.telegram.tgnet.TLRPC.TL_pageListItemBlocks
                if (r12 == 0) goto L_0x02e9
                r12 = r1
                org.telegram.tgnet.TLRPC$TL_pageListItemBlocks r12 = (org.telegram.tgnet.TLRPC.TL_pageListItemBlocks) r12
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r13 = r12.blocks
                boolean r13 = r13.isEmpty()
                if (r13 != 0) goto L_0x02d9
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r13 = r12.blocks
                r0 = 0
                java.lang.Object r13 = r13.get(r0)
                org.telegram.tgnet.TLRPC$PageBlock r13 = (org.telegram.tgnet.TLRPC.PageBlock) r13
                org.telegram.tgnet.TLRPC.PageBlock unused = r11.blockItem = r13
                goto L_0x02e9
            L_0x02d9:
                org.telegram.tgnet.TLRPC$TL_pageListItemText r0 = new org.telegram.tgnet.TLRPC$TL_pageListItemText
                r0.<init>()
                org.telegram.tgnet.TLRPC$TL_textPlain r13 = new org.telegram.tgnet.TLRPC$TL_textPlain
                r13.<init>()
                r13.text = r14
                r0.text = r13
                r1 = r0
                goto L_0x02ea
            L_0x02e9:
                r0 = r1
            L_0x02ea:
                boolean r1 = r9 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild
                if (r1 == 0) goto L_0x031f
                r12 = r9
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r12 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r12
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r1 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild
                r13 = 0
                r1.<init>()
                r13 = r1
                org.telegram.tgnet.TLRPC$PageBlock r1 = r12.parent
                org.telegram.tgnet.TLRPC.PageBlock unused = r13.parent = r1
                org.telegram.tgnet.TLRPC.PageBlock unused = r13.block = r11
                int r18 = r8 + 1
                r1 = r27
                r19 = r2
                r2 = r28
                r20 = r3
                r3 = r13
                r21 = r12
                r12 = r4
                r4 = r30
                r22 = r5
                r5 = r18
                r18 = r13
                r13 = r6
                r6 = r32
                r1.addBlock(r2, r3, r4, r5, r6)
                goto L_0x0344
            L_0x031f:
                r19 = r2
                r20 = r3
                r12 = r4
                r22 = r5
                r13 = r6
                if (r19 != 0) goto L_0x0332
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                org.telegram.tgnet.TLRPC$PageBlock r1 = r1.fixListBlock(r9, r11)
                r18 = r1
                goto L_0x0335
            L_0x0332:
                r1 = r11
                r18 = r1
            L_0x0335:
                int r5 = r8 + 1
                r1 = r27
                r2 = r28
                r3 = r18
                r4 = r30
                r6 = r32
                r1.addBlock(r2, r3, r4, r5, r6)
            L_0x0344:
                boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageListItemBlocks
                if (r1 == 0) goto L_0x03cb
                r6 = r0
                org.telegram.tgnet.TLRPC$TL_pageListItemBlocks r6 = (org.telegram.tgnet.TLRPC.TL_pageListItemBlocks) r6
                r1 = 1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r2 = r6.blocks
                int r5 = r2.size()
                r26 = r11
                r11 = r1
                r1 = r26
            L_0x0357:
                if (r11 >= r5) goto L_0x03c7
                org.telegram.ui.ArticleViewer$TL_pageBlockListItem r2 = new org.telegram.ui.ArticleViewer$TL_pageBlockListItem
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                r4 = 0
                r2.<init>()
                r4 = r2
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r1 = r6.blocks
                java.lang.Object r1 = r1.get(r11)
                org.telegram.tgnet.TLRPC$PageBlock r1 = (org.telegram.tgnet.TLRPC.PageBlock) r1
                org.telegram.tgnet.TLRPC.PageBlock unused = r4.blockItem = r1
                org.telegram.ui.ArticleViewer.TL_pageBlockListParent unused = r4.parent = r12
                boolean r1 = r9 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild
                if (r1 == 0) goto L_0x03a1
                r18 = r9
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r18 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r18
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r1 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild
                r2 = 0
                r1.<init>()
                r3 = r1
                org.telegram.tgnet.TLRPC$PageBlock r1 = r18.parent
                org.telegram.tgnet.TLRPC.PageBlock unused = r3.parent = r1
                org.telegram.tgnet.TLRPC.PageBlock unused = r3.block = r4
                int r21 = r8 + 1
                r1 = r27
                r2 = r28
                r23 = r3
                r24 = r4
                r4 = r30
                r25 = r5
                r5 = r21
                r21 = r6
                r6 = r32
                r1.addBlock(r2, r3, r4, r5, r6)
                goto L_0x03b6
            L_0x03a1:
                r24 = r4
                r25 = r5
                r21 = r6
                int r5 = r8 + 1
                r1 = r27
                r2 = r28
                r3 = r24
                r4 = r30
                r6 = r32
                r1.addBlock(r2, r3, r4, r5, r6)
            L_0x03b6:
                java.util.ArrayList r1 = r12.items
                r2 = r24
                r1.add(r2)
                int r11 = r11 + 1
                r1 = r2
                r6 = r21
                r5 = r25
                goto L_0x0357
            L_0x03c7:
                r25 = r5
                r21 = r6
            L_0x03cb:
                int r2 = r19 + 1
                r0 = r28
                r4 = r12
                r6 = r13
                r3 = r20
                r5 = r22
                r11 = 0
                r12 = 1
                r13 = 0
                goto L_0x025b
            L_0x03da:
                r19 = r2
                r20 = r3
                r12 = r4
                r22 = r5
                r29 = r10
                goto L_0x0632
            L_0x03e5:
                r13 = r6
                boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockOrderedList
                if (r0 == 0) goto L_0x0630
                r0 = r10
                org.telegram.tgnet.TLRPC$TL_pageBlockOrderedList r0 = (org.telegram.tgnet.TLRPC.TL_pageBlockOrderedList) r0
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent r1 = new org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListParent
                org.telegram.ui.ArticleViewer r2 = org.telegram.ui.ArticleViewer.this
                r3 = 0
                r1.<init>()
                r11 = r1
                org.telegram.tgnet.TLRPC.TL_pageBlockOrderedList unused = r11.pageBlockOrderedList = r0
                int unused = r11.level = r8
                r1 = 0
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageListOrderedItem> r2 = r0.items
                int r12 = r2.size()
                r6 = r1
            L_0x0404:
                if (r6 >= r12) goto L_0x0629
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageListOrderedItem> r1 = r0.items
                java.lang.Object r1 = r1.get(r6)
                org.telegram.tgnet.TLRPC$PageListOrderedItem r1 = (org.telegram.tgnet.TLRPC.PageListOrderedItem) r1
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r2 = new org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                r4 = 0
                r2.<init>()
                r5 = r2
                int unused = r5.index = r6
                org.telegram.ui.ArticleViewer.TL_pageBlockOrderedListParent unused = r5.parent = r11
                java.util.ArrayList r2 = r11.items
                r2.add(r5)
                boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_pageListOrderedItemText
                java.lang.String r3 = "."
                if (r2 == 0) goto L_0x04a1
                r2 = r1
                org.telegram.tgnet.TLRPC$TL_pageListOrderedItemText r2 = (org.telegram.tgnet.TLRPC.TL_pageListOrderedItemText) r2
                org.telegram.tgnet.TLRPC$RichText r4 = r2.text
                org.telegram.tgnet.TLRPC.RichText unused = r5.textItem = r4
                java.lang.String r4 = r2.num
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 == 0) goto L_0x046c
                boolean r3 = r7.isRtl
                if (r3 == 0) goto L_0x0455
                r3 = 1
                java.lang.Object[] r4 = new java.lang.Object[r3]
                int r16 = r6 + 1
                java.lang.Integer r16 = java.lang.Integer.valueOf(r16)
                r18 = 0
                r4[r18] = r16
                java.lang.String r4 = java.lang.String.format(r15, r4)
                java.lang.String unused = r5.num = r4
                r18 = r0
                goto L_0x049d
            L_0x0455:
                r3 = 1
                r18 = 0
                java.lang.Object[] r4 = new java.lang.Object[r3]
                int r3 = r6 + 1
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                r4[r18] = r3
                java.lang.String r3 = java.lang.String.format(r13, r4)
                java.lang.String unused = r5.num = r3
                r18 = r0
                goto L_0x049d
            L_0x046c:
                boolean r4 = r7.isRtl
                if (r4 == 0) goto L_0x0487
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r3)
                java.lang.String r3 = r2.num
                r4.append(r3)
                java.lang.String r3 = r4.toString()
                java.lang.String unused = r5.num = r3
                r18 = r0
                goto L_0x049d
            L_0x0487:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r18 = r0
                java.lang.String r0 = r2.num
                r4.append(r0)
                r4.append(r3)
                java.lang.String r0 = r4.toString()
                java.lang.String unused = r5.num = r0
            L_0x049d:
                r16 = 0
                goto L_0x0537
            L_0x04a1:
                r18 = r0
                boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_pageListOrderedItemBlocks
                if (r0 == 0) goto L_0x0535
                r0 = r1
                org.telegram.tgnet.TLRPC$TL_pageListOrderedItemBlocks r0 = (org.telegram.tgnet.TLRPC.TL_pageListOrderedItemBlocks) r0
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r2 = r0.blocks
                boolean r2 = r2.isEmpty()
                if (r2 != 0) goto L_0x04bf
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r2 = r0.blocks
                r4 = 0
                java.lang.Object r2 = r2.get(r4)
                org.telegram.tgnet.TLRPC$PageBlock r2 = (org.telegram.tgnet.TLRPC.PageBlock) r2
                org.telegram.tgnet.TLRPC.PageBlock unused = r5.blockItem = r2
                goto L_0x04ce
            L_0x04bf:
                org.telegram.tgnet.TLRPC$TL_pageListOrderedItemText r2 = new org.telegram.tgnet.TLRPC$TL_pageListOrderedItemText
                r2.<init>()
                org.telegram.tgnet.TLRPC$TL_textPlain r4 = new org.telegram.tgnet.TLRPC$TL_textPlain
                r4.<init>()
                r4.text = r14
                r2.text = r4
                r1 = r2
            L_0x04ce:
                java.lang.String r2 = r0.num
                boolean r2 = android.text.TextUtils.isEmpty(r2)
                if (r2 == 0) goto L_0x0504
                boolean r2 = r7.isRtl
                if (r2 == 0) goto L_0x04ef
                r4 = 1
                java.lang.Object[] r2 = new java.lang.Object[r4]
                int r3 = r6 + 1
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                r16 = 0
                r2[r16] = r3
                java.lang.String r2 = java.lang.String.format(r15, r2)
                java.lang.String unused = r5.num = r2
                goto L_0x0537
            L_0x04ef:
                r4 = 1
                r16 = 0
                java.lang.Object[] r2 = new java.lang.Object[r4]
                int r3 = r6 + 1
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                r2[r16] = r3
                java.lang.String r2 = java.lang.String.format(r13, r2)
                java.lang.String unused = r5.num = r2
                goto L_0x0537
            L_0x0504:
                r4 = 1
                r16 = 0
                boolean r2 = r7.isRtl
                if (r2 == 0) goto L_0x0520
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                r2.append(r3)
                java.lang.String r3 = r0.num
                r2.append(r3)
                java.lang.String r2 = r2.toString()
                java.lang.String unused = r5.num = r2
                goto L_0x0537
            L_0x0520:
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r4 = r0.num
                r2.append(r4)
                r2.append(r3)
                java.lang.String r2 = r2.toString()
                java.lang.String unused = r5.num = r2
                goto L_0x0537
            L_0x0535:
                r16 = 0
            L_0x0537:
                r0 = r1
                boolean r1 = r9 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild
                if (r1 == 0) goto L_0x056b
                r20 = r9
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r20 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r20
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r1 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild
                r2 = 0
                r1.<init>()
                r4 = r1
                org.telegram.tgnet.TLRPC$PageBlock r1 = r20.parent
                org.telegram.tgnet.TLRPC.PageBlock unused = r4.parent = r1
                org.telegram.tgnet.TLRPC.PageBlock unused = r4.block = r5
                int r21 = r8 + 1
                r1 = r27
                r2 = r28
                r3 = r4
                r19 = r4
                r22 = 1
                r4 = r30
                r29 = r10
                r10 = r5
                r5 = r21
                r21 = r6
                r6 = r32
                r1.addBlock(r2, r3, r4, r5, r6)
                goto L_0x058f
            L_0x056b:
                r21 = r6
                r29 = r10
                r22 = 1
                r10 = r5
                if (r21 != 0) goto L_0x057d
                org.telegram.ui.ArticleViewer r1 = org.telegram.ui.ArticleViewer.this
                org.telegram.tgnet.TLRPC$PageBlock r1 = r1.fixListBlock(r9, r10)
                r19 = r1
                goto L_0x0580
            L_0x057d:
                r1 = r10
                r19 = r1
            L_0x0580:
                int r5 = r8 + 1
                r1 = r27
                r2 = r28
                r3 = r19
                r4 = r30
                r6 = r32
                r1.addBlock(r2, r3, r4, r5, r6)
            L_0x058f:
                boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageListOrderedItemBlocks
                if (r1 == 0) goto L_0x061f
                r6 = r0
                org.telegram.tgnet.TLRPC$TL_pageListOrderedItemBlocks r6 = (org.telegram.tgnet.TLRPC.TL_pageListOrderedItemBlocks) r6
                r1 = 1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r2 = r6.blocks
                int r5 = r2.size()
                r26 = r10
                r10 = r1
                r1 = r26
            L_0x05a2:
                if (r10 >= r5) goto L_0x0618
                org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem r2 = new org.telegram.ui.ArticleViewer$TL_pageBlockOrderedListItem
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                r4 = 0
                r2.<init>()
                r4 = r2
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r1 = r6.blocks
                java.lang.Object r1 = r1.get(r10)
                org.telegram.tgnet.TLRPC$PageBlock r1 = (org.telegram.tgnet.TLRPC.PageBlock) r1
                org.telegram.tgnet.TLRPC.PageBlock unused = r4.blockItem = r1
                org.telegram.ui.ArticleViewer.TL_pageBlockOrderedListParent unused = r4.parent = r11
                boolean r1 = r9 instanceof org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild
                if (r1 == 0) goto L_0x05f0
                r19 = r9
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r19 = (org.telegram.ui.ArticleViewer.TL_pageBlockDetailsChild) r19
                org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild r1 = new org.telegram.ui.ArticleViewer$TL_pageBlockDetailsChild
                r3 = 0
                r1.<init>()
                r2 = r1
                org.telegram.tgnet.TLRPC$PageBlock r1 = r19.parent
                org.telegram.tgnet.TLRPC.PageBlock unused = r2.parent = r1
                org.telegram.tgnet.TLRPC.PageBlock unused = r2.block = r4
                int r17 = r8 + 1
                r1 = r27
                r20 = r2
                r2 = r28
                r23 = r3
                r3 = r20
                r24 = r4
                r4 = r30
                r25 = r5
                r5 = r17
                r17 = r6
                r6 = r32
                r1.addBlock(r2, r3, r4, r5, r6)
                goto L_0x0607
            L_0x05f0:
                r24 = r4
                r25 = r5
                r17 = r6
                r23 = 0
                int r5 = r8 + 1
                r1 = r27
                r2 = r28
                r3 = r24
                r4 = r30
                r6 = r32
                r1.addBlock(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x0633 }
            L_0x0607:
                java.util.ArrayList r1 = r11.items
                r2 = r24
                r1.add(r2)
                int r10 = r10 + 1
                r1 = r2
                r6 = r17
                r5 = r25
                goto L_0x05a2
            L_0x0618:
                r25 = r5
                r17 = r6
                r23 = 0
                goto L_0x0621
            L_0x061f:
                r23 = 0
            L_0x0621:
                int r6 = r21 + 1
                r10 = r29
                r0 = r18
                goto L_0x0404
            L_0x0629:
                r18 = r0
                r21 = r6
                r29 = r10
                goto L_0x0632
            L_0x0630:
                r29 = r10
            L_0x0632:
                return
            L_0x0633:
                r0 = move-exception
                r1 = r0
                goto L_0x0637
            L_0x0636:
                throw r1
            L_0x0637:
                goto L_0x0636
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.WebpageAdapter.addBlock(org.telegram.ui.ArticleViewer$WebpageAdapter, org.telegram.tgnet.TLRPC$PageBlock, int, int, int):void");
        }

        private void addAllMediaFromBlock(WebpageAdapter adapter, TLRPC.PageBlock block) {
            if (block instanceof TLRPC.TL_pageBlockPhoto) {
                TLRPC.TL_pageBlockPhoto pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) block;
                TLRPC.Photo photo = getPhotoWithId(pageBlockPhoto.photo_id);
                if (photo != null) {
                    pageBlockPhoto.thumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 56, true);
                    pageBlockPhoto.thumbObject = photo;
                    this.photoBlocks.add(block);
                }
            } else if ((block instanceof TLRPC.TL_pageBlockVideo) && WebPageUtils.isVideo(adapter.currentPage, block)) {
                TLRPC.TL_pageBlockVideo pageBlockVideo = (TLRPC.TL_pageBlockVideo) block;
                TLRPC.Document document = getDocumentWithId(pageBlockVideo.video_id);
                if (document != null) {
                    pageBlockVideo.thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 56, true);
                    pageBlockVideo.thumbObject = document;
                    this.photoBlocks.add(block);
                }
            } else if (block instanceof TLRPC.TL_pageBlockSlideshow) {
                TLRPC.TL_pageBlockSlideshow slideshow = (TLRPC.TL_pageBlockSlideshow) block;
                int count = slideshow.items.size();
                for (int a = 0; a < count; a++) {
                    TLRPC.PageBlock innerBlock = slideshow.items.get(a);
                    innerBlock.groupId = ArticleViewer.this.lastBlockNum;
                    addAllMediaFromBlock(adapter, innerBlock);
                }
                ArticleViewer.access$13208(ArticleViewer.this);
            } else if (block instanceof TLRPC.TL_pageBlockCollage) {
                TLRPC.TL_pageBlockCollage collage = (TLRPC.TL_pageBlockCollage) block;
                int count2 = collage.items.size();
                for (int a2 = 0; a2 < count2; a2++) {
                    TLRPC.PageBlock innerBlock2 = collage.items.get(a2);
                    innerBlock2.groupId = ArticleViewer.this.lastBlockNum;
                    addAllMediaFromBlock(adapter, innerBlock2);
                }
                ArticleViewer.access$13208(ArticleViewer.this);
            } else if (block instanceof TLRPC.TL_pageBlockCover) {
                addAllMediaFromBlock(adapter, ((TLRPC.TL_pageBlockCover) block).cover);
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
                    view = new ReportCell(this.context);
                    break;
                default:
                    TextView textView = new TextView(this.context);
                    textView.setBackgroundColor(-65536);
                    textView.setTextColor(-16777216);
                    textView.setTextSize(1, 20.0f);
                    TextView textView2 = textView;
                    view = textView;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            view.setFocusable(true);
            return new RecyclerListView.Holder(view);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 23 || type == 24) {
                return true;
            }
            return false;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position < this.localBlocks.size()) {
                bindBlockToHolder(holder.getItemViewType(), holder, this.localBlocks.get(position), position, this.localBlocks.size());
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 90) {
                ((ReportCell) holder.itemView).setViews(this.currentPage.cached_page != null ? this.currentPage.cached_page.views : 0);
            }
        }

        /* access modifiers changed from: private */
        public void bindBlockToHolder(int type, RecyclerView.ViewHolder holder, TLRPC.PageBlock block, int position, int total) {
            TLRPC.PageBlock originalBlock = block;
            if (block instanceof TLRPC.TL_pageBlockCover) {
                block = ((TLRPC.TL_pageBlockCover) block).cover;
            } else if (block instanceof TL_pageBlockDetailsChild) {
                block = ((TL_pageBlockDetailsChild) block).block;
            }
            boolean z = false;
            switch (type) {
                case 0:
                    ((BlockParagraphCell) holder.itemView).setBlock((TLRPC.TL_pageBlockParagraph) block);
                    return;
                case 1:
                    ((BlockHeaderCell) holder.itemView).setBlock((TLRPC.TL_pageBlockHeader) block);
                    return;
                case 2:
                    BlockDividerCell blockDividerCell = (BlockDividerCell) holder.itemView;
                    return;
                case 3:
                    ((BlockEmbedCell) holder.itemView).setBlock((TLRPC.TL_pageBlockEmbed) block);
                    return;
                case 4:
                    ((BlockSubtitleCell) holder.itemView).setBlock((TLRPC.TL_pageBlockSubtitle) block);
                    return;
                case 5:
                    BlockVideoCell cell = (BlockVideoCell) holder.itemView;
                    TLRPC.TL_pageBlockVideo tL_pageBlockVideo = (TLRPC.TL_pageBlockVideo) block;
                    boolean z2 = position == 0;
                    if (position == total - 1) {
                        z = true;
                    }
                    cell.setBlock(tL_pageBlockVideo, z2, z);
                    cell.setParentBlock(this.channelBlock, originalBlock);
                    return;
                case 6:
                    ((BlockPullquoteCell) holder.itemView).setBlock((TLRPC.TL_pageBlockPullquote) block);
                    return;
                case 7:
                    ((BlockBlockquoteCell) holder.itemView).setBlock((TLRPC.TL_pageBlockBlockquote) block);
                    return;
                case 8:
                    ((BlockSlideshowCell) holder.itemView).setBlock((TLRPC.TL_pageBlockSlideshow) block);
                    return;
                case 9:
                    BlockPhotoCell cell2 = (BlockPhotoCell) holder.itemView;
                    TLRPC.TL_pageBlockPhoto tL_pageBlockPhoto = (TLRPC.TL_pageBlockPhoto) block;
                    boolean z3 = position == 0;
                    if (position == total - 1) {
                        z = true;
                    }
                    cell2.setBlock(tL_pageBlockPhoto, z3, z);
                    cell2.setParentBlock(originalBlock);
                    return;
                case 10:
                    ((BlockAuthorDateCell) holder.itemView).setBlock((TLRPC.TL_pageBlockAuthorDate) block);
                    return;
                case 11:
                    ((BlockTitleCell) holder.itemView).setBlock((TLRPC.TL_pageBlockTitle) block);
                    return;
                case 12:
                    ((BlockListItemCell) holder.itemView).setBlock((TL_pageBlockListItem) block);
                    return;
                case 13:
                    ((BlockFooterCell) holder.itemView).setBlock((TLRPC.TL_pageBlockFooter) block);
                    return;
                case 14:
                    ((BlockPreformattedCell) holder.itemView).setBlock((TLRPC.TL_pageBlockPreformatted) block);
                    return;
                case 15:
                    ((BlockSubheaderCell) holder.itemView).setBlock((TLRPC.TL_pageBlockSubheader) block);
                    return;
                case 16:
                    ((BlockEmbedPostCell) holder.itemView).setBlock((TLRPC.TL_pageBlockEmbedPost) block);
                    return;
                case 17:
                    ((BlockCollageCell) holder.itemView).setBlock((TLRPC.TL_pageBlockCollage) block);
                    return;
                case 18:
                    ((BlockChannelCell) holder.itemView).setBlock((TLRPC.TL_pageBlockChannel) block);
                    return;
                case 19:
                    BlockAudioCell cell3 = (BlockAudioCell) holder.itemView;
                    TLRPC.TL_pageBlockAudio tL_pageBlockAudio = (TLRPC.TL_pageBlockAudio) block;
                    boolean z4 = position == 0;
                    if (position == total - 1) {
                        z = true;
                    }
                    cell3.setBlock(tL_pageBlockAudio, z4, z);
                    return;
                case 20:
                    ((BlockKickerCell) holder.itemView).setBlock((TLRPC.TL_pageBlockKicker) block);
                    return;
                case 21:
                    ((BlockOrderedListItemCell) holder.itemView).setBlock((TL_pageBlockOrderedListItem) block);
                    return;
                case 22:
                    BlockMapCell cell4 = (BlockMapCell) holder.itemView;
                    TLRPC.TL_pageBlockMap tL_pageBlockMap = (TLRPC.TL_pageBlockMap) block;
                    boolean z5 = position == 0;
                    if (position == total - 1) {
                        z = true;
                    }
                    cell4.setBlock(tL_pageBlockMap, z5, z);
                    return;
                case 23:
                    ((BlockRelatedArticlesCell) holder.itemView).setBlock((TL_pageBlockRelatedArticlesChild) block);
                    return;
                case 24:
                    ((BlockDetailsCell) holder.itemView).setBlock((TLRPC.TL_pageBlockDetails) block);
                    return;
                case 25:
                    ((BlockTableCell) holder.itemView).setBlock((TLRPC.TL_pageBlockTable) block);
                    return;
                case 26:
                    ((BlockRelatedArticlesHeaderCell) holder.itemView).setBlock((TLRPC.TL_pageBlockRelatedArticles) block);
                    return;
                case 27:
                    BlockDetailsBottomCell blockDetailsBottomCell = (BlockDetailsBottomCell) holder.itemView;
                    return;
                case 100:
                    ((TextView) holder.itemView).setText("unsupported block " + block);
                    return;
                default:
                    return;
            }
        }

        /* access modifiers changed from: private */
        public int getTypeForBlock(TLRPC.PageBlock block) {
            if (block instanceof TLRPC.TL_pageBlockParagraph) {
                return 0;
            }
            if (block instanceof TLRPC.TL_pageBlockHeader) {
                return 1;
            }
            if (block instanceof TLRPC.TL_pageBlockDivider) {
                return 2;
            }
            if (block instanceof TLRPC.TL_pageBlockEmbed) {
                return 3;
            }
            if (block instanceof TLRPC.TL_pageBlockSubtitle) {
                return 4;
            }
            if (block instanceof TLRPC.TL_pageBlockVideo) {
                return 5;
            }
            if (block instanceof TLRPC.TL_pageBlockPullquote) {
                return 6;
            }
            if (block instanceof TLRPC.TL_pageBlockBlockquote) {
                return 7;
            }
            if (block instanceof TLRPC.TL_pageBlockSlideshow) {
                return 8;
            }
            if (block instanceof TLRPC.TL_pageBlockPhoto) {
                return 9;
            }
            if (block instanceof TLRPC.TL_pageBlockAuthorDate) {
                return 10;
            }
            if (block instanceof TLRPC.TL_pageBlockTitle) {
                return 11;
            }
            if (block instanceof TL_pageBlockListItem) {
                return 12;
            }
            if (block instanceof TLRPC.TL_pageBlockFooter) {
                return 13;
            }
            if (block instanceof TLRPC.TL_pageBlockPreformatted) {
                return 14;
            }
            if (block instanceof TLRPC.TL_pageBlockSubheader) {
                return 15;
            }
            if (block instanceof TLRPC.TL_pageBlockEmbedPost) {
                return 16;
            }
            if (block instanceof TLRPC.TL_pageBlockCollage) {
                return 17;
            }
            if (block instanceof TLRPC.TL_pageBlockChannel) {
                return 18;
            }
            if (block instanceof TLRPC.TL_pageBlockAudio) {
                return 19;
            }
            if (block instanceof TLRPC.TL_pageBlockKicker) {
                return 20;
            }
            if (block instanceof TL_pageBlockOrderedListItem) {
                return 21;
            }
            if (block instanceof TLRPC.TL_pageBlockMap) {
                return 22;
            }
            if (block instanceof TL_pageBlockRelatedArticlesChild) {
                return 23;
            }
            if (block instanceof TLRPC.TL_pageBlockDetails) {
                return 24;
            }
            if (block instanceof TLRPC.TL_pageBlockTable) {
                return 25;
            }
            if (block instanceof TLRPC.TL_pageBlockRelatedArticles) {
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
            if (block instanceof TLRPC.TL_pageBlockCover) {
                return getTypeForBlock(((TLRPC.TL_pageBlockCover) block).cover);
            }
            return 100;
        }

        public int getItemViewType(int position) {
            if (position == this.localBlocks.size()) {
                return 90;
            }
            return getTypeForBlock(this.localBlocks.get(position));
        }

        public TLRPC.PageBlock getItem(int position) {
            return this.localBlocks.get(position);
        }

        public int getItemCount() {
            TLRPC.WebPage webPage = this.currentPage;
            if (webPage == null || webPage.cached_page == null) {
                return 0;
            }
            return this.localBlocks.size() + 1;
        }

        private boolean isBlockOpened(TL_pageBlockDetailsChild child) {
            TLRPC.PageBlock parentBlock = ArticleViewer.this.getLastNonListPageBlock(child.parent);
            if (parentBlock instanceof TLRPC.TL_pageBlockDetails) {
                return ((TLRPC.TL_pageBlockDetails) parentBlock).open;
            }
            if (!(parentBlock instanceof TL_pageBlockDetailsChild)) {
                return false;
            }
            TL_pageBlockDetailsChild parent = (TL_pageBlockDetailsChild) parentBlock;
            TLRPC.PageBlock parentBlock2 = ArticleViewer.this.getLastNonListPageBlock(parent.block);
            if (!(parentBlock2 instanceof TLRPC.TL_pageBlockDetails) || ((TLRPC.TL_pageBlockDetails) parentBlock2).open) {
                return isBlockOpened(parent);
            }
            return false;
        }

        /* access modifiers changed from: private */
        public void updateRows() {
            this.localBlocks.clear();
            int size = this.blocks.size();
            for (int a = 0; a < size; a++) {
                TLRPC.PageBlock originalBlock = this.blocks.get(a);
                TLRPC.PageBlock block = ArticleViewer.this.getLastNonListPageBlock(originalBlock);
                if (!(block instanceof TL_pageBlockDetailsChild) || isBlockOpened((TL_pageBlockDetailsChild) block)) {
                    this.localBlocks.add(originalBlock);
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
        public TLRPC.TL_pageBlockVideo currentBlock;
        private TLRPC.Document currentDocument;
        private int currentType;
        /* access modifiers changed from: private */
        public MessageObject.GroupedMessagePosition groupPosition;
        /* access modifiers changed from: private */
        public ImageReceiver imageView;
        private boolean isFirst;
        private boolean isGif;
        private WebpageAdapter parentAdapter;
        private TLRPC.PageBlock parentBlock;
        private boolean photoPressed;
        private RadialProgress2 radialProgress;
        private int textX;
        private int textY;

        public BlockVideoCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.imageView = imageReceiver;
            imageReceiver.setNeedsQualityThumb(true);
            this.imageView.setShouldGenerateQualityThumb(true);
            this.currentType = type;
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setProgressColor(-1);
            this.radialProgress.setColors(NUM, NUM, -1, -2500135);
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            BlockChannelCell blockChannelCell = new BlockChannelCell(context, this.parentAdapter, 1);
            this.channelCell = blockChannelCell;
            addView(blockChannelCell, LayoutHelper.createFrame(-1, -2.0f));
        }

        public void setBlock(TLRPC.TL_pageBlockVideo block, boolean first, boolean last) {
            this.currentBlock = block;
            this.parentBlock = null;
            TLRPC.Document access$13300 = this.parentAdapter.getDocumentWithId(block.video_id);
            this.currentDocument = access$13300;
            this.isGif = MessageObject.isVideoDocument(access$13300) || MessageObject.isGifDocument(this.currentDocument);
            this.isFirst = first;
            this.channelCell.setVisibility(4);
            updateButtonState(false);
            requestLayout();
        }

        public void setParentBlock(TLRPC.TL_pageBlockChannel channelBlock, TLRPC.PageBlock block) {
            this.parentBlock = block;
            if (channelBlock != null && (block instanceof TLRPC.TL_pageBlockCover)) {
                this.channelCell.setBlock(channelBlock);
                this.channelCell.setVisibility(0);
            }
        }

        public View getChannelCell() {
            return this.channelCell;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a3, code lost:
            if (r2 <= ((float) (r3 + org.telegram.messenger.AndroidUtilities.dp(48.0f)))) goto L_0x00a9;
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
                org.telegram.ui.ArticleViewer$WebpageAdapter r3 = r12.parentAdapter
                org.telegram.tgnet.TLRPC$TL_pageBlockChannel r3 = r3.channelBlock
                if (r3 == 0) goto L_0x006d
                int r3 = r13.getAction()
                if (r3 != r1) goto L_0x006d
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                int r3 = r3.currentAccount
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
                org.telegram.ui.ArticleViewer$WebpageAdapter r5 = r12.parentAdapter
                org.telegram.tgnet.TLRPC$TL_pageBlockChannel r5 = r5.channelBlock
                org.telegram.tgnet.TLRPC$Chat r5 = r5.channel
                java.lang.String r5 = r5.username
                org.telegram.ui.ArticleViewer r6 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ActionBar.BaseFragment r6 = r6.parentFragment
                r7 = 2
                r3.openByUserName(r5, r6, r7)
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                r3.close(r4, r1)
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
                int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
                if (r3 > 0) goto L_0x00a5
                int r3 = r12.buttonY
                float r6 = (float) r3
                int r6 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
                if (r6 < 0) goto L_0x00a5
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r3 = r3 + r5
                float r3 = (float) r3
                int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r3 <= 0) goto L_0x00a9
            L_0x00a5:
                int r3 = r12.buttonState
                if (r3 != 0) goto L_0x00af
            L_0x00a9:
                r12.buttonPressed = r1
                r12.invalidate()
                goto L_0x00e1
            L_0x00af:
                r12.photoPressed = r1
                goto L_0x00e1
            L_0x00b2:
                int r3 = r13.getAction()
                if (r3 != r1) goto L_0x00d8
                boolean r3 = r12.photoPressed
                if (r3 == 0) goto L_0x00c8
                r12.photoPressed = r4
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                org.telegram.tgnet.TLRPC$TL_pageBlockVideo r5 = r12.currentBlock
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r12.parentAdapter
                r3.openPhoto(r5, r6)
                goto L_0x00e1
            L_0x00c8:
                int r3 = r12.buttonPressed
                if (r3 != r1) goto L_0x00e1
                r12.buttonPressed = r4
                r12.playSoundEffect(r4)
                r12.didPressedButton(r1)
                r12.invalidate()
                goto L_0x00e1
            L_0x00d8:
                int r3 = r13.getAction()
                r5 = 3
                if (r3 != r5) goto L_0x00e1
                r12.photoPressed = r4
            L_0x00e1:
                boolean r3 = r12.photoPressed
                if (r3 != 0) goto L_0x0119
                int r3 = r12.buttonPressed
                if (r3 != 0) goto L_0x0119
                org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r12.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r9 = r12.captionLayout
                int r10 = r12.textX
                int r11 = r12.textY
                r7 = r13
                r8 = r12
                boolean r3 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10, r11)
                if (r3 != 0) goto L_0x0119
                org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r12.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r9 = r12.creditLayout
                int r10 = r12.textX
                int r3 = r12.textY
                int r7 = r12.creditOffset
                int r11 = r3 + r7
                r7 = r13
                r8 = r12
                boolean r3 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10, r11)
                if (r3 != 0) goto L_0x0119
                boolean r3 = super.onTouchEvent(r13)
                if (r3 == 0) goto L_0x0118
                goto L_0x0119
            L_0x0118:
                r1 = 0
            L_0x0119:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockVideoCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width;
            int height;
            int textWidth;
            int photoX;
            int height2;
            int i;
            int width2 = View.MeasureSpec.getSize(widthMeasureSpec);
            int height3 = 0;
            int i2 = this.currentType;
            if (i2 == 1) {
                int width3 = ((View) getParent()).getMeasuredWidth();
                height3 = ((View) getParent()).getMeasuredHeight();
                width = width3;
            } else if (i2 == 2) {
                height3 = (int) Math.ceil((double) (this.groupPosition.ph * ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f));
                width = width2;
            } else {
                width = width2;
            }
            TLRPC.TL_pageBlockVideo tL_pageBlockVideo = this.currentBlock;
            if (tL_pageBlockVideo != null) {
                int photoWidth = width;
                int photoHeight = height3;
                if (this.currentType != 0 || tL_pageBlockVideo.level <= 0) {
                    photoX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                } else {
                    int dp = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                    photoX = dp;
                    this.textX = dp;
                    photoWidth -= AndroidUtilities.dp(18.0f) + photoX;
                    textWidth = photoWidth;
                }
                if (this.currentDocument != null) {
                    int size = AndroidUtilities.dp(48.0f);
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(this.currentDocument.thumbs, 48);
                    int i3 = this.currentType;
                    if (i3 == 0) {
                        boolean found = false;
                        int a = 0;
                        int count = this.currentDocument.attributes.size();
                        while (true) {
                            if (a >= count) {
                                break;
                            }
                            TLRPC.DocumentAttribute attribute = this.currentDocument.attributes.get(a);
                            if (attribute instanceof TLRPC.TL_documentAttributeVideo) {
                                height3 = (int) (((float) attribute.h) * (((float) photoWidth) / ((float) attribute.w)));
                                found = true;
                                break;
                            }
                            a++;
                        }
                        float w = thumb != null ? (float) thumb.w : 100.0f;
                        float h = thumb != null ? (float) thumb.h : 100.0f;
                        if (!found) {
                            height3 = (int) ((((float) photoWidth) / w) * h);
                        }
                        if (this.parentBlock instanceof TLRPC.TL_pageBlockCover) {
                            height3 = Math.min(height3, photoWidth);
                        } else {
                            int maxHeight = (int) (((float) (Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(56.0f))) * 0.9f);
                            if (height3 > maxHeight) {
                                height3 = maxHeight;
                                photoWidth = (int) ((((float) height3) / h) * w);
                                photoX += ((width - photoX) - photoWidth) / 2;
                            }
                        }
                        if (height3 == 0) {
                            height3 = AndroidUtilities.dp(100.0f);
                        } else if (height3 < size) {
                            height3 = size;
                        }
                        photoHeight = height3;
                    } else if (i3 == 2) {
                        if ((this.groupPosition.flags & 2) == 0) {
                            photoWidth -= AndroidUtilities.dp(2.0f);
                        }
                        if ((this.groupPosition.flags & 8) == 0) {
                            photoHeight -= AndroidUtilities.dp(2.0f);
                        }
                    }
                    this.imageView.setQualityThumbDocument(this.currentDocument);
                    this.imageView.setImageCoords((float) photoX, (this.isFirst || (i = this.currentType) == 1 || i == 2 || this.currentBlock.level > 0) ? 0.0f : (float) AndroidUtilities.dp(8.0f), (float) photoWidth, (float) photoHeight);
                    if (this.isGif) {
                        this.autoDownload = DownloadController.getInstance(ArticleViewer.this.currentAccount).canDownloadMedia(4, this.currentDocument.size);
                        File path = FileLoader.getInstance(ArticleViewer.this.currentAccount).getPathToAttach(this.currentDocument, true);
                        if (this.autoDownload || path.exists()) {
                            this.imageView.setStrippedLocation((ImageLocation) null);
                            this.imageView.setImage(ImageLocation.getForDocument(this.currentDocument), "g", (ImageLocation) null, (String) null, ImageLocation.getForDocument(thumb, this.currentDocument), "80_80_b", (Drawable) null, this.currentDocument.size, (String) null, this.parentAdapter.currentPage, 1);
                        } else {
                            this.imageView.setStrippedLocation(ImageLocation.getForDocument(this.currentDocument));
                            this.imageView.setImage((ImageLocation) null, (String) null, (ImageLocation) null, (String) null, ImageLocation.getForDocument(thumb, this.currentDocument), "80_80_b", (Drawable) null, this.currentDocument.size, (String) null, this.parentAdapter.currentPage, 1);
                        }
                    } else {
                        this.imageView.setStrippedLocation((ImageLocation) null);
                        this.imageView.setImage((ImageLocation) null, (String) null, ImageLocation.getForDocument(thumb, this.currentDocument), "80_80_b", 0, (String) null, this.parentAdapter.currentPage, 1);
                    }
                    this.imageView.setAspectFit(true);
                    this.buttonX = (int) (this.imageView.getImageX() + ((this.imageView.getImageWidth() - ((float) size)) / 2.0f));
                    int imageY = (int) (this.imageView.getImageY() + ((this.imageView.getImageHeight() - ((float) size)) / 2.0f));
                    this.buttonY = imageY;
                    RadialProgress2 radialProgress2 = this.radialProgress;
                    int i4 = this.buttonX;
                    radialProgress2.setProgressRect(i4, imageY, i4 + size, imageY + size);
                    height = height3;
                    int i5 = photoWidth;
                    int i6 = photoHeight;
                    int i7 = photoX;
                } else {
                    height = height3;
                    int i8 = photoWidth;
                    int i9 = photoHeight;
                    int i10 = photoX;
                }
                this.textY = (int) (this.imageView.getImageY() + this.imageView.getImageHeight() + ((float) AndroidUtilities.dp(8.0f)));
                if (this.currentType == 0) {
                    DrawingText access$13500 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                    this.captionLayout = access$13500;
                    if (access$13500 != null) {
                        int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                        this.creditOffset = dp2;
                        int height4 = height + dp2 + AndroidUtilities.dp(4.0f);
                        this.captionLayout.x = this.textX;
                        this.captionLayout.y = this.textY;
                        height2 = height4;
                    } else {
                        height2 = height;
                    }
                    DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                    this.creditLayout = access$13600;
                    if (access$13600 != null) {
                        height = height2 + AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                        this.creditLayout.x = this.textX;
                        this.creditLayout.y = this.textY + this.creditOffset;
                    } else {
                        height = height2;
                    }
                }
                if (!this.isFirst && this.currentType == 0 && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                boolean nextIsChannel = (this.parentBlock instanceof TLRPC.TL_pageBlockCover) && this.parentAdapter.blocks.size() > 1 && (this.parentAdapter.blocks.get(1) instanceof TLRPC.TL_pageBlockChannel);
                if (this.currentType != 2 && !nextIsChannel) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            this.channelCell.measure(widthMeasureSpec, heightMeasureSpec);
            this.channelCell.setTranslationY(this.imageView.getImageHeight() - ((float) AndroidUtilities.dp(39.0f)));
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
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
                int count = 0;
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.captionLayout.draw(canvas, this);
                    canvas.restore();
                    count = 0 + 1;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, count);
                    this.creditLayout.draw(canvas, this);
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
            if (i == 3) {
                return 0;
            }
            return 4;
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentDocument);
            boolean fileExists = FileLoader.getInstance(ArticleViewer.this.currentAccount).getPathToAttach(this.currentDocument, true).exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setIcon(4, false, false);
                return;
            }
            if (fileExists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                if (!this.isGif) {
                    this.buttonState = 3;
                } else {
                    this.buttonState = -1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(fileName, (MessageObject) null, this);
                float setProgress = 0.0f;
                boolean progressVisible = false;
                if (FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(fileName)) {
                    progressVisible = true;
                    this.buttonState = 1;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    setProgress = progress != null ? progress.floatValue() : 0.0f;
                } else if (this.cancelLoading || !this.autoDownload || !this.isGif) {
                    this.buttonState = 0;
                } else {
                    progressVisible = true;
                    this.buttonState = 1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), progressVisible, animated);
                this.radialProgress.setProgress(setProgress, false);
            }
            invalidate();
        }

        private void didPressedButton(boolean animated) {
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
                this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                invalidate();
            } else if (i == 1) {
                this.cancelLoading = true;
                if (this.isGif) {
                    this.imageView.cancelLoadImage();
                } else {
                    FileLoader.getInstance(ArticleViewer.this.currentAccount).cancelLoadFile(this.currentDocument);
                }
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                invalidate();
            } else if (i == 2) {
                this.imageView.setAllowStartAnimation(true);
                this.imageView.startAnimation();
                this.buttonState = -1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
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

        public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
        }

        public void onProgressDownload(String fileName, long downloadSize, long totalSize) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) downloadSize) / ((float) totalSize)), true);
            if (this.buttonState != 1) {
                updateButtonState(true);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            StringBuilder sb = new StringBuilder(LocaleController.getString("AttachVideo", NUM));
            if (this.captionLayout != null) {
                sb.append(", ");
                sb.append(this.captionLayout.getText());
            }
            info.setText(sb.toString());
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
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
        private TLRPC.TL_pageBlockAudio currentBlock;
        private TLRPC.Document currentDocument;
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

        public BlockAudioCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setCircleRadius(AndroidUtilities.dp(24.0f));
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            SeekBar seekBar2 = new SeekBar(this);
            this.seekBar = seekBar2;
            seekBar2.setDelegate(new ArticleViewer$BlockAudioCell$$ExternalSyntheticLambda0(this));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ArticleViewer$BlockAudioCell  reason: not valid java name */
        public /* synthetic */ void m2708lambda$new$0$orgtelegramuiArticleViewer$BlockAudioCell(float progress) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject != null) {
                messageObject.audioProgress = progress;
                MediaController.getInstance().seekToProgress(this.currentMessageObject, progress);
            }
        }

        public void setBlock(TLRPC.TL_pageBlockAudio block, boolean first, boolean last) {
            this.currentBlock = block;
            MessageObject messageObject = (MessageObject) this.parentAdapter.audioBlocks.get(this.currentBlock);
            this.currentMessageObject = messageObject;
            if (messageObject != null) {
                this.currentDocument = messageObject.getDocument();
            }
            this.isFirst = first;
            this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioCacheSeekbar"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarSelected"));
            updateButtonState(false);
            requestLayout();
        }

        public MessageObject getMessageObject() {
            return this.currentMessageObject;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0064, code lost:
            if (r1 <= ((float) (r4 + org.telegram.messenger.AndroidUtilities.dp(48.0f)))) goto L_0x006a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0068, code lost:
            if (r13.buttonState == 0) goto L_0x006a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x006a, code lost:
            r13.buttonPressed = 1;
            invalidate();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r14) {
            /*
                r13 = this;
                float r0 = r14.getX()
                float r1 = r14.getY()
                org.telegram.ui.Components.SeekBar r2 = r13.seekBar
                int r3 = r14.getAction()
                float r4 = r14.getX()
                int r5 = r13.seekBarX
                float r5 = (float) r5
                float r4 = r4 - r5
                float r5 = r14.getY()
                int r6 = r13.seekBarY
                float r6 = (float) r6
                float r5 = r5 - r6
                boolean r2 = r2.onTouch(r3, r4, r5)
                r3 = 1
                if (r2 == 0) goto L_0x0036
                int r4 = r14.getAction()
                if (r4 != 0) goto L_0x0032
                android.view.ViewParent r4 = r13.getParent()
                r4.requestDisallowInterceptTouchEvent(r3)
            L_0x0032:
                r13.invalidate()
                return r3
            L_0x0036:
                int r4 = r14.getAction()
                r5 = 0
                if (r4 != 0) goto L_0x0070
                int r4 = r13.buttonState
                r6 = -1
                if (r4 == r6) goto L_0x0066
                int r4 = r13.buttonX
                float r6 = (float) r4
                int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
                if (r6 < 0) goto L_0x0066
                r6 = 1111490560(0x42400000, float:48.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r4 = r4 + r7
                float r4 = (float) r4
                int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r4 > 0) goto L_0x0066
                int r4 = r13.buttonY
                float r7 = (float) r4
                int r7 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
                if (r7 < 0) goto L_0x0066
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r4 = r4 + r6
                float r4 = (float) r4
                int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r4 <= 0) goto L_0x006a
            L_0x0066:
                int r4 = r13.buttonState
                if (r4 != 0) goto L_0x008f
            L_0x006a:
                r13.buttonPressed = r3
                r13.invalidate()
                goto L_0x008f
            L_0x0070:
                int r4 = r14.getAction()
                if (r4 != r3) goto L_0x0086
                int r4 = r13.buttonPressed
                if (r4 != r3) goto L_0x008f
                r13.buttonPressed = r5
                r13.playSoundEffect(r5)
                r13.didPressedButton(r3)
                r13.invalidate()
                goto L_0x008f
            L_0x0086:
                int r4 = r14.getAction()
                r6 = 3
                if (r4 != r6) goto L_0x008f
                r13.buttonPressed = r5
            L_0x008f:
                int r4 = r13.buttonPressed
                if (r4 != 0) goto L_0x00c3
                org.telegram.ui.ArticleViewer r6 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r7 = r13.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r10 = r13.captionLayout
                int r11 = r13.textX
                int r12 = r13.textY
                r8 = r14
                r9 = r13
                boolean r4 = r6.checkLayoutForLinks(r7, r8, r9, r10, r11, r12)
                if (r4 != 0) goto L_0x00c3
                org.telegram.ui.ArticleViewer r6 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r7 = r13.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r10 = r13.creditLayout
                int r11 = r13.textX
                int r4 = r13.textY
                int r8 = r13.creditOffset
                int r12 = r4 + r8
                r8 = r14
                r9 = r13
                boolean r4 = r6.checkLayoutForLinks(r7, r8, r9, r10, r11, r12)
                if (r4 != 0) goto L_0x00c3
                boolean r4 = super.onTouchEvent(r14)
                if (r4 == 0) goto L_0x00c2
                goto L_0x00c3
            L_0x00c2:
                r3 = 0
            L_0x00c3:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockAudioCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            SpannableStringBuilder stringBuilder;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height2 = AndroidUtilities.dp(54.0f);
            TLRPC.TL_pageBlockAudio tL_pageBlockAudio = this.currentBlock;
            if (tL_pageBlockAudio != null) {
                if (tL_pageBlockAudio.level > 0) {
                    this.textX = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = AndroidUtilities.dp(18.0f);
                }
                int textWidth = (width - this.textX) - AndroidUtilities.dp(18.0f);
                int size = AndroidUtilities.dp(44.0f);
                this.buttonX = AndroidUtilities.dp(16.0f);
                int dp = AndroidUtilities.dp(5.0f);
                this.buttonY = dp;
                RadialProgress2 radialProgress2 = this.radialProgress;
                int i = this.buttonX;
                radialProgress2.setProgressRect(i, dp, i + size, dp + size);
                DrawingText access$13500 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                this.captionLayout = access$13500;
                if (access$13500 != null) {
                    int dp2 = AndroidUtilities.dp(8.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp2;
                    height = height2 + dp2 + AndroidUtilities.dp(8.0f);
                } else {
                    height = height2;
                }
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = access$13600;
                if (access$13600 != null) {
                    height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                }
                if (!this.isFirst && this.currentBlock.level <= 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                String author = this.currentMessageObject.getMusicAuthor(false);
                String title = this.currentMessageObject.getMusicTitle(false);
                int dp3 = this.buttonX + AndroidUtilities.dp(50.0f) + size;
                this.seekBarX = dp3;
                int w = (width - dp3) - AndroidUtilities.dp(18.0f);
                if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(author)) {
                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(author)) {
                        stringBuilder = new SpannableStringBuilder(String.format("%s - %s", new Object[]{author, title}));
                    } else if (!TextUtils.isEmpty(title)) {
                        stringBuilder = new SpannableStringBuilder(title);
                    } else {
                        stringBuilder = new SpannableStringBuilder(author);
                    }
                    if (!TextUtils.isEmpty(author)) {
                        stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, author.length(), 18);
                    }
                    CharSequence stringFinal = TextUtils.ellipsize(stringBuilder, Theme.chat_audioTitlePaint, (float) w, TextUtils.TruncateAt.END);
                    DrawingText drawingText = new DrawingText();
                    this.titleLayout = drawingText;
                    drawingText.textLayout = new StaticLayout(stringFinal, ArticleViewer.audioTimePaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.titleLayout.parentBlock = this.currentBlock;
                    this.seekBarY = this.buttonY + ((size - AndroidUtilities.dp(30.0f)) / 2) + AndroidUtilities.dp(11.0f);
                } else {
                    this.titleLayout = null;
                    this.seekBarY = this.buttonY + ((size - AndroidUtilities.dp(30.0f)) / 2);
                }
                this.seekBar.setSize(w, AndroidUtilities.dp(30.0f));
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            updatePlayingMessageProgress();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
                this.radialProgress.setProgressColor(Theme.getColor("chat_inFileProgress"));
                this.radialProgress.draw(canvas);
                canvas.save();
                canvas.translate((float) this.seekBarX, (float) this.seekBarY);
                this.seekBar.draw(canvas);
                canvas.restore();
                int count = 0;
                if (this.durationLayout != null) {
                    canvas.save();
                    canvas.translate((float) (this.buttonX + AndroidUtilities.dp(54.0f)), (float) (this.seekBarY + AndroidUtilities.dp(6.0f)));
                    this.durationLayout.draw(canvas);
                    canvas.restore();
                }
                if (this.titleLayout != null) {
                    canvas.save();
                    this.titleLayout.x = this.buttonX + AndroidUtilities.dp(54.0f);
                    this.titleLayout.y = this.seekBarY - AndroidUtilities.dp(16.0f);
                    canvas.translate((float) this.titleLayout.x, (float) this.titleLayout.y);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.titleLayout.draw(canvas, this);
                    canvas.restore();
                    count = 0 + 1;
                }
                if (this.captionLayout != null) {
                    canvas.save();
                    this.captionLayout.x = this.textX;
                    this.captionLayout.y = this.textY;
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, count);
                    this.captionLayout.draw(canvas, this);
                    canvas.restore();
                    count++;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    this.creditLayout.x = this.textX;
                    this.creditLayout.y = this.textY + this.creditOffset;
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, count);
                    this.creditLayout.draw(canvas, this);
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
            if (i == 3) {
                return 3;
            }
            return 0;
        }

        public void updatePlayingMessageProgress() {
            if (this.currentDocument != null && this.currentMessageObject != null) {
                if (!this.seekBar.isDragging()) {
                    this.seekBar.setProgress(this.currentMessageObject.audioProgress);
                }
                int duration = 0;
                if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    int a = 0;
                    while (true) {
                        if (a >= this.currentDocument.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute = this.currentDocument.attributes.get(a);
                        if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                            duration = attribute.duration;
                            break;
                        }
                        a++;
                    }
                } else {
                    duration = this.currentMessageObject.audioProgressSec;
                }
                String timeString = AndroidUtilities.formatShortDuration(duration);
                String str = this.lastTimeString;
                if (str == null || (str != null && !str.equals(timeString))) {
                    this.lastTimeString = timeString;
                    ArticleViewer.audioTimePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
                    this.durationLayout = new StaticLayout(timeString, ArticleViewer.audioTimePaint, (int) Math.ceil((double) ArticleViewer.audioTimePaint.measureText(timeString)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
                ArticleViewer.audioTimePaint.setColor(ArticleViewer.this.getTextColor());
                invalidate();
            }
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentDocument);
            boolean fileExists = FileLoader.getInstance(ArticleViewer.this.currentAccount).getPathToAttach(this.currentDocument, true).exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setIcon(4, false, false);
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
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(fileName, (MessageObject) null, this);
                if (!FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, animated);
                    this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                } else {
                    this.buttonState = 3;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress != null) {
                        this.radialProgress.setProgress(progress.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                    this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                }
            }
            updatePlayingMessageProgress();
        }

        private void didPressedButton(boolean animated) {
            int i = this.buttonState;
            if (i == 0) {
                if (MediaController.getInstance().setPlaylist(this.parentAdapter.audioMessages, this.currentMessageObject, 0, false, (MediaController.PlaylistGlobalSearchParams) null)) {
                    this.buttonState = 1;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                    invalidate();
                }
            } else if (i == 1) {
                if (MediaController.getInstance().m104lambda$startAudioAgain$7$orgtelegrammessengerMediaController(this.currentMessageObject)) {
                    this.buttonState = 0;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                    invalidate();
                }
            } else if (i == 2) {
                this.radialProgress.setProgress(0.0f, false);
                FileLoader.getInstance(ArticleViewer.this.currentAccount).loadFile(this.currentDocument, this.parentAdapter.currentPage, 1, 1);
                this.buttonState = 3;
                this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                invalidate();
            } else if (i == 3) {
                FileLoader.getInstance(ArticleViewer.this.currentAccount).cancelLoadFile(this.currentDocument);
                this.buttonState = 2;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
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

        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(true);
        }

        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
        }

        public void onProgressDownload(String fileName, long downloadSize, long totalSize) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) downloadSize) / ((float) totalSize)), true);
            if (this.buttonState != 3) {
                updateButtonState(true);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.titleLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.captionLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
            DrawingText drawingText3 = this.creditLayout;
            if (drawingText3 != null) {
                blocks.add(drawingText3);
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
        private TLRPC.TL_pageBlockEmbedPost currentBlock;
        private DrawingText dateLayout;
        private int dateX;
        private int lineHeight;
        private DrawingText nameLayout;
        private int nameX;
        private WebpageAdapter parentAdapter;
        private int textX;
        private int textY;

        public BlockEmbedPostCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.avatarImageView = imageReceiver;
            imageReceiver.setRoundRadius(AndroidUtilities.dp(20.0f));
            this.avatarImageView.setImageCoords((float) AndroidUtilities.dp(32.0f), (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(40.0f), (float) AndroidUtilities.dp(40.0f));
        }

        public void setBlock(TLRPC.TL_pageBlockEmbedPost block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.captionLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int height2;
            int height3;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            TLRPC.TL_pageBlockEmbedPost tL_pageBlockEmbedPost = this.currentBlock;
            if (tL_pageBlockEmbedPost != null) {
                if (tL_pageBlockEmbedPost instanceof TL_pageBlockEmbedPostCaption) {
                    this.textX = AndroidUtilities.dp(18.0f);
                    this.textY = AndroidUtilities.dp(4.0f);
                    int textWidth = width - AndroidUtilities.dp(50.0f);
                    DrawingText access$13500 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                    this.captionLayout = access$13500;
                    if (access$13500 != null) {
                        int dp = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                        this.creditOffset = dp;
                        height = 0 + dp + AndroidUtilities.dp(4.0f);
                    } else {
                        height = 0;
                    }
                    DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                    this.creditLayout = access$13600;
                    if (access$13600 != null) {
                        height += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    }
                } else {
                    int i = 0;
                    boolean z = tL_pageBlockEmbedPost.author_photo_id != 0;
                    this.avatarVisible = z;
                    if (z) {
                        TLRPC.Photo photo = this.parentAdapter.getPhotoWithId(this.currentBlock.author_photo_id);
                        boolean z2 = photo instanceof TLRPC.TL_photo;
                        this.avatarVisible = z2;
                        if (z2) {
                            this.avatarDrawable.setInfo(0, this.currentBlock.author, (String) null);
                            this.avatarImageView.setImage(ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.dp(40.0f), true), photo), "40_40", (Drawable) this.avatarDrawable, 0, (String) null, (Object) this.parentAdapter.currentPage, 1);
                        }
                    }
                    DrawingText access$14500 = ArticleViewer.this.createLayoutForText(this, this.currentBlock.author, (TLRPC.RichText) null, width - AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 50)), 0, this.currentBlock, Layout.Alignment.ALIGN_NORMAL, 1, this.parentAdapter);
                    this.nameLayout = access$14500;
                    if (access$14500 != null) {
                        access$14500.x = AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 32));
                        this.nameLayout.y = AndroidUtilities.dp(this.dateLayout != null ? 10.0f : 19.0f);
                    }
                    if (this.currentBlock.date != 0) {
                        this.dateLayout = ArticleViewer.this.createLayoutForText(this, LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.date) * 1000), (TLRPC.RichText) null, width - AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 50)), AndroidUtilities.dp(29.0f), this.currentBlock, this.parentAdapter);
                    } else {
                        this.dateLayout = null;
                    }
                    int height4 = AndroidUtilities.dp(56.0f);
                    if (this.currentBlock.blocks.isEmpty()) {
                        this.textX = AndroidUtilities.dp(32.0f);
                        this.textY = AndroidUtilities.dp(56.0f);
                        int textWidth2 = width - AndroidUtilities.dp(50.0f);
                        DrawingText access$135002 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.text, textWidth2, this.textY, this.currentBlock, this.parentAdapter);
                        this.captionLayout = access$135002;
                        if (access$135002 != null) {
                            int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                            this.creditOffset = dp2;
                            height3 = height4 + dp2 + AndroidUtilities.dp(4.0f);
                        } else {
                            height3 = height4;
                        }
                        DrawingText access$136002 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.credit, textWidth2, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                        this.creditLayout = access$136002;
                        if (access$136002 != null) {
                            height3 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                        }
                        height2 = height3;
                    } else {
                        this.captionLayout = null;
                        this.creditLayout = null;
                        height2 = height4;
                    }
                    DrawingText drawingText = this.dateLayout;
                    if (drawingText != null) {
                        if (this.avatarVisible) {
                            i = 54;
                        }
                        drawingText.x = AndroidUtilities.dp((float) (i + 32));
                        this.dateLayout.y = AndroidUtilities.dp(29.0f);
                    }
                    DrawingText drawingText2 = this.captionLayout;
                    if (drawingText2 != null) {
                        drawingText2.x = this.textX;
                        this.captionLayout.y = this.textY;
                    }
                    DrawingText drawingText3 = this.creditLayout;
                    if (drawingText3 != null) {
                        drawingText3.x = this.textX;
                        this.creditLayout.y = this.textY;
                    }
                }
                this.lineHeight = height;
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            TLRPC.TL_pageBlockEmbedPost tL_pageBlockEmbedPost = this.currentBlock;
            if (tL_pageBlockEmbedPost != null) {
                int count = 0;
                if (!(tL_pageBlockEmbedPost instanceof TL_pageBlockEmbedPostCaption)) {
                    if (this.avatarVisible) {
                        this.avatarImageView.draw(canvas);
                    }
                    int i = 54;
                    int i2 = 0;
                    if (this.nameLayout != null) {
                        canvas.save();
                        canvas.translate((float) AndroidUtilities.dp((float) ((this.avatarVisible ? 54 : 0) + 32)), (float) AndroidUtilities.dp(this.dateLayout != null ? 10.0f : 19.0f));
                        ArticleViewer.this.drawTextSelection(canvas, this, 0);
                        this.nameLayout.draw(canvas, this);
                        canvas.restore();
                        count = 0 + 1;
                    }
                    if (this.dateLayout != null) {
                        canvas.save();
                        if (!this.avatarVisible) {
                            i = 0;
                        }
                        canvas.translate((float) AndroidUtilities.dp((float) (i + 32)), (float) AndroidUtilities.dp(29.0f));
                        ArticleViewer.this.drawTextSelection(canvas, this, count);
                        this.dateLayout.draw(canvas, this);
                        canvas.restore();
                        count++;
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
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, count);
                    this.captionLayout.draw(canvas, this);
                    canvas.restore();
                    count++;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, count);
                    this.creditLayout.draw(canvas, this);
                    canvas.restore();
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.nameLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.dateLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
            DrawingText drawingText3 = this.captionLayout;
            if (drawingText3 != null) {
                blocks.add(drawingText3);
            }
            DrawingText drawingText4 = this.creditLayout;
            if (drawingText4 != null) {
                blocks.add(drawingText4);
            }
        }
    }

    public class BlockParagraphCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockParagraph currentBlock;
        private WebpageAdapter parentAdapter;
        public DrawingText textLayout;
        public int textX;
        public int textY;

        public BlockParagraphCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockParagraph block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockParagraph tL_pageBlockParagraph = this.currentBlock;
            if (tL_pageBlockParagraph != null) {
                if (tL_pageBlockParagraph.level == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 18));
                }
                DrawingText access$14500 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.text, (width - AndroidUtilities.dp(18.0f)) - this.textX, this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, 0, this.parentAdapter);
                this.textLayout = access$14500;
                if (access$14500 != null) {
                    int height2 = access$14500.getHeight();
                    if (this.currentBlock.level > 0) {
                        height = height2 + AndroidUtilities.dp(8.0f);
                    } else {
                        height = height2 + AndroidUtilities.dp(16.0f);
                    }
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this);
                    this.textLayout.draw(canvas, this);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                info.setText(drawingText.getText());
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    private class BlockEmbedCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockEmbed currentBlock;
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
            public void postEvent(String eventName, String eventData) {
                AndroidUtilities.runOnUIThread(new ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$$ExternalSyntheticLambda0(this, eventName, eventData));
            }

            /* renamed from: lambda$postEvent$0$org-telegram-ui-ArticleViewer$BlockEmbedCell$TelegramWebviewProxy  reason: not valid java name */
            public /* synthetic */ void m2711x9281b05f(String eventName, String eventData) {
                if ("resize_frame".equals(eventName)) {
                    try {
                        int unused = BlockEmbedCell.this.exactWebViewHeight = Utilities.parseInt((CharSequence) new JSONObject(eventData).getString("height")).intValue();
                        BlockEmbedCell.this.requestLayout();
                    } catch (Throwable th) {
                    }
                }
            }
        }

        public class TouchyWebView extends WebView {
            public TouchyWebView(Context context) {
                super(context);
                setFocusable(false);
            }

            public boolean onTouchEvent(MotionEvent event) {
                boolean unused = BlockEmbedCell.this.wasUserInteraction = true;
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

        public BlockEmbedCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            WebPlayerView webPlayerView = new WebPlayerView(context, false, false, new WebPlayerView.WebPlayerViewDelegate(ArticleViewer.this) {
                public void onInitFailed() {
                    BlockEmbedCell.this.webView.setVisibility(0);
                    BlockEmbedCell.this.videoView.setVisibility(4);
                    BlockEmbedCell.this.videoView.loadVideo((String) null, (TLRPC.Photo) null, (Object) null, (String) null, false);
                    HashMap<String, String> args = new HashMap<>();
                    args.put("Referer", ApplicationLoader.applicationContext.getPackageName());
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
                        WebPlayerView unused = ArticleViewer.this.fullscreenedVideo = BlockEmbedCell.this.videoView;
                        ArticleViewer.this.fullscreenVideoContainer.addView(controlsView, LayoutHelper.createFrame(-1, -1.0f));
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(0);
                    } else {
                        ArticleViewer.this.fullscreenAspectRatioView.removeView(ArticleViewer.this.fullscreenTextureView);
                        WebPlayerView unused2 = ArticleViewer.this.fullscreenedVideo = null;
                        ArticleViewer.this.fullscreenAspectRatioView.setVisibility(8);
                        ArticleViewer.this.fullscreenVideoContainer.setVisibility(4);
                    }
                    return ArticleViewer.this.fullscreenTextureView;
                }

                public void prepareToSwitchInlineMode(boolean inline, Runnable switchInlineModeRunnable, float aspectRatio, boolean animated) {
                }

                public TextureView onSwitchInlineMode(View controlsView, boolean inline, int videoWidth, int videoHeight, int rotation, boolean animated) {
                    return null;
                }

                public void onSharePressed() {
                    if (ArticleViewer.this.parentActivity != null) {
                        ArticleViewer.this.showDialog(new ShareAlert(ArticleViewer.this.parentActivity, (ArrayList<MessageObject>) null, BlockEmbedCell.this.currentBlock.url, false, BlockEmbedCell.this.currentBlock.url, false));
                    }
                }

                public void onPlayStateChanged(WebPlayerView playerView, boolean playing) {
                    if (playing) {
                        if (!(ArticleViewer.this.currentPlayingVideo == null || ArticleViewer.this.currentPlayingVideo == playerView)) {
                            ArticleViewer.this.currentPlayingVideo.pause();
                        }
                        WebPlayerView unused = ArticleViewer.this.currentPlayingVideo = playerView;
                        try {
                            ArticleViewer.this.parentActivity.getWindow().addFlags(128);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else {
                        if (ArticleViewer.this.currentPlayingVideo == playerView) {
                            WebPlayerView unused2 = ArticleViewer.this.currentPlayingVideo = null;
                        }
                        try {
                            ArticleViewer.this.parentActivity.getWindow().clearFlags(128);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    }
                }

                public boolean checkInlinePermissions() {
                    return false;
                }

                public ViewGroup getTextureViewContainer() {
                    return null;
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
            if (Build.VERSION.SDK_INT >= 17) {
                this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
                this.webView.addJavascriptInterface(new TelegramWebviewProxy(), "TelegramWebviewProxy");
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.webView.getSettings().setMixedContentMode(0);
                CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
            }
            this.webView.setWebChromeClient(new WebChromeClient(ArticleViewer.this) {
                public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
                    onShowCustomView(view, callback);
                }

                public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
                    if (ArticleViewer.this.customView != null) {
                        callback.onCustomViewHidden();
                        return;
                    }
                    View unused = ArticleViewer.this.customView = view;
                    WebChromeClient.CustomViewCallback unused2 = ArticleViewer.this.customViewCallback = callback;
                    AndroidUtilities.runOnUIThread(new ArticleViewer$BlockEmbedCell$2$$ExternalSyntheticLambda0(this), 100);
                }

                /* renamed from: lambda$onShowCustomView$0$org-telegram-ui-ArticleViewer$BlockEmbedCell$2  reason: not valid java name */
                public /* synthetic */ void m2710x5355var_() {
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
                    Browser.openUrl((Context) ArticleViewer.this.parentActivity, url);
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
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.videoView.destroy();
        }

        public void setBlock(TLRPC.TL_pageBlockEmbed block) {
            TLRPC.TL_pageBlockEmbed previousBlock = this.currentBlock;
            this.currentBlock = block;
            this.webView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            TLRPC.TL_pageBlockEmbed tL_pageBlockEmbed = this.currentBlock;
            if (previousBlock != tL_pageBlockEmbed) {
                this.wasUserInteraction = false;
                if (tL_pageBlockEmbed.allow_scrolling) {
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
                    if (this.currentBlock.html != null) {
                        this.webView.loadDataWithBaseURL("https://telegram.org/embed", this.currentBlock.html, "text/html", "UTF-8", (String) null);
                        this.videoView.setVisibility(4);
                        this.videoView.loadVideo((String) null, (TLRPC.Photo) null, (Object) null, (String) null, false);
                        this.webView.setVisibility(0);
                    } else {
                        if (this.videoView.loadVideo(block.url, this.currentBlock.poster_photo_id != 0 ? this.parentAdapter.getPhotoWithId(this.currentBlock.poster_photo_id) : null, this.parentAdapter.currentPage, (String) null, false)) {
                            this.webView.setVisibility(4);
                            this.videoView.setVisibility(0);
                            this.webView.stopLoading();
                            this.webView.loadUrl("about:blank");
                        } else {
                            this.webView.setVisibility(0);
                            this.videoView.setVisibility(4);
                            this.videoView.loadVideo((String) null, (TLRPC.Photo) null, (Object) null, (String) null, false);
                            HashMap<String, String> args = new HashMap<>();
                            args.put("Referer", ApplicationLoader.applicationContext.getPackageName());
                            this.webView.loadUrl(this.currentBlock.url, args);
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

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.captionLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int listWidth;
            int textWidth;
            float scale;
            int height2;
            int height3;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            TLRPC.TL_pageBlockEmbed tL_pageBlockEmbed = this.currentBlock;
            if (tL_pageBlockEmbed != null) {
                int listWidth2 = width;
                if (tL_pageBlockEmbed.level > 0) {
                    int dp = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                    this.listX = dp;
                    this.textX = dp;
                    int listWidth3 = listWidth2 - (dp + AndroidUtilities.dp(18.0f));
                    textWidth = listWidth3;
                    listWidth = listWidth3;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    int textWidth2 = width - AndroidUtilities.dp(36.0f);
                    if (!this.currentBlock.full_width) {
                        int listWidth4 = listWidth2 - AndroidUtilities.dp(36.0f);
                        this.listX += AndroidUtilities.dp(18.0f);
                        listWidth = listWidth4;
                        textWidth = textWidth2;
                    } else {
                        listWidth = listWidth2;
                        textWidth = textWidth2;
                    }
                }
                if (this.currentBlock.w == 0) {
                    scale = 1.0f;
                } else {
                    scale = ((float) width) / ((float) this.currentBlock.w);
                }
                int i = this.exactWebViewHeight;
                if (i != 0) {
                    height2 = AndroidUtilities.dp((float) i);
                } else {
                    height2 = (int) (((float) (this.currentBlock.w == 0 ? AndroidUtilities.dp((float) this.currentBlock.h) : this.currentBlock.h)) * scale);
                }
                if (height2 == 0) {
                    height3 = AndroidUtilities.dp(10.0f);
                } else {
                    height3 = height2;
                }
                this.webView.measure(View.MeasureSpec.makeMeasureSpec(listWidth, NUM), View.MeasureSpec.makeMeasureSpec(height3, NUM));
                if (this.videoView.getParent() == this) {
                    this.videoView.measure(View.MeasureSpec.makeMeasureSpec(listWidth, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f) + height3, NUM));
                }
                this.textY = AndroidUtilities.dp(8.0f) + height3;
                DrawingText access$13500 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                this.captionLayout = access$13500;
                if (access$13500 != null) {
                    int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp2;
                    height3 += dp2 + AndroidUtilities.dp(4.0f);
                } else {
                    this.creditOffset = 0;
                }
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = access$13600;
                if (access$13600 != null) {
                    height3 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    this.creditLayout.x = this.textX;
                    this.creditLayout.y = this.creditOffset;
                }
                height = height3 + AndroidUtilities.dp(5.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.dp(8.0f);
                } else if (this.currentBlock.level == 0 && this.captionLayout != null) {
                    height += AndroidUtilities.dp(8.0f);
                }
                DrawingText drawingText = this.captionLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    this.captionLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            TouchyWebView touchyWebView = this.webView;
            int i = this.listX;
            touchyWebView.layout(i, 0, touchyWebView.getMeasuredWidth() + i, this.webView.getMeasuredHeight());
            if (this.videoView.getParent() == this) {
                WebPlayerView webPlayerView = this.videoView;
                int i2 = this.listX;
                webPlayerView.layout(i2, 0, webPlayerView.getMeasuredWidth() + i2, this.videoView.getMeasuredHeight());
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int count = 0;
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.captionLayout.draw(canvas, this);
                    canvas.restore();
                    count = 0 + 1;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, count);
                    this.creditLayout.draw(canvas, this);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    public class BlockTableCell extends FrameLayout implements TableLayout.TableLayoutDelegate, TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockTable currentBlock;
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

        public BlockTableCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            AnonymousClass1 r0 = new HorizontalScrollView(context, ArticleViewer.this) {
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    boolean intercept = super.onInterceptTouchEvent(ev);
                    if (BlockTableCell.this.tableLayout.getMeasuredWidth() > getMeasuredWidth() - AndroidUtilities.dp(36.0f) && intercept) {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                    return intercept;
                }

                public boolean onTouchEvent(MotionEvent ev) {
                    if (BlockTableCell.this.tableLayout.getMeasuredWidth() <= getMeasuredWidth() - AndroidUtilities.dp(36.0f)) {
                        return false;
                    }
                    return super.onTouchEvent(ev);
                }

                /* access modifiers changed from: protected */
                public void onScrollChanged(int l, int t, int oldl, int oldt) {
                    super.onScrollChanged(l, t, oldl, oldt);
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null) {
                        DrawingText unused = ArticleViewer.this.pressedLinkOwnerLayout = null;
                        View unused2 = ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                    BlockTableCell.this.updateChildTextPositions();
                    if (ArticleViewer.this.textSelectionHelper != null && ArticleViewer.this.textSelectionHelper.isSelectionMode()) {
                        ArticleViewer.this.textSelectionHelper.invalidate();
                    }
                }

                /* access modifiers changed from: protected */
                public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
                    ArticleViewer.this.removePressedLink();
                    return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    BlockTableCell.this.tableLayout.measure(View.MeasureSpec.makeMeasureSpec((View.MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()) - getPaddingRight(), 0), heightMeasureSpec);
                    setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), BlockTableCell.this.tableLayout.getMeasuredHeight());
                }
            };
            this.scrollView = r0;
            r0.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.scrollView.setClipToPadding(false);
            addView(this.scrollView, LayoutHelper.createFrame(-1, -2.0f));
            TableLayout tableLayout2 = new TableLayout(context, this, ArticleViewer.this.textSelectionHelper);
            this.tableLayout = tableLayout2;
            tableLayout2.setOrientation(0);
            this.tableLayout.setRowOrderPreserved(true);
            this.scrollView.addView(this.tableLayout, new FrameLayout.LayoutParams(-2, -2));
            setWillNotDraw(false);
        }

        public DrawingText createTextLayout(TLRPC.TL_pageTableCell cell, int maxWidth) {
            Layout.Alignment alignment;
            if (cell == null) {
                return null;
            }
            if (cell.align_right) {
                alignment = Layout.Alignment.ALIGN_OPPOSITE;
            } else if (cell.align_center) {
                alignment = Layout.Alignment.ALIGN_CENTER;
            } else {
                alignment = Layout.Alignment.ALIGN_NORMAL;
            }
            return ArticleViewer.this.createLayoutForText(this, (CharSequence) null, cell.text, maxWidth, -1, this.currentBlock, alignment, 0, this.parentAdapter);
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

        public void onLayoutChild(DrawingText text, int x, int y) {
            if (text != null && !ArticleViewer.this.searchResults.isEmpty() && ArticleViewer.this.searchText != null) {
                String lowerString = text.textLayout.getText().toString().toLowerCase();
                int startIndex = 0;
                while (true) {
                    int indexOf = lowerString.indexOf(ArticleViewer.this.searchText, startIndex);
                    int index = indexOf;
                    if (indexOf >= 0) {
                        startIndex = index + ArticleViewer.this.searchText.length();
                        if (index == 0 || AndroidUtilities.isPunctuationCharacter(lowerString.charAt(index - 1))) {
                            HashMap access$1700 = ArticleViewer.this.adapter[0].searchTextOffset;
                            access$1700.put(ArticleViewer.this.searchText + this.currentBlock + text.parentText + index, Integer.valueOf(text.textLayout.getLineTop(text.textLayout.getLineForOffset(index)) + y));
                        }
                    } else {
                        return;
                    }
                }
            }
        }

        public void setBlock(TLRPC.TL_pageBlockTable block) {
            this.currentBlock = block;
            AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("windowBackgroundWhite"));
            this.tableLayout.removeAllChildrens();
            this.tableLayout.setDrawLines(this.currentBlock.bordered);
            this.tableLayout.setStriped(this.currentBlock.striped);
            this.tableLayout.setRtl(this.parentAdapter.isRtl);
            int maxCols = 0;
            if (!this.currentBlock.rows.isEmpty()) {
                TLRPC.TL_pageTableRow row = this.currentBlock.rows.get(0);
                int size2 = row.cells.size();
                for (int c = 0; c < size2; c++) {
                    TLRPC.TL_pageTableCell cell = row.cells.get(c);
                    maxCols += cell.colspan != 0 ? cell.colspan : 1;
                }
            }
            int size = this.currentBlock.rows.size();
            for (int r = 0; r < size; r++) {
                TLRPC.TL_pageTableRow row2 = this.currentBlock.rows.get(r);
                int cols = 0;
                int size22 = row2.cells.size();
                for (int c2 = 0; c2 < size22; c2++) {
                    TLRPC.TL_pageTableCell cell2 = row2.cells.get(c2);
                    int colspan = cell2.colspan != 0 ? cell2.colspan : 1;
                    int rowspan = cell2.rowspan != 0 ? cell2.rowspan : 1;
                    if (cell2.text != null) {
                        this.tableLayout.addChild(cell2, cols, r, colspan);
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
                TableLayout.Child c = this.tableLayout.getChildAt(i);
                if (ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, c.textLayout, (this.scrollView.getPaddingLeft() - this.scrollView.getScrollX()) + this.listX + c.getTextX(), this.listY + c.getTextY())) {
                    return true;
                }
            }
            if (ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.titleLayout, this.textX, this.textY) || super.onTouchEvent(event)) {
                return true;
            }
            return false;
        }

        public void invalidate() {
            super.invalidate();
            this.tableLayout.invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int textWidth;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height2 = 0;
            TLRPC.TL_pageBlockTable tL_pageBlockTable = this.currentBlock;
            if (tL_pageBlockTable != null) {
                if (tL_pageBlockTable.level > 0) {
                    int dp = AndroidUtilities.dp((float) (this.currentBlock.level * 14));
                    this.listX = dp;
                    int dp2 = dp + AndroidUtilities.dp(18.0f);
                    this.textX = dp2;
                    textWidth = width - dp2;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                }
                DrawingText access$14500 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.title, textWidth, 0, this.currentBlock, Layout.Alignment.ALIGN_CENTER, 0, this.parentAdapter);
                this.titleLayout = access$14500;
                if (access$14500 != null) {
                    this.textY = 0;
                    height2 = 0 + access$14500.getHeight() + AndroidUtilities.dp(8.0f);
                    this.listY = height2;
                    this.titleLayout.x = this.textX;
                    this.titleLayout.y = this.textY;
                } else {
                    this.listY = AndroidUtilities.dp(8.0f);
                }
                this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(width - this.listX, NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                height = height2 + this.scrollView.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            updateChildTextPositions();
        }

        /* access modifiers changed from: private */
        public void updateChildTextPositions() {
            int count = this.titleLayout == null ? 0 : 1;
            int N = this.tableLayout.getChildCount();
            for (int i = 0; i < N; i++) {
                TableLayout.Child c = this.tableLayout.getChildAt(i);
                if (c.textLayout != null) {
                    c.textLayout.x = ((c.getTextX() + this.listX) + AndroidUtilities.dp(18.0f)) - this.scrollView.getScrollX();
                    c.textLayout.y = c.getTextY() + this.listY;
                    c.textLayout.row = c.getRow();
                    c.setSelectionIndex(count);
                    count++;
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            HorizontalScrollView horizontalScrollView = this.scrollView;
            int i = this.listX;
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

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int i = 0;
                if (this.titleLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.titleLayout.draw(canvas, this);
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.titleLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            int N = this.tableLayout.getChildCount();
            for (int i = 0; i < N; i++) {
                TableLayout.Child c = this.tableLayout.getChildAt(i);
                if (c.textLayout != null) {
                    blocks.add(c.textLayout);
                }
            }
        }
    }

    private class BlockCollageCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockCollage currentBlock;
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
            public long groupId;
            public boolean hasSibling;
            private int maxSizeWidth = 1000;
            public ArrayList<MessageObject.GroupedMessagePosition> posArray = new ArrayList<>();
            public HashMap<TLObject, MessageObject.GroupedMessagePosition> positions = new HashMap<>();

            public GroupedMessages() {
            }

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

            /* JADX WARNING: Code restructure failed: missing block: B:158:0x07a7, code lost:
                if (r4.lineCounts[2] > r4.lineCounts[3]) goto L_0x07ae;
             */
            /* JADX WARNING: Removed duplicated region for block: B:195:0x084e  */
            /* JADX WARNING: Removed duplicated region for block: B:233:0x0863 A[SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void calculate() {
                /*
                    r41 = this;
                    r10 = r41
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                    r0.clear()
                    java.util.HashMap<org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.positions
                    r0.clear()
                    org.telegram.ui.ArticleViewer$BlockCollageCell r0 = org.telegram.ui.ArticleViewer.BlockCollageCell.this
                    org.telegram.tgnet.TLRPC$TL_pageBlockCollage r0 = r0.currentBlock
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r0 = r0.items
                    int r11 = r0.size()
                    r12 = 1
                    if (r11 > r12) goto L_0x001c
                    return
                L_0x001c:
                    r13 = 1145798656(0x444b8000, float:814.0)
                    java.lang.StringBuilder r0 = new java.lang.StringBuilder
                    r0.<init>()
                    r14 = r0
                    r0 = 1065353216(0x3var_, float:1.0)
                    r1 = 0
                    r15 = 0
                    r10.hasSibling = r15
                    r2 = 0
                    r16 = r1
                L_0x002e:
                    r17 = 1067030938(0x3var_a, float:1.2)
                    r1 = 1073741824(0x40000000, float:2.0)
                    if (r2 >= r11) goto L_0x00d9
                    org.telegram.ui.ArticleViewer$BlockCollageCell r4 = org.telegram.ui.ArticleViewer.BlockCollageCell.this
                    org.telegram.tgnet.TLRPC$TL_pageBlockCollage r4 = r4.currentBlock
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r4 = r4.items
                    java.lang.Object r4 = r4.get(r2)
                    org.telegram.tgnet.TLObject r4 = (org.telegram.tgnet.TLObject) r4
                    boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockPhoto
                    if (r5 == 0) goto L_0x0065
                    r5 = r4
                    org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r5 = (org.telegram.tgnet.TLRPC.TL_pageBlockPhoto) r5
                    org.telegram.ui.ArticleViewer$BlockCollageCell r6 = org.telegram.ui.ArticleViewer.BlockCollageCell.this
                    org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r6.parentAdapter
                    long r7 = r5.photo_id
                    org.telegram.tgnet.TLRPC$Photo r6 = r6.getPhotoWithId(r7)
                    if (r6 != 0) goto L_0x005a
                    goto L_0x00d5
                L_0x005a:
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r6.sizes
                    int r8 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
                    org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
                    goto L_0x0084
                L_0x0065:
                    boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockVideo
                    if (r5 == 0) goto L_0x00d5
                    r5 = r4
                    org.telegram.tgnet.TLRPC$TL_pageBlockVideo r5 = (org.telegram.tgnet.TLRPC.TL_pageBlockVideo) r5
                    org.telegram.ui.ArticleViewer$BlockCollageCell r6 = org.telegram.ui.ArticleViewer.BlockCollageCell.this
                    org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r6.parentAdapter
                    long r7 = r5.video_id
                    org.telegram.tgnet.TLRPC$Document r6 = r6.getDocumentWithId(r7)
                    if (r6 != 0) goto L_0x007b
                    goto L_0x00d5
                L_0x007b:
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r6.thumbs
                    r8 = 90
                    org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
                    r5 = r7
                L_0x0084:
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r6 = new org.telegram.messenger.MessageObject$GroupedMessagePosition
                    r6.<init>()
                    int r7 = r11 + -1
                    if (r2 != r7) goto L_0x008f
                    r7 = 1
                    goto L_0x0090
                L_0x008f:
                    r7 = 0
                L_0x0090:
                    r6.last = r7
                    if (r5 != 0) goto L_0x0097
                    r3 = 1065353216(0x3var_, float:1.0)
                    goto L_0x009e
                L_0x0097:
                    int r3 = r5.w
                    float r3 = (float) r3
                    int r7 = r5.h
                    float r7 = (float) r7
                    float r3 = r3 / r7
                L_0x009e:
                    r6.aspectRatio = r3
                    float r3 = r6.aspectRatio
                    int r3 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1))
                    if (r3 <= 0) goto L_0x00ac
                    java.lang.String r3 = "w"
                    r14.append(r3)
                    goto L_0x00c0
                L_0x00ac:
                    float r3 = r6.aspectRatio
                    r7 = 1061997773(0x3f4ccccd, float:0.8)
                    int r3 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                    if (r3 >= 0) goto L_0x00bb
                    java.lang.String r3 = "n"
                    r14.append(r3)
                    goto L_0x00c0
                L_0x00bb:
                    java.lang.String r3 = "q"
                    r14.append(r3)
                L_0x00c0:
                    float r3 = r6.aspectRatio
                    float r0 = r0 + r3
                    float r3 = r6.aspectRatio
                    int r1 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
                    if (r1 <= 0) goto L_0x00cb
                    r16 = 1
                L_0x00cb:
                    java.util.HashMap<org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r10.positions
                    r1.put(r4, r6)
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                    r1.add(r6)
                L_0x00d5:
                    int r2 = r2 + 1
                    goto L_0x002e
                L_0x00d9:
                    r2 = 1123024896(0x42var_, float:120.0)
                    int r9 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    float r2 = (float) r2
                    android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r4 = r4.x
                    android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r5 = r5.y
                    int r4 = java.lang.Math.min(r4, r5)
                    float r4 = (float) r4
                    int r5 = r10.maxSizeWidth
                    float r5 = (float) r5
                    float r4 = r4 / r5
                    float r2 = r2 / r4
                    int r8 = (int) r2
                    r2 = 1109393408(0x42200000, float:40.0)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    float r2 = (float) r2
                    android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r4 = r4.x
                    android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r5 = r5.y
                    int r4 = java.lang.Math.min(r4, r5)
                    float r4 = (float) r4
                    int r5 = r10.maxSizeWidth
                    float r6 = (float) r5
                    float r4 = r4 / r6
                    float r2 = r2 / r4
                    int r7 = (int) r2
                    float r2 = (float) r5
                    float r6 = r2 / r13
                    float r2 = (float) r11
                    float r5 = r0 / r2
                    r4 = 4
                    r2 = 2
                    r0 = 3
                    if (r16 != 0) goto L_0x0564
                    if (r11 == r2) goto L_0x012d
                    if (r11 == r0) goto L_0x012d
                    if (r11 != r4) goto L_0x0123
                    goto L_0x012d
                L_0x0123:
                    r25 = r5
                    r26 = r6
                    r21 = r14
                    r19 = 2
                    goto L_0x056c
                L_0x012d:
                    r17 = 1053609165(0x3ecccccd, float:0.4)
                    if (r11 != r2) goto L_0x0273
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                    java.lang.Object r0 = r0.get(r15)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r0
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r4 = r10.posArray
                    java.lang.Object r4 = r4.get(r12)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4
                    java.lang.String r15 = r14.toString()
                    java.lang.String r12 = "ww"
                    boolean r18 = r15.equals(r12)
                    if (r18 == 0) goto L_0x01b5
                    double r2 = (double) r5
                    r20 = 4608983858650965606(0x3ffNUM, double:1.4)
                    r23 = r2
                    double r1 = (double) r6
                    java.lang.Double.isNaN(r1)
                    double r1 = r1 * r20
                    int r3 = (r23 > r1 ? 1 : (r23 == r1 ? 0 : -1))
                    if (r3 <= 0) goto L_0x01b5
                    float r1 = r0.aspectRatio
                    float r2 = r4.aspectRatio
                    float r1 = r1 - r2
                    double r1 = (double) r1
                    r20 = 4596373779694328218(0x3fCLASSNAMEa, double:0.2)
                    int r3 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1))
                    if (r3 >= 0) goto L_0x01b5
                    int r1 = r10.maxSizeWidth
                    float r1 = (float) r1
                    float r2 = r0.aspectRatio
                    float r1 = r1 / r2
                    int r2 = r10.maxSizeWidth
                    float r2 = (float) r2
                    float r3 = r4.aspectRatio
                    float r2 = r2 / r3
                    r3 = 1073741824(0x40000000, float:2.0)
                    float r3 = r13 / r3
                    float r2 = java.lang.Math.min(r2, r3)
                    float r1 = java.lang.Math.min(r1, r2)
                    int r1 = java.lang.Math.round(r1)
                    float r1 = (float) r1
                    float r1 = r1 / r13
                    r19 = 0
                    r20 = 0
                    r21 = 0
                    r22 = 0
                    int r2 = r10.maxSizeWidth
                    r25 = 7
                    r18 = r0
                    r23 = r2
                    r24 = r1
                    r18.set(r19, r20, r21, r22, r23, r24, r25)
                    r21 = 1
                    r22 = 1
                    int r2 = r10.maxSizeWidth
                    r25 = 11
                    r18 = r4
                    r23 = r2
                    r18.set(r19, r20, r21, r22, r23, r24, r25)
                    r26 = r6
                    goto L_0x0262
                L_0x01b5:
                    boolean r1 = r15.equals(r12)
                    if (r1 != 0) goto L_0x0227
                    java.lang.String r1 = "qq"
                    boolean r1 = r15.equals(r1)
                    if (r1 == 0) goto L_0x01c6
                    r26 = r6
                    goto L_0x0229
                L_0x01c6:
                    int r1 = r10.maxSizeWidth
                    float r2 = (float) r1
                    float r2 = r2 * r17
                    float r1 = (float) r1
                    float r3 = r0.aspectRatio
                    float r1 = r1 / r3
                    float r3 = r0.aspectRatio
                    r12 = 1065353216(0x3var_, float:1.0)
                    float r3 = r12 / r3
                    r26 = r6
                    float r6 = r4.aspectRatio
                    float r6 = r12 / r6
                    float r3 = r3 + r6
                    float r1 = r1 / r3
                    int r1 = java.lang.Math.round(r1)
                    float r1 = (float) r1
                    float r1 = java.lang.Math.max(r2, r1)
                    int r1 = (int) r1
                    int r2 = r10.maxSizeWidth
                    int r2 = r2 - r1
                    if (r2 >= r8) goto L_0x01f0
                    int r3 = r8 - r2
                    r2 = r8
                    int r1 = r1 - r3
                L_0x01f0:
                    float r3 = (float) r2
                    float r6 = r0.aspectRatio
                    float r3 = r3 / r6
                    float r6 = (float) r1
                    float r12 = r4.aspectRatio
                    float r6 = r6 / r12
                    float r3 = java.lang.Math.min(r3, r6)
                    int r3 = java.lang.Math.round(r3)
                    float r3 = (float) r3
                    float r3 = java.lang.Math.min(r13, r3)
                    float r3 = r3 / r13
                    r19 = 0
                    r20 = 0
                    r21 = 0
                    r22 = 0
                    r25 = 13
                    r18 = r0
                    r23 = r2
                    r24 = r3
                    r18.set(r19, r20, r21, r22, r23, r24, r25)
                    r19 = 1
                    r20 = 1
                    r25 = 14
                    r18 = r4
                    r23 = r1
                    r18.set(r19, r20, r21, r22, r23, r24, r25)
                    goto L_0x0262
                L_0x0227:
                    r26 = r6
                L_0x0229:
                    int r1 = r10.maxSizeWidth
                    r2 = 2
                    int r1 = r1 / r2
                    float r2 = (float) r1
                    float r3 = r0.aspectRatio
                    float r2 = r2 / r3
                    float r3 = (float) r1
                    float r6 = r4.aspectRatio
                    float r3 = r3 / r6
                    float r3 = java.lang.Math.min(r3, r13)
                    float r2 = java.lang.Math.min(r2, r3)
                    int r2 = java.lang.Math.round(r2)
                    float r2 = (float) r2
                    float r2 = r2 / r13
                    r19 = 0
                    r20 = 0
                    r21 = 0
                    r22 = 0
                    r25 = 13
                    r18 = r0
                    r23 = r1
                    r24 = r2
                    r18.set(r19, r20, r21, r22, r23, r24, r25)
                    r19 = 1
                    r20 = 1
                    r25 = 14
                    r18 = r4
                    r18.set(r19, r20, r21, r22, r23, r24, r25)
                L_0x0262:
                    r18 = r5
                    r27 = r7
                    r12 = r8
                    r30 = r9
                    r23 = r11
                    r32 = r13
                    r21 = r14
                    r22 = r26
                    goto L_0x0849
                L_0x0273:
                    r26 = r6
                    r1 = 1059648963(0x3var_f5c3, float:0.66)
                    if (r11 != r0) goto L_0x03b9
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                    java.lang.Object r0 = r0.get(r15)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r0 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r0
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                    r3 = 1
                    java.lang.Object r2 = r2.get(r3)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r10.posArray
                    r4 = 2
                    java.lang.Object r3 = r3.get(r4)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                    char r4 = r14.charAt(r15)
                    r6 = 110(0x6e, float:1.54E-43)
                    if (r4 != r6) goto L_0x033f
                    r1 = 1056964608(0x3var_, float:0.5)
                    float r4 = r13 * r1
                    float r6 = r2.aspectRatio
                    int r12 = r10.maxSizeWidth
                    float r12 = (float) r12
                    float r6 = r6 * r12
                    float r12 = r3.aspectRatio
                    float r15 = r2.aspectRatio
                    float r12 = r12 + r15
                    float r6 = r6 / r12
                    int r6 = java.lang.Math.round(r6)
                    float r6 = (float) r6
                    float r4 = java.lang.Math.min(r4, r6)
                    float r6 = r13 - r4
                    float r12 = (float) r8
                    int r15 = r10.maxSizeWidth
                    float r15 = (float) r15
                    float r15 = r15 * r1
                    float r1 = r3.aspectRatio
                    float r1 = r1 * r4
                    r25 = r5
                    float r5 = r2.aspectRatio
                    float r5 = r5 * r6
                    float r1 = java.lang.Math.min(r1, r5)
                    int r1 = java.lang.Math.round(r1)
                    float r1 = (float) r1
                    float r1 = java.lang.Math.min(r15, r1)
                    float r1 = java.lang.Math.max(r12, r1)
                    int r1 = (int) r1
                    float r5 = r0.aspectRatio
                    float r5 = r5 * r13
                    float r12 = (float) r7
                    float r5 = r5 + r12
                    int r12 = r10.maxSizeWidth
                    int r12 = r12 - r1
                    float r12 = (float) r12
                    float r5 = java.lang.Math.min(r5, r12)
                    int r5 = java.lang.Math.round(r5)
                    r28 = 0
                    r29 = 0
                    r30 = 0
                    r31 = 1
                    r33 = 1065353216(0x3var_, float:1.0)
                    r34 = 13
                    r27 = r0
                    r32 = r5
                    r27.set(r28, r29, r30, r31, r32, r33, r34)
                    r28 = 1
                    r29 = 1
                    r31 = 0
                    float r33 = r6 / r13
                    r34 = 6
                    r27 = r2
                    r32 = r1
                    r27.set(r28, r29, r30, r31, r32, r33, r34)
                    r28 = 0
                    r30 = 1
                    r31 = 1
                    float r33 = r4 / r13
                    r34 = 10
                    r27 = r3
                    r27.set(r28, r29, r30, r31, r32, r33, r34)
                    int r12 = r10.maxSizeWidth
                    r3.spanSize = r12
                    r12 = 2
                    float[] r12 = new float[r12]
                    float r15 = r4 / r13
                    r17 = 0
                    r12[r17] = r15
                    float r15 = r6 / r13
                    r17 = r1
                    r1 = 1
                    r12[r1] = r15
                    r0.siblingHeights = r12
                    int r12 = r10.maxSizeWidth
                    int r12 = r12 - r5
                    r2.spanSize = r12
                    r3.leftSpanOffset = r5
                    r10.hasSibling = r1
                    goto L_0x03a8
                L_0x033f:
                    r25 = r5
                    int r4 = r10.maxSizeWidth
                    float r4 = (float) r4
                    float r5 = r0.aspectRatio
                    float r4 = r4 / r5
                    float r1 = r1 * r13
                    float r1 = java.lang.Math.min(r4, r1)
                    int r1 = java.lang.Math.round(r1)
                    float r1 = (float) r1
                    float r1 = r1 / r13
                    r28 = 0
                    r29 = 1
                    r30 = 0
                    r31 = 0
                    int r4 = r10.maxSizeWidth
                    r34 = 7
                    r27 = r0
                    r32 = r4
                    r33 = r1
                    r27.set(r28, r29, r30, r31, r32, r33, r34)
                    int r4 = r10.maxSizeWidth
                    r5 = 2
                    int r4 = r4 / r5
                    float r5 = r13 - r1
                    float r6 = (float) r4
                    float r12 = r2.aspectRatio
                    float r6 = r6 / r12
                    float r12 = (float) r4
                    float r15 = r3.aspectRatio
                    float r12 = r12 / r15
                    float r6 = java.lang.Math.min(r6, r12)
                    int r6 = java.lang.Math.round(r6)
                    float r6 = (float) r6
                    float r5 = java.lang.Math.min(r5, r6)
                    float r5 = r5 / r13
                    r29 = 0
                    r30 = 1
                    r31 = 1
                    r34 = 9
                    r27 = r2
                    r32 = r4
                    r33 = r5
                    r27.set(r28, r29, r30, r31, r32, r33, r34)
                    r18 = 1
                    r19 = 1
                    r20 = 1
                    r21 = 1
                    r24 = 10
                    r17 = r3
                    r22 = r4
                    r23 = r5
                    r17.set(r18, r19, r20, r21, r22, r23, r24)
                L_0x03a8:
                    r27 = r7
                    r12 = r8
                    r30 = r9
                    r23 = r11
                    r32 = r13
                    r21 = r14
                    r18 = r25
                    r22 = r26
                    goto L_0x0849
                L_0x03b9:
                    r25 = r5
                    if (r11 != r4) goto L_0x0553
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                    r3 = 0
                    java.lang.Object r2 = r2.get(r3)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r10.posArray
                    r4 = 1
                    java.lang.Object r3 = r3.get(r4)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r4 = r10.posArray
                    r5 = 2
                    java.lang.Object r4 = r4.get(r5)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r5 = r10.posArray
                    java.lang.Object r5 = r5.get(r0)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r5
                    r6 = 0
                    char r12 = r14.charAt(r6)
                    r6 = 119(0x77, float:1.67E-43)
                    if (r12 != r6) goto L_0x0484
                    int r0 = r10.maxSizeWidth
                    float r0 = (float) r0
                    float r6 = r2.aspectRatio
                    float r0 = r0 / r6
                    float r1 = r1 * r13
                    float r0 = java.lang.Math.min(r0, r1)
                    int r0 = java.lang.Math.round(r0)
                    float r0 = (float) r0
                    float r0 = r0 / r13
                    r28 = 0
                    r29 = 2
                    r30 = 0
                    r31 = 0
                    int r1 = r10.maxSizeWidth
                    r34 = 7
                    r27 = r2
                    r32 = r1
                    r33 = r0
                    r27.set(r28, r29, r30, r31, r32, r33, r34)
                    int r1 = r10.maxSizeWidth
                    float r1 = (float) r1
                    float r6 = r3.aspectRatio
                    float r12 = r4.aspectRatio
                    float r6 = r6 + r12
                    float r12 = r5.aspectRatio
                    float r6 = r6 + r12
                    float r1 = r1 / r6
                    int r1 = java.lang.Math.round(r1)
                    float r1 = (float) r1
                    float r6 = (float) r8
                    int r12 = r10.maxSizeWidth
                    float r12 = (float) r12
                    float r12 = r12 * r17
                    float r15 = r3.aspectRatio
                    float r15 = r15 * r1
                    float r12 = java.lang.Math.min(r12, r15)
                    float r6 = java.lang.Math.max(r6, r12)
                    int r6 = (int) r6
                    float r12 = (float) r8
                    int r15 = r10.maxSizeWidth
                    float r15 = (float) r15
                    r17 = 1051260355(0x3ea8f5c3, float:0.33)
                    float r15 = r15 * r17
                    float r12 = java.lang.Math.max(r12, r15)
                    float r15 = r5.aspectRatio
                    float r15 = r15 * r1
                    float r12 = java.lang.Math.max(r12, r15)
                    int r12 = (int) r12
                    int r15 = r10.maxSizeWidth
                    int r15 = r15 - r6
                    int r15 = r15 - r12
                    r21 = r14
                    float r14 = r13 - r0
                    float r1 = java.lang.Math.min(r14, r1)
                    float r1 = r1 / r13
                    r29 = 0
                    r30 = 1
                    r31 = 1
                    r34 = 9
                    r27 = r3
                    r32 = r6
                    r33 = r1
                    r27.set(r28, r29, r30, r31, r32, r33, r34)
                    r28 = 1
                    r29 = 1
                    r34 = 8
                    r27 = r4
                    r32 = r15
                    r27.set(r28, r29, r30, r31, r32, r33, r34)
                    r28 = 2
                    r29 = 2
                    r34 = 10
                    r27 = r5
                    r32 = r12
                    r27.set(r28, r29, r30, r31, r32, r33, r34)
                    goto L_0x0544
                L_0x0484:
                    r21 = r14
                    float r1 = r3.aspectRatio
                    r6 = 1065353216(0x3var_, float:1.0)
                    float r1 = r6 / r1
                    float r12 = r4.aspectRatio
                    float r12 = r6 / r12
                    float r1 = r1 + r12
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r12 = r10.posArray
                    java.lang.Object r12 = r12.get(r0)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r12 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r12
                    float r12 = r12.aspectRatio
                    float r12 = r6 / r12
                    float r1 = r1 + r12
                    float r1 = r13 / r1
                    int r1 = java.lang.Math.round(r1)
                    int r1 = java.lang.Math.max(r8, r1)
                    float r6 = (float) r9
                    float r12 = (float) r1
                    float r14 = r3.aspectRatio
                    float r12 = r12 / r14
                    float r6 = java.lang.Math.max(r6, r12)
                    float r6 = r6 / r13
                    r12 = 1051260355(0x3ea8f5c3, float:0.33)
                    float r6 = java.lang.Math.min(r12, r6)
                    float r14 = (float) r9
                    float r15 = (float) r1
                    float r0 = r4.aspectRatio
                    float r15 = r15 / r0
                    float r0 = java.lang.Math.max(r14, r15)
                    float r0 = r0 / r13
                    float r0 = java.lang.Math.min(r12, r0)
                    r12 = 1065353216(0x3var_, float:1.0)
                    float r12 = r12 - r6
                    float r12 = r12 - r0
                    float r14 = r2.aspectRatio
                    float r14 = r14 * r13
                    float r15 = (float) r7
                    float r14 = r14 + r15
                    int r15 = r10.maxSizeWidth
                    int r15 = r15 - r1
                    float r15 = (float) r15
                    float r14 = java.lang.Math.min(r14, r15)
                    int r14 = java.lang.Math.round(r14)
                    r28 = 0
                    r29 = 0
                    r30 = 0
                    r31 = 2
                    float r15 = r6 + r0
                    float r33 = r15 + r12
                    r34 = 13
                    r27 = r2
                    r32 = r14
                    r27.set(r28, r29, r30, r31, r32, r33, r34)
                    r28 = 1
                    r29 = 1
                    r31 = 0
                    r34 = 6
                    r27 = r3
                    r32 = r1
                    r33 = r6
                    r27.set(r28, r29, r30, r31, r32, r33, r34)
                    r28 = 0
                    r30 = 1
                    r31 = 1
                    r34 = 2
                    r27 = r4
                    r33 = r0
                    r27.set(r28, r29, r30, r31, r32, r33, r34)
                    int r15 = r10.maxSizeWidth
                    r4.spanSize = r15
                    r30 = 2
                    r31 = 2
                    r34 = 10
                    r27 = r5
                    r33 = r12
                    r27.set(r28, r29, r30, r31, r32, r33, r34)
                    int r15 = r10.maxSizeWidth
                    r5.spanSize = r15
                    int r15 = r10.maxSizeWidth
                    int r15 = r15 - r14
                    r3.spanSize = r15
                    r4.leftSpanOffset = r14
                    r5.leftSpanOffset = r14
                    r15 = 3
                    float[] r15 = new float[r15]
                    r17 = 0
                    r15[r17] = r6
                    r17 = r1
                    r1 = 1
                    r15[r1] = r0
                    r19 = 2
                    r15[r19] = r12
                    r2.siblingHeights = r15
                    r10.hasSibling = r1
                L_0x0544:
                    r27 = r7
                    r12 = r8
                    r30 = r9
                    r23 = r11
                    r32 = r13
                    r18 = r25
                    r22 = r26
                    goto L_0x0849
                L_0x0553:
                    r21 = r14
                    r27 = r7
                    r12 = r8
                    r30 = r9
                    r23 = r11
                    r32 = r13
                    r18 = r25
                    r22 = r26
                    goto L_0x0849
                L_0x0564:
                    r25 = r5
                    r26 = r6
                    r21 = r14
                    r19 = 2
                L_0x056c:
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r0 = r10.posArray
                    int r0 = r0.size()
                    float[] r12 = new float[r0]
                    r0 = 0
                L_0x0575:
                    if (r0 >= r11) goto L_0x05b8
                    r1 = 1066192077(0x3f8ccccd, float:1.1)
                    int r1 = (r25 > r1 ? 1 : (r25 == r1 ? 0 : -1))
                    if (r1 <= 0) goto L_0x0591
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                    java.lang.Object r1 = r1.get(r0)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1
                    float r1 = r1.aspectRatio
                    r2 = 1065353216(0x3var_, float:1.0)
                    float r1 = java.lang.Math.max(r2, r1)
                    r12[r0] = r1
                    goto L_0x05a3
                L_0x0591:
                    r2 = 1065353216(0x3var_, float:1.0)
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r10.posArray
                    java.lang.Object r1 = r1.get(r0)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1
                    float r1 = r1.aspectRatio
                    float r1 = java.lang.Math.min(r2, r1)
                    r12[r0] = r1
                L_0x05a3:
                    r1 = 1059760867(0x3f2aaae3, float:0.66667)
                    r3 = 1071225242(0x3fd9999a, float:1.7)
                    r5 = r12[r0]
                    float r3 = java.lang.Math.min(r3, r5)
                    float r1 = java.lang.Math.max(r1, r3)
                    r12[r0] = r1
                    int r0 = r0 + 1
                    goto L_0x0575
                L_0x05b8:
                    java.util.ArrayList r0 = new java.util.ArrayList
                    r0.<init>()
                    r14 = r0
                    r0 = 1
                    r6 = r0
                L_0x05c0:
                    int r0 = r12.length
                    if (r6 >= r0) goto L_0x0608
                    int r0 = r12.length
                    int r15 = r0 - r6
                    r0 = 3
                    if (r6 > r0) goto L_0x05f8
                    if (r15 <= r0) goto L_0x05d2
                    r23 = r11
                    r18 = r25
                    r24 = 4
                    goto L_0x05fe
                L_0x05d2:
                    org.telegram.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt r5 = new org.telegram.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt
                    r1 = 0
                    float r18 = r10.multiHeight(r12, r1, r6)
                    int r1 = r12.length
                    float r22 = r10.multiHeight(r12, r6, r1)
                    r3 = 3
                    r0 = r5
                    r1 = r41
                    r23 = r11
                    r11 = 2
                    r2 = r6
                    r11 = 3
                    r3 = r15
                    r24 = 4
                    r4 = r18
                    r11 = r5
                    r18 = r25
                    r5 = r22
                    r0.<init>(r2, r3, r4, r5)
                    r14.add(r11)
                    goto L_0x05fe
                L_0x05f8:
                    r23 = r11
                    r18 = r25
                    r24 = 4
                L_0x05fe:
                    int r6 = r6 + 1
                    r25 = r18
                    r11 = r23
                    r4 = 4
                    r19 = 2
                    goto L_0x05c0
                L_0x0608:
                    r23 = r11
                    r18 = r25
                    r24 = 4
                    r0 = 1
                    r11 = r0
                L_0x0610:
                    int r0 = r12.length
                    r1 = 1
                    int r0 = r0 - r1
                    if (r11 >= r0) goto L_0x0684
                    r0 = 1
                    r15 = r0
                L_0x0617:
                    int r0 = r12.length
                    int r0 = r0 - r11
                    if (r15 >= r0) goto L_0x067b
                    int r0 = r12.length
                    int r0 = r0 - r11
                    int r6 = r0 - r15
                    r0 = 3
                    if (r11 > r0) goto L_0x066a
                    r0 = 1062836634(0x3var_a, float:0.85)
                    int r0 = (r18 > r0 ? 1 : (r18 == r0 ? 0 : -1))
                    if (r0 >= 0) goto L_0x062b
                    r4 = 4
                    goto L_0x062c
                L_0x062b:
                    r4 = 3
                L_0x062c:
                    if (r15 > r4) goto L_0x066a
                    r0 = 3
                    if (r6 <= r0) goto L_0x063a
                    r27 = r7
                    r29 = r8
                    r22 = r26
                    r26 = r6
                    goto L_0x0672
                L_0x063a:
                    org.telegram.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt r5 = new org.telegram.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt
                    r0 = 0
                    float r22 = r10.multiHeight(r12, r0, r11)
                    int r0 = r11 + r15
                    float r27 = r10.multiHeight(r12, r11, r0)
                    int r0 = r11 + r15
                    int r1 = r12.length
                    float r28 = r10.multiHeight(r12, r0, r1)
                    r0 = r5
                    r1 = r41
                    r2 = r11
                    r3 = r15
                    r4 = r6
                    r29 = r8
                    r8 = r5
                    r5 = r22
                    r22 = r26
                    r26 = r6
                    r6 = r27
                    r27 = r7
                    r7 = r28
                    r0.<init>(r2, r3, r4, r5, r6, r7)
                    r14.add(r8)
                    goto L_0x0672
                L_0x066a:
                    r27 = r7
                    r29 = r8
                    r22 = r26
                    r26 = r6
                L_0x0672:
                    int r15 = r15 + 1
                    r26 = r22
                    r7 = r27
                    r8 = r29
                    goto L_0x0617
                L_0x067b:
                    r27 = r7
                    r29 = r8
                    r22 = r26
                    int r11 = r11 + 1
                    goto L_0x0610
                L_0x0684:
                    r27 = r7
                    r29 = r8
                    r22 = r26
                    r0 = 1
                    r11 = r0
                L_0x068c:
                    int r0 = r12.length
                    r1 = 2
                    int r0 = r0 - r1
                    if (r11 >= r0) goto L_0x0731
                    r0 = 1
                    r15 = r0
                L_0x0693:
                    int r0 = r12.length
                    int r0 = r0 - r11
                    if (r15 >= r0) goto L_0x0723
                    r0 = 1
                    r8 = r0
                L_0x0699:
                    int r0 = r12.length
                    int r0 = r0 - r11
                    int r0 = r0 - r15
                    if (r8 >= r0) goto L_0x0711
                    int r0 = r12.length
                    int r0 = r0 - r11
                    int r0 = r0 - r15
                    int r7 = r0 - r8
                    r0 = 3
                    if (r11 > r0) goto L_0x06fa
                    if (r15 > r0) goto L_0x06fa
                    if (r8 > r0) goto L_0x06fa
                    if (r7 <= r0) goto L_0x06b9
                    r26 = r7
                    r30 = r9
                    r28 = r12
                    r32 = r13
                    r12 = r29
                    r29 = r8
                    goto L_0x0706
                L_0x06b9:
                    org.telegram.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt r6 = new org.telegram.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt
                    r0 = 0
                    float r26 = r10.multiHeight(r12, r0, r11)
                    int r0 = r11 + r15
                    float r28 = r10.multiHeight(r12, r11, r0)
                    int r0 = r11 + r15
                    int r1 = r11 + r15
                    int r1 = r1 + r8
                    float r30 = r10.multiHeight(r12, r0, r1)
                    int r0 = r11 + r15
                    int r0 = r0 + r8
                    int r1 = r12.length
                    float r31 = r10.multiHeight(r12, r0, r1)
                    r0 = r6
                    r1 = r41
                    r2 = r11
                    r3 = r15
                    r4 = r8
                    r5 = r7
                    r32 = r13
                    r13 = r6
                    r6 = r26
                    r26 = r7
                    r7 = r28
                    r28 = r12
                    r12 = r29
                    r29 = r8
                    r8 = r30
                    r30 = r9
                    r9 = r31
                    r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9)
                    r14.add(r13)
                    goto L_0x0706
                L_0x06fa:
                    r26 = r7
                    r30 = r9
                    r28 = r12
                    r32 = r13
                    r12 = r29
                    r29 = r8
                L_0x0706:
                    int r8 = r29 + 1
                    r29 = r12
                    r12 = r28
                    r9 = r30
                    r13 = r32
                    goto L_0x0699
                L_0x0711:
                    r30 = r9
                    r28 = r12
                    r32 = r13
                    r12 = r29
                    r29 = r8
                    int r15 = r15 + 1
                    r29 = r12
                    r12 = r28
                    goto L_0x0693
                L_0x0723:
                    r30 = r9
                    r28 = r12
                    r32 = r13
                    r12 = r29
                    int r11 = r11 + 1
                    r12 = r28
                    goto L_0x068c
                L_0x0731:
                    r30 = r9
                    r28 = r12
                    r32 = r13
                    r12 = r29
                    r0 = 0
                    r1 = 0
                    int r2 = r10.maxSizeWidth
                    r3 = 3
                    int r2 = r2 / r3
                    int r2 = r2 * 4
                    float r2 = (float) r2
                    r3 = 0
                L_0x0743:
                    int r4 = r14.size()
                    if (r3 >= r4) goto L_0x07c8
                    java.lang.Object r4 = r14.get(r3)
                    org.telegram.ui.ArticleViewer$BlockCollageCell$GroupedMessages$MessageGroupedLayoutAttempt r4 = (org.telegram.ui.ArticleViewer.BlockCollageCell.GroupedMessages.MessageGroupedLayoutAttempt) r4
                    r5 = 0
                    r6 = 2139095039(0x7f7fffff, float:3.4028235E38)
                    r7 = 0
                L_0x0754:
                    float[] r8 = r4.heights
                    int r8 = r8.length
                    if (r7 >= r8) goto L_0x076d
                    float[] r8 = r4.heights
                    r8 = r8[r7]
                    float r5 = r5 + r8
                    float[] r8 = r4.heights
                    r8 = r8[r7]
                    int r8 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                    if (r8 >= 0) goto L_0x076a
                    float[] r8 = r4.heights
                    r6 = r8[r7]
                L_0x076a:
                    int r7 = r7 + 1
                    goto L_0x0754
                L_0x076d:
                    float r7 = r5 - r2
                    float r7 = java.lang.Math.abs(r7)
                    int[] r8 = r4.lineCounts
                    int r8 = r8.length
                    r9 = 1
                    if (r8 <= r9) goto L_0x07b1
                    int[] r8 = r4.lineCounts
                    r13 = 0
                    r8 = r8[r13]
                    int[] r15 = r4.lineCounts
                    r15 = r15[r9]
                    if (r8 > r15) goto L_0x07ac
                    int[] r8 = r4.lineCounts
                    int r8 = r8.length
                    r15 = 2
                    if (r8 <= r15) goto L_0x0798
                    int[] r8 = r4.lineCounts
                    r8 = r8[r9]
                    int[] r9 = r4.lineCounts
                    r9 = r9[r15]
                    if (r8 > r9) goto L_0x0795
                    goto L_0x0798
                L_0x0795:
                    r9 = 3
                    r15 = 2
                    goto L_0x07ae
                L_0x0798:
                    int[] r8 = r4.lineCounts
                    int r8 = r8.length
                    r9 = 3
                    if (r8 <= r9) goto L_0x07aa
                    int[] r8 = r4.lineCounts
                    r15 = 2
                    r8 = r8[r15]
                    int[] r13 = r4.lineCounts
                    r13 = r13[r9]
                    if (r8 <= r13) goto L_0x07b3
                    goto L_0x07ae
                L_0x07aa:
                    r15 = 2
                    goto L_0x07b3
                L_0x07ac:
                    r9 = 3
                    r15 = 2
                L_0x07ae:
                    float r7 = r7 * r17
                    goto L_0x07b3
                L_0x07b1:
                    r9 = 3
                    r15 = 2
                L_0x07b3:
                    float r8 = (float) r12
                    int r8 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                    if (r8 >= 0) goto L_0x07bc
                    r8 = 1069547520(0x3fCLASSNAME, float:1.5)
                    float r7 = r7 * r8
                L_0x07bc:
                    if (r0 == 0) goto L_0x07c2
                    int r8 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
                    if (r8 >= 0) goto L_0x07c4
                L_0x07c2:
                    r0 = r4
                    r1 = r7
                L_0x07c4:
                    int r3 = r3 + 1
                    goto L_0x0743
                L_0x07c8:
                    if (r0 != 0) goto L_0x07cb
                    return
                L_0x07cb:
                    r3 = 0
                    r4 = 0
                    r5 = 0
                L_0x07ce:
                    int[] r6 = r0.lineCounts
                    int r6 = r6.length
                    if (r5 >= r6) goto L_0x0845
                    int[] r6 = r0.lineCounts
                    r6 = r6[r5]
                    float[] r7 = r0.heights
                    r7 = r7[r5]
                    int r8 = r10.maxSizeWidth
                    r9 = 0
                    r13 = 0
                L_0x07df:
                    if (r13 >= r6) goto L_0x0831
                    r15 = r28[r3]
                    r17 = r1
                    float r1 = r15 * r7
                    int r1 = (int) r1
                    int r8 = r8 - r1
                    r19 = r2
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                    java.lang.Object r2 = r2.get(r3)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2
                    r20 = 0
                    if (r5 != 0) goto L_0x07f9
                    r20 = r20 | 4
                L_0x07f9:
                    r24 = r8
                    int[] r8 = r0.lineCounts
                    int r8 = r8.length
                    r25 = 1
                    int r8 = r8 + -1
                    if (r5 != r8) goto L_0x0806
                    r20 = r20 | 8
                L_0x0806:
                    if (r13 != 0) goto L_0x080a
                    r20 = r20 | 1
                L_0x080a:
                    int r8 = r6 + -1
                    if (r13 != r8) goto L_0x0812
                    r20 = r20 | 2
                    r8 = r2
                    r9 = r8
                L_0x0812:
                    float r39 = r7 / r32
                    r33 = r2
                    r34 = r13
                    r35 = r13
                    r36 = r5
                    r37 = r5
                    r38 = r1
                    r40 = r20
                    r33.set(r34, r35, r36, r37, r38, r39, r40)
                    int r3 = r3 + 1
                    int r13 = r13 + 1
                    r1 = r17
                    r2 = r19
                    r8 = r24
                    goto L_0x07df
                L_0x0831:
                    r17 = r1
                    r19 = r2
                    int r1 = r9.pw
                    int r1 = r1 + r8
                    r9.pw = r1
                    int r1 = r9.spanSize
                    int r1 = r1 + r8
                    r9.spanSize = r1
                    float r4 = r4 + r7
                    int r5 = r5 + 1
                    r1 = r17
                    goto L_0x07ce
                L_0x0845:
                    r17 = r1
                    r19 = r2
                L_0x0849:
                    r0 = 0
                L_0x084a:
                    r1 = r23
                    if (r0 >= r1) goto L_0x0863
                    java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r10.posArray
                    java.lang.Object r2 = r2.get(r0)
                    org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2
                    int r3 = r2.flags
                    r4 = 1
                    r3 = r3 & r4
                    if (r3 == 0) goto L_0x085e
                    r2.edge = r4
                L_0x085e:
                    int r0 = r0 + 1
                    r23 = r1
                    goto L_0x084a
                L_0x0863:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockCollageCell.GroupedMessages.calculate():void");
            }
        }

        public BlockCollageCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            AnonymousClass1 r0 = new RecyclerListView(context, ArticleViewer.this) {
                public void requestLayout() {
                    if (!BlockCollageCell.this.inLayout) {
                        super.requestLayout();
                    }
                }
            };
            this.innerListView = r0;
            r0.addItemDecoration(new RecyclerView.ItemDecoration(ArticleViewer.this) {
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    MessageObject.GroupedMessagePosition position;
                    outRect.bottom = 0;
                    if (view instanceof BlockPhotoCell) {
                        position = BlockCollageCell.this.group.positions.get(((BlockPhotoCell) view).currentBlock);
                    } else if (view instanceof BlockVideoCell) {
                        position = BlockCollageCell.this.group.positions.get(((BlockVideoCell) view).currentBlock);
                    } else {
                        position = null;
                    }
                    if (position != null && position.siblingHeights != null) {
                        float maxHeight = ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f;
                        int h = 0;
                        for (float f : position.siblingHeights) {
                            h += (int) Math.ceil((double) (f * maxHeight));
                        }
                        int h2 = h + ((position.maxY - position.minY) * AndroidUtilities.dp2(11.0f));
                        int count = BlockCollageCell.this.group.posArray.size();
                        int a = 0;
                        while (true) {
                            if (a >= count) {
                                break;
                            }
                            MessageObject.GroupedMessagePosition pos = BlockCollageCell.this.group.posArray.get(a);
                            if (pos.minY == position.minY && ((pos.minX != position.minX || pos.maxX != position.maxX || pos.minY != position.minY || pos.maxY != position.maxY) && pos.minY == position.minY)) {
                                h2 -= ((int) Math.ceil((double) (pos.ph * maxHeight))) - AndroidUtilities.dp(4.0f);
                                break;
                            }
                            a++;
                        }
                        outRect.bottom = -h2;
                    }
                }
            });
            final ArticleViewer articleViewer = ArticleViewer.this;
            AnonymousClass3 r2 = new GridLayoutManagerFixed(context, 1000, 1, true) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }

                public boolean shouldLayoutChildFromOpositeSide(View child) {
                    return false;
                }

                /* access modifiers changed from: protected */
                public boolean hasSiblingChild(int position) {
                    MessageObject.GroupedMessagePosition pos = BlockCollageCell.this.group.positions.get(BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1));
                    if (pos.minX == pos.maxX || pos.minY != pos.maxY || pos.minY == 0) {
                        return false;
                    }
                    int count = BlockCollageCell.this.group.posArray.size();
                    for (int a = 0; a < count; a++) {
                        MessageObject.GroupedMessagePosition p = BlockCollageCell.this.group.posArray.get(a);
                        if (p != pos && p.minY <= pos.minY && p.maxY >= pos.minY) {
                            return true;
                        }
                    }
                    return false;
                }
            };
            r2.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(ArticleViewer.this) {
                public int getSpanSize(int position) {
                    return BlockCollageCell.this.group.positions.get(BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1)).spanSize;
                }
            });
            this.innerListView.setLayoutManager(r2);
            RecyclerListView recyclerListView = this.innerListView;
            AnonymousClass5 r22 = new RecyclerView.Adapter(ArticleViewer.this) {
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view;
                    switch (viewType) {
                        case 0:
                            view = new BlockPhotoCell(BlockCollageCell.this.getContext(), BlockCollageCell.this.parentAdapter, 2);
                            break;
                        default:
                            view = new BlockVideoCell(BlockCollageCell.this.getContext(), BlockCollageCell.this.parentAdapter, 2);
                            break;
                    }
                    return new RecyclerListView.Holder(view);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    TLRPC.PageBlock pageBlock = BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1);
                    switch (holder.getItemViewType()) {
                        case 0:
                            BlockPhotoCell cell = (BlockPhotoCell) holder.itemView;
                            MessageObject.GroupedMessagePosition unused = cell.groupPosition = BlockCollageCell.this.group.positions.get(pageBlock);
                            cell.setBlock((TLRPC.TL_pageBlockPhoto) pageBlock, true, true);
                            return;
                        default:
                            BlockVideoCell cell2 = (BlockVideoCell) holder.itemView;
                            MessageObject.GroupedMessagePosition unused2 = cell2.groupPosition = BlockCollageCell.this.group.positions.get(pageBlock);
                            cell2.setBlock((TLRPC.TL_pageBlockVideo) pageBlock, true, true);
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
                    if (BlockCollageCell.this.currentBlock.items.get((BlockCollageCell.this.currentBlock.items.size() - position) - 1) instanceof TLRPC.TL_pageBlockPhoto) {
                        return 0;
                    }
                    return 1;
                }
            };
            this.innerAdapter = r22;
            recyclerListView.setAdapter(r22);
            addView(this.innerListView, LayoutHelper.createFrame(-1, -2.0f));
            setWillNotDraw(false);
        }

        public void setBlock(TLRPC.TL_pageBlockCollage block) {
            if (this.currentBlock != block) {
                this.currentBlock = block;
                this.group.calculate();
            }
            this.innerAdapter.notifyDataSetChanged();
            this.innerListView.setGlowColor(Theme.getColor("windowBackgroundWhite"));
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.captionLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int listWidth;
            int textWidth;
            int height2;
            this.inLayout = true;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            TLRPC.TL_pageBlockCollage tL_pageBlockCollage = this.currentBlock;
            if (tL_pageBlockCollage != null) {
                int listWidth2 = width;
                if (tL_pageBlockCollage.level > 0) {
                    int dp = AndroidUtilities.dp((float) (this.currentBlock.level * 14)) + AndroidUtilities.dp(18.0f);
                    this.listX = dp;
                    this.textX = dp;
                    int listWidth3 = listWidth2 - (dp + AndroidUtilities.dp(18.0f));
                    textWidth = listWidth3;
                    listWidth = listWidth3;
                } else {
                    this.listX = 0;
                    this.textX = AndroidUtilities.dp(18.0f);
                    textWidth = width - AndroidUtilities.dp(36.0f);
                    listWidth = listWidth2;
                }
                this.innerListView.measure(View.MeasureSpec.makeMeasureSpec(listWidth, NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                int height3 = this.innerListView.getMeasuredHeight();
                this.textY = AndroidUtilities.dp(8.0f) + height3;
                DrawingText access$13500 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                this.captionLayout = access$13500;
                if (access$13500 != null) {
                    int dp2 = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp2;
                    int height4 = height3 + dp2 + AndroidUtilities.dp(4.0f);
                    this.captionLayout.x = this.textX;
                    this.captionLayout.y = this.textY;
                    height2 = height4;
                } else {
                    this.creditOffset = 0;
                    height2 = height3;
                }
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = access$13600;
                if (access$13600 != null) {
                    height2 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    this.creditLayout.x = this.textX;
                    this.creditLayout.y = this.textY + this.creditOffset;
                }
                int height5 = height2 + AndroidUtilities.dp(16.0f);
                if (this.currentBlock.level > 0 && !this.currentBlock.bottom) {
                    height5 += AndroidUtilities.dp(8.0f);
                }
                height = height5;
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
            this.inLayout = false;
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.innerListView.layout(this.listX, AndroidUtilities.dp(8.0f), this.listX + this.innerListView.getMeasuredWidth(), this.innerListView.getMeasuredHeight() + AndroidUtilities.dp(8.0f));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int count = 0;
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.captionLayout.draw(canvas, this);
                    canvas.restore();
                    count = 0 + 1;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, count);
                    this.creditLayout.draw(canvas, this);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    private class BlockSlideshowCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockSlideshow currentBlock;
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

        public BlockSlideshowCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            if (ArticleViewer.dotsPaint == null) {
                Paint unused = ArticleViewer.dotsPaint = new Paint(1);
                ArticleViewer.dotsPaint.setColor(-1);
            }
            AnonymousClass1 r0 = new ViewPager(context, ArticleViewer.this) {
                public boolean onTouchEvent(MotionEvent ev) {
                    return super.onTouchEvent(ev);
                }

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    ArticleViewer.this.cancelCheckLongPress();
                    return super.onInterceptTouchEvent(ev);
                }
            };
            this.innerListView = r0;
            r0.addOnPageChangeListener(new ViewPager.OnPageChangeListener(ArticleViewer.this) {
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    float width = (float) BlockSlideshowCell.this.innerListView.getMeasuredWidth();
                    if (width != 0.0f) {
                        BlockSlideshowCell blockSlideshowCell = BlockSlideshowCell.this;
                        float unused = blockSlideshowCell.pageOffset = (((((float) position) * width) + ((float) positionOffsetPixels)) - (((float) blockSlideshowCell.currentPage) * width)) / width;
                        BlockSlideshowCell.this.dotsContainer.invalidate();
                    }
                }

                public void onPageSelected(int position) {
                    int unused = BlockSlideshowCell.this.currentPage = position;
                    BlockSlideshowCell.this.dotsContainer.invalidate();
                }

                public void onPageScrollStateChanged(int state) {
                }
            });
            ViewPager viewPager = this.innerListView;
            AnonymousClass3 r1 = new PagerAdapter(ArticleViewer.this) {

                /* renamed from: org.telegram.ui.ArticleViewer$BlockSlideshowCell$3$ObjectContainer */
                class ObjectContainer {
                    /* access modifiers changed from: private */
                    public TLRPC.PageBlock block;
                    /* access modifiers changed from: private */
                    public View view;

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
                    TLRPC.PageBlock block = BlockSlideshowCell.this.currentBlock.items.get(position);
                    if (block instanceof TLRPC.TL_pageBlockPhoto) {
                        view = new BlockPhotoCell(BlockSlideshowCell.this.getContext(), BlockSlideshowCell.this.parentAdapter, 1);
                        ((BlockPhotoCell) view).setBlock((TLRPC.TL_pageBlockPhoto) block, true, true);
                    } else {
                        view = new BlockVideoCell(BlockSlideshowCell.this.getContext(), BlockSlideshowCell.this.parentAdapter, 1);
                        ((BlockVideoCell) view).setBlock((TLRPC.TL_pageBlockVideo) block, true, true);
                    }
                    container.addView(view);
                    ObjectContainer objectContainer = new ObjectContainer();
                    View unused = objectContainer.view = view;
                    TLRPC.PageBlock unused2 = objectContainer.block = block;
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
            this.innerAdapter = r1;
            viewPager.setAdapter(r1);
            AndroidUtilities.setViewPagerEdgeEffectColor(this.innerListView, Theme.getColor("windowBackgroundWhite"));
            addView(this.innerListView);
            AnonymousClass4 r02 = new View(context, ArticleViewer.this) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    int xOffset;
                    if (BlockSlideshowCell.this.currentBlock != null) {
                        int count = BlockSlideshowCell.this.innerAdapter.getCount();
                        int totalWidth = (AndroidUtilities.dp(7.0f) * count) + ((count - 1) * AndroidUtilities.dp(6.0f)) + AndroidUtilities.dp(4.0f);
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
                            int cx = AndroidUtilities.dp(4.0f) + xOffset + (AndroidUtilities.dp(13.0f) * a);
                            Drawable drawable = BlockSlideshowCell.this.currentPage == a ? ArticleViewer.this.slideDotBigDrawable : ArticleViewer.this.slideDotDrawable;
                            drawable.setBounds(cx - AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f) + cx, AndroidUtilities.dp(10.0f));
                            drawable.draw(canvas);
                            a++;
                        }
                    }
                }
            };
            this.dotsContainer = r02;
            addView(r02);
            setWillNotDraw(false);
        }

        public void setBlock(TLRPC.TL_pageBlockSlideshow block) {
            this.currentBlock = block;
            this.innerAdapter.notifyDataSetChanged();
            this.innerListView.setCurrentItem(0, false);
            this.innerListView.forceLayout();
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.captionLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int height2;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            if (this.currentBlock != null) {
                int height3 = AndroidUtilities.dp(310.0f);
                this.innerListView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height3, NUM));
                int size = this.currentBlock.items.size();
                this.dotsContainer.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(10.0f), NUM));
                int textWidth = width - AndroidUtilities.dp(36.0f);
                this.textY = AndroidUtilities.dp(16.0f) + height3;
                DrawingText access$13500 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                this.captionLayout = access$13500;
                if (access$13500 != null) {
                    int dp = AndroidUtilities.dp(4.0f) + this.captionLayout.getHeight();
                    this.creditOffset = dp;
                    int height4 = height3 + dp + AndroidUtilities.dp(4.0f);
                    this.captionLayout.x = this.textX;
                    this.captionLayout.y = this.textY;
                    height2 = height4;
                } else {
                    this.creditOffset = 0;
                    height2 = height3;
                }
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption.credit, textWidth, this.textY + this.creditOffset, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.creditLayout = access$13600;
                if (access$13600 != null) {
                    height2 += AndroidUtilities.dp(4.0f) + this.creditLayout.getHeight();
                    this.creditLayout.x = this.textX;
                    this.creditLayout.y = this.textY + this.creditOffset;
                }
                height = height2 + AndroidUtilities.dp(16.0f);
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.innerListView.layout(0, AndroidUtilities.dp(8.0f), this.innerListView.getMeasuredWidth(), AndroidUtilities.dp(8.0f) + this.innerListView.getMeasuredHeight());
            int y = this.innerListView.getBottom() - AndroidUtilities.dp(23.0f);
            View view = this.dotsContainer;
            view.layout(0, y, view.getMeasuredWidth(), this.dotsContainer.getMeasuredHeight() + y);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int count = 0;
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.captionLayout.draw(canvas, this);
                    canvas.restore();
                    count = 0 + 1;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, count);
                    this.creditLayout.draw(canvas, this);
                    canvas.restore();
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
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

        public BlockListItemCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockListItem block) {
            if (this.currentBlock != block) {
                this.currentBlock = block;
                RecyclerView.ViewHolder viewHolder = this.blockLayout;
                if (viewHolder != null) {
                    removeView(viewHolder.itemView);
                    this.blockLayout = null;
                }
                if (this.currentBlock.blockItem != null) {
                    int access$7700 = this.parentAdapter.getTypeForBlock(this.currentBlock.blockItem);
                    this.currentBlockType = access$7700;
                    RecyclerView.ViewHolder onCreateViewHolder = this.parentAdapter.onCreateViewHolder(this, access$7700);
                    this.blockLayout = onCreateViewHolder;
                    addView(onCreateViewHolder.itemView);
                }
            }
            if (this.currentBlock.blockItem != null) {
                this.parentAdapter.bindBlockToHolder(this.currentBlockType, this.blockLayout, this.currentBlock.blockItem, 0, 0);
            }
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int maxWidth;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TL_pageBlockListItem tL_pageBlockListItem = this.currentBlock;
            if (tL_pageBlockListItem != null) {
                this.textLayout = null;
                this.textY = (tL_pageBlockListItem.index == 0 && this.currentBlock.parent.level == 0) ? AndroidUtilities.dp(10.0f) : 0;
                this.numOffsetY = 0;
                if (!(this.currentBlock.parent.lastMaxNumCalcWidth == width && this.currentBlock.parent.lastFontSize == SharedConfig.ivFontSize)) {
                    int unused = this.currentBlock.parent.lastMaxNumCalcWidth = width;
                    int unused2 = this.currentBlock.parent.lastFontSize = SharedConfig.ivFontSize;
                    int unused3 = this.currentBlock.parent.maxNumWidth = 0;
                    int size = this.currentBlock.parent.items.size();
                    for (int a = 0; a < size; a++) {
                        TL_pageBlockListItem item = (TL_pageBlockListItem) this.currentBlock.parent.items.get(a);
                        if (item.num != null) {
                            DrawingText unused4 = item.numLayout = ArticleViewer.this.createLayoutForText(this, item.num, (TLRPC.RichText) null, width - AndroidUtilities.dp(54.0f), this.textY, this.currentBlock, this.parentAdapter);
                            int unused5 = this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) item.numLayout.getLineWidth(0)));
                        }
                    }
                    int unused6 = this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) ArticleViewer.listTextNumPaint.measureText("00.")));
                }
                this.drawDot = !this.currentBlock.parent.pageBlockList.ordered;
                if (this.parentAdapter.isRtl) {
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = AndroidUtilities.dp(24.0f) + this.currentBlock.parent.maxNumWidth + (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f));
                }
                int maxWidth2 = (width - AndroidUtilities.dp(18.0f)) - this.textX;
                if (this.parentAdapter.isRtl) {
                    maxWidth = maxWidth2 - ((AndroidUtilities.dp(6.0f) + this.currentBlock.parent.maxNumWidth) + (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f)));
                } else {
                    maxWidth = maxWidth2;
                }
                if (this.currentBlock.textItem != null) {
                    DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.textItem, maxWidth, this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                    this.textLayout = access$13600;
                    if (access$13600 != null && access$13600.getLineCount() > 0) {
                        if (this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            this.numOffsetY = (this.currentBlock.numLayout.getLineAscent(0) + AndroidUtilities.dp(2.5f)) - this.textLayout.getLineAscent(0);
                        }
                        height = 0 + this.textLayout.getHeight() + AndroidUtilities.dp(8.0f);
                    }
                } else if (this.currentBlock.blockItem != null) {
                    this.blockX = this.textX;
                    this.blockY = this.textY;
                    RecyclerView.ViewHolder viewHolder = this.blockLayout;
                    if (viewHolder != null) {
                        if (viewHolder.itemView instanceof BlockParagraphCell) {
                            this.blockY -= AndroidUtilities.dp(8.0f);
                            if (!this.parentAdapter.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                            height = 0 - AndroidUtilities.dp(8.0f);
                        } else if ((this.blockLayout.itemView instanceof BlockHeaderCell) || (this.blockLayout.itemView instanceof BlockSubheaderCell) || (this.blockLayout.itemView instanceof BlockTitleCell) || (this.blockLayout.itemView instanceof BlockSubtitleCell)) {
                            if (!this.parentAdapter.isRtl) {
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
                        this.blockLayout.itemView.measure(View.MeasureSpec.makeMeasureSpec(maxWidth, NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                        if ((this.blockLayout.itemView instanceof BlockParagraphCell) && this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            BlockParagraphCell paragraphCell = (BlockParagraphCell) this.blockLayout.itemView;
                            if (paragraphCell.textLayout != null && paragraphCell.textLayout.getLineCount() > 0) {
                                this.numOffsetY = (this.currentBlock.numLayout.getLineAscent(0) + AndroidUtilities.dp(2.5f)) - paragraphCell.textLayout.getLineAscent(0);
                            }
                        }
                        if (this.currentBlock.blockItem instanceof TLRPC.TL_pageBlockDetails) {
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
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    this.textLayout.y = this.textY;
                }
                RecyclerView.ViewHolder viewHolder2 = this.blockLayout;
                if (viewHolder2 != null && (viewHolder2.itemView instanceof TextSelectionHelper.ArticleSelectableView)) {
                    ArticleViewer.this.textSelectionHelper.arrayList.clear();
                    ((TextSelectionHelper.ArticleSelectableView) this.blockLayout.itemView).fillTextLayoutBlocks(ArticleViewer.this.textSelectionHelper.arrayList);
                    Iterator<TextSelectionHelper.TextLayoutBlock> it = ArticleViewer.this.textSelectionHelper.arrayList.iterator();
                    while (it.hasNext()) {
                        TextSelectionHelper.TextLayoutBlock block = it.next();
                        if (block instanceof DrawingText) {
                            ((DrawingText) block).x += this.blockX;
                            ((DrawingText) block).y += this.blockY;
                        }
                    }
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                int i = this.blockX;
                view.layout(i, this.blockY, this.blockLayout.itemView.getMeasuredWidth() + i, this.blockY + this.blockLayout.itemView.getMeasuredHeight());
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int width = getMeasuredWidth();
                if (this.currentBlock.numLayout != null) {
                    canvas.save();
                    int i = 0;
                    if (this.parentAdapter.isRtl) {
                        float dp = (float) (((width - AndroidUtilities.dp(15.0f)) - this.currentBlock.parent.maxNumWidth) - (this.currentBlock.parent.level * AndroidUtilities.dp(12.0f)));
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
                    this.currentBlock.numLayout.draw(canvas, this);
                    canvas.restore();
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this);
                    this.textLayout.draw(canvas, this);
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                info.setText(drawingText.getText());
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null && (viewHolder.itemView instanceof TextSelectionHelper.ArticleSelectableView)) {
                ((TextSelectionHelper.ArticleSelectableView) this.blockLayout.itemView).fillTextLayoutBlocks(blocks);
            }
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
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

        public BlockOrderedListItemCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
        }

        public void setBlock(TL_pageBlockOrderedListItem block) {
            if (this.currentBlock != block) {
                this.currentBlock = block;
                RecyclerView.ViewHolder viewHolder = this.blockLayout;
                if (viewHolder != null) {
                    removeView(viewHolder.itemView);
                    this.blockLayout = null;
                }
                if (this.currentBlock.blockItem != null) {
                    int access$7700 = this.parentAdapter.getTypeForBlock(this.currentBlock.blockItem);
                    this.currentBlockType = access$7700;
                    RecyclerView.ViewHolder onCreateViewHolder = this.parentAdapter.onCreateViewHolder(this, access$7700);
                    this.blockLayout = onCreateViewHolder;
                    addView(onCreateViewHolder.itemView);
                }
            }
            if (this.currentBlock.blockItem != null) {
                this.parentAdapter.bindBlockToHolder(this.currentBlockType, this.blockLayout, this.currentBlock.blockItem, 0, 0);
            }
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY)) {
                return true;
            }
            return super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int maxWidth;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TL_pageBlockOrderedListItem tL_pageBlockOrderedListItem = this.currentBlock;
            if (tL_pageBlockOrderedListItem != null) {
                this.textLayout = null;
                this.textY = (tL_pageBlockOrderedListItem.index == 0 && this.currentBlock.parent.level == 0) ? AndroidUtilities.dp(10.0f) : 0;
                this.numOffsetY = 0;
                if (!(this.currentBlock.parent.lastMaxNumCalcWidth == width && this.currentBlock.parent.lastFontSize == SharedConfig.ivFontSize)) {
                    int unused = this.currentBlock.parent.lastMaxNumCalcWidth = width;
                    int unused2 = this.currentBlock.parent.lastFontSize = SharedConfig.ivFontSize;
                    int unused3 = this.currentBlock.parent.maxNumWidth = 0;
                    int size = this.currentBlock.parent.items.size();
                    for (int a = 0; a < size; a++) {
                        TL_pageBlockOrderedListItem item = (TL_pageBlockOrderedListItem) this.currentBlock.parent.items.get(a);
                        if (item.num != null) {
                            DrawingText unused4 = item.numLayout = ArticleViewer.this.createLayoutForText(this, item.num, (TLRPC.RichText) null, width - AndroidUtilities.dp(54.0f), this.textY, this.currentBlock, this.parentAdapter);
                            int unused5 = this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) item.numLayout.getLineWidth(0)));
                        }
                    }
                    int unused6 = this.currentBlock.parent.maxNumWidth = Math.max(this.currentBlock.parent.maxNumWidth, (int) Math.ceil((double) ArticleViewer.listTextNumPaint.measureText("00.")));
                }
                if (this.parentAdapter.isRtl) {
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textX = AndroidUtilities.dp(24.0f) + this.currentBlock.parent.maxNumWidth + (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f));
                }
                this.verticalAlign = false;
                int maxWidth2 = (width - AndroidUtilities.dp(18.0f)) - this.textX;
                if (this.parentAdapter.isRtl) {
                    maxWidth = maxWidth2 - ((AndroidUtilities.dp(6.0f) + this.currentBlock.parent.maxNumWidth) + (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f)));
                } else {
                    maxWidth = maxWidth2;
                }
                if (this.currentBlock.textItem != null) {
                    DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.textItem, maxWidth, this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                    this.textLayout = access$13600;
                    if (access$13600 != null && access$13600.getLineCount() > 0) {
                        if (this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            this.numOffsetY = this.currentBlock.numLayout.getLineAscent(0) - this.textLayout.getLineAscent(0);
                        }
                        height = 0 + this.textLayout.getHeight() + AndroidUtilities.dp(8.0f);
                    }
                } else if (this.currentBlock.blockItem != null) {
                    this.blockX = this.textX;
                    this.blockY = this.textY;
                    RecyclerView.ViewHolder viewHolder = this.blockLayout;
                    if (viewHolder != null) {
                        if (viewHolder.itemView instanceof BlockParagraphCell) {
                            this.blockY -= AndroidUtilities.dp(8.0f);
                            if (!this.parentAdapter.isRtl) {
                                this.blockX -= AndroidUtilities.dp(18.0f);
                            }
                            maxWidth += AndroidUtilities.dp(18.0f);
                            height = 0 - AndroidUtilities.dp(8.0f);
                        } else if ((this.blockLayout.itemView instanceof BlockHeaderCell) || (this.blockLayout.itemView instanceof BlockSubheaderCell) || (this.blockLayout.itemView instanceof BlockTitleCell) || (this.blockLayout.itemView instanceof BlockSubtitleCell)) {
                            if (!this.parentAdapter.isRtl) {
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
                        this.blockLayout.itemView.measure(View.MeasureSpec.makeMeasureSpec(maxWidth, NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                        if ((this.blockLayout.itemView instanceof BlockParagraphCell) && this.currentBlock.numLayout != null && this.currentBlock.numLayout.getLineCount() > 0) {
                            BlockParagraphCell paragraphCell = (BlockParagraphCell) this.blockLayout.itemView;
                            if (paragraphCell.textLayout != null && paragraphCell.textLayout.getLineCount() > 0) {
                                this.numOffsetY = this.currentBlock.numLayout.getLineAscent(0) - paragraphCell.textLayout.getLineAscent(0);
                            }
                        }
                        if (this.currentBlock.blockItem instanceof TLRPC.TL_pageBlockDetails) {
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
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    this.textLayout.y = this.textY;
                    if (this.currentBlock.numLayout != null) {
                        this.textLayout.prefix = this.currentBlock.numLayout.textLayout.getText();
                    }
                }
                RecyclerView.ViewHolder viewHolder2 = this.blockLayout;
                if (viewHolder2 != null && (viewHolder2.itemView instanceof TextSelectionHelper.ArticleSelectableView)) {
                    ArticleViewer.this.textSelectionHelper.arrayList.clear();
                    ((TextSelectionHelper.ArticleSelectableView) this.blockLayout.itemView).fillTextLayoutBlocks(ArticleViewer.this.textSelectionHelper.arrayList);
                    Iterator<TextSelectionHelper.TextLayoutBlock> it = ArticleViewer.this.textSelectionHelper.arrayList.iterator();
                    while (it.hasNext()) {
                        TextSelectionHelper.TextLayoutBlock block = it.next();
                        if (block instanceof DrawingText) {
                            ((DrawingText) block).x += this.blockX;
                            ((DrawingText) block).y += this.blockY;
                        }
                    }
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null) {
                View view = viewHolder.itemView;
                int i = this.blockX;
                view.layout(i, this.blockY, this.blockLayout.itemView.getMeasuredWidth() + i, this.blockY + this.blockLayout.itemView.getMeasuredHeight());
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int width = getMeasuredWidth();
                if (this.currentBlock.numLayout != null) {
                    canvas.save();
                    if (this.parentAdapter.isRtl) {
                        canvas.translate((float) (((width - AndroidUtilities.dp(18.0f)) - this.currentBlock.parent.maxNumWidth) - (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f))), (float) (this.textY + this.numOffsetY));
                    } else {
                        canvas.translate((float) (((AndroidUtilities.dp(18.0f) + this.currentBlock.parent.maxNumWidth) - ((int) Math.ceil((double) this.currentBlock.numLayout.getLineWidth(0)))) + (this.currentBlock.parent.level * AndroidUtilities.dp(20.0f))), (float) (this.textY + this.numOffsetY));
                    }
                    this.currentBlock.numLayout.draw(canvas, this);
                    canvas.restore();
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this);
                    this.textLayout.draw(canvas, this);
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                info.setText(drawingText.getText());
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            RecyclerView.ViewHolder viewHolder = this.blockLayout;
            if (viewHolder != null && (viewHolder.itemView instanceof TextSelectionHelper.ArticleSelectableView)) {
                ((TextSelectionHelper.ArticleSelectableView) this.blockLayout.itemView).fillTextLayoutBlocks(blocks);
            }
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    private class BlockDetailsCell extends View implements Drawable.Callback, TextSelectionHelper.ArticleSelectableView {
        /* access modifiers changed from: private */
        public AnimatedArrowDrawable arrow;
        private TLRPC.TL_pageBlockDetails currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(50.0f);
        private int textY = (AndroidUtilities.dp(11.0f) + 1);

        public BlockDetailsCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            this.arrow = new AnimatedArrowDrawable(ArticleViewer.getGrayTextColor(), true);
        }

        public void invalidateDrawable(Drawable drawable) {
            invalidate();
        }

        public void scheduleDrawable(Drawable drawable, Runnable runnable, long l) {
        }

        public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        }

        public void setBlock(TLRPC.TL_pageBlockDetails block) {
            this.currentBlock = block;
            this.arrow.setAnimationProgress(block.open ? 0.0f : 1.0f);
            this.arrow.setCallback(this);
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int h = AndroidUtilities.dp(39.0f);
            TLRPC.TL_pageBlockDetails tL_pageBlockDetails = this.currentBlock;
            if (tL_pageBlockDetails != null) {
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tL_pageBlockDetails.title, width - AndroidUtilities.dp(52.0f), 0, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13600;
                if (access$13600 != null) {
                    h = Math.max(h, AndroidUtilities.dp(21.0f) + this.textLayout.getHeight());
                    this.textY = ((this.textLayout.getHeight() + AndroidUtilities.dp(21.0f)) - this.textLayout.getHeight()) / 2;
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            }
            setMeasuredDimension(width, h + 1);
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
                    this.textLayout.draw(canvas, this);
                    canvas.restore();
                }
                int y = getMeasuredHeight() - 1;
                canvas.drawLine(0.0f, (float) y, (float) getMeasuredWidth(), (float) y, ArticleViewer.dividerPaint);
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    private static class BlockDetailsBottomCell extends View {
        private RectF rect = new RectF();

        public BlockDetailsBottomCell(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(4.0f) + 1);
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(12.0f));
            Theme.setCombinedDrawableColor(this.shadowDrawable, Theme.getColor("windowBackgroundGray"), false);
        }
    }

    private class BlockRelatedArticlesHeaderCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockRelatedArticles currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockRelatedArticlesHeaderCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockRelatedArticles block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            TLRPC.TL_pageBlockRelatedArticles tL_pageBlockRelatedArticles = this.currentBlock;
            if (tL_pageBlockRelatedArticles != null) {
                DrawingText access$14500 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tL_pageBlockRelatedArticles.title, width - AndroidUtilities.dp(52.0f), 0, this.currentBlock, Layout.Alignment.ALIGN_NORMAL, 1, this.parentAdapter);
                this.textLayout = access$14500;
                if (access$14500 != null) {
                    this.textY = AndroidUtilities.dp(6.0f) + ((AndroidUtilities.dp(32.0f) - this.textLayout.getHeight()) / 2);
                }
            }
            if (this.textLayout != null) {
                setMeasuredDimension(width, AndroidUtilities.dp(38.0f));
                this.textLayout.x = this.textX;
                this.textLayout.y = this.textY;
                return;
            }
            setMeasuredDimension(width, 1);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
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

        public BlockRelatedArticlesCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.imageView = imageReceiver;
            imageReceiver.setRoundRadius(AndroidUtilities.dp(6.0f));
        }

        public void setBlock(TL_pageBlockRelatedArticlesChild block) {
            this.currentBlock = block;
            requestLayout();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int availableWidth;
            int layoutHeight;
            boolean isTitleRtl;
            int height;
            int height2;
            String description;
            int height3;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            this.divider = this.currentBlock.num != this.currentBlock.parent.articles.size() - 1;
            TLRPC.TL_pageRelatedArticle item = this.currentBlock.parent.articles.get(this.currentBlock.num);
            int additionalHeight = AndroidUtilities.dp((float) (SharedConfig.ivFontSize - 16));
            TLRPC.Photo photo = item.photo_id != 0 ? this.parentAdapter.getPhotoWithId(item.photo_id) : null;
            if (photo != null) {
                this.drawImage = true;
                TLRPC.PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 80, true);
                if (image == thumb) {
                    thumb = null;
                }
                this.imageView.setImage(ImageLocation.getForPhoto(image, photo), "64_64", ImageLocation.getForPhoto(thumb, photo), "64_64_b", (long) image.size, (String) null, this.parentAdapter.currentPage, 1);
            } else {
                this.drawImage = false;
            }
            int layoutHeight2 = AndroidUtilities.dp(60.0f);
            int availableWidth2 = width - AndroidUtilities.dp(36.0f);
            if (this.drawImage) {
                int imageWidth = AndroidUtilities.dp(44.0f);
                this.imageView.setImageCoords((float) ((width - imageWidth) - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(8.0f), (float) imageWidth, (float) imageWidth);
                availableWidth = (int) (((float) availableWidth2) - (this.imageView.getImageWidth() + ((float) AndroidUtilities.dp(6.0f))));
            } else {
                availableWidth = availableWidth2;
            }
            int height4 = AndroidUtilities.dp(18.0f);
            boolean isTitleRtl2 = false;
            if (item.title != null) {
                layoutHeight = layoutHeight2;
                TLRPC.Photo photo2 = photo;
                this.textLayout = ArticleViewer.this.createLayoutForText(this, item.title, (TLRPC.RichText) null, availableWidth, this.textY, this.currentBlock, Layout.Alignment.ALIGN_NORMAL, 3, this.parentAdapter);
            } else {
                layoutHeight = layoutHeight2;
                TLRPC.Photo photo3 = photo;
            }
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                int count = drawingText.getLineCount();
                int lineCount = 4 - count;
                this.textOffset = this.textLayout.getHeight() + AndroidUtilities.dp(6.0f) + additionalHeight;
                int height5 = height4 + this.textLayout.getHeight();
                int a = 0;
                while (true) {
                    if (a >= count) {
                        break;
                    } else if (this.textLayout.getLineLeft(a) != 0.0f) {
                        isTitleRtl2 = true;
                        break;
                    } else {
                        a++;
                    }
                }
                this.textLayout.x = this.textX;
                this.textLayout.y = this.textY;
                isTitleRtl = isTitleRtl2;
                height = height5;
                height2 = lineCount;
            } else {
                this.textOffset = 0;
                isTitleRtl = false;
                height = height4;
                height2 = 4;
            }
            if (item.published_date != 0 && !TextUtils.isEmpty(item.author)) {
                description = LocaleController.formatString("ArticleDateByAuthor", NUM, LocaleController.getInstance().chatFullDate.format(((long) item.published_date) * 1000), item.author);
            } else if (!TextUtils.isEmpty(item.author)) {
                description = LocaleController.formatString("ArticleByAuthor", NUM, item.author);
            } else if (item.published_date != 0) {
                description = LocaleController.getInstance().chatFullDate.format(((long) item.published_date) * 1000);
            } else if (!TextUtils.isEmpty(item.description)) {
                description = item.description;
            } else {
                description = item.url;
            }
            DrawingText access$14500 = ArticleViewer.this.createLayoutForText(this, description, (TLRPC.RichText) null, availableWidth, this.textY + this.textOffset, this.currentBlock, (this.parentAdapter.isRtl || isTitleRtl) ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, height2, this.parentAdapter);
            this.textLayout2 = access$14500;
            if (access$14500 != null) {
                int height6 = height + access$14500.getHeight();
                if (this.textLayout != null) {
                    height6 += AndroidUtilities.dp(6.0f) + additionalHeight;
                }
                this.textLayout2.x = this.textX;
                this.textLayout2.y = this.textY + this.textOffset;
                height3 = height6;
            } else {
                height3 = height;
            }
            setMeasuredDimension(width, (this.divider ? 1 : 0) + Math.max(layoutHeight, height3));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.drawImage) {
                    this.imageView.draw(canvas);
                }
                int count = 0;
                canvas.save();
                canvas.translate((float) this.textX, (float) AndroidUtilities.dp(10.0f));
                if (this.textLayout != null) {
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.textLayout.draw(canvas, this);
                    count = 0 + 1;
                }
                if (this.textLayout2 != null) {
                    canvas.translate(0.0f, (float) this.textOffset);
                    ArticleViewer.this.drawTextSelection(canvas, this, count);
                    this.textLayout2.draw(canvas, this);
                }
                canvas.restore();
                if (this.divider) {
                    canvas.drawLine(this.parentAdapter.isRtl ? 0.0f : (float) AndroidUtilities.dp(17.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (this.parentAdapter.isRtl ? AndroidUtilities.dp(17.0f) : 0)), (float) (getMeasuredHeight() - 1), ArticleViewer.dividerPaint);
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.textLayout2;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    private class BlockHeaderCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockHeader currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockHeaderCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockHeader block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockHeader tL_pageBlockHeader = this.currentBlock;
            if (tL_pageBlockHeader != null) {
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tL_pageBlockHeader.text, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13600;
                if (access$13600 != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.textLayout != null) {
                info.setText(this.textLayout.getText() + ", " + LocaleController.getString("AccDescrIVHeading", NUM));
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    private static class BlockDividerCell extends View {
        private RectF rect = new RectF();

        public BlockDividerCell(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(18.0f));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int width = getMeasuredWidth() / 3;
            this.rect.set((float) width, (float) AndroidUtilities.dp(8.0f), (float) (width * 2), (float) AndroidUtilities.dp(10.0f));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), ArticleViewer.dividerPaint);
        }
    }

    private class BlockSubtitleCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockSubtitle currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockSubtitleCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockSubtitle block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockSubtitle tL_pageBlockSubtitle = this.currentBlock;
            if (tL_pageBlockSubtitle != null) {
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tL_pageBlockSubtitle.text, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13600;
                if (access$13600 != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.textLayout != null) {
                info.setText(this.textLayout.getText() + ", " + LocaleController.getString("AccDescrIVHeading", NUM));
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    private class BlockPullquoteCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockPullquote currentBlock;
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

        public void setBlock(TLRPC.TL_pageBlockPullquote block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(event);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockPullquote tL_pageBlockPullquote = this.currentBlock;
            if (tL_pageBlockPullquote != null) {
                DrawingText access$13500 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tL_pageBlockPullquote.text, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter);
                this.textLayout = access$13500;
                if (access$13500 != null) {
                    height = 0 + AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
                this.textY2 = AndroidUtilities.dp(2.0f) + height;
                DrawingText access$135002 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption, width - AndroidUtilities.dp(36.0f), this.textY2, this.currentBlock, this.parentAdapter);
                this.textLayout2 = access$135002;
                if (access$135002 != null) {
                    height += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                    this.textLayout2.x = this.textX;
                    this.textLayout2.y = this.textY2;
                }
                if (height != 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int count = 0;
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.textLayout.draw(canvas, this);
                    canvas.restore();
                    count = 0 + 1;
                }
                if (this.textLayout2 != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY2);
                    ArticleViewer.this.drawTextSelection(canvas, this, count);
                    this.textLayout2.draw(canvas, this);
                    canvas.restore();
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.textLayout2;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    private class BlockBlockquoteCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockBlockquote currentBlock;
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

        public void setBlock(TLRPC.TL_pageBlockBlockquote block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY)) {
                return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout2, this.textX, this.textY2) || super.onTouchEvent(event);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (this.currentBlock != null) {
                int textWidth = width - AndroidUtilities.dp(50.0f);
                if (this.currentBlock.level > 0) {
                    textWidth -= AndroidUtilities.dp((float) (this.currentBlock.level * 14));
                }
                DrawingText access$13500 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.text, textWidth, this.textY, this.currentBlock, this.parentAdapter);
                this.textLayout = access$13500;
                if (access$13500 != null) {
                    height = 0 + AndroidUtilities.dp(8.0f) + this.textLayout.getHeight();
                }
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
                this.textY2 = AndroidUtilities.dp(8.0f) + height;
                DrawingText access$135002 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.caption, textWidth, this.textY2, this.currentBlock, this.parentAdapter);
                this.textLayout2 = access$135002;
                if (access$135002 != null) {
                    height += AndroidUtilities.dp(8.0f) + this.textLayout2.getHeight();
                }
                if (height != 0) {
                    height += AndroidUtilities.dp(8.0f);
                }
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    this.textLayout.y = this.textY;
                }
                DrawingText drawingText2 = this.textLayout2;
                if (drawingText2 != null) {
                    drawingText2.x = this.textX;
                    this.textLayout2.y = this.textY2;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                int counter = 0;
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.textLayout.draw(canvas, this);
                    canvas.restore();
                    counter = 0 + 1;
                }
                if (this.textLayout2 != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY2);
                    ArticleViewer.this.drawTextSelection(canvas, this, counter);
                    this.textLayout2.draw(canvas, this);
                    canvas.restore();
                }
                if (this.parentAdapter.isRtl) {
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

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.textLayout2;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
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
        public TLRPC.TL_pageBlockPhoto currentBlock;
        private String currentFilter;
        private TLRPC.Photo currentPhoto;
        private TLRPC.PhotoSize currentPhotoObject;
        private TLRPC.PhotoSize currentPhotoObjectThumb;
        private String currentThumbFilter;
        private int currentType;
        /* access modifiers changed from: private */
        public MessageObject.GroupedMessagePosition groupPosition;
        /* access modifiers changed from: private */
        public ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
        private Drawable linkDrawable;
        private WebpageAdapter parentAdapter;
        private TLRPC.PageBlock parentBlock;
        private boolean photoPressed;
        private RadialProgress2 radialProgress;
        private int textX;
        private int textY;

        public BlockPhotoCell(Context context, WebpageAdapter adapter, int type) {
            super(context);
            this.parentAdapter = adapter;
            setWillNotDraw(false);
            this.channelCell = new BlockChannelCell(context, this.parentAdapter, 1);
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setProgressColor(-1);
            this.radialProgress.setColors(NUM, NUM, -1, -2500135);
            this.TAG = DownloadController.getInstance(ArticleViewer.this.currentAccount).generateObserverTag();
            addView(this.channelCell, LayoutHelper.createFrame(-1, -2.0f));
            this.currentType = type;
        }

        public void setBlock(TLRPC.TL_pageBlockPhoto block, boolean first, boolean last) {
            this.parentBlock = null;
            this.currentBlock = block;
            this.isFirst = first;
            this.channelCell.setVisibility(4);
            if (!TextUtils.isEmpty(this.currentBlock.url)) {
                this.linkDrawable = getResources().getDrawable(NUM);
            }
            TLRPC.TL_pageBlockPhoto tL_pageBlockPhoto = this.currentBlock;
            if (tL_pageBlockPhoto != null) {
                TLRPC.Photo photo = this.parentAdapter.getPhotoWithId(tL_pageBlockPhoto.photo_id);
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

        public void setParentBlock(TLRPC.PageBlock block) {
            this.parentBlock = block;
            if (this.parentAdapter.channelBlock != null && (this.parentBlock instanceof TLRPC.TL_pageBlockCover)) {
                this.channelCell.setBlock(this.parentAdapter.channelBlock);
                this.channelCell.setVisibility(0);
            }
        }

        public View getChannelCell() {
            return this.channelCell;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:28:0x00a3, code lost:
            if (r2 <= ((float) (r3 + org.telegram.messenger.AndroidUtilities.dp(48.0f)))) goto L_0x00a9;
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
                org.telegram.ui.ArticleViewer$WebpageAdapter r3 = r12.parentAdapter
                org.telegram.tgnet.TLRPC$TL_pageBlockChannel r3 = r3.channelBlock
                if (r3 == 0) goto L_0x006d
                int r3 = r13.getAction()
                if (r3 != r1) goto L_0x006d
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                int r3 = r3.currentAccount
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
                org.telegram.ui.ArticleViewer$WebpageAdapter r5 = r12.parentAdapter
                org.telegram.tgnet.TLRPC$TL_pageBlockChannel r5 = r5.channelBlock
                org.telegram.tgnet.TLRPC$Chat r5 = r5.channel
                java.lang.String r5 = r5.username
                org.telegram.ui.ArticleViewer r6 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ActionBar.BaseFragment r6 = r6.parentFragment
                r7 = 2
                r3.openByUserName(r5, r6, r7)
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                r3.close(r4, r1)
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
                int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
                if (r3 > 0) goto L_0x00a5
                int r3 = r12.buttonY
                float r6 = (float) r3
                int r6 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
                if (r6 < 0) goto L_0x00a5
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r3 = r3 + r5
                float r3 = (float) r3
                int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r3 <= 0) goto L_0x00a9
            L_0x00a5:
                int r3 = r12.buttonState
                if (r3 != 0) goto L_0x00af
            L_0x00a9:
                r12.buttonPressed = r1
                r12.invalidate()
                goto L_0x00e3
            L_0x00af:
                r12.photoPressed = r1
                goto L_0x00e3
            L_0x00b2:
                int r3 = r13.getAction()
                if (r3 != r1) goto L_0x00d8
                boolean r3 = r12.photoPressed
                if (r3 == 0) goto L_0x00c8
                r12.photoPressed = r4
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r5 = r12.currentBlock
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r12.parentAdapter
                r3.openPhoto(r5, r6)
                goto L_0x00e3
            L_0x00c8:
                int r3 = r12.buttonPressed
                if (r3 != r1) goto L_0x00e3
                r12.buttonPressed = r4
                r12.playSoundEffect(r4)
                r12.didPressedButton(r1)
                r12.invalidate()
                goto L_0x00e3
            L_0x00d8:
                int r3 = r13.getAction()
                r5 = 3
                if (r3 != r5) goto L_0x00e3
                r12.photoPressed = r4
                r12.buttonPressed = r4
            L_0x00e3:
                boolean r3 = r12.photoPressed
                if (r3 != 0) goto L_0x011b
                int r3 = r12.buttonPressed
                if (r3 != 0) goto L_0x011b
                org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r12.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r9 = r12.captionLayout
                int r10 = r12.textX
                int r11 = r12.textY
                r7 = r13
                r8 = r12
                boolean r3 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10, r11)
                if (r3 != 0) goto L_0x011b
                org.telegram.ui.ArticleViewer r5 = org.telegram.ui.ArticleViewer.this
                org.telegram.ui.ArticleViewer$WebpageAdapter r6 = r12.parentAdapter
                org.telegram.ui.ArticleViewer$DrawingText r9 = r12.creditLayout
                int r10 = r12.textX
                int r3 = r12.textY
                int r7 = r12.creditOffset
                int r11 = r3 + r7
                r7 = r13
                r8 = r12
                boolean r3 = r5.checkLayoutForLinks(r6, r7, r8, r9, r10, r11)
                if (r3 != 0) goto L_0x011b
                boolean r3 = super.onTouchEvent(r13)
                if (r3 == 0) goto L_0x011a
                goto L_0x011b
            L_0x011a:
                r1 = 0
            L_0x011b:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockPhotoCell.onTouchEvent(android.view.MotionEvent):boolean");
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x013e, code lost:
            r8 = r10.currentType;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r30, int r31) {
            /*
                r29 = this;
                r10 = r29
                int r0 = android.view.View.MeasureSpec.getSize(r30)
                r1 = 0
                int r2 = r10.currentType
                r11 = 2
                r12 = 1
                if (r2 != r12) goto L_0x0023
                android.view.ViewParent r2 = r29.getParent()
                android.view.View r2 = (android.view.View) r2
                int r0 = r2.getMeasuredWidth()
                android.view.ViewParent r2 = r29.getParent()
                android.view.View r2 = (android.view.View) r2
                int r1 = r2.getMeasuredHeight()
                r13 = r0
                goto L_0x0045
            L_0x0023:
                if (r2 != r11) goto L_0x0044
                org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = r10.groupPosition
                float r2 = r2.ph
                android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
                int r3 = r3.x
                android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
                int r4 = r4.y
                int r3 = java.lang.Math.max(r3, r4)
                float r3 = (float) r3
                float r2 = r2 * r3
                r3 = 1056964608(0x3var_, float:0.5)
                float r2 = r2 * r3
                double r2 = (double) r2
                double r2 = java.lang.Math.ceil(r2)
                int r1 = (int) r2
                r13 = r0
                goto L_0x0045
            L_0x0044:
                r13 = r0
            L_0x0045:
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r0 = r10.currentBlock
                if (r0 == 0) goto L_0x034f
                org.telegram.ui.ArticleViewer$WebpageAdapter r2 = r10.parentAdapter
                long r3 = r0.photo_id
                org.telegram.tgnet.TLRPC$Photo r0 = r2.getPhotoWithId(r3)
                r10.currentPhoto = r0
                r0 = 1111490560(0x42400000, float:48.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r0 = r13
                r2 = r1
                int r3 = r10.currentType
                r4 = 1099956224(0x41900000, float:18.0)
                if (r3 != 0) goto L_0x0083
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r3 = r10.currentBlock
                int r3 = r3.level
                if (r3 <= 0) goto L_0x0083
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r3 = r10.currentBlock
                int r3 = r3.level
                int r3 = r3 * 14
                float r3 = (float) r3
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r3 = r3 + r5
                r5 = r3
                r10.textX = r3
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r3 = r3 + r5
                int r0 = r0 - r3
                r3 = r0
                r15 = r3
                goto L_0x0093
            L_0x0083:
                r5 = 0
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
                r10.textX = r3
                r3 = 1108344832(0x42100000, float:36.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = r13 - r3
                r15 = r3
            L_0x0093:
                org.telegram.tgnet.TLRPC$Photo r3 = r10.currentPhoto
                r16 = 0
                r17 = 1090519040(0x41000000, float:8.0)
                if (r3 == 0) goto L_0x0252
                org.telegram.tgnet.TLRPC$PhotoSize r4 = r10.currentPhotoObject
                if (r4 == 0) goto L_0x0252
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.sizes
                r4 = 40
                org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4, r12)
                r10.currentPhotoObjectThumb = r3
                org.telegram.tgnet.TLRPC$PhotoSize r4 = r10.currentPhotoObject
                r6 = 0
                if (r4 != r3) goto L_0x00b0
                r10.currentPhotoObjectThumb = r6
            L_0x00b0:
                int r3 = r10.currentType
                r7 = 1073741824(0x40000000, float:2.0)
                if (r3 != 0) goto L_0x0102
                float r3 = (float) r0
                int r4 = r4.w
                float r4 = (float) r4
                float r3 = r3 / r4
                org.telegram.tgnet.TLRPC$PhotoSize r4 = r10.currentPhotoObject
                int r4 = r4.h
                float r4 = (float) r4
                float r4 = r4 * r3
                int r1 = (int) r4
                org.telegram.tgnet.TLRPC$PageBlock r4 = r10.parentBlock
                boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCover
                if (r4 == 0) goto L_0x00ce
                int r1 = java.lang.Math.min(r1, r0)
                goto L_0x0100
            L_0x00ce:
                android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
                int r4 = r4.x
                android.graphics.Point r8 = org.telegram.messenger.AndroidUtilities.displaySize
                int r8 = r8.y
                int r4 = java.lang.Math.max(r4, r8)
                r8 = 1113587712(0x42600000, float:56.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r4 = r4 - r8
                float r4 = (float) r4
                r8 = 1063675494(0x3var_, float:0.9)
                float r4 = r4 * r8
                int r4 = (int) r4
                if (r1 <= r4) goto L_0x0100
                r1 = r4
                float r8 = (float) r1
                org.telegram.tgnet.TLRPC$PhotoSize r9 = r10.currentPhotoObject
                int r9 = r9.h
                float r9 = (float) r9
                float r3 = r8 / r9
                org.telegram.tgnet.TLRPC$PhotoSize r8 = r10.currentPhotoObject
                int r8 = r8.w
                float r8 = (float) r8
                float r8 = r8 * r3
                int r0 = (int) r8
                int r8 = r13 - r5
                int r8 = r8 - r0
                int r8 = r8 / r11
                int r5 = r5 + r8
            L_0x0100:
                r2 = r1
                goto L_0x0136
            L_0x0102:
                if (r3 != r11) goto L_0x0136
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = r10.groupPosition
                int r3 = r3.flags
                r3 = r3 & r11
                if (r3 != 0) goto L_0x0110
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r0 = r0 - r3
            L_0x0110:
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = r10.groupPosition
                int r3 = r3.flags
                r3 = r3 & 8
                if (r3 != 0) goto L_0x011d
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r2 = r2 - r3
            L_0x011d:
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = r10.groupPosition
                int r3 = r3.leftSpanOffset
                if (r3 == 0) goto L_0x0137
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = r10.groupPosition
                int r3 = r3.leftSpanOffset
                int r3 = r3 * r13
                float r3 = (float) r3
                r4 = 1148846080(0x447a0000, float:1000.0)
                float r3 = r3 / r4
                double r3 = (double) r3
                double r3 = java.lang.Math.ceil(r3)
                int r3 = (int) r3
                int r0 = r0 - r3
                int r5 = r5 + r3
                goto L_0x0137
            L_0x0136:
            L_0x0137:
                org.telegram.messenger.ImageReceiver r3 = r10.imageView
                float r4 = (float) r5
                boolean r8 = r10.isFirst
                if (r8 != 0) goto L_0x0151
                int r8 = r10.currentType
                if (r8 == r12) goto L_0x0151
                if (r8 == r11) goto L_0x0151
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r8 = r10.currentBlock
                int r8 = r8.level
                if (r8 <= 0) goto L_0x014b
                goto L_0x0151
            L_0x014b:
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r17)
                float r8 = (float) r8
                goto L_0x0152
            L_0x0151:
                r8 = 0
            L_0x0152:
                float r9 = (float) r0
                float r7 = (float) r2
                r3.setImageCoords(r4, r8, r9, r7)
                int r3 = r10.currentType
                if (r3 != 0) goto L_0x015e
                r10.currentFilter = r6
                goto L_0x0176
            L_0x015e:
                java.util.Locale r3 = java.util.Locale.US
                java.lang.Object[] r4 = new java.lang.Object[r11]
                java.lang.Integer r7 = java.lang.Integer.valueOf(r0)
                r4[r16] = r7
                java.lang.Integer r7 = java.lang.Integer.valueOf(r2)
                r4[r12] = r7
                java.lang.String r7 = "%d_%d"
                java.lang.String r3 = java.lang.String.format(r3, r7, r4)
                r10.currentFilter = r3
            L_0x0176:
                java.lang.String r3 = "80_80_b"
                r10.currentThumbFilter = r3
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                int r3 = r3.currentAccount
                org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
                int r3 = r3.getCurrentDownloadMask()
                r3 = r3 & r12
                if (r3 == 0) goto L_0x018d
                r3 = 1
                goto L_0x018e
            L_0x018d:
                r3 = 0
            L_0x018e:
                r10.autoDownload = r3
                org.telegram.ui.ArticleViewer r3 = org.telegram.ui.ArticleViewer.this
                int r3 = r3.currentAccount
                org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r3)
                org.telegram.tgnet.TLRPC$PhotoSize r4 = r10.currentPhotoObject
                java.io.File r3 = r3.getPathToAttach(r4, r12)
                boolean r4 = r10.autoDownload
                if (r4 != 0) goto L_0x01e3
                boolean r4 = r3.exists()
                if (r4 == 0) goto L_0x01ab
                goto L_0x01e3
            L_0x01ab:
                org.telegram.messenger.ImageReceiver r4 = r10.imageView
                org.telegram.tgnet.TLRPC$PhotoSize r6 = r10.currentPhotoObject
                org.telegram.tgnet.TLRPC$Photo r7 = r10.currentPhoto
                org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r6, (org.telegram.tgnet.TLRPC.Photo) r7)
                r4.setStrippedLocation(r6)
                org.telegram.messenger.ImageReceiver r4 = r10.imageView
                r20 = 0
                java.lang.String r6 = r10.currentFilter
                org.telegram.tgnet.TLRPC$PhotoSize r7 = r10.currentPhotoObjectThumb
                org.telegram.tgnet.TLRPC$Photo r8 = r10.currentPhoto
                org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r7, (org.telegram.tgnet.TLRPC.Photo) r8)
                java.lang.String r7 = r10.currentThumbFilter
                org.telegram.tgnet.TLRPC$PhotoSize r8 = r10.currentPhotoObject
                int r8 = r8.size
                long r8 = (long) r8
                r26 = 0
                org.telegram.ui.ArticleViewer$WebpageAdapter r11 = r10.parentAdapter
                org.telegram.tgnet.TLRPC$WebPage r27 = r11.currentPage
                r28 = 1
                r19 = r4
                r21 = r6
                r23 = r7
                r24 = r8
                r19.setImage(r20, r21, r22, r23, r24, r26, r27, r28)
                goto L_0x0218
            L_0x01e3:
                org.telegram.messenger.ImageReceiver r4 = r10.imageView
                r4.setStrippedLocation(r6)
                org.telegram.messenger.ImageReceiver r4 = r10.imageView
                org.telegram.tgnet.TLRPC$PhotoSize r6 = r10.currentPhotoObject
                org.telegram.tgnet.TLRPC$Photo r7 = r10.currentPhoto
                org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r6, (org.telegram.tgnet.TLRPC.Photo) r7)
                java.lang.String r6 = r10.currentFilter
                org.telegram.tgnet.TLRPC$PhotoSize r7 = r10.currentPhotoObjectThumb
                org.telegram.tgnet.TLRPC$Photo r8 = r10.currentPhoto
                org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r7, (org.telegram.tgnet.TLRPC.Photo) r8)
                java.lang.String r7 = r10.currentThumbFilter
                org.telegram.tgnet.TLRPC$PhotoSize r8 = r10.currentPhotoObject
                int r8 = r8.size
                long r8 = (long) r8
                r26 = 0
                org.telegram.ui.ArticleViewer$WebpageAdapter r11 = r10.parentAdapter
                org.telegram.tgnet.TLRPC$WebPage r27 = r11.currentPage
                r28 = 1
                r19 = r4
                r21 = r6
                r23 = r7
                r24 = r8
                r19.setImage(r20, r21, r22, r23, r24, r26, r27, r28)
            L_0x0218:
                org.telegram.messenger.ImageReceiver r4 = r10.imageView
                float r4 = r4.getImageX()
                org.telegram.messenger.ImageReceiver r6 = r10.imageView
                float r6 = r6.getImageWidth()
                float r7 = (float) r14
                float r6 = r6 - r7
                r7 = 1073741824(0x40000000, float:2.0)
                float r6 = r6 / r7
                float r4 = r4 + r6
                int r4 = (int) r4
                r10.buttonX = r4
                org.telegram.messenger.ImageReceiver r4 = r10.imageView
                float r4 = r4.getImageY()
                org.telegram.messenger.ImageReceiver r6 = r10.imageView
                float r6 = r6.getImageHeight()
                float r8 = (float) r14
                float r6 = r6 - r8
                float r6 = r6 / r7
                float r4 = r4 + r6
                int r4 = (int) r4
                r10.buttonY = r4
                org.telegram.ui.Components.RadialProgress2 r6 = r10.radialProgress
                int r7 = r10.buttonX
                int r8 = r7 + r14
                int r9 = r4 + r14
                r6.setProgressRect(r7, r4, r8, r9)
                r11 = r0
                r8 = r1
                r18 = r2
                r19 = r5
                goto L_0x0258
            L_0x0252:
                r11 = r0
                r8 = r1
                r18 = r2
                r19 = r5
            L_0x0258:
                org.telegram.messenger.ImageReceiver r0 = r10.imageView
                float r0 = r0.getImageY()
                org.telegram.messenger.ImageReceiver r1 = r10.imageView
                float r1 = r1.getImageHeight()
                float r0 = r0 + r1
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
                float r1 = (float) r1
                float r0 = r0 + r1
                int r0 = (int) r0
                r10.textY = r0
                int r0 = r10.currentType
                if (r0 != 0) goto L_0x02e5
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r1 = r10.currentBlock
                org.telegram.tgnet.TLRPC$TL_pageCaption r1 = r1.caption
                org.telegram.tgnet.TLRPC$RichText r3 = r1.text
                int r5 = r10.textY
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r6 = r10.currentBlock
                org.telegram.ui.ArticleViewer$WebpageAdapter r7 = r10.parentAdapter
                r1 = r29
                r4 = r15
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7)
                r10.captionLayout = r0
                r20 = 1082130432(0x40800000, float:4.0)
                if (r0 == 0) goto L_0x02a4
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
                org.telegram.ui.ArticleViewer$DrawingText r1 = r10.captionLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                r10.creditOffset = r0
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r20)
                int r0 = r0 + r1
                int r8 = r8 + r0
                r21 = r8
                goto L_0x02a6
            L_0x02a4:
                r21 = r8
            L_0x02a6:
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r1 = r10.currentBlock
                org.telegram.tgnet.TLRPC$TL_pageCaption r1 = r1.caption
                org.telegram.tgnet.TLRPC$RichText r3 = r1.credit
                int r1 = r10.textY
                int r4 = r10.creditOffset
                int r5 = r1 + r4
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r6 = r10.currentBlock
                org.telegram.ui.ArticleViewer$WebpageAdapter r1 = r10.parentAdapter
                boolean r1 = r1.isRtl
                if (r1 == 0) goto L_0x02c4
                android.text.Layout$Alignment r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT()
                goto L_0x02c6
            L_0x02c4:
                android.text.Layout$Alignment r1 = android.text.Layout.Alignment.ALIGN_NORMAL
            L_0x02c6:
                r7 = r1
                r8 = 0
                org.telegram.ui.ArticleViewer$WebpageAdapter r9 = r10.parentAdapter
                r1 = r29
                r4 = r15
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8, r9)
                r10.creditLayout = r0
                if (r0 == 0) goto L_0x02e3
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r20)
                org.telegram.ui.ArticleViewer$DrawingText r1 = r10.creditLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                int r8 = r21 + r0
                goto L_0x02e5
            L_0x02e3:
                r8 = r21
            L_0x02e5:
                boolean r0 = r10.isFirst
                if (r0 != 0) goto L_0x02f8
                int r0 = r10.currentType
                if (r0 != 0) goto L_0x02f8
                org.telegram.tgnet.TLRPC$TL_pageBlockPhoto r0 = r10.currentBlock
                int r0 = r0.level
                if (r0 > 0) goto L_0x02f8
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
                int r8 = r8 + r0
            L_0x02f8:
                org.telegram.tgnet.TLRPC$PageBlock r0 = r10.parentBlock
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCover
                if (r0 == 0) goto L_0x0321
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r10.parentAdapter
                java.util.ArrayList r0 = r0.blocks
                if (r0 == 0) goto L_0x0321
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r10.parentAdapter
                java.util.ArrayList r0 = r0.blocks
                int r0 = r0.size()
                if (r0 <= r12) goto L_0x0321
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r10.parentAdapter
                java.util.ArrayList r0 = r0.blocks
                java.lang.Object r0 = r0.get(r12)
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockChannel
                if (r0 == 0) goto L_0x0321
                goto L_0x0322
            L_0x0321:
                r12 = 0
            L_0x0322:
                r0 = r12
                int r1 = r10.currentType
                r2 = 2
                if (r1 == r2) goto L_0x032f
                if (r0 != 0) goto L_0x032f
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
                int r8 = r8 + r1
            L_0x032f:
                org.telegram.ui.ArticleViewer$DrawingText r1 = r10.captionLayout
                if (r1 == 0) goto L_0x033d
                int r2 = r10.textX
                r1.x = r2
                org.telegram.ui.ArticleViewer$DrawingText r1 = r10.captionLayout
                int r2 = r10.textY
                r1.y = r2
            L_0x033d:
                org.telegram.ui.ArticleViewer$DrawingText r1 = r10.creditLayout
                if (r1 == 0) goto L_0x034e
                int r2 = r10.textX
                r1.x = r2
                org.telegram.ui.ArticleViewer$DrawingText r1 = r10.creditLayout
                int r2 = r10.textY
                int r3 = r10.creditOffset
                int r2 = r2 + r3
                r1.y = r2
            L_0x034e:
                goto L_0x0350
            L_0x034f:
                r8 = 1
            L_0x0350:
                org.telegram.ui.ArticleViewer$BlockChannelCell r0 = r10.channelCell
                r1 = r30
                r2 = r31
                r0.measure(r1, r2)
                org.telegram.ui.ArticleViewer$BlockChannelCell r0 = r10.channelCell
                org.telegram.messenger.ImageReceiver r3 = r10.imageView
                float r3 = r3.getImageHeight()
                r4 = 1109131264(0x421CLASSNAME, float:39.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r4 = (float) r4
                float r3 = r3 - r4
                r0.setTranslationY(r3)
                r10.setMeasuredDimension(r13, r8)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockPhotoCell.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
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
                    int x = getMeasuredWidth() - AndroidUtilities.dp(35.0f);
                    int y = (int) (this.imageView.getImageY() + ((float) AndroidUtilities.dp(11.0f)));
                    this.linkDrawable.setBounds(x, y, AndroidUtilities.dp(24.0f) + x, AndroidUtilities.dp(24.0f) + y);
                    this.linkDrawable.draw(canvas);
                }
                int count = 0;
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.captionLayout.draw(canvas, this);
                    canvas.restore();
                    count = 0 + 1;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, count);
                    this.creditLayout.draw(canvas, this);
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
            return 4;
        }

        private void didPressedButton(boolean animated) {
            int i = this.buttonState;
            if (i == 0) {
                this.radialProgress.setProgress(0.0f, animated);
                this.imageView.setImage(ImageLocation.getForPhoto(this.currentPhotoObject, this.currentPhoto), this.currentFilter, ImageLocation.getForPhoto(this.currentPhotoObjectThumb, this.currentPhoto), this.currentThumbFilter, (long) this.currentPhotoObject.size, (String) null, this.parentAdapter.currentPage, 1);
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                invalidate();
            } else if (i == 1) {
                this.imageView.cancelLoadImage();
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
                invalidate();
            }
        }

        public void updateButtonState(boolean animated) {
            String fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
            boolean fileExists = FileLoader.getInstance(ArticleViewer.this.currentAccount).getPathToAttach(this.currentPhotoObject, true).exists();
            if (TextUtils.isEmpty(fileName)) {
                this.radialProgress.setIcon(4, false, false);
                return;
            }
            if (fileExists) {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).removeLoadingFileObserver(this);
                this.buttonState = -1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, animated);
            } else {
                DownloadController.getInstance(ArticleViewer.this.currentAccount).addLoadingFileObserver(fileName, (MessageObject) null, this);
                float setProgress = 0.0f;
                if (this.autoDownload || FileLoader.getInstance(ArticleViewer.this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 1;
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    setProgress = progress != null ? progress.floatValue() : 0.0f;
                } else {
                    this.buttonState = 0;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), true, animated);
                this.radialProgress.setProgress(setProgress, false);
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

        public void onFailedDownload(String fileName, boolean canceled) {
            updateButtonState(false);
        }

        public void onSuccessDownload(String fileName) {
            this.radialProgress.setProgress(1.0f, true);
            updateButtonState(true);
        }

        public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
        }

        public void onProgressDownload(String fileName, long downloadSize, long totalSize) {
            this.radialProgress.setProgress(Math.min(1.0f, ((float) downloadSize) / ((float) totalSize)), true);
            if (this.buttonState != 1) {
                updateButtonState(true);
            }
        }

        public int getObserverTag() {
            return this.TAG;
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            StringBuilder sb = new StringBuilder(LocaleController.getString("AttachPhoto", NUM));
            if (this.captionLayout != null) {
                sb.append(", ");
                sb.append(this.captionLayout.getText());
            }
            info.setText(sb.toString());
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    private class BlockMapCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private DrawingText captionLayout;
        private DrawingText creditLayout;
        private int creditOffset;
        private TLRPC.TL_pageBlockMap currentBlock;
        private int currentMapProvider;
        private int currentType;
        private ImageReceiver imageView = new ImageReceiver(this);
        private boolean isFirst;
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

        public void setBlock(TLRPC.TL_pageBlockMap block, boolean first, boolean last) {
            this.currentBlock = block;
            this.isFirst = first;
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
                    Activity access$2400 = ArticleViewer.this.parentActivity;
                    access$2400.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon)));
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (event.getAction() == 3) {
                this.photoPressed = false;
            }
            if (!this.photoPressed) {
                if (!ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.captionLayout, this.textX, this.textY)) {
                    if (ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.creditLayout, this.textX, this.textY + this.creditOffset) || super.onTouchEvent(event)) {
                        return true;
                    }
                    return false;
                }
            }
            return true;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x00bc, code lost:
            r2 = r9.currentType;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMeasure(int r34, int r35) {
            /*
                r33 = this;
                r9 = r33
                int r0 = android.view.View.MeasureSpec.getSize(r34)
                r1 = 0
                int r2 = r9.currentType
                r3 = 1
                r10 = 2
                if (r2 != r3) goto L_0x0023
                android.view.ViewParent r2 = r33.getParent()
                android.view.View r2 = (android.view.View) r2
                int r0 = r2.getMeasuredWidth()
                android.view.ViewParent r2 = r33.getParent()
                android.view.View r2 = (android.view.View) r2
                int r1 = r2.getMeasuredHeight()
                r11 = r0
                goto L_0x0029
            L_0x0023:
                if (r2 != r10) goto L_0x0028
                r1 = r0
                r11 = r0
                goto L_0x0029
            L_0x0028:
                r11 = r0
            L_0x0029:
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r0 = r9.currentBlock
                if (r0 == 0) goto L_0x0220
                r2 = r11
                int r4 = r9.currentType
                r5 = 1099956224(0x41900000, float:18.0)
                if (r4 != 0) goto L_0x0054
                int r0 = r0.level
                if (r0 <= 0) goto L_0x0054
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r0 = r9.currentBlock
                int r0 = r0.level
                int r0 = r0 * 14
                float r0 = (float) r0
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r0 = r0 + r4
                r4 = r0
                r9.textX = r0
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r0 = r0 + r4
                int r2 = r2 - r0
                r0 = r2
                r12 = r0
                goto L_0x0064
            L_0x0054:
                r4 = 0
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r9.textX = r0
                r0 = 1108344832(0x42100000, float:36.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r0 = r11 - r0
                r12 = r0
            L_0x0064:
                int r0 = r9.currentType
                if (r0 != 0) goto L_0x00b0
                float r0 = (float) r2
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r5 = r9.currentBlock
                int r5 = r5.w
                float r5 = (float) r5
                float r0 = r0 / r5
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r5 = r9.currentBlock
                int r5 = r5.h
                float r5 = (float) r5
                float r5 = r5 * r0
                int r1 = (int) r5
                android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
                int r5 = r5.x
                android.graphics.Point r6 = org.telegram.messenger.AndroidUtilities.displaySize
                int r6 = r6.y
                int r5 = java.lang.Math.max(r5, r6)
                r6 = 1113587712(0x42600000, float:56.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r5 = r5 - r6
                float r5 = (float) r5
                r6 = 1063675494(0x3var_, float:0.9)
                float r5 = r5 * r6
                int r5 = (int) r5
                if (r1 <= r5) goto L_0x00ac
                r1 = r5
                float r6 = (float) r1
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r7 = r9.currentBlock
                int r7 = r7.h
                float r7 = (float) r7
                float r6 = r6 / r7
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r0 = r9.currentBlock
                int r0 = r0.w
                float r0 = (float) r0
                float r0 = r0 * r6
                int r2 = (int) r0
                int r0 = r11 - r4
                int r0 = r0 - r2
                int r0 = r0 / r10
                int r4 = r4 + r0
                r8 = r1
                r13 = r2
                r14 = r4
                goto L_0x00b3
            L_0x00ac:
                r8 = r1
                r13 = r2
                r14 = r4
                goto L_0x00b3
            L_0x00b0:
                r8 = r1
                r13 = r2
                r14 = r4
            L_0x00b3:
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                float r1 = (float) r14
                boolean r2 = r9.isFirst
                r15 = 1090519040(0x41000000, float:8.0)
                if (r2 != 0) goto L_0x00cf
                int r2 = r9.currentType
                if (r2 == r3) goto L_0x00cf
                if (r2 == r10) goto L_0x00cf
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r2 = r9.currentBlock
                int r2 = r2.level
                if (r2 <= 0) goto L_0x00c9
                goto L_0x00cf
            L_0x00c9:
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
                float r2 = (float) r2
                goto L_0x00d0
            L_0x00cf:
                r2 = 0
            L_0x00d0:
                float r3 = (float) r13
                float r4 = (float) r8
                r0.setImageCoords(r1, r2, r3, r4)
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                int r16 = r0.currentAccount
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r0 = r9.currentBlock
                org.telegram.tgnet.TLRPC$GeoPoint r0 = r0.geo
                double r0 = r0.lat
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r2 = r9.currentBlock
                org.telegram.tgnet.TLRPC$GeoPoint r2 = r2.geo
                double r2 = r2._long
                float r4 = (float) r13
                float r5 = org.telegram.messenger.AndroidUtilities.density
                float r4 = r4 / r5
                int r4 = (int) r4
                float r5 = (float) r8
                float r6 = org.telegram.messenger.AndroidUtilities.density
                float r5 = r5 / r6
                int r5 = (int) r5
                r23 = 1
                r24 = 15
                r25 = -1
                r17 = r0
                r19 = r2
                r21 = r4
                r22 = r5
                java.lang.String r16 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r16, r17, r19, r21, r22, r23, r24, r25)
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r0 = r9.currentBlock
                org.telegram.tgnet.TLRPC$GeoPoint r0 = r0.geo
                float r1 = (float) r13
                float r2 = org.telegram.messenger.AndroidUtilities.density
                float r1 = r1 / r2
                int r1 = (int) r1
                float r2 = (float) r8
                float r3 = org.telegram.messenger.AndroidUtilities.density
                float r2 = r2 / r3
                int r2 = (int) r2
                r3 = 15
                float r4 = org.telegram.messenger.AndroidUtilities.density
                double r4 = (double) r4
                double r4 = java.lang.Math.ceil(r4)
                int r4 = (int) r4
                int r4 = java.lang.Math.min(r10, r4)
                org.telegram.messenger.WebFile r17 = org.telegram.messenger.WebFile.createWithGeoPoint(r0, r1, r2, r3, r4)
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                int r0 = r0.currentAccount
                org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
                int r0 = r0.mapProvider
                r9.currentMapProvider = r0
                if (r0 != r10) goto L_0x0149
                if (r17 == 0) goto L_0x015c
                org.telegram.messenger.ImageReceiver r1 = r9.imageView
                org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForWebFile(r17)
                r3 = 0
                r4 = 0
                r5 = 0
                org.telegram.ui.ArticleViewer$WebpageAdapter r0 = r9.parentAdapter
                org.telegram.tgnet.TLRPC$WebPage r6 = r0.currentPage
                r7 = 0
                r1.setImage(r2, r3, r4, r5, r6, r7)
                goto L_0x015c
            L_0x0149:
                if (r16 == 0) goto L_0x015c
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                r28 = 0
                r29 = 0
                r30 = 0
                r31 = 0
                r26 = r0
                r27 = r16
                r26.setImage(r27, r28, r29, r30, r31)
            L_0x015c:
                org.telegram.messenger.ImageReceiver r0 = r9.imageView
                float r0 = r0.getImageY()
                org.telegram.messenger.ImageReceiver r1 = r9.imageView
                float r1 = r1.getImageHeight()
                float r0 = r0 + r1
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
                float r1 = (float) r1
                float r0 = r0 + r1
                int r0 = (int) r0
                r9.textY = r0
                int r0 = r9.currentType
                if (r0 != 0) goto L_0x0203
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r1 = r9.currentBlock
                org.telegram.tgnet.TLRPC$TL_pageCaption r1 = r1.caption
                org.telegram.tgnet.TLRPC$RichText r3 = r1.text
                int r5 = r9.textY
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r6 = r9.currentBlock
                org.telegram.ui.ArticleViewer$WebpageAdapter r7 = r9.parentAdapter
                r1 = r33
                r4 = r12
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7)
                r9.captionLayout = r0
                r18 = 1082130432(0x40800000, float:4.0)
                if (r0 == 0) goto L_0x01b4
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
                org.telegram.ui.ArticleViewer$DrawingText r1 = r9.captionLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                r9.creditOffset = r0
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
                int r0 = r0 + r1
                int r8 = r8 + r0
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.captionLayout
                int r1 = r9.textX
                r0.x = r1
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.captionLayout
                int r1 = r9.textY
                r0.y = r1
                r19 = r8
                goto L_0x01b6
            L_0x01b4:
                r19 = r8
            L_0x01b6:
                org.telegram.ui.ArticleViewer r0 = org.telegram.ui.ArticleViewer.this
                r2 = 0
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r1 = r9.currentBlock
                org.telegram.tgnet.TLRPC$TL_pageCaption r1 = r1.caption
                org.telegram.tgnet.TLRPC$RichText r3 = r1.credit
                int r1 = r9.textY
                int r4 = r9.creditOffset
                int r5 = r1 + r4
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r6 = r9.currentBlock
                org.telegram.ui.ArticleViewer$WebpageAdapter r1 = r9.parentAdapter
                boolean r1 = r1.isRtl
                if (r1 == 0) goto L_0x01d4
                android.text.Layout$Alignment r1 = org.telegram.ui.Components.StaticLayoutEx.ALIGN_RIGHT()
                goto L_0x01d6
            L_0x01d4:
                android.text.Layout$Alignment r1 = android.text.Layout.Alignment.ALIGN_NORMAL
            L_0x01d6:
                r7 = r1
                org.telegram.ui.ArticleViewer$WebpageAdapter r8 = r9.parentAdapter
                r1 = r33
                r4 = r12
                org.telegram.ui.ArticleViewer$DrawingText r0 = r0.createLayoutForText(r1, r2, r3, r4, r5, r6, r7, r8)
                r9.creditLayout = r0
                if (r0 == 0) goto L_0x0201
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
                org.telegram.ui.ArticleViewer$DrawingText r1 = r9.creditLayout
                int r1 = r1.getHeight()
                int r0 = r0 + r1
                int r8 = r19 + r0
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.creditLayout
                int r1 = r9.textX
                r0.x = r1
                org.telegram.ui.ArticleViewer$DrawingText r0 = r9.creditLayout
                int r1 = r9.textY
                int r2 = r9.creditOffset
                int r1 = r1 + r2
                r0.y = r1
                goto L_0x0203
            L_0x0201:
                r8 = r19
            L_0x0203:
                boolean r0 = r9.isFirst
                if (r0 != 0) goto L_0x0216
                int r0 = r9.currentType
                if (r0 != 0) goto L_0x0216
                org.telegram.tgnet.TLRPC$TL_pageBlockMap r0 = r9.currentBlock
                int r0 = r0.level
                if (r0 > 0) goto L_0x0216
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r8 = r8 + r0
            L_0x0216:
                int r0 = r9.currentType
                if (r0 == r10) goto L_0x021f
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r8 = r8 + r0
            L_0x021f:
                goto L_0x0221
            L_0x0220:
                r8 = 1
            L_0x0221:
                r9.setMeasuredDimension(r11, r8)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ArticleViewer.BlockMapCell.onMeasure(int, int):void");
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                Theme.chat_docBackPaint.setColor(Theme.getColor("chat_inLocationBackground"));
                canvas.drawRect(this.imageView.getImageX(), this.imageView.getImageY(), this.imageView.getImageX2(), this.imageView.getImageY2(), Theme.chat_docBackPaint);
                int i = 0;
                int left = (int) (this.imageView.getCenterX() - ((float) (Theme.chat_locationDrawable[0].getIntrinsicWidth() / 2)));
                int top = (int) (this.imageView.getCenterY() - ((float) (Theme.chat_locationDrawable[0].getIntrinsicHeight() / 2)));
                Theme.chat_locationDrawable[0].setBounds(left, top, Theme.chat_locationDrawable[0].getIntrinsicWidth() + left, Theme.chat_locationDrawable[0].getIntrinsicHeight() + top);
                Theme.chat_locationDrawable[0].draw(canvas);
                this.imageView.draw(canvas);
                if (this.currentMapProvider == 2 && this.imageView.hasNotThumb()) {
                    int w = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicWidth()) * 0.8f);
                    int h = (int) (((float) Theme.chat_redLocationIcon.getIntrinsicHeight()) * 0.8f);
                    int x = (int) (this.imageView.getImageX() + ((this.imageView.getImageWidth() - ((float) w)) / 2.0f));
                    int y = (int) (this.imageView.getImageY() + ((this.imageView.getImageHeight() / 2.0f) - ((float) h)));
                    Theme.chat_redLocationIcon.setAlpha((int) (this.imageView.getCurrentAlpha() * 255.0f));
                    Theme.chat_redLocationIcon.setBounds(x, y, x + w, y + h);
                    Theme.chat_redLocationIcon.draw(canvas);
                }
                int count = 0;
                if (this.captionLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this, 0);
                    this.captionLayout.draw(canvas, this);
                    canvas.restore();
                    count = 0 + 1;
                }
                if (this.creditLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) (this.textY + this.creditOffset));
                    ArticleViewer.this.drawTextSelection(canvas, this, count);
                    this.creditLayout.draw(canvas, this);
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            StringBuilder sb = new StringBuilder(LocaleController.getString("Map", NUM));
            if (this.captionLayout != null) {
                sb.append(", ");
                sb.append(this.captionLayout.getText());
            }
            info.setText(sb.toString());
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.captionLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
            DrawingText drawingText2 = this.creditLayout;
            if (drawingText2 != null) {
                blocks.add(drawingText2);
            }
        }
    }

    private class BlockChannelCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        private Paint backgroundPaint;
        private int buttonWidth;
        private AnimatorSet currentAnimation;
        private TLRPC.TL_pageBlockChannel currentBlock;
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
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setText(LocaleController.getString("ChannelJoin", NUM));
            this.textView.setGravity(19);
            addView(this.textView, LayoutHelper.createFrame(-2, 39, 53));
            this.textView.setOnClickListener(new ArticleViewer$BlockChannelCell$$ExternalSyntheticLambda0(this));
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setImageResource(NUM);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(39, 39, 53));
            ContextProgressView contextProgressView = new ContextProgressView(context, 0);
            this.progressView = contextProgressView;
            addView(contextProgressView, LayoutHelper.createFrame(39, 39, 53));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ArticleViewer$BlockChannelCell  reason: not valid java name */
        public /* synthetic */ void m2709lambda$new$0$orgtelegramuiArticleViewer$BlockChannelCell(View v) {
            if (this.currentState == 0) {
                setState(1, true);
                ArticleViewer articleViewer = ArticleViewer.this;
                articleViewer.joinChannel(this, articleViewer.loadedChannel);
            }
        }

        public void setBlock(TLRPC.TL_pageBlockChannel block) {
            this.currentBlock = block;
            if (this.currentType == 0) {
                int color = Theme.getColor("switchTrack");
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                this.textView.setTextColor(ArticleViewer.this.getLinkTextColor());
                this.backgroundPaint.setColor(Color.argb(34, r, g, b));
                this.imageView.setColorFilter(new PorterDuffColorFilter(ArticleViewer.getGrayTextColor(), PorterDuff.Mode.MULTIPLY));
            } else {
                this.textView.setTextColor(-1);
                this.backgroundPaint.setColor(NUM);
                this.imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
            }
            TLRPC.Chat channel = MessagesController.getInstance(ArticleViewer.this.currentAccount).getChat(Long.valueOf(block.channel.id));
            if (channel == null || channel.min) {
                ArticleViewer.this.loadChannel(this, this.parentAdapter, block.channel);
                setState(1, false);
            } else {
                TLRPC.Chat unused = ArticleViewer.this.loadedChannel = channel;
                if (!channel.left || channel.kicked) {
                    setState(4, false);
                } else {
                    setState(0, false);
                }
            }
            requestLayout();
        }

        public void setState(int state, boolean animated) {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.currentState = state;
            float f = 0.0f;
            float f2 = 0.1f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.currentAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[9];
                TextView textView2 = this.textView;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = state == 0 ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(textView2, property, fArr);
                TextView textView3 = this.textView;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = state == 0 ? 1.0f : 0.1f;
                animatorArr[1] = ObjectAnimator.ofFloat(textView3, property2, fArr2);
                TextView textView4 = this.textView;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                fArr3[0] = state == 0 ? 1.0f : 0.1f;
                animatorArr[2] = ObjectAnimator.ofFloat(textView4, property3, fArr3);
                ContextProgressView contextProgressView = this.progressView;
                Property property4 = View.ALPHA;
                float[] fArr4 = new float[1];
                fArr4[0] = state == 1 ? 1.0f : 0.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(contextProgressView, property4, fArr4);
                ContextProgressView contextProgressView2 = this.progressView;
                Property property5 = View.SCALE_X;
                float[] fArr5 = new float[1];
                fArr5[0] = state == 1 ? 1.0f : 0.1f;
                animatorArr[4] = ObjectAnimator.ofFloat(contextProgressView2, property5, fArr5);
                ContextProgressView contextProgressView3 = this.progressView;
                Property property6 = View.SCALE_Y;
                float[] fArr6 = new float[1];
                fArr6[0] = state == 1 ? 1.0f : 0.1f;
                animatorArr[5] = ObjectAnimator.ofFloat(contextProgressView3, property6, fArr6);
                ImageView imageView2 = this.imageView;
                Property property7 = View.ALPHA;
                float[] fArr7 = new float[1];
                if (state == 2) {
                    f = 1.0f;
                }
                fArr7[0] = f;
                animatorArr[6] = ObjectAnimator.ofFloat(imageView2, property7, fArr7);
                ImageView imageView3 = this.imageView;
                Property property8 = View.SCALE_X;
                float[] fArr8 = new float[1];
                fArr8[0] = state == 2 ? 1.0f : 0.1f;
                animatorArr[7] = ObjectAnimator.ofFloat(imageView3, property8, fArr8);
                ImageView imageView4 = this.imageView;
                Property property9 = View.SCALE_Y;
                float[] fArr9 = new float[1];
                if (state == 2) {
                    f2 = 1.0f;
                }
                fArr9[0] = f2;
                animatorArr[8] = ObjectAnimator.ofFloat(imageView4, property9, fArr9);
                animatorSet2.playTogether(animatorArr);
                this.currentAnimation.setDuration(150);
                this.currentAnimation.start();
                return;
            }
            this.textView.setAlpha(state == 0 ? 1.0f : 0.0f);
            this.textView.setScaleX(state == 0 ? 1.0f : 0.1f);
            this.textView.setScaleY(state == 0 ? 1.0f : 0.1f);
            this.progressView.setAlpha(state == 1 ? 1.0f : 0.0f);
            this.progressView.setScaleX(state == 1 ? 1.0f : 0.1f);
            this.progressView.setScaleY(state == 1 ? 1.0f : 0.1f);
            ImageView imageView5 = this.imageView;
            if (state == 2) {
                f = 1.0f;
            }
            imageView5.setAlpha(f);
            this.imageView.setScaleX(state == 2 ? 1.0f : 0.1f);
            ImageView imageView6 = this.imageView;
            if (state == 2) {
                f2 = 1.0f;
            }
            imageView6.setScaleY(f2);
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (this.currentType != 0) {
                return super.onTouchEvent(event);
            }
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width, AndroidUtilities.dp(48.0f));
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            this.buttonWidth = this.textView.getMeasuredWidth();
            this.progressView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(39.0f), NUM));
            TLRPC.TL_pageBlockChannel tL_pageBlockChannel = this.currentBlock;
            if (tL_pageBlockChannel != null) {
                this.textLayout = ArticleViewer.this.createLayoutForText(this, tL_pageBlockChannel.channel.title, (TLRPC.RichText) null, (width - AndroidUtilities.dp(52.0f)) - this.buttonWidth, this.textY, this.currentBlock, StaticLayoutEx.ALIGN_LEFT(), this.parentAdapter);
                if (this.parentAdapter.isRtl) {
                    this.textX2 = this.textX;
                } else {
                    this.textX2 = (getMeasuredWidth() - this.textX) - this.buttonWidth;
                }
                DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    drawingText.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            this.imageView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, this.textX2 + (this.buttonWidth / 2) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            this.progressView.layout((this.textX2 + (this.buttonWidth / 2)) - AndroidUtilities.dp(19.0f), 0, this.textX2 + (this.buttonWidth / 2) + AndroidUtilities.dp(20.0f), AndroidUtilities.dp(39.0f));
            TextView textView2 = this.textView;
            int i = this.textX2;
            textView2.layout(i, 0, textView2.getMeasuredWidth() + i, this.textView.getMeasuredHeight());
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
                    this.textLayout.draw(canvas, this);
                    canvas.restore();
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    private class BlockAuthorDateCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockAuthorDate currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX;
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockAuthorDateCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockAuthorDate block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            MetricAffectingSpan[] spans;
            Spannable spannableAuthor;
            CharSequence text;
            CharSequence text2;
            int idx;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockAuthorDate tL_pageBlockAuthorDate = this.currentBlock;
            if (tL_pageBlockAuthorDate != null) {
                CharSequence author = ArticleViewer.this.getText(this.parentAdapter, (View) this, tL_pageBlockAuthorDate.author, this.currentBlock.author, (TLRPC.PageBlock) this.currentBlock, width);
                if (author instanceof Spannable) {
                    Spannable spannableAuthor2 = (Spannable) author;
                    spannableAuthor = spannableAuthor2;
                    spans = (MetricAffectingSpan[]) spannableAuthor2.getSpans(0, author.length(), MetricAffectingSpan.class);
                } else {
                    spannableAuthor = null;
                    spans = null;
                }
                if (this.currentBlock.published_date != 0 && !TextUtils.isEmpty(author)) {
                    text = LocaleController.formatString("ArticleDateByAuthor", NUM, LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000), author);
                } else if (!TextUtils.isEmpty(author)) {
                    text = LocaleController.formatString("ArticleByAuthor", NUM, author);
                } else {
                    text = LocaleController.getInstance().chatFullDate.format(((long) this.currentBlock.published_date) * 1000);
                }
                if (spans != null) {
                    try {
                        if (spans.length > 0 && (idx = TextUtils.indexOf(text, author)) != -1) {
                            Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
                            text = spannable;
                            for (int a = 0; a < spans.length; a++) {
                                spannable.setSpan(spans[a], spannableAuthor.getSpanStart(spans[a]) + idx, spannableAuthor.getSpanEnd(spans[a]) + idx, 33);
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        text2 = text;
                    }
                }
                text2 = text;
                DrawingText access$13500 = ArticleViewer.this.createLayoutForText(this, text2, (TLRPC.RichText) null, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter);
                this.textLayout = access$13500;
                if (access$13500 != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    if (this.parentAdapter.isRtl) {
                        this.textX = (int) Math.floor((double) ((((float) width) - this.textLayout.getLineWidth(0)) - ((float) AndroidUtilities.dp(16.0f))));
                    } else {
                        this.textX = AndroidUtilities.dp(18.0f);
                    }
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                info.setText(drawingText.getText());
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    private class BlockTitleCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockTitle currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockTitleCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockTitle block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockTitle tL_pageBlockTitle = this.currentBlock;
            if (tL_pageBlockTitle != null) {
                if (tL_pageBlockTitle.first) {
                    height = 0 + AndroidUtilities.dp(8.0f);
                    this.textY = AndroidUtilities.dp(16.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
                }
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13600;
                if (access$13600 != null) {
                    height += AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.textLayout != null) {
                info.setText(this.textLayout.getText() + ", " + LocaleController.getString("AccDescrIVTitle", NUM));
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    private class BlockKickerCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockKicker currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY;

        public BlockKickerCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockKicker block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockKicker tL_pageBlockKicker = this.currentBlock;
            if (tL_pageBlockKicker != null) {
                if (tL_pageBlockKicker.first) {
                    this.textY = AndroidUtilities.dp(16.0f);
                    height = 0 + AndroidUtilities.dp(8.0f);
                } else {
                    this.textY = AndroidUtilities.dp(8.0f);
                }
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.text, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13600;
                if (access$13600 != null) {
                    height += AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    private class BlockFooterCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockFooter currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockFooterCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockFooter block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockFooter tL_pageBlockFooter = this.currentBlock;
            if (tL_pageBlockFooter != null) {
                if (tL_pageBlockFooter.level == 0) {
                    this.textY = AndroidUtilities.dp(8.0f);
                    this.textX = AndroidUtilities.dp(18.0f);
                } else {
                    this.textY = 0;
                    this.textX = AndroidUtilities.dp((float) ((this.currentBlock.level * 14) + 18));
                }
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, this.currentBlock.text, (width - AndroidUtilities.dp(18.0f)) - this.textX, this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13600;
                if (access$13600 != null) {
                    int height2 = access$13600.getHeight();
                    if (this.currentBlock.level > 0) {
                        height = height2 + AndroidUtilities.dp(8.0f);
                    } else {
                        height = height2 + AndroidUtilities.dp(16.0f);
                    }
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate((float) this.textX, (float) this.textY);
                    ArticleViewer.this.drawTextSelection(canvas, this);
                    this.textLayout.draw(canvas, this);
                    canvas.restore();
                }
                if (this.currentBlock.level > 0) {
                    canvas.drawRect((float) AndroidUtilities.dp(18.0f), 0.0f, (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - (this.currentBlock.bottom ? AndroidUtilities.dp(6.0f) : 0)), ArticleViewer.quoteLinePaint);
                }
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }
    }

    private class BlockPreformattedCell extends FrameLayout implements TextSelectionHelper.ArticleSelectableView {
        /* access modifiers changed from: private */
        public TLRPC.TL_pageBlockPreformatted currentBlock;
        /* access modifiers changed from: private */
        public WebpageAdapter parentAdapter;
        private HorizontalScrollView scrollView;
        /* access modifiers changed from: private */
        public View textContainer;
        /* access modifiers changed from: private */
        public DrawingText textLayout;

        public BlockPreformattedCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
            AnonymousClass1 r0 = new HorizontalScrollView(context, ArticleViewer.this) {
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (BlockPreformattedCell.this.textContainer.getMeasuredWidth() > getMeasuredWidth()) {
                        ArticleViewer.this.windowView.requestDisallowInterceptTouchEvent(true);
                    }
                    return super.onInterceptTouchEvent(ev);
                }

                /* access modifiers changed from: protected */
                public void onScrollChanged(int l, int t, int oldl, int oldt) {
                    super.onScrollChanged(l, t, oldl, oldt);
                    if (ArticleViewer.this.pressedLinkOwnerLayout != null) {
                        DrawingText unused = ArticleViewer.this.pressedLinkOwnerLayout = null;
                        View unused2 = ArticleViewer.this.pressedLinkOwnerView = null;
                    }
                }
            };
            this.scrollView = r0;
            r0.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            addView(this.scrollView, LayoutHelper.createFrame(-1, -2.0f));
            this.textContainer = new View(context, ArticleViewer.this) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int height = 0;
                    int width = 1;
                    if (BlockPreformattedCell.this.currentBlock != null) {
                        BlockPreformattedCell blockPreformattedCell = BlockPreformattedCell.this;
                        DrawingText unused = blockPreformattedCell.textLayout = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, BlockPreformattedCell.this.currentBlock.text, AndroidUtilities.dp(5000.0f), 0, BlockPreformattedCell.this.currentBlock, BlockPreformattedCell.this.parentAdapter);
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

                public boolean onTouchEvent(MotionEvent event) {
                    ArticleViewer articleViewer = ArticleViewer.this;
                    WebpageAdapter access$20800 = BlockPreformattedCell.this.parentAdapter;
                    BlockPreformattedCell blockPreformattedCell = BlockPreformattedCell.this;
                    return articleViewer.checkLayoutForLinks(access$20800, event, blockPreformattedCell, blockPreformattedCell.textLayout, 0, 0) || super.onTouchEvent(event);
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (BlockPreformattedCell.this.textLayout != null) {
                        canvas.save();
                        ArticleViewer.this.drawTextSelection(canvas, BlockPreformattedCell.this);
                        BlockPreformattedCell.this.textLayout.draw(canvas, this);
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
                this.scrollView.setOnScrollChangeListener(new ArticleViewer$BlockPreformattedCell$$ExternalSyntheticLambda0(this));
            }
            setWillNotDraw(false);
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ArticleViewer$BlockPreformattedCell  reason: not valid java name */
        public /* synthetic */ void m2712lambda$new$0$orgtelegramuiArticleViewer$BlockPreformattedCell(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (ArticleViewer.this.textSelectionHelper != null && ArticleViewer.this.textSelectionHelper.isSelectionMode()) {
                ArticleViewer.this.textSelectionHelper.invalidate();
            }
        }

        public void setBlock(TLRPC.TL_pageBlockPreformatted block) {
            this.currentBlock = block;
            this.scrollView.setScrollX(0);
            this.textContainer.requestLayout();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
            setMeasuredDimension(width, this.scrollView.getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null) {
                canvas.drawRect(0.0f, (float) AndroidUtilities.dp(8.0f), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(8.0f)), ArticleViewer.preformattedBackgroundPaint);
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
            }
        }

        public void invalidate() {
            this.textContainer.invalidate();
            super.invalidate();
        }
    }

    private class BlockSubheaderCell extends View implements TextSelectionHelper.ArticleSelectableView {
        private TLRPC.TL_pageBlockSubheader currentBlock;
        private WebpageAdapter parentAdapter;
        private DrawingText textLayout;
        private int textX = AndroidUtilities.dp(18.0f);
        private int textY = AndroidUtilities.dp(8.0f);

        public BlockSubheaderCell(Context context, WebpageAdapter adapter) {
            super(context);
            this.parentAdapter = adapter;
        }

        public void setBlock(TLRPC.TL_pageBlockSubheader block) {
            this.currentBlock = block;
            requestLayout();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return ArticleViewer.this.checkLayoutForLinks(this.parentAdapter, event, this, this.textLayout, this.textX, this.textY) || super.onTouchEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            TLRPC.TL_pageBlockSubheader tL_pageBlockSubheader = this.currentBlock;
            if (tL_pageBlockSubheader != null) {
                DrawingText access$13600 = ArticleViewer.this.createLayoutForText(this, (CharSequence) null, tL_pageBlockSubheader.text, width - AndroidUtilities.dp(36.0f), this.textY, this.currentBlock, this.parentAdapter.isRtl ? StaticLayoutEx.ALIGN_RIGHT() : Layout.Alignment.ALIGN_NORMAL, this.parentAdapter);
                this.textLayout = access$13600;
                if (access$13600 != null) {
                    height = 0 + AndroidUtilities.dp(16.0f) + this.textLayout.getHeight();
                    this.textLayout.x = this.textX;
                    this.textLayout.y = this.textY;
                }
            } else {
                height = 1;
            }
            setMeasuredDimension(width, height);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.currentBlock != null && this.textLayout != null) {
                canvas.save();
                canvas.translate((float) this.textX, (float) this.textY);
                ArticleViewer.this.drawTextSelection(canvas, this);
                this.textLayout.draw(canvas, this);
                canvas.restore();
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            if (this.textLayout != null) {
                info.setText(this.textLayout.getText() + ", " + LocaleController.getString("AccDescrIVHeading", NUM));
            }
        }

        public void fillTextLayoutBlocks(ArrayList<TextSelectionHelper.TextLayoutBlock> blocks) {
            DrawingText drawingText = this.textLayout;
            if (drawingText != null) {
                blocks.add(drawingText);
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
        }

        public void setViews(int count) {
            if (count == 0) {
                this.hasViews = false;
                this.viewsTextView.setVisibility(8);
                this.textView.setGravity(17);
            } else {
                this.hasViews = true;
                this.viewsTextView.setVisibility(0);
                this.textView.setGravity(21);
                this.viewsTextView.setText(LocaleController.formatPluralStringComma("Views", count));
            }
            int color = Theme.getColor("switchTrack");
            this.textView.setTextColor(ArticleViewer.getGrayTextColor());
            this.viewsTextView.setTextColor(ArticleViewer.getGrayTextColor());
            this.textView.setBackgroundColor(Color.argb(34, Color.red(color), Color.green(color), Color.blue(color)));
        }
    }

    /* access modifiers changed from: private */
    public void drawTextSelection(Canvas canvas, TextSelectionHelper.ArticleSelectableView view) {
        drawTextSelection(canvas, view, 0);
    }

    /* access modifiers changed from: private */
    public void drawTextSelection(Canvas canvas, TextSelectionHelper.ArticleSelectableView view, int i) {
        TextSelectionHelper.ArticleTextSelectionHelper articleTextSelectionHelper;
        View v = (View) view;
        if (v.getTag() == null || v.getTag() != "bottomSheet" || (articleTextSelectionHelper = this.textSelectionHelperBottomSheet) == null) {
            this.textSelectionHelper.draw(canvas, view, i);
        } else {
            articleTextSelectionHelper.draw(canvas, view, i);
        }
    }

    public boolean openPhoto(TLRPC.PageBlock block, WebpageAdapter adapter2) {
        int index;
        List<TLRPC.PageBlock> pageBlocks;
        if (!(block instanceof TLRPC.TL_pageBlockVideo) || WebPageUtils.isVideo(adapter2.currentPage, block)) {
            pageBlocks = new ArrayList<>(adapter2.photoBlocks);
            index = adapter2.photoBlocks.indexOf(block);
        } else {
            pageBlocks = Collections.singletonList(block);
            index = 0;
        }
        PhotoViewer photoViewer = PhotoViewer.getInstance();
        photoViewer.setParentActivity(this.parentActivity);
        return photoViewer.openPhoto(index, (PhotoViewer.PageBlocksAdapter) new RealPageBlocksAdapter(adapter2.currentPage, pageBlocks), (PhotoViewer.PhotoViewerProvider) new PageBlocksPhotoViewerProvider(pageBlocks));
    }

    private class RealPageBlocksAdapter implements PhotoViewer.PageBlocksAdapter {
        private final TLRPC.WebPage page;
        private final List<TLRPC.PageBlock> pageBlocks;

        private RealPageBlocksAdapter(TLRPC.WebPage page2, List<TLRPC.PageBlock> pageBlocks2) {
            this.page = page2;
            this.pageBlocks = pageBlocks2;
        }

        public int getItemsCount() {
            return this.pageBlocks.size();
        }

        public TLRPC.PageBlock get(int index) {
            return this.pageBlocks.get(index);
        }

        public List<TLRPC.PageBlock> getAll() {
            return this.pageBlocks;
        }

        public boolean isVideo(int index) {
            return index < this.pageBlocks.size() && index >= 0 && WebPageUtils.isVideo(this.page, get(index));
        }

        public TLObject getMedia(int index) {
            if (index >= this.pageBlocks.size() || index < 0) {
                return null;
            }
            return WebPageUtils.getMedia(this.page, get(index));
        }

        public File getFile(int index) {
            if (index >= this.pageBlocks.size() || index < 0) {
                return null;
            }
            return WebPageUtils.getMediaFile(this.page, get(index));
        }

        public String getFileName(int index) {
            TLObject media = getMedia(index);
            if (media instanceof TLRPC.Photo) {
                media = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo) media).sizes, AndroidUtilities.getPhotoSize());
            }
            return FileLoader.getAttachFileName(media);
        }

        public CharSequence getCaption(int index) {
            CharSequence caption = null;
            TLRPC.PageBlock pageBlock = get(index);
            if (pageBlock instanceof TLRPC.TL_pageBlockPhoto) {
                String url = ((TLRPC.TL_pageBlockPhoto) pageBlock).url;
                if (!TextUtils.isEmpty(url)) {
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder(url);
                    stringBuilder.setSpan(new URLSpan(url) {
                        public void onClick(View widget) {
                            ArticleViewer.this.openWebpageUrl(getURL(), (String) null);
                        }
                    }, 0, url.length(), 34);
                    caption = stringBuilder;
                }
            }
            if (caption == null) {
                TLRPC.RichText captionRichText = ArticleViewer.this.getBlockCaption(pageBlock, 2);
                caption = ArticleViewer.this.getText(this.page, (View) null, captionRichText, captionRichText, pageBlock, -AndroidUtilities.dp(100.0f));
                if (caption instanceof Spannable) {
                    Spannable spannable = (Spannable) caption;
                    TextPaintUrlSpan[] spans = (TextPaintUrlSpan[]) spannable.getSpans(0, caption.length(), TextPaintUrlSpan.class);
                    SpannableStringBuilder builder = new SpannableStringBuilder(caption.toString());
                    caption = builder;
                    if (spans != null && spans.length > 0) {
                        for (int a = 0; a < spans.length; a++) {
                            builder.setSpan(new URLSpan(spans[a].getUrl()) {
                                public void onClick(View widget) {
                                    ArticleViewer.this.openWebpageUrl(getURL(), (String) null);
                                }
                            }, spannable.getSpanStart(spans[a]), spannable.getSpanEnd(spans[a]), 33);
                        }
                    }
                }
            }
            return caption;
        }

        public TLRPC.PhotoSize getFileLocation(TLObject media, int[] size) {
            TLRPC.PhotoSize thumb;
            if (media instanceof TLRPC.Photo) {
                TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo) media).sizes, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    size[0] = sizeFull.size;
                    if (size[0] == 0) {
                        size[0] = -1;
                    }
                    return sizeFull;
                }
                size[0] = -1;
                return null;
            } else if (!(media instanceof TLRPC.Document) || (thumb = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Document) media).thumbs, 90)) == null) {
                return null;
            } else {
                size[0] = thumb.size;
                if (size[0] == 0) {
                    size[0] = -1;
                }
                return thumb;
            }
        }

        public void updateSlideshowCell(TLRPC.PageBlock currentPageBlock) {
            int count = ArticleViewer.this.listView[0].getChildCount();
            for (int a = 0; a < count; a++) {
                View child = ArticleViewer.this.listView[0].getChildAt(a);
                if (child instanceof BlockSlideshowCell) {
                    BlockSlideshowCell cell = (BlockSlideshowCell) child;
                    int idx = cell.currentBlock.items.indexOf(currentPageBlock);
                    if (idx != -1) {
                        cell.innerListView.setCurrentItem(idx, false);
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
        private final List<TLRPC.PageBlock> pageBlocks;
        private final int[] tempArr = new int[2];

        public PageBlocksPhotoViewerProvider(List<TLRPC.PageBlock> pageBlocks2) {
            this.pageBlocks = pageBlocks2;
        }

        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            ImageReceiver imageReceiver;
            if (index < 0 || index >= this.pageBlocks.size() || (imageReceiver = getImageReceiverFromListView(ArticleViewer.this.listView[0], this.pageBlocks.get(index), this.tempArr)) == null) {
                return null;
            }
            PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
            object.viewX = this.tempArr[0];
            object.viewY = this.tempArr[1];
            object.parentView = ArticleViewer.this.listView[0];
            object.imageReceiver = imageReceiver;
            object.thumb = imageReceiver.getBitmapSafe();
            object.radius = imageReceiver.getRoundRadius();
            object.clipTopAddition = ArticleViewer.this.currentHeaderHeight;
            return object;
        }

        private ImageReceiver getImageReceiverFromListView(ViewGroup listView, TLRPC.PageBlock pageBlock, int[] coords) {
            int count = listView.getChildCount();
            for (int a = 0; a < count; a++) {
                ImageReceiver imageReceiver = getImageReceiverView(listView.getChildAt(a), pageBlock, coords);
                if (imageReceiver != null) {
                    return imageReceiver;
                }
            }
            return null;
        }

        private ImageReceiver getImageReceiverView(View view, TLRPC.PageBlock pageBlock, int[] coords) {
            ImageReceiver imageReceiver;
            ImageReceiver imageReceiver2;
            if (view instanceof BlockPhotoCell) {
                BlockPhotoCell cell = (BlockPhotoCell) view;
                if (cell.currentBlock != pageBlock) {
                    return null;
                }
                view.getLocationInWindow(coords);
                return cell.imageView;
            } else if (view instanceof BlockVideoCell) {
                BlockVideoCell cell2 = (BlockVideoCell) view;
                if (cell2.currentBlock != pageBlock) {
                    return null;
                }
                view.getLocationInWindow(coords);
                return cell2.imageView;
            } else if (view instanceof BlockCollageCell) {
                ImageReceiver imageReceiver3 = getImageReceiverFromListView(((BlockCollageCell) view).innerListView, pageBlock, coords);
                if (imageReceiver3 != null) {
                    return imageReceiver3;
                }
                return null;
            } else if (view instanceof BlockSlideshowCell) {
                ImageReceiver imageReceiver4 = getImageReceiverFromListView(((BlockSlideshowCell) view).innerListView, pageBlock, coords);
                if (imageReceiver4 != null) {
                    return imageReceiver4;
                }
                return null;
            } else if (view instanceof BlockListItemCell) {
                BlockListItemCell blockListItemCell = (BlockListItemCell) view;
                if (blockListItemCell.blockLayout == null || (imageReceiver2 = getImageReceiverView(blockListItemCell.blockLayout.itemView, pageBlock, coords)) == null) {
                    return null;
                }
                return imageReceiver2;
            } else if (!(view instanceof BlockOrderedListItemCell)) {
                return null;
            } else {
                BlockOrderedListItemCell blockOrderedListItemCell = (BlockOrderedListItemCell) view;
                if (blockOrderedListItemCell.blockLayout == null || (imageReceiver = getImageReceiverView(blockOrderedListItemCell.blockLayout.itemView, pageBlock, coords)) == null) {
                    return null;
                }
                return imageReceiver;
            }
        }
    }
}
