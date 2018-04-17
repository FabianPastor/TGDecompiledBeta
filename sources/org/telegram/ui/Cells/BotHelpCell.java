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
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.beta.R;
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

    public void setText(String text) {
        Throwable e;
        BotHelpCell botHelpCell = this;
        String str = text;
        if (str != null) {
            if (text.length() != 0) {
                if (str == null || botHelpCell.oldText == null || !str.equals(botHelpCell.oldText)) {
                    int maxWidth;
                    int a;
                    float f;
                    botHelpCell.oldText = str;
                    setVisibility(0);
                    if (AndroidUtilities.isTablet()) {
                        maxWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * NUM);
                    } else {
                        maxWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
                    }
                    String[] lines = str.split("\n");
                    SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                    String help = LocaleController.getString("BotInfoTitle", R.string.BotInfoTitle);
                    stringBuilder.append(help);
                    stringBuilder.append("\n\n");
                    for (a = 0; a < lines.length; a++) {
                        stringBuilder.append(lines[a].trim());
                        if (a != lines.length - 1) {
                            stringBuilder.append("\n");
                        }
                    }
                    MessageObject.addLinks(false, stringBuilder);
                    stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, help.length(), 33);
                    Emoji.replaceEmoji(stringBuilder, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    try {
                        StaticLayout staticLayout = staticLayout;
                        StaticLayout staticLayout2 = staticLayout;
                        f = 22.0f;
                        try {
                            staticLayout = new StaticLayout(stringBuilder, Theme.chat_msgTextPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            botHelpCell.textLayout = staticLayout2;
                            int a2 = 0;
                            botHelpCell.width = 0;
                            botHelpCell.height = botHelpCell.textLayout.getHeight() + AndroidUtilities.dp(f);
                            a = botHelpCell.textLayout.getLineCount();
                            while (a2 < a) {
                                botHelpCell.width = (int) Math.ceil((double) Math.max((float) botHelpCell.width, botHelpCell.textLayout.getLineWidth(a2) + botHelpCell.textLayout.getLineLeft(a2)));
                                a2++;
                            }
                            if (botHelpCell.width > maxWidth) {
                                botHelpCell.width = maxWidth;
                            }
                        } catch (Exception e2) {
                            e = e2;
                            FileLog.m3e(e);
                            botHelpCell.width += AndroidUtilities.dp(f);
                            return;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        f = 22.0f;
                        FileLog.m3e(e);
                        botHelpCell.width += AndroidUtilities.dp(f);
                        return;
                    }
                    botHelpCell.width += AndroidUtilities.dp(f);
                    return;
                }
                return;
            }
        }
        setVisibility(8);
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        boolean result = false;
        if (this.textLayout != null) {
            if (event.getAction() != 0) {
                if (r1.pressedLink == null || event.getAction() != 1) {
                    if (event.getAction() == 3) {
                        resetPressedLink();
                    }
                }
            }
            if (event.getAction() == 0) {
                resetPressedLink();
                try {
                    int x2 = (int) (x - ((float) r1.textX));
                    int line = r1.textLayout.getLineForVertical((int) (y - ((float) r1.textY)));
                    int off = r1.textLayout.getOffsetForHorizontal(line, (float) x2);
                    float left = r1.textLayout.getLineLeft(line);
                    if (left > ((float) x2) || r1.textLayout.getLineWidth(line) + left < ((float) x2)) {
                        resetPressedLink();
                    } else {
                        Spannable buffer = (Spannable) r1.textLayout.getText();
                        ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                        if (link.length != 0) {
                            resetPressedLink();
                            r1.pressedLink = link[0];
                            result = true;
                            try {
                                int start = buffer.getSpanStart(r1.pressedLink);
                                r1.urlPath.setCurrentLayout(r1.textLayout, start, 0.0f);
                                r1.textLayout.getSelectionPath(start, buffer.getSpanEnd(r1.pressedLink), r1.urlPath);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        } else {
                            resetPressedLink();
                        }
                    }
                } catch (Throwable e2) {
                    boolean result2 = result;
                    Throwable e3 = e2;
                    resetPressedLink();
                    FileLog.m3e(e3);
                    result = result2;
                }
            } else if (r1.pressedLink != null) {
                try {
                    if (r1.pressedLink instanceof URLSpanNoUnderline) {
                        String url = ((URLSpanNoUnderline) r1.pressedLink).getURL();
                        if ((url.startsWith("@") || url.startsWith("#") || url.startsWith("/")) && r1.delegate != null) {
                            r1.delegate.didPressUrl(url);
                        }
                    } else if (r1.pressedLink instanceof URLSpan) {
                        Browser.openUrl(getContext(), ((URLSpan) r1.pressedLink).getURL());
                    } else {
                        r1.pressedLink.onClick(r1);
                    }
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
                resetPressedLink();
                result = true;
            }
        }
        if (!result) {
            if (!super.onTouchEvent(event)) {
                return false;
            }
        }
        return true;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), this.height + AndroidUtilities.dp(8.0f));
    }

    protected void onDraw(Canvas canvas) {
        int x = (canvas.getWidth() - this.width) / 2;
        int y = AndroidUtilities.dp(NUM);
        Theme.chat_msgInMediaShadowDrawable.setBounds(x, y, this.width + x, this.height + y);
        Theme.chat_msgInMediaShadowDrawable.draw(canvas);
        Theme.chat_msgInMediaDrawable.setBounds(x, y, this.width + x, this.height + y);
        Theme.chat_msgInMediaDrawable.draw(canvas);
        Theme.chat_msgTextPaint.setColor(Theme.getColor(Theme.key_chat_messageTextIn));
        Theme.chat_msgTextPaint.linkColor = Theme.getColor(Theme.key_chat_messageLinkIn);
        canvas.save();
        int dp = AndroidUtilities.dp(11.0f) + x;
        this.textX = dp;
        float f = (float) dp;
        int dp2 = AndroidUtilities.dp(11.0f) + y;
        this.textY = dp2;
        canvas.translate(f, (float) dp2);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, Theme.chat_urlPaint);
        }
        if (this.textLayout != null) {
            this.textLayout.draw(canvas);
        }
        canvas.restore();
    }
}
