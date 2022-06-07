package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_help_premiumPromo;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.PremiumPreviewFragment;

public class VideoScreenPreview extends View implements PagerHeaderView {
    boolean attached;
    CellFlickerDrawable.DrawableInterface cellFlickerDrawable;
    int currentAccount;
    boolean fromTop = false;
    ImageReceiver imageReceiver = new ImageReceiver(this);
    Paint phoneFrame1 = new Paint(1);
    Paint phoneFrame2 = new Paint(1);
    boolean play;
    /* access modifiers changed from: private */
    public float roundRadius;
    RoundedBitmapDrawable roundedBitmapDrawable;
    int type;
    boolean visible;

    public VideoScreenPreview(Context context, int i, int i2) {
        super(context);
        new Path();
        this.currentAccount = i;
        this.type = i2;
        this.phoneFrame1.setColor(-16777216);
        this.phoneFrame2.setColor(ColorUtils.blendARGB(Theme.getColor("premiumGradient2"), -16777216, 0.5f));
        this.imageReceiver.setLayerNum(Integer.MAX_VALUE);
        setVideo();
    }

    private void setVideo() {
        TLRPC$TL_help_premiumPromo premiumPromo = MediaDataController.getInstance(this.currentAccount).getPremiumPromo();
        String featureTypeToServerString = PremiumPreviewFragment.featureTypeToServerString(this.type);
        if (premiumPromo != null) {
            int i = -1;
            int i2 = 0;
            while (true) {
                if (i2 >= premiumPromo.video_sections.size()) {
                    break;
                } else if (premiumPromo.video_sections.get(i2).equals(featureTypeToServerString)) {
                    i = i2;
                    break;
                } else {
                    i2++;
                }
            }
            if (i >= 0) {
                TLRPC$Document tLRPC$Document = premiumPromo.videos.get(i);
                AnonymousClass1 r7 = null;
                for (int i3 = 0; i3 < tLRPC$Document.thumbs.size(); i3++) {
                    if (tLRPC$Document.thumbs.get(i3) instanceof TLRPC$TL_photoStrippedSize) {
                        this.roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), ImageLoader.getStrippedPhotoBitmap(tLRPC$Document.thumbs.get(i3).bytes, "b"));
                        this.cellFlickerDrawable = new CellFlickerDrawable().getDrawableInterface(this);
                        AnonymousClass1 r1 = new CombinedDrawable(this.roundedBitmapDrawable, this.cellFlickerDrawable) {
                            public void setBounds(int i, int i2, int i3, int i4) {
                                VideoScreenPreview videoScreenPreview = VideoScreenPreview.this;
                                if (videoScreenPreview.fromTop) {
                                    super.setBounds(i, (int) (((float) i2) - videoScreenPreview.roundRadius), i3, i4);
                                } else {
                                    super.setBounds(i, i2, i3, (int) (((float) i4) + videoScreenPreview.roundRadius));
                                }
                            }
                        };
                        r1.setFullsize(true);
                        r7 = r1;
                    }
                }
                this.imageReceiver.setImage(ImageLocation.getForDocument(tLRPC$Document), "g", r7, (String) null, (Object) null, 1);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float measuredHeight = (float) ((int) (((float) getMeasuredHeight()) * 0.9f));
        float measuredWidth = (((float) getMeasuredWidth()) - (0.671f * measuredHeight)) / 2.0f;
        float f = 0.0671f * measuredHeight;
        this.roundRadius = f;
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(measuredWidth, -f, ((float) getMeasuredWidth()) - measuredWidth, measuredHeight);
        } else {
            AndroidUtilities.rectTmp.set(measuredWidth, ((float) getMeasuredHeight()) - measuredHeight, ((float) getMeasuredWidth()) - measuredWidth, ((float) getMeasuredHeight()) + this.roundRadius);
        }
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.inset((float) (-AndroidUtilities.dp(3.0f)), (float) (-AndroidUtilities.dp(3.0f)));
        rectF.inset((float) (-AndroidUtilities.dp(3.0f)), (float) (-AndroidUtilities.dp(3.0f)));
        canvas.drawRoundRect(rectF, this.roundRadius + ((float) AndroidUtilities.dp(3.0f)), this.roundRadius + ((float) AndroidUtilities.dp(3.0f)), this.phoneFrame2);
        rectF.inset((float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f));
        float f2 = this.roundRadius;
        canvas.drawRoundRect(rectF, f2, f2, this.phoneFrame1);
        if (this.fromTop) {
            rectF.set(measuredWidth, 0.0f, ((float) getMeasuredWidth()) - measuredWidth, measuredHeight);
        } else {
            rectF.set(measuredWidth, ((float) getMeasuredHeight()) - measuredHeight, ((float) getMeasuredWidth()) - measuredWidth, (float) getMeasuredHeight());
        }
        float dp = this.roundRadius - ((float) AndroidUtilities.dp(3.0f));
        this.roundRadius = dp;
        this.roundedBitmapDrawable.setCornerRadius(dp);
        CellFlickerDrawable.DrawableInterface drawableInterface = this.cellFlickerDrawable;
        float f3 = this.roundRadius;
        drawableInterface.radius = f3;
        if (this.fromTop) {
            this.imageReceiver.setRoundRadius(0, 0, (int) f3, (int) f3);
        } else {
            this.imageReceiver.setRoundRadius((int) f3, (int) f3, 0, 0);
        }
        this.imageReceiver.setImageCoords(rectF.left, rectF.top, rectF.width(), rectF.height());
        this.imageReceiver.draw(canvas);
        if (!this.fromTop) {
            canvas.drawCircle(this.imageReceiver.getCenterX(), this.imageReceiver.getImageY() + ((float) AndroidUtilities.dp(12.0f)), (float) AndroidUtilities.dp(6.0f), this.phoneFrame1);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0086, code lost:
        if (r8 > -1.0f) goto L_0x0088;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x004b, code lost:
        if (r8 < 1.0f) goto L_0x0088;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setOffset(float r8) {
        /*
            r7 = this;
            r0 = 1
            r1 = 0
            r2 = 1112014848(0x42480000, float:50.0)
            r3 = 0
            r4 = 1050253722(0x3e99999a, float:0.3)
            r5 = 1065353216(0x3var_, float:1.0)
            int r6 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r6 >= 0) goto L_0x0050
            float r8 = -r8
            int r6 = r7.getMeasuredWidth()
            float r6 = (float) r6
            float r8 = r8 / r6
            float r6 = r5 - r8
            float r3 = org.telegram.messenger.Utilities.clamp(r6, r5, r3)
            r6 = 1056964608(0x3var_, float:0.5)
            float r3 = r3 * r6
            float r3 = r3 + r6
            r7.setAlpha(r3)
            float r2 = r2 * r8
            r7.setRotationY(r2)
            r7.invalidate()
            boolean r2 = r7.fromTop
            if (r2 == 0) goto L_0x003d
            int r2 = r7.getMeasuredHeight()
            int r2 = -r2
            float r2 = (float) r2
            float r2 = r2 * r4
            float r2 = r2 * r8
            r7.setTranslationY(r2)
            goto L_0x0049
        L_0x003d:
            int r2 = r7.getMeasuredHeight()
            float r2 = (float) r2
            float r2 = r2 * r4
            float r2 = r2 * r8
            r7.setTranslationY(r2)
        L_0x0049:
            int r8 = (r8 > r5 ? 1 : (r8 == r5 ? 0 : -1))
            if (r8 >= 0) goto L_0x004e
            goto L_0x0088
        L_0x004e:
            r0 = 0
            goto L_0x0088
        L_0x0050:
            float r8 = -r8
            int r6 = r7.getMeasuredWidth()
            float r6 = (float) r6
            float r8 = r8 / r6
            float r6 = r8 + r5
            org.telegram.messenger.Utilities.clamp(r6, r5, r3)
            r7.invalidate()
            float r2 = r2 * r8
            r7.setRotationY(r2)
            boolean r2 = r7.fromTop
            if (r2 == 0) goto L_0x0075
            int r2 = r7.getMeasuredHeight()
            float r2 = (float) r2
            float r2 = r2 * r4
            float r2 = r2 * r8
            r7.setTranslationY(r2)
            goto L_0x0082
        L_0x0075:
            int r2 = r7.getMeasuredHeight()
            int r2 = -r2
            float r2 = (float) r2
            float r2 = r2 * r4
            float r2 = r2 * r8
            r7.setTranslationY(r2)
        L_0x0082:
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            int r8 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r8 <= 0) goto L_0x004e
        L_0x0088:
            boolean r8 = r7.visible
            if (r0 == r8) goto L_0x0091
            r7.visible = r0
            r7.updateAttachState()
        L_0x0091:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.VideoScreenPreview.setOffset(float):void");
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        updateAttachState();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
        updateAttachState();
    }

    private void updateAttachState() {
        boolean z = this.visible && this.attached;
        if (this.play != z) {
            this.play = z;
            if (z) {
                this.imageReceiver.onAttachedToWindow();
            } else {
                this.imageReceiver.onDetachedFromWindow();
            }
        }
    }
}
