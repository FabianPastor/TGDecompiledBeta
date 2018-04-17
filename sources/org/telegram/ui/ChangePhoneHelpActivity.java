package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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

    /* renamed from: org.telegram.ui.ChangePhoneHelpActivity$2 */
    class C09322 implements OnTouchListener {
        C09322() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangePhoneHelpActivity$3 */
    class C09343 implements OnClickListener {

        /* renamed from: org.telegram.ui.ChangePhoneHelpActivity$3$1 */
        class C09331 implements DialogInterface.OnClickListener {
            C09331() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ChangePhoneHelpActivity.this.presentFragment(new ChangePhoneActivity(), true);
            }
        }

        C09343() {
        }

        public void onClick(View v) {
            if (ChangePhoneHelpActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(ChangePhoneHelpActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("PhoneNumberAlert", R.string.PhoneNumberAlert));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C09331());
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                ChangePhoneHelpActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangePhoneHelpActivity$1 */
    class C19531 extends ActionBarMenuOnItemClick {
        C19531() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChangePhoneHelpActivity.this.finishFragment();
            }
        }
    }

    public View createView(Context context) {
        String value;
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        User user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        if (user == null || user.phone == null || user.phone.length() == 0) {
            value = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
        } else {
            value = PhoneFormat.getInstance();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("+");
            stringBuilder.append(user.phone);
            value = value.format(stringBuilder.toString());
        }
        r1.actionBar.setTitle(value);
        r1.actionBar.setActionBarMenuOnItemClick(new C19531());
        r1.fragmentView = new RelativeLayout(context2);
        r1.fragmentView.setOnTouchListener(new C09322());
        RelativeLayout relativeLayout = r1.fragmentView;
        ScrollView scrollView = new ScrollView(context2);
        relativeLayout.addView(scrollView);
        LayoutParams layoutParams3 = (LayoutParams) scrollView.getLayoutParams();
        layoutParams3.width = -1;
        layoutParams3.height = -2;
        layoutParams3.addRule(15, -1);
        scrollView.setLayoutParams(layoutParams3);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(0, AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f));
        scrollView.addView(linearLayout);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -2;
        linearLayout.setLayoutParams(layoutParams);
        r1.imageView = new ImageView(context2);
        r1.imageView.setImageResource(R.drawable.phone_change);
        r1.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_changephoneinfo_image), Mode.MULTIPLY));
        linearLayout.addView(r1.imageView, LayoutHelper.createLinear(-2, -2, 1));
        r1.textView1 = new TextView(context2);
        r1.textView1.setTextSize(1, 16.0f);
        r1.textView1.setGravity(1);
        r1.textView1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        try {
            r1.textView1.setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", R.string.PhoneNumberHelp)));
        } catch (Throwable e) {
            FileLog.m3e(e);
            r1.textView1.setText(LocaleController.getString("PhoneNumberHelp", R.string.PhoneNumberHelp));
        }
        linearLayout.addView(r1.textView1, LayoutHelper.createLinear(-2, -2, 1, 20, 56, 20, 0));
        r1.textView2 = new TextView(context2);
        r1.textView2.setTextSize(1, 18.0f);
        r1.textView2.setGravity(1);
        r1.textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        r1.textView2.setText(LocaleController.getString("PhoneNumberChange", R.string.PhoneNumberChange));
        r1.textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r1.textView2.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f));
        linearLayout.addView(r1.textView2, LayoutHelper.createLinear(-2, -2, 1, 20, 46, 20, 0));
        r1.textView2.setOnClickListener(new C09343());
        return r1.fragmentView;
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4), new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_changephoneinfo_image)};
    }
}
