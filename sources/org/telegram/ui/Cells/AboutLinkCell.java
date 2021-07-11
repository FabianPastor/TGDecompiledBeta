package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
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
import org.telegram.ui.Cells.AboutLinkCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class AboutLinkCell extends FrameLayout {
    Runnable longPressedRunnable = new Runnable() {
        public void run() {
            String str;
            if (AboutLinkCell.this.pressedLink != null) {
                if (AboutLinkCell.this.pressedLink instanceof URLSpanNoUnderline) {
                    str = ((URLSpanNoUnderline) AboutLinkCell.this.pressedLink).getURL();
                } else if (AboutLinkCell.this.pressedLink instanceof URLSpan) {
                    str = ((URLSpan) AboutLinkCell.this.pressedLink).getURL();
                } else {
                    str = AboutLinkCell.this.pressedLink.toString();
                }
                ClickableSpan access$000 = AboutLinkCell.this.pressedLink;
                BottomSheet.Builder builder = new BottomSheet.Builder(AboutLinkCell.this.parentFragment.getParentActivity());
                builder.setTitle(str);
                builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new DialogInterface.OnClickListener(access$000, str) {
                    public final /* synthetic */ ClickableSpan f$1;
                    public final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AboutLinkCell.AnonymousClass1.this.lambda$run$0$AboutLinkCell$1(this.f$1, this.f$2, dialogInterface, i);
                    }
                });
                builder.show();
                AboutLinkCell.this.resetPressedLink();
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$run$0 */
        public /* synthetic */ void lambda$run$0$AboutLinkCell$1(ClickableSpan clickableSpan, String str, DialogInterface dialogInterface, int i) {
            if (i == 0) {
                AboutLinkCell.this.onLinkClick(clickableSpan);
            } else if (i == 1) {
                AndroidUtilities.addToClipboard(str);
                if (str.startsWith("@")) {
                    BulletinFactory.of(AboutLinkCell.this.parentFragment).createSimpleBulletin(NUM, LocaleController.getString("UsernameCopied", NUM)).show();
                } else if (str.startsWith("#") || str.startsWith("$")) {
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

    /* access modifiers changed from: protected */
    public void didPressUrl(String str) {
    }

    public AboutLinkCell(Context context, BaseFragment baseFragment) {
        super(context);
        this.parentFragment = baseFragment;
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

    /* access modifiers changed from: private */
    public void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        AndroidUtilities.cancelRunOnUIThread(this.longPressedRunnable);
        invalidate();
    }

    public void setText(String str, boolean z) {
        setTextAndValue(str, (String) null, z);
    }

    public void setTextAndValue(String str, String str2, boolean z) {
        if (!TextUtils.isEmpty(str) && !TextUtils.equals(str, this.oldText)) {
            this.oldText = str;
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.oldText);
            this.stringBuilder = spannableStringBuilder;
            MessageObject.addLinks(false, spannableStringBuilder, false, false, !z);
            Emoji.replaceEmoji(this.stringBuilder, Theme.profile_aboutTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            if (TextUtils.isEmpty(str2)) {
                this.valueTextView.setVisibility(8);
            } else {
                this.valueTextView.setText(str2);
                this.valueTextView.setVisibility(0);
            }
            requestLayout();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:48:0x00d2 A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:49:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            float r0 = r8.getX()
            float r1 = r8.getY()
            android.text.StaticLayout r2 = r7.textLayout
            r3 = 0
            r4 = 1
            if (r2 == 0) goto L_0x00c9
            int r2 = r8.getAction()
            if (r2 == 0) goto L_0x002b
            android.text.style.ClickableSpan r2 = r7.pressedLink
            if (r2 == 0) goto L_0x001f
            int r2 = r8.getAction()
            if (r2 != r4) goto L_0x001f
            goto L_0x002b
        L_0x001f:
            int r0 = r8.getAction()
            r1 = 3
            if (r0 != r1) goto L_0x00c9
            r7.resetPressedLink()
            goto L_0x00c9
        L_0x002b:
            int r2 = r8.getAction()
            if (r2 != 0) goto L_0x00b8
            r7.resetPressedLink()
            int r2 = r7.textX     // Catch:{ Exception -> 0x00ae }
            float r2 = (float) r2     // Catch:{ Exception -> 0x00ae }
            float r0 = r0 - r2
            int r0 = (int) r0     // Catch:{ Exception -> 0x00ae }
            int r2 = r7.textY     // Catch:{ Exception -> 0x00ae }
            float r2 = (float) r2     // Catch:{ Exception -> 0x00ae }
            float r1 = r1 - r2
            int r1 = (int) r1     // Catch:{ Exception -> 0x00ae }
            android.text.StaticLayout r2 = r7.textLayout     // Catch:{ Exception -> 0x00ae }
            int r1 = r2.getLineForVertical(r1)     // Catch:{ Exception -> 0x00ae }
            android.text.StaticLayout r2 = r7.textLayout     // Catch:{ Exception -> 0x00ae }
            float r0 = (float) r0     // Catch:{ Exception -> 0x00ae }
            int r2 = r2.getOffsetForHorizontal(r1, r0)     // Catch:{ Exception -> 0x00ae }
            android.text.StaticLayout r5 = r7.textLayout     // Catch:{ Exception -> 0x00ae }
            float r5 = r5.getLineLeft(r1)     // Catch:{ Exception -> 0x00ae }
            int r6 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r6 > 0) goto L_0x00aa
            android.text.StaticLayout r6 = r7.textLayout     // Catch:{ Exception -> 0x00ae }
            float r1 = r6.getLineWidth(r1)     // Catch:{ Exception -> 0x00ae }
            float r5 = r5 + r1
            int r0 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x00aa
            android.text.StaticLayout r0 = r7.textLayout     // Catch:{ Exception -> 0x00ae }
            java.lang.CharSequence r0 = r0.getText()     // Catch:{ Exception -> 0x00ae }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x00ae }
            java.lang.Class<android.text.style.ClickableSpan> r1 = android.text.style.ClickableSpan.class
            java.lang.Object[] r1 = r0.getSpans(r2, r2, r1)     // Catch:{ Exception -> 0x00ae }
            android.text.style.ClickableSpan[] r1 = (android.text.style.ClickableSpan[]) r1     // Catch:{ Exception -> 0x00ae }
            int r2 = r1.length     // Catch:{ Exception -> 0x00ae }
            if (r2 == 0) goto L_0x00a6
            r7.resetPressedLink()     // Catch:{ Exception -> 0x00ae }
            r1 = r1[r3]     // Catch:{ Exception -> 0x00ae }
            r7.pressedLink = r1     // Catch:{ Exception -> 0x00ae }
            int r1 = r0.getSpanStart(r1)     // Catch:{ Exception -> 0x0094 }
            org.telegram.ui.Components.LinkPath r2 = r7.urlPath     // Catch:{ Exception -> 0x0094 }
            android.text.StaticLayout r5 = r7.textLayout     // Catch:{ Exception -> 0x0094 }
            r6 = 0
            r2.setCurrentLayout(r5, r1, r6)     // Catch:{ Exception -> 0x0094 }
            android.text.StaticLayout r2 = r7.textLayout     // Catch:{ Exception -> 0x0094 }
            android.text.style.ClickableSpan r5 = r7.pressedLink     // Catch:{ Exception -> 0x0094 }
            int r0 = r0.getSpanEnd(r5)     // Catch:{ Exception -> 0x0094 }
            org.telegram.ui.Components.LinkPath r5 = r7.urlPath     // Catch:{ Exception -> 0x0094 }
            r2.getSelectionPath(r1, r0, r5)     // Catch:{ Exception -> 0x0094 }
            goto L_0x0098
        L_0x0094:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x00a3 }
        L_0x0098:
            java.lang.Runnable r0 = r7.longPressedRunnable     // Catch:{ Exception -> 0x00a3 }
            int r1 = android.view.ViewConfiguration.getLongPressTimeout()     // Catch:{ Exception -> 0x00a3 }
            long r1 = (long) r1     // Catch:{ Exception -> 0x00a3 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)     // Catch:{ Exception -> 0x00a3 }
            goto L_0x00c7
        L_0x00a3:
            r0 = move-exception
            r1 = 1
            goto L_0x00b0
        L_0x00a6:
            r7.resetPressedLink()     // Catch:{ Exception -> 0x00ae }
            goto L_0x00c9
        L_0x00aa:
            r7.resetPressedLink()     // Catch:{ Exception -> 0x00ae }
            goto L_0x00c9
        L_0x00ae:
            r0 = move-exception
            r1 = 0
        L_0x00b0:
            r7.resetPressedLink()
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = r1
            goto L_0x00ca
        L_0x00b8:
            android.text.style.ClickableSpan r0 = r7.pressedLink
            if (r0 == 0) goto L_0x00c9
            r7.onLinkClick(r0)     // Catch:{ Exception -> 0x00c0 }
            goto L_0x00c4
        L_0x00c0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00c4:
            r7.resetPressedLink()
        L_0x00c7:
            r0 = 1
            goto L_0x00ca
        L_0x00c9:
            r0 = 0
        L_0x00ca:
            if (r0 != 0) goto L_0x00d2
            boolean r8 = super.onTouchEvent(r8)
            if (r8 == 0) goto L_0x00d3
        L_0x00d2:
            r3 = 1
        L_0x00d3:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.AboutLinkCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: private */
    public void onLinkClick(ClickableSpan clickableSpan) {
        if (clickableSpan instanceof URLSpanNoUnderline) {
            String url = ((URLSpanNoUnderline) clickableSpan).getURL();
            if (url.startsWith("@") || url.startsWith("#") || url.startsWith("/")) {
                didPressUrl(url);
            }
        } else if (clickableSpan instanceof URLSpan) {
            String url2 = ((URLSpan) clickableSpan).getURL();
            if (AndroidUtilities.shouldShowUrlInAlert(url2)) {
                AlertsCreator.showOpenUrlAlert(this.parentFragment, url2, true, true);
            } else {
                Browser.openUrl(getContext(), url2);
            }
        } else {
            clickableSpan.onClick(this);
        }
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"DrawAllocation"})
    public void onMeasure(int i, int i2) {
        if (this.stringBuilder != null) {
            int size = View.MeasureSpec.getSize(i) - AndroidUtilities.dp(46.0f);
            if (Build.VERSION.SDK_INT >= 24) {
                SpannableStringBuilder spannableStringBuilder = this.stringBuilder;
                this.textLayout = StaticLayout.Builder.obtain(spannableStringBuilder, 0, spannableStringBuilder.length(), Theme.profile_aboutTextPaint, size).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(LocaleController.isRTL ? StaticLayoutEx.ALIGN_RIGHT() : StaticLayoutEx.ALIGN_LEFT()).build();
            } else {
                this.textLayout = new StaticLayout(this.stringBuilder, Theme.profile_aboutTextPaint, size, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
        }
        StaticLayout staticLayout = this.textLayout;
        int height = (staticLayout != null ? staticLayout.getHeight() : AndroidUtilities.dp(20.0f)) + AndroidUtilities.dp(16.0f);
        if (this.valueTextView.getVisibility() == 0) {
            height += AndroidUtilities.dp(23.0f);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
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

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            CharSequence text = staticLayout.getText();
            CharSequence text2 = this.valueTextView.getText();
            if (TextUtils.isEmpty(text2)) {
                accessibilityNodeInfo.setText(text);
                return;
            }
            accessibilityNodeInfo.setText(text2 + ": " + text);
        }
    }
}
