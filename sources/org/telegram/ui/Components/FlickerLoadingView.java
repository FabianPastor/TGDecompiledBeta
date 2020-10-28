package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class FlickerLoadingView extends View {
    int color0;
    int color1;
    LinearGradient gradient;
    int gradientWidth;
    private boolean isSingleCell;
    private long lastUpdateTime;
    private Matrix matrix = new Matrix();
    Paint paint = new Paint();
    RectF rectF = new RectF();
    private boolean showDate = true;
    private int totalTranslation;
    int viewType;

    public int getColumnsCount() {
        return 2;
    }

    public void setViewType(int i) {
        this.viewType = i;
        invalidate();
    }

    public void setIsSingleCell(boolean z) {
        this.isSingleCell = z;
    }

    public int getViewType() {
        return this.viewType;
    }

    public FlickerLoadingView(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.isSingleCell) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(getCellHeight(), NUM));
        } else {
            super.onMeasure(i, i2);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        int color = Theme.getColor("dialogBackground");
        int color2 = Theme.getColor("windowBackgroundGray");
        int i = 1;
        int i2 = 0;
        if (!(this.color1 == color2 && this.color0 == color)) {
            this.color0 = color;
            this.color1 = color2;
            if (this.isSingleCell) {
                int dp = AndroidUtilities.dp(200.0f);
                this.gradientWidth = dp;
                this.gradient = new LinearGradient(0.0f, 0.0f, (float) dp, 0.0f, new int[]{color2, color, color, color2}, new float[]{0.0f, 0.4f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
            } else {
                int dp2 = AndroidUtilities.dp(600.0f);
                this.gradientWidth = dp2;
                this.gradient = new LinearGradient(0.0f, 0.0f, 0.0f, (float) dp2, new int[]{color2, color, color, color2}, new float[]{0.0f, 0.4f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
            }
            this.paint.setShader(this.gradient);
        }
        float f = 140.0f;
        if (getViewType() == 1) {
            while (i2 < getMeasuredHeight()) {
                int dp3 = AndroidUtilities.dp(25.0f);
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(9.0f) + dp3)), (float) ((AndroidUtilities.dp(78.0f) >> i) + i2), (float) dp3, this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(20.0f) + i2), (float) AndroidUtilities.dp(f), (float) (i2 + AndroidUtilities.dp(28.0f)));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(42.0f) + i2), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(50.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + i2), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(28.0f) + i2));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i2 += getCellHeight();
                if (this.isSingleCell) {
                    break;
                }
                i = 1;
                f = 140.0f;
            }
        } else if (getViewType() == 2) {
            int measuredWidth = (getMeasuredWidth() - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount();
            int i3 = 0;
            while (i3 < getMeasuredHeight()) {
                for (int i4 = 0; i4 < getColumnsCount(); i4++) {
                    int dp4 = (AndroidUtilities.dp(2.0f) + measuredWidth) * i4;
                    canvas.drawRect((float) dp4, (float) i3, (float) (dp4 + measuredWidth), (float) (i3 + measuredWidth), this.paint);
                }
                i3 += AndroidUtilities.dp(2.0f) + measuredWidth;
                if (this.isSingleCell) {
                    break;
                }
            }
        } else if (getViewType() == 3) {
            while (i2 < getMeasuredHeight()) {
                this.rectF.set((float) AndroidUtilities.dp(12.0f), (float) (AndroidUtilities.dp(8.0f) + i2), (float) AndroidUtilities.dp(52.0f), (float) (i2 + AndroidUtilities.dp(48.0f)));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + i2), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + i2), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(42.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + i2), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + i2));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i2 += getCellHeight();
                if (this.isSingleCell) {
                    break;
                }
            }
        } else if (getViewType() == 4) {
            while (i2 < getMeasuredHeight()) {
                int dp5 = AndroidUtilities.dp(44.0f) >> 1;
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(12.0f) + dp5)), (float) (AndroidUtilities.dp(6.0f) + i2 + dp5), (float) dp5, this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + i2), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + i2), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(42.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + i2), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + i2));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i2 += getCellHeight();
                if (this.isSingleCell) {
                    break;
                }
            }
        } else if (getViewType() == 5) {
            while (i2 < getMeasuredHeight()) {
                this.rectF.set((float) AndroidUtilities.dp(10.0f), (float) (AndroidUtilities.dp(11.0f) + i2), (float) AndroidUtilities.dp(62.0f), (float) (AndroidUtilities.dp(63.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + i2), (float) AndroidUtilities.dp(140.0f), (float) (i2 + AndroidUtilities.dp(20.0f)));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + i2), (float) AndroidUtilities.dp(268.0f), (float) (AndroidUtilities.dp(42.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(54.0f) + i2), (float) AndroidUtilities.dp(188.0f), (float) (AndroidUtilities.dp(62.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + i2), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + i2));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i2 += getCellHeight();
                if (this.isSingleCell) {
                    break;
                }
            }
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long abs = Math.abs(this.lastUpdateTime - elapsedRealtime);
        if (abs > 17) {
            abs = 16;
        }
        this.lastUpdateTime = elapsedRealtime;
        if (this.isSingleCell) {
            int measuredWidth2 = (int) (((float) this.totalTranslation) + (((float) (abs * ((long) getMeasuredWidth()))) / 400.0f));
            this.totalTranslation = measuredWidth2;
            if (measuredWidth2 >= getMeasuredWidth() * 2) {
                this.totalTranslation = (-this.gradientWidth) * 2;
            }
            this.matrix.setTranslate((float) this.totalTranslation, 0.0f);
        } else {
            int measuredHeight = (int) (((float) this.totalTranslation) + (((float) (abs * ((long) getMeasuredHeight()))) / 400.0f));
            this.totalTranslation = measuredHeight;
            if (measuredHeight >= getMeasuredHeight() * 2) {
                this.totalTranslation = (-this.gradientWidth) * 2;
            }
            this.matrix.setTranslate(0.0f, (float) this.totalTranslation);
        }
        this.gradient.setLocalMatrix(this.matrix);
        invalidate();
    }

    private float checkRtl(float f) {
        return LocaleController.isRTL ? ((float) getMeasuredWidth()) - f : f;
    }

    private void checkRtl(RectF rectF2) {
        if (LocaleController.isRTL) {
            rectF2.left = ((float) getMeasuredWidth()) - rectF2.left;
            rectF2.right = ((float) getMeasuredWidth()) - rectF2.right;
        }
    }

    private int getCellHeight() {
        if (getViewType() == 1) {
            return AndroidUtilities.dp(78.0f) + 1;
        }
        if (getViewType() == 2) {
            return ((getMeasuredWidth() - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount()) + AndroidUtilities.dp(2.0f);
        }
        if (getViewType() == 3) {
            return AndroidUtilities.dp(56.0f) + 1;
        }
        if (getViewType() == 4) {
            return AndroidUtilities.dp(56.0f) + 1;
        }
        if (getViewType() == 5) {
            return AndroidUtilities.dp(80.0f);
        }
        return 0;
    }

    public void showDate(boolean z) {
        this.showDate = z;
    }
}
