package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$TL_botCommand;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public class BotCommandsMenuView extends View {
    final MenuDrawable backDrawable;
    Drawable backgroundDrawable;
    float expandProgress;
    boolean expanded;
    boolean isOpened;
    int lastSize;
    StaticLayout menuText;
    final Paint paint = new Paint(1);
    final RectF rectTmp = new RectF();
    final TextPaint textPaint;

    /* access modifiers changed from: protected */
    public void onTranslationChanged(float f) {
    }

    public BotCommandsMenuView(Context context) {
        super(context);
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        AnonymousClass1 r1 = new MenuDrawable() {
            public void invalidateSelf() {
                super.invalidateSelf();
                BotCommandsMenuView.this.invalidate();
            }
        };
        this.backDrawable = r1;
        updateColors();
        r1.setMiniIcon(true);
        r1.setRotateToBack(false);
        r1.setRotation(0.0f, false);
        r1.setCallback(this);
        textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r1.setRoundCap();
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(16.0f), 0, Theme.getColor("windowBackgroundWhite"));
        this.backgroundDrawable = createSimpleSelectorRoundRectDrawable;
        createSimpleSelectorRoundRectDrawable.setCallback(this);
    }

    private void updateColors() {
        this.paint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
        int color = Theme.getColor("windowBackgroundWhite");
        this.backDrawable.setBackColor(color);
        this.backDrawable.setIconColor(color);
        this.textPaint.setColor(color);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = (View.MeasureSpec.getSize(i) + View.MeasureSpec.getSize(i2)) << 16;
        if (this.lastSize != size || this.menuText == null) {
            this.backDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.textPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            this.lastSize = size;
            String string = LocaleController.getString("BotsMenuTitle", NUM);
            int measureText = (int) this.textPaint.measureText(string);
            this.menuText = StaticLayoutEx.createStaticLayout(string, this.textPaint, measureText, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, measureText, 1);
        }
        onTranslationChanged(((float) (this.menuText.getWidth() + AndroidUtilities.dp(4.0f))) * this.expandProgress);
        int dp = AndroidUtilities.dp(40.0f);
        if (this.expanded) {
            dp += this.menuText.getWidth() + AndroidUtilities.dp(4.0f);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(dp, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00be  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00e4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchDraw(android.graphics.Canvas r10) {
        /*
            r9 = this;
            android.text.StaticLayout r0 = r9.menuText
            if (r0 == 0) goto L_0x00f5
            r0 = 0
            boolean r1 = r9.expanded
            r2 = 1
            r3 = 1037726734(0x3dda740e, float:0.10666667)
            r4 = 0
            if (r1 == 0) goto L_0x0025
            float r5 = r9.expandProgress
            r6 = 1065353216(0x3var_, float:1.0)
            int r7 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r7 == 0) goto L_0x0025
            float r5 = r5 + r3
            r9.expandProgress = r5
            int r0 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x0020
            r9.expandProgress = r6
            goto L_0x0023
        L_0x0020:
            r9.invalidate()
        L_0x0023:
            r0 = 1
            goto L_0x003b
        L_0x0025:
            if (r1 != 0) goto L_0x003b
            float r1 = r9.expandProgress
            int r5 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r5 == 0) goto L_0x003b
            float r1 = r1 - r3
            r9.expandProgress = r1
            int r0 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x0037
            r9.expandProgress = r4
            goto L_0x0023
        L_0x0037:
            r9.invalidate()
            goto L_0x0023
        L_0x003b:
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            float r2 = r9.expandProgress
            float r1 = r1.getInterpolation(r2)
            if (r0 == 0) goto L_0x0053
            int r2 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0053
            android.text.TextPaint r2 = r9.textPaint
            r3 = 1132396544(0x437var_, float:255.0)
            float r3 = r3 * r1
            int r3 = (int) r3
            r2.setAlpha(r3)
        L_0x0053:
            android.graphics.RectF r2 = r9.rectTmp
            r3 = 1109393408(0x42200000, float:40.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            android.text.StaticLayout r5 = r9.menuText
            int r5 = r5.getWidth()
            r6 = 1082130432(0x40800000, float:4.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r7
            float r5 = (float) r5
            float r5 = r5 * r1
            float r3 = r3 + r5
            int r5 = r9.getMeasuredHeight()
            float r5 = (float) r5
            r2.set(r4, r4, r3, r5)
            android.graphics.RectF r2 = r9.rectTmp
            r3 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r5 = (float) r5
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            android.graphics.Paint r7 = r9.paint
            r10.drawRoundRect(r2, r5, r3, r7)
            android.graphics.drawable.Drawable r2 = r9.backgroundDrawable
            android.graphics.RectF r3 = r9.rectTmp
            float r5 = r3.left
            int r5 = (int) r5
            float r7 = r3.top
            int r7 = (int) r7
            float r8 = r3.right
            int r8 = (int) r8
            float r3 = r3.bottom
            int r3 = (int) r3
            r2.setBounds(r5, r7, r8, r3)
            android.graphics.drawable.Drawable r2 = r9.backgroundDrawable
            r2.draw(r10)
            r10.save()
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r3 = (float) r3
            r10.translate(r2, r3)
            org.telegram.ui.ActionBar.MenuDrawable r2 = r9.backDrawable
            r2.draw(r10)
            r10.restore()
            int r2 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x00e2
            r10.save()
            r2 = 1107820544(0x42080000, float:34.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            int r3 = r9.getMeasuredHeight()
            android.text.StaticLayout r4 = r9.menuText
            int r4 = r4.getHeight()
            int r3 = r3 - r4
            float r3 = (float) r3
            r4 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 / r4
            r10.translate(r2, r3)
            android.text.StaticLayout r2 = r9.menuText
            r2.draw(r10)
            r10.restore()
        L_0x00e2:
            if (r0 == 0) goto L_0x00f5
            android.text.StaticLayout r0 = r9.menuText
            int r0 = r0.getWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 + r2
            float r0 = (float) r0
            float r0 = r0 * r1
            r9.onTranslationChanged(r0)
        L_0x00f5:
            super.dispatchDraw(r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotCommandsMenuView.dispatchDraw(android.graphics.Canvas):void");
    }

    public void setExpanded(boolean z, boolean z2) {
        if (this.expanded != z) {
            this.expanded = z;
            if (!z2) {
                this.expandProgress = z ? 1.0f : 0.0f;
            }
            requestLayout();
            invalidate();
        }
    }

    public boolean isOpened() {
        return this.isOpened;
    }

    public static class BotCommandsAdapter extends RecyclerListView.SelectionAdapter {
        ArrayList<String> newResult = new ArrayList<>();
        ArrayList<String> newResultHelp = new ArrayList<>();

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            BotCommandView botCommandView = new BotCommandView(viewGroup.getContext());
            botCommandView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(botCommandView);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            BotCommandView botCommandView = (BotCommandView) viewHolder.itemView;
            botCommandView.command.setText(this.newResult.get(i));
            botCommandView.description.setText(this.newResultHelp.get(i));
            botCommandView.commandStr = this.newResult.get(i);
        }

        public int getItemCount() {
            return this.newResult.size();
        }

        public void setBotInfo(LongSparseArray<TLRPC$BotInfo> longSparseArray) {
            this.newResult.clear();
            this.newResultHelp.clear();
            for (int i = 0; i < longSparseArray.size(); i++) {
                TLRPC$BotInfo valueAt = longSparseArray.valueAt(i);
                for (int i2 = 0; i2 < valueAt.commands.size(); i2++) {
                    TLRPC$TL_botCommand tLRPC$TL_botCommand = valueAt.commands.get(i2);
                    if (!(tLRPC$TL_botCommand == null || tLRPC$TL_botCommand.command == null)) {
                        ArrayList<String> arrayList = this.newResult;
                        arrayList.add("/" + tLRPC$TL_botCommand.command);
                        String str = tLRPC$TL_botCommand.description;
                        if (str == null || str.length() <= 1) {
                            this.newResultHelp.add(tLRPC$TL_botCommand.description);
                        } else {
                            ArrayList<String> arrayList2 = this.newResultHelp;
                            arrayList2.add(tLRPC$TL_botCommand.description.substring(0, 1).toUpperCase() + tLRPC$TL_botCommand.description.substring(1).toLowerCase());
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setOpened(boolean z) {
        if (this.isOpened != z) {
            this.isOpened = z;
        }
        this.backDrawable.setRotation(z ? 1.0f : 0.0f, true);
    }

    public static class BotCommandView extends LinearLayout {
        TextView command;
        String commandStr;
        TextView description;

        public BotCommandView(Context context) {
            super(context);
            setOrientation(0);
            setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            TextView textView = new TextView(context);
            this.description = textView;
            textView.setTextSize(1, 16.0f);
            this.description.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.description.setTag("windowBackgroundWhiteBlackText");
            this.description.setLines(1);
            this.description.setEllipsize(TextUtils.TruncateAt.END);
            addView(this.description, LayoutHelper.createLinear(-1, -2, 1.0f, 16, 0, 0, AndroidUtilities.dp(8.0f), 0));
            TextView textView2 = new TextView(context);
            this.command = textView2;
            textView2.setTextSize(1, 14.0f);
            this.command.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            this.command.setTag("windowBackgroundWhiteGrayText");
            addView(this.command, LayoutHelper.createLinear(-2, -2, 0.0f, 16));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36.0f), NUM));
        }

        public String getCommand() {
            return this.commandStr;
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || this.backgroundDrawable == drawable;
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        this.backgroundDrawable.setState(getDrawableState());
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.backgroundDrawable.jumpToCurrentState();
    }
}
