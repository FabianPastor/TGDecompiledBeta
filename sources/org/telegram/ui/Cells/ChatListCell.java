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

    /* access modifiers changed from: protected */
    public void didSelectChatType(boolean z) {
    }

    private class ListView extends FrameLayout {
        /* access modifiers changed from: private */
        public RadioButton button;
        private boolean isThreeLines;
        private RectF rect = new RectF();
        private TextPaint textPaint;

        public ListView(Context context, boolean z) {
            super(context);
            int i;
            String str;
            boolean z2 = true;
            this.textPaint = new TextPaint(1);
            setWillNotDraw(false);
            this.isThreeLines = z;
            if (z) {
                i = NUM;
                str = "ChatListExpanded";
            } else {
                i = NUM;
                str = "ChatListDefault";
            }
            setContentDescription(LocaleController.getString(str, i));
            this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            AnonymousClass1 r12 = new RadioButton(context, ChatListCell.this) {
                public void invalidate() {
                    super.invalidate();
                    ListView.this.invalidate();
                }
            };
            this.button = r12;
            r12.setSize(AndroidUtilities.dp(20.0f));
            addView(this.button, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 26.0f, 10.0f, 0.0f));
            RadioButton radioButton = this.button;
            boolean z3 = this.isThreeLines;
            if ((!z3 || !SharedConfig.useThreeLinesLayout) && (z3 || SharedConfig.useThreeLinesLayout)) {
                z2 = false;
            }
            radioButton.setChecked(z2, false);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            String str;
            int i;
            Canvas canvas2 = canvas;
            int color = Theme.getColor("switchTrack");
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            this.button.setColor(Theme.getColor("radioBackground"), Theme.getColor("radioBackgroundChecked"));
            this.rect.set((float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), (float) (getMeasuredWidth() - AndroidUtilities.dp(1.0f)), (float) AndroidUtilities.dp(73.0f));
            Theme.chat_instantViewRectPaint.setColor(Color.argb((int) (this.button.getProgress() * 43.0f), red, green, blue));
            canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(74.0f));
            Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) ((1.0f - this.button.getProgress()) * 31.0f), red, green, blue));
            canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.dialogs_onlineCirclePaint);
            if (this.isThreeLines) {
                i = NUM;
                str = "ChatListExpanded";
            } else {
                i = NUM;
                str = "ChatListDefault";
            }
            String string = LocaleController.getString(str, i);
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas2.drawText(string, (float) ((getMeasuredWidth() - ((int) Math.ceil((double) this.textPaint.measureText(string)))) / 2), (float) AndroidUtilities.dp(96.0f), this.textPaint);
            int i2 = 0;
            for (int i3 = 2; i2 < i3; i3 = 2) {
                int dp = AndroidUtilities.dp(i2 == 0 ? 21.0f : 53.0f);
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb(i2 == 0 ? 204 : 90, red, green, blue));
                canvas2.drawCircle((float) AndroidUtilities.dp(22.0f), (float) dp, (float) AndroidUtilities.dp(11.0f), Theme.dialogs_onlineCirclePaint);
                int i4 = 0;
                while (true) {
                    if (i4 >= (this.isThreeLines ? 3 : 2)) {
                        break;
                    }
                    Theme.dialogs_onlineCirclePaint.setColor(Color.argb(i4 == 0 ? 204 : 90, red, green, blue));
                    float f = 72.0f;
                    if (this.isThreeLines) {
                        RectF rectF = this.rect;
                        float dp2 = (float) AndroidUtilities.dp(41.0f);
                        float f2 = (float) (i4 * 7);
                        float dp3 = (float) (dp - AndroidUtilities.dp(8.3f - f2));
                        int measuredWidth = getMeasuredWidth();
                        if (i4 != 0) {
                            f = 48.0f;
                        }
                        rectF.set(dp2, dp3, (float) (measuredWidth - AndroidUtilities.dp(f)), (float) (dp - AndroidUtilities.dp(5.3f - f2)));
                        canvas2.drawRoundRect(this.rect, AndroidUtilities.dpf2(1.5f), AndroidUtilities.dpf2(1.5f), Theme.dialogs_onlineCirclePaint);
                    } else {
                        RectF rectF2 = this.rect;
                        float dp4 = (float) AndroidUtilities.dp(41.0f);
                        int i5 = i4 * 10;
                        float dp5 = (float) (dp - AndroidUtilities.dp((float) (7 - i5)));
                        int measuredWidth2 = getMeasuredWidth();
                        if (i4 != 0) {
                            f = 48.0f;
                        }
                        rectF2.set(dp4, dp5, (float) (measuredWidth2 - AndroidUtilities.dp(f)), (float) (dp - AndroidUtilities.dp((float) (3 - i5))));
                        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                    }
                    i4++;
                }
                i2++;
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName(RadioButton.class.getName());
            accessibilityNodeInfo.setChecked(this.button.isChecked());
            accessibilityNodeInfo.setCheckable(true);
        }
    }

    public ChatListCell(Context context) {
        super(context);
        setOrientation(0);
        setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), 0);
        int i = 0;
        while (true) {
            ListView[] listViewArr = this.listView;
            if (i < listViewArr.length) {
                boolean z = i == 1;
                listViewArr[i] = new ListView(context, z);
                addView(this.listView[i], LayoutHelper.createLinear(-1, -1, 0.5f, i == 1 ? 10 : 0, 0, 0, 0));
                this.listView[i].setOnClickListener(new View.OnClickListener(z) {
                    public final /* synthetic */ boolean f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        ChatListCell.this.lambda$new$0$ChatListCell(this.f$1, view);
                    }
                });
                i++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(123.0f), NUM));
    }
}
