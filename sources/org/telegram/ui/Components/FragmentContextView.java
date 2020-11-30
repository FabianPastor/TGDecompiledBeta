package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.SharingLocationsAlert;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;

public class FragmentContextView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int account;
    private FragmentContextView additionalContextView;
    /* access modifiers changed from: private */
    public int animationIndex;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private View applyingView;
    /* access modifiers changed from: private */
    public AvatarImageView avatars;
    /* access modifiers changed from: private */
    public Runnable checkLocationRunnable;
    private ImageView closeButton;
    /* access modifiers changed from: private */
    public int currentStyle;
    /* access modifiers changed from: private */
    public FragmentContextViewDelegate delegate;
    private boolean disabledCallCheck;
    boolean drawOverlay;
    private boolean firstLocationsLoaded;
    private BaseFragment fragment;
    private FrameLayout frameLayout;
    private boolean isLocation;
    private boolean isMusic;
    private boolean isMuted;
    private TextView joinButton;
    private int lastLocationSharingCount;
    private MessageObject lastMessageObject;
    private String lastString;
    private RLottieImageView muteButton;
    private RLottieDrawable muteDrawable;
    private ImageView playButton;
    private PlayPauseDrawable playPauseDrawable;
    private ImageView playbackSpeedButton;
    private View selector;
    private View shadow;
    private TextView subtitleTextView;
    private boolean supportsCalls;
    private TextView titleTextView;
    private float topPadding;
    private boolean visible;

    public interface FragmentContextViewDelegate {
        void onAnimation(boolean z, boolean z2);
    }

    private class AvatarImageView extends FrameLayout {
        DrawingState[] animatingStates = new DrawingState[3];
        DrawingState[] currentStates = new DrawingState[3];
        private Paint paint = new Paint(1);
        Random random = new Random();
        float transitionProgress = 1.0f;
        ValueAnimator transitionProgressAnimator;
        boolean updateAfterTransition;
        boolean wasDraw;

        public void commitTransition() {
            boolean z;
            if (!this.wasDraw) {
                this.transitionProgress = 1.0f;
                swapStates();
                return;
            }
            DrawingState[] drawingStateArr = new DrawingState[3];
            System.arraycopy(this.currentStates, 0, drawingStateArr, 0, 3);
            boolean z2 = false;
            for (int i = 0; i < 3; i++) {
                int i2 = 0;
                while (true) {
                    if (i2 >= 3) {
                        z = false;
                        break;
                    } else if (this.currentStates[i2].id == this.animatingStates[i].id) {
                        drawingStateArr[i2] = null;
                        if (i == i2) {
                            int unused = this.animatingStates[i].animationType = -1;
                        } else {
                            int unused2 = this.animatingStates[i].animationType = 2;
                            int unused3 = this.animatingStates[i].moveFromIndex = i2;
                        }
                        z = true;
                    } else {
                        i2++;
                    }
                }
                if (!z) {
                    int unused4 = this.animatingStates[i].animationType = 0;
                    z2 = true;
                }
            }
            for (int i3 = 0; i3 < 3; i3++) {
                if (drawingStateArr[i3] != null) {
                    int unused5 = drawingStateArr[i3].animationType = 1;
                    z2 = true;
                }
            }
            if (z2) {
                ValueAnimator valueAnimator = this.transitionProgressAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.transitionProgress = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.transitionProgressAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        FragmentContextView.AvatarImageView.this.lambda$commitTransition$0$FragmentContextView$AvatarImageView(valueAnimator);
                    }
                });
                this.transitionProgressAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AvatarImageView avatarImageView = AvatarImageView.this;
                        if (avatarImageView.transitionProgressAnimator != null) {
                            avatarImageView.transitionProgressAnimator = null;
                            avatarImageView.transitionProgress = 1.0f;
                            avatarImageView.swapStates();
                            AvatarImageView avatarImageView2 = AvatarImageView.this;
                            if (avatarImageView2.updateAfterTransition) {
                                avatarImageView2.updateAfterTransition = false;
                                FragmentContextView.this.updateAvatars();
                            }
                            AvatarImageView.this.invalidate();
                        }
                    }
                });
                this.transitionProgressAnimator.setDuration(220);
                this.transitionProgressAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.transitionProgressAnimator.start();
                invalidate();
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$commitTransition$0 */
        public /* synthetic */ void lambda$commitTransition$0$FragmentContextView$AvatarImageView(ValueAnimator valueAnimator) {
            this.transitionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        /* access modifiers changed from: private */
        public void swapStates() {
            for (int i = 0; i < 3; i++) {
                DrawingState[] drawingStateArr = this.currentStates;
                DrawingState drawingState = drawingStateArr[i];
                DrawingState[] drawingStateArr2 = this.animatingStates;
                drawingStateArr[i] = drawingStateArr2[i];
                drawingStateArr2[i] = drawingState;
            }
        }

        public void updateAfterTransitionEnd() {
            this.updateAfterTransition = true;
        }

        private class DrawingState {
            /* access modifiers changed from: private */
            public int animationType;
            /* access modifiers changed from: private */
            public AvatarDrawable avatarDrawable;
            /* access modifiers changed from: private */
            public int id;
            /* access modifiers changed from: private */
            public ImageReceiver imageReceiver;
            /* access modifiers changed from: private */
            public long lastSpeakTime;
            /* access modifiers changed from: private */
            public long lastUpdateTime;
            /* access modifiers changed from: private */
            public int moveFromIndex;
            /* access modifiers changed from: private */
            public GroupCallUserCell.AvatarWavesDrawable wavesDrawable;

            private DrawingState(AvatarImageView avatarImageView) {
            }
        }

        public AvatarImageView(Context context) {
            super(context);
            for (int i = 0; i < 3; i++) {
                DrawingState[] drawingStateArr = this.currentStates;
                drawingStateArr[i] = new DrawingState();
                ImageReceiver unused = drawingStateArr[i].imageReceiver = new ImageReceiver(this);
                this.currentStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
                AvatarDrawable unused2 = this.currentStates[i].avatarDrawable = new AvatarDrawable();
                this.currentStates[i].avatarDrawable.setTextSize(AndroidUtilities.dp(7.0f));
                DrawingState[] drawingStateArr2 = this.animatingStates;
                drawingStateArr2[i] = new DrawingState();
                ImageReceiver unused3 = drawingStateArr2[i].imageReceiver = new ImageReceiver(this);
                this.animatingStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(12.0f));
                AvatarDrawable unused4 = this.animatingStates[i].avatarDrawable = new AvatarDrawable();
                this.animatingStates[i].avatarDrawable.setTextSize(AndroidUtilities.dp(7.0f));
            }
            setWillNotDraw(false);
        }

        /* access modifiers changed from: private */
        public void setObject(int i, int i2, TLObject tLObject) {
            TLRPC$User tLRPC$User;
            TLRPC$Chat tLRPC$Chat;
            TLObject tLObject2 = tLObject;
            int unused = this.animatingStates[i].id = 0;
            if (tLObject2 == null) {
                this.animatingStates[i].imageReceiver.setImageBitmap((Drawable) null);
                return;
            }
            long unused2 = this.animatingStates[i].lastSpeakTime = -1;
            if (tLObject2 instanceof TLRPC$TL_groupCallParticipant) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = (TLRPC$TL_groupCallParticipant) tLObject2;
                TLRPC$User user = MessagesController.getInstance(i2).getUser(Integer.valueOf(tLRPC$TL_groupCallParticipant.user_id));
                this.animatingStates[i].avatarDrawable.setInfo(user);
                long unused3 = this.animatingStates[i].lastSpeakTime = (long) tLRPC$TL_groupCallParticipant.active_date;
                int unused4 = this.animatingStates[i].id = user.id;
                tLRPC$Chat = null;
                tLRPC$User = user;
            } else if (tLObject2 instanceof TLRPC$User) {
                TLRPC$User tLRPC$User2 = (TLRPC$User) tLObject2;
                this.animatingStates[i].avatarDrawable.setInfo(tLRPC$User2);
                int unused5 = this.animatingStates[i].id = tLRPC$User2.id;
                tLRPC$User = tLRPC$User2;
                tLRPC$Chat = null;
            } else {
                TLRPC$Chat tLRPC$Chat2 = (TLRPC$Chat) tLObject2;
                this.animatingStates[i].avatarDrawable.setInfo(tLRPC$Chat2);
                int unused6 = this.animatingStates[i].id = tLRPC$Chat2.id;
                tLRPC$Chat = tLRPC$Chat2;
                tLRPC$User = null;
            }
            if (tLRPC$User != null) {
                this.animatingStates[i].imageReceiver.setImage(ImageLocation.getForUser(tLRPC$User, false), "50_50", this.animatingStates[i].avatarDrawable, (String) null, tLRPC$User, 0);
            } else {
                this.animatingStates[i].imageReceiver.setImage(ImageLocation.getForChat(tLRPC$Chat, false), "50_50", this.animatingStates[i].avatarDrawable, (String) null, tLRPC$Chat, 0);
            }
            this.animatingStates[i].imageReceiver.setRoundRadius(AndroidUtilities.dp(FragmentContextView.this.currentStyle == 4 ? 16.0f : 12.0f));
            float dp = (float) AndroidUtilities.dp(FragmentContextView.this.currentStyle == 4 ? 32.0f : 24.0f);
            this.animatingStates[i].imageReceiver.setImageCoords(0.0f, 0.0f, dp, dp);
            invalidate();
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:106:0x028d A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x0116  */
        /* JADX WARNING: Removed duplicated region for block: B:95:0x0270  */
        /* JADX WARNING: Removed duplicated region for block: B:96:0x0285  */
        /* JADX WARNING: Removed duplicated region for block: B:98:0x028a  */
        @android.annotation.SuppressLint({"DrawAllocation"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r19) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = 1
                r0.wasDraw = r2
                org.telegram.ui.Components.FragmentContextView r3 = org.telegram.ui.Components.FragmentContextView.this
                int r3 = r3.currentStyle
                r4 = 4
                if (r3 != r4) goto L_0x0013
                r3 = 1103101952(0x41CLASSNAME, float:24.0)
                goto L_0x0015
            L_0x0013:
                r3 = 1101004800(0x41a00000, float:20.0)
            L_0x0015:
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r5 = 1092616192(0x41200000, float:10.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                org.telegram.messenger.voip.VoIPService r6 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                if (r6 == 0) goto L_0x0031
                org.telegram.messenger.voip.VoIPService r6 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
                boolean r6 = r6.isMicMute()
                if (r6 == 0) goto L_0x0031
                r6 = 1
                goto L_0x0032
            L_0x0031:
                r6 = 0
            L_0x0032:
                org.telegram.ui.Components.FragmentContextView r8 = org.telegram.ui.Components.FragmentContextView.this
                int r8 = r8.currentStyle
                r9 = 3
                if (r8 != r4) goto L_0x0047
                android.graphics.Paint r6 = r0.paint
                java.lang.String r8 = "inappPlayerBackground"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r6.setColor(r8)
                goto L_0x005f
            L_0x0047:
                org.telegram.ui.Components.FragmentContextView r8 = org.telegram.ui.Components.FragmentContextView.this
                int r8 = r8.currentStyle
                if (r8 == r9) goto L_0x005f
                android.graphics.Paint r8 = r0.paint
                if (r6 == 0) goto L_0x0056
                java.lang.String r6 = "returnToCallMutedBackground"
                goto L_0x0058
            L_0x0056:
                java.lang.String r6 = "returnToCallBackground"
            L_0x0058:
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r8.setColor(r6)
            L_0x005f:
                r6 = 2
                r8 = 2
            L_0x0061:
                if (r8 < 0) goto L_0x029d
                r10 = 0
            L_0x0064:
                if (r10 >= r6) goto L_0x0294
                r11 = 1065353216(0x3var_, float:1.0)
                if (r10 != 0) goto L_0x0074
                float r12 = r0.transitionProgress
                int r12 = (r12 > r11 ? 1 : (r12 == r11 ? 0 : -1))
                if (r12 != 0) goto L_0x0074
            L_0x0070:
                r9 = 0
                r15 = 4
                goto L_0x028d
            L_0x0074:
                if (r10 != 0) goto L_0x0079
                org.telegram.ui.Components.FragmentContextView$AvatarImageView$DrawingState[] r12 = r0.animatingStates
                goto L_0x007b
            L_0x0079:
                org.telegram.ui.Components.FragmentContextView$AvatarImageView$DrawingState[] r12 = r0.currentStates
            L_0x007b:
                if (r10 != r2) goto L_0x008c
                float r13 = r0.transitionProgress
                int r13 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
                if (r13 == 0) goto L_0x008c
                r13 = r12[r8]
                int r13 = r13.animationType
                if (r13 == r2) goto L_0x008c
                goto L_0x0070
            L_0x008c:
                r13 = r12[r8]
                org.telegram.messenger.ImageReceiver r13 = r13.imageReceiver
                boolean r14 = r13.hasImageSet()
                if (r14 != 0) goto L_0x0099
                goto L_0x0070
            L_0x0099:
                int r14 = r3 * r8
                int r14 = r14 + r5
                r13.setImageX(r14)
                org.telegram.ui.Components.FragmentContextView r15 = org.telegram.ui.Components.FragmentContextView.this
                int r15 = r15.currentStyle
                if (r15 != r4) goto L_0x00aa
                r15 = 1090519040(0x41000000, float:8.0)
                goto L_0x00ac
            L_0x00aa:
                r15 = 1086324736(0x40CLASSNAME, float:6.0)
            L_0x00ac:
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
                float r15 = (float) r15
                r13.setImageY(r15)
                float r15 = r0.transitionProgress
                int r15 = (r15 > r11 ? 1 : (r15 == r11 ? 0 : -1))
                if (r15 == 0) goto L_0x0111
                r15 = r12[r8]
                int r15 = r15.animationType
                if (r15 != r2) goto L_0x00d8
                r19.save()
                float r14 = r0.transitionProgress
                float r15 = r11 - r14
                float r14 = r11 - r14
                float r7 = r13.getCenterX()
                float r4 = r13.getCenterY()
                r1.scale(r15, r14, r7, r4)
            L_0x00d6:
                r4 = 1
                goto L_0x0112
            L_0x00d8:
                r4 = r12[r8]
                int r4 = r4.animationType
                if (r4 != 0) goto L_0x00f1
                r19.save()
                float r4 = r0.transitionProgress
                float r7 = r13.getCenterX()
                float r14 = r13.getCenterY()
                r1.scale(r4, r4, r7, r14)
                goto L_0x00d6
            L_0x00f1:
                r4 = r12[r8]
                int r4 = r4.animationType
                if (r4 != r6) goto L_0x0111
                r4 = r12[r8]
                int r4 = r4.moveFromIndex
                int r4 = r4 * r3
                int r4 = r4 + r5
                float r7 = (float) r14
                float r14 = r0.transitionProgress
                float r7 = r7 * r14
                float r4 = (float) r4
                float r14 = r11 - r14
                float r4 = r4 * r14
                float r7 = r7 + r4
                int r4 = (int) r7
                r13.setImageX(r4)
            L_0x0111:
                r4 = 0
            L_0x0112:
                int r7 = r12.length
                int r7 = r7 - r2
                if (r8 == r7) goto L_0x0268
                org.telegram.ui.Components.FragmentContextView r7 = org.telegram.ui.Components.FragmentContextView.this
                int r7 = r7.currentStyle
                r14 = 1095761920(0x41500000, float:13.0)
                if (r7 != r9) goto L_0x0179
                org.telegram.ui.Components.FragmentContextViewWavesDrawable r7 = org.telegram.ui.ActionBar.Theme.getFragmentContextViewWavesDrawable()
                r12 = 0
            L_0x0125:
                if (r12 >= r6) goto L_0x0268
                if (r12 != 0) goto L_0x012e
                org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r15 = r7.previousState
                if (r15 != 0) goto L_0x012e
                goto L_0x0172
            L_0x012e:
                if (r12 != r2) goto L_0x0135
                org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r15 = r7.currentState
                if (r15 != 0) goto L_0x0135
                goto L_0x0172
            L_0x0135:
                if (r12 != 0) goto L_0x0148
                android.graphics.Paint r15 = r7.paint
                org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r6 = r7.previousState
                android.graphics.Shader r6 = r6.shader
                r15.setShader(r6)
                android.graphics.Paint r6 = r7.paint
                r15 = 255(0xff, float:3.57E-43)
                r6.setAlpha(r15)
                goto L_0x015d
            L_0x0148:
                android.graphics.Paint r6 = r7.paint
                org.telegram.ui.Components.FragmentContextViewWavesDrawable$WeavingState r15 = r7.currentState
                android.graphics.Shader r15 = r15.shader
                r6.setShader(r15)
                android.graphics.Paint r6 = r7.paint
                r15 = 1132396544(0x437var_, float:255.0)
                float r9 = r7.progressToState
                float r9 = r9 * r15
                int r9 = (int) r9
                r6.setAlpha(r9)
            L_0x015d:
                float r6 = r13.getCenterX()
                float r9 = r13.getCenterY()
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
                float r15 = (float) r15
                android.graphics.Paint r14 = r7.paint
                r1.drawCircle(r6, r9, r15, r14)
                r18.invalidate()
            L_0x0172:
                int r12 = r12 + 1
                r6 = 2
                r9 = 3
                r14 = 1095761920(0x41500000, float:13.0)
                goto L_0x0125
            L_0x0179:
                org.telegram.ui.Components.FragmentContextView r6 = org.telegram.ui.Components.FragmentContextView.this
                int r6 = r6.currentStyle
                r7 = 1099431936(0x41880000, float:17.0)
                r9 = 4
                if (r6 != r9) goto L_0x0246
                float r6 = r13.getCenterX()
                float r9 = r13.getCenterY()
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r14 = (float) r14
                android.graphics.Paint r15 = r0.paint
                r1.drawCircle(r6, r9, r14, r15)
                r6 = r12[r8]
                org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r6 = r6.wavesDrawable
                if (r6 != 0) goto L_0x01c7
                r6 = r12[r8]
                org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r9 = new org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r14 = 1101529088(0x41a80000, float:21.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
                r9.<init>(r7, r14)
                org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable unused = r6.wavesDrawable = r9
                r6 = r12[r8]
                org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r6 = r6.wavesDrawable
                java.lang.String r7 = "voipgroup_listeningText"
                int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
                r9 = 76
                int r7 = androidx.core.graphics.ColorUtils.setAlphaComponent(r7, r9)
                r6.setColor(r7)
            L_0x01c7:
                long r6 = java.lang.System.currentTimeMillis()
                r9 = r12[r8]
                long r14 = r9.lastUpdateTime
                long r14 = r6 - r14
                r16 = 100
                int r9 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
                if (r9 <= 0) goto L_0x0228
                r9 = r12[r8]
                long unused = r9.lastUpdateTime = r6
                int r6 = org.telegram.messenger.UserConfig.selectedAccount
                org.telegram.tgnet.ConnectionsManager r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r6)
                int r6 = r6.getCurrentTime()
                long r6 = (long) r6
                r9 = r12[r8]
                long r14 = r9.lastSpeakTime
                long r6 = r6 - r14
                r14 = 5
                int r9 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
                if (r9 >= 0) goto L_0x0212
                r6 = r12[r8]
                org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r6 = r6.wavesDrawable
                r6.setShowWaves(r2)
                r6 = r12[r8]
                org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r6 = r6.wavesDrawable
                java.util.Random r7 = r0.random
                int r7 = r7.nextInt()
                int r7 = r7 % 100
                double r14 = (double) r7
                r6.setAmplitude(r14, r0)
                goto L_0x0228
            L_0x0212:
                r6 = r12[r8]
                org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r6 = r6.wavesDrawable
                r9 = 0
                r6.setShowWaves(r9)
                r6 = r12[r8]
                org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r6 = r6.wavesDrawable
                r14 = 0
                r6.setAmplitude(r14, r0)
                goto L_0x0229
            L_0x0228:
                r9 = 0
            L_0x0229:
                r6 = r12[r8]
                org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r6 = r6.wavesDrawable
                float r7 = r13.getCenterX()
                float r14 = r13.getCenterY()
                r6.draw(r1, r7, r14, r0)
                r6 = r12[r8]
                org.telegram.ui.Cells.GroupCallUserCell$AvatarWavesDrawable r6 = r6.wavesDrawable
                float r6 = r6.getAvatarScale()
                r15 = 4
                goto L_0x026c
            L_0x0246:
                r9 = 0
                float r6 = r13.getCenterX()
                float r12 = r13.getCenterY()
                org.telegram.ui.Components.FragmentContextView r14 = org.telegram.ui.Components.FragmentContextView.this
                int r14 = r14.currentStyle
                r15 = 4
                if (r14 != r15) goto L_0x025b
                r14 = 1099431936(0x41880000, float:17.0)
                goto L_0x025d
            L_0x025b:
                r14 = 1095761920(0x41500000, float:13.0)
            L_0x025d:
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r14)
                float r7 = (float) r7
                android.graphics.Paint r14 = r0.paint
                r1.drawCircle(r6, r12, r7, r14)
                goto L_0x026a
            L_0x0268:
                r9 = 0
                r15 = 4
            L_0x026a:
                r6 = 1065353216(0x3var_, float:1.0)
            L_0x026c:
                int r7 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
                if (r7 == 0) goto L_0x0285
                r19.save()
                float r7 = r13.getCenterX()
                float r11 = r13.getCenterY()
                r1.scale(r6, r6, r7, r11)
                r13.draw(r1)
                r19.restore()
                goto L_0x0288
            L_0x0285:
                r13.draw(r1)
            L_0x0288:
                if (r4 == 0) goto L_0x028d
                r19.restore()
            L_0x028d:
                int r10 = r10 + 1
                r4 = 4
                r6 = 2
                r9 = 3
                goto L_0x0064
            L_0x0294:
                r9 = 0
                r15 = 4
                int r8 = r8 + -1
                r4 = 4
                r6 = 2
                r9 = 3
                goto L_0x0061
            L_0x029d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.AvatarImageView.onDraw(android.graphics.Canvas):void");
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.wasDraw = false;
            for (int i = 0; i < 3; i++) {
                this.currentStates[i].imageReceiver.onDetachedFromWindow();
                this.animatingStates[i].imageReceiver.onDetachedFromWindow();
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            for (int i = 0; i < 3; i++) {
                this.currentStates[i].imageReceiver.onAttachedToWindow();
                this.animatingStates[i].imageReceiver.onAttachedToWindow();
            }
        }
    }

    public FragmentContextView(Context context, BaseFragment baseFragment, boolean z) {
        this(context, baseFragment, (View) null, z);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public FragmentContextView(Context context, BaseFragment baseFragment, View view, boolean z) {
        super(context);
        Context context2 = context;
        View view2 = view;
        boolean z2 = z;
        this.currentStyle = -1;
        this.supportsCalls = true;
        this.account = UserConfig.selectedAccount;
        this.lastLocationSharingCount = -1;
        this.checkLocationRunnable = new Runnable() {
            public void run() {
                FragmentContextView.this.checkLocationString();
                AndroidUtilities.runOnUIThread(FragmentContextView.this.checkLocationRunnable, 1000);
            }
        };
        this.animationIndex = -1;
        this.fragment = baseFragment;
        this.applyingView = view2;
        this.visible = true;
        this.isLocation = z2;
        if (view2 == null) {
            ((ViewGroup) baseFragment.getFragmentView()).setClipToPadding(false);
        }
        setTag(1);
        AnonymousClass2 r2 = new FrameLayout(context2) {
            public void setBackgroundColor(int i) {
                clearAnimation();
                super.setBackgroundColor(i);
            }

            public void invalidate() {
                super.invalidate();
                if (FragmentContextView.this.avatars != null && FragmentContextView.this.avatars.getVisibility() == 0) {
                    FragmentContextView.this.avatars.invalidate();
                }
            }
        };
        this.frameLayout = r2;
        r2.setWillNotDraw(false);
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view3 = new View(context2);
        this.selector = view3;
        this.frameLayout.addView(view3, LayoutHelper.createFrame(-1, -1.0f));
        View view4 = new View(context2);
        this.shadow = view4;
        view4.setBackgroundResource(NUM);
        addView(this.shadow, LayoutHelper.createFrame(-1, 2.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context2);
        this.playButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.playButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerPlayPause"), PorterDuff.Mode.MULTIPLY));
        ImageView imageView2 = this.playButton;
        PlayPauseDrawable playPauseDrawable2 = new PlayPauseDrawable(14);
        this.playPauseDrawable = playPauseDrawable2;
        imageView2.setImageDrawable(playPauseDrawable2);
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            this.playButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("inappPlayerPlayPause") & NUM, 1, AndroidUtilities.dp(14.0f)));
        }
        addView(this.playButton, LayoutHelper.createFrame(36, 36, 51));
        this.playButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$0$FragmentContextView(view);
            }
        });
        TextView textView = new TextView(context2);
        this.titleTextView = textView;
        textView.setMaxLines(1);
        this.titleTextView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.titleTextView.setTextSize(1, 15.0f);
        this.titleTextView.setGravity(19);
        addView(this.titleTextView, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        this.subtitleTextView = textView2;
        textView2.setMaxLines(1);
        this.subtitleTextView.setLines(1);
        this.subtitleTextView.setSingleLine(true);
        this.subtitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.subtitleTextView.setTextSize(1, 13.0f);
        this.subtitleTextView.setTextColor(Theme.getColor("inappPlayerClose"));
        addView(this.subtitleTextView, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 10.0f, 36.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.joinButton = textView3;
        textView3.setText(LocaleController.getString("VoipChatJoin", NUM));
        this.joinButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.joinButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.joinButton.setTextSize(1, 14.0f);
        this.joinButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.joinButton.setGravity(17);
        this.joinButton.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
        addView(this.joinButton, LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 10.0f, 14.0f, 0.0f));
        this.joinButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$1$FragmentContextView(view);
            }
        });
        if (!z2) {
            ImageView imageView3 = new ImageView(context2);
            this.playbackSpeedButton = imageView3;
            imageView3.setScaleType(ImageView.ScaleType.CENTER);
            this.playbackSpeedButton.setImageResource(NUM);
            this.playbackSpeedButton.setContentDescription(LocaleController.getString("AccDescrPlayerSpeed", NUM));
            if (AndroidUtilities.density >= 3.0f) {
                this.playbackSpeedButton.setPadding(0, 1, 0, 0);
            }
            addView(this.playbackSpeedButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 36.0f, 0.0f));
            this.playbackSpeedButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    FragmentContextView.this.lambda$new$2$FragmentContextView(view);
                }
            });
            updatePlaybackButton();
        }
        AvatarImageView avatarImageView = new AvatarImageView(context2);
        this.avatars = avatarImageView;
        avatarImageView.setVisibility(8);
        addView(this.avatars, LayoutHelper.createFrame(108, 36, 51));
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(16.0f), AndroidUtilities.dp(20.0f), true, (int[]) null);
        this.muteDrawable = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.muteButton = rLottieImageView;
        rLottieImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("returnToCallText"), PorterDuff.Mode.MULTIPLY));
        if (i >= 21) {
            this.muteButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("inappPlayerClose") & NUM, 1, AndroidUtilities.dp(14.0f)));
        }
        this.muteButton.setAnimation(this.muteDrawable);
        this.muteButton.setScaleType(ImageView.ScaleType.CENTER);
        this.muteButton.setVisibility(8);
        addView(this.muteButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
        this.muteButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$3$FragmentContextView(view);
            }
        });
        ImageView imageView4 = new ImageView(context2);
        this.closeButton = imageView4;
        imageView4.setImageResource(NUM);
        this.closeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerClose"), PorterDuff.Mode.MULTIPLY));
        if (i >= 21) {
            this.closeButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("inappPlayerClose") & NUM, 1, AndroidUtilities.dp(14.0f)));
        }
        this.closeButton.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.closeButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$5$FragmentContextView(view);
            }
        });
        setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                FragmentContextView.this.lambda$new$7$FragmentContextView(view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$FragmentContextView(View view) {
        if (this.currentStyle != 0) {
            return;
        }
        if (MediaController.getInstance().isMessagePaused()) {
            MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        } else {
            MediaController.getInstance().lambda$startAudioAgain$7(MediaController.getInstance().getPlayingMessageObject());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$FragmentContextView(View view) {
        callOnClick();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$FragmentContextView(View view) {
        if (MediaController.getInstance().getPlaybackSpeed(this.isMusic) > 1.0f) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.0f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.8f);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$FragmentContextView(View view) {
        if (VoIPService.getSharedInstance() != null) {
            this.isMuted = !VoIPService.getSharedInstance().isMicMute();
            VoIPService.getSharedInstance().setMicMute(this.isMuted);
            GroupCallActivity.editCallMember(AccountInstance.getInstance(this.account), VoIPService.getSharedInstance().groupCall, UserConfig.getInstance(this.account).getCurrentUser(), this.isMuted);
            this.muteDrawable.setCustomEndFrame(this.isMuted ? 0 : 15);
            this.muteButton.playAnimation();
            Theme.getFragmentContextViewWavesDrawable().setState(this.isMuted ? 1 : 0);
            this.muteButton.performHapticFeedback(3, 2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$5 */
    public /* synthetic */ void lambda$new$5$FragmentContextView(View view) {
        if (this.currentStyle == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("StopLiveLocationAlertToTitle", NUM));
            BaseFragment baseFragment = this.fragment;
            if (baseFragment instanceof DialogsActivity) {
                builder.setMessage(LocaleController.getString("StopLiveLocationAlertAllText", NUM));
            } else {
                ChatActivity chatActivity = (ChatActivity) baseFragment;
                TLRPC$Chat currentChat = chatActivity.getCurrentChat();
                TLRPC$User currentUser = chatActivity.getCurrentUser();
                if (currentChat != null) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("StopLiveLocationAlertToGroupText", NUM, currentChat.title)));
                } else if (currentUser != null) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("StopLiveLocationAlertToUserText", NUM, UserObject.getFirstName(currentUser))));
                } else {
                    builder.setMessage(LocaleController.getString("AreYouSure", NUM));
                }
            }
            builder.setPositiveButton(LocaleController.getString("Stop", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FragmentContextView.this.lambda$null$4$FragmentContextView(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            builder.show();
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
                return;
            }
            return;
        }
        MediaController.getInstance().cleanupPlayer(true, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$4 */
    public /* synthetic */ void lambda$null$4$FragmentContextView(DialogInterface dialogInterface, int i) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof DialogsActivity) {
            for (int i2 = 0; i2 < 3; i2++) {
                LocationController.getInstance(i2).removeAllLocationSharings();
            }
            return;
        }
        LocationController.getInstance(baseFragment.getCurrentAccount()).removeSharingLocation(((ChatActivity) this.fragment).getDialogId());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$7 */
    public /* synthetic */ void lambda$new$7$FragmentContextView(View view) {
        ChatActivity chatActivity;
        ChatObject.Call groupCall;
        long j;
        int i = this.currentStyle;
        long j2 = 0;
        if (i == 0) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (this.fragment != null && playingMessageObject != null) {
                if (playingMessageObject.isMusic()) {
                    this.fragment.showDialog(new AudioPlayerAlert(getContext()));
                    return;
                }
                BaseFragment baseFragment = this.fragment;
                if (baseFragment instanceof ChatActivity) {
                    j2 = ((ChatActivity) baseFragment).getDialogId();
                }
                if (playingMessageObject.getDialogId() == j2) {
                    ((ChatActivity) this.fragment).scrollToMessageId(playingMessageObject.getId(), 0, false, 0, true, 0);
                    return;
                }
                long dialogId = playingMessageObject.getDialogId();
                Bundle bundle = new Bundle();
                int i2 = (int) dialogId;
                int i3 = (int) (dialogId >> 32);
                if (i2 == 0) {
                    bundle.putInt("enc_id", i3);
                } else if (i2 > 0) {
                    bundle.putInt("user_id", i2);
                } else {
                    bundle.putInt("chat_id", -i2);
                }
                bundle.putInt("message_id", playingMessageObject.getId());
                this.fragment.presentFragment(new ChatActivity(bundle), this.fragment instanceof ChatActivity);
            }
        } else if (i == 1) {
            getContext().startActivity(new Intent(getContext(), LaunchActivity.class).setAction("voip"));
        } else if (i == 2) {
            int i4 = UserConfig.selectedAccount;
            BaseFragment baseFragment2 = this.fragment;
            if (baseFragment2 instanceof ChatActivity) {
                j = ((ChatActivity) baseFragment2).getDialogId();
                i4 = this.fragment.getCurrentAccount();
            } else {
                if (LocationController.getLocationsCount() == 1) {
                    int i5 = 0;
                    while (true) {
                        if (i5 >= 3) {
                            break;
                        } else if (!LocationController.getInstance(i5).sharingLocationsUI.isEmpty()) {
                            LocationController.SharingLocationInfo sharingLocationInfo = LocationController.getInstance(i5).sharingLocationsUI.get(0);
                            j = sharingLocationInfo.did;
                            i4 = sharingLocationInfo.messageObject.currentAccount;
                            break;
                        } else {
                            i5++;
                        }
                    }
                }
                j = 0;
            }
            if (j != 0) {
                openSharingLocation(LocationController.getInstance(i4).getSharingLocationInfo(j));
            } else {
                this.fragment.showDialog(new SharingLocationsAlert(getContext(), new SharingLocationsAlert.SharingLocationsAlertDelegate() {
                    public final void didSelectLocation(LocationController.SharingLocationInfo sharingLocationInfo) {
                        FragmentContextView.this.openSharingLocation(sharingLocationInfo);
                    }
                }));
            }
        } else if (i == 3) {
            Intent action = new Intent(getContext(), LaunchActivity.class).setAction("voip_chat");
            action.putExtra("currentAccount", VoIPService.getSharedInstance().getAccount());
            getContext().startActivity(action);
        } else if (i == 4 && this.fragment.getParentActivity() != null && (groupCall = chatActivity.getGroupCall()) != null) {
            this.disabledCallCheck = true;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    FragmentContextView.this.lambda$null$6$FragmentContextView();
                }
            }, 1000);
            VoIPHelper.startCall((chatActivity = (ChatActivity) this.fragment).getMessagesController().getChat(Integer.valueOf(groupCall.chatId)), 0, this.fragment.getParentActivity());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$6 */
    public /* synthetic */ void lambda$null$6$FragmentContextView() {
        this.disabledCallCheck = false;
        checkCall(false);
    }

    public void setSupportsCalls(boolean z) {
        this.supportsCalls = z;
    }

    public void setDelegate(FragmentContextViewDelegate fragmentContextViewDelegate) {
        this.delegate = fragmentContextViewDelegate;
    }

    private void updatePlaybackButton() {
        if (this.playbackSpeedButton != null) {
            String str = MediaController.getInstance().getPlaybackSpeed(this.isMusic) > 1.0f ? "inappPlayerPlayPause" : "inappPlayerClose";
            this.playbackSpeedButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), PorterDuff.Mode.MULTIPLY));
            if (Build.VERSION.SDK_INT >= 21) {
                this.playbackSpeedButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(str) & NUM, 1, AndroidUtilities.dp(14.0f)));
            }
        }
    }

    public void setAdditionalContextView(FragmentContextView fragmentContextView) {
        this.additionalContextView = fragmentContextView;
    }

    /* access modifiers changed from: private */
    public void openSharingLocation(LocationController.SharingLocationInfo sharingLocationInfo) {
        if (sharingLocationInfo != null && this.fragment.getParentActivity() != null) {
            LaunchActivity launchActivity = (LaunchActivity) this.fragment.getParentActivity();
            launchActivity.switchToAccount(sharingLocationInfo.messageObject.currentAccount, true);
            LocationActivity locationActivity = new LocationActivity(2);
            locationActivity.setMessageObject(sharingLocationInfo.messageObject);
            locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate(sharingLocationInfo.messageObject.getDialogId()) {
                public final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
                    SendMessagesHelper.getInstance(LocationController.SharingLocationInfo.this.messageObject.currentAccount).sendMessage(tLRPC$MessageMedia, this.f$1, (MessageObject) null, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
                }
            });
            launchActivity.lambda$runLinkRequest$39(locationActivity);
        }
    }

    @Keep
    public float getTopPadding() {
        return this.topPadding;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0047, code lost:
        if (((org.telegram.ui.ChatActivity) r0).getGroupCall() != null) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0058, code lost:
        if (r0.getId() != 0) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0010, code lost:
        if (org.telegram.messenger.LocationController.getLocationsCount() != 0) goto L_0x005c;
     */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x005f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkVisibility() {
        /*
            r4 = this;
            boolean r0 = r4.isLocation
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0028
            org.telegram.ui.ActionBar.BaseFragment r0 = r4.fragment
            boolean r3 = r0 instanceof org.telegram.ui.DialogsActivity
            if (r3 == 0) goto L_0x0013
            int r0 = org.telegram.messenger.LocationController.getLocationsCount()
            if (r0 == 0) goto L_0x005b
            goto L_0x005c
        L_0x0013:
            int r0 = r0.getCurrentAccount()
            org.telegram.messenger.LocationController r0 = org.telegram.messenger.LocationController.getInstance(r0)
            org.telegram.ui.ActionBar.BaseFragment r2 = r4.fragment
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            long r2 = r2.getDialogId()
            boolean r2 = r0.isSharingLocation(r2)
            goto L_0x005c
        L_0x0028:
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r0 == 0) goto L_0x003b
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r0 = r0.getCallState()
            r3 = 15
            if (r0 == r3) goto L_0x003b
            goto L_0x005c
        L_0x003b:
            org.telegram.ui.ActionBar.BaseFragment r0 = r4.fragment
            boolean r3 = r0 instanceof org.telegram.ui.ChatActivity
            if (r3 == 0) goto L_0x004a
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            org.telegram.messenger.ChatObject$Call r0 = r0.getGroupCall()
            if (r0 == 0) goto L_0x004a
            goto L_0x005c
        L_0x004a:
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r0 = r0.getPlayingMessageObject()
            if (r0 == 0) goto L_0x005b
            int r0 = r0.getId()
            if (r0 == 0) goto L_0x005b
            goto L_0x005c
        L_0x005b:
            r2 = 0
        L_0x005c:
            if (r2 == 0) goto L_0x005f
            goto L_0x0061
        L_0x005f:
            r1 = 8
        L_0x0061:
            r4.setVisibility(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.checkVisibility():void");
    }

    @Keep
    public void setTopPadding(float f) {
        FragmentContextView fragmentContextView;
        this.topPadding = f;
        if (this.fragment != null && getParent() != null) {
            View view = this.applyingView;
            if (view == null) {
                view = this.fragment.getFragmentView();
            }
            FragmentContextView fragmentContextView2 = this.additionalContextView;
            int dp = (fragmentContextView2 == null || fragmentContextView2.getVisibility() != 0 || this.additionalContextView.getParent() == null) ? 0 : AndroidUtilities.dp((float) getStyleHeight());
            if (!(view == null || getParent() == null)) {
                view.setPadding(0, ((int) this.topPadding) + dp, 0, 0);
            }
            if (this.isLocation && (fragmentContextView = this.additionalContextView) != null) {
                ((FrameLayout.LayoutParams) fragmentContextView.getLayoutParams()).topMargin = (-AndroidUtilities.dp((float) getStyleHeight())) - ((int) this.topPadding);
            }
        }
    }

    private void updateStyle(int i) {
        int i2 = this.currentStyle;
        if (i2 != i) {
            if (i2 == 3) {
                Theme.getFragmentContextViewWavesDrawable().removeParent(this);
            }
            this.currentStyle = i;
            AvatarImageView avatarImageView = this.avatars;
            if (avatarImageView != null) {
                avatarImageView.setLayoutParams(LayoutHelper.createFrame(108, getStyleHeight(), 51));
            }
            this.frameLayout.setLayoutParams(LayoutHelper.createFrame(-1, (float) getStyleHeight(), 51, 0.0f, 0.0f, 0.0f, 0.0f));
            this.shadow.setLayoutParams(LayoutHelper.createFrame(-1, 2.0f, 51, 0.0f, (float) getStyleHeight(), 0.0f, 0.0f));
            if (i == 0 || i == 2) {
                this.selector.setBackground(Theme.getSelectorDrawable(false));
                this.frameLayout.setBackgroundColor(Theme.getColor("inappPlayerBackground"));
                this.frameLayout.setTag("inappPlayerBackground");
                this.titleTextView.setGravity(19);
                this.titleTextView.setTextColor(Theme.getColor("inappPlayerTitle"));
                this.titleTextView.setTag("inappPlayerTitle");
                this.subtitleTextView.setVisibility(8);
                this.joinButton.setVisibility(8);
                this.closeButton.setVisibility(0);
                this.playButton.setVisibility(0);
                this.muteButton.setVisibility(8);
                this.avatars.setVisibility(8);
                this.titleTextView.setTypeface(Typeface.DEFAULT);
                this.titleTextView.setTextSize(1, 15.0f);
                if (i == 0) {
                    this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                    this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
                    ImageView imageView = this.playbackSpeedButton;
                    if (imageView != null) {
                        imageView.setVisibility(0);
                    }
                    this.closeButton.setContentDescription(LocaleController.getString("AccDescrClosePlayer", NUM));
                    return;
                }
                this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 8.0f, 0.0f, 0.0f, 0.0f));
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 51.0f, 0.0f, 36.0f, 0.0f));
                this.closeButton.setContentDescription(LocaleController.getString("AccDescrStopLiveLocation", NUM));
            } else if (i == 4) {
                this.selector.setBackground(Theme.getSelectorDrawable(false));
                this.frameLayout.setBackgroundColor(Theme.getColor("inappPlayerBackground"));
                this.frameLayout.setTag("inappPlayerBackground");
                this.muteButton.setVisibility(8);
                this.subtitleTextView.setVisibility(0);
                this.joinButton.setVisibility(0);
                this.titleTextView.setTextColor(Theme.getColor("inappPlayerTitle"));
                this.titleTextView.setTag("inappPlayerTitle");
                this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.titleTextView.setTextSize(1, 15.0f);
                this.titleTextView.setPadding(0, 0, 0, 0);
                this.titleTextView.setText(LocaleController.getString("VoipGroupVoiceChat", NUM));
                this.titleTextView.setGravity(51);
                this.avatars.setVisibility(0);
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                ImageView imageView2 = this.playbackSpeedButton;
                if (imageView2 != null) {
                    imageView2.setVisibility(8);
                }
            } else if (i == 1 || i == 3) {
                this.selector.setBackground((Drawable) null);
                this.frameLayout.setTag("returnToCallBackground");
                if (i == 3) {
                    this.titleTextView.setText(LocaleController.getString("VoipGroupViewVoiceChat", NUM));
                    this.muteButton.setVisibility(0);
                    this.avatars.setVisibility(0);
                    boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
                    this.isMuted = z;
                    this.muteDrawable.setCustomEndFrame(z ? 0 : 14);
                    RLottieDrawable rLottieDrawable = this.muteDrawable;
                    rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame(), false, true);
                    this.muteButton.invalidate();
                    this.frameLayout.setBackground((Drawable) null);
                    Theme.getFragmentContextViewWavesDrawable().addParent(this);
                    invalidate();
                } else {
                    this.titleTextView.setText(LocaleController.getString("ReturnToCall", NUM));
                    this.muteButton.setVisibility(8);
                    this.avatars.setVisibility(8);
                    this.frameLayout.setBackgroundColor(Theme.getColor("returnToCallBackground"));
                }
                this.titleTextView.setGravity(19);
                this.titleTextView.setTextColor(Theme.getColor("returnToCallText"));
                this.titleTextView.setTag("returnToCallText");
                this.closeButton.setVisibility(8);
                this.playButton.setVisibility(8);
                this.subtitleTextView.setVisibility(8);
                this.joinButton.setVisibility(8);
                this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.titleTextView.setTextSize(1, 14.0f);
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 2.0f));
                this.titleTextView.setPadding(0, 0, 0, 0);
                ImageView imageView3 = this.playbackSpeedButton;
                if (imageView3 != null) {
                    imageView3.setVisibility(8);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.topPadding = 0.0f;
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsCacheChanged);
        } else {
            for (int i = 0; i < 3; i++) {
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidStart);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.groupCallUpdated);
                NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.groupCallTypingsUpdated);
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.messagePlayingSpeedChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didStartedCall);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcAmplitudeEvent);
        }
        if (this.currentStyle == 3) {
            Theme.getFragmentContextViewWavesDrawable().removeParent(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsCacheChanged);
            FragmentContextView fragmentContextView = this.additionalContextView;
            if (fragmentContextView != null) {
                fragmentContextView.checkVisibility();
            }
            checkLiveLocation(true);
        } else {
            for (int i = 0; i < 3; i++) {
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidStart);
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.groupCallUpdated);
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.groupCallTypingsUpdated);
            }
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.messagePlayingSpeedChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didStartedCall);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didEndCall);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcAmplitudeEvent);
            FragmentContextView fragmentContextView2 = this.additionalContextView;
            if (fragmentContextView2 != null) {
                fragmentContextView2.checkVisibility();
            }
            if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) {
                BaseFragment baseFragment = this.fragment;
                if (!(baseFragment instanceof ChatActivity) || ((ChatActivity) baseFragment).getGroupCall() == null) {
                    checkPlayer(true);
                    updatePlaybackButton();
                } else {
                    checkCall(true);
                }
            } else {
                checkCall(true);
            }
        }
        if (this.currentStyle == 3) {
            Theme.getFragmentContextViewWavesDrawable().addParent(this);
            boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
            if (this.isMuted != z) {
                this.isMuted = z;
                this.muteDrawable.setCustomEndFrame(z ? 0 : 14);
                RLottieDrawable rLottieDrawable = this.muteDrawable;
                rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame(), false, true);
                this.muteButton.invalidate();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, AndroidUtilities.dp2((float) (getStyleHeight() + 2)));
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.liveLocationsChanged) {
            checkLiveLocation(false);
        } else if (i == NotificationCenter.liveLocationsCacheChanged) {
            if (this.fragment instanceof ChatActivity) {
                if (((ChatActivity) this.fragment).getDialogId() == objArr[0].longValue()) {
                    checkLocationString();
                }
            }
        } else if (i == NotificationCenter.messagePlayingDidStart || i == NotificationCenter.messagePlayingPlayStateChanged || i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.didEndCall) {
            checkPlayer(false);
        } else if (i == NotificationCenter.didStartedCall || i == NotificationCenter.groupCallUpdated) {
            checkCall(false);
        } else if (i == NotificationCenter.groupCallTypingsUpdated) {
            if (this.visible && this.currentStyle == 4) {
                ChatObject.Call groupCall = ((ChatActivity) this.fragment).getGroupCall();
                if (groupCall != null) {
                    int i3 = groupCall.speakingMembersCount;
                    if (i3 != 0) {
                        this.subtitleTextView.setText(LocaleController.formatPluralString("MembersTalking", i3));
                    } else {
                        this.subtitleTextView.setText(LocaleController.formatPluralString("Members", groupCall.call.participants_count));
                    }
                }
                updateAvatars();
            }
        } else if (i == NotificationCenter.messagePlayingSpeedChanged) {
            updatePlaybackButton();
        } else if (i == NotificationCenter.webRtcAmplitudeEvent) {
            Theme.getFragmentContextViewWavesDrawable().setAmplitude((double) objArr[0].floatValue());
        }
    }

    private int getStyleHeight() {
        return this.currentStyle == 4 ? 48 : 36;
    }

    private void checkLiveLocation(boolean z) {
        boolean z2;
        String str;
        String str2;
        View fragmentView = this.fragment.getFragmentView();
        if (!z && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
            z = true;
        }
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof DialogsActivity) {
            z2 = LocationController.getLocationsCount() != 0;
        } else {
            z2 = LocationController.getInstance(baseFragment.getCurrentAccount()).isSharingLocation(((ChatActivity) this.fragment).getDialogId());
        }
        if (!z2) {
            this.lastLocationSharingCount = -1;
            AndroidUtilities.cancelRunOnUIThread(this.checkLocationRunnable);
            if (this.visible) {
                this.visible = false;
                if (z) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f})});
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                            FragmentContextView.this.setVisibility(8);
                            AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
                return;
            }
            return;
        }
        updateStyle(2);
        this.playButton.setImageDrawable(new ShareLocationDrawable(getContext(), 1));
        if (z && this.topPadding == 0.0f) {
            setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
        }
        if (!this.visible) {
            if (!z) {
                AnimatorSet animatorSet4 = this.animatorSet;
                if (animatorSet4 != null) {
                    animatorSet4.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet5 = new AnimatorSet();
                this.animatorSet = animatorSet5;
                animatorSet5.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2((float) getStyleHeight())})});
                this.animatorSet.setDuration(200);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                            AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
            }
            this.visible = true;
            setVisibility(0);
        }
        if (this.fragment instanceof DialogsActivity) {
            String string = LocaleController.getString("LiveLocationContext", NUM);
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < 3; i++) {
                arrayList.addAll(LocationController.getInstance(i).sharingLocationsUI);
            }
            if (arrayList.size() == 1) {
                LocationController.SharingLocationInfo sharingLocationInfo = (LocationController.SharingLocationInfo) arrayList.get(0);
                int dialogId = (int) sharingLocationInfo.messageObject.getDialogId();
                if (dialogId > 0) {
                    str2 = UserObject.getFirstName(MessagesController.getInstance(sharingLocationInfo.messageObject.currentAccount).getUser(Integer.valueOf(dialogId)));
                    str = LocaleController.getString("AttachLiveLocationIsSharing", NUM);
                } else {
                    TLRPC$Chat chat = MessagesController.getInstance(sharingLocationInfo.messageObject.currentAccount).getChat(Integer.valueOf(-dialogId));
                    str2 = chat != null ? chat.title : "";
                    str = LocaleController.getString("AttachLiveLocationIsSharingChat", NUM);
                }
            } else {
                str2 = LocaleController.formatPluralString("Chats", arrayList.size());
                str = LocaleController.getString("AttachLiveLocationIsSharingChats", NUM);
            }
            String format = String.format(str, new Object[]{string, str2});
            int indexOf = format.indexOf(string);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(format);
            this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), indexOf, string.length() + indexOf, 18);
            this.titleTextView.setText(spannableStringBuilder);
            return;
        }
        this.checkLocationRunnable.run();
        checkLocationString();
    }

    /* access modifiers changed from: private */
    public void checkLocationString() {
        int i;
        String str;
        BaseFragment baseFragment = this.fragment;
        if ((baseFragment instanceof ChatActivity) && this.titleTextView != null) {
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            long dialogId = chatActivity.getDialogId();
            int currentAccount = chatActivity.getCurrentAccount();
            ArrayList arrayList = LocationController.getInstance(currentAccount).locationsCache.get(dialogId);
            if (!this.firstLocationsLoaded) {
                LocationController.getInstance(currentAccount).loadLiveLocations(dialogId);
                this.firstLocationsLoaded = true;
            }
            TLRPC$User tLRPC$User = null;
            if (arrayList != null) {
                int clientUserId = UserConfig.getInstance(currentAccount).getClientUserId();
                int currentTime = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                i = 0;
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList.get(i2);
                    TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
                    if (tLRPC$MessageMedia != null && tLRPC$Message.date + tLRPC$MessageMedia.period > currentTime) {
                        int fromChatId = MessageObject.getFromChatId(tLRPC$Message);
                        if (tLRPC$User == null && fromChatId != clientUserId) {
                            tLRPC$User = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(fromChatId));
                        }
                        i++;
                    }
                }
            } else {
                i = 0;
            }
            if (this.lastLocationSharingCount != i) {
                this.lastLocationSharingCount = i;
                String string = LocaleController.getString("LiveLocationContext", NUM);
                if (i == 0) {
                    str = string;
                } else {
                    int i3 = i - 1;
                    if (LocationController.getInstance(currentAccount).isSharingLocation(dialogId)) {
                        if (i3 == 0) {
                            str = String.format("%1$s - %2$s", new Object[]{string, LocaleController.getString("ChatYourSelfName", NUM)});
                        } else if (i3 != 1 || tLRPC$User == null) {
                            str = String.format("%1$s - %2$s %3$s", new Object[]{string, LocaleController.getString("ChatYourSelfName", NUM), LocaleController.formatPluralString("AndOther", i3)});
                        } else {
                            str = String.format("%1$s - %2$s", new Object[]{string, LocaleController.formatString("SharingYouAndOtherName", NUM, UserObject.getFirstName(tLRPC$User))});
                        }
                    } else if (i3 != 0) {
                        str = String.format("%1$s - %2$s %3$s", new Object[]{string, UserObject.getFirstName(tLRPC$User), LocaleController.formatPluralString("AndOther", i3)});
                    } else {
                        str = String.format("%1$s - %2$s", new Object[]{string, UserObject.getFirstName(tLRPC$User)});
                    }
                }
                if (!str.equals(this.lastString)) {
                    this.lastString = str;
                    int indexOf = str.indexOf(string);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                    this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
                    if (indexOf >= 0) {
                        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), indexOf, string.length() + indexOf, 18);
                    }
                    this.titleTextView.setText(spannableStringBuilder);
                }
            }
        }
    }

    private void checkPlayer(boolean z) {
        SpannableStringBuilder spannableStringBuilder;
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        View fragmentView = this.fragment.getFragmentView();
        if (!z && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
            z = true;
        }
        if (playingMessageObject == null || playingMessageObject.getId() == 0 || playingMessageObject.isVideo()) {
            this.lastMessageObject = null;
            boolean z2 = (!this.supportsCalls || VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) ? false : true;
            if (!z2) {
                BaseFragment baseFragment = this.fragment;
                if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).getGroupCall() != null) {
                    z2 = true;
                }
            }
            if (z2) {
                checkCall(false);
            } else if (this.visible) {
                this.visible = false;
                if (z) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{0.0f})});
                this.animatorSet.setDuration(200);
                FragmentContextViewDelegate fragmentContextViewDelegate = this.delegate;
                if (fragmentContextViewDelegate != null) {
                    fragmentContextViewDelegate.onAnimation(true, false);
                }
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        NotificationCenter.getInstance(FragmentContextView.this.account).onAnimationFinish(FragmentContextView.this.animationIndex);
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                            FragmentContextView.this.setVisibility(8);
                            if (FragmentContextView.this.delegate != null) {
                                FragmentContextView.this.delegate.onAnimation(false, false);
                            }
                            AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
                this.animationIndex = NotificationCenter.getInstance(this.account).setAnimationInProgress(this.animationIndex, (int[]) null);
            }
        } else {
            int i = this.currentStyle;
            updateStyle(0);
            if (z && this.topPadding == 0.0f) {
                setTopPadding((float) AndroidUtilities.dp2((float) getStyleHeight()));
                FragmentContextView fragmentContextView = this.additionalContextView;
                if (fragmentContextView == null || fragmentContextView.getVisibility() != 0) {
                    ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp((float) getStyleHeight());
                } else {
                    ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp((float) (getStyleHeight() + this.additionalContextView.getStyleHeight()));
                }
                FragmentContextViewDelegate fragmentContextViewDelegate2 = this.delegate;
                if (fragmentContextViewDelegate2 != null) {
                    fragmentContextViewDelegate2.onAnimation(true, true);
                    this.delegate.onAnimation(false, true);
                }
            }
            if (!this.visible) {
                if (!z) {
                    AnimatorSet animatorSet4 = this.animatorSet;
                    if (animatorSet4 != null) {
                        animatorSet4.cancel();
                        this.animatorSet = null;
                    }
                    this.animatorSet = new AnimatorSet();
                    FragmentContextView fragmentContextView2 = this.additionalContextView;
                    if (fragmentContextView2 == null || fragmentContextView2.getVisibility() != 0) {
                        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp((float) getStyleHeight());
                    } else {
                        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp((float) (getStyleHeight() + this.additionalContextView.getStyleHeight()));
                    }
                    FragmentContextViewDelegate fragmentContextViewDelegate3 = this.delegate;
                    if (fragmentContextViewDelegate3 != null) {
                        fragmentContextViewDelegate3.onAnimation(true, true);
                    }
                    this.animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "topPadding", new float[]{(float) AndroidUtilities.dp2((float) getStyleHeight())})});
                    this.animatorSet.setDuration(200);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            NotificationCenter.getInstance(FragmentContextView.this.account).onAnimationFinish(FragmentContextView.this.animationIndex);
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animator)) {
                                if (FragmentContextView.this.delegate != null) {
                                    FragmentContextView.this.delegate.onAnimation(false, true);
                                }
                                AnimatorSet unused = FragmentContextView.this.animatorSet = null;
                            }
                        }
                    });
                    this.animatorSet.start();
                    this.animationIndex = NotificationCenter.getInstance(this.account).setAnimationInProgress(this.animationIndex, (int[]) null);
                }
                this.visible = true;
                setVisibility(0);
            }
            if (MediaController.getInstance().isMessagePaused()) {
                this.playPauseDrawable.setPause(false, !z);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
            } else {
                this.playPauseDrawable.setPause(true, !z);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
            }
            if (this.lastMessageObject != playingMessageObject || i != 0) {
                this.lastMessageObject = playingMessageObject;
                if (playingMessageObject.isVoice() || this.lastMessageObject.isRoundVideo()) {
                    this.isMusic = false;
                    ImageView imageView = this.playbackSpeedButton;
                    if (imageView != null) {
                        imageView.setAlpha(1.0f);
                        this.playbackSpeedButton.setEnabled(true);
                    }
                    this.titleTextView.setPadding(0, 0, AndroidUtilities.dp(44.0f), 0);
                    spannableStringBuilder = new SpannableStringBuilder(String.format("%s %s", new Object[]{playingMessageObject.getMusicAuthor(), playingMessageObject.getMusicTitle()}));
                    this.titleTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                    updatePlaybackButton();
                } else {
                    this.isMusic = true;
                    if (this.playbackSpeedButton == null) {
                        this.titleTextView.setPadding(0, 0, 0, 0);
                    } else if (playingMessageObject.getDuration() >= 1200) {
                        this.playbackSpeedButton.setAlpha(1.0f);
                        this.playbackSpeedButton.setEnabled(true);
                        this.titleTextView.setPadding(0, 0, AndroidUtilities.dp(44.0f), 0);
                        updatePlaybackButton();
                    } else {
                        this.playbackSpeedButton.setAlpha(0.0f);
                        this.playbackSpeedButton.setEnabled(false);
                        this.titleTextView.setPadding(0, 0, 0, 0);
                    }
                    spannableStringBuilder = new SpannableStringBuilder(String.format("%s - %s", new Object[]{playingMessageObject.getMusicAuthor(), playingMessageObject.getMusicTitle()}));
                    this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
                }
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), 0, playingMessageObject.getMusicAuthor().length(), 18);
                this.titleTextView.setText(spannableStringBuilder);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x005e  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00ab  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkCall(boolean r23) {
        /*
            r22 = this;
            r0 = r22
            boolean r1 = r0.disabledCallCheck
            if (r1 == 0) goto L_0x0007
            return
        L_0x0007:
            org.telegram.ui.ActionBar.BaseFragment r1 = r0.fragment
            android.view.View r1 = r1.getFragmentView()
            r2 = 1
            if (r23 != 0) goto L_0x0026
            if (r1 == 0) goto L_0x0026
            android.view.ViewParent r3 = r1.getParent()
            if (r3 == 0) goto L_0x0024
            android.view.ViewParent r1 = r1.getParent()
            android.view.View r1 = (android.view.View) r1
            int r1 = r1.getVisibility()
            if (r1 == 0) goto L_0x0026
        L_0x0024:
            r1 = 1
            goto L_0x0028
        L_0x0026:
            r1 = r23
        L_0x0028:
            boolean r3 = r0.supportsCalls
            r4 = 0
            if (r3 == 0) goto L_0x0041
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r3 == 0) goto L_0x0041
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r3 = r3.getCallState()
            r5 = 15
            if (r3 == r5) goto L_0x0041
            r3 = 1
            goto L_0x0042
        L_0x0041:
            r3 = 0
        L_0x0042:
            if (r3 != 0) goto L_0x0055
            org.telegram.ui.ActionBar.BaseFragment r5 = r0.fragment
            boolean r6 = r5 instanceof org.telegram.ui.ChatActivity
            if (r6 == 0) goto L_0x0055
            org.telegram.ui.ChatActivity r5 = (org.telegram.ui.ChatActivity) r5
            org.telegram.messenger.ChatObject$Call r5 = r5.getGroupCall()
            if (r5 == 0) goto L_0x0055
            r3 = 1
            r5 = 1
            goto L_0x0056
        L_0x0055:
            r5 = 0
        L_0x0056:
            r6 = 0
            r7 = 200(0xc8, double:9.9E-322)
            java.lang.String r9 = "topPadding"
            r10 = 0
            if (r3 != 0) goto L_0x00ab
            boolean r3 = r0.visible
            if (r3 == 0) goto L_0x0285
            r0.visible = r4
            if (r1 == 0) goto L_0x0076
            int r1 = r22.getVisibility()
            r2 = 8
            if (r1 == r2) goto L_0x0071
            r0.setVisibility(r2)
        L_0x0071:
            r0.setTopPadding(r10)
            goto L_0x0285
        L_0x0076:
            android.animation.AnimatorSet r1 = r0.animatorSet
            if (r1 == 0) goto L_0x007f
            r1.cancel()
            r0.animatorSet = r6
        L_0x007f:
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            r0.animatorSet = r1
            android.animation.Animator[] r3 = new android.animation.Animator[r2]
            float[] r2 = new float[r2]
            r2[r4] = r10
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r0, r9, r2)
            r3[r4] = r2
            r1.playTogether(r3)
            android.animation.AnimatorSet r1 = r0.animatorSet
            r1.setDuration(r7)
            android.animation.AnimatorSet r1 = r0.animatorSet
            org.telegram.ui.Components.FragmentContextView$7 r2 = new org.telegram.ui.Components.FragmentContextView$7
            r2.<init>()
            r1.addListener(r2)
            android.animation.AnimatorSet r1 = r0.animatorSet
            r1.start()
            goto L_0x0285
        L_0x00ab:
            r3 = 3
            if (r5 == 0) goto L_0x018e
            int r5 = r0.currentStyle
            r11 = 4
            if (r5 != r11) goto L_0x00b5
            r5 = 1
            goto L_0x00b6
        L_0x00b5:
            r5 = 0
        L_0x00b6:
            r0.updateStyle(r11)
            org.telegram.ui.ActionBar.BaseFragment r11 = r0.fragment
            org.telegram.ui.ChatActivity r11 = (org.telegram.ui.ChatActivity) r11
            org.telegram.messenger.ChatObject$Call r11 = r11.getGroupCall()
            org.telegram.tgnet.TLRPC$GroupCall r12 = r11.call
            int r12 = r12.participants_count
            if (r12 != 0) goto L_0x00d6
            android.widget.TextView r12 = r0.subtitleTextView
            r13 = 2131625897(0x7f0e07a9, float:1.8879015E38)
            java.lang.String r14 = "MembersTalkingNobody"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r12.setText(r13)
            goto L_0x00f1
        L_0x00d6:
            int r13 = r11.speakingMembersCount
            if (r13 == 0) goto L_0x00e6
            android.widget.TextView r12 = r0.subtitleTextView
            java.lang.String r14 = "MembersTalking"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatPluralString(r14, r13)
            r12.setText(r13)
            goto L_0x00f1
        L_0x00e6:
            android.widget.TextView r13 = r0.subtitleTextView
            java.lang.String r14 = "Members"
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r14, r12)
            r13.setText(r12)
        L_0x00f1:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r11 = r11.sortedParticipants
            int r11 = r11.size()
            int r3 = java.lang.Math.min(r3, r11)
            r22.updateAvatars()
            r11 = 10
            if (r3 != 0) goto L_0x0103
            goto L_0x010a
        L_0x0103:
            int r3 = r3 - r2
            int r3 = r3 * 24
            int r3 = r3 + r11
            int r3 = r3 + 32
            int r11 = r11 + r3
        L_0x010a:
            org.telegram.ui.Components.FragmentContextView$AvatarImageView r3 = r0.avatars
            boolean r3 = r3.wasDraw
            if (r3 == 0) goto L_0x0161
            if (r5 == 0) goto L_0x0161
            android.widget.TextView r3 = r0.titleTextView
            android.view.ViewGroup$LayoutParams r3 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            int r3 = r3.leftMargin
            float r5 = (float) r11
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r5)
            if (r12 == r3) goto L_0x0161
            android.widget.TextView r12 = r0.titleTextView
            float r12 = r12.getTranslationX()
            float r3 = (float) r3
            float r12 = r12 + r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            float r12 = r12 - r3
            android.widget.TextView r3 = r0.titleTextView
            r3.setTranslationX(r12)
            android.widget.TextView r3 = r0.subtitleTextView
            r3.setTranslationX(r12)
            android.widget.TextView r3 = r0.titleTextView
            android.view.ViewPropertyAnimator r3 = r3.animate()
            android.view.ViewPropertyAnimator r3 = r3.translationX(r10)
            r12 = 220(0xdc, double:1.087E-321)
            android.view.ViewPropertyAnimator r3 = r3.setDuration(r12)
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r3.setInterpolator(r5)
            android.widget.TextView r3 = r0.subtitleTextView
            android.view.ViewPropertyAnimator r3 = r3.animate()
            android.view.ViewPropertyAnimator r3 = r3.translationX(r10)
            android.view.ViewPropertyAnimator r3 = r3.setDuration(r12)
            r3.setInterpolator(r5)
        L_0x0161:
            android.widget.TextView r3 = r0.titleTextView
            r12 = -1
            r13 = 1101004800(0x41a00000, float:20.0)
            r14 = 51
            float r5 = (float) r11
            r16 = 1084227584(0x40a00000, float:5.0)
            r17 = 1108344832(0x42100000, float:36.0)
            r18 = 0
            r15 = r5
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r3.setLayoutParams(r11)
            android.widget.TextView r3 = r0.subtitleTextView
            r15 = -1
            r16 = 1101004800(0x41a00000, float:20.0)
            r17 = 51
            r19 = 1103626240(0x41CLASSNAME, float:25.0)
            r20 = 1108344832(0x42100000, float:36.0)
            r21 = 0
            r18 = r5
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r16, r17, r18, r19, r20, r21)
            r3.setLayoutParams(r5)
            goto L_0x01a6
        L_0x018e:
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r5 == 0) goto L_0x01a3
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$Call r5 = r5.groupCall
            if (r5 == 0) goto L_0x01a3
            r22.updateAvatars()
            r0.updateStyle(r3)
            goto L_0x01a6
        L_0x01a3:
            r0.updateStyle(r2)
        L_0x01a6:
            if (r1 == 0) goto L_0x01ae
            float r3 = r0.topPadding
            int r3 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r3 == 0) goto L_0x01c2
        L_0x01ae:
            boolean r3 = r0.visible
            if (r3 == 0) goto L_0x0205
            float r3 = r0.topPadding
            int r5 = r22.getStyleHeight()
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp2(r5)
            float r5 = (float) r5
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 == 0) goto L_0x0205
        L_0x01c2:
            int r3 = r22.getStyleHeight()
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp2(r3)
            float r3 = (float) r3
            r0.setTopPadding(r3)
            org.telegram.ui.Components.FragmentContextView r3 = r0.additionalContextView
            if (r3 == 0) goto L_0x01f3
            int r3 = r3.getVisibility()
            if (r3 != 0) goto L_0x01f3
            android.view.ViewGroup$LayoutParams r3 = r22.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            int r5 = r22.getStyleHeight()
            org.telegram.ui.Components.FragmentContextView r10 = r0.additionalContextView
            int r10 = r10.getStyleHeight()
            int r5 = r5 + r10
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = -r5
            r3.topMargin = r5
            goto L_0x0205
        L_0x01f3:
            android.view.ViewGroup$LayoutParams r3 = r22.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            int r5 = r22.getStyleHeight()
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = -r5
            r3.topMargin = r5
        L_0x0205:
            boolean r3 = r0.visible
            if (r3 != 0) goto L_0x0285
            if (r1 != 0) goto L_0x0280
            android.animation.AnimatorSet r1 = r0.animatorSet
            if (r1 == 0) goto L_0x0214
            r1.cancel()
            r0.animatorSet = r6
        L_0x0214:
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            r0.animatorSet = r1
            org.telegram.ui.Components.FragmentContextView r1 = r0.additionalContextView
            if (r1 == 0) goto L_0x023f
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x023f
            android.view.ViewGroup$LayoutParams r1 = r22.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r3 = r22.getStyleHeight()
            org.telegram.ui.Components.FragmentContextView r5 = r0.additionalContextView
            int r5 = r5.getStyleHeight()
            int r3 = r3 + r5
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = -r3
            r1.topMargin = r3
            goto L_0x0251
        L_0x023f:
            android.view.ViewGroup$LayoutParams r1 = r22.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r3 = r22.getStyleHeight()
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = -r3
            r1.topMargin = r3
        L_0x0251:
            android.animation.AnimatorSet r1 = r0.animatorSet
            android.animation.Animator[] r3 = new android.animation.Animator[r2]
            float[] r5 = new float[r2]
            int r6 = r22.getStyleHeight()
            float r6 = (float) r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp2(r6)
            float r6 = (float) r6
            r5[r4] = r6
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r0, r9, r5)
            r3[r4] = r5
            r1.playTogether(r3)
            android.animation.AnimatorSet r1 = r0.animatorSet
            r1.setDuration(r7)
            android.animation.AnimatorSet r1 = r0.animatorSet
            org.telegram.ui.Components.FragmentContextView$8 r3 = new org.telegram.ui.Components.FragmentContextView$8
            r3.<init>()
            r1.addListener(r3)
            android.animation.AnimatorSet r1 = r0.animatorSet
            r1.start()
        L_0x0280:
            r0.visible = r2
            r0.setVisibility(r4)
        L_0x0285:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.checkCall(boolean):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:27:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateAvatars() {
        /*
            r7 = this;
            org.telegram.ui.Components.FragmentContextView$AvatarImageView r0 = r7.avatars
            android.animation.ValueAnimator r1 = r0.transitionProgressAnimator
            if (r1 != 0) goto L_0x0062
            int r0 = r7.currentStyle
            r1 = 4
            r2 = 0
            if (r0 != r1) goto L_0x0020
            org.telegram.ui.ActionBar.BaseFragment r0 = r7.fragment
            boolean r1 = r0 instanceof org.telegram.ui.ChatActivity
            if (r1 == 0) goto L_0x001d
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            org.telegram.messenger.ChatObject$Call r1 = r0.getGroupCall()
            int r0 = r0.getCurrentAccount()
            goto L_0x0038
        L_0x001d:
            int r0 = r7.account
            goto L_0x0037
        L_0x0020:
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r0 == 0) goto L_0x0035
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$Call r1 = r0.groupCall
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r0 = r0.getAccount()
            goto L_0x0038
        L_0x0035:
            int r0 = r7.account
        L_0x0037:
            r1 = r2
        L_0x0038:
            if (r1 == 0) goto L_0x0065
            r3 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r4 = r1.sortedParticipants
            int r4 = r4.size()
        L_0x0041:
            r5 = 3
            if (r3 >= r5) goto L_0x005c
            if (r3 >= r4) goto L_0x0054
            org.telegram.ui.Components.FragmentContextView$AvatarImageView r5 = r7.avatars
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r6 = r1.sortedParticipants
            java.lang.Object r6 = r6.get(r3)
            org.telegram.tgnet.TLObject r6 = (org.telegram.tgnet.TLObject) r6
            r5.setObject(r3, r0, r6)
            goto L_0x0059
        L_0x0054:
            org.telegram.ui.Components.FragmentContextView$AvatarImageView r5 = r7.avatars
            r5.setObject(r3, r0, r2)
        L_0x0059:
            int r3 = r3 + 1
            goto L_0x0041
        L_0x005c:
            org.telegram.ui.Components.FragmentContextView$AvatarImageView r0 = r7.avatars
            r0.commitTransition()
            goto L_0x0065
        L_0x0062:
            r0.updateAfterTransitionEnd()
        L_0x0065:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.updateAvatars():void");
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (!this.drawOverlay || getVisibility() == 0) {
            boolean z = true;
            int i = 0;
            if (this.currentStyle != 3 || !this.drawOverlay) {
                z = false;
            } else {
                if (VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute()) {
                    i = 1;
                }
                Theme.getFragmentContextViewWavesDrawable().setState(i);
                float measuredHeight = 1.0f - (this.topPadding / ((float) getMeasuredHeight()));
                float f = measuredHeight * 2.0f;
                if (f > 1.0f) {
                    f = 1.0f;
                }
                canvas.save();
                canvas.clipRect(0.0f, (((((float) (-AndroidUtilities.dp(20.0f))) * (1.0f - f)) + ((float) getMeasuredHeight())) - ((float) AndroidUtilities.dp(2.0f))) - (((float) getMeasuredHeight()) * (1.0f - measuredHeight)), (float) getMeasuredWidth(), (float) getMeasuredHeight());
                Theme.getFragmentContextViewWavesDrawable().draw(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)), canvas, this);
                invalidate();
            }
            super.dispatchDraw(canvas);
            if (z) {
                canvas.restore();
            }
        }
    }

    public void setDrawOverlay(boolean z) {
        this.drawOverlay = z;
    }

    public void invalidate() {
        super.invalidate();
        if (this.currentStyle == 3 && getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    public int getCurrentStyle() {
        return this.currentStyle;
    }
}
