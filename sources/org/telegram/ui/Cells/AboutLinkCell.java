package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.StaticLayout.Builder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class AboutLinkCell extends FrameLayout {
    private AboutLinkCellDelegate delegate;
    private ImageView imageView;
    private String oldText;
    private ClickableSpan pressedLink;
    private SpannableStringBuilder stringBuilder;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    private LinkPath urlPath = new LinkPath();

    public interface AboutLinkCellDelegate {
        void didPressUrl(String str);
    }

    public AboutLinkCell(Context context) {
        super(context);
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), Mode.MULTIPLY));
        addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 5.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        setWillNotDraw(null);
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public void setDelegate(AboutLinkCellDelegate aboutLinkCellDelegate) {
        this.delegate = aboutLinkCellDelegate;
    }

    private void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        invalidate();
    }

    public void setTextAndIcon(String str, int i, boolean z) {
        if (!TextUtils.isEmpty(str)) {
            if (str == null || this.oldText == null || !str.equals(this.oldText)) {
                this.oldText = str;
                this.stringBuilder = new SpannableStringBuilder(this.oldText);
                if (z) {
                    MessageObject.addLinks(false, this.stringBuilder, false);
                }
                Emoji.replaceEmoji(this.stringBuilder, Theme.profile_aboutTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                requestLayout();
                if (i == 0) {
                    this.imageView.setImageDrawable(0);
                } else {
                    this.imageView.setImageResource(i);
                }
            }
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        Throwable e;
        boolean z;
        boolean z2;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (this.textLayout != null) {
            if (motionEvent.getAction() != 0) {
                if (this.pressedLink == null || motionEvent.getAction() != 1) {
                    if (motionEvent.getAction() == 3) {
                        resetPressedLink();
                    }
                }
            }
            if (motionEvent.getAction() == 0) {
                resetPressedLink();
                try {
                    int i = (int) (x - ((float) this.textX));
                    int lineForVertical = this.textLayout.getLineForVertical((int) (y - ((float) this.textY)));
                    x = (float) i;
                    int offsetForHorizontal = this.textLayout.getOffsetForHorizontal(lineForVertical, x);
                    float lineLeft = this.textLayout.getLineLeft(lineForVertical);
                    if (lineLeft > x || lineLeft + this.textLayout.getLineWidth(lineForVertical) < x) {
                        resetPressedLink();
                    } else {
                        Spannable spannable = (Spannable) this.textLayout.getText();
                        ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
                        if (clickableSpanArr.length != 0) {
                            resetPressedLink();
                            this.pressedLink = clickableSpanArr[0];
                            try {
                                lineForVertical = spannable.getSpanStart(this.pressedLink);
                                this.urlPath.setCurrentLayout(this.textLayout, lineForVertical, 0.0f);
                                this.textLayout.getSelectionPath(lineForVertical, spannable.getSpanEnd(this.pressedLink), this.urlPath);
                            } catch (Throwable e2) {
                                try {
                                    FileLog.m3e(e2);
                                } catch (Exception e3) {
                                    e2 = e3;
                                    z = true;
                                    resetPressedLink();
                                    FileLog.m3e(e2);
                                    z2 = z;
                                    if (!z2) {
                                    }
                                    return true;
                                }
                            }
                        }
                        resetPressedLink();
                    }
                } catch (Exception e4) {
                    e2 = e4;
                    z = false;
                    resetPressedLink();
                    FileLog.m3e(e2);
                    z2 = z;
                    if (z2) {
                    }
                    return true;
                }
            } else if (this.pressedLink != null) {
                try {
                    if (this.pressedLink instanceof URLSpanNoUnderline) {
                        String url = ((URLSpanNoUnderline) this.pressedLink).getURL();
                        if ((url.startsWith("@") || url.startsWith("#") || url.startsWith("/")) && this.delegate != null) {
                            this.delegate.didPressUrl(url);
                        }
                    } else if (this.pressedLink instanceof URLSpan) {
                        Browser.openUrl(getContext(), ((URLSpan) this.pressedLink).getURL());
                    } else {
                        this.pressedLink.onClick(this);
                    }
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
                resetPressedLink();
            }
            z2 = true;
            if (z2 || super.onTouchEvent(motionEvent) != null) {
                return true;
            }
            return false;
        }
        z2 = false;
        if (z2) {
        }
        return true;
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int i, int i2) {
        if (this.stringBuilder != 0) {
            int size = MeasureSpec.getSize(i) - AndroidUtilities.dp(87.0f);
            if (VERSION.SDK_INT >= 24) {
                this.textLayout = Builder.obtain(this.stringBuilder, 0, this.stringBuilder.length(), Theme.profile_aboutTextPaint, size).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Alignment.ALIGN_NORMAL).build();
            } else {
                this.textLayout = new StaticLayout(this.stringBuilder, Theme.profile_aboutTextPaint, size, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec((this.textLayout != null ? this.textLayout.getHeight() : AndroidUtilities.dp(20.0f)) + AndroidUtilities.dp(16.0f), NUM));
    }

    protected void onDraw(Canvas canvas) {
        canvas.save();
        int dp = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 71.0f);
        this.textX = dp;
        float f = (float) dp;
        int dp2 = AndroidUtilities.dp(8.0f);
        this.textY = dp2;
        canvas.translate(f, (float) dp2);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
        }
        try {
            if (this.textLayout != null) {
                this.textLayout.draw(canvas);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        canvas.restore();
    }
}
