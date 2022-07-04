package org.telegram.ui.Components;

import android.content.Context;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.voip.VoIPButtonsLayout;
import org.telegram.ui.Components.voip.VoIPToggleButton;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;

public class GroupCallPipAlertView extends LinearLayout implements VoIPService.StateListener, NotificationCenter.NotificationCenterDelegate {
    public static final int POSITION_BOTTOM = 2;
    public static final int POSITION_LEFT = 0;
    public static final int POSITION_RIGHT = 1;
    public static final int POSITION_TOP = 3;
    BackupImageView avatarImageView;
    int currentAccount;
    float cx;
    float cy;
    FrameLayout groupInfoContainer;
    private boolean invalidateGradient = true;
    VoIPToggleButton leaveButton;
    LinearGradient linearGradient;
    VoIPToggleButton muteButton;
    float muteProgress;
    private boolean mutedByAdmin;
    float mutedByAdminProgress;
    Paint paint = new Paint(1);
    private int position;
    RectF rectF = new RectF();
    VoIPToggleButton soundButton;
    TextView subtitleView;
    TextView titleView;

    public /* synthetic */ void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    public /* synthetic */ void onCameraSwitch(boolean z) {
        VoIPService.StateListener.CC.$default$onCameraSwitch(this, z);
    }

    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCallPipAlertView(Context context, int account) {
        super(context);
        Context context2 = context;
        setOrientation(1);
        this.currentAccount = account;
        this.paint.setAlpha(234);
        AnonymousClass1 r4 = new FrameLayout(context2) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                if (Build.VERSION.SDK_INT >= 21) {
                    VoIPService service = VoIPService.getSharedInstance();
                    if (service == null || !ChatObject.isChannelOrGiga(service.getChat())) {
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString("VoipGroupOpenVoiceChat", NUM)));
                    } else {
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString("VoipChannelOpenVoiceChat", NUM)));
                    }
                }
            }
        };
        this.groupInfoContainer = r4;
        r4.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(22.0f));
        this.groupInfoContainer.addView(this.avatarImageView, LayoutHelper.createFrame(44, 44.0f));
        this.groupInfoContainer.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(-1, 76)));
        this.groupInfoContainer.setOnClickListener(new GroupCallPipAlertView$$ExternalSyntheticLambda0(this));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context2);
        this.titleView = textView;
        textView.setTextColor(-1);
        this.titleView.setTextSize(15.0f);
        this.titleView.setMaxLines(2);
        this.titleView.setEllipsize(TextUtils.TruncateAt.END);
        this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(this.titleView, LayoutHelper.createLinear(-1, -2));
        TextView textView2 = new TextView(context2);
        this.subtitleView = textView2;
        textView2.setTextSize(12.0f);
        this.subtitleView.setTextColor(ColorUtils.setAlphaComponent(-1, 153));
        linearLayout.addView(this.subtitleView, LayoutHelper.createLinear(-1, -2));
        this.groupInfoContainer.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 16, 55.0f, 0.0f, 0.0f, 0.0f));
        addView(this.groupInfoContainer, LayoutHelper.createLinear(-1, -2, 0, 10, 10, 10, 10));
        VoIPToggleButton voIPToggleButton = new VoIPToggleButton(context2, 44.0f);
        this.soundButton = voIPToggleButton;
        voIPToggleButton.setTextSize(12);
        this.soundButton.setOnClickListener(new GroupCallPipAlertView$$ExternalSyntheticLambda1(this, context2));
        this.soundButton.setCheckable(true);
        this.soundButton.setBackgroundColor(ColorUtils.setAlphaComponent(-1, 38), ColorUtils.setAlphaComponent(-1, 76));
        VoIPToggleButton voIPToggleButton2 = new VoIPToggleButton(context2, 44.0f);
        this.muteButton = voIPToggleButton2;
        voIPToggleButton2.setTextSize(12);
        this.muteButton.setOnClickListener(new GroupCallPipAlertView$$ExternalSyntheticLambda2(this, context2));
        VoIPToggleButton voIPToggleButton3 = new VoIPToggleButton(context2, 44.0f);
        this.leaveButton = voIPToggleButton3;
        voIPToggleButton3.setTextSize(12);
        this.leaveButton.setData(NUM, -1, -3257782, 0.3f, false, LocaleController.getString("VoipGroupLeave", NUM), false, false);
        this.leaveButton.setOnClickListener(new GroupCallPipAlertView$$ExternalSyntheticLambda3(this, context2));
        VoIPButtonsLayout buttonsContainer = new VoIPButtonsLayout(context2);
        buttonsContainer.setChildSize(68);
        buttonsContainer.setUseStartPadding(false);
        buttonsContainer.addView(this.soundButton, LayoutHelper.createFrame(68, 63.0f));
        buttonsContainer.addView(this.muteButton, LayoutHelper.createFrame(68, 63.0f));
        buttonsContainer.addView(this.leaveButton, LayoutHelper.createFrame(68, 63.0f));
        setWillNotDraw(false);
        addView(buttonsContainer, LayoutHelper.createLinear(-1, -2, 0, 6, 0, 6, 0));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-GroupCallPipAlertView  reason: not valid java name */
    public /* synthetic */ void m1008lambda$new$0$orgtelegramuiComponentsGroupCallPipAlertView(View view) {
        if (VoIPService.getSharedInstance() != null) {
            Intent intent = new Intent(getContext(), LaunchActivity.class).setAction("voip_chat");
            intent.putExtra("currentAccount", VoIPService.getSharedInstance().getAccount());
            getContext().startActivity(intent);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-GroupCallPipAlertView  reason: not valid java name */
    public /* synthetic */ void m1009lambda$new$1$orgtelegramuiComponentsGroupCallPipAlertView(Context context, View v) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(getContext(), Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context));
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-GroupCallPipAlertView  reason: not valid java name */
    public /* synthetic */ void m1010lambda$new$2$orgtelegramuiComponentsGroupCallPipAlertView(Context context, View v) {
        if (VoIPService.getSharedInstance() == null) {
            return;
        }
        if (VoIPService.getSharedInstance().mutedByAdmin()) {
            this.muteButton.shakeView();
            try {
                Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            VoIPService.getSharedInstance().setMicMute(!VoIPService.getSharedInstance().isMicMute(), false, true);
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-GroupCallPipAlertView  reason: not valid java name */
    public /* synthetic */ void m1011lambda$new$4$orgtelegramuiComponentsGroupCallPipAlertView(Context context, View v) {
        GroupCallActivity.onLeaveClick(getContext(), new GroupCallPipAlertView$$ExternalSyntheticLambda4(context), Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x006c  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x01c5  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x01ce  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0209  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x022c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r29) {
        /*
            r28 = this;
            r0 = r28
            r1 = r29
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            r3 = 0
            r4 = 1
            if (r2 == 0) goto L_0x001d
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r2 = r2.isMicMute()
            if (r2 != 0) goto L_0x001d
            boolean r2 = r0.mutedByAdmin
            if (r2 == 0) goto L_0x001b
            goto L_0x001d
        L_0x001b:
            r2 = 0
            goto L_0x001e
        L_0x001d:
            r2 = 1
        L_0x001e:
            r5 = 1037726734(0x3dda740e, float:0.10666667)
            r6 = 1065353216(0x3var_, float:1.0)
            r7 = 0
            if (r2 == 0) goto L_0x003b
            float r8 = r0.muteProgress
            int r9 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r9 == 0) goto L_0x003b
            float r8 = r8 + r5
            r0.muteProgress = r8
            int r8 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r8 < 0) goto L_0x0035
            r0.muteProgress = r6
        L_0x0035:
            r0.invalidateGradient = r4
            r28.invalidate()
            goto L_0x0051
        L_0x003b:
            if (r2 != 0) goto L_0x0051
            float r8 = r0.muteProgress
            int r9 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x0051
            float r8 = r8 - r5
            r0.muteProgress = r8
            int r8 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r8 >= 0) goto L_0x004c
            r0.muteProgress = r7
        L_0x004c:
            r0.invalidateGradient = r4
            r28.invalidate()
        L_0x0051:
            boolean r8 = r0.mutedByAdmin
            if (r8 == 0) goto L_0x006a
            float r9 = r0.mutedByAdminProgress
            int r10 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r10 == 0) goto L_0x006a
            float r9 = r9 + r5
            r0.mutedByAdminProgress = r9
            int r5 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r5 < 0) goto L_0x0064
            r0.mutedByAdminProgress = r6
        L_0x0064:
            r0.invalidateGradient = r4
            r28.invalidate()
            goto L_0x0080
        L_0x006a:
            if (r8 != 0) goto L_0x0080
            float r8 = r0.mutedByAdminProgress
            int r9 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x0080
            float r8 = r8 - r5
            r0.mutedByAdminProgress = r8
            int r5 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r5 >= 0) goto L_0x007b
            r0.mutedByAdminProgress = r7
        L_0x007b:
            r0.invalidateGradient = r4
            r28.invalidate()
        L_0x0080:
            boolean r5 = r0.invalidateGradient
            r8 = 2
            if (r5 == 0) goto L_0x0198
            java.lang.String r5 = "voipgroup_overlayAlertGradientMuted"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            java.lang.String r9 = "voipgroup_overlayAlertGradientUnmuted"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            float r10 = r0.muteProgress
            float r10 = r6 - r10
            int r5 = androidx.core.graphics.ColorUtils.blendARGB(r5, r9, r10)
            java.lang.String r9 = "voipgroup_overlayAlertGradientMuted2"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            java.lang.String r10 = "voipgroup_overlayAlertGradientUnmuted2"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            float r11 = r0.muteProgress
            float r6 = r6 - r11
            int r6 = androidx.core.graphics.ColorUtils.blendARGB(r9, r10, r6)
            java.lang.String r9 = "voipgroup_overlayAlertMutedByAdmin"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            float r10 = r0.mutedByAdminProgress
            int r5 = androidx.core.graphics.ColorUtils.blendARGB(r5, r9, r10)
            java.lang.String r9 = "kvoipgroup_overlayAlertMutedByAdmin2"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            float r10 = r0.mutedByAdminProgress
            int r6 = androidx.core.graphics.ColorUtils.blendARGB(r6, r9, r10)
            r0.invalidateGradient = r3
            int r9 = r0.position
            r10 = 1073741824(0x40000000, float:2.0)
            r11 = 1114636288(0x42700000, float:60.0)
            if (r9 != 0) goto L_0x00fe
            android.graphics.LinearGradient r9 = new android.graphics.LinearGradient
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = -r11
            float r13 = (float) r11
            float r11 = r0.cy
            float r12 = r28.getTranslationY()
            float r14 = r11 - r12
            int r11 = r28.getMeasuredWidth()
            float r15 = (float) r11
            int r11 = r28.getMeasuredHeight()
            float r11 = (float) r11
            float r16 = r11 / r10
            int[] r10 = new int[r8]
            r10[r3] = r5
            r10[r4] = r6
            r18 = 0
            android.graphics.Shader$TileMode r19 = android.graphics.Shader.TileMode.CLAMP
            r12 = r9
            r17 = r10
            r12.<init>(r13, r14, r15, r16, r17, r18, r19)
            r0.linearGradient = r9
            goto L_0x0198
        L_0x00fe:
            if (r9 != r4) goto L_0x0133
            android.graphics.LinearGradient r9 = new android.graphics.LinearGradient
            r21 = 0
            int r12 = r28.getMeasuredHeight()
            float r12 = (float) r12
            float r22 = r12 / r10
            int r10 = r28.getMeasuredWidth()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 + r11
            float r10 = (float) r10
            float r11 = r0.cy
            float r12 = r28.getTranslationY()
            float r24 = r11 - r12
            int[] r11 = new int[r8]
            r11[r3] = r6
            r11[r4] = r5
            r26 = 0
            android.graphics.Shader$TileMode r27 = android.graphics.Shader.TileMode.CLAMP
            r20 = r9
            r23 = r10
            r25 = r11
            r20.<init>(r21, r22, r23, r24, r25, r26, r27)
            r0.linearGradient = r9
            goto L_0x0198
        L_0x0133:
            if (r9 != r8) goto L_0x0166
            android.graphics.LinearGradient r9 = new android.graphics.LinearGradient
            float r12 = r0.cx
            float r13 = r28.getTranslationX()
            float r13 = r12 - r13
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r11 = -r11
            float r14 = (float) r11
            int r11 = r28.getMeasuredWidth()
            float r11 = (float) r11
            float r15 = r11 / r10
            int r10 = r28.getMeasuredHeight()
            float r10 = (float) r10
            int[] r11 = new int[r8]
            r11[r3] = r5
            r11[r4] = r6
            r18 = 0
            android.graphics.Shader$TileMode r19 = android.graphics.Shader.TileMode.CLAMP
            r12 = r9
            r16 = r10
            r17 = r11
            r12.<init>(r13, r14, r15, r16, r17, r18, r19)
            r0.linearGradient = r9
            goto L_0x0198
        L_0x0166:
            android.graphics.LinearGradient r9 = new android.graphics.LinearGradient
            int r12 = r28.getMeasuredWidth()
            float r12 = (float) r12
            float r21 = r12 / r10
            r22 = 0
            float r10 = r0.cx
            float r12 = r28.getTranslationX()
            float r23 = r10 - r12
            int r10 = r28.getMeasuredHeight()
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 + r11
            float r10 = (float) r10
            int[] r11 = new int[r8]
            r11[r3] = r6
            r11[r4] = r5
            r26 = 0
            android.graphics.Shader$TileMode r27 = android.graphics.Shader.TileMode.CLAMP
            r20 = r9
            r24 = r10
            r25 = r11
            r20.<init>(r21, r22, r23, r24, r25, r26, r27)
            r0.linearGradient = r9
        L_0x0198:
            android.graphics.RectF r3 = r0.rectF
            int r5 = r28.getMeasuredWidth()
            float r5 = (float) r5
            int r6 = r28.getMeasuredHeight()
            float r6 = (float) r6
            r3.set(r7, r7, r5, r6)
            android.graphics.Paint r3 = r0.paint
            android.graphics.LinearGradient r5 = r0.linearGradient
            r3.setShader(r5)
            android.graphics.RectF r3 = r0.rectF
            r5 = 1092616192(0x41200000, float:10.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r6 = (float) r6
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r9 = (float) r9
            android.graphics.Paint r10 = r0.paint
            r1.drawRoundRect(r3, r6, r9, r10)
            int r3 = r0.position
            if (r3 != 0) goto L_0x01ce
            float r3 = r0.cy
            float r6 = r28.getTranslationY()
            float r3 = r3 - r6
            r6 = 0
            goto L_0x01f6
        L_0x01ce:
            if (r3 != r4) goto L_0x01dd
            float r3 = r0.cy
            float r6 = r28.getTranslationY()
            float r3 = r3 - r6
            int r6 = r28.getMeasuredWidth()
            float r6 = (float) r6
            goto L_0x01f6
        L_0x01dd:
            if (r3 != r8) goto L_0x01e9
            float r3 = r0.cx
            float r6 = r28.getTranslationX()
            float r6 = r3 - r6
            r3 = 0
            goto L_0x01f6
        L_0x01e9:
            float r3 = r0.cx
            float r6 = r28.getTranslationX()
            float r6 = r3 - r6
            int r3 = r28.getMeasuredHeight()
            float r3 = (float) r3
        L_0x01f6:
            r0.setPivotX(r6)
            r0.setPivotY(r3)
            r29.save()
            int r9 = r0.position
            r10 = 1110704128(0x42340000, float:45.0)
            r11 = 1077936128(0x40400000, float:3.0)
            r12 = 1097859072(0x41700000, float:15.0)
            if (r9 != 0) goto L_0x022c
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r4 = (float) r4
            float r4 = r6 - r4
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r8 = (float) r8
            float r8 = r3 - r8
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r9 = (float) r9
            float r9 = r9 + r3
            r1.clipRect(r4, r8, r6, r9)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            r1.translate(r4, r7)
            r1.rotate(r10, r6, r3)
            goto L_0x0298
        L_0x022c:
            if (r9 != r4) goto L_0x0251
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r4 = (float) r4
            float r4 = r3 - r4
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r8 = (float) r8
            float r8 = r8 + r6
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r9 = (float) r9
            float r9 = r9 + r3
            r1.clipRect(r6, r4, r8, r9)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r4 = -r4
            float r4 = (float) r4
            r1.translate(r4, r7)
            r1.rotate(r10, r6, r3)
            goto L_0x0298
        L_0x0251:
            if (r9 != r8) goto L_0x0276
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r4 = (float) r4
            float r4 = r6 - r4
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r8 = (float) r8
            float r8 = r3 - r8
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r9 = (float) r9
            float r9 = r9 + r6
            r1.clipRect(r4, r8, r9, r3)
            r1.rotate(r10, r6, r3)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            r1.translate(r7, r4)
            goto L_0x0298
        L_0x0276:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r4 = (float) r4
            float r4 = r6 - r4
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r8 = (float) r8
            float r8 = r8 + r6
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r9 = (float) r9
            float r9 = r9 + r3
            r1.clipRect(r4, r3, r8, r9)
            r1.rotate(r10, r6, r3)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r4 = -r4
            float r4 = (float) r4
            r1.translate(r7, r4)
        L_0x0298:
            android.graphics.RectF r4 = r0.rectF
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r7 = (float) r7
            float r7 = r6 - r7
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r8 = (float) r8
            float r8 = r3 - r8
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r9 = (float) r9
            float r9 = r9 + r6
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r5 = r5 + r3
            r4.set(r7, r8, r9, r5)
            android.graphics.RectF r4 = r0.rectF
            r5 = 1082130432(0x40800000, float:4.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r7 = (float) r7
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            android.graphics.Paint r8 = r0.paint
            r1.drawRoundRect(r4, r7, r5, r8)
            r29.restore()
            super.onDraw(r29)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallPipAlertView.onDraw(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(230.0f), NUM), heightMeasureSpec);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        String titleStr;
        super.onAttachedToWindow();
        VoIPService service = VoIPService.getSharedInstance();
        if (!(service == null || service.groupCall == null)) {
            int color2 = AvatarDrawable.getColorForId(service.getChat().id);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setColor(color2);
            avatarDrawable.setInfo(service.getChat());
            this.avatarImageView.setImage(ImageLocation.getForLocal(service.getChat().photo.photo_small), "50_50", (Drawable) avatarDrawable, (Object) null);
            if (!TextUtils.isEmpty(service.groupCall.call.title)) {
                titleStr = service.groupCall.call.title;
            } else {
                titleStr = service.getChat().title;
            }
            if (titleStr != null) {
                titleStr = titleStr.replace("\n", " ").replaceAll(" +", " ").trim();
            }
            this.titleView.setText(titleStr);
            updateMembersCount();
            service.registerStateListener(this);
            if (VoIPService.getSharedInstance() != null) {
                this.mutedByAdmin = VoIPService.getSharedInstance().mutedByAdmin();
            }
            float f = 1.0f;
            this.mutedByAdminProgress = this.mutedByAdmin ? 1.0f : 0.0f;
            if (!(VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute() || this.mutedByAdmin)) {
                f = 0.0f;
            }
            this.muteProgress = f;
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupCallUpdated);
        updateButtons(false);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            service.unregisterStateListener(this);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
    }

    private void updateMembersCount() {
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null && service.groupCall != null) {
            int currentCallState = service.getCallState();
            if (service.isSwitchingStream() || !(currentCallState == 1 || currentCallState == 2 || currentCallState == 6 || currentCallState == 5)) {
                this.subtitleView.setText(LocaleController.formatPluralString(service.groupCall.call.rtmp_stream ? "ViewersWatching" : "Participants", service.groupCall.call.participants_count, new Object[0]));
            } else {
                this.subtitleView.setText(LocaleController.getString("VoipGroupConnecting", NUM));
            }
        }
    }

    private void updateButtons(boolean animated) {
        String str;
        int i;
        if (this.soundButton == null) {
            boolean z = animated;
        } else if (this.muteButton == null) {
            boolean z2 = animated;
        } else {
            VoIPService service = VoIPService.getSharedInstance();
            if (service != null) {
                boolean bluetooth = service.isBluetoothOn();
                boolean checked = !bluetooth && service.isSpeakerphoneOn();
                this.soundButton.setChecked(checked, animated);
                if (bluetooth) {
                    this.soundButton.setData(NUM, -1, 0, 0.1f, true, LocaleController.getString("VoipAudioRoutingBluetooth", NUM), false, animated);
                } else if (checked) {
                    this.soundButton.setData(NUM, -1, 0, 0.3f, true, LocaleController.getString("VoipSpeaker", NUM), false, animated);
                } else if (service.isHeadsetPlugged()) {
                    this.soundButton.setData(NUM, -1, 0, 0.1f, true, LocaleController.getString("VoipAudioRoutingHeadset", NUM), false, animated);
                } else {
                    this.soundButton.setData(NUM, -1, 0, 0.1f, true, LocaleController.getString("VoipSpeaker", NUM), false, animated);
                }
                if (service.mutedByAdmin()) {
                    this.muteButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 76), 0.1f, true, LocaleController.getString("VoipMutedByAdminShort", NUM), true, animated);
                } else {
                    VoIPToggleButton voIPToggleButton = this.muteButton;
                    int alphaComponent = ColorUtils.setAlphaComponent(-1, (int) ((service.isMicMute() ? 0.3f : 0.15f) * 255.0f));
                    if (service.isMicMute()) {
                        i = NUM;
                        str = "VoipUnmute";
                    } else {
                        i = NUM;
                        str = "VoipMute";
                    }
                    voIPToggleButton.setData(NUM, -1, alphaComponent, 0.1f, true, LocaleController.getString(str, i), service.isMicMute(), animated);
                }
                invalidate();
            }
        }
    }

    public void onAudioSettingsChanged() {
        updateButtons(true);
    }

    public void onStateChanged(int state) {
        updateMembersCount();
    }

    public void setPosition(int position2, float cx2, float cy2) {
        this.position = position2;
        this.cx = cx2;
        this.cy = cy2;
        invalidate();
        this.invalidateGradient = true;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        boolean mutedByAdminNew;
        if (id == NotificationCenter.groupCallUpdated) {
            updateMembersCount();
            if (VoIPService.getSharedInstance() != null && (mutedByAdminNew = VoIPService.getSharedInstance().mutedByAdmin()) != this.mutedByAdmin) {
                this.mutedByAdmin = mutedByAdminNew;
                invalidate();
            }
        }
    }
}
