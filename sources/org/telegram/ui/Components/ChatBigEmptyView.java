package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class ChatBigEmptyView extends LinearLayout {
    private ArrayList<ImageView> imageViews = new ArrayList();
    private TextView secretViewStatusTextView;
    private ArrayList<TextView> textViews = new ArrayList();

    public ChatBigEmptyView(Context context, boolean z) {
        View imageView;
        Context context2 = context;
        super(context);
        setBackgroundResource(C0446R.drawable.system);
        getBackground().setColorFilter(Theme.colorFilter);
        setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
        setOrientation(1);
        if (z) {
            r0.secretViewStatusTextView = new TextView(context2);
            r0.secretViewStatusTextView.setTextSize(1, 15.0f);
            r0.secretViewStatusTextView.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
            r0.secretViewStatusTextView.setGravity(1);
            r0.secretViewStatusTextView.setMaxWidth(AndroidUtilities.dp(210.0f));
            r0.textViews.add(r0.secretViewStatusTextView);
            addView(r0.secretViewStatusTextView, LayoutHelper.createLinear(-2, -2, 49));
        } else {
            imageView = new ImageView(context2);
            imageView.setImageResource(C0446R.drawable.cloud_big);
            addView(imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 2, 0, 0));
        }
        imageView = new TextView(context2);
        if (z) {
            imageView.setText(LocaleController.getString("EncryptedDescriptionTitle", C0446R.string.EncryptedDescriptionTitle));
            imageView.setTextSize(1, 15.0f);
        } else {
            imageView.setText(LocaleController.getString("ChatYourSelfTitle", C0446R.string.ChatYourSelfTitle));
            imageView.setTextSize(1, 16.0f);
            imageView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            imageView.setGravity(1);
        }
        imageView.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
        r0.textViews.add(imageView);
        imageView.setMaxWidth(AndroidUtilities.dp(260.0f));
        int i = z ? LocaleController.isRTL ? 5 : 3 : 1;
        int i2 = 0;
        addView(imageView, LayoutHelper.createLinear(-2, -2, i | 48, 0, 8, 0, z ? 0 : 8));
        for (int i3 = i2; i3 < 4; i3++) {
            View linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(i2);
            addView(linearLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
            View imageView2 = new ImageView(context2);
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_serviceText), Mode.MULTIPLY));
            imageView2.setImageResource(z ? C0446R.drawable.ic_lock_white : C0446R.drawable.list_circle);
            r0.imageViews.add(imageView2);
            View textView = new TextView(context2);
            textView.setTextSize(1, 15.0f);
            textView.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
            r0.textViews.add(textView);
            textView.setGravity(16 | (LocaleController.isRTL ? 5 : 3));
            textView.setMaxWidth(AndroidUtilities.dp(260.0f));
            switch (i3) {
                case 0:
                    if (!z) {
                        textView.setText(LocaleController.getString("ChatYourSelfDescription1", C0446R.string.ChatYourSelfDescription1));
                        break;
                    } else {
                        textView.setText(LocaleController.getString("EncryptedDescription1", C0446R.string.EncryptedDescription1));
                        break;
                    }
                case 1:
                    if (!z) {
                        textView.setText(LocaleController.getString("ChatYourSelfDescription2", C0446R.string.ChatYourSelfDescription2));
                        break;
                    } else {
                        textView.setText(LocaleController.getString("EncryptedDescription2", C0446R.string.EncryptedDescription2));
                        break;
                    }
                case 2:
                    if (!z) {
                        textView.setText(LocaleController.getString("ChatYourSelfDescription3", C0446R.string.ChatYourSelfDescription3));
                        break;
                    } else {
                        textView.setText(LocaleController.getString("EncryptedDescription3", C0446R.string.EncryptedDescription3));
                        break;
                    }
                case 3:
                    if (!z) {
                        textView.setText(LocaleController.getString("ChatYourSelfDescription4", C0446R.string.ChatYourSelfDescription4));
                        break;
                    } else {
                        textView.setText(LocaleController.getString("EncryptedDescription4", C0446R.string.EncryptedDescription4));
                        break;
                    }
                default:
                    break;
            }
            if (LocaleController.isRTL) {
                linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
                if (z) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 7.0f, 0.0f, 0.0f));
                }
            } else {
                if (z) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 8.0f, 0.0f));
                }
                linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
            }
        }
    }

    public void setTextColor(int i) {
        int i2 = 0;
        for (int i3 = 0; i3 < this.textViews.size(); i3++) {
            ((TextView) this.textViews.get(i3)).setTextColor(i);
        }
        while (i2 < this.imageViews.size()) {
            ((ImageView) this.imageViews.get(i2)).setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_serviceText), Mode.MULTIPLY));
            i2++;
        }
    }

    public void setSecretText(String str) {
        this.secretViewStatusTextView.setText(str);
    }
}
