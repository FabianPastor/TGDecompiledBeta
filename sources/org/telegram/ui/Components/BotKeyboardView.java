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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class BotKeyboardView extends LinearLayout {
    private TLRPC.TL_replyKeyboardMarkup botButtons;
    private int buttonHeight;
    private ArrayList<ImageView> buttonIcons = new ArrayList<>();
    private ArrayList<TextView> buttonViews = new ArrayList<>();
    private LinearLayout container;
    private BotKeyboardViewDelegate delegate;
    private boolean isFullSize;
    private int panelHeight;
    private final Theme.ResourcesProvider resourcesProvider;
    private ScrollView scrollView;

    public interface BotKeyboardViewDelegate {
        void didPressedButton(TLRPC.KeyboardButton keyboardButton);
    }

    public BotKeyboardView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        setOrientation(1);
        ScrollView scrollView2 = new ScrollView(context);
        this.scrollView = scrollView2;
        addView(scrollView2);
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

    public void setPanelHeight(int height) {
        TLRPC.TL_replyKeyboardMarkup tL_replyKeyboardMarkup;
        this.panelHeight = height;
        if (this.isFullSize && (tL_replyKeyboardMarkup = this.botButtons) != null && tL_replyKeyboardMarkup.rows.size() != 0) {
            this.buttonHeight = !this.isFullSize ? 42 : (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            int count = this.container.getChildCount();
            int newHeight = AndroidUtilities.dp((float) this.buttonHeight);
            for (int a = 0; a < count; a++) {
                View v = this.container.getChildAt(a);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();
                if (layoutParams.height != newHeight) {
                    layoutParams.height = newHeight;
                    v.setLayoutParams(layoutParams);
                }
            }
        }
    }

    public void invalidateViews() {
        for (int a = 0; a < this.buttonViews.size(); a++) {
            this.buttonViews.get(a).invalidate();
            this.buttonIcons.get(a).invalidate();
        }
    }

    public boolean isFullSize() {
        return this.isFullSize;
    }

    public void setButtons(TLRPC.TL_replyKeyboardMarkup buttons) {
        TLRPC.TL_replyKeyboardMarkup tL_replyKeyboardMarkup = buttons;
        this.botButtons = tL_replyKeyboardMarkup;
        this.container.removeAllViews();
        this.buttonViews.clear();
        this.buttonIcons.clear();
        boolean z = false;
        this.scrollView.scrollTo(0, 0);
        if (tL_replyKeyboardMarkup != null && this.botButtons.rows.size() != 0) {
            int i = 1;
            boolean z2 = !tL_replyKeyboardMarkup.resize;
            this.isFullSize = z2;
            this.buttonHeight = !z2 ? 42 : (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            int a = 0;
            while (a < tL_replyKeyboardMarkup.rows.size()) {
                TLRPC.TL_keyboardButtonRow row = (TLRPC.TL_keyboardButtonRow) tL_replyKeyboardMarkup.rows.get(a);
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(z ? 1 : 0);
                this.container.addView(layout, LayoutHelper.createLinear(-1, this.buttonHeight, 15.0f, a == 0 ? 15.0f : 10.0f, 15.0f, a == tL_replyKeyboardMarkup.rows.size() - i ? 15.0f : 0.0f));
                float weight = 1.0f / ((float) row.buttons.size());
                int b = 0;
                while (b < row.buttons.size()) {
                    TLRPC.KeyboardButton button = row.buttons.get(b);
                    TextView textView = new TextView(getContext());
                    textView.setTag(button);
                    textView.setTextColor(getThemedColor("chat_botKeyboardButtonText"));
                    textView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), getThemedColor("chat_botKeyboardButtonBackground"), getThemedColor("chat_botKeyboardButtonBackgroundPressed")));
                    textView.setTextSize(i, 16.0f);
                    textView.setGravity(17);
                    FrameLayout frame = new FrameLayout(getContext());
                    frame.addView(textView, LayoutHelper.createFrame(-1, -1.0f));
                    textView.setPadding(AndroidUtilities.dp(4.0f), z ? 1 : 0, AndroidUtilities.dp(4.0f), z);
                    textView.setText(Emoji.replaceEmoji(button.text, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), z));
                    TextView textView2 = textView;
                    TLRPC.KeyboardButton button2 = button;
                    FrameLayout frame2 = frame;
                    layout.addView(frame2, LayoutHelper.createLinear(0, -1, weight, 0, 0, b != row.buttons.size() + -1 ? 10 : 0, 0));
                    textView2.setOnClickListener(new BotKeyboardView$$ExternalSyntheticLambda0(this));
                    this.buttonViews.add(textView2);
                    ImageView icon = new ImageView(getContext());
                    icon.setColorFilter(getThemedColor("chat_botKeyboardButtonText"));
                    if ((button2 instanceof TLRPC.TL_keyboardButtonWebView) || (button2 instanceof TLRPC.TL_keyboardButtonSimpleWebView)) {
                        icon.setImageResource(NUM);
                        icon.setVisibility(0);
                    } else {
                        icon.setVisibility(8);
                    }
                    this.buttonIcons.add(icon);
                    frame2.addView(icon, LayoutHelper.createFrame(12, 12.0f, 53, 0.0f, 8.0f, 8.0f, 0.0f));
                    b++;
                    z = false;
                    i = 1;
                }
                a++;
                z = false;
                i = 1;
            }
        }
    }

    /* renamed from: lambda$setButtons$0$org-telegram-ui-Components-BotKeyboardView  reason: not valid java name */
    public /* synthetic */ void m576lambda$setButtons$0$orgtelegramuiComponentsBotKeyboardView(View v) {
        this.delegate.didPressedButton((TLRPC.KeyboardButton) v.getTag());
    }

    public int getKeyboardHeight() {
        TLRPC.TL_replyKeyboardMarkup tL_replyKeyboardMarkup = this.botButtons;
        if (tL_replyKeyboardMarkup == null) {
            return 0;
        }
        return this.isFullSize ? this.panelHeight : (tL_replyKeyboardMarkup.rows.size() * AndroidUtilities.dp((float) this.buttonHeight)) + AndroidUtilities.dp(30.0f) + ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f));
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
