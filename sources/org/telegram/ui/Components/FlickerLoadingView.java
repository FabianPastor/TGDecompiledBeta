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
import java.util.Random;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;

public class FlickerLoadingView extends View {
    private Paint backgroundPaint;
    private int color0;
    private int color1;
    private String colorKey1;
    private String colorKey2;
    private String colorKey3;
    private LinearGradient gradient;
    private int gradientWidth;
    private Paint headerPaint;
    private boolean isSingleCell;
    private int itemsCount;
    private long lastUpdateTime;
    private Matrix matrix;
    private int paddingLeft;
    private int paddingTop;
    private Paint paint;
    float[] randomParams;
    private RectF rectF;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean showDate;
    private int skipDrawItemsCount;
    private int totalTranslation;
    private boolean useHeaderOffset;
    private int viewType;

    public int getColumnsCount() {
        return 2;
    }

    public void setViewType(int i) {
        this.viewType = i;
        if (i == 11) {
            Random random = new Random();
            this.randomParams = new float[2];
            for (int i2 = 0; i2 < 2; i2++) {
                this.randomParams[i2] = ((float) Math.abs(random.nextInt() % 1000)) / 1000.0f;
            }
        }
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
        this(context, (Theme.ResourcesProvider) null);
    }

    public FlickerLoadingView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.paint = new Paint();
        this.headerPaint = new Paint();
        this.rectF = new RectF();
        this.showDate = true;
        this.colorKey1 = "windowBackgroundWhite";
        this.colorKey2 = "windowBackgroundGray";
        this.itemsCount = 1;
        this.resourcesProvider = resourcesProvider2;
        this.matrix = new Matrix();
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
        int themedColor = getThemedColor(this.colorKey1);
        int themedColor2 = getThemedColor(this.colorKey2);
        int i = 4;
        int i2 = 0;
        if (!(this.color1 == themedColor2 && this.color0 == themedColor)) {
            this.color0 = themedColor;
            this.color1 = themedColor2;
            if (this.isSingleCell || this.viewType == 13) {
                int dp = AndroidUtilities.dp(200.0f);
                this.gradientWidth = dp;
                this.gradient = new LinearGradient(0.0f, 0.0f, (float) dp, 0.0f, new int[]{themedColor2, themedColor, themedColor, themedColor2}, new float[]{0.0f, 0.4f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
            } else {
                int dp2 = AndroidUtilities.dp(600.0f);
                this.gradientWidth = dp2;
                this.gradient = new LinearGradient(0.0f, 0.0f, 0.0f, (float) dp2, new int[]{themedColor2, themedColor, themedColor, themedColor2}, new float[]{0.0f, 0.4f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
            }
            this.paint.setShader(this.gradient);
        }
        int i3 = this.paddingTop;
        if (this.useHeaderOffset) {
            int dp3 = i3 + AndroidUtilities.dp(32.0f);
            String str = this.colorKey3;
            if (str != null) {
                this.headerPaint.setColor(getThemedColor(str));
            }
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(32.0f), this.colorKey3 != null ? this.headerPaint : this.paint);
            i3 = dp3;
        }
        float f = 16.0f;
        if (getViewType() == 7) {
            while (i3 <= getMeasuredHeight()) {
                int cellHeight = getCellHeight(getMeasuredWidth());
                int dp4 = AndroidUtilities.dp(28.0f);
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(10.0f) + dp4)), (float) ((cellHeight >> 1) + i3), (float) dp4, this.paint);
                this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(16.0f) + i3), (float) AndroidUtilities.dp(148.0f), (float) (i3 + AndroidUtilities.dp(24.0f)));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(38.0f) + i3), (float) AndroidUtilities.dp(268.0f), (float) (AndroidUtilities.dp(46.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (SharedConfig.useThreeLinesLayout) {
                    this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(54.0f) + i3), (float) AndroidUtilities.dp(220.0f), (float) (AndroidUtilities.dp(62.0f) + i3));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(16.0f) + i3), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(24.0f) + i3));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i3 += getCellHeight(getMeasuredWidth());
                i2++;
                if (this.isSingleCell && i2 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 1) {
            while (i3 <= getMeasuredHeight()) {
                int dp5 = AndroidUtilities.dp(25.0f);
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(9.0f) + dp5)), (float) ((AndroidUtilities.dp(78.0f) >> 1) + i3), (float) dp5, this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(20.0f) + i3), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(28.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(42.0f) + i3), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(50.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + i3), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(28.0f) + i3));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i3 += getCellHeight(getMeasuredWidth());
                i2++;
                if (this.isSingleCell && i2 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 2) {
            int measuredWidth = (getMeasuredWidth() - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount();
            int i4 = i3;
            int i5 = 0;
            while (true) {
                if (i4 >= getMeasuredHeight() && !this.isSingleCell) {
                    break;
                }
                for (int i6 = 0; i6 < getColumnsCount(); i6++) {
                    if (i5 != 0 || i6 >= this.skipDrawItemsCount) {
                        int dp6 = (measuredWidth + AndroidUtilities.dp(2.0f)) * i6;
                        canvas.drawRect((float) dp6, (float) i4, (float) (dp6 + measuredWidth), (float) (i4 + measuredWidth), this.paint);
                    }
                }
                i4 += measuredWidth + AndroidUtilities.dp(2.0f);
                i5++;
                if (this.isSingleCell && i5 >= 2) {
                    break;
                }
            }
        } else if (getViewType() == 3) {
            while (i3 <= getMeasuredHeight()) {
                this.rectF.set((float) AndroidUtilities.dp(12.0f), (float) (AndroidUtilities.dp(8.0f) + i3), (float) AndroidUtilities.dp(52.0f), (float) (AndroidUtilities.dp(48.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + i3), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + i3), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(42.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + i3), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + i3));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i3 += getCellHeight(getMeasuredWidth());
                i2++;
                if (this.isSingleCell && i2 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 4) {
            while (i3 <= getMeasuredHeight()) {
                int dp7 = AndroidUtilities.dp(44.0f) >> 1;
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(12.0f) + dp7)), (float) (AndroidUtilities.dp(6.0f) + i3 + dp7), (float) dp7, this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + i3), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + i3), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(42.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + i3), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + i3));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i3 += getCellHeight(getMeasuredWidth());
                i2++;
                if (this.isSingleCell && i2 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 5) {
            while (i3 <= getMeasuredHeight()) {
                this.rectF.set((float) AndroidUtilities.dp(10.0f), (float) (AndroidUtilities.dp(11.0f) + i3), (float) AndroidUtilities.dp(62.0f), (float) (AndroidUtilities.dp(63.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + i3), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + i3), (float) AndroidUtilities.dp(268.0f), (float) (AndroidUtilities.dp(42.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(54.0f) + i3), (float) AndroidUtilities.dp(188.0f), (float) (AndroidUtilities.dp(62.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + i3), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + i3));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i3 += getCellHeight(getMeasuredWidth());
                i2++;
                if (this.isSingleCell && i2 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 6 || getViewType() == 10) {
            while (i3 <= getMeasuredHeight()) {
                int dp8 = AndroidUtilities.dp(23.0f);
                canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(9.0f) + dp8)), (float) ((AndroidUtilities.dp(64.0f) >> 1) + i3), (float) dp8, this.paint);
                this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(17.0f) + i3), (float) (this.paddingLeft + AndroidUtilities.dp(260.0f)), (float) (AndroidUtilities.dp(25.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(39.0f) + i3), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (AndroidUtilities.dp(47.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + i3), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(28.0f) + i3));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i3 += getCellHeight(getMeasuredWidth());
                i2++;
                if (this.isSingleCell && i2 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 8) {
            while (i3 <= getMeasuredHeight()) {
                int dp9 = AndroidUtilities.dp(23.0f);
                canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(11.0f) + dp9)), (float) ((AndroidUtilities.dp(64.0f) >> 1) + i3), (float) dp9, this.paint);
                this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(17.0f) + i3), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (AndroidUtilities.dp(25.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(39.0f) + i3), (float) (this.paddingLeft + AndroidUtilities.dp(260.0f)), (float) (AndroidUtilities.dp(47.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + i3), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(28.0f) + i3));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i3 += getCellHeight(getMeasuredWidth());
                i2++;
                if (this.isSingleCell && i2 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 9) {
            while (i3 <= getMeasuredHeight()) {
                canvas2.drawCircle(checkRtl((float) AndroidUtilities.dp(35.0f)), (float) ((getCellHeight(getMeasuredWidth()) >> 1) + i3), (float) (AndroidUtilities.dp(32.0f) / 2), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(72.0f), (float) (AndroidUtilities.dp(16.0f) + i3), (float) AndroidUtilities.dp(268.0f), (float) (AndroidUtilities.dp(24.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) AndroidUtilities.dp(72.0f), (float) (AndroidUtilities.dp(38.0f) + i3), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(46.0f) + i3));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(16.0f) + i3), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(24.0f) + i3));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                }
                i3 += getCellHeight(getMeasuredWidth());
                i2++;
                if (this.isSingleCell && i2 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 11) {
            int i7 = 0;
            while (i3 <= getMeasuredHeight()) {
                this.rectF.set((float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(14.0f), (((float) getMeasuredWidth()) * 0.5f) + ((float) AndroidUtilities.dp(this.randomParams[0] * 40.0f)), (float) (AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f)));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(18.0f)), (float) AndroidUtilities.dp(14.0f), (((float) getMeasuredWidth()) - (((float) getMeasuredWidth()) * 0.2f)) - ((float) AndroidUtilities.dp(this.randomParams[0] * 20.0f)), (float) (AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f)));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                i3 += getCellHeight(getMeasuredWidth());
                i7++;
                if (this.isSingleCell && i7 >= this.itemsCount) {
                    break;
                }
            }
        } else if (getViewType() == 12) {
            int dp10 = i3 + AndroidUtilities.dp(14.0f);
            while (dp10 <= getMeasuredHeight()) {
                int measuredWidth2 = getMeasuredWidth() / i;
                int i8 = 0;
                while (i8 < i) {
                    float f2 = ((float) (measuredWidth2 * i8)) + (((float) measuredWidth2) / 2.0f);
                    canvas2.drawCircle(f2, ((float) (AndroidUtilities.dp(7.0f) + dp10)) + (((float) AndroidUtilities.dp(56.0f)) / 2.0f), (float) AndroidUtilities.dp(28.0f), this.paint);
                    float dp11 = (float) (AndroidUtilities.dp(7.0f) + dp10 + AndroidUtilities.dp(56.0f) + AndroidUtilities.dp(f));
                    RectF rectF2 = AndroidUtilities.rectTmp;
                    rectF2.set(f2 - ((float) AndroidUtilities.dp(24.0f)), dp11 - ((float) AndroidUtilities.dp(4.0f)), f2 + ((float) AndroidUtilities.dp(24.0f)), dp11 + ((float) AndroidUtilities.dp(4.0f)));
                    canvas2.drawRoundRect(rectF2, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
                    i8++;
                    f = 16.0f;
                    i = 4;
                }
                dp10 += getCellHeight(getMeasuredWidth());
                if (this.isSingleCell) {
                    break;
                }
                f = 16.0f;
                i = 4;
            }
        } else if (getViewType() == 13) {
            float measuredHeight = ((float) getMeasuredHeight()) / 2.0f;
            RectF rectF3 = AndroidUtilities.rectTmp;
            rectF3.set((float) AndroidUtilities.dp(40.0f), measuredHeight - ((float) AndroidUtilities.dp(4.0f)), (float) (getMeasuredWidth() - AndroidUtilities.dp(120.0f)), ((float) AndroidUtilities.dp(4.0f)) + measuredHeight);
            canvas2.drawRoundRect(rectF3, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
            if (this.backgroundPaint == null) {
                Paint paint2 = new Paint(1);
                this.backgroundPaint = paint2;
                paint2.setColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
            }
            while (i2 < 3) {
                canvas2.drawCircle((float) ((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i2)), measuredHeight, (float) AndroidUtilities.dp(13.0f), this.backgroundPaint);
                canvas2.drawCircle((float) ((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i2)), measuredHeight, (float) AndroidUtilities.dp(12.0f), this.paint);
                i2++;
            }
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long abs = Math.abs(this.lastUpdateTime - elapsedRealtime);
        if (abs > 17) {
            abs = 16;
        }
        this.lastUpdateTime = elapsedRealtime;
        if (this.isSingleCell || this.viewType == 13) {
            int measuredWidth3 = (int) (((float) this.totalTranslation) + (((float) (abs * ((long) getMeasuredWidth()))) / 400.0f));
            this.totalTranslation = measuredWidth3;
            if (measuredWidth3 >= getMeasuredWidth() * 2) {
                this.totalTranslation = (-this.gradientWidth) * 2;
            }
            this.matrix.setTranslate((float) this.totalTranslation, 0.0f);
        } else {
            int measuredHeight2 = (int) (((float) this.totalTranslation) + (((float) (abs * ((long) getMeasuredHeight()))) / 400.0f));
            this.totalTranslation = measuredHeight2;
            if (measuredHeight2 >= getMeasuredHeight() * 2) {
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
            if (getViewType() == 11) {
                return AndroidUtilities.dp(36.0f);
            }
            if (getViewType() == 12) {
                return AndroidUtilities.dp(103.0f);
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

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
