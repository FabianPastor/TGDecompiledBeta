package org.telegram.ui.Components.Reactions;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.ReactionsContainerLayout;

public class ReactionsEffectOverlay {
    public static ReactionsEffectOverlay currentOverlay;
    private static int unicPrefix;
    float animateInProgress;
    float animateOutProgress;
    /* access modifiers changed from: private */
    public final FrameLayout container;
    /* access modifiers changed from: private */
    public float dismissProgress;
    /* access modifiers changed from: private */
    public boolean dismissed;
    /* access modifiers changed from: private */
    public final AnimationView effectImageView;
    /* access modifiers changed from: private */
    public final AnimationView emojiImageView;
    /* access modifiers changed from: private */
    public final AnimationView emojiStaticImageView;
    private final long groupId;
    /* access modifiers changed from: private */
    public ReactionsContainerLayout.ReactionHolderView holderView;
    /* access modifiers changed from: private */
    public float lastDrawnToX;
    /* access modifiers changed from: private */
    public float lastDrawnToY;
    int[] loc = new int[2];
    /* access modifiers changed from: private */
    public final int messageId;
    private final String reaction;
    /* access modifiers changed from: private */
    public boolean started;
    /* access modifiers changed from: private */
    public boolean wasScrolled;
    /* access modifiers changed from: private */
    public WindowManager windowManager;
    FrameLayout windowView;

    static /* synthetic */ float access$116(ReactionsEffectOverlay reactionsEffectOverlay, float f) {
        float f2 = reactionsEffectOverlay.dismissProgress + f;
        reactionsEffectOverlay.dismissProgress = f2;
        return f2;
    }

