package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;
import android.view.View.MeasureSpec;
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
        private RadioButton button;
        private boolean isThreeLines;
        private RectF rect = new RectF();
        private TextPaint textPaint;

        public ListView(Context context, boolean z) {
            super(context);
            boolean z2 = true;
            this.textPaint = new TextPaint(1);
            setWillNotDraw(false);
            this.isThreeLines = z;
            this.button = new RadioButton(context);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            this.button = new RadioButton(context, ChatListCell.this) {
                public void invalidate() {
                    super.invalidate();
                    ListView.this.invalidate();
                }
            };
            this.button.setSize(AndroidUtilities.dp(20.0f));
            addView(this.button, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 26.0f, 10.0f, 0.0f));
            RadioButton radioButton = this.button;
            if (!(this.isThreeLines && SharedConfig.useThreeLinesLayout) && (this.isThreeLines || SharedConfig.useThreeLinesLayout)) {
                z2 = false;
            }
            radioButton.setChecked(z2, false);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            int i;
            String str;
            Canvas canvas2 = canvas;
            int color = Theme.getColor("switchTrack");
            int red = Color.red(color);
            int green = Color.green(color);
            color = Color.blue(color);
            this.button.setColor(Theme.getColor("radioBackground"), Theme.getColor("radioBackgroundChecked"));
            this.rect.set((float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(1.0f)), (float) AndroidUtilities.dp(73.0f));
            Theme.chat_instantViewRectPaint.setColor(Color.argb((int) (this.button.getProgress() * 43.0f), red, green, color));
            canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(74.0f));
            Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) ((1.0f - this.button.getProgress()) * 31.0f), red, green, color));
            canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.dialogs_onlineCirclePaint);
            if (this.isThreeLines) {
                i = NUM;
                str = "ChatListExpanded";
            } else {
                i = NUM;
                str = "ChatListDefault";
            }
            String string = LocaleController.getString(str, i);
            int ceil = (int) Math.ceil((double) this.textPaint.measureText(string));
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            int measuredWidth = getMeasuredWidth() - ceil;
            ceil = 2;
            canvas2.drawText(string, (float) (measuredWidth / 2), (float) AndroidUtilities.dp(96.0f), this.textPaint);
            measuredWidth = 0;
            while (measuredWidth < ceil) {
                int dp = AndroidUtilities.dp(measuredWidth == 0 ? 21.0f : 53.0f);
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb(measuredWidth == 0 ? 204 : 90, red, green, color));
                canvas2.drawCircle((float) AndroidUtilities.dp(22.0f), (float) dp, (float) AndroidUtilities.dp(11.0f), Theme.dialogs_onlineCirclePaint);
                int i2 = 0;
                while (true) {
                    if (i2 >= (this.isThreeLines ? 3 : 2)) {
                        break;
                    }
                    Theme.dialogs_onlineCirclePaint.setColor(Color.argb(i2 == 0 ? 204 : 90, red, green, color));
                    float f = 72.0f;
                    float dp2;
                    if (this.isThreeLines) {
                        RectF rectF = this.rect;
                        float dp3 = (float) AndroidUtilities.dp(41.0f);
                        float f2 = (float) (i2 * 7);
                        dp2 = (float) (dp - AndroidUtilities.dp(8.3f - f2));
                        int measuredWidth2 = getMeasuredWidth();
                        if (i2 != 0) {
                            f = 48.0f;
                        }
                        rectF.set(dp3, dp2, (float) (measuredWidth2 - AndroidUtilities.dp(f)), (float) (dp - AndroidUtilities.dp(5.3f - f2)));
                        canvas2.drawRoundRect(this.rect, AndroidUtilities.dpf2(1.5f), AndroidUtilities.dpf2(1.5f), Theme.dialogs_onlineCirclePaint);
                    } else {
                        RectF rectF2 = this.rect;
                        dp2 = (float) AndroidUtilities.dp(41.0f);
                        int i3 = i2 * 10;
                        float dp4 = (float) (dp - AndroidUtilities.dp((float) (7 - i3)));
                        int measuredWidth3 = getMeasuredWidth();
                        if (i2 != 0) {
                            f = 48.0f;
                        }
                        rectF2.set(dp2, dp4, (float) (measuredWidth3 - AndroidUtilities.dp(f)), (float) (dp - AndroidUtilities.dp((float) (3 - i3))));
                        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                    }
                    i2++;
                }
                measuredWidth++;
                ceil = 2;
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void didSelectChatType(boolean z) {
    }

    public ChatListCell(Context context) {
        super(context);
        setOrientation(0);
        setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), 0);
        int i = 0;
        while (i < this.listView.length) {
            boolean z = i == 1;
            this.listView[i] = new ListView(context, z);
            addView(this.listView[i], LayoutHelper.createLinear(-1, -1, 0.5f, i == 1 ? 10 : 0, 0, 0, 0));
            this.listView[i].setOnClickListener(new -$$Lambda$ChatListCell$iynS8JCANgqC1uqLhRG8bhggO74(this, z));
            i++;
        }
    }

    public /* synthetic */ void lambda$new$0$ChatListCell(boolean z, View view) {
        for (int i = 0; i < 2; i++) {
            this.listView[i].button.setChecked(this.listView[i] == view, true);
        }
        didSelectChatType(z);
    }

    public void invalidate() {
        super.invalidate();
        int i = 0;
        while (true) {
            ListView[] listViewArr = this.listView;
            if (i < listViewArr.length) {
                listViewArr[i].invalidate();
                i++;
            } else {
                return;
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(123.0f), NUM));
    }
}
