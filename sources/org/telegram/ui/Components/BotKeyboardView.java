package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonSimpleWebView;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonWebView;
import org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public class BotKeyboardView extends LinearLayout {
    private TLRPC$TL_replyKeyboardMarkup botButtons;
    private int buttonHeight;
    private ArrayList<ImageView> buttonIcons;
    private ArrayList<TextView> buttonViews;
    private LinearLayout container;
    private BotKeyboardViewDelegate delegate;
    private boolean isFullSize;
    private int panelHeight;
    private final Theme.ResourcesProvider resourcesProvider;
    private ScrollView scrollView;

    /* loaded from: classes3.dex */
    public interface BotKeyboardViewDelegate {
        void didPressedButton(TLRPC$KeyboardButton tLRPC$KeyboardButton);
    }

    public BotKeyboardView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.buttonViews = new ArrayList<>();
        this.buttonIcons = new ArrayList<>();
        this.resourcesProvider = resourcesProvider;
        setOrientation(1);
        ScrollView scrollView = new ScrollView(context);
        this.scrollView = scrollView;
        addView(scrollView);
        LinearLayout linearLayout = new LinearLayout(context);
        this.container = linearLayout;
        linearLayout.setOrientation(1);
        this.scrollView.addView(this.container);
        updateColors();
    }

    public void updateColors() {
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, getThemedColor("chat_emojiPanelBackground"));
        setBackgroundColor(getThemedColor("chat_emojiPanelBackground"));
        for (int i = 0; i < this.buttonViews.size(); i++) {
            this.buttonViews.get(i).setTextColor(getThemedColor("chat_botKeyboardButtonText"));
            this.buttonViews.get(i).setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), getThemedColor("chat_botKeyboardButtonBackground"), getThemedColor("chat_botKeyboardButtonBackgroundPressed")));
            this.buttonIcons.get(i).setColorFilter(getThemedColor("chat_botKeyboardButtonText"));
        }
        invalidate();
    }

    public void setDelegate(BotKeyboardViewDelegate botKeyboardViewDelegate) {
        this.delegate = botKeyboardViewDelegate;
    }

    public void setPanelHeight(int i) {
        TLRPC$TL_replyKeyboardMarkup tLRPC$TL_replyKeyboardMarkup;
        this.panelHeight = i;
        if (!this.isFullSize || (tLRPC$TL_replyKeyboardMarkup = this.botButtons) == null || tLRPC$TL_replyKeyboardMarkup.rows.size() == 0) {
            return;
        }
        this.buttonHeight = !this.isFullSize ? 42 : (int) Math.max(42.0f, (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size()) / AndroidUtilities.density);
        int childCount = this.container.getChildCount();
        int dp = AndroidUtilities.dp(this.buttonHeight);
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.container.getChildAt(i2);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childAt.getLayoutParams();
            if (layoutParams.height != dp) {
                layoutParams.height = dp;
                childAt.setLayoutParams(layoutParams);
            }
        }
    }

    public void invalidateViews() {
        for (int i = 0; i < this.buttonViews.size(); i++) {
            this.buttonViews.get(i).invalidate();
            this.buttonIcons.get(i).invalidate();
        }
    }

    public boolean isFullSize() {
        return this.isFullSize;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v13 */
    /* JADX WARN: Type inference failed for: r3v19 */
    /* JADX WARN: Type inference failed for: r3v2, types: [int, boolean] */
    public void setButtons(TLRPC$TL_replyKeyboardMarkup tLRPC$TL_replyKeyboardMarkup) {
        this.botButtons = tLRPC$TL_replyKeyboardMarkup;
        this.container.removeAllViews();
        this.buttonViews.clear();
        this.buttonIcons.clear();
        boolean z = false;
        this.scrollView.scrollTo(0, 0);
        if (tLRPC$TL_replyKeyboardMarkup == null || this.botButtons.rows.size() == 0) {
            return;
        }
        int i = 1;
        boolean z2 = !tLRPC$TL_replyKeyboardMarkup.resize;
        this.isFullSize = z2;
        this.buttonHeight = !z2 ? 42 : (int) Math.max(42.0f, (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size()) / AndroidUtilities.density);
        int i2 = 0;
        while (i2 < tLRPC$TL_replyKeyboardMarkup.rows.size()) {
            TLRPC$TL_keyboardButtonRow tLRPC$TL_keyboardButtonRow = tLRPC$TL_replyKeyboardMarkup.rows.get(i2);
            LinearLayout linearLayout = new LinearLayout(getContext());
            int i3 = z ? 1 : 0;
            int i4 = z ? 1 : 0;
            int i5 = z ? 1 : 0;
            linearLayout.setOrientation(i3);
            this.container.addView(linearLayout, LayoutHelper.createLinear(-1, this.buttonHeight, 15.0f, i2 == 0 ? 15.0f : 10.0f, 15.0f, i2 == tLRPC$TL_replyKeyboardMarkup.rows.size() - i ? 15.0f : 0.0f));
            float size = 1.0f / tLRPC$TL_keyboardButtonRow.buttons.size();
            int i6 = 0;
            ?? r3 = z;
            while (i6 < tLRPC$TL_keyboardButtonRow.buttons.size()) {
                TLRPC$KeyboardButton tLRPC$KeyboardButton = tLRPC$TL_keyboardButtonRow.buttons.get(i6);
                TextView textView = new TextView(getContext());
                textView.setTag(tLRPC$KeyboardButton);
                textView.setTextColor(getThemedColor("chat_botKeyboardButtonText"));
                textView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), getThemedColor("chat_botKeyboardButtonBackground"), getThemedColor("chat_botKeyboardButtonBackgroundPressed")));
                textView.setTextSize(i, 16.0f);
                textView.setGravity(17);
                FrameLayout frameLayout = new FrameLayout(getContext());
                frameLayout.addView(textView, LayoutHelper.createFrame(-1, -1.0f));
                textView.setPadding(AndroidUtilities.dp(4.0f), r3 == true ? 1 : 0, AndroidUtilities.dp(4.0f), r3);
                textView.setText(Emoji.replaceEmoji(tLRPC$KeyboardButton.text, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), r3));
                linearLayout.addView(frameLayout, LayoutHelper.createLinear(0, -1, size, 0, 0, i6 != tLRPC$TL_keyboardButtonRow.buttons.size() + (-1) ? 10 : 0, 0));
                textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.BotKeyboardView$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        BotKeyboardView.this.lambda$setButtons$0(view);
                    }
                });
                this.buttonViews.add(textView);
                ImageView imageView = new ImageView(getContext());
                imageView.setColorFilter(getThemedColor("chat_botKeyboardButtonText"));
                if ((tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonWebView) || (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonSimpleWebView)) {
                    imageView.setImageResource(R.drawable.bot_webview);
                    imageView.setVisibility(0);
                } else {
                    imageView.setVisibility(8);
                }
                this.buttonIcons.add(imageView);
                frameLayout.addView(imageView, LayoutHelper.createFrame(12, 12.0f, 53, 0.0f, 8.0f, 8.0f, 0.0f));
                i6++;
                r3 = 0;
                i = 1;
            }
            i2++;
            z = false;
            i = 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setButtons$0(View view) {
        this.delegate.didPressedButton((TLRPC$KeyboardButton) view.getTag());
    }

    public int getKeyboardHeight() {
        TLRPC$TL_replyKeyboardMarkup tLRPC$TL_replyKeyboardMarkup = this.botButtons;
        if (tLRPC$TL_replyKeyboardMarkup == null) {
            return 0;
        }
        return this.isFullSize ? this.panelHeight : (tLRPC$TL_replyKeyboardMarkup.rows.size() * AndroidUtilities.dp(this.buttonHeight)) + AndroidUtilities.dp(30.0f) + ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f));
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
