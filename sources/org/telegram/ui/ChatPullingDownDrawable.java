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
import org.telegram.tgnet.TLRPC;
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
    CounterView.CounterDrawable counterDrawable = new CounterView.CounterDrawable((View) null, true, (Theme.ResourcesProvider) null);
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
    TLRPC.Chat nextChat;
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

    public ChatPullingDownDrawable(int currentAccount2, View fragmentView2, long currentDialog2, int folderId2, int filterId2, Theme.ResourcesProvider resourcesProvider2) {
        this.fragmentView = fragmentView2;
        this.currentAccount = currentAccount2;
        this.currentDialog = currentDialog2;
        this.folderId = folderId2;
        this.filterId = filterId2;
        this.resourcesProvider = resourcesProvider2;
        this.arrowPaint.setStrokeWidth(AndroidUtilities.dpf2(2.8f));
        this.arrowPaint.setStrokeCap(Paint.Cap.ROUND);
        this.counterDrawable.gravity = 3;
        this.counterDrawable.setType(1);
        this.counterDrawable.addServiceGradient = true;
        this.counterDrawable.circlePaint = getThemedPaint("paintChatActionBackground");
        this.counterDrawable.textPaint = this.textPaint;
        this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint2.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.xRefPaint.setColor(-16777216);
        this.xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        updateDialog();
    }

    public void updateDialog() {
        TLRPC.Dialog dialog = getNextUnreadDialog(this.currentDialog, this.folderId, this.filterId, true, this.params);
        if (dialog != null) {
            this.nextDialogId = dialog.id;
            int[] iArr = this.params;
            this.drawFolderBackground = iArr[0] == 1;
            this.dialogFolderId = iArr[1];
            this.dialogFilterId = iArr[2];
            this.emptyStub = false;
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialog.id));
            this.nextChat = chat;
            if (chat == null) {
                MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(dialog.id));
            }
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(this.nextChat);
            this.imageReceiver.setImage(ImageLocation.getForChat(this.nextChat, 1), "50_50", avatarDrawable, (String) null, UserConfig.getInstance(0).getCurrentUser(), 0);
            MessagesController.getInstance(this.currentAccount).ensureMessagesLoaded(dialog.id, 0, (MessagesController.MessagesLoadedCallback) null);
            this.counterDrawable.setCount(dialog.unread_count, false);
            return;
        }
        this.nextChat = null;
        this.drawFolderBackground = false;
        this.emptyStub = true;
    }

    public void setWidth(int width) {
        String str2;
        String str1;
        int i;
        int i2 = width;
        if (i2 != this.lastWidth) {
            this.circleRadius = ((float) AndroidUtilities.dp(56.0f)) / 2.0f;
            this.lastWidth = i2;
            TLRPC.Chat chat = this.nextChat;
            String nameStr = chat != null ? chat.title : LocaleController.getString("SwipeToGoNextChannelEnd", NUM);
            int measureText = (int) this.textPaint.measureText(nameStr);
            this.chatNameWidth = measureText;
            this.chatNameWidth = Math.min(measureText, this.lastWidth - AndroidUtilities.dp(60.0f));
            this.chatNameLayout = new StaticLayout(nameStr, this.textPaint, this.chatNameWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            boolean z = this.drawFolderBackground;
            if (z && (i = this.dialogFolderId) != this.folderId && i != 0) {
                str1 = LocaleController.getString("SwipeToGoNextArchive", NUM);
                str2 = LocaleController.getString("ReleaseToGoNextArchive", NUM);
            } else if (z) {
                str1 = LocaleController.getString("SwipeToGoNextFolder", NUM);
                str2 = LocaleController.getString("ReleaseToGoNextFolder", NUM);
            } else {
                str1 = LocaleController.getString("SwipeToGoNextChannel", NUM);
                str2 = LocaleController.getString("ReleaseToGoNextChannel", NUM);
            }
            int measureText2 = (int) this.textPaint2.measureText(str1);
            this.layout1Width = measureText2;
            this.layout1Width = Math.min(measureText2, this.lastWidth - AndroidUtilities.dp(60.0f));
            this.layout1 = new StaticLayout(str1, this.textPaint2, this.layout1Width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            int measureText3 = (int) this.textPaint2.measureText(str2);
            this.layout2Width = measureText3;
            this.layout2Width = Math.min(measureText3, this.lastWidth - AndroidUtilities.dp(60.0f));
            this.layout2 = new StaticLayout(str2, this.textPaint2, this.layout2Width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.imageReceiver.setImageCoords((((float) this.lastWidth) / 2.0f) - (((float) AndroidUtilities.dp(40.0f)) / 2.0f), (((float) AndroidUtilities.dp(12.0f)) + this.circleRadius) - (((float) AndroidUtilities.dp(40.0f)) / 2.0f), (float) AndroidUtilities.dp(40.0f), (float) AndroidUtilities.dp(40.0f));
            this.imageReceiver.setRoundRadius((int) (((float) AndroidUtilities.dp(40.0f)) / 2.0f));
            this.counterDrawable.setSize(AndroidUtilities.dp(28.0f), AndroidUtilities.dp(100.0f));
        }
    }

    public void draw(Canvas canvas, View parent, float progress, float alpha) {
        float alpha2;
        int oldAlpha3;
        float offset;
        int oldAlpha32;
        int oldAlpha33;
        int oldAlpha34;
        Canvas canvas2 = canvas;
        View view = parent;
        float f = progress;
        this.parentView = view;
        this.counterDrawable.setParent(view);
        float offset2 = ((float) AndroidUtilities.dp(110.0f)) * f;
        if (offset2 >= ((float) AndroidUtilities.dp(8.0f))) {
            if (f < 0.2f) {
                alpha2 = 5.0f * f * alpha;
            } else {
                alpha2 = alpha;
            }
            Theme.applyServiceShaderMatrix(this.lastWidth, parent.getMeasuredHeight(), 0.0f, ((float) parent.getMeasuredHeight()) - offset2);
            this.textPaint.setColor(getThemedColor("chat_serviceText"));
            this.arrowPaint.setColor(getThemedColor("chat_serviceText"));
            this.textPaint2.setColor(getThemedColor("chat_messagePanelHint"));
            int oldAlpha = getThemedPaint("paintChatActionBackground").getAlpha();
            int oldAlpha1 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
            int oldAlpha2 = this.textPaint.getAlpha();
            int oldAlpha35 = this.arrowPaint.getAlpha();
            Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (((float) oldAlpha1) * alpha2));
            getThemedPaint("paintChatActionBackground").setAlpha((int) (((float) oldAlpha) * alpha2));
            this.textPaint.setAlpha((int) (((float) oldAlpha2) * alpha2));
            this.imageReceiver.setAlpha(alpha2);
            if ((f < 1.0f || this.lastProgress >= 1.0f) && (f >= 1.0f || this.lastProgress != 1.0f)) {
                oldAlpha3 = oldAlpha35;
            } else {
                long time = System.currentTimeMillis();
                oldAlpha3 = oldAlpha35;
                if (time - this.lastHapticTime > 100) {
                    view.performHapticFeedback(3, 2);
                    this.lastHapticTime = time;
                }
                this.lastProgress = f;
            }
            if (f == 1.0f && !this.animateSwipeToRelease) {
                this.animateSwipeToRelease = true;
                this.animateCheck = true;
                showReleaseState(true, view);
                this.lastShowingReleaseTime = System.currentTimeMillis();
            } else if (f != 1.0f && this.animateSwipeToRelease) {
                this.animateSwipeToRelease = false;
                showReleaseState(false, view);
            }
            float cx = ((float) this.lastWidth) / 2.0f;
            float bounceOffset = this.bounceProgress * ((float) (-AndroidUtilities.dp(4.0f)));
            if (this.emptyStub) {
                offset = offset2 - bounceOffset;
            } else {
                offset = offset2;
            }
            float widthRadius = Math.max(0.0f, Math.min(this.circleRadius, ((offset / 2.0f) - (((float) AndroidUtilities.dp(16.0f)) * f)) - ((float) AndroidUtilities.dp(4.0f))));
            float max = ((Math.max(0.0f, Math.min(this.circleRadius * f, (offset / 2.0f) - (((float) AndroidUtilities.dp(8.0f)) * f))) * 2.0f) - ((float) AndroidUtilities.dp2(16.0f))) * (1.0f - this.swipeToReleaseProgress);
            float f2 = this.swipeToReleaseProgress;
            float size = (((float) AndroidUtilities.dp(56.0f)) * f2) + max;
            if (f2 < 1.0f || this.emptyStub) {
                float bottom = (((float) (-AndroidUtilities.dp(8.0f))) * (1.0f - this.swipeToReleaseProgress)) + (((-offset) + ((float) AndroidUtilities.dp(56.0f))) * this.swipeToReleaseProgress);
                AndroidUtilities.rectTmp.set(cx - widthRadius, -offset, cx + widthRadius, bottom);
                if (this.swipeToReleaseProgress > 0.0f && !this.emptyStub) {
                    float inset = ((float) AndroidUtilities.dp(16.0f)) * this.swipeToReleaseProgress;
                    AndroidUtilities.rectTmp.inset(inset, inset);
                }
                drawBackground(canvas2, AndroidUtilities.rectTmp);
                float arrowCy = (((-offset) + ((float) AndroidUtilities.dp(24.0f))) + (((float) AndroidUtilities.dp(8.0f)) * (1.0f - f))) - (((float) AndroidUtilities.dp(36.0f)) * this.swipeToReleaseProgress);
                canvas.save();
                float f3 = bottom;
                AndroidUtilities.rectTmp.inset((float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f));
                canvas2.clipRect(AndroidUtilities.rectTmp);
                float f4 = this.swipeToReleaseProgress;
                if (f4 > 0.0f) {
                    this.arrowPaint.setAlpha((int) ((1.0f - f4) * 255.0f));
                }
                drawArrow(canvas2, cx, arrowCy, ((float) AndroidUtilities.dp(24.0f)) * f);
                if (this.emptyStub) {
                    float top = (((((float) (-AndroidUtilities.dp(8.0f))) - (((float) AndroidUtilities.dp2(8.0f)) * f)) - size) * (1.0f - this.swipeToReleaseProgress)) + (((-offset) - ((float) AndroidUtilities.dp(2.0f))) * this.swipeToReleaseProgress) + bounceOffset;
                    oldAlpha32 = oldAlpha3;
                    this.arrowPaint.setAlpha(oldAlpha32);
                    canvas.save();
                    canvas2.scale(f, f, cx, ((float) AndroidUtilities.dp(28.0f)) + top);
                    drawCheck(canvas2, cx, ((float) AndroidUtilities.dp(28.0f)) + top);
                    canvas.restore();
                } else {
                    oldAlpha32 = oldAlpha3;
                }
                canvas.restore();
            } else {
                oldAlpha32 = oldAlpha3;
            }
            if (this.chatNameLayout == null || this.swipeToReleaseProgress <= 0.0f) {
                oldAlpha33 = oldAlpha32;
                float f5 = alpha2;
            } else {
                getThemedPaint("paintChatActionBackground").setAlpha((int) (((float) oldAlpha) * alpha2));
                this.textPaint.setAlpha((int) (((float) oldAlpha2) * alpha2));
                float y = ((((float) AndroidUtilities.dp(20.0f)) * (1.0f - this.swipeToReleaseProgress)) - (((float) AndroidUtilities.dp(36.0f)) * this.swipeToReleaseProgress)) + bounceOffset;
                RectF rectF = AndroidUtilities.rectTmp;
                int i = this.lastWidth;
                int i2 = this.chatNameWidth;
                oldAlpha33 = oldAlpha32;
                float f6 = alpha2;
                rectF.set(((float) (i - i2)) / 2.0f, y, ((float) i) - (((float) (i - i2)) / 2.0f), ((float) this.chatNameLayout.getHeight()) + y);
                AndroidUtilities.rectTmp.inset((float) (-AndroidUtilities.dp(8.0f)), (float) (-AndroidUtilities.dp(4.0f)));
                canvas2.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), getThemedPaint("paintChatActionBackground"));
                if (hasGradientService()) {
                    canvas2.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(15.0f), (float) AndroidUtilities.dp(15.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
                }
                canvas.save();
                canvas2.translate(((float) (this.lastWidth - this.chatNameWidth)) / 2.0f, y);
                this.chatNameLayout.draw(canvas2);
                canvas.restore();
            }
            if (this.emptyStub || size <= 0.0f) {
                float f7 = offset;
                oldAlpha34 = oldAlpha33;
                float f8 = cx;
            } else {
                float top2 = (((((float) (-AndroidUtilities.dp(8.0f))) - (((float) AndroidUtilities.dp2(8.0f)) * f)) - size) * (1.0f - this.swipeToReleaseProgress)) + (((-offset) + ((float) AndroidUtilities.dp(4.0f))) * this.swipeToReleaseProgress) + bounceOffset;
                this.imageReceiver.setRoundRadius((int) (size / 2.0f));
                this.imageReceiver.setImageCoords(cx - (size / 2.0f), top2, size, size);
                if (this.swipeToReleaseProgress > 0.0f) {
                    float imageX = this.imageReceiver.getImageX();
                    float imageY = this.imageReceiver.getImageY();
                    float imageWidth = this.imageReceiver.getImageWidth() + this.imageReceiver.getImageX();
                    float f9 = size;
                    float size2 = this.imageReceiver.getImageHeight() + this.imageReceiver.getImageY();
                    float var_ = offset;
                    oldAlpha34 = oldAlpha33;
                    float cx2 = cx;
                    canvas.saveLayerAlpha(imageX, imageY, imageWidth, size2, 255, 31);
                    this.imageReceiver.draw(canvas2);
                    float var_ = this.swipeToReleaseProgress;
                    canvas2.scale(var_, var_, cx2 + ((float) AndroidUtilities.dp(12.0f)) + this.counterDrawable.getCenterX(), (top2 - ((float) AndroidUtilities.dp(6.0f))) + ((float) AndroidUtilities.dp(14.0f)));
                    canvas2.translate(cx2 + ((float) AndroidUtilities.dp(12.0f)), top2 - ((float) AndroidUtilities.dp(6.0f)));
                    this.counterDrawable.updateBackgroundRect();
                    this.counterDrawable.rectF.inset((float) (-AndroidUtilities.dp(2.0f)), (float) (-AndroidUtilities.dp(2.0f)));
                    canvas2.drawRoundRect(this.counterDrawable.rectF, this.counterDrawable.rectF.height() / 2.0f, this.counterDrawable.rectF.height() / 2.0f, this.xRefPaint);
                    canvas.restore();
                    canvas.save();
                    float var_ = this.swipeToReleaseProgress;
                    canvas2.scale(var_, var_, cx2 + ((float) AndroidUtilities.dp(12.0f)) + this.counterDrawable.getCenterX(), (top2 - ((float) AndroidUtilities.dp(6.0f))) + ((float) AndroidUtilities.dp(14.0f)));
                    canvas2.translate(cx2 + ((float) AndroidUtilities.dp(12.0f)), top2 - ((float) AndroidUtilities.dp(6.0f)));
                    this.counterDrawable.draw(canvas2);
                    canvas.restore();
                } else {
                    float var_ = offset;
                    oldAlpha34 = oldAlpha33;
                    float var_ = cx;
                    this.imageReceiver.draw(canvas2);
                }
            }
            getThemedPaint("paintChatActionBackground").setAlpha(oldAlpha);
            Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(oldAlpha1);
            this.textPaint.setAlpha(oldAlpha2);
            this.arrowPaint.setAlpha(oldAlpha34);
            this.imageReceiver.setAlpha(1.0f);
        }
    }

    private void drawCheck(Canvas canvas, float cx, float cy) {
        Canvas canvas2 = canvas;
        if (this.animateCheck) {
            float f = this.checkProgress;
            if (f < 1.0f) {
                float f2 = f + 0.07272727f;
                this.checkProgress = f2;
                if (f2 > 1.0f) {
                    this.checkProgress = 1.0f;
                }
            }
            float f3 = this.checkProgress;
            float p1 = f3 > 0.5f ? 1.0f : f3 / 0.5f;
            float p2 = f3 < 0.5f ? 0.0f : (f3 - 0.5f) / 0.5f;
            canvas.save();
            canvas2.clipRect(AndroidUtilities.rectTmp);
            canvas2.translate(cx - ((float) AndroidUtilities.dp(24.0f)), cy - ((float) AndroidUtilities.dp(24.0f)));
            float x1 = (float) AndroidUtilities.dp(16.0f);
            float y1 = (float) AndroidUtilities.dp(26.0f);
            float x2 = (float) AndroidUtilities.dp(22.0f);
            float y2 = (float) AndroidUtilities.dp(32.0f);
            float x3 = (float) AndroidUtilities.dp(32.0f);
            float y3 = (float) AndroidUtilities.dp(20.0f);
            float x32 = x3;
            canvas.drawLine(x1, y1, ((1.0f - p1) * x1) + (x2 * p1), ((1.0f - p1) * y1) + (y2 * p1), this.arrowPaint);
            if (p2 > 0.0f) {
                canvas.drawLine(x2, y2, ((1.0f - p2) * x2) + (x32 * p2), (y3 * p2) + ((1.0f - p2) * y2), this.arrowPaint);
            }
            canvas.restore();
        }
    }

    private void drawBackground(Canvas canvas, RectF rectTmp) {
        if (this.drawFolderBackground) {
            this.path.reset();
            float roundRadius = rectTmp.width() * 0.2f;
            float folderOffset = rectTmp.width() * 0.1f;
            float folderOffset2 = rectTmp.width() * 0.03f;
            float roundRadius2 = folderOffset / 2.0f;
            float h = rectTmp.height() - folderOffset;
            this.path.moveTo(rectTmp.right, rectTmp.top + roundRadius + folderOffset);
            this.path.rQuadTo(0.0f, -roundRadius, -roundRadius, -roundRadius);
            this.path.rLineTo((((-(rectTmp.width() - (roundRadius * 2.0f))) / 2.0f) + (roundRadius2 * 2.0f)) - folderOffset2, 0.0f);
            this.path.rQuadTo((-roundRadius2) / 2.0f, 0.0f, (-roundRadius2) * 2.0f, (-folderOffset) / 2.0f);
            this.path.rQuadTo((-roundRadius2) / 2.0f, (-folderOffset) / 2.0f, (-roundRadius2) * 2.0f, (-folderOffset) / 2.0f);
            this.path.rLineTo(((-(rectTmp.width() - (roundRadius * 2.0f))) / 2.0f) + (roundRadius2 * 2.0f) + folderOffset2, 0.0f);
            this.path.rQuadTo(-roundRadius, 0.0f, -roundRadius, roundRadius);
            this.path.rLineTo(0.0f, (h + folderOffset) - (roundRadius * 2.0f));
            this.path.rQuadTo(0.0f, roundRadius, roundRadius, roundRadius);
            this.path.rLineTo(rectTmp.width() - (roundRadius * 2.0f), 0.0f);
            this.path.rQuadTo(roundRadius, 0.0f, roundRadius, -roundRadius);
            this.path.rLineTo(0.0f, -(h - (2.0f * roundRadius)));
            this.path.close();
            canvas.drawPath(this.path, getThemedPaint("paintChatActionBackground"));
            if (hasGradientService()) {
                canvas.drawPath(this.path, Theme.chat_actionBackgroundGradientDarkenPaint);
                return;
            }
            return;
        }
        RectF rectF = AndroidUtilities.rectTmp;
        float f = this.circleRadius;
        canvas.drawRoundRect(rectF, f, f, getThemedPaint("paintChatActionBackground"));
        if (hasGradientService()) {
            RectF rectF2 = AndroidUtilities.rectTmp;
            float f2 = this.circleRadius;
            canvas.drawRoundRect(rectF2, f2, f2, Theme.chat_actionBackgroundGradientDarkenPaint);
        }
    }

    private void showReleaseState(boolean show, final View parent) {
        AnimatorSet animatorSet = this.showReleaseAnimator;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.showReleaseAnimator.cancel();
        }
        if (show) {
            ValueAnimator out = ValueAnimator.ofFloat(new float[]{this.swipeToReleaseProgress, 1.0f});
            out.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda2(this, parent));
            out.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            out.setDuration(250);
            this.bounceProgress = 0.0f;
            ValueAnimator bounceUp = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            bounceUp.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda3(this, parent));
            bounceUp.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            bounceUp.setDuration(180);
            ValueAnimator bounceDown = ValueAnimator.ofFloat(new float[]{1.0f, -0.5f});
            bounceDown.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda4(this, parent));
            bounceDown.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            bounceDown.setDuration(120);
            ValueAnimator bounceOut = ValueAnimator.ofFloat(new float[]{-0.5f, 0.0f});
            bounceOut.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda5(this, parent));
            bounceOut.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            bounceOut.setDuration(100);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.showReleaseAnimator = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ChatPullingDownDrawable.this.bounceProgress = 0.0f;
                    ChatPullingDownDrawable.this.swipeToReleaseProgress = 1.0f;
                    parent.invalidate();
                    ChatPullingDownDrawable.this.fragmentView.invalidate();
                    if (ChatPullingDownDrawable.this.onAnimationFinishRunnable != null) {
                        ChatPullingDownDrawable.this.onAnimationFinishRunnable.run();
                        ChatPullingDownDrawable.this.onAnimationFinishRunnable = null;
                    }
                }
            });
            AnimatorSet bounce = new AnimatorSet();
            bounce.playSequentially(new Animator[]{bounceUp, bounceDown, bounceOut});
            this.showReleaseAnimator.playTogether(new Animator[]{out, bounce});
            this.showReleaseAnimator.start();
            return;
        }
        ValueAnimator out2 = ValueAnimator.ofFloat(new float[]{this.swipeToReleaseProgress, 0.0f});
        out2.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda6(this, parent));
        out2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        out2.setDuration(220);
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.showReleaseAnimator = animatorSet3;
        animatorSet3.playTogether(new Animator[]{out2});
        this.showReleaseAnimator.start();
    }

    /* renamed from: lambda$showReleaseState$0$org-telegram-ui-ChatPullingDownDrawable  reason: not valid java name */
    public /* synthetic */ void m1965xaa989d1b(View parent, ValueAnimator animation) {
        this.swipeToReleaseProgress = ((Float) animation.getAnimatedValue()).floatValue();
        parent.invalidate();
        this.fragmentView.invalidate();
    }

    /* renamed from: lambda$showReleaseState$1$org-telegram-ui-ChatPullingDownDrawable  reason: not valid java name */
    public /* synthetic */ void m1966x3738CLASSNAMEc(View parent, ValueAnimator animation) {
        this.bounceProgress = ((Float) animation.getAnimatedValue()).floatValue();
        parent.invalidate();
    }

    /* renamed from: lambda$showReleaseState$2$org-telegram-ui-ChatPullingDownDrawable  reason: not valid java name */
    public /* synthetic */ void m1967xc3d8var_d(View parent, ValueAnimator animation) {
        this.bounceProgress = ((Float) animation.getAnimatedValue()).floatValue();
        parent.invalidate();
    }

    /* renamed from: lambda$showReleaseState$3$org-telegram-ui-ChatPullingDownDrawable  reason: not valid java name */
    public /* synthetic */ void m1968x50791e1e(View parent, ValueAnimator animation) {
        this.bounceProgress = ((Float) animation.getAnimatedValue()).floatValue();
        parent.invalidate();
    }

    /* renamed from: lambda$showReleaseState$4$org-telegram-ui-ChatPullingDownDrawable  reason: not valid java name */
    public /* synthetic */ void m1969xdd19491f(View parent, ValueAnimator animation) {
        this.swipeToReleaseProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.fragmentView.invalidate();
        parent.invalidate();
    }

    private void drawArrow(Canvas canvas, float cx, float cy, float size) {
        canvas.save();
        float s = size / AndroidUtilities.dpf2(24.0f);
        canvas.scale(s, s, cx, cy - ((float) AndroidUtilities.dp(20.0f)));
        canvas.translate(cx - ((float) AndroidUtilities.dp2(12.0f)), cy - ((float) AndroidUtilities.dp(12.0f)));
        canvas.drawLine(AndroidUtilities.dpf2(12.5f), AndroidUtilities.dpf2(4.0f), AndroidUtilities.dpf2(12.5f), AndroidUtilities.dpf2(22.0f), this.arrowPaint);
        canvas.drawLine(AndroidUtilities.dpf2(3.5f), AndroidUtilities.dpf2(12.0f), AndroidUtilities.dpf2(12.5f), AndroidUtilities.dpf2(3.5f), this.arrowPaint);
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

    public void didReceivedNotification(int id, int account, Object... args) {
        TLRPC.Dialog dialog;
        if (this.nextDialogId != 0 && (dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.nextDialogId)) != null) {
            this.counterDrawable.setCount(dialog.unread_count, true);
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
            }
        }
    }

    public static TLRPC.Dialog getNextUnreadDialog(long currentDialogId, int folderId2, int filterId2) {
        return getNextUnreadDialog(currentDialogId, folderId2, filterId2, true, (int[]) null);
    }

    public static TLRPC.Dialog getNextUnreadDialog(long currentDialogId, int folderId2, int filterId2, boolean searchNext, int[] params2) {
        ArrayList<TLRPC.Dialog> dialogs;
        TLRPC.Dialog dialog;
        TLRPC.Dialog dialog2;
        int i = folderId2;
        int i2 = filterId2;
        MessagesController messagesController = AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController();
        if (params2 != null) {
            params2[0] = 0;
            params2[1] = i;
            params2[2] = i2;
        }
        if (i2 != 0) {
            MessagesController.DialogFilter filter = messagesController.dialogFiltersById.get(i2);
            if (filter == null) {
                return null;
            }
            dialogs = filter.dialogs;
        } else {
            dialogs = messagesController.getDialogs(i);
        }
        if (dialogs == null) {
            return null;
        }
        for (int i3 = 0; i3 < dialogs.size(); i3++) {
            TLRPC.Dialog dialog3 = dialogs.get(i3);
            TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-dialog3.id));
            if (chat != null && dialog3.id != currentDialogId && dialog3.unread_count > 0 && DialogObject.isChannel(dialog3) && !chat.megagroup && !messagesController.isPromoDialog(dialog3.id, false) && MessagesController.getRestrictionReason(chat.restriction_reason) == null) {
                return dialog3;
            }
        }
        if (searchNext) {
            if (i2 != 0) {
                int i4 = 0;
                while (i4 < messagesController.dialogFilters.size()) {
                    int newFilterId = messagesController.dialogFilters.get(i4).id;
                    if (i2 == newFilterId || (dialog2 = getNextUnreadDialog(currentDialogId, folderId2, newFilterId, false, params2)) == null) {
                        i4++;
                    } else {
                        if (params2 != null) {
                            params2[0] = 1;
                        }
                        return dialog2;
                    }
                }
            }
            int i5 = 0;
            while (i5 < messagesController.dialogsByFolder.size()) {
                int newFolderId = messagesController.dialogsByFolder.keyAt(i5);
                if (i == newFolderId || (dialog = getNextUnreadDialog(currentDialogId, newFolderId, 0, false, params2)) == null) {
                    i5++;
                } else {
                    if (params2 != null) {
                        params2[0] = 1;
                    }
                    return dialog;
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
            android.text.StaticLayout r4 = r11.layout1
            r5 = 1073741824(0x40000000, float:2.0)
            r6 = 1092616192(0x41200000, float:10.0)
            r7 = 1099956224(0x41900000, float:18.0)
            if (r4 == 0) goto L_0x009d
            float r4 = r11.swipeToReleaseProgress
            int r8 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r8 >= 0) goto L_0x009d
            android.text.TextPaint r8 = r11.textPaint2
            float r9 = (float) r10
            float r4 = r3 - r4
            float r9 = r9 * r4
            float r4 = r11.progressToBottomPannel
            float r9 = r9 * r4
            int r4 = (int) r9
            r8.setAlpha(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 + r13
            float r4 = (float) r4
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r8 = (float) r8
            float r9 = r11.swipeToReleaseProgress
            float r8 = r8 * r9
            float r4 = r4 - r8
            r12.save()
            int r8 = r11.lastWidth
            int r9 = r11.layout1Width
            int r8 = r8 - r9
            float r8 = (float) r8
            float r8 = r8 / r5
            r12.translate(r8, r4)
            android.text.StaticLayout r8 = r11.layout1
            r8.draw(r12)
            r12.restore()
        L_0x009d:
            android.text.StaticLayout r4 = r11.layout2
            if (r4 == 0) goto L_0x00da
            float r4 = r11.swipeToReleaseProgress
            int r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x00da
            android.text.TextPaint r2 = r11.textPaint2
            float r8 = (float) r10
            float r8 = r8 * r4
            float r4 = r11.progressToBottomPannel
            float r8 = r8 * r4
            int r4 = (int) r8
            r2.setAlpha(r4)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r2 = r2 + r13
            float r2 = (float) r2
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r4 = (float) r4
            float r6 = r11.swipeToReleaseProgress
            float r3 = r3 - r6
            float r4 = r4 * r3
            float r2 = r2 + r4
            r12.save()
            int r3 = r11.lastWidth
            int r4 = r11.layout2Width
            int r3 = r3 - r4
            float r3 = (float) r3
            float r3 = r3 / r5
            r12.translate(r3, r2)
            android.text.StaticLayout r3 = r11.layout2
            r3.draw(r12)
            r12.restore()
        L_0x00da:
            android.text.TextPaint r2 = r11.textPaint2
            r2.setAlpha(r10)
            r0.setAlpha(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatPullingDownDrawable.drawBottomPanel(android.graphics.Canvas, int, int, int):void");
    }

    public void showBottomPanel(boolean b) {
        this.showBottomPanel = b;
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
        ValueAnimator out = ValueAnimator.ofFloat(new float[]{this.swipeToReleaseProgress, 1.0f});
        out.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda0(this));
        ValueAnimator bounceOut = ValueAnimator.ofFloat(new float[]{this.bounceProgress, 0.0f});
        bounceOut.addUpdateListener(new ChatPullingDownDrawable$$ExternalSyntheticLambda1(this));
        this.showReleaseAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ChatPullingDownDrawable.this.bounceProgress = 0.0f;
                ChatPullingDownDrawable.this.swipeToReleaseProgress = 1.0f;
                if (ChatPullingDownDrawable.this.parentView != null) {
                    ChatPullingDownDrawable.this.parentView.invalidate();
                }
                ChatPullingDownDrawable.this.fragmentView.invalidate();
                if (ChatPullingDownDrawable.this.onAnimationFinishRunnable != null) {
                    ChatPullingDownDrawable.this.onAnimationFinishRunnable.run();
                    ChatPullingDownDrawable.this.onAnimationFinishRunnable = null;
                }
            }
        });
        this.showReleaseAnimator.playTogether(new Animator[]{out, bounceOut});
        this.showReleaseAnimator.setDuration(120);
        this.showReleaseAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.showReleaseAnimator.start();
    }

    /* renamed from: lambda$runOnAnimationFinish$5$org-telegram-ui-ChatPullingDownDrawable  reason: not valid java name */
    public /* synthetic */ void m1963x9CLASSNAMEc7c6(ValueAnimator animation) {
        this.swipeToReleaseProgress = ((Float) animation.getAnimatedValue()).floatValue();
        this.fragmentView.invalidate();
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    /* renamed from: lambda$runOnAnimationFinish$6$org-telegram-ui-ChatPullingDownDrawable  reason: not valid java name */
    public /* synthetic */ void m1964x2913f2c7(ValueAnimator animation) {
        this.bounceProgress = ((Float) animation.getAnimatedValue()).floatValue();
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public void reset() {
        this.checkProgress = 0.0f;
        this.animateCheck = false;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private Paint getThemedPaint(String paintKey) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Paint paint = resourcesProvider2 != null ? resourcesProvider2.getPaint(paintKey) : null;
        return paint != null ? paint : Theme.getThemePaint(paintKey);
    }

    private boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        return resourcesProvider2 != null ? resourcesProvider2.hasGradientService() : Theme.hasGradientService();
    }
}
