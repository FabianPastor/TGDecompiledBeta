package org.telegram.ui.Components.Reactions;

import android.annotation.SuppressLint;
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
    @SuppressLint({"StaticFieldLeak"})
    public static ReactionsEffectOverlay currentOverlay;
    public static ReactionsEffectOverlay currentShortOverlay;
    private static int uniqPrefix;
    float animateInProgress;
    float animateOutProgress;
    /* access modifiers changed from: private */
    public final int animationType;
    private ChatMessageCell cell;
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
    /* access modifiers changed from: private */
    public boolean finished;
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

    private ReactionsEffectOverlay(Context context, BaseFragment baseFragment, ReactionsContainerLayout reactionsContainerLayout, ChatMessageCell chatMessageCell, float f, float f2, String str, int i, int i2) {
        float f3;
        float f4;
        float f5;
        int round;
        boolean z;
        Context context2 = context;
        BaseFragment baseFragment2 = baseFragment;
        ReactionsContainerLayout reactionsContainerLayout2 = reactionsContainerLayout;
        ChatMessageCell chatMessageCell2 = chatMessageCell;
        String str2 = str;
        int i3 = i2;
        ChatActivity chatActivity = null;
        this.holderView = null;
        this.messageId = chatMessageCell.getMessageObject().getId();
        this.groupId = chatMessageCell.getMessageObject().getGroupId();
        this.reaction = str2;
        this.animationType = i3;
        this.cell = chatMessageCell2;
        ReactionsLayoutInBubble.ReactionButton reactionButton = chatMessageCell2.getReactionButton(str2);
        final ChatActivity chatActivity2 = baseFragment2 instanceof ChatActivity ? (ChatActivity) baseFragment2 : chatActivity;
        if (reactionsContainerLayout2 != null) {
            int i4 = 0;
            while (true) {
                if (i4 >= reactionsContainerLayout2.recyclerListView.getChildCount()) {
                    break;
                } else if (((ReactionsContainerLayout.ReactionHolderView) reactionsContainerLayout2.recyclerListView.getChildAt(i4)).currentReaction.reaction.equals(str2)) {
                    this.holderView = (ReactionsContainerLayout.ReactionHolderView) reactionsContainerLayout2.recyclerListView.getChildAt(i4);
                    break;
                } else {
                    i4++;
                }
            }
        }
        ReactionsContainerLayout.ReactionHolderView reactionHolderView = this.holderView;
        boolean z2 = (reactionHolderView == null && (f == 0.0f || f2 == 0.0f)) ? false : true;
        if (reactionHolderView != null) {
            reactionHolderView.getLocationOnScreen(this.loc);
            float x = ((float) this.loc[0]) + this.holderView.backupImageView.getX();
            float y = ((float) this.loc[1]) + this.holderView.backupImageView.getY();
            f5 = ((float) this.holderView.backupImageView.getWidth()) * this.holderView.getScaleX();
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
        if (i3 == 2) {
            round = AndroidUtilities.dp(34.0f);
        } else if (i3 == 1) {
            round = AndroidUtilities.dp(80.0f);
        } else {
            int dp = AndroidUtilities.dp(350.0f);
            Point point = AndroidUtilities.displaySize;
            round = Math.round(((float) Math.min(dp, Math.min(point.x, point.y))) * 0.8f);
        }
        int i5 = round;
        int i6 = (int) ((((float) i5) * 2.0f) / AndroidUtilities.density);
        int i7 = i5 >> 1;
        float f6 = f5 / ((float) i7);
        this.animateInProgress = 0.0f;
        this.animateOutProgress = 0.0f;
        FrameLayout frameLayout = new FrameLayout(context2);
        this.container = frameLayout;
        int i8 = i7;
        final BaseFragment baseFragment3 = baseFragment;
        int i9 = i6 >> 1;
        AnonymousClass1 r15 = r0;
        final ChatMessageCell chatMessageCell3 = chatMessageCell;
        int i10 = i6;
        final String str3 = str;
        int i11 = i5;
        final int i12 = i8;
        FrameLayout frameLayout2 = frameLayout;
        final int i13 = i2;
        final boolean z3 = z2;
        final float f7 = f6;
        final float f8 = f4;
        final float f9 = f3;
        AnonymousClass1 r0 = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                ChatMessageCell chatMessageCell;
                int i;
                float f;
                float f2;
                float f3;
                float f4;
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
                    float f5 = 0.0f;
                    if (ReactionsEffectOverlay.this.holderView != null) {
                        ReactionsEffectOverlay.this.holderView.backupImageView.setAlpha(0.0f);
                        ReactionsEffectOverlay.this.holderView.pressedBackupImageView.setAlpha(0.0f);
                    }
                    BaseFragment baseFragment = baseFragment3;
                    if (baseFragment instanceof ChatActivity) {
                        chatMessageCell = ((ChatActivity) baseFragment).findMessageCell(ReactionsEffectOverlay.this.messageId);
                    } else {
                        chatMessageCell = chatMessageCell3;
                    }
                    if (chatMessageCell3.getMessageObject().shouldDrawReactionsInLayout()) {
                        i = AndroidUtilities.dp(20.0f);
                    } else {
                        i = AndroidUtilities.dp(14.0f);
                    }
                    float f6 = (float) i;
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
                        if (chatMessageCell.drawPinnedBottom && !chatMessageCell.shouldDrawTimeOnMedia()) {
                            f2 += (float) AndroidUtilities.dp(2.0f);
                        }
                        float unused2 = ReactionsEffectOverlay.this.lastDrawnToX = f;
                        float unused3 = ReactionsEffectOverlay.this.lastDrawnToY = f2;
                    } else {
                        f = ReactionsEffectOverlay.this.lastDrawnToX;
                        f2 = ReactionsEffectOverlay.this.lastDrawnToY;
                    }
                    if (baseFragment3.getParentActivity() != null && baseFragment3.getFragmentView().getParent() != null && baseFragment3.getFragmentView().getVisibility() == 0 && baseFragment3.getFragmentView() != null) {
                        baseFragment3.getFragmentView().getLocationOnScreen(ReactionsEffectOverlay.this.loc);
                        setAlpha(((View) baseFragment3.getFragmentView().getParent()).getAlpha());
                        int i3 = i12;
                        float f7 = f - ((((float) i3) - f6) / 2.0f);
                        float f8 = f2 - ((((float) i3) - f6) / 2.0f);
                        if (i13 != 1) {
                            int[] iArr2 = ReactionsEffectOverlay.this.loc;
                            if (f7 < ((float) iArr2[0])) {
                                f7 = (float) iArr2[0];
                            }
                            if (((float) i3) + f7 > ((float) (iArr2[0] + getMeasuredWidth()))) {
                                f7 = (float) ((ReactionsEffectOverlay.this.loc[0] + getMeasuredWidth()) - i12);
                            }
                        }
                        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
                        float interpolation = cubicBezierInterpolator.getInterpolation(ReactionsEffectOverlay.this.animateOutProgress);
                        if (i13 == 2) {
                            f3 = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(interpolation);
                            f4 = cubicBezierInterpolator.getInterpolation(interpolation);
                        } else if (z3) {
                            f3 = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(ReactionsEffectOverlay.this.animateInProgress);
                            f4 = cubicBezierInterpolator.getInterpolation(ReactionsEffectOverlay.this.animateInProgress);
                        } else {
                            f3 = ReactionsEffectOverlay.this.animateInProgress;
                            f4 = f3;
                        }
                        float f9 = 1.0f - f3;
                        float var_ = (f7 * f9) + f3;
                        float var_ = f6 / ((float) i12);
                        if (i13 == 1) {
                            var_ = 1.0f;
                        } else {
                            f7 = (f7 * f3) + (f8 * f9);
                            f8 = (f8 * f4) + (f9 * (1.0f - f4));
                        }
                        ReactionsEffectOverlay.this.effectImageView.setTranslationX(f7);
                        ReactionsEffectOverlay.this.effectImageView.setTranslationY(f8);
                        float var_ = 1.0f - interpolation;
                        ReactionsEffectOverlay.this.effectImageView.setAlpha(var_);
                        ReactionsEffectOverlay.this.effectImageView.setScaleX(var_);
                        ReactionsEffectOverlay.this.effectImageView.setScaleY(var_);
                        int i4 = i13;
                        if (i4 == 2) {
                            var_ = (f7 * f9) + (var_ * f3);
                            f7 = (f8 * f9) + (f * f3);
                            f8 = (f9 * (1.0f - f4)) + (f2 * f4);
                        } else if (interpolation != 0.0f) {
                            var_ = (var_ * var_) + (var_ * interpolation);
                            f7 = (f7 * var_) + (f * interpolation);
                            f8 = (f8 * var_) + (f2 * interpolation);
                        }
                        if (i4 != 1) {
                            AnimationView access$800 = ReactionsEffectOverlay.this.emojiStaticImageView;
                            if (interpolation > 0.7f) {
                                f5 = (interpolation - 0.7f) / 0.3f;
                            }
                            access$800.setAlpha(f5);
                        }
                        ReactionsEffectOverlay.this.container.setTranslationX(f7);
                        ReactionsEffectOverlay.this.container.setTranslationY(f8);
                        ReactionsEffectOverlay.this.container.setScaleX(var_);
                        ReactionsEffectOverlay.this.container.setScaleY(var_);
                        super.dispatchDraw(canvas);
                        if (i13 == 1 || ReactionsEffectOverlay.this.emojiImageView.wasPlaying) {
                            ReactionsEffectOverlay reactionsEffectOverlay = ReactionsEffectOverlay.this;
                            float var_ = reactionsEffectOverlay.animateInProgress;
                            if (var_ != 1.0f) {
                                if (z3) {
                                    reactionsEffectOverlay.animateInProgress = var_ + 0.045714285f;
                                } else {
                                    reactionsEffectOverlay.animateInProgress = var_ + 0.07272727f;
                                }
                                if (reactionsEffectOverlay.animateInProgress > 1.0f) {
                                    reactionsEffectOverlay.animateInProgress = 1.0f;
                                }
                            }
                        }
                        if (i13 == 2 || ((ReactionsEffectOverlay.this.wasScrolled && i13 == 0) || ((i13 != 1 && ReactionsEffectOverlay.this.emojiImageView.wasPlaying && ReactionsEffectOverlay.this.emojiImageView.getImageReceiver().getLottieAnimation() != null && !ReactionsEffectOverlay.this.emojiImageView.getImageReceiver().getLottieAnimation().isRunning()) || (i13 == 1 && ReactionsEffectOverlay.this.effectImageView.wasPlaying && ReactionsEffectOverlay.this.effectImageView.getImageReceiver().getLottieAnimation() != null && !ReactionsEffectOverlay.this.effectImageView.getImageReceiver().getLottieAnimation().isRunning())))) {
                            ReactionsEffectOverlay reactionsEffectOverlay2 = ReactionsEffectOverlay.this;
                            float var_ = reactionsEffectOverlay2.animateOutProgress;
                            if (var_ != 1.0f) {
                                int i5 = i13;
                                if (i5 == 1) {
                                    reactionsEffectOverlay2.animateOutProgress = 1.0f;
                                } else {
                                    reactionsEffectOverlay2.animateOutProgress = var_ + (16.0f / (i5 == 2 ? 350.0f : 220.0f));
                                }
                                if (reactionsEffectOverlay2.animateOutProgress > 0.7f && !reactionsEffectOverlay2.finished) {
                                    ReactionsEffectOverlay.startShortAnimation();
                                }
                                if (ReactionsEffectOverlay.this.animateOutProgress >= 1.0f) {
                                    int i6 = i13;
                                    if (i6 == 0 || i6 == 2) {
                                        chatMessageCell3.reactionsLayoutInBubble.animateReaction(str3);
                                    }
                                    ReactionsEffectOverlay.this.animateOutProgress = 1.0f;
                                    if (i13 == 1) {
                                        ReactionsEffectOverlay.currentShortOverlay = null;
                                    } else {
                                        ReactionsEffectOverlay.currentOverlay = null;
                                    }
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
        this.windowView = r15;
        AnimationView animationView = new AnimationView(context2);
        this.effectImageView = animationView;
        AnimationView animationView2 = new AnimationView(context2);
        this.emojiImageView = animationView2;
        AnimationView animationView3 = new AnimationView(context2);
        this.emojiStaticImageView = animationView3;
        TLRPC$TL_availableReaction tLRPC$TL_availableReaction = MediaDataController.getInstance(i).getReactionsMap().get(str);
        if (tLRPC$TL_availableReaction != null) {
            int i14 = i2;
            if (i14 != 2) {
                TLRPC$Document tLRPC$Document = i14 == 1 ? tLRPC$TL_availableReaction.around_animation : tLRPC$TL_availableReaction.effect_animation;
                ImageReceiver imageReceiver = animationView.getImageReceiver();
                StringBuilder sb = new StringBuilder();
                int i15 = uniqPrefix;
                uniqPrefix = i15 + 1;
                sb.append(i15);
                sb.append("_");
                sb.append(chatMessageCell.getMessageObject().getId());
                sb.append("_");
                imageReceiver.setUniqKeyPrefix(sb.toString());
                ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$Document);
                StringBuilder sb2 = new StringBuilder();
                int i16 = i10;
                sb2.append(i16);
                sb2.append("_");
                sb2.append(i16);
                sb2.append("_pcache");
                animationView.setImage(forDocument, sb2.toString(), (ImageLocation) null, (String) null, 0, (Object) null);
                z = false;
                animationView.getImageReceiver().setAutoRepeat(0);
                animationView.getImageReceiver().setAllowStartAnimation(false);
                if (animationView.getImageReceiver().getLottieAnimation() != null) {
                    animationView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                    animationView.getImageReceiver().getLottieAnimation().start();
                }
            } else {
                z = false;
            }
            if (i14 == 2) {
                TLRPC$Document tLRPC$Document2 = tLRPC$TL_availableReaction.appear_animation;
                ImageReceiver imageReceiver2 = animationView2.getImageReceiver();
                StringBuilder sb3 = new StringBuilder();
                int i17 = uniqPrefix;
                uniqPrefix = i17 + 1;
                sb3.append(i17);
                sb3.append("_");
                sb3.append(chatMessageCell.getMessageObject().getId());
                sb3.append("_");
                imageReceiver2.setUniqKeyPrefix(sb3.toString());
                ImageLocation forDocument2 = ImageLocation.getForDocument(tLRPC$Document2);
                StringBuilder sb4 = new StringBuilder();
                int i18 = i9;
                sb4.append(i18);
                sb4.append("_");
                sb4.append(i18);
                animationView2.setImage(forDocument2, sb4.toString(), (ImageLocation) null, (String) null, 0, (Object) null);
            } else {
                int i19 = i9;
                if (i14 == 0) {
                    TLRPC$Document tLRPC$Document3 = tLRPC$TL_availableReaction.activate_animation;
                    ImageReceiver imageReceiver3 = animationView2.getImageReceiver();
                    StringBuilder sb5 = new StringBuilder();
                    int i20 = uniqPrefix;
                    uniqPrefix = i20 + 1;
                    sb5.append(i20);
                    sb5.append("_");
                    sb5.append(chatMessageCell.getMessageObject().getId());
                    sb5.append("_");
                    imageReceiver3.setUniqKeyPrefix(sb5.toString());
                    ImageLocation forDocument3 = ImageLocation.getForDocument(tLRPC$Document3);
                    animationView2.setImage(forDocument3, i19 + "_" + i19, (ImageLocation) null, (String) null, 0, (Object) null);
                }
            }
            animationView2.getImageReceiver().setAutoRepeat(z ? 1 : 0);
            animationView2.getImageReceiver().setAllowStartAnimation(z);
            if (animationView2.getImageReceiver().getLottieAnimation() != null) {
                if (i14 == 2) {
                    animationView2.getImageReceiver().getLottieAnimation().setCurrentFrame(animationView2.getImageReceiver().getLottieAnimation().getFramesCount() - 1, z);
                } else {
                    animationView2.getImageReceiver().getLottieAnimation().setCurrentFrame(z, z);
                    animationView2.getImageReceiver().getLottieAnimation().start();
                }
            }
            int i21 = i8;
            int i22 = i11;
            int i23 = i22 - i21;
            int i24 = i23 >> 1;
            i23 = i14 == 1 ? i24 : i23;
            FrameLayout frameLayout3 = frameLayout2;
            frameLayout3.addView(animationView2);
            animationView2.getLayoutParams().width = i21;
            animationView2.getLayoutParams().height = i21;
            ((FrameLayout.LayoutParams) animationView2.getLayoutParams()).topMargin = i24;
            ((FrameLayout.LayoutParams) animationView2.getLayoutParams()).leftMargin = i23;
            if (i14 != 1) {
                animationView3.getImageReceiver().setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.static_icon), "40_40", (Drawable) null, "webp", tLRPC$TL_availableReaction, 1);
            }
            frameLayout3.addView(animationView3);
            animationView3.getLayoutParams().width = i21;
            animationView3.getLayoutParams().height = i21;
            ((FrameLayout.LayoutParams) animationView3.getLayoutParams()).topMargin = i24;
            ((FrameLayout.LayoutParams) animationView3.getLayoutParams()).leftMargin = i23;
            this.windowView.addView(frameLayout3);
            frameLayout3.getLayoutParams().width = i22;
            frameLayout3.getLayoutParams().height = i22;
            int i25 = -i24;
            ((FrameLayout.LayoutParams) frameLayout3.getLayoutParams()).topMargin = i25;
            int i26 = -i23;
            ((FrameLayout.LayoutParams) frameLayout3.getLayoutParams()).leftMargin = i26;
            this.windowView.addView(animationView);
            animationView.getLayoutParams().width = i22;
            animationView.getLayoutParams().height = i22;
            animationView.getLayoutParams().width = i22;
            animationView.getLayoutParams().height = i22;
            ((FrameLayout.LayoutParams) animationView.getLayoutParams()).topMargin = i25;
            ((FrameLayout.LayoutParams) animationView.getLayoutParams()).leftMargin = i26;
            frameLayout3.setPivotX((float) i23);
            frameLayout3.setPivotY((float) i24);
        }
    }

    public static void show(BaseFragment baseFragment, ReactionsContainerLayout reactionsContainerLayout, ChatMessageCell chatMessageCell, float f, float f2, String str, int i, int i2) {
        int i3 = i2;
        if (chatMessageCell != null && MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            if (i3 == 2 || i3 == 0) {
                show(baseFragment, (ReactionsContainerLayout) null, chatMessageCell, 0.0f, 0.0f, str, i, 1);
            }
            ReactionsEffectOverlay reactionsEffectOverlay = new ReactionsEffectOverlay(baseFragment.getParentActivity(), baseFragment, reactionsContainerLayout, chatMessageCell, f, f2, str, i, i2);
            if (i3 == 1) {
                currentShortOverlay = reactionsEffectOverlay;
            } else {
                currentOverlay = reactionsEffectOverlay;
            }
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
            return;
        }
        startShortAnimation();
        ReactionsEffectOverlay reactionsEffectOverlay2 = currentShortOverlay;
        if (reactionsEffectOverlay2 != null) {
            reactionsEffectOverlay2.cell.reactionsLayoutInBubble.animateReaction(reactionsEffectOverlay2.reaction);
        }
    }

    public static void startShortAnimation() {
        ReactionsEffectOverlay reactionsEffectOverlay = currentShortOverlay;
        if (reactionsEffectOverlay != null && !reactionsEffectOverlay.started) {
            reactionsEffectOverlay.started = true;
            if (reactionsEffectOverlay.animationType == 1) {
                reactionsEffectOverlay.cell.performHapticFeedback(3);
            }
        }
    }

    public static void removeCurrent(boolean z) {
        int i = 0;
        while (i < 2) {
            ReactionsEffectOverlay reactionsEffectOverlay = i == 0 ? currentOverlay : currentShortOverlay;
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
            i++;
        }
        currentShortOverlay = null;
        currentOverlay = null;
    }

    public static boolean isPlaying(int i, long j, String str) {
        ReactionsEffectOverlay reactionsEffectOverlay = currentOverlay;
        if (reactionsEffectOverlay == null) {
            return false;
        }
        int i2 = reactionsEffectOverlay.animationType;
        if (i2 != 2 && i2 != 0) {
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

        public AnimationView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (getImageReceiver().getLottieAnimation() != null && getImageReceiver().getLottieAnimation().isRunning()) {
                this.wasPlaying = true;
            }
            if (!this.wasPlaying && getImageReceiver().getLottieAnimation() != null && !getImageReceiver().getLottieAnimation().isRunning()) {
                if (ReactionsEffectOverlay.this.animationType == 2) {
                    getImageReceiver().getLottieAnimation().setCurrentFrame(getImageReceiver().getLottieAnimation().getFramesCount() - 1, false);
                } else {
                    getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                    getImageReceiver().getLottieAnimation().start();
                }
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
