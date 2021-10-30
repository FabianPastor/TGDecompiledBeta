package org.telegram.ui.Components;

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

    /* access modifiers changed from: protected */
    public void onStartRecord(int i) {
        throw null;
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public GroupCallRecordAlert(android.content.Context r20, org.telegram.tgnet.TLRPC$Chat r21, boolean r22) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
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
            r4.setWillNotDraw(r2)
            android.view.ViewGroup r4 = r0.containerView
            r4.setClipChildren(r2)
            android.view.ViewGroup r4 = r0.containerView
            android.graphics.drawable.Drawable r5 = r0.shadowDrawable
            r4.setBackgroundDrawable(r5)
            android.view.ViewGroup r4 = r0.containerView
            int r5 = r0.backgroundPaddingLeft
            r4.setPadding(r5, r2, r5, r2)
            android.widget.TextView r4 = new android.widget.TextView
            android.content.Context r5 = r19.getContext()
            r4.<init>(r5)
            boolean r5 = org.telegram.messenger.ChatObject.isChannelOrGiga(r21)
            if (r5 == 0) goto L_0x0053
            r5 = 2131628354(0x7f0e1142, float:1.8883998E38)
            java.lang.String r6 = "VoipChannelRecordVoiceChat"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r4.setText(r5)
            goto L_0x005f
        L_0x0053:
            r5 = 2131628550(0x7f0e1206, float:1.8884396E38)
            java.lang.String r6 = "VoipRecordVoiceChat"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r4.setText(r5)
        L_0x005f:
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
            if (r8 == 0) goto L_0x007a
            r8 = 5
            goto L_0x007b
        L_0x007a:
            r8 = 3
        L_0x007b:
            r8 = r8 | 48
            r4.setGravity(r8)
            android.view.ViewGroup r8 = r0.containerView
            r11 = -2
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x008b
            r13 = 5
            goto L_0x008c
        L_0x008b:
            r13 = 3
        L_0x008c:
            r13 = r13 | 48
            r14 = 1103101952(0x41CLASSNAME, float:24.0)
            r15 = 1105723392(0x41e80000, float:29.0)
            r16 = 1103101952(0x41CLASSNAME, float:24.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r8.addView(r4, r11)
            android.widget.TextView r4 = new android.widget.TextView
            android.content.Context r8 = r19.getContext()
            r4.<init>(r8)
            r8 = 2131628551(0x7f0e1207, float:1.8884398E38)
            java.lang.String r11 = "VoipRecordVoiceChatInfo"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
            r4.setText(r8)
            r4.setTextColor(r5)
            r8 = 1096810496(0x41600000, float:14.0)
            r4.setTextSize(r7, r8)
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 == 0) goto L_0x00c0
            r11 = 5
            goto L_0x00c1
        L_0x00c0:
            r11 = 3
        L_0x00c1:
            r11 = r11 | 48
            r4.setGravity(r11)
            android.view.ViewGroup r11 = r0.containerView
            r12 = -2
            r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x00d0
            goto L_0x00d1
        L_0x00d0:
            r9 = 3
        L_0x00d1:
            r14 = r9 | 48
            r15 = 1103101952(0x41CLASSNAME, float:24.0)
            r16 = 1115160576(0x42780000, float:62.0)
            r17 = 1103101952(0x41CLASSNAME, float:24.0)
            r18 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r11.addView(r4, r9)
            android.widget.TextView[] r4 = new android.widget.TextView[r10]
            r0.titles = r4
            androidx.viewpager.widget.ViewPager r4 = new androidx.viewpager.widget.ViewPager
            r4.<init>(r1)
            r0.viewPager = r4
            r4.setClipChildren(r2)
            androidx.viewpager.widget.ViewPager r4 = r0.viewPager
            r9 = 4
            r4.setOffscreenPageLimit(r9)
            androidx.viewpager.widget.ViewPager r4 = r0.viewPager
            r4.setClipToPadding(r2)
            androidx.viewpager.widget.ViewPager r4 = r0.viewPager
            r9 = 2130706432(0x7var_, float:1.7014118E38)
            org.telegram.messenger.AndroidUtilities.setViewPagerEdgeEffectColor(r4, r9)
            androidx.viewpager.widget.ViewPager r4 = r0.viewPager
            org.telegram.ui.Components.GroupCallRecordAlert$Adapter r9 = new org.telegram.ui.Components.GroupCallRecordAlert$Adapter
            r10 = 0
            r9.<init>()
            r4.setAdapter(r9)
            androidx.viewpager.widget.ViewPager r4 = r0.viewPager
            r4.setPageMargin(r2)
            android.view.ViewGroup r4 = r0.containerView
            androidx.viewpager.widget.ViewPager r9 = r0.viewPager
            r10 = -1
            r11 = -1082130432(0xffffffffbvar_, float:-1.0)
            r12 = 1
            r13 = 0
            r14 = 1120403456(0x42CLASSNAME, float:100.0)
            r15 = 0
            r16 = 1124204544(0x43020000, float:130.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r4.addView(r9, r10)
            androidx.viewpager.widget.ViewPager r4 = r0.viewPager
            org.telegram.ui.Components.GroupCallRecordAlert$2 r9 = new org.telegram.ui.Components.GroupCallRecordAlert$2
            r9.<init>()
            r4.addOnPageChangeListener(r9)
            android.view.View r4 = new android.view.View
            android.content.Context r9 = r19.getContext()
            r4.<init>(r9)
            android.graphics.drawable.GradientDrawable r9 = new android.graphics.drawable.GradientDrawable
            android.graphics.drawable.GradientDrawable$Orientation r10 = android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT
            r11 = 2
            int[] r12 = new int[r11]
            r12[r2] = r3
            r12[r7] = r2
            r9.<init>(r10, r12)
            r4.setBackground(r9)
            android.view.ViewGroup r9 = r0.containerView
            r12 = 120(0x78, float:1.68E-43)
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r14 = 51
            r16 = 1120403456(0x42CLASSNAME, float:100.0)
            r17 = 0
            r18 = 1124204544(0x43020000, float:130.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r9.addView(r4, r10)
            android.view.View r4 = new android.view.View
            android.content.Context r9 = r19.getContext()
            r4.<init>(r9)
            android.graphics.drawable.GradientDrawable r9 = new android.graphics.drawable.GradientDrawable
            android.graphics.drawable.GradientDrawable$Orientation r10 = android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT
            int[] r11 = new int[r11]
            r11[r2] = r2
            r11[r7] = r3
            r9.<init>(r10, r11)
            r4.setBackground(r9)
            android.view.ViewGroup r3 = r0.containerView
            r9 = 120(0x78, float:1.68E-43)
            r10 = -1082130432(0xffffffffbvar_, float:-1.0)
            r11 = 53
            r12 = 0
            r13 = 1120403456(0x42CLASSNAME, float:100.0)
            r14 = 0
            r15 = 1124204544(0x43020000, float:130.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r3.addView(r4, r9)
            org.telegram.ui.Components.GroupCallRecordAlert$3 r3 = new org.telegram.ui.Components.GroupCallRecordAlert$3
            android.content.Context r4 = r19.getContext()
            r3.<init>(r4)
            r0.positiveButton = r3
            r4 = 1115684864(0x42800000, float:64.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.setMinWidth(r4)
            android.widget.TextView r3 = r0.positiveButton
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            r3.setTag(r4)
            android.widget.TextView r3 = r0.positiveButton
            r3.setTextSize(r7, r8)
            android.widget.TextView r3 = r0.positiveButton
            java.lang.String r4 = "voipgroup_nameText"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setTextColor(r8)
            android.widget.TextView r3 = r0.positiveButton
            r8 = 17
            r3.setGravity(r8)
            android.widget.TextView r3 = r0.positiveButton
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r3.setTypeface(r8)
            android.widget.TextView r3 = r0.positiveButton
            r8 = 2131628549(0x7f0e1205, float:1.8884394E38)
            java.lang.String r9 = "VoipRecordStart"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r3.setText(r8)
            int r3 = android.os.Build.VERSION.SDK_INT
            r8 = 23
            if (r3 < r8) goto L_0x01f8
            android.widget.TextView r3 = r0.positiveButton
            r8 = 1086324736(0x40CLASSNAME, float:6.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r9 = 76
            int r4 = androidx.core.graphics.ColorUtils.setAlphaComponent(r4, r9)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r8, r2, r4)
            r3.setForeground(r4)
        L_0x01f8:
            android.widget.TextView r3 = r0.positiveButton
            r4 = 1094713344(0x41400000, float:12.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.setPadding(r2, r8, r2, r9)
            android.widget.TextView r3 = r0.positiveButton
            org.telegram.ui.Components.GroupCallRecordAlert$$ExternalSyntheticLambda0 r8 = new org.telegram.ui.Components.GroupCallRecordAlert$$ExternalSyntheticLambda0
            r8.<init>(r0)
            r3.setOnClickListener(r8)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.TextView r8 = r0.positiveButton
            r9 = -1
            r10 = 1111490560(0x42400000, float:48.0)
            r11 = 80
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 1115684864(0x42800000, float:64.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r3.addView(r8, r9)
            android.widget.LinearLayout r3 = new android.widget.LinearLayout
            r3.<init>(r1)
            r0.titlesLayout = r3
            android.view.ViewGroup r8 = r0.containerView
            r9 = 64
            r10 = 80
            r11 = -2
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r9, r10)
            r8.addView(r3, r9)
            r3 = 0
        L_0x023c:
            android.widget.TextView[] r8 = r0.titles
            int r9 = r8.length
            if (r3 >= r9) goto L_0x02d5
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r1)
            r8[r3] = r9
            android.widget.TextView[] r8 = r0.titles
            r8 = r8[r3]
            r8.setTextSize(r7, r4)
            android.widget.TextView[] r8 = r0.titles
            r8 = r8[r3]
            r8.setTextColor(r5)
            android.widget.TextView[] r8 = r0.titles
            r8 = r8[r3]
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r6)
            r8.setTypeface(r9)
            android.widget.TextView[] r8 = r0.titles
            r8 = r8[r3]
            r9 = 1092616192(0x41200000, float:10.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r8.setPadding(r10, r2, r9, r2)
            android.widget.TextView[] r8 = r0.titles
            r8 = r8[r3]
            r9 = 16
            r8.setGravity(r9)
            android.widget.TextView[] r8 = r0.titles
            r8 = r8[r3]
            r8.setSingleLine(r7)
            android.widget.LinearLayout r8 = r0.titlesLayout
            android.widget.TextView[] r9 = r0.titles
            r9 = r9[r3]
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r5)
            r8.addView(r9, r10)
            if (r3 != 0) goto L_0x02a2
            android.widget.TextView[] r8 = r0.titles
            r8 = r8[r3]
            r9 = 2131628546(0x7f0e1202, float:1.8884388E38)
            java.lang.String r10 = "VoipRecordAudio"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setText(r9)
            goto L_0x02c5
        L_0x02a2:
            if (r3 != r7) goto L_0x02b5
            android.widget.TextView[] r8 = r0.titles
            r8 = r8[r3]
            r9 = 2131628548(0x7f0e1204, float:1.8884392E38)
            java.lang.String r10 = "VoipRecordPortrait"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setText(r9)
            goto L_0x02c5
        L_0x02b5:
            android.widget.TextView[] r8 = r0.titles
            r8 = r8[r3]
            r9 = 2131628547(0x7f0e1203, float:1.888439E38)
            java.lang.String r10 = "VoipRecordLandscape"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setText(r9)
        L_0x02c5:
            android.widget.TextView[] r8 = r0.titles
            r8 = r8[r3]
            org.telegram.ui.Components.GroupCallRecordAlert$$ExternalSyntheticLambda1 r9 = new org.telegram.ui.Components.GroupCallRecordAlert$$ExternalSyntheticLambda1
            r9.<init>(r0, r3)
            r8.setOnClickListener(r9)
            int r3 = r3 + 1
            goto L_0x023c
        L_0x02d5:
            if (r22 == 0) goto L_0x02dc
            androidx.viewpager.widget.ViewPager r1 = r0.viewPager
            r1.setCurrentItem(r7)
        L_0x02dc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallRecordAlert.<init>(android.content.Context, org.telegram.tgnet.TLRPC$Chat, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        onStartRecord(this.currentPage);
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(int i, View view) {
        this.viewPager.setCurrentItem(i, true);
    }

    /* access modifiers changed from: private */
    public void updateTitlesLayout() {
        TextView[] textViewArr = this.titles;
        int i = this.currentPage;
        TextView textView = textViewArr[i];
        TextView textView2 = i < textViewArr.length + -1 ? textViewArr[i + 1] : null;
        int measuredWidth = this.containerView.getMeasuredWidth() / 2;
        float left = (float) (textView.getLeft() + (textView.getMeasuredWidth() / 2));
        float measuredWidth2 = ((float) (this.containerView.getMeasuredWidth() / 2)) - left;
        if (textView2 != null) {
            measuredWidth2 -= (((float) (textView2.getLeft() + (textView2.getMeasuredWidth() / 2))) - left) * this.pageOffset;
        }
        int i2 = 0;
        while (true) {
            TextView[] textViewArr2 = this.titles;
            if (i2 < textViewArr2.length) {
                int i3 = this.currentPage;
                float f = 0.9f;
                float f2 = 0.7f;
                if (i2 >= i3 && i2 <= i3 + 1) {
                    if (i2 == i3) {
                        float f3 = this.pageOffset;
                        f2 = 1.0f - (0.3f * f3);
                        f = 1.0f - (f3 * 0.1f);
                    } else {
                        float f4 = this.pageOffset;
                        f2 = 0.7f + (0.3f * f4);
                        f = 0.9f + (f4 * 0.1f);
                    }
                }
                textViewArr2[i2].setAlpha(f2);
                this.titles[i2].setScaleX(f);
                this.titles[i2].setScaleY(f);
                i2++;
            } else {
                this.titlesLayout.setTranslationX(measuredWidth2);
                this.positiveButton.invalidate();
                return;
            }
        }
    }

    private class Adapter extends PagerAdapter {
        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }

        public Parcelable saveState() {
            return null;
        }

        private Adapter() {
        }

        public int getCount() {
            return GroupCallRecordAlert.this.titles.length;
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            ImageView imageView = new ImageView(GroupCallRecordAlert.this.getContext());
            imageView.setTag(Integer.valueOf(i));
            imageView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(200.0f), -1));
            SvgHelper.SvgDrawable drawable = SvgHelper.getDrawable(RLottieDrawable.readRes((File) null, i == 0 ? NUM : i == 1 ? NUM : NUM));
            drawable.setAspectFill(false);
            imageView.setImageDrawable(drawable);
            if (imageView.getParent() != null) {
                ((ViewGroup) imageView.getParent()).removeView(imageView);
            }
            viewGroup.addView(imageView, 0);
            return imageView;
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
            super.setPrimaryItem(viewGroup, i, obj);
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view.equals(obj);
        }
    }
}
