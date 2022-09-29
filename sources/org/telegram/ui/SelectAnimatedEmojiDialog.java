package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_account_clearRecentEmojiStatuses;
import org.telegram.tgnet.TLRPC$TL_emojiStatus;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmojiDefaultStatuses;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.FixedHeightEmptyCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CloseProgressDrawable2;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.DrawingInBackgroundThreadDrawable;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.EmojiTabsStrip;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumLockIconView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerListView;

public class SelectAnimatedEmojiDialog extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static String[] lastSearchKeyboardLanguage;
    private static HashMap<Integer, Parcelable> listStates = new HashMap<>();
    private Adapter adapter;
    /* access modifiers changed from: private */
    public View animateExpandFromButton;
    /* access modifiers changed from: private */
    public int animateExpandFromPosition;
    /* access modifiers changed from: private */
    public long animateExpandStartTime;
    /* access modifiers changed from: private */
    public int animateExpandToPosition;
    /* access modifiers changed from: private */
    public BaseFragment baseFragment;
    AnimatedEmojiDrawable bigReactionAnimatedEmoji;
    ImageReceiver bigReactionImageReceiver;
    public onLongPressedListener bigReactionListener;
    private boolean bottomGradientShown;
    /* access modifiers changed from: private */
    public View bottomGradientView;
    private View bubble1View;
    private View bubble2View;
    public boolean cancelPressed;
    private Runnable clearSearchRunnable;
    /* access modifiers changed from: private */
    public FrameLayout contentView;
    private View contentViewForeground;
    /* access modifiers changed from: private */
    public int currentAccount;
    private boolean defaultSetLoading;
    /* access modifiers changed from: private */
    public ArrayList<AnimatedEmojiSpan> defaultStatuses;
    private ValueAnimator dimAnimator;
    /* access modifiers changed from: private */
    public Runnable dismiss;
    /* access modifiers changed from: private */
    public boolean drawBackground;
    /* access modifiers changed from: private */
    public Rect drawableToBounds;
    public EmojiListView emojiGridView;
    public FrameLayout emojiSearchEmptyView;
    private BackupImageView emojiSearchEmptyViewImageView;
    public EmojiListView emojiSearchGridView;
    private float emojiSelectAlpha;
    private ValueAnimator emojiSelectAnimator;
    private Rect emojiSelectRect;
    /* access modifiers changed from: private */
    public ImageViewEmoji emojiSelectView;
    /* access modifiers changed from: private */
    public EmojiTabsStrip emojiTabs;
    private View emojiTabsShadow;
    private Integer emojiX;
    private ArrayList<String> emptyViewEmojis;
    private ArrayList<Long> expandedEmojiSets;
    private boolean gridSearch;
    /* access modifiers changed from: private */
    public ValueAnimator gridSwitchAnimator;
    public FrameLayout gridViewContainer;
    private ValueAnimator hideAnimator;
    /* access modifiers changed from: private */
    public Integer hintExpireDate;
    /* access modifiers changed from: private */
    public boolean includeEmpty;
    /* access modifiers changed from: private */
    public boolean includeHint;
    /* access modifiers changed from: private */
    public ArrayList<Long> installedEmojiSets;
    private boolean isAttached;
    private String lastQuery;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    private Integer listStateId;
    /* access modifiers changed from: private */
    public int longtapHintRow;
    public onRecentClearedListener onRecentClearedListener;
    private OvershootInterpolator overshootInterpolator;
    /* access modifiers changed from: private */
    public ArrayList<EmojiView.EmojiPack> packs;
    Paint paint;
    /* access modifiers changed from: private */
    public int popularSectionRow;
    /* access modifiers changed from: private */
    public SparseIntArray positionToButton;
    /* access modifiers changed from: private */
    public SparseIntArray positionToExpand;
    /* access modifiers changed from: private */
    public SparseIntArray positionToSection;
    private Drawable premiumStar;
    /* access modifiers changed from: private */
    public ColorFilter premiumStarColorFilter;
    float pressedProgress;
    /* access modifiers changed from: private */
    public ArrayList<AnimatedEmojiSpan> recent;
    /* access modifiers changed from: private */
    public EmojiPackExpand recentExpandButton;
    /* access modifiers changed from: private */
    public boolean recentExpanded;
    /* access modifiers changed from: private */
    public ArrayList<ReactionsLayoutInBubble.VisibleReaction> recentReactions;
    /* access modifiers changed from: private */
    public int recentReactionsEndRow;
    /* access modifiers changed from: private */
    public int recentReactionsSectionRow;
    /* access modifiers changed from: private */
    public int recentReactionsStartRow;
    private List<ReactionsLayoutInBubble.VisibleReaction> recentReactionsToSet;
    /* access modifiers changed from: private */
    public Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public ArrayList<Integer> rowHashCodes;
    /* access modifiers changed from: private */
    public float scaleX;
    /* access modifiers changed from: private */
    public float scaleY;
    private float scrimAlpha;
    /* access modifiers changed from: private */
    public int scrimColor;
    /* access modifiers changed from: private */
    public AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable scrimDrawable;
    private View scrimDrawableParent;
    private RecyclerAnimationScrollHelper scrollHelper;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public SearchBox searchBox;
    /* access modifiers changed from: private */
    public ValueAnimator searchEmptyViewAnimator;
    private boolean searchEmptyViewVisible;
    /* access modifiers changed from: private */
    public ArrayList<Long> searchResult;
    /* access modifiers changed from: private */
    public int searchRow;
    private Runnable searchRunnable;
    public boolean searched;
    public boolean searching;
    /* access modifiers changed from: private */
    public SparseIntArray sectionToPosition;
    /* access modifiers changed from: private */
    public SelectStatusDurationDialog selectStatusDateDialog;
    HashSet<Long> selectedDocumentIds;
    ImageViewEmoji selectedReactionView;
    HashSet<ReactionsLayoutInBubble.VisibleReaction> selectedReactions;
    public Paint selectorAccentPaint;
    public Paint selectorPaint;
    /* access modifiers changed from: private */
    public ValueAnimator showAnimator;
    /* access modifiers changed from: private */
    public boolean smoothScrolling;
    private View topGradientView;
    private int topMarginDp;
    /* access modifiers changed from: private */
    public ArrayList<ReactionsLayoutInBubble.VisibleReaction> topReactions;
    /* access modifiers changed from: private */
    public int topReactionsEndRow;
    /* access modifiers changed from: private */
    public int topReactionsStartRow;
    /* access modifiers changed from: private */
    public int totalCount;
    /* access modifiers changed from: private */
    public int type;

    public interface onLongPressedListener {
        void onLongPressed(ImageViewEmoji imageViewEmoji);
    }

    public interface onRecentClearedListener {
        void onRecentCleared();
    }

    /* access modifiers changed from: protected */
    public void onEmojiSelected(View view, Long l, TLRPC$Document tLRPC$Document, Integer num) {
    }

    /* access modifiers changed from: protected */
    public void onInputFocus() {
    }

    /* access modifiers changed from: protected */
    public void onReactionClick(ImageViewEmoji imageViewEmoji, ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
    }

    public void putAnimatedEmojiToCache(AnimatedEmojiDrawable animatedEmojiDrawable) {
        this.emojiGridView.animatedEmojiDrawables.put(animatedEmojiDrawable.getDocumentId(), animatedEmojiDrawable);
    }

    public void setSelectedReactions(HashSet<ReactionsLayoutInBubble.VisibleReaction> hashSet) {
        this.selectedReactions = hashSet;
        this.selectedDocumentIds.clear();
        ArrayList arrayList = new ArrayList(hashSet);
        for (int i = 0; i < arrayList.size(); i++) {
            if (((ReactionsLayoutInBubble.VisibleReaction) arrayList.get(i)).documentId != 0) {
                this.selectedDocumentIds.add(Long.valueOf(((ReactionsLayoutInBubble.VisibleReaction) arrayList.get(i)).documentId));
            }
        }
    }

    public static class SelectAnimatedEmojiDialogWindow extends PopupWindow {
        private static final ViewTreeObserver.OnScrollChangedListener NOP = SelectAnimatedEmojiDialog$SelectAnimatedEmojiDialogWindow$$ExternalSyntheticLambda0.INSTANCE;
        private static final Field superListenerField;
        private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
        private ViewTreeObserver mViewTreeObserver;

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$static$0() {
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

        public SelectAnimatedEmojiDialogWindow(View view, int i, int i2) {
            super(view, i, i2);
            init();
        }

        private void init() {
            setFocusable(true);
            setAnimationStyle(0);
            setOutsideTouchable(true);
            setClippingEnabled(true);
            setInputMethodMode(0);
            setSoftInputMode(4);
            Field field = superListenerField;
            if (field != null) {
                try {
                    this.mSuperScrollListener = (ViewTreeObserver.OnScrollChangedListener) field.get(this);
                    field.set(this, NOP);
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
            if (getContentView() instanceof SelectAnimatedEmojiDialog) {
                ((SelectAnimatedEmojiDialog) getContentView()).onShow(new SelectAnimatedEmojiDialog$SelectAnimatedEmojiDialogWindow$$ExternalSyntheticLambda1(this));
            }
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

        public void dimBehind() {
            View rootView = getContentView().getRootView();
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) rootView.getLayoutParams();
            layoutParams.flags |= 2;
            layoutParams.dimAmount = 0.2f;
            ((WindowManager) getContentView().getContext().getSystemService("window")).updateViewLayout(rootView, layoutParams);
        }

        private void dismissDim() {
            View rootView = getContentView().getRootView();
            WindowManager windowManager = (WindowManager) getContentView().getContext().getSystemService("window");
            if (rootView.getLayoutParams() != null && (rootView.getLayoutParams() instanceof WindowManager.LayoutParams)) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) rootView.getLayoutParams();
                try {
                    int i = layoutParams.flags;
                    if ((i & 2) != 0) {
                        layoutParams.flags = i & -3;
                        layoutParams.dimAmount = 0.0f;
                        windowManager.updateViewLayout(rootView, layoutParams);
                    }
                } catch (Exception unused) {
                }
            }
        }

        public void showAsDropDown(View view) {
            super.showAsDropDown(view);
            registerListener(view);
        }

        public void showAsDropDown(View view, int i, int i2) {
            super.showAsDropDown(view, i, i2);
            registerListener(view);
        }

        public void showAsDropDown(View view, int i, int i2, int i3) {
            super.showAsDropDown(view, i, i2, i3);
            registerListener(view);
        }

        public void showAtLocation(View view, int i, int i2, int i3) {
            super.showAtLocation(view, i, i2, i3);
            unregisterListener();
        }

        public void dismiss() {
            if (getContentView() instanceof SelectAnimatedEmojiDialog) {
                ((SelectAnimatedEmojiDialog) getContentView()).onDismiss(new SelectAnimatedEmojiDialog$SelectAnimatedEmojiDialogWindow$$ExternalSyntheticLambda2(this));
                dismissDim();
                return;
            }
            super.dismiss();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$dismiss$1() {
            super.dismiss();
        }
    }

    public SelectAnimatedEmojiDialog(BaseFragment baseFragment2, Context context, boolean z, Integer num, int i, Theme.ResourcesProvider resourcesProvider2) {
        this(baseFragment2, context, z, num, i, resourcesProvider2, 16);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public SelectAnimatedEmojiDialog(org.telegram.ui.ActionBar.BaseFragment r35, android.content.Context r36, boolean r37, java.lang.Integer r38, int r39, org.telegram.ui.ActionBar.Theme.ResourcesProvider r40, int r41) {
        /*
            r34 = this;
            r7 = r34
            r8 = r36
            r9 = r38
            r10 = r39
            r11 = r40
            r7.<init>(r8)
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            r7.selectedReactions = r0
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            r7.selectedDocumentIds = r0
            android.graphics.Paint r0 = new android.graphics.Paint
            r12 = 1
            r0.<init>(r12)
            r7.selectorPaint = r0
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>(r12)
            r7.selectorAccentPaint = r0
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            r7.currentAccount = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.rowHashCodes = r0
            android.util.SparseIntArray r0 = new android.util.SparseIntArray
            r0.<init>()
            r7.positionToSection = r0
            android.util.SparseIntArray r0 = new android.util.SparseIntArray
            r0.<init>()
            r7.sectionToPosition = r0
            android.util.SparseIntArray r0 = new android.util.SparseIntArray
            r0.<init>()
            r7.positionToExpand = r0
            android.util.SparseIntArray r0 = new android.util.SparseIntArray
            r0.<init>()
            r7.positionToButton = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.expandedEmojiSets = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.installedEmojiSets = r0
            r13 = 0
            r7.recentExpanded = r13
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.recent = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.topReactions = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.recentReactions = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.defaultStatuses = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.packs = r0
            r7.includeEmpty = r13
            r7.includeHint = r13
            r7.drawBackground = r12
            org.telegram.messenger.ImageReceiver r0 = new org.telegram.messenger.ImageReceiver
            r0.<init>()
            r7.bigReactionImageReceiver = r0
            r14 = 1065353216(0x3var_, float:1.0)
            r7.scrimAlpha = r14
            r7.emojiSelectAlpha = r14
            android.view.animation.OvershootInterpolator r0 = new android.view.animation.OvershootInterpolator
            r15 = 1073741824(0x40000000, float:2.0)
            r0.<init>(r15)
            r7.overshootInterpolator = r0
            r7.bottomGradientShown = r13
            r7.smoothScrolling = r13
            r7.searching = r13
            r7.searched = r13
            r7.gridSearch = r13
            java.util.ArrayList r0 = new java.util.ArrayList
            r6 = 4
            r0.<init>(r6)
            r7.emptyViewEmojis = r0
            java.lang.String r1 = "😖"
            r0.add(r1)
            java.util.ArrayList<java.lang.String> r0 = r7.emptyViewEmojis
            java.lang.String r1 = "😫"
            r0.add(r1)
            java.util.ArrayList<java.lang.String> r0 = r7.emptyViewEmojis
            java.lang.String r1 = "🫠"
            r0.add(r1)
            java.util.ArrayList<java.lang.String> r0 = r7.emptyViewEmojis
            java.lang.String r1 = "😨"
            r0.add(r1)
            java.util.ArrayList<java.lang.String> r0 = r7.emptyViewEmojis
            java.lang.String r1 = "❓"
            r0.add(r1)
            r7.searchEmptyViewVisible = r13
            r5 = -1
            r7.animateExpandFromPosition = r5
            r7.animateExpandToPosition = r5
            r0 = -1
            r7.animateExpandStartTime = r0
            r7.defaultSetLoading = r13
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            r7.paint = r0
            r7.resourcesProvider = r11
            r7.type = r10
            r0 = r37
            r7.includeEmpty = r0
            r0 = r35
            r7.baseFragment = r0
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "emoji"
            r1.append(r2)
            if (r10 != 0) goto L_0x0107
            java.lang.String r2 = "status"
            goto L_0x0109
        L_0x0107:
            java.lang.String r2 = "reaction"
        L_0x0109:
            r1.append(r2)
            java.lang.String r2 = "usehint"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            int r0 = r0.getInt(r1, r13)
            r1 = 3
            if (r0 >= r1) goto L_0x011e
            r0 = 1
            goto L_0x011f
        L_0x011e:
            r0 = 0
        L_0x011f:
            r7.includeHint = r0
            android.graphics.Paint r0 = r7.selectorPaint
            java.lang.String r1 = "listSelectorSDK21"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r1, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            r0.setColor(r1)
            android.graphics.Paint r0 = r7.selectorAccentPaint
            java.lang.String r1 = "windowBackgroundWhiteBlueIcon"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r1, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            r3 = 30
            int r2 = androidx.core.graphics.ColorUtils.setAlphaComponent(r2, r3)
            r0.setColor(r2)
            android.graphics.PorterDuffColorFilter r0 = new android.graphics.PorterDuffColorFilter
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r1, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
            r0.<init>(r1, r2)
            r7.premiumStarColorFilter = r0
            r7.emojiX = r9
            if (r9 != 0) goto L_0x0150
            r3 = 0
            goto L_0x0169
        L_0x0150:
            int r0 = r38.intValue()
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 1133641728(0x43920000, float:292.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = androidx.core.math.MathUtils.clamp((int) r0, (int) r1, (int) r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r3 = r0
        L_0x0169:
            if (r3 == 0) goto L_0x0179
            int r0 = r3.intValue()
            r1 = 1126825984(0x432a0000, float:170.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            if (r0 <= r1) goto L_0x0179
            r0 = 1
            goto L_0x017a
        L_0x0179:
            r0 = 0
        L_0x017a:
            r7.setFocusableInTouchMode(r12)
            r2 = 2
            if (r10 == 0) goto L_0x0182
            if (r10 != r2) goto L_0x01a3
        L_0x0182:
            r1 = r41
            r7.topMarginDp = r1
            r1 = 1082130432(0x40800000, float:4.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r7.setPadding(r4, r5, r6, r1)
            org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda9 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda9
            r1.<init>(r7)
            r7.setOnTouchListener(r1)
        L_0x01a3:
            java.lang.String r6 = "actionBarDefaultSubmenuBackground"
            if (r3 == 0) goto L_0x01f8
            android.view.View r1 = new android.view.View
            r1.<init>(r8)
            r7.bubble1View = r1
            android.content.res.Resources r1 = r34.getResources()
            int r4 = org.telegram.messenger.R.drawable.shadowed_bubble1
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r4)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            int r5 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r6, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r12)
            r1.setColorFilter(r4)
            android.view.View r4 = r7.bubble1View
            r4.setBackground(r1)
            android.view.View r1 = r7.bubble1View
            r16 = 10
            r17 = 1092616192(0x41200000, float:10.0)
            r18 = 51
            int r4 = r3.intValue()
            float r4 = (float) r4
            float r5 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r5
            if (r0 == 0) goto L_0x01e4
            r5 = -12
            goto L_0x01e5
        L_0x01e4:
            r5 = 4
        L_0x01e5:
            float r5 = (float) r5
            float r19 = r4 + r5
            int r4 = r7.topMarginDp
            float r4 = (float) r4
            r21 = 0
            r22 = 0
            r20 = r4
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r7.addView(r1, r4)
        L_0x01f8:
            org.telegram.ui.SelectAnimatedEmojiDialog$1 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$1
            r1.<init>(r8, r11, r3)
            r7.contentView = r1
            if (r10 == 0) goto L_0x0203
            if (r10 != r2) goto L_0x0218
        L_0x0203:
            r4 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.setPadding(r5, r12, r15, r4)
        L_0x0218:
            android.widget.FrameLayout r1 = r7.contentView
            r17 = -1
            r18 = -1082130432(0xffffffffbvar_, float:-1.0)
            r19 = 119(0x77, float:1.67E-43)
            r20 = 0
            r12 = 6
            if (r10 == 0) goto L_0x022b
            if (r10 != r2) goto L_0x0228
            goto L_0x022b
        L_0x0228:
            r21 = 0
            goto L_0x0231
        L_0x022b:
            int r4 = r7.topMarginDp
            int r4 = r4 + r12
            float r4 = (float) r4
            r21 = r4
        L_0x0231:
            r22 = 0
            r23 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r7.addView(r1, r4)
            r17 = 5
            if (r3 == 0) goto L_0x0290
            org.telegram.ui.SelectAnimatedEmojiDialog$2 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$2
            r1.<init>(r7, r8)
            r7.bubble2View = r1
            android.content.res.Resources r1 = r34.getResources()
            int r4 = org.telegram.messenger.R.drawable.shadowed_bubble2_half
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r4)
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            int r5 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r6, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r2)
            r1.setColorFilter(r4)
            android.view.View r2 = r7.bubble2View
            r2.setBackground(r1)
            android.view.View r1 = r7.bubble2View
            r18 = 17
            r19 = 1091567616(0x41100000, float:9.0)
            r20 = 51
            int r2 = r3.intValue()
            float r2 = (float) r2
            float r4 = org.telegram.messenger.AndroidUtilities.density
            float r2 = r2 / r4
            if (r0 == 0) goto L_0x0279
            r0 = -25
            goto L_0x027b
        L_0x0279:
            r0 = 10
        L_0x027b:
            float r0 = (float) r0
            float r21 = r2 + r0
            int r0 = r7.topMarginDp
            int r0 = r0 + 5
            float r0 = (float) r0
            r23 = 0
            r24 = 0
            r22 = r0
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r7.addView(r1, r0)
        L_0x0290:
            org.telegram.ui.SelectAnimatedEmojiDialog$3 r5 = new org.telegram.ui.SelectAnimatedEmojiDialog$3
            r4 = 0
            r18 = 0
            r19 = 1
            r20 = 0
            r0 = r5
            r1 = r34
            r12 = 2
            r2 = r36
            r15 = r3
            r3 = r4
            r4 = r18
            r14 = r5
            r5 = r19
            r26 = r6
            r6 = r20
            r0.<init>(r2, r3, r4, r5, r6)
            r7.emojiTabs = r14
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r0 = r14.recentTab
            org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda8 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda8
            r1.<init>(r7)
            r0.setOnLongClickListener(r1)
            org.telegram.ui.Components.EmojiTabsStrip r0 = r7.emojiTabs
            r0.updateButtonDrawables = r13
            if (r10 == 0) goto L_0x02c4
            if (r10 != r12) goto L_0x02c2
            goto L_0x02c4
        L_0x02c2:
            r12 = 5
            goto L_0x02c5
        L_0x02c4:
            r12 = 6
        L_0x02c5:
            r0.setAnimatedEmojiCacheType(r12)
            org.telegram.ui.Components.EmojiTabsStrip r0 = r7.emojiTabs
            if (r15 != 0) goto L_0x02ce
            r1 = 1
            goto L_0x02cf
        L_0x02ce:
            r1 = 0
        L_0x02cf:
            r0.animateAppear = r1
            r1 = 1084227584(0x40a00000, float:5.0)
            r0.setPaddingLeft(r1)
            android.widget.FrameLayout r0 = r7.contentView
            org.telegram.ui.Components.EmojiTabsStrip r2 = r7.emojiTabs
            r6 = 1108344832(0x42100000, float:36.0)
            r12 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r6)
            r0.addView(r2, r3)
            org.telegram.ui.SelectAnimatedEmojiDialog$4 r0 = new org.telegram.ui.SelectAnimatedEmojiDialog$4
            r0.<init>(r7, r8, r15)
            r7.emojiTabsShadow = r0
            java.lang.String r2 = "divider"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            r0.setBackgroundColor(r2)
            android.widget.FrameLayout r0 = r7.contentView
            android.view.View r2 = r7.emojiTabsShadow
            r27 = -1
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1065353216(0x3var_, float:1.0)
            float r28 = r4 / r3
            r29 = 48
            r30 = 0
            r31 = 1108344832(0x42100000, float:36.0)
            r32 = 0
            r33 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r0.addView(r2, r3)
            org.telegram.ui.SelectAnimatedEmojiDialog$5 r0 = new org.telegram.ui.SelectAnimatedEmojiDialog$5
            r0.<init>(r8)
            r7.emojiGridView = r0
            org.telegram.ui.SelectAnimatedEmojiDialog$6 r0 = new org.telegram.ui.SelectAnimatedEmojiDialog$6
            r0.<init>(r7)
            r2 = 220(0xdc, double:1.087E-321)
            r0.setAddDuration(r2)
            r2 = 260(0x104, double:1.285E-321)
            r0.setMoveDuration(r2)
            r2 = 160(0xa0, double:7.9E-322)
            r0.setChangeDuration(r2)
            r0.setSupportsChangeAnimations(r13)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            r0.setMoveInterpolator(r2)
            r0.setDelayAnimations(r13)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r3 = r7.emojiGridView
            r3.setItemAnimator(r0)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r0 = r7.emojiGridView
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4 = 1073741824(0x40000000, float:2.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r14 = 1108869120(0x42180000, float:38.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r0.setPadding(r3, r5, r4, r14)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r0 = r7.emojiGridView
            org.telegram.ui.SelectAnimatedEmojiDialog$Adapter r3 = new org.telegram.ui.SelectAnimatedEmojiDialog$Adapter
            r4 = 0
            r3.<init>()
            r7.adapter = r3
            r0.setAdapter(r3)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r0 = r7.emojiGridView
            org.telegram.ui.SelectAnimatedEmojiDialog$7 r3 = new org.telegram.ui.SelectAnimatedEmojiDialog$7
            r5 = 8
            r3.<init>(r8, r5)
            r7.layoutManager = r3
            r0.setLayoutManager(r3)
            androidx.recyclerview.widget.GridLayoutManager r0 = r7.layoutManager
            org.telegram.ui.SelectAnimatedEmojiDialog$8 r3 = new org.telegram.ui.SelectAnimatedEmojiDialog$8
            r3.<init>()
            r0.setSpanSizeLookup(r3)
            org.telegram.ui.SelectAnimatedEmojiDialog$9 r0 = new org.telegram.ui.SelectAnimatedEmojiDialog$9
            r0.<init>(r7, r8)
            r7.gridViewContainer = r0
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r3 = r7.emojiGridView
            r19 = -1
            r20 = -1082130432(0xffffffffbvar_, float:-1.0)
            r21 = 119(0x77, float:1.67E-43)
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r3, r14)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r0 = new org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView
            r0.<init>(r8)
            r7.emojiSearchGridView = r0
            androidx.recyclerview.widget.RecyclerView$ItemAnimator r0 = r0.getItemAnimator()
            if (r0 == 0) goto L_0x03b9
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r0 = r7.emojiSearchGridView
            androidx.recyclerview.widget.RecyclerView$ItemAnimator r0 = r0.getItemAnimator()
            r13 = 180(0xb4, double:8.9E-322)
            r0.setDurations(r13)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r0 = r7.emojiSearchGridView
            androidx.recyclerview.widget.RecyclerView$ItemAnimator r0 = r0.getItemAnimator()
            r0.setMoveInterpolator(r2)
        L_0x03b9:
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r8)
            int r2 = org.telegram.messenger.R.string.NoEmojiFound
            java.lang.String r3 = "NoEmojiFound"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setText(r2)
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r0.setTextSize(r3, r2)
            java.lang.String r2 = "chat_emojiPanelEmptyText"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            r0.setTextColor(r2)
            org.telegram.ui.Components.BackupImageView r2 = new org.telegram.ui.Components.BackupImageView
            r2.<init>(r8)
            r7.emojiSearchEmptyViewImageView = r2
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r8)
            r7.emojiSearchEmptyView = r2
            org.telegram.ui.Components.BackupImageView r3 = r7.emojiSearchEmptyViewImageView
            r19 = 36
            r20 = 1108344832(0x42100000, float:36.0)
            r21 = 49
            r22 = 0
            r23 = 1098907648(0x41800000, float:16.0)
            r24 = 0
            r25 = 0
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r2.addView(r3, r13)
            android.widget.FrameLayout r2 = r7.emojiSearchEmptyView
            r19 = -2
            r20 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r23 = 1114636288(0x42700000, float:60.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r2.addView(r0, r3)
            android.widget.FrameLayout r0 = r7.emojiSearchEmptyView
            r0.setVisibility(r5)
            android.widget.FrameLayout r0 = r7.emojiSearchEmptyView
            r2 = 0
            r0.setAlpha(r2)
            android.widget.FrameLayout r0 = r7.gridViewContainer
            android.widget.FrameLayout r2 = r7.emojiSearchEmptyView
            r19 = -1
            r21 = 16
            r23 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r2, r3)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r0 = r7.emojiSearchGridView
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r3 = 1113063424(0x42580000, float:54.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r13 = 1073741824(0x40000000, float:2.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r0.setPadding(r2, r3, r1, r13)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r0 = r7.emojiSearchGridView
            org.telegram.ui.SelectAnimatedEmojiDialog$SearchAdapter r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$SearchAdapter
            r1.<init>()
            r7.searchAdapter = r1
            r0.setAdapter(r1)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r0 = r7.emojiSearchGridView
            org.telegram.ui.SelectAnimatedEmojiDialog$10 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$10
            r1.<init>(r8, r5)
            r0.setLayoutManager(r1)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r0 = r7.emojiSearchGridView
            r0.setVisibility(r5)
            android.widget.FrameLayout r0 = r7.gridViewContainer
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r1 = r7.emojiSearchGridView
            r20 = -1082130432(0xffffffffbvar_, float:-1.0)
            r21 = 119(0x77, float:1.67E-43)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = r7.contentView
            android.widget.FrameLayout r1 = r7.gridViewContainer
            r21 = 48
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r3 = 1065353216(0x3var_, float:1.0)
            float r14 = r3 / r2
            float r23 = r14 + r6
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper r0 = new org.telegram.ui.Components.RecyclerAnimationScrollHelper
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r1 = r7.emojiGridView
            androidx.recyclerview.widget.GridLayoutManager r2 = r7.layoutManager
            r0.<init>(r1, r2)
            r7.scrollHelper = r0
            org.telegram.ui.SelectAnimatedEmojiDialog$11 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$11
            r1.<init>()
            r0.setAnimationCallback(r1)
            org.telegram.ui.SelectAnimatedEmojiDialog$12 r13 = new org.telegram.ui.SelectAnimatedEmojiDialog$12
            r0 = r13
            r1 = r34
            r2 = r39
            r3 = r36
            r4 = r40
            r5 = r38
            r0.<init>(r2, r3, r4, r5)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r0 = r7.emojiGridView
            int r1 = android.view.ViewConfiguration.getLongPressTimeout()
            float r1 = (float) r1
            r2 = 1048576000(0x3e800000, float:0.25)
            float r1 = r1 * r2
            long r3 = (long) r1
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended) r13, (long) r3)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r0 = r7.emojiSearchGridView
            int r1 = android.view.ViewConfiguration.getLongPressTimeout()
            float r1 = (float) r1
            float r1 = r1 * r2
            long r1 = (long) r1
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended) r13, (long) r1)
            org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda18 r0 = new org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda18
            r0.<init>(r7, r10)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r1 = r7.emojiGridView
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r0)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r1 = r7.emojiSearchGridView
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r0)
            org.telegram.ui.SelectAnimatedEmojiDialog$SearchBox r0 = new org.telegram.ui.SelectAnimatedEmojiDialog$SearchBox
            r0.<init>(r8)
            r7.searchBox = r0
            r1 = 1113587712(0x42600000, float:56.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            r0.setTranslationY(r1)
            org.telegram.ui.SelectAnimatedEmojiDialog$SearchBox r0 = r7.searchBox
            r1 = 4
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r7.gridViewContainer
            org.telegram.ui.SelectAnimatedEmojiDialog$SearchBox r1 = r7.searchBox
            r20 = 1112539136(0x42500000, float:52.0)
            r23 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r1, r2)
            org.telegram.ui.SelectAnimatedEmojiDialog$13 r0 = new org.telegram.ui.SelectAnimatedEmojiDialog$13
            r0.<init>(r7, r8, r15)
            r7.topGradientView = r0
            android.content.res.Resources r0 = r34.getResources()
            int r1 = org.telegram.messenger.R.drawable.gradient_top
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            r2 = r26
            int r3 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            r4 = 1061997773(0x3f4ccccd, float:0.8)
            int r3 = org.telegram.messenger.AndroidUtilities.multiplyAlphaComponent(r3, r4)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.SRC_IN
            r1.<init>(r3, r5)
            r0.setColorFilter(r1)
            android.view.View r1 = r7.topGradientView
            r1.setBackground(r0)
            android.view.View r0 = r7.topGradientView
            r1 = 0
            r0.setAlpha(r1)
            android.widget.FrameLayout r0 = r7.contentView
            android.view.View r1 = r7.topGradientView
            r20 = 1101004800(0x41a00000, float:20.0)
            r21 = 55
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r5 = 1065353216(0x3var_, float:1.0)
            float r14 = r5 / r3
            float r23 = r14 + r6
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r1, r3)
            android.view.View r0 = new android.view.View
            r0.<init>(r8)
            r7.bottomGradientView = r0
            android.content.res.Resources r0 = r34.getResources()
            int r1 = org.telegram.messenger.R.drawable.gradient_bottom
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            int r2 = org.telegram.messenger.AndroidUtilities.multiplyAlphaComponent(r2, r4)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.SRC_IN
            r1.<init>(r2, r3)
            r0.setColorFilter(r1)
            android.view.View r1 = r7.bottomGradientView
            r1.setBackground(r0)
            android.view.View r0 = r7.bottomGradientView
            r1 = 0
            r0.setAlpha(r1)
            android.widget.FrameLayout r0 = r7.contentView
            android.view.View r2 = r7.bottomGradientView
            r3 = 20
            r4 = 87
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r12, (int) r3, (int) r4)
            r0.addView(r2, r3)
            android.view.View r0 = new android.view.View
            r0.<init>(r8)
            r7.contentViewForeground = r0
            r0.setAlpha(r1)
            android.view.View r0 = r7.contentViewForeground
            r1 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout r0 = r7.contentView
            android.view.View r1 = r7.contentViewForeground
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r2)
            r0.addView(r1, r2)
            int r0 = r7.currentAccount
            preload(r0)
            org.telegram.messenger.ImageReceiver r0 = r7.bigReactionImageReceiver
            r1 = 7
            r0.setLayerNum(r1)
            r0 = 0
            r7.updateRows(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SelectAnimatedEmojiDialog.<init>(org.telegram.ui.ActionBar.BaseFragment, android.content.Context, boolean, java.lang.Integer, int, org.telegram.ui.ActionBar.Theme$ResourcesProvider, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
        Runnable runnable;
        if (motionEvent.getAction() != 0 || (runnable = this.dismiss) == null) {
            return false;
        }
        runnable.run();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$1(View view) {
        onRecentLongClick();
        try {
            performHapticFeedback(0, 1);
        } catch (Exception unused) {
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(int i, View view, int i2) {
        if (view instanceof ImageViewEmoji) {
            ImageViewEmoji imageViewEmoji = (ImageViewEmoji) view;
            if (imageViewEmoji.isDefaultReaction) {
                incrementHintUse();
                onReactionClick(imageViewEmoji, imageViewEmoji.reaction);
            } else {
                onEmojiClick(imageViewEmoji, imageViewEmoji.span);
            }
            if (i != 1) {
                try {
                    performHapticFeedback(3, 1);
                } catch (Exception unused) {
                }
            }
        } else if (view instanceof ImageView) {
            onEmojiClick(view, (AnimatedEmojiSpan) null);
            if (i != 1) {
                performHapticFeedback(3, 1);
            }
        } else if (view instanceof EmojiPackExpand) {
            expand(i2, (EmojiPackExpand) view);
            if (i != 1) {
                performHapticFeedback(3, 1);
            }
        } else if (view != null) {
            view.callOnClick();
        }
    }

    public void setExpireDateHint(int i) {
        this.includeHint = true;
        this.hintExpireDate = Integer.valueOf(i);
        updateRows(false);
    }

    /* access modifiers changed from: private */
    public void setBigReactionAnimatedEmoji(AnimatedEmojiDrawable animatedEmojiDrawable) {
        AnimatedEmojiDrawable animatedEmojiDrawable2;
        if (this.isAttached && (animatedEmojiDrawable2 = this.bigReactionAnimatedEmoji) != animatedEmojiDrawable) {
            if (animatedEmojiDrawable2 != null) {
                animatedEmojiDrawable2.removeView((View) this);
            }
            this.bigReactionAnimatedEmoji = animatedEmojiDrawable;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.addView((View) this);
            }
        }
    }

    private void onRecentLongClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), (Theme.ResourcesProvider) null);
        builder.setTitle(LocaleController.getString("ClearRecentEmojiStatusesTitle", R.string.ClearRecentEmojiStatusesTitle));
        builder.setMessage(LocaleController.getString("ClearRecentEmojiStatusesText", R.string.ClearRecentEmojiStatusesText));
        builder.setPositiveButton(LocaleController.getString("Clear", R.string.Clear).toUpperCase(), new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda6(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setDimEnabled(false);
        builder.setOnDismissListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda7(this));
        builder.show();
        setDim(1.0f, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRecentLongClick$3(DialogInterface dialogInterface, int i) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_clearRecentEmojiStatuses(), (RequestDelegate) null);
        MediaDataController.getInstance(this.currentAccount).clearRecentEmojiStatuses();
        updateRows(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRecentLongClick$4(DialogInterface dialogInterface) {
        setDim(0.0f, true);
    }

    private void setDim(float f, boolean z) {
        ValueAnimator valueAnimator = this.dimAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.dimAnimator = null;
        }
        if (z) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.contentViewForeground.getAlpha(), f * 0.25f});
            this.dimAnimator = ofFloat;
            ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda1(this));
            this.dimAnimator.setDuration(200);
            this.dimAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.dimAnimator.start();
            return;
        }
        this.contentViewForeground.setAlpha(f * 0.25f);
        int blendOver = Theme.blendOver(Theme.getColor("actionBarDefaultSubmenuBackground", this.resourcesProvider), ColorUtils.setAlphaComponent(-16777216, (int) (f * 255.0f * 0.25f)));
        View view = this.bubble1View;
        if (view != null) {
            view.getBackground().setColorFilter(new PorterDuffColorFilter(blendOver, PorterDuff.Mode.MULTIPLY));
        }
        View view2 = this.bubble2View;
        if (view2 != null) {
            view2.getBackground().setColorFilter(new PorterDuffColorFilter(blendOver, PorterDuff.Mode.MULTIPLY));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setDim$5(ValueAnimator valueAnimator) {
        this.contentViewForeground.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        int blendOver = Theme.blendOver(Theme.getColor("actionBarDefaultSubmenuBackground", this.resourcesProvider), ColorUtils.setAlphaComponent(-16777216, (int) (((Float) valueAnimator.getAnimatedValue()).floatValue() * 255.0f)));
        View view = this.bubble1View;
        if (view != null) {
            view.getBackground().setColorFilter(new PorterDuffColorFilter(blendOver, PorterDuff.Mode.MULTIPLY));
        }
        View view2 = this.bubble2View;
        if (view2 != null) {
            view2.getBackground().setColorFilter(new PorterDuffColorFilter(blendOver, PorterDuff.Mode.MULTIPLY));
        }
    }

    /* access modifiers changed from: private */
    public void updateTabsPosition(int i) {
        if (i != -1) {
            int spanCount = this.layoutManager.getSpanCount() * 5;
            if (this.recent.size() <= spanCount || this.recentExpanded) {
                spanCount = this.recent.size() + (this.includeEmpty ? 1 : 0);
            }
            if (i <= spanCount || i <= this.recentReactions.size()) {
                this.emojiTabs.select(0);
                return;
            }
            int spanCount2 = this.layoutManager.getSpanCount() * 3;
            for (int i2 = 0; i2 < this.positionToSection.size(); i2++) {
                int keyAt = this.positionToSection.keyAt(i2);
                int i3 = i2 - (this.defaultStatuses.isEmpty() ^ true ? 1 : 0);
                EmojiView.EmojiPack emojiPack = i3 >= 0 ? this.packs.get(i3) : null;
                if (emojiPack != null) {
                    boolean z = emojiPack.expanded;
                    int size = emojiPack.documents.size();
                    if (!z) {
                        size = Math.min(spanCount2, size);
                    }
                    if (i > keyAt && i <= keyAt + 1 + size) {
                        this.emojiTabs.select(i2 + 1);
                        return;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSearchBox() {
        SearchBox searchBox2 = this.searchBox;
        if (searchBox2 != null) {
            if (this.searching) {
                searchBox2.clearAnimation();
                this.searchBox.setVisibility(0);
                this.searchBox.animate().translationY((float) (-AndroidUtilities.dp(4.0f))).start();
            } else if (this.emojiGridView.getChildCount() > 0) {
                View childAt = this.emojiGridView.getChildAt(0);
                if (this.emojiGridView.getChildAdapterPosition(childAt) != this.searchRow || !"searchbox".equals(childAt.getTag())) {
                    this.searchBox.setTranslationY((float) (-AndroidUtilities.dp(56.0f)));
                    return;
                }
                this.searchBox.setVisibility(0);
                this.searchBox.setTranslationY(childAt.getY() - ((float) AndroidUtilities.dp(4.0f)));
            } else {
                this.searchBox.setTranslationY((float) (-AndroidUtilities.dp(56.0f)));
            }
        }
    }

    /* access modifiers changed from: private */
    public Drawable getPremiumStar() {
        if (this.premiumStar == null) {
            Drawable mutate = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.msg_settings_premium).mutate();
            this.premiumStar = mutate;
            mutate.setColorFilter(this.premiumStarColorFilter);
        }
        return this.premiumStar;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.scrimDrawable;
        if (!(swapAnimatedEmojiDrawable == null || this.emojiX == null)) {
            Rect bounds = swapAnimatedEmojiDrawable.getBounds();
            View view = this.scrimDrawableParent;
            float scaleY2 = view == null ? 1.0f : view.getScaleY();
            int i = 255;
            if (Build.VERSION.SDK_INT >= 19) {
                i = this.scrimDrawable.getAlpha();
            }
            View view2 = this.scrimDrawableParent;
            int height = view2 == null ? bounds.height() : view2.getHeight();
            canvas.save();
            canvas2.translate(0.0f, -getTranslationY());
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable2 = this.scrimDrawable;
            double d = (double) i;
            double pow = Math.pow((double) this.contentView.getAlpha(), 0.25d);
            Double.isNaN(d);
            double d2 = d * pow;
            double d3 = (double) this.scrimAlpha;
            Double.isNaN(d3);
            swapAnimatedEmojiDrawable2.setAlpha((int) (d2 * d3));
            if (this.drawableToBounds == null) {
                this.drawableToBounds = new Rect();
            }
            this.drawableToBounds.set((int) (((((float) bounds.centerX()) - ((((float) bounds.width()) / 2.0f) * scaleY2)) - ((float) bounds.centerX())) + ((float) this.emojiX.intValue()) + ((float) ((scaleY2 <= 1.0f || scaleY2 >= 1.5f) ? 0 : 2))), (int) ((((((((float) (height - (height - bounds.bottom))) * scaleY2) - (scaleY2 > 1.5f ? (((float) bounds.height()) * 0.81f) + 1.0f : 0.0f)) - ((float) bounds.top)) - (((float) bounds.height()) / 2.0f)) + ((float) AndroidUtilities.dp((float) this.topMarginDp))) - (((float) bounds.height()) * scaleY2)), (int) (((((float) bounds.centerX()) + ((((float) bounds.width()) / 2.0f) * scaleY2)) - ((float) bounds.centerX())) + ((float) this.emojiX.intValue()) + ((float) ((scaleY2 <= 1.0f || scaleY2 >= 1.5f) ? 0 : 2))), (int) (((((((float) (height - (height - bounds.bottom))) * scaleY2) - (scaleY2 > 1.5f ? (((float) bounds.height()) * 0.81f) + 1.0f : 0.0f)) - ((float) bounds.top)) - (((float) bounds.height()) / 2.0f)) + ((float) AndroidUtilities.dp((float) this.topMarginDp))));
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable3 = this.scrimDrawable;
            Rect rect = this.drawableToBounds;
            int i2 = rect.left;
            int i3 = rect.top;
            int width = (int) (((float) i2) + (((float) rect.width()) / scaleY2));
            Rect rect2 = this.drawableToBounds;
            swapAnimatedEmojiDrawable3.setBounds(i2, i3, width, (int) (((float) rect2.top) + (((float) rect2.height()) / scaleY2)));
            Rect rect3 = this.drawableToBounds;
            canvas2.scale(scaleY2, scaleY2, (float) rect3.left, (float) rect3.top);
            this.scrimDrawable.draw(canvas2);
            this.scrimDrawable.setAlpha(i);
            this.scrimDrawable.setBounds(bounds);
            canvas.restore();
        }
        super.dispatchDraw(canvas);
        ImageViewEmoji imageViewEmoji = this.emojiSelectView;
        if (imageViewEmoji != null && this.emojiSelectRect != null && this.drawableToBounds != null && imageViewEmoji.drawable != null) {
            canvas.save();
            canvas2.translate(0.0f, -getTranslationY());
            this.emojiSelectView.drawable.setAlpha((int) (this.emojiSelectAlpha * 255.0f));
            this.emojiSelectView.drawable.setBounds(this.emojiSelectRect);
            this.emojiSelectView.drawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhiteBlueIcon", this.resourcesProvider), this.scrimColor, 1.0f - this.scrimAlpha), PorterDuff.Mode.MULTIPLY));
            this.emojiSelectView.drawable.draw(canvas2);
            canvas.restore();
        }
    }

    public void animateEmojiSelect(ImageViewEmoji imageViewEmoji, final Runnable runnable) {
        if (this.emojiSelectAnimator != null || this.scrimDrawable == null) {
            runnable.run();
            return;
        }
        imageViewEmoji.notDraw = true;
        Rect rect = new Rect();
        rect.set(this.contentView.getLeft() + this.emojiGridView.getLeft() + imageViewEmoji.getLeft(), this.contentView.getTop() + this.emojiGridView.getTop() + imageViewEmoji.getTop(), this.contentView.getLeft() + this.emojiGridView.getLeft() + imageViewEmoji.getRight(), this.contentView.getTop() + this.emojiGridView.getTop() + imageViewEmoji.getBottom());
        Drawable drawable = imageViewEmoji.drawable;
        AnimatedEmojiDrawable make = drawable instanceof AnimatedEmojiDrawable ? AnimatedEmojiDrawable.make(this.currentAccount, 7, ((AnimatedEmojiDrawable) drawable).getDocumentId()) : null;
        this.emojiSelectView = imageViewEmoji;
        Rect rect2 = new Rect();
        this.emojiSelectRect = rect2;
        rect2.set(rect);
        final boolean[] zArr = new boolean[1];
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.emojiSelectAnimator = ofFloat;
        ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda3(this, rect, imageViewEmoji, zArr, runnable, make));
        this.emojiSelectAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ImageViewEmoji unused = SelectAnimatedEmojiDialog.this.emojiSelectView = null;
                SelectAnimatedEmojiDialog.this.invalidate();
                boolean[] zArr = zArr;
                if (!zArr[0]) {
                    zArr[0] = true;
                    runnable.run();
                }
            }
        });
        this.emojiSelectAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.emojiSelectAnimator.setDuration(260);
        this.emojiSelectAnimator.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateEmojiSelect$6(Rect rect, ImageViewEmoji imageViewEmoji, boolean[] zArr, Runnable runnable, AnimatedEmojiDrawable animatedEmojiDrawable, ValueAnimator valueAnimator) {
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable;
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.scrimAlpha = 1.0f - ((floatValue * floatValue) * floatValue);
        this.emojiSelectAlpha = 1.0f - ((float) Math.pow((double) floatValue, 10.0d));
        AndroidUtilities.lerp(rect, this.drawableToBounds, floatValue, this.emojiSelectRect);
        float max = Math.max(1.0f, this.overshootInterpolator.getInterpolation(MathUtils.clamp((3.0f * floatValue) - 2.0f, 0.0f, 1.0f))) * imageViewEmoji.getScaleX();
        Rect rect2 = this.emojiSelectRect;
        rect2.set((int) (((float) rect2.centerX()) - ((((float) this.emojiSelectRect.width()) / 2.0f) * max)), (int) (((float) this.emojiSelectRect.centerY()) - ((((float) this.emojiSelectRect.height()) / 2.0f) * max)), (int) (((float) this.emojiSelectRect.centerX()) + ((((float) this.emojiSelectRect.width()) / 2.0f) * max)), (int) (((float) this.emojiSelectRect.centerY()) + ((((float) this.emojiSelectRect.height()) / 2.0f) * max)));
        invalidate();
        if (floatValue > 0.85f && !zArr[0]) {
            zArr[0] = true;
            runnable.run();
            if (animatedEmojiDrawable != null && (swapAnimatedEmojiDrawable = this.scrimDrawable) != null) {
                swapAnimatedEmojiDrawable.play();
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkScroll() {
        boolean canScrollVertically = this.emojiGridView.canScrollVertically(1);
        if (canScrollVertically != this.bottomGradientShown) {
            this.bottomGradientShown = canScrollVertically;
            this.bottomGradientView.animate().alpha(canScrollVertically ? 1.0f : 0.0f).setDuration(200).start();
        }
    }

    /* access modifiers changed from: private */
    public void scrollToPosition(int i, int i2) {
        View findViewByPosition = this.layoutManager.findViewByPosition(i);
        int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
        if ((findViewByPosition != null || ((float) Math.abs(i - findFirstVisibleItemPosition)) <= ((float) this.layoutManager.getSpanCount()) * 9.0f) && SharedConfig.animationsEnabled()) {
            AnonymousClass15 r0 = new LinearSmoothScrollerCustom(this.emojiGridView.getContext(), 2) {
                public void onEnd() {
                    boolean unused = SelectAnimatedEmojiDialog.this.smoothScrolling = false;
                }

                /* access modifiers changed from: protected */
                public void onStart() {
                    boolean unused = SelectAnimatedEmojiDialog.this.smoothScrolling = true;
                }
            };
            r0.setTargetPosition(i);
            r0.setOffset(i2);
            this.layoutManager.startSmoothScroll(r0);
            return;
        }
        this.scrollHelper.setScrollDirection(this.layoutManager.findFirstVisibleItemPosition() < i ? 0 : 1);
        this.scrollHelper.scrollToPosition(i, i2, false, true);
    }

    public void switchGrids(final boolean z) {
        if (this.gridSearch != z) {
            this.gridSearch = z;
            this.emojiGridView.setVisibility(0);
            this.emojiSearchGridView.setVisibility(0);
            ValueAnimator valueAnimator = this.gridSwitchAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator valueAnimator2 = this.searchEmptyViewAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
                this.searchEmptyViewAnimator = null;
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.gridSwitchAnimator = ofFloat;
            ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda4(this, z));
            this.gridSwitchAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    int i = 8;
                    SelectAnimatedEmojiDialog.this.emojiSearchGridView.setVisibility(z ? 0 : 8);
                    EmojiListView emojiListView = SelectAnimatedEmojiDialog.this.emojiGridView;
                    if (!z) {
                        i = 0;
                    }
                    emojiListView.setVisibility(i);
                    ValueAnimator unused = SelectAnimatedEmojiDialog.this.gridSwitchAnimator = null;
                    if (!z && SelectAnimatedEmojiDialog.this.searchResult != null) {
                        SelectAnimatedEmojiDialog.this.searchResult.clear();
                        SelectAnimatedEmojiDialog.this.searchAdapter.updateRows(false);
                    }
                }
            });
            this.gridSwitchAnimator.setDuration(280);
            this.gridSwitchAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.gridSwitchAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$switchGrids$7(boolean z, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (!z) {
            floatValue = 1.0f - floatValue;
        }
        this.emojiGridView.setAlpha(1.0f - floatValue);
        this.emojiSearchGridView.setAlpha(floatValue);
        this.emojiSearchEmptyView.setAlpha(this.emojiSearchGridView.getAlpha() * floatValue);
    }

    public void updateSearchEmptyViewImage() {
        ImageLocation forDocument;
        if (this.emojiSearchEmptyViewImageView != null) {
            ArrayList arrayList = new ArrayList(MediaDataController.getInstance(this.currentAccount).getFeaturedEmojiSets());
            Collections.shuffle(arrayList);
            TLRPC$Document tLRPC$Document = null;
            for (int i = 0; i < arrayList.size(); i++) {
                if ((arrayList.get(i) instanceof TLRPC$TL_stickerSetFullCovered) && ((TLRPC$TL_stickerSetFullCovered) arrayList.get(i)).documents != null) {
                    ArrayList arrayList2 = new ArrayList(((TLRPC$TL_stickerSetFullCovered) arrayList.get(i)).documents);
                    Collections.shuffle(arrayList2);
                    int i2 = 0;
                    while (true) {
                        if (i2 < arrayList2.size()) {
                            TLRPC$Document tLRPC$Document2 = (TLRPC$Document) arrayList2.get(i2);
                            if (tLRPC$Document2 != null && this.emptyViewEmojis.contains(MessageObject.findAnimatedEmojiEmoticon(tLRPC$Document2, (String) null))) {
                                tLRPC$Document = tLRPC$Document2;
                                break;
                            }
                            i2++;
                        } else {
                            break;
                        }
                    }
                }
                if (tLRPC$Document != null) {
                    break;
                }
            }
            if (tLRPC$Document == null) {
                ArrayList arrayList3 = new ArrayList(MediaDataController.getInstance(this.currentAccount).getStickerSets(5));
                Collections.shuffle(arrayList3);
                for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                    if (arrayList3.get(i3) != null && ((TLRPC$TL_messages_stickerSet) arrayList3.get(i3)).documents != null) {
                        ArrayList arrayList4 = new ArrayList(((TLRPC$TL_messages_stickerSet) arrayList3.get(i3)).documents);
                        Collections.shuffle(arrayList4);
                        int i4 = 0;
                        while (true) {
                            if (i4 < arrayList4.size()) {
                                TLRPC$Document tLRPC$Document3 = (TLRPC$Document) arrayList4.get(i4);
                                if (tLRPC$Document3 != null && this.emptyViewEmojis.contains(MessageObject.findAnimatedEmojiEmoticon(tLRPC$Document3, (String) null))) {
                                    tLRPC$Document = tLRPC$Document3;
                                    break;
                                }
                                i4++;
                            } else {
                                break;
                            }
                        }
                    }
                    if (tLRPC$Document != null) {
                        break;
                    }
                }
            }
            TLRPC$Document tLRPC$Document4 = tLRPC$Document;
            if (tLRPC$Document4 != null) {
                String str = "36_36";
                SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$Document4.thumbs, "windowBackgroundWhiteGrayIcon", 0.2f);
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document4.thumbs, 90);
                if ("video/webm".equals(tLRPC$Document4.mime_type)) {
                    forDocument = ImageLocation.getForDocument(tLRPC$Document4);
                    str = str + "_" + "g";
                    if (svgThumb != null) {
                        svgThumb.overrideWidthAndHeight(512, 512);
                    }
                } else {
                    if (svgThumb != null && MessageObject.isAnimatedStickerDocument(tLRPC$Document4, false)) {
                        svgThumb.overrideWidthAndHeight(512, 512);
                    }
                    forDocument = ImageLocation.getForDocument(tLRPC$Document4);
                }
                String str2 = str;
                ImageLocation imageLocation = forDocument;
                this.emojiSearchEmptyViewImageView.setLayerNum(7);
                this.emojiSearchEmptyViewImageView.setRoundRadius(AndroidUtilities.dp(4.0f));
                this.emojiSearchEmptyViewImageView.setImage(imageLocation, str2, ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$Document4), "36_36", (Drawable) svgThumb, (Object) tLRPC$Document4);
            }
        }
    }

    public void switchSearchEmptyView(final boolean z) {
        if (this.searchEmptyViewVisible != z) {
            this.searchEmptyViewVisible = z;
            ValueAnimator valueAnimator = this.searchEmptyViewAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.searchEmptyViewAnimator = ofFloat;
            ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda5(this, z));
            this.searchEmptyViewAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = SelectAnimatedEmojiDialog.this;
                    selectAnimatedEmojiDialog.emojiSearchEmptyView.setVisibility((!z || selectAnimatedEmojiDialog.emojiSearchGridView.getVisibility() != 0) ? 8 : 0);
                    ValueAnimator unused = SelectAnimatedEmojiDialog.this.searchEmptyViewAnimator = null;
                }
            });
            this.searchEmptyViewAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.searchEmptyViewAnimator.setDuration(100);
            this.searchEmptyViewAnimator.start();
            if (z) {
                updateSearchEmptyViewImage();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$switchSearchEmptyView$8(boolean z, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (!z) {
            floatValue = 1.0f - floatValue;
        }
        this.emojiSearchEmptyView.setAlpha(this.emojiSearchGridView.getAlpha() * floatValue);
    }

    public void search(String str) {
        Runnable runnable = this.clearSearchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.clearSearchRunnable = null;
        }
        Runnable runnable2 = this.searchRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.searchRunnable = null;
        }
        boolean z = true;
        if (str == null) {
            this.searching = false;
            this.searched = false;
            SearchBox searchBox2 = this.searchBox;
            if (!(searchBox2 == null || searchBox2.clearDrawable == null)) {
                this.searchBox.clearDrawable.stopAnimation();
            }
            this.searchAdapter.updateRows(true);
            this.lastQuery = null;
        } else {
            boolean z2 = !this.searching;
            this.searching = true;
            this.searched = false;
            SearchBox searchBox3 = this.searchBox;
            if (!(searchBox3 == null || searchBox3.clearDrawable == null)) {
                this.searchBox.clearDrawable.startAnimation();
            }
            if (!str.equals(this.lastQuery)) {
                SelectAnimatedEmojiDialog$$ExternalSyntheticLambda10 selectAnimatedEmojiDialog$$ExternalSyntheticLambda10 = new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda10(this);
                this.clearSearchRunnable = selectAnimatedEmojiDialog$$ExternalSyntheticLambda10;
                AndroidUtilities.runOnUIThread(selectAnimatedEmojiDialog$$ExternalSyntheticLambda10, 120);
            }
            this.lastQuery = str;
            String[] currentKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
            if (!Arrays.equals(currentKeyboardLanguage, lastSearchKeyboardLanguage)) {
                MediaDataController.getInstance(this.currentAccount).fetchNewEmojiKeywords(currentKeyboardLanguage);
            }
            lastSearchKeyboardLanguage = currentKeyboardLanguage;
            SelectAnimatedEmojiDialog$$ExternalSyntheticLambda15 selectAnimatedEmojiDialog$$ExternalSyntheticLambda15 = new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda15(this, str, z2);
            this.searchRunnable = selectAnimatedEmojiDialog$$ExternalSyntheticLambda15;
            AndroidUtilities.runOnUIThread(selectAnimatedEmojiDialog$$ExternalSyntheticLambda15, 425);
        }
        switchGrids(this.searching);
        updateSearchBox();
        float f = 0.0f;
        ((View) this.emojiGridView.getParent()).animate().translationY(this.searching ? (float) (-AndroidUtilities.dp(36.0f)) : 0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(160).start();
        SearchBox searchBox4 = this.searchBox;
        if (searchBox4 != null && searchBox4.clear != null) {
            if (this.searchBox.clear.getAlpha() == 0.0f) {
                z = false;
            }
            if (this.searching != z) {
                ViewPropertyAnimator animate = this.searchBox.clear.animate();
                float f2 = 1.0f;
                if (this.searching) {
                    f = 1.0f;
                }
                ViewPropertyAnimator scaleX2 = animate.alpha(f).setDuration(150).scaleX(this.searching ? 1.0f : 0.1f);
                if (!this.searching) {
                    f2 = 0.1f;
                }
                scaleX2.scaleY(f2).start();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$search$9() {
        ArrayList<Long> arrayList = this.searchResult;
        if (arrayList != null) {
            arrayList.clear();
        }
        this.searchAdapter.updateRows(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$search$13(String str, boolean z) {
        MediaDataController.getInstance(this.currentAccount).getAnimatedEmojiByKeywords(str, new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda17(this, str, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$search$12(String str, boolean z, ArrayList arrayList) {
        ArrayList<TLRPC$Document> arrayList2;
        ArrayList<TLRPC$Document> arrayList3;
        if (arrayList == null) {
            arrayList = new ArrayList();
        }
        if (Emoji.fullyConsistsOfEmojis(str)) {
            ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(5);
            str.toLowerCase();
            for (int i = 0; i < stickerSets.size(); i++) {
                if (!(stickerSets.get(i).documents == null || (arrayList3 = stickerSets.get(i).documents) == null)) {
                    for (int i2 = 0; i2 < arrayList3.size(); i2++) {
                        String findAnimatedEmojiEmoticon = MessageObject.findAnimatedEmojiEmoticon(arrayList3.get(i2), (String) null);
                        long j = arrayList3.get(i2).id;
                        if (findAnimatedEmojiEmoticon != null && !arrayList.contains(Long.valueOf(j)) && str.contains(findAnimatedEmojiEmoticon)) {
                            arrayList.add(Long.valueOf(j));
                        }
                    }
                }
            }
            ArrayList<TLRPC$StickerSetCovered> featuredEmojiSets = MediaDataController.getInstance(this.currentAccount).getFeaturedEmojiSets();
            for (int i3 = 0; i3 < featuredEmojiSets.size(); i3++) {
                if (!(!(featuredEmojiSets.get(i3) instanceof TLRPC$TL_stickerSetFullCovered) || ((TLRPC$TL_stickerSetFullCovered) featuredEmojiSets.get(i3)).keywords == null || (arrayList2 = ((TLRPC$TL_stickerSetFullCovered) featuredEmojiSets.get(i3)).documents) == null)) {
                    for (int i4 = 0; i4 < arrayList2.size(); i4++) {
                        String findAnimatedEmojiEmoticon2 = MessageObject.findAnimatedEmojiEmoticon(arrayList2.get(i4), (String) null);
                        long j2 = arrayList2.get(i4).id;
                        if (findAnimatedEmojiEmoticon2 != null && !arrayList.contains(Long.valueOf(j2)) && str.contains(findAnimatedEmojiEmoticon2)) {
                            arrayList.add(Long.valueOf(j2));
                        }
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda14(this, str, arrayList, z));
            return;
        }
        MediaDataController.getInstance(this.currentAccount).getEmojiSuggestions(lastSearchKeyboardLanguage, str, false, new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda16(this, str, arrayList, z), (CountDownLatch) null, true, 30);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$search$10(String str, ArrayList arrayList, boolean z) {
        Runnable runnable = this.clearSearchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.clearSearchRunnable = null;
        }
        if (str == this.lastQuery) {
            this.searched = true;
            SearchBox searchBox2 = this.searchBox;
            if (!(searchBox2 == null || searchBox2.clearDrawable == null)) {
                this.searchBox.clearDrawable.stopAnimation();
            }
            ArrayList<Long> arrayList2 = this.searchResult;
            if (arrayList2 == null) {
                this.searchResult = new ArrayList<>();
            } else {
                arrayList2.clear();
            }
            this.emojiSearchGridView.scrollToPosition(0);
            this.searchResult.addAll(arrayList);
            this.searchAdapter.updateRows(true ^ z);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$search$11(String str, ArrayList arrayList, boolean z, ArrayList arrayList2, String str2) {
        Runnable runnable = this.clearSearchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.clearSearchRunnable = null;
        }
        if (str == this.lastQuery) {
            this.searched = true;
            SearchBox searchBox2 = this.searchBox;
            if (!(searchBox2 == null || searchBox2.clearDrawable == null)) {
                this.searchBox.clearDrawable.stopAnimation();
            }
            for (int i = 0; i < arrayList2.size(); i++) {
                try {
                    if (((MediaDataController.KeywordResult) arrayList2.get(i)).emoji.startsWith("animated_")) {
                        arrayList.add(Long.valueOf(Long.parseLong(((MediaDataController.KeywordResult) arrayList2.get(i)).emoji.substring(9))));
                    }
                } catch (Exception unused) {
                }
            }
            ArrayList<Long> arrayList3 = this.searchResult;
            if (arrayList3 == null) {
                this.searchResult = new ArrayList<>();
            } else {
                arrayList3.clear();
            }
            this.emojiSearchGridView.scrollToPosition(0);
            this.searched = true;
            this.searchResult.addAll(arrayList);
            this.searchAdapter.updateRows(true ^ z);
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        public int VIEW_TYPE_EMOJI;
        public int VIEW_TYPE_SEARCH;
        private int count;
        /* access modifiers changed from: private */
        public ArrayList<Integer> rowHashCodes;

        private SearchAdapter() {
            this.VIEW_TYPE_SEARCH = 7;
            this.VIEW_TYPE_EMOJI = 3;
            this.count = 1;
            this.rowHashCodes = new ArrayList<>();
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == this.VIEW_TYPE_EMOJI;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == this.VIEW_TYPE_SEARCH) {
                view = new View(this, SelectAnimatedEmojiDialog.this.getContext()) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(52.0f), NUM));
                    }
                };
                view.setTag("searchbox");
            } else {
                SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = SelectAnimatedEmojiDialog.this;
                view = new ImageViewEmoji(selectAnimatedEmojiDialog.getContext());
            }
            if (SelectAnimatedEmojiDialog.this.showAnimator != null && SelectAnimatedEmojiDialog.this.showAnimator.isRunning()) {
                view.setScaleX(0.0f);
                view.setScaleY(0.0f);
            }
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int i) {
            return this.VIEW_TYPE_EMOJI;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == this.VIEW_TYPE_EMOJI) {
                ImageViewEmoji imageViewEmoji = (ImageViewEmoji) viewHolder.itemView;
                boolean z = false;
                imageViewEmoji.empty = false;
                imageViewEmoji.position = i;
                imageViewEmoji.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                imageViewEmoji.setDrawable((Drawable) null);
                if (SelectAnimatedEmojiDialog.this.searchResult != null && i >= 0 && i < SelectAnimatedEmojiDialog.this.searchResult.size()) {
                    long longValue = ((Long) SelectAnimatedEmojiDialog.this.searchResult.get(i)).longValue();
                    AnimatedEmojiSpan animatedEmojiSpan = new AnimatedEmojiSpan(longValue, (Paint.FontMetricsInt) null);
                    imageViewEmoji.span = animatedEmojiSpan;
                    imageViewEmoji.document = animatedEmojiSpan.document;
                    z = SelectAnimatedEmojiDialog.this.selectedDocumentIds.contains(Long.valueOf(longValue));
                    Drawable drawable = (Drawable) SelectAnimatedEmojiDialog.this.emojiGridView.animatedEmojiDrawables.get(imageViewEmoji.span.getDocumentId());
                    if (drawable == null) {
                        drawable = AnimatedEmojiDrawable.make(SelectAnimatedEmojiDialog.this.currentAccount, SelectAnimatedEmojiDialog.this.getCacheType(), imageViewEmoji.span.getDocumentId());
                        SelectAnimatedEmojiDialog.this.emojiGridView.animatedEmojiDrawables.put(imageViewEmoji.span.getDocumentId(), drawable);
                    }
                    imageViewEmoji.setDrawable(drawable);
                }
                imageViewEmoji.setViewSelected(z);
            }
        }

        public int getItemCount() {
            return this.count;
        }

        public void updateRows(boolean z) {
            final ArrayList arrayList = new ArrayList(this.rowHashCodes);
            boolean z2 = false;
            this.count = 0;
            this.rowHashCodes.clear();
            if (SelectAnimatedEmojiDialog.this.searchResult != null) {
                for (int i = 0; i < SelectAnimatedEmojiDialog.this.searchResult.size(); i++) {
                    this.count++;
                    this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{-4342, SelectAnimatedEmojiDialog.this.searchResult.get(i)})));
                }
            }
            if (z) {
                DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    public boolean areContentsTheSame(int i, int i2) {
                        return true;
                    }

                    public int getOldListSize() {
                        return arrayList.size();
                    }

                    public int getNewListSize() {
                        return SearchAdapter.this.rowHashCodes.size();
                    }

                    public boolean areItemsTheSame(int i, int i2) {
                        return ((Integer) arrayList.get(i)).equals(SearchAdapter.this.rowHashCodes.get(i2));
                    }
                }, false).dispatchUpdatesTo((RecyclerView.Adapter) this);
            } else {
                notifyDataSetChanged();
            }
            SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = SelectAnimatedEmojiDialog.this;
            if (selectAnimatedEmojiDialog.searched && this.count == 0) {
                z2 = true;
            }
            selectAnimatedEmojiDialog.switchSearchEmptyView(z2);
        }
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        public int VIEW_TYPE_BUTTON;
        public int VIEW_TYPE_EMOJI;
        public int VIEW_TYPE_EXPAND;
        public int VIEW_TYPE_HEADER;
        public int VIEW_TYPE_HINT;
        public int VIEW_TYPE_IMAGE;
        public int VIEW_TYPE_REACTION;
        public int VIEW_TYPE_SEARCH;

        private Adapter() {
            this.VIEW_TYPE_HEADER = 0;
            this.VIEW_TYPE_REACTION = 1;
            this.VIEW_TYPE_IMAGE = 2;
            this.VIEW_TYPE_EMOJI = 3;
            this.VIEW_TYPE_EXPAND = 4;
            this.VIEW_TYPE_BUTTON = 5;
            this.VIEW_TYPE_HINT = 6;
            this.VIEW_TYPE_SEARCH = 7;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == this.VIEW_TYPE_IMAGE || itemViewType == this.VIEW_TYPE_REACTION || itemViewType == this.VIEW_TYPE_EMOJI;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            HeaderView headerView;
            if (i == this.VIEW_TYPE_HEADER) {
                SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = SelectAnimatedEmojiDialog.this;
                headerView = new HeaderView(selectAnimatedEmojiDialog, selectAnimatedEmojiDialog.getContext());
            } else if (i == this.VIEW_TYPE_IMAGE) {
                headerView = new ImageView(SelectAnimatedEmojiDialog.this.getContext());
            } else if (i == this.VIEW_TYPE_EMOJI || i == this.VIEW_TYPE_REACTION) {
                SelectAnimatedEmojiDialog selectAnimatedEmojiDialog2 = SelectAnimatedEmojiDialog.this;
                headerView = new ImageViewEmoji(selectAnimatedEmojiDialog2.getContext());
            } else if (i == this.VIEW_TYPE_EXPAND) {
                headerView = new EmojiPackExpand(SelectAnimatedEmojiDialog.this.getContext(), (Theme.ResourcesProvider) null);
            } else if (i == this.VIEW_TYPE_BUTTON) {
                SelectAnimatedEmojiDialog selectAnimatedEmojiDialog3 = SelectAnimatedEmojiDialog.this;
                headerView = new EmojiPackButton(selectAnimatedEmojiDialog3, selectAnimatedEmojiDialog3.getContext());
            } else if (i == this.VIEW_TYPE_HINT) {
                AnonymousClass1 r2 = new TextView(this, SelectAnimatedEmojiDialog.this.getContext()) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(AndroidUtilities.dp(26.0f)), NUM));
                    }
                };
                r2.setTextSize(1, 13.0f);
                if (SelectAnimatedEmojiDialog.this.type == 0) {
                    r2.setText(LocaleController.getString("EmojiLongtapHint", R.string.EmojiLongtapHint));
                } else {
                    r2.setText(LocaleController.getString("ReactionsLongtapHint", R.string.ReactionsLongtapHint));
                }
                r2.setGravity(17);
                r2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText", SelectAnimatedEmojiDialog.this.resourcesProvider));
                headerView = r2;
            } else if (i == this.VIEW_TYPE_SEARCH) {
                FixedHeightEmptyCell fixedHeightEmptyCell = new FixedHeightEmptyCell(SelectAnimatedEmojiDialog.this.getContext(), 52);
                fixedHeightEmptyCell.setTag("searchbox");
                headerView = fixedHeightEmptyCell;
            } else {
                SelectAnimatedEmojiDialog selectAnimatedEmojiDialog4 = SelectAnimatedEmojiDialog.this;
                headerView = new ImageViewEmoji(selectAnimatedEmojiDialog4.getContext());
            }
            if (SelectAnimatedEmojiDialog.this.showAnimator != null && SelectAnimatedEmojiDialog.this.showAnimator.isRunning()) {
                headerView.setScaleX(0.0f);
                headerView.setScaleY(0.0f);
            }
            return new RecyclerListView.Holder(headerView);
        }

        public int getItemViewType(int i) {
            if (i == SelectAnimatedEmojiDialog.this.searchRow) {
                return this.VIEW_TYPE_SEARCH;
            }
            if ((i >= SelectAnimatedEmojiDialog.this.recentReactionsStartRow && i < SelectAnimatedEmojiDialog.this.recentReactionsEndRow) || (i >= SelectAnimatedEmojiDialog.this.topReactionsStartRow && i < SelectAnimatedEmojiDialog.this.topReactionsEndRow)) {
                return this.VIEW_TYPE_REACTION;
            }
            if (SelectAnimatedEmojiDialog.this.positionToExpand.indexOfKey(i) >= 0) {
                return this.VIEW_TYPE_EXPAND;
            }
            if (SelectAnimatedEmojiDialog.this.positionToButton.indexOfKey(i) >= 0) {
                return this.VIEW_TYPE_BUTTON;
            }
            if (i == SelectAnimatedEmojiDialog.this.longtapHintRow) {
                return this.VIEW_TYPE_HINT;
            }
            if (SelectAnimatedEmojiDialog.this.positionToSection.indexOfKey(i) >= 0 || i == SelectAnimatedEmojiDialog.this.recentReactionsSectionRow || i == SelectAnimatedEmojiDialog.this.popularSectionRow) {
                return this.VIEW_TYPE_HEADER;
            }
            return this.VIEW_TYPE_EMOJI;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:124:0x0425, code lost:
            if (r0.this$0.selectedDocumentIds.contains(java.lang.Long.valueOf(r2.getDocumentId())) != false) goto L_0x0532;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:150:0x04ac, code lost:
            if (r0.this$0.selectedDocumentIds.contains(java.lang.Long.valueOf(r2.getDocumentId())) != false) goto L_0x0532;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:173:0x0530, code lost:
            if (r0.this$0.selectedDocumentIds.contains(java.lang.Long.valueOf(r2.getDocumentId())) != false) goto L_0x0532;
         */
        /* JADX WARNING: Removed duplicated region for block: B:178:0x0538  */
        /* JADX WARNING: Removed duplicated region for block: B:182:0x0579  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r22, int r23) {
            /*
                r21 = this;
                r0 = r21
                r1 = r22
                r2 = r23
                int r3 = r22.getItemViewType()
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.animation.ValueAnimator r4 = r4.showAnimator
                r5 = 1065353216(0x3var_, float:1.0)
                if (r4 == 0) goto L_0x0020
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.animation.ValueAnimator r4 = r4.showAnimator
                boolean r4 = r4.isRunning()
                if (r4 != 0) goto L_0x002a
            L_0x0020:
                android.view.View r4 = r1.itemView
                r4.setScaleX(r5)
                android.view.View r4 = r1.itemView
                r4.setScaleY(r5)
            L_0x002a:
                int r4 = r0.VIEW_TYPE_HINT
                r6 = 0
                r7 = 1
                if (r3 != r4) goto L_0x005c
                android.view.View r1 = r1.itemView
                android.widget.TextView r1 = (android.widget.TextView) r1
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.lang.Integer r2 = r2.hintExpireDate
                if (r2 == 0) goto L_0x057f
                int r2 = org.telegram.messenger.R.string.EmojiStatusExpireHint
                java.lang.Object[] r3 = new java.lang.Object[r7]
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.lang.Integer r4 = r4.hintExpireDate
                int r4 = r4.intValue()
                long r4 = (long) r4
                java.lang.String r4 = org.telegram.messenger.LocaleController.formatStatusExpireDateTime(r4)
                r3[r6] = r4
                java.lang.String r4 = "EmojiStatusExpireHint"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
                r1.setText(r2)
                goto L_0x057f
            L_0x005c:
                int r4 = r0.VIEW_TYPE_HEADER
                r8 = 8
                r9 = 0
                if (r3 != r4) goto L_0x00de
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$HeaderView r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.HeaderView) r1
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.recentReactionsSectionRow
                if (r2 != r3) goto L_0x008a
                int r2 = org.telegram.messenger.R.string.RecentlyUsed
                java.lang.String r3 = "RecentlyUsed"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2, r6)
                android.widget.ImageView r2 = r1.closeIcon
                r2.setVisibility(r6)
                android.widget.ImageView r1 = r1.closeIcon
                org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$$ExternalSyntheticLambda0
                r2.<init>(r0)
                r1.setOnClickListener(r2)
                return
            L_0x008a:
                android.widget.ImageView r3 = r1.closeIcon
                r3.setVisibility(r8)
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.popularSectionRow
                if (r2 != r3) goto L_0x00a3
                int r2 = org.telegram.messenger.R.string.PopularReactions
                java.lang.String r3 = "PopularReactions"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2, r6)
                return
            L_0x00a3:
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r3 = r3.positionToSection
                int r2 = r3.get(r2)
                if (r2 < 0) goto L_0x00d9
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                java.lang.Object r2 = r3.get(r2)
                org.telegram.ui.Components.EmojiView$EmojiPack r2 = (org.telegram.ui.Components.EmojiView.EmojiPack) r2
                org.telegram.tgnet.TLRPC$StickerSet r3 = r2.set
                java.lang.String r3 = r3.title
                boolean r2 = r2.free
                if (r2 != 0) goto L_0x00d4
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r2 = r2.currentAccount
                org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
                boolean r2 = r2.isPremium()
                if (r2 != 0) goto L_0x00d4
                r6 = 1
            L_0x00d4:
                r1.setText(r3, r6)
                goto L_0x057f
            L_0x00d9:
                r1.setText(r9, r6)
                goto L_0x057f
            L_0x00de:
                int r4 = r0.VIEW_TYPE_REACTION
                if (r3 != r4) goto L_0x0222
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r1
                r1.position = r2
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.recentReactionsStartRow
                if (r2 < r3) goto L_0x010c
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.recentReactionsEndRow
                if (r2 >= r3) goto L_0x010c
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.recentReactionsStartRow
                int r2 = r2 - r3
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.recentReactions
                java.lang.Object r2 = r3.get(r2)
                org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r2 = (org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction) r2
                goto L_0x011f
            L_0x010c:
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.topReactionsStartRow
                int r2 = r2 - r3
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.topReactions
                java.lang.Object r2 = r3.get(r2)
                org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r2 = (org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction) r2
            L_0x011f:
                org.telegram.messenger.ImageReceiver r3 = r1.imageReceiver
                if (r3 != 0) goto L_0x0133
                org.telegram.messenger.ImageReceiver r3 = new org.telegram.messenger.ImageReceiver
                r3.<init>()
                r1.imageReceiver = r3
                r4 = 7
                r3.setLayerNum(r4)
                org.telegram.messenger.ImageReceiver r3 = r1.imageReceiver
                r3.onAttachedToWindow()
            L_0x0133:
                r1.reaction = r2
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r3 = r3.selectedReactions
                boolean r3 = r3.contains(r2)
                r1.setViewSelected(r3)
                java.lang.String r3 = r2.emojicon
                if (r3 == 0) goto L_0x019b
                r1.isDefaultReaction = r7
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.currentAccount
                org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
                java.util.HashMap r3 = r3.getReactionsMap()
                java.lang.String r4 = r2.emojicon
                java.lang.Object r3 = r3.get(r4)
                org.telegram.tgnet.TLRPC$TL_availableReaction r3 = (org.telegram.tgnet.TLRPC$TL_availableReaction) r3
                if (r3 == 0) goto L_0x0181
                org.telegram.tgnet.TLRPC$Document r4 = r3.activate_animation
                r5 = 1045220557(0x3e4ccccd, float:0.2)
                java.lang.String r6 = "windowBackgroundWhiteGrayIcon"
                org.telegram.messenger.SvgHelper$SvgDrawable r15 = org.telegram.messenger.DocumentObject.getSvgThumb((org.telegram.tgnet.TLRPC$Document) r4, (java.lang.String) r6, (float) r5)
                org.telegram.messenger.ImageReceiver r10 = r1.imageReceiver
                org.telegram.tgnet.TLRPC$Document r3 = r3.select_animation
                org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForDocument(r3)
                r13 = 0
                r14 = 0
                r16 = 0
                r20 = 0
                java.lang.String r12 = "60_60_pcache"
                java.lang.String r18 = "tgs"
                r19 = r2
                r10.setImage(r11, r12, r13, r14, r15, r16, r18, r19, r20)
                goto L_0x0186
            L_0x0181:
                org.telegram.messenger.ImageReceiver r2 = r1.imageReceiver
                r2.clearImage()
            L_0x0186:
                r1.span = r9
                r1.document = r9
                r1.setDrawable(r9)
                org.telegram.ui.Components.Premium.PremiumLockIconView r2 = r1.premiumLockIconView
                if (r2 == 0) goto L_0x057f
                r2.setVisibility(r8)
                org.telegram.ui.Components.Premium.PremiumLockIconView r1 = r1.premiumLockIconView
                r1.setImageReceiver(r9)
                goto L_0x057f
            L_0x019b:
                r1.isDefaultReaction = r6
                org.telegram.ui.Components.AnimatedEmojiSpan r3 = new org.telegram.ui.Components.AnimatedEmojiSpan
                long r4 = r2.documentId
                r3.<init>((long) r4, (android.graphics.Paint.FontMetricsInt) r9)
                r1.span = r3
                r1.document = r9
                org.telegram.messenger.ImageReceiver r2 = r1.imageReceiver
                r2.clearImage()
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r2 = r2.emojiGridView
                android.util.LongSparseArray r2 = r2.animatedEmojiDrawables
                org.telegram.ui.Components.AnimatedEmojiSpan r3 = r1.span
                long r3 = r3.getDocumentId()
                java.lang.Object r2 = r2.get(r3)
                android.graphics.drawable.Drawable r2 = (android.graphics.drawable.Drawable) r2
                if (r2 != 0) goto L_0x01ea
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r2 = r2.currentAccount
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.getCacheType()
                org.telegram.ui.Components.AnimatedEmojiSpan r4 = r1.span
                long r4 = r4.getDocumentId()
                org.telegram.ui.Components.AnimatedEmojiDrawable r2 = org.telegram.ui.Components.AnimatedEmojiDrawable.make((int) r2, (int) r3, (long) r4)
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r3 = r3.emojiGridView
                android.util.LongSparseArray r3 = r3.animatedEmojiDrawables
                org.telegram.ui.Components.AnimatedEmojiSpan r4 = r1.span
                long r4 = r4.getDocumentId()
                r3.put(r4, r2)
            L_0x01ea:
                r1.setDrawable(r2)
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r2 = r2.currentAccount
                org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
                boolean r2 = r2.isPremium()
                if (r2 != 0) goto L_0x057f
                org.telegram.ui.Components.Premium.PremiumLockIconView r2 = r1.premiumLockIconView
                if (r2 != 0) goto L_0x021b
                org.telegram.ui.Components.Premium.PremiumLockIconView r2 = new org.telegram.ui.Components.Premium.PremiumLockIconView
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r3 = r3.getContext()
                int r4 = org.telegram.ui.Components.Premium.PremiumLockIconView.TYPE_STICKERS_PREMIUM_LOCKED
                r2.<init>(r3, r4)
                r1.premiumLockIconView = r2
                r3 = 85
                r4 = 12
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r4, (int) r3)
                r1.addView(r2, r3)
            L_0x021b:
                org.telegram.ui.Components.Premium.PremiumLockIconView r1 = r1.premiumLockIconView
                r1.setVisibility(r6)
                goto L_0x057f
            L_0x0222:
                int r4 = r0.VIEW_TYPE_EXPAND
                r8 = -1
                if (r3 != r4) goto L_0x02d7
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackExpand r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.EmojiPackExpand) r1
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r3 = r3.positionToExpand
                int r2 = r3.get(r2)
                if (r2 < 0) goto L_0x0250
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                int r3 = r3.size()
                if (r2 >= r3) goto L_0x0250
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                java.lang.Object r3 = r3.get(r2)
                org.telegram.ui.Components.EmojiView$EmojiPack r3 = (org.telegram.ui.Components.EmojiView.EmojiPack) r3
                goto L_0x0251
            L_0x0250:
                r3 = r9
            L_0x0251:
                java.lang.String r4 = "+"
                if (r2 != r8) goto L_0x028f
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.SelectAnimatedEmojiDialog.EmojiPackExpand unused = r2.recentExpandButton = r1
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                androidx.recyclerview.widget.GridLayoutManager r2 = r2.layoutManager
                int r2 = r2.getSpanCount()
                int r2 = r2 * 5
                android.widget.TextView r1 = r1.textView
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r4)
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r4 = r4.recent
                int r4 = r4.size()
                int r4 = r4 - r2
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r2 = r2.includeEmpty
                int r4 = r4 + r2
                int r4 = r4 + r7
                r3.append(r4)
                java.lang.String r2 = r3.toString()
                r1.setText(r2)
                goto L_0x057f
            L_0x028f:
                if (r3 == 0) goto L_0x02c8
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackExpand r2 = r2.recentExpandButton
                if (r2 != r1) goto L_0x029e
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.SelectAnimatedEmojiDialog.EmojiPackExpand unused = r2.recentExpandButton = r9
            L_0x029e:
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                androidx.recyclerview.widget.GridLayoutManager r2 = r2.layoutManager
                int r2 = r2.getSpanCount()
                int r2 = r2 * 3
                android.widget.TextView r1 = r1.textView
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r4)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r3 = r3.documents
                int r3 = r3.size()
                int r3 = r3 - r2
                int r3 = r3 + r7
                r5.append(r3)
                java.lang.String r2 = r5.toString()
                r1.setText(r2)
                goto L_0x057f
            L_0x02c8:
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackExpand r2 = r2.recentExpandButton
                if (r2 != r1) goto L_0x057f
                org.telegram.ui.SelectAnimatedEmojiDialog r1 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.SelectAnimatedEmojiDialog.EmojiPackExpand unused = r1.recentExpandButton = r9
                goto L_0x057f
            L_0x02d7:
                int r4 = r0.VIEW_TYPE_BUTTON
                if (r3 != r4) goto L_0x032a
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackButton r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.EmojiPackButton) r1
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r3 = r3.positionToButton
                int r2 = r3.get(r2)
                if (r2 < 0) goto L_0x057f
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                int r3 = r3.size()
                if (r2 >= r3) goto L_0x057f
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                java.lang.Object r3 = r3.get(r2)
                org.telegram.ui.Components.EmojiView$EmojiPack r3 = (org.telegram.ui.Components.EmojiView.EmojiPack) r3
                if (r3 == 0) goto L_0x057f
                org.telegram.tgnet.TLRPC$StickerSet r4 = r3.set
                java.lang.String r4 = r4.title
                boolean r5 = r3.free
                if (r5 != 0) goto L_0x031e
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r5 = r5.currentAccount
                org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
                boolean r5 = r5.isPremium()
                if (r5 != 0) goto L_0x031e
                r6 = 1
            L_0x031e:
                boolean r5 = r3.installed
                org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$$ExternalSyntheticLambda1 r7 = new org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$$ExternalSyntheticLambda1
                r7.<init>(r0, r3, r2)
                r1.set(r4, r6, r5, r7)
                goto L_0x057f
            L_0x032a:
                int r4 = r0.VIEW_TYPE_SEARCH
                if (r3 != r4) goto L_0x0330
                goto L_0x057f
            L_0x0330:
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r1
                r1.empty = r6
                r1.position = r2
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r1.setPadding(r3, r4, r10, r5)
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                androidx.recyclerview.widget.GridLayoutManager r3 = r3.layoutManager
                int r3 = r3.getSpanCount()
                int r3 = r3 * 5
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                androidx.recyclerview.widget.GridLayoutManager r4 = r4.layoutManager
                int r4 = r4.getSpanCount()
                int r4 = r4 * 3
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r5 = r5.recent
                int r5 = r5.size()
                if (r5 <= r3) goto L_0x0378
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r5 = r5.recentExpanded
                if (r5 != 0) goto L_0x0378
                goto L_0x0389
            L_0x0378:
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.recent
                int r3 = r3.size()
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r5 = r5.includeEmpty
                int r3 = r3 + r5
            L_0x0389:
                r1.setDrawable(r9)
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r5 = r5.includeEmpty
                if (r5 == 0) goto L_0x03cd
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r5 = r5.searchRow
                if (r5 == r8) goto L_0x039e
                r5 = 1
                goto L_0x039f
            L_0x039e:
                r5 = 0
            L_0x039f:
                org.telegram.ui.SelectAnimatedEmojiDialog r10 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r10 = r10.includeHint
                int r5 = r5 + r10
                if (r2 != r5) goto L_0x03cd
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<java.lang.Long> r2 = r2.selectedDocumentIds
                boolean r2 = r2.contains(r9)
                r1.empty = r7
                r3 = 1084227584(0x40a00000, float:5.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r1.setPadding(r4, r5, r6, r3)
                r1.span = r9
                r1.document = r9
                goto L_0x0534
            L_0x03cd:
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r5 = r5.searchRow
                if (r5 == r8) goto L_0x03d7
                r5 = 1
                goto L_0x03d8
            L_0x03d7:
                r5 = 0
            L_0x03d8:
                int r5 = r2 - r5
                org.telegram.ui.SelectAnimatedEmojiDialog r10 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r10 = r10.includeHint
                int r5 = r5 - r10
                if (r5 >= r3) goto L_0x0429
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.recent
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r4 = r4.searchRow
                if (r4 == r8) goto L_0x03f3
                r4 = 1
                goto L_0x03f4
            L_0x03f3:
                r4 = 0
            L_0x03f4:
                int r2 = r2 - r4
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r4 = r4.includeHint
                int r2 = r2 - r4
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r4 = r4.includeEmpty
                int r2 = r2 - r4
                java.lang.Object r2 = r3.get(r2)
                org.telegram.ui.Components.AnimatedEmojiSpan r2 = (org.telegram.ui.Components.AnimatedEmojiSpan) r2
                r1.span = r2
                if (r2 != 0) goto L_0x040f
                r3 = r9
                goto L_0x0411
            L_0x040f:
                org.telegram.tgnet.TLRPC$Document r3 = r2.document
            L_0x0411:
                r1.document = r3
                if (r2 == 0) goto L_0x0533
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<java.lang.Long> r3 = r3.selectedDocumentIds
                long r4 = r2.getDocumentId()
                java.lang.Long r2 = java.lang.Long.valueOf(r4)
                boolean r2 = r3.contains(r2)
                if (r2 == 0) goto L_0x0533
                goto L_0x0532
            L_0x0429:
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r5 = r5.defaultStatuses
                boolean r5 = r5.isEmpty()
                if (r5 != 0) goto L_0x04b0
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r5 = r5.searchRow
                if (r5 == r8) goto L_0x043f
                r5 = 1
                goto L_0x0440
            L_0x043f:
                r5 = 0
            L_0x0440:
                int r5 = r2 - r5
                org.telegram.ui.SelectAnimatedEmojiDialog r10 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r10 = r10.includeHint
                int r5 = r5 - r10
                int r5 = r5 - r3
                int r5 = r5 - r7
                if (r5 < 0) goto L_0x04b0
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r5 = r5.searchRow
                if (r5 == r8) goto L_0x0457
                r5 = 1
                goto L_0x0458
            L_0x0457:
                r5 = 0
            L_0x0458:
                int r5 = r2 - r5
                org.telegram.ui.SelectAnimatedEmojiDialog r10 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r10 = r10.includeHint
                int r5 = r5 - r10
                int r5 = r5 - r3
                int r5 = r5 - r7
                org.telegram.ui.SelectAnimatedEmojiDialog r10 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r10 = r10.defaultStatuses
                int r10 = r10.size()
                if (r5 >= r10) goto L_0x04b0
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r4 = r4.searchRow
                if (r4 == r8) goto L_0x0479
                r4 = 1
                goto L_0x047a
            L_0x0479:
                r4 = 0
            L_0x047a:
                int r2 = r2 - r4
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r4 = r4.includeHint
                int r2 = r2 - r4
                int r2 = r2 - r3
                int r2 = r2 - r7
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.defaultStatuses
                java.lang.Object r2 = r3.get(r2)
                org.telegram.ui.Components.AnimatedEmojiSpan r2 = (org.telegram.ui.Components.AnimatedEmojiSpan) r2
                r1.span = r2
                if (r2 != 0) goto L_0x0496
                r3 = r9
                goto L_0x0498
            L_0x0496:
                org.telegram.tgnet.TLRPC$Document r3 = r2.document
            L_0x0498:
                r1.document = r3
                if (r2 == 0) goto L_0x0533
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<java.lang.Long> r3 = r3.selectedDocumentIds
                long r4 = r2.getDocumentId()
                java.lang.Long r2 = java.lang.Long.valueOf(r4)
                boolean r2 = r3.contains(r2)
                if (r2 == 0) goto L_0x0533
                goto L_0x0532
            L_0x04b0:
                r3 = 0
            L_0x04b1:
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r5 = r5.positionToSection
                int r5 = r5.size()
                if (r3 >= r5) goto L_0x051c
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r5 = r5.positionToSection
                int r5 = r5.keyAt(r3)
                org.telegram.ui.SelectAnimatedEmojiDialog r8 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r8 = r8.defaultStatuses
                boolean r8 = r8.isEmpty()
                r8 = r8 ^ r7
                int r8 = r3 - r8
                if (r8 < 0) goto L_0x04e3
                org.telegram.ui.SelectAnimatedEmojiDialog r10 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r10 = r10.packs
                java.lang.Object r8 = r10.get(r8)
                org.telegram.ui.Components.EmojiView$EmojiPack r8 = (org.telegram.ui.Components.EmojiView.EmojiPack) r8
                goto L_0x04e4
            L_0x04e3:
                r8 = r9
            L_0x04e4:
                if (r8 != 0) goto L_0x04e7
                goto L_0x0519
            L_0x04e7:
                boolean r10 = r8.expanded
                if (r10 == 0) goto L_0x04f2
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r10 = r8.documents
                int r10 = r10.size()
                goto L_0x04fc
            L_0x04f2:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r10 = r8.documents
                int r10 = r10.size()
                int r10 = java.lang.Math.min(r10, r4)
            L_0x04fc:
                if (r2 <= r5) goto L_0x0519
                int r11 = r5 + 1
                int r11 = r11 + r10
                if (r2 > r11) goto L_0x0519
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r8 = r8.documents
                int r5 = r2 - r5
                int r5 = r5 - r7
                java.lang.Object r5 = r8.get(r5)
                org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
                if (r5 == 0) goto L_0x0519
                org.telegram.ui.Components.AnimatedEmojiSpan r8 = new org.telegram.ui.Components.AnimatedEmojiSpan
                r8.<init>((org.telegram.tgnet.TLRPC$Document) r5, (android.graphics.Paint.FontMetricsInt) r9)
                r1.span = r8
                r1.document = r5
            L_0x0519:
                int r3 = r3 + 1
                goto L_0x04b1
            L_0x051c:
                org.telegram.ui.Components.AnimatedEmojiSpan r2 = r1.span
                if (r2 == 0) goto L_0x0533
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<java.lang.Long> r3 = r3.selectedDocumentIds
                long r4 = r2.getDocumentId()
                java.lang.Long r2 = java.lang.Long.valueOf(r4)
                boolean r2 = r3.contains(r2)
                if (r2 == 0) goto L_0x0533
            L_0x0532:
                r6 = 1
            L_0x0533:
                r2 = r6
            L_0x0534:
                org.telegram.ui.Components.AnimatedEmojiSpan r3 = r1.span
                if (r3 == 0) goto L_0x0579
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r3 = r3.emojiGridView
                android.util.LongSparseArray r3 = r3.animatedEmojiDrawables
                org.telegram.ui.Components.AnimatedEmojiSpan r4 = r1.span
                long r4 = r4.getDocumentId()
                java.lang.Object r3 = r3.get(r4)
                org.telegram.ui.Components.AnimatedEmojiDrawable r3 = (org.telegram.ui.Components.AnimatedEmojiDrawable) r3
                if (r3 != 0) goto L_0x0575
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.currentAccount
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r4 = r4.getCacheType()
                org.telegram.ui.Components.AnimatedEmojiSpan r5 = r1.span
                long r5 = r5.getDocumentId()
                org.telegram.ui.Components.AnimatedEmojiDrawable r3 = org.telegram.ui.Components.AnimatedEmojiDrawable.make((int) r3, (int) r4, (long) r5)
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r4 = r4.emojiGridView
                android.util.LongSparseArray r4 = r4.animatedEmojiDrawables
                org.telegram.ui.Components.AnimatedEmojiSpan r5 = r1.span
                long r5 = r5.getDocumentId()
                r4.put(r5, r3)
            L_0x0575:
                r1.setDrawable(r3)
                goto L_0x057c
            L_0x0579:
                r1.setDrawable(r9)
            L_0x057c:
                r1.setViewSelected(r2)
            L_0x057f:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SelectAnimatedEmojiDialog.Adapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBindViewHolder$0(View view) {
            SelectAnimatedEmojiDialog.this.clearRecent();
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x004c, code lost:
            r1 = r6.this$0.emojiGridView.getChildAt(r0);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$onBindViewHolder$1(org.telegram.ui.Components.EmojiView.EmojiPack r7, int r8, android.view.View r9) {
            /*
                r6 = this;
                boolean r9 = r7.free
                if (r9 != 0) goto L_0x0033
                org.telegram.ui.SelectAnimatedEmojiDialog r9 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r9 = r9.currentAccount
                org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r9)
                boolean r9 = r9.isPremium()
                if (r9 != 0) goto L_0x0033
                org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet r7 = new org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet
                org.telegram.ui.SelectAnimatedEmojiDialog r8 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.ActionBar.BaseFragment r1 = r8.baseFragment
                org.telegram.ui.SelectAnimatedEmojiDialog r8 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r2 = r8.getContext()
                org.telegram.ui.SelectAnimatedEmojiDialog r8 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r8.currentAccount
                r4 = 11
                r5 = 0
                r0 = r7
                r0.<init>(r1, r2, r3, r4, r5)
                r7.show()
                return
            L_0x0033:
                r9 = 0
                r0 = 0
            L_0x0035:
                org.telegram.ui.SelectAnimatedEmojiDialog r1 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r1 = r1.emojiGridView
                int r1 = r1.getChildCount()
                r2 = 0
                if (r0 >= r1) goto L_0x0072
                org.telegram.ui.SelectAnimatedEmojiDialog r1 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r1 = r1.emojiGridView
                android.view.View r1 = r1.getChildAt(r0)
                boolean r1 = r1 instanceof org.telegram.ui.SelectAnimatedEmojiDialog.EmojiPackExpand
                if (r1 == 0) goto L_0x006f
                org.telegram.ui.SelectAnimatedEmojiDialog r1 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r1 = r1.emojiGridView
                android.view.View r1 = r1.getChildAt(r0)
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r3 = r3.emojiGridView
                int r3 = r3.getChildAdapterPosition(r1)
                if (r3 < 0) goto L_0x006f
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r4 = r4.positionToExpand
                int r4 = r4.get(r3)
                if (r4 != r8) goto L_0x006f
                java.lang.Integer r8 = java.lang.Integer.valueOf(r3)
                goto L_0x0074
            L_0x006f:
                int r0 = r0 + 1
                goto L_0x0035
            L_0x0072:
                r8 = r2
                r1 = r8
            L_0x0074:
                if (r8 == 0) goto L_0x007f
                org.telegram.ui.SelectAnimatedEmojiDialog r0 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r8 = r8.intValue()
                r0.expand(r8, r1)
            L_0x007f:
                org.telegram.tgnet.TLRPC$StickerSet r8 = r7.set
                org.telegram.ui.Components.EmojiPacksAlert.installSet(r2, r8, r9)
                org.telegram.ui.SelectAnimatedEmojiDialog r8 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r8 = r8.installedEmojiSets
                org.telegram.tgnet.TLRPC$StickerSet r7 = r7.set
                long r0 = r7.id
                java.lang.Long r7 = java.lang.Long.valueOf(r0)
                r8.add(r7)
                org.telegram.ui.SelectAnimatedEmojiDialog r7 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                r8 = 1
                r7.updateRows(r8)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SelectAnimatedEmojiDialog.Adapter.lambda$onBindViewHolder$1(org.telegram.ui.Components.EmojiView$EmojiPack, int, android.view.View):void");
        }

        public int getItemCount() {
            return SelectAnimatedEmojiDialog.this.totalCount;
        }
    }

    /* access modifiers changed from: private */
    public void clearRecent() {
        onRecentClearedListener onrecentclearedlistener;
        if (this.type == 1 && (onrecentclearedlistener = this.onRecentClearedListener) != null) {
            onrecentclearedlistener.onRecentCleared();
        }
    }

    private class HeaderView extends FrameLayout {
        ImageView closeIcon;
        private LinearLayout layoutView;
        private ValueAnimator lockAnimator;
        private float lockT;
        private RLottieImageView lockView;
        private TextView textView;

        public HeaderView(SelectAnimatedEmojiDialog selectAnimatedEmojiDialog, Context context) {
            super(context);
            LinearLayout linearLayout = new LinearLayout(context);
            this.layoutView = linearLayout;
            linearLayout.setOrientation(0);
            addView(this.layoutView, LayoutHelper.createFrame(-2, -2, 17));
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.lockView = rLottieImageView;
            rLottieImageView.setAnimation(R.raw.unlock_icon, 20, 20);
            this.lockView.setColorFilter(Theme.getColor("chat_emojiPanelStickerSetName", selectAnimatedEmojiDialog.resourcesProvider));
            this.layoutView.addView(this.lockView, LayoutHelper.createLinear(20, 20));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextColor(Theme.getColor("chat_emojiPanelStickerSetName", selectAnimatedEmojiDialog.resourcesProvider));
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setTextSize(1, 14.0f);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.layoutView.addView(this.textView, LayoutHelper.createLinear(-2, -2, 17));
            ImageView imageView = new ImageView(context);
            this.closeIcon = imageView;
            imageView.setImageResource(R.drawable.msg_close);
            this.closeIcon.setScaleType(ImageView.ScaleType.CENTER);
            this.closeIcon.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelStickerSetNameIcon", selectAnimatedEmojiDialog.resourcesProvider), PorterDuff.Mode.MULTIPLY));
            addView(this.closeIcon, LayoutHelper.createFrame(24, 24, 21));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
        }

        public void setText(String str, boolean z) {
            this.textView.setText(str);
            updateLock(z, false);
        }

        public void updateLock(boolean z, boolean z2) {
            ValueAnimator valueAnimator = this.lockAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.lockAnimator = null;
            }
            float f = 0.0f;
            if (z2) {
                float[] fArr = new float[2];
                fArr[0] = this.lockT;
                if (z) {
                    f = 1.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.lockAnimator = ofFloat;
                ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$HeaderView$$ExternalSyntheticLambda0(this));
                this.lockAnimator.setDuration(200);
                this.lockAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                this.lockAnimator.start();
                return;
            }
            if (z) {
                f = 1.0f;
            }
            this.lockT = f;
            this.lockView.setTranslationX(((float) AndroidUtilities.dp(-8.0f)) * (1.0f - this.lockT));
            this.textView.setTranslationX(((float) AndroidUtilities.dp(-8.0f)) * (1.0f - this.lockT));
            this.lockView.setAlpha(this.lockT);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateLock$0(ValueAnimator valueAnimator) {
            this.lockT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.lockView.setTranslationX(((float) AndroidUtilities.dp(-8.0f)) * (1.0f - this.lockT));
            this.textView.setTranslationX(((float) AndroidUtilities.dp(-8.0f)) * (1.0f - this.lockT));
            this.lockView.setAlpha(this.lockT);
        }
    }

    private class EmojiPackButton extends FrameLayout {
        AnimatedTextView addButtonTextView;
        FrameLayout addButtonView;
        private ValueAnimator installFadeAway;
        private String lastTitle;
        private ValueAnimator lockAnimator;
        private Boolean lockShow;
        private float lockT;
        PremiumButtonView premiumButtonView;

        public EmojiPackButton(SelectAnimatedEmojiDialog selectAnimatedEmojiDialog, Context context) {
            super(context);
            AnimatedTextView animatedTextView = new AnimatedTextView(getContext());
            this.addButtonTextView = animatedTextView;
            animatedTextView.setAnimationProperties(0.3f, 0, 250, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.addButtonTextView.setTextSize((float) AndroidUtilities.dp(14.0f));
            this.addButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.addButtonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText", selectAnimatedEmojiDialog.resourcesProvider));
            this.addButtonTextView.setGravity(17);
            FrameLayout frameLayout = new FrameLayout(getContext());
            this.addButtonView = frameLayout;
            frameLayout.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor("featuredStickers_addButton", selectAnimatedEmojiDialog.resourcesProvider), 8.0f));
            this.addButtonView.addView(this.addButtonTextView, LayoutHelper.createFrame(-1, -2, 17));
            addView(this.addButtonView, LayoutHelper.createFrame(-1, -1.0f));
            PremiumButtonView premiumButtonView2 = new PremiumButtonView(getContext(), false);
            this.premiumButtonView = premiumButtonView2;
            premiumButtonView2.setIcon(R.raw.unlock_icon);
            addView(this.premiumButtonView, LayoutHelper.createFrame(-1, -1.0f));
        }

        public void set(String str, boolean z, boolean z2, View.OnClickListener onClickListener) {
            this.lastTitle = str;
            if (z) {
                this.addButtonView.setVisibility(8);
                this.premiumButtonView.setVisibility(0);
                this.premiumButtonView.setButton(LocaleController.formatString("UnlockPremiumEmojiPack", R.string.UnlockPremiumEmojiPack, str), onClickListener);
            } else {
                this.premiumButtonView.setVisibility(8);
                this.addButtonView.setVisibility(0);
                this.addButtonView.setOnClickListener(onClickListener);
            }
            updateInstall(z2, false);
            updateLock(z, false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f));
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f) + getPaddingTop() + getPaddingBottom(), NUM));
        }

        public void updateInstall(boolean z, boolean z2) {
            String str;
            if (z) {
                str = LocaleController.getString("Added", R.string.Added);
            } else {
                str = LocaleController.formatString("AddStickersCount", R.string.AddStickersCount, this.lastTitle);
            }
            this.addButtonTextView.setText(str, z2);
            ValueAnimator valueAnimator = this.installFadeAway;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.installFadeAway = null;
            }
            this.addButtonView.setEnabled(!z);
            float f = 0.6f;
            if (z2) {
                float[] fArr = new float[2];
                fArr[0] = this.addButtonView.getAlpha();
                if (!z) {
                    f = 1.0f;
                }
                fArr[1] = f;
                this.installFadeAway = ValueAnimator.ofFloat(fArr);
                FrameLayout frameLayout = this.addButtonView;
                frameLayout.setAlpha(frameLayout.getAlpha());
                this.installFadeAway.addUpdateListener(new SelectAnimatedEmojiDialog$EmojiPackButton$$ExternalSyntheticLambda1(this));
                this.installFadeAway.setDuration(450);
                this.installFadeAway.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.installFadeAway.start();
                return;
            }
            FrameLayout frameLayout2 = this.addButtonView;
            if (!z) {
                f = 1.0f;
            }
            frameLayout2.setAlpha(f);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateInstall$0(ValueAnimator valueAnimator) {
            this.addButtonView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        private void updateLock(final boolean z, boolean z2) {
            ValueAnimator valueAnimator = this.lockAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.lockAnimator = null;
            }
            Boolean bool = this.lockShow;
            if (bool == null || bool.booleanValue() != z) {
                Boolean valueOf = Boolean.valueOf(z);
                this.lockShow = valueOf;
                float f = 0.0f;
                int i = 0;
                if (z2) {
                    this.premiumButtonView.setVisibility(0);
                    float[] fArr = new float[2];
                    fArr[0] = this.lockT;
                    if (z) {
                        f = 1.0f;
                    }
                    fArr[1] = f;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                    this.lockAnimator = ofFloat;
                    ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$EmojiPackButton$$ExternalSyntheticLambda0(this));
                    this.lockAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (!z) {
                                EmojiPackButton.this.premiumButtonView.setVisibility(8);
                            }
                        }
                    });
                    this.lockAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.lockAnimator.setDuration(350);
                    this.lockAnimator.start();
                    return;
                }
                if (valueOf.booleanValue()) {
                    f = 1.0f;
                }
                this.lockT = f;
                this.addButtonView.setAlpha(1.0f - f);
                this.premiumButtonView.setAlpha(this.lockT);
                this.premiumButtonView.setScaleX(this.lockT);
                this.premiumButtonView.setScaleY(this.lockT);
                PremiumButtonView premiumButtonView2 = this.premiumButtonView;
                if (!this.lockShow.booleanValue()) {
                    i = 8;
                }
                premiumButtonView2.setVisibility(i);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateLock$1(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.lockT = floatValue;
            this.addButtonView.setAlpha(1.0f - floatValue);
            this.premiumButtonView.setAlpha(this.lockT);
        }
    }

    public static class EmojiPackExpand extends FrameLayout {
        public TextView textView;

        public EmojiPackExpand(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextSize(1, 12.0f);
            this.textView.setTextColor(-1);
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0f), ColorUtils.setAlphaComponent(Theme.getColor("chat_emojiPanelStickerSetName", resourcesProvider), 99)));
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(1.66f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f));
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }
    }

    public long animateExpandDuration() {
        return animateExpandAppearDuration() + animateExpandCrossfadeDuration() + 16;
    }

    public long animateExpandAppearDuration() {
        return Math.max(450, ((long) Math.min(55, this.animateExpandToPosition - this.animateExpandFromPosition)) * 30);
    }

    public long animateExpandCrossfadeDuration() {
        return Math.max(300, ((long) Math.min(45, this.animateExpandToPosition - this.animateExpandFromPosition)) * 25);
    }

    public class ImageViewEmoji extends FrameLayout {
        public boolean attached;
        ValueAnimator backAnimator;
        public ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder;
        public float bigReactionSelectedProgress;
        public TLRPC$Document document;
        public Drawable drawable;
        public Rect drawableBounds;
        public boolean empty = false;
        public ImageReceiver imageReceiver;
        public ImageReceiver imageReceiverToDraw;
        final AnimatedEmojiSpan.InvalidateHolder invalidateHolder = new AnimatedEmojiSpan.InvalidateHolder() {
            public void invalidate() {
                EmojiListView emojiListView = SelectAnimatedEmojiDialog.this.emojiGridView;
                if (emojiListView != null) {
                    emojiListView.invalidate();
                }
            }
        };
        public boolean isDefaultReaction;
        public boolean notDraw = false;
        public int position;
        PremiumLockIconView premiumLockIconView;
        /* access modifiers changed from: private */
        public float pressedProgress;
        public ReactionsLayoutInBubble.VisibleReaction reaction;
        public boolean selected;
        public float skewAlpha;
        public int skewIndex;
        public AnimatedEmojiSpan span;

        public ImageViewEmoji(Context context) {
            super(context);
        }

        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM));
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
                        ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$ImageViewEmoji$$ExternalSyntheticLambda0(this));
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
        public /* synthetic */ void lambda$setPressed$0(ValueAnimator valueAnimator) {
            this.pressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            SelectAnimatedEmojiDialog.this.emojiGridView.invalidate();
        }

        public void updatePressedProgress() {
            if (isPressed()) {
                float f = this.pressedProgress;
                if (f != 1.0f) {
                    this.pressedProgress = Utilities.clamp(f + 0.16f, 1.0f, 0.0f);
                    invalidate();
                }
            }
        }

        public void update(long j) {
            ImageReceiver imageReceiver2 = this.imageReceiverToDraw;
            if (imageReceiver2 != null) {
                if (imageReceiver2.getLottieAnimation() != null) {
                    this.imageReceiverToDraw.getLottieAnimation().updateCurrentFrame(j, true);
                }
                if (this.imageReceiverToDraw.getAnimation() != null) {
                    this.imageReceiverToDraw.getAnimation().updateCurrentFrame(j, true);
                }
            }
        }

        public void setViewSelected(boolean z) {
            if (this.selected != z) {
                this.selected = z;
            }
        }

        public void drawSelected(Canvas canvas) {
            Paint paint;
            if (this.selected && !this.notDraw) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                rectF.inset((float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f));
                float dp = (float) AndroidUtilities.dp(4.0f);
                float dp2 = (float) AndroidUtilities.dp(4.0f);
                if (!this.empty) {
                    Drawable drawable2 = this.drawable;
                    if (!(drawable2 instanceof AnimatedEmojiDrawable) || !((AnimatedEmojiDrawable) drawable2).canOverrideColor()) {
                        paint = SelectAnimatedEmojiDialog.this.selectorPaint;
                        canvas.drawRoundRect(rectF, dp, dp2, paint);
                    }
                }
                paint = SelectAnimatedEmojiDialog.this.selectorAccentPaint;
                canvas.drawRoundRect(rectF, dp, dp2, paint);
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attached = true;
            Drawable drawable2 = this.drawable;
            if (drawable2 instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawable2).addView(this.invalidateHolder);
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attached = false;
            Drawable drawable2 = this.drawable;
            if (drawable2 instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawable2).removeView(this.invalidateHolder);
            }
        }

        public void setDrawable(Drawable drawable2) {
            Drawable drawable3 = this.drawable;
            if (drawable3 != drawable2) {
                if (drawable3 != null && (drawable3 instanceof AnimatedEmojiDrawable)) {
                    ((AnimatedEmojiDrawable) drawable3).removeView(this.invalidateHolder);
                }
                this.drawable = drawable2;
                if (this.attached && (drawable2 instanceof AnimatedEmojiDrawable)) {
                    ((AnimatedEmojiDrawable) drawable2).addView(this.invalidateHolder);
                }
            }
        }
    }

    public void onEmojiClick(View view, AnimatedEmojiSpan animatedEmojiSpan) {
        incrementHintUse();
        if (animatedEmojiSpan == null) {
            onEmojiSelected(view, (Long) null, (TLRPC$Document) null, (Integer) null);
            return;
        }
        TLRPC$TL_emojiStatus tLRPC$TL_emojiStatus = new TLRPC$TL_emojiStatus();
        tLRPC$TL_emojiStatus.document_id = animatedEmojiSpan.getDocumentId();
        TLRPC$Document tLRPC$Document = animatedEmojiSpan.document;
        if (tLRPC$Document == null) {
            tLRPC$Document = AnimatedEmojiDrawable.findDocument(this.currentAccount, animatedEmojiSpan.documentId);
        }
        if (view instanceof ImageViewEmoji) {
            if (this.type == 0) {
                MediaDataController.getInstance(this.currentAccount).pushRecentEmojiStatus(tLRPC$TL_emojiStatus);
            }
            int i = this.type;
            if (i == 0 || i == 2) {
                animateEmojiSelect((ImageViewEmoji) view, new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda13(this, view, animatedEmojiSpan, tLRPC$Document));
            } else {
                onEmojiSelected(view, Long.valueOf(animatedEmojiSpan.documentId), tLRPC$Document, (Integer) null);
            }
        } else {
            onEmojiSelected(view, Long.valueOf(animatedEmojiSpan.documentId), tLRPC$Document, (Integer) null);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onEmojiClick$14(View view, AnimatedEmojiSpan animatedEmojiSpan, TLRPC$Document tLRPC$Document) {
        onEmojiSelected(view, Long.valueOf(animatedEmojiSpan.documentId), tLRPC$Document, (Integer) null);
    }

    /* access modifiers changed from: private */
    public void incrementHintUse() {
        if (this.type != 2) {
            StringBuilder sb = new StringBuilder();
            sb.append("emoji");
            sb.append(this.type == 0 ? "status" : "reaction");
            sb.append("usehint");
            String sb2 = sb.toString();
            int i = MessagesController.getGlobalMainSettings().getInt(sb2, 0);
            if (i <= 3) {
                MessagesController.getGlobalMainSettings().edit().putInt(sb2, i + 1).apply();
            }
        }
    }

    public static void preload(int i) {
        if (MediaDataController.getInstance(i) != null) {
            MediaDataController.getInstance(i).checkStickers(5);
            MediaDataController.getInstance(i).fetchEmojiStatuses(0, true);
            MediaDataController.getInstance(i).checkReactions();
            MediaDataController.getInstance(i).getStickerSet(new TLRPC$TL_inputStickerSetEmojiDefaultStatuses(), false);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0413  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x04e7  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x06c3  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x06d3  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x06de  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x04d7 A[EDGE_INSN: B:223:0x04d7->B:157:0x04d7 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:237:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateRows(boolean r23) {
        /*
            r22 = this;
            r0 = r22
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            if (r1 != 0) goto L_0x000b
            return
        L_0x000b:
            java.util.ArrayList r2 = new java.util.ArrayList
            r3 = 5
            java.util.ArrayList r4 = r1.getStickerSets(r3)
            r2.<init>(r4)
            java.util.ArrayList r4 = new java.util.ArrayList
            java.util.ArrayList r1 = r1.getFeaturedEmojiSets()
            r4.<init>(r1)
            java.util.ArrayList r1 = new java.util.ArrayList
            java.util.ArrayList<java.lang.Integer> r5 = r0.rowHashCodes
            r1.<init>(r5)
            r5 = 0
            r0.totalCount = r5
            r6 = -1
            r0.recentReactionsSectionRow = r6
            r0.recentReactionsStartRow = r6
            r0.recentReactionsEndRow = r6
            r0.popularSectionRow = r6
            r0.longtapHintRow = r6
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r7 = r0.recent
            r7.clear()
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r7 = r0.defaultStatuses
            r7.clear()
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r7 = r0.topReactions
            r7.clear()
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r7 = r0.recentReactions
            r7.clear()
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$EmojiPack> r7 = r0.packs
            r7.clear()
            android.util.SparseIntArray r7 = r0.positionToSection
            r7.clear()
            android.util.SparseIntArray r7 = r0.sectionToPosition
            r7.clear()
            android.util.SparseIntArray r7 = r0.positionToExpand
            r7.clear()
            java.util.ArrayList<java.lang.Integer> r7 = r0.rowHashCodes
            r7.clear()
            android.util.SparseIntArray r7 = r0.positionToButton
            r7.clear()
            boolean r7 = r2.isEmpty()
            if (r7 != 0) goto L_0x0074
            int r7 = r0.totalCount
            int r8 = r7 + 1
            r0.totalCount = r8
            r0.searchRow = r7
            goto L_0x0076
        L_0x0074:
            r0.searchRow = r6
        L_0x0076:
            boolean r7 = r0.includeHint
            r8 = 2
            if (r7 == 0) goto L_0x0091
            int r7 = r0.type
            if (r7 == r8) goto L_0x0091
            int r7 = r0.totalCount
            int r9 = r7 + 1
            r0.totalCount = r9
            r0.longtapHintRow = r7
            java.util.ArrayList<java.lang.Integer> r7 = r0.rowHashCodes
            r9 = 6
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r7.add(r9)
        L_0x0091:
            java.util.List<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r7 = r0.recentReactionsToSet
            r11 = 1
            if (r7 == 0) goto L_0x01a4
            int r6 = r0.totalCount
            r0.topReactionsStartRow = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            java.util.List<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r7 = r0.recentReactionsToSet
            r6.addAll(r7)
            r7 = 0
        L_0x00a5:
            r12 = 16
            if (r7 >= r12) goto L_0x00bd
            boolean r12 = r6.isEmpty()
            if (r12 != 0) goto L_0x00ba
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r12 = r0.topReactions
            java.lang.Object r13 = r6.remove(r5)
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r13 = (org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction) r13
            r12.add(r13)
        L_0x00ba:
            int r7 = r7 + 1
            goto L_0x00a5
        L_0x00bd:
            r7 = 0
        L_0x00be:
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r12 = r0.topReactions
            int r12 = r12.size()
            if (r7 >= r12) goto L_0x00f2
            java.util.ArrayList<java.lang.Integer> r12 = r0.rowHashCodes
            java.lang.Object[] r13 = new java.lang.Object[r8]
            r14 = -5632(0xffffffffffffea00, float:NaN)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r13[r5] = r14
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r14 = r0.topReactions
            java.lang.Object r14 = r14.get(r7)
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r14 = (org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction) r14
            int r14 = r14.hashCode()
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r13[r11] = r14
            int r13 = java.util.Arrays.hashCode(r13)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r12.add(r13)
            int r7 = r7 + 1
            goto L_0x00be
        L_0x00f2:
            int r7 = r0.totalCount
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r12 = r0.topReactions
            int r12 = r12.size()
            int r7 = r7 + r12
            r0.totalCount = r7
            r0.topReactionsEndRow = r7
            boolean r7 = r6.isEmpty()
            if (r7 != 0) goto L_0x0407
            r7 = 0
        L_0x0106:
            int r12 = r6.size()
            if (r7 >= r12) goto L_0x011f
            java.lang.Object r12 = r6.get(r7)
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r12 = (org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction) r12
            long r12 = r12.documentId
            r14 = 0
            int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r16 == 0) goto L_0x011c
            r7 = 0
            goto L_0x0120
        L_0x011c:
            int r7 = r7 + 1
            goto L_0x0106
        L_0x011f:
            r7 = 1
        L_0x0120:
            if (r7 == 0) goto L_0x0140
            int r12 = r0.currentAccount
            org.telegram.messenger.UserConfig r12 = org.telegram.messenger.UserConfig.getInstance(r12)
            boolean r12 = r12.isPremium()
            if (r12 == 0) goto L_0x0152
            int r12 = r0.totalCount
            int r13 = r12 + 1
            r0.totalCount = r13
            r0.popularSectionRow = r12
            java.util.ArrayList<java.lang.Integer> r12 = r0.rowHashCodes
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r12.add(r3)
            goto L_0x0152
        L_0x0140:
            int r3 = r0.totalCount
            int r12 = r3 + 1
            r0.totalCount = r12
            r0.recentReactionsSectionRow = r3
            java.util.ArrayList<java.lang.Integer> r3 = r0.rowHashCodes
            r12 = 4
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r3.add(r12)
        L_0x0152:
            int r3 = r0.totalCount
            r0.recentReactionsStartRow = r3
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r3 = r0.recentReactions
            r3.addAll(r6)
            r3 = 0
        L_0x015c:
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r6 = r0.recentReactions
            int r6 = r6.size()
            if (r3 >= r6) goto L_0x0195
            java.util.ArrayList<java.lang.Integer> r6 = r0.rowHashCodes
            java.lang.Object[] r12 = new java.lang.Object[r8]
            if (r7 == 0) goto L_0x016d
            r13 = 4235(0x108b, float:5.934E-42)
            goto L_0x016f
        L_0x016d:
            r13 = -3142(0xfffffffffffff3ba, float:NaN)
        L_0x016f:
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r12[r5] = r13
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r13 = r0.recentReactions
            java.lang.Object r13 = r13.get(r3)
            org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r13 = (org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction) r13
            int r13 = r13.hashCode()
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r12[r11] = r13
            int r12 = java.util.Arrays.hashCode(r12)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r6.add(r12)
            int r3 = r3 + 1
            goto L_0x015c
        L_0x0195:
            int r3 = r0.totalCount
            java.util.ArrayList<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r6 = r0.recentReactions
            int r6 = r6.size()
            int r3 = r3 + r6
            r0.totalCount = r3
            r0.recentReactionsEndRow = r3
            goto L_0x0407
        L_0x01a4:
            int r7 = r0.type
            if (r7 != 0) goto L_0x0407
            int r7 = r0.currentAccount
            org.telegram.messenger.MediaDataController r7 = org.telegram.messenger.MediaDataController.getInstance(r7)
            java.util.ArrayList r7 = r7.getRecentEmojiStatuses()
            int r12 = r0.currentAccount
            org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r12)
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmojiDefaultStatuses r13 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmojiDefaultStatuses
            r13.<init>()
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r12 = r12.getStickerSet(r13, r5)
            if (r12 != 0) goto L_0x01c7
            r0.defaultSetLoading = r11
            goto L_0x0407
        L_0x01c7:
            boolean r13 = r0.includeEmpty
            if (r13 == 0) goto L_0x01d9
            int r13 = r0.totalCount
            int r13 = r13 + r11
            r0.totalCount = r13
            java.util.ArrayList<java.lang.Integer> r13 = r0.rowHashCodes
            java.lang.Integer r14 = java.lang.Integer.valueOf(r8)
            r13.add(r14)
        L_0x01d9:
            int r13 = r0.currentAccount
            org.telegram.messenger.MediaDataController r13 = org.telegram.messenger.MediaDataController.getInstance(r13)
            java.util.ArrayList r13 = r13.getDefaultEmojiStatuses()
            androidx.recyclerview.widget.GridLayoutManager r14 = r0.layoutManager
            int r14 = r14.getSpanCount()
            int r14 = r14 * 13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r15 = r12.documents
            r6 = 0
            if (r15 == 0) goto L_0x022c
            boolean r15 = r15.isEmpty()
            if (r15 != 0) goto L_0x022c
            r15 = 0
        L_0x01f7:
            androidx.recyclerview.widget.GridLayoutManager r9 = r0.layoutManager
            int r9 = r9.getSpanCount()
            int r9 = r9 - r11
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r10 = r12.documents
            int r10 = r10.size()
            int r9 = java.lang.Math.min(r9, r10)
            if (r15 >= r9) goto L_0x022c
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r9 = r0.recent
            org.telegram.ui.Components.AnimatedEmojiSpan r10 = new org.telegram.ui.Components.AnimatedEmojiSpan
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r5 = r12.documents
            java.lang.Object r5 = r5.get(r15)
            org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
            r10.<init>((org.telegram.tgnet.TLRPC$Document) r5, (android.graphics.Paint.FontMetricsInt) r6)
            r9.add(r10)
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r5 = r0.recent
            int r5 = r5.size()
            boolean r9 = r0.includeEmpty
            int r5 = r5 + r9
            if (r5 < r14) goto L_0x0228
            goto L_0x022c
        L_0x0228:
            int r15 = r15 + 1
            r5 = 0
            goto L_0x01f7
        L_0x022c:
            r9 = 1000(0x3e8, double:4.94E-321)
            if (r7 == 0) goto L_0x02a4
            boolean r5 = r7.isEmpty()
            if (r5 != 0) goto L_0x02a4
            java.util.Iterator r5 = r7.iterator()
        L_0x023a:
            boolean r7 = r5.hasNext()
            if (r7 == 0) goto L_0x02a4
            java.lang.Object r7 = r5.next()
            org.telegram.tgnet.TLRPC$EmojiStatus r7 = (org.telegram.tgnet.TLRPC$EmojiStatus) r7
            boolean r12 = r7 instanceof org.telegram.tgnet.TLRPC$TL_emojiStatus
            if (r12 == 0) goto L_0x0254
            org.telegram.tgnet.TLRPC$TL_emojiStatus r7 = (org.telegram.tgnet.TLRPC$TL_emojiStatus) r7
            long r11 = r7.document_id
            r20 = r11
            r12 = r4
            r3 = r20
            goto L_0x0268
        L_0x0254:
            boolean r11 = r7 instanceof org.telegram.tgnet.TLRPC$TL_emojiStatusUntil
            if (r11 == 0) goto L_0x02a2
            org.telegram.tgnet.TLRPC$TL_emojiStatusUntil r7 = (org.telegram.tgnet.TLRPC$TL_emojiStatusUntil) r7
            int r11 = r7.until
            long r18 = java.lang.System.currentTimeMillis()
            r12 = r4
            long r3 = r18 / r9
            int r4 = (int) r3
            if (r11 <= r4) goto L_0x02a0
            long r3 = r7.document_id
        L_0x0268:
            r7 = 0
        L_0x0269:
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r11 = r0.recent
            int r11 = r11.size()
            if (r7 >= r11) goto L_0x0286
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r11 = r0.recent
            java.lang.Object r11 = r11.get(r7)
            org.telegram.ui.Components.AnimatedEmojiSpan r11 = (org.telegram.ui.Components.AnimatedEmojiSpan) r11
            long r18 = r11.getDocumentId()
            int r11 = (r18 > r3 ? 1 : (r18 == r3 ? 0 : -1))
            if (r11 != 0) goto L_0x0283
            r7 = 1
            goto L_0x0287
        L_0x0283:
            int r7 = r7 + 1
            goto L_0x0269
        L_0x0286:
            r7 = 0
        L_0x0287:
            if (r7 == 0) goto L_0x028a
            goto L_0x02a0
        L_0x028a:
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r7 = r0.recent
            org.telegram.ui.Components.AnimatedEmojiSpan r11 = new org.telegram.ui.Components.AnimatedEmojiSpan
            r11.<init>((long) r3, (android.graphics.Paint.FontMetricsInt) r6)
            r7.add(r11)
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r3 = r0.recent
            int r3 = r3.size()
            boolean r4 = r0.includeEmpty
            int r3 = r3 + r4
            if (r3 < r14) goto L_0x02a0
            goto L_0x02a5
        L_0x02a0:
            r4 = r12
            r3 = 5
        L_0x02a2:
            r11 = 1
            goto L_0x023a
        L_0x02a4:
            r12 = r4
        L_0x02a5:
            if (r13 == 0) goto L_0x0314
            boolean r3 = r13.isEmpty()
            if (r3 != 0) goto L_0x0314
            java.util.Iterator r3 = r13.iterator()
        L_0x02b1:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x0314
            java.lang.Object r4 = r3.next()
            org.telegram.tgnet.TLRPC$EmojiStatus r4 = (org.telegram.tgnet.TLRPC$EmojiStatus) r4
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_emojiStatus
            if (r5 == 0) goto L_0x02c6
            org.telegram.tgnet.TLRPC$TL_emojiStatus r4 = (org.telegram.tgnet.TLRPC$TL_emojiStatus) r4
            long r4 = r4.document_id
            goto L_0x02d9
        L_0x02c6:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_emojiStatusUntil
            if (r5 == 0) goto L_0x02b1
            org.telegram.tgnet.TLRPC$TL_emojiStatusUntil r4 = (org.telegram.tgnet.TLRPC$TL_emojiStatusUntil) r4
            int r5 = r4.until
            long r18 = java.lang.System.currentTimeMillis()
            long r6 = r18 / r9
            int r7 = (int) r6
            if (r5 <= r7) goto L_0x0311
            long r4 = r4.document_id
        L_0x02d9:
            r6 = 0
        L_0x02da:
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r7 = r0.recent
            int r7 = r7.size()
            if (r6 >= r7) goto L_0x02f7
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r7 = r0.recent
            java.lang.Object r7 = r7.get(r6)
            org.telegram.ui.Components.AnimatedEmojiSpan r7 = (org.telegram.ui.Components.AnimatedEmojiSpan) r7
            long r18 = r7.getDocumentId()
            int r7 = (r18 > r4 ? 1 : (r18 == r4 ? 0 : -1))
            if (r7 != 0) goto L_0x02f4
            r6 = 1
            goto L_0x02f8
        L_0x02f4:
            int r6 = r6 + 1
            goto L_0x02da
        L_0x02f7:
            r6 = 0
        L_0x02f8:
            if (r6 != 0) goto L_0x0311
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r6 = r0.recent
            org.telegram.ui.Components.AnimatedEmojiSpan r7 = new org.telegram.ui.Components.AnimatedEmojiSpan
            r11 = 0
            r7.<init>((long) r4, (android.graphics.Paint.FontMetricsInt) r11)
            r6.add(r7)
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r4 = r0.recent
            int r4 = r4.size()
            boolean r5 = r0.includeEmpty
            int r4 = r4 + r5
            if (r4 < r14) goto L_0x0312
            goto L_0x0314
        L_0x0311:
            r11 = 0
        L_0x0312:
            r6 = r11
            goto L_0x02b1
        L_0x0314:
            androidx.recyclerview.widget.GridLayoutManager r3 = r0.layoutManager
            int r3 = r3.getSpanCount()
            r4 = 5
            int r3 = r3 * 5
            boolean r4 = r0.includeEmpty
            int r4 = r3 - r4
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r5 = r0.recent
            int r5 = r5.size()
            r6 = 43223(0xa8d7, float:6.0568E-41)
            if (r5 <= r4) goto L_0x03cd
            boolean r5 = r0.recentExpanded
            if (r5 != 0) goto L_0x03cd
            r5 = 0
            r7 = 1
        L_0x0332:
            int r9 = r4 + -1
            if (r5 >= r9) goto L_0x0366
            java.util.ArrayList<java.lang.Integer> r9 = r0.rowHashCodes
            java.lang.Object[] r10 = new java.lang.Object[r8]
            java.lang.Integer r11 = java.lang.Integer.valueOf(r6)
            r13 = 0
            r10[r13] = r11
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r11 = r0.recent
            java.lang.Object r11 = r11.get(r5)
            org.telegram.ui.Components.AnimatedEmojiSpan r11 = (org.telegram.ui.Components.AnimatedEmojiSpan) r11
            long r13 = r11.getDocumentId()
            java.lang.Long r11 = java.lang.Long.valueOf(r13)
            r10[r7] = r11
            int r10 = java.util.Arrays.hashCode(r10)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r9.add(r10)
            int r9 = r0.totalCount
            int r9 = r9 + r7
            r0.totalCount = r9
            int r5 = r5 + 1
            goto L_0x0332
        L_0x0366:
            java.util.ArrayList<java.lang.Integer> r4 = r0.rowHashCodes
            r5 = 3
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r5 = -5531(0xffffffffffffea65, float:NaN)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r5)
            r5 = 0
            r6[r5] = r9
            r5 = -1
            java.lang.Integer r9 = java.lang.Integer.valueOf(r5)
            r6[r7] = r9
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r5 = r0.recent
            int r5 = r5.size()
            int r5 = r5 - r3
            boolean r9 = r0.includeEmpty
            int r5 = r5 + r9
            int r5 = r5 + r7
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6[r8] = r5
            int r5 = java.util.Arrays.hashCode(r6)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4.add(r5)
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackExpand r4 = r0.recentExpandButton
            if (r4 == 0) goto L_0x03be
            android.widget.TextView r4 = r4.textView
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "+"
            r5.append(r6)
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r6 = r0.recent
            int r6 = r6.size()
            int r6 = r6 - r3
            boolean r3 = r0.includeEmpty
            int r6 = r6 + r3
            r3 = 1
            int r6 = r6 + r3
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r4.setText(r5)
            goto L_0x03bf
        L_0x03be:
            r3 = 1
        L_0x03bf:
            android.util.SparseIntArray r4 = r0.positionToExpand
            int r5 = r0.totalCount
            r6 = -1
            r4.put(r5, r6)
            int r4 = r0.totalCount
            int r4 = r4 + r3
            r0.totalCount = r4
            goto L_0x0408
        L_0x03cd:
            r3 = 0
        L_0x03ce:
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r4 = r0.recent
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0408
            java.util.ArrayList<java.lang.Integer> r4 = r0.rowHashCodes
            java.lang.Object[] r5 = new java.lang.Object[r8]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)
            r9 = 0
            r5[r9] = r7
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan> r7 = r0.recent
            java.lang.Object r7 = r7.get(r3)
            org.telegram.ui.Components.AnimatedEmojiSpan r7 = (org.telegram.ui.Components.AnimatedEmojiSpan) r7
            long r9 = r7.getDocumentId()
            java.lang.Long r7 = java.lang.Long.valueOf(r9)
            r9 = 1
            r5[r9] = r7
            int r5 = java.util.Arrays.hashCode(r5)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4.add(r5)
            int r4 = r0.totalCount
            int r4 = r4 + r9
            r0.totalCount = r4
            int r3 = r3 + 1
            goto L_0x03ce
        L_0x0407:
            r12 = r4
        L_0x0408:
            r3 = 0
        L_0x0409:
            int r4 = r2.size()
            r5 = 9211(0x23fb, float:1.2907E-41)
            r6 = 3212(0xc8c, float:4.501E-42)
            if (r3 >= r4) goto L_0x04d7
            java.lang.Object r4 = r2.get(r3)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r4
            if (r4 == 0) goto L_0x04d3
            org.telegram.tgnet.TLRPC$StickerSet r7 = r4.set
            if (r7 == 0) goto L_0x04d3
            boolean r9 = r7.emojis
            if (r9 == 0) goto L_0x04d3
            java.util.ArrayList<java.lang.Long> r9 = r0.installedEmojiSets
            long r10 = r7.id
            java.lang.Long r7 = java.lang.Long.valueOf(r10)
            boolean r7 = r9.contains(r7)
            if (r7 != 0) goto L_0x04d3
            android.util.SparseIntArray r7 = r0.positionToSection
            int r9 = r0.totalCount
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$EmojiPack> r10 = r0.packs
            int r10 = r10.size()
            r7.put(r9, r10)
            android.util.SparseIntArray r7 = r0.sectionToPosition
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$EmojiPack> r9 = r0.packs
            int r9 = r9.size()
            int r10 = r0.totalCount
            r7.put(r9, r10)
            int r7 = r0.totalCount
            r9 = 1
            int r7 = r7 + r9
            r0.totalCount = r7
            java.util.ArrayList<java.lang.Integer> r7 = r0.rowHashCodes
            java.lang.Object[] r10 = new java.lang.Object[r8]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r11 = 0
            r10[r11] = r5
            org.telegram.tgnet.TLRPC$StickerSet r5 = r4.set
            long r13 = r5.id
            java.lang.Long r5 = java.lang.Long.valueOf(r13)
            r10[r9] = r5
            int r5 = java.util.Arrays.hashCode(r10)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r7.add(r5)
            org.telegram.ui.Components.EmojiView$EmojiPack r5 = new org.telegram.ui.Components.EmojiView$EmojiPack
            r5.<init>()
            r5.installed = r9
            r5.featured = r11
            r5.expanded = r9
            boolean r7 = org.telegram.messenger.MessageObject.isPremiumEmojiPack((org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r4)
            r7 = r7 ^ r9
            r5.free = r7
            org.telegram.tgnet.TLRPC$StickerSet r7 = r4.set
            r5.set = r7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r4.documents
            r5.documents = r4
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$EmojiPack> r4 = r0.packs
            r4.size()
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$EmojiPack> r4 = r0.packs
            r4.add(r5)
            int r4 = r0.totalCount
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r7 = r5.documents
            int r7 = r7.size()
            int r4 = r4 + r7
            r0.totalCount = r4
            r4 = 0
        L_0x04a1:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r7 = r5.documents
            int r7 = r7.size()
            if (r4 >= r7) goto L_0x04d3
            java.util.ArrayList<java.lang.Integer> r7 = r0.rowHashCodes
            java.lang.Object[] r9 = new java.lang.Object[r8]
            java.lang.Integer r10 = java.lang.Integer.valueOf(r6)
            r11 = 0
            r9[r11] = r10
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r10 = r5.documents
            java.lang.Object r10 = r10.get(r4)
            org.telegram.tgnet.TLRPC$Document r10 = (org.telegram.tgnet.TLRPC$Document) r10
            long r10 = r10.id
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            r11 = 1
            r9[r11] = r10
            int r9 = java.util.Arrays.hashCode(r9)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r7.add(r9)
            int r4 = r4 + 1
            goto L_0x04a1
        L_0x04d3:
            int r3 = r3 + 1
            goto L_0x0409
        L_0x04d7:
            androidx.recyclerview.widget.GridLayoutManager r2 = r0.layoutManager
            int r2 = r2.getSpanCount()
            r3 = 3
            int r2 = r2 * 3
            r13 = 0
        L_0x04e1:
            int r3 = r12.size()
            if (r13 >= r3) goto L_0x06b9
            java.lang.Object r3 = r12.get(r13)
            org.telegram.tgnet.TLRPC$StickerSetCovered r3 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r3
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered
            if (r4 == 0) goto L_0x06aa
            org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered r3 = (org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered) r3
            r4 = 0
        L_0x04f4:
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$EmojiPack> r7 = r0.packs
            int r7 = r7.size()
            if (r4 >= r7) goto L_0x0517
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$EmojiPack> r7 = r0.packs
            java.lang.Object r7 = r7.get(r4)
            org.telegram.ui.Components.EmojiView$EmojiPack r7 = (org.telegram.ui.Components.EmojiView.EmojiPack) r7
            org.telegram.tgnet.TLRPC$StickerSet r7 = r7.set
            long r9 = r7.id
            org.telegram.tgnet.TLRPC$StickerSet r7 = r3.set
            long r6 = r7.id
            int r14 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r14 != 0) goto L_0x0512
            r4 = 1
            goto L_0x0518
        L_0x0512:
            int r4 = r4 + 1
            r6 = 3212(0xc8c, float:4.501E-42)
            goto L_0x04f4
        L_0x0517:
            r4 = 0
        L_0x0518:
            if (r4 == 0) goto L_0x051c
            goto L_0x06aa
        L_0x051c:
            android.util.SparseIntArray r4 = r0.positionToSection
            int r6 = r0.totalCount
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$EmojiPack> r7 = r0.packs
            int r7 = r7.size()
            r4.put(r6, r7)
            android.util.SparseIntArray r4 = r0.sectionToPosition
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$EmojiPack> r6 = r0.packs
            int r6 = r6.size()
            int r7 = r0.totalCount
            r4.put(r6, r7)
            int r4 = r0.totalCount
            r6 = 1
            int r4 = r4 + r6
            r0.totalCount = r4
            java.util.ArrayList<java.lang.Integer> r4 = r0.rowHashCodes
            java.lang.Object[] r7 = new java.lang.Object[r8]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r5)
            r10 = 0
            r7[r10] = r9
            org.telegram.tgnet.TLRPC$StickerSet r9 = r3.set
            long r9 = r9.id
            java.lang.Long r9 = java.lang.Long.valueOf(r9)
            r7[r6] = r9
            int r6 = java.util.Arrays.hashCode(r7)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r4.add(r6)
            org.telegram.ui.Components.EmojiView$EmojiPack r4 = new org.telegram.ui.Components.EmojiView$EmojiPack
            r4.<init>()
            java.util.ArrayList<java.lang.Long> r6 = r0.installedEmojiSets
            org.telegram.tgnet.TLRPC$StickerSet r7 = r3.set
            long r9 = r7.id
            java.lang.Long r7 = java.lang.Long.valueOf(r9)
            boolean r6 = r6.contains(r7)
            r4.installed = r6
            r6 = 1
            r4.featured = r6
            boolean r7 = org.telegram.messenger.MessageObject.isPremiumEmojiPack((org.telegram.tgnet.TLRPC$StickerSetCovered) r3)
            r7 = r7 ^ r6
            r4.free = r7
            org.telegram.tgnet.TLRPC$StickerSet r6 = r3.set
            r4.set = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r3.documents
            r4.documents = r6
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$EmojiPack> r6 = r0.packs
            r6.size()
            java.util.ArrayList<java.lang.Long> r6 = r0.expandedEmojiSets
            org.telegram.tgnet.TLRPC$StickerSet r7 = r4.set
            long r9 = r7.id
            java.lang.Long r7 = java.lang.Long.valueOf(r9)
            boolean r6 = r6.contains(r7)
            r4.expanded = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r4.documents
            int r6 = r6.size()
            if (r6 <= r2) goto L_0x061e
            boolean r6 = r4.expanded
            if (r6 != 0) goto L_0x061e
            int r6 = r0.totalCount
            int r6 = r6 + r2
            r0.totalCount = r6
            r6 = 0
        L_0x05aa:
            int r7 = r2 + -1
            if (r6 >= r7) goto L_0x05dc
            java.util.ArrayList<java.lang.Integer> r7 = r0.rowHashCodes
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r10 = 3212(0xc8c, float:4.501E-42)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r10)
            r10 = 0
            r9[r10] = r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r10 = r4.documents
            java.lang.Object r10 = r10.get(r6)
            org.telegram.tgnet.TLRPC$Document r10 = (org.telegram.tgnet.TLRPC$Document) r10
            r14 = r12
            long r11 = r10.id
            java.lang.Long r10 = java.lang.Long.valueOf(r11)
            r11 = 1
            r9[r11] = r10
            int r9 = java.util.Arrays.hashCode(r9)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r7.add(r9)
            int r6 = r6 + 1
            r12 = r14
            goto L_0x05aa
        L_0x05dc:
            r14 = r12
            java.util.ArrayList<java.lang.Integer> r6 = r0.rowHashCodes
            r7 = 3
            java.lang.Object[] r9 = new java.lang.Object[r7]
            r10 = -5531(0xffffffffffffea65, float:NaN)
            java.lang.Integer r11 = java.lang.Integer.valueOf(r10)
            r12 = 0
            r9[r12] = r11
            org.telegram.tgnet.TLRPC$StickerSet r11 = r3.set
            long r11 = r11.id
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            r12 = 1
            r9[r12] = r11
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r11 = r4.documents
            int r11 = r11.size()
            int r11 = r11 - r2
            int r11 = r11 + r12
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r9[r8] = r11
            int r9 = java.util.Arrays.hashCode(r9)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r6.add(r9)
            android.util.SparseIntArray r6 = r0.positionToExpand
            int r9 = r0.totalCount
            int r9 = r9 - r12
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$EmojiPack> r11 = r0.packs
            int r11 = r11.size()
            r6.put(r9, r11)
            goto L_0x0669
        L_0x061e:
            r14 = r12
            r7 = 3
            r10 = -5531(0xffffffffffffea65, float:NaN)
            int r6 = r0.totalCount
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r4.documents
            int r9 = r9.size()
            int r6 = r6 + r9
            r0.totalCount = r6
            r6 = 0
        L_0x062e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r4.documents
            int r9 = r9.size()
            if (r6 >= r9) goto L_0x0669
            java.util.ArrayList<java.lang.Integer> r9 = r0.rowHashCodes
            java.lang.Object[] r11 = new java.lang.Object[r8]
            r12 = 3212(0xc8c, float:4.501E-42)
            java.lang.Integer r16 = java.lang.Integer.valueOf(r12)
            r17 = 0
            r11[r17] = r16
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r5 = r4.documents
            java.lang.Object r5 = r5.get(r6)
            org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
            r17 = r13
            long r12 = r5.id
            java.lang.Long r5 = java.lang.Long.valueOf(r12)
            r12 = 1
            r11[r12] = r5
            int r5 = java.util.Arrays.hashCode(r11)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r9.add(r5)
            int r6 = r6 + 1
            r13 = r17
            r5 = 9211(0x23fb, float:1.2907E-41)
            goto L_0x062e
        L_0x0669:
            r17 = r13
            boolean r5 = r4.installed
            if (r5 != 0) goto L_0x06a4
            android.util.SparseIntArray r5 = r0.positionToButton
            int r6 = r0.totalCount
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$EmojiPack> r9 = r0.packs
            int r9 = r9.size()
            r5.put(r6, r9)
            int r5 = r0.totalCount
            r6 = 1
            int r5 = r5 + r6
            r0.totalCount = r5
            java.util.ArrayList<java.lang.Integer> r5 = r0.rowHashCodes
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r11 = 3321(0xcf9, float:4.654E-42)
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r12 = 0
            r9[r12] = r11
            org.telegram.tgnet.TLRPC$StickerSet r3 = r3.set
            long r11 = r3.id
            java.lang.Long r3 = java.lang.Long.valueOf(r11)
            r9[r6] = r3
            int r3 = java.util.Arrays.hashCode(r9)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r5.add(r3)
        L_0x06a4:
            java.util.ArrayList<org.telegram.ui.Components.EmojiView$EmojiPack> r3 = r0.packs
            r3.add(r4)
            goto L_0x06b0
        L_0x06aa:
            r14 = r12
            r17 = r13
            r7 = 3
            r10 = -5531(0xffffffffffffea65, float:NaN)
        L_0x06b0:
            int r13 = r17 + 1
            r12 = r14
            r5 = 9211(0x23fb, float:1.2907E-41)
            r6 = 3212(0xc8c, float:4.501E-42)
            goto L_0x04e1
        L_0x06b9:
            org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda11 r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda11
            r2.<init>(r0)
            r0.post(r2)
            if (r23 == 0) goto L_0x06d3
            org.telegram.ui.SelectAnimatedEmojiDialog$18 r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$18
            r2.<init>(r1)
            r1 = 0
            androidx.recyclerview.widget.DiffUtil$DiffResult r1 = androidx.recyclerview.widget.DiffUtil.calculateDiff(r2, r1)
            org.telegram.ui.SelectAnimatedEmojiDialog$Adapter r2 = r0.adapter
            r1.dispatchUpdatesTo((androidx.recyclerview.widget.RecyclerView.Adapter) r2)
            goto L_0x06d8
        L_0x06d3:
            org.telegram.ui.SelectAnimatedEmojiDialog$Adapter r1 = r0.adapter
            r1.notifyDataSetChanged()
        L_0x06d8:
            org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r1 = r0.emojiGridView
            boolean r2 = r1.scrolledByUserOnce
            if (r2 != 0) goto L_0x06e2
            r2 = 1
            r1.scrollToPosition(r2)
        L_0x06e2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SelectAnimatedEmojiDialog.updateRows(boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateRows$15() {
        this.emojiTabs.updateEmojiPacks(this.packs);
    }

    public void expand(int i, View view) {
        int i2;
        Integer num;
        int i3;
        int i4;
        int i5;
        int i6 = this.positionToExpand.get(i);
        Integer num2 = null;
        boolean z = false;
        if (i6 >= 0 && i6 < this.packs.size()) {
            i5 = this.layoutManager.getSpanCount() * 3;
            EmojiView.EmojiPack emojiPack = this.packs.get(i6);
            if (!emojiPack.expanded) {
                if (i6 + 1 == this.packs.size()) {
                    z = true;
                }
                i2 = this.sectionToPosition.get(i6);
                this.expandedEmojiSets.add(Long.valueOf(emojiPack.set.id));
                i3 = emojiPack.expanded ? emojiPack.documents.size() : Math.min(i5, emojiPack.documents.size());
                num = emojiPack.documents.size() > i5 ? Integer.valueOf(i2 + 1 + i3) : null;
                emojiPack.expanded = true;
                i4 = emojiPack.documents.size();
            } else {
                return;
            }
        } else if (i6 == -1) {
            int spanCount = this.layoutManager.getSpanCount() * 5;
            boolean z2 = this.recentExpanded;
            if (!z2) {
                int i7 = (this.searchRow != -1 ? 1 : 0) + (this.includeHint ? 1 : 0);
                boolean z3 = this.includeEmpty;
                int i8 = i7 + (z3 ? 1 : 0);
                i3 = z2 ? this.recent.size() : Math.min((spanCount - z3) - 2, this.recent.size());
                i4 = this.recent.size();
                this.recentExpanded = true;
                num = null;
                int i9 = i8;
                i5 = spanCount;
                i2 = i9;
            } else {
                return;
            }
        } else {
            return;
        }
        if (i4 > i3) {
            num = Integer.valueOf(i2 + 1 + i3);
            num2 = Integer.valueOf(i4 - i3);
        }
        updateRows(true);
        if (num != null && num2 != null) {
            this.animateExpandFromButton = view;
            this.animateExpandFromPosition = num.intValue();
            this.animateExpandToPosition = num.intValue() + num2.intValue();
            this.animateExpandStartTime = SystemClock.elapsedRealtime();
            if (z) {
                post(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda12(this, num2.intValue() > i5 / 2 ? 1.5f : 3.5f, num.intValue()));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$expand$16(float f, int i) {
        try {
            LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(this.emojiGridView.getContext(), 0, f);
            linearSmoothScrollerCustom.setTargetPosition(i);
            this.layoutManager.startSmoothScroll(linearSmoothScrollerCustom);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.drawBackground) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) Math.min((float) AndroidUtilities.dp(324.0f), ((float) AndroidUtilities.displaySize.x) * 0.95f), NUM), View.MeasureSpec.makeMeasureSpec((int) Math.min((float) AndroidUtilities.dp(330.0f), ((float) AndroidUtilities.displaySize.y) * 0.75f), Integer.MIN_VALUE));
        } else {
            super.onMeasure(i, i2);
        }
    }

    /* access modifiers changed from: private */
    public int getCacheType() {
        int i = this.type;
        return (i == 0 || i == 2) ? 2 : 3;
    }

    public class EmojiListView extends RecyclerListView {
        /* access modifiers changed from: private */
        public LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables = new LongSparseArray<>();
        private int lastChildCount = -1;
        ArrayList<DrawingInBackgroundLine> lineDrawables = new ArrayList<>();
        ArrayList<DrawingInBackgroundLine> lineDrawablesTmp = new ArrayList<>();
        ArrayList<ArrayList<ImageViewEmoji>> unusedArrays = new ArrayList<>();
        ArrayList<DrawingInBackgroundLine> unusedLineDrawables = new ArrayList<>();
        SparseArray<ArrayList<ImageViewEmoji>> viewsGroupedByLines = new SparseArray<>();

        public EmojiListView(Context context) {
            super(context);
            setDrawSelectorBehind(true);
            setClipToPadding(false);
            setSelectorRadius(AndroidUtilities.dp(4.0f));
            setSelectorDrawableColor(Theme.getColor("listSelectorSDK21", this.resourcesProvider));
        }

        private AnimatedEmojiSpan[] getAnimatedEmojiSpans() {
            AnimatedEmojiSpan[] animatedEmojiSpanArr = new AnimatedEmojiSpan[SelectAnimatedEmojiDialog.this.emojiGridView.getChildCount()];
            for (int i = 0; i < SelectAnimatedEmojiDialog.this.emojiGridView.getChildCount(); i++) {
                View childAt = SelectAnimatedEmojiDialog.this.emojiGridView.getChildAt(i);
                if (childAt instanceof ImageViewEmoji) {
                    animatedEmojiSpanArr[i] = ((ImageViewEmoji) childAt).span;
                }
            }
            return animatedEmojiSpanArr;
        }

        public void updateEmojiDrawables() {
            this.animatedEmojiDrawables = AnimatedEmojiSpan.update(SelectAnimatedEmojiDialog.this.getCacheType(), (View) this, getAnimatedEmojiSpans(), this.animatedEmojiDrawables);
        }

        public boolean drawChild(Canvas canvas, View view, long j) {
            return super.drawChild(canvas, view, j);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:7:0x0017, code lost:
            if (((org.telegram.ui.Components.AnimatedEmojiDrawable) r0).canOverrideColor() != false) goto L_0x0019;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean canHighlightChildAt(android.view.View r3, float r4, float r5) {
            /*
                r2 = this;
                boolean r0 = r3 instanceof org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji
                if (r0 == 0) goto L_0x002b
                r0 = r3
                org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r0 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r0
                boolean r1 = r0.empty
                if (r1 != 0) goto L_0x0019
                android.graphics.drawable.Drawable r0 = r0.drawable
                boolean r1 = r0 instanceof org.telegram.ui.Components.AnimatedEmojiDrawable
                if (r1 == 0) goto L_0x002b
                org.telegram.ui.Components.AnimatedEmojiDrawable r0 = (org.telegram.ui.Components.AnimatedEmojiDrawable) r0
                boolean r0 = r0.canOverrideColor()
                if (r0 == 0) goto L_0x002b
            L_0x0019:
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r2.resourcesProvider
                java.lang.String r1 = "windowBackgroundWhiteBlueIcon"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r1, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
                r1 = 30
                int r0 = androidx.core.graphics.ColorUtils.setAlphaComponent(r0, r1)
                r2.setSelectorDrawableColor(r0)
                goto L_0x0036
            L_0x002b:
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r2.resourcesProvider
                java.lang.String r1 = "listSelectorSDK21"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r1, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
                r2.setSelectorDrawableColor(r0)
            L_0x0036:
                boolean r3 = super.canHighlightChildAt(r3, r4, r5)
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.canHighlightChildAt(android.view.View, float, float):boolean");
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (SelectAnimatedEmojiDialog.this.showAnimator == null || !SelectAnimatedEmojiDialog.this.showAnimator.isRunning()) {
                updateEmojiDrawables();
                this.lastChildCount = getChildCount();
            }
        }

        public void dispatchDraw(Canvas canvas) {
            DrawingInBackgroundLine drawingInBackgroundLine;
            ImageReceiver imageReceiver;
            if (getVisibility() == 0) {
                int saveCount = canvas.getSaveCount();
                if (!(this.lastChildCount == getChildCount() || SelectAnimatedEmojiDialog.this.showAnimator == null || SelectAnimatedEmojiDialog.this.showAnimator.isRunning())) {
                    updateEmojiDrawables();
                    this.lastChildCount = getChildCount();
                }
                if (!this.selectorRect.isEmpty()) {
                    this.selectorDrawable.setBounds(this.selectorRect);
                    canvas.save();
                    Consumer<Canvas> consumer = this.selectorTransformer;
                    if (consumer != null) {
                        consumer.accept(canvas);
                    }
                    this.selectorDrawable.draw(canvas);
                    canvas.restore();
                }
                for (int i = 0; i < this.viewsGroupedByLines.size(); i++) {
                    ArrayList valueAt = this.viewsGroupedByLines.valueAt(i);
                    valueAt.clear();
                    this.unusedArrays.add(valueAt);
                }
                this.viewsGroupedByLines.clear();
                if ((SelectAnimatedEmojiDialog.this.animateExpandStartTime > 0 && SystemClock.elapsedRealtime() - SelectAnimatedEmojiDialog.this.animateExpandStartTime < SelectAnimatedEmojiDialog.this.animateExpandDuration()) && SelectAnimatedEmojiDialog.this.animateExpandFromButton != null) {
                    int access$6300 = SelectAnimatedEmojiDialog.this.animateExpandFromPosition;
                }
                if (this.animatedEmojiDrawables != null) {
                    for (int i2 = 0; i2 < getChildCount(); i2++) {
                        View childAt = getChildAt(i2);
                        if (childAt instanceof ImageViewEmoji) {
                            ImageViewEmoji imageViewEmoji = (ImageViewEmoji) childAt;
                            imageViewEmoji.updatePressedProgress();
                            int y = SelectAnimatedEmojiDialog.this.smoothScrolling ? (int) childAt.getY() : childAt.getTop();
                            ArrayList arrayList = this.viewsGroupedByLines.get(y);
                            canvas.save();
                            canvas.translate(imageViewEmoji.getX(), imageViewEmoji.getY());
                            imageViewEmoji.drawSelected(canvas);
                            canvas.restore();
                            if (imageViewEmoji.getBackground() != null) {
                                imageViewEmoji.getBackground().setBounds((int) imageViewEmoji.getX(), (int) imageViewEmoji.getY(), ((int) imageViewEmoji.getX()) + imageViewEmoji.getWidth(), ((int) imageViewEmoji.getY()) + imageViewEmoji.getHeight());
                                imageViewEmoji.getBackground().setAlpha((int) (((float) 255) * imageViewEmoji.getAlpha()));
                                imageViewEmoji.getBackground().draw(canvas);
                                imageViewEmoji.getBackground().setAlpha(255);
                            }
                            if (arrayList == null) {
                                if (!this.unusedArrays.isEmpty()) {
                                    ArrayList<ArrayList<ImageViewEmoji>> arrayList2 = this.unusedArrays;
                                    arrayList = arrayList2.remove(arrayList2.size() - 1);
                                } else {
                                    arrayList = new ArrayList();
                                }
                                this.viewsGroupedByLines.put(y, arrayList);
                            }
                            arrayList.add(imageViewEmoji);
                            PremiumLockIconView premiumLockIconView = imageViewEmoji.premiumLockIconView;
                            if (premiumLockIconView != null && premiumLockIconView.getVisibility() == 0 && imageViewEmoji.premiumLockIconView.getImageReceiver() == null && (imageReceiver = imageViewEmoji.imageReceiverToDraw) != null) {
                                imageViewEmoji.premiumLockIconView.setImageReceiver(imageReceiver);
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
                    ImageViewEmoji imageViewEmoji2 = valueAt2.get(0);
                    int childAdapterPosition = getChildAdapterPosition(imageViewEmoji2);
                    int i4 = 0;
                    while (true) {
                        if (i4 >= this.lineDrawablesTmp.size()) {
                            break;
                        } else if (this.lineDrawablesTmp.get(i4).position == childAdapterPosition) {
                            drawingInBackgroundLine2 = this.lineDrawablesTmp.get(i4);
                            this.lineDrawablesTmp.remove(i4);
                            break;
                        } else {
                            i4++;
                        }
                    }
                    if (drawingInBackgroundLine == null) {
                        if (!this.unusedLineDrawables.isEmpty()) {
                            ArrayList<DrawingInBackgroundLine> arrayList3 = this.unusedLineDrawables;
                            drawingInBackgroundLine = arrayList3.remove(arrayList3.size() - 1);
                        } else {
                            drawingInBackgroundLine = new DrawingInBackgroundLine();
                        }
                        drawingInBackgroundLine.position = childAdapterPosition;
                        drawingInBackgroundLine.onAttachToWindow();
                    }
                    this.lineDrawables.add(drawingInBackgroundLine);
                    drawingInBackgroundLine.imageViewEmojis = valueAt2;
                    canvas.save();
                    canvas.translate((float) imageViewEmoji2.getLeft(), imageViewEmoji2.getY());
                    drawingInBackgroundLine.startOffset = imageViewEmoji2.getLeft();
                    int measuredWidth = getMeasuredWidth() - (imageViewEmoji2.getLeft() * 2);
                    int measuredHeight = imageViewEmoji2.getMeasuredHeight();
                    if (measuredWidth > 0 && measuredHeight > 0) {
                        drawingInBackgroundLine.draw(canvas, currentTimeMillis, measuredWidth, measuredHeight, 1.0f);
                    }
                    canvas.restore();
                    i3++;
                }
                for (int i5 = 0; i5 < this.lineDrawablesTmp.size(); i5++) {
                    if (this.unusedLineDrawables.size() < 3) {
                        this.unusedLineDrawables.add(this.lineDrawablesTmp.get(i5));
                        this.lineDrawablesTmp.get(i5).imageViewEmojis = null;
                        this.lineDrawablesTmp.get(i5).reset();
                    } else {
                        this.lineDrawablesTmp.get(i5).onDetachFromWindow();
                    }
                }
                this.lineDrawablesTmp.clear();
                for (int i6 = 0; i6 < getChildCount(); i6++) {
                    View childAt2 = getChildAt(i6);
                    if (childAt2 instanceof ImageViewEmoji) {
                        ImageViewEmoji imageViewEmoji3 = (ImageViewEmoji) childAt2;
                        if (imageViewEmoji3.premiumLockIconView != null) {
                            canvas.save();
                            canvas.translate((float) ((int) (imageViewEmoji3.getX() + imageViewEmoji3.premiumLockIconView.getX())), (float) ((int) (imageViewEmoji3.getY() + imageViewEmoji3.premiumLockIconView.getY())));
                            imageViewEmoji3.premiumLockIconView.draw(canvas);
                            canvas.restore();
                        }
                    } else if (childAt2 != null) {
                        canvas.save();
                        canvas.translate((float) ((int) childAt2.getX()), (float) ((int) childAt2.getY()));
                        childAt2.draw(canvas);
                        canvas.restore();
                    }
                }
                canvas.restoreToCount(saveCount);
            }
        }

        public class DrawingInBackgroundLine extends DrawingInBackgroundThreadDrawable {
            private OvershootInterpolator appearScaleInterpolator = new OvershootInterpolator(3.0f);
            ArrayList<ImageViewEmoji> drawInBackgroundViews = new ArrayList<>();
            ArrayList<ImageViewEmoji> imageViewEmojis;
            public int position;
            float skewAlpha = 1.0f;
            boolean skewBelow = false;
            public int startOffset;

            public DrawingInBackgroundLine() {
            }

            /* JADX WARNING: Removed duplicated region for block: B:49:0x011c  */
            /* JADX WARNING: Removed duplicated region for block: B:50:0x012d  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void draw(android.graphics.Canvas r12, long r13, int r15, int r16, float r17) {
                /*
                    r11 = this;
                    r0 = r11
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r1 = r0.imageViewEmojis
                    if (r1 != 0) goto L_0x0006
                    return
                L_0x0006:
                    r2 = 1065353216(0x3var_, float:1.0)
                    r0.skewAlpha = r2
                    r3 = 0
                    r0.skewBelow = r3
                    boolean r1 = r1.isEmpty()
                    r4 = 0
                    if (r1 != 0) goto L_0x0061
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r1 = r0.imageViewEmojis
                    java.lang.Object r1 = r1.get(r3)
                    android.view.View r1 = (android.view.View) r1
                    float r5 = r1.getY()
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    int r6 = r6.getHeight()
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r7 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    int r7 = r7.getPaddingBottom()
                    int r6 = r6 - r7
                    int r7 = r1.getHeight()
                    int r6 = r6 - r7
                    float r6 = (float) r6
                    int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
                    if (r5 <= 0) goto L_0x0061
                    float r5 = r1.getY()
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    int r6 = r6.getHeight()
                    float r6 = (float) r6
                    float r5 = r5 - r6
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    int r6 = r6.getPaddingBottom()
                    float r6 = (float) r6
                    float r5 = r5 + r6
                    float r5 = -r5
                    int r1 = r1.getHeight()
                    float r1 = (float) r1
                    float r5 = r5 / r1
                    float r1 = androidx.core.math.MathUtils.clamp((float) r5, (float) r4, (float) r2)
                    r0.skewAlpha = r1
                    r5 = 1048576000(0x3e800000, float:0.25)
                    r6 = 1061158912(0x3var_, float:0.75)
                    float r1 = r1 * r6
                    float r1 = r1 + r5
                    r0.skewAlpha = r1
                L_0x0061:
                    float r1 = r0.skewAlpha
                    r5 = 1
                    int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                    if (r1 < 0) goto L_0x009a
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r1 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    boolean r1 = r1.isAnimating()
                    if (r1 != 0) goto L_0x009a
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r1 = r0.imageViewEmojis
                    int r1 = r1.size()
                    r6 = 4
                    if (r1 <= r6) goto L_0x009a
                    int r1 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
                    if (r1 == 0) goto L_0x009a
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r1 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r1 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    android.animation.ValueAnimator r1 = r1.showAnimator
                    if (r1 == 0) goto L_0x0098
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r1 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r1 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    android.animation.ValueAnimator r1 = r1.showAnimator
                    boolean r1 = r1.isRunning()
                    if (r1 == 0) goto L_0x0098
                    goto L_0x009a
                L_0x0098:
                    r1 = 0
                    goto L_0x009b
                L_0x009a:
                    r1 = 1
                L_0x009b:
                    if (r1 != 0) goto L_0x0119
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r6 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    long r6 = r6.animateExpandStartTime
                    r8 = 0
                    int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                    if (r10 <= 0) goto L_0x00c6
                    long r6 = android.os.SystemClock.elapsedRealtime()
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r8 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r8 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    long r8 = r8.animateExpandStartTime
                    long r6 = r6 - r8
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r8 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r8 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    long r8 = r8.animateExpandDuration()
                    int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                    if (r10 >= 0) goto L_0x00c6
                    r6 = 1
                    goto L_0x00c7
                L_0x00c6:
                    r6 = 0
                L_0x00c7:
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r7 = r0.imageViewEmojis
                    int r7 = r7.size()
                    if (r3 >= r7) goto L_0x0119
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r7 = r0.imageViewEmojis
                    java.lang.Object r7 = r7.get(r3)
                    org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r7 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r7
                    float r8 = r7.pressedProgress
                    int r8 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
                    if (r8 != 0) goto L_0x011a
                    android.animation.ValueAnimator r8 = r7.backAnimator
                    if (r8 != 0) goto L_0x011a
                    float r8 = r7.getTranslationX()
                    int r8 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
                    if (r8 != 0) goto L_0x011a
                    float r8 = r7.getTranslationY()
                    int r8 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
                    if (r8 != 0) goto L_0x011a
                    float r8 = r7.getAlpha()
                    int r8 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
                    if (r8 != 0) goto L_0x011a
                    if (r6 == 0) goto L_0x0116
                    int r8 = r7.position
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r9 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r9 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    int r9 = r9.animateExpandFromPosition
                    if (r8 <= r9) goto L_0x0116
                    int r7 = r7.position
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r8 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r8 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    int r8 = r8.animateExpandToPosition
                    if (r7 >= r8) goto L_0x0116
                    goto L_0x011a
                L_0x0116:
                    int r3 = r3 + 1
                    goto L_0x00c7
                L_0x0119:
                    r5 = r1
                L_0x011a:
                    if (r5 == 0) goto L_0x012d
                    long r1 = java.lang.System.currentTimeMillis()
                    r11.prepareDraw(r1)
                    r1 = r12
                    r2 = r17
                    r11.drawInUiThread(r12, r2)
                    r11.reset()
                    goto L_0x0133
                L_0x012d:
                    r1 = r12
                    r2 = r17
                    super.draw(r12, r13, r15, r16, r17)
                L_0x0133:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.DrawingInBackgroundLine.draw(android.graphics.Canvas, long, int, int, float):void");
            }

            public void drawBitmap(Canvas canvas, Bitmap bitmap, Paint paint) {
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
            }

            /* JADX WARNING: type inference failed for: r7v13, types: [android.graphics.drawable.Drawable] */
            /* JADX WARNING: Multi-variable type inference failed */
            /* JADX WARNING: Unknown variable types count: 1 */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void prepareDraw(long r18) {
                /*
                    r17 = this;
                    r0 = r17
                    r1 = r18
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r3 = r0.drawInBackgroundViews
                    r3.clear()
                    r3 = 0
                    r4 = 0
                L_0x000b:
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r5 = r0.imageViewEmojis
                    int r5 = r5.size()
                    if (r4 >= r5) goto L_0x02ac
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r5 = r0.imageViewEmojis
                    java.lang.Object r5 = r5.get(r4)
                    org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r5 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r5
                    boolean r6 = r5.notDraw
                    if (r6 == 0) goto L_0x0021
                    goto L_0x02a8
                L_0x0021:
                    boolean r6 = r5.empty
                    r7 = 0
                    r8 = 1065353216(0x3var_, float:1.0)
                    r9 = 1073741824(0x40000000, float:2.0)
                    if (r6 == 0) goto L_0x00e8
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r6 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    android.graphics.drawable.Drawable r6 = r6.getPremiumStar()
                    float r10 = r5.pressedProgress
                    int r7 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
                    if (r7 != 0) goto L_0x003e
                    boolean r7 = r5.selected
                    if (r7 == 0) goto L_0x0057
                L_0x003e:
                    r7 = 1061997773(0x3f4ccccd, float:0.8)
                    r10 = 1045220557(0x3e4ccccd, float:0.2)
                    boolean r11 = r5.selected
                    if (r11 == 0) goto L_0x004c
                    r11 = 1060320051(0x3var_, float:0.7)
                    goto L_0x0050
                L_0x004c:
                    float r11 = r5.pressedProgress
                L_0x0050:
                    float r11 = r8 - r11
                    float r11 = r11 * r10
                    float r11 = r11 + r7
                    float r8 = r8 * r11
                L_0x0057:
                    if (r6 != 0) goto L_0x005b
                    goto L_0x02a8
                L_0x005b:
                    r7 = 255(0xff, float:3.57E-43)
                    r6.setAlpha(r7)
                    int r7 = r5.getWidth()
                    int r10 = r5.getPaddingLeft()
                    int r7 = r7 - r10
                    int r10 = r5.getPaddingRight()
                    int r7 = r7 - r10
                    int r10 = r5.getHeight()
                    int r11 = r5.getPaddingTop()
                    int r10 = r10 - r11
                    int r11 = r5.getPaddingBottom()
                    int r10 = r10 - r11
                    android.graphics.Rect r11 = org.telegram.messenger.AndroidUtilities.rectTmp2
                    int r12 = r5.getWidth()
                    float r12 = (float) r12
                    float r12 = r12 / r9
                    float r7 = (float) r7
                    float r7 = r7 / r9
                    float r13 = r5.getScaleX()
                    float r13 = r13 * r7
                    float r13 = r13 * r8
                    float r12 = r12 - r13
                    int r12 = (int) r12
                    int r13 = r5.getHeight()
                    float r13 = (float) r13
                    float r13 = r13 / r9
                    float r10 = (float) r10
                    float r10 = r10 / r9
                    float r14 = r5.getScaleY()
                    float r14 = r14 * r10
                    float r14 = r14 * r8
                    float r13 = r13 - r14
                    int r13 = (int) r13
                    int r14 = r5.getWidth()
                    float r14 = (float) r14
                    float r14 = r14 / r9
                    float r15 = r5.getScaleX()
                    float r7 = r7 * r15
                    float r7 = r7 * r8
                    float r14 = r14 + r7
                    int r7 = (int) r14
                    int r14 = r5.getHeight()
                    float r14 = (float) r14
                    float r14 = r14 / r9
                    float r9 = r5.getScaleY()
                    float r10 = r10 * r9
                    float r10 = r10 * r8
                    float r14 = r14 + r10
                    int r8 = (int) r14
                    r11.set(r12, r13, r7, r8)
                    int r7 = r5.getLeft()
                    int r8 = r0.startOffset
                    int r7 = r7 - r8
                    r11.offset(r7, r3)
                    android.graphics.Rect r7 = r5.drawableBounds
                    if (r7 != 0) goto L_0x00da
                    android.graphics.Rect r7 = new android.graphics.Rect
                    r7.<init>()
                    r5.drawableBounds = r7
                L_0x00da:
                    android.graphics.Rect r7 = r5.drawableBounds
                    r7.set(r11)
                    r5.drawable = r6
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r6 = r0.drawInBackgroundViews
                    r6.add(r5)
                    goto L_0x02a8
                L_0x00e8:
                    float r6 = r5.pressedProgress
                    int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
                    if (r6 != 0) goto L_0x00f4
                    boolean r6 = r5.selected
                    if (r6 == 0) goto L_0x00fc
                L_0x00f4:
                    boolean r6 = r5.selected
                    if (r6 == 0) goto L_0x00f9
                    goto L_0x00fc
                L_0x00f9:
                    float unused = r5.pressedProgress
                L_0x00fc:
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r6 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    long r10 = r6.animateExpandStartTime
                    r12 = 0
                    int r6 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
                    if (r6 <= 0) goto L_0x0125
                    long r10 = android.os.SystemClock.elapsedRealtime()
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r6 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    long r14 = r6.animateExpandStartTime
                    long r10 = r10 - r14
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r6 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    long r14 = r6.animateExpandDuration()
                    int r6 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1))
                    if (r6 >= 0) goto L_0x0125
                    r6 = 1
                    goto L_0x0126
                L_0x0125:
                    r6 = 0
                L_0x0126:
                    r10 = 1082130432(0x40800000, float:4.0)
                    if (r6 == 0) goto L_0x01a1
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r6 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    int r6 = r6.animateExpandFromPosition
                    if (r6 < 0) goto L_0x01a1
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r6 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    int r6 = r6.animateExpandToPosition
                    if (r6 < 0) goto L_0x01a1
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r6 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    long r14 = r6.animateExpandStartTime
                    int r6 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1))
                    if (r6 <= 0) goto L_0x01a1
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    int r6 = r6.getChildAdapterPosition(r5)
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r11 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r11 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    int r11 = r11.animateExpandFromPosition
                    int r6 = r6 - r11
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r11 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r11 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    int r11 = r11.animateExpandToPosition
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r12 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r12 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    int r12 = r12.animateExpandFromPosition
                    int r11 = r11 - r12
                    if (r6 < 0) goto L_0x019e
                    if (r6 >= r11) goto L_0x019e
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r12 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r12 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    long r12 = r12.animateExpandAppearDuration()
                    float r12 = (float) r12
                    long r13 = android.os.SystemClock.elapsedRealtime()
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r15 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r15 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    long r15 = r15.animateExpandStartTime
                    long r13 = r13 - r15
                    float r13 = (float) r13
                    float r13 = r13 / r12
                    float r7 = androidx.core.math.MathUtils.clamp((float) r13, (float) r7, (float) r8)
                    float r6 = (float) r6
                    float r11 = (float) r11
                    float r12 = r11 / r10
                    float r13 = org.telegram.messenger.AndroidUtilities.cascade(r7, r6, r11, r12)
                    float r6 = org.telegram.messenger.AndroidUtilities.cascade(r7, r6, r11, r12)
                    android.view.animation.OvershootInterpolator r7 = r0.appearScaleInterpolator
                    r7.getInterpolation(r6)
                    float r13 = r13 * r8
                    goto L_0x01a5
                L_0x019e:
                    r13 = 1065353216(0x3var_, float:1.0)
                    goto L_0x01a5
                L_0x01a1:
                    float r13 = r5.getAlpha()
                L_0x01a5:
                    boolean r6 = r5.isDefaultReaction
                    if (r6 != 0) goto L_0x01e0
                    org.telegram.ui.Components.AnimatedEmojiSpan r6 = r5.span
                    if (r6 != 0) goto L_0x01af
                    goto L_0x02a8
                L_0x01af:
                    r6 = 0
                    android.graphics.drawable.Drawable r7 = r5.drawable
                    boolean r11 = r7 instanceof org.telegram.ui.Components.AnimatedEmojiDrawable
                    if (r11 == 0) goto L_0x01b9
                    r6 = r7
                    org.telegram.ui.Components.AnimatedEmojiDrawable r6 = (org.telegram.ui.Components.AnimatedEmojiDrawable) r6
                L_0x01b9:
                    if (r6 == 0) goto L_0x02a8
                    org.telegram.messenger.ImageReceiver r7 = r6.getImageReceiver()
                    if (r7 != 0) goto L_0x01c3
                    goto L_0x02a8
                L_0x01c3:
                    org.telegram.messenger.ImageReceiver r7 = r6.getImageReceiver()
                    r11 = 1132396544(0x437var_, float:255.0)
                    float r13 = r13 * r11
                    int r11 = (int) r13
                    r6.setAlpha(r11)
                    r5.setDrawable(r6)
                    android.graphics.drawable.Drawable r6 = r5.drawable
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r11 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r11 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    android.graphics.ColorFilter r11 = r11.premiumStarColorFilter
                    r6.setColorFilter(r11)
                    goto L_0x01e5
                L_0x01e0:
                    org.telegram.messenger.ImageReceiver r7 = r5.imageReceiver
                    r7.setAlpha(r13)
                L_0x01e5:
                    if (r7 != 0) goto L_0x01e9
                    goto L_0x02a8
                L_0x01e9:
                    boolean r6 = r5.selected
                    if (r6 == 0) goto L_0x01f5
                    int r6 = org.telegram.messenger.AndroidUtilities.dp(r10)
                    r7.setRoundRadius((int) r6)
                    goto L_0x01f8
                L_0x01f5:
                    r7.setRoundRadius((int) r3)
                L_0x01f8:
                    org.telegram.messenger.ImageReceiver$BackgroundThreadDrawHolder r6 = r5.backgroundThreadDrawHolder
                    org.telegram.messenger.ImageReceiver$BackgroundThreadDrawHolder r6 = r7.setDrawInBackgroundThread(r6)
                    r5.backgroundThreadDrawHolder = r6
                    r6.time = r1
                    r5.imageReceiverToDraw = r7
                    r5.update(r1)
                    r5.getWidth()
                    r5.getPaddingLeft()
                    r5.getPaddingRight()
                    r5.getHeight()
                    r5.getPaddingTop()
                    r5.getPaddingBottom()
                    android.graphics.Rect r6 = org.telegram.messenger.AndroidUtilities.rectTmp2
                    int r7 = r5.getPaddingLeft()
                    int r10 = r5.getPaddingTop()
                    int r11 = r5.getWidth()
                    int r12 = r5.getPaddingRight()
                    int r11 = r11 - r12
                    int r12 = r5.getHeight()
                    int r13 = r5.getPaddingBottom()
                    int r12 = r12 - r13
                    r6.set(r7, r10, r11, r12)
                    boolean r7 = r5.selected
                    if (r7 == 0) goto L_0x028a
                    int r7 = r6.centerX()
                    float r7 = (float) r7
                    int r10 = r6.width()
                    float r10 = (float) r10
                    float r10 = r10 / r9
                    r11 = 1063004406(0x3f5CLASSNAMEf6, float:0.86)
                    float r10 = r10 * r11
                    float r7 = r7 - r10
                    int r7 = java.lang.Math.round(r7)
                    int r10 = r6.centerY()
                    float r10 = (float) r10
                    int r12 = r6.height()
                    float r12 = (float) r12
                    float r12 = r12 / r9
                    float r12 = r12 * r11
                    float r10 = r10 - r12
                    int r10 = java.lang.Math.round(r10)
                    int r12 = r6.centerX()
                    float r12 = (float) r12
                    int r13 = r6.width()
                    float r13 = (float) r13
                    float r13 = r13 / r9
                    float r13 = r13 * r11
                    float r12 = r12 + r13
                    int r12 = java.lang.Math.round(r12)
                    int r13 = r6.centerY()
                    float r13 = (float) r13
                    int r14 = r6.height()
                    float r14 = (float) r14
                    float r14 = r14 / r9
                    float r14 = r14 * r11
                    float r13 = r13 + r14
                    int r9 = java.lang.Math.round(r13)
                    r6.set(r7, r10, r12, r9)
                L_0x028a:
                    int r7 = r5.getLeft()
                    float r9 = r5.getTranslationX()
                    int r9 = (int) r9
                    int r7 = r7 + r9
                    int r9 = r0.startOffset
                    int r7 = r7 - r9
                    r6.offset(r7, r3)
                    org.telegram.messenger.ImageReceiver$BackgroundThreadDrawHolder r7 = r5.backgroundThreadDrawHolder
                    r7.setBounds(r6)
                    r5.skewAlpha = r8
                    r5.skewIndex = r4
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r6 = r0.drawInBackgroundViews
                    r6.add(r5)
                L_0x02a8:
                    int r4 = r4 + 1
                    goto L_0x000b
                L_0x02ac:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.DrawingInBackgroundLine.prepareDraw(long):void");
            }

            public void drawInBackground(Canvas canvas) {
                for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                    ImageViewEmoji imageViewEmoji = this.drawInBackgroundViews.get(i);
                    if (!imageViewEmoji.notDraw) {
                        if (imageViewEmoji.empty) {
                            imageViewEmoji.drawable.setBounds(imageViewEmoji.drawableBounds);
                            Drawable drawable = imageViewEmoji.drawable;
                            if (drawable instanceof AnimatedEmojiDrawable) {
                                ((AnimatedEmojiDrawable) drawable).draw(canvas, false);
                            } else {
                                drawable.draw(canvas);
                            }
                        } else {
                            ImageReceiver imageReceiver = imageViewEmoji.imageReceiverToDraw;
                            if (imageReceiver != null) {
                                imageReceiver.draw(canvas, imageViewEmoji.backgroundThreadDrawHolder);
                            }
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void drawInUiThread(Canvas canvas, float f) {
                Canvas canvas2 = canvas;
                if (this.imageViewEmojis != null) {
                    canvas.save();
                    canvas2.translate((float) (-this.startOffset), 0.0f);
                    float f2 = f;
                    for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                        ImageViewEmoji imageViewEmoji = this.imageViewEmojis.get(i);
                        if (!imageViewEmoji.notDraw) {
                            float scaleX = imageViewEmoji.getScaleX();
                            if (imageViewEmoji.pressedProgress != 0.0f || imageViewEmoji.selected) {
                                scaleX *= ((1.0f - (imageViewEmoji.selected ? 0.7f : imageViewEmoji.pressedProgress)) * 0.2f) + 0.8f;
                            }
                            boolean z = true;
                            boolean z2 = SelectAnimatedEmojiDialog.this.animateExpandStartTime > 0 && SystemClock.elapsedRealtime() - SelectAnimatedEmojiDialog.this.animateExpandStartTime < SelectAnimatedEmojiDialog.this.animateExpandDuration();
                            if (!z2 || SelectAnimatedEmojiDialog.this.animateExpandFromPosition < 0 || SelectAnimatedEmojiDialog.this.animateExpandToPosition < 0 || SelectAnimatedEmojiDialog.this.animateExpandStartTime <= 0) {
                                z = false;
                            }
                            if (z) {
                                int childAdapterPosition = EmojiListView.this.getChildAdapterPosition(imageViewEmoji) - SelectAnimatedEmojiDialog.this.animateExpandFromPosition;
                                int access$6500 = SelectAnimatedEmojiDialog.this.animateExpandToPosition - SelectAnimatedEmojiDialog.this.animateExpandFromPosition;
                                if (childAdapterPosition >= 0 && childAdapterPosition < access$6500) {
                                    float clamp = MathUtils.clamp(((float) (SystemClock.elapsedRealtime() - SelectAnimatedEmojiDialog.this.animateExpandStartTime)) / ((float) SelectAnimatedEmojiDialog.this.animateExpandAppearDuration()), 0.0f, 1.0f);
                                    float f3 = (float) childAdapterPosition;
                                    float f4 = (float) access$6500;
                                    float f5 = f4 / 4.0f;
                                    float cascade = AndroidUtilities.cascade(clamp, f3, f4, f5);
                                    scaleX *= (this.appearScaleInterpolator.getInterpolation(AndroidUtilities.cascade(clamp, f3, f4, f5)) * 0.5f) + 0.5f;
                                    f2 = cascade;
                                }
                            } else {
                                f2 = imageViewEmoji.getAlpha();
                            }
                            Rect rect = AndroidUtilities.rectTmp2;
                            rect.set(((int) imageViewEmoji.getX()) + imageViewEmoji.getPaddingLeft(), imageViewEmoji.getPaddingTop(), (((int) imageViewEmoji.getX()) + imageViewEmoji.getWidth()) - imageViewEmoji.getPaddingRight(), imageViewEmoji.getHeight() - imageViewEmoji.getPaddingBottom());
                            if (!SelectAnimatedEmojiDialog.this.smoothScrolling && !z2) {
                                rect.offset(0, (int) imageViewEmoji.getTranslationY());
                            }
                            Drawable drawable = null;
                            if (imageViewEmoji.empty) {
                                drawable = SelectAnimatedEmojiDialog.this.getPremiumStar();
                                drawable.setBounds(rect);
                                drawable.setAlpha(255);
                            } else if (imageViewEmoji.isDefaultReaction) {
                                ImageReceiver imageReceiver = imageViewEmoji.imageReceiver;
                                if (imageReceiver != null) {
                                    imageReceiver.setImageCoords(rect);
                                }
                            } else if (!(imageViewEmoji.span == null || imageViewEmoji.notDraw || (drawable = imageViewEmoji.drawable) == null)) {
                                drawable.setAlpha(255);
                                drawable.setBounds(rect);
                            }
                            Drawable drawable2 = imageViewEmoji.drawable;
                            if (drawable2 instanceof AnimatedEmojiDrawable) {
                                drawable2.setColorFilter(SelectAnimatedEmojiDialog.this.premiumStarColorFilter);
                            }
                            float f6 = this.skewAlpha;
                            imageViewEmoji.skewAlpha = f6;
                            imageViewEmoji.skewIndex = i;
                            if (scaleX != 1.0f || f6 < 1.0f) {
                                canvas.save();
                                canvas2.scale(scaleX, scaleX, (float) rect.centerX(), (float) rect.centerY());
                                skew(canvas2, i, imageViewEmoji.getHeight());
                                drawImage(canvas2, drawable, imageViewEmoji, f2);
                                canvas.restore();
                            } else {
                                drawImage(canvas2, drawable, imageViewEmoji, f2);
                            }
                        }
                    }
                    canvas.restore();
                }
            }

            private void skew(Canvas canvas, int i, int i2) {
                float f = this.skewAlpha;
                if (f >= 1.0f) {
                    return;
                }
                if (this.skewBelow) {
                    canvas.translate(0.0f, (float) i2);
                    canvas.skew((1.0f - ((((float) i) * 2.0f) / ((float) this.imageViewEmojis.size()))) * (-(1.0f - this.skewAlpha)), 0.0f);
                    canvas.translate(0.0f, (float) (-i2));
                    return;
                }
                canvas.scale(1.0f, f, 0.0f, 0.0f);
                canvas.skew((1.0f - ((((float) i) * 2.0f) / ((float) this.imageViewEmojis.size()))) * (1.0f - this.skewAlpha), 0.0f);
            }

            private void drawImage(Canvas canvas, Drawable drawable, ImageViewEmoji imageViewEmoji, float f) {
                ImageReceiver imageReceiver;
                if (drawable != null) {
                    drawable.setColorFilter(SelectAnimatedEmojiDialog.this.premiumStarColorFilter);
                    drawable.setAlpha((int) (f * 255.0f));
                    if (drawable instanceof AnimatedEmojiDrawable) {
                        ((AnimatedEmojiDrawable) drawable).draw(canvas, false);
                    } else {
                        drawable.draw(canvas);
                    }
                    PremiumLockIconView premiumLockIconView = imageViewEmoji.premiumLockIconView;
                } else if (imageViewEmoji.isDefaultReaction && (imageReceiver = imageViewEmoji.imageReceiver) != null) {
                    imageReceiver.setAlpha(f);
                    imageViewEmoji.imageReceiver.draw(canvas);
                }
            }

            public void onFrameReady() {
                super.onFrameReady();
                for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                    ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder = this.drawInBackgroundViews.get(i).backgroundThreadDrawHolder;
                    if (backgroundThreadDrawHolder != null) {
                        backgroundThreadDrawHolder.release();
                    }
                }
                SelectAnimatedEmojiDialog.this.emojiGridView.invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = SelectAnimatedEmojiDialog.this;
            if (this == selectAnimatedEmojiDialog.emojiGridView) {
                selectAnimatedEmojiDialog.bigReactionImageReceiver.onAttachedToWindow();
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = SelectAnimatedEmojiDialog.this;
            if (this == selectAnimatedEmojiDialog.emojiGridView) {
                selectAnimatedEmojiDialog.bigReactionImageReceiver.onDetachedFromWindow();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.isAttached = true;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredEmojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentEmojiStatusesUpdate);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setBigReactionAnimatedEmoji((AnimatedEmojiDrawable) null);
        this.isAttached = false;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredEmojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentEmojiStatusesUpdate);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.scrimDrawable;
        if (swapAnimatedEmojiDrawable instanceof AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) {
            swapAnimatedEmojiDrawable.removeParentView(this);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoad) {
            if (objArr[0].intValue() == 5) {
                updateRows(true);
            }
        } else if (i == NotificationCenter.featuredEmojiDidLoad) {
            updateRows(true);
        } else if (i == NotificationCenter.recentEmojiStatusesUpdate) {
            updateRows(true);
        } else if (i == NotificationCenter.groupStickersDidLoad && this.defaultSetLoading) {
            updateRows(true);
            this.defaultSetLoading = false;
        }
    }

    public void onShow(Runnable runnable) {
        Integer num = this.listStateId;
        if (num != null) {
            Parcelable parcelable = listStates.get(num);
        }
        this.dismiss = runnable;
        if (!this.drawBackground) {
            checkScroll();
            for (int i = 0; i < this.emojiGridView.getChildCount(); i++) {
                View childAt = this.emojiGridView.getChildAt(i);
                childAt.setScaleX(1.0f);
                childAt.setScaleY(1.0f);
            }
            return;
        }
        ValueAnimator valueAnimator = this.showAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.showAnimator = null;
        }
        ValueAnimator valueAnimator2 = this.hideAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
            this.hideAnimator = null;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.showAnimator = ofFloat;
        ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda0(this));
        this.showAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                SelectAnimatedEmojiDialog.this.checkScroll();
                SelectAnimatedEmojiDialog.this.updateShow(1.0f);
                for (int i = 0; i < SelectAnimatedEmojiDialog.this.emojiGridView.getChildCount(); i++) {
                    View childAt = SelectAnimatedEmojiDialog.this.emojiGridView.getChildAt(i);
                    childAt.setScaleX(1.0f);
                    childAt.setScaleY(1.0f);
                }
                for (int i2 = 0; i2 < SelectAnimatedEmojiDialog.this.emojiTabs.contentView.getChildCount(); i2++) {
                    View childAt2 = SelectAnimatedEmojiDialog.this.emojiTabs.contentView.getChildAt(i2);
                    childAt2.setScaleX(1.0f);
                    childAt2.setScaleY(1.0f);
                }
                SelectAnimatedEmojiDialog.this.emojiTabs.contentView.invalidate();
                SelectAnimatedEmojiDialog.this.emojiGridView.updateEmojiDrawables();
            }
        });
        updateShow(0.0f);
        this.showAnimator.setDuration(800);
        this.showAnimator.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onShow$17(ValueAnimator valueAnimator) {
        updateShow(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private class SearchBox extends FrameLayout {
        private FrameLayout box;
        /* access modifiers changed from: private */
        public ImageView clear;
        /* access modifiers changed from: private */
        public CloseProgressDrawable2 clearDrawable;
        /* access modifiers changed from: private */
        public EditTextCaption input;
        private ImageView search;

        public SearchBox(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground", SelectAnimatedEmojiDialog.this.resourcesProvider));
            FrameLayout frameLayout = new FrameLayout(context);
            this.box = frameLayout;
            frameLayout.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("chat_emojiPanelBackground", SelectAnimatedEmojiDialog.this.resourcesProvider)));
            if (Build.VERSION.SDK_INT >= 21) {
                this.box.setClipToOutline(true);
                this.box.setOutlineProvider(new ViewOutlineProvider(this, SelectAnimatedEmojiDialog.this) {
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (float) AndroidUtilities.dp(18.0f));
                    }
                });
            }
            addView(this.box, LayoutHelper.createFrame(-1, 36.0f, 55, 8.0f, 12.0f, 8.0f, 8.0f));
            ImageView imageView = new ImageView(context);
            this.search = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.search.setImageResource(R.drawable.smiles_inputsearch);
            this.search.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiSearchIcon", SelectAnimatedEmojiDialog.this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
            this.box.addView(this.search, LayoutHelper.createFrame(36, 36, 51));
            AnonymousClass2 r0 = new EditTextCaption(context, SelectAnimatedEmojiDialog.this.resourcesProvider, SelectAnimatedEmojiDialog.this) {
                /* access modifiers changed from: protected */
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    super.onTextChanged(charSequence, i, i2, i3);
                    SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = SelectAnimatedEmojiDialog.this;
                    String str = null;
                    if (!(charSequence == null || AndroidUtilities.trim(charSequence, (int[]) null).length() == 0)) {
                        str = charSequence.toString();
                    }
                    selectAnimatedEmojiDialog.search(str);
                }

                /* access modifiers changed from: protected */
                public void onFocusChanged(boolean z, int i, Rect rect) {
                    if (z) {
                        SelectAnimatedEmojiDialog.this.onInputFocus();
                        AndroidUtilities.runOnUIThread(new SelectAnimatedEmojiDialog$SearchBox$2$$ExternalSyntheticLambda0(this), 200);
                    }
                    super.onFocusChanged(z, i, rect);
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onFocusChanged$0() {
                    AndroidUtilities.showKeyboard(SearchBox.this.input);
                }
            };
            this.input = r0;
            r0.setBackground((Drawable) null);
            this.input.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
            this.input.setTextSize(1, 16.0f);
            this.input.setHint("Search emoji");
            this.input.setHintTextColor(Theme.getColor("chat_emojiSearchIcon", SelectAnimatedEmojiDialog.this.resourcesProvider));
            this.input.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", SelectAnimatedEmojiDialog.this.resourcesProvider));
            this.input.setImeOptions(NUM);
            this.input.setCursorColor(Theme.getColor("featuredStickers_addedIcon", SelectAnimatedEmojiDialog.this.resourcesProvider));
            this.input.setCursorSize(AndroidUtilities.dp(20.0f));
            this.input.setGravity(19);
            this.input.setCursorWidth(1.5f);
            this.input.setMaxLines(1);
            this.input.setLines(1);
            this.input.setSingleLine(true);
            this.box.addView(this.input, LayoutHelper.createFrame(-1, -1.0f, 119, 36.0f, -1.0f, 32.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clear = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clear;
            AnonymousClass3 r02 = new CloseProgressDrawable2(1.25f, SelectAnimatedEmojiDialog.this) {
                /* access modifiers changed from: protected */
                public int getCurrentColor() {
                    return Theme.getColor("chat_emojiSearchIcon", SelectAnimatedEmojiDialog.this.resourcesProvider);
                }
            };
            this.clearDrawable = r02;
            imageView3.setImageDrawable(r02);
            this.clearDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clear.setScaleX(0.1f);
            this.clear.setScaleY(0.1f);
            this.clear.setAlpha(0.0f);
            this.box.addView(this.clear, LayoutHelper.createFrame(36, 36, 53));
            this.clear.setOnClickListener(new SelectAnimatedEmojiDialog$SearchBox$$ExternalSyntheticLambda1(this));
            setOnClickListener(new SelectAnimatedEmojiDialog$SearchBox$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            this.input.setText("");
            SelectAnimatedEmojiDialog.this.search((String) null);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            SelectAnimatedEmojiDialog.this.onInputFocus();
            this.input.requestFocus();
            SelectAnimatedEmojiDialog.this.scrollToPosition(0, 0);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
        }
    }

    /* access modifiers changed from: private */
    public void updateShow(float f) {
        if (this.bubble1View != null) {
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(MathUtils.clamp((((f * 800.0f) - 0.0f) / 120.0f) / 1.0f, 0.0f, 1.0f));
            this.bubble1View.setAlpha(interpolation);
            this.bubble1View.setScaleX(interpolation);
            this.bubble1View.setScaleY(interpolation);
        }
        if (this.bubble2View != null) {
            float clamp = MathUtils.clamp((((f * 800.0f) - 30.0f) / 120.0f) / 1.0f, 0.0f, 1.0f);
            this.bubble2View.setAlpha(clamp);
            this.bubble2View.setScaleX(clamp);
            this.bubble2View.setScaleY(clamp);
        }
        float f2 = f * 800.0f;
        float f3 = f2 - 40.0f;
        float clamp2 = MathUtils.clamp(f3 / 700.0f, 0.0f, 1.0f);
        float clamp3 = MathUtils.clamp((f2 - 80.0f) / 700.0f, 0.0f, 1.0f);
        float clamp4 = MathUtils.clamp(f3 / 750.0f, 0.0f, 1.0f);
        float clamp5 = MathUtils.clamp((f2 - 30.0f) / 120.0f, 0.0f, 1.0f);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        float interpolation2 = cubicBezierInterpolator.getInterpolation(clamp2);
        float interpolation3 = cubicBezierInterpolator.getInterpolation(clamp3);
        this.contentView.setAlpha(clamp5);
        if (this.scrimDrawable != null) {
            invalidate();
        }
        float f4 = 1.0f - clamp5;
        this.contentView.setTranslationY(((float) AndroidUtilities.dp(-5.0f)) * f4);
        View view = this.bubble2View;
        if (view != null) {
            view.setTranslationY(((float) AndroidUtilities.dp(-5.0f)) * f4);
        }
        this.scaleX = (interpolation2 * 0.85f) + 0.15f;
        this.scaleY = (interpolation3 * 0.925f) + 0.075f;
        View view2 = this.bubble2View;
        if (view2 != null) {
            view2.setAlpha(clamp5);
        }
        this.contentView.invalidate();
        this.emojiTabsShadow.setAlpha(clamp5);
        this.emojiTabsShadow.setScaleX(Math.min(this.scaleX, 1.0f));
        float pivotX = this.emojiTabsShadow.getPivotX();
        double d = (double) (pivotX * pivotX);
        double pow = Math.pow((double) this.contentView.getHeight(), 2.0d);
        Double.isNaN(d);
        float sqrt = (float) Math.sqrt(Math.max(d + pow, Math.pow((double) (((float) this.contentView.getWidth()) - pivotX), 2.0d) + Math.pow((double) this.contentView.getHeight(), 2.0d)));
        for (int i = 0; i < this.emojiTabs.contentView.getChildCount(); i++) {
            View childAt = this.emojiTabs.contentView.getChildAt(i);
            float left = ((float) childAt.getLeft()) + (((float) childAt.getWidth()) / 2.0f);
            float top = ((float) childAt.getTop()) + (((float) childAt.getHeight()) / 2.0f);
            float f5 = left - pivotX;
            float cascade = AndroidUtilities.cascade(clamp4, (float) Math.sqrt((double) ((f5 * f5) + (top * top * 0.4f))), sqrt, ((float) childAt.getHeight()) * 1.75f);
            if (Float.isNaN(cascade)) {
                cascade = 0.0f;
            }
            childAt.setScaleX(cascade);
            childAt.setScaleY(cascade);
        }
        this.emojiTabs.contentView.invalidate();
        for (int i2 = 0; i2 < this.emojiGridView.getChildCount(); i2++) {
            View childAt2 = this.emojiGridView.getChildAt(i2);
            float left2 = ((float) childAt2.getLeft()) + (((float) childAt2.getWidth()) / 2.0f);
            float top2 = ((float) childAt2.getTop()) + (((float) childAt2.getHeight()) / 2.0f);
            float f6 = left2 - pivotX;
            float cascade2 = AndroidUtilities.cascade(clamp4, (float) Math.sqrt((double) ((f6 * f6) + (top2 * top2 * 0.2f))), sqrt, ((float) childAt2.getHeight()) * 1.75f);
            if (Float.isNaN(cascade2)) {
                cascade2 = 0.0f;
            }
            childAt2.setScaleX(cascade2);
            childAt2.setScaleY(cascade2);
        }
        this.emojiGridView.invalidate();
    }

    public void onDismiss(final Runnable runnable) {
        Integer num = this.listStateId;
        if (num != null) {
            listStates.put(num, this.layoutManager.onSaveInstanceState());
        }
        ValueAnimator valueAnimator = this.hideAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.hideAnimator = null;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.hideAnimator = ofFloat;
        ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda2(this));
        this.hideAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                runnable.run();
                if (SelectAnimatedEmojiDialog.this.selectStatusDateDialog != null) {
                    SelectAnimatedEmojiDialog.this.selectStatusDateDialog.dismiss();
                    SelectStatusDurationDialog unused = SelectAnimatedEmojiDialog.this.selectStatusDateDialog = null;
                }
            }
        });
        this.hideAnimator.setDuration(200);
        this.hideAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.hideAnimator.start();
        SearchBox searchBox2 = this.searchBox;
        if (searchBox2 != null) {
            AndroidUtilities.hideKeyboard(searchBox2.input);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDismiss$18(ValueAnimator valueAnimator) {
        float floatValue = 1.0f - ((Float) valueAnimator.getAnimatedValue()).floatValue();
        setTranslationY(((float) AndroidUtilities.dp(8.0f)) * (1.0f - floatValue));
        View view = this.bubble1View;
        if (view != null) {
            view.setAlpha(floatValue);
        }
        View view2 = this.bubble2View;
        if (view2 != null) {
            view2.setAlpha(floatValue * floatValue);
        }
        this.contentView.setAlpha(floatValue);
        this.contentView.invalidate();
        invalidate();
    }

    public void setDrawBackground(boolean z) {
        this.drawBackground = z;
    }

    public void setRecentReactions(List<ReactionsLayoutInBubble.VisibleReaction> list) {
        this.recentReactionsToSet = list;
        updateRows(true);
    }

    public void resetBackgroundBitmaps() {
        for (int i = 0; i < this.emojiGridView.lineDrawables.size(); i++) {
            EmojiListView.DrawingInBackgroundLine drawingInBackgroundLine = this.emojiGridView.lineDrawables.get(i);
            for (int i2 = 0; i2 < drawingInBackgroundLine.imageViewEmojis.size(); i2++) {
                if (drawingInBackgroundLine.imageViewEmojis.get(i2).notDraw) {
                    drawingInBackgroundLine.imageViewEmojis.get(i2).notDraw = false;
                    drawingInBackgroundLine.imageViewEmojis.get(i2).invalidate();
                    drawingInBackgroundLine.reset();
                }
            }
        }
        this.emojiGridView.invalidate();
        for (int i3 = 0; i3 < this.emojiSearchGridView.lineDrawables.size(); i3++) {
            EmojiListView.DrawingInBackgroundLine drawingInBackgroundLine2 = this.emojiSearchGridView.lineDrawables.get(i3);
            for (int i4 = 0; i4 < drawingInBackgroundLine2.imageViewEmojis.size(); i4++) {
                if (drawingInBackgroundLine2.imageViewEmojis.get(i4).notDraw) {
                    drawingInBackgroundLine2.imageViewEmojis.get(i4).notDraw = false;
                    drawingInBackgroundLine2.imageViewEmojis.get(i4).invalidate();
                    drawingInBackgroundLine2.reset();
                }
            }
        }
        this.emojiSearchGridView.invalidate();
    }

    public void setSelected(Long l) {
        this.selectedDocumentIds.clear();
        this.selectedDocumentIds.add(l);
    }

    public void setScrimDrawable(AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable, View view) {
        this.scrimColor = swapAnimatedEmojiDrawable == null ? 0 : swapAnimatedEmojiDrawable.getColor().intValue();
        this.scrimDrawable = swapAnimatedEmojiDrawable;
        this.scrimDrawableParent = view;
        if (swapAnimatedEmojiDrawable != null) {
            swapAnimatedEmojiDrawable.addParentView(this);
        }
        invalidate();
    }

    public void drawBigReaction(Canvas canvas, View view) {
        if (this.selectedReactionView != null) {
            this.bigReactionImageReceiver.setParentView(view);
            ImageViewEmoji imageViewEmoji = this.selectedReactionView;
            if (imageViewEmoji != null) {
                float f = this.pressedProgress;
                if (f != 1.0f && !this.cancelPressed) {
                    float f2 = f + 0.010666667f;
                    this.pressedProgress = f2;
                    if (f2 >= 1.0f) {
                        this.pressedProgress = 1.0f;
                        onLongPressedListener onlongpressedlistener = this.bigReactionListener;
                        if (onlongpressedlistener != null) {
                            onlongpressedlistener.onLongPressed(imageViewEmoji);
                        }
                    }
                    this.selectedReactionView.bigReactionSelectedProgress = this.pressedProgress;
                }
                float f3 = (this.pressedProgress * 2.0f) + 1.0f;
                canvas.save();
                canvas.translate(this.emojiGridView.getX() + this.selectedReactionView.getX(), this.emojiGridView.getY() + this.selectedReactionView.getY());
                this.paint.setColor(Theme.getColor("actionBarDefaultSubmenuBackground", this.resourcesProvider));
                canvas.drawRect(0.0f, 0.0f, (float) this.selectedReactionView.getMeasuredWidth(), (float) this.selectedReactionView.getMeasuredHeight(), this.paint);
                canvas.scale(f3, f3, ((float) this.selectedReactionView.getMeasuredWidth()) / 2.0f, (float) this.selectedReactionView.getMeasuredHeight());
                ImageViewEmoji imageViewEmoji2 = this.selectedReactionView;
                ImageReceiver imageReceiver = imageViewEmoji2.isDefaultReaction ? this.bigReactionImageReceiver : imageViewEmoji2.imageReceiverToDraw;
                AnimatedEmojiDrawable animatedEmojiDrawable = this.bigReactionAnimatedEmoji;
                if (!(animatedEmojiDrawable == null || animatedEmojiDrawable.getImageReceiver() == null || !this.bigReactionAnimatedEmoji.getImageReceiver().hasBitmapImage())) {
                    imageReceiver = this.bigReactionAnimatedEmoji.getImageReceiver();
                }
                if (imageReceiver != null) {
                    imageReceiver.setImageCoords(0.0f, 0.0f, (float) this.selectedReactionView.getMeasuredWidth(), (float) this.selectedReactionView.getMeasuredHeight());
                    imageReceiver.draw(canvas);
                }
                canvas.restore();
                view.invalidate();
            }
        }
    }

    public void setSaveState(int i) {
        this.listStateId = Integer.valueOf(i);
    }

    public void setOnLongPressedListener(onLongPressedListener onlongpressedlistener) {
        this.bigReactionListener = onlongpressedlistener;
    }

    public void setOnRecentClearedListener(onRecentClearedListener onrecentclearedlistener) {
        this.onRecentClearedListener = onrecentclearedlistener;
    }

    private class SelectStatusDurationDialog extends Dialog {
        /* access modifiers changed from: private */
        public Bitmap blurBitmap;
        /* access modifiers changed from: private */
        public Paint blurBitmapPaint;
        /* access modifiers changed from: private */
        public boolean changeToScrimColor;
        /* access modifiers changed from: private */
        public int clipBottom;
        /* access modifiers changed from: private */
        public ContentView contentView;
        /* access modifiers changed from: private */
        public Rect current = new Rect();
        private BottomSheet dateBottomSheet;
        private boolean dismissed = false;
        private View emojiPreviewView;
        /* access modifiers changed from: private */
        public Rect from = new Rect();
        /* access modifiers changed from: private */
        public ImageReceiver imageReceiver;
        /* access modifiers changed from: private */
        public ImageViewEmoji imageViewEmoji;
        /* access modifiers changed from: private */
        public WindowInsets lastInsets;
        private LinearLayout linearLayoutView;
        /* access modifiers changed from: private */
        public ActionBarPopupWindow.ActionBarPopupWindowLayout menuView;
        private Runnable parentDialogDismiss;
        private View parentDialogView;
        /* access modifiers changed from: private */
        public int parentDialogX;
        /* access modifiers changed from: private */
        public int parentDialogY;
        /* access modifiers changed from: private */
        public Theme.ResourcesProvider resourcesProvider;
        /* access modifiers changed from: private */
        public ValueAnimator showAnimator;
        /* access modifiers changed from: private */
        public ValueAnimator showMenuAnimator;
        /* access modifiers changed from: private */
        public float showMenuT;
        /* access modifiers changed from: private */
        public float showT;
        private boolean showing;
        private boolean showingMenu;
        /* access modifiers changed from: private */
        public int[] tempLocation = new int[2];
        final /* synthetic */ SelectAnimatedEmojiDialog this$0;
        /* access modifiers changed from: private */
        public Rect to = new Rect();

        /* access modifiers changed from: protected */
        public boolean getOutBounds(Rect rect) {
            throw null;
        }

        /* access modifiers changed from: protected */
        public void onEnd(Integer num) {
            throw null;
        }

        /* access modifiers changed from: protected */
        public void onEndPartly(Integer num) {
            throw null;
        }

        private class ContentView extends FrameLayout {
            public ContentView(Context context) {
                super(context);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), NUM));
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                float f;
                if (!(SelectStatusDurationDialog.this.blurBitmap == null || SelectStatusDurationDialog.this.blurBitmapPaint == null)) {
                    canvas.save();
                    canvas.scale(12.0f, 12.0f);
                    SelectStatusDurationDialog.this.blurBitmapPaint.setAlpha((int) (SelectStatusDurationDialog.this.showT * 255.0f));
                    canvas.drawBitmap(SelectStatusDurationDialog.this.blurBitmap, 0.0f, 0.0f, SelectStatusDurationDialog.this.blurBitmapPaint);
                    canvas.restore();
                }
                super.dispatchDraw(canvas);
                if (SelectStatusDurationDialog.this.imageViewEmoji != null) {
                    Drawable drawable = SelectStatusDurationDialog.this.imageViewEmoji.drawable;
                    if (drawable != null) {
                        if (SelectStatusDurationDialog.this.changeToScrimColor) {
                            drawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(SelectStatusDurationDialog.this.this$0.scrimColor, Theme.getColor("windowBackgroundWhiteBlueIcon", SelectStatusDurationDialog.this.resourcesProvider), SelectStatusDurationDialog.this.showT), PorterDuff.Mode.MULTIPLY));
                        } else {
                            drawable.setColorFilter(SelectStatusDurationDialog.this.this$0.premiumStarColorFilter);
                        }
                        drawable.setAlpha((int) ((1.0f - SelectStatusDurationDialog.this.showT) * 255.0f));
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set(SelectStatusDurationDialog.this.current);
                        if (SelectStatusDurationDialog.this.imageViewEmoji.pressedProgress != 0.0f || SelectStatusDurationDialog.this.imageViewEmoji.selected) {
                            f = (((1.0f - (SelectStatusDurationDialog.this.imageViewEmoji.selected ? 0.7f : SelectStatusDurationDialog.this.imageViewEmoji.pressedProgress)) * 0.2f) + 0.8f) * 1.0f;
                        } else {
                            f = 1.0f;
                        }
                        Rect rect = AndroidUtilities.rectTmp2;
                        rect.set((int) (rectF.centerX() - ((rectF.width() / 2.0f) * f)), (int) (rectF.centerY() - ((rectF.height() / 2.0f) * f)), (int) (rectF.centerX() + ((rectF.width() / 2.0f) * f)), (int) (rectF.centerY() + ((rectF.height() / 2.0f) * f)));
                        float access$7200 = 1.0f - ((1.0f - SelectStatusDurationDialog.this.imageViewEmoji.skewAlpha) * (1.0f - SelectStatusDurationDialog.this.showT));
                        canvas.save();
                        if (access$7200 < 1.0f) {
                            canvas.translate((float) rect.left, (float) rect.top);
                            canvas.scale(1.0f, access$7200, 0.0f, 0.0f);
                            canvas.skew((1.0f - ((((float) SelectStatusDurationDialog.this.imageViewEmoji.skewIndex) * 2.0f) / ((float) SelectStatusDurationDialog.this.this$0.layoutManager.getSpanCount()))) * (1.0f - access$7200), 0.0f);
                            canvas.translate((float) (-rect.left), (float) (-rect.top));
                        }
                        canvas.clipRect(0.0f, 0.0f, (float) getWidth(), ((float) SelectStatusDurationDialog.this.clipBottom) + (SelectStatusDurationDialog.this.showT * ((float) AndroidUtilities.dp(45.0f))));
                        drawable.setBounds(rect);
                        drawable.draw(canvas);
                        canvas.restore();
                        if (SelectStatusDurationDialog.this.imageViewEmoji.skewIndex == 0) {
                            rect.offset(AndroidUtilities.dp(access$7200 * 8.0f), 0);
                        } else if (SelectStatusDurationDialog.this.imageViewEmoji.skewIndex == 1) {
                            rect.offset(AndroidUtilities.dp(access$7200 * 4.0f), 0);
                        } else if (SelectStatusDurationDialog.this.imageViewEmoji.skewIndex == SelectStatusDurationDialog.this.this$0.layoutManager.getSpanCount() - 2) {
                            rect.offset(-AndroidUtilities.dp(access$7200 * -4.0f), 0);
                        } else if (SelectStatusDurationDialog.this.imageViewEmoji.skewIndex == SelectStatusDurationDialog.this.this$0.layoutManager.getSpanCount() - 1) {
                            rect.offset(AndroidUtilities.dp(access$7200 * -8.0f), 0);
                        }
                        canvas.saveLayerAlpha((float) rect.left, (float) rect.top, (float) rect.right, (float) rect.bottom, (int) ((1.0f - SelectStatusDurationDialog.this.showT) * 255.0f), 31);
                        canvas.clipRect(rect);
                        canvas.translate((float) ((int) (SelectStatusDurationDialog.this.this$0.bottomGradientView.getX() + SelectStatusDurationDialog.this.this$0.contentView.getX() + ((float) SelectStatusDurationDialog.this.parentDialogX))), ((float) ((int) SelectStatusDurationDialog.this.this$0.bottomGradientView.getY())) + SelectStatusDurationDialog.this.this$0.contentView.getY() + ((float) SelectStatusDurationDialog.this.parentDialogY));
                        SelectStatusDurationDialog.this.this$0.bottomGradientView.draw(canvas);
                        canvas.restore();
                    } else if (SelectStatusDurationDialog.this.imageViewEmoji.isDefaultReaction && SelectStatusDurationDialog.this.imageViewEmoji.imageReceiver != null) {
                        SelectStatusDurationDialog.this.imageViewEmoji.imageReceiver.setAlpha(1.0f - SelectStatusDurationDialog.this.showT);
                        SelectStatusDurationDialog.this.imageViewEmoji.imageReceiver.setImageCoords(SelectStatusDurationDialog.this.current);
                        SelectStatusDurationDialog.this.imageViewEmoji.imageReceiver.draw(canvas);
                    }
                }
                if (SelectStatusDurationDialog.this.imageReceiver != null) {
                    SelectStatusDurationDialog.this.imageReceiver.setAlpha(SelectStatusDurationDialog.this.showT);
                    SelectStatusDurationDialog.this.imageReceiver.setImageCoords(SelectStatusDurationDialog.this.current);
                    SelectStatusDurationDialog.this.imageReceiver.draw(canvas);
                }
            }

            /* access modifiers changed from: protected */
            public void onConfigurationChanged(Configuration configuration) {
                WindowInsets unused = SelectStatusDurationDialog.this.lastInsets = null;
            }

            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                if (SelectStatusDurationDialog.this.imageReceiver != null) {
                    SelectStatusDurationDialog.this.imageReceiver.onAttachedToWindow();
                }
            }

            /* access modifiers changed from: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                if (SelectStatusDurationDialog.this.imageReceiver != null) {
                    SelectStatusDurationDialog.this.imageReceiver.onDetachedFromWindow();
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                Activity access$8500 = SelectStatusDurationDialog.this.getParentActivity();
                if (access$8500 != null) {
                    View decorView = access$8500.getWindow().getDecorView();
                    if (SelectStatusDurationDialog.this.blurBitmap == null || SelectStatusDurationDialog.this.blurBitmap.getWidth() != decorView.getMeasuredWidth() || SelectStatusDurationDialog.this.blurBitmap.getHeight() != decorView.getMeasuredHeight()) {
                        SelectStatusDurationDialog.this.prepareBlurBitmap();
                    }
                }
            }
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public SelectStatusDurationDialog(org.telegram.ui.SelectAnimatedEmojiDialog r30, android.content.Context r31, java.lang.Runnable r32, android.view.View r33, org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji r34, org.telegram.ui.ActionBar.Theme.ResourcesProvider r35) {
            /*
                r29 = this;
                r0 = r29
                r1 = r30
                r2 = r31
                r3 = r33
                r4 = r34
                r12 = r35
                r0.this$0 = r1
                r0.<init>(r2)
                android.graphics.Rect r5 = new android.graphics.Rect
                r5.<init>()
                r0.from = r5
                android.graphics.Rect r5 = new android.graphics.Rect
                r5.<init>()
                r0.to = r5
                android.graphics.Rect r5 = new android.graphics.Rect
                r5.<init>()
                r0.current = r5
                r5 = 2
                int[] r5 = new int[r5]
                r0.tempLocation = r5
                r13 = 0
                r0.dismissed = r13
                r0.imageViewEmoji = r4
                r0.resourcesProvider = r12
                r5 = r32
                r0.parentDialogDismiss = r5
                r0.parentDialogView = r3
                org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$ContentView r5 = new org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$ContentView
                r5.<init>(r2)
                r0.contentView = r5
                android.view.ViewGroup$LayoutParams r6 = new android.view.ViewGroup$LayoutParams
                r14 = -1
                r6.<init>(r14, r14)
                r0.setContentView(r5, r6)
                android.widget.LinearLayout r5 = new android.widget.LinearLayout
                r5.<init>(r2)
                r0.linearLayoutView = r5
                r15 = 1
                r5.setOrientation(r15)
                org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$1 r5 = new org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$1
                r5.<init>(r2, r1)
                r0.emojiPreviewView = r5
                android.widget.LinearLayout r6 = r0.linearLayoutView
                r16 = 160(0xa0, float:2.24E-43)
                r17 = 160(0xa0, float:2.24E-43)
                r18 = 17
                r19 = 0
                r20 = 0
                r21 = 0
                r22 = 16
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22)
                r6.addView(r5, r7)
                org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r5 = new org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout
                int r6 = org.telegram.messenger.R.drawable.popup_fixed_alert2
                r5.<init>(r2, r6, r12)
                r0.menuView = r5
                android.widget.LinearLayout r6 = r0.linearLayoutView
                r16 = -2
                r17 = -2
                r22 = 0
                android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22)
                r6.addView(r5, r7)
                org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r7 = r0.menuView
                int r5 = org.telegram.messenger.R.string.SetEmojiStatusUntil1Hour
                java.lang.String r6 = "SetEmojiStatusUntil1Hour"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r5 = 1
                r6 = 0
                r8 = 0
                r10 = 0
                r11 = r35
                org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = org.telegram.ui.ActionBar.ActionBarMenuItem.addItem(r5, r6, r7, r8, r9, r10, r11)
                org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda5 r6 = new org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda5
                r6.<init>(r0)
                r5.setOnClickListener(r6)
                org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r7 = r0.menuView
                int r5 = org.telegram.messenger.R.string.SetEmojiStatusUntil2Hours
                java.lang.String r6 = "SetEmojiStatusUntil2Hours"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r5 = 0
                r6 = 0
                org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = org.telegram.ui.ActionBar.ActionBarMenuItem.addItem(r5, r6, r7, r8, r9, r10, r11)
                org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda7 r6 = new org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda7
                r6.<init>(r0)
                r5.setOnClickListener(r6)
                org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r7 = r0.menuView
                int r5 = org.telegram.messenger.R.string.SetEmojiStatusUntil8Hours
                java.lang.String r6 = "SetEmojiStatusUntil8Hours"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r5 = 0
                r6 = 0
                org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = org.telegram.ui.ActionBar.ActionBarMenuItem.addItem(r5, r6, r7, r8, r9, r10, r11)
                org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda6 r6 = new org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda6
                r6.<init>(r0)
                r5.setOnClickListener(r6)
                org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r7 = r0.menuView
                int r5 = org.telegram.messenger.R.string.SetEmojiStatusUntil2Days
                java.lang.String r6 = "SetEmojiStatusUntil2Days"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r5 = 0
                r6 = 0
                org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = org.telegram.ui.ActionBar.ActionBarMenuItem.addItem(r5, r6, r7, r8, r9, r10, r11)
                org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda4 r6 = new org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda4
                r6.<init>(r0)
                r5.setOnClickListener(r6)
                org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r7 = r0.menuView
                int r5 = org.telegram.messenger.R.string.SetEmojiStatusUntilOther
                java.lang.String r6 = "SetEmojiStatusUntilOther"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r5 = 0
                r6 = 1
                org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = org.telegram.ui.ActionBar.ActionBarMenuItem.addItem(r5, r6, r7, r8, r9, r10, r11)
                org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda8 r6 = new org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda8
                r6.<init>(r0, r2)
                r5.setOnClickListener(r6)
                org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$ContentView r2 = r0.contentView
                android.widget.LinearLayout r5 = r0.linearLayoutView
                r6 = -2
                r7 = 17
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r7)
                r2.addView(r5, r6)
                android.view.Window r2 = r29.getWindow()
                if (r2 == 0) goto L_0x016d
                int r5 = org.telegram.messenger.R.style.DialogNoAnimation
                r2.setWindowAnimations(r5)
                r5 = 0
                r2.setBackgroundDrawable(r5)
                android.view.WindowManager$LayoutParams r5 = r2.getAttributes()
                r5.width = r14
                r6 = 51
                r5.gravity = r6
                r6 = 0
                r5.dimAmount = r6
                int r6 = r5.flags
                r6 = r6 & -3
                r5.flags = r6
                r7 = 131072(0x20000, float:1.83671E-40)
                r6 = r6 | r7
                r5.flags = r6
                int r7 = android.os.Build.VERSION.SDK_INT
                r8 = 21
                if (r7 < r8) goto L_0x0150
                r8 = -2147417856(0xfffffffvar_, float:-9.2194E-41)
                r6 = r6 | r8
                r5.flags = r6
                org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$ContentView r6 = r0.contentView
                org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda3 r8 = new org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda3
                r8.<init>(r0)
                r6.setOnApplyWindowInsetsListener(r8)
            L_0x0150:
                int r6 = r5.flags
                r6 = r6 | 1024(0x400, float:1.435E-42)
                r5.flags = r6
                org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$ContentView r6 = r0.contentView
                r6.setFitsSystemWindows(r15)
                org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$ContentView r6 = r0.contentView
                r8 = 1284(0x504, float:1.799E-42)
                r6.setSystemUiVisibility(r8)
                r5.height = r14
                r6 = 28
                if (r7 < r6) goto L_0x016a
                r5.layoutInDisplayCutoutMode = r15
            L_0x016a:
                r2.setAttributes(r5)
            L_0x016d:
                if (r4 == 0) goto L_0x0171
                r4.notDraw = r15
            L_0x0171:
                r29.prepareBlurBitmap()
                org.telegram.messenger.ImageReceiver r2 = new org.telegram.messenger.ImageReceiver
                r2.<init>()
                r0.imageReceiver = r2
                org.telegram.ui.SelectAnimatedEmojiDialog$SelectStatusDurationDialog$ContentView r5 = r0.contentView
                r2.setParentView(r5)
                org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
                r5 = 7
                r2.setLayerNum(r5)
                org.telegram.tgnet.TLRPC$Document r2 = r4.document
                if (r2 != 0) goto L_0x0196
                android.graphics.drawable.Drawable r5 = r4.drawable
                boolean r6 = r5 instanceof org.telegram.ui.Components.AnimatedEmojiDrawable
                if (r6 == 0) goto L_0x0196
                org.telegram.ui.Components.AnimatedEmojiDrawable r5 = (org.telegram.ui.Components.AnimatedEmojiDrawable) r5
                org.telegram.tgnet.TLRPC$Document r2 = r5.getDocument()
            L_0x0196:
                if (r2 == 0) goto L_0x0224
                java.lang.String r5 = "160_160"
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r2.thumbs
                r7 = 1045220557(0x3e4ccccd, float:0.2)
                java.lang.String r8 = "windowBackgroundWhiteGrayIcon"
                org.telegram.messenger.SvgHelper$SvgDrawable r6 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) r6, (java.lang.String) r8, (float) r7)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r2.thumbs
                r8 = 90
                org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
                java.lang.String r8 = r2.mime_type
                java.lang.String r9 = "video/webm"
                boolean r8 = r9.equals(r8)
                r9 = 512(0x200, float:7.175E-43)
                if (r8 == 0) goto L_0x01dd
                org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForDocument(r2)
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                r10.append(r5)
                java.lang.String r11 = "_"
                r10.append(r11)
                java.lang.String r11 = "g"
                r10.append(r11)
                java.lang.String r10 = r10.toString()
                if (r6 == 0) goto L_0x01d8
                r6.overrideWidthAndHeight(r9, r9)
            L_0x01d8:
                r17 = r8
                r18 = r10
                goto L_0x01f0
            L_0x01dd:
                if (r6 == 0) goto L_0x01e8
                boolean r8 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r2, r13)
                if (r8 == 0) goto L_0x01e8
                r6.overrideWidthAndHeight(r9, r9)
            L_0x01e8:
                org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForDocument(r2)
                r18 = r5
                r17 = r8
            L_0x01f0:
                org.telegram.messenger.ImageReceiver r8 = r0.imageReceiver
                org.telegram.messenger.ImageLocation r19 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r7, (org.telegram.tgnet.TLRPC$Document) r2)
                r21 = 0
                r22 = 0
                long r9 = r2.size
                r26 = 0
                r28 = 1
                r16 = r8
                r20 = r5
                r23 = r6
                r24 = r9
                r27 = r2
                r16.setImage(r17, r18, r19, r20, r21, r22, r23, r24, r26, r27, r28)
                android.graphics.drawable.Drawable r2 = r4.drawable
                boolean r5 = r2 instanceof org.telegram.ui.Components.AnimatedEmojiDrawable
                if (r5 == 0) goto L_0x0224
                org.telegram.ui.Components.AnimatedEmojiDrawable r2 = (org.telegram.ui.Components.AnimatedEmojiDrawable) r2
                boolean r2 = r2.canOverrideColor()
                if (r2 == 0) goto L_0x0224
                org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
                android.graphics.ColorFilter r1 = r30.premiumStarColorFilter
                r2.setColorFilter(r1)
            L_0x0224:
                int[] r1 = r0.tempLocation
                r4.getLocationOnScreen(r1)
                android.graphics.Rect r1 = r0.from
                int[] r2 = r0.tempLocation
                r2 = r2[r13]
                int r5 = r34.getPaddingLeft()
                int r2 = r2 + r5
                r1.left = r2
                android.graphics.Rect r1 = r0.from
                int[] r2 = r0.tempLocation
                r2 = r2[r15]
                int r5 = r34.getPaddingTop()
                int r2 = r2 + r5
                r1.top = r2
                android.graphics.Rect r1 = r0.from
                int[] r2 = r0.tempLocation
                r2 = r2[r13]
                int r5 = r34.getWidth()
                int r2 = r2 + r5
                int r5 = r34.getPaddingRight()
                int r2 = r2 - r5
                r1.right = r2
                android.graphics.Rect r1 = r0.from
                int[] r2 = r0.tempLocation
                r2 = r2[r15]
                int r5 = r34.getHeight()
                int r2 = r2 + r5
                int r4 = r34.getPaddingBottom()
                int r2 = r2 - r4
                r1.bottom = r2
                android.graphics.Rect r1 = r0.from
                android.graphics.Rect r2 = r0.to
                float r4 = r0.showT
                android.graphics.Rect r5 = r0.current
                org.telegram.messenger.AndroidUtilities.lerp((android.graphics.Rect) r1, (android.graphics.Rect) r2, (float) r4, (android.graphics.Rect) r5)
                int[] r1 = r0.tempLocation
                r3.getLocationOnScreen(r1)
                int[] r1 = r0.tempLocation
                r2 = r1[r13]
                r0.parentDialogX = r2
                r1 = r1[r15]
                r0.parentDialogY = r1
                int r2 = r33.getHeight()
                int r1 = r1 + r2
                r0.clipBottom = r1
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SelectAnimatedEmojiDialog.SelectStatusDurationDialog.<init>(org.telegram.ui.SelectAnimatedEmojiDialog, android.content.Context, java.lang.Runnable, android.view.View, org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            done(Integer.valueOf((int) ((System.currentTimeMillis() / 1000) + 3600)));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            done(Integer.valueOf((int) ((System.currentTimeMillis() / 1000) + 7200)));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(View view) {
            done(Integer.valueOf((int) ((System.currentTimeMillis() / 1000) + 28800)));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(View view) {
            done(Integer.valueOf((int) ((System.currentTimeMillis() / 1000) + 172800)));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6(Context context, View view) {
            if (this.dateBottomSheet == null) {
                boolean[] zArr = new boolean[1];
                BottomSheet.Builder createStatusUntilDatePickerDialog = AlertsCreator.createStatusUntilDatePickerDialog(context, System.currentTimeMillis() / 1000, new SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda11(this, zArr));
                createStatusUntilDatePickerDialog.setOnPreDismissListener(new SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda2(this, zArr));
                this.dateBottomSheet = createStatusUntilDatePickerDialog.show();
                animateMenuShow(false, (Runnable) null);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(boolean[] zArr, int i) {
            zArr[0] = true;
            done(Integer.valueOf(i));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5(boolean[] zArr, DialogInterface dialogInterface) {
            if (!zArr[0]) {
                animateMenuShow(true, (Runnable) null);
            }
            this.dateBottomSheet = null;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ WindowInsets lambda$new$7(View view, WindowInsets windowInsets) {
            view.requestLayout();
            return Build.VERSION.SDK_INT >= 30 ? WindowInsets.CONSUMED : windowInsets.consumeSystemWindowInsets();
        }

        private void done(Integer num) {
            Runnable runnable;
            boolean z = num != null && getOutBounds(this.from);
            this.changeToScrimColor = z;
            if (z) {
                this.parentDialogView.getLocationOnScreen(this.tempLocation);
                Rect rect = this.from;
                int[] iArr = this.tempLocation;
                rect.offset(iArr[0], iArr[1]);
            } else {
                this.imageViewEmoji.getLocationOnScreen(this.tempLocation);
                this.from.left = this.tempLocation[0] + this.imageViewEmoji.getPaddingLeft();
                this.from.top = this.tempLocation[1] + this.imageViewEmoji.getPaddingTop();
                this.from.right = (this.tempLocation[0] + this.imageViewEmoji.getWidth()) - this.imageViewEmoji.getPaddingRight();
                this.from.bottom = (this.tempLocation[1] + this.imageViewEmoji.getHeight()) - this.imageViewEmoji.getPaddingBottom();
            }
            if (!(num == null || (runnable = this.parentDialogDismiss) == null)) {
                runnable.run();
            }
            animateShow(false, new SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda10(this, num), new SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda9(this, num), !z);
            animateMenuShow(false, (Runnable) null);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$done$8(Integer num) {
            onEnd(num);
            super.dismiss();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$done$9(Integer num) {
            if (num != null) {
                try {
                    this.this$0.performHapticFeedback(0, 1);
                } catch (Exception unused) {
                }
                onEndPartly(num);
            }
        }

        /* access modifiers changed from: private */
        public Activity getParentActivity() {
            for (Context context = getContext(); context instanceof ContextWrapper; context = ((ContextWrapper) context).getBaseContext()) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
            }
            return null;
        }

        /* access modifiers changed from: private */
        public void prepareBlurBitmap() {
            Activity parentActivity = getParentActivity();
            if (parentActivity != null) {
                View decorView = parentActivity.getWindow().getDecorView();
                int measuredWidth = (int) (((float) decorView.getMeasuredWidth()) / 12.0f);
                int measuredHeight = (int) (((float) decorView.getMeasuredHeight()) / 12.0f);
                Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                canvas.scale(0.083333336f, 0.083333336f);
                canvas.drawColor(Theme.getColor("windowBackgroundWhite"));
                decorView.draw(canvas);
                if (parentActivity instanceof LaunchActivity) {
                    LaunchActivity launchActivity = (LaunchActivity) parentActivity;
                    if (launchActivity.getActionBarLayout().getLastFragment().getVisibleDialog() != null) {
                        launchActivity.getActionBarLayout().getLastFragment().getVisibleDialog().getWindow().getDecorView().draw(canvas);
                    }
                }
                View view = this.parentDialogView;
                if (view != null) {
                    view.getLocationOnScreen(this.tempLocation);
                    canvas.save();
                    int[] iArr = this.tempLocation;
                    canvas.translate((float) iArr[0], (float) iArr[1]);
                    this.parentDialogView.draw(canvas);
                    canvas.restore();
                }
                Utilities.stackBlurBitmap(createBitmap, Math.max(10, Math.max(measuredWidth, measuredHeight) / 180));
                this.blurBitmapPaint = new Paint(1);
                this.blurBitmap = createBitmap;
            }
        }

        private void animateShow(boolean z, Runnable runnable, Runnable runnable2, boolean z2) {
            if (this.imageViewEmoji != null) {
                ValueAnimator valueAnimator = this.showAnimator;
                if (valueAnimator != null) {
                    if (this.showing != z) {
                        valueAnimator.cancel();
                    } else {
                        return;
                    }
                }
                this.showing = z;
                if (z) {
                    this.imageViewEmoji.notDraw = true;
                }
                boolean[] zArr = new boolean[1];
                float[] fArr = new float[2];
                fArr[0] = this.showT;
                fArr[1] = z ? 1.0f : 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.showAnimator = ofFloat;
                final boolean z3 = z;
                ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda1(this, z3, z2, runnable2, zArr));
                final Runnable runnable3 = runnable2;
                final boolean[] zArr2 = zArr;
                final boolean z4 = z2;
                final Runnable runnable4 = runnable;
                this.showAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        Runnable runnable;
                        float unused = SelectStatusDurationDialog.this.showT = z3 ? 1.0f : 0.0f;
                        AndroidUtilities.lerp(SelectStatusDurationDialog.this.from, SelectStatusDurationDialog.this.to, SelectStatusDurationDialog.this.showT, SelectStatusDurationDialog.this.current);
                        SelectStatusDurationDialog.this.contentView.invalidate();
                        if (!z3) {
                            SelectStatusDurationDialog.this.menuView.setAlpha(SelectStatusDurationDialog.this.showT);
                        }
                        if (SelectStatusDurationDialog.this.showT < 0.5f && !z3 && (runnable = runnable3) != null) {
                            boolean[] zArr = zArr2;
                            if (!zArr[0]) {
                                zArr[0] = true;
                                runnable.run();
                            }
                        }
                        if (!z3) {
                            if (z4) {
                                SelectStatusDurationDialog.this.imageViewEmoji.notDraw = false;
                                SelectStatusDurationDialog.this.this$0.emojiGridView.invalidate();
                            }
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
                        }
                        ValueAnimator unused2 = SelectStatusDurationDialog.this.showAnimator = null;
                        SelectStatusDurationDialog.this.contentView.invalidate();
                        Runnable runnable2 = runnable4;
                        if (runnable2 != null) {
                            runnable2.run();
                        }
                    }
                });
                this.showAnimator.setDuration(420);
                this.showAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.showAnimator.start();
            } else if (runnable != null) {
                runnable.run();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$animateShow$10(boolean z, boolean z2, Runnable runnable, boolean[] zArr, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.showT = floatValue;
            AndroidUtilities.lerp(this.from, this.to, floatValue, this.current);
            this.contentView.invalidate();
            if (!z) {
                this.menuView.setAlpha(this.showT);
            }
            if (this.showT < 0.025f && !z) {
                if (z2) {
                    this.imageViewEmoji.notDraw = false;
                    this.this$0.emojiGridView.invalidate();
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
            }
            if (this.showT < 0.5f && !z && runnable != null && !zArr[0]) {
                zArr[0] = true;
                runnable.run();
            }
        }

        private void animateMenuShow(final boolean z, final Runnable runnable) {
            ValueAnimator valueAnimator = this.showMenuAnimator;
            if (valueAnimator != null) {
                if (this.showingMenu != z) {
                    valueAnimator.cancel();
                } else {
                    return;
                }
            }
            this.showingMenu = z;
            float[] fArr = new float[2];
            fArr[0] = this.showMenuT;
            fArr[1] = z ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.showMenuAnimator = ofFloat;
            ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda0(this));
            this.showMenuAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    float unused = SelectStatusDurationDialog.this.showMenuT = z ? 1.0f : 0.0f;
                    SelectStatusDurationDialog.this.menuView.setBackScaleY(SelectStatusDurationDialog.this.showMenuT);
                    SelectStatusDurationDialog.this.menuView.setAlpha(CubicBezierInterpolator.EASE_OUT.getInterpolation(SelectStatusDurationDialog.this.showMenuT));
                    int itemsCount = SelectStatusDurationDialog.this.menuView.getItemsCount();
                    for (int i = 0; i < itemsCount; i++) {
                        float cascade = AndroidUtilities.cascade(SelectStatusDurationDialog.this.showMenuT, (float) i, (float) itemsCount, 4.0f);
                        SelectStatusDurationDialog.this.menuView.getItemAt(i).setTranslationY((1.0f - cascade) * ((float) AndroidUtilities.dp(-12.0f)));
                        SelectStatusDurationDialog.this.menuView.getItemAt(i).setAlpha(cascade);
                    }
                    ValueAnimator unused2 = SelectStatusDurationDialog.this.showMenuAnimator = null;
                    Runnable runnable = runnable;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            if (z) {
                this.showMenuAnimator.setDuration(360);
                this.showMenuAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            } else {
                this.showMenuAnimator.setDuration(240);
                this.showMenuAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            }
            this.showMenuAnimator.start();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$animateMenuShow$11(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.showMenuT = floatValue;
            this.menuView.setBackScaleY(floatValue);
            this.menuView.setAlpha(CubicBezierInterpolator.EASE_OUT.getInterpolation(this.showMenuT));
            int itemsCount = this.menuView.getItemsCount();
            for (int i = 0; i < itemsCount; i++) {
                float cascade = AndroidUtilities.cascade(this.showMenuT, (float) i, (float) itemsCount, 4.0f);
                this.menuView.getItemAt(i).setTranslationY((1.0f - cascade) * ((float) AndroidUtilities.dp(-12.0f)));
                this.menuView.getItemAt(i).setAlpha(cascade);
            }
        }

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            boolean dispatchTouchEvent = super.dispatchTouchEvent(motionEvent);
            if (dispatchTouchEvent || motionEvent.getAction() != 0) {
                return dispatchTouchEvent;
            }
            dismiss();
            return false;
        }

        public void show() {
            super.show();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
            animateShow(true, (Runnable) null, (Runnable) null, true);
            animateMenuShow(true, (Runnable) null);
        }

        public void dismiss() {
            if (!this.dismissed) {
                done((Integer) null);
                this.dismissed = true;
            }
        }
    }
}
