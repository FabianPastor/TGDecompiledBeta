package org.telegram.ui.Components;

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
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardMarkup;
import org.telegram.ui.ActionBar.Theme;

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
    class C10931 implements OnClickListener {
        C10931() {
        }

        public void onClick(View v) {
            BotKeyboardView.this.delegate.didPressedButton((KeyboardButton) v.getTag());
        }
    }

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
            this.buttonHeight = !this.isFullSize ? 42 : (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            int count = this.container.getChildCount();
            int newHeight = AndroidUtilities.dp((float) this.buttonHeight);
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
        TL_replyKeyboardMarkup tL_replyKeyboardMarkup = buttons;
        this.botButtons = tL_replyKeyboardMarkup;
        this.container.removeAllViews();
        this.buttonViews.clear();
        boolean z = false;
        this.scrollView.scrollTo(0, 0);
        if (tL_replyKeyboardMarkup != null && r0.botButtons.rows.size() != 0) {
            r0.isFullSize = tL_replyKeyboardMarkup.resize ^ true;
            float f = 10.0f;
            r0.buttonHeight = !r0.isFullSize ? 42 : (int) Math.max(42.0f, ((float) (((r0.panelHeight - AndroidUtilities.dp(30.0f)) - ((r0.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / r0.botButtons.rows.size())) / AndroidUtilities.density);
            int a = 0;
            while (a < tL_replyKeyboardMarkup.rows.size()) {
                TL_keyboardButtonRow row = (TL_keyboardButtonRow) tL_replyKeyboardMarkup.rows.get(a);
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(z);
                LinearLayout linearLayout = r0.container;
                int i = r0.buttonHeight;
                float f2 = 15.0f;
                float f3 = a == 0 ? 15.0f : f;
                if (a != tL_replyKeyboardMarkup.rows.size() - 1) {
                    f2 = 0.0f;
                }
                linearLayout.addView(layout, LayoutHelper.createLinear(-1, i, 15.0f, f3, 15.0f, f2));
                float weight = 1.0f / ((float) row.buttons.size());
                int b = z;
                while (b < row.buttons.size()) {
                    KeyboardButton button = (KeyboardButton) row.buttons.get(b);
                    TextView textView = new TextView(getContext());
                    textView.setTag(button);
                    textView.setTextColor(Theme.getColor(Theme.key_chat_botKeyboardButtonText));
                    textView.setTextSize(1, 16.0f);
                    textView.setGravity(17);
                    textView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_chat_botKeyboardButtonBackground), Theme.getColor(Theme.key_chat_botKeyboardButtonBackgroundPressed)));
                    textView.setPadding(AndroidUtilities.dp(4.0f), z, AndroidUtilities.dp(4.0f), z);
                    textView.setText(Emoji.replaceEmoji(button.text, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), z));
                    boolean z2 = b != row.buttons.size() - 1 ? true : z;
                    TextView textView2 = textView;
                    layout.addView(textView2, LayoutHelper.createLinear(0, -1, weight, 0, 0, (int) z2, 0));
                    textView2.setOnClickListener(new C10931());
                    r0.buttonViews.add(textView2);
                    b++;
                    z = false;
                }
                a++;
                z = false;
                f = 10.0f;
            }
        }
    }

    public int getKeyboardHeight() {
        return this.isFullSize ? this.panelHeight : ((this.botButtons.rows.size() * AndroidUtilities.dp((float) this.buttonHeight)) + AndroidUtilities.dp(30.0f)) + ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f));
    }
}
