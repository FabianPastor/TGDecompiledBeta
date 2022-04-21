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
    public static final int CONTACT_TYPE = 18;
    public static final int DIALOG_CELL_TYPE = 7;
    public static final int DIALOG_TYPE = 1;
    public static final int FILES_TYPE = 3;
    public static final int INVITE_LINKS_TYPE = 9;
    public static final int LINKS_TYPE = 5;
    public static final int MEMBER_REQUESTS_TYPE = 15;
    public static final int MESSAGE_SEEN_TYPE = 13;
    public static final int PHOTOS_TYPE = 2;
    public static final int QR_TYPE = 17;
    public static final int REACTED_TYPE = 16;
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
        } else if (this.itemsCount > 1 && ignoreHeightCheck()) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(getCellHeight(View.MeasureSpec.getSize(widthMeasureSpec)) * this.itemsCount, NUM));
        } else if (this.itemsCount <= 1 || View.MeasureSpec.getSize(heightMeasureSpec) <= 0) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(getCellHeight(View.MeasureSpec.getSize(widthMeasureSpec)), NUM));
        } else {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(heightMeasureSpec), getCellHeight(View.MeasureSpec.getSize(widthMeasureSpec)) * this.itemsCount), NUM));
        }
    }

    private boolean ignoreHeightCheck() {
        return this.viewType == 18;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Paint paint2;
        int h;
        int h2;
        int h3;
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
        int h4 = this.paddingTop;
        float f = 32.0f;
        if (this.useHeaderOffset) {
            int h5 = h4 + AndroidUtilities.dp(32.0f);
            String str = this.colorKey3;
            if (str != null) {
                this.headerPaint.setColor(getThemedColor(str));
            }
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(32.0f), this.colorKey3 != null ? this.headerPaint : paint2);
            h4 = h5;
        }
        float f2 = 16.0f;
        float f3 = 24.0f;
        float f4 = 28.0f;
        float f5 = 4.0f;
        if (getViewType() != 7) {
            float f6 = 25.0f;
            float f7 = 20.0f;
            if (getViewType() != 18) {
                float f8 = 260.0f;
                float f9 = 140.0f;
                float var_ = 68.0f;
                if (getViewType() != 1) {
                    float var_ = 2.0f;
                    if (getViewType() != 2) {
                        float var_ = 8.0f;
                        if (getViewType() != 3) {
                            int i = 4;
                            if (getViewType() != 4) {
                                if (getViewType() != 5) {
                                    if (getViewType() == 6) {
                                        h = h4;
                                    } else if (getViewType() == 10) {
                                        h = h4;
                                    } else if (getViewType() == 8) {
                                        int k = 0;
                                        while (h4 <= getMeasuredHeight()) {
                                            int r = AndroidUtilities.dp(23.0f);
                                            canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(11.0f) + r)), (float) ((AndroidUtilities.dp(64.0f) >> 1) + h4), (float) r, paint2);
                                            this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(17.0f) + h4), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (AndroidUtilities.dp(25.0f) + h4));
                                            checkRtl(this.rectF);
                                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                            this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(68.0f)), (float) (AndroidUtilities.dp(39.0f) + h4), (float) (this.paddingLeft + AndroidUtilities.dp(260.0f)), (float) (AndroidUtilities.dp(47.0f) + h4));
                                            checkRtl(this.rectF);
                                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                            if (this.showDate) {
                                                this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + h4), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(28.0f) + h4));
                                                checkRtl(this.rectF);
                                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                            }
                                            h4 += getCellHeight(getMeasuredWidth());
                                            k++;
                                            if (this.isSingleCell && k >= this.itemsCount) {
                                                break;
                                            }
                                        }
                                    } else if (getViewType() == 9) {
                                        int k2 = 0;
                                        while (h4 <= getMeasuredHeight()) {
                                            canvas2.drawCircle(checkRtl((float) AndroidUtilities.dp(35.0f)), (float) ((getCellHeight(getMeasuredWidth()) >> 1) + h4), (float) (AndroidUtilities.dp(32.0f) / 2), paint2);
                                            this.rectF.set((float) AndroidUtilities.dp(72.0f), (float) (AndroidUtilities.dp(16.0f) + h4), (float) AndroidUtilities.dp(268.0f), (float) (h4 + AndroidUtilities.dp(24.0f)));
                                            checkRtl(this.rectF);
                                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                            this.rectF.set((float) AndroidUtilities.dp(72.0f), (float) (AndroidUtilities.dp(38.0f) + h4), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(46.0f) + h4));
                                            checkRtl(this.rectF);
                                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                            if (this.showDate) {
                                                this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(16.0f) + h4), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (h4 + AndroidUtilities.dp(24.0f)));
                                                checkRtl(this.rectF);
                                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                            }
                                            h4 += getCellHeight(getMeasuredWidth());
                                            k2++;
                                            if (this.isSingleCell && k2 >= this.itemsCount) {
                                                break;
                                            }
                                        }
                                    } else {
                                        float var_ = 0.5f;
                                        if (getViewType() == 11) {
                                            int k3 = 0;
                                            while (h4 <= getMeasuredHeight()) {
                                                this.rectF.set((float) AndroidUtilities.dp(18.0f), (float) AndroidUtilities.dp(14.0f), (((float) getMeasuredWidth()) * 0.5f) + ((float) AndroidUtilities.dp(this.randomParams[0] * 40.0f)), (float) (AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f)));
                                                checkRtl(this.rectF);
                                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                                this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(18.0f)), (float) AndroidUtilities.dp(14.0f), (((float) getMeasuredWidth()) - (((float) getMeasuredWidth()) * 0.2f)) - ((float) AndroidUtilities.dp(this.randomParams[0] * 20.0f)), (float) (AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f)));
                                                checkRtl(this.rectF);
                                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                                h4 += getCellHeight(getMeasuredWidth());
                                                k3++;
                                                if (this.isSingleCell && k3 >= this.itemsCount) {
                                                    break;
                                                }
                                            }
                                        } else {
                                            float var_ = 56.0f;
                                            if (getViewType() == 12) {
                                                int k4 = 0;
                                                int h6 = h4 + AndroidUtilities.dp(14.0f);
                                                while (h6 <= getMeasuredHeight()) {
                                                    int part = getMeasuredWidth() / i;
                                                    int i2 = 0;
                                                    while (i2 < i) {
                                                        float cx = ((float) (part * i2)) + (((float) part) / var_);
                                                        canvas2.drawCircle(cx, ((float) (AndroidUtilities.dp(7.0f) + h6)) + (((float) AndroidUtilities.dp(var_)) / var_), (float) AndroidUtilities.dp(28.0f), paint2);
                                                        float y = (float) (AndroidUtilities.dp(7.0f) + h6 + AndroidUtilities.dp(var_) + AndroidUtilities.dp(f2));
                                                        AndroidUtilities.rectTmp.set(cx - ((float) AndroidUtilities.dp(24.0f)), y - ((float) AndroidUtilities.dp(4.0f)), ((float) AndroidUtilities.dp(24.0f)) + cx, ((float) AndroidUtilities.dp(4.0f)) + y);
                                                        canvas2.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                                        i2++;
                                                        f2 = 16.0f;
                                                        i = 4;
                                                        var_ = 2.0f;
                                                        var_ = 56.0f;
                                                    }
                                                    h6 += getCellHeight(getMeasuredWidth());
                                                    k4++;
                                                    if (this.isSingleCell) {
                                                        break;
                                                    }
                                                    f2 = 16.0f;
                                                    i = 4;
                                                    var_ = 2.0f;
                                                    var_ = 56.0f;
                                                }
                                            } else {
                                                if (getViewType() == 13) {
                                                    float cy = ((float) getMeasuredHeight()) / 2.0f;
                                                    AndroidUtilities.rectTmp.set((float) AndroidUtilities.dp(40.0f), cy - ((float) AndroidUtilities.dp(4.0f)), (float) (getMeasuredWidth() - AndroidUtilities.dp(120.0f)), ((float) AndroidUtilities.dp(4.0f)) + cy);
                                                    canvas2.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                                    if (this.backgroundPaint == null) {
                                                        Paint paint4 = new Paint(1);
                                                        this.backgroundPaint = paint4;
                                                        paint4.setColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
                                                    }
                                                    for (int i3 = 0; i3 < 3; i3++) {
                                                        canvas2.drawCircle((float) ((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i3)), cy, (float) AndroidUtilities.dp(13.0f), this.backgroundPaint);
                                                        canvas2.drawCircle((float) ((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i3)), cy, (float) AndroidUtilities.dp(12.0f), paint2);
                                                    }
                                                    h2 = h4;
                                                } else if (getViewType() == 14 || getViewType() == 17) {
                                                    int x = AndroidUtilities.dp(12.0f);
                                                    int itemWidth = AndroidUtilities.dp(77.0f);
                                                    int INNER_RECT_SPACE = AndroidUtilities.dp(4.0f);
                                                    float BUBBLE_HEIGHT = (float) AndroidUtilities.dp(21.0f);
                                                    float BUBBLE_WIDTH = (float) AndroidUtilities.dp(41.0f);
                                                    while (x < getMeasuredWidth()) {
                                                        if (this.backgroundPaint == null) {
                                                            Paint paint5 = new Paint(1);
                                                            this.backgroundPaint = paint5;
                                                            paint5.setColor(Theme.getColor("dialogBackground"));
                                                        }
                                                        AndroidUtilities.rectTmp.set((float) (AndroidUtilities.dp(f5) + x), (float) AndroidUtilities.dp(f5), (float) ((x + itemWidth) - AndroidUtilities.dp(f5)), (float) (getMeasuredHeight() - AndroidUtilities.dp(f5)));
                                                        canvas2.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), paint2);
                                                        if (getViewType() == 14) {
                                                            float bubbleTop = (float) (AndroidUtilities.dp(var_) + INNER_RECT_SPACE);
                                                            float bubbleLeft = (float) (AndroidUtilities.dp(22.0f) + INNER_RECT_SPACE);
                                                            this.rectF.set(((float) x) + bubbleLeft, bubbleTop, ((float) x) + bubbleLeft + BUBBLE_WIDTH, bubbleTop + BUBBLE_HEIGHT);
                                                            RectF rectF2 = this.rectF;
                                                            canvas2.drawRoundRect(rectF2, rectF2.height() * var_, this.rectF.height() * var_, this.backgroundPaint);
                                                            float bubbleLeft2 = (float) (AndroidUtilities.dp(5.0f) + INNER_RECT_SPACE);
                                                            float bubbleTop2 = bubbleTop + ((float) AndroidUtilities.dp(4.0f)) + BUBBLE_HEIGHT;
                                                            this.rectF.set(((float) x) + bubbleLeft2, bubbleTop2, ((float) x) + bubbleLeft2 + BUBBLE_WIDTH, bubbleTop2 + BUBBLE_HEIGHT);
                                                            RectF rectF3 = this.rectF;
                                                            canvas2.drawRoundRect(rectF3, rectF3.height() * var_, this.rectF.height() * var_, this.backgroundPaint);
                                                            h3 = h4;
                                                        } else if (getViewType() == 17) {
                                                            float radius = (float) AndroidUtilities.dp(5.0f);
                                                            float squareSize = (float) AndroidUtilities.dp(32.0f);
                                                            float left = ((float) x) + ((((float) itemWidth) - squareSize) / 2.0f);
                                                            int top = AndroidUtilities.dp(21.0f);
                                                            h3 = h4;
                                                            AndroidUtilities.rectTmp.set(left, (float) top, left + squareSize, (float) (top + AndroidUtilities.dp(32.0f)));
                                                            canvas2.drawRoundRect(AndroidUtilities.rectTmp, radius, radius, this.backgroundPaint);
                                                        } else {
                                                            h3 = h4;
                                                        }
                                                        canvas2.drawCircle((float) ((itemWidth / 2) + x), (float) (getMeasuredHeight() - AndroidUtilities.dp(20.0f)), (float) AndroidUtilities.dp(8.0f), this.backgroundPaint);
                                                        x += itemWidth;
                                                        h4 = h3;
                                                        var_ = 0.5f;
                                                        f5 = 4.0f;
                                                        var_ = 8.0f;
                                                    }
                                                    h2 = h4;
                                                } else if (getViewType() == 15) {
                                                    int count = 0;
                                                    int radius2 = AndroidUtilities.dp(23.0f);
                                                    int rectRadius = AndroidUtilities.dp(4.0f);
                                                    while (h4 <= getMeasuredHeight()) {
                                                        canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(12.0f))) + ((float) radius2), (float) (AndroidUtilities.dp(8.0f) + h4 + radius2), (float) radius2, paint2);
                                                        this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(74.0f)), (float) (AndroidUtilities.dp(12.0f) + h4), (float) (this.paddingLeft + AndroidUtilities.dp(260.0f)), (float) (AndroidUtilities.dp(20.0f) + h4));
                                                        checkRtl(this.rectF);
                                                        canvas2.drawRoundRect(this.rectF, (float) rectRadius, (float) rectRadius, paint2);
                                                        this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(74.0f)), (float) (AndroidUtilities.dp(36.0f) + h4), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (AndroidUtilities.dp(42.0f) + h4));
                                                        checkRtl(this.rectF);
                                                        canvas2.drawRoundRect(this.rectF, (float) rectRadius, (float) rectRadius, paint2);
                                                        h4 += getCellHeight(getMeasuredWidth());
                                                        count++;
                                                        if (this.isSingleCell && count >= this.itemsCount) {
                                                            break;
                                                        }
                                                    }
                                                } else if (getViewType() == 16) {
                                                    int k5 = 0;
                                                    while (h4 <= getMeasuredHeight()) {
                                                        int r2 = AndroidUtilities.dp(18.0f);
                                                        canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(8.0f) + r2)), (float) (AndroidUtilities.dp(24.0f) + h4), (float) r2, paint2);
                                                        this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(58.0f)), (float) (AndroidUtilities.dp(20.0f) + h4), (float) (getWidth() - AndroidUtilities.dp(53.0f)), (float) (AndroidUtilities.dp(28.0f) + h4));
                                                        checkRtl(this.rectF);
                                                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(8.0f), (float) AndroidUtilities.dp(8.0f), paint2);
                                                        if (k5 < 4) {
                                                            int r3 = AndroidUtilities.dp(12.0f);
                                                            canvas2.drawCircle(checkRtl((float) ((getWidth() - AndroidUtilities.dp(12.0f)) - r3)), (float) (AndroidUtilities.dp(24.0f) + h4), (float) r3, paint2);
                                                        }
                                                        h4 += getCellHeight(getMeasuredWidth());
                                                        k5++;
                                                        if (this.isSingleCell && k5 >= this.itemsCount) {
                                                            break;
                                                        }
                                                    }
                                                } else {
                                                    h2 = h4;
                                                }
                                                int i4 = h2;
                                            }
                                        }
                                    }
                                    int k6 = 0;
                                    int h7 = h;
                                    while (h7 <= getMeasuredHeight()) {
                                        int r4 = AndroidUtilities.dp(23.0f);
                                        canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(9.0f) + r4)), (float) ((AndroidUtilities.dp(64.0f) >> 1) + h7), (float) r4, paint2);
                                        this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(var_)), (float) (AndroidUtilities.dp(17.0f) + h7), (float) (this.paddingLeft + AndroidUtilities.dp(f8)), (float) (AndroidUtilities.dp(25.0f) + h7));
                                        checkRtl(this.rectF);
                                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                        this.rectF.set((float) (this.paddingLeft + AndroidUtilities.dp(var_)), (float) (AndroidUtilities.dp(39.0f) + h7), (float) (this.paddingLeft + AndroidUtilities.dp(140.0f)), (float) (h7 + AndroidUtilities.dp(47.0f)));
                                        checkRtl(this.rectF);
                                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                        if (this.showDate) {
                                            this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + h7), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (h7 + AndroidUtilities.dp(28.0f)));
                                            checkRtl(this.rectF);
                                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                        }
                                        h7 += getCellHeight(getMeasuredWidth());
                                        k6++;
                                        if (this.isSingleCell && k6 >= this.itemsCount) {
                                            break;
                                        }
                                        f8 = 260.0f;
                                        var_ = 68.0f;
                                    }
                                } else {
                                    int k7 = 0;
                                    while (h4 <= getMeasuredHeight()) {
                                        this.rectF.set((float) AndroidUtilities.dp(10.0f), (float) (AndroidUtilities.dp(11.0f) + h4), (float) AndroidUtilities.dp(62.0f), (float) (AndroidUtilities.dp(63.0f) + h4));
                                        checkRtl(this.rectF);
                                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                        this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + h4), (float) AndroidUtilities.dp(140.0f), (float) (AndroidUtilities.dp(20.0f) + h4));
                                        checkRtl(this.rectF);
                                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                        this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + h4), (float) AndroidUtilities.dp(268.0f), (float) (AndroidUtilities.dp(42.0f) + h4));
                                        checkRtl(this.rectF);
                                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                        this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(54.0f) + h4), (float) AndroidUtilities.dp(188.0f), (float) (AndroidUtilities.dp(62.0f) + h4));
                                        checkRtl(this.rectF);
                                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                        if (this.showDate) {
                                            this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + h4), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(20.0f) + h4));
                                            checkRtl(this.rectF);
                                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                        }
                                        h4 += getCellHeight(getMeasuredWidth());
                                        k7++;
                                        if (this.isSingleCell && k7 >= this.itemsCount) {
                                            break;
                                        }
                                    }
                                }
                            } else {
                                int k8 = 0;
                                while (h4 <= getMeasuredHeight()) {
                                    int radius3 = AndroidUtilities.dp(44.0f) >> 1;
                                    canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(12.0f) + radius3)), (float) (AndroidUtilities.dp(6.0f) + h4 + radius3), (float) radius3, paint2);
                                    this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + h4), (float) AndroidUtilities.dp(140.0f), (float) (h4 + AndroidUtilities.dp(20.0f)));
                                    checkRtl(this.rectF);
                                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                    this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + h4), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(42.0f) + h4));
                                    checkRtl(this.rectF);
                                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                    if (this.showDate) {
                                        this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + h4), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (h4 + AndroidUtilities.dp(20.0f)));
                                        checkRtl(this.rectF);
                                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                    }
                                    h4 += getCellHeight(getMeasuredWidth());
                                    k8++;
                                    if (this.isSingleCell && k8 >= this.itemsCount) {
                                        break;
                                    }
                                }
                            }
                        } else {
                            int k9 = 0;
                            while (h4 <= getMeasuredHeight()) {
                                this.rectF.set((float) AndroidUtilities.dp(12.0f), (float) (AndroidUtilities.dp(8.0f) + h4), (float) AndroidUtilities.dp(52.0f), (float) (AndroidUtilities.dp(48.0f) + h4));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(12.0f) + h4), (float) AndroidUtilities.dp(140.0f), (float) (h4 + AndroidUtilities.dp(20.0f)));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(34.0f) + h4), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(42.0f) + h4));
                                checkRtl(this.rectF);
                                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                if (this.showDate) {
                                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(12.0f) + h4), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (h4 + AndroidUtilities.dp(20.0f)));
                                    checkRtl(this.rectF);
                                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                                }
                                h4 += getCellHeight(getMeasuredWidth());
                                k9++;
                                if (this.isSingleCell && k9 >= this.itemsCount) {
                                    break;
                                }
                            }
                        }
                    } else {
                        int photoWidth = (getMeasuredWidth() - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount();
                        int h8 = h4;
                        int k10 = 0;
                        while (true) {
                            if (h8 >= getMeasuredHeight() && !this.isSingleCell) {
                                int i5 = h8;
                                break;
                            }
                            for (int i6 = 0; i6 < getColumnsCount(); i6++) {
                                if (k10 != 0 || i6 >= this.skipDrawItemsCount) {
                                    int x2 = i6 * (AndroidUtilities.dp(2.0f) + photoWidth);
                                    canvas.drawRect((float) x2, (float) h8, (float) (x2 + photoWidth), (float) (h8 + photoWidth), paint2);
                                }
                            }
                            h8 += AndroidUtilities.dp(2.0f) + photoWidth;
                            k10++;
                            if (this.isSingleCell && k10 >= 2) {
                                int i7 = h8;
                                break;
                            }
                        }
                    }
                } else {
                    int k11 = 0;
                    while (h4 <= getMeasuredHeight()) {
                        int r5 = AndroidUtilities.dp(f6);
                        canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(9.0f) + r5)), (float) (h4 + (AndroidUtilities.dp(78.0f) >> 1)), (float) r5, paint2);
                        this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(f7) + h4), (float) AndroidUtilities.dp(f9), (float) (h4 + AndroidUtilities.dp(28.0f)));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                        this.rectF.set((float) AndroidUtilities.dp(68.0f), (float) (AndroidUtilities.dp(42.0f) + h4), (float) AndroidUtilities.dp(260.0f), (float) (AndroidUtilities.dp(50.0f) + h4));
                        checkRtl(this.rectF);
                        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                        if (this.showDate) {
                            this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(20.0f) + h4), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (h4 + AndroidUtilities.dp(28.0f)));
                            checkRtl(this.rectF);
                            canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                        }
                        h4 += getCellHeight(getMeasuredWidth());
                        k11++;
                        if (this.isSingleCell && k11 >= this.itemsCount) {
                            break;
                        }
                        f9 = 140.0f;
                        f6 = 25.0f;
                        f7 = 20.0f;
                    }
                }
            } else {
                int h9 = h4;
                int k12 = 0;
                while (true) {
                    if (h9 > getMeasuredHeight()) {
                        int k13 = h9;
                        break;
                    }
                    int r6 = AndroidUtilities.dp(25.0f);
                    canvas2.drawCircle(checkRtl((float) (this.paddingLeft + AndroidUtilities.dp(9.0f) + r6)), (float) (AndroidUtilities.dp(f) + h9), (float) r6, paint2);
                    int firstNameWidth = k12 % 2 == 0 ? 52 : 72;
                    this.rectF.set((float) AndroidUtilities.dp((float) 76), (float) (AndroidUtilities.dp(20.0f) + h9), (float) AndroidUtilities.dp((float) (76 + firstNameWidth)), (float) (h9 + AndroidUtilities.dp(28.0f)));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                    this.rectF.set((float) AndroidUtilities.dp((float) (76 + firstNameWidth + 8)), (float) (AndroidUtilities.dp(20.0f) + h9), (float) AndroidUtilities.dp((float) (76 + firstNameWidth + 8 + 84)), (float) (h9 + AndroidUtilities.dp(28.0f)));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                    this.rectF.set((float) AndroidUtilities.dp((float) 76), (float) (AndroidUtilities.dp(42.0f) + h9), (float) AndroidUtilities.dp((float) (76 + 64)), (float) (AndroidUtilities.dp(50.0f) + h9));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                    int i8 = r6;
                    canvas.drawLine((float) AndroidUtilities.dp((float) 76), (float) (getCellHeight(getMeasuredWidth()) + h9), (float) getMeasuredWidth(), (float) (getCellHeight(getMeasuredWidth()) + h9), paint2);
                    h9 += getCellHeight(getMeasuredWidth());
                    int k14 = k12 + 1;
                    if (this.isSingleCell && k14 >= this.itemsCount) {
                        int k15 = h9;
                        break;
                    } else {
                        k12 = k14;
                        f = 32.0f;
                    }
                }
            }
        } else {
            int k16 = 0;
            while (h4 <= getMeasuredHeight()) {
                int childH = getCellHeight(getMeasuredWidth());
                int r7 = AndroidUtilities.dp(f4);
                canvas2.drawCircle(checkRtl((float) (AndroidUtilities.dp(10.0f) + r7)), (float) ((childH >> 1) + h4), (float) r7, paint2);
                this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (h4 + AndroidUtilities.dp(16.0f)), (float) AndroidUtilities.dp(148.0f), (float) (h4 + AndroidUtilities.dp(f3)));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(38.0f) + h4), (float) AndroidUtilities.dp(268.0f), (float) (AndroidUtilities.dp(46.0f) + h4));
                checkRtl(this.rectF);
                canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                if (SharedConfig.useThreeLinesLayout) {
                    this.rectF.set((float) AndroidUtilities.dp(76.0f), (float) (AndroidUtilities.dp(54.0f) + h4), (float) AndroidUtilities.dp(220.0f), (float) (AndroidUtilities.dp(62.0f) + h4));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                }
                if (this.showDate) {
                    this.rectF.set((float) (getMeasuredWidth() - AndroidUtilities.dp(50.0f)), (float) (AndroidUtilities.dp(16.0f) + h4), (float) (getMeasuredWidth() - AndroidUtilities.dp(12.0f)), (float) (AndroidUtilities.dp(24.0f) + h4));
                    checkRtl(this.rectF);
                    canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint2);
                }
                h4 += getCellHeight(getMeasuredWidth());
                k16++;
                if (this.isSingleCell && k16 >= this.itemsCount) {
                    break;
                }
                f3 = 24.0f;
                f4 = 28.0f;
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
        if (this.isSingleCell || this.viewType == 13 || getViewType() == 14 || getViewType() == 17) {
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
            if (this.isSingleCell || (i = this.viewType) == 13 || i == 14 || i == 17) {
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
        switch (getViewType()) {
            case 1:
                return AndroidUtilities.dp(78.0f) + 1;
            case 2:
                return AndroidUtilities.dp(2.0f) + ((width - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount());
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
