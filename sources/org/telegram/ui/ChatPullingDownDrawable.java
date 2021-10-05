package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CounterView;
import org.telegram.ui.Components.CubicBezierInterpolator;

public class ChatPullingDownDrawable implements NotificationCenter.NotificationCenterDelegate {
    boolean animateCheck;
    public boolean animateSwipeToRelease;
    Paint arrowPaint = new Paint(1);
    float bounceProgress;
    StaticLayout chatNameLayout;
    int chatNameWidth;
    float checkProgress;
    float circleRadius;
    CounterView.CounterDrawable counterDrawable = new CounterView.CounterDrawable((View) null, (Theme.ResourcesProvider) null);
    private final int currentAccount;
    private final long currentDialog;
    public int dialogFilterId;
    public int dialogFolderId;
    boolean drawFolderBackground;
    boolean emptyStub;
    private final int filterId;
    private final int folderId;
    /* access modifiers changed from: private */
    public final View fragmentView;
    ImageReceiver imageReceiver = new ImageReceiver();
    long lastHapticTime;
    float lastProgress;
    public long lastShowingReleaseTime;
    int lastWidth;
    StaticLayout layout1;
    int layout1Width;
    StaticLayout layout2;
    int layout2Width;
    TLRPC$Chat nextChat;
    public long nextDialogId;
    Runnable onAnimationFinishRunnable;
    int[] params = new int[3];
    View parentView;
    Path path = new Path();
    float progressToBottomPannel;
    private final Theme.ResourcesProvider resourcesProvider;
    boolean showBottomPanel;
    AnimatorSet showReleaseAnimator;
    float swipeToReleaseProgress;
    TextPaint textPaint = new TextPaint(1);
    TextPaint textPaint2 = new TextPaint(1);
    private Paint xRefPaint = new Paint(1);

    public ChatPullingDownDrawable(int i, View view, long j, int i2, int i3, Theme.ResourcesProvider resourcesProvider2) {
        this.fragmentView = view;
        this.currentAccount = i;
        this.currentDialog = j;
        this.folderId = i2;
        this.filterId = i3;
        this.resourcesProvider = resourcesProvider2;
        this.arrowPaint.setStrokeWidth(AndroidUtilities.dpf2(2.8f));
        this.arrowPaint.setStrokeCap(Paint.Cap.ROUND);
        CounterView.CounterDrawable counterDrawable2 = this.counterDrawable;
        counterDrawable2.gravity = 3;
        counterDrawable2.setType(1);
        CounterView.CounterDrawable counterDrawable3 = this.counterDrawable;
        counterDrawable3.addServiceGradient = true;
        counterDrawable3.circlePaint = getThemedPaint("paintChatActionBackground");
        CounterView.CounterDrawable counterDrawable4 = this.counterDrawable;
        TextPaint textPaint3 = this.textPaint;
        counterDrawable4.textPaint = textPaint3;
        textPaint3.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint2.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.xRefPaint.setColor(-16777216);
        this.xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        updateDialog();
    }

