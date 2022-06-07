package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.tgnet.TLRPC$TL_attachMenuBot;
import org.telegram.tgnet.TLRPC$TL_attachMenuBotIcon;

public class AttachBotIntroTopView extends View {
    private Drawable attachDrawable;
    private Paint backgroundPaint = new Paint(1);
    private ImageReceiver imageReceiver;
    private Paint paint = new Paint(1);

    public AttachBotIntroTopView(Context context) {
        super(context);
        ImageReceiver imageReceiver2 = new ImageReceiver(this);
        this.imageReceiver = imageReceiver2;
        imageReceiver2.setAlpha(0.0f);
        this.imageReceiver.setDelegate(new AttachBotIntroTopView$$ExternalSyntheticLambda1(this));
        this.attachDrawable = ContextCompat.getDrawable(context, NUM).mutate().getConstantState().newDrawable();
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setStrokeCap(Paint.Cap.ROUND);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(ImageReceiver imageReceiver2, boolean z, boolean z2, boolean z3) {
        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(150);
        duration.addUpdateListener(new AttachBotIntroTopView$$ExternalSyntheticLambda0(this));
        duration.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ValueAnimator valueAnimator) {
        this.imageReceiver.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        invalidate();
    }

    public void setAttachBot(TLRPC$TL_attachMenuBot tLRPC$TL_attachMenuBot) {
        TLRPC$TL_attachMenuBotIcon staticAttachMenuBotIcon = MediaDataController.getStaticAttachMenuBotIcon(tLRPC$TL_attachMenuBot);
        if (staticAttachMenuBotIcon != null) {
            this.imageReceiver.setImage(ImageLocation.getForDocument(staticAttachMenuBotIcon.icon), "42_42", DocumentObject.getSvgThumb(staticAttachMenuBotIcon.icon, "dialogTextGray2", 1.0f), "svg", tLRPC$TL_attachMenuBot, 0);
        }
    }

    public void setBackgroundColor(int i) {
        this.backgroundPaint.setColor(i);
    }

    public void setColor(int i) {
        this.attachDrawable.setColorFilter(i, PorterDuff.Mode.SRC_IN);
        this.paint.setColor(i);
        this.imageReceiver.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(0.0f, 0.0f, (float) getWidth(), (float) (getHeight() + AndroidUtilities.dp(6.0f)));
        canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.backgroundPaint);
        this.imageReceiver.setImageCoords((((float) getWidth()) / 2.0f) - ((float) AndroidUtilities.dp(66.0f)), (((float) getHeight()) / 2.0f) - (((float) AndroidUtilities.dp(42.0f)) / 2.0f), (float) AndroidUtilities.dp(42.0f), (float) AndroidUtilities.dp(42.0f));
        this.imageReceiver.draw(canvas);
        Canvas canvas2 = canvas;
        canvas2.drawLine((((float) getWidth()) / 2.0f) - ((float) AndroidUtilities.dp(8.0f)), ((float) getHeight()) / 2.0f, (((float) getWidth()) / 2.0f) + ((float) AndroidUtilities.dp(8.0f)), ((float) getHeight()) / 2.0f, this.paint);
        canvas2.drawLine(((float) getWidth()) / 2.0f, (((float) getHeight()) / 2.0f) - ((float) AndroidUtilities.dp(8.0f)), ((float) getWidth()) / 2.0f, (((float) getHeight()) / 2.0f) + ((float) AndroidUtilities.dp(8.0f)), this.paint);
        this.attachDrawable.setBounds((getWidth() / 2) + AndroidUtilities.dp(24.0f), (getHeight() / 2) - (AndroidUtilities.dp(42.0f) / 2), (getWidth() / 2) + AndroidUtilities.dp(66.0f), (getHeight() / 2) + (AndroidUtilities.dp(42.0f) / 2));
        this.attachDrawable.draw(canvas);
    }
}
