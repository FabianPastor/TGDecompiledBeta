package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
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

    public /* synthetic */ void onStateChanged(int i) {
        VoIPBaseService.StateListener.CC.$default$onStateChanged(this, i);
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
        FrameLayout frameLayout = new FrameLayout(context2);
        this.groupInfoContainer = frameLayout;
        frameLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(22.0f));
        this.groupInfoContainer.addView(this.avatarImageView, LayoutHelper.createFrame(44, 44.0f));
        this.groupInfoContainer.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(-1, 153)));
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
        VoIPToggleButton voIPToggleButton2 = new VoIPToggleButton(context2, 44.0f);
        this.muteButton = voIPToggleButton2;
        voIPToggleButton2.setTextSize(12);
        this.muteButton.setOnClickListener($$Lambda$GroupCallPipAlertView$bhEkrdJgfuKD5XNhbwd0K5pdlUM.INSTANCE);
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
        voIPButtonsLayout.setChildSize(60);
        voIPButtonsLayout.setUseStartPadding(false);
        voIPButtonsLayout.addView(this.soundButton, LayoutHelper.createFrame(60, 63.0f));
        voIPButtonsLayout.addView(this.muteButton, LayoutHelper.createFrame(60, 63.0f));
        voIPButtonsLayout.addView(this.leaveButton, LayoutHelper.createFrame(60, 63.0f));
        setWillNotDraw(false);
        addView(voIPButtonsLayout, LayoutHelper.createLinear(-1, -2, 0, 10, 0, 10, 0));
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

    static /* synthetic */ void lambda$new$2(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().setMicMute(!VoIPService.getSharedInstance().isMicMute(), false);
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
    @SuppressLint({"DrawAllocation"})
    public void onDraw(Canvas canvas) {
        float f;
        float f2;
        Canvas canvas2 = canvas;
        if (this.invalidateGradient) {
            this.invalidateGradient = false;
            int i = this.position;
            if (i == 0) {
                this.linearGradient = new LinearGradient((float) (-AndroidUtilities.dp(60.0f)), this.cy - getTranslationY(), (float) getMeasuredWidth(), ((float) getMeasuredHeight()) / 2.0f, new int[]{-15573658, -13613947}, (float[]) null, Shader.TileMode.CLAMP);
            } else if (i == 1) {
                float measuredHeight = ((float) getMeasuredHeight()) / 2.0f;
                float translationY = this.cy - getTranslationY();
                this.linearGradient = new LinearGradient(0.0f, measuredHeight, (float) (getMeasuredWidth() + AndroidUtilities.dp(60.0f)), translationY, new int[]{-13613947, -15573658}, (float[]) null, Shader.TileMode.CLAMP);
            } else if (i == 2) {
                this.linearGradient = new LinearGradient(this.cx - getTranslationX(), (float) (-AndroidUtilities.dp(60.0f)), ((float) getMeasuredWidth()) / 2.0f, (float) getMeasuredHeight(), new int[]{-15573658, -13613947}, (float[]) null, Shader.TileMode.CLAMP);
            } else {
                this.linearGradient = new LinearGradient(((float) getMeasuredWidth()) / 2.0f, 0.0f, this.cx - getTranslationX(), (float) (getMeasuredHeight() + AndroidUtilities.dp(60.0f)), new int[]{-13613947, -15573658}, (float[]) null, Shader.TileMode.CLAMP);
            }
        }
        this.rectF.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
        this.paint.setShader(this.linearGradient);
        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), this.paint);
        int i2 = this.position;
        if (i2 == 0) {
            f2 = this.cy - getTranslationY();
            f = 0.0f;
        } else if (i2 == 1) {
            f2 = this.cy - getTranslationY();
            f = (float) getMeasuredWidth();
        } else if (i2 == 2) {
            f = this.cx - getTranslationX();
            f2 = 0.0f;
        } else {
            f = this.cx - getTranslationX();
            f2 = (float) getMeasuredHeight();
        }
        setPivotX(f);
        setPivotY(f2);
        canvas.save();
        int i3 = this.position;
        if (i3 == 0) {
            canvas2.clipRect(f - ((float) AndroidUtilities.dp(15.0f)), f2 - ((float) AndroidUtilities.dp(15.0f)), f, ((float) AndroidUtilities.dp(15.0f)) + f2);
            canvas2.translate((float) AndroidUtilities.dp(3.0f), 0.0f);
            canvas2.rotate(45.0f, f, f2);
        } else if (i3 == 1) {
            canvas2.clipRect(f, f2 - ((float) AndroidUtilities.dp(15.0f)), ((float) AndroidUtilities.dp(15.0f)) + f, ((float) AndroidUtilities.dp(15.0f)) + f2);
            canvas2.translate((float) (-AndroidUtilities.dp(3.0f)), 0.0f);
            canvas2.rotate(45.0f, f, f2);
        } else if (i3 == 2) {
            canvas2.clipRect(f - ((float) AndroidUtilities.dp(15.0f)), f2 - ((float) AndroidUtilities.dp(15.0f)), ((float) AndroidUtilities.dp(15.0f)) + f, f2);
            canvas2.rotate(45.0f, f, f2);
            canvas2.translate(0.0f, (float) AndroidUtilities.dp(3.0f));
        } else {
            canvas2.clipRect(f - ((float) AndroidUtilities.dp(15.0f)), f2, ((float) AndroidUtilities.dp(15.0f)) + f, ((float) AndroidUtilities.dp(15.0f)) + f2);
            canvas2.rotate(45.0f, f, f2);
            canvas2.translate(0.0f, (float) (-AndroidUtilities.dp(3.0f)));
        }
        this.rectF.set(f - ((float) AndroidUtilities.dp(10.0f)), f2 - ((float) AndroidUtilities.dp(10.0f)), f + ((float) AndroidUtilities.dp(10.0f)), f2 + ((float) AndroidUtilities.dp(10.0f)));
        canvas2.drawRoundRect(this.rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.paint);
        canvas.restore();
        super.onDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(230.0f), NUM), i2);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (!(sharedInstance == null || sharedInstance.groupCall == null)) {
            int colorForId = AvatarDrawable.getColorForId(sharedInstance.getChat().id);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setColor(colorForId);
            avatarDrawable.setInfo(sharedInstance.getChat());
            this.avatarImageView.setImage(ImageLocation.getForLocal(sharedInstance.getChat().photo.photo_small), "50_50", (Drawable) avatarDrawable, (Object) null);
            this.titleView.setText(sharedInstance.getChat().title);
            updateMembersCount();
            sharedInstance.registerStateListener(this);
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
        ChatObject.Call call;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null && (call = sharedInstance.groupCall) != null) {
            int i = call.speakingMembersCount;
            if (i != 0) {
                this.subtitleView.setText(LocaleController.formatPluralString("MembersTalking", i));
            } else {
                this.subtitleView.setText(LocaleController.formatPluralString("Members", call.call.participants_count));
            }
        }
    }

    private void updateButtons(boolean z) {
        VoIPService sharedInstance;
        int i;
        String str;
        int i2;
        if (this.soundButton != null && this.muteButton != null && (sharedInstance = VoIPService.getSharedInstance()) != null) {
            boolean isBluetoothOn = sharedInstance.isBluetoothOn();
            boolean z2 = !isBluetoothOn && sharedInstance.isSpeakerphoneOn();
            this.soundButton.setChecked(z2);
            if (z2) {
                i = ColorUtils.setAlphaComponent(-1, 76);
            } else {
                i = ColorUtils.setAlphaComponent(-1, 38);
            }
            int i3 = i;
            if (isBluetoothOn) {
                this.soundButton.setData(NUM, -1, i3, 0.1f, true, LocaleController.getString("VoipAudioRoutingBluetooth", NUM), false, z);
            } else if (z2) {
                this.soundButton.setData(NUM, -1, i3, 0.3f, true, LocaleController.getString("VoipSpeaker", NUM), false, z);
            } else if (sharedInstance.isHeadsetPlugged()) {
                this.soundButton.setData(NUM, -1, i3, 0.1f, true, LocaleController.getString("VoipAudioRoutingHeadset", NUM), false, z);
            } else {
                this.soundButton.setData(NUM, -1, i3, 0.1f, true, LocaleController.getString("VoipSpeaker", NUM), false, z);
            }
            VoIPToggleButton voIPToggleButton = this.muteButton;
            int alphaComponent = ColorUtils.setAlphaComponent(-1, 38);
            if (sharedInstance.isMicMute()) {
                i2 = NUM;
                str = "VoipUnmute";
            } else {
                i2 = NUM;
                str = "VoipMute";
            }
            voIPToggleButton.setData(NUM, -1, alphaComponent, 0.1f, true, LocaleController.getString(str, i2), sharedInstance.isMicMute(), z);
        }
    }

    public void onAudioSettingsChanged() {
        updateButtons(true);
    }

    public void setPosition(int i, float f, float f2) {
        this.position = i;
        this.cx = f;
        this.cy = f2;
        invalidate();
        this.invalidateGradient = true;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.groupCallUpdated) {
            updateMembersCount();
        }
    }
}
