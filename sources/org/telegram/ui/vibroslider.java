package org.telegram.ui;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PopupSwipeBackLayout;
import org.telegram.ui.Components.Switch;

public class vibroslider extends BaseFragment {
    private int amplitude1 = 50;
    private int amplitude2 = 50;
    private int amplitude3 = 50;
    private long duration1 = 50;
    private long duration2 = 50;
    private long duration3 = 50;

    public View createView(Context context) {
        FrameLayout fragmentView = new FrameLayout(context);
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(1);
        fragmentView.addView(ll, LayoutHelper.createFrame(-1, -1.0f, 48, 16.0f, 16.0f, 16.0f, 16.0f));
        ll.addView(new Slider(this, context, 0, 100, 50, new vibroslider$$ExternalSyntheticLambda2(this)), LayoutHelper.createLinear(-1, -2));
        Context context2 = context;
        ll.addView(new Slider(this, context2, 0, 100, 50, new vibroslider$$ExternalSyntheticLambda3(this)), LayoutHelper.createLinear(-1, -2));
        ll.addView(new Slider(this, context2, 0, 100, 50, new vibroslider$$ExternalSyntheticLambda4(this)), LayoutHelper.createLinear(-1, -2));
        ll.addView(new Slider(this, context2, 0, 255, 50, new vibroslider$$ExternalSyntheticLambda5(this)), LayoutHelper.createLinear(-1, -2));
        ll.addView(new Slider(this, context2, 0, 255, 50, new vibroslider$$ExternalSyntheticLambda6(this)), LayoutHelper.createLinear(-1, -2));
        ll.addView(new Slider(this, context2, 0, 255, 50, new vibroslider$$ExternalSyntheticLambda7(this)), LayoutHelper.createLinear(-1, -2));
        FrameLayout button = new FrameLayout(context);
        button.setBackground(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 4.0f));
        button.setOnClickListener(new vibroslider$$ExternalSyntheticLambda0(context));
        ll.addView(button, LayoutHelper.createLinear(-1, 48, 4.0f, 80.0f, 4.0f, 4.0f));
        Switch switchView = new Switch(context);
        switchView.setOnClickListener(new vibroslider$$ExternalSyntheticLambda1(switchView));
        ll.addView(switchView);
        return fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-vibroslider  reason: not valid java name */
    public /* synthetic */ void m4857lambda$createView$0$orgtelegramuivibroslider(int a) {
        this.duration1 = (long) a;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-vibroslider  reason: not valid java name */
    public /* synthetic */ void m4858lambda$createView$1$orgtelegramuivibroslider(int a) {
        this.duration2 = (long) a;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-vibroslider  reason: not valid java name */
    public /* synthetic */ void m4859lambda$createView$2$orgtelegramuivibroslider(int a) {
        this.duration3 = (long) a;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-vibroslider  reason: not valid java name */
    public /* synthetic */ void m4860lambda$createView$3$orgtelegramuivibroslider(int a) {
        this.amplitude1 = a;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-vibroslider  reason: not valid java name */
    public /* synthetic */ void m4861lambda$createView$4$orgtelegramuivibroslider(int a) {
        this.amplitude2 = a;
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-vibroslider  reason: not valid java name */
    public /* synthetic */ void m4862lambda$createView$5$orgtelegramuivibroslider(int a) {
        this.amplitude3 = a;
    }

    static /* synthetic */ void lambda$createView$6(Context context, View e) {
        if (Build.VERSION.SDK_INT >= 26) {
            Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
            VibrationEffect vibrationEffect = VibrationEffect.createWaveform(new long[]{100, 20, 10}, new int[]{5, 0, 255}, -1);
            vibrator.cancel();
            vibrator.vibrate(vibrationEffect);
        }
    }

    public class Slider extends FrameLayout {
        private int max;
        private int min;
        private PopupSwipeBackLayout.IntCallback onChange;
        final /* synthetic */ vibroslider this$0;
        /* access modifiers changed from: private */
        public int value;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public Slider(org.telegram.ui.vibroslider r24, android.content.Context r25, int r26, int r27, int r28, org.telegram.ui.Components.PopupSwipeBackLayout.IntCallback r29) {
            /*
                r23 = this;
                r7 = r23
                r8 = r25
                r9 = r26
                r10 = r27
                r11 = r24
                r7.this$0 = r11
                r7.<init>(r8)
                r7.min = r9
                r7.max = r10
                r12 = r28
                r7.value = r12
                r13 = r29
                r7.onChange = r13
                android.widget.TextView r0 = new android.widget.TextView
                r0.<init>(r8)
                r14 = r0
                java.lang.String r0 = "fonts/rmedium.ttf"
                android.graphics.Typeface r0 = org.telegram.messenger.AndroidUtilities.getTypeface(r0)
                r14.setTypeface(r0)
                java.lang.String r0 = "windowBackgroundWhiteBlackText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r14.setTextColor(r0)
                r0 = -2
                r1 = 19
                android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r0, (int) r0, (int) r1)
                r7.addView(r14, r0)
                org.telegram.ui.Components.SeekBarView r0 = new org.telegram.ui.Components.SeekBarView
                r0.<init>(r8)
                r15 = r0
                r0 = 1
                r15.setReportChanges(r0)
                org.telegram.ui.vibroslider$Slider$1 r6 = new org.telegram.ui.vibroslider$Slider$1
                r0 = r6
                r1 = r23
                r2 = r24
                r3 = r26
                r4 = r27
                r5 = r14
                r8 = r6
                r6 = r29
                r0.<init>(r2, r3, r4, r5, r6)
                r15.setDelegate(r8)
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                int r1 = r7.value
                r0.append(r1)
                java.lang.String r1 = ""
                r0.append(r1)
                java.lang.String r0 = r0.toString()
                r14.setText(r0)
                int r0 = r7.value
                int r0 = r0 - r9
                float r0 = (float) r0
                int r1 = r10 - r9
                float r1 = (float) r1
                float r0 = r0 / r1
                r15.setProgress(r0)
                r16 = -1
                r17 = 1108869120(0x42180000, float:38.0)
                r18 = 23
                r19 = 1103101952(0x41CLASSNAME, float:24.0)
                r20 = 0
                r21 = 0
                r22 = 0
                android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
                r7.addView(r15, r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.vibroslider.Slider.<init>(org.telegram.ui.vibroslider, android.content.Context, int, int, int, org.telegram.ui.Components.PopupSwipeBackLayout$IntCallback):void");
        }
    }
}
