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
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$TL_botCommand;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public class BotCommandsMenuView extends View {
    final MenuDrawable backDrawable;
    Drawable backgroundDrawable;
    boolean drawBackgroundDrawable;
    float expandProgress;
    boolean expanded;
    boolean isOpened;
    boolean isWebView;
    boolean isWebViewOpened;
    int lastSize;
    private String menuText;
    StaticLayout menuTextLayout;
    final Paint paint = new Paint(1);
    final RectF rectTmp = new RectF();
    final TextPaint textPaint;
    RLottieDrawable webViewAnimation;

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
        int i = R.raw.bot_webview_sheet_to_cross;
        this.webViewAnimation = new RLottieDrawable(i, String.valueOf(i) + hashCode(), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f)) {
            public void invalidateSelf() {
                super.invalidateSelf();
                BotCommandsMenuView.this.invalidate();
            }

            /* access modifiers changed from: protected */
            public void invalidateInternal() {
                super.invalidateInternal();
                BotCommandsMenuView.this.invalidate();
            }
        };
        this.menuText = LocaleController.getString(R.string.BotsMenuTitle);
        this.drawBackgroundDrawable = true;
        updateColors();
        r1.setMiniIcon(true);
        r1.setRotateToBack(false);
        r1.setRotation(0.0f, false);
        r1.setCallback(this);
        textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r1.setRoundCap();
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(16.0f), 0, Theme.getColor("featuredStickers_addButtonPressed"));
        this.backgroundDrawable = createSimpleSelectorRoundRectDrawable;
        createSimpleSelectorRoundRectDrawable.setCallback(this);
        setContentDescription(LocaleController.getString("AccDescrBotMenu", R.string.AccDescrBotMenu));
    }

    public void setDrawBackgroundDrawable(boolean z) {
        this.drawBackgroundDrawable = z;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.webViewAnimation.setMasterParent(this);
        this.webViewAnimation.setCurrentParentView(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.webViewAnimation.setMasterParent(this);
    }

    public void setWebView(boolean z) {
        this.isWebView = z;
        invalidate();
    }

    private void updateColors() {
        this.paint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
        int color = Theme.getColor("chat_messagePanelVoicePressed");
        this.backDrawable.setBackColor(color);
        this.backDrawable.setIconColor(color);
        this.textPaint.setColor(color);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = (View.MeasureSpec.getSize(i) + View.MeasureSpec.getSize(i2)) << 16;
        if (this.lastSize != size || this.menuTextLayout == null) {
            this.backDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.textPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            this.lastSize = size;
            int measureText = (int) this.textPaint.measureText(this.menuText);
            this.menuTextLayout = StaticLayoutEx.createStaticLayout(this.menuText, this.textPaint, measureText, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, measureText, 1);
        }
        onTranslationChanged(((float) (this.menuTextLayout.getWidth() + AndroidUtilities.dp(4.0f))) * this.expandProgress);
        int dp = AndroidUtilities.dp(40.0f);
        if (this.expanded) {
            dp += this.menuTextLayout.getWidth() + AndroidUtilities.dp(4.0f);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(dp, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0059  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00d5  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0119  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchDraw(android.graphics.Canvas r11) {
        /*
            r10 = this;
            android.text.StaticLayout r0 = r10.menuTextLayout
            if (r0 == 0) goto L_0x012a
            boolean r0 = r10.expanded
            r1 = 1
            r2 = 1037726734(0x3dda740e, float:0.10666667)
            r3 = 0
            r4 = 0
            if (r0 == 0) goto L_0x0024
            float r5 = r10.expandProgress
            r6 = 1065353216(0x3var_, float:1.0)
            int r7 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r7 == 0) goto L_0x0024
            float r5 = r5 + r2
            r10.expandProgress = r5
            int r0 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x0020
            r10.expandProgress = r6
            goto L_0x003b
        L_0x0020:
            r10.invalidate()
            goto L_0x003b
        L_0x0024:
            if (r0 != 0) goto L_0x003a
            float r0 = r10.expandProgress
            int r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r5 == 0) goto L_0x003a
            float r0 = r0 - r2
            r10.expandProgress = r0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x0036
            r10.expandProgress = r4
            goto L_0x003b
        L_0x0036:
            r10.invalidate()
            goto L_0x003b
        L_0x003a:
            r1 = 0
        L_0x003b:
            org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            float r2 = r10.expandProgress
            float r0 = r0.getInterpolation(r2)
            if (r1 == 0) goto L_0x0053
            int r2 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0053
            android.text.TextPaint r2 = r10.textPaint
            r5 = 1132396544(0x437var_, float:255.0)
            float r5 = r5 * r0
            int r5 = (int) r5
            r2.setAlpha(r5)
        L_0x0053:
            boolean r2 = r10.drawBackgroundDrawable
            r5 = 1082130432(0x40800000, float:4.0)
            if (r2 == 0) goto L_0x00a4
            android.graphics.RectF r2 = r10.rectTmp
            r6 = 1109393408(0x42200000, float:40.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            android.text.StaticLayout r7 = r10.menuTextLayout
            int r7 = r7.getWidth()
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r7 = r7 + r8
            float r7 = (float) r7
            float r7 = r7 * r0
            float r6 = r6 + r7
            int r7 = r10.getMeasuredHeight()
            float r7 = (float) r7
            r2.set(r4, r4, r6, r7)
            android.graphics.RectF r2 = r10.rectTmp
            r6 = 1098907648(0x41800000, float:16.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            android.graphics.Paint r8 = r10.paint
            r11.drawRoundRect(r2, r7, r6, r8)
            android.graphics.drawable.Drawable r2 = r10.backgroundDrawable
            android.graphics.RectF r6 = r10.rectTmp
            float r7 = r6.left
            int r7 = (int) r7
            float r8 = r6.top
            int r8 = (int) r8
            float r9 = r6.right
            int r9 = (int) r9
            float r6 = r6.bottom
            int r6 = (int) r6
            r2.setBounds(r7, r8, r9, r6)
            android.graphics.drawable.Drawable r2 = r10.backgroundDrawable
            r2.draw(r11)
        L_0x00a4:
            boolean r2 = r10.isWebView
            if (r2 == 0) goto L_0x00d5
            r11.save()
            r2 = 1092091904(0x41180000, float:9.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            r11.translate(r2, r6)
            org.telegram.ui.Components.RLottieDrawable r2 = r10.webViewAnimation
            int r6 = r2.width
            int r7 = r2.height
            r2.setBounds(r3, r3, r6, r7)
            r2.draw(r11)
            r11.restore()
            boolean r2 = r2.isRunning()
            if (r2 == 0) goto L_0x00ef
            r10.invalidate()
            goto L_0x00ef
        L_0x00d5:
            r11.save()
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            r11.translate(r2, r3)
            org.telegram.ui.ActionBar.MenuDrawable r2 = r10.backDrawable
            r2.draw(r11)
            r11.restore()
        L_0x00ef:
            int r2 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0117
            r11.save()
            r2 = 1107820544(0x42080000, float:34.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            int r3 = r10.getMeasuredHeight()
            android.text.StaticLayout r4 = r10.menuTextLayout
            int r4 = r4.getHeight()
            int r3 = r3 - r4
            float r3 = (float) r3
            r4 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 / r4
            r11.translate(r2, r3)
            android.text.StaticLayout r2 = r10.menuTextLayout
            r2.draw(r11)
            r11.restore()
        L_0x0117:
            if (r1 == 0) goto L_0x012a
            android.text.StaticLayout r1 = r10.menuTextLayout
            int r1 = r1.getWidth()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r1 = r1 + r2
            float r1 = (float) r1
            float r1 = r1 * r0
            r10.onTranslationChanged(r1)
        L_0x012a:
            super.dispatchDraw(r11)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BotCommandsMenuView.dispatchDraw(android.graphics.Canvas):void");
    }

    public boolean setMenuText(String str) {
        if (str == null) {
            str = LocaleController.getString(R.string.BotsMenuTitle);
        }
        String str2 = this.menuText;
        boolean z = str2 == null || !str2.equals(str);
        this.menuText = str;
        this.menuTextLayout = null;
        requestLayout();
        return z;
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
        int i = 1;
        if (!this.isWebView) {
            this.backDrawable.setRotation(z ? 1.0f : 0.0f, true);
        } else if (this.isWebViewOpened != z) {
            RLottieDrawable rLottieDrawable = this.webViewAnimation;
            rLottieDrawable.stop();
            rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
            if (z) {
                i = rLottieDrawable.getFramesCount();
            }
            rLottieDrawable.setCustomEndFrame(i);
            rLottieDrawable.start();
            this.isWebViewOpened = z;
        }
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
