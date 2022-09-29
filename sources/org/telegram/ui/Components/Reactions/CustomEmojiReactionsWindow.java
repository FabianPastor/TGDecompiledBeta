package org.telegram.ui.Components.Reactions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
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
    float keyboardHeight;
    private Runnable onDismiss;
    Path pathToClip = new Path();
    List<ReactionsLayoutInBubble.VisibleReaction> reactions;
    ReactionsContainerLayout reactionsContainerLayout;
    Theme.ResourcesProvider resourcesProvider;
    SelectAnimatedEmojiDialog selectAnimatedEmojiDialog;
    /* access modifiers changed from: private */
    public boolean wasFocused;
    WindowManager windowManager;
    FrameLayout windowView;
    float yTranslation;

    static /* synthetic */ int access$808(CustomEmojiReactionsWindow customEmojiReactionsWindow) {
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

            /* access modifiers changed from: protected */
            public boolean fitSystemWindows(Rect rect) {
                CustomEmojiReactionsWindow customEmojiReactionsWindow = CustomEmojiReactionsWindow.this;
                if (customEmojiReactionsWindow.keyboardHeight != ((float) rect.bottom) && customEmojiReactionsWindow.wasFocused) {
                    CustomEmojiReactionsWindow customEmojiReactionsWindow2 = CustomEmojiReactionsWindow.this;
                    customEmojiReactionsWindow2.keyboardHeight = (float) rect.bottom;
                    customEmojiReactionsWindow2.updateWindowPosition();
                }
                return super.fitSystemWindows(rect);
            }
        };
        this.windowView = r0;
        r0.setOnClickListener(new CustomEmojiReactionsWindow$$ExternalSyntheticLambda2(this));
        this.containerView = new ContainerView(context);
        final BaseFragment baseFragment3 = baseFragment2;
        final ReactionsContainerLayout reactionsContainerLayout4 = reactionsContainerLayout2;
        AnonymousClass2 r02 = new SelectAnimatedEmojiDialog(this, baseFragment2, context, false, (Integer) null, 1, resourcesProvider3) {
            final /* synthetic */ CustomEmojiReactionsWindow this$0;

            {
                this.this$0 = r9;
            }

            /* access modifiers changed from: protected */
            public void onInputFocus() {
                if (!this.this$0.wasFocused) {
                    boolean unused = this.this$0.wasFocused = true;
                    CustomEmojiReactionsWindow customEmojiReactionsWindow = this.this$0;
                    customEmojiReactionsWindow.windowManager.updateViewLayout(customEmojiReactionsWindow.windowView, customEmojiReactionsWindow.createLayoutParams(true));
                    BaseFragment baseFragment = baseFragment3;
                    if (baseFragment instanceof ChatActivity) {
                        ((ChatActivity) baseFragment).needEnterText();
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onReactionClick(SelectAnimatedEmojiDialog.ImageViewEmoji imageViewEmoji, ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
                reactionsContainerLayout4.onReactionClicked(imageViewEmoji, visibleReaction, false);
                AndroidUtilities.hideKeyboard(this.this$0.windowView);
            }

            /* access modifiers changed from: protected */
            public void onEmojiSelected(View view, Long l, TLRPC$Document tLRPC$Document, Integer num) {
                if (!UserConfig.getInstance(baseFragment3.getCurrentAccount()).isPremium()) {
                    this.this$0.windowView.performHapticFeedback(3);
                    BulletinFactory.of(this.this$0.windowView, (Theme.ResourcesProvider) null).createEmojiBulletin(tLRPC$Document, AndroidUtilities.replaceTags(LocaleController.getString("UnlockPremiumEmojiReaction", R.string.UnlockPremiumEmojiReaction)), LocaleController.getString("PremiumMore", R.string.PremiumMore), new CustomEmojiReactionsWindow$2$$ExternalSyntheticLambda0(this)).show();
                    return;
                }
                reactionsContainerLayout4.onReactionClicked(view, ReactionsLayoutInBubble.VisibleReaction.fromCustomEmoji(l), false);
                AndroidUtilities.hideKeyboard(this.this$0.windowView);
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onEmojiSelected$0() {
                this.this$0.showUnlockPremiumAlert();
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
        WindowManager.LayoutParams createLayoutParams = createLayoutParams(false);
        WindowManager windowManager2 = baseFragment2.getParentActivity().getWindowManager();
        this.windowManager = windowManager2;
        windowManager2.addView(this.windowView, createLayoutParams);
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
    public void updateWindowPosition() {
        if (!this.dismissed) {
            float f = this.yTranslation;
            if (((float) this.containerView.getMeasuredHeight()) + f > (((float) this.windowView.getMeasuredHeight()) - this.keyboardHeight) - ((float) AndroidUtilities.dp(32.0f))) {
                f = ((((float) this.windowView.getMeasuredHeight()) - this.keyboardHeight) - ((float) this.containerView.getMeasuredHeight())) - ((float) AndroidUtilities.dp(32.0f));
            }
            if (f < 0.0f) {
                f = 0.0f;
            }
            this.containerView.animate().translationY(f).setDuration(250).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
    }

    /* access modifiers changed from: private */
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
        int[] iArr2 = new int[2];
        reactionsContainerLayout2.getLocationOnScreen(iArr);
        this.windowView.getLocationOnScreen(iArr2);
        float dp = (float) (((iArr[1] - iArr2[1]) - AndroidUtilities.dp(44.0f)) - AndroidUtilities.dp(34.0f));
        if (((float) this.containerView.getMeasuredHeight()) + dp > ((float) (this.windowView.getMeasuredHeight() - AndroidUtilities.dp(32.0f)))) {
            dp = (float) ((this.windowView.getMeasuredHeight() - AndroidUtilities.dp(32.0f)) - this.containerView.getMeasuredHeight());
        }
        if (dp < ((float) AndroidUtilities.dp(16.0f))) {
            dp = (float) AndroidUtilities.dp(16.0f);
        }
        this.containerView.setTranslationX((float) ((iArr[0] - iArr2[0]) - AndroidUtilities.dp(2.0f)));
        if (!z) {
            this.yTranslation = this.containerView.getTranslationY();
        } else {
            this.yTranslation = dp;
        }
        this.containerView.setTranslationY(this.yTranslation);
        this.fromRect.offset(((float) (iArr[0] - iArr2[0])) - this.containerView.getX(), ((float) (iArr[1] - iArr2[1])) - this.containerView.getY());
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
            AndroidUtilities.hideKeyboard(this.windowView);
            createTransition(false);
            if (this.wasFocused) {
                BaseFragment baseFragment2 = this.baseFragment;
                if (baseFragment2 instanceof ChatActivity) {
                    ((ChatActivity) baseFragment2).onEditTextDialogClose(true, true);
                }
            }
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
        int[] radiusTmp = new int[4];
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
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0110, code lost:
            r2 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r0.this$0.selectAnimatedEmojiDialog.emojiGridView.getChildAt(r1);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void dispatchDraw(android.graphics.Canvas r26) {
            /*
                r25 = this;
                r0 = r25
                r8 = r26
                android.graphics.Paint r1 = r0.dimPaint
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r2 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r2 = r2.enterTransitionProgress
                r3 = 1045220557(0x3e4ccccd, float:0.2)
                float r2 = r2 * r3
                r9 = 1132396544(0x437var_, float:255.0)
                float r2 = r2 * r9
                int r2 = (int) r2
                r1.setAlpha(r2)
                android.graphics.Paint r1 = r0.dimPaint
                r8.drawPaint(r1)
                android.graphics.RectF r1 = org.telegram.messenger.AndroidUtilities.rectTmp
                int r2 = r25.getMeasuredWidth()
                float r2 = (float) r2
                int r3 = r25.getMeasuredHeight()
                float r3 = (float) r3
                r10 = 0
                r1.set(r10, r10, r2, r3)
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r2 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                android.graphics.RectF r3 = r2.fromRect
                float r4 = r2.enterTransitionProgress
                android.graphics.RectF r2 = r2.drawingRect
                org.telegram.messenger.AndroidUtilities.lerp((android.graphics.RectF) r3, (android.graphics.RectF) r1, (float) r4, (android.graphics.RectF) r2)
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r1 = r1.fromRadius
                r2 = 1090519040(0x41000000, float:8.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r3 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r3 = r3.enterTransitionProgress
                float r11 = org.telegram.messenger.AndroidUtilities.lerp((float) r1, (float) r2, (float) r3)
                android.graphics.drawable.Drawable r1 = r0.shadow
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r2 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r2 = r2.enterTransitionProgress
                r3 = 1028443341(0x3d4ccccd, float:0.05)
                float r2 = r2 / r3
                r12 = 1065353216(0x3var_, float:1.0)
                float r2 = org.telegram.messenger.Utilities.clamp((float) r2, (float) r12, (float) r10)
                float r2 = r2 * r9
                int r2 = (int) r2
                r1.setAlpha(r2)
                android.graphics.drawable.Drawable r1 = r0.shadow
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r2 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                android.graphics.RectF r2 = r2.drawingRect
                float r4 = r2.left
                int r4 = (int) r4
                android.graphics.Rect r5 = r0.shadowPad
                int r6 = r5.left
                int r4 = r4 - r6
                float r6 = r2.top
                int r6 = (int) r6
                int r7 = r5.top
                int r6 = r6 - r7
                float r7 = r2.right
                int r7 = (int) r7
                int r13 = r5.right
                int r7 = r7 + r13
                float r2 = r2.bottom
                int r2 = (int) r2
                int r5 = r5.bottom
                int r2 = r2 + r5
                r1.setBounds(r4, r6, r7, r2)
                android.graphics.drawable.Drawable r1 = r0.shadow
                r1.draw(r8)
                java.util.HashMap<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction, org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r1 = r0.transitionReactions
                r1.clear()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                android.graphics.RectF r1 = r1.drawingRect
                android.graphics.Paint r2 = r0.backgroundPaint
                r8.drawRoundRect(r1, r11, r11, r2)
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                android.graphics.RectF r2 = r1.drawingRect
                float r4 = r2.left
                org.telegram.ui.Components.ReactionsContainerLayout r1 = r1.reactionsContainerLayout
                android.graphics.RectF r1 = r1.rect
                float r1 = r1.left
                float r4 = r4 - r1
                float r1 = r2.width()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r2 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.Components.ReactionsContainerLayout r2 = r2.reactionsContainerLayout
                android.graphics.RectF r2 = r2.rect
                float r2 = r2.width()
                float r1 = r1 - r2
                float r4 = r4 + r1
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r1 = r1.enterTransitionProgress
                int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                if (r1 <= 0) goto L_0x00e8
                r26.save()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                android.graphics.RectF r2 = r1.drawingRect
                float r3 = r2.top
                org.telegram.ui.Components.ReactionsContainerLayout r1 = r1.reactionsContainerLayout
                android.graphics.RectF r1 = r1.rect
                float r1 = r1.top
                float r3 = r3 - r1
                float r1 = r2.height()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r2 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.Components.ReactionsContainerLayout r2 = r2.reactionsContainerLayout
                android.graphics.RectF r2 = r2.rect
                float r2 = r2.height()
                float r1 = r1 - r2
                float r3 = r3 + r1
                r8.translate(r4, r3)
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.Components.ReactionsContainerLayout r1 = r1.reactionsContainerLayout
                r1.drawBubbles(r8)
                r26.restore()
            L_0x00e8:
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.Components.ReactionsContainerLayout r2 = r1.reactionsContainerLayout
                r13 = 0
                if (r2 == 0) goto L_0x04b0
                float r1 = r1.enterTransitionProgress
                int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
                if (r1 == 0) goto L_0x04b0
                r1 = 0
            L_0x00f6:
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r2 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = r2.selectAnimatedEmojiDialog
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r2 = r2.emojiGridView
                int r2 = r2.getChildCount()
                if (r1 >= r2) goto L_0x012d
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r2 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = r2.selectAnimatedEmojiDialog
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r2 = r2.emojiGridView
                android.view.View r2 = r2.getChildAt(r1)
                boolean r2 = r2 instanceof org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji
                if (r2 == 0) goto L_0x012a
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r2 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = r2.selectAnimatedEmojiDialog
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r2 = r2.emojiGridView
                android.view.View r2 = r2.getChildAt(r1)
                org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r2 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r2
                org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r3 = r2.reaction
                if (r3 == 0) goto L_0x012a
                java.util.HashMap<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction, org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r4 = r0.transitionReactions
                r4.put(r3, r2)
                r2.notDraw = r13
                r2.invalidate()
            L_0x012a:
                int r1 = r1 + 1
                goto L_0x00f6
            L_0x012d:
                r26.save()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                android.graphics.RectF r2 = r1.drawingRect
                float r3 = r2.left
                float r2 = r2.top
                org.telegram.ui.Components.ReactionsContainerLayout r1 = r1.reactionsContainerLayout
                float r1 = r1.expandSize()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r4 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r4 = r4.enterTransitionProgress
                float r4 = r12 - r4
                float r1 = r1 * r4
                float r2 = r2 + r1
                r8.translate(r3, r2)
                r15 = -1
                r7 = -1
                r16 = 0
                r17 = 0
                r18 = 1065353216(0x3var_, float:1.0)
                r19 = 0
                r20 = 0
            L_0x0156:
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.Components.ReactionsContainerLayout r1 = r1.reactionsContainerLayout
                org.telegram.ui.Components.RecyclerListView r1 = r1.recyclerListView
                int r1 = r1.getChildCount()
                if (r7 >= r1) goto L_0x04a2
                if (r7 != r15) goto L_0x016b
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.Components.ReactionsContainerLayout r1 = r1.reactionsContainerLayout
                org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r1 = r1.nextRecentReaction
                goto L_0x0175
            L_0x016b:
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.Components.ReactionsContainerLayout r1 = r1.reactionsContainerLayout
                org.telegram.ui.Components.RecyclerListView r1 = r1.recyclerListView
                android.view.View r1 = r1.getChildAt(r7)
            L_0x0175:
                r6 = r1
                int r1 = r6.getLeft()
                if (r1 < 0) goto L_0x0491
                int r1 = r6.getVisibility()
                r2 = 8
                if (r1 != r2) goto L_0x0186
                goto L_0x0491
            L_0x0186:
                r26.save()
                boolean r1 = r6 instanceof org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView
                if (r1 == 0) goto L_0x041a
                r1 = r6
                org.telegram.ui.Components.ReactionsContainerLayout$ReactionHolderView r1 = (org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView) r1
                java.util.HashMap<org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction, org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji> r2 = r0.transitionReactions
                org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$VisibleReaction r3 = r1.currentReaction
                java.lang.Object r2 = r2.get(r3)
                org.telegram.ui.SelectAnimatedEmojiDialog$ImageViewEmoji r2 = (org.telegram.ui.SelectAnimatedEmojiDialog.ImageViewEmoji) r2
                r3 = 1073741824(0x40000000, float:2.0)
                if (r2 == 0) goto L_0x02a0
                float r4 = r6.getX()
                org.telegram.ui.Components.BackupImageView r5 = r1.loopImageView
                float r5 = r5.getX()
                float r4 = r4 + r5
                float r5 = r6.getY()
                org.telegram.ui.Components.BackupImageView r6 = r1.loopImageView
                float r6 = r6.getY()
                float r5 = r5 + r6
                if (r7 != r15) goto L_0x01cc
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r6 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.Components.ReactionsContainerLayout r6 = r6.reactionsContainerLayout
                org.telegram.ui.Components.RecyclerListView r6 = r6.recyclerListView
                float r6 = r6.getX()
                float r4 = r4 - r6
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r6 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.Components.ReactionsContainerLayout r6 = r6.reactionsContainerLayout
                org.telegram.ui.Components.RecyclerListView r6 = r6.recyclerListView
                float r6 = r6.getY()
                float r5 = r5 - r6
            L_0x01cc:
                float r6 = r2.getX()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r15 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.SelectAnimatedEmojiDialog r15 = r15.selectAnimatedEmojiDialog
                float r15 = r15.getX()
                float r6 = r6 + r15
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r15 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.SelectAnimatedEmojiDialog r15 = r15.selectAnimatedEmojiDialog
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r15 = r15.emojiGridView
                float r15 = r15.getX()
                float r6 = r6 + r15
                float r15 = r2.getY()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r13 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.SelectAnimatedEmojiDialog r13 = r13.selectAnimatedEmojiDialog
                float r13 = r13.getY()
                float r15 = r15 + r13
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r13 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.SelectAnimatedEmojiDialog r13 = r13.selectAnimatedEmojiDialog
                android.widget.FrameLayout r13 = r13.gridViewContainer
                float r13 = r13.getY()
                float r15 = r15 + r13
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r13 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.SelectAnimatedEmojiDialog r13 = r13.selectAnimatedEmojiDialog
                org.telegram.ui.SelectAnimatedEmojiDialog$EmojiListView r13 = r13.emojiGridView
                float r13 = r13.getY()
                float r15 = r15 + r13
                int r13 = r2.getMeasuredWidth()
                float r13 = (float) r13
                boolean r9 = r2.selected
                if (r9 == 0) goto L_0x021a
                r9 = 1063004406(0x3f5CLASSNAMEf6, float:0.86)
                float r9 = r9 * r13
                float r13 = r13 - r9
                float r13 = r13 / r3
                float r6 = r6 + r13
                float r15 = r15 + r13
                r13 = r9
            L_0x021a:
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r9 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r9 = r9.enterTransitionProgress
                float r9 = org.telegram.messenger.AndroidUtilities.lerp((float) r4, (float) r6, (float) r9)
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r14 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r14 = r14.enterTransitionProgress
                float r14 = org.telegram.messenger.AndroidUtilities.lerp((float) r5, (float) r15, (float) r14)
                org.telegram.ui.Components.BackupImageView r3 = r1.loopImageView
                int r3 = r3.getMeasuredWidth()
                float r3 = (float) r3
                float r13 = r13 / r3
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r3 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r3 = r3.enterTransitionProgress
                float r3 = org.telegram.messenger.AndroidUtilities.lerp((float) r12, (float) r13, (float) r3)
                int r12 = r1.position
                r22 = 1086324736(0x40CLASSNAME, float:6.0)
                if (r12 != 0) goto L_0x0248
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
                float r12 = (float) r12
                r22 = r12
                goto L_0x025b
            L_0x0248:
                boolean r12 = r1.selected
                if (r12 == 0) goto L_0x0258
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r22)
                float r12 = (float) r12
                r22 = r12
                r23 = r22
                r24 = r23
                goto L_0x025f
            L_0x0258:
                r12 = 0
                r22 = 0
            L_0x025b:
                r23 = 0
                r24 = 0
            L_0x025f:
                r8.translate(r9, r14)
                r8.scale(r3, r3)
                int r9 = (r16 > r10 ? 1 : (r16 == r10 ? 0 : -1))
                if (r9 != 0) goto L_0x0299
                int r9 = (r17 > r10 ? 1 : (r17 == r10 ? 0 : -1))
                if (r9 != 0) goto L_0x0299
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r9 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                android.graphics.RectF r14 = r9.fromRect
                float r14 = r14.left
                float r14 = r14 + r4
                float r14 = r14 - r6
                float r4 = r9.enterTransitionProgress
                float r16 = org.telegram.messenger.AndroidUtilities.lerp((float) r14, (float) r10, (float) r4)
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r4 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                android.graphics.RectF r9 = r4.fromRect
                float r9 = r9.top
                float r9 = r9 + r5
                float r9 = r9 - r15
                float r4 = r4.enterTransitionProgress
                float r17 = org.telegram.messenger.AndroidUtilities.lerp((float) r9, (float) r10, (float) r4)
                r4 = 1065353216(0x3var_, float:1.0)
                float r5 = r4 / r13
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r9 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r9 = r9.enterTransitionProgress
                float r18 = org.telegram.messenger.AndroidUtilities.lerp((float) r5, (float) r4, (float) r9)
                r19 = r6
                r20 = r15
            L_0x0299:
                r4 = r22
                r5 = r23
                r6 = r24
                goto L_0x02bf
            L_0x02a0:
                float r3 = r6.getX()
                org.telegram.ui.Components.BackupImageView r4 = r1.loopImageView
                float r4 = r4.getX()
                float r3 = r3 + r4
                float r4 = r6.getY()
                org.telegram.ui.Components.BackupImageView r5 = r1.loopImageView
                float r5 = r5.getY()
                float r4 = r4 + r5
                r8.translate(r3, r4)
                r3 = 1065353216(0x3var_, float:1.0)
                r4 = 0
                r5 = 0
                r6 = 0
                r12 = 0
            L_0x02bf:
                org.telegram.ui.Components.BackupImageView r9 = r1.loopImageView
                int r9 = r9.getVisibility()
                if (r9 != 0) goto L_0x03ad
                if (r2 == 0) goto L_0x03ad
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r9 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.Components.BackupImageView r13 = r1.loopImageView
                boolean r9 = r9.imageIsEquals(r13, r2)
                if (r9 == 0) goto L_0x03ad
                boolean r9 = r2.selected
                if (r9 == 0) goto L_0x0332
                org.telegram.ui.Components.BackupImageView r9 = r1.loopImageView
                int r9 = r9.getMeasuredWidth()
                float r9 = (float) r9
                r13 = 1073741824(0x40000000, float:2.0)
                float r9 = r9 / r13
                org.telegram.ui.Components.BackupImageView r14 = r1.loopImageView
                int r14 = r14.getMeasuredHeight()
                float r14 = (float) r14
                float r14 = r14 / r13
                int r15 = r1.getMeasuredWidth()
                int r21 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r15 = r15 - r21
                float r15 = (float) r15
                int r21 = r2.getMeasuredWidth()
                int r22 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r10 = r21 - r22
                float r10 = (float) r10
                float r10 = r10 / r3
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r3 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r3 = r3.enterTransitionProgress
                float r3 = org.telegram.messenger.AndroidUtilities.lerp((float) r15, (float) r10, (float) r3)
                android.graphics.RectF r10 = org.telegram.messenger.AndroidUtilities.rectTmp
                float r3 = r3 / r13
                float r13 = r9 - r3
                r22 = r7
                float r7 = r14 - r3
                float r9 = r9 + r3
                float r14 = r14 + r3
                r10.set(r13, r7, r9, r14)
                r3 = 1073741824(0x40000000, float:2.0)
                float r15 = r15 / r3
                r3 = 1082130432(0x40800000, float:4.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r3 = (float) r3
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r7 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r7 = r7.enterTransitionProgress
                float r3 = org.telegram.messenger.AndroidUtilities.lerp((float) r15, (float) r3, (float) r7)
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r7 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.SelectAnimatedEmojiDialog r7 = r7.selectAnimatedEmojiDialog
                android.graphics.Paint r7 = r7.selectorPaint
                r8.drawRoundRect(r10, r3, r3, r7)
                goto L_0x0334
            L_0x0332:
                r22 = r7
            L_0x0334:
                r3 = 0
                int r7 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
                if (r7 != 0) goto L_0x0340
                org.telegram.ui.Components.BackupImageView r1 = r1.loopImageView
                r1.draw(r8)
                r9 = 0
                goto L_0x03a2
            L_0x0340:
                org.telegram.ui.Components.BackupImageView r3 = r1.loopImageView
                org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
                org.telegram.ui.Components.BackupImageView r7 = r1.loopImageView
                org.telegram.ui.Components.AnimatedEmojiDrawable r7 = r7.animatedEmojiDrawable
                if (r7 == 0) goto L_0x035a
                org.telegram.messenger.ImageReceiver r7 = r7.getImageReceiver()
                if (r7 == 0) goto L_0x035a
                org.telegram.ui.Components.BackupImageView r3 = r1.loopImageView
                org.telegram.ui.Components.AnimatedEmojiDrawable r3 = r3.animatedEmojiDrawable
                org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
            L_0x035a:
                int[] r7 = r3.getRoundRadius()
                r9 = 0
            L_0x035f:
                r10 = 4
                if (r9 >= r10) goto L_0x036b
                int[] r10 = r0.radiusTmp
                r13 = r7[r9]
                r10[r9] = r13
                int r9 = r9 + 1
                goto L_0x035f
            L_0x036b:
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r7 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r7 = r7.enterTransitionProgress
                r9 = 0
                float r4 = org.telegram.messenger.AndroidUtilities.lerp((float) r4, (float) r9, (float) r7)
                int r4 = (int) r4
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r7 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r7 = r7.enterTransitionProgress
                float r5 = org.telegram.messenger.AndroidUtilities.lerp((float) r5, (float) r9, (float) r7)
                int r5 = (int) r5
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r7 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r7 = r7.enterTransitionProgress
                float r6 = org.telegram.messenger.AndroidUtilities.lerp((float) r6, (float) r9, (float) r7)
                int r6 = (int) r6
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r7 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r7 = r7.enterTransitionProgress
                float r7 = org.telegram.messenger.AndroidUtilities.lerp((float) r12, (float) r9, (float) r7)
                int r7 = (int) r7
                r3.setRoundRadius(r4, r5, r6, r7)
                org.telegram.ui.Components.BackupImageView r4 = r1.loopImageView
                r4.draw(r8)
                org.telegram.ui.Components.BackupImageView r1 = r1.loopImageView
                r1.draw(r8)
                int[] r1 = r0.radiusTmp
                r3.setRoundRadius((int[]) r1)
            L_0x03a2:
                boolean r1 = r2.notDraw
                if (r1 != 0) goto L_0x0413
                r1 = 1
                r2.notDraw = r1
                r2.invalidate()
                goto L_0x0413
            L_0x03ad:
                r22 = r7
                r9 = 0
                boolean r2 = r1.hasEnterAnimation
                if (r2 == 0) goto L_0x03e0
                org.telegram.ui.Components.BackupImageView r2 = r1.enterImageView
                org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
                float r2 = r2.getAlpha()
                org.telegram.ui.Components.BackupImageView r3 = r1.enterImageView
                org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r4 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r4 = r4.enterTransitionProgress
                r5 = 1065353216(0x3var_, float:1.0)
                float r12 = r5 - r4
                float r12 = r12 * r2
                r3.setAlpha(r12)
                org.telegram.ui.Components.BackupImageView r3 = r1.enterImageView
                r3.draw(r8)
                org.telegram.ui.Components.BackupImageView r1 = r1.enterImageView
                org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
                r1.setAlpha(r2)
                goto L_0x0413
            L_0x03e0:
                org.telegram.ui.Components.BackupImageView r2 = r1.loopImageView
                org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
                org.telegram.ui.Components.BackupImageView r3 = r1.loopImageView
                org.telegram.ui.Components.AnimatedEmojiDrawable r3 = r3.animatedEmojiDrawable
                if (r3 == 0) goto L_0x03fa
                org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
                if (r3 == 0) goto L_0x03fa
                org.telegram.ui.Components.BackupImageView r2 = r1.loopImageView
                org.telegram.ui.Components.AnimatedEmojiDrawable r2 = r2.animatedEmojiDrawable
                org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
            L_0x03fa:
                float r3 = r2.getAlpha()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r4 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r4 = r4.enterTransitionProgress
                r5 = 1065353216(0x3var_, float:1.0)
                float r12 = r5 - r4
                float r12 = r12 * r3
                r2.setAlpha(r12)
                org.telegram.ui.Components.BackupImageView r1 = r1.loopImageView
                r1.draw(r8)
                r2.setAlpha(r3)
            L_0x0413:
                r15 = r22
                r2 = 1065353216(0x3var_, float:1.0)
                r10 = 1132396544(0x437var_, float:255.0)
                goto L_0x048d
            L_0x041a:
                r22 = r7
                r9 = 0
                float r1 = r6.getX()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r2 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                android.graphics.RectF r2 = r2.drawingRect
                float r2 = r2.width()
                float r1 = r1 + r2
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r2 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.Components.ReactionsContainerLayout r2 = r2.reactionsContainerLayout
                android.graphics.RectF r2 = r2.rect
                float r2 = r2.width()
                float r1 = r1 - r2
                float r2 = r6.getY()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r3 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                android.graphics.RectF r4 = r3.fromRect
                float r4 = r4.top
                float r2 = r2 + r4
                android.graphics.RectF r3 = r3.drawingRect
                float r3 = r3.top
                float r2 = r2 - r3
                r8.translate(r1, r2)
                r2 = 0
                r3 = 0
                int r1 = r6.getMeasuredWidth()
                float r4 = (float) r1
                int r1 = r6.getMeasuredHeight()
                float r5 = (float) r1
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r1 = r1.enterTransitionProgress
                r7 = 1065353216(0x3var_, float:1.0)
                float r12 = r7 - r1
                r10 = 1132396544(0x437var_, float:255.0)
                float r12 = r12 * r10
                int r7 = (int) r12
                r12 = 31
                r1 = r26
                r13 = r6
                r6 = r7
                r15 = r22
                r7 = r12
                r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                float r1 = r1.enterTransitionProgress
                r2 = 1065353216(0x3var_, float:1.0)
                float r12 = r2 - r1
                float r1 = r2 - r1
                int r3 = r13.getMeasuredWidth()
                r4 = 1
                int r3 = r3 >> r4
                float r3 = (float) r3
                int r5 = r13.getMeasuredHeight()
                int r5 = r5 >> r4
                float r4 = (float) r5
                r8.scale(r12, r1, r3, r4)
                r13.draw(r8)
                r26.restore()
            L_0x048d:
                r26.restore()
                goto L_0x0497
            L_0x0491:
                r15 = r7
                r2 = 1065353216(0x3var_, float:1.0)
                r9 = 0
                r10 = 1132396544(0x437var_, float:255.0)
            L_0x0497:
                int r7 = r15 + 1
                r9 = 1132396544(0x437var_, float:255.0)
                r10 = 0
                r12 = 1065353216(0x3var_, float:1.0)
                r13 = 0
                r15 = -1
                goto L_0x0156
            L_0x04a2:
                r26.restore()
                r10 = r16
                r9 = r17
                r12 = r18
                r1 = r19
                r2 = r20
                goto L_0x04b8
            L_0x04b0:
                r2 = 1065353216(0x3var_, float:1.0)
                r9 = 0
                r1 = 0
                r2 = 0
                r10 = 0
                r12 = 1065353216(0x3var_, float:1.0)
            L_0x04b8:
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r3 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                boolean r3 = r3.invalidatePath
                if (r3 == 0) goto L_0x04d8
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r3 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                r4 = 0
                boolean unused = r3.invalidatePath = r4
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r3 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                android.graphics.Path r3 = r3.pathToClip
                r3.rewind()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r3 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                android.graphics.Path r4 = r3.pathToClip
                android.graphics.RectF r3 = r3.drawingRect
                android.graphics.Path$Direction r5 = android.graphics.Path.Direction.CW
                r4.addRoundRect(r3, r11, r11, r5)
            L_0x04d8:
                r26.save()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r3 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                android.graphics.Path r3 = r3.pathToClip
                r8.clipPath(r3)
                r8.translate(r10, r9)
                r8.scale(r12, r12, r1, r2)
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.SelectAnimatedEmojiDialog r2 = r1.selectAnimatedEmojiDialog
                float r1 = r1.enterTransitionProgress
                r2.setAlpha(r1)
                super.dispatchDraw(r26)
                r26.restore()
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                int r1 = r1.frameDrawCount
                r2 = 5
                if (r1 >= r2) goto L_0x0516
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                int r1 = r1.frameDrawCount
                r2 = 3
                if (r1 != r2) goto L_0x0511
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.Components.ReactionsContainerLayout r1 = r1.reactionsContainerLayout
                r2 = 1
                r1.setSkipDraw(r2)
            L_0x0511:
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.access$808(r1)
            L_0x0516:
                org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow r1 = org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.this
                org.telegram.ui.SelectAnimatedEmojiDialog r1 = r1.selectAnimatedEmojiDialog
                r1.drawBigReaction(r8, r0)
                r25.invalidate()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow.ContainerView.dispatchDraw(android.graphics.Canvas):void");
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
