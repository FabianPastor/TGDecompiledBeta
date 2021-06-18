package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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
        if (this.lastSize != size) {
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
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00db  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchDraw(android.graphics.Canvas r10) {
        /*
            r9 = this;
            boolean r0 = r9.expanded
            r1 = 1
            r2 = 1037726734(0x3dda740e, float:0.10666667)
            r3 = 0
            if (r0 == 0) goto L_0x001f
            float r4 = r9.expandProgress
            r5 = 1065353216(0x3var_, float:1.0)
            int r6 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r6 == 0) goto L_0x001f
            float r4 = r4 + r2
            r9.expandProgress = r4
            int r0 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x001b
            r9.expandProgress = r5
            goto L_0x0036
        L_0x001b:
            r9.invalidate()
            goto L_0x0036
        L_0x001f:
            if (r0 != 0) goto L_0x0035
            float r0 = r9.expandProgress
            int r4 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x0035
            float r0 = r0 - r2
            r9.expandProgress = r0
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 >= 0) goto L_0x0031
            r9.expandProgress = r3
            goto L_0x0036
        L_0x0031:
            r9.invalidate()
            goto L_0x0036
        L_0x0035:
            r1 = 0
        L_0x0036:
            org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            float r2 = r9.expandProgress
            float r0 = r0.getInterpolation(r2)
            if (r1 == 0) goto L_0x004e
            int r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x004e
            android.text.TextPaint r2 = r9.textPaint
            r4 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r0
            int r4 = (int) r4
            r2.setAlpha(r4)
        L_0x004e:
            android.graphics.RectF r2 = org.telegram.messenger.AndroidUtilities.rectTmp
            r4 = 1109393408(0x42200000, float:40.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            android.text.StaticLayout r5 = r9.menuText
            int r5 = r5.getWidth()
            r6 = 1082130432(0x40800000, float:4.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r7
            float r5 = (float) r5
            float r5 = r5 * r0
            float r4 = r4 + r5
            int r5 = r9.getMeasuredHeight()
            float r5 = (float) r5
            r2.set(r3, r3, r4, r5)
            r4 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            android.graphics.Paint r7 = r9.paint
            r10.drawRoundRect(r2, r5, r4, r7)
            android.graphics.drawable.Drawable r4 = r9.backgroundDrawable
            float r5 = r2.left
            int r5 = (int) r5
            float r7 = r2.top
            int r7 = (int) r7
            float r8 = r2.right
            int r8 = (int) r8
            float r2 = r2.bottom
            int r2 = (int) r2
            r4.setBounds(r5, r7, r8, r2)
            android.graphics.drawable.Drawable r2 = r9.backgroundDrawable
            r2.draw(r10)
            r10.save()
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r4 = (float) r4
            r10.translate(r2, r4)
            org.telegram.ui.ActionBar.MenuDrawable r2 = r9.backDrawable
            r2.draw(r10)
            r10.restore()
            int r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x00d9
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
        L_0x00d9:
            if (r1 == 0) goto L_0x00ec
            android.text.StaticLayout r1 = r9.menuText
            int r1 = r1.getWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r1 = r1 + r2
            float r1 = (float) r1
            float r1 = r1 * r0
            r9.onTranslationChanged(r1)
        L_0x00ec:
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

        public void setBotInfo(SparseArray<TLRPC$BotInfo> sparseArray) {
            this.newResult.clear();
            this.newResultHelp.clear();
            for (int i = 0; i < sparseArray.size(); i++) {
                TLRPC$BotInfo valueAt = sparseArray.valueAt(i);
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
