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

        public void onClick(View view) {
            BotKeyboardView.this.delegate.didPressedButton((KeyboardButton) view.getTag());
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

    public void setPanelHeight(int i) {
        this.panelHeight = i;
        if (this.isFullSize != 0 && this.botButtons != 0 && this.botButtons.rows.size() != 0) {
            this.buttonHeight = this.isFullSize == 0 ? 42 : (int) Math.max(NUM, ((float) (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            i = this.container.getChildCount();
            int dp = AndroidUtilities.dp((float) this.buttonHeight);
            for (int i2 = 0; i2 < i; i2++) {
                View childAt = this.container.getChildAt(i2);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.height != dp) {
                    layoutParams.height = dp;
                    childAt.setLayoutParams(layoutParams);
                }
            }
        }
    }

    public void invalidateViews() {
        for (int i = 0; i < this.buttonViews.size(); i++) {
            ((TextView) this.buttonViews.get(i)).invalidate();
        }
    }

    public boolean isFullSize() {
        return this.isFullSize;
    }

    public void setButtons(TL_replyKeyboardMarkup tL_replyKeyboardMarkup) {
        TL_replyKeyboardMarkup tL_replyKeyboardMarkup2 = tL_replyKeyboardMarkup;
        this.botButtons = tL_replyKeyboardMarkup2;
        this.container.removeAllViews();
        this.buttonViews.clear();
        boolean z = false;
        this.scrollView.scrollTo(0, 0);
        if (tL_replyKeyboardMarkup2 != null && r0.botButtons.rows.size() != 0) {
            r0.isFullSize = tL_replyKeyboardMarkup2.resize ^ true;
            float f = 10.0f;
            r0.buttonHeight = !r0.isFullSize ? 42 : (int) Math.max(42.0f, ((float) (((r0.panelHeight - AndroidUtilities.dp(30.0f)) - ((r0.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / r0.botButtons.rows.size())) / AndroidUtilities.density);
            int i = 0;
            while (i < tL_replyKeyboardMarkup2.rows.size()) {
                TL_keyboardButtonRow tL_keyboardButtonRow = (TL_keyboardButtonRow) tL_replyKeyboardMarkup2.rows.get(i);
                View linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(z);
                LinearLayout linearLayout2 = r0.container;
                int i2 = r0.buttonHeight;
                float f2 = 15.0f;
                float f3 = i == 0 ? 15.0f : f;
                if (i != tL_replyKeyboardMarkup2.rows.size() - 1) {
                    f2 = 0.0f;
                }
                linearLayout2.addView(linearLayout, LayoutHelper.createLinear(-1, i2, 15.0f, f3, 15.0f, f2));
                float size = 1.0f / ((float) tL_keyboardButtonRow.buttons.size());
                int i3 = z;
                while (i3 < tL_keyboardButtonRow.buttons.size()) {
                    KeyboardButton keyboardButton = (KeyboardButton) tL_keyboardButtonRow.buttons.get(i3);
                    View textView = new TextView(getContext());
                    textView.setTag(keyboardButton);
                    textView.setTextColor(Theme.getColor(Theme.key_chat_botKeyboardButtonText));
                    textView.setTextSize(1, 16.0f);
                    textView.setGravity(17);
                    textView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor(Theme.key_chat_botKeyboardButtonBackground), Theme.getColor(Theme.key_chat_botKeyboardButtonBackgroundPressed)));
                    textView.setPadding(AndroidUtilities.dp(4.0f), z, AndroidUtilities.dp(4.0f), z);
                    textView.setText(Emoji.replaceEmoji(keyboardButton.text, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), z));
                    boolean z2 = i3 != tL_keyboardButtonRow.buttons.size() - 1 ? true : z;
                    View view = textView;
                    linearLayout.addView(view, LayoutHelper.createLinear(0, -1, size, 0, 0, (int) z2, 0));
                    view.setOnClickListener(new C10931());
                    r0.buttonViews.add(view);
                    i3++;
                    z = false;
                }
                i++;
                z = false;
                f = 10.0f;
            }
        }
    }

    public int getKeyboardHeight() {
        return this.isFullSize ? this.panelHeight : ((this.botButtons.rows.size() * AndroidUtilities.dp((float) this.buttonHeight)) + AndroidUtilities.dp(30.0f)) + ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f));
    }
}
