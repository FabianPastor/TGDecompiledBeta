package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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
import org.telegram.tgnet.TLRPC$TL_messages_reorderStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickers;
import org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebDocument;
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
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.ListView.RecyclerListViewWithOverlayDraw;
import org.telegram.ui.Components.PagerSlidingTabStrip;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTabStrip;
import org.telegram.ui.Components.TrendingStickersLayout;
import org.telegram.ui.ContentPreviewViewer;
/* loaded from: classes3.dex */
public class EmojiView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final ViewTreeObserver.OnScrollChangedListener NOP;
    private static final Field superListenerField;
    private ArrayList<Tab> allTabs;
    private boolean allowAnimatedEmoji;
    private View animateExpandFromButton;
    private int animateExpandFromPosition;
    private long animateExpandStartTime;
    private int animateExpandToPosition;
    private LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables;
    private ImageView backspaceButton;
    private AnimatorSet backspaceButtonAnimation;
    private boolean backspaceOnce;
    private boolean backspacePressed;
    private FrameLayout bottomTabContainer;
    private AnimatorSet bottomTabContainerAnimation;
    private View bottomTabContainerBackground;
    private FrameLayout bulletinContainer;
    private Runnable checkExpandStickerTabsRunnable;
    private ChooseStickerActionTracker chooseStickerActionTracker;
    private ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate;
    public int currentAccount;
    private int currentBackgroundType;
    private long currentChatId;
    private int currentPage;
    private ArrayList<Tab> currentTabs;
    private EmojiViewDelegate delegate;
    private Paint dotPaint;
    private DragListener dragListener;
    private EmojiGridAdapter emojiAdapter;
    private FrameLayout emojiContainer;
    private EmojiGridView emojiGridView;
    private float emojiLastX;
    private float emojiLastY;
    private GridLayoutManager emojiLayoutManager;
    private Drawable emojiLockDrawable;
    private Paint emojiLockPaint;
    private boolean emojiPackAlertOpened;
    EmojiPagesAdapter emojiPagerAdapter;
    private RecyclerAnimationScrollHelper emojiScrollHelper;
    private EmojiSearchAdapter emojiSearchAdapter;
    private SearchField emojiSearchField;
    private int emojiSize;
    private boolean emojiSmoothScrolling;
    private AnimatorSet emojiTabShadowAnimator;
    private EmojiTabsStrip emojiTabs;
    private View emojiTabsShadow;
    private String[] emojiTitles;
    private ImageViewEmoji emojiTouchedView;
    private float emojiTouchedX;
    private float emojiTouchedY;
    private ArrayList<EmojiPack> emojipacksProcessed;
    private boolean expandStickersByDragg;
    private ArrayList<Long> expandedEmojiSets;
    private int favTabNum;
    private ArrayList<TLRPC$Document> favouriteStickers;
    private ArrayList<TLRPC$StickerSetCovered> featuredEmojiSets;
    private ArrayList<TLRPC$StickerSetCovered> featuredStickerSets;
    private boolean firstEmojiAttach;
    private boolean firstGifAttach;
    private boolean firstStickersAttach;
    private boolean firstTabUpdate;
    private ImageView floatingButton;
    private boolean forseMultiwindowLayout;
    private BaseFragment fragment;
    ArrayList<TLRPC$TL_messages_stickerSet> frozenStickerSets;
    private GifAdapter gifAdapter;
    private final Map<String, TLRPC$messages_BotResults> gifCache;
    private FrameLayout gifContainer;
    private int gifFirstEmojiTabNum;
    private RecyclerListView gifGridView;
    private Drawable[] gifIcons;
    private GifLayoutManager gifLayoutManager;
    private RecyclerListView.OnItemClickListener gifOnItemClickListener;
    private int gifRecentTabNum;
    private GifAdapter gifSearchAdapter;
    private SearchField gifSearchField;
    private GifSearchPreloader gifSearchPreloader;
    private ScrollSlidingTabStrip gifTabs;
    private int gifTrendingTabNum;
    private int groupStickerPackNum;
    private int groupStickerPackPosition;
    private TLRPC$TL_messages_stickerSet groupStickerSet;
    private boolean groupStickersHidden;
    private boolean hasChatStickers;
    private boolean ignoreStickersScroll;
    private TLRPC$ChatFull info;
    public ArrayList<Long> installedEmojiSets;
    private LongSparseArray<TLRPC$StickerSetCovered> installingStickerSets;
    private boolean isLayout;
    private float lastBottomScrollDy;
    private int lastNotifyHeight;
    private int lastNotifyHeight2;
    private int lastNotifyWidth;
    private ArrayList<String> lastRecentArray;
    private int lastRecentCount;
    private String[] lastSearchKeyboardLanguage;
    private float lastStickersX;
    private int[] location;
    private TextView mediaBanTooltip;
    private boolean needEmojiSearch;
    private Object outlineProvider;
    private ViewPager pager;
    private EmojiColorPickerView pickerView;
    private EmojiPopupWindow pickerViewPopup;
    private int popupHeight;
    private int popupWidth;
    private boolean premiumBulletin;
    private ArrayList<TLRPC$Document> premiumStickers;
    private int premiumTabNum;
    private TLRPC$StickerSetCovered[] primaryInstallingStickerSets;
    private ArrayList<TLRPC$Document> recentGifs;
    private ArrayList<TLRPC$Document> recentStickers;
    private int recentTabNum;
    android.graphics.Rect rect;
    private LongSparseArray<TLRPC$StickerSetCovered> removingStickerSets;
    private final Theme.ResourcesProvider resourcesProvider;
    private AnimatorSet searchAnimation;
    private ImageView searchButton;
    private int searchFieldHeight;
    private Drawable searchIconDotDrawable;
    private Drawable searchIconDrawable;
    private View shadowLine;
    private boolean showing;
    private long shownBottomTabAfterClick;
    private Drawable[] stickerIcons;
    private ArrayList<TLRPC$TL_messages_stickerSet> stickerSets;
    private ImageView stickerSettingsButton;
    private AnimatorSet stickersButtonAnimation;
    private FrameLayout stickersContainer;
    private boolean stickersContainerAttached;
    private StickersGridAdapter stickersGridAdapter;
    private RecyclerListView stickersGridView;
    private GridLayoutManager stickersLayoutManager;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    private RecyclerAnimationScrollHelper stickersScrollHelper;
    private SearchField stickersSearchField;
    private StickersSearchGridAdapter stickersSearchGridAdapter;
    private ScrollSlidingTabStrip stickersTab;
    private FrameLayout stickersTabContainer;
    private int stickersTabOffset;
    private Drawable[] tabIcons;
    private final int[] tabsMinusDy;
    private ObjectAnimator[] tabsYAnimators;
    private HashMap<Long, Utilities.Callback<TLRPC$TL_messages_stickerSet>> toInstall;
    private TrendingAdapter trendingAdapter;
    private TrendingAdapter trendingEmojiAdapter;
    private int trendingTabNum;
    private PagerSlidingTabStrip typeTabs;

    /* loaded from: classes3.dex */
    public static class CustomEmoji {
        public long documentId;
        public TLRPC$TL_messages_stickerSet stickerSet;
    }

    /* loaded from: classes3.dex */
    public interface DragListener {
        void onDrag(int i);

        void onDragCancel();

        void onDragEnd(float f);

        void onDragStart();
    }

    /* loaded from: classes3.dex */
    public static class EmojiPack {
        public ArrayList<TLRPC$Document> documents;
        public boolean expanded;
        public boolean featured;
        public boolean free;
        public boolean installed;
        public TLRPC$StickerSet set;
    }

    /* loaded from: classes3.dex */
    public interface EmojiViewDelegate {

        /* renamed from: org.telegram.ui.Components.EmojiView$EmojiViewDelegate$-CC */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static boolean $default$canSchedule(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static long $default$getDialogId(EmojiViewDelegate emojiViewDelegate) {
                return 0L;
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

            public static void $default$onEmojiSettingsClick(EmojiViewDelegate emojiViewDelegate, ArrayList arrayList) {
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

        void onCustomEmojiSelected(long j, TLRPC$Document tLRPC$Document, String str, boolean z);

        void onEmojiSelected(String str);

        void onEmojiSettingsClick(ArrayList<TLRPC$TL_messages_stickerSet> arrayList);

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

    public static /* synthetic */ void lambda$static$0() {
    }

    public static /* synthetic */ void access$4400(EmojiView emojiView) {
        emojiView.openPremiumAnimatedEmojiFeature();
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
            viewPager.setAdapter(null);
            this.pager.setAdapter(this.emojiPagerAdapter);
            PagerSlidingTabStrip pagerSlidingTabStrip2 = this.typeTabs;
            if (pagerSlidingTabStrip2 == null) {
                return;
            }
            pagerSlidingTabStrip2.setViewPager(this.pager);
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
        NOP = EmojiView$$ExternalSyntheticLambda4.INSTANCE;
    }

    @Override // android.view.View
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

    /* loaded from: classes3.dex */
    public class SearchField extends FrameLayout {
        private View backgroundView;
        private ImageView clearSearchImageView;
        private CloseProgressDrawable2 progressDrawable;
        private View searchBackground;
        private EditTextBoldCursor searchEditText;
        private ImageView searchIconImageView;
        private AnimatorSet shadowAnimator;
        private View shadowView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SearchField(Context context, int i) {
            super(context);
            EmojiView.this = r13;
            View view = new View(context);
            this.shadowView = view;
            view.setAlpha(0.0f);
            this.shadowView.setTag(1);
            this.shadowView.setBackgroundColor(r13.getThemedColor("chat_emojiPanelShadowLine"));
            addView(this.shadowView, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83));
            View view2 = new View(context);
            this.backgroundView = view2;
            view2.setBackgroundColor(r13.getThemedColor("chat_emojiPanelBackground"));
            addView(this.backgroundView, new FrameLayout.LayoutParams(-1, r13.searchFieldHeight));
            View view3 = new View(context);
            this.searchBackground = view3;
            view3.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), r13.getThemedColor("chat_emojiSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 14.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(R.drawable.smiles_inputsearch);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(r13.getThemedColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 14.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2(r13) { // from class: org.telegram.ui.Components.EmojiView.SearchField.1
                {
                    SearchField.this = this;
                }

                @Override // org.telegram.ui.Components.CloseProgressDrawable2
                protected int getCurrentColor() {
                    return EmojiView.this.getThemedColor("chat_emojiSearchIcon");
                }
            };
            this.progressDrawable = closeProgressDrawable2;
            imageView3.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 14.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$SearchField$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view4) {
                    EmojiView.SearchField.this.lambda$new$0(view4);
                }
            });
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context, r13, i) { // from class: org.telegram.ui.Components.EmojiView.SearchField.2
                final /* synthetic */ int val$type;

                {
                    SearchField.this = this;
                    this.val$type = i;
                }

                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (!SearchField.this.searchEditText.isEnabled()) {
                        return super.onTouchEvent(motionEvent);
                    }
                    if (motionEvent.getAction() == 0) {
                        if (!EmojiView.this.delegate.isSearchOpened()) {
                            SearchField searchField = SearchField.this;
                            EmojiView.this.openSearch(searchField);
                        }
                        EmojiViewDelegate emojiViewDelegate = EmojiView.this.delegate;
                        int i2 = 1;
                        if (this.val$type == 1) {
                            i2 = 2;
                        }
                        emojiViewDelegate.onSearchOpenClose(i2);
                        SearchField.this.searchEditText.requestFocus();
                        AndroidUtilities.showKeyboard(SearchField.this.searchEditText);
                    }
                    return super.onTouchEvent(motionEvent);
                }
            };
            this.searchEditText = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(r13.getThemedColor("chat_emojiSearchIcon"));
            this.searchEditText.setTextColor(r13.getThemedColor("windowBackgroundWhiteBlackText"));
            this.searchEditText.setBackgroundDrawable(null);
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
            this.searchEditText.setCursorColor(r13.getThemedColor("featuredStickers_addedIcon"));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 12.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(r13, i) { // from class: org.telegram.ui.Components.EmojiView.SearchField.3
                final /* synthetic */ int val$type;

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                }

                {
                    SearchField.this = this;
                    this.val$type = i;
                }

                @Override // android.text.TextWatcher
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
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150L).scaleX(z2 ? 1.0f : 0.1f);
                        if (!z2) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    int i2 = this.val$type;
                    if (i2 == 0) {
                        EmojiView.this.stickersSearchGridAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (i2 == 1) {
                        EmojiView.this.emojiSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (i2 != 2) {
                    } else {
                        EmojiView.this.gifSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    }
                }
            });
        }

        public /* synthetic */ void lambda$new$0(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        public void showShadow(boolean z, boolean z2) {
            if (!z || this.shadowView.getTag() != null) {
                if (!z && this.shadowView.getTag() != null) {
                    return;
                }
                AnimatorSet animatorSet = this.shadowAnimator;
                Integer num = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.shadowAnimator = null;
                }
                View view = this.shadowView;
                if (!z) {
                    num = 1;
                }
                view.setTag(num);
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
                    this.shadowAnimator.setDuration(200L);
                    this.shadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.shadowAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.SearchField.4
                        {
                            SearchField.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            SearchField.this.shadowAnimator = null;
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

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class TypedScrollListener extends RecyclerView.OnScrollListener {
        private boolean smoothScrolling;
        private final int type;

        public TypedScrollListener(int i) {
            EmojiView.this = r1;
            this.type = i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (recyclerView.getLayoutManager().isSmoothScrolling()) {
                this.smoothScrolling = true;
            } else if (i == 0) {
                if (!this.smoothScrolling) {
                    EmojiView.this.animateTabsY(this.type);
                }
                if (EmojiView.this.ignoreStickersScroll) {
                    EmojiView.this.ignoreStickersScroll = false;
                }
                this.smoothScrolling = false;
            } else {
                if (i == 1) {
                    if (EmojiView.this.ignoreStickersScroll) {
                        EmojiView.this.ignoreStickersScroll = false;
                    }
                    SearchField searchFieldForType = EmojiView.this.getSearchFieldForType(this.type);
                    if (searchFieldForType != null) {
                        searchFieldForType.hideKeyboard();
                    }
                    this.smoothScrolling = false;
                }
                if (!this.smoothScrolling) {
                    EmojiView.this.stopAnimatingTabsY(this.type);
                }
                if (this.type != 0) {
                    return;
                }
                if (EmojiView.this.chooseStickerActionTracker == null) {
                    EmojiView.this.createStickersChooseActionTracker();
                }
                EmojiView.this.chooseStickerActionTracker.doSomeAction();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            EmojiView.this.checkScroll(this.type);
            EmojiView.this.checkTabsY(this.type, i2);
            checkSearchFieldScroll();
            if (!this.smoothScrolling) {
                EmojiView.this.checkBottomTabScroll(i2);
            }
        }

        private void checkSearchFieldScroll() {
            int i = this.type;
            if (i == 0) {
                EmojiView.this.checkStickersSearchFieldScroll(false);
            } else if (i == 1) {
                EmojiView.this.checkEmojiSearchFieldScroll(false);
            } else if (i != 2) {
            } else {
                EmojiView.this.checkGifSearchFieldScroll(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class DraggableScrollSlidingTabStrip extends ScrollSlidingTabStrip {
        private float downX;
        private float downY;
        private boolean draggingHorizontally;
        private boolean draggingVertically;
        private boolean first;
        private float lastTranslateX;
        private float lastX;
        private boolean startedScroll;
        private final int touchSlop;
        private VelocityTracker vTracker;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public DraggableScrollSlidingTabStrip(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            EmojiView.this = r1;
            this.first = true;
            this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }

        @Override // org.telegram.ui.Components.ScrollSlidingTabStrip, android.widget.HorizontalScrollView, android.view.ViewGroup
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
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null && Math.abs(motionEvent.getRawY() - this.downY) >= this.touchSlop) {
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

        @Override // org.telegram.ui.Components.ScrollSlidingTabStrip, android.widget.HorizontalScrollView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (isDragging()) {
                return super.onTouchEvent(motionEvent);
            }
            if (this.first) {
                this.first = false;
                this.lastX = motionEvent.getX();
            }
            if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                EmojiView.this.lastStickersX = motionEvent.getRawX();
            }
            if (motionEvent.getAction() == 0) {
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                this.downX = motionEvent.getRawX();
                this.downY = motionEvent.getRawY();
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null) {
                if (Math.abs(motionEvent.getRawX() - this.downX) >= this.touchSlop && canScrollHorizontally((int) (this.downX - motionEvent.getRawX()))) {
                    this.draggingHorizontally = true;
                    AndroidUtilities.cancelRunOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable);
                    EmojiView.this.expandStickersByDragg = true;
                    EmojiView.this.updateStickerTabsPosition();
                } else if (Math.abs(motionEvent.getRawY() - this.downY) >= this.touchSlop) {
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
                AndroidUtilities.runOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable, 1500L);
            }
            if (this.draggingVertically) {
                if (this.vTracker == null) {
                    this.vTracker = VelocityTracker.obtain();
                }
                this.vTracker.addMovement(motionEvent);
                if (motionEvent.getAction() != 1 && motionEvent.getAction() != 3) {
                    EmojiView.this.dragListener.onDrag(Math.round(motionEvent.getRawY() - this.downY));
                } else {
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
                }
                cancelLongPress();
                return true;
            }
            float translationX = getTranslationX();
            if (getScrollX() == 0 && translationX == 0.0f) {
                if (!this.startedScroll && this.lastX - motionEvent.getX() < 0.0f) {
                    if (EmojiView.this.pager.beginFakeDrag()) {
                        this.startedScroll = true;
                        this.lastTranslateX = getTranslationX();
                    }
                } else if (this.startedScroll && this.lastX - motionEvent.getX() > 0.0f && EmojiView.this.pager.isFakeDragging()) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
            }
            if (this.startedScroll) {
                motionEvent.getX();
                try {
                    this.lastTranslateX = translationX;
                } catch (Exception e) {
                    try {
                        EmojiView.this.pager.endFakeDrag();
                    } catch (Exception unused) {
                    }
                    this.startedScroll = false;
                    FileLog.e(e);
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
            return this.startedScroll || super.onTouchEvent(motionEvent);
        }
    }

    /* loaded from: classes3.dex */
    public class ImageViewEmoji extends ImageView {
        ValueAnimator backAnimator;
        private ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder;
        public AnimatedEmojiDrawable drawable;
        public boolean ignoring;
        private boolean isRecent;
        private EmojiPack pack;
        public int position;
        float pressedProgress;
        private AnimatedEmojiSpan span;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ImageViewEmoji(Context context) {
            super(context);
            EmojiView.this = r2;
            setScaleType(ImageView.ScaleType.CENTER);
            setBackground(Theme.createRadSelectorDrawable(r2.getThemedColor("listSelectorSDK21"), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f)));
        }

        public void sendEmoji(String str) {
            String str2;
            if (getSpan() != null) {
                if (EmojiView.this.delegate == null) {
                    return;
                }
                long j = getSpan().documentId;
                TLRPC$Document tLRPC$Document = getSpan().document;
                if (tLRPC$Document == null) {
                    for (int i = 0; i < EmojiView.this.emojipacksProcessed.size(); i++) {
                        EmojiPack emojiPack = (EmojiPack) EmojiView.this.emojipacksProcessed.get(i);
                        int i2 = 0;
                        while (true) {
                            ArrayList<TLRPC$Document> arrayList = emojiPack.documents;
                            if (arrayList != null && i2 < arrayList.size()) {
                                if (emojiPack.documents.get(i2).id == j) {
                                    tLRPC$Document = emojiPack.documents.get(i2);
                                    break;
                                }
                                i2++;
                            }
                        }
                    }
                }
                if (tLRPC$Document == null) {
                    tLRPC$Document = AnimatedEmojiDrawable.findDocument(EmojiView.this.currentAccount, j);
                }
                TLRPC$Document tLRPC$Document2 = tLRPC$Document;
                String findAnimatedEmojiEmoticon = tLRPC$Document2 != null ? MessageObject.findAnimatedEmojiEmoticon(tLRPC$Document2) : null;
                if (MessageObject.isFreeEmoji(tLRPC$Document2) || UserConfig.getInstance(EmojiView.this.currentAccount).isPremium() || (EmojiView.this.delegate != null && EmojiView.this.delegate.isUserSelf())) {
                    EmojiView.this.shownBottomTabAfterClick = SystemClock.elapsedRealtime();
                    EmojiView.this.showBottomTab(true, true);
                    EmojiView emojiView = EmojiView.this;
                    emojiView.addEmojiToRecent("animated_" + j);
                    EmojiView.this.delegate.onCustomEmojiSelected(j, tLRPC$Document2, findAnimatedEmojiEmoticon, this.isRecent);
                    return;
                }
                EmojiView.this.showBottomTab(false, true);
                BulletinFactory of = EmojiView.this.fragment != null ? BulletinFactory.of(EmojiView.this.fragment) : BulletinFactory.of(EmojiView.this.bulletinContainer, EmojiView.this.resourcesProvider);
                if (EmojiView.this.premiumBulletin || EmojiView.this.fragment == null) {
                    SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(LocaleController.getString("UnlockPremiumEmojiHint", R.string.UnlockPremiumEmojiHint));
                    String string = LocaleController.getString("PremiumMore", R.string.PremiumMore);
                    final EmojiView emojiView2 = EmojiView.this;
                    of.createEmojiBulletin(tLRPC$Document2, replaceTags, string, new Runnable() { // from class: org.telegram.ui.Components.EmojiView$ImageViewEmoji$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            EmojiView.access$4400(EmojiView.this);
                        }
                    }).show();
                } else {
                    of.createSimpleBulletin(R.raw.saved_messages, AndroidUtilities.replaceTags(LocaleController.getString("UnlockPremiumEmojiHint2", R.string.UnlockPremiumEmojiHint2)), LocaleController.getString("Open", R.string.Open), new Runnable() { // from class: org.telegram.ui.Components.EmojiView$ImageViewEmoji$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            EmojiView.ImageViewEmoji.this.lambda$sendEmoji$1();
                        }
                    }).show();
                }
                EmojiView emojiView3 = EmojiView.this;
                emojiView3.premiumBulletin = !emojiView3.premiumBulletin;
                return;
            }
            EmojiView.this.shownBottomTabAfterClick = SystemClock.elapsedRealtime();
            EmojiView.this.showBottomTab(true, true);
            String str3 = str != null ? str : (String) getTag();
            new SpannableStringBuilder().append((CharSequence) str3);
            if (str != null) {
                if (EmojiView.this.delegate == null) {
                    return;
                }
                EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(str));
                return;
            }
            if (!this.isRecent && (str2 = Emoji.emojiColor.get(str3)) != null) {
                str3 = EmojiView.addColorToCode(str3, str2);
            }
            EmojiView.this.addEmojiToRecent(str3);
            if (EmojiView.this.delegate == null) {
                return;
            }
            EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(str3));
        }

        public /* synthetic */ void lambda$sendEmoji$1() {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", UserConfig.getInstance(EmojiView.this.currentAccount).getClientUserId());
            EmojiView.this.fragment.presentFragment(new AnonymousClass1(this, bundle));
        }

        /* renamed from: org.telegram.ui.Components.EmojiView$ImageViewEmoji$1 */
        /* loaded from: classes3.dex */
        public class AnonymousClass1 extends ChatActivity {
            AnonymousClass1(ImageViewEmoji imageViewEmoji, Bundle bundle) {
                super(bundle);
            }

            @Override // org.telegram.ui.ChatActivity, org.telegram.ui.ActionBar.BaseFragment
            public void onTransitionAnimationEnd(boolean z, boolean z2) {
                ChatActivityEnterView chatActivityEnterView;
                super.onTransitionAnimationEnd(z, z2);
                if (!z || (chatActivityEnterView = this.chatActivityEnterView) == null) {
                    return;
                }
                chatActivityEnterView.showEmojiView();
                this.chatActivityEnterView.postDelayed(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$ImageViewEmoji$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiView.ImageViewEmoji.AnonymousClass1.this.lambda$onTransitionAnimationEnd$0();
                    }
                }, 100L);
            }

            public /* synthetic */ void lambda$onTransitionAnimationEnd$0() {
                if (this.chatActivityEnterView.getEmojiView() != null) {
                    this.chatActivityEnterView.getEmojiView().scrollEmojisToAnimated();
                }
            }
        }

        public void setImageDrawable(Drawable drawable, boolean z) {
            super.setImageDrawable(drawable);
            this.isRecent = z;
        }

        public void setSpan(AnimatedEmojiSpan animatedEmojiSpan) {
            this.span = animatedEmojiSpan;
        }

        public AnimatedEmojiSpan getSpan() {
            return this.span;
        }

        @Override // android.widget.ImageView, android.view.View
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i));
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName("android.view.View");
        }

        @Override // android.view.View
        public void setPressed(boolean z) {
            ValueAnimator valueAnimator;
            if (isPressed() != z) {
                super.setPressed(z);
                invalidate();
                if (z && (valueAnimator = this.backAnimator) != null) {
                    valueAnimator.removeAllListeners();
                    this.backAnimator.cancel();
                }
                if (z) {
                    return;
                }
                float f = this.pressedProgress;
                if (f == 0.0f) {
                    return;
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(f, 0.0f);
                this.backAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiView$ImageViewEmoji$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        EmojiView.ImageViewEmoji.this.lambda$setPressed$2(valueAnimator2);
                    }
                });
                this.backAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.ImageViewEmoji.2
                    {
                        ImageViewEmoji.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        super.onAnimationEnd(animator);
                        ImageViewEmoji.this.backAnimator = null;
                    }
                });
                this.backAnimator.setInterpolator(new OvershootInterpolator(5.0f));
                this.backAnimator.setDuration(350L);
                this.backAnimator.start();
            }
        }

        public /* synthetic */ void lambda$setPressed$2(ValueAnimator valueAnimator) {
            this.pressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        @Override // android.widget.ImageView, android.view.View
        protected void onDraw(Canvas canvas) {
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
            canvas.scale(f3, f3, getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f);
            super.onDraw(canvas);
            canvas.restore();
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiPopupWindow extends PopupWindow {
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
            if (this.mSuperScrollListener == null || (viewTreeObserver = this.mViewTreeObserver) == null) {
                return;
            }
            if (viewTreeObserver.isAlive()) {
                this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
            }
            this.mViewTreeObserver = null;
        }

        private void registerListener(View view) {
            if (this.mSuperScrollListener != null) {
                ViewTreeObserver viewTreeObserver = view.getWindowToken() != null ? view.getViewTreeObserver() : null;
                ViewTreeObserver viewTreeObserver2 = this.mViewTreeObserver;
                if (viewTreeObserver == viewTreeObserver2) {
                    return;
                }
                if (viewTreeObserver2 != null && viewTreeObserver2.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = viewTreeObserver;
                if (viewTreeObserver == null) {
                    return;
                }
                viewTreeObserver.addOnScrollChangedListener(this.mSuperScrollListener);
            }
        }

        @Override // android.widget.PopupWindow
        public void showAsDropDown(View view, int i, int i2) {
            try {
                super.showAsDropDown(view, i, i2);
                registerListener(view);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // android.widget.PopupWindow
        public void update(View view, int i, int i2, int i3, int i4) {
            super.update(view, i, i2, i3, i4);
            registerListener(view);
        }

        @Override // android.widget.PopupWindow
        public void update(View view, int i, int i2) {
            super.update(view, i, i2);
            registerListener(view);
        }

        @Override // android.widget.PopupWindow
        public void showAtLocation(View view, int i, int i2, int i3) {
            super.showAtLocation(view, i, i2, i3);
            unregisterListener();
        }

        @Override // android.widget.PopupWindow
        public void dismiss() {
            setFocusable(false);
            try {
                super.dismiss();
            } catch (Exception unused) {
            }
            unregisterListener();
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiColorPickerView extends View {
        private Drawable arrowDrawable;
        private int arrowX;
        private Drawable backgroundDrawable;
        private String currentEmoji;
        private Drawable[] drawables;
        private RectF rect;
        private Paint rectPaint;
        private int selection;
        private AnimatedFloat selectionAnimated;

        public void setEmoji(String str, int i) {
            String str2;
            this.currentEmoji = str;
            this.arrowX = i;
            int i2 = 0;
            while (i2 < this.drawables.length) {
                if (i2 != 0) {
                    str2 = EmojiView.addColorToCode(str, i2 != 1 ? i2 != 2 ? i2 != 3 ? i2 != 4 ? i2 != 5 ? "" : "\u1f3ff" : "\u1f3fe" : "\u1f3fd" : "\u1f3fc" : "\u1f3fb");
                } else {
                    str2 = str;
                }
                this.drawables[i2] = Emoji.getEmojiBigDrawable(str2);
                i2++;
            }
            invalidate();
        }

        public void setSelection(int i) {
            if (this.selection == i) {
                return;
            }
            this.selection = i;
            invalidate();
        }

        public int getSelection() {
            return this.selection;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiColorPickerView(Context context) {
            super(context);
            EmojiView.this = r4;
            this.drawables = new Drawable[6];
            this.rectPaint = new Paint(1);
            this.rect = new RectF();
            this.selectionAnimated = new AnimatedFloat(this, 125L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.backgroundDrawable = getResources().getDrawable(R.drawable.stickers_back_all);
            this.arrowDrawable = getResources().getDrawable(R.drawable.stickers_back_arrow);
            Theme.setDrawableColor(this.backgroundDrawable, r4.getThemedColor("dialogBackground"));
            Theme.setDrawableColor(this.arrowDrawable, r4.getThemedColor("dialogBackground"));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
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
            float f2 = this.selectionAnimated.set(this.selection);
            float dp4 = (EmojiView.this.emojiSize * f2) + AndroidUtilities.dp((f2 * 4.0f) + 5.0f);
            float dp5 = AndroidUtilities.dp(9.0f);
            this.rect.set(dp4, dp5 - ((int) AndroidUtilities.dpf2(3.5f)), EmojiView.this.emojiSize + dp4, EmojiView.this.emojiSize + dp5 + AndroidUtilities.dp(3.0f));
            this.rectPaint.setColor(EmojiView.this.getThemedColor("listSelectorSDK21"));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.rectPaint);
            if (this.currentEmoji != null) {
                for (int i = 0; i < 6; i++) {
                    Drawable drawable2 = this.drawables[i];
                    if (drawable2 != null) {
                        float dp6 = (EmojiView.this.emojiSize * i) + AndroidUtilities.dp((i * 4) + 5);
                        float min = ((1.0f - (Math.min(0.5f, Math.abs(i - f2)) * 2.0f)) * 0.1f) + 0.9f;
                        canvas.save();
                        canvas.scale(min, min, (EmojiView.this.emojiSize / 2.0f) + dp6, (EmojiView.this.emojiSize / 2.0f) + dp5);
                        int i2 = (int) dp6;
                        int i3 = (int) dp5;
                        drawable2.setBounds(i2, i3, EmojiView.this.emojiSize + i2, EmojiView.this.emojiSize + i3);
                        drawable2.draw(canvas);
                        canvas.restore();
                    }
                }
            }
        }
    }

    public EmojiView(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, Context context, boolean z4, TLRPC$ChatFull tLRPC$ChatFull, ViewGroup viewGroup, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        final Theme.ResourcesProvider resourcesProvider2;
        int i;
        this.allTabs = new ArrayList<>();
        this.currentTabs = new ArrayList<>();
        this.firstEmojiAttach = true;
        this.gifSearchPreloader = new GifSearchPreloader();
        this.gifCache = new HashMap();
        this.firstGifAttach = true;
        this.gifRecentTabNum = -2;
        this.gifTrendingTabNum = -2;
        this.gifFirstEmojiTabNum = -2;
        this.firstStickersAttach = true;
        this.tabsMinusDy = new int[3];
        this.tabsYAnimators = new ObjectAnimator[3];
        this.currentAccount = UserConfig.selectedAccount;
        this.stickerSets = new ArrayList<>();
        this.recentGifs = new ArrayList<>();
        this.recentStickers = new ArrayList<>();
        this.favouriteStickers = new ArrayList<>();
        this.premiumStickers = new ArrayList<>();
        this.featuredStickerSets = new ArrayList<>();
        this.featuredEmojiSets = new ArrayList<>();
        new ArrayList();
        this.expandedEmojiSets = new ArrayList<>();
        this.installedEmojiSets = new ArrayList<>();
        this.emojipacksProcessed = new ArrayList<>();
        this.toInstall = new HashMap<>();
        this.primaryInstallingStickerSets = new TLRPC$StickerSetCovered[10];
        this.installingStickerSets = new LongSparseArray<>();
        this.removingStickerSets = new LongSparseArray<>();
        this.location = new int[2];
        this.recentTabNum = -2;
        this.favTabNum = -2;
        this.trendingTabNum = -2;
        this.premiumTabNum = -2;
        this.currentBackgroundType = -1;
        this.checkExpandStickerTabsRunnable = new Runnable() { // from class: org.telegram.ui.Components.EmojiView.1
            {
                EmojiView.this = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (!EmojiView.this.stickersTab.isDragging()) {
                    EmojiView.this.expandStickersByDragg = false;
                    EmojiView.this.updateStickerTabsPosition();
                }
            }
        };
        this.contentPreviewViewerDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() { // from class: org.telegram.ui.Components.EmojiView.2
            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ boolean can() {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$can(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ boolean needMenu() {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needMenu(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ boolean needOpen() {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needOpen(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ boolean needRemove() {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needRemove(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean needSend() {
                return true;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ void remove(SendMessagesHelper.ImportingSticker importingSticker) {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$remove(this, importingSticker);
            }

            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void sendSticker(TLRPC$Document tLRPC$Document, String str, Object obj, boolean z5, int i2) {
                EmojiView.this.delegate.onStickerSelected(null, tLRPC$Document, str, obj, null, z5, i2);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean canSchedule() {
                return EmojiView.this.delegate.canSchedule();
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean isInScheduleMode() {
                return EmojiView.this.delegate.isInScheduleMode();
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void openSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z5) {
                if (tLRPC$InputStickerSet == null) {
                    return;
                }
                EmojiView.this.delegate.onShowStickerSet(null, tLRPC$InputStickerSet);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void sendGif(Object obj, Object obj2, boolean z5, int i2) {
                if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                    EmojiView.this.delegate.onGifSelected(null, obj, null, obj2, z5, i2);
                } else if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifSearchAdapter) {
                } else {
                    EmojiView.this.delegate.onGifSelected(null, obj, null, obj2, z5, i2);
                }
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void gifAddedOrDeleted() {
                EmojiView.this.updateRecentGifs();
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public long getDialogId() {
                return EmojiView.this.delegate.getDialogId();
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public String getQuery(boolean z5) {
                if (z5) {
                    if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifSearchAdapter) {
                        return null;
                    }
                    return EmojiView.this.gifSearchAdapter.lastSearchImageString;
                } else if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                    return null;
                } else {
                    return EmojiView.this.emojiSearchAdapter.lastSearchEmojiString;
                }
            }
        };
        this.premiumBulletin = true;
        this.animateExpandFromPosition = -1;
        this.animateExpandToPosition = -1;
        this.animateExpandStartTime = -1L;
        this.emojiPackAlertOpened = false;
        this.rect = new android.graphics.Rect();
        this.fragment = baseFragment;
        this.allowAnimatedEmoji = z;
        this.resourcesProvider = resourcesProvider;
        int themedColor = getThemedColor("chat_emojiBottomPanelIcon");
        int argb = Color.argb(30, Color.red(themedColor), Color.green(themedColor), Color.blue(themedColor));
        this.searchFieldHeight = AndroidUtilities.dp(64.0f);
        this.needEmojiSearch = z4;
        this.tabIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, R.drawable.smiles_tab_smiles, getThemedColor("chat_emojiPanelBackspace"), getThemedColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(context, R.drawable.smiles_tab_gif, getThemedColor("chat_emojiPanelBackspace"), getThemedColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(context, R.drawable.smiles_tab_stickers, getThemedColor("chat_emojiPanelBackspace"), getThemedColor("chat_emojiPanelIconSelected"))};
        int i2 = R.drawable.msg_emoji_recent;
        Drawable createEmojiIconSelectorDrawable = Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_new1, getThemedColor("chat_emojiPanelIcon"), getThemedColor("chat_emojiPanelIconSelected"));
        this.searchIconDrawable = createEmojiIconSelectorDrawable;
        Drawable createEmojiIconSelectorDrawable2 = Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_new2, getThemedColor("chat_emojiPanelStickerPackSelectorLine"), getThemedColor("chat_emojiPanelStickerPackSelectorLine"));
        this.searchIconDotDrawable = createEmojiIconSelectorDrawable2;
        this.stickerIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, i2, getThemedColor("chat_emojiPanelIcon"), getThemedColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_faves, getThemedColor("chat_emojiPanelIcon"), getThemedColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_new3, getThemedColor("chat_emojiPanelIcon"), getThemedColor("chat_emojiPanelIconSelected")), new LayerDrawable(new Drawable[]{createEmojiIconSelectorDrawable, createEmojiIconSelectorDrawable2})};
        this.gifIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, i2, getThemedColor("chat_emojiPanelIcon"), getThemedColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(context, R.drawable.stickers_gifs_trending, getThemedColor("chat_emojiPanelIcon"), getThemedColor("chat_emojiPanelIconSelected"))};
        this.emojiTitles = new String[]{LocaleController.getString("Emoji1", R.string.Emoji1), LocaleController.getString("Emoji2", R.string.Emoji2), LocaleController.getString("Emoji3", R.string.Emoji3), LocaleController.getString("Emoji4", R.string.Emoji4), LocaleController.getString("Emoji5", R.string.Emoji5), LocaleController.getString("Emoji6", R.string.Emoji6), LocaleController.getString("Emoji7", R.string.Emoji7), LocaleController.getString("Emoji8", R.string.Emoji8)};
        this.info = tLRPC$ChatFull;
        Paint paint = new Paint(1);
        this.dotPaint = paint;
        paint.setColor(getThemedColor("chat_emojiPanelNewTrending"));
        int i3 = Build.VERSION.SDK_INT;
        if (i3 >= 21) {
            this.outlineProvider = new ViewOutlineProvider(this) { // from class: org.telegram.ui.Components.EmojiView.3
                @Override // android.view.ViewOutlineProvider
                @TargetApi(21)
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(view.getPaddingLeft(), view.getPaddingTop(), view.getMeasuredWidth() - view.getPaddingRight(), view.getMeasuredHeight() - view.getPaddingBottom(), AndroidUtilities.dp(6.0f));
                }
            };
        }
        this.emojiContainer = new FrameLayout(context);
        Tab tab = new Tab();
        tab.type = 0;
        tab.view = this.emojiContainer;
        this.allTabs.add(tab);
        if (z) {
            MediaDataController.getInstance(this.currentAccount).checkStickers(5);
            MediaDataController.getInstance(this.currentAccount).checkFeaturedEmoji();
        }
        this.emojiGridView = new EmojiGridView(context);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDelay(0L);
        defaultItemAnimator.setAddDuration(220L);
        defaultItemAnimator.setMoveDuration(220L);
        defaultItemAnimator.setChangeDuration(160L);
        defaultItemAnimator.setMoveInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.emojiGridView.setItemAnimator(defaultItemAnimator);
        this.emojiGridView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.EmojiView.4
            {
                EmojiView.this = this;
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            /* JADX WARN: Removed duplicated region for block: B:196:0x01c7  */
            /* JADX WARN: Removed duplicated region for block: B:199:0x01d7  */
            /* JADX WARN: Removed duplicated region for block: B:200:0x01da  */
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public boolean onItemClick(android.view.View r18, int r19) {
                /*
                    Method dump skipped, instructions count: 594
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.AnonymousClass4.onItemClick(android.view.View, int):boolean");
            }
        });
        this.emojiGridView.setInstantClick(true);
        EmojiGridView emojiGridView = this.emojiGridView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 8) { // from class: org.telegram.ui.Components.EmojiView.5
            {
                EmojiView.this = this;
            }

            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i4) {
                try {
                    LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(recyclerView.getContext(), 2) { // from class: org.telegram.ui.Components.EmojiView.5.1
                        {
                            AnonymousClass5.this = this;
                        }

                        @Override // androidx.recyclerview.widget.LinearSmoothScrollerCustom
                        public void onEnd() {
                            EmojiView.this.emojiSmoothScrolling = false;
                        }
                    };
                    linearSmoothScrollerCustom.setTargetPosition(i4);
                    startSmoothScroll(linearSmoothScrollerCustom);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        };
        this.emojiLayoutManager = gridLayoutManager;
        emojiGridView.setLayoutManager(gridLayoutManager);
        this.emojiGridView.setTopGlowOffset(AndroidUtilities.dp(38.0f));
        this.emojiGridView.setBottomGlowOffset(AndroidUtilities.dp(36.0f));
        this.emojiGridView.setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(36.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(44.0f));
        this.emojiGridView.setGlowColor(getThemedColor("chat_emojiPanelBackground"));
        this.emojiGridView.setSelectorDrawableColor(0);
        this.emojiGridView.setClipToPadding(false);
        this.emojiLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.EmojiView.6
            {
                EmojiView.this = this;
            }

            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int i4) {
                if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                    if ((EmojiView.this.needEmojiSearch && i4 == 0) || i4 == EmojiView.this.emojiAdapter.trendingRow || i4 == EmojiView.this.emojiAdapter.trendingHeaderRow || EmojiView.this.emojiAdapter.positionToSection.indexOfKey(i4) >= 0 || EmojiView.this.emojiAdapter.positionToUnlock.indexOfKey(i4) >= 0) {
                        return EmojiView.this.emojiLayoutManager.getSpanCount();
                    }
                } else if (i4 == 0 || (i4 == 1 && EmojiView.this.emojiSearchAdapter.searchWas && EmojiView.this.emojiSearchAdapter.result.isEmpty())) {
                    return EmojiView.this.emojiLayoutManager.getSpanCount();
                }
                return 1;
            }

            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanGroupIndex(int i4, int i5) {
                return super.getSpanGroupIndex(i4, i5);
            }
        });
        EmojiGridView emojiGridView2 = this.emojiGridView;
        EmojiGridAdapter emojiGridAdapter = new EmojiGridAdapter();
        this.emojiAdapter = emojiGridAdapter;
        emojiGridView2.setAdapter(emojiGridAdapter);
        this.emojiGridView.addItemDecoration(new EmojiGridSpacing());
        this.emojiSearchAdapter = new EmojiSearchAdapter();
        this.emojiContainer.addView(this.emojiGridView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerAnimationScrollHelper recyclerAnimationScrollHelper = new RecyclerAnimationScrollHelper(this.emojiGridView, this.emojiLayoutManager);
        this.emojiScrollHelper = recyclerAnimationScrollHelper;
        recyclerAnimationScrollHelper.setAnimationCallback(new RecyclerAnimationScrollHelper.AnimationCallback() { // from class: org.telegram.ui.Components.EmojiView.7
            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimationCallback
            public void onPreAnimation() {
                EmojiView.this.emojiGridView.updateEmojiDrawables();
                EmojiView.this.emojiSmoothScrolling = true;
            }

            @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimationCallback
            public void onEndAnimation() {
                EmojiView.this.emojiSmoothScrolling = false;
                EmojiView.this.emojiGridView.updateEmojiDrawables();
            }

            @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimationCallback
            public void ignoreView(View view, boolean z5) {
                if (view instanceof ImageViewEmoji) {
                    ((ImageViewEmoji) view).ignoring = z5;
                }
            }
        });
        this.emojiGridView.setOnScrollListener(new TypedScrollListener(1) { // from class: org.telegram.ui.Components.EmojiView.8
            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.EmojiView.TypedScrollListener, androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i4, int i5) {
                EmojiView.this.updateEmojiTabsPosition();
                super.onScrolled(recyclerView, i4, i5);
            }

            @Override // org.telegram.ui.Components.EmojiView.TypedScrollListener, androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i4) {
                if (i4 == 0) {
                    EmojiView.this.emojiSmoothScrolling = false;
                }
                super.onScrollStateChanged(recyclerView, i4);
            }
        });
        this.emojiTabs = new EmojiTabsStrip(context, resourcesProvider, true, z, baseFragment != null ? new Runnable() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                EmojiView.this.lambda$new$1();
            }
        } : null) { // from class: org.telegram.ui.Components.EmojiView.9
            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.EmojiTabsStrip
            protected boolean isInstalled(EmojiPack emojiPack) {
                return emojiPack.installed || EmojiView.this.installedEmojiSets.contains(Long.valueOf(emojiPack.set.id));
            }

            @Override // android.view.View
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                if (EmojiView.this.emojiTabsShadow != null) {
                    EmojiView.this.emojiTabsShadow.setTranslationY(f);
                }
            }

            /* JADX WARN: Removed duplicated region for block: B:70:0x00aa  */
            /* JADX WARN: Removed duplicated region for block: B:75:? A[RETURN, SYNTHETIC] */
            @Override // org.telegram.ui.Components.EmojiTabsStrip
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            protected boolean onTabClick(int r10) {
                /*
                    r9 = this;
                    org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                    boolean r0 = org.telegram.ui.Components.EmojiView.access$6000(r0)
                    r1 = 0
                    if (r0 == 0) goto La
                    return r1
                La:
                    r0 = 0
                    if (r10 != 0) goto L18
                    org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                    boolean r2 = org.telegram.ui.Components.EmojiView.access$6400(r2)
                    java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                    goto L1b
                L18:
                    int r10 = r10 + (-1)
                    r2 = r0
                L1b:
                    if (r2 != 0) goto L44
                    java.lang.String[][] r3 = org.telegram.messenger.EmojiData.dataColored
                    int r3 = r3.length
                    if (r10 >= r3) goto L44
                    org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                    org.telegram.ui.Components.EmojiView$EmojiGridAdapter r3 = org.telegram.ui.Components.EmojiView.access$6500(r3)
                    android.util.SparseIntArray r3 = org.telegram.ui.Components.EmojiView.EmojiGridAdapter.access$7400(r3)
                    int r3 = r3.indexOfKey(r10)
                    if (r3 < 0) goto L44
                    org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                    org.telegram.ui.Components.EmojiView$EmojiGridAdapter r2 = org.telegram.ui.Components.EmojiView.access$6500(r2)
                    android.util.SparseIntArray r2 = org.telegram.ui.Components.EmojiView.EmojiGridAdapter.access$7400(r2)
                    int r2 = r2.get(r10)
                    java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                L44:
                    if (r2 != 0) goto La7
                    org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                    java.util.ArrayList r3 = r3.getEmojipacks()
                    java.lang.String[][] r4 = org.telegram.messenger.EmojiData.dataColored
                    int r4 = r4.length
                    int r10 = r10 - r4
                    if (r3 == 0) goto La7
                    if (r10 < 0) goto La7
                    int r4 = r3.size()
                    if (r10 >= r4) goto La7
                    r2 = 0
                L5b:
                    org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                    java.util.ArrayList r4 = org.telegram.ui.Components.EmojiView.access$3600(r4)
                    int r4 = r4.size()
                    if (r2 >= r4) goto L89
                    org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                    java.util.ArrayList r4 = org.telegram.ui.Components.EmojiView.access$3600(r4)
                    java.lang.Object r4 = r4.get(r2)
                    org.telegram.ui.Components.EmojiView$EmojiPack r4 = (org.telegram.ui.Components.EmojiView.EmojiPack) r4
                    org.telegram.tgnet.TLRPC$StickerSet r4 = r4.set
                    long r4 = r4.id
                    java.lang.Object r6 = r3.get(r10)
                    org.telegram.ui.Components.EmojiView$EmojiPack r6 = (org.telegram.ui.Components.EmojiView.EmojiPack) r6
                    org.telegram.tgnet.TLRPC$StickerSet r6 = r6.set
                    long r6 = r6.id
                    int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                    if (r8 != 0) goto L86
                    goto L8a
                L86:
                    int r2 = r2 + 1
                    goto L5b
                L89:
                    r2 = -1
                L8a:
                    org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                    org.telegram.ui.Components.EmojiView$EmojiGridAdapter r10 = org.telegram.ui.Components.EmojiView.access$6500(r10)
                    android.util.SparseIntArray r10 = org.telegram.ui.Components.EmojiView.EmojiGridAdapter.access$7400(r10)
                    java.lang.String[][] r3 = org.telegram.messenger.EmojiData.dataColored
                    int r3 = r3.length
                    int r2 = r2 + r3
                    int r10 = r10.get(r2)
                    java.lang.Integer r2 = java.lang.Integer.valueOf(r10)
                    r10 = -1055916032(0xffffffffCLASSNAME, float:-9.0)
                    int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                    goto La8
                La7:
                    r10 = 0
                La8:
                    if (r2 == 0) goto Lca
                    org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                    org.telegram.ui.Components.EmojiView$EmojiGridView r3 = org.telegram.ui.Components.EmojiView.access$1000(r3)
                    r3.stopScroll()
                    org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                    int r4 = r2.intValue()
                    org.telegram.ui.Components.EmojiView.access$7500(r3, r4)
                    org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                    int r2 = r2.intValue()
                    org.telegram.ui.Components.EmojiView.access$7600(r3, r2, r10)
                    org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                    org.telegram.ui.Components.EmojiView.access$7700(r10, r0, r1)
                Lca:
                    r10 = 1
                    return r10
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.AnonymousClass9.onTabClick(int):boolean");
            }
        };
        if (z4) {
            SearchField searchField = new SearchField(context, 1);
            this.emojiSearchField = searchField;
            this.emojiContainer.addView(searchField, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
            this.emojiSearchField.searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.Components.EmojiView.10
                {
                    EmojiView.this = this;
                }

                @Override // android.view.View.OnFocusChangeListener
                public void onFocusChange(View view, boolean z5) {
                    if (z5) {
                        EmojiView.this.lastSearchKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(EmojiView.this.lastSearchKeyboardLanguage);
                    }
                }
            });
        }
        this.emojiTabs.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
        this.emojiAdapter.processEmoji(true);
        this.emojiTabs.updateEmojiPacks(getEmojipacks());
        this.emojiContainer.addView(this.emojiTabs, LayoutHelper.createFrame(-1, 36.0f));
        View view = new View(context);
        this.emojiTabsShadow = view;
        view.setAlpha(0.0f);
        this.emojiTabsShadow.setTag(1);
        this.emojiTabsShadow.setBackgroundColor(getThemedColor("chat_emojiPanelShadowLine"));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(36.0f);
        this.emojiContainer.addView(this.emojiTabsShadow, layoutParams);
        if (z2) {
            if (z3) {
                this.gifContainer = new FrameLayout(context);
                Tab tab2 = new Tab();
                tab2.type = 1;
                tab2.view = this.gifContainer;
                this.allTabs.add(tab2);
                RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.EmojiView.11
                    private boolean ignoreLayout;
                    private boolean wasMeasured;

                    {
                        EmojiView.this = this;
                    }

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.gifGridView, 0, EmojiView.this.contentPreviewViewerDelegate, this.resourcesProvider);
                    }

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
                    public void onMeasure(int i4, int i5) {
                        super.onMeasure(i4, i5);
                        if (!this.wasMeasured) {
                            EmojiView.this.gifAdapter.notifyDataSetChanged();
                            this.wasMeasured = true;
                        }
                    }

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
                    public void onLayout(boolean z5, int i4, int i5, int i6, int i7) {
                        if (EmojiView.this.firstGifAttach && EmojiView.this.gifAdapter.getItemCount() > 1) {
                            this.ignoreLayout = true;
                            EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(1, 0);
                            EmojiView.this.gifSearchField.setVisibility(0);
                            EmojiView.this.gifTabs.onPageScrolled(0, 0);
                            EmojiView.this.firstGifAttach = false;
                            this.ignoreLayout = false;
                        }
                        super.onLayout(z5, i4, i5, i6, i7);
                        EmojiView.this.checkGifSearchFieldScroll(true);
                    }

                    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
                    public void requestLayout() {
                        if (this.ignoreLayout) {
                            return;
                        }
                        super.requestLayout();
                    }
                };
                this.gifGridView = recyclerListView;
                recyclerListView.setClipToPadding(false);
                RecyclerListView recyclerListView2 = this.gifGridView;
                GifLayoutManager gifLayoutManager = new GifLayoutManager(context);
                this.gifLayoutManager = gifLayoutManager;
                recyclerListView2.setLayoutManager(gifLayoutManager);
                this.gifGridView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Components.EmojiView.12
                    {
                        EmojiView.this = this;
                    }

                    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
                    public void getItemOffsets(android.graphics.Rect rect, View view2, RecyclerView recyclerView, RecyclerView.State state) {
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(view2);
                        int i4 = 0;
                        if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter && childAdapterPosition == EmojiView.this.gifAdapter.trendingSectionItem) {
                            rect.set(0, 0, 0, 0);
                        } else if (childAdapterPosition != 0) {
                            rect.left = 0;
                            rect.bottom = 0;
                            rect.top = AndroidUtilities.dp(2.0f);
                            if (!EmojiView.this.gifLayoutManager.isLastInRow(childAdapterPosition - 1)) {
                                i4 = AndroidUtilities.dp(2.0f);
                            }
                            rect.right = i4;
                        } else {
                            rect.set(0, 0, 0, 0);
                        }
                    }
                });
                this.gifGridView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                this.gifGridView.setOverScrollMode(2);
                ((SimpleItemAnimator) this.gifGridView.getItemAnimator()).setSupportsChangeAnimations(false);
                RecyclerListView recyclerListView3 = this.gifGridView;
                GifAdapter gifAdapter = new GifAdapter(this, context, true);
                this.gifAdapter = gifAdapter;
                recyclerListView3.setAdapter(gifAdapter);
                this.gifSearchAdapter = new GifAdapter(this, context);
                this.gifGridView.setOnScrollListener(new TypedScrollListener(2));
                resourcesProvider2 = resourcesProvider;
                this.gifGridView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnTouchListener
                    public final boolean onTouch(View view2, MotionEvent motionEvent) {
                        boolean lambda$new$2;
                        lambda$new$2 = EmojiView.this.lambda$new$2(resourcesProvider2, view2, motionEvent);
                        return lambda$new$2;
                    }
                });
                RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda9
                    @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                    public final void onItemClick(View view2, int i4) {
                        EmojiView.this.lambda$new$3(view2, i4);
                    }
                };
                this.gifOnItemClickListener = onItemClickListener;
                this.gifGridView.setOnItemClickListener(onItemClickListener);
                this.gifContainer.addView(this.gifGridView, LayoutHelper.createFrame(-1, -1.0f));
                SearchField searchField2 = new SearchField(context, 2);
                this.gifSearchField = searchField2;
                searchField2.setVisibility(4);
                this.gifContainer.addView(this.gifSearchField, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
                DraggableScrollSlidingTabStrip draggableScrollSlidingTabStrip = new DraggableScrollSlidingTabStrip(context, resourcesProvider2);
                this.gifTabs = draggableScrollSlidingTabStrip;
                draggableScrollSlidingTabStrip.setType(ScrollSlidingTabStrip.Type.TAB);
                this.gifTabs.setUnderlineHeight(AndroidUtilities.getShadowHeight());
                this.gifTabs.setIndicatorColor(getThemedColor("chat_emojiPanelStickerPackSelectorLine"));
                this.gifTabs.setUnderlineColor(getThemedColor("chat_emojiPanelShadowLine"));
                this.gifTabs.setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
                this.gifContainer.addView(this.gifTabs, LayoutHelper.createFrame(-1, 36, 51));
                updateGifTabs();
                this.gifTabs.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda10
                    @Override // org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate
                    public final void onPageSelected(int i4) {
                        EmojiView.this.lambda$new$4(i4);
                    }
                });
                this.gifAdapter.loadTrendingGifs();
            } else {
                resourcesProvider2 = resourcesProvider;
            }
            this.stickersContainer = new FrameLayout(context) { // from class: org.telegram.ui.Components.EmojiView.13
                {
                    EmojiView.this = this;
                }

                @Override // android.view.ViewGroup, android.view.View
                protected void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    EmojiView.this.stickersContainerAttached = true;
                    EmojiView.this.updateStickerTabsPosition();
                    if (EmojiView.this.chooseStickerActionTracker != null) {
                        EmojiView.this.chooseStickerActionTracker.checkVisibility();
                    }
                }

                @Override // android.view.ViewGroup, android.view.View
                protected void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    EmojiView.this.stickersContainerAttached = false;
                    EmojiView.this.updateStickerTabsPosition();
                    if (EmojiView.this.chooseStickerActionTracker != null) {
                        EmojiView.this.chooseStickerActionTracker.checkVisibility();
                    }
                }
            };
            MediaDataController.getInstance(this.currentAccount).checkStickers(0);
            MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
            RecyclerListViewWithOverlayDraw recyclerListViewWithOverlayDraw = new RecyclerListViewWithOverlayDraw(context) { // from class: org.telegram.ui.Components.EmojiView.14
                boolean ignoreLayout;

                {
                    EmojiView.this = this;
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate, this.resourcesProvider);
                }

                @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
                public void setVisibility(int i4) {
                    super.setVisibility(i4);
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
                public void onLayout(boolean z5, int i4, int i5, int i6, int i7) {
                    if (EmojiView.this.firstStickersAttach && EmojiView.this.stickersGridAdapter.getItemCount() > 0) {
                        this.ignoreLayout = true;
                        EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(1, 0);
                        EmojiView.this.firstStickersAttach = false;
                        this.ignoreLayout = false;
                    }
                    super.onLayout(z5, i4, i5, i6, i7);
                    EmojiView.this.checkStickersSearchFieldScroll(true);
                }

                @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
                public void requestLayout() {
                    if (this.ignoreLayout) {
                        return;
                    }
                    super.requestLayout();
                }
            };
            this.stickersGridView = recyclerListViewWithOverlayDraw;
            GridLayoutManager gridLayoutManager2 = new GridLayoutManager(context, 5) { // from class: org.telegram.ui.Components.EmojiView.15
                {
                    EmojiView.this = this;
                }

                @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i4) {
                    try {
                        LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(recyclerView.getContext(), 2);
                        linearSmoothScrollerCustom.setTargetPosition(i4);
                        startSmoothScroll(linearSmoothScrollerCustom);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }

                @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public int scrollVerticallyBy(int i4, RecyclerView.Recycler recycler, RecyclerView.State state) {
                    int scrollVerticallyBy = super.scrollVerticallyBy(i4, recycler, state);
                    if (scrollVerticallyBy != 0 && EmojiView.this.stickersGridView.getScrollState() == 1) {
                        EmojiView.this.expandStickersByDragg = false;
                        EmojiView.this.updateStickerTabsPosition();
                    }
                    if (EmojiView.this.chooseStickerActionTracker == null) {
                        EmojiView.this.createStickersChooseActionTracker();
                    }
                    EmojiView.this.chooseStickerActionTracker.doSomeAction();
                    return scrollVerticallyBy;
                }
            };
            this.stickersLayoutManager = gridLayoutManager2;
            recyclerListViewWithOverlayDraw.setLayoutManager(gridLayoutManager2);
            this.stickersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.EmojiView.16
                {
                    EmojiView.this = this;
                }

                @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                public int getSpanSize(int i4) {
                    if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersGridAdapter) {
                        if (i4 != EmojiView.this.stickersSearchGridAdapter.totalItems && (EmojiView.this.stickersSearchGridAdapter.cache.get(i4) == null || (EmojiView.this.stickersSearchGridAdapter.cache.get(i4) instanceof TLRPC$Document))) {
                            return 1;
                        }
                        return EmojiView.this.stickersGridAdapter.stickersPerRow;
                    } else if (i4 == 0) {
                        return EmojiView.this.stickersGridAdapter.stickersPerRow;
                    } else {
                        if (i4 != EmojiView.this.stickersGridAdapter.totalItems && (EmojiView.this.stickersGridAdapter.cache.get(i4) == null || (EmojiView.this.stickersGridAdapter.cache.get(i4) instanceof TLRPC$Document))) {
                            return 1;
                        }
                        return EmojiView.this.stickersGridAdapter.stickersPerRow;
                    }
                }
            });
            this.stickersGridView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
            this.stickersGridView.setClipToPadding(false);
            Tab tab3 = new Tab();
            tab3.type = 2;
            tab3.view = this.stickersContainer;
            this.allTabs.add(tab3);
            this.stickersSearchGridAdapter = new StickersSearchGridAdapter(context);
            RecyclerListView recyclerListView4 = this.stickersGridView;
            StickersGridAdapter stickersGridAdapter = new StickersGridAdapter(context);
            this.stickersGridAdapter = stickersGridAdapter;
            recyclerListView4.setAdapter(stickersGridAdapter);
            this.stickersGridView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda2
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view2, MotionEvent motionEvent) {
                    boolean lambda$new$5;
                    lambda$new$5 = EmojiView.this.lambda$new$5(resourcesProvider2, view2, motionEvent);
                    return lambda$new$5;
                }
            });
            RecyclerListView.OnItemClickListener onItemClickListener2 = new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda8
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view2, int i4) {
                    EmojiView.this.lambda$new$6(view2, i4);
                }
            };
            this.stickersOnItemClickListener = onItemClickListener2;
            this.stickersGridView.setOnItemClickListener(onItemClickListener2);
            this.stickersGridView.setGlowColor(getThemedColor("chat_emojiPanelBackground"));
            this.stickersContainer.addView(this.stickersGridView);
            this.stickersScrollHelper = new RecyclerAnimationScrollHelper(this.stickersGridView, this.stickersLayoutManager);
            SearchField searchField3 = new SearchField(context, 0);
            this.stickersSearchField = searchField3;
            this.stickersContainer.addView(searchField3, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
            AnonymousClass17 anonymousClass17 = new AnonymousClass17(context, resourcesProvider2);
            this.stickersTab = anonymousClass17;
            anonymousClass17.setDragEnabled(true);
            this.stickersTab.setWillNotDraw(false);
            this.stickersTab.setType(ScrollSlidingTabStrip.Type.TAB);
            this.stickersTab.setUnderlineHeight(AndroidUtilities.getShadowHeight());
            this.stickersTab.setIndicatorColor(getThemedColor("chat_emojiPanelStickerPackSelectorLine"));
            this.stickersTab.setUnderlineColor(getThemedColor("chat_emojiPanelShadowLine"));
            if (viewGroup != null) {
                FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.EmojiView.18
                    Paint paint = new Paint();

                    {
                        EmojiView.this = this;
                    }

                    @Override // android.view.ViewGroup, android.view.View
                    protected void dispatchDraw(Canvas canvas) {
                        float dp = AndroidUtilities.dp(50.0f) * EmojiView.this.delegate.getProgressToSearchOpened();
                        if (dp > getMeasuredHeight()) {
                            return;
                        }
                        canvas.save();
                        if (dp != 0.0f) {
                            canvas.clipRect(0.0f, dp, getMeasuredWidth(), getMeasuredHeight());
                        }
                        this.paint.setColor(EmojiView.this.getThemedColor("chat_emojiPanelBackground"));
                        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(36.0f) + EmojiView.this.stickersTab.getExpandedOffset(), this.paint);
                        super.dispatchDraw(canvas);
                        EmojiView.this.stickersTab.drawOverlays(canvas);
                        canvas.restore();
                    }

                    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                    protected void onLayout(boolean z5, int i4, int i5, int i6, int i7) {
                        super.onLayout(z5, i4, i5, i6, i7);
                        EmojiView.this.updateStickerTabsPosition();
                    }
                };
                this.stickersTabContainer = frameLayout;
                frameLayout.addView(this.stickersTab, LayoutHelper.createFrame(-1, 36, 51));
                viewGroup.addView(this.stickersTabContainer, LayoutHelper.createFrame(-1, -2.0f));
            } else {
                this.stickersContainer.addView(this.stickersTab, LayoutHelper.createFrame(-1, 36, 51));
            }
            updateStickerTabs(true);
            this.stickersTab.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda11
                @Override // org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate
                public final void onPageSelected(int i4) {
                    EmojiView.this.lambda$new$7(i4);
                }
            });
            this.stickersGridView.setOnScrollListener(new TypedScrollListener(0));
        } else {
            resourcesProvider2 = resourcesProvider;
        }
        this.currentTabs.clear();
        this.currentTabs.addAll(this.allTabs);
        ViewPager viewPager = new ViewPager(context) { // from class: org.telegram.ui.Components.EmojiView.19
            {
                EmojiView.this = this;
            }

            @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                }
                try {
                    return super.onInterceptTouchEvent(motionEvent);
                } catch (IllegalArgumentException unused) {
                    return false;
                }
            }

            @Override // androidx.viewpager.widget.ViewPager
            public void setCurrentItem(int i4, boolean z5) {
                EmojiView.this.startStopVisibleGifs(i4 == 1);
                if (i4 != getCurrentItem()) {
                    super.setCurrentItem(i4, z5);
                } else if (i4 != 0) {
                    if (i4 == 1) {
                        EmojiView.this.gifGridView.smoothScrollToPosition(1);
                    } else {
                        EmojiView.this.stickersGridView.smoothScrollToPosition(1);
                    }
                } else {
                    EmojiView.this.tabsMinusDy[1] = 0;
                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(EmojiView.this.emojiTabs, ViewGroup.TRANSLATION_Y, 0.0f);
                    ofFloat.setDuration(150L);
                    ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    ofFloat.start();
                    EmojiView.this.scrollEmojisToPosition(0, 0);
                    if (EmojiView.this.emojiTabs == null) {
                        return;
                    }
                    EmojiView.this.emojiTabs.select(0);
                }
            }
        };
        this.pager = viewPager;
        EmojiPagesAdapter emojiPagesAdapter = new EmojiPagesAdapter();
        this.emojiPagerAdapter = emojiPagesAdapter;
        viewPager.setAdapter(emojiPagesAdapter);
        ImageView imageView = new ImageView(context) { // from class: org.telegram.ui.Components.EmojiView.20
            {
                EmojiView.this = this;
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    EmojiView.this.backspacePressed = true;
                    EmojiView.this.backspaceOnce = false;
                    EmojiView.this.postBackspaceRunnable(350);
                } else if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                    EmojiView.this.backspacePressed = false;
                    if (!EmojiView.this.backspaceOnce && EmojiView.this.delegate != null && EmojiView.this.delegate.onBackspace()) {
                        EmojiView.this.backspaceButton.performHapticFeedback(3);
                    }
                }
                super.onTouchEvent(motionEvent);
                return true;
            }
        };
        this.backspaceButton = imageView;
        imageView.setHapticFeedbackEnabled(true);
        this.backspaceButton.setImageResource(R.drawable.smiles_tab_clear);
        this.backspaceButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
        this.backspaceButton.setScaleType(ImageView.ScaleType.CENTER);
        ImageView imageView2 = this.backspaceButton;
        int i4 = R.string.AccDescrBackspace;
        imageView2.setContentDescription(LocaleController.getString("AccDescrBackspace", i4));
        this.backspaceButton.setFocusable(true);
        this.backspaceButton.setOnClickListener(new View.OnClickListener(this) { // from class: org.telegram.ui.Components.EmojiView.21
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
            }
        });
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.bulletinContainer = frameLayout2;
        if (z4) {
            addView(frameLayout2, LayoutHelper.createFrame(-1, 100.0f, 87, 0.0f, 0.0f, 0.0f, (AndroidUtilities.getShadowHeight() / AndroidUtilities.density) + 40.0f));
        } else {
            addView(frameLayout2, LayoutHelper.createFrame(-1, 100.0f, 87, 0.0f, 0.0f, 0.0f, 0.0f));
        }
        this.bottomTabContainer = new FrameLayout(this, context) { // from class: org.telegram.ui.Components.EmojiView.22
            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        View view2 = new View(context);
        this.shadowLine = view2;
        view2.setBackgroundColor(getThemedColor("chat_emojiPanelShadowLine"));
        this.bottomTabContainer.addView(this.shadowLine, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight()));
        View view3 = new View(context);
        this.bottomTabContainerBackground = view3;
        this.bottomTabContainer.addView(view3, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(40.0f), 83));
        if (z4) {
            addView(this.bottomTabContainer, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(40.0f) + AndroidUtilities.getShadowHeight(), 83));
            this.bottomTabContainer.addView(this.backspaceButton, LayoutHelper.createFrame(47, 40, 85));
            if (i3 >= 21) {
                i = argb;
                this.backspaceButton.setBackground(Theme.createSelectorDrawable(i, 1, AndroidUtilities.dp(18.0f)));
            } else {
                i = argb;
            }
            ImageView imageView3 = new ImageView(context);
            this.stickerSettingsButton = imageView3;
            imageView3.setImageResource(R.drawable.smiles_tab_settings);
            this.stickerSettingsButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
            this.stickerSettingsButton.setScaleType(ImageView.ScaleType.CENTER);
            this.stickerSettingsButton.setFocusable(true);
            if (i3 >= 21) {
                this.stickerSettingsButton.setBackground(Theme.createSelectorDrawable(i, 1, AndroidUtilities.dp(18.0f)));
            }
            this.stickerSettingsButton.setContentDescription(LocaleController.getString("Settings", R.string.Settings));
            this.bottomTabContainer.addView(this.stickerSettingsButton, LayoutHelper.createFrame(47, 40, 85));
            this.stickerSettingsButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView.23
                {
                    EmojiView.this = this;
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View view4) {
                    if (EmojiView.this.delegate != null) {
                        EmojiView.this.delegate.onStickersSettingsClick();
                    }
                }
            });
            PagerSlidingTabStrip pagerSlidingTabStrip = new PagerSlidingTabStrip(context, resourcesProvider2);
            this.typeTabs = pagerSlidingTabStrip;
            pagerSlidingTabStrip.setViewPager(this.pager);
            this.typeTabs.setShouldExpand(false);
            this.typeTabs.setIndicatorHeight(0);
            this.typeTabs.setUnderlineHeight(0);
            this.typeTabs.setTabPaddingLeftRight(AndroidUtilities.dp(10.0f));
            this.bottomTabContainer.addView(this.typeTabs, LayoutHelper.createFrame(-2, 40, 81));
            this.typeTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.Components.EmojiView.24
                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrollStateChanged(int i5) {
                }

                {
                    EmojiView.this = this;
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrolled(int i5, float f, int i6) {
                    EmojiView.this.checkGridVisibility(i5, f);
                    EmojiView emojiView = EmojiView.this;
                    emojiView.onPageScrolled(i5, (emojiView.getMeasuredWidth() - EmojiView.this.getPaddingLeft()) - EmojiView.this.getPaddingRight(), i6);
                    boolean z5 = true;
                    EmojiView.this.showBottomTab(true, true);
                    int currentItem = EmojiView.this.pager.getCurrentItem();
                    SearchField searchField4 = currentItem == 0 ? EmojiView.this.emojiSearchField : currentItem == 1 ? EmojiView.this.gifSearchField : EmojiView.this.stickersSearchField;
                    String obj = searchField4.searchEditText.getText().toString();
                    int i7 = 0;
                    while (i7 < 3) {
                        SearchField searchField5 = i7 == 0 ? EmojiView.this.emojiSearchField : i7 == 1 ? EmojiView.this.gifSearchField : EmojiView.this.stickersSearchField;
                        if (searchField5 != null && searchField5 != searchField4 && searchField5.searchEditText != null && !searchField5.searchEditText.getText().toString().equals(obj)) {
                            searchField5.searchEditText.setText(obj);
                            searchField5.searchEditText.setSelection(obj.length());
                        }
                        i7++;
                    }
                    EmojiView emojiView2 = EmojiView.this;
                    if ((i5 != 0 || f <= 0.0f) && i5 != 1) {
                        z5 = false;
                    }
                    emojiView2.startStopVisibleGifs(z5);
                    EmojiView.this.updateStickerTabsPosition();
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageSelected(int i5) {
                    EmojiView.this.saveNewPage();
                    boolean z5 = false;
                    EmojiView.this.showBackspaceButton(i5 == 0, true);
                    EmojiView emojiView = EmojiView.this;
                    if (i5 == 2) {
                        z5 = true;
                    }
                    emojiView.showStickerSettingsButton(z5, true);
                    if (EmojiView.this.delegate.isSearchOpened()) {
                        if (i5 == 0) {
                            if (EmojiView.this.emojiSearchField == null) {
                                return;
                            }
                            EmojiView.this.emojiSearchField.searchEditText.requestFocus();
                        } else if (i5 == 1) {
                            if (EmojiView.this.gifSearchField == null) {
                                return;
                            }
                            EmojiView.this.gifSearchField.searchEditText.requestFocus();
                        } else if (EmojiView.this.stickersSearchField == null) {
                        } else {
                            EmojiView.this.stickersSearchField.searchEditText.requestFocus();
                        }
                    }
                }
            });
            ImageView imageView4 = new ImageView(context);
            this.searchButton = imageView4;
            imageView4.setImageResource(R.drawable.smiles_tab_search);
            this.searchButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
            this.searchButton.setScaleType(ImageView.ScaleType.CENTER);
            this.searchButton.setContentDescription(LocaleController.getString("Search", R.string.Search));
            this.searchButton.setFocusable(true);
            if (i3 >= 21) {
                this.searchButton.setBackground(Theme.createSelectorDrawable(i, 1, AndroidUtilities.dp(18.0f)));
            }
            this.bottomTabContainer.addView(this.searchButton, LayoutHelper.createFrame(47, 40, 83));
            this.searchButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView.25
                {
                    EmojiView.this = this;
                }

                @Override // android.view.View.OnClickListener
                public void onClick(View view4) {
                    int currentItem = EmojiView.this.pager.getCurrentItem();
                    SearchField searchField4 = currentItem == 0 ? EmojiView.this.emojiSearchField : currentItem == 1 ? EmojiView.this.gifSearchField : EmojiView.this.stickersSearchField;
                    if (searchField4 == null) {
                        return;
                    }
                    searchField4.searchEditText.requestFocus();
                    MotionEvent obtain = MotionEvent.obtain(0L, 0L, 0, 0.0f, 0.0f, 0);
                    searchField4.searchEditText.onTouchEvent(obtain);
                    obtain.recycle();
                    MotionEvent obtain2 = MotionEvent.obtain(0L, 0L, 1, 0.0f, 0.0f, 0);
                    searchField4.searchEditText.onTouchEvent(obtain2);
                    obtain2.recycle();
                }
            });
        } else {
            addView(this.bottomTabContainer, LayoutHelper.createFrame((i3 >= 21 ? 40 : 44) + 16, (i3 >= 21 ? 40 : 44) + 8, (LocaleController.isRTL ? 3 : 5) | 80, 0.0f, 0.0f, 2.0f, 0.0f));
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor("chat_emojiPanelBackground"), getThemedColor("chat_emojiPanelBackground"));
            if (i3 < 21) {
                Drawable mutate = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
                createSimpleSelectorCircleDrawable = combinedDrawable;
            } else {
                StateListAnimator stateListAnimator = new StateListAnimator();
                ImageView imageView5 = this.floatingButton;
                Property property = View.TRANSLATION_Z;
                stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(imageView5, property, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, property, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
                this.backspaceButton.setStateListAnimator(stateListAnimator);
                this.backspaceButton.setOutlineProvider(new ViewOutlineProvider(this) { // from class: org.telegram.ui.Components.EmojiView.26
                    @Override // android.view.ViewOutlineProvider
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view4, Outline outline) {
                        outline.setOval(0, 0, view4.getMeasuredWidth(), view4.getMeasuredHeight());
                    }
                });
            }
            this.backspaceButton.setPadding(0, 0, AndroidUtilities.dp(2.0f), 0);
            this.backspaceButton.setBackground(createSimpleSelectorCircleDrawable);
            this.backspaceButton.setContentDescription(LocaleController.getString("AccDescrBackspace", i4));
            this.backspaceButton.setFocusable(true);
            this.bottomTabContainer.addView(this.backspaceButton, LayoutHelper.createFrame((i3 >= 21 ? 40 : 44) - 4, (i3 >= 21 ? 40 : 44) - 4, 51, 10.0f, 0.0f, 10.0f, 0.0f));
            this.shadowLine.setVisibility(8);
            this.bottomTabContainerBackground.setVisibility(8);
        }
        addView(this.pager, 0, LayoutHelper.createFrame(-1, -1, 51));
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context);
        this.mediaBanTooltip = correctlyMeasuringTextView;
        correctlyMeasuringTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), getThemedColor("chat_gifSaveHintBackground")));
        this.mediaBanTooltip.setTextColor(getThemedColor("chat_gifSaveHintText"));
        this.mediaBanTooltip.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
        this.mediaBanTooltip.setGravity(16);
        this.mediaBanTooltip.setTextSize(1, 14.0f);
        this.mediaBanTooltip.setVisibility(4);
        addView(this.mediaBanTooltip, LayoutHelper.createFrame(-2, -2.0f, 81, 5.0f, 0.0f, 5.0f, 53.0f));
        this.emojiSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
        this.pickerView = new EmojiColorPickerView(context);
        EmojiColorPickerView emojiColorPickerView = this.pickerView;
        int dp = AndroidUtilities.dp(((AndroidUtilities.isTablet() ? 40 : 32) * 6) + 10 + 20);
        this.popupWidth = dp;
        int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 64.0f : 56.0f);
        this.popupHeight = dp2;
        EmojiPopupWindow emojiPopupWindow = new EmojiPopupWindow(this, emojiColorPickerView, dp, dp2);
        this.pickerViewPopup = emojiPopupWindow;
        emojiPopupWindow.setOutsideTouchable(true);
        this.pickerViewPopup.setClippingEnabled(true);
        this.pickerViewPopup.setInputMethodMode(2);
        this.pickerViewPopup.setSoftInputMode(0);
        this.pickerViewPopup.getContentView().setFocusableInTouchMode(true);
        this.pickerViewPopup.getContentView().setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnKeyListener
            public final boolean onKey(View view4, int i5, KeyEvent keyEvent) {
                boolean lambda$new$8;
                lambda$new$8 = EmojiView.this.lambda$new$8(view4, i5, keyEvent);
                return lambda$new$8;
            }
        });
        this.currentPage = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
        Emoji.loadRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
        setAllow(z2, z3, false);
    }

    public /* synthetic */ void lambda$new$1() {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null) {
            emojiViewDelegate.onEmojiSettingsClick(this.emojiAdapter.frozenEmojiPacks);
        }
    }

    public /* synthetic */ boolean lambda$new$2(Theme.ResourcesProvider resourcesProvider, View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.gifGridView, 0, this.gifOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider);
    }

    public /* synthetic */ void lambda$new$3(View view, int i) {
        if (this.delegate == null) {
            return;
        }
        int i2 = i - 1;
        RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
        GifAdapter gifAdapter = this.gifAdapter;
        if (adapter != gifAdapter) {
            RecyclerView.Adapter adapter2 = this.gifGridView.getAdapter();
            GifAdapter gifAdapter2 = this.gifSearchAdapter;
            if (adapter2 != gifAdapter2 || i2 < 0 || i2 >= gifAdapter2.results.size()) {
                return;
            }
            this.delegate.onGifSelected(view, this.gifSearchAdapter.results.get(i2), this.gifSearchAdapter.lastSearchImageString, this.gifSearchAdapter.bot, true, 0);
            updateRecentGifs();
        } else if (i2 < 0) {
        } else {
            if (i2 < gifAdapter.recentItemsCount) {
                this.delegate.onGifSelected(view, this.recentGifs.get(i2), null, "gif", true, 0);
                return;
            }
            if (this.gifAdapter.recentItemsCount > 0) {
                i2 = (i2 - this.gifAdapter.recentItemsCount) - 1;
            }
            if (i2 < 0 || i2 >= this.gifAdapter.results.size()) {
                return;
            }
            this.delegate.onGifSelected(view, this.gifAdapter.results.get(i2), null, this.gifAdapter.bot, true, 0);
        }
    }

    public /* synthetic */ void lambda$new$4(int i) {
        if (i != this.gifTrendingTabNum || !this.gifAdapter.results.isEmpty()) {
            this.gifGridView.stopScroll();
            this.gifTabs.onPageScrolled(i, 0);
            int i2 = 1;
            if (i == this.gifRecentTabNum || i == this.gifTrendingTabNum) {
                this.gifSearchField.searchEditText.setText("");
                if (i != this.gifTrendingTabNum || this.gifAdapter.trendingSectionItem < 1) {
                    GifLayoutManager gifLayoutManager = this.gifLayoutManager;
                    EmojiViewDelegate emojiViewDelegate = this.delegate;
                    if (emojiViewDelegate != null && emojiViewDelegate.isExpanded()) {
                        i2 = 0;
                    }
                    gifLayoutManager.scrollToPositionWithOffset(i2, 0);
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

    public /* synthetic */ boolean lambda$new$5(Theme.ResourcesProvider resourcesProvider, View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider);
    }

    public /* synthetic */ void lambda$new$6(View view, int i) {
        String str;
        RecyclerView.Adapter adapter = this.stickersGridView.getAdapter();
        StickersSearchGridAdapter stickersSearchGridAdapter = this.stickersSearchGridAdapter;
        if (adapter == stickersSearchGridAdapter) {
            String str2 = stickersSearchGridAdapter.searchQuery;
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) this.stickersSearchGridAdapter.positionsToSets.get(i);
            if (tLRPC$StickerSetCovered != null) {
                this.delegate.onShowStickerSet(tLRPC$StickerSetCovered.set, null);
                return;
            }
            str = str2;
        } else {
            str = null;
        }
        if (!(view instanceof StickerEmojiCell)) {
            return;
        }
        StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
        if (stickerEmojiCell.getSticker() != null && MessageObject.isPremiumSticker(stickerEmojiCell.getSticker()) && !AccountInstance.getInstance(this.currentAccount).getUserConfig().isPremium()) {
            ContentPreviewViewer.getInstance().showMenuFor(stickerEmojiCell);
            return;
        }
        ContentPreviewViewer.getInstance().reset();
        if (stickerEmojiCell.isDisabled()) {
            return;
        }
        stickerEmojiCell.disable();
        this.delegate.onStickerSelected(stickerEmojiCell, stickerEmojiCell.getSticker(), str, stickerEmojiCell.getParentObject(), stickerEmojiCell.getSendAnimationData(), true, 0);
    }

    /* renamed from: org.telegram.ui.Components.EmojiView$17 */
    /* loaded from: classes3.dex */
    public class AnonymousClass17 extends DraggableScrollSlidingTabStrip {
        public static /* synthetic */ void lambda$sendReorder$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass17(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            EmojiView.this = r1;
        }

        @Override // org.telegram.ui.Components.ScrollSlidingTabStrip
        protected void updatePosition() {
            EmojiView.this.updateStickerTabsPosition();
            EmojiView.this.stickersTabContainer.invalidate();
            invalidate();
            if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.invalidateEnterView();
            }
        }

        @Override // org.telegram.ui.Components.ScrollSlidingTabStrip
        protected void stickerSetPositionChanged(int i, int i2) {
            int i3 = i - EmojiView.this.stickersTabOffset;
            int i4 = i2 - EmojiView.this.stickersTabOffset;
            MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
            swapListElements(EmojiView.this.stickerSets, i3, i4);
            Collections.sort(mediaDataController.getStickerSets(0), new Comparator() { // from class: org.telegram.ui.Components.EmojiView$17$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$stickerSetPositionChanged$0;
                    lambda$stickerSetPositionChanged$0 = EmojiView.AnonymousClass17.this.lambda$stickerSetPositionChanged$0((TLRPC$TL_messages_stickerSet) obj, (TLRPC$TL_messages_stickerSet) obj2);
                    return lambda$stickerSetPositionChanged$0;
                }
            });
            EmojiView.this.reloadStickersAdapter();
            AndroidUtilities.cancelRunOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable);
            AndroidUtilities.runOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable, 1500L);
            sendReorder();
            EmojiView.this.updateStickerTabs(true);
        }

        public /* synthetic */ int lambda$stickerSetPositionChanged$0(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2) {
            int indexOf = EmojiView.this.stickerSets.indexOf(tLRPC$TL_messages_stickerSet);
            int indexOf2 = EmojiView.this.stickerSets.indexOf(tLRPC$TL_messages_stickerSet2);
            if (indexOf < 0 || indexOf2 < 0) {
                return 0;
            }
            return indexOf - indexOf2;
        }

        private void swapListElements(List<TLRPC$TL_messages_stickerSet> list, int i, int i2) {
            list.add(i2, list.remove(i));
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r2v2, types: [int] */
        /* JADX WARN: Type inference failed for: r2v8 */
        /* JADX WARN: Type inference failed for: r4v3, types: [java.util.ArrayList] */
        private void sendReorder() {
            MediaDataController.getInstance(EmojiView.this.currentAccount).calcNewHash(0);
            TLRPC$TL_messages_reorderStickerSets tLRPC$TL_messages_reorderStickerSets = new TLRPC$TL_messages_reorderStickerSets();
            tLRPC$TL_messages_reorderStickerSets.masks = false;
            tLRPC$TL_messages_reorderStickerSets.emojis = false;
            for (int i = EmojiView.this.hasChatStickers; i < EmojiView.this.stickerSets.size(); i++) {
                tLRPC$TL_messages_reorderStickerSets.order.add(Long.valueOf(((TLRPC$TL_messages_stickerSet) EmojiView.this.stickerSets.get(i)).set.id));
            }
            ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_messages_reorderStickerSets, EmojiView$17$$ExternalSyntheticLambda1.INSTANCE);
            NotificationCenter.getInstance(EmojiView.this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, 0, Boolean.TRUE);
        }

        @Override // org.telegram.ui.Components.ScrollSlidingTabStrip
        protected void invalidateOverlays() {
            EmojiView.this.stickersTabContainer.invalidate();
        }
    }

    public /* synthetic */ void lambda$new$7(int i) {
        if (this.firstTabUpdate) {
            return;
        }
        if (i == this.trendingTabNum) {
            openTrendingStickers(null);
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
            if (i5 >= this.stickerSets.size()) {
                return;
            }
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

    public /* synthetic */ boolean lambda$new$8(View view, int i, KeyEvent keyEvent) {
        EmojiPopupWindow emojiPopupWindow;
        if (i == 82 && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == 1 && (emojiPopupWindow = this.pickerViewPopup) != null && emojiPopupWindow.isShowing()) {
            this.pickerViewPopup.dismiss();
            return true;
        }
        return false;
    }

    /* loaded from: classes3.dex */
    public class EmojiGridView extends RecyclerListView {
        private boolean ignoreLayout;
        private int lastChildCount;
        ArrayList<DrawingInBackgroundLine> lineDrawables;
        ArrayList<DrawingInBackgroundLine> lineDrawablesTmp;
        private HashMap<Integer, TouchDownInfo> touches;
        ArrayList<ArrayList<ImageViewEmoji>> unusedArrays;
        ArrayList<DrawingInBackgroundLine> unusedLineDrawables;
        SparseArray<ArrayList<ImageViewEmoji>> viewsGroupedByLines;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiGridView(Context context) {
            super(context);
            EmojiView.this = r3;
            this.viewsGroupedByLines = new SparseArray<>();
            this.lineDrawables = new ArrayList<>();
            this.lineDrawablesTmp = new ArrayList<>();
            this.unusedArrays = new ArrayList<>();
            this.unusedLineDrawables = new ArrayList<>();
            this.lastChildCount = -1;
            new SparseIntArray();
            new AnimatedFloat(this, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
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

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
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

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            if (EmojiView.this.needEmojiSearch && EmojiView.this.firstEmojiAttach) {
                this.ignoreLayout = true;
                EmojiView.this.emojiLayoutManager.scrollToPositionWithOffset(1, 0);
                EmojiView.this.firstEmojiAttach = false;
                this.ignoreLayout = false;
            }
            super.onLayout(z, i, i2, i3, i4);
            EmojiView.this.checkEmojiSearchFieldScroll(true);
            updateEmojiDrawables();
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        /* JADX WARN: Removed duplicated region for block: B:94:0x006f  */
        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onTouchEvent(android.view.MotionEvent r12) {
            /*
                Method dump skipped, instructions count: 456
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiGridView.onTouchEvent(android.view.MotionEvent):boolean");
        }

        public void updateEmojiDrawables() {
            EmojiView.this.animatedEmojiDrawables = AnimatedEmojiSpan.update(2, this, getAnimatedEmojiSpans(), EmojiView.this.animatedEmojiDrawables);
        }

        @Override // androidx.recyclerview.widget.RecyclerView
        public void onScrollStateChanged(int i) {
            super.onScrollStateChanged(i);
            if (i == 0) {
                if (!canScrollVertically(-1) || !canScrollVertically(1)) {
                    EmojiView.this.showBottomTab(true, true);
                }
                if (canScrollVertically(1)) {
                    return;
                }
                EmojiView.this.checkTabsY(1, AndroidUtilities.dp(36.0f));
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (this.lastChildCount != getChildCount()) {
                updateEmojiDrawables();
                this.lastChildCount = getChildCount();
            }
            for (int i = 0; i < this.viewsGroupedByLines.size(); i++) {
                ArrayList<ImageViewEmoji> valueAt = this.viewsGroupedByLines.valueAt(i);
                valueAt.clear();
                this.unusedArrays.add(valueAt);
            }
            this.viewsGroupedByLines.clear();
            boolean z = ((EmojiView.this.animateExpandStartTime > 0L ? 1 : (EmojiView.this.animateExpandStartTime == 0L ? 0 : -1)) > 0 && ((SystemClock.elapsedRealtime() - EmojiView.this.animateExpandStartTime) > animateExpandDuration() ? 1 : ((SystemClock.elapsedRealtime() - EmojiView.this.animateExpandStartTime) == animateExpandDuration() ? 0 : -1)) < 0) && EmojiView.this.animateExpandFromButton != null && EmojiView.this.animateExpandFromPosition >= 0;
            if (EmojiView.this.animatedEmojiDrawables != null && EmojiView.this.emojiGridView != null) {
                for (int i2 = 0; i2 < EmojiView.this.emojiGridView.getChildCount(); i2++) {
                    View childAt = EmojiView.this.emojiGridView.getChildAt(i2);
                    if (childAt instanceof ImageViewEmoji) {
                        int top = childAt.getTop() + ((int) childAt.getTranslationY());
                        ArrayList<ImageViewEmoji> arrayList = this.viewsGroupedByLines.get(top);
                        if (arrayList == null) {
                            if (!this.unusedArrays.isEmpty()) {
                                ArrayList<ArrayList<ImageViewEmoji>> arrayList2 = this.unusedArrays;
                                arrayList = arrayList2.remove(arrayList2.size() - 1);
                            } else {
                                arrayList = new ArrayList<>();
                            }
                            this.viewsGroupedByLines.put(top, arrayList);
                        }
                        arrayList.add((ImageViewEmoji) childAt);
                    }
                    if (z && childAt != null && getChildAdapterPosition(childAt) == EmojiView.this.animateExpandFromPosition - 1) {
                        float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(MathUtils.clamp(((float) (SystemClock.elapsedRealtime() - EmojiView.this.animateExpandStartTime)) / 140.0f, 0.0f, 1.0f));
                        if (interpolation < 1.0f) {
                            float f = 1.0f - interpolation;
                            canvas.saveLayerAlpha(childAt.getLeft(), childAt.getTop(), childAt.getRight(), childAt.getBottom(), (int) (255.0f * f), 31);
                            canvas.translate(childAt.getLeft(), childAt.getTop());
                            float f2 = (f * 0.5f) + 0.5f;
                            canvas.scale(f2, f2, childAt.getWidth() / 2.0f, childAt.getHeight() / 2.0f);
                            EmojiView.this.animateExpandFromButton.draw(canvas);
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
                DrawingInBackgroundLine drawingInBackgroundLine = null;
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
                        drawingInBackgroundLine = this.lineDrawablesTmp.get(i5);
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
                canvas.translate(imageViewEmoji.getLeft(), imageViewEmoji.getY() + imageViewEmoji.getPaddingTop());
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

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateEmojiDrawables();
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            AnimatedEmojiSpan.release(this, EmojiView.this.animatedEmojiDrawables);
            for (int i = 0; i < this.lineDrawables.size(); i++) {
                this.lineDrawables.get(i).onDetachFromWindow();
            }
            for (int i2 = 0; i2 < this.unusedLineDrawables.size(); i2++) {
                this.unusedLineDrawables.get(i2).onDetachFromWindow();
            }
            this.unusedLineDrawables.addAll(this.lineDrawables);
            this.lineDrawables.clear();
        }

        /* loaded from: classes3.dex */
        public class TouchDownInfo {
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
                for (Map.Entry<Integer, TouchDownInfo> entry : hashMap.entrySet()) {
                    if (entry != null && entry.getValue().view == view && (remove = this.touches.remove(entry.getKey())) != null) {
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
            return Math.max(600L, Math.min(55, EmojiView.this.animateExpandToPosition - EmojiView.this.animateExpandFromPosition) * 40);
        }

        public long animateExpandCrossfadeDuration() {
            return Math.max(400L, Math.min(45, EmojiView.this.animateExpandToPosition - EmojiView.this.animateExpandFromPosition) * 35);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes3.dex */
        public class DrawingInBackgroundLine extends DrawingInBackgroundThreadDrawable {
            ArrayList<ImageViewEmoji> imageViewEmojis;
            public int position;
            public int startOffset;
            ArrayList<ImageViewEmoji> drawInBackgroundViews = new ArrayList<>();
            private OvershootInterpolator appearScaleInterpolator = new OvershootInterpolator(3.0f);

            DrawingInBackgroundLine() {
                EmojiGridView.this = r2;
            }

            /* JADX WARN: Code restructure failed: missing block: B:80:0x007e, code lost:
                prepareDraw(java.lang.System.currentTimeMillis());
                drawInUiThread(r9, r14);
                reset();
             */
            /* JADX WARN: Code restructure failed: missing block: B:91:?, code lost:
                return;
             */
            @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void draw(android.graphics.Canvas r9, long r10, int r12, int r13, float r14) {
                /*
                    r8 = this;
                    java.util.ArrayList<org.telegram.ui.Components.EmojiView$ImageViewEmoji> r0 = r8.imageViewEmojis
                    if (r0 != 0) goto L5
                    return
                L5:
                    int r0 = r0.size()
                    r1 = 4
                    r2 = 0
                    r3 = 1
                    if (r0 <= r1) goto L17
                    int r0 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
                    if (r0 != 0) goto L15
                    goto L17
                L15:
                    r0 = 0
                    goto L18
                L17:
                    r0 = 1
                L18:
                    if (r0 != 0) goto L7b
                    org.telegram.ui.Components.EmojiView$EmojiGridView r1 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                    long r4 = org.telegram.ui.Components.EmojiView.access$11900(r1)
                    r6 = 0
                    int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                    if (r1 <= 0) goto L41
                    long r4 = android.os.SystemClock.elapsedRealtime()
                    org.telegram.ui.Components.EmojiView$EmojiGridView r1 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                    long r6 = org.telegram.ui.Components.EmojiView.access$11900(r1)
                    long r4 = r4 - r6
                    org.telegram.ui.Components.EmojiView$EmojiGridView r1 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    long r6 = r1.animateExpandDuration()
                    int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                    if (r1 >= 0) goto L41
                    r1 = 1
                    goto L42
                L41:
                    r1 = 0
                L42:
                    java.util.ArrayList<org.telegram.ui.Components.EmojiView$ImageViewEmoji> r4 = r8.imageViewEmojis
                    int r4 = r4.size()
                    if (r2 >= r4) goto L7b
                    java.util.ArrayList<org.telegram.ui.Components.EmojiView$ImageViewEmoji> r4 = r8.imageViewEmojis
                    java.lang.Object r4 = r4.get(r2)
                    org.telegram.ui.Components.EmojiView$ImageViewEmoji r4 = (org.telegram.ui.Components.EmojiView.ImageViewEmoji) r4
                    float r5 = r4.pressedProgress
                    r6 = 0
                    int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
                    if (r5 != 0) goto L7c
                    android.animation.ValueAnimator r5 = r4.backAnimator
                    if (r5 != 0) goto L7c
                    int r5 = r4.position
                    org.telegram.ui.Components.EmojiView$EmojiGridView r6 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                    int r6 = org.telegram.ui.Components.EmojiView.access$12100(r6)
                    if (r5 <= r6) goto L78
                    int r4 = r4.position
                    org.telegram.ui.Components.EmojiView$EmojiGridView r5 = org.telegram.ui.Components.EmojiView.EmojiGridView.this
                    org.telegram.ui.Components.EmojiView r5 = org.telegram.ui.Components.EmojiView.this
                    int r5 = org.telegram.ui.Components.EmojiView.access$12200(r5)
                    if (r4 >= r5) goto L78
                    if (r1 == 0) goto L78
                    goto L7c
                L78:
                    int r2 = r2 + 1
                    goto L42
                L7b:
                    r3 = r0
                L7c:
                    if (r3 == 0) goto L8c
                    long r10 = java.lang.System.currentTimeMillis()
                    r8.prepareDraw(r10)
                    r8.drawInUiThread(r9, r14)
                    r8.reset()
                    goto L8f
                L8c:
                    super.draw(r9, r10, r12, r13, r14)
                L8f:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiGridView.DrawingInBackgroundLine.draw(android.graphics.Canvas, long, int, int, float):void");
            }

            @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
            public void prepareDraw(long j) {
                AnimatedEmojiDrawable animatedEmojiDrawable;
                this.drawInBackgroundViews.clear();
                for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                    ImageViewEmoji imageViewEmoji = this.imageViewEmojis.get(i);
                    if (imageViewEmoji.getSpan() != null && (animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiView.this.animatedEmojiDrawables.get(imageViewEmoji.span.getDocumentId())) != null && animatedEmojiDrawable.getImageReceiver() != null) {
                        animatedEmojiDrawable.update(j);
                        imageViewEmoji.backgroundThreadDrawHolder = animatedEmojiDrawable.getImageReceiver().setDrawInBackgroundThread(imageViewEmoji.backgroundThreadDrawHolder);
                        imageViewEmoji.backgroundThreadDrawHolder.time = j;
                        imageViewEmoji.backgroundThreadDrawHolder.overrideAlpha = 1.0f;
                        animatedEmojiDrawable.setAlpha(255);
                        int height = (int) (imageViewEmoji.getHeight() * 0.03f);
                        android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                        rect.set((imageViewEmoji.getLeft() + imageViewEmoji.getPaddingLeft()) - this.startOffset, height, (imageViewEmoji.getRight() - imageViewEmoji.getPaddingRight()) - this.startOffset, ((imageViewEmoji.getMeasuredHeight() + height) - imageViewEmoji.getPaddingTop()) - imageViewEmoji.getPaddingBottom());
                        imageViewEmoji.backgroundThreadDrawHolder.setBounds(rect);
                        imageViewEmoji.drawable = animatedEmojiDrawable;
                        animatedEmojiDrawable.getImageReceiver();
                        this.drawInBackgroundViews.add(imageViewEmoji);
                    }
                }
            }

            @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
            public void drawInBackground(Canvas canvas) {
                for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                    ImageViewEmoji imageViewEmoji = this.drawInBackgroundViews.get(i);
                    AnimatedEmojiDrawable animatedEmojiDrawable = imageViewEmoji.drawable;
                    if (animatedEmojiDrawable != null) {
                        animatedEmojiDrawable.draw(canvas, imageViewEmoji.backgroundThreadDrawHolder, false);
                    }
                }
            }

            /* JADX WARN: Removed duplicated region for block: B:84:0x017e  */
            /* JADX WARN: Removed duplicated region for block: B:85:0x0196  */
            @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            protected void drawInUiThread(android.graphics.Canvas r21, float r22) {
                /*
                    Method dump skipped, instructions count: 419
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiGridView.DrawingInBackgroundLine.drawInUiThread(android.graphics.Canvas, float):void");
            }

            @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
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

        @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
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
                    if (findChildViewUnder != null && remove != null && Math.sqrt(Math.pow(x - remove.x, 2.0d) + Math.pow(y - remove.y, 2.0d)) < AndroidUtilities.touchSlop * 3.0f && !z3 && (!EmojiView.this.pickerViewPopup.isShowing() || SystemClock.elapsedRealtime() - remove.time < ViewConfiguration.getLongPressTimeout())) {
                        View view3 = remove.view;
                        try {
                            if (view3 instanceof ImageViewEmoji) {
                                ((ImageViewEmoji) view3).sendEmoji(null);
                                performHapticFeedback(3, 1);
                            } else if (view3 instanceof EmojiPackExpand) {
                                EmojiPackExpand emojiPackExpand = (EmojiPackExpand) view3;
                                EmojiView.this.emojiAdapter.expand(getChildAdapterPosition(emojiPackExpand), emojiPackExpand);
                                performHapticFeedback(3, 1);
                            } else if (view3 != null) {
                                view3.callOnClick();
                            }
                        } catch (Exception unused) {
                        }
                    }
                    if (remove != null && (view2 = remove.view) != null && Build.VERSION.SDK_INT >= 21 && (view2.getBackground() instanceof RippleDrawable)) {
                        remove.view.getBackground().setState(new int[0]);
                    }
                    if (remove != null && (view = remove.view) != null) {
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
            return super.dispatchTouchEvent(motionEvent) || (!z3 && !this.touches.isEmpty());
        }
    }

    public void createStickersChooseActionTracker() {
        ChooseStickerActionTracker chooseStickerActionTracker = new ChooseStickerActionTracker(this.currentAccount, this.delegate.getDialogId(), this.delegate.getThreadId()) { // from class: org.telegram.ui.Components.EmojiView.27
            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.EmojiView.ChooseStickerActionTracker
            public boolean isShown() {
                return EmojiView.this.delegate != null && EmojiView.this.getVisibility() == 0 && EmojiView.this.stickersContainerAttached;
            }
        };
        this.chooseStickerActionTracker = chooseStickerActionTracker;
        chooseStickerActionTracker.checkVisibility();
    }

    public void updateEmojiTabsPosition() {
        updateEmojiTabsPosition(this.emojiLayoutManager.findFirstCompletelyVisibleItemPosition());
    }

    public void updateEmojiTabsPosition(int i) {
        if (!this.emojiSmoothScrolling) {
            int i2 = -1;
            if (i == -1) {
                return;
            }
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
                            while (i3 < emojipacks.size()) {
                                long j = emojipacks.get(i3).set.id;
                                long j2 = emojiPack.set.id;
                                if (j == j2 && (!emojiPack.featured || (!emojiPack.installed && !this.installedEmojiSets.contains(Long.valueOf(j2))))) {
                                    i3 = EmojiData.dataColored.length + 1 + i3;
                                    break;
                                }
                                i3++;
                            }
                        } else {
                            size2--;
                        }
                    }
                }
                i3 = i2;
            }
            if (i3 < 0) {
                return;
            }
            this.emojiTabs.select(i3);
        }
    }

    public void checkGridVisibility(int i, float f) {
        if (this.stickersContainer == null || this.gifContainer == null) {
            return;
        }
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
        } else if (i != 1) {
            if (i != 2) {
                return;
            }
            this.emojiGridView.setVisibility(8);
            this.gifGridView.setVisibility(8);
            this.gifTabs.setVisibility(8);
            this.stickersGridView.setVisibility(0);
            this.stickersTabContainer.setVisibility(0);
        } else {
            this.emojiGridView.setVisibility(8);
            this.gifGridView.setVisibility(0);
            this.gifTabs.setVisibility(0);
            this.stickersGridView.setVisibility(f == 0.0f ? 8 : 0);
            FrameLayout frameLayout = this.stickersTabContainer;
            if (f == 0.0f) {
                i2 = 8;
            }
            frameLayout.setVisibility(i2);
        }
    }

    public void openPremiumAnimatedEmojiFeature() {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null) {
            emojiViewDelegate.onAnimatedEmojiUnlockClick();
        }
    }

    /* loaded from: classes3.dex */
    private class EmojiPackButton extends FrameLayout {
        AnimatedTextView addButtonTextView;
        FrameLayout addButtonView;
        PremiumButtonView premiumButtonView;

        public EmojiPackButton(EmojiView emojiView, Context context) {
            super(context);
            AnimatedTextView animatedTextView = new AnimatedTextView(getContext());
            this.addButtonTextView = animatedTextView;
            animatedTextView.setAnimationProperties(0.3f, 0L, 250L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.addButtonTextView.setTextSize(AndroidUtilities.dp(14.0f));
            this.addButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.addButtonTextView.setTextColor(emojiView.getThemedColor("featuredStickers_buttonText"));
            this.addButtonTextView.setGravity(17);
            FrameLayout frameLayout = new FrameLayout(getContext());
            this.addButtonView = frameLayout;
            frameLayout.setBackground(Theme.AdaptiveRipple.filledRect(emojiView.getThemedColor("featuredStickers_addButton"), 8.0f));
            this.addButtonView.addView(this.addButtonTextView, LayoutHelper.createFrame(-1, -2, 17));
            addView(this.addButtonView, LayoutHelper.createFrame(-1, -1.0f));
            PremiumButtonView premiumButtonView = new PremiumButtonView(getContext(), false);
            this.premiumButtonView = premiumButtonView;
            premiumButtonView.setIcon(R.raw.unlock_icon);
            addView(this.premiumButtonView, LayoutHelper.createFrame(-1, -1.0f));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(11.0f));
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f) + getPaddingTop() + getPaddingBottom(), NUM));
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiPackHeader extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
        TextView addButtonView;
        FrameLayout buttonsView;
        private int currentButtonState;
        boolean divider;
        private Paint dividerPaint;
        TextView headerView;
        RLottieImageView lockView;
        private EmojiPack pack;
        PremiumButtonView premiumButtonView;
        TextView removeButtonView;
        private AnimatorSet stateAnimator;
        private TLRPC$InputStickerSet toInstall;
        private TLRPC$InputStickerSet toUninstall;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EmojiPackHeader(Context context) {
            super(context);
            EmojiView.this = r17;
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.lockView = rLottieImageView;
            int i = R.raw.unlock_icon;
            rLottieImageView.setAnimation(i, 24, 24);
            this.lockView.setColorFilter(r17.getThemedColor("chat_emojiPanelStickerSetName"));
            addView(this.lockView, LayoutHelper.createFrameRelatively(20.0f, 20.0f, 8388611, 10.0f, 15.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.headerView = textView;
            textView.setTextSize(1, 15.0f);
            this.headerView.setTextColor(r17.getThemedColor("chat_emojiPanelStickerSetName"));
            this.headerView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.headerView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    EmojiView.EmojiPackHeader.this.lambda$new$0(view);
                }
            });
            addView(this.headerView, LayoutHelper.createFrameRelatively(-2.0f, -1.0f, 8388611, 15.0f, 15.0f, 52.0f, 0.0f));
            FrameLayout frameLayout = new FrameLayout(context);
            this.buttonsView = frameLayout;
            frameLayout.setPadding(AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), 0);
            this.buttonsView.setClipToPadding(false);
            this.buttonsView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    EmojiView.EmojiPackHeader.this.lambda$new$1(view);
                }
            });
            addView(this.buttonsView, LayoutHelper.createFrameRelatively(-2.0f, -1.0f, 8388725));
            TextView textView2 = new TextView(context);
            this.addButtonView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.addButtonView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.addButtonView.setText(LocaleController.getString("Add", R.string.Add));
            this.addButtonView.setTextColor(r17.getThemedColor("featuredStickers_buttonText"));
            this.addButtonView.setBackground(Theme.AdaptiveRipple.createRect(r17.getThemedColor("featuredStickers_addButton"), r17.getThemedColor("featuredStickers_addButtonPressed"), 16.0f));
            this.addButtonView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
            this.addButtonView.setGravity(17);
            this.addButtonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    EmojiView.EmojiPackHeader.this.lambda$new$2(view);
                }
            });
            this.buttonsView.addView(this.addButtonView, LayoutHelper.createFrameRelatively(-2.0f, 26.0f, 8388661));
            TextView textView3 = new TextView(context);
            this.removeButtonView = textView3;
            textView3.setTextSize(1, 14.0f);
            this.removeButtonView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.removeButtonView.setText(LocaleController.getString("StickersRemove", R.string.StickersRemove));
            this.removeButtonView.setTextColor(r17.getThemedColor("featuredStickers_removeButtonText"));
            this.removeButtonView.setBackground(Theme.AdaptiveRipple.createRect(0, r17.getThemedColor("featuredStickers_addButton") & NUM, 16.0f));
            this.removeButtonView.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
            this.removeButtonView.setGravity(17);
            this.removeButtonView.setTranslationX(AndroidUtilities.dp(4.0f));
            this.removeButtonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    EmojiView.EmojiPackHeader.this.lambda$new$3(view);
                }
            });
            this.buttonsView.addView(this.removeButtonView, LayoutHelper.createFrameRelatively(-2.0f, 26.0f, 8388661));
            PremiumButtonView premiumButtonView = new PremiumButtonView(context, AndroidUtilities.dp(16.0f), false);
            this.premiumButtonView = premiumButtonView;
            premiumButtonView.setIcon(i);
            this.premiumButtonView.setButton(LocaleController.getString("Unlock", R.string.Unlock), new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    EmojiView.EmojiPackHeader.this.lambda$new$4(view);
                }
            });
            try {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.premiumButtonView.getIconView().getLayoutParams();
                marginLayoutParams.leftMargin = AndroidUtilities.dp(1.0f);
                marginLayoutParams.topMargin = AndroidUtilities.dp(1.0f);
                int dp = AndroidUtilities.dp(20.0f);
                marginLayoutParams.height = dp;
                marginLayoutParams.width = dp;
                ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) this.premiumButtonView.getTextView().getLayoutParams();
                marginLayoutParams2.leftMargin = AndroidUtilities.dp(5.0f);
                marginLayoutParams2.topMargin = AndroidUtilities.dp(-0.5f);
                this.premiumButtonView.getChildAt(0).setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
            } catch (Exception unused) {
            }
            this.buttonsView.addView(this.premiumButtonView, LayoutHelper.createFrameRelatively(-2.0f, 26.0f, 8388661));
            setWillNotDraw(false);
        }

        public /* synthetic */ void lambda$new$0(View view) {
            TLRPC$StickerSet tLRPC$StickerSet;
            EmojiPack emojiPack = this.pack;
            if (emojiPack == null || (tLRPC$StickerSet = emojiPack.set) == null) {
                return;
            }
            EmojiView.this.openEmojiPackAlert(tLRPC$StickerSet);
        }

        public /* synthetic */ void lambda$new$1(View view) {
            TextView textView = this.addButtonView;
            if (textView != null && textView.getVisibility() == 0 && this.addButtonView.isEnabled()) {
                this.addButtonView.performClick();
                return;
            }
            TextView textView2 = this.removeButtonView;
            if (textView2 != null && textView2.getVisibility() == 0 && this.removeButtonView.isEnabled()) {
                this.removeButtonView.performClick();
                return;
            }
            PremiumButtonView premiumButtonView = this.premiumButtonView;
            if (premiumButtonView == null || premiumButtonView.getVisibility() != 0 || !this.premiumButtonView.isEnabled()) {
                return;
            }
            this.premiumButtonView.performClick();
        }

        public /* synthetic */ void lambda$new$2(View view) {
            TLRPC$StickerSet tLRPC$StickerSet;
            Integer num;
            View view2;
            View childAt;
            int childAdapterPosition;
            int i;
            EmojiPack emojiPack = this.pack;
            if (emojiPack == null || (tLRPC$StickerSet = emojiPack.set) == null) {
                return;
            }
            emojiPack.installed = true;
            if (!EmojiView.this.installedEmojiSets.contains(Long.valueOf(tLRPC$StickerSet.id))) {
                EmojiView.this.installedEmojiSets.add(Long.valueOf(this.pack.set.id));
            }
            updateState(true);
            int i2 = 0;
            while (true) {
                num = null;
                if (i2 >= EmojiView.this.emojiGridView.getChildCount()) {
                    view2 = null;
                    break;
                } else if ((EmojiView.this.emojiGridView.getChildAt(i2) instanceof EmojiPackExpand) && (childAdapterPosition = EmojiView.this.emojiGridView.getChildAdapterPosition((childAt = EmojiView.this.emojiGridView.getChildAt(i2)))) >= 0 && (i = EmojiView.this.emojiAdapter.positionToExpand.get(childAdapterPosition)) >= 0 && i < EmojiView.this.emojipacksProcessed.size() && EmojiView.this.emojipacksProcessed.get(i) != null && this.pack != null && ((EmojiPack) EmojiView.this.emojipacksProcessed.get(i)).set.id == this.pack.set.id) {
                    num = Integer.valueOf(childAdapterPosition);
                    view2 = childAt;
                    break;
                } else {
                    i2++;
                }
            }
            if (num != null) {
                EmojiView.this.emojiAdapter.expand(num.intValue(), view2);
            }
            if (this.toInstall != null) {
                return;
            }
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
            TLRPC$StickerSet tLRPC$StickerSet2 = this.pack.set;
            tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet2.id;
            tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet2.access_hash;
            TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSet(tLRPC$TL_inputStickerSetID, true);
            if (stickerSet == null || stickerSet.set == null) {
                NotificationCenter.getInstance(EmojiView.this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
                MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
                this.toInstall = tLRPC$TL_inputStickerSetID;
                mediaDataController.getStickerSet(tLRPC$TL_inputStickerSetID, false);
                return;
            }
            install(stickerSet);
        }

        public /* synthetic */ void lambda$new$3(View view) {
            TLRPC$StickerSet tLRPC$StickerSet;
            EmojiPack emojiPack = this.pack;
            if (emojiPack == null || (tLRPC$StickerSet = emojiPack.set) == null) {
                return;
            }
            emojiPack.installed = false;
            EmojiView.this.installedEmojiSets.remove(Long.valueOf(tLRPC$StickerSet.id));
            updateState(true);
            if (EmojiView.this.emojiTabs != null) {
                EmojiView.this.emojiTabs.updateEmojiPacks(EmojiView.this.getEmojipacks());
            }
            EmojiView.this.updateEmojiTabsPosition();
            if (this.toUninstall != null) {
                return;
            }
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
            TLRPC$StickerSet tLRPC$StickerSet2 = this.pack.set;
            tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet2.id;
            tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet2.access_hash;
            TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSet(tLRPC$TL_inputStickerSetID, true);
            if (stickerSet == null || stickerSet.set == null) {
                NotificationCenter.getInstance(EmojiView.this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
                MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
                this.toUninstall = tLRPC$TL_inputStickerSetID;
                mediaDataController.getStickerSet(tLRPC$TL_inputStickerSetID, false);
                return;
            }
            uninstall(stickerSet);
        }

        public /* synthetic */ void lambda$new$4(View view) {
            EmojiView.this.openPremiumAnimatedEmojiFeature();
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            ((ViewGroup.MarginLayoutParams) this.headerView.getLayoutParams()).topMargin = AndroidUtilities.dp(this.currentButtonState == 0 ? 10.0f : 15.0f);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.currentButtonState == 0 ? 32.0f : 42.0f), NUM));
        }

        public void setStickerSet(EmojiPack emojiPack, boolean z) {
            if (emojiPack == null) {
                return;
            }
            this.pack = emojiPack;
            this.divider = z;
            this.headerView.setText(emojiPack.set.title);
            if (emojiPack.installed && !emojiPack.set.official) {
                this.premiumButtonView.setButton(LocaleController.getString("Restore", R.string.Restore), new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda5
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        EmojiView.EmojiPackHeader.this.lambda$setStickerSet$5(view);
                    }
                });
            } else {
                this.premiumButtonView.setButton(LocaleController.getString("Unlock", R.string.Unlock), new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        EmojiView.EmojiPackHeader.this.lambda$setStickerSet$6(view);
                    }
                });
            }
            updateState(false);
        }

        public /* synthetic */ void lambda$setStickerSet$5(View view) {
            EmojiView.this.openPremiumAnimatedEmojiFeature();
        }

        public /* synthetic */ void lambda$setStickerSet$6(View view) {
            EmojiView.this.openPremiumAnimatedEmojiFeature();
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            TLRPC$TL_messages_stickerSet stickerSetById;
            TLRPC$TL_messages_stickerSet stickerSetById2;
            if (i == NotificationCenter.groupStickersDidLoad) {
                if (this.toInstall != null && (stickerSetById2 = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSetById(this.toInstall.id)) != null && stickerSetById2.set != null) {
                    install(stickerSetById2);
                    this.toInstall = null;
                }
                if (this.toUninstall == null || (stickerSetById = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSetById(this.toUninstall.id)) == null || stickerSetById.set == null) {
                    return;
                }
                uninstall(stickerSetById);
                this.toUninstall = null;
            }
        }

        private BaseFragment getFragment() {
            if (EmojiView.this.fragment != null) {
                return EmojiView.this.fragment;
            }
            return new BaseFragment() { // from class: org.telegram.ui.Components.EmojiView.EmojiPackHeader.1
                {
                    EmojiPackHeader.this = this;
                }

                @Override // org.telegram.ui.ActionBar.BaseFragment
                public int getCurrentAccount() {
                    return EmojiView.this.currentAccount;
                }

                @Override // org.telegram.ui.ActionBar.BaseFragment
                public View getFragmentView() {
                    return EmojiView.this.bulletinContainer;
                }

                @Override // org.telegram.ui.ActionBar.BaseFragment
                public FrameLayout getLayoutContainer() {
                    return EmojiView.this.bulletinContainer;
                }

                @Override // org.telegram.ui.ActionBar.BaseFragment
                public Theme.ResourcesProvider getResourceProvider() {
                    return EmojiView.this.resourcesProvider;
                }
            };
        }

        private void install(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
            EmojiPacksAlert.installSet(getFragment(), tLRPC$TL_messages_stickerSet, true, null, new Runnable() { // from class: org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.EmojiPackHeader.this.lambda$install$7();
                }
            });
        }

        public /* synthetic */ void lambda$install$7() {
            this.pack.installed = true;
            updateState(true);
        }

        private void uninstall(final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
            EmojiPacksAlert.uninstallSet(getFragment(), tLRPC$TL_messages_stickerSet, true, new Runnable() { // from class: org.telegram.ui.Components.EmojiView$EmojiPackHeader$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.EmojiPackHeader.this.lambda$uninstall$8(tLRPC$TL_messages_stickerSet);
                }
            });
        }

        public /* synthetic */ void lambda$uninstall$8(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
            this.pack.installed = true;
            if (!EmojiView.this.installedEmojiSets.contains(Long.valueOf(tLRPC$TL_messages_stickerSet.set.id))) {
                EmojiView.this.installedEmojiSets.add(Long.valueOf(tLRPC$TL_messages_stickerSet.set.id));
            }
            updateState(true);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(EmojiView.this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.divider) {
                if (this.dividerPaint == null) {
                    Paint paint = new Paint(1);
                    this.dividerPaint = paint;
                    paint.setStrokeWidth(1.0f);
                    this.dividerPaint.setColor(EmojiView.this.getThemedColor("divider"));
                }
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), 1.0f, this.dividerPaint);
            }
            super.onDraw(canvas);
        }

        public void updateState(boolean z) {
            EmojiPack emojiPack = this.pack;
            if (emojiPack == null) {
                return;
            }
            int i = 1;
            boolean z2 = emojiPack.installed || EmojiView.this.installedEmojiSets.contains(Long.valueOf(emojiPack.set.id));
            if (this.pack.free || UserConfig.getInstance(EmojiView.this.currentAccount).isPremium()) {
                i = this.pack.featured ? z2 ? 3 : 2 : 0;
            }
            updateState(i, z);
        }

        public void updateState(final int i, boolean z) {
            int i2 = 0;
            if ((i == 0) != (this.currentButtonState == 0)) {
                requestLayout();
            }
            this.currentButtonState = i;
            AnimatorSet animatorSet = this.stateAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.stateAnimator = null;
            }
            this.premiumButtonView.setEnabled(i == 1);
            this.addButtonView.setEnabled(i == 2);
            this.removeButtonView.setEnabled(i == 3);
            float f = 0.0f;
            if (z) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.stateAnimator = animatorSet2;
                Animator[] animatorArr = new Animator[12];
                RLottieImageView rLottieImageView = this.lockView;
                Property property = FrameLayout.TRANSLATION_X;
                float[] fArr = new float[1];
                fArr[0] = i == 1 ? 0.0f : -AndroidUtilities.dp(16.0f);
                animatorArr[0] = ObjectAnimator.ofFloat(rLottieImageView, property, fArr);
                RLottieImageView rLottieImageView2 = this.lockView;
                Property property2 = FrameLayout.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = i == 1 ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(rLottieImageView2, property2, fArr2);
                TextView textView = this.headerView;
                Property property3 = FrameLayout.TRANSLATION_X;
                float[] fArr3 = new float[1];
                fArr3[0] = i == 1 ? AndroidUtilities.dp(16.0f) : 0.0f;
                animatorArr[2] = ObjectAnimator.ofFloat(textView, property3, fArr3);
                PremiumButtonView premiumButtonView = this.premiumButtonView;
                Property property4 = FrameLayout.ALPHA;
                float[] fArr4 = new float[1];
                fArr4[0] = i == 1 ? 1.0f : 0.0f;
                animatorArr[3] = ObjectAnimator.ofFloat(premiumButtonView, property4, fArr4);
                PremiumButtonView premiumButtonView2 = this.premiumButtonView;
                Property property5 = FrameLayout.SCALE_X;
                float[] fArr5 = new float[1];
                fArr5[0] = i == 1 ? 1.0f : 0.6f;
                animatorArr[4] = ObjectAnimator.ofFloat(premiumButtonView2, property5, fArr5);
                PremiumButtonView premiumButtonView3 = this.premiumButtonView;
                Property property6 = FrameLayout.SCALE_Y;
                float[] fArr6 = new float[1];
                fArr6[0] = i == 1 ? 1.0f : 0.6f;
                animatorArr[5] = ObjectAnimator.ofFloat(premiumButtonView3, property6, fArr6);
                TextView textView2 = this.addButtonView;
                Property property7 = FrameLayout.ALPHA;
                float[] fArr7 = new float[1];
                fArr7[0] = i == 2 ? 1.0f : 0.0f;
                animatorArr[6] = ObjectAnimator.ofFloat(textView2, property7, fArr7);
                TextView textView3 = this.addButtonView;
                Property property8 = FrameLayout.SCALE_X;
                float[] fArr8 = new float[1];
                fArr8[0] = i == 2 ? 1.0f : 0.6f;
                animatorArr[7] = ObjectAnimator.ofFloat(textView3, property8, fArr8);
                TextView textView4 = this.addButtonView;
                Property property9 = FrameLayout.SCALE_Y;
                float[] fArr9 = new float[1];
                fArr9[0] = i == 2 ? 1.0f : 0.6f;
                animatorArr[8] = ObjectAnimator.ofFloat(textView4, property9, fArr9);
                TextView textView5 = this.removeButtonView;
                Property property10 = FrameLayout.ALPHA;
                float[] fArr10 = new float[1];
                if (i == 3) {
                    f = 1.0f;
                }
                fArr10[0] = f;
                animatorArr[9] = ObjectAnimator.ofFloat(textView5, property10, fArr10);
                TextView textView6 = this.removeButtonView;
                Property property11 = FrameLayout.SCALE_X;
                float[] fArr11 = new float[1];
                fArr11[0] = i == 3 ? 1.0f : 0.6f;
                animatorArr[10] = ObjectAnimator.ofFloat(textView6, property11, fArr11);
                TextView textView7 = this.removeButtonView;
                Property property12 = FrameLayout.SCALE_Y;
                float[] fArr12 = new float[1];
                fArr12[0] = i == 3 ? 1.0f : 0.6f;
                animatorArr[11] = ObjectAnimator.ofFloat(textView7, property12, fArr12);
                animatorSet2.playTogether(animatorArr);
                this.stateAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.EmojiPackHeader.2
                    {
                        EmojiPackHeader.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                        EmojiPackHeader.this.premiumButtonView.setVisibility(0);
                        EmojiPackHeader.this.addButtonView.setVisibility(0);
                        EmojiPackHeader.this.removeButtonView.setVisibility(0);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        int i3 = 0;
                        EmojiPackHeader.this.premiumButtonView.setVisibility(i == 1 ? 0 : 8);
                        EmojiPackHeader.this.addButtonView.setVisibility(i == 2 ? 0 : 8);
                        TextView textView8 = EmojiPackHeader.this.removeButtonView;
                        if (i != 3) {
                            i3 = 8;
                        }
                        textView8.setVisibility(i3);
                    }
                });
                this.stateAnimator.setDuration(250L);
                this.stateAnimator.setInterpolator(new OvershootInterpolator(1.02f));
                this.stateAnimator.start();
                return;
            }
            this.lockView.setAlpha(i == 1 ? 1.0f : 0.0f);
            this.lockView.setTranslationX(i == 1 ? 0.0f : -AndroidUtilities.dp(16.0f));
            this.headerView.setTranslationX(i == 1 ? AndroidUtilities.dp(16.0f) : 0.0f);
            this.premiumButtonView.setAlpha(i == 1 ? 1.0f : 0.0f);
            this.premiumButtonView.setScaleX(i == 1 ? 1.0f : 0.6f);
            this.premiumButtonView.setScaleY(i == 1 ? 1.0f : 0.6f);
            this.premiumButtonView.setVisibility(i == 1 ? 0 : 8);
            this.addButtonView.setAlpha(i == 2 ? 1.0f : 0.0f);
            this.addButtonView.setScaleX(i == 2 ? 1.0f : 0.6f);
            this.addButtonView.setScaleY(i == 2 ? 1.0f : 0.6f);
            this.addButtonView.setVisibility(i == 2 ? 0 : 8);
            TextView textView8 = this.removeButtonView;
            if (i == 3) {
                f = 1.0f;
            }
            textView8.setAlpha(f);
            this.removeButtonView.setScaleX(i == 3 ? 1.0f : 0.6f);
            this.removeButtonView.setScaleY(i == 3 ? 1.0f : 0.6f);
            TextView textView9 = this.removeButtonView;
            if (i != 3) {
                i2 = 8;
            }
            textView9.setVisibility(i2);
        }
    }

    public void openEmojiPackAlert(final TLRPC$StickerSet tLRPC$StickerSet) {
        if (this.emojiPackAlertOpened) {
            return;
        }
        this.emojiPackAlertOpened = true;
        ArrayList arrayList = new ArrayList(1);
        TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
        tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
        tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
        arrayList.add(tLRPC$TL_inputStickerSetID);
        new EmojiPacksAlert(this.fragment, getContext(), this.resourcesProvider, arrayList) { // from class: org.telegram.ui.Components.EmojiView.28
            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.EmojiPacksAlert, org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
            public void dismiss() {
                EmojiView.this.emojiPackAlertOpened = false;
                super.dismiss();
            }

            @Override // org.telegram.ui.Components.EmojiPacksAlert
            protected void onButtonClicked(boolean z) {
                if (z) {
                    if (!EmojiView.this.installedEmojiSets.contains(Long.valueOf(tLRPC$StickerSet.id))) {
                        EmojiView.this.installedEmojiSets.add(Long.valueOf(tLRPC$StickerSet.id));
                    }
                } else {
                    EmojiView.this.installedEmojiSets.remove(Long.valueOf(tLRPC$StickerSet.id));
                }
                EmojiView.this.updateEmojiHeaders();
            }
        }.show();
    }

    /* loaded from: classes3.dex */
    public class EmojiGridSpacing extends RecyclerView.ItemDecoration {
        public EmojiGridSpacing() {
            EmojiView.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            if (view instanceof StickerSetNameCell) {
                rect.left = AndroidUtilities.dp(5.0f);
                rect.right = AndroidUtilities.dp(5.0f);
                if (recyclerView.getChildAdapterPosition(view) + 1 <= EmojiView.this.emojiAdapter.plainEmojisCount || UserConfig.getInstance(EmojiView.this.currentAccount).isPremium()) {
                    return;
                }
                rect.top = AndroidUtilities.dp(10.0f);
            } else if ((view instanceof RecyclerListView) || (view instanceof EmojiPackHeader)) {
                rect.left = -EmojiView.this.emojiGridView.getPaddingLeft();
                rect.right = -EmojiView.this.emojiGridView.getPaddingRight();
                if (view instanceof EmojiPackHeader) {
                    rect.top = AndroidUtilities.dp(8.0f);
                } else {
                    rect.bottom = AndroidUtilities.dp(6.0f);
                }
            } else if (!(view instanceof BackupImageView)) {
            } else {
                rect.bottom = AndroidUtilities.dp(12.0f);
            }
        }
    }

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
        if (str3 != null) {
            return str4 + str3;
        }
        return str4;
    }

    public void openTrendingStickers(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
        this.delegate.showTrendingStickersAlert(new TrendingStickersLayout(getContext(), new TrendingStickersLayout.Delegate() { // from class: org.telegram.ui.Components.EmojiView.29
            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean canSendSticker() {
                return true;
            }

            {
                EmojiView.this = this;
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered2, boolean z) {
                EmojiView.this.delegate.onStickerSetAdd(tLRPC$StickerSetCovered2);
                if (z) {
                    EmojiView.this.updateStickerTabs(true);
                }
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered2) {
                EmojiView.this.delegate.onStickerSetRemove(tLRPC$StickerSetCovered2);
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean onListViewInterceptTouchEvent(RecyclerListView recyclerListView, MotionEvent motionEvent) {
                return ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, recyclerListView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean onListViewTouchEvent(RecyclerListView recyclerListView, RecyclerListView.OnItemClickListener onItemClickListener, MotionEvent motionEvent) {
                return ContentPreviewViewer.getInstance().onTouch(motionEvent, recyclerListView, EmojiView.this.getMeasuredHeight(), onItemClickListener, EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public String[] getLastSearchKeyboardLanguage() {
                return EmojiView.this.lastSearchKeyboardLanguage;
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public void setLastSearchKeyboardLanguage(String[] strArr) {
                EmojiView.this.lastSearchKeyboardLanguage = strArr;
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public void onStickerSelected(TLRPC$Document tLRPC$Document, Object obj, boolean z, boolean z2, int i) {
                EmojiView.this.delegate.onStickerSelected(null, tLRPC$Document, null, obj, null, z2, i);
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean canSchedule() {
                return EmojiView.this.delegate.canSchedule();
            }

            @Override // org.telegram.ui.Components.TrendingStickersLayout.Delegate
            public boolean isInScheduleMode() {
                return EmojiView.this.delegate.isInScheduleMode();
            }
        }, this.primaryInstallingStickerSets, this.installingStickerSets, this.removingStickerSets, tLRPC$StickerSetCovered, this.resourcesProvider));
    }

    @Override // android.view.View
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        updateStickerTabsPosition();
        updateBottomTabContainerPosition();
    }

    private void updateBottomTabContainerPosition() {
        View view;
        int measuredHeight;
        if (this.bottomTabContainer.getTag() == null) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                return;
            }
            ViewPager viewPager = this.pager;
            if ((viewPager != null && viewPager.getCurrentItem() == 0) || (view = (View) getParent()) == null) {
                return;
            }
            float y = getY() - view.getHeight();
            if (getLayoutParams().height > 0) {
                measuredHeight = getLayoutParams().height;
            } else {
                measuredHeight = getMeasuredHeight();
            }
            float f = y + measuredHeight;
            if (this.bottomTabContainer.getTop() - f < 0.0f) {
                f = this.bottomTabContainer.getTop();
            }
            float f2 = -f;
            this.bottomTabContainer.setTranslationY(f2);
            if (!this.needEmojiSearch) {
                return;
            }
            this.bulletinContainer.setTranslationY(f2);
        }
    }

    public void updateStickerTabsPosition() {
        if (this.stickersTabContainer == null) {
            return;
        }
        boolean z = getVisibility() == 0 && this.stickersContainerAttached && this.delegate.getProgressToSearchOpened() != 1.0f;
        this.stickersTabContainer.setVisibility(z ? 0 : 8);
        if (z) {
            this.rect.setEmpty();
            this.pager.getChildVisibleRect(this.stickersContainer, this.rect, null);
            float dp = AndroidUtilities.dp(50.0f) * this.delegate.getProgressToSearchOpened();
            int i = this.rect.left;
            if (i != 0 || dp != 0.0f) {
                this.expandStickersByDragg = false;
            }
            this.stickersTabContainer.setTranslationX(i);
            float top = (((getTop() + getTranslationY()) - this.stickersTabContainer.getTop()) - this.stickersTab.getExpandedOffset()) - dp;
            if (this.stickersTabContainer.getTranslationY() != top) {
                this.stickersTabContainer.setTranslationY(top);
                this.stickersTabContainer.invalidate();
            }
        }
        if (this.expandStickersByDragg && z && this.showing) {
            this.stickersTab.expandStickers(this.lastStickersX, true);
            return;
        }
        this.expandStickersByDragg = false;
        this.stickersTab.expandStickers(this.lastStickersX, false);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        updateBottomTabContainerPosition();
        super.dispatchDraw(canvas);
    }

    public void startStopVisibleGifs(boolean z) {
        RecyclerListView recyclerListView = this.gifGridView;
        if (recyclerListView == null) {
            return;
        }
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
        if (str != null) {
            if (!str.startsWith("animated_") && !Emoji.isValidEmoji(str)) {
                return;
            }
            Emoji.addRecentEmoji(str);
            if (getVisibility() != 0 || this.pager.getCurrentItem() != 0) {
                Emoji.sortEmoji();
                this.emojiAdapter.notifyDataSetChanged();
            }
            Emoji.saveRecentEmoji();
            if (this.allowAnimatedEmoji) {
                return;
            }
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

    public void openSearch(SearchField searchField) {
        SearchField searchField2;
        final RecyclerListView recyclerListView;
        View view;
        LinearLayoutManager linearLayoutManager;
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
                if (searchField == searchField2 && (emojiViewDelegate = this.delegate) != null && emojiViewDelegate.isExpanded()) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (view != null && i != 2) {
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -AndroidUtilities.dp(40.0f)), ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, -AndroidUtilities.dp(36.0f)), ObjectAnimator.ofFloat(searchField2, View.TRANSLATION_Y, AndroidUtilities.dp(0.0f)));
                    } else {
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, -AndroidUtilities.dp(36.0f)), ObjectAnimator.ofFloat(searchField2, View.TRANSLATION_Y, AndroidUtilities.dp(0.0f)));
                    }
                    this.searchAnimation.setDuration(220L);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.30
                        {
                            EmojiView.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                recyclerListView.setTranslationY(0.0f);
                                if (recyclerListView != EmojiView.this.stickersGridView) {
                                    if (recyclerListView == EmojiView.this.emojiGridView || recyclerListView == EmojiView.this.gifGridView) {
                                        recyclerListView.setPadding(0, 0, 0, 0);
                                    }
                                } else {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                                }
                                EmojiView.this.searchAnimation = null;
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                } else {
                    searchField2.setTranslationY(AndroidUtilities.dp(0.0f));
                    if (view != null && i != 2) {
                        view.setTranslationY(-AndroidUtilities.dp(40.0f));
                    }
                    if (recyclerListView == this.stickersGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                    } else if (recyclerListView == this.emojiGridView || recyclerListView == this.gifGridView) {
                        recyclerListView.setPadding(0, 0, 0, 0);
                    }
                    if (recyclerListView == this.gifGridView) {
                        GifAdapter gifAdapter = this.gifSearchAdapter;
                        if (this.gifAdapter.results.size() <= 0) {
                            z = false;
                        }
                        if (gifAdapter.showTrendingWhenSearchEmpty = z) {
                            this.gifSearchAdapter.search("");
                            RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
                            GifAdapter gifAdapter2 = this.gifSearchAdapter;
                            if (adapter != gifAdapter2) {
                                this.gifGridView.setAdapter(gifAdapter2);
                            }
                        }
                    }
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                }
            }
        }
    }

    private void showEmojiShadow(boolean z, boolean z2) {
        if (!z || this.emojiTabsShadow.getTag() != null) {
            if (!z && this.emojiTabsShadow.getTag() != null) {
                return;
            }
            AnimatorSet animatorSet = this.emojiTabShadowAnimator;
            Integer num = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.emojiTabShadowAnimator = null;
            }
            View view = this.emojiTabsShadow;
            if (!z) {
                num = 1;
            }
            view.setTag(num);
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
                this.emojiTabShadowAnimator.setDuration(200L);
                this.emojiTabShadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.emojiTabShadowAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.31
                    {
                        EmojiView.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        EmojiView.this.emojiTabShadowAnimator = null;
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
        closeSearch(z, -1L);
    }

    private void scrollStickersToPosition(int i, int i2) {
        View findViewByPosition = this.stickersLayoutManager.findViewByPosition(i);
        int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (findViewByPosition == null && Math.abs(i - findFirstVisibleItemPosition) > 40) {
            this.stickersScrollHelper.setScrollDirection(this.stickersLayoutManager.findFirstVisibleItemPosition() < i ? 0 : 1);
            this.stickersScrollHelper.scrollToPosition(i, i2, false, true);
            return;
        }
        this.ignoreStickersScroll = true;
        this.stickersGridView.smoothScrollToPosition(i);
    }

    public void scrollEmojisToAnimated() {
        if (this.emojiSmoothScrolling) {
            return;
        }
        try {
            int i = this.emojiAdapter.sectionToPosition.get(EmojiData.dataColored.length);
            if (i <= 0) {
                return;
            }
            this.emojiGridView.stopScroll();
            updateEmojiTabsPosition(i);
            scrollEmojisToPosition(i, AndroidUtilities.dp(-9.0f));
            checkEmojiTabY(null, 0);
        } catch (Exception unused) {
        }
    }

    public void scrollEmojisToPosition(int i, int i2) {
        View findViewByPosition = this.emojiLayoutManager.findViewByPosition(i);
        int findFirstVisibleItemPosition = this.emojiLayoutManager.findFirstVisibleItemPosition();
        if ((findViewByPosition == null && Math.abs(i - findFirstVisibleItemPosition) > this.emojiLayoutManager.getSpanCount() * 9.0f) || !SharedConfig.animationsEnabled()) {
            this.emojiScrollHelper.setScrollDirection(this.emojiLayoutManager.findFirstVisibleItemPosition() < i ? 0 : 1);
            this.emojiScrollHelper.scrollToPosition(i, i2, false, true);
            return;
        }
        this.ignoreStickersScroll = true;
        LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(this.emojiGridView.getContext(), 2) { // from class: org.telegram.ui.Components.EmojiView.32
            {
                EmojiView.this = this;
            }

            @Override // androidx.recyclerview.widget.LinearSmoothScrollerCustom
            public void onEnd() {
                EmojiView.this.emojiSmoothScrolling = false;
            }

            @Override // androidx.recyclerview.widget.LinearSmoothScrollerCustom, androidx.recyclerview.widget.RecyclerView.SmoothScroller
            protected void onStart() {
                EmojiView.this.emojiSmoothScrolling = true;
            }
        };
        linearSmoothScrollerCustom.setTargetPosition(i);
        linearSmoothScrollerCustom.setOffset(i2);
        this.emojiLayoutManager.startSmoothScroll(linearSmoothScrollerCustom);
    }

    public void closeSearch(boolean z, long j) {
        SearchField searchField;
        final RecyclerListView recyclerListView;
        final GridLayoutManager gridLayoutManager;
        View view;
        TLRPC$TL_messages_stickerSet stickerSetById;
        int positionForPack;
        AnimatorSet animatorSet = this.searchAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        int currentItem = this.pager.getCurrentItem();
        int i = 2;
        if (currentItem == 2 && j != -1 && (stickerSetById = MediaDataController.getInstance(this.currentAccount).getStickerSetById(j)) != null && (positionForPack = this.stickersGridAdapter.getPositionForPack(stickerSetById)) >= 0 && positionForPack < this.stickersGridAdapter.getItemCount()) {
            scrollStickersToPosition(positionForPack, AndroidUtilities.dp(48.0f));
        }
        GifAdapter gifAdapter = this.gifSearchAdapter;
        if (gifAdapter != null) {
            gifAdapter.showTrendingWhenSearchEmpty = false;
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
                if (i2 == currentItem && z) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (view != null && i2 != i) {
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, AndroidUtilities.dp(36.0f) - this.searchFieldHeight), ObjectAnimator.ofFloat(searchField, View.TRANSLATION_Y, AndroidUtilities.dp(36.0f) - this.searchFieldHeight));
                    } else {
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, AndroidUtilities.dp(36.0f) - this.searchFieldHeight), ObjectAnimator.ofFloat(searchField, View.TRANSLATION_Y, -this.searchFieldHeight));
                    }
                    this.searchAnimation.setDuration(200L);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.33
                        {
                            EmojiView.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                int findFirstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                                int top = findFirstVisibleItemPosition != -1 ? (int) (gridLayoutManager.findViewByPosition(findFirstVisibleItemPosition).getTop() + recyclerListView.getTranslationY()) : 0;
                                recyclerListView.setTranslationY(0.0f);
                                if (recyclerListView != EmojiView.this.stickersGridView) {
                                    if (recyclerListView != EmojiView.this.gifGridView) {
                                        if (recyclerListView == EmojiView.this.emojiGridView) {
                                            recyclerListView.setPadding(0, AndroidUtilities.dp(36.0f), 0, AndroidUtilities.dp(44.0f));
                                        }
                                    } else {
                                        recyclerListView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                                    }
                                } else {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(44.0f));
                                }
                                if (findFirstVisibleItemPosition != -1) {
                                    gridLayoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, top - recyclerListView.getPaddingTop());
                                }
                                EmojiView.this.searchAnimation = null;
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                    i = 2;
                } else {
                    searchField.setTranslationY(AndroidUtilities.dp(36.0f) - this.searchFieldHeight);
                    i = 2;
                    if (view != null && i2 != 2) {
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
                }
            }
        }
        if (!z) {
            this.delegate.onSearchOpenClose(0);
        }
        showBottomTab(true, z);
    }

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
        } else if (this.stickersSearchField == null || (recyclerListView = this.stickersGridView) == null) {
        } else {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition2 != null) {
                this.stickersSearchField.setTranslationY(findViewHolderForAdapterPosition2.itemView.getTop());
            } else {
                this.stickersSearchField.setTranslationY(-this.searchFieldHeight);
            }
            this.stickersSearchField.showShadow(false, !z);
        }
    }

    public void checkBottomTabScroll(float f) {
        int dp;
        if (SystemClock.elapsedRealtime() - this.shownBottomTabAfterClick < ViewConfiguration.getTapTimeout()) {
            return;
        }
        this.lastBottomScrollDy += f;
        if (this.pager.getCurrentItem() == 0) {
            dp = AndroidUtilities.dp(38.0f);
        } else {
            dp = AndroidUtilities.dp(48.0f);
        }
        float f2 = this.lastBottomScrollDy;
        if (f2 >= dp) {
            showBottomTab(false, true);
        } else if (f2 <= (-dp)) {
            showBottomTab(true, true);
        } else if ((this.bottomTabContainer.getTag() != null || this.lastBottomScrollDy >= 0.0f) && (this.bottomTabContainer.getTag() == null || this.lastBottomScrollDy <= 0.0f)) {
        } else {
            this.lastBottomScrollDy = 0.0f;
        }
    }

    public void showBackspaceButton(final boolean z, boolean z2) {
        if (!z || this.backspaceButton.getTag() != null) {
            if (!z && this.backspaceButton.getTag() != null) {
                return;
            }
            AnimatorSet animatorSet = this.backspaceButtonAnimation;
            Integer num = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.backspaceButtonAnimation = null;
            }
            ImageView imageView = this.backspaceButton;
            if (!z) {
                num = 1;
            }
            imageView.setTag(num);
            int i = 0;
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
                this.backspaceButtonAnimation.setDuration(200L);
                this.backspaceButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.backspaceButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.34
                    {
                        EmojiView.this = this;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
                i = 4;
            }
            imageView6.setVisibility(i);
        }
    }

    public void showStickerSettingsButton(final boolean z, boolean z2) {
        ImageView imageView = this.stickerSettingsButton;
        if (imageView == null) {
            return;
        }
        if (z && imageView.getTag() == null) {
            return;
        }
        if (!z && this.stickerSettingsButton.getTag() != null) {
            return;
        }
        AnimatorSet animatorSet = this.stickersButtonAnimation;
        Integer num = null;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.stickersButtonAnimation = null;
        }
        ImageView imageView2 = this.stickerSettingsButton;
        if (!z) {
            num = 1;
        }
        imageView2.setTag(num);
        int i = 0;
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
            this.stickersButtonAnimation.setDuration(200L);
            this.stickersButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.stickersButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.35
                {
                    EmojiView.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
            i = 4;
        }
        imageView7.setVisibility(i);
    }

    public void showBottomTab(boolean z, boolean z2) {
        float dp;
        float dp2;
        float dp3;
        float translationY;
        int dp4;
        float f = 0.0f;
        this.lastBottomScrollDy = 0.0f;
        if (!z || this.bottomTabContainer.getTag() != null) {
            if (!z && this.bottomTabContainer.getTag() != null) {
                return;
            }
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                return;
            }
            AnimatorSet animatorSet = this.bottomTabContainerAnimation;
            Integer num = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.bottomTabContainerAnimation = null;
            }
            FrameLayout frameLayout = this.bottomTabContainer;
            if (!z) {
                num = 1;
            }
            frameLayout.setTag(num);
            float f2 = 50.0f;
            if (z2) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.bottomTabContainerAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[3];
                FrameLayout frameLayout2 = this.bottomTabContainer;
                Property property = View.TRANSLATION_Y;
                float[] fArr = new float[1];
                if (z) {
                    dp3 = 0.0f;
                } else {
                    dp3 = AndroidUtilities.dp(this.needEmojiSearch ? 45.0f : 50.0f);
                }
                fArr[0] = dp3;
                animatorArr[0] = ObjectAnimator.ofFloat(frameLayout2, property, fArr);
                FrameLayout frameLayout3 = this.bulletinContainer;
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[1];
                boolean z3 = this.needEmojiSearch;
                if (z3) {
                    if (z) {
                        dp4 = 0;
                    } else {
                        if (z3) {
                            f2 = 45.0f;
                        }
                        dp4 = AndroidUtilities.dp(f2);
                    }
                    translationY = dp4;
                } else {
                    translationY = frameLayout3.getTranslationY();
                }
                fArr2[0] = translationY;
                animatorArr[1] = ObjectAnimator.ofFloat(frameLayout3, property2, fArr2);
                View view = this.shadowLine;
                Property property3 = View.TRANSLATION_Y;
                float[] fArr3 = new float[1];
                if (!z) {
                    f = AndroidUtilities.dp(45.0f);
                }
                fArr3[0] = f;
                animatorArr[2] = ObjectAnimator.ofFloat(view, property3, fArr3);
                animatorSet2.playTogether(animatorArr);
                this.bottomTabContainerAnimation.setDuration(200L);
                this.bottomTabContainerAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.bottomTabContainerAnimation.start();
                return;
            }
            FrameLayout frameLayout4 = this.bottomTabContainer;
            if (z) {
                dp = 0.0f;
            } else {
                dp = AndroidUtilities.dp(this.needEmojiSearch ? 45.0f : 50.0f);
            }
            frameLayout4.setTranslationY(dp);
            boolean z4 = this.needEmojiSearch;
            if (z4) {
                FrameLayout frameLayout5 = this.bulletinContainer;
                if (z) {
                    dp2 = 0.0f;
                } else {
                    if (z4) {
                        f2 = 45.0f;
                    }
                    dp2 = AndroidUtilities.dp(f2);
                }
                frameLayout5.setTranslationY(dp2);
            }
            View view2 = this.shadowLine;
            if (!z) {
                f = AndroidUtilities.dp(45.0f);
            }
            view2.setTranslationY(f);
        }
    }

    public void checkTabsY(int i, int i2) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (i == 1) {
            checkEmojiTabY(this.emojiGridView, i2);
            return;
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) || this.ignoreStickersScroll) {
            return;
        }
        RecyclerListView listViewForType = getListViewForType(i);
        if (i2 > 0 && listViewForType != null && listViewForType.getVisibility() == 0 && (findViewHolderForAdapterPosition = listViewForType.findViewHolderForAdapterPosition(0)) != null && findViewHolderForAdapterPosition.itemView.getTop() + this.searchFieldHeight >= listViewForType.getPaddingTop()) {
            return;
        }
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
            getTabsForType(i).setTranslationY(Math.max(-AndroidUtilities.dp(48.0f), this.tabsMinusDy[i]));
        }
    }

    private void resetTabsY(int i) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && i != 0) {
            View tabsForType = getTabsForType(i);
            this.tabsMinusDy[i] = 0;
            tabsForType.setTranslationY(0);
        }
    }

    public void animateTabsY(final int i) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && i != 0) {
            float dpf2 = AndroidUtilities.dpf2(i == 1 ? 36.0f : 48.0f);
            float f = this.tabsMinusDy[i] / (-dpf2);
            if (f <= 0.0f || f >= 1.0f) {
                animateSearchField(i);
                return;
            }
            View tabsForType = getTabsForType(i);
            int i2 = f > 0.5f ? (int) (-Math.ceil(dpf2)) : 0;
            if (f > 0.5f) {
                animateSearchField(i, false, i2);
            }
            if (i == 1) {
                checkEmojiShadow(i2);
            }
            ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
            if (objectAnimatorArr[i] == null) {
                objectAnimatorArr[i] = ObjectAnimator.ofFloat(tabsForType, View.TRANSLATION_Y, tabsForType.getTranslationY(), i2);
                this.tabsYAnimators[i].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        EmojiView.this.lambda$animateTabsY$9(i, valueAnimator);
                    }
                });
                this.tabsYAnimators[i].setDuration(200L);
            } else {
                objectAnimatorArr[i].setFloatValues(tabsForType.getTranslationY(), i2);
            }
            this.tabsYAnimators[i].start();
        }
    }

    public /* synthetic */ void lambda$animateTabsY$9(int i, ValueAnimator valueAnimator) {
        this.tabsMinusDy[i] = (int) ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    public void stopAnimatingTabsY(int i) {
        ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
        if (objectAnimatorArr[i] == null || !objectAnimatorArr[i].isRunning()) {
            return;
        }
        this.tabsYAnimators[i].cancel();
    }

    private void animateSearchField(int i) {
        RecyclerListView listViewForType = getListViewForType(i);
        boolean z = true;
        int dp = AndroidUtilities.dp(i == 1 ? 38.0f : 48.0f);
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = listViewForType.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition != null) {
            int bottom = findViewHolderForAdapterPosition.itemView.getBottom();
            int[] iArr = this.tabsMinusDy;
            float f = (bottom - (dp + iArr[i])) / this.searchFieldHeight;
            if (f <= 0.0f && f >= 1.0f) {
                return;
            }
            if (f <= 0.5f) {
                z = false;
            }
            animateSearchField(i, z, iArr[i]);
        }
    }

    private void animateSearchField(int i, boolean z, final int i2) {
        if (getListViewForType(i).findViewHolderForAdapterPosition(0) == null) {
            return;
        }
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(this, getContext()) { // from class: org.telegram.ui.Components.EmojiView.36
            @Override // androidx.recyclerview.widget.LinearSmoothScroller
            public int getVerticalSnapPreference() {
                return -1;
            }

            @Override // androidx.recyclerview.widget.LinearSmoothScroller
            public int calculateTimeForDeceleration(int i3) {
                return super.calculateTimeForDeceleration(i3) * 16;
            }

            @Override // androidx.recyclerview.widget.LinearSmoothScroller
            public int calculateDtToFit(int i3, int i4, int i5, int i6, int i7) {
                return super.calculateDtToFit(i3, i4, i5, i6, i7) + i2;
            }
        };
        linearSmoothScroller.setTargetPosition(!z ? 1 : 0);
        getLayoutManagerForType(i).startSmoothScroll(linearSmoothScroller);
    }

    private View getTabsForType(int i) {
        if (i != 0) {
            if (i == 1) {
                return this.emojiTabs;
            }
            if (i == 2) {
                return this.gifTabs;
            }
            throw new IllegalArgumentException("Unexpected argument: " + i);
        }
        return this.stickersTab;
    }

    private RecyclerListView getListViewForType(int i) {
        if (i != 0) {
            if (i == 1) {
                return this.emojiGridView;
            }
            if (i == 2) {
                return this.gifGridView;
            }
            throw new IllegalArgumentException("Unexpected argument: " + i);
        }
        return this.stickersGridView;
    }

    private GridLayoutManager getLayoutManagerForType(int i) {
        if (i != 0) {
            if (i == 1) {
                return this.emojiLayoutManager;
            }
            if (i == 2) {
                return this.gifLayoutManager;
            }
            throw new IllegalArgumentException("Unexpected argument: " + i);
        }
        return this.stickersLayoutManager;
    }

    public SearchField getSearchFieldForType(int i) {
        if (i != 0) {
            if (i == 1) {
                return this.emojiSearchField;
            }
            if (i == 2) {
                return this.gifSearchField;
            }
            throw new IllegalArgumentException("Unexpected argument: " + i);
        }
        return this.stickersSearchField;
    }

    public void checkEmojiSearchFieldScroll(boolean z) {
        EmojiGridView emojiGridView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition == null) {
                this.emojiSearchField.showShadow(true, !z);
            } else {
                this.emojiSearchField.showShadow(findViewHolderForAdapterPosition.itemView.getTop() < this.emojiGridView.getPaddingTop(), !z);
            }
            showEmojiShadow(false, !z);
        } else if (this.emojiSearchField == null || (emojiGridView = this.emojiGridView) == null) {
        } else {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = emojiGridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition2 != null) {
                this.emojiSearchField.setTranslationY(findViewHolderForAdapterPosition2.itemView.getTop());
            } else {
                this.emojiSearchField.setTranslationY(-this.searchFieldHeight);
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

    public void checkEmojiTabY(View view, int i) {
        EmojiGridView emojiGridView;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (view == null) {
            EmojiTabsStrip emojiTabsStrip = this.emojiTabs;
            this.tabsMinusDy[1] = 0;
            emojiTabsStrip.setTranslationY(0);
        } else if (view.getVisibility() != 0 || this.emojiSmoothScrolling) {
        } else {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                return;
            }
            if (i > 0 && (emojiGridView = this.emojiGridView) != null && emojiGridView.getVisibility() == 0 && (findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0)) != null) {
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
            this.emojiTabs.setTranslationY(Math.max(-AndroidUtilities.dp(36.0f), this.tabsMinusDy[1]));
        }
    }

    public void checkGifSearchFieldScroll(boolean z) {
        RecyclerListView recyclerListView;
        int findLastVisibleItemPosition;
        RecyclerListView recyclerListView2 = this.gifGridView;
        if (recyclerListView2 != null && (recyclerListView2.getAdapter() instanceof GifAdapter)) {
            GifAdapter gifAdapter = (GifAdapter) this.gifGridView.getAdapter();
            if (!gifAdapter.searchEndReached && gifAdapter.reqId == 0 && !gifAdapter.results.isEmpty() && (findLastVisibleItemPosition = this.gifLayoutManager.findLastVisibleItemPosition()) != -1 && findLastVisibleItemPosition > this.gifLayoutManager.getItemCount() - 5) {
                gifAdapter.search(gifAdapter.lastSearchImageString, gifAdapter.nextSearchOffset, true, gifAdapter.lastSearchIsEmoji, gifAdapter.lastSearchIsEmoji);
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
        } else if (this.gifSearchField == null || (recyclerListView = this.gifGridView) == null) {
        } else {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition2 != null) {
                this.gifSearchField.setTranslationY(findViewHolderForAdapterPosition2.itemView.getTop());
            } else {
                this.gifSearchField.setTranslationY(-this.searchFieldHeight);
            }
            this.gifSearchField.showShadow(false, !z);
        }
    }

    public void scrollGifsToTop() {
        GifLayoutManager gifLayoutManager = this.gifLayoutManager;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        gifLayoutManager.scrollToPositionWithOffset((emojiViewDelegate == null || !emojiViewDelegate.isExpanded()) ? 1 : 0, 0);
        resetTabsY(2);
    }

    public void checkScroll(int i) {
        int findFirstVisibleItemPosition;
        int findFirstVisibleItemPosition2;
        if (i == 0) {
            if (this.ignoreStickersScroll || (findFirstVisibleItemPosition2 = this.stickersLayoutManager.findFirstVisibleItemPosition()) == -1 || this.stickersGridView == null) {
                return;
            }
            int i2 = this.favTabNum;
            if (i2 <= 0 && (i2 = this.recentTabNum) <= 0) {
                i2 = this.stickersTabOffset;
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition2), i2);
        } else if (i != 2) {
        } else {
            RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
            GifAdapter gifAdapter = this.gifAdapter;
            if (adapter != gifAdapter || gifAdapter.trendingSectionItem < 0 || this.gifTrendingTabNum < 0 || this.gifRecentTabNum < 0 || (findFirstVisibleItemPosition = this.gifLayoutManager.findFirstVisibleItemPosition()) == -1) {
                return;
            }
            this.gifTabs.onPageScrolled(findFirstVisibleItemPosition >= this.gifAdapter.trendingSectionItem ? this.gifTrendingTabNum : this.gifRecentTabNum, 0);
        }
    }

    public void saveNewPage() {
        ViewPager viewPager = this.pager;
        if (viewPager == null) {
            return;
        }
        int currentItem = viewPager.getCurrentItem();
        int i = 1;
        if (currentItem != 2) {
            i = currentItem == 1 ? 2 : 0;
        }
        if (this.currentPage == i) {
            return;
        }
        this.currentPage = i;
        MessagesController.getGlobalEmojiSettings().edit().putInt("selected_page", i).commit();
    }

    public void clearRecentEmoji() {
        Emoji.clearRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
    }

    public void onPageScrolled(int i, int i2, int i3) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate == null) {
            return;
        }
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

    public void postBackspaceRunnable(final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                EmojiView.this.lambda$postBackspaceRunnable$10(i);
            }
        }, i);
    }

    public /* synthetic */ void lambda$postBackspaceRunnable$10(int i) {
        if (!this.backspacePressed) {
            return;
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null && emojiViewDelegate.onBackspace()) {
            this.backspaceButton.performHapticFeedback(3);
        }
        this.backspaceOnce = true;
        postBackspaceRunnable(Math.max(50, i - 100));
    }

    public void switchToGifRecent() {
        showBackspaceButton(false, false);
        showStickerSettingsButton(false, false);
        this.pager.setCurrentItem(1, false);
    }

    public void updateEmojiHeaders() {
        if (this.emojiGridView == null) {
            return;
        }
        for (int i = 0; i < this.emojiGridView.getChildCount(); i++) {
            View childAt = this.emojiGridView.getChildAt(i);
            if (childAt instanceof EmojiPackHeader) {
                ((EmojiPackHeader) childAt).updateState(true);
            }
        }
    }

    public void updateStickerTabs(boolean z) {
        ArrayList<TLRPC$Document> arrayList;
        ArrayList<TLRPC$Document> arrayList2;
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip == null || scrollSlidingTabStrip.isDragging()) {
            return;
        }
        this.recentTabNum = -2;
        this.favTabNum = -2;
        this.trendingTabNum = -2;
        this.premiumTabNum = -2;
        this.hasChatStickers = false;
        this.stickersTabOffset = 0;
        int currentPosition = this.stickersTab.getCurrentPosition();
        this.stickersTab.beginUpdate((getParent() == null || getVisibility() != 0 || (this.installingStickerSets.size() == 0 && this.removingStickerSets.size() == 0)) ? false : true);
        MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
        SharedPreferences emojiSettings = MessagesController.getEmojiSettings(this.currentAccount);
        this.featuredStickerSets.clear();
        ArrayList<TLRPC$StickerSetCovered> featuredStickerSets = mediaDataController.getFeaturedStickerSets();
        int size = featuredStickerSets.size();
        for (int i = 0; i < size; i++) {
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = featuredStickerSets.get(i);
            if (!mediaDataController.isStickerPackInstalled(tLRPC$StickerSetCovered.set.id)) {
                this.featuredStickerSets.add(tLRPC$StickerSetCovered);
            }
        }
        TrendingAdapter trendingAdapter = this.trendingAdapter;
        if (trendingAdapter != null) {
            trendingAdapter.notifyDataSetChanged();
        }
        if (!featuredStickerSets.isEmpty() && (this.featuredStickerSets.isEmpty() || emojiSettings.getLong("featured_hidden", 0L) == featuredStickerSets.get(0).set.id)) {
            int i2 = mediaDataController.getUnreadStickerSets().isEmpty() ? 2 : 3;
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
        if (this.frozenStickerSets == null || z) {
            this.frozenStickerSets = new ArrayList<>(mediaDataController.getStickerSets(0));
        }
        ArrayList<TLRPC$TL_messages_stickerSet> arrayList3 = this.frozenStickerSets;
        int i6 = 0;
        while (true) {
            TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr = this.primaryInstallingStickerSets;
            if (i6 >= tLRPC$StickerSetCoveredArr.length) {
                break;
            }
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered2 = tLRPC$StickerSetCoveredArr[i6];
            if (tLRPC$StickerSetCovered2 != null) {
                TLRPC$TL_messages_stickerSet stickerSetById = mediaDataController.getStickerSetById(tLRPC$StickerSetCovered2.set.id);
                if (stickerSetById != null && !stickerSetById.set.archived) {
                    this.primaryInstallingStickerSets[i6] = null;
                } else {
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
                }
            }
            i6++;
        }
        ArrayList<TLRPC$TL_messages_stickerSet> filterPremiumStickers = MessagesController.getInstance(this.currentAccount).filterPremiumStickers(arrayList3);
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
            long j = MessagesController.getEmojiSettings(this.currentAccount).getLong("group_hide_stickers_" + this.info.id, -1L);
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
                TLRPC$TL_messages_stickerSet groupStickerSetById = mediaDataController.getGroupStickerSetById(tLRPC$StickerSet2);
                if (groupStickerSetById != null && (arrayList = groupStickerSetById.documents) != null && !arrayList.isEmpty() && groupStickerSetById.set != null) {
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

    private void checkPanels() {
        int findFirstVisibleItemPosition;
        if (this.stickersTab == null || (findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition()) == -1) {
            return;
        }
        int i = this.favTabNum;
        if (i <= 0 && (i = this.recentTabNum) <= 0) {
            i = this.stickersTabOffset;
        }
        this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition), i);
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
        } else if (ViewCompat.isLaidOut(this.gifTabs)) {
            if (z3 && !z2) {
                this.gifTabs.onPageScrolled(currentPosition + 1, 0);
            } else if (!z3 && z2) {
                this.gifTabs.onPageScrolled(currentPosition - 1, 0);
            }
        }
    }

    public void addRecentSticker(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return;
        }
        MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, null, tLRPC$Document, (int) (System.currentTimeMillis() / 1000), false);
        boolean isEmpty = this.recentStickers.isEmpty();
        this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        if (!isEmpty) {
            return;
        }
        updateStickerTabs(false);
    }

    public void addRecentGif(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return;
        }
        boolean isEmpty = this.recentGifs.isEmpty();
        updateRecentGifs();
        if (!isEmpty) {
            return;
        }
        updateStickerTabs(false);
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.isLayout) {
            return;
        }
        super.requestLayout();
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
        EmojiGridView emojiGridView = this.emojiGridView;
        if (emojiGridView != null) {
            emojiGridView.setGlowColor(getThemedColor("chat_emojiPanelBackground"));
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
        GifAdapter gifAdapter = this.gifSearchAdapter;
        if (gifAdapter != null) {
            gifAdapter.progressEmptyView.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
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
            paint3.setAlpha((int) (paint3.getAlpha() * 0.5f));
        }
        Drawable drawable3 = this.emojiLockDrawable;
        if (drawable3 != null) {
            drawable3.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_emojiPanelStickerSetName"), PorterDuff.Mode.MULTIPLY));
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        this.isLayout = true;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            if (this.currentBackgroundType != 1) {
                if (Build.VERSION.SDK_INT >= 21) {
                    setOutlineProvider((ViewOutlineProvider) this.outlineProvider);
                    setClipToOutline(true);
                    setElevation(AndroidUtilities.dp(2.0f));
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
                setOutlineProvider(null);
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

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        if (this.lastNotifyWidth != i5) {
            this.lastNotifyWidth = i5;
            reloadStickersAdapter();
        }
        View view = (View) getParent();
        if (view != null) {
            int i6 = i4 - i2;
            int height = view.getHeight();
            if (this.lastNotifyHeight != i6 || this.lastNotifyHeight2 != height) {
                EmojiViewDelegate emojiViewDelegate = this.delegate;
                if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                    this.bottomTabContainer.setTranslationY(AndroidUtilities.dp(49.0f));
                    if (this.needEmojiSearch) {
                        this.bulletinContainer.setTranslationY(AndroidUtilities.dp(49.0f));
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

    public void reloadStickersAdapter() {
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        StickersSearchGridAdapter stickersSearchGridAdapter = this.stickersSearchGridAdapter;
        if (stickersSearchGridAdapter != null) {
            stickersSearchGridAdapter.notifyDataSetChanged();
        }
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().close();
        }
        ContentPreviewViewer.getInstance().reset();
    }

    public void setDelegate(EmojiViewDelegate emojiViewDelegate) {
        this.delegate = emojiViewDelegate;
    }

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        updateStickerTabs(false);
    }

    public void invalidateViews() {
        this.emojiGridView.invalidateViews();
    }

    public void setForseMultiwindowLayout(boolean z) {
        this.forseMultiwindowLayout = z;
    }

    public void onOpen(boolean z) {
        if (this.currentPage != 0 && this.currentChatId != 0) {
            this.currentPage = 0;
        }
        if (this.currentPage == 0 || z || this.currentTabs.size() == 1) {
            showBackspaceButton(true, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() == 0) {
                return;
            }
            this.pager.setCurrentItem(0, !z);
            return;
        }
        int i = this.currentPage;
        if (i != 1) {
            if (i != 2) {
                return;
            }
            showBackspaceButton(false, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 1) {
                this.pager.setCurrentItem(1, false);
            }
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.gifTabs;
            if (scrollSlidingTabStrip == null) {
                return;
            }
            scrollSlidingTabStrip.selectTab(0);
            return;
        }
        showBackspaceButton(false, false);
        showStickerSettingsButton(true, false);
        if (this.pager.getCurrentItem() != 2) {
            this.pager.setCurrentItem(2, false);
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
        if (scrollSlidingTabStrip2 == null) {
            return;
        }
        this.firstTabUpdate = true;
        int i2 = this.favTabNum;
        if (i2 >= 0) {
            scrollSlidingTabStrip2.selectTab(i2);
        } else {
            int i3 = this.recentTabNum;
            if (i3 >= 0) {
                scrollSlidingTabStrip2.selectTab(i3);
            } else {
                scrollSlidingTabStrip2.selectTab(this.stickersTabOffset);
            }
        }
        this.firstTabUpdate = false;
        this.stickersLayoutManager.scrollToPositionWithOffset(1, 0);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.this.lambda$onAttachedToWindow$11();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onAttachedToWindow$11() {
        updateStickerTabs(false);
        reloadStickersAdapter();
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        boolean z = getVisibility() != i;
        super.setVisibility(i);
        if (z) {
            if (i != 8) {
                Emoji.sortEmoji();
                this.emojiAdapter.notifyDataSetChanged();
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
                if (this.stickersGridAdapter != null) {
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
                    updateStickerTabs(false);
                    reloadStickersAdapter();
                }
                checkDocuments(true);
                checkDocuments(false);
                MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
                MediaDataController.getInstance(this.currentAccount).loadRecents(0, false, true, false);
                MediaDataController.getInstance(this.currentAccount).loadRecents(2, false, true, false);
            }
            ChooseStickerActionTracker chooseStickerActionTracker = this.chooseStickerActionTracker;
            if (chooseStickerActionTracker == null) {
                return;
            }
            chooseStickerActionTracker.checkVisibility();
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

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EmojiPopupWindow emojiPopupWindow = this.pickerViewPopup;
        if (emojiPopupWindow == null || !emojiPopupWindow.isShowing()) {
            return;
        }
        this.pickerViewPopup.dismiss();
    }

    private void checkDocuments(boolean z) {
        if (z) {
            updateRecentGifs();
            return;
        }
        int size = this.recentStickers.size();
        int size2 = this.favouriteStickers.size();
        this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
        this.favouriteStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(2);
        if (UserConfig.getInstance(this.currentAccount).isPremium()) {
            this.premiumStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(7);
        } else {
            this.premiumStickers = new ArrayList<>();
        }
        for (int i = 0; i < this.favouriteStickers.size(); i++) {
            TLRPC$Document tLRPC$Document = this.favouriteStickers.get(i);
            int i2 = 0;
            while (true) {
                if (i2 < this.recentStickers.size()) {
                    TLRPC$Document tLRPC$Document2 = this.recentStickers.get(i2);
                    if (tLRPC$Document2.dc_id == tLRPC$Document.dc_id && tLRPC$Document2.id == tLRPC$Document.id) {
                        this.recentStickers.remove(i2);
                        break;
                    }
                    i2++;
                }
            }
        }
        if (MessagesController.getInstance(this.currentAccount).premiumLocked) {
            int i3 = 0;
            while (i3 < this.favouriteStickers.size()) {
                if (MessageObject.isPremiumSticker(this.favouriteStickers.get(i3))) {
                    this.favouriteStickers.remove(i3);
                    i3--;
                }
                i3++;
            }
            int i4 = 0;
            while (i4 < this.recentStickers.size()) {
                if (MessageObject.isPremiumSticker(this.recentStickers.get(i4))) {
                    this.recentStickers.remove(i4);
                    i4--;
                }
                i4++;
            }
        }
        if (size != this.recentStickers.size() || size2 != this.favouriteStickers.size()) {
            updateStickerTabs(false);
        }
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        checkPanels();
    }

    public void updateRecentGifs() {
        GifAdapter gifAdapter;
        int size = this.recentGifs.size();
        long calcDocumentsHash = MediaDataController.calcDocumentsHash(this.recentGifs, Integer.MAX_VALUE);
        ArrayList<TLRPC$Document> recentGifs = MediaDataController.getInstance(this.currentAccount).getRecentGifs();
        this.recentGifs = recentGifs;
        long calcDocumentsHash2 = MediaDataController.calcDocumentsHash(recentGifs, Integer.MAX_VALUE);
        if ((this.gifTabs != null && size == 0 && !this.recentGifs.isEmpty()) || (size != 0 && this.recentGifs.isEmpty())) {
            updateGifTabs();
        }
        if ((size == this.recentGifs.size() && calcDocumentsHash == calcDocumentsHash2) || (gifAdapter = this.gifAdapter) == null) {
            return;
        }
        gifAdapter.notifyDataSetChanged();
    }

    public void setStickersBanned(boolean z, long j) {
        PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
        if (pagerSlidingTabStrip == null) {
            return;
        }
        if (z) {
            this.currentChatId = j;
        } else {
            this.currentChatId = 0L;
        }
        View tab = pagerSlidingTabStrip.getTab(2);
        if (tab == null) {
            return;
        }
        tab.setAlpha(this.currentChatId != 0 ? 0.5f : 1.0f);
        if (this.currentChatId == 0 || this.pager.getCurrentItem() == 0) {
            return;
        }
        showBackspaceButton(true, true);
        showStickerSettingsButton(false, true);
        this.pager.setCurrentItem(0, false);
    }

    public void showStickerBanHint(boolean z) {
        TLRPC$Chat chat;
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
        if (this.mediaBanTooltip.getVisibility() == 0 || (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.currentChatId))) == null) {
            return;
        }
        if (ChatObject.hasAdminRights(chat) || (tLRPC$TL_chatBannedRights = chat.default_banned_rights) == null || !tLRPC$TL_chatBannedRights.send_stickers) {
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2 = chat.banned_rights;
            if (tLRPC$TL_chatBannedRights2 == null) {
                return;
            }
            if (AndroidUtilities.isBannedForever(tLRPC$TL_chatBannedRights2)) {
                if (z) {
                    this.mediaBanTooltip.setText(LocaleController.getString("AttachGifRestrictedForever", R.string.AttachGifRestrictedForever));
                } else {
                    this.mediaBanTooltip.setText(LocaleController.getString("AttachStickersRestrictedForever", R.string.AttachStickersRestrictedForever));
                }
            } else if (z) {
                this.mediaBanTooltip.setText(LocaleController.formatString("AttachGifRestricted", R.string.AttachGifRestricted, LocaleController.formatDateForBan(chat.banned_rights.until_date)));
            } else {
                this.mediaBanTooltip.setText(LocaleController.formatString("AttachStickersRestricted", R.string.AttachStickersRestricted, LocaleController.formatDateForBan(chat.banned_rights.until_date)));
            }
        } else if (z) {
            this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachGifRestricted", R.string.GlobalAttachGifRestricted));
        } else {
            this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachStickersRestricted", R.string.GlobalAttachStickersRestricted));
        }
        this.mediaBanTooltip.setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.mediaBanTooltip, View.ALPHA, 0.0f, 1.0f));
        animatorSet.addListener(new AnonymousClass37());
        animatorSet.setDuration(300L);
        animatorSet.start();
    }

    /* renamed from: org.telegram.ui.Components.EmojiView$37 */
    /* loaded from: classes3.dex */
    public class AnonymousClass37 extends AnimatorListenerAdapter {
        AnonymousClass37() {
            EmojiView.this = r1;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$37$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.AnonymousClass37.this.lambda$onAnimationEnd$0();
                }
            }, 5000L);
        }

        public /* synthetic */ void lambda$onAnimationEnd$0() {
            if (EmojiView.this.mediaBanTooltip == null) {
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(EmojiView.this.mediaBanTooltip, View.ALPHA, 0.0f));
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiView.37.1
                {
                    AnonymousClass37.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (EmojiView.this.mediaBanTooltip != null) {
                        EmojiView.this.mediaBanTooltip.setVisibility(4);
                    }
                }
            });
            animatorSet.setDuration(300L);
            animatorSet.start();
        }
    }

    private void updateVisibleTrendingSets() {
        boolean z;
        RecyclerListView recyclerListView = this.stickersGridView;
        if (recyclerListView == null) {
            return;
        }
        try {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.stickersGridView.getChildAt(i);
                if ((childAt instanceof FeaturedStickerSetInfoCell) && ((RecyclerListView.Holder) this.stickersGridView.getChildViewHolder(childAt)) != null) {
                    FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) childAt;
                    ArrayList<Long> unreadStickerSets = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
                    TLRPC$StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
                    boolean z2 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(stickerSet.set.id));
                    int i2 = 0;
                    while (true) {
                        TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr = this.primaryInstallingStickerSets;
                        if (i2 >= tLRPC$StickerSetCoveredArr.length) {
                            z = false;
                            break;
                        } else if (tLRPC$StickerSetCoveredArr[i2] != null && tLRPC$StickerSetCoveredArr[i2].set.id == stickerSet.set.id) {
                            z = true;
                            break;
                        } else {
                            i2++;
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
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean areThereAnyStickers() {
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        return stickersGridAdapter != null && stickersGridAdapter.getItemCount() > 0;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        Utilities.Callback<TLRPC$TL_messages_stickerSet> remove;
        TLRPC$StickerSet tLRPC$StickerSet;
        if (i == NotificationCenter.stickersDidLoad) {
            if (((Integer) objArr[0]).intValue() == 0) {
                if (this.stickersGridAdapter == null) {
                    return;
                }
                updateStickerTabs(((Boolean) objArr[1]).booleanValue());
                updateVisibleTrendingSets();
                reloadStickersAdapter();
                checkPanels();
            } else if (((Integer) objArr[0]).intValue() == 5) {
                this.emojiAdapter.notifyDataSetChanged(((Boolean) objArr[1]).booleanValue());
            }
        } else if (i == NotificationCenter.recentDocumentsDidLoad) {
            boolean booleanValue = ((Boolean) objArr[0]).booleanValue();
            int intValue = ((Integer) objArr[1]).intValue();
            if (!booleanValue && intValue != 0 && intValue != 2) {
                return;
            }
            checkDocuments(booleanValue);
        } else if (i == NotificationCenter.featuredStickersDidLoad) {
            updateVisibleTrendingSets();
            PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
            if (pagerSlidingTabStrip != null) {
                int childCount = pagerSlidingTabStrip.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    this.typeTabs.getChildAt(i3).invalidate();
                }
            }
            updateStickerTabs(false);
        } else if (i == NotificationCenter.featuredEmojiDidLoad) {
            EmojiGridAdapter emojiGridAdapter = this.emojiAdapter;
            if (emojiGridAdapter == null) {
                return;
            }
            emojiGridAdapter.notifyDataSetChanged();
        } else if (i == NotificationCenter.groupStickersDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull != null && (tLRPC$StickerSet = tLRPC$ChatFull.stickerset) != null && tLRPC$StickerSet.id == ((Long) objArr[0]).longValue()) {
                updateStickerTabs(false);
            }
            if (!this.toInstall.containsKey((Long) objArr[0]) || objArr.length < 2) {
                return;
            }
            long longValue = ((Long) objArr[0]).longValue();
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) objArr[1];
            if (this.toInstall.get(Long.valueOf(longValue)) == null || tLRPC$TL_messages_stickerSet == null || (remove = this.toInstall.remove(Long.valueOf(longValue))) == null) {
                return;
            }
            remove.run(tLRPC$TL_messages_stickerSet);
        } else if (i == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.stickersGridView;
            if (recyclerListView != null) {
                int childCount2 = recyclerListView.getChildCount();
                for (int i4 = 0; i4 < childCount2; i4++) {
                    View childAt = this.stickersGridView.getChildAt(i4);
                    if ((childAt instanceof StickerSetNameCell) || (childAt instanceof StickerEmojiCell)) {
                        childAt.invalidate();
                    }
                }
            }
            EmojiColorPickerView emojiColorPickerView = this.pickerView;
            if (emojiColorPickerView != null) {
                emojiColorPickerView.invalidate();
            }
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.gifTabs;
            if (scrollSlidingTabStrip == null) {
                return;
            }
            scrollSlidingTabStrip.invalidateTabs();
        } else if (i == NotificationCenter.newEmojiSuggestionsAvailable) {
            if (this.emojiGridView == null || !this.needEmojiSearch) {
                return;
            }
            if ((!this.emojiSearchField.progressDrawable.isAnimating() && this.emojiGridView.getAdapter() != this.emojiSearchAdapter) || TextUtils.isEmpty(this.emojiSearchAdapter.lastSearchEmojiString)) {
                return;
            }
            EmojiSearchAdapter emojiSearchAdapter = this.emojiSearchAdapter;
            emojiSearchAdapter.search(emojiSearchAdapter.lastSearchEmojiString);
        } else if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            EmojiGridAdapter emojiGridAdapter2 = this.emojiAdapter;
            if (emojiGridAdapter2 != null) {
                emojiGridAdapter2.notifyDataSetChanged();
            }
            updateEmojiHeaders();
            updateStickerTabs(false);
        }
    }

    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    /* loaded from: classes3.dex */
    public class TrendingAdapter extends RecyclerListView.SelectionAdapter {
        private boolean emoji;

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public TrendingAdapter(boolean z) {
            EmojiView.this = r1;
            this.emoji = z;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1753onCreateViewHolder(ViewGroup viewGroup, int i) {
            BackupImageView backupImageView = new BackupImageView(EmojiView.this.getContext()) { // from class: org.telegram.ui.Components.EmojiView.TrendingAdapter.1
                {
                    TrendingAdapter.this = this;
                }

                @Override // org.telegram.ui.Components.BackupImageView, android.view.View
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (!MediaDataController.getInstance(EmojiView.this.currentAccount).isStickerPackUnread(TrendingAdapter.this.emoji, ((TLRPC$StickerSetCovered) getTag()).set.id) || EmojiView.this.dotPaint == null) {
                        return;
                    }
                    canvas.drawCircle(canvas.getWidth() - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(3.0f), EmojiView.this.dotPaint);
                }
            };
            float f = 36.0f;
            int dp = AndroidUtilities.dp(this.emoji ? 36.0f : 30.0f);
            if (!this.emoji) {
                f = 30.0f;
            }
            backupImageView.setSize(dp, AndroidUtilities.dp(f));
            backupImageView.setLayerNum(1);
            backupImageView.setAspectFit(true);
            backupImageView.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f)));
            return new RecyclerListView.Holder(backupImageView);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ImageLocation forSticker;
            BackupImageView backupImageView = (BackupImageView) viewHolder.itemView;
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) (this.emoji ? EmojiView.this.featuredEmojiSets : EmojiView.this.featuredStickerSets).get(i);
            backupImageView.setTag(tLRPC$StickerSetCovered);
            TLRPC$Document tLRPC$Document = tLRPC$StickerSetCovered.cover;
            if (!tLRPC$StickerSetCovered.covers.isEmpty()) {
                tLRPC$Document = tLRPC$StickerSetCovered.covers.get(0);
            }
            TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$StickerSetCovered.set.thumbs, 90);
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
            } else if (!(closestPhotoSizeWithSize instanceof TLRPC$PhotoSize)) {
                return;
            } else {
                forSticker = ImageLocation.getForSticker((TLRPC$PhotoSize) closestPhotoSizeWithSize, tLRPC$Document, tLRPC$StickerSetCovered.set.thumb_version);
            }
            ImageLocation imageLocation = forSticker;
            if (imageLocation == null) {
                return;
            }
            if (!z || (!MessageObject.isAnimatedStickerDocument(tLRPC$Document, true) && !MessageObject.isVideoSticker(tLRPC$Document))) {
                if (imageLocation.imageType == 1) {
                    backupImageView.setImage(imageLocation, "30_30", "tgs", svgThumb, tLRPC$StickerSetCovered);
                } else {
                    backupImageView.setImage(imageLocation, (String) null, "webp", svgThumb, tLRPC$StickerSetCovered);
                }
            } else if (svgThumb != null) {
                backupImageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "30_30", svgThumb, 0, tLRPC$StickerSetCovered);
            } else {
                backupImageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "30_30", imageLocation, (String) null, 0, tLRPC$StickerSetCovered);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return (this.emoji ? EmojiView.this.featuredEmojiSets : EmojiView.this.featuredStickerSets).size();
        }
    }

    /* loaded from: classes3.dex */
    private class TrendingListView extends RecyclerListView {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TrendingListView(Context context, RecyclerView.Adapter adapter) {
            super(context);
            EmojiView.this = r2;
            setNestedScrollingEnabled(true);
            setSelectorRadius(AndroidUtilities.dp(4.0f));
            setSelectorDrawableColor(getThemedColor("listSelectorSDK21"));
            setTag(9);
            setItemAnimator(null);
            setLayoutAnimation(null);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, context, r2) { // from class: org.telegram.ui.Components.EmojiView.TrendingListView.1
                @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            linearLayoutManager.setOrientation(0);
            setLayoutManager(linearLayoutManager);
            setAdapter(adapter);
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (getParent() != null && getParent().getParent() != null) {
                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1) || canScrollHorizontally(1));
                EmojiView.this.pager.requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    /* loaded from: classes3.dex */
    public class StickersGridAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int stickersPerRow;
        private int totalItems;
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        private HashMap<Object, Integer> packStartPosition = new HashMap<>();
        private SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<Object> cacheParents = new SparseArray<>();
        private SparseIntArray positionToRow = new SparseIntArray();

        public StickersGridAdapter(Context context) {
            EmojiView.this = r1;
            this.context = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.itemView instanceof RecyclerListView;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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
            if ("search".equals(obj) || "trend1".equals(obj) || "trend2".equals(obj)) {
                if (EmojiView.this.favTabNum >= 0) {
                    return EmojiView.this.favTabNum;
                }
                if (EmojiView.this.recentTabNum < 0) {
                    return 0;
                }
                return EmojiView.this.recentTabNum;
            }
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
                return "recent".equals(obj2) ? EmojiView.this.recentTabNum : EmojiView.this.favTabNum;
            }
        }

        public /* synthetic */ void lambda$onCreateViewHolder$1(StickerSetNameCell stickerSetNameCell, View view) {
            RecyclerView.ViewHolder childViewHolder;
            if (EmojiView.this.stickersGridView.indexOfChild(stickerSetNameCell) == -1 || (childViewHolder = EmojiView.this.stickersGridView.getChildViewHolder(stickerSetNameCell)) == null) {
                return;
            }
            if (childViewHolder.getAdapterPosition() == EmojiView.this.groupStickerPackPosition) {
                if (EmojiView.this.groupStickerSet != null) {
                    if (EmojiView.this.delegate == null) {
                        return;
                    }
                    EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
                    return;
                }
                SharedPreferences.Editor edit = MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit();
                edit.putLong("group_hide_stickers_" + EmojiView.this.info.id, EmojiView.this.info.stickerset != null ? EmojiView.this.info.stickerset.id : 0L).apply();
                EmojiView.this.updateStickerTabs(false);
                if (EmojiView.this.stickersGridAdapter == null) {
                    return;
                }
                EmojiView.this.stickersGridAdapter.notifyDataSetChanged();
            } else if (this.cache.get(childViewHolder.getAdapterPosition()) != EmojiView.this.recentStickers) {
            } else {
                AlertDialog create = new AlertDialog.Builder(this.context).setTitle(LocaleController.getString(R.string.ClearRecentStickersAlertTitle)).setMessage(LocaleController.getString(R.string.ClearRecentStickersAlertMessage)).setPositiveButton(LocaleController.getString(R.string.ClearButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$0(dialogInterface, i);
                    }
                }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
                create.show();
                TextView textView = (TextView) create.getButton(-1);
                if (textView == null) {
                    return;
                }
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0(DialogInterface dialogInterface, int i) {
            MediaDataController.getInstance(EmojiView.this.currentAccount).clearRecentStickers();
        }

        public /* synthetic */ void lambda$onCreateViewHolder$2(View view) {
            if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
            }
        }

        public /* synthetic */ void lambda$onCreateViewHolder$3(View view) {
            ArrayList<TLRPC$StickerSetCovered> featuredStickerSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getFeaturedStickerSets();
            if (!featuredStickerSets.isEmpty()) {
                MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit().putLong("featured_hidden", featuredStickerSets.get(0).set.id).commit();
                if (EmojiView.this.stickersGridAdapter != null) {
                    EmojiView.this.stickersGridAdapter.notifyItemRangeRemoved(1, 2);
                }
                EmojiView.this.updateStickerTabs(false);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r6v4, types: [org.telegram.ui.Cells.StickerSetGroupInfoCell, android.view.View] */
        /* JADX WARN: Type inference failed for: r6v5, types: [android.view.View] */
        /* JADX WARN: Type inference failed for: r6v7, types: [org.telegram.ui.Components.EmojiView$TrendingListView, android.view.View, androidx.recyclerview.widget.RecyclerView, org.telegram.ui.Components.RecyclerListView] */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        @SuppressLint({"NotifyDataSetChanged"})
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1753onCreateViewHolder(ViewGroup viewGroup, int i) {
            StickerSetNameCell stickerSetNameCell;
            switch (i) {
                case 0:
                    stickerSetNameCell = new StickerEmojiCell(this, this.context, true) { // from class: org.telegram.ui.Components.EmojiView.StickersGridAdapter.1
                        @Override // android.widget.FrameLayout, android.view.View
                        public void onMeasure(int i2, int i3) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    break;
                case 1:
                    stickerSetNameCell = new EmptyCell(this.context);
                    break;
                case 2:
                    final StickerSetNameCell stickerSetNameCell2 = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    stickerSetNameCell2.setOnIconClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda3
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$1(stickerSetNameCell2, view);
                        }
                    });
                    stickerSetNameCell = stickerSetNameCell2;
                    break;
                case 3:
                    ?? stickerSetGroupInfoCell = new StickerSetGroupInfoCell(this.context);
                    stickerSetGroupInfoCell.setAddOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$2(view);
                        }
                    });
                    stickerSetGroupInfoCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    stickerSetNameCell = stickerSetGroupInfoCell;
                    break;
                case 4:
                    ?? view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    stickerSetNameCell = view;
                    break;
                case 5:
                    StickerSetNameCell stickerSetNameCell3 = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    stickerSetNameCell3.setOnIconClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$3(view2);
                        }
                    });
                    stickerSetNameCell = stickerSetNameCell3;
                    break;
                case 6:
                    EmojiView emojiView = EmojiView.this;
                    ?? trendingListView = new TrendingListView(this.context, emojiView.trendingAdapter = new TrendingAdapter(false));
                    trendingListView.addItemDecoration(new RecyclerView.ItemDecoration(this) { // from class: org.telegram.ui.Components.EmojiView.StickersGridAdapter.2
                        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
                        public void getItemOffsets(android.graphics.Rect rect, View view2, RecyclerView recyclerView, RecyclerView.State state) {
                            rect.right = AndroidUtilities.dp(8.0f);
                        }
                    });
                    trendingListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersGridAdapter$$ExternalSyntheticLambda4
                        @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                        public final void onItemClick(View view2, int i2) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$4(view2, i2);
                        }
                    });
                    trendingListView.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(52.0f)));
                    stickerSetNameCell = trendingListView;
                    break;
                default:
                    stickerSetNameCell = null;
                    break;
            }
            return new RecyclerListView.Holder(stickerSetNameCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$4(View view, int i) {
            EmojiView.this.openTrendingStickers((TLRPC$StickerSetCovered) view.getTag());
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            int i3;
            String str;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                TLRPC$Document tLRPC$Document = (TLRPC$Document) this.cache.get(i);
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
                stickerEmojiCell.setSticker(tLRPC$Document, this.cacheParents.get(i), false);
                stickerEmojiCell.setRecent(EmojiView.this.recentStickers.contains(tLRPC$Document));
                return;
            }
            ArrayList<TLRPC$Document> arrayList = null;
            TLRPC$Chat tLRPC$Chat = null;
            int i4 = 1;
            if (itemViewType == 1) {
                EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                if (i == this.totalItems) {
                    int i5 = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                    if (i5 == Integer.MIN_VALUE) {
                        emptyCell.setHeight(1);
                        return;
                    }
                    Object obj = this.rowStartPack.get(i5);
                    if (obj instanceof TLRPC$TL_messages_stickerSet) {
                        arrayList = ((TLRPC$TL_messages_stickerSet) obj).documents;
                    } else if (obj instanceof String) {
                        arrayList = "recent".equals(obj) ? EmojiView.this.recentStickers : EmojiView.this.favouriteStickers;
                    }
                    if (arrayList == null) {
                        emptyCell.setHeight(1);
                        return;
                    } else if (!arrayList.isEmpty()) {
                        int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil(arrayList.size() / this.stickersPerRow)) * AndroidUtilities.dp(82.0f));
                        if (height > 0) {
                            i4 = height;
                        }
                        emptyCell.setHeight(i4);
                        return;
                    } else {
                        emptyCell.setHeight(AndroidUtilities.dp(8.0f));
                        return;
                    }
                }
                emptyCell.setHeight(AndroidUtilities.dp(82.0f));
            } else if (itemViewType != 2) {
                if (itemViewType == 3) {
                    StickerSetGroupInfoCell stickerSetGroupInfoCell = (StickerSetGroupInfoCell) viewHolder.itemView;
                    if (i == this.totalItems - 1) {
                        z = true;
                    }
                    stickerSetGroupInfoCell.setIsLast(z);
                } else if (itemViewType != 5) {
                } else {
                    StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                    if (MediaDataController.getInstance(EmojiView.this.currentAccount).loadFeaturedPremium) {
                        i3 = R.string.FeaturedStickersPremium;
                        str = "FeaturedStickersPremium";
                    } else {
                        i3 = R.string.FeaturedStickers;
                        str = "FeaturedStickers";
                    }
                    stickerSetNameCell.setText(LocaleController.getString(str, i3), R.drawable.msg_close);
                }
            } else {
                StickerSetNameCell stickerSetNameCell2 = (StickerSetNameCell) viewHolder.itemView;
                if (i == EmojiView.this.groupStickerPackPosition) {
                    if (!EmojiView.this.groupStickersHidden || EmojiView.this.groupStickerSet != null) {
                        i2 = EmojiView.this.groupStickerSet != null ? R.drawable.msg_mini_customize : R.drawable.msg_close;
                    } else {
                        i2 = 0;
                    }
                    if (EmojiView.this.info != null) {
                        tLRPC$Chat = MessagesController.getInstance(EmojiView.this.currentAccount).getChat(Long.valueOf(EmojiView.this.info.id));
                    }
                    int i6 = R.string.CurrentGroupStickers;
                    Object[] objArr = new Object[1];
                    objArr[0] = tLRPC$Chat != null ? tLRPC$Chat.title : "Group Stickers";
                    stickerSetNameCell2.setText(LocaleController.formatString("CurrentGroupStickers", i6, objArr), i2);
                    return;
                }
                Object obj2 = this.cache.get(i);
                if (!(obj2 instanceof TLRPC$TL_messages_stickerSet)) {
                    if (obj2 != EmojiView.this.recentStickers) {
                        if (obj2 != EmojiView.this.favouriteStickers) {
                            if (obj2 != EmojiView.this.premiumStickers) {
                                return;
                            }
                            stickerSetNameCell2.setText(LocaleController.getString("PremiumStickers", R.string.PremiumStickers), 0);
                            return;
                        }
                        stickerSetNameCell2.setText(LocaleController.getString("FavoriteStickers", R.string.FavoriteStickers), 0);
                        return;
                    }
                    stickerSetNameCell2.setText(LocaleController.getString("RecentStickers", R.string.RecentStickers), R.drawable.msg_close, LocaleController.getString(R.string.ClearRecentStickersAlertTitle));
                    return;
                }
                TLRPC$StickerSet tLRPC$StickerSet = ((TLRPC$TL_messages_stickerSet) obj2).set;
                if (tLRPC$StickerSet == null) {
                    return;
                }
                stickerSetNameCell2.setText(tLRPC$StickerSet.title, 0);
            }
        }

        private void updateItems() {
            ArrayList<TLRPC$Document> arrayList;
            Object obj;
            ArrayList arrayList2;
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
            ArrayList arrayList3 = EmojiView.this.stickerSets;
            int i3 = -5;
            int i4 = -5;
            int i5 = 0;
            while (i4 < arrayList3.size()) {
                if (i4 == i3) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i6 = this.totalItems;
                    this.totalItems = i6 + 1;
                    sparseArray.put(i6, "search");
                    i5++;
                } else if (i4 != -4) {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
                    if (i4 == -3) {
                        arrayList = EmojiView.this.favouriteStickers;
                        this.packStartPosition.put("fav", Integer.valueOf(this.totalItems));
                        obj = "fav";
                    } else if (i4 == -2) {
                        arrayList = EmojiView.this.recentStickers;
                        this.packStartPosition.put("recent", Integer.valueOf(this.totalItems));
                        obj = "recent";
                    } else if (i4 == -1) {
                        arrayList = EmojiView.this.premiumStickers;
                        this.packStartPosition.put("premium", Integer.valueOf(this.totalItems));
                        obj = "premium";
                    } else {
                        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = (TLRPC$TL_messages_stickerSet) arrayList3.get(i4);
                        ArrayList<TLRPC$Document> arrayList4 = tLRPC$TL_messages_stickerSet2.documents;
                        this.packStartPosition.put(tLRPC$TL_messages_stickerSet2, Integer.valueOf(this.totalItems));
                        tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet2;
                        arrayList = arrayList4;
                        obj = null;
                    }
                    if (i4 == EmojiView.this.groupStickerPackNum) {
                        EmojiView.this.groupStickerPackPosition = this.totalItems;
                        if (arrayList.isEmpty()) {
                            this.rowStartPack.put(i5, tLRPC$TL_messages_stickerSet);
                            int i7 = i5 + 1;
                            this.positionToRow.put(this.totalItems, i5);
                            this.rowStartPack.put(i7, tLRPC$TL_messages_stickerSet);
                            this.positionToRow.put(this.totalItems + 1, i7);
                            SparseArray<Object> sparseArray2 = this.cache;
                            int i8 = this.totalItems;
                            this.totalItems = i8 + 1;
                            sparseArray2.put(i8, tLRPC$TL_messages_stickerSet);
                            SparseArray<Object> sparseArray3 = this.cache;
                            int i9 = this.totalItems;
                            this.totalItems = i9 + 1;
                            sparseArray3.put(i9, "group");
                            arrayList2 = arrayList3;
                            i5 = i7 + 1;
                            i4++;
                            arrayList3 = arrayList2;
                            i2 = 0;
                            i3 = -5;
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        int ceil = (int) Math.ceil(arrayList.size() / this.stickersPerRow);
                        if (tLRPC$TL_messages_stickerSet != null) {
                            this.cache.put(this.totalItems, tLRPC$TL_messages_stickerSet);
                        } else {
                            this.cache.put(this.totalItems, arrayList);
                        }
                        this.positionToRow.put(this.totalItems, i5);
                        int i10 = 0;
                        while (i10 < arrayList.size()) {
                            int i11 = i10 + 1;
                            int i12 = this.totalItems + i11;
                            this.cache.put(i12, arrayList.get(i10));
                            if (tLRPC$TL_messages_stickerSet != null) {
                                this.cacheParents.put(i12, tLRPC$TL_messages_stickerSet);
                            } else {
                                this.cacheParents.put(i12, obj);
                            }
                            this.positionToRow.put(this.totalItems + i11, i5 + 1 + (i10 / this.stickersPerRow));
                            i10 = i11;
                            arrayList3 = arrayList3;
                        }
                        arrayList2 = arrayList3;
                        int i13 = 0;
                        while (true) {
                            i = ceil + 1;
                            if (i13 >= i) {
                                break;
                            }
                            if (tLRPC$TL_messages_stickerSet != null) {
                                this.rowStartPack.put(i5 + i13, tLRPC$TL_messages_stickerSet);
                            } else if (i4 == -1) {
                                this.rowStartPack.put(i5 + i13, "premium");
                            } else {
                                if (i4 == -2) {
                                    this.rowStartPack.put(i5 + i13, "recent");
                                } else {
                                    this.rowStartPack.put(i5 + i13, "fav");
                                }
                                i13++;
                            }
                            i13++;
                        }
                        this.totalItems += (ceil * this.stickersPerRow) + 1;
                        i5 += i;
                        i4++;
                        arrayList3 = arrayList2;
                        i2 = 0;
                        i3 = -5;
                    }
                } else {
                    MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
                    SharedPreferences emojiSettings = MessagesController.getEmojiSettings(EmojiView.this.currentAccount);
                    ArrayList<TLRPC$StickerSetCovered> featuredStickerSets = mediaDataController.getFeaturedStickerSets();
                    if (!EmojiView.this.featuredStickerSets.isEmpty() && emojiSettings.getLong("featured_hidden", 0L) != featuredStickerSets.get(i2).set.id) {
                        SparseArray<Object> sparseArray4 = this.cache;
                        int i14 = this.totalItems;
                        this.totalItems = i14 + 1;
                        sparseArray4.put(i14, "trend1");
                        SparseArray<Object> sparseArray5 = this.cache;
                        int i15 = this.totalItems;
                        this.totalItems = i15 + 1;
                        sparseArray5.put(i15, "trend2");
                        i5 += 2;
                    }
                }
                arrayList2 = arrayList3;
                i4++;
                arrayList3 = arrayList2;
                i2 = 0;
                i3 = -5;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeRemoved(int i, int i2) {
            updateItems();
            super.notifyItemRangeRemoved(i, i2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateItems();
            super.notifyDataSetChanged();
        }
    }

    /* loaded from: classes3.dex */
    public static class EmojiPackExpand extends FrameLayout {
        public TextView textView;

        public EmojiPackExpand(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextSize(1, 13.0f);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhite", resourcesProvider));
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0f), ColorUtils.setAlphaComponent(Theme.getColor("chat_emojiPanelStickerSetName", resourcesProvider), 99)));
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(1.66f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(2.0f));
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiGridAdapter extends RecyclerListView.SelectionAdapter {
        private int firstTrendingRow;
        private ArrayList<TLRPC$TL_messages_stickerSet> frozenEmojiPacks;
        private int itemCount;
        private ArrayList<Integer> packStartPosition;
        public int plainEmojisCount;
        private SparseIntArray positionToExpand;
        private SparseIntArray positionToSection;
        private SparseIntArray positionToUnlock;
        private ArrayList<Integer> rowHashCodes;
        private SparseIntArray sectionToPosition;
        private int trendingHeaderRow;
        private int trendingRow;

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        private EmojiGridAdapter() {
            EmojiView.this = r1;
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

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.itemCount;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 4 || itemViewType == 3 || itemViewType == 6;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0(View view, int i) {
            ArrayList arrayList = new ArrayList();
            ArrayList<TLRPC$StickerSetCovered> featuredEmojiSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getFeaturedEmojiSets();
            for (int i2 = 0; featuredEmojiSets != null && i2 < featuredEmojiSets.size(); i2++) {
                TLRPC$StickerSetCovered tLRPC$StickerSetCovered = featuredEmojiSets.get(i2);
                if (tLRPC$StickerSetCovered != null && tLRPC$StickerSetCovered.set != null) {
                    TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                    TLRPC$StickerSet tLRPC$StickerSet = tLRPC$StickerSetCovered.set;
                    tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                    tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                    arrayList.add(tLRPC$TL_inputStickerSetID);
                }
            }
            MediaDataController.getInstance(EmojiView.this.currentAccount).markFeaturedStickersAsRead(true, true);
            if (EmojiView.this.fragment != null) {
                EmojiView.this.fragment.showDialog(new EmojiPacksAlert(EmojiView.this.fragment, EmojiView.this.getContext(), EmojiView.this.fragment.getResourceProvider(), arrayList));
            } else {
                new EmojiPacksAlert(null, EmojiView.this.getContext(), EmojiView.this.resourcesProvider, arrayList).show();
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r6v6, types: [org.telegram.ui.Components.EmojiView$TrendingListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, org.telegram.ui.Components.RecyclerListView] */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1753onCreateViewHolder(ViewGroup viewGroup, int i) {
            View imageViewEmoji;
            StickerSetNameCell stickerSetNameCell;
            if (i != 0) {
                if (i == 1) {
                    stickerSetNameCell = new StickerSetNameCell(EmojiView.this.getContext(), true, EmojiView.this.resourcesProvider);
                } else if (i == 3) {
                    EmojiView emojiView = EmojiView.this;
                    imageViewEmoji = new EmojiPackButton(emojiView, emojiView.getContext());
                } else if (i == 4) {
                    EmojiView emojiView2 = EmojiView.this;
                    Context context = emojiView2.getContext();
                    EmojiView emojiView3 = EmojiView.this;
                    ?? trendingListView = new TrendingListView(context, emojiView3.trendingEmojiAdapter = new TrendingAdapter(true));
                    trendingListView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
                    trendingListView.setClipToPadding(false);
                    trendingListView.addItemDecoration(new RecyclerView.ItemDecoration(this) { // from class: org.telegram.ui.Components.EmojiView.EmojiGridAdapter.1
                        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
                        public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                            rect.right = AndroidUtilities.dp(8.0f);
                        }
                    });
                    trendingListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiView$EmojiGridAdapter$$ExternalSyntheticLambda1
                        @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                        public final void onItemClick(View view, int i2) {
                            EmojiView.EmojiGridAdapter.this.lambda$onCreateViewHolder$0(view, i2);
                        }
                    });
                    stickerSetNameCell = trendingListView;
                } else if (i == 5) {
                    EmojiView emojiView4 = EmojiView.this;
                    imageViewEmoji = new EmojiPackHeader(emojiView4.getContext());
                } else if (i == 6) {
                    imageViewEmoji = new EmojiPackExpand(EmojiView.this.getContext(), EmojiView.this.resourcesProvider);
                } else {
                    imageViewEmoji = new View(EmojiView.this.getContext());
                    imageViewEmoji.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                }
                imageViewEmoji = stickerSetNameCell;
            } else {
                EmojiView emojiView5 = EmojiView.this;
                imageViewEmoji = new ImageViewEmoji(emojiView5.getContext());
            }
            return new RecyclerListView.Holder(imageViewEmoji);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, @SuppressLint({"RecyclerView"}) int i) {
            String str;
            String str2;
            Long l;
            TLRPC$Document tLRPC$Document;
            String str3;
            TLRPC$Document tLRPC$Document2;
            String str4;
            String str5;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            boolean z2 = true;
            EmojiPack emojiPack = null;
            if (itemViewType != 0) {
                if (itemViewType == 1) {
                    StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                    stickerSetNameCell.position = i;
                    int i2 = this.positionToSection.get(i);
                    if (i != this.trendingHeaderRow) {
                        if (i2 >= EmojiView.this.emojiTitles.length) {
                            try {
                                str5 = ((EmojiPack) EmojiView.this.emojipacksProcessed.get(i2 - EmojiView.this.emojiTitles.length)).set.title;
                            } catch (Exception unused) {
                                str5 = "";
                            }
                        } else {
                            str5 = EmojiView.this.emojiTitles[i2];
                        }
                    } else {
                        str5 = LocaleController.getString("FeaturedEmojiPacks", R.string.FeaturedEmojiPacks);
                    }
                    stickerSetNameCell.setText(str5, 0);
                    return;
                } else if (itemViewType != 5) {
                    if (itemViewType != 6) {
                        return;
                    } else {
                        EmojiPackExpand emojiPackExpand = (EmojiPackExpand) viewHolder.itemView;
                        int i3 = this.positionToExpand.get(i);
                        int spanCount = EmojiView.this.emojiLayoutManager.getSpanCount() * 3;
                        if (i3 >= 0 && i3 < EmojiView.this.emojipacksProcessed.size()) {
                            emojiPack = (EmojiPack) EmojiView.this.emojipacksProcessed.get(i3);
                        }
                        if (emojiPack == null) {
                            return;
                        }
                        emojiPackExpand.textView.setText("+" + ((emojiPack.documents.size() - spanCount) + 1));
                        return;
                    }
                } else {
                    EmojiPackHeader emojiPackHeader = (EmojiPackHeader) viewHolder.itemView;
                    int length = this.positionToSection.get(i) - EmojiView.this.emojiTitles.length;
                    EmojiPack emojiPack2 = (EmojiPack) EmojiView.this.emojipacksProcessed.get(length);
                    int i4 = length - 1;
                    if (i4 >= 0) {
                        emojiPack = (EmojiPack) EmojiView.this.emojipacksProcessed.get(i4);
                    }
                    if (emojiPack2 != null && emojiPack2.featured && (emojiPack == null || emojiPack.free || !emojiPack.installed || UserConfig.getInstance(EmojiView.this.currentAccount).isPremium())) {
                        z = true;
                    }
                    emojiPackHeader.setStickerSet(emojiPack2, z);
                    return;
                }
            }
            ImageViewEmoji imageViewEmoji = (ImageViewEmoji) viewHolder.itemView;
            imageViewEmoji.position = i;
            imageViewEmoji.pack = null;
            if (EmojiView.this.needEmojiSearch) {
                i--;
            }
            if (this.trendingRow >= 0) {
                i -= 2;
            }
            int size = EmojiView.this.getRecentEmoji().size();
            if (i < size) {
                String str6 = EmojiView.this.getRecentEmoji().get(i);
                if (str6 != null && str6.startsWith("animated_")) {
                    try {
                        l = Long.valueOf(Long.parseLong(str6.substring(9)));
                        str = null;
                        str4 = null;
                    } catch (Exception unused2) {
                    }
                    str3 = str4;
                    tLRPC$Document2 = null;
                }
                str = str6;
                str4 = str;
                l = null;
                str3 = str4;
                tLRPC$Document2 = null;
            } else {
                int i5 = 0;
                while (true) {
                    String[][] strArr = EmojiData.dataColored;
                    if (i5 >= strArr.length) {
                        str = null;
                        break;
                    }
                    int length2 = strArr[i5].length + 1 + size;
                    if (i < length2) {
                        String str7 = strArr[i5][(i - size) - 1];
                        String str8 = Emoji.emojiColor.get(str7);
                        if (str8 != null) {
                            str = EmojiView.addColorToCode(str7, str8);
                            str2 = str7;
                        } else {
                            str = str7;
                        }
                    } else {
                        i5++;
                        size = length2;
                    }
                }
                str2 = str;
                if (str2 == null) {
                    boolean isPremium = UserConfig.getInstance(EmojiView.this.currentAccount).isPremium();
                    int spanCount2 = EmojiView.this.emojiLayoutManager.getSpanCount() * 3;
                    for (int i6 = 0; i6 < this.packStartPosition.size(); i6++) {
                        EmojiPack emojiPack3 = (EmojiPack) EmojiView.this.emojipacksProcessed.get(i6);
                        int intValue = this.packStartPosition.get(i6).intValue() + 1;
                        int min = ((!emojiPack3.installed || emojiPack3.featured || (!emojiPack3.free && !isPremium)) && !emojiPack3.expanded) ? Math.min(spanCount2, emojiPack3.documents.size()) : emojiPack3.documents.size();
                        int i7 = imageViewEmoji.position;
                        if (i7 >= intValue && i7 - intValue < min) {
                            imageViewEmoji.pack = emojiPack3;
                            TLRPC$Document tLRPC$Document3 = emojiPack3.documents.get(imageViewEmoji.position - intValue);
                            tLRPC$Document = tLRPC$Document3;
                            l = tLRPC$Document3 == null ? null : Long.valueOf(tLRPC$Document3.id);
                            str3 = str2;
                            tLRPC$Document2 = tLRPC$Document;
                            z2 = false;
                        }
                    }
                }
                l = null;
                tLRPC$Document = null;
                str3 = str2;
                tLRPC$Document2 = tLRPC$Document;
                z2 = false;
            }
            if (l != null) {
                imageViewEmoji.setPadding(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f));
            } else {
                imageViewEmoji.setPadding(0, 0, 0, 0);
            }
            if (l != null) {
                imageViewEmoji.setImageDrawable(null, z2);
                if (imageViewEmoji.getSpan() == null || imageViewEmoji.getSpan().getDocumentId() != l.longValue()) {
                    if (tLRPC$Document2 != null) {
                        imageViewEmoji.setSpan(new AnimatedEmojiSpan(tLRPC$Document2, (Paint.FontMetricsInt) null));
                    } else {
                        imageViewEmoji.setSpan(new AnimatedEmojiSpan(l.longValue(), (Paint.FontMetricsInt) null));
                    }
                }
            } else {
                imageViewEmoji.setImageDrawable(Emoji.getEmojiBigDrawable(str), z2);
                imageViewEmoji.setSpan(null);
            }
            imageViewEmoji.setTag(str3);
            imageViewEmoji.setContentDescription(str);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == this.trendingRow) {
                return 4;
            }
            if (i == this.trendingHeaderRow) {
                return 1;
            }
            if (this.positionToSection.indexOfKey(i) >= 0) {
                return this.positionToSection.get(i) >= EmojiData.dataColored.length ? 5 : 1;
            } else if (EmojiView.this.needEmojiSearch && i == 0) {
                return 2;
            } else {
                if (this.positionToUnlock.indexOfKey(i) >= 0) {
                    return 3;
                }
                return this.positionToExpand.indexOfKey(i) >= 0 ? 6 : 0;
            }
        }

        public void processEmoji(boolean z) {
            boolean z2;
            EmojiView.this.emojipacksProcessed.clear();
            if (!EmojiView.this.allowAnimatedEmoji) {
                return;
            }
            MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
            if (z || this.frozenEmojiPacks == null) {
                this.frozenEmojiPacks = new ArrayList<>(mediaDataController.getStickerSets(5));
            }
            ArrayList<TLRPC$TL_messages_stickerSet> arrayList = this.frozenEmojiPacks;
            boolean isPremium = UserConfig.getInstance(EmojiView.this.currentAccount).isPremium();
            if (!isPremium) {
                int i = 0;
                while (i < arrayList.size()) {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = arrayList.get(i);
                    if (tLRPC$TL_messages_stickerSet != null && !MessageObject.isPremiumEmojiPack(tLRPC$TL_messages_stickerSet)) {
                        EmojiPack emojiPack = new EmojiPack();
                        emojiPack.set = tLRPC$TL_messages_stickerSet.set;
                        emojiPack.documents = new ArrayList<>(tLRPC$TL_messages_stickerSet.documents);
                        emojiPack.free = true;
                        emojiPack.installed = mediaDataController.isStickerPackInstalled(tLRPC$TL_messages_stickerSet.set.id);
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
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = arrayList.get(i2);
                if (isPremium) {
                    EmojiPack emojiPack2 = new EmojiPack();
                    TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet2.set;
                    emojiPack2.set = tLRPC$StickerSet;
                    emojiPack2.documents = tLRPC$TL_messages_stickerSet2.documents;
                    emojiPack2.free = false;
                    emojiPack2.installed = mediaDataController.isStickerPackInstalled(tLRPC$StickerSet.id);
                    emojiPack2.featured = false;
                    emojiPack2.expanded = true;
                    EmojiView.this.emojipacksProcessed.add(emojiPack2);
                } else {
                    ArrayList arrayList2 = new ArrayList();
                    ArrayList arrayList3 = new ArrayList();
                    if (tLRPC$TL_messages_stickerSet2 != null && tLRPC$TL_messages_stickerSet2.documents != null) {
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
                        emojiPack3.installed = mediaDataController.isStickerPackInstalled(tLRPC$TL_messages_stickerSet2.set.id);
                        emojiPack3.featured = false;
                        emojiPack3.expanded = true;
                        EmojiView.this.emojipacksProcessed.add(emojiPack3);
                    }
                    if (arrayList3.size() > 0) {
                        EmojiPack emojiPack4 = new EmojiPack();
                        emojiPack4.set = tLRPC$TL_messages_stickerSet2.set;
                        emojiPack4.documents = new ArrayList<>(arrayList3);
                        emojiPack4.free = false;
                        emojiPack4.installed = mediaDataController.isStickerPackInstalled(tLRPC$TL_messages_stickerSet2.set.id);
                        emojiPack4.featured = false;
                        emojiPack4.expanded = EmojiView.this.expandedEmojiSets.contains(Long.valueOf(emojiPack4.set.id));
                        EmojiView.this.emojipacksProcessed.add(emojiPack4);
                    }
                }
            }
            for (int i4 = 0; i4 < EmojiView.this.featuredEmojiSets.size(); i4++) {
                TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) EmojiView.this.featuredEmojiSets.get(i4);
                EmojiPack emojiPack5 = new EmojiPack();
                emojiPack5.installed = mediaDataController.isStickerPackInstalled(tLRPC$StickerSetCovered.set.id);
                emojiPack5.set = tLRPC$StickerSetCovered.set;
                emojiPack5.documents = tLRPC$StickerSetCovered instanceof TLRPC$TL_stickerSetFullCovered ? ((TLRPC$TL_stickerSetFullCovered) tLRPC$StickerSetCovered).documents : tLRPC$StickerSetCovered.covers;
                int i5 = 0;
                while (true) {
                    if (i5 >= emojiPack5.documents.size()) {
                        z2 = false;
                        break;
                    } else if (!MessageObject.isFreeEmoji(emojiPack5.documents.get(i5))) {
                        z2 = true;
                        break;
                    } else {
                        i5++;
                    }
                }
                emojiPack5.free = !z2;
                emojiPack5.expanded = EmojiView.this.expandedEmojiSets.contains(Long.valueOf(emojiPack5.set.id));
                emojiPack5.featured = true;
                EmojiView.this.emojipacksProcessed.add(emojiPack5);
            }
            if (EmojiView.this.emojiTabs == null) {
                return;
            }
            EmojiView.this.emojiTabs.updateEmojiPacks(EmojiView.this.getEmojipacks());
        }

        public void expand(int i, View view) {
            int i2 = this.positionToExpand.get(i);
            if (i2 < 0 || i2 >= EmojiView.this.emojipacksProcessed.size()) {
                return;
            }
            EmojiPack emojiPack = (EmojiPack) EmojiView.this.emojipacksProcessed.get(i2);
            if (emojiPack.expanded) {
                return;
            }
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
            processEmoji(false);
            updateRows();
            if (valueOf == null || num == null) {
                return;
            }
            EmojiView.this.animateExpandFromButton = view;
            EmojiView.this.animateExpandFromPosition = valueOf.intValue();
            EmojiView.this.animateExpandToPosition = valueOf.intValue() + num.intValue();
            EmojiView.this.animateExpandStartTime = SystemClock.elapsedRealtime();
            notifyItemRangeInserted(valueOf.intValue(), num.intValue());
            notifyItemChanged(valueOf.intValue());
            if (!z) {
                return;
            }
            final int intValue2 = valueOf.intValue();
            final float f = num.intValue() > spanCount / 2 ? 1.5f : 4.0f;
            EmojiView.this.post(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$EmojiGridAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.EmojiGridAdapter.this.lambda$expand$1(f, intValue2);
                }
            });
        }

        public /* synthetic */ void lambda$expand$1(float f, int i) {
            try {
                LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(EmojiView.this.emojiGridView.getContext(), 0, f);
                linearSmoothScrollerCustom.setTargetPosition(i);
                EmojiView.this.emojiLayoutManager.startSmoothScroll(linearSmoothScrollerCustom);
            } catch (Exception e) {
                FileLog.e(e);
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
                    if (i4 < strArr2[i2].length) {
                        this.rowHashCodes.add(Integer.valueOf(strArr2[i2][i4].hashCode()));
                        i4++;
                    }
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
                    objArr[1] = Long.valueOf(tLRPC$StickerSet == null ? i5 : tLRPC$StickerSet.id);
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

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            notifyDataSetChanged(false);
        }

        public void notifyDataSetChanged(boolean z) {
            final ArrayList arrayList = new ArrayList(this.rowHashCodes);
            MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
            ArrayList<TLRPC$StickerSetCovered> featuredEmojiSets = mediaDataController.getFeaturedEmojiSets();
            EmojiView.this.featuredEmojiSets.clear();
            int size = featuredEmojiSets.size();
            for (int i = 0; i < size; i++) {
                TLRPC$StickerSetCovered tLRPC$StickerSetCovered = featuredEmojiSets.get(i);
                if (!mediaDataController.isStickerPackInstalled(tLRPC$StickerSetCovered.set.id) || EmojiView.this.installedEmojiSets.contains(Long.valueOf(tLRPC$StickerSetCovered.set.id))) {
                    EmojiView.this.featuredEmojiSets.add(tLRPC$StickerSetCovered);
                }
            }
            processEmoji(z);
            updateRows();
            if (EmojiView.this.trendingEmojiAdapter != null) {
                EmojiView.this.trendingEmojiAdapter.notifyDataSetChanged();
            }
            DiffUtil.calculateDiff(new DiffUtil.Callback() { // from class: org.telegram.ui.Components.EmojiView.EmojiGridAdapter.2
                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public boolean areContentsTheSame(int i2, int i3) {
                    return true;
                }

                {
                    EmojiGridAdapter.this = this;
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public int getOldListSize() {
                    return arrayList.size();
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public int getNewListSize() {
                    return EmojiGridAdapter.this.rowHashCodes.size();
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public boolean areItemsTheSame(int i2, int i3) {
                    return ((Integer) arrayList.get(i2)).equals(EmojiGridAdapter.this.rowHashCodes.get(i3));
                }
            }, false).dispatchUpdatesTo(this);
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

    /* loaded from: classes3.dex */
    public class EmojiSearchAdapter extends RecyclerListView.SelectionAdapter {
        private String lastSearchAlias;
        private String lastSearchEmojiString;
        private ArrayList<MediaDataController.KeywordResult> result;
        private Runnable searchRunnable;
        private boolean searchWas;

        private EmojiSearchAdapter() {
            EmojiView.this = r1;
            this.result = new ArrayList<>();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1753onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            if (i == 0) {
                EmojiView emojiView = EmojiView.this;
                frameLayout = new ImageViewEmoji(emojiView.getContext());
            } else if (i == 1) {
                View view = new View(EmojiView.this.getContext());
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                frameLayout = view;
            } else {
                FrameLayout frameLayout2 = new FrameLayout(EmojiView.this.getContext()) { // from class: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.1
                    {
                        EmojiSearchAdapter.this = this;
                    }

                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int i2, int i3) {
                        int dp;
                        View view2 = (View) EmojiView.this.getParent();
                        if (view2 != null) {
                            dp = (int) (view2.getMeasuredHeight() - EmojiView.this.getY());
                        } else {
                            dp = AndroidUtilities.dp(120.0f);
                        }
                        super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(dp - EmojiView.this.searchFieldHeight, NUM));
                    }
                };
                TextView textView = new TextView(EmojiView.this.getContext());
                textView.setText(LocaleController.getString("NoEmojiFound", R.string.NoEmojiFound));
                textView.setTextSize(1, 16.0f);
                textView.setTextColor(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"));
                frameLayout2.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
                ImageView imageView = new ImageView(EmojiView.this.getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setImageResource(R.drawable.msg_emoji_question);
                imageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
                frameLayout2.addView(imageView, LayoutHelper.createFrame(48, 48, 85));
                imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2
                    {
                        EmojiSearchAdapter.this = this;
                    }

                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        Object obj;
                        boolean[] zArr = new boolean[1];
                        BottomSheet.Builder builder = new BottomSheet.Builder(EmojiView.this.getContext());
                        LinearLayout linearLayout = new LinearLayout(EmojiView.this.getContext());
                        linearLayout.setOrientation(1);
                        linearLayout.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                        ImageView imageView2 = new ImageView(EmojiView.this.getContext());
                        imageView2.setImageResource(R.drawable.smiles_info);
                        linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 49, 0, 15, 0, 0));
                        TextView textView2 = new TextView(EmojiView.this.getContext());
                        textView2.setText(LocaleController.getString("EmojiSuggestions", R.string.EmojiSuggestions));
                        textView2.setTextSize(1, 15.0f);
                        textView2.setTextColor(EmojiView.this.getThemedColor("dialogTextBlue2"));
                        int i2 = 5;
                        textView2.setGravity(LocaleController.isRTL ? 5 : 3);
                        textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 51, 0, 24, 0, 0));
                        TextView textView3 = new TextView(EmojiView.this.getContext());
                        textView3.setText(AndroidUtilities.replaceTags(LocaleController.getString("EmojiSuggestionsInfo", R.string.EmojiSuggestionsInfo)));
                        textView3.setTextSize(1, 15.0f);
                        textView3.setTextColor(EmojiView.this.getThemedColor("dialogTextBlack"));
                        textView3.setGravity(LocaleController.isRTL ? 5 : 3);
                        linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 51, 0, 11, 0, 0));
                        TextView textView4 = new TextView(EmojiView.this.getContext());
                        int i3 = R.string.EmojiSuggestionsUrl;
                        Object[] objArr = new Object[1];
                        if (EmojiSearchAdapter.this.lastSearchAlias == null) {
                            obj = EmojiView.this.lastSearchKeyboardLanguage;
                        } else {
                            obj = EmojiSearchAdapter.this.lastSearchAlias;
                        }
                        objArr[0] = obj;
                        textView4.setText(LocaleController.formatString("EmojiSuggestionsUrl", i3, objArr));
                        textView4.setTextSize(1, 15.0f);
                        textView4.setTextColor(EmojiView.this.getThemedColor("dialogTextLink"));
                        if (!LocaleController.isRTL) {
                            i2 = 3;
                        }
                        textView4.setGravity(i2);
                        linearLayout.addView(textView4, LayoutHelper.createLinear(-2, -2, 51, 0, 18, 0, 16));
                        textView4.setOnClickListener(new AnonymousClass1(zArr, builder));
                        builder.setCustomView(linearLayout);
                        builder.show();
                    }

                    /* renamed from: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 */
                    /* loaded from: classes3.dex */
                    public class AnonymousClass1 implements View.OnClickListener {
                        final /* synthetic */ BottomSheet.Builder val$builder;
                        final /* synthetic */ boolean[] val$loadingUrl;

                        AnonymousClass1(boolean[] zArr, BottomSheet.Builder builder) {
                            AnonymousClass2.this = r1;
                            this.val$loadingUrl = zArr;
                            this.val$builder = builder;
                        }

                        @Override // android.view.View.OnClickListener
                        public void onClick(View view) {
                            String str;
                            boolean[] zArr = this.val$loadingUrl;
                            if (zArr[0]) {
                                return;
                            }
                            zArr[0] = true;
                            final AlertDialog[] alertDialogArr = {new AlertDialog(EmojiView.this.getContext(), 3)};
                            TLRPC$TL_messages_getEmojiURL tLRPC$TL_messages_getEmojiURL = new TLRPC$TL_messages_getEmojiURL();
                            if (EmojiSearchAdapter.this.lastSearchAlias == null) {
                                str = EmojiView.this.lastSearchKeyboardLanguage[0];
                            } else {
                                str = EmojiSearchAdapter.this.lastSearchAlias;
                            }
                            tLRPC$TL_messages_getEmojiURL.lang_code = str;
                            ConnectionsManager connectionsManager = ConnectionsManager.getInstance(EmojiView.this.currentAccount);
                            final BottomSheet.Builder builder = this.val$builder;
                            final int sendRequest = connectionsManager.sendRequest(tLRPC$TL_messages_getEmojiURL, new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda3
                                @Override // org.telegram.tgnet.RequestDelegate
                                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                    EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$1(alertDialogArr, builder, tLObject, tLRPC$TL_error);
                                }
                            });
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$3(alertDialogArr, sendRequest);
                                }
                            }, 1000L);
                        }

                        public /* synthetic */ void lambda$onClick$1(final AlertDialog[] alertDialogArr, final BottomSheet.Builder builder, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda2
                                @Override // java.lang.Runnable
                                public final void run() {
                                    EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$0(alertDialogArr, tLObject, builder);
                                }
                            });
                        }

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

                        public /* synthetic */ void lambda$onClick$3(AlertDialog[] alertDialogArr, final int i) {
                            if (alertDialogArr[0] == null) {
                                return;
                            }
                            alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1$$ExternalSyntheticLambda0
                                @Override // android.content.DialogInterface.OnCancelListener
                                public final void onCancel(DialogInterface dialogInterface) {
                                    EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$2(i, dialogInterface);
                                }
                            });
                            alertDialogArr[0].show();
                        }

                        public /* synthetic */ void lambda$onClick$2(int i, DialogInterface dialogInterface) {
                            ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(i, true);
                        }
                    }
                });
                frameLayout2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                frameLayout = frameLayout2;
            }
            return new RecyclerListView.Holder(frameLayout);
        }

        /* JADX WARN: Removed duplicated region for block: B:53:0x0058  */
        /* JADX WARN: Removed duplicated region for block: B:54:0x006e  */
        /* JADX WARN: Removed duplicated region for block: B:56:0x0073  */
        /* JADX WARN: Removed duplicated region for block: B:61:0x0099  */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r9, int r10) {
            /*
                r8 = this;
                int r0 = r9.getItemViewType()
                if (r0 == 0) goto L8
                goto La6
            L8:
                android.view.View r9 = r9.itemView
                org.telegram.ui.Components.EmojiView$ImageViewEmoji r9 = (org.telegram.ui.Components.EmojiView.ImageViewEmoji) r9
                r0 = 0
                org.telegram.ui.Components.EmojiView.ImageViewEmoji.access$17002(r9, r0)
                int r10 = r10 + (-1)
                java.util.ArrayList<org.telegram.messenger.MediaDataController$KeywordResult> r1 = r8.result
                boolean r1 = r1.isEmpty()
                r2 = 0
                if (r1 == 0) goto L2d
                boolean r1 = r8.searchWas
                if (r1 != 0) goto L2d
                org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r1 = r1.getRecentEmoji()
                java.lang.Object r10 = r1.get(r10)
                java.lang.String r10 = (java.lang.String) r10
                r1 = 1
                goto L38
            L2d:
                java.util.ArrayList<org.telegram.messenger.MediaDataController$KeywordResult> r1 = r8.result
                java.lang.Object r10 = r1.get(r10)
                org.telegram.messenger.MediaDataController$KeywordResult r10 = (org.telegram.messenger.MediaDataController.KeywordResult) r10
                java.lang.String r10 = r10.emoji
                r1 = 0
            L38:
                if (r10 == 0) goto L53
                java.lang.String r3 = "animated_"
                boolean r3 = r10.startsWith(r3)
                if (r3 == 0) goto L53
                r3 = 9
                java.lang.String r3 = r10.substring(r3)     // Catch: java.lang.Exception -> L53
                long r3 = java.lang.Long.parseLong(r3)     // Catch: java.lang.Exception -> L53
                java.lang.Long r10 = java.lang.Long.valueOf(r3)     // Catch: java.lang.Exception -> L53
                r3 = r0
                r4 = r3
                goto L56
            L53:
                r3 = r10
                r4 = r3
                r10 = r0
            L56:
                if (r10 == 0) goto L6e
                r2 = 1077936128(0x40400000, float:3.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r9.setPadding(r5, r6, r7, r2)
                goto L71
            L6e:
                r9.setPadding(r2, r2, r2, r2)
            L71:
                if (r10 == 0) goto L99
                r9.setImageDrawable(r0, r1)
                org.telegram.ui.Components.AnimatedEmojiSpan r1 = r9.getSpan()
                if (r1 == 0) goto L8c
                org.telegram.ui.Components.AnimatedEmojiSpan r1 = r9.getSpan()
                long r1 = r1.getDocumentId()
                long r5 = r10.longValue()
                int r3 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                if (r3 == 0) goto La3
            L8c:
                org.telegram.ui.Components.AnimatedEmojiSpan r1 = new org.telegram.ui.Components.AnimatedEmojiSpan
                long r2 = r10.longValue()
                r1.<init>(r2, r0)
                r9.setSpan(r1)
                goto La3
            L99:
                android.graphics.drawable.Drawable r10 = org.telegram.messenger.Emoji.getEmojiBigDrawable(r3)
                r9.setImageDrawable(r10, r1)
                r9.setSpan(r0)
            La3:
                r9.setTag(r4)
            La6:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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
                Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.3
                    {
                        EmojiSearchAdapter.this = this;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        EmojiView.this.emojiSearchField.progressDrawable.startAnimation();
                        final String str2 = EmojiSearchAdapter.this.lastSearchEmojiString;
                        String[] currentKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        if (!Arrays.equals(EmojiView.this.lastSearchKeyboardLanguage, currentKeyboardLanguage)) {
                            MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(currentKeyboardLanguage);
                        }
                        EmojiView.this.lastSearchKeyboardLanguage = currentKeyboardLanguage;
                        MediaDataController.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, EmojiSearchAdapter.this.lastSearchEmojiString, false, new MediaDataController.KeywordResultCallback() { // from class: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.3.1
                            {
                                AnonymousClass3.this = this;
                            }

                            @Override // org.telegram.messenger.MediaDataController.KeywordResultCallback
                            public void run(ArrayList<MediaDataController.KeywordResult> arrayList, String str3) {
                                if (str2.equals(EmojiSearchAdapter.this.lastSearchEmojiString)) {
                                    EmojiSearchAdapter.this.lastSearchAlias = str3;
                                    EmojiView.this.emojiSearchField.progressDrawable.stopAnimation();
                                    EmojiSearchAdapter.this.searchWas = true;
                                    if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                                        EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiSearchAdapter);
                                    }
                                    EmojiSearchAdapter.this.result = arrayList;
                                    EmojiSearchAdapter.this.notifyDataSetChanged();
                                }
                            }
                        }, true);
                    }
                };
                this.searchRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, 300L);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class EmojiPagesAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        private EmojiPagesAdapter() {
            EmojiView.this = r1;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        @Override // org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider
        public boolean canScrollToTab(int i) {
            boolean z = true;
            if ((i == 1 || i == 2) && EmojiView.this.currentChatId != 0) {
                EmojiView emojiView = EmojiView.this;
                if (i != 1) {
                    z = false;
                }
                emojiView.showStickerBanHint(z);
                return false;
            }
            return true;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return EmojiView.this.currentTabs.size();
        }

        @Override // org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider
        public Drawable getPageIconDrawable(int i) {
            return EmojiView.this.tabIcons[i];
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public CharSequence getPageTitle(int i) {
            if (i != 0) {
                if (i == 1) {
                    return LocaleController.getString("AccDescrGIFs", R.string.AccDescrGIFs);
                }
                if (i == 2) {
                    return LocaleController.getString("AccDescrStickers", R.string.AccDescrStickers);
                }
                return null;
            }
            return LocaleController.getString("Emoji", R.string.Emoji);
        }

        @Override // org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider
        public void customOnDraw(Canvas canvas, int i) {
            if (i != 2 || MediaDataController.getInstance(EmojiView.this.currentAccount).getUnreadStickerSets().isEmpty() || EmojiView.this.dotPaint == null) {
                return;
            }
            canvas.drawCircle((canvas.getWidth() / 2) + AndroidUtilities.dp(9.0f), (canvas.getHeight() / 2) - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), EmojiView.this.dotPaint);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        /* renamed from: instantiateItem */
        public Object mo1567instantiateItem(ViewGroup viewGroup, int i) {
            View view = ((Tab) EmojiView.this.currentTabs.get(i)).view;
            viewGroup.addView(view);
            return view;
        }
    }

    /* loaded from: classes3.dex */
    public class GifAdapter extends RecyclerListView.SelectionAdapter {
        private TLRPC$User bot;
        private final Context context;
        private int firstResultItem;
        private int itemsCount;
        private String lastSearchImageString;
        private boolean lastSearchIsEmoji;
        private final int maxRecentRowsCount;
        private String nextSearchOffset;
        private final GifProgressEmptyView progressEmptyView;
        private int recentItemsCount;
        private int reqId;
        private ArrayList<TLRPC$BotInlineResult> results;
        private HashMap<String, TLRPC$BotInlineResult> resultsMap;
        private boolean searchEndReached;
        private Runnable searchRunnable;
        private boolean searchingUser;
        private boolean showTrendingWhenSearchEmpty;
        private int trendingSectionItem;
        private final boolean withRecent;

        public GifAdapter(EmojiView emojiView, Context context) {
            this(context, false, 0);
        }

        public GifAdapter(EmojiView emojiView, Context context, boolean z) {
            this(context, z, z ? Integer.MAX_VALUE : 0);
        }

        public GifAdapter(Context context, boolean z, int i) {
            EmojiView.this = r2;
            this.results = new ArrayList<>();
            this.resultsMap = new HashMap<>();
            this.trendingSectionItem = -1;
            this.firstResultItem = -1;
            this.context = context;
            this.withRecent = z;
            this.maxRecentRowsCount = i;
            this.progressEmptyView = z ? null : new GifProgressEmptyView(context);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.itemsCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0) {
                return 1;
            }
            boolean z = this.withRecent;
            if (z && i == this.trendingSectionItem) {
                return 2;
            }
            return (z || !this.results.isEmpty()) ? 0 : 3;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1753onCreateViewHolder(ViewGroup viewGroup, int i) {
            StickerSetNameCell stickerSetNameCell;
            if (i == 0) {
                ContextLinkCell contextLinkCell = new ContextLinkCell(this.context);
                contextLinkCell.setCanPreviewGif(true);
                stickerSetNameCell = contextLinkCell;
            } else if (i == 1) {
                View view = new View(EmojiView.this.getContext());
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                stickerSetNameCell = view;
            } else if (i == 2) {
                StickerSetNameCell stickerSetNameCell2 = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                stickerSetNameCell2.setText(LocaleController.getString("FeaturedGifs", R.string.FeaturedGifs), 0);
                RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, -2);
                ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = AndroidUtilities.dp(2.5f);
                ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = AndroidUtilities.dp(5.5f);
                stickerSetNameCell2.setLayoutParams(layoutParams);
                stickerSetNameCell = stickerSetNameCell2;
            } else {
                View view2 = this.progressEmptyView;
                view2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                stickerSetNameCell = view2;
            }
            return new RecyclerListView.Holder(stickerSetNameCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() != 0) {
                return;
            }
            ContextLinkCell contextLinkCell = (ContextLinkCell) viewHolder.itemView;
            int i2 = this.firstResultItem;
            if (i2 < 0 || i < i2) {
                contextLinkCell.setGif((TLRPC$Document) EmojiView.this.recentGifs.get(i - 1), false);
            } else {
                contextLinkCell.setLink(this.results.get(i - i2), this.bot, true, false, false, true);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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
            } else if (this.withRecent) {
            } else {
                this.itemsCount++;
            }
        }

        private void updateRecentItemsCount() {
            int i;
            if (!this.withRecent || (i = this.maxRecentRowsCount) == 0) {
                return;
            }
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
                    int min = Math.min(spanCount, (int) Math.floor(spanCount * (((fixSize.width / fixSize.height) * dp) / measuredWidth)));
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
                if (i4 >= this.maxRecentRowsCount) {
                    return;
                }
                this.recentItemsCount += i3;
            }
        }

        public void loadTrendingGifs() {
            search("", "", true, true, true);
        }

        private void searchBotUser() {
            if (this.searchingUser) {
                return;
            }
            this.searchingUser = true;
            TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
            tLRPC$TL_contacts_resolveUsername.username = MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot;
            ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_contacts_resolveUsername, new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    EmojiView.GifAdapter.this.lambda$searchBotUser$1(tLObject, tLRPC$TL_error);
                }
            });
        }

        public /* synthetic */ void lambda$searchBotUser$1(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiView.GifAdapter.this.lambda$searchBotUser$0(tLObject);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$searchBotUser$0(TLObject tLObject) {
            TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
            MessagesController.getInstance(EmojiView.this.currentAccount).putUsers(tLRPC$TL_contacts_resolvedPeer.users, false);
            MessagesController.getInstance(EmojiView.this.currentAccount).putChats(tLRPC$TL_contacts_resolvedPeer.chats, false);
            MessagesStorage.getInstance(EmojiView.this.currentAccount).putUsersAndChats(tLRPC$TL_contacts_resolvedPeer.users, tLRPC$TL_contacts_resolvedPeer.chats, true, true);
            String str = this.lastSearchImageString;
            this.lastSearchImageString = null;
            search(str, "", false);
        }

        public void search(final String str) {
            if (this.withRecent) {
                return;
            }
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
                if (!this.showTrendingWhenSearchEmpty) {
                    int currentPosition = EmojiView.this.gifTabs.getCurrentPosition();
                    if (currentPosition == EmojiView.this.gifRecentTabNum || currentPosition == EmojiView.this.gifTrendingTabNum) {
                        if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                            return;
                        }
                        EmojiView.this.gifGridView.setAdapter(EmojiView.this.gifAdapter);
                        return;
                    }
                    searchEmoji(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchEmojies.get(currentPosition - EmojiView.this.gifFirstEmojiTabNum));
                    return;
                }
                loadTrendingGifs();
                return;
            }
            String lowerCase = str.toLowerCase();
            this.lastSearchImageString = lowerCase;
            if (TextUtils.isEmpty(lowerCase)) {
                return;
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.GifAdapter.this.lambda$search$2(str);
                }
            };
            this.searchRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 300L);
        }

        public /* synthetic */ void lambda$search$2(String str) {
            search(str, "", true);
        }

        public void searchEmoji(String str) {
            if (this.lastSearchIsEmoji && TextUtils.equals(this.lastSearchImageString, str)) {
                EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(1, 0);
            } else {
                search(str, "", true, true, true);
            }
        }

        protected void search(String str, String str2, boolean z) {
            search(str, str2, z, false, false);
        }

        protected void search(final String str, final String str2, final boolean z, final boolean z2, final boolean z3) {
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
            if (!(userOrChat instanceof TLRPC$User)) {
                if (!z) {
                    return;
                }
                searchBotUser();
                if (this.withRecent) {
                    return;
                }
                EmojiView.this.gifSearchField.progressDrawable.startAnimation();
                return;
            }
            if (!this.withRecent && TextUtils.isEmpty(str2)) {
                EmojiView.this.gifSearchField.progressDrawable.startAnimation();
            }
            this.bot = (TLRPC$User) userOrChat;
            final String str3 = "gif_search_" + str + "_" + str2;
            RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda4
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    EmojiView.GifAdapter.this.lambda$search$4(str, str2, z, z2, z3, str3, tLObject, tLRPC$TL_error);
                }
            };
            if (!z3 && !this.withRecent && z2 && TextUtils.isEmpty(str2)) {
                this.results.clear();
                this.resultsMap.clear();
                if (EmojiView.this.gifGridView.getAdapter() != this) {
                    EmojiView.this.gifGridView.setAdapter(this);
                }
                notifyDataSetChanged();
                EmojiView.this.scrollGifsToTop();
            }
            if (!z3 || !EmojiView.this.gifCache.containsKey(str3)) {
                if (EmojiView.this.gifSearchPreloader.isLoading(str3)) {
                    return;
                }
                if (z3) {
                    this.reqId = -1;
                    MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(str3, requestDelegate);
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
                this.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_messages_getInlineBotResults, requestDelegate, 2);
                return;
            }
            lambda$search$3(str, str2, z, z2, true, str3, (TLObject) EmojiView.this.gifCache.get(str3));
        }

        public /* synthetic */ void lambda$search$4(final String str, final String str2, final boolean z, final boolean z2, final boolean z3, final String str3, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$GifAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.GifAdapter.this.lambda$search$3(str, str2, z, z2, z3, str3, tLObject);
                }
            });
        }

        /* renamed from: processResponse */
        public void lambda$search$3(String str, String str2, boolean z, boolean z2, boolean z3, String str3, TLObject tLObject) {
            if (str == null || !str.equals(this.lastSearchImageString)) {
                return;
            }
            boolean z4 = false;
            this.reqId = 0;
            if (z3 && (!(tLObject instanceof TLRPC$messages_BotResults) || ((TLRPC$messages_BotResults) tLObject).results.isEmpty())) {
                search(str, str2, z, z2, false);
                return;
            }
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
            if (this.withRecent) {
                return;
            }
            if (EmojiView.this.gifGridView.getAdapter() != this) {
                EmojiView.this.gifGridView.setAdapter(this);
            }
            if (!z2 || TextUtils.isEmpty(str) || !TextUtils.isEmpty(str2)) {
                return;
            }
            EmojiView.this.scrollGifsToTop();
        }
    }

    /* loaded from: classes3.dex */
    public class GifSearchPreloader {
        private final List<String> loadingKeys;

        private GifSearchPreloader() {
            EmojiView.this = r1;
            this.loadingKeys = new ArrayList();
        }

        public boolean isLoading(String str) {
            return this.loadingKeys.contains(str);
        }

        public void preload(String str) {
            preload(str, "", true);
        }

        private void preload(final String str, final String str2, final boolean z) {
            final String str3 = "gif_search_" + str + "_" + str2;
            if (!z || !EmojiView.this.gifCache.containsKey(str3)) {
                RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.Components.EmojiView$GifSearchPreloader$$ExternalSyntheticLambda1
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        EmojiView.GifSearchPreloader.this.lambda$preload$1(str, str2, z, str3, tLObject, tLRPC$TL_error);
                    }
                };
                if (z) {
                    this.loadingKeys.add(str3);
                    MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(str3, requestDelegate);
                    return;
                }
                MessagesController messagesController = MessagesController.getInstance(EmojiView.this.currentAccount);
                TLObject userOrChat = messagesController.getUserOrChat(messagesController.gifSearchBot);
                if (!(userOrChat instanceof TLRPC$User)) {
                    return;
                }
                this.loadingKeys.add(str3);
                TLRPC$TL_messages_getInlineBotResults tLRPC$TL_messages_getInlineBotResults = new TLRPC$TL_messages_getInlineBotResults();
                if (str == null) {
                    str = "";
                }
                tLRPC$TL_messages_getInlineBotResults.query = str;
                tLRPC$TL_messages_getInlineBotResults.bot = messagesController.getInputUser((TLRPC$User) userOrChat);
                tLRPC$TL_messages_getInlineBotResults.offset = str2;
                tLRPC$TL_messages_getInlineBotResults.peer = new TLRPC$TL_inputPeerEmpty();
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_messages_getInlineBotResults, requestDelegate, 2);
            }
        }

        public /* synthetic */ void lambda$preload$1(final String str, final String str2, final boolean z, final String str3, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$GifSearchPreloader$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiView.GifSearchPreloader.this.lambda$preload$0(str, str2, z, str3, tLObject);
                }
            });
        }

        /* renamed from: processResponse */
        public void lambda$preload$0(String str, String str2, boolean z, String str3, TLObject tLObject) {
            this.loadingKeys.remove(str3);
            if (EmojiView.this.gifSearchAdapter.lastSearchIsEmoji && EmojiView.this.gifSearchAdapter.lastSearchImageString.equals(str)) {
                EmojiView.this.gifSearchAdapter.lambda$search$3(str, str2, false, true, z, str3, tLObject);
            } else if (z && (!(tLObject instanceof TLRPC$messages_BotResults) || ((TLRPC$messages_BotResults) tLObject).results.isEmpty())) {
                preload(str, str2, false);
            } else if (!(tLObject instanceof TLRPC$messages_BotResults) || EmojiView.this.gifCache.containsKey(str3)) {
            } else {
                EmojiView.this.gifCache.put(str3, (TLRPC$messages_BotResults) tLObject);
            }
        }
    }

    /* loaded from: classes3.dex */
    public class GifLayoutManager extends ExtendedGridLayoutManager {
        private Size size;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public GifLayoutManager(Context context) {
            super(context, 100, true);
            EmojiView.this = r3;
            this.size = new Size();
            setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(r3) { // from class: org.telegram.ui.Components.EmojiView.GifLayoutManager.1
                {
                    GifLayoutManager.this = this;
                }

                @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                public int getSpanSize(int i) {
                    if (i == 0 || (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty())) {
                        return GifLayoutManager.this.getSpanCount();
                    }
                    return GifLayoutManager.this.getSpanSizeForItem(i - 1);
                }
            });
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r4v19, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARN: Type inference failed for: r4v20, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARN: Type inference failed for: r4v21, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARN: Type inference failed for: r4v5, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARN: Type inference failed for: r4v6, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        /* JADX WARN: Type inference failed for: r4v7, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
        @Override // org.telegram.ui.Components.ExtendedGridLayoutManager
        protected Size getSizeForItem(int i) {
            List<TLRPC$DocumentAttribute> list;
            TLRPC$Document tLRPC$Document;
            TLRPC$Document tLRPC$Document2;
            TLRPC$Document tLRPC$Document3 = null;
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                if (i > EmojiView.this.gifAdapter.recentItemsCount) {
                    TLRPC$BotInlineResult tLRPC$BotInlineResult = (TLRPC$BotInlineResult) EmojiView.this.gifAdapter.results.get((i - EmojiView.this.gifAdapter.recentItemsCount) - 1);
                    tLRPC$Document = tLRPC$BotInlineResult.document;
                    if (tLRPC$Document != null) {
                        tLRPC$Document2 = tLRPC$Document.attributes;
                    } else {
                        TLRPC$WebDocument tLRPC$WebDocument = tLRPC$BotInlineResult.content;
                        if (tLRPC$WebDocument != null) {
                            tLRPC$Document2 = tLRPC$WebDocument.attributes;
                        } else {
                            TLRPC$WebDocument tLRPC$WebDocument2 = tLRPC$BotInlineResult.thumb;
                            if (tLRPC$WebDocument2 != null) {
                                tLRPC$Document2 = tLRPC$WebDocument2.attributes;
                            }
                            list = tLRPC$Document3;
                            tLRPC$Document3 = tLRPC$Document;
                            return getSizeForItem(tLRPC$Document3, list);
                        }
                    }
                    tLRPC$Document3 = tLRPC$Document2;
                    list = tLRPC$Document3;
                    tLRPC$Document3 = tLRPC$Document;
                    return getSizeForItem(tLRPC$Document3, list);
                } else if (i == EmojiView.this.gifAdapter.recentItemsCount) {
                    return null;
                } else {
                    tLRPC$Document3 = (TLRPC$Document) EmojiView.this.recentGifs.get(i);
                    list = tLRPC$Document3.attributes;
                    return getSizeForItem(tLRPC$Document3, list);
                }
            } else if (!EmojiView.this.gifSearchAdapter.results.isEmpty()) {
                TLRPC$BotInlineResult tLRPC$BotInlineResult2 = (TLRPC$BotInlineResult) EmojiView.this.gifSearchAdapter.results.get(i);
                tLRPC$Document = tLRPC$BotInlineResult2.document;
                if (tLRPC$Document != null) {
                    tLRPC$Document2 = tLRPC$Document.attributes;
                } else {
                    TLRPC$WebDocument tLRPC$WebDocument3 = tLRPC$BotInlineResult2.content;
                    if (tLRPC$WebDocument3 != null) {
                        tLRPC$Document2 = tLRPC$WebDocument3.attributes;
                    } else {
                        TLRPC$WebDocument tLRPC$WebDocument4 = tLRPC$BotInlineResult2.thumb;
                        if (tLRPC$WebDocument4 != null) {
                            tLRPC$Document2 = tLRPC$WebDocument4.attributes;
                        }
                        list = tLRPC$Document3;
                        tLRPC$Document3 = tLRPC$Document;
                        return getSizeForItem(tLRPC$Document3, list);
                    }
                }
                tLRPC$Document3 = tLRPC$Document2;
                list = tLRPC$Document3;
                tLRPC$Document3 = tLRPC$Document;
                return getSizeForItem(tLRPC$Document3, list);
            } else {
                list = null;
                return getSizeForItem(tLRPC$Document3, list);
            }
        }

        @Override // org.telegram.ui.Components.ExtendedGridLayoutManager
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
            Size size = this.size;
            size.height = 100.0f;
            size.width = 100.0f;
            if (tLRPC$Document != null && (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90)) != null && (i = closestPhotoSizeWithSize.w) != 0 && (i2 = closestPhotoSizeWithSize.h) != 0) {
                Size size2 = this.size;
                size2.width = i;
                size2.height = i2;
            }
            if (list != null) {
                for (int i3 = 0; i3 < list.size(); i3++) {
                    TLRPC$DocumentAttribute tLRPC$DocumentAttribute = list.get(i3);
                    if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeImageSize) || (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo)) {
                        Size size3 = this.size;
                        size3.width = tLRPC$DocumentAttribute.w;
                        size3.height = tLRPC$DocumentAttribute.h;
                        break;
                    }
                }
            }
            return this.size;
        }
    }

    /* loaded from: classes3.dex */
    public class GifProgressEmptyView extends FrameLayout {
        private final ImageView imageView;
        private boolean loadingState;
        private final RadialProgressView progressView;
        private final TextView textView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public GifProgressEmptyView(Context context) {
            super(context);
            EmojiView.this = r13;
            ImageView imageView = new ImageView(getContext());
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageResource(R.drawable.gif_empty);
            imageView.setColorFilter(new PorterDuffColorFilter(r13.getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
            addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
            TextView textView = new TextView(getContext());
            this.textView = textView;
            textView.setText(LocaleController.getString("NoGIFsFound", R.string.NoGIFsFound));
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(r13.getThemedColor("chat_emojiPanelEmptyText"));
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context, r13.resourcesProvider);
            this.progressView = radialProgressView;
            radialProgressView.setVisibility(8);
            radialProgressView.setProgressColor(r13.getThemedColor("progressCircle"));
            addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            int dp;
            int measuredHeight = EmojiView.this.gifGridView.getMeasuredHeight();
            if (!this.loadingState) {
                dp = (int) ((((measuredHeight - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3) * 1.7f);
            } else {
                dp = measuredHeight - AndroidUtilities.dp(80.0f);
            }
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(dp, NUM));
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

    /* loaded from: classes3.dex */
    public class StickersSearchGridAdapter extends RecyclerListView.SelectionAdapter {
        boolean cleared;
        private Context context;
        private int emojiSearchId;
        private int reqId;
        private int reqId2;
        private String searchQuery;
        private int totalItems;
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        private SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<Object> cacheParent = new SparseArray<>();
        private SparseIntArray positionToRow = new SparseIntArray();
        private SparseArray<String> positionToEmoji = new SparseArray<>();
        private ArrayList<TLRPC$StickerSetCovered> serverPacks = new ArrayList<>();
        private ArrayList<TLRPC$TL_messages_stickerSet> localPacks = new ArrayList<>();
        private HashMap<TLRPC$TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
        private HashMap<TLRPC$TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
        private HashMap<ArrayList<TLRPC$Document>, String> emojiStickers = new HashMap<>();
        private ArrayList<ArrayList<TLRPC$Document>> emojiArrays = new ArrayList<>();
        private SparseArray<TLRPC$StickerSetCovered> positionsToSets = new SparseArray<>();
        private Runnable searchRunnable = new AnonymousClass1();

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        static /* synthetic */ int access$19504(StickersSearchGridAdapter stickersSearchGridAdapter) {
            int i = stickersSearchGridAdapter.emojiSearchId + 1;
            stickersSearchGridAdapter.emojiSearchId = i;
            return i;
        }

        /* renamed from: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1 */
        /* loaded from: classes3.dex */
        public class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
                StickersSearchGridAdapter.this = r1;
            }

            public void clear() {
                StickersSearchGridAdapter stickersSearchGridAdapter = StickersSearchGridAdapter.this;
                if (stickersSearchGridAdapter.cleared) {
                    return;
                }
                stickersSearchGridAdapter.cleared = true;
                stickersSearchGridAdapter.emojiStickers.clear();
                StickersSearchGridAdapter.this.emojiArrays.clear();
                StickersSearchGridAdapter.this.localPacks.clear();
                StickersSearchGridAdapter.this.serverPacks.clear();
                StickersSearchGridAdapter.this.localPacksByShortName.clear();
                StickersSearchGridAdapter.this.localPacksByName.clear();
            }

            /* JADX WARN: Code restructure failed: missing block: B:129:0x0079, code lost:
                if (r5.charAt(r9) <= 57343) goto L17;
             */
            /* JADX WARN: Code restructure failed: missing block: B:135:0x0093, code lost:
                if (r5.charAt(r9) != 9794) goto L27;
             */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void run() {
                /*
                    Method dump skipped, instructions count: 847
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1.run():void");
            }

            public /* synthetic */ void lambda$run$1(final TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                if (tLObject instanceof TLRPC$TL_messages_foundStickerSets) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.lambda$run$0(tLRPC$TL_messages_searchStickerSets, tLObject);
                        }
                    });
                }
            }

            public /* synthetic */ void lambda$run$0(TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, TLObject tLObject) {
                if (tLRPC$TL_messages_searchStickerSets.q.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    clear();
                    EmojiView.this.stickersSearchField.progressDrawable.stopAnimation();
                    StickersSearchGridAdapter.this.reqId = 0;
                    if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                        EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                    }
                    StickersSearchGridAdapter.this.serverPacks.addAll(((TLRPC$TL_messages_foundStickerSets) tLObject).sets);
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }

            public /* synthetic */ void lambda$run$3(final TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, final ArrayList arrayList, final LongSparseArray longSparseArray, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.lambda$run$2(tLRPC$TL_messages_getStickers, tLObject, arrayList, longSparseArray);
                    }
                });
            }

            public /* synthetic */ void lambda$run$2(TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
                if (tLRPC$TL_messages_getStickers.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    StickersSearchGridAdapter.this.reqId2 = 0;
                    if (!(tLObject instanceof TLRPC$TL_messages_stickers)) {
                        return;
                    }
                    TLRPC$TL_messages_stickers tLRPC$TL_messages_stickers = (TLRPC$TL_messages_stickers) tLObject;
                    int size = arrayList.size();
                    int size2 = tLRPC$TL_messages_stickers.stickers.size();
                    for (int i = 0; i < size2; i++) {
                        TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickers.stickers.get(i);
                        if (longSparseArray.indexOfKey(tLRPC$Document.id) < 0) {
                            arrayList.add(tLRPC$Document);
                        }
                    }
                    if (size == arrayList.size()) {
                        return;
                    }
                    StickersSearchGridAdapter.this.emojiStickers.put(arrayList, StickersSearchGridAdapter.this.searchQuery);
                    if (size == 0) {
                        StickersSearchGridAdapter.this.emojiArrays.add(arrayList);
                    }
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }
        }

        public StickersSearchGridAdapter(Context context) {
            EmojiView.this = r1;
            this.context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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
            AndroidUtilities.runOnUIThread(this.searchRunnable, 300L);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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

        public /* synthetic */ void lambda$onCreateViewHolder$0(View view) {
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
            TLRPC$StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
            if (EmojiView.this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0 || EmojiView.this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0) {
                return;
            }
            if (featuredStickerSetInfoCell.isInstalled()) {
                EmojiView.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                EmojiView.this.delegate.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
                return;
            }
            featuredStickerSetInfoCell.setAddDrawProgress(true, true);
            EmojiView.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
            EmojiView.this.delegate.onStickerSetAdd(featuredStickerSetInfoCell.getStickerSet());
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r15v1, types: [org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$2] */
        /* JADX WARN: Type inference failed for: r15v10, types: [android.widget.FrameLayout, android.view.View, org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$3] */
        /* JADX WARN: Type inference failed for: r15v2 */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1753onCreateViewHolder(ViewGroup viewGroup, int i) {
            ?? r15;
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell;
            if (i == 0) {
                r15 = new StickerEmojiCell(this, this.context, true) { // from class: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.2
                    @Override // android.widget.FrameLayout, android.view.View
                    public void onMeasure(int i2, int i3) {
                        super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                    }
                };
            } else {
                if (i == 1) {
                    featuredStickerSetInfoCell = new EmptyCell(this.context);
                } else if (i == 2) {
                    featuredStickerSetInfoCell = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                } else if (i == 3) {
                    FeaturedStickerSetInfoCell featuredStickerSetInfoCell2 = new FeaturedStickerSetInfoCell(this.context, 17, false, true, EmojiView.this.resourcesProvider);
                    featuredStickerSetInfoCell2.setAddOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            EmojiView.StickersSearchGridAdapter.this.lambda$onCreateViewHolder$0(view);
                        }
                    });
                    featuredStickerSetInfoCell = featuredStickerSetInfoCell2;
                } else if (i == 4) {
                    View view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    featuredStickerSetInfoCell = view;
                } else if (i != 5) {
                    featuredStickerSetInfoCell = null;
                } else {
                    r15 = new FrameLayout(this.context) { // from class: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.3
                        {
                            StickersSearchGridAdapter.this = this;
                        }

                        @Override // android.widget.FrameLayout, android.view.View
                        protected void onMeasure(int i2, int i3) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec((int) ((((EmojiView.this.stickersGridView.getMeasuredHeight() - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3) * 1.7f), NUM));
                        }
                    };
                    ImageView imageView = new ImageView(this.context);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(R.drawable.stickers_empty);
                    imageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
                    r15.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
                    TextView textView = new TextView(this.context);
                    textView.setText(LocaleController.getString("NoStickersFound", R.string.NoStickersFound));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(EmojiView.this.getThemedColor("chat_emojiPanelEmptyText"));
                    r15.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
                    r15.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                }
                return new RecyclerListView.Holder(featuredStickerSetInfoCell);
            }
            featuredStickerSetInfoCell = r15;
            return new RecyclerListView.Holder(featuredStickerSetInfoCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            char c = 1;
            int i2 = 1;
            z = true;
            if (itemViewType == 0) {
                TLRPC$Document tLRPC$Document = (TLRPC$Document) this.cache.get(i);
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
                stickerEmojiCell.setSticker(tLRPC$Document, null, this.cacheParent.get(i), this.positionToEmoji.get(i), false);
                if (!EmojiView.this.recentStickers.contains(tLRPC$Document) && !EmojiView.this.favouriteStickers.contains(tLRPC$Document)) {
                    z = false;
                }
                stickerEmojiCell.setRecent(z);
                return;
            }
            Integer num = null;
            if (itemViewType == 1) {
                EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                if (i == this.totalItems) {
                    int i3 = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                    if (i3 == Integer.MIN_VALUE) {
                        emptyCell.setHeight(1);
                        return;
                    }
                    Object obj = this.rowStartPack.get(i3);
                    if (obj instanceof TLRPC$TL_messages_stickerSet) {
                        num = Integer.valueOf(((TLRPC$TL_messages_stickerSet) obj).documents.size());
                    } else if (obj instanceof Integer) {
                        num = (Integer) obj;
                    }
                    if (num == null) {
                        emptyCell.setHeight(1);
                        return;
                    } else if (num.intValue() != 0) {
                        int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil(num.intValue() / EmojiView.this.stickersGridAdapter.stickersPerRow)) * AndroidUtilities.dp(82.0f));
                        if (height > 0) {
                            i2 = height;
                        }
                        emptyCell.setHeight(i2);
                        return;
                    } else {
                        emptyCell.setHeight(AndroidUtilities.dp(8.0f));
                        return;
                    }
                }
                emptyCell.setHeight(AndroidUtilities.dp(82.0f));
            } else if (itemViewType == 2) {
                StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                Object obj2 = this.cache.get(i);
                if (!(obj2 instanceof TLRPC$TL_messages_stickerSet)) {
                    return;
                }
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) obj2;
                if (!TextUtils.isEmpty(this.searchQuery) && this.localPacksByShortName.containsKey(tLRPC$TL_messages_stickerSet)) {
                    TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
                    if (tLRPC$StickerSet != null) {
                        stickerSetNameCell.setText(tLRPC$StickerSet.title, 0);
                    }
                    stickerSetNameCell.setUrl(tLRPC$TL_messages_stickerSet.set.short_name, this.searchQuery.length());
                    return;
                }
                Integer num2 = this.localPacksByName.get(tLRPC$TL_messages_stickerSet);
                TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$TL_messages_stickerSet.set;
                if (tLRPC$StickerSet2 != null && num2 != null) {
                    stickerSetNameCell.setText(tLRPC$StickerSet2.title, 0, num2.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                }
                stickerSetNameCell.setUrl(null, 0);
            } else if (itemViewType != 3) {
            } else {
                TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) this.cache.get(i);
                FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) viewHolder.itemView;
                boolean z2 = EmojiView.this.installingStickerSets.indexOfKey(tLRPC$StickerSetCovered.set.id) >= 0;
                if (EmojiView.this.removingStickerSets.indexOfKey(tLRPC$StickerSetCovered.set.id) < 0) {
                    c = 0;
                }
                if (z2 || c != 0) {
                    if (z2 && featuredStickerSetInfoCell.isInstalled()) {
                        EmojiView.this.installingStickerSets.remove(tLRPC$StickerSetCovered.set.id);
                        z2 = false;
                    } else if (c != 0 && !featuredStickerSetInfoCell.isInstalled()) {
                        EmojiView.this.removingStickerSets.remove(tLRPC$StickerSetCovered.set.id);
                    }
                }
                featuredStickerSetInfoCell.setAddDrawProgress(z2, false);
                int indexOfIgnoreCase = TextUtils.isEmpty(this.searchQuery) ? -1 : AndroidUtilities.indexOfIgnoreCase(tLRPC$StickerSetCovered.set.title, this.searchQuery);
                if (indexOfIgnoreCase >= 0) {
                    featuredStickerSetInfoCell.setStickerSet(tLRPC$StickerSetCovered, false, false, indexOfIgnoreCase, this.searchQuery.length());
                    return;
                }
                featuredStickerSetInfoCell.setStickerSet(tLRPC$StickerSetCovered, false);
                if (TextUtils.isEmpty(this.searchQuery) || AndroidUtilities.indexOfIgnoreCase(tLRPC$StickerSetCovered.set.short_name, this.searchQuery) != 0) {
                    return;
                }
                featuredStickerSetInfoCell.setUrl(tLRPC$StickerSetCovered.set.short_name, this.searchQuery.length());
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r8v19, types: [org.telegram.tgnet.TLRPC$messages_StickerSet, org.telegram.tgnet.TLRPC$TL_messages_stickerSet] */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            int i;
            ArrayList<TLRPC$Document> arrayList;
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered;
            this.rowStartPack.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.positionsToSets.clear();
            this.positionToEmoji.clear();
            this.totalItems = 0;
            int size = this.serverPacks.size();
            int size2 = this.localPacks.size();
            int i2 = !this.emojiArrays.isEmpty() ? 1 : 0;
            int i3 = -1;
            int i4 = -1;
            int i5 = 0;
            while (i4 < size + size2 + i2) {
                if (i4 == i3) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i6 = this.totalItems;
                    this.totalItems = i6 + 1;
                    sparseArray.put(i6, "search");
                    i5++;
                    i = size;
                } else {
                    if (i4 < size2) {
                        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.localPacks.get(i4);
                        arrayList = tLRPC$TL_messages_stickerSet.documents;
                        i = size;
                        tLRPC$StickerSetCovered = tLRPC$TL_messages_stickerSet;
                    } else {
                        int i7 = i4 - size2;
                        if (i7 < i2) {
                            int size3 = this.emojiArrays.size();
                            String str = "";
                            int i8 = 0;
                            for (int i9 = 0; i9 < size3; i9++) {
                                ArrayList<TLRPC$Document> arrayList2 = this.emojiArrays.get(i9);
                                String str2 = this.emojiStickers.get(arrayList2);
                                if (str2 != null && !str.equals(str2)) {
                                    this.positionToEmoji.put(this.totalItems + i8, str2);
                                    str = str2;
                                }
                                int size4 = arrayList2.size();
                                int i10 = 0;
                                while (i10 < size4) {
                                    int i11 = this.totalItems + i8;
                                    int i12 = (i8 / EmojiView.this.stickersGridAdapter.stickersPerRow) + i5;
                                    TLRPC$Document tLRPC$Document = arrayList2.get(i10);
                                    int i13 = size;
                                    this.cache.put(i11, tLRPC$Document);
                                    int i14 = size3;
                                    String str3 = str;
                                    TLRPC$TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(tLRPC$Document));
                                    if (stickerSetById != null) {
                                        this.cacheParent.put(i11, stickerSetById);
                                    }
                                    this.positionToRow.put(i11, i12);
                                    i8++;
                                    i10++;
                                    size = i13;
                                    size3 = i14;
                                    str = str3;
                                }
                            }
                            i = size;
                            int ceil = (int) Math.ceil(i8 / EmojiView.this.stickersGridAdapter.stickersPerRow);
                            for (int i15 = 0; i15 < ceil; i15++) {
                                this.rowStartPack.put(i5 + i15, Integer.valueOf(i8));
                            }
                            this.totalItems += EmojiView.this.stickersGridAdapter.stickersPerRow * ceil;
                            i5 += ceil;
                        } else {
                            i = size;
                            TLRPC$StickerSetCovered tLRPC$StickerSetCovered2 = this.serverPacks.get(i7 - i2);
                            arrayList = tLRPC$StickerSetCovered2.covers;
                            tLRPC$StickerSetCovered = tLRPC$StickerSetCovered2;
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        int ceil2 = (int) Math.ceil(arrayList.size() / EmojiView.this.stickersGridAdapter.stickersPerRow);
                        this.cache.put(this.totalItems, tLRPC$StickerSetCovered);
                        if (i4 >= size2 && (tLRPC$StickerSetCovered instanceof TLRPC$StickerSetCovered)) {
                            this.positionsToSets.put(this.totalItems, tLRPC$StickerSetCovered);
                        }
                        this.positionToRow.put(this.totalItems, i5);
                        int size5 = arrayList.size();
                        int i16 = 0;
                        while (i16 < size5) {
                            int i17 = i16 + 1;
                            int i18 = this.totalItems + i17;
                            int i19 = i5 + 1 + (i16 / EmojiView.this.stickersGridAdapter.stickersPerRow);
                            this.cache.put(i18, arrayList.get(i16));
                            this.cacheParent.put(i18, tLRPC$StickerSetCovered);
                            this.positionToRow.put(i18, i19);
                            if (i4 >= size2 && (tLRPC$StickerSetCovered instanceof TLRPC$StickerSetCovered)) {
                                this.positionsToSets.put(i18, tLRPC$StickerSetCovered);
                            }
                            i16 = i17;
                        }
                        int i20 = ceil2 + 1;
                        for (int i21 = 0; i21 < i20; i21++) {
                            this.rowStartPack.put(i5 + i21, tLRPC$StickerSetCovered);
                        }
                        this.totalItems += (ceil2 * EmojiView.this.stickersGridAdapter.stickersPerRow) + 1;
                        i5 += i20;
                    }
                }
                i4++;
                size = i;
                i3 = -1;
            }
            super.notifyDataSetChanged();
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
        ChooseStickerActionTracker chooseStickerActionTracker = this.chooseStickerActionTracker;
        if (chooseStickerActionTracker != null) {
            chooseStickerActionTracker.reset();
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class ChooseStickerActionTracker {
        private final int currentAccount;
        private final long dialogId;
        private final int threadId;
        boolean typingWasSent;
        boolean visible = false;
        long lastActionTime = -1;

        public abstract boolean isShown();

        public ChooseStickerActionTracker(int i, long j, int i2) {
            this.currentAccount = i;
            this.dialogId = j;
            this.threadId = i2;
        }

        public void doSomeAction() {
            if (this.visible) {
                if (this.lastActionTime == -1) {
                    this.lastActionTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - this.lastActionTime <= 2000) {
                } else {
                    this.typingWasSent = true;
                    this.lastActionTime = System.currentTimeMillis();
                    MessagesController.getInstance(this.currentAccount).sendTyping(this.dialogId, this.threadId, 10, 0);
                }
            }
        }

        public void reset() {
            if (this.typingWasSent) {
                MessagesController.getInstance(this.currentAccount).sendTyping(this.dialogId, this.threadId, 2, 0);
            }
            this.lastActionTime = -1L;
        }

        public void checkVisibility() {
            boolean isShown = isShown();
            this.visible = isShown;
            if (!isShown) {
                reset();
            }
        }
    }

    /* loaded from: classes3.dex */
    public class Tab {
        int type;
        View view;

        private Tab(EmojiView emojiView) {
        }
    }
}
