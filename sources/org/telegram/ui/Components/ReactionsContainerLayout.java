package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatReactions;
import org.telegram.tgnet.TLRPC$Reaction;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_chatReactionsAll;
import org.telegram.tgnet.TLRPC$TL_chatReactionsNone;
import org.telegram.tgnet.TLRPC$TL_chatReactionsSome;
import org.telegram.tgnet.TLRPC$TL_messageReactions;
import org.telegram.tgnet.TLRPC$TL_reactionCustomEmoji;
import org.telegram.tgnet.TLRPC$TL_reactionEmoji;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumLockIconView;
import org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerListView;

public class ReactionsContainerLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    public static final Property<ReactionsContainerLayout, Float> TRANSITION_PROGRESS_VALUE = new Property<ReactionsContainerLayout, Float>(Float.class, "transitionProgress") {
        public Float get(ReactionsContainerLayout reactionsContainerLayout) {
            return Float.valueOf(reactionsContainerLayout.transitionProgress);
        }

        public void set(ReactionsContainerLayout reactionsContainerLayout, Float f) {
            reactionsContainerLayout.setTransitionProgress(f.floatValue());
        }
    };
    private boolean allReactionsAvailable;
    /* access modifiers changed from: private */
    public boolean allReactionsIsDefault;
    private List<ReactionsLayoutInBubble.VisibleReaction> allReactionsList;
    /* access modifiers changed from: private */
    public final boolean animationEnabled;
    private Paint bgPaint = new Paint(1);
    public int bigCircleOffset;
    private float bigCircleRadius;
    ValueAnimator cancelPressedAnimation;
    /* access modifiers changed from: private */
    public float cancelPressedProgress;
    /* access modifiers changed from: private */
    public boolean clicked;
    /* access modifiers changed from: private */
    public int currentAccount;
    private float customEmojiReactionsEnterProgress;
    /* access modifiers changed from: private */
    public InternalImageView customEmojiReactionsIconView;
    FrameLayout customReactionsContainer;
    /* access modifiers changed from: private */
    public ReactionsContainerDelegate delegate;
    BaseFragment fragment;
    long lastReactionSentTime;
    HashSet<View> lastVisibleViews;
    HashSet<View> lastVisibleViewsTmp;
    /* access modifiers changed from: private */
    public float leftAlpha;
    /* access modifiers changed from: private */
    public Paint leftShadowPaint = new Paint(1);
    /* access modifiers changed from: private */
    public LinearLayoutManager linearLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerView.Adapter listAdapter;
    /* access modifiers changed from: private */
    public int[] location;
    private Path mPath = new Path();
    private MessageObject messageObject;
    public ReactionHolderView nextRecentReaction;
    private float otherViewsScale;
    View popupLayout;
    FrameLayout premiumLockContainer;
    /* access modifiers changed from: private */
    public PremiumLockIconView premiumLockIconView;
    private List<TLRPC$TL_availableReaction> premiumLockedReactions;
    private boolean prepareAnimation;
    /* access modifiers changed from: private */
    public float pressedProgress;
    /* access modifiers changed from: private */
    public ReactionsLayoutInBubble.VisibleReaction pressedReaction;
    /* access modifiers changed from: private */
    public int pressedReactionPosition;
    private float pressedViewScale;
    ValueAnimator pullingDownBackAnimator;
    float pullingLeftOffset;
    public float radius = ((float) AndroidUtilities.dp(72.0f));
    CustomEmojiReactionsWindow reactionsWindow;
    public RectF rect = new RectF();
    public final RecyclerListView recyclerListView;
    Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public float rightAlpha;
    /* access modifiers changed from: private */
    public Paint rightShadowPaint = new Paint(1);
    /* access modifiers changed from: private */
    public Paint selectedPaint;
    HashSet<ReactionsLayoutInBubble.VisibleReaction> selectedReactions;
    private Drawable shadow;
    private Rect shadowPad;
    boolean skipDraw;
    private float smallCircleRadius;
    /* access modifiers changed from: private */
    public float transitionProgress = 1.0f;
    /* access modifiers changed from: private */
    public List<ReactionsLayoutInBubble.VisibleReaction> visibleReactionsList;
    private long waitingLoadingChatId;

    public interface ReactionsContainerDelegate {
        void onReactionClicked(View view, ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z, boolean z2);
    }

    public ReactionsContainerLayout(BaseFragment baseFragment, final Context context, int i, View view, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        boolean z = true;
        float dp = (float) AndroidUtilities.dp(8.0f);
        this.bigCircleRadius = dp;
        this.smallCircleRadius = dp / 2.0f;
        this.bigCircleOffset = AndroidUtilities.dp(36.0f);
        this.visibleReactionsList = new ArrayList(20);
        this.premiumLockedReactions = new ArrayList(10);
        this.allReactionsList = new ArrayList(20);
        this.selectedReactions = new HashSet<>();
        this.location = new int[2];
        this.shadowPad = new Rect();
        new ArrayList();
        this.lastVisibleViews = new HashSet<>();
        this.lastVisibleViewsTmp = new HashSet<>();
        Paint paint = new Paint(1);
        this.selectedPaint = paint;
        paint.setColor(Theme.getColor("listSelectorSDK21", resourcesProvider2));
        this.resourcesProvider = resourcesProvider2;
        this.currentAccount = i;
        this.fragment = baseFragment;
        this.popupLayout = view;
        ReactionHolderView reactionHolderView = new ReactionHolderView(context);
        this.nextRecentReaction = reactionHolderView;
        reactionHolderView.setVisibility(8);
        ReactionHolderView reactionHolderView2 = this.nextRecentReaction;
        reactionHolderView2.touchable = false;
        reactionHolderView2.pressedBackupImageView.setVisibility(8);
        addView(this.nextRecentReaction);
        this.animationEnabled = (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) || SharedConfig.getDevicePerformanceClass() == 0) ? false : z;
        this.shadow = ContextCompat.getDrawable(context, R.drawable.reactions_bubble_shadow).mutate();
        Rect rect2 = this.shadowPad;
        int dp2 = AndroidUtilities.dp(7.0f);
        rect2.bottom = dp2;
        rect2.right = dp2;
        rect2.top = dp2;
        rect2.left = dp2;
        this.shadow.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelShadow"), PorterDuff.Mode.MULTIPLY));
        AnonymousClass2 r5 = new RecyclerListView(context) {
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (ReactionsContainerLayout.this.pressedReaction == null || !(view instanceof ReactionHolderView) || !((ReactionHolderView) view).currentReaction.equals(ReactionsContainerLayout.this.pressedReaction)) {
                    return super.drawChild(canvas, view, j);
                }
                return true;
            }

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    if (motionEvent.getAction() != 1 || ReactionsContainerLayout.this.getPullingLeftProgress() <= 0.95f) {
                        ReactionsContainerLayout.this.animatePullingBack();
                    } else {
                        ReactionsContainerLayout.this.showCustomEmojiReactionDialog();
                    }
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.recyclerListView = r5;
        r5.setClipChildren(false);
        r5.setClipToPadding(false);
        this.linearLayoutManager = new LinearLayoutManager(context, 0, false) {
            public int scrollHorizontallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
                int i2;
                boolean z = false;
                if (i < 0) {
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    if (reactionsContainerLayout.pullingLeftOffset != 0.0f) {
                        float access$200 = reactionsContainerLayout.getPullingLeftProgress();
                        ReactionsContainerLayout reactionsContainerLayout2 = ReactionsContainerLayout.this;
                        reactionsContainerLayout2.pullingLeftOffset += (float) i;
                        if ((access$200 > 1.0f) != (reactionsContainerLayout2.getPullingLeftProgress() > 1.0f)) {
                            ReactionsContainerLayout.this.recyclerListView.performHapticFeedback(3);
                        }
                        ReactionsContainerLayout reactionsContainerLayout3 = ReactionsContainerLayout.this;
                        float f = reactionsContainerLayout3.pullingLeftOffset;
                        if (f < 0.0f) {
                            i2 = (int) f;
                            reactionsContainerLayout3.pullingLeftOffset = 0.0f;
                        } else {
                            i2 = 0;
                        }
                        FrameLayout frameLayout = reactionsContainerLayout3.customReactionsContainer;
                        if (frameLayout != null) {
                            frameLayout.invalidate();
                        }
                        ReactionsContainerLayout.this.recyclerListView.invalidate();
                        i = i2;
                    }
                }
                int scrollHorizontallyBy = super.scrollHorizontallyBy(i, recycler, state);
                if (i > 0 && scrollHorizontallyBy == 0 && ReactionsContainerLayout.this.recyclerListView.getScrollState() == 1 && ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                    ValueAnimator valueAnimator = ReactionsContainerLayout.this.pullingDownBackAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.removeAllListeners();
                        ReactionsContainerLayout.this.pullingDownBackAnimator.cancel();
                    }
                    float access$2002 = ReactionsContainerLayout.this.getPullingLeftProgress();
                    float f2 = 0.6f;
                    if (access$2002 > 1.0f) {
                        f2 = 0.05f;
                    }
                    ReactionsContainerLayout reactionsContainerLayout4 = ReactionsContainerLayout.this;
                    reactionsContainerLayout4.pullingLeftOffset += ((float) i) * f2;
                    float access$2003 = reactionsContainerLayout4.getPullingLeftProgress();
                    boolean z2 = access$2002 > 1.0f;
                    if (access$2003 > 1.0f) {
                        z = true;
                    }
                    if (z2 != z) {
                        ReactionsContainerLayout.this.recyclerListView.performHapticFeedback(3);
                    }
                    FrameLayout frameLayout2 = ReactionsContainerLayout.this.customReactionsContainer;
                    if (frameLayout2 != null) {
                        frameLayout2.invalidate();
                    }
                    ReactionsContainerLayout.this.recyclerListView.invalidate();
                }
                return scrollHorizontallyBy;
            }
        };
        r5.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                super.getItemOffsets(rect, view, recyclerView, state);
                if (!ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                    int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                    if (childAdapterPosition == 0) {
                        rect.left = AndroidUtilities.dp(6.0f);
                    }
                    rect.right = AndroidUtilities.dp(4.0f);
                    if (childAdapterPosition != ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                        return;
                    }
                    if (ReactionsContainerLayout.this.showUnlockPremiumButton() || ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                        rect.right = AndroidUtilities.dp(2.0f);
                    } else {
                        rect.right = AndroidUtilities.dp(6.0f);
                    }
                } else {
                    rect.left = 0;
                    rect.right = 0;
                }
            }
        });
        r5.setLayoutManager(this.linearLayoutManager);
        r5.setOverScrollMode(2);
        AnonymousClass5 r8 = new RecyclerView.Adapter() {
            int premiumUnlockButtonRow;
            int rowCount;

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view;
                if (i == 1) {
                    ReactionsContainerLayout.this.premiumLockContainer = new FrameLayout(context);
                    PremiumLockIconView unused = ReactionsContainerLayout.this.premiumLockIconView = new PremiumLockIconView(context, PremiumLockIconView.TYPE_REACTIONS);
                    ReactionsContainerLayout.this.premiumLockIconView.setColor(ColorUtils.blendARGB(Theme.getColor("actionBarDefaultSubmenuItemIcon"), Theme.getColor("dialogBackground"), 0.7f));
                    ReactionsContainerLayout.this.premiumLockIconView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
                    ReactionsContainerLayout.this.premiumLockIconView.setScaleX(0.0f);
                    ReactionsContainerLayout.this.premiumLockIconView.setScaleY(0.0f);
                    ReactionsContainerLayout.this.premiumLockIconView.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    reactionsContainerLayout.premiumLockContainer.addView(reactionsContainerLayout.premiumLockIconView, LayoutHelper.createFrame(26, 26, 17));
                    ReactionsContainerLayout.this.premiumLockIconView.setOnClickListener(new ReactionsContainerLayout$5$$ExternalSyntheticLambda1(this));
                    view = ReactionsContainerLayout.this.premiumLockContainer;
                } else if (i != 2) {
                    view = new ReactionHolderView(context);
                } else {
                    ReactionsContainerLayout.this.customReactionsContainer = new CustomReactionsContainer(context);
                    InternalImageView unused2 = ReactionsContainerLayout.this.customEmojiReactionsIconView = new InternalImageView(context);
                    ReactionsContainerLayout.this.customEmojiReactionsIconView.setImageResource(R.drawable.msg_reactions_expand);
                    ReactionsContainerLayout.this.customEmojiReactionsIconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    ReactionsContainerLayout.this.customEmojiReactionsIconView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
                    ReactionsContainerLayout.this.customEmojiReactionsIconView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(28.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor("listSelectorSDK21"), 40)));
                    ReactionsContainerLayout.this.customEmojiReactionsIconView.setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
                    ReactionsContainerLayout reactionsContainerLayout2 = ReactionsContainerLayout.this;
                    reactionsContainerLayout2.customReactionsContainer.addView(reactionsContainerLayout2.customEmojiReactionsIconView, LayoutHelper.createFrame(30, 30, 17));
                    ReactionsContainerLayout.this.customEmojiReactionsIconView.setOnClickListener(new ReactionsContainerLayout$5$$ExternalSyntheticLambda0(this));
                    view = ReactionsContainerLayout.this.customReactionsContainer;
                }
                int paddingTop = (ReactionsContainerLayout.this.getLayoutParams().height - ReactionsContainerLayout.this.getPaddingTop()) - ReactionsContainerLayout.this.getPaddingBottom();
                view.setLayoutParams(new RecyclerView.LayoutParams(paddingTop - AndroidUtilities.dp(12.0f), paddingTop));
                return new RecyclerListView.Holder(view);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onCreateViewHolder$0(View view) {
                int[] iArr = new int[2];
                view.getLocationOnScreen(iArr);
                ReactionsContainerLayout.this.showUnlockPremium(((float) iArr[0]) + (((float) view.getMeasuredWidth()) / 2.0f), ((float) iArr[1]) + (((float) view.getMeasuredHeight()) / 2.0f));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onCreateViewHolder$1(View view) {
                ReactionsContainerLayout.this.showCustomEmojiReactionDialog();
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                if (viewHolder.getItemViewType() == 0) {
                    ReactionHolderView reactionHolderView = (ReactionHolderView) viewHolder.itemView;
                    reactionHolderView.setScaleX(1.0f);
                    reactionHolderView.setScaleY(1.0f);
                    reactionHolderView.setReaction((ReactionsLayoutInBubble.VisibleReaction) ReactionsContainerLayout.this.visibleReactionsList.get(i));
                }
            }

            public int getItemCount() {
                return this.rowCount;
            }

            public int getItemViewType(int i) {
                if (i < 0 || i >= ReactionsContainerLayout.this.visibleReactionsList.size()) {
                    return i == this.premiumUnlockButtonRow ? 1 : 2;
                }
                return 0;
            }

            public void notifyDataSetChanged() {
                this.rowCount = 0;
                this.premiumUnlockButtonRow = -1;
                this.rowCount = 0 + ReactionsContainerLayout.this.visibleReactionsList.size();
                if (ReactionsContainerLayout.this.showUnlockPremiumButton()) {
                    int i = this.rowCount;
                    this.rowCount = i + 1;
                    this.premiumUnlockButtonRow = i;
                }
                if (ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                    this.rowCount++;
                }
                super.notifyDataSetChanged();
            }
        };
        this.listAdapter = r8;
        r5.setAdapter(r8);
        r5.addOnScrollListener(new LeftRightShadowsListener());
        r5.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (recyclerView.getChildCount() > 2) {
                    recyclerView.getLocationInWindow(ReactionsContainerLayout.this.location);
                    int i3 = ReactionsContainerLayout.this.location[0];
                    View childAt = recyclerView.getChildAt(0);
                    childAt.getLocationInWindow(ReactionsContainerLayout.this.location);
                    float min = ((1.0f - Math.min(1.0f, (-Math.min((float) (ReactionsContainerLayout.this.location[0] - i3), 0.0f)) / ((float) childAt.getWidth()))) * 0.39999998f) + 0.6f;
                    if (Float.isNaN(min)) {
                        min = 1.0f;
                    }
                    ReactionsContainerLayout.this.setChildScale(childAt, min);
                    View childAt2 = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                    childAt2.getLocationInWindow(ReactionsContainerLayout.this.location);
                    float min2 = ((1.0f - Math.min(1.0f, (-Math.min((float) ((i3 + recyclerView.getWidth()) - (ReactionsContainerLayout.this.location[0] + childAt2.getWidth())), 0.0f)) / ((float) childAt2.getWidth()))) * 0.39999998f) + 0.6f;
                    if (Float.isNaN(min2)) {
                        min2 = 1.0f;
                    }
                    ReactionsContainerLayout.this.setChildScale(childAt2, min2);
                }
                for (int i4 = 1; i4 < ReactionsContainerLayout.this.recyclerListView.getChildCount() - 1; i4++) {
                    ReactionsContainerLayout.this.setChildScale(ReactionsContainerLayout.this.recyclerListView.getChildAt(i4), 1.0f);
                }
                ReactionsContainerLayout.this.invalidate();
            }
        });
        r5.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                if (childAdapterPosition == 0) {
                    rect.left = AndroidUtilities.dp(8.0f);
                }
                if (childAdapterPosition == ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                    rect.right = AndroidUtilities.dp(8.0f);
                }
            }
        });
        r5.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ReactionsContainerLayout$$ExternalSyntheticLambda3(this));
        r5.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new ReactionsContainerLayout$$ExternalSyntheticLambda4(this));
        addView(r5, LayoutHelper.createFrame(-1, -1.0f));
        setClipChildren(false);
        setClipToPadding(false);
        invalidateShaders();
        int paddingTop = (r5.getLayoutParams().height - r5.getPaddingTop()) - r5.getPaddingBottom();
        this.nextRecentReaction.getLayoutParams().width = paddingTop - AndroidUtilities.dp(12.0f);
        this.nextRecentReaction.getLayoutParams().height = paddingTop;
        this.bgPaint.setColor(Theme.getColor("actionBarDefaultSubmenuBackground", resourcesProvider2));
        MediaDataController.getInstance(i).preloadReactions();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, int i) {
        ReactionsContainerDelegate reactionsContainerDelegate = this.delegate;
        if (reactionsContainerDelegate != null && (view instanceof ReactionHolderView)) {
            reactionsContainerDelegate.onReactionClicked(this, ((ReactionHolderView) view).currentReaction, false, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$1(View view, int i) {
        ReactionsContainerDelegate reactionsContainerDelegate = this.delegate;
        if (reactionsContainerDelegate == null || !(view instanceof ReactionHolderView)) {
            return false;
        }
        reactionsContainerDelegate.onReactionClicked(this, ((ReactionHolderView) view).currentReaction, true, false);
        return true;
    }

    /* access modifiers changed from: private */
    public void animatePullingBack() {
        float f = this.pullingLeftOffset;
        if (f != 0.0f) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{f, 0.0f});
            this.pullingDownBackAnimator = ofFloat;
            ofFloat.addUpdateListener(new ReactionsContainerLayout$$ExternalSyntheticLambda0(this));
            this.pullingDownBackAnimator.setDuration(150);
            this.pullingDownBackAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animatePullingBack$2(ValueAnimator valueAnimator) {
        this.pullingLeftOffset = ((Float) this.pullingDownBackAnimator.getAnimatedValue()).floatValue();
        FrameLayout frameLayout = this.customReactionsContainer;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        invalidate();
    }

    /* access modifiers changed from: private */
    public void showCustomEmojiReactionDialog() {
        if (this.reactionsWindow == null) {
            this.reactionsWindow = new CustomEmojiReactionsWindow(this.fragment, this.allReactionsList, this.selectedReactions, this, this.resourcesProvider);
            for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
                View childAt = this.recyclerListView.getChildAt(i);
                if (childAt instanceof ReactionHolderView) {
                    ReactionHolderView reactionHolderView = (ReactionHolderView) childAt;
                    if (reactionHolderView.loopImageView.getImageReceiver().getLottieAnimation() != null) {
                        reactionHolderView.loopImageView.getImageReceiver().moveLottieToFront();
                    }
                    if (reactionHolderView.loopImageView.animatedEmojiDrawable != null) {
                        this.reactionsWindow.getSelectAnimatedEmojiDialog().putAnimatedEmojiToCache(reactionHolderView.loopImageView.animatedEmojiDrawable);
                    }
                }
            }
            ReactionHolderView reactionHolderView2 = this.nextRecentReaction;
            if (reactionHolderView2 != null && reactionHolderView2.getVisibility() == 0) {
                this.nextRecentReaction.loopImageView.getImageReceiver().moveLottieToFront();
            }
            this.reactionsWindow.onDismissListener(new ReactionsContainerLayout$$ExternalSyntheticLambda2(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showCustomEmojiReactionDialog$3() {
        this.reactionsWindow = null;
    }

    public boolean showCustomEmojiReaction() {
        return this.allReactionsAvailable;
    }

    /* access modifiers changed from: private */
    public boolean showUnlockPremiumButton() {
        return !this.premiumLockedReactions.isEmpty() && !MessagesController.getInstance(this.currentAccount).premiumLocked;
    }

    /* access modifiers changed from: private */
    public void showUnlockPremium(float f, float f2) {
        new PremiumFeatureBottomSheet(this.fragment, 4, true).show();
    }

    /* access modifiers changed from: private */
    public void setChildScale(View view, float f) {
        if (view instanceof ReactionHolderView) {
            ((ReactionHolderView) view).sideScale = f;
            return;
        }
        view.setScaleX(f);
        view.setScaleY(f);
    }

    public void setDelegate(ReactionsContainerDelegate reactionsContainerDelegate) {
        this.delegate = reactionsContainerDelegate;
    }

    @SuppressLint({"NotifyDataSetChanged"})
    private void setVisibleReactionsList(List<ReactionsLayoutInBubble.VisibleReaction> list) {
        this.visibleReactionsList.clear();
        if (showCustomEmojiReaction()) {
            int dp = (AndroidUtilities.displaySize.x - AndroidUtilities.dp(36.0f)) / AndroidUtilities.dp(34.0f);
            if (dp > 7) {
                dp = 7;
            }
            if (dp < 1) {
                dp = 1;
            }
            int i = 0;
            while (i < Math.min(list.size(), dp)) {
                this.visibleReactionsList.add(list.get(i));
                i++;
            }
            if (i < list.size()) {
                this.nextRecentReaction.setReaction(list.get(i));
            }
        } else {
            this.visibleReactionsList.addAll(list);
        }
        this.allReactionsIsDefault = true;
        for (int i2 = 0; i2 < this.visibleReactionsList.size(); i2++) {
            if (this.visibleReactionsList.get(i2).documentId != 0) {
                this.allReactionsIsDefault = false;
            }
        }
        this.allReactionsList.clear();
        this.allReactionsList.addAll(list);
        if (((getLayoutParams().height - getPaddingTop()) - getPaddingBottom()) * list.size() < AndroidUtilities.dp(200.0f)) {
            getLayoutParams().width = -2;
        }
        this.listAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        float f;
        float f2;
        float f3;
        float f4;
        Canvas canvas2 = canvas;
        float max = (Math.max(0.25f, Math.min(this.transitionProgress, 1.0f)) - 0.25f) / 0.75f;
        float f5 = this.bigCircleRadius * max;
        float f6 = this.smallCircleRadius * max;
        this.lastVisibleViewsTmp.clear();
        this.lastVisibleViewsTmp.addAll(this.lastVisibleViews);
        this.lastVisibleViews.clear();
        if (this.prepareAnimation) {
            invalidate();
        }
        if (this.pressedReaction != null) {
            float f7 = this.pressedProgress;
            if (f7 != 1.0f) {
                float f8 = f7 + 0.010666667f;
                this.pressedProgress = f8;
                if (f8 >= 1.0f) {
                    this.pressedProgress = 1.0f;
                }
                invalidate();
            }
        }
        float f9 = this.pressedProgress;
        this.pressedViewScale = (f9 * 2.0f) + 1.0f;
        this.otherViewsScale = 1.0f - (f9 * 0.15f);
        int save = canvas.save();
        if (LocaleController.isRTL) {
            f2 = (float) getWidth();
            f = 0.125f;
        } else {
            f2 = (float) getWidth();
            f = 0.875f;
        }
        float var_ = f2 * f;
        float var_ = this.transitionProgress;
        if (var_ <= 0.75f) {
            float var_ = var_ / 0.75f;
            canvas2.scale(var_, var_, var_, ((float) getHeight()) / 2.0f);
        }
        if (LocaleController.isRTL) {
            f3 = Math.max(0.25f, this.transitionProgress);
            f4 = 0.0f;
        } else {
            f4 = 1.0f - Math.max(0.25f, this.transitionProgress);
            f3 = 1.0f;
        }
        float pullingLeftProgress = getPullingLeftProgress();
        float expandSize = expandSize();
        View view = this.popupLayout;
        if (view != null) {
            view.setTranslationY(expandSize);
        }
        float var_ = f6;
        this.rect.set(((float) getPaddingLeft()) + (((float) (getWidth() - getPaddingRight())) * f4), (((float) getPaddingTop()) + (((float) this.recyclerListView.getMeasuredHeight()) * (1.0f - this.otherViewsScale))) - expandSize, ((float) (getWidth() - getPaddingRight())) * f3, ((float) (getHeight() - getPaddingBottom())) + expandSize);
        this.radius = (this.rect.height() - (expandSize * 2.0f)) / 2.0f;
        this.shadow.setAlpha((int) (Utilities.clamp(1.0f - (this.customEmojiReactionsEnterProgress / 0.05f), 1.0f, 0.0f) * 255.0f));
        Drawable drawable = this.shadow;
        int width = getWidth() - getPaddingRight();
        Rect rect2 = this.shadowPad;
        int i = (int) expandSize;
        drawable.setBounds((int) ((((float) getPaddingLeft()) + (((float) (width + rect2.right)) * f4)) - ((float) rect2.left)), (getPaddingTop() - this.shadowPad.top) - i, (int) (((float) ((getWidth() - getPaddingRight()) + this.shadowPad.right)) * f3), (getHeight() - getPaddingBottom()) + this.shadowPad.bottom + i);
        this.shadow.draw(canvas2);
        canvas2.restoreToCount(save);
        if (!this.skipDraw) {
            int save2 = canvas.save();
            float var_ = this.transitionProgress;
            if (var_ <= 0.75f) {
                float var_ = var_ / 0.75f;
                canvas2.scale(var_, var_, var_, ((float) getHeight()) / 2.0f);
            }
            RectF rectF = this.rect;
            float var_ = this.radius;
            canvas2.drawRoundRect(rectF, var_, var_, this.bgPaint);
            canvas2.restoreToCount(save2);
        }
        this.mPath.rewind();
        Path path = this.mPath;
        RectF rectF2 = this.rect;
        float var_ = this.radius;
        path.addRoundRect(rectF2, var_, var_, Path.Direction.CW);
        int save3 = canvas.save();
        float var_ = this.transitionProgress;
        if (var_ <= 0.75f) {
            float var_ = var_ / 0.75f;
            canvas2.scale(var_, var_, var_, ((float) getHeight()) / 2.0f);
        }
        if (this.transitionProgress != 0.0f && getAlpha() == 1.0f) {
            int i2 = 0;
            int i3 = 0;
            for (int i4 = 0; i4 < this.recyclerListView.getChildCount(); i4++) {
                View childAt = this.recyclerListView.getChildAt(i4);
                if (childAt instanceof ReactionHolderView) {
                    ReactionHolderView reactionHolderView = (ReactionHolderView) this.recyclerListView.getChildAt(i4);
                    checkPressedProgress(canvas2, reactionHolderView);
                    if (!reactionHolderView.hasEnterAnimation || reactionHolderView.enterImageView.getImageReceiver().getLottieAnimation() != null) {
                        if (reactionHolderView.getX() + (((float) reactionHolderView.getMeasuredWidth()) / 2.0f) > 0.0f && reactionHolderView.getX() + (((float) reactionHolderView.getMeasuredWidth()) / 2.0f) < ((float) this.recyclerListView.getWidth())) {
                            if (!this.lastVisibleViewsTmp.contains(reactionHolderView)) {
                                reactionHolderView.play(i3);
                                i3 += 30;
                            }
                            this.lastVisibleViews.add(reactionHolderView);
                        } else if (!reactionHolderView.isEnter) {
                            reactionHolderView.resetAnimation();
                        }
                        if (reactionHolderView.getLeft() > i2) {
                            i2 = reactionHolderView.getLeft();
                        }
                    }
                } else {
                    if (childAt == this.premiumLockContainer) {
                        if (childAt.getX() + (((float) childAt.getMeasuredWidth()) / 2.0f) <= 0.0f || childAt.getX() + (((float) childAt.getMeasuredWidth()) / 2.0f) >= ((float) this.recyclerListView.getWidth())) {
                            this.premiumLockIconView.resetAnimation();
                        } else {
                            if (!this.lastVisibleViewsTmp.contains(childAt)) {
                                this.premiumLockIconView.play(i3);
                                i3 += 30;
                            }
                            this.lastVisibleViews.add(childAt);
                        }
                    }
                    if (childAt == this.customReactionsContainer) {
                        if (childAt.getX() + (((float) childAt.getMeasuredWidth()) / 2.0f) <= 0.0f || childAt.getX() + (((float) childAt.getMeasuredWidth()) / 2.0f) >= ((float) this.recyclerListView.getWidth())) {
                            this.customEmojiReactionsIconView.resetAnimation();
                        } else {
                            if (!this.lastVisibleViewsTmp.contains(childAt)) {
                                this.customEmojiReactionsIconView.play(i3);
                                i3 += 30;
                            }
                            this.lastVisibleViews.add(childAt);
                        }
                    }
                    checkPressedProgressForOtherViews(childAt);
                }
            }
            if (pullingLeftProgress > 0.0f) {
                float pullingLeftProgress2 = getPullingLeftProgress();
                int dp = i2 + AndroidUtilities.dp(32.0f);
                float clamp = Utilities.clamp(((float) dp) / ((float) (getMeasuredWidth() - AndroidUtilities.dp(34.0f))), 1.0f, 0.0f) * pullingLeftProgress2 * ((float) AndroidUtilities.dp(32.0f));
                if (this.nextRecentReaction.getTag() == null) {
                    this.nextRecentReaction.setTag(Float.valueOf(1.0f));
                    this.nextRecentReaction.resetAnimation();
                    this.nextRecentReaction.play(0);
                }
                float clamp2 = Utilities.clamp(pullingLeftProgress2, 1.0f, 0.0f);
                this.nextRecentReaction.setScaleX(clamp2);
                this.nextRecentReaction.setScaleY(clamp2);
                this.nextRecentReaction.setTranslationX((((float) (this.recyclerListView.getLeft() + dp)) - clamp) - ((float) AndroidUtilities.dp(20.0f)));
                this.nextRecentReaction.setVisibility(0);
            } else {
                this.nextRecentReaction.setVisibility(8);
                if (this.nextRecentReaction.getTag() != null) {
                    this.nextRecentReaction.setTag((Object) null);
                }
            }
        }
        if (!this.skipDraw || this.reactionsWindow == null) {
            canvas2.clipPath(this.mPath);
            canvas2.translate(((float) ((LocaleController.isRTL ? -1 : 1) * getWidth())) * (1.0f - this.transitionProgress), 0.0f);
            super.dispatchDraw(canvas);
            if (this.leftShadowPaint != null) {
                this.leftShadowPaint.setAlpha((int) (Utilities.clamp(this.leftAlpha * this.transitionProgress, 1.0f, 0.0f) * 255.0f));
                canvas2.drawRect(this.rect, this.leftShadowPaint);
            }
            if (this.rightShadowPaint != null) {
                this.rightShadowPaint.setAlpha((int) (Utilities.clamp(this.rightAlpha * this.transitionProgress, 1.0f, 0.0f) * 255.0f));
                canvas2.drawRect(this.rect, this.rightShadowPaint);
            }
            canvas2.restoreToCount(save3);
            drawBubbles(canvas, f5, max, var_, 255);
            invalidate();
            return;
        }
        int clamp3 = (int) (Utilities.clamp(1.0f - (this.customEmojiReactionsEnterProgress / 0.2f), 1.0f, 0.0f) * (1.0f - this.customEmojiReactionsEnterProgress) * 255.0f);
        canvas.save();
        drawBubbles(canvas, f5, max, var_, clamp3);
        canvas.restore();
    }

    public void drawBubbles(Canvas canvas) {
        float max = (Math.max(0.25f, Math.min(this.transitionProgress, 1.0f)) - 0.25f) / 0.75f;
        drawBubbles(canvas, this.bigCircleRadius * max, max, this.smallCircleRadius * max, (int) (Utilities.clamp(this.customEmojiReactionsEnterProgress / 0.2f, 1.0f, 0.0f) * (1.0f - this.customEmojiReactionsEnterProgress) * 255.0f));
    }

    private void drawBubbles(Canvas canvas, float f, float f2, float f3, int i) {
        canvas.save();
        canvas.clipRect(0.0f, this.rect.bottom, (float) getMeasuredWidth(), (float) (getMeasuredHeight() + AndroidUtilities.dp(8.0f)));
        float width = (float) (LocaleController.isRTL ? this.bigCircleOffset : getWidth() - this.bigCircleOffset);
        float height = ((float) (getHeight() - getPaddingBottom())) + expandSize();
        int dp = AndroidUtilities.dp(3.0f);
        this.shadow.setAlpha(i);
        this.bgPaint.setAlpha(i);
        float f4 = (float) dp;
        float f5 = f4 * f2;
        this.shadow.setBounds((int) ((width - f) - f5), (int) ((height - f) - f5), (int) (width + f + f5), (int) (height + f + f5));
        this.shadow.draw(canvas);
        canvas.drawCircle(width, height, f, this.bgPaint);
        float width2 = LocaleController.isRTL ? ((float) this.bigCircleOffset) - this.bigCircleRadius : ((float) (getWidth() - this.bigCircleOffset)) + this.bigCircleRadius;
        float height2 = ((((float) getHeight()) - this.smallCircleRadius) - f4) + expandSize();
        float f6 = ((float) (-AndroidUtilities.dp(1.0f))) * f2;
        this.shadow.setBounds((int) ((width2 - f) - f6), (int) ((height2 - f) - f6), (int) (width2 + f + f6), (int) (f + height2 + f6));
        this.shadow.draw(canvas);
        canvas.drawCircle(width2, height2, f3, this.bgPaint);
        canvas.restore();
        this.shadow.setAlpha(255);
        this.bgPaint.setAlpha(255);
    }

    private void checkPressedProgressForOtherViews(View view) {
        int childAdapterPosition = this.recyclerListView.getChildAdapterPosition(view);
        float measuredWidth = ((((float) view.getMeasuredWidth()) * (this.pressedViewScale - 1.0f)) / 3.0f) - ((((float) view.getMeasuredWidth()) * (1.0f - this.otherViewsScale)) * ((float) (Math.abs(this.pressedReactionPosition - childAdapterPosition) - 1)));
        if (childAdapterPosition < this.pressedReactionPosition) {
            view.setPivotX(0.0f);
            view.setTranslationX(-measuredWidth);
        } else {
            view.setPivotX((float) view.getMeasuredWidth());
            view.setTranslationX(measuredWidth);
        }
        view.setScaleX(this.otherViewsScale);
        view.setScaleY(this.otherViewsScale);
    }

    private void checkPressedProgress(Canvas canvas, ReactionHolderView reactionHolderView) {
        AnimatedEmojiDrawable animatedEmojiDrawable;
        float clamp = this.pullingLeftOffset != 0.0f ? Utilities.clamp(((float) reactionHolderView.getLeft()) / ((float) (getMeasuredWidth() - AndroidUtilities.dp(34.0f))), 1.0f, 0.0f) * getPullingLeftProgress() * ((float) AndroidUtilities.dp(46.0f)) : 0.0f;
        if (reactionHolderView.currentReaction.equals(this.pressedReaction)) {
            BackupImageView backupImageView = showCustomEmojiReaction() ? reactionHolderView.loopImageView : reactionHolderView.enterImageView;
            reactionHolderView.setPivotX((float) (reactionHolderView.getMeasuredWidth() >> 1));
            reactionHolderView.setPivotY(backupImageView.getY() + ((float) backupImageView.getMeasuredHeight()));
            reactionHolderView.setScaleX(this.pressedViewScale);
            reactionHolderView.setScaleY(this.pressedViewScale);
            if (!this.clicked) {
                if (this.cancelPressedAnimation == null) {
                    reactionHolderView.pressedBackupImageView.setVisibility(0);
                    reactionHolderView.pressedBackupImageView.setAlpha(1.0f);
                    if (reactionHolderView.pressedBackupImageView.getImageReceiver().hasBitmapImage() || !((animatedEmojiDrawable = reactionHolderView.pressedBackupImageView.animatedEmojiDrawable) == null || animatedEmojiDrawable.getImageReceiver() == null || !reactionHolderView.pressedBackupImageView.animatedEmojiDrawable.getImageReceiver().hasBitmapImage())) {
                        backupImageView.setAlpha(0.0f);
                    }
                } else {
                    reactionHolderView.pressedBackupImageView.setAlpha(1.0f - this.cancelPressedProgress);
                    backupImageView.setAlpha(this.cancelPressedProgress);
                }
                if (this.pressedProgress == 1.0f) {
                    this.clicked = true;
                    if (System.currentTimeMillis() - this.lastReactionSentTime > 300) {
                        this.lastReactionSentTime = System.currentTimeMillis();
                        this.delegate.onReactionClicked(reactionHolderView, reactionHolderView.currentReaction, true, false);
                    }
                }
            }
            canvas.save();
            float x = this.recyclerListView.getX() + reactionHolderView.getX();
            float measuredWidth = ((((float) reactionHolderView.getMeasuredWidth()) * reactionHolderView.getScaleX()) - ((float) reactionHolderView.getMeasuredWidth())) / 2.0f;
            float f = x - measuredWidth;
            if (f < 0.0f && reactionHolderView.getTranslationX() >= 0.0f) {
                reactionHolderView.setTranslationX((-f) - clamp);
            } else if (((float) reactionHolderView.getMeasuredWidth()) + x + measuredWidth <= ((float) getMeasuredWidth()) || reactionHolderView.getTranslationX() > 0.0f) {
                reactionHolderView.setTranslationX(0.0f - clamp);
            } else {
                reactionHolderView.setTranslationX((((((float) getMeasuredWidth()) - x) - ((float) reactionHolderView.getMeasuredWidth())) - measuredWidth) - clamp);
            }
            canvas.translate(this.recyclerListView.getX() + reactionHolderView.getX(), this.recyclerListView.getY() + reactionHolderView.getY());
            canvas.scale(reactionHolderView.getScaleX(), reactionHolderView.getScaleY(), reactionHolderView.getPivotX(), reactionHolderView.getPivotY());
            reactionHolderView.draw(canvas);
            canvas.restore();
            return;
        }
        int childAdapterPosition = this.recyclerListView.getChildAdapterPosition(reactionHolderView);
        float measuredWidth2 = ((((float) reactionHolderView.getMeasuredWidth()) * (this.pressedViewScale - 1.0f)) / 3.0f) - ((((float) reactionHolderView.getMeasuredWidth()) * (1.0f - this.otherViewsScale)) * ((float) (Math.abs(this.pressedReactionPosition - childAdapterPosition) - 1)));
        if (childAdapterPosition < this.pressedReactionPosition) {
            reactionHolderView.setPivotX(0.0f);
            reactionHolderView.setTranslationX(-measuredWidth2);
        } else {
            reactionHolderView.setPivotX(((float) reactionHolderView.getMeasuredWidth()) - clamp);
            reactionHolderView.setTranslationX(measuredWidth2 - clamp);
        }
        reactionHolderView.setPivotY(reactionHolderView.enterImageView.getY() + ((float) reactionHolderView.enterImageView.getMeasuredHeight()));
        reactionHolderView.setScaleX(this.otherViewsScale);
        reactionHolderView.setScaleY(this.otherViewsScale);
        reactionHolderView.enterImageView.setScaleX(reactionHolderView.sideScale);
        reactionHolderView.enterImageView.setScaleY(reactionHolderView.sideScale);
        reactionHolderView.pressedBackupImageView.setVisibility(4);
        reactionHolderView.enterImageView.setAlpha(1.0f);
    }

    /* access modifiers changed from: private */
    public float getPullingLeftProgress() {
        return Utilities.clamp(this.pullingLeftOffset / ((float) AndroidUtilities.dp(42.0f)), 2.0f, 0.0f);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        invalidateShaders();
    }

    private void invalidateShaders() {
        int dp = AndroidUtilities.dp(24.0f);
        float height = ((float) getHeight()) / 2.0f;
        float f = height;
        float f2 = height;
        int color = Theme.getColor("actionBarDefaultSubmenuBackground");
        this.leftShadowPaint.setShader(new LinearGradient(0.0f, f, (float) dp, f2, color, 0, Shader.TileMode.CLAMP));
        this.rightShadowPaint.setShader(new LinearGradient((float) getWidth(), f, (float) (getWidth() - dp), f2, color, 0, Shader.TileMode.CLAMP));
        invalidate();
    }

    public void setTransitionProgress(float f) {
        this.transitionProgress = f;
        invalidate();
    }

    public void setMessage(MessageObject messageObject2, TLRPC$ChatFull tLRPC$ChatFull) {
        this.messageObject = messageObject2;
        ArrayList arrayList = new ArrayList();
        if (!messageObject2.isForwardedChannelPost() || (tLRPC$ChatFull = MessagesController.getInstance(this.currentAccount).getChatFull(-messageObject2.getFromChatId())) != null) {
            if (tLRPC$ChatFull != null) {
                TLRPC$ChatReactions tLRPC$ChatReactions = tLRPC$ChatFull.available_reactions;
                if (tLRPC$ChatReactions instanceof TLRPC$TL_chatReactionsAll) {
                    TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(tLRPC$ChatFull.id));
                    if (chat == null || ChatObject.isChannelAndNotMegaGroup(chat)) {
                        this.allReactionsAvailable = false;
                    } else {
                        this.allReactionsAvailable = true;
                    }
                    fillRecentReactionsList(arrayList);
                } else if (tLRPC$ChatReactions instanceof TLRPC$TL_chatReactionsSome) {
                    Iterator<TLRPC$Reaction> it = ((TLRPC$TL_chatReactionsSome) tLRPC$ChatReactions).reactions.iterator();
                    while (it.hasNext()) {
                        TLRPC$Reaction next = it.next();
                        Iterator<TLRPC$TL_availableReaction> it2 = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList().iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            TLRPC$TL_availableReaction next2 = it2.next();
                            if (!(next instanceof TLRPC$TL_reactionEmoji) || !next2.reaction.equals(((TLRPC$TL_reactionEmoji) next).emoticon)) {
                                if (next instanceof TLRPC$TL_reactionCustomEmoji) {
                                    arrayList.add(ReactionsLayoutInBubble.VisibleReaction.fromTLReaction(next));
                                    break;
                                }
                            } else {
                                arrayList.add(ReactionsLayoutInBubble.VisibleReaction.fromTLReaction(next));
                                break;
                            }
                        }
                    }
                } else {
                    throw new RuntimeException("Unknow chat reactions type");
                }
            } else {
                this.allReactionsAvailable = true;
                fillRecentReactionsList(arrayList);
            }
            setVisibleReactionsList(arrayList);
            TLRPC$TL_messageReactions tLRPC$TL_messageReactions = messageObject2.messageOwner.reactions;
            if (tLRPC$TL_messageReactions != null && tLRPC$TL_messageReactions.results != null) {
                for (int i = 0; i < messageObject2.messageOwner.reactions.results.size(); i++) {
                    if (messageObject2.messageOwner.reactions.results.get(i).chosen) {
                        this.selectedReactions.add(ReactionsLayoutInBubble.VisibleReaction.fromTLReaction(messageObject2.messageOwner.reactions.results.get(i).reaction));
                    }
                }
                return;
            }
            return;
        }
        this.waitingLoadingChatId = -messageObject2.getFromChatId();
        MessagesController.getInstance(this.currentAccount).loadFullChat(-messageObject2.getFromChatId(), 0, true);
        setVisibility(4);
    }

    private void fillRecentReactionsList(List<ReactionsLayoutInBubble.VisibleReaction> list) {
        int i = 0;
        if (!this.allReactionsAvailable) {
            List<TLRPC$TL_availableReaction> enabledReactionsList = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
            while (i < enabledReactionsList.size()) {
                list.add(ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(enabledReactionsList.get(i)));
                i++;
            }
            return;
        }
        ArrayList<TLRPC$Reaction> topReactions = MediaDataController.getInstance(this.currentAccount).getTopReactions();
        HashSet hashSet = new HashSet();
        int i2 = 0;
        for (int i3 = 0; i3 < topReactions.size(); i3++) {
            ReactionsLayoutInBubble.VisibleReaction fromTLReaction = ReactionsLayoutInBubble.VisibleReaction.fromTLReaction(topReactions.get(i3));
            if (!hashSet.contains(fromTLReaction) && (UserConfig.getInstance(this.currentAccount).isPremium() || fromTLReaction.documentId == 0)) {
                hashSet.add(fromTLReaction);
                list.add(fromTLReaction);
                i2++;
            }
            if (i2 == 16) {
                break;
            }
        }
        ArrayList<TLRPC$Reaction> recentReactions = MediaDataController.getInstance(this.currentAccount).getRecentReactions();
        for (int i4 = 0; i4 < recentReactions.size(); i4++) {
            ReactionsLayoutInBubble.VisibleReaction fromTLReaction2 = ReactionsLayoutInBubble.VisibleReaction.fromTLReaction(recentReactions.get(i4));
            if (!hashSet.contains(fromTLReaction2)) {
                hashSet.add(fromTLReaction2);
                list.add(fromTLReaction2);
            }
        }
        List<TLRPC$TL_availableReaction> enabledReactionsList2 = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
        while (i < enabledReactionsList2.size()) {
            ReactionsLayoutInBubble.VisibleReaction fromEmojicon = ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(enabledReactionsList2.get(i));
            if (!hashSet.contains(fromEmojicon)) {
                hashSet.add(fromEmojicon);
                list.add(fromEmojicon);
            }
            i++;
        }
    }

    public void startEnterAnimation() {
        setTransitionProgress(0.0f);
        setAlpha(1.0f);
        ObjectAnimator duration = ObjectAnimator.ofFloat(this, TRANSITION_PROGRESS_VALUE, new float[]{0.0f, 1.0f}).setDuration(400);
        duration.setInterpolator(new OvershootInterpolator(1.004f));
        duration.start();
    }

    public int getTotalWidth() {
        int itemsCount = getItemsCount();
        if (!showCustomEmojiReaction()) {
            return (AndroidUtilities.dp(36.0f) * itemsCount) + (AndroidUtilities.dp(2.0f) * (itemsCount - 1)) + AndroidUtilities.dp(16.0f);
        }
        return (AndroidUtilities.dp(36.0f) * itemsCount) - AndroidUtilities.dp(6.0f);
    }

    public int getItemsCount() {
        return this.visibleReactionsList.size() + (showCustomEmojiReaction() ? 1 : 0) + 1;
    }

    public void setCustomEmojiEnterProgress(float f) {
        this.customEmojiReactionsEnterProgress = f;
        this.popupLayout.setAlpha(1.0f - f);
        invalidate();
    }

    public void dismissParent(boolean z) {
        CustomEmojiReactionsWindow customEmojiReactionsWindow = this.reactionsWindow;
        if (customEmojiReactionsWindow != null) {
            customEmojiReactionsWindow.dismiss(z);
            this.reactionsWindow = null;
        }
    }

    public void onReactionClicked(View view, ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z) {
        ReactionsContainerDelegate reactionsContainerDelegate = this.delegate;
        if (reactionsContainerDelegate != null) {
            reactionsContainerDelegate.onReactionClicked(view, visibleReaction, z, true);
        }
    }

    public void prepareAnimation(boolean z) {
        this.prepareAnimation = z;
        invalidate();
    }

    public void setSkipDraw(boolean z) {
        if (this.skipDraw != z) {
            this.skipDraw = z;
            if (!z) {
                for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
                    if (this.recyclerListView.getChildAt(i) instanceof ReactionHolderView) {
                        ReactionHolderView reactionHolderView = (ReactionHolderView) this.recyclerListView.getChildAt(i);
                        if (reactionHolderView.hasEnterAnimation) {
                            reactionHolderView.loopImageView.setVisibility(0);
                            reactionHolderView.enterImageView.setVisibility(4);
                            if (reactionHolderView.shouldSwitchToLoopView) {
                                reactionHolderView.switchedToLoopView = true;
                            }
                        }
                    }
                }
            }
            invalidate();
        }
    }

    public void onCustomEmojiWindowOpened() {
        animatePullingBack();
    }

    public void clearRecentReactions() {
        AlertDialog create = new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString(R.string.ClearRecentReactionsAlertTitle)).setMessage(LocaleController.getString(R.string.ClearRecentReactionsAlertMessage)).setPositiveButton(LocaleController.getString(R.string.ClearButton), new ReactionsContainerLayout$$ExternalSyntheticLambda1(this)).setNegativeButton(LocaleController.getString(R.string.Cancel), (DialogInterface.OnClickListener) null).create();
        create.show();
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearRecentReactions$4(DialogInterface dialogInterface, int i) {
        MediaDataController.getInstance(this.currentAccount).clearRecentReactions();
        ArrayList arrayList = new ArrayList();
        fillRecentReactionsList(arrayList);
        setVisibleReactionsList(arrayList);
        this.lastVisibleViews.clear();
        this.reactionsWindow.setRecentReactions(arrayList);
    }

    private final class LeftRightShadowsListener extends RecyclerView.OnScrollListener {
        private ValueAnimator leftAnimator;
        private boolean leftVisible;
        private ValueAnimator rightAnimator;
        private boolean rightVisible;

        private LeftRightShadowsListener() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            boolean z = false;
            boolean z2 = ReactionsContainerLayout.this.linearLayoutManager.findFirstVisibleItemPosition() != 0;
            float f = 1.0f;
            if (z2 != this.leftVisible) {
                ValueAnimator valueAnimator = this.leftAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.leftAnimator = startAnimator(ReactionsContainerLayout.this.leftAlpha, z2 ? 1.0f : 0.0f, new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda1(this), new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda4(this));
                this.leftVisible = z2;
            }
            if (ReactionsContainerLayout.this.linearLayoutManager.findLastVisibleItemPosition() != ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                z = true;
            }
            if (z != this.rightVisible) {
                ValueAnimator valueAnimator2 = this.rightAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                float access$1800 = ReactionsContainerLayout.this.rightAlpha;
                if (!z) {
                    f = 0.0f;
                }
                this.rightAnimator = startAnimator(access$1800, f, new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda2(this), new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda3(this));
                this.rightVisible = z;
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onScrolled$0(Float f) {
            ReactionsContainerLayout.this.leftShadowPaint.setAlpha((int) (ReactionsContainerLayout.this.leftAlpha = f.floatValue() * 255.0f));
            ReactionsContainerLayout.this.invalidate();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onScrolled$1() {
            this.leftAnimator = null;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onScrolled$2(Float f) {
            ReactionsContainerLayout.this.rightShadowPaint.setAlpha((int) (ReactionsContainerLayout.this.rightAlpha = f.floatValue() * 255.0f));
            ReactionsContainerLayout.this.invalidate();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onScrolled$3() {
            this.rightAnimator = null;
        }

        private ValueAnimator startAnimator(float f, float f2, Consumer<Float> consumer, final Runnable runnable) {
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{f, f2}).setDuration((long) (Math.abs(f2 - f) * 150.0f));
            duration.addUpdateListener(new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda0(consumer));
            duration.addListener(new AnimatorListenerAdapter(this) {
                public void onAnimationEnd(Animator animator) {
                    runnable.run();
                }
            });
            duration.start();
            return duration;
        }
    }

    public final class ReactionHolderView extends FrameLayout {
        public ReactionsLayoutInBubble.VisibleReaction currentReaction;
        public BackupImageView enterImageView;
        public boolean hasEnterAnimation;
        /* access modifiers changed from: private */
        public boolean isEnter;
        Runnable longPressRunnable = new Runnable() {
            public void run() {
                ReactionHolderView.this.performHapticFeedback(0);
                ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                int unused = reactionsContainerLayout.pressedReactionPosition = reactionsContainerLayout.visibleReactionsList.indexOf(ReactionHolderView.this.currentReaction);
                ReactionHolderView reactionHolderView = ReactionHolderView.this;
                ReactionsLayoutInBubble.VisibleReaction unused2 = ReactionsContainerLayout.this.pressedReaction = reactionHolderView.currentReaction;
                ReactionsContainerLayout.this.invalidate();
            }
        };
        public BackupImageView loopImageView;
        Runnable playRunnable = new Runnable() {
            public void run() {
                if (ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation() != null && !ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().isRunning() && !ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                    ReactionHolderView.this.enterImageView.getImageReceiver().getLottieAnimation().start();
                }
            }
        };
        boolean pressed;
        public BackupImageView pressedBackupImageView;
        float pressedX;
        float pressedY;
        public boolean selected;
        public boolean shouldSwitchToLoopView;
        public float sideScale = 1.0f;
        public boolean switchedToLoopView;
        boolean touchable = true;

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            ReactionsLayoutInBubble.VisibleReaction visibleReaction = this.currentReaction;
            if (visibleReaction != null) {
                String str = visibleReaction.emojicon;
                if (str != null) {
                    accessibilityNodeInfo.setText(str);
                    accessibilityNodeInfo.setEnabled(true);
                    return;
                }
                accessibilityNodeInfo.setText(LocaleController.getString(R.string.AccDescrCustomEmoji));
                accessibilityNodeInfo.setEnabled(true);
            }
        }

        ReactionHolderView(Context context) {
            super(context);
            this.enterImageView = new BackupImageView(context, ReactionsContainerLayout.this) {
                /* access modifiers changed from: protected */
                public void dispatchDraw(Canvas canvas) {
                    super.dispatchDraw(canvas);
                    ReactionHolderView reactionHolderView = ReactionHolderView.this;
                    if (reactionHolderView.shouldSwitchToLoopView && !reactionHolderView.switchedToLoopView && this.imageReceiver.getLottieAnimation() != null && this.imageReceiver.getLottieAnimation().isLastFrame() && ReactionHolderView.this.loopImageView.imageReceiver.getLottieAnimation() != null && ReactionHolderView.this.loopImageView.imageReceiver.getLottieAnimation().hasBitmap()) {
                        ReactionHolderView reactionHolderView2 = ReactionHolderView.this;
                        reactionHolderView2.switchedToLoopView = true;
                        reactionHolderView2.loopImageView.imageReceiver.getLottieAnimation().setCurrentFrame(0, false, true);
                        ReactionHolderView.this.loopImageView.setVisibility(0);
                        AndroidUtilities.runOnUIThread(new ReactionsContainerLayout$ReactionHolderView$2$$ExternalSyntheticLambda0(this));
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$dispatchDraw$0() {
                    ReactionHolderView.this.enterImageView.setVisibility(4);
                }

                public void invalidate() {
                    super.invalidate();
                    ReactionsContainerLayout.this.invalidate();
                }

                public void invalidate(Rect rect) {
                    super.invalidate(rect);
                    ReactionsContainerLayout.this.invalidate();
                }
            };
            this.loopImageView = new BackupImageView(context);
            this.enterImageView.getImageReceiver().setAutoRepeat(0);
            this.enterImageView.getImageReceiver().setAllowStartLottieAnimation(false);
            this.pressedBackupImageView = new BackupImageView(context, ReactionsContainerLayout.this) {
                public void invalidate() {
                    super.invalidate();
                    ReactionsContainerLayout.this.invalidate();
                }
            };
            addView(this.enterImageView, LayoutHelper.createFrame(34, 34, 17));
            addView(this.pressedBackupImageView, LayoutHelper.createFrame(34, 34, 17));
            addView(this.loopImageView, LayoutHelper.createFrame(34, 34, 17));
        }

        /* access modifiers changed from: private */
        public void setReaction(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
            ReactionsLayoutInBubble.VisibleReaction visibleReaction2 = visibleReaction;
            ReactionsLayoutInBubble.VisibleReaction visibleReaction3 = this.currentReaction;
            if (visibleReaction3 == null || !visibleReaction3.equals(visibleReaction2)) {
                resetAnimation();
                this.currentReaction = visibleReaction2;
                this.selected = ReactionsContainerLayout.this.selectedReactions.contains(visibleReaction2);
                if (this.currentReaction.emojicon != null) {
                    TLRPC$TL_availableReaction tLRPC$TL_availableReaction = MediaDataController.getInstance(ReactionsContainerLayout.this.currentAccount).getReactionsMap().get(this.currentReaction.emojicon);
                    if (tLRPC$TL_availableReaction != null) {
                        SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$TL_availableReaction.activate_animation, "windowBackgroundGray", 1.0f);
                        ReactionsLayoutInBubble.VisibleReaction visibleReaction4 = visibleReaction;
                        this.enterImageView.getImageReceiver().setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.appear_animation), "30_30_nolimit_pcache", (ImageLocation) null, (String) null, svgThumb, 0, "tgs", visibleReaction4, 0);
                        this.pressedBackupImageView.getImageReceiver().setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.select_animation), "60_60_pcache", (ImageLocation) null, (String) null, svgThumb, 0, "tgs", visibleReaction4, 0);
                        this.loopImageView.getImageReceiver().setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.select_animation), "60_60_pcache", (ImageLocation) null, (String) null, (Drawable) null, 0, "tgs", this.currentReaction, 0);
                        this.loopImageView.setAnimatedEmojiDrawable((AnimatedEmojiDrawable) null);
                        this.pressedBackupImageView.setAnimatedEmojiDrawable((AnimatedEmojiDrawable) null);
                    }
                } else {
                    this.pressedBackupImageView.getImageReceiver().clearImage();
                    this.loopImageView.getImageReceiver().clearImage();
                    this.pressedBackupImageView.setAnimatedEmojiDrawable(new AnimatedEmojiDrawable(4, ReactionsContainerLayout.this.currentAccount, this.currentReaction.documentId));
                    this.loopImageView.setAnimatedEmojiDrawable(new AnimatedEmojiDrawable(3, ReactionsContainerLayout.this.currentAccount, this.currentReaction.documentId));
                }
                boolean z = true;
                setFocusable(true);
                boolean z2 = this.currentReaction.emojicon != null && (!ReactionsContainerLayout.this.showCustomEmojiReaction() || ReactionsContainerLayout.this.allReactionsIsDefault);
                this.hasEnterAnimation = z2;
                if (!z2 || !ReactionsContainerLayout.this.showCustomEmojiReaction()) {
                    z = false;
                }
                this.shouldSwitchToLoopView = z;
                if (!this.hasEnterAnimation) {
                    this.enterImageView.setVisibility(8);
                    this.loopImageView.setVisibility(0);
                } else {
                    this.switchedToLoopView = false;
                    this.enterImageView.setVisibility(0);
                    this.loopImageView.setVisibility(8);
                }
                if (this.selected) {
                    ViewGroup.LayoutParams layoutParams = this.loopImageView.getLayoutParams();
                    ViewGroup.LayoutParams layoutParams2 = this.loopImageView.getLayoutParams();
                    int dp = AndroidUtilities.dp(26.0f);
                    layoutParams2.height = dp;
                    layoutParams.width = dp;
                    ViewGroup.LayoutParams layoutParams3 = this.enterImageView.getLayoutParams();
                    ViewGroup.LayoutParams layoutParams4 = this.enterImageView.getLayoutParams();
                    int dp2 = AndroidUtilities.dp(26.0f);
                    layoutParams4.height = dp2;
                    layoutParams3.width = dp2;
                    return;
                }
                ViewGroup.LayoutParams layoutParams5 = this.loopImageView.getLayoutParams();
                ViewGroup.LayoutParams layoutParams6 = this.loopImageView.getLayoutParams();
                int dp3 = AndroidUtilities.dp(34.0f);
                layoutParams6.height = dp3;
                layoutParams5.width = dp3;
                ViewGroup.LayoutParams layoutParams7 = this.enterImageView.getLayoutParams();
                ViewGroup.LayoutParams layoutParams8 = this.enterImageView.getLayoutParams();
                int dp4 = AndroidUtilities.dp(34.0f);
                layoutParams8.height = dp4;
                layoutParams7.width = dp4;
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            resetAnimation();
        }

        public boolean play(int i) {
            if (!ReactionsContainerLayout.this.animationEnabled) {
                resetAnimation();
                this.isEnter = true;
                return false;
            }
            AndroidUtilities.cancelRunOnUIThread(this.playRunnable);
            if (this.hasEnterAnimation) {
                if (this.enterImageView.getImageReceiver().getLottieAnimation() == null || this.enterImageView.getImageReceiver().getLottieAnimation().isGeneratingCache() || this.isEnter) {
                    if (this.enterImageView.getImageReceiver().getLottieAnimation() != null && this.isEnter && !this.enterImageView.getImageReceiver().getLottieAnimation().isRunning() && !this.enterImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                        this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(this.enterImageView.getImageReceiver().getLottieAnimation().getFramesCount() - 1, false);
                    }
                    this.loopImageView.setScaleY(1.0f);
                    this.loopImageView.setScaleX(1.0f);
                } else {
                    this.isEnter = true;
                    if (i == 0) {
                        this.enterImageView.getImageReceiver().getLottieAnimation().stop();
                        this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                        this.playRunnable.run();
                    } else {
                        this.enterImageView.getImageReceiver().getLottieAnimation().stop();
                        this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                        AndroidUtilities.runOnUIThread(this.playRunnable, (long) i);
                    }
                    return true;
                }
            } else if (!this.isEnter) {
                this.loopImageView.setScaleY(0.0f);
                this.loopImageView.setScaleX(0.0f);
                this.loopImageView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).setStartDelay((long) i).start();
                this.isEnter = true;
            }
            return false;
        }

        public void resetAnimation() {
            if (this.hasEnterAnimation) {
                AndroidUtilities.cancelRunOnUIThread(this.playRunnable);
                if (this.enterImageView.getImageReceiver().getLottieAnimation() != null && !this.enterImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                    this.enterImageView.getImageReceiver().getLottieAnimation().stop();
                    if (ReactionsContainerLayout.this.animationEnabled) {
                        this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false, true);
                    } else {
                        this.enterImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(this.enterImageView.getImageReceiver().getLottieAnimation().getFramesCount() - 1, false, true);
                    }
                }
                this.loopImageView.setVisibility(4);
                this.enterImageView.setVisibility(0);
                this.switchedToLoopView = false;
                this.loopImageView.setScaleY(1.0f);
                this.loopImageView.setScaleX(1.0f);
            } else {
                this.loopImageView.animate().cancel();
                this.loopImageView.setScaleY(0.0f);
                this.loopImageView.setScaleX(0.0f);
            }
            this.isEnter = false;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!this.touchable || ReactionsContainerLayout.this.cancelPressedAnimation != null) {
                return false;
            }
            if (motionEvent.getAction() == 0) {
                this.pressed = true;
                this.pressedX = motionEvent.getX();
                this.pressedY = motionEvent.getY();
                if (this.sideScale == 1.0f) {
                    AndroidUtilities.runOnUIThread(this.longPressRunnable, (long) ViewConfiguration.getLongPressTimeout());
                }
            }
            float scaledTouchSlop = ((float) ViewConfiguration.get(getContext()).getScaledTouchSlop()) * 2.0f;
            if ((motionEvent.getAction() == 2 && (Math.abs(this.pressedX - motionEvent.getX()) > scaledTouchSlop || Math.abs(this.pressedY - motionEvent.getY()) > scaledTouchSlop)) || motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (motionEvent.getAction() == 1 && this.pressed && ((ReactionsContainerLayout.this.pressedReaction == null || ReactionsContainerLayout.this.pressedProgress > 0.8f) && ReactionsContainerLayout.this.delegate != null)) {
                    boolean unused = ReactionsContainerLayout.this.clicked = true;
                    long currentTimeMillis = System.currentTimeMillis();
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    if (currentTimeMillis - reactionsContainerLayout.lastReactionSentTime > 300) {
                        reactionsContainerLayout.lastReactionSentTime = System.currentTimeMillis();
                        ReactionsContainerLayout.this.delegate.onReactionClicked(this, this.currentReaction, ReactionsContainerLayout.this.pressedProgress > 0.8f, false);
                    }
                }
                if (!ReactionsContainerLayout.this.clicked) {
                    ReactionsContainerLayout.this.cancelPressed();
                }
                AndroidUtilities.cancelRunOnUIThread(this.longPressRunnable);
                this.pressed = false;
            }
            return true;
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            if (this.selected) {
                canvas.drawCircle((float) (getMeasuredWidth() >> 1), (float) (getMeasuredHeight() >> 1), (float) ((getMeasuredWidth() >> 1) - AndroidUtilities.dp(1.0f)), ReactionsContainerLayout.this.selectedPaint);
            }
            AnimatedEmojiDrawable animatedEmojiDrawable = this.loopImageView.animatedEmojiDrawable;
            if (!(animatedEmojiDrawable == null || animatedEmojiDrawable.getImageReceiver() == null)) {
                this.loopImageView.animatedEmojiDrawable.getImageReceiver().setRoundRadius(this.selected ? AndroidUtilities.dp(6.0f) : 0);
            }
            super.dispatchDraw(canvas);
        }
    }

    /* access modifiers changed from: private */
    public void cancelPressed() {
        if (this.pressedReaction != null) {
            this.cancelPressedProgress = 0.0f;
            final float f = this.pressedProgress;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.cancelPressedAnimation = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float unused = ReactionsContainerLayout.this.cancelPressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    float unused2 = reactionsContainerLayout.pressedProgress = f * (1.0f - reactionsContainerLayout.cancelPressedProgress);
                    ReactionsContainerLayout.this.invalidate();
                }
            });
            this.cancelPressedAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    reactionsContainerLayout.cancelPressedAnimation = null;
                    float unused = reactionsContainerLayout.pressedProgress = 0.0f;
                    ReactionsLayoutInBubble.VisibleReaction unused2 = ReactionsContainerLayout.this.pressedReaction = null;
                    ReactionsContainerLayout.this.invalidate();
                }
            });
            this.cancelPressedAnimation.setDuration(150);
            this.cancelPressedAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.cancelPressedAnimation.start();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = objArr[0];
            if (tLRPC$ChatFull.id == this.waitingLoadingChatId && getVisibility() != 0 && !(tLRPC$ChatFull.available_reactions instanceof TLRPC$TL_chatReactionsNone)) {
                setMessage(this.messageObject, (TLRPC$ChatFull) null);
                setVisibility(0);
                startEnterAnimation();
            }
        }
    }

    public void setAlpha(float f) {
        if (getAlpha() != f && f == 0.0f) {
            this.lastVisibleViews.clear();
            for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
                if (this.recyclerListView.getChildAt(i) instanceof ReactionHolderView) {
                    ((ReactionHolderView) this.recyclerListView.getChildAt(i)).resetAnimation();
                }
            }
        }
        super.setAlpha(f);
    }

    public void setTranslationX(float f) {
        if (f != getTranslationX()) {
            super.setTranslationX(f);
        }
    }

    private class InternalImageView extends ImageView {
        ValueAnimator valueAnimator;

        public InternalImageView(Context context) {
            super(context);
        }

        public void play(int i) {
            invalidate();
            ValueAnimator valueAnimator2 = this.valueAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.removeAllListeners();
                this.valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{getScaleX(), 1.0f});
            this.valueAnimator = ofFloat;
            ofFloat.setInterpolator(AndroidUtilities.overshootInterpolator);
            this.valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    InternalImageView.this.setScaleX(floatValue);
                    InternalImageView.this.setScaleY(floatValue);
                    ReactionsContainerLayout.this.customReactionsContainer.invalidate();
                }
            });
            this.valueAnimator.setDuration(300);
            this.valueAnimator.start();
        }

        public void resetAnimation() {
            setScaleX(0.0f);
            setScaleY(0.0f);
            ReactionsContainerLayout.this.customReactionsContainer.invalidate();
            ValueAnimator valueAnimator2 = this.valueAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
        }
    }

    private class CustomReactionsContainer extends FrameLayout {
        Paint backgroundPaint = new Paint(1);

        public CustomReactionsContainer(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            this.backgroundPaint.setColor(ColorUtils.blendARGB(Theme.getColor("actionBarDefaultSubmenuItemIcon", ReactionsContainerLayout.this.resourcesProvider), Theme.getColor("dialogBackground", ReactionsContainerLayout.this.resourcesProvider), 0.7f));
            int measuredHeight = getMeasuredHeight() >> 1;
            int measuredWidth = getMeasuredWidth() >> 1;
            View childAt = getChildAt(0);
            int measuredWidth2 = (getMeasuredWidth() - AndroidUtilities.dp(6.0f)) >> 1;
            float unused = ReactionsContainerLayout.this.getPullingLeftProgress();
            float expandSize = ReactionsContainerLayout.this.expandSize();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set((float) (measuredWidth - measuredWidth2), ((float) (measuredHeight - measuredWidth2)) - expandSize, (float) (measuredWidth + measuredWidth2), ((float) (measuredHeight + measuredWidth2)) + expandSize);
            canvas.save();
            canvas.scale(childAt.getScaleX(), childAt.getScaleY(), (float) measuredWidth, (float) measuredHeight);
            float f = (float) measuredWidth2;
            canvas.drawRoundRect(rectF, f, f, this.backgroundPaint);
            canvas.restore();
            canvas.save();
            canvas.translate(0.0f, expandSize);
            super.dispatchDraw(canvas);
            canvas.restore();
        }
    }

    public float expandSize() {
        return (float) ((int) (getPullingLeftProgress() * ((float) AndroidUtilities.dp(6.0f))));
    }
}
