package org.telegram.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.LocationFetchCallback;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ShareLocationDrawable;

@TargetApi(23)
public class ActionIntroActivity extends BaseFragment implements LocationFetchCallback {
    public static final int ACTION_TYPE_CHANGE_PHONE_NUMBER = 3;
    public static final int ACTION_TYPE_CHANNEL_CREATE = 0;
    public static final int ACTION_TYPE_NEARBY_GROUP_CREATE = 2;
    public static final int ACTION_TYPE_NEARBY_LOCATION_ACCESS = 1;
    public static final int ACTION_TYPE_NEARBY_LOCATION_ENABLED = 4;
    private TextView buttonTextView;
    private String currentGroupCreateAddress;
    private String currentGroupCreateDisplayAddress;
    private Location currentGroupCreateLocation;
    private int currentType;
    private TextView descriptionText;
    private TextView descriptionText2;
    private Drawable drawable1;
    private Drawable drawable2;
    private ImageView imageView;
    private TextView subtitleTextView;
    private TextView titleTextView;

    public ActionIntroActivity(int i) {
        this.currentType = i;
    }

    public View createView(Context context) {
        String str = "windowBackgroundWhite";
        this.actionBar.setBackgroundColor(Theme.getColor(str));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setAddToContainer(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ActionIntroActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new ViewGroup(context) {
            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Missing block: B:9:0x003a, code skipped:
            if (r13 != 4) goto L_0x0342;
     */
            public void onMeasure(int r12, int r13) {
                /*
                r11 = this;
                r12 = android.view.View.MeasureSpec.getSize(r12);
                r0 = android.view.View.MeasureSpec.getSize(r13);
                r1 = org.telegram.ui.ActionIntroActivity.this;
                r1 = r1.actionBar;
                r2 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r1.measure(r3, r13);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.currentType;
                r1 = NUM; // 0x3ee66666 float:0.45 double:5.21380997E-315;
                r3 = NUM; // 0x3var_a float:0.6 double:5.230388065E-315;
                r4 = NUM; // 0x42280000 float:42.0 double:5.483722033E-315;
                r5 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
                r6 = 0;
                if (r13 == 0) goto L_0x029a;
            L_0x002a:
                r7 = 1;
                if (r13 == r7) goto L_0x01ee;
            L_0x002d:
                r7 = 2;
                r8 = NUM; // 0x3var_ae14 float:0.78 double:5.245308343E-315;
                r9 = NUM; // 0x3ee147ae float:0.44 double:5.21215216E-315;
                if (r13 == r7) goto L_0x0105;
            L_0x0036:
                r7 = 3;
                if (r13 == r7) goto L_0x003e;
            L_0x0039:
                r1 = 4;
                if (r13 == r1) goto L_0x01ee;
            L_0x003c:
                goto L_0x0342;
            L_0x003e:
                if (r12 <= r0) goto L_0x00a6;
            L_0x0040:
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.imageView;
                r7 = (float) r12;
                r1 = r1 * r7;
                r1 = (int) r1;
                r9 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r10 = (float) r0;
                r10 = r10 * r8;
                r8 = (int) r10;
                r8 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r5);
                r13.measure(r9, r8);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.subtitleTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r8 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r1, r8);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.titleTextView;
                r7 = r7 * r3;
                r1 = (int) r7;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r7 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r3, r7);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.descriptionText;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r6 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r3, r6);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.buttonTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r5);
                r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r2 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r2);
                r13.measure(r1, r2);
                goto L_0x0342;
            L_0x00a6:
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.imageView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = (float) r0;
                r3 = r3 * r9;
                r3 = (int) r3;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r5);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.titleTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.subtitleTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.descriptionText;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.buttonTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r5);
                r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r2 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r2);
                r13.measure(r1, r2);
                goto L_0x0342;
            L_0x0105:
                if (r12 <= r0) goto L_0x017e;
            L_0x0107:
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.imageView;
                r7 = (float) r12;
                r1 = r1 * r7;
                r1 = (int) r1;
                r9 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r10 = (float) r0;
                r10 = r10 * r8;
                r8 = (int) r10;
                r8 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r5);
                r13.measure(r9, r8);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.subtitleTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r8 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r1, r8);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.titleTextView;
                r7 = r7 * r3;
                r1 = (int) r7;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r7 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r3, r7);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.descriptionText;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r7 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r3, r7);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.descriptionText2;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r6 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r3, r6);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.buttonTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r5);
                r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r2 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r2);
                r13.measure(r1, r2);
                goto L_0x0342;
            L_0x017e:
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.imageView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = (float) r0;
                r3 = r3 * r9;
                r3 = (int) r3;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r5);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.titleTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.subtitleTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.descriptionText;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.descriptionText2;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.buttonTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r5);
                r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r2 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r2);
                r13.measure(r1, r2);
                goto L_0x0342;
            L_0x01ee:
                r13 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
                if (r12 <= r0) goto L_0x0248;
            L_0x01f2:
                r1 = org.telegram.ui.ActionIntroActivity.this;
                r1 = r1.imageView;
                r7 = org.telegram.messenger.AndroidUtilities.dp(r13);
                r7 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r2);
                r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
                r13 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r2);
                r1.measure(r7, r13);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.titleTextView;
                r1 = (float) r12;
                r1 = r1 * r3;
                r1 = (int) r1;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r7 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r3, r7);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.descriptionText;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r6 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r3, r6);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.buttonTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r5);
                r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r2 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r2);
                r13.measure(r1, r2);
                goto L_0x0342;
            L_0x0248:
                r1 = org.telegram.ui.ActionIntroActivity.this;
                r1 = r1.imageView;
                r3 = org.telegram.messenger.AndroidUtilities.dp(r13);
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r2);
                r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
                r13 = android.view.View.MeasureSpec.makeMeasureSpec(r13, r2);
                r1.measure(r3, r13);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.titleTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.descriptionText;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.buttonTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r5);
                r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r2 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r2);
                r13.measure(r1, r2);
                goto L_0x0342;
            L_0x029a:
                if (r12 <= r0) goto L_0x02f3;
            L_0x029c:
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.imageView;
                r7 = (float) r12;
                r1 = r1 * r7;
                r1 = (int) r1;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r8 = (float) r0;
                r9 = NUM; // 0x3f2e147b float:0.68 double:5.2370193E-315;
                r8 = r8 * r9;
                r8 = (int) r8;
                r8 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r2);
                r13.measure(r1, r8);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.titleTextView;
                r7 = r7 * r3;
                r1 = (int) r7;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r7 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r3, r7);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.descriptionText;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r2);
                r6 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r3, r6);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.buttonTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r5);
                r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r2 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r2);
                r13.measure(r1, r2);
                goto L_0x0342;
            L_0x02f3:
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.imageView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = (float) r0;
                r7 = NUM; // 0x3ecCLASSNAMEba float:0.399 double:5.20535514E-315;
                r3 = r3 * r7;
                r3 = (int) r3;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r2);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.titleTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.descriptionText;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2);
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6);
                r13.measure(r1, r3);
                r13 = org.telegram.ui.ActionIntroActivity.this;
                r13 = r13.buttonTextView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r5);
                r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r2 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r2);
                r13.measure(r1, r2);
            L_0x0342:
                r11.setMeasuredDimension(r12, r0);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionIntroActivity$AnonymousClass2.onMeasure(int, int):void");
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                ActionIntroActivity.this.actionBar.layout(0, 0, i3, ActionIntroActivity.this.actionBar.getMeasuredHeight());
                int i5 = i3 - i;
                i = i4 - i2;
                i2 = ActionIntroActivity.this.currentType;
                float f;
                float f2;
                float f3;
                if (i2 != 0) {
                    if (i2 != 1) {
                        if (i2 != 2) {
                            if (i2 != 3) {
                                if (i2 != 4) {
                                    return;
                                }
                            } else if (i3 > i4) {
                                f = (float) i;
                                i2 = ((int) ((0.95f * f) - ((float) ActionIntroActivity.this.imageView.getMeasuredHeight()))) / 2;
                                ActionIntroActivity.this.imageView.layout(0, i2, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i2);
                                i2 += ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(10.0f);
                                ActionIntroActivity.this.subtitleTextView.layout(0, i2, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i2);
                                f2 = (float) i5;
                                f3 = 0.4f * f2;
                                i2 = (int) f3;
                                i3 = (int) (0.12f * f);
                                ActionIntroActivity.this.titleTextView.layout(i2, i3, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i2, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i3);
                                i3 = (int) (0.24f * f);
                                ActionIntroActivity.this.descriptionText.layout(i2, i3, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i2, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i3);
                                i5 = (int) (f3 + (((f2 * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                                i = (int) (f * 0.8f);
                                ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                                return;
                            } else {
                                f = (float) i;
                                i2 = (int) (0.2229f * f);
                                ActionIntroActivity.this.imageView.layout(0, i2, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i2);
                                i2 = (int) (0.352f * f);
                                ActionIntroActivity.this.titleTextView.layout(0, i2, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i2);
                                i2 = (int) (0.409f * f);
                                ActionIntroActivity.this.subtitleTextView.layout(0, i2, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i2);
                                i2 = (int) (0.468f * f);
                                ActionIntroActivity.this.descriptionText.layout(0, i2, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i2);
                                i5 = (i5 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                                i = (int) (f * 0.805f);
                                ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                                return;
                            }
                        } else if (i3 > i4) {
                            f = (float) i;
                            i3 = ((int) ((0.9f * f) - ((float) ActionIntroActivity.this.imageView.getMeasuredHeight()))) / 2;
                            ActionIntroActivity.this.imageView.layout(0, i3, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i3);
                            i3 += ActionIntroActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(10.0f);
                            ActionIntroActivity.this.subtitleTextView.layout(0, i3, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i3);
                            f2 = (float) i5;
                            f3 = 0.4f * f2;
                            i3 = (int) f3;
                            i4 = (int) (0.12f * f);
                            ActionIntroActivity.this.titleTextView.layout(i3, i4, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i3, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i4);
                            i4 = (int) (0.26f * f);
                            ActionIntroActivity.this.descriptionText.layout(i3, i4, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i3, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i4);
                            i5 = (int) (f3 + (((f2 * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            i = (int) (f * 0.6f);
                            ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                            i5 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                            ActionIntroActivity.this.descriptionText2.layout(i3, i5, ActionIntroActivity.this.descriptionText2.getMeasuredWidth() + i3, ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + i5);
                            return;
                        } else {
                            f = (float) i;
                            i3 = (int) (0.197f * f);
                            ActionIntroActivity.this.imageView.layout(0, i3, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i3);
                            i3 = (int) (0.421f * f);
                            ActionIntroActivity.this.titleTextView.layout(0, i3, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i3);
                            i3 = (int) (0.477f * f);
                            ActionIntroActivity.this.subtitleTextView.layout(0, i3, ActionIntroActivity.this.subtitleTextView.getMeasuredWidth(), ActionIntroActivity.this.subtitleTextView.getMeasuredHeight() + i3);
                            i3 = (int) (0.537f * f);
                            ActionIntroActivity.this.descriptionText.layout(0, i3, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i3);
                            i5 = (i5 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                            i = (int) (f * 0.71f);
                            ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                            i5 = (getMeasuredHeight() - ActionIntroActivity.this.descriptionText2.getMeasuredHeight()) - AndroidUtilities.dp(20.0f);
                            ActionIntroActivity.this.descriptionText2.layout(0, i5, ActionIntroActivity.this.descriptionText2.getMeasuredWidth(), ActionIntroActivity.this.descriptionText2.getMeasuredHeight() + i5);
                            return;
                        }
                    }
                    if (i3 > i4) {
                        i2 = (i - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                        f2 = (float) i5;
                        i3 = ((int) ((0.5f * f2) - ((float) ActionIntroActivity.this.imageView.getMeasuredWidth()))) / 2;
                        ActionIntroActivity.this.imageView.layout(i3, i2, ActionIntroActivity.this.imageView.getMeasuredWidth() + i3, ActionIntroActivity.this.imageView.getMeasuredHeight() + i2);
                        f3 = 0.4f * f2;
                        i2 = (int) f3;
                        f = (float) i;
                        i3 = (int) (0.14f * f);
                        ActionIntroActivity.this.titleTextView.layout(i2, i3, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i2, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i3);
                        i3 = (int) (0.31f * f);
                        ActionIntroActivity.this.descriptionText.layout(i2, i3, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i2, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i3);
                        i5 = (int) (f3 + (((f2 * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                        i = (int) (f * 0.78f);
                        ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                        return;
                    }
                    f = (float) i;
                    i2 = (int) (0.214f * f);
                    i3 = (i5 - ActionIntroActivity.this.imageView.getMeasuredWidth()) / 2;
                    ActionIntroActivity.this.imageView.layout(i3, i2, ActionIntroActivity.this.imageView.getMeasuredWidth() + i3, ActionIntroActivity.this.imageView.getMeasuredHeight() + i2);
                    i2 = (int) (0.414f * f);
                    ActionIntroActivity.this.titleTextView.layout(0, i2, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i2);
                    i2 = (int) (0.493f * f);
                    ActionIntroActivity.this.descriptionText.layout(0, i2, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i2);
                    i5 = (i5 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                    i = (int) (f * 0.71f);
                    ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                } else if (i3 > i4) {
                    i2 = (i - ActionIntroActivity.this.imageView.getMeasuredHeight()) / 2;
                    ActionIntroActivity.this.imageView.layout(0, i2, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i2);
                    f2 = (float) i5;
                    f3 = 0.4f * f2;
                    i2 = (int) f3;
                    f = (float) i;
                    i3 = (int) (0.22f * f);
                    ActionIntroActivity.this.titleTextView.layout(i2, i3, ActionIntroActivity.this.titleTextView.getMeasuredWidth() + i2, ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i3);
                    i3 = (int) (0.39f * f);
                    ActionIntroActivity.this.descriptionText.layout(i2, i3, ActionIntroActivity.this.descriptionText.getMeasuredWidth() + i2, ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i3);
                    i5 = (int) (f3 + (((f2 * 0.6f) - ((float) ActionIntroActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                    i = (int) (f * 0.69f);
                    ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                } else {
                    f = (float) i;
                    i2 = (int) (0.188f * f);
                    ActionIntroActivity.this.imageView.layout(0, i2, ActionIntroActivity.this.imageView.getMeasuredWidth(), ActionIntroActivity.this.imageView.getMeasuredHeight() + i2);
                    i2 = (int) (0.651f * f);
                    ActionIntroActivity.this.titleTextView.layout(0, i2, ActionIntroActivity.this.titleTextView.getMeasuredWidth(), ActionIntroActivity.this.titleTextView.getMeasuredHeight() + i2);
                    i2 = (int) (0.731f * f);
                    ActionIntroActivity.this.descriptionText.layout(0, i2, ActionIntroActivity.this.descriptionText.getMeasuredWidth(), ActionIntroActivity.this.descriptionText.getMeasuredHeight() + i2);
                    i5 = (i5 - ActionIntroActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                    i = (int) (f * 0.853f);
                    ActionIntroActivity.this.buttonTextView.layout(i5, i, ActionIntroActivity.this.buttonTextView.getMeasuredWidth() + i5, ActionIntroActivity.this.buttonTextView.getMeasuredHeight() + i);
                }
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(str));
        ViewGroup viewGroup = (ViewGroup) this.fragmentView;
        viewGroup.setOnTouchListener(-$$Lambda$ActionIntroActivity$fQjBdUFaAXJ5YCul6b6pzBMDpBQ.INSTANCE);
        viewGroup.addView(this.actionBar);
        this.imageView = new ImageView(context);
        viewGroup.addView(this.imageView);
        this.titleTextView = new TextView(context);
        String str2 = "windowBackgroundWhiteBlackText";
        this.titleTextView.setTextColor(Theme.getColor(str2));
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.titleTextView.setTextSize(1, 24.0f);
        viewGroup.addView(this.titleTextView);
        this.subtitleTextView = new TextView(context);
        this.subtitleTextView.setTextColor(Theme.getColor(str2));
        this.subtitleTextView.setGravity(1);
        this.subtitleTextView.setTextSize(1, 15.0f);
        this.subtitleTextView.setSingleLine(true);
        this.subtitleTextView.setEllipsize(TruncateAt.END);
        if (this.currentType == 2) {
            this.subtitleTextView.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        } else {
            this.subtitleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        this.subtitleTextView.setVisibility(8);
        viewGroup.addView(this.subtitleTextView);
        this.descriptionText = new TextView(context);
        String str3 = "windowBackgroundWhiteGrayText6";
        this.descriptionText.setTextColor(Theme.getColor(str3));
        this.descriptionText.setGravity(1);
        this.descriptionText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText.setTextSize(1, 15.0f);
        if (this.currentType == 2) {
            this.descriptionText.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
        } else {
            this.descriptionText.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        viewGroup.addView(this.descriptionText);
        this.descriptionText2 = new TextView(context);
        this.descriptionText2.setTextColor(Theme.getColor(str3));
        this.descriptionText2.setGravity(1);
        this.descriptionText2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText2.setTextSize(1, 13.0f);
        this.descriptionText2.setVisibility(8);
        if (this.currentType == 2) {
            this.descriptionText2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        } else {
            this.descriptionText2.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        }
        viewGroup.addView(this.descriptionText2);
        this.buttonTextView = new TextView(context);
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        viewGroup.addView(this.buttonTextView);
        this.buttonTextView.setOnClickListener(new -$$Lambda$ActionIntroActivity$BcvFnoCk-mhvF1NmHV0oGHf_Czc(this));
        int i = this.currentType;
        TextView textView;
        if (i == 0) {
            this.imageView.setImageResource(NUM);
            this.imageView.setScaleType(ScaleType.FIT_CENTER);
            this.titleTextView.setText(LocaleController.getString("ChannelAlertTitle", NUM));
            this.descriptionText.setText(LocaleController.getString("ChannelAlertText", NUM));
            this.buttonTextView.setText(LocaleController.getString("ChannelAlertCreate2", NUM));
        } else if (i == 1) {
            this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100.0f), Theme.getColor("chats_archiveBackground")));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context, 3));
            this.imageView.setScaleType(ScaleType.CENTER);
            this.titleTextView.setText(LocaleController.getString("PeopleNearby", NUM));
            this.descriptionText.setText(LocaleController.getString("PeopleNearbyAccessInfo", NUM));
            this.buttonTextView.setText(LocaleController.getString("PeopleNearbyAllowAccess", NUM));
        } else if (i == 2) {
            this.subtitleTextView.setVisibility(0);
            this.descriptionText2.setVisibility(0);
            this.imageView.setImageResource(Theme.getCurrentTheme().isDark() ? NUM : NUM);
            this.imageView.setScaleType(ScaleType.CENTER);
            textView = this.subtitleTextView;
            CharSequence charSequence = this.currentGroupCreateDisplayAddress;
            if (charSequence == null) {
                charSequence = "";
            }
            textView.setText(charSequence);
            this.titleTextView.setText(LocaleController.getString("NearbyCreateGroup", NUM));
            this.descriptionText.setText(LocaleController.getString("NearbyCreateGroupInfo", NUM));
            this.descriptionText2.setText(LocaleController.getString("NearbyCreateGroupInfo2", NUM));
            this.buttonTextView.setText(LocaleController.getString("NearbyStartGroup", NUM));
        } else if (i == 3) {
            this.subtitleTextView.setVisibility(0);
            this.drawable1 = context.getResources().getDrawable(NUM);
            this.drawable2 = context.getResources().getDrawable(NUM);
            this.drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image"), Mode.MULTIPLY));
            this.drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image2"), Mode.MULTIPLY));
            this.imageView.setImageDrawable(new CombinedDrawable(this.drawable1, this.drawable2));
            this.imageView.setScaleType(ScaleType.CENTER);
            textView = this.subtitleTextView;
            PhoneFormat instance = PhoneFormat.getInstance();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("+");
            stringBuilder.append(getUserConfig().getCurrentUser().phone);
            textView.setText(instance.format(stringBuilder.toString()));
            this.titleTextView.setText(LocaleController.getString("PhoneNumberChange2", NUM));
            this.descriptionText.setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", NUM)));
            this.buttonTextView.setText(LocaleController.getString("PhoneNumberChange2", NUM));
        } else if (i == 4) {
            this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100.0f), Theme.getColor("chats_archiveBackground")));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context, 3));
            this.imageView.setScaleType(ScaleType.CENTER);
            this.titleTextView.setText(LocaleController.getString("PeopleNearby", NUM));
            this.descriptionText.setText(LocaleController.getString("PeopleNearbyGpsInfo", NUM));
            this.buttonTextView.setText(LocaleController.getString("PeopleNearbyGps", NUM));
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$ActionIntroActivity(View view) {
        if (getParentActivity() != null) {
            int i = this.currentType;
            Bundle bundle;
            if (i == 0) {
                bundle = new Bundle();
                bundle.putInt("step", 0);
                presentFragment(new ChannelCreateActivity(bundle), true);
            } else if (i == 1) {
                getParentActivity().requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            } else if (i != 2) {
                if (i == 3) {
                    Builder builder = new Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("PhoneNumberChangeTitle", NUM));
                    builder.setMessage(LocaleController.getString("PhoneNumberAlert", NUM));
                    builder.setPositiveButton(LocaleController.getString("Change", NUM), new -$$Lambda$ActionIntroActivity$h-zrlY3ShP9MNAFsjq-ukj28QiM(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    showDialog(builder.create());
                } else if (i == 4) {
                    try {
                        getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            } else if (this.currentGroupCreateAddress != null && this.currentGroupCreateLocation != null) {
                bundle = new Bundle();
                ArrayList arrayList = new ArrayList();
                arrayList.add(Integer.valueOf(getUserConfig().getClientUserId()));
                bundle.putIntegerArrayList("result", arrayList);
                bundle.putInt("chatType", 4);
                bundle.putString("address", this.currentGroupCreateAddress);
                bundle.putParcelable("location", this.currentGroupCreateLocation);
                presentFragment(new GroupCreateFinalActivity(bundle), true);
            }
        }
    }

    public /* synthetic */ void lambda$null$1$ActionIntroActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new ChangePhoneActivity(), true);
    }

    public void onLocationAddressAvailable(String str, String str2, Location location) {
        TextView textView = this.subtitleTextView;
        if (textView != null) {
            textView.setText(str);
            this.currentGroupCreateAddress = str;
            this.currentGroupCreateDisplayAddress = str2;
            this.currentGroupCreateLocation = location;
        }
    }

    public void onResume() {
        super.onResume();
        if (this.currentType == 4) {
            boolean isLocationEnabled;
            int i = VERSION.SDK_INT;
            if (i >= 28) {
                isLocationEnabled = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
            } else {
                if (i >= 19) {
                    try {
                        boolean z = false;
                        if (Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) != 0) {
                            z = true;
                        }
                        isLocationEnabled = z;
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                isLocationEnabled = true;
            }
            if (isLocationEnabled) {
                presentFragment(new PeopleNearbyActivity(), true);
            }
        }
    }

    private void showPermissionAlert(boolean z) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$ActionIntroActivity$MDYMt6ansWvDbsIN0dYdwBhtYBI(this));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$3$ActionIntroActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("package:");
                stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
                intent.setData(Uri.parse(stringBuilder.toString()));
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void setGroupCreateAddress(String str, String str2, Location location) {
        this.currentGroupCreateAddress = str;
        this.currentGroupCreateDisplayAddress = str2;
        this.currentGroupCreateLocation = location;
        if (location != null && str == null) {
            LocationController.fetchLocationAddress(location, this);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (!(i != 2 || iArr == null || iArr.length == 0)) {
            if (iArr[0] == 0) {
                presentFragment(new PeopleNearbyActivity(), true);
            } else if (getParentActivity() != null) {
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
                builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$ActionIntroActivity$VKxRXA-XbgTCJpZrpCKC8NHvoI4(this));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                showDialog(builder.create());
            }
        }
    }

    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$4$ActionIntroActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("package:");
                stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
                intent.setData(Uri.parse(stringBuilder.toString()));
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[12];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarWhiteSelector");
        themeDescriptionArr[4] = new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[5] = new ThemeDescription(this.subtitleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[6] = new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6");
        themeDescriptionArr[7] = new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "featuredStickers_buttonText");
        themeDescriptionArr[8] = new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, null, null, null, null, "featuredStickers_addButton");
        themeDescriptionArr[9] = new ThemeDescription(this.buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "featuredStickers_addButtonPressed");
        themeDescriptionArr[10] = new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, new Drawable[]{this.drawable1}, null, "changephoneinfo_image");
        themeDescriptionArr[11] = new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, new Drawable[]{this.drawable2}, null, "changephoneinfo_image2");
        return themeDescriptionArr;
    }
}
