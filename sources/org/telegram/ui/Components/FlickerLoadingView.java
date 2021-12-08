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
    public static final int AUDIO_TYPE = 4;
    public static final int BOTS_MENU_TYPE = 11;
    public static final int CALL_LOG_TYPE = 8;
    public static final int CHAT_THEMES_TYPE = 14;
    public static final int DIALOG_CELL_TYPE = 7;
    public static final int DIALOG_TYPE = 1;
    public static final int FILES_TYPE = 3;
    public static final int INVITE_LINKS_TYPE = 9;
    public static final int LINKS_TYPE = 5;
    public static final int MEMBER_REQUESTS_TYPE = 15;
    public static final int MESSAGE_SEEN_TYPE = 13;
    public static final int PHOTOS_TYPE = 2;
    public static final int SHARE_ALERT_TYPE = 12;
    public static final int USERS2_TYPE = 10;
    public static final int USERS_TYPE = 6;
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

    public void setViewType(int type) {
        this.viewType = type;
        if (type == 11) {
            Random random = new Random();
            this.randomParams = new float[2];
            for (int i = 0; i < 2; i++) {
                this.randomParams[i] = ((float) Math.abs(random.nextInt() % 1000)) / 1000.0f;
            }
        }
        invalidate();
    }

    public void setIsSingleCell(boolean b) {
        this.isSingleCell = b;
    }

    public int getViewType() {
        return this.viewType;
    }

    public int getColumnsCount() {
        return 2;
    }

    public void setColors(String key1, String key2, String key3) {
        this.colorKey1 = key1;
        this.colorKey2 = key2;
        this.colorKey3 = key3;
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!this.isSingleCell) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else if (this.itemsCount <= 1 || View.MeasureSpec.getSize(heightMeasureSpec) <= 0) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(getCellHeight(View.MeasureSpec.getSize(widthMeasureSpec)), NUM));
        } else {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(heightMeasureSpec), getCellHeight(View.MeasureSpec.getSize(widthMeasureSpec)) * this.itemsCount), NUM));
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Paint paint2;
        Canvas canvas2 = canvas;
        Paint paint3 = this.paint;
        if (this.globalGradientView != null) {
            if (getParent() != null) {
                View parent = (View) getParent();
                this.globalGradientView.setParentSize(parent.getMeasuredWidth(), parent.getMeasuredHeight(), -getX());
            }
            paint2 = this.globalGradientView.paint;
        } else {
            paint2 = paint3;
        }
        updateColors();
        updateGradient();
        int h = this.paddingTop;
        if (this.useHeaderOffset) {
            int h2 = h + AndroidUtilities.dp(32.0f);
            String str = this.colorKey3;
            if (str != null) {
                this.headerPaint.setColor(getThemedColor(str));
            }
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(32.0f), this.colorKey3 != null ? this.headerPaint : paint2);
            h = h2;
        }
        float f = 268.0f;
        float f2 = 16.0f;
        float f3 = 24.0f;
        float f4 = 28.0f;
        float f5 = 4.0f;
        if (getViewType() == 7) {
            int k = 0;
            while (h <= getMeasuredHeight()) {
                int childH = getCellHeight(getMeasuredWidth());
                int r = AndroidUtilities.dp(f4);
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(10.0f) + r)), (float) ((childH >> 1) + h), (float) r, paint2);
                this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (h + AndroidUtilities.dp(f2)), (float) AndroidUtilities.dp(148.0f), (float) (h + AndroidUtilities.dp(f3)));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(38.0f) + h), (float) AndroidUtilities.dp(268.0f), (float) (AndroidUtilities.dp(46.0f) + h));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                if (SharedConfig.useThreeLinesLayout) {
                    this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(54.0f) + h), (float) AndroidUtilities.dp(220.0f), (float) (AndroidUtilities.dp(62.0f) + h));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                }
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(16.0f) + h), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(24.0f) + h));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                }
                h += getCellHeight(getMeasuredWidth());
                k++;
                if (this.isSingleCell && k >= this.itemsCount) {
                    break;
                }
                f2 = 16.0f;
                f3 = 24.0f;
                f4 = 28.0f;
            }
        } else {
            float f6 = 260.0f;
            float f7 = 140.0f;
            float f8 = 20.0f;
            int i = 1;
            if (getViewType() == 1) {
                int k2 = 0;
                while (h <= getMeasuredHeight()) {
                    int r2 = AndroidUtilities.dp(25.0f);
                    canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(9.0f) + r2)), (float) ((AndroidUtilities.dp(78.0f) >> i) + h), (float) r2, paint2);
                    this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(f8) + h), (float) AndroidUtilities.dp(f7), (float) (h + AndroidUtilities.dp(28.0f)));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                    this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(42.0f) + h), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(50.0f) + h));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                    if (this.showDate) {
                        this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + h), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (h + AndroidUtilities.dp(28.0f)));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                    }
                    h += getCellHeight(getMeasuredWidth());
                    k2++;
                    if (this.isSingleCell && k2 >= this.itemsCount) {
                        break;
                    }
                    f7 = 140.0f;
                    f8 = 20.0f;
                    i = 1;
                }
            } else if (getViewType() == 2) {
                int photoWidth = (getMeasuredWidth() - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount();
                int h3 = h;
                int k3 = 0;
                while (true) {
                    if (h3 >= getMeasuredHeight() && !this.isSingleCell) {
                        int i2 = h3;
                        break;
                    }
                    for (int i3 = 0; i3 < getColumnsCount(); i3++) {
                        if (k3 != 0 || i3 >= this.skipDrawItemsCount) {
                            int x = i3 * (AndroidUtilities.dp(2.0f) + photoWidth);
                            canvas.drawRect((float) x, (float) h3, (float) (x + photoWidth), (float) (h3 + photoWidth), paint2);
                        }
                    }
                    h3 += AndroidUtilities.dp(2.0f) + photoWidth;
                    k3++;
                    if (this.isSingleCell && k3 >= 2) {
                        int i4 = h3;
                        break;
                    }
                }
            } else {
                float f9 = 8.0f;
                if (getViewType() == 3) {
                    int k4 = 0;
                    while (h <= getMeasuredHeight()) {
                        this.rectF.set((float) AndroidUtilities.dp(12.0f), (float) (AndroidUtilities.dp(8.0f) + h), (float) AndroidUtilities.dp(52.0f), (float) (AndroidUtilities.dp(48.0f) + h));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + h), (float) AndroidUtilities.dp(140.0f), (float) (h + AndroidUtilities.dp(20.0f)));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + h), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(42.0f) + h));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                        if (this.showDate) {
                            this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + h), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (h + AndroidUtilities.dp(20.0f)));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                        }
                        h += getCellHeight(getMeasuredWidth());
                        k4++;
                        if (this.isSingleCell && k4 >= this.itemsCount) {
                            break;
                        }
                    }
                } else {
                    int i5 = 4;
                    if (getViewType() == 4) {
                        int k5 = 0;
                        while (h <= getMeasuredHeight()) {
                            int radius = AndroidUtilities.dp(44.0f) >> 1;
                            canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(12.0f) + radius)), (float) (AndroidUtilities.dp(6.0f) + h + radius), (float) radius, paint2);
                            this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + h), (float) AndroidUtilities.dp(140.0f), (float) (h + AndroidUtilities.dp(20.0f)));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + h), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(42.0f) + h));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            if (this.showDate) {
                                this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + h), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (h + AndroidUtilities.dp(20.0f)));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            }
                            h += getCellHeight(getMeasuredWidth());
                            k5++;
                            if (this.isSingleCell && k5 >= this.itemsCount) {
                                break;
                            }
                        }
                    } else if (getViewType() == 5) {
                        int k6 = 0;
                        while (h <= getMeasuredHeight()) {
                            this.rectF.set((float) AndroidUtilities.dp(10.0f), (float) (AndroidUtilities.dp(11.0f) + h), (float) AndroidUtilities.dp(62.0f), (float) (AndroidUtilities.dp(63.0f) + h));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + h), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + h));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + h), (float) AndroidUtilities.dp(268.0f), (float) (AndroidUtilities.dp(42.0f) + h));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(54.0f) + h), (float) AndroidUtilities.dp(188.0f), (float) (AndroidUtilities.dp(62.0f) + h));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            if (this.showDate) {
                                this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + h), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + h));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            }
                            h += getCellHeight(getMeasuredWidth());
                            k6++;
                            if (this.isSingleCell && k6 >= this.itemsCount) {
                                break;
                            }
                        }
                    } else if (getViewType() == 6 || getViewType() == 10) {
                        int k7 = 0;
                        while (h <= getMeasuredHeight()) {
                            int r3 = AndroidUtilities.dp(23.0f);
                            canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(9.0f) + r3)), (float) ((AndroidUtilities.dp(64.0f) >> 1) + h), (float) r3, paint2);
                            this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(17.0f) + h), (float) (this.paddingLeft + AndroidUtilities.dp(f6)), (float) (AndroidUtilities.dp(25.0f) + h));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(39.0f) + h), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (AndroidUtilities.dp(47.0f) + h));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            if (this.showDate) {
                                this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + h), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (h + AndroidUtilities.dp(28.0f)));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            }
                            h += getCellHeight(getMeasuredWidth());
                            k7++;
                            if (this.isSingleCell && k7 >= this.itemsCount) {
                                break;
                            }
                            f6 = 260.0f;
                        }
                    } else if (getViewType() == 8) {
                        int k8 = 0;
                        while (h <= getMeasuredHeight()) {
                            int r4 = AndroidUtilities.dp(23.0f);
                            canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(11.0f) + r4)), (float) ((AndroidUtilities.dp(64.0f) >> 1) + h), (float) r4, paint2);
                            this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(17.0f) + h), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (AndroidUtilities.dp(25.0f) + h));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(39.0f) + h), (float) (this.paddingLeft + AndroidUtilities.dp(260.0f)), (float) (AndroidUtilities.dp(47.0f) + h));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            if (this.showDate) {
                                this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + h), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(28.0f) + h));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            }
                            h += getCellHeight(getMeasuredWidth());
                            k8++;
                            if (this.isSingleCell && k8 >= this.itemsCount) {
                                break;
                            }
                        }
                    } else if (getViewType() == 9) {
                        int k9 = 0;
                        while (h <= getMeasuredHeight()) {
                            canvas2.drawCircle(checkRtl((float) AndroidUtilities.dp(35.0f)), (float) ((getCellHeight(getMeasuredWidth()) >> 1) + h), (float) (AndroidUtilities.dp(32.0f) / 2), paint2);
                            this.rectF.set((float) AndroidUtilities.dp(72.0f), (float) (AndroidUtilities.dp(16.0f) + h), (float) AndroidUtilities.dp(f), (float) (h + AndroidUtilities.dp(24.0f)));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            this.rectF.set((float) AndroidUtilities.dp(72.0f), (float) (AndroidUtilities.dp(38.0f) + h), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(46.0f) + h));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            if (this.showDate) {
                                this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(16.0f) + h), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (h + AndroidUtilities.dp(24.0f)));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            }
                            h += getCellHeight(getMeasuredWidth());
                            k9++;
                            if (this.isSingleCell && k9 >= this.itemsCount) {
                                break;
                            }
                            f = 268.0f;
                        }
                    } else if (getViewType() == 11) {
                        int k10 = 0;
                        while (h <= getMeasuredHeight()) {
                            this.rectF.set((float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(14.0f), (((float) getMeasuredWidth()) * 0.5f) + ((float) AndroidUtilities.dp(this.randomParams[0] * 40.0f)), (float) (AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f)));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(18.0f)), (float) AndroidUtilities.dp(14.0f), (((float) getMeasuredWidth()) - (((float) getMeasuredWidth()) * 0.2f)) - ((float) AndroidUtilities.dp(this.randomParams[0] * 20.0f)), (float) (AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f)));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            h += getCellHeight(getMeasuredWidth());
                            k10++;
                            if (this.isSingleCell && k10 >= this.itemsCount) {
                                break;
                            }
                        }
                    } else {
                        float var_ = 56.0f;
                        if (getViewType() == 12) {
                            int k11 = 0;
                            int h4 = h + AndroidUtilities.dp(14.0f);
                            while (h4 <= getMeasuredHeight()) {
                                int part = getMeasuredWidth() / i5;
                                int i6 = 0;
                                while (i6 < i5) {
                                    float cx = ((float) (part * i6)) + (((float) part) / 2.0f);
                                    canvas2.drawCircle(cx, ((float) (AndroidUtilities.dp(7.0f) + h4)) + (((float) AndroidUtilities.dp(var_)) / 2.0f), (float) AndroidUtilities.dp(28.0f), paint2);
                                    float y = (float) (AndroidUtilities.dp(7.0f) + h4 + AndroidUtilities.dp(var_) + AndroidUtilities.dp(16.0f));
                                    AndroidUtilities.rectTmp.set(cx - ((float) AndroidUtilities.dp(24.0f)), y - ((float) AndroidUtilities.dp(4.0f)), ((float) AndroidUtilities.dp(24.0f)) + cx, ((float) AndroidUtilities.dp(4.0f)) + y);
                                    canvas2.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                    i6++;
                                    i5 = 4;
                                    var_ = 56.0f;
                                }
                                h4 += getCellHeight(getMeasuredWidth());
                                k11++;
                                if (this.isSingleCell) {
                                    break;
                                }
                                i5 = 4;
                                var_ = 56.0f;
                            }
                        } else if (getViewType() == 13) {
                            float cy = ((float) getMeasuredHeight()) / 2.0f;
                            AndroidUtilities.rectTmp.set((float) AndroidUtilities.dp(40.0f), cy - ((float) AndroidUtilities.dp(4.0f)), (float) (getMeasuredWidth() - AndroidUtilities.dp(120.0f)), ((float) AndroidUtilities.dp(4.0f)) + cy);
                            canvas2.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                            if (this.backgroundPaint == null) {
                                Paint paint4 = new Paint(1);
                                this.backgroundPaint = paint4;
                                paint4.setColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
                            }
                            for (int i7 = 0; i7 < 3; i7++) {
                                canvas2.drawCircle((float) ((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i7)), cy, (float) AndroidUtilities.dp(13.0f), this.backgroundPaint);
                                canvas2.drawCircle((float) ((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i7)), cy, (float) AndroidUtilities.dp(12.0f), paint2);
                            }
                        } else if (getViewType() == 14) {
                            int x2 = AndroidUtilities.dp(12.0f);
                            int itemWidth = AndroidUtilities.dp(77.0f);
                            int INNER_RECT_SPACE = AndroidUtilities.dp(4.0f);
                            float BUBBLE_HEIGHT = (float) AndroidUtilities.dp(21.0f);
                            float BUBBLE_WIDTH = (float) AndroidUtilities.dp(41.0f);
                            while (x2 < getMeasuredWidth()) {
                                if (this.backgroundPaint == null) {
                                    Paint paint5 = new Paint(1);
                                    this.backgroundPaint = paint5;
                                    paint5.setColor(Theme.getColor("dialogBackground"));
                                }
                                float bubbleTop = (float) (AndroidUtilities.dp(f9) + INNER_RECT_SPACE);
                                float bubbleLeft = (float) (AndroidUtilities.dp(22.0f) + INNER_RECT_SPACE);
                                AndroidUtilities.rectTmp.set((float) (AndroidUtilities.dp(f5) + x2), (float) AndroidUtilities.dp(f5), (float) ((x2 + itemWidth) - AndroidUtilities.dp(f5)), (float) (getMeasuredHeight() - AndroidUtilities.dp(f5)));
                                canvas2.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), paint2);
                                this.rectF.set(((float) x2) + bubbleLeft, bubbleTop, ((float) x2) + bubbleLeft + BUBBLE_WIDTH, bubbleTop + BUBBLE_HEIGHT);
                                RectF rectF2 = this.rectF;
                                canvas2.drawRoundRect(rectF2, rectF2.height() * 0.5f, this.rectF.height() * 0.5f, this.backgroundPaint);
                                float bubbleLeft2 = (float) (AndroidUtilities.dp(5.0f) + INNER_RECT_SPACE);
                                float bubbleTop2 = bubbleTop + ((float) AndroidUtilities.dp(4.0f)) + BUBBLE_HEIGHT;
                                this.rectF.set(((float) x2) + bubbleLeft2, bubbleTop2, ((float) x2) + bubbleLeft2 + BUBBLE_WIDTH, bubbleTop2 + BUBBLE_HEIGHT);
                                RectF rectF3 = this.rectF;
                                canvas2.drawRoundRect(rectF3, rectF3.height() * 0.5f, this.rectF.height() * 0.5f, this.backgroundPaint);
                                canvas2.drawCircle((float) ((itemWidth / 2) + x2), (float) (getMeasuredHeight() - AndroidUtilities.dp(20.0f)), (float) AndroidUtilities.dp(8.0f), this.backgroundPaint);
                                x2 += itemWidth;
                                f5 = 4.0f;
                                f9 = 8.0f;
                            }
                        } else if (getViewType() == 15) {
                            int count = 0;
                            int radius2 = AndroidUtilities.dp(23.0f);
                            int rectRadius = AndroidUtilities.dp(4.0f);
                            while (h <= getMeasuredHeight()) {
                                canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(12.0f))) + ((float) radius2), (float) (AndroidUtilities.dp(8.0f) + h + radius2), (float) radius2, paint2);
                                this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(74.0f)), (float) (AndroidUtilities.dp(12.0f) + h), (float) (this.paddingLeft + AndroidUtilities.dp(260.0f)), (float) (AndroidUtilities.dp(20.0f) + h));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) rectRadius, (float) rectRadius, paint2);
                                this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(74.0f)), (float) (AndroidUtilities.dp(36.0f) + h), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (AndroidUtilities.dp(42.0f) + h));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) rectRadius, (float) rectRadius, paint2);
                                h += getCellHeight(getMeasuredWidth());
                                count++;
                                if (this.isSingleCell && count >= this.itemsCount) {
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
        long newUpdateTime = SystemClock.elapsedRealtime();
        long dt = Math.abs(this.lastUpdateTime - newUpdateTime);
        if (dt > 17) {
            dt = 16;
        }
        if (dt < 4) {
            dt = 0;
        }
        int width = this.parentWidth;
        if (width == 0) {
            width = getMeasuredWidth();
        }
        int height = this.parentHeight;
        if (height == 0) {
            height = getMeasuredHeight();
        }
        this.lastUpdateTime = newUpdateTime;
        if (this.isSingleCell || this.viewType == 13 || getViewType() == 14) {
            int i = (int) (((float) this.totalTranslation) + (((float) (((long) width) * dt)) / 400.0f));
            this.totalTranslation = i;
            if (i >= width * 2) {
                this.totalTranslation = (-this.gradientWidth) * 2;
            }
            this.matrix.setTranslate(((float) this.totalTranslation) + this.parentXOffset, 0.0f);
        } else {
            int i2 = (int) (((float) this.totalTranslation) + (((float) (((long) height) * dt)) / 400.0f));
            this.totalTranslation = i2;
            if (i2 >= height * 2) {
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
        int color02 = getThemedColor(this.colorKey1);
        int color12 = getThemedColor(this.colorKey2);
        if (this.color1 != color12 || this.color0 != color02) {
            this.color0 = color02;
            this.color1 = color12;
            if (this.isSingleCell || (i = this.viewType) == 13 || i == 14) {
                int dp = AndroidUtilities.dp(200.0f);
                this.gradientWidth = dp;
                this.gradient = new LinearGradient(0.0f, 0.0f, (float) dp, 0.0f, new int[]{color12, color02, color02, color12}, new float[]{0.0f, 0.4f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
            } else {
                int dp2 = AndroidUtilities.dp(600.0f);
                this.gradientWidth = dp2;
                this.gradient = new LinearGradient(0.0f, 0.0f, 0.0f, (float) dp2, new int[]{color12, color02, color02, color12}, new float[]{0.0f, 0.4f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
            }
            this.paint.setShader(this.gradient);
        }
    }

    private float checkRtl(float x) {
        if (LocaleController.isRTL) {
            return ((float) getMeasuredWidth()) - x;
        }
        return x;
    }

    private void checkRtl(RectF rectF2) {
        if (LocaleController.isRTL) {
            rectF2.left = ((float) getMeasuredWidth()) - rectF2.left;
            rectF2.right = ((float) getMeasuredWidth()) - rectF2.right;
        }
    }

    private int getCellHeight(int width) {
        if (getViewType() == 7) {
            return AndroidUtilities.dp((float) ((SharedConfig.useThreeLinesLayout ? 78 : 72) + 1));
        } else if (getViewType() == 1) {
            return AndroidUtilities.dp(78.0f) + 1;
        } else {
            if (getViewType() == 2) {
                return AndroidUtilities.dp(2.0f) + ((width - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount());
            } else if (getViewType() == 3) {
                return AndroidUtilities.dp(56.0f);
            } else {
                if (getViewType() == 4) {
                    return AndroidUtilities.dp(56.0f);
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
                if (getViewType() == 15) {
                    return AndroidUtilities.dp(107.0f);
                }
                return 0;
            }
        }
    }

    public void showDate(boolean showDate2) {
        this.showDate = showDate2;
    }

    public void setUseHeaderOffset(boolean useHeaderOffset2) {
        this.useHeaderOffset = useHeaderOffset2;
    }

    public void skipDrawItemsCount(int i) {
        this.skipDrawItemsCount = i;
    }

    public void setPaddingTop(int t) {
        this.paddingTop = t;
        invalidate();
    }

    public void setPaddingLeft(int paddingLeft2) {
        this.paddingLeft = paddingLeft2;
        invalidate();
    }

    public void setItemsCount(int i) {
        this.itemsCount = i;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void setGlobalGradientView(FlickerLoadingView globalGradientView2) {
        this.globalGradientView = globalGradientView2;
    }

    public void setParentSize(int parentWidth2, int parentHeight2, float parentXOffset2) {
        this.parentWidth = parentWidth2;
        this.parentHeight = parentHeight2;
        this.parentXOffset = parentXOffset2;
    }

    public Paint getPaint() {
        return this.paint;
    }
}
