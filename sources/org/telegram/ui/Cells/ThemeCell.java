package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.Components.LayoutHelper;

public class ThemeCell extends FrameLayout {
    private static byte[] bytes = new byte[1024];
    private ImageView checkImage;
    private ThemeInfo currentThemeInfo;
    private boolean isNightTheme;
    private boolean needDivider;
    private ImageView optionsButton;
    private Paint paint = new Paint(1);
    private TextView textView;

    public ThemeCell(Context context, boolean nightTheme) {
        Context context2 = context;
        super(context);
        setWillNotDraw(false);
        this.isNightTheme = nightTheme;
        this.textView = new TextView(context2);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0f));
        this.textView.setEllipsize(TruncateAt.END);
        int i = 3;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(r0.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 101.0f : 60.0f, 0.0f, LocaleController.isRTL ? 60.0f : 101.0f, 0.0f));
        r0.checkImage = new ImageView(context2);
        r0.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), Mode.MULTIPLY));
        r0.checkImage.setImageResource(R.drawable.sticker_added);
        if (r0.isNightTheme) {
            View view = r0.checkImage;
            if (!LocaleController.isRTL) {
                i = 5;
            }
            addView(view, LayoutHelper.createFrame(19, 14.0f, i | 16, 17.0f, 0.0f, 17.0f, 0.0f));
            return;
        }
        addView(r0.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : 5) | 16, 55.0f, 0.0f, 55.0f, 0.0f));
        r0.optionsButton = new ImageView(context2);
        r0.optionsButton.setFocusable(false);
        r0.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
        r0.optionsButton.setImageResource(R.drawable.ic_ab_other);
        r0.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), Mode.MULTIPLY));
        r0.optionsButton.setScaleType(ScaleType.CENTER);
        view = r0.optionsButton;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(48, 48, i | 48));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), Mode.MULTIPLY));
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f) + this.needDivider, NUM));
    }

    public void setOnOptionsClick(OnClickListener listener) {
        this.optionsButton.setOnClickListener(listener);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public ThemeInfo getCurrentThemeInfo() {
        return this.currentThemeInfo;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setTheme(ThemeInfo themeInfo, boolean divider) {
        Throwable e;
        boolean finished;
        ThemeInfo themeInfo2 = themeInfo;
        this.currentThemeInfo = themeInfo2;
        String text = themeInfo.getName();
        int i = 0;
        if (text.endsWith(".attheme")) {
            text = text.substring(0, text.lastIndexOf(46));
        }
        r1.textView.setText(text);
        r1.needDivider = divider;
        updateCurrentThemeCheck();
        boolean finished2 = false;
        if (themeInfo2.pathToFile == null) {
            if (themeInfo2.assetName == null) {
                String str = text;
                if (!finished2) {
                    r1.paint.setColor(Theme.getDefaultColor(Theme.key_actionBarDefault));
                }
            }
        }
        FileInputStream stream = null;
        int currentPosition = 0;
        try {
            File file;
            if (themeInfo2.assetName != null) {
                try {
                    file = Theme.getAssetFile(themeInfo2.assetName);
                } catch (Throwable th) {
                    e = th;
                    str = text;
                    if (stream != null) {
                        stream.close();
                    }
                    throw e;
                }
            }
            file = new File(themeInfo2.pathToFile);
            stream = new FileInputStream(file);
            finished = false;
            int linesRead = 0;
            while (true) {
                try {
                    int read = stream.read(bytes);
                    int read2 = read;
                    if (read == -1) {
                        break;
                    }
                    boolean z;
                    read = currentPosition;
                    int start = 0;
                    int currentPosition2 = currentPosition;
                    currentPosition = linesRead;
                    linesRead = i;
                    while (linesRead < read2) {
                        if (bytes[linesRead] == (byte) 10) {
                            currentPosition++;
                            i = (linesRead - start) + 1;
                            str = text;
                            try {
                                String line = new String(bytes, start, i - 1, C0539C.UTF8_NAME);
                                if (line.startsWith("WPS") == null) {
                                    text = line.indexOf(61);
                                    int idx = text;
                                    if (text != -1) {
                                        text = line.substring(null, idx);
                                        if (text.equals(Theme.key_actionBarDefault)) {
                                            String param = line.substring(idx + 1);
                                            String key;
                                            if (param.length() > 0) {
                                                key = text;
                                                if (param.charAt(null) == 35) {
                                                    try {
                                                        line = Color.parseColor(param);
                                                    } catch (Exception e2) {
                                                        Exception line2 = e2;
                                                        line = Utilities.parseInt(param).intValue();
                                                    }
                                                    r1.paint.setColor(line);
                                                    finished = true;
                                                }
                                            } else {
                                                key = text;
                                            }
                                            line = Utilities.parseInt(param).intValue();
                                            try {
                                                r1.paint.setColor(line);
                                                finished = true;
                                            } catch (Throwable th2) {
                                                e = th2;
                                                int i2 = 1;
                                            }
                                        }
                                    }
                                    String str2 = line;
                                    start += i;
                                    currentPosition2 += i;
                                }
                                linesRead = currentPosition;
                                break;
                            } catch (Throwable th22) {
                                e = th22;
                            }
                        } else {
                            str = text;
                        }
                        linesRead++;
                        text = str;
                        themeInfo2 = themeInfo;
                        z = divider;
                    }
                    str = text;
                    linesRead = currentPosition;
                    if (read == currentPosition2) {
                        break;
                    } else if (linesRead >= 500) {
                        break;
                    } else {
                        stream.getChannel().position((long) currentPosition2);
                        if (finished) {
                            break;
                        }
                        currentPosition = currentPosition2;
                        text = str;
                        themeInfo2 = themeInfo;
                        z = divider;
                        i = 0;
                    }
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable th222) {
                            FileLog.m3e(th222);
                        }
                    }
                } catch (Throwable th2222) {
                    str = text;
                    e = th2222;
                }
                if (!finished2) {
                    r1.paint.setColor(Theme.getDefaultColor(Theme.key_actionBarDefault));
                }
            }
            finished2 = finished;
            if (stream != null) {
                stream.close();
            }
        } catch (Throwable th22222) {
            str = text;
            e = th22222;
            finished = false;
            if (stream != null) {
                stream.close();
            }
            throw e;
        }
        if (!finished2) {
            r1.paint.setColor(Theme.getDefaultColor(Theme.key_actionBarDefault));
        }
    }

    public void updateCurrentThemeCheck() {
        ThemeInfo currentTheme;
        if (this.isNightTheme) {
            currentTheme = Theme.getCurrentNightTheme();
        } else {
            currentTheme = Theme.getCurrentTheme();
        }
        int newVisibility = this.currentThemeInfo == currentTheme ? 0 : 4;
        if (this.checkImage.getVisibility() != newVisibility) {
            this.checkImage.setVisibility(newVisibility);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
        int x = AndroidUtilities.dp(NUM);
        if (LocaleController.isRTL) {
            x = getWidth() - x;
        }
        canvas.drawCircle((float) x, (float) AndroidUtilities.dp(24.0f), (float) AndroidUtilities.dp(11.0f), this.paint);
    }
}
