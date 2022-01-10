package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.ui.ActionBar.Theme;
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
    /* access modifiers changed from: private */
    public final boolean animationEnabled;
    private Paint bgPaint = new Paint(1);
    private int bigCircleOffset;
    private float bigCircleRadius;
    private int currentAccount;
    private ReactionsContainerDelegate delegate;
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
    private float radius = ((float) AndroidUtilities.dp(72.0f));
    /* access modifiers changed from: private */
    public List<TLRPC$TL_availableReaction> reactionsList;
    private RectF rect = new RectF();
    public final RecyclerListView recyclerListView;
    Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public float rightAlpha;
    /* access modifiers changed from: private */
    public Paint rightShadowPaint = new Paint(1);
    private Drawable shadow;
    private Rect shadowPad;
    private float smallCircleRadius;
    /* access modifiers changed from: private */
    public float transitionProgress = 1.0f;
    private long waitingLoadingChatId;

    public interface ReactionsContainerDelegate {
        void onReactionClicked(View view, TLRPC$TL_availableReaction tLRPC$TL_availableReaction);
    }

    static {
        new Random();
    }

    public ReactionsContainerLayout(final Context context, int i, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        boolean z = true;
        float dp = (float) AndroidUtilities.dp(8.0f);
        this.bigCircleRadius = dp;
        this.smallCircleRadius = dp / 2.0f;
        this.bigCircleOffset = AndroidUtilities.dp(36.0f);
        this.reactionsList = Collections.emptyList();
        this.location = new int[2];
        this.shadowPad = new Rect();
        new ArrayList();
        this.lastVisibleViews = new HashSet<>();
        this.lastVisibleViewsTmp = new HashSet<>();
        this.resourcesProvider = resourcesProvider2;
        this.currentAccount = i;
        this.animationEnabled = (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) || SharedConfig.getDevicePerformanceClass() == 0) ? false : z;
        this.shadow = ContextCompat.getDrawable(context, NUM).mutate();
        Rect rect2 = this.shadowPad;
        int dp2 = AndroidUtilities.dp(7.0f);
        rect2.bottom = dp2;
        rect2.right = dp2;
        rect2.top = dp2;
        rect2.left = dp2;
        this.shadow.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelShadow"), PorterDuff.Mode.MULTIPLY));
        RecyclerListView recyclerListView2 = new RecyclerListView(context);
        this.recyclerListView = recyclerListView2;
        this.linearLayoutManager = new LinearLayoutManager(context, 0, false);
        recyclerListView2.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                super.getItemOffsets(rect, view, recyclerView, state);
                int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                if (childAdapterPosition == 0) {
                    rect.left = AndroidUtilities.dp(6.0f);
                }
                rect.right = AndroidUtilities.dp(4.0f);
                if (childAdapterPosition == ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                    rect.right = AndroidUtilities.dp(6.0f);
                }
            }
        });
        recyclerListView2.setLayoutManager(this.linearLayoutManager);
        recyclerListView2.setOverScrollMode(2);
        AnonymousClass3 r0 = new RecyclerView.Adapter() {
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                ReactionHolderView reactionHolderView = new ReactionHolderView(context);
                int paddingTop = (ReactionsContainerLayout.this.getLayoutParams().height - ReactionsContainerLayout.this.getPaddingTop()) - ReactionsContainerLayout.this.getPaddingBottom();
                reactionHolderView.setLayoutParams(new RecyclerView.LayoutParams(paddingTop - AndroidUtilities.dp(12.0f), paddingTop));
                return new RecyclerListView.Holder(reactionHolderView);
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ReactionHolderView reactionHolderView = (ReactionHolderView) viewHolder.itemView;
                reactionHolderView.setScaleX(1.0f);
                reactionHolderView.setScaleY(1.0f);
                reactionHolderView.setReaction((TLRPC$TL_availableReaction) ReactionsContainerLayout.this.reactionsList.get(i));
            }

            public int getItemCount() {
                return ReactionsContainerLayout.this.reactionsList.size();
            }
        };
        this.listAdapter = r0;
        recyclerListView2.setAdapter(r0);
        recyclerListView2.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ReactionsContainerLayout$$ExternalSyntheticLambda0(this));
        recyclerListView2.addOnScrollListener(new LeftRightShadowsListener());
        recyclerListView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    childAt.setScaleX(min);
                    childAt.setScaleY(min);
                    View childAt2 = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                    childAt2.getLocationInWindow(ReactionsContainerLayout.this.location);
                    float min2 = ((1.0f - Math.min(1.0f, (-Math.min((float) ((i3 + recyclerView.getWidth()) - (ReactionsContainerLayout.this.location[0] + childAt2.getWidth())), 0.0f)) / ((float) childAt2.getWidth()))) * 0.39999998f) + 0.6f;
                    if (Float.isNaN(min2)) {
                        min2 = 1.0f;
                    }
                    childAt2.setScaleX(min2);
                    childAt2.setScaleY(min2);
                }
                for (int i4 = 1; i4 < ReactionsContainerLayout.this.recyclerListView.getChildCount() - 1; i4++) {
                    View childAt3 = ReactionsContainerLayout.this.recyclerListView.getChildAt(i4);
                    childAt3.setScaleX(1.0f);
                    childAt3.setScaleY(1.0f);
                }
                ReactionsContainerLayout.this.invalidate();
            }
        });
        recyclerListView2.addItemDecoration(new RecyclerView.ItemDecoration() {
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
        addView(recyclerListView2, LayoutHelper.createFrame(-1, -1.0f));
        invalidateShaders();
        this.bgPaint.setColor(Theme.getColor("actionBarDefaultSubmenuBackground", resourcesProvider2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, int i) {
        ReactionHolderView reactionHolderView = (ReactionHolderView) view;
        ReactionsContainerDelegate reactionsContainerDelegate = this.delegate;
        if (reactionsContainerDelegate != null) {
            reactionsContainerDelegate.onReactionClicked(reactionHolderView, reactionHolderView.currentReaction);
        }
    }

    public void setDelegate(ReactionsContainerDelegate reactionsContainerDelegate) {
        this.delegate = reactionsContainerDelegate;
    }

    @SuppressLint({"NotifyDataSetChanged"})
    private void setReactionsList(List<TLRPC$TL_availableReaction> list) {
        this.reactionsList = list;
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
        this.lastVisibleViewsTmp.clear();
        this.lastVisibleViewsTmp.addAll(this.lastVisibleViews);
        this.lastVisibleViews.clear();
        if (this.transitionProgress != 0.0f) {
            int i = 0;
            for (int i2 = 0; i2 < this.recyclerListView.getChildCount(); i2++) {
                ReactionHolderView reactionHolderView = (ReactionHolderView) this.recyclerListView.getChildAt(i2);
                if (reactionHolderView.backupImageView.getImageReceiver().getLottieAnimation() != null) {
                    if (reactionHolderView.getX() + (((float) reactionHolderView.getMeasuredWidth()) / 2.0f) > 0.0f && reactionHolderView.getX() + (((float) reactionHolderView.getMeasuredWidth()) / 2.0f) < ((float) this.recyclerListView.getWidth())) {
                        if (!this.lastVisibleViewsTmp.contains(reactionHolderView)) {
                            reactionHolderView.play(i);
                            i += 30;
                        }
                        this.lastVisibleViews.add(reactionHolderView);
                    } else if (!reactionHolderView.isEnter) {
                        reactionHolderView.resetAnimation();
                    }
                }
            }
        }
        float max = (Math.max(0.25f, Math.min(this.transitionProgress, 1.0f)) - 0.25f) / 0.75f;
        float f5 = this.bigCircleRadius * max;
        float f6 = this.smallCircleRadius * max;
        float width = (float) (LocaleController.isRTL ? this.bigCircleOffset : getWidth() - this.bigCircleOffset);
        float height = (float) (getHeight() - getPaddingBottom());
        float dp = (float) AndroidUtilities.dp(3.0f);
        float f7 = dp * max;
        this.shadow.setBounds((int) ((width - f5) - f7), (int) ((height - f5) - f7), (int) (width + f5 + f7), (int) (height + f5 + f7));
        this.shadow.draw(canvas2);
        canvas2.drawCircle(width, height, f5, this.bgPaint);
        float width2 = LocaleController.isRTL ? ((float) this.bigCircleOffset) - this.bigCircleRadius : ((float) (getWidth() - this.bigCircleOffset)) + this.bigCircleRadius;
        float height2 = (((float) getHeight()) - this.smallCircleRadius) - dp;
        float f8 = ((float) (-AndroidUtilities.dp(1.0f))) * max;
        this.shadow.setBounds((int) ((width2 - f5) - f8), (int) ((height2 - f5) - f8), (int) (width2 + f5 + f8), (int) (height2 + f5 + f8));
        this.shadow.draw(canvas2);
        canvas2.drawCircle(width2, height2, f6, this.bgPaint);
        int save = canvas.save();
        this.mPath.rewind();
        this.mPath.addCircle((float) (LocaleController.isRTL ? this.bigCircleOffset : getWidth() - this.bigCircleOffset), (float) (getHeight() - getPaddingBottom()), f5, Path.Direction.CW);
        canvas2.clipPath(this.mPath, Region.Op.DIFFERENCE);
        if (LocaleController.isRTL) {
            f2 = (float) getWidth();
            f = 0.125f;
        } else {
            f2 = (float) getWidth();
            f = 0.875f;
        }
        float f9 = f2 * f;
        float var_ = this.transitionProgress;
        if (var_ <= 0.75f) {
            float var_ = var_ / 0.75f;
            canvas2.scale(var_, var_, f9, ((float) getHeight()) / 2.0f);
        }
        if (LocaleController.isRTL) {
            f3 = Math.max(0.25f, this.transitionProgress);
            f4 = 0.0f;
        } else {
            f4 = 1.0f - Math.max(0.25f, this.transitionProgress);
            f3 = 1.0f;
        }
        this.rect.set(((float) getPaddingLeft()) + (((float) (getWidth() - getPaddingRight())) * f4), (float) getPaddingTop(), ((float) (getWidth() - getPaddingRight())) * f3, (float) (getHeight() - getPaddingBottom()));
        Drawable drawable = this.shadow;
        int width3 = getWidth() - getPaddingRight();
        Rect rect2 = this.shadowPad;
        drawable.setBounds((int) ((((float) getPaddingLeft()) + (((float) (width3 + rect2.right)) * f4)) - ((float) rect2.left)), getPaddingTop() - this.shadowPad.top, (int) (((float) ((getWidth() - getPaddingRight()) + this.shadowPad.right)) * f3), (getHeight() - getPaddingBottom()) + this.shadowPad.bottom);
        this.shadow.draw(canvas2);
        canvas2.restoreToCount(save);
        int save2 = canvas.save();
        float var_ = this.transitionProgress;
        if (var_ <= 0.75f) {
            float var_ = var_ / 0.75f;
            canvas2.scale(var_, var_, f9, ((float) getHeight()) / 2.0f);
        }
        RectF rectF = this.rect;
        float var_ = this.radius;
        canvas2.drawRoundRect(rectF, var_, var_, this.bgPaint);
        canvas2.restoreToCount(save2);
        this.mPath.rewind();
        Path path = this.mPath;
        RectF rectF2 = this.rect;
        float var_ = this.radius;
        path.addRoundRect(rectF2, var_, var_, Path.Direction.CW);
        int save3 = canvas.save();
        float var_ = this.transitionProgress;
        if (var_ <= 0.75f) {
            float var_ = var_ / 0.75f;
            canvas2.scale(var_, var_, f9, ((float) getHeight()) / 2.0f);
        }
        canvas2.clipPath(this.mPath);
        canvas2.translate(((float) ((LocaleController.isRTL ? -1 : 1) * getWidth())) * (1.0f - this.transitionProgress), 0.0f);
        super.dispatchDraw(canvas);
        canvas2.restoreToCount(save3);
        int save4 = canvas.save();
        if (LocaleController.isRTL) {
            f3 = Math.max(0.25f, Math.min(1.0f, this.transitionProgress));
        } else {
            f4 = 1.0f - Math.max(0.25f, Math.min(1.0f, this.transitionProgress));
        }
        this.rect.set(((float) getPaddingLeft()) + (((float) (getWidth() - getPaddingRight())) * f4), (float) getPaddingTop(), ((float) (getWidth() - getPaddingRight())) * f3, (float) (getHeight() - getPaddingBottom()));
        this.mPath.rewind();
        Path path2 = this.mPath;
        RectF rectF3 = this.rect;
        float var_ = this.radius;
        path2.addRoundRect(rectF3, var_, var_, Path.Direction.CW);
        canvas2.clipPath(this.mPath);
        Paint paint = this.leftShadowPaint;
        if (paint != null) {
            paint.setAlpha((int) (this.leftAlpha * this.transitionProgress * 255.0f));
            canvas2.drawRect(this.rect, this.leftShadowPaint);
        }
        Paint paint2 = this.rightShadowPaint;
        if (paint2 != null) {
            paint2.setAlpha((int) (this.rightAlpha * this.transitionProgress * 255.0f));
            canvas2.drawRect(this.rect, this.rightShadowPaint);
        }
        canvas2.restoreToCount(save4);
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
        List list;
        this.messageObject = messageObject2;
        if (!messageObject2.isForwardedChannelPost() || (tLRPC$ChatFull = MessagesController.getInstance(this.currentAccount).getChatFull(-messageObject2.getFromChatId())) != null) {
            if (tLRPC$ChatFull != null) {
                list = new ArrayList(tLRPC$ChatFull.available_reactions.size());
                Iterator<String> it = tLRPC$ChatFull.available_reactions.iterator();
                while (it.hasNext()) {
                    String next = it.next();
                    Iterator<TLRPC$TL_availableReaction> it2 = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList().iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        TLRPC$TL_availableReaction next2 = it2.next();
                        if (next2.reaction.equals(next)) {
                            list.add(next2);
                            break;
                        }
                    }
                }
            } else {
                list = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
            }
            setReactionsList(list);
            return;
        }
        this.waitingLoadingChatId = -messageObject2.getFromChatId();
        MessagesController.getInstance(this.currentAccount).loadFullChat(-messageObject2.getFromChatId(), 0, true);
        setVisibility(4);
    }

    public void startEnterAnimation() {
        setTransitionProgress(0.0f);
        setAlpha(1.0f);
        ObjectAnimator duration = ObjectAnimator.ofFloat(this, TRANSITION_PROGRESS_VALUE, new float[]{0.0f, 1.0f}).setDuration(400);
        duration.setInterpolator(new OvershootInterpolator(1.004f));
        duration.start();
    }

    public int getTotalWidth() {
        return (AndroidUtilities.dp(36.0f) * this.reactionsList.size()) + AndroidUtilities.dp(16.0f);
    }

    public int getItemsCount() {
        return this.reactionsList.size();
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
                float access$900 = ReactionsContainerLayout.this.rightAlpha;
                if (!z) {
                    f = 0.0f;
                }
                this.rightAnimator = startAnimator(access$900, f, new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda2(this), new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda3(this));
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
        public BackupImageView backupImageView;
        public TLRPC$TL_availableReaction currentReaction;
        /* access modifiers changed from: private */
        public boolean isEnter;
        Runnable playRunnable = new Runnable() {
            public void run() {
                if (ReactionHolderView.this.backupImageView.getImageReceiver().getLottieAnimation() != null && !ReactionHolderView.this.backupImageView.getImageReceiver().getLottieAnimation().isRunning() && !ReactionHolderView.this.backupImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                    ReactionHolderView.this.backupImageView.getImageReceiver().getLottieAnimation().start();
                }
            }
        };

        ReactionHolderView(Context context) {
            super(context);
            AnonymousClass2 r0 = new BackupImageView(context, ReactionsContainerLayout.this) {
                public void invalidate() {
                    super.invalidate();
                    ReactionsContainerLayout.this.invalidate();
                }
            };
            this.backupImageView = r0;
            r0.getImageReceiver().setAutoRepeat(0);
            this.backupImageView.getImageReceiver().setAllowStartLottieAnimation(false);
            addView(this.backupImageView, LayoutHelper.createFrame(34, 34, 17));
        }

        /* access modifiers changed from: private */
        public void setReaction(TLRPC$TL_availableReaction tLRPC$TL_availableReaction) {
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction2 = this.currentReaction;
            if (tLRPC$TL_availableReaction2 == null || !tLRPC$TL_availableReaction2.reaction.equals(tLRPC$TL_availableReaction.reaction)) {
                resetAnimation();
                this.currentReaction = tLRPC$TL_availableReaction;
                this.backupImageView.getImageReceiver().setImage(ImageLocation.getForDocument(this.currentReaction.appear_animation), "60_60_nolimit", (ImageLocation) null, (String) null, DocumentObject.getSvgThumb(tLRPC$TL_availableReaction.activate_animation, "windowBackgroundGray", 1.0f), 0, "tgs", tLRPC$TL_availableReaction, 0);
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
            if (this.backupImageView.getImageReceiver().getLottieAnimation() == null || this.backupImageView.getImageReceiver().getLottieAnimation().isGeneratingCache() || this.isEnter) {
                if (this.backupImageView.getImageReceiver().getLottieAnimation() != null && this.isEnter && !this.backupImageView.getImageReceiver().getLottieAnimation().isRunning() && !this.backupImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                    this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(this.backupImageView.getImageReceiver().getLottieAnimation().getFramesCount() - 1, false);
                }
                return false;
            }
            this.isEnter = true;
            if (i == 0) {
                this.backupImageView.getImageReceiver().getLottieAnimation().stop();
                this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                this.playRunnable.run();
            } else {
                this.backupImageView.getImageReceiver().getLottieAnimation().stop();
                this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                AndroidUtilities.runOnUIThread(this.playRunnable, (long) i);
            }
            return true;
        }

        public void resetAnimation() {
            AndroidUtilities.cancelRunOnUIThread(this.playRunnable);
            if (this.backupImageView.getImageReceiver().getLottieAnimation() != null && !this.backupImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                this.backupImageView.getImageReceiver().getLottieAnimation().stop();
                if (ReactionsContainerLayout.this.animationEnabled) {
                    this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false, true);
                } else {
                    this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(this.backupImageView.getImageReceiver().getLottieAnimation().getFramesCount() - 1, false, true);
                }
            }
            this.isEnter = false;
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
            if (tLRPC$ChatFull.id == this.waitingLoadingChatId && getVisibility() != 0 && !tLRPC$ChatFull.available_reactions.isEmpty()) {
                setMessage(this.messageObject, (TLRPC$ChatFull) null);
                setVisibility(0);
                startEnterAnimation();
            }
        }
    }
}
