package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
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
import org.telegram.messenger.volley.DefaultRetryPolicy;
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
    private TextPaint textPaint = new TextPaint(1);
    private int textX;
    private int textY;
    private Paint urlPaint;
    private LinkPath urlPath = new LinkPath();

    public interface AboutLinkCellDelegate {
        void didPressUrl(String str);
    }

    public AboutLinkCell(Context context) {
        float f = 16.0f;
        super(context);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.textPaint.setColor(-16777216);
        this.textPaint.linkColor = Theme.MSG_LINK_TEXT_COLOR;
        this.urlPaint = new Paint();
        this.urlPaint.setColor(Theme.MSG_LINK_SELECT_BACKGROUND_COLOR);
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        View view = this.imageView;
        int i = (LocaleController.isRTL ? 5 : 3) | 48;
        float f2 = LocaleController.isRTL ? 0.0f : 16.0f;
        if (!LocaleController.isRTL) {
            f = 0.0f;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i, f2, 5.0f, f, 0.0f));
        setWillNotDraw(false);
    }

    public void setDelegate(AboutLinkCellDelegate botHelpCellDelegate) {
        this.delegate = botHelpCellDelegate;
    }

    private void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        invalidate();
    }

    public void setTextAndIcon(String text, int resId) {
        if (text == null || text.length() == 0) {
            setVisibility(8);
        } else if (text == null || this.oldText == null || !text.equals(this.oldText)) {
            this.oldText = text;
            this.stringBuilder = new SpannableStringBuilder(this.oldText);
            MessageObject.addLinks(this.stringBuilder, false);
            Emoji.replaceEmoji(this.stringBuilder, this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            requestLayout();
            if (resId == 0) {
                this.imageView.setImageDrawable(null);
            } else {
                this.imageView.setImageResource(resId);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (VERSION.SDK_INT >= 21 && getBackground() != null && (event.getAction() == 0 || event.getAction() == 2)) {
            getBackground().setHotspot(x, y);
        }
        boolean result = false;
        if (this.textLayout != null) {
            if (event.getAction() == 0 || (this.pressedLink != null && event.getAction() == 1)) {
                if (event.getAction() == 0) {
                    resetPressedLink();
                    try {
                        int x2 = (int) (x - ((float) this.textX));
                        int line = this.textLayout.getLineForVertical((int) (y - ((float) this.textY)));
                        int off = this.textLayout.getOffsetForHorizontal(line, (float) x2);
                        float left = this.textLayout.getLineLeft(line);
                        if (left > ((float) x2) || this.textLayout.getLineWidth(line) + left < ((float) x2)) {
                            resetPressedLink();
                        } else {
                            Spannable buffer = (Spannable) this.textLayout.getText();
                            ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                            if (link.length != 0) {
                                resetPressedLink();
                                this.pressedLink = link[0];
                                result = true;
                                try {
                                    int start = buffer.getSpanStart(this.pressedLink);
                                    this.urlPath.setCurrentLayout(this.textLayout, start, 0.0f);
                                    this.textLayout.getSelectionPath(start, buffer.getSpanEnd(this.pressedLink), this.urlPath);
                                } catch (Throwable e) {
                                    FileLog.e("tmessages", e);
                                }
                            } else {
                                resetPressedLink();
                            }
                        }
                    } catch (Throwable e2) {
                        resetPressedLink();
                        FileLog.e("tmessages", e2);
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
                        FileLog.e("tmessages", e22);
                    }
                    resetPressedLink();
                    result = true;
                }
            } else if (event.getAction() == 3) {
                resetPressedLink();
            }
        }
        if (result || super.onTouchEvent(event)) {
            return true;
        }
        return false;
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.textLayout = new StaticLayout(this.stringBuilder, this.textPaint, MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(87.0f), Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(this.textLayout.getHeight() + AndroidUtilities.dp(16.0f), NUM));
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
            canvas.drawPath(this.urlPath, this.urlPaint);
        }
        try {
            if (this.textLayout != null) {
                this.textLayout.draw(canvas);
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        canvas.restore();
    }
}
