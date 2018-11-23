package org.telegram.p005ui.Components;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup;

/* renamed from: org.telegram.ui.Components.BotKeyboardView */
public class BotKeyboardView extends LinearLayout {
    private TL_replyKeyboardMarkup botButtons;
    private int buttonHeight;
    private ArrayList<TextView> buttonViews = new ArrayList();
    private LinearLayout container;
    private BotKeyboardViewDelegate delegate;
    private boolean isFullSize;
    private int panelHeight;
    private ScrollView scrollView;

    /* renamed from: org.telegram.ui.Components.BotKeyboardView$1 */
    class C06191 implements OnClickListener {
        C06191() {
        }

        public void onClick(View v) {
            BotKeyboardView.this.delegate.didPressedButton((KeyboardButton) v.getTag());
        }
    }

    /* renamed from: org.telegram.ui.Components.BotKeyboardView$BotKeyboardViewDelegate */
    public interface BotKeyboardViewDelegate {
        void didPressedButton(KeyboardButton keyboardButton);
    }

    public BotKeyboardView(Context context) {
        super(context);
        setOrientation(1);
        this.scrollView = new ScrollView(context);
        addView(this.scrollView);
        this.container = new LinearLayout(context);
        this.container.setOrientation(1);
        this.scrollView.addView(this.container);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_chat_emojiPanelBackground));
        setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
    }

    public void setDelegate(BotKeyboardViewDelegate botKeyboardViewDelegate) {
        this.delegate = botKeyboardViewDelegate;
    }

    public void setPanelHeight(int height) {
        this.panelHeight = height;
        if (this.isFullSize && this.botButtons != null && this.botButtons.rows.size() != 0) {
            int max;
            if (this.isFullSize) {
                max = (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.m9dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.m9dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            } else {
                max = 42;
            }
            this.buttonHeight = max;
            int count = this.container.getChildCount();
            int newHeight = AndroidUtilities.m9dp((float) this.buttonHeight);
            for (int a = 0; a < count; a++) {
                View v = this.container.getChildAt(a);
                LayoutParams layoutParams = (LayoutParams) v.getLayoutParams();
                if (layoutParams.height != newHeight) {
                    layoutParams.height = newHeight;
                    v.setLayoutParams(layoutParams);
                }
            }
        }
    }

    public void invalidateViews() {
        for (int a = 0; a < this.buttonViews.size(); a++) {
            ((TextView) this.buttonViews.get(a)).invalidate();
        }
    }

    public boolean isFullSize() {
        return this.isFullSize;
    }

    public void setButtons(TL_replyKeyboardMarkup buttons) {
        this.botButtons = buttons;
        this.container.removeAllViews();
        this.buttonViews.clear();
        this.scrollView.scrollTo(0, 0);
        if (buttons != null && this.botButtons.rows.size() != 0) {
            this.isFullSize = !buttons.resize;
            this.buttonHeight = !this.isFullSize ? 42 : (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.m9dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.m9dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            int a = 0;
            while (a < buttons.rows.size()) {
                float f;
                TL_keyboardButtonRow row = (TL_keyboardButtonRow) buttons.rows.get(a);
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(0);
                LinearLayout linearLayout = this.container;
                int i = this.buttonHeight;
                float f2 = a == 0 ? 15.0f : 10.0f;
                if (a == buttons.rows.size() - 1) {
                    f = 15.0f;
                } else {
                    f = 0.0f;
                }
                linearLayout.addView(layout, LayoutHelper.createLinear(-1, i, 15.0f, f2, 15.0f, f));
                float weight = 1.0f / ((float) row.buttons.size());
                int b = 0;
                while (b < row.buttons.size()) {
                    KeyboardButton button = (KeyboardButton) row.buttons.get(b);
                    TextView textView = new TextView(getContext());
                    textView.setTag(button);
                    textView.setTextColor(Theme.getColor(Theme.key_chat_botKeyboardButtonText));
                    textView.setTextSize(1, 16.0f);
                    textView.setGravity(17);
                    textView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.m9dp(4.0f), Theme.getColor(Theme.key_chat_botKeyboardButtonBackground), Theme.getColor(Theme.key_chat_botKeyboardButtonBackgroundPressed)));
                    textView.setPadding(AndroidUtilities.m9dp(4.0f), 0, AndroidUtilities.m9dp(4.0f), 0);
                    textView.setText(Emoji.replaceEmoji(button.text, textView.getPaint().getFontMetricsInt(), AndroidUtilities.m9dp(16.0f), false));
                    layout.addView(textView, LayoutHelper.createLinear(0, -1, weight, 0, 0, b != row.buttons.size() + -1 ? 10 : 0, 0));
                    textView.setOnClickListener(new C06191());
                    this.buttonViews.add(textView);
                    b++;
                }
                a++;
            }
        }
    }

    public int getKeyboardHeight() {
        return this.isFullSize ? this.panelHeight : ((this.botButtons.rows.size() * AndroidUtilities.m9dp((float) this.buttonHeight)) + AndroidUtilities.m9dp(30.0f)) + ((this.botButtons.rows.size() - 1) * AndroidUtilities.m9dp(10.0f));
    }
}
