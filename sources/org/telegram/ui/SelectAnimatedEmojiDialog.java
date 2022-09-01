package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
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
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_emojiStatus;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
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
    public int animateExpandFromPosition = -1;
    /* access modifiers changed from: private */
    public long animateExpandStartTime = -1;
    /* access modifiers changed from: private */
    public int animateExpandToPosition = -1;
    /* access modifiers changed from: private */
    public LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables = new LongSparseArray<>();
    AnimatedEmojiDrawable bigReactionAnimatedEmoji;
    ImageReceiver bigReactionImageReceiver = new ImageReceiver();
    public onLongPressedListener bigReactionListener;
    private boolean bottomGradientShown = false;
    private View bottomGradientView;
    private View bubble1View;
    private View bubble2View;
    public boolean cancelPressed;
    private FrameLayout contentView;
    private View contentViewForeground;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public ArrayList<AnimatedEmojiSpan> defaultStatuses = new ArrayList<>();
    private ValueAnimator dimAnimator;
    private Runnable dismiss;
    /* access modifiers changed from: private */
    public boolean drawBackground = true;
    public RecyclerListView emojiGridView;
    private ValueAnimator emojiSelectAnimator;
    private Rect emojiSelectRect;
    /* access modifiers changed from: private */
    public ImageViewEmoji emojiSelectView;
    /* access modifiers changed from: private */
    public EmojiTabsStrip emojiTabs;
    private View emojiTabsShadow;
    private Integer emojiX;
    private ArrayList<Long> expandedEmojiSets = new ArrayList<>();
    private ValueAnimator hideAnimator;
    /* access modifiers changed from: private */
    public boolean includeEmpty = false;
    /* access modifiers changed from: private */
    public ArrayList<Long> installedEmojiSets = new ArrayList<>();
    private boolean isAttached;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    ArrayList<EmojiListView.DrawingInBackgroundLine> lineDrawables = new ArrayList<>();
    ArrayList<EmojiListView.DrawingInBackgroundLine> lineDrawablesTmp = new ArrayList<>();
    private Integer listStateId;
    /* access modifiers changed from: private */
    public int longtapHintRow;
    public onRecentClearedListener onRecentClearedListener;
    private OvershootInterpolator overshootInterpolator = new OvershootInterpolator(2.0f);
    /* access modifiers changed from: private */
    public ArrayList<EmojiView.EmojiPack> packs = new ArrayList<>();
    Paint paint = new Paint();
    /* access modifiers changed from: private */
    public int popularSectionRow;
    /* access modifiers changed from: private */
    public SparseIntArray positionToButton = new SparseIntArray();
    /* access modifiers changed from: private */
    public SparseIntArray positionToExpand = new SparseIntArray();
    /* access modifiers changed from: private */
    public SparseIntArray positionToSection = new SparseIntArray();
    private Drawable premiumStar;
    /* access modifiers changed from: private */
    public ColorFilter premiumStarColorFilter;
    float pressedProgress;
    /* access modifiers changed from: private */
    public ArrayList<AnimatedEmojiSpan> recent = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean recentExpanded = false;
    /* access modifiers changed from: private */
    public ArrayList<ReactionsLayoutInBubble.VisibleReaction> recentReactions = new ArrayList<>();
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
    public ArrayList<Integer> rowHashCodes = new ArrayList<>();
    /* access modifiers changed from: private */
    public float scaleX;
    /* access modifiers changed from: private */
    public float scaleY;
    private float scrimAlpha = 1.0f;
    private int scrimColor;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable scrimDrawable;
    private View scrimDrawableParent;
    private RecyclerAnimationScrollHelper scrollHelper;
    /* access modifiers changed from: private */
    public SparseIntArray sectionToPosition = new SparseIntArray();
    HashSet<Long> selectedDocumentIds = new HashSet<>();
    ImageViewEmoji selectedReactionView;
    HashSet<ReactionsLayoutInBubble.VisibleReaction> selectedReactions = new HashSet<>();
    public Paint selectorPaint = new Paint(1);
    /* access modifiers changed from: private */
    public ValueAnimator showAnimator;
    /* access modifiers changed from: private */
    public boolean smoothScrolling = false;
    private View topGradientView;
    /* access modifiers changed from: private */
    public ArrayList<ReactionsLayoutInBubble.VisibleReaction> topReactions = new ArrayList<>();
    /* access modifiers changed from: private */
    public int topReactionsEndRow;
    /* access modifiers changed from: private */
    public int topReactionsStartRow;
    /* access modifiers changed from: private */
    public int totalCount;
    private int type;

    public interface onLongPressedListener {
        void onLongPressed(ImageViewEmoji imageViewEmoji);
    }

    public interface onRecentClearedListener {
        void onRecentCleared();
    }

    /* access modifiers changed from: protected */
    public void onEmojiSelected(View view, Long l, TLRPC$Document tLRPC$Document) {
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SelectAnimatedEmojiDialog(Context context, boolean z, Integer num, int i, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        final Integer num2;
        int i2;
        Context context2 = context;
        final boolean z2 = z;
        Integer num3 = num;
        final int i3 = i;
        final Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        this.resourcesProvider = resourcesProvider3;
        this.type = i3;
        this.includeEmpty = z2;
        this.selectorPaint.setColor(Theme.getColor("listSelectorSDK21", resourcesProvider3));
        this.premiumStarColorFilter = new PorterDuffColorFilter(ColorUtils.setAlphaComponent(Theme.getColor("chats_menuItemIcon", resourcesProvider3), 178), PorterDuff.Mode.MULTIPLY);
        this.emojiX = num3;
        if (num3 == null) {
            num2 = null;
        } else {
            num2 = Integer.valueOf(MathUtils.clamp(num.intValue(), AndroidUtilities.dp(26.0f), AndroidUtilities.dp(324.0f)));
        }
        boolean z3 = num2 != null && num2.intValue() > AndroidUtilities.dp(170.0f);
        setFocusableInTouchMode(true);
        if (i3 == 0) {
            setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
            setOnTouchListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda6(this));
            i2 = 16;
        } else {
            i2 = 0;
        }
        if (num2 != null) {
            this.bubble1View = new View(context2);
            Drawable mutate = getResources().getDrawable(R.drawable.shadowed_bubble1).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultSubmenuBackground", resourcesProvider3), PorterDuff.Mode.MULTIPLY));
            this.bubble1View.setBackground(mutate);
            addView(this.bubble1View, LayoutHelper.createFrame(10, 10.0f, 51, (((float) num2.intValue()) / AndroidUtilities.density) + ((float) (z3 ? -12 : 4)), (float) i2, 0.0f, 0.0f));
        }
        AnonymousClass1 r2 = new FrameLayout(context2) {
            private Paint paint = new Paint(1);
            private Path path = new Path();

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                if (!SelectAnimatedEmojiDialog.this.drawBackground) {
                    super.dispatchDraw(canvas);
                    return;
                }
                canvas.save();
                this.paint.setShadowLayer((float) AndroidUtilities.dp(2.0f), 0.0f, (float) AndroidUtilities.dp(-0.66f), NUM);
                this.paint.setColor(Theme.getColor("actionBarDefaultSubmenuBackground", resourcesProvider3));
                this.paint.setAlpha((int) (getAlpha() * 255.0f));
                Integer num = num2;
                float width = (num == null ? ((float) getWidth()) / 2.0f : (float) num.intValue()) + ((float) AndroidUtilities.dp(20.0f));
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(((float) getPaddingLeft()) + (width - (SelectAnimatedEmojiDialog.this.scaleX * width)), (float) getPaddingTop(), ((float) getPaddingLeft()) + width + ((((float) ((getWidth() - getPaddingLeft()) - getPaddingRight())) - width) * SelectAnimatedEmojiDialog.this.scaleX), ((float) getPaddingTop()) + (((float) ((getHeight() - getPaddingBottom()) - getPaddingTop())) * SelectAnimatedEmojiDialog.this.scaleY));
                this.path.rewind();
                this.path.addRoundRect(rectF, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), Path.Direction.CW);
                canvas.drawPath(this.path, this.paint);
                canvas.clipPath(this.path);
                super.dispatchDraw(canvas);
                canvas.restore();
            }
        };
        this.contentView = r2;
        if (i3 == 0) {
            r2.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        }
        addView(this.contentView, LayoutHelper.createFrame(-1, -1.0f, 119, 0.0f, i3 == 0 ? (float) (i2 + 6) : 0.0f, 0.0f, 0.0f));
        if (num2 != null) {
            this.bubble2View = new View(this, context2) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, i2);
                    setPivotX((float) (getMeasuredWidth() / 2));
                    setPivotY((float) getMeasuredHeight());
                }
            };
            Drawable drawable = getResources().getDrawable(R.drawable.shadowed_bubble2_half);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultSubmenuBackground", resourcesProvider3), PorterDuff.Mode.MULTIPLY));
            this.bubble2View.setBackground(drawable);
            addView(this.bubble2View, LayoutHelper.createFrame(17, 9.0f, 51, (((float) num2.intValue()) / AndroidUtilities.density) + ((float) (z3 ? -25 : 10)), (float) (i2 + 5), 0.0f, 0.0f));
        }
        String str = "listSelectorSDK21";
        String str2 = "actionBarDefaultSubmenuBackground";
        AnonymousClass3 r0 = new EmojiTabsStrip(context, (Theme.ResourcesProvider) null, false, true, (Runnable) null) {
            /* access modifiers changed from: protected */
            public boolean onTabClick(int i) {
                int i2 = 0;
                if (SelectAnimatedEmojiDialog.this.smoothScrolling) {
                    return false;
                }
                if (i > 0) {
                    int i3 = i - 1;
                    if (SelectAnimatedEmojiDialog.this.sectionToPosition.indexOfKey(i3) >= 0) {
                        i2 = SelectAnimatedEmojiDialog.this.sectionToPosition.get(i3);
                    }
                }
                SelectAnimatedEmojiDialog.this.scrollToPosition(i2, AndroidUtilities.dp(-2.0f));
                SelectAnimatedEmojiDialog.this.emojiTabs.select(i);
                return true;
            }

            /* access modifiers changed from: protected */
            public void onTabCreate(EmojiTabsStrip.EmojiTabButton emojiTabButton) {
                if (SelectAnimatedEmojiDialog.this.showAnimator == null || SelectAnimatedEmojiDialog.this.showAnimator.isRunning()) {
                    emojiTabButton.setScaleX(0.0f);
                    emojiTabButton.setScaleY(0.0f);
                }
            }
        };
        this.emojiTabs = r0;
        r0.updateButtonDrawables = false;
        r0.setAnimatedEmojiCacheType(5);
        EmojiTabsStrip emojiTabsStrip = this.emojiTabs;
        final Integer num4 = num2;
        emojiTabsStrip.animateAppear = num4 == null;
        emojiTabsStrip.setPaddingLeft(5.0f);
        this.contentView.addView(this.emojiTabs, LayoutHelper.createFrame(-1, 36.0f));
        AnonymousClass4 r02 = new View(this, context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                Integer num = num4;
                if (num != null) {
                    setPivotX((float) num.intValue());
                }
            }
        };
        this.emojiTabsShadow = r02;
        r02.setBackgroundColor(Theme.getColor("divider", resourcesProvider3));
        this.contentView.addView(this.emojiTabsShadow, LayoutHelper.createFrame(-1, 1.0f / AndroidUtilities.density, 48, 0.0f, 36.0f, 0.0f, 0.0f));
        this.emojiGridView = new EmojiListView(context2) {
            public void onScrolled(int i, int i2) {
                super.onScrolled(i, i2);
                SelectAnimatedEmojiDialog.this.checkScroll();
                if (!SelectAnimatedEmojiDialog.this.smoothScrolling) {
                    SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = SelectAnimatedEmojiDialog.this;
                    selectAnimatedEmojiDialog.updateTabsPosition(selectAnimatedEmojiDialog.layoutManager.findFirstCompletelyVisibleItemPosition());
                }
            }

            public void onScrollStateChanged(int i) {
                if (i == 0) {
                    boolean unused = SelectAnimatedEmojiDialog.this.smoothScrolling = false;
                }
                super.onScrollStateChanged(i);
            }
        };
        AnonymousClass6 r03 = new DefaultItemAnimator(this) {
            /* access modifiers changed from: protected */
            public float animateByScale(View view) {
                return view instanceof EmojiPackExpand ? 0.6f : 0.0f;
            }
        };
        r03.setAddDuration(220);
        r03.setMoveDuration(260);
        r03.setChangeDuration(160);
        r03.setSupportsChangeAnimations(false);
        r03.setMoveInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        r03.setDelayAnimations(false);
        this.emojiGridView.setItemAnimator(r03);
        this.emojiGridView.setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(2.0f));
        this.emojiGridView.setClipToPadding(false);
        RecyclerListView recyclerListView = this.emojiGridView;
        Adapter adapter2 = new Adapter();
        this.adapter = adapter2;
        recyclerListView.setAdapter(adapter2);
        RecyclerListView recyclerListView2 = this.emojiGridView;
        AnonymousClass7 r1 = new GridLayoutManager(context2, 8) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                try {
                    AnonymousClass1 r3 = new LinearSmoothScrollerCustom(recyclerView.getContext(), 2) {
                        public void onEnd() {
                            boolean unused = SelectAnimatedEmojiDialog.this.smoothScrolling = false;
                        }
                    };
                    r3.setTargetPosition(i);
                    startSmoothScroll(r3);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        };
        this.layoutManager = r1;
        recyclerListView2.setLayoutManager(r1);
        this.emojiGridView.setSelectorRadius(AndroidUtilities.dp(4.0f));
        this.emojiGridView.setSelectorDrawableColor(Theme.getColor(str, resourcesProvider3));
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                if (SelectAnimatedEmojiDialog.this.positionToSection.indexOfKey(i) >= 0 || SelectAnimatedEmojiDialog.this.positionToButton.indexOfKey(i) >= 0 || i == SelectAnimatedEmojiDialog.this.recentReactionsSectionRow || i == SelectAnimatedEmojiDialog.this.popularSectionRow || i == SelectAnimatedEmojiDialog.this.longtapHintRow) {
                    return SelectAnimatedEmojiDialog.this.layoutManager.getSpanCount();
                }
                return 1;
            }
        });
        this.contentView.addView(this.emojiGridView, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, (1.0f / AndroidUtilities.density) + 36.0f, 0.0f, 0.0f));
        RecyclerAnimationScrollHelper recyclerAnimationScrollHelper = new RecyclerAnimationScrollHelper(this.emojiGridView, this.layoutManager);
        this.scrollHelper = recyclerAnimationScrollHelper;
        recyclerAnimationScrollHelper.setAnimationCallback(new RecyclerAnimationScrollHelper.AnimationCallback() {
            public void onPreAnimation() {
                boolean unused = SelectAnimatedEmojiDialog.this.smoothScrolling = true;
            }

            public void onEndAnimation() {
                boolean unused = SelectAnimatedEmojiDialog.this.smoothScrolling = false;
            }
        });
        this.emojiGridView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListenerExtended) new RecyclerListView.OnItemLongClickListenerExtended() {
            public /* synthetic */ void onMove(float f, float f2) {
                RecyclerListView.OnItemLongClickListenerExtended.CC.$default$onMove(this, f, f2);
            }

            public boolean onItemClick(View view, int i, float f, float f2) {
                View view2 = view;
                if (!(view2 instanceof ImageViewEmoji) || i3 != 1) {
                    int spanCount = SelectAnimatedEmojiDialog.this.layoutManager.getSpanCount() * 5;
                    if (SelectAnimatedEmojiDialog.this.recent.size() <= spanCount || SelectAnimatedEmojiDialog.this.recentExpanded) {
                        spanCount = SelectAnimatedEmojiDialog.this.recent.size();
                    }
                    boolean z = z2;
                    if (i - (z ? 1 : 0) >= spanCount || (i == 0 && z)) {
                        return false;
                    }
                    SelectAnimatedEmojiDialog.this.onRecentLongClick();
                    return true;
                }
                SelectAnimatedEmojiDialog.this.performHapticFeedback(0);
                ImageViewEmoji imageViewEmoji = (ImageViewEmoji) view2;
                if (imageViewEmoji.isDefaultReaction || UserConfig.getInstance(SelectAnimatedEmojiDialog.this.currentAccount).isPremium()) {
                    SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = SelectAnimatedEmojiDialog.this;
                    selectAnimatedEmojiDialog.selectedReactionView = imageViewEmoji;
                    selectAnimatedEmojiDialog.pressedProgress = 0.0f;
                    selectAnimatedEmojiDialog.cancelPressed = false;
                    if (imageViewEmoji.isDefaultReaction) {
                        TLRPC$TL_availableReaction tLRPC$TL_availableReaction = MediaDataController.getInstance(selectAnimatedEmojiDialog.currentAccount).getReactionsMap().get(SelectAnimatedEmojiDialog.this.selectedReactionView.reaction.emojicon);
                        if (tLRPC$TL_availableReaction != null) {
                            SelectAnimatedEmojiDialog.this.bigReactionImageReceiver.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.select_animation), "60_60_pcache", (ImageLocation) null, (String) null, (Drawable) null, 0, "tgs", SelectAnimatedEmojiDialog.this.selectedReactionView.reaction, 0);
                        }
                    } else {
                        selectAnimatedEmojiDialog.setBigReactionAnimatedEmoji(new AnimatedEmojiDrawable(4, SelectAnimatedEmojiDialog.this.currentAccount, SelectAnimatedEmojiDialog.this.selectedReactionView.span.documentId));
                    }
                    SelectAnimatedEmojiDialog.this.emojiGridView.invalidate();
                    return true;
                }
                TLRPC$Document tLRPC$Document = imageViewEmoji.span.document;
                if (tLRPC$Document == null) {
                    tLRPC$Document = AnimatedEmojiDrawable.findDocument(SelectAnimatedEmojiDialog.this.currentAccount, imageViewEmoji.span.documentId);
                }
                SelectAnimatedEmojiDialog.this.onEmojiSelected(imageViewEmoji, Long.valueOf(imageViewEmoji.span.documentId), tLRPC$Document);
                return true;
            }

            public void onLongClickRelease() {
                SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = SelectAnimatedEmojiDialog.this;
                if (selectAnimatedEmojiDialog.selectedReactionView != null) {
                    selectAnimatedEmojiDialog.cancelPressed = true;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{selectAnimatedEmojiDialog.pressedProgress, 0.0f});
                    ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$10$$ExternalSyntheticLambda0(this));
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = SelectAnimatedEmojiDialog.this;
                            selectAnimatedEmojiDialog.selectedReactionView.bigReactionSelectedProgress = 0.0f;
                            selectAnimatedEmojiDialog.selectedReactionView = null;
                            selectAnimatedEmojiDialog.emojiGridView.invalidate();
                        }
                    });
                    ofFloat.setDuration(150);
                    ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    ofFloat.start();
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onLongClickRelease$0(ValueAnimator valueAnimator) {
                SelectAnimatedEmojiDialog.this.pressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            }
        });
        this.emojiGridView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda10(this, i3));
        this.topGradientView = new View(this, context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                Integer num = num4;
                if (num != null) {
                    setPivotX((float) num.intValue());
                }
            }
        };
        Drawable drawable2 = getResources().getDrawable(R.drawable.gradient_top);
        String str3 = str2;
        drawable2.setColorFilter(new PorterDuffColorFilter(AndroidUtilities.multiplyAlphaComponent(Theme.getColor(str3, resourcesProvider3), 0.8f), PorterDuff.Mode.SRC_IN));
        this.topGradientView.setBackground(drawable2);
        this.topGradientView.setAlpha(0.0f);
        this.contentView.addView(this.topGradientView, LayoutHelper.createFrame(-1, 20.0f, 55, 0.0f, (1.0f / AndroidUtilities.density) + 36.0f, 0.0f, 0.0f));
        this.bottomGradientView = new View(context2);
        Drawable drawable3 = getResources().getDrawable(R.drawable.gradient_bottom);
        drawable3.setColorFilter(new PorterDuffColorFilter(AndroidUtilities.multiplyAlphaComponent(Theme.getColor(str3, resourcesProvider3), 0.8f), PorterDuff.Mode.SRC_IN));
        this.bottomGradientView.setBackground(drawable3);
        this.bottomGradientView.setAlpha(0.0f);
        this.contentView.addView(this.bottomGradientView, LayoutHelper.createFrame(-1, 20, 87));
        View view = new View(context2);
        this.contentViewForeground = view;
        view.setAlpha(0.0f);
        this.contentViewForeground.setBackgroundColor(-16777216);
        this.contentView.addView(this.contentViewForeground, LayoutHelper.createFrame(-1, -1.0f));
        Emoji.loadRecentEmoji();
        MediaDataController.getInstance(UserConfig.selectedAccount).checkStickers(5);
        MediaDataController.getInstance(UserConfig.selectedAccount).fetchEmojiStatuses(0, true);
        MediaDataController.getInstance(UserConfig.selectedAccount).checkReactions();
        this.bigReactionImageReceiver.setLayerNum(7);
        updateRows(true);
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
    public /* synthetic */ void lambda$new$1(int i, View view, int i2) {
        if (view instanceof ImageViewEmoji) {
            ImageViewEmoji imageViewEmoji = (ImageViewEmoji) view;
            if (imageViewEmoji.isDefaultReaction) {
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

    /* access modifiers changed from: private */
    public void setBigReactionAnimatedEmoji(AnimatedEmojiDrawable animatedEmojiDrawable) {
        AnimatedEmojiDrawable animatedEmojiDrawable2;
        if (this.isAttached && (animatedEmojiDrawable2 = this.bigReactionAnimatedEmoji) != animatedEmojiDrawable) {
            if (animatedEmojiDrawable2 != null) {
                animatedEmojiDrawable2.removeView((Drawable.Callback) this);
            }
            this.bigReactionAnimatedEmoji = animatedEmojiDrawable;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.addView((View) this);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onRecentLongClick() {
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
    public /* synthetic */ void lambda$onRecentLongClick$2(DialogInterface dialogInterface, int i) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_clearRecentEmojiStatuses(), (RequestDelegate) null);
        MediaDataController.getInstance(this.currentAccount).clearRecentEmojiStatuses();
        updateRows(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRecentLongClick$3(DialogInterface dialogInterface) {
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
            ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda2(this));
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
    public /* synthetic */ void lambda$setDim$4(ValueAnimator valueAnimator) {
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
        if (!(this.scrimDrawable == null || this.emojiX == null)) {
            canvas.save();
            Rect bounds = this.scrimDrawable.getBounds();
            canvas.translate((float) ((-bounds.centerX()) + this.emojiX.intValue()), ((((float) (-bounds.top)) - (((float) bounds.height()) / 2.0f)) - getTranslationY()) + ((float) AndroidUtilities.dp(16.0f)));
            int alpha = Build.VERSION.SDK_INT >= 19 ? this.scrimDrawable.getAlpha() : 255;
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.scrimDrawable;
            double d = (double) alpha;
            double pow = Math.pow((double) this.contentView.getAlpha(), 0.25d);
            Double.isNaN(d);
            double d2 = d * pow;
            double d3 = (double) this.scrimAlpha;
            Double.isNaN(d3);
            swapAnimatedEmojiDrawable.setAlpha((int) (d2 * d3));
            View view = this.scrimDrawableParent;
            float scaleY2 = view == null ? 1.0f : view.getScaleY();
            this.scrimDrawable.setBounds((int) (((float) bounds.centerX()) - ((((float) bounds.width()) / 2.0f) * scaleY2)), bounds.bottom - ((int) (((float) bounds.height()) * scaleY2)), (int) (((float) bounds.centerX()) + ((((float) bounds.width()) / 2.0f) * scaleY2)), bounds.bottom);
            this.scrimDrawable.draw(canvas);
            this.scrimDrawable.setAlpha(alpha);
            canvas.restore();
        }
        super.dispatchDraw(canvas);
        if (this.emojiSelectView != null && this.emojiSelectRect != null) {
            canvas.save();
            canvas.translate(0.0f, -getTranslationY());
            this.emojiSelectView.drawable.setAlpha(255);
            this.emojiSelectView.drawable.setBounds(this.emojiSelectRect);
            this.emojiSelectView.drawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(Theme.getColor("premiumGradient1", this.resourcesProvider), this.scrimColor, 1.0f - this.scrimAlpha), PorterDuff.Mode.MULTIPLY));
            this.emojiSelectView.drawable.draw(canvas);
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
        Rect rect2 = new Rect();
        rect2.set(this.scrimDrawable.getBounds());
        rect2.offset((-rect2.centerX()) + this.emojiX.intValue(), (int) ((((float) (-rect2.top)) - (((float) rect2.height()) / 2.0f)) + ((float) AndroidUtilities.dp(16.0f))));
        View view = this.scrimDrawableParent;
        float scaleY2 = view == null ? 1.0f : view.getScaleY();
        rect2.set((int) (((float) rect2.centerX()) - ((((float) rect2.width()) / 2.0f) * scaleY2)), rect2.bottom - ((int) (((float) rect2.height()) * scaleY2)), (int) (((float) rect2.centerX()) + ((((float) rect2.width()) / 2.0f) * scaleY2)), rect2.bottom);
        Drawable drawable = imageViewEmoji.drawable;
        AnimatedEmojiDrawable make = drawable instanceof AnimatedEmojiDrawable ? AnimatedEmojiDrawable.make(this.currentAccount, 7, ((AnimatedEmojiDrawable) drawable).getDocumentId()) : null;
        this.emojiSelectView = imageViewEmoji;
        Rect rect3 = new Rect();
        this.emojiSelectRect = rect3;
        rect3.set(rect);
        final boolean[] zArr = new boolean[1];
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.emojiSelectAnimator = ofFloat;
        ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda3(this, rect, rect2, imageViewEmoji, zArr, runnable, make));
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
    public /* synthetic */ void lambda$animateEmojiSelect$5(Rect rect, Rect rect2, ImageViewEmoji imageViewEmoji, boolean[] zArr, Runnable runnable, AnimatedEmojiDrawable animatedEmojiDrawable, ValueAnimator valueAnimator) {
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable;
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.scrimAlpha = 1.0f - ((floatValue * floatValue) * floatValue);
        AndroidUtilities.lerp(rect, rect2, floatValue, this.emojiSelectRect);
        float max = Math.max(1.0f, this.overshootInterpolator.getInterpolation(MathUtils.clamp((3.0f * floatValue) - 2.0f, 0.0f, 1.0f))) * imageViewEmoji.getScaleX();
        Rect rect3 = this.emojiSelectRect;
        rect3.set((int) (((float) rect3.centerX()) - ((((float) this.emojiSelectRect.width()) / 2.0f) * max)), (int) (((float) this.emojiSelectRect.centerY()) - ((((float) this.emojiSelectRect.height()) / 2.0f) * max)), (int) (((float) this.emojiSelectRect.centerX()) + ((((float) this.emojiSelectRect.width()) / 2.0f) * max)), (int) (((float) this.emojiSelectRect.centerY()) + ((((float) this.emojiSelectRect.height()) / 2.0f) * max)));
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
                goto L_0x008b
            L_0x000f:
                r2 = 2
                if (r3 != r2) goto L_0x001e
                android.widget.ImageView r2 = new android.widget.ImageView
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r3 = r3.getContext()
                r2.<init>(r3)
                goto L_0x008b
            L_0x001e:
                r2 = 3
                if (r3 != r2) goto L_0x002d
                org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r0 = r3.getContext()
                r2.<init>(r0)
                goto L_0x008b
            L_0x002d:
                r2 = 4
                if (r3 != r2) goto L_0x003d
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackExpand r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackExpand
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r3 = r3.getContext()
                r0 = 0
                r2.<init>(r3, r0)
                goto L_0x008b
            L_0x003d:
                r2 = 5
                if (r3 != r2) goto L_0x004c
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackButton r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackButton
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r0 = r3.getContext()
                r2.<init>(r3, r0)
                goto L_0x008b
            L_0x004c:
                r2 = 6
                if (r3 != r2) goto L_0x0080
                org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$1 r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$1
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r3 = r3.getContext()
                r2.<init>(r1, r3)
                r3 = 1
                r0 = 1095761920(0x41500000, float:13.0)
                r2.setTextSize(r3, r0)
                int r3 = org.telegram.messenger.R.string.ReactionsLongtapHint
                java.lang.String r0 = "ReactionsLongtapHint"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r0, r3)
                r2.setText(r3)
                r3 = 17
                r2.setGravity(r3)
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r3.resourcesProvider
                java.lang.String r0 = "windowBackgroundWhiteGrayText"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
                r2.setTextColor(r3)
                goto L_0x008b
            L_0x0080:
                org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.content.Context r0 = r3.getContext()
                r2.<init>(r0)
            L_0x008b:
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.animation.ValueAnimator r3 = r3.showAnimator
                if (r3 == 0) goto L_0x00a6
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.animation.ValueAnimator r3 = r3.showAnimator
                boolean r3 = r3.isRunning()
                if (r3 == 0) goto L_0x00a6
                r3 = 0
                r2.setScaleX(r3)
                r2.setScaleY(r3)
            L_0x00a6:
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v45, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: org.telegram.ui.Components.EmojiView$EmojiPack} */
        /* JADX WARNING: Code restructure failed: missing block: B:103:0x0382, code lost:
            if (r0.this$0.selectedDocumentIds.contains(java.lang.Long.valueOf(r2.getDocumentId())) != false) goto L_0x0341;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:126:0x0403, code lost:
            if (r0.this$0.selectedDocumentIds.contains(java.lang.Long.valueOf(r2.getDocumentId())) != false) goto L_0x0341;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:91:0x033f, code lost:
            if (r0.this$0.selectedDocumentIds.contains(java.lang.Long.valueOf(r2.getDocumentId())) != false) goto L_0x0341;
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
                if (r3 != r4) goto L_0x002e
                return
            L_0x002e:
                r4 = 8
                r6 = 0
                r7 = 0
                r8 = 1
                if (r3 != 0) goto L_0x00b0
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$HeaderView r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.HeaderView) r1
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.recentReactionsSectionRow
                if (r2 != r3) goto L_0x005c
                int r2 = org.telegram.messenger.R.string.RecentlyUsed
                java.lang.String r3 = "RecentlyUsed"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2, r7)
                android.widget.ImageView r2 = r1.closeIcon
                r2.setVisibility(r7)
                android.widget.ImageView r1 = r1.closeIcon
                org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$$ExternalSyntheticLambda0
                r2.<init>(r0)
                r1.setOnClickListener(r2)
                return
            L_0x005c:
                android.widget.ImageView r3 = r1.closeIcon
                r3.setVisibility(r4)
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.popularSectionRow
                if (r2 != r3) goto L_0x0075
                int r2 = org.telegram.messenger.R.string.PopularReactions
                java.lang.String r3 = "PopularReactions"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2, r7)
                return
            L_0x0075:
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r3 = r3.positionToSection
                int r2 = r3.get(r2)
                if (r2 < 0) goto L_0x00ab
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                java.lang.Object r2 = r3.get(r2)
                org.telegram.ui.Components.EmojiView$EmojiPack r2 = (org.telegram.ui.Components.EmojiView.EmojiPack) r2
                org.telegram.tgnet.TLRPC$StickerSet r3 = r2.set
                java.lang.String r3 = r3.title
                boolean r2 = r2.free
                if (r2 != 0) goto L_0x00a6
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r2 = r2.currentAccount
                org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
                boolean r2 = r2.isPremium()
                if (r2 != 0) goto L_0x00a6
                r7 = 1
            L_0x00a6:
                r1.setText(r3, r7)
                goto L_0x040a
            L_0x00ab:
                r1.setText(r6, r7)
                goto L_0x040a
            L_0x00b0:
                r9 = 3
                if (r3 != r9) goto L_0x01ac
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r1
                r1.position = r2
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.recentReactionsStartRow
                if (r2 < r3) goto L_0x00dd
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.recentReactionsEndRow
                if (r2 >= r3) goto L_0x00dd
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.recentReactionsStartRow
                int r2 = r2 - r3
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.recentReactions
                java.lang.Object r2 = r3.get(r2)
                org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r2 = (org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction) r2
                goto L_0x00f0
            L_0x00dd:
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.topReactionsStartRow
                int r2 = r2 - r3
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.topReactions
                java.lang.Object r2 = r3.get(r2)
                org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r2 = (org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.VisibleReaction) r2
            L_0x00f0:
                org.telegram.messenger.ImageReceiver r3 = r1.imageReceiver
                if (r3 != 0) goto L_0x0104
                org.telegram.messenger.ImageReceiver r3 = new org.telegram.messenger.ImageReceiver
                r3.<init>()
                r1.imageReceiver = r3
                r5 = 7
                r3.setLayerNum(r5)
                org.telegram.messenger.ImageReceiver r3 = r1.imageReceiver
                r3.onAttachedToWindow()
            L_0x0104:
                r1.reaction = r2
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction> r3 = r3.selectedReactions
                boolean r3 = r3.contains(r2)
                r1.setViewSelected(r3)
                java.lang.String r3 = r2.emojicon
                if (r3 == 0) goto L_0x0167
                r1.isDefaultReaction = r8
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r3 = r3.currentAccount
                org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
                java.util.HashMap r3 = r3.getReactionsMap()
                java.lang.String r5 = r2.emojicon
                java.lang.Object r3 = r3.get(r5)
                org.telegram.tgnet.TLRPC$TL_availableReaction r3 = (org.telegram.tgnet.TLRPC$TL_availableReaction) r3
                if (r3 == 0) goto L_0x0152
                org.telegram.tgnet.TLRPC$Document r5 = r3.activate_animation
                r7 = 1045220557(0x3e4ccccd, float:0.2)
                java.lang.String r8 = "windowBackgroundWhiteGrayIcon"
                org.telegram.messenger.SvgHelper$SvgDrawable r14 = org.telegram.messenger.DocumentObject.getSvgThumb((org.telegram.tgnet.TLRPC$Document) r5, (java.lang.String) r8, (float) r7)
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
                goto L_0x0157
            L_0x0152:
                org.telegram.messenger.ImageReceiver r2 = r1.imageReceiver
                r2.clearImage()
            L_0x0157:
                r1.span = r6
                org.telegram.ui.Components.Premium.PremiumLockIconView r2 = r1.premiumLockIconView
                if (r2 == 0) goto L_0x040a
                r2.setVisibility(r4)
                org.telegram.ui.Components.Premium.PremiumLockIconView r1 = r1.premiumLockIconView
                r1.setImageReceiver(r6)
                goto L_0x040a
            L_0x0167:
                r1.isDefaultReaction = r7
                org.telegram.ui.Components.AnimatedEmojiSpan r3 = new org.telegram.ui.Components.AnimatedEmojiSpan
                long r4 = r2.documentId
                r3.<init>((long) r4, (android.graphics.Paint.FontMetricsInt) r6)
                r1.span = r3
                org.telegram.messenger.ImageReceiver r2 = r1.imageReceiver
                r2.clearImage()
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r2 = r2.currentAccount
                org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
                boolean r2 = r2.isPremium()
                if (r2 != 0) goto L_0x040a
                org.telegram.ui.Components.Premium.PremiumLockIconView r2 = r1.premiumLockIconView
                if (r2 != 0) goto L_0x01a5
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
            L_0x01a5:
                org.telegram.ui.Components.Premium.PremiumLockIconView r1 = r1.premiumLockIconView
                r1.setVisibility(r7)
                goto L_0x040a
            L_0x01ac:
                r4 = 4
                r10 = 5
                if (r3 != r4) goto L_0x023f
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackExpand r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.EmojiPackExpand) r1
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r3 = r3.positionToExpand
                int r2 = r3.get(r2)
                if (r2 < 0) goto L_0x01d9
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                int r3 = r3.size()
                if (r2 >= r3) goto L_0x01d9
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                java.lang.Object r3 = r3.get(r2)
                r6 = r3
                org.telegram.ui.Components.EmojiView$EmojiPack r6 = (org.telegram.ui.Components.EmojiView.EmojiPack) r6
            L_0x01d9:
                r3 = -1
                java.lang.String r4 = "+"
                if (r2 != r3) goto L_0x0213
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
                int r4 = r4 + r8
                r3.append(r4)
                java.lang.String r2 = r3.toString()
                r1.setText(r2)
                goto L_0x040a
            L_0x0213:
                if (r6 == 0) goto L_0x040a
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                androidx.recyclerview.widget.GridLayoutManager r2 = r2.layoutManager
                int r2 = r2.getSpanCount()
                int r2 = r2 * 3
                android.widget.TextView r1 = r1.textView
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r4)
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r4 = r6.documents
                int r4 = r4.size()
                int r4 = r4 - r2
                int r4 = r4 + r8
                r3.append(r4)
                java.lang.String r2 = r3.toString()
                r1.setText(r2)
                goto L_0x040a
            L_0x023f:
                if (r3 != r10) goto L_0x0290
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiPackButton r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.EmojiPackButton) r1
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r3 = r3.positionToButton
                int r2 = r3.get(r2)
                if (r2 < 0) goto L_0x040a
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                int r3 = r3.size()
                if (r2 >= r3) goto L_0x040a
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.packs
                java.lang.Object r3 = r3.get(r2)
                org.telegram.ui.Components.EmojiView$EmojiPack r3 = (org.telegram.ui.Components.EmojiView.EmojiPack) r3
                if (r3 == 0) goto L_0x040a
                org.telegram.tgnet.TLRPC$StickerSet r4 = r3.set
                java.lang.String r4 = r4.title
                boolean r5 = r3.free
                if (r5 != 0) goto L_0x0284
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r5 = r5.currentAccount
                org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
                boolean r5 = r5.isPremium()
                if (r5 != 0) goto L_0x0284
                r7 = 1
            L_0x0284:
                boolean r5 = r3.installed
                org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$$ExternalSyntheticLambda1 r6 = new org.telegram.ui.SelectAnimatedEmojiDialog$Adapter$$ExternalSyntheticLambda1
                r6.<init>(r0, r3, r2)
                r1.set(r4, r7, r5, r6)
                goto L_0x040a
            L_0x0290:
                android.view.View r1 = r1.itemView
                org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r1 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r1
                r1.empty = r7
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
                if (r5 <= r3) goto L_0x02d8
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r5 = r5.recentExpanded
                if (r5 != 0) goto L_0x02d8
                goto L_0x02e9
            L_0x02d8:
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.recent
                int r3 = r3.size()
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r5 = r5.includeEmpty
                int r3 = r3 + r5
            L_0x02e9:
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r5 = r5.includeEmpty
                if (r5 == 0) goto L_0x0316
                if (r2 != 0) goto L_0x0316
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<java.lang.Long> r2 = r2.selectedDocumentIds
                boolean r2 = r2.isEmpty()
                r1.empty = r8
                r3 = 1084227584(0x40a00000, float:5.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r1.setPadding(r4, r5, r7, r3)
                r1.span = r6
                goto L_0x0407
            L_0x0316:
                if (r2 >= r3) goto L_0x0345
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r3 = r3.recent
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                boolean r4 = r4.includeEmpty
                int r2 = r2 - r4
                java.lang.Object r2 = r3.get(r2)
                org.telegram.ui.Components.AnimatedEmojiSpan r2 = (org.telegram.ui.Components.AnimatedEmojiSpan) r2
                r1.span = r2
                if (r2 == 0) goto L_0x0342
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<java.lang.Long> r3 = r3.selectedDocumentIds
                long r4 = r2.getDocumentId()
                java.lang.Long r2 = java.lang.Long.valueOf(r4)
                boolean r2 = r3.contains(r2)
                if (r2 == 0) goto L_0x0342
            L_0x0341:
                r7 = 1
            L_0x0342:
                r2 = r7
                goto L_0x0407
            L_0x0345:
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r5 = r5.defaultStatuses
                boolean r5 = r5.isEmpty()
                if (r5 != 0) goto L_0x0385
                int r3 = r2 - r3
                int r3 = r3 - r8
                if (r3 < 0) goto L_0x0385
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r5 = r5.defaultStatuses
                int r5 = r5.size()
                if (r3 >= r5) goto L_0x0385
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r2 = r2.defaultStatuses
                java.lang.Object r2 = r2.get(r3)
                org.telegram.ui.Components.AnimatedEmojiSpan r2 = (org.telegram.ui.Components.AnimatedEmojiSpan) r2
                r1.span = r2
                if (r2 == 0) goto L_0x0342
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<java.lang.Long> r3 = r3.selectedDocumentIds
                long r4 = r2.getDocumentId()
                java.lang.Long r2 = java.lang.Long.valueOf(r4)
                boolean r2 = r3.contains(r2)
                if (r2 == 0) goto L_0x0342
                goto L_0x0341
            L_0x0385:
                r3 = 0
            L_0x0386:
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r5 = r5.positionToSection
                int r5 = r5.size()
                if (r3 >= r5) goto L_0x03ef
                org.telegram.ui.SelectAnimatedEmojiDialog r5 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r5 = r5.positionToSection
                int r5 = r5.keyAt(r3)
                org.telegram.ui.SelectAnimatedEmojiDialog r9 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r9 = r9.defaultStatuses
                boolean r9 = r9.isEmpty()
                r9 = r9 ^ r8
                int r9 = r3 - r9
                if (r9 < 0) goto L_0x03b8
                org.telegram.ui.SelectAnimatedEmojiDialog r10 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.ArrayList r10 = r10.packs
                java.lang.Object r9 = r10.get(r9)
                org.telegram.ui.Components.EmojiView$EmojiPack r9 = (org.telegram.ui.Components.EmojiView.EmojiPack) r9
                goto L_0x03b9
            L_0x03b8:
                r9 = r6
            L_0x03b9:
                if (r9 != 0) goto L_0x03bc
                goto L_0x03ec
            L_0x03bc:
                boolean r10 = r9.expanded
                if (r10 == 0) goto L_0x03c7
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r10 = r9.documents
                int r10 = r10.size()
                goto L_0x03d1
            L_0x03c7:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r10 = r9.documents
                int r10 = r10.size()
                int r10 = java.lang.Math.min(r10, r4)
            L_0x03d1:
                if (r2 <= r5) goto L_0x03ec
                int r11 = r5 + 1
                int r11 = r11 + r10
                if (r2 > r11) goto L_0x03ec
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r9.documents
                int r5 = r2 - r5
                int r5 = r5 - r8
                java.lang.Object r5 = r9.get(r5)
                org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
                if (r5 == 0) goto L_0x03ec
                org.telegram.ui.Components.AnimatedEmojiSpan r9 = new org.telegram.ui.Components.AnimatedEmojiSpan
                r9.<init>((org.telegram.tgnet.TLRPC$Document) r5, (android.graphics.Paint.FontMetricsInt) r6)
                r1.span = r9
            L_0x03ec:
                int r3 = r3 + 1
                goto L_0x0386
            L_0x03ef:
                org.telegram.ui.Components.AnimatedEmojiSpan r2 = r1.span
                if (r2 == 0) goto L_0x0342
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                java.util.HashSet<java.lang.Long> r3 = r3.selectedDocumentIds
                long r4 = r2.getDocumentId()
                java.lang.Long r2 = java.lang.Long.valueOf(r4)
                boolean r2 = r3.contains(r2)
                if (r2 == 0) goto L_0x0342
                goto L_0x0341
            L_0x0407:
                r1.setViewSelected(r2)
            L_0x040a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SelectAnimatedEmojiDialog.Adapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBindViewHolder$0(View view) {
            SelectAnimatedEmojiDialog.this.clearRecent();
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0047, code lost:
            r1 = r6.this$0.emojiGridView.getChildAt(r0);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$onBindViewHolder$1(org.telegram.ui.Components.EmojiView.EmojiPack r7, int r8, android.view.View r9) {
            /*
                r6 = this;
                boolean r9 = r7.free
                if (r9 != 0) goto L_0x002e
                org.telegram.ui.SelectAnimatedEmojiDialog r9 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r9 = r9.currentAccount
                org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r9)
                boolean r9 = r9.isPremium()
                if (r9 != 0) goto L_0x002e
                org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet r7 = new org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet
                r1 = 0
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
            L_0x002e:
                r9 = 0
                r0 = 0
            L_0x0030:
                org.telegram.ui.SelectAnimatedEmojiDialog r1 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.Components.RecyclerListView r1 = r1.emojiGridView
                int r1 = r1.getChildCount()
                r2 = 0
                if (r0 >= r1) goto L_0x006d
                org.telegram.ui.SelectAnimatedEmojiDialog r1 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.Components.RecyclerListView r1 = r1.emojiGridView
                android.view.View r1 = r1.getChildAt(r0)
                boolean r1 = r1 instanceof org.telegram.ui.SelectAnimatedEmojiDialog.EmojiPackExpand
                if (r1 == 0) goto L_0x006a
                org.telegram.ui.SelectAnimatedEmojiDialog r1 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.Components.RecyclerListView r1 = r1.emojiGridView
                android.view.View r1 = r1.getChildAt(r0)
                org.telegram.ui.SelectAnimatedEmojiDialog r3 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                org.telegram.ui.Components.RecyclerListView r3 = r3.emojiGridView
                int r3 = r3.getChildAdapterPosition(r1)
                if (r3 < 0) goto L_0x006a
                org.telegram.ui.SelectAnimatedEmojiDialog r4 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                android.util.SparseIntArray r4 = r4.positionToExpand
                int r4 = r4.get(r3)
                if (r4 != r8) goto L_0x006a
                java.lang.Integer r8 = java.lang.Integer.valueOf(r3)
                goto L_0x006f
            L_0x006a:
                int r0 = r0 + 1
                goto L_0x0030
            L_0x006d:
                r8 = r2
                r1 = r8
            L_0x006f:
                if (r8 == 0) goto L_0x007a
                org.telegram.ui.SelectAnimatedEmojiDialog r0 = org.telegram.ui.SelectAnimatedEmojiDialog.this
                int r8 = r8.intValue()
                r0.expand(r8, r1)
            L_0x007a:
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
        ValueAnimator backAnimator;
        public ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder;
        public float bigReactionSelectedProgress;
        public Drawable drawable;
        public Rect drawableBounds;
        public boolean empty = false;
        public ImageReceiver imageReceiver;
        public ImageReceiver imageReceiverToDraw;
        public boolean isDefaultReaction;
        public boolean notDraw = false;
        public int position;
        PremiumLockIconView premiumLockIconView;
        /* access modifiers changed from: private */
        public float pressedProgress;
        public ReactionsLayoutInBubble.VisibleReaction reaction;
        public boolean selected;
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

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            if (this.selected && !this.notDraw) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                rectF.inset((float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f));
                canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), SelectAnimatedEmojiDialog.this.selectorPaint);
            }
            super.dispatchDraw(canvas);
        }
    }

    public void onEmojiClick(View view, AnimatedEmojiSpan animatedEmojiSpan) {
        if (animatedEmojiSpan == null) {
            onEmojiSelected(view, (Long) null, (TLRPC$Document) null);
            return;
        }
        TLRPC$TL_emojiStatus tLRPC$TL_emojiStatus = new TLRPC$TL_emojiStatus();
        tLRPC$TL_emojiStatus.document_id = animatedEmojiSpan.getDocumentId();
        MediaDataController.getInstance(this.currentAccount).pushRecentEmojiStatus(tLRPC$TL_emojiStatus);
        TLRPC$Document tLRPC$Document = animatedEmojiSpan.document;
        if (tLRPC$Document == null) {
            tLRPC$Document = AnimatedEmojiDrawable.findDocument(this.currentAccount, animatedEmojiSpan.documentId);
        }
        if (this.type != 0 || !(view instanceof ImageViewEmoji)) {
            onEmojiSelected(view, Long.valueOf(animatedEmojiSpan.documentId), tLRPC$Document);
        } else {
            animateEmojiSelect((ImageViewEmoji) view, new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda9(this, view, animatedEmojiSpan, tLRPC$Document));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onEmojiClick$6(View view, AnimatedEmojiSpan animatedEmojiSpan, TLRPC$Document tLRPC$Document) {
        onEmojiSelected(view, Long.valueOf(animatedEmojiSpan.documentId), tLRPC$Document);
    }

    /* access modifiers changed from: private */
    public void updateRows(boolean z) {
        int i;
        boolean z2;
        TLRPC$StickerSet tLRPC$StickerSet;
        boolean z3;
        boolean z4;
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
            int i2 = 2;
            if (this.includeEmpty) {
                this.totalCount++;
                this.rowHashCodes.add(2);
            }
            if (this.type == 1) {
                int i3 = this.totalCount;
                this.totalCount = i3 + 1;
                this.longtapHintRow = i3;
                this.rowHashCodes.add(6);
            }
            if (this.recentReactionsToSet != null) {
                this.topReactionsStartRow = this.totalCount;
                ArrayList arrayList4 = new ArrayList();
                arrayList4.addAll(this.recentReactionsToSet);
                for (int i4 = 0; i4 < 16; i4++) {
                    if (!arrayList4.isEmpty()) {
                        this.topReactions.add((ReactionsLayoutInBubble.VisibleReaction) arrayList4.remove(0));
                    }
                }
                for (int i5 = 0; i5 < this.topReactions.size(); i5++) {
                    this.rowHashCodes.add(Integer.valueOf(Arrays.hashCode(new Object[]{-5632, Integer.valueOf(this.topReactions.get(i5).hashCode())})));
                }
                int size = this.totalCount + this.topReactions.size();
                this.totalCount = size;
                this.topReactionsEndRow = size;
                if (!arrayList4.isEmpty()) {
                    int i6 = 0;
                    while (true) {
                        if (i6 >= arrayList4.size()) {
                            z4 = true;
                            break;
                        } else if (((ReactionsLayoutInBubble.VisibleReaction) arrayList4.get(i6)).documentId != 0) {
                            z4 = false;
                            break;
                        } else {
                            i6++;
                        }
                    }
                    if (!z4) {
                        int i7 = this.totalCount;
                        this.totalCount = i7 + 1;
                        this.recentReactionsSectionRow = i7;
                        this.rowHashCodes.add(4);
                    } else if (UserConfig.getInstance(this.currentAccount).isPremium()) {
                        int i8 = this.totalCount;
                        this.totalCount = i8 + 1;
                        this.popularSectionRow = i8;
                        this.rowHashCodes.add(5);
                    }
                    this.recentReactionsStartRow = this.totalCount;
                    this.recentReactions.addAll(arrayList4);
                    for (int i9 = 0; i9 < this.recentReactions.size(); i9++) {
                        ArrayList<Integer> arrayList5 = this.rowHashCodes;
                        Object[] objArr = new Object[2];
                        objArr[0] = Integer.valueOf(z4 ? 4235 : -3142);
                        objArr[1] = Integer.valueOf(this.recentReactions.get(i9).hashCode());
                        arrayList5.add(Integer.valueOf(Arrays.hashCode(objArr)));
                    }
                    int size2 = this.totalCount + this.recentReactions.size();
                    this.totalCount = size2;
                    this.recentReactionsEndRow = size2;
                }
            } else if (this.type == 0) {
                ArrayList<TLRPC$EmojiStatus> recentEmojiStatuses = MediaDataController.getInstance(this.currentAccount).getRecentEmojiStatuses();
                ArrayList<TLRPC$EmojiStatus> defaultEmojiStatuses = MediaDataController.getInstance(this.currentAccount).getDefaultEmojiStatuses();
                if (recentEmojiStatuses != null && !recentEmojiStatuses.isEmpty()) {
                    Iterator<TLRPC$EmojiStatus> it = recentEmojiStatuses.iterator();
                    while (it.hasNext()) {
                        TLRPC$EmojiStatus next = it.next();
                        if (next instanceof TLRPC$TL_emojiStatus) {
                            this.recent.add(new AnimatedEmojiSpan(((TLRPC$TL_emojiStatus) next).document_id, (Paint.FontMetricsInt) null));
                        }
                    }
                }
                if (defaultEmojiStatuses != null && !defaultEmojiStatuses.isEmpty()) {
                    Iterator<TLRPC$EmojiStatus> it2 = defaultEmojiStatuses.iterator();
                    while (it2.hasNext()) {
                        TLRPC$EmojiStatus next2 = it2.next();
                        if (next2 instanceof TLRPC$TL_emojiStatus) {
                            long j = ((TLRPC$TL_emojiStatus) next2).document_id;
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
            int i14 = 0;
            while (true) {
                i = 9211;
                if (i14 >= arrayList.size()) {
                    break;
                }
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
                i14++;
            }
            int spanCount2 = this.layoutManager.getSpanCount() * 3;
            int i16 = 0;
            while (i16 < arrayList2.size()) {
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
                        ArrayList<Integer> arrayList6 = this.rowHashCodes;
                        Object[] objArr2 = new Object[i2];
                        objArr2[0] = Integer.valueOf(i);
                        objArr2[1] = Long.valueOf(tLRPC$TL_stickerSetFullCovered.set.id);
                        arrayList6.add(Integer.valueOf(Arrays.hashCode(objArr2)));
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
                            int i19 = 0;
                            while (i19 < spanCount2 - 1) {
                                ArrayList<Integer> arrayList7 = this.rowHashCodes;
                                Object[] objArr3 = new Object[i2];
                                objArr3[0] = 3212;
                                objArr3[1] = Long.valueOf(emojiPack2.documents.get(i19).id);
                                arrayList7.add(Integer.valueOf(Arrays.hashCode(objArr3)));
                                i19++;
                                i2 = 2;
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
                        i16++;
                        i = 9211;
                        i2 = 2;
                    }
                }
                i16++;
                i = 9211;
                i2 = 2;
            }
            post(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda7(this));
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
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateRows$7() {
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
                boolean z3 = this.includeEmpty;
                i3 = z2 ? this.recent.size() : Math.min((i5 - (z3 ? 1 : 0)) - 2, this.recent.size());
                int size = this.recent.size();
                this.recentExpanded = true;
                num = null;
                boolean z4 = z3;
                i4 = size;
                i2 = z4;
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
                post(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda8(this, num2.intValue() > i5 / 2 ? 1.5f : 3.5f, num.intValue()));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$expand$8(float f, int i) {
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
        return this.type == 0 ? 2 : 3;
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
            super.dispatchDraw(canvas);
            if (!(this.lastChildCount == getChildCount() || SelectAnimatedEmojiDialog.this.showAnimator == null || SelectAnimatedEmojiDialog.this.showAnimator.isRunning())) {
                SelectAnimatedEmojiDialog.this.updateEmojiDrawables();
                this.lastChildCount = getChildCount();
            }
            for (int i = 0; i < this.viewsGroupedByLines.size(); i++) {
                ArrayList valueAt = this.viewsGroupedByLines.valueAt(i);
                valueAt.clear();
                this.unusedArrays.add(valueAt);
            }
            this.viewsGroupedByLines.clear();
            if ((SelectAnimatedEmojiDialog.this.animateExpandStartTime > 0 && SystemClock.elapsedRealtime() - SelectAnimatedEmojiDialog.this.animateExpandStartTime < SelectAnimatedEmojiDialog.this.animateExpandDuration()) && SelectAnimatedEmojiDialog.this.animateExpandFromButton != null) {
                int access$4100 = SelectAnimatedEmojiDialog.this.animateExpandFromPosition;
            }
            if (!(SelectAnimatedEmojiDialog.this.animatedEmojiDrawables == null || SelectAnimatedEmojiDialog.this.emojiGridView == null)) {
                for (int i2 = 0; i2 < SelectAnimatedEmojiDialog.this.emojiGridView.getChildCount(); i2++) {
                    View childAt = SelectAnimatedEmojiDialog.this.emojiGridView.getChildAt(i2);
                    if (childAt instanceof ImageViewEmoji) {
                        ImageViewEmoji imageViewEmoji = (ImageViewEmoji) childAt;
                        imageViewEmoji.updatePressedProgress();
                        int y = SelectAnimatedEmojiDialog.this.smoothScrolling ? (int) childAt.getY() : childAt.getTop();
                        ArrayList arrayList = this.viewsGroupedByLines.get(y);
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
                ImageReceiver imageReceiver;
                long j2 = j;
                this.drawInBackgroundViews.clear();
                for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                    ImageViewEmoji imageViewEmoji = this.imageViewEmojis.get(i);
                    if (!imageViewEmoji.notDraw) {
                        float f2 = 0.7f;
                        float f3 = 1.0f;
                        if (imageViewEmoji.empty) {
                            Drawable access$4500 = SelectAnimatedEmojiDialog.this.getPremiumStar();
                            if (imageViewEmoji.pressedProgress != 0.0f || imageViewEmoji.selected) {
                                if (!imageViewEmoji.selected) {
                                    f2 = imageViewEmoji.pressedProgress;
                                }
                                f3 = 1.0f * (((1.0f - f2) * 0.2f) + 0.8f);
                            }
                            if (access$4500 != null) {
                                access$4500.setAlpha(255);
                                int width = (imageViewEmoji.getWidth() - imageViewEmoji.getPaddingLeft()) - imageViewEmoji.getPaddingRight();
                                int height = (imageViewEmoji.getHeight() - imageViewEmoji.getPaddingTop()) - imageViewEmoji.getPaddingBottom();
                                Rect rect = AndroidUtilities.rectTmp2;
                                float f4 = ((float) width) / 2.0f;
                                float f5 = ((float) height) / 2.0f;
                                rect.set((int) ((((float) imageViewEmoji.getWidth()) / 2.0f) - ((imageViewEmoji.getScaleX() * f4) * f3)), (int) ((((float) imageViewEmoji.getHeight()) / 2.0f) - ((imageViewEmoji.getScaleY() * f5) * f3)), (int) ((((float) imageViewEmoji.getWidth()) / 2.0f) + (f4 * imageViewEmoji.getScaleX() * f3)), (int) ((((float) imageViewEmoji.getHeight()) / 2.0f) + (f5 * imageViewEmoji.getScaleY() * f3)));
                                rect.offset(imageViewEmoji.getLeft() - this.startOffset, 0);
                                if (imageViewEmoji.drawableBounds == null) {
                                    imageViewEmoji.drawableBounds = new Rect();
                                }
                                imageViewEmoji.drawableBounds.set(rect);
                                imageViewEmoji.drawable = access$4500;
                                this.drawInBackgroundViews.add(imageViewEmoji);
                            }
                        } else {
                            if (imageViewEmoji.pressedProgress != 0.0f || imageViewEmoji.selected) {
                                if (!imageViewEmoji.selected) {
                                    f2 = imageViewEmoji.pressedProgress;
                                }
                                f = (((1.0f - f2) * 0.2f) + 0.8f) * 1.0f;
                            } else {
                                f = 1.0f;
                            }
                            if (!(SelectAnimatedEmojiDialog.this.animateExpandStartTime > 0 && SystemClock.elapsedRealtime() - SelectAnimatedEmojiDialog.this.animateExpandStartTime < SelectAnimatedEmojiDialog.this.animateExpandDuration()) || SelectAnimatedEmojiDialog.this.animateExpandFromPosition < 0 || SelectAnimatedEmojiDialog.this.animateExpandToPosition < 0 || SelectAnimatedEmojiDialog.this.animateExpandStartTime <= 0) {
                                f3 = imageViewEmoji.getAlpha();
                            } else {
                                int childAdapterPosition = EmojiListView.this.getChildAdapterPosition(imageViewEmoji) - SelectAnimatedEmojiDialog.this.animateExpandFromPosition;
                                int access$4400 = SelectAnimatedEmojiDialog.this.animateExpandToPosition - SelectAnimatedEmojiDialog.this.animateExpandFromPosition;
                                if (childAdapterPosition >= 0 && childAdapterPosition < access$4400) {
                                    float clamp = MathUtils.clamp(((float) (SystemClock.elapsedRealtime() - SelectAnimatedEmojiDialog.this.animateExpandStartTime)) / ((float) SelectAnimatedEmojiDialog.this.animateExpandAppearDuration()), 0.0f, 1.0f);
                                    float f6 = (float) childAdapterPosition;
                                    float f7 = (float) access$4400;
                                    float f8 = f7 / 4.0f;
                                    float cascade = AndroidUtilities.cascade(clamp, f6, f7, f8);
                                    f *= (this.appearScaleInterpolator.getInterpolation(AndroidUtilities.cascade(clamp, f6, f7, f8)) * 0.5f) + 0.5f;
                                    f3 = 1.0f * cascade;
                                }
                            }
                            if (imageViewEmoji.isDefaultReaction) {
                                imageReceiver = imageViewEmoji.imageReceiver;
                                imageReceiver.setAlpha(f3);
                            } else if (imageViewEmoji.span != null) {
                                AnimatedEmojiDrawable animatedEmojiDrawable = (AnimatedEmojiDrawable) SelectAnimatedEmojiDialog.this.animatedEmojiDrawables.get(imageViewEmoji.span.getDocumentId());
                                if (animatedEmojiDrawable == null && Math.min(imageViewEmoji.getScaleX(), imageViewEmoji.getScaleY()) * f > 0.0f && imageViewEmoji.getAlpha() * f3 > 0.0f) {
                                    animatedEmojiDrawable = AnimatedEmojiDrawable.make(SelectAnimatedEmojiDialog.this.currentAccount, SelectAnimatedEmojiDialog.this.getCacheType(), imageViewEmoji.span.getDocumentId());
                                    animatedEmojiDrawable.setColorFilter(SelectAnimatedEmojiDialog.this.premiumStarColorFilter);
                                    animatedEmojiDrawable.addView((View) EmojiListView.this);
                                    SelectAnimatedEmojiDialog.this.animatedEmojiDrawables.put(imageViewEmoji.span.getDocumentId(), animatedEmojiDrawable);
                                }
                                if (!(animatedEmojiDrawable == null || animatedEmojiDrawable.getImageReceiver() == null)) {
                                    imageReceiver = animatedEmojiDrawable.getImageReceiver();
                                    animatedEmojiDrawable.setAlpha((int) (f3 * 255.0f));
                                    imageViewEmoji.drawable = animatedEmojiDrawable;
                                    animatedEmojiDrawable.setColorFilter(SelectAnimatedEmojiDialog.this.premiumStarColorFilter);
                                }
                            }
                            if (imageReceiver != null) {
                                ImageReceiver.BackgroundThreadDrawHolder drawInBackgroundThread = imageReceiver.setDrawInBackgroundThread(imageViewEmoji.backgroundThreadDrawHolder);
                                imageViewEmoji.backgroundThreadDrawHolder = drawInBackgroundThread;
                                drawInBackgroundThread.time = j2;
                                imageViewEmoji.imageReceiverToDraw = imageReceiver;
                                imageViewEmoji.update(j2);
                                int width2 = (imageViewEmoji.getWidth() - imageViewEmoji.getPaddingLeft()) - imageViewEmoji.getPaddingRight();
                                int height2 = (imageViewEmoji.getHeight() - imageViewEmoji.getPaddingTop()) - imageViewEmoji.getPaddingBottom();
                                Rect rect2 = AndroidUtilities.rectTmp2;
                                float f9 = ((float) width2) / 2.0f;
                                float var_ = ((float) height2) / 2.0f;
                                rect2.set((int) ((((float) imageViewEmoji.getWidth()) / 2.0f) - ((imageViewEmoji.getScaleX() * f9) * f)), (int) ((((float) imageViewEmoji.getHeight()) / 2.0f) - ((imageViewEmoji.getScaleY() * var_) * f)), (int) ((((float) imageViewEmoji.getWidth()) / 2.0f) + (f9 * imageViewEmoji.getScaleX() * f)), (int) ((((float) imageViewEmoji.getHeight()) / 2.0f) + (var_ * imageViewEmoji.getScaleY() * f)));
                                rect2.offset((imageViewEmoji.getLeft() + ((int) imageViewEmoji.getTranslationX())) - this.startOffset, 0);
                                imageViewEmoji.backgroundThreadDrawHolder.setBounds(rect2);
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
                            imageViewEmoji.drawable.draw(canvas);
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
                                int access$4400 = SelectAnimatedEmojiDialog.this.animateExpandToPosition - SelectAnimatedEmojiDialog.this.animateExpandFromPosition;
                                if (childAdapterPosition >= 0 && childAdapterPosition < access$4400) {
                                    float clamp = MathUtils.clamp(((float) (SystemClock.elapsedRealtime() - SelectAnimatedEmojiDialog.this.animateExpandStartTime)) / ((float) SelectAnimatedEmojiDialog.this.animateExpandAppearDuration()), 0.0f, 1.0f);
                                    float f3 = (float) childAdapterPosition;
                                    float f4 = (float) access$4400;
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
                                Drawable access$4500 = SelectAnimatedEmojiDialog.this.getPremiumStar();
                                access$4500.setBounds(rect);
                                access$4500.setAlpha(255);
                                drawable = access$4500;
                            } else if (imageViewEmoji.isDefaultReaction) {
                                ImageReceiver imageReceiver = imageViewEmoji.imageReceiver;
                                if (imageReceiver != null) {
                                    imageReceiver.setImageCoords(rect);
                                }
                            } else if (imageViewEmoji.span != null && !imageViewEmoji.notDraw) {
                                Drawable drawable2 = (Drawable) SelectAnimatedEmojiDialog.this.animatedEmojiDrawables.get(imageViewEmoji.span.getDocumentId());
                                AnimatedEmojiDrawable animatedEmojiDrawable = drawable2;
                                if (drawable2 == null) {
                                    animatedEmojiDrawable = drawable2;
                                    if (Math.min(imageViewEmoji.getScaleX(), imageViewEmoji.getScaleY()) * scaleX > 0.0f) {
                                        animatedEmojiDrawable = drawable2;
                                        if (imageViewEmoji.getAlpha() * f2 > 0.0f) {
                                            AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(SelectAnimatedEmojiDialog.this.currentAccount, SelectAnimatedEmojiDialog.this.getCacheType(), imageViewEmoji.span.getDocumentId());
                                            make.addView((View) EmojiListView.this);
                                            SelectAnimatedEmojiDialog.this.animatedEmojiDrawables.put(imageViewEmoji.span.getDocumentId(), make);
                                            animatedEmojiDrawable = make;
                                        }
                                    }
                                }
                                if (animatedEmojiDrawable != null) {
                                    animatedEmojiDrawable.setAlpha(255);
                                    animatedEmojiDrawable.setBounds(rect);
                                    drawable = animatedEmojiDrawable;
                                }
                            }
                            Drawable drawable3 = imageViewEmoji.drawable;
                            if (drawable3 instanceof AnimatedEmojiDrawable) {
                                drawable3.setColorFilter(SelectAnimatedEmojiDialog.this.premiumStarColorFilter);
                            }
                            if (scaleX != 1.0f || this.skewAlpha < 1.0f) {
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
                    drawable.draw(canvas);
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
            AnimatedEmojiSpan.release((View) this, (LongSparseArray<AnimatedEmojiDrawable>) SelectAnimatedEmojiDialog.this.animatedEmojiDrawables);
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
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setBigReactionAnimatedEmoji((AnimatedEmojiDrawable) null);
        this.isAttached = false;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredEmojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentEmojiStatusesUpdate);
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
        ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda1(this));
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
    public /* synthetic */ void lambda$onShow$9(ValueAnimator valueAnimator) {
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
        ofFloat.addUpdateListener(new SelectAnimatedEmojiDialog$$ExternalSyntheticLambda0(this));
        this.hideAnimator.addListener(new AnimatorListenerAdapter(this) {
            public void onAnimationEnd(Animator animator) {
                runnable.run();
            }
        });
        this.hideAnimator.setDuration(200);
        this.hideAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.hideAnimator.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDismiss$10(ValueAnimator valueAnimator) {
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
        swapAnimatedEmojiDrawable.addParentView(this);
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
}
