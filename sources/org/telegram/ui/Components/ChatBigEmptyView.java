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

    public ChatBigEmptyView(Context context, int i) {
        Context context2 = context;
        int i2 = i;
        super(context);
        setBackgroundResource(NUM);
        getBackground().setColorFilter(Theme.colorFilter);
        setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
        setOrientation(1);
        String str = "chat_serviceText";
        if (i2 == 0) {
            this.statusTextView = new TextView(context2);
            this.statusTextView.setTextSize(1, 15.0f);
            this.statusTextView.setTextColor(Theme.getColor(str));
            this.statusTextView.setGravity(1);
            this.statusTextView.setMaxWidth(AndroidUtilities.dp(210.0f));
            this.textViews.add(this.statusTextView);
            addView(this.statusTextView, LayoutHelper.createLinear(-2, -2, 49));
        } else if (i2 == 1) {
            this.statusTextView = new TextView(context2);
            this.statusTextView.setTextSize(1, 15.0f);
            this.statusTextView.setTextColor(Theme.getColor(str));
            this.statusTextView.setGravity(1);
            this.statusTextView.setMaxWidth(AndroidUtilities.dp(210.0f));
            this.textViews.add(this.statusTextView);
            addView(this.statusTextView, LayoutHelper.createLinear(-2, -2, 49));
        } else {
            ImageView imageView = new ImageView(context2);
            imageView.setImageResource(NUM);
            addView(imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 2, 0, 0));
        }
        TextView textView = new TextView(context2);
        if (i2 == 0) {
            textView.setText(LocaleController.getString("EncryptedDescriptionTitle", NUM));
            textView.setTextSize(1, 15.0f);
        } else if (i2 == 1) {
            textView.setText(LocaleController.getString("GroupEmptyTitle2", NUM));
            textView.setTextSize(1, 15.0f);
        } else {
            textView.setText(LocaleController.getString("ChatYourSelfTitle", NUM));
            textView.setTextSize(1, 16.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setGravity(1);
        }
        textView.setTextColor(Theme.getColor(str));
        this.textViews.add(textView);
        textView.setMaxWidth(AndroidUtilities.dp(260.0f));
        int i3 = i2 != 2 ? LocaleController.isRTL ? 5 : 3 : 1;
        addView(textView, LayoutHelper.createLinear(-2, -2, i3 | 48, 0, 8, 0, i2 != 2 ? 0 : 8));
        for (int i4 = 0; i4 < 4; i4++) {
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
            ImageView imageView2 = new ImageView(context2);
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            if (i2 == 0) {
                imageView2.setImageResource(NUM);
            } else if (i2 == 2) {
                imageView2.setImageResource(NUM);
            } else {
                imageView2.setImageResource(NUM);
            }
            this.imageViews.add(imageView2);
            TextView textView2 = new TextView(context2);
            textView2.setTextSize(1, 15.0f);
            textView2.setTextColor(Theme.getColor(str));
            this.textViews.add(textView2);
            textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView2.setMaxWidth(AndroidUtilities.dp(260.0f));
            if (i4 != 0) {
                if (i4 != 1) {
                    if (i4 != 2) {
                        if (i4 == 3) {
                            if (i2 == 0) {
                                textView2.setText(LocaleController.getString("EncryptedDescription4", NUM));
                            } else if (i2 == 2) {
                                textView2.setText(LocaleController.getString("ChatYourSelfDescription4", NUM));
                            } else {
                                textView2.setText(LocaleController.getString("GroupDescription4", NUM));
                            }
                        }
                    } else if (i2 == 0) {
                        textView2.setText(LocaleController.getString("EncryptedDescription3", NUM));
                    } else if (i2 == 2) {
                        textView2.setText(LocaleController.getString("ChatYourSelfDescription3", NUM));
                    } else {
                        textView2.setText(LocaleController.getString("GroupDescription3", NUM));
                    }
                } else if (i2 == 0) {
                    textView2.setText(LocaleController.getString("EncryptedDescription2", NUM));
                } else if (i2 == 2) {
                    textView2.setText(LocaleController.getString("ChatYourSelfDescription2", NUM));
                } else {
                    textView2.setText(LocaleController.getString("GroupDescription2", NUM));
                }
            } else if (i2 == 0) {
                textView2.setText(LocaleController.getString("EncryptedDescription1", NUM));
            } else if (i2 == 2) {
                textView2.setText(LocaleController.getString("ChatYourSelfDescription1", NUM));
            } else {
                textView2.setText(LocaleController.getString("GroupDescription1", NUM));
            }
            if (LocaleController.isRTL) {
                linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2));
                if (i2 == 0) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                } else if (i2 == 2) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 7.0f, 0.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                }
            } else {
                if (i2 == 0) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
                } else if (i2 == 2) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 8.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
                }
                linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2));
            }
        }
    }

    public void setTextColor(int i) {
        for (int i2 = 0; i2 < this.textViews.size(); i2++) {
            ((TextView) this.textViews.get(i2)).setTextColor(i);
        }
        for (int i3 = 0; i3 < this.imageViews.size(); i3++) {
            ((ImageView) this.imageViews.get(i3)).setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_serviceText"), Mode.MULTIPLY));
        }
    }

    public void setStatusText(CharSequence charSequence) {
        this.statusTextView.setText(charSequence);
    }
}
