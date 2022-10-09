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
/* loaded from: classes3.dex */
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
    final Paint paint;
    final RectF rectTmp;
    final TextPaint textPaint;
    RLottieDrawable webViewAnimation;

    protected void onTranslationChanged(float f) {
    }

    public BotCommandsMenuView(Context context) {
        super(context);
        this.rectTmp = new RectF();
        this.paint = new Paint(1);
        TextPaint textPaint = new TextPaint(1);
        this.textPaint = textPaint;
        MenuDrawable menuDrawable = new MenuDrawable() { // from class: org.telegram.ui.Components.BotCommandsMenuView.1
            @Override // android.graphics.drawable.Drawable
            public void invalidateSelf() {
                super.invalidateSelf();
                BotCommandsMenuView.this.invalidate();
            }
        };
        this.backDrawable = menuDrawable;
        int i = R.raw.bot_webview_sheet_to_cross;
        this.webViewAnimation = new RLottieDrawable(i, String.valueOf(i) + hashCode(), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f)) { // from class: org.telegram.ui.Components.BotCommandsMenuView.2
            @Override // android.graphics.drawable.Drawable
            public void invalidateSelf() {
                super.invalidateSelf();
                BotCommandsMenuView.this.invalidate();
            }

            @Override // org.telegram.ui.Components.RLottieDrawable
            protected void invalidateInternal() {
                super.invalidateInternal();
                BotCommandsMenuView.this.invalidate();
            }
        };
        this.menuText = LocaleController.getString(R.string.BotsMenuTitle);
        this.drawBackgroundDrawable = true;
        updateColors();
        menuDrawable.setMiniIcon(true);
        menuDrawable.setRotateToBack(false);
        menuDrawable.setRotation(0.0f, false);
        menuDrawable.setCallback(this);
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        menuDrawable.setRoundCap();
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(16.0f), 0, Theme.getColor("featuredStickers_addButtonPressed"));
        this.backgroundDrawable = createSimpleSelectorRoundRectDrawable;
        createSimpleSelectorRoundRectDrawable.setCallback(this);
        setContentDescription(LocaleController.getString("AccDescrBotMenu", R.string.AccDescrBotMenu));
    }

    public void setDrawBackgroundDrawable(boolean z) {
        this.drawBackgroundDrawable = z;
        invalidate();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.webViewAnimation.setMasterParent(this);
        this.webViewAnimation.setCurrentParentView(this);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
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

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int size = (View.MeasureSpec.getSize(i) + View.MeasureSpec.getSize(i2)) << 16;
        if (this.lastSize != size || this.menuTextLayout == null) {
            this.backDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.textPaint.setTextSize(AndroidUtilities.dp(15.0f));
            this.lastSize = size;
            int measureText = (int) this.textPaint.measureText(this.menuText);
            this.menuTextLayout = StaticLayoutEx.createStaticLayout(this.menuText, this.textPaint, measureText, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, measureText, 1);
        }
        onTranslationChanged((this.menuTextLayout.getWidth() + AndroidUtilities.dp(4.0f)) * this.expandProgress);
        int dp = AndroidUtilities.dp(40.0f);
        if (this.expanded) {
            dp += this.menuTextLayout.getWidth() + AndroidUtilities.dp(4.0f);
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(dp, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0045  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0059  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00a8  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00d5  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00f3  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0119  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void dispatchDraw(android.graphics.Canvas r11) {
        /*
            Method dump skipped, instructions count: 302
            To view this dump add '--comments-level debug' option
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

    /* loaded from: classes3.dex */
    public static class BotCommandsAdapter extends RecyclerListView.SelectionAdapter {
        ArrayList<String> newResult = new ArrayList<>();
        ArrayList<String> newResultHelp = new ArrayList<>();

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1753onCreateViewHolder(ViewGroup viewGroup, int i) {
            BotCommandView botCommandView = new BotCommandView(viewGroup.getContext());
            botCommandView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(botCommandView);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            BotCommandView botCommandView = (BotCommandView) viewHolder.itemView;
            botCommandView.command.setText(this.newResult.get(i));
            botCommandView.description.setText(this.newResultHelp.get(i));
            botCommandView.commandStr = this.newResult.get(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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
                    if (tLRPC$TL_botCommand != null && tLRPC$TL_botCommand.command != null) {
                        ArrayList<String> arrayList = this.newResult;
                        arrayList.add("/" + tLRPC$TL_botCommand.command);
                        this.newResultHelp.add(tLRPC$TL_botCommand.description);
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
        if (this.isWebView) {
            if (this.isWebViewOpened == z) {
                return;
            }
            RLottieDrawable rLottieDrawable = this.webViewAnimation;
            rLottieDrawable.stop();
            rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
            if (z) {
                i = rLottieDrawable.getFramesCount();
            }
            rLottieDrawable.setCustomEndFrame(i);
            rLottieDrawable.start();
            this.isWebViewOpened = z;
            return;
        }
        this.backDrawable.setRotation(z ? 1.0f : 0.0f, true);
    }

    /* loaded from: classes3.dex */
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

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36.0f), NUM));
        }

        public String getCommand() {
            return this.commandStr;
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || this.backgroundDrawable == drawable;
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.backgroundDrawable.setState(getDrawableState());
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.backgroundDrawable.jumpToCurrentState();
    }
}
