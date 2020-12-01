package org.telegram.ui.Cells;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;

public class GroupCallUserCell extends FrameLayout {
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private AvatarWavesDrawable avatarWavesDrawable;
    private int currentAccount = UserConfig.selectedAccount;
    private ChatObject.Call currentCall;
    private TLRPC$User currentUser;
    private Paint dividerPaint;
    private boolean isSpeaking;
    private int lastMuteColor;
    private boolean lastMuted;
    private RLottieImageView muteButton;
    private RLottieDrawable muteDrawable;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private TLRPC$TL_groupCallParticipant participant;
    /* access modifiers changed from: private */
    public SimpleTextView[] statusTextView = new SimpleTextView[2];
    private Runnable updateRunnable = new Runnable() {
        public final void run() {
            GroupCallUserCell.this.lambda$new$0$GroupCallUserCell();
        }
    };
    private boolean updateRunnableScheduled;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    /* renamed from: onMuteClick */
    public void lambda$new$1(GroupCallUserCell groupCallUserCell) {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$GroupCallUserCell() {
        this.isSpeaking = false;
        applyParticipantChanges(true, true);
        this.avatarWavesDrawable.setAmplitude(0.0d);
        this.updateRunnableScheduled = false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCallUserCell(Context context) {
        super(context);
        Context context2 = context;
        Paint paint = new Paint();
        this.dividerPaint = paint;
        paint.setColor(Theme.getColor("voipgroup_actionBar"));
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        BackupImageView backupImageView2 = this.avatarImageView;
        boolean z = LocaleController.isRTL;
        int i = 5;
        addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, (z ? 5 : 3) | 48, z ? 0.0f : 11.0f, 6.0f, z ? 11.0f : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("voipgroup_nameText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        boolean z2 = LocaleController.isRTL;
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z2 ? 5 : 3) | 48, z2 ? 48.0f : 67.0f, 10.0f, z2 ? 67.0f : 48.0f, 0.0f));
        for (int i2 = 0; i2 < 2; i2++) {
            this.statusTextView[i2] = new SimpleTextView(context2);
            this.statusTextView[i2].setTextSize(15);
            this.statusTextView[i2].setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            if (i2 == 0) {
                this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_listeningText"));
                this.statusTextView[i2].setText(LocaleController.getString("Listening", NUM));
            } else {
                this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_speakingText"));
                this.statusTextView[i2].setText(LocaleController.getString("Speaking", NUM));
            }
            SimpleTextView simpleTextView3 = this.statusTextView[i2];
            boolean z3 = LocaleController.isRTL;
            addView(simpleTextView3, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 48.0f : 67.0f, 32.0f, z3 ? 67.0f : 48.0f, 0.0f));
        }
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(19.0f), AndroidUtilities.dp(24.0f), true, (int[]) null);
        this.muteDrawable = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.muteButton = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.muteButton.setAnimation(this.muteDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            RippleDrawable rippleDrawable = (RippleDrawable) Theme.createSelectorDrawable(Theme.getColor("voipgroup_mutedIcon") & NUM);
            Theme.setRippleDrawableForceSoftware(rippleDrawable);
            this.muteButton.setBackground(rippleDrawable);
        }
        addView(this.muteButton, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : i) | 16, 16.0f, 0.0f, 16.0f, 0.0f));
        this.muteButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCallUserCell.this.lambda$new$1$GroupCallUserCell(view);
            }
        });
        this.avatarWavesDrawable = new AvatarWavesDrawable(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(29.0f));
        setWillNotDraw(false);
        setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.updateRunnableScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            this.updateRunnableScheduled = false;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
    }

    public boolean isSelfUser() {
        return UserObject.isUserSelf(this.currentUser);
    }

    public CharSequence getName() {
        return this.nameTextView.getText();
    }

    public void setData(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, ChatObject.Call call, boolean z, boolean z2) {
        this.currentCall = call;
        this.participant = tLRPC$TL_groupCallParticipant;
        this.needDivider = z2;
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.participant.user_id));
        this.currentUser = user;
        this.avatarDrawable.setInfo(user);
        this.nameTextView.setText(UserObject.getUserName(this.currentUser));
        this.muteButton.setEnabled(z && !UserObject.isUserSelf(this.currentUser));
        this.avatarImageView.setImage(ImageLocation.getForUser(this.currentUser, false), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentUser);
        applyParticipantChanges(false);
    }

    public TLRPC$TL_groupCallParticipant getParticipant() {
        return this.participant;
    }

    public void setAmplitude(double d) {
        if (d > 1.5d) {
            if (this.updateRunnableScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            }
            if (!this.isSpeaking) {
                this.isSpeaking = true;
                applyParticipantChanges(true);
            }
            this.avatarWavesDrawable.setAmplitude(d);
            AndroidUtilities.runOnUIThread(this.updateRunnable, 500);
            this.updateRunnableScheduled = true;
            return;
        }
        this.avatarWavesDrawable.setAmplitude(0.0d);
    }

    public void clickMuteButton() {
        this.muteButton.callOnClick();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f) + (this.needDivider ? 1 : 0), NUM));
    }

    public void applyParticipantChanges(boolean z) {
        applyParticipantChanges(z, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:55:0x00b7  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x01b1  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0249  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0251  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0258  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x025e  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0272  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyParticipantChanges(boolean r11, boolean r12) {
        /*
            r10 = this;
            r0 = 1
            r1 = 0
            if (r12 != 0) goto L_0x0035
            long r2 = android.os.SystemClock.uptimeMillis()
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r10.participant
            long r4 = r12.lastSpeakTime
            long r2 = r2 - r4
            r4 = 500(0x1f4, double:2.47E-321)
            int r12 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r12 >= 0) goto L_0x0015
            r12 = 1
            goto L_0x0016
        L_0x0015:
            r12 = 0
        L_0x0016:
            boolean r6 = r10.isSpeaking
            if (r6 == 0) goto L_0x001c
            if (r12 != 0) goto L_0x0035
        L_0x001c:
            r10.isSpeaking = r12
            boolean r12 = r10.updateRunnableScheduled
            if (r12 == 0) goto L_0x0029
            java.lang.Runnable r12 = r10.updateRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r12)
            r10.updateRunnableScheduled = r1
        L_0x0029:
            boolean r12 = r10.isSpeaking
            if (r12 == 0) goto L_0x0035
            java.lang.Runnable r12 = r10.updateRunnable
            long r4 = r4 - r2
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r12, r4)
            r10.updateRunnableScheduled = r0
        L_0x0035:
            org.telegram.messenger.ChatObject$Call r12 = r10.currentCall
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r12 = r12.participants
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r10.participant
            int r2 = r2.user_id
            java.lang.Object r12 = r12.get(r2)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r12
            if (r12 == 0) goto L_0x0047
            r10.participant = r12
        L_0x0047:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r10.participant
            boolean r2 = r12.muted
            if (r2 == 0) goto L_0x004f
            boolean r12 = r12.can_self_unmute
        L_0x004f:
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r10.statusTextView
            r12 = r12[r1]
            java.lang.Object r12 = r12.getTag()
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r10.participant
            boolean r4 = r3.muted
            java.lang.String r5 = "voipgroup_mutedIcon"
            r6 = 0
            if (r4 == 0) goto L_0x0070
            boolean r3 = r3.can_self_unmute
            if (r3 != 0) goto L_0x006b
            java.lang.String r3 = "voipgroup_mutedByAdminIcon"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            goto L_0x0085
        L_0x006b:
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            goto L_0x0085
        L_0x0070:
            boolean r3 = r10.isSpeaking
            if (r3 != 0) goto L_0x0087
            boolean r3 = r10.isSelfUser()
            if (r3 == 0) goto L_0x0081
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r10.participant
            boolean r3 = r3.muted
            if (r3 != 0) goto L_0x0081
            goto L_0x0087
        L_0x0081:
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r5)
        L_0x0085:
            r4 = r6
            goto L_0x0091
        L_0x0087:
            java.lang.String r3 = "voipgroup_speakingText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r0)
        L_0x0091:
            android.animation.AnimatorSet r5 = r10.animatorSet
            if (r5 == 0) goto L_0x00a3
            if (r4 != 0) goto L_0x0099
            if (r12 != 0) goto L_0x00a1
        L_0x0099:
            if (r4 == 0) goto L_0x009d
            if (r12 == 0) goto L_0x00a1
        L_0x009d:
            int r7 = r10.lastMuteColor
            if (r7 == r3) goto L_0x00a3
        L_0x00a1:
            r7 = 1
            goto L_0x00a4
        L_0x00a3:
            r7 = 0
        L_0x00a4:
            if (r11 == 0) goto L_0x00a8
            if (r7 == 0) goto L_0x00ad
        L_0x00a8:
            if (r5 == 0) goto L_0x00ad
            r5.cancel()
        L_0x00ad:
            if (r11 == 0) goto L_0x00b5
            int r5 = r10.lastMuteColor
            if (r5 != r3) goto L_0x00b5
            if (r7 == 0) goto L_0x00f1
        L_0x00b5:
            if (r11 == 0) goto L_0x00d6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            int r5 = r10.lastMuteColor
            r10.lastMuteColor = r3
            r8 = 2
            float[] r8 = new float[r8]
            r8 = {0, NUM} // fill-array
            android.animation.ValueAnimator r8 = android.animation.ValueAnimator.ofFloat(r8)
            org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$S663G3GaI5zMrGXw3lTjaMVwPeE r9 = new org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$S663G3GaI5zMrGXw3lTjaMVwPeE
            r9.<init>(r5, r3)
            r8.addUpdateListener(r9)
            r6.add(r8)
            goto L_0x00f1
        L_0x00d6:
            org.telegram.ui.Components.RLottieImageView r5 = r10.muteButton
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            r10.lastMuteColor = r3
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r3, r9)
            r5.setColorFilter(r8)
            org.telegram.ui.Components.RLottieImageView r5 = r10.muteButton
            android.graphics.drawable.Drawable r5 = r5.getDrawable()
            r8 = 436207615(0x19ffffff, float:2.6469778E-23)
            r3 = r3 & r8
            org.telegram.ui.ActionBar.Theme.setSelectorDrawableColor(r5, r3, r0)
        L_0x00f1:
            if (r11 == 0) goto L_0x00fd
            if (r4 != 0) goto L_0x00f7
            if (r12 != 0) goto L_0x00fd
        L_0x00f7:
            if (r4 == 0) goto L_0x00fb
            if (r12 == 0) goto L_0x00fd
        L_0x00fb:
            if (r7 == 0) goto L_0x021b
        L_0x00fd:
            r12 = 1065353216(0x3var_, float:1.0)
            r3 = 1073741824(0x40000000, float:2.0)
            r5 = 0
            if (r11 == 0) goto L_0x01b1
            if (r6 != 0) goto L_0x010b
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
        L_0x010b:
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r1]
            r7.setVisibility(r1)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r0]
            r7.setVisibility(r1)
            if (r4 != 0) goto L_0x0167
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r1]
            android.util.Property r8 = android.view.View.TRANSLATION_Y
            float[] r9 = new float[r0]
            r9[r1] = r5
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r6.add(r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r1]
            android.util.Property r8 = android.view.View.ALPHA
            float[] r9 = new float[r0]
            r9[r1] = r12
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r6.add(r12)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r10.statusTextView
            r12 = r12[r0]
            android.util.Property r7 = android.view.View.TRANSLATION_Y
            float[] r8 = new float[r0]
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = -r3
            float r3 = (float) r3
            r8[r1] = r3
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r7, r8)
            r6.add(r12)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r10.statusTextView
            r12 = r12[r0]
            android.util.Property r3 = android.view.View.ALPHA
            float[] r7 = new float[r0]
            r7[r1] = r5
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r3, r7)
            r6.add(r12)
            goto L_0x0214
        L_0x0167:
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r1]
            android.util.Property r8 = android.view.View.TRANSLATION_Y
            float[] r9 = new float[r0]
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r9[r1] = r3
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r7, r8, r9)
            r6.add(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.statusTextView
            r3 = r3[r1]
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r0]
            r8[r1] = r5
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r8)
            r6.add(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.statusTextView
            r3 = r3[r0]
            android.util.Property r7 = android.view.View.TRANSLATION_Y
            float[] r8 = new float[r0]
            r8[r1] = r5
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r7, r8)
            r6.add(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.statusTextView
            r3 = r3[r0]
            android.util.Property r5 = android.view.View.ALPHA
            float[] r7 = new float[r0]
            r7[r1] = r12
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r3, r5, r7)
            r6.add(r12)
            goto L_0x0214
        L_0x01b1:
            r7 = 4
            if (r4 != 0) goto L_0x01e5
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r10.statusTextView
            r8 = r8[r1]
            r8.setVisibility(r1)
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r10.statusTextView
            r8 = r8[r0]
            r8.setVisibility(r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r1]
            r7.setTranslationY(r5)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r1]
            r7.setAlpha(r12)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r10.statusTextView
            r12 = r12[r0]
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = -r3
            float r3 = (float) r3
            r12.setTranslationY(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r10.statusTextView
            r12 = r12[r0]
            r12.setAlpha(r5)
            goto L_0x0214
        L_0x01e5:
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r10.statusTextView
            r8 = r8[r1]
            r8.setVisibility(r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r0]
            r7.setVisibility(r1)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r10.statusTextView
            r7 = r7[r1]
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r7.setTranslationY(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.statusTextView
            r3 = r3[r1]
            r3.setAlpha(r5)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.statusTextView
            r3 = r3[r0]
            r3.setTranslationY(r5)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r10.statusTextView
            r3 = r3[r0]
            r3.setAlpha(r12)
        L_0x0214:
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r10.statusTextView
            r12 = r12[r1]
            r12.setTag(r4)
        L_0x021b:
            if (r6 == 0) goto L_0x023d
            android.animation.AnimatorSet r12 = new android.animation.AnimatorSet
            r12.<init>()
            r10.animatorSet = r12
            org.telegram.ui.Cells.GroupCallUserCell$1 r3 = new org.telegram.ui.Cells.GroupCallUserCell$1
            r3.<init>(r4)
            r12.addListener(r3)
            android.animation.AnimatorSet r12 = r10.animatorSet
            r12.playTogether(r6)
            android.animation.AnimatorSet r12 = r10.animatorSet
            r3 = 180(0xb4, double:8.9E-322)
            r12.setDuration(r3)
            android.animation.AnimatorSet r12 = r10.animatorSet
            r12.start()
        L_0x023d:
            if (r11 == 0) goto L_0x0247
            boolean r12 = r10.lastMuted
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r10.participant
            boolean r3 = r3.muted
            if (r12 == r3) goto L_0x026e
        L_0x0247:
            if (r2 == 0) goto L_0x0251
            org.telegram.ui.Components.RLottieDrawable r12 = r10.muteDrawable
            r3 = 12
            r12.setCustomEndFrame(r3)
            goto L_0x0256
        L_0x0251:
            org.telegram.ui.Components.RLottieDrawable r12 = r10.muteDrawable
            r12.setCustomEndFrame(r1)
        L_0x0256:
            if (r11 == 0) goto L_0x025e
            org.telegram.ui.Components.RLottieImageView r11 = r10.muteButton
            r11.playAnimation()
            goto L_0x026c
        L_0x025e:
            org.telegram.ui.Components.RLottieDrawable r11 = r10.muteDrawable
            int r12 = r11.getCustomEndFrame()
            r11.setCurrentFrame(r12, r1, r0)
            org.telegram.ui.Components.RLottieImageView r11 = r10.muteButton
            r11.invalidate()
        L_0x026c:
            r10.lastMuted = r2
        L_0x026e:
            boolean r11 = r10.isSpeaking
            if (r11 != 0) goto L_0x0279
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r11 = r10.avatarWavesDrawable
            r2 = 0
            r11.setAmplitude(r2)
        L_0x0279:
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r11 = r10.avatarWavesDrawable
            boolean r12 = r10.isSpeaking
            if (r12 == 0) goto L_0x0286
            boolean r12 = r10.isSelfUser()
            if (r12 != 0) goto L_0x0286
            goto L_0x0287
        L_0x0286:
            r0 = 0
        L_0x0287:
            r11.setShowWaves(r0, r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCallUserCell.applyParticipantChanges(boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyParticipantChanges$2 */
    public /* synthetic */ void lambda$applyParticipantChanges$2$GroupCallUserCell(int i, int i2, ValueAnimator valueAnimator) {
        int offsetColor = AndroidUtilities.getOffsetColor(i, i2, valueAnimator.getAnimatedFraction(), 1.0f);
        this.muteButton.setColorFilter(new PorterDuffColorFilter(offsetColor, PorterDuff.Mode.MULTIPLY));
        Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), offsetColor & NUM, true);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), this.dividerPaint);
        }
        this.avatarWavesDrawable.draw(canvas, (float) (this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2)), (float) (this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2)), this);
        this.avatarImageView.setScaleX(this.avatarWavesDrawable.getAvatarScale());
        this.avatarImageView.setScaleY(this.avatarWavesDrawable.getAvatarScale());
        super.dispatchDraw(canvas);
    }

    public static class AvatarWavesDrawable {
        float amplitude;
        float animateAmplitudeDiff;
        float animateToAmplitude;
        private BlobDrawable blobDrawable = new BlobDrawable(6);
        private BlobDrawable blobDrawable2;
        boolean showWaves;
        float wavesEnter = 0.0f;

        public AvatarWavesDrawable(int i, int i2) {
            BlobDrawable blobDrawable3 = new BlobDrawable(8);
            this.blobDrawable2 = blobDrawable3;
            BlobDrawable blobDrawable4 = this.blobDrawable;
            float f = (float) i;
            blobDrawable4.minRadius = f;
            float f2 = (float) i2;
            blobDrawable4.maxRadius = f2;
            blobDrawable3.minRadius = f;
            blobDrawable3.maxRadius = f2;
            blobDrawable4.generateBlob();
            this.blobDrawable2.generateBlob();
            this.blobDrawable.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_speakingText"), 38));
            this.blobDrawable2.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_speakingText"), 38));
        }

        /* JADX WARNING: Removed duplicated region for block: B:29:0x0094  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void draw(android.graphics.Canvas r8, float r9, float r10, android.view.View r11) {
            /*
                r7 = this;
                float r0 = r7.animateToAmplitude
                float r1 = r7.amplitude
                r2 = 0
                int r3 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r3 == 0) goto L_0x0023
                float r3 = r7.animateAmplitudeDiff
                r4 = 1098907648(0x41800000, float:16.0)
                float r4 = r4 * r3
                float r1 = r1 + r4
                r7.amplitude = r1
                int r3 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
                if (r3 <= 0) goto L_0x001d
                int r1 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1))
                if (r1 <= 0) goto L_0x0023
                r7.amplitude = r0
                goto L_0x0023
            L_0x001d:
                int r1 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1))
                if (r1 >= 0) goto L_0x0023
                r7.amplitude = r0
            L_0x0023:
                r0 = 1061997773(0x3f4ccccd, float:0.8)
                r1 = 1053609165(0x3ecccccd, float:0.4)
                float r3 = r7.amplitude
                float r3 = r3 * r1
                float r3 = r3 + r0
                boolean r0 = r7.showWaves
                if (r0 != 0) goto L_0x0038
                float r0 = r7.wavesEnter
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 == 0) goto L_0x009a
            L_0x0038:
                r8.save()
                boolean r0 = r7.showWaves
                r1 = 1027292903(0x3d3b3ee7, float:0.NUM)
                r4 = 1065353216(0x3var_, float:1.0)
                if (r0 == 0) goto L_0x0054
                float r5 = r7.wavesEnter
                int r6 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
                if (r6 == 0) goto L_0x0054
                float r5 = r5 + r1
                r7.wavesEnter = r5
                int r0 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
                if (r0 <= 0) goto L_0x0065
                r7.wavesEnter = r4
                goto L_0x0065
            L_0x0054:
                if (r0 != 0) goto L_0x0065
                float r0 = r7.wavesEnter
                int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r5 == 0) goto L_0x0065
                float r0 = r0 - r1
                r7.wavesEnter = r0
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 >= 0) goto L_0x0065
                r7.wavesEnter = r2
            L_0x0065:
                org.telegram.ui.Components.CubicBezierInterpolator r0 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                float r1 = r7.wavesEnter
                float r0 = r0.getInterpolation(r1)
                float r3 = r3 * r0
                r8.scale(r3, r3, r9, r10)
                org.telegram.ui.Components.BlobDrawable r1 = r7.blobDrawable
                float r3 = r7.amplitude
                r1.update(r3, r4)
                org.telegram.ui.Components.BlobDrawable r1 = r7.blobDrawable
                android.graphics.Paint r3 = r1.paint
                r1.draw(r9, r10, r8, r3)
                org.telegram.ui.Components.BlobDrawable r1 = r7.blobDrawable2
                float r3 = r7.amplitude
                r1.update(r3, r4)
                org.telegram.ui.Components.BlobDrawable r1 = r7.blobDrawable2
                org.telegram.ui.Components.BlobDrawable r3 = r7.blobDrawable
                android.graphics.Paint r3 = r3.paint
                r1.draw(r9, r10, r8, r3)
                int r9 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r9 == 0) goto L_0x0097
                r11.invalidate()
            L_0x0097:
                r8.restore()
            L_0x009a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable.draw(android.graphics.Canvas, float, float, android.view.View):void");
        }

        public float getAvatarScale() {
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.wavesEnter);
            return (((this.amplitude * 0.2f) + 0.9f) * interpolation) + ((1.0f - interpolation) * 1.0f);
        }

        public void setShowWaves(boolean z, View view) {
            if (this.showWaves != z) {
                view.invalidate();
            }
            this.showWaves = z;
        }

        public void setAmplitude(double d) {
            float f = ((float) d) / 80.0f;
            float f2 = 0.0f;
            if (!this.showWaves) {
                f = 0.0f;
            }
            if (f > 1.0f) {
                f2 = 1.0f;
            } else if (f >= 0.0f) {
                f2 = f;
            }
            this.animateToAmplitude = f2;
            this.animateAmplitudeDiff = (f2 - this.amplitude) / 200.0f;
        }

        public void setColor(int i) {
            this.blobDrawable.paint.setColor(i);
        }
    }
}
