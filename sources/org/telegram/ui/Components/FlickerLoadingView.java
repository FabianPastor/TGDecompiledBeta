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
import org.telegram.messenger.SharedConfig;
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
    private int itemsCount = 1;
    private long lastUpdateTime;
    private Matrix matrix = new Matrix();
    private int paddingLeft;
    private int paddingTop;
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
        if (!this.isSingleCell) {
            super.onMeasure(i, i2);
        } else if (this.itemsCount <= 1 || View.MeasureSpec.getSize(i2) <= 0) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(getCellHeight(View.MeasureSpec.getSize(i)), NUM));
        } else {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i2), getCellHeight(View.MeasureSpec.getSize(i)) * this.itemsCount), NUM));
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        int color = Theme.getColor(this.colorKey1);
        int color2 = Theme.getColor(this.colorKey2);
        int i = 0;
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
        int i2 = this.paddingTop;
        if (this.useHeaderOffset) {
            int dp3 = i2 + AndroidUtilities.dp(32.0f);
            String str = this.colorKey3;
            if (str != null) {
                this.headerPaint.setColor(Theme.getColor(str));
            }
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(32.0f), this.colorKey3 != null ? this.headerPaint : this.paint);
            i2 = dp3;
        }
        if (getViewType() == 7) {
            while (i2 <= getMeasuredHeight()) {
                int cellHeight = getCellHeight(getMeasuredWidth());
                int dp4 = AndroidUtilities.dp(28.0f);
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(10.0f) + dp4)), (float) ((cellHeight >> 1) + i2), (float) dp4, this.paint);
                this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(16.0f) + i2), (float) AndroidUtilities.dp(148.0f), (float) (i2 + AndroidUtilities.dp(24.0f)));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(38.0f) + i2), (float) AndroidUtilities.dp(268.0f), (float) (AndroidUtilities.dp(46.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (SharedConfig.useThreeLinesLayout) {
                    this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(54.0f) + i2), (float) AndroidUtilities.dp(220.0f), (float) (AndroidUtilities.dp(62.0f) + i2));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(16.0f) + i2), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(24.0f) + i2));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i2 += getCellHeight(getMeasuredWidth());
                i++;
                if (this.isSingleCell && i >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 1) {
            while (i2 <= getMeasuredHeight()) {
                int dp5 = AndroidUtilities.dp(25.0f);
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(9.0f) + dp5)), (float) ((AndroidUtilities.dp(78.0f) >> 1) + i2), (float) dp5, this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(20.0f) + i2), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(28.0f) + i2));
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
                i2 += getCellHeight(getMeasuredWidth());
                i++;
                if (this.isSingleCell && i >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 2) {
            int measuredWidth = (getMeasuredWidth() - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount();
            int i3 = i2;
            int i4 = 0;
            while (true) {
                if (i3 >= getMeasuredHeight() && !this.isSingleCell) {
                    break;
                }
                for (int i5 = 0; i5 < getColumnsCount(); i5++) {
                    if (i4 != 0 || i5 >= this.skipDrawItemsCount) {
                        int dp6 = (measuredWidth + AndroidUtilities.dp(2.0f)) * i5;
                        canvas.drawRect((float) dp6, (float) i3, (float) (dp6 + measuredWidth), (float) (i3 + measuredWidth), this.paint);
                    }
                }
                i3 += measuredWidth + AndroidUtilities.dp(2.0f);
                i4++;
                if (this.isSingleCell && i4 >= 2) {
                    break;
                }
            }
        } else if (getViewType() == 3) {
            int i6 = 0;
            while (i2 <= getMeasuredHeight()) {
                this.rectF.set((float) AndroidUtilities.dp(12.0f), (float) (AndroidUtilities.dp(8.0f) + i2), (float) AndroidUtilities.dp(52.0f), (float) (AndroidUtilities.dp(48.0f) + i2));
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
                i2 += getCellHeight(getMeasuredWidth());
                i6++;
                if (this.isSingleCell && i6 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 4) {
            int i7 = 0;
            while (i2 <= getMeasuredHeight()) {
                int dp7 = AndroidUtilities.dp(44.0f) >> 1;
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(12.0f) + dp7)), (float) (AndroidUtilities.dp(6.0f) + i2 + dp7), (float) dp7, this.paint);
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
                i2 += getCellHeight(getMeasuredWidth());
                i7++;
                if (this.isSingleCell && i7 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 5) {
            int i8 = 0;
            while (i2 <= getMeasuredHeight()) {
                this.rectF.set((float) AndroidUtilities.dp(10.0f), (float) (AndroidUtilities.dp(11.0f) + i2), (float) AndroidUtilities.dp(62.0f), (float) (AndroidUtilities.dp(63.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + i2), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + i2));
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
                i2 += getCellHeight(getMeasuredWidth());
                i8++;
                if (this.isSingleCell && i8 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 6 || getViewType() == 10) {
            int i9 = 0;
            while (i2 <= getMeasuredHeight()) {
                int dp8 = AndroidUtilities.dp(23.0f);
                canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(9.0f) + dp8)), (float) ((AndroidUtilities.dp(64.0f) >> 1) + i2), (float) dp8, this.paint);
                this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(17.0f) + i2), (float) (this.paddingLeft + AndroidUtilities.dp(260.0f)), (float) (AndroidUtilities.dp(25.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(39.0f) + i2), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (AndroidUtilities.dp(47.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + i2), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(28.0f) + i2));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i2 += getCellHeight(getMeasuredWidth());
                i9++;
                if (this.isSingleCell && i9 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 8) {
            int i10 = 0;
            while (i2 <= getMeasuredHeight()) {
                int dp9 = AndroidUtilities.dp(23.0f);
                canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(11.0f) + dp9)), (float) ((AndroidUtilities.dp(64.0f) >> 1) + i2), (float) dp9, this.paint);
                this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(17.0f) + i2), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (AndroidUtilities.dp(25.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(39.0f) + i2), (float) (this.paddingLeft + AndroidUtilities.dp(260.0f)), (float) (AndroidUtilities.dp(47.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + i2), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(28.0f) + i2));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i2 += getCellHeight(getMeasuredWidth());
                i10++;
                if (this.isSingleCell && i10 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 9) {
            int i11 = 0;
            while (i2 <= getMeasuredHeight()) {
                canvas2.drawCircle(checkRtl((float) AndroidUtilities.dp(35.0f)), (float) ((getCellHeight(getMeasuredWidth()) >> 1) + i2), (float) (AndroidUtilities.dp(32.0f) / 2), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(72.0f), (float) (AndroidUtilities.dp(16.0f) + i2), (float) AndroidUtilities.dp(268.0f), (float) (AndroidUtilities.dp(24.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(72.0f), (float) (AndroidUtilities.dp(38.0f) + i2), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(46.0f) + i2));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(16.0f) + i2), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(24.0f) + i2));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i2 += getCellHeight(getMeasuredWidth());
                i11++;
                if (this.isSingleCell && i11 >= this.itemsCount) {
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
        if (getViewType() == 7) {
            return AndroidUtilities.dp((float) ((SharedConfig.useThreeLinesLayout ? 78 : 72) + 1));
        } else if (getViewType() == 1) {
            return AndroidUtilities.dp(78.0f) + 1;
        } else {
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
            if (getViewType() == 9) {
                return AndroidUtilities.dp(66.0f);
            }
            if (getViewType() == 10) {
                return AndroidUtilities.dp(58.0f);
            }
            if (getViewType() == 8) {
                return AndroidUtilities.dp(61.0f);
            }
            return 0;
        }
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

    public void setPaddingTop(int i) {
        this.paddingTop = i;
        invalidate();
    }

    public void setPaddingLeft(int i) {
        this.paddingLeft = i;
        invalidate();
    }

    public void setItemsCount(int i) {
        this.itemsCount = i;
    }
}
