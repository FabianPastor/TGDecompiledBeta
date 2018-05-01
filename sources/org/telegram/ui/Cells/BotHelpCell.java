package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class BotHelpCell extends View {
    private BotHelpCellDelegate delegate;
    private int height;
    private String oldText;
    private ClickableSpan pressedLink;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    private LinkPath urlPath = new LinkPath();
    private int width;

    public interface BotHelpCellDelegate {
        void didPressUrl(String str);
    }

    public BotHelpCell(Context context) {
        super(context);
    }

    public void setDelegate(BotHelpCellDelegate botHelpCellDelegate) {
        this.delegate = botHelpCellDelegate;
    }

    private void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        invalidate();
    }

    public void setText(String str) {
        if (str != null) {
            if (str.length() != 0) {
                if (str == null || this.oldText == null || !str.equals(this.oldText)) {
                    int minTabletSide;
                    this.oldText = str;
                    int i = 0;
                    setVisibility(0);
                    if (AndroidUtilities.isTablet()) {
                        minTabletSide = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
                    } else {
                        minTabletSide = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
                    }
                    str = str.split("\n");
                    CharSequence spannableStringBuilder = new SpannableStringBuilder();
                    Object string = LocaleController.getString("BotInfoTitle", C0446R.string.BotInfoTitle);
                    spannableStringBuilder.append(string);
                    spannableStringBuilder.append("\n\n");
                    for (int i2 = 0; i2 < str.length; i2++) {
                        spannableStringBuilder.append(str[i2].trim());
                        if (i2 != str.length - 1) {
                            spannableStringBuilder.append("\n");
                        }
                    }
                    MessageObject.addLinks(false, spannableStringBuilder);
                    spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, string.length(), 33);
                    Emoji.replaceEmoji(spannableStringBuilder, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    try {
                        this.textLayout = new StaticLayout(spannableStringBuilder, Theme.chat_msgTextPaint, minTabletSide, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        this.width = 0;
                        this.height = this.textLayout.getHeight() + AndroidUtilities.dp(22.0f);
                        int lineCount = this.textLayout.getLineCount();
                        while (i < lineCount) {
                            this.width = (int) Math.ceil((double) Math.max((float) this.width, this.textLayout.getLineWidth(i) + this.textLayout.getLineLeft(i)));
                            i++;
                        }
                        if (this.width > minTabletSide) {
                            this.width = minTabletSide;
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    this.width += AndroidUtilities.dp(22.0f);
                    return;
                }
                return;
            }
        }
        setVisibility(8);
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

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), this.height + AndroidUtilities.dp(8.0f));
    }

    protected void onDraw(Canvas canvas) {
        int width = (canvas.getWidth() - this.width) / 2;
        int dp = AndroidUtilities.dp(4.0f);
        Theme.chat_msgInMediaShadowDrawable.setBounds(width, dp, this.width + width, this.height + dp);
        Theme.chat_msgInMediaShadowDrawable.draw(canvas);
        Theme.chat_msgInMediaDrawable.setBounds(width, dp, this.width + width, this.height + dp);
        Theme.chat_msgInMediaDrawable.draw(canvas);
        Theme.chat_msgTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
        Theme.chat_msgTextPaint.linkColor = Theme.getColor(Theme.key_chat_messageLinkIn);
        canvas.save();
        int dp2 = AndroidUtilities.dp(11.0f) + width;
        this.textX = dp2;
        float f = (float) dp2;
        int dp3 = AndroidUtilities.dp(11.0f) + dp;
        this.textY = dp3;
        canvas.translate(f, (float) dp3);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, Theme.chat_urlPaint);
        }
        if (this.textLayout != null) {
            this.textLayout.draw(canvas);
        }
        canvas.restore();
    }
}
