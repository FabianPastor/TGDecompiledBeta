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
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_help_premiumPromo;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.PremiumPreviewFragment;

public class VideoScreenPreview extends View implements PagerHeaderView {
    private static final float[] speedScaleVideoTimestamps = {0.02f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.02f};
    boolean attached;
    CellFlickerDrawable.DrawableInterface cellFlickerDrawable;
    int currentAccount;
    boolean fromTop = false;
    ImageReceiver imageReceiver = new ImageReceiver(this);
    Paint phoneFrame1 = new Paint(1);
    Paint phoneFrame2 = new Paint(1);
    boolean play;
    float progress;
    /* access modifiers changed from: private */
    public float roundRadius;
    RoundedBitmapDrawable roundedBitmapDrawable;
    int size;
    SpeedLineParticles$Drawable speedLinesDrawable;
    StarParticlesView.Drawable starDrawable;
    private final SvgHelper.SvgDrawable svgIcon;
    int type;
    boolean visible;

    public VideoScreenPreview(Context context, SvgHelper.SvgDrawable svgDrawable, int i, int i2) {
        super(context);
        new Path();
        this.currentAccount = i;
        this.type = i2;
        this.svgIcon = svgDrawable;
        this.phoneFrame1.setColor(-16777216);
        this.phoneFrame2.setColor(ColorUtils.blendARGB(Theme.getColor("premiumGradient2"), -16777216, 0.5f));
        this.imageReceiver.setLayerNum(Integer.MAX_VALUE);
        setVideo();
        if (i2 == 6) {
            StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(30);
            this.starDrawable = drawable;
            drawable.speedScale = 3.0f;
            drawable.init();
        } else if (i2 == 2) {
            SpeedLineParticles$Drawable speedLineParticles$Drawable = new SpeedLineParticles$Drawable(200);
            this.speedLinesDrawable = speedLineParticles$Drawable;
            speedLineParticles$Drawable.init();
        }
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
                        this.cellFlickerDrawable = new CellFlickerDrawable().getDrawableInterface(this, this.svgIcon);
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
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int measuredWidth = getMeasuredWidth() << (getMeasuredHeight() + 16);
        if (this.size != measuredWidth) {
            this.size = measuredWidth;
            StarParticlesView.Drawable drawable = this.starDrawable;
            if (drawable != null) {
                drawable.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                this.starDrawable.rect.inset((float) AndroidUtilities.dp(30.0f), (float) AndroidUtilities.dp(30.0f));
                this.starDrawable.resetPositions();
            }
            SpeedLineParticles$Drawable speedLineParticles$Drawable = this.speedLinesDrawable;
            if (speedLineParticles$Drawable != null) {
                speedLineParticles$Drawable.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                this.speedLinesDrawable.screenRect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                this.speedLinesDrawable.rect.inset((float) AndroidUtilities.dp(100.0f), (float) AndroidUtilities.dp(100.0f));
                this.speedLinesDrawable.rect.offset(0.0f, ((float) getMeasuredHeight()) * 0.1f);
                this.speedLinesDrawable.resetPositions();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!(this.starDrawable == null && this.speedLinesDrawable == null)) {
            float pow = (float) Math.pow((double) (1.0f - this.progress), 2.0d);
            canvas.save();
            canvas.scale(pow, pow, ((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f);
            StarParticlesView.Drawable drawable = this.starDrawable;
            if (drawable != null) {
                drawable.onDraw(canvas);
            } else if (this.speedLinesDrawable != null) {
                float f = 0.2f;
                if (this.imageReceiver.getAnimation() != null) {
                    float clamp = Utilities.clamp(((float) this.imageReceiver.getAnimation().getLastFrameTimestamp()) / ((float) this.imageReceiver.getAnimation().getDurationMs()), 1.0f, 0.0f);
                    float[] fArr = speedScaleVideoTimestamps;
                    float length = 1.0f / ((float) (fArr.length - 1));
                    int i = (int) (clamp / length);
                    int i2 = i + 1;
                    float f2 = (clamp - (((float) i) * length)) / length;
                    if (i2 < fArr.length) {
                        f = (fArr[i] * (1.0f - f2)) + (fArr[i2] * f2);
                    } else {
                        f = fArr[i];
                    }
                }
                SpeedLineParticles$Drawable speedLineParticles$Drawable = this.speedLinesDrawable;
                speedLineParticles$Drawable.speedScale = (((1.0f - Utilities.clamp(this.progress / 0.1f, 1.0f, 0.0f)) * 0.9f) + 0.1f) * 150.0f * f;
                speedLineParticles$Drawable.onDraw(canvas);
            }
            canvas.restore();
            invalidate();
        }
        float measuredHeight = (float) ((int) (((float) getMeasuredHeight()) * 0.9f));
        float measuredWidth = (((float) getMeasuredWidth()) - (0.671f * measuredHeight)) / 2.0f;
        float f3 = 0.0671f * measuredHeight;
        this.roundRadius = f3;
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(measuredWidth, -f3, ((float) getMeasuredWidth()) - measuredWidth, measuredHeight);
        } else {
            AndroidUtilities.rectTmp.set(measuredWidth, ((float) getMeasuredHeight()) - measuredHeight, ((float) getMeasuredWidth()) - measuredWidth, ((float) getMeasuredHeight()) + this.roundRadius);
        }
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.inset((float) (-AndroidUtilities.dp(3.0f)), (float) (-AndroidUtilities.dp(3.0f)));
        rectF.inset((float) (-AndroidUtilities.dp(3.0f)), (float) (-AndroidUtilities.dp(3.0f)));
        canvas.drawRoundRect(rectF, this.roundRadius + ((float) AndroidUtilities.dp(3.0f)), this.roundRadius + ((float) AndroidUtilities.dp(3.0f)), this.phoneFrame2);
        rectF.inset((float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f));
        float f4 = this.roundRadius;
        canvas.drawRoundRect(rectF, f4, f4, this.phoneFrame1);
        if (this.fromTop) {
            rectF.set(measuredWidth, 0.0f, ((float) getMeasuredWidth()) - measuredWidth, measuredHeight);
        } else {
            rectF.set(measuredWidth, ((float) getMeasuredHeight()) - measuredHeight, ((float) getMeasuredWidth()) - measuredWidth, (float) getMeasuredHeight());
        }
        float dp = this.roundRadius - ((float) AndroidUtilities.dp(3.0f));
        this.roundRadius = dp;
        RoundedBitmapDrawable roundedBitmapDrawable2 = this.roundedBitmapDrawable;
        if (roundedBitmapDrawable2 != null) {
            roundedBitmapDrawable2.setCornerRadius(dp);
        }
        CellFlickerDrawable.DrawableInterface drawableInterface = this.cellFlickerDrawable;
        if (drawableInterface != null) {
            drawableInterface.radius = this.roundRadius;
        }
        if (this.fromTop) {
            ImageReceiver imageReceiver2 = this.imageReceiver;
            float f5 = this.roundRadius;
            imageReceiver2.setRoundRadius(0, 0, (int) f5, (int) f5);
        } else {
            ImageReceiver imageReceiver3 = this.imageReceiver;
            float f6 = this.roundRadius;
            imageReceiver3.setRoundRadius((int) f6, (int) f6, 0, 0);
        }
        this.imageReceiver.setImageCoords(rectF.left, rectF.top, rectF.width(), rectF.height());
        this.imageReceiver.draw(canvas);
        if (!this.fromTop) {
            canvas.drawCircle(this.imageReceiver.getCenterX(), this.imageReceiver.getImageY() + ((float) AndroidUtilities.dp(12.0f)), (float) AndroidUtilities.dp(6.0f), this.phoneFrame1);
        }
    }

    public void setOffset(float f) {
        boolean z = true;
        if (f < 0.0f) {
            float measuredWidth = (-f) / ((float) getMeasuredWidth());
            setAlpha((Utilities.clamp(1.0f - measuredWidth, 1.0f, 0.0f) * 0.5f) + 0.5f);
            setRotationY(50.0f * measuredWidth);
            invalidate();
            if (this.fromTop) {
                setTranslationY(((float) (-getMeasuredHeight())) * 0.3f * measuredWidth);
            } else {
                setTranslationY(((float) getMeasuredHeight()) * 0.3f * measuredWidth);
            }
            this.progress = Math.abs(measuredWidth);
            if (measuredWidth >= 1.0f) {
                z = false;
            }
        } else {
            float measuredWidth2 = (-f) / ((float) getMeasuredWidth());
            Utilities.clamp(measuredWidth2 + 1.0f, 1.0f, 0.0f);
            invalidate();
            setRotationY(50.0f * measuredWidth2);
            if (this.fromTop) {
                setTranslationY(((float) getMeasuredHeight()) * 0.3f * measuredWidth2);
            } else {
                setTranslationY(((float) (-getMeasuredHeight())) * 0.3f * measuredWidth2);
            }
            if (measuredWidth2 <= -1.0f) {
                z = false;
            }
            this.progress = Math.abs(measuredWidth2);
        }
        if (z != this.visible) {
            this.visible = z;
            updateAttachState();
        }
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
