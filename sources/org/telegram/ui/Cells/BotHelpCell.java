package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class BotHelpCell extends View {
    private boolean animating;
    private BotHelpCellDelegate delegate;
    private int height;
    private String oldText;
    private ClickableSpan pressedLink;
    private final Theme.ResourcesProvider resourcesProvider;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    private LinkPath urlPath = new LinkPath();
    public boolean wasDraw;
    private int width;

    public interface BotHelpCellDelegate {
        void didPressUrl(String str);
    }

    public BotHelpCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
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

    public void setText(boolean bot, String text) {
        int maxWidth;
        String str = text;
        if (str == null || text.length() == 0) {
            setVisibility(8);
        } else if (str == null || !str.equals(this.oldText)) {
            this.oldText = AndroidUtilities.getSafeString(text);
            setVisibility(0);
            if (AndroidUtilities.isTablet()) {
                maxWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
            } else {
                maxWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
            }
            String[] lines = str.split("\n");
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            String help = LocaleController.getString("BotInfoTitle", NUM);
            if (bot) {
                stringBuilder.append(help);
                stringBuilder.append("\n\n");
            }
            for (int a = 0; a < lines.length; a++) {
                stringBuilder.append(lines[a].trim());
                if (a != lines.length - 1) {
                    stringBuilder.append("\n");
                }
            }
            MessageObject.addLinks(false, stringBuilder);
            if (bot) {
                stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, help.length(), 33);
            }
            Emoji.replaceEmoji(stringBuilder, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            try {
                StaticLayout staticLayout = r4;
                StaticLayout staticLayout2 = new StaticLayout(stringBuilder, Theme.chat_msgTextPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.textLayout = staticLayout;
                this.width = 0;
                this.height = staticLayout.getHeight() + AndroidUtilities.dp(22.0f);
                int count = this.textLayout.getLineCount();
                for (int a2 = 0; a2 < count; a2++) {
                    this.width = (int) Math.ceil((double) Math.max((float) this.width, this.textLayout.getLineWidth(a2) + this.textLayout.getLineLeft(a2)));
                }
                if (this.width > maxWidth) {
                    this.width = maxWidth;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.width += AndroidUtilities.dp(22.0f);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        BotHelpCellDelegate botHelpCellDelegate;
        float x = event.getX();
        float y = event.getY();
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
                                ClickableSpan clickableSpan = link[0];
                                this.pressedLink = clickableSpan;
                                result = true;
                                try {
                                    int start = buffer.getSpanStart(clickableSpan);
                                    this.urlPath.setCurrentLayout(this.textLayout, start, 0.0f);
                                    this.textLayout.getSelectionPath(start, buffer.getSpanEnd(this.pressedLink), this.urlPath);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                            } else {
                                resetPressedLink();
                            }
                        }
                    } catch (Exception e2) {
                        resetPressedLink();
                        FileLog.e((Throwable) e2);
                    }
                } else {
                    ClickableSpan clickableSpan2 = this.pressedLink;
                    if (clickableSpan2 != null) {
                        try {
                            if (clickableSpan2 instanceof URLSpanNoUnderline) {
                                String url = ((URLSpanNoUnderline) clickableSpan2).getURL();
                                if ((url.startsWith("@") || url.startsWith("#") || url.startsWith("/")) && (botHelpCellDelegate = this.delegate) != null) {
                                    botHelpCellDelegate.didPressUrl(url);
                                }
                            } else if (clickableSpan2 instanceof URLSpan) {
                                BotHelpCellDelegate botHelpCellDelegate2 = this.delegate;
                                if (botHelpCellDelegate2 != null) {
                                    botHelpCellDelegate2.didPressUrl(((URLSpan) clickableSpan2).getURL());
                                }
                            } else {
                                clickableSpan2.onClick(this);
                            }
                        } catch (Exception e3) {
                            FileLog.e((Throwable) e3);
                        }
                        resetPressedLink();
                        result = true;
                    }
                }
            } else if (event.getAction() == 3) {
                resetPressedLink();
            }
        }
        return result || super.onTouchEvent(event);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), this.height + AndroidUtilities.dp(8.0f));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int x = (getWidth() - this.width) / 2;
        int y = AndroidUtilities.dp(2.0f);
        Drawable shadowDrawable = Theme.chat_msgInMediaDrawable.getShadowDrawable();
        if (shadowDrawable != null) {
            shadowDrawable.setBounds(x, y, this.width + x, this.height + y);
            shadowDrawable.draw(canvas);
        }
        int w = AndroidUtilities.displaySize.x;
        int h = AndroidUtilities.displaySize.y;
        if (getParent() instanceof View) {
            View view = (View) getParent();
            w = view.getMeasuredWidth();
            h = view.getMeasuredHeight();
        }
        Theme.MessageDrawable drawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInMedia");
        drawable.setTop((int) getY(), w, h, false, false);
        drawable.setBounds(x, y, this.width + x, this.height + y);
        drawable.draw(canvas);
        Theme.chat_msgTextPaint.setColor(getThemedColor("chat_messageTextIn"));
        Theme.chat_msgTextPaint.linkColor = getThemedColor("chat_messageLinkIn");
        canvas.save();
        int dp = AndroidUtilities.dp(11.0f) + x;
        this.textX = dp;
        int dp2 = AndroidUtilities.dp(11.0f) + y;
        this.textY = dp2;
        canvas.translate((float) dp, (float) dp2);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, Theme.chat_urlPaint);
        }
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            staticLayout.draw(canvas);
        }
        canvas.restore();
        this.wasDraw = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasDraw = false;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setText(this.textLayout.getText());
    }

    public boolean animating() {
        return this.animating;
    }

    public void setAnimating(boolean animating2) {
        this.animating = animating2;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private Drawable getThemedDrawable(String drawableKey) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Drawable drawable = resourcesProvider2 != null ? resourcesProvider2.getDrawable(drawableKey) : null;
        return drawable != null ? drawable : Theme.getThemeDrawable(drawableKey);
    }
}
