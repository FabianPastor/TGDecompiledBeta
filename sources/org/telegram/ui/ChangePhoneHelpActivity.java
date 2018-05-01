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
import org.telegram.messenger.C0446R;
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

    /* renamed from: org.telegram.ui.ChangePhoneHelpActivity$2 */
    class C09382 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C09382() {
        }
    }

    /* renamed from: org.telegram.ui.ChangePhoneHelpActivity$3 */
    class C09403 implements OnClickListener {

        /* renamed from: org.telegram.ui.ChangePhoneHelpActivity$3$1 */
        class C09391 implements DialogInterface.OnClickListener {
            C09391() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ChangePhoneHelpActivity.this.presentFragment(new ChangePhoneActivity(), true);
            }
        }

        C09403() {
        }

        public void onClick(View view) {
            if (ChangePhoneHelpActivity.this.getParentActivity() != null) {
                view = new Builder(ChangePhoneHelpActivity.this.getParentActivity());
                view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                view.setMessage(LocaleController.getString("PhoneNumberAlert", C0446R.string.PhoneNumberAlert));
                view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C09391());
                view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                ChangePhoneHelpActivity.this.showDialog(view.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangePhoneHelpActivity$1 */
    class C19591 extends ActionBarMenuOnItemClick {
        C19591() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChangePhoneHelpActivity.this.finishFragment();
            }
        }
    }

    public View createView(Context context) {
        CharSequence string;
        Context context2 = context;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        if (currentUser == null || currentUser.phone == null || currentUser.phone.length() == 0) {
            string = LocaleController.getString("NumberUnknown", C0446R.string.NumberUnknown);
        } else {
            PhoneFormat instance = PhoneFormat.getInstance();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("+");
            stringBuilder.append(currentUser.phone);
            string = instance.format(stringBuilder.toString());
        }
        r1.actionBar.setTitle(string);
        r1.actionBar.setActionBarMenuOnItemClick(new C19591());
        r1.fragmentView = new RelativeLayout(context2);
        r1.fragmentView.setOnTouchListener(new C09382());
        RelativeLayout relativeLayout = (RelativeLayout) r1.fragmentView;
        View scrollView = new ScrollView(context2);
        relativeLayout.addView(scrollView);
        LayoutParams layoutParams = (LayoutParams) scrollView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -2;
        layoutParams.addRule(15, -1);
        scrollView.setLayoutParams(layoutParams);
        View linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(0, AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f));
        scrollView.addView(linearLayout);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -2;
        linearLayout.setLayoutParams(layoutParams2);
        r1.imageView = new ImageView(context2);
        r1.imageView.setImageResource(C0446R.drawable.phone_change);
        r1.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_changephoneinfo_image), Mode.MULTIPLY));
        linearLayout.addView(r1.imageView, LayoutHelper.createLinear(-2, -2, 1));
        r1.textView1 = new TextView(context2);
        r1.textView1.setTextSize(1, 16.0f);
        r1.textView1.setGravity(1);
        r1.textView1.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        try {
            r1.textView1.setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", C0446R.string.PhoneNumberHelp)));
        } catch (Throwable e) {
            FileLog.m3e(e);
            r1.textView1.setText(LocaleController.getString("PhoneNumberHelp", C0446R.string.PhoneNumberHelp));
        }
        linearLayout.addView(r1.textView1, LayoutHelper.createLinear(-2, -2, 1, 20, 56, 20, 0));
        r1.textView2 = new TextView(context2);
        r1.textView2.setTextSize(1, 18.0f);
        r1.textView2.setGravity(1);
        r1.textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        r1.textView2.setText(LocaleController.getString("PhoneNumberChange", C0446R.string.PhoneNumberChange));
        r1.textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r1.textView2.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f));
        linearLayout.addView(r1.textView2, LayoutHelper.createLinear(-2, -2, 1, 20, 46, 20, 0));
        r1.textView2.setOnClickListener(new C09403());
        return r1.fragmentView;
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4), new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_changephoneinfo_image)};
    }
}
