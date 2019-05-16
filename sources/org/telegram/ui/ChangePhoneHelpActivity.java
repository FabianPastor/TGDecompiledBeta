package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.LayoutHelper;

public class ChangePhoneHelpActivity extends BaseFragment {
    private ImageView imageView;
    private TextView textView1;
    private TextView textView2;

    public View createView(Context context) {
        CharSequence format;
        RelativeLayout relativeLayout;
        ScrollView scrollView;
        LayoutParams layoutParams;
        LinearLayout linearLayout;
        FrameLayout.LayoutParams layoutParams2;
        Context context2 = context;
        String str = "PhoneNumberHelp";
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        if (currentUser != null) {
            String str2 = currentUser.phone;
            if (!(str2 == null || str2.length() == 0)) {
                PhoneFormat instance = PhoneFormat.getInstance();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(currentUser.phone);
                format = instance.format(stringBuilder.toString());
                this.actionBar.setTitle(format);
                this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
                    public void onItemClick(int i) {
                        if (i == -1) {
                            ChangePhoneHelpActivity.this.finishFragment();
                        }
                    }
                });
                this.fragmentView = new RelativeLayout(context2);
                this.fragmentView.setOnTouchListener(-$$Lambda$ChangePhoneHelpActivity$At2SQoCuyPKWr2c7pksPfUQl31M.INSTANCE);
                relativeLayout = (RelativeLayout) this.fragmentView;
                scrollView = new ScrollView(context2);
                relativeLayout.addView(scrollView);
                layoutParams = (LayoutParams) scrollView.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = -2;
                layoutParams.addRule(15, -1);
                scrollView.setLayoutParams(layoutParams);
                linearLayout = new LinearLayout(context2);
                linearLayout.setOrientation(1);
                linearLayout.setPadding(0, AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f));
                scrollView.addView(linearLayout);
                layoutParams2 = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
                layoutParams2.width = -1;
                layoutParams2.height = -2;
                linearLayout.setLayoutParams(layoutParams2);
                this.imageView = new ImageView(context2);
                this.imageView.setImageResource(NUM);
                this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image"), Mode.MULTIPLY));
                linearLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2, 1));
                this.textView1 = new TextView(context2);
                this.textView1.setTextSize(1, 16.0f);
                this.textView1.setGravity(1);
                this.textView1.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.textView1.setText(AndroidUtilities.replaceTags(LocaleController.getString(str, NUM)));
                linearLayout.addView(this.textView1, LayoutHelper.createLinear(-2, -2, 1, 20, 56, 20, 0));
                this.textView2 = new TextView(context2);
                this.textView2.setTextSize(1, 18.0f);
                this.textView2.setGravity(1);
                this.textView2.setTextColor(Theme.getColor("key_changephoneinfo_changeText"));
                this.textView2.setText(LocaleController.getString("PhoneNumberChange", NUM));
                this.textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.textView2.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f));
                linearLayout.addView(this.textView2, LayoutHelper.createLinear(-2, -2, 1, 20, 46, 20, 0));
                this.textView2.setOnClickListener(new -$$Lambda$ChangePhoneHelpActivity$qVZMrZGI0M_ckM3yEp535Q8q-vk(this));
                return this.fragmentView;
            }
        }
        format = LocaleController.getString("NumberUnknown", NUM);
        this.actionBar.setTitle(format);
        this.actionBar.setActionBarMenuOnItemClick(/* anonymous class already generated */);
        this.fragmentView = new RelativeLayout(context2);
        this.fragmentView.setOnTouchListener(-$$Lambda$ChangePhoneHelpActivity$At2SQoCuyPKWr2c7pksPfUQl31M.INSTANCE);
        relativeLayout = (RelativeLayout) this.fragmentView;
        scrollView = new ScrollView(context2);
        relativeLayout.addView(scrollView);
        layoutParams = (LayoutParams) scrollView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -2;
        layoutParams.addRule(15, -1);
        scrollView.setLayoutParams(layoutParams);
        linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(0, AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f));
        scrollView.addView(linearLayout);
        layoutParams2 = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -2;
        linearLayout.setLayoutParams(layoutParams2);
        this.imageView = new ImageView(context2);
        this.imageView.setImageResource(NUM);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image"), Mode.MULTIPLY));
        linearLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2, 1));
        this.textView1 = new TextView(context2);
        this.textView1.setTextSize(1, 16.0f);
        this.textView1.setGravity(1);
        this.textView1.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        try {
            this.textView1.setText(AndroidUtilities.replaceTags(LocaleController.getString(str, NUM)));
        } catch (Exception e) {
            FileLog.e(e);
            this.textView1.setText(LocaleController.getString(str, NUM));
        }
        linearLayout.addView(this.textView1, LayoutHelper.createLinear(-2, -2, 1, 20, 56, 20, 0));
        this.textView2 = new TextView(context2);
        this.textView2.setTextSize(1, 18.0f);
        this.textView2.setGravity(1);
        this.textView2.setTextColor(Theme.getColor("key_changephoneinfo_changeText"));
        this.textView2.setText(LocaleController.getString("PhoneNumberChange", NUM));
        this.textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView2.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f));
        linearLayout.addView(this.textView2, LayoutHelper.createLinear(-2, -2, 1, 20, 46, 20, 0));
        this.textView2.setOnClickListener(new -$$Lambda$ChangePhoneHelpActivity$qVZMrZGI0M_ckM3yEp535Q8q-vk(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$ChangePhoneHelpActivity(View view) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("PhoneNumberAlert", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$ChangePhoneHelpActivity$qA8BPNV0fO4W8yi1slKpuIUhZp4(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$null$1$ChangePhoneHelpActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new ChangePhoneActivity(), true);
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "key_changephoneinfo_changeText"), new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "changephoneinfo_image")};
    }
}
