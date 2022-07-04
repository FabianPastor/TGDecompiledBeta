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
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_transcribeAudio;
import org.telegram.tgnet.TLRPC$TL_messages_transcribedAudio;
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
    private boolean pressed = false;
    private Path progressClipPath;
    private int rippleColor;
    private SeekBarWaveform seekBar;
    private float[] segments;
    private Drawable selectorDrawable;
    private boolean shouldBeOpen;
    private long start = SystemClock.elapsedRealtime();
    private Paint strokePaint;

    public TranscribeButton(ChatMessageCell chatMessageCell, SeekBarWaveform seekBarWaveform) {
        this.parent = chatMessageCell;
        this.seekBar = seekBarWaveform;
        this.bounds = new Rect(0, 0, AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
        Rect rect = new Rect(this.bounds);
        this.pressBounds = rect;
        rect.inset(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "transcribe_out", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
        this.outIconDrawable = rLottieDrawable;
        rLottieDrawable.setCurrentFrame(0);
        this.outIconDrawable.setCallback(chatMessageCell);
        this.outIconDrawable.addParentView(chatMessageCell);
        this.outIconDrawable.setOnFinishCallback(new TranscribeButton$$ExternalSyntheticLambda5(this), 19);
        this.outIconDrawable.setAllowDecodeSingleFrame(true);
        RLottieDrawable rLottieDrawable2 = new RLottieDrawable(NUM, "transcribe_in", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
        this.inIconDrawable = rLottieDrawable2;
        rLottieDrawable2.setCurrentFrame(0);
        this.inIconDrawable.setCallback(chatMessageCell);
        this.inIconDrawable.addParentView(chatMessageCell);
        this.inIconDrawable.setOnFinishCallback(new TranscribeButton$$ExternalSyntheticLambda4(this), 19);
        this.inIconDrawable.setAllowDecodeSingleFrame(true);
        this.isOpen = false;
        this.shouldBeOpen = false;
        this.premium = AccountInstance.getInstance(chatMessageCell.getMessageObject().currentAccount).getUserConfig().isPremium();
        this.loadingFloat = new AnimatedFloat((View) chatMessageCell, 250, (TimeInterpolator) CubicBezierInterpolator.EASE_OUT_QUINT);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.outIconDrawable.stop();
        this.inIconDrawable.stop();
        this.shouldBeOpen = true;
        this.isOpen = true;
        this.inIconDrawable.setCurrentFrame(0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        this.inIconDrawable.stop();
        this.outIconDrawable.stop();
        this.shouldBeOpen = false;
        this.isOpen = false;
        this.outIconDrawable.setCurrentFrame(0);
    }

    public void setLoading(boolean z, boolean z2) {
        this.loading = z;
        this.seekBar.setLoading(z);
        float f = 0.0f;
        if (!z2) {
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

    public void setOpen(boolean z, boolean z2) {
        boolean z3 = this.shouldBeOpen;
        this.shouldBeOpen = z;
        if (!z2) {
            this.isOpen = z;
            this.inIconDrawable.stop();
            this.outIconDrawable.stop();
            this.inIconDrawable.setCurrentFrame(0);
            this.outIconDrawable.setCurrentFrame(0);
        } else if (z && !z3) {
            this.isOpen = false;
            this.inIconDrawable.setCurrentFrame(0);
            this.outIconDrawable.setCurrentFrame(0);
            this.outIconDrawable.start();
        } else if (!z && z3) {
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

    public boolean onTouch(int i, float f, float f2) {
        boolean z;
        if (i == 1 || i == 3) {
            if (!this.pressed || i != 1) {
                this.pressed = false;
                return false;
            }
            boolean z2 = this.shouldBeOpen;
            boolean z3 = !z2;
            if (!z2) {
                z = !this.loading;
                if (this.premium && this.parent.getMessageObject().isSent()) {
                    setLoading(true, true);
                }
            } else {
                setOpen(false, true);
                setLoading(false, true);
                z = true;
            }
            if (Build.VERSION.SDK_INT >= 21) {
                Drawable drawable = this.selectorDrawable;
                if (drawable instanceof RippleDrawable) {
                    drawable.setState(StateSet.NOTHING);
                    this.parent.invalidate();
                }
            }
            this.pressed = false;
            if (z) {
                if (this.premium || !z3) {
                    transcribePressed(this.parent.getMessageObject(), z3);
                } else if (this.parent.getDelegate() != null) {
                    this.parent.getDelegate().needShowPremiumFeatures(PremiumPreviewFragment.featureTypeToServerString(8));
                }
            }
            return true;
        } else if (!this.pressBounds.contains((int) f, (int) f2)) {
            return false;
        } else {
            if (i == 0) {
                this.pressed = true;
            }
            if (this.pressed && Build.VERSION.SDK_INT >= 21) {
                Drawable drawable2 = this.selectorDrawable;
                if (drawable2 instanceof RippleDrawable) {
                    drawable2.setHotspot(f, f2);
                    this.selectorDrawable.setState(pressedState);
                    this.parent.invalidate();
                }
            }
            return true;
        }
    }

    public void setColor(boolean z, int i, int i2) {
        boolean z2 = this.color != i;
        this.color = i;
        this.iconColor = i;
        int alphaComponent = ColorUtils.setAlphaComponent(i, (int) (((float) Color.alpha(i)) * 0.156f));
        this.backgroundColor = alphaComponent;
        this.rippleColor = Theme.blendOver(alphaComponent, ColorUtils.setAlphaComponent(i, (int) (((float) Color.alpha(i)) * (Theme.isCurrentThemeDark() ? 0.3f : 0.2f))));
        if (this.backgroundPaint == null) {
            this.backgroundPaint = new Paint();
        }
        this.backgroundPaint.setColor(this.backgroundColor);
        if (z2 || this.selectorDrawable == null) {
            Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), 0, this.rippleColor);
            this.selectorDrawable = createSimpleSelectorRoundRectDrawable;
            createSimpleSelectorRoundRectDrawable.setCallback(this.parent);
        }
        if (z2) {
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
        this.strokePaint.setColor(i);
    }

    public void draw(Canvas canvas) {
        this.bounds.set(0, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(27.0f));
        this.pressBounds.set(this.bounds.left - AndroidUtilities.dp(8.0f), this.bounds.top - AndroidUtilities.dp(8.0f), this.bounds.right + AndroidUtilities.dp(8.0f), this.bounds.bottom + AndroidUtilities.dp(8.0f));
        if (this.boundsPath == null) {
            this.boundsPath = new Path();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(this.bounds);
            this.boundsPath.addRoundRect(rectF, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), Path.Direction.CW);
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
        float f2 = this.loadingFloat.set(this.loading ? 1.0f : 0.0f);
        if (f2 > 0.0f) {
            float[] segments2 = getSegments((long) (((float) (SystemClock.elapsedRealtime() - this.start)) * 0.75f));
            canvas.save();
            if (this.progressClipPath == null) {
                this.progressClipPath = new Path();
            }
            this.progressClipPath.reset();
            RectF rectF2 = AndroidUtilities.rectTmp;
            rectF2.set(this.pressBounds);
            float max = Math.max(40.0f * f2, segments2[1] - segments2[0]);
            Path path = this.progressClipPath;
            float f3 = segments2[0];
            float f4 = (1.0f - f2) * max;
            if (this.loading) {
                f = 0.0f;
            }
            path.addArc(rectF2, f3 + (f4 * f), max * f2);
            this.progressClipPath.lineTo(rectF2.centerX(), rectF2.centerY());
            this.progressClipPath.close();
            canvas.clipPath(this.progressClipPath);
            rectF2.set(this.bounds);
            this.strokePaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
            canvas.drawRoundRect(rectF2, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), this.strokePaint);
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

    private float[] getSegments(long j) {
        if (this.segments == null) {
            this.segments = new float[2];
        }
        long j2 = j % 5400;
        float[] fArr = this.segments;
        float f = ((float) (1520 * j2)) / 5400.0f;
        fArr[0] = f - 20.0f;
        fArr[1] = f;
        for (int i = 0; i < 4; i++) {
            int i2 = i * 1350;
            float[] fArr2 = this.segments;
            fArr2[1] = fArr2[1] + (this.interpolator.getInterpolation(((float) (j2 - ((long) i2))) / 667.0f) * 250.0f);
            float[] fArr3 = this.segments;
            fArr3[0] = fArr3[0] + (this.interpolator.getInterpolation(((float) (j2 - ((long) (i2 + 667)))) / 667.0f) * 250.0f);
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
                float r0 = r0 * r5
                int r0 = (int) r0
                int r0 = r0 + r2
                r3.setBounds(r1, r2, r4, r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranscribeButton.LoadingPointsSpan.<init>():void");
        }

        public void updateDrawState(TextPaint textPaint) {
            float textSize = textPaint.getTextSize() * 0.89f;
            int i = (int) (0.02f * textSize);
            getDrawable().setBounds(0, i, (int) textSize, ((int) (textSize * 1.25f)) + i);
            super.updateDrawState(textPaint);
        }
    }

    private static class LoadingPointsDrawable extends Drawable {
        private int lastColor;
        private RLottieDrawable lottie;
        private Paint paint;

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public LoadingPointsDrawable(TextPaint textPaint) {
            this.paint = textPaint;
            float textSize = textPaint.getTextSize() * 0.89f;
            AnonymousClass1 r0 = new RLottieDrawable(this, NUM, "dots_loading", (int) textSize, (int) (textSize * 1.25f)) {
                /* access modifiers changed from: protected */
                public boolean hasParentView() {
                    return true;
                }
            };
            this.lottie = r0;
            r0.setAutoRepeat(1);
            this.lottie.setCurrentFrame((int) ((((float) SystemClock.elapsedRealtime()) / 16.0f) % 60.0f));
            this.lottie.setAllowDecodeSingleFrame(true);
            this.lottie.start();
        }

        public void setColor(int i) {
            this.lottie.beginApplyLayerColors();
            this.lottie.setLayerColor("Comp 1.**", i);
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
    }

    private static int reqInfoHash(MessageObject messageObject) {
        if (messageObject == null) {
            return 0;
        }
        return Arrays.hashCode(new Object[]{Integer.valueOf(messageObject.currentAccount), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(messageObject.getId())});
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001a, code lost:
        r0 = transcribeOperationsById;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0020, code lost:
        r3 = r3.messageOwner;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isTranscribing(org.telegram.messenger.MessageObject r3) {
        /*
            java.util.HashMap<java.lang.Integer, org.telegram.messenger.MessageObject> r0 = transcribeOperationsByDialogPosition
            if (r0 == 0) goto L_0x001a
            boolean r0 = r0.containsValue(r3)
            if (r0 != 0) goto L_0x0030
            java.util.HashMap<java.lang.Integer, org.telegram.messenger.MessageObject> r0 = transcribeOperationsByDialogPosition
            int r1 = reqInfoHash(r3)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            boolean r0 = r0.containsKey(r1)
            if (r0 != 0) goto L_0x0030
        L_0x001a:
            java.util.HashMap<java.lang.Long, org.telegram.messenger.MessageObject> r0 = transcribeOperationsById
            if (r0 == 0) goto L_0x0032
            if (r3 == 0) goto L_0x0032
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            if (r3 == 0) goto L_0x0032
            long r1 = r3.voiceTranscriptionId
            java.lang.Long r3 = java.lang.Long.valueOf(r1)
            boolean r3 = r0.containsKey(r3)
            if (r3 == 0) goto L_0x0032
        L_0x0030:
            r3 = 1
            goto L_0x0033
        L_0x0032:
            r3 = 0
        L_0x0033:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranscribeButton.isTranscribing(org.telegram.messenger.MessageObject):boolean");
    }

    private static void transcribePressed(MessageObject messageObject, boolean z) {
        if (messageObject != null && messageObject.messageOwner != null && messageObject.isSent()) {
            int i = messageObject.currentAccount;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            TLRPC$InputPeer inputPeer = MessagesController.getInstance(i).getInputPeer(messageObject.messageOwner.peer_id);
            long peerDialogId = DialogObject.getPeerDialogId(inputPeer);
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            int i2 = tLRPC$Message.id;
            if (!z) {
                HashMap<Integer, MessageObject> hashMap = transcribeOperationsByDialogPosition;
                if (hashMap != null) {
                    hashMap.remove(Integer.valueOf(reqInfoHash(messageObject)));
                }
                messageObject.messageOwner.voiceTranscriptionOpen = false;
                MessagesStorage.getInstance(i).updateMessageVoiceTranscriptionOpen(peerDialogId, i2, messageObject.messageOwner);
                AndroidUtilities.runOnUIThread(new TranscribeButton$$ExternalSyntheticLambda0(i, messageObject));
            } else if (tLRPC$Message.voiceTranscription == null || !tLRPC$Message.voiceTranscriptionFinal) {
                TLRPC$TL_messages_transcribeAudio tLRPC$TL_messages_transcribeAudio = new TLRPC$TL_messages_transcribeAudio();
                tLRPC$TL_messages_transcribeAudio.peer = inputPeer;
                tLRPC$TL_messages_transcribeAudio.msg_id = i2;
                if (transcribeOperationsByDialogPosition == null) {
                    transcribeOperationsByDialogPosition = new HashMap<>();
                }
                transcribeOperationsByDialogPosition.put(Integer.valueOf(reqInfoHash(messageObject)), messageObject);
                ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_transcribeAudio, new TranscribeButton$$ExternalSyntheticLambda6(messageObject, elapsedRealtime, i, peerDialogId, i2));
            } else {
                tLRPC$Message.voiceTranscriptionOpen = true;
                MessagesStorage.getInstance(i).updateMessageVoiceTranscriptionOpen(peerDialogId, i2, messageObject.messageOwner);
                AndroidUtilities.runOnUIThread(new TranscribeButton$$ExternalSyntheticLambda1(i, messageObject));
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$transcribePressed$2(int i, MessageObject messageObject) {
        NotificationCenter instance = NotificationCenter.getInstance(i);
        int i2 = NotificationCenter.voiceTranscriptionUpdate;
        Boolean bool = Boolean.TRUE;
        instance.postNotificationName(i2, messageObject, null, null, bool, bool);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$transcribePressed$4(MessageObject messageObject, long j, int i, long j2, int i2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        long j3;
        boolean z;
        MessageObject messageObject2 = messageObject;
        TLObject tLObject2 = tLObject;
        String str = "";
        if (tLObject2 instanceof TLRPC$TL_messages_transcribedAudio) {
            TLRPC$TL_messages_transcribedAudio tLRPC$TL_messages_transcribedAudio = (TLRPC$TL_messages_transcribedAudio) tLObject2;
            String str2 = tLRPC$TL_messages_transcribedAudio.text;
            long j4 = tLRPC$TL_messages_transcribedAudio.transcription_id;
            z = !tLRPC$TL_messages_transcribedAudio.pending;
            if (!TextUtils.isEmpty(str2)) {
                str = str2;
            } else if (!z) {
                str = null;
            }
            if (transcribeOperationsById == null) {
                transcribeOperationsById = new HashMap<>();
            }
            transcribeOperationsById.put(Long.valueOf(j4), messageObject2);
            messageObject2.messageOwner.voiceTranscriptionId = j4;
            j3 = j4;
        } else {
            j3 = 0;
            z = true;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime() - j;
        TLRPC$Message tLRPC$Message = messageObject2.messageOwner;
        tLRPC$Message.voiceTranscriptionOpen = true;
        tLRPC$Message.voiceTranscriptionFinal = z;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("Transcription request sent, received final=" + z + " id=" + j3 + " text=" + str);
        }
        MessagesStorage.getInstance(i).updateMessageVoiceTranscription(j2, i2, str, messageObject2.messageOwner);
        if (z) {
            AndroidUtilities.runOnUIThread(new TranscribeButton$$ExternalSyntheticLambda3(messageObject2, j3, str), Math.max(0, 350 - elapsedRealtime));
        }
    }

    public static boolean finishTranscription(MessageObject messageObject, long j, String str) {
        MessageObject messageObject2 = null;
        try {
            HashMap<Long, MessageObject> hashMap = transcribeOperationsById;
            if (hashMap != null && hashMap.containsKey(Long.valueOf(j))) {
                messageObject2 = transcribeOperationsById.remove(Long.valueOf(j));
            }
            if (messageObject == null) {
                messageObject = messageObject2;
            }
            if (messageObject != null) {
                if (messageObject.messageOwner != null) {
                    HashMap<Integer, MessageObject> hashMap2 = transcribeOperationsByDialogPosition;
                    if (hashMap2 != null) {
                        hashMap2.remove(Integer.valueOf(reqInfoHash(messageObject)));
                    }
                    messageObject.messageOwner.voiceTranscriptionFinal = true;
                    MessagesStorage.getInstance(messageObject.currentAccount).updateMessageVoiceTranscription(messageObject.getDialogId(), messageObject.getId(), str, messageObject.messageOwner);
                    AndroidUtilities.runOnUIThread(new TranscribeButton$$ExternalSyntheticLambda2(messageObject, j, str));
                    return true;
                }
            }
        } catch (Exception unused) {
        }
        return false;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$finishTranscription$6(MessageObject messageObject, long j, String str) {
        NotificationCenter instance = NotificationCenter.getInstance(messageObject.currentAccount);
        int i = NotificationCenter.voiceTranscriptionUpdate;
        Boolean bool = Boolean.TRUE;
        instance.postNotificationName(i, messageObject, Long.valueOf(j), str, bool, bool);
    }
}
