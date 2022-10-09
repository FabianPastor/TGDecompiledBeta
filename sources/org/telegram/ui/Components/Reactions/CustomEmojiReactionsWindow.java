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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.ReactionsContainerLayout;
import org.telegram.ui.SelectAnimatedEmojiDialog;
/* loaded from: classes3.dex */
public class CustomEmojiReactionsWindow {
    BaseFragment baseFragment;
    ContainerView containerView;
    private float dismissProgress;
    private boolean dismissed;
    boolean enterTransitionFinished;
    float enterTransitionProgress;
    float fromRadius;
    private boolean invalidatePath;
    float keyboardHeight;
    private Runnable onDismiss;
    List<ReactionsLayoutInBubble.VisibleReaction> reactions;
    ReactionsContainerLayout reactionsContainerLayout;
    Theme.ResourcesProvider resourcesProvider;
    SelectAnimatedEmojiDialog selectAnimatedEmojiDialog;
    private boolean wasFocused;
    WindowManager windowManager;
    FrameLayout windowView;
    float yTranslation;
    RectF fromRect = new RectF();
    public RectF drawingRect = new RectF();
    Path pathToClip = new Path();
    private int frameDrawCount = 0;

    static /* synthetic */ int access$808(CustomEmojiReactionsWindow customEmojiReactionsWindow) {
        int i = customEmojiReactionsWindow.frameDrawCount;
        customEmojiReactionsWindow.frameDrawCount = i + 1;
        return i;
    }

