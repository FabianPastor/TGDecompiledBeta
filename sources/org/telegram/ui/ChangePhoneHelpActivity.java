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
import org.telegram.messenger.beta.R;
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
        String value;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        User user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        if (user == null || user.phone == null || user.phone.length() == 0) {
            value = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
        } else {
            value = PhoneFormat.getInstance().format("+" + user.phone);
        }
        this.actionBar.setTitle(value);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChangePhoneHelpActivity.this.lambda$createView$1$AudioSelectActivity();
                }
            }
        });
        this.fragmentView = new RelativeLayout(context);
        this.fragmentView.setOnTouchListener(ChangePhoneHelpActivity$$Lambda$0.$instance);
        RelativeLayout relativeLayout = this.fragmentView;
        ScrollView scrollView = new ScrollView(context);
        relativeLayout.addView(scrollView);
        LayoutParams layoutParams3 = (LayoutParams) scrollView.getLayoutParams();
        layoutParams3.width = -1;
        layoutParams3.height = -2;
        layoutParams3.addRule(15, -1);
        scrollView.setLayoutParams(layoutParams3);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(0, AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f));
        scrollView.addView(linearLayout);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -2;
        linearLayout.setLayoutParams(layoutParams);
        this.imageView = new ImageView(context);
        this.imageView.setImageResource(R.drawable.phone_change);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image"), Mode.MULTIPLY));
        linearLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2, 1));
        this.textView1 = new TextView(context);
        this.textView1.setTextSize(1, 16.0f);
        this.textView1.setGravity(1);
        this.textView1.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        try {
            this.textView1.setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", R.string.PhoneNumberHelp)));
        } catch (Throwable e) {
            FileLog.e(e);
            this.textView1.setText(LocaleController.getString("PhoneNumberHelp", R.string.PhoneNumberHelp));
        }
        linearLayout.addView(this.textView1, LayoutHelper.createLinear(-2, -2, 1, 20, 56, 20, 0));
        this.textView2 = new TextView(context);
        this.textView2.setTextSize(1, 18.0f);
        this.textView2.setGravity(1);
        this.textView2.setTextColor(Theme.getColor("key_changephoneinfo_changeText"));
        this.textView2.setText(LocaleController.getString("PhoneNumberChange", R.string.PhoneNumberChange));
        this.textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView2.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f));
        linearLayout.addView(this.textView2, LayoutHelper.createLinear(-2, -2, 1, 20, 46, 20, 0));
        this.textView2.setOnClickListener(new ChangePhoneHelpActivity$$Lambda$1(this));
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$2$ChangePhoneHelpActivity(View v) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("PhoneNumberAlert", R.string.PhoneNumberAlert));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChangePhoneHelpActivity$$Lambda$2(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$null$1$ChangePhoneHelpActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new ChangePhoneActivity(), true);
    }

    public ThemeDescription[] getThemeDescriptions() {
        r8 = new ThemeDescription[8];
        r8[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        r8[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r8[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r8[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r8[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r8[5] = new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r8[6] = new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "key_changephoneinfo_changeText");
        r8[7] = new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "changephoneinfo_image");
        return r8;
    }
}
