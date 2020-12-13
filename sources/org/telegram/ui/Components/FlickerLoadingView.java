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
    private int color0;
    private int color1;
    private String colorKey1 = "windowBackgroundWhite";
    private String colorKey2 = "windowBackgroundGray";
    private String colorKey3;
    private LinearGradient gradient;
    private int gradientWidth;
    private Paint headerPaint = new Paint();
    private boolean isSingleCell;
    private long lastUpdateTime;
    private Matrix matrix = new Matrix();
    private Paint paint = new Paint();
    private RectF rectF = new RectF();
    private boolean showDate = true;
    private int skipDrawItemsCount;
    private int totalTranslation;
    private boolean useHeaderOffset;
    private int viewType;

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

    public void setColors(String str, String str2, String str3) {
        this.colorKey1 = str;
        this.colorKey2 = str2;
        this.colorKey3 = str3;
        invalidate();
    }

    public FlickerLoadingView(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.isSingleCell) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(getCellHeight(View.MeasureSpec.getSize(i)), NUM));
        } else {
            super.onMeasure(i, i2);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        Canvas canvas2 = canvas;
        int color = Theme.getColor(this.colorKey1);
        int color2 = Theme.getColor(this.colorKey2);
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
        if (this.useHeaderOffset) {
            i = AndroidUtilities.dp(32.0f) + 0;
            String str = this.colorKey3;
            if (str != null) {
                this.headerPaint.setColor(Theme.getColor(str));
            }
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(32.0f), this.colorKey3 != null ? this.headerPaint : this.paint);
        } else {
            i = 0;
        }
        float f = 25.0f;
        if (getViewType() == 1) {
            while (i < getMeasuredHeight()) {
                int dp3 = AndroidUtilities.dp(f);
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(9.0f) + dp3)), (float) ((AndroidUtilities.dp(78.0f) >> 1) + i), (float) dp3, this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(20.0f) + i), (float) AndroidUtilities.dp(140.0f), (float) (i + AndroidUtilities.dp(28.0f)));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(42.0f) + i), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(50.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(28.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i += getCellHeight(getMeasuredWidth());
                if (this.isSingleCell) {
                    break;
                }
                f = 25.0f;
            }
        } else if (getViewType() == 2) {
            int measuredWidth = (getMeasuredWidth() - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount();
            int i2 = 0;
            while (true) {
                if (i >= getMeasuredHeight() && !this.isSingleCell) {
                    break;
                }
                for (int i3 = 0; i3 < getColumnsCount(); i3++) {
                    if (i2 != 0 || i3 >= this.skipDrawItemsCount) {
                        int dp4 = (measuredWidth + AndroidUtilities.dp(2.0f)) * i3;
                        canvas.drawRect((float) dp4, (float) i, (float) (dp4 + measuredWidth), (float) (i + measuredWidth), this.paint);
                    }
                }
                i += measuredWidth + AndroidUtilities.dp(2.0f);
                i2++;
                if (this.isSingleCell && i2 >= 2) {
                    break;
                }
            }
        } else if (getViewType() == 3) {
            while (i < getMeasuredHeight()) {
                this.rectF.set((float) AndroidUtilities.dp(12.0f), (float) (AndroidUtilities.dp(8.0f) + i), (float) AndroidUtilities.dp(52.0f), (float) (AndroidUtilities.dp(48.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + i), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + i), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(42.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i += getCellHeight(getMeasuredWidth());
                if (this.isSingleCell) {
                    break;
                }
            }
        } else if (getViewType() == 4) {
            while (i < getMeasuredHeight()) {
                int dp5 = AndroidUtilities.dp(44.0f) >> 1;
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(12.0f) + dp5)), (float) (AndroidUtilities.dp(6.0f) + i + dp5), (float) dp5, this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + i), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + i), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(42.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i += getCellHeight(getMeasuredWidth());
                if (this.isSingleCell) {
                    break;
                }
            }
        } else if (getViewType() == 5) {
            while (i < getMeasuredHeight()) {
                this.rectF.set((float) AndroidUtilities.dp(10.0f), (float) (AndroidUtilities.dp(11.0f) + i), (float) AndroidUtilities.dp(62.0f), (float) (AndroidUtilities.dp(63.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + i), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + i), (float) AndroidUtilities.dp(268.0f), (float) (AndroidUtilities.dp(42.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(54.0f) + i), (float) AndroidUtilities.dp(188.0f), (float) (AndroidUtilities.dp(62.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i += getCellHeight(getMeasuredWidth());
                if (this.isSingleCell) {
                    break;
                }
            }
        } else if (getViewType() == 6) {
            while (i < getMeasuredHeight()) {
                int dp6 = AndroidUtilities.dp(23.0f);
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(9.0f) + dp6)), (float) ((AndroidUtilities.dp(64.0f) >> 1) + i), (float) dp6, this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(17.0f) + i), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(25.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(39.0f) + i), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(47.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(28.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i += getCellHeight(getMeasuredWidth());
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

    private int getCellHeight(int i) {
        if (getViewType() == 1) {
            return AndroidUtilities.dp(78.0f) + 1;
        }
        if (getViewType() == 2) {
            return ((i - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount()) + AndroidUtilities.dp(2.0f);
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
        if (getViewType() == 6) {
            return AndroidUtilities.dp(64.0f);
        }
        return 0;
    }

    public void showDate(boolean z) {
        this.showDate = z;
    }

    public void setUseHeaderOffset(boolean z) {
        this.useHeaderOffset = z;
    }

    public void skipDrawItemsCount(int i) {
        this.skipDrawItemsCount = i;
    }
}
