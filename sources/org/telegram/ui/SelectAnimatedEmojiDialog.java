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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$EmojiStatus;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_account_clearRecentEmojiStatuses;
import org.telegram.tgnet.TLRPC$TL_emojiStatus;
import org.telegram.tgnet.TLRPC$TL_emojiStatusUntil;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmojiDefaultStatuses;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.DrawingInBackgroundThreadDrawable;
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
    public LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables;
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
    public RecyclerListView emojiGridView;
    private float emojiSelectAlpha;
    private ValueAnimator emojiSelectAnimator;
    private Rect emojiSelectRect;
    /* access modifiers changed from: private */
    public ImageViewEmoji emojiSelectView;
    /* access modifiers changed from: private */
    public EmojiTabsStrip emojiTabs;
    private View emojiTabsShadow;
    private Integer emojiX;
    private ArrayList<Long> expandedEmojiSets;
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
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    ArrayList<EmojiListView.DrawingInBackgroundLine> lineDrawables;
    ArrayList<EmojiListView.DrawingInBackgroundLine> lineDrawablesTmp;
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
    public void onReactionClick(ImageViewEmoji imageViewEmoji, ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
    }

    public void putAnimatedEmojiToCache(AnimatedEmojiDrawable animatedEmojiDrawable) {
        this.animatedEmojiDrawables.put(animatedEmojiDrawable.getDocumentId(), animatedEmojiDrawable);
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
            setInputMethodMode(2);
            setSoftInputMode(0);
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
    public SelectAnimatedEmojiDialog(org.telegram.ui.ActionBar.BaseFragment r28, android.content.Context r29, boolean r30, java.lang.Integer r31, int r32, org.telegram.ui.ActionBar.Theme.ResourcesProvider r33, int r34) {
        /*
            r27 = this;
            r7 = r27
            r8 = r29
            r9 = r31
            r10 = r32
            r11 = r33
            r7.<init>(r8)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.lineDrawables = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.lineDrawablesTmp = r0
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
            r6 = -1
            r7.animateExpandFromPosition = r6
            r7.animateExpandToPosition = r6
            r0 = -1
            r7.animateExpandStartTime = r0
            r7.defaultSetLoading = r13
            android.util.LongSparseArray r0 = new android.util.LongSparseArray
            r0.<init>()
            r7.animatedEmojiDrawables = r0
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>()
            r7.paint = r0
            r7.resourcesProvider = r11
            r7.type = r10
            r0 = r30
            r7.includeEmpty = r0
            r0 = r28
            r7.baseFragment = r0
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "emoji"
            r1.append(r2)
            if (r10 != 0) goto L_0x00eb
            java.lang.String r2 = "status"
            goto L_0x00ed
        L_0x00eb:
            java.lang.String r2 = "reaction"
        L_0x00ed:
            r1.append(r2)
            java.lang.String r2 = "usehint"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            int r0 = r0.getInt(r1, r13)
            r1 = 3
            if (r0 >= r1) goto L_0x0102
            r0 = 1
            goto L_0x0103
        L_0x0102:
            r0 = 0
        L_0x0103:
            r7.includeHint = r0
            android.graphics.Paint r0 = r7.selectorPaint
            java.lang.String r5 = "listSelectorSDK21"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r5, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
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
            if (r9 != 0) goto L_0x0134
            r3 = 0
            goto L_0x014d
        L_0x0134:
            int r0 = r31.intValue()
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 1133641728(0x43920000, float:292.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = androidx.core.math.MathUtils.clamp((int) r0, (int) r1, (int) r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r3 = r0
        L_0x014d:
            if (r3 == 0) goto L_0x015d
            int r0 = r3.intValue()
            r1 = 1126825984(0x432a0000, float:170.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            if (r0 <= r1) goto L_0x015d
            r0 = 1
            goto L_0x015e
        L_0x015d:
            r0 = 0
        L_0x015e:
            r7.setFocusableInTouchMode(r12)
            r2 = 2
            r16 = 1082130432(0x40800000, float:4.0)
            if (r10 == 0) goto L_0x0168
            if (r10 != r2) goto L_0x0187
        L_0x0168:
            r1 = r34
            r7.topMarginDp = r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r7.setPadding(r1, r4, r6, r15)
            org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda7 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda7
            r1.<init>(r7)
            r7.setOnTouchListener(r1)
        L_0x0187:
            java.lang.String r15 = "actionBarDefaultSubmenuBackground"
            if (r3 == 0) goto L_0x01dc
            android.view.View r1 = new android.view.View
            r1.<init>(r8)
            r7.bubble1View = r1
            android.content.res.Resources r1 = r27.getResources()
            int r4 = org.telegram.messenger.R.drawable.shadowed_bubble1
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r4)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            int r6 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r15, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r6, r12)
            r1.setColorFilter(r4)
            android.view.View r4 = r7.bubble1View
            r4.setBackground(r1)
            android.view.View r1 = r7.bubble1View
            r17 = 10
            r18 = 1092616192(0x41200000, float:10.0)
            r19 = 51
            int r4 = r3.intValue()
            float r4 = (float) r4
            float r6 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r6
            if (r0 == 0) goto L_0x01c8
            r6 = -12
            goto L_0x01c9
        L_0x01c8:
            r6 = 4
        L_0x01c9:
            float r6 = (float) r6
            float r20 = r4 + r6
            int r4 = r7.topMarginDp
            float r4 = (float) r4
            r22 = 0
            r23 = 0
            r21 = r4
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r7.addView(r1, r4)
        L_0x01dc:
            org.telegram.ui.SelectAnimatedEmojiDialog$1 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$1
            r1.<init>(r8, r11, r3)
            r7.contentView = r1
            if (r10 == 0) goto L_0x01e7
            if (r10 != r2) goto L_0x01fc
        L_0x01e7:
            r4 = 1090519040(0x41000000, float:8.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.setPadding(r6, r12, r14, r4)
        L_0x01fc:
            android.widget.FrameLayout r1 = r7.contentView
            r18 = -1
            r19 = -1082130432(0xffffffffbvar_, float:-1.0)
            r20 = 119(0x77, float:1.67E-43)
            r21 = 0
            r12 = 6
            if (r10 == 0) goto L_0x020f
            if (r10 != r2) goto L_0x020c
            goto L_0x020f
        L_0x020c:
            r22 = 0
            goto L_0x0215
        L_0x020f:
            int r4 = r7.topMarginDp
            int r4 = r4 + r12
            float r4 = (float) r4
            r22 = r4
        L_0x0215:
            r23 = 0
            r24 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r7.addView(r1, r4)
            r18 = 5
            if (r3 == 0) goto L_0x0274
            org.telegram.ui.SelectAnimatedEmojiDialog$2 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$2
            r1.<init>(r7, r8)
            r7.bubble2View = r1
            android.content.res.Resources r1 = r27.getResources()
            int r4 = org.telegram.messenger.R.drawable.shadowed_bubble2_half
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r4)
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            int r6 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r15, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r6, r2)
            r1.setColorFilter(r4)
            android.view.View r2 = r7.bubble2View
            r2.setBackground(r1)
            android.view.View r1 = r7.bubble2View
            r19 = 17
            r20 = 1091567616(0x41100000, float:9.0)
            r21 = 51
            int r2 = r3.intValue()
            float r2 = (float) r2
            float r4 = org.telegram.messenger.AndroidUtilities.density
            float r2 = r2 / r4
            if (r0 == 0) goto L_0x025d
            r0 = -25
            goto L_0x025f
        L_0x025d:
            r0 = 10
        L_0x025f:
            float r0 = (float) r0
            float r22 = r2 + r0
            int r0 = r7.topMarginDp
            int r0 = r0 + 5
            float r0 = (float) r0
            r24 = 0
            r25 = 0
            r23 = r0
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r7.addView(r1, r0)
        L_0x0274:
            org.telegram.ui.SelectAnimatedEmojiDialog$3 r6 = new org.telegram.ui.SelectAnimatedEmojiDialog$3
            r4 = 0
            r19 = 0
            r20 = 1
            r21 = 0
            r0 = r6
            r1 = r27
            r12 = 2
            r2 = r29
            r14 = r3
            r3 = r4
            r4 = r19
            r26 = r5
            r5 = r20
            r12 = r6
            r6 = r21
            r0.<init>(r2, r3, r4, r5, r6)
            r7.emojiTabs = r12
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r0 = r12.recentTab
            org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda6 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda6
            r1.<init>(r7)
            r0.setOnLongClickListener(r1)
            org.telegram.ui.Components.EmojiTabsStrip r0 = r7.emojiTabs
            r0.updateButtonDrawables = r13
            if (r10 == 0) goto L_0x02a9
            r1 = 2
            if (r10 != r1) goto L_0x02a7
            goto L_0x02a9
        L_0x02a7:
            r12 = 5
            goto L_0x02aa
        L_0x02a9:
            r12 = 6
        L_0x02aa:
            r0.setAnimatedEmojiCacheType(r12)
            org.telegram.ui.Components.EmojiTabsStrip r0 = r7.emojiTabs
            if (r14 != 0) goto L_0x02b3
            r1 = 1
            goto L_0x02b4
        L_0x02b3:
            r1 = 0
        L_0x02b4:
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
            r0.<init>(r7, r8, r14)
            r7.emojiTabsShadow = r0
            java.lang.String r2 = "divider"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r2, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            r0.setBackgroundColor(r2)
            android.widget.FrameLayout r0 = r7.contentView
            android.view.View r2 = r7.emojiTabsShadow
            r18 = -1
            float r3 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1065353216(0x3var_, float:1.0)
            float r19 = r4 / r3
            r20 = 48
            r21 = 0
            r22 = 1108344832(0x42100000, float:36.0)
            r23 = 0
            r24 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r0.addView(r2, r3)
            org.telegram.ui.SelectAnimatedEmojiDialog$5 r0 = new org.telegram.ui.SelectAnimatedEmojiDialog$5
            r0.<init>(r8)
            r7.emojiGridView = r0
            r2 = 1
            r0.setDrawSelectorBehind(r2)
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
            org.telegram.ui.Components.RecyclerListView r2 = r7.emojiGridView
            r2.setItemAnimator(r0)
            org.telegram.ui.Components.RecyclerListView r0 = r7.emojiGridView
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r3 = 1073741824(0x40000000, float:2.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r0.setPadding(r2, r4, r1, r3)
            org.telegram.ui.Components.RecyclerListView r0 = r7.emojiGridView
            r0.setClipToPadding(r13)
            org.telegram.ui.Components.RecyclerListView r0 = r7.emojiGridView
            org.telegram.ui.SelectAnimatedEmojiDialog$Adapter r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$Adapter
            r2 = 0
            r1.<init>()
            r7.adapter = r1
            r0.setAdapter(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.emojiGridView
            org.telegram.ui.SelectAnimatedEmojiDialog$7 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$7
            r2 = 8
            r1.<init>(r8, r2)
            r7.layoutManager = r1
            r0.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.emojiGridView
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r0.setSelectorRadius(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.emojiGridView
            r1 = r26
            int r1 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r1, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            r0.setSelectorDrawableColor(r1)
            androidx.recyclerview.widget.GridLayoutManager r0 = r7.layoutManager
            org.telegram.ui.SelectAnimatedEmojiDialog$8 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$8
            r1.<init>()
            r0.setSpanSizeLookup(r1)
            android.widget.FrameLayout r0 = r7.contentView
            org.telegram.ui.Components.RecyclerListView r1 = r7.emojiGridView
            r19 = -1082130432(0xffffffffbvar_, float:-1.0)
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = r3 / r2
            float r22 = r2 + r6
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RecyclerAnimationScrollHelper r0 = new org.telegram.ui.Components.RecyclerAnimationScrollHelper
            org.telegram.ui.Components.RecyclerListView r1 = r7.emojiGridView
            androidx.recyclerview.widget.GridLayoutManager r2 = r7.layoutManager
            r0.<init>(r1, r2)
            r7.scrollHelper = r0
            org.telegram.ui.SelectAnimatedEmojiDialog$9 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$9
            r1.<init>()
            r0.setAnimationCallback(r1)
            org.telegram.ui.Components.RecyclerListView r5 = r7.emojiGridView
            org.telegram.ui.SelectAnimatedEmojiDialog$10 r4 = new org.telegram.ui.SelectAnimatedEmojiDialog$10
            r0 = r4
            r1 = r27
            r2 = r32
            r3 = r29
            r13 = r4
            r4 = r33
            r12 = r5
            r5 = r31
            r0.<init>(r2, r3, r4, r5)
            int r0 = android.view.ViewConfiguration.getLongPressTimeout()
            float r0 = (float) r0
            r1 = 1048576000(0x3e800000, float:0.25)
            float r0 = r0 * r1
            long r0 = (long) r0
            r12.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended) r13, (long) r0)
            org.telegram.ui.Components.RecyclerListView r0 = r7.emojiGridView
            org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda11 r1 = new org.telegram.ui.SelectAnimatedEmojiDialog$$ExternalSyntheticLambda11
            r1.<init>(r7, r10)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.SelectAnimatedEmojiDialog$11 r0 = new org.telegram.ui.SelectAnimatedEmojiDialog$11
            r0.<init>(r7, r8, r14)
            r7.topGradientView = r0
            android.content.res.Resources r0 = r27.getResources()
            int r1 = org.telegram.messenger.R.drawable.gradient_top
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r15, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            r3 = 1061997773(0x3f4ccccd, float:0.8)
            int r2 = org.telegram.messenger.AndroidUtilities.multiplyAlphaComponent(r2, r3)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.SRC_IN
            r1.<init>(r2, r4)
            r0.setColorFilter(r1)
            android.view.View r1 = r7.topGradientView
            r1.setBackground(r0)
            android.view.View r0 = r7.topGradientView
            r1 = 0
            r0.setAlpha(r1)
            android.widget.FrameLayout r0 = r7.contentView
            android.view.View r1 = r7.topGradientView
            r19 = 1101004800(0x41a00000, float:20.0)
            r20 = 55
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r4 = 1065353216(0x3var_, float:1.0)
            float r14 = r4 / r2
            float r22 = r14 + r6
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r0.addView(r1, r2)
            android.view.View r0 = new android.view.View
            r0.<init>(r8)
            r7.bottomGradientView = r0
            android.content.res.Resources r0 = r27.getResources()
            int r1 = org.telegram.messenger.R.drawable.gradient_bottom
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            int r2 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r15, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r11)
            int r2 = org.telegram.messenger.AndroidUtilities.multiplyAlphaComponent(r2, r3)
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
            r5 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r3, (int) r4)
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
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
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
        builder.setPositiveButton(LocaleController.getString("Clear", R.string.Clear).toUpperCase(), new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda4(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        builder.setDimEnabled(false);
        builder.setOnDismissListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda5(this));
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
            ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda0(this));
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
            AnonymousClass13 r0 = new LinearSmoothScrollerCustom(this.emojiGridView.getContext(), 2) {
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

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return (viewHolder.getItemViewType() == 0 || viewHolder.getItemViewType() == 5 || viewHolder.getItemViewType() == 4 || viewHolder.getItemViewType() == 6) ? false : true;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v14, resolved type: org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v15, resolved type: org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v16, resolved type: org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackButton} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v17, resolved type: org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackExpand} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v18, resolved type: org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v19, resolved type: android.widget.ImageView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v20, resolved type: org.telegram.ui.SelectAnimatedEmojiDialog$HeaderView} */
        /* JADX WARNING: type inference failed for: r2v8, types: [android.widget.TextView, org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$1] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r2, int r3) {
            /*
                r1 = this;
                if (r3 != 0) goto L_0x000f
                org.telegram.ui.SelectAnimatedEmojiDialog$HeaderView r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$HeaderView
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r0 = r3.getContext()
                r2.<init>(r3, r0)
                goto L_0x00a1
            L_0x000f:
                r2 = 2
                if (r3 != r2) goto L_0x001f
                android.widget.ImageView r2 = new android.widget.ImageView
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r3 = r3.getContext()
                r2.<init>(r3)
                goto L_0x00a1
            L_0x001f:
                r2 = 3
                if (r3 != r2) goto L_0x002f
                org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r0 = r3.getContext()
                r2.<init>(r0)
                goto L_0x00a1
            L_0x002f:
                r2 = 4
                if (r3 != r2) goto L_0x003f
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackExpand r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackExpand
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r3 = r3.getContext()
                r0 = 0
                r2.<init>(r3, r0)
                goto L_0x00a1
            L_0x003f:
                r2 = 5
                if (r3 != r2) goto L_0x004e
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackButton r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackButton
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r0 = r3.getContext()
                r2.<init>(r3, r0)
                goto L_0x00a1
            L_0x004e:
                r2 = 6
                if (r3 != r2) goto L_0x0096
                org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$1 r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$1
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r3 = r3.getContext()
                r2.<init>(r1, r3)
                r3 = 1
                r0 = 1095761920(0x41500000, float:13.0)
                r2.setTextSize(r3, r0)
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.type
                if (r3 != 0) goto L_0x0076
                int r3 = org.telegram.messenger.R.string.EmojiLongtapHint
                java.lang.String r0 = "EmojiLongtapHint"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r0, r3)
                r2.setText(r3)
                goto L_0x0081
            L_0x0076:
                int r3 = org.telegram.messenger.R.string.ReactionsLongtapHint
                java.lang.String r0 = "ReactionsLongtapHint"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r0, r3)
                r2.setText(r3)
            L_0x0081:
                r3 = 17
                r2.setGravity(r3)
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r3.resourcesProvider
                java.lang.String r0 = "windowBackgroundWhiteGrayText"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
                r2.setTextColor(r3)
                goto L_0x00a1
            L_0x0096:
                org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r0 = r3.getContext()
                r2.<init>(r0)
            L_0x00a1:
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.animation.ValueAnimator r3 = r3.showAnimator
                if (r3 == 0) goto L_0x00bc
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.animation.ValueAnimator r3 = r3.showAnimator
                boolean r3 = r3.isRunning()
                if (r3 == 0) goto L_0x00bc
                r3 = 0
                r2.setScaleX(r3)
                r2.setScaleY(r3)
            L_0x00bc:
                org.telegram.ui.Components.RecyclerListView$Holder r3 = new org.telegram.ui.Components.RecyclerListView$Holder
                r3.<init>(r2)
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SelectAnimatedEmojiDialog.Adapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public int getItemViewType(int i) {
            if (i >= SelectAnimatedEmojiDialog.this.recentReactionsStartRow && i < SelectAnimatedEmojiDialog.this.recentReactionsEndRow) {
                return 3;
            }
            if (i >= SelectAnimatedEmojiDialog.this.topReactionsStartRow && i < SelectAnimatedEmojiDialog.this.topReactionsEndRow) {
                return 3;
            }
            if (SelectAnimatedEmojiDialog.this.positionToExpand.indexOfKey(i) >= 0) {
                return 4;
            }
            if (SelectAnimatedEmojiDialog.this.positionToButton.indexOfKey(i) >= 0) {
                return 5;
            }
            if (i == SelectAnimatedEmojiDialog.this.longtapHintRow) {
                return 6;
            }
            return (SelectAnimatedEmojiDialog.this.positionToSection.indexOfKey(i) >= 0 || i == SelectAnimatedEmojiDialog.this.recentReactionsSectionRow || i == SelectAnimatedEmojiDialog.this.popularSectionRow) ? 0 : 1;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: org.telegram.ui.Components.EmojiView$EmojiPack} */
        /* JADX WARNING: type inference failed for: r8v0 */
        /* JADX WARNING: type inference failed for: r8v1, types: [org.telegram.tgnet.TLRPC$Document] */
        /* JADX WARNING: type inference failed for: r8v3, types: [org.telegram.tgnet.TLRPC$Document] */
        /* JADX WARNING: type inference failed for: r8v8 */
        /* JADX WARNING: type inference failed for: r8v9 */
        /* JADX WARNING: type inference failed for: r8v10 */
        /* JADX WARNING: Code restructure failed: missing block: B:111:0x03f3, code lost:
            if (r0.this$0.selectedDocumentIds.contains(java.lang.Long.valueOf(r2.getDocumentId())) != false) goto L_0x0479;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:134:0x0477, code lost:
            if (r0.this$0.selectedDocumentIds.contains(java.lang.Long.valueOf(r2.getDocumentId())) != false) goto L_0x0479;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:98:0x0391, code lost:
            if (r0.this$0.selectedDocumentIds.contains(java.lang.Long.valueOf(r2.getDocumentId())) != false) goto L_0x0479;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r21, int r22) {
            /*
                r20 = this;
                r0 = r20
                r1 = r21
                r2 = r22
                int r3 = r21.getItemViewType()
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
                r4 = 6
                r6 = 0
                r7 = 1
                if (r3 != r4) goto L_0x005a
                android.view.View r1 = r1.itemView
                android.widget.TextView r1 = (android.widget.TextView) r1
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.lang.Integer r2 = r2.hintExpireDate
                if (r2 == 0) goto L_0x0059
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
            L_0x0059:
                return
            L_0x005a:
                r4 = 8
                r8 = 0
                if (r3 != 0) goto L_0x00da
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$HeaderView r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.HeaderView) r1
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.recentReactionsSectionRow
                if (r2 != r3) goto L_0x0086
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
            L_0x0086:
                android.widget.ImageView r3 = r1.closeIcon
                r3.setVisibility(r4)
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.popularSectionRow
                if (r2 != r3) goto L_0x009f
                int r2 = org.telegram.messenger.R.string.PopularReactions
                java.lang.String r3 = "PopularReactions"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2, r6)
                return
            L_0x009f:
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r3 = r3.positionToSection
                int r2 = r3.get(r2)
                if (r2 < 0) goto L_0x00d5
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                java.lang.Object r2 = r3.get(r2)
                org.telegram.ui.Components.EmojiView$EmojiPack r2 = (org.telegram.ui.Components.EmojiView.EmojiPack) r2
                org.telegram.tgnet.TLRPC$StickerSet r3 = r2.set
                java.lang.String r3 = r3.title
                boolean r2 = r2.free
                if (r2 != 0) goto L_0x00d0
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r2 = r2.currentAccount
                org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
                boolean r2 = r2.isPremium()
                if (r2 != 0) goto L_0x00d0
                r6 = 1
            L_0x00d0:
                r1.setText(r3, r6)
                goto L_0x047e
            L_0x00d5:
                r1.setText(r8, r6)
                goto L_0x047e
            L_0x00da:
                r9 = 3
                if (r3 != r9) goto L_0x01dd
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r1
                r1.position = r2
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.recentReactionsStartRow
                if (r2 < r3) goto L_0x0107
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.recentReactionsEndRow
                if (r2 >= r3) goto L_0x0107
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.recentReactionsStartRow
                int r2 = r2 - r3
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.recentReactions
                java.lang.Object r2 = r3.get(r2)
                org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r2 = (org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction) r2
                goto L_0x011a
            L_0x0107:
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.topReactionsStartRow
                int r2 = r2 - r3
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.topReactions
                java.lang.Object r2 = r3.get(r2)
                org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r2 = (org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction) r2
            L_0x011a:
                org.telegram.messenger.ImageReceiver r3 = r1.imageReceiver
                if (r3 != 0) goto L_0x012e
                org.telegram.messenger.ImageReceiver r3 = new org.telegram.messenger.ImageReceiver
                r3.<init>()
                r1.imageReceiver = r3
                r5 = 7
                r3.setLayerNum(r5)
                org.telegram.messenger.ImageReceiver r3 = r1.imageReceiver
                r3.onAttachedToWindow()
            L_0x012e:
                r1.reaction = r2
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r3 = r3.selectedReactions
                boolean r3 = r3.contains(r2)
                r1.setViewSelected(r3)
                java.lang.String r3 = r2.emojicon
                if (r3 == 0) goto L_0x0196
                r1.isDefaultReaction = r7
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.currentAccount
                org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
                java.util.HashMap r3 = r3.getReactionsMap()
                java.lang.String r5 = r2.emojicon
                java.lang.Object r3 = r3.get(r5)
                org.telegram.tgnet.TLRPC$TL_availableReaction r3 = (org.telegram.tgnet.TLRPC$TL_availableReaction) r3
                if (r3 == 0) goto L_0x017c
                org.telegram.tgnet.TLRPC$Document r5 = r3.activate_animation
                r6 = 1045220557(0x3e4ccccd, float:0.2)
                java.lang.String r7 = "windowBackgroundWhiteGrayIcon"
                org.telegram.messenger.SvgHelper$SvgDrawable r14 = org.telegram.messenger.DocumentObject.getSvgThumb((org.telegram.tgnet.TLRPC$Document) r5, (java.lang.String) r7, (float) r6)
                org.telegram.messenger.ImageReceiver r9 = r1.imageReceiver
                org.telegram.tgnet.TLRPC$Document r3 = r3.select_animation
                org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r3)
                r12 = 0
                r13 = 0
                r15 = 0
                r19 = 0
                java.lang.String r11 = "60_60_pcache"
                java.lang.String r17 = "tgs"
                r18 = r2
                r9.setImage(r10, r11, r12, r13, r14, r15, r17, r18, r19)
                goto L_0x0181
            L_0x017c:
                org.telegram.messenger.ImageReceiver r2 = r1.imageReceiver
                r2.clearImage()
            L_0x0181:
                r1.span = r8
                r1.document = r8
                r1.setDrawable(r8)
                org.telegram.ui.Components.Premium.PremiumLockIconView r2 = r1.premiumLockIconView
                if (r2 == 0) goto L_0x047e
                r2.setVisibility(r4)
                org.telegram.ui.Components.Premium.PremiumLockIconView r1 = r1.premiumLockIconView
                r1.setImageReceiver(r8)
                goto L_0x047e
            L_0x0196:
                r1.isDefaultReaction = r6
                org.telegram.ui.Components.AnimatedEmojiSpan r3 = new org.telegram.ui.Components.AnimatedEmojiSpan
                long r4 = r2.documentId
                r3.<init>((long) r4, (android.graphics.Paint.FontMetricsInt) r8)
                r1.span = r3
                r1.document = r8
                org.telegram.messenger.ImageReceiver r2 = r1.imageReceiver
                r2.clearImage()
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r2 = r2.currentAccount
                org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
                boolean r2 = r2.isPremium()
                if (r2 != 0) goto L_0x047e
                org.telegram.ui.Components.Premium.PremiumLockIconView r2 = r1.premiumLockIconView
                if (r2 != 0) goto L_0x01d6
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
            L_0x01d6:
                org.telegram.ui.Components.Premium.PremiumLockIconView r1 = r1.premiumLockIconView
                r1.setVisibility(r6)
                goto L_0x047e
            L_0x01dd:
                r4 = 4
                r10 = 5
                if (r3 != r4) goto L_0x0270
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackExpand r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.EmojiPackExpand) r1
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r3 = r3.positionToExpand
                int r2 = r3.get(r2)
                if (r2 < 0) goto L_0x020a
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                int r3 = r3.size()
                if (r2 >= r3) goto L_0x020a
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                java.lang.Object r3 = r3.get(r2)
                r8 = r3
                org.telegram.ui.Components.EmojiView$EmojiPack r8 = (org.telegram.ui.Components.EmojiView.EmojiPack) r8
            L_0x020a:
                r3 = -1
                java.lang.String r4 = "+"
                if (r2 != r3) goto L_0x0244
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
                goto L_0x047e
            L_0x0244:
                if (r8 == 0) goto L_0x047e
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                androidx.recyclerview.widget.GridLayoutManager r2 = r2.layoutManager
                int r2 = r2.getSpanCount()
                int r2 = r2 * 3
                android.widget.TextView r1 = r1.textView
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r4)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r8.documents
                int r4 = r4.size()
                int r4 = r4 - r2
                int r4 = r4 + r7
                r3.append(r4)
                java.lang.String r2 = r3.toString()
                r1.setText(r2)
                goto L_0x047e
            L_0x0270:
                if (r3 != r10) goto L_0x02c1
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackButton r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.EmojiPackButton) r1
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r3 = r3.positionToButton
                int r2 = r3.get(r2)
                if (r2 < 0) goto L_0x047e
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                int r3 = r3.size()
                if (r2 >= r3) goto L_0x047e
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                java.lang.Object r3 = r3.get(r2)
                org.telegram.ui.Components.EmojiView$EmojiPack r3 = (org.telegram.ui.Components.EmojiView.EmojiPack) r3
                if (r3 == 0) goto L_0x047e
                org.telegram.tgnet.TLRPC$StickerSet r4 = r3.set
                java.lang.String r4 = r4.title
                boolean r5 = r3.free
                if (r5 != 0) goto L_0x02b5
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r5 = r5.currentAccount
                org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
                boolean r5 = r5.isPremium()
                if (r5 != 0) goto L_0x02b5
                r6 = 1
            L_0x02b5:
                boolean r5 = r3.installed
                org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$$ExternalSyntheticLambda1 r7 = new org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$$ExternalSyntheticLambda1
                r7.<init>(r0, r3, r2)
                r1.set(r4, r6, r5, r7)
                goto L_0x047e
            L_0x02c1:
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r1
                r1.empty = r6
                r1.position = r2
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r1.setPadding(r3, r4, r11, r5)
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
                if (r5 <= r3) goto L_0x0309
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r5 = r5.recentExpanded
                if (r5 != 0) goto L_0x0309
                goto L_0x031a
            L_0x0309:
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.recent
                int r3 = r3.size()
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r5 = r5.includeEmpty
                int r3 = r3 + r5
            L_0x031a:
                r1.setDrawable(r8)
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r5 = r5.includeEmpty
                if (r5 == 0) goto L_0x0352
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r5 = r5.includeHint
                if (r2 != r5) goto L_0x0352
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<java.lang.Long> r2 = r2.selectedDocumentIds
                boolean r2 = r2.contains(r8)
                r1.empty = r7
                r3 = 1084227584(0x40a00000, float:5.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r1.setPadding(r4, r5, r6, r3)
                r1.span = r8
                r1.document = r8
                goto L_0x047b
            L_0x0352:
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r5 = r5.includeHint
                int r5 = r2 - r5
                if (r5 >= r3) goto L_0x0395
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.recent
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r4 = r4.includeHint
                int r2 = r2 - r4
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r4 = r4.includeEmpty
                int r2 = r2 - r4
                java.lang.Object r2 = r3.get(r2)
                org.telegram.ui.Components.AnimatedEmojiSpan r2 = (org.telegram.ui.Components.AnimatedEmojiSpan) r2
                r1.span = r2
                if (r2 != 0) goto L_0x037b
                goto L_0x037d
            L_0x037b:
                org.telegram.tgnet.TLRPC$Document r8 = r2.document
            L_0x037d:
                r1.document = r8
                if (r2 == 0) goto L_0x047a
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<java.lang.Long> r3 = r3.selectedDocumentIds
                long r4 = r2.getDocumentId()
                java.lang.Long r2 = java.lang.Long.valueOf(r4)
                boolean r2 = r3.contains(r2)
                if (r2 == 0) goto L_0x047a
                goto L_0x0479
            L_0x0395:
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r5 = r5.defaultStatuses
                boolean r5 = r5.isEmpty()
                if (r5 != 0) goto L_0x03f7
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r5 = r5.includeHint
                int r5 = r2 - r5
                int r5 = r5 - r3
                int r5 = r5 - r7
                if (r5 < 0) goto L_0x03f7
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r5 = r5.includeHint
                int r5 = r2 - r5
                int r5 = r5 - r3
                int r5 = r5 - r7
                org.telegram.ui.SelectAnimatedEmojiDialog r9 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r9 = r9.defaultStatuses
                int r9 = r9.size()
                if (r5 >= r9) goto L_0x03f7
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
                if (r2 != 0) goto L_0x03dd
                goto L_0x03df
            L_0x03dd:
                org.telegram.tgnet.TLRPC$Document r8 = r2.document
            L_0x03df:
                r1.document = r8
                if (r2 == 0) goto L_0x047a
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<java.lang.Long> r3 = r3.selectedDocumentIds
                long r4 = r2.getDocumentId()
                java.lang.Long r2 = java.lang.Long.valueOf(r4)
                boolean r2 = r3.contains(r2)
                if (r2 == 0) goto L_0x047a
                goto L_0x0479
            L_0x03f7:
                r3 = 0
            L_0x03f8:
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r5 = r5.positionToSection
                int r5 = r5.size()
                if (r3 >= r5) goto L_0x0463
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r5 = r5.positionToSection
                int r5 = r5.keyAt(r3)
                org.telegram.ui.SelectAnimatedEmojiDialog r9 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r9 = r9.defaultStatuses
                boolean r9 = r9.isEmpty()
                r9 = r9 ^ r7
                int r9 = r3 - r9
                if (r9 < 0) goto L_0x042a
                org.telegram.ui.SelectAnimatedEmojiDialog r10 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r10 = r10.packs
                java.lang.Object r9 = r10.get(r9)
                org.telegram.ui.Components.EmojiView$EmojiPack r9 = (org.telegram.ui.Components.EmojiView.EmojiPack) r9
                goto L_0x042b
            L_0x042a:
                r9 = r8
            L_0x042b:
                if (r9 != 0) goto L_0x042e
                goto L_0x0460
            L_0x042e:
                boolean r10 = r9.expanded
                if (r10 == 0) goto L_0x0439
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r10 = r9.documents
                int r10 = r10.size()
                goto L_0x0443
            L_0x0439:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r10 = r9.documents
                int r10 = r10.size()
                int r10 = java.lang.Math.min(r10, r4)
            L_0x0443:
                if (r2 <= r5) goto L_0x0460
                int r11 = r5 + 1
                int r11 = r11 + r10
                if (r2 > r11) goto L_0x0460
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r9.documents
                int r5 = r2 - r5
                int r5 = r5 - r7
                java.lang.Object r5 = r9.get(r5)
                org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
                if (r5 == 0) goto L_0x0460
                org.telegram.ui.Components.AnimatedEmojiSpan r9 = new org.telegram.ui.Components.AnimatedEmojiSpan
                r9.<init>((org.telegram.tgnet.TLRPC$Document) r5, (android.graphics.Paint.FontMetricsInt) r8)
                r1.span = r9
                r1.document = r5
            L_0x0460:
                int r3 = r3 + 1
                goto L_0x03f8
            L_0x0463:
                org.telegram.ui.Components.AnimatedEmojiSpan r2 = r1.span
                if (r2 == 0) goto L_0x047a
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<java.lang.Long> r3 = r3.selectedDocumentIds
                long r4 = r2.getDocumentId()
                java.lang.Long r2 = java.lang.Long.valueOf(r4)
                boolean r2 = r3.contains(r2)
                if (r2 == 0) goto L_0x047a
            L_0x0479:
                r6 = 1
            L_0x047a:
                r2 = r6
            L_0x047b:
                r1.setViewSelected(r2)
            L_0x047e:
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
                org.telegram.ui.Components.RecyclerListView r1 = r1.emojiGridView
                int r1 = r1.getChildCount()
                r2 = 0
                if (r0 >= r1) goto L_0x0072
                org.telegram.ui.SelectAnimatedEmojiDialog r1 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.Components.RecyclerListView r1 = r1.emojiGridView
                android.view.View r1 = r1.getChildAt(r0)
                boolean r1 = r1 instanceof org.telegram.ui.SelectAnimatedEmojiDialog.EmojiPackExpand
                if (r1 == 0) goto L_0x006f
                org.telegram.ui.SelectAnimatedEmojiDialog r1 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.Components.RecyclerListView r1 = r1.emojiGridView
                android.view.View r1 = r1.getChildAt(r0)
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.Components.RecyclerListView r3 = r3.emojiGridView
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
                RecyclerListView recyclerListView = SelectAnimatedEmojiDialog.this.emojiGridView;
                if (recyclerListView != null) {
                    recyclerListView.invalidate();
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
                animateEmojiSelect((ImageViewEmoji) view, new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda10(this, view, animatedEmojiSpan, tLRPC$Document));
            } else {
                onEmojiSelected(view, Long.valueOf(animatedEmojiSpan.documentId), tLRPC$Document, (Integer) null);
            }
        } else {
            onEmojiSelected(view, Long.valueOf(animatedEmojiSpan.documentId), tLRPC$Document, (Integer) null);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onEmojiClick$7(View view, AnimatedEmojiSpan animatedEmojiSpan, TLRPC$Document tLRPC$Document) {
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
    public void updateRows(boolean z) {
        boolean z2;
        TLRPC$StickerSet tLRPC$StickerSet;
        long j;
        boolean z3;
        Iterator<TLRPC$EmojiStatus> it;
        long j2;
        boolean z4;
        boolean z5;
        MediaDataController instance = MediaDataController.getInstance(UserConfig.selectedAccount);
        if (instance != null) {
            ArrayList arrayList = new ArrayList(instance.getStickerSets(5));
            ArrayList arrayList2 = new ArrayList(instance.getFeaturedEmojiSets());
            final ArrayList arrayList3 = new ArrayList(this.rowHashCodes);
            this.totalCount = 0;
            this.recentReactionsSectionRow = -1;
            this.recentReactionsStartRow = -1;
            this.recentReactionsEndRow = -1;
            this.popularSectionRow = -1;
            this.longtapHintRow = -1;
            this.recent.clear();
            this.defaultStatuses.clear();
            this.topReactions.clear();
            this.recentReactions.clear();
            this.packs.clear();
            this.positionToSection.clear();
            this.sectionToPosition.clear();
            this.positionToExpand.clear();
            this.rowHashCodes.clear();
            this.positionToButton.clear();
            if (this.includeHint && this.type != 2) {
                int i = this.totalCount;
                this.totalCount = i + 1;
                this.longtapHintRow = i;
                this.rowHashCodes.add(6);
            }
            if (this.recentReactionsToSet != null) {
                this.topReactionsStartRow = this.totalCount;
                ArrayList arrayList4 = new ArrayList();
                arrayList4.addAll(this.recentReactionsToSet);
                for (int i2 = 0; i2 < 16; i2++) {
                    if (!arrayList4.isEmpty()) {
                        this.topReactions.add((ReactionsLayoutInBubble.VisibleReaction) arrayList4.remove(0));
                    }
                }
                for (int i3 = 0; i3 < this.topReactions.size(); i3++) {
                    this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{-5632, Integer.valueOf(this.topReactions.get(i3).hashCode())})));
                }
                int size = this.totalCount + this.topReactions.size();
                this.totalCount = size;
                this.topReactionsEndRow = size;
                if (!arrayList4.isEmpty()) {
                    int i4 = 0;
                    while (true) {
                        if (i4 >= arrayList4.size()) {
                            z5 = true;
                            break;
                        } else if (((ReactionsLayoutInBubble.VisibleReaction) arrayList4.get(i4)).documentId != 0) {
                            z5 = false;
                            break;
                        } else {
                            i4++;
                        }
                    }
                    if (!z5) {
                        int i5 = this.totalCount;
                        this.totalCount = i5 + 1;
                        this.recentReactionsSectionRow = i5;
                        this.rowHashCodes.add(4);
                    } else if (UserConfig.getInstance(this.currentAccount).isPremium()) {
                        int i6 = this.totalCount;
                        this.totalCount = i6 + 1;
                        this.popularSectionRow = i6;
                        this.rowHashCodes.add(5);
                    }
                    this.recentReactionsStartRow = this.totalCount;
                    this.recentReactions.addAll(arrayList4);
                    for (int i7 = 0; i7 < this.recentReactions.size(); i7++) {
                        ArrayList<Integer> arrayList5 = this.rowHashCodes;
                        Object[] objArr = new Object[2];
                        objArr[0] = Integer.valueOf(z5 ? 4235 : -3142);
                        objArr[1] = Integer.valueOf(this.recentReactions.get(i7).hashCode());
                        arrayList5.add(Integer.valueOf(Arrays.hashCode(objArr)));
                    }
                    int size2 = this.totalCount + this.recentReactions.size();
                    this.totalCount = size2;
                    this.recentReactionsEndRow = size2;
                }
            } else if (this.type == 0) {
                ArrayList<TLRPC$EmojiStatus> recentEmojiStatuses = MediaDataController.getInstance(this.currentAccount).getRecentEmojiStatuses();
                TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSet(new TLRPC$TL_inputStickerSetEmojiDefaultStatuses(), false);
                if (stickerSet == null) {
                    this.defaultSetLoading = true;
                } else {
                    if (this.includeEmpty) {
                        this.totalCount++;
                        this.rowHashCodes.add(2);
                    }
                    ArrayList<TLRPC$EmojiStatus> defaultEmojiStatuses = MediaDataController.getInstance(this.currentAccount).getDefaultEmojiStatuses();
                    ArrayList<TLRPC$Document> arrayList6 = stickerSet.documents;
                    if (arrayList6 != null && !arrayList6.isEmpty()) {
                        for (int i8 = 0; i8 < Math.min(this.layoutManager.getSpanCount() - 1, stickerSet.documents.size()); i8++) {
                            this.recent.add(new AnimatedEmojiSpan(stickerSet.documents.get(i8), (Paint.FontMetricsInt) null));
                        }
                    }
                    if (recentEmojiStatuses != null && !recentEmojiStatuses.isEmpty()) {
                        Iterator<TLRPC$EmojiStatus> it2 = recentEmojiStatuses.iterator();
                        while (it2.hasNext()) {
                            TLRPC$EmojiStatus next = it2.next();
                            if (next instanceof TLRPC$TL_emojiStatus) {
                                it = it2;
                                j2 = ((TLRPC$TL_emojiStatus) next).document_id;
                            } else {
                                it = it2;
                                if (next instanceof TLRPC$TL_emojiStatusUntil) {
                                    TLRPC$TL_emojiStatusUntil tLRPC$TL_emojiStatusUntil = (TLRPC$TL_emojiStatusUntil) next;
                                    if (tLRPC$TL_emojiStatusUntil.until > ((int) (System.currentTimeMillis() / 1000))) {
                                        j2 = tLRPC$TL_emojiStatusUntil.document_id;
                                    }
                                    it2 = it;
                                } else {
                                    it2 = it;
                                }
                            }
                            int i9 = 0;
                            while (true) {
                                if (i9 >= this.recent.size()) {
                                    z4 = false;
                                    break;
                                } else if (this.recent.get(i9).getDocumentId() == j2) {
                                    z4 = true;
                                    break;
                                } else {
                                    i9++;
                                }
                            }
                            if (!z4) {
                                this.recent.add(new AnimatedEmojiSpan(j2, (Paint.FontMetricsInt) null));
                            }
                            it2 = it;
                        }
                    }
                    if (defaultEmojiStatuses != null && !defaultEmojiStatuses.isEmpty()) {
                        Iterator<TLRPC$EmojiStatus> it3 = defaultEmojiStatuses.iterator();
                        while (it3.hasNext()) {
                            TLRPC$EmojiStatus next2 = it3.next();
                            if (next2 instanceof TLRPC$TL_emojiStatus) {
                                j = ((TLRPC$TL_emojiStatus) next2).document_id;
                            } else if (next2 instanceof TLRPC$TL_emojiStatusUntil) {
                                TLRPC$TL_emojiStatusUntil tLRPC$TL_emojiStatusUntil2 = (TLRPC$TL_emojiStatusUntil) next2;
                                if (tLRPC$TL_emojiStatusUntil2.until > ((int) (System.currentTimeMillis() / 1000))) {
                                    j = tLRPC$TL_emojiStatusUntil2.document_id;
                                }
                            }
                            int i10 = 0;
                            while (true) {
                                if (i10 >= this.recent.size()) {
                                    z3 = false;
                                    break;
                                } else if (this.recent.get(i10).getDocumentId() == j) {
                                    z3 = true;
                                    break;
                                } else {
                                    i10++;
                                }
                            }
                            if (!z3) {
                                this.recent.add(new AnimatedEmojiSpan(j, (Paint.FontMetricsInt) null));
                            }
                        }
                    }
                    int spanCount = this.layoutManager.getSpanCount() * 5;
                    int i11 = spanCount - (this.includeEmpty ? 1 : 0);
                    if (this.recent.size() <= i11 || this.recentExpanded) {
                        for (int i12 = 0; i12 < this.recent.size(); i12++) {
                            this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{43223, Long.valueOf(this.recent.get(i12).getDocumentId())})));
                            this.totalCount++;
                        }
                    } else {
                        for (int i13 = 0; i13 < i11 - 1; i13++) {
                            this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{43223, Long.valueOf(this.recent.get(i13).getDocumentId())})));
                            this.totalCount++;
                        }
                        this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{-5531, -1, Integer.valueOf((this.recent.size() - spanCount) + (this.includeEmpty ? 1 : 0) + 1)})));
                        this.positionToExpand.put(this.totalCount, -1);
                        this.totalCount++;
                    }
                }
            }
            for (int i14 = 0; i14 < arrayList.size(); i14++) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) arrayList.get(i14);
                if (tLRPC$TL_messages_stickerSet != null && (tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set) != null && tLRPC$StickerSet.emojis && !this.installedEmojiSets.contains(Long.valueOf(tLRPC$StickerSet.id))) {
                    this.positionToSection.put(this.totalCount, this.packs.size());
                    this.sectionToPosition.put(this.packs.size(), this.totalCount);
                    this.totalCount++;
                    this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{9211, Long.valueOf(tLRPC$TL_messages_stickerSet.set.id)})));
                    EmojiView.EmojiPack emojiPack = new EmojiView.EmojiPack();
                    emojiPack.installed = true;
                    emojiPack.featured = false;
                    emojiPack.expanded = true;
                    emojiPack.free = !MessageObject.isPremiumEmojiPack(tLRPC$TL_messages_stickerSet);
                    emojiPack.set = tLRPC$TL_messages_stickerSet.set;
                    emojiPack.documents = tLRPC$TL_messages_stickerSet.documents;
                    this.packs.size();
                    this.packs.add(emojiPack);
                    this.totalCount += emojiPack.documents.size();
                    for (int i15 = 0; i15 < emojiPack.documents.size(); i15++) {
                        this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{3212, Long.valueOf(emojiPack.documents.get(i15).id)})));
                    }
                }
            }
            int spanCount2 = this.layoutManager.getSpanCount() * 3;
            for (int i16 = 0; i16 < arrayList2.size(); i16++) {
                TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) arrayList2.get(i16);
                if (tLRPC$StickerSetCovered instanceof TLRPC$TL_stickerSetFullCovered) {
                    TLRPC$TL_stickerSetFullCovered tLRPC$TL_stickerSetFullCovered = (TLRPC$TL_stickerSetFullCovered) tLRPC$StickerSetCovered;
                    int i17 = 0;
                    while (true) {
                        if (i17 >= this.packs.size()) {
                            z2 = false;
                            break;
                        } else if (this.packs.get(i17).set.id == tLRPC$TL_stickerSetFullCovered.set.id) {
                            z2 = true;
                            break;
                        } else {
                            i17++;
                        }
                    }
                    if (!z2) {
                        this.positionToSection.put(this.totalCount, this.packs.size());
                        this.sectionToPosition.put(this.packs.size(), this.totalCount);
                        this.totalCount++;
                        this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{9211, Long.valueOf(tLRPC$TL_stickerSetFullCovered.set.id)})));
                        EmojiView.EmojiPack emojiPack2 = new EmojiView.EmojiPack();
                        emojiPack2.installed = this.installedEmojiSets.contains(Long.valueOf(tLRPC$TL_stickerSetFullCovered.set.id));
                        emojiPack2.featured = true;
                        emojiPack2.free = !MessageObject.isPremiumEmojiPack((TLRPC$StickerSetCovered) tLRPC$TL_stickerSetFullCovered);
                        emojiPack2.set = tLRPC$TL_stickerSetFullCovered.set;
                        emojiPack2.documents = tLRPC$TL_stickerSetFullCovered.documents;
                        this.packs.size();
                        emojiPack2.expanded = this.expandedEmojiSets.contains(Long.valueOf(emojiPack2.set.id));
                        if (emojiPack2.documents.size() <= spanCount2 || emojiPack2.expanded) {
                            this.totalCount += emojiPack2.documents.size();
                            for (int i18 = 0; i18 < emojiPack2.documents.size(); i18++) {
                                this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{3212, Long.valueOf(emojiPack2.documents.get(i18).id)})));
                            }
                        } else {
                            this.totalCount += spanCount2;
                            for (int i19 = 0; i19 < spanCount2 - 1; i19++) {
                                this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{3212, Long.valueOf(emojiPack2.documents.get(i19).id)})));
                            }
                            this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{-5531, Long.valueOf(tLRPC$TL_stickerSetFullCovered.set.id), Integer.valueOf((emojiPack2.documents.size() - spanCount2) + 1)})));
                            this.positionToExpand.put(this.totalCount - 1, this.packs.size());
                        }
                        if (!emojiPack2.installed) {
                            this.positionToButton.put(this.totalCount, this.packs.size());
                            this.totalCount++;
                            this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{3321, Long.valueOf(tLRPC$TL_stickerSetFullCovered.set.id)})));
                        }
                        this.packs.add(emojiPack2);
                    }
                }
            }
            post(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda8(this));
            if (z) {
                DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    public boolean areContentsTheSame(int i, int i2) {
                        return true;
                    }

                    public int getOldListSize() {
                        return arrayList3.size();
                    }

                    public int getNewListSize() {
                        return SelectAnimatedEmojiDialog.this.rowHashCodes.size();
                    }

                    public boolean areItemsTheSame(int i, int i2) {
                        return ((Integer) arrayList3.get(i)).equals(SelectAnimatedEmojiDialog.this.rowHashCodes.get(i2));
                    }
                }, true).dispatchUpdatesTo((RecyclerView.Adapter) this.adapter);
            } else {
                this.adapter.notifyDataSetChanged();
            }
            RecyclerListView recyclerListView = this.emojiGridView;
            if (!recyclerListView.scrolledByUserOnce) {
                recyclerListView.scrollToPosition(0);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateRows$8() {
        this.emojiTabs.updateEmojiPacks(this.packs);
    }

    public void expand(int i, View view) {
        int i2;
        Integer num;
        int i3;
        int i4;
        int i5;
        int i6 = this.positionToExpand.get(i);
        boolean z = false;
        Integer num2 = null;
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
            i5 = this.layoutManager.getSpanCount() * 5;
            boolean z2 = this.recentExpanded;
            if (!z2) {
                boolean z3 = this.includeHint;
                boolean z4 = this.includeEmpty;
                int i7 = (z3 ? 1 : 0) + (z4 ? 1 : 0);
                i3 = z2 ? this.recent.size() : Math.min((i5 - z4) - 2, this.recent.size());
                int size = this.recent.size();
                this.recentExpanded = true;
                num = null;
                int i8 = i7;
                i4 = size;
                i2 = i8;
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
                post(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda9(this, num2.intValue() > i5 / 2 ? 1.5f : 3.5f, num.intValue()));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$expand$9(float f, int i) {
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

    private AnimatedEmojiSpan[] getAnimatedEmojiSpans() {
        AnimatedEmojiSpan[] animatedEmojiSpanArr = new AnimatedEmojiSpan[this.emojiGridView.getChildCount()];
        for (int i = 0; i < this.emojiGridView.getChildCount(); i++) {
            View childAt = this.emojiGridView.getChildAt(i);
            if (childAt instanceof ImageViewEmoji) {
                animatedEmojiSpanArr[i] = ((ImageViewEmoji) childAt).span;
            }
        }
        return animatedEmojiSpanArr;
    }

    /* access modifiers changed from: private */
    public int getCacheType() {
        int i = this.type;
        return (i == 0 || i == 2) ? 2 : 3;
    }

    public void updateEmojiDrawables() {
        this.animatedEmojiDrawables = AnimatedEmojiSpan.update(getCacheType(), (View) this.emojiGridView, getAnimatedEmojiSpans(), this.animatedEmojiDrawables);
    }

    class EmojiListView extends RecyclerListView {
        private int lastChildCount = -1;
        ArrayList<ArrayList<ImageViewEmoji>> unusedArrays = new ArrayList<>();
        ArrayList<DrawingInBackgroundLine> unusedLineDrawables = new ArrayList<>();
        SparseArray<ArrayList<ImageViewEmoji>> viewsGroupedByLines = new SparseArray<>();

        public EmojiListView(Context context) {
            super(context);
        }

        public boolean drawChild(Canvas canvas, View view, long j) {
            return super.drawChild(canvas, view, j);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (SelectAnimatedEmojiDialog.this.showAnimator == null || !SelectAnimatedEmojiDialog.this.showAnimator.isRunning()) {
                SelectAnimatedEmojiDialog.this.updateEmojiDrawables();
                this.lastChildCount = getChildCount();
            }
        }

        public void dispatchDraw(Canvas canvas) {
            DrawingInBackgroundLine drawingInBackgroundLine;
            ImageReceiver imageReceiver;
            if (!(this.lastChildCount == getChildCount() || SelectAnimatedEmojiDialog.this.showAnimator == null || SelectAnimatedEmojiDialog.this.showAnimator.isRunning())) {
                SelectAnimatedEmojiDialog.this.updateEmojiDrawables();
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
                int access$4900 = SelectAnimatedEmojiDialog.this.animateExpandFromPosition;
            }
            if (!(SelectAnimatedEmojiDialog.this.animatedEmojiDrawables == null || SelectAnimatedEmojiDialog.this.emojiGridView == null)) {
                for (int i2 = 0; i2 < SelectAnimatedEmojiDialog.this.emojiGridView.getChildCount(); i2++) {
                    View childAt = SelectAnimatedEmojiDialog.this.emojiGridView.getChildAt(i2);
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
                            imageViewEmoji.getBackground().draw(canvas);
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
            SelectAnimatedEmojiDialog.this.lineDrawablesTmp.clear();
            SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = SelectAnimatedEmojiDialog.this;
            selectAnimatedEmojiDialog.lineDrawablesTmp.addAll(selectAnimatedEmojiDialog.lineDrawables);
            SelectAnimatedEmojiDialog.this.lineDrawables.clear();
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
                    if (i4 >= SelectAnimatedEmojiDialog.this.lineDrawablesTmp.size()) {
                        break;
                    } else if (SelectAnimatedEmojiDialog.this.lineDrawablesTmp.get(i4).position == childAdapterPosition) {
                        drawingInBackgroundLine2 = SelectAnimatedEmojiDialog.this.lineDrawablesTmp.get(i4);
                        SelectAnimatedEmojiDialog.this.lineDrawablesTmp.remove(i4);
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
                SelectAnimatedEmojiDialog.this.lineDrawables.add(drawingInBackgroundLine);
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
            for (int i5 = 0; i5 < SelectAnimatedEmojiDialog.this.lineDrawablesTmp.size(); i5++) {
                if (this.unusedLineDrawables.size() < 3) {
                    this.unusedLineDrawables.add(SelectAnimatedEmojiDialog.this.lineDrawablesTmp.get(i5));
                    SelectAnimatedEmojiDialog.this.lineDrawablesTmp.get(i5).imageViewEmojis = null;
                    SelectAnimatedEmojiDialog.this.lineDrawablesTmp.get(i5).reset();
                } else {
                    SelectAnimatedEmojiDialog.this.lineDrawablesTmp.get(i5).onDetachFromWindow();
                }
            }
            SelectAnimatedEmojiDialog.this.lineDrawablesTmp.clear();
            if (SelectAnimatedEmojiDialog.this.emojiGridView != null) {
                for (int i6 = 0; i6 < SelectAnimatedEmojiDialog.this.emojiGridView.getChildCount(); i6++) {
                    View childAt2 = SelectAnimatedEmojiDialog.this.emojiGridView.getChildAt(i6);
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

            /* JADX WARNING: Removed duplicated region for block: B:47:0x0105  */
            /* JADX WARNING: Removed duplicated region for block: B:48:0x0116  */
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
                    if (r1 != 0) goto L_0x0052
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r1 = r0.imageViewEmojis
                    java.lang.Object r1 = r1.get(r3)
                    android.view.View r1 = (android.view.View) r1
                    float r5 = r1.getY()
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    int r6 = r6.getHeight()
                    int r7 = r1.getHeight()
                    int r6 = r6 - r7
                    float r6 = (float) r6
                    int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
                    if (r5 <= 0) goto L_0x0052
                    float r5 = r1.getY()
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    int r6 = r6.getHeight()
                    float r6 = (float) r6
                    float r5 = r5 - r6
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
                L_0x0052:
                    float r1 = r0.skewAlpha
                    r5 = 1
                    int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                    if (r1 < 0) goto L_0x0083
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r1 = r0.imageViewEmojis
                    int r1 = r1.size()
                    r6 = 4
                    if (r1 <= r6) goto L_0x0083
                    int r1 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
                    if (r1 == 0) goto L_0x0083
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r1 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r1 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    android.animation.ValueAnimator r1 = r1.showAnimator
                    if (r1 == 0) goto L_0x0081
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r1 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r1 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    android.animation.ValueAnimator r1 = r1.showAnimator
                    boolean r1 = r1.isRunning()
                    if (r1 == 0) goto L_0x0081
                    goto L_0x0083
                L_0x0081:
                    r1 = 0
                    goto L_0x0084
                L_0x0083:
                    r1 = 1
                L_0x0084:
                    if (r1 != 0) goto L_0x0102
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r6 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r6 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    long r6 = r6.animateExpandStartTime
                    r8 = 0
                    int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                    if (r10 <= 0) goto L_0x00af
                    long r6 = android.os.SystemClock.elapsedRealtime()
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r8 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r8 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    long r8 = r8.animateExpandStartTime
                    long r6 = r6 - r8
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r8 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r8 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    long r8 = r8.animateExpandDuration()
                    int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                    if (r10 >= 0) goto L_0x00af
                    r6 = 1
                    goto L_0x00b0
                L_0x00af:
                    r6 = 0
                L_0x00b0:
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r7 = r0.imageViewEmojis
                    int r7 = r7.size()
                    if (r3 >= r7) goto L_0x0102
                    java.util.ArrayList<org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r7 = r0.imageViewEmojis
                    java.lang.Object r7 = r7.get(r3)
                    org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r7 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r7
                    float r8 = r7.pressedProgress
                    int r8 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
                    if (r8 != 0) goto L_0x0103
                    android.animation.ValueAnimator r8 = r7.backAnimator
                    if (r8 != 0) goto L_0x0103
                    float r8 = r7.getTranslationX()
                    int r8 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
                    if (r8 != 0) goto L_0x0103
                    float r8 = r7.getTranslationY()
                    int r8 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
                    if (r8 != 0) goto L_0x0103
                    float r8 = r7.getAlpha()
                    int r8 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
                    if (r8 != 0) goto L_0x0103
                    if (r6 == 0) goto L_0x00ff
                    int r8 = r7.position
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r9 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r9 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    int r9 = r9.animateExpandFromPosition
                    if (r8 <= r9) goto L_0x00ff
                    int r7 = r7.position
                    org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r8 = org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.this
                    org.telegram.ui.SelectAnimatedEmojiDialog r8 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                    int r8 = r8.animateExpandToPosition
                    if (r7 >= r8) goto L_0x00ff
                    goto L_0x0103
                L_0x00ff:
                    int r3 = r3 + 1
                    goto L_0x00b0
                L_0x0102:
                    r5 = r1
                L_0x0103:
                    if (r5 == 0) goto L_0x0116
                    long r1 = java.lang.System.currentTimeMillis()
                    r11.prepareDraw(r1)
                    r1 = r12
                    r2 = r17
                    r11.drawInUiThread(r12, r2)
                    r11.reset()
                    goto L_0x011c
                L_0x0116:
                    r1 = r12
                    r2 = r17
                    super.draw(r12, r13, r15, r16, r17)
                L_0x011c:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SelectAnimatedEmojiDialog.EmojiListView.DrawingInBackgroundLine.draw(android.graphics.Canvas, long, int, int, float):void");
            }

            public void drawBitmap(Canvas canvas, Bitmap bitmap, Paint paint) {
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
            }

            public void prepareDraw(long j) {
                float f;
                float f2;
                ImageReceiver imageReceiver;
                long j2 = j;
                this.drawInBackgroundViews.clear();
                for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                    ImageViewEmoji imageViewEmoji = this.imageViewEmojis.get(i);
                    if (!imageViewEmoji.notDraw) {
                        float f3 = 0.7f;
                        float f4 = 1.0f;
                        if (imageViewEmoji.empty) {
                            Drawable access$5300 = SelectAnimatedEmojiDialog.this.getPremiumStar();
                            if (imageViewEmoji.pressedProgress != 0.0f || imageViewEmoji.selected) {
                                if (!imageViewEmoji.selected) {
                                    f3 = imageViewEmoji.pressedProgress;
                                }
                                f4 = 1.0f * (((1.0f - f3) * 0.2f) + 0.8f);
                            }
                            if (access$5300 != null) {
                                access$5300.setAlpha(255);
                                int width = (imageViewEmoji.getWidth() - imageViewEmoji.getPaddingLeft()) - imageViewEmoji.getPaddingRight();
                                int height = (imageViewEmoji.getHeight() - imageViewEmoji.getPaddingTop()) - imageViewEmoji.getPaddingBottom();
                                Rect rect = AndroidUtilities.rectTmp2;
                                float f5 = ((float) width) / 2.0f;
                                float f6 = ((float) height) / 2.0f;
                                rect.set((int) ((((float) imageViewEmoji.getWidth()) / 2.0f) - ((imageViewEmoji.getScaleX() * f5) * f4)), (int) ((((float) imageViewEmoji.getHeight()) / 2.0f) - ((imageViewEmoji.getScaleY() * f6) * f4)), (int) ((((float) imageViewEmoji.getWidth()) / 2.0f) + (f5 * imageViewEmoji.getScaleX() * f4)), (int) ((((float) imageViewEmoji.getHeight()) / 2.0f) + (f6 * imageViewEmoji.getScaleY() * f4)));
                                rect.offset(imageViewEmoji.getLeft() - this.startOffset, 0);
                                if (imageViewEmoji.drawableBounds == null) {
                                    imageViewEmoji.drawableBounds = new Rect();
                                }
                                imageViewEmoji.drawableBounds.set(rect);
                                imageViewEmoji.drawable = access$5300;
                                this.drawInBackgroundViews.add(imageViewEmoji);
                            }
                        } else {
                            if (imageViewEmoji.pressedProgress != 0.0f || imageViewEmoji.selected) {
                                if (!imageViewEmoji.selected) {
                                    f3 = imageViewEmoji.pressedProgress;
                                }
                                f = (((1.0f - f3) * 0.2f) + 0.8f) * 1.0f;
                            } else {
                                f = 1.0f;
                            }
                            if (!(SelectAnimatedEmojiDialog.this.animateExpandStartTime > 0 && SystemClock.elapsedRealtime() - SelectAnimatedEmojiDialog.this.animateExpandStartTime < SelectAnimatedEmojiDialog.this.animateExpandDuration()) || SelectAnimatedEmojiDialog.this.animateExpandFromPosition < 0 || SelectAnimatedEmojiDialog.this.animateExpandToPosition < 0 || SelectAnimatedEmojiDialog.this.animateExpandStartTime <= 0) {
                                f2 = imageViewEmoji.getAlpha();
                            } else {
                                int childAdapterPosition = EmojiListView.this.getChildAdapterPosition(imageViewEmoji) - SelectAnimatedEmojiDialog.this.animateExpandFromPosition;
                                int access$5200 = SelectAnimatedEmojiDialog.this.animateExpandToPosition - SelectAnimatedEmojiDialog.this.animateExpandFromPosition;
                                if (childAdapterPosition < 0 || childAdapterPosition >= access$5200) {
                                    f2 = 1.0f;
                                } else {
                                    float clamp = MathUtils.clamp(((float) (SystemClock.elapsedRealtime() - SelectAnimatedEmojiDialog.this.animateExpandStartTime)) / ((float) SelectAnimatedEmojiDialog.this.animateExpandAppearDuration()), 0.0f, 1.0f);
                                    float f7 = (float) childAdapterPosition;
                                    float f8 = (float) access$5200;
                                    float f9 = f8 / 4.0f;
                                    float cascade = AndroidUtilities.cascade(clamp, f7, f8, f9);
                                    f *= (this.appearScaleInterpolator.getInterpolation(AndroidUtilities.cascade(clamp, f7, f8, f9)) * 0.5f) + 0.5f;
                                    f2 = cascade * 1.0f;
                                }
                            }
                            if (imageViewEmoji.isDefaultReaction) {
                                imageReceiver = imageViewEmoji.imageReceiver;
                                imageReceiver.setAlpha(f2);
                            } else if (imageViewEmoji.span != null) {
                                AnimatedEmojiDrawable animatedEmojiDrawable = (AnimatedEmojiDrawable) SelectAnimatedEmojiDialog.this.animatedEmojiDrawables.get(imageViewEmoji.span.getDocumentId());
                                if (animatedEmojiDrawable == null && Math.min(imageViewEmoji.getScaleX(), imageViewEmoji.getScaleY()) * f > 0.0f && imageViewEmoji.getAlpha() * f2 > 0.0f) {
                                    animatedEmojiDrawable = AnimatedEmojiDrawable.make(SelectAnimatedEmojiDialog.this.currentAccount, SelectAnimatedEmojiDialog.this.getCacheType(), imageViewEmoji.span.getDocumentId());
                                    animatedEmojiDrawable.setColorFilter(SelectAnimatedEmojiDialog.this.premiumStarColorFilter);
                                    SelectAnimatedEmojiDialog.this.animatedEmojiDrawables.put(imageViewEmoji.span.getDocumentId(), animatedEmojiDrawable);
                                }
                                if (!(animatedEmojiDrawable == null || animatedEmojiDrawable.getImageReceiver() == null)) {
                                    imageReceiver = animatedEmojiDrawable.getImageReceiver();
                                    animatedEmojiDrawable.setAlpha((int) (f2 * 255.0f));
                                    imageViewEmoji.setDrawable(animatedEmojiDrawable);
                                    imageViewEmoji.drawable.setColorFilter(SelectAnimatedEmojiDialog.this.premiumStarColorFilter);
                                }
                            }
                            if (imageReceiver != null) {
                                if (imageViewEmoji.selected) {
                                    imageReceiver.setRoundRadius(AndroidUtilities.dp(4.0f));
                                } else {
                                    imageReceiver.setRoundRadius(0);
                                }
                                ImageReceiver.BackgroundThreadDrawHolder drawInBackgroundThread = imageReceiver.setDrawInBackgroundThread(imageViewEmoji.backgroundThreadDrawHolder);
                                imageViewEmoji.backgroundThreadDrawHolder = drawInBackgroundThread;
                                drawInBackgroundThread.time = j2;
                                imageViewEmoji.imageReceiverToDraw = imageReceiver;
                                imageViewEmoji.update(j2);
                                imageViewEmoji.getWidth();
                                imageViewEmoji.getPaddingLeft();
                                imageViewEmoji.getPaddingRight();
                                imageViewEmoji.getHeight();
                                imageViewEmoji.getPaddingTop();
                                imageViewEmoji.getPaddingBottom();
                                Rect rect2 = AndroidUtilities.rectTmp2;
                                rect2.set(imageViewEmoji.getPaddingLeft(), imageViewEmoji.getPaddingTop(), imageViewEmoji.getWidth() - imageViewEmoji.getPaddingRight(), imageViewEmoji.getHeight() - imageViewEmoji.getPaddingBottom());
                                if (imageViewEmoji.selected) {
                                    rect2.set(Math.round(((float) rect2.centerX()) - ((((float) rect2.width()) / 2.0f) * 0.86f)), Math.round(((float) rect2.centerY()) - ((((float) rect2.height()) / 2.0f) * 0.86f)), Math.round(((float) rect2.centerX()) + ((((float) rect2.width()) / 2.0f) * 0.86f)), Math.round(((float) rect2.centerY()) + ((((float) rect2.height()) / 2.0f) * 0.86f)));
                                }
                                rect2.offset((imageViewEmoji.getLeft() + ((int) imageViewEmoji.getTranslationX())) - this.startOffset, 0);
                                imageViewEmoji.backgroundThreadDrawHolder.setBounds(rect2);
                                imageViewEmoji.skewAlpha = 1.0f;
                                imageViewEmoji.skewIndex = i;
                                this.drawInBackgroundViews.add(imageViewEmoji);
                            }
                        }
                    }
                }
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
                                int access$5200 = SelectAnimatedEmojiDialog.this.animateExpandToPosition - SelectAnimatedEmojiDialog.this.animateExpandFromPosition;
                                if (childAdapterPosition >= 0 && childAdapterPosition < access$5200) {
                                    float clamp = MathUtils.clamp(((float) (SystemClock.elapsedRealtime() - SelectAnimatedEmojiDialog.this.animateExpandStartTime)) / ((float) SelectAnimatedEmojiDialog.this.animateExpandAppearDuration()), 0.0f, 1.0f);
                                    float f3 = (float) childAdapterPosition;
                                    float f4 = (float) access$5200;
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
                            } else if (imageViewEmoji.span != null && !imageViewEmoji.notDraw) {
                                drawable = (Drawable) SelectAnimatedEmojiDialog.this.animatedEmojiDrawables.get(imageViewEmoji.span.getDocumentId());
                                if (drawable == null && Math.min(imageViewEmoji.getScaleX(), imageViewEmoji.getScaleY()) * scaleX > 0.0f && imageViewEmoji.getAlpha() * f2 > 0.0f) {
                                    drawable = AnimatedEmojiDrawable.make(SelectAnimatedEmojiDialog.this.currentAccount, SelectAnimatedEmojiDialog.this.getCacheType(), imageViewEmoji.span.getDocumentId());
                                    imageViewEmoji.setDrawable(drawable);
                                    SelectAnimatedEmojiDialog.this.animatedEmojiDrawables.put(imageViewEmoji.span.getDocumentId(), drawable);
                                }
                                if (drawable != null) {
                                    drawable.setAlpha(255);
                                    drawable.setBounds(rect);
                                }
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
            SelectAnimatedEmojiDialog.this.bigReactionImageReceiver.onAttachedToWindow();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            SelectAnimatedEmojiDialog.this.bigReactionImageReceiver.onDetachedFromWindow();
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
        ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda2(this));
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
                SelectAnimatedEmojiDialog.this.updateEmojiDrawables();
            }
        });
        updateShow(0.0f);
        this.showAnimator.setDuration(800);
        this.showAnimator.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onShow$10(ValueAnimator valueAnimator) {
        updateShow(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    static {
        new CubicBezierInterpolator(0.0d, 0.0d, 0.65d, 1.04d);
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
        ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda1(this));
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
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDismiss$11(ValueAnimator valueAnimator) {
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
        for (int i = 0; i < this.lineDrawables.size(); i++) {
            EmojiListView.DrawingInBackgroundLine drawingInBackgroundLine = this.lineDrawables.get(i);
            for (int i2 = 0; i2 < drawingInBackgroundLine.imageViewEmojis.size(); i2++) {
                if (drawingInBackgroundLine.imageViewEmojis.get(i2).notDraw) {
                    drawingInBackgroundLine.imageViewEmojis.get(i2).notDraw = false;
                    drawingInBackgroundLine.imageViewEmojis.get(i2).invalidate();
                    drawingInBackgroundLine.reset();
                }
            }
        }
        this.emojiGridView.invalidate();
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
                        float access$5900 = 1.0f - ((1.0f - SelectStatusDurationDialog.this.imageViewEmoji.skewAlpha) * (1.0f - SelectStatusDurationDialog.this.showT));
                        canvas.save();
                        if (access$5900 < 1.0f) {
                            canvas.translate((float) rect.left, (float) rect.top);
                            canvas.scale(1.0f, access$5900, 0.0f, 0.0f);
                            canvas.skew((1.0f - ((((float) SelectStatusDurationDialog.this.imageViewEmoji.skewIndex) * 2.0f) / ((float) SelectStatusDurationDialog.this.this$0.layoutManager.getSpanCount()))) * (1.0f - access$5900), 0.0f);
                            canvas.translate((float) (-rect.left), (float) (-rect.top));
                        }
                        canvas.clipRect(0.0f, 0.0f, (float) getWidth(), ((float) SelectStatusDurationDialog.this.clipBottom) + (SelectStatusDurationDialog.this.showT * ((float) AndroidUtilities.dp(45.0f))));
                        drawable.setBounds(rect);
                        drawable.draw(canvas);
                        canvas.restore();
                        if (SelectStatusDurationDialog.this.imageViewEmoji.skewIndex == 0) {
                            rect.offset(AndroidUtilities.dp(access$5900 * 8.0f), 0);
                        } else if (SelectStatusDurationDialog.this.imageViewEmoji.skewIndex == 1) {
                            rect.offset(AndroidUtilities.dp(access$5900 * 4.0f), 0);
                        } else if (SelectStatusDurationDialog.this.imageViewEmoji.skewIndex == SelectStatusDurationDialog.this.this$0.layoutManager.getSpanCount() - 2) {
                            rect.offset(-AndroidUtilities.dp(access$5900 * -4.0f), 0);
                        } else if (SelectStatusDurationDialog.this.imageViewEmoji.skewIndex == SelectStatusDurationDialog.this.this$0.layoutManager.getSpanCount() - 1) {
                            rect.offset(AndroidUtilities.dp(access$5900 * -8.0f), 0);
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
                Activity access$7200 = SelectStatusDurationDialog.this.getParentActivity();
                if (access$7200 != null) {
                    View decorView = access$7200.getWindow().getDecorView();
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
