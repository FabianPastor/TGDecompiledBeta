package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC$TL_replyKeyboardMarkup;
import org.telegram.ui.ActionBar.Theme;

public class BotKeyboardView extends LinearLayout {
    private TLRPC$TL_replyKeyboardMarkup botButtons;
    private int buttonHeight;
    private ArrayList<TextView> buttonViews = new ArrayList<>();
    private LinearLayout container;
    private BotKeyboardViewDelegate delegate;
    private boolean isFullSize;
    private int panelHeight;
    private ScrollView scrollView;

    public interface BotKeyboardViewDelegate {
        void didPressedButton(TLRPC$KeyboardButton tLRPC$KeyboardButton);
    }

    public BotKeyboardView(Context context) {
        super(context);
        setOrientation(1);
        ScrollView scrollView2 = new ScrollView(context);
        this.scrollView = scrollView2;
        addView(scrollView2);
        LinearLayout linearLayout = new LinearLayout(context);
        this.container = linearLayout;
        linearLayout.setOrientation(1);
        this.scrollView.addView(this.container);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("chat_emojiPanelBackground"));
        setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
    }

    public void setDelegate(BotKeyboardViewDelegate botKeyboardViewDelegate) {
        this.delegate = botKeyboardViewDelegate;
    }

    public void setPanelHeight(int i) {
        TLRPC$TL_replyKeyboardMarkup tLRPC$TL_replyKeyboardMarkup;
        this.panelHeight = i;
        if (this.isFullSize && (tLRPC$TL_replyKeyboardMarkup = this.botButtons) != null && tLRPC$TL_replyKeyboardMarkup.rows.size() != 0) {
            this.buttonHeight = !this.isFullSize ? 42 : (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            int childCount = this.container.getChildCount();
            int dp = AndroidUtilities.dp((float) this.buttonHeight);
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.container.getChildAt(i2);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childAt.getLayoutParams();
                if (layoutParams.height != dp) {
                    layoutParams.height = dp;
                    childAt.setLayoutParams(layoutParams);
                }
            }
        }
    }

    public void invalidateViews() {
        for (int i = 0; i < this.buttonViews.size(); i++) {
            this.buttonViews.get(i).invalidate();
        }
    }

    public boolean isFullSize() {
        return this.isFullSize;
    }

    public void setButtons(TLRPC$TL_replyKeyboardMarkup tLRPC$TL_replyKeyboardMarkup) {
        int i;
        TLRPC$TL_replyKeyboardMarkup tLRPC$TL_replyKeyboardMarkup2 = tLRPC$TL_replyKeyboardMarkup;
        this.botButtons = tLRPC$TL_replyKeyboardMarkup2;
        this.container.removeAllViews();
        this.buttonViews.clear();
        boolean z = false;
        this.scrollView.scrollTo(0, 0);
        if (tLRPC$TL_replyKeyboardMarkup2 != null && this.botButtons.rows.size() != 0) {
            boolean z2 = !tLRPC$TL_replyKeyboardMarkup2.resize;
            this.isFullSize = z2;
            if (!z2) {
                i = 42;
            } else {
                i = (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            }
            this.buttonHeight = i;
            int i2 = 0;
            while (i2 < tLRPC$TL_replyKeyboardMarkup2.rows.size()) {
                TLRPC$TL_keyboardButtonRow tLRPC$TL_keyboardButtonRow = tLRPC$TL_replyKeyboardMarkup2.rows.get(i2);
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(z ? 1 : 0);
                this.container.addView(linearLayout, LayoutHelper.createLinear(-1, this.buttonHeight, 15.0f, i2 == 0 ? 15.0f : 10.0f, 15.0f, i2 == tLRPC$TL_replyKeyboardMarkup2.rows.size() - 1 ? 15.0f : 0.0f));
                float size = 1.0f / ((float) tLRPC$TL_keyboardButtonRow.buttons.size());
                int i3 = 0;
                while (i3 < tLRPC$TL_keyboardButtonRow.buttons.size()) {
                    TLRPC$KeyboardButton tLRPC$KeyboardButton = tLRPC$TL_keyboardButtonRow.buttons.get(i3);
                    TextView textView = new TextView(getContext());
                    textView.setTag(tLRPC$KeyboardButton);
                    textView.setTextColor(Theme.getColor("chat_botKeyboardButtonText"));
                    textView.setTextSize(1, 16.0f);
                    textView.setGravity(17);
                    textView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("chat_botKeyboardButtonBackground"), Theme.getColor("chat_botKeyboardButtonBackgroundPressed")));
                    textView.setPadding(AndroidUtilities.dp(4.0f), z ? 1 : 0, AndroidUtilities.dp(4.0f), z);
                    textView.setText(Emoji.replaceEmoji(tLRPC$KeyboardButton.text, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), z));
                    TextView textView2 = textView;
                    linearLayout.addView(textView2, LayoutHelper.createLinear(0, -1, size, 0, 0, i3 != tLRPC$TL_keyboardButtonRow.buttons.size() - 1 ? 10 : 0, 0));
                    textView2.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            BotKeyboardView.this.lambda$setButtons$0$BotKeyboardView(view);
                        }
                    });
                    this.buttonViews.add(textView2);
                    i3++;
                    z = false;
                }
                i2++;
                z = false;
            }
        }
    }

    public /* synthetic */ void lambda$setButtons$0$BotKeyboardView(View view) {
        this.delegate.didPressedButton((TLRPC$KeyboardButton) view.getTag());
    }

    public int getKeyboardHeight() {
        return this.isFullSize ? this.panelHeight : (this.botButtons.rows.size() * AndroidUtilities.dp((float) this.buttonHeight)) + AndroidUtilities.dp(30.0f) + ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f));
    }
}
