package org.telegram.ui.Cells;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Chat;
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
    private AccountInstance accountInstance;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private AvatarWavesDrawable avatarWavesDrawable;
    private Runnable checkRaiseRunnable = new Runnable() {
        public final void run() {
            GroupCallUserCell.this.lambda$new$2$GroupCallUserCell();
        }
    };
    private ChatObject.Call currentCall;
    private TLRPC$Chat currentChat;
    private boolean currentIconGray;
    private int currentStatus;
    private TLRPC$User currentUser;
    private Paint dividerPaint;
    private String grayIconColor = "voipgroup_mutedIcon";
    private boolean isSpeaking;
    private int lastMuteColor;
    private boolean lastMuted;
    private boolean lastRaisedHand;
    private RLottieImageView muteButton;
    private RLottieDrawable muteDrawable;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private TLRPC$TL_groupCallParticipant participant;
    private float progressToAvatarPreview;
    private Runnable raiseHandCallback = new Runnable() {
        public final void run() {
            GroupCallUserCell.this.lambda$new$1$GroupCallUserCell();
        }
    };
    private int selfId;
    private Runnable shakeHandCallback = new Runnable() {
        public final void run() {
            GroupCallUserCell.this.lambda$new$0$GroupCallUserCell();
        }
    };
    private RLottieDrawable shakeHandDrawable;
    private Drawable speakingDrawable;
    /* access modifiers changed from: private */
    public SimpleTextView[] statusTextView = new SimpleTextView[5];
    private Runnable updateRunnable = new Runnable() {
        public final void run() {
            GroupCallUserCell.this.lambda$new$3$GroupCallUserCell();
        }
    };
    private boolean updateRunnableScheduled;
    private Runnable updateVoiceRunnable = new Runnable() {
        public final void run() {
            GroupCallUserCell.this.lambda$new$4$GroupCallUserCell();
        }
    };
    private boolean updateVoiceRunnableScheduled;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    /* renamed from: onMuteClick */
    public void lambda$new$5(GroupCallUserCell groupCallUserCell) {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$GroupCallUserCell() {
        this.shakeHandDrawable.setOnFinishCallback((Runnable) null, 0);
        this.muteDrawable.setOnFinishCallback((Runnable) null, 0);
        this.muteButton.setAnimation(this.muteDrawable);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$GroupCallUserCell() {
        int nextInt = Utilities.random.nextInt(100);
        int i = 540;
        int i2 = 420;
        if (nextInt < 32) {
            i = 120;
            i2 = 0;
        } else if (nextInt < 64) {
            i = 240;
            i2 = 120;
        } else if (nextInt < 97) {
            i = 420;
            i2 = 240;
        } else if (nextInt != 98) {
            i = 720;
            i2 = 540;
        }
        this.shakeHandDrawable.setCustomEndFrame(i);
        this.shakeHandDrawable.setOnFinishCallback(this.shakeHandCallback, i - 1);
        this.muteButton.setAnimation(this.shakeHandDrawable);
        this.shakeHandDrawable.setCurrentFrame(i2);
        this.muteButton.playAnimation();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$GroupCallUserCell() {
        applyParticipantChanges(true, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$GroupCallUserCell() {
        this.isSpeaking = false;
        applyParticipantChanges(true, true);
        this.avatarWavesDrawable.setAmplitude(0.0d);
        this.updateRunnableScheduled = false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$4 */
    public /* synthetic */ void lambda$new$4$GroupCallUserCell() {
        applyParticipantChanges(true, true);
        this.updateVoiceRunnableScheduled = false;
    }

    public void setProgressToAvatarPreview(float f) {
        this.progressToAvatarPreview = f;
        this.nameTextView.setTranslationX(((float) (LocaleController.isRTL ? AndroidUtilities.dp(53.0f) : -AndroidUtilities.dp(53.0f))) * f);
        boolean z = false;
        int i = 0;
        while (true) {
            SimpleTextView[] simpleTextViewArr = this.statusTextView;
            if (i >= simpleTextViewArr.length) {
                break;
            }
            if (TextUtils.isEmpty(simpleTextViewArr[4].getText()) || this.statusTextView[4].getLineCount() <= 1) {
                this.statusTextView[i].setTranslationX(((float) (LocaleController.isRTL ? AndroidUtilities.dp(53.0f) : -AndroidUtilities.dp(53.0f))) * f);
            } else {
                if (f > 0.0f) {
                    this.statusTextView[i].setFullLayoutAdditionalWidth(AndroidUtilities.dp(92.0f), LocaleController.isRTL ? AndroidUtilities.dp(48.0f) : AndroidUtilities.dp(53.0f));
                } else {
                    this.statusTextView[i].setFullLayoutAdditionalWidth(0, 0);
                }
                this.statusTextView[i].setFullAlpha(f);
                this.statusTextView[i].setTranslationX(0.0f);
                this.statusTextView[i].invalidate();
            }
            i++;
        }
        this.avatarImageView.setAlpha(f == 0.0f ? 1.0f : 0.0f);
        AvatarWavesDrawable avatarWavesDrawable2 = this.avatarWavesDrawable;
        if (this.isSpeaking && f == 0.0f) {
            z = true;
        }
        avatarWavesDrawable2.setShowWaves(z, this);
        float f2 = 1.0f - f;
        this.muteButton.setAlpha(f2);
        float f3 = (f2 * 0.4f) + 0.6f;
        this.muteButton.setScaleX(f3);
        this.muteButton.setScaleY(f3);
        invalidate();
    }

    public AvatarWavesDrawable getAvatarWavesDrawable() {
        return this.avatarWavesDrawable;
    }

    private static class VerifiedDrawable extends Drawable {
        private Drawable[] drawables;

        public int getOpacity() {
            return -2;
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public VerifiedDrawable(Context context) {
            Drawable[] drawableArr = new Drawable[2];
            this.drawables = drawableArr;
            drawableArr[0] = context.getResources().getDrawable(NUM).mutate();
            this.drawables[0].setColorFilter(new PorterDuffColorFilter(-9063442, PorterDuff.Mode.MULTIPLY));
            this.drawables[1] = context.getResources().getDrawable(NUM).mutate();
        }

        public int getIntrinsicWidth() {
            return this.drawables[0].getIntrinsicWidth();
        }

        public int getIntrinsicHeight() {
            return this.drawables[0].getIntrinsicHeight();
        }

        public void draw(Canvas canvas) {
            int i = 0;
            while (true) {
                Drawable[] drawableArr = this.drawables;
                if (i < drawableArr.length) {
                    drawableArr[i].setBounds(getBounds());
                    this.drawables[i].draw(canvas);
                    i++;
                } else {
                    return;
                }
            }
        }

        public void setAlpha(int i) {
            int i2 = 0;
            while (true) {
                Drawable[] drawableArr = this.drawables;
                if (i2 < drawableArr.length) {
                    drawableArr[i2].setAlpha(i);
                    i2++;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCallUserCell(Context context) {
        super(context);
        Context context2 = context;
        int i = 5;
        Paint paint = new Paint();
        this.dividerPaint = paint;
        paint.setColor(Theme.getColor("voipgroup_actionBar"));
        this.avatarDrawable = new AvatarDrawable();
        setClipChildren(false);
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        BackupImageView backupImageView2 = this.avatarImageView;
        boolean z = LocaleController.isRTL;
        addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, (z ? 5 : 3) | 48, z ? 0.0f : 11.0f, 6.0f, z ? 11.0f : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("voipgroup_nameText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setDrawablePadding(AndroidUtilities.dp(6.0f));
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        boolean z2 = LocaleController.isRTL;
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z2 ? 5 : 3) | 48, z2 ? 54.0f : 67.0f, 10.0f, z2 ? 67.0f : 54.0f, 0.0f));
        Drawable drawable = context.getResources().getDrawable(NUM);
        this.speakingDrawable = drawable;
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("voipgroup_speakingText"), PorterDuff.Mode.MULTIPLY));
        final int i2 = 0;
        while (true) {
            SimpleTextView[] simpleTextViewArr = this.statusTextView;
            if (i2 >= simpleTextViewArr.length) {
                break;
            }
            simpleTextViewArr[i2] = new SimpleTextView(context2) {
                float originalAlpha;

                public void setAlpha(float f) {
                    this.originalAlpha = f;
                    if (i2 == 4) {
                        float fullAlpha = GroupCallUserCell.this.statusTextView[4].getFullAlpha();
                        if (fullAlpha > 0.0f) {
                            super.setAlpha(Math.max(f, fullAlpha));
                        } else {
                            super.setAlpha(f);
                        }
                    } else {
                        super.setAlpha(f * (1.0f - GroupCallUserCell.this.statusTextView[4].getFullAlpha()));
                    }
                }

                public void setTranslationY(float f) {
                    if (i2 == 4 && getFullAlpha() > 0.0f) {
                        f = 0.0f;
                    }
                    super.setTranslationY(f);
                }

                public float getAlpha() {
                    return this.originalAlpha;
                }

                public void setFullAlpha(float f) {
                    super.setFullAlpha(f);
                    for (int i = 0; i < GroupCallUserCell.this.statusTextView.length; i++) {
                        GroupCallUserCell.this.statusTextView[i].setAlpha(GroupCallUserCell.this.statusTextView[i].getAlpha());
                    }
                }
            };
            this.statusTextView[i2].setTextSize(15);
            this.statusTextView[i2].setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            if (i2 == 4) {
                this.statusTextView[i2].setBuildFullLayout(true);
                this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_mutedIcon"));
                SimpleTextView simpleTextView3 = this.statusTextView[i2];
                boolean z3 = LocaleController.isRTL;
                addView(simpleTextView3, LayoutHelper.createFrame(-1, -2.0f, (z3 ? 5 : 3) | 48, z3 ? 54.0f : 67.0f, 32.0f, z3 ? 67.0f : 54.0f, 0.0f));
            } else {
                if (i2 == 0) {
                    this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_listeningText"));
                    this.statusTextView[i2].setText(LocaleController.getString("Listening", NUM));
                } else if (i2 == 1) {
                    this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_speakingText"));
                    this.statusTextView[i2].setText(LocaleController.getString("Speaking", NUM));
                    this.statusTextView[i2].setDrawablePadding(AndroidUtilities.dp(2.0f));
                } else if (i2 == 2) {
                    this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_mutedByAdminIcon"));
                    this.statusTextView[i2].setText(LocaleController.getString("VoipGroupMutedForMe", NUM));
                } else if (i2 == 3) {
                    this.statusTextView[i2].setTextColor(Theme.getColor("voipgroup_listeningText"));
                    this.statusTextView[i2].setText(LocaleController.getString("WantsToSpeak", NUM));
                }
                SimpleTextView simpleTextView4 = this.statusTextView[i2];
                boolean z4 = LocaleController.isRTL;
                addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, (z4 ? 5 : 3) | 48, z4 ? 54.0f : 67.0f, 32.0f, z4 ? 67.0f : 54.0f, 0.0f));
            }
            i2++;
        }
        this.muteDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(34.0f), AndroidUtilities.dp(32.0f), true, (int[]) null);
        this.shakeHandDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(34.0f), AndroidUtilities.dp(32.0f), true, (int[]) null);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.muteButton = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.muteButton.setAnimation(this.muteDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            RippleDrawable rippleDrawable = (RippleDrawable) Theme.createSelectorDrawable(Theme.getColor(this.grayIconColor) & NUM);
            Theme.setRippleDrawableForceSoftware(rippleDrawable);
            this.muteButton.setBackground(rippleDrawable);
        }
        this.muteButton.setImportantForAccessibility(2);
        addView(this.muteButton, LayoutHelper.createFrame(48, -1.0f, (LocaleController.isRTL ? 3 : i) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
        this.muteButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCallUserCell.this.lambda$new$5$GroupCallUserCell(view);
            }
        });
        this.avatarWavesDrawable = new AvatarWavesDrawable(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(29.0f));
        setWillNotDraw(false);
        setFocusable(true);
    }

    public int getClipHeight() {
        if (this.statusTextView[4].getLineCount() <= 1) {
            return getMeasuredHeight();
        }
        return this.statusTextView[4].getTop() + this.statusTextView[4].getTextHeight() + AndroidUtilities.dp(8.0f);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.updateRunnableScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            this.updateRunnableScheduled = false;
        }
        if (this.updateVoiceRunnableScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.updateVoiceRunnable);
            this.updateVoiceRunnableScheduled = false;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
    }

    public boolean isSelfUser() {
        int i = this.selfId;
        if (i > 0) {
            TLRPC$User tLRPC$User = this.currentUser;
            if (tLRPC$User == null || tLRPC$User.id != i) {
                return false;
            }
            return true;
        }
        TLRPC$Chat tLRPC$Chat = this.currentChat;
        if (tLRPC$Chat == null || tLRPC$Chat.id != (-i)) {
            return false;
        }
        return true;
    }

    public boolean isHandRaised() {
        return this.lastRaisedHand;
    }

    public CharSequence getName() {
        return this.nameTextView.getText();
    }

    public void setData(AccountInstance accountInstance2, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, ChatObject.Call call, int i) {
        this.currentCall = call;
        this.accountInstance = accountInstance2;
        this.selfId = i;
        this.participant = tLRPC$TL_groupCallParticipant;
        int peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
        VerifiedDrawable verifiedDrawable = null;
        if (peerId > 0) {
            TLRPC$User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(peerId));
            this.currentUser = user;
            this.currentChat = null;
            this.avatarDrawable.setInfo(user);
            this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            SimpleTextView simpleTextView = this.nameTextView;
            TLRPC$User tLRPC$User = this.currentUser;
            if (tLRPC$User != null && tLRPC$User.verified) {
                verifiedDrawable = new VerifiedDrawable(getContext());
            }
            simpleTextView.setRightDrawable((Drawable) verifiedDrawable);
            this.avatarImageView.getImageReceiver().setCurrentAccount(accountInstance2.getCurrentAccount());
            this.avatarImageView.setImage(ImageLocation.getForUser(this.currentUser, false), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentUser);
            return;
        }
        TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-peerId));
        this.currentChat = chat;
        this.currentUser = null;
        this.avatarDrawable.setInfo(chat);
        TLRPC$Chat tLRPC$Chat = this.currentChat;
        if (tLRPC$Chat != null) {
            this.nameTextView.setText(tLRPC$Chat.title);
            SimpleTextView simpleTextView2 = this.nameTextView;
            if (this.currentChat.verified) {
                verifiedDrawable = new VerifiedDrawable(getContext());
            }
            simpleTextView2.setRightDrawable((Drawable) verifiedDrawable);
            this.avatarImageView.getImageReceiver().setCurrentAccount(accountInstance2.getCurrentAccount());
            this.avatarImageView.setImage(ImageLocation.getForChat(this.currentChat, false), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChat);
        }
    }

    public void setDrawDivider(boolean z) {
        this.needDivider = z;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
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

    public boolean clickMuteButton() {
        if (!this.muteButton.isEnabled()) {
            return false;
        }
        this.muteButton.callOnClick();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), NUM));
    }

    public void applyParticipantChanges(boolean z) {
        applyParticipantChanges(z, false);
    }

    public void setGrayIconColor(String str, int i) {
        if (!this.grayIconColor.equals(str)) {
            if (this.currentIconGray) {
                this.lastMuteColor = Theme.getColor(str);
            }
            this.grayIconColor = str;
        }
        if (this.currentIconGray) {
            this.muteButton.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), i & NUM, true);
        }
    }

    public void setAboutVisibleProgress(int i, float f) {
        if (TextUtils.isEmpty(this.statusTextView[4].getText())) {
            f = 0.0f;
        }
        this.statusTextView[4].setFullAlpha(f);
        invalidate();
    }

    public void setAboutVisible(boolean z) {
        if (z) {
            this.statusTextView[4].setTranslationY(0.0f);
        } else {
            this.statusTextView[4].setFullAlpha(0.0f);
        }
        invalidate();
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x0186  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0192  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x01a5  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x01b6  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x01dd  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01df  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x01f6  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x0214  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0232  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0288  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x028e A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x032d  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0361  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0364  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x03f7  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x040a  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x040c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void applyParticipantChanges(boolean r21, boolean r22) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            org.telegram.messenger.AccountInstance r2 = r0.accountInstance
            org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
            org.telegram.messenger.ChatObject$Call r3 = r0.currentCall
            int r3 = r3.chatId
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2.getChat(r3)
            org.telegram.ui.Components.RLottieImageView r2 = r0.muteButton
            boolean r3 = r20.isSelfUser()
            r4 = 0
            r6 = 0
            r7 = 1
            if (r3 == 0) goto L_0x002c
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r0.participant
            long r8 = r3.raise_hand_rating
            int r3 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x002a
            goto L_0x002c
        L_0x002a:
            r3 = 0
            goto L_0x002d
        L_0x002c:
            r3 = 1
        L_0x002d:
            r2.setEnabled(r3)
            long r2 = android.os.SystemClock.elapsedRealtime()
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r8 = r0.participant
            long r9 = r8.lastVoiceUpdateTime
            long r2 = r2 - r9
            r9 = 500(0x1f4, double:2.47E-321)
            int r11 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r11 >= 0) goto L_0x0042
            boolean r2 = r8.hasVoiceDelayed
            goto L_0x0044
        L_0x0042:
            boolean r2 = r8.hasVoice
        L_0x0044:
            if (r22 != 0) goto L_0x0077
            long r11 = android.os.SystemClock.uptimeMillis()
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r0.participant
            long r13 = r3.lastSpeakTime
            long r11 = r11 - r13
            int r3 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r3 >= 0) goto L_0x0055
            r3 = 1
            goto L_0x0056
        L_0x0055:
            r3 = 0
        L_0x0056:
            boolean r8 = r0.isSpeaking
            if (r8 == 0) goto L_0x005e
            if (r3 == 0) goto L_0x005e
            if (r2 == 0) goto L_0x0077
        L_0x005e:
            r0.isSpeaking = r3
            boolean r3 = r0.updateRunnableScheduled
            if (r3 == 0) goto L_0x006b
            java.lang.Runnable r3 = r0.updateRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r3)
            r0.updateRunnableScheduled = r6
        L_0x006b:
            boolean r3 = r0.isSpeaking
            if (r3 == 0) goto L_0x0077
            java.lang.Runnable r3 = r0.updateRunnable
            long r9 = r9 - r11
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3, r9)
            r0.updateRunnableScheduled = r7
        L_0x0077:
            org.telegram.messenger.ChatObject$Call r3 = r0.currentCall
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r3 = r3.participants
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r8 = r0.participant
            org.telegram.tgnet.TLRPC$Peer r8 = r8.peer
            int r8 = org.telegram.messenger.MessageObject.getPeerId(r8)
            java.lang.Object r3 = r3.get(r8)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r3
            if (r3 == 0) goto L_0x008d
            r0.participant = r3
        L_0x008d:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r0.participant
            boolean r3 = r3.muted_by_you
            if (r3 == 0) goto L_0x009b
            boolean r3 = r20.isSelfUser()
            if (r3 != 0) goto L_0x009b
            r3 = 1
            goto L_0x009c
        L_0x009b:
            r3 = 0
        L_0x009c:
            boolean r8 = r20.isSelfUser()
            if (r8 == 0) goto L_0x00bc
            org.telegram.messenger.voip.VoIPService r8 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r8 == 0) goto L_0x00ba
            org.telegram.messenger.voip.VoIPService r8 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r8 = r8.isMicMute()
            if (r8 == 0) goto L_0x00ba
            boolean r8 = r0.isSpeaking
            if (r8 == 0) goto L_0x00b8
            if (r2 != 0) goto L_0x00ba
        L_0x00b8:
            r8 = 1
            goto L_0x00cb
        L_0x00ba:
            r8 = 0
            goto L_0x00cb
        L_0x00bc:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r8 = r0.participant
            boolean r8 = r8.muted
            if (r8 == 0) goto L_0x00c8
            boolean r8 = r0.isSpeaking
            if (r8 == 0) goto L_0x00b8
            if (r2 == 0) goto L_0x00b8
        L_0x00c8:
            if (r3 == 0) goto L_0x00ba
            goto L_0x00b8
        L_0x00cb:
            if (r8 == 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r9 = r0.participant
            boolean r9 = r9.can_self_unmute
        L_0x00d1:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r9 = r0.participant
            java.lang.String r9 = r9.about
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            r9 = r9 ^ r7
            r0.currentIconGray = r6
            java.lang.Runnable r10 = r0.checkRaiseRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r10)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r10 = r0.participant
            boolean r11 = r10.muted
            java.lang.String r13 = "voipgroup_listeningText"
            r14 = 2
            r15 = 4
            if (r11 == 0) goto L_0x00ef
            boolean r11 = r0.isSpeaking
            if (r11 == 0) goto L_0x00f1
        L_0x00ef:
            if (r3 == 0) goto L_0x0160
        L_0x00f1:
            boolean r2 = r10.can_self_unmute
            if (r2 == 0) goto L_0x010a
            if (r3 == 0) goto L_0x00f8
            goto L_0x010a
        L_0x00f8:
            java.lang.String r2 = r0.grayIconColor
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.currentIconGray = r7
            if (r9 == 0) goto L_0x0104
            r3 = 4
            goto L_0x0105
        L_0x0104:
            r3 = 0
        L_0x0105:
            r4 = r3
            r18 = r13
            goto L_0x017f
        L_0x010a:
            if (r2 != 0) goto L_0x0114
            long r10 = r10.raise_hand_rating
            int r2 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x0114
            r2 = 1
            goto L_0x0115
        L_0x0114:
            r2 = 0
        L_0x0115:
            if (r2 == 0) goto L_0x0148
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            long r16 = android.os.SystemClock.elapsedRealtime()
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r0.participant
            r18 = r13
            long r12 = r11.lastRaiseHandDate
            long r16 = r16 - r12
            int r11 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r11 == 0) goto L_0x013b
            r4 = 5000(0x1388, double:2.4703E-320)
            int r11 = (r16 > r4 ? 1 : (r16 == r4 ? 0 : -1))
            if (r11 <= 0) goto L_0x0132
            goto L_0x013b
        L_0x0132:
            java.lang.Runnable r3 = r0.checkRaiseRunnable
            long r4 = r4 - r16
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3, r4)
            r3 = 3
            goto L_0x0144
        L_0x013b:
            if (r3 == 0) goto L_0x013f
            r3 = 2
            goto L_0x0144
        L_0x013f:
            if (r9 == 0) goto L_0x0143
            r3 = 4
            goto L_0x0144
        L_0x0143:
            r3 = 0
        L_0x0144:
            r4 = r3
            r3 = r2
            r2 = r10
            goto L_0x0180
        L_0x0148:
            r18 = r13
            java.lang.String r4 = "voipgroup_mutedByAdminIcon"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            if (r3 == 0) goto L_0x0154
            r3 = 2
            goto L_0x0159
        L_0x0154:
            if (r9 == 0) goto L_0x0158
            r3 = 4
            goto L_0x0159
        L_0x0158:
            r3 = 0
        L_0x0159:
            r19 = r3
            r3 = r2
            r2 = r4
            r4 = r19
            goto L_0x0180
        L_0x0160:
            r18 = r13
            boolean r3 = r0.isSpeaking
            if (r3 == 0) goto L_0x0171
            if (r2 == 0) goto L_0x0171
            java.lang.String r2 = "voipgroup_speakingText"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3 = 0
            r4 = 1
            goto L_0x0180
        L_0x0171:
            java.lang.String r2 = r0.grayIconColor
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            if (r9 == 0) goto L_0x017b
            r3 = 4
            goto L_0x017c
        L_0x017b:
            r3 = 0
        L_0x017c:
            r0.currentIconGray = r7
            r4 = r3
        L_0x017f:
            r3 = 0
        L_0x0180:
            boolean r5 = r20.isSelfUser()
            if (r5 == 0) goto L_0x0192
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r0.statusTextView
            r5 = r5[r15]
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r5.setTextColor(r10)
            goto L_0x019f
        L_0x0192:
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r0.statusTextView
            r5 = r5[r15]
            java.lang.String r10 = r0.grayIconColor
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r5.setTextColor(r10)
        L_0x019f:
            boolean r5 = r20.isSelfUser()
            if (r5 == 0) goto L_0x01b6
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r0.statusTextView
            r5 = r5[r15]
            r9 = 2131627687(0x7f0e0ea7, float:1.8882645E38)
            java.lang.String r10 = "ThisIsYou"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9)
            goto L_0x01d1
        L_0x01b6:
            if (r9 == 0) goto L_0x01c8
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r0.statusTextView
            r5 = r5[r15]
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r9 = r0.participant
            java.lang.String r9 = r9.about
            java.lang.CharSequence r9 = org.telegram.messenger.AndroidUtilities.replaceNewLines(r9)
            r5.setText(r9)
            goto L_0x01d1
        L_0x01c8:
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r0.statusTextView
            r5 = r5[r15]
            java.lang.String r9 = ""
            r5.setText(r9)
        L_0x01d1:
            android.animation.AnimatorSet r5 = r0.animatorSet
            if (r5 == 0) goto L_0x01df
            int r9 = r0.currentStatus
            if (r4 != r9) goto L_0x01dd
            int r9 = r0.lastMuteColor
            if (r9 == r2) goto L_0x01df
        L_0x01dd:
            r9 = 1
            goto L_0x01e0
        L_0x01df:
            r9 = 0
        L_0x01e0:
            r10 = 0
            if (r1 == 0) goto L_0x01e5
            if (r9 == 0) goto L_0x01ec
        L_0x01e5:
            if (r5 == 0) goto L_0x01ec
            r5.cancel()
            r0.animatorSet = r10
        L_0x01ec:
            if (r1 == 0) goto L_0x01f4
            int r5 = r0.lastMuteColor
            if (r5 != r2) goto L_0x01f4
            if (r9 == 0) goto L_0x022f
        L_0x01f4:
            if (r1 == 0) goto L_0x0214
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            int r11 = r0.lastMuteColor
            r0.lastMuteColor = r2
            float[] r12 = new float[r14]
            r12 = {0, NUM} // fill-array
            android.animation.ValueAnimator r12 = android.animation.ValueAnimator.ofFloat(r12)
            org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$Wks65hHhbzbC-yE71sZqLFpgAyQ r13 = new org.telegram.ui.Cells.-$$Lambda$GroupCallUserCell$Wks65hHhbzbC-yE71sZqLFpgAyQ
            r13.<init>(r11, r2)
            r12.addUpdateListener(r13)
            r5.add(r12)
            goto L_0x0230
        L_0x0214:
            org.telegram.ui.Components.RLottieImageView r5 = r0.muteButton
            android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
            r0.lastMuteColor = r2
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r11.<init>(r2, r12)
            r5.setColorFilter(r11)
            org.telegram.ui.Components.RLottieImageView r5 = r0.muteButton
            android.graphics.drawable.Drawable r5 = r5.getDrawable()
            r11 = 620756991(0x24ffffff, float:1.11022296E-16)
            r2 = r2 & r11
            org.telegram.ui.ActionBar.Theme.setSelectorDrawableColor(r5, r2, r7)
        L_0x022f:
            r5 = r10
        L_0x0230:
            if (r4 != r7) goto L_0x0281
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r0.participant
            int r2 = org.telegram.messenger.ChatObject.getParticipantVolume(r2)
            int r11 = r2 / 100
            r12 = 2131627490(0x7f0e0de2, float:1.8882246E38)
            java.lang.String r13 = "Speaking"
            r14 = 100
            if (r11 == r14) goto L_0x026f
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r7]
            android.graphics.drawable.Drawable r15 = r0.speakingDrawable
            r6.setLeftDrawable((android.graphics.drawable.Drawable) r15)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.statusTextView
            r6 = r6[r7]
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            if (r2 >= r14) goto L_0x0258
            r11 = 1
        L_0x0258:
            r15.append(r11)
            java.lang.String r2 = "% "
            r15.append(r2)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r15.append(r2)
            java.lang.String r2 = r15.toString()
            r6.setText(r2)
            goto L_0x0281
        L_0x026f:
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.statusTextView
            r2 = r2[r7]
            r2.setLeftDrawable((android.graphics.drawable.Drawable) r10)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.statusTextView
            r2 = r2[r7]
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r2.setText(r6)
        L_0x0281:
            boolean r2 = r20.isSelfUser()
            r6 = 0
            if (r2 == 0) goto L_0x028e
            r2 = 4
            r0.applyStatus(r2)
            goto L_0x0326
        L_0x028e:
            if (r1 == 0) goto L_0x0296
            int r2 = r0.currentStatus
            if (r4 != r2) goto L_0x0296
            if (r9 == 0) goto L_0x0326
        L_0x0296:
            if (r1 == 0) goto L_0x0321
            if (r5 != 0) goto L_0x029f
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
        L_0x029f:
            r2 = 1065353216(0x3var_, float:1.0)
            r9 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            if (r4 != 0) goto L_0x02df
            r11 = 0
        L_0x02a6:
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.statusTextView
            int r13 = r12.length
            if (r11 >= r13) goto L_0x0324
            r12 = r12[r11]
            android.util.Property r13 = android.view.View.TRANSLATION_Y
            float[] r14 = new float[r7]
            if (r11 != r4) goto L_0x02b5
            r15 = 0
            goto L_0x02ba
        L_0x02b5:
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r15 = (float) r15
        L_0x02ba:
            r16 = 0
            r14[r16] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r5.add(r12)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.statusTextView
            r12 = r12[r11]
            android.util.Property r13 = android.view.View.ALPHA
            float[] r14 = new float[r7]
            if (r11 != r4) goto L_0x02d2
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x02d3
        L_0x02d2:
            r15 = 0
        L_0x02d3:
            r14[r16] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r5.add(r12)
            int r11 = r11 + 1
            goto L_0x02a6
        L_0x02df:
            r11 = 0
        L_0x02e0:
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.statusTextView
            int r13 = r12.length
            if (r11 >= r13) goto L_0x0324
            r12 = r12[r11]
            android.util.Property r13 = android.view.View.TRANSLATION_Y
            float[] r14 = new float[r7]
            if (r11 != r4) goto L_0x02f1
            r15 = 0
        L_0x02ee:
            r16 = 0
            goto L_0x02fe
        L_0x02f1:
            if (r11 != 0) goto L_0x02f6
            r15 = 1073741824(0x40000000, float:2.0)
            goto L_0x02f8
        L_0x02f6:
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
        L_0x02f8:
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            goto L_0x02ee
        L_0x02fe:
            r14[r16] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r5.add(r12)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.statusTextView
            r12 = r12[r11]
            android.util.Property r13 = android.view.View.ALPHA
            float[] r14 = new float[r7]
            if (r11 != r4) goto L_0x0314
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x0315
        L_0x0314:
            r15 = 0
        L_0x0315:
            r14[r16] = r15
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r5.add(r12)
            int r11 = r11 + 1
            goto L_0x02e0
        L_0x0321:
            r0.applyStatus(r4)
        L_0x0324:
            r0.currentStatus = r4
        L_0x0326:
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r2 = r0.avatarWavesDrawable
            r2.setMuted(r4, r1)
            if (r5 == 0) goto L_0x0356
            android.animation.AnimatorSet r2 = r0.animatorSet
            if (r2 == 0) goto L_0x0336
            r2.cancel()
            r0.animatorSet = r10
        L_0x0336:
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            r0.animatorSet = r2
            org.telegram.ui.Cells.GroupCallUserCell$2 r9 = new org.telegram.ui.Cells.GroupCallUserCell$2
            r9.<init>(r4)
            r2.addListener(r9)
            android.animation.AnimatorSet r2 = r0.animatorSet
            r2.playTogether(r5)
            android.animation.AnimatorSet r2 = r0.animatorSet
            r11 = 180(0xb4, double:8.9E-322)
            r2.setDuration(r11)
            android.animation.AnimatorSet r2 = r0.animatorSet
            r2.start()
        L_0x0356:
            if (r1 == 0) goto L_0x0364
            boolean r2 = r0.lastMuted
            if (r2 != r8) goto L_0x0364
            boolean r2 = r0.lastRaisedHand
            if (r2 == r3) goto L_0x0361
            goto L_0x0364
        L_0x0361:
            r4 = 0
            goto L_0x03f3
        L_0x0364:
            r2 = 21
            if (r3 == 0) goto L_0x0384
            org.telegram.ui.Components.RLottieDrawable r5 = r0.muteDrawable
            r9 = 84
            boolean r5 = r5.setCustomEndFrame(r9)
            if (r1 == 0) goto L_0x037d
            org.telegram.ui.Components.RLottieDrawable r9 = r0.muteDrawable
            java.lang.Runnable r10 = r0.raiseHandCallback
            r11 = 83
            r9.setOnFinishCallback(r10, r11)
            r11 = 0
            goto L_0x03ab
        L_0x037d:
            org.telegram.ui.Components.RLottieDrawable r9 = r0.muteDrawable
            r11 = 0
            r9.setOnFinishCallback(r10, r11)
            goto L_0x03ab
        L_0x0384:
            r11 = 0
            org.telegram.ui.Components.RLottieImageView r5 = r0.muteButton
            org.telegram.ui.Components.RLottieDrawable r9 = r0.muteDrawable
            r5.setAnimation(r9)
            org.telegram.ui.Components.RLottieDrawable r5 = r0.muteDrawable
            r5.setOnFinishCallback(r10, r11)
            if (r8 == 0) goto L_0x039e
            boolean r5 = r0.lastRaisedHand
            if (r5 == 0) goto L_0x039e
            org.telegram.ui.Components.RLottieDrawable r5 = r0.muteDrawable
            boolean r5 = r5.setCustomEndFrame(r2)
            goto L_0x03ab
        L_0x039e:
            org.telegram.ui.Components.RLottieDrawable r5 = r0.muteDrawable
            if (r8 == 0) goto L_0x03a5
            r9 = 64
            goto L_0x03a7
        L_0x03a5:
            r9 = 42
        L_0x03a7:
            boolean r5 = r5.setCustomEndFrame(r9)
        L_0x03ab:
            if (r1 == 0) goto L_0x03df
            if (r5 == 0) goto L_0x03d8
            r1 = 3
            if (r4 != r1) goto L_0x03ba
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r2 = 63
            r1.setCurrentFrame(r2)
            goto L_0x03d8
        L_0x03ba:
            if (r8 == 0) goto L_0x03c9
            boolean r1 = r0.lastRaisedHand
            if (r1 == 0) goto L_0x03c9
            if (r3 != 0) goto L_0x03c9
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r2 = 0
            r1.setCurrentFrame(r2)
            goto L_0x03d8
        L_0x03c9:
            if (r8 == 0) goto L_0x03d3
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r2 = 43
            r1.setCurrentFrame(r2)
            goto L_0x03d8
        L_0x03d3:
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            r1.setCurrentFrame(r2)
        L_0x03d8:
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            r1.playAnimation()
            r4 = 0
            goto L_0x03ef
        L_0x03df:
            org.telegram.ui.Components.RLottieDrawable r1 = r0.muteDrawable
            int r2 = r1.getCustomEndFrame()
            int r2 = r2 - r7
            r4 = 0
            r1.setCurrentFrame(r2, r4, r7)
            org.telegram.ui.Components.RLottieImageView r1 = r0.muteButton
            r1.invalidate()
        L_0x03ef:
            r0.lastMuted = r8
            r0.lastRaisedHand = r3
        L_0x03f3:
            boolean r1 = r0.isSpeaking
            if (r1 != 0) goto L_0x03fe
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r1 = r0.avatarWavesDrawable
            r2 = 0
            r1.setAmplitude(r2)
        L_0x03fe:
            org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r1 = r0.avatarWavesDrawable
            boolean r2 = r0.isSpeaking
            if (r2 == 0) goto L_0x040c
            float r2 = r0.progressToAvatarPreview
            int r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x040c
            r6 = 1
            goto L_0x040d
        L_0x040c:
            r6 = 0
        L_0x040d:
            r1.setShowWaves(r6, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCallUserCell.applyParticipantChanges(boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$applyParticipantChanges$6 */
    public /* synthetic */ void lambda$applyParticipantChanges$6$GroupCallUserCell(int i, int i2, ValueAnimator valueAnimator) {
        int offsetColor = AndroidUtilities.getOffsetColor(i, i2, valueAnimator.getAnimatedFraction(), 1.0f);
        this.muteButton.setColorFilter(new PorterDuffColorFilter(offsetColor, PorterDuff.Mode.MULTIPLY));
        Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), offsetColor & NUM, true);
    }

    /* access modifiers changed from: private */
    public void applyStatus(int i) {
        float f;
        int i2 = 0;
        if (i == 0) {
            while (true) {
                SimpleTextView[] simpleTextViewArr = this.statusTextView;
                if (i2 < simpleTextViewArr.length) {
                    simpleTextViewArr[i2].setTranslationY(i2 == i ? 0.0f : (float) AndroidUtilities.dp(-2.0f));
                    this.statusTextView[i2].setAlpha(i2 == i ? 1.0f : 0.0f);
                    i2++;
                } else {
                    return;
                }
            }
        } else {
            while (true) {
                SimpleTextView[] simpleTextViewArr2 = this.statusTextView;
                if (i2 < simpleTextViewArr2.length) {
                    SimpleTextView simpleTextView = simpleTextViewArr2[i2];
                    if (i2 == i) {
                        f = 0.0f;
                    } else {
                        f = (float) AndroidUtilities.dp(i2 == 0 ? 2.0f : -2.0f);
                    }
                    simpleTextView.setTranslationY(f);
                    this.statusTextView[i2].setAlpha(i2 == i ? 1.0f : 0.0f);
                    i2++;
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.needDivider) {
            float f = this.progressToAvatarPreview;
            if (f != 0.0f) {
                this.dividerPaint.setAlpha((int) ((1.0f - f) * 255.0f));
            } else {
                this.dividerPaint.setAlpha((int) ((1.0f - this.statusTextView[4].getFullAlpha()) * 255.0f));
            }
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), this.dividerPaint);
        }
        int left = this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2);
        int top = this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2);
        this.avatarWavesDrawable.update();
        if (this.progressToAvatarPreview == 0.0f) {
            this.avatarWavesDrawable.draw(canvas, (float) left, (float) top, this);
        }
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
        private boolean hasCustomColor;
        boolean invalidateColor = true;
        private int isMuted;
        private float progressToMuted = 0.0f;
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

        public void update() {
            float f = this.animateToAmplitude;
            float f2 = this.amplitude;
            if (f != f2) {
                float f3 = this.animateAmplitudeDiff;
                float f4 = f2 + (16.0f * f3);
                this.amplitude = f4;
                if (f3 > 0.0f) {
                    if (f4 > f) {
                        this.amplitude = f;
                    }
                } else if (f4 < f) {
                    this.amplitude = f;
                }
            }
            boolean z = this.showWaves;
            if (z) {
                float f5 = this.wavesEnter;
                if (f5 != 1.0f) {
                    float f6 = f5 + 0.045714285f;
                    this.wavesEnter = f6;
                    if (f6 > 1.0f) {
                        this.wavesEnter = 1.0f;
                        return;
                    }
                    return;
                }
            }
            if (!z) {
                float f7 = this.wavesEnter;
                if (f7 != 0.0f) {
                    float f8 = f7 - 0.045714285f;
                    this.wavesEnter = f8;
                    if (f8 < 0.0f) {
                        this.wavesEnter = 0.0f;
                    }
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:23:0x005d  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void draw(android.graphics.Canvas r8, float r9, float r10, android.view.View r11) {
            /*
                r7 = this;
                float r0 = r7.amplitude
                r1 = 1053609165(0x3ecccccd, float:0.4)
                float r0 = r0 * r1
                r1 = 1061997773(0x3f4ccccd, float:0.8)
                float r0 = r0 + r1
                boolean r1 = r7.showWaves
                r2 = 0
                if (r1 != 0) goto L_0x0016
                float r1 = r7.wavesEnter
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x00a5
            L_0x0016:
                r8.save()
                org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                float r3 = r7.wavesEnter
                float r1 = r1.getInterpolation(r3)
                float r0 = r0 * r1
                r8.scale(r0, r0, r9, r10)
                boolean r0 = r7.hasCustomColor
                r1 = 1065353216(0x3var_, float:1.0)
                if (r0 != 0) goto L_0x0084
                int r0 = r7.isMuted
                r3 = 1037726734(0x3dda740e, float:0.10666667)
                r4 = 1
                if (r0 == r4) goto L_0x0046
                float r5 = r7.progressToMuted
                int r6 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
                if (r6 == 0) goto L_0x0046
                float r5 = r5 + r3
                r7.progressToMuted = r5
                int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
                if (r0 <= 0) goto L_0x0043
                r7.progressToMuted = r1
            L_0x0043:
                r7.invalidateColor = r4
                goto L_0x0059
            L_0x0046:
                if (r0 != r4) goto L_0x0059
                float r0 = r7.progressToMuted
                int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r5 == 0) goto L_0x0059
                float r0 = r0 - r3
                r7.progressToMuted = r0
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 >= 0) goto L_0x0057
                r7.progressToMuted = r2
            L_0x0057:
                r7.invalidateColor = r4
            L_0x0059:
                boolean r0 = r7.invalidateColor
                if (r0 == 0) goto L_0x0084
                java.lang.String r0 = "voipgroup_speakingText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                int r3 = r7.isMuted
                r4 = 2
                if (r3 != r4) goto L_0x006b
                java.lang.String r3 = "voipgroup_mutedByAdminIcon"
                goto L_0x006d
            L_0x006b:
                java.lang.String r3 = "voipgroup_listeningText"
            L_0x006d:
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                float r4 = r7.progressToMuted
                int r0 = androidx.core.graphics.ColorUtils.blendARGB(r0, r3, r4)
                org.telegram.ui.Components.BlobDrawable r3 = r7.blobDrawable
                android.graphics.Paint r3 = r3.paint
                r4 = 38
                int r0 = androidx.core.graphics.ColorUtils.setAlphaComponent(r0, r4)
                r3.setColor(r0)
            L_0x0084:
                org.telegram.ui.Components.BlobDrawable r0 = r7.blobDrawable
                float r3 = r7.amplitude
                r0.update(r3, r1)
                org.telegram.ui.Components.BlobDrawable r0 = r7.blobDrawable
                android.graphics.Paint r3 = r0.paint
                r0.draw(r9, r10, r8, r3)
                org.telegram.ui.Components.BlobDrawable r0 = r7.blobDrawable2
                float r3 = r7.amplitude
                r0.update(r3, r1)
                org.telegram.ui.Components.BlobDrawable r0 = r7.blobDrawable2
                org.telegram.ui.Components.BlobDrawable r1 = r7.blobDrawable
                android.graphics.Paint r1 = r1.paint
                r0.draw(r9, r10, r8, r1)
                r8.restore()
            L_0x00a5:
                float r8 = r7.wavesEnter
                int r8 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
                if (r8 == 0) goto L_0x00ae
                r11.invalidate()
            L_0x00ae:
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
            this.hasCustomColor = true;
            this.blobDrawable.paint.setColor(i);
        }

        public void setMuted(int i, boolean z) {
            this.isMuted = i;
            if (!z) {
                this.progressToMuted = i != 1 ? 1.0f : 0.0f;
            }
            this.invalidateColor = true;
        }
    }

    public BackupImageView getAvatarImageView() {
        return this.avatarImageView;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        String str;
        int i;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (accessibilityNodeInfo.isEnabled() && Build.VERSION.SDK_INT >= 21) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.participant;
            if (!tLRPC$TL_groupCallParticipant.muted || tLRPC$TL_groupCallParticipant.can_self_unmute) {
                i = NUM;
                str = "VoipMute";
            } else {
                i = NUM;
                str = "VoipUnmute";
            }
            accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString(str, i)));
        }
    }
}
