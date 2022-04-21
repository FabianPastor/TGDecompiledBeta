package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public class ReactionsContainerLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final int ALPHA_DURATION = 150;
    private static final float CLIP_PROGRESS = 0.25f;
    private static final float SCALE_PROGRESS = 0.75f;
    private static final float SIDE_SCALE = 0.6f;
    public static final Property<ReactionsContainerLayout, Float> TRANSITION_PROGRESS_VALUE = new Property<ReactionsContainerLayout, Float>(Float.class, "transitionProgress") {
        public Float get(ReactionsContainerLayout reactionsContainerLayout) {
            return Float.valueOf(reactionsContainerLayout.transitionProgress);
        }

        public void set(ReactionsContainerLayout object, Float value) {
            object.setTransitionProgress(value.floatValue());
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
    public List<TLRPC.TL_availableReaction> reactionsList;
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
    private List<String> triggeredReactions;
    private long waitingLoadingChatId;

    public interface ReactionsContainerDelegate {
        void onReactionClicked(View view, TLRPC.TL_availableReaction tL_availableReaction, boolean z);
    }

    public ReactionsContainerLayout(final Context context, int currentAccount2, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        boolean z = true;
        float dp = (float) AndroidUtilities.dp(8.0f);
        this.bigCircleRadius = dp;
        this.smallCircleRadius = dp / 2.0f;
        this.bigCircleOffset = AndroidUtilities.dp(36.0f);
        this.reactionsList = Collections.emptyList();
        this.location = new int[2];
        this.shadowPad = new Rect();
        this.triggeredReactions = new ArrayList();
        this.lastVisibleViews = new HashSet<>();
        this.lastVisibleViewsTmp = new HashSet<>();
        this.resourcesProvider = resourcesProvider2;
        this.currentAccount = currentAccount2;
        this.animationEnabled = (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) || SharedConfig.getDevicePerformanceClass() == 0) ? false : z;
        this.shadow = ContextCompat.getDrawable(context, NUM).mutate();
        Rect rect2 = this.shadowPad;
        int dp2 = AndroidUtilities.dp(7.0f);
        rect2.bottom = dp2;
        rect2.right = dp2;
        rect2.top = dp2;
        rect2.left = dp2;
        this.shadow.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelShadow"), PorterDuff.Mode.MULTIPLY));
        AnonymousClass2 r1 = new RecyclerListView(context) {
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (ReactionsContainerLayout.this.pressedReaction == null || !((ReactionHolderView) child).currentReaction.reaction.equals(ReactionsContainerLayout.this.pressedReaction)) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                return true;
            }
        };
        this.recyclerListView = r1;
        this.linearLayoutManager = new LinearLayoutManager(context, 0, false);
        r1.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    outRect.left = AndroidUtilities.dp(6.0f);
                }
                outRect.right = AndroidUtilities.dp(4.0f);
                if (position == ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                    outRect.right = AndroidUtilities.dp(6.0f);
                }
            }
        });
        r1.setLayoutManager(this.linearLayoutManager);
        r1.setOverScrollMode(2);
        AnonymousClass4 r0 = new RecyclerView.Adapter() {
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ReactionHolderView hv = new ReactionHolderView(context);
                int size = (ReactionsContainerLayout.this.getLayoutParams().height - ReactionsContainerLayout.this.getPaddingTop()) - ReactionsContainerLayout.this.getPaddingBottom();
                hv.setLayoutParams(new RecyclerView.LayoutParams(size - AndroidUtilities.dp(12.0f), size));
                return new RecyclerListView.Holder(hv);
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ReactionHolderView h = (ReactionHolderView) holder.itemView;
                h.setScaleX(1.0f);
                h.setScaleY(1.0f);
                h.setReaction((TLRPC.TL_availableReaction) ReactionsContainerLayout.this.reactionsList.get(position));
            }

            public int getItemCount() {
                return ReactionsContainerLayout.this.reactionsList.size();
            }
        };
        this.listAdapter = r0;
        r1.setAdapter(r0);
        r1.addOnScrollListener(new LeftRightShadowsListener());
        r1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                RecyclerView recyclerView2 = recyclerView;
                if (recyclerView.getChildCount() > 2) {
                    recyclerView2.getLocationInWindow(ReactionsContainerLayout.this.location);
                    int rX = ReactionsContainerLayout.this.location[0];
                    View ch1 = recyclerView2.getChildAt(0);
                    ch1.getLocationInWindow(ReactionsContainerLayout.this.location);
                    float s1 = ((1.0f - Math.min(1.0f, (-Math.min((float) (ReactionsContainerLayout.this.location[0] - rX), 0.0f)) / ((float) ch1.getWidth()))) * 0.39999998f) + 0.6f;
                    if (Float.isNaN(s1)) {
                        s1 = 1.0f;
                    }
                    ((ReactionHolderView) ch1).sideScale = s1;
                    View ch2 = recyclerView2.getChildAt(recyclerView.getChildCount() - 1);
                    ch2.getLocationInWindow(ReactionsContainerLayout.this.location);
                    float s2 = ((1.0f - Math.min(1.0f, (-Math.min((float) ((recyclerView.getWidth() + rX) - (ch2.getWidth() + ReactionsContainerLayout.this.location[0])), 0.0f)) / ((float) ch2.getWidth()))) * 0.39999998f) + 0.6f;
                    if (Float.isNaN(s2)) {
                        s2 = 1.0f;
                    }
                    ((ReactionHolderView) ch2).sideScale = s2;
                }
                for (int i = 1; i < ReactionsContainerLayout.this.recyclerListView.getChildCount() - 1; i++) {
                    ((ReactionHolderView) ReactionsContainerLayout.this.recyclerListView.getChildAt(i)).sideScale = 1.0f;
                }
                ReactionsContainerLayout.this.invalidate();
            }
        });
        r1.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int i = parent.getChildAdapterPosition(view);
                if (i == 0) {
                    outRect.left = AndroidUtilities.dp(8.0f);
                }
                if (i == ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                    outRect.right = AndroidUtilities.dp(8.0f);
                }
            }
        });
        addView(r1, LayoutHelper.createFrame(-1, -1.0f));
        invalidateShaders();
        this.bgPaint.setColor(Theme.getColor("actionBarDefaultSubmenuBackground", resourcesProvider2));
    }

    public void setDelegate(ReactionsContainerDelegate delegate2) {
        this.delegate = delegate2;
    }

    private void setReactionsList(List<TLRPC.TL_availableReaction> reactionsList2) {
        this.reactionsList = reactionsList2;
        if (reactionsList2.size() * ((getLayoutParams().height - getPaddingTop()) - getPaddingBottom()) < AndroidUtilities.dp(200.0f)) {
            getLayoutParams().width = -2;
        }
        this.listAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        float f;
        float f2;
        Canvas canvas2 = canvas;
        this.lastVisibleViewsTmp.clear();
        this.lastVisibleViewsTmp.addAll(this.lastVisibleViews);
        this.lastVisibleViews.clear();
        if (this.pressedReaction != null) {
            float f3 = this.pressedProgress;
            if (f3 != 1.0f) {
                float f4 = f3 + 0.010666667f;
                this.pressedProgress = f4;
                if (f4 >= 1.0f) {
                    this.pressedProgress = 1.0f;
                }
                invalidate();
            }
        }
        float cPr = (Math.max(0.25f, Math.min(this.transitionProgress, 1.0f)) - 0.25f) / 0.75f;
        float br = this.bigCircleRadius * cPr;
        float sr = this.smallCircleRadius * cPr;
        float f5 = this.pressedProgress;
        this.pressedViewScale = (f5 * 2.0f) + 1.0f;
        this.otherViewsScale = 1.0f - (f5 * 0.15f);
        int s = canvas.save();
        if (LocaleController.isRTL) {
            f2 = (float) getWidth();
            f = 0.125f;
        } else {
            f2 = (float) getWidth();
            f = 0.875f;
        }
        float pivotX = f2 * f;
        float f6 = this.transitionProgress;
        if (f6 <= 0.75f) {
            float sc = f6 / 0.75f;
            canvas2.scale(sc, sc, pivotX, ((float) getHeight()) / 2.0f);
        }
        float lt = 0.0f;
        float rt = 1.0f;
        if (LocaleController.isRTL) {
            rt = Math.max(0.25f, this.transitionProgress);
        } else {
            lt = 1.0f - Math.max(0.25f, this.transitionProgress);
        }
        this.rect.set(((float) getPaddingLeft()) + (((float) (getWidth() - getPaddingRight())) * lt), ((float) getPaddingTop()) + (((float) this.recyclerListView.getMeasuredHeight()) * (1.0f - this.otherViewsScale)), ((float) (getWidth() - getPaddingRight())) * rt, (float) (getHeight() - getPaddingBottom()));
        this.radius = this.rect.height() / 2.0f;
        this.shadow.setBounds((int) ((((float) getPaddingLeft()) + (((float) ((getWidth() - getPaddingRight()) + this.shadowPad.right)) * lt)) - ((float) this.shadowPad.left)), getPaddingTop() - this.shadowPad.top, (int) (((float) ((getWidth() - getPaddingRight()) + this.shadowPad.right)) * rt), (getHeight() - getPaddingBottom()) + this.shadowPad.bottom);
        this.shadow.draw(canvas2);
        canvas2.restoreToCount(s);
        int s2 = canvas.save();
        float f7 = this.transitionProgress;
        if (f7 <= 0.75f) {
            float sc2 = f7 / 0.75f;
            canvas2.scale(sc2, sc2, pivotX, ((float) getHeight()) / 2.0f);
        }
        RectF rectF = this.rect;
        float f8 = this.radius;
        canvas2.drawRoundRect(rectF, f8, f8, this.bgPaint);
        canvas2.restoreToCount(s2);
        this.mPath.rewind();
        Path path = this.mPath;
        RectF rectF2 = this.rect;
        float f9 = this.radius;
        path.addRoundRect(rectF2, f9, f9, Path.Direction.CW);
        int s3 = canvas.save();
        float var_ = this.transitionProgress;
        if (var_ <= 0.75f) {
            float sc3 = var_ / 0.75f;
            canvas2.scale(sc3, sc3, pivotX, ((float) getHeight()) / 2.0f);
        }
        if (this.transitionProgress != 0.0f && getAlpha() == 1.0f) {
            int delay = 0;
            for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
                ReactionHolderView view = (ReactionHolderView) this.recyclerListView.getChildAt(i);
                checkPressedProgress(canvas2, view);
                if (view.backupImageView.getImageReceiver().getLottieAnimation() != null) {
                    if (view.getX() + (((float) view.getMeasuredWidth()) / 2.0f) > 0.0f && view.getX() + (((float) view.getMeasuredWidth()) / 2.0f) < ((float) this.recyclerListView.getWidth())) {
                        if (!this.lastVisibleViewsTmp.contains(view)) {
                            view.play(delay);
                            delay += 30;
                        }
                        this.lastVisibleViews.add(view);
                    } else if (!view.isEnter) {
                        view.resetAnimation();
                    }
                }
            }
        }
        canvas2.clipPath(this.mPath);
        canvas2.translate(((float) ((LocaleController.isRTL ? -1 : 1) * getWidth())) * (1.0f - this.transitionProgress), 0.0f);
        super.dispatchDraw(canvas);
        if (this.leftShadowPaint != null) {
            this.leftShadowPaint.setAlpha((int) (Utilities.clamp(this.leftAlpha * this.transitionProgress, 1.0f, 0.0f) * 255.0f));
            canvas2.drawRect(this.rect, this.leftShadowPaint);
        }
        if (this.rightShadowPaint != null) {
            this.rightShadowPaint.setAlpha((int) (255.0f * Utilities.clamp(this.rightAlpha * this.transitionProgress, 1.0f, 0.0f)));
            canvas2.drawRect(this.rect, this.rightShadowPaint);
        }
        canvas2.restoreToCount(s3);
        canvas.save();
        canvas2.clipRect(0.0f, this.rect.bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight());
        float cx = (float) (LocaleController.isRTL ? this.bigCircleOffset : getWidth() - this.bigCircleOffset);
        float cy = (float) (getHeight() - getPaddingBottom());
        int sPad = AndroidUtilities.dp(3.0f);
        int i2 = s3;
        float var_ = pivotX;
        this.shadow.setBounds((int) ((cx - br) - (((float) sPad) * cPr)), (int) ((cy - br) - (((float) sPad) * cPr)), (int) (cx + br + (((float) sPad) * cPr)), (int) (cy + br + (((float) sPad) * cPr)));
        this.shadow.draw(canvas2);
        canvas2.drawCircle(cx, cy, br, this.bgPaint);
        float cx2 = LocaleController.isRTL ? ((float) this.bigCircleOffset) - this.bigCircleRadius : ((float) (getWidth() - this.bigCircleOffset)) + this.bigCircleRadius;
        float cy2 = (((float) getHeight()) - this.smallCircleRadius) - ((float) sPad);
        int sPad2 = -AndroidUtilities.dp(1.0f);
        this.shadow.setBounds((int) ((cx2 - br) - (((float) sPad2) * cPr)), (int) ((cy2 - br) - (((float) sPad2) * cPr)), (int) (cx2 + br + (((float) sPad2) * cPr)), (int) (cy2 + br + (((float) sPad2) * cPr)));
        this.shadow.draw(canvas2);
        canvas2.drawCircle(cx2, cy2, sr, this.bgPaint);
        canvas.restore();
    }

    private void checkPressedProgress(Canvas canvas, ReactionHolderView view) {
        if (view.currentReaction.reaction.equals(this.pressedReaction)) {
            view.setPivotX((float) (view.getMeasuredWidth() >> 1));
            view.setPivotY(view.backupImageView.getY() + ((float) view.backupImageView.getMeasuredHeight()));
            view.setScaleX(this.pressedViewScale);
            view.setScaleY(this.pressedViewScale);
            if (!this.clicked) {
                if (this.cancelPressedAnimation == null) {
                    view.pressedBackupImageView.setVisibility(0);
                    view.pressedBackupImageView.setAlpha(1.0f);
                    if (view.pressedBackupImageView.getImageReceiver().hasBitmapImage()) {
                        view.backupImageView.setAlpha(0.0f);
                    }
                } else {
                    view.pressedBackupImageView.setAlpha(1.0f - this.cancelPressedProgress);
                    view.backupImageView.setAlpha(this.cancelPressedProgress);
                }
                if (this.pressedProgress == 1.0f) {
                    this.clicked = true;
                    if (System.currentTimeMillis() - this.lastReactionSentTime > 300) {
                        this.lastReactionSentTime = System.currentTimeMillis();
                        this.delegate.onReactionClicked(view, view.currentReaction, true);
                    }
                }
            }
            canvas.save();
            float x = this.recyclerListView.getX() + view.getX();
            float additionalWidth = ((((float) view.getMeasuredWidth()) * view.getScaleX()) - ((float) view.getMeasuredWidth())) / 2.0f;
            if (x - additionalWidth < 0.0f && view.getTranslationX() >= 0.0f) {
                view.setTranslationX(-(x - additionalWidth));
            } else if (((float) view.getMeasuredWidth()) + x + additionalWidth <= ((float) getMeasuredWidth()) || view.getTranslationX() > 0.0f) {
                view.setTranslationX(0.0f);
            } else {
                view.setTranslationX(((((float) getMeasuredWidth()) - x) - ((float) view.getMeasuredWidth())) - additionalWidth);
            }
            canvas.translate(this.recyclerListView.getX() + view.getX(), this.recyclerListView.getY() + view.getY());
            canvas.scale(view.getScaleX(), view.getScaleY(), view.getPivotX(), view.getPivotY());
            view.draw(canvas);
            canvas.restore();
            return;
        }
        int position = this.recyclerListView.getChildAdapterPosition(view);
        float translationX = ((((float) view.getMeasuredWidth()) * (this.pressedViewScale - 1.0f)) / 3.0f) - ((((float) view.getMeasuredWidth()) * (1.0f - this.otherViewsScale)) * ((float) (Math.abs(this.pressedReactionPosition - position) - 1)));
        if (position < this.pressedReactionPosition) {
            view.setPivotX(0.0f);
            view.setTranslationX(-translationX);
        } else {
            view.setPivotX((float) view.getMeasuredWidth());
            view.setTranslationX(translationX);
        }
        view.setPivotY(view.backupImageView.getY() + ((float) view.backupImageView.getMeasuredHeight()));
        view.setScaleX(this.otherViewsScale);
        view.setScaleY(this.otherViewsScale);
        view.backupImageView.setScaleX(view.sideScale);
        view.backupImageView.setScaleY(view.sideScale);
        view.pressedBackupImageView.setVisibility(4);
        view.backupImageView.setAlpha(1.0f);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateShaders();
    }

    private void invalidateShaders() {
        int dp = AndroidUtilities.dp(24.0f);
        float cy = ((float) getHeight()) / 2.0f;
        float f = cy;
        float f2 = cy;
        int color = Theme.getColor("actionBarDefaultSubmenuBackground");
        this.leftShadowPaint.setShader(new LinearGradient(0.0f, f, (float) dp, f2, color, 0, Shader.TileMode.CLAMP));
        this.rightShadowPaint.setShader(new LinearGradient((float) getWidth(), f, (float) (getWidth() - dp), f2, color, 0, Shader.TileMode.CLAMP));
        invalidate();
    }

    public void setTransitionProgress(float transitionProgress2) {
        this.transitionProgress = transitionProgress2;
        invalidate();
    }

    public void setMessage(MessageObject message, TLRPC.ChatFull chatFull) {
        List<TLRPC.TL_availableReaction> l;
        this.messageObject = message;
        TLRPC.ChatFull reactionsChat = chatFull;
        if (!message.isForwardedChannelPost() || (reactionsChat = MessagesController.getInstance(this.currentAccount).getChatFull(-message.getFromChatId())) != null) {
            if (reactionsChat != null) {
                l = new ArrayList<>(reactionsChat.available_reactions.size());
                Iterator<String> it = reactionsChat.available_reactions.iterator();
                while (it.hasNext()) {
                    String s = it.next();
                    Iterator<TLRPC.TL_availableReaction> it2 = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList().iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        TLRPC.TL_availableReaction a = it2.next();
                        if (a.reaction.equals(s)) {
                            l.add(a);
                            break;
                        }
                    }
                }
            } else {
                l = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
            }
            setReactionsList(l);
            return;
        }
        this.waitingLoadingChatId = -message.getFromChatId();
        MessagesController.getInstance(this.currentAccount).loadFullChat(-message.getFromChatId(), 0, true);
        setVisibility(4);
    }

    public void startEnterAnimation() {
        setTransitionProgress(0.0f);
        setAlpha(1.0f);
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, TRANSITION_PROGRESS_VALUE, new float[]{0.0f, 1.0f}).setDuration(400);
        animator.setInterpolator(new OvershootInterpolator(1.004f));
        animator.start();
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

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            boolean r = false;
            boolean l = ReactionsContainerLayout.this.linearLayoutManager.findFirstVisibleItemPosition() != 0;
            float f = 1.0f;
            if (l != this.leftVisible) {
                ValueAnimator valueAnimator = this.leftAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.leftAnimator = startAnimator(ReactionsContainerLayout.this.leftAlpha, l ? 1.0f : 0.0f, new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda1(this), new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda3(this));
                this.leftVisible = l;
            }
            if (ReactionsContainerLayout.this.linearLayoutManager.findLastVisibleItemPosition() != ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                r = true;
            }
            if (r != this.rightVisible) {
                ValueAnimator valueAnimator2 = this.rightAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                float access$1000 = ReactionsContainerLayout.this.rightAlpha;
                if (!r) {
                    f = 0.0f;
                }
                this.rightAnimator = startAnimator(access$1000, f, new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda2(this), new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda4(this));
                this.rightVisible = r;
            }
        }

        /* renamed from: lambda$onScrolled$0$org-telegram-ui-Components-ReactionsContainerLayout$LeftRightShadowsListener  reason: not valid java name */
        public /* synthetic */ void m4285x9b48f4c4(Float aFloat) {
            ReactionsContainerLayout.this.leftShadowPaint.setAlpha((int) (ReactionsContainerLayout.this.leftAlpha = aFloat.floatValue() * 255.0f));
            ReactionsContainerLayout.this.invalidate();
        }

        /* renamed from: lambda$onScrolled$1$org-telegram-ui-Components-ReactionsContainerLayout$LeftRightShadowsListener  reason: not valid java name */
        public /* synthetic */ void m4286xb44a4663() {
            this.leftAnimator = null;
        }

        /* renamed from: lambda$onScrolled$2$org-telegram-ui-Components-ReactionsContainerLayout$LeftRightShadowsListener  reason: not valid java name */
        public /* synthetic */ void m4287xcd4b9802(Float aFloat) {
            ReactionsContainerLayout.this.rightShadowPaint.setAlpha((int) (ReactionsContainerLayout.this.rightAlpha = aFloat.floatValue() * 255.0f));
            ReactionsContainerLayout.this.invalidate();
        }

        /* renamed from: lambda$onScrolled$3$org-telegram-ui-Components-ReactionsContainerLayout$LeftRightShadowsListener  reason: not valid java name */
        public /* synthetic */ void m4288xe64ce9a1() {
            this.rightAnimator = null;
        }

        private ValueAnimator startAnimator(float fromAlpha, float toAlpha, Consumer<Float> callback, final Runnable onEnd) {
            ValueAnimator a = ValueAnimator.ofFloat(new float[]{fromAlpha, toAlpha}).setDuration((long) (Math.abs(toAlpha - fromAlpha) * 150.0f));
            a.addUpdateListener(new ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda0(callback));
            a.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    onEnd.run();
                }
            });
            a.start();
            return a;
        }
    }

    public final class ReactionHolderView extends FrameLayout {
        public BackupImageView backupImageView;
        public TLRPC.TL_availableReaction currentReaction;
        /* access modifiers changed from: private */
        public boolean isEnter;
        Runnable longPressRunnable = new Runnable() {
            public void run() {
                ReactionHolderView.this.performHapticFeedback(0);
                int unused = ReactionsContainerLayout.this.pressedReactionPosition = ReactionsContainerLayout.this.reactionsList.indexOf(ReactionHolderView.this.currentReaction);
                String unused2 = ReactionsContainerLayout.this.pressedReaction = ReactionHolderView.this.currentReaction.reaction;
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
        public void setReaction(TLRPC.TL_availableReaction react) {
            TLRPC.TL_availableReaction tL_availableReaction = this.currentReaction;
            if (tL_availableReaction == null || !tL_availableReaction.reaction.equals(react.reaction)) {
                resetAnimation();
                this.currentReaction = react;
                SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(react.activate_animation, "windowBackgroundGray", 1.0f);
                TLRPC.TL_availableReaction tL_availableReaction2 = react;
                this.backupImageView.getImageReceiver().setImage(ImageLocation.getForDocument(this.currentReaction.appear_animation), "60_60_nolimit", (ImageLocation) null, (String) null, svgThumb, 0, "tgs", tL_availableReaction2, 0);
                this.pressedBackupImageView.getImageReceiver().setImage(ImageLocation.getForDocument(this.currentReaction.select_animation), "60_60_nolimit", (ImageLocation) null, (String) null, svgThumb, 0, "tgs", tL_availableReaction2, 0);
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            resetAnimation();
        }

        public boolean play(int delay) {
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
            if (delay == 0) {
                this.backupImageView.getImageReceiver().getLottieAnimation().stop();
                this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                this.playRunnable.run();
            } else {
                this.backupImageView.getImageReceiver().getLottieAnimation().stop();
                this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                AndroidUtilities.runOnUIThread(this.playRunnable, (long) delay);
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

        public boolean onTouchEvent(MotionEvent event) {
            if (ReactionsContainerLayout.this.cancelPressedAnimation != null) {
                return false;
            }
            if (event.getAction() == 0) {
                this.pressed = true;
                this.pressedX = event.getX();
                this.pressedY = event.getY();
                if (this.sideScale == 1.0f) {
                    AndroidUtilities.runOnUIThread(this.longPressRunnable, (long) ViewConfiguration.getLongPressTimeout());
                }
            }
            float touchSlop = ((float) ViewConfiguration.get(getContext()).getScaledTouchSlop()) * 2.0f;
            if ((event.getAction() == 2 && (Math.abs(this.pressedX - event.getX()) > touchSlop || Math.abs(this.pressedY - event.getY()) > touchSlop)) || event.getAction() == 1 || event.getAction() == 3) {
                if (event.getAction() == 1 && this.pressed && ((ReactionsContainerLayout.this.pressedReaction == null || ReactionsContainerLayout.this.pressedProgress > 0.8f) && ReactionsContainerLayout.this.delegate != null)) {
                    boolean unused = ReactionsContainerLayout.this.clicked = true;
                    if (System.currentTimeMillis() - ReactionsContainerLayout.this.lastReactionSentTime > 300) {
                        ReactionsContainerLayout.this.lastReactionSentTime = System.currentTimeMillis();
                        ReactionsContainerLayout.this.delegate.onReactionClicked(this, this.currentReaction, ReactionsContainerLayout.this.pressedProgress > 0.8f);
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
    }

    /* access modifiers changed from: private */
    public void cancelPressed() {
        if (this.pressedReaction != null) {
            this.cancelPressedProgress = 0.0f;
            final float fromProgress = this.pressedProgress;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.cancelPressedAnimation = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float unused = ReactionsContainerLayout.this.cancelPressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    float unused2 = reactionsContainerLayout.pressedProgress = fromProgress * (1.0f - reactionsContainerLayout.cancelPressedProgress);
                    ReactionsContainerLayout.this.invalidate();
                }
            });
            this.cancelPressedAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ReactionsContainerLayout.this.cancelPressedAnimation = null;
                    float unused = ReactionsContainerLayout.this.pressedProgress = 0.0f;
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = args[0];
            if (chatFull.id == this.waitingLoadingChatId && getVisibility() != 0 && !chatFull.available_reactions.isEmpty()) {
                setMessage(this.messageObject, (TLRPC.ChatFull) null);
                setVisibility(0);
                startEnterAnimation();
            }
        }
    }

    public void setAlpha(float alpha) {
        if (getAlpha() != alpha && alpha == 0.0f) {
            this.lastVisibleViews.clear();
            for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
                ((ReactionHolderView) this.recyclerListView.getChildAt(i)).resetAnimation();
            }
        }
        super.setAlpha(alpha);
    }
}
