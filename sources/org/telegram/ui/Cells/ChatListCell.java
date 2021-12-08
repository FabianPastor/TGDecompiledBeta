package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class ChatListCell extends LinearLayout {
    private ListView[] listView = new ListView[2];

    private class ListView extends FrameLayout {
        /* access modifiers changed from: private */
        public RadioButton button;
        private boolean isThreeLines;
        private RectF rect = new RectF();
        private TextPaint textPaint;

        public ListView(Context context, boolean threeLines) {
            super(context);
            String str;
            int i;
            boolean z = true;
            this.textPaint = new TextPaint(1);
            setWillNotDraw(false);
            this.isThreeLines = threeLines;
            if (threeLines) {
                i = NUM;
                str = "ChatListExpanded";
            } else {
                i = NUM;
                str = "ChatListDefault";
            }
            setContentDescription(LocaleController.getString(str, i));
            this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            AnonymousClass1 r2 = new RadioButton(context, ChatListCell.this) {
                public void invalidate() {
                    super.invalidate();
                    ListView.this.invalidate();
                }
            };
            this.button = r2;
            r2.setSize(AndroidUtilities.dp(20.0f));
            addView(this.button, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 26.0f, 10.0f, 0.0f));
            RadioButton radioButton = this.button;
            if ((!this.isThreeLines || !SharedConfig.useThreeLinesLayout) && (this.isThreeLines || SharedConfig.useThreeLinesLayout)) {
                z = false;
            }
            radioButton.setChecked(z, false);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            String str;
            int i;
            Canvas canvas2 = canvas;
            int color = Theme.getColor("switchTrack");
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            this.button.setColor(Theme.getColor("radioBackground"), Theme.getColor("radioBackgroundChecked"));
            this.rect.set((float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(1.0f)), (float) AndroidUtilities.dp(73.0f));
            Theme.chat_instantViewRectPaint.setColor(Color.argb((int) (this.button.getProgress() * 43.0f), r, g, b));
            canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(74.0f));
            Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) ((1.0f - this.button.getProgress()) * 31.0f), r, g, b));
            canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.dialogs_onlineCirclePaint);
            if (this.isThreeLines) {
                i = NUM;
                str = "ChatListExpanded";
            } else {
                i = NUM;
                str = "ChatListDefault";
            }
            String text = LocaleController.getString(str, i);
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas2.drawText(text, (float) ((getMeasuredWidth() - ((int) Math.ceil((double) this.textPaint.measureText(text)))) / 2), (float) AndroidUtilities.dp(96.0f), this.textPaint);
            int a = 0;
            for (int i2 = 2; a < i2; i2 = 2) {
                int cy = AndroidUtilities.dp(a == 0 ? 21.0f : 53.0f);
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb(a == 0 ? 204 : 90, r, g, b));
                canvas2.drawCircle((float) AndroidUtilities.dp(22.0f), (float) cy, (float) AndroidUtilities.dp(11.0f), Theme.dialogs_onlineCirclePaint);
                int i3 = 0;
                while (true) {
                    if (i3 >= (this.isThreeLines ? 3 : 2)) {
                        break;
                    }
                    Theme.dialogs_onlineCirclePaint.setColor(Color.argb(i3 == 0 ? 204 : 90, r, g, b));
                    float f = 72.0f;
                    if (this.isThreeLines) {
                        RectF rectF = this.rect;
                        float dp = (float) AndroidUtilities.dp(41.0f);
                        float dp2 = (float) (cy - AndroidUtilities.dp(8.3f - ((float) (i3 * 7))));
                        int measuredWidth = getMeasuredWidth();
                        if (i3 != 0) {
                            f = 48.0f;
                        }
                        rectF.set(dp, dp2, (float) (measuredWidth - AndroidUtilities.dp(f)), (float) (cy - AndroidUtilities.dp(5.3f - ((float) (i3 * 7)))));
                        canvas2.drawRoundRect(this.rect, AndroidUtilities.dpf2(1.5f), AndroidUtilities.dpf2(1.5f), Theme.dialogs_onlineCirclePaint);
                    } else {
                        RectF rectF2 = this.rect;
                        float dp3 = (float) AndroidUtilities.dp(41.0f);
                        float dp4 = (float) (cy - AndroidUtilities.dp((float) (7 - (i3 * 10))));
                        int measuredWidth2 = getMeasuredWidth();
                        if (i3 != 0) {
                            f = 48.0f;
                        }
                        rectF2.set(dp3, dp4, (float) (measuredWidth2 - AndroidUtilities.dp(f)), (float) (cy - AndroidUtilities.dp((float) (3 - (i3 * 10)))));
                        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                    }
                    i3++;
                }
                a++;
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName(RadioButton.class.getName());
            info.setChecked(this.button.isChecked());
            info.setCheckable(true);
        }
    }

    public ChatListCell(Context context) {
        super(context);
        setOrientation(0);
        setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), 0);
        int a = 0;
        while (true) {
            ListView[] listViewArr = this.listView;
            if (a < listViewArr.length) {
                boolean isThreeLines = a == 1;
                listViewArr[a] = new ListView(context, isThreeLines);
                addView(this.listView[a], LayoutHelper.createLinear(-1, -1, 0.5f, a == 1 ? 10 : 0, 0, 0, 0));
                this.listView[a].setOnClickListener(new ChatListCell$$ExternalSyntheticLambda0(this, isThreeLines));
                a++;
            } else {
                return;
            }
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-ChatListCell  reason: not valid java name */
    public /* synthetic */ void m1527lambda$new$0$orgtelegramuiCellsChatListCell(boolean isThreeLines, View v) {
        for (int b = 0; b < 2; b++) {
            this.listView[b].button.setChecked(this.listView[b] == v, true);
        }
        didSelectChatType(isThreeLines);
    }

    /* access modifiers changed from: protected */
    public void didSelectChatType(boolean threeLines) {
    }

    public void invalidate() {
        super.invalidate();
        int a = 0;
        while (true) {
            ListView[] listViewArr = this.listView;
            if (a < listViewArr.length) {
                listViewArr[a].invalidate();
                a++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(123.0f), NUM));
    }
}
