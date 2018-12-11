package org.telegram.p005ui.Cells;

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
import com.google.android.exoplayer2.CLASSNAMEC;
import java.io.File;
import java.io.FileInputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.Theme.ThemeInfo;
import org.telegram.p005ui.Components.LayoutHelper;

/* renamed from: org.telegram.ui.Cells.ThemeCell */
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
        int i;
        int i2 = 3;
        super(context);
        setWillNotDraw(false);
        this.isNightTheme = nightTheme;
        this.textView = new TextView(context);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setPadding(0, 0, 0, AndroidUtilities.m9dp(1.0f));
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        View view = this.textView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -1.0f, i | 48, LocaleController.isRTL ? 105.0f : 60.0f, 0.0f, LocaleController.isRTL ? 60.0f : 105.0f, 0.0f));
        this.checkImage = new ImageView(context);
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), Mode.MULTIPLY));
        this.checkImage.setImageResource(CLASSNAMER.drawable.sticker_added);
        if (this.isNightTheme) {
            view = this.checkImage;
            if (!LocaleController.isRTL) {
                i2 = 5;
            }
            addView(view, LayoutHelper.createFrame(19, 14.0f, i2 | 16, 21.0f, 0.0f, 21.0f, 0.0f));
            return;
        }
        view = this.checkImage;
        if (LocaleController.isRTL) {
            i = 3;
        } else {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(19, 14.0f, i | 16, 59.0f, 0.0f, 59.0f, 0.0f));
        this.optionsButton = new ImageView(context);
        this.optionsButton.setFocusable(false);
        this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
        this.optionsButton.setImageResource(CLASSNAMER.drawable.ic_ab_other);
        this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), Mode.MULTIPLY));
        this.optionsButton.setScaleType(ScaleType.CENTER);
        View view2 = this.optionsButton;
        if (!LocaleController.isRTL) {
            i2 = 5;
        }
        addView(view2, LayoutHelper.createFrame(48, 48, i2 | 48));
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), Mode.MULTIPLY));
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.m9dp(50.0f), NUM));
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

    /* JADX WARNING: Removed duplicated region for block: B:92:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00ce  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setTheme(ThemeInfo themeInfo, boolean divider) {
        FileInputStream stream;
        Throwable e;
        Throwable th;
        this.currentThemeInfo = themeInfo;
        String text = themeInfo.getName();
        if (text.endsWith(".attheme")) {
            text = text.substring(0, text.lastIndexOf(46));
        }
        this.textView.setText(text);
        this.needDivider = divider;
        updateCurrentThemeCheck();
        boolean finished = false;
        if (!(themeInfo.pathToFile == null && themeInfo.assetName == null)) {
            stream = null;
            int currentPosition = 0;
            try {
                File file;
                if (themeInfo.assetName != null) {
                    file = Theme.getAssetFile(themeInfo.assetName);
                } else {
                    file = new File(themeInfo.pathToFile);
                }
                FileInputStream fileInputStream = new FileInputStream(file);
                int linesRead = 0;
                do {
                    try {
                        int read = fileInputStream.read(bytes);
                        if (read == -1) {
                            break;
                        }
                        int previousPosition = currentPosition;
                        int start = 0;
                        for (int a = 0; a < read; a++) {
                            if (bytes[a] == (byte) 10) {
                                linesRead++;
                                int len = (a - start) + 1;
                                String line = new String(bytes, start, len - 1, CLASSNAMEC.UTF8_NAME);
                                if (line.startsWith("WPS")) {
                                    break;
                                }
                                int idx = line.indexOf(61);
                                if (idx == -1 || !line.substring(0, idx).equals(Theme.key_actionBarDefault)) {
                                    start += len;
                                    currentPosition += len;
                                } else {
                                    int value;
                                    String param = line.substring(idx + 1);
                                    if (param.length() <= 0 || param.charAt(0) != '#') {
                                        value = Utilities.parseInt(param).intValue();
                                    } else {
                                        try {
                                            value = Color.parseColor(param);
                                        } catch (Exception e2) {
                                            value = Utilities.parseInt(param).intValue();
                                        }
                                    }
                                    finished = true;
                                    this.paint.setColor(value);
                                }
                            }
                        }
                        if (previousPosition == currentPosition || linesRead >= 500) {
                            break;
                        }
                        fileInputStream.getChannel().position((long) currentPosition);
                    } catch (Throwable th2) {
                        th = th2;
                        stream = fileInputStream;
                    }
                } while (!finished);
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable e3) {
                        FileLog.m13e(e3);
                    }
                }
            } catch (Throwable th3) {
                e3 = th3;
            }
        }
        if (finished) {
            this.paint.setColor(Theme.getDefaultColor(Theme.key_actionBarDefault));
            return;
        }
        return;
        try {
            FileLog.m13e(e3);
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable e32) {
                    FileLog.m13e(e32);
                }
            }
            if (finished) {
            }
        } catch (Throwable th4) {
            th = th4;
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable e322) {
                    FileLog.m13e(e322);
                }
            }
            throw th;
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
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.m9dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.m9dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
        int x = AndroidUtilities.m9dp(31.0f);
        if (LocaleController.isRTL) {
            x = getWidth() - x;
        }
        canvas.drawCircle((float) x, (float) AndroidUtilities.m9dp(24.0f), (float) AndroidUtilities.m9dp(11.0f), this.paint);
    }
}