    public void updateDialog() {
        TLRPC$Dialog nextUnreadDialog = getNextUnreadDialog(this.currentDialog, this.folderId, this.filterId, true, this.params);
        if (nextUnreadDialog != null) {
            this.nextDialogId = nextUnreadDialog.id;
            int[] iArr = this.params;
            this.drawFolderBackground = iArr[0] == 1;
            this.dialogFolderId = iArr[1];
            this.dialogFilterId = iArr[2];
            this.emptyStub = false;
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-nextUnreadDialog.id));
            this.nextChat = chat;
            if (chat == null) {
                MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(nextUnreadDialog.id));
            }
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(this.nextChat);
            this.imageReceiver.setImage(ImageLocation.getForChat(this.nextChat, 1), "50_50", avatarDrawable, (String) null, UserConfig.getInstance(0).getCurrentUser(), 0);
            MessagesController.getInstance(this.currentAccount).ensureMessagesLoaded(nextUnreadDialog.id, 0, (MessagesController.MessagesLoadedCallback) null);
            this.counterDrawable.setCount(nextUnreadDialog.unread_count, false);
            return;
        }
        this.nextChat = null;
        this.drawFolderBackground = false;
        this.emptyStub = true;
    }

    public void setWidth(int i) {
        String str;
        String str2;
        int i2;
        int i3 = i;
        if (i3 != this.lastWidth) {
            this.circleRadius = ((float) AndroidUtilities.dp(56.0f)) / 2.0f;
            this.lastWidth = i3;
            TLRPC$Chat tLRPC$Chat = this.nextChat;
            String string = tLRPC$Chat != null ? tLRPC$Chat.title : LocaleController.getString("SwipeToGoNextChannelEnd", NUM);
            int measureText = (int) this.textPaint.measureText(string);
            this.chatNameWidth = measureText;
            this.chatNameWidth = Math.min(measureText, this.lastWidth - AndroidUtilities.dp(60.0f));
            this.chatNameLayout = new StaticLayout(string, this.textPaint, this.chatNameWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            boolean z = this.drawFolderBackground;
            if (z && (i2 = this.dialogFolderId) != this.folderId && i2 != 0) {
                str2 = LocaleController.getString("SwipeToGoNextArchive", NUM);
                str = LocaleController.getString("ReleaseToGoNextArchive", NUM);
            } else if (z) {
                str2 = LocaleController.getString("SwipeToGoNextFolder", NUM);
                str = LocaleController.getString("ReleaseToGoNextFolder", NUM);
            } else {
                str2 = LocaleController.getString("SwipeToGoNextChannel", NUM);
                str = LocaleController.getString("ReleaseToGoNextChannel", NUM);
            }
            String str3 = str2;
            String str4 = str;
            int measureText2 = (int) this.textPaint2.measureText(str3);
            this.layout1Width = measureText2;
            this.layout1Width = Math.min(measureText2, this.lastWidth - AndroidUtilities.dp(60.0f));
            this.layout1 = new StaticLayout(str3, this.textPaint2, this.layout1Width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            int measureText3 = (int) this.textPaint2.measureText(str4);
            this.layout2Width = measureText3;
            this.layout2Width = Math.min(measureText3, this.lastWidth - AndroidUtilities.dp(60.0f));
            this.layout2 = new StaticLayout(str4, this.textPaint2, this.layout2Width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.imageReceiver.setImageCoords((((float) this.lastWidth) / 2.0f) - (((float) AndroidUtilities.dp(40.0f)) / 2.0f), (((float) AndroidUtilities.dp(12.0f)) + this.circleRadius) - (((float) AndroidUtilities.dp(40.0f)) / 2.0f), (float) AndroidUtilities.dp(40.0f), (float) AndroidUtilities.dp(40.0f));
            this.imageReceiver.setRoundRadius((int) (((float) AndroidUtilities.dp(40.0f)) / 2.0f));
            this.counterDrawable.setSize(AndroidUtilities.dp(28.0f), AndroidUtilities.dp(100.0f));
        }
    }

    public void draw(Canvas canvas, View view, float f, float f2) {
        int i;
        float f3;
        int i2;
        int i3;
        float f4;
        Canvas canvas2 = canvas;
        View view2 = view;
        float f5 = f;
        this.parentView = view2;
        this.counterDrawable.setParent(view2);
        float dp = ((float) AndroidUtilities.dp(110.0f)) * f5;
        if (dp >= ((float) AndroidUtilities.dp(8.0f))) {
            float f6 = f5 < 0.2f ? 5.0f * f5 * f2 : f2;
            Theme.applyServiceShaderMatrix(this.lastWidth, view.getMeasuredHeight(), 0.0f, ((float) view.getMeasuredHeight()) - dp);
            this.textPaint.setColor(getThemedColor("chat_serviceText"));
            this.arrowPaint.setColor(getThemedColor("chat_serviceText"));
            this.textPaint2.setColor(getThemedColor("chat_messagePanelHint"));
            int alpha = getThemedPaint("paintChatActionBackground").getAlpha();
            int alpha2 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
            int alpha3 = this.textPaint.getAlpha();
            int alpha4 = this.arrowPaint.getAlpha();
            Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (((float) alpha2) * f6));
            int i4 = (int) (((float) alpha) * f6);
            getThemedPaint("paintChatActionBackground").setAlpha(i4);
            int i5 = (int) (((float) alpha3) * f6);
            this.textPaint.setAlpha(i5);
            this.imageReceiver.setAlpha(f6);
            if ((f5 < 1.0f || this.lastProgress >= 1.0f) && (f5 >= 1.0f || this.lastProgress != 1.0f)) {
                i = alpha;
            } else {
                long currentTimeMillis = System.currentTimeMillis();
                i = alpha;
                if (currentTimeMillis - this.lastHapticTime > 100) {
                    view2.performHapticFeedback(3, 2);
                    this.lastHapticTime = currentTimeMillis;
                }
                this.lastProgress = f5;
            }
            if (f5 == 1.0f && !this.animateSwipeToRelease) {
                this.animateSwipeToRelease = true;
                this.animateCheck = true;
                showReleaseState(true, view2);
                this.lastShowingReleaseTime = System.currentTimeMillis();
            } else if (f5 != 1.0f && this.animateSwipeToRelease) {
                this.animateSwipeToRelease = false;
                showReleaseState(false, view2);
            }
            float f7 = ((float) this.lastWidth) / 2.0f;
            float f8 = this.bounceProgress * ((float) (-AndroidUtilities.dp(4.0f)));
            if (this.emptyStub) {
                dp -= f8;
            }
            float f9 = dp / 2.0f;
            float max = Math.max(0.0f, Math.min(this.circleRadius, (f9 - (((float) AndroidUtilities.dp(16.0f)) * f5)) - ((float) AndroidUtilities.dp(4.0f))));
            float max2 = ((Math.max(0.0f, Math.min(this.circleRadius * f5, f9 - (((float) AndroidUtilities.dp(8.0f)) * f5))) * 2.0f) - ((float) AndroidUtilities.dp2(16.0f))) * (1.0f - this.swipeToReleaseProgress);
            float var_ = this.swipeToReleaseProgress;
            float dp2 = max2 + (((float) AndroidUtilities.dp(56.0f)) * var_);
            if (var_ < 1.0f || this.emptyStub) {
                float var_ = -dp;
                i2 = alpha3;
                i3 = alpha2;
                float dp3 = (((float) (-AndroidUtilities.dp(8.0f))) * (1.0f - this.swipeToReleaseProgress)) + ((((float) AndroidUtilities.dp(56.0f)) + var_) * this.swipeToReleaseProgress);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(f7 - max, var_, max + f7, dp3);
                if (this.swipeToReleaseProgress > 0.0f && !this.emptyStub) {
                    float dp4 = ((float) AndroidUtilities.dp(16.0f)) * this.swipeToReleaseProgress;
                    rectF.inset(dp4, dp4);
                }
                drawBackground(canvas2, rectF);
                float dp5 = ((((float) AndroidUtilities.dp(24.0f)) + var_) + (((float) AndroidUtilities.dp(8.0f)) * (1.0f - f5))) - (((float) AndroidUtilities.dp(36.0f)) * this.swipeToReleaseProgress);
                canvas.save();
                f3 = dp;
                rectF.inset((float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f));
                canvas2.clipRect(rectF);
                float var_ = this.swipeToReleaseProgress;
                if (var_ > 0.0f) {
                    this.arrowPaint.setAlpha((int) ((1.0f - var_) * 255.0f));
                }
                drawArrow(canvas2, f7, dp5, ((float) AndroidUtilities.dp(24.0f)) * f5);
                if (this.emptyStub) {
                    float dp22 = (((((float) (-AndroidUtilities.dp(8.0f))) - (((float) AndroidUtilities.dp2(8.0f)) * f5)) - dp2) * (1.0f - this.swipeToReleaseProgress)) + ((var_ - ((float) AndroidUtilities.dp(2.0f))) * this.swipeToReleaseProgress) + f8;
                    this.arrowPaint.setAlpha(alpha4);
                    canvas.save();
                    canvas2.scale(f5, f5, f7, ((float) AndroidUtilities.dp(28.0f)) + dp22);
                    drawCheck(canvas2, f7, dp22 + ((float) AndroidUtilities.dp(28.0f)));
                    canvas.restore();
                }
                canvas.restore();
            } else {
                f3 = dp;
                i3 = alpha2;
                i2 = alpha3;
            }
            if (this.chatNameLayout != null && this.swipeToReleaseProgress > 0.0f) {
                getThemedPaint("paintChatActionBackground").setAlpha(i4);
                this.textPaint.setAlpha(i5);
                float dp6 = ((((float) AndroidUtilities.dp(20.0f)) * (1.0f - this.swipeToReleaseProgress)) - (((float) AndroidUtilities.dp(36.0f)) * this.swipeToReleaseProgress)) + f8;
                RectF rectF2 = AndroidUtilities.rectTmp;
                int i6 = this.lastWidth;
                int i7 = this.chatNameWidth;
                rectF2.set(((float) (i6 - i7)) / 2.0f, dp6, ((float) i6) - (((float) (i6 - i7)) / 2.0f), ((float) this.chatNameLayout.getHeight()) + dp6);
                rectF2.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(4.0f)));
                canvas2.drawRoundRect(rectF2, (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), getThemedPaint("paintChatActionBackground"));
                if (hasGradientService()) {
                    canvas2.drawRoundRect(rectF2, (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
                }
                canvas.save();
                canvas2.translate(((float) (this.lastWidth - this.chatNameWidth)) / 2.0f, dp6);
                this.chatNameLayout.draw(canvas2);
                canvas.restore();
            }
            if (this.emptyStub || dp2 <= 0.0f) {
                f4 = 1.0f;
            } else {
                float dp23 = (((((float) (-AndroidUtilities.dp(8.0f))) - (((float) AndroidUtilities.dp2(8.0f)) * f5)) - dp2) * (1.0f - this.swipeToReleaseProgress)) + (((-f3) + ((float) AndroidUtilities.dp(4.0f))) * this.swipeToReleaseProgress) + f8;
                float var_ = dp2 / 2.0f;
                this.imageReceiver.setRoundRadius((int) var_);
                this.imageReceiver.setImageCoords(f7 - var_, dp23, dp2, dp2);
                if (this.swipeToReleaseProgress > 0.0f) {
                    f4 = 1.0f;
                    canvas.saveLayerAlpha(this.imageReceiver.getImageX(), this.imageReceiver.getImageY(), this.imageReceiver.getImageWidth() + this.imageReceiver.getImageX(), this.imageReceiver.getImageHeight() + this.imageReceiver.getImageY(), 255, 31);
                    this.imageReceiver.draw(canvas2);
                    float var_ = this.swipeToReleaseProgress;
                    canvas2.scale(var_, var_, ((float) AndroidUtilities.dp(12.0f)) + f7 + this.counterDrawable.getCenterX(), (dp23 - ((float) AndroidUtilities.dp(6.0f))) + ((float) AndroidUtilities.dp(14.0f)));
                    canvas2.translate(((float) AndroidUtilities.dp(12.0f)) + f7, dp23 - ((float) AndroidUtilities.dp(6.0f)));
                    this.counterDrawable.updateBackgroundRect();
                    this.counterDrawable.rectF.inset((float) (-AndroidUtilities.dp(2.0f)), (float) (-AndroidUtilities.dp(2.0f)));
                    RectF rectF3 = this.counterDrawable.rectF;
                    canvas2.drawRoundRect(rectF3, rectF3.height() / 2.0f, this.counterDrawable.rectF.height() / 2.0f, this.xRefPaint);
                    canvas.restore();
                    canvas.save();
                    float var_ = this.swipeToReleaseProgress;
                    canvas2.scale(var_, var_, ((float) AndroidUtilities.dp(12.0f)) + f7 + this.counterDrawable.getCenterX(), (dp23 - ((float) AndroidUtilities.dp(6.0f))) + ((float) AndroidUtilities.dp(14.0f)));
                    canvas2.translate(f7 + ((float) AndroidUtilities.dp(12.0f)), dp23 - ((float) AndroidUtilities.dp(6.0f)));
                    this.counterDrawable.draw(canvas2);
                    canvas.restore();
                } else {
                    f4 = 1.0f;
                    this.imageReceiver.draw(canvas2);
                }
            }
            getThemedPaint("paintChatActionBackground").setAlpha(i);
            Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(i3);
            this.textPaint.setAlpha(i2);
            this.arrowPaint.setAlpha(alpha4);
            this.imageReceiver.setAlpha(f4);
        }
    }

    private void drawCheck(Canvas canvas, float f, float f2) {
        Canvas canvas2 = canvas;
        if (this.animateCheck) {
            float f3 = this.checkProgress;
            if (f3 < 1.0f) {
                float f4 = f3 + 0.07272727f;
                this.checkProgress = f4;
                if (f4 > 1.0f) {
                    this.checkProgress = 1.0f;
                }
            }
            float f5 = this.checkProgress;
            float f6 = f5 > 0.5f ? 1.0f : f5 / 0.5f;
            float f7 = f5 < 0.5f ? 0.0f : (f5 - 0.5f) / 0.5f;
            canvas.save();
            canvas2.clipRect(AndroidUtilities.rectTmp);
            canvas2.translate(f - ((float) AndroidUtilities.dp(24.0f)), f2 - ((float) AndroidUtilities.dp(24.0f)));
            float dp = (float) AndroidUtilities.dp(16.0f);
            float dp2 = (float) AndroidUtilities.dp(26.0f);
            float dp3 = (float) AndroidUtilities.dp(22.0f);
            float dp4 = (float) AndroidUtilities.dp(32.0f);
            float dp5 = (float) AndroidUtilities.dp(32.0f);
            float dp6 = (float) AndroidUtilities.dp(20.0f);
            float f8 = 1.0f - f6;
            canvas.drawLine(dp, dp2, (dp * f8) + (dp3 * f6), (f8 * dp2) + (f6 * dp4), this.arrowPaint);
            if (f7 > 0.0f) {
                float f9 = 1.0f - f7;
                canvas.drawLine(dp3, dp4, (dp3 * f9) + (dp5 * f7), (f9 * dp4) + (dp6 * f7), this.arrowPaint);
            }
            canvas.restore();
        }
    }

    private void drawBackground(Canvas canvas, RectF rectF) {
        Canvas canvas2 = canvas;
        RectF rectF2 = rectF;
        if (this.drawFolderBackground) {
            this.path.reset();
            float width = rectF.width() * 0.2f;
            float width2 = rectF.width() * 0.1f;
            float width3 = rectF.width() * 0.03f;
            float f = width2 / 2.0f;
            float height = rectF.height() - width2;
            this.path.moveTo(rectF2.right, rectF2.top + width + width2);
            float f2 = -width;
            this.path.rQuadTo(0.0f, f2, f2, f2);
            float f3 = width * 2.0f;
            float f4 = f * 2.0f;
            this.path.rLineTo((((-(rectF.width() - f3)) / 2.0f) + f4) - width3, 0.0f);
            float f5 = -f;
            float f6 = f5 / 2.0f;
            float f7 = f5 * 2.0f;
            float f8 = (-width2) / 2.0f;
            this.path.rQuadTo(f6, 0.0f, f7, f8);
            this.path.rQuadTo(f6, f8, f7, f8);
            this.path.rLineTo(((-(rectF.width() - f3)) / 2.0f) + f4 + width3, 0.0f);
            this.path.rQuadTo(f2, 0.0f, f2, width);
            this.path.rLineTo(0.0f, (width2 + height) - f3);
            this.path.rQuadTo(0.0f, width, width, width);
            this.path.rLineTo(rectF.width() - f3, 0.0f);
            this.path.rQuadTo(width, 0.0f, width, f2);
            this.path.rLineTo(0.0f, -(height - f3));
            this.path.close();
            canvas2.drawPath(this.path, getThemedPaint("paintChatActionBackground"));
            if (hasGradientService()) {
                canvas2.drawPath(this.path, Theme.chat_actionBackgroundGradientDarkenPaint);
                return;
            }
            return;
        }
        RectF rectF3 = AndroidUtilities.rectTmp;
        float f9 = this.circleRadius;
        canvas2.drawRoundRect(rectF3, f9, f9, getThemedPaint("paintChatActionBackground"));
        if (hasGradientService()) {
            float var_ = this.circleRadius;
            canvas2.drawRoundRect(rectF3, var_, var_, Theme.chat_actionBackgroundGradientDarkenPaint);
        }
    }

    private void showReleaseState(boolean z, final View view) {
        AnimatorSet animatorSet = this.showReleaseAnimator;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.showReleaseAnimator.cancel();
        }
        if (z) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.swipeToReleaseProgress, 1.0f});
            ofFloat.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda5(this, view));
            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            ofFloat.setDuration(250);
            this.bounceProgress = 0.0f;
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat2.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda4(this, view));
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_BOTH;
            ofFloat2.setInterpolator(cubicBezierInterpolator);
            ofFloat2.setDuration(180);
            ValueAnimator ofFloat3 = ValueAnimator.ofFloat(new float[]{1.0f, -0.5f});
            ofFloat3.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda3(this, view));
            ofFloat3.setInterpolator(cubicBezierInterpolator);
            ofFloat3.setDuration(120);
            ValueAnimator ofFloat4 = ValueAnimator.ofFloat(new float[]{-0.5f, 0.0f});
            ofFloat4.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda6(this, view));
            ofFloat4.setInterpolator(cubicBezierInterpolator);
            ofFloat4.setDuration(100);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.showReleaseAnimator = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ChatPullingDownDrawable chatPullingDownDrawable = ChatPullingDownDrawable.this;
                    chatPullingDownDrawable.bounceProgress = 0.0f;
                    chatPullingDownDrawable.swipeToReleaseProgress = 1.0f;
                    view.invalidate();
                    ChatPullingDownDrawable.this.fragmentView.invalidate();
                    Runnable runnable = ChatPullingDownDrawable.this.onAnimationFinishRunnable;
                    if (runnable != null) {
                        runnable.run();
                        ChatPullingDownDrawable.this.onAnimationFinishRunnable = null;
                    }
                }
            });
            AnimatorSet animatorSet3 = new AnimatorSet();
            animatorSet3.playSequentially(new Animator[]{ofFloat2, ofFloat3, ofFloat4});
            this.showReleaseAnimator.playTogether(new Animator[]{ofFloat, animatorSet3});
            this.showReleaseAnimator.start();
            return;
        }
        ValueAnimator ofFloat5 = ValueAnimator.ofFloat(new float[]{this.swipeToReleaseProgress, 0.0f});
        ofFloat5.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda2(this, view));
        ofFloat5.setInterpolator(CubicBezierInterpolator.DEFAULT);
        ofFloat5.setDuration(220);
        AnimatorSet animatorSet4 = new AnimatorSet();
        this.showReleaseAnimator = animatorSet4;
        animatorSet4.playTogether(new Animator[]{ofFloat5});
        this.showReleaseAnimator.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showReleaseState$0(View view, ValueAnimator valueAnimator) {
        this.swipeToReleaseProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        view.invalidate();
        this.fragmentView.invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showReleaseState$1(View view, ValueAnimator valueAnimator) {
        this.bounceProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        view.invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showReleaseState$2(View view, ValueAnimator valueAnimator) {
        this.bounceProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        view.invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showReleaseState$3(View view, ValueAnimator valueAnimator) {
        this.bounceProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        view.invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showReleaseState$4(View view, ValueAnimator valueAnimator) {
        this.swipeToReleaseProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.fragmentView.invalidate();
        view.invalidate();
    }

    private void drawArrow(Canvas canvas, float f, float f2, float f3) {
        canvas.save();
        float dpf2 = f3 / AndroidUtilities.dpf2(24.0f);
        canvas.scale(dpf2, dpf2, f, f2 - ((float) AndroidUtilities.dp(20.0f)));
        canvas.translate(f - ((float) AndroidUtilities.dp2(12.0f)), f2 - ((float) AndroidUtilities.dp(12.0f)));
        Canvas canvas2 = canvas;
        canvas2.drawLine(AndroidUtilities.dpf2(12.5f), AndroidUtilities.dpf2(4.0f), AndroidUtilities.dpf2(12.5f), AndroidUtilities.dpf2(22.0f), this.arrowPaint);
        canvas2.drawLine(AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(12.0f), AndroidUtilities.dpf2(12.5f), AndroidUtilities.dpf2(3.5f), this.arrowPaint);
        canvas.drawLine(AndroidUtilities.dpf2(21.5f), AndroidUtilities.dpf2(12.0f), AndroidUtilities.dpf2(12.5f), AndroidUtilities.dpf2(3.5f), this.arrowPaint);
        canvas.restore();
    }

    public void onAttach() {
        this.imageReceiver.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
    }

    public void onDetach() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        this.imageReceiver.onDetachedFromWindow();
        this.lastProgress = 0.0f;
        this.lastHapticTime = 0;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$Dialog tLRPC$Dialog;
        if (this.nextDialogId != 0 && (tLRPC$Dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.nextDialogId)) != null) {
            this.counterDrawable.setCount(tLRPC$Dialog.unread_count, true);
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
            }
        }
    }

    public static TLRPC$Dialog getNextUnreadDialog(long j, int i, int i2, boolean z, int[] iArr) {
        ArrayList<TLRPC$Dialog> arrayList;
        TLRPC$Dialog nextUnreadDialog;
        TLRPC$Dialog nextUnreadDialog2;
        int i3 = i;
        int i4 = i2;
        MessagesController messagesController = AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController();
        if (iArr != null) {
            iArr[0] = 0;
            iArr[1] = i3;
            iArr[2] = i4;
        }
        if (i4 != 0) {
            arrayList = messagesController.dialogFiltersById.get(i4).dialogs;
        } else {
            arrayList = messagesController.getDialogs(i);
        }
        if (arrayList == null) {
            return null;
        }
        for (int i5 = 0; i5 < arrayList.size(); i5++) {
            TLRPC$Dialog tLRPC$Dialog = arrayList.get(i5);
            TLRPC$Chat chat = messagesController.getChat(Long.valueOf(-tLRPC$Dialog.id));
            if (chat != null && tLRPC$Dialog.id != j && tLRPC$Dialog.unread_count > 0 && DialogObject.isChannel(tLRPC$Dialog) && !chat.megagroup && !messagesController.isPromoDialog(tLRPC$Dialog.id, false) && MessagesController.getRestrictionReason(chat.restriction_reason) == null) {
                return tLRPC$Dialog;
            }
        }
        if (z) {
            if (i4 != 0) {
                int i6 = 0;
                while (i6 < messagesController.dialogFilters.size()) {
                    int i7 = messagesController.dialogFilters.get(i6).id;
                    if (i4 == i7 || (nextUnreadDialog2 = getNextUnreadDialog(j, i, i7, false, iArr)) == null) {
                        i6++;
                    } else {
                        if (iArr != null) {
                            iArr[0] = 1;
                        }
                        return nextUnreadDialog2;
                    }
                }
            }
            int i8 = 0;
            while (i8 < messagesController.dialogsByFolder.size()) {
                int keyAt = messagesController.dialogsByFolder.keyAt(i8);
                if (i3 == keyAt || (nextUnreadDialog = getNextUnreadDialog(j, keyAt, 0, false, iArr)) == null) {
                    i8++;
                } else {
                    if (iArr != null) {
                        iArr[0] = 1;
                    }
                    return nextUnreadDialog;
                }
            }
        }
        return null;
    }

    public long getChatId() {
        return this.nextChat.id;
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00a1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawBottomPanel(android.graphics.Canvas r12, int r13, int r14, int r15) {
        /*
            r11 = this;
            boolean r0 = r11.showBottomPanel
            r1 = 1037726734(0x3dda740e, float:0.10666667)
            r2 = 0
            r3 = 1065353216(0x3var_, float:1.0)
            if (r0 == 0) goto L_0x0020
            float r4 = r11.progressToBottomPannel
            int r5 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0020
            float r4 = r4 + r1
            r11.progressToBottomPannel = r4
            int r0 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x001a
            r11.progressToBottomPannel = r3
            goto L_0x0037
        L_0x001a:
            android.view.View r0 = r11.fragmentView
            r0.invalidate()
            goto L_0x0037
        L_0x0020:
            if (r0 != 0) goto L_0x0037
            float r0 = r11.progressToBottomPannel
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0037
            float r0 = r0 - r1
            r11.progressToBottomPannel = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x0032
            r11.progressToBottomPannel = r2
            goto L_0x0037
        L_0x0032:
            android.view.View r0 = r11.fragmentView
            r0.invalidate()
        L_0x0037:
            java.lang.String r0 = "paintChatComposeBackground"
            android.graphics.Paint r0 = r11.getThemedPaint(r0)
            int r1 = r0.getAlpha()
            android.text.TextPaint r4 = r11.textPaint2
            int r10 = r4.getAlpha()
            float r4 = (float) r1
            float r5 = r11.progressToBottomPannel
            float r4 = r4 * r5
            int r4 = (int) r4
            r0.setAlpha(r4)
            r5 = 0
            float r6 = (float) r13
            float r7 = (float) r15
            float r8 = (float) r14
            r4 = r12
            r9 = r0
            r4.drawRect(r5, r6, r7, r8, r9)
            android.text.StaticLayout r14 = r11.layout1
            r15 = 1073741824(0x40000000, float:2.0)
            r4 = 1092616192(0x41200000, float:10.0)
            r5 = 1099956224(0x41900000, float:18.0)
            if (r14 == 0) goto L_0x009d
            float r14 = r11.swipeToReleaseProgress
            int r6 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1))
            if (r6 >= 0) goto L_0x009d
            android.text.TextPaint r6 = r11.textPaint2
            float r7 = (float) r10
            float r14 = r3 - r14
            float r7 = r7 * r14
            float r14 = r11.progressToBottomPannel
            float r7 = r7 * r14
            int r14 = (int) r7
            r6.setAlpha(r14)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r14 = r14 + r13
            float r14 = (float) r14
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r6 = (float) r6
            float r7 = r11.swipeToReleaseProgress
            float r6 = r6 * r7
            float r14 = r14 - r6
            r12.save()
            int r6 = r11.lastWidth
            int r7 = r11.layout1Width
            int r6 = r6 - r7
            float r6 = (float) r6
            float r6 = r6 / r15
            r12.translate(r6, r14)
            android.text.StaticLayout r14 = r11.layout1
            r14.draw(r12)
            r12.restore()
        L_0x009d:
            android.text.StaticLayout r14 = r11.layout2
            if (r14 == 0) goto L_0x00da
            float r14 = r11.swipeToReleaseProgress
            int r2 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x00da
            android.text.TextPaint r2 = r11.textPaint2
            float r6 = (float) r10
            float r6 = r6 * r14
            float r14 = r11.progressToBottomPannel
            float r6 = r6 * r14
            int r14 = (int) r6
            r2.setAlpha(r14)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r13 = r13 + r14
            float r13 = (float) r13
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r14 = (float) r14
            float r2 = r11.swipeToReleaseProgress
            float r3 = r3 - r2
            float r14 = r14 * r3
            float r13 = r13 + r14
            r12.save()
            int r14 = r11.lastWidth
            int r2 = r11.layout2Width
            int r14 = r14 - r2
            float r14 = (float) r14
            float r14 = r14 / r15
            r12.translate(r14, r13)
            android.text.StaticLayout r13 = r11.layout2
            r13.draw(r12)
            r12.restore()
        L_0x00da:
            android.text.TextPaint r12 = r11.textPaint2
            r12.setAlpha(r1)
            r0.setAlpha(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatPullingDownDrawable.drawBottomPanel(android.graphics.Canvas, int, int, int):void");
    }

    public void showBottomPanel(boolean z) {
        this.showBottomPanel = z;
        this.fragmentView.invalidate();
    }

    public boolean needDrawBottomPanel() {
        return (this.showBottomPanel || this.progressToBottomPannel > 0.0f) && !this.emptyStub;
    }

    public boolean animationIsRunning() {
        return this.swipeToReleaseProgress != 1.0f;
    }

    public void runOnAnimationFinish(Runnable runnable) {
        AnimatorSet animatorSet = this.showReleaseAnimator;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.showReleaseAnimator.cancel();
        }
        this.onAnimationFinishRunnable = runnable;
        this.showReleaseAnimator = new AnimatorSet();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.swipeToReleaseProgress, 1.0f});
        ofFloat.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda1(this));
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{this.bounceProgress, 0.0f});
        ofFloat2.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda0(this));
        this.showReleaseAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ChatPullingDownDrawable chatPullingDownDrawable = ChatPullingDownDrawable.this;
                chatPullingDownDrawable.bounceProgress = 0.0f;
                chatPullingDownDrawable.swipeToReleaseProgress = 1.0f;
                View view = chatPullingDownDrawable.parentView;
                if (view != null) {
                    view.invalidate();
                }
                ChatPullingDownDrawable.this.fragmentView.invalidate();
                Runnable runnable = ChatPullingDownDrawable.this.onAnimationFinishRunnable;
                if (runnable != null) {
                    runnable.run();
                    ChatPullingDownDrawable.this.onAnimationFinishRunnable = null;
                }
            }
        });
        this.showReleaseAnimator.playTogether(new Animator[]{ofFloat, ofFloat2});
        this.showReleaseAnimator.setDuration(120);
        this.showReleaseAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.showReleaseAnimator.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runOnAnimationFinish$5(ValueAnimator valueAnimator) {
        this.swipeToReleaseProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.fragmentView.invalidate();
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runOnAnimationFinish$6(ValueAnimator valueAnimator) {
        this.bounceProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public void reset() {
        this.checkProgress = 0.0f;
        this.animateCheck = false;
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    private Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Paint paint = resourcesProvider2 != null ? resourcesProvider2.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    private boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        return resourcesProvider2 != null ? resourcesProvider2.hasGradientService() : Theme.hasGradientService();
    }
}
