package org.telegram.ui.Components;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SvgHelper;
import org.telegram.ui.ActionBar.BottomSheet;

public class GroupCallRecordAlert extends BottomSheet {
    /* access modifiers changed from: private */
    public int currentPage;
    /* access modifiers changed from: private */
    public float pageOffset;
    /* access modifiers changed from: private */
    public TextView positiveButton;
    /* access modifiers changed from: private */
    public TextView[] titles;
    /* access modifiers changed from: private */
    public LinearLayout titlesLayout;
    /* access modifiers changed from: private */
    public ViewPager viewPager;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public GroupCallRecordAlert(android.content.Context r22, org.telegram.tgnet.TLRPC.Chat r23, boolean r24) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            r2 = 0
            r0.<init>(r1, r2)
            java.lang.String r3 = "voipgroup_inviteMembersBackground"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.drawable.Drawable r4 = r0.shadowDrawable
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r3, r6)
            r4.setColorFilter(r5)
            org.telegram.ui.Components.GroupCallRecordAlert$1 r4 = new org.telegram.ui.Components.GroupCallRecordAlert$1
            r4.<init>(r1)
            r0.containerView = r4
            android.view.ViewGroup r4 = r0.containerView
            r4.setWillNotDraw(r2)
            android.view.ViewGroup r4 = r0.containerView
            r4.setClipChildren(r2)
            android.view.ViewGroup r4 = r0.containerView
            android.graphics.drawable.Drawable r5 = r0.shadowDrawable
            r4.setBackgroundDrawable(r5)
            android.view.ViewGroup r4 = r0.containerView
            int r5 = r0.backgroundPaddingLeft
            int r6 = r0.backgroundPaddingLeft
            r4.setPadding(r5, r2, r6, r2)
            android.widget.TextView r4 = new android.widget.TextView
            android.content.Context r5 = r21.getContext()
            r4.<init>(r5)
            boolean r5 = org.telegram.messenger.ChatObject.isChannelOrGiga(r23)
            if (r5 == 0) goto L_0x0057
            r5 = 2131628441(0x7f0e1199, float:1.8884175E38)
            java.lang.String r6 = "VoipChannelRecordVoiceChat"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r4.setText(r5)
            goto L_0x0063
        L_0x0057:
            r5 = 2131628637(0x7f0e125d, float:1.8884572E38)
            java.lang.String r6 = "VoipRecordVoiceChat"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r4.setText(r5)
        L_0x0063:
            r5 = -1
            r4.setTextColor(r5)
            r6 = 1101004800(0x41a00000, float:20.0)
            r7 = 1
            r4.setTextSize(r7, r6)
            java.lang.String r6 = "fonts/rmedium.ttf"
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r4.setTypeface(r8)
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            r9 = 5
            r10 = 3
            if (r8 == 0) goto L_0x007e
            r8 = 5
            goto L_0x007f
        L_0x007e:
            r8 = 3
        L_0x007f:
            r8 = r8 | 48
            r4.setGravity(r8)
            android.view.ViewGroup r8 = r0.containerView
            r11 = -2
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x008f
            r13 = 5
            goto L_0x0090
        L_0x008f:
            r13 = 3
        L_0x0090:
            r13 = r13 | 48
            r14 = 1103101952(0x41CLASSNAME, float:24.0)
            r15 = 1105723392(0x41e80000, float:29.0)
            r16 = 1103101952(0x41CLASSNAME, float:24.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r8.addView(r4, r11)
            android.widget.TextView r8 = new android.widget.TextView
            android.content.Context r11 = r21.getContext()
            r8.<init>(r11)
            r11 = 2131628638(0x7f0e125e, float:1.8884574E38)
            java.lang.String r12 = "VoipRecordVoiceChatInfo"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r8.setText(r11)
            r8.setTextColor(r5)
            r11 = 1096810496(0x41600000, float:14.0)
            r8.setTextSize(r7, r11)
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x00c4
            r12 = 5
            goto L_0x00c5
        L_0x00c4:
            r12 = 3
        L_0x00c5:
            r12 = r12 | 48
            r8.setGravity(r12)
            android.view.ViewGroup r12 = r0.containerView
            r13 = -2
            r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r15 = org.telegram.messenger.LocaleController.isRTL
            if (r15 == 0) goto L_0x00d4
            goto L_0x00d5
        L_0x00d4:
            r9 = 3
        L_0x00d5:
            r15 = r9 | 48
            r16 = 1103101952(0x41CLASSNAME, float:24.0)
            r17 = 1115160576(0x42780000, float:62.0)
            r18 = 1103101952(0x41CLASSNAME, float:24.0)
            r19 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r12.addView(r8, r9)
            android.widget.TextView[] r9 = new android.widget.TextView[r10]
            r0.titles = r9
            androidx.viewpager.widget.ViewPager r9 = new androidx.viewpager.widget.ViewPager
            r9.<init>(r1)
            r0.viewPager = r9
            r9.setClipChildren(r2)
            androidx.viewpager.widget.ViewPager r9 = r0.viewPager
            r10 = 4
            r9.setOffscreenPageLimit(r10)
            androidx.viewpager.widget.ViewPager r9 = r0.viewPager
            r9.setClipToPadding(r2)
            androidx.viewpager.widget.ViewPager r9 = r0.viewPager
            r10 = 2130706432(0x7var_, float:1.7014118E38)
            org.telegram.messenger.AndroidUtilities.setViewPagerEdgeEffectColor(r9, r10)
            androidx.viewpager.widget.ViewPager r9 = r0.viewPager
            org.telegram.ui.Components.GroupCallRecordAlert$Adapter r10 = new org.telegram.ui.Components.GroupCallRecordAlert$Adapter
            r12 = 0
            r10.<init>()
            r9.setAdapter(r10)
            androidx.viewpager.widget.ViewPager r9 = r0.viewPager
            r9.setPageMargin(r2)
            android.view.ViewGroup r9 = r0.containerView
            androidx.viewpager.widget.ViewPager r10 = r0.viewPager
            r12 = -1
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r14 = 1
            r15 = 0
            r16 = 1120403456(0x42CLASSNAME, float:100.0)
            r17 = 0
            r18 = 1124204544(0x43020000, float:130.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r9.addView(r10, r12)
            androidx.viewpager.widget.ViewPager r9 = r0.viewPager
            org.telegram.ui.Components.GroupCallRecordAlert$2 r10 = new org.telegram.ui.Components.GroupCallRecordAlert$2
            r10.<init>()
            r9.addOnPageChangeListener(r10)
            android.view.View r9 = new android.view.View
            android.content.Context r10 = r21.getContext()
            r9.<init>(r10)
            android.graphics.drawable.GradientDrawable r10 = new android.graphics.drawable.GradientDrawable
            android.graphics.drawable.GradientDrawable$Orientation r12 = android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT
            r13 = 2
            int[] r14 = new int[r13]
            r14[r2] = r3
            r14[r7] = r2
            r10.<init>(r12, r14)
            r9.setBackground(r10)
            android.view.ViewGroup r10 = r0.containerView
            r14 = 120(0x78, float:1.68E-43)
            r15 = -1082130432(0xffffffffbvar_, float:-1.0)
            r16 = 51
            r18 = 1120403456(0x42CLASSNAME, float:100.0)
            r20 = 1124204544(0x43020000, float:130.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r10.addView(r9, r12)
            android.view.View r10 = new android.view.View
            android.content.Context r12 = r21.getContext()
            r10.<init>(r12)
            android.graphics.drawable.GradientDrawable r12 = new android.graphics.drawable.GradientDrawable
            android.graphics.drawable.GradientDrawable$Orientation r14 = android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT
            int[] r13 = new int[r13]
            r13[r2] = r2
            r13[r7] = r3
            r12.<init>(r14, r13)
            r10.setBackground(r12)
            android.view.ViewGroup r12 = r0.containerView
            r13 = 120(0x78, float:1.68E-43)
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            r15 = 53
            r16 = 0
            r17 = 1120403456(0x42CLASSNAME, float:100.0)
            r18 = 0
            r19 = 1124204544(0x43020000, float:130.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r12.addView(r10, r13)
            org.telegram.ui.Components.GroupCallRecordAlert$3 r12 = new org.telegram.ui.Components.GroupCallRecordAlert$3
            android.content.Context r13 = r21.getContext()
            r12.<init>(r13)
            r0.positiveButton = r12
            r13 = 1115684864(0x42800000, float:64.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r12.setMinWidth(r13)
            android.widget.TextView r12 = r0.positiveButton
            java.lang.Integer r13 = java.lang.Integer.valueOf(r5)
            r12.setTag(r13)
            android.widget.TextView r12 = r0.positiveButton
            r12.setTextSize(r7, r11)
            android.widget.TextView r11 = r0.positiveButton
            java.lang.String r12 = "voipgroup_nameText"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r11.setTextColor(r13)
            android.widget.TextView r11 = r0.positiveButton
            r13 = 17
            r11.setGravity(r13)
            android.widget.TextView r11 = r0.positiveButton
            android.graphics.Typeface r13 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r11.setTypeface(r13)
            android.widget.TextView r11 = r0.positiveButton
            r13 = 2131628636(0x7f0e125c, float:1.888457E38)
            java.lang.String r14 = "VoipRecordStart"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r11.setText(r13)
            int r11 = android.os.Build.VERSION.SDK_INT
            r13 = 23
            if (r11 < r13) goto L_0x01fd
            android.widget.TextView r11 = r0.positiveButton
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r14 = 76
            int r12 = androidx.core.graphics.ColorUtils.setAlphaComponent(r12, r14)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r13, r2, r12)
            r11.setForeground(r12)
        L_0x01fd:
            android.widget.TextView r11 = r0.positiveButton
            r12 = 1094713344(0x41400000, float:12.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r11.setPadding(r2, r13, r2, r14)
            android.widget.TextView r11 = r0.positiveButton
            org.telegram.ui.Components.GroupCallRecordAlert$$ExternalSyntheticLambda0 r13 = new org.telegram.ui.Components.GroupCallRecordAlert$$ExternalSyntheticLambda0
            r13.<init>(r0)
            r11.setOnClickListener(r13)
            android.view.ViewGroup r11 = r0.containerView
            android.widget.TextView r13 = r0.positiveButton
            r14 = -1
            r15 = 1111490560(0x42400000, float:48.0)
            r16 = 80
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 1115684864(0x42800000, float:64.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r11.addView(r13, r14)
            android.widget.LinearLayout r11 = new android.widget.LinearLayout
            r11.<init>(r1)
            r0.titlesLayout = r11
            android.view.ViewGroup r11 = r0.containerView
            android.widget.LinearLayout r13 = r0.titlesLayout
            r14 = 64
            r15 = 80
            r2 = -2
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r14, (int) r15)
            r11.addView(r13, r14)
            r11 = 0
        L_0x0246:
            android.widget.TextView[] r13 = r0.titles
            int r14 = r13.length
            if (r11 >= r14) goto L_0x02e3
            android.widget.TextView r14 = new android.widget.TextView
            r14.<init>(r1)
            r13[r11] = r14
            android.widget.TextView[] r13 = r0.titles
            r13 = r13[r11]
            r13.setTextSize(r7, r12)
            android.widget.TextView[] r13 = r0.titles
            r13 = r13[r11]
            r13.setTextColor(r5)
            android.widget.TextView[] r13 = r0.titles
            r13 = r13[r11]
            android.graphics.Typeface r14 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r13.setTypeface(r14)
            android.widget.TextView[] r13 = r0.titles
            r13 = r13[r11]
            r14 = 1092616192(0x41200000, float:10.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r12 = 0
            r13.setPadding(r15, r12, r14, r12)
            android.widget.TextView[] r13 = r0.titles
            r13 = r13[r11]
            r14 = 16
            r13.setGravity(r14)
            android.widget.TextView[] r13 = r0.titles
            r13 = r13[r11]
            r13.setSingleLine(r7)
            android.widget.LinearLayout r13 = r0.titlesLayout
            android.widget.TextView[] r14 = r0.titles
            r14 = r14[r11]
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r5)
            r13.addView(r14, r15)
            if (r11 != 0) goto L_0x02ad
            android.widget.TextView[] r13 = r0.titles
            r13 = r13[r11]
            r14 = 2131628633(0x7f0e1259, float:1.8884564E38)
            java.lang.String r15 = "VoipRecordAudio"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r13.setText(r14)
            goto L_0x02d0
        L_0x02ad:
            if (r11 != r7) goto L_0x02c0
            android.widget.TextView[] r13 = r0.titles
            r13 = r13[r11]
            r14 = 2131628635(0x7f0e125b, float:1.8884568E38)
            java.lang.String r15 = "VoipRecordPortrait"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r13.setText(r14)
            goto L_0x02d0
        L_0x02c0:
            android.widget.TextView[] r13 = r0.titles
            r13 = r13[r11]
            r14 = 2131628634(0x7f0e125a, float:1.8884566E38)
            java.lang.String r15 = "VoipRecordLandscape"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r13.setText(r14)
        L_0x02d0:
            r13 = r11
            android.widget.TextView[] r14 = r0.titles
            r14 = r14[r11]
            org.telegram.ui.Components.GroupCallRecordAlert$$ExternalSyntheticLambda1 r15 = new org.telegram.ui.Components.GroupCallRecordAlert$$ExternalSyntheticLambda1
            r15.<init>(r0, r13)
            r14.setOnClickListener(r15)
            int r11 = r11 + 1
            r12 = 1094713344(0x41400000, float:12.0)
            goto L_0x0246
        L_0x02e3:
            if (r24 == 0) goto L_0x02ea
            androidx.viewpager.widget.ViewPager r2 = r0.viewPager
            r2.setCurrentItem(r7)
        L_0x02ea:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallRecordAlert.<init>(android.content.Context, org.telegram.tgnet.TLRPC$Chat, boolean):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-GroupCallRecordAlert  reason: not valid java name */
    public /* synthetic */ void m2324lambda$new$0$orgtelegramuiComponentsGroupCallRecordAlert(View view) {
        onStartRecord(this.currentPage);
        dismiss();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-GroupCallRecordAlert  reason: not valid java name */
    public /* synthetic */ void m2325lambda$new$1$orgtelegramuiComponentsGroupCallRecordAlert(int num, View view) {
        this.viewPager.setCurrentItem(num, true);
    }

    /* access modifiers changed from: private */
    public void updateTitlesLayout() {
        float scale;
        float alpha;
        View[] viewArr = this.titles;
        int i = this.currentPage;
        View current = viewArr[i];
        View next = i < viewArr.length + -1 ? viewArr[i + 1] : null;
        float measuredWidth = (float) (this.containerView.getMeasuredWidth() / 2);
        float currentCx = (float) (current.getLeft() + (current.getMeasuredWidth() / 2));
        float tx = ((float) (this.containerView.getMeasuredWidth() / 2)) - currentCx;
        if (next != null) {
            tx -= (((float) (next.getLeft() + (next.getMeasuredWidth() / 2))) - currentCx) * this.pageOffset;
        }
        int a = 0;
        while (true) {
            TextView[] textViewArr = this.titles;
            if (a < textViewArr.length) {
                int i2 = this.currentPage;
                if (a < i2 || a > i2 + 1) {
                    alpha = 0.7f;
                    scale = 0.9f;
                } else if (a == i2) {
                    float f = this.pageOffset;
                    alpha = 1.0f - (0.3f * f);
                    scale = 1.0f - (f * 0.1f);
                } else {
                    float f2 = this.pageOffset;
                    alpha = (0.3f * f2) + 0.7f;
                    scale = (f2 * 0.1f) + 0.9f;
                }
                textViewArr[a].setAlpha(alpha);
                this.titles[a].setScaleX(scale);
                this.titles[a].setScaleY(scale);
                a++;
            } else {
                this.titlesLayout.setTranslationX(tx);
                this.positiveButton.invalidate();
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onStartRecord(int type) {
    }

    private class Adapter extends PagerAdapter {
        private Adapter() {
        }

        public int getCount() {
            return GroupCallRecordAlert.this.titles.length;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            int res;
            ImageView imageView = new ImageView(GroupCallRecordAlert.this.getContext());
            imageView.setTag(Integer.valueOf(position));
            imageView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(200.0f), -1));
            View view = imageView;
            if (position == 0) {
                res = NUM;
            } else if (position == 1) {
                res = NUM;
            } else {
                res = NUM;
            }
            SvgHelper.SvgDrawable drawable = SvgHelper.getDrawable(RLottieDrawable.readRes((File) null, res));
            drawable.setAspectFill(false);
            imageView.setImageDrawable(drawable);
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            container.addView(view, 0);
            return view;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }
}