    private ReactionsEffectOverlay(Context context, BaseFragment baseFragment, ReactionsContainerLayout reactionsContainerLayout, ChatMessageCell chatMessageCell, float f, float f2, String str, int i) {
        float f3;
        final float f4;
        float f5;
        Context context2 = context;
        BaseFragment baseFragment2 = baseFragment;
        ReactionsContainerLayout reactionsContainerLayout2 = reactionsContainerLayout;
        ChatMessageCell chatMessageCell2 = chatMessageCell;
        String str2 = str;
        ChatActivity chatActivity = null;
        this.holderView = null;
        this.messageId = chatMessageCell.getMessageObject().getId();
        this.groupId = chatMessageCell.getMessageObject().getGroupId();
        this.reaction = str2;
        ReactionsLayoutInBubble.ReactionButton reactionButton = chatMessageCell2.getReactionButton(str2);
        final ChatActivity chatActivity2 = baseFragment2 instanceof ChatActivity ? (ChatActivity) baseFragment2 : chatActivity;
        if (reactionsContainerLayout2 != null) {
            int i2 = 0;
            while (true) {
                if (i2 >= reactionsContainerLayout2.recyclerListView.getChildCount()) {
                    break;
                } else if (((ReactionsContainerLayout.ReactionHolderView) reactionsContainerLayout2.recyclerListView.getChildAt(i2)).currentReaction.reaction.equals(str2)) {
                    this.holderView = (ReactionsContainerLayout.ReactionHolderView) reactionsContainerLayout2.recyclerListView.getChildAt(i2);
                    break;
                } else {
                    i2++;
                }
            }
        }
        ReactionsContainerLayout.ReactionHolderView reactionHolderView = this.holderView;
        final boolean z = (reactionHolderView == null && (f == 0.0f || f2 == 0.0f)) ? false : true;
        if (reactionHolderView != null) {
            reactionsContainerLayout2.getLocationOnScreen(this.loc);
            float x = ((float) this.loc[0]) + this.holderView.getX() + this.holderView.backupImageView.getX() + ((float) AndroidUtilities.dp(16.0f));
            float y = ((float) this.loc[1]) + this.holderView.getY() + this.holderView.backupImageView.getY() + ((float) AndroidUtilities.dp(16.0f));
            f5 = (float) this.holderView.backupImageView.getWidth();
            f4 = x;
            f3 = y;
        } else if (reactionButton != null) {
            chatMessageCell2.getLocationInWindow(this.loc);
            float imageX = ((float) (this.loc[0] + chatMessageCell2.reactionsLayoutInBubble.x + reactionButton.x)) + reactionButton.imageReceiver.getImageX();
            float imageY = ((float) (this.loc[1] + chatMessageCell2.reactionsLayoutInBubble.y + reactionButton.y)) + reactionButton.imageReceiver.getImageY();
            float imageHeight = reactionButton.imageReceiver.getImageHeight();
            reactionButton.imageReceiver.getImageWidth();
            f4 = imageX;
            f3 = imageY;
            f5 = imageHeight;
        } else {
            ((View) chatMessageCell.getParent()).getLocationInWindow(this.loc);
            int[] iArr = this.loc;
            f3 = ((float) iArr[1]) + f2;
            f4 = ((float) iArr[0]) + f;
            f5 = 0.0f;
        }
        int dp = AndroidUtilities.dp(350.0f);
        Point point = AndroidUtilities.displaySize;
        int round = Math.round(((float) Math.min(dp, Math.min(point.x, point.y))) * 0.8f);
        int i3 = (int) ((((float) round) * 2.0f) / AndroidUtilities.density);
        int i4 = round >> 1;
        float f6 = f5 / ((float) i4);
        this.animateInProgress = 0.0f;
        this.animateOutProgress = 0.0f;
        FrameLayout frameLayout = new FrameLayout(context2);
        this.container = frameLayout;
        AnonymousClass1 r14 = r0;
        int i5 = i3 >> 1;
        final BaseFragment baseFragment3 = baseFragment;
        FrameLayout frameLayout2 = frameLayout;
        final ChatMessageCell chatMessageCell3 = chatMessageCell;
        int i6 = i4;
        final String str3 = str;
        int i7 = i3;
        final int i8 = i6;
        int i9 = round;
        final float f7 = f6;
        final float f8 = f3;
        AnonymousClass1 r0 = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                ChatMessageCell chatMessageCell;
                float f;
                float f2;
                float f3;
                float f4;
                int i;
                float f5;
                if (ReactionsEffectOverlay.this.dismissed) {
                    if (ReactionsEffectOverlay.this.dismissProgress != 1.0f) {
                        ReactionsEffectOverlay.access$116(ReactionsEffectOverlay.this, 0.10666667f);
                        if (ReactionsEffectOverlay.this.dismissProgress > 1.0f) {
                            float unused = ReactionsEffectOverlay.this.dismissProgress = 1.0f;
                            AndroidUtilities.runOnUIThread(new ReactionsEffectOverlay$1$$ExternalSyntheticLambda1(this));
                        }
                    }
                    if (ReactionsEffectOverlay.this.dismissProgress != 1.0f) {
                        setAlpha(1.0f - ReactionsEffectOverlay.this.dismissProgress);
                        super.dispatchDraw(canvas);
                    }
                    invalidate();
                } else if (!ReactionsEffectOverlay.this.started) {
                    invalidate();
                } else {
                    float f6 = 0.0f;
                    if (ReactionsEffectOverlay.this.holderView != null) {
                        ReactionsEffectOverlay.this.holderView.backupImageView.setAlpha(0.0f);
                    }
                    BaseFragment baseFragment = baseFragment3;
                    if (baseFragment instanceof ChatActivity) {
                        chatMessageCell = ((ChatActivity) baseFragment).findMessageCell(ReactionsEffectOverlay.this.messageId);
                    } else {
                        chatMessageCell = chatMessageCell3;
                    }
                    if (chatMessageCell != null) {
                        chatMessageCell3.getLocationInWindow(ReactionsEffectOverlay.this.loc);
                        ReactionsLayoutInBubble.ReactionButton reactionButton = chatMessageCell3.getReactionButton(str3);
                        int[] iArr = ReactionsEffectOverlay.this.loc;
                        int i2 = iArr[0];
                        ReactionsLayoutInBubble reactionsLayoutInBubble = chatMessageCell3.reactionsLayoutInBubble;
                        f = (float) (i2 + reactionsLayoutInBubble.x);
                        f2 = (float) (iArr[1] + reactionsLayoutInBubble.y);
                        if (reactionButton != null) {
                            f += ((float) reactionButton.x) + reactionButton.imageReceiver.getImageX();
                            f2 += ((float) reactionButton.y) + reactionButton.imageReceiver.getImageY();
                        }
                        ChatActivity chatActivity = chatActivity2;
                        if (chatActivity != null) {
                            f2 += chatActivity.drawingChatLisViewYoffset;
                        }
                        float unused2 = ReactionsEffectOverlay.this.lastDrawnToX = f;
                        float unused3 = ReactionsEffectOverlay.this.lastDrawnToY = f2;
                    } else {
                        f = ReactionsEffectOverlay.this.lastDrawnToX;
                        f2 = ReactionsEffectOverlay.this.lastDrawnToY;
                    }
                    int i3 = i8;
                    float f7 = f - (((float) i3) / 2.0f);
                    float f8 = f2 - (((float) i3) / 2.0f);
                    if (baseFragment3.getParentActivity() != null && baseFragment3.getFragmentView().getParent() != null && baseFragment3.getFragmentView().getVisibility() == 0 && baseFragment3.getFragmentView() != null) {
                        baseFragment3.getFragmentView().getLocationOnScreen(ReactionsEffectOverlay.this.loc);
                        setAlpha(((View) baseFragment3.getFragmentView().getParent()).getAlpha());
                        int[] iArr2 = ReactionsEffectOverlay.this.loc;
                        if (f7 < ((float) iArr2[0])) {
                            f7 = (float) iArr2[0];
                        }
                        if (((float) i8) + f7 > ((float) (iArr2[0] + getMeasuredWidth()))) {
                            f7 = (float) ((ReactionsEffectOverlay.this.loc[0] + getMeasuredWidth()) - i8);
                        }
                        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
                        float interpolation = cubicBezierInterpolator.getInterpolation(ReactionsEffectOverlay.this.animateOutProgress);
                        if (z) {
                            f3 = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(ReactionsEffectOverlay.this.animateInProgress);
                            f4 = cubicBezierInterpolator.getInterpolation(ReactionsEffectOverlay.this.animateInProgress);
                        } else {
                            f3 = ReactionsEffectOverlay.this.animateInProgress;
                            f4 = f3;
                        }
                        float f9 = 1.0f - f3;
                        float var_ = (f7 * f9) + f3;
                        if (chatMessageCell3.getMessageObject().shouldDrawReactionsInLayout()) {
                            f5 = (float) AndroidUtilities.dp(20.0f);
                            i = i8;
                        } else {
                            f5 = (float) AndroidUtilities.dp(14.0f);
                            i = i8;
                        }
                        float var_ = f5 / ((float) i);
                        float var_ = (f4 * f9) + (f7 * f3);
                        float var_ = (f8 * (1.0f - f4)) + (f8 * f4);
                        ReactionsEffectOverlay.this.effectImageView.setTranslationX(var_);
                        ReactionsEffectOverlay.this.effectImageView.setTranslationY(var_);
                        float var_ = 1.0f - interpolation;
                        ReactionsEffectOverlay.this.effectImageView.setAlpha(var_);
                        ReactionsEffectOverlay.this.effectImageView.setScaleX(var_);
                        ReactionsEffectOverlay.this.effectImageView.setScaleY(var_);
                        if (interpolation != 0.0f) {
                            var_ = (var_ * var_) + (var_ * interpolation);
                            var_ = (var_ * var_) + (f * interpolation);
                            var_ = (var_ * var_) + (f2 * interpolation);
                        }
                        AnimationView access$800 = ReactionsEffectOverlay.this.emojiStaticImageView;
                        if (interpolation > 0.7f) {
                            f6 = (interpolation - 0.7f) / 0.3f;
                        }
                        access$800.setAlpha(f6);
                        ReactionsEffectOverlay.this.container.setTranslationX(var_);
                        ReactionsEffectOverlay.this.container.setTranslationY(var_);
                        ReactionsEffectOverlay.this.container.setScaleX(var_);
                        ReactionsEffectOverlay.this.container.setScaleY(var_);
                        super.dispatchDraw(canvas);
                        if (ReactionsEffectOverlay.this.emojiImageView.wasPlaying) {
                            ReactionsEffectOverlay reactionsEffectOverlay = ReactionsEffectOverlay.this;
                            float var_ = reactionsEffectOverlay.animateInProgress;
                            if (var_ != 1.0f) {
                                if (z) {
                                    reactionsEffectOverlay.animateInProgress = var_ + 0.045714285f;
                                } else {
                                    reactionsEffectOverlay.animateInProgress = var_ + 0.07272727f;
                                }
                                if (reactionsEffectOverlay.animateInProgress > 1.0f) {
                                    reactionsEffectOverlay.animateInProgress = 1.0f;
                                }
                            }
                        }
                        if (ReactionsEffectOverlay.this.wasScrolled || (ReactionsEffectOverlay.this.emojiImageView.wasPlaying && ReactionsEffectOverlay.this.emojiImageView.getImageReceiver().getLottieAnimation() != null && !ReactionsEffectOverlay.this.emojiImageView.getImageReceiver().getLottieAnimation().isRunning())) {
                            ReactionsEffectOverlay reactionsEffectOverlay2 = ReactionsEffectOverlay.this;
                            float var_ = reactionsEffectOverlay2.animateOutProgress;
                            if (var_ != 1.0f) {
                                float var_ = var_ + 0.07272727f;
                                reactionsEffectOverlay2.animateOutProgress = var_;
                                if (var_ > 1.0f) {
                                    reactionsEffectOverlay2.animateOutProgress = 1.0f;
                                    ReactionsEffectOverlay.currentOverlay = null;
                                    chatMessageCell3.invalidate();
                                    if (!(chatMessageCell3.getCurrentMessagesGroup() == null || chatMessageCell3.getParent() == null)) {
                                        ((View) chatMessageCell3.getParent()).invalidate();
                                    }
                                    AndroidUtilities.runOnUIThread(new ReactionsEffectOverlay$1$$ExternalSyntheticLambda0(this));
                                }
                            }
                        }
                        invalidate();
                    }
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$dispatchDraw$0() {
                try {
                    ReactionsEffectOverlay.this.windowManager.removeView(ReactionsEffectOverlay.this.windowView);
                } catch (Exception unused) {
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$dispatchDraw$1() {
                try {
                    ReactionsEffectOverlay.this.windowManager.removeView(ReactionsEffectOverlay.this.windowView);
                } catch (Exception unused) {
                }
            }
        };
        this.windowView = r14;
        AnimationView animationView = new AnimationView(this, context2);
        this.effectImageView = animationView;
        AnimationView animationView2 = new AnimationView(this, context2);
        this.emojiImageView = animationView2;
        AnimationView animationView3 = new AnimationView(this, context2);
        this.emojiStaticImageView = animationView3;
        TLRPC$TL_availableReaction tLRPC$TL_availableReaction = MediaDataController.getInstance(i).getReactionsMap().get(str2);
        if (tLRPC$TL_availableReaction != null) {
            TLRPC$Document tLRPC$Document = tLRPC$TL_availableReaction.effect_animation;
            ImageReceiver imageReceiver = animationView.getImageReceiver();
            StringBuilder sb = new StringBuilder();
            int i10 = unicPrefix;
            unicPrefix = i10 + 1;
            sb.append(i10);
            sb.append("_");
            sb.append(chatMessageCell.getMessageObject().getId());
            sb.append("_");
            imageReceiver.setUniqKeyPrefix(sb.toString());
            ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$Document);
            StringBuilder sb2 = new StringBuilder();
            int i11 = i7;
            sb2.append(i11);
            sb2.append("_");
            sb2.append(i11);
            sb2.append("_pcache");
            animationView.setImage(forDocument, sb2.toString(), (ImageLocation) null, (String) null, 0, (Object) null);
            animationView.getImageReceiver().setAutoRepeat(0);
            animationView.getImageReceiver().setAllowStartAnimation(false);
            if (animationView.getImageReceiver().getLottieAnimation() != null) {
                animationView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                animationView.getImageReceiver().getLottieAnimation().start();
            }
            TLRPC$Document tLRPC$Document2 = tLRPC$TL_availableReaction.activate_animation;
            ImageReceiver imageReceiver2 = animationView2.getImageReceiver();
            StringBuilder sb3 = new StringBuilder();
            int i12 = unicPrefix;
            unicPrefix = i12 + 1;
            sb3.append(i12);
            sb3.append("_");
            sb3.append(chatMessageCell.getMessageObject().getId());
            sb3.append("_");
            imageReceiver2.setUniqKeyPrefix(sb3.toString());
            ImageLocation forDocument2 = ImageLocation.getForDocument(tLRPC$Document2);
            StringBuilder sb4 = new StringBuilder();
            int i13 = i5;
            sb4.append(i13);
            sb4.append("_");
            sb4.append(i13);
            animationView2.setImage(forDocument2, sb4.toString(), (ImageLocation) null, (String) null, 0, (Object) null);
            animationView2.getImageReceiver().setAutoRepeat(0);
            animationView2.getImageReceiver().setAllowStartAnimation(false);
            if (animationView2.getImageReceiver().getLottieAnimation() != null) {
                animationView2.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                animationView2.getImageReceiver().getLottieAnimation().start();
            }
            int i14 = i6;
            int i15 = i9;
            int i16 = i15 - i14;
            int i17 = i16 >> 1;
            FrameLayout frameLayout3 = frameLayout2;
            frameLayout3.addView(animationView2);
            animationView2.getLayoutParams().width = i14;
            animationView2.getLayoutParams().height = i14;
            ((FrameLayout.LayoutParams) animationView2.getLayoutParams()).topMargin = i17;
            ((FrameLayout.LayoutParams) animationView2.getLayoutParams()).leftMargin = i16;
            animationView3.getImageReceiver().setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.static_icon), "40_40", (Drawable) null, "webp", tLRPC$TL_availableReaction, 1);
            frameLayout3.addView(animationView3);
            animationView3.getLayoutParams().width = i14;
            animationView3.getLayoutParams().height = i14;
            ((FrameLayout.LayoutParams) animationView3.getLayoutParams()).topMargin = i17;
            ((FrameLayout.LayoutParams) animationView3.getLayoutParams()).leftMargin = i16;
            this.windowView.addView(frameLayout3);
            frameLayout3.getLayoutParams().width = i15;
            frameLayout3.getLayoutParams().height = i15;
            int i18 = -i17;
            ((FrameLayout.LayoutParams) frameLayout3.getLayoutParams()).topMargin = i18;
            int i19 = -i16;
            ((FrameLayout.LayoutParams) frameLayout3.getLayoutParams()).leftMargin = i19;
            this.windowView.addView(animationView);
            animationView.getLayoutParams().width = i15;
            animationView.getLayoutParams().height = i15;
            animationView.getLayoutParams().width = i15;
            animationView.getLayoutParams().height = i15;
            ((FrameLayout.LayoutParams) animationView.getLayoutParams()).topMargin = i18;
            ((FrameLayout.LayoutParams) animationView.getLayoutParams()).leftMargin = i19;
            frameLayout3.setPivotX((float) i16);
            frameLayout3.setPivotY((float) i17);
        }
    }

    public static void show(BaseFragment baseFragment, ReactionsContainerLayout reactionsContainerLayout, ChatMessageCell chatMessageCell, float f, float f2, String str, int i) {
        if (chatMessageCell != null && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            ReactionsEffectOverlay reactionsEffectOverlay = new ReactionsEffectOverlay(baseFragment.getParentActivity(), baseFragment, reactionsContainerLayout, chatMessageCell, f, f2, str, i);
            currentOverlay = reactionsEffectOverlay;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.height = -1;
            layoutParams.width = -1;
            layoutParams.type = 1000;
            layoutParams.flags = 65816;
            layoutParams.format = -3;
            WindowManager windowManager2 = baseFragment.getParentActivity().getWindowManager();
            reactionsEffectOverlay.windowManager = windowManager2;
            windowManager2.addView(reactionsEffectOverlay.windowView, layoutParams);
            chatMessageCell.invalidate();
            if (chatMessageCell.getCurrentMessagesGroup() != null && chatMessageCell.getParent() != null) {
                ((View) chatMessageCell.getParent()).invalidate();
            }
        }
    }

    public static void startAnimation() {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            reactionsEffectOverlay.started = true;
        }
    }

    public static void removeCurrent(boolean z) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            if (z) {
                try {
                    reactionsEffectOverlay.windowManager.removeView(reactionsEffectOverlay.windowView);
                } catch (Exception unused) {
                }
            } else {
                reactionsEffectOverlay.dismissed = true;
            }
        }
        currentOverlay = null;
    }

    public static boolean isPlaying(int i, long j, String str) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay == null) {
            return false;
        }
        long j2 = reactionsEffectOverlay.groupId;
        if (((j2 == 0 || j != j2) && i != reactionsEffectOverlay.messageId) || !reactionsEffectOverlay.reaction.equals(str)) {
            return false;
        }
        return true;
    }

    private class AnimationView extends BackupImageView {
        boolean wasPlaying;

        public AnimationView(ReactionsEffectOverlay reactionsEffectOverlay, Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (getImageReceiver().getLottieAnimation() != null && getImageReceiver().getLottieAnimation().isRunning()) {
                this.wasPlaying = true;
            }
            if (!this.wasPlaying && getImageReceiver().getLottieAnimation() != null && !getImageReceiver().getLottieAnimation().isRunning()) {
                getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                getImageReceiver().getLottieAnimation().start();
            }
            super.onDraw(canvas);
        }
    }

    public static void onScrolled(int i) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay != null) {
            reactionsEffectOverlay.lastDrawnToY -= (float) i;
            if (i != 0) {
                reactionsEffectOverlay.wasScrolled = true;
            }
        }
    }
}
