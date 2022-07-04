package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.StateSet;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import java.util.Arrays;
import java.util.HashMap;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.PremiumPreviewFragment;

public class TranscribeButton {
    private static final int[] pressedState = {16842910, 16842919};
    private static HashMap<Integer, MessageObject> transcribeOperationsByDialogPosition;
    private static HashMap<Long, MessageObject> transcribeOperationsById;
    private int backgroundColor;
    private Paint backgroundPaint;
    private Rect bounds;
    private Path boundsPath;
    private int color;
    private int iconColor;
    private RLottieDrawable inIconDrawable;
    private final FastOutSlowInInterpolator interpolator = new FastOutSlowInInterpolator();
    private boolean isOpen;
    private boolean loading;
    private AnimatedFloat loadingFloat;
    private RLottieDrawable outIconDrawable;
    private ChatMessageCell parent;
    private boolean premium;
    private Rect pressBounds;
    private long pressId = 0;
    private boolean pressed = false;
    private Path progressClipPath;
    private int rippleColor;
    private SeekBarWaveform seekBar;
    private float[] segments;
    private Drawable selectorDrawable;
    private boolean shouldBeOpen;
    private long start = SystemClock.elapsedRealtime();
    private Paint strokePaint;

    public TranscribeButton(ChatMessageCell parent2, SeekBarWaveform seekBar2) {
        this.parent = parent2;
        this.seekBar = seekBar2;
        this.bounds = new Rect(0, 0, AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
        Rect rect = new Rect(this.bounds);
        this.pressBounds = rect;
        rect.inset(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "transcribe_out", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
        this.outIconDrawable = rLottieDrawable;
        rLottieDrawable.setCurrentFrame(0);
        this.outIconDrawable.setCallback(parent2);
        this.outIconDrawable.addParentView(parent2);
        this.outIconDrawable.setOnFinishCallback(new TranscribeButton$$ExternalSyntheticLambda4(this), 19);
        this.outIconDrawable.setAllowDecodeSingleFrame(true);
        RLottieDrawable rLottieDrawable2 = new RLottieDrawable(NUM, "transcribe_in", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
        this.inIconDrawable = rLottieDrawable2;
        rLottieDrawable2.setCurrentFrame(0);
        this.inIconDrawable.setCallback(parent2);
        this.inIconDrawable.addParentView(parent2);
        this.inIconDrawable.setOnFinishCallback(new TranscribeButton$$ExternalSyntheticLambda5(this), 19);
        this.inIconDrawable.setAllowDecodeSingleFrame(true);
        this.isOpen = false;
        this.shouldBeOpen = false;
        this.premium = AccountInstance.getInstance(parent2.getMessageObject().currentAccount).getUserConfig().isPremium();
        this.loadingFloat = new AnimatedFloat((View) parent2, 250, (TimeInterpolator) CubicBezierInterpolator.EASE_OUT_QUINT);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-TranscribeButton  reason: not valid java name */
    public /* synthetic */ void m1501lambda$new$0$orgtelegramuiComponentsTranscribeButton() {
        this.outIconDrawable.stop();
        this.inIconDrawable.stop();
        this.shouldBeOpen = true;
        this.isOpen = true;
        this.inIconDrawable.setCurrentFrame(0);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-TranscribeButton  reason: not valid java name */
    public /* synthetic */ void m1502lambda$new$1$orgtelegramuiComponentsTranscribeButton() {
        this.inIconDrawable.stop();
        this.outIconDrawable.stop();
        this.shouldBeOpen = false;
        this.isOpen = false;
        this.outIconDrawable.setCurrentFrame(0);
    }

    public void setLoading(boolean loading2, boolean animated) {
        this.loading = loading2;
        this.seekBar.setLoading(loading2);
        float f = 0.0f;
        if (!animated) {
            AnimatedFloat animatedFloat = this.loadingFloat;
            if (this.loading) {
                f = 1.0f;
            }
            animatedFloat.set(f, true);
        } else if (this.loadingFloat.get() <= 0.0f) {
            this.start = SystemClock.elapsedRealtime();
        }
        ChatMessageCell chatMessageCell = this.parent;
        if (chatMessageCell != null) {
            chatMessageCell.invalidate();
        }
    }

    public void setOpen(boolean open, boolean animated) {
        boolean wasShouldBeOpen = this.shouldBeOpen;
        this.shouldBeOpen = open;
        if (!animated) {
            this.isOpen = open;
            this.inIconDrawable.stop();
            this.outIconDrawable.stop();
            this.inIconDrawable.setCurrentFrame(0);
            this.outIconDrawable.setCurrentFrame(0);
        } else if (open && !wasShouldBeOpen) {
            this.isOpen = false;
            this.inIconDrawable.setCurrentFrame(0);
            this.outIconDrawable.setCurrentFrame(0);
            this.outIconDrawable.start();
        } else if (!open && wasShouldBeOpen) {
            this.isOpen = true;
            this.outIconDrawable.setCurrentFrame(0);
            this.inIconDrawable.setCurrentFrame(0);
            this.inIconDrawable.start();
        }
        ChatMessageCell chatMessageCell = this.parent;
        if (chatMessageCell != null) {
            chatMessageCell.invalidate();
        }
    }

    public boolean onTouch(int action, float x, float y) {
        if (action == 1 || action == 3) {
            if (!this.pressed || action != 1) {
                this.pressed = false;
                return false;
            }
            onTap();
            return true;
        } else if (!this.pressBounds.contains((int) x, (int) y)) {
            return false;
        } else {
            if (action == 0) {
                this.pressed = true;
            }
            if (this.pressed && Build.VERSION.SDK_INT >= 21) {
                Drawable drawable = this.selectorDrawable;
                if (drawable instanceof RippleDrawable) {
                    drawable.setHotspot(x, y);
                    this.selectorDrawable.setState(pressedState);
                    this.parent.invalidate();
                }
            }
            return true;
        }
    }

    public void onTap() {
        boolean processClick;
        boolean z = this.shouldBeOpen;
        boolean toOpen = !z;
        if (!z) {
            processClick = !this.loading;
            if (this.premium && this.parent.getMessageObject().isSent()) {
                setLoading(true, true);
            }
        } else {
            processClick = true;
            setOpen(false, true);
            setLoading(false, true);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable drawable = this.selectorDrawable;
            if (drawable instanceof RippleDrawable) {
                drawable.setState(StateSet.NOTHING);
                this.parent.invalidate();
            }
        }
        this.pressed = false;
        if (!processClick) {
            return;
        }
        if (this.premium || !toOpen) {
            transcribePressed(this.parent.getMessageObject(), toOpen);
        } else if (this.parent.getDelegate() != null) {
            this.parent.getDelegate().needShowPremiumFeatures(PremiumPreviewFragment.featureTypeToServerString(8));
        }
    }

    public void setColor(boolean isOut, int color2, int grayColor) {
        boolean z = !this.premium;
        boolean newColor = this.color != color2;
        this.color = color2;
        this.iconColor = color2;
        int alphaComponent = ColorUtils.setAlphaComponent(color2, (int) (((float) Color.alpha(color2)) * 0.156f));
        this.backgroundColor = alphaComponent;
        this.rippleColor = Theme.blendOver(alphaComponent, ColorUtils.setAlphaComponent(color2, (int) (((float) Color.alpha(color2)) * (Theme.isCurrentThemeDark() ? 0.3f : 0.2f))));
        if (this.backgroundPaint == null) {
            this.backgroundPaint = new Paint();
        }
        this.backgroundPaint.setColor(this.backgroundColor);
        if (newColor || this.selectorDrawable == null) {
            Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), 0, this.rippleColor);
            this.selectorDrawable = createSimpleSelectorRoundRectDrawable;
            createSimpleSelectorRoundRectDrawable.setCallback(this.parent);
        }
        if (newColor) {
            this.inIconDrawable.beginApplyLayerColors();
            this.inIconDrawable.setLayerColor("Artboard Outlines.**", this.iconColor);
            this.inIconDrawable.commitApplyLayerColors();
            this.inIconDrawable.setAllowDecodeSingleFrame(true);
            this.inIconDrawable.updateCurrentFrame();
            this.outIconDrawable.beginApplyLayerColors();
            this.outIconDrawable.setLayerColor("Artboard Outlines.**", this.iconColor);
            this.outIconDrawable.commitApplyLayerColors();
            this.outIconDrawable.setAllowDecodeSingleFrame(true);
            this.outIconDrawable.updateCurrentFrame();
        }
        if (this.strokePaint == null) {
            Paint paint = new Paint(1);
            this.strokePaint = paint;
            paint.setStyle(Paint.Style.STROKE);
        }
        this.strokePaint.setColor(color2);
    }

    public void draw(Canvas canvas) {
        this.bounds.set(0, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(27.0f));
        this.pressBounds.set(this.bounds.left - AndroidUtilities.dp(8.0f), this.bounds.top - AndroidUtilities.dp(8.0f), this.bounds.right + AndroidUtilities.dp(8.0f), this.bounds.bottom + AndroidUtilities.dp(8.0f));
        if (this.boundsPath == null) {
            this.boundsPath = new Path();
            AndroidUtilities.rectTmp.set(this.bounds);
            this.boundsPath.addRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), Path.Direction.CW);
        }
        canvas.save();
        canvas.clipPath(this.boundsPath);
        Paint paint = this.backgroundPaint;
        if (paint != null) {
            canvas.drawRect(this.bounds, paint);
        }
        Drawable drawable = this.selectorDrawable;
        if (drawable != null && this.premium) {
            drawable.setBounds(this.bounds);
            this.selectorDrawable.draw(canvas);
        }
        canvas.restore();
        float f = 1.0f;
        float loadingT = this.loadingFloat.set(this.loading ? 1.0f : 0.0f);
        if (loadingT > 0.0f) {
            float[] segments2 = getSegments((long) (((float) (SystemClock.elapsedRealtime() - this.start)) * 0.75f));
            canvas.save();
            if (this.progressClipPath == null) {
                this.progressClipPath = new Path();
            }
            this.progressClipPath.reset();
            AndroidUtilities.rectTmp.set(this.pressBounds);
            float segmentLength = Math.max(40.0f * loadingT, segments2[1] - segments2[0]);
            Path path = this.progressClipPath;
            RectF rectF = AndroidUtilities.rectTmp;
            float f2 = segments2[0];
            float f3 = (1.0f - loadingT) * segmentLength;
            if (this.loading) {
                f = 0.0f;
            }
            path.addArc(rectF, f2 + (f3 * f), segmentLength * loadingT);
            this.progressClipPath.lineTo(AndroidUtilities.rectTmp.centerX(), AndroidUtilities.rectTmp.centerY());
            this.progressClipPath.close();
            canvas.clipPath(this.progressClipPath);
            AndroidUtilities.rectTmp.set(this.bounds);
            this.strokePaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
            canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), this.strokePaint);
            canvas.restore();
            this.parent.invalidate();
        }
        canvas.save();
        canvas.translate((float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f));
        if (this.isOpen) {
            this.inIconDrawable.draw(canvas);
        } else {
            this.outIconDrawable.draw(canvas);
        }
        canvas.restore();
    }

    private float[] getSegments(long d) {
        if (this.segments == null) {
            this.segments = new float[2];
        }
        long t = d % 5400;
        float[] fArr = this.segments;
        fArr[0] = (((float) (t * 1520)) / 5400.0f) - 20.0f;
        fArr[1] = ((float) (1520 * t)) / 5400.0f;
        for (int i = 0; i < 4; i++) {
            float[] fArr2 = this.segments;
            fArr2[1] = fArr2[1] + (this.interpolator.getInterpolation(((float) (t - ((long) (i * 1350)))) / 667.0f) * 250.0f);
            float[] fArr3 = this.segments;
            fArr3[0] = fArr3[0] + (this.interpolator.getInterpolation(((float) (t - ((long) ((i * 1350) + 667)))) / 667.0f) * 250.0f);
        }
        return this.segments;
    }

    public static class LoadingPointsSpan extends ImageSpan {
        private static LoadingPointsDrawable drawable;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public LoadingPointsSpan() {
            /*
                r6 = this;
                org.telegram.ui.Components.TranscribeButton$LoadingPointsDrawable r0 = drawable
                if (r0 != 0) goto L_0x000d
                org.telegram.ui.Components.TranscribeButton$LoadingPointsDrawable r0 = new org.telegram.ui.Components.TranscribeButton$LoadingPointsDrawable
                android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
                r0.<init>(r1)
                drawable = r0
            L_0x000d:
                r1 = 0
                r6.<init>(r0, r1)
                android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
                float r0 = r0.getTextSize()
                r2 = 1063507722(0x3var_d70a, float:0.89)
                float r0 = r0 * r2
                r2 = 1017370378(0x3ca3d70a, float:0.02)
                float r2 = r2 * r0
                int r2 = (int) r2
                android.graphics.drawable.Drawable r3 = r6.getDrawable()
                int r4 = (int) r0
                r5 = 1067450368(0x3fa00000, float:1.25)
                float r5 = r5 * r0
                int r5 = (int) r5
                int r5 = r5 + r2
                r3.setBounds(r1, r2, r4, r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranscribeButton.LoadingPointsSpan.<init>():void");
        }

        public void updateDrawState(TextPaint textPaint) {
            float fontSize = textPaint.getTextSize() * 0.89f;
            int yoff = (int) (0.02f * fontSize);
            getDrawable().setBounds(0, yoff, (int) fontSize, ((int) (1.25f * fontSize)) + yoff);
            super.updateDrawState(textPaint);
        }
    }

    private static class LoadingPointsDrawable extends Drawable {
        private int lastColor;
        private RLottieDrawable lottie;
        private Paint paint;

        public LoadingPointsDrawable(TextPaint textPaint) {
            this.paint = textPaint;
            float fontSize = textPaint.getTextSize() * 0.89f;
            AnonymousClass1 r1 = new RLottieDrawable(NUM, "dots_loading", (int) fontSize, (int) (1.25f * fontSize)) {
                /* access modifiers changed from: protected */
                public boolean hasParentView() {
                    return true;
                }
            };
            this.lottie = r1;
            r1.setAutoRepeat(1);
            this.lottie.setCurrentFrame((int) ((((float) SystemClock.elapsedRealtime()) / 16.0f) % 60.0f));
            this.lottie.setAllowDecodeSingleFrame(true);
            this.lottie.start();
        }

        public void setColor(int color) {
            this.lottie.beginApplyLayerColors();
            this.lottie.setLayerColor("Comp 1.**", color);
            this.lottie.commitApplyLayerColors();
            this.lottie.setAllowDecodeSingleFrame(true);
            this.lottie.updateCurrentFrame();
        }

        public void draw(Canvas canvas) {
            int color = this.paint.getColor();
            if (color != this.lastColor) {
                setColor(color);
                this.lastColor = color;
            }
            this.lottie.draw(canvas);
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return -2;
        }
    }

    private static int reqInfoHash(MessageObject messageObject) {
        if (messageObject == null) {
            return 0;
        }
        return Arrays.hashCode(new Object[]{Integer.valueOf(messageObject.currentAccount), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(messageObject.getId())});
    }

    public static boolean isTranscribing(MessageObject messageObject) {
        HashMap<Integer, MessageObject> hashMap = transcribeOperationsByDialogPosition;
        return (hashMap != null && (hashMap.containsValue(messageObject) || transcribeOperationsByDialogPosition.containsKey(Integer.valueOf(reqInfoHash(messageObject))))) || !(transcribeOperationsById == null || messageObject == null || messageObject.messageOwner == null || !transcribeOperationsById.containsKey(Long.valueOf(messageObject.messageOwner.voiceTranscriptionId)));
    }

    private static void transcribePressed(MessageObject messageObject, boolean open) {
        MessageObject messageObject2 = messageObject;
        if (messageObject2 != null && messageObject2.messageOwner != null && messageObject.isSent()) {
            int account = messageObject2.currentAccount;
            long start2 = SystemClock.elapsedRealtime();
            TLRPC.InputPeer peer = MessagesController.getInstance(account).getInputPeer(messageObject2.messageOwner.peer_id);
            long dialogId = DialogObject.getPeerDialogId(peer);
            int messageId = messageObject2.messageOwner.id;
            if (!open) {
                long dialogId2 = dialogId;
                long j = start2;
                HashMap<Integer, MessageObject> hashMap = transcribeOperationsByDialogPosition;
                if (hashMap != null) {
                    hashMap.remove(Integer.valueOf(reqInfoHash(messageObject)));
                }
                messageObject2.messageOwner.voiceTranscriptionOpen = false;
                MessagesStorage.getInstance(account).updateMessageVoiceTranscriptionOpen(dialogId2, messageId, messageObject2.messageOwner);
                AndroidUtilities.runOnUIThread(new TranscribeButton$$ExternalSyntheticLambda1(account, messageObject2));
            } else if (messageObject2.messageOwner.voiceTranscription == null || !messageObject2.messageOwner.voiceTranscriptionFinal) {
                TLRPC.TL_messages_transcribeAudio req = new TLRPC.TL_messages_transcribeAudio();
                req.peer = peer;
                req.msg_id = messageId;
                if (transcribeOperationsByDialogPosition == null) {
                    transcribeOperationsByDialogPosition = new HashMap<>();
                }
                transcribeOperationsByDialogPosition.put(Integer.valueOf(reqInfoHash(messageObject)), messageObject2);
                TranscribeButton$$ExternalSyntheticLambda6 transcribeButton$$ExternalSyntheticLambda6 = r0;
                long j2 = start2;
                TranscribeButton$$ExternalSyntheticLambda6 transcribeButton$$ExternalSyntheticLambda62 = new TranscribeButton$$ExternalSyntheticLambda6(messageObject, start2, account, dialogId, messageId);
                ConnectionsManager.getInstance(account).sendRequest(req, transcribeButton$$ExternalSyntheticLambda6);
                long j3 = dialogId;
            } else {
                messageObject2.messageOwner.voiceTranscriptionOpen = true;
                MessagesStorage.getInstance(account).updateMessageVoiceTranscriptionOpen(dialogId, messageId, messageObject2.messageOwner);
                AndroidUtilities.runOnUIThread(new TranscribeButton$$ExternalSyntheticLambda0(account, messageObject2));
                long j4 = dialogId;
                long j5 = start2;
            }
        }
    }

    static /* synthetic */ void lambda$transcribePressed$4(MessageObject messageObject, long start2, int account, long dialogId, int messageId, TLObject res, TLRPC.TL_error err) {
        String text;
        boolean isFinal;
        MessageObject messageObject2 = messageObject;
        TLObject tLObject = res;
        long id = 0;
        if (tLObject instanceof TLRPC.TL_messages_transcribedAudio) {
            TLRPC.TL_messages_transcribedAudio r = (TLRPC.TL_messages_transcribedAudio) tLObject;
            text = r.text;
            id = r.transcription_id;
            isFinal = !r.pending;
            if (TextUtils.isEmpty(text)) {
                text = !isFinal ? null : "";
            }
            if (transcribeOperationsById == null) {
                transcribeOperationsById = new HashMap<>();
            }
            transcribeOperationsById.put(Long.valueOf(id), messageObject2);
            messageObject2.messageOwner.voiceTranscriptionId = id;
        } else {
            text = "";
            isFinal = true;
        }
        String finalText = text;
        long finalId = id;
        long duration = SystemClock.elapsedRealtime() - start2;
        messageObject2.messageOwner.voiceTranscriptionOpen = true;
        messageObject2.messageOwner.voiceTranscriptionFinal = isFinal;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("Transcription request sent, received final=" + isFinal + " id=" + finalId + " text=" + finalText);
        }
        MessagesStorage.getInstance(account).updateMessageVoiceTranscription(dialogId, messageId, finalText, messageObject2.messageOwner);
        if (isFinal) {
            AndroidUtilities.runOnUIThread(new TranscribeButton$$ExternalSyntheticLambda3(messageObject2, finalId, finalText), Math.max(0, 350 - duration));
        }
    }

    public static boolean finishTranscription(MessageObject messageObject, long transcription_id, String text) {
        MessageObject messageObjectByTranscriptionId = null;
        try {
            HashMap<Long, MessageObject> hashMap = transcribeOperationsById;
            if (hashMap != null && hashMap.containsKey(Long.valueOf(transcription_id))) {
                messageObjectByTranscriptionId = transcribeOperationsById.remove(Long.valueOf(transcription_id));
            }
            if (messageObject == null) {
                messageObject = messageObjectByTranscriptionId;
            }
            if (messageObject != null) {
                if (messageObject.messageOwner != null) {
                    MessageObject finalMessageObject = messageObject;
                    HashMap<Integer, MessageObject> hashMap2 = transcribeOperationsByDialogPosition;
                    if (hashMap2 != null) {
                        hashMap2.remove(Integer.valueOf(reqInfoHash(messageObject)));
                    }
                    messageObject.messageOwner.voiceTranscriptionFinal = true;
                    MessagesStorage.getInstance(messageObject.currentAccount).updateMessageVoiceTranscription(messageObject.getDialogId(), messageObject.getId(), text, messageObject.messageOwner);
                    AndroidUtilities.runOnUIThread(new TranscribeButton$$ExternalSyntheticLambda2(finalMessageObject, transcription_id, text));
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
