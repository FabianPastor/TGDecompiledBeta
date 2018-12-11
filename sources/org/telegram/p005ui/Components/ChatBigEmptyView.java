package org.telegram.p005ui.Components;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.LocaleController;
import org.telegram.p005ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.Components.ChatBigEmptyView */
public class ChatBigEmptyView extends LinearLayout {
    private ArrayList<ImageView> imageViews = new ArrayList();
    private TextView secretViewStatusTextView;
    private ArrayList<TextView> textViews = new ArrayList();

    public ChatBigEmptyView(Context context, boolean secretChat) {
        ImageView imageView;
        super(context);
        setBackgroundResource(CLASSNAMER.drawable.system);
        getBackground().setColorFilter(Theme.colorFilter);
        setPadding(AndroidUtilities.m10dp(16.0f), AndroidUtilities.m10dp(12.0f), AndroidUtilities.m10dp(16.0f), AndroidUtilities.m10dp(12.0f));
        setOrientation(1);
        if (secretChat) {
            this.secretViewStatusTextView = new TextView(context);
            this.secretViewStatusTextView.setTextSize(1, 15.0f);
            this.secretViewStatusTextView.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
            this.secretViewStatusTextView.setGravity(1);
            this.secretViewStatusTextView.setMaxWidth(AndroidUtilities.m10dp(210.0f));
            this.textViews.add(this.secretViewStatusTextView);
            addView(this.secretViewStatusTextView, LayoutHelper.createLinear(-2, -2, 49));
        } else {
            imageView = new ImageView(context);
            imageView.setImageResource(CLASSNAMER.drawable.cloud_big);
            addView(imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 2, 0, 0));
        }
        TextView textView = new TextView(context);
        if (secretChat) {
            textView.setText(LocaleController.getString("EncryptedDescriptionTitle", CLASSNAMER.string.EncryptedDescriptionTitle));
            textView.setTextSize(1, 15.0f);
        } else {
            textView.setText(LocaleController.getString("ChatYourSelfTitle", CLASSNAMER.string.ChatYourSelfTitle));
            textView.setTextSize(1, 16.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setGravity(1);
        }
        textView.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
        this.textViews.add(textView);
        textView.setMaxWidth(AndroidUtilities.m10dp(260.0f));
        int i = secretChat ? LocaleController.isRTL ? 5 : 3 : 1;
        addView(textView, LayoutHelper.createLinear(-2, -2, i | 48, 0, 8, 0, secretChat ? 0 : 8));
        for (int a = 0; a < 4; a++) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
            imageView = new ImageView(context);
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_serviceText), Mode.MULTIPLY));
            imageView.setImageResource(secretChat ? CLASSNAMER.drawable.ic_lock_white : CLASSNAMER.drawable.list_circle);
            this.imageViews.add(imageView);
            textView = new TextView(context);
            textView.setTextSize(1, 15.0f);
            textView.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
            this.textViews.add(textView);
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView.setMaxWidth(AndroidUtilities.m10dp(260.0f));
            switch (a) {
                case 0:
                    if (!secretChat) {
                        textView.setText(LocaleController.getString("ChatYourSelfDescription1", CLASSNAMER.string.ChatYourSelfDescription1));
                        break;
                    } else {
                        textView.setText(LocaleController.getString("EncryptedDescription1", CLASSNAMER.string.EncryptedDescription1));
                        break;
                    }
                case 1:
                    if (!secretChat) {
                        textView.setText(LocaleController.getString("ChatYourSelfDescription2", CLASSNAMER.string.ChatYourSelfDescription2));
                        break;
                    } else {
                        textView.setText(LocaleController.getString("EncryptedDescription2", CLASSNAMER.string.EncryptedDescription2));
                        break;
                    }
                case 2:
                    if (!secretChat) {
                        textView.setText(LocaleController.getString("ChatYourSelfDescription3", CLASSNAMER.string.ChatYourSelfDescription3));
                        break;
                    } else {
                        textView.setText(LocaleController.getString("EncryptedDescription3", CLASSNAMER.string.EncryptedDescription3));
                        break;
                    }
                case 3:
                    if (!secretChat) {
                        textView.setText(LocaleController.getString("ChatYourSelfDescription4", CLASSNAMER.string.ChatYourSelfDescription4));
                        break;
                    } else {
                        textView.setText(LocaleController.getString("EncryptedDescription4", CLASSNAMER.string.EncryptedDescription4));
                        break;
                    }
            }
            if (LocaleController.isRTL) {
                linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
                if (secretChat) {
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 8.0f, 7.0f, 0.0f, 0.0f));
                }
            } else {
                if (secretChat) {
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 8.0f, 0.0f));
                }
                linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
            }
        }
    }

    public void setTextColor(int color) {
        int a;
        for (a = 0; a < this.textViews.size(); a++) {
            ((TextView) this.textViews.get(a)).setTextColor(color);
        }
        for (a = 0; a < this.imageViews.size(); a++) {
            ((ImageView) this.imageViews.get(a)).setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_serviceText), Mode.MULTIPLY));
        }
    }

    public void setSecretText(String text) {
        this.secretViewStatusTextView.setText(text);
    }
}
