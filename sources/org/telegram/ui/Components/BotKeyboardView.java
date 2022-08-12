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

public class BotKeyboardView extends LinearLayout {
    private TLRPC$TL_replyKeyboardMarkup botButtons;
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
        void didPressedButton(TLRPC$KeyboardButton tLRPC$KeyboardButton);
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
            this.buttonIcons.get(i).invalidate();
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
        this.buttonIcons.clear();
        boolean z = false;
        this.scrollView.scrollTo(0, 0);
        if (tLRPC$TL_replyKeyboardMarkup2 != null && this.botButtons.rows.size() != 0) {
            int i2 = 1;
            boolean z2 = !tLRPC$TL_replyKeyboardMarkup2.resize;
            this.isFullSize = z2;
            if (!z2) {
                i = 42;
            } else {
                i = (int) Math.max(42.0f, ((float) (((this.panelHeight - AndroidUtilities.dp(30.0f)) - ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f))) / this.botButtons.rows.size())) / AndroidUtilities.density);
            }
            this.buttonHeight = i;
            int i3 = 0;
            while (i3 < tLRPC$TL_replyKeyboardMarkup2.rows.size()) {
                TLRPC$TL_keyboardButtonRow tLRPC$TL_keyboardButtonRow = tLRPC$TL_replyKeyboardMarkup2.rows.get(i3);
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(z ? 1 : 0);
                this.container.addView(linearLayout, LayoutHelper.createLinear(-1, this.buttonHeight, 15.0f, i3 == 0 ? 15.0f : 10.0f, 15.0f, i3 == tLRPC$TL_replyKeyboardMarkup2.rows.size() - i2 ? 15.0f : 0.0f));
                float size = 1.0f / ((float) tLRPC$TL_keyboardButtonRow.buttons.size());
                int i4 = 0;
                while (i4 < tLRPC$TL_keyboardButtonRow.buttons.size()) {
                    TLRPC$KeyboardButton tLRPC$KeyboardButton = tLRPC$TL_keyboardButtonRow.buttons.get(i4);
                    TextView textView = new TextView(getContext());
                    textView.setTag(tLRPC$KeyboardButton);
                    textView.setTextColor(getThemedColor("chat_botKeyboardButtonText"));
                    textView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), getThemedColor("chat_botKeyboardButtonBackground"), getThemedColor("chat_botKeyboardButtonBackgroundPressed")));
                    textView.setTextSize(i2, 16.0f);
                    textView.setGravity(17);
                    FrameLayout frameLayout = new FrameLayout(getContext());
                    frameLayout.addView(textView, LayoutHelper.createFrame(-1, -1.0f));
                    textView.setPadding(AndroidUtilities.dp(4.0f), z ? 1 : 0, AndroidUtilities.dp(4.0f), z);
                    textView.setText(Emoji.replaceEmoji(tLRPC$KeyboardButton.text, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), z));
                    TextView textView2 = textView;
                    TLRPC$KeyboardButton tLRPC$KeyboardButton2 = tLRPC$KeyboardButton;
                    FrameLayout frameLayout2 = frameLayout;
                    linearLayout.addView(frameLayout2, LayoutHelper.createLinear(0, -1, size, 0, 0, i4 != tLRPC$TL_keyboardButtonRow.buttons.size() + -1 ? 10 : 0, 0));
                    textView2.setOnClickListener(new BotKeyboardView$$ExternalSyntheticLambda0(this));
                    this.buttonViews.add(textView2);
                    ImageView imageView = new ImageView(getContext());
                    imageView.setColorFilter(getThemedColor("chat_botKeyboardButtonText"));
                    if ((tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonWebView) || (tLRPC$KeyboardButton2 instanceof TLRPC$TL_keyboardButtonSimpleWebView)) {
                        imageView.setImageResource(R.drawable.bot_webview);
                        imageView.setVisibility(0);
                    } else {
                        imageView.setVisibility(8);
                    }
                    this.buttonIcons.add(imageView);
                    frameLayout2.addView(imageView, LayoutHelper.createFrame(12, 12.0f, 53, 0.0f, 8.0f, 8.0f, 0.0f));
                    i4++;
                    z = false;
                    i2 = 1;
                }
                i3++;
                z = false;
                i2 = 1;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setButtons$0(View view) {
        this.delegate.didPressedButton((TLRPC$KeyboardButton) view.getTag());
    }

    public int getKeyboardHeight() {
        TLRPC$TL_replyKeyboardMarkup tLRPC$TL_replyKeyboardMarkup = this.botButtons;
        if (tLRPC$TL_replyKeyboardMarkup == null) {
            return 0;
        }
        return this.isFullSize ? this.panelHeight : (tLRPC$TL_replyKeyboardMarkup.rows.size() * AndroidUtilities.dp((float) this.buttonHeight)) + AndroidUtilities.dp(30.0f) + ((this.botButtons.rows.size() - 1) * AndroidUtilities.dp(10.0f));
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
