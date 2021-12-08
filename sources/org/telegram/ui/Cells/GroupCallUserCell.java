package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;

public class GroupCallUserCell extends FrameLayout {
    private AccountInstance accountInstance;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private AvatarDrawable avatarDrawable;
    /* access modifiers changed from: private */
    public BackupImageView avatarImageView;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    private AvatarWavesDrawable avatarWavesDrawable;
    private Runnable checkRaiseRunnable = new GroupCallUserCell$$ExternalSyntheticLambda4(this);
    private ChatObject.Call currentCall;
    private TLRPC.Chat currentChat;
    private boolean currentIconGray;
    private int currentStatus;
    private TLRPC.User currentUser;
    private Paint dividerPaint;
    private SimpleTextView fullAboutTextView;
    private String grayIconColor = "voipgroup_mutedIcon";
    private boolean hasAvatar;
    private boolean isSpeaking;
    private int lastMuteColor;
    private boolean lastMuted;
    private boolean lastRaisedHand;
    private RLottieImageView muteButton;
    private RLottieDrawable muteDrawable;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private TLRPC.TL_groupCallParticipant participant;
    /* access modifiers changed from: private */
    public float progressToAvatarPreview;
    private Runnable raiseHandCallback = new GroupCallUserCell$$ExternalSyntheticLambda3(this);
    private long selfId;
    private Runnable shakeHandCallback = new GroupCallUserCell$$ExternalSyntheticLambda2(this);
    private RLottieDrawable shakeHandDrawable;
    private Drawable speakingDrawable;
    /* access modifiers changed from: private */
    public SimpleTextView[] statusTextView = new SimpleTextView[5];
    private Runnable updateRunnable = new GroupCallUserCell$$ExternalSyntheticLambda5(this);
    private boolean updateRunnableScheduled;
    private Runnable updateVoiceRunnable = new GroupCallUserCell$$ExternalSyntheticLambda6(this);
    private boolean updateVoiceRunnableScheduled;

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-GroupCallUserCell  reason: not valid java name */
    public /* synthetic */ void m1534lambda$new$0$orgtelegramuiCellsGroupCallUserCell() {
        this.shakeHandDrawable.setOnFinishCallback((Runnable) null, 0);
        this.muteDrawable.setOnFinishCallback((Runnable) null, 0);
        this.muteButton.setAnimation(this.muteDrawable);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-GroupCallUserCell  reason: not valid java name */
    public /* synthetic */ void m1535lambda$new$1$orgtelegramuiCellsGroupCallUserCell() {
        int endFrame;
        int startFrame;
        int num = Utilities.random.nextInt(100);
        if (num < 32) {
            startFrame = 0;
            endFrame = 120;
        } else if (num < 64) {
            startFrame = 120;
            endFrame = 240;
        } else if (num < 97) {
            startFrame = 240;
            endFrame = 420;
        } else if (num == 98) {
            startFrame = 420;
            endFrame = 540;
        } else {
            startFrame = 540;
            endFrame = 720;
        }
        this.shakeHandDrawable.setCustomEndFrame(endFrame);
        this.shakeHandDrawable.setOnFinishCallback(this.shakeHandCallback, endFrame - 1);
        this.muteButton.setAnimation(this.shakeHandDrawable);
        this.shakeHandDrawable.setCurrentFrame(startFrame);
        this.muteButton.playAnimation();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Cells-GroupCallUserCell  reason: not valid java name */
    public /* synthetic */ void m1536lambda$new$2$orgtelegramuiCellsGroupCallUserCell() {
        applyParticipantChanges(true, true);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Cells-GroupCallUserCell  reason: not valid java name */
    public /* synthetic */ void m1537lambda$new$3$orgtelegramuiCellsGroupCallUserCell() {
        this.isSpeaking = false;
        applyParticipantChanges(true, true);
        this.avatarWavesDrawable.setAmplitude(0.0d);
        this.updateRunnableScheduled = false;
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Cells-GroupCallUserCell  reason: not valid java name */
    public /* synthetic */ void m1538lambda$new$4$orgtelegramuiCellsGroupCallUserCell() {
        applyParticipantChanges(true, true);
        this.updateVoiceRunnableScheduled = false;
    }

    public void setProgressToAvatarPreview(float progressToAvatarPreview2) {
        this.progressToAvatarPreview = progressToAvatarPreview2;
        this.nameTextView.setTranslationX(((float) (LocaleController.isRTL ? AndroidUtilities.dp(53.0f) : -AndroidUtilities.dp(53.0f))) * progressToAvatarPreview2);
        boolean z = true;
        if (!isSelfUser() || progressToAvatarPreview2 <= 0.0f) {
            this.fullAboutTextView.setVisibility(8);
            int i = 0;
            while (true) {
                SimpleTextView[] simpleTextViewArr = this.statusTextView;
                if (i >= simpleTextViewArr.length) {
                    break;
                }
                if (TextUtils.isEmpty(simpleTextViewArr[4].getText()) || this.statusTextView[4].getLineCount() <= 1) {
                    this.statusTextView[i].setTranslationX(((float) (LocaleController.isRTL ? AndroidUtilities.dp(53.0f) : -AndroidUtilities.dp(53.0f))) * progressToAvatarPreview2);
                    this.statusTextView[i].setFullLayoutAdditionalWidth(0, 0);
                } else {
                    this.statusTextView[i].setFullLayoutAdditionalWidth(AndroidUtilities.dp(92.0f), LocaleController.isRTL ? AndroidUtilities.dp(48.0f) : AndroidUtilities.dp(53.0f));
                    this.statusTextView[i].setFullAlpha(progressToAvatarPreview2);
                    this.statusTextView[i].setTranslationX(0.0f);
                    this.statusTextView[i].invalidate();
                }
                i++;
            }
        } else {
            this.fullAboutTextView.setTranslationX(((float) (LocaleController.isRTL ? -AndroidUtilities.dp(53.0f) : AndroidUtilities.dp(53.0f))) * (1.0f - progressToAvatarPreview2));
            this.fullAboutTextView.setVisibility(0);
            this.fullAboutTextView.setAlpha(progressToAvatarPreview2);
            this.statusTextView[4].setAlpha(1.0f - progressToAvatarPreview2);
            SimpleTextView simpleTextView = this.statusTextView[4];
            boolean z2 = LocaleController.isRTL;
            int dp = AndroidUtilities.dp(53.0f);
            if (!z2) {
                dp = -dp;
            }
            simpleTextView.setTranslationX(((float) dp) * progressToAvatarPreview2);
        }
        this.avatarImageView.setAlpha(progressToAvatarPreview2 == 0.0f ? 1.0f : 0.0f);
        AvatarWavesDrawable avatarWavesDrawable2 = this.avatarWavesDrawable;
        if (!this.isSpeaking || progressToAvatarPreview2 != 0.0f) {
            z = false;
        }
        avatarWavesDrawable2.setShowWaves(z, this);
        this.muteButton.setAlpha(1.0f - progressToAvatarPreview2);
        this.muteButton.setScaleX(((1.0f - progressToAvatarPreview2) * 0.4f) + 0.6f);
        this.muteButton.setScaleY(((1.0f - progressToAvatarPreview2) * 0.4f) + 0.6f);
        invalidate();
    }

    public AvatarWavesDrawable getAvatarWavesDrawable() {
        return this.avatarWavesDrawable;
    }

    public void setUploadProgress(float progress, boolean animated) {
        this.avatarProgressView.setProgress(progress);
        if (progress < 1.0f) {
            AndroidUtilities.updateViewVisibilityAnimated(this.avatarProgressView, true, 1.0f, animated);
        } else {
            AndroidUtilities.updateViewVisibilityAnimated(this.avatarProgressView, false, 1.0f, animated);
        }
    }

    public void setDrawAvatar(boolean draw) {
        if (this.avatarImageView.getImageReceiver().getVisible() != draw) {
            this.avatarImageView.getImageReceiver().setVisible(draw, true);
        }
    }

    private static class VerifiedDrawable extends Drawable {
        private Drawable[] drawables;

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
            int a = 0;
            while (true) {
                Drawable[] drawableArr = this.drawables;
                if (a < drawableArr.length) {
                    drawableArr[a].setBounds(getBounds());
                    this.drawables[a].draw(canvas);
                    a++;
                } else {
                    return;
                }
            }
        }

        public void setAlpha(int alpha) {
            int a = 0;
            while (true) {
                Drawable[] drawableArr = this.drawables;
                if (a < drawableArr.length) {
                    drawableArr[a].setAlpha(alpha);
                    a++;
                } else {
                    return;
                }
            }
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return -2;
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
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 11.0f, 6.0f, LocaleController.isRTL ? 11.0f : 0.0f, 0.0f));
        AnonymousClass1 r5 = new RadialProgressView(context2) {
            private Paint paint;

            {
                Paint paint2 = new Paint(1);
                this.paint = paint2;
                paint2.setColor(NUM);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (GroupCallUserCell.this.avatarImageView.getImageReceiver().hasNotThumb() && GroupCallUserCell.this.avatarImageView.getAlpha() > 0.0f) {
                    this.paint.setAlpha((int) (GroupCallUserCell.this.avatarImageView.getImageReceiver().getCurrentAlpha() * 85.0f * GroupCallUserCell.this.avatarImageView.getAlpha()));
                    canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, ((float) getMeasuredWidth()) / 2.0f, this.paint);
                }
                GroupCallUserCell.this.avatarProgressView.setProgressColor(ColorUtils.setAlphaComponent(-1, (int) (GroupCallUserCell.this.avatarImageView.getImageReceiver().getCurrentAlpha() * 255.0f * GroupCallUserCell.this.avatarImageView.getAlpha())));
                super.onDraw(canvas);
            }
        };
        this.avatarProgressView = r5;
        r5.setSize(AndroidUtilities.dp(26.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.avatarProgressView.setNoProgress(false);
        addView(this.avatarProgressView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 11.0f, 6.0f, LocaleController.isRTL ? 11.0f : 0.0f, 0.0f));
        AndroidUtilities.updateViewVisibilityAnimated(this.avatarProgressView, false, 1.0f, false);
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("voipgroup_nameText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setDrawablePadding(AndroidUtilities.dp(6.0f));
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 54.0f : 67.0f, 10.0f, LocaleController.isRTL ? 67.0f : 54.0f, 0.0f));
        Drawable drawable = context.getResources().getDrawable(NUM);
        this.speakingDrawable = drawable;
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("voipgroup_speakingText"), PorterDuff.Mode.MULTIPLY));
        int a = 0;
        while (true) {
            SimpleTextView[] simpleTextViewArr = this.statusTextView;
            if (a >= simpleTextViewArr.length) {
                break;
            }
            final int num = a;
            simpleTextViewArr[a] = new SimpleTextView(context2) {
                float originalAlpha;

                public void setAlpha(float alpha) {
                    this.originalAlpha = alpha;
                    if (num == 4) {
                        float alphaOverride = GroupCallUserCell.this.statusTextView[4].getFullAlpha();
                        if (GroupCallUserCell.this.isSelfUser() && GroupCallUserCell.this.progressToAvatarPreview > 0.0f) {
                            super.setAlpha(1.0f - GroupCallUserCell.this.progressToAvatarPreview);
                        } else if (alphaOverride > 0.0f) {
                            super.setAlpha(Math.max(alpha, alphaOverride));
                        } else {
                            super.setAlpha(alpha);
                        }
                    } else {
                        super.setAlpha(alpha * (1.0f - GroupCallUserCell.this.statusTextView[4].getFullAlpha()));
                    }
                }

                public void setTranslationY(float translationY) {
                    if (num == 4 && getFullAlpha() > 0.0f) {
                        translationY = 0.0f;
                    }
                    super.setTranslationY(translationY);
                }

                public float getAlpha() {
                    return this.originalAlpha;
                }

                public void setFullAlpha(float value) {
                    super.setFullAlpha(value);
                    for (int a = 0; a < GroupCallUserCell.this.statusTextView.length; a++) {
                        GroupCallUserCell.this.statusTextView[a].setAlpha(GroupCallUserCell.this.statusTextView[a].getAlpha());
                    }
                }
            };
            this.statusTextView[a].setTextSize(15);
            this.statusTextView[a].setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            if (a == 4) {
                this.statusTextView[a].setBuildFullLayout(true);
                this.statusTextView[a].setTextColor(Theme.getColor("voipgroup_mutedIcon"));
                addView(this.statusTextView[a], LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 54.0f : 67.0f, 32.0f, LocaleController.isRTL ? 67.0f : 54.0f, 0.0f));
            } else {
                if (a == 0) {
                    this.statusTextView[a].setTextColor(Theme.getColor("voipgroup_listeningText"));
                    this.statusTextView[a].setText(LocaleController.getString("Listening", NUM));
                } else if (a == 1) {
                    this.statusTextView[a].setTextColor(Theme.getColor("voipgroup_speakingText"));
                    this.statusTextView[a].setText(LocaleController.getString("Speaking", NUM));
                    this.statusTextView[a].setDrawablePadding(AndroidUtilities.dp(2.0f));
                } else if (a == 2) {
                    this.statusTextView[a].setTextColor(Theme.getColor("voipgroup_mutedByAdminIcon"));
                    this.statusTextView[a].setText(LocaleController.getString("VoipGroupMutedForMe", NUM));
                } else if (a == 3) {
                    this.statusTextView[a].setTextColor(Theme.getColor("voipgroup_listeningText"));
                    this.statusTextView[a].setText(LocaleController.getString("WantsToSpeak", NUM));
                }
                addView(this.statusTextView[a], LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 54.0f : 67.0f, 32.0f, LocaleController.isRTL ? 67.0f : 54.0f, 0.0f));
            }
            a++;
        }
        SimpleTextView simpleTextView2 = new SimpleTextView(context2);
        this.fullAboutTextView = simpleTextView2;
        simpleTextView2.setMaxLines(3);
        this.fullAboutTextView.setTextSize(15);
        this.fullAboutTextView.setTextColor(Theme.getColor("voipgroup_mutedIcon"));
        this.fullAboutTextView.setVisibility(8);
        addView(this.fullAboutTextView, LayoutHelper.createFrame(-1, 60.0f, (LocaleController.isRTL ? 5 : 3) | 48, 14.0f, 32.0f, 14.0f, 0.0f));
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
        this.muteButton.setOnClickListener(new GroupCallUserCell$$ExternalSyntheticLambda1(this));
        this.avatarWavesDrawable = new AvatarWavesDrawable(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(29.0f));
        setWillNotDraw(false);
        setFocusable(true);
    }

    /* access modifiers changed from: protected */
    /* renamed from: onMuteClick */
    public void m1539lambda$new$5$orgtelegramuiCellsGroupCallUserCell(GroupCallUserCell cell) {
    }

    public int getClipHeight() {
        SimpleTextView aboutTextView;
        if (TextUtils.isEmpty(this.fullAboutTextView.getText()) || !this.hasAvatar) {
            aboutTextView = this.statusTextView[4];
        } else {
            aboutTextView = this.fullAboutTextView;
        }
        if (aboutTextView.getLineCount() <= 1) {
            return getMeasuredHeight();
        }
        return aboutTextView.getTop() + aboutTextView.getTextHeight() + AndroidUtilities.dp(8.0f);
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
        if (this.selfId > 0) {
            TLRPC.User user = this.currentUser;
            if (user == null || user.id != this.selfId) {
                return false;
            }
            return true;
        }
        TLRPC.Chat chat = this.currentChat;
        if (chat == null || chat.id != (-this.selfId)) {
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

    public boolean hasAvatarSet() {
        return this.avatarImageView.getImageReceiver().hasNotThumb();
    }

    public void setData(AccountInstance account, TLRPC.TL_groupCallParticipant groupCallParticipant, ChatObject.Call call, long self, TLRPC.FileLocation uploadingAvatar, boolean animated) {
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant = groupCallParticipant;
        this.currentCall = call;
        this.accountInstance = account;
        this.selfId = self;
        this.participant = tL_groupCallParticipant;
        long id = MessageObject.getPeerId(tL_groupCallParticipant.peer);
        boolean z = false;
        if (id > 0) {
            TLRPC.User user = this.accountInstance.getMessagesController().getUser(Long.valueOf(id));
            this.currentUser = user;
            this.currentChat = null;
            this.avatarDrawable.setInfo(user);
            this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            SimpleTextView simpleTextView = this.nameTextView;
            TLRPC.User user2 = this.currentUser;
            simpleTextView.setRightDrawable((Drawable) (user2 == null || !user2.verified) ? null : new VerifiedDrawable(getContext()));
            this.avatarImageView.getImageReceiver().setCurrentAccount(account.getCurrentAccount());
            if (uploadingAvatar != null) {
                this.hasAvatar = true;
                this.avatarImageView.setImage(ImageLocation.getForLocal(uploadingAvatar), "50_50", (Drawable) this.avatarDrawable, (Object) null);
            } else {
                ImageLocation imageLocation = ImageLocation.getForUser(this.currentUser, 1);
                if (imageLocation != null) {
                    z = true;
                }
                this.hasAvatar = z;
                this.avatarImageView.setImage(imageLocation, "50_50", (Drawable) this.avatarDrawable, (Object) this.currentUser);
            }
        } else {
            TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-id));
            this.currentChat = chat;
            this.currentUser = null;
            this.avatarDrawable.setInfo(chat);
            TLRPC.Chat chat2 = this.currentChat;
            if (chat2 != null) {
                this.nameTextView.setText(chat2.title);
                this.nameTextView.setRightDrawable((Drawable) this.currentChat.verified ? new VerifiedDrawable(getContext()) : null);
                this.avatarImageView.getImageReceiver().setCurrentAccount(account.getCurrentAccount());
                if (uploadingAvatar != null) {
                    this.hasAvatar = true;
                    this.avatarImageView.setImage(ImageLocation.getForLocal(uploadingAvatar), "50_50", (Drawable) this.avatarDrawable, (Object) null);
                } else {
                    ImageLocation imageLocation2 = ImageLocation.getForChat(this.currentChat, 1);
                    if (imageLocation2 != null) {
                        z = true;
                    }
                    this.hasAvatar = z;
                    this.avatarImageView.setImage(imageLocation2, "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChat);
                }
            }
        }
        applyParticipantChanges(animated);
    }

    public void setDrawDivider(boolean draw) {
        this.needDivider = draw;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        applyParticipantChanges(false);
    }

    public TLRPC.TL_groupCallParticipant getParticipant() {
        return this.participant;
    }

    public void setAmplitude(double value) {
        if (value > 1.5d) {
            if (this.updateRunnableScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            }
            if (!this.isSpeaking) {
                this.isSpeaking = true;
                applyParticipantChanges(true);
            }
            this.avatarWavesDrawable.setAmplitude(value);
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), NUM));
    }

    public void applyParticipantChanges(boolean animated) {
        applyParticipantChanges(animated, false);
    }

    public void setGrayIconColor(String key, int value) {
        if (!this.grayIconColor.equals(key)) {
            if (this.currentIconGray) {
                this.lastMuteColor = Theme.getColor(key);
            }
            this.grayIconColor = key;
        }
        if (this.currentIconGray) {
            this.muteButton.setColorFilter(new PorterDuffColorFilter(value, PorterDuff.Mode.MULTIPLY));
            Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), NUM & value, true);
        }
    }

    public void setAboutVisibleProgress(int color, float progress) {
        if (TextUtils.isEmpty(this.statusTextView[4].getText())) {
            progress = 0.0f;
        }
        this.statusTextView[4].setFullAlpha(progress);
        this.statusTextView[4].setFullLayoutAdditionalWidth(0, 0);
        invalidate();
    }

    public void setAboutVisible(boolean visible) {
        if (visible) {
            this.statusTextView[4].setTranslationY(0.0f);
        } else {
            this.statusTextView[4].setFullAlpha(0.0f);
        }
        invalidate();
    }

    private void applyParticipantChanges(boolean animated, boolean internal) {
        boolean hasVoice;
        boolean newMuted;
        final int newStatus;
        int newMuteColor;
        int i;
        boolean z;
        boolean changed;
        float f;
        char c;
        char c2;
        int newStatus2;
        boolean z2 = animated;
        if (this.currentCall != null) {
            this.muteButton.setEnabled(!isSelfUser() || this.participant.raise_hand_rating != 0);
            if (SystemClock.elapsedRealtime() - this.participant.lastVoiceUpdateTime < 500) {
                hasVoice = this.participant.hasVoiceDelayed;
            } else {
                hasVoice = this.participant.hasVoice;
            }
            if (!internal) {
                long diff = SystemClock.uptimeMillis() - this.participant.lastSpeakTime;
                boolean newSpeaking = diff < 500;
                if (!this.isSpeaking || !newSpeaking || hasVoice) {
                    this.isSpeaking = newSpeaking;
                    if (this.updateRunnableScheduled) {
                        AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
                        this.updateRunnableScheduled = false;
                    }
                    if (this.isSpeaking) {
                        AndroidUtilities.runOnUIThread(this.updateRunnable, 500 - diff);
                        this.updateRunnableScheduled = true;
                    }
                }
            }
            TLRPC.TL_groupCallParticipant newParticipant = this.currentCall.participants.get(MessageObject.getPeerId(this.participant.peer));
            if (newParticipant != null) {
                this.participant = newParticipant;
            }
            ArrayList<Animator> animators = null;
            boolean newRaisedHand = false;
            boolean myted_by_me = this.participant.muted_by_you && !isSelfUser();
            if (isSelfUser()) {
                newMuted = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute() && (!this.isSpeaking || !hasVoice);
            } else {
                newMuted = (this.participant.muted && (!this.isSpeaking || !hasVoice)) || myted_by_me;
            }
            if (!newMuted || this.participant.can_self_unmute) {
            }
            boolean hasAbout = !TextUtils.isEmpty(this.participant.about);
            this.currentIconGray = false;
            AndroidUtilities.cancelRunOnUIThread(this.checkRaiseRunnable);
            if ((!this.participant.muted || this.isSpeaking) && !myted_by_me) {
                if (this.isSpeaking == 0 || !hasVoice) {
                    newMuteColor = Theme.getColor(this.grayIconColor);
                    newStatus = hasAbout ? 4 : 0;
                    this.currentIconGray = true;
                } else {
                    newMuteColor = Theme.getColor("voipgroup_speakingText");
                    newStatus = 1;
                }
            } else if (!this.participant.can_self_unmute || myted_by_me) {
                boolean z3 = !this.participant.can_self_unmute && this.participant.raise_hand_rating != 0;
                newRaisedHand = z3;
                if (z3) {
                    int newMuteColor2 = Theme.getColor("voipgroup_listeningText");
                    long time = SystemClock.elapsedRealtime() - this.participant.lastRaiseHandDate;
                    if (this.participant.lastRaiseHandDate == 0 || time > 5000) {
                        newStatus2 = myted_by_me ? 2 : hasAbout ? 4 : 0;
                    } else {
                        AndroidUtilities.runOnUIThread(this.checkRaiseRunnable, 5000 - time);
                        newStatus2 = 3;
                    }
                    newMuteColor = newMuteColor2;
                } else {
                    newMuteColor = Theme.getColor("voipgroup_mutedByAdminIcon");
                    newStatus = myted_by_me ? 2 : hasAbout ? 4 : 0;
                }
            } else {
                newMuteColor = Theme.getColor(this.grayIconColor);
                this.currentIconGray = true;
                newStatus = hasAbout ? 4 : 0;
            }
            if (!isSelfUser()) {
                this.statusTextView[4].setTextColor(Theme.getColor(this.grayIconColor));
            }
            if (isSelfUser()) {
                if (!hasAbout && !this.hasAvatar) {
                    if (this.currentUser != null) {
                        c2 = 4;
                        this.statusTextView[4].setText(LocaleController.getString("TapToAddPhotoOrBio", NUM));
                    } else {
                        c2 = 4;
                        this.statusTextView[4].setText(LocaleController.getString("TapToAddPhotoOrDescription", NUM));
                    }
                    this.statusTextView[c2].setTextColor(Theme.getColor(this.grayIconColor));
                } else if (!hasAbout) {
                    if (this.currentUser != null) {
                        c = 4;
                        this.statusTextView[4].setText(LocaleController.getString("TapToAddBio", NUM));
                    } else {
                        c = 4;
                        this.statusTextView[4].setText(LocaleController.getString("TapToAddDescription", NUM));
                    }
                    this.statusTextView[c].setTextColor(Theme.getColor(this.grayIconColor));
                } else if (!this.hasAvatar) {
                    this.statusTextView[4].setText(LocaleController.getString("TapToAddPhoto", NUM));
                    this.statusTextView[4].setTextColor(Theme.getColor(this.grayIconColor));
                } else {
                    this.statusTextView[4].setText(LocaleController.getString("ThisIsYou", NUM));
                    this.statusTextView[4].setTextColor(Theme.getColor("voipgroup_listeningText"));
                }
                if (hasAbout) {
                    this.fullAboutTextView.setText(AndroidUtilities.replaceNewLines(this.participant.about));
                    this.fullAboutTextView.setTextColor(Theme.getColor("voipgroup_mutedIcon"));
                } else {
                    this.fullAboutTextView.setText(this.statusTextView[newStatus].getText());
                    this.fullAboutTextView.setTextColor(this.statusTextView[newStatus].getTextColor());
                }
            } else if (hasAbout) {
                this.statusTextView[4].setText(AndroidUtilities.replaceNewLines(this.participant.about));
                this.fullAboutTextView.setText("");
            } else {
                this.statusTextView[4].setText("");
                this.fullAboutTextView.setText("");
            }
            boolean somethingChanged = false;
            AnimatorSet animatorSet2 = this.animatorSet;
            if (!(animatorSet2 == null || (newStatus == this.currentStatus && this.lastMuteColor == newMuteColor))) {
                somethingChanged = true;
            }
            if ((!z2 || somethingChanged) && animatorSet2 != null) {
                animatorSet2.cancel();
                this.animatorSet = null;
            }
            if (z2 && this.lastMuteColor == newMuteColor && !somethingChanged) {
                i = 1;
            } else if (z2) {
                animators = new ArrayList<>();
                int oldColor = this.lastMuteColor;
                this.lastMuteColor = newMuteColor;
                ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                animator.addUpdateListener(new GroupCallUserCell$$ExternalSyntheticLambda0(this, oldColor, newMuteColor));
                animators.add(animator);
                i = 1;
            } else {
                RLottieImageView rLottieImageView = this.muteButton;
                this.lastMuteColor = newMuteColor;
                rLottieImageView.setColorFilter(new PorterDuffColorFilter(newMuteColor, PorterDuff.Mode.MULTIPLY));
                i = 1;
                Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), NUM & newMuteColor, true);
            }
            if (newStatus == i) {
                int vol = ChatObject.getParticipantVolume(this.participant);
                int volume = vol / 100;
                if (volume != 100) {
                    boolean z4 = hasVoice;
                    this.statusTextView[1].setLeftDrawable(this.speakingDrawable);
                    SimpleTextView simpleTextView = this.statusTextView[1];
                    TLRPC.TL_groupCallParticipant tL_groupCallParticipant = newParticipant;
                    Object[] objArr = new Object[1];
                    objArr[0] = Integer.valueOf(vol < 100 ? 1 : volume);
                    int i2 = newMuteColor;
                    simpleTextView.setText(LocaleController.formatString("SpeakingWithVolume", NUM, objArr));
                } else {
                    TLRPC.TL_groupCallParticipant tL_groupCallParticipant2 = newParticipant;
                    int i3 = newMuteColor;
                    this.statusTextView[1].setLeftDrawable((Drawable) null);
                    this.statusTextView[1].setText(LocaleController.getString("Speaking", NUM));
                }
            } else {
                TLRPC.TL_groupCallParticipant tL_groupCallParticipant3 = newParticipant;
                int i4 = newMuteColor;
            }
            if (isSelfUser()) {
                applyStatus(4);
            } else if (!z2 || newStatus != this.currentStatus || somethingChanged) {
                if (z2) {
                    if (animators == null) {
                        animators = new ArrayList<>();
                    }
                    if (newStatus != 0) {
                        int a = 0;
                        while (true) {
                            SimpleTextView[] simpleTextViewArr = this.statusTextView;
                            if (a >= simpleTextViewArr.length) {
                                break;
                            }
                            SimpleTextView simpleTextView2 = simpleTextViewArr[a];
                            Property property = View.TRANSLATION_Y;
                            float[] fArr = new float[1];
                            if (a == newStatus) {
                                f = 0.0f;
                            } else {
                                f = (float) AndroidUtilities.dp(a == 0 ? 2.0f : -2.0f);
                            }
                            fArr[0] = f;
                            animators.add(ObjectAnimator.ofFloat(simpleTextView2, property, fArr));
                            SimpleTextView simpleTextView3 = this.statusTextView[a];
                            Property property2 = View.ALPHA;
                            float[] fArr2 = new float[1];
                            fArr2[0] = a == newStatus ? 1.0f : 0.0f;
                            animators.add(ObjectAnimator.ofFloat(simpleTextView3, property2, fArr2));
                            a++;
                        }
                    } else {
                        int a2 = 0;
                        while (true) {
                            SimpleTextView[] simpleTextViewArr2 = this.statusTextView;
                            if (a2 >= simpleTextViewArr2.length) {
                                break;
                            }
                            SimpleTextView simpleTextView4 = simpleTextViewArr2[a2];
                            Property property3 = View.TRANSLATION_Y;
                            float[] fArr3 = new float[1];
                            fArr3[0] = a2 == newStatus ? 0.0f : (float) AndroidUtilities.dp(-2.0f);
                            animators.add(ObjectAnimator.ofFloat(simpleTextView4, property3, fArr3));
                            SimpleTextView simpleTextView5 = this.statusTextView[a2];
                            Property property4 = View.ALPHA;
                            float[] fArr4 = new float[1];
                            fArr4[0] = a2 == newStatus ? 1.0f : 0.0f;
                            animators.add(ObjectAnimator.ofFloat(simpleTextView5, property4, fArr4));
                            a2++;
                        }
                    }
                } else {
                    applyStatus(newStatus);
                }
                this.currentStatus = newStatus;
            }
            this.avatarWavesDrawable.setMuted(newStatus, z2);
            if (animators != null) {
                AnimatorSet animatorSet3 = this.animatorSet;
                if (animatorSet3 != null) {
                    animatorSet3.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet4 = new AnimatorSet();
                this.animatorSet = animatorSet4;
                animatorSet4.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (!GroupCallUserCell.this.isSelfUser()) {
                            GroupCallUserCell.this.applyStatus(newStatus);
                        }
                        AnimatorSet unused = GroupCallUserCell.this.animatorSet = null;
                    }
                });
                this.animatorSet.playTogether(animators);
                this.animatorSet.setDuration(180);
                this.animatorSet.start();
            }
            if (z2 && this.lastMuted == newMuted && this.lastRaisedHand == newRaisedHand) {
                z = false;
            } else {
                if (newRaisedHand) {
                    changed = this.muteDrawable.setCustomEndFrame(84);
                    if (z2) {
                        this.muteDrawable.setOnFinishCallback(this.raiseHandCallback, 83);
                    } else {
                        this.muteDrawable.setOnFinishCallback((Runnable) null, 0);
                    }
                } else {
                    this.muteButton.setAnimation(this.muteDrawable);
                    this.muteDrawable.setOnFinishCallback((Runnable) null, 0);
                    if (!newMuted || !this.lastRaisedHand) {
                        changed = this.muteDrawable.setCustomEndFrame(newMuted ? 64 : 42);
                    } else {
                        changed = this.muteDrawable.setCustomEndFrame(21);
                    }
                }
                if (z2) {
                    if (changed) {
                        if (newStatus == 3) {
                            this.muteDrawable.setCurrentFrame(63);
                        } else if (newMuted && this.lastRaisedHand && !newRaisedHand) {
                            this.muteDrawable.setCurrentFrame(0);
                        } else if (newMuted) {
                            this.muteDrawable.setCurrentFrame(43);
                        } else {
                            this.muteDrawable.setCurrentFrame(21);
                        }
                    }
                    this.muteButton.playAnimation();
                    z = false;
                } else {
                    RLottieDrawable rLottieDrawable = this.muteDrawable;
                    z = false;
                    rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
                    this.muteButton.invalidate();
                }
                this.lastMuted = newMuted;
                this.lastRaisedHand = newRaisedHand;
            }
            if (!this.isSpeaking) {
                this.avatarWavesDrawable.setAmplitude(0.0d);
            }
            AvatarWavesDrawable avatarWavesDrawable2 = this.avatarWavesDrawable;
            if (this.isSpeaking && this.progressToAvatarPreview == 0.0f) {
                z = true;
            }
            avatarWavesDrawable2.setShowWaves(z, this);
        }
    }

    /* renamed from: lambda$applyParticipantChanges$6$org-telegram-ui-Cells-GroupCallUserCell  reason: not valid java name */
    public /* synthetic */ void m1533xvar_dd947(int oldColor, int newMuteColor, ValueAnimator animation) {
        int color = AndroidUtilities.getOffsetColor(oldColor, newMuteColor, animation.getAnimatedFraction(), 1.0f);
        this.muteButton.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), NUM & color, true);
    }

    /* access modifiers changed from: private */
    public void applyStatus(int newStatus) {
        float f;
        if (newStatus == 0) {
            int a = 0;
            while (true) {
                SimpleTextView[] simpleTextViewArr = this.statusTextView;
                if (a < simpleTextViewArr.length) {
                    simpleTextViewArr[a].setTranslationY(a == newStatus ? 0.0f : (float) AndroidUtilities.dp(-2.0f));
                    this.statusTextView[a].setAlpha(a == newStatus ? 1.0f : 0.0f);
                    a++;
                } else {
                    return;
                }
            }
        } else {
            int a2 = 0;
            while (true) {
                SimpleTextView[] simpleTextViewArr2 = this.statusTextView;
                if (a2 < simpleTextViewArr2.length) {
                    SimpleTextView simpleTextView = simpleTextViewArr2[a2];
                    if (a2 == newStatus) {
                        f = 0.0f;
                    } else {
                        f = (float) AndroidUtilities.dp(a2 == 0 ? 2.0f : -2.0f);
                    }
                    simpleTextView.setTranslationY(f);
                    this.statusTextView[a2].setAlpha(a2 == newStatus ? 1.0f : 0.0f);
                    a2++;
                } else {
                    return;
                }
            }
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
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
        int cx = this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2);
        int cy = this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2);
        this.avatarWavesDrawable.update();
        if (this.progressToAvatarPreview == 0.0f) {
            this.avatarWavesDrawable.draw(canvas, (float) cx, (float) cy, this);
        }
        this.avatarImageView.setScaleX(this.avatarWavesDrawable.getAvatarScale());
        this.avatarImageView.setScaleY(this.avatarWavesDrawable.getAvatarScale());
        this.avatarProgressView.setScaleX(this.avatarWavesDrawable.getAvatarScale());
        this.avatarProgressView.setScaleY(this.avatarWavesDrawable.getAvatarScale());
        super.dispatchDraw(canvas);
    }

    public void getAvatarPosition(int[] pos) {
        this.avatarImageView.getLocationInWindow(pos);
    }

    public static class AvatarWavesDrawable {
        float amplitude;
        float animateAmplitudeDiff;
        float animateToAmplitude;
        private BlobDrawable blobDrawable = new BlobDrawable(6);
        private BlobDrawable blobDrawable2 = new BlobDrawable(8);
        private boolean hasCustomColor;
        boolean invalidateColor = true;
        private int isMuted;
        private float progressToMuted = 0.0f;
        boolean showWaves;
        float wavesEnter = 0.0f;

        public AvatarWavesDrawable(int minRadius, int maxRadius) {
            this.blobDrawable.minRadius = (float) minRadius;
            this.blobDrawable.maxRadius = (float) maxRadius;
            this.blobDrawable2.minRadius = (float) minRadius;
            this.blobDrawable2.maxRadius = (float) maxRadius;
            this.blobDrawable.generateBlob();
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

        /* JADX WARNING: Removed duplicated region for block: B:23:0x005f  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void draw(android.graphics.Canvas r10, float r11, float r12, android.view.View r13) {
            /*
                r9 = this;
                float r0 = r9.amplitude
                r1 = 1053609165(0x3ecccccd, float:0.4)
                float r0 = r0 * r1
                r1 = 1061997773(0x3f4ccccd, float:0.8)
                float r0 = r0 + r1
                boolean r1 = r9.showWaves
                r2 = 0
                if (r1 != 0) goto L_0x0016
                float r1 = r9.wavesEnter
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x00a7
            L_0x0016:
                r10.save()
                org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                float r3 = r9.wavesEnter
                float r1 = r1.getInterpolation(r3)
                float r3 = r0 * r1
                float r4 = r0 * r1
                r10.scale(r3, r4, r11, r12)
                boolean r3 = r9.hasCustomColor
                r4 = 1065353216(0x3var_, float:1.0)
                if (r3 != 0) goto L_0x0086
                int r3 = r9.isMuted
                r5 = 1037726734(0x3dda740e, float:0.10666667)
                r6 = 1
                if (r3 == r6) goto L_0x0048
                float r7 = r9.progressToMuted
                int r8 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
                if (r8 == 0) goto L_0x0048
                float r7 = r7 + r5
                r9.progressToMuted = r7
                int r3 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
                if (r3 <= 0) goto L_0x0045
                r9.progressToMuted = r4
            L_0x0045:
                r9.invalidateColor = r6
                goto L_0x005b
            L_0x0048:
                if (r3 != r6) goto L_0x005b
                float r3 = r9.progressToMuted
                int r7 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
                if (r7 == 0) goto L_0x005b
                float r3 = r3 - r5
                r9.progressToMuted = r3
                int r3 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
                if (r3 >= 0) goto L_0x0059
                r9.progressToMuted = r2
            L_0x0059:
                r9.invalidateColor = r6
            L_0x005b:
                boolean r3 = r9.invalidateColor
                if (r3 == 0) goto L_0x0086
                java.lang.String r3 = "voipgroup_speakingText"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                int r5 = r9.isMuted
                r6 = 2
                if (r5 != r6) goto L_0x006d
                java.lang.String r5 = "voipgroup_mutedByAdminIcon"
                goto L_0x006f
            L_0x006d:
                java.lang.String r5 = "voipgroup_listeningText"
            L_0x006f:
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                float r6 = r9.progressToMuted
                int r3 = androidx.core.graphics.ColorUtils.blendARGB(r3, r5, r6)
                org.telegram.ui.Components.BlobDrawable r5 = r9.blobDrawable
                android.graphics.Paint r5 = r5.paint
                r6 = 38
                int r6 = androidx.core.graphics.ColorUtils.setAlphaComponent(r3, r6)
                r5.setColor(r6)
            L_0x0086:
                org.telegram.ui.Components.BlobDrawable r3 = r9.blobDrawable
                float r5 = r9.amplitude
                r3.update(r5, r4)
                org.telegram.ui.Components.BlobDrawable r3 = r9.blobDrawable
                android.graphics.Paint r5 = r3.paint
                r3.draw(r11, r12, r10, r5)
                org.telegram.ui.Components.BlobDrawable r3 = r9.blobDrawable2
                float r5 = r9.amplitude
                r3.update(r5, r4)
                org.telegram.ui.Components.BlobDrawable r3 = r9.blobDrawable2
                org.telegram.ui.Components.BlobDrawable r4 = r9.blobDrawable
                android.graphics.Paint r4 = r4.paint
                r3.draw(r11, r12, r10, r4)
                r10.restore()
            L_0x00a7:
                float r1 = r9.wavesEnter
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto L_0x00b0
                r13.invalidate()
            L_0x00b0:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable.draw(android.graphics.Canvas, float, float, android.view.View):void");
        }

        public float getAvatarScale() {
            float wavesEnter2 = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.wavesEnter);
            return (((this.amplitude * 0.2f) + 0.9f) * wavesEnter2) + ((1.0f - wavesEnter2) * 1.0f);
        }

        public void setShowWaves(boolean show, View parentView) {
            if (this.showWaves != show) {
                parentView.invalidate();
            }
            this.showWaves = show;
        }

        public void setAmplitude(double value) {
            float amplitude2 = ((float) value) / 80.0f;
            if (!this.showWaves) {
                amplitude2 = 0.0f;
            }
            if (amplitude2 > 1.0f) {
                amplitude2 = 1.0f;
            } else if (amplitude2 < 0.0f) {
                amplitude2 = 0.0f;
            }
            this.animateToAmplitude = amplitude2;
            this.animateAmplitudeDiff = (amplitude2 - this.amplitude) / 200.0f;
        }

        public void setColor(int color) {
            this.hasCustomColor = true;
            this.blobDrawable.paint.setColor(color);
        }

        public void setMuted(int status, boolean animated) {
            this.isMuted = status;
            if (!animated) {
                this.progressToMuted = status != 1 ? 1.0f : 0.0f;
            }
            this.invalidateColor = true;
        }
    }

    public BackupImageView getAvatarImageView() {
        return this.avatarImageView;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        String str;
        int i;
        super.onInitializeAccessibilityNodeInfo(info);
        if (info.isEnabled() && Build.VERSION.SDK_INT >= 21) {
            if (!this.participant.muted || this.participant.can_self_unmute) {
                i = NUM;
                str = "VoipMute";
            } else {
                i = NUM;
                str = "VoipUnmute";
            }
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString(str, i)));
        }
    }

    public long getPeerId() {
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant = this.participant;
        if (tL_groupCallParticipant == null) {
            return 0;
        }
        return MessageObject.getPeerId(tL_groupCallParticipant.peer);
    }
}
