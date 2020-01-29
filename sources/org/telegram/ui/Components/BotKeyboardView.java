package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class BotKeyboardView extends LinearLayout {
    private TLRPC.TL_replyKeyboardMarkup botButtons;
    private int buttonHeight;
    private ArrayList<TextView> buttonViews = new ArrayList<>();
    private LinearLayout container;
    private BotKeyboardViewDelegate delegate;
    private boolean isFullSize;
    private int panelHeight;
    private ScrollView scrollView;

    public interface BotKeyboardViewDelegate {
        void didPressedButton(TLRPC.KeyboardButton keyboardButton);
    }

    public BotKeyboardView(Context context) {
        super(context);
        setOrientation(1);
        this.scrollView = new ScrollView(context);
        addView(this.scrollView);
        this.container = new LinearLayout(context);
        this.container.setOrientation(1);
        this.scrollView.addView(this.container);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("chat_emojiPanelBackground"));
        setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
    }

    public void setDelegate(BotKeyboardViewDelegate botKeyboardViewDelegate) {
        this.delegate = botKeyboardViewDelegate;
    }

    public void setPanelHeight(int i) {
        TLRPC.TL_replyKeyboardMarkup tL_replyKeyboardMarkup;
        this.panelHeight = i;
        if (this.isFullSize && (tL_replyKeyboardMarkup = this.botButtons) != null && tL_replyKeyboardMarkup.rows.size() != 0) {
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

    public void setButtons(TLRPC.TL_replyKeyboardMarkup tL_replyKeyboardMarkup) {
        TLRPC.TL_replyKeyboardMarkup tL_replyKeyboardMarkup2 = tL_replyKeyboardMarkup;
        this.botButtons = tL_replyKeyboardMarkup2;
        this.container.removeAllViews();
        this.buttonViews.clear();
        boolean z = false;
        this.scrollView.scrollTo(0, 0);
        if (tL_replyKeyboardMarkup2 != null && this.botButtons.rows.size() != 0) {
            this.isFullSize = !tL_replyKeyboardMarkup2.resize;
            this.buttonHeight = !this.isFullSize ? 42 : (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            int i = 0;
            while (i < tL_replyKeyboardMarkup2.rows.size()) {
                TLRPC.TL_keyboardButtonRow tL_keyboardButtonRow = tL_replyKeyboardMarkup2.rows.get(i);
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(z ? 1 : 0);
                this.container.addView(linearLayout, LayoutHelper.createLinear(-1, this.buttonHeight, 15.0f, i == 0 ? 15.0f : 10.0f, 15.0f, i == tL_replyKeyboardMarkup2.rows.size() - 1 ? 15.0f : 0.0f));
                float size = 1.0f / ((float) tL_keyboardButtonRow.buttons.size());
                int i2 = 0;
                while (i2 < tL_keyboardButtonRow.buttons.size()) {
                    TLRPC.KeyboardButton keyboardButton = tL_keyboardButtonRow.buttons.get(i2);
                    TextView textView = new TextView(getContext());
                    textView.setTag(keyboardButton);
                    textView.setTextColor(Theme.getColor("chat_botKeyboardButtonText"));
                    textView.setTextSize(1, 16.0f);
                    textView.setGravity(17);
                    textView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("chat_botKeyboardButtonBackground"), Theme.getColor("chat_botKeyboardButtonBackgroundPressed")));
                    textView.setPadding(AndroidUtilities.dp(4.0f), z ? 1 : 0, AndroidUtilities.dp(4.0f), z);
                    textView.setText(Emoji.replaceEmoji(keyboardButton.text, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), z));
                    TextView textView2 = textView;
                    linearLayout.addView(textView2, LayoutHelper.createLinear(0, -1, size, 0, 0, i2 != tL_keyboardButtonRow.buttons.size() - 1 ? 10 : 0, 0));
                    textView2.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            BotKeyboardView.this.lambda$setButtons$0$BotKeyboardView(view);
                        }
                    });
                    this.buttonViews.add(textView2);
                    i2++;
                    z = false;
                }
                i++;
                z = false;
            }
        }
    }

    public /* synthetic */ void lambda$setButtons$0$BotKeyboardView(View view) {
        this.delegate.didPressedButton((TLRPC.KeyboardButton) view.getTag());
    }

    public int getKeyboardHeight() {
        return this.isFullSize ? this.panelHeight : (this.botButtons.rows.size() * AndroidUtilities.dp((float) this.buttonHeight)) + AndroidUtilities.dp(30.0f) + ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f));
    }
}
