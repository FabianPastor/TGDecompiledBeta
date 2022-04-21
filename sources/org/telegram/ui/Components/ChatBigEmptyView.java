package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
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
    private ArrayList<ImageView> imageViews = new ArrayList<>();
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView statusTextView;
    private ArrayList<TextView> textViews = new ArrayList<>();

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChatBigEmptyView(Context context, View parent, int type, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        int i = type;
        this.resourcesProvider = resourcesProvider2;
        setBackground(Theme.createServiceDrawable(AndroidUtilities.dp(18.0f), this, parent, getThemedPaint("paintChatActionBackground")));
        setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
        setOrientation(1);
        if (i == 0) {
            TextView textView = new TextView(context2);
            this.statusTextView = textView;
            textView.setTextSize(1, 15.0f);
            this.statusTextView.setTextColor(getThemedColor("chat_serviceText"));
            this.statusTextView.setGravity(1);
            this.statusTextView.setMaxWidth(AndroidUtilities.dp(210.0f));
            this.textViews.add(this.statusTextView);
            addView(this.statusTextView, LayoutHelper.createLinear(-2, -2, 49));
        } else if (i == 1) {
            TextView textView2 = new TextView(context2);
            this.statusTextView = textView2;
            textView2.setTextSize(1, 15.0f);
            this.statusTextView.setTextColor(getThemedColor("chat_serviceText"));
            this.statusTextView.setGravity(1);
            this.statusTextView.setMaxWidth(AndroidUtilities.dp(210.0f));
            this.textViews.add(this.statusTextView);
            addView(this.statusTextView, LayoutHelper.createLinear(-2, -2, 49));
        } else {
            ImageView imageView = new ImageView(context2);
            imageView.setImageResource(NUM);
            addView(imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 2, 0, 0));
        }
        TextView textView3 = new TextView(context2);
        if (i == 0) {
            textView3.setText(LocaleController.getString("EncryptedDescriptionTitle", NUM));
            textView3.setTextSize(1, 15.0f);
        } else if (i == 1) {
            textView3.setText(LocaleController.getString("GroupEmptyTitle2", NUM));
            textView3.setTextSize(1, 15.0f);
        } else {
            textView3.setText(LocaleController.getString("ChatYourSelfTitle", NUM));
            textView3.setTextSize(1, 16.0f);
            textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView3.setGravity(1);
        }
        textView3.setTextColor(getThemedColor("chat_serviceText"));
        this.textViews.add(textView3);
        textView3.setMaxWidth(AndroidUtilities.dp(260.0f));
        int i2 = 0;
        addView(textView3, LayoutHelper.createLinear(-2, -2, (i != 2 ? LocaleController.isRTL ? 5 : 3 : 1) | 48, 0, 8, 0, i != 2 ? 0 : 8));
        int a = 0;
        while (a < 4) {
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(i2);
            addView(linearLayout, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
            ImageView imageView2 = new ImageView(context2);
            imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_serviceText"), PorterDuff.Mode.MULTIPLY));
            if (i == 0) {
                imageView2.setImageResource(NUM);
            } else if (i == 2) {
                imageView2.setImageResource(NUM);
            } else {
                imageView2.setImageResource(NUM);
            }
            this.imageViews.add(imageView2);
            TextView textView4 = new TextView(context2);
            textView4.setTextSize(1, 15.0f);
            textView4.setTextColor(getThemedColor("chat_serviceText"));
            this.textViews.add(textView4);
            textView4.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView4.setMaxWidth(AndroidUtilities.dp(260.0f));
            switch (a) {
                case 0:
                    if (i != 0) {
                        if (i != 2) {
                            textView4.setText(LocaleController.getString("GroupDescription1", NUM));
                            break;
                        } else {
                            textView4.setText(LocaleController.getString("ChatYourSelfDescription1", NUM));
                            break;
                        }
                    } else {
                        textView4.setText(LocaleController.getString("EncryptedDescription1", NUM));
                        break;
                    }
                case 1:
                    if (i != 0) {
                        if (i != 2) {
                            textView4.setText(LocaleController.getString("GroupDescription2", NUM));
                            break;
                        } else {
                            textView4.setText(LocaleController.getString("ChatYourSelfDescription2", NUM));
                            break;
                        }
                    } else {
                        textView4.setText(LocaleController.getString("EncryptedDescription2", NUM));
                        break;
                    }
                case 2:
                    if (i != 0) {
                        if (i != 2) {
                            textView4.setText(LocaleController.getString("GroupDescription3", NUM));
                            break;
                        } else {
                            textView4.setText(LocaleController.getString("ChatYourSelfDescription3", NUM));
                            break;
                        }
                    } else {
                        textView4.setText(LocaleController.getString("EncryptedDescription3", NUM));
                        break;
                    }
                case 3:
                    if (i != 0) {
                        if (i != 2) {
                            textView4.setText(LocaleController.getString("GroupDescription4", NUM));
                            break;
                        } else {
                            textView4.setText(LocaleController.getString("ChatYourSelfDescription4", NUM));
                            break;
                        }
                    } else {
                        textView4.setText(LocaleController.getString("EncryptedDescription4", NUM));
                        break;
                    }
            }
            if (LocaleController.isRTL) {
                linearLayout.addView(textView4, LayoutHelper.createLinear(-2, -2));
                if (i == 0) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                } else if (i == 2) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 7.0f, 0.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 8.0f, 3.0f, 0.0f, 0.0f));
                }
            } else {
                if (i == 0) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
                } else if (i == 2) {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 8.0f, 0.0f));
                } else {
                    linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 0.0f, 4.0f, 8.0f, 0.0f));
                }
                linearLayout.addView(textView4, LayoutHelper.createLinear(-2, -2));
            }
            a++;
            i2 = 0;
        }
    }

    public void setTextColor(int color) {
        for (int a = 0; a < this.textViews.size(); a++) {
            this.textViews.get(a).setTextColor(color);
        }
        for (int a2 = 0; a2 < this.imageViews.size(); a2++) {
            this.imageViews.get(a2).setColorFilter(new PorterDuffColorFilter(getThemedColor("chat_serviceText"), PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setStatusText(CharSequence text) {
        this.statusTextView.setText(text);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private Paint getThemedPaint(String paintKey) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Paint paint = resourcesProvider2 != null ? resourcesProvider2.getPaint(paintKey) : null;
        return paint != null ? paint : Theme.getThemePaint(paintKey);
    }
}
