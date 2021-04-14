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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.voip.VoIPButtonsLayout;
import org.telegram.ui.Components.voip.VoIPToggleButton;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;

public class GroupCallPipAlertView extends LinearLayout implements VoIPBaseService.StateListener, NotificationCenter.NotificationCenterDelegate {
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

    public /* synthetic */ void onCameraSwitch(boolean z) {
        VoIPBaseService.StateListener.CC.$default$onCameraSwitch(this, z);
    }

    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPBaseService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPBaseService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPBaseService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPBaseService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCallPipAlertView(Context context, int i) {
        super(context);
        Context context2 = context;
        setOrientation(1);
        this.currentAccount = i;
        this.paint.setAlpha(234);
        AnonymousClass1 r2 = new FrameLayout(this, context2) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                if (Build.VERSION.SDK_INT >= 21) {
                    accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString("VoipGroupOpenVoiceChat", NUM)));
                }
            }
        };
        this.groupInfoContainer = r2;
        r2.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(22.0f));
        this.groupInfoContainer.addView(this.avatarImageView, LayoutHelper.createFrame(44, 44.0f));
        this.groupInfoContainer.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(-1, 76)));
        this.groupInfoContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCallPipAlertView.this.lambda$new$0$GroupCallPipAlertView(view);
            }
        });
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
        this.soundButton.setOnClickListener(new View.OnClickListener(context2) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                GroupCallPipAlertView.this.lambda$new$1$GroupCallPipAlertView(this.f$1, view);
            }
        });
        this.soundButton.setCheckable(true);
        this.soundButton.setBackgroundColor(ColorUtils.setAlphaComponent(-1, 38), ColorUtils.setAlphaComponent(-1, 76));
        VoIPToggleButton voIPToggleButton2 = new VoIPToggleButton(context2, 44.0f);
        this.muteButton = voIPToggleButton2;
        voIPToggleButton2.setTextSize(12);
        this.muteButton.setOnClickListener(new View.OnClickListener(context2) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                GroupCallPipAlertView.this.lambda$new$2$GroupCallPipAlertView(this.f$1, view);
            }
        });
        VoIPToggleButton voIPToggleButton3 = new VoIPToggleButton(context2, 44.0f);
        this.leaveButton = voIPToggleButton3;
        voIPToggleButton3.setTextSize(12);
        this.leaveButton.setData(NUM, -1, -3257782, 0.3f, false, LocaleController.getString("VoipGroupLeave", NUM), false, false);
        this.leaveButton.setOnClickListener(new View.OnClickListener(context2) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                GroupCallPipAlertView.this.lambda$new$4$GroupCallPipAlertView(this.f$1, view);
            }
        });
        VoIPButtonsLayout voIPButtonsLayout = new VoIPButtonsLayout(context2);
        voIPButtonsLayout.setChildSize(68);
        voIPButtonsLayout.setUseStartPadding(false);
        voIPButtonsLayout.addView(this.soundButton, LayoutHelper.createFrame(68, 63.0f));
        voIPButtonsLayout.addView(this.muteButton, LayoutHelper.createFrame(68, 63.0f));
        voIPButtonsLayout.addView(this.leaveButton, LayoutHelper.createFrame(68, 63.0f));
        setWillNotDraw(false);
        addView(voIPButtonsLayout, LayoutHelper.createLinear(-1, -2, 0, 6, 0, 6, 0));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$GroupCallPipAlertView(View view) {
        if (VoIPService.getSharedInstance() != null) {
            Intent action = new Intent(getContext(), LaunchActivity.class).setAction("voip_chat");
            action.putExtra("currentAccount", VoIPService.getSharedInstance().getAccount());
            getContext().startActivity(action);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$GroupCallPipAlertView(Context context, View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(getContext(), Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$GroupCallPipAlertView(Context context, View view) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$GroupCallPipAlertView(Context context, View view) {
        GroupCallActivity.onLeaveClick(getContext(), new Runnable(context) {
            public final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                GroupCallPip.updateVisibility(this.f$0);
            }
        }, Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x006c  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x01c3  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x01cc  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0207  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x022a  */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r28) {
        /*
            r27 = this;
            r0 = r27
            r1 = r28
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
            int r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r2 < 0) goto L_0x0035
            r0.muteProgress = r6
        L_0x0035:
            r0.invalidateGradient = r4
            r27.invalidate()
            goto L_0x0051
        L_0x003b:
            if (r2 != 0) goto L_0x0051
            float r2 = r0.muteProgress
            int r8 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r8 == 0) goto L_0x0051
            float r2 = r2 - r5
            r0.muteProgress = r2
            int r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r2 >= 0) goto L_0x004c
            r0.muteProgress = r7
        L_0x004c:
            r0.invalidateGradient = r4
            r27.invalidate()
        L_0x0051:
            boolean r2 = r0.mutedByAdmin
            if (r2 == 0) goto L_0x006a
            float r8 = r0.mutedByAdminProgress
            int r9 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r9 == 0) goto L_0x006a
            float r8 = r8 + r5
            r0.mutedByAdminProgress = r8
            int r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r2 < 0) goto L_0x0064
            r0.mutedByAdminProgress = r6
        L_0x0064:
            r0.invalidateGradient = r4
            r27.invalidate()
            goto L_0x0080
        L_0x006a:
            if (r2 != 0) goto L_0x0080
            float r2 = r0.mutedByAdminProgress
            int r8 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r8 == 0) goto L_0x0080
            float r2 = r2 - r5
            r0.mutedByAdminProgress = r2
            int r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r2 >= 0) goto L_0x007b
            r0.mutedByAdminProgress = r7
        L_0x007b:
            r0.invalidateGradient = r4
            r27.invalidate()
        L_0x0080:
            boolean r2 = r0.invalidateGradient
            r5 = 2
            if (r2 == 0) goto L_0x0196
            java.lang.String r2 = "voipgroup_overlayAlertGradientMuted"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r8 = "voipgroup_overlayAlertGradientUnmuted"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            float r9 = r0.muteProgress
            float r9 = r6 - r9
            int r2 = androidx.core.graphics.ColorUtils.blendARGB(r2, r8, r9)
            java.lang.String r8 = "voipgroup_overlayAlertGradientMuted2"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            java.lang.String r9 = "voipgroup_overlayAlertGradientUnmuted2"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            float r10 = r0.muteProgress
            float r6 = r6 - r10
            int r6 = androidx.core.graphics.ColorUtils.blendARGB(r8, r9, r6)
            java.lang.String r8 = "voipgroup_overlayAlertMutedByAdmin"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            float r9 = r0.mutedByAdminProgress
            int r2 = androidx.core.graphics.ColorUtils.blendARGB(r2, r8, r9)
            java.lang.String r8 = "kvoipgroup_overlayAlertMutedByAdmin2"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            float r9 = r0.mutedByAdminProgress
            int r6 = androidx.core.graphics.ColorUtils.blendARGB(r6, r8, r9)
            r0.invalidateGradient = r3
            int r8 = r0.position
            r9 = 1073741824(0x40000000, float:2.0)
            r10 = 1114636288(0x42700000, float:60.0)
            if (r8 != 0) goto L_0x00fe
            android.graphics.LinearGradient r8 = new android.graphics.LinearGradient
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = -r10
            float r12 = (float) r10
            float r10 = r0.cy
            float r11 = r27.getTranslationY()
            float r13 = r10 - r11
            int r10 = r27.getMeasuredWidth()
            float r14 = (float) r10
            int r10 = r27.getMeasuredHeight()
            float r10 = (float) r10
            float r15 = r10 / r9
            int[] r9 = new int[r5]
            r9[r3] = r2
            r9[r4] = r6
            r17 = 0
            android.graphics.Shader$TileMode r18 = android.graphics.Shader.TileMode.CLAMP
            r11 = r8
            r16 = r9
            r11.<init>(r12, r13, r14, r15, r16, r17, r18)
            r0.linearGradient = r8
            goto L_0x0196
        L_0x00fe:
            if (r8 != r4) goto L_0x0133
            android.graphics.LinearGradient r8 = new android.graphics.LinearGradient
            r20 = 0
            int r11 = r27.getMeasuredHeight()
            float r11 = (float) r11
            float r21 = r11 / r9
            int r9 = r27.getMeasuredWidth()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 + r10
            float r9 = (float) r9
            float r10 = r0.cy
            float r11 = r27.getTranslationY()
            float r23 = r10 - r11
            int[] r10 = new int[r5]
            r10[r3] = r6
            r10[r4] = r2
            r25 = 0
            android.graphics.Shader$TileMode r26 = android.graphics.Shader.TileMode.CLAMP
            r19 = r8
            r22 = r9
            r24 = r10
            r19.<init>(r20, r21, r22, r23, r24, r25, r26)
            r0.linearGradient = r8
            goto L_0x0196
        L_0x0133:
            if (r8 != r5) goto L_0x0164
            android.graphics.LinearGradient r8 = new android.graphics.LinearGradient
            float r11 = r0.cx
            float r12 = r27.getTranslationX()
            float r12 = r11 - r12
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r10 = -r10
            float r13 = (float) r10
            int r10 = r27.getMeasuredWidth()
            float r10 = (float) r10
            float r14 = r10 / r9
            int r9 = r27.getMeasuredHeight()
            float r15 = (float) r9
            int[] r9 = new int[r5]
            r9[r3] = r2
            r9[r4] = r6
            r17 = 0
            android.graphics.Shader$TileMode r18 = android.graphics.Shader.TileMode.CLAMP
            r11 = r8
            r16 = r9
            r11.<init>(r12, r13, r14, r15, r16, r17, r18)
            r0.linearGradient = r8
            goto L_0x0196
        L_0x0164:
            android.graphics.LinearGradient r8 = new android.graphics.LinearGradient
            int r11 = r27.getMeasuredWidth()
            float r11 = (float) r11
            float r20 = r11 / r9
            r21 = 0
            float r9 = r0.cx
            float r11 = r27.getTranslationX()
            float r22 = r9 - r11
            int r9 = r27.getMeasuredHeight()
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = r9 + r10
            float r9 = (float) r9
            int[] r10 = new int[r5]
            r10[r3] = r6
            r10[r4] = r2
            r25 = 0
            android.graphics.Shader$TileMode r26 = android.graphics.Shader.TileMode.CLAMP
            r19 = r8
            r23 = r9
            r24 = r10
            r19.<init>(r20, r21, r22, r23, r24, r25, r26)
            r0.linearGradient = r8
        L_0x0196:
            android.graphics.RectF r2 = r0.rectF
            int r3 = r27.getMeasuredWidth()
            float r3 = (float) r3
            int r6 = r27.getMeasuredHeight()
            float r6 = (float) r6
            r2.set(r7, r7, r3, r6)
            android.graphics.Paint r2 = r0.paint
            android.graphics.LinearGradient r3 = r0.linearGradient
            r2.setShader(r3)
            android.graphics.RectF r2 = r0.rectF
            r3 = 1092616192(0x41200000, float:10.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r6 = (float) r6
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r8 = (float) r8
            android.graphics.Paint r9 = r0.paint
            r1.drawRoundRect(r2, r6, r8, r9)
            int r2 = r0.position
            if (r2 != 0) goto L_0x01cc
            float r2 = r0.cy
            float r6 = r27.getTranslationY()
            float r2 = r2 - r6
            r6 = 0
            goto L_0x01f4
        L_0x01cc:
            if (r2 != r4) goto L_0x01db
            float r2 = r0.cy
            float r6 = r27.getTranslationY()
            float r2 = r2 - r6
            int r6 = r27.getMeasuredWidth()
            float r6 = (float) r6
            goto L_0x01f4
        L_0x01db:
            if (r2 != r5) goto L_0x01e7
            float r2 = r0.cx
            float r6 = r27.getTranslationX()
            float r6 = r2 - r6
            r2 = 0
            goto L_0x01f4
        L_0x01e7:
            float r2 = r0.cx
            float r6 = r27.getTranslationX()
            float r6 = r2 - r6
            int r2 = r27.getMeasuredHeight()
            float r2 = (float) r2
        L_0x01f4:
            r0.setPivotX(r6)
            r0.setPivotY(r2)
            r28.save()
            int r8 = r0.position
            r9 = 1110704128(0x42340000, float:45.0)
            r10 = 1077936128(0x40400000, float:3.0)
            r11 = 1097859072(0x41700000, float:15.0)
            if (r8 != 0) goto L_0x022a
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            float r4 = r6 - r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r5 = (float) r5
            float r5 = r2 - r5
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r8 = (float) r8
            float r8 = r8 + r2
            r1.clipRect(r4, r5, r6, r8)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r4 = (float) r4
            r1.translate(r4, r7)
            r1.rotate(r9, r6, r2)
            goto L_0x0296
        L_0x022a:
            if (r8 != r4) goto L_0x024f
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            float r4 = r2 - r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r5 = (float) r5
            float r5 = r5 + r6
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r8 = (float) r8
            float r8 = r8 + r2
            r1.clipRect(r6, r4, r5, r8)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r4 = -r4
            float r4 = (float) r4
            r1.translate(r4, r7)
            r1.rotate(r9, r6, r2)
            goto L_0x0296
        L_0x024f:
            if (r8 != r5) goto L_0x0274
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            float r4 = r6 - r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r5 = (float) r5
            float r5 = r2 - r5
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r8 = (float) r8
            float r8 = r8 + r6
            r1.clipRect(r4, r5, r8, r2)
            r1.rotate(r9, r6, r2)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r4 = (float) r4
            r1.translate(r7, r4)
            goto L_0x0296
        L_0x0274:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r4 = (float) r4
            float r4 = r6 - r4
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r5 = (float) r5
            float r5 = r5 + r6
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r8 = (float) r8
            float r8 = r8 + r2
            r1.clipRect(r4, r2, r5, r8)
            r1.rotate(r9, r6, r2)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r4 = -r4
            float r4 = (float) r4
            r1.translate(r7, r4)
        L_0x0296:
            android.graphics.RectF r4 = r0.rectF
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r5 = (float) r5
            float r5 = r6 - r5
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r7 = (float) r7
            float r7 = r2 - r7
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r8 = (float) r8
            float r6 = r6 + r8
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r2 = r2 + r3
            r4.set(r5, r7, r6, r2)
            android.graphics.RectF r2 = r0.rectF
            r3 = 1082130432(0x40800000, float:4.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r4 = (float) r4
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            android.graphics.Paint r5 = r0.paint
            r1.drawRoundRect(r2, r4, r3, r5)
            r28.restore()
            super.onDraw(r28)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallPipAlertView.onDraw(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(230.0f), NUM), i2);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        String str;
        super.onAttachedToWindow();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (!(sharedInstance == null || sharedInstance.groupCall == null)) {
            int colorForId = AvatarDrawable.getColorForId(sharedInstance.getChat().id);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setColor(colorForId);
            avatarDrawable.setInfo(sharedInstance.getChat());
            this.avatarImageView.setImage(ImageLocation.getForLocal(sharedInstance.getChat().photo.photo_small), "50_50", (Drawable) avatarDrawable, (Object) null);
            if (!TextUtils.isEmpty(sharedInstance.groupCall.call.title)) {
                str = sharedInstance.groupCall.call.title;
            } else {
                str = sharedInstance.getChat().title;
            }
            if (str != null) {
                str = str.replace("\n", " ").replaceAll(" +", " ").trim();
            }
            this.titleView.setText(str);
            updateMembersCount();
            sharedInstance.registerStateListener(this);
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
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.unregisterStateListener(this);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
    }

    private void updateMembersCount() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null && sharedInstance.groupCall != null) {
            int callState = sharedInstance.getCallState();
            if (sharedInstance.isSwitchingStream() || !(callState == 1 || callState == 2 || callState == 6 || callState == 5)) {
                this.subtitleView.setText(LocaleController.formatPluralString("Participants", sharedInstance.groupCall.call.participants_count));
            } else {
                this.subtitleView.setText(LocaleController.getString("VoipGroupConnecting", NUM));
            }
        }
    }

    private void updateButtons(boolean z) {
        VoIPService sharedInstance;
        String str;
        int i;
        if (this.soundButton != null && this.muteButton != null && (sharedInstance = VoIPService.getSharedInstance()) != null) {
            boolean isBluetoothOn = sharedInstance.isBluetoothOn();
            boolean z2 = !isBluetoothOn && sharedInstance.isSpeakerphoneOn();
            this.soundButton.setChecked(z2, z);
            if (isBluetoothOn) {
                this.soundButton.setData(NUM, -1, 0, 0.1f, true, LocaleController.getString("VoipAudioRoutingBluetooth", NUM), false, z);
            } else if (z2) {
                this.soundButton.setData(NUM, -1, 0, 0.3f, true, LocaleController.getString("VoipSpeaker", NUM), false, z);
            } else if (sharedInstance.isHeadsetPlugged()) {
                this.soundButton.setData(NUM, -1, 0, 0.1f, true, LocaleController.getString("VoipAudioRoutingHeadset", NUM), false, z);
            } else {
                this.soundButton.setData(NUM, -1, 0, 0.1f, true, LocaleController.getString("VoipSpeaker", NUM), false, z);
            }
            if (sharedInstance.mutedByAdmin()) {
                this.muteButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 76), 0.1f, true, LocaleController.getString("VoipMutedByAdminShort", NUM), true, z);
            } else {
                VoIPToggleButton voIPToggleButton = this.muteButton;
                int alphaComponent = ColorUtils.setAlphaComponent(-1, (int) ((sharedInstance.isMicMute() ? 0.3f : 0.15f) * 255.0f));
                if (sharedInstance.isMicMute()) {
                    i = NUM;
                    str = "VoipUnmute";
                } else {
                    i = NUM;
                    str = "VoipMute";
                }
                voIPToggleButton.setData(NUM, -1, alphaComponent, 0.1f, true, LocaleController.getString(str, i), sharedInstance.isMicMute(), z);
            }
            invalidate();
        }
    }

    public void onAudioSettingsChanged() {
        updateButtons(true);
    }

    public void onStateChanged(int i) {
        updateMembersCount();
    }

    public void setPosition(int i, float f, float f2) {
        this.position = i;
        this.cx = f;
        this.cy = f2;
        invalidate();
        this.invalidateGradient = true;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        boolean mutedByAdmin2;
        if (i == NotificationCenter.groupCallUpdated) {
            updateMembersCount();
            if (VoIPService.getSharedInstance() != null && (mutedByAdmin2 = VoIPService.getSharedInstance().mutedByAdmin()) != this.mutedByAdmin) {
                this.mutedByAdmin = mutedByAdmin2;
                invalidate();
            }
        }
    }
}
