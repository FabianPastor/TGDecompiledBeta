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
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;
/* loaded from: classes3.dex */
public class ChatListCell extends LinearLayout {
    private ListView[] listView;

    protected void didSelectChatType(boolean z) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ListView extends FrameLayout {
        private RadioButton button;
        private boolean isThreeLines;
        private RectF rect;
        private TextPaint textPaint;

        public ListView(ChatListCell chatListCell, Context context, boolean z) {
            super(context);
            int i;
            String str;
            this.rect = new RectF();
            boolean z2 = true;
            this.textPaint = new TextPaint(1);
            setWillNotDraw(false);
            this.isThreeLines = z;
            if (z) {
                i = R.string.ChatListExpanded;
                str = "ChatListExpanded";
            } else {
                i = R.string.ChatListDefault;
                str = "ChatListDefault";
            }
            setContentDescription(LocaleController.getString(str, i));
            this.textPaint.setTextSize(AndroidUtilities.dp(13.0f));
            RadioButton radioButton = new RadioButton(context, chatListCell) { // from class: org.telegram.ui.Cells.ChatListCell.ListView.1
                @Override // android.view.View
                public void invalidate() {
                    super.invalidate();
                    ListView.this.invalidate();
                }
            };
            this.button = radioButton;
            radioButton.setSize(AndroidUtilities.dp(20.0f));
            addView(this.button, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 26.0f, 10.0f, 0.0f));
            RadioButton radioButton2 = this.button;
            boolean z3 = this.isThreeLines;
            if ((!z3 || !SharedConfig.useThreeLinesLayout) && (z3 || SharedConfig.useThreeLinesLayout)) {
                z2 = false;
            }
            radioButton2.setChecked(z2, false);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int i;
            String str;
            float f;
            int color = Theme.getColor("switchTrack");
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            this.button.setColor(Theme.getColor("radioBackground"), Theme.getColor("radioBackgroundChecked"));
            this.rect.set(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), getMeasuredWidth() - AndroidUtilities.dp(1.0f), AndroidUtilities.dp(73.0f));
            Theme.chat_instantViewRectPaint.setColor(Color.argb((int) (this.button.getProgress() * 43.0f), red, green, blue));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
            this.rect.set(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(74.0f));
            Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) ((1.0f - this.button.getProgress()) * 31.0f), red, green, blue));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.dialogs_onlineCirclePaint);
            if (this.isThreeLines) {
                i = R.string.ChatListExpanded;
                str = "ChatListExpanded";
            } else {
                i = R.string.ChatListDefault;
                str = "ChatListDefault";
            }
            String string = LocaleController.getString(str, i);
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.drawText(string, (getMeasuredWidth() - ((int) Math.ceil(this.textPaint.measureText(string)))) / 2, AndroidUtilities.dp(96.0f), this.textPaint);
            int i2 = 0;
            for (int i3 = 2; i2 < i3; i3 = 2) {
                int dp = AndroidUtilities.dp(i2 == 0 ? 21.0f : 53.0f);
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb(i2 == 0 ? 204 : 90, red, green, blue));
                canvas.drawCircle(AndroidUtilities.dp(22.0f), dp, AndroidUtilities.dp(11.0f), Theme.dialogs_onlineCirclePaint);
                int i4 = 0;
                while (true) {
                    if (i4 < (this.isThreeLines ? 3 : 2)) {
                        Theme.dialogs_onlineCirclePaint.setColor(Color.argb(i4 == 0 ? 204 : 90, red, green, blue));
                        float f2 = 72.0f;
                        if (this.isThreeLines) {
                            RectF rectF = this.rect;
                            float dp2 = AndroidUtilities.dp(41.0f);
                            float dp3 = dp - AndroidUtilities.dp(8.3f - (i4 * 7));
                            int measuredWidth = getMeasuredWidth();
                            if (i4 != 0) {
                                f2 = 48.0f;
                            }
                            rectF.set(dp2, dp3, measuredWidth - AndroidUtilities.dp(f2), dp - AndroidUtilities.dp(5.3f - f));
                            canvas.drawRoundRect(this.rect, AndroidUtilities.dpf2(1.5f), AndroidUtilities.dpf2(1.5f), Theme.dialogs_onlineCirclePaint);
                        } else {
                            RectF rectF2 = this.rect;
                            float dp4 = AndroidUtilities.dp(41.0f);
                            int i5 = i4 * 10;
                            float dp5 = dp - AndroidUtilities.dp(7 - i5);
                            int measuredWidth2 = getMeasuredWidth();
                            if (i4 != 0) {
                                f2 = 48.0f;
                            }
                            rectF2.set(dp4, dp5, measuredWidth2 - AndroidUtilities.dp(f2), dp - AndroidUtilities.dp(3 - i5));
                            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                        }
                        i4++;
                    }
                }
                i2++;
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            int i;
            String str;
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName(RadioButton.class.getName());
            accessibilityNodeInfo.setChecked(this.button.isChecked());
            accessibilityNodeInfo.setCheckable(true);
            if (this.isThreeLines) {
                i = R.string.ChatListExpanded;
                str = "ChatListExpanded";
            } else {
                i = R.string.ChatListDefault;
                str = "ChatListDefault";
            }
            accessibilityNodeInfo.setContentDescription(LocaleController.getString(str, i));
        }
    }

    public ChatListCell(Context context) {
        super(context);
        this.listView = new ListView[2];
        setOrientation(0);
        setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), 0);
        int i = 0;
        while (true) {
            ListView[] listViewArr = this.listView;
            if (i < listViewArr.length) {
                final boolean z = i == 1;
                listViewArr[i] = new ListView(this, context, z);
                addView(this.listView[i], LayoutHelper.createLinear(-1, -1, 0.5f, i == 1 ? 10 : 0, 0, 0, 0));
                this.listView[i].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.ChatListCell$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ChatListCell.this.lambda$new$0(z, view);
                    }
                });
                i++;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(boolean z, View view) {
        for (int i = 0; i < 2; i++) {
            this.listView[i].button.setChecked(this.listView[i] == view, true);
        }
        didSelectChatType(z);
    }

    @Override // android.view.View
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

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(123.0f), NUM));
    }
}
