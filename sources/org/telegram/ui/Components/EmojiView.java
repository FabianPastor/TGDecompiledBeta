package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetGroupInfoCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.PagerSlidingTabStrip;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TrendingStickersLayout;
import org.telegram.ui.ContentPreviewViewer;

public class EmojiView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static final ViewTreeObserver.OnScrollChangedListener NOP = EmojiView$$ExternalSyntheticLambda5.INSTANCE;
    /* access modifiers changed from: private */
    public static final Field superListenerField;
    /* access modifiers changed from: private */
    public ImageView backspaceButton;
    private AnimatorSet backspaceButtonAnimation;
    /* access modifiers changed from: private */
    public boolean backspaceOnce;
    /* access modifiers changed from: private */
    public boolean backspacePressed;
    private FrameLayout bottomTabContainer;
    private AnimatorSet bottomTabContainerAnimation;
    private View bottomTabContainerBackground;
    /* access modifiers changed from: private */
    public Runnable checkExpandStickerTabsRunnable = new Runnable() {
        public void run() {
            if (!EmojiView.this.stickersTab.isDragging()) {
                boolean unused = EmojiView.this.expandStickersByDragg = false;
                EmojiView.this.updateStickerTabsPosition();
            }
        }
    };
    /* access modifiers changed from: private */
    public ChooseStickerActionTracker chooseStickerActionTracker;
    /* access modifiers changed from: private */
    public ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() {
        public /* synthetic */ boolean needMenu() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needMenu(this);
        }

        public /* synthetic */ boolean needOpen() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needOpen(this);
        }

        public /* synthetic */ boolean needRemove() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needRemove(this);
        }

        public /* synthetic */ void remove(SendMessagesHelper.ImportingSticker importingSticker) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$remove(this, importingSticker);
        }

        public void sendSticker(TLRPC.Document sticker, String query, Object parent, boolean notify, int scheduleDate) {
            EmojiView.this.delegate.onStickerSelected((View) null, sticker, query, parent, (MessageObject.SendAnimationData) null, notify, scheduleDate);
        }

        public boolean needSend() {
            return true;
        }

        public boolean canSchedule() {
            return EmojiView.this.delegate.canSchedule();
        }

        public boolean isInScheduleMode() {
            return EmojiView.this.delegate.isInScheduleMode();
        }

        public void openSet(TLRPC.InputStickerSet set, boolean clearsInputField) {
            if (set != null) {
                EmojiView.this.delegate.onShowStickerSet((TLRPC.StickerSet) null, set);
            }
        }

        public void sendGif(Object gif, Object parent, boolean notify, int scheduleDate) {
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                EmojiView.this.delegate.onGifSelected((View) null, gif, (String) null, parent, notify, scheduleDate);
            } else if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter) {
                EmojiView.this.delegate.onGifSelected((View) null, gif, (String) null, parent, notify, scheduleDate);
            }
        }

        public void gifAddedOrDeleted() {
            EmojiView.this.updateRecentGifs();
        }

        public long getDialogId() {
            return EmojiView.this.delegate.getDialogId();
        }

        public String getQuery(boolean isGif) {
            if (isGif) {
                if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter) {
                    return EmojiView.this.gifSearchAdapter.lastSearchImageString;
                }
                return null;
            } else if (EmojiView.this.emojiGridView.getAdapter() == EmojiView.this.emojiSearchAdapter) {
                return EmojiView.this.emojiSearchAdapter.lastSearchEmojiString;
            } else {
                return null;
            }
        }
    };
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private int currentBackgroundType = -1;
    /* access modifiers changed from: private */
    public long currentChatId;
    private int currentPage;
    /* access modifiers changed from: private */
    public EmojiViewDelegate delegate;
    /* access modifiers changed from: private */
    public Paint dotPaint;
    /* access modifiers changed from: private */
    public DragListener dragListener;
    /* access modifiers changed from: private */
    public EmojiGridAdapter emojiAdapter;
    private FrameLayout emojiContainer;
    /* access modifiers changed from: private */
    public RecyclerListView emojiGridView;
    private Drawable[] emojiIcons;
    /* access modifiers changed from: private */
    public float emojiLastX;
    /* access modifiers changed from: private */
    public float emojiLastY;
    /* access modifiers changed from: private */
    public GridLayoutManager emojiLayoutManager;
    /* access modifiers changed from: private */
    public EmojiSearchAdapter emojiSearchAdapter;
    /* access modifiers changed from: private */
    public SearchField emojiSearchField;
    /* access modifiers changed from: private */
    public int emojiSize;
    /* access modifiers changed from: private */
    public AnimatorSet emojiTabShadowAnimator;
    /* access modifiers changed from: private */
    public ScrollSlidingTabStrip emojiTabs;
    /* access modifiers changed from: private */
    public View emojiTabsShadow;
    /* access modifiers changed from: private */
    public String[] emojiTitles;
    /* access modifiers changed from: private */
    public ImageViewEmoji emojiTouchedView;
    /* access modifiers changed from: private */
    public float emojiTouchedX;
    /* access modifiers changed from: private */
    public float emojiTouchedY;
    /* access modifiers changed from: private */
    public boolean expandStickersByDragg;
    /* access modifiers changed from: private */
    public int favTabBum = -2;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Document> favouriteStickers = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.StickerSetCovered> featuredStickerSets = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean firstEmojiAttach = true;
    /* access modifiers changed from: private */
    public boolean firstGifAttach = true;
    /* access modifiers changed from: private */
    public boolean firstStickersAttach = true;
    private boolean firstTabUpdate;
    private ImageView floatingButton;
    private boolean forseMultiwindowLayout;
    /* access modifiers changed from: private */
    public GifAdapter gifAdapter;
    /* access modifiers changed from: private */
    public final Map<String, TLRPC.messages_BotResults> gifCache = new HashMap();
    private FrameLayout gifContainer;
    /* access modifiers changed from: private */
    public int gifFirstEmojiTabNum = -2;
    /* access modifiers changed from: private */
    public RecyclerListView gifGridView;
    private Drawable[] gifIcons;
    /* access modifiers changed from: private */
    public GifLayoutManager gifLayoutManager;
    private RecyclerListView.OnItemClickListener gifOnItemClickListener;
    /* access modifiers changed from: private */
    public int gifRecentTabNum = -2;
    /* access modifiers changed from: private */
    public GifAdapter gifSearchAdapter;
    /* access modifiers changed from: private */
    public SearchField gifSearchField;
    /* access modifiers changed from: private */
    public GifSearchPreloader gifSearchPreloader = new GifSearchPreloader();
    /* access modifiers changed from: private */
    public ScrollSlidingTabStrip gifTabs;
    /* access modifiers changed from: private */
    public int gifTrendingTabNum = -2;
    /* access modifiers changed from: private */
    public int groupStickerPackNum;
    /* access modifiers changed from: private */
    public int groupStickerPackPosition;
    /* access modifiers changed from: private */
    public TLRPC.TL_messages_stickerSet groupStickerSet;
    /* access modifiers changed from: private */
    public boolean groupStickersHidden;
    /* access modifiers changed from: private */
    public boolean hasChatStickers;
    private int hasRecentEmoji = -1;
    /* access modifiers changed from: private */
    public boolean ignoreStickersScroll;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets = new LongSparseArray<>();
    private boolean isLayout;
    private float lastBottomScrollDy;
    private int lastNotifyHeight;
    private int lastNotifyHeight2;
    private int lastNotifyWidth;
    /* access modifiers changed from: private */
    public String[] lastSearchKeyboardLanguage;
    /* access modifiers changed from: private */
    public float lastStickersX;
    /* access modifiers changed from: private */
    public int[] location = new int[2];
    /* access modifiers changed from: private */
    public TextView mediaBanTooltip;
    /* access modifiers changed from: private */
    public boolean needEmojiSearch;
    private Object outlineProvider;
    /* access modifiers changed from: private */
    public ViewPager pager;
    /* access modifiers changed from: private */
    public EmojiColorPickerView pickerView;
    /* access modifiers changed from: private */
    public EmojiPopupWindow pickerViewPopup;
    /* access modifiers changed from: private */
    public int popupHeight;
    /* access modifiers changed from: private */
    public int popupWidth;
    private TLRPC.StickerSetCovered[] primaryInstallingStickerSets = new TLRPC.StickerSetCovered[10];
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Document> recentGifs = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Document> recentStickers = new ArrayList<>();
    /* access modifiers changed from: private */
    public int recentTabBum = -2;
    Rect rect = new Rect();
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.StickerSetCovered> removingStickerSets = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public final Theme.ResourcesProvider resourcesProvider;
    private RecyclerAnimationScrollHelper scrollHelper;
    /* access modifiers changed from: private */
    public AnimatorSet searchAnimation;
    private ImageView searchButton;
    /* access modifiers changed from: private */
    public int searchFieldHeight;
    private Drawable searchIconDotDrawable;
    private Drawable searchIconDrawable;
    private View shadowLine;
    private boolean showGifs;
    private boolean showing;
    private Drawable[] stickerIcons;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = new ArrayList<>();
    /* access modifiers changed from: private */
    public ImageView stickerSettingsButton;
    private AnimatorSet stickersButtonAnimation;
    private FrameLayout stickersContainer;
    /* access modifiers changed from: private */
    public boolean stickersContainerAttached;
    /* access modifiers changed from: private */
    public StickersGridAdapter stickersGridAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView stickersGridView;
    /* access modifiers changed from: private */
    public GridLayoutManager stickersLayoutManager;
    private int stickersMinusDy;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    /* access modifiers changed from: private */
    public SearchField stickersSearchField;
    /* access modifiers changed from: private */
    public StickersSearchGridAdapter stickersSearchGridAdapter;
    /* access modifiers changed from: private */
    public ScrollSlidingTabStrip stickersTab;
    /* access modifiers changed from: private */
    public FrameLayout stickersTabContainer;
    /* access modifiers changed from: private */
    public int stickersTabOffset;
    /* access modifiers changed from: private */
    public Drawable[] tabIcons;
    private final int[] tabsMinusDy = new int[3];
    private ObjectAnimator[] tabsYAnimators = new ObjectAnimator[3];
    private View topShadow;
    /* access modifiers changed from: private */
    public TrendingAdapter trendingAdapter;
    private int trendingTabNum = -2;
    private PagerSlidingTabStrip typeTabs;
    /* access modifiers changed from: private */
    public ArrayList<View> views = new ArrayList<>();

    @Retention(RetentionPolicy.SOURCE)
    private @interface Type {
        public static final int EMOJIS = 1;
        public static final int GIFS = 2;
        public static final int STICKERS = 0;
    }

    public interface DragListener {
        void onDrag(int i);

        void onDragCancel();

        void onDragEnd(float f);

        void onDragStart();
    }

    public interface EmojiViewDelegate {
        boolean canSchedule();

        long getDialogId();

        float getProgressToSearchOpened();

        int getThreadId();

        void invalidateEnterView();

        boolean isExpanded();

        boolean isInScheduleMode();

        boolean isSearchOpened();

        boolean onBackspace();

        void onClearEmojiRecent();

        void onEmojiSelected(String str);

        void onGifSelected(View view, Object obj, String str, Object obj2, boolean z, int i);

        void onSearchOpenClose(int i);

        void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet);

        void onStickerSelected(View view, TLRPC.Document document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i);

        void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered);

        void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered);

        void onStickersGroupClick(long j);

        void onStickersSettingsClick();

        void onTabOpened(int i);

        void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout);

        /* renamed from: org.telegram.ui.Components.EmojiView$EmojiViewDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static boolean $default$onBackspace(EmojiViewDelegate _this) {
                return false;
            }

            public static void $default$onEmojiSelected(EmojiViewDelegate _this, String emoji) {
            }

            public static void $default$onStickerSelected(EmojiViewDelegate _this, View view, TLRPC.Document sticker, String query, Object parent, MessageObject.SendAnimationData sendAnimationData, boolean notify, int scheduleDate) {
            }

            public static void $default$onStickersSettingsClick(EmojiViewDelegate _this) {
            }

            public static void $default$onStickersGroupClick(EmojiViewDelegate _this, long chatId) {
            }

            public static void $default$onGifSelected(EmojiViewDelegate _this, View view, Object gif, String query, Object parent, boolean notify, int scheduleDate) {
            }

            public static void $default$onTabOpened(EmojiViewDelegate _this, int type) {
            }

            public static void $default$onClearEmojiRecent(EmojiViewDelegate _this) {
            }

            public static void $default$onShowStickerSet(EmojiViewDelegate _this, TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet) {
            }

            public static void $default$onStickerSetAdd(EmojiViewDelegate _this, TLRPC.StickerSetCovered stickerSet) {
            }

            public static void $default$onStickerSetRemove(EmojiViewDelegate _this, TLRPC.StickerSetCovered stickerSet) {
            }

            public static void $default$onSearchOpenClose(EmojiViewDelegate _this, int type) {
            }

            public static boolean $default$isSearchOpened(EmojiViewDelegate _this) {
                return false;
            }

            public static boolean $default$isExpanded(EmojiViewDelegate _this) {
                return false;
            }

            public static boolean $default$canSchedule(EmojiViewDelegate _this) {
                return false;
            }

            public static boolean $default$isInScheduleMode(EmojiViewDelegate _this) {
                return false;
            }

            public static long $default$getDialogId(EmojiViewDelegate _this) {
                return 0;
            }

            public static int $default$getThreadId(EmojiViewDelegate _this) {
                return 0;
            }

            public static void $default$showTrendingStickersAlert(EmojiViewDelegate _this, TrendingStickersLayout layout) {
            }

            public static void $default$invalidateEnterView(EmojiViewDelegate _this) {
            }

            public static float $default$getProgressToSearchOpened(EmojiViewDelegate _this) {
                return 0.0f;
            }
        }
    }

    static {
        Field f = null;
        try {
            f = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }
        superListenerField = f;
    }

    static /* synthetic */ void lambda$static$0() {
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.stickersSearchField.searchEditText.setEnabled(enabled);
        this.gifSearchField.searchEditText.setEnabled(enabled);
        this.emojiSearchField.searchEditText.setEnabled(enabled);
    }

    private class SearchField extends FrameLayout {
        /* access modifiers changed from: private */
        public View backgroundView;
        /* access modifiers changed from: private */
        public ImageView clearSearchImageView;
        /* access modifiers changed from: private */
        public CloseProgressDrawable2 progressDrawable;
        /* access modifiers changed from: private */
        public View searchBackground;
        /* access modifiers changed from: private */
        public EditTextBoldCursor searchEditText;
        /* access modifiers changed from: private */
        public ImageView searchIconImageView;
        /* access modifiers changed from: private */
        public AnimatorSet shadowAnimator;
        /* access modifiers changed from: private */
        public View shadowView;

        public SearchField(Context context, final int type) {
            super(context);
            View view = new View(context);
            this.shadowView = view;
            view.setAlpha(0.0f);
            this.shadowView.setTag(1);
            this.shadowView.setBackgroundColor(EmojiView.this.getThemedColor("chat_emojiPanelShadowLine"));
            addView(this.shadowView, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83));
            View view2 = new View(context);
            this.backgroundView = view2;
            view2.setBackgroundColor(EmojiView.this.getThemedColor("chat_emojiPanelBackground"));
            addView(this.backgroundView, new FrameLayout.LayoutParams(-1, EmojiView.this.searchFieldHeight));
            View view3 = new View(context);
            this.searchBackground = view3;
            view3.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), EmojiView.this.getThemedColor("chat_emojiSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 14.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(NUM);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 14.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView3.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 14.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new EmojiView$SearchField$$ExternalSyntheticLambda0(this));
            AnonymousClass1 r0 = new EditTextBoldCursor(context, EmojiView.this) {
                public boolean onTouchEvent(MotionEvent event) {
                    if (!SearchField.this.searchEditText.isEnabled()) {
                        return super.onTouchEvent(event);
                    }
                    if (event.getAction() == 0) {
                        if (!EmojiView.this.delegate.isSearchOpened()) {
                            EmojiView.this.openSearch(SearchField.this);
                        }
                        EmojiViewDelegate access$400 = EmojiView.this.delegate;
                        int i = 1;
                        if (type == 1) {
                            i = 2;
                        }
                        access$400.onSearchOpenClose(i);
                        SearchField.this.searchEditText.requestFocus();
                        AndroidUtilities.showKeyboard(SearchField.this.searchEditText);
                    }
                    return super.onTouchEvent(event);
                }
            };
            this.searchEditText = r0;
            r0.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(EmojiView.this.getThemedColor("chat_emojiSearchIcon"));
            this.searchEditText.setTextColor(EmojiView.this.getThemedColor("windowBackgroundWhiteBlackText"));
            this.searchEditText.setBackgroundDrawable((Drawable) null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(NUM);
            if (type == 0) {
                this.searchEditText.setHint(LocaleController.getString("SearchStickersHint", NUM));
            } else if (type == 1) {
                this.searchEditText.setHint(LocaleController.getString("SearchEmojiHint", NUM));
            } else if (type == 2) {
                this.searchEditText.setHint(LocaleController.getString("SearchGifsTitle", NUM));
            }
            this.searchEditText.setCursorColor(EmojiView.this.getThemedColor("featuredStickers_addedIcon"));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 12.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(EmojiView.this) {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    boolean showed = false;
                    boolean show = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() != 0.0f) {
                        showed = true;
                    }
                    if (show != showed) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (show) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(show ? 1.0f : 0.1f);
                        if (!show) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    int i = type;
                    if (i == 0) {
                        EmojiView.this.stickersSearchGridAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (i == 1) {
                        EmojiView.this.emojiSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (i == 2) {
                        EmojiView.this.gifSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    }
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-EmojiView$SearchField  reason: not valid java name */
        public /* synthetic */ void m2262lambda$new$0$orgtelegramuiComponentsEmojiView$SearchField(View v) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        /* access modifiers changed from: private */
        public void showShadow(boolean show, boolean animated) {
            if (show && this.shadowView.getTag() == null) {
                return;
            }
            if (show || this.shadowView.getTag() == null) {
                AnimatorSet animatorSet = this.shadowAnimator;
                int i = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.shadowAnimator = null;
                }
                View view = this.shadowView;
                if (!show) {
                    i = 1;
                }
                view.setTag(i);
                float f = 1.0f;
                if (animated) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.shadowAnimator = animatorSet2;
                    Animator[] animatorArr = new Animator[1];
                    View view2 = this.shadowView;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    if (!show) {
                        f = 0.0f;
                    }
                    fArr[0] = f;
                    animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                    animatorSet2.playTogether(animatorArr);
                    this.shadowAnimator.setDuration(200);
                    this.shadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.shadowAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            AnimatorSet unused = SearchField.this.shadowAnimator = null;
                        }
                    });
                    this.shadowAnimator.start();
                    return;
                }
                View view3 = this.shadowView;
                if (!show) {
                    f = 0.0f;
                }
                view3.setAlpha(f);
            }
        }
    }

    private class TypedScrollListener extends RecyclerView.OnScrollListener {
        private boolean smoothScrolling;
        private final int type;

        public TypedScrollListener(int type2) {
            this.type = type2;
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (recyclerView.getLayoutManager().isSmoothScrolling()) {
                this.smoothScrolling = true;
            } else if (newState == 0) {
                if (!this.smoothScrolling) {
                    EmojiView.this.animateTabsY(this.type);
                }
                if (EmojiView.this.ignoreStickersScroll) {
                    boolean unused = EmojiView.this.ignoreStickersScroll = false;
                }
                this.smoothScrolling = false;
            } else {
                if (newState == 1) {
                    if (EmojiView.this.ignoreStickersScroll) {
                        boolean unused2 = EmojiView.this.ignoreStickersScroll = false;
                    }
                    SearchField searchField = EmojiView.this.getSearchFieldForType(this.type);
                    if (searchField != null) {
                        searchField.hideKeyboard();
                    }
                    this.smoothScrolling = false;
                }
                if (!this.smoothScrolling) {
                    EmojiView.this.stopAnimatingTabsY(this.type);
                }
                if (this.type == 0) {
                    if (EmojiView.this.chooseStickerActionTracker == null) {
                        EmojiView.this.createStickersChooseActionTracker();
                    }
                    EmojiView.this.chooseStickerActionTracker.doSomeAction();
                }
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            EmojiView.this.checkScroll(this.type);
            EmojiView.this.checkTabsY(this.type, dy);
            checkSearchFieldScroll();
            if (!this.smoothScrolling) {
                EmojiView.this.checkBottomTabScroll((float) dy);
            }
        }

        private void checkSearchFieldScroll() {
            switch (this.type) {
                case 0:
                    EmojiView.this.checkStickersSearchFieldScroll(false);
                    return;
                case 1:
                    EmojiView.this.checkEmojiSearchFieldScroll(false);
                    return;
                case 2:
                    EmojiView.this.checkGifSearchFieldScroll(false);
                    return;
                default:
                    return;
            }
        }
    }

    private class DraggableScrollSlidingTabStrip extends ScrollSlidingTabStrip {
        private float downX;
        private float downY;
        private boolean draggingHorizontally;
        private boolean draggingVertically;
        private boolean first = true;
        private float lastTranslateX;
        private float lastX;
        private boolean startedScroll;
        private final int touchSlop;
        private VelocityTracker vTracker;

        public DraggableScrollSlidingTabStrip(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (isDragging()) {
                return super.onInterceptTouchEvent(ev);
            }
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (ev.getAction() == 0) {
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                this.downX = ev.getRawX();
                this.downY = ev.getRawY();
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null && Math.abs(ev.getRawY() - this.downY) >= ((float) this.touchSlop)) {
                this.draggingVertically = true;
                this.downY = ev.getRawY();
                EmojiView.this.dragListener.onDragStart();
                if (this.startedScroll) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
                return true;
            }
            return super.onInterceptTouchEvent(ev);
        }

        public boolean onTouchEvent(MotionEvent ev) {
            if (isDragging()) {
                return super.onTouchEvent(ev);
            }
            if (this.first) {
                this.first = false;
                this.lastX = ev.getX();
            }
            if (ev.getAction() == 0 || ev.getAction() == 2) {
                float unused = EmojiView.this.lastStickersX = ev.getRawX();
            }
            if (ev.getAction() == 0) {
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                this.downX = ev.getRawX();
                this.downY = ev.getRawY();
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null) {
                if (Math.abs(ev.getRawX() - this.downX) >= ((float) this.touchSlop) && canScrollHorizontally((int) (this.downX - ev.getRawX()))) {
                    this.draggingHorizontally = true;
                    AndroidUtilities.cancelRunOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable);
                    boolean unused2 = EmojiView.this.expandStickersByDragg = true;
                    EmojiView.this.updateStickerTabsPosition();
                } else if (Math.abs(ev.getRawY() - this.downY) >= ((float) this.touchSlop)) {
                    this.draggingVertically = true;
                    this.downY = ev.getRawY();
                    EmojiView.this.dragListener.onDragStart();
                    if (this.startedScroll) {
                        EmojiView.this.pager.endFakeDrag();
                        this.startedScroll = false;
                    }
                }
            }
            if (EmojiView.this.expandStickersByDragg && (ev.getAction() == 1 || ev.getAction() == 3)) {
                AndroidUtilities.runOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable, 1500);
            }
            if (this.draggingVertically) {
                if (this.vTracker == null) {
                    this.vTracker = VelocityTracker.obtain();
                }
                this.vTracker.addMovement(ev);
                if (ev.getAction() == 1 || ev.getAction() == 3) {
                    this.vTracker.computeCurrentVelocity(1000);
                    float velocity = this.vTracker.getYVelocity();
                    this.vTracker.recycle();
                    this.vTracker = null;
                    if (ev.getAction() == 1) {
                        EmojiView.this.dragListener.onDragEnd(velocity);
                    } else {
                        EmojiView.this.dragListener.onDragCancel();
                    }
                    this.first = true;
                    this.draggingHorizontally = false;
                    this.draggingVertically = false;
                } else {
                    EmojiView.this.dragListener.onDrag(Math.round(ev.getRawY() - this.downY));
                }
                cancelLongPress();
                return true;
            }
            float newTranslationX = getTranslationX();
            if (getScrollX() == 0 && newTranslationX == 0.0f) {
                if (this.startedScroll || this.lastX - ev.getX() >= 0.0f) {
                    if (this.startedScroll && this.lastX - ev.getX() > 0.0f && EmojiView.this.pager.isFakeDragging()) {
                        EmojiView.this.pager.endFakeDrag();
                        this.startedScroll = false;
                    }
                } else if (EmojiView.this.pager.beginFakeDrag()) {
                    this.startedScroll = true;
                    this.lastTranslateX = getTranslationX();
                }
            }
            if (this.startedScroll) {
                int x = (int) (((ev.getX() - this.lastX) + newTranslationX) - this.lastTranslateX);
                try {
                    this.lastTranslateX = newTranslationX;
                } catch (Exception e) {
                    try {
                        EmojiView.this.pager.endFakeDrag();
                    } catch (Exception e2) {
                    }
                    this.startedScroll = false;
                    FileLog.e((Throwable) e);
                }
            }
            this.lastX = ev.getX();
            if (ev.getAction() == 3 || ev.getAction() == 1) {
                this.first = true;
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                if (this.startedScroll) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
            }
            if (this.startedScroll || super.onTouchEvent(ev)) {
                return true;
            }
            return false;
        }
    }

    private class ImageViewEmoji extends ImageView {
        /* access modifiers changed from: private */
        public boolean isRecent;

        public ImageViewEmoji(Context context) {
            super(context);
            setScaleType(ImageView.ScaleType.CENTER);
        }

        /* access modifiers changed from: private */
        public void sendEmoji(String override) {
            String color;
            EmojiView.this.showBottomTab(true, true);
            String code = override != null ? override : (String) getTag();
            new SpannableStringBuilder().append(code);
            if (override == null) {
                if (!this.isRecent && (color = Emoji.emojiColor.get(code)) != null) {
                    code = EmojiView.addColorToCode(code, color);
                }
                EmojiView.this.addEmojiToRecent(code);
                if (EmojiView.this.delegate != null) {
                    EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(code));
                }
            } else if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(override));
            }
        }

        public void setImageDrawable(Drawable drawable, boolean recent) {
            super.setImageDrawable(drawable);
            this.isRecent = recent;
        }

        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(widthMeasureSpec));
        }
    }

    private class EmojiPopupWindow extends PopupWindow {
        private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
        private ViewTreeObserver mViewTreeObserver;

        public EmojiPopupWindow() {
            init();
        }

        public EmojiPopupWindow(Context context) {
            super(context);
            init();
        }

        public EmojiPopupWindow(int width, int height) {
            super(width, height);
            init();
        }

        public EmojiPopupWindow(View contentView) {
            super(contentView);
            init();
        }

        public EmojiPopupWindow(View contentView, int width, int height, boolean focusable) {
            super(contentView, width, height, focusable);
            init();
        }

        public EmojiPopupWindow(View contentView, int width, int height) {
            super(contentView, width, height);
            init();
        }

        private void init() {
            if (EmojiView.superListenerField != null) {
                try {
                    this.mSuperScrollListener = (ViewTreeObserver.OnScrollChangedListener) EmojiView.superListenerField.get(this);
                    EmojiView.superListenerField.set(this, EmojiView.NOP);
                } catch (Exception e) {
                    this.mSuperScrollListener = null;
                }
            }
        }

        private void unregisterListener() {
            ViewTreeObserver viewTreeObserver;
            if (this.mSuperScrollListener != null && (viewTreeObserver = this.mViewTreeObserver) != null) {
                if (viewTreeObserver.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = null;
            }
        }

        private void registerListener(View anchor) {
            if (this.mSuperScrollListener != null) {
                ViewTreeObserver vto = anchor.getWindowToken() != null ? anchor.getViewTreeObserver() : null;
                ViewTreeObserver viewTreeObserver = this.mViewTreeObserver;
                if (vto != viewTreeObserver) {
                    if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
                        this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                    }
                    this.mViewTreeObserver = vto;
                    if (vto != null) {
                        vto.addOnScrollChangedListener(this.mSuperScrollListener);
                    }
                }
            }
        }

        public void showAsDropDown(View anchor, int xoff, int yoff) {
            try {
                super.showAsDropDown(anchor, xoff, yoff);
                registerListener(anchor);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void update(View anchor, int xoff, int yoff, int width, int height) {
            super.update(anchor, xoff, yoff, width, height);
            registerListener(anchor);
        }

        public void update(View anchor, int width, int height) {
            super.update(anchor, width, height);
            registerListener(anchor);
        }

        public void showAtLocation(View parent, int gravity, int x, int y) {
            super.showAtLocation(parent, gravity, x, y);
            unregisterListener();
        }

        public void dismiss() {
            setFocusable(false);
            try {
                super.dismiss();
            } catch (Exception e) {
            }
            unregisterListener();
        }
    }

    private class EmojiColorPickerView extends View {
        /* access modifiers changed from: private */
        public Drawable arrowDrawable = getResources().getDrawable(NUM);
        private int arrowX;
        /* access modifiers changed from: private */
        public Drawable backgroundDrawable = getResources().getDrawable(NUM);
        private String currentEmoji;
        private RectF rect = new RectF();
        private Paint rectPaint = new Paint(1);
        private int selection;

        public void setEmoji(String emoji, int arrowPosition) {
            this.currentEmoji = emoji;
            this.arrowX = arrowPosition;
            this.rectPaint.setColor(NUM);
            invalidate();
        }

        public String getEmoji() {
            return this.currentEmoji;
        }

        public void setSelection(int position) {
            if (this.selection != position) {
                this.selection = position;
                invalidate();
            }
        }

        public int getSelection() {
            return this.selection;
        }

        public EmojiColorPickerView(Context context) {
            super(context);
            Theme.setDrawableColor(this.backgroundDrawable, EmojiView.this.getThemedColor("dialogBackground"));
            Theme.setDrawableColor(this.arrowDrawable, EmojiView.this.getThemedColor("dialogBackground"));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            String color;
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 52.0f));
            this.backgroundDrawable.draw(canvas);
            Drawable drawable = this.arrowDrawable;
            int dp = this.arrowX - AndroidUtilities.dp(9.0f);
            float f = 55.5f;
            int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 55.5f : 47.5f);
            int dp3 = this.arrowX + AndroidUtilities.dp(9.0f);
            if (!AndroidUtilities.isTablet()) {
                f = 47.5f;
            }
            drawable.setBounds(dp, dp2, dp3, AndroidUtilities.dp(f + 8.0f));
            this.arrowDrawable.draw(canvas);
            if (this.currentEmoji != null) {
                for (int a = 0; a < 6; a++) {
                    int x = (EmojiView.this.emojiSize * a) + AndroidUtilities.dp((float) ((a * 4) + 5));
                    int y = AndroidUtilities.dp(9.0f);
                    if (this.selection == a) {
                        this.rect.set((float) x, (float) (y - ((int) AndroidUtilities.dpf2(3.5f))), (float) (EmojiView.this.emojiSize + x), (float) (EmojiView.this.emojiSize + y + AndroidUtilities.dp(3.0f)));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.rectPaint);
                    }
                    String code = this.currentEmoji;
                    if (a != 0) {
                        switch (a) {
                            case 1:
                                color = "🏻";
                                break;
                            case 2:
                                color = "🏼";
                                break;
                            case 3:
                                color = "🏽";
                                break;
                            case 4:
                                color = "🏾";
                                break;
                            case 5:
                                color = "🏿";
                                break;
                            default:
                                color = "";
                                break;
                        }
                        code = EmojiView.addColorToCode(code, color);
                    }
                    Drawable drawable2 = Emoji.getEmojiBigDrawable(code);
                    if (drawable2 != null) {
                        drawable2.setBounds(x, y, EmojiView.this.emojiSize + x, EmojiView.this.emojiSize + y);
                        drawable2.draw(canvas);
                    }
                }
            }
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public EmojiView(boolean r27, boolean r28, android.content.Context r29, boolean r30, org.telegram.tgnet.TLRPC.ChatFull r31, android.view.ViewGroup r32, org.telegram.ui.ActionBar.Theme.ResourcesProvider r33) {
        /*
            r26 = this;
            r0 = r26
            r1 = r28
            r2 = r29
            r3 = r30
            r4 = r32
            r5 = r33
            r0.<init>(r2)
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r0.views = r6
            r6 = 1
            r0.firstEmojiAttach = r6
            r7 = -1
            r0.hasRecentEmoji = r7
            org.telegram.ui.Components.EmojiView$GifSearchPreloader r8 = new org.telegram.ui.Components.EmojiView$GifSearchPreloader
            r9 = 0
            r8.<init>()
            r0.gifSearchPreloader = r8
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>()
            r0.gifCache = r8
            r0.firstGifAttach = r6
            r8 = -2
            r0.gifRecentTabNum = r8
            r0.gifTrendingTabNum = r8
            r0.gifFirstEmojiTabNum = r8
            r0.firstStickersAttach = r6
            r10 = 3
            int[] r11 = new int[r10]
            r0.tabsMinusDy = r11
            android.animation.ObjectAnimator[] r11 = new android.animation.ObjectAnimator[r10]
            r0.tabsYAnimators = r11
            int r11 = org.telegram.messenger.UserConfig.selectedAccount
            r0.currentAccount = r11
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            r0.stickerSets = r11
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            r0.recentGifs = r11
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            r0.recentStickers = r11
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            r0.favouriteStickers = r11
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            r0.featuredStickerSets = r11
            r11 = 10
            org.telegram.tgnet.TLRPC$StickerSetCovered[] r11 = new org.telegram.tgnet.TLRPC.StickerSetCovered[r11]
            r0.primaryInstallingStickerSets = r11
            android.util.LongSparseArray r11 = new android.util.LongSparseArray
            r11.<init>()
            r0.installingStickerSets = r11
            android.util.LongSparseArray r11 = new android.util.LongSparseArray
            r11.<init>()
            r0.removingStickerSets = r11
            r11 = 2
            int[] r12 = new int[r11]
            r0.location = r12
            r0.recentTabBum = r8
            r0.favTabBum = r8
            r0.trendingTabNum = r8
            r0.currentBackgroundType = r7
            org.telegram.ui.Components.EmojiView$1 r12 = new org.telegram.ui.Components.EmojiView$1
            r12.<init>()
            r0.checkExpandStickerTabsRunnable = r12
            org.telegram.ui.Components.EmojiView$2 r12 = new org.telegram.ui.Components.EmojiView$2
            r12.<init>()
            r0.contentPreviewViewerDelegate = r12
            android.graphics.Rect r12 = new android.graphics.Rect
            r12.<init>()
            r0.rect = r12
            r0.resourcesProvider = r5
            java.lang.String r12 = "chat_emojiBottomPanelIcon"
            int r13 = r0.getThemedColor(r12)
            int r14 = android.graphics.Color.red(r13)
            int r15 = android.graphics.Color.green(r13)
            int r8 = android.graphics.Color.blue(r13)
            r7 = 30
            int r7 = android.graphics.Color.argb(r7, r14, r15, r8)
            r8 = 1115684864(0x42800000, float:64.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r0.searchFieldHeight = r8
            r0.needEmojiSearch = r3
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r10]
            int r13 = r0.getThemedColor(r12)
            java.lang.String r14 = "chat_emojiPanelIconSelected"
            int r15 = r0.getThemedColor(r14)
            r9 = 2131166102(0x7var_, float:1.794644E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r9, r13, r15)
            r13 = 0
            r8[r13] = r9
            int r9 = r0.getThemedColor(r12)
            int r15 = r0.getThemedColor(r14)
            r10 = 2131166099(0x7var_, float:1.7946434E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r10, r9, r15)
            r8[r6] = r9
            int r9 = r0.getThemedColor(r12)
            int r10 = r0.getThemedColor(r14)
            r15 = 2131166103(0x7var_, float:1.7946442E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r9, r10)
            r8[r11] = r9
            r0.tabIcons = r8
            r8 = 9
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r8]
            java.lang.String r9 = "chat_emojiPanelIcon"
            int r10 = r0.getThemedColor(r9)
            int r15 = r0.getThemedColor(r14)
            r11 = 2131166093(0x7var_d, float:1.7946422E38)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r11, r10, r15)
            r8[r13] = r10
            int r10 = r0.getThemedColor(r9)
            int r11 = r0.getThemedColor(r14)
            r15 = 2131166094(0x7var_e, float:1.7946424E38)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r10, r11)
            r8[r6] = r10
            int r10 = r0.getThemedColor(r9)
            int r11 = r0.getThemedColor(r14)
            r15 = 2131166087(0x7var_, float:1.794641E38)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r10, r11)
            r11 = 2
            r8[r11] = r10
            int r10 = r0.getThemedColor(r9)
            int r11 = r0.getThemedColor(r14)
            r15 = 2131166089(0x7var_, float:1.7946414E38)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r10, r11)
            r11 = 3
            r8[r11] = r10
            int r10 = r0.getThemedColor(r9)
            int r11 = r0.getThemedColor(r14)
            r15 = 2131166086(0x7var_, float:1.7946407E38)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r10, r11)
            r11 = 4
            r8[r11] = r10
            int r10 = r0.getThemedColor(r9)
            int r15 = r0.getThemedColor(r14)
            r6 = 2131166095(0x7var_f, float:1.7946426E38)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r6, r10, r15)
            r10 = 5
            r8[r10] = r6
            int r6 = r0.getThemedColor(r9)
            int r15 = r0.getThemedColor(r14)
            r10 = 2131166090(0x7var_a, float:1.7946416E38)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r10, r6, r15)
            r10 = 6
            r8[r10] = r6
            int r6 = r0.getThemedColor(r9)
            int r10 = r0.getThemedColor(r14)
            r15 = 2131166091(0x7var_b, float:1.7946418E38)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r6, r10)
            r10 = 7
            r8[r10] = r6
            int r6 = r0.getThemedColor(r9)
            int r9 = r0.getThemedColor(r14)
            r10 = 2131166088(0x7var_, float:1.7946411E38)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r10, r6, r9)
            r9 = 8
            r8[r9] = r6
            r0.emojiIcons = r8
            android.graphics.drawable.Drawable[] r6 = new android.graphics.drawable.Drawable[r11]
            int r8 = r0.getThemedColor(r12)
            int r10 = r0.getThemedColor(r14)
            r15 = 2131165413(0x7var_e5, float:1.7945042E38)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r8, r10)
            r6[r13] = r8
            int r8 = r0.getThemedColor(r12)
            int r10 = r0.getThemedColor(r14)
            r15 = 2131165409(0x7var_e1, float:1.7945034E38)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r8, r10)
            r10 = 1
            r6[r10] = r8
            int r8 = r0.getThemedColor(r12)
            int r10 = r0.getThemedColor(r14)
            r15 = 2131165412(0x7var_e4, float:1.794504E38)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r8, r10)
            r10 = 2
            r6[r10] = r8
            android.graphics.drawable.LayerDrawable r8 = new android.graphics.drawable.LayerDrawable
            android.graphics.drawable.Drawable[] r15 = new android.graphics.drawable.Drawable[r10]
            int r10 = r0.getThemedColor(r12)
            int r11 = r0.getThemedColor(r14)
            r9 = 2131165410(0x7var_e2, float:1.7945036E38)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r9, r10, r11)
            r0.searchIconDrawable = r9
            r15[r13] = r9
            java.lang.String r9 = "chat_emojiPanelStickerPackSelectorLine"
            int r10 = r0.getThemedColor(r9)
            int r11 = r0.getThemedColor(r9)
            r13 = 2131165411(0x7var_e3, float:1.7945038E38)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r13, r10, r11)
            r0.searchIconDotDrawable = r10
            r11 = 1
            r15[r11] = r10
            r8.<init>(r15)
            r10 = 3
            r6[r10] = r8
            r0.stickerIcons = r6
            r6 = 2
            android.graphics.drawable.Drawable[] r8 = new android.graphics.drawable.Drawable[r6]
            int r6 = r0.getThemedColor(r12)
            int r10 = r0.getThemedColor(r14)
            r11 = 2131166118(0x7var_a6, float:1.7946472E38)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r11, r6, r10)
            r10 = 0
            r8[r10] = r6
            int r6 = r0.getThemedColor(r12)
            int r10 = r0.getThemedColor(r14)
            r11 = 2131166117(0x7var_a5, float:1.794647E38)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r11, r6, r10)
            r10 = 1
            r8[r10] = r6
            r0.gifIcons = r8
            r6 = 8
            java.lang.String[] r8 = new java.lang.String[r6]
            java.lang.String r6 = "Emoji1"
            r10 = 2131625386(0x7f0e05aa, float:1.8877978E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r10)
            r10 = 0
            r8[r10] = r6
            java.lang.String r6 = "Emoji2"
            r10 = 2131625387(0x7f0e05ab, float:1.887798E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r10)
            r10 = 1
            r8[r10] = r6
            java.lang.String r6 = "Emoji3"
            r10 = 2131625388(0x7f0e05ac, float:1.8877983E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r10)
            r10 = 2
            r8[r10] = r6
            java.lang.String r6 = "Emoji4"
            r10 = 2131625389(0x7f0e05ad, float:1.8877985E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r10)
            r10 = 3
            r8[r10] = r6
            java.lang.String r6 = "Emoji5"
            r11 = 2131625390(0x7f0e05ae, float:1.8877987E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r11)
            r11 = 4
            r8[r11] = r6
            java.lang.String r6 = "Emoji6"
            r11 = 2131625391(0x7f0e05af, float:1.8877989E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r11)
            r11 = 5
            r8[r11] = r6
            java.lang.String r6 = "Emoji7"
            r11 = 2131625392(0x7f0e05b0, float:1.887799E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r11)
            r11 = 6
            r8[r11] = r6
            java.lang.String r6 = "Emoji8"
            r11 = 2131625393(0x7f0e05b1, float:1.8877993E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r11)
            r11 = 7
            r8[r11] = r6
            r0.emojiTitles = r8
            r0.showGifs = r1
            r6 = r31
            r0.info = r6
            android.graphics.Paint r8 = new android.graphics.Paint
            r11 = 1
            r8.<init>(r11)
            r0.dotPaint = r8
            java.lang.String r11 = "chat_emojiPanelNewTrending"
            int r11 = r0.getThemedColor(r11)
            r8.setColor(r11)
            int r8 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r8 < r11) goto L_0x02c2
            org.telegram.ui.Components.EmojiView$3 r8 = new org.telegram.ui.Components.EmojiView$3
            r8.<init>()
            r0.outlineProvider = r8
        L_0x02c2:
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r2)
            r0.emojiContainer = r8
            java.util.ArrayList<android.view.View> r12 = r0.views
            r12.add(r8)
            org.telegram.ui.Components.EmojiView$4 r8 = new org.telegram.ui.Components.EmojiView$4
            r8.<init>(r2)
            r0.emojiGridView = r8
            r12 = 1
            r8.setInstantClick(r12)
            org.telegram.ui.Components.RecyclerListView r8 = r0.emojiGridView
            androidx.recyclerview.widget.GridLayoutManager r12 = new androidx.recyclerview.widget.GridLayoutManager
            r13 = 8
            r12.<init>(r2, r13)
            r0.emojiLayoutManager = r12
            r8.setLayoutManager(r12)
            org.telegram.ui.Components.RecyclerListView r8 = r0.emojiGridView
            r12 = 1108869120(0x42180000, float:38.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r8.setTopGlowOffset(r13)
            org.telegram.ui.Components.RecyclerListView r8 = r0.emojiGridView
            r13 = 1111490560(0x42400000, float:48.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r8.setBottomGlowOffset(r13)
            org.telegram.ui.Components.RecyclerListView r8 = r0.emojiGridView
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r14 = 1110441984(0x42300000, float:44.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r10 = 0
            r8.setPadding(r10, r13, r10, r15)
            org.telegram.ui.Components.RecyclerListView r8 = r0.emojiGridView
            java.lang.String r13 = "chat_emojiPanelBackground"
            int r15 = r0.getThemedColor(r13)
            r8.setGlowColor(r15)
            org.telegram.ui.Components.RecyclerListView r8 = r0.emojiGridView
            r8.setClipToPadding(r10)
            androidx.recyclerview.widget.GridLayoutManager r8 = r0.emojiLayoutManager
            org.telegram.ui.Components.EmojiView$5 r10 = new org.telegram.ui.Components.EmojiView$5
            r10.<init>()
            r8.setSpanSizeLookup(r10)
            org.telegram.ui.Components.RecyclerListView r8 = r0.emojiGridView
            org.telegram.ui.Components.EmojiView$EmojiGridAdapter r10 = new org.telegram.ui.Components.EmojiView$EmojiGridAdapter
            r15 = 0
            r10.<init>()
            r0.emojiAdapter = r10
            r8.setAdapter(r10)
            org.telegram.ui.Components.EmojiView$EmojiSearchAdapter r8 = new org.telegram.ui.Components.EmojiView$EmojiSearchAdapter
            r8.<init>()
            r0.emojiSearchAdapter = r8
            android.widget.FrameLayout r8 = r0.emojiContainer
            org.telegram.ui.Components.RecyclerListView r10 = r0.emojiGridView
            r15 = -1082130432(0xffffffffbvar_, float:-1.0)
            r11 = -1
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r15)
            r8.addView(r10, r15)
            org.telegram.ui.Components.RecyclerListView r8 = r0.emojiGridView
            org.telegram.ui.Components.EmojiView$6 r10 = new org.telegram.ui.Components.EmojiView$6
            r11 = 1
            r10.<init>(r11)
            r8.setOnScrollListener(r10)
            org.telegram.ui.Components.RecyclerListView r8 = r0.emojiGridView
            org.telegram.ui.Components.EmojiView$7 r10 = new org.telegram.ui.Components.EmojiView$7
            r10.<init>()
            r8.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r10)
            org.telegram.ui.Components.RecyclerListView r8 = r0.emojiGridView
            org.telegram.ui.Components.EmojiView$8 r10 = new org.telegram.ui.Components.EmojiView$8
            r10.<init>()
            r8.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r10)
            org.telegram.ui.Components.EmojiView$9 r8 = new org.telegram.ui.Components.EmojiView$9
            r8.<init>(r2, r5)
            r0.emojiTabs = r8
            if (r3 == 0) goto L_0x039a
            org.telegram.ui.Components.EmojiView$SearchField r8 = new org.telegram.ui.Components.EmojiView$SearchField
            r10 = 1
            r8.<init>(r2, r10)
            r0.emojiSearchField = r8
            android.widget.FrameLayout r10 = r0.emojiContainer
            android.widget.FrameLayout$LayoutParams r11 = new android.widget.FrameLayout$LayoutParams
            int r15 = r0.searchFieldHeight
            int r18 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r15 = r15 + r18
            r14 = -1
            r11.<init>(r14, r15)
            r10.addView(r8, r11)
            org.telegram.ui.Components.EmojiView$SearchField r8 = r0.emojiSearchField
            org.telegram.ui.Components.EditTextBoldCursor r8 = r8.searchEditText
            org.telegram.ui.Components.EmojiView$10 r10 = new org.telegram.ui.Components.EmojiView$10
            r10.<init>()
            r8.setOnFocusChangeListener(r10)
        L_0x039a:
            org.telegram.ui.Components.ScrollSlidingTabStrip r8 = r0.emojiTabs
            r10 = 1
            r8.setShouldExpand(r10)
            org.telegram.ui.Components.ScrollSlidingTabStrip r8 = r0.emojiTabs
            r10 = -1
            r8.setIndicatorHeight(r10)
            org.telegram.ui.Components.ScrollSlidingTabStrip r8 = r0.emojiTabs
            r8.setUnderlineHeight(r10)
            org.telegram.ui.Components.ScrollSlidingTabStrip r8 = r0.emojiTabs
            int r11 = r0.getThemedColor(r13)
            r8.setBackgroundColor(r11)
            android.widget.FrameLayout r8 = r0.emojiContainer
            org.telegram.ui.Components.ScrollSlidingTabStrip r11 = r0.emojiTabs
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r12)
            r8.addView(r11, r14)
            org.telegram.ui.Components.ScrollSlidingTabStrip r8 = r0.emojiTabs
            org.telegram.ui.Components.EmojiView$11 r10 = new org.telegram.ui.Components.EmojiView$11
            r10.<init>()
            r8.setDelegate(r10)
            android.view.View r8 = new android.view.View
            r8.<init>(r2)
            r0.emojiTabsShadow = r8
            r10 = 0
            r8.setAlpha(r10)
            android.view.View r8 = r0.emojiTabsShadow
            r10 = 1
            java.lang.Integer r11 = java.lang.Integer.valueOf(r10)
            r8.setTag(r11)
            android.view.View r8 = r0.emojiTabsShadow
            java.lang.String r10 = "chat_emojiPanelShadowLine"
            int r11 = r0.getThemedColor(r10)
            r8.setBackgroundColor(r11)
            android.widget.FrameLayout$LayoutParams r8 = new android.widget.FrameLayout$LayoutParams
            int r11 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r14 = 51
            r15 = -1
            r8.<init>(r15, r11, r14)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r8.topMargin = r11
            android.widget.FrameLayout r11 = r0.emojiContainer
            android.view.View r12 = r0.emojiTabsShadow
            r11.addView(r12, r8)
            if (r27 == 0) goto L_0x063d
            if (r1 == 0) goto L_0x050c
            android.widget.FrameLayout r11 = new android.widget.FrameLayout
            r11.<init>(r2)
            r0.gifContainer = r11
            java.util.ArrayList<android.view.View> r12 = r0.views
            r12.add(r11)
            org.telegram.ui.Components.EmojiView$12 r11 = new org.telegram.ui.Components.EmojiView$12
            r11.<init>(r2)
            r0.gifGridView = r11
            r12 = 0
            r11.setClipToPadding(r12)
            org.telegram.ui.Components.RecyclerListView r11 = r0.gifGridView
            org.telegram.ui.Components.EmojiView$GifLayoutManager r12 = new org.telegram.ui.Components.EmojiView$GifLayoutManager
            r12.<init>(r2)
            r0.gifLayoutManager = r12
            r11.setLayoutManager(r12)
            org.telegram.ui.Components.RecyclerListView r11 = r0.gifGridView
            org.telegram.ui.Components.EmojiView$13 r12 = new org.telegram.ui.Components.EmojiView$13
            r12.<init>()
            r11.addItemDecoration(r12)
            org.telegram.ui.Components.RecyclerListView r11 = r0.gifGridView
            r12 = 1112539136(0x42500000, float:52.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r15 = 1110441984(0x42300000, float:44.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r15 = 0
            r11.setPadding(r15, r12, r15, r14)
            org.telegram.ui.Components.RecyclerListView r11 = r0.gifGridView
            r12 = 2
            r11.setOverScrollMode(r12)
            org.telegram.ui.Components.RecyclerListView r11 = r0.gifGridView
            androidx.recyclerview.widget.RecyclerView$ItemAnimator r11 = r11.getItemAnimator()
            androidx.recyclerview.widget.SimpleItemAnimator r11 = (androidx.recyclerview.widget.SimpleItemAnimator) r11
            r11.setSupportsChangeAnimations(r15)
            org.telegram.ui.Components.RecyclerListView r11 = r0.gifGridView
            org.telegram.ui.Components.EmojiView$GifAdapter r12 = new org.telegram.ui.Components.EmojiView$GifAdapter
            r14 = 1
            r12.<init>(r0, r2, r14)
            r0.gifAdapter = r12
            r11.setAdapter(r12)
            org.telegram.ui.Components.EmojiView$GifAdapter r11 = new org.telegram.ui.Components.EmojiView$GifAdapter
            r11.<init>(r0, r2)
            r0.gifSearchAdapter = r11
            org.telegram.ui.Components.RecyclerListView r11 = r0.gifGridView
            org.telegram.ui.Components.EmojiView$TypedScrollListener r12 = new org.telegram.ui.Components.EmojiView$TypedScrollListener
            r14 = 2
            r12.<init>(r14)
            r11.setOnScrollListener(r12)
            org.telegram.ui.Components.RecyclerListView r11 = r0.gifGridView
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda3 r12 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda3
            r12.<init>(r0, r5)
            r11.setOnTouchListener(r12)
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda8 r11 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda8
            r11.<init>(r0)
            r0.gifOnItemClickListener = r11
            org.telegram.ui.Components.RecyclerListView r12 = r0.gifGridView
            r12.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r11)
            android.widget.FrameLayout r11 = r0.gifContainer
            org.telegram.ui.Components.RecyclerListView r12 = r0.gifGridView
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            r15 = -1
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r14)
            r11.addView(r12, r14)
            org.telegram.ui.Components.EmojiView$SearchField r11 = new org.telegram.ui.Components.EmojiView$SearchField
            r12 = 2
            r11.<init>(r2, r12)
            r0.gifSearchField = r11
            r12 = 4
            r11.setVisibility(r12)
            android.widget.FrameLayout r11 = r0.gifContainer
            org.telegram.ui.Components.EmojiView$SearchField r12 = r0.gifSearchField
            android.widget.FrameLayout$LayoutParams r14 = new android.widget.FrameLayout$LayoutParams
            int r15 = r0.searchFieldHeight
            int r19 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r15 = r15 + r19
            r1 = -1
            r14.<init>(r1, r15)
            r11.addView(r12, r14)
            org.telegram.ui.Components.EmojiView$DraggableScrollSlidingTabStrip r1 = new org.telegram.ui.Components.EmojiView$DraggableScrollSlidingTabStrip
            r1.<init>(r2, r5)
            r0.gifTabs = r1
            org.telegram.ui.Components.ScrollSlidingTabStrip$Type r11 = org.telegram.ui.Components.ScrollSlidingTabStrip.Type.TAB
            r1.setType(r11)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.gifTabs
            int r11 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r1.setUnderlineHeight(r11)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.gifTabs
            int r11 = r0.getThemedColor(r9)
            r1.setIndicatorColor(r11)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.gifTabs
            int r11 = r0.getThemedColor(r10)
            r1.setUnderlineColor(r11)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.gifTabs
            int r11 = r0.getThemedColor(r13)
            r1.setBackgroundColor(r11)
            android.widget.FrameLayout r1 = r0.gifContainer
            org.telegram.ui.Components.ScrollSlidingTabStrip r11 = r0.gifTabs
            r12 = 48
            r14 = 51
            r15 = -1
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r15, (int) r12, (int) r14)
            r1.addView(r11, r12)
            r26.updateGifTabs()
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.gifTabs
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda10 r11 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda10
            r11.<init>(r0)
            r1.setDelegate(r11)
            org.telegram.ui.Components.EmojiView$GifAdapter r1 = r0.gifAdapter
            r1.loadTrendingGifs()
        L_0x050c:
            org.telegram.ui.Components.EmojiView$14 r1 = new org.telegram.ui.Components.EmojiView$14
            r1.<init>(r2)
            r0.stickersContainer = r1
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r11 = 0
            r1.checkStickers(r11)
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r1.checkFeaturedStickers()
            org.telegram.ui.Components.EmojiView$15 r1 = new org.telegram.ui.Components.EmojiView$15
            r1.<init>(r2)
            r0.stickersGridView = r1
            org.telegram.ui.Components.EmojiView$16 r11 = new org.telegram.ui.Components.EmojiView$16
            r12 = 5
            r11.<init>(r2, r12)
            r0.stickersLayoutManager = r11
            r1.setLayoutManager(r11)
            androidx.recyclerview.widget.GridLayoutManager r1 = r0.stickersLayoutManager
            org.telegram.ui.Components.EmojiView$17 r11 = new org.telegram.ui.Components.EmojiView$17
            r11.<init>()
            r1.setSpanSizeLookup(r11)
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            r11 = 1112539136(0x42500000, float:52.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r14 = 1110441984(0x42300000, float:44.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r14 = 0
            r1.setPadding(r14, r11, r14, r15)
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            r1.setClipToPadding(r14)
            java.util.ArrayList<android.view.View> r1 = r0.views
            android.widget.FrameLayout r11 = r0.stickersContainer
            r1.add(r11)
            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r1 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter
            r1.<init>(r2)
            r0.stickersSearchGridAdapter = r1
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            org.telegram.ui.Components.EmojiView$StickersGridAdapter r11 = new org.telegram.ui.Components.EmojiView$StickersGridAdapter
            r11.<init>(r2)
            r0.stickersGridAdapter = r11
            r1.setAdapter(r11)
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda4 r11 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda4
            r11.<init>(r0, r5)
            r1.setOnTouchListener(r11)
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda9 r1 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda9
            r1.<init>(r0)
            r0.stickersOnItemClickListener = r1
            org.telegram.ui.Components.RecyclerListView r11 = r0.stickersGridView
            r11.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            int r11 = r0.getThemedColor(r13)
            r1.setGlowColor(r11)
            android.widget.FrameLayout r1 = r0.stickersContainer
            org.telegram.ui.Components.RecyclerListView r11 = r0.stickersGridView
            r1.addView(r11)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper r1 = new org.telegram.ui.Components.RecyclerAnimationScrollHelper
            org.telegram.ui.Components.RecyclerListView r11 = r0.stickersGridView
            androidx.recyclerview.widget.GridLayoutManager r14 = r0.stickersLayoutManager
            r1.<init>(r11, r14)
            r0.scrollHelper = r1
            org.telegram.ui.Components.EmojiView$SearchField r1 = new org.telegram.ui.Components.EmojiView$SearchField
            r11 = 0
            r1.<init>(r2, r11)
            r0.stickersSearchField = r1
            android.widget.FrameLayout r11 = r0.stickersContainer
            android.widget.FrameLayout$LayoutParams r14 = new android.widget.FrameLayout$LayoutParams
            int r15 = r0.searchFieldHeight
            int r17 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r15 = r15 + r17
            r12 = -1
            r14.<init>(r12, r15)
            r11.addView(r1, r14)
            org.telegram.ui.Components.EmojiView$18 r1 = new org.telegram.ui.Components.EmojiView$18
            r1.<init>(r2, r5)
            r0.stickersTab = r1
            r11 = 1
            r1.setDragEnabled(r11)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.stickersTab
            r11 = 0
            r1.setWillNotDraw(r11)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.stickersTab
            org.telegram.ui.Components.ScrollSlidingTabStrip$Type r11 = org.telegram.ui.Components.ScrollSlidingTabStrip.Type.TAB
            r1.setType(r11)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.stickersTab
            int r11 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r1.setUnderlineHeight(r11)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.stickersTab
            int r9 = r0.getThemedColor(r9)
            r1.setIndicatorColor(r9)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.stickersTab
            int r9 = r0.getThemedColor(r10)
            r1.setUnderlineColor(r9)
            if (r4 == 0) goto L_0x0615
            org.telegram.ui.Components.EmojiView$19 r1 = new org.telegram.ui.Components.EmojiView$19
            r1.<init>(r2)
            r0.stickersTabContainer = r1
            org.telegram.ui.Components.ScrollSlidingTabStrip r9 = r0.stickersTab
            r11 = 48
            r12 = 51
            r14 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r11, (int) r12)
            r1.addView(r9, r11)
            android.widget.FrameLayout r1 = r0.stickersTabContainer
            r9 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r9)
            r4.addView(r1, r9)
            goto L_0x0625
        L_0x0615:
            r14 = -1
            android.widget.FrameLayout r1 = r0.stickersContainer
            org.telegram.ui.Components.ScrollSlidingTabStrip r9 = r0.stickersTab
            r11 = 48
            r12 = 51
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r11, (int) r12)
            r1.addView(r9, r11)
        L_0x0625:
            r26.updateStickerTabs()
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.stickersTab
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda1 r9 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda1
            r9.<init>(r0)
            r1.setDelegate(r9)
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            org.telegram.ui.Components.EmojiView$TypedScrollListener r9 = new org.telegram.ui.Components.EmojiView$TypedScrollListener
            r11 = 0
            r9.<init>(r11)
            r1.setOnScrollListener(r9)
        L_0x063d:
            org.telegram.ui.Components.EmojiView$20 r1 = new org.telegram.ui.Components.EmojiView$20
            r1.<init>(r2)
            r0.pager = r1
            org.telegram.ui.Components.EmojiView$EmojiPagesAdapter r9 = new org.telegram.ui.Components.EmojiView$EmojiPagesAdapter
            r11 = 0
            r9.<init>()
            r1.setAdapter(r9)
            android.view.View r1 = new android.view.View
            r1.<init>(r2)
            r0.topShadow = r1
            r9 = 2131165466(0x7var_a, float:1.794515E38)
            r11 = -1907225(0xffffffffffe2e5e7, float:NaN)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r9, (int) r11)
            r1.setBackgroundDrawable(r9)
            android.view.View r1 = r0.topShadow
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            r11 = -1
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r9)
            r0.addView(r1, r9)
            org.telegram.ui.Components.EmojiView$21 r1 = new org.telegram.ui.Components.EmojiView$21
            r1.<init>(r2)
            r0.backspaceButton = r1
            r9 = 2131166098(0x7var_, float:1.7946432E38)
            r1.setImageResource(r9)
            android.widget.ImageView r1 = r0.backspaceButton
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            java.lang.String r11 = "chat_emojiPanelBackspace"
            int r11 = r0.getThemedColor(r11)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r11, r12)
            r1.setColorFilter(r9)
            android.widget.ImageView r1 = r0.backspaceButton
            android.widget.ImageView$ScaleType r9 = android.widget.ImageView.ScaleType.CENTER
            r1.setScaleType(r9)
            android.widget.ImageView r1 = r0.backspaceButton
            r9 = 2131623953(0x7f0e0011, float:1.8875072E38)
            java.lang.String r11 = "AccDescrBackspace"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r1.setContentDescription(r9)
            android.widget.ImageView r1 = r0.backspaceButton
            r9 = 1
            r1.setFocusable(r9)
            android.widget.ImageView r1 = r0.backspaceButton
            org.telegram.ui.Components.EmojiView$22 r9 = new org.telegram.ui.Components.EmojiView$22
            r9.<init>()
            r1.setOnClickListener(r9)
            org.telegram.ui.Components.EmojiView$23 r1 = new org.telegram.ui.Components.EmojiView$23
            r1.<init>(r2)
            r0.bottomTabContainer = r1
            android.view.View r1 = new android.view.View
            r1.<init>(r2)
            r0.shadowLine = r1
            int r9 = r0.getThemedColor(r10)
            r1.setBackgroundColor(r9)
            android.widget.FrameLayout r1 = r0.bottomTabContainer
            android.view.View r9 = r0.shadowLine
            android.widget.FrameLayout$LayoutParams r10 = new android.widget.FrameLayout$LayoutParams
            int r11 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r12 = -1
            r10.<init>(r12, r11)
            r1.addView(r9, r10)
            android.view.View r1 = new android.view.View
            r1.<init>(r2)
            r0.bottomTabContainerBackground = r1
            android.widget.FrameLayout r9 = r0.bottomTabContainer
            android.widget.FrameLayout$LayoutParams r10 = new android.widget.FrameLayout$LayoutParams
            r11 = 1110441984(0x42300000, float:44.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r15 = 83
            r10.<init>(r12, r14, r15)
            r9.addView(r1, r10)
            r9 = 44
            if (r3 == 0) goto L_0x082d
            android.widget.FrameLayout r10 = r0.bottomTabContainer
            android.widget.FrameLayout$LayoutParams r12 = new android.widget.FrameLayout$LayoutParams
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r13 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r11 = r11 + r13
            r13 = 83
            r14 = -1
            r12.<init>(r14, r11, r13)
            r0.addView(r10, r12)
            android.widget.FrameLayout r10 = r0.bottomTabContainer
            android.widget.ImageView r11 = r0.backspaceButton
            r12 = 52
            r13 = 85
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r12, (int) r9, (int) r13)
            r10.addView(r11, r12)
            int r10 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r10 < r11) goto L_0x0728
            android.widget.ImageView r10 = r0.backspaceButton
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7)
            r10.setBackground(r11)
        L_0x0728:
            android.widget.ImageView r10 = new android.widget.ImageView
            r10.<init>(r2)
            r0.stickerSettingsButton = r10
            r11 = 2131166101(0x7var_, float:1.7946438E38)
            r10.setImageResource(r11)
            android.widget.ImageView r10 = r0.stickerSettingsButton
            android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
            java.lang.String r12 = "chat_emojiPanelBackspace"
            int r12 = r0.getThemedColor(r12)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r11.<init>(r12, r13)
            r10.setColorFilter(r11)
            android.widget.ImageView r10 = r0.stickerSettingsButton
            android.widget.ImageView$ScaleType r11 = android.widget.ImageView.ScaleType.CENTER
            r10.setScaleType(r11)
            android.widget.ImageView r10 = r0.stickerSettingsButton
            r11 = 1
            r10.setFocusable(r11)
            int r10 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r10 < r11) goto L_0x0763
            android.widget.ImageView r10 = r0.stickerSettingsButton
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7)
            r10.setBackground(r11)
        L_0x0763:
            android.widget.ImageView r10 = r0.stickerSettingsButton
            r11 = 2131627778(0x7f0e0var_, float:1.888283E38)
            java.lang.String r12 = "Settings"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r10.setContentDescription(r11)
            android.widget.FrameLayout r10 = r0.bottomTabContainer
            android.widget.ImageView r11 = r0.stickerSettingsButton
            r12 = 52
            r13 = 85
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r12, (int) r9, (int) r13)
            r10.addView(r11, r12)
            android.widget.ImageView r10 = r0.stickerSettingsButton
            org.telegram.ui.Components.EmojiView$24 r11 = new org.telegram.ui.Components.EmojiView$24
            r11.<init>()
            r10.setOnClickListener(r11)
            org.telegram.ui.Components.PagerSlidingTabStrip r10 = new org.telegram.ui.Components.PagerSlidingTabStrip
            r10.<init>(r2)
            r0.typeTabs = r10
            androidx.viewpager.widget.ViewPager r11 = r0.pager
            r10.setViewPager(r11)
            org.telegram.ui.Components.PagerSlidingTabStrip r10 = r0.typeTabs
            r11 = 0
            r10.setShouldExpand(r11)
            org.telegram.ui.Components.PagerSlidingTabStrip r10 = r0.typeTabs
            r10.setIndicatorHeight(r11)
            org.telegram.ui.Components.PagerSlidingTabStrip r10 = r0.typeTabs
            r10.setUnderlineHeight(r11)
            org.telegram.ui.Components.PagerSlidingTabStrip r10 = r0.typeTabs
            r11 = 1092616192(0x41200000, float:10.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r10.setTabPaddingLeftRight(r11)
            android.widget.FrameLayout r10 = r0.bottomTabContainer
            org.telegram.ui.Components.PagerSlidingTabStrip r11 = r0.typeTabs
            r12 = 81
            r13 = -2
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r9, (int) r12)
            r10.addView(r11, r12)
            org.telegram.ui.Components.PagerSlidingTabStrip r10 = r0.typeTabs
            org.telegram.ui.Components.EmojiView$25 r11 = new org.telegram.ui.Components.EmojiView$25
            r11.<init>()
            r10.setOnPageChangeListener(r11)
            android.widget.ImageView r10 = new android.widget.ImageView
            r10.<init>(r2)
            r0.searchButton = r10
            r11 = 2131166100(0x7var_, float:1.7946436E38)
            r10.setImageResource(r11)
            android.widget.ImageView r10 = r0.searchButton
            android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
            java.lang.String r12 = "chat_emojiPanelBackspace"
            int r12 = r0.getThemedColor(r12)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r11.<init>(r12, r13)
            r10.setColorFilter(r11)
            android.widget.ImageView r10 = r0.searchButton
            android.widget.ImageView$ScaleType r11 = android.widget.ImageView.ScaleType.CENTER
            r10.setScaleType(r11)
            android.widget.ImageView r10 = r0.searchButton
            r11 = 2131627616(0x7f0e0e60, float:1.8882501E38)
            java.lang.String r12 = "Search"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r10.setContentDescription(r11)
            android.widget.ImageView r10 = r0.searchButton
            r11 = 1
            r10.setFocusable(r11)
            int r10 = android.os.Build.VERSION.SDK_INT
            r11 = 21
            if (r10 < r11) goto L_0x0812
            android.widget.ImageView r10 = r0.searchButton
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7)
            r10.setBackground(r11)
        L_0x0812:
            android.widget.FrameLayout r10 = r0.bottomTabContainer
            android.widget.ImageView r11 = r0.searchButton
            r12 = 52
            r13 = 83
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r12, (int) r9, (int) r13)
            r10.addView(r11, r9)
            android.widget.ImageView r9 = r0.searchButton
            org.telegram.ui.Components.EmojiView$26 r10 = new org.telegram.ui.Components.EmojiView$26
            r10.<init>()
            r9.setOnClickListener(r10)
            goto L_0x097a
        L_0x082d:
            android.widget.FrameLayout r10 = r0.bottomTabContainer
            int r11 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            if (r11 < r12) goto L_0x0838
            r11 = 40
            goto L_0x083a
        L_0x0838:
            r11 = 44
        L_0x083a:
            int r19 = r11 + 20
            int r11 = android.os.Build.VERSION.SDK_INT
            if (r11 < r12) goto L_0x0843
            r11 = 40
            goto L_0x0845
        L_0x0843:
            r11 = 44
        L_0x0845:
            int r11 = r11 + 12
            float r11 = (float) r11
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x084f
            r16 = 3
            goto L_0x0851
        L_0x084f:
            r16 = 5
        L_0x0851:
            r21 = r16 | 80
            r22 = 0
            r23 = 0
            r24 = 1073741824(0x40000000, float:2.0)
            r25 = 0
            r20 = r11
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r10, r11)
            r10 = 1113587712(0x42600000, float:56.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r11 = r0.getThemedColor(r13)
            int r12 = r0.getThemedColor(r13)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r10, r11, r12)
            int r11 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            if (r11 >= r12) goto L_0x08ae
            android.content.res.Resources r11 = r29.getResources()
            r12 = 2131165435(0x7var_fb, float:1.7945087E38)
            android.graphics.drawable.Drawable r11 = r11.getDrawable(r12)
            android.graphics.drawable.Drawable r11 = r11.mutate()
            android.graphics.PorterDuffColorFilter r12 = new android.graphics.PorterDuffColorFilter
            r13 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r12.<init>(r13, r14)
            r11.setColorFilter(r12)
            org.telegram.ui.Components.CombinedDrawable r12 = new org.telegram.ui.Components.CombinedDrawable
            r13 = 0
            r12.<init>(r11, r10, r13, r13)
            r13 = 1109393408(0x42200000, float:40.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r14 = 1109393408(0x42200000, float:40.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r12.setIconSize(r13, r14)
            r10 = r12
            goto L_0x091d
        L_0x08ae:
            android.animation.StateListAnimator r11 = new android.animation.StateListAnimator
            r11.<init>()
            r12 = 1
            int[] r13 = new int[r12]
            r12 = 16842919(0x10100a7, float:2.3694026E-38)
            r14 = 0
            r13[r14] = r12
            android.widget.ImageView r12 = r0.floatingButton
            android.util.Property r15 = android.view.View.TRANSLATION_Z
            r1 = 2
            float[] r9 = new float[r1]
            r1 = 1073741824(0x40000000, float:2.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r9[r14] = r1
            r1 = 1082130432(0x40800000, float:4.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r16 = 1
            r9[r16] = r1
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r12, r15, r9)
            r14 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r1 = r1.setDuration(r14)
            r11.addState(r13, r1)
            r1 = 0
            int[] r9 = new int[r1]
            android.widget.ImageView r12 = r0.floatingButton
            android.util.Property r13 = android.view.View.TRANSLATION_Z
            r14 = 2
            float[] r15 = new float[r14]
            r14 = 1082130432(0x40800000, float:4.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r15[r1] = r14
            r1 = 1073741824(0x40000000, float:2.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r14 = 1
            r15[r14] = r1
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r12, r13, r15)
            r12 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r1 = r1.setDuration(r12)
            r11.addState(r9, r1)
            android.widget.ImageView r1 = r0.backspaceButton
            r1.setStateListAnimator(r11)
            android.widget.ImageView r1 = r0.backspaceButton
            org.telegram.ui.Components.EmojiView$27 r9 = new org.telegram.ui.Components.EmojiView$27
            r9.<init>()
            r1.setOutlineProvider(r9)
        L_0x091d:
            android.widget.ImageView r1 = r0.backspaceButton
            r9 = 1073741824(0x40000000, float:2.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r11 = 0
            r1.setPadding(r11, r11, r9, r11)
            android.widget.ImageView r1 = r0.backspaceButton
            r1.setBackground(r10)
            android.widget.ImageView r1 = r0.backspaceButton
            r9 = 2131623953(0x7f0e0011, float:1.8875072E38)
            java.lang.String r11 = "AccDescrBackspace"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r1.setContentDescription(r9)
            android.widget.ImageView r1 = r0.backspaceButton
            r9 = 1
            r1.setFocusable(r9)
            android.widget.FrameLayout r1 = r0.bottomTabContainer
            android.widget.ImageView r9 = r0.backspaceButton
            int r11 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            if (r11 < r12) goto L_0x094f
            r19 = 40
            goto L_0x0951
        L_0x094f:
            r19 = 44
        L_0x0951:
            int r11 = android.os.Build.VERSION.SDK_INT
            if (r11 < r12) goto L_0x0958
            r11 = 40
            goto L_0x095a
        L_0x0958:
            r11 = 44
        L_0x095a:
            float r11 = (float) r11
            r21 = 51
            r22 = 1092616192(0x41200000, float:10.0)
            r23 = 0
            r24 = 1092616192(0x41200000, float:10.0)
            r25 = 0
            r20 = r11
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r1.addView(r9, r11)
            android.view.View r1 = r0.shadowLine
            r9 = 8
            r1.setVisibility(r9)
            android.view.View r1 = r0.bottomTabContainerBackground
            r1.setVisibility(r9)
        L_0x097a:
            androidx.viewpager.widget.ViewPager r1 = r0.pager
            r9 = 51
            r10 = -1
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r10, (int) r9)
            r10 = 0
            r0.addView(r1, r10, r9)
            org.telegram.ui.Components.CorrectlyMeasuringTextView r1 = new org.telegram.ui.Components.CorrectlyMeasuringTextView
            r1.<init>(r2)
            r0.mediaBanTooltip = r1
            r9 = 1077936128(0x40400000, float:3.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            java.lang.String r10 = "chat_gifSaveHintBackground"
            int r10 = r0.getThemedColor(r10)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r9, r10)
            r1.setBackgroundDrawable(r9)
            android.widget.TextView r1 = r0.mediaBanTooltip
            java.lang.String r9 = "chat_gifSaveHintText"
            int r9 = r0.getThemedColor(r9)
            r1.setTextColor(r9)
            android.widget.TextView r1 = r0.mediaBanTooltip
            r9 = 1090519040(0x41000000, float:8.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r10 = 1088421888(0x40e00000, float:7.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r11 = 1090519040(0x41000000, float:8.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r12 = 1088421888(0x40e00000, float:7.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.setPadding(r9, r10, r11, r12)
            android.widget.TextView r1 = r0.mediaBanTooltip
            r9 = 16
            r1.setGravity(r9)
            android.widget.TextView r1 = r0.mediaBanTooltip
            r9 = 1096810496(0x41600000, float:14.0)
            r10 = 1
            r1.setTextSize(r10, r9)
            android.widget.TextView r1 = r0.mediaBanTooltip
            r9 = 4
            r1.setVisibility(r9)
            android.widget.TextView r1 = r0.mediaBanTooltip
            r9 = -2
            r10 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r11 = 81
            r12 = 1084227584(0x40a00000, float:5.0)
            r13 = 0
            r14 = 1084227584(0x40a00000, float:5.0)
            r15 = 1112801280(0x42540000, float:53.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r0.addView(r1, r9)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x09fc
            r1 = 1109393408(0x42200000, float:40.0)
            goto L_0x09fe
        L_0x09fc:
            r1 = 1107296256(0x42000000, float:32.0)
        L_0x09fe:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.emojiSize = r1
            org.telegram.ui.Components.EmojiView$EmojiColorPickerView r1 = new org.telegram.ui.Components.EmojiView$EmojiColorPickerView
            r1.<init>(r2)
            r0.pickerView = r1
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = new org.telegram.ui.Components.EmojiView$EmojiPopupWindow
            org.telegram.ui.Components.EmojiView$EmojiColorPickerView r9 = r0.pickerView
            boolean r10 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r10 == 0) goto L_0x0a18
            r10 = 40
            goto L_0x0a1a
        L_0x0a18:
            r10 = 32
        L_0x0a1a:
            int r10 = r10 * 6
            int r10 = r10 + 10
            int r10 = r10 + 20
            float r10 = (float) r10
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r0.popupWidth = r10
            boolean r11 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r11 == 0) goto L_0x0a30
            r11 = 1115684864(0x42800000, float:64.0)
            goto L_0x0a32
        L_0x0a30:
            r11 = 1113587712(0x42600000, float:56.0)
        L_0x0a32:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r0.popupHeight = r11
            r1.<init>(r9, r10, r11)
            r0.pickerViewPopup = r1
            r9 = 1
            r1.setOutsideTouchable(r9)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            r1.setClippingEnabled(r9)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            r10 = 2
            r1.setInputMethodMode(r10)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            r10 = 0
            r1.setSoftInputMode(r10)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            android.view.View r1 = r1.getContentView()
            r1.setFocusableInTouchMode(r9)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            android.view.View r1 = r1.getContentView()
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda2 r9 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda2
            r9.<init>(r0)
            r1.setOnKeyListener(r9)
            android.content.SharedPreferences r1 = org.telegram.messenger.MessagesController.getGlobalEmojiSettings()
            java.lang.String r9 = "selected_page"
            r10 = 0
            int r1 = r1.getInt(r9, r10)
            r0.currentPage = r1
            org.telegram.messenger.Emoji.loadRecentEmoji()
            org.telegram.ui.Components.EmojiView$EmojiGridAdapter r1 = r0.emojiAdapter
            r1.notifyDataSetChanged()
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            if (r1 == 0) goto L_0x0ab1
            java.util.ArrayList<android.view.View> r1 = r0.views
            int r1 = r1.size()
            r9 = 1
            if (r1 != r9) goto L_0x0a9a
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0a9a
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            r9 = 4
            r1.setVisibility(r9)
            goto L_0x0ab1
        L_0x0a9a:
            java.util.ArrayList<android.view.View> r1 = r0.views
            int r1 = r1.size()
            r9 = 1
            if (r1 == r9) goto L_0x0ab1
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            int r1 = r1.getVisibility()
            if (r1 == 0) goto L_0x0ab1
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            r9 = 0
            r1.setVisibility(r9)
        L_0x0ab1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.<init>(boolean, boolean, android.content.Context, boolean, org.telegram.tgnet.TLRPC$ChatFull, android.view.ViewGroup, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-EmojiView  reason: not valid java name */
    public /* synthetic */ boolean m2242lambda$new$1$orgtelegramuiComponentsEmojiView(Theme.ResourcesProvider resourcesProvider2, View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.gifGridView, 0, this.gifOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider2);
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-EmojiView  reason: not valid java name */
    public /* synthetic */ void m2243lambda$new$2$orgtelegramuiComponentsEmojiView(View view, int position) {
        if (this.delegate != null) {
            int position2 = position - 1;
            RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
            GifAdapter gifAdapter2 = this.gifAdapter;
            if (adapter != gifAdapter2) {
                RecyclerView.Adapter adapter2 = this.gifGridView.getAdapter();
                GifAdapter gifAdapter3 = this.gifSearchAdapter;
                if (adapter2 == gifAdapter3 && position2 >= 0 && position2 < gifAdapter3.results.size()) {
                    this.delegate.onGifSelected(view, this.gifSearchAdapter.results.get(position2), this.gifSearchAdapter.lastSearchImageString, this.gifSearchAdapter.bot, true, 0);
                    updateRecentGifs();
                }
            } else if (position2 >= 0) {
                if (position2 < gifAdapter2.recentItemsCount) {
                    this.delegate.onGifSelected(view, this.recentGifs.get(position2), (String) null, "gif", true, 0);
                    return;
                }
                int resultPos = position2;
                if (this.gifAdapter.recentItemsCount > 0) {
                    resultPos = (resultPos - this.gifAdapter.recentItemsCount) - 1;
                }
                if (resultPos >= 0 && resultPos < this.gifAdapter.results.size()) {
                    this.delegate.onGifSelected(view, this.gifAdapter.results.get(resultPos), (String) null, this.gifAdapter.bot, true, 0);
                }
            }
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-EmojiView  reason: not valid java name */
    public /* synthetic */ void m2244lambda$new$3$orgtelegramuiComponentsEmojiView(int page) {
        if (page != this.gifTrendingTabNum || !this.gifAdapter.results.isEmpty()) {
            this.gifGridView.stopScroll();
            this.gifTabs.onPageScrolled(page, 0);
            int i = 1;
            if (page == this.gifRecentTabNum || page == this.gifTrendingTabNum) {
                this.gifSearchField.searchEditText.setText("");
                if (page != this.gifTrendingTabNum || this.gifAdapter.trendingSectionItem < 1) {
                    GifLayoutManager gifLayoutManager2 = this.gifLayoutManager;
                    EmojiViewDelegate emojiViewDelegate = this.delegate;
                    if (emojiViewDelegate != null && emojiViewDelegate.isExpanded()) {
                        i = 0;
                    }
                    gifLayoutManager2.scrollToPositionWithOffset(i, 0);
                } else {
                    this.gifLayoutManager.scrollToPositionWithOffset(this.gifAdapter.trendingSectionItem, -AndroidUtilities.dp(4.0f));
                }
                if (page == this.gifTrendingTabNum) {
                    ArrayList<String> gifSearchEmojies = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
                    if (!gifSearchEmojies.isEmpty()) {
                        this.gifSearchPreloader.preload(gifSearchEmojies.get(0));
                    }
                }
            } else {
                ArrayList<String> gifSearchEmojies2 = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
                this.gifSearchAdapter.searchEmoji(gifSearchEmojies2.get(page - this.gifFirstEmojiTabNum));
                int i2 = this.gifFirstEmojiTabNum;
                if (page - i2 > 0) {
                    this.gifSearchPreloader.preload(gifSearchEmojies2.get((page - i2) - 1));
                }
                if (page - this.gifFirstEmojiTabNum < gifSearchEmojies2.size() - 1) {
                    this.gifSearchPreloader.preload(gifSearchEmojies2.get((page - this.gifFirstEmojiTabNum) + 1));
                }
            }
            resetTabsY(2);
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-EmojiView  reason: not valid java name */
    public /* synthetic */ boolean m2245lambda$new$4$orgtelegramuiComponentsEmojiView(Theme.ResourcesProvider resourcesProvider2, View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider2);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-EmojiView  reason: not valid java name */
    public /* synthetic */ void m2246lambda$new$5$orgtelegramuiComponentsEmojiView(View view, int position) {
        String query = null;
        RecyclerView.Adapter adapter = this.stickersGridView.getAdapter();
        StickersSearchGridAdapter stickersSearchGridAdapter2 = this.stickersSearchGridAdapter;
        if (adapter == stickersSearchGridAdapter2) {
            query = stickersSearchGridAdapter2.searchQuery;
            TLRPC.StickerSetCovered pack = (TLRPC.StickerSetCovered) this.stickersSearchGridAdapter.positionsToSets.get(position);
            if (pack != null) {
                this.delegate.onShowStickerSet(pack.set, (TLRPC.InputStickerSet) null);
                return;
            }
        }
        if (view instanceof StickerEmojiCell) {
            ContentPreviewViewer.getInstance().reset();
            StickerEmojiCell cell = (StickerEmojiCell) view;
            if (!cell.isDisabled()) {
                cell.disable();
                this.delegate.onStickerSelected(cell, cell.getSticker(), query, cell.getParentObject(), cell.getSendAnimationData(), true, 0);
            }
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-EmojiView  reason: not valid java name */
    public /* synthetic */ void m2247lambda$new$6$orgtelegramuiComponentsEmojiView(int page) {
        int firstTab;
        if (!this.firstTabUpdate) {
            if (page == this.trendingTabNum) {
                openTrendingStickers((TLRPC.StickerSetCovered) null);
            } else if (page == this.recentTabBum) {
                this.stickersGridView.stopScroll();
                scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("recent"), 0);
                resetTabsY(0);
                ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
                int i = this.recentTabBum;
                scrollSlidingTabStrip.onPageScrolled(i, i > 0 ? i : this.stickersTabOffset);
            } else if (page == this.favTabBum) {
                this.stickersGridView.stopScroll();
                scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("fav"), 0);
                resetTabsY(0);
                ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
                int i2 = this.favTabBum;
                scrollSlidingTabStrip2.onPageScrolled(i2, i2 > 0 ? i2 : this.stickersTabOffset);
            } else {
                int index = page - this.stickersTabOffset;
                if (index < this.stickerSets.size()) {
                    if (index >= this.stickerSets.size()) {
                        index = this.stickerSets.size() - 1;
                    }
                    this.firstStickersAttach = false;
                    this.stickersGridView.stopScroll();
                    scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack(this.stickerSets.get(index)), 0);
                    resetTabsY(0);
                    checkScroll(0);
                    if (this.favTabBum > 0) {
                        firstTab = this.favTabBum;
                    } else if (this.recentTabBum > 0) {
                        firstTab = this.recentTabBum;
                    } else {
                        firstTab = this.stickersTabOffset;
                    }
                    this.stickersTab.onPageScrolled(page, firstTab);
                    this.expandStickersByDragg = false;
                    updateStickerTabsPosition();
                }
            }
        }
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-EmojiView  reason: not valid java name */
    public /* synthetic */ boolean m2248lambda$new$7$orgtelegramuiComponentsEmojiView(View v, int keyCode, KeyEvent event) {
        EmojiPopupWindow emojiPopupWindow;
        if (keyCode != 82 || event.getRepeatCount() != 0 || event.getAction() != 1 || (emojiPopupWindow = this.pickerViewPopup) == null || !emojiPopupWindow.isShowing()) {
            return false;
        }
        this.pickerViewPopup.dismiss();
        return true;
    }

    /* access modifiers changed from: private */
    public void createStickersChooseActionTracker() {
        AnonymousClass28 r0 = new ChooseStickerActionTracker(this.currentAccount, this.delegate.getDialogId(), this.delegate.getThreadId()) {
            public boolean isShown() {
                return EmojiView.this.delegate != null && EmojiView.this.getVisibility() == 0 && EmojiView.this.stickersContainerAttached;
            }
        };
        this.chooseStickerActionTracker = r0;
        r0.checkVisibility();
    }

    /* access modifiers changed from: private */
    public void checkGridVisibility(int position, float positionOffset) {
        if (this.stickersContainer != null && this.gifContainer != null) {
            int i = 0;
            if (position == 0) {
                this.emojiGridView.setVisibility(0);
                this.gifGridView.setVisibility(positionOffset == 0.0f ? 8 : 0);
                ScrollSlidingTabStrip scrollSlidingTabStrip = this.gifTabs;
                if (positionOffset == 0.0f) {
                    i = 8;
                }
                scrollSlidingTabStrip.setVisibility(i);
                this.stickersGridView.setVisibility(8);
                this.stickersTab.setVisibility(8);
            } else if (position == 1) {
                this.emojiGridView.setVisibility(8);
                this.gifGridView.setVisibility(0);
                this.gifTabs.setVisibility(0);
                this.stickersGridView.setVisibility(positionOffset == 0.0f ? 8 : 0);
                ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
                if (positionOffset == 0.0f) {
                    i = 8;
                }
                scrollSlidingTabStrip2.setVisibility(i);
            } else if (position == 2) {
                this.emojiGridView.setVisibility(8);
                this.gifGridView.setVisibility(8);
                this.gifTabs.setVisibility(8);
                this.stickersGridView.setVisibility(0);
                this.stickersTab.setVisibility(0);
            }
        }
    }

    /* access modifiers changed from: private */
    public static String addColorToCode(String code, String color) {
        String end = null;
        int length = code.length();
        if (length > 2 && code.charAt(code.length() - 2) == 8205) {
            end = code.substring(code.length() - 2);
            code = code.substring(0, code.length() - 2);
        } else if (length > 3 && code.charAt(code.length() - 3) == 8205) {
            end = code.substring(code.length() - 3);
            code = code.substring(0, code.length() - 3);
        }
        String code2 = code + color;
        if (end == null) {
            return code2;
        }
        return code2 + end;
    }

    /* access modifiers changed from: private */
    public void openTrendingStickers(TLRPC.StickerSetCovered set) {
        this.delegate.showTrendingStickersAlert(new TrendingStickersLayout(getContext(), new TrendingStickersLayout.Delegate() {
            public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSet, boolean primary) {
                EmojiView.this.delegate.onStickerSetAdd(stickerSet);
                if (primary) {
                    EmojiView.this.updateStickerTabs();
                }
            }

            public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSet) {
                EmojiView.this.delegate.onStickerSetRemove(stickerSet);
            }

            public boolean onListViewInterceptTouchEvent(RecyclerListView listView, MotionEvent event) {
                return ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, listView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
            }

            public boolean onListViewTouchEvent(RecyclerListView listView, RecyclerListView.OnItemClickListener onItemClickListener, MotionEvent event) {
                return ContentPreviewViewer.getInstance().onTouch(event, listView, EmojiView.this.getMeasuredHeight(), onItemClickListener, EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
            }

            public String[] getLastSearchKeyboardLanguage() {
                return EmojiView.this.lastSearchKeyboardLanguage;
            }

            public void setLastSearchKeyboardLanguage(String[] language) {
                String[] unused = EmojiView.this.lastSearchKeyboardLanguage = language;
            }

            public boolean canSendSticker() {
                return true;
            }

            public void onStickerSelected(TLRPC.Document sticker, Object parent, boolean clearsInputField, boolean notify, int scheduleDate) {
                EmojiView.this.delegate.onStickerSelected((View) null, sticker, (String) null, parent, (MessageObject.SendAnimationData) null, notify, scheduleDate);
            }

            public boolean canSchedule() {
                return EmojiView.this.delegate.canSchedule();
            }

            public boolean isInScheduleMode() {
                return EmojiView.this.delegate.isInScheduleMode();
            }
        }, this.primaryInstallingStickerSets, this.installingStickerSets, this.removingStickerSets, set, this.resourcesProvider));
    }

    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        updateBottomTabContainerPosition();
        updateStickerTabsPosition();
    }

    /* access modifiers changed from: private */
    public void updateStickerTabsPosition() {
        if (this.stickersTabContainer != null) {
            boolean visible = getVisibility() == 0 && this.stickersContainerAttached && this.delegate.getProgressToSearchOpened() != 1.0f;
            this.stickersTabContainer.setVisibility(visible ? 0 : 8);
            if (visible) {
                this.rect.setEmpty();
                this.pager.getChildVisibleRect(this.stickersContainer, this.rect, (Point) null);
                float searchProgressOffset = ((float) AndroidUtilities.dp(50.0f)) * this.delegate.getProgressToSearchOpened();
                int left = this.rect.left;
                if (!(left == 0 && searchProgressOffset == 0.0f)) {
                    this.expandStickersByDragg = false;
                }
                this.stickersTabContainer.setTranslationX((float) left);
                float y = (((((float) getTop()) + getTranslationY()) - ((float) this.stickersTabContainer.getTop())) - this.stickersTab.getExpandedOffset()) - searchProgressOffset;
                if (this.stickersTabContainer.getTranslationY() != y) {
                    this.stickersTabContainer.setTranslationY(y);
                    this.stickersTabContainer.invalidate();
                }
            }
            if (!this.expandStickersByDragg || !visible || !this.showing) {
                this.expandStickersByDragg = false;
                this.stickersTab.expandStickers(this.lastStickersX, false);
                return;
            }
            this.stickersTab.expandStickers(this.lastStickersX, true);
        }
    }

    private void updateBottomTabContainerPosition() {
        View parent;
        float y;
        if (this.bottomTabContainer.getTag() == null) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && (parent = (View) getParent()) != null) {
                float y2 = getY() - ((float) parent.getHeight());
                if (getLayoutParams().height > 0) {
                    y = y2 + ((float) getLayoutParams().height);
                } else {
                    y = y2 + ((float) getMeasuredHeight());
                }
                if (((float) this.bottomTabContainer.getTop()) - y < 0.0f) {
                    y = (float) this.bottomTabContainer.getTop();
                }
                this.bottomTabContainer.setTranslationY(-y);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        updateBottomTabContainerPosition();
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: private */
    public void startStopVisibleGifs(boolean start) {
        RecyclerListView recyclerListView = this.gifGridView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.gifGridView.getChildAt(a);
                if (child instanceof ContextLinkCell) {
                    ImageReceiver imageReceiver = ((ContextLinkCell) child).getPhotoImage();
                    if (start) {
                        imageReceiver.setAllowStartAnimation(true);
                        imageReceiver.startAnimation();
                    } else {
                        imageReceiver.setAllowStartAnimation(false);
                        imageReceiver.stopAnimation();
                    }
                }
            }
        }
    }

    public void addEmojiToRecent(String code) {
        if (Emoji.isValidEmoji(code)) {
            int size = Emoji.recentEmoji.size();
            Emoji.addRecentEmoji(code);
            if (!(getVisibility() == 0 && this.pager.getCurrentItem() == 0)) {
                Emoji.sortEmoji();
                this.emojiAdapter.notifyDataSetChanged();
            }
            Emoji.saveRecentEmoji();
        }
    }

    public void showSearchField(boolean show) {
        for (int a = 0; a < 3; a++) {
            GridLayoutManager layoutManager = getLayoutManagerForType(a);
            int position = layoutManager.findFirstVisibleItemPosition();
            if (show) {
                if (position == 1 || position == 2) {
                    layoutManager.scrollToPosition(0);
                    resetTabsY(a);
                }
            } else if (position == 0) {
                layoutManager.scrollToPositionWithOffset(1, 0);
            }
        }
    }

    public void hideSearchKeyboard() {
        SearchField searchField = this.stickersSearchField;
        if (searchField != null) {
            searchField.hideKeyboard();
        }
        SearchField searchField2 = this.gifSearchField;
        if (searchField2 != null) {
            searchField2.hideKeyboard();
        }
        SearchField searchField3 = this.emojiSearchField;
        if (searchField3 != null) {
            searchField3.hideKeyboard();
        }
    }

    /* access modifiers changed from: private */
    public void openSearch(SearchField searchField) {
        GridLayoutManager layoutManager;
        ScrollSlidingTabStrip tabStrip;
        final RecyclerListView gridView;
        SearchField currentField;
        EmojiViewDelegate emojiViewDelegate;
        AnimatorSet animatorSet = this.searchAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        this.firstStickersAttach = false;
        this.firstGifAttach = false;
        this.firstEmojiAttach = false;
        for (int a = 0; a < 3; a++) {
            boolean z = true;
            if (a == 0) {
                currentField = this.emojiSearchField;
                gridView = this.emojiGridView;
                tabStrip = this.emojiTabs;
                layoutManager = this.emojiLayoutManager;
            } else if (a == 1) {
                currentField = this.gifSearchField;
                gridView = this.gifGridView;
                tabStrip = this.gifTabs;
                layoutManager = this.gifLayoutManager;
            } else {
                currentField = this.stickersSearchField;
                gridView = this.stickersGridView;
                tabStrip = this.stickersTab;
                layoutManager = this.stickersLayoutManager;
            }
            if (currentField == null) {
                SearchField searchField2 = searchField;
            } else if (searchField != currentField || (emojiViewDelegate = this.delegate) == null || !emojiViewDelegate.isExpanded()) {
                currentField.setTranslationY((float) AndroidUtilities.dp(0.0f));
                if (!(tabStrip == null || a == 2)) {
                    tabStrip.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                }
                if (gridView == this.stickersGridView) {
                    gridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                } else if (gridView == this.emojiGridView || gridView == this.gifGridView) {
                    gridView.setPadding(0, 0, 0, 0);
                }
                if (gridView == this.gifGridView) {
                    GifAdapter gifAdapter2 = this.gifSearchAdapter;
                    if (this.gifAdapter.results.size() <= 0) {
                        z = false;
                    }
                    if (gifAdapter2.showTrendingWhenSearchEmpty = z) {
                        this.gifSearchAdapter.search("");
                        RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
                        GifAdapter gifAdapter3 = this.gifSearchAdapter;
                        if (adapter != gifAdapter3) {
                            this.gifGridView.setAdapter(gifAdapter3);
                        }
                    }
                }
                layoutManager.scrollToPositionWithOffset(0, 0);
            } else {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.searchAnimation = animatorSet2;
                if (tabStrip == null || a == 2) {
                    animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))}), ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)})});
                } else {
                    animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(tabStrip, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))}), ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))}), ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)})});
                }
                this.searchAnimation.setDuration(220);
                this.searchAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(EmojiView.this.searchAnimation)) {
                            gridView.setTranslationY(0.0f);
                            if (gridView == EmojiView.this.stickersGridView) {
                                gridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                            } else if (gridView == EmojiView.this.emojiGridView || gridView == EmojiView.this.gifGridView) {
                                gridView.setPadding(0, 0, 0, 0);
                            }
                            AnimatorSet unused = EmojiView.this.searchAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (animation.equals(EmojiView.this.searchAnimation)) {
                            AnimatorSet unused = EmojiView.this.searchAnimation = null;
                        }
                    }
                });
                this.searchAnimation.start();
            }
        }
        SearchField searchField3 = searchField;
    }

    private void showEmojiShadow(boolean show, boolean animated) {
        if (show && this.emojiTabsShadow.getTag() == null) {
            return;
        }
        if (show || this.emojiTabsShadow.getTag() == null) {
            AnimatorSet animatorSet = this.emojiTabShadowAnimator;
            int i = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.emojiTabShadowAnimator = null;
            }
            View view = this.emojiTabsShadow;
            if (!show) {
                i = 1;
            }
            view.setTag(i);
            float f = 1.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.emojiTabShadowAnimator = animatorSet2;
                Animator[] animatorArr = new Animator[1];
                View view2 = this.emojiTabsShadow;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr[0] = f;
                animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                animatorSet2.playTogether(animatorArr);
                this.emojiTabShadowAnimator.setDuration(200);
                this.emojiTabShadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.emojiTabShadowAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AnimatorSet unused = EmojiView.this.emojiTabShadowAnimator = null;
                    }
                });
                this.emojiTabShadowAnimator.start();
                return;
            }
            View view3 = this.emojiTabsShadow;
            if (!show) {
                f = 0.0f;
            }
            view3.setAlpha(f);
        }
    }

    public void closeSearch(boolean animated) {
        closeSearch(animated, -1);
    }

    private void scrollStickersToPosition(int p, int offset) {
        View view = this.stickersLayoutManager.findViewByPosition(p);
        int firstPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (view != null || Math.abs(p - firstPosition) <= 40) {
            this.ignoreStickersScroll = true;
            this.stickersGridView.smoothScrollToPosition(p);
            return;
        }
        this.scrollHelper.setScrollDirection(this.stickersLayoutManager.findFirstVisibleItemPosition() < p ? 0 : 1);
        this.scrollHelper.scrollToPosition(p, offset, false, true);
    }

    public void closeSearch(boolean animated, long scrollToSet) {
        ScrollSlidingTabStrip tabStrip;
        final GridLayoutManager layoutManager;
        final RecyclerListView gridView;
        SearchField currentField;
        TLRPC.TL_messages_stickerSet set;
        int pos;
        boolean z = animated;
        long j = scrollToSet;
        AnimatorSet animatorSet = this.searchAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        int currentItem = this.pager.getCurrentItem();
        int i = 2;
        if (currentItem == 2 && j != -1 && (set = MediaDataController.getInstance(this.currentAccount).getStickerSetById(j)) != null && (pos = this.stickersGridAdapter.getPositionForPack(set)) >= 0 && pos < this.stickersGridAdapter.getItemCount()) {
            scrollStickersToPosition(pos, AndroidUtilities.dp(60.0f));
        }
        GifAdapter gifAdapter2 = this.gifSearchAdapter;
        if (gifAdapter2 != null) {
            boolean unused = gifAdapter2.showTrendingWhenSearchEmpty = false;
        }
        int a = 0;
        while (a < 3) {
            if (a == 0) {
                currentField = this.emojiSearchField;
                gridView = this.emojiGridView;
                layoutManager = this.emojiLayoutManager;
                tabStrip = this.emojiTabs;
            } else if (a == 1) {
                currentField = this.gifSearchField;
                gridView = this.gifGridView;
                layoutManager = this.gifLayoutManager;
                tabStrip = this.gifTabs;
            } else {
                currentField = this.stickersSearchField;
                gridView = this.stickersGridView;
                layoutManager = this.stickersLayoutManager;
                tabStrip = this.stickersTab;
            }
            if (currentField != null) {
                currentField.searchEditText.setText("");
                if (a != currentItem || !z) {
                    currentField.setTranslationY((float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight));
                    if (tabStrip != null) {
                        if (a != 2) {
                            tabStrip.setTranslationY(0.0f);
                        }
                    }
                    if (gridView == this.stickersGridView) {
                        gridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(44.0f));
                    } else if (gridView == this.gifGridView) {
                        gridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(44.0f));
                    } else if (gridView == this.emojiGridView) {
                        gridView.setPadding(0, AndroidUtilities.dp(38.0f), 0, AndroidUtilities.dp(44.0f));
                    }
                    layoutManager.scrollToPositionWithOffset(1, 0);
                } else {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (tabStrip == null || a == i) {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)}), ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, new float[]{(float) (-this.searchFieldHeight)})});
                    } else {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(tabStrip, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)}), ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)})});
                    }
                    this.searchAnimation.setDuration(200);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(EmojiView.this.searchAnimation)) {
                                int firstVisPos = layoutManager.findFirstVisibleItemPosition();
                                int top = 0;
                                if (firstVisPos != -1) {
                                    top = (int) (((float) layoutManager.findViewByPosition(firstVisPos).getTop()) + gridView.getTranslationY());
                                }
                                gridView.setTranslationY(0.0f);
                                if (gridView == EmojiView.this.stickersGridView) {
                                    gridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(44.0f));
                                } else if (gridView == EmojiView.this.gifGridView) {
                                    gridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(44.0f));
                                } else if (gridView == EmojiView.this.emojiGridView) {
                                    gridView.setPadding(0, AndroidUtilities.dp(38.0f), 0, AndroidUtilities.dp(44.0f));
                                }
                                if (firstVisPos != -1) {
                                    layoutManager.scrollToPositionWithOffset(firstVisPos, top - gridView.getPaddingTop());
                                }
                                AnimatorSet unused = EmojiView.this.searchAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (animation.equals(EmojiView.this.searchAnimation)) {
                                AnimatorSet unused = EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                }
            }
            a++;
            long j2 = scrollToSet;
            i = 2;
        }
        if (!z) {
            this.delegate.onSearchOpenClose(0);
        }
        showBottomTab(true, z);
    }

    /* access modifiers changed from: private */
    public void checkStickersSearchFieldScroll(boolean isLayout2) {
        RecyclerListView recyclerListView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z = false;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder holder = this.stickersGridView.findViewHolderForAdapterPosition(0);
            if (holder == null) {
                this.stickersSearchField.showShadow(true, !isLayout2);
                return;
            }
            SearchField searchField = this.stickersSearchField;
            if (holder.itemView.getTop() < this.stickersGridView.getPaddingTop()) {
                z = true;
            }
            searchField.showShadow(z, !isLayout2);
        } else if (this.stickersSearchField != null && (recyclerListView = this.stickersGridView) != null) {
            RecyclerView.ViewHolder holder2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (holder2 != null) {
                this.stickersSearchField.setTranslationY((float) holder2.itemView.getTop());
            } else {
                this.stickersSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            this.stickersSearchField.showShadow(false, !isLayout2);
        }
    }

    /* access modifiers changed from: private */
    public void checkBottomTabScroll(float dy) {
        int offset;
        this.lastBottomScrollDy += dy;
        if (this.pager.getCurrentItem() == 0) {
            offset = AndroidUtilities.dp(38.0f);
        } else {
            offset = AndroidUtilities.dp(48.0f);
        }
        float f = this.lastBottomScrollDy;
        if (f >= ((float) offset)) {
            showBottomTab(false, true);
        } else if (f <= ((float) (-offset))) {
            showBottomTab(true, true);
        } else if ((this.bottomTabContainer.getTag() == null && this.lastBottomScrollDy < 0.0f) || (this.bottomTabContainer.getTag() != null && this.lastBottomScrollDy > 0.0f)) {
            this.lastBottomScrollDy = 0.0f;
        }
    }

    /* access modifiers changed from: private */
    public void showBackspaceButton(final boolean show, boolean animated) {
        if (show && this.backspaceButton.getTag() == null) {
            return;
        }
        if (show || this.backspaceButton.getTag() == null) {
            AnimatorSet animatorSet = this.backspaceButtonAnimation;
            int i = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.backspaceButtonAnimation = null;
            }
            ImageView imageView = this.backspaceButton;
            if (!show) {
                i = 1;
            }
            imageView.setTag(i);
            int i2 = 0;
            float f = 1.0f;
            if (animated) {
                if (show) {
                    this.backspaceButton.setVisibility(0);
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.backspaceButtonAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[3];
                ImageView imageView2 = this.backspaceButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                ImageView imageView3 = this.backspaceButton;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = show ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView3, property2, fArr2);
                ImageView imageView4 = this.backspaceButton;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr3[0] = f;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView4, property3, fArr3);
                animatorSet2.playTogether(animatorArr);
                this.backspaceButtonAnimation.setDuration(200);
                this.backspaceButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.backspaceButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (!show) {
                            EmojiView.this.backspaceButton.setVisibility(4);
                        }
                    }
                });
                this.backspaceButtonAnimation.start();
                return;
            }
            this.backspaceButton.setAlpha(show ? 1.0f : 0.0f);
            this.backspaceButton.setScaleX(show ? 1.0f : 0.0f);
            ImageView imageView5 = this.backspaceButton;
            if (!show) {
                f = 0.0f;
            }
            imageView5.setScaleY(f);
            ImageView imageView6 = this.backspaceButton;
            if (!show) {
                i2 = 4;
            }
            imageView6.setVisibility(i2);
        }
    }

    /* access modifiers changed from: private */
    public void showStickerSettingsButton(final boolean show, boolean animated) {
        ImageView imageView = this.stickerSettingsButton;
        if (imageView != null) {
            if (show && imageView.getTag() == null) {
                return;
            }
            if (show || this.stickerSettingsButton.getTag() == null) {
                AnimatorSet animatorSet = this.stickersButtonAnimation;
                int i = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.stickersButtonAnimation = null;
                }
                ImageView imageView2 = this.stickerSettingsButton;
                if (!show) {
                    i = 1;
                }
                imageView2.setTag(i);
                int i2 = 0;
                float f = 1.0f;
                if (animated) {
                    if (show) {
                        this.stickerSettingsButton.setVisibility(0);
                    }
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.stickersButtonAnimation = animatorSet2;
                    Animator[] animatorArr = new Animator[3];
                    ImageView imageView3 = this.stickerSettingsButton;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = show ? 1.0f : 0.0f;
                    animatorArr[0] = ObjectAnimator.ofFloat(imageView3, property, fArr);
                    ImageView imageView4 = this.stickerSettingsButton;
                    Property property2 = View.SCALE_X;
                    float[] fArr2 = new float[1];
                    fArr2[0] = show ? 1.0f : 0.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(imageView4, property2, fArr2);
                    ImageView imageView5 = this.stickerSettingsButton;
                    Property property3 = View.SCALE_Y;
                    float[] fArr3 = new float[1];
                    if (!show) {
                        f = 0.0f;
                    }
                    fArr3[0] = f;
                    animatorArr[2] = ObjectAnimator.ofFloat(imageView5, property3, fArr3);
                    animatorSet2.playTogether(animatorArr);
                    this.stickersButtonAnimation.setDuration(200);
                    this.stickersButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.stickersButtonAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (!show) {
                                EmojiView.this.stickerSettingsButton.setVisibility(4);
                            }
                        }
                    });
                    this.stickersButtonAnimation.start();
                    return;
                }
                this.stickerSettingsButton.setAlpha(show ? 1.0f : 0.0f);
                this.stickerSettingsButton.setScaleX(show ? 1.0f : 0.0f);
                ImageView imageView6 = this.stickerSettingsButton;
                if (!show) {
                    f = 0.0f;
                }
                imageView6.setScaleY(f);
                ImageView imageView7 = this.stickerSettingsButton;
                if (!show) {
                    i2 = 4;
                }
                imageView7.setVisibility(i2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void showBottomTab(boolean show, boolean animated) {
        float f;
        float f2;
        float f3 = 0.0f;
        this.lastBottomScrollDy = 0.0f;
        if (show && this.bottomTabContainer.getTag() == null) {
            return;
        }
        if (show || this.bottomTabContainer.getTag() == null) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                AnimatorSet animatorSet = this.bottomTabContainerAnimation;
                int i = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.bottomTabContainerAnimation = null;
                }
                FrameLayout frameLayout = this.bottomTabContainer;
                if (!show) {
                    i = 1;
                }
                frameLayout.setTag(i);
                float f4 = 54.0f;
                if (animated) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.bottomTabContainerAnimation = animatorSet2;
                    Animator[] animatorArr = new Animator[2];
                    FrameLayout frameLayout2 = this.bottomTabContainer;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    if (show) {
                        f2 = 0.0f;
                    } else {
                        if (this.needEmojiSearch) {
                            f4 = 49.0f;
                        }
                        f2 = (float) AndroidUtilities.dp(f4);
                    }
                    fArr[0] = f2;
                    animatorArr[0] = ObjectAnimator.ofFloat(frameLayout2, property, fArr);
                    View view = this.shadowLine;
                    Property property2 = View.TRANSLATION_Y;
                    float[] fArr2 = new float[1];
                    if (!show) {
                        f3 = (float) AndroidUtilities.dp(49.0f);
                    }
                    fArr2[0] = f3;
                    animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
                    animatorSet2.playTogether(animatorArr);
                    this.bottomTabContainerAnimation.setDuration(200);
                    this.bottomTabContainerAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.bottomTabContainerAnimation.start();
                    return;
                }
                FrameLayout frameLayout3 = this.bottomTabContainer;
                if (show) {
                    f = 0.0f;
                } else {
                    if (this.needEmojiSearch) {
                        f4 = 49.0f;
                    }
                    f = (float) AndroidUtilities.dp(f4);
                }
                frameLayout3.setTranslationY(f);
                View view2 = this.shadowLine;
                if (!show) {
                    f3 = (float) AndroidUtilities.dp(49.0f);
                }
                view2.setTranslationY(f3);
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkTabsY(int type, int dy) {
        RecyclerView.ViewHolder holder;
        if (type == 1) {
            checkEmojiTabY(this.emojiGridView, dy);
            return;
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && !this.ignoreStickersScroll) {
            RecyclerListView listView = getListViewForType(type);
            if (dy <= 0 || listView == null || listView.getVisibility() != 0 || (holder = listView.findViewHolderForAdapterPosition(0)) == null || holder.itemView.getTop() + this.searchFieldHeight < listView.getPaddingTop()) {
                int[] iArr = this.tabsMinusDy;
                iArr[type] = iArr[type] - dy;
                if (iArr[type] > 0) {
                    iArr[type] = 0;
                } else if (iArr[type] < (-AndroidUtilities.dp(288.0f))) {
                    this.tabsMinusDy[type] = -AndroidUtilities.dp(288.0f);
                }
                if (type == 0) {
                    updateStickerTabsPosition();
                } else {
                    getTabsForType(type).setTranslationY((float) Math.max(-AndroidUtilities.dp(48.0f), this.tabsMinusDy[type]));
                }
            }
        }
    }

    private void resetTabsY(int type) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && type != 0) {
            ScrollSlidingTabStrip tabsForType = getTabsForType(type);
            this.tabsMinusDy[type] = 0;
            tabsForType.setTranslationY((float) 0);
        }
    }

    /* access modifiers changed from: private */
    public void animateTabsY(int type) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && type != 0) {
            float tabsHeight = AndroidUtilities.dpf2(type == 1 ? 38.0f : 48.0f);
            float fraction = ((float) this.tabsMinusDy[type]) / (-tabsHeight);
            if (fraction <= 0.0f || fraction >= 1.0f) {
                animateSearchField(type);
                return;
            }
            ScrollSlidingTabStrip tabStrip = getTabsForType(type);
            int endValue = fraction > 0.5f ? (int) (-Math.ceil((double) tabsHeight)) : 0;
            if (fraction > 0.5f) {
                animateSearchField(type, false, endValue);
            }
            if (type == 1) {
                checkEmojiShadow(endValue);
            }
            ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
            if (objectAnimatorArr[type] == null) {
                objectAnimatorArr[type] = ObjectAnimator.ofFloat(tabStrip, View.TRANSLATION_Y, new float[]{tabStrip.getTranslationY(), (float) endValue});
                this.tabsYAnimators[type].addUpdateListener(new EmojiView$$ExternalSyntheticLambda0(this, type));
                this.tabsYAnimators[type].setDuration(200);
            } else {
                objectAnimatorArr[type].setFloatValues(new float[]{tabStrip.getTranslationY(), (float) endValue});
            }
            this.tabsYAnimators[type].start();
        }
    }

    /* renamed from: lambda$animateTabsY$8$org-telegram-ui-Components-EmojiView  reason: not valid java name */
    public /* synthetic */ void m2241lambda$animateTabsY$8$orgtelegramuiComponentsEmojiView(int type, ValueAnimator a) {
        this.tabsMinusDy[type] = (int) ((Float) a.getAnimatedValue()).floatValue();
    }

    /* access modifiers changed from: private */
    public void stopAnimatingTabsY(int type) {
        ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
        if (objectAnimatorArr[type] != null && objectAnimatorArr[type].isRunning()) {
            this.tabsYAnimators[type].cancel();
        }
    }

    private void animateSearchField(int type) {
        RecyclerListView listView = getListViewForType(type);
        boolean z = true;
        int tabsHeight = AndroidUtilities.dp(type == 1 ? 38.0f : 48.0f);
        RecyclerView.ViewHolder holder = listView.findViewHolderForAdapterPosition(0);
        if (holder != null) {
            int bottom = holder.itemView.getBottom();
            int[] iArr = this.tabsMinusDy;
            float fraction = ((float) (bottom - (iArr[type] + tabsHeight))) / ((float) this.searchFieldHeight);
            if (fraction > 0.0f || fraction < 1.0f) {
                if (fraction <= 0.5f) {
                    z = false;
                }
                animateSearchField(type, z, iArr[type]);
            }
        }
    }

    private void animateSearchField(int type, boolean visible, final int tabsMinusDy2) {
        if (getListViewForType(type).findViewHolderForAdapterPosition(0) != null) {
            LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
                /* access modifiers changed from: protected */
                public int getVerticalSnapPreference() {
                    return -1;
                }

                /* access modifiers changed from: protected */
                public int calculateTimeForDeceleration(int dx) {
                    return super.calculateTimeForDeceleration(dx) * 16;
                }

                public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                    return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference) + tabsMinusDy2;
                }
            };
            smoothScroller.setTargetPosition(visible ^ true ? 1 : 0);
            getLayoutManagerForType(type).startSmoothScroll(smoothScroller);
        }
    }

    private ScrollSlidingTabStrip getTabsForType(int type) {
        switch (type) {
            case 0:
                return this.stickersTab;
            case 1:
                return this.emojiTabs;
            case 2:
                return this.gifTabs;
            default:
                throw new IllegalArgumentException("Unexpected argument: " + type);
        }
    }

    private RecyclerListView getListViewForType(int type) {
        switch (type) {
            case 0:
                return this.stickersGridView;
            case 1:
                return this.emojiGridView;
            case 2:
                return this.gifGridView;
            default:
                throw new IllegalArgumentException("Unexpected argument: " + type);
        }
    }

    private GridLayoutManager getLayoutManagerForType(int type) {
        switch (type) {
            case 0:
                return this.stickersLayoutManager;
            case 1:
                return this.emojiLayoutManager;
            case 2:
                return this.gifLayoutManager;
            default:
                throw new IllegalArgumentException("Unexpected argument: " + type);
        }
    }

    /* access modifiers changed from: private */
    public SearchField getSearchFieldForType(int type) {
        switch (type) {
            case 0:
                return this.stickersSearchField;
            case 1:
                return this.emojiSearchField;
            case 2:
                return this.gifSearchField;
            default:
                throw new IllegalArgumentException("Unexpected argument: " + type);
        }
    }

    /* access modifiers changed from: private */
    public void checkEmojiSearchFieldScroll(boolean isLayout2) {
        RecyclerListView recyclerListView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder holder = this.emojiGridView.findViewHolderForAdapterPosition(0);
            boolean z = true;
            if (holder == null) {
                this.emojiSearchField.showShadow(true, !isLayout2);
            } else {
                SearchField searchField = this.emojiSearchField;
                if (holder.itemView.getTop() >= this.emojiGridView.getPaddingTop()) {
                    z = false;
                }
                searchField.showShadow(z, !isLayout2);
            }
            showEmojiShadow(false, !isLayout2);
        } else if (this.emojiSearchField != null && (recyclerListView = this.emojiGridView) != null) {
            RecyclerView.ViewHolder holder2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (holder2 != null) {
                this.emojiSearchField.setTranslationY((float) holder2.itemView.getTop());
            } else {
                this.emojiSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            this.emojiSearchField.showShadow(false, !isLayout2);
            checkEmojiShadow(Math.round(this.emojiTabs.getTranslationY()));
        }
    }

    private void checkEmojiShadow(int tabsTranslationY) {
        ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
        if (objectAnimatorArr[1] == null || !objectAnimatorArr[1].isRunning()) {
            boolean z = false;
            RecyclerView.ViewHolder holder = this.emojiGridView.findViewHolderForAdapterPosition(0);
            int translatedBottom = AndroidUtilities.dp(38.0f) + tabsTranslationY;
            if (translatedBottom > 0 && (holder == null || holder.itemView.getBottom() < translatedBottom)) {
                z = true;
            }
            showEmojiShadow(z, true ^ this.isLayout);
        }
    }

    /* access modifiers changed from: private */
    public void checkEmojiTabY(View list, int dy) {
        RecyclerListView recyclerListView;
        RecyclerView.ViewHolder holder;
        if (list == null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.emojiTabs;
            this.tabsMinusDy[1] = 0;
            scrollSlidingTabStrip.setTranslationY((float) 0);
        } else if (list.getVisibility() == 0) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                if (dy > 0 && (recyclerListView = this.emojiGridView) != null && recyclerListView.getVisibility() == 0 && (holder = this.emojiGridView.findViewHolderForAdapterPosition(0)) != null) {
                    if (holder.itemView.getTop() + (this.needEmojiSearch ? this.searchFieldHeight : 0) >= this.emojiGridView.getPaddingTop()) {
                        return;
                    }
                }
                int[] iArr = this.tabsMinusDy;
                iArr[1] = iArr[1] - dy;
                if (iArr[1] > 0) {
                    iArr[1] = 0;
                } else if (iArr[1] < (-AndroidUtilities.dp(288.0f))) {
                    this.tabsMinusDy[1] = -AndroidUtilities.dp(288.0f);
                }
                this.emojiTabs.setTranslationY((float) Math.max(-AndroidUtilities.dp(38.0f), this.tabsMinusDy[1]));
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkGifSearchFieldScroll(boolean isLayout2) {
        RecyclerListView recyclerListView;
        int position;
        RecyclerListView recyclerListView2 = this.gifGridView;
        if (recyclerListView2 != null && (recyclerListView2.getAdapter() instanceof GifAdapter)) {
            GifAdapter adapter = (GifAdapter) this.gifGridView.getAdapter();
            if (!adapter.searchEndReached && adapter.reqId == 0 && !adapter.results.isEmpty() && (position = this.gifLayoutManager.findLastVisibleItemPosition()) != -1 && position > this.gifLayoutManager.getItemCount() - 5) {
                adapter.search(adapter.lastSearchImageString, adapter.nextSearchOffset, true, adapter.lastSearchIsEmoji, adapter.lastSearchIsEmoji);
            }
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z = false;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder holder = this.gifGridView.findViewHolderForAdapterPosition(0);
            if (holder == null) {
                this.gifSearchField.showShadow(true, !isLayout2);
                return;
            }
            SearchField searchField = this.gifSearchField;
            if (holder.itemView.getTop() < this.gifGridView.getPaddingTop()) {
                z = true;
            }
            searchField.showShadow(z, !isLayout2);
        } else if (this.gifSearchField != null && (recyclerListView = this.gifGridView) != null) {
            RecyclerView.ViewHolder holder2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (holder2 != null) {
                this.gifSearchField.setTranslationY((float) holder2.itemView.getTop());
            } else {
                this.gifSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            this.gifSearchField.showShadow(false, !isLayout2);
        }
    }

    /* access modifiers changed from: private */
    public void scrollGifsToTop() {
        GifLayoutManager gifLayoutManager2 = this.gifLayoutManager;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        gifLayoutManager2.scrollToPositionWithOffset((emojiViewDelegate == null || !emojiViewDelegate.isExpanded()) ? 1 : 0, 0);
        resetTabsY(2);
    }

    /* access modifiers changed from: private */
    public void checkScroll(int type) {
        GifAdapter gifAdapter2;
        int firstVisibleItem;
        int firstVisibleItem2;
        int firstTab;
        if (type == 0) {
            if (!this.ignoreStickersScroll && (firstVisibleItem2 = this.stickersLayoutManager.findFirstVisibleItemPosition()) != -1 && this.stickersGridView != null) {
                if (this.favTabBum > 0) {
                    firstTab = this.favTabBum;
                } else if (this.recentTabBum > 0) {
                    firstTab = this.recentTabBum;
                } else {
                    firstTab = this.stickersTabOffset;
                }
                this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(firstVisibleItem2), firstTab);
            }
        } else if (type == 2 && this.gifGridView.getAdapter() == (gifAdapter2 = this.gifAdapter) && gifAdapter2.trendingSectionItem >= 0 && this.gifTrendingTabNum >= 0 && this.gifRecentTabNum >= 0 && (firstVisibleItem = this.gifLayoutManager.findFirstVisibleItemPosition()) != -1) {
            this.gifTabs.onPageScrolled(firstVisibleItem >= this.gifAdapter.trendingSectionItem ? this.gifTrendingTabNum : this.gifRecentTabNum, 0);
        }
    }

    /* access modifiers changed from: private */
    public void saveNewPage() {
        int newPage;
        ViewPager viewPager = this.pager;
        if (viewPager != null) {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem == 2) {
                newPage = 1;
            } else if (currentItem == 1) {
                newPage = 2;
            } else {
                newPage = 0;
            }
            if (this.currentPage != newPage) {
                this.currentPage = newPage;
                MessagesController.getGlobalEmojiSettings().edit().putInt("selected_page", newPage).commit();
            }
        }
    }

    public void clearRecentEmoji() {
        Emoji.clearRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void onPageScrolled(int position, int width, int positionOffsetPixels) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null) {
            int i = 0;
            if (position == 1) {
                if (positionOffsetPixels != 0) {
                    i = 2;
                }
                emojiViewDelegate.onTabOpened(i);
            } else if (position == 2) {
                emojiViewDelegate.onTabOpened(3);
            } else {
                emojiViewDelegate.onTabOpened(0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void postBackspaceRunnable(int time) {
        AndroidUtilities.runOnUIThread(new EmojiView$$ExternalSyntheticLambda7(this, time), (long) time);
    }

    /* renamed from: lambda$postBackspaceRunnable$9$org-telegram-ui-Components-EmojiView  reason: not valid java name */
    public /* synthetic */ void m2250xb66e279a(int time) {
        if (this.backspacePressed) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.onBackspace()) {
                this.backspaceButton.performHapticFeedback(3);
            }
            this.backspaceOnce = true;
            postBackspaceRunnable(Math.max(50, time - 100));
        }
    }

    public void switchToGifRecent() {
        showBackspaceButton(false, false);
        showStickerSettingsButton(false, false);
        this.pager.setCurrentItem(1, false);
    }

    /* access modifiers changed from: private */
    public void updateEmojiTabs() {
        int newHas = Emoji.recentEmoji.isEmpty() ^ 1;
        int i = this.hasRecentEmoji;
        if (i == -1 || i != newHas) {
            this.hasRecentEmoji = (int) newHas;
            this.emojiTabs.removeTabs();
            String[] descriptions = {LocaleController.getString("RecentStickers", NUM), LocaleController.getString("Emoji1", NUM), LocaleController.getString("Emoji2", NUM), LocaleController.getString("Emoji3", NUM), LocaleController.getString("Emoji4", NUM), LocaleController.getString("Emoji5", NUM), LocaleController.getString("Emoji6", NUM), LocaleController.getString("Emoji7", NUM), LocaleController.getString("Emoji8", NUM)};
            for (int a = 0; a < this.emojiIcons.length; a++) {
                if (a != 0 || !Emoji.recentEmoji.isEmpty()) {
                    this.emojiTabs.addIconTab(a, this.emojiIcons[a]).setContentDescription(descriptions[a]);
                }
            }
            this.emojiTabs.updateTabStyles();
        }
    }

    /* access modifiers changed from: private */
    public void updateStickerTabs() {
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip != null && !scrollSlidingTabStrip.isDragging()) {
            this.recentTabBum = -2;
            this.favTabBum = -2;
            this.trendingTabNum = -2;
            this.hasChatStickers = false;
            this.stickersTabOffset = 0;
            int lastPosition = this.stickersTab.getCurrentPosition();
            this.stickersTab.beginUpdate((getParent() == null || getVisibility() != 0 || (this.installingStickerSets.size() == 0 && this.removingStickerSets.size() == 0)) ? false : true);
            MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
            SharedPreferences preferences = MessagesController.getEmojiSettings(this.currentAccount);
            this.featuredStickerSets.clear();
            ArrayList<TLRPC.StickerSetCovered> featured = mediaDataController.getFeaturedStickerSets();
            int N = featured.size();
            for (int a = 0; a < N; a++) {
                TLRPC.StickerSetCovered set = featured.get(a);
                if (!mediaDataController.isStickerPackInstalled(set.set.id)) {
                    this.featuredStickerSets.add(set);
                }
            }
            TrendingAdapter trendingAdapter2 = this.trendingAdapter;
            if (trendingAdapter2 != null) {
                trendingAdapter2.notifyDataSetChanged();
            }
            if (!featured.isEmpty() && (this.featuredStickerSets.isEmpty() || preferences.getLong("featured_hidden", 0) == featured.get(0).set.id)) {
                int id = mediaDataController.getUnreadStickerSets().isEmpty() ? 2 : 3;
                StickerTabView trendingStickersTabView = this.stickersTab.addStickerIconTab(id, this.stickerIcons[id]);
                trendingStickersTabView.textView.setText(LocaleController.getString("FeaturedStickersShort", NUM));
                trendingStickersTabView.setContentDescription(LocaleController.getString("FeaturedStickers", NUM));
                int i = this.stickersTabOffset;
                this.trendingTabNum = i;
                this.stickersTabOffset = i + 1;
            }
            if (!this.favouriteStickers.isEmpty()) {
                int i2 = this.stickersTabOffset;
                this.favTabBum = i2;
                this.stickersTabOffset = i2 + 1;
                StickerTabView stickerTabView = this.stickersTab.addStickerIconTab(1, this.stickerIcons[1]);
                stickerTabView.textView.setText(LocaleController.getString("FavoriteStickersShort", NUM));
                stickerTabView.setContentDescription(LocaleController.getString("FavoriteStickers", NUM));
            }
            if (!this.recentStickers.isEmpty()) {
                int i3 = this.stickersTabOffset;
                this.recentTabBum = i3;
                this.stickersTabOffset = i3 + 1;
                StickerTabView stickerTabView2 = this.stickersTab.addStickerIconTab(0, this.stickerIcons[0]);
                stickerTabView2.textView.setText(LocaleController.getString("RecentStickersShort", NUM));
                stickerTabView2.setContentDescription(LocaleController.getString("RecentStickers", NUM));
            }
            this.stickerSets.clear();
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = null;
            this.groupStickerSet = null;
            this.groupStickerPackPosition = -1;
            this.groupStickerPackNum = -10;
            ArrayList<TLRPC.TL_messages_stickerSet> packs = mediaDataController.getStickerSets(0);
            int i4 = 0;
            while (true) {
                TLRPC.StickerSetCovered[] stickerSetCoveredArr = this.primaryInstallingStickerSets;
                if (i4 >= stickerSetCoveredArr.length) {
                    break;
                }
                TLRPC.StickerSetCovered installingStickerSet = stickerSetCoveredArr[i4];
                if (installingStickerSet != null) {
                    TLRPC.TL_messages_stickerSet pack = mediaDataController.getStickerSetById(installingStickerSet.set.id);
                    if (pack == null || pack.set.archived) {
                        TLRPC.TL_messages_stickerSet set2 = new TLRPC.TL_messages_stickerSet();
                        set2.set = installingStickerSet.set;
                        if (installingStickerSet.cover != null) {
                            set2.documents.add(installingStickerSet.cover);
                        } else if (!installingStickerSet.covers.isEmpty()) {
                            set2.documents.addAll(installingStickerSet.covers);
                        }
                        if (!set2.documents.isEmpty()) {
                            this.stickerSets.add(set2);
                        }
                    } else {
                        this.primaryInstallingStickerSets[i4] = null;
                    }
                }
                i4++;
            }
            for (int a2 = 0; a2 < packs.size(); a2++) {
                TLRPC.TL_messages_stickerSet pack2 = packs.get(a2);
                if (!pack2.set.archived && pack2.documents != null && !pack2.documents.isEmpty()) {
                    this.stickerSets.add(pack2);
                }
            }
            if (this.info != null) {
                long hiddenStickerSetId = MessagesController.getEmojiSettings(this.currentAccount).getLong("group_hide_stickers_" + this.info.id, -1);
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.info.id));
                if (chat == null || this.info.stickerset == null || !ChatObject.hasAdminRights(chat)) {
                    this.groupStickersHidden = hiddenStickerSetId != -1;
                } else if (this.info.stickerset != null) {
                    this.groupStickersHidden = hiddenStickerSetId == this.info.stickerset.id;
                }
                if (this.info.stickerset != null) {
                    TLRPC.TL_messages_stickerSet pack3 = mediaDataController.getGroupStickerSetById(this.info.stickerset);
                    if (!(pack3 == null || pack3.documents == null || pack3.documents.isEmpty() || pack3.set == null)) {
                        TLRPC.TL_messages_stickerSet set3 = new TLRPC.TL_messages_stickerSet();
                        set3.documents = pack3.documents;
                        set3.packs = pack3.packs;
                        set3.set = pack3.set;
                        if (this.groupStickersHidden) {
                            this.groupStickerPackNum = this.stickerSets.size();
                            this.stickerSets.add(set3);
                        } else {
                            this.groupStickerPackNum = 0;
                            this.stickerSets.add(0, set3);
                        }
                        if (this.info.can_set_stickers) {
                            tL_messages_stickerSet = set3;
                        }
                        this.groupStickerSet = tL_messages_stickerSet;
                    }
                } else if (this.info.can_set_stickers) {
                    TLRPC.TL_messages_stickerSet pack4 = new TLRPC.TL_messages_stickerSet();
                    if (this.groupStickersHidden) {
                        this.groupStickerPackNum = this.stickerSets.size();
                        this.stickerSets.add(pack4);
                    } else {
                        this.groupStickerPackNum = 0;
                        this.stickerSets.add(0, pack4);
                    }
                }
            }
            int a3 = 0;
            while (a3 < this.stickerSets.size()) {
                if (a3 == this.groupStickerPackNum) {
                    TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.info.id));
                    if (chat2 == null) {
                        this.stickerSets.remove(0);
                        a3--;
                    } else {
                        this.hasChatStickers = true;
                        this.stickersTab.addStickerTab(chat2);
                    }
                } else {
                    TLRPC.TL_messages_stickerSet stickerSet = this.stickerSets.get(a3);
                    TLRPC.Document document = (TLRPC.Document) stickerSet.documents.get(0);
                    TLObject thumb = FileLoader.getClosestPhotoSizeWithSize(stickerSet.set.thumbs, 90);
                    if (thumb == null) {
                        thumb = document;
                    }
                    this.stickersTab.addStickerTab(thumb, document, stickerSet).setContentDescription(stickerSet.set.title + ", " + LocaleController.getString("AccDescrStickerSet", NUM));
                }
                a3++;
            }
            this.stickersTab.commitUpdate();
            this.stickersTab.updateTabStyles();
            if (lastPosition != 0) {
                this.stickersTab.onPageScrolled(lastPosition, lastPosition);
            }
            checkPanels();
        }
    }

    private void checkPanels() {
        int position;
        int firstTab;
        if (this.stickersTab != null && (position = this.stickersLayoutManager.findFirstVisibleItemPosition()) != -1) {
            if (this.favTabBum > 0) {
                firstTab = this.favTabBum;
            } else if (this.recentTabBum > 0) {
                firstTab = this.recentTabBum;
            } else {
                firstTab = this.stickersTabOffset;
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(position), firstTab);
        }
    }

    private void updateGifTabs() {
        int lastPosition = this.gifTabs.getCurrentPosition();
        int i = this.gifRecentTabNum;
        boolean wasRecentTabSelected = lastPosition == i;
        boolean hadRecent = i >= 0;
        boolean hasRecent = !this.recentGifs.isEmpty();
        this.gifTabs.beginUpdate(false);
        int gifTabsCount = 0;
        this.gifRecentTabNum = -2;
        this.gifTrendingTabNum = -2;
        this.gifFirstEmojiTabNum = -2;
        if (hasRecent) {
            this.gifRecentTabNum = 0;
            this.gifTabs.addIconTab(0, this.gifIcons[0]).setContentDescription(LocaleController.getString("RecentStickers", NUM));
            gifTabsCount = 0 + 1;
        }
        int gifTabsCount2 = gifTabsCount + 1;
        this.gifTrendingTabNum = gifTabsCount;
        this.gifTabs.addIconTab(1, this.gifIcons[1]).setContentDescription(LocaleController.getString("FeaturedGifs", NUM));
        this.gifFirstEmojiTabNum = gifTabsCount2;
        int dp = AndroidUtilities.dp(13.0f);
        int dp2 = AndroidUtilities.dp(11.0f);
        List<String> gifSearchEmojies = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
        int N = gifSearchEmojies.size();
        for (int i2 = 0; i2 < N; i2++) {
            String emoji = gifSearchEmojies.get(i2);
            Emoji.EmojiDrawable emojiDrawable = Emoji.getEmojiDrawable(emoji);
            if (emojiDrawable != null) {
                gifTabsCount2++;
                this.gifTabs.addEmojiTab(i2 + 3, emojiDrawable, MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(emoji)).setContentDescription(emoji);
            }
        }
        this.gifTabs.commitUpdate();
        this.gifTabs.updateTabStyles();
        if (wasRecentTabSelected && !hasRecent) {
            this.gifTabs.selectTab(this.gifTrendingTabNum);
        } else if (!ViewCompat.isLaidOut(this.gifTabs)) {
        } else {
            if (hasRecent && !hadRecent) {
                this.gifTabs.onPageScrolled(lastPosition + 1, 0);
            } else if (!hasRecent && hadRecent) {
                this.gifTabs.onPageScrolled(lastPosition - 1, 0);
            }
        }
    }

    public void addRecentSticker(TLRPC.Document document) {
        if (document != null) {
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, (Object) null, document, (int) (System.currentTimeMillis() / 1000), false);
            boolean wasEmpty = this.recentStickers.isEmpty();
            this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
            StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
            if (stickersGridAdapter2 != null) {
                stickersGridAdapter2.notifyDataSetChanged();
            }
            if (wasEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void addRecentGif(TLRPC.Document document) {
        if (document != null) {
            boolean wasEmpty = this.recentGifs.isEmpty();
            updateRecentGifs();
            if (wasEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void requestLayout() {
        if (!this.isLayout) {
            super.requestLayout();
        }
    }

    public void updateColors() {
        SearchField searchField;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            Drawable background = getBackground();
            if (background != null) {
                background.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackground"), PorterDuff.Mode.MULTIPLY));
            }
        } else {
            setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
            }
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.emojiTabs;
        if (scrollSlidingTabStrip != null) {
            scrollSlidingTabStrip.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
            this.emojiTabsShadow.setBackgroundColor(getThemedColor("chat_emojiPanelShadowLine"));
        }
        EmojiColorPickerView emojiColorPickerView = this.pickerView;
        if (emojiColorPickerView != null) {
            Theme.setDrawableColor(emojiColorPickerView.backgroundDrawable, getThemedColor("dialogBackground"));
            Theme.setDrawableColor(this.pickerView.arrowDrawable, getThemedColor("dialogBackground"));
        }
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                searchField = this.stickersSearchField;
            } else if (a == 1) {
                searchField = this.emojiSearchField;
            } else {
                searchField = this.gifSearchField;
            }
            if (searchField != null) {
                searchField.backgroundView.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
                searchField.shadowView.setBackgroundColor(getThemedColor("chat_emojiPanelShadowLine"));
                searchField.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
                searchField.searchIconImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
                Theme.setDrawableColorByKey(searchField.searchBackground.getBackground(), "chat_emojiSearchBackground");
                searchField.searchBackground.invalidate();
                searchField.searchEditText.setHintTextColor(getThemedColor("chat_emojiSearchIcon"));
                searchField.searchEditText.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
            }
        }
        Paint paint = this.dotPaint;
        if (paint != null) {
            paint.setColor(getThemedColor("chat_emojiPanelNewTrending"));
        }
        RecyclerListView recyclerListView = this.emojiGridView;
        if (recyclerListView != null) {
            recyclerListView.setGlowColor(getThemedColor("chat_emojiPanelBackground"));
        }
        RecyclerListView recyclerListView2 = this.stickersGridView;
        if (recyclerListView2 != null) {
            recyclerListView2.setGlowColor(getThemedColor("chat_emojiPanelBackground"));
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
        if (scrollSlidingTabStrip2 != null) {
            scrollSlidingTabStrip2.setIndicatorColor(getThemedColor("chat_emojiPanelStickerPackSelectorLine"));
            this.stickersTab.setUnderlineColor(getThemedColor("chat_emojiPanelShadowLine"));
            this.stickersTab.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip3 = this.gifTabs;
        if (scrollSlidingTabStrip3 != null) {
            scrollSlidingTabStrip3.setIndicatorColor(getThemedColor("chat_emojiPanelStickerPackSelectorLine"));
            this.gifTabs.setUnderlineColor(getThemedColor("chat_emojiPanelShadowLine"));
            this.gifTabs.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
        }
        ImageView imageView = this.backspaceButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
            if (this.emojiSearchField == null) {
                Theme.setSelectorDrawableColor(this.backspaceButton.getBackground(), getThemedColor("chat_emojiPanelBackground"), false);
                Theme.setSelectorDrawableColor(this.backspaceButton.getBackground(), getThemedColor("chat_emojiPanelBackground"), true);
            }
        }
        ImageView imageView2 = this.stickerSettingsButton;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView3 = this.searchButton;
        if (imageView3 != null) {
            imageView3.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
        }
        View view = this.shadowLine;
        if (view != null) {
            view.setBackgroundColor(getThemedColor("chat_emojiPanelShadowLine"));
        }
        TextView textView = this.mediaBanTooltip;
        if (textView != null) {
            ((ShapeDrawable) textView.getBackground()).getPaint().setColor(getThemedColor("chat_gifSaveHintBackground"));
            this.mediaBanTooltip.setTextColor(getThemedColor("chat_gifSaveHintText"));
        }
        GifAdapter gifAdapter2 = this.gifSearchAdapter;
        if (gifAdapter2 != null) {
            gifAdapter2.progressEmptyView.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
            this.gifSearchAdapter.progressEmptyView.textView.setTextColor(getThemedColor("chat_emojiPanelEmptyText"));
            this.gifSearchAdapter.progressEmptyView.progressView.setProgressColor(getThemedColor("progressCircle"));
        }
        int a2 = 0;
        while (true) {
            Drawable[] drawableArr = this.tabIcons;
            if (a2 >= drawableArr.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr[a2], getThemedColor("chat_emojiBottomPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.tabIcons[a2], getThemedColor("chat_emojiPanelIconSelected"), true);
            a2++;
        }
        int a3 = 0;
        while (true) {
            Drawable[] drawableArr2 = this.emojiIcons;
            if (a3 >= drawableArr2.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr2[a3], getThemedColor("chat_emojiPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.emojiIcons[a3], getThemedColor("chat_emojiPanelIconSelected"), true);
            a3++;
        }
        int a4 = 0;
        while (true) {
            Drawable[] drawableArr3 = this.stickerIcons;
            if (a4 >= drawableArr3.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr3[a4], getThemedColor("chat_emojiPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.stickerIcons[a4], getThemedColor("chat_emojiPanelIconSelected"), true);
            a4++;
        }
        int a5 = 0;
        while (true) {
            Drawable[] drawableArr4 = this.gifIcons;
            if (a5 >= drawableArr4.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr4[a5], getThemedColor("chat_emojiPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.gifIcons[a5], getThemedColor("chat_emojiPanelIconSelected"), true);
            a5++;
        }
        Drawable drawable = this.searchIconDrawable;
        if (drawable != null) {
            Theme.setEmojiDrawableColor(drawable, getThemedColor("chat_emojiBottomPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.searchIconDrawable, getThemedColor("chat_emojiPanelIconSelected"), true);
        }
        Drawable drawable2 = this.searchIconDotDrawable;
        if (drawable2 != null) {
            Theme.setEmojiDrawableColor(drawable2, getThemedColor("chat_emojiPanelStickerPackSelectorLine"), false);
            Theme.setEmojiDrawableColor(this.searchIconDotDrawable, getThemedColor("chat_emojiPanelStickerPackSelectorLine"), true);
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.isLayout = true;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            if (this.currentBackgroundType != 1) {
                if (Build.VERSION.SDK_INT >= 21) {
                    setOutlineProvider((ViewOutlineProvider) this.outlineProvider);
                    setClipToOutline(true);
                    setElevation((float) AndroidUtilities.dp(2.0f));
                }
                setBackgroundResource(NUM);
                getBackground().setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackground"), PorterDuff.Mode.MULTIPLY));
                if (this.needEmojiSearch) {
                    this.bottomTabContainerBackground.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
                }
                this.currentBackgroundType = 1;
            }
        } else if (this.currentBackgroundType != 0) {
            if (Build.VERSION.SDK_INT >= 21) {
                setOutlineProvider((ViewOutlineProvider) null);
                setClipToOutline(false);
                setElevation(0.0f);
            }
            setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
            }
            this.currentBackgroundType = 0;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), NUM));
        this.isLayout = false;
        setTranslationY(getTranslationY());
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.lastNotifyWidth != right - left) {
            this.lastNotifyWidth = right - left;
            reloadStickersAdapter();
        }
        View parent = (View) getParent();
        if (parent != null) {
            int newHeight = bottom - top;
            int newHeight2 = parent.getHeight();
            if (!(this.lastNotifyHeight == newHeight && this.lastNotifyHeight2 == newHeight2)) {
                EmojiViewDelegate emojiViewDelegate = this.delegate;
                if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                    this.bottomTabContainer.setTranslationY((float) AndroidUtilities.dp(49.0f));
                } else if (this.bottomTabContainer.getTag() == null) {
                    if (newHeight <= this.lastNotifyHeight) {
                        this.bottomTabContainer.setTranslationY(0.0f);
                    } else {
                        float y = (getY() + ((float) getMeasuredHeight())) - ((float) parent.getHeight());
                        if (((float) this.bottomTabContainer.getTop()) - y < 0.0f) {
                            y = (float) this.bottomTabContainer.getTop();
                        }
                        this.bottomTabContainer.setTranslationY(-y);
                    }
                }
                this.lastNotifyHeight = newHeight;
                this.lastNotifyHeight2 = newHeight2;
            }
        }
        super.onLayout(changed, left, top, right, bottom);
        updateStickerTabsPosition();
    }

    /* access modifiers changed from: private */
    public void reloadStickersAdapter() {
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        if (stickersGridAdapter2 != null) {
            stickersGridAdapter2.notifyDataSetChanged();
        }
        StickersSearchGridAdapter stickersSearchGridAdapter2 = this.stickersSearchGridAdapter;
        if (stickersSearchGridAdapter2 != null) {
            stickersSearchGridAdapter2.notifyDataSetChanged();
        }
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().close();
        }
        ContentPreviewViewer.getInstance().reset();
    }

    public void setDelegate(EmojiViewDelegate emojiViewDelegate) {
        this.delegate = emojiViewDelegate;
    }

    public void setDragListener(DragListener listener) {
        this.dragListener = listener;
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo) {
        this.info = chatInfo;
        updateStickerTabs();
    }

    public void invalidateViews() {
        this.emojiGridView.invalidateViews();
    }

    public void setForseMultiwindowLayout(boolean value) {
        this.forseMultiwindowLayout = value;
    }

    public void onOpen(boolean forceEmoji) {
        if (!(this.currentPage == 0 || this.currentChatId == 0)) {
            this.currentPage = 0;
        }
        if (this.currentPage == 0 || forceEmoji || this.views.size() == 1) {
            showBackspaceButton(true, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 0) {
                this.pager.setCurrentItem(0, !forceEmoji);
                return;
            }
            return;
        }
        int i = this.currentPage;
        if (i == 1) {
            showBackspaceButton(false, false);
            showStickerSettingsButton(true, false);
            if (this.pager.getCurrentItem() != 2) {
                this.pager.setCurrentItem(2, false);
            }
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            if (scrollSlidingTabStrip != null) {
                this.firstTabUpdate = true;
                int i2 = this.favTabBum;
                if (i2 >= 0) {
                    scrollSlidingTabStrip.selectTab(i2);
                } else {
                    int i3 = this.recentTabBum;
                    if (i3 >= 0) {
                        scrollSlidingTabStrip.selectTab(i3);
                    } else {
                        scrollSlidingTabStrip.selectTab(this.stickersTabOffset);
                    }
                }
                this.firstTabUpdate = false;
                this.stickersLayoutManager.scrollToPositionWithOffset(1, 0);
            }
        } else if (i == 2) {
            showBackspaceButton(false, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 1) {
                this.pager.setCurrentItem(1, false);
            }
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.gifTabs;
            if (scrollSlidingTabStrip2 != null) {
                scrollSlidingTabStrip2.selectTab(0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
            AndroidUtilities.runOnUIThread(new EmojiView$$ExternalSyntheticLambda6(this));
        }
    }

    /* renamed from: lambda$onAttachedToWindow$10$org-telegram-ui-Components-EmojiView  reason: not valid java name */
    public /* synthetic */ void m2249xvar_a476() {
        updateStickerTabs();
        reloadStickersAdapter();
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != 8) {
            Emoji.sortEmoji();
            this.emojiAdapter.notifyDataSetChanged();
            if (this.stickersGridAdapter != null) {
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
                updateStickerTabs();
                reloadStickersAdapter();
            }
            checkDocuments(true);
            checkDocuments(false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, false, true, false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(2, false, true, false);
        }
        ChooseStickerActionTracker chooseStickerActionTracker2 = this.chooseStickerActionTracker;
        if (chooseStickerActionTracker2 != null) {
            chooseStickerActionTracker2.checkVisibility();
        }
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentDocumentsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EmojiPopupWindow emojiPopupWindow = this.pickerViewPopup;
        if (emojiPopupWindow != null && emojiPopupWindow.isShowing()) {
            this.pickerViewPopup.dismiss();
        }
    }

    private void checkDocuments(boolean isGif) {
        if (isGif) {
            updateRecentGifs();
            return;
        }
        int previousCount = this.recentStickers.size();
        int previousCount2 = this.favouriteStickers.size();
        this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
        this.favouriteStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(2);
        for (int a = 0; a < this.favouriteStickers.size(); a++) {
            TLRPC.Document favSticker = this.favouriteStickers.get(a);
            int b = 0;
            while (true) {
                if (b >= this.recentStickers.size()) {
                    break;
                }
                TLRPC.Document recSticker = this.recentStickers.get(b);
                if (recSticker.dc_id == favSticker.dc_id && recSticker.id == favSticker.id) {
                    this.recentStickers.remove(b);
                    break;
                }
                b++;
            }
        }
        if (!(previousCount == this.recentStickers.size() && previousCount2 == this.favouriteStickers.size())) {
            updateStickerTabs();
        }
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        if (stickersGridAdapter2 != null) {
            stickersGridAdapter2.notifyDataSetChanged();
        }
        checkPanels();
    }

    /* access modifiers changed from: private */
    public void updateRecentGifs() {
        GifAdapter gifAdapter2;
        int prevSize = this.recentGifs.size();
        long prevHash = MediaDataController.calcDocumentsHash(this.recentGifs, Integer.MAX_VALUE);
        ArrayList<TLRPC.Document> recentGifs2 = MediaDataController.getInstance(this.currentAccount).getRecentGifs();
        this.recentGifs = recentGifs2;
        long newHash = MediaDataController.calcDocumentsHash(recentGifs2, Integer.MAX_VALUE);
        if ((this.gifTabs != null && prevSize == 0 && !this.recentGifs.isEmpty()) || (prevSize != 0 && this.recentGifs.isEmpty())) {
            updateGifTabs();
        }
        if ((prevSize != this.recentGifs.size() || prevHash != newHash) && (gifAdapter2 = this.gifAdapter) != null) {
            gifAdapter2.notifyDataSetChanged();
        }
    }

    public void setStickersBanned(boolean value, long chatId) {
        PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
        if (pagerSlidingTabStrip != null) {
            if (value) {
                this.currentChatId = chatId;
            } else {
                this.currentChatId = 0;
            }
            View view = pagerSlidingTabStrip.getTab(2);
            if (view != null) {
                view.setAlpha(this.currentChatId != 0 ? 0.5f : 1.0f);
                if (this.currentChatId != 0 && this.pager.getCurrentItem() != 0) {
                    showBackspaceButton(true, true);
                    showStickerSettingsButton(false, true);
                    this.pager.setCurrentItem(0, false);
                }
            }
        }
    }

    public void showStickerBanHint(boolean gif) {
        TLRPC.Chat chat;
        if (this.mediaBanTooltip.getVisibility() != 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.currentChatId))) != null) {
            if (ChatObject.hasAdminRights(chat) || chat.default_banned_rights == null || !chat.default_banned_rights.send_stickers) {
                if (chat.banned_rights != null) {
                    if (AndroidUtilities.isBannedForever(chat.banned_rights)) {
                        if (gif) {
                            this.mediaBanTooltip.setText(LocaleController.getString("AttachGifRestrictedForever", NUM));
                        } else {
                            this.mediaBanTooltip.setText(LocaleController.getString("AttachStickersRestrictedForever", NUM));
                        }
                    } else if (gif) {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachGifRestricted", NUM, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                    } else {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachStickersRestricted", NUM, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                    }
                } else {
                    return;
                }
            } else if (gif) {
                this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachGifRestricted", NUM));
            } else {
                this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachStickersRestricted", NUM));
            }
            this.mediaBanTooltip.setVisibility(0);
            AnimatorSet AnimatorSet = new AnimatorSet();
            AnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaBanTooltip, View.ALPHA, new float[]{0.0f, 1.0f})});
            AnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AndroidUtilities.runOnUIThread(new EmojiView$36$$ExternalSyntheticLambda0(this), 5000);
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-EmojiView$36  reason: not valid java name */
                public /* synthetic */ void m2251lambda$onAnimationEnd$0$orgtelegramuiComponentsEmojiView$36() {
                    if (EmojiView.this.mediaBanTooltip != null) {
                        AnimatorSet AnimatorSet1 = new AnimatorSet();
                        AnimatorSet1.playTogether(new Animator[]{ObjectAnimator.ofFloat(EmojiView.this.mediaBanTooltip, View.ALPHA, new float[]{0.0f})});
                        AnimatorSet1.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation1) {
                                if (EmojiView.this.mediaBanTooltip != null) {
                                    EmojiView.this.mediaBanTooltip.setVisibility(4);
                                }
                            }
                        });
                        AnimatorSet1.setDuration(300);
                        AnimatorSet1.start();
                    }
                }
            });
            AnimatorSet.setDuration(300);
            AnimatorSet.start();
        }
    }

    private void updateVisibleTrendingSets() {
        boolean forceInstalled;
        RecyclerListView recyclerListView = this.stickersGridView;
        if (recyclerListView != null) {
            try {
                int count = recyclerListView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.stickersGridView.getChildAt(a);
                    if (child instanceof FeaturedStickerSetInfoCell) {
                        if (((RecyclerListView.Holder) this.stickersGridView.getChildViewHolder(child)) != null) {
                            FeaturedStickerSetInfoCell cell = (FeaturedStickerSetInfoCell) child;
                            ArrayList<Long> unreadStickers = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
                            TLRPC.StickerSetCovered stickerSetCovered = cell.getStickerSet();
                            boolean unread = unreadStickers != null && unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id));
                            int i = 0;
                            while (true) {
                                TLRPC.StickerSetCovered[] stickerSetCoveredArr = this.primaryInstallingStickerSets;
                                if (i < stickerSetCoveredArr.length) {
                                    if (stickerSetCoveredArr[i] != null && stickerSetCoveredArr[i].set.id == stickerSetCovered.set.id) {
                                        forceInstalled = true;
                                        break;
                                    }
                                    i++;
                                } else {
                                    forceInstalled = false;
                                    break;
                                }
                            }
                            cell.setStickerSet(stickerSetCovered, unread, true, 0, 0, forceInstalled);
                            if (unread) {
                                MediaDataController.getInstance(this.currentAccount).markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                            }
                            boolean installing = this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                            boolean removing = this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                            if (installing || removing) {
                                if (installing && cell.isInstalled()) {
                                    this.installingStickerSets.remove(stickerSetCovered.set.id);
                                    installing = false;
                                } else if (removing && !cell.isInstalled()) {
                                    this.removingStickerSets.remove(stickerSetCovered.set.id);
                                }
                            }
                            cell.setAddDrawProgress(!forceInstalled && installing, true);
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public boolean areThereAnyStickers() {
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        return stickersGridAdapter2 != null && stickersGridAdapter2.getItemCount() > 0;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoad) {
            if (args[0].intValue() == 0) {
                updateStickerTabs();
                updateVisibleTrendingSets();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (id == NotificationCenter.recentDocumentsDidLoad) {
            boolean isGif = args[0].booleanValue();
            int type = args[1].intValue();
            if (isGif || type == 0 || type == 2) {
                checkDocuments(isGif);
            }
        } else if (id == NotificationCenter.featuredStickersDidLoad) {
            updateVisibleTrendingSets();
            PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
            if (pagerSlidingTabStrip != null) {
                int count = pagerSlidingTabStrip.getChildCount();
                for (int a = 0; a < count; a++) {
                    this.typeTabs.getChildAt(a).invalidate();
                }
            }
            updateStickerTabs();
        } else if (id == NotificationCenter.groupStickersDidLoad) {
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null && chatFull.stickerset != null && this.info.stickerset.id == args[0].longValue()) {
                updateStickerTabs();
            }
        } else if (id == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.stickersGridView;
            if (recyclerListView != null) {
                int count2 = recyclerListView.getChildCount();
                for (int a2 = 0; a2 < count2; a2++) {
                    View child = this.stickersGridView.getChildAt(a2);
                    if ((child instanceof StickerSetNameCell) || (child instanceof StickerEmojiCell)) {
                        child.invalidate();
                    }
                }
            }
            EmojiColorPickerView emojiColorPickerView = this.pickerView;
            if (emojiColorPickerView != null) {
                emojiColorPickerView.invalidate();
            }
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.gifTabs;
            if (scrollSlidingTabStrip != null) {
                scrollSlidingTabStrip.invalidateTabs();
            }
        } else if (id == NotificationCenter.newEmojiSuggestionsAvailable && this.emojiGridView != null && this.needEmojiSearch) {
            if ((this.emojiSearchField.progressDrawable.isAnimating() || this.emojiGridView.getAdapter() == this.emojiSearchAdapter) && !TextUtils.isEmpty(this.emojiSearchAdapter.lastSearchEmojiString)) {
                EmojiSearchAdapter emojiSearchAdapter2 = this.emojiSearchAdapter;
                emojiSearchAdapter2.search(emojiSearchAdapter2.lastSearchEmojiString);
            }
        }
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private class TrendingAdapter extends RecyclerListView.SelectionAdapter {
        private TrendingAdapter() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            BackupImageView imageView = new BackupImageView(EmojiView.this.getContext()) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (MediaDataController.getInstance(EmojiView.this.currentAccount).isStickerPackUnread(((TLRPC.StickerSetCovered) getTag()).set.id) && EmojiView.this.dotPaint != null) {
                        canvas.drawCircle((float) (canvas.getWidth() - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(14.0f), (float) AndroidUtilities.dp(3.0f), EmojiView.this.dotPaint);
                    }
                }
            };
            imageView.setSize(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
            imageView.setLayerNum(1);
            imageView.setAspectFit(true);
            imageView.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(52.0f), AndroidUtilities.dp(52.0f)));
            return new RecyclerListView.Holder(imageView);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.Document document;
            TLObject object;
            ImageLocation imageLocation;
            BackupImageView imageView = (BackupImageView) holder.itemView;
            TLRPC.StickerSetCovered set = (TLRPC.StickerSetCovered) EmojiView.this.featuredStickerSets.get(position);
            imageView.setTag(set);
            TLRPC.Document document2 = set.cover;
            if (!set.covers.isEmpty()) {
                document = set.covers.get(0);
            } else {
                document = document2;
            }
            TLObject object2 = FileLoader.getClosestPhotoSizeWithSize(set.set.thumbs, 90);
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(set.set.thumbs, "emptyListPlaceholder", 0.2f);
            if (svgThumb != null) {
                svgThumb.overrideWidthAndHeight(512, 512);
            }
            if (object2 == null) {
                object = document;
            } else {
                object = object2;
            }
            if (object instanceof TLRPC.Document) {
                imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), document);
            } else if (object instanceof TLRPC.PhotoSize) {
                imageLocation = ImageLocation.getForSticker((TLRPC.PhotoSize) object, document, set.set.thumb_version);
            } else {
                return;
            }
            if (imageLocation != null) {
                if (!(object instanceof TLRPC.Document) || !MessageObject.isAnimatedStickerDocument(document, true)) {
                    if (imageLocation.imageType == 1) {
                        imageView.setImage(imageLocation, "30_30", "tgs", (Drawable) svgThumb, (Object) set);
                    } else {
                        imageView.setImage(imageLocation, (String) null, "webp", (Drawable) svgThumb, (Object) set);
                    }
                } else if (svgThumb != null) {
                    imageView.setImage(ImageLocation.getForDocument(document), "30_30", (Drawable) svgThumb, 0, (Object) set);
                } else {
                    imageView.setImage(ImageLocation.getForDocument(document), "30_30", imageLocation, (String) null, 0, (Object) set);
                }
            }
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public int getItemCount() {
            return EmojiView.this.featuredStickerSets.size();
        }
    }

    private class StickersGridAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<Object> cacheParents = new SparseArray<>();
        private Context context;
        private HashMap<Object, Integer> packStartPosition = new HashMap<>();
        private SparseIntArray positionToRow = new SparseIntArray();
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        /* access modifiers changed from: private */
        public int stickersPerRow;
        /* access modifiers changed from: private */
        public int totalItems;

        public StickersGridAdapter(Context context2) {
            this.context = context2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.itemView instanceof RecyclerListView;
        }

        public int getItemCount() {
            int i = this.totalItems;
            if (i != 0) {
                return i + 1;
            }
            return 0;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public int getPositionForPack(Object pack) {
            Integer pos = this.packStartPosition.get(pack);
            if (pos == null) {
                return -1;
            }
            return pos.intValue();
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 4;
            }
            Object object = this.cache.get(position);
            if (object == null) {
                return 1;
            }
            if (object instanceof TLRPC.Document) {
                return 0;
            }
            if (!(object instanceof String)) {
                return 2;
            }
            if ("trend1".equals(object)) {
                return 5;
            }
            if ("trend2".equals(object)) {
                return 6;
            }
            return 3;
        }

        public int getTabForPosition(int position) {
            Object cacheObject = this.cache.get(position);
            if (!"search".equals(cacheObject) && !"trend1".equals(cacheObject) && !"trend2".equals(cacheObject)) {
                if (position == 0) {
                    position = 1;
                }
                if (this.stickersPerRow == 0) {
                    int width = EmojiView.this.getMeasuredWidth();
                    if (width == 0) {
                        width = AndroidUtilities.displaySize.x;
                    }
                    this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
                }
                int row = this.positionToRow.get(position, Integer.MIN_VALUE);
                if (row == Integer.MIN_VALUE) {
                    return (EmojiView.this.stickerSets.size() - 1) + EmojiView.this.stickersTabOffset;
                }
                Object pack = this.rowStartPack.get(row);
                if (!(pack instanceof String)) {
                    return EmojiView.this.stickersTabOffset + EmojiView.this.stickerSets.indexOf((TLRPC.TL_messages_stickerSet) pack);
                } else if ("recent".equals(pack)) {
                    return EmojiView.this.recentTabBum;
                } else {
                    return EmojiView.this.favTabBum;
                }
            } else if (EmojiView.this.favTabBum >= 0) {
                return EmojiView.this.favTabBum;
            } else {
                if (EmojiView.this.recentTabBum >= 0) {
                    return EmojiView.this.recentTabBum;
                }
                return 0;
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new StickerEmojiCell(this.context, true) {
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    view = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    ((StickerSetNameCell) view).setOnIconClickListener(new EmojiView$StickersGridAdapter$$ExternalSyntheticLambda0(this));
                    break;
                case 3:
                    view = new StickerSetGroupInfoCell(this.context);
                    ((StickerSetGroupInfoCell) view).setAddOnClickListener(new EmojiView$StickersGridAdapter$$ExternalSyntheticLambda1(this));
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    break;
                case 4:
                    view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
                case 5:
                    view = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    ((StickerSetNameCell) view).setOnIconClickListener(new EmojiView$StickersGridAdapter$$ExternalSyntheticLambda2(this));
                    break;
                case 6:
                    RecyclerListView horizontalListView = new RecyclerListView(this.context) {
                        public boolean onInterceptTouchEvent(MotionEvent e) {
                            if (!(getParent() == null || getParent().getParent() == null)) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1) || canScrollHorizontally(1));
                                EmojiView.this.pager.requestDisallowInterceptTouchEvent(true);
                            }
                            return super.onInterceptTouchEvent(e);
                        }
                    };
                    horizontalListView.setSelectorRadius(AndroidUtilities.dp(4.0f));
                    horizontalListView.setSelectorDrawableColor(Theme.getColor("listSelectorSDK21"));
                    horizontalListView.setTag(9);
                    horizontalListView.setItemAnimator((RecyclerView.ItemAnimator) null);
                    horizontalListView.setLayoutAnimation((LayoutAnimationController) null);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this.context) {
                        public boolean supportsPredictiveItemAnimations() {
                            return false;
                        }
                    };
                    layoutManager.setOrientation(0);
                    horizontalListView.setLayoutManager(layoutManager);
                    horizontalListView.setAdapter(EmojiView.this.trendingAdapter = new TrendingAdapter());
                    horizontalListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new EmojiView$StickersGridAdapter$$ExternalSyntheticLambda3(this));
                    view = horizontalListView;
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(52.0f)));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Components-EmojiView$StickersGridAdapter  reason: not valid java name */
        public /* synthetic */ void m2263xd2fvar_(View v) {
            if (EmojiView.this.groupStickerSet == null) {
                SharedPreferences.Editor edit = MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit();
                edit.putLong("group_hide_stickers_" + EmojiView.this.info.id, EmojiView.this.info.stickerset != null ? EmojiView.this.info.stickerset.id : 0).commit();
                EmojiView.this.updateStickerTabs();
                if (EmojiView.this.stickersGridAdapter != null) {
                    EmojiView.this.stickersGridAdapter.notifyDataSetChanged();
                }
            } else if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
            }
        }

        /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-Components-EmojiView$StickersGridAdapter  reason: not valid java name */
        public /* synthetic */ void m2264x5fec4d27(View v) {
            if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
            }
        }

        /* renamed from: lambda$onCreateViewHolder$2$org-telegram-ui-Components-EmojiView$StickersGridAdapter  reason: not valid java name */
        public /* synthetic */ void m2265xecd96446(View v) {
            ArrayList<TLRPC.StickerSetCovered> featured = MediaDataController.getInstance(EmojiView.this.currentAccount).getFeaturedStickerSets();
            if (!featured.isEmpty()) {
                MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit().putLong("featured_hidden", featured.get(0).set.id).commit();
                if (EmojiView.this.stickersGridAdapter != null) {
                    EmojiView.this.stickersGridAdapter.notifyItemRangeRemoved(1, 2);
                }
                EmojiView.this.updateStickerTabs();
            }
        }

        /* renamed from: lambda$onCreateViewHolder$3$org-telegram-ui-Components-EmojiView$StickersGridAdapter  reason: not valid java name */
        public /* synthetic */ void m2266x79CLASSNAMEb65(View view1, int position) {
            EmojiView.this.openTrendingStickers((TLRPC.StickerSetCovered) view1.getTag());
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArrayList<TLRPC.Document> documents;
            int icon = NUM;
            boolean z = false;
            int i = 1;
            switch (holder.getItemViewType()) {
                case 0:
                    TLRPC.Document sticker = (TLRPC.Document) this.cache.get(position);
                    StickerEmojiCell cell = (StickerEmojiCell) holder.itemView;
                    cell.setSticker(sticker, this.cacheParents.get(position), false);
                    cell.setRecent(EmojiView.this.recentStickers.contains(sticker));
                    return;
                case 1:
                    EmptyCell cell2 = (EmptyCell) holder.itemView;
                    if (position == this.totalItems) {
                        int row = this.positionToRow.get(position - 1, Integer.MIN_VALUE);
                        if (row == Integer.MIN_VALUE) {
                            cell2.setHeight(1);
                            return;
                        }
                        Object pack = this.rowStartPack.get(row);
                        if (pack instanceof TLRPC.TL_messages_stickerSet) {
                            documents = ((TLRPC.TL_messages_stickerSet) pack).documents;
                        } else if (!(pack instanceof String)) {
                            documents = null;
                        } else if ("recent".equals(pack)) {
                            documents = EmojiView.this.recentStickers;
                        } else {
                            documents = EmojiView.this.favouriteStickers;
                        }
                        if (documents == null) {
                            cell2.setHeight(1);
                            return;
                        } else if (documents.isEmpty()) {
                            cell2.setHeight(AndroidUtilities.dp(8.0f));
                            return;
                        } else {
                            int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil((double) (((float) documents.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                            if (height > 0) {
                                i = height;
                            }
                            cell2.setHeight(i);
                            return;
                        }
                    } else {
                        cell2.setHeight(AndroidUtilities.dp(82.0f));
                        return;
                    }
                case 2:
                    StickerSetNameCell cell3 = (StickerSetNameCell) holder.itemView;
                    if (position == EmojiView.this.groupStickerPackPosition) {
                        if (EmojiView.this.groupStickersHidden && EmojiView.this.groupStickerSet == null) {
                            icon = 0;
                        } else if (EmojiView.this.groupStickerSet != null) {
                            icon = NUM;
                        }
                        TLRPC.Chat chat = EmojiView.this.info != null ? MessagesController.getInstance(EmojiView.this.currentAccount).getChat(Long.valueOf(EmojiView.this.info.id)) : null;
                        Object[] objArr = new Object[1];
                        objArr[0] = chat != null ? chat.title : "Group Stickers";
                        cell3.setText(LocaleController.formatString("CurrentGroupStickers", NUM, objArr), icon);
                        return;
                    }
                    Object object = this.cache.get(position);
                    if (object instanceof TLRPC.TL_messages_stickerSet) {
                        TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) object;
                        if (set.set != null) {
                            cell3.setText(set.set.title, 0);
                            return;
                        }
                        return;
                    } else if (object == EmojiView.this.recentStickers) {
                        cell3.setText(LocaleController.getString("RecentStickers", NUM), 0);
                        return;
                    } else if (object == EmojiView.this.favouriteStickers) {
                        cell3.setText(LocaleController.getString("FavoriteStickers", NUM), 0);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    StickerSetGroupInfoCell cell4 = (StickerSetGroupInfoCell) holder.itemView;
                    if (position == this.totalItems - 1) {
                        z = true;
                    }
                    cell4.setIsLast(z);
                    return;
                case 5:
                    ((StickerSetNameCell) holder.itemView).setText(LocaleController.getString("FeaturedStickers", NUM), NUM);
                    return;
                default:
                    return;
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_stickerSet} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void updateItems() {
            /*
                r17 = this;
                r0 = r17
                org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                int r1 = r1.getMeasuredWidth()
                if (r1 != 0) goto L_0x000e
                android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
                int r1 = r2.x
            L_0x000e:
                r2 = 1116733440(0x42900000, float:72.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = r1 / r2
                r0.stickersPerRow = r2
                org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                androidx.recyclerview.widget.GridLayoutManager r2 = r2.stickersLayoutManager
                int r3 = r0.stickersPerRow
                r2.setSpanCount(r3)
                android.util.SparseArray<java.lang.Object> r2 = r0.rowStartPack
                r2.clear()
                java.util.HashMap<java.lang.Object, java.lang.Integer> r2 = r0.packStartPosition
                r2.clear()
                android.util.SparseIntArray r2 = r0.positionToRow
                r2.clear()
                android.util.SparseArray<java.lang.Object> r2 = r0.cache
                r2.clear()
                r2 = 0
                r0.totalItems = r2
                org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r3 = r3.stickerSets
                r4 = 0
                r5 = -4
            L_0x0042:
                int r6 = r3.size()
                if (r5 >= r6) goto L_0x01f6
                r6 = 0
                r7 = -4
                if (r5 != r7) goto L_0x005f
                android.util.SparseArray<java.lang.Object> r7 = r0.cache
                int r8 = r0.totalItems
                int r9 = r8 + 1
                r0.totalItems = r9
                java.lang.String r9 = "search"
                r7.put(r8, r9)
                int r4 = r4 + 1
                r16 = r1
                goto L_0x01ef
            L_0x005f:
                r7 = -3
                if (r5 != r7) goto L_0x00bc
                org.telegram.ui.Components.EmojiView r7 = org.telegram.ui.Components.EmojiView.this
                int r7 = r7.currentAccount
                org.telegram.messenger.MediaDataController r7 = org.telegram.messenger.MediaDataController.getInstance(r7)
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                int r8 = r8.currentAccount
                android.content.SharedPreferences r8 = org.telegram.messenger.MessagesController.getEmojiSettings(r8)
                java.util.ArrayList r9 = r7.getFeaturedStickerSets()
                org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r10 = r10.featuredStickerSets
                boolean r10 = r10.isEmpty()
                if (r10 != 0) goto L_0x0158
                r10 = 0
                java.lang.String r12 = "featured_hidden"
                long r10 = r8.getLong(r12, r10)
                java.lang.Object r12 = r9.get(r2)
                org.telegram.tgnet.TLRPC$StickerSetCovered r12 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r12
                org.telegram.tgnet.TLRPC$StickerSet r12 = r12.set
                long r12 = r12.id
                int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
                if (r14 == 0) goto L_0x0158
                android.util.SparseArray<java.lang.Object> r10 = r0.cache
                int r11 = r0.totalItems
                int r12 = r11 + 1
                r0.totalItems = r12
                java.lang.String r12 = "trend1"
                r10.put(r11, r12)
                android.util.SparseArray<java.lang.Object> r10 = r0.cache
                int r11 = r0.totalItems
                int r12 = r11 + 1
                r0.totalItems = r12
                java.lang.String r12 = "trend2"
                r10.put(r11, r12)
                int r4 = r4 + 2
                r16 = r1
                goto L_0x01ef
            L_0x00bc:
                r7 = -2
                java.lang.String r8 = "recent"
                java.lang.String r9 = "fav"
                r10 = -1
                if (r5 != r7) goto L_0x00d7
                org.telegram.ui.Components.EmojiView r7 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r7 = r7.favouriteStickers
                java.util.HashMap<java.lang.Object, java.lang.Integer> r11 = r0.packStartPosition
                r12 = r9
                int r13 = r0.totalItems
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                r11.put(r9, r13)
                goto L_0x0101
            L_0x00d7:
                if (r5 != r10) goto L_0x00ec
                org.telegram.ui.Components.EmojiView r7 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r7 = r7.recentStickers
                java.util.HashMap<java.lang.Object, java.lang.Integer> r11 = r0.packStartPosition
                r12 = r8
                int r13 = r0.totalItems
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                r11.put(r8, r13)
                goto L_0x0101
            L_0x00ec:
                r12 = 0
                java.lang.Object r7 = r3.get(r5)
                r6 = r7
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r6 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r6
                java.util.ArrayList r7 = r6.documents
                java.util.HashMap<java.lang.Object, java.lang.Integer> r11 = r0.packStartPosition
                int r13 = r0.totalItems
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                r11.put(r6, r13)
            L_0x0101:
                org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                int r11 = r11.groupStickerPackNum
                if (r5 != r11) goto L_0x0151
                org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                int r13 = r0.totalItems
                int unused = r11.groupStickerPackPosition = r13
                boolean r11 = r7.isEmpty()
                if (r11 == 0) goto L_0x0151
                android.util.SparseArray<java.lang.Object> r8 = r0.rowStartPack
                r8.put(r4, r6)
                android.util.SparseIntArray r8 = r0.positionToRow
                int r9 = r0.totalItems
                int r10 = r4 + 1
                r8.put(r9, r4)
                android.util.SparseArray<java.lang.Object> r4 = r0.rowStartPack
                r4.put(r10, r6)
                android.util.SparseIntArray r4 = r0.positionToRow
                int r8 = r0.totalItems
                int r8 = r8 + 1
                int r9 = r10 + 1
                r4.put(r8, r10)
                android.util.SparseArray<java.lang.Object> r4 = r0.cache
                int r8 = r0.totalItems
                int r10 = r8 + 1
                r0.totalItems = r10
                r4.put(r8, r6)
                android.util.SparseArray<java.lang.Object> r4 = r0.cache
                int r8 = r0.totalItems
                int r10 = r8 + 1
                r0.totalItems = r10
                java.lang.String r10 = "group"
                r4.put(r8, r10)
                r16 = r1
                r4 = r9
                goto L_0x01ef
            L_0x0151:
                boolean r11 = r7.isEmpty()
                if (r11 == 0) goto L_0x015c
            L_0x0158:
                r16 = r1
                goto L_0x01ef
            L_0x015c:
                int r11 = r7.size()
                float r11 = (float) r11
                int r13 = r0.stickersPerRow
                float r13 = (float) r13
                float r11 = r11 / r13
                double r13 = (double) r11
                double r13 = java.lang.Math.ceil(r13)
                int r11 = (int) r13
                if (r6 == 0) goto L_0x0175
                android.util.SparseArray<java.lang.Object> r13 = r0.cache
                int r14 = r0.totalItems
                r13.put(r14, r6)
                goto L_0x017c
            L_0x0175:
                android.util.SparseArray<java.lang.Object> r13 = r0.cache
                int r14 = r0.totalItems
                r13.put(r14, r7)
            L_0x017c:
                android.util.SparseIntArray r13 = r0.positionToRow
                int r14 = r0.totalItems
                r13.put(r14, r4)
                r13 = 0
            L_0x0184:
                int r14 = r7.size()
                if (r13 >= r14) goto L_0x01bf
                int r14 = r13 + 1
                int r15 = r0.totalItems
                int r14 = r14 + r15
                android.util.SparseArray<java.lang.Object> r15 = r0.cache
                java.lang.Object r2 = r7.get(r13)
                r15.put(r14, r2)
                if (r6 == 0) goto L_0x01a0
                android.util.SparseArray<java.lang.Object> r2 = r0.cacheParents
                r2.put(r14, r6)
                goto L_0x01a5
            L_0x01a0:
                android.util.SparseArray<java.lang.Object> r2 = r0.cacheParents
                r2.put(r14, r12)
            L_0x01a5:
                android.util.SparseIntArray r2 = r0.positionToRow
                int r15 = r13 + 1
                int r10 = r0.totalItems
                int r15 = r15 + r10
                int r10 = r4 + 1
                r16 = r1
                int r1 = r0.stickersPerRow
                int r1 = r13 / r1
                int r10 = r10 + r1
                r2.put(r15, r10)
                int r13 = r13 + 1
                r1 = r16
                r2 = 0
                r10 = -1
                goto L_0x0184
            L_0x01bf:
                r16 = r1
                r1 = 0
            L_0x01c2:
                int r2 = r11 + 1
                if (r1 >= r2) goto L_0x01e1
                if (r6 == 0) goto L_0x01d1
                android.util.SparseArray<java.lang.Object> r2 = r0.rowStartPack
                int r10 = r4 + r1
                r2.put(r10, r6)
                r13 = -1
                goto L_0x01de
            L_0x01d1:
                android.util.SparseArray<java.lang.Object> r2 = r0.rowStartPack
                int r10 = r4 + r1
                r13 = -1
                if (r5 != r13) goto L_0x01da
                r14 = r8
                goto L_0x01db
            L_0x01da:
                r14 = r9
            L_0x01db:
                r2.put(r10, r14)
            L_0x01de:
                int r1 = r1 + 1
                goto L_0x01c2
            L_0x01e1:
                int r1 = r0.totalItems
                int r2 = r0.stickersPerRow
                int r2 = r2 * r11
                int r2 = r2 + 1
                int r1 = r1 + r2
                r0.totalItems = r1
                int r1 = r11 + 1
                int r4 = r4 + r1
            L_0x01ef:
                int r5 = r5 + 1
                r1 = r16
                r2 = 0
                goto L_0x0042
            L_0x01f6:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersGridAdapter.updateItems():void");
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateItems();
            super.notifyItemRangeRemoved(positionStart, itemCount);
        }

        public void notifyDataSetChanged() {
            updateItems();
            super.notifyDataSetChanged();
        }
    }

    private class EmojiGridAdapter extends RecyclerListView.SelectionAdapter {
        private int itemCount;
        /* access modifiers changed from: private */
        public SparseIntArray positionToSection;
        /* access modifiers changed from: private */
        public SparseIntArray sectionToPosition;

        private EmojiGridAdapter() {
            this.positionToSection = new SparseIntArray();
            this.sectionToPosition = new SparseIntArray();
        }

        public int getItemCount() {
            return this.itemCount;
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    EmojiView emojiView = EmojiView.this;
                    view = new ImageViewEmoji(emojiView.getContext());
                    break;
                case 1:
                    view = new StickerSetNameCell(EmojiView.this.getContext(), true, EmojiView.this.resourcesProvider);
                    break;
                default:
                    view = new View(EmojiView.this.getContext());
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean recent;
            String code;
            String coloredCode;
            switch (holder.getItemViewType()) {
                case 0:
                    ImageViewEmoji imageView = (ImageViewEmoji) holder.itemView;
                    if (EmojiView.this.needEmojiSearch) {
                        position--;
                    }
                    int count = Emoji.recentEmoji.size();
                    if (position < count) {
                        coloredCode = Emoji.recentEmoji.get(position);
                        code = coloredCode;
                        recent = true;
                    } else {
                        int a = 0;
                        while (true) {
                            if (a < EmojiData.dataColored.length) {
                                int size = EmojiData.dataColored[a].length + 1;
                                if (position < count + size) {
                                    String str = EmojiData.dataColored[a][(position - count) - 1];
                                    String code2 = str;
                                    String coloredCode2 = str;
                                    String color = Emoji.emojiColor.get(code2);
                                    if (color != null) {
                                        String access$3700 = EmojiView.addColorToCode(coloredCode2, color);
                                        code = code2;
                                        coloredCode = access$3700;
                                    } else {
                                        String str2 = coloredCode2;
                                        code = code2;
                                        coloredCode = str2;
                                    }
                                } else {
                                    count += size;
                                    a++;
                                }
                            } else {
                                code = null;
                                coloredCode = null;
                            }
                        }
                        recent = false;
                    }
                    imageView.setImageDrawable(Emoji.getEmojiBigDrawable(coloredCode), recent);
                    imageView.setTag(code);
                    imageView.setContentDescription(coloredCode);
                    return;
                case 1:
                    ((StickerSetNameCell) holder.itemView).setText(EmojiView.this.emojiTitles[this.positionToSection.get(position)], 0);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (EmojiView.this.needEmojiSearch && position == 0) {
                return 2;
            }
            if (this.positionToSection.indexOfKey(position) >= 0) {
                return 1;
            }
            return 0;
        }

        public void notifyDataSetChanged() {
            this.positionToSection.clear();
            this.itemCount = Emoji.recentEmoji.size() + (EmojiView.this.needEmojiSearch ? 1 : 0);
            for (int a = 0; a < EmojiData.dataColored.length; a++) {
                this.positionToSection.put(this.itemCount, a);
                this.sectionToPosition.put(a, this.itemCount);
                this.itemCount += EmojiData.dataColored[a].length + 1;
            }
            EmojiView.this.updateEmojiTabs();
            super.notifyDataSetChanged();
        }
    }

    private class EmojiSearchAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public String lastSearchAlias;
        /* access modifiers changed from: private */
        public String lastSearchEmojiString;
        /* access modifiers changed from: private */
        public ArrayList<MediaDataController.KeywordResult> result;
        private Runnable searchRunnable;
        /* access modifiers changed from: private */
        public boolean searchWas;

        private EmojiSearchAdapter() {
            this.result = new ArrayList<>();
        }

        public int getItemCount() {
            if (this.result.isEmpty() && !this.searchWas) {
                return Emoji.recentEmoji.size() + 1;
            }
            if (!this.result.isEmpty()) {
                return this.result.size() + 1;
            }
            return 2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    EmojiView emojiView = EmojiView.this;
                    view = new ImageViewEmoji(emojiView.getContext());
                    break;
                case 1:
                    View view2 = new View(EmojiView.this.getContext());
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    view = view2;
                    break;
                default:
                    AnonymousClass1 view3 = new FrameLayout(EmojiView.this.getContext()) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            int parentHeight;
                            View parent = (View) EmojiView.this.getParent();
                            if (parent != null) {
                                parentHeight = (int) (((float) parent.getMeasuredHeight()) - EmojiView.this.getY());
                            } else {
                                parentHeight = AndroidUtilities.dp(120.0f);
                            }
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(parentHeight - EmojiView.this.searchFieldHeight, NUM));
                        }
                    };
                    TextView textView = new TextView(EmojiView.this.getContext());
                    textView.setText(LocaleController.getString("NoEmojiFound", NUM));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"));
                    view3.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
                    ImageView imageView = new ImageView(EmojiView.this.getContext());
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(NUM);
                    imageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
                    view3.addView(imageView, LayoutHelper.createFrame(48, 48, 85));
                    imageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            final boolean[] loadingUrl = new boolean[1];
                            final BottomSheet.Builder builder = new BottomSheet.Builder(EmojiView.this.getContext());
                            LinearLayout linearLayout = new LinearLayout(EmojiView.this.getContext());
                            linearLayout.setOrientation(1);
                            linearLayout.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                            ImageView imageView1 = new ImageView(EmojiView.this.getContext());
                            imageView1.setImageResource(NUM);
                            linearLayout.addView(imageView1, LayoutHelper.createLinear(-2, -2, 49, 0, 15, 0, 0));
                            TextView textView = new TextView(EmojiView.this.getContext());
                            textView.setText(LocaleController.getString("EmojiSuggestions", NUM));
                            textView.setTextSize(1, 15.0f);
                            textView.setTextColor(EmojiView.this.getThemedColor("dialogTextBlue2"));
                            int i = 5;
                            textView.setGravity(LocaleController.isRTL ? 5 : 3);
                            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                            linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 51, 0, 24, 0, 0));
                            TextView textView2 = new TextView(EmojiView.this.getContext());
                            textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString("EmojiSuggestionsInfo", NUM)));
                            textView2.setTextSize(1, 15.0f);
                            textView2.setTextColor(EmojiView.this.getThemedColor("dialogTextBlack"));
                            textView2.setGravity(LocaleController.isRTL ? 5 : 3);
                            linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 51, 0, 11, 0, 0));
                            TextView textView3 = new TextView(EmojiView.this.getContext());
                            Object[] objArr = new Object[1];
                            objArr[0] = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage;
                            textView3.setText(LocaleController.formatString("EmojiSuggestionsUrl", NUM, objArr));
                            textView3.setTextSize(1, 15.0f);
                            textView3.setTextColor(EmojiView.this.getThemedColor("dialogTextLink"));
                            if (!LocaleController.isRTL) {
                                i = 3;
                            }
                            textView3.setGravity(i);
                            linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 51, 0, 18, 0, 16));
                            textView3.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    boolean[] zArr = loadingUrl;
                                    if (!zArr[0]) {
                                        zArr[0] = true;
                                        AlertDialog[] progressDialog = {new AlertDialog(EmojiView.this.getContext(), 3)};
                                        TLRPC.TL_messages_getEmojiURL req = new TLRPC.TL_messages_getEmojiURL();
                                        req.lang_code = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage[0];
                                        AndroidUtilities.runOnUIThread(new EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda1(this, progressDialog, ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, new EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda3(this, progressDialog, builder))), 1000);
                                    }
                                }

                                /* renamed from: lambda$onClick$1$org-telegram-ui-Components-EmojiView$EmojiSearchAdapter$2$1  reason: not valid java name */
                                public /* synthetic */ void m2253x6d790acb(AlertDialog[] progressDialog, BottomSheet.Builder builder, TLObject response, TLRPC.TL_error error) {
                                    AndroidUtilities.runOnUIThread(new EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda2(this, progressDialog, response, builder));
                                }

                                /* renamed from: lambda$onClick$0$org-telegram-ui-Components-EmojiView$EmojiSearchAdapter$2$1  reason: not valid java name */
                                public /* synthetic */ void m2252xb3036a4a(AlertDialog[] progressDialog, TLObject response, BottomSheet.Builder builder) {
                                    try {
                                        progressDialog[0].dismiss();
                                    } catch (Throwable th) {
                                    }
                                    progressDialog[0] = null;
                                    if (response instanceof TLRPC.TL_emojiURL) {
                                        Browser.openUrl(EmojiView.this.getContext(), ((TLRPC.TL_emojiURL) response).url);
                                        builder.getDismissRunnable().run();
                                    }
                                }

                                /* renamed from: lambda$onClick$3$org-telegram-ui-Components-EmojiView$EmojiSearchAdapter$2$1  reason: not valid java name */
                                public /* synthetic */ void m2255xe2644bcd(AlertDialog[] progressDialog, int requestId) {
                                    if (progressDialog[0] != null) {
                                        progressDialog[0].setOnCancelListener(new EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda0(this, requestId));
                                        progressDialog[0].show();
                                    }
                                }

                                /* renamed from: lambda$onClick$2$org-telegram-ui-Components-EmojiView$EmojiSearchAdapter$2$1  reason: not valid java name */
                                public /* synthetic */ void m2254x27eeab4c(int requestId, DialogInterface dialog) {
                                    ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(requestId, true);
                                }
                            });
                            builder.setCustomView(linearLayout);
                            builder.show();
                        }
                    });
                    view3.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    view = view3;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean recent;
            String code;
            String coloredCode;
            switch (holder.getItemViewType()) {
                case 0:
                    ImageViewEmoji imageView = (ImageViewEmoji) holder.itemView;
                    int position2 = position - 1;
                    if (!this.result.isEmpty() || this.searchWas) {
                        coloredCode = this.result.get(position2).emoji;
                        code = coloredCode;
                        recent = false;
                    } else {
                        coloredCode = Emoji.recentEmoji.get(position2);
                        code = coloredCode;
                        recent = true;
                    }
                    imageView.setImageDrawable(Emoji.getEmojiBigDrawable(coloredCode), recent);
                    imageView.setTag(code);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            if (position != 1 || !this.searchWas || !this.result.isEmpty()) {
                return 0;
            }
            return 2;
        }

        public void search(String text) {
            if (TextUtils.isEmpty(text)) {
                this.lastSearchEmojiString = null;
                if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiAdapter) {
                    EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiAdapter);
                    this.searchWas = false;
                }
                notifyDataSetChanged();
            } else {
                this.lastSearchEmojiString = text.toLowerCase();
            }
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            if (!TextUtils.isEmpty(this.lastSearchEmojiString)) {
                AnonymousClass3 r0 = new Runnable() {
                    public void run() {
                        EmojiView.this.emojiSearchField.progressDrawable.startAnimation();
                        final String query = EmojiSearchAdapter.this.lastSearchEmojiString;
                        String[] newLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        if (!Arrays.equals(EmojiView.this.lastSearchKeyboardLanguage, newLanguage)) {
                            MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(newLanguage);
                        }
                        String[] unused = EmojiView.this.lastSearchKeyboardLanguage = newLanguage;
                        MediaDataController.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, EmojiSearchAdapter.this.lastSearchEmojiString, false, new MediaDataController.KeywordResultCallback() {
                            public void run(ArrayList<MediaDataController.KeywordResult> param, String alias) {
                                if (query.equals(EmojiSearchAdapter.this.lastSearchEmojiString)) {
                                    String unused = EmojiSearchAdapter.this.lastSearchAlias = alias;
                                    EmojiView.this.emojiSearchField.progressDrawable.stopAnimation();
                                    boolean unused2 = EmojiSearchAdapter.this.searchWas = true;
                                    if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                                        EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiSearchAdapter);
                                    }
                                    ArrayList unused3 = EmojiSearchAdapter.this.result = param;
                                    EmojiSearchAdapter.this.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                };
                this.searchRunnable = r0;
                AndroidUtilities.runOnUIThread(r0, 300);
            }
        }
    }

    private class EmojiPagesAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
        private EmojiPagesAdapter() {
        }

        public void destroyItem(ViewGroup viewGroup, int position, Object object) {
            viewGroup.removeView((View) EmojiView.this.views.get(position));
        }

        public boolean canScrollToTab(int position) {
            boolean z = true;
            if ((position != 1 && position != 2) || EmojiView.this.currentChatId == 0) {
                return true;
            }
            EmojiView emojiView = EmojiView.this;
            if (position != 1) {
                z = false;
            }
            emojiView.showStickerBanHint(z);
            return false;
        }

        public int getCount() {
            return EmojiView.this.views.size();
        }

        public Drawable getPageIconDrawable(int position) {
            return EmojiView.this.tabIcons[position];
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return LocaleController.getString("Emoji", NUM);
                case 1:
                    return LocaleController.getString("AccDescrGIFs", NUM);
                case 2:
                    return LocaleController.getString("AccDescrStickers", NUM);
                default:
                    return null;
            }
        }

        public void customOnDraw(Canvas canvas, int position) {
            if (position == 2 && !MediaDataController.getInstance(EmojiView.this.currentAccount).getUnreadStickerSets().isEmpty() && EmojiView.this.dotPaint != null) {
                canvas.drawCircle((float) ((canvas.getWidth() / 2) + AndroidUtilities.dp(9.0f)), (float) ((canvas.getHeight() / 2) - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(5.0f), EmojiView.this.dotPaint);
            }
        }

        public Object instantiateItem(ViewGroup viewGroup, int position) {
            View view = (View) EmojiView.this.views.get(position);
            viewGroup.addView(view);
            return view;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private class GifAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public TLRPC.User bot;
        private final Context context;
        private int firstResultItem;
        private int itemsCount;
        /* access modifiers changed from: private */
        public String lastSearchImageString;
        /* access modifiers changed from: private */
        public boolean lastSearchIsEmoji;
        private final int maxRecentRowsCount;
        /* access modifiers changed from: private */
        public String nextSearchOffset;
        /* access modifiers changed from: private */
        public final GifProgressEmptyView progressEmptyView;
        /* access modifiers changed from: private */
        public int recentItemsCount;
        /* access modifiers changed from: private */
        public int reqId;
        /* access modifiers changed from: private */
        public ArrayList<TLRPC.BotInlineResult> results;
        private HashMap<String, TLRPC.BotInlineResult> resultsMap;
        /* access modifiers changed from: private */
        public boolean searchEndReached;
        private Runnable searchRunnable;
        private boolean searchingUser;
        /* access modifiers changed from: private */
        public boolean showTrendingWhenSearchEmpty;
        /* access modifiers changed from: private */
        public int trendingSectionItem;
        private final boolean withRecent;

        public GifAdapter(EmojiView emojiView, Context context2) {
            this(context2, false, 0);
        }

        /* JADX INFO: this call moved to the top of the method (can break code semantics) */
        public GifAdapter(EmojiView emojiView, Context context2, boolean withRecent2) {
            this(context2, withRecent2, withRecent2 ? Integer.MAX_VALUE : 0);
        }

        public GifAdapter(Context context2, boolean withRecent2, int maxRecentRowsCount2) {
            this.results = new ArrayList<>();
            this.resultsMap = new HashMap<>();
            this.trendingSectionItem = -1;
            this.firstResultItem = -1;
            this.context = context2;
            this.withRecent = withRecent2;
            this.maxRecentRowsCount = maxRecentRowsCount2;
            this.progressEmptyView = withRecent2 ? null : new GifProgressEmptyView(context2);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemCount() {
            return this.itemsCount;
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            boolean z = this.withRecent;
            if (z && position == this.trendingSectionItem) {
                return 2;
            }
            if (z || !this.results.isEmpty()) {
                return 0;
            }
            return 3;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    ContextLinkCell cell = new ContextLinkCell(this.context);
                    cell.setCanPreviewGif(true);
                    view = cell;
                    break;
                case 1:
                    view = new View(EmojiView.this.getContext());
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
                case 2:
                    StickerSetNameCell cell1 = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    cell1.setText(LocaleController.getString("FeaturedGifs", NUM), 0);
                    view = cell1;
                    RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, -2);
                    lp.topMargin = AndroidUtilities.dp(2.5f);
                    lp.bottomMargin = AndroidUtilities.dp(5.5f);
                    view.setLayoutParams(lp);
                    break;
                default:
                    view = this.progressEmptyView;
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    ContextLinkCell cell = (ContextLinkCell) holder.itemView;
                    int i = this.firstResultItem;
                    if (i < 0 || position < i) {
                        cell.setGif((TLRPC.Document) EmojiView.this.recentGifs.get(position - 1), false);
                        return;
                    }
                    cell.setLink(this.results.get(position - i), this.bot, true, false, false, true);
                    return;
                default:
                    return;
            }
        }

        public void notifyDataSetChanged() {
            updateRecentItemsCount();
            updateItems();
            super.notifyDataSetChanged();
        }

        private void updateItems() {
            this.trendingSectionItem = -1;
            this.firstResultItem = -1;
            this.itemsCount = 1;
            if (this.withRecent) {
                this.itemsCount = this.recentItemsCount + 1;
            }
            if (!this.results.isEmpty()) {
                if (this.withRecent && this.recentItemsCount > 0) {
                    int i = this.itemsCount;
                    this.itemsCount = i + 1;
                    this.trendingSectionItem = i;
                }
                int i2 = this.itemsCount;
                this.firstResultItem = i2;
                this.itemsCount = i2 + this.results.size();
            } else if (!this.withRecent) {
                this.itemsCount++;
            }
        }

        private void updateRecentItemsCount() {
            int i;
            if (this.withRecent && (i = this.maxRecentRowsCount) != 0) {
                if (i == Integer.MAX_VALUE) {
                    this.recentItemsCount = EmojiView.this.recentGifs.size();
                } else if (EmojiView.this.gifGridView.getMeasuredWidth() != 0) {
                    int listWidth = EmojiView.this.gifGridView.getMeasuredWidth();
                    int spanCount = EmojiView.this.gifLayoutManager.getSpanCount();
                    int preferredRowSize = AndroidUtilities.dp(100.0f);
                    int rowCount = 0;
                    int spanLeft = spanCount;
                    int currentItemsInRow = 0;
                    this.recentItemsCount = 0;
                    int N = EmojiView.this.recentGifs.size();
                    for (int i2 = 0; i2 < N; i2++) {
                        Size size = EmojiView.this.gifLayoutManager.fixSize(EmojiView.this.gifLayoutManager.getSizeForItem((TLRPC.Document) EmojiView.this.recentGifs.get(i2)));
                        int requiredSpan = Math.min(spanCount, (int) Math.floor((double) (((float) spanCount) * (((size.width / size.height) * ((float) preferredRowSize)) / ((float) listWidth)))));
                        if (spanLeft < requiredSpan) {
                            this.recentItemsCount += currentItemsInRow;
                            rowCount++;
                            if (rowCount == this.maxRecentRowsCount) {
                                break;
                            }
                            currentItemsInRow = 0;
                            spanLeft = spanCount;
                        }
                        currentItemsInRow++;
                        spanLeft -= requiredSpan;
                    }
                    if (rowCount < this.maxRecentRowsCount) {
                        this.recentItemsCount += currentItemsInRow;
                    }
                }
            }
        }

        public void loadTrendingGifs() {
            search("", "", true, true, true);
        }

        private void searchBotUser() {
            if (!this.searchingUser) {
                this.searchingUser = true;
                TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                req.username = MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot;
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, new EmojiView$GifAdapter$$ExternalSyntheticLambda2(this));
            }
        }

        /* renamed from: lambda$searchBotUser$1$org-telegram-ui-Components-EmojiView$GifAdapter  reason: not valid java name */
        public /* synthetic */ void m2259xd8a53b(TLObject response, TLRPC.TL_error error) {
            if (response != null) {
                AndroidUtilities.runOnUIThread(new EmojiView$GifAdapter$$ExternalSyntheticLambda1(this, response));
            }
        }

        /* renamed from: lambda$searchBotUser$0$org-telegram-ui-Components-EmojiView$GifAdapter  reason: not valid java name */
        public /* synthetic */ void m2258x14f0b3a(TLObject response) {
            TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
            MessagesController.getInstance(EmojiView.this.currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(EmojiView.this.currentAccount).putChats(res.chats, false);
            MessagesStorage.getInstance(EmojiView.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
            String str = this.lastSearchImageString;
            this.lastSearchImageString = null;
            search(str, "", false);
        }

        public void search(final String text) {
            if (!this.withRecent) {
                int i = this.reqId;
                if (i != 0) {
                    if (i >= 0) {
                        ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                    }
                    this.reqId = 0;
                }
                this.lastSearchIsEmoji = false;
                GifProgressEmptyView gifProgressEmptyView = this.progressEmptyView;
                if (gifProgressEmptyView != null) {
                    gifProgressEmptyView.setLoadingState(false);
                }
                Runnable runnable = this.searchRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                if (TextUtils.isEmpty(text)) {
                    this.lastSearchImageString = null;
                    if (this.showTrendingWhenSearchEmpty) {
                        loadTrendingGifs();
                        return;
                    }
                    int page = EmojiView.this.gifTabs.getCurrentPosition();
                    if (page != EmojiView.this.gifRecentTabNum && page != EmojiView.this.gifTrendingTabNum) {
                        searchEmoji(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchEmojies.get(page - EmojiView.this.gifFirstEmojiTabNum));
                    } else if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifAdapter) {
                        EmojiView.this.gifGridView.setAdapter(EmojiView.this.gifAdapter);
                    }
                } else {
                    String lowerCase = text.toLowerCase();
                    this.lastSearchImageString = lowerCase;
                    if (!TextUtils.isEmpty(lowerCase)) {
                        AnonymousClass1 r0 = new Runnable() {
                            public void run() {
                                GifAdapter.this.search(text, "", true);
                            }
                        };
                        this.searchRunnable = r0;
                        AndroidUtilities.runOnUIThread(r0, 300);
                    }
                }
            }
        }

        public void searchEmoji(String emoji) {
            if (!this.lastSearchIsEmoji || !TextUtils.equals(this.lastSearchImageString, emoji)) {
                search(emoji, "", true, true, true);
            } else {
                EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(1, 0);
            }
        }

        /* access modifiers changed from: protected */
        public void search(String query, String offset, boolean searchUser) {
            search(query, offset, searchUser, false, false);
        }

        /* access modifiers changed from: protected */
        public void search(String query, String offset, boolean searchUser, boolean isEmoji, boolean cache) {
            String str = query;
            String str2 = offset;
            boolean z = isEmoji;
            int i = this.reqId;
            if (i != 0) {
                if (i >= 0) {
                    ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                }
                this.reqId = 0;
            }
            this.lastSearchImageString = str;
            this.lastSearchIsEmoji = z;
            GifProgressEmptyView gifProgressEmptyView = this.progressEmptyView;
            if (gifProgressEmptyView != null) {
                gifProgressEmptyView.setLoadingState(z);
            }
            TLObject object = MessagesController.getInstance(EmojiView.this.currentAccount).getUserOrChat(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot);
            if (object instanceof TLRPC.User) {
                if (!this.withRecent && TextUtils.isEmpty(offset)) {
                    EmojiView.this.gifSearchField.progressDrawable.startAnimation();
                }
                this.bot = (TLRPC.User) object;
                String key = "gif_search_" + str + "_" + str2;
                EmojiView$GifAdapter$$ExternalSyntheticLambda3 emojiView$GifAdapter$$ExternalSyntheticLambda3 = new EmojiView$GifAdapter$$ExternalSyntheticLambda3(this, query, offset, searchUser, isEmoji, cache, key);
                if (!cache && !this.withRecent && z && TextUtils.isEmpty(offset)) {
                    this.results.clear();
                    this.resultsMap.clear();
                    if (EmojiView.this.gifGridView.getAdapter() != this) {
                        EmojiView.this.gifGridView.setAdapter(this);
                    }
                    notifyDataSetChanged();
                    EmojiView.this.scrollGifsToTop();
                }
                if (cache && EmojiView.this.gifCache.containsKey(key)) {
                    m2256lambda$search$2$orgtelegramuiComponentsEmojiView$GifAdapter(query, offset, searchUser, isEmoji, true, key, (TLObject) EmojiView.this.gifCache.get(key));
                } else if (!EmojiView.this.gifSearchPreloader.isLoading(key)) {
                    if (cache) {
                        this.reqId = -1;
                        MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(key, emojiView$GifAdapter$$ExternalSyntheticLambda3);
                        return;
                    }
                    TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
                    req.query = str == null ? "" : str;
                    req.bot = MessagesController.getInstance(EmojiView.this.currentAccount).getInputUser(this.bot);
                    req.offset = str2;
                    req.peer = new TLRPC.TL_inputPeerEmpty();
                    this.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, emojiView$GifAdapter$$ExternalSyntheticLambda3, 2);
                }
            } else if (searchUser) {
                searchBotUser();
                if (!this.withRecent) {
                    EmojiView.this.gifSearchField.progressDrawable.startAnimation();
                }
            }
        }

        /* renamed from: lambda$search$3$org-telegram-ui-Components-EmojiView$GifAdapter  reason: not valid java name */
        public /* synthetic */ void m2257lambda$search$3$orgtelegramuiComponentsEmojiView$GifAdapter(String query, String offset, boolean searchUser, boolean isEmoji, boolean cache, String key, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new EmojiView$GifAdapter$$ExternalSyntheticLambda0(this, query, offset, searchUser, isEmoji, cache, key, response));
        }

        /* access modifiers changed from: private */
        /* renamed from: processResponse */
        public void m2256lambda$search$2$orgtelegramuiComponentsEmojiView$GifAdapter(String query, String offset, boolean searchUser, boolean isEmoji, boolean cache, String key, TLObject response) {
            if (query != null && query.equals(this.lastSearchImageString)) {
                boolean z = false;
                this.reqId = 0;
                if (!cache || ((response instanceof TLRPC.messages_BotResults) && !((TLRPC.messages_BotResults) response).results.isEmpty())) {
                    if (!this.withRecent && TextUtils.isEmpty(offset)) {
                        this.results.clear();
                        this.resultsMap.clear();
                        EmojiView.this.gifSearchField.progressDrawable.stopAnimation();
                    }
                    if (response instanceof TLRPC.messages_BotResults) {
                        int addedCount = 0;
                        int oldCount = this.results.size();
                        TLRPC.messages_BotResults res = (TLRPC.messages_BotResults) response;
                        if (!EmojiView.this.gifCache.containsKey(key)) {
                            EmojiView.this.gifCache.put(key, res);
                        }
                        if (!cache && res.cache_time != 0) {
                            MessagesStorage.getInstance(EmojiView.this.currentAccount).saveBotCache(key, res);
                        }
                        this.nextSearchOffset = res.next_offset;
                        for (int a = 0; a < res.results.size(); a++) {
                            TLRPC.BotInlineResult result = res.results.get(a);
                            if (!this.resultsMap.containsKey(result.id)) {
                                result.query_id = res.query_id;
                                this.results.add(result);
                                this.resultsMap.put(result.id, result);
                                addedCount++;
                            }
                        }
                        if (oldCount == this.results.size() || TextUtils.isEmpty(this.nextSearchOffset)) {
                            z = true;
                        }
                        this.searchEndReached = z;
                        if (addedCount != 0) {
                            if (!isEmoji || oldCount != 0) {
                                updateItems();
                                if (!this.withRecent) {
                                    if (oldCount != 0) {
                                        notifyItemChanged(oldCount);
                                    }
                                    notifyItemRangeInserted(oldCount + 1, addedCount);
                                } else if (oldCount != 0) {
                                    notifyItemChanged(this.recentItemsCount + 1 + oldCount);
                                    notifyItemRangeInserted(this.recentItemsCount + 1 + oldCount + 1, addedCount);
                                } else {
                                    notifyItemRangeInserted(this.recentItemsCount + 1, addedCount + 1);
                                }
                            } else {
                                notifyDataSetChanged();
                            }
                        } else if (this.results.isEmpty()) {
                            notifyDataSetChanged();
                        }
                    } else {
                        notifyDataSetChanged();
                    }
                    if (!this.withRecent) {
                        if (EmojiView.this.gifGridView.getAdapter() != this) {
                            EmojiView.this.gifGridView.setAdapter(this);
                        }
                        if (isEmoji && !TextUtils.isEmpty(query) && TextUtils.isEmpty(offset)) {
                            EmojiView.this.scrollGifsToTop();
                            return;
                        }
                        return;
                    }
                    return;
                }
                search(query, offset, searchUser, isEmoji, false);
            }
        }
    }

    private class GifSearchPreloader {
        private final List<String> loadingKeys;

        private GifSearchPreloader() {
            this.loadingKeys = new ArrayList();
        }

        public boolean isLoading(String key) {
            return this.loadingKeys.contains(key);
        }

        public void preload(String query) {
            preload(query, "", true);
        }

        private void preload(String query, String offset, boolean cache) {
            String key = "gif_search_" + query + "_" + offset;
            if (!cache || !EmojiView.this.gifCache.containsKey(key)) {
                EmojiView$GifSearchPreloader$$ExternalSyntheticLambda1 emojiView$GifSearchPreloader$$ExternalSyntheticLambda1 = new EmojiView$GifSearchPreloader$$ExternalSyntheticLambda1(this, query, offset, cache, key);
                if (cache) {
                    this.loadingKeys.add(key);
                    MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(key, emojiView$GifSearchPreloader$$ExternalSyntheticLambda1);
                    return;
                }
                MessagesController messagesController = MessagesController.getInstance(EmojiView.this.currentAccount);
                TLObject gifSearchBot = messagesController.getUserOrChat(messagesController.gifSearchBot);
                if (gifSearchBot instanceof TLRPC.User) {
                    this.loadingKeys.add(key);
                    TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
                    req.query = query == null ? "" : query;
                    req.bot = messagesController.getInputUser((TLRPC.User) gifSearchBot);
                    req.offset = offset;
                    req.peer = new TLRPC.TL_inputPeerEmpty();
                    ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, emojiView$GifSearchPreloader$$ExternalSyntheticLambda1, 2);
                }
            }
        }

        /* renamed from: lambda$preload$1$org-telegram-ui-Components-EmojiView$GifSearchPreloader  reason: not valid java name */
        public /* synthetic */ void m2261x522811b9(String query, String offset, boolean cache, String key, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new EmojiView$GifSearchPreloader$$ExternalSyntheticLambda0(this, query, offset, cache, key, response));
        }

        /* access modifiers changed from: private */
        /* renamed from: processResponse */
        public void m2260x2CLASSNAMEb8(String query, String offset, boolean cache, String key, TLObject response) {
            this.loadingKeys.remove(key);
            if (EmojiView.this.gifSearchAdapter.lastSearchIsEmoji && EmojiView.this.gifSearchAdapter.lastSearchImageString.equals(query)) {
                EmojiView.this.gifSearchAdapter.m2256lambda$search$2$orgtelegramuiComponentsEmojiView$GifAdapter(query, offset, false, true, cache, key, response);
            } else if (cache && (!(response instanceof TLRPC.messages_BotResults) || ((TLRPC.messages_BotResults) response).results.isEmpty())) {
                preload(query, offset, false);
            } else if ((response instanceof TLRPC.messages_BotResults) && !EmojiView.this.gifCache.containsKey(key)) {
                EmojiView.this.gifCache.put(key, (TLRPC.messages_BotResults) response);
            }
        }
    }

    private class GifLayoutManager extends ExtendedGridLayoutManager {
        private Size size = new Size();

        public GifLayoutManager(Context context) {
            super(context, 100, true);
            setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(EmojiView.this) {
                public int getSpanSize(int position) {
                    if (position == 0 || (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty())) {
                        return GifLayoutManager.this.getSpanCount();
                    }
                    return GifLayoutManager.this.getSpanSizeForItem(position - 1);
                }
            });
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v21, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: org.telegram.tgnet.TLRPC$Document} */
        /* access modifiers changed from: protected */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.ui.Components.Size getSizeForItem(int r4) {
            /*
                r3 = this;
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.RecyclerListView r0 = r0.gifGridView
                androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
                org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$GifAdapter r1 = r1.gifAdapter
                if (r0 != r1) goto L_0x0075
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifAdapter
                int r0 = r0.recentItemsCount
                if (r4 <= r0) goto L_0x0057
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifAdapter
                java.util.ArrayList r0 = r0.results
                org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$GifAdapter r1 = r1.gifAdapter
                int r1 = r1.recentItemsCount
                int r1 = r4 - r1
                int r1 = r1 + -1
                java.lang.Object r0 = r0.get(r1)
                org.telegram.tgnet.TLRPC$BotInlineResult r0 = (org.telegram.tgnet.TLRPC.BotInlineResult) r0
                org.telegram.tgnet.TLRPC$Document r1 = r0.document
                if (r1 == 0) goto L_0x0043
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r1.attributes
                goto L_0x0056
            L_0x0043:
                org.telegram.tgnet.TLRPC$WebDocument r2 = r0.content
                if (r2 == 0) goto L_0x004c
                org.telegram.tgnet.TLRPC$WebDocument r2 = r0.content
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
                goto L_0x0056
            L_0x004c:
                org.telegram.tgnet.TLRPC$WebDocument r2 = r0.thumb
                if (r2 == 0) goto L_0x0055
                org.telegram.tgnet.TLRPC$WebDocument r2 = r0.thumb
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
                goto L_0x0056
            L_0x0055:
                r2 = 0
            L_0x0056:
                goto L_0x00b2
            L_0x0057:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifAdapter
                int r0 = r0.recentItemsCount
                if (r4 != r0) goto L_0x0065
                r0 = 0
                return r0
            L_0x0065:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r0 = r0.recentGifs
                java.lang.Object r0 = r0.get(r4)
                r1 = r0
                org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC.Document) r1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r1.attributes
                goto L_0x00b2
            L_0x0075:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifSearchAdapter
                java.util.ArrayList r0 = r0.results
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x00b0
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifSearchAdapter
                java.util.ArrayList r0 = r0.results
                java.lang.Object r0 = r0.get(r4)
                org.telegram.tgnet.TLRPC$BotInlineResult r0 = (org.telegram.tgnet.TLRPC.BotInlineResult) r0
                org.telegram.tgnet.TLRPC$Document r1 = r0.document
                if (r1 == 0) goto L_0x009c
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r1.attributes
                goto L_0x00af
            L_0x009c:
                org.telegram.tgnet.TLRPC$WebDocument r2 = r0.content
                if (r2 == 0) goto L_0x00a5
                org.telegram.tgnet.TLRPC$WebDocument r2 = r0.content
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
                goto L_0x00af
            L_0x00a5:
                org.telegram.tgnet.TLRPC$WebDocument r2 = r0.thumb
                if (r2 == 0) goto L_0x00ae
                org.telegram.tgnet.TLRPC$WebDocument r2 = r0.thumb
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
                goto L_0x00af
            L_0x00ae:
                r2 = 0
            L_0x00af:
                goto L_0x00b2
            L_0x00b0:
                r1 = 0
                r2 = 0
            L_0x00b2:
                org.telegram.ui.Components.Size r0 = r3.getSizeForItem(r1, r2)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.GifLayoutManager.getSizeForItem(int):org.telegram.ui.Components.Size");
        }

        /* access modifiers changed from: protected */
        public int getFlowItemCount() {
            if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifSearchAdapter || !EmojiView.this.gifSearchAdapter.results.isEmpty()) {
                return getItemCount() - 1;
            }
            return 0;
        }

        public Size getSizeForItem(TLRPC.Document document) {
            return getSizeForItem(document, document.attributes);
        }

        public Size getSizeForItem(TLRPC.Document document, List<TLRPC.DocumentAttribute> attributes) {
            TLRPC.DocumentAttribute attribute;
            TLRPC.PhotoSize thumb;
            Size size2 = this.size;
            size2.height = 100.0f;
            size2.width = 100.0f;
            if (!(document == null || (thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90)) == null || thumb.w == 0 || thumb.h == 0)) {
                this.size.width = (float) thumb.w;
                this.size.height = (float) thumb.h;
            }
            if (attributes != null) {
                int b = 0;
                while (true) {
                    if (b >= attributes.size()) {
                        break;
                    }
                    attribute = attributes.get(b);
                    if ((attribute instanceof TLRPC.TL_documentAttributeImageSize) || (attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                        this.size.width = (float) attribute.w;
                        this.size.height = (float) attribute.h;
                    } else {
                        b++;
                    }
                }
                this.size.width = (float) attribute.w;
                this.size.height = (float) attribute.h;
            }
            return this.size;
        }
    }

    private class GifProgressEmptyView extends FrameLayout {
        /* access modifiers changed from: private */
        public final ImageView imageView;
        private boolean loadingState;
        /* access modifiers changed from: private */
        public final RadialProgressView progressView;
        /* access modifiers changed from: private */
        public final TextView textView;

        public GifProgressEmptyView(Context context) {
            super(context);
            ImageView imageView2 = new ImageView(getContext());
            this.imageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            imageView2.setImageResource(NUM);
            imageView2.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
            addView(imageView2, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
            TextView textView2 = new TextView(getContext());
            this.textView = textView2;
            textView2.setText(LocaleController.getString("NoGIFsFound", NUM));
            textView2.setTextSize(1, 16.0f);
            textView2.setTextColor(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"));
            addView(textView2, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context, EmojiView.this.resourcesProvider);
            this.progressView = radialProgressView;
            radialProgressView.setVisibility(8);
            radialProgressView.setProgressColor(EmojiView.this.getThemedColor("progressCircle"));
            addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int height2 = EmojiView.this.gifGridView.getMeasuredHeight();
            if (!this.loadingState) {
                height = (int) (((float) (((height2 - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3)) * 1.7f);
            } else {
                height = height2 - AndroidUtilities.dp(92.0f);
            }
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height, NUM));
        }

        public boolean isLoadingState() {
            return this.loadingState;
        }

        public void setLoadingState(boolean loadingState2) {
            if (this.loadingState != loadingState2) {
                this.loadingState = loadingState2;
                int i = 8;
                this.imageView.setVisibility(loadingState2 ? 8 : 0);
                this.textView.setVisibility(loadingState2 ? 8 : 0);
                RadialProgressView radialProgressView = this.progressView;
                if (loadingState2) {
                    i = 0;
                }
                radialProgressView.setVisibility(i);
            }
        }
    }

    private class StickersSearchGridAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<Object> cacheParent = new SparseArray<>();
        boolean cleared;
        private Context context;
        /* access modifiers changed from: private */
        public ArrayList<ArrayList<TLRPC.Document>> emojiArrays = new ArrayList<>();
        /* access modifiers changed from: private */
        public int emojiSearchId;
        /* access modifiers changed from: private */
        public HashMap<ArrayList<TLRPC.Document>, String> emojiStickers = new HashMap<>();
        /* access modifiers changed from: private */
        public ArrayList<TLRPC.TL_messages_stickerSet> localPacks = new ArrayList<>();
        /* access modifiers changed from: private */
        public HashMap<TLRPC.TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<TLRPC.TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
        private SparseArray<String> positionToEmoji = new SparseArray<>();
        private SparseIntArray positionToRow = new SparseIntArray();
        /* access modifiers changed from: private */
        public SparseArray<TLRPC.StickerSetCovered> positionsToSets = new SparseArray<>();
        /* access modifiers changed from: private */
        public int reqId;
        /* access modifiers changed from: private */
        public int reqId2;
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        /* access modifiers changed from: private */
        public String searchQuery;
        private Runnable searchRunnable = new Runnable() {
            /* access modifiers changed from: private */
            public void clear() {
                if (!StickersSearchGridAdapter.this.cleared) {
                    StickersSearchGridAdapter.this.cleared = true;
                    StickersSearchGridAdapter.this.emojiStickers.clear();
                    StickersSearchGridAdapter.this.emojiArrays.clear();
                    StickersSearchGridAdapter.this.localPacks.clear();
                    StickersSearchGridAdapter.this.serverPacks.clear();
                    StickersSearchGridAdapter.this.localPacksByShortName.clear();
                    StickersSearchGridAdapter.this.localPacksByName.clear();
                }
            }

            public void run() {
                if (!TextUtils.isEmpty(StickersSearchGridAdapter.this.searchQuery)) {
                    EmojiView.this.stickersSearchField.progressDrawable.startAnimation();
                    StickersSearchGridAdapter.this.cleared = false;
                    final int lastId = StickersSearchGridAdapter.access$16204(StickersSearchGridAdapter.this);
                    ArrayList<TLRPC.Document> emojiStickersArray = new ArrayList<>(0);
                    LongSparseArray<TLRPC.Document> emojiStickersMap = new LongSparseArray<>(0);
                    final HashMap<String, ArrayList<TLRPC.Document>> allStickers = MediaDataController.getInstance(EmojiView.this.currentAccount).getAllStickers();
                    if (StickersSearchGridAdapter.this.searchQuery.length() <= 14) {
                        CharSequence emoji = StickersSearchGridAdapter.this.searchQuery;
                        int length = emoji.length();
                        int a = 0;
                        while (a < length) {
                            if (a < length - 1 && ((emoji.charAt(a) == 55356 && emoji.charAt(a + 1) >= 57339 && emoji.charAt(a + 1) <= 57343) || (emoji.charAt(a) == 8205 && (emoji.charAt(a + 1) == 9792 || emoji.charAt(a + 1) == 9794)))) {
                                emoji = TextUtils.concat(new CharSequence[]{emoji.subSequence(0, a), emoji.subSequence(a + 2, emoji.length())});
                                length -= 2;
                                a--;
                            } else if (emoji.charAt(a) == 65039) {
                                emoji = TextUtils.concat(new CharSequence[]{emoji.subSequence(0, a), emoji.subSequence(a + 1, emoji.length())});
                                length--;
                                a--;
                            }
                            a++;
                        }
                        ArrayList<TLRPC.Document> newStickers = allStickers != null ? allStickers.get(emoji.toString()) : null;
                        if (newStickers != null && !newStickers.isEmpty()) {
                            clear();
                            emojiStickersArray.addAll(newStickers);
                            int size = newStickers.size();
                            for (int a2 = 0; a2 < size; a2++) {
                                TLRPC.Document document = newStickers.get(a2);
                                emojiStickersMap.put(document.id, document);
                            }
                            StickersSearchGridAdapter.this.emojiStickers.put(emojiStickersArray, StickersSearchGridAdapter.this.searchQuery);
                            StickersSearchGridAdapter.this.emojiArrays.add(emojiStickersArray);
                        }
                    }
                    if (allStickers != null && !allStickers.isEmpty() && StickersSearchGridAdapter.this.searchQuery.length() > 1) {
                        String[] newLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        if (!Arrays.equals(EmojiView.this.lastSearchKeyboardLanguage, newLanguage)) {
                            MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(newLanguage);
                        }
                        String[] unused = EmojiView.this.lastSearchKeyboardLanguage = newLanguage;
                        MediaDataController.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, StickersSearchGridAdapter.this.searchQuery, false, new MediaDataController.KeywordResultCallback() {
                            public void run(ArrayList<MediaDataController.KeywordResult> param, String alias) {
                                if (lastId == StickersSearchGridAdapter.this.emojiSearchId) {
                                    boolean added = false;
                                    int size = param.size();
                                    for (int a = 0; a < size; a++) {
                                        String emoji = param.get(a).emoji;
                                        HashMap hashMap = allStickers;
                                        ArrayList<TLRPC.Document> newStickers = hashMap != null ? (ArrayList) hashMap.get(emoji) : null;
                                        if (newStickers != null && !newStickers.isEmpty()) {
                                            AnonymousClass1.this.clear();
                                            if (!StickersSearchGridAdapter.this.emojiStickers.containsKey(newStickers)) {
                                                StickersSearchGridAdapter.this.emojiStickers.put(newStickers, emoji);
                                                StickersSearchGridAdapter.this.emojiArrays.add(newStickers);
                                                added = true;
                                            }
                                        }
                                    }
                                    if (added) {
                                        StickersSearchGridAdapter.this.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                    }
                    ArrayList<TLRPC.TL_messages_stickerSet> local = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSets(0);
                    int size2 = local.size();
                    for (int a3 = 0; a3 < size2; a3++) {
                        TLRPC.TL_messages_stickerSet set = local.get(a3);
                        int indexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(set.set.title, StickersSearchGridAdapter.this.searchQuery);
                        int index = indexOfIgnoreCase;
                        if (indexOfIgnoreCase >= 0) {
                            if (index == 0 || set.set.title.charAt(index - 1) == ' ') {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(set);
                                StickersSearchGridAdapter.this.localPacksByName.put(set, Integer.valueOf(index));
                            }
                        } else if (set.set.short_name != null) {
                            int indexOfIgnoreCase2 = AndroidUtilities.indexOfIgnoreCase(set.set.short_name, StickersSearchGridAdapter.this.searchQuery);
                            int index2 = indexOfIgnoreCase2;
                            if (indexOfIgnoreCase2 >= 0 && (index2 == 0 || set.set.short_name.charAt(index2 - 1) == ' ')) {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(set);
                                StickersSearchGridAdapter.this.localPacksByShortName.put(set, true);
                            }
                        }
                    }
                    ArrayList<TLRPC.TL_messages_stickerSet> local2 = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSets(3);
                    int size3 = local2.size();
                    for (int a4 = 0; a4 < size3; a4++) {
                        TLRPC.TL_messages_stickerSet set2 = local2.get(a4);
                        int indexOfIgnoreCase3 = AndroidUtilities.indexOfIgnoreCase(set2.set.title, StickersSearchGridAdapter.this.searchQuery);
                        int index3 = indexOfIgnoreCase3;
                        if (indexOfIgnoreCase3 >= 0) {
                            if (index3 == 0 || set2.set.title.charAt(index3 - 1) == ' ') {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(set2);
                                StickersSearchGridAdapter.this.localPacksByName.put(set2, Integer.valueOf(index3));
                            }
                        } else if (set2.set.short_name != null) {
                            int indexOfIgnoreCase4 = AndroidUtilities.indexOfIgnoreCase(set2.set.short_name, StickersSearchGridAdapter.this.searchQuery);
                            int index4 = indexOfIgnoreCase4;
                            if (indexOfIgnoreCase4 >= 0 && (index4 == 0 || set2.set.short_name.charAt(index4 - 1) == ' ')) {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(set2);
                                StickersSearchGridAdapter.this.localPacksByShortName.put(set2, true);
                            }
                        }
                    }
                    if ((!StickersSearchGridAdapter.this.localPacks.isEmpty() || !StickersSearchGridAdapter.this.emojiStickers.isEmpty()) && EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                        EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                    }
                    TLRPC.TL_messages_searchStickerSets req = new TLRPC.TL_messages_searchStickerSets();
                    req.q = StickersSearchGridAdapter.this.searchQuery;
                    StickersSearchGridAdapter stickersSearchGridAdapter = StickersSearchGridAdapter.this;
                    int unused2 = stickersSearchGridAdapter.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, new EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda3(this, req));
                    if (Emoji.isValidEmoji(StickersSearchGridAdapter.this.searchQuery)) {
                        TLRPC.TL_messages_getStickers req2 = new TLRPC.TL_messages_getStickers();
                        req2.emoticon = StickersSearchGridAdapter.this.searchQuery;
                        req2.hash = 0;
                        StickersSearchGridAdapter stickersSearchGridAdapter2 = StickersSearchGridAdapter.this;
                        int unused3 = stickersSearchGridAdapter2.reqId2 = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req2, new EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda2(this, req2, emojiStickersArray, emojiStickersMap));
                    }
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }

            /* renamed from: lambda$run$1$org-telegram-ui-Components-EmojiView$StickersSearchGridAdapter$1  reason: not valid java name */
            public /* synthetic */ void m2269x55var_(TLRPC.TL_messages_searchStickerSets req, TLObject response, TLRPC.TL_error error) {
                if (response instanceof TLRPC.TL_messages_foundStickerSets) {
                    AndroidUtilities.runOnUIThread(new EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda1(this, req, response));
                }
            }

            /* renamed from: lambda$run$0$org-telegram-ui-Components-EmojiView$StickersSearchGridAdapter$1  reason: not valid java name */
            public /* synthetic */ void m2268xebc7bd08(TLRPC.TL_messages_searchStickerSets req, TLObject response) {
                if (req.q.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    clear();
                    EmojiView.this.stickersSearchField.progressDrawable.stopAnimation();
                    int unused = StickersSearchGridAdapter.this.reqId = 0;
                    if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                        EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                    }
                    StickersSearchGridAdapter.this.serverPacks.addAll(((TLRPC.TL_messages_foundStickerSets) response).sets);
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }

            /* renamed from: lambda$run$3$org-telegram-ui-Components-EmojiView$StickersSearchGridAdapter$1  reason: not valid java name */
            public /* synthetic */ void m2271x2a565565(TLRPC.TL_messages_getStickers req2, ArrayList emojiStickersArray, LongSparseArray emojiStickersMap, TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda0(this, req2, response, emojiStickersArray, emojiStickersMap));
            }

            /* renamed from: lambda$run$2$org-telegram-ui-Components-EmojiView$StickersSearchGridAdapter$1  reason: not valid java name */
            public /* synthetic */ void m2270xCLASSNAMEcd46(TLRPC.TL_messages_getStickers req2, TLObject response, ArrayList emojiStickersArray, LongSparseArray emojiStickersMap) {
                if (req2.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    int unused = StickersSearchGridAdapter.this.reqId2 = 0;
                    if (response instanceof TLRPC.TL_messages_stickers) {
                        TLRPC.TL_messages_stickers res = (TLRPC.TL_messages_stickers) response;
                        int oldCount = emojiStickersArray.size();
                        int size = res.stickers.size();
                        for (int a = 0; a < size; a++) {
                            TLRPC.Document document = res.stickers.get(a);
                            if (emojiStickersMap.indexOfKey(document.id) < 0) {
                                emojiStickersArray.add(document);
                            }
                        }
                        if (oldCount != emojiStickersArray.size()) {
                            StickersSearchGridAdapter.this.emojiStickers.put(emojiStickersArray, StickersSearchGridAdapter.this.searchQuery);
                            if (oldCount == 0) {
                                StickersSearchGridAdapter.this.emojiArrays.add(emojiStickersArray);
                            }
                            StickersSearchGridAdapter.this.notifyDataSetChanged();
                        }
                    }
                }
            }
        };
        /* access modifiers changed from: private */
        public ArrayList<TLRPC.StickerSetCovered> serverPacks = new ArrayList<>();
        /* access modifiers changed from: private */
        public int totalItems;

        static /* synthetic */ int access$16204(StickersSearchGridAdapter x0) {
            int i = x0.emojiSearchId + 1;
            x0.emojiSearchId = i;
            return i;
        }

        public StickersSearchGridAdapter(Context context2) {
            this.context = context2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            int i = this.totalItems;
            if (i != 1) {
                return i + 1;
            }
            return 2;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public void search(String text) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (this.reqId2 != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId2, true);
                this.reqId2 = 0;
            }
            if (TextUtils.isEmpty(text)) {
                this.searchQuery = null;
                this.localPacks.clear();
                this.emojiStickers.clear();
                this.serverPacks.clear();
                if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersGridAdapter) {
                    EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersGridAdapter);
                }
                notifyDataSetChanged();
            } else {
                this.searchQuery = text.toLowerCase();
            }
            AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
            AndroidUtilities.runOnUIThread(this.searchRunnable, 300);
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 4;
            }
            if (position == 1 && this.totalItems == 1) {
                return 5;
            }
            Object object = this.cache.get(position);
            if (object == null) {
                return 1;
            }
            if (object instanceof TLRPC.Document) {
                return 0;
            }
            if (object instanceof TLRPC.StickerSetCovered) {
                return 3;
            }
            return 2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FrameLayout view = null;
            switch (viewType) {
                case 0:
                    view = new StickerEmojiCell(this.context, true) {
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    view = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    break;
                case 3:
                    view = new FeaturedStickerSetInfoCell(this.context, 17, false, true, EmojiView.this.resourcesProvider);
                    ((FeaturedStickerSetInfoCell) view).setAddOnClickListener(new EmojiView$StickersSearchGridAdapter$$ExternalSyntheticLambda0(this));
                    break;
                case 4:
                    view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
                case 5:
                    FrameLayout frameLayout = new FrameLayout(this.context) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec((int) (((float) (((EmojiView.this.stickersGridView.getMeasuredHeight() - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3)) * 1.7f), NUM));
                        }
                    };
                    ImageView imageView = new ImageView(this.context);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(NUM);
                    imageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
                    TextView textView = new TextView(this.context);
                    textView.setText(LocaleController.getString("NoStickersFound", NUM));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"));
                    frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
                    view = frameLayout;
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Components-EmojiView$StickersSearchGridAdapter  reason: not valid java name */
        public /* synthetic */ void m2267xcd177CLASSNAME(View v) {
            FeaturedStickerSetInfoCell parent1 = (FeaturedStickerSetInfoCell) v.getParent();
            TLRPC.StickerSetCovered pack = parent1.getStickerSet();
            if (EmojiView.this.installingStickerSets.indexOfKey(pack.set.id) < 0 && EmojiView.this.removingStickerSets.indexOfKey(pack.set.id) < 0) {
                if (parent1.isInstalled()) {
                    EmojiView.this.removingStickerSets.put(pack.set.id, pack);
                    EmojiView.this.delegate.onStickerSetRemove(parent1.getStickerSet());
                    return;
                }
                parent1.setAddDrawProgress(true, true);
                EmojiView.this.installingStickerSets.put(pack.set.id, pack);
                EmojiView.this.delegate.onStickerSetAdd(parent1.getStickerSet());
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:22:0x007d  */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x007f  */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x008c  */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x009c  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r13, int r14) {
            /*
                r12 = this;
                int r0 = r13.getItemViewType()
                r1 = 1
                r2 = 0
                switch(r0) {
                    case 0: goto L_0x01b3;
                    case 1: goto L_0x012b;
                    case 2: goto L_0x00c2;
                    case 3: goto L_0x000b;
                    default: goto L_0x0009;
                }
            L_0x0009:
                goto L_0x01f4
            L_0x000b:
                android.util.SparseArray<java.lang.Object> r0 = r12.cache
                java.lang.Object r0 = r0.get(r14)
                org.telegram.tgnet.TLRPC$StickerSetCovered r0 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r0
                android.view.View r3 = r13.itemView
                r9 = r3
                org.telegram.ui.Cells.FeaturedStickerSetInfoCell r9 = (org.telegram.ui.Cells.FeaturedStickerSetInfoCell) r9
                org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                android.util.LongSparseArray r3 = r3.installingStickerSets
                org.telegram.tgnet.TLRPC$StickerSet r4 = r0.set
                long r4 = r4.id
                int r3 = r3.indexOfKey(r4)
                if (r3 < 0) goto L_0x002a
                r3 = 1
                goto L_0x002b
            L_0x002a:
                r3 = 0
            L_0x002b:
                org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                android.util.LongSparseArray r4 = r4.removingStickerSets
                org.telegram.tgnet.TLRPC$StickerSet r5 = r0.set
                long r5 = r5.id
                int r4 = r4.indexOfKey(r5)
                if (r4 < 0) goto L_0x003c
                goto L_0x003d
            L_0x003c:
                r1 = 0
            L_0x003d:
                if (r3 != 0) goto L_0x0041
                if (r1 == 0) goto L_0x0071
            L_0x0041:
                if (r3 == 0) goto L_0x0059
                boolean r4 = r9.isInstalled()
                if (r4 == 0) goto L_0x0059
                org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                android.util.LongSparseArray r4 = r4.installingStickerSets
                org.telegram.tgnet.TLRPC$StickerSet r5 = r0.set
                long r5 = r5.id
                r4.remove(r5)
                r3 = 0
                r10 = r3
                goto L_0x0072
            L_0x0059:
                if (r1 == 0) goto L_0x0071
                boolean r4 = r9.isInstalled()
                if (r4 != 0) goto L_0x0071
                org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                android.util.LongSparseArray r4 = r4.removingStickerSets
                org.telegram.tgnet.TLRPC$StickerSet r5 = r0.set
                long r5 = r5.id
                r4.remove(r5)
                r1 = 0
                r10 = r3
                goto L_0x0072
            L_0x0071:
                r10 = r3
            L_0x0072:
                r9.setAddDrawProgress(r10, r2)
                java.lang.String r3 = r12.searchQuery
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 == 0) goto L_0x007f
                r3 = -1
                goto L_0x0089
            L_0x007f:
                org.telegram.tgnet.TLRPC$StickerSet r3 = r0.set
                java.lang.String r3 = r3.title
                java.lang.String r4 = r12.searchQuery
                int r3 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r3, r4)
            L_0x0089:
                r11 = r3
                if (r11 < 0) goto L_0x009c
                r5 = 0
                r6 = 0
                java.lang.String r2 = r12.searchQuery
                int r8 = r2.length()
                r3 = r9
                r4 = r0
                r7 = r11
                r3.setStickerSet(r4, r5, r6, r7, r8)
                goto L_0x01f4
            L_0x009c:
                r9.setStickerSet(r0, r2)
                java.lang.String r2 = r12.searchQuery
                boolean r2 = android.text.TextUtils.isEmpty(r2)
                if (r2 != 0) goto L_0x01f4
                org.telegram.tgnet.TLRPC$StickerSet r2 = r0.set
                java.lang.String r2 = r2.short_name
                java.lang.String r3 = r12.searchQuery
                int r2 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r2, r3)
                if (r2 != 0) goto L_0x01f4
                org.telegram.tgnet.TLRPC$StickerSet r2 = r0.set
                java.lang.String r2 = r2.short_name
                java.lang.String r3 = r12.searchQuery
                int r3 = r3.length()
                r9.setUrl(r2, r3)
                goto L_0x01f4
            L_0x00c2:
                android.view.View r0 = r13.itemView
                org.telegram.ui.Cells.StickerSetNameCell r0 = (org.telegram.ui.Cells.StickerSetNameCell) r0
                android.util.SparseArray<java.lang.Object> r1 = r12.cache
                java.lang.Object r1 = r1.get(r14)
                boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_stickerSet
                if (r3 == 0) goto L_0x01f4
                r3 = r1
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r3 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r3
                java.lang.String r4 = r12.searchQuery
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 != 0) goto L_0x00fc
                java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_stickerSet, java.lang.Boolean> r4 = r12.localPacksByShortName
                boolean r4 = r4.containsKey(r3)
                if (r4 == 0) goto L_0x00fc
                org.telegram.tgnet.TLRPC$StickerSet r4 = r3.set
                if (r4 == 0) goto L_0x00ee
                org.telegram.tgnet.TLRPC$StickerSet r4 = r3.set
                java.lang.String r4 = r4.title
                r0.setText(r4, r2)
            L_0x00ee:
                org.telegram.tgnet.TLRPC$StickerSet r2 = r3.set
                java.lang.String r2 = r2.short_name
                java.lang.String r4 = r12.searchQuery
                int r4 = r4.length()
                r0.setUrl(r2, r4)
                goto L_0x0129
            L_0x00fc:
                java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_stickerSet, java.lang.Integer> r4 = r12.localPacksByName
                java.lang.Object r4 = r4.get(r3)
                java.lang.Integer r4 = (java.lang.Integer) r4
                org.telegram.tgnet.TLRPC$StickerSet r5 = r3.set
                if (r5 == 0) goto L_0x0125
                if (r4 == 0) goto L_0x0125
                org.telegram.tgnet.TLRPC$StickerSet r5 = r3.set
                java.lang.String r5 = r5.title
                int r6 = r4.intValue()
                java.lang.String r7 = r12.searchQuery
                boolean r7 = android.text.TextUtils.isEmpty(r7)
                if (r7 != 0) goto L_0x0121
                java.lang.String r7 = r12.searchQuery
                int r7 = r7.length()
                goto L_0x0122
            L_0x0121:
                r7 = 0
            L_0x0122:
                r0.setText(r5, r2, r6, r7)
            L_0x0125:
                r5 = 0
                r0.setUrl(r5, r2)
            L_0x0129:
                goto L_0x01f4
            L_0x012b:
                android.view.View r0 = r13.itemView
                org.telegram.ui.Cells.EmptyCell r0 = (org.telegram.ui.Cells.EmptyCell) r0
                int r2 = r12.totalItems
                r3 = 1118044160(0x42a40000, float:82.0)
                if (r14 != r2) goto L_0x01ab
                android.util.SparseIntArray r2 = r12.positionToRow
                int r4 = r14 + -1
                r5 = -2147483648(0xfffffffvar_, float:-0.0)
                int r2 = r2.get(r4, r5)
                if (r2 != r5) goto L_0x0145
                r0.setHeight(r1)
                goto L_0x01aa
            L_0x0145:
                android.util.SparseArray<java.lang.Object> r4 = r12.rowStartPack
                java.lang.Object r4 = r4.get(r2)
                boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messages_stickerSet
                if (r5 == 0) goto L_0x015d
                r5 = r4
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r5 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r5
                java.util.ArrayList r5 = r5.documents
                int r5 = r5.size()
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                goto L_0x0166
            L_0x015d:
                boolean r5 = r4 instanceof java.lang.Integer
                if (r5 == 0) goto L_0x0165
                r5 = r4
                java.lang.Integer r5 = (java.lang.Integer) r5
                goto L_0x0166
            L_0x0165:
                r5 = 0
            L_0x0166:
                if (r5 != 0) goto L_0x016c
                r0.setHeight(r1)
                goto L_0x01aa
            L_0x016c:
                int r6 = r5.intValue()
                if (r6 != 0) goto L_0x017c
                r1 = 1090519040(0x41000000, float:8.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r0.setHeight(r1)
                goto L_0x01aa
            L_0x017c:
                org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                androidx.viewpager.widget.ViewPager r6 = r6.pager
                int r6 = r6.getHeight()
                int r7 = r5.intValue()
                float r7 = (float) r7
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r8 = r8.stickersGridAdapter
                int r8 = r8.stickersPerRow
                float r8 = (float) r8
                float r7 = r7 / r8
                double r7 = (double) r7
                double r7 = java.lang.Math.ceil(r7)
                int r7 = (int) r7
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r7 = r7 * r3
                int r6 = r6 - r7
                if (r6 <= 0) goto L_0x01a7
                r1 = r6
            L_0x01a7:
                r0.setHeight(r1)
            L_0x01aa:
                goto L_0x01f4
            L_0x01ab:
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r0.setHeight(r1)
                goto L_0x01f4
            L_0x01b3:
                android.util.SparseArray<java.lang.Object> r0 = r12.cache
                java.lang.Object r0 = r0.get(r14)
                org.telegram.tgnet.TLRPC$Document r0 = (org.telegram.tgnet.TLRPC.Document) r0
                android.view.View r3 = r13.itemView
                r9 = r3
                org.telegram.ui.Cells.StickerEmojiCell r9 = (org.telegram.ui.Cells.StickerEmojiCell) r9
                r5 = 0
                android.util.SparseArray<java.lang.Object> r3 = r12.cacheParent
                java.lang.Object r6 = r3.get(r14)
                android.util.SparseArray<java.lang.String> r3 = r12.positionToEmoji
                java.lang.Object r3 = r3.get(r14)
                r7 = r3
                java.lang.String r7 = (java.lang.String) r7
                r8 = 0
                r3 = r9
                r4 = r0
                r3.setSticker(r4, r5, r6, r7, r8)
                org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r3 = r3.recentStickers
                boolean r3 = r3.contains(r0)
                if (r3 != 0) goto L_0x01f0
                org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r3 = r3.favouriteStickers
                boolean r3 = r3.contains(r0)
                if (r3 == 0) goto L_0x01ef
                goto L_0x01f0
            L_0x01ef:
                r1 = 0
            L_0x01f0:
                r9.setRecent(r1)
            L_0x01f4:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: org.telegram.tgnet.TLRPC$TL_messages_stickerSet} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void notifyDataSetChanged() {
            /*
                r22 = this;
                r0 = r22
                android.util.SparseArray<java.lang.Object> r1 = r0.rowStartPack
                r1.clear()
                android.util.SparseIntArray r1 = r0.positionToRow
                r1.clear()
                android.util.SparseArray<java.lang.Object> r1 = r0.cache
                r1.clear()
                android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r1 = r0.positionsToSets
                r1.clear()
                android.util.SparseArray<java.lang.String> r1 = r0.positionToEmoji
                r1.clear()
                r1 = 0
                r0.totalItems = r1
                r1 = 0
                r2 = -1
                java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r3 = r0.serverPacks
                int r3 = r3.size()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r4 = r0.localPacks
                int r4 = r4.size()
                java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r5 = r0.emojiArrays
                boolean r5 = r5.isEmpty()
                r5 = r5 ^ 1
            L_0x0034:
                int r6 = r3 + r4
                int r6 = r6 + r5
                if (r2 >= r6) goto L_0x0204
                r6 = 0
                r7 = -1
                if (r2 != r7) goto L_0x0050
                android.util.SparseArray<java.lang.Object> r7 = r0.cache
                int r8 = r0.totalItems
                int r9 = r8 + 1
                r0.totalItems = r9
                java.lang.String r9 = "search"
                r7.put(r8, r9)
                int r1 = r1 + 1
                r16 = r3
                goto L_0x01fe
            L_0x0050:
                r7 = r2
                if (r7 >= r4) goto L_0x0062
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r8 = r0.localPacks
                java.lang.Object r8 = r8.get(r7)
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r8
                java.util.ArrayList r9 = r8.documents
                r6 = r8
                r16 = r3
                goto L_0x0159
            L_0x0062:
                int r7 = r7 - r4
                if (r7 >= r5) goto L_0x014b
                r8 = 0
                java.lang.String r9 = ""
                r10 = 0
                java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r11 = r0.emojiArrays
                int r11 = r11.size()
            L_0x006f:
                if (r10 >= r11) goto L_0x010e
                java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r12 = r0.emojiArrays
                java.lang.Object r12 = r12.get(r10)
                java.util.ArrayList r12 = (java.util.ArrayList) r12
                java.util.HashMap<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>, java.lang.String> r13 = r0.emojiStickers
                java.lang.Object r13 = r13.get(r12)
                java.lang.String r13 = (java.lang.String) r13
                if (r13 == 0) goto L_0x0092
                boolean r14 = r9.equals(r13)
                if (r14 != 0) goto L_0x0092
                r9 = r13
                android.util.SparseArray<java.lang.String> r14 = r0.positionToEmoji
                int r15 = r0.totalItems
                int r15 = r15 + r8
                r14.put(r15, r9)
            L_0x0092:
                r14 = 0
                int r15 = r12.size()
            L_0x0097:
                if (r14 >= r15) goto L_0x00fe
                r16 = r3
                int r3 = r0.totalItems
                int r3 = r3 + r8
                r17 = r9
                org.telegram.ui.Components.EmojiView r9 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r9 = r9.stickersGridAdapter
                int r9 = r9.stickersPerRow
                int r9 = r8 / r9
                int r9 = r9 + r1
                java.lang.Object r18 = r12.get(r14)
                r19 = r11
                r11 = r18
                org.telegram.tgnet.TLRPC$Document r11 = (org.telegram.tgnet.TLRPC.Document) r11
                r18 = r12
                android.util.SparseArray<java.lang.Object> r12 = r0.cache
                r12.put(r3, r11)
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                int r12 = r12.currentAccount
                org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r12)
                r20 = r13
                r21 = r14
                long r13 = org.telegram.messenger.MediaDataController.getStickerSetId(r11)
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r12 = r12.getStickerSetById(r13)
                if (r12 == 0) goto L_0x00db
                android.util.SparseArray<java.lang.Object> r13 = r0.cacheParent
                r13.put(r3, r12)
            L_0x00db:
                android.util.SparseIntArray r13 = r0.positionToRow
                r13.put(r3, r9)
                if (r2 < r4) goto L_0x00ee
                boolean r13 = r6 instanceof org.telegram.tgnet.TLRPC.StickerSetCovered
                if (r13 == 0) goto L_0x00ee
                android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r13 = r0.positionsToSets
                r14 = r6
                org.telegram.tgnet.TLRPC$StickerSetCovered r14 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r14
                r13.put(r3, r14)
            L_0x00ee:
                int r8 = r8 + 1
                int r14 = r21 + 1
                r3 = r16
                r9 = r17
                r12 = r18
                r11 = r19
                r13 = r20
                goto L_0x0097
            L_0x00fe:
                r16 = r3
                r17 = r9
                r19 = r11
                r18 = r12
                r20 = r13
                r21 = r14
                int r10 = r10 + 1
                goto L_0x006f
            L_0x010e:
                r16 = r3
                r19 = r11
                float r3 = (float) r8
                org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r10 = r10.stickersGridAdapter
                int r10 = r10.stickersPerRow
                float r10 = (float) r10
                float r3 = r3 / r10
                double r10 = (double) r3
                double r10 = java.lang.Math.ceil(r10)
                int r3 = (int) r10
                r10 = 0
                r11 = r3
            L_0x0127:
                if (r10 >= r11) goto L_0x0137
                android.util.SparseArray<java.lang.Object> r12 = r0.rowStartPack
                int r13 = r1 + r10
                java.lang.Integer r14 = java.lang.Integer.valueOf(r8)
                r12.put(r13, r14)
                int r10 = r10 + 1
                goto L_0x0127
            L_0x0137:
                int r10 = r0.totalItems
                org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r11 = r11.stickersGridAdapter
                int r11 = r11.stickersPerRow
                int r11 = r11 * r3
                int r10 = r10 + r11
                r0.totalItems = r10
                int r1 = r1 + r3
                goto L_0x01fe
            L_0x014b:
                r16 = r3
                int r7 = r7 - r5
                java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r3 = r0.serverPacks
                java.lang.Object r3 = r3.get(r7)
                org.telegram.tgnet.TLRPC$StickerSetCovered r3 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r3
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r3.covers
                r6 = r3
            L_0x0159:
                boolean r3 = r9.isEmpty()
                if (r3 == 0) goto L_0x0161
                goto L_0x01fe
            L_0x0161:
                int r3 = r9.size()
                float r3 = (float) r3
                org.telegram.ui.Components.EmojiView r7 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r7 = r7.stickersGridAdapter
                int r7 = r7.stickersPerRow
                float r7 = (float) r7
                float r3 = r3 / r7
                double r7 = (double) r3
                double r7 = java.lang.Math.ceil(r7)
                int r3 = (int) r7
                android.util.SparseArray<java.lang.Object> r7 = r0.cache
                int r8 = r0.totalItems
                r7.put(r8, r6)
                if (r2 < r4) goto L_0x018f
                boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC.StickerSetCovered
                if (r7 == 0) goto L_0x018f
                android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r7 = r0.positionsToSets
                int r8 = r0.totalItems
                r10 = r6
                org.telegram.tgnet.TLRPC$StickerSetCovered r10 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r10
                r7.put(r8, r10)
            L_0x018f:
                android.util.SparseIntArray r7 = r0.positionToRow
                int r8 = r0.totalItems
                r7.put(r8, r1)
                r7 = 0
                int r8 = r9.size()
            L_0x019b:
                if (r7 >= r8) goto L_0x01d9
                int r10 = r7 + 1
                int r11 = r0.totalItems
                int r10 = r10 + r11
                int r11 = r1 + 1
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r12 = r12.stickersGridAdapter
                int r12 = r12.stickersPerRow
                int r12 = r7 / r12
                int r11 = r11 + r12
                java.lang.Object r12 = r9.get(r7)
                org.telegram.tgnet.TLRPC$Document r12 = (org.telegram.tgnet.TLRPC.Document) r12
                android.util.SparseArray<java.lang.Object> r13 = r0.cache
                r13.put(r10, r12)
                if (r6 == 0) goto L_0x01c3
                android.util.SparseArray<java.lang.Object> r13 = r0.cacheParent
                r13.put(r10, r6)
            L_0x01c3:
                android.util.SparseIntArray r13 = r0.positionToRow
                r13.put(r10, r11)
                if (r2 < r4) goto L_0x01d6
                boolean r13 = r6 instanceof org.telegram.tgnet.TLRPC.StickerSetCovered
                if (r13 == 0) goto L_0x01d6
                android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r13 = r0.positionsToSets
                r14 = r6
                org.telegram.tgnet.TLRPC$StickerSetCovered r14 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r14
                r13.put(r10, r14)
            L_0x01d6:
                int r7 = r7 + 1
                goto L_0x019b
            L_0x01d9:
                r7 = 0
                int r8 = r3 + 1
            L_0x01dc:
                if (r7 >= r8) goto L_0x01e8
                android.util.SparseArray<java.lang.Object> r10 = r0.rowStartPack
                int r11 = r1 + r7
                r10.put(r11, r6)
                int r7 = r7 + 1
                goto L_0x01dc
            L_0x01e8:
                int r7 = r0.totalItems
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r8 = r8.stickersGridAdapter
                int r8 = r8.stickersPerRow
                int r8 = r8 * r3
                int r8 = r8 + 1
                int r7 = r7 + r8
                r0.totalItems = r7
                int r7 = r3 + 1
                int r1 = r1 + r7
            L_0x01fe:
                int r2 = r2 + 1
                r3 = r16
                goto L_0x0034
            L_0x0204:
                super.notifyDataSetChanged()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.notifyDataSetChanged():void");
        }
    }

    public void searchProgressChanged() {
        updateStickerTabsPosition();
    }

    public float getStickersExpandOffset() {
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip == null) {
            return 0.0f;
        }
        return scrollSlidingTabStrip.getExpandedOffset();
    }

    public void setShowing(boolean showing2) {
        this.showing = showing2;
        updateStickerTabsPosition();
    }

    public void onMessageSend() {
        ChooseStickerActionTracker chooseStickerActionTracker2 = this.chooseStickerActionTracker;
        if (chooseStickerActionTracker2 != null) {
            chooseStickerActionTracker2.reset();
        }
    }

    public static abstract class ChooseStickerActionTracker {
        private final int currentAccount;
        private final long dialogId;
        long lastActionTime = -1;
        private final int threadId;
        boolean typingWasSent;
        boolean visible = false;

        public abstract boolean isShown();

        public ChooseStickerActionTracker(int currentAccount2, long dialogId2, int threadId2) {
            this.currentAccount = currentAccount2;
            this.dialogId = dialogId2;
            this.threadId = threadId2;
        }

        public void doSomeAction() {
            if (!this.visible) {
                return;
            }
            if (this.lastActionTime == -1) {
                this.lastActionTime = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - this.lastActionTime > 2000) {
                this.typingWasSent = true;
                this.lastActionTime = System.currentTimeMillis();
                MessagesController.getInstance(this.currentAccount).sendTyping(this.dialogId, this.threadId, 10, 0);
            }
        }

        /* access modifiers changed from: private */
        public void reset() {
            if (this.typingWasSent) {
                MessagesController.getInstance(this.currentAccount).sendTyping(this.dialogId, this.threadId, 2, 0);
            }
            this.lastActionTime = -1;
        }

        public void checkVisibility() {
            boolean isShown = isShown();
            this.visible = isShown;
            if (!isShown) {
                reset();
            }
        }
    }
}
