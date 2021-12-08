package org.telegram.ui.Cells;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class AboutLinkCell extends FrameLayout {
    Runnable longPressedRunnable = new Runnable() {
        public void run() {
            String url;
            if (AboutLinkCell.this.pressedLink != null) {
                if (AboutLinkCell.this.pressedLink instanceof URLSpanNoUnderline) {
                    url = ((URLSpanNoUnderline) AboutLinkCell.this.pressedLink).getURL();
                } else if (AboutLinkCell.this.pressedLink instanceof URLSpan) {
                    url = ((URLSpan) AboutLinkCell.this.pressedLink).getURL();
                } else {
                    url = AboutLinkCell.this.pressedLink.toString();
                }
                ClickableSpan pressedLinkFinal = AboutLinkCell.this.pressedLink;
                BottomSheet.Builder builder = new BottomSheet.Builder(AboutLinkCell.this.parentFragment.getParentActivity());
                builder.setTitle(url);
                builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new AboutLinkCell$1$$ExternalSyntheticLambda0(this, pressedLinkFinal, url));
                builder.show();
                AboutLinkCell.this.resetPressedLink();
            }
        }

        /* renamed from: lambda$run$0$org-telegram-ui-Cells-AboutLinkCell$1  reason: not valid java name */
        public /* synthetic */ void m1525lambda$run$0$orgtelegramuiCellsAboutLinkCell$1(ClickableSpan pressedLinkFinal, String url, DialogInterface dialog, int which) {
            if (which == 0) {
                AboutLinkCell.this.onLinkClick(pressedLinkFinal);
            } else if (which == 1) {
                AndroidUtilities.addToClipboard(url);
                if (url.startsWith("@")) {
                    BulletinFactory.of(AboutLinkCell.this.parentFragment).createSimpleBulletin(NUM, LocaleController.getString("UsernameCopied", NUM)).show();
                } else if (url.startsWith("#") || url.startsWith("$")) {
                    BulletinFactory.of(AboutLinkCell.this.parentFragment).createSimpleBulletin(NUM, LocaleController.getString("HashtagCopied", NUM)).show();
                } else {
                    BulletinFactory.of(AboutLinkCell.this.parentFragment).createSimpleBulletin(NUM, LocaleController.getString("LinkCopied", NUM)).show();
                }
            }
        }
    };
    private String oldText;
    /* access modifiers changed from: private */
    public BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public ClickableSpan pressedLink;
    private SpannableStringBuilder stringBuilder;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    private LinkPath urlPath = new LinkPath();
    private TextView valueTextView;

    public AboutLinkCell(Context context, BaseFragment fragment) {
        super(context);
        this.parentFragment = fragment;
        TextView textView = new TextView(context);
        this.valueTextView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        int i = 5;
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setImportantForAccessibility(2);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 80, 23.0f, 0.0f, 23.0f, 10.0f));
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void didPressUrl(String url) {
    }

    /* access modifiers changed from: private */
    public void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        AndroidUtilities.cancelRunOnUIThread(this.longPressedRunnable);
        invalidate();
    }

    public void setText(String text, boolean parseLinks) {
        setTextAndValue(text, (String) null, parseLinks);
    }

    public void setTextAndValue(String text, String value, boolean parseLinks) {
        if (!TextUtils.isEmpty(text) && !TextUtils.equals(text, this.oldText)) {
            try {
                this.oldText = AndroidUtilities.getSafeString(text);
            } catch (Throwable th) {
                this.oldText = text;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.oldText);
            this.stringBuilder = spannableStringBuilder;
            MessageObject.addLinks(false, spannableStringBuilder, false, false, !parseLinks);
            Emoji.replaceEmoji(this.stringBuilder, Theme.profile_aboutTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            if (TextUtils.isEmpty(value)) {
                this.valueTextView.setVisibility(8);
            } else {
                this.valueTextView.setText(value);
                this.valueTextView.setVisibility(0);
            }
            requestLayout();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
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
                                AndroidUtilities.runOnUIThread(this.longPressedRunnable, (long) ViewConfiguration.getLongPressTimeout());
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
                            onLinkClick(clickableSpan2);
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

    /* access modifiers changed from: private */
    public void onLinkClick(ClickableSpan pressedLink2) {
        if (pressedLink2 instanceof URLSpanNoUnderline) {
            String url = ((URLSpanNoUnderline) pressedLink2).getURL();
            if (url.startsWith("@") || url.startsWith("#") || url.startsWith("/")) {
                didPressUrl(url);
            }
        } else if (pressedLink2 instanceof URLSpan) {
            String url2 = ((URLSpan) pressedLink2).getURL();
            if (AndroidUtilities.shouldShowUrlInAlert(url2)) {
                AlertsCreator.showOpenUrlAlert(this.parentFragment, url2, true, true);
            } else {
                Browser.openUrl(getContext(), url2);
            }
        } else {
            pressedLink2.onClick(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.stringBuilder != null) {
            int maxWidth = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(46.0f);
            if (Build.VERSION.SDK_INT >= 24) {
                SpannableStringBuilder spannableStringBuilder = this.stringBuilder;
                this.textLayout = StaticLayout.Builder.obtain(spannableStringBuilder, 0, spannableStringBuilder.length(), Theme.profile_aboutTextPaint, maxWidth).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(LocaleController.isRTL ? StaticLayoutEx.ALIGN_RIGHT() : StaticLayoutEx.ALIGN_LEFT()).build();
            } else {
                this.textLayout = new StaticLayout(this.stringBuilder, Theme.profile_aboutTextPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }
        StaticLayout staticLayout = this.textLayout;
        int height = (staticLayout != null ? staticLayout.getHeight() : AndroidUtilities.dp(20.0f)) + AndroidUtilities.dp(16.0f);
        if (this.valueTextView.getVisibility() == 0) {
            height += AndroidUtilities.dp(23.0f);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.save();
        int dp = AndroidUtilities.dp(23.0f);
        this.textX = dp;
        int dp2 = AndroidUtilities.dp(8.0f);
        this.textY = dp2;
        canvas.translate((float) dp, (float) dp2);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
        }
        try {
            StaticLayout staticLayout = this.textLayout;
            if (staticLayout != null) {
                staticLayout.draw(canvas);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        canvas.restore();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            CharSequence text = staticLayout.getText();
            CharSequence valueText = this.valueTextView.getText();
            if (TextUtils.isEmpty(valueText)) {
                info.setText(text);
                return;
            }
            info.setText(valueText + ": " + text);
        }
    }
}
