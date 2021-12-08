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
import org.telegram.tgnet.TLRPC;
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

    public BotCommandsMenuView(Context context) {
        super(context);
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        AnonymousClass1 r2 = new MenuDrawable() {
            public void invalidateSelf() {
                super.invalidateSelf();
                BotCommandsMenuView.this.invalidate();
            }
        };
        this.backDrawable = r2;
        updateColors();
        r2.setMiniIcon(true);
        r2.setRotateToBack(false);
        r2.setRotation(0.0f, false);
        r2.setCallback(this);
        textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r2.setRoundCap();
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(16.0f), 0, Theme.getColor("windowBackgroundWhite"));
        this.backgroundDrawable = createSimpleSelectorRoundRectDrawable;
        createSimpleSelectorRoundRectDrawable.setCallback(this);
    }

    private void updateColors() {
        this.paint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
        int textColor = Theme.getColor("chat_messagePanelVoicePressed");
        this.backDrawable.setBackColor(textColor);
        this.backDrawable.setIconColor(textColor);
        this.textPaint.setColor(textColor);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (View.MeasureSpec.getSize(widthMeasureSpec) + View.MeasureSpec.getSize(heightMeasureSpec)) << 16;
        if (this.lastSize != size || this.menuText == null) {
            this.backDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.textPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            this.lastSize = size;
            String string = LocaleController.getString("BotsMenuTitle", NUM);
            int w = (int) this.textPaint.measureText(string);
            this.menuText = StaticLayoutEx.createStaticLayout(string, this.textPaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, w, 1);
        }
        onTranslationChanged(((float) (this.menuText.getWidth() + AndroidUtilities.dp(4.0f))) * this.expandProgress);
        int width = AndroidUtilities.dp(40.0f);
        if (this.expanded) {
            width += this.menuText.getWidth() + AndroidUtilities.dp(4.0f);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00c3  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00e9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchDraw(android.graphics.Canvas r10) {
        /*
            r9 = this;
            android.text.StaticLayout r0 = r9.menuText
            if (r0 == 0) goto L_0x00fa
            r0 = 0
            boolean r1 = r9.expanded
            r2 = 1037726734(0x3dda740e, float:0.10666667)
            r3 = 0
            if (r1 == 0) goto L_0x0024
            float r4 = r9.expandProgress
            r5 = 1065353216(0x3var_, float:1.0)
            int r6 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r6 == 0) goto L_0x0024
            float r4 = r4 + r2
            r9.expandProgress = r4
            int r1 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x001f
            r9.expandProgress = r5
            goto L_0x0022
        L_0x001f:
            r9.invalidate()
        L_0x0022:
            r0 = 1
            goto L_0x003a
        L_0x0024:
            if (r1 != 0) goto L_0x003a
            float r1 = r9.expandProgress
            int r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x003a
            float r1 = r1 - r2
            r9.expandProgress = r1
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 >= 0) goto L_0x0036
            r9.expandProgress = r3
            goto L_0x0039
        L_0x0036:
            r9.invalidate()
        L_0x0039:
            r0 = 1
        L_0x003a:
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            float r2 = r9.expandProgress
            float r1 = r1.getInterpolation(r2)
            if (r0 == 0) goto L_0x0052
            int r2 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0052
            android.text.TextPaint r2 = r9.textPaint
            r4 = 1132396544(0x437var_, float:255.0)
            float r4 = r4 * r1
            int r4 = (int) r4
            r2.setAlpha(r4)
        L_0x0052:
            android.graphics.RectF r2 = r9.rectTmp
            r4 = 1109393408(0x42200000, float:40.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            android.text.StaticLayout r5 = r9.menuText
            int r5 = r5.getWidth()
            r6 = 1082130432(0x40800000, float:4.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r7
            float r5 = (float) r5
            float r5 = r5 * r1
            float r4 = r4 + r5
            int r5 = r9.getMeasuredHeight()
            float r5 = (float) r5
            r2.set(r3, r3, r4, r5)
            android.graphics.RectF r2 = r9.rectTmp
            r4 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            android.graphics.Paint r7 = r9.paint
            r10.drawRoundRect(r2, r5, r4, r7)
            android.graphics.drawable.Drawable r2 = r9.backgroundDrawable
            android.graphics.RectF r4 = r9.rectTmp
            float r4 = r4.left
            int r4 = (int) r4
            android.graphics.RectF r5 = r9.rectTmp
            float r5 = r5.top
            int r5 = (int) r5
            android.graphics.RectF r7 = r9.rectTmp
            float r7 = r7.right
            int r7 = (int) r7
            android.graphics.RectF r8 = r9.rectTmp
            float r8 = r8.bottom
            int r8 = (int) r8
            r2.setBounds(r4, r5, r7, r8)
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
            int r2 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x00e7
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
        L_0x00e7:
            if (r0 == 0) goto L_0x00fa
            android.text.StaticLayout r2 = r9.menuText
            int r2 = r2.getWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r2 = r2 + r3
            float r2 = (float) r2
            float r2 = r2 * r1
            r9.onTranslationChanged(r2)
        L_0x00fa:
            super.dispatchDraw(r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotCommandsMenuView.dispatchDraw(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    public void onTranslationChanged(float translationX) {
    }

    public void setExpanded(boolean expanded2, boolean animated) {
        if (this.expanded != expanded2) {
            this.expanded = expanded2;
            if (!animated) {
                this.expandProgress = expanded2 ? 1.0f : 0.0f;
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

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            BotCommandView view = new BotCommandView(parent.getContext());
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            BotCommandView view = (BotCommandView) holder.itemView;
            view.command.setText(this.newResult.get(position));
            view.description.setText(this.newResultHelp.get(position));
            view.commandStr = this.newResult.get(position);
        }

        public int getItemCount() {
            return this.newResult.size();
        }

        public void setBotInfo(LongSparseArray<TLRPC.BotInfo> botInfo) {
            this.newResult.clear();
            this.newResultHelp.clear();
            for (int b = 0; b < botInfo.size(); b++) {
                TLRPC.BotInfo info = botInfo.valueAt(b);
                for (int a = 0; a < info.commands.size(); a++) {
                    TLRPC.TL_botCommand botCommand = info.commands.get(a);
                    if (!(botCommand == null || botCommand.command == null)) {
                        ArrayList<String> arrayList = this.newResult;
                        arrayList.add("/" + botCommand.command);
                        if (botCommand.description == null || botCommand.description.length() <= 1) {
                            this.newResultHelp.add(botCommand.description);
                        } else {
                            ArrayList<String> arrayList2 = this.newResultHelp;
                            arrayList2.add(botCommand.description.substring(0, 1).toUpperCase() + botCommand.description.substring(1).toLowerCase());
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setOpened(boolean opened) {
        if (this.isOpened != opened) {
            this.isOpened = opened;
        }
        this.backDrawable.setRotation(opened ? 1.0f : 0.0f, true);
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36.0f), NUM));
        }

        public String getCommand() {
            return this.commandStr;
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || this.backgroundDrawable == who;
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