    public CustomEmojiReactionsWindow(BaseFragment baseFragment, List<ReactionsLayoutInBubble.VisibleReaction> list, HashSet<ReactionsLayoutInBubble.VisibleReaction> hashSet, final ReactionsContainerLayout reactionsContainerLayout, Theme.ResourcesProvider resourcesProvider) {
        this.reactions = list;
        this.baseFragment = baseFragment;
        this.resourcesProvider = resourcesProvider;
        Context context = baseFragment.getContext();
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.1
            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchSetPressed(boolean z) {
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                if (keyEvent.getAction() == 1 && keyEvent.getKeyCode() == 4) {
                    CustomEmojiReactionsWindow.this.dismiss();
                    return true;
                }
                return false;
            }

            @Override // android.view.View
            protected boolean fitSystemWindows(Rect rect) {
                CustomEmojiReactionsWindow customEmojiReactionsWindow = CustomEmojiReactionsWindow.this;
                if (customEmojiReactionsWindow.keyboardHeight != rect.bottom && customEmojiReactionsWindow.wasFocused) {
                    CustomEmojiReactionsWindow customEmojiReactionsWindow2 = CustomEmojiReactionsWindow.this;
                    customEmojiReactionsWindow2.keyboardHeight = rect.bottom;
                    customEmojiReactionsWindow2.updateWindowPosition();
                }
                return super.fitSystemWindows(rect);
            }
        };
        this.windowView = frameLayout;
        frameLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CustomEmojiReactionsWindow.this.lambda$new$0(view);
            }
        });
        this.containerView = new ContainerView(context);
        AnonymousClass2 anonymousClass2 = new AnonymousClass2(baseFragment, context, false, null, 1, resourcesProvider, baseFragment, reactionsContainerLayout);
        this.selectAnimatedEmojiDialog = anonymousClass2;
        anonymousClass2.setOnLongPressedListener(new SelectAnimatedEmojiDialog.onLongPressedListener(this) { // from class: org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.3
            @Override // org.telegram.ui.SelectAnimatedEmojiDialog.onLongPressedListener
            public void onLongPressed(SelectAnimatedEmojiDialog.ImageViewEmoji imageViewEmoji) {
                if (imageViewEmoji.isDefaultReaction) {
                    reactionsContainerLayout.onReactionClicked(imageViewEmoji, imageViewEmoji.reaction, true);
                } else {
                    reactionsContainerLayout.onReactionClicked(imageViewEmoji, ReactionsLayoutInBubble.VisibleReaction.fromCustomEmoji(Long.valueOf(imageViewEmoji.span.documentId)), true);
                }
            }
        });
        this.selectAnimatedEmojiDialog.setOnRecentClearedListener(new SelectAnimatedEmojiDialog.onRecentClearedListener(this) { // from class: org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.4
            @Override // org.telegram.ui.SelectAnimatedEmojiDialog.onRecentClearedListener
            public void onRecentCleared() {
                reactionsContainerLayout.clearRecentReactions();
            }
        });
        this.selectAnimatedEmojiDialog.setRecentReactions(list);
        this.selectAnimatedEmojiDialog.setSelectedReactions(hashSet);
        this.selectAnimatedEmojiDialog.setDrawBackground(false);
        this.selectAnimatedEmojiDialog.onShow(null);
        this.containerView.addView(this.selectAnimatedEmojiDialog, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
        this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1.0f, 48, 16.0f, 16.0f, 16.0f, 16.0f));
        this.windowView.setClipChildren(false);
        WindowManager.LayoutParams createLayoutParams = createLayoutParams(false);
        WindowManager windowManager = baseFragment.getParentActivity().getWindowManager();
        this.windowManager = windowManager;
        windowManager.addView(this.windowView, createLayoutParams);
        this.reactionsContainerLayout = reactionsContainerLayout;
        reactionsContainerLayout.prepareAnimation(true);
        this.containerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.5
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                CustomEmojiReactionsWindow.this.containerView.removeOnLayoutChangeListener(this);
                reactionsContainerLayout.prepareAnimation(false);
                CustomEmojiReactionsWindow.this.createTransition(true);
            }
        });
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 7);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow$2  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass2 extends SelectAnimatedEmojiDialog {
        final /* synthetic */ BaseFragment val$baseFragment;
        final /* synthetic */ ReactionsContainerLayout val$reactionsContainerLayout;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass2(BaseFragment baseFragment, Context context, boolean z, Integer num, int i, Theme.ResourcesProvider resourcesProvider, BaseFragment baseFragment2, ReactionsContainerLayout reactionsContainerLayout) {
            super(baseFragment, context, z, num, i, resourcesProvider);
            this.val$baseFragment = baseFragment2;
            this.val$reactionsContainerLayout = reactionsContainerLayout;
        }

        @Override // org.telegram.ui.SelectAnimatedEmojiDialog
        protected void onInputFocus() {
            if (!CustomEmojiReactionsWindow.this.wasFocused) {
                CustomEmojiReactionsWindow.this.wasFocused = true;
                CustomEmojiReactionsWindow customEmojiReactionsWindow = CustomEmojiReactionsWindow.this;
                customEmojiReactionsWindow.windowManager.updateViewLayout(customEmojiReactionsWindow.windowView, customEmojiReactionsWindow.createLayoutParams(true));
                BaseFragment baseFragment = this.val$baseFragment;
                if (!(baseFragment instanceof ChatActivity)) {
                    return;
                }
                ((ChatActivity) baseFragment).needEnterText();
            }
        }

        @Override // org.telegram.ui.SelectAnimatedEmojiDialog
        protected void onReactionClick(SelectAnimatedEmojiDialog.ImageViewEmoji imageViewEmoji, ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
            this.val$reactionsContainerLayout.onReactionClicked(imageViewEmoji, visibleReaction, false);
            AndroidUtilities.hideKeyboard(CustomEmojiReactionsWindow.this.windowView);
        }

        @Override // org.telegram.ui.SelectAnimatedEmojiDialog
        protected void onEmojiSelected(View view, Long l, TLRPC$Document tLRPC$Document, Integer num) {
            if (!UserConfig.getInstance(this.val$baseFragment.getCurrentAccount()).isPremium()) {
                CustomEmojiReactionsWindow.this.windowView.performHapticFeedback(3);
                BulletinFactory.of(CustomEmojiReactionsWindow.this.windowView, null).createEmojiBulletin(tLRPC$Document, AndroidUtilities.replaceTags(LocaleController.getString("UnlockPremiumEmojiReaction", R.string.UnlockPremiumEmojiReaction)), LocaleController.getString("PremiumMore", R.string.PremiumMore), new Runnable() { // from class: org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        CustomEmojiReactionsWindow.AnonymousClass2.this.lambda$onEmojiSelected$0();
                    }
                }).show();
                return;
            }
            this.val$reactionsContainerLayout.onReactionClicked(view, ReactionsLayoutInBubble.VisibleReaction.fromCustomEmoji(l), false);
            AndroidUtilities.hideKeyboard(CustomEmojiReactionsWindow.this.windowView);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onEmojiSelected$0() {
            CustomEmojiReactionsWindow.this.showUnlockPremiumAlert();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateWindowPosition() {
        if (this.dismissed) {
            return;
        }
        float f = this.yTranslation;
        if (this.containerView.getMeasuredHeight() + f > (this.windowView.getMeasuredHeight() - this.keyboardHeight) - AndroidUtilities.dp(32.0f)) {
            f = ((this.windowView.getMeasuredHeight() - this.keyboardHeight) - this.containerView.getMeasuredHeight()) - AndroidUtilities.dp(32.0f);
        }
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.containerView.animate().translationY(f).setDuration(250L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public WindowManager.LayoutParams createLayoutParams(boolean z) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = -1;
        layoutParams.width = -1;
        layoutParams.type = 1000;
        layoutParams.softInputMode = 16;
        if (z) {
            layoutParams.flags = 65792;
        } else {
            layoutParams.flags = 65800;
        }
        layoutParams.format = -3;
        return layoutParams;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showUnlockPremiumAlert() {
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            baseFragment.showDialog(new PremiumFeatureBottomSheet(this.baseFragment, 11, false));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createTransition(final boolean z) {
        this.fromRect.set(this.reactionsContainerLayout.rect);
        ReactionsContainerLayout reactionsContainerLayout = this.reactionsContainerLayout;
        this.fromRadius = reactionsContainerLayout.radius;
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        reactionsContainerLayout.getLocationOnScreen(iArr);
        this.windowView.getLocationOnScreen(iArr2);
        float dp = ((iArr[1] - iArr2[1]) - AndroidUtilities.dp(44.0f)) - AndroidUtilities.dp(34.0f);
        if (this.containerView.getMeasuredHeight() + dp > this.windowView.getMeasuredHeight() - AndroidUtilities.dp(32.0f)) {
            dp = (this.windowView.getMeasuredHeight() - AndroidUtilities.dp(32.0f)) - this.containerView.getMeasuredHeight();
        }
        if (dp < AndroidUtilities.dp(16.0f)) {
            dp = AndroidUtilities.dp(16.0f);
        }
        this.containerView.setTranslationX((iArr[0] - iArr2[0]) - AndroidUtilities.dp(2.0f));
        if (!z) {
            this.yTranslation = this.containerView.getTranslationY();
        } else {
            this.yTranslation = dp;
        }
        this.containerView.setTranslationY(this.yTranslation);
        this.fromRect.offset((iArr[0] - iArr2[0]) - this.containerView.getX(), (iArr[1] - iArr2[1]) - this.containerView.getY());
        this.reactionsContainerLayout.setCustomEmojiEnterProgress(this.enterTransitionProgress);
        float[] fArr = new float[2];
        fArr[0] = this.enterTransitionProgress;
        fArr[1] = z ? 1.0f : 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CustomEmojiReactionsWindow.this.lambda$createTransition$1(valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                CustomEmojiReactionsWindow customEmojiReactionsWindow = CustomEmojiReactionsWindow.this;
                boolean z2 = z;
                customEmojiReactionsWindow.enterTransitionProgress = z2 ? 1.0f : 0.0f;
                if (z2) {
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
        ofFloat.setStartDelay(30L);
        ofFloat.setDuration(350L);
        ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
        ofFloat.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createTransition$1(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.enterTransitionProgress = floatValue;
        this.reactionsContainerLayout.setCustomEmojiEnterProgress(floatValue);
        this.invalidatePath = true;
        this.containerView.invalidate();
    }

    public void removeView() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 7);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                CustomEmojiReactionsWindow.this.lambda$removeView$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeView$2() {
        if (this.windowView.getParent() == null) {
            return;
        }
        try {
            this.windowManager.removeView(this.windowView);
        } catch (Exception unused) {
        }
        Runnable runnable = this.onDismiss;
        if (runnable == null) {
            return;
        }
        runnable.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismiss() {
        if (this.dismissed) {
            return;
        }
        Bulletin.hideVisible();
        this.dismissed = true;
        AndroidUtilities.hideKeyboard(this.windowView);
        createTransition(false);
        if (!this.wasFocused) {
            return;
        }
        BaseFragment baseFragment = this.baseFragment;
        if (!(baseFragment instanceof ChatActivity)) {
            return;
        }
        ((ChatActivity) baseFragment).onEditTextDialogClose(true, true);
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
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    CustomEmojiReactionsWindow.this.lambda$dismiss$3(valueAnimator);
                }
            });
            ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.7
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    CustomEmojiReactionsWindow.this.removeView();
                }
            });
            ofFloat.setDuration(150L);
            ofFloat.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dismiss$3(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.dismissProgress = floatValue;
        this.containerView.setAlpha(1.0f - floatValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ContainerView extends FrameLayout {
        Paint backgroundPaint;
        private Paint dimPaint;
        int[] radiusTmp;
        Drawable shadow;
        Rect shadowPad;
        HashMap<ReactionsLayoutInBubble.VisibleReaction, SelectAnimatedEmojiDialog.ImageViewEmoji> transitionReactions;

        public ContainerView(Context context) {
            super(context);
            this.shadowPad = new Rect();
            this.backgroundPaint = new Paint(1);
            this.dimPaint = new Paint(1);
            this.radiusTmp = new int[4];
            this.transitionReactions = new HashMap<>();
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

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            int min = Math.min(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
            int dp = (AndroidUtilities.dp(36.0f) * 8) + AndroidUtilities.dp(12.0f);
            if (dp < min) {
                min = dp;
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(min, NUM), View.MeasureSpec.makeMeasureSpec(min, NUM));
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            float f;
            float f2;
            float f3;
            float f4;
            float f5;
            View childAt;
            int i;
            float f6;
            float f7;
            float f8;
            float f9;
            float var_;
            int i2;
            float var_;
            float var_;
            float var_;
            SelectAnimatedEmojiDialog.ImageViewEmoji imageViewEmoji;
            ReactionsLayoutInBubble.VisibleReaction visibleReaction;
            this.dimPaint.setAlpha((int) (CustomEmojiReactionsWindow.this.enterTransitionProgress * 0.2f * 255.0f));
            canvas.drawPaint(this.dimPaint);
            RectF rectF = AndroidUtilities.rectTmp;
            float var_ = 0.0f;
            rectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
            CustomEmojiReactionsWindow customEmojiReactionsWindow = CustomEmojiReactionsWindow.this;
            AndroidUtilities.lerp(customEmojiReactionsWindow.fromRect, rectF, customEmojiReactionsWindow.enterTransitionProgress, customEmojiReactionsWindow.drawingRect);
            float lerp = AndroidUtilities.lerp(CustomEmojiReactionsWindow.this.fromRadius, AndroidUtilities.dp(8.0f), CustomEmojiReactionsWindow.this.enterTransitionProgress);
            float var_ = 1.0f;
            this.shadow.setAlpha((int) (Utilities.clamp(CustomEmojiReactionsWindow.this.enterTransitionProgress / 0.05f, 1.0f, 0.0f) * 255.0f));
            Drawable drawable = this.shadow;
            RectF rectF2 = CustomEmojiReactionsWindow.this.drawingRect;
            Rect rect = this.shadowPad;
            drawable.setBounds(((int) rectF2.left) - rect.left, ((int) rectF2.top) - rect.top, ((int) rectF2.right) + rect.right, ((int) rectF2.bottom) + rect.bottom);
            this.shadow.draw(canvas);
            this.transitionReactions.clear();
            canvas.drawRoundRect(CustomEmojiReactionsWindow.this.drawingRect, lerp, lerp, this.backgroundPaint);
            CustomEmojiReactionsWindow customEmojiReactionsWindow2 = CustomEmojiReactionsWindow.this;
            RectF rectF3 = customEmojiReactionsWindow2.drawingRect;
            float width = (rectF3.left - customEmojiReactionsWindow2.reactionsContainerLayout.rect.left) + (rectF3.width() - CustomEmojiReactionsWindow.this.reactionsContainerLayout.rect.width());
            if (CustomEmojiReactionsWindow.this.enterTransitionProgress > 0.05f) {
                canvas.save();
                CustomEmojiReactionsWindow customEmojiReactionsWindow3 = CustomEmojiReactionsWindow.this;
                RectF rectF4 = customEmojiReactionsWindow3.drawingRect;
                canvas.translate(width, (rectF4.top - customEmojiReactionsWindow3.reactionsContainerLayout.rect.top) + (rectF4.height() - CustomEmojiReactionsWindow.this.reactionsContainerLayout.rect.height()));
                CustomEmojiReactionsWindow.this.reactionsContainerLayout.drawBubbles(canvas);
                canvas.restore();
            }
            CustomEmojiReactionsWindow customEmojiReactionsWindow4 = CustomEmojiReactionsWindow.this;
            if (customEmojiReactionsWindow4.reactionsContainerLayout == null || customEmojiReactionsWindow4.enterTransitionProgress == 1.0f) {
                f = 0.0f;
                f2 = 0.0f;
                f3 = 0.0f;
                f4 = 0.0f;
                f5 = 1.0f;
            } else {
                for (int i3 = 0; i3 < CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.emojiGridView.getChildCount(); i3++) {
                    if ((CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.emojiGridView.getChildAt(i3) instanceof SelectAnimatedEmojiDialog.ImageViewEmoji) && (visibleReaction = (imageViewEmoji = (SelectAnimatedEmojiDialog.ImageViewEmoji) CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.emojiGridView.getChildAt(i3)).reaction) != null) {
                        this.transitionReactions.put(visibleReaction, imageViewEmoji);
                        imageViewEmoji.notDraw = false;
                        imageViewEmoji.invalidate();
                    }
                }
                canvas.save();
                CustomEmojiReactionsWindow customEmojiReactionsWindow5 = CustomEmojiReactionsWindow.this;
                RectF rectF5 = customEmojiReactionsWindow5.drawingRect;
                canvas.translate(rectF5.left, rectF5.top + (customEmojiReactionsWindow5.reactionsContainerLayout.expandSize() * (1.0f - CustomEmojiReactionsWindow.this.enterTransitionProgress)));
                int i4 = -1;
                int i5 = -1;
                float var_ = 0.0f;
                float var_ = 0.0f;
                float var_ = 1.0f;
                float var_ = 0.0f;
                float var_ = 0.0f;
                while (i5 < CustomEmojiReactionsWindow.this.reactionsContainerLayout.recyclerListView.getChildCount()) {
                    if (i5 == i4) {
                        childAt = CustomEmojiReactionsWindow.this.reactionsContainerLayout.nextRecentReaction;
                    } else {
                        childAt = CustomEmojiReactionsWindow.this.reactionsContainerLayout.recyclerListView.getChildAt(i5);
                    }
                    View view = childAt;
                    if (view.getLeft() < 0 || view.getVisibility() == 8) {
                        i = i5;
                    } else {
                        canvas.save();
                        if (view instanceof ReactionsContainerLayout.ReactionHolderView) {
                            ReactionsContainerLayout.ReactionHolderView reactionHolderView = (ReactionsContainerLayout.ReactionHolderView) view;
                            SelectAnimatedEmojiDialog.ImageViewEmoji imageViewEmoji2 = this.transitionReactions.get(reactionHolderView.currentReaction);
                            if (imageViewEmoji2 != null) {
                                float x = view.getX() + reactionHolderView.loopImageView.getX();
                                float y = view.getY() + reactionHolderView.loopImageView.getY();
                                if (i5 == i4) {
                                    x -= CustomEmojiReactionsWindow.this.reactionsContainerLayout.recyclerListView.getX();
                                    y -= CustomEmojiReactionsWindow.this.reactionsContainerLayout.recyclerListView.getY();
                                }
                                float x2 = imageViewEmoji2.getX() + CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.getX() + CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.emojiGridView.getX();
                                float y2 = imageViewEmoji2.getY() + CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.getY() + CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.gridViewContainer.getY() + CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.emojiGridView.getY();
                                float measuredWidth = imageViewEmoji2.getMeasuredWidth();
                                if (imageViewEmoji2.selected) {
                                    float var_ = 0.86f * measuredWidth;
                                    float var_ = (measuredWidth - var_) / 2.0f;
                                    x2 += var_;
                                    y2 += var_;
                                    measuredWidth = var_;
                                }
                                float lerp2 = AndroidUtilities.lerp(x, x2, CustomEmojiReactionsWindow.this.enterTransitionProgress);
                                float lerp3 = AndroidUtilities.lerp(y, y2, CustomEmojiReactionsWindow.this.enterTransitionProgress);
                                float measuredWidth2 = measuredWidth / reactionHolderView.loopImageView.getMeasuredWidth();
                                f6 = AndroidUtilities.lerp(var_, measuredWidth2, CustomEmojiReactionsWindow.this.enterTransitionProgress);
                                if (reactionHolderView.position == 0) {
                                    var_ = AndroidUtilities.dp(6.0f);
                                    var_ = var_;
                                } else if (reactionHolderView.selected) {
                                    var_ = AndroidUtilities.dp(6.0f);
                                    var_ = var_;
                                    var_ = var_;
                                    var_ = var_;
                                    canvas.translate(lerp2, lerp3);
                                    canvas.scale(f6, f6);
                                    if (var_ == var_ && var_ == var_) {
                                        CustomEmojiReactionsWindow customEmojiReactionsWindow6 = CustomEmojiReactionsWindow.this;
                                        var_ = AndroidUtilities.lerp((customEmojiReactionsWindow6.fromRect.left + x) - x2, var_, customEmojiReactionsWindow6.enterTransitionProgress);
                                        CustomEmojiReactionsWindow customEmojiReactionsWindow7 = CustomEmojiReactionsWindow.this;
                                        var_ = AndroidUtilities.lerp((customEmojiReactionsWindow7.fromRect.top + y) - y2, var_, customEmojiReactionsWindow7.enterTransitionProgress);
                                        var_ = AndroidUtilities.lerp(1.0f / measuredWidth2, 1.0f, CustomEmojiReactionsWindow.this.enterTransitionProgress);
                                        var_ = x2;
                                        var_ = y2;
                                    }
                                    f7 = var_;
                                    f8 = var_;
                                    f9 = var_;
                                } else {
                                    var_ = 0.0f;
                                    var_ = 0.0f;
                                }
                                var_ = 0.0f;
                                var_ = 0.0f;
                                canvas.translate(lerp2, lerp3);
                                canvas.scale(f6, f6);
                                if (var_ == var_) {
                                    CustomEmojiReactionsWindow customEmojiReactionsWindow62 = CustomEmojiReactionsWindow.this;
                                    var_ = AndroidUtilities.lerp((customEmojiReactionsWindow62.fromRect.left + x) - x2, var_, customEmojiReactionsWindow62.enterTransitionProgress);
                                    CustomEmojiReactionsWindow customEmojiReactionsWindow72 = CustomEmojiReactionsWindow.this;
                                    var_ = AndroidUtilities.lerp((customEmojiReactionsWindow72.fromRect.top + y) - y2, var_, customEmojiReactionsWindow72.enterTransitionProgress);
                                    var_ = AndroidUtilities.lerp(1.0f / measuredWidth2, 1.0f, CustomEmojiReactionsWindow.this.enterTransitionProgress);
                                    var_ = x2;
                                    var_ = y2;
                                }
                                f7 = var_;
                                f8 = var_;
                                f9 = var_;
                            } else {
                                canvas.translate(view.getX() + reactionHolderView.loopImageView.getX(), view.getY() + reactionHolderView.loopImageView.getY());
                                f6 = 1.0f;
                                f7 = 0.0f;
                                f8 = 0.0f;
                                f9 = 0.0f;
                                var_ = 0.0f;
                            }
                            if (reactionHolderView.loopImageView.getVisibility() == 0 && imageViewEmoji2 != null && CustomEmojiReactionsWindow.this.imageIsEquals(reactionHolderView.loopImageView, imageViewEmoji2)) {
                                if (imageViewEmoji2.selected) {
                                    float measuredWidth3 = reactionHolderView.loopImageView.getMeasuredWidth() / 2.0f;
                                    float measuredHeight = reactionHolderView.loopImageView.getMeasuredHeight() / 2.0f;
                                    float measuredWidth4 = reactionHolderView.getMeasuredWidth() - AndroidUtilities.dp(2.0f);
                                    float lerp4 = AndroidUtilities.lerp(measuredWidth4, (imageViewEmoji2.getMeasuredWidth() - AndroidUtilities.dp(2.0f)) / f6, CustomEmojiReactionsWindow.this.enterTransitionProgress);
                                    RectF rectF6 = AndroidUtilities.rectTmp;
                                    float var_ = lerp4 / 2.0f;
                                    i2 = i5;
                                    rectF6.set(measuredWidth3 - var_, measuredHeight - var_, measuredWidth3 + var_, measuredHeight + var_);
                                    float lerp5 = AndroidUtilities.lerp(measuredWidth4 / 2.0f, AndroidUtilities.dp(4.0f), CustomEmojiReactionsWindow.this.enterTransitionProgress);
                                    canvas.drawRoundRect(rectF6, lerp5, lerp5, CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.selectorPaint);
                                } else {
                                    i2 = i5;
                                }
                                if (var_ != 0.0f) {
                                    ImageReceiver imageReceiver = reactionHolderView.loopImageView.getImageReceiver();
                                    AnimatedEmojiDrawable animatedEmojiDrawable = reactionHolderView.loopImageView.animatedEmojiDrawable;
                                    if (animatedEmojiDrawable != null && animatedEmojiDrawable.getImageReceiver() != null) {
                                        imageReceiver = reactionHolderView.loopImageView.animatedEmojiDrawable.getImageReceiver();
                                    }
                                    int[] roundRadius = imageReceiver.getRoundRadius();
                                    for (int i6 = 0; i6 < 4; i6++) {
                                        this.radiusTmp[i6] = roundRadius[i6];
                                    }
                                    imageReceiver.setRoundRadius((int) AndroidUtilities.lerp(f7, 0.0f, CustomEmojiReactionsWindow.this.enterTransitionProgress), (int) AndroidUtilities.lerp(f8, 0.0f, CustomEmojiReactionsWindow.this.enterTransitionProgress), (int) AndroidUtilities.lerp(f9, 0.0f, CustomEmojiReactionsWindow.this.enterTransitionProgress), (int) AndroidUtilities.lerp(var_, 0.0f, CustomEmojiReactionsWindow.this.enterTransitionProgress));
                                    reactionHolderView.loopImageView.draw(canvas);
                                    reactionHolderView.loopImageView.draw(canvas);
                                    imageReceiver.setRoundRadius(this.radiusTmp);
                                } else {
                                    reactionHolderView.loopImageView.draw(canvas);
                                }
                                if (!imageViewEmoji2.notDraw) {
                                    imageViewEmoji2.notDraw = true;
                                    imageViewEmoji2.invalidate();
                                }
                            } else {
                                i2 = i5;
                                if (reactionHolderView.hasEnterAnimation) {
                                    float alpha = reactionHolderView.enterImageView.getImageReceiver().getAlpha();
                                    reactionHolderView.enterImageView.getImageReceiver().setAlpha((1.0f - CustomEmojiReactionsWindow.this.enterTransitionProgress) * alpha);
                                    reactionHolderView.enterImageView.draw(canvas);
                                    reactionHolderView.enterImageView.getImageReceiver().setAlpha(alpha);
                                } else {
                                    ImageReceiver imageReceiver2 = reactionHolderView.loopImageView.getImageReceiver();
                                    AnimatedEmojiDrawable animatedEmojiDrawable2 = reactionHolderView.loopImageView.animatedEmojiDrawable;
                                    if (animatedEmojiDrawable2 != null && animatedEmojiDrawable2.getImageReceiver() != null) {
                                        imageReceiver2 = reactionHolderView.loopImageView.animatedEmojiDrawable.getImageReceiver();
                                    }
                                    float alpha2 = imageReceiver2.getAlpha();
                                    imageReceiver2.setAlpha((1.0f - CustomEmojiReactionsWindow.this.enterTransitionProgress) * alpha2);
                                    reactionHolderView.loopImageView.draw(canvas);
                                    imageReceiver2.setAlpha(alpha2);
                                }
                            }
                            i = i2;
                        } else {
                            float x3 = (view.getX() + CustomEmojiReactionsWindow.this.drawingRect.width()) - CustomEmojiReactionsWindow.this.reactionsContainerLayout.rect.width();
                            float y3 = view.getY();
                            CustomEmojiReactionsWindow customEmojiReactionsWindow8 = CustomEmojiReactionsWindow.this;
                            canvas.translate(x3, (y3 + customEmojiReactionsWindow8.fromRect.top) - customEmojiReactionsWindow8.drawingRect.top);
                            i = i5;
                            canvas.saveLayerAlpha(0.0f, 0.0f, view.getMeasuredWidth(), view.getMeasuredHeight(), (int) ((1.0f - CustomEmojiReactionsWindow.this.enterTransitionProgress) * 255.0f), 31);
                            float var_ = CustomEmojiReactionsWindow.this.enterTransitionProgress;
                            canvas.scale(1.0f - var_, 1.0f - var_, view.getMeasuredWidth() >> 1, view.getMeasuredHeight() >> 1);
                            view.draw(canvas);
                            canvas.restore();
                        }
                        canvas.restore();
                    }
                    i5 = i + 1;
                    var_ = 0.0f;
                    var_ = 1.0f;
                    i4 = -1;
                }
                canvas.restore();
                f4 = var_;
                f = var_;
                f5 = var_;
                f2 = var_;
                f3 = var_;
            }
            if (CustomEmojiReactionsWindow.this.invalidatePath) {
                CustomEmojiReactionsWindow.this.invalidatePath = false;
                CustomEmojiReactionsWindow.this.pathToClip.rewind();
                CustomEmojiReactionsWindow customEmojiReactionsWindow9 = CustomEmojiReactionsWindow.this;
                customEmojiReactionsWindow9.pathToClip.addRoundRect(customEmojiReactionsWindow9.drawingRect, lerp, lerp, Path.Direction.CW);
            }
            canvas.save();
            canvas.clipPath(CustomEmojiReactionsWindow.this.pathToClip);
            canvas.translate(f4, f);
            canvas.scale(f5, f5, f2, f3);
            CustomEmojiReactionsWindow customEmojiReactionsWindow10 = CustomEmojiReactionsWindow.this;
            customEmojiReactionsWindow10.selectAnimatedEmojiDialog.setAlpha(customEmojiReactionsWindow10.enterTransitionProgress);
            super.dispatchDraw(canvas);
            canvas.restore();
            if (CustomEmojiReactionsWindow.this.frameDrawCount < 5) {
                if (CustomEmojiReactionsWindow.this.frameDrawCount == 3) {
                    CustomEmojiReactionsWindow.this.reactionsContainerLayout.setSkipDraw(true);
                }
                CustomEmojiReactionsWindow.access$808(CustomEmojiReactionsWindow.this);
            }
            CustomEmojiReactionsWindow.this.selectAnimatedEmojiDialog.drawBigReaction(canvas, this);
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean imageIsEquals(BackupImageView backupImageView, SelectAnimatedEmojiDialog.ImageViewEmoji imageViewEmoji) {
        AnimatedEmojiSpan animatedEmojiSpan = imageViewEmoji.span;
        return animatedEmojiSpan == null ? imageViewEmoji.imageReceiver.getLottieAnimation() == backupImageView.getImageReceiver().getLottieAnimation() : backupImageView.animatedEmojiDrawable != null && animatedEmojiSpan.getDocumentId() == backupImageView.animatedEmojiDrawable.getDocumentId();
    }

    public void setRecentReactions(List<ReactionsLayoutInBubble.VisibleReaction> list) {
        this.selectAnimatedEmojiDialog.setRecentReactions(list);
    }

    public SelectAnimatedEmojiDialog getSelectAnimatedEmojiDialog() {
        return this.selectAnimatedEmojiDialog;
    }
}
