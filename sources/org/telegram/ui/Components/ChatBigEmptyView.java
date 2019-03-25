package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class ChatBigEmptyView extends LinearLayout {
    public static final int EMPTY_VIEW_TYPE_GROUP = 1;
    public static final int EMPTY_VIEW_TYPE_SAVED = 2;
    public static final int EMPTY_VIEW_TYPE_SECRET = 0;
    private ArrayList<ImageView> imageViews = new ArrayList();
    private TextView statusTextView;
    private ArrayList<TextView> textViews = new ArrayList();

    public ChatBigEmptyView(Context context, int type) {
        ImageView imageView;
        super(context);
        setBackgroundResource(NUM);
        getBackground().setColorFilter(Theme.colorFilter);
        setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
        setOrientation(1);
        if (type == 0) {
            this.statusTextView = new TextView(context);
            this.statusTextView.setTextSize(1, 15.0f);
            this.statusTextView.setTextColor(Theme.getColor("chat_serviceText"));
            this.statusTextView.setGravity(1);
            this.statusTextView.setMaxWidth(AndroidUtilities.dp(210.0f));
            this.textViews.add(this.statusTextView);
            addView(this.statusTextView, LayoutHelper.createLinear(-2, -2, 49));
        } else if (type == 1) {
            this.statusTextView = new TextView(context);
            this.statusTextView.setTextSize(1, 15.0f);
            this.statusTextView.setTextColor(Theme.getColor("chat_serviceText"));
            this.statusTextView.setGravity(1);
            this.statusTextView.setMaxWidth(AndroidUtilities.dp(210.0f));
            this.textViews.add(this.statusTextView);
            addView(this.statusTextView, LayoutHelper.createLinear(-2, -2, 49));
        } else {
            imageView = new ImageView(context);
            imageView.setImageResource(NUM);
            addView(imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 2, 0, 0));
        }
        TextView textView = new TextView(context);
        if (type == 0) {
            textView.setText(LocaleController.getString("EncryptedDescriptionTitle", NUM));
            textView.setTextSize(1, 15.0f);
        } else if (type == 1) {
            textView.setText(LocaleController.getString("GroupEmptyTitle2", NUM));
            textView.setTextSize(1, 15.0f);
        } else {
            textView.setText(LocaleController.getString("ChatYourSelfTitle", NUM));
            textView.setTextSize(1, 16.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setGravity(1);
        }
        textView.setTextColor(Theme.getColor("chat_serviceText"));
        this.textViews.add(textView);
        textView.setMaxWidth(AndroidUtilities.dp(260.0f));
        int i = type != 2 ? LocaleController.isRTL ? 5 : 3 : 1;
        addView(textView, LayoutHelper.createLinear(-2, -2, i | 48, 0, 8, 0, type != 2 ? 0 : 8));
        for (int a = 0; a < 4; a++) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
            imageView = new ImageView(context);
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_serviceText"), Mode.MULTIPLY));
            if (type == 0) {
                imageView.setImageResource(NUM);
            } else if (type == 2) {
                imageView.setImageResource(NUM);
            } else {
                imageView.setImageResource(NUM);
            }
            this.imageViews.add(imageView);
            textView = new TextView(context);
            textView.setTextSize(1, 15.0f);
            textView.setTextColor(Theme.getColor("chat_serviceText"));
            this.textViews.add(textView);
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView.setMaxWidth(AndroidUtilities.dp(260.0f));
            switch (a) {
                case 0:
                    if (type != 0) {
                        if (type != 2) {
                            textView.setText(LocaleController.getString("GroupDescription1", NUM));
                            break;
                        } else {
                            textView.setText(LocaleController.getString("ChatYourSelfDescription1", NUM));
                            break;
                        }
                    }
                    textView.setText(LocaleController.getString("EncryptedDescription1", NUM));
                    break;
                case 1:
                    if (type != 0) {
                        if (type != 2) {
                            textView.setText(LocaleController.getString("GroupDescription2", NUM));
                            break;
                        } else {
                            textView.setText(LocaleController.getString("ChatYourSelfDescription2", NUM));
                            break;
                        }
                    }
                    textView.setText(LocaleController.getString("EncryptedDescription2", NUM));
                    break;
                case 2:
                    if (type != 0) {
                        if (type != 2) {
                            textView.setText(LocaleController.getString("GroupDescription3", NUM));
                            break;
                        } else {
                            textView.setText(LocaleController.getString("ChatYourSelfDescription3", NUM));
                            break;
                        }
                    }
                    textView.setText(LocaleController.getString("EncryptedDescription3", NUM));
                    break;
                case 3:
                    if (type != 0) {
                        if (type != 2) {
                            textView.setText(LocaleController.getString("GroupDescription4", NUM));
                            break;
                        } else {
                            textView.setText(LocaleController.getString("ChatYourSelfDescription4", NUM));
                            break;
                        }
                    }
                    textView.setText(LocaleController.getString("EncryptedDescription4", NUM));
                    break;
            }
            if (LocaleController.isRTL) {
                linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
                if (type == 0) {
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                } else if (type == 2) {
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 8.0f, 7.0f, 0.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                }
            } else {
                if (type == 0) {
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
                } else if (type == 2) {
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 8.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
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
            ((ImageView) this.imageViews.get(a)).setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_serviceText"), Mode.MULTIPLY));
        }
    }

    public void setStatusText(CharSequence text) {
        this.statusTextView.setText(text);
    }
}
