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
        } else if (this.itemsCount > 1 && ignoreHeightCheck()) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(getCellHeight(View.MeasureSpec.getSize(i)) * this.itemsCount, NUM));
        } else if (this.itemsCount <= 1 || View.MeasureSpec.getSize(i2) <= 0) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(getCellHeight(View.MeasureSpec.getSize(i)), NUM));
        } else {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i2), getCellHeight(View.MeasureSpec.getSize(i)) * this.itemsCount), NUM));
        }
    }

    private boolean ignoreHeightCheck() {
        return this.viewType == 18;
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
        float f = 32.0f;
        if (this.useHeaderOffset) {
            int dp = i + AndroidUtilities.dp(32.0f);
            String str = this.colorKey3;
            if (str != null) {
                this.headerPaint.setColor(getThemedColor(str));
            }
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(32.0f), this.colorKey3 != null ? this.headerPaint : paint3);
            i = dp;
        }
        float f2 = 28.0f;
        int i2 = 0;
        int i3 = 1;
        float f3 = 4.0f;
        if (getViewType() == 7) {
            while (i <= getMeasuredHeight()) {
                int cellHeight = getCellHeight(getMeasuredWidth());
                int dp2 = AndroidUtilities.dp(f2);
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(10.0f) + dp2)), (float) ((cellHeight >> 1) + i), (float) dp2, paint3);
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
                i2++;
                if (this.isSingleCell && i2 >= this.itemsCount) {
                    break;
                }
                f2 = 28.0f;
            }
        } else {
            float f4 = 25.0f;
            float f5 = 42.0f;
            if (getViewType() == 18) {
                int i4 = i;
                int i5 = 0;
                while (i4 <= getMeasuredHeight()) {
                    int dp3 = AndroidUtilities.dp(25.0f);
                    canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(9.0f) + dp3)), (float) (AndroidUtilities.dp(f) + i4), (float) dp3, paint3);
                    float f6 = (float) 76;
                    int i6 = (i5 % 2 == 0 ? 52 : 72) + 76;
                    this.rectF.set((float) AndroidUtilities.dp(f6), (float) (AndroidUtilities.dp(20.0f) + i4), (float) AndroidUtilities.dp((float) i6), (float) (i4 + AndroidUtilities.dp(28.0f)));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    int i7 = i6 + 8;
                    this.rectF.set((float) AndroidUtilities.dp((float) i7), (float) (AndroidUtilities.dp(20.0f) + i4), (float) AndroidUtilities.dp((float) (i7 + 84)), (float) (AndroidUtilities.dp(28.0f) + i4));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    this.rectF.set((float) AndroidUtilities.dp(f6), (float) (AndroidUtilities.dp(42.0f) + i4), (float) AndroidUtilities.dp((float) 140), (float) (AndroidUtilities.dp(50.0f) + i4));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                    canvas.drawLine((float) AndroidUtilities.dp(f6), (float) (getCellHeight(getMeasuredWidth()) + i4), (float) getMeasuredWidth(), (float) (getCellHeight(getMeasuredWidth()) + i4), paint3);
                    i4 += getCellHeight(getMeasuredWidth());
                    i5++;
                    if (this.isSingleCell && i5 >= this.itemsCount) {
                        break;
                    }
                    f = 32.0f;
                }
            } else {
                float f7 = 140.0f;
                if (getViewType() == 1) {
                    while (i <= getMeasuredHeight()) {
                        int dp4 = AndroidUtilities.dp(f4);
                        canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(9.0f) + dp4)), (float) ((AndroidUtilities.dp(78.0f) >> 1) + i), (float) dp4, paint3);
                        this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(20.0f) + i), (float) AndroidUtilities.dp(f7), (float) (i + AndroidUtilities.dp(28.0f)));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                        this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(42.0f) + i), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(50.0f) + i));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                        if (this.showDate) {
                            this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (i + AndroidUtilities.dp(28.0f)));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                        }
                        i += getCellHeight(getMeasuredWidth());
                        i2++;
                        if (this.isSingleCell && i2 >= this.itemsCount) {
                            break;
                        }
                        f7 = 140.0f;
                        f4 = 25.0f;
                    }
                } else {
                    int i8 = 2;
                    if (getViewType() == 2) {
                        int measuredWidth = (getMeasuredWidth() - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount();
                        int i9 = i;
                        int i10 = 0;
                        while (true) {
                            if (i9 >= getMeasuredHeight() && !this.isSingleCell) {
                                break;
                            }
                            for (int i11 = 0; i11 < getColumnsCount(); i11++) {
                                if (i10 != 0 || i11 >= this.skipDrawItemsCount) {
                                    int dp5 = (measuredWidth + AndroidUtilities.dp(2.0f)) * i11;
                                    canvas.drawRect((float) dp5, (float) i9, (float) (dp5 + measuredWidth), (float) (i9 + measuredWidth), paint3);
                                }
                            }
                            i9 += measuredWidth + AndroidUtilities.dp(2.0f);
                            i10++;
                            if (this.isSingleCell && i10 >= 2) {
                                break;
                            }
                        }
                    } else if (getViewType() == 3) {
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
                            i2++;
                            if (this.isSingleCell && i2 >= this.itemsCount) {
                                break;
                            }
                        }
                    } else if (getViewType() == 4) {
                        while (i <= getMeasuredHeight()) {
                            int dp6 = AndroidUtilities.dp(44.0f) >> 1;
                            canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(12.0f) + dp6)), (float) (AndroidUtilities.dp(6.0f) + i + dp6), (float) dp6, paint3);
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
                            i2++;
                            if (this.isSingleCell && i2 >= this.itemsCount) {
                                break;
                            }
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
                            i2++;
                            if (this.isSingleCell && i2 >= this.itemsCount) {
                                break;
                            }
                        }
                    } else if (getViewType() == 6 || getViewType() == 10) {
                        while (i <= getMeasuredHeight()) {
                            int dp7 = AndroidUtilities.dp(23.0f);
                            canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(9.0f) + dp7)), (float) ((AndroidUtilities.dp(64.0f) >> 1) + i), (float) dp7, paint3);
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
                            i2++;
                            if (this.isSingleCell && i2 >= this.itemsCount) {
                                break;
                            }
                        }
                    } else if (getViewType() == 8) {
                        while (i <= getMeasuredHeight()) {
                            int dp8 = AndroidUtilities.dp(23.0f);
                            canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(11.0f) + dp8)), (float) ((AndroidUtilities.dp(64.0f) >> 1) + i), (float) dp8, paint3);
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
                            i2++;
                            if (this.isSingleCell && i2 >= this.itemsCount) {
                                break;
                            }
                        }
                    } else if (getViewType() == 9) {
                        while (i <= getMeasuredHeight()) {
                            canvas2.drawCircle(checkRtl((float) AndroidUtilities.dp(35.0f)), (float) ((getCellHeight(getMeasuredWidth()) >> 1) + i), (float) (AndroidUtilities.dp(32.0f) / i8), paint3);
                            this.rectF.set((float) AndroidUtilities.dp(72.0f), (float) (AndroidUtilities.dp(16.0f) + i), (float) AndroidUtilities.dp(268.0f), (float) (i + AndroidUtilities.dp(24.0f)));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                            this.rectF.set((float) AndroidUtilities.dp(72.0f), (float) (AndroidUtilities.dp(38.0f) + i), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(46.0f) + i));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                            if (this.showDate) {
                                this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(16.0f) + i), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(24.0f) + i));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                            }
                            i += getCellHeight(getMeasuredWidth());
                            i2++;
                            if (this.isSingleCell && i2 >= this.itemsCount) {
                                break;
                            }
                            i8 = 2;
                        }
                    } else {
                        float f8 = 0.5f;
                        if (getViewType() == 11) {
                            int i12 = 0;
                            while (i <= getMeasuredHeight()) {
                                this.rectF.set((float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(14.0f), (((float) getMeasuredWidth()) * 0.5f) + ((float) AndroidUtilities.dp(this.randomParams[0] * 40.0f)), (float) (AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f)));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                                this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(18.0f)), (float) AndroidUtilities.dp(14.0f), (((float) getMeasuredWidth()) - (((float) getMeasuredWidth()) * 0.2f)) - ((float) AndroidUtilities.dp(this.randomParams[0] * 20.0f)), (float) (AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f)));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                                i += getCellHeight(getMeasuredWidth());
                                i12++;
                                if (this.isSingleCell && i12 >= this.itemsCount) {
                                    break;
                                }
                            }
                        } else if (getViewType() == 12) {
                            int dp9 = i + AndroidUtilities.dp(14.0f);
                            while (dp9 <= getMeasuredHeight()) {
                                int measuredWidth2 = getMeasuredWidth() / 4;
                                int i13 = 0;
                                for (int i14 = 4; i13 < i14; i14 = 4) {
                                    float f9 = ((float) (measuredWidth2 * i13)) + (((float) measuredWidth2) / 2.0f);
                                    canvas2.drawCircle(f9, ((float) (AndroidUtilities.dp(7.0f) + dp9)) + (((float) AndroidUtilities.dp(56.0f)) / 2.0f), (float) AndroidUtilities.dp(28.0f), paint3);
                                    float dp10 = (float) (AndroidUtilities.dp(7.0f) + dp9 + AndroidUtilities.dp(56.0f) + AndroidUtilities.dp(16.0f));
                                    RectF rectF2 = AndroidUtilities.rectTmp;
                                    rectF2.set(f9 - ((float) AndroidUtilities.dp(24.0f)), dp10 - ((float) AndroidUtilities.dp(4.0f)), f9 + ((float) AndroidUtilities.dp(24.0f)), dp10 + ((float) AndroidUtilities.dp(4.0f)));
                                    canvas2.drawRoundRect(rectF2, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint3);
                                    i13++;
                                }
                                dp9 += getCellHeight(getMeasuredWidth());
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
                            while (i2 < 3) {
                                canvas2.drawCircle((float) ((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i2)), measuredHeight, (float) AndroidUtilities.dp(13.0f), this.backgroundPaint);
                                canvas2.drawCircle((float) ((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i2)), measuredHeight, (float) AndroidUtilities.dp(12.0f), paint3);
                                i2++;
                            }
                        } else if (getViewType() == 14 || getViewType() == 17) {
                            int dp11 = AndroidUtilities.dp(12.0f);
                            int dp12 = AndroidUtilities.dp(77.0f);
                            int dp13 = AndroidUtilities.dp(4.0f);
                            float dp14 = (float) AndroidUtilities.dp(21.0f);
                            float dp15 = (float) AndroidUtilities.dp(41.0f);
                            while (dp11 < getMeasuredWidth()) {
                                if (this.backgroundPaint == null) {
                                    Paint paint5 = new Paint(i3);
                                    this.backgroundPaint = paint5;
                                    paint5.setColor(Theme.getColor("dialogBackground"));
                                }
                                RectF rectF4 = AndroidUtilities.rectTmp;
                                int i15 = dp11 + dp12;
                                rectF4.set((float) (AndroidUtilities.dp(f3) + dp11), (float) AndroidUtilities.dp(f3), (float) (i15 - AndroidUtilities.dp(f3)), (float) (getMeasuredHeight() - AndroidUtilities.dp(f3)));
                                canvas2.drawRoundRect(rectF4, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), paint3);
                                if (getViewType() == 14) {
                                    float dp16 = (float) (AndroidUtilities.dp(8.0f) + dp13);
                                    float var_ = (float) dp11;
                                    float dp17 = ((float) (AndroidUtilities.dp(22.0f) + dp13)) + var_;
                                    this.rectF.set(dp17, dp16, dp17 + dp15, dp16 + dp14);
                                    RectF rectF5 = this.rectF;
                                    canvas2.drawRoundRect(rectF5, rectF5.height() * f8, this.rectF.height() * f8, this.backgroundPaint);
                                    float dp18 = dp16 + ((float) AndroidUtilities.dp(4.0f)) + dp14;
                                    float dp19 = var_ + ((float) (AndroidUtilities.dp(5.0f) + dp13));
                                    this.rectF.set(dp19, dp18, dp19 + dp15, dp18 + dp14);
                                    RectF rectF6 = this.rectF;
                                    canvas2.drawRoundRect(rectF6, rectF6.height() * f8, this.rectF.height() * f8, this.backgroundPaint);
                                } else if (getViewType() == 17) {
                                    float dp20 = (float) AndroidUtilities.dp(5.0f);
                                    float dp21 = (float) AndroidUtilities.dp(32.0f);
                                    float var_ = ((float) dp11) + ((((float) dp12) - dp21) / 2.0f);
                                    int dp22 = AndroidUtilities.dp(21.0f);
                                    rectF4.set(var_, (float) dp22, dp21 + var_, (float) (dp22 + AndroidUtilities.dp(32.0f)));
                                    canvas2.drawRoundRect(rectF4, dp20, dp20, this.backgroundPaint);
                                    canvas2.drawCircle((float) (dp11 + (dp12 / 2)), (float) (getMeasuredHeight() - AndroidUtilities.dp(20.0f)), (float) AndroidUtilities.dp(8.0f), this.backgroundPaint);
                                    dp11 = i15;
                                    f8 = 0.5f;
                                    i3 = 1;
                                    f3 = 4.0f;
                                }
                                canvas2.drawCircle((float) (dp11 + (dp12 / 2)), (float) (getMeasuredHeight() - AndroidUtilities.dp(20.0f)), (float) AndroidUtilities.dp(8.0f), this.backgroundPaint);
                                dp11 = i15;
                                f8 = 0.5f;
                                i3 = 1;
                                f3 = 4.0f;
                            }
                        } else if (getViewType() == 15) {
                            int dp23 = AndroidUtilities.dp(23.0f);
                            int dp24 = AndroidUtilities.dp(4.0f);
                            while (i <= getMeasuredHeight()) {
                                float var_ = (float) dp23;
                                canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(12.0f))) + var_, (float) (AndroidUtilities.dp(8.0f) + i + dp23), var_, paint3);
                                this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(74.0f)), (float) (AndroidUtilities.dp(12.0f) + i), (float) (this.paddingLeft + AndroidUtilities.dp(260.0f)), (float) (AndroidUtilities.dp(20.0f) + i));
                                checkRtl(this.rectF);
                                float var_ = (float) dp24;
                                canvas2.drawRoundRect(this.rectF, var_, var_, paint3);
                                this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(74.0f)), (float) (AndroidUtilities.dp(36.0f) + i), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (i + AndroidUtilities.dp(f5)));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, var_, var_, paint3);
                                i += getCellHeight(getMeasuredWidth());
                                i2++;
                                if (this.isSingleCell && i2 >= this.itemsCount) {
                                    break;
                                }
                                f5 = 42.0f;
                            }
                        } else if (getViewType() == 16) {
                            while (i <= getMeasuredHeight()) {
                                int dp25 = AndroidUtilities.dp(18.0f);
                                canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(8.0f) + dp25)), (float) (AndroidUtilities.dp(24.0f) + i), (float) dp25, paint3);
                                this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(58.0f)), (float) (AndroidUtilities.dp(20.0f) + i), (float) (getWidth() - AndroidUtilities.dp(53.0f)), (float) (AndroidUtilities.dp(28.0f) + i));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), paint3);
                                if (i2 < 4) {
                                    int dp26 = AndroidUtilities.dp(12.0f);
                                    canvas2.drawCircle(checkRtl((float) ((getWidth() - AndroidUtilities.dp(12.0f)) - dp26)), (float) (AndroidUtilities.dp(24.0f) + i), (float) dp26, paint3);
                                }
                                i += getCellHeight(getMeasuredWidth());
                                i2++;
                                if (this.isSingleCell && i2 >= this.itemsCount) {
                                    break;
                                }
                            }
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
                return AndroidUtilities.dp(48.0f);
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
}
