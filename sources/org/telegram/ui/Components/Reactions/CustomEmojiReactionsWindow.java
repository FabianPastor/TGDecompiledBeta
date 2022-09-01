package org.telegram.ui.Components.Reactions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.ReactionsContainerLayout;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public class CustomEmojiReactionsWindow {
    BaseFragment baseFragment;
    ContainerView containerView;
    private float dismissProgress;
    private boolean dismissed;
    public RectF drawingRect = new RectF();
    boolean enterTransitionFinished;
    float enterTransitionProgress;
    /* access modifiers changed from: private */
    public int frameDrawCount = 0;
    float fromRadius;
    RectF fromRect = new RectF();
    /* access modifiers changed from: private */
    public boolean invalidatePath;
    private Runnable onDismiss;
    Path pathToClip = new Path();
    List<ReactionsLayoutInBubble.VisibleReaction> reactions;
    ReactionsContainerLayout reactionsContainerLayout;
    Theme.ResourcesProvider resourcesProvider;
    SelectAnimatedEmojiDialog selectAnimatedEmojiDialog;
    WindowManager windowManager;
    FrameLayout windowView;

    static /* synthetic */ int access$508(CustomEmojiReactionsWindow customEmojiReactionsWindow) {
        int i = customEmojiReactionsWindow.frameDrawCount;
        customEmojiReactionsWindow.frameDrawCount = i + 1;
        return i;
    }

    public CustomEmojiReactionsWindow(BaseFragment baseFragment2, List<ReactionsLayoutInBubble.VisibleReaction> list, HashSet<ReactionsLayoutInBubble.VisibleReaction> hashSet, ReactionsContainerLayout reactionsContainerLayout2, Theme.ResourcesProvider resourcesProvider2) {
        List<ReactionsLayoutInBubble.VisibleReaction> list2 = list;
        final ReactionsContainerLayout reactionsContainerLayout3 = reactionsContainerLayout2;
        this.reactions = list2;
        this.baseFragment = baseFragment2;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        this.resourcesProvider = resourcesProvider3;
        Context context = baseFragment2.getContext();
        AnonymousClass1 r0 = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void dispatchSetPressed(boolean z) {
            }

            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                if (keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 4) {
                    return false;
                }
                CustomEmojiReactionsWindow.this.dismiss();
                return true;
            }
        };
        this.windowView = r0;
        r0.setOnClickListener(new CustomEmojiReactionsWindow$$ExternalSyntheticLambda2(this));
        this.containerView = new ContainerView(context);
        final ReactionsContainerLayout reactionsContainerLayout4 = reactionsContainerLayout2;
        final BaseFragment baseFragment3 = baseFragment2;
        AnonymousClass2 r02 = new SelectAnimatedEmojiDialog(context, false, (Integer) null, 1, resourcesProvider3) {
            /* access modifiers changed from: protected */
            public void onReactionClick(SelectAnimatedEmojiDialog.ImageViewEmoji imageViewEmoji, ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
                reactionsContainerLayout4.onReactionClicked(imageViewEmoji, visibleReaction, false);
            }

            /* access modifiers changed from: protected */
            public void onEmojiSelected(View view, Long l, TLRPC$Document tLRPC$Document) {
                if (!UserConfig.getInstance(baseFragment3.getCurrentAccount()).isPremium()) {
                    CustomEmojiReactionsWindow.this.windowView.performHapticFeedback(3);
                    BulletinFactory.of(CustomEmojiReactionsWindow.this.windowView, (Theme.ResourcesProvider) null).createEmojiBulletin(tLRPC$Document, AndroidUtilities.replaceTags(LocaleController.getString("UnlockPremiumEmojiReaction", R.string.UnlockPremiumEmojiReaction)), LocaleController.getString("PremiumMore", R.string.PremiumMore), new CustomEmojiReactionsWindow$2$$ExternalSyntheticLambda0(this)).show();
                    return;
                }
                reactionsContainerLayout4.onReactionClicked(view, ReactionsLayoutInBubble.VisibleReaction.fromCustomEmoji(l), false);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onEmojiSelected$0() {
                CustomEmojiReactionsWindow.this.showUnlockPremiumAlert();
            }
        };
        this.selectAnimatedEmojiDialog = r02;
        r02.setOnLongPressedListener(new SelectAnimatedEmojiDialog.onLongPressedListener(this) {
            public void onLongPressed(SelectAnimatedEmojiDialog.ImageViewEmoji imageViewEmoji) {
                if (imageViewEmoji.isDefaultReaction) {
                    reactionsContainerLayout3.onReactionClicked(imageViewEmoji, imageViewEmoji.reaction, true);
                } else {
                    reactionsContainerLayout3.onReactionClicked(imageViewEmoji, ReactionsLayoutInBubble.VisibleReaction.fromCustomEmoji(Long.valueOf(imageViewEmoji.span.documentId)), true);
                }
            }
        });
        this.selectAnimatedEmojiDialog.setOnRecentClearedListener(new SelectAnimatedEmojiDialog.onRecentClearedListener(this) {
            public void onRecentCleared() {
                reactionsContainerLayout3.clearRecentReactions();
            }
        });
        this.selectAnimatedEmojiDialog.setRecentReactions(list2);
        this.selectAnimatedEmojiDialog.setSelectedReactions(hashSet);
        this.selectAnimatedEmojiDialog.setDrawBackground(false);
        this.selectAnimatedEmojiDialog.onShow((Runnable) null);
        this.containerView.addView(this.selectAnimatedEmojiDialog, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
        this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1.0f, 48, 16.0f, 16.0f, 16.0f, 16.0f));
        this.windowView.setClipChildren(false);
        new EditTextBoldCursor(context) {
            boolean focusable = false;

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (!this.focusable) {
                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.height = -1;
                    layoutParams.width = -1;
                    layoutParams.type = 1000;
                    layoutParams.flags = 65792;
                    layoutParams.format = -3;
                    CustomEmojiReactionsWindow customEmojiReactionsWindow = CustomEmojiReactionsWindow.this;
                    customEmojiReactionsWindow.windowManager.updateViewLayout(customEmojiReactionsWindow.windowView, layoutParams);
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        }.setBackgroundColor(-16777216);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = -1;
        layoutParams.width = -1;
        layoutParams.type = 1000;
        layoutParams.flags = 65800;
        layoutParams.format = -3;
        WindowManager windowManager2 = baseFragment2.getParentActivity().getWindowManager();
        this.windowManager = windowManager2;
        windowManager2.addView(this.windowView, layoutParams);
        this.reactionsContainerLayout = reactionsContainerLayout3;
        reactionsContainerLayout3.prepareAnimation(true);
        this.containerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                CustomEmojiReactionsWindow.this.containerView.removeOnLayoutChangeListener(this);
                reactionsContainerLayout3.prepareAnimation(false);
                CustomEmojiReactionsWindow.this.createTransition(true);
            }
        });
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 7);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public void showUnlockPremiumAlert() {
        BaseFragment baseFragment2 = this.baseFragment;
        if (baseFragment2 instanceof ChatActivity) {
            baseFragment2.showDialog(new PremiumFeatureBottomSheet(this.baseFragment, 11, false));
        }
    }

    /* access modifiers changed from: private */
    public void createTransition(final boolean z) {
        this.fromRect.set(this.reactionsContainerLayout.rect);
        ReactionsContainerLayout reactionsContainerLayout2 = this.reactionsContainerLayout;
        this.fromRadius = reactionsContainerLayout2.radius;
        int[] iArr = new int[2];
        reactionsContainerLayout2.getLocationOnScreen(iArr);
        float dp = (float) ((iArr[1] - AndroidUtilities.dp(44.0f)) - AndroidUtilities.dp(34.0f));
        if (((float) this.containerView.getMeasuredHeight()) + dp > ((float) (this.windowView.getMeasuredHeight() - AndroidUtilities.dp(32.0f)))) {
            dp = (float) ((this.windowView.getMeasuredHeight() - AndroidUtilities.dp(32.0f)) - this.containerView.getMeasuredHeight());
        }
        if (dp < ((float) AndroidUtilities.dp(16.0f))) {
            dp = (float) AndroidUtilities.dp(16.0f);
        }
        this.containerView.setTranslationX((float) (iArr[0] - AndroidUtilities.dp(2.0f)));
        this.containerView.setTranslationY(dp);
        this.fromRect.offset(((float) iArr[0]) - this.containerView.getX(), ((float) iArr[1]) - this.containerView.getY());
        this.reactionsContainerLayout.setCustomEmojiEnterProgress(this.enterTransitionProgress);
        float[] fArr = new float[2];
        fArr[0] = this.enterTransitionProgress;
        fArr[1] = z ? 1.0f : 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        ofFloat.addUpdateListener(new CustomEmojiReactionsWindow$$ExternalSyntheticLambda1(this));
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                CustomEmojiReactionsWindow customEmojiReactionsWindow = CustomEmojiReactionsWindow.this;
                boolean z = z;
                customEmojiReactionsWindow.enterTransitionProgress = z ? 1.0f : 0.0f;
                if (z) {
                    customEmojiReactionsWindow.enterTransitionFinished = true;
                    customEmojiReactionsWindow.selectAnimatedEmojiDialog.resetBackgroundBitmaps();
                    CustomEmojiReactionsWindow.this.reactionsContainerLayout.onCustomEmojiWindowOpened();
                    CustomEmojiReactionsWindow.this.containerView.invalidate();
                }
                CustomEmojiReactionsWindow customEmojiReactionsWindow2 = CustomEmojiReactionsWindow.this;
                customEmojiReactionsWindow2.reactionsContainerLayout.setCustomEmojiEnterProgress(customEmojiReactionsWindow2.enterTransitionProgress);
                if (!z) {
                    CustomEmojiReactionsWindow.this.reactionsContainerLayout.setSkipDraw(false);
                }
                if (!z) {
                    CustomEmojiReactionsWindow.this.removeView();
                }
            }
        });
        ofFloat.setStartDelay(30);
        ofFloat.setDuration(350);
        ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createTransition$1(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.enterTransitionProgress = floatValue;
        this.reactionsContainerLayout.setCustomEmojiEnterProgress(floatValue);
        this.invalidatePath = true;
        this.containerView.invalidate();
    }

    public void removeView() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 7);
        AndroidUtilities.runOnUIThread(new CustomEmojiReactionsWindow$$ExternalSyntheticLambda3(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeView$2() {
        if (this.windowView.getParent() != null) {
            try {
                this.windowManager.removeView(this.windowView);
            } catch (Exception unused) {
            }
            Runnable runnable = this.onDismiss;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    /* access modifiers changed from: private */
    public void dismiss() {
        if (!this.dismissed) {
            Bulletin.hideVisible();
            this.dismissed = true;
            createTransition(false);
        }
    }

    public void onDismissListener(Runnable runnable) {
        this.onDismiss = runnable;
    }

    public void dismiss(boolean z) {
        if (!this.dismissed || !z) {
            this.dismissed = true;
            if (!z) {
                removeView();
                return;
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.addUpdateListener(new CustomEmojiReactionsWindow$$ExternalSyntheticLambda0(this));
            ofFloat.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    CustomEmojiReactionsWindow.this.removeView();
                }
            });
            ofFloat.setDuration(150);
            ofFloat.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dismiss$3(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.dismissProgress = floatValue;
        this.containerView.setAlpha(1.0f - floatValue);
    }

    private class ContainerView extends FrameLayout {
        Paint backgroundPaint = new Paint(1);
        private Paint dimPaint = new Paint(1);
        Drawable shadow;
        Rect shadowPad = new Rect();
        HashMap<ReactionsLayoutInBubble.VisibleReaction, SelectAnimatedEmojiDialog.ImageViewEmoji> transitionReactions = new HashMap<>();

        public ContainerView(Context context) {
            super(context);
            this.shadow = ContextCompat.getDrawable(context, R.drawable.reactions_bubble_shadow).mutate();
            Rect rect = this.shadowPad;
            int dp = AndroidUtilities.dp(7.0f);
            rect.bottom = dp;
            rect.right = dp;
            rect.top = dp;
            rect.left = dp;
            this.shadow.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelShadow", CustomEmojiReactionsWindow.this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
            this.backgroundPaint.setColor(Theme.getColor("actionBarDefaultSubmenuBackground", CustomEmojiReactionsWindow.this.resourcesProvider));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int min = Math.min(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
            int dp = (AndroidUtilities.dp(36.0f) * 8) + AndroidUtilities.dp(12.0f);
            if (dp < min) {
                min = dp;
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(min, NUM), View.MeasureSpec.makeMeasureSpec(min, NUM));
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            float f;
            float f2;
            float f3;
            float f4;
            View view;
            int i;
            float f5;
            float f6;
            SelectAnimatedEmojiDialog.ImageViewEmoji imageViewEmoji;
            ReactionsLayoutInBubble.VisibleReaction visibleReaction;
            Canvas canvas2 = canvas;
            this.dimPaint.setAlpha((int) (CustomEmojiReactionsWindow.this.enterTransitionProgress * 0.2f * 255.0f));
            canvas2.drawPaint(this.dimPaint);
            RectF rectF = AndroidUtilities.rectTmp;
            float f7 = 0.0f;
            rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            CustomEmojiReactionsWindow customEmojiReactionsWindow = CustomEmojiReactionsWindow.this;
            AndroidUtilities.lerp(customEmojiReactionsWindow.fromRect, rectF, customEmojiReactionsWindow.enterTransitionProgress, customEmojiReactionsWindow.drawingRect);
            float lerp = AndroidUtilities.lerp(CustomEmojiReactionsWindow.this.fromRadius, (float) AndroidUtilities.dp(8.0f), CustomEmojiReactionsWindow.this.enterTransitionProgress);
            float f8 = 1.0f;
            this.shadow.setAlpha((int) (Utilities.clamp(CustomEmojiReactionsWindow.this.enterTransitionProgress / 0.05f, 1.0f, 0.0f) * 255.0f));
            Drawable drawable = this.shadow;
            RectF rectF2 = CustomEmojiReactionsWindow.this.drawingRect;
            Rect rect = this.shadowPad;
            drawable.setBounds(((int) rectF2.left) - rect.left, ((int) rectF2.top) - rect.top, ((int) rectF2.right) + rect.right, ((int) rectF2.bottom) + rect.bottom);
            this.shadow.draw(canvas2);
            this.transitionReactions.clear();
            canvas2.drawRoundRect(CustomEmojiReactionsWindow.this.drawingRect, lerp, lerp, this.backgroundPaint);
            CustomEmojiReactionsWindow customEmojiReactionsWindow2 = CustomEmojiReactionsWindow.this;
            RectF rectF3 = customEmojiReactionsWindow2.drawingRect;
            float width = (rectF3.left - customEmojiReactionsWindow2.reactionsContainerLayout.rect.left) + (rectF3.width() - CustomEmojiReactionsWindow.this.reactionsContainerLayout.rect.width());
            if (CustomEmojiReactionsWindow.this.enterTransitionProgress > 0.05f) {
                canvas.save();
                CustomEmojiReactionsWindow customEmojiReactionsWindow3 = CustomEmojiReactionsWindow.this;
                RectF rectF4 = customEmojiReactionsWindow3.drawingRect;
                canvas2.translate(width, (rectF4.top - customEmojiReactionsWindow3.reactionsContainerLayout.rect.top) + (rectF4.height() - CustomEmojiReactionsWindow.this.reactionsContainerLayout.rect.height()));
                CustomEmojiReactionsWindow.this.reactionsContainerLayout.drawBubbles(canvas2);
                canvas.restore();
            }
            CustomEmojiReactionsWindow customEmojiReactionsWindow4 = CustomEmojiReactionsWindow.this;
            if (customEmojiReactionsWindow4.reactionsContainerLayout == null || customEmojiReactionsWindow4.enterTransitionProgress == 1.0f) {
                f4 = 0.0f;
                f3 = 0.0f;
                f2 = 0.0f;
                f = 1.0f;
            } else {
                for (int i2 = 0; i2 < CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.emojiGridView.getChildCount(); i2++) {
                    if ((CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.emojiGridView.getChildAt(i2) instanceof SelectAnimatedEmojiDialog.ImageViewEmoji) && (visibleReaction = imageViewEmoji.reaction) != null) {
                        this.transitionReactions.put(visibleReaction, (imageViewEmoji = (SelectAnimatedEmojiDialog.ImageViewEmoji) CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.emojiGridView.getChildAt(i2)));
                        imageViewEmoji.notDraw = false;
                        imageViewEmoji.invalidate();
                    }
                }
                canvas.save();
                CustomEmojiReactionsWindow customEmojiReactionsWindow5 = CustomEmojiReactionsWindow.this;
                RectF rectF5 = customEmojiReactionsWindow5.drawingRect;
                canvas2.translate(rectF5.left, rectF5.top + (customEmojiReactionsWindow5.reactionsContainerLayout.expandSize() * (1.0f - CustomEmojiReactionsWindow.this.enterTransitionProgress)));
                int i3 = -1;
                int i4 = -1;
                float f9 = 0.0f;
                float var_ = 0.0f;
                float var_ = 1.0f;
                float var_ = 0.0f;
                float var_ = 0.0f;
                while (i4 < CustomEmojiReactionsWindow.this.reactionsContainerLayout.recyclerListView.getChildCount()) {
                    if (i4 == i3) {
                        view = CustomEmojiReactionsWindow.this.reactionsContainerLayout.nextRecentReaction;
                    } else {
                        view = CustomEmojiReactionsWindow.this.reactionsContainerLayout.recyclerListView.getChildAt(i4);
                    }
                    View view2 = view;
                    if (view2.getLeft() < 0 || view2.getVisibility() == 8) {
                        i = i4;
                    } else {
                        canvas.save();
                        if (view2 instanceof ReactionsContainerLayout.ReactionHolderView) {
                            ReactionsContainerLayout.ReactionHolderView reactionHolderView = (ReactionsContainerLayout.ReactionHolderView) view2;
                            SelectAnimatedEmojiDialog.ImageViewEmoji imageViewEmoji2 = this.transitionReactions.get(reactionHolderView.currentReaction);
                            if (imageViewEmoji2 != null) {
                                float x = view2.getX() + reactionHolderView.loopImageView.getX();
                                float y = view2.getY() + reactionHolderView.loopImageView.getY();
                                if (i4 == i3) {
                                    x -= CustomEmojiReactionsWindow.this.reactionsContainerLayout.recyclerListView.getX();
                                    y -= CustomEmojiReactionsWindow.this.reactionsContainerLayout.recyclerListView.getY();
                                }
                                float x2 = imageViewEmoji2.getX() + CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.getX() + CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.emojiGridView.getX();
                                float y2 = imageViewEmoji2.getY() + CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.getY() + CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.emojiGridView.getY();
                                float measuredWidth = (float) imageViewEmoji2.getMeasuredWidth();
                                if (imageViewEmoji2.selected) {
                                    float var_ = 0.86f * measuredWidth;
                                    float var_ = (measuredWidth - var_) / 2.0f;
                                    x2 += var_;
                                    y2 += var_;
                                    measuredWidth = var_;
                                }
                                float lerp2 = AndroidUtilities.lerp(x, x2, CustomEmojiReactionsWindow.this.enterTransitionProgress);
                                float lerp3 = AndroidUtilities.lerp(y, y2, CustomEmojiReactionsWindow.this.enterTransitionProgress);
                                float measuredWidth2 = measuredWidth / ((float) reactionHolderView.loopImageView.getMeasuredWidth());
                                f6 = AndroidUtilities.lerp(f8, measuredWidth2, CustomEmojiReactionsWindow.this.enterTransitionProgress);
                                f5 = reactionHolderView.selected ? (float) AndroidUtilities.dp(6.0f) : 0.0f;
                                canvas2.translate(lerp2, lerp3);
                                canvas2.scale(f6, f6);
                                if (f9 == 0.0f && var_ == 0.0f) {
                                    CustomEmojiReactionsWindow customEmojiReactionsWindow6 = CustomEmojiReactionsWindow.this;
                                    f9 = AndroidUtilities.lerp((customEmojiReactionsWindow6.fromRect.left + x) - x2, 0.0f, customEmojiReactionsWindow6.enterTransitionProgress);
                                    CustomEmojiReactionsWindow customEmojiReactionsWindow7 = CustomEmojiReactionsWindow.this;
                                    var_ = AndroidUtilities.lerp((customEmojiReactionsWindow7.fromRect.top + y) - y2, 0.0f, customEmojiReactionsWindow7.enterTransitionProgress);
                                    var_ = AndroidUtilities.lerp(1.0f / measuredWidth2, 1.0f, CustomEmojiReactionsWindow.this.enterTransitionProgress);
                                    var_ = x2;
                                    var_ = y2;
                                }
                            } else {
                                canvas2.translate(view2.getX() + reactionHolderView.loopImageView.getX(), view2.getY() + reactionHolderView.loopImageView.getY());
                                f6 = 1.0f;
                                f5 = 0.0f;
                            }
                            if (reactionHolderView.loopImageView.getVisibility() == 0 && imageViewEmoji2 != null && CustomEmojiReactionsWindow.this.imageIsEquals(reactionHolderView.loopImageView, imageViewEmoji2)) {
                                if (imageViewEmoji2.selected) {
                                    float measuredWidth3 = ((float) reactionHolderView.loopImageView.getMeasuredWidth()) / 2.0f;
                                    float measuredHeight = ((float) reactionHolderView.loopImageView.getMeasuredHeight()) / 2.0f;
                                    float measuredWidth4 = (float) (reactionHolderView.getMeasuredWidth() - AndroidUtilities.dp(2.0f));
                                    float lerp4 = AndroidUtilities.lerp(measuredWidth4, ((float) (imageViewEmoji2.getMeasuredWidth() - AndroidUtilities.dp(2.0f))) / f6, CustomEmojiReactionsWindow.this.enterTransitionProgress);
                                    RectF rectF6 = AndroidUtilities.rectTmp;
                                    float var_ = lerp4 / 2.0f;
                                    rectF6.set(measuredWidth3 - var_, measuredHeight - var_, measuredWidth3 + var_, measuredHeight + var_);
                                    float lerp5 = AndroidUtilities.lerp(measuredWidth4 / 2.0f, (float) AndroidUtilities.dp(4.0f), CustomEmojiReactionsWindow.this.enterTransitionProgress);
                                    canvas2.drawRoundRect(rectF6, lerp5, lerp5, CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.selectorPaint);
                                }
                                if (f5 != 0.0f) {
                                    ImageReceiver imageReceiver = reactionHolderView.loopImageView.getImageReceiver();
                                    AnimatedEmojiDrawable animatedEmojiDrawable = reactionHolderView.loopImageView.animatedEmojiDrawable;
                                    if (!(animatedEmojiDrawable == null || animatedEmojiDrawable.getImageReceiver() == null)) {
                                        imageReceiver = reactionHolderView.loopImageView.animatedEmojiDrawable.getImageReceiver();
                                    }
                                    int i5 = imageReceiver.getRoundRadius()[0];
                                    imageReceiver.setRoundRadius((int) AndroidUtilities.lerp(f5, 0.0f, CustomEmojiReactionsWindow.this.enterTransitionProgress));
                                    reactionHolderView.loopImageView.draw(canvas2);
                                    reactionHolderView.loopImageView.draw(canvas2);
                                    imageReceiver.setRoundRadius(i5);
                                } else {
                                    reactionHolderView.loopImageView.draw(canvas2);
                                }
                                if (!imageViewEmoji2.notDraw) {
                                    imageViewEmoji2.notDraw = true;
                                    imageViewEmoji2.invalidate();
                                }
                            } else if (reactionHolderView.hasEnterAnimation) {
                                float alpha = reactionHolderView.enterImageView.getImageReceiver().getAlpha();
                                reactionHolderView.enterImageView.getImageReceiver().setAlpha((1.0f - CustomEmojiReactionsWindow.this.enterTransitionProgress) * alpha);
                                reactionHolderView.enterImageView.draw(canvas2);
                                reactionHolderView.enterImageView.getImageReceiver().setAlpha(alpha);
                            } else {
                                ImageReceiver imageReceiver2 = reactionHolderView.loopImageView.getImageReceiver();
                                AnimatedEmojiDrawable animatedEmojiDrawable2 = reactionHolderView.loopImageView.animatedEmojiDrawable;
                                if (!(animatedEmojiDrawable2 == null || animatedEmojiDrawable2.getImageReceiver() == null)) {
                                    imageReceiver2 = reactionHolderView.loopImageView.animatedEmojiDrawable.getImageReceiver();
                                }
                                float alpha2 = imageReceiver2.getAlpha();
                                imageReceiver2.setAlpha((1.0f - CustomEmojiReactionsWindow.this.enterTransitionProgress) * alpha2);
                                reactionHolderView.loopImageView.draw(canvas2);
                                imageReceiver2.setAlpha(alpha2);
                            }
                            i = i4;
                        } else {
                            float x3 = (view2.getX() + CustomEmojiReactionsWindow.this.drawingRect.width()) - CustomEmojiReactionsWindow.this.reactionsContainerLayout.rect.width();
                            float y3 = view2.getY();
                            CustomEmojiReactionsWindow customEmojiReactionsWindow8 = CustomEmojiReactionsWindow.this;
                            canvas2.translate(x3, (y3 + customEmojiReactionsWindow8.fromRect.top) - customEmojiReactionsWindow8.drawingRect.top);
                            View view3 = view2;
                            i = i4;
                            canvas.saveLayerAlpha(0.0f, 0.0f, (float) view2.getMeasuredWidth(), (float) view2.getMeasuredHeight(), (int) ((1.0f - CustomEmojiReactionsWindow.this.enterTransitionProgress) * 255.0f), 31);
                            float var_ = CustomEmojiReactionsWindow.this.enterTransitionProgress;
                            canvas2.scale(1.0f - var_, 1.0f - var_, (float) (view3.getMeasuredWidth() >> 1), (float) (view3.getMeasuredHeight() >> 1));
                            view3.draw(canvas2);
                            canvas.restore();
                        }
                        canvas.restore();
                    }
                    i4 = i + 1;
                    f8 = 1.0f;
                    i3 = -1;
                }
                canvas.restore();
                f7 = f9;
                f4 = var_;
                f = var_;
                f3 = var_;
                f2 = var_;
            }
            if (CustomEmojiReactionsWindow.this.invalidatePath) {
                boolean unused = CustomEmojiReactionsWindow.this.invalidatePath = false;
                CustomEmojiReactionsWindow.this.pathToClip.rewind();
                CustomEmojiReactionsWindow customEmojiReactionsWindow9 = CustomEmojiReactionsWindow.this;
                customEmojiReactionsWindow9.pathToClip.addRoundRect(customEmojiReactionsWindow9.drawingRect, lerp, lerp, Path.Direction.CW);
            }
            canvas.save();
            canvas2.clipPath(CustomEmojiReactionsWindow.this.pathToClip);
            canvas2.translate(f7, f4);
            canvas2.scale(f, f, f3, f2);
            CustomEmojiReactionsWindow customEmojiReactionsWindow10 = CustomEmojiReactionsWindow.this;
            customEmojiReactionsWindow10.selectAnimatedEmojiDialog.setAlpha(customEmojiReactionsWindow10.enterTransitionProgress);
            super.dispatchDraw(canvas);
            canvas.restore();
            if (CustomEmojiReactionsWindow.this.frameDrawCount < 5) {
                if (CustomEmojiReactionsWindow.this.frameDrawCount == 3) {
                    CustomEmojiReactionsWindow.this.reactionsContainerLayout.setSkipDraw(true);
                }
                CustomEmojiReactionsWindow.access$508(CustomEmojiReactionsWindow.this);
            }
            CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.drawBigReaction(canvas2, this);
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    public boolean imageIsEquals(BackupImageView backupImageView, SelectAnimatedEmojiDialog.ImageViewEmoji imageViewEmoji) {
        AnimatedEmojiSpan animatedEmojiSpan = imageViewEmoji.span;
        if (animatedEmojiSpan == null) {
            if (imageViewEmoji.imageReceiver.getLottieAnimation() == backupImageView.getImageReceiver().getLottieAnimation()) {
                return true;
            }
            return false;
        } else if (backupImageView.animatedEmojiDrawable == null || animatedEmojiSpan.getDocumentId() != backupImageView.animatedEmojiDrawable.getDocumentId()) {
            return false;
        } else {
            return true;
        }
    }

    public void setRecentReactions(List<ReactionsLayoutInBubble.VisibleReaction> list) {
        this.selectAnimatedEmojiDialog.setRecentReactions(list);
    }

    public SelectAnimatedEmojiDialog getSelectAnimatedEmojiDialog() {
        return this.selectAnimatedEmojiDialog;
    }
}
