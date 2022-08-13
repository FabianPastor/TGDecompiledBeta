package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AccountInstance;
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
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_emojiURL;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_messages_foundStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_getEmojiURL;
import org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC$TL_messages_getStickers;
import org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickers;
import org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_BotResults;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetGroupInfoCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.PagerSlidingTabStrip;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TrendingStickersLayout;
import org.telegram.ui.ContentPreviewViewer;

public class EmojiView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static final ViewTreeObserver.OnScrollChangedListener NOP = EmojiView$$ExternalSyntheticLambda4.INSTANCE;
    /* access modifiers changed from: private */
    public static final Field superListenerField;
    private ArrayList<Tab> allTabs = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean allowAnimatedEmoji;
    /* access modifiers changed from: private */
    public View animateExpandFromButton;
    /* access modifiers changed from: private */
    public int animateExpandFromPosition;
    /* access modifiers changed from: private */
    public long animateExpandStartTime;
    /* access modifiers changed from: private */
    public int animateExpandToPosition;
    /* access modifiers changed from: private */
    public LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables;
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
    public FrameLayout bulletinContainer;
    /* access modifiers changed from: private */
    public Runnable checkExpandStickerTabsRunnable;
    /* access modifiers changed from: private */
    public ChooseStickerActionTracker chooseStickerActionTracker;
    /* access modifiers changed from: private */
    public ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate;
    public int currentAccount = UserConfig.selectedAccount;
    private int currentBackgroundType;
    /* access modifiers changed from: private */
    public long currentChatId;
    private int currentPage;
    /* access modifiers changed from: private */
    public ArrayList<Tab> currentTabs = new ArrayList<>();
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
    public EmojiGridView emojiGridView;
    /* access modifiers changed from: private */
    public float emojiLastX;
    /* access modifiers changed from: private */
    public float emojiLastY;
    /* access modifiers changed from: private */
    public GridLayoutManager emojiLayoutManager;
    private Drawable emojiLockDrawable;
    private Paint emojiLockPaint;
    /* access modifiers changed from: private */
    public boolean emojiPackAlertOpened;
    EmojiPagesAdapter emojiPagerAdapter;
    private RecyclerAnimationScrollHelper emojiScrollHelper;
    /* access modifiers changed from: private */
    public EmojiSearchAdapter emojiSearchAdapter;
    /* access modifiers changed from: private */
    public SearchField emojiSearchField;
    /* access modifiers changed from: private */
    public int emojiSize;
    /* access modifiers changed from: private */
    public boolean emojiSmoothScrolling;
    /* access modifiers changed from: private */
    public AnimatorSet emojiTabShadowAnimator;
    /* access modifiers changed from: private */
    public EmojiTabsStrip emojiTabs;
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
    public ArrayList<EmojiPack> emojipacksProcessed;
    /* access modifiers changed from: private */
    public boolean expandStickersByDragg;
    /* access modifiers changed from: private */
    public ArrayList<Long> expandedEmojiSets;
    /* access modifiers changed from: private */
    public int favTabNum;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Document> favouriteStickers = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$StickerSetCovered> featuredEmojiSets = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$StickerSetCovered> featuredStickerSets = new ArrayList<>();
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
    public BaseFragment fragment;
    /* access modifiers changed from: private */
    public GifAdapter gifAdapter;
    /* access modifiers changed from: private */
    public final Map<String, TLRPC$messages_BotResults> gifCache = new HashMap();
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
    public TLRPC$TL_messages_stickerSet groupStickerSet;
    /* access modifiers changed from: private */
    public boolean groupStickersHidden;
    /* access modifiers changed from: private */
    public boolean hasChatStickers;
    /* access modifiers changed from: private */
    public boolean ignoreStickersScroll;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull info;
    public ArrayList<Long> installedEmojiSets;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC$StickerSetCovered> installingStickerSets;
    private boolean isLayout;
    private float lastBottomScrollDy;
    private int lastNotifyHeight;
    private int lastNotifyHeight2;
    private int lastNotifyWidth;
    private ArrayList<String> lastRecentArray;
    private int lastRecentCount;
    /* access modifiers changed from: private */
    public String[] lastSearchKeyboardLanguage;
    /* access modifiers changed from: private */
    public float lastStickersX;
    /* access modifiers changed from: private */
    public int[] location;
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
    /* access modifiers changed from: private */
    public boolean premiumBulletin;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Document> premiumStickers = new ArrayList<>();
    /* access modifiers changed from: private */
    public int premiumTabNum;
    private TLRPC$StickerSetCovered[] primaryInstallingStickerSets;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Document> recentGifs = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Document> recentStickers = new ArrayList<>();
    /* access modifiers changed from: private */
    public int recentTabNum;
    Rect rect;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC$StickerSetCovered> removingStickerSets;
    /* access modifiers changed from: private */
    public final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public AnimatorSet searchAnimation;
    private ImageView searchButton;
    /* access modifiers changed from: private */
    public int searchFieldHeight;
    private Drawable searchIconDotDrawable;
    private Drawable searchIconDrawable;
    private View shadowLine;
    private boolean showing;
    /* access modifiers changed from: private */
    public long shownBottomTabAfterClick;
    private Drawable[] stickerIcons;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = new ArrayList<>();
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
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    private RecyclerAnimationScrollHelper stickersScrollHelper;
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
    /* access modifiers changed from: private */
    public final int[] tabsMinusDy = new int[3];
    private ObjectAnimator[] tabsYAnimators = new ObjectAnimator[3];
    private HashMap<Long, Utilities.Callback<TLRPC$TL_messages_stickerSet>> toInstall;
    /* access modifiers changed from: private */
    public TrendingAdapter trendingAdapter;
    /* access modifiers changed from: private */
    public TrendingAdapter trendingEmojiAdapter;
    private int trendingTabNum;
    private PagerSlidingTabStrip typeTabs;

    public static class CustomEmoji {
        public long documentId;
        public TLRPC$TL_messages_stickerSet stickerSet;
    }

    public interface DragListener {
        void onDrag(int i);

        void onDragCancel();

        void onDragEnd(float f);

        void onDragStart();
    }

    public static class EmojiPack {
        public ArrayList<TLRPC$Document> documents;
        public boolean expanded;
        public boolean featured;
        public boolean free;
        public boolean installed;
        public TLRPC$StickerSet set;
    }

    public interface EmojiViewDelegate {

        /* renamed from: org.telegram.ui.Components.EmojiView$EmojiViewDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static boolean $default$canSchedule(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static long $default$getDialogId(EmojiViewDelegate emojiViewDelegate) {
                return 0;
            }

            public static float $default$getProgressToSearchOpened(EmojiViewDelegate emojiViewDelegate) {
                return 0.0f;
            }

            public static int $default$getThreadId(EmojiViewDelegate emojiViewDelegate) {
                return 0;
            }

            public static void $default$invalidateEnterView(EmojiViewDelegate emojiViewDelegate) {
            }

            public static boolean $default$isExpanded(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static boolean $default$isInScheduleMode(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static boolean $default$isSearchOpened(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static boolean $default$isUserSelf(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static void $default$onClearEmojiRecent(EmojiViewDelegate emojiViewDelegate) {
            }

            public static void $default$onEmojiSettingsClick(EmojiViewDelegate emojiViewDelegate) {
            }

            public static void $default$onGifSelected(EmojiViewDelegate emojiViewDelegate, View view, Object obj, String str, Object obj2, boolean z, int i) {
            }

            public static void $default$onSearchOpenClose(EmojiViewDelegate emojiViewDelegate, int i) {
            }

            public static void $default$onShowStickerSet(EmojiViewDelegate emojiViewDelegate, TLRPC$StickerSet tLRPC$StickerSet, TLRPC$InputStickerSet tLRPC$InputStickerSet) {
            }

            public static void $default$onStickerSelected(EmojiViewDelegate emojiViewDelegate, View view, TLRPC$Document tLRPC$Document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i) {
            }

            public static void $default$onStickerSetAdd(EmojiViewDelegate emojiViewDelegate, TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
            }

            public static void $default$onStickerSetRemove(EmojiViewDelegate emojiViewDelegate, TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
            }

            public static void $default$onStickersGroupClick(EmojiViewDelegate emojiViewDelegate, long j) {
            }

            public static void $default$onStickersSettingsClick(EmojiViewDelegate emojiViewDelegate) {
            }

            public static void $default$onTabOpened(EmojiViewDelegate emojiViewDelegate, int i) {
            }

            public static void $default$showTrendingStickersAlert(EmojiViewDelegate emojiViewDelegate, TrendingStickersLayout trendingStickersLayout) {
            }
        }

        boolean canSchedule();

        long getDialogId();

        float getProgressToSearchOpened();

        int getThreadId();

        void invalidateEnterView();

        boolean isExpanded();

        boolean isInScheduleMode();

        boolean isSearchOpened();

        boolean isUserSelf();

        void onAnimatedEmojiUnlockClick();

        boolean onBackspace();

        void onClearEmojiRecent();

        void onCustomEmojiSelected(long j, TLRPC$Document tLRPC$Document, String str);

        void onEmojiSelected(String str);

        void onEmojiSettingsClick();

        void onGifSelected(View view, Object obj, String str, Object obj2, boolean z, int i);

        void onSearchOpenClose(int i);

        void onShowStickerSet(TLRPC$StickerSet tLRPC$StickerSet, TLRPC$InputStickerSet tLRPC$InputStickerSet);

        void onStickerSelected(View view, TLRPC$Document tLRPC$Document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i);

        void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered);

        void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered);

        void onStickersGroupClick(long j);

        void onStickersSettingsClick();

        void onTabOpened(int i);

        void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$0() {
    }

    public void setAllow(boolean z, boolean z2, boolean z3) {
        this.currentTabs.clear();
        boolean z4 = false;
        for (int i = 0; i < this.allTabs.size(); i++) {
            if (this.allTabs.get(i).type == 0) {
                this.currentTabs.add(this.allTabs.get(i));
            }
            if (this.allTabs.get(i).type == 1 && z2) {
                this.currentTabs.add(this.allTabs.get(i));
            }
            if (this.allTabs.get(i).type == 2 && z) {
                this.currentTabs.add(this.allTabs.get(i));
            }
        }
        PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
        if (pagerSlidingTabStrip != null) {
            if (this.currentTabs.size() > 1) {
                z4 = true;
            }
            AndroidUtilities.updateViewVisibilityAnimated(pagerSlidingTabStrip, z4, 1.0f, z3);
        }
        ViewPager viewPager = this.pager;
        if (viewPager != null) {
            viewPager.setAdapter((PagerAdapter) null);
            this.pager.setAdapter(this.emojiPagerAdapter);
            PagerSlidingTabStrip pagerSlidingTabStrip2 = this.typeTabs;
            if (pagerSlidingTabStrip2 != null) {
                pagerSlidingTabStrip2.setViewPager(this.pager);
            }
        }
    }

    static {
        Field field = null;
        try {
            field = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            field.setAccessible(true);
        } catch (NoSuchFieldException unused) {
        }
        superListenerField = field;
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        SearchField searchField = this.stickersSearchField;
        if (searchField != null) {
            searchField.searchEditText.setEnabled(z);
        }
        SearchField searchField2 = this.gifSearchField;
        if (searchField2 != null) {
            searchField2.searchEditText.setEnabled(z);
        }
        SearchField searchField3 = this.emojiSearchField;
        if (searchField3 != null) {
            searchField3.searchEditText.setEnabled(z);
        }
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

        public SearchField(Context context, int i) {
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
            this.searchIconImageView.setImageResource(R.drawable.smiles_inputsearch);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 14.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            AnonymousClass1 r3 = new CloseProgressDrawable2(EmojiView.this) {
                /* access modifiers changed from: protected */
                public int getCurrentColor() {
                    return EmojiView.this.getThemedColor("chat_emojiSearchIcon");
                }
            };
            this.progressDrawable = r3;
            imageView3.setImageDrawable(r3);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 14.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new EmojiView$SearchField$$ExternalSyntheticLambda0(this));
            AnonymousClass2 r0 = new EditTextBoldCursor(context, EmojiView.this, i) {
                final /* synthetic */ int val$type;

                {
                    this.val$type = r4;
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (!SearchField.this.searchEditText.isEnabled()) {
                        return super.onTouchEvent(motionEvent);
                    }
                    if (motionEvent.getAction() == 0) {
                        if (!EmojiView.this.delegate.isSearchOpened()) {
                            SearchField searchField = SearchField.this;
                            EmojiView.this.openSearch(searchField);
                        }
                        EmojiViewDelegate access$400 = EmojiView.this.delegate;
                        int i = 1;
                        if (this.val$type == 1) {
                            i = 2;
                        }
                        access$400.onSearchOpenClose(i);
                        SearchField.this.searchEditText.requestFocus();
                        AndroidUtilities.showKeyboard(SearchField.this.searchEditText);
                    }
                    return super.onTouchEvent(motionEvent);
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
            if (i == 0) {
                this.searchEditText.setHint(LocaleController.getString("SearchStickersHint", R.string.SearchStickersHint));
            } else if (i == 1) {
                this.searchEditText.setHint(LocaleController.getString("SearchEmojiHint", R.string.SearchEmojiHint));
            } else if (i == 2) {
                this.searchEditText.setHint(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
            }
            this.searchEditText.setCursorColor(EmojiView.this.getThemedColor("featuredStickers_addedIcon"));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 12.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(EmojiView.this, i) {
                final /* synthetic */ int val$type;

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                {
                    this.val$type = r3;
                }

                public void afterTextChanged(Editable editable) {
                    boolean z = false;
                    boolean z2 = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() != 0.0f) {
                        z = true;
                    }
                    if (z2 != z) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (z2) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(z2 ? 1.0f : 0.1f);
                        if (!z2) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    int i = this.val$type;
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        /* access modifiers changed from: private */
        public void showShadow(boolean z, boolean z2) {
            if (z && this.shadowView.getTag() == null) {
                return;
            }
            if (z || this.shadowView.getTag() == null) {
                AnimatorSet animatorSet = this.shadowAnimator;
                int i = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.shadowAnimator = null;
                }
                View view = this.shadowView;
                if (!z) {
                    i = 1;
                }
                view.setTag(i);
                float f = 1.0f;
                if (z2) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.shadowAnimator = animatorSet2;
                    Animator[] animatorArr = new Animator[1];
                    View view2 = this.shadowView;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    if (!z) {
                        f = 0.0f;
                    }
                    fArr[0] = f;
                    animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                    animatorSet2.playTogether(animatorArr);
                    this.shadowAnimator.setDuration(200);
                    this.shadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.shadowAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            AnimatorSet unused = SearchField.this.shadowAnimator = null;
                        }
                    });
                    this.shadowAnimator.start();
                    return;
                }
                View view3 = this.shadowView;
                if (!z) {
                    f = 0.0f;
                }
                view3.setAlpha(f);
            }
        }
    }

    private class TypedScrollListener extends RecyclerView.OnScrollListener {
        private boolean smoothScrolling;
        private final int type;

        public TypedScrollListener(int i) {
            this.type = i;
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (recyclerView.getLayoutManager().isSmoothScrolling()) {
                this.smoothScrolling = true;
            } else if (i == 0) {
                if (!this.smoothScrolling) {
                    EmojiView.this.animateTabsY(this.type);
                }
                if (EmojiView.this.ignoreStickersScroll) {
                    boolean unused = EmojiView.this.ignoreStickersScroll = false;
                }
                this.smoothScrolling = false;
            } else {
                if (i == 1) {
                    if (EmojiView.this.ignoreStickersScroll) {
                        boolean unused2 = EmojiView.this.ignoreStickersScroll = false;
                    }
                    SearchField access$2200 = EmojiView.this.getSearchFieldForType(this.type);
                    if (access$2200 != null) {
                        access$2200.hideKeyboard();
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

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            EmojiView.this.checkScroll(this.type);
            EmojiView.this.checkTabsY(this.type, i2);
            checkSearchFieldScroll();
            if (!this.smoothScrolling) {
                EmojiView.this.checkBottomTabScroll((float) i2);
            }
        }

        private void checkSearchFieldScroll() {
            int i = this.type;
            if (i == 0) {
                EmojiView.this.checkStickersSearchFieldScroll(false);
            } else if (i == 1) {
                EmojiView.this.checkEmojiSearchFieldScroll(false);
            } else if (i == 2) {
                EmojiView.this.checkGifSearchFieldScroll(false);
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

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (isDragging()) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (motionEvent.getAction() == 0) {
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                this.downX = motionEvent.getRawX();
                this.downY = motionEvent.getRawY();
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null && Math.abs(motionEvent.getRawY() - this.downY) >= ((float) this.touchSlop)) {
                this.draggingVertically = true;
                this.downY = motionEvent.getRawY();
                EmojiView.this.dragListener.onDragStart();
                if (this.startedScroll) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
                return true;
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (isDragging()) {
                return super.onTouchEvent(motionEvent);
            }
            if (this.first) {
                this.first = false;
                this.lastX = motionEvent.getX();
            }
            if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                float unused = EmojiView.this.lastStickersX = motionEvent.getRawX();
            }
            if (motionEvent.getAction() == 0) {
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                this.downX = motionEvent.getRawX();
                this.downY = motionEvent.getRawY();
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null) {
                if (Math.abs(motionEvent.getRawX() - this.downX) >= ((float) this.touchSlop) && canScrollHorizontally((int) (this.downX - motionEvent.getRawX()))) {
                    this.draggingHorizontally = true;
                    AndroidUtilities.cancelRunOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable);
                    boolean unused2 = EmojiView.this.expandStickersByDragg = true;
                    EmojiView.this.updateStickerTabsPosition();
                } else if (Math.abs(motionEvent.getRawY() - this.downY) >= ((float) this.touchSlop)) {
                    this.draggingVertically = true;
                    this.downY = motionEvent.getRawY();
                    EmojiView.this.dragListener.onDragStart();
                    if (this.startedScroll) {
                        EmojiView.this.pager.endFakeDrag();
                        this.startedScroll = false;
                    }
                }
            }
            if (EmojiView.this.expandStickersByDragg && (motionEvent.getAction() == 1 || motionEvent.getAction() == 3)) {
                AndroidUtilities.runOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable, 1500);
            }
            if (this.draggingVertically) {
                if (this.vTracker == null) {
                    this.vTracker = VelocityTracker.obtain();
                }
                this.vTracker.addMovement(motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    this.vTracker.computeCurrentVelocity(1000);
                    float yVelocity = this.vTracker.getYVelocity();
                    this.vTracker.recycle();
                    this.vTracker = null;
                    if (motionEvent.getAction() == 1) {
                        EmojiView.this.dragListener.onDragEnd(yVelocity);
                    } else {
                        EmojiView.this.dragListener.onDragCancel();
                    }
                    this.first = true;
                    this.draggingHorizontally = false;
                    this.draggingVertically = false;
                } else {
                    EmojiView.this.dragListener.onDrag(Math.round(motionEvent.getRawY() - this.downY));
                }
                cancelLongPress();
                return true;
            }
            float translationX = getTranslationX();
            if (getScrollX() == 0 && translationX == 0.0f) {
                if (this.startedScroll || this.lastX - motionEvent.getX() >= 0.0f) {
                    if (this.startedScroll && this.lastX - motionEvent.getX() > 0.0f && EmojiView.this.pager.isFakeDragging()) {
                        EmojiView.this.pager.endFakeDrag();
                        this.startedScroll = false;
                    }
                } else if (EmojiView.this.pager.beginFakeDrag()) {
                    this.startedScroll = true;
                    this.lastTranslateX = getTranslationX();
                }
            }
            if (this.startedScroll) {
                motionEvent.getX();
                try {
                    this.lastTranslateX = translationX;
                } catch (Exception e) {
                    try {
                        EmojiView.this.pager.endFakeDrag();
                    } catch (Exception unused3) {
                    }
                    this.startedScroll = false;
                    FileLog.e((Throwable) e);
                }
            }
            this.lastX = motionEvent.getX();
            if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                this.first = true;
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                if (this.startedScroll) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
            }
            if (this.startedScroll || super.onTouchEvent(motionEvent)) {
                return true;
            }
            return false;
        }
    }

    private class ImageViewEmoji extends ImageView {
        ValueAnimator backAnimator;
        /* access modifiers changed from: private */
        public ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder;
        public AnimatedEmojiDrawable drawable;
        public boolean ignoring;
        /* access modifiers changed from: private */
        public boolean isRecent;
        /* access modifiers changed from: private */
        public EmojiPack pack;
        public int position;
        float pressedProgress;
        /* access modifiers changed from: private */
        public AnimatedEmojiSpan span;

        public ImageViewEmoji(Context context) {
            super(context);
            setScaleType(ImageView.ScaleType.CENTER);
            setBackground(Theme.createRadSelectorDrawable(EmojiView.this.getThemedColor("listSelectorSDK21"), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f)));
        }

        /* access modifiers changed from: private */
        public void sendEmoji(String str) {
            String str2;
            String str3;
            if (getSpan() == null) {
                long unused = EmojiView.this.shownBottomTabAfterClick = SystemClock.elapsedRealtime();
                EmojiView.this.showBottomTab(true, true);
                if (str != null) {
                    str2 = str;
                } else {
                    str2 = (String) getTag();
                }
                new SpannableStringBuilder().append(str2);
                if (str == null) {
                    if (!this.isRecent && (str3 = Emoji.emojiColor.get(str2)) != null) {
                        str2 = EmojiView.addColorToCode(str2, str3);
                    }
                    EmojiView.this.addEmojiToRecent(str2);
                    if (EmojiView.this.delegate != null) {
                        EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(str2));
                    }
                } else if (EmojiView.this.delegate != null) {
                    EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(str));
                }
            } else if (EmojiView.this.delegate != null) {
                long j = getSpan().documentId;
                TLRPC$Document tLRPC$Document = getSpan().document;
                String str4 = null;
                if (tLRPC$Document == null) {
                    for (int i = 0; i < EmojiView.this.emojipacksProcessed.size(); i++) {
                        EmojiPack emojiPack = (EmojiPack) EmojiView.this.emojipacksProcessed.get(i);
                        int i2 = 0;
                        while (true) {
                            ArrayList<TLRPC$Document> arrayList = emojiPack.documents;
                            if (arrayList == null || i2 >= arrayList.size()) {
                                break;
                            } else if (emojiPack.documents.get(i2).id == j) {
                                tLRPC$Document = emojiPack.documents.get(i2);
                                break;
                            } else {
                                i2++;
                            }
                        }
                    }
                }
                if (tLRPC$Document == null) {
                    tLRPC$Document = AnimatedEmojiDrawable.findDocument(EmojiView.this.currentAccount, j);
                }
                if (tLRPC$Document != null) {
                    str4 = MessageObject.findAnimatedEmojiEmoticon(tLRPC$Document);
                }
                if (MessageObject.isFreeEmoji(tLRPC$Document) || UserConfig.getInstance(EmojiView.this.currentAccount).isPremium() || (EmojiView.this.delegate != null && EmojiView.this.delegate.isUserSelf())) {
                    long unused2 = EmojiView.this.shownBottomTabAfterClick = SystemClock.elapsedRealtime();
                    EmojiView.this.showBottomTab(true, true);
                    EmojiView emojiView = EmojiView.this;
                    emojiView.addEmojiToRecent("animated_" + j);
                    EmojiView.this.delegate.onCustomEmojiSelected(j, tLRPC$Document, str4);
                    return;
                }
                EmojiView.this.showBottomTab(false, true);
                BulletinFactory of = EmojiView.this.fragment != null ? BulletinFactory.of(EmojiView.this.fragment) : BulletinFactory.of(EmojiView.this.bulletinContainer, EmojiView.this.resourcesProvider);
                if (EmojiView.this.premiumBulletin || EmojiView.this.fragment == null) {
                    of.createEmojiBulletin(tLRPC$Document, AndroidUtilities.replaceTags(LocaleController.getString("UnlockPremiumEmojiHint", R.string.UnlockPremiumEmojiHint)), LocaleController.getString("PremiumMore", R.string.PremiumMore), new EmojiView$ImageViewEmoji$$ExternalSyntheticLambda2(EmojiView.this)).show();
                } else {
                    of.createSimpleBulletin(R.raw.saved_messages, AndroidUtilities.replaceTags(LocaleController.getString("UnlockPremiumEmojiHint2", R.string.UnlockPremiumEmojiHint2)), LocaleController.getString("Open", R.string.Open), new EmojiView$ImageViewEmoji$$ExternalSyntheticLambda1(this)).show();
                }
                EmojiView emojiView2 = EmojiView.this;
                boolean unused3 = emojiView2.premiumBulletin = !emojiView2.premiumBulletin;
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$sendEmoji$1() {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", UserConfig.getInstance(EmojiView.this.currentAccount).getClientUserId());
            EmojiView.this.fragment.presentFragment(new ChatActivity(this, bundle) {
                public void onTransitionAnimationEnd(boolean z, boolean z2) {
                    ChatActivityEnterView chatActivityEnterView;
                    super.onTransitionAnimationEnd(z, z2);
                    if (z && (chatActivityEnterView = this.chatActivityEnterView) != null) {
                        chatActivityEnterView.showEmojiView();
                        this.chatActivityEnterView.postDelayed(new EmojiView$ImageViewEmoji$1$$ExternalSyntheticLambda0(this), 100);
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onTransitionAnimationEnd$0() {
                    if (this.chatActivityEnterView.getEmojiView() != null) {
                        this.chatActivityEnterView.getEmojiView().scrollEmojisToAnimated();
                    }
                }
            });
        }

        public void setImageDrawable(Drawable drawable2, boolean z) {
            super.setImageDrawable(drawable2);
            this.isRecent = z;
        }

        public void setSpan(AnimatedEmojiSpan animatedEmojiSpan) {
            this.span = animatedEmojiSpan;
        }

        public AnimatedEmojiSpan getSpan() {
            return this.span;
        }

        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i));
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName("android.view.View");
        }

        public void setPressed(boolean z) {
            ValueAnimator valueAnimator;
            if (isPressed() != z) {
                super.setPressed(z);
                invalidate();
                if (z && (valueAnimator = this.backAnimator) != null) {
                    valueAnimator.removeAllListeners();
                    this.backAnimator.cancel();
                }
                if (!z) {
                    float f = this.pressedProgress;
                    if (f != 0.0f) {
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{f, 0.0f});
                        this.backAnimator = ofFloat;
                        ofFloat.addUpdateListener(new EmojiView$ImageViewEmoji$$ExternalSyntheticLambda0(this));
                        this.backAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                super.onAnimationEnd(animator);
                                ImageViewEmoji.this.backAnimator = null;
                            }
                        });
                        this.backAnimator.setInterpolator(new OvershootInterpolator(5.0f));
                        this.backAnimator.setDuration(350);
                        this.backAnimator.start();
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setPressed$2(ValueAnimator valueAnimator) {
            this.pressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (isPressed()) {
                float f = this.pressedProgress;
                if (f != 1.0f) {
                    float f2 = f + 0.16f;
                    this.pressedProgress = f2;
                    this.pressedProgress = Utilities.clamp(f2, 1.0f, 0.0f);
                    invalidate();
                }
            }
            float f3 = ((1.0f - this.pressedProgress) * 0.2f) + 0.8f;
            canvas.save();
            canvas.scale(f3, f3, ((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f);
            super.onDraw(canvas);
            canvas.restore();
        }
    }

    private class EmojiPopupWindow extends PopupWindow {
        private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
        private ViewTreeObserver mViewTreeObserver;

        public EmojiPopupWindow(EmojiView emojiView, View view, int i, int i2) {
            super(view, i, i2);
            init();
        }

        private void init() {
            if (EmojiView.superListenerField != null) {
                try {
                    this.mSuperScrollListener = (ViewTreeObserver.OnScrollChangedListener) EmojiView.superListenerField.get(this);
                    EmojiView.superListenerField.set(this, EmojiView.NOP);
                } catch (Exception unused) {
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

        private void registerListener(View view) {
            if (this.mSuperScrollListener != null) {
                ViewTreeObserver viewTreeObserver = view.getWindowToken() != null ? view.getViewTreeObserver() : null;
                ViewTreeObserver viewTreeObserver2 = this.mViewTreeObserver;
                if (viewTreeObserver != viewTreeObserver2) {
                    if (viewTreeObserver2 != null && viewTreeObserver2.isAlive()) {
                        this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                    }
                    this.mViewTreeObserver = viewTreeObserver;
                    if (viewTreeObserver != null) {
                        viewTreeObserver.addOnScrollChangedListener(this.mSuperScrollListener);
                    }
                }
            }
        }

        public void showAsDropDown(View view, int i, int i2) {
            try {
                super.showAsDropDown(view, i, i2);
                registerListener(view);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void update(View view, int i, int i2, int i3, int i4) {
            super.update(view, i, i2, i3, i4);
            registerListener(view);
        }

        public void update(View view, int i, int i2) {
            super.update(view, i, i2);
            registerListener(view);
        }

        public void showAtLocation(View view, int i, int i2, int i3) {
            super.showAtLocation(view, i, i2, i3);
            unregisterListener();
        }

        public void dismiss() {
            setFocusable(false);
            try {
                super.dismiss();
            } catch (Exception unused) {
            }
            unregisterListener();
        }
    }

    private class EmojiColorPickerView extends View {
        /* access modifiers changed from: private */
        public Drawable arrowDrawable = getResources().getDrawable(R.drawable.stickers_back_arrow);
        private int arrowX;
        /* access modifiers changed from: private */
        public Drawable backgroundDrawable = getResources().getDrawable(R.drawable.stickers_back_all);
        private String currentEmoji;
        private Drawable[] drawables = new Drawable[6];
        private RectF rect = new RectF();
        private Paint rectPaint = new Paint(1);
        private int selection;
        private AnimatedFloat selectionAnimated = new AnimatedFloat((View) this, 125, (TimeInterpolator) CubicBezierInterpolator.EASE_OUT_QUINT);

        public void setEmoji(String str, int i) {
            String str2;
            this.currentEmoji = str;
            this.arrowX = i;
            int i2 = 0;
            while (i2 < this.drawables.length) {
                if (i2 != 0) {
                    str2 = EmojiView.addColorToCode(str, i2 != 1 ? i2 != 2 ? i2 != 3 ? i2 != 4 ? i2 != 5 ? "" : "🏿" : "🏾" : "🏽" : "🏼" : "🏻");
                } else {
                    str2 = str;
                }
                this.drawables[i2] = Emoji.getEmojiBigDrawable(str2);
                i2++;
            }
            invalidate();
        }

        public void setSelection(int i) {
            if (this.selection != i) {
                this.selection = i;
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
            float f2 = this.selectionAnimated.set((float) this.selection);
            float access$4700 = (((float) EmojiView.this.emojiSize) * f2) + ((float) AndroidUtilities.dp((f2 * 4.0f) + 5.0f));
            float dp4 = (float) AndroidUtilities.dp(9.0f);
            this.rect.set(access$4700, dp4 - ((float) ((int) AndroidUtilities.dpf2(3.5f))), ((float) EmojiView.this.emojiSize) + access$4700, ((float) EmojiView.this.emojiSize) + dp4 + ((float) AndroidUtilities.dp(3.0f)));
            this.rectPaint.setColor(EmojiView.this.getThemedColor("listSelectorSDK21"));
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.rectPaint);
            if (this.currentEmoji != null) {
                for (int i = 0; i < 6; i++) {
                    Drawable drawable2 = this.drawables[i];
                    if (drawable2 != null) {
                        float access$47002 = (float) ((EmojiView.this.emojiSize * i) + AndroidUtilities.dp((float) ((i * 4) + 5)));
                        float min = ((1.0f - (Math.min(0.5f, Math.abs(((float) i) - f2)) * 2.0f)) * 0.1f) + 0.9f;
                        canvas.save();
                        canvas.scale(min, min, (((float) EmojiView.this.emojiSize) / 2.0f) + access$47002, (((float) EmojiView.this.emojiSize) / 2.0f) + dp4);
                        int i2 = (int) access$47002;
                        int i3 = (int) dp4;
                        drawable2.setBounds(i2, i3, EmojiView.this.emojiSize + i2, EmojiView.this.emojiSize + i3);
                        drawable2.draw(canvas);
                        canvas.restore();
                    }
                }
            }
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public EmojiView(org.telegram.ui.ActionBar.BaseFragment r33, boolean r34, boolean r35, boolean r36, android.content.Context r37, boolean r38, org.telegram.tgnet.TLRPC$ChatFull r39, android.view.ViewGroup r40, org.telegram.ui.ActionBar.Theme.ResourcesProvider r41) {
        /*
            r32 = this;
            r7 = r32
            r0 = r33
            r4 = r34
            r8 = r35
            r9 = r36
            r10 = r37
            r11 = r38
            r12 = r40
            r13 = r41
            r7.<init>(r10)
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r7.allTabs = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r7.currentTabs = r1
            r14 = 1
            r7.firstEmojiAttach = r14
            org.telegram.ui.Components.EmojiView$GifSearchPreloader r1 = new org.telegram.ui.Components.EmojiView$GifSearchPreloader
            r15 = 0
            r1.<init>()
            r7.gifSearchPreloader = r1
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r7.gifCache = r1
            r7.firstGifAttach = r14
            r6 = -2
            r7.gifRecentTabNum = r6
            r7.gifTrendingTabNum = r6
            r7.gifFirstEmojiTabNum = r6
            r7.firstStickersAttach = r14
            r5 = 3
            int[] r1 = new int[r5]
            r7.tabsMinusDy = r1
            android.animation.ObjectAnimator[] r1 = new android.animation.ObjectAnimator[r5]
            r7.tabsYAnimators = r1
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            r7.currentAccount = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r7.stickerSets = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r7.recentGifs = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r7.recentStickers = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r7.favouriteStickers = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r7.premiumStickers = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r7.featuredStickerSets = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r7.featuredEmojiSets = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r7.expandedEmojiSets = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r7.installedEmojiSets = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r7.emojipacksProcessed = r1
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r7.toInstall = r1
            r1 = 10
            org.telegram.tgnet.TLRPC$StickerSetCovered[] r1 = new org.telegram.tgnet.TLRPC$StickerSetCovered[r1]
            r7.primaryInstallingStickerSets = r1
            android.util.LongSparseArray r1 = new android.util.LongSparseArray
            r1.<init>()
            r7.installingStickerSets = r1
            android.util.LongSparseArray r1 = new android.util.LongSparseArray
            r1.<init>()
            r7.removingStickerSets = r1
            r3 = 2
            int[] r1 = new int[r3]
            r7.location = r1
            r7.recentTabNum = r6
            r7.favTabNum = r6
            r7.trendingTabNum = r6
            r7.premiumTabNum = r6
            r2 = -1
            r7.currentBackgroundType = r2
            org.telegram.ui.Components.EmojiView$1 r1 = new org.telegram.ui.Components.EmojiView$1
            r1.<init>()
            r7.checkExpandStickerTabsRunnable = r1
            org.telegram.ui.Components.EmojiView$2 r1 = new org.telegram.ui.Components.EmojiView$2
            r1.<init>()
            r7.contentPreviewViewerDelegate = r1
            r7.premiumBulletin = r14
            r7.animateExpandFromPosition = r2
            r7.animateExpandToPosition = r2
            r2 = -1
            r7.animateExpandStartTime = r2
            r3 = 0
            r7.emojiPackAlertOpened = r3
            android.graphics.Rect r1 = new android.graphics.Rect
            r1.<init>()
            r7.rect = r1
            r7.fragment = r0
            r7.allowAnimatedEmoji = r4
            r7.resourcesProvider = r13
            java.lang.String r1 = "chat_emojiBottomPanelIcon"
            int r1 = r7.getThemedColor(r1)
            int r2 = android.graphics.Color.red(r1)
            int r6 = android.graphics.Color.green(r1)
            int r1 = android.graphics.Color.blue(r1)
            r15 = 30
            int r15 = android.graphics.Color.argb(r15, r2, r6, r1)
            r1 = 1115684864(0x42800000, float:64.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r7.searchFieldHeight = r1
            r7.needEmojiSearch = r11
            android.graphics.drawable.Drawable[] r1 = new android.graphics.drawable.Drawable[r5]
            int r2 = org.telegram.messenger.R.drawable.smiles_tab_smiles
            java.lang.String r6 = "chat_emojiPanelBackspace"
            int r5 = r7.getThemedColor(r6)
            java.lang.String r14 = "chat_emojiPanelIconSelected"
            r19 = r15
            int r15 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r10, r2, r5, r15)
            r1[r3] = r2
            int r2 = org.telegram.messenger.R.drawable.smiles_tab_gif
            int r5 = r7.getThemedColor(r6)
            int r15 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r10, r2, r5, r15)
            r5 = 1
            r1[r5] = r2
            int r2 = org.telegram.messenger.R.drawable.smiles_tab_stickers
            int r5 = r7.getThemedColor(r6)
            int r15 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r10, r2, r5, r15)
            r5 = 2
            r1[r5] = r2
            r7.tabIcons = r1
            r15 = 4
            android.graphics.drawable.Drawable[] r1 = new android.graphics.drawable.Drawable[r15]
            int r2 = org.telegram.messenger.R.drawable.msg_emoji_recent
            java.lang.String r5 = "chat_emojiPanelIcon"
            int r15 = r7.getThemedColor(r5)
            r20 = r6
            int r6 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r10, r2, r15, r6)
            r1[r3] = r6
            int r6 = org.telegram.messenger.R.drawable.emoji_tabs_faves
            int r15 = r7.getThemedColor(r5)
            int r3 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r10, r6, r15, r3)
            r6 = 1
            r1[r6] = r3
            int r3 = org.telegram.messenger.R.drawable.emoji_tabs_new3
            int r6 = r7.getThemedColor(r5)
            int r15 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r10, r3, r6, r15)
            r6 = 2
            r1[r6] = r3
            android.graphics.drawable.LayerDrawable r3 = new android.graphics.drawable.LayerDrawable
            android.graphics.drawable.Drawable[] r15 = new android.graphics.drawable.Drawable[r6]
            int r6 = org.telegram.messenger.R.drawable.emoji_tabs_new1
            int r12 = r7.getThemedColor(r5)
            int r13 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r10, r6, r12, r13)
            r7.searchIconDrawable = r6
            r12 = 0
            r15[r12] = r6
            int r6 = org.telegram.messenger.R.drawable.emoji_tabs_new2
            java.lang.String r12 = "chat_emojiPanelStickerPackSelectorLine"
            int r13 = r7.getThemedColor(r12)
            int r9 = r7.getThemedColor(r12)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r10, r6, r13, r9)
            r7.searchIconDotDrawable = r6
            r9 = 1
            r15[r9] = r6
            r3.<init>(r15)
            r6 = 3
            r1[r6] = r3
            r7.stickerIcons = r1
            r1 = 2
            android.graphics.drawable.Drawable[] r3 = new android.graphics.drawable.Drawable[r1]
            int r1 = r7.getThemedColor(r5)
            int r6 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r10, r2, r1, r6)
            r2 = 0
            r3[r2] = r1
            int r1 = org.telegram.messenger.R.drawable.stickers_gifs_trending
            int r2 = r7.getThemedColor(r5)
            int r5 = r7.getThemedColor(r14)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r10, r1, r2, r5)
            r2 = 1
            r3[r2] = r1
            r7.gifIcons = r3
            r9 = 8
            java.lang.String[] r1 = new java.lang.String[r9]
            int r3 = org.telegram.messenger.R.string.Emoji1
            java.lang.String r5 = "Emoji1"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r5 = 0
            r1[r5] = r3
            int r3 = org.telegram.messenger.R.string.Emoji2
            java.lang.String r5 = "Emoji2"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r1[r2] = r3
            int r2 = org.telegram.messenger.R.string.Emoji3
            java.lang.String r3 = "Emoji3"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2
            r1[r3] = r2
            int r2 = org.telegram.messenger.R.string.Emoji4
            java.lang.String r5 = "Emoji4"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r5 = 3
            r1[r5] = r2
            int r2 = org.telegram.messenger.R.string.Emoji5
            java.lang.String r6 = "Emoji5"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r6 = 4
            r1[r6] = r2
            int r2 = org.telegram.messenger.R.string.Emoji6
            java.lang.String r6 = "Emoji6"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r13 = 5
            r1[r13] = r2
            int r2 = org.telegram.messenger.R.string.Emoji7
            java.lang.String r6 = "Emoji7"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r6 = 6
            r1[r6] = r2
            int r2 = org.telegram.messenger.R.string.Emoji8
            java.lang.String r6 = "Emoji8"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r6 = 7
            r1[r6] = r2
            r7.emojiTitles = r1
            r1 = r39
            r7.info = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r2 = 1
            r1.<init>(r2)
            r7.dotPaint = r1
            java.lang.String r2 = "chat_emojiPanelNewTrending"
            int r2 = r7.getThemedColor(r2)
            r1.setColor(r2)
            int r14 = android.os.Build.VERSION.SDK_INT
            r15 = 21
            if (r14 < r15) goto L_0x025a
            org.telegram.ui.Components.EmojiView$3 r1 = new org.telegram.ui.Components.EmojiView$3
            r1.<init>(r7)
            r7.outlineProvider = r1
        L_0x025a:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r10)
            r7.emojiContainer = r1
            org.telegram.ui.Components.EmojiView$Tab r1 = new org.telegram.ui.Components.EmojiView$Tab
            r2 = 0
            r1.<init>()
            r2 = 0
            r1.type = r2
            android.widget.FrameLayout r2 = r7.emojiContainer
            r1.view = r2
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$Tab> r2 = r7.allTabs
            r2.add(r1)
            if (r4 == 0) goto L_0x0287
            int r1 = r7.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r1.checkStickers(r13)
            int r1 = r7.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r1.checkFeaturedEmoji()
        L_0x0287:
            org.telegram.ui.Components.EmojiView$EmojiGridView r1 = new org.telegram.ui.Components.EmojiView$EmojiGridView
            r1.<init>(r10)
            r7.emojiGridView = r1
            androidx.recyclerview.widget.DefaultItemAnimator r1 = new androidx.recyclerview.widget.DefaultItemAnimator
            r1.<init>()
            r3 = 0
            r1.setAddDelay(r3)
            r2 = 220(0xdc, double:1.087E-321)
            r1.setAddDuration(r2)
            r1.setMoveDuration(r2)
            r2 = 160(0xa0, double:7.9E-322)
            r1.setChangeDuration(r2)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            r1.setMoveInterpolator(r2)
            org.telegram.ui.Components.EmojiView$EmojiGridView r2 = r7.emojiGridView
            r2.setItemAnimator(r1)
            org.telegram.ui.Components.EmojiView$EmojiGridView r1 = r7.emojiGridView
            org.telegram.ui.Components.EmojiView$4 r2 = new org.telegram.ui.Components.EmojiView$4
            r2.<init>()
            r1.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r2)
            org.telegram.ui.Components.EmojiView$EmojiGridView r1 = r7.emojiGridView
            r2 = 1
            r1.setInstantClick(r2)
            org.telegram.ui.Components.EmojiView$EmojiGridView r1 = r7.emojiGridView
            org.telegram.ui.Components.EmojiView$5 r2 = new org.telegram.ui.Components.EmojiView$5
            r2.<init>(r10, r9)
            r7.emojiLayoutManager = r2
            r1.setLayoutManager(r2)
            org.telegram.ui.Components.EmojiView$EmojiGridView r1 = r7.emojiGridView
            r2 = 1108869120(0x42180000, float:38.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setTopGlowOffset(r2)
            org.telegram.ui.Components.EmojiView$EmojiGridView r1 = r7.emojiGridView
            r6 = 1108344832(0x42100000, float:36.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.setBottomGlowOffset(r2)
            org.telegram.ui.Components.EmojiView$EmojiGridView r1 = r7.emojiGridView
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r18 = 1110441984(0x42300000, float:44.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.setPadding(r2, r3, r4, r5)
            org.telegram.ui.Components.EmojiView$EmojiGridView r1 = r7.emojiGridView
            java.lang.String r5 = "chat_emojiPanelBackground"
            int r2 = r7.getThemedColor(r5)
            r1.setGlowColor(r2)
            org.telegram.ui.Components.EmojiView$EmojiGridView r1 = r7.emojiGridView
            r3 = 0
            r1.setSelectorDrawableColor(r3)
            org.telegram.ui.Components.EmojiView$EmojiGridView r1 = r7.emojiGridView
            r1.setClipToPadding(r3)
            androidx.recyclerview.widget.GridLayoutManager r1 = r7.emojiLayoutManager
            org.telegram.ui.Components.EmojiView$6 r2 = new org.telegram.ui.Components.EmojiView$6
            r2.<init>()
            r1.setSpanSizeLookup(r2)
            org.telegram.ui.Components.EmojiView$EmojiGridView r1 = r7.emojiGridView
            org.telegram.ui.Components.EmojiView$EmojiGridAdapter r2 = new org.telegram.ui.Components.EmojiView$EmojiGridAdapter
            r4 = 0
            r2.<init>()
            r7.emojiAdapter = r2
            r1.setAdapter(r2)
            org.telegram.ui.Components.EmojiView$EmojiGridView r1 = r7.emojiGridView
            org.telegram.ui.Components.EmojiView$EmojiGridSpacing r2 = new org.telegram.ui.Components.EmojiView$EmojiGridSpacing
            r2.<init>()
            r1.addItemDecoration(r2)
            org.telegram.ui.Components.EmojiView$EmojiSearchAdapter r1 = new org.telegram.ui.Components.EmojiView$EmojiSearchAdapter
            r1.<init>()
            r7.emojiSearchAdapter = r1
            android.widget.FrameLayout r1 = r7.emojiContainer
            org.telegram.ui.Components.EmojiView$EmojiGridView r2 = r7.emojiGridView
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4)
            r1.addView(r2, r4)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper r1 = new org.telegram.ui.Components.RecyclerAnimationScrollHelper
            org.telegram.ui.Components.EmojiView$EmojiGridView r2 = r7.emojiGridView
            androidx.recyclerview.widget.GridLayoutManager r4 = r7.emojiLayoutManager
            r1.<init>(r2, r4)
            r7.emojiScrollHelper = r1
            org.telegram.ui.Components.EmojiView$7 r2 = new org.telegram.ui.Components.EmojiView$7
            r2.<init>()
            r1.setAnimationCallback(r2)
            org.telegram.ui.Components.EmojiView$EmojiGridView r1 = r7.emojiGridView
            org.telegram.ui.Components.EmojiView$8 r2 = new org.telegram.ui.Components.EmojiView$8
            r4 = 1
            r2.<init>(r4)
            r1.setOnScrollListener(r2)
            org.telegram.ui.Components.EmojiView$9 r4 = new org.telegram.ui.Components.EmojiView$9
            if (r0 == 0) goto L_0x0372
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda6 r0 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda6
            r0.<init>(r7)
            r17 = r0
            goto L_0x0374
        L_0x0372:
            r17 = 0
        L_0x0374:
            r0 = r4
            r1 = r32
            r2 = r37
            r9 = 2
            r13 = 0
            r15 = -1
            r3 = r41
            r9 = r4
            r4 = r34
            r13 = r5
            r21 = 3
            r5 = r32
            r23 = r20
            r6 = r17
            r0.<init>(r2, r3, r4, r5, r6)
            r7.emojiTabs = r9
            if (r11 == 0) goto L_0x03b8
            org.telegram.ui.Components.EmojiView$SearchField r0 = new org.telegram.ui.Components.EmojiView$SearchField
            r1 = 1
            r0.<init>(r10, r1)
            r7.emojiSearchField = r0
            android.widget.FrameLayout r1 = r7.emojiContainer
            android.widget.FrameLayout$LayoutParams r2 = new android.widget.FrameLayout$LayoutParams
            int r3 = r7.searchFieldHeight
            int r4 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r3 = r3 + r4
            r2.<init>(r15, r3)
            r1.addView(r0, r2)
            org.telegram.ui.Components.EmojiView$SearchField r0 = r7.emojiSearchField
            org.telegram.ui.Components.EditTextBoldCursor r0 = r0.searchEditText
            org.telegram.ui.Components.EmojiView$10 r1 = new org.telegram.ui.Components.EmojiView$10
            r1.<init>()
            r0.setOnFocusChangeListener(r1)
        L_0x03b8:
            org.telegram.ui.Components.EmojiTabsStrip r0 = r7.emojiTabs
            int r1 = r7.getThemedColor(r13)
            r0.setBackgroundColor(r1)
            org.telegram.ui.Components.EmojiView$EmojiGridAdapter r0 = r7.emojiAdapter
            r0.processEmoji()
            org.telegram.ui.Components.EmojiTabsStrip r0 = r7.emojiTabs
            if (r0 == 0) goto L_0x03cd
            r0.updateEmojiPacks()
        L_0x03cd:
            android.widget.FrameLayout r0 = r7.emojiContainer
            org.telegram.ui.Components.EmojiTabsStrip r1 = r7.emojiTabs
            r2 = 1108344832(0x42100000, float:36.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r2)
            r0.addView(r1, r3)
            android.view.View r0 = new android.view.View
            r0.<init>(r10)
            r7.emojiTabsShadow = r0
            r1 = 0
            r0.setAlpha(r1)
            android.view.View r0 = r7.emojiTabsShadow
            r1 = 1
            java.lang.Integer r3 = java.lang.Integer.valueOf(r1)
            r0.setTag(r3)
            android.view.View r0 = r7.emojiTabsShadow
            java.lang.String r1 = "chat_emojiPanelShadowLine"
            int r3 = r7.getThemedColor(r1)
            r0.setBackgroundColor(r3)
            android.widget.FrameLayout$LayoutParams r0 = new android.widget.FrameLayout$LayoutParams
            int r3 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r4 = 51
            r0.<init>(r15, r3, r4)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.topMargin = r3
            android.widget.FrameLayout r3 = r7.emojiContainer
            android.view.View r5 = r7.emojiTabsShadow
            r3.addView(r5, r0)
            r0 = 1109393408(0x42200000, float:40.0)
            r3 = r36
            if (r8 == 0) goto L_0x0663
            if (r3 == 0) goto L_0x0529
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r10)
            r7.gifContainer = r5
            org.telegram.ui.Components.EmojiView$Tab r5 = new org.telegram.ui.Components.EmojiView$Tab
            r6 = 0
            r5.<init>()
            r6 = 1
            r5.type = r6
            android.widget.FrameLayout r6 = r7.gifContainer
            r5.view = r6
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$Tab> r6 = r7.allTabs
            r6.add(r5)
            org.telegram.ui.Components.EmojiView$11 r5 = new org.telegram.ui.Components.EmojiView$11
            r5.<init>(r10)
            r7.gifGridView = r5
            r6 = 0
            r5.setClipToPadding(r6)
            org.telegram.ui.Components.RecyclerListView r5 = r7.gifGridView
            org.telegram.ui.Components.EmojiView$GifLayoutManager r6 = new org.telegram.ui.Components.EmojiView$GifLayoutManager
            r6.<init>(r10)
            r7.gifLayoutManager = r6
            r5.setLayoutManager(r6)
            org.telegram.ui.Components.RecyclerListView r5 = r7.gifGridView
            org.telegram.ui.Components.EmojiView$12 r6 = new org.telegram.ui.Components.EmojiView$12
            r6.<init>()
            r5.addItemDecoration(r6)
            org.telegram.ui.Components.RecyclerListView r5 = r7.gifGridView
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r9 = 1110441984(0x42300000, float:44.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r2 = 0
            r5.setPadding(r2, r6, r2, r9)
            org.telegram.ui.Components.RecyclerListView r5 = r7.gifGridView
            r6 = 2
            r5.setOverScrollMode(r6)
            org.telegram.ui.Components.RecyclerListView r5 = r7.gifGridView
            androidx.recyclerview.widget.RecyclerView$ItemAnimator r5 = r5.getItemAnimator()
            androidx.recyclerview.widget.SimpleItemAnimator r5 = (androidx.recyclerview.widget.SimpleItemAnimator) r5
            r5.setSupportsChangeAnimations(r2)
            org.telegram.ui.Components.RecyclerListView r2 = r7.gifGridView
            org.telegram.ui.Components.EmojiView$GifAdapter r5 = new org.telegram.ui.Components.EmojiView$GifAdapter
            r6 = 1
            r5.<init>(r7, r10, r6)
            r7.gifAdapter = r5
            r2.setAdapter(r5)
            org.telegram.ui.Components.EmojiView$GifAdapter r2 = new org.telegram.ui.Components.EmojiView$GifAdapter
            r2.<init>(r7, r10)
            r7.gifSearchAdapter = r2
            org.telegram.ui.Components.RecyclerListView r2 = r7.gifGridView
            org.telegram.ui.Components.EmojiView$TypedScrollListener r5 = new org.telegram.ui.Components.EmojiView$TypedScrollListener
            r6 = 2
            r5.<init>(r6)
            r2.setOnScrollListener(r5)
            org.telegram.ui.Components.RecyclerListView r2 = r7.gifGridView
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda3 r5 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda3
            r6 = r41
            r5.<init>(r7, r6)
            r2.setOnTouchListener(r5)
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda9 r2 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda9
            r2.<init>(r7)
            r7.gifOnItemClickListener = r2
            org.telegram.ui.Components.RecyclerListView r5 = r7.gifGridView
            r5.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r2)
            android.widget.FrameLayout r2 = r7.gifContainer
            org.telegram.ui.Components.RecyclerListView r5 = r7.gifGridView
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r9)
            r2.addView(r5, r9)
            org.telegram.ui.Components.EmojiView$SearchField r2 = new org.telegram.ui.Components.EmojiView$SearchField
            r5 = 2
            r2.<init>(r10, r5)
            r7.gifSearchField = r2
            r5 = 4
            r2.setVisibility(r5)
            android.widget.FrameLayout r2 = r7.gifContainer
            org.telegram.ui.Components.EmojiView$SearchField r5 = r7.gifSearchField
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            int r0 = r7.searchFieldHeight
            int r17 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r0 = r0 + r17
            r9.<init>(r15, r0)
            r2.addView(r5, r9)
            org.telegram.ui.Components.EmojiView$DraggableScrollSlidingTabStrip r0 = new org.telegram.ui.Components.EmojiView$DraggableScrollSlidingTabStrip
            r0.<init>(r10, r6)
            r7.gifTabs = r0
            org.telegram.ui.Components.ScrollSlidingTabStrip$Type r2 = org.telegram.ui.Components.ScrollSlidingTabStrip.Type.TAB
            r0.setType(r2)
            org.telegram.ui.Components.ScrollSlidingTabStrip r0 = r7.gifTabs
            int r2 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r0.setUnderlineHeight(r2)
            org.telegram.ui.Components.ScrollSlidingTabStrip r0 = r7.gifTabs
            int r2 = r7.getThemedColor(r12)
            r0.setIndicatorColor(r2)
            org.telegram.ui.Components.ScrollSlidingTabStrip r0 = r7.gifTabs
            int r2 = r7.getThemedColor(r1)
            r0.setUnderlineColor(r2)
            org.telegram.ui.Components.ScrollSlidingTabStrip r0 = r7.gifTabs
            int r2 = r7.getThemedColor(r13)
            r0.setBackgroundColor(r2)
            android.widget.FrameLayout r0 = r7.gifContainer
            org.telegram.ui.Components.ScrollSlidingTabStrip r2 = r7.gifTabs
            r5 = 36
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r15, (int) r5, (int) r4)
            r0.addView(r2, r5)
            r32.updateGifTabs()
            org.telegram.ui.Components.ScrollSlidingTabStrip r0 = r7.gifTabs
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda10 r2 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda10
            r2.<init>(r7)
            r0.setDelegate(r2)
            org.telegram.ui.Components.EmojiView$GifAdapter r0 = r7.gifAdapter
            r0.loadTrendingGifs()
            goto L_0x052b
        L_0x0529:
            r6 = r41
        L_0x052b:
            org.telegram.ui.Components.EmojiView$13 r0 = new org.telegram.ui.Components.EmojiView$13
            r0.<init>(r10)
            r7.stickersContainer = r0
            int r0 = r7.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            r2 = 0
            r0.checkStickers(r2)
            int r0 = r7.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            r0.checkFeaturedStickers()
            org.telegram.ui.Components.EmojiView$14 r0 = new org.telegram.ui.Components.EmojiView$14
            r0.<init>(r10)
            r7.stickersGridView = r0
            org.telegram.ui.Components.EmojiView$15 r2 = new org.telegram.ui.Components.EmojiView$15
            r5 = 5
            r2.<init>(r10, r5)
            r7.stickersLayoutManager = r2
            r0.setLayoutManager(r2)
            androidx.recyclerview.widget.GridLayoutManager r0 = r7.stickersLayoutManager
            org.telegram.ui.Components.EmojiView$16 r2 = new org.telegram.ui.Components.EmojiView$16
            r2.<init>()
            r0.setSpanSizeLookup(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r7.stickersGridView
            r2 = 1109393408(0x42200000, float:40.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r2 = 1110441984(0x42300000, float:44.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r5 = 0
            r0.setPadding(r5, r9, r5, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r7.stickersGridView
            r0.setClipToPadding(r5)
            org.telegram.ui.Components.EmojiView$Tab r0 = new org.telegram.ui.Components.EmojiView$Tab
            r2 = 0
            r0.<init>()
            r2 = 2
            r0.type = r2
            android.widget.FrameLayout r2 = r7.stickersContainer
            r0.view = r2
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$Tab> r2 = r7.allTabs
            r2.add(r0)
            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter
            r0.<init>(r10)
            r7.stickersSearchGridAdapter = r0
            org.telegram.ui.Components.RecyclerListView r0 = r7.stickersGridView
            org.telegram.ui.Components.EmojiView$StickersGridAdapter r2 = new org.telegram.ui.Components.EmojiView$StickersGridAdapter
            r2.<init>(r10)
            r7.stickersGridAdapter = r2
            r0.setAdapter(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r7.stickersGridView
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda2
            r2.<init>(r7, r6)
            r0.setOnTouchListener(r2)
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda8 r0 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda8
            r0.<init>(r7)
            r7.stickersOnItemClickListener = r0
            org.telegram.ui.Components.RecyclerListView r2 = r7.stickersGridView
            r2.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r0)
            org.telegram.ui.Components.RecyclerListView r0 = r7.stickersGridView
            int r2 = r7.getThemedColor(r13)
            r0.setGlowColor(r2)
            android.widget.FrameLayout r0 = r7.stickersContainer
            org.telegram.ui.Components.RecyclerListView r2 = r7.stickersGridView
            r0.addView(r2)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper r0 = new org.telegram.ui.Components.RecyclerAnimationScrollHelper
            org.telegram.ui.Components.RecyclerListView r2 = r7.stickersGridView
            androidx.recyclerview.widget.GridLayoutManager r5 = r7.stickersLayoutManager
            r0.<init>(r2, r5)
            r7.stickersScrollHelper = r0
            org.telegram.ui.Components.EmojiView$SearchField r0 = new org.telegram.ui.Components.EmojiView$SearchField
            r2 = 0
            r0.<init>(r10, r2)
            r7.stickersSearchField = r0
            android.widget.FrameLayout r2 = r7.stickersContainer
            android.widget.FrameLayout$LayoutParams r5 = new android.widget.FrameLayout$LayoutParams
            int r9 = r7.searchFieldHeight
            int r17 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r9 = r9 + r17
            r5.<init>(r15, r9)
            r2.addView(r0, r5)
            org.telegram.ui.Components.EmojiView$17 r0 = new org.telegram.ui.Components.EmojiView$17
            r0.<init>(r10, r6)
            r7.stickersTab = r0
            r2 = 1
            r0.setDragEnabled(r2)
            org.telegram.ui.Components.ScrollSlidingTabStrip r0 = r7.stickersTab
            r2 = 0
            r0.setWillNotDraw(r2)
            org.telegram.ui.Components.ScrollSlidingTabStrip r0 = r7.stickersTab
            org.telegram.ui.Components.ScrollSlidingTabStrip$Type r2 = org.telegram.ui.Components.ScrollSlidingTabStrip.Type.TAB
            r0.setType(r2)
            org.telegram.ui.Components.ScrollSlidingTabStrip r0 = r7.stickersTab
            int r2 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r0.setUnderlineHeight(r2)
            org.telegram.ui.Components.ScrollSlidingTabStrip r0 = r7.stickersTab
            int r2 = r7.getThemedColor(r12)
            r0.setIndicatorColor(r2)
            org.telegram.ui.Components.ScrollSlidingTabStrip r0 = r7.stickersTab
            int r2 = r7.getThemedColor(r1)
            r0.setUnderlineColor(r2)
            r0 = r40
            if (r0 == 0) goto L_0x063d
            org.telegram.ui.Components.EmojiView$18 r2 = new org.telegram.ui.Components.EmojiView$18
            r2.<init>(r10)
            r7.stickersTabContainer = r2
            org.telegram.ui.Components.ScrollSlidingTabStrip r5 = r7.stickersTab
            r9 = 36
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r15, (int) r9, (int) r4)
            r2.addView(r5, r9)
            android.widget.FrameLayout r2 = r7.stickersTabContainer
            r5 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r5)
            r0.addView(r2, r5)
            goto L_0x064a
        L_0x063d:
            android.widget.FrameLayout r0 = r7.stickersContainer
            org.telegram.ui.Components.ScrollSlidingTabStrip r2 = r7.stickersTab
            r5 = 36
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r15, (int) r5, (int) r4)
            r0.addView(r2, r5)
        L_0x064a:
            r32.updateStickerTabs()
            org.telegram.ui.Components.ScrollSlidingTabStrip r0 = r7.stickersTab
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda11 r2 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda11
            r2.<init>(r7)
            r0.setDelegate(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r7.stickersGridView
            org.telegram.ui.Components.EmojiView$TypedScrollListener r2 = new org.telegram.ui.Components.EmojiView$TypedScrollListener
            r5 = 0
            r2.<init>(r5)
            r0.setOnScrollListener(r2)
            goto L_0x0665
        L_0x0663:
            r6 = r41
        L_0x0665:
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$Tab> r0 = r7.currentTabs
            r0.clear()
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$Tab> r0 = r7.currentTabs
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$Tab> r2 = r7.allTabs
            r0.addAll(r2)
            org.telegram.ui.Components.EmojiView$19 r0 = new org.telegram.ui.Components.EmojiView$19
            r0.<init>(r10)
            r7.pager = r0
            org.telegram.ui.Components.EmojiView$EmojiPagesAdapter r2 = new org.telegram.ui.Components.EmojiView$EmojiPagesAdapter
            r5 = 0
            r2.<init>()
            r7.emojiPagerAdapter = r2
            r0.setAdapter(r2)
            org.telegram.ui.Components.EmojiView$20 r0 = new org.telegram.ui.Components.EmojiView$20
            r0.<init>(r10)
            r7.backspaceButton = r0
            r2 = 1
            r0.setHapticFeedbackEnabled(r2)
            android.widget.ImageView r0 = r7.backspaceButton
            int r2 = org.telegram.messenger.R.drawable.smiles_tab_clear
            r0.setImageResource(r2)
            android.widget.ImageView r0 = r7.backspaceButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            r5 = r23
            int r9 = r7.getThemedColor(r5)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r9, r12)
            r0.setColorFilter(r2)
            android.widget.ImageView r0 = r7.backspaceButton
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            android.widget.ImageView r0 = r7.backspaceButton
            int r2 = org.telegram.messenger.R.string.AccDescrBackspace
            java.lang.String r9 = "AccDescrBackspace"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r0.setContentDescription(r9)
            android.widget.ImageView r0 = r7.backspaceButton
            r9 = 1
            r0.setFocusable(r9)
            android.widget.ImageView r0 = r7.backspaceButton
            org.telegram.ui.Components.EmojiView$21 r9 = new org.telegram.ui.Components.EmojiView$21
            r9.<init>(r7)
            r0.setOnClickListener(r9)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r10)
            r7.bulletinContainer = r0
            if (r11 == 0) goto L_0x06f4
            r25 = -1
            r26 = 1120403456(0x42CLASSNAME, float:100.0)
            r27 = 87
            r28 = 0
            r29 = 0
            r30 = 0
            int r9 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            float r9 = (float) r9
            float r12 = org.telegram.messenger.AndroidUtilities.density
            float r9 = r9 / r12
            r12 = 1109393408(0x42200000, float:40.0)
            float r31 = r9 + r12
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r7.addView(r0, r9)
            goto L_0x0709
        L_0x06f4:
            r25 = -1
            r26 = 1120403456(0x42CLASSNAME, float:100.0)
            r27 = 87
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r7.addView(r0, r9)
        L_0x0709:
            org.telegram.ui.Components.EmojiView$22 r0 = new org.telegram.ui.Components.EmojiView$22
            r0.<init>(r7, r10)
            r7.bottomTabContainer = r0
            android.view.View r0 = new android.view.View
            r0.<init>(r10)
            r7.shadowLine = r0
            int r1 = r7.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout r0 = r7.bottomTabContainer
            android.view.View r1 = r7.shadowLine
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            int r12 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r9.<init>(r15, r12)
            r0.addView(r1, r9)
            android.view.View r0 = new android.view.View
            r0.<init>(r10)
            r7.bottomTabContainerBackground = r0
            android.widget.FrameLayout r1 = r7.bottomTabContainer
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            r12 = 1109393408(0x42200000, float:40.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r12 = 83
            r9.<init>(r15, r4, r12)
            r1.addView(r0, r9)
            r0 = 40
            if (r11 == 0) goto L_0x088f
            android.widget.FrameLayout r1 = r7.bottomTabContainer
            android.widget.FrameLayout$LayoutParams r2 = new android.widget.FrameLayout$LayoutParams
            r4 = 1109393408(0x42200000, float:40.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r11 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r9 = r9 + r11
            r11 = 83
            r2.<init>(r15, r9, r11)
            r7.addView(r1, r2)
            android.widget.FrameLayout r1 = r7.bottomTabContainer
            android.widget.ImageView r2 = r7.backspaceButton
            r9 = 47
            r11 = 85
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r0, (int) r11)
            r1.addView(r2, r9)
            r1 = 21
            if (r14 < r1) goto L_0x0788
            android.widget.ImageView r1 = r7.backspaceButton
            r2 = 1099956224(0x41900000, float:18.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r9 = r19
            r11 = 1
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9, r11, r2)
            r1.setBackground(r2)
            goto L_0x078a
        L_0x0788:
            r9 = r19
        L_0x078a:
            android.widget.ImageView r1 = new android.widget.ImageView
            r1.<init>(r10)
            r7.stickerSettingsButton = r1
            int r2 = org.telegram.messenger.R.drawable.smiles_tab_settings
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r7.stickerSettingsButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r11 = r7.getThemedColor(r5)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r11, r12)
            r1.setColorFilter(r2)
            android.widget.ImageView r1 = r7.stickerSettingsButton
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r1.setScaleType(r2)
            android.widget.ImageView r1 = r7.stickerSettingsButton
            r2 = 1
            r1.setFocusable(r2)
            r1 = 21
            if (r14 < r1) goto L_0x07c6
            android.widget.ImageView r1 = r7.stickerSettingsButton
            r11 = 1099956224(0x41900000, float:18.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9, r2, r11)
            r1.setBackground(r11)
        L_0x07c6:
            android.widget.ImageView r1 = r7.stickerSettingsButton
            int r2 = org.telegram.messenger.R.string.Settings
            java.lang.String r11 = "Settings"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r11, r2)
            r1.setContentDescription(r2)
            android.widget.FrameLayout r1 = r7.bottomTabContainer
            android.widget.ImageView r2 = r7.stickerSettingsButton
            r11 = 47
            r12 = 85
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r0, (int) r12)
            r1.addView(r2, r11)
            android.widget.ImageView r1 = r7.stickerSettingsButton
            org.telegram.ui.Components.EmojiView$23 r2 = new org.telegram.ui.Components.EmojiView$23
            r2.<init>()
            r1.setOnClickListener(r2)
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = new org.telegram.ui.Components.PagerSlidingTabStrip
            r1.<init>(r10, r6)
            r7.typeTabs = r1
            androidx.viewpager.widget.ViewPager r2 = r7.pager
            r1.setViewPager(r2)
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r7.typeTabs
            r2 = 0
            r1.setShouldExpand(r2)
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r7.typeTabs
            r1.setIndicatorHeight(r2)
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r7.typeTabs
            r1.setUnderlineHeight(r2)
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r7.typeTabs
            r2 = 1092616192(0x41200000, float:10.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setTabPaddingLeftRight(r2)
            android.widget.FrameLayout r1 = r7.bottomTabContainer
            org.telegram.ui.Components.PagerSlidingTabStrip r2 = r7.typeTabs
            r6 = 81
            r11 = -2
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r0, (int) r6)
            r1.addView(r2, r6)
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r7.typeTabs
            org.telegram.ui.Components.EmojiView$24 r2 = new org.telegram.ui.Components.EmojiView$24
            r2.<init>()
            r1.setOnPageChangeListener(r2)
            android.widget.ImageView r1 = new android.widget.ImageView
            r1.<init>(r10)
            r7.searchButton = r1
            int r2 = org.telegram.messenger.R.drawable.smiles_tab_search
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r7.searchButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r5 = r7.getThemedColor(r5)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r5, r6)
            r1.setColorFilter(r2)
            android.widget.ImageView r1 = r7.searchButton
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r1.setScaleType(r2)
            android.widget.ImageView r1 = r7.searchButton
            int r2 = org.telegram.messenger.R.string.Search
            java.lang.String r5 = "Search"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r1.setContentDescription(r2)
            android.widget.ImageView r1 = r7.searchButton
            r2 = 1
            r1.setFocusable(r2)
            r1 = 21
            if (r14 < r1) goto L_0x0874
            android.widget.ImageView r1 = r7.searchButton
            r5 = 1099956224(0x41900000, float:18.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9, r2, r5)
            r1.setBackground(r5)
        L_0x0874:
            android.widget.FrameLayout r1 = r7.bottomTabContainer
            android.widget.ImageView r2 = r7.searchButton
            r5 = 47
            r6 = 83
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r0, (int) r6)
            r1.addView(r2, r5)
            android.widget.ImageView r1 = r7.searchButton
            org.telegram.ui.Components.EmojiView$25 r2 = new org.telegram.ui.Components.EmojiView$25
            r2.<init>()
            r1.setOnClickListener(r2)
            goto L_0x09ce
        L_0x088f:
            r4 = 1109393408(0x42200000, float:40.0)
            android.widget.FrameLayout r1 = r7.bottomTabContainer
            r6 = 21
            if (r14 < r6) goto L_0x089a
            r9 = 40
            goto L_0x089c
        L_0x089a:
            r9 = 44
        L_0x089c:
            int r23 = r9 + 16
            if (r14 < r6) goto L_0x08a3
            r6 = 40
            goto L_0x08a5
        L_0x08a3:
            r6 = 44
        L_0x08a5:
            r9 = 8
            int r6 = r6 + r9
            float r6 = (float) r6
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x08ae
            goto L_0x08b0
        L_0x08ae:
            r21 = 5
        L_0x08b0:
            r25 = r21 | 80
            r26 = 0
            r27 = 0
            r28 = 1073741824(0x40000000, float:2.0)
            r29 = 0
            r24 = r6
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r7.addView(r1, r6)
            r1 = 1113587712(0x42600000, float:56.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r6 = r7.getThemedColor(r13)
            int r9 = r7.getThemedColor(r13)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r1, r6, r9)
            r6 = 21
            if (r14 >= r6) goto L_0x0908
            android.content.res.Resources r6 = r37.getResources()
            int r9 = org.telegram.messenger.R.drawable.floating_shadow
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r9)
            android.graphics.drawable.Drawable r6 = r6.mutate()
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            r11 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r11, r12)
            r6.setColorFilter(r9)
            org.telegram.ui.Components.CombinedDrawable r9 = new org.telegram.ui.Components.CombinedDrawable
            r11 = 0
            r9.<init>(r6, r1, r11, r11)
            r1 = 1108344832(0x42100000, float:36.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r9.setIconSize(r6, r1)
            r1 = r9
            goto L_0x0974
        L_0x0908:
            r11 = 0
            android.animation.StateListAnimator r6 = new android.animation.StateListAnimator
            r6.<init>()
            r9 = 1
            int[] r12 = new int[r9]
            r9 = 16842919(0x10100a7, float:2.3694026E-38)
            r12[r11] = r9
            android.widget.ImageView r9 = r7.floatingButton
            android.util.Property r13 = android.view.View.TRANSLATION_Z
            r0 = 2
            float[] r4 = new float[r0]
            r0 = 1073741824(0x40000000, float:2.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            r4[r11] = r0
            r0 = 1082130432(0x40800000, float:4.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            r16 = 1
            r4[r16] = r0
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r9, r13, r4)
            r4 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r0 = r0.setDuration(r4)
            r6.addState(r12, r0)
            int[] r0 = new int[r11]
            android.widget.ImageView r4 = r7.floatingButton
            r5 = 2
            float[] r9 = new float[r5]
            r5 = 1082130432(0x40800000, float:4.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r9[r11] = r5
            r5 = 1073741824(0x40000000, float:2.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r11 = 1
            r9[r11] = r5
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r13, r9)
            r11 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r4 = r4.setDuration(r11)
            r6.addState(r0, r4)
            android.widget.ImageView r0 = r7.backspaceButton
            r0.setStateListAnimator(r6)
            android.widget.ImageView r0 = r7.backspaceButton
            org.telegram.ui.Components.EmojiView$26 r4 = new org.telegram.ui.Components.EmojiView$26
            r4.<init>(r7)
            r0.setOutlineProvider(r4)
        L_0x0974:
            android.widget.ImageView r0 = r7.backspaceButton
            r4 = 1073741824(0x40000000, float:2.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r5 = 0
            r0.setPadding(r5, r5, r4, r5)
            android.widget.ImageView r0 = r7.backspaceButton
            r0.setBackground(r1)
            android.widget.ImageView r0 = r7.backspaceButton
            java.lang.String r1 = "AccDescrBackspace"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.setContentDescription(r1)
            android.widget.ImageView r0 = r7.backspaceButton
            r1 = 1
            r0.setFocusable(r1)
            android.widget.FrameLayout r0 = r7.bottomTabContainer
            android.widget.ImageView r1 = r7.backspaceButton
            r2 = 21
            if (r14 < r2) goto L_0x09a1
            r4 = 40
            goto L_0x09a3
        L_0x09a1:
            r4 = 44
        L_0x09a3:
            r5 = 4
            int r21 = r4 + -4
            if (r14 < r2) goto L_0x09ab
            r2 = 40
            goto L_0x09ad
        L_0x09ab:
            r2 = 44
        L_0x09ad:
            int r2 = r2 - r5
            float r2 = (float) r2
            r23 = 51
            r24 = 1092616192(0x41200000, float:10.0)
            r25 = 0
            r26 = 1092616192(0x41200000, float:10.0)
            r27 = 0
            r22 = r2
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r0.addView(r1, r2)
            android.view.View r0 = r7.shadowLine
            r1 = 8
            r0.setVisibility(r1)
            android.view.View r0 = r7.bottomTabContainerBackground
            r0.setVisibility(r1)
        L_0x09ce:
            androidx.viewpager.widget.ViewPager r0 = r7.pager
            r1 = 51
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r15, (int) r15, (int) r1)
            r2 = 0
            r7.addView(r0, r2, r1)
            org.telegram.ui.Components.CorrectlyMeasuringTextView r0 = new org.telegram.ui.Components.CorrectlyMeasuringTextView
            r0.<init>(r10)
            r7.mediaBanTooltip = r0
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            java.lang.String r2 = "chat_gifSaveHintBackground"
            int r2 = r7.getThemedColor(r2)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            android.widget.TextView r0 = r7.mediaBanTooltip
            java.lang.String r1 = "chat_gifSaveHintText"
            int r1 = r7.getThemedColor(r1)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r7.mediaBanTooltip
            r1 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 1088421888(0x40e00000, float:7.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r5 = 1088421888(0x40e00000, float:7.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r0.setPadding(r1, r2, r4, r5)
            android.widget.TextView r0 = r7.mediaBanTooltip
            r1 = 16
            r0.setGravity(r1)
            android.widget.TextView r0 = r7.mediaBanTooltip
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            android.widget.TextView r0 = r7.mediaBanTooltip
            r1 = 4
            r0.setVisibility(r1)
            android.widget.TextView r0 = r7.mediaBanTooltip
            r11 = -2
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r13 = 81
            r14 = 1084227584(0x40a00000, float:5.0)
            r15 = 0
            r16 = 1084227584(0x40a00000, float:5.0)
            r17 = 1112801280(0x42540000, float:53.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r7.addView(r0, r1)
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x0a4f
            r0 = 1109393408(0x42200000, float:40.0)
            goto L_0x0a51
        L_0x0a4f:
            r0 = 1107296256(0x42000000, float:32.0)
        L_0x0a51:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.emojiSize = r0
            org.telegram.ui.Components.EmojiView$EmojiColorPickerView r0 = new org.telegram.ui.Components.EmojiView$EmojiColorPickerView
            r0.<init>(r10)
            r7.pickerView = r0
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r0 = new org.telegram.ui.Components.EmojiView$EmojiPopupWindow
            org.telegram.ui.Components.EmojiView$EmojiColorPickerView r1 = r7.pickerView
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x0a6b
            r2 = 40
            goto L_0x0a6d
        L_0x0a6b:
            r2 = 32
        L_0x0a6d:
            int r2 = r2 * 6
            int r2 = r2 + 10
            int r2 = r2 + 20
            float r2 = (float) r2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r7.popupWidth = r2
            boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r4 == 0) goto L_0x0a83
            r4 = 1115684864(0x42800000, float:64.0)
            goto L_0x0a85
        L_0x0a83:
            r4 = 1113587712(0x42600000, float:56.0)
        L_0x0a85:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r7.popupHeight = r4
            r0.<init>(r7, r1, r2, r4)
            r7.pickerViewPopup = r0
            r1 = 1
            r0.setOutsideTouchable(r1)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r0 = r7.pickerViewPopup
            r0.setClippingEnabled(r1)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r0 = r7.pickerViewPopup
            r2 = 2
            r0.setInputMethodMode(r2)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r0 = r7.pickerViewPopup
            r2 = 0
            r0.setSoftInputMode(r2)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r0 = r7.pickerViewPopup
            android.view.View r0 = r0.getContentView()
            r0.setFocusableInTouchMode(r1)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r0 = r7.pickerViewPopup
            android.view.View r0 = r0.getContentView()
            org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda1 r1 = new org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda1
            r1.<init>(r7)
            r0.setOnKeyListener(r1)
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalEmojiSettings()
            java.lang.String r1 = "selected_page"
            int r0 = r0.getInt(r1, r2)
            r7.currentPage = r0
            org.telegram.messenger.Emoji.loadRecentEmoji()
            org.telegram.ui.Components.EmojiView$EmojiGridAdapter r0 = r7.emojiAdapter
            r0.notifyDataSetChanged()
            r7.setAllow(r8, r3, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.<init>(org.telegram.ui.ActionBar.BaseFragment, boolean, boolean, boolean, android.content.Context, boolean, org.telegram.tgnet.TLRPC$ChatFull, android.view.ViewGroup, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null) {
            emojiViewDelegate.onEmojiSettingsClick();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$2(Theme.ResourcesProvider resourcesProvider2, View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.gifGridView, 0, this.gifOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view, int i) {
        if (this.delegate != null) {
            int i2 = i - 1;
            RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
            GifAdapter gifAdapter2 = this.gifAdapter;
            if (adapter != gifAdapter2) {
                RecyclerView.Adapter adapter2 = this.gifGridView.getAdapter();
                GifAdapter gifAdapter3 = this.gifSearchAdapter;
                if (adapter2 == gifAdapter3 && i2 >= 0 && i2 < gifAdapter3.results.size()) {
                    this.delegate.onGifSelected(view, this.gifSearchAdapter.results.get(i2), this.gifSearchAdapter.lastSearchImageString, this.gifSearchAdapter.bot, true, 0);
                    updateRecentGifs();
                }
            } else if (i2 >= 0) {
                if (i2 < gifAdapter2.recentItemsCount) {
                    this.delegate.onGifSelected(view, this.recentGifs.get(i2), (String) null, "gif", true, 0);
                    return;
                }
                if (this.gifAdapter.recentItemsCount > 0) {
                    i2 = (i2 - this.gifAdapter.recentItemsCount) - 1;
                }
                if (i2 >= 0 && i2 < this.gifAdapter.results.size()) {
                    this.delegate.onGifSelected(view, this.gifAdapter.results.get(i2), (String) null, this.gifAdapter.bot, true, 0);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(int i) {
        if (i != this.gifTrendingTabNum || !this.gifAdapter.results.isEmpty()) {
            this.gifGridView.stopScroll();
            this.gifTabs.onPageScrolled(i, 0);
            int i2 = 1;
            if (i == this.gifRecentTabNum || i == this.gifTrendingTabNum) {
                this.gifSearchField.searchEditText.setText("");
                if (i != this.gifTrendingTabNum || this.gifAdapter.trendingSectionItem < 1) {
                    GifLayoutManager gifLayoutManager2 = this.gifLayoutManager;
                    EmojiViewDelegate emojiViewDelegate = this.delegate;
                    if (emojiViewDelegate != null && emojiViewDelegate.isExpanded()) {
                        i2 = 0;
                    }
                    gifLayoutManager2.scrollToPositionWithOffset(i2, 0);
                } else {
                    this.gifLayoutManager.scrollToPositionWithOffset(this.gifAdapter.trendingSectionItem, -AndroidUtilities.dp(4.0f));
                }
                if (i == this.gifTrendingTabNum) {
                    ArrayList<String> arrayList = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
                    if (!arrayList.isEmpty()) {
                        this.gifSearchPreloader.preload(arrayList.get(0));
                    }
                }
            } else {
                ArrayList<String> arrayList2 = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
                this.gifSearchAdapter.searchEmoji(arrayList2.get(i - this.gifFirstEmojiTabNum));
                int i3 = this.gifFirstEmojiTabNum;
                if (i - i3 > 0) {
                    this.gifSearchPreloader.preload(arrayList2.get((i - i3) - 1));
                }
                if (i - this.gifFirstEmojiTabNum < arrayList2.size() - 1) {
                    this.gifSearchPreloader.preload(arrayList2.get((i - this.gifFirstEmojiTabNum) + 1));
                }
            }
            resetTabsY(2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$5(Theme.ResourcesProvider resourcesProvider2, View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(View view, int i) {
        String str;
        RecyclerView.Adapter adapter = this.stickersGridView.getAdapter();
        StickersSearchGridAdapter stickersSearchGridAdapter2 = this.stickersSearchGridAdapter;
        if (adapter == stickersSearchGridAdapter2) {
            String access$19400 = stickersSearchGridAdapter2.searchQuery;
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) this.stickersSearchGridAdapter.positionsToSets.get(i);
            if (tLRPC$StickerSetCovered != null) {
                this.delegate.onShowStickerSet(tLRPC$StickerSetCovered.set, (TLRPC$InputStickerSet) null);
                return;
            }
            str = access$19400;
        } else {
            str = null;
        }
        if (view instanceof StickerEmojiCell) {
            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
            if (stickerEmojiCell.getSticker() == null || !MessageObject.isPremiumSticker(stickerEmojiCell.getSticker()) || AccountInstance.getInstance(this.currentAccount).getUserConfig().isPremium()) {
                ContentPreviewViewer.getInstance().reset();
                if (!stickerEmojiCell.isDisabled()) {
                    stickerEmojiCell.disable();
                    this.delegate.onStickerSelected(stickerEmojiCell, stickerEmojiCell.getSticker(), str, stickerEmojiCell.getParentObject(), stickerEmojiCell.getSendAnimationData(), true, 0);
                    return;
                }
                return;
            }
            ContentPreviewViewer.getInstance().showMenuFor(stickerEmojiCell);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(int i) {
        if (!this.firstTabUpdate) {
            if (i == this.trendingTabNum) {
                openTrendingStickers((TLRPC$StickerSetCovered) null);
            } else if (i == this.recentTabNum) {
                this.stickersGridView.stopScroll();
                scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("recent"), 0);
                resetTabsY(0);
                ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
                int i2 = this.recentTabNum;
                scrollSlidingTabStrip.onPageScrolled(i2, i2 > 0 ? i2 : this.stickersTabOffset);
            } else if (i == this.favTabNum) {
                this.stickersGridView.stopScroll();
                scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("fav"), 0);
                resetTabsY(0);
                ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
                int i3 = this.favTabNum;
                scrollSlidingTabStrip2.onPageScrolled(i3, i3 > 0 ? i3 : this.stickersTabOffset);
            } else if (i == this.premiumTabNum) {
                this.stickersGridView.stopScroll();
                scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("premium"), 0);
                resetTabsY(0);
                ScrollSlidingTabStrip scrollSlidingTabStrip3 = this.stickersTab;
                int i4 = this.premiumTabNum;
                scrollSlidingTabStrip3.onPageScrolled(i4, i4 > 0 ? i4 : this.stickersTabOffset);
            } else {
                int i5 = i - this.stickersTabOffset;
                if (i5 < this.stickerSets.size()) {
                    if (i5 >= this.stickerSets.size()) {
                        i5 = this.stickerSets.size() - 1;
                    }
                    this.firstStickersAttach = false;
                    this.stickersGridView.stopScroll();
                    scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack(this.stickerSets.get(i5)), 0);
                    resetTabsY(0);
                    checkScroll(0);
                    int i6 = this.favTabNum;
                    if (i6 <= 0 && (i6 = this.recentTabNum) <= 0) {
                        i6 = this.stickersTabOffset;
                    }
                    this.stickersTab.onPageScrolled(i, i6);
                    this.expandStickersByDragg = false;
                    updateStickerTabsPosition();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$8(View view, int i, KeyEvent keyEvent) {
        EmojiPopupWindow emojiPopupWindow;
        if (i != 82 || keyEvent.getRepeatCount() != 0 || keyEvent.getAction() != 1 || (emojiPopupWindow = this.pickerViewPopup) == null || !emojiPopupWindow.isShowing()) {
            return false;
        }
        this.pickerViewPopup.dismiss();
        return true;
    }

    class EmojiGridView extends RecyclerListView {
        private boolean ignoreLayout;
        private int lastChildCount = -1;
        ArrayList<DrawingInBackgroundLine> lineDrawables = new ArrayList<>();
        ArrayList<DrawingInBackgroundLine> lineDrawablesTmp = new ArrayList<>();
        private HashMap<Integer, TouchDownInfo> touches;
        ArrayList<ArrayList<ImageViewEmoji>> unusedArrays = new ArrayList<>();
        ArrayList<DrawingInBackgroundLine> unusedLineDrawables = new ArrayList<>();
        SparseArray<ArrayList<ImageViewEmoji>> viewsGroupedByLines = new SparseArray<>();

        public EmojiGridView(Context context) {
            super(context);
            new SparseIntArray();
            new AnimatedFloat((View) this, 350, (TimeInterpolator) CubicBezierInterpolator.EASE_OUT_QUINT);
        }

        private AnimatedEmojiSpan[] getAnimatedEmojiSpans() {
            AnimatedEmojiSpan[] animatedEmojiSpanArr = new AnimatedEmojiSpan[EmojiView.this.emojiGridView.getChildCount()];
            for (int i = 0; i < EmojiView.this.emojiGridView.getChildCount(); i++) {
                View childAt = EmojiView.this.emojiGridView.getChildAt(i);
                if (childAt instanceof ImageViewEmoji) {
                    animatedEmojiSpanArr[i] = ((ImageViewEmoji) childAt).getSpan();
                }
            }
            return animatedEmojiSpanArr;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            this.ignoreLayout = true;
            int size = View.MeasureSpec.getSize(i);
            int spanCount = EmojiView.this.emojiLayoutManager.getSpanCount();
            EmojiView.this.emojiLayoutManager.setSpanCount(Math.max(1, size / AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 45.0f)));
            this.ignoreLayout = false;
            super.onMeasure(i, i2);
            if (spanCount != EmojiView.this.emojiLayoutManager.getSpanCount()) {
                EmojiView.this.emojiAdapter.notifyDataSetChanged();
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            if (EmojiView.this.needEmojiSearch && EmojiView.this.firstEmojiAttach) {
                this.ignoreLayout = true;
                EmojiView.this.emojiLayoutManager.scrollToPositionWithOffset(1, 0);
                boolean unused = EmojiView.this.firstEmojiAttach = false;
                this.ignoreLayout = false;
            }
            super.onLayout(z, i, i2, i3, i4);
            EmojiView.this.checkEmojiSearchFieldScroll(true);
            updateEmojiDrawables();
        }

        public void requestLayout() {
            if (!this.ignoreLayout) {
                super.requestLayout();
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:18:0x006f  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r12) {
            /*
                r11 = this;
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$ImageViewEmoji r0 = r0.emojiTouchedView
                if (r0 == 0) goto L_0x01b1
                int r0 = r12.getAction()
                r1 = 2
                r2 = 5
                r3 = 3
                r4 = -971227136(0xffffffffCLASSNAMECLASSNAME, float:-10000.0)
                r5 = 1
                if (r0 == r5) goto L_0x00da
                int r0 = r12.getAction()
                if (r0 != r3) goto L_0x001d
                goto L_0x00da
            L_0x001d:
                int r0 = r12.getAction()
                if (r0 != r1) goto L_0x01b0
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                float r0 = r0.emojiTouchedX
                r1 = 0
                int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r0 == 0) goto L_0x006c
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                float r0 = r0.emojiTouchedX
                float r3 = r12.getX()
                float r0 = r0 - r3
                float r0 = java.lang.Math.abs(r0)
                r3 = 1045220557(0x3e4ccccd, float:0.2)
                float r6 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r3, r5)
                int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
                if (r0 > 0) goto L_0x0062
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                float r0 = r0.emojiTouchedY
                float r6 = r12.getY()
                float r0 = r0 - r6
                float r0 = java.lang.Math.abs(r0)
                float r3 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r3, r1)
                int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
                if (r0 <= 0) goto L_0x0060
                goto L_0x0062
            L_0x0060:
                r0 = 1
                goto L_0x006d
            L_0x0062:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                float unused = r0.emojiTouchedX = r4
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                float unused = r0.emojiTouchedY = r4
            L_0x006c:
                r0 = 0
            L_0x006d:
                if (r0 != 0) goto L_0x01b0
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                int[] r0 = r0.location
                r11.getLocationOnScreen(r0)
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                int[] r0 = r0.location
                r0 = r0[r1]
                float r0 = (float) r0
                float r12 = r12.getX()
                float r0 = r0 + r12
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$EmojiColorPickerView r12 = r12.pickerView
                org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                int[] r3 = r3.location
                r12.getLocationOnScreen(r3)
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                int[] r12 = r12.location
                r12 = r12[r1]
                r3 = 1077936128(0x40400000, float:3.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r12 = r12 + r3
                float r12 = (float) r12
                float r0 = r0 - r12
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                int r12 = r12.emojiSize
                r3 = 1082130432(0x40800000, float:4.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r12 = r12 + r3
                float r12 = (float) r12
                float r0 = r0 / r12
                int r12 = (int) r0
                if (r12 >= 0) goto L_0x00ba
                r2 = 0
                goto L_0x00be
            L_0x00ba:
                if (r12 <= r2) goto L_0x00bd
                goto L_0x00be
            L_0x00bd:
                r2 = r12
            L_0x00be:
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$EmojiColorPickerView r12 = r12.pickerView
                int r12 = r12.getSelection()
                if (r12 == r2) goto L_0x00cf
                r12 = 9
                r11.performHapticFeedback(r12, r5)     // Catch:{ Exception -> 0x00cf }
            L_0x00cf:
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$EmojiColorPickerView r12 = r12.pickerView
                r12.setSelection(r2)
                goto L_0x01b0
            L_0x00da:
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$EmojiPopupWindow r12 = r12.pickerViewPopup
                r0 = 0
                if (r12 == 0) goto L_0x01a1
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$EmojiPopupWindow r12 = r12.pickerViewPopup
                boolean r12 = r12.isShowing()
                if (r12 == 0) goto L_0x01a1
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$EmojiPopupWindow r12 = r12.pickerViewPopup
                r12.dismiss()
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$EmojiColorPickerView r12 = r12.pickerView
                int r12 = r12.getSelection()
                java.lang.String r6 = "🏿"
                java.lang.String r7 = "🏾"
                java.lang.String r8 = "🏽"
                java.lang.String r9 = "🏼"
                java.lang.String r10 = "🏻"
                if (r12 == r5) goto L_0x0121
                if (r12 == r1) goto L_0x011f
                if (r12 == r3) goto L_0x011d
                r1 = 4
                if (r12 == r1) goto L_0x011b
                if (r12 == r2) goto L_0x0119
                r12 = r0
                goto L_0x0122
            L_0x0119:
                r12 = r6
                goto L_0x0122
            L_0x011b:
                r12 = r7
                goto L_0x0122
            L_0x011d:
                r12 = r8
                goto L_0x0122
            L_0x011f:
                r12 = r9
                goto L_0x0122
            L_0x0121:
                r12 = r10
            L_0x0122:
                org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$ImageViewEmoji r1 = r1.emojiTouchedView
                java.lang.Object r1 = r1.getTag()
                java.lang.String r1 = (java.lang.String) r1
                org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$ImageViewEmoji r2 = r2.emojiTouchedView
                boolean r2 = r2.isRecent
                if (r2 != 0) goto L_0x0172
                if (r12 == 0) goto L_0x0146
                java.util.HashMap<java.lang.String, java.lang.String> r2 = org.telegram.messenger.Emoji.emojiColor
                r2.put(r1, r12)
                java.lang.String r1 = org.telegram.ui.Components.EmojiView.addColorToCode(r1, r12)
                goto L_0x014b
            L_0x0146:
                java.util.HashMap<java.lang.String, java.lang.String> r12 = org.telegram.messenger.Emoji.emojiColor
                r12.remove(r1)
            L_0x014b:
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$ImageViewEmoji r12 = r12.emojiTouchedView
                android.graphics.drawable.Drawable r1 = org.telegram.messenger.Emoji.getEmojiBigDrawable(r1)
                org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$ImageViewEmoji r2 = r2.emojiTouchedView
                boolean r2 = r2.isRecent
                r12.setImageDrawable(r1, r2)
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$ImageViewEmoji r12 = r12.emojiTouchedView
                r12.sendEmoji(r0)
                r11.performHapticFeedback(r3, r5)     // Catch:{ Exception -> 0x016e }
            L_0x016e:
                org.telegram.messenger.Emoji.saveEmojiColors()
                goto L_0x01a1
            L_0x0172:
                java.lang.String r2 = ""
                java.lang.String r1 = r1.replace(r10, r2)
                java.lang.String r1 = r1.replace(r9, r2)
                java.lang.String r1 = r1.replace(r8, r2)
                java.lang.String r1 = r1.replace(r7, r2)
                java.lang.String r1 = r1.replace(r6, r2)
                if (r12 == 0) goto L_0x0198
                org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$ImageViewEmoji r2 = r2.emojiTouchedView
                java.lang.String r12 = org.telegram.ui.Components.EmojiView.addColorToCode(r1, r12)
                r2.sendEmoji(r12)
                goto L_0x01a1
            L_0x0198:
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$ImageViewEmoji r12 = r12.emojiTouchedView
                r12.sendEmoji(r1)
            L_0x01a1:
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView.ImageViewEmoji unused = r12.emojiTouchedView = r0
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                float unused = r12.emojiTouchedX = r4
                org.telegram.ui.Components.EmojiView r12 = org.telegram.ui.Components.EmojiView.this
                float unused = r12.emojiTouchedY = r4
            L_0x01b0:
                return r5
            L_0x01b1:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                float r1 = r12.getX()
                float unused = r0.emojiLastX = r1
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                float r1 = r12.getY()
                float unused = r0.emojiLastY = r1
                boolean r12 = super.onTouchEvent(r12)
                return r12
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiGridView.onTouchEvent(android.view.MotionEvent):boolean");
        }

        public void updateEmojiDrawables() {
            LongSparseArray unused = EmojiView.this.animatedEmojiDrawables = AnimatedEmojiSpan.update(2, (View) this, getAnimatedEmojiSpans(), (LongSparseArray<AnimatedEmojiDrawable>) EmojiView.this.animatedEmojiDrawables);
        }

        public void onScrollStateChanged(int i) {
            super.onScrollStateChanged(i);
            if (i == 0) {
                if (!canScrollVertically(-1) || !canScrollVertically(1)) {
                    EmojiView.this.showBottomTab(true, true);
                }
                if (!canScrollVertically(1)) {
                    EmojiView.this.checkTabsY(1, AndroidUtilities.dp(36.0f));
                }
            }
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            DrawingInBackgroundLine drawingInBackgroundLine;
            Canvas canvas2 = canvas;
            super.dispatchDraw(canvas);
            if (this.lastChildCount != getChildCount()) {
                updateEmojiDrawables();
                this.lastChildCount = getChildCount();
            }
            for (int i = 0; i < this.viewsGroupedByLines.size(); i++) {
                ArrayList valueAt = this.viewsGroupedByLines.valueAt(i);
                valueAt.clear();
                this.unusedArrays.add(valueAt);
            }
            this.viewsGroupedByLines.clear();
            boolean z = ((EmojiView.this.animateExpandStartTime > 0 ? 1 : (EmojiView.this.animateExpandStartTime == 0 ? 0 : -1)) > 0 && ((SystemClock.elapsedRealtime() - EmojiView.this.animateExpandStartTime) > animateExpandDuration() ? 1 : ((SystemClock.elapsedRealtime() - EmojiView.this.animateExpandStartTime) == animateExpandDuration() ? 0 : -1)) < 0) && EmojiView.this.animateExpandFromButton != null && EmojiView.this.animateExpandFromPosition >= 0;
            if (!(EmojiView.this.animatedEmojiDrawables == null || EmojiView.this.emojiGridView == null)) {
                for (int i2 = 0; i2 < EmojiView.this.emojiGridView.getChildCount(); i2++) {
                    View childAt = EmojiView.this.emojiGridView.getChildAt(i2);
                    if (childAt instanceof ImageViewEmoji) {
                        int top = childAt.getTop() + ((int) childAt.getTranslationY());
                        ArrayList arrayList = this.viewsGroupedByLines.get(top);
                        if (arrayList == null) {
                            if (!this.unusedArrays.isEmpty()) {
                                ArrayList<ArrayList<ImageViewEmoji>> arrayList2 = this.unusedArrays;
                                arrayList = arrayList2.remove(arrayList2.size() - 1);
                            } else {
                                arrayList = new ArrayList();
                            }
                            this.viewsGroupedByLines.put(top, arrayList);
                        }
                        arrayList.add((ImageViewEmoji) childAt);
                    }
                    if (z && childAt != null && getChildAdapterPosition(childAt) == EmojiView.this.animateExpandFromPosition - 1) {
                        float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(MathUtils.clamp(((float) (SystemClock.elapsedRealtime() - EmojiView.this.animateExpandStartTime)) / 140.0f, 0.0f, 1.0f));
                        if (interpolation < 1.0f) {
                            float f = 1.0f - interpolation;
                            canvas.saveLayerAlpha((float) childAt.getLeft(), (float) childAt.getTop(), (float) childAt.getRight(), (float) childAt.getBottom(), (int) (255.0f * f), 31);
                            canvas2.translate((float) childAt.getLeft(), (float) childAt.getTop());
                            float f2 = (f * 0.5f) + 0.5f;
                            canvas2.scale(f2, f2, ((float) childAt.getWidth()) / 2.0f, ((float) childAt.getHeight()) / 2.0f);
                            EmojiView.this.animateExpandFromButton.draw(canvas2);
                            canvas.restore();
                        }
                    }
                }
            }
            this.lineDrawablesTmp.clear();
            this.lineDrawablesTmp.addAll(this.lineDrawables);
            this.lineDrawables.clear();
            long currentTimeMillis = System.currentTimeMillis();
            int i3 = 0;
            while (true) {
                DrawingInBackgroundLine drawingInBackgroundLine2 = null;
                if (i3 >= this.viewsGroupedByLines.size()) {
                    break;
                }
                ArrayList<ImageViewEmoji> valueAt2 = this.viewsGroupedByLines.valueAt(i3);
                ImageViewEmoji imageViewEmoji = valueAt2.get(0);
                int i4 = imageViewEmoji.position;
                int i5 = 0;
                while (true) {
                    if (i5 >= this.lineDrawablesTmp.size()) {
                        break;
                    } else if (this.lineDrawablesTmp.get(i5).position == i4) {
                        drawingInBackgroundLine2 = this.lineDrawablesTmp.get(i5);
                        this.lineDrawablesTmp.remove(i5);
                        break;
                    } else {
                        i5++;
                    }
                }
                if (drawingInBackgroundLine == null) {
                    if (!this.unusedLineDrawables.isEmpty()) {
                        ArrayList<DrawingInBackgroundLine> arrayList3 = this.unusedLineDrawables;
                        drawingInBackgroundLine = arrayList3.remove(arrayList3.size() - 1);
                    } else {
                        drawingInBackgroundLine = new DrawingInBackgroundLine();
                    }
                    drawingInBackgroundLine.position = i4;
                    drawingInBackgroundLine.onAttachToWindow();
                }
                this.lineDrawables.add(drawingInBackgroundLine);
                drawingInBackgroundLine.imageViewEmojis = valueAt2;
                canvas.save();
                canvas2.translate((float) imageViewEmoji.getLeft(), imageViewEmoji.getY() + ((float) imageViewEmoji.getPaddingTop()));
                drawingInBackgroundLine.startOffset = imageViewEmoji.getLeft();
                int measuredWidth = getMeasuredWidth() - (imageViewEmoji.getLeft() * 2);
                int measuredHeight = imageViewEmoji.getMeasuredHeight() - imageViewEmoji.getPaddingBottom();
                if (measuredWidth > 0 && measuredHeight > 0) {
                    drawingInBackgroundLine.draw(canvas, currentTimeMillis, measuredWidth, measuredHeight, 1.0f);
                }
                canvas.restore();
                i3++;
            }
            for (int i6 = 0; i6 < this.lineDrawablesTmp.size(); i6++) {
                if (this.unusedLineDrawables.size() < 3) {
                    this.unusedLineDrawables.add(this.lineDrawablesTmp.get(i6));
                    this.lineDrawablesTmp.get(i6).imageViewEmojis = null;
                    this.lineDrawablesTmp.get(i6).reset();
                } else {
                    this.lineDrawablesTmp.get(i6).onDetachFromWindow();
                }
            }
            this.lineDrawablesTmp.clear();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateEmojiDrawables();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            AnimatedEmojiSpan.release((View) this, (LongSparseArray<AnimatedEmojiDrawable>) EmojiView.this.animatedEmojiDrawables);
            for (int i = 0; i < this.lineDrawables.size(); i++) {
                this.lineDrawables.get(i).onDetachFromWindow();
            }
            for (int i2 = 0; i2 < this.unusedLineDrawables.size(); i2++) {
                this.unusedLineDrawables.get(i2).onDetachFromWindow();
            }
            this.unusedLineDrawables.addAll(this.lineDrawables);
            this.lineDrawables.clear();
        }

        class TouchDownInfo {
            long time;
            View view;
            float x;
            float y;

            TouchDownInfo(EmojiGridView emojiGridView) {
            }
        }

        public void clearTouchesFor(View view) {
            TouchDownInfo remove;
            HashMap<Integer, TouchDownInfo> hashMap = this.touches;
            if (hashMap != null) {
                for (Map.Entry next : hashMap.entrySet()) {
                    if (!(next == null || ((TouchDownInfo) next.getValue()).view != view || (remove = this.touches.remove(next.getKey())) == null)) {
                        View view2 = remove.view;
                        if (view2 != null && Build.VERSION.SDK_INT >= 21 && (view2.getBackground() instanceof RippleDrawable)) {
                            remove.view.getBackground().setState(new int[0]);
                        }
                        View view3 = remove.view;
                        if (view3 != null) {
                            view3.setPressed(false);
                        }
                    }
                }
            }
        }

        public long animateExpandDuration() {
            return animateExpandAppearDuration() + animateExpandCrossfadeDuration() + 150;
        }

        public long animateExpandAppearDuration() {
            return Math.max(600, ((long) Math.min(55, EmojiView.this.animateExpandToPosition - EmojiView.this.animateExpandFromPosition)) * 40);
        }

        public long animateExpandCrossfadeDuration() {
            return Math.max(400, ((long) Math.min(45, EmojiView.this.animateExpandToPosition - EmojiView.this.animateExpandFromPosition)) * 35);
        }

        class DrawingInBackgroundLine extends DrawingInBackgroundThreadDrawable {
            private OvershootInterpolator appearScaleInterpolator = new OvershootInterpolator(3.0f);
            ArrayList<ImageViewEmoji> drawInBackgroundViews = new ArrayList<>();
            ArrayList<ImageViewEmoji> imageViewEmojis;
            public int position;
            public int startOffset;

            DrawingInBackgroundLine() {
            }

            /* JADX WARNING: Removed duplicated region for block: B:30:0x007e  */
            /* JADX WARNING: Removed duplicated region for block: B:31:0x008c  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void draw(android.graphics.Canvas r9, long r10, int r12, int r13, float r14) {
                /*
                    r8 = this;
                    java.util.ArrayList<org.telegram.ui.Components.EmojiView$ImageViewEmoji> r0 = r8.imageViewEmojis
                    if (r0 != 0) goto L_0x0005
                    return
                L_0x0005:
                    int r0 = r0.size()
                    r1 = 4
                    r2 = 0
                    r3 = 1
                    if (r0 <= r1) goto L_0x0017
                    int r0 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
                    if (r0 != 0) goto L_0x0015
                    goto L_0x0017
                L_0x0015:
                    r0 = 0
                    goto L_0x0018
                L_0x0017:
                    r0 = 1
                L_0x0018:
                    if (r0 != 0) goto L_0x007b
                    org.telegram.ui.Components.EmojiView$EmojiGridView r1 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                    long r4 = r1.animateExpandStartTime
                    r6 = 0
                    int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                    if (r1 <= 0) goto L_0x0041
                    long r4 = android.os.SystemClock.elapsedRealtime()
                    org.telegram.ui.Components.EmojiView$EmojiGridView r1 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                    long r6 = r1.animateExpandStartTime
                    long r4 = r4 - r6
                    org.telegram.ui.Components.EmojiView$EmojiGridView r1 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    long r6 = r1.animateExpandDuration()
                    int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                    if (r1 >= 0) goto L_0x0041
                    r1 = 1
                    goto L_0x0042
                L_0x0041:
                    r1 = 0
                L_0x0042:
                    java.util.ArrayList<org.telegram.ui.Components.EmojiView$ImageViewEmoji> r4 = r8.imageViewEmojis
                    int r4 = r4.size()
                    if (r2 >= r4) goto L_0x007b
                    java.util.ArrayList<org.telegram.ui.Components.EmojiView$ImageViewEmoji> r4 = r8.imageViewEmojis
                    java.lang.Object r4 = r4.get(r2)
                    org.telegram.ui.Components.EmojiView$ImageViewEmoji r4 = (org.telegram.ui.Components.EmojiView.ImageViewEmoji) r4
                    float r5 = r4.pressedProgress
                    r6 = 0
                    int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
                    if (r5 != 0) goto L_0x007c
                    android.animation.ValueAnimator r5 = r4.backAnimator
                    if (r5 != 0) goto L_0x007c
                    int r5 = r4.position
                    org.telegram.ui.Components.EmojiView$EmojiGridView r6 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                    int r6 = r6.animateExpandFromPosition
                    if (r5 <= r6) goto L_0x0078
                    int r4 = r4.position
                    org.telegram.ui.Components.EmojiView$EmojiGridView r5 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r5 = org.telegram.ui.Components.EmojiView.this
                    int r5 = r5.animateExpandToPosition
                    if (r4 >= r5) goto L_0x0078
                    if (r1 == 0) goto L_0x0078
                    goto L_0x007c
                L_0x0078:
                    int r2 = r2 + 1
                    goto L_0x0042
                L_0x007b:
                    r3 = r0
                L_0x007c:
                    if (r3 == 0) goto L_0x008c
                    long r10 = java.lang.System.currentTimeMillis()
                    r8.prepareDraw(r10)
                    r8.drawInUiThread(r9)
                    r8.reset()
                    goto L_0x008f
                L_0x008c:
                    super.draw(r9, r10, r12, r13, r14)
                L_0x008f:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiGridView.DrawingInBackgroundLine.draw(android.graphics.Canvas, long, int, int, float):void");
            }

            public void prepareDraw(long j) {
                AnimatedEmojiDrawable animatedEmojiDrawable;
                this.drawInBackgroundViews.clear();
                for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                    ImageViewEmoji imageViewEmoji = this.imageViewEmojis.get(i);
                    if (!(imageViewEmoji.getSpan() == null || (animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiView.this.animatedEmojiDrawables.get(imageViewEmoji.span.getDocumentId())) == null || animatedEmojiDrawable.getImageReceiver() == null)) {
                        animatedEmojiDrawable.update(j);
                        ImageReceiver.BackgroundThreadDrawHolder unused = imageViewEmoji.backgroundThreadDrawHolder = animatedEmojiDrawable.getImageReceiver().setDrawInBackgroundThread(imageViewEmoji.backgroundThreadDrawHolder);
                        imageViewEmoji.backgroundThreadDrawHolder.time = j;
                        animatedEmojiDrawable.setAlpha(255);
                        int height = (int) (((float) imageViewEmoji.getHeight()) * 0.03f);
                        Rect rect = AndroidUtilities.rectTmp2;
                        rect.set((imageViewEmoji.getLeft() + imageViewEmoji.getPaddingLeft()) - this.startOffset, height, (imageViewEmoji.getRight() - imageViewEmoji.getPaddingRight()) - this.startOffset, ((imageViewEmoji.getMeasuredHeight() + height) - imageViewEmoji.getPaddingTop()) - imageViewEmoji.getPaddingBottom());
                        imageViewEmoji.backgroundThreadDrawHolder.setBounds(rect);
                        imageViewEmoji.drawable = animatedEmojiDrawable;
                        animatedEmojiDrawable.getImageReceiver();
                        this.drawInBackgroundViews.add(imageViewEmoji);
                    }
                }
            }

            public void drawInBackground(Canvas canvas) {
                for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                    ImageViewEmoji imageViewEmoji = this.drawInBackgroundViews.get(i);
                    AnimatedEmojiDrawable animatedEmojiDrawable = imageViewEmoji.drawable;
                    if (animatedEmojiDrawable != null) {
                        animatedEmojiDrawable.draw(canvas, imageViewEmoji.backgroundThreadDrawHolder);
                    }
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:33:0x0174  */
            /* JADX WARNING: Removed duplicated region for block: B:34:0x018b  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void drawInUiThread(android.graphics.Canvas r20) {
                /*
                    r19 = this;
                    r0 = r19
                    r1 = r20
                    java.util.ArrayList<org.telegram.ui.Components.EmojiView$ImageViewEmoji> r2 = r0.imageViewEmojis
                    if (r2 == 0) goto L_0x0195
                    r20.save()
                    int r2 = r0.startOffset
                    int r2 = -r2
                    float r2 = (float) r2
                    r3 = 0
                    r1.translate(r2, r3)
                    r4 = 0
                L_0x0014:
                    java.util.ArrayList<org.telegram.ui.Components.EmojiView$ImageViewEmoji> r5 = r0.imageViewEmojis
                    int r5 = r5.size()
                    if (r4 >= r5) goto L_0x0192
                    java.util.ArrayList<org.telegram.ui.Components.EmojiView$ImageViewEmoji> r5 = r0.imageViewEmojis
                    java.lang.Object r5 = r5.get(r4)
                    org.telegram.ui.Components.EmojiView$ImageViewEmoji r5 = (org.telegram.ui.Components.EmojiView.ImageViewEmoji) r5
                    org.telegram.ui.Components.AnimatedEmojiSpan r6 = r5.getSpan()
                    if (r6 != 0) goto L_0x002c
                    goto L_0x018e
                L_0x002c:
                    org.telegram.ui.Components.EmojiView$EmojiGridView r6 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                    android.util.LongSparseArray r6 = r6.animatedEmojiDrawables
                    org.telegram.ui.Components.AnimatedEmojiSpan r7 = r5.span
                    long r7 = r7.getDocumentId()
                    java.lang.Object r6 = r6.get(r7)
                    org.telegram.ui.Components.AnimatedEmojiDrawable r6 = (org.telegram.ui.Components.AnimatedEmojiDrawable) r6
                    if (r6 != 0) goto L_0x0046
                    goto L_0x018e
                L_0x0046:
                    int r7 = r5.getHeight()
                    float r7 = (float) r7
                    r8 = 1022739087(0x3cf5CLASSNAMEf, float:0.03)
                    float r7 = r7 * r8
                    int r7 = (int) r7
                    android.graphics.Rect r8 = org.telegram.messenger.AndroidUtilities.rectTmp2
                    int r9 = r5.getLeft()
                    int r10 = r5.getPaddingLeft()
                    int r9 = r9 + r10
                    int r10 = r5.getRight()
                    int r11 = r5.getPaddingRight()
                    int r10 = r10 - r11
                    int r11 = r5.getMeasuredHeight()
                    int r11 = r11 + r7
                    int r12 = r5.getPaddingBottom()
                    int r11 = r11 - r12
                    int r12 = r5.getPaddingTop()
                    int r11 = r11 - r12
                    r8.set(r9, r7, r10, r11)
                    float r7 = r5.pressedProgress
                    r9 = 1065353216(0x3var_, float:1.0)
                    int r10 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
                    if (r10 == 0) goto L_0x008d
                    r10 = 1061997773(0x3f4ccccd, float:0.8)
                    r11 = 1045220557(0x3e4ccccd, float:0.2)
                    float r7 = r9 - r7
                    float r7 = r7 * r11
                    float r7 = r7 + r10
                    float r7 = r7 * r9
                    goto L_0x008f
                L_0x008d:
                    r7 = 1065353216(0x3var_, float:1.0)
                L_0x008f:
                    org.telegram.ui.Components.EmojiView$EmojiGridView r10 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                    long r10 = r10.animateExpandStartTime
                    r12 = 0
                    int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
                    if (r14 <= 0) goto L_0x00b6
                    long r10 = android.os.SystemClock.elapsedRealtime()
                    org.telegram.ui.Components.EmojiView$EmojiGridView r14 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r14 = org.telegram.ui.Components.EmojiView.this
                    long r14 = r14.animateExpandStartTime
                    long r10 = r10 - r14
                    org.telegram.ui.Components.EmojiView$EmojiGridView r14 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    long r14 = r14.animateExpandDuration()
                    int r16 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1))
                    if (r16 >= 0) goto L_0x00b6
                    r10 = 1
                    goto L_0x00b7
                L_0x00b6:
                    r10 = 0
                L_0x00b7:
                    if (r10 == 0) goto L_0x0163
                    org.telegram.ui.Components.EmojiView$EmojiGridView r10 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                    int r10 = r10.animateExpandFromPosition
                    if (r10 < 0) goto L_0x0163
                    org.telegram.ui.Components.EmojiView$EmojiGridView r10 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                    int r10 = r10.animateExpandToPosition
                    if (r10 < 0) goto L_0x0163
                    org.telegram.ui.Components.EmojiView$EmojiGridView r10 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                    long r10 = r10.animateExpandStartTime
                    int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
                    if (r14 <= 0) goto L_0x0163
                    org.telegram.ui.Components.EmojiView$EmojiGridView r10 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    int r5 = r10.getChildAdapterPosition(r5)
                    org.telegram.ui.Components.EmojiView$EmojiGridView r10 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                    int r10 = r10.animateExpandFromPosition
                    int r5 = r5 - r10
                    org.telegram.ui.Components.EmojiView$EmojiGridView r10 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                    int r10 = r10.animateExpandToPosition
                    org.telegram.ui.Components.EmojiView$EmojiGridView r11 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                    int r11 = r11.animateExpandFromPosition
                    int r10 = r10 - r11
                    if (r5 < 0) goto L_0x0163
                    if (r5 >= r10) goto L_0x0163
                    org.telegram.ui.Components.EmojiView$EmojiGridView r11 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    long r11 = r11.animateExpandAppearDuration()
                    float r11 = (float) r11
                    org.telegram.ui.Components.EmojiView$EmojiGridView r12 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    long r12 = r12.animateExpandCrossfadeDuration()
                    float r12 = (float) r12
                    long r13 = android.os.SystemClock.elapsedRealtime()
                    org.telegram.ui.Components.EmojiView$EmojiGridView r15 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r15 = org.telegram.ui.Components.EmojiView.this
                    long r15 = r15.animateExpandStartTime
                    long r13 = r13 - r15
                    float r13 = (float) r13
                    r14 = 1055286886(0x3ee66666, float:0.45)
                    float r14 = r14 * r11
                    float r13 = r13 - r14
                    float r13 = r13 / r12
                    float r12 = androidx.core.math.MathUtils.clamp((float) r13, (float) r3, (float) r9)
                    org.telegram.ui.Components.CubicBezierInterpolator r13 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
                    long r14 = android.os.SystemClock.elapsedRealtime()
                    org.telegram.ui.Components.EmojiView$EmojiGridView r2 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                    long r17 = r2.animateExpandStartTime
                    long r14 = r14 - r17
                    float r2 = (float) r14
                    float r2 = r2 / r11
                    float r2 = androidx.core.math.MathUtils.clamp((float) r2, (float) r3, (float) r9)
                    float r2 = r13.getInterpolation(r2)
                    float r11 = (float) r10
                    r13 = 1084227584(0x40a00000, float:5.0)
                    float r13 = r11 / r13
                    org.telegram.messenger.AndroidUtilities.cascade(r12, r5, r10, r13)
                    r12 = 1082130432(0x40800000, float:4.0)
                    float r11 = r11 / r12
                    float r12 = org.telegram.messenger.AndroidUtilities.cascade(r2, r5, r10, r11)
                    int r13 = r10 / 4
                    int r5 = r5 + r13
                    int r10 = r10 + r13
                    float r2 = org.telegram.messenger.AndroidUtilities.cascade(r2, r5, r10, r11)
                    android.view.animation.OvershootInterpolator r5 = r0.appearScaleInterpolator
                    float r2 = r5.getInterpolation(r2)
                    r5 = 1056964608(0x3var_, float:0.5)
                    float r2 = r2 * r5
                    float r2 = r2 + r5
                    float r7 = r7 * r2
                    goto L_0x0165
                L_0x0163:
                    r12 = 1065353216(0x3var_, float:1.0)
                L_0x0165:
                    r2 = 1132396544(0x437var_, float:255.0)
                    float r12 = r12 * r2
                    int r2 = (int) r12
                    r6.setAlpha(r2)
                    r6.setBounds(r8)
                    int r2 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                    if (r2 == 0) goto L_0x018b
                    r20.save()
                    int r2 = r8.centerX()
                    float r2 = (float) r2
                    int r5 = r8.centerY()
                    float r5 = (float) r5
                    r1.scale(r7, r7, r2, r5)
                    r6.draw(r1)
                    r20.restore()
                    goto L_0x018e
                L_0x018b:
                    r6.draw(r1)
                L_0x018e:
                    int r4 = r4 + 1
                    goto L_0x0014
                L_0x0192:
                    r20.restore()
                L_0x0195:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiGridView.DrawingInBackgroundLine.drawInUiThread(android.graphics.Canvas):void");
            }

            public void onFrameReady() {
                super.onFrameReady();
                for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                    ImageViewEmoji imageViewEmoji = this.drawInBackgroundViews.get(i);
                    if (imageViewEmoji.backgroundThreadDrawHolder != null) {
                        imageViewEmoji.backgroundThreadDrawHolder.release();
                    }
                }
                EmojiView.this.emojiGridView.invalidate();
            }
        }

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            View view;
            View view2;
            boolean z = motionEvent.getActionMasked() == 5 || motionEvent.getActionMasked() == 0;
            boolean z2 = motionEvent.getActionMasked() == 6 || motionEvent.getActionMasked() == 1;
            boolean z3 = motionEvent.getActionMasked() == 3;
            if (z || z2 || z3) {
                int actionIndex = motionEvent.getActionIndex();
                int pointerId = motionEvent.getPointerId(actionIndex);
                if (this.touches == null) {
                    this.touches = new HashMap<>();
                }
                float x = motionEvent.getX(actionIndex);
                float y = motionEvent.getY(actionIndex);
                View findChildViewUnder = findChildViewUnder(x, y);
                if (!z) {
                    TouchDownInfo remove = this.touches.remove(Integer.valueOf(pointerId));
                    if (findChildViewUnder != null && remove != null && Math.sqrt(Math.pow((double) (x - remove.x), 2.0d) + Math.pow((double) (y - remove.y), 2.0d)) < ((double) (AndroidUtilities.touchSlop * 3.0f)) && ((float) (SystemClock.elapsedRealtime() - remove.time)) <= ((float) ViewConfiguration.getTapTimeout()) * 1.2f && !z3 && (!EmojiView.this.pickerViewPopup.isShowing() || SystemClock.elapsedRealtime() - remove.time < ((long) ViewConfiguration.getLongPressTimeout()))) {
                        View view3 = remove.view;
                        if (view3 instanceof ImageViewEmoji) {
                            ((ImageViewEmoji) view3).sendEmoji((String) null);
                            try {
                                performHapticFeedback(3, 1);
                            } catch (Exception unused) {
                            }
                        } else if (view3 instanceof EmojiPackExpand) {
                            EmojiPackExpand emojiPackExpand = (EmojiPackExpand) view3;
                            EmojiView.this.emojiAdapter.expand(getChildAdapterPosition(emojiPackExpand), emojiPackExpand);
                            performHapticFeedback(3, 1);
                        } else if (view3 != null) {
                            view3.callOnClick();
                        }
                    }
                    if (remove != null && (view2 = remove.view) != null && Build.VERSION.SDK_INT >= 21 && (view2.getBackground() instanceof RippleDrawable)) {
                        remove.view.getBackground().setState(new int[0]);
                    }
                    if (!(remove == null || (view = remove.view) == null)) {
                        view.setPressed(false);
                    }
                } else if (findChildViewUnder != null) {
                    TouchDownInfo touchDownInfo = new TouchDownInfo(this);
                    touchDownInfo.x = x;
                    touchDownInfo.y = y;
                    touchDownInfo.time = SystemClock.elapsedRealtime();
                    touchDownInfo.view = findChildViewUnder;
                    if (Build.VERSION.SDK_INT >= 21 && (findChildViewUnder.getBackground() instanceof RippleDrawable)) {
                        findChildViewUnder.getBackground().setState(new int[]{16842919, 16842910});
                    }
                    touchDownInfo.view.setPressed(true);
                    this.touches.put(Integer.valueOf(pointerId), touchDownInfo);
                    stopScroll();
                }
            }
            if (super.dispatchTouchEvent(motionEvent) || (!z3 && !this.touches.isEmpty())) {
                return true;
            }
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void createStickersChooseActionTracker() {
        AnonymousClass27 r0 = new ChooseStickerActionTracker(this.currentAccount, this.delegate.getDialogId(), this.delegate.getThreadId()) {
            public boolean isShown() {
                return EmojiView.this.delegate != null && EmojiView.this.getVisibility() == 0 && EmojiView.this.stickersContainerAttached;
            }
        };
        this.chooseStickerActionTracker = r0;
        r0.checkVisibility();
    }

    /* access modifiers changed from: private */
    public void updateEmojiTabsPosition() {
        updateEmojiTabsPosition(this.emojiLayoutManager.findFirstCompletelyVisibleItemPosition());
    }

    /* access modifiers changed from: private */
    public void updateEmojiTabsPosition(int i) {
        int i2 = -1;
        if (i != -1) {
            int size = getRecentEmoji().size() + (this.needEmojiSearch ? 1 : 0);
            int i3 = 0;
            if (i >= size) {
                int i4 = 0;
                while (true) {
                    String[][] strArr = EmojiData.dataColored;
                    if (i4 >= strArr.length) {
                        break;
                    }
                    size += strArr[i4].length + 1;
                    if (i < size) {
                        i2 = i4 + 1;
                        break;
                    }
                    i4++;
                }
                if (i2 < 0) {
                    ArrayList<EmojiPack> emojipacks = getEmojipacks();
                    int size2 = this.emojiAdapter.packStartPosition.size() - 1;
                    while (true) {
                        if (size2 < 0) {
                            break;
                        } else if (((Integer) this.emojiAdapter.packStartPosition.get(size2)).intValue() <= i) {
                            EmojiPack emojiPack = this.emojipacksProcessed.get(size2);
                            while (true) {
                                if (i3 >= emojipacks.size()) {
                                    break;
                                }
                                long j = emojipacks.get(i3).set.id;
                                long j2 = emojiPack.set.id;
                                if (j != j2 || (emojiPack.featured && (emojiPack.installed || this.installedEmojiSets.contains(Long.valueOf(j2))))) {
                                    i3++;
                                }
                            }
                            i3 = EmojiData.dataColored.length + 1 + i3;
                        } else {
                            size2--;
                        }
                    }
                }
                i3 = i2;
            }
            if (!this.emojiSmoothScrolling && i3 >= 0) {
                this.emojiTabs.select(i3);
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkGridVisibility(int i, float f) {
        if (this.stickersContainer != null && this.gifContainer != null) {
            int i2 = 0;
            if (i == 0) {
                this.emojiGridView.setVisibility(0);
                this.gifGridView.setVisibility(f == 0.0f ? 8 : 0);
                ScrollSlidingTabStrip scrollSlidingTabStrip = this.gifTabs;
                if (f == 0.0f) {
                    i2 = 8;
                }
                scrollSlidingTabStrip.setVisibility(i2);
                this.stickersGridView.setVisibility(8);
                this.stickersTabContainer.setVisibility(8);
            } else if (i == 1) {
                this.emojiGridView.setVisibility(8);
                this.gifGridView.setVisibility(0);
                this.gifTabs.setVisibility(0);
                this.stickersGridView.setVisibility(f == 0.0f ? 8 : 0);
                FrameLayout frameLayout = this.stickersTabContainer;
                if (f == 0.0f) {
                    i2 = 8;
                }
                frameLayout.setVisibility(i2);
            } else if (i == 2) {
                this.emojiGridView.setVisibility(8);
                this.gifGridView.setVisibility(8);
                this.gifTabs.setVisibility(8);
                this.stickersGridView.setVisibility(0);
                this.stickersTabContainer.setVisibility(0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void openPremiumAnimatedEmojiFeature() {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null) {
            emojiViewDelegate.onAnimatedEmojiUnlockClick();
        }
    }

    private class EmojiPackButton extends FrameLayout {
        AnimatedTextView addButtonTextView;
        FrameLayout addButtonView;
        PremiumButtonView premiumButtonView;

        public EmojiPackButton(EmojiView emojiView, Context context) {
            super(context);
            AnimatedTextView animatedTextView = new AnimatedTextView(getContext());
            this.addButtonTextView = animatedTextView;
            animatedTextView.setAnimationProperties(0.3f, 0, 250, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.addButtonTextView.setTextSize((float) AndroidUtilities.dp(14.0f));
            this.addButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.addButtonTextView.setTextColor(emojiView.getThemedColor("featuredStickers_buttonText"));
            this.addButtonTextView.setGravity(17);
            FrameLayout frameLayout = new FrameLayout(getContext());
            this.addButtonView = frameLayout;
            frameLayout.setBackground(Theme.AdaptiveRipple.filledRect(emojiView.getThemedColor("featuredStickers_addButton"), 8.0f));
            this.addButtonView.addView(this.addButtonTextView, LayoutHelper.createFrame(-1, -2, 17));
            addView(this.addButtonView, LayoutHelper.createFrame(-1, -1.0f));
            PremiumButtonView premiumButtonView2 = new PremiumButtonView(getContext(), false);
            this.premiumButtonView = premiumButtonView2;
            premiumButtonView2.setIcon(R.raw.unlock_icon);
            addView(this.premiumButtonView, LayoutHelper.createFrame(-1, -1.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(11.0f));
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f) + getPaddingTop() + getPaddingBottom(), NUM));
        }
    }

    private class EmojiPackHeader extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
        TextView addButtonView;
        FrameLayout buttonsView;
        private int currentButtonState;
        boolean divider;
        TextView headerView;
        RLottieImageView lockView;
        private EmojiPack pack;
        PremiumButtonView premiumButtonView;
        TextView removeButtonView;
        private AnimatorSet stateAnimator;
        final /* synthetic */ EmojiView this$0;
        private TLRPC$InputStickerSet toInstall;
        private TLRPC$InputStickerSet toUninstall;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public EmojiPackHeader(org.telegram.ui.Components.EmojiView r17, android.content.Context r18) {
            /*
                r16 = this;
                r0 = r16
                r1 = r17
                r2 = r18
                r0.this$0 = r1
                r0.<init>(r2)
                org.telegram.ui.Components.RLottieImageView r3 = new org.telegram.ui.Components.RLottieImageView
                r3.<init>(r2)
                r0.lockView = r3
                int r4 = org.telegram.messenger.R.raw.unlock_icon
                r5 = 24
                r3.setAnimation((int) r4, (int) r5, (int) r5)
                org.telegram.ui.Components.RLottieImageView r3 = r0.lockView
                java.lang.String r5 = "chat_emojiPanelStickerSetName"
                int r6 = r1.getThemedColor(r5)
                r3.setColorFilter(r6)
                org.telegram.ui.Components.RLottieImageView r3 = r0.lockView
                r6 = 1101004800(0x41a00000, float:20.0)
                r7 = 1101004800(0x41a00000, float:20.0)
                r8 = 8388611(0x800003, float:1.1754948E-38)
                r9 = 1092616192(0x41200000, float:10.0)
                r10 = 1097859072(0x41700000, float:15.0)
                r11 = 0
                r12 = 0
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r6, r7, r8, r9, r10, r11, r12)
                r0.addView(r3, r6)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.headerView = r3
                int r5 = r1.getThemedColor(r5)
                r3.setTextColor(r5)
                android.widget.TextView r3 = r0.headerView
                r5 = 1
                r6 = 1097859072(0x41700000, float:15.0)
                r3.setTextSize(r5, r6)
                android.widget.TextView r3 = r0.headerView
                java.lang.String r6 = "fonts/rmedium.ttf"
                android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
                r3.setTypeface(r7)
                android.widget.TextView r3 = r0.headerView
                org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda6 r7 = new org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda6
                r7.<init>(r0)
                r3.setOnClickListener(r7)
                android.widget.TextView r3 = r0.headerView
                r7 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r8 = -1082130432(0xffffffffbvar_, float:-1.0)
                r9 = 8388611(0x800003, float:1.1754948E-38)
                r11 = 1097859072(0x41700000, float:15.0)
                r12 = 1112539136(0x42500000, float:52.0)
                r13 = 0
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r7, r8, r9, r10, r11, r12, r13)
                r0.addView(r3, r7)
                android.widget.FrameLayout r3 = new android.widget.FrameLayout
                r3.<init>(r2)
                r0.buttonsView = r3
                r7 = 1093664768(0x41300000, float:11.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r10 = 0
                r3.setPadding(r8, r9, r7, r10)
                android.widget.FrameLayout r3 = r0.buttonsView
                r3.setClipToPadding(r10)
                android.widget.FrameLayout r3 = r0.buttonsView
                org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda2 r7 = new org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda2
                r7.<init>(r0)
                r3.setOnClickListener(r7)
                android.widget.FrameLayout r3 = r0.buttonsView
                r7 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r8 = -1082130432(0xffffffffbvar_, float:-1.0)
                r9 = 8388725(0x800075, float:1.1755107E-38)
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r7, r8, r9)
                r0.addView(r3, r8)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.addButtonView = r3
                r8 = 1096810496(0x41600000, float:14.0)
                r3.setTextSize(r5, r8)
                android.widget.TextView r3 = r0.addButtonView
                android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
                r3.setTypeface(r9)
                android.widget.TextView r3 = r0.addButtonView
                int r9 = org.telegram.messenger.R.string.Add
                java.lang.String r11 = "Add"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
                r3.setText(r9)
                android.widget.TextView r3 = r0.addButtonView
                java.lang.String r9 = "featuredStickers_buttonText"
                int r9 = r1.getThemedColor(r9)
                r3.setTextColor(r9)
                android.widget.TextView r3 = r0.addButtonView
                java.lang.String r9 = "featuredStickers_addButton"
                int r11 = r1.getThemedColor(r9)
                java.lang.String r12 = "featuredStickers_addButtonPressed"
                int r12 = r1.getThemedColor(r12)
                float[] r13 = new float[r5]
                r14 = 1098907648(0x41800000, float:16.0)
                r13[r10] = r14
                android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.AdaptiveRipple.createRect((int) r11, (int) r12, (float[]) r13)
                r3.setBackground(r11)
                android.widget.TextView r3 = r0.addButtonView
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r3.setPadding(r11, r10, r12, r10)
                android.widget.TextView r3 = r0.addButtonView
                r11 = 17
                r3.setGravity(r11)
                android.widget.TextView r3 = r0.addButtonView
                org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda1 r12 = new org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda1
                r12.<init>(r0)
                r3.setOnClickListener(r12)
                android.widget.FrameLayout r3 = r0.buttonsView
                android.widget.TextView r12 = r0.addButtonView
                r13 = 1104150528(0x41d00000, float:26.0)
                r15 = 8388661(0x800035, float:1.1755018E-38)
                android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r7, r13, r15)
                r3.addView(r12, r11)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                r0.removeButtonView = r3
                r3.setTextSize(r5, r8)
                android.widget.TextView r3 = r0.removeButtonView
                android.graphics.Typeface r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
                r3.setTypeface(r6)
                android.widget.TextView r3 = r0.removeButtonView
                int r6 = org.telegram.messenger.R.string.StickersRemove
                java.lang.String r8 = "StickersRemove"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
                r3.setText(r6)
                android.widget.TextView r3 = r0.removeButtonView
                java.lang.String r6 = "featuredStickers_removeButtonText"
                int r6 = r1.getThemedColor(r6)
                r3.setTextColor(r6)
                android.widget.TextView r3 = r0.removeButtonView
                int r1 = r1.getThemedColor(r9)
                r6 = 452984831(0x1affffff, float:1.0587911E-22)
                r1 = r1 & r6
                float[] r5 = new float[r5]
                r5[r10] = r14
                android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.AdaptiveRipple.createRect((int) r10, (int) r1, (float[]) r5)
                r3.setBackground(r1)
                android.widget.TextView r1 = r0.removeButtonView
                r3 = 1094713344(0x41400000, float:12.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r1.setPadding(r5, r10, r3, r10)
                android.widget.TextView r1 = r0.removeButtonView
                r3 = 17
                r1.setGravity(r3)
                android.widget.TextView r1 = r0.removeButtonView
                r3 = 1082130432(0x40800000, float:4.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                r1.setTranslationX(r3)
                android.widget.TextView r1 = r0.removeButtonView
                org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda0
                r3.<init>(r0)
                r1.setOnClickListener(r3)
                android.widget.FrameLayout r1 = r0.buttonsView
                android.widget.TextView r3 = r0.removeButtonView
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r7, r13, r15)
                r1.addView(r3, r5)
                org.telegram.ui.Components.Premium.PremiumButtonView r1 = new org.telegram.ui.Components.Premium.PremiumButtonView
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r1.<init>(r2, r3, r10)
                r0.premiumButtonView = r1
                r1.setIcon(r4)
                org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.premiumButtonView
                int r2 = org.telegram.messenger.R.string.Unlock
                java.lang.String r3 = "Unlock"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda4 r3 = new org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda4
                r3.<init>(r0)
                r1.setButton(r2, r3)
                org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.premiumButtonView     // Catch:{ Exception -> 0x0212 }
                org.telegram.ui.Components.RLottieImageView r1 = r1.getIconView()     // Catch:{ Exception -> 0x0212 }
                android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()     // Catch:{ Exception -> 0x0212 }
                android.view.ViewGroup$MarginLayoutParams r1 = (android.view.ViewGroup.MarginLayoutParams) r1     // Catch:{ Exception -> 0x0212 }
                r2 = 1065353216(0x3var_, float:1.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x0212 }
                r1.leftMargin = r3     // Catch:{ Exception -> 0x0212 }
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x0212 }
                r1.topMargin = r2     // Catch:{ Exception -> 0x0212 }
                r2 = 1101004800(0x41a00000, float:20.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x0212 }
                r1.height = r2     // Catch:{ Exception -> 0x0212 }
                r1.width = r2     // Catch:{ Exception -> 0x0212 }
                org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.premiumButtonView     // Catch:{ Exception -> 0x0212 }
                org.telegram.ui.Components.AnimatedTextView r1 = r1.getTextView()     // Catch:{ Exception -> 0x0212 }
                android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()     // Catch:{ Exception -> 0x0212 }
                android.view.ViewGroup$MarginLayoutParams r1 = (android.view.ViewGroup.MarginLayoutParams) r1     // Catch:{ Exception -> 0x0212 }
                r2 = 1084227584(0x40a00000, float:5.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x0212 }
                r1.leftMargin = r2     // Catch:{ Exception -> 0x0212 }
                r2 = -1090519040(0xffffffffbvar_, float:-0.5)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x0212 }
                r1.topMargin = r2     // Catch:{ Exception -> 0x0212 }
                org.telegram.ui.Components.Premium.PremiumButtonView r1 = r0.premiumButtonView     // Catch:{ Exception -> 0x0212 }
                android.view.View r1 = r1.getChildAt(r10)     // Catch:{ Exception -> 0x0212 }
                r2 = 1090519040(0x41000000, float:8.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x0212 }
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x0212 }
                r1.setPadding(r3, r10, r2, r10)     // Catch:{ Exception -> 0x0212 }
            L_0x0212:
                android.widget.FrameLayout r1 = r0.buttonsView
                org.telegram.ui.Components.Premium.PremiumButtonView r2 = r0.premiumButtonView
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrameRelatively(r7, r13, r15)
                r1.addView(r2, r3)
                r0.setWillNotDraw(r10)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiPackHeader.<init>(org.telegram.ui.Components.EmojiView, android.content.Context):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            TLRPC$StickerSet tLRPC$StickerSet;
            EmojiPack emojiPack = this.pack;
            if (emojiPack != null && (tLRPC$StickerSet = emojiPack.set) != null) {
                this.this$0.openEmojiPackAlert(tLRPC$StickerSet);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            TextView textView = this.addButtonView;
            if (textView == null || textView.getVisibility() != 0 || !this.addButtonView.isEnabled()) {
                TextView textView2 = this.removeButtonView;
                if (textView2 == null || textView2.getVisibility() != 0 || !this.removeButtonView.isEnabled()) {
                    PremiumButtonView premiumButtonView2 = this.premiumButtonView;
                    if (premiumButtonView2 != null && premiumButtonView2.getVisibility() == 0 && this.premiumButtonView.isEnabled()) {
                        this.premiumButtonView.performClick();
                        return;
                    }
                    return;
                }
                this.removeButtonView.performClick();
                return;
            }
            this.addButtonView.performClick();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(View view) {
            TLRPC$StickerSet tLRPC$StickerSet;
            Integer num;
            View view2;
            View childAt;
            int childAdapterPosition;
            int i;
            EmojiPack emojiPack = this.pack;
            if (emojiPack != null && (tLRPC$StickerSet = emojiPack.set) != null) {
                emojiPack.installed = true;
                if (!this.this$0.installedEmojiSets.contains(Long.valueOf(tLRPC$StickerSet.id))) {
                    this.this$0.installedEmojiSets.add(Long.valueOf(this.pack.set.id));
                }
                updateState(true);
                int i2 = 0;
                while (true) {
                    num = null;
                    if (i2 < this.this$0.emojiGridView.getChildCount()) {
                        if ((this.this$0.emojiGridView.getChildAt(i2) instanceof EmojiPackExpand) && (childAdapterPosition = this.this$0.emojiGridView.getChildAdapterPosition(childAt)) >= 0 && (i = this.this$0.emojiAdapter.positionToExpand.get(childAdapterPosition)) >= 0 && i < this.this$0.emojipacksProcessed.size() && this.this$0.emojipacksProcessed.get(i) != null && this.pack != null && ((EmojiPack) this.this$0.emojipacksProcessed.get(i)).set.id == this.pack.set.id) {
                            View childAt2 = this.this$0.emojiGridView.getChildAt(i2);
                            num = Integer.valueOf(childAdapterPosition);
                            view2 = childAt2;
                            break;
                        }
                        i2++;
                    } else {
                        view2 = null;
                        break;
                    }
                }
                if (num != null) {
                    this.this$0.emojiAdapter.expand(num.intValue(), view2);
                }
                if (this.toInstall == null) {
                    TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                    TLRPC$StickerSet tLRPC$StickerSet2 = this.pack.set;
                    tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet2.id;
                    tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet2.access_hash;
                    TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.this$0.currentAccount).getStickerSet(tLRPC$TL_inputStickerSetID, true);
                    if (stickerSet == null || stickerSet.set == null) {
                        NotificationCenter.getInstance(this.this$0.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
                        MediaDataController instance = MediaDataController.getInstance(this.this$0.currentAccount);
                        this.toInstall = tLRPC$TL_inputStickerSetID;
                        instance.getStickerSet(tLRPC$TL_inputStickerSetID, false);
                        return;
                    }
                    install(stickerSet);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(View view) {
            TLRPC$StickerSet tLRPC$StickerSet;
            EmojiPack emojiPack = this.pack;
            if (emojiPack != null && (tLRPC$StickerSet = emojiPack.set) != null) {
                emojiPack.installed = false;
                this.this$0.installedEmojiSets.remove(Long.valueOf(tLRPC$StickerSet.id));
                updateState(true);
                if (this.this$0.emojiTabs != null) {
                    this.this$0.emojiTabs.updateEmojiPacks();
                }
                this.this$0.updateEmojiTabsPosition();
                if (this.toUninstall == null) {
                    TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                    TLRPC$StickerSet tLRPC$StickerSet2 = this.pack.set;
                    tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet2.id;
                    tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet2.access_hash;
                    TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.this$0.currentAccount).getStickerSet(tLRPC$TL_inputStickerSetID, true);
                    if (stickerSet == null || stickerSet.set == null) {
                        NotificationCenter.getInstance(this.this$0.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
                        MediaDataController instance = MediaDataController.getInstance(this.this$0.currentAccount);
                        this.toUninstall = tLRPC$TL_inputStickerSetID;
                        instance.getStickerSet(tLRPC$TL_inputStickerSetID, false);
                        return;
                    }
                    uninstall(stickerSet);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(View view) {
            this.this$0.openPremiumAnimatedEmojiFeature();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            ((ViewGroup.MarginLayoutParams) this.headerView.getLayoutParams()).topMargin = AndroidUtilities.dp(this.currentButtonState == 0 ? 10.0f : 15.0f);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.currentButtonState == 0 ? 32.0f : 42.0f), NUM));
        }

        public void setStickerSet(EmojiPack emojiPack, boolean z) {
            if (emojiPack != null) {
                this.pack = emojiPack;
                this.divider = z;
                this.headerView.setText(emojiPack.set.title);
                if (!emojiPack.installed || emojiPack.set.official) {
                    this.premiumButtonView.setButton(LocaleController.getString("Unlock", R.string.Unlock), new EmojiView$EmojiPackHeader$$ExternalSyntheticLambda3(this));
                } else {
                    this.premiumButtonView.setButton(LocaleController.getString("Restore", R.string.Restore), new EmojiView$EmojiPackHeader$$ExternalSyntheticLambda5(this));
                }
                updateState(false);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setStickerSet$5(View view) {
            this.this$0.openPremiumAnimatedEmojiFeature();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setStickerSet$6(View view) {
            this.this$0.openPremiumAnimatedEmojiFeature();
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            TLRPC$TL_messages_stickerSet stickerSetById;
            TLRPC$TL_messages_stickerSet stickerSetById2;
            if (i == NotificationCenter.groupStickersDidLoad) {
                if (!(this.toInstall == null || (stickerSetById2 = MediaDataController.getInstance(this.this$0.currentAccount).getStickerSetById(this.toInstall.id)) == null || stickerSetById2.set == null)) {
                    install(stickerSetById2);
                    this.toInstall = null;
                }
                if (this.toUninstall != null && (stickerSetById = MediaDataController.getInstance(this.this$0.currentAccount).getStickerSetById(this.toUninstall.id)) != null && stickerSetById.set != null) {
                    uninstall(stickerSetById);
                    this.toUninstall = null;
                }
            }
        }

        private BaseFragment getFragment() {
            if (this.this$0.fragment != null) {
                return this.this$0.fragment;
            }
            return new BaseFragment() {
                public int getCurrentAccount() {
                    return EmojiPackHeader.this.this$0.currentAccount;
                }

                public View getFragmentView() {
                    return EmojiPackHeader.this.this$0.bulletinContainer;
                }

                public FrameLayout getLayoutContainer() {
                    return EmojiPackHeader.this.this$0.bulletinContainer;
                }

                public Theme.ResourcesProvider getResourceProvider() {
                    return EmojiPackHeader.this.this$0.resourcesProvider;
                }
            };
        }

        private void install(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
            EmojiPacksAlert.installSet(getFragment(), tLRPC$TL_messages_stickerSet, true, (Utilities.Callback<Boolean>) null, new EmojiView$EmojiPackHeader$$ExternalSyntheticLambda7(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$install$7() {
            this.pack.installed = true;
            updateState(true);
        }

        private void uninstall(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
            EmojiPacksAlert.uninstallSet(getFragment(), tLRPC$TL_messages_stickerSet, true, new EmojiView$EmojiPackHeader$$ExternalSyntheticLambda8(this, tLRPC$TL_messages_stickerSet));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$uninstall$8(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
            this.pack.installed = true;
            if (!this.this$0.installedEmojiSets.contains(Long.valueOf(tLRPC$TL_messages_stickerSet.set.id))) {
                this.this$0.installedEmojiSets.add(Long.valueOf(tLRPC$TL_messages_stickerSet.set.id));
            }
            updateState(true);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(this.this$0.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.divider) {
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), 1.0f, Theme.dividerPaint);
            }
            super.onDraw(canvas);
        }

        public void updateState(boolean z) {
            EmojiPack emojiPack = this.pack;
            if (emojiPack != null) {
                int i = 1;
                boolean z2 = emojiPack.installed || this.this$0.installedEmojiSets.contains(Long.valueOf(emojiPack.set.id));
                if (this.pack.free || UserConfig.getInstance(this.this$0.currentAccount).isPremium()) {
                    i = this.pack.featured ? z2 ? 3 : 2 : 0;
                }
                updateState(i, z);
            }
        }

        public void updateState(int i, boolean z) {
            float f;
            final int i2 = i;
            int i3 = 0;
            if ((i2 == 0) != (this.currentButtonState == 0)) {
                requestLayout();
            }
            this.currentButtonState = i2;
            AnimatorSet animatorSet = this.stateAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.stateAnimator = null;
            }
            this.premiumButtonView.setEnabled(i2 == 1);
            this.addButtonView.setEnabled(i2 == 2);
            this.removeButtonView.setEnabled(i2 == 3);
            float f2 = 0.0f;
            if (z) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.stateAnimator = animatorSet2;
                Animator[] animatorArr = new Animator[12];
                RLottieImageView rLottieImageView = this.lockView;
                Property property = FrameLayout.TRANSLATION_X;
                float[] fArr = new float[1];
                if (i2 == 1) {
                    f = 0.0f;
                } else {
                    f = (float) (-AndroidUtilities.dp(16.0f));
                }
                fArr[0] = f;
                animatorArr[0] = ObjectAnimator.ofFloat(rLottieImageView, property, fArr);
                RLottieImageView rLottieImageView2 = this.lockView;
                Property property2 = FrameLayout.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = i2 == 1 ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(rLottieImageView2, property2, fArr2);
                TextView textView = this.headerView;
                Property property3 = FrameLayout.TRANSLATION_X;
                float[] fArr3 = new float[1];
                fArr3[0] = i2 == 1 ? (float) AndroidUtilities.dp(16.0f) : 0.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(textView, property3, fArr3);
                PremiumButtonView premiumButtonView2 = this.premiumButtonView;
                Property property4 = FrameLayout.ALPHA;
                float[] fArr4 = new float[1];
                fArr4[0] = i2 == 1 ? 1.0f : 0.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(premiumButtonView2, property4, fArr4);
                PremiumButtonView premiumButtonView3 = this.premiumButtonView;
                Property property5 = FrameLayout.SCALE_X;
                float[] fArr5 = new float[1];
                fArr5[0] = i2 == 1 ? 1.0f : 0.6f;
                animatorArr[4] = ObjectAnimator.ofFloat(premiumButtonView3, property5, fArr5);
                PremiumButtonView premiumButtonView4 = this.premiumButtonView;
                Property property6 = FrameLayout.SCALE_Y;
                float[] fArr6 = new float[1];
                fArr6[0] = i2 == 1 ? 1.0f : 0.6f;
                animatorArr[5] = ObjectAnimator.ofFloat(premiumButtonView4, property6, fArr6);
                TextView textView2 = this.addButtonView;
                Property property7 = FrameLayout.ALPHA;
                float[] fArr7 = new float[1];
                fArr7[0] = i2 == 2 ? 1.0f : 0.0f;
                animatorArr[6] = ObjectAnimator.ofFloat(textView2, property7, fArr7);
                TextView textView3 = this.addButtonView;
                Property property8 = FrameLayout.SCALE_X;
                float[] fArr8 = new float[1];
                fArr8[0] = i2 == 2 ? 1.0f : 0.6f;
                animatorArr[7] = ObjectAnimator.ofFloat(textView3, property8, fArr8);
                TextView textView4 = this.addButtonView;
                Property property9 = FrameLayout.SCALE_Y;
                float[] fArr9 = new float[1];
                fArr9[0] = i2 == 2 ? 1.0f : 0.6f;
                animatorArr[8] = ObjectAnimator.ofFloat(textView4, property9, fArr9);
                TextView textView5 = this.removeButtonView;
                Property property10 = FrameLayout.ALPHA;
                float[] fArr10 = new float[1];
                if (i2 == 3) {
                    f2 = 1.0f;
                }
                fArr10[0] = f2;
                animatorArr[9] = ObjectAnimator.ofFloat(textView5, property10, fArr10);
                TextView textView6 = this.removeButtonView;
                Property property11 = FrameLayout.SCALE_X;
                float[] fArr11 = new float[1];
                fArr11[0] = i2 == 3 ? 1.0f : 0.6f;
                animatorArr[10] = ObjectAnimator.ofFloat(textView6, property11, fArr11);
                TextView textView7 = this.removeButtonView;
                Property property12 = FrameLayout.SCALE_Y;
                float[] fArr12 = new float[1];
                fArr12[0] = i2 == 3 ? 1.0f : 0.6f;
                animatorArr[11] = ObjectAnimator.ofFloat(textView7, property12, fArr12);
                animatorSet2.playTogether(animatorArr);
                this.stateAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        EmojiPackHeader.this.premiumButtonView.setVisibility(0);
                        EmojiPackHeader.this.addButtonView.setVisibility(0);
                        EmojiPackHeader.this.removeButtonView.setVisibility(0);
                    }

                    public void onAnimationEnd(Animator animator) {
                        int i = 0;
                        EmojiPackHeader.this.premiumButtonView.setVisibility(i2 == 1 ? 0 : 8);
                        EmojiPackHeader.this.addButtonView.setVisibility(i2 == 2 ? 0 : 8);
                        TextView textView = EmojiPackHeader.this.removeButtonView;
                        if (i2 != 3) {
                            i = 8;
                        }
                        textView.setVisibility(i);
                    }
                });
                this.stateAnimator.setDuration(250);
                this.stateAnimator.setInterpolator(new OvershootInterpolator(1.02f));
                this.stateAnimator.start();
                return;
            }
            this.lockView.setAlpha(i2 == 1 ? 1.0f : 0.0f);
            this.lockView.setTranslationX(i2 == 1 ? 0.0f : (float) (-AndroidUtilities.dp(16.0f)));
            this.headerView.setTranslationX(i2 == 1 ? (float) AndroidUtilities.dp(16.0f) : 0.0f);
            this.premiumButtonView.setAlpha(i2 == 1 ? 1.0f : 0.0f);
            this.premiumButtonView.setScaleX(i2 == 1 ? 1.0f : 0.6f);
            this.premiumButtonView.setScaleY(i2 == 1 ? 1.0f : 0.6f);
            this.premiumButtonView.setVisibility(i2 == 1 ? 0 : 8);
            this.addButtonView.setAlpha(i2 == 2 ? 1.0f : 0.0f);
            this.addButtonView.setScaleX(i2 == 2 ? 1.0f : 0.6f);
            this.addButtonView.setScaleY(i2 == 2 ? 1.0f : 0.6f);
            this.addButtonView.setVisibility(i2 == 2 ? 0 : 8);
            TextView textView8 = this.removeButtonView;
            if (i2 == 3) {
                f2 = 1.0f;
            }
            textView8.setAlpha(f2);
            this.removeButtonView.setScaleX(i2 == 3 ? 1.0f : 0.6f);
            this.removeButtonView.setScaleY(i2 == 3 ? 1.0f : 0.6f);
            TextView textView9 = this.removeButtonView;
            if (i2 != 3) {
                i3 = 8;
            }
            textView9.setVisibility(i3);
        }
    }

    public void openEmojiPackAlert(TLRPC$StickerSet tLRPC$StickerSet) {
        if (!this.emojiPackAlertOpened) {
            this.emojiPackAlertOpened = true;
            ArrayList arrayList = new ArrayList(1);
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
            tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
            tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
            arrayList.add(tLRPC$TL_inputStickerSetID);
            final TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$StickerSet;
            new EmojiPacksAlert(this.fragment, getContext(), this.resourcesProvider, arrayList) {
                public void dismiss() {
                    boolean unused = EmojiView.this.emojiPackAlertOpened = false;
                    super.dismiss();
                }

                /* access modifiers changed from: protected */
                public void onButtonClicked(boolean z) {
                    if (!z) {
                        EmojiView.this.installedEmojiSets.remove(Long.valueOf(tLRPC$StickerSet2.id));
                    } else if (!EmojiView.this.installedEmojiSets.contains(Long.valueOf(tLRPC$StickerSet2.id))) {
                        EmojiView.this.installedEmojiSets.add(Long.valueOf(tLRPC$StickerSet2.id));
                    }
                    EmojiView.this.updateEmojiHeaders();
                }
            }.show();
        }
    }

    public class EmojiGridSpacing extends RecyclerView.ItemDecoration {
        public EmojiGridSpacing() {
        }

        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            if (view instanceof StickerSetNameCell) {
                rect.left = AndroidUtilities.dp(5.0f);
                rect.right = AndroidUtilities.dp(5.0f);
                if (recyclerView.getChildAdapterPosition(view) + 1 > EmojiView.this.emojiAdapter.plainEmojisCount && !UserConfig.getInstance(EmojiView.this.currentAccount).isPremium()) {
                    rect.top = AndroidUtilities.dp(10.0f);
                }
            } else if ((view instanceof RecyclerListView) || (view instanceof EmojiPackHeader)) {
                rect.left = -EmojiView.this.emojiGridView.getPaddingLeft();
                rect.right = -EmojiView.this.emojiGridView.getPaddingRight();
                if (view instanceof EmojiPackHeader) {
                    rect.top = AndroidUtilities.dp(8.0f);
                } else {
                    rect.bottom = AndroidUtilities.dp(6.0f);
                }
            } else if (view instanceof BackupImageView) {
                rect.bottom = AndroidUtilities.dp(12.0f);
            }
        }
    }

    /* access modifiers changed from: private */
    public static String addColorToCode(String str, String str2) {
        String str3;
        int length = str.length();
        if (length > 2 && str.charAt(str.length() - 2) == 8205) {
            str3 = str.substring(str.length() - 2);
            str = str.substring(0, str.length() - 2);
        } else if (length <= 3 || str.charAt(str.length() - 3) != 8205) {
            str3 = null;
        } else {
            str3 = str.substring(str.length() - 3);
            str = str.substring(0, str.length() - 3);
        }
        String str4 = str + str2;
        if (str3 == null) {
            return str4;
        }
        return str4 + str3;
    }

    /* access modifiers changed from: private */
    public void openTrendingStickers(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
        this.delegate.showTrendingStickersAlert(new TrendingStickersLayout(getContext(), new TrendingStickersLayout.Delegate() {
            public boolean canSendSticker() {
                return true;
            }

            public void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z) {
                EmojiView.this.delegate.onStickerSetAdd(tLRPC$StickerSetCovered);
                if (z) {
                    EmojiView.this.updateStickerTabs();
                }
            }

            public void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
                EmojiView.this.delegate.onStickerSetRemove(tLRPC$StickerSetCovered);
            }

            public boolean onListViewInterceptTouchEvent(RecyclerListView recyclerListView, MotionEvent motionEvent) {
                return ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, recyclerListView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
            }

            public boolean onListViewTouchEvent(RecyclerListView recyclerListView, RecyclerListView.OnItemClickListener onItemClickListener, MotionEvent motionEvent) {
                return ContentPreviewViewer.getInstance().onTouch(motionEvent, recyclerListView, EmojiView.this.getMeasuredHeight(), onItemClickListener, EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
            }

            public String[] getLastSearchKeyboardLanguage() {
                return EmojiView.this.lastSearchKeyboardLanguage;
            }

            public void setLastSearchKeyboardLanguage(String[] strArr) {
                String[] unused = EmojiView.this.lastSearchKeyboardLanguage = strArr;
            }

            public void onStickerSelected(TLRPC$Document tLRPC$Document, Object obj, boolean z, boolean z2, int i) {
                EmojiView.this.delegate.onStickerSelected((View) null, tLRPC$Document, (String) null, obj, (MessageObject.SendAnimationData) null, z2, i);
            }

            public boolean canSchedule() {
                return EmojiView.this.delegate.canSchedule();
            }

            public boolean isInScheduleMode() {
                return EmojiView.this.delegate.isInScheduleMode();
            }
        }, this.primaryInstallingStickerSets, this.installingStickerSets, this.removingStickerSets, tLRPC$StickerSetCovered, this.resourcesProvider));
    }

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        updateStickerTabsPosition();
        updateBottomTabContainerPosition();
    }

    private void updateBottomTabContainerPosition() {
        View view;
        int i;
        if (this.bottomTabContainer.getTag() == null) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                ViewPager viewPager = this.pager;
                if ((viewPager == null || viewPager.getCurrentItem() != 0) && (view = (View) getParent()) != null) {
                    float y = getY() - ((float) view.getHeight());
                    if (getLayoutParams().height > 0) {
                        i = getLayoutParams().height;
                    } else {
                        i = getMeasuredHeight();
                    }
                    float f = y + ((float) i);
                    if (((float) this.bottomTabContainer.getTop()) - f < 0.0f) {
                        f = (float) this.bottomTabContainer.getTop();
                    }
                    float f2 = -f;
                    this.bottomTabContainer.setTranslationY(f2);
                    if (this.needEmojiSearch) {
                        this.bulletinContainer.setTranslationY(f2);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateStickerTabsPosition() {
        if (this.stickersTabContainer != null) {
            boolean z = getVisibility() == 0 && this.stickersContainerAttached && this.delegate.getProgressToSearchOpened() != 1.0f;
            this.stickersTabContainer.setVisibility(z ? 0 : 8);
            if (z) {
                this.rect.setEmpty();
                this.pager.getChildVisibleRect(this.stickersContainer, this.rect, (Point) null);
                float dp = ((float) AndroidUtilities.dp(50.0f)) * this.delegate.getProgressToSearchOpened();
                int i = this.rect.left;
                if (!(i == 0 && dp == 0.0f)) {
                    this.expandStickersByDragg = false;
                }
                this.stickersTabContainer.setTranslationX((float) i);
                float top = (((((float) getTop()) + getTranslationY()) - ((float) this.stickersTabContainer.getTop())) - this.stickersTab.getExpandedOffset()) - dp;
                if (this.stickersTabContainer.getTranslationY() != top) {
                    this.stickersTabContainer.setTranslationY(top);
                    this.stickersTabContainer.invalidate();
                }
            }
            if (!this.expandStickersByDragg || !z || !this.showing) {
                this.expandStickersByDragg = false;
                this.stickersTab.expandStickers(this.lastStickersX, false);
                return;
            }
            this.stickersTab.expandStickers(this.lastStickersX, true);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        updateBottomTabContainerPosition();
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: private */
    public void startStopVisibleGifs(boolean z) {
        RecyclerListView recyclerListView = this.gifGridView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.gifGridView.getChildAt(i);
                if (childAt instanceof ContextLinkCell) {
                    ImageReceiver photoImage = ((ContextLinkCell) childAt).getPhotoImage();
                    if (z) {
                        photoImage.setAllowStartAnimation(true);
                        photoImage.startAnimation();
                    } else {
                        photoImage.setAllowStartAnimation(false);
                        photoImage.stopAnimation();
                    }
                }
            }
        }
    }

    public ArrayList<String> getRecentEmoji() {
        if (this.allowAnimatedEmoji) {
            return Emoji.recentEmoji;
        }
        if (this.lastRecentArray == null) {
            this.lastRecentArray = new ArrayList<>();
        }
        if (Emoji.recentEmoji.size() != this.lastRecentCount) {
            this.lastRecentArray.clear();
            for (int i = 0; i < Emoji.recentEmoji.size(); i++) {
                if (!Emoji.recentEmoji.get(i).startsWith("animated_")) {
                    this.lastRecentArray.add(Emoji.recentEmoji.get(i));
                }
            }
            this.lastRecentCount = this.lastRecentArray.size();
        }
        return this.lastRecentArray;
    }

    public void addEmojiToRecent(String str) {
        if (str == null) {
            return;
        }
        if (str.startsWith("animated_") || Emoji.isValidEmoji(str)) {
            Emoji.addRecentEmoji(str);
            if (!(getVisibility() == 0 && this.pager.getCurrentItem() == 0)) {
                Emoji.sortEmoji();
                this.emojiAdapter.notifyDataSetChanged();
            }
            Emoji.saveRecentEmoji();
            if (!this.allowAnimatedEmoji) {
                ArrayList<String> arrayList = this.lastRecentArray;
                if (arrayList == null) {
                    this.lastRecentArray = new ArrayList<>();
                } else {
                    arrayList.clear();
                }
                for (int i = 0; i < Emoji.recentEmoji.size(); i++) {
                    if (!Emoji.recentEmoji.get(i).startsWith("animated_")) {
                        this.lastRecentArray.add(Emoji.recentEmoji.get(i));
                    }
                }
                this.lastRecentCount = this.lastRecentArray.size();
            }
        }
    }

    public void showSearchField(boolean z) {
        for (int i = 0; i < 3; i++) {
            GridLayoutManager layoutManagerForType = getLayoutManagerForType(i);
            int findFirstVisibleItemPosition = layoutManagerForType.findFirstVisibleItemPosition();
            if (z) {
                if (findFirstVisibleItemPosition == 1 || findFirstVisibleItemPosition == 2) {
                    layoutManagerForType.scrollToPosition(0);
                    resetTabsY(i);
                }
            } else if (findFirstVisibleItemPosition == 0) {
                layoutManagerForType.scrollToPositionWithOffset(1, 0);
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
        LinearLayoutManager linearLayoutManager;
        View view;
        final RecyclerListView recyclerListView;
        SearchField searchField2;
        EmojiViewDelegate emojiViewDelegate;
        AnimatorSet animatorSet = this.searchAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        this.firstStickersAttach = false;
        this.firstGifAttach = false;
        this.firstEmojiAttach = false;
        for (int i = 0; i < 3; i++) {
            boolean z = true;
            if (i == 0) {
                searchField2 = this.emojiSearchField;
                recyclerListView = this.emojiGridView;
                view = this.emojiTabs;
                linearLayoutManager = this.emojiLayoutManager;
            } else if (i == 1) {
                searchField2 = this.gifSearchField;
                recyclerListView = this.gifGridView;
                view = this.gifTabs;
                linearLayoutManager = this.gifLayoutManager;
            } else {
                searchField2 = this.stickersSearchField;
                recyclerListView = this.stickersGridView;
                view = this.stickersTab;
                linearLayoutManager = this.stickersLayoutManager;
            }
            if (searchField2 != null) {
                if (searchField != searchField2 || (emojiViewDelegate = this.delegate) == null || !emojiViewDelegate.isExpanded()) {
                    searchField2.setTranslationY((float) AndroidUtilities.dp(0.0f));
                    if (!(view == null || i == 2)) {
                        view.setTranslationY((float) (-AndroidUtilities.dp(40.0f)));
                    }
                    if (recyclerListView == this.stickersGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                    } else if (recyclerListView == this.emojiGridView || recyclerListView == this.gifGridView) {
                        recyclerListView.setPadding(0, 0, 0, 0);
                    }
                    if (recyclerListView == this.gifGridView) {
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
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                } else {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (view == null || i == 2) {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(36.0f))}), ObjectAnimator.ofFloat(searchField2, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)})});
                    } else {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(40.0f))}), ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(36.0f))}), ObjectAnimator.ofFloat(searchField2, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)})});
                    }
                    this.searchAnimation.setDuration(220);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                recyclerListView.setTranslationY(0.0f);
                                if (recyclerListView == EmojiView.this.stickersGridView) {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                                } else if (recyclerListView == EmojiView.this.emojiGridView || recyclerListView == EmojiView.this.gifGridView) {
                                    recyclerListView.setPadding(0, 0, 0, 0);
                                }
                                AnimatorSet unused = EmojiView.this.searchAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                AnimatorSet unused = EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                }
            }
        }
    }

    private void showEmojiShadow(boolean z, boolean z2) {
        if (z && this.emojiTabsShadow.getTag() == null) {
            return;
        }
        if (z || this.emojiTabsShadow.getTag() == null) {
            AnimatorSet animatorSet = this.emojiTabShadowAnimator;
            int i = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.emojiTabShadowAnimator = null;
            }
            View view = this.emojiTabsShadow;
            if (!z) {
                i = 1;
            }
            view.setTag(i);
            float f = 1.0f;
            if (z2) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.emojiTabShadowAnimator = animatorSet2;
                Animator[] animatorArr = new Animator[1];
                View view2 = this.emojiTabsShadow;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr[0] = f;
                animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                animatorSet2.playTogether(animatorArr);
                this.emojiTabShadowAnimator.setDuration(200);
                this.emojiTabShadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.emojiTabShadowAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = EmojiView.this.emojiTabShadowAnimator = null;
                    }
                });
                this.emojiTabShadowAnimator.start();
                return;
            }
            View view3 = this.emojiTabsShadow;
            if (!z) {
                f = 0.0f;
            }
            view3.setAlpha(f);
        }
    }

    public void closeSearch(boolean z) {
        closeSearch(z, -1);
    }

    private void scrollStickersToPosition(int i, int i2) {
        View findViewByPosition = this.stickersLayoutManager.findViewByPosition(i);
        int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (findViewByPosition != null || Math.abs(i - findFirstVisibleItemPosition) <= 40) {
            this.ignoreStickersScroll = true;
            this.stickersGridView.smoothScrollToPosition(i);
            return;
        }
        this.stickersScrollHelper.setScrollDirection(this.stickersLayoutManager.findFirstVisibleItemPosition() < i ? 0 : 1);
        this.stickersScrollHelper.scrollToPosition(i, i2, false, true);
    }

    public void scrollEmojisToAnimated() {
        if (!this.emojiSmoothScrolling) {
            try {
                int i = this.emojiAdapter.sectionToPosition.get(EmojiData.dataColored.length);
                if (i > 0) {
                    this.emojiGridView.stopScroll();
                    updateEmojiTabsPosition(i);
                    scrollEmojisToPosition(i, AndroidUtilities.dp(-9.0f));
                    checkEmojiTabY((View) null, 0);
                }
            } catch (Exception unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void scrollEmojisToPosition(int i, int i2) {
        View findViewByPosition = this.emojiLayoutManager.findViewByPosition(i);
        int findFirstVisibleItemPosition = this.emojiLayoutManager.findFirstVisibleItemPosition();
        if ((findViewByPosition != null || ((float) Math.abs(i - findFirstVisibleItemPosition)) <= ((float) this.emojiLayoutManager.getSpanCount()) * 9.0f) && SharedConfig.animationsEnabled()) {
            this.ignoreStickersScroll = true;
            AnonymousClass32 r0 = new LinearSmoothScrollerCustom(this.emojiGridView.getContext(), 2) {
                public void onEnd() {
                    boolean unused = EmojiView.this.emojiSmoothScrolling = false;
                }

                /* access modifiers changed from: protected */
                public void onStart() {
                    boolean unused = EmojiView.this.emojiSmoothScrolling = true;
                }
            };
            r0.setTargetPosition(i);
            r0.setOffset(i2);
            this.emojiLayoutManager.startSmoothScroll(r0);
            return;
        }
        this.emojiScrollHelper.setScrollDirection(this.emojiLayoutManager.findFirstVisibleItemPosition() < i ? 0 : 1);
        this.emojiScrollHelper.scrollToPosition(i, i2, false, true);
    }

    public void closeSearch(boolean z, long j) {
        View view;
        final GridLayoutManager gridLayoutManager;
        final RecyclerListView recyclerListView;
        SearchField searchField;
        TLRPC$TL_messages_stickerSet stickerSetById;
        int positionForPack;
        boolean z2 = z;
        long j2 = j;
        AnimatorSet animatorSet = this.searchAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        int currentItem = this.pager.getCurrentItem();
        int i = 2;
        if (currentItem == 2 && j2 != -1 && (stickerSetById = MediaDataController.getInstance(this.currentAccount).getStickerSetById(j2)) != null && (positionForPack = this.stickersGridAdapter.getPositionForPack(stickerSetById)) >= 0 && positionForPack < this.stickersGridAdapter.getItemCount()) {
            scrollStickersToPosition(positionForPack, AndroidUtilities.dp(48.0f));
        }
        GifAdapter gifAdapter2 = this.gifSearchAdapter;
        if (gifAdapter2 != null) {
            boolean unused = gifAdapter2.showTrendingWhenSearchEmpty = false;
        }
        for (int i2 = 0; i2 < 3; i2++) {
            if (i2 == 0) {
                searchField = this.emojiSearchField;
                recyclerListView = this.emojiGridView;
                gridLayoutManager = this.emojiLayoutManager;
                view = this.emojiTabs;
            } else if (i2 == 1) {
                searchField = this.gifSearchField;
                recyclerListView = this.gifGridView;
                gridLayoutManager = this.gifLayoutManager;
                view = this.gifTabs;
            } else {
                searchField = this.stickersSearchField;
                recyclerListView = this.stickersGridView;
                gridLayoutManager = this.stickersLayoutManager;
                view = this.stickersTab;
            }
            if (searchField != null) {
                searchField.searchEditText.setText("");
                if (i2 != currentItem || !z2) {
                    searchField.setTranslationY((float) (AndroidUtilities.dp(36.0f) - this.searchFieldHeight));
                    i = 2;
                    if (!(view == null || i2 == 2)) {
                        view.setTranslationY(0.0f);
                    }
                    if (recyclerListView == this.stickersGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                    } else if (recyclerListView == this.gifGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                    } else if (recyclerListView == this.emojiGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(36.0f), 0, AndroidUtilities.dp(44.0f));
                    }
                    gridLayoutManager.scrollToPositionWithOffset(1, 0);
                } else {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (view == null || i2 == i) {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(36.0f) - this.searchFieldHeight)}), ObjectAnimator.ofFloat(searchField, View.TRANSLATION_Y, new float[]{(float) (-this.searchFieldHeight)})});
                    } else {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(36.0f) - this.searchFieldHeight)}), ObjectAnimator.ofFloat(searchField, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(36.0f) - this.searchFieldHeight)})});
                    }
                    this.searchAnimation.setDuration(200);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                int findFirstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                                int top = findFirstVisibleItemPosition != -1 ? (int) (((float) gridLayoutManager.findViewByPosition(findFirstVisibleItemPosition).getTop()) + recyclerListView.getTranslationY()) : 0;
                                recyclerListView.setTranslationY(0.0f);
                                if (recyclerListView == EmojiView.this.stickersGridView) {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                                } else if (recyclerListView == EmojiView.this.gifGridView) {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                                } else if (recyclerListView == EmojiView.this.emojiGridView) {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(36.0f), 0, AndroidUtilities.dp(44.0f));
                                }
                                if (findFirstVisibleItemPosition != -1) {
                                    gridLayoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, top - recyclerListView.getPaddingTop());
                                }
                                AnimatorSet unused = EmojiView.this.searchAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                AnimatorSet unused = EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                    i = 2;
                }
            }
        }
        if (!z2) {
            this.delegate.onSearchOpenClose(0);
        }
        showBottomTab(true, z2);
    }

    /* access modifiers changed from: private */
    public void checkStickersSearchFieldScroll(boolean z) {
        RecyclerListView recyclerListView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z2 = false;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.stickersGridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition == null) {
                this.stickersSearchField.showShadow(true, !z);
                return;
            }
            SearchField searchField = this.stickersSearchField;
            if (findViewHolderForAdapterPosition.itemView.getTop() < this.stickersGridView.getPaddingTop()) {
                z2 = true;
            }
            searchField.showShadow(z2, !z);
        } else if (this.stickersSearchField != null && (recyclerListView = this.stickersGridView) != null) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition2 != null) {
                this.stickersSearchField.setTranslationY((float) findViewHolderForAdapterPosition2.itemView.getTop());
            } else {
                this.stickersSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            this.stickersSearchField.showShadow(false, !z);
        }
    }

    /* access modifiers changed from: private */
    public void checkBottomTabScroll(float f) {
        int i;
        if (SystemClock.elapsedRealtime() - this.shownBottomTabAfterClick >= ((long) ViewConfiguration.getTapTimeout())) {
            this.lastBottomScrollDy += f;
            if (this.pager.getCurrentItem() == 0) {
                i = AndroidUtilities.dp(38.0f);
            } else {
                i = AndroidUtilities.dp(48.0f);
            }
            float f2 = this.lastBottomScrollDy;
            if (f2 >= ((float) i)) {
                showBottomTab(false, true);
            } else if (f2 <= ((float) (-i))) {
                showBottomTab(true, true);
            } else if ((this.bottomTabContainer.getTag() == null && this.lastBottomScrollDy < 0.0f) || (this.bottomTabContainer.getTag() != null && this.lastBottomScrollDy > 0.0f)) {
                this.lastBottomScrollDy = 0.0f;
            }
        }
    }

    /* access modifiers changed from: private */
    public void showBackspaceButton(final boolean z, boolean z2) {
        if (z && this.backspaceButton.getTag() == null) {
            return;
        }
        if (z || this.backspaceButton.getTag() == null) {
            AnimatorSet animatorSet = this.backspaceButtonAnimation;
            int i = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.backspaceButtonAnimation = null;
            }
            ImageView imageView = this.backspaceButton;
            if (!z) {
                i = 1;
            }
            imageView.setTag(i);
            int i2 = 0;
            float f = 1.0f;
            if (z2) {
                if (z) {
                    this.backspaceButton.setVisibility(0);
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.backspaceButtonAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[3];
                ImageView imageView2 = this.backspaceButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                ImageView imageView3 = this.backspaceButton;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView3, property2, fArr2);
                ImageView imageView4 = this.backspaceButton;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr3[0] = f;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView4, property3, fArr3);
                animatorSet2.playTogether(animatorArr);
                this.backspaceButtonAnimation.setDuration(200);
                this.backspaceButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.backspaceButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            EmojiView.this.backspaceButton.setVisibility(4);
                        }
                    }
                });
                this.backspaceButtonAnimation.start();
                return;
            }
            this.backspaceButton.setAlpha(z ? 1.0f : 0.0f);
            this.backspaceButton.setScaleX(z ? 1.0f : 0.0f);
            ImageView imageView5 = this.backspaceButton;
            if (!z) {
                f = 0.0f;
            }
            imageView5.setScaleY(f);
            ImageView imageView6 = this.backspaceButton;
            if (!z) {
                i2 = 4;
            }
            imageView6.setVisibility(i2);
        }
    }

    /* access modifiers changed from: private */
    public void showStickerSettingsButton(final boolean z, boolean z2) {
        ImageView imageView = this.stickerSettingsButton;
        if (imageView != null) {
            if (z && imageView.getTag() == null) {
                return;
            }
            if (z || this.stickerSettingsButton.getTag() == null) {
                AnimatorSet animatorSet = this.stickersButtonAnimation;
                int i = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.stickersButtonAnimation = null;
                }
                ImageView imageView2 = this.stickerSettingsButton;
                if (!z) {
                    i = 1;
                }
                imageView2.setTag(i);
                int i2 = 0;
                float f = 1.0f;
                if (z2) {
                    if (z) {
                        this.stickerSettingsButton.setVisibility(0);
                    }
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.stickersButtonAnimation = animatorSet2;
                    Animator[] animatorArr = new Animator[3];
                    ImageView imageView3 = this.stickerSettingsButton;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = z ? 1.0f : 0.0f;
                    animatorArr[0] = ObjectAnimator.ofFloat(imageView3, property, fArr);
                    ImageView imageView4 = this.stickerSettingsButton;
                    Property property2 = View.SCALE_X;
                    float[] fArr2 = new float[1];
                    fArr2[0] = z ? 1.0f : 0.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(imageView4, property2, fArr2);
                    ImageView imageView5 = this.stickerSettingsButton;
                    Property property3 = View.SCALE_Y;
                    float[] fArr3 = new float[1];
                    if (!z) {
                        f = 0.0f;
                    }
                    fArr3[0] = f;
                    animatorArr[2] = ObjectAnimator.ofFloat(imageView5, property3, fArr3);
                    animatorSet2.playTogether(animatorArr);
                    this.stickersButtonAnimation.setDuration(200);
                    this.stickersButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.stickersButtonAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (!z) {
                                EmojiView.this.stickerSettingsButton.setVisibility(4);
                            }
                        }
                    });
                    this.stickersButtonAnimation.start();
                    return;
                }
                this.stickerSettingsButton.setAlpha(z ? 1.0f : 0.0f);
                this.stickerSettingsButton.setScaleX(z ? 1.0f : 0.0f);
                ImageView imageView6 = this.stickerSettingsButton;
                if (!z) {
                    f = 0.0f;
                }
                imageView6.setScaleY(f);
                ImageView imageView7 = this.stickerSettingsButton;
                if (!z) {
                    i2 = 4;
                }
                imageView7.setVisibility(i2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void showBottomTab(boolean z, boolean z2) {
        float f;
        float f2;
        float f3;
        float f4;
        int i;
        float f5 = 0.0f;
        this.lastBottomScrollDy = 0.0f;
        if (z && this.bottomTabContainer.getTag() == null) {
            return;
        }
        if (z || this.bottomTabContainer.getTag() == null) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                AnimatorSet animatorSet = this.bottomTabContainerAnimation;
                int i2 = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.bottomTabContainerAnimation = null;
                }
                FrameLayout frameLayout = this.bottomTabContainer;
                if (!z) {
                    i2 = 1;
                }
                frameLayout.setTag(i2);
                float f6 = 50.0f;
                if (z2) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.bottomTabContainerAnimation = animatorSet2;
                    Animator[] animatorArr = new Animator[3];
                    FrameLayout frameLayout2 = this.bottomTabContainer;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    if (z) {
                        f3 = 0.0f;
                    } else {
                        f3 = (float) AndroidUtilities.dp(this.needEmojiSearch ? 45.0f : 50.0f);
                    }
                    fArr[0] = f3;
                    animatorArr[0] = ObjectAnimator.ofFloat(frameLayout2, property, fArr);
                    FrameLayout frameLayout3 = this.bulletinContainer;
                    Property property2 = View.TRANSLATION_Y;
                    float[] fArr2 = new float[1];
                    boolean z3 = this.needEmojiSearch;
                    if (z3) {
                        if (z) {
                            i = 0;
                        } else {
                            if (z3) {
                                f6 = 45.0f;
                            }
                            i = AndroidUtilities.dp(f6);
                        }
                        f4 = (float) i;
                    } else {
                        f4 = frameLayout3.getTranslationY();
                    }
                    fArr2[0] = f4;
                    animatorArr[1] = ObjectAnimator.ofFloat(frameLayout3, property2, fArr2);
                    View view = this.shadowLine;
                    Property property3 = View.TRANSLATION_Y;
                    float[] fArr3 = new float[1];
                    if (!z) {
                        f5 = (float) AndroidUtilities.dp(45.0f);
                    }
                    fArr3[0] = f5;
                    animatorArr[2] = ObjectAnimator.ofFloat(view, property3, fArr3);
                    animatorSet2.playTogether(animatorArr);
                    this.bottomTabContainerAnimation.setDuration(200);
                    this.bottomTabContainerAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.bottomTabContainerAnimation.start();
                    return;
                }
                FrameLayout frameLayout4 = this.bottomTabContainer;
                if (z) {
                    f = 0.0f;
                } else {
                    f = (float) AndroidUtilities.dp(this.needEmojiSearch ? 45.0f : 50.0f);
                }
                frameLayout4.setTranslationY(f);
                boolean z4 = this.needEmojiSearch;
                if (z4) {
                    FrameLayout frameLayout5 = this.bulletinContainer;
                    if (z) {
                        f2 = 0.0f;
                    } else {
                        if (z4) {
                            f6 = 45.0f;
                        }
                        f2 = (float) AndroidUtilities.dp(f6);
                    }
                    frameLayout5.setTranslationY(f2);
                }
                View view2 = this.shadowLine;
                if (!z) {
                    f5 = (float) AndroidUtilities.dp(45.0f);
                }
                view2.setTranslationY(f5);
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkTabsY(int i, int i2) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (i == 1) {
            checkEmojiTabY(this.emojiGridView, i2);
            return;
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && !this.ignoreStickersScroll) {
            RecyclerListView listViewForType = getListViewForType(i);
            if (i2 <= 0 || listViewForType == null || listViewForType.getVisibility() != 0 || (findViewHolderForAdapterPosition = listViewForType.findViewHolderForAdapterPosition(0)) == null || findViewHolderForAdapterPosition.itemView.getTop() + this.searchFieldHeight < listViewForType.getPaddingTop()) {
                int[] iArr = this.tabsMinusDy;
                iArr[i] = iArr[i] - i2;
                if (iArr[i] > 0) {
                    iArr[i] = 0;
                } else if (iArr[i] < (-AndroidUtilities.dp(288.0f))) {
                    this.tabsMinusDy[i] = -AndroidUtilities.dp(288.0f);
                }
                if (i == 0) {
                    updateStickerTabsPosition();
                } else {
                    getTabsForType(i).setTranslationY((float) Math.max(-AndroidUtilities.dp(48.0f), this.tabsMinusDy[i]));
                }
            }
        }
    }

    private void resetTabsY(int i) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && i != 0) {
            View tabsForType = getTabsForType(i);
            this.tabsMinusDy[i] = 0;
            tabsForType.setTranslationY((float) 0);
        }
    }

    /* access modifiers changed from: private */
    public void animateTabsY(int i) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && i != 0) {
            float dpf2 = AndroidUtilities.dpf2(i == 1 ? 36.0f : 48.0f);
            float f = ((float) this.tabsMinusDy[i]) / (-dpf2);
            if (f <= 0.0f || f >= 1.0f) {
                animateSearchField(i);
                return;
            }
            View tabsForType = getTabsForType(i);
            int i2 = f > 0.5f ? (int) (-Math.ceil((double) dpf2)) : 0;
            if (f > 0.5f) {
                animateSearchField(i, false, i2);
            }
            if (i == 1) {
                checkEmojiShadow(i2);
            }
            ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
            if (objectAnimatorArr[i] == null) {
                objectAnimatorArr[i] = ObjectAnimator.ofFloat(tabsForType, View.TRANSLATION_Y, new float[]{tabsForType.getTranslationY(), (float) i2});
                this.tabsYAnimators[i].addUpdateListener(new EmojiView$$ExternalSyntheticLambda0(this, i));
                this.tabsYAnimators[i].setDuration(200);
            } else {
                objectAnimatorArr[i].setFloatValues(new float[]{tabsForType.getTranslationY(), (float) i2});
            }
            this.tabsYAnimators[i].start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateTabsY$9(int i, ValueAnimator valueAnimator) {
        this.tabsMinusDy[i] = (int) ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    /* access modifiers changed from: private */
    public void stopAnimatingTabsY(int i) {
        ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
        if (objectAnimatorArr[i] != null && objectAnimatorArr[i].isRunning()) {
            this.tabsYAnimators[i].cancel();
        }
    }

    private void animateSearchField(int i) {
        RecyclerListView listViewForType = getListViewForType(i);
        boolean z = true;
        int dp = AndroidUtilities.dp(i == 1 ? 38.0f : 48.0f);
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = listViewForType.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition != null) {
            int bottom = findViewHolderForAdapterPosition.itemView.getBottom();
            int[] iArr = this.tabsMinusDy;
            float f = ((float) (bottom - (dp + iArr[i]))) / ((float) this.searchFieldHeight);
            if (f > 0.0f || f < 1.0f) {
                if (f <= 0.5f) {
                    z = false;
                }
                animateSearchField(i, z, iArr[i]);
            }
        }
    }

    private void animateSearchField(int i, boolean z, final int i2) {
        if (getListViewForType(i).findViewHolderForAdapterPosition(0) != null) {
            AnonymousClass36 r0 = new LinearSmoothScroller(this, getContext()) {
                /* access modifiers changed from: protected */
                public int getVerticalSnapPreference() {
                    return -1;
                }

                /* access modifiers changed from: protected */
                public int calculateTimeForDeceleration(int i) {
                    return super.calculateTimeForDeceleration(i) * 16;
                }

                public int calculateDtToFit(int i, int i2, int i3, int i4, int i5) {
                    return super.calculateDtToFit(i, i2, i3, i4, i5) + i2;
                }
            };
            r0.setTargetPosition(z ^ true ? 1 : 0);
            getLayoutManagerForType(i).startSmoothScroll(r0);
        }
    }

    private View getTabsForType(int i) {
        if (i == 0) {
            return this.stickersTab;
        }
        if (i == 1) {
            return this.emojiTabs;
        }
        if (i == 2) {
            return this.gifTabs;
        }
        throw new IllegalArgumentException("Unexpected argument: " + i);
    }

    private RecyclerListView getListViewForType(int i) {
        if (i == 0) {
            return this.stickersGridView;
        }
        if (i == 1) {
            return this.emojiGridView;
        }
        if (i == 2) {
            return this.gifGridView;
        }
        throw new IllegalArgumentException("Unexpected argument: " + i);
    }

    private GridLayoutManager getLayoutManagerForType(int i) {
        if (i == 0) {
            return this.stickersLayoutManager;
        }
        if (i == 1) {
            return this.emojiLayoutManager;
        }
        if (i == 2) {
            return this.gifLayoutManager;
        }
        throw new IllegalArgumentException("Unexpected argument: " + i);
    }

    /* access modifiers changed from: private */
    public SearchField getSearchFieldForType(int i) {
        if (i == 0) {
            return this.stickersSearchField;
        }
        if (i == 1) {
            return this.emojiSearchField;
        }
        if (i == 2) {
            return this.gifSearchField;
        }
        throw new IllegalArgumentException("Unexpected argument: " + i);
    }

    /* access modifiers changed from: private */
    public void checkEmojiSearchFieldScroll(boolean z) {
        EmojiGridView emojiGridView2;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition == null) {
                this.emojiSearchField.showShadow(true, !z);
            } else {
                this.emojiSearchField.showShadow(findViewHolderForAdapterPosition.itemView.getTop() < this.emojiGridView.getPaddingTop(), !z);
            }
            showEmojiShadow(false, !z);
        } else if (this.emojiSearchField != null && (emojiGridView2 = this.emojiGridView) != null) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = emojiGridView2.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition2 != null) {
                this.emojiSearchField.setTranslationY((float) findViewHolderForAdapterPosition2.itemView.getTop());
            } else {
                this.emojiSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            this.emojiSearchField.showShadow(false, !z);
            checkEmojiShadow(Math.round(this.emojiTabs.getTranslationY()));
        }
    }

    private void checkEmojiShadow(int i) {
        ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
        if (objectAnimatorArr[1] == null || !objectAnimatorArr[1].isRunning()) {
            boolean z = false;
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0);
            int dp = AndroidUtilities.dp(38.0f) + i;
            if (dp > 0 && (findViewHolderForAdapterPosition == null || findViewHolderForAdapterPosition.itemView.getBottom() < dp)) {
                z = true;
            }
            showEmojiShadow(z, !this.isLayout);
        }
    }

    /* access modifiers changed from: private */
    public void checkEmojiTabY(View view, int i) {
        EmojiGridView emojiGridView2;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (view == null) {
            EmojiTabsStrip emojiTabsStrip = this.emojiTabs;
            this.tabsMinusDy[1] = 0;
            emojiTabsStrip.setTranslationY((float) 0);
        } else if (view.getVisibility() == 0 && !this.emojiSmoothScrolling) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                if (i > 0 && (emojiGridView2 = this.emojiGridView) != null && emojiGridView2.getVisibility() == 0 && (findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0)) != null) {
                    if (findViewHolderForAdapterPosition.itemView.getTop() + (this.needEmojiSearch ? this.searchFieldHeight : 0) >= this.emojiGridView.getPaddingTop()) {
                        return;
                    }
                }
                int[] iArr = this.tabsMinusDy;
                iArr[1] = iArr[1] - i;
                if (iArr[1] > 0) {
                    iArr[1] = 0;
                } else if (iArr[1] < (-AndroidUtilities.dp(108.0f))) {
                    this.tabsMinusDy[1] = -AndroidUtilities.dp(108.0f);
                }
                this.emojiTabs.setTranslationY((float) Math.max(-AndroidUtilities.dp(36.0f), this.tabsMinusDy[1]));
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkGifSearchFieldScroll(boolean z) {
        RecyclerListView recyclerListView;
        int findLastVisibleItemPosition;
        RecyclerListView recyclerListView2 = this.gifGridView;
        if (recyclerListView2 != null && (recyclerListView2.getAdapter() instanceof GifAdapter)) {
            GifAdapter gifAdapter2 = (GifAdapter) this.gifGridView.getAdapter();
            if (!gifAdapter2.searchEndReached && gifAdapter2.reqId == 0 && !gifAdapter2.results.isEmpty() && (findLastVisibleItemPosition = this.gifLayoutManager.findLastVisibleItemPosition()) != -1 && findLastVisibleItemPosition > this.gifLayoutManager.getItemCount() - 5) {
                gifAdapter2.search(gifAdapter2.lastSearchImageString, gifAdapter2.nextSearchOffset, true, gifAdapter2.lastSearchIsEmoji, gifAdapter2.lastSearchIsEmoji);
            }
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z2 = false;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.gifGridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition == null) {
                this.gifSearchField.showShadow(true, !z);
                return;
            }
            SearchField searchField = this.gifSearchField;
            if (findViewHolderForAdapterPosition.itemView.getTop() < this.gifGridView.getPaddingTop()) {
                z2 = true;
            }
            searchField.showShadow(z2, !z);
        } else if (this.gifSearchField != null && (recyclerListView = this.gifGridView) != null) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition2 != null) {
                this.gifSearchField.setTranslationY((float) findViewHolderForAdapterPosition2.itemView.getTop());
            } else {
                this.gifSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            this.gifSearchField.showShadow(false, !z);
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
    public void checkScroll(int i) {
        GifAdapter gifAdapter2;
        int findFirstVisibleItemPosition;
        int findFirstVisibleItemPosition2;
        if (i == 0) {
            if (!this.ignoreStickersScroll && (findFirstVisibleItemPosition2 = this.stickersLayoutManager.findFirstVisibleItemPosition()) != -1 && this.stickersGridView != null) {
                int i2 = this.favTabNum;
                if (i2 <= 0 && (i2 = this.recentTabNum) <= 0) {
                    i2 = this.stickersTabOffset;
                }
                this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition2), i2);
            }
        } else if (i == 2 && this.gifGridView.getAdapter() == (gifAdapter2 = this.gifAdapter) && gifAdapter2.trendingSectionItem >= 0 && this.gifTrendingTabNum >= 0 && this.gifRecentTabNum >= 0 && (findFirstVisibleItemPosition = this.gifLayoutManager.findFirstVisibleItemPosition()) != -1) {
            this.gifTabs.onPageScrolled(findFirstVisibleItemPosition >= this.gifAdapter.trendingSectionItem ? this.gifTrendingTabNum : this.gifRecentTabNum, 0);
        }
    }

    /* access modifiers changed from: private */
    public void saveNewPage() {
        ViewPager viewPager = this.pager;
        if (viewPager != null) {
            int currentItem = viewPager.getCurrentItem();
            int i = 1;
            if (currentItem != 2) {
                i = currentItem == 1 ? 2 : 0;
            }
            if (this.currentPage != i) {
                this.currentPage = i;
                MessagesController.getGlobalEmojiSettings().edit().putInt("selected_page", i).commit();
            }
        }
    }

    public void clearRecentEmoji() {
        Emoji.clearRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void onPageScrolled(int i, int i2, int i3) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null) {
            int i4 = 0;
            if (i == 1) {
                if (i3 != 0) {
                    i4 = 2;
                }
                emojiViewDelegate.onTabOpened(i4);
            } else if (i == 2) {
                emojiViewDelegate.onTabOpened(3);
            } else {
                emojiViewDelegate.onTabOpened(0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void postBackspaceRunnable(int i) {
        AndroidUtilities.runOnUIThread(new EmojiView$$ExternalSyntheticLambda7(this, i), (long) i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$postBackspaceRunnable$10(int i) {
        if (this.backspacePressed) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.onBackspace()) {
                this.backspaceButton.performHapticFeedback(3);
            }
            this.backspaceOnce = true;
            postBackspaceRunnable(Math.max(50, i - 100));
        }
    }

    public void switchToGifRecent() {
        showBackspaceButton(false, false);
        showStickerSettingsButton(false, false);
        this.pager.setCurrentItem(1, false);
    }

    /* access modifiers changed from: private */
    public void updateEmojiHeaders() {
        if (this.emojiGridView != null) {
            for (int i = 0; i < this.emojiGridView.getChildCount(); i++) {
                View childAt = this.emojiGridView.getChildAt(i);
                if (childAt instanceof EmojiPackHeader) {
                    ((EmojiPackHeader) childAt).updateState(true);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateStickerTabs() {
        ArrayList<TLRPC$Document> arrayList;
        ArrayList<TLRPC$Document> arrayList2;
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip != null && !scrollSlidingTabStrip.isDragging()) {
            this.recentTabNum = -2;
            this.favTabNum = -2;
            this.trendingTabNum = -2;
            this.premiumTabNum = -2;
            this.hasChatStickers = false;
            this.stickersTabOffset = 0;
            int currentPosition = this.stickersTab.getCurrentPosition();
            this.stickersTab.beginUpdate((getParent() == null || getVisibility() != 0 || (this.installingStickerSets.size() == 0 && this.removingStickerSets.size() == 0)) ? false : true);
            MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
            SharedPreferences emojiSettings = MessagesController.getEmojiSettings(this.currentAccount);
            this.featuredStickerSets.clear();
            ArrayList<TLRPC$StickerSetCovered> featuredStickerSets2 = instance.getFeaturedStickerSets();
            int size = featuredStickerSets2.size();
            for (int i = 0; i < size; i++) {
                TLRPC$StickerSetCovered tLRPC$StickerSetCovered = featuredStickerSets2.get(i);
                if (!instance.isStickerPackInstalled(tLRPC$StickerSetCovered.set.id)) {
                    this.featuredStickerSets.add(tLRPC$StickerSetCovered);
                }
            }
            TrendingAdapter trendingAdapter2 = this.trendingAdapter;
            if (trendingAdapter2 != null) {
                trendingAdapter2.notifyDataSetChanged();
            }
            if (!featuredStickerSets2.isEmpty() && (this.featuredStickerSets.isEmpty() || emojiSettings.getLong("featured_hidden", 0) == featuredStickerSets2.get(0).set.id)) {
                int i2 = instance.getUnreadStickerSets().isEmpty() ? 2 : 3;
                StickerTabView addStickerIconTab = this.stickersTab.addStickerIconTab(i2, this.stickerIcons[i2]);
                addStickerIconTab.textView.setText(LocaleController.getString("FeaturedStickersShort", R.string.FeaturedStickersShort));
                addStickerIconTab.setContentDescription(LocaleController.getString("FeaturedStickers", R.string.FeaturedStickers));
                int i3 = this.stickersTabOffset;
                this.trendingTabNum = i3;
                this.stickersTabOffset = i3 + 1;
            }
            if (!this.favouriteStickers.isEmpty()) {
                int i4 = this.stickersTabOffset;
                this.favTabNum = i4;
                this.stickersTabOffset = i4 + 1;
                StickerTabView addStickerIconTab2 = this.stickersTab.addStickerIconTab(1, this.stickerIcons[1]);
                addStickerIconTab2.textView.setText(LocaleController.getString("FavoriteStickersShort", R.string.FavoriteStickersShort));
                addStickerIconTab2.setContentDescription(LocaleController.getString("FavoriteStickers", R.string.FavoriteStickers));
            }
            if (!this.recentStickers.isEmpty()) {
                int i5 = this.stickersTabOffset;
                this.recentTabNum = i5;
                this.stickersTabOffset = i5 + 1;
                StickerTabView addStickerIconTab3 = this.stickersTab.addStickerIconTab(0, this.stickerIcons[0]);
                addStickerIconTab3.textView.setText(LocaleController.getString("RecentStickersShort", R.string.RecentStickersShort));
                addStickerIconTab3.setContentDescription(LocaleController.getString("RecentStickers", R.string.RecentStickers));
            }
            this.stickerSets.clear();
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
            this.groupStickerSet = null;
            this.groupStickerPackPosition = -1;
            this.groupStickerPackNum = -10;
            ArrayList<TLRPC$TL_messages_stickerSet> stickerSets2 = instance.getStickerSets(0);
            int i6 = 0;
            while (true) {
                TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr = this.primaryInstallingStickerSets;
                if (i6 >= tLRPC$StickerSetCoveredArr.length) {
                    break;
                }
                TLRPC$StickerSetCovered tLRPC$StickerSetCovered2 = tLRPC$StickerSetCoveredArr[i6];
                if (tLRPC$StickerSetCovered2 != null) {
                    TLRPC$TL_messages_stickerSet stickerSetById = instance.getStickerSetById(tLRPC$StickerSetCovered2.set.id);
                    if (stickerSetById == null || stickerSetById.set.archived) {
                        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = new TLRPC$TL_messages_stickerSet();
                        tLRPC$TL_messages_stickerSet2.set = tLRPC$StickerSetCovered2.set;
                        TLRPC$Document tLRPC$Document = tLRPC$StickerSetCovered2.cover;
                        if (tLRPC$Document != null) {
                            tLRPC$TL_messages_stickerSet2.documents.add(tLRPC$Document);
                        } else if (!tLRPC$StickerSetCovered2.covers.isEmpty()) {
                            tLRPC$TL_messages_stickerSet2.documents.addAll(tLRPC$StickerSetCovered2.covers);
                        }
                        if (!tLRPC$TL_messages_stickerSet2.documents.isEmpty()) {
                            this.stickerSets.add(tLRPC$TL_messages_stickerSet2);
                        }
                    } else {
                        this.primaryInstallingStickerSets[i6] = null;
                    }
                }
                i6++;
            }
            ArrayList<TLRPC$TL_messages_stickerSet> filterPremiumStickers = MessagesController.getInstance(this.currentAccount).filterPremiumStickers(stickerSets2);
            for (int i7 = 0; i7 < filterPremiumStickers.size(); i7++) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet3 = filterPremiumStickers.get(i7);
                if (!tLRPC$TL_messages_stickerSet3.set.archived && (arrayList2 = tLRPC$TL_messages_stickerSet3.documents) != null && !arrayList2.isEmpty()) {
                    this.stickerSets.add(tLRPC$TL_messages_stickerSet3);
                }
            }
            if (!this.premiumStickers.isEmpty()) {
                int i8 = this.stickersTabOffset;
                this.premiumTabNum = i8;
                this.stickersTabOffset = i8 + 1;
                StickerTabView addStickerIconTab4 = this.stickersTab.addStickerIconTab(4, PremiumGradient.getInstance().premiumStarMenuDrawable2);
                addStickerIconTab4.textView.setText(LocaleController.getString("PremiumStickersShort", R.string.PremiumStickersShort));
                addStickerIconTab4.setContentDescription(LocaleController.getString("PremiumStickers", R.string.PremiumStickers));
            }
            if (this.info != null) {
                long j = MessagesController.getEmojiSettings(this.currentAccount).getLong("group_hide_stickers_" + this.info.id, -1);
                TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.info.id));
                if (chat == null || this.info.stickerset == null || !ChatObject.hasAdminRights(chat)) {
                    this.groupStickersHidden = j != -1;
                } else {
                    TLRPC$StickerSet tLRPC$StickerSet = this.info.stickerset;
                    if (tLRPC$StickerSet != null) {
                        this.groupStickersHidden = j == tLRPC$StickerSet.id;
                    }
                }
                TLRPC$ChatFull tLRPC$ChatFull = this.info;
                TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$ChatFull.stickerset;
                if (tLRPC$StickerSet2 != null) {
                    TLRPC$TL_messages_stickerSet groupStickerSetById = instance.getGroupStickerSetById(tLRPC$StickerSet2);
                    if (!(groupStickerSetById == null || (arrayList = groupStickerSetById.documents) == null || arrayList.isEmpty() || groupStickerSetById.set == null)) {
                        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet4 = new TLRPC$TL_messages_stickerSet();
                        tLRPC$TL_messages_stickerSet4.documents = groupStickerSetById.documents;
                        tLRPC$TL_messages_stickerSet4.packs = groupStickerSetById.packs;
                        tLRPC$TL_messages_stickerSet4.set = groupStickerSetById.set;
                        if (this.groupStickersHidden) {
                            this.groupStickerPackNum = this.stickerSets.size();
                            this.stickerSets.add(tLRPC$TL_messages_stickerSet4);
                        } else {
                            this.groupStickerPackNum = 0;
                            this.stickerSets.add(0, tLRPC$TL_messages_stickerSet4);
                        }
                        if (this.info.can_set_stickers) {
                            tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet4;
                        }
                        this.groupStickerSet = tLRPC$TL_messages_stickerSet;
                    }
                } else if (tLRPC$ChatFull.can_set_stickers) {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet5 = new TLRPC$TL_messages_stickerSet();
                    if (this.groupStickersHidden) {
                        this.groupStickerPackNum = this.stickerSets.size();
                        this.stickerSets.add(tLRPC$TL_messages_stickerSet5);
                    } else {
                        this.groupStickerPackNum = 0;
                        this.stickerSets.add(0, tLRPC$TL_messages_stickerSet5);
                    }
                }
            }
            int i9 = 0;
            while (i9 < this.stickerSets.size()) {
                if (i9 == this.groupStickerPackNum) {
                    TLRPC$Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.info.id));
                    if (chat2 == null) {
                        this.stickerSets.remove(0);
                        i9--;
                    } else {
                        this.hasChatStickers = true;
                        this.stickersTab.addStickerTab(chat2);
                    }
                } else {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet6 = this.stickerSets.get(i9);
                    TLRPC$Document tLRPC$Document2 = tLRPC$TL_messages_stickerSet6.documents.get(0);
                    TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_messages_stickerSet6.set.thumbs, 90);
                    if (closestPhotoSizeWithSize == null || tLRPC$TL_messages_stickerSet6.set.gifs) {
                        closestPhotoSizeWithSize = tLRPC$Document2;
                    }
                    this.stickersTab.addStickerTab(closestPhotoSizeWithSize, tLRPC$Document2, tLRPC$TL_messages_stickerSet6).setContentDescription(tLRPC$TL_messages_stickerSet6.set.title + ", " + LocaleController.getString("AccDescrStickerSet", R.string.AccDescrStickerSet));
                }
                i9++;
            }
            this.stickersTab.commitUpdate();
            this.stickersTab.updateTabStyles();
            if (currentPosition != 0) {
                this.stickersTab.onPageScrolled(currentPosition, currentPosition);
            }
            checkPanels();
        }
    }

    private void checkPanels() {
        int findFirstVisibleItemPosition;
        if (this.stickersTab != null && (findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition()) != -1) {
            int i = this.favTabNum;
            if (i <= 0 && (i = this.recentTabNum) <= 0) {
                i = this.stickersTabOffset;
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition), i);
        }
    }

    private void updateGifTabs() {
        int i;
        int currentPosition = this.gifTabs.getCurrentPosition();
        int i2 = this.gifRecentTabNum;
        boolean z = currentPosition == i2;
        boolean z2 = i2 >= 0;
        boolean z3 = !this.recentGifs.isEmpty();
        this.gifTabs.beginUpdate(false);
        this.gifRecentTabNum = -2;
        this.gifTrendingTabNum = -2;
        this.gifFirstEmojiTabNum = -2;
        if (z3) {
            this.gifRecentTabNum = 0;
            this.gifTabs.addIconTab(0, this.gifIcons[0]).setContentDescription(LocaleController.getString("RecentStickers", R.string.RecentStickers));
            i = 1;
        } else {
            i = 0;
        }
        this.gifTrendingTabNum = i;
        this.gifTabs.addIconTab(1, this.gifIcons[1]).setContentDescription(LocaleController.getString("FeaturedGifs", R.string.FeaturedGifs));
        this.gifFirstEmojiTabNum = i + 1;
        AndroidUtilities.dp(13.0f);
        AndroidUtilities.dp(11.0f);
        ArrayList<String> arrayList = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
        int size = arrayList.size();
        for (int i3 = 0; i3 < size; i3++) {
            String str = arrayList.get(i3);
            Emoji.EmojiDrawable emojiDrawable = Emoji.getEmojiDrawable(str);
            if (emojiDrawable != null) {
                this.gifTabs.addEmojiTab(i3 + 3, emojiDrawable, MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(str)).setContentDescription(str);
            }
        }
        this.gifTabs.commitUpdate();
        this.gifTabs.updateTabStyles();
        if (z && !z3) {
            this.gifTabs.selectTab(this.gifTrendingTabNum);
        } else if (!ViewCompat.isLaidOut(this.gifTabs)) {
        } else {
            if (z3 && !z2) {
                this.gifTabs.onPageScrolled(currentPosition + 1, 0);
            } else if (!z3 && z2) {
                this.gifTabs.onPageScrolled(currentPosition - 1, 0);
            }
        }
    }

    public void addRecentSticker(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null) {
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, (Object) null, tLRPC$Document, (int) (System.currentTimeMillis() / 1000), false);
            boolean isEmpty = this.recentStickers.isEmpty();
            this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
            StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
            if (stickersGridAdapter2 != null) {
                stickersGridAdapter2.notifyDataSetChanged();
            }
            if (isEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void addRecentGif(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null) {
            boolean isEmpty = this.recentGifs.isEmpty();
            updateRecentGifs();
            if (isEmpty) {
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
        EmojiTabsStrip emojiTabsStrip = this.emojiTabs;
        if (emojiTabsStrip != null) {
            emojiTabsStrip.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
            this.emojiTabsShadow.setBackgroundColor(getThemedColor("chat_emojiPanelShadowLine"));
        }
        EmojiColorPickerView emojiColorPickerView = this.pickerView;
        if (emojiColorPickerView != null) {
            Theme.setDrawableColor(emojiColorPickerView.backgroundDrawable, getThemedColor("dialogBackground"));
            Theme.setDrawableColor(this.pickerView.arrowDrawable, getThemedColor("dialogBackground"));
        }
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                searchField = this.stickersSearchField;
            } else if (i == 1) {
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
        EmojiGridView emojiGridView2 = this.emojiGridView;
        if (emojiGridView2 != null) {
            emojiGridView2.setGlowColor(getThemedColor("chat_emojiPanelBackground"));
        }
        RecyclerListView recyclerListView = this.stickersGridView;
        if (recyclerListView != null) {
            recyclerListView.setGlowColor(getThemedColor("chat_emojiPanelBackground"));
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip != null) {
            scrollSlidingTabStrip.setIndicatorColor(getThemedColor("chat_emojiPanelStickerPackSelectorLine"));
            this.stickersTab.setUnderlineColor(getThemedColor("chat_emojiPanelShadowLine"));
            this.stickersTab.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.gifTabs;
        if (scrollSlidingTabStrip2 != null) {
            scrollSlidingTabStrip2.setIndicatorColor(getThemedColor("chat_emojiPanelStickerPackSelectorLine"));
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
        int i2 = 0;
        while (true) {
            Drawable[] drawableArr = this.tabIcons;
            if (i2 >= drawableArr.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr[i2], getThemedColor("chat_emojiBottomPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.tabIcons[i2], getThemedColor("chat_emojiPanelIconSelected"), true);
            i2++;
        }
        EmojiTabsStrip emojiTabsStrip2 = this.emojiTabs;
        if (emojiTabsStrip2 != null) {
            emojiTabsStrip2.updateColors();
        }
        int i3 = 0;
        while (true) {
            Drawable[] drawableArr2 = this.stickerIcons;
            if (i3 >= drawableArr2.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr2[i3], getThemedColor("chat_emojiPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.stickerIcons[i3], getThemedColor("chat_emojiPanelIconSelected"), true);
            i3++;
        }
        int i4 = 0;
        while (true) {
            Drawable[] drawableArr3 = this.gifIcons;
            if (i4 >= drawableArr3.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr3[i4], getThemedColor("chat_emojiPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.gifIcons[i4], getThemedColor("chat_emojiPanelIconSelected"), true);
            i4++;
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
        Paint paint2 = this.emojiLockPaint;
        if (paint2 != null) {
            paint2.setColor(getThemedColor("chat_emojiPanelStickerSetName"));
            Paint paint3 = this.emojiLockPaint;
            paint3.setAlpha((int) (((float) paint3.getAlpha()) * 0.5f));
        }
        Drawable drawable3 = this.emojiLockDrawable;
        if (drawable3 != null) {
            drawable3.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelStickerSetName"), PorterDuff.Mode.MULTIPLY));
        }
    }

    public void onMeasure(int i, int i2) {
        this.isLayout = true;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            if (this.currentBackgroundType != 1) {
                if (Build.VERSION.SDK_INT >= 21) {
                    setOutlineProvider((ViewOutlineProvider) this.outlineProvider);
                    setClipToOutline(true);
                    setElevation((float) AndroidUtilities.dp(2.0f));
                }
                setBackgroundResource(R.drawable.smiles_popup);
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
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), NUM));
        this.isLayout = false;
        setTranslationY(getTranslationY());
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        if (this.lastNotifyWidth != i5) {
            this.lastNotifyWidth = i5;
            reloadStickersAdapter();
        }
        View view = (View) getParent();
        if (view != null) {
            int i6 = i4 - i2;
            int height = view.getHeight();
            if (!(this.lastNotifyHeight == i6 && this.lastNotifyHeight2 == height)) {
                EmojiViewDelegate emojiViewDelegate = this.delegate;
                if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                    this.bottomTabContainer.setTranslationY((float) AndroidUtilities.dp(49.0f));
                    if (this.needEmojiSearch) {
                        this.bulletinContainer.setTranslationY((float) AndroidUtilities.dp(49.0f));
                    }
                } else if (this.bottomTabContainer.getTag() == null && i6 <= this.lastNotifyHeight) {
                    this.bottomTabContainer.setTranslationY(0.0f);
                    if (this.needEmojiSearch) {
                        this.bulletinContainer.setTranslationY(0.0f);
                    }
                }
                this.lastNotifyHeight = i6;
                this.lastNotifyHeight2 = height;
            }
        }
        super.onLayout(z, i, i2, i3, i4);
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

    public void setDragListener(DragListener dragListener2) {
        this.dragListener = dragListener2;
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        updateStickerTabs();
    }

    public void invalidateViews() {
        this.emojiGridView.invalidateViews();
    }

    public void setForseMultiwindowLayout(boolean z) {
        this.forseMultiwindowLayout = z;
    }

    public void onOpen(boolean z) {
        if (!(this.currentPage == 0 || this.currentChatId == 0)) {
            this.currentPage = 0;
        }
        if (this.currentPage == 0 || z || this.currentTabs.size() == 1) {
            showBackspaceButton(true, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 0) {
                this.pager.setCurrentItem(0, !z);
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
                int i2 = this.favTabNum;
                if (i2 >= 0) {
                    scrollSlidingTabStrip.selectTab(i2);
                } else {
                    int i3 = this.recentTabNum;
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
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            AndroidUtilities.runOnUIThread(new EmojiView$$ExternalSyntheticLambda5(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttachedToWindow$11() {
        updateStickerTabs();
        reloadStickersAdapter();
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 8) {
            Emoji.sortEmoji();
            this.emojiAdapter.notifyDataSetChanged();
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
            if (this.stickersGridAdapter != null) {
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentDocumentsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
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

    private void checkDocuments(boolean z) {
        if (z) {
            updateRecentGifs();
            return;
        }
        int size = this.recentStickers.size();
        int size2 = this.favouriteStickers.size();
        int i = 0;
        this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
        this.favouriteStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(2);
        if (UserConfig.getInstance(this.currentAccount).isPremium()) {
            this.premiumStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(7);
        } else {
            this.premiumStickers = new ArrayList<>();
        }
        for (int i2 = 0; i2 < this.favouriteStickers.size(); i2++) {
            TLRPC$Document tLRPC$Document = this.favouriteStickers.get(i2);
            int i3 = 0;
            while (true) {
                if (i3 >= this.recentStickers.size()) {
                    break;
                }
                TLRPC$Document tLRPC$Document2 = this.recentStickers.get(i3);
                if (tLRPC$Document2.dc_id == tLRPC$Document.dc_id && tLRPC$Document2.id == tLRPC$Document.id) {
                    this.recentStickers.remove(i3);
                    break;
                }
                i3++;
            }
        }
        if (MessagesController.getInstance(this.currentAccount).premiumLocked) {
            int i4 = 0;
            while (i4 < this.favouriteStickers.size()) {
                if (MessageObject.isPremiumSticker(this.favouriteStickers.get(i4))) {
                    this.favouriteStickers.remove(i4);
                    i4--;
                }
                i4++;
            }
            while (i < this.recentStickers.size()) {
                if (MessageObject.isPremiumSticker(this.recentStickers.get(i))) {
                    this.recentStickers.remove(i);
                    i--;
                }
                i++;
            }
        }
        if (!(size == this.recentStickers.size() && size2 == this.favouriteStickers.size())) {
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
        int size = this.recentGifs.size();
        long calcDocumentsHash = MediaDataController.calcDocumentsHash(this.recentGifs, Integer.MAX_VALUE);
        ArrayList<TLRPC$Document> recentGifs2 = MediaDataController.getInstance(this.currentAccount).getRecentGifs();
        this.recentGifs = recentGifs2;
        long calcDocumentsHash2 = MediaDataController.calcDocumentsHash(recentGifs2, Integer.MAX_VALUE);
        if ((this.gifTabs != null && size == 0 && !this.recentGifs.isEmpty()) || (size != 0 && this.recentGifs.isEmpty())) {
            updateGifTabs();
        }
        if ((size != this.recentGifs.size() || calcDocumentsHash != calcDocumentsHash2) && (gifAdapter2 = this.gifAdapter) != null) {
            gifAdapter2.notifyDataSetChanged();
        }
    }

    public void setStickersBanned(boolean z, long j) {
        PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
        if (pagerSlidingTabStrip != null) {
            if (z) {
                this.currentChatId = j;
            } else {
                this.currentChatId = 0;
            }
            View tab = pagerSlidingTabStrip.getTab(2);
            if (tab != null) {
                tab.setAlpha(this.currentChatId != 0 ? 0.5f : 1.0f);
                if (this.currentChatId != 0 && this.pager.getCurrentItem() != 0) {
                    showBackspaceButton(true, true);
                    showStickerSettingsButton(false, true);
                    this.pager.setCurrentItem(0, false);
                }
            }
        }
    }

    public void showStickerBanHint(boolean z) {
        TLRPC$Chat chat;
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
        if (this.mediaBanTooltip.getVisibility() != 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.currentChatId))) != null) {
            if (ChatObject.hasAdminRights(chat) || (tLRPC$TL_chatBannedRights = chat.default_banned_rights) == null || !tLRPC$TL_chatBannedRights.send_stickers) {
                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2 = chat.banned_rights;
                if (tLRPC$TL_chatBannedRights2 != null) {
                    if (AndroidUtilities.isBannedForever(tLRPC$TL_chatBannedRights2)) {
                        if (z) {
                            this.mediaBanTooltip.setText(LocaleController.getString("AttachGifRestrictedForever", R.string.AttachGifRestrictedForever));
                        } else {
                            this.mediaBanTooltip.setText(LocaleController.getString("AttachStickersRestrictedForever", R.string.AttachStickersRestrictedForever));
                        }
                    } else if (z) {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachGifRestricted", R.string.AttachGifRestricted, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                    } else {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachStickersRestricted", R.string.AttachStickersRestricted, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                    }
                } else {
                    return;
                }
            } else if (z) {
                this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachGifRestricted", R.string.GlobalAttachGifRestricted));
            } else {
                this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachStickersRestricted", R.string.GlobalAttachStickersRestricted));
            }
            this.mediaBanTooltip.setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaBanTooltip, View.ALPHA, new float[]{0.0f, 1.0f})});
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AndroidUtilities.runOnUIThread(new EmojiView$37$$ExternalSyntheticLambda0(this), 5000);
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onAnimationEnd$0() {
                    if (EmojiView.this.mediaBanTooltip != null) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(EmojiView.this.mediaBanTooltip, View.ALPHA, new float[]{0.0f})});
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (EmojiView.this.mediaBanTooltip != null) {
                                    EmojiView.this.mediaBanTooltip.setVisibility(4);
                                }
                            }
                        });
                        animatorSet.setDuration(300);
                        animatorSet.start();
                    }
                }
            });
            animatorSet.setDuration(300);
            animatorSet.start();
        }
    }

    private void updateVisibleTrendingSets() {
        boolean z;
        RecyclerListView recyclerListView = this.stickersGridView;
        if (recyclerListView != null) {
            try {
                int childCount = recyclerListView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = this.stickersGridView.getChildAt(i);
                    if (childAt instanceof FeaturedStickerSetInfoCell) {
                        if (((RecyclerListView.Holder) this.stickersGridView.getChildViewHolder(childAt)) != null) {
                            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) childAt;
                            ArrayList<Long> unreadStickerSets = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
                            TLRPC$StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
                            boolean z2 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(stickerSet.set.id));
                            int i2 = 0;
                            while (true) {
                                TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr = this.primaryInstallingStickerSets;
                                if (i2 < tLRPC$StickerSetCoveredArr.length) {
                                    if (tLRPC$StickerSetCoveredArr[i2] != null && tLRPC$StickerSetCoveredArr[i2].set.id == stickerSet.set.id) {
                                        z = true;
                                        break;
                                    }
                                    i2++;
                                } else {
                                    z = false;
                                    break;
                                }
                            }
                            featuredStickerSetInfoCell.setStickerSet(stickerSet, z2, true, 0, 0, z);
                            if (z2) {
                                MediaDataController.getInstance(this.currentAccount).markFeaturedStickersByIdAsRead(false, stickerSet.set.id);
                            }
                            boolean z3 = this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0;
                            boolean z4 = this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0;
                            if (z3 || z4) {
                                if (z3 && featuredStickerSetInfoCell.isInstalled()) {
                                    this.installingStickerSets.remove(stickerSet.set.id);
                                    z3 = false;
                                } else if (z4 && !featuredStickerSetInfoCell.isInstalled()) {
                                    this.removingStickerSets.remove(stickerSet.set.id);
                                }
                            }
                            featuredStickerSetInfoCell.setAddDrawProgress(!z && z3, true);
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        Utilities.Callback remove;
        TLRPC$StickerSet tLRPC$StickerSet;
        int i3 = 0;
        if (i == NotificationCenter.stickersDidLoad) {
            if (objArr[0].intValue() == 0) {
                if (this.stickersGridAdapter != null) {
                    updateStickerTabs();
                    updateVisibleTrendingSets();
                    reloadStickersAdapter();
                    checkPanels();
                }
            } else if (objArr[0].intValue() == 5) {
                this.emojiAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.recentDocumentsDidLoad) {
            boolean booleanValue = objArr[0].booleanValue();
            int intValue = objArr[1].intValue();
            if (booleanValue || intValue == 0 || intValue == 2) {
                checkDocuments(booleanValue);
            }
        } else if (i == NotificationCenter.featuredStickersDidLoad) {
            updateVisibleTrendingSets();
            PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
            if (pagerSlidingTabStrip != null) {
                int childCount = pagerSlidingTabStrip.getChildCount();
                while (i3 < childCount) {
                    this.typeTabs.getChildAt(i3).invalidate();
                    i3++;
                }
            }
            updateStickerTabs();
        } else if (i == NotificationCenter.featuredEmojiDidLoad) {
            EmojiGridAdapter emojiGridAdapter = this.emojiAdapter;
            if (emojiGridAdapter != null) {
                emojiGridAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.groupStickersDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (!(tLRPC$ChatFull == null || (tLRPC$StickerSet = tLRPC$ChatFull.stickerset) == null || tLRPC$StickerSet.id != objArr[0].longValue())) {
                updateStickerTabs();
            }
            if (this.toInstall.containsKey(objArr[0]) && objArr.length >= 2) {
                long longValue = objArr[0].longValue();
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = objArr[1];
                if (this.toInstall.get(Long.valueOf(longValue)) != null && tLRPC$TL_messages_stickerSet != null && (remove = this.toInstall.remove(Long.valueOf(longValue))) != null) {
                    remove.run(tLRPC$TL_messages_stickerSet);
                }
            }
        } else if (i == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.stickersGridView;
            if (recyclerListView != null) {
                int childCount2 = recyclerListView.getChildCount();
                while (i3 < childCount2) {
                    View childAt = this.stickersGridView.getChildAt(i3);
                    if ((childAt instanceof StickerSetNameCell) || (childAt instanceof StickerEmojiCell)) {
                        childAt.invalidate();
                    }
                    i3++;
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
        } else if (i == NotificationCenter.newEmojiSuggestionsAvailable) {
            if (this.emojiGridView != null && this.needEmojiSearch) {
                if ((this.emojiSearchField.progressDrawable.isAnimating() || this.emojiGridView.getAdapter() == this.emojiSearchAdapter) && !TextUtils.isEmpty(this.emojiSearchAdapter.lastSearchEmojiString)) {
                    EmojiSearchAdapter emojiSearchAdapter2 = this.emojiSearchAdapter;
                    emojiSearchAdapter2.search(emojiSearchAdapter2.lastSearchEmojiString);
                }
            }
        } else if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            EmojiGridAdapter emojiGridAdapter2 = this.emojiAdapter;
            if (emojiGridAdapter2 != null) {
                emojiGridAdapter2.notifyDataSetChanged();
            }
            updateEmojiHeaders();
            updateStickerTabs();
        }
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    private class TrendingAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public boolean emoji;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public TrendingAdapter(boolean z) {
            this.emoji = z;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            AnonymousClass1 r4 = new BackupImageView(EmojiView.this.getContext()) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (MediaDataController.getInstance(EmojiView.this.currentAccount).isStickerPackUnread(TrendingAdapter.this.emoji, ((TLRPC$StickerSetCovered) getTag()).set.id) && EmojiView.this.dotPaint != null) {
                        canvas.drawCircle((float) (canvas.getWidth() - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(14.0f), (float) AndroidUtilities.dp(3.0f), EmojiView.this.dotPaint);
                    }
                }
            };
            float f = 36.0f;
            int dp = AndroidUtilities.dp(this.emoji ? 36.0f : 30.0f);
            if (!this.emoji) {
                f = 30.0f;
            }
            r4.setSize(dp, AndroidUtilities.dp(f));
            r4.setLayerNum(1);
            r4.setAspectFit(true);
            r4.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f)));
            return new RecyclerListView.Holder(r4);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ImageLocation forSticker;
            BackupImageView backupImageView = (BackupImageView) viewHolder.itemView;
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) (this.emoji ? EmojiView.this.featuredEmojiSets : EmojiView.this.featuredStickerSets).get(i);
            backupImageView.setTag(tLRPC$StickerSetCovered);
            TLRPC$Document tLRPC$Document = tLRPC$StickerSetCovered.cover;
            if (!tLRPC$StickerSetCovered.covers.isEmpty()) {
                tLRPC$Document = tLRPC$StickerSetCovered.covers.get(0);
            }
            Object closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$StickerSetCovered.set.thumbs, 90);
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$StickerSetCovered.set.thumbs, "emptyListPlaceholder", 0.2f);
            if (svgThumb != null) {
                svgThumb.overrideWidthAndHeight(512, 512);
            }
            if (closestPhotoSizeWithSize == null || MessageObject.isVideoSticker(tLRPC$Document)) {
                closestPhotoSizeWithSize = tLRPC$Document;
            }
            boolean z = closestPhotoSizeWithSize instanceof TLRPC$Document;
            if (z) {
                forSticker = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document);
            } else if (closestPhotoSizeWithSize instanceof TLRPC$PhotoSize) {
                forSticker = ImageLocation.getForSticker((TLRPC$PhotoSize) closestPhotoSizeWithSize, tLRPC$Document, tLRPC$StickerSetCovered.set.thumb_version);
            } else {
                return;
            }
            ImageLocation imageLocation = forSticker;
            if (imageLocation != null) {
                if (!z || (!MessageObject.isAnimatedStickerDocument(tLRPC$Document, true) && !MessageObject.isVideoSticker(tLRPC$Document))) {
                    if (imageLocation.imageType == 1) {
                        backupImageView.setImage(imageLocation, "30_30", "tgs", (Drawable) svgThumb, (Object) tLRPC$StickerSetCovered);
                    } else {
                        backupImageView.setImage(imageLocation, (String) null, "webp", (Drawable) svgThumb, (Object) tLRPC$StickerSetCovered);
                    }
                } else if (svgThumb != null) {
                    backupImageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "30_30", (Drawable) svgThumb, 0, (Object) tLRPC$StickerSetCovered);
                } else {
                    backupImageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "30_30", imageLocation, (String) null, 0, (Object) tLRPC$StickerSetCovered);
                }
            }
        }

        public int getItemCount() {
            return (this.emoji ? EmojiView.this.featuredEmojiSets : EmojiView.this.featuredStickerSets).size();
        }
    }

    private class TrendingListView extends RecyclerListView {
        public TrendingListView(Context context, RecyclerView.Adapter adapter) {
            super(context);
            setNestedScrollingEnabled(true);
            setSelectorRadius(AndroidUtilities.dp(4.0f));
            setSelectorDrawableColor(getThemedColor("listSelectorSDK21"));
            setTag(9);
            setItemAnimator((RecyclerView.ItemAnimator) null);
            setLayoutAnimation((LayoutAnimationController) null);
            AnonymousClass1 r0 = new LinearLayoutManager(this, context, EmojiView.this) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            r0.setOrientation(0);
            setLayoutManager(r0);
            setAdapter(adapter);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (!(getParent() == null || getParent().getParent() == null)) {
                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1) || canScrollHorizontally(1));
                EmojiView.this.pager.requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
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

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.itemView instanceof RecyclerListView;
        }

        public int getItemCount() {
            int i = this.totalItems;
            if (i != 0) {
                return i + 1;
            }
            return 0;
        }

        public int getPositionForPack(Object obj) {
            Integer num = this.packStartPosition.get(obj);
            if (num == null) {
                return -1;
            }
            return num.intValue();
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 4;
            }
            Object obj = this.cache.get(i);
            if (obj == null) {
                return 1;
            }
            if (obj instanceof TLRPC$Document) {
                return 0;
            }
            if (!(obj instanceof String)) {
                return 2;
            }
            if ("trend1".equals(obj)) {
                return 5;
            }
            return "trend2".equals(obj) ? 6 : 3;
        }

        public int getTabForPosition(int i) {
            Object obj = this.cache.get(i);
            if (!"search".equals(obj) && !"trend1".equals(obj) && !"trend2".equals(obj)) {
                if (i == 0) {
                    i = 1;
                }
                if (this.stickersPerRow == 0) {
                    int measuredWidth = EmojiView.this.getMeasuredWidth();
                    if (measuredWidth == 0) {
                        measuredWidth = AndroidUtilities.displaySize.x;
                    }
                    this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
                }
                int i2 = this.positionToRow.get(i, Integer.MIN_VALUE);
                if (i2 == Integer.MIN_VALUE) {
                    return (EmojiView.this.stickerSets.size() - 1) + EmojiView.this.stickersTabOffset;
                }
                Object obj2 = this.rowStartPack.get(i2);
                if (!(obj2 instanceof String)) {
                    return EmojiView.this.stickerSets.indexOf((TLRPC$TL_messages_stickerSet) obj2) + EmojiView.this.stickersTabOffset;
                } else if ("premium".equals(obj2)) {
                    return EmojiView.this.premiumTabNum;
                } else {
                    if ("recent".equals(obj2)) {
                        return EmojiView.this.recentTabNum;
                    }
                    return EmojiView.this.favTabNum;
                }
            } else if (EmojiView.this.favTabNum >= 0) {
                return EmojiView.this.favTabNum;
            } else {
                if (EmojiView.this.recentTabNum >= 0) {
                    return EmojiView.this.recentTabNum;
                }
                return 0;
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$1(StickerSetNameCell stickerSetNameCell, View view) {
            RecyclerView.ViewHolder childViewHolder;
            if (EmojiView.this.stickersGridView.indexOfChild(stickerSetNameCell) != -1 && (childViewHolder = EmojiView.this.stickersGridView.getChildViewHolder(stickerSetNameCell)) != null) {
                if (childViewHolder.getAdapterPosition() == EmojiView.this.groupStickerPackPosition) {
                    if (EmojiView.this.groupStickerSet == null) {
                        SharedPreferences.Editor edit = MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit();
                        edit.putLong("group_hide_stickers_" + EmojiView.this.info.id, EmojiView.this.info.stickerset != null ? EmojiView.this.info.stickerset.id : 0).apply();
                        EmojiView.this.updateStickerTabs();
                        if (EmojiView.this.stickersGridAdapter != null) {
                            EmojiView.this.stickersGridAdapter.notifyDataSetChanged();
                        }
                    } else if (EmojiView.this.delegate != null) {
                        EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
                    }
                } else if (this.cache.get(childViewHolder.getAdapterPosition()) == EmojiView.this.recentStickers) {
                    AlertDialog create = new AlertDialog.Builder(this.context).setTitle(LocaleController.getString(R.string.ClearRecentStickersAlertTitle)).setMessage(LocaleController.getString(R.string.ClearRecentStickersAlertMessage)).setPositiveButton(LocaleController.getString(R.string.ClearButton), new EmojiView$StickersGridAdapter$$ExternalSyntheticLambda0(this)).setNegativeButton(LocaleController.getString(R.string.Cancel), (DialogInterface.OnClickListener) null).create();
                    create.show();
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$0(DialogInterface dialogInterface, int i) {
            MediaDataController.getInstance(EmojiView.this.currentAccount).clearRecentStickers();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$2(View view) {
            if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$3(View view) {
            ArrayList<TLRPC$StickerSetCovered> featuredStickerSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getFeaturedStickerSets();
            if (!featuredStickerSets.isEmpty()) {
                MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit().putLong("featured_hidden", featuredStickerSets.get(0).set.id).commit();
                if (EmojiView.this.stickersGridAdapter != null) {
                    EmojiView.this.stickersGridAdapter.notifyItemRangeRemoved(1, 2);
                }
                EmojiView.this.updateStickerTabs();
            }
        }

        @SuppressLint({"NotifyDataSetChanged"})
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TrendingListView trendingListView;
            switch (i) {
                case 0:
                    trendingListView = new StickerEmojiCell(this, this.context, true) {
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    break;
                case 1:
                    trendingListView = new EmptyCell(this.context);
                    break;
                case 2:
                    StickerSetNameCell stickerSetNameCell = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    stickerSetNameCell.setOnIconClickListener(new EmojiView$StickersGridAdapter$$ExternalSyntheticLambda3(this, stickerSetNameCell));
                    trendingListView = stickerSetNameCell;
                    break;
                case 3:
                    StickerSetGroupInfoCell stickerSetGroupInfoCell = new StickerSetGroupInfoCell(this.context);
                    stickerSetGroupInfoCell.setAddOnClickListener(new EmojiView$StickersGridAdapter$$ExternalSyntheticLambda2(this));
                    stickerSetGroupInfoCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    trendingListView = stickerSetGroupInfoCell;
                    break;
                case 4:
                    View view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    trendingListView = view;
                    break;
                case 5:
                    StickerSetNameCell stickerSetNameCell2 = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    stickerSetNameCell2.setOnIconClickListener(new EmojiView$StickersGridAdapter$$ExternalSyntheticLambda1(this));
                    trendingListView = stickerSetNameCell2;
                    break;
                case 6:
                    EmojiView emojiView = EmojiView.this;
                    TrendingListView trendingListView2 = new TrendingListView(this.context, emojiView.trendingAdapter = new TrendingAdapter(false));
                    trendingListView2.addItemDecoration(new RecyclerView.ItemDecoration(this) {
                        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                            rect.right = AndroidUtilities.dp(8.0f);
                        }
                    });
                    trendingListView2.setOnItemClickListener((RecyclerListView.OnItemClickListener) new EmojiView$StickersGridAdapter$$ExternalSyntheticLambda4(this));
                    trendingListView2.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(52.0f)));
                    trendingListView = trendingListView2;
                    break;
                default:
                    trendingListView = null;
                    break;
            }
            return new RecyclerListView.Holder(trendingListView);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$4(View view, int i) {
            EmojiView.this.openTrendingStickers((TLRPC$StickerSetCovered) view.getTag());
        }

        /* JADX WARNING: type inference failed for: r2v1 */
        /* JADX WARNING: type inference failed for: r2v2, types: [java.util.ArrayList] */
        /* JADX WARNING: type inference failed for: r2v8, types: [org.telegram.tgnet.TLRPC$Chat] */
        /* JADX WARNING: type inference failed for: r2v16 */
        /* JADX WARNING: type inference failed for: r2v17 */
        /* JADX WARNING: type inference failed for: r2v18 */
        /* JADX WARNING: type inference failed for: r2v19 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r7, int r8) {
            /*
                r6 = this;
                int r0 = r7.getItemViewType()
                r1 = 0
                if (r0 == 0) goto L_0x0191
                r2 = 0
                r3 = 1
                if (r0 == r3) goto L_0x010a
                r4 = 2
                if (r0 == r4) goto L_0x0049
                r2 = 3
                if (r0 == r2) goto L_0x003a
                r8 = 5
                if (r0 == r8) goto L_0x0016
                goto L_0x01b3
            L_0x0016:
                android.view.View r7 = r7.itemView
                org.telegram.ui.Cells.StickerSetNameCell r7 = (org.telegram.ui.Cells.StickerSetNameCell) r7
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                int r8 = r8.currentAccount
                org.telegram.messenger.MediaDataController r8 = org.telegram.messenger.MediaDataController.getInstance(r8)
                boolean r8 = r8.loadFeaturedPremium
                if (r8 == 0) goto L_0x002b
                int r8 = org.telegram.messenger.R.string.FeaturedStickersPremium
                java.lang.String r0 = "FeaturedStickersPremium"
                goto L_0x002f
            L_0x002b:
                int r8 = org.telegram.messenger.R.string.FeaturedStickers
                java.lang.String r0 = "FeaturedStickers"
            L_0x002f:
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r0, r8)
                int r0 = org.telegram.messenger.R.drawable.msg_close
                r7.setText(r8, r0)
                goto L_0x01b3
            L_0x003a:
                android.view.View r7 = r7.itemView
                org.telegram.ui.Cells.StickerSetGroupInfoCell r7 = (org.telegram.ui.Cells.StickerSetGroupInfoCell) r7
                int r0 = r6.totalItems
                int r0 = r0 - r3
                if (r8 != r0) goto L_0x0044
                r1 = 1
            L_0x0044:
                r7.setIsLast(r1)
                goto L_0x01b3
            L_0x0049:
                android.view.View r7 = r7.itemView
                org.telegram.ui.Cells.StickerSetNameCell r7 = (org.telegram.ui.Cells.StickerSetNameCell) r7
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                int r0 = r0.groupStickerPackPosition
                if (r8 != r0) goto L_0x00ac
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                boolean r8 = r8.groupStickersHidden
                if (r8 == 0) goto L_0x0067
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = r8.groupStickerSet
                if (r8 != 0) goto L_0x0067
                r8 = 0
                goto L_0x0074
            L_0x0067:
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = r8.groupStickerSet
                if (r8 == 0) goto L_0x0072
                int r8 = org.telegram.messenger.R.drawable.msg_mini_customize
                goto L_0x0074
            L_0x0072:
                int r8 = org.telegram.messenger.R.drawable.msg_close
            L_0x0074:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.tgnet.TLRPC$ChatFull r0 = r0.info
                if (r0 == 0) goto L_0x0094
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                int r0 = r0.currentAccount
                org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
                org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.info
                long r4 = r2.id
                java.lang.Long r2 = java.lang.Long.valueOf(r4)
                org.telegram.tgnet.TLRPC$Chat r2 = r0.getChat(r2)
            L_0x0094:
                int r0 = org.telegram.messenger.R.string.CurrentGroupStickers
                java.lang.Object[] r3 = new java.lang.Object[r3]
                if (r2 == 0) goto L_0x009d
                java.lang.String r2 = r2.title
                goto L_0x009f
            L_0x009d:
                java.lang.String r2 = "Group Stickers"
            L_0x009f:
                r3[r1] = r2
                java.lang.String r1 = "CurrentGroupStickers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
                r7.setText(r0, r8)
                goto L_0x01b3
            L_0x00ac:
                android.util.SparseArray<java.lang.Object> r0 = r6.cache
                java.lang.Object r8 = r0.get(r8)
                boolean r0 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
                if (r0 == 0) goto L_0x00c3
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r8
                org.telegram.tgnet.TLRPC$StickerSet r8 = r8.set
                if (r8 == 0) goto L_0x01b3
                java.lang.String r8 = r8.title
                r7.setText(r8, r1)
                goto L_0x01b3
            L_0x00c3:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r0 = r0.recentStickers
                if (r8 != r0) goto L_0x00e0
                int r8 = org.telegram.messenger.R.string.RecentStickers
                java.lang.String r0 = "RecentStickers"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r0, r8)
                int r0 = org.telegram.messenger.R.drawable.msg_close
                int r1 = org.telegram.messenger.R.string.ClearRecentStickersAlertTitle
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString((int) r1)
                r7.setText(r8, r0, r1)
                goto L_0x01b3
            L_0x00e0:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r0 = r0.favouriteStickers
                if (r8 != r0) goto L_0x00f5
                int r8 = org.telegram.messenger.R.string.FavoriteStickers
                java.lang.String r0 = "FavoriteStickers"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r0, r8)
                r7.setText(r8, r1)
                goto L_0x01b3
            L_0x00f5:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r0 = r0.premiumStickers
                if (r8 != r0) goto L_0x01b3
                int r8 = org.telegram.messenger.R.string.PremiumStickers
                java.lang.String r0 = "PremiumStickers"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r0, r8)
                r7.setText(r8, r1)
                goto L_0x01b3
            L_0x010a:
                android.view.View r7 = r7.itemView
                org.telegram.ui.Cells.EmptyCell r7 = (org.telegram.ui.Cells.EmptyCell) r7
                int r0 = r6.totalItems
                r1 = 1118044160(0x42a40000, float:82.0)
                if (r8 != r0) goto L_0x0189
                android.util.SparseIntArray r0 = r6.positionToRow
                int r8 = r8 - r3
                r4 = -2147483648(0xfffffffvar_, float:-0.0)
                int r8 = r0.get(r8, r4)
                if (r8 != r4) goto L_0x0124
                r7.setHeight(r3)
                goto L_0x01b3
            L_0x0124:
                android.util.SparseArray<java.lang.Object> r0 = r6.rowStartPack
                java.lang.Object r8 = r0.get(r8)
                boolean r0 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
                if (r0 == 0) goto L_0x0133
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r8
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r8.documents
                goto L_0x014c
            L_0x0133:
                boolean r0 = r8 instanceof java.lang.String
                if (r0 == 0) goto L_0x014c
                java.lang.String r0 = "recent"
                boolean r8 = r0.equals(r8)
                if (r8 == 0) goto L_0x0146
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r2 = r8.recentStickers
                goto L_0x014c
            L_0x0146:
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r2 = r8.favouriteStickers
            L_0x014c:
                if (r2 != 0) goto L_0x0152
                r7.setHeight(r3)
                goto L_0x01b3
            L_0x0152:
                boolean r8 = r2.isEmpty()
                if (r8 == 0) goto L_0x0162
                r8 = 1090519040(0x41000000, float:8.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r7.setHeight(r8)
                goto L_0x01b3
            L_0x0162:
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                androidx.viewpager.widget.ViewPager r8 = r8.pager
                int r8 = r8.getHeight()
                int r0 = r2.size()
                float r0 = (float) r0
                int r2 = r6.stickersPerRow
                float r2 = (float) r2
                float r0 = r0 / r2
                double r4 = (double) r0
                double r4 = java.lang.Math.ceil(r4)
                int r0 = (int) r4
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r0 = r0 * r1
                int r8 = r8 - r0
                if (r8 <= 0) goto L_0x0185
                r3 = r8
            L_0x0185:
                r7.setHeight(r3)
                goto L_0x01b3
            L_0x0189:
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r7.setHeight(r8)
                goto L_0x01b3
            L_0x0191:
                android.util.SparseArray<java.lang.Object> r0 = r6.cache
                java.lang.Object r0 = r0.get(r8)
                org.telegram.tgnet.TLRPC$Document r0 = (org.telegram.tgnet.TLRPC$Document) r0
                android.view.View r7 = r7.itemView
                org.telegram.ui.Cells.StickerEmojiCell r7 = (org.telegram.ui.Cells.StickerEmojiCell) r7
                android.util.SparseArray<java.lang.Object> r2 = r6.cacheParents
                java.lang.Object r8 = r2.get(r8)
                r7.setSticker(r0, r8, r1)
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r8 = r8.recentStickers
                boolean r8 = r8.contains(r0)
                r7.setRecent(r8)
            L_0x01b3:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersGridAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        private void updateItems() {
            ArrayList arrayList;
            Object obj;
            ArrayList<TLRPC$Document> arrayList2;
            int i;
            int measuredWidth = EmojiView.this.getMeasuredWidth();
            if (measuredWidth == 0) {
                measuredWidth = AndroidUtilities.displaySize.x;
            }
            this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            EmojiView.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
            this.rowStartPack.clear();
            this.packStartPosition.clear();
            this.positionToRow.clear();
            this.cache.clear();
            int i2 = 0;
            this.totalItems = 0;
            ArrayList access$9700 = EmojiView.this.stickerSets;
            int i3 = -5;
            int i4 = -5;
            int i5 = 0;
            while (i4 < access$9700.size()) {
                if (i4 == i3) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i6 = this.totalItems;
                    this.totalItems = i6 + 1;
                    sparseArray.put(i6, "search");
                    i5++;
                } else if (i4 == -4) {
                    MediaDataController instance = MediaDataController.getInstance(EmojiView.this.currentAccount);
                    SharedPreferences emojiSettings = MessagesController.getEmojiSettings(EmojiView.this.currentAccount);
                    ArrayList<TLRPC$StickerSetCovered> featuredStickerSets = instance.getFeaturedStickerSets();
                    if (!EmojiView.this.featuredStickerSets.isEmpty() && emojiSettings.getLong("featured_hidden", 0) != featuredStickerSets.get(i2).set.id) {
                        SparseArray<Object> sparseArray2 = this.cache;
                        int i7 = this.totalItems;
                        this.totalItems = i7 + 1;
                        sparseArray2.put(i7, "trend1");
                        SparseArray<Object> sparseArray3 = this.cache;
                        int i8 = this.totalItems;
                        this.totalItems = i8 + 1;
                        sparseArray3.put(i8, "trend2");
                        i5 += 2;
                    }
                } else {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
                    if (i4 == -3) {
                        arrayList2 = EmojiView.this.favouriteStickers;
                        this.packStartPosition.put("fav", Integer.valueOf(this.totalItems));
                        obj = "fav";
                    } else if (i4 == -2) {
                        arrayList2 = EmojiView.this.recentStickers;
                        this.packStartPosition.put("recent", Integer.valueOf(this.totalItems));
                        obj = "recent";
                    } else if (i4 == -1) {
                        arrayList2 = EmojiView.this.premiumStickers;
                        this.packStartPosition.put("premium", Integer.valueOf(this.totalItems));
                        obj = "premium";
                    } else {
                        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = (TLRPC$TL_messages_stickerSet) access$9700.get(i4);
                        ArrayList<TLRPC$Document> arrayList3 = tLRPC$TL_messages_stickerSet2.documents;
                        this.packStartPosition.put(tLRPC$TL_messages_stickerSet2, Integer.valueOf(this.totalItems));
                        tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet2;
                        arrayList2 = arrayList3;
                        obj = null;
                    }
                    if (i4 == EmojiView.this.groupStickerPackNum) {
                        int unused = EmojiView.this.groupStickerPackPosition = this.totalItems;
                        if (arrayList2.isEmpty()) {
                            this.rowStartPack.put(i5, tLRPC$TL_messages_stickerSet);
                            int i9 = i5 + 1;
                            this.positionToRow.put(this.totalItems, i5);
                            this.rowStartPack.put(i9, tLRPC$TL_messages_stickerSet);
                            this.positionToRow.put(this.totalItems + 1, i9);
                            SparseArray<Object> sparseArray4 = this.cache;
                            int i10 = this.totalItems;
                            this.totalItems = i10 + 1;
                            sparseArray4.put(i10, tLRPC$TL_messages_stickerSet);
                            SparseArray<Object> sparseArray5 = this.cache;
                            int i11 = this.totalItems;
                            this.totalItems = i11 + 1;
                            sparseArray5.put(i11, "group");
                            arrayList = access$9700;
                            i5 = i9 + 1;
                            i4++;
                            access$9700 = arrayList;
                            i2 = 0;
                            i3 = -5;
                        }
                    }
                    if (!arrayList2.isEmpty()) {
                        int ceil = (int) Math.ceil((double) (((float) arrayList2.size()) / ((float) this.stickersPerRow)));
                        if (tLRPC$TL_messages_stickerSet != null) {
                            this.cache.put(this.totalItems, tLRPC$TL_messages_stickerSet);
                        } else {
                            this.cache.put(this.totalItems, arrayList2);
                        }
                        this.positionToRow.put(this.totalItems, i5);
                        int i12 = 0;
                        while (i12 < arrayList2.size()) {
                            int i13 = i12 + 1;
                            int i14 = this.totalItems + i13;
                            this.cache.put(i14, arrayList2.get(i12));
                            if (tLRPC$TL_messages_stickerSet != null) {
                                this.cacheParents.put(i14, tLRPC$TL_messages_stickerSet);
                            } else {
                                this.cacheParents.put(i14, obj);
                            }
                            this.positionToRow.put(this.totalItems + i13, i5 + 1 + (i12 / this.stickersPerRow));
                            i12 = i13;
                            access$9700 = access$9700;
                        }
                        arrayList = access$9700;
                        int i15 = 0;
                        while (true) {
                            i = ceil + 1;
                            if (i15 >= i) {
                                break;
                            }
                            if (tLRPC$TL_messages_stickerSet != null) {
                                this.rowStartPack.put(i5 + i15, tLRPC$TL_messages_stickerSet);
                            } else if (i4 == -1) {
                                this.rowStartPack.put(i5 + i15, "premium");
                            } else {
                                if (i4 == -2) {
                                    this.rowStartPack.put(i5 + i15, "recent");
                                } else {
                                    this.rowStartPack.put(i5 + i15, "fav");
                                }
                                i15++;
                            }
                            i15++;
                        }
                        this.totalItems += (ceil * this.stickersPerRow) + 1;
                        i5 += i;
                        i4++;
                        access$9700 = arrayList;
                        i2 = 0;
                        i3 = -5;
                    }
                }
                arrayList = access$9700;
                i4++;
                access$9700 = arrayList;
                i2 = 0;
                i3 = -5;
            }
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            updateItems();
            super.notifyItemRangeRemoved(i, i2);
        }

        public void notifyDataSetChanged() {
            updateItems();
            super.notifyDataSetChanged();
        }
    }

    public static class EmojiPackExpand extends FrameLayout {
        public TextView textView;

        public EmojiPackExpand(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextSize(1, 13.0f);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhite", resourcesProvider));
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0f), ColorUtils.setAlphaComponent(Theme.getColor("chat_emojiPanelStickerSetName", resourcesProvider), 99)));
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(1.66f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(2.0f));
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }
    }

    private class EmojiGridAdapter extends RecyclerListView.SelectionAdapter {
        private int firstTrendingRow;
        private int itemCount;
        /* access modifiers changed from: private */
        public ArrayList<Integer> packStartPosition;
        public int plainEmojisCount;
        /* access modifiers changed from: private */
        public SparseIntArray positionToExpand;
        /* access modifiers changed from: private */
        public SparseIntArray positionToSection;
        /* access modifiers changed from: private */
        public SparseIntArray positionToUnlock;
        /* access modifiers changed from: private */
        public ArrayList<Integer> rowHashCodes;
        /* access modifiers changed from: private */
        public SparseIntArray sectionToPosition;
        /* access modifiers changed from: private */
        public int trendingHeaderRow;
        /* access modifiers changed from: private */
        public int trendingRow;

        public long getItemId(int i) {
            return (long) i;
        }

        private EmojiGridAdapter() {
            this.trendingHeaderRow = -1;
            this.trendingRow = -1;
            this.firstTrendingRow = -1;
            this.rowHashCodes = new ArrayList<>();
            this.positionToSection = new SparseIntArray();
            this.sectionToPosition = new SparseIntArray();
            this.positionToUnlock = new SparseIntArray();
            this.positionToExpand = new SparseIntArray();
            this.packStartPosition = new ArrayList<>();
        }

        public int getItemCount() {
            return this.itemCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 4 || itemViewType == 3 || itemViewType == 6;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$0(View view, int i) {
            ArrayList arrayList = new ArrayList();
            ArrayList<TLRPC$StickerSetCovered> featuredEmojiSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getFeaturedEmojiSets();
            int i2 = 0;
            while (featuredEmojiSets != null && i2 < featuredEmojiSets.size()) {
                TLRPC$StickerSetCovered tLRPC$StickerSetCovered = featuredEmojiSets.get(i2);
                if (!(tLRPC$StickerSetCovered == null || tLRPC$StickerSetCovered.set == null)) {
                    TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                    TLRPC$StickerSet tLRPC$StickerSet = tLRPC$StickerSetCovered.set;
                    tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                    tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                    arrayList.add(tLRPC$TL_inputStickerSetID);
                }
                i2++;
            }
            MediaDataController.getInstance(EmojiView.this.currentAccount).markFeaturedStickersAsRead(true, true);
            if (EmojiView.this.fragment != null) {
                EmojiView.this.fragment.showDialog(new EmojiPacksAlert(EmojiView.this.fragment, EmojiView.this.getContext(), EmojiView.this.fragment.getResourceProvider(), arrayList));
            } else {
                new EmojiPacksAlert((BaseFragment) null, EmojiView.this.getContext(), EmojiView.this.resourcesProvider, arrayList).show();
            }
        }

        /* JADX WARNING: type inference failed for: r6v6, types: [org.telegram.ui.Components.EmojiView$TrendingListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, org.telegram.ui.Components.RecyclerListView] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
            /*
                r4 = this;
                if (r6 == 0) goto L_0x00a2
                r5 = 1
                if (r6 == r5) goto L_0x008f
                r0 = 3
                if (r6 == r0) goto L_0x0083
                r0 = 4
                if (r6 == r0) goto L_0x004b
                r5 = 5
                if (r6 == r5) goto L_0x003f
                r5 = 6
                if (r6 == r5) goto L_0x002d
                android.view.View r5 = new android.view.View
                org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                android.content.Context r6 = r6.getContext()
                r5.<init>(r6)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r6 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                int r1 = r1.searchFieldHeight
                r6.<init>((int) r0, (int) r1)
                r5.setLayoutParams(r6)
                goto L_0x00ad
            L_0x002d:
                org.telegram.ui.Components.EmojiView$EmojiPackExpand r5 = new org.telegram.ui.Components.EmojiView$EmojiPackExpand
                org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                android.content.Context r6 = r6.getContext()
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r0.resourcesProvider
                r5.<init>(r6, r0)
                goto L_0x00ad
            L_0x003f:
                org.telegram.ui.Components.EmojiView$EmojiPackHeader r5 = new org.telegram.ui.Components.EmojiView$EmojiPackHeader
                org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                android.content.Context r0 = r6.getContext()
                r5.<init>(r6, r0)
                goto L_0x00ad
            L_0x004b:
                org.telegram.ui.Components.EmojiView$TrendingListView r6 = new org.telegram.ui.Components.EmojiView$TrendingListView
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                android.content.Context r1 = r0.getContext()
                org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$TrendingAdapter r3 = new org.telegram.ui.Components.EmojiView$TrendingAdapter
                r3.<init>(r5)
                org.telegram.ui.Components.EmojiView$TrendingAdapter r5 = r2.trendingEmojiAdapter = r3
                r6.<init>(r1, r5)
                r5 = 1090519040(0x41000000, float:8.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r1 = 0
                r6.setPadding(r0, r1, r5, r1)
                r6.setClipToPadding(r1)
                org.telegram.ui.Components.EmojiView$EmojiGridAdapter$1 r5 = new org.telegram.ui.Components.EmojiView$EmojiGridAdapter$1
                r5.<init>(r4)
                r6.addItemDecoration(r5)
                org.telegram.ui.Components.EmojiView$EmojiGridAdapter$$ExternalSyntheticLambda1 r5 = new org.telegram.ui.Components.EmojiView$EmojiGridAdapter$$ExternalSyntheticLambda1
                r5.<init>(r4)
                r6.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r5)
                goto L_0x00a0
            L_0x0083:
                org.telegram.ui.Components.EmojiView$EmojiPackButton r5 = new org.telegram.ui.Components.EmojiView$EmojiPackButton
                org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                android.content.Context r0 = r6.getContext()
                r5.<init>(r6, r0)
                goto L_0x00ad
            L_0x008f:
                org.telegram.ui.Cells.StickerSetNameCell r6 = new org.telegram.ui.Cells.StickerSetNameCell
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                android.content.Context r0 = r0.getContext()
                org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r1.resourcesProvider
                r6.<init>(r0, r5, r1)
            L_0x00a0:
                r5 = r6
                goto L_0x00ad
            L_0x00a2:
                org.telegram.ui.Components.EmojiView$ImageViewEmoji r5 = new org.telegram.ui.Components.EmojiView$ImageViewEmoji
                org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                android.content.Context r0 = r6.getContext()
                r5.<init>(r0)
            L_0x00ad:
                org.telegram.ui.Components.RecyclerListView$Holder r6 = new org.telegram.ui.Components.RecyclerListView$Holder
                r6.<init>(r5)
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiGridAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v52, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: org.telegram.ui.Components.EmojiView$EmojiPack} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v56, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: org.telegram.ui.Components.EmojiView$EmojiPack} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r13, @android.annotation.SuppressLint({"RecyclerView"}) int r14) {
            /*
                r12 = this;
                int r0 = r13.getItemViewType()
                r1 = 0
                r2 = 1
                r3 = 0
                if (r0 == 0) goto L_0x0103
                if (r0 == r2) goto L_0x00b8
                r4 = 5
                if (r0 == r4) goto L_0x0066
                r1 = 6
                if (r0 == r1) goto L_0x0013
                goto L_0x0269
            L_0x0013:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Components.EmojiView$EmojiPackExpand r13 = (org.telegram.ui.Components.EmojiView.EmojiPackExpand) r13
                android.util.SparseIntArray r0 = r12.positionToExpand
                int r14 = r0.get(r14)
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                androidx.recyclerview.widget.GridLayoutManager r0 = r0.emojiLayoutManager
                int r0 = r0.getSpanCount()
                int r0 = r0 * 3
                if (r14 < 0) goto L_0x0044
                org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r1 = r1.emojipacksProcessed
                int r1 = r1.size()
                if (r14 >= r1) goto L_0x0044
                org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r1 = r1.emojipacksProcessed
                java.lang.Object r14 = r1.get(r14)
                r3 = r14
                org.telegram.ui.Components.EmojiView$EmojiPack r3 = (org.telegram.ui.Components.EmojiView.EmojiPack) r3
            L_0x0044:
                if (r3 == 0) goto L_0x0269
                android.widget.TextView r13 = r13.textView
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                java.lang.String r1 = "+"
                r14.append(r1)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r3.documents
                int r1 = r1.size()
                int r1 = r1 - r0
                int r1 = r1 + r2
                r14.append(r1)
                java.lang.String r14 = r14.toString()
                r13.setText(r14)
                goto L_0x0269
            L_0x0066:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Components.EmojiView$EmojiPackHeader r13 = (org.telegram.ui.Components.EmojiView.EmojiPackHeader) r13
                android.util.SparseIntArray r0 = r12.positionToSection
                int r14 = r0.get(r14)
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.lang.String[] r0 = r0.emojiTitles
                int r0 = r0.length
                int r14 = r14 - r0
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r0 = r0.emojipacksProcessed
                java.lang.Object r0 = r0.get(r14)
                org.telegram.ui.Components.EmojiView$EmojiPack r0 = (org.telegram.ui.Components.EmojiView.EmojiPack) r0
                int r14 = r14 - r2
                if (r14 < 0) goto L_0x0094
                org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r3 = r3.emojipacksProcessed
                java.lang.Object r14 = r3.get(r14)
                r3 = r14
                org.telegram.ui.Components.EmojiView$EmojiPack r3 = (org.telegram.ui.Components.EmojiView.EmojiPack) r3
            L_0x0094:
                if (r0 == 0) goto L_0x00b3
                boolean r14 = r0.featured
                if (r14 == 0) goto L_0x00b3
                if (r3 == 0) goto L_0x00b2
                boolean r14 = r3.free
                if (r14 != 0) goto L_0x00b2
                boolean r14 = r3.installed
                if (r14 == 0) goto L_0x00b2
                org.telegram.ui.Components.EmojiView r14 = org.telegram.ui.Components.EmojiView.this
                int r14 = r14.currentAccount
                org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)
                boolean r14 = r14.isPremium()
                if (r14 == 0) goto L_0x00b3
            L_0x00b2:
                r1 = 1
            L_0x00b3:
                r13.setStickerSet(r0, r1)
                goto L_0x0269
            L_0x00b8:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.StickerSetNameCell r13 = (org.telegram.ui.Cells.StickerSetNameCell) r13
                r13.position = r14
                android.util.SparseIntArray r0 = r12.positionToSection
                int r0 = r0.get(r14)
                int r2 = r12.trendingHeaderRow
                if (r14 != r2) goto L_0x00d1
                int r14 = org.telegram.messenger.R.string.FeaturedEmojiPacks
                java.lang.String r0 = "FeaturedEmojiPacks"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                goto L_0x00fe
            L_0x00d1:
                org.telegram.ui.Components.EmojiView r14 = org.telegram.ui.Components.EmojiView.this
                java.lang.String[] r14 = r14.emojiTitles
                int r14 = r14.length
                if (r0 < r14) goto L_0x00f6
                org.telegram.ui.Components.EmojiView r14 = org.telegram.ui.Components.EmojiView.this     // Catch:{ Exception -> 0x00f3 }
                java.util.ArrayList r14 = r14.emojipacksProcessed     // Catch:{ Exception -> 0x00f3 }
                org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this     // Catch:{ Exception -> 0x00f3 }
                java.lang.String[] r2 = r2.emojiTitles     // Catch:{ Exception -> 0x00f3 }
                int r2 = r2.length     // Catch:{ Exception -> 0x00f3 }
                int r0 = r0 - r2
                java.lang.Object r14 = r14.get(r0)     // Catch:{ Exception -> 0x00f3 }
                org.telegram.ui.Components.EmojiView$EmojiPack r14 = (org.telegram.ui.Components.EmojiView.EmojiPack) r14     // Catch:{ Exception -> 0x00f3 }
                org.telegram.tgnet.TLRPC$StickerSet r14 = r14.set     // Catch:{ Exception -> 0x00f3 }
                java.lang.String r14 = r14.title     // Catch:{ Exception -> 0x00f3 }
                goto L_0x00fe
            L_0x00f3:
                java.lang.String r14 = ""
                goto L_0x00fe
            L_0x00f6:
                org.telegram.ui.Components.EmojiView r14 = org.telegram.ui.Components.EmojiView.this
                java.lang.String[] r14 = r14.emojiTitles
                r14 = r14[r0]
            L_0x00fe:
                r13.setText(r14, r1)
                goto L_0x0269
            L_0x0103:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Components.EmojiView$ImageViewEmoji r13 = (org.telegram.ui.Components.EmojiView.ImageViewEmoji) r13
                r13.position = r14
                org.telegram.ui.Components.EmojiView.EmojiPack unused = r13.pack = r3
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                boolean r0 = r0.needEmojiSearch
                if (r0 == 0) goto L_0x0116
                int r14 = r14 + -1
            L_0x0116:
                int r0 = r12.trendingRow
                if (r0 < 0) goto L_0x011c
                int r14 = r14 + -2
            L_0x011c:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r0 = r0.getRecentEmoji()
                int r0 = r0.size()
                if (r14 >= r0) goto L_0x0156
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r0 = r0.getRecentEmoji()
                java.lang.Object r14 = r0.get(r14)
                java.lang.String r14 = (java.lang.String) r14
                if (r14 == 0) goto L_0x014f
                java.lang.String r0 = "animated_"
                boolean r0 = r14.startsWith(r0)
                if (r0 == 0) goto L_0x014f
                r0 = 9
                java.lang.String r0 = r14.substring(r0)     // Catch:{ Exception -> 0x014f }
                long r4 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x014f }
                java.lang.Long r14 = java.lang.Long.valueOf(r4)     // Catch:{ Exception -> 0x014f }
                r0 = r3
                r4 = r0
                goto L_0x0152
            L_0x014f:
                r0 = r14
                r4 = r0
                r14 = r3
            L_0x0152:
                r5 = r4
                r4 = r3
                goto L_0x020b
            L_0x0156:
                r4 = 0
            L_0x0157:
                java.lang.String[][] r5 = org.telegram.messenger.EmojiData.dataColored
                int r6 = r5.length
                if (r4 >= r6) goto L_0x017f
                r6 = r5[r4]
                int r6 = r6.length
                int r6 = r6 + r2
                int r6 = r6 + r0
                if (r14 >= r6) goto L_0x017b
                r4 = r5[r4]
                int r14 = r14 - r0
                int r14 = r14 - r2
                r14 = r4[r14]
                java.util.HashMap<java.lang.String, java.lang.String> r0 = org.telegram.messenger.Emoji.emojiColor
                java.lang.Object r0 = r0.get(r14)
                java.lang.String r0 = (java.lang.String) r0
                if (r0 == 0) goto L_0x0179
                java.lang.String r0 = org.telegram.ui.Components.EmojiView.addColorToCode(r14, r0)
                r4 = r14
                goto L_0x0181
            L_0x0179:
                r0 = r14
                goto L_0x0180
            L_0x017b:
                int r4 = r4 + 1
                r0 = r6
                goto L_0x0157
            L_0x017f:
                r0 = r3
            L_0x0180:
                r4 = r0
            L_0x0181:
                if (r4 != 0) goto L_0x0206
                org.telegram.ui.Components.EmojiView r14 = org.telegram.ui.Components.EmojiView.this
                int r14 = r14.currentAccount
                org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)
                boolean r14 = r14.isPremium()
                org.telegram.ui.Components.EmojiView r5 = org.telegram.ui.Components.EmojiView.this
                androidx.recyclerview.widget.GridLayoutManager r5 = r5.emojiLayoutManager
                int r5 = r5.getSpanCount()
                int r5 = r5 * 3
                r6 = 0
            L_0x019c:
                java.util.ArrayList<java.lang.Integer> r7 = r12.packStartPosition
                int r7 = r7.size()
                if (r6 >= r7) goto L_0x0206
                org.telegram.ui.Components.EmojiView r7 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r7 = r7.emojipacksProcessed
                java.lang.Object r7 = r7.get(r6)
                org.telegram.ui.Components.EmojiView$EmojiPack r7 = (org.telegram.ui.Components.EmojiView.EmojiPack) r7
                java.util.ArrayList<java.lang.Integer> r8 = r12.packStartPosition
                java.lang.Object r8 = r8.get(r6)
                java.lang.Integer r8 = (java.lang.Integer) r8
                int r8 = r8.intValue()
                int r8 = r8 + r2
                boolean r9 = r7.installed
                if (r9 == 0) goto L_0x01cb
                boolean r9 = r7.featured
                if (r9 != 0) goto L_0x01cb
                boolean r9 = r7.free
                if (r9 != 0) goto L_0x01cf
                if (r14 != 0) goto L_0x01cf
            L_0x01cb:
                boolean r9 = r7.expanded
                if (r9 == 0) goto L_0x01d6
            L_0x01cf:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r7.documents
                int r9 = r9.size()
                goto L_0x01e0
            L_0x01d6:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r7.documents
                int r9 = r9.size()
                int r9 = java.lang.Math.min(r5, r9)
            L_0x01e0:
                int r10 = r13.position
                if (r10 < r8) goto L_0x0203
                int r10 = r10 - r8
                if (r10 >= r9) goto L_0x0203
                org.telegram.ui.Components.EmojiView.EmojiPack unused = r13.pack = r7
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r14 = r7.documents
                int r2 = r13.position
                int r2 = r2 - r8
                java.lang.Object r14 = r14.get(r2)
                org.telegram.tgnet.TLRPC$Document r14 = (org.telegram.tgnet.TLRPC$Document) r14
                if (r14 != 0) goto L_0x01f9
                r2 = r3
                goto L_0x01ff
            L_0x01f9:
                long r5 = r14.id
                java.lang.Long r2 = java.lang.Long.valueOf(r5)
            L_0x01ff:
                r11 = r2
                r2 = r14
                r14 = r11
                goto L_0x0208
            L_0x0203:
                int r6 = r6 + 1
                goto L_0x019c
            L_0x0206:
                r14 = r3
                r2 = r14
            L_0x0208:
                r5 = r4
                r4 = r2
                r2 = 0
            L_0x020b:
                if (r14 == 0) goto L_0x0223
                r1 = 1077936128(0x40400000, float:3.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r13.setPadding(r6, r7, r8, r1)
                goto L_0x0226
            L_0x0223:
                r13.setPadding(r1, r1, r1, r1)
            L_0x0226:
                if (r14 == 0) goto L_0x0259
                r13.setImageDrawable(r3, r2)
                org.telegram.ui.Components.AnimatedEmojiSpan r1 = r13.getSpan()
                if (r1 == 0) goto L_0x0241
                org.telegram.ui.Components.AnimatedEmojiSpan r1 = r13.getSpan()
                long r1 = r1.getDocumentId()
                long r6 = r14.longValue()
                int r8 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
                if (r8 == 0) goto L_0x0263
            L_0x0241:
                if (r4 == 0) goto L_0x024c
                org.telegram.ui.Components.AnimatedEmojiSpan r14 = new org.telegram.ui.Components.AnimatedEmojiSpan
                r14.<init>((org.telegram.tgnet.TLRPC$Document) r4, (android.graphics.Paint.FontMetricsInt) r3)
                r13.setSpan(r14)
                goto L_0x0263
            L_0x024c:
                org.telegram.ui.Components.AnimatedEmojiSpan r1 = new org.telegram.ui.Components.AnimatedEmojiSpan
                long r6 = r14.longValue()
                r1.<init>((long) r6, (android.graphics.Paint.FontMetricsInt) r3)
                r13.setSpan(r1)
                goto L_0x0263
            L_0x0259:
                android.graphics.drawable.Drawable r14 = org.telegram.messenger.Emoji.getEmojiBigDrawable(r0)
                r13.setImageDrawable(r14, r2)
                r13.setSpan(r3)
            L_0x0263:
                r13.setTag(r5)
                r13.setContentDescription(r0)
            L_0x0269:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiGridAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (i == this.trendingRow) {
                return 4;
            }
            if (i == this.trendingHeaderRow) {
                return 1;
            }
            if (this.positionToSection.indexOfKey(i) >= 0) {
                if (this.positionToSection.get(i) >= EmojiData.dataColored.length) {
                    return 5;
                }
                return 1;
            } else if (EmojiView.this.needEmojiSearch && i == 0) {
                return 2;
            } else {
                if (this.positionToUnlock.indexOfKey(i) >= 0) {
                    return 3;
                }
                return this.positionToExpand.indexOfKey(i) >= 0 ? 6 : 0;
            }
        }

        public void processEmoji() {
            boolean z;
            EmojiView.this.emojipacksProcessed.clear();
            if (EmojiView.this.allowAnimatedEmoji) {
                MediaDataController instance = MediaDataController.getInstance(EmojiView.this.currentAccount);
                ArrayList arrayList = new ArrayList(instance.getStickerSets(5));
                boolean isPremium = UserConfig.getInstance(EmojiView.this.currentAccount).isPremium();
                if (!isPremium) {
                    int i = 0;
                    while (i < arrayList.size()) {
                        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) arrayList.get(i);
                        if (tLRPC$TL_messages_stickerSet != null && !MessageObject.isPremiumEmojiPack(tLRPC$TL_messages_stickerSet)) {
                            EmojiPack emojiPack = new EmojiPack();
                            emojiPack.set = tLRPC$TL_messages_stickerSet.set;
                            emojiPack.documents = new ArrayList<>(tLRPC$TL_messages_stickerSet.documents);
                            emojiPack.free = true;
                            emojiPack.installed = instance.isStickerPackInstalled(tLRPC$TL_messages_stickerSet.set.id);
                            emojiPack.featured = false;
                            emojiPack.expanded = true;
                            EmojiView.this.emojipacksProcessed.add(emojiPack);
                            arrayList.remove(i);
                            i--;
                        }
                        i++;
                    }
                }
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = (TLRPC$TL_messages_stickerSet) arrayList.get(i2);
                    if (isPremium) {
                        EmojiPack emojiPack2 = new EmojiPack();
                        TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet2.set;
                        emojiPack2.set = tLRPC$StickerSet;
                        emojiPack2.documents = tLRPC$TL_messages_stickerSet2.documents;
                        emojiPack2.free = false;
                        emojiPack2.installed = instance.isStickerPackInstalled(tLRPC$StickerSet.id);
                        emojiPack2.featured = false;
                        emojiPack2.expanded = true;
                        EmojiView.this.emojipacksProcessed.add(emojiPack2);
                    } else {
                        ArrayList arrayList2 = new ArrayList();
                        ArrayList arrayList3 = new ArrayList();
                        if (!(tLRPC$TL_messages_stickerSet2 == null || tLRPC$TL_messages_stickerSet2.documents == null)) {
                            for (int i3 = 0; i3 < tLRPC$TL_messages_stickerSet2.documents.size(); i3++) {
                                if (MessageObject.isFreeEmoji(tLRPC$TL_messages_stickerSet2.documents.get(i3))) {
                                    arrayList2.add(tLRPC$TL_messages_stickerSet2.documents.get(i3));
                                } else {
                                    arrayList3.add(tLRPC$TL_messages_stickerSet2.documents.get(i3));
                                }
                            }
                        }
                        if (arrayList2.size() > 0) {
                            EmojiPack emojiPack3 = new EmojiPack();
                            emojiPack3.set = tLRPC$TL_messages_stickerSet2.set;
                            emojiPack3.documents = new ArrayList<>(arrayList2);
                            emojiPack3.free = true;
                            emojiPack3.installed = instance.isStickerPackInstalled(tLRPC$TL_messages_stickerSet2.set.id);
                            emojiPack3.featured = false;
                            emojiPack3.expanded = true;
                            EmojiView.this.emojipacksProcessed.add(emojiPack3);
                        }
                        if (arrayList3.size() > 0) {
                            EmojiPack emojiPack4 = new EmojiPack();
                            emojiPack4.set = tLRPC$TL_messages_stickerSet2.set;
                            emojiPack4.documents = new ArrayList<>(arrayList3);
                            emojiPack4.free = false;
                            emojiPack4.installed = instance.isStickerPackInstalled(tLRPC$TL_messages_stickerSet2.set.id);
                            emojiPack4.featured = false;
                            emojiPack4.expanded = EmojiView.this.expandedEmojiSets.contains(Long.valueOf(emojiPack4.set.id));
                            EmojiView.this.emojipacksProcessed.add(emojiPack4);
                        }
                    }
                }
                for (int i4 = 0; i4 < EmojiView.this.featuredEmojiSets.size(); i4++) {
                    TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) EmojiView.this.featuredEmojiSets.get(i4);
                    EmojiPack emojiPack5 = new EmojiPack();
                    emojiPack5.installed = instance.isStickerPackInstalled(tLRPC$StickerSetCovered.set.id);
                    emojiPack5.set = tLRPC$StickerSetCovered.set;
                    emojiPack5.documents = tLRPC$StickerSetCovered instanceof TLRPC$TL_stickerSetFullCovered ? ((TLRPC$TL_stickerSetFullCovered) tLRPC$StickerSetCovered).documents : tLRPC$StickerSetCovered.covers;
                    int i5 = 0;
                    while (true) {
                        if (i5 >= emojiPack5.documents.size()) {
                            z = false;
                            break;
                        } else if (!MessageObject.isFreeEmoji(emojiPack5.documents.get(i5))) {
                            z = true;
                            break;
                        } else {
                            i5++;
                        }
                    }
                    emojiPack5.free = !z;
                    emojiPack5.expanded = EmojiView.this.expandedEmojiSets.contains(Long.valueOf(emojiPack5.set.id));
                    emojiPack5.featured = true;
                    EmojiView.this.emojipacksProcessed.add(emojiPack5);
                }
                if (EmojiView.this.emojiTabs != null) {
                    EmojiView.this.emojiTabs.updateEmojiPacks();
                }
            }
        }

        public void expand(int i, View view) {
            int i2 = this.positionToExpand.get(i);
            if (i2 >= 0 && i2 < EmojiView.this.emojipacksProcessed.size()) {
                EmojiPack emojiPack = (EmojiPack) EmojiView.this.emojipacksProcessed.get(i2);
                if (!emojiPack.expanded) {
                    boolean z = i2 + 1 == EmojiView.this.emojipacksProcessed.size();
                    int intValue = this.packStartPosition.get(i2).intValue();
                    EmojiView.this.expandedEmojiSets.add(Long.valueOf(emojiPack.set.id));
                    boolean isPremium = UserConfig.getInstance(EmojiView.this.currentAccount).isPremium();
                    int spanCount = EmojiView.this.emojiLayoutManager.getSpanCount() * 3;
                    int min = ((!emojiPack.installed || emojiPack.featured || (!emojiPack.free && !isPremium)) && !emojiPack.expanded) ? Math.min(spanCount, emojiPack.documents.size()) : emojiPack.documents.size();
                    Integer num = null;
                    Integer valueOf = emojiPack.documents.size() > spanCount ? Integer.valueOf(intValue + 1 + min) : null;
                    emojiPack.expanded = true;
                    int size = emojiPack.documents.size() - min;
                    if (size > 0) {
                        valueOf = Integer.valueOf(intValue + 1 + min);
                        num = Integer.valueOf(size);
                    }
                    processEmoji();
                    updateRows();
                    if (valueOf != null && num != null) {
                        View unused = EmojiView.this.animateExpandFromButton = view;
                        int unused2 = EmojiView.this.animateExpandFromPosition = valueOf.intValue();
                        int unused3 = EmojiView.this.animateExpandToPosition = valueOf.intValue() + num.intValue();
                        long unused4 = EmojiView.this.animateExpandStartTime = SystemClock.elapsedRealtime();
                        notifyItemRangeInserted(valueOf.intValue(), num.intValue());
                        notifyItemChanged(valueOf.intValue());
                        if (z) {
                            EmojiView.this.post(new EmojiView$EmojiGridAdapter$$ExternalSyntheticLambda0(this, num.intValue() > spanCount / 2 ? 1.5f : 4.0f, valueOf.intValue()));
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$expand$1(float f, int i) {
            try {
                LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(EmojiView.this.emojiGridView.getContext(), 0, f);
                linearSmoothScrollerCustom.setTargetPosition(i);
                EmojiView.this.emojiLayoutManager.startSmoothScroll(linearSmoothScrollerCustom);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void updateRows() {
            this.positionToSection.clear();
            this.sectionToPosition.clear();
            this.positionToUnlock.clear();
            this.positionToExpand.clear();
            this.packStartPosition.clear();
            this.rowHashCodes.clear();
            this.itemCount = 0;
            if (EmojiView.this.needEmojiSearch) {
                this.itemCount++;
                this.rowHashCodes.add(-1);
            }
            ArrayList<String> recentEmoji = EmojiView.this.getRecentEmoji();
            if (EmojiView.this.emojiTabs != null) {
                EmojiView.this.emojiTabs.showRecent(!recentEmoji.isEmpty());
            }
            this.itemCount += recentEmoji.size();
            for (int i = 0; i < recentEmoji.size(); i++) {
                this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{-43263, recentEmoji.get(i)})));
            }
            int i2 = 0;
            int i3 = 0;
            while (true) {
                String[][] strArr = EmojiData.dataColored;
                if (i2 >= strArr.length) {
                    break;
                }
                this.positionToSection.put(this.itemCount, i3);
                this.sectionToPosition.put(i3, this.itemCount);
                this.itemCount += strArr[i2].length + 1;
                this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{43245, Integer.valueOf(i2)})));
                int i4 = 0;
                while (true) {
                    String[][] strArr2 = EmojiData.dataColored;
                    if (i4 >= strArr2[i2].length) {
                        break;
                    }
                    this.rowHashCodes.add(Integer.valueOf(strArr2[i2][i4].hashCode()));
                    i4++;
                }
                i2++;
                i3++;
            }
            boolean isPremium = UserConfig.getInstance(EmojiView.this.currentAccount).isPremium();
            int spanCount = EmojiView.this.emojiLayoutManager.getSpanCount() * 3;
            this.plainEmojisCount = this.itemCount;
            this.firstTrendingRow = -1;
            if (EmojiView.this.emojipacksProcessed != null) {
                int i5 = 0;
                while (i5 < EmojiView.this.emojipacksProcessed.size()) {
                    this.positionToSection.put(this.itemCount, i3);
                    this.sectionToPosition.put(i3, this.itemCount);
                    this.packStartPosition.add(Integer.valueOf(this.itemCount));
                    EmojiPack emojiPack = (EmojiPack) EmojiView.this.emojipacksProcessed.get(i5);
                    boolean z = emojiPack.featured;
                    if (z && this.firstTrendingRow < 0) {
                        this.firstTrendingRow = this.itemCount;
                    }
                    int min = (((!emojiPack.installed || z || (!emojiPack.free && !isPremium)) && !emojiPack.expanded) ? Math.min(spanCount, emojiPack.documents.size()) : emojiPack.documents.size()) + 1;
                    if (!emojiPack.expanded && emojiPack.documents.size() > spanCount) {
                        min--;
                    }
                    ArrayList<Integer> arrayList = this.rowHashCodes;
                    Object[] objArr = new Object[2];
                    objArr[0] = Integer.valueOf(emojiPack.featured ? 56345 : -645231);
                    TLRPC$StickerSet tLRPC$StickerSet = emojiPack.set;
                    objArr[1] = Long.valueOf(tLRPC$StickerSet == null ? (long) i5 : tLRPC$StickerSet.id);
                    arrayList.add(Integer.valueOf(Arrays.hashCode(objArr)));
                    for (int i6 = 1; i6 < min; i6++) {
                        ArrayList<Integer> arrayList2 = this.rowHashCodes;
                        Object[] objArr2 = new Object[2];
                        objArr2[0] = Integer.valueOf(emojiPack.featured ? 3442 : 3213);
                        objArr2[1] = Long.valueOf(emojiPack.documents.get(i6 - 1).id);
                        arrayList2.add(Integer.valueOf(Arrays.hashCode(objArr2)));
                    }
                    this.itemCount += min;
                    if (!emojiPack.expanded && emojiPack.documents.size() > spanCount) {
                        this.positionToExpand.put(this.itemCount, i5);
                        this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{-65174, Long.valueOf(emojiPack.set.id)})));
                        this.itemCount++;
                    }
                    i5++;
                    i3++;
                }
            }
        }

        public void notifyDataSetChanged() {
            final ArrayList arrayList = new ArrayList(this.rowHashCodes);
            MediaDataController instance = MediaDataController.getInstance(EmojiView.this.currentAccount);
            ArrayList<TLRPC$StickerSetCovered> featuredEmojiSets = instance.getFeaturedEmojiSets();
            EmojiView.this.featuredEmojiSets.clear();
            int size = featuredEmojiSets.size();
            for (int i = 0; i < size; i++) {
                TLRPC$StickerSetCovered tLRPC$StickerSetCovered = featuredEmojiSets.get(i);
                if (!instance.isStickerPackInstalled(tLRPC$StickerSetCovered.set.id) || EmojiView.this.installedEmojiSets.contains(Long.valueOf(tLRPC$StickerSetCovered.set.id))) {
                    EmojiView.this.featuredEmojiSets.add(tLRPC$StickerSetCovered);
                }
            }
            processEmoji();
            updateRows();
            if (EmojiView.this.trendingEmojiAdapter != null) {
                EmojiView.this.trendingEmojiAdapter.notifyDataSetChanged();
            }
            DiffUtil.calculateDiff(new DiffUtil.Callback() {
                public boolean areContentsTheSame(int i, int i2) {
                    return true;
                }

                public int getOldListSize() {
                    return arrayList.size();
                }

                public int getNewListSize() {
                    return EmojiGridAdapter.this.rowHashCodes.size();
                }

                public boolean areItemsTheSame(int i, int i2) {
                    return ((Integer) arrayList.get(i)).equals(EmojiGridAdapter.this.rowHashCodes.get(i2));
                }
            }, false).dispatchUpdatesTo((RecyclerView.Adapter) this);
        }
    }

    public ArrayList<EmojiPack> getEmojipacks() {
        ArrayList<EmojiPack> arrayList = new ArrayList<>();
        for (int i = 0; i < this.emojipacksProcessed.size(); i++) {
            EmojiPack emojiPack = this.emojipacksProcessed.get(i);
            if ((!emojiPack.featured && (emojiPack.installed || this.installedEmojiSets.contains(Long.valueOf(emojiPack.set.id)))) || (emojiPack.featured && !emojiPack.installed && !this.installedEmojiSets.contains(Long.valueOf(emojiPack.set.id)))) {
                arrayList.add(emojiPack);
            }
        }
        return arrayList;
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
            int size;
            if (this.result.isEmpty() && !this.searchWas) {
                size = EmojiView.this.getRecentEmoji().size();
            } else if (this.result.isEmpty()) {
                return 2;
            } else {
                size = this.result.size();
            }
            return size + 1;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            AnonymousClass1 r12;
            if (i == 0) {
                EmojiView emojiView = EmojiView.this;
                r12 = new ImageViewEmoji(emojiView.getContext());
            } else if (i != 1) {
                AnonymousClass1 r122 = new FrameLayout(EmojiView.this.getContext()) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        int i3;
                        View view = (View) EmojiView.this.getParent();
                        if (view != null) {
                            i3 = (int) (((float) view.getMeasuredHeight()) - EmojiView.this.getY());
                        } else {
                            i3 = AndroidUtilities.dp(120.0f);
                        }
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(i3 - EmojiView.this.searchFieldHeight, NUM));
                    }
                };
                TextView textView = new TextView(EmojiView.this.getContext());
                textView.setText(LocaleController.getString("NoEmojiFound", R.string.NoEmojiFound));
                textView.setTextSize(1, 16.0f);
                textView.setTextColor(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"));
                r122.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
                ImageView imageView = new ImageView(EmojiView.this.getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setImageResource(R.drawable.msg_emoji_question);
                imageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
                r122.addView(imageView, LayoutHelper.createFrame(48, 48, 85));
                imageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        final boolean[] zArr = new boolean[1];
                        final BottomSheet.Builder builder = new BottomSheet.Builder(EmojiView.this.getContext());
                        LinearLayout linearLayout = new LinearLayout(EmojiView.this.getContext());
                        linearLayout.setOrientation(1);
                        linearLayout.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                        ImageView imageView = new ImageView(EmojiView.this.getContext());
                        imageView.setImageResource(R.drawable.smiles_info);
                        linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 15, 0, 0));
                        TextView textView = new TextView(EmojiView.this.getContext());
                        textView.setText(LocaleController.getString("EmojiSuggestions", R.string.EmojiSuggestions));
                        textView.setTextSize(1, 15.0f);
                        textView.setTextColor(EmojiView.this.getThemedColor("dialogTextBlue2"));
                        int i = 5;
                        textView.setGravity(LocaleController.isRTL ? 5 : 3);
                        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 51, 0, 24, 0, 0));
                        TextView textView2 = new TextView(EmojiView.this.getContext());
                        textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString("EmojiSuggestionsInfo", R.string.EmojiSuggestionsInfo)));
                        textView2.setTextSize(1, 15.0f);
                        textView2.setTextColor(EmojiView.this.getThemedColor("dialogTextBlack"));
                        textView2.setGravity(LocaleController.isRTL ? 5 : 3);
                        linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 51, 0, 11, 0, 0));
                        TextView textView3 = new TextView(EmojiView.this.getContext());
                        int i2 = R.string.EmojiSuggestionsUrl;
                        Object[] objArr = new Object[1];
                        objArr[0] = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage;
                        textView3.setText(LocaleController.formatString("EmojiSuggestionsUrl", i2, objArr));
                        textView3.setTextSize(1, 15.0f);
                        textView3.setTextColor(EmojiView.this.getThemedColor("dialogTextLink"));
                        if (!LocaleController.isRTL) {
                            i = 3;
                        }
                        textView3.setGravity(i);
                        linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 51, 0, 18, 0, 16));
                        textView3.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                boolean[] zArr = zArr;
                                if (!zArr[0]) {
                                    zArr[0] = true;
                                    AlertDialog[] alertDialogArr = {new AlertDialog(EmojiView.this.getContext(), 3)};
                                    TLRPC$TL_messages_getEmojiURL tLRPC$TL_messages_getEmojiURL = new TLRPC$TL_messages_getEmojiURL();
                                    tLRPC$TL_messages_getEmojiURL.lang_code = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage[0];
                                    AndroidUtilities.runOnUIThread(new EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda1(this, alertDialogArr, ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_messages_getEmojiURL, new EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda3(this, alertDialogArr, builder))), 1000);
                                }
                            }

                            /* access modifiers changed from: private */
                            public /* synthetic */ void lambda$onClick$1(AlertDialog[] alertDialogArr, BottomSheet.Builder builder, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                AndroidUtilities.runOnUIThread(new EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda2(this, alertDialogArr, tLObject, builder));
                            }

                            /* access modifiers changed from: private */
                            public /* synthetic */ void lambda$onClick$0(AlertDialog[] alertDialogArr, TLObject tLObject, BottomSheet.Builder builder) {
                                try {
                                    alertDialogArr[0].dismiss();
                                } catch (Throwable unused) {
                                }
                                alertDialogArr[0] = null;
                                if (tLObject instanceof TLRPC$TL_emojiURL) {
                                    Browser.openUrl(EmojiView.this.getContext(), ((TLRPC$TL_emojiURL) tLObject).url);
                                    builder.getDismissRunnable().run();
                                }
                            }

                            /* access modifiers changed from: private */
                            public /* synthetic */ void lambda$onClick$3(AlertDialog[] alertDialogArr, int i) {
                                if (alertDialogArr[0] != null) {
                                    alertDialogArr[0].setOnCancelListener(new EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda0(this, i));
                                    alertDialogArr[0].show();
                                }
                            }

                            /* access modifiers changed from: private */
                            public /* synthetic */ void lambda$onClick$2(int i, DialogInterface dialogInterface) {
                                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(i, true);
                            }
                        });
                        builder.setCustomView(linearLayout);
                        builder.show();
                    }
                });
                r122.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                r12 = r122;
            } else {
                View view = new View(EmojiView.this.getContext());
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                r12 = view;
            }
            return new RecyclerListView.Holder(r12);
        }

        /* JADX WARNING: Removed duplicated region for block: B:18:0x0058  */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x006e  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x0073  */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x0099  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r9, int r10) {
            /*
                r8 = this;
                int r0 = r9.getItemViewType()
                if (r0 == 0) goto L_0x0008
                goto L_0x00a6
            L_0x0008:
                android.view.View r9 = r9.itemView
                org.telegram.ui.Components.EmojiView$ImageViewEmoji r9 = (org.telegram.ui.Components.EmojiView.ImageViewEmoji) r9
                r0 = 0
                org.telegram.ui.Components.EmojiView.EmojiPack unused = r9.pack = r0
                int r10 = r10 + -1
                java.util.ArrayList<org.telegram.messenger.MediaDataController$KeywordResult> r1 = r8.result
                boolean r1 = r1.isEmpty()
                r2 = 0
                if (r1 == 0) goto L_0x002d
                boolean r1 = r8.searchWas
                if (r1 != 0) goto L_0x002d
                org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r1 = r1.getRecentEmoji()
                java.lang.Object r10 = r1.get(r10)
                java.lang.String r10 = (java.lang.String) r10
                r1 = 1
                goto L_0x0038
            L_0x002d:
                java.util.ArrayList<org.telegram.messenger.MediaDataController$KeywordResult> r1 = r8.result
                java.lang.Object r10 = r1.get(r10)
                org.telegram.messenger.MediaDataController$KeywordResult r10 = (org.telegram.messenger.MediaDataController.KeywordResult) r10
                java.lang.String r10 = r10.emoji
                r1 = 0
            L_0x0038:
                if (r10 == 0) goto L_0x0053
                java.lang.String r3 = "animated_"
                boolean r3 = r10.startsWith(r3)
                if (r3 == 0) goto L_0x0053
                r3 = 9
                java.lang.String r3 = r10.substring(r3)     // Catch:{ Exception -> 0x0053 }
                long r3 = java.lang.Long.parseLong(r3)     // Catch:{ Exception -> 0x0053 }
                java.lang.Long r10 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x0053 }
                r3 = r0
                r4 = r3
                goto L_0x0056
            L_0x0053:
                r3 = r10
                r4 = r3
                r10 = r0
            L_0x0056:
                if (r10 == 0) goto L_0x006e
                r2 = 1077936128(0x40400000, float:3.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r9.setPadding(r5, r6, r7, r2)
                goto L_0x0071
            L_0x006e:
                r9.setPadding(r2, r2, r2, r2)
            L_0x0071:
                if (r10 == 0) goto L_0x0099
                r9.setImageDrawable(r0, r1)
                org.telegram.ui.Components.AnimatedEmojiSpan r1 = r9.getSpan()
                if (r1 == 0) goto L_0x008c
                org.telegram.ui.Components.AnimatedEmojiSpan r1 = r9.getSpan()
                long r1 = r1.getDocumentId()
                long r5 = r10.longValue()
                int r3 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                if (r3 == 0) goto L_0x00a3
            L_0x008c:
                org.telegram.ui.Components.AnimatedEmojiSpan r1 = new org.telegram.ui.Components.AnimatedEmojiSpan
                long r2 = r10.longValue()
                r1.<init>((long) r2, (android.graphics.Paint.FontMetricsInt) r0)
                r9.setSpan(r1)
                goto L_0x00a3
            L_0x0099:
                android.graphics.drawable.Drawable r10 = org.telegram.messenger.Emoji.getEmojiBigDrawable(r3)
                r9.setImageDrawable(r10, r1)
                r9.setSpan(r0)
            L_0x00a3:
                r9.setTag(r4)
            L_0x00a6:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 1;
            }
            return (i != 1 || !this.searchWas || !this.result.isEmpty()) ? 0 : 2;
        }

        public void search(String str) {
            if (TextUtils.isEmpty(str)) {
                this.lastSearchEmojiString = null;
                if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiAdapter) {
                    EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiAdapter);
                    this.searchWas = false;
                }
                notifyDataSetChanged();
            } else {
                this.lastSearchEmojiString = str.toLowerCase();
            }
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            if (!TextUtils.isEmpty(this.lastSearchEmojiString)) {
                AnonymousClass3 r3 = new Runnable() {
                    public void run() {
                        EmojiView.this.emojiSearchField.progressDrawable.startAnimation();
                        final String access$1200 = EmojiSearchAdapter.this.lastSearchEmojiString;
                        String[] currentKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        if (!Arrays.equals(EmojiView.this.lastSearchKeyboardLanguage, currentKeyboardLanguage)) {
                            MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(currentKeyboardLanguage);
                        }
                        String[] unused = EmojiView.this.lastSearchKeyboardLanguage = currentKeyboardLanguage;
                        MediaDataController.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, EmojiSearchAdapter.this.lastSearchEmojiString, false, new MediaDataController.KeywordResultCallback() {
                            public void run(ArrayList<MediaDataController.KeywordResult> arrayList, String str) {
                                if (access$1200.equals(EmojiSearchAdapter.this.lastSearchEmojiString)) {
                                    String unused = EmojiSearchAdapter.this.lastSearchAlias = str;
                                    EmojiView.this.emojiSearchField.progressDrawable.stopAnimation();
                                    boolean unused2 = EmojiSearchAdapter.this.searchWas = true;
                                    if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                                        EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiSearchAdapter);
                                    }
                                    ArrayList unused3 = EmojiSearchAdapter.this.result = arrayList;
                                    EmojiSearchAdapter.this.notifyDataSetChanged();
                                }
                            }
                        }, true);
                    }
                };
                this.searchRunnable = r3;
                AndroidUtilities.runOnUIThread(r3, 300);
            }
        }
    }

    private class EmojiPagesAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        private EmojiPagesAdapter() {
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        public boolean canScrollToTab(int i) {
            boolean z = true;
            if ((i != 1 && i != 2) || EmojiView.this.currentChatId == 0) {
                return true;
            }
            EmojiView emojiView = EmojiView.this;
            if (i != 1) {
                z = false;
            }
            emojiView.showStickerBanHint(z);
            return false;
        }

        public int getCount() {
            return EmojiView.this.currentTabs.size();
        }

        public Drawable getPageIconDrawable(int i) {
            return EmojiView.this.tabIcons[i];
        }

        public CharSequence getPageTitle(int i) {
            if (i == 0) {
                return LocaleController.getString("Emoji", R.string.Emoji);
            }
            if (i == 1) {
                return LocaleController.getString("AccDescrGIFs", R.string.AccDescrGIFs);
            }
            if (i != 2) {
                return null;
            }
            return LocaleController.getString("AccDescrStickers", R.string.AccDescrStickers);
        }

        public void customOnDraw(Canvas canvas, int i) {
            if (i == 2 && !MediaDataController.getInstance(EmojiView.this.currentAccount).getUnreadStickerSets().isEmpty() && EmojiView.this.dotPaint != null) {
                canvas.drawCircle((float) ((canvas.getWidth() / 2) + AndroidUtilities.dp(9.0f)), (float) ((canvas.getHeight() / 2) - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(5.0f), EmojiView.this.dotPaint);
            }
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View view = ((Tab) EmojiView.this.currentTabs.get(i)).view;
            viewGroup.addView(view);
            return view;
        }
    }

    private class GifAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public TLRPC$User bot;
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
        public ArrayList<TLRPC$BotInlineResult> results;
        private HashMap<String, TLRPC$BotInlineResult> resultsMap;
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
        public GifAdapter(EmojiView emojiView, Context context2, boolean z) {
            this(context2, z, z ? Integer.MAX_VALUE : 0);
        }

        public GifAdapter(Context context2, boolean z, int i) {
            GifProgressEmptyView gifProgressEmptyView;
            this.results = new ArrayList<>();
            this.resultsMap = new HashMap<>();
            this.trendingSectionItem = -1;
            this.firstResultItem = -1;
            this.context = context2;
            this.withRecent = z;
            this.maxRecentRowsCount = i;
            if (z) {
                gifProgressEmptyView = null;
            } else {
                gifProgressEmptyView = new GifProgressEmptyView(context2);
            }
            this.progressEmptyView = gifProgressEmptyView;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public int getItemCount() {
            return this.itemsCount;
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 1;
            }
            boolean z = this.withRecent;
            if (!z || i != this.trendingSectionItem) {
                return (z || !this.results.isEmpty()) ? 0 : 3;
            }
            return 2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            GifProgressEmptyView gifProgressEmptyView;
            if (i == 0) {
                ContextLinkCell contextLinkCell = new ContextLinkCell(this.context);
                contextLinkCell.setCanPreviewGif(true);
                gifProgressEmptyView = contextLinkCell;
            } else if (i == 1) {
                View view = new View(EmojiView.this.getContext());
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                gifProgressEmptyView = view;
            } else if (i != 2) {
                GifProgressEmptyView gifProgressEmptyView2 = this.progressEmptyView;
                gifProgressEmptyView2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                gifProgressEmptyView = gifProgressEmptyView2;
            } else {
                StickerSetNameCell stickerSetNameCell = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                stickerSetNameCell.setText(LocaleController.getString("FeaturedGifs", R.string.FeaturedGifs), 0);
                RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, -2);
                layoutParams.topMargin = AndroidUtilities.dp(2.5f);
                layoutParams.bottomMargin = AndroidUtilities.dp(5.5f);
                stickerSetNameCell.setLayoutParams(layoutParams);
                gifProgressEmptyView = stickerSetNameCell;
            }
            return new RecyclerListView.Holder(gifProgressEmptyView);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ContextLinkCell contextLinkCell = (ContextLinkCell) viewHolder.itemView;
                int i2 = this.firstResultItem;
                if (i2 < 0 || i < i2) {
                    contextLinkCell.setGif((TLRPC$Document) EmojiView.this.recentGifs.get(i - 1), false);
                } else {
                    contextLinkCell.setLink(this.results.get(i - i2), this.bot, true, false, false, true);
                }
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
                    int measuredWidth = EmojiView.this.gifGridView.getMeasuredWidth();
                    int spanCount = EmojiView.this.gifLayoutManager.getSpanCount();
                    int dp = AndroidUtilities.dp(100.0f);
                    this.recentItemsCount = 0;
                    int size = EmojiView.this.recentGifs.size();
                    int i2 = spanCount;
                    int i3 = 0;
                    int i4 = 0;
                    for (int i5 = 0; i5 < size; i5++) {
                        Size fixSize = EmojiView.this.gifLayoutManager.fixSize(EmojiView.this.gifLayoutManager.getSizeForItem((TLRPC$Document) EmojiView.this.recentGifs.get(i5)));
                        int min = Math.min(spanCount, (int) Math.floor((double) (((float) spanCount) * (((fixSize.width / fixSize.height) * ((float) dp)) / ((float) measuredWidth)))));
                        if (i2 < min) {
                            this.recentItemsCount += i3;
                            i4++;
                            if (i4 == this.maxRecentRowsCount) {
                                break;
                            }
                            i2 = spanCount;
                            i3 = 0;
                        }
                        i3++;
                        i2 -= min;
                    }
                    if (i4 < this.maxRecentRowsCount) {
                        this.recentItemsCount += i3;
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
                TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
                tLRPC$TL_contacts_resolveUsername.username = MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot;
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_contacts_resolveUsername, new EmojiView$GifAdapter$$ExternalSyntheticLambda3(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchBotUser$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject != null) {
                AndroidUtilities.runOnUIThread(new EmojiView$GifAdapter$$ExternalSyntheticLambda2(this, tLObject));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchBotUser$0(TLObject tLObject) {
            TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
            MessagesController.getInstance(EmojiView.this.currentAccount).putUsers(tLRPC$TL_contacts_resolvedPeer.users, false);
            MessagesController.getInstance(EmojiView.this.currentAccount).putChats(tLRPC$TL_contacts_resolvedPeer.chats, false);
            MessagesStorage.getInstance(EmojiView.this.currentAccount).putUsersAndChats(tLRPC$TL_contacts_resolvedPeer.users, tLRPC$TL_contacts_resolvedPeer.chats, true, true);
            String str = this.lastSearchImageString;
            this.lastSearchImageString = null;
            search(str, "", false);
        }

        public void search(String str) {
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
                if (TextUtils.isEmpty(str)) {
                    this.lastSearchImageString = null;
                    if (this.showTrendingWhenSearchEmpty) {
                        loadTrendingGifs();
                        return;
                    }
                    int currentPosition = EmojiView.this.gifTabs.getCurrentPosition();
                    if (currentPosition != EmojiView.this.gifRecentTabNum && currentPosition != EmojiView.this.gifTrendingTabNum) {
                        searchEmoji(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchEmojies.get(currentPosition - EmojiView.this.gifFirstEmojiTabNum));
                    } else if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifAdapter) {
                        EmojiView.this.gifGridView.setAdapter(EmojiView.this.gifAdapter);
                    }
                } else {
                    String lowerCase = str.toLowerCase();
                    this.lastSearchImageString = lowerCase;
                    if (!TextUtils.isEmpty(lowerCase)) {
                        EmojiView$GifAdapter$$ExternalSyntheticLambda0 emojiView$GifAdapter$$ExternalSyntheticLambda0 = new EmojiView$GifAdapter$$ExternalSyntheticLambda0(this, str);
                        this.searchRunnable = emojiView$GifAdapter$$ExternalSyntheticLambda0;
                        AndroidUtilities.runOnUIThread(emojiView$GifAdapter$$ExternalSyntheticLambda0, 300);
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$search$2(String str) {
            search(str, "", true);
        }

        public void searchEmoji(String str) {
            if (!this.lastSearchIsEmoji || !TextUtils.equals(this.lastSearchImageString, str)) {
                search(str, "", true, true, true);
            } else {
                EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(1, 0);
            }
        }

        /* access modifiers changed from: protected */
        public void search(String str, String str2, boolean z) {
            search(str, str2, z, false, false);
        }

        /* access modifiers changed from: protected */
        public void search(String str, String str2, boolean z, boolean z2, boolean z3) {
            int i = this.reqId;
            if (i != 0) {
                if (i >= 0) {
                    ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                }
                this.reqId = 0;
            }
            this.lastSearchImageString = str;
            this.lastSearchIsEmoji = z2;
            GifProgressEmptyView gifProgressEmptyView = this.progressEmptyView;
            if (gifProgressEmptyView != null) {
                gifProgressEmptyView.setLoadingState(z2);
            }
            TLObject userOrChat = MessagesController.getInstance(EmojiView.this.currentAccount).getUserOrChat(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot);
            if (userOrChat instanceof TLRPC$User) {
                if (!this.withRecent && TextUtils.isEmpty(str2)) {
                    EmojiView.this.gifSearchField.progressDrawable.startAnimation();
                }
                this.bot = (TLRPC$User) userOrChat;
                String str3 = "gif_search_" + str + "_" + str2;
                EmojiView$GifAdapter$$ExternalSyntheticLambda4 emojiView$GifAdapter$$ExternalSyntheticLambda4 = new EmojiView$GifAdapter$$ExternalSyntheticLambda4(this, str, str2, z, z2, z3, str3);
                if (!z3 && !this.withRecent && z2 && TextUtils.isEmpty(str2)) {
                    this.results.clear();
                    this.resultsMap.clear();
                    if (EmojiView.this.gifGridView.getAdapter() != this) {
                        EmojiView.this.gifGridView.setAdapter(this);
                    }
                    notifyDataSetChanged();
                    EmojiView.this.scrollGifsToTop();
                }
                if (z3 && EmojiView.this.gifCache.containsKey(str3)) {
                    lambda$search$3(str, str2, z, z2, true, str3, (TLObject) EmojiView.this.gifCache.get(str3));
                } else if (!EmojiView.this.gifSearchPreloader.isLoading(str3)) {
                    if (z3) {
                        this.reqId = -1;
                        MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(str3, emojiView$GifAdapter$$ExternalSyntheticLambda4);
                        return;
                    }
                    TLRPC$TL_messages_getInlineBotResults tLRPC$TL_messages_getInlineBotResults = new TLRPC$TL_messages_getInlineBotResults();
                    if (str == null) {
                        str = "";
                    }
                    tLRPC$TL_messages_getInlineBotResults.query = str;
                    tLRPC$TL_messages_getInlineBotResults.bot = MessagesController.getInstance(EmojiView.this.currentAccount).getInputUser(this.bot);
                    tLRPC$TL_messages_getInlineBotResults.offset = str2;
                    tLRPC$TL_messages_getInlineBotResults.peer = new TLRPC$TL_inputPeerEmpty();
                    this.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_messages_getInlineBotResults, emojiView$GifAdapter$$ExternalSyntheticLambda4, 2);
                }
            } else if (z) {
                searchBotUser();
                if (!this.withRecent) {
                    EmojiView.this.gifSearchField.progressDrawable.startAnimation();
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$search$4(String str, String str2, boolean z, boolean z2, boolean z3, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new EmojiView$GifAdapter$$ExternalSyntheticLambda1(this, str, str2, z, z2, z3, str3, tLObject));
        }

        /* access modifiers changed from: private */
        /* renamed from: processResponse */
        public void lambda$search$3(String str, String str2, boolean z, boolean z2, boolean z3, String str3, TLObject tLObject) {
            if (str != null && str.equals(this.lastSearchImageString)) {
                boolean z4 = false;
                this.reqId = 0;
                if (!z3 || ((tLObject instanceof TLRPC$messages_BotResults) && !((TLRPC$messages_BotResults) tLObject).results.isEmpty())) {
                    if (!this.withRecent && TextUtils.isEmpty(str2)) {
                        this.results.clear();
                        this.resultsMap.clear();
                        EmojiView.this.gifSearchField.progressDrawable.stopAnimation();
                    }
                    if (tLObject instanceof TLRPC$messages_BotResults) {
                        int size = this.results.size();
                        TLRPC$messages_BotResults tLRPC$messages_BotResults = (TLRPC$messages_BotResults) tLObject;
                        if (!EmojiView.this.gifCache.containsKey(str3)) {
                            EmojiView.this.gifCache.put(str3, tLRPC$messages_BotResults);
                        }
                        if (!z3 && tLRPC$messages_BotResults.cache_time != 0) {
                            MessagesStorage.getInstance(EmojiView.this.currentAccount).saveBotCache(str3, tLRPC$messages_BotResults);
                        }
                        this.nextSearchOffset = tLRPC$messages_BotResults.next_offset;
                        int i = 0;
                        for (int i2 = 0; i2 < tLRPC$messages_BotResults.results.size(); i2++) {
                            TLRPC$BotInlineResult tLRPC$BotInlineResult = tLRPC$messages_BotResults.results.get(i2);
                            if (!this.resultsMap.containsKey(tLRPC$BotInlineResult.id)) {
                                tLRPC$BotInlineResult.query_id = tLRPC$messages_BotResults.query_id;
                                this.results.add(tLRPC$BotInlineResult);
                                this.resultsMap.put(tLRPC$BotInlineResult.id, tLRPC$BotInlineResult);
                                i++;
                            }
                        }
                        if (size == this.results.size() || TextUtils.isEmpty(this.nextSearchOffset)) {
                            z4 = true;
                        }
                        this.searchEndReached = z4;
                        if (i != 0) {
                            if (!z2 || size != 0) {
                                updateItems();
                                if (!this.withRecent) {
                                    if (size != 0) {
                                        notifyItemChanged(size);
                                    }
                                    notifyItemRangeInserted(size + 1, i);
                                } else if (size != 0) {
                                    notifyItemChanged(this.recentItemsCount + 1 + size);
                                    notifyItemRangeInserted(this.recentItemsCount + 1 + size + 1, i);
                                } else {
                                    notifyItemRangeInserted(this.recentItemsCount + 1, i + 1);
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
                        if (z2 && !TextUtils.isEmpty(str) && TextUtils.isEmpty(str2)) {
                            EmojiView.this.scrollGifsToTop();
                            return;
                        }
                        return;
                    }
                    return;
                }
                search(str, str2, z, z2, false);
            }
        }
    }

    private class GifSearchPreloader {
        private final List<String> loadingKeys;

        private GifSearchPreloader() {
            this.loadingKeys = new ArrayList();
        }

        public boolean isLoading(String str) {
            return this.loadingKeys.contains(str);
        }

        public void preload(String str) {
            preload(str, "", true);
        }

        private void preload(String str, String str2, boolean z) {
            String str3 = "gif_search_" + str + "_" + str2;
            if (!z || !EmojiView.this.gifCache.containsKey(str3)) {
                EmojiView$GifSearchPreloader$$ExternalSyntheticLambda1 emojiView$GifSearchPreloader$$ExternalSyntheticLambda1 = new EmojiView$GifSearchPreloader$$ExternalSyntheticLambda1(this, str, str2, z, str3);
                if (z) {
                    this.loadingKeys.add(str3);
                    MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(str3, emojiView$GifSearchPreloader$$ExternalSyntheticLambda1);
                    return;
                }
                MessagesController instance = MessagesController.getInstance(EmojiView.this.currentAccount);
                TLObject userOrChat = instance.getUserOrChat(instance.gifSearchBot);
                if (userOrChat instanceof TLRPC$User) {
                    this.loadingKeys.add(str3);
                    TLRPC$TL_messages_getInlineBotResults tLRPC$TL_messages_getInlineBotResults = new TLRPC$TL_messages_getInlineBotResults();
                    if (str == null) {
                        str = "";
                    }
                    tLRPC$TL_messages_getInlineBotResults.query = str;
                    tLRPC$TL_messages_getInlineBotResults.bot = instance.getInputUser((TLRPC$User) userOrChat);
                    tLRPC$TL_messages_getInlineBotResults.offset = str2;
                    tLRPC$TL_messages_getInlineBotResults.peer = new TLRPC$TL_inputPeerEmpty();
                    ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_messages_getInlineBotResults, emojiView$GifSearchPreloader$$ExternalSyntheticLambda1, 2);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$preload$1(String str, String str2, boolean z, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new EmojiView$GifSearchPreloader$$ExternalSyntheticLambda0(this, str, str2, z, str3, tLObject));
        }

        /* access modifiers changed from: private */
        /* renamed from: processResponse */
        public void lambda$preload$0(String str, String str2, boolean z, String str3, TLObject tLObject) {
            this.loadingKeys.remove(str3);
            if (EmojiView.this.gifSearchAdapter.lastSearchIsEmoji && EmojiView.this.gifSearchAdapter.lastSearchImageString.equals(str)) {
                EmojiView.this.gifSearchAdapter.lambda$search$3(str, str2, false, true, z, str3, tLObject);
            } else if (z && (!(tLObject instanceof TLRPC$messages_BotResults) || ((TLRPC$messages_BotResults) tLObject).results.isEmpty())) {
                preload(str, str2, false);
            } else if ((tLObject instanceof TLRPC$messages_BotResults) && !EmojiView.this.gifCache.containsKey(str3)) {
                EmojiView.this.gifCache.put(str3, (TLRPC$messages_BotResults) tLObject);
            }
        }
    }

    private class GifLayoutManager extends ExtendedGridLayoutManager {
        private Size size = new Size();

        public GifLayoutManager(Context context) {
            super(context, 100, true);
            setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(EmojiView.this) {
                public int getSpanSize(int i) {
                    if (i == 0 || (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty())) {
                        return GifLayoutManager.this.getSpanCount();
                    }
                    return GifLayoutManager.this.getSpanSizeForItem(i - 1);
                }
            });
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v12, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: org.telegram.tgnet.TLRPC$Document} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v25, resolved type: java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>} */
        /* JADX WARNING: type inference failed for: r4v1, types: [java.util.List] */
        /* JADX WARNING: type inference failed for: r4v3 */
        /* JADX WARNING: type inference failed for: r4v5 */
        /* JADX WARNING: type inference failed for: r4v9, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARNING: type inference failed for: r4v10, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARNING: type inference failed for: r4v11, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARNING: type inference failed for: r4v19, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARNING: type inference failed for: r4v20, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARNING: type inference failed for: r4v21, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
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
                r2 = 0
                if (r0 != r1) goto L_0x006e
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifAdapter
                int r0 = r0.recentItemsCount
                if (r4 <= r0) goto L_0x0051
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifAdapter
                java.util.ArrayList r0 = r0.results
                org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$GifAdapter r1 = r1.gifAdapter
                int r1 = r1.recentItemsCount
                int r4 = r4 - r1
                int r4 = r4 + -1
                java.lang.Object r4 = r0.get(r4)
                org.telegram.tgnet.TLRPC$BotInlineResult r4 = (org.telegram.tgnet.TLRPC$BotInlineResult) r4
                org.telegram.tgnet.TLRPC$Document r0 = r4.document
                if (r0 == 0) goto L_0x0043
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r0.attributes
                goto L_0x0094
            L_0x0043:
                org.telegram.tgnet.TLRPC$WebDocument r1 = r4.content
                if (r1 == 0) goto L_0x004a
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r1.attributes
                goto L_0x0094
            L_0x004a:
                org.telegram.tgnet.TLRPC$WebDocument r4 = r4.thumb
                if (r4 == 0) goto L_0x00a4
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r4.attributes
                goto L_0x0094
            L_0x0051:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifAdapter
                int r0 = r0.recentItemsCount
                if (r4 != r0) goto L_0x005e
                return r2
            L_0x005e:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r0 = r0.recentGifs
                java.lang.Object r4 = r0.get(r4)
                r2 = r4
                org.telegram.tgnet.TLRPC$Document r2 = (org.telegram.tgnet.TLRPC$Document) r2
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r2.attributes
                goto L_0x00a8
            L_0x006e:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifSearchAdapter
                java.util.ArrayList r0 = r0.results
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x00a7
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifSearchAdapter
                java.util.ArrayList r0 = r0.results
                java.lang.Object r4 = r0.get(r4)
                org.telegram.tgnet.TLRPC$BotInlineResult r4 = (org.telegram.tgnet.TLRPC$BotInlineResult) r4
                org.telegram.tgnet.TLRPC$Document r0 = r4.document
                if (r0 == 0) goto L_0x0096
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r0.attributes
            L_0x0094:
                r2 = r4
                goto L_0x00a4
            L_0x0096:
                org.telegram.tgnet.TLRPC$WebDocument r1 = r4.content
                if (r1 == 0) goto L_0x009d
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r1.attributes
                goto L_0x0094
            L_0x009d:
                org.telegram.tgnet.TLRPC$WebDocument r4 = r4.thumb
                if (r4 == 0) goto L_0x00a4
                java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r4.attributes
                goto L_0x0094
            L_0x00a4:
                r4 = r2
                r2 = r0
                goto L_0x00a8
            L_0x00a7:
                r4 = r2
            L_0x00a8:
                org.telegram.ui.Components.Size r4 = r3.getSizeForItem(r2, r4)
                return r4
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

        public Size getSizeForItem(TLRPC$Document tLRPC$Document) {
            return getSizeForItem(tLRPC$Document, tLRPC$Document.attributes);
        }

        public Size getSizeForItem(TLRPC$Document tLRPC$Document, List<TLRPC$DocumentAttribute> list) {
            TLRPC$PhotoSize closestPhotoSizeWithSize;
            int i;
            int i2;
            Size size2 = this.size;
            size2.height = 100.0f;
            size2.width = 100.0f;
            if (!(tLRPC$Document == null || (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90)) == null || (i = closestPhotoSizeWithSize.w) == 0 || (i2 = closestPhotoSizeWithSize.h) == 0)) {
                Size size3 = this.size;
                size3.width = (float) i;
                size3.height = (float) i2;
            }
            if (list != null) {
                int i3 = 0;
                while (true) {
                    if (i3 >= list.size()) {
                        break;
                    }
                    TLRPC$DocumentAttribute tLRPC$DocumentAttribute = list.get(i3);
                    if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeImageSize) || (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo)) {
                        Size size4 = this.size;
                        size4.width = (float) tLRPC$DocumentAttribute.w;
                        size4.height = (float) tLRPC$DocumentAttribute.h;
                    } else {
                        i3++;
                    }
                }
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
            imageView2.setImageResource(R.drawable.gif_empty);
            imageView2.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
            addView(imageView2, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
            TextView textView2 = new TextView(getContext());
            this.textView = textView2;
            textView2.setText(LocaleController.getString("NoGIFsFound", R.string.NoGIFsFound));
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
        public void onMeasure(int i, int i2) {
            int i3;
            int measuredHeight = EmojiView.this.gifGridView.getMeasuredHeight();
            if (!this.loadingState) {
                i3 = (int) (((float) (((measuredHeight - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3)) * 1.7f);
            } else {
                i3 = measuredHeight - AndroidUtilities.dp(80.0f);
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(i3, NUM));
        }

        public void setLoadingState(boolean z) {
            if (this.loadingState != z) {
                this.loadingState = z;
                int i = 8;
                this.imageView.setVisibility(z ? 8 : 0);
                this.textView.setVisibility(z ? 8 : 0);
                RadialProgressView radialProgressView = this.progressView;
                if (z) {
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
        public ArrayList<ArrayList<TLRPC$Document>> emojiArrays = new ArrayList<>();
        /* access modifiers changed from: private */
        public int emojiSearchId;
        /* access modifiers changed from: private */
        public HashMap<ArrayList<TLRPC$Document>, String> emojiStickers = new HashMap<>();
        /* access modifiers changed from: private */
        public ArrayList<TLRPC$TL_messages_stickerSet> localPacks = new ArrayList<>();
        /* access modifiers changed from: private */
        public HashMap<TLRPC$TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<TLRPC$TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
        private SparseArray<String> positionToEmoji = new SparseArray<>();
        private SparseIntArray positionToRow = new SparseIntArray();
        /* access modifiers changed from: private */
        public SparseArray<TLRPC$StickerSetCovered> positionsToSets = new SparseArray<>();
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
                StickersSearchGridAdapter stickersSearchGridAdapter = StickersSearchGridAdapter.this;
                if (!stickersSearchGridAdapter.cleared) {
                    stickersSearchGridAdapter.cleared = true;
                    stickersSearchGridAdapter.emojiStickers.clear();
                    StickersSearchGridAdapter.this.emojiArrays.clear();
                    StickersSearchGridAdapter.this.localPacks.clear();
                    StickersSearchGridAdapter.this.serverPacks.clear();
                    StickersSearchGridAdapter.this.localPacksByShortName.clear();
                    StickersSearchGridAdapter.this.localPacksByName.clear();
                }
            }

            /* JADX WARNING: Code restructure failed: missing block: B:14:0x0079, code lost:
                if (r5.charAt(r9) <= 57343) goto L_0x0095;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:20:0x0093, code lost:
                if (r5.charAt(r9) != 9794) goto L_0x00b0;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r13 = this;
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.lang.String r0 = r0.searchQuery
                    boolean r0 = android.text.TextUtils.isEmpty(r0)
                    if (r0 == 0) goto L_0x000d
                    return
                L_0x000d:
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                    org.telegram.ui.Components.EmojiView$SearchField r0 = r0.stickersSearchField
                    org.telegram.ui.Components.CloseProgressDrawable2 r0 = r0.progressDrawable
                    r0.startAnimation()
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    r1 = 0
                    r0.cleared = r1
                    int r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.access$19504(r0)
                    java.util.ArrayList r2 = new java.util.ArrayList
                    r2.<init>(r1)
                    android.util.LongSparseArray r3 = new android.util.LongSparseArray
                    r3.<init>(r1)
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                    int r4 = r4.currentAccount
                    org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
                    java.util.HashMap r4 = r4.getAllStickers()
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.lang.String r5 = r5.searchQuery
                    int r5 = r5.length()
                    r6 = 14
                    r7 = 1
                    if (r5 > r6) goto L_0x0120
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.lang.String r5 = r5.searchQuery
                    int r6 = r5.length()
                    r8 = 0
                L_0x0057:
                    if (r8 >= r6) goto L_0x00d7
                    int r9 = r6 + -1
                    r10 = 2
                    if (r8 >= r9) goto L_0x00b0
                    char r9 = r5.charAt(r8)
                    r11 = 55356(0xd83c, float:7.757E-41)
                    if (r9 != r11) goto L_0x007b
                    int r9 = r8 + 1
                    char r11 = r5.charAt(r9)
                    r12 = 57339(0xdffb, float:8.0349E-41)
                    if (r11 < r12) goto L_0x007b
                    char r9 = r5.charAt(r9)
                    r11 = 57343(0xdfff, float:8.0355E-41)
                    if (r9 <= r11) goto L_0x0095
                L_0x007b:
                    char r9 = r5.charAt(r8)
                    r11 = 8205(0x200d, float:1.1498E-41)
                    if (r9 != r11) goto L_0x00b0
                    int r9 = r8 + 1
                    char r11 = r5.charAt(r9)
                    r12 = 9792(0x2640, float:1.3722E-41)
                    if (r11 == r12) goto L_0x0095
                    char r9 = r5.charAt(r9)
                    r11 = 9794(0x2642, float:1.3724E-41)
                    if (r9 != r11) goto L_0x00b0
                L_0x0095:
                    java.lang.CharSequence[] r9 = new java.lang.CharSequence[r10]
                    java.lang.CharSequence r10 = r5.subSequence(r1, r8)
                    r9[r1] = r10
                    int r10 = r8 + 2
                    int r11 = r5.length()
                    java.lang.CharSequence r5 = r5.subSequence(r10, r11)
                    r9[r7] = r5
                    java.lang.CharSequence r5 = android.text.TextUtils.concat(r9)
                    int r6 = r6 + -2
                    goto L_0x00d3
                L_0x00b0:
                    char r9 = r5.charAt(r8)
                    r11 = 65039(0xfe0f, float:9.1139E-41)
                    if (r9 != r11) goto L_0x00d5
                    java.lang.CharSequence[] r9 = new java.lang.CharSequence[r10]
                    java.lang.CharSequence r10 = r5.subSequence(r1, r8)
                    r9[r1] = r10
                    int r10 = r8 + 1
                    int r11 = r5.length()
                    java.lang.CharSequence r5 = r5.subSequence(r10, r11)
                    r9[r7] = r5
                    java.lang.CharSequence r5 = android.text.TextUtils.concat(r9)
                    int r6 = r6 + -1
                L_0x00d3:
                    int r8 = r8 + -1
                L_0x00d5:
                    int r8 = r8 + r7
                    goto L_0x0057
                L_0x00d7:
                    if (r4 == 0) goto L_0x00e4
                    java.lang.String r5 = r5.toString()
                    java.lang.Object r5 = r4.get(r5)
                    java.util.ArrayList r5 = (java.util.ArrayList) r5
                    goto L_0x00e5
                L_0x00e4:
                    r5 = 0
                L_0x00e5:
                    if (r5 == 0) goto L_0x0120
                    boolean r6 = r5.isEmpty()
                    if (r6 != 0) goto L_0x0120
                    r13.clear()
                    r2.addAll(r5)
                    int r6 = r5.size()
                    r8 = 0
                L_0x00f8:
                    if (r8 >= r6) goto L_0x0108
                    java.lang.Object r9 = r5.get(r8)
                    org.telegram.tgnet.TLRPC$Document r9 = (org.telegram.tgnet.TLRPC$Document) r9
                    long r10 = r9.id
                    r3.put(r10, r9)
                    int r8 = r8 + 1
                    goto L_0x00f8
                L_0x0108:
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.util.HashMap r5 = r5.emojiStickers
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.lang.String r6 = r6.searchQuery
                    r5.put(r2, r6)
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.util.ArrayList r5 = r5.emojiArrays
                    r5.add(r2)
                L_0x0120:
                    if (r4 == 0) goto L_0x017c
                    boolean r5 = r4.isEmpty()
                    if (r5 != 0) goto L_0x017c
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.lang.String r5 = r5.searchQuery
                    int r5 = r5.length()
                    if (r5 <= r7) goto L_0x017c
                    java.lang.String[] r5 = org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage()
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                    java.lang.String[] r6 = r6.lastSearchKeyboardLanguage
                    boolean r6 = java.util.Arrays.equals(r6, r5)
                    if (r6 != 0) goto L_0x0153
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                    int r6 = r6.currentAccount
                    org.telegram.messenger.MediaDataController r6 = org.telegram.messenger.MediaDataController.getInstance(r6)
                    r6.fetchNewEmojiKeywords(r5)
                L_0x0153:
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                    java.lang.String[] unused = r6.lastSearchKeyboardLanguage = r5
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r5 = org.telegram.ui.Components.EmojiView.this
                    int r5 = r5.currentAccount
                    org.telegram.messenger.MediaDataController r6 = org.telegram.messenger.MediaDataController.getInstance(r5)
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r5 = org.telegram.ui.Components.EmojiView.this
                    java.lang.String[] r7 = r5.lastSearchKeyboardLanguage
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.lang.String r8 = r5.searchQuery
                    r9 = 0
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$1 r10 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$1
                    r10.<init>(r0, r4)
                    r11 = 0
                    r6.getEmojiSuggestions(r7, r8, r9, r10, r11)
                L_0x017c:
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                    int r0 = r0.currentAccount
                    org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
                    java.util.ArrayList r0 = r0.getStickerSets(r1)
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                    int r4 = r4.currentAccount
                    org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
                    r4.filterPremiumStickers((java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet>) r0)
                    int r4 = r0.size()
                    r5 = 0
                L_0x019c:
                    r6 = 32
                    if (r5 >= r4) goto L_0x0218
                    java.lang.Object r7 = r0.get(r5)
                    org.telegram.tgnet.TLRPC$TL_messages_stickerSet r7 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r7
                    org.telegram.tgnet.TLRPC$StickerSet r8 = r7.set
                    java.lang.String r8 = r8.title
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r9 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.lang.String r9 = r9.searchQuery
                    int r8 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r8, r9)
                    if (r8 < 0) goto L_0x01de
                    if (r8 == 0) goto L_0x01c4
                    org.telegram.tgnet.TLRPC$StickerSet r9 = r7.set
                    java.lang.String r9 = r9.title
                    int r10 = r8 + -1
                    char r9 = r9.charAt(r10)
                    if (r9 != r6) goto L_0x0215
                L_0x01c4:
                    r13.clear()
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.util.ArrayList r6 = r6.localPacks
                    r6.add(r7)
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.util.HashMap r6 = r6.localPacksByName
                    java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
                    r6.put(r7, r8)
                    goto L_0x0215
                L_0x01de:
                    org.telegram.tgnet.TLRPC$StickerSet r8 = r7.set
                    java.lang.String r8 = r8.short_name
                    if (r8 == 0) goto L_0x0215
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r9 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.lang.String r9 = r9.searchQuery
                    int r8 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r8, r9)
                    if (r8 < 0) goto L_0x0215
                    if (r8 == 0) goto L_0x01fe
                    org.telegram.tgnet.TLRPC$StickerSet r9 = r7.set
                    java.lang.String r9 = r9.short_name
                    int r8 = r8 + -1
                    char r8 = r9.charAt(r8)
                    if (r8 != r6) goto L_0x0215
                L_0x01fe:
                    r13.clear()
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.util.ArrayList r6 = r6.localPacks
                    r6.add(r7)
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.util.HashMap r6 = r6.localPacksByShortName
                    java.lang.Boolean r8 = java.lang.Boolean.TRUE
                    r6.put(r7, r8)
                L_0x0215:
                    int r5 = r5 + 1
                    goto L_0x019c
                L_0x0218:
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                    int r0 = r0.currentAccount
                    org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
                    r4 = 3
                    java.util.ArrayList r0 = r0.getStickerSets(r4)
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                    int r4 = r4.currentAccount
                    org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
                    r4.filterPremiumStickers((java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet>) r0)
                    int r4 = r0.size()
                L_0x0238:
                    if (r1 >= r4) goto L_0x02b2
                    java.lang.Object r5 = r0.get(r1)
                    org.telegram.tgnet.TLRPC$TL_messages_stickerSet r5 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r5
                    org.telegram.tgnet.TLRPC$StickerSet r7 = r5.set
                    java.lang.String r7 = r7.title
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r8 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.lang.String r8 = r8.searchQuery
                    int r7 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r7, r8)
                    if (r7 < 0) goto L_0x0278
                    if (r7 == 0) goto L_0x025e
                    org.telegram.tgnet.TLRPC$StickerSet r8 = r5.set
                    java.lang.String r8 = r8.title
                    int r9 = r7 + -1
                    char r8 = r8.charAt(r9)
                    if (r8 != r6) goto L_0x02af
                L_0x025e:
                    r13.clear()
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r8 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.util.ArrayList r8 = r8.localPacks
                    r8.add(r5)
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r8 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.util.HashMap r8 = r8.localPacksByName
                    java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
                    r8.put(r5, r7)
                    goto L_0x02af
                L_0x0278:
                    org.telegram.tgnet.TLRPC$StickerSet r7 = r5.set
                    java.lang.String r7 = r7.short_name
                    if (r7 == 0) goto L_0x02af
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r8 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.lang.String r8 = r8.searchQuery
                    int r7 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r7, r8)
                    if (r7 < 0) goto L_0x02af
                    if (r7 == 0) goto L_0x0298
                    org.telegram.tgnet.TLRPC$StickerSet r8 = r5.set
                    java.lang.String r8 = r8.short_name
                    int r7 = r7 + -1
                    char r7 = r8.charAt(r7)
                    if (r7 != r6) goto L_0x02af
                L_0x0298:
                    r13.clear()
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r7 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.util.ArrayList r7 = r7.localPacks
                    r7.add(r5)
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r7 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.util.HashMap r7 = r7.localPacksByShortName
                    java.lang.Boolean r8 = java.lang.Boolean.TRUE
                    r7.put(r5, r8)
                L_0x02af:
                    int r1 = r1 + 1
                    goto L_0x0238
                L_0x02b2:
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.util.ArrayList r0 = r0.localPacks
                    boolean r0 = r0.isEmpty()
                    if (r0 == 0) goto L_0x02ca
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.util.HashMap r0 = r0.emojiStickers
                    boolean r0 = r0.isEmpty()
                    if (r0 != 0) goto L_0x02f3
                L_0x02ca:
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                    org.telegram.ui.Components.RecyclerListView r0 = r0.stickersGridView
                    androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r1 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r1 = r1.stickersSearchGridAdapter
                    if (r0 == r1) goto L_0x02f3
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                    org.telegram.ui.Components.RecyclerListView r0 = r0.stickersGridView
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r1 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r1 = r1.stickersSearchGridAdapter
                    r0.setAdapter(r1)
                L_0x02f3:
                    org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets r0 = new org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets
                    r0.<init>()
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r1 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.lang.String r1 = r1.searchQuery
                    r0.q = r1
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r1 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                    int r4 = r4.currentAccount
                    org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda3 r5 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda3
                    r5.<init>(r13, r0)
                    int r0 = r4.sendRequest(r0, r5)
                    int unused = r1.reqId = r0
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.lang.String r0 = r0.searchQuery
                    boolean r0 = org.telegram.messenger.Emoji.isValidEmoji(r0)
                    if (r0 == 0) goto L_0x0349
                    org.telegram.tgnet.TLRPC$TL_messages_getStickers r0 = new org.telegram.tgnet.TLRPC$TL_messages_getStickers
                    r0.<init>()
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r1 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    java.lang.String r1 = r1.searchQuery
                    r0.emoticon = r1
                    r4 = 0
                    r0.hash = r4
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r1 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                    int r4 = r4.currentAccount
                    org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda2 r5 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda2
                    r5.<init>(r13, r0, r2, r3)
                    int r0 = r4.sendRequest(r0, r5)
                    int unused = r1.reqId2 = r0
                L_0x0349:
                    org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                    r0.notifyDataSetChanged()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1.run():void");
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$1(TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                if (tLObject instanceof TLRPC$TL_messages_foundStickerSets) {
                    AndroidUtilities.runOnUIThread(new EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda1(this, tLRPC$TL_messages_searchStickerSets, tLObject));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0(TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, TLObject tLObject) {
                if (tLRPC$TL_messages_searchStickerSets.q.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    clear();
                    EmojiView.this.stickersSearchField.progressDrawable.stopAnimation();
                    int unused = StickersSearchGridAdapter.this.reqId = 0;
                    if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                        EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                    }
                    StickersSearchGridAdapter.this.serverPacks.addAll(((TLRPC$TL_messages_foundStickerSets) tLObject).sets);
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$3(TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, ArrayList arrayList, LongSparseArray longSparseArray, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda0(this, tLRPC$TL_messages_getStickers, tLObject, arrayList, longSparseArray));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$2(TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
                if (tLRPC$TL_messages_getStickers.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    int unused = StickersSearchGridAdapter.this.reqId2 = 0;
                    if (tLObject instanceof TLRPC$TL_messages_stickers) {
                        TLRPC$TL_messages_stickers tLRPC$TL_messages_stickers = (TLRPC$TL_messages_stickers) tLObject;
                        int size = arrayList.size();
                        int size2 = tLRPC$TL_messages_stickers.stickers.size();
                        for (int i = 0; i < size2; i++) {
                            TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickers.stickers.get(i);
                            if (longSparseArray.indexOfKey(tLRPC$Document.id) < 0) {
                                arrayList.add(tLRPC$Document);
                            }
                        }
                        if (size != arrayList.size()) {
                            StickersSearchGridAdapter.this.emojiStickers.put(arrayList, StickersSearchGridAdapter.this.searchQuery);
                            if (size == 0) {
                                StickersSearchGridAdapter.this.emojiArrays.add(arrayList);
                            }
                            StickersSearchGridAdapter.this.notifyDataSetChanged();
                        }
                    }
                }
            }
        };
        /* access modifiers changed from: private */
        public ArrayList<TLRPC$StickerSetCovered> serverPacks = new ArrayList<>();
        /* access modifiers changed from: private */
        public int totalItems;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        static /* synthetic */ int access$19504(StickersSearchGridAdapter stickersSearchGridAdapter) {
            int i = stickersSearchGridAdapter.emojiSearchId + 1;
            stickersSearchGridAdapter.emojiSearchId = i;
            return i;
        }

        public StickersSearchGridAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            int i = this.totalItems;
            if (i != 1) {
                return i + 1;
            }
            return 2;
        }

        public void search(String str) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (this.reqId2 != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId2, true);
                this.reqId2 = 0;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchQuery = null;
                this.localPacks.clear();
                this.emojiStickers.clear();
                this.serverPacks.clear();
                if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersGridAdapter) {
                    EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersGridAdapter);
                }
                notifyDataSetChanged();
            } else {
                this.searchQuery = str.toLowerCase();
            }
            AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
            AndroidUtilities.runOnUIThread(this.searchRunnable, 300);
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 4;
            }
            if (i == 1 && this.totalItems == 1) {
                return 5;
            }
            Object obj = this.cache.get(i);
            if (obj == null) {
                return 1;
            }
            if (obj instanceof TLRPC$Document) {
                return 0;
            }
            return obj instanceof TLRPC$StickerSetCovered ? 3 : 2;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$0(View view) {
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
            TLRPC$StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
            if (EmojiView.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0 && EmojiView.this.removingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                if (featuredStickerSetInfoCell.isInstalled()) {
                    EmojiView.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                    EmojiView.this.delegate.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
                    return;
                }
                featuredStickerSetInfoCell.setAddDrawProgress(true, true);
                EmojiView.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                EmojiView.this.delegate.onStickerSetAdd(featuredStickerSetInfoCell.getStickerSet());
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v2, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v3, resolved type: android.widget.FrameLayout} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v7, resolved type: android.view.View} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: android.widget.FrameLayout} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v12, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v13, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v14, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v15, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
        /* JADX WARNING: type inference failed for: r15v2 */
        /* JADX WARNING: type inference failed for: r14v4, types: [org.telegram.ui.Cells.EmptyCell] */
        /* JADX WARNING: type inference failed for: r14v5, types: [org.telegram.ui.Cells.StickerSetNameCell] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r14, int r15) {
            /*
                r13 = this;
                r14 = 1
                if (r15 == 0) goto L_0x00d3
                if (r15 == r14) goto L_0x00cb
                r0 = 2
                if (r15 == r0) goto L_0x00bc
                r0 = 3
                if (r15 == r0) goto L_0x00a1
                r0 = 4
                r1 = -1
                if (r15 == r0) goto L_0x008b
                r0 = 5
                if (r15 == r0) goto L_0x0015
                r14 = 0
                goto L_0x00db
            L_0x0015:
                org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$3 r15 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$3
                android.content.Context r0 = r13.context
                r15.<init>(r0)
                android.widget.ImageView r0 = new android.widget.ImageView
                android.content.Context r2 = r13.context
                r0.<init>(r2)
                android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
                r0.setScaleType(r2)
                int r2 = org.telegram.messenger.R.drawable.stickers_empty
                r0.setImageResource(r2)
                android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
                org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                java.lang.String r4 = "chat_emojiPanelEmptyText"
                int r3 = r3.getThemedColor(r4)
                android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
                r2.<init>(r3, r5)
                r0.setColorFilter(r2)
                r6 = -2
                r7 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r8 = 17
                r9 = 0
                r10 = 0
                r11 = 0
                r12 = 1114374144(0x426CLASSNAME, float:59.0)
                android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
                r15.addView(r0, r2)
                android.widget.TextView r0 = new android.widget.TextView
                android.content.Context r2 = r13.context
                r0.<init>(r2)
                int r2 = org.telegram.messenger.R.string.NoStickersFound
                java.lang.String r3 = "NoStickersFound"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                r2 = 1098907648(0x41800000, float:16.0)
                r0.setTextSize(r14, r2)
                org.telegram.ui.Components.EmojiView r14 = org.telegram.ui.Components.EmojiView.this
                int r14 = r14.getThemedColor(r4)
                r0.setTextColor(r14)
                r2 = -2
                r3 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                r4 = 17
                r5 = 0
                r6 = 0
                r7 = 0
                r8 = 1091567616(0x41100000, float:9.0)
                android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
                r15.addView(r0, r14)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r14 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -2
                r14.<init>((int) r1, (int) r0)
                r15.setLayoutParams(r14)
                goto L_0x00da
            L_0x008b:
                android.view.View r14 = new android.view.View
                android.content.Context r15 = r13.context
                r14.<init>(r15)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r15 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                int r0 = r0.searchFieldHeight
                r15.<init>((int) r1, (int) r0)
                r14.setLayoutParams(r15)
                goto L_0x00db
            L_0x00a1:
                org.telegram.ui.Cells.FeaturedStickerSetInfoCell r14 = new org.telegram.ui.Cells.FeaturedStickerSetInfoCell
                android.content.Context r3 = r13.context
                r4 = 17
                r5 = 0
                r6 = 1
                org.telegram.ui.Components.EmojiView r15 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r15.resourcesProvider
                r2 = r14
                r2.<init>(r3, r4, r5, r6, r7)
                org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$$ExternalSyntheticLambda0 r15 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$$ExternalSyntheticLambda0
                r15.<init>(r13)
                r14.setAddOnClickListener(r15)
                goto L_0x00db
            L_0x00bc:
                org.telegram.ui.Cells.StickerSetNameCell r14 = new org.telegram.ui.Cells.StickerSetNameCell
                android.content.Context r15 = r13.context
                r0 = 0
                org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r1.resourcesProvider
                r14.<init>(r15, r0, r1)
                goto L_0x00db
            L_0x00cb:
                org.telegram.ui.Cells.EmptyCell r14 = new org.telegram.ui.Cells.EmptyCell
                android.content.Context r15 = r13.context
                r14.<init>(r15)
                goto L_0x00db
            L_0x00d3:
                org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$2 r15 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$2
                android.content.Context r0 = r13.context
                r15.<init>(r13, r0, r14)
            L_0x00da:
                r14 = r15
            L_0x00db:
                org.telegram.ui.Components.RecyclerListView$Holder r15 = new org.telegram.ui.Components.RecyclerListView$Holder
                r15.<init>(r14)
                return r15
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v11, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v16, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v11, resolved type: java.lang.Integer} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: boolean} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
            /*
                r9 = this;
                int r0 = r10.getItemViewType()
                r1 = 1
                r2 = 0
                if (r0 == 0) goto L_0x01af
                r3 = 0
                if (r0 == r1) goto L_0x0128
                r4 = 2
                if (r0 == r4) goto L_0x00c4
                r3 = 3
                if (r0 == r3) goto L_0x0013
                goto L_0x01ee
            L_0x0013:
                android.util.SparseArray<java.lang.Object> r0 = r9.cache
                java.lang.Object r11 = r0.get(r11)
                r4 = r11
                org.telegram.tgnet.TLRPC$StickerSetCovered r4 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r4
                android.view.View r10 = r10.itemView
                r3 = r10
                org.telegram.ui.Cells.FeaturedStickerSetInfoCell r3 = (org.telegram.ui.Cells.FeaturedStickerSetInfoCell) r3
                org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                android.util.LongSparseArray r10 = r10.installingStickerSets
                org.telegram.tgnet.TLRPC$StickerSet r11 = r4.set
                long r5 = r11.id
                int r10 = r10.indexOfKey(r5)
                if (r10 < 0) goto L_0x0033
                r10 = 1
                goto L_0x0034
            L_0x0033:
                r10 = 0
            L_0x0034:
                org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                android.util.LongSparseArray r11 = r11.removingStickerSets
                org.telegram.tgnet.TLRPC$StickerSet r0 = r4.set
                long r5 = r0.id
                int r11 = r11.indexOfKey(r5)
                if (r11 < 0) goto L_0x0045
                goto L_0x0046
            L_0x0045:
                r1 = 0
            L_0x0046:
                if (r10 != 0) goto L_0x004a
                if (r1 == 0) goto L_0x0076
            L_0x004a:
                if (r10 == 0) goto L_0x0061
                boolean r11 = r3.isInstalled()
                if (r11 == 0) goto L_0x0061
                org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                android.util.LongSparseArray r10 = r10.installingStickerSets
                org.telegram.tgnet.TLRPC$StickerSet r11 = r4.set
                long r0 = r11.id
                r10.remove(r0)
                r10 = 0
                goto L_0x0076
            L_0x0061:
                if (r1 == 0) goto L_0x0076
                boolean r11 = r3.isInstalled()
                if (r11 != 0) goto L_0x0076
                org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                android.util.LongSparseArray r11 = r11.removingStickerSets
                org.telegram.tgnet.TLRPC$StickerSet r0 = r4.set
                long r0 = r0.id
                r11.remove(r0)
            L_0x0076:
                r3.setAddDrawProgress(r10, r2)
                java.lang.String r10 = r9.searchQuery
                boolean r10 = android.text.TextUtils.isEmpty(r10)
                if (r10 == 0) goto L_0x0084
                r10 = -1
                r7 = -1
                goto L_0x008f
            L_0x0084:
                org.telegram.tgnet.TLRPC$StickerSet r10 = r4.set
                java.lang.String r10 = r10.title
                java.lang.String r11 = r9.searchQuery
                int r10 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r10, r11)
                r7 = r10
            L_0x008f:
                if (r7 < 0) goto L_0x009e
                r5 = 0
                r6 = 0
                java.lang.String r10 = r9.searchQuery
                int r8 = r10.length()
                r3.setStickerSet(r4, r5, r6, r7, r8)
                goto L_0x01ee
            L_0x009e:
                r3.setStickerSet(r4, r2)
                java.lang.String r10 = r9.searchQuery
                boolean r10 = android.text.TextUtils.isEmpty(r10)
                if (r10 != 0) goto L_0x01ee
                org.telegram.tgnet.TLRPC$StickerSet r10 = r4.set
                java.lang.String r10 = r10.short_name
                java.lang.String r11 = r9.searchQuery
                int r10 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r10, r11)
                if (r10 != 0) goto L_0x01ee
                org.telegram.tgnet.TLRPC$StickerSet r10 = r4.set
                java.lang.String r10 = r10.short_name
                java.lang.String r11 = r9.searchQuery
                int r11 = r11.length()
                r3.setUrl(r10, r11)
                goto L_0x01ee
            L_0x00c4:
                android.view.View r10 = r10.itemView
                org.telegram.ui.Cells.StickerSetNameCell r10 = (org.telegram.ui.Cells.StickerSetNameCell) r10
                android.util.SparseArray<java.lang.Object> r0 = r9.cache
                java.lang.Object r11 = r0.get(r11)
                boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
                if (r0 == 0) goto L_0x01ee
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r11 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r11
                java.lang.String r0 = r9.searchQuery
                boolean r0 = android.text.TextUtils.isEmpty(r0)
                if (r0 != 0) goto L_0x00fc
                java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_stickerSet, java.lang.Boolean> r0 = r9.localPacksByShortName
                boolean r0 = r0.containsKey(r11)
                if (r0 == 0) goto L_0x00fc
                org.telegram.tgnet.TLRPC$StickerSet r0 = r11.set
                if (r0 == 0) goto L_0x00ed
                java.lang.String r0 = r0.title
                r10.setText(r0, r2)
            L_0x00ed:
                org.telegram.tgnet.TLRPC$StickerSet r11 = r11.set
                java.lang.String r11 = r11.short_name
                java.lang.String r0 = r9.searchQuery
                int r0 = r0.length()
                r10.setUrl(r11, r0)
                goto L_0x01ee
            L_0x00fc:
                java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_stickerSet, java.lang.Integer> r0 = r9.localPacksByName
                java.lang.Object r0 = r0.get(r11)
                java.lang.Integer r0 = (java.lang.Integer) r0
                org.telegram.tgnet.TLRPC$StickerSet r11 = r11.set
                if (r11 == 0) goto L_0x0123
                if (r0 == 0) goto L_0x0123
                java.lang.String r11 = r11.title
                int r0 = r0.intValue()
                java.lang.String r1 = r9.searchQuery
                boolean r1 = android.text.TextUtils.isEmpty(r1)
                if (r1 != 0) goto L_0x011f
                java.lang.String r1 = r9.searchQuery
                int r1 = r1.length()
                goto L_0x0120
            L_0x011f:
                r1 = 0
            L_0x0120:
                r10.setText(r11, r2, r0, r1)
            L_0x0123:
                r10.setUrl(r3, r2)
                goto L_0x01ee
            L_0x0128:
                android.view.View r10 = r10.itemView
                org.telegram.ui.Cells.EmptyCell r10 = (org.telegram.ui.Cells.EmptyCell) r10
                int r0 = r9.totalItems
                r2 = 1118044160(0x42a40000, float:82.0)
                if (r11 != r0) goto L_0x01a7
                android.util.SparseIntArray r0 = r9.positionToRow
                int r11 = r11 - r1
                r4 = -2147483648(0xfffffffvar_, float:-0.0)
                int r11 = r0.get(r11, r4)
                if (r11 != r4) goto L_0x0142
                r10.setHeight(r1)
                goto L_0x01ee
            L_0x0142:
                android.util.SparseArray<java.lang.Object> r0 = r9.rowStartPack
                java.lang.Object r11 = r0.get(r11)
                boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
                if (r0 == 0) goto L_0x0159
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r11 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r11
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r11 = r11.documents
                int r11 = r11.size()
                java.lang.Integer r3 = java.lang.Integer.valueOf(r11)
                goto L_0x0160
            L_0x0159:
                boolean r0 = r11 instanceof java.lang.Integer
                if (r0 == 0) goto L_0x0160
                r3 = r11
                java.lang.Integer r3 = (java.lang.Integer) r3
            L_0x0160:
                if (r3 != 0) goto L_0x0167
                r10.setHeight(r1)
                goto L_0x01ee
            L_0x0167:
                int r11 = r3.intValue()
                if (r11 != 0) goto L_0x0178
                r11 = 1090519040(0x41000000, float:8.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r10.setHeight(r11)
                goto L_0x01ee
            L_0x0178:
                org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                androidx.viewpager.widget.ViewPager r11 = r11.pager
                int r11 = r11.getHeight()
                int r0 = r3.intValue()
                float r0 = (float) r0
                org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r3 = r3.stickersGridAdapter
                int r3 = r3.stickersPerRow
                float r3 = (float) r3
                float r0 = r0 / r3
                double r3 = (double) r0
                double r3 = java.lang.Math.ceil(r3)
                int r0 = (int) r3
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r0 = r0 * r2
                int r11 = r11 - r0
                if (r11 <= 0) goto L_0x01a3
                r1 = r11
            L_0x01a3:
                r10.setHeight(r1)
                goto L_0x01ee
            L_0x01a7:
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r10.setHeight(r11)
                goto L_0x01ee
            L_0x01af:
                android.util.SparseArray<java.lang.Object> r0 = r9.cache
                java.lang.Object r0 = r0.get(r11)
                org.telegram.tgnet.TLRPC$Document r0 = (org.telegram.tgnet.TLRPC$Document) r0
                android.view.View r10 = r10.itemView
                org.telegram.ui.Cells.StickerEmojiCell r10 = (org.telegram.ui.Cells.StickerEmojiCell) r10
                r5 = 0
                android.util.SparseArray<java.lang.Object> r3 = r9.cacheParent
                java.lang.Object r6 = r3.get(r11)
                android.util.SparseArray<java.lang.String> r3 = r9.positionToEmoji
                java.lang.Object r11 = r3.get(r11)
                r7 = r11
                java.lang.String r7 = (java.lang.String) r7
                r8 = 0
                r3 = r10
                r4 = r0
                r3.setSticker(r4, r5, r6, r7, r8)
                org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r11 = r11.recentStickers
                boolean r11 = r11.contains(r0)
                if (r11 != 0) goto L_0x01eb
                org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r11 = r11.favouriteStickers
                boolean r11 = r11.contains(r0)
                if (r11 == 0) goto L_0x01ea
                goto L_0x01eb
            L_0x01ea:
                r1 = 0
            L_0x01eb:
                r10.setRecent(r1)
            L_0x01ee:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        /* JADX WARNING: type inference failed for: r8v19, types: [org.telegram.tgnet.TLRPC$messages_StickerSet, org.telegram.tgnet.TLRPC$TL_messages_stickerSet] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void notifyDataSetChanged() {
            /*
                r19 = this;
                r0 = r19
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
                java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r2 = r0.serverPacks
                int r2 = r2.size()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r3 = r0.localPacks
                int r3 = r3.size()
                java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r4 = r0.emojiArrays
                boolean r4 = r4.isEmpty()
                r4 = r4 ^ 1
                r5 = -1
                r6 = -1
                r7 = 0
            L_0x0035:
                int r8 = r2 + r3
                int r8 = r8 + r4
                if (r6 >= r8) goto L_0x01df
                if (r6 != r5) goto L_0x004f
                android.util.SparseArray<java.lang.Object> r8 = r0.cache
                int r9 = r0.totalItems
                int r10 = r9 + 1
                r0.totalItems = r10
                java.lang.String r10 = "search"
                r8.put(r9, r10)
                int r7 = r7 + 1
                r16 = r2
                goto L_0x01d7
            L_0x004f:
                if (r6 >= r3) goto L_0x005f
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r8 = r0.localPacks
                java.lang.Object r8 = r8.get(r6)
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r8
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r8.documents
                r16 = r2
                goto L_0x0137
            L_0x005f:
                int r8 = r6 - r3
                if (r8 >= r4) goto L_0x0129
                java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r8 = r0.emojiArrays
                int r8 = r8.size()
                java.lang.String r9 = ""
                r10 = 0
                r11 = 0
            L_0x006d:
                if (r10 >= r8) goto L_0x00ef
                java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r12 = r0.emojiArrays
                java.lang.Object r12 = r12.get(r10)
                java.util.ArrayList r12 = (java.util.ArrayList) r12
                java.util.HashMap<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>, java.lang.String> r13 = r0.emojiStickers
                java.lang.Object r13 = r13.get(r12)
                java.lang.String r13 = (java.lang.String) r13
                if (r13 == 0) goto L_0x0090
                boolean r14 = r9.equals(r13)
                if (r14 != 0) goto L_0x0090
                android.util.SparseArray<java.lang.String> r9 = r0.positionToEmoji
                int r14 = r0.totalItems
                int r14 = r14 + r11
                r9.put(r14, r13)
                r9 = r13
            L_0x0090:
                int r13 = r12.size()
                r14 = 0
            L_0x0095:
                if (r14 >= r13) goto L_0x00e3
                int r15 = r0.totalItems
                int r15 = r15 + r11
                org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r1 = r1.stickersGridAdapter
                int r1 = r1.stickersPerRow
                int r1 = r11 / r1
                int r1 = r1 + r7
                java.lang.Object r16 = r12.get(r14)
                r5 = r16
                org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
                r16 = r2
                android.util.SparseArray<java.lang.Object> r2 = r0.cache
                r2.put(r15, r5)
                org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                int r2 = r2.currentAccount
                org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r2)
                r17 = r8
                r18 = r9
                long r8 = org.telegram.messenger.MediaDataController.getStickerSetId(r5)
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r2.getStickerSetById(r8)
                if (r2 == 0) goto L_0x00d1
                android.util.SparseArray<java.lang.Object> r5 = r0.cacheParent
                r5.put(r15, r2)
            L_0x00d1:
                android.util.SparseIntArray r2 = r0.positionToRow
                r2.put(r15, r1)
                int r11 = r11 + 1
                int r14 = r14 + 1
                r2 = r16
                r8 = r17
                r9 = r18
                r1 = 0
                r5 = -1
                goto L_0x0095
            L_0x00e3:
                r16 = r2
                r17 = r8
                r18 = r9
                int r10 = r10 + 1
                r1 = 0
                r5 = -1
                goto L_0x006d
            L_0x00ef:
                r16 = r2
                float r1 = (float) r11
                org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r2 = r2.stickersGridAdapter
                int r2 = r2.stickersPerRow
                float r2 = (float) r2
                float r1 = r1 / r2
                double r1 = (double) r1
                double r1 = java.lang.Math.ceil(r1)
                int r1 = (int) r1
                r2 = 0
            L_0x0105:
                if (r2 >= r1) goto L_0x0115
                android.util.SparseArray<java.lang.Object> r5 = r0.rowStartPack
                int r8 = r7 + r2
                java.lang.Integer r9 = java.lang.Integer.valueOf(r11)
                r5.put(r8, r9)
                int r2 = r2 + 1
                goto L_0x0105
            L_0x0115:
                int r2 = r0.totalItems
                org.telegram.ui.Components.EmojiView r5 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r5 = r5.stickersGridAdapter
                int r5 = r5.stickersPerRow
                int r5 = r5 * r1
                int r2 = r2 + r5
                r0.totalItems = r2
                int r7 = r7 + r1
                goto L_0x01d7
            L_0x0129:
                r16 = r2
                int r8 = r8 - r4
                java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r1 = r0.serverPacks
                java.lang.Object r1 = r1.get(r8)
                r8 = r1
                org.telegram.tgnet.TLRPC$StickerSetCovered r8 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r8
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r8.covers
            L_0x0137:
                boolean r1 = r9.isEmpty()
                if (r1 == 0) goto L_0x013f
                goto L_0x01d7
            L_0x013f:
                int r1 = r9.size()
                float r1 = (float) r1
                org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r2 = r2.stickersGridAdapter
                int r2 = r2.stickersPerRow
                float r2 = (float) r2
                float r1 = r1 / r2
                double r1 = (double) r1
                double r1 = java.lang.Math.ceil(r1)
                int r1 = (int) r1
                android.util.SparseArray<java.lang.Object> r2 = r0.cache
                int r5 = r0.totalItems
                r2.put(r5, r8)
                if (r6 < r3) goto L_0x016d
                boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
                if (r2 == 0) goto L_0x016d
                android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r2 = r0.positionsToSets
                int r5 = r0.totalItems
                r10 = r8
                org.telegram.tgnet.TLRPC$StickerSetCovered r10 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r10
                r2.put(r5, r10)
            L_0x016d:
                android.util.SparseIntArray r2 = r0.positionToRow
                int r5 = r0.totalItems
                r2.put(r5, r7)
                int r2 = r9.size()
                r5 = 0
            L_0x0179:
                if (r5 >= r2) goto L_0x01b4
                int r10 = r5 + 1
                int r11 = r0.totalItems
                int r11 = r11 + r10
                int r12 = r7 + 1
                org.telegram.ui.Components.EmojiView r13 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r13 = r13.stickersGridAdapter
                int r13 = r13.stickersPerRow
                int r13 = r5 / r13
                int r12 = r12 + r13
                java.lang.Object r5 = r9.get(r5)
                org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
                android.util.SparseArray<java.lang.Object> r13 = r0.cache
                r13.put(r11, r5)
                android.util.SparseArray<java.lang.Object> r5 = r0.cacheParent
                r5.put(r11, r8)
                android.util.SparseIntArray r5 = r0.positionToRow
                r5.put(r11, r12)
                if (r6 < r3) goto L_0x01b2
                boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
                if (r5 == 0) goto L_0x01b2
                android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r5 = r0.positionsToSets
                r12 = r8
                org.telegram.tgnet.TLRPC$StickerSetCovered r12 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r12
                r5.put(r11, r12)
            L_0x01b2:
                r5 = r10
                goto L_0x0179
            L_0x01b4:
                int r2 = r1 + 1
                r5 = 0
            L_0x01b7:
                if (r5 >= r2) goto L_0x01c3
                android.util.SparseArray<java.lang.Object> r9 = r0.rowStartPack
                int r10 = r7 + r5
                r9.put(r10, r8)
                int r5 = r5 + 1
                goto L_0x01b7
            L_0x01c3:
                int r5 = r0.totalItems
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                org.telegram.ui.Components.EmojiView$StickersGridAdapter r8 = r8.stickersGridAdapter
                int r8 = r8.stickersPerRow
                int r1 = r1 * r8
                int r1 = r1 + 1
                int r5 = r5 + r1
                r0.totalItems = r5
                int r7 = r7 + r2
            L_0x01d7:
                int r6 = r6 + 1
                r2 = r16
                r1 = 0
                r5 = -1
                goto L_0x0035
            L_0x01df:
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

    public void setShowing(boolean z) {
        this.showing = z;
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

        public ChooseStickerActionTracker(int i, long j, int i2) {
            this.currentAccount = i;
            this.dialogId = j;
            this.threadId = i2;
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

    private class Tab {
        int type;
        View view;

        private Tab(EmojiView emojiView) {
        }
    }
}
