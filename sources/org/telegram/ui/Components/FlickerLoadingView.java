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
    FlickerLoadingView globalGradientView;
    private LinearGradient gradient;
    private int gradientWidth;
    private Paint headerPaint;
    private boolean ignoreHeightCheck;
    private boolean isSingleCell;
    private int itemsCount;
    private long lastUpdateTime;
    private Matrix matrix;
    private int paddingLeft;
    private int paddingTop;
    private Paint paint;
    private int parentHeight;
    private int parentWidth;
    private float parentXOffset;
    float[] randomParams;
    private RectF rectF;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean showDate;
    private int skipDrawItemsCount;
    private int totalTranslation;
    private boolean useHeaderOffset;
    private int viewType;

    public int getAdditionalHeight() {
        return 0;
    }

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
        if (this.isSingleCell) {
            int i3 = this.itemsCount;
            if (i3 > 1 && this.ignoreHeightCheck) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((getCellHeight(View.MeasureSpec.getSize(i)) * this.itemsCount) + getAdditionalHeight(), NUM));
            } else if (i3 <= 1 || View.MeasureSpec.getSize(i2) <= 0) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(getCellHeight(View.MeasureSpec.getSize(i)) + getAdditionalHeight(), NUM));
            } else {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i2), getCellHeight(View.MeasureSpec.getSize(i)) * this.itemsCount) + getAdditionalHeight(), NUM));
            }
        } else {
            super.onMeasure(i, i2);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        Paint paint2 = this.paint;
        if (this.globalGradientView != null) {
            if (getParent() != null) {
                View view = (View) getParent();
                this.globalGradientView.setParentSize(view.getMeasuredWidth(), view.getMeasuredHeight(), -getX());
            }
            paint2 = this.globalGradientView.paint;
        }
        Paint paint3 = paint2;
        updateColors();
        updateGradient();
        int i = this.paddingTop;
        if (this.useHeaderOffset) {
            int dp = i + AndroidUtilities.dp(32.0f);
            String str = this.colorKey3;
            if (str != null) {
                this.headerPaint.setColor(getThemedColor(str));
            }
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(32.0f), this.colorKey3 != null ? this.headerPaint : paint3);
            i = dp;
        }
        float f = 28.0f;
        int i2 = 0;
        int i3 = 1;
        if (getViewType() == 7) {
            while (i <= getMeasuredHeight()) {
                int cellHeight = getCellHeight(getMeasuredWidth());
                int dp2 = AndroidUtilities.dp(f);
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(10.0f) + dp2)), (float) ((cellHeight >> i3) + i), (float) dp2, paint3);
                this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(16.0f) + i), (float) AndroidUtilities.dp(148.0f), (float) (i + AndroidUtilities.dp(24.0f)));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(38.0f) + i), (float) AndroidUtilities.dp(268.0f), (float) (AndroidUtilities.dp(46.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                if (SharedConfig.useThreeLinesLayout) {
                    this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(54.0f) + i), (float) AndroidUtilities.dp(220.0f), (float) (AndroidUtilities.dp(62.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                }
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(16.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(24.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                }
                i += getCellHeight(getMeasuredWidth());
                int i4 = i2 + 1;
                if (this.isSingleCell && i4 >= this.itemsCount) {
                    break;
                }
                i2 = i4;
                i3 = 1;
                f = 28.0f;
            }
        } else if (getViewType() == 18) {
            int i5 = i;
            while (i5 <= getMeasuredHeight()) {
                int dp3 = AndroidUtilities.dp(25.0f);
                canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(9.0f) + dp3)), (float) (AndroidUtilities.dp(32.0f) + i5), (float) dp3, paint3);
                float f2 = (float) 76;
                int i6 = (i2 % 2 == 0 ? 52 : 72) + 76;
                this.rectF.set((float) AndroidUtilities.dp(f2), (float) (AndroidUtilities.dp(20.0f) + i5), (float) AndroidUtilities.dp((float) i6), (float) (AndroidUtilities.dp(28.0f) + i5));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                int i7 = i6 + 8;
                this.rectF.set((float) AndroidUtilities.dp((float) i7), (float) (AndroidUtilities.dp(20.0f) + i5), (float) AndroidUtilities.dp((float) (i7 + 84)), (float) (AndroidUtilities.dp(28.0f) + i5));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                this.rectF.set((float) AndroidUtilities.dp(f2), (float) (AndroidUtilities.dp(42.0f) + i5), (float) AndroidUtilities.dp((float) 140), (float) (AndroidUtilities.dp(50.0f) + i5));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                canvas.drawLine((float) AndroidUtilities.dp(f2), (float) (getCellHeight(getMeasuredWidth()) + i5), (float) getMeasuredWidth(), (float) (getCellHeight(getMeasuredWidth()) + i5), paint3);
                i5 += getCellHeight(getMeasuredWidth());
                int i8 = i2 + 1;
                if (this.isSingleCell && i8 >= this.itemsCount) {
                    break;
                }
                i2 = i8;
            }
        } else if (getViewType() == 19) {
            int i9 = i;
            while (i9 <= getMeasuredHeight()) {
                int dp4 = AndroidUtilities.dp(20.0f);
                canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(9.0f) + dp4)), (float) (AndroidUtilities.dp(29.0f) + i9), (float) dp4, paint3);
                float f3 = (float) 76;
                this.rectF.set((float) AndroidUtilities.dp(f3), (float) (AndroidUtilities.dp(16.0f) + i9), (float) AndroidUtilities.dp((float) ((i2 % 2 == 0 ? 92 : 128) + 76)), (float) (AndroidUtilities.dp(24.0f) + i9));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                this.rectF.set((float) AndroidUtilities.dp(f3), (float) (AndroidUtilities.dp(38.0f) + i9), (float) AndroidUtilities.dp((float) 240), (float) (AndroidUtilities.dp(46.0f) + i9));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                canvas.drawLine((float) AndroidUtilities.dp(f3), (float) (getCellHeight(getMeasuredWidth()) + i9), (float) getMeasuredWidth(), (float) (getCellHeight(getMeasuredWidth()) + i9), paint3);
                i9 += getCellHeight(getMeasuredWidth());
                int i10 = i2 + 1;
                if (this.isSingleCell && i10 >= this.itemsCount) {
                    break;
                }
                i2 = i10;
            }
        } else if (getViewType() == 1) {
            while (i <= getMeasuredHeight()) {
                int dp5 = AndroidUtilities.dp(25.0f);
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(9.0f) + dp5)), (float) ((AndroidUtilities.dp(78.0f) >> 1) + i), (float) dp5, paint3);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(20.0f) + i), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(28.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(42.0f) + i), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(50.0f) + i));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(28.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                }
                i += getCellHeight(getMeasuredWidth());
                int i11 = i2 + 1;
                if (this.isSingleCell && i11 >= this.itemsCount) {
                    break;
                }
                i2 = i11;
            }
        } else if (getViewType() == 2) {
            int measuredWidth = (getMeasuredWidth() - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount();
            int i12 = i;
            int i13 = 0;
            while (true) {
                if (i12 >= getMeasuredHeight() && !this.isSingleCell) {
                    break;
                }
                for (int i14 = 0; i14 < getColumnsCount(); i14++) {
                    if (i13 != 0 || i14 >= this.skipDrawItemsCount) {
                        int dp6 = (measuredWidth + AndroidUtilities.dp(2.0f)) * i14;
                        canvas.drawRect((float) dp6, (float) i12, (float) (dp6 + measuredWidth), (float) (i12 + measuredWidth), paint3);
                    }
                }
                i12 += measuredWidth + AndroidUtilities.dp(2.0f);
                i13++;
                if (this.isSingleCell && i13 >= 2) {
                    break;
                }
            }
        } else {
            int i15 = 2;
            if (getViewType() == 3) {
                while (i <= getMeasuredHeight()) {
                    this.rectF.set((float) AndroidUtilities.dp(12.0f), (float) (AndroidUtilities.dp(8.0f) + i), (float) AndroidUtilities.dp(52.0f), (float) (AndroidUtilities.dp(48.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + i), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + i), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(42.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    if (this.showDate) {
                        this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + i));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    }
                    i += getCellHeight(getMeasuredWidth());
                    int i16 = i2 + 1;
                    if (this.isSingleCell && i16 >= this.itemsCount) {
                        break;
                    }
                    i2 = i16;
                }
            } else if (getViewType() == 4) {
                while (i <= getMeasuredHeight()) {
                    int dp7 = AndroidUtilities.dp(44.0f) >> 1;
                    canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(12.0f) + dp7)), (float) (AndroidUtilities.dp(6.0f) + i + dp7), (float) dp7, paint3);
                    this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + i), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + i), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(42.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    if (this.showDate) {
                        this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + i));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    }
                    i += getCellHeight(getMeasuredWidth());
                    int i17 = i2 + 1;
                    if (this.isSingleCell && i17 >= this.itemsCount) {
                        break;
                    }
                    i2 = i17;
                }
            } else if (getViewType() == 5) {
                while (i <= getMeasuredHeight()) {
                    this.rectF.set((float) AndroidUtilities.dp(10.0f), (float) (AndroidUtilities.dp(11.0f) + i), (float) AndroidUtilities.dp(62.0f), (float) (AndroidUtilities.dp(63.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + i), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + i), (float) AndroidUtilities.dp(268.0f), (float) (AndroidUtilities.dp(42.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(54.0f) + i), (float) AndroidUtilities.dp(188.0f), (float) (AndroidUtilities.dp(62.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    if (this.showDate) {
                        this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + i));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    }
                    i += getCellHeight(getMeasuredWidth());
                    int i18 = i2 + 1;
                    if (this.isSingleCell && i18 >= this.itemsCount) {
                        break;
                    }
                    i2 = i18;
                }
            } else if (getViewType() == 6 || getViewType() == 10) {
                while (i <= getMeasuredHeight()) {
                    int dp8 = AndroidUtilities.dp(23.0f);
                    canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(9.0f) + dp8)), (float) ((AndroidUtilities.dp(64.0f) >> 1) + i), (float) dp8, paint3);
                    this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(17.0f) + i), (float) (this.paddingLeft + AndroidUtilities.dp(260.0f)), (float) (AndroidUtilities.dp(25.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(39.0f) + i), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (AndroidUtilities.dp(47.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    if (this.showDate) {
                        this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(28.0f) + i));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    }
                    i += getCellHeight(getMeasuredWidth());
                    int i19 = i2 + 1;
                    if (this.isSingleCell && i19 >= this.itemsCount) {
                        break;
                    }
                    i2 = i19;
                }
            } else if (getViewType() == 8) {
                while (i <= getMeasuredHeight()) {
                    int dp9 = AndroidUtilities.dp(23.0f);
                    canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(11.0f) + dp9)), (float) ((AndroidUtilities.dp(64.0f) >> 1) + i), (float) dp9, paint3);
                    this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(17.0f) + i), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (AndroidUtilities.dp(25.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(39.0f) + i), (float) (this.paddingLeft + AndroidUtilities.dp(260.0f)), (float) (AndroidUtilities.dp(47.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    if (this.showDate) {
                        this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(28.0f) + i));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    }
                    i += getCellHeight(getMeasuredWidth());
                    int i20 = i2 + 1;
                    if (this.isSingleCell && i20 >= this.itemsCount) {
                        break;
                    }
                    i2 = i20;
                }
            } else if (getViewType() == 9) {
                while (i <= getMeasuredHeight()) {
                    canvas2.drawCircle(checkRtl((float) AndroidUtilities.dp(35.0f)), (float) ((getCellHeight(getMeasuredWidth()) >> 1) + i), (float) (AndroidUtilities.dp(32.0f) / i15), paint3);
                    this.rectF.set((float) AndroidUtilities.dp(72.0f), (float) (AndroidUtilities.dp(16.0f) + i), (float) AndroidUtilities.dp(268.0f), (float) (i + AndroidUtilities.dp(24.0f)));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    this.rectF.set((float) AndroidUtilities.dp(72.0f), (float) (AndroidUtilities.dp(38.0f) + i), (float) AndroidUtilities.dp(140.0f), (float) (i + AndroidUtilities.dp(46.0f)));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    if (this.showDate) {
                        this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(16.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(24.0f) + i));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    }
                    i += getCellHeight(getMeasuredWidth());
                    int i21 = i2 + 1;
                    if (this.isSingleCell && i21 >= this.itemsCount) {
                        break;
                    }
                    i2 = i21;
                    i15 = 2;
                }
            } else {
                float f4 = 0.5f;
                if (getViewType() == 11) {
                    int i22 = 0;
                    while (i <= getMeasuredHeight()) {
                        this.rectF.set((float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(14.0f), (((float) getMeasuredWidth()) * 0.5f) + ((float) AndroidUtilities.dp(this.randomParams[0] * 40.0f)), (float) (AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f)));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                        this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(18.0f)), (float) AndroidUtilities.dp(14.0f), (((float) getMeasuredWidth()) - (((float) getMeasuredWidth()) * 0.2f)) - ((float) AndroidUtilities.dp(this.randomParams[0] * 20.0f)), (float) (AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f)));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                        i += getCellHeight(getMeasuredWidth());
                        i22++;
                        if (this.isSingleCell && i22 >= this.itemsCount) {
                            break;
                        }
                    }
                } else if (getViewType() == 12) {
                    int dp10 = i + AndroidUtilities.dp(14.0f);
                    while (dp10 <= getMeasuredHeight()) {
                        int measuredWidth2 = getMeasuredWidth() / 4;
                        for (int i23 = 0; i23 < 4; i23++) {
                            float f5 = ((float) (measuredWidth2 * i23)) + (((float) measuredWidth2) / 2.0f);
                            canvas2.drawCircle(f5, ((float) (AndroidUtilities.dp(7.0f) + dp10)) + (((float) AndroidUtilities.dp(56.0f)) / 2.0f), (float) AndroidUtilities.dp(28.0f), paint3);
                            float dp11 = (float) (AndroidUtilities.dp(7.0f) + dp10 + AndroidUtilities.dp(56.0f) + AndroidUtilities.dp(16.0f));
                            RectF rectF2 = AndroidUtilities.rectTmp;
                            rectF2.set(f5 - ((float) AndroidUtilities.dp(24.0f)), dp11 - ((float) AndroidUtilities.dp(4.0f)), f5 + ((float) AndroidUtilities.dp(24.0f)), dp11 + ((float) AndroidUtilities.dp(4.0f)));
                            canvas2.drawRoundRect(rectF2, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                        }
                        dp10 += getCellHeight(getMeasuredWidth());
                        if (this.isSingleCell) {
                            break;
                        }
                    }
                } else if (getViewType() == 13) {
                    float measuredHeight = ((float) getMeasuredHeight()) / 2.0f;
                    RectF rectF3 = AndroidUtilities.rectTmp;
                    rectF3.set((float) AndroidUtilities.dp(40.0f), measuredHeight - ((float) AndroidUtilities.dp(4.0f)), (float) (getMeasuredWidth() - AndroidUtilities.dp(120.0f)), ((float) AndroidUtilities.dp(4.0f)) + measuredHeight);
                    canvas2.drawRoundRect(rectF3, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    if (this.backgroundPaint == null) {
                        Paint paint4 = new Paint(1);
                        this.backgroundPaint = paint4;
                        paint4.setColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
                    }
                    for (int i24 = 0; i24 < 3; i24++) {
                        canvas2.drawCircle((float) ((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i24)), measuredHeight, (float) AndroidUtilities.dp(13.0f), this.backgroundPaint);
                        canvas2.drawCircle((float) ((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i24)), measuredHeight, (float) AndroidUtilities.dp(12.0f), paint3);
                    }
                } else if (getViewType() == 14 || getViewType() == 17) {
                    int dp12 = AndroidUtilities.dp(12.0f);
                    int dp13 = AndroidUtilities.dp(77.0f);
                    int dp14 = AndroidUtilities.dp(4.0f);
                    float dp15 = (float) AndroidUtilities.dp(21.0f);
                    float dp16 = (float) AndroidUtilities.dp(41.0f);
                    while (dp12 < getMeasuredWidth()) {
                        if (this.backgroundPaint == null) {
                            Paint paint5 = new Paint(1);
                            this.backgroundPaint = paint5;
                            paint5.setColor(Theme.getColor("dialogBackground"));
                        }
                        RectF rectF4 = AndroidUtilities.rectTmp;
                        int i25 = dp12 + dp13;
                        rectF4.set((float) (AndroidUtilities.dp(4.0f) + dp12), (float) AndroidUtilities.dp(4.0f), (float) (i25 - AndroidUtilities.dp(4.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(4.0f)));
                        canvas2.drawRoundRect(rectF4, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), paint3);
                        if (getViewType() == 14) {
                            float dp17 = (float) (AndroidUtilities.dp(8.0f) + dp14);
                            float f6 = (float) dp12;
                            float dp18 = ((float) (AndroidUtilities.dp(22.0f) + dp14)) + f6;
                            this.rectF.set(dp18, dp17, dp18 + dp16, dp17 + dp15);
                            RectF rectF5 = this.rectF;
                            canvas2.drawRoundRect(rectF5, rectF5.height() * f4, this.rectF.height() * f4, this.backgroundPaint);
                            float dp19 = dp17 + ((float) AndroidUtilities.dp(4.0f)) + dp15;
                            float dp20 = f6 + ((float) (AndroidUtilities.dp(5.0f) + dp14));
                            this.rectF.set(dp20, dp19, dp20 + dp16, dp19 + dp15);
                            RectF rectF6 = this.rectF;
                            canvas2.drawRoundRect(rectF6, rectF6.height() * f4, this.rectF.height() * f4, this.backgroundPaint);
                        } else if (getViewType() == 17) {
                            float dp21 = (float) AndroidUtilities.dp(5.0f);
                            float dp22 = (float) AndroidUtilities.dp(32.0f);
                            float f7 = ((float) dp12) + ((((float) dp13) - dp22) / 2.0f);
                            int dp23 = AndroidUtilities.dp(21.0f);
                            rectF4.set(f7, (float) dp23, dp22 + f7, (float) (dp23 + AndroidUtilities.dp(32.0f)));
                            canvas2.drawRoundRect(rectF4, dp21, dp21, this.backgroundPaint);
                            canvas2.drawCircle((float) (dp12 + (dp13 / 2)), (float) (getMeasuredHeight() - AndroidUtilities.dp(20.0f)), (float) AndroidUtilities.dp(8.0f), this.backgroundPaint);
                            dp12 = i25;
                            f4 = 0.5f;
                        }
                        canvas2.drawCircle((float) (dp12 + (dp13 / 2)), (float) (getMeasuredHeight() - AndroidUtilities.dp(20.0f)), (float) AndroidUtilities.dp(8.0f), this.backgroundPaint);
                        dp12 = i25;
                        f4 = 0.5f;
                    }
                } else if (getViewType() == 15) {
                    int dp24 = AndroidUtilities.dp(23.0f);
                    int dp25 = AndroidUtilities.dp(4.0f);
                    while (i <= getMeasuredHeight()) {
                        float f8 = (float) dp24;
                        canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(12.0f))) + f8, (float) (AndroidUtilities.dp(8.0f) + i + dp24), f8, paint3);
                        this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(74.0f)), (float) (AndroidUtilities.dp(12.0f) + i), (float) (this.paddingLeft + AndroidUtilities.dp(260.0f)), (float) (AndroidUtilities.dp(20.0f) + i));
                        checkRtl(this.rectF);
                        float f9 = (float) dp25;
                        canvas2.drawRoundRect(this.rectF, f9, f9, paint3);
                        this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(74.0f)), (float) (AndroidUtilities.dp(36.0f) + i), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (AndroidUtilities.dp(42.0f) + i));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, f9, f9, paint3);
                        i += getCellHeight(getMeasuredWidth());
                        int i26 = i2 + 1;
                        if (this.isSingleCell && i26 >= this.itemsCount) {
                            break;
                        }
                        i2 = i26;
                    }
                } else if (getViewType() == 16 || getViewType() == 23) {
                    int i27 = 0;
                    while (i <= getMeasuredHeight()) {
                        int dp26 = AndroidUtilities.dp(18.0f);
                        canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(8.0f) + dp26)), (float) (AndroidUtilities.dp(24.0f) + i), (float) dp26, paint3);
                        this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(58.0f)), (float) (AndroidUtilities.dp(20.0f) + i), (float) (getWidth() - AndroidUtilities.dp(53.0f)), (float) (AndroidUtilities.dp(28.0f) + i));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), paint3);
                        if (i27 < 4) {
                            int dp27 = AndroidUtilities.dp(12.0f);
                            canvas2.drawCircle(checkRtl((float) ((getWidth() - AndroidUtilities.dp(12.0f)) - dp27)), (float) (AndroidUtilities.dp(24.0f) + i), (float) dp27, paint3);
                        }
                        i += getCellHeight(getMeasuredWidth());
                        i27++;
                        if (this.isSingleCell && i27 >= this.itemsCount) {
                            break;
                        }
                    }
                    this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(8.0f)), (float) (AndroidUtilities.dp(20.0f) + i), (float) (getWidth() - AndroidUtilities.dp(8.0f)), (float) (AndroidUtilities.dp(28.0f) + i));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), paint3);
                    this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(8.0f)), (float) (AndroidUtilities.dp(36.0f) + i), (float) (getWidth() - AndroidUtilities.dp(53.0f)), (float) (i + AndroidUtilities.dp(44.0f)));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), paint3);
                } else {
                    int i28 = this.viewType;
                    if (i28 == 21) {
                        while (i <= getMeasuredHeight()) {
                            int dp28 = AndroidUtilities.dp(46.0f) >> 1;
                            canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(20.0f) + dp28)), (float) ((AndroidUtilities.dp(58.0f) >> 1) + i), (float) dp28, paint3);
                            this.rectF.set((float) AndroidUtilities.dp(74.0f), (float) (AndroidUtilities.dp(16.0f) + i), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(24.0f) + i));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                            this.rectF.set((float) AndroidUtilities.dp(74.0f), (float) (AndroidUtilities.dp(38.0f) + i), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(46.0f) + i));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                            i += getCellHeight(getMeasuredWidth());
                            int i29 = i2 + 1;
                            if (this.isSingleCell && i29 >= this.itemsCount) {
                                break;
                            }
                            i2 = i29;
                        }
                    } else if (i28 == 22) {
                        while (i <= getMeasuredHeight()) {
                            int dp29 = AndroidUtilities.dp(48.0f) >> 1;
                            canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(20.0f) + dp29)), (float) (AndroidUtilities.dp(6.0f) + i + dp29), (float) dp29, paint3);
                            this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(16.0f) + i), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(24.0f) + i));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                            this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(38.0f) + i), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(46.0f) + i));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                            i += getCellHeight(getMeasuredWidth());
                            int i30 = i2 + 1;
                            if (this.isSingleCell && i30 >= this.itemsCount) {
                                break;
                            }
                            i2 = i30;
                        }
                    }
                }
            }
        }
        invalidate();
    }

    public void updateGradient() {
        FlickerLoadingView flickerLoadingView = this.globalGradientView;
        if (flickerLoadingView != null) {
            flickerLoadingView.updateGradient();
            return;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long abs = Math.abs(this.lastUpdateTime - elapsedRealtime);
        if (abs > 17) {
            abs = 16;
        }
        if (abs < 4) {
            abs = 0;
        }
        int i = this.parentWidth;
        if (i == 0) {
            i = getMeasuredWidth();
        }
        int i2 = this.parentHeight;
        if (i2 == 0) {
            i2 = getMeasuredHeight();
        }
        this.lastUpdateTime = elapsedRealtime;
        if (this.isSingleCell || this.viewType == 13 || getViewType() == 14 || getViewType() == 17) {
            int i3 = (int) (((float) this.totalTranslation) + (((float) (abs * ((long) i))) / 400.0f));
            this.totalTranslation = i3;
            if (i3 >= i * 2) {
                this.totalTranslation = (-this.gradientWidth) * 2;
            }
            this.matrix.setTranslate(((float) this.totalTranslation) + this.parentXOffset, 0.0f);
        } else {
            int i4 = (int) (((float) this.totalTranslation) + (((float) (abs * ((long) i2))) / 400.0f));
            this.totalTranslation = i4;
            if (i4 >= i2 * 2) {
                this.totalTranslation = (-this.gradientWidth) * 2;
            }
            this.matrix.setTranslate(this.parentXOffset, (float) this.totalTranslation);
        }
        LinearGradient linearGradient = this.gradient;
        if (linearGradient != null) {
            linearGradient.setLocalMatrix(this.matrix);
        }
    }

    public void updateColors() {
        int i;
        FlickerLoadingView flickerLoadingView = this.globalGradientView;
        if (flickerLoadingView != null) {
            flickerLoadingView.updateColors();
            return;
        }
        int themedColor = getThemedColor(this.colorKey1);
        int themedColor2 = getThemedColor(this.colorKey2);
        if (this.color1 != themedColor2 || this.color0 != themedColor) {
            this.color0 = themedColor;
            this.color1 = themedColor2;
            if (this.isSingleCell || (i = this.viewType) == 13 || i == 14 || i == 17) {
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
        switch (getViewType()) {
            case 1:
                return AndroidUtilities.dp(78.0f) + 1;
            case 2:
                return ((i - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount()) + AndroidUtilities.dp(2.0f);
            case 3:
            case 4:
                return AndroidUtilities.dp(56.0f);
            case 5:
                return AndroidUtilities.dp(80.0f);
            case 6:
            case 18:
                return AndroidUtilities.dp(64.0f);
            case 7:
                return AndroidUtilities.dp((float) ((SharedConfig.useThreeLinesLayout ? 78 : 72) + 1));
            case 8:
                return AndroidUtilities.dp(61.0f);
            case 9:
                return AndroidUtilities.dp(66.0f);
            case 10:
                return AndroidUtilities.dp(58.0f);
            case 11:
                return AndroidUtilities.dp(36.0f);
            case 12:
                return AndroidUtilities.dp(103.0f);
            case 15:
                return AndroidUtilities.dp(107.0f);
            case 16:
            case 23:
                return AndroidUtilities.dp(48.0f);
            case 19:
                return AndroidUtilities.dp(58.0f);
            case 21:
                return AndroidUtilities.dp(58.0f);
            case 22:
                return AndroidUtilities.dp(60.0f);
            default:
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

    public void setGlobalGradientView(FlickerLoadingView flickerLoadingView) {
        this.globalGradientView = flickerLoadingView;
    }

    public void setParentSize(int i, int i2, float f) {
        this.parentWidth = i;
        this.parentHeight = i2;
        this.parentXOffset = f;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setIgnoreHeightCheck(boolean z) {
        this.ignoreHeightCheck = z;
    }
}
