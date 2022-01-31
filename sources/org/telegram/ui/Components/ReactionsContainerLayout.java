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
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
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
import org.telegram.messenger.SvgHelper;
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
    ValueAnimator cancelPressedAnimation;
    /* access modifiers changed from: private */
    public float cancelPressedProgress;
    /* access modifiers changed from: private */
    public boolean clicked;
    private int currentAccount;
    /* access modifiers changed from: private */
    public ReactionsContainerDelegate delegate;
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
    private float otherViewsScale;
    /* access modifiers changed from: private */
    public float pressedProgress;
    /* access modifiers changed from: private */
    public String pressedReaction;
    /* access modifiers changed from: private */
    public int pressedReactionPosition;
    private float pressedViewScale;
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
        void onReactionClicked(View view, TLRPC$TL_availableReaction tLRPC$TL_availableReaction, boolean z);
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
        AnonymousClass2 r7 = new RecyclerListView(context) {
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (ReactionsContainerLayout.this.pressedReaction == null || !((ReactionHolderView) view).currentReaction.equals(ReactionsContainerLayout.this.pressedReaction)) {
                    return super.drawChild(canvas, view, j);
                }
                return true;
            }
        };
        this.recyclerListView = r7;
        this.linearLayoutManager = new LinearLayoutManager(context, 0, false);
        r7.addItemDecoration(new RecyclerView.ItemDecoration() {
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
        r7.setLayoutManager(this.linearLayoutManager);
        r7.setOverScrollMode(2);
        AnonymousClass4 r0 = new RecyclerView.Adapter() {
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
        r7.setAdapter(r0);
        r7.addOnScrollListener(new LeftRightShadowsListener());
        r7.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    ((ReactionHolderView) childAt).sideScale = min;
                    View childAt2 = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                    childAt2.getLocationInWindow(ReactionsContainerLayout.this.location);
                    float min2 = ((1.0f - Math.min(1.0f, (-Math.min((float) ((i3 + recyclerView.getWidth()) - (ReactionsContainerLayout.this.location[0] + childAt2.getWidth())), 0.0f)) / ((float) childAt2.getWidth()))) * 0.39999998f) + 0.6f;
                    if (Float.isNaN(min2)) {
                        min2 = 1.0f;
                    }
                    ((ReactionHolderView) childAt2).sideScale = min2;
                }
                for (int i4 = 1; i4 < ReactionsContainerLayout.this.recyclerListView.getChildCount() - 1; i4++) {
                    ((ReactionHolderView) ReactionsContainerLayout.this.recyclerListView.getChildAt(i4)).sideScale = 1.0f;
                }
                ReactionsContainerLayout.this.invalidate();
            }
        });
        r7.addItemDecoration(new RecyclerView.ItemDecoration() {
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
        addView(r7, LayoutHelper.createFrame(-1, -1.0f));
        invalidateShaders();
        this.bgPaint.setColor(Theme.getColor("actionBarDefaultSubmenuBackground", resourcesProvider2));
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
        if (this.pressedReaction != null) {
            float f5 = this.pressedProgress;
            if (f5 != 1.0f) {
                float f6 = f5 + 0.008f;
                this.pressedProgress = f6;
                if (f6 >= 1.0f) {
                    this.pressedProgress = 1.0f;
                }
                invalidate();
            }
        }
        float max = (Math.max(0.25f, Math.min(this.transitionProgress, 1.0f)) - 0.25f) / 0.75f;
        float f7 = this.bigCircleRadius * max;
        float f8 = this.smallCircleRadius * max;
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
        this.rect.set(((float) getPaddingLeft()) + (((float) (getWidth() - getPaddingRight())) * f4), ((float) getPaddingTop()) + (((float) this.recyclerListView.getMeasuredHeight()) * (1.0f - this.otherViewsScale)), ((float) (getWidth() - getPaddingRight())) * f3, (float) (getHeight() - getPaddingBottom()));
        this.radius = this.rect.height() / 2.0f;
        Drawable drawable = this.shadow;
        int width = getWidth() - getPaddingRight();
        Rect rect2 = this.shadowPad;
        drawable.setBounds((int) ((((float) getPaddingLeft()) + (((float) (width + rect2.right)) * f4)) - ((float) rect2.left)), getPaddingTop() - this.shadowPad.top, (int) (((float) ((getWidth() - getPaddingRight()) + this.shadowPad.right)) * f3), (getHeight() - getPaddingBottom()) + this.shadowPad.bottom);
        this.shadow.draw(canvas2);
        canvas2.restoreToCount(save);
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
        if (this.transitionProgress != 0.0f) {
            int i = 0;
            for (int i2 = 0; i2 < this.recyclerListView.getChildCount(); i2++) {
                ReactionHolderView reactionHolderView = (ReactionHolderView) this.recyclerListView.getChildAt(i2);
                checkPressedProgress(canvas2, reactionHolderView);
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
        canvas2.clipPath(this.mPath);
        canvas2.translate(((float) ((LocaleController.isRTL ? -1 : 1) * getWidth())) * (1.0f - this.transitionProgress), 0.0f);
        super.dispatchDraw(canvas);
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
        canvas2.restoreToCount(save3);
        canvas.save();
        canvas2.clipRect(0.0f, this.rect.bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight());
        float width2 = (float) (LocaleController.isRTL ? this.bigCircleOffset : getWidth() - this.bigCircleOffset);
        float height = (float) (getHeight() - getPaddingBottom());
        float dp = (float) AndroidUtilities.dp(3.0f);
        float var_ = dp * max;
        this.shadow.setBounds((int) ((width2 - f7) - var_), (int) ((height - f7) - var_), (int) (width2 + f7 + var_), (int) (height + f7 + var_));
        this.shadow.draw(canvas2);
        canvas2.drawCircle(width2, height, f7, this.bgPaint);
        float width3 = LocaleController.isRTL ? ((float) this.bigCircleOffset) - this.bigCircleRadius : ((float) (getWidth() - this.bigCircleOffset)) + this.bigCircleRadius;
        float height2 = (((float) getHeight()) - this.smallCircleRadius) - dp;
        float var_ = ((float) (-AndroidUtilities.dp(1.0f))) * max;
        this.shadow.setBounds((int) ((width3 - f7) - var_), (int) ((height2 - f7) - var_), (int) (width3 + f7 + var_), (int) (f7 + height2 + var_));
        this.shadow.draw(canvas2);
        canvas2.drawCircle(width3, height2, f8, this.bgPaint);
        canvas.restore();
    }

    private void checkPressedProgress(Canvas canvas, ReactionHolderView reactionHolderView) {
        if (reactionHolderView.currentReaction.reaction.equals(this.pressedReaction)) {
            reactionHolderView.setPivotX((float) (reactionHolderView.getMeasuredWidth() >> 1));
            reactionHolderView.setPivotY(reactionHolderView.backupImageView.getY() + ((float) reactionHolderView.backupImageView.getMeasuredHeight()));
            reactionHolderView.setScaleX(this.pressedViewScale);
            reactionHolderView.setScaleY(this.pressedViewScale);
            if (!this.clicked) {
                if (this.cancelPressedAnimation == null) {
                    reactionHolderView.pressedBackupImageView.setVisibility(0);
                    reactionHolderView.pressedBackupImageView.setAlpha(1.0f);
                    if (reactionHolderView.pressedBackupImageView.getImageReceiver().hasBitmapImage()) {
                        reactionHolderView.backupImageView.setAlpha(0.0f);
                    }
                } else {
                    reactionHolderView.pressedBackupImageView.setAlpha(1.0f - this.cancelPressedProgress);
                    reactionHolderView.backupImageView.setAlpha(this.cancelPressedProgress);
                }
                if (this.pressedProgress == 1.0f) {
                    this.clicked = true;
                    this.delegate.onReactionClicked(reactionHolderView, reactionHolderView.currentReaction, true);
                }
            }
            canvas.save();
            canvas.translate(this.recyclerListView.getX() + reactionHolderView.getX(), this.recyclerListView.getY() + reactionHolderView.getY());
            canvas.scale(reactionHolderView.getScaleX(), reactionHolderView.getScaleY(), reactionHolderView.getPivotX(), reactionHolderView.getPivotY());
            reactionHolderView.draw(canvas);
            canvas.restore();
            return;
        }
        int childAdapterPosition = this.recyclerListView.getChildAdapterPosition(reactionHolderView);
        float measuredWidth = ((((float) reactionHolderView.getMeasuredWidth()) * (this.pressedViewScale - 1.0f)) / 3.0f) - ((((float) reactionHolderView.getMeasuredWidth()) * (1.0f - this.otherViewsScale)) * ((float) (Math.abs(this.pressedReactionPosition - childAdapterPosition) - 1)));
        if (childAdapterPosition < this.pressedReactionPosition) {
            reactionHolderView.setPivotX(0.0f);
            reactionHolderView.setTranslationX(-measuredWidth);
        } else {
            reactionHolderView.setPivotX((float) reactionHolderView.getMeasuredWidth());
            reactionHolderView.setTranslationX(measuredWidth);
        }
        reactionHolderView.setPivotY(reactionHolderView.backupImageView.getY() + ((float) reactionHolderView.backupImageView.getMeasuredHeight()));
        reactionHolderView.setScaleX(this.otherViewsScale);
        reactionHolderView.setScaleY(this.otherViewsScale);
        reactionHolderView.backupImageView.setScaleX(reactionHolderView.sideScale);
        reactionHolderView.backupImageView.setScaleY(reactionHolderView.sideScale);
        reactionHolderView.pressedBackupImageView.setVisibility(4);
        reactionHolderView.backupImageView.setAlpha(1.0f);
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
                float access$1000 = ReactionsContainerLayout.this.rightAlpha;
                if (!z) {
                    f = 0.0f;
                }
                this.rightAnimator = startAnimator(access$1000, f, new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda2(this), new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda3(this));
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
        Runnable longPressRunnable = new Runnable() {
            public void run() {
                ReactionHolderView.this.performHapticFeedback(0);
                ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                int unused = reactionsContainerLayout.pressedReactionPosition = reactionsContainerLayout.reactionsList.indexOf(ReactionHolderView.this.currentReaction);
                ReactionHolderView reactionHolderView = ReactionHolderView.this;
                String unused2 = ReactionsContainerLayout.this.pressedReaction = reactionHolderView.currentReaction.reaction;
                ReactionsContainerLayout.this.invalidate();
            }
        };
        Runnable playRunnable = new Runnable() {
            public void run() {
                if (ReactionHolderView.this.backupImageView.getImageReceiver().getLottieAnimation() != null && !ReactionHolderView.this.backupImageView.getImageReceiver().getLottieAnimation().isRunning() && !ReactionHolderView.this.backupImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                    ReactionHolderView.this.backupImageView.getImageReceiver().getLottieAnimation().start();
                }
            }
        };
        boolean pressed;
        public BackupImageView pressedBackupImageView;
        float pressedX;
        float pressedY;
        public float sideScale = 1.0f;

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
            this.pressedBackupImageView = new BackupImageView(context, ReactionsContainerLayout.this) {
                public void invalidate() {
                    super.invalidate();
                    ReactionsContainerLayout.this.invalidate();
                }
            };
            addView(this.backupImageView, LayoutHelper.createFrame(34, 34, 17));
            addView(this.pressedBackupImageView, LayoutHelper.createFrame(34, 34, 17));
        }

        /* access modifiers changed from: private */
        public void setReaction(TLRPC$TL_availableReaction tLRPC$TL_availableReaction) {
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction2 = this.currentReaction;
            if (tLRPC$TL_availableReaction2 == null || !tLRPC$TL_availableReaction2.reaction.equals(tLRPC$TL_availableReaction.reaction)) {
                resetAnimation();
                this.currentReaction = tLRPC$TL_availableReaction;
                SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$TL_availableReaction.activate_animation, "windowBackgroundGray", 1.0f);
                TLRPC$TL_availableReaction tLRPC$TL_availableReaction3 = tLRPC$TL_availableReaction;
                this.backupImageView.getImageReceiver().setImage(ImageLocation.getForDocument(this.currentReaction.appear_animation), "60_60_nolimit", (ImageLocation) null, (String) null, svgThumb, 0, "tgs", tLRPC$TL_availableReaction3, 0);
                this.pressedBackupImageView.getImageReceiver().setImage(ImageLocation.getForDocument(this.currentReaction.select_animation), "60_60_nolimit", (ImageLocation) null, (String) null, svgThumb, 0, "tgs", tLRPC$TL_availableReaction3, 0);
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

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (ReactionsContainerLayout.this.cancelPressedAnimation != null) {
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
                    ReactionsContainerLayout.this.delegate.onReactionClicked(this, this.currentReaction, ReactionsContainerLayout.this.pressedProgress > 0.8f);
                }
                if (!ReactionsContainerLayout.this.clicked) {
                    ReactionsContainerLayout.this.cancelPressed();
                }
                AndroidUtilities.cancelRunOnUIThread(this.longPressRunnable);
                this.pressed = false;
            }
            return true;
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
                    String unused2 = ReactionsContainerLayout.this.pressedReaction = null;
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
            if (tLRPC$ChatFull.id == this.waitingLoadingChatId && getVisibility() != 0 && !tLRPC$ChatFull.available_reactions.isEmpty()) {
                setMessage(this.messageObject, (TLRPC$ChatFull) null);
                setVisibility(0);
                startEnterAnimation();
            }
        }
    }
}
