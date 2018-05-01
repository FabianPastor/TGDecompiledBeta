package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPBaseService.StateListener;
import org.telegram.messenger.voip.VoIPController;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.DarkAlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CorrectlyMeasuringTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.CallSwipeView;
import org.telegram.ui.Components.voip.CallSwipeView.Listener;
import org.telegram.ui.Components.voip.CheckableImageView;
import org.telegram.ui.Components.voip.DarkTheme;
import org.telegram.ui.Components.voip.FabBackgroundDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPActivity extends Activity implements NotificationCenterDelegate, StateListener {
    private static final String TAG = "tg-voip-ui";
    private View acceptBtn;
    private CallSwipeView acceptSwipe;
    private TextView accountNameText;
    private ImageView addMemberBtn;
    private ImageView blurOverlayView1;
    private ImageView blurOverlayView2;
    private Bitmap blurredPhoto1;
    private Bitmap blurredPhoto2;
    private LinearLayout bottomButtons;
    private TextView brandingText;
    private int callState;
    private View cancelBtn;
    private ImageView chatBtn;
    private FrameLayout content;
    private Animator currentAcceptAnim;
    private int currentAccount = -1;
    private Animator currentDeclineAnim;
    private View declineBtn;
    private CallSwipeView declineSwipe;
    private boolean didAcceptFromHere = false;
    private TextView durationText;
    private AnimatorSet ellAnimator;
    private TextAlphaSpan[] ellSpans;
    private AnimatorSet emojiAnimator;
    boolean emojiExpanded;
    private TextView emojiExpandedText;
    boolean emojiTooltipVisible;
    private LinearLayout emojiWrap;
    private View endBtn;
    private FabBackgroundDrawable endBtnBg;
    private View endBtnIcon;
    private boolean firstStateChange = true;
    private TextView hintTextView;
    private boolean isIncomingWaiting;
    private ImageView[] keyEmojiViews = new ImageView[4];
    private boolean keyEmojiVisible;
    private String lastStateText;
    private CheckableImageView micToggle;
    private TextView nameText;
    private BackupImageView photoView;
    private AnimatorSet retryAnim;
    private boolean retrying;
    private int signalBarsCount;
    private SignalBarsDrawable signalBarsDrawable;
    private CheckableImageView spkToggle;
    private TextView stateText;
    private TextView stateText2;
    private LinearLayout swipeViewsWrap;
    private Animator textChangingAnim;
    private Animator tooltipAnim;
    private Runnable tooltipHider;
    private User user;

    /* renamed from: org.telegram.ui.VoIPActivity$2 */
    class C17522 implements OnClickListener {
        private int tapCount = null;

        C17522() {
        }

        public void onClick(View view) {
            if (BuildVars.DEBUG_VERSION == null) {
                if (this.tapCount != 9) {
                    this.tapCount++;
                    return;
                }
            }
            VoIPActivity.this.showDebugAlert();
            this.tapCount = null;
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$3 */
    class C17593 implements OnClickListener {

        /* renamed from: org.telegram.ui.VoIPActivity$3$1 */
        class C17531 implements Runnable {
            C17531() {
            }

            public void run() {
                if (VoIPService.getSharedInstance() != null || VoIPActivity.this.isFinishing()) {
                    if (VoIPService.getSharedInstance() != null) {
                        VoIPService.getSharedInstance().registerStateListener(VoIPActivity.this);
                    }
                    return;
                }
                VoIPActivity.this.endBtn.postDelayed(this, 100);
            }
        }

        C17593() {
        }

        public void onClick(View view) {
            VoIPActivity.this.endBtn.setEnabled(false);
            if (VoIPActivity.this.retrying != null) {
                view = new Intent(VoIPActivity.this, VoIPService.class);
                view.putExtra("user_id", VoIPActivity.this.user.id);
                view.putExtra("is_outgoing", true);
                view.putExtra("start_incall_activity", false);
                view.putExtra("account", VoIPActivity.this.currentAccount);
                try {
                    VoIPActivity.this.startService(view);
                } catch (Throwable th) {
                    FileLog.m3e(th);
                }
                VoIPActivity.this.hideRetry();
                VoIPActivity.this.endBtn.postDelayed(new C17531(), 100);
                return;
            }
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().hangUp();
            }
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$4 */
    class C17604 implements OnClickListener {
        C17604() {
        }

        public void onClick(View view) {
            view = VoIPService.getSharedInstance();
            if (view != null) {
                view.toggleSpeakerphoneOrShowRouteSheet(VoIPActivity.this);
            }
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$5 */
    class C17615 implements OnClickListener {
        C17615() {
        }

        public void onClick(View view) {
            if (VoIPService.getSharedInstance() == null) {
                VoIPActivity.this.finish();
                return;
            }
            view = VoIPActivity.this.micToggle.isChecked() ^ 1;
            VoIPActivity.this.micToggle.setChecked(view);
            VoIPService.getSharedInstance().setMicMute(view);
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$6 */
    class C17626 implements OnClickListener {
        C17626() {
        }

        public void onClick(View view) {
            if (VoIPActivity.this.isIncomingWaiting != null) {
                VoIPActivity.this.showMessagesSheet();
                return;
            }
            view = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("com.tmessages.openchat");
            stringBuilder.append(Math.random());
            stringBuilder.append(ConnectionsManager.DEFAULT_DATACENTER_ID);
            view.setAction(stringBuilder.toString());
            view.putExtra("currentAccount", VoIPActivity.this.currentAccount);
            view.setFlags(32768);
            view.putExtra("userId", VoIPActivity.this.user.id);
            VoIPActivity.this.startActivity(view);
            VoIPActivity.this.finish();
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$9 */
    class C17679 implements OnClickListener {
        C17679() {
        }

        public void onClick(View view) {
            VoIPActivity.this.finish();
        }
    }

    private class SignalBarsDrawable extends Drawable {
        private int[] barHeights;
        private int offsetStart;
        private Paint paint;
        private RectF rect;

        public int getOpacity() {
            return -3;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        private SignalBarsDrawable() {
            this.barHeights = new int[]{AndroidUtilities.dp(3.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(12.0f)};
            this.paint = new Paint(1);
            this.rect = new RectF();
            this.offsetStart = 6;
        }

        public void draw(Canvas canvas) {
            if (VoIPActivity.this.callState == 3 || VoIPActivity.this.callState == 5) {
                this.paint.setColor(-1);
                int dp = getBounds().left + AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) this.offsetStart);
                int i = getBounds().top;
                int i2 = 0;
                while (i2 < 4) {
                    int i3 = i2 + 1;
                    this.paint.setAlpha(i3 <= VoIPActivity.this.signalBarsCount ? 242 : 102);
                    this.rect.set((float) (AndroidUtilities.dp((float) (4 * i2)) + dp), (float) ((getIntrinsicHeight() + i) - this.barHeights[i2]), (float) (((AndroidUtilities.dp(4.0f) * i2) + dp) + AndroidUtilities.dp(3.0f)), (float) (getIntrinsicHeight() + i));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(0.3f), (float) AndroidUtilities.dp(0.3f), this.paint);
                    i2 = i3;
                }
            }
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp((float) (15 + this.offsetStart));
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(12.0f);
        }
    }

    private class TextAlphaSpan extends CharacterStyle {
        private int alpha = null;

        public int getAlpha() {
            return this.alpha;
        }

        public void setAlpha(int i) {
            this.alpha = i;
            VoIPActivity.this.stateText.invalidate();
            VoIPActivity.this.stateText2.invalidate();
        }

        public void updateDrawState(TextPaint textPaint) {
            textPaint.setAlpha(this.alpha);
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$1 */
    class C23101 implements ImageReceiverDelegate {
        C23101() {
        }

        public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
            imageReceiver = imageReceiver.getBitmapSafe();
            if (imageReceiver != null) {
                VoIPActivity.this.updateBlurredPhotos(imageReceiver);
            }
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$7 */
    class C23117 implements Listener {

        /* renamed from: org.telegram.ui.VoIPActivity$7$1 */
        class C17631 extends AnimatorListenerAdapter {
            C17631() {
            }

            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.currentDeclineAnim = null;
            }
        }

        /* renamed from: org.telegram.ui.VoIPActivity$7$2 */
        class C17642 extends AnimatorListenerAdapter {
            C17642() {
            }

            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.currentDeclineAnim = null;
            }
        }

        C23117() {
        }

        public void onDragComplete() {
            VoIPActivity.this.acceptSwipe.setEnabled(false);
            VoIPActivity.this.declineSwipe.setEnabled(false);
            if (VoIPService.getSharedInstance() == null) {
                VoIPActivity.this.finish();
                return;
            }
            VoIPActivity.this.didAcceptFromHere = true;
            if (VERSION.SDK_INT < 23 || VoIPActivity.this.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                VoIPService.getSharedInstance().acceptIncomingCall();
                VoIPActivity.this.callAccepted();
            } else {
                VoIPActivity.this.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
            }
        }

        public void onDragStart() {
            if (VoIPActivity.this.currentDeclineAnim != null) {
                VoIPActivity.this.currentDeclineAnim.cancel();
            }
            Animator animatorSet = new AnimatorSet();
            r1 = new Animator[2];
            r1[0] = ObjectAnimator.ofFloat(VoIPActivity.this.declineSwipe, "alpha", new float[]{0.2f});
            r1[1] = ObjectAnimator.ofFloat(VoIPActivity.this.declineBtn, "alpha", new float[]{0.2f});
            animatorSet.playTogether(r1);
            animatorSet.setDuration(200);
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.addListener(new C17631());
            VoIPActivity.this.currentDeclineAnim = animatorSet;
            animatorSet.start();
            VoIPActivity.this.declineSwipe.stopAnimatingArrows();
        }

        public void onDragCancel() {
            if (VoIPActivity.this.currentDeclineAnim != null) {
                VoIPActivity.this.currentDeclineAnim.cancel();
            }
            Animator animatorSet = new AnimatorSet();
            r1 = new Animator[2];
            r1[0] = ObjectAnimator.ofFloat(VoIPActivity.this.declineSwipe, "alpha", new float[]{1.0f});
            r1[1] = ObjectAnimator.ofFloat(VoIPActivity.this.declineBtn, "alpha", new float[]{1.0f});
            animatorSet.playTogether(r1);
            animatorSet.setDuration(200);
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.addListener(new C17642());
            VoIPActivity.this.currentDeclineAnim = animatorSet;
            animatorSet.start();
            VoIPActivity.this.declineSwipe.startAnimatingArrows();
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$8 */
    class C23128 implements Listener {

        /* renamed from: org.telegram.ui.VoIPActivity$8$1 */
        class C17651 extends AnimatorListenerAdapter {
            C17651() {
            }

            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.currentAcceptAnim = null;
            }
        }

        /* renamed from: org.telegram.ui.VoIPActivity$8$2 */
        class C17662 extends AnimatorListenerAdapter {
            C17662() {
            }

            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.currentAcceptAnim = null;
            }
        }

        C23128() {
        }

        public void onDragComplete() {
            VoIPActivity.this.acceptSwipe.setEnabled(false);
            VoIPActivity.this.declineSwipe.setEnabled(false);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().declineIncomingCall(4, null);
            } else {
                VoIPActivity.this.finish();
            }
        }

        public void onDragStart() {
            if (VoIPActivity.this.currentAcceptAnim != null) {
                VoIPActivity.this.currentAcceptAnim.cancel();
            }
            Animator animatorSet = new AnimatorSet();
            r1 = new Animator[2];
            r1[0] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, "alpha", new float[]{0.2f});
            r1[1] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, "alpha", new float[]{0.2f});
            animatorSet.playTogether(r1);
            animatorSet.setDuration(200);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.addListener(new C17651());
            VoIPActivity.this.currentAcceptAnim = animatorSet;
            animatorSet.start();
            VoIPActivity.this.acceptSwipe.stopAnimatingArrows();
        }

        public void onDragCancel() {
            if (VoIPActivity.this.currentAcceptAnim != null) {
                VoIPActivity.this.currentAcceptAnim.cancel();
            }
            Animator animatorSet = new AnimatorSet();
            r1 = new Animator[2];
            r1[0] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, "alpha", new float[]{1.0f});
            r1[1] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, "alpha", new float[]{1.0f});
            animatorSet.playTogether(r1);
            animatorSet.setDuration(200);
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.addListener(new C17662());
            VoIPActivity.this.currentAcceptAnim = animatorSet;
            animatorSet.start();
            VoIPActivity.this.acceptSwipe.startAnimatingArrows();
        }
    }

    private void showInviteFragment() {
    }

    protected void onCreate(Bundle bundle) {
        requestWindowFeature(1);
        getWindow().addFlags(524288);
        super.onCreate(bundle);
        if (VoIPService.getSharedInstance() == null) {
            finish();
            return;
        }
        this.currentAccount = VoIPService.getSharedInstance().getAccount();
        if (this.currentAccount == -1) {
            finish();
            return;
        }
        if ((getResources().getConfiguration().screenLayout & 15) < 3) {
            setRequestedOrientation(1);
        }
        bundle = createContentView();
        setContentView(bundle);
        if (VERSION.SDK_INT >= 21) {
            getWindow().addFlags(Integer.MIN_VALUE);
            getWindow().setStatusBarColor(0);
            getWindow().setNavigationBarColor(0);
            getWindow().getDecorView().setSystemUiVisibility(1792);
        } else if (VERSION.SDK_INT >= 19) {
            getWindow().addFlags(201326592);
            getWindow().getDecorView().setSystemUiVisibility(1792);
        }
        this.user = VoIPService.getSharedInstance().getUser();
        if (this.user.photo != null) {
            this.photoView.getImageReceiver().setDelegate(new C23101());
            this.photoView.setImage(this.user.photo.photo_big, null, new ColorDrawable(Theme.ACTION_BAR_VIDEO_EDIT_COLOR));
            this.photoView.setLayerType(2, null);
        } else {
            this.photoView.setVisibility(8);
            bundle.setBackgroundDrawable(new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{-14994098, -14328963}));
        }
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setVolumeControlStream(0);
        this.nameText.setOnClickListener(new C17522());
        this.endBtn.setOnClickListener(new C17593());
        this.spkToggle.setOnClickListener(new C17604());
        this.micToggle.setOnClickListener(new C17615());
        this.chatBtn.setOnClickListener(new C17626());
        this.spkToggle.setChecked(((AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO)).isSpeakerphoneOn());
        this.micToggle.setChecked(VoIPService.getSharedInstance().isMicMute());
        onAudioSettingsChanged();
        this.nameText.setText(ContactsController.formatName(this.user.first_name, this.user.last_name));
        VoIPService.getSharedInstance().registerStateListener(this);
        this.acceptSwipe.setListener(new C23117());
        this.declineSwipe.setListener(new C23128());
        this.cancelBtn.setOnClickListener(new C17679());
        getWindow().getDecorView().setKeepScreenOn(true);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeInCallActivity);
        this.hintTextView.setText(LocaleController.formatString("CallEmojiKeyTooltip", C0446R.string.CallEmojiKeyTooltip, this.user.first_name));
        this.emojiExpandedText.setText(LocaleController.formatString("CallEmojiKeyTooltip", C0446R.string.CallEmojiKeyTooltip, this.user.first_name));
    }

    private View createContentView() {
        View anonymousClass10 = new FrameLayout(this) {
            private void setNegativeMargins(Rect rect, LayoutParams layoutParams) {
                layoutParams.topMargin = -rect.top;
                layoutParams.bottomMargin = -rect.bottom;
                layoutParams.leftMargin = -rect.left;
                layoutParams.rightMargin = -rect.right;
            }

            protected boolean fitSystemWindows(Rect rect) {
                setNegativeMargins(rect, (LayoutParams) VoIPActivity.this.photoView.getLayoutParams());
                setNegativeMargins(rect, (LayoutParams) VoIPActivity.this.blurOverlayView1.getLayoutParams());
                setNegativeMargins(rect, (LayoutParams) VoIPActivity.this.blurOverlayView2.getLayoutParams());
                return super.fitSystemWindows(rect);
            }
        };
        anonymousClass10.setBackgroundColor(0);
        anonymousClass10.setFitsSystemWindows(true);
        anonymousClass10.setClipToPadding(false);
        View anonymousClass11 = new BackupImageView(this) {
            private Drawable bottomGradient = getResources().getDrawable(C0446R.drawable.gradient_bottom);
            private Paint paint = new Paint();
            private Drawable topGradient = getResources().getDrawable(C0446R.drawable.gradient_top);

            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                this.paint.setColor(NUM);
                canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), this.paint);
                this.topGradient.setBounds(0, 0, getWidth(), AndroidUtilities.dp(170.0f));
                this.topGradient.setAlpha(128);
                this.topGradient.draw(canvas);
                this.bottomGradient.setBounds(0, getHeight() - AndroidUtilities.dp(220.0f), getWidth(), getHeight());
                this.bottomGradient.setAlpha(178);
                this.bottomGradient.draw(canvas);
            }
        };
        this.photoView = anonymousClass11;
        anonymousClass10.addView(anonymousClass11);
        this.blurOverlayView1 = new ImageView(this);
        this.blurOverlayView1.setScaleType(ScaleType.CENTER_CROP);
        this.blurOverlayView1.setAlpha(0.0f);
        anonymousClass10.addView(this.blurOverlayView1);
        this.blurOverlayView2 = new ImageView(this);
        this.blurOverlayView2.setScaleType(ScaleType.CENTER_CROP);
        this.blurOverlayView2.setAlpha(0.0f);
        anonymousClass10.addView(this.blurOverlayView2);
        anonymousClass11 = new TextView(this);
        anonymousClass11.setTextColor(-855638017);
        anonymousClass11.setText(LocaleController.getString("VoipInCallBranding", C0446R.string.VoipInCallBranding));
        Drawable mutate = getResources().getDrawable(C0446R.drawable.notification).mutate();
        mutate.setAlpha(204);
        mutate.setBounds(0, 0, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f));
        this.signalBarsDrawable = new SignalBarsDrawable();
        this.signalBarsDrawable.setBounds(0, 0, this.signalBarsDrawable.getIntrinsicWidth(), this.signalBarsDrawable.getIntrinsicHeight());
        Drawable drawable = LocaleController.isRTL ? r6.signalBarsDrawable : mutate;
        if (!LocaleController.isRTL) {
            mutate = r6.signalBarsDrawable;
        }
        anonymousClass11.setCompoundDrawables(drawable, null, mutate, null);
        anonymousClass11.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        anonymousClass11.setGravity(LocaleController.isRTL ? 5 : 3);
        anonymousClass11.setCompoundDrawablePadding(AndroidUtilities.dp(5.0f));
        anonymousClass11.setTextSize(1, 14.0f);
        anonymousClass10.addView(anonymousClass11, LayoutHelper.createFrame(-2, -2.0f, 48 | (LocaleController.isRTL ? 5 : 3), 18.0f, 18.0f, 18.0f, 0.0f));
        r6.brandingText = anonymousClass11;
        anonymousClass11 = new TextView(r6);
        anonymousClass11.setSingleLine();
        anonymousClass11.setTextColor(-1);
        anonymousClass11.setTextSize(1, 40.0f);
        anonymousClass11.setEllipsize(TruncateAt.END);
        anonymousClass11.setGravity(LocaleController.isRTL ? 5 : 3);
        anonymousClass11.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        anonymousClass11.setTypeface(Typeface.create("sans-serif-light", 0));
        r6.nameText = anonymousClass11;
        anonymousClass10.addView(anonymousClass11, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 43.0f, 18.0f, 0.0f));
        anonymousClass11 = new TextView(r6);
        anonymousClass11.setTextColor(-855638017);
        anonymousClass11.setSingleLine();
        anonymousClass11.setEllipsize(TruncateAt.END);
        anonymousClass11.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        anonymousClass11.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        anonymousClass11.setTextSize(1, 15.0f);
        anonymousClass11.setGravity(LocaleController.isRTL ? 5 : 3);
        r6.stateText = anonymousClass11;
        anonymousClass10.addView(anonymousClass11, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        r6.durationText = anonymousClass11;
        anonymousClass11 = new TextView(r6);
        anonymousClass11.setTextColor(-855638017);
        anonymousClass11.setSingleLine();
        anonymousClass11.setEllipsize(TruncateAt.END);
        anonymousClass11.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        anonymousClass11.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        anonymousClass11.setTextSize(1, 15.0f);
        anonymousClass11.setGravity(LocaleController.isRTL ? 5 : 3);
        anonymousClass11.setVisibility(8);
        r6.stateText2 = anonymousClass11;
        anonymousClass10.addView(anonymousClass11, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        r6.ellSpans = new TextAlphaSpan[]{new TextAlphaSpan(), new TextAlphaSpan(), new TextAlphaSpan()};
        anonymousClass11 = new LinearLayout(r6);
        anonymousClass11.setOrientation(0);
        anonymousClass10.addView(anonymousClass11, LayoutHelper.createFrame(-1, -2, 80));
        View textView = new TextView(r6);
        textView.setTextColor(-855638017);
        textView.setSingleLine();
        textView.setEllipsize(TruncateAt.END);
        textView.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        textView.setTextSize(1, 15.0f);
        textView.setGravity(LocaleController.isRTL ? 5 : 3);
        r6.accountNameText = textView;
        anonymousClass10.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 120.0f, 18.0f, 0.0f));
        View checkableImageView = new CheckableImageView(r6);
        checkableImageView.setBackgroundResource(C0446R.drawable.bg_voip_icon_btn);
        mutate = getResources().getDrawable(C0446R.drawable.ic_mic_off_white_24dp).mutate();
        checkableImageView.setAlpha(204);
        checkableImageView.setImageDrawable(mutate);
        checkableImageView.setScaleType(ScaleType.CENTER);
        View frameLayout = new FrameLayout(r6);
        r6.micToggle = checkableImageView;
        frameLayout.addView(checkableImageView, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        anonymousClass11.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f));
        checkableImageView = new ImageView(r6);
        mutate = getResources().getDrawable(C0446R.drawable.ic_chat_bubble_white_24dp).mutate();
        mutate.setAlpha(204);
        checkableImageView.setImageDrawable(mutate);
        checkableImageView.setScaleType(ScaleType.CENTER);
        frameLayout = new FrameLayout(r6);
        r6.chatBtn = checkableImageView;
        frameLayout.addView(checkableImageView, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        anonymousClass11.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f));
        checkableImageView = new CheckableImageView(r6);
        checkableImageView.setBackgroundResource(C0446R.drawable.bg_voip_icon_btn);
        mutate = getResources().getDrawable(C0446R.drawable.ic_volume_up_white_24dp).mutate();
        checkableImageView.setAlpha(204);
        checkableImageView.setImageDrawable(mutate);
        checkableImageView.setScaleType(ScaleType.CENTER);
        frameLayout = new FrameLayout(r6);
        r6.spkToggle = checkableImageView;
        frameLayout.addView(checkableImageView, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        anonymousClass11.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f));
        r6.bottomButtons = anonymousClass11;
        anonymousClass11 = new LinearLayout(r6);
        anonymousClass11.setOrientation(0);
        checkableImageView = new CallSwipeView(r6);
        checkableImageView.setColor(-12207027);
        r6.acceptSwipe = checkableImageView;
        anonymousClass11.addView(checkableImageView, LayoutHelper.createLinear(-1, 70, 1.0f, 4, 4, -35, 4));
        frameLayout = new CallSwipeView(r6);
        frameLayout.setColor(-1696188);
        r6.declineSwipe = frameLayout;
        anonymousClass11.addView(frameLayout, LayoutHelper.createLinear(-1, 70, 1.0f, -35, 4, 4, 4));
        r6.swipeViewsWrap = anonymousClass11;
        anonymousClass10.addView(anonymousClass11, LayoutHelper.createFrame(-1, -2.0f, 80, 20.0f, 0.0f, 20.0f, 68.0f));
        anonymousClass11 = new ImageView(r6);
        Drawable fabBackgroundDrawable = new FabBackgroundDrawable();
        fabBackgroundDrawable.setColor(-12207027);
        anonymousClass11.setBackgroundDrawable(fabBackgroundDrawable);
        anonymousClass11.setImageResource(C0446R.drawable.ic_call_end_white_36dp);
        anonymousClass11.setScaleType(ScaleType.MATRIX);
        Matrix matrix = new Matrix();
        matrix.setTranslate((float) AndroidUtilities.dp(17.0f), (float) AndroidUtilities.dp(17.0f));
        matrix.postRotate(-135.0f, (float) AndroidUtilities.dp(35.0f), (float) AndroidUtilities.dp(35.0f));
        anonymousClass11.setImageMatrix(matrix);
        r6.acceptBtn = anonymousClass11;
        anonymousClass10.addView(anonymousClass11, LayoutHelper.createFrame(78, 78.0f, 83, 20.0f, 0.0f, 0.0f, 68.0f));
        View imageView = new ImageView(r6);
        Drawable fabBackgroundDrawable2 = new FabBackgroundDrawable();
        fabBackgroundDrawable2.setColor(-1696188);
        imageView.setBackgroundDrawable(fabBackgroundDrawable2);
        imageView.setImageResource(C0446R.drawable.ic_call_end_white_36dp);
        imageView.setScaleType(ScaleType.CENTER);
        r6.declineBtn = imageView;
        anonymousClass10.addView(imageView, LayoutHelper.createFrame(78, 78.0f, 85, 0.0f, 0.0f, 20.0f, 68.0f));
        checkableImageView.setViewToDrag(anonymousClass11, false);
        frameLayout.setViewToDrag(imageView, true);
        anonymousClass11 = new FrameLayout(r6);
        Drawable fabBackgroundDrawable3 = new FabBackgroundDrawable();
        fabBackgroundDrawable3.setColor(-1696188);
        r6.endBtnBg = fabBackgroundDrawable3;
        anonymousClass11.setBackgroundDrawable(fabBackgroundDrawable3);
        checkableImageView = new ImageView(r6);
        checkableImageView.setImageResource(C0446R.drawable.ic_call_end_white_36dp);
        checkableImageView.setScaleType(ScaleType.CENTER);
        r6.endBtnIcon = checkableImageView;
        anonymousClass11.addView(checkableImageView, LayoutHelper.createFrame(70, 70.0f));
        anonymousClass11.setForeground(getResources().getDrawable(C0446R.drawable.fab_highlight_dark));
        r6.endBtn = anonymousClass11;
        anonymousClass10.addView(anonymousClass11, LayoutHelper.createFrame(78, 78.0f, 81, 0.0f, 0.0f, 0.0f, 68.0f));
        anonymousClass11 = new ImageView(r6);
        fabBackgroundDrawable3 = new FabBackgroundDrawable();
        fabBackgroundDrawable3.setColor(-1);
        anonymousClass11.setBackgroundDrawable(fabBackgroundDrawable3);
        anonymousClass11.setImageResource(C0446R.drawable.edit_cancel);
        anonymousClass11.setColorFilter(-NUM);
        anonymousClass11.setScaleType(ScaleType.CENTER);
        anonymousClass11.setVisibility(8);
        r6.cancelBtn = anonymousClass11;
        anonymousClass10.addView(anonymousClass11, LayoutHelper.createFrame(78, 78.0f, 83, 52.0f, 0.0f, 0.0f, 68.0f));
        r6.emojiWrap = new LinearLayout(r6);
        r6.emojiWrap.setOrientation(0);
        r6.emojiWrap.setClipToPadding(false);
        r6.emojiWrap.setPivotX(0.0f);
        r6.emojiWrap.setPivotY(0.0f);
        r6.emojiWrap.setPadding(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(10.0f));
        int i = 0;
        while (i < 4) {
            checkableImageView = new ImageView(r6);
            checkableImageView.setScaleType(ScaleType.FIT_XY);
            r6.emojiWrap.addView(checkableImageView, LayoutHelper.createLinear(22, 22, i == 0 ? 0.0f : 4.0f, 0.0f, 0.0f, 0.0f));
            r6.keyEmojiViews[i] = checkableImageView;
            i++;
        }
        r6.emojiWrap.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (VoIPActivity.this.emojiTooltipVisible != null) {
                    VoIPActivity.this.setEmojiTooltipVisible(false);
                    if (VoIPActivity.this.tooltipHider != null) {
                        VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
                        VoIPActivity.this.tooltipHider = null;
                    }
                }
                VoIPActivity.this.setEmojiExpanded(VoIPActivity.this.emojiExpanded ^ 1);
            }
        });
        anonymousClass10.addView(r6.emojiWrap, LayoutHelper.createFrame(-2, -2, 48 | (LocaleController.isRTL ? 3 : 5)));
        r6.emojiWrap.setOnLongClickListener(new OnLongClickListener() {

            /* renamed from: org.telegram.ui.VoIPActivity$13$1 */
            class C17471 implements Runnable {
                C17471() {
                }

                public void run() {
                    VoIPActivity.this.tooltipHider = null;
                    VoIPActivity.this.setEmojiTooltipVisible(false);
                }
            }

            public boolean onLongClick(View view) {
                if (VoIPActivity.this.emojiExpanded != null) {
                    return null;
                }
                if (VoIPActivity.this.tooltipHider != null) {
                    VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
                    VoIPActivity.this.tooltipHider = null;
                }
                VoIPActivity.this.setEmojiTooltipVisible(VoIPActivity.this.emojiTooltipVisible ^ true);
                if (VoIPActivity.this.emojiTooltipVisible != null) {
                    VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.this.tooltipHider = new C17471(), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                }
                return true;
            }
        });
        r6.emojiExpandedText = new TextView(r6);
        r6.emojiExpandedText.setTextSize(1, 16.0f);
        r6.emojiExpandedText.setTextColor(-1);
        r6.emojiExpandedText.setGravity(17);
        r6.emojiExpandedText.setAlpha(0.0f);
        anonymousClass10.addView(r6.emojiExpandedText, LayoutHelper.createFrame(-1, -2.0f, 17, 10.0f, 32.0f, 10.0f, 0.0f));
        r6.hintTextView = new CorrectlyMeasuringTextView(r6);
        r6.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), -231525581));
        r6.hintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        r6.hintTextView.setTextSize(1, 14.0f);
        r6.hintTextView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        r6.hintTextView.setGravity(17);
        r6.hintTextView.setMaxWidth(AndroidUtilities.dp(300.0f));
        r6.hintTextView.setAlpha(0.0f);
        anonymousClass10.addView(r6.hintTextView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 42.0f, 10.0f, 0.0f));
        int alpha = r6.stateText.getPaint().getAlpha();
        r6.ellAnimator = new AnimatorSet();
        AnimatorSet animatorSet = r6.ellAnimator;
        Animator[] animatorArr = new Animator[6];
        int i2 = alpha;
        animatorArr[0] = createAlphaAnimator(r6.ellSpans[0], 0, i2, 0, 300);
        animatorArr[1] = createAlphaAnimator(r6.ellSpans[1], 0, i2, 150, 300);
        animatorArr[2] = createAlphaAnimator(r6.ellSpans[2], 0, i2, 300, 300);
        int i3 = alpha;
        animatorArr[3] = createAlphaAnimator(r6.ellSpans[0], i3, 0, 1000, 400);
        animatorArr[4] = createAlphaAnimator(r6.ellSpans[1], i3, 0, 1000, 400);
        animatorArr[5] = createAlphaAnimator(r6.ellSpans[2], i3, 0, 1000, 400);
        animatorSet.playTogether(animatorArr);
        r6.ellAnimator.addListener(new AnimatorListenerAdapter() {
            private Runnable restarter = new C17481();

            /* renamed from: org.telegram.ui.VoIPActivity$14$1 */
            class C17481 implements Runnable {
                C17481() {
                }

                public void run() {
                    if (!VoIPActivity.this.isFinishing()) {
                        VoIPActivity.this.ellAnimator.start();
                    }
                }
            }

            public void onAnimationEnd(Animator animator) {
                if (VoIPActivity.this.isFinishing() == null) {
                    VoIPActivity.this.content.postDelayed(this.restarter, 300);
                }
            }
        });
        anonymousClass10.setClipChildren(false);
        r6.content = anonymousClass10;
        return anonymousClass10;
    }

    @SuppressLint({"ObjectAnimatorBinding"})
    private ObjectAnimator createAlphaAnimator(Object obj, int i, int i2, int i3, int i4) {
        obj = ObjectAnimator.ofInt(obj, "alpha", new int[]{i, i2});
        obj.setDuration((long) i4);
        obj.setStartDelay((long) i3);
        obj.setInterpolator(CubicBezierInterpolator.DEFAULT);
        return obj;
    }

    protected void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeInCallActivity);
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
        }
        super.onDestroy();
    }

    public void onBackPressed() {
        if (this.emojiExpanded) {
            setEmojiExpanded(false);
            return;
        }
        if (!this.isIncomingWaiting) {
            super.onBackPressed();
        }
    }

    protected void onResume() {
        super.onResume();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().onUIForegroundStateChanged(true);
        }
    }

    protected void onPause() {
        super.onPause();
        if (this.retrying) {
            finish();
        }
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().onUIForegroundStateChanged(false);
        }
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 101) {
            if (VoIPService.getSharedInstance() == 0) {
                finish();
            } else if (iArr.length > 0 && iArr[0] == 0) {
                VoIPService.getSharedInstance().acceptIncomingCall();
                callAccepted();
            } else if (shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO") == 0) {
                VoIPService.getSharedInstance().declineIncomingCall();
                VoIPHelper.permissionDenied(this, new Runnable() {
                    public void run() {
                        VoIPActivity.this.finish();
                    }
                });
            } else {
                this.acceptSwipe.reset();
            }
        }
    }

    private void updateKeyView() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r7 = this;
        r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance();
        if (r0 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r0 = new org.telegram.ui.Components.IdenticonDrawable;
        r0.<init>();
        r1 = 4;
        r2 = new int[r1];
        r2 = {16777215, -1, -NUM, 872415231};
        r0.setColors(r2);
        r0 = new org.telegram.tgnet.TLRPC$TL_encryptedChat;
        r0.<init>();
        r2 = new java.io.ByteArrayOutputStream;	 Catch:{ Exception -> 0x003b }
        r2.<init>();	 Catch:{ Exception -> 0x003b }
        r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance();	 Catch:{ Exception -> 0x003b }
        r3 = r3.getEncryptionKey();	 Catch:{ Exception -> 0x003b }
        r2.write(r3);	 Catch:{ Exception -> 0x003b }
        r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance();	 Catch:{ Exception -> 0x003b }
        r3 = r3.getGA();	 Catch:{ Exception -> 0x003b }
        r2.write(r3);	 Catch:{ Exception -> 0x003b }
        r2 = r2.toByteArray();	 Catch:{ Exception -> 0x003b }
        r0.auth_key = r2;	 Catch:{ Exception -> 0x003b }
    L_0x003b:
        r2 = r0.auth_key;
        r0 = r0.auth_key;
        r3 = 0;
        r0 = r0.length;
        r0 = org.telegram.messenger.Utilities.computeSHA256(r2, r3, r0);
        r0 = org.telegram.messenger.voip.EncryptionKeyEmojifier.emojifyForCall(r0);
        r2 = r3;
    L_0x004a:
        if (r2 >= r1) goto L_0x006b;
    L_0x004c:
        r4 = r0[r2];
        r4 = org.telegram.messenger.Emoji.getEmojiDrawable(r4);
        if (r4 == 0) goto L_0x0068;
    L_0x0054:
        r5 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4.setBounds(r3, r3, r6, r5);
        r5 = r7.keyEmojiViews;
        r5 = r5[r2];
        r5.setImageDrawable(r4);
    L_0x0068:
        r2 = r2 + 1;
        goto L_0x004a;
    L_0x006b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.VoIPActivity.updateKeyView():void");
    }

    private CharSequence getFormattedDebugString() {
        String debugString = VoIPService.getSharedInstance().getDebugString();
        CharSequence spannableString = new SpannableString(debugString);
        int i = 0;
        do {
            int i2 = i + 1;
            int indexOf = debugString.indexOf(10, i2);
            if (indexOf == -1) {
                indexOf = debugString.length();
            }
            String substring = debugString.substring(i, indexOf);
            if (substring.contains("IN_USE")) {
                spannableString.setSpan(new ForegroundColorSpan(-16711936), i, indexOf, 0);
            } else if (substring.contains(": ")) {
                spannableString.setSpan(new ForegroundColorSpan(-NUM), i, (substring.indexOf(58) + i) + 1, 0);
            }
            i = debugString.indexOf(10, i2);
        } while (i != -1);
        return spannableString;
    }

    private void showDebugAlert() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().forceRating();
            final View linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(1);
            linearLayout.setBackgroundColor(-872415232);
            int dp = AndroidUtilities.dp(16.0f);
            int i = dp * 2;
            linearLayout.setPadding(dp, i, dp, i);
            View textView = new TextView(this);
            textView.setTextColor(-1);
            textView.setTextSize(1, 15.0f);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setGravity(17);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("libtgvoip v");
            stringBuilder.append(VoIPController.getVersion());
            textView.setText(stringBuilder.toString());
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 16.0f));
            textView = new ScrollView(this);
            final View textView2 = new TextView(this);
            textView2.setTypeface(Typeface.MONOSPACE);
            textView2.setTextSize(1, 11.0f);
            textView2.setMaxWidth(AndroidUtilities.dp(350.0f));
            textView2.setTextColor(-1);
            textView2.setText(getFormattedDebugString());
            textView.addView(textView2);
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, 1.0f));
            textView = new TextView(this);
            textView.setBackgroundColor(-1);
            textView.setTextColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
            textView.setPadding(dp, dp, dp, dp);
            textView.setTextSize(1, 15.0f);
            textView.setText(LocaleController.getString("Close", C0446R.string.Close));
            linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
            final WindowManager windowManager = (WindowManager) getSystemService("window");
            windowManager.addView(linearLayout, new WindowManager.LayoutParams(-1, -1, 1000, 0, -3));
            textView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    windowManager.removeView(linearLayout);
                }
            });
            linearLayout.postDelayed(new Runnable() {
                public void run() {
                    if (!VoIPActivity.this.isFinishing()) {
                        if (VoIPService.getSharedInstance() != null) {
                            textView2.setText(VoIPActivity.this.getFormattedDebugString());
                            linearLayout.postDelayed(this, 500);
                        }
                    }
                }
            }, 500);
        }
    }

    private void startUpdatingCallDuration() {
        new Runnable() {
            public void run() {
                if (!VoIPActivity.this.isFinishing()) {
                    if (VoIPService.getSharedInstance() != null) {
                        if (VoIPActivity.this.callState == 3 || VoIPActivity.this.callState == 5) {
                            CharSequence format;
                            long callDuration = VoIPService.getSharedInstance().getCallDuration() / 1000;
                            TextView access$3000 = VoIPActivity.this.durationText;
                            if (callDuration > 3600) {
                                format = String.format("%d:%02d:%02d", new Object[]{Long.valueOf(callDuration / 3600), Long.valueOf((callDuration % 3600) / 60), Long.valueOf(callDuration % 60)});
                            } else {
                                format = String.format("%d:%02d", new Object[]{Long.valueOf(callDuration / 60), Long.valueOf(callDuration % 60)});
                            }
                            access$3000.setText(format);
                            VoIPActivity.this.durationText.postDelayed(this, 500);
                        }
                    }
                }
            }
        }.run();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (!this.isIncomingWaiting || (i != 25 && i != 24)) {
            return super.onKeyDown(i, keyEvent);
        }
        if (VoIPService.getSharedInstance() != 0) {
            VoIPService.getSharedInstance().stopRinging();
        } else {
            finish();
        }
        return true;
    }

    private void callAccepted() {
        this.endBtn.setVisibility(0);
        if (VoIPService.getSharedInstance().hasEarpiece()) {
            r0.spkToggle.setVisibility(0);
        } else {
            r0.spkToggle.setVisibility(8);
        }
        r0.bottomButtons.setVisibility(0);
        if (r0.didAcceptFromHere) {
            ObjectAnimator ofArgb;
            r0.acceptBtn.setVisibility(8);
            if (VERSION.SDK_INT >= 21) {
                ofArgb = ObjectAnimator.ofArgb(r0.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
            } else {
                ofArgb = ObjectAnimator.ofInt(r0.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
                ofArgb.setEvaluator(new ArgbEvaluator());
            }
            AnimatorSet animatorSet = new AnimatorSet();
            AnimatorSet animatorSet2 = new AnimatorSet();
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(r0.endBtnIcon, "rotation", new float[]{-135.0f, 0.0f}), ofArgb});
            animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet2.setDuration(500);
            AnimatorSet animatorSet3 = new AnimatorSet();
            r4 = new Animator[3];
            r4[1] = ObjectAnimator.ofFloat(r0.declineBtn, "alpha", new float[]{0.0f});
            r4[2] = ObjectAnimator.ofFloat(r0.accountNameText, "alpha", new float[]{0.0f});
            animatorSet3.playTogether(r4);
            animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_IN);
            animatorSet3.setDuration(125);
            animatorSet.playTogether(new Animator[]{animatorSet2, animatorSet3});
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    VoIPActivity.this.swipeViewsWrap.setVisibility(8);
                    VoIPActivity.this.declineBtn.setVisibility(8);
                    VoIPActivity.this.accountNameText.setVisibility(8);
                }
            });
            animatorSet.start();
            return;
        }
        animatorSet3 = new AnimatorSet();
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(r0.bottomButtons, "alpha", new float[]{0.0f, 1.0f})});
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.setDuration(500);
        AnimatorSet animatorSet4 = new AnimatorSet();
        r5 = new Animator[4];
        r5[1] = ObjectAnimator.ofFloat(r0.declineBtn, "alpha", new float[]{0.0f});
        r5[2] = ObjectAnimator.ofFloat(r0.acceptBtn, "alpha", new float[]{0.0f});
        r5[3] = ObjectAnimator.ofFloat(r0.accountNameText, "alpha", new float[]{0.0f});
        animatorSet4.playTogether(r5);
        animatorSet4.setInterpolator(CubicBezierInterpolator.EASE_IN);
        animatorSet4.setDuration(125);
        animatorSet3.playTogether(new Animator[]{animatorSet, animatorSet4});
        animatorSet3.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.swipeViewsWrap.setVisibility(8);
                VoIPActivity.this.declineBtn.setVisibility(8);
                VoIPActivity.this.acceptBtn.setVisibility(8);
                VoIPActivity.this.accountNameText.setVisibility(8);
            }
        });
        animatorSet3.start();
    }

    private void showRetry() {
        ObjectAnimator ofArgb;
        if (this.retryAnim != null) {
            this.retryAnim.cancel();
        }
        this.endBtn.setEnabled(false);
        this.retrying = true;
        this.cancelBtn.setVisibility(0);
        this.cancelBtn.setAlpha(0.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        if (VERSION.SDK_INT >= 21) {
            ofArgb = ObjectAnimator.ofArgb(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-1696188, -12207027});
        } else {
            ofArgb = ObjectAnimator.ofInt(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-1696188, -12207027});
            ofArgb.setEvaluator(new ArgbEvaluator());
        }
        r5 = new Animator[4];
        r5[1] = ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[]{0.0f, (float) (((this.content.getWidth() / 2) - AndroidUtilities.dp(52.0f)) - (this.endBtn.getWidth() / 2))});
        r5[2] = ofArgb;
        r5[3] = ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[]{0.0f, -135.0f});
        animatorSet.playTogether(r5);
        animatorSet.setStartDelay(200);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.retryAnim = null;
                VoIPActivity.this.endBtn.setEnabled(true);
            }
        });
        this.retryAnim = animatorSet;
        animatorSet.start();
    }

    private void hideRetry() {
        if (this.retryAnim != null) {
            this.retryAnim.cancel();
        }
        this.retrying = false;
        ObjectAnimator ofArgb;
        if (VERSION.SDK_INT >= 21) {
            ofArgb = ObjectAnimator.ofArgb(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
        } else {
            ofArgb = ObjectAnimator.ofInt(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
            ofArgb.setEvaluator(new ArgbEvaluator());
        }
        AnimatorSet animatorSet = new AnimatorSet();
        r4 = new Animator[4];
        r4[2] = ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[]{0.0f});
        r4[3] = ObjectAnimator.ofFloat(this.cancelBtn, "alpha", new float[]{0.0f});
        animatorSet.playTogether(r4);
        animatorSet.setStartDelay(200);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.cancelBtn.setVisibility(8);
                VoIPActivity.this.endBtn.setEnabled(true);
                VoIPActivity.this.retryAnim = null;
            }
        });
        this.retryAnim = animatorSet;
        animatorSet.start();
    }

    public void onStateChanged(final int i) {
        final int i2 = this.callState;
        this.callState = i;
        runOnUiThread(new Runnable() {

            /* renamed from: org.telegram.ui.VoIPActivity$23$1 */
            class C17491 implements Runnable {
                C17491() {
                }

                public void run() {
                    VoIPActivity.this.finish();
                }
            }

            /* renamed from: org.telegram.ui.VoIPActivity$23$2 */
            class C17502 implements Runnable {
                C17502() {
                }

                public void run() {
                    VoIPActivity.this.tooltipHider = null;
                    VoIPActivity.this.setEmojiTooltipVisible(false);
                }
            }

            /* renamed from: org.telegram.ui.VoIPActivity$23$3 */
            class C17513 implements Runnable {
                C17513() {
                }

                public void run() {
                    VoIPActivity.this.finish();
                }
            }

            public void run() {
                boolean access$3500 = VoIPActivity.this.firstStateChange;
                if (VoIPActivity.this.firstStateChange) {
                    VoIPActivity.this.spkToggle.setChecked(((AudioManager) VoIPActivity.this.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).isSpeakerphoneOn());
                    if (VoIPActivity.this.isIncomingWaiting = i == 15) {
                        VoIPActivity.this.swipeViewsWrap.setVisibility(0);
                        VoIPActivity.this.endBtn.setVisibility(8);
                        VoIPActivity.this.acceptSwipe.startAnimatingArrows();
                        VoIPActivity.this.declineSwipe.startAnimatingArrows();
                        if (UserConfig.getActivatedAccountsCount() > 1) {
                            User currentUser = UserConfig.getInstance(VoIPActivity.this.currentAccount).getCurrentUser();
                            VoIPActivity.this.accountNameText.setText(LocaleController.formatString("VoipAnsweringAsAccount", C0446R.string.VoipAnsweringAsAccount, ContactsController.formatName(currentUser.first_name, currentUser.last_name)));
                        } else {
                            VoIPActivity.this.accountNameText.setVisibility(8);
                        }
                        VoIPActivity.this.getWindow().addFlags(2097152);
                    } else {
                        VoIPActivity.this.swipeViewsWrap.setVisibility(8);
                        VoIPActivity.this.acceptBtn.setVisibility(8);
                        VoIPActivity.this.declineBtn.setVisibility(8);
                        VoIPActivity.this.accountNameText.setVisibility(8);
                        VoIPActivity.this.getWindow().clearFlags(2097152);
                    }
                    if (i != 3) {
                        VoIPActivity.this.emojiWrap.setVisibility(8);
                    }
                    VoIPActivity.this.firstStateChange = false;
                }
                if (!(!VoIPActivity.this.isIncomingWaiting || i == 15 || i == 11 || i == 10)) {
                    VoIPActivity.this.isIncomingWaiting = false;
                    if (!VoIPActivity.this.didAcceptFromHere) {
                        VoIPActivity.this.callAccepted();
                    }
                }
                if (i == 15) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipIncoming", C0446R.string.VoipIncoming), false);
                    VoIPActivity.this.getWindow().addFlags(2097152);
                } else {
                    if (i != 1) {
                        if (i != 2) {
                            if (i == 12) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipExchangingKeys", C0446R.string.VoipExchangingKeys), true);
                            } else if (i == 13) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipWaiting", C0446R.string.VoipWaiting), true);
                            } else if (i == 16) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRinging", C0446R.string.VoipRinging), true);
                            } else if (i == 14) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRequesting", C0446R.string.VoipRequesting), true);
                            } else if (i == 10) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipHangingUp", C0446R.string.VoipHangingUp), true);
                                VoIPActivity.this.endBtnIcon.setAlpha(0.5f);
                                VoIPActivity.this.endBtn.setEnabled(false);
                            } else if (i == 11) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipCallEnded", C0446R.string.VoipCallEnded), false);
                                VoIPActivity.this.stateText.postDelayed(new C17491(), 200);
                            } else if (i == 17) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipBusy", C0446R.string.VoipBusy), false);
                                VoIPActivity.this.showRetry();
                            } else {
                                int lastError;
                                if (i != 3) {
                                    if (i != 5) {
                                        if (i == 4) {
                                            VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipFailed", C0446R.string.VoipFailed), false);
                                            lastError = VoIPService.getSharedInstance() != null ? VoIPService.getSharedInstance().getLastError() : 0;
                                            if (lastError == 1) {
                                                VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerIncompatible", C0446R.string.VoipPeerIncompatible, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                                            } else if (lastError == -1) {
                                                VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerOutdated", C0446R.string.VoipPeerOutdated, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                                            } else if (lastError == -2) {
                                                VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", C0446R.string.CallNotAvailable, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                                            } else if (lastError == 3) {
                                                VoIPActivity.this.showErrorDialog("Error initializing audio hardware");
                                            } else if (lastError == -3) {
                                                VoIPActivity.this.finish();
                                            } else if (lastError == -5) {
                                                VoIPActivity.this.showErrorDialog(LocaleController.getString("VoipErrorUnknown", C0446R.string.VoipErrorUnknown));
                                            } else {
                                                VoIPActivity.this.stateText.postDelayed(new C17513(), 1000);
                                            }
                                        }
                                    }
                                }
                                if (!access$3500 && i == 3) {
                                    lastError = MessagesController.getGlobalMainSettings().getInt("call_emoji_tooltip_count", 0);
                                    if (lastError < 3) {
                                        VoIPActivity.this.setEmojiTooltipVisible(true);
                                        VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.this.tooltipHider = new C17502(), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                                        MessagesController.getGlobalMainSettings().edit().putInt("call_emoji_tooltip_count", lastError + 1).commit();
                                    }
                                }
                                if (!(i2 == 3 || i2 == 5)) {
                                    VoIPActivity.this.setStateTextAnimated("0:00", false);
                                    VoIPActivity.this.startUpdatingCallDuration();
                                    VoIPActivity.this.updateKeyView();
                                    if (VoIPActivity.this.emojiWrap.getVisibility() != 0) {
                                        VoIPActivity.this.emojiWrap.setVisibility(0);
                                        VoIPActivity.this.emojiWrap.setAlpha(0.0f);
                                        VoIPActivity.this.emojiWrap.animate().alpha(1.0f).setDuration(200).setInterpolator(new DecelerateInterpolator()).start();
                                    }
                                }
                            }
                        }
                    }
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipConnecting", C0446R.string.VoipConnecting), true);
                }
                VoIPActivity.this.brandingText.invalidate();
            }
        });
    }

    public void onSignalBarsCountChanged(final int i) {
        runOnUiThread(new Runnable() {
            public void run() {
                VoIPActivity.this.signalBarsCount = i;
                VoIPActivity.this.brandingText.invalidate();
            }
        });
    }

    private void showErrorDialog(CharSequence charSequence) {
        charSequence = new Builder(this).setTitle(LocaleController.getString("VoipFailed", C0446R.string.VoipFailed)).setMessage(charSequence).setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null).show();
        charSequence.setCanceledOnTouchOutside(true);
        charSequence.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                VoIPActivity.this.finish();
            }
        });
    }

    public void onAudioSettingsChanged() {
        VoIPBaseService sharedInstance = VoIPBaseService.getSharedInstance();
        if (sharedInstance != null) {
            this.micToggle.setChecked(sharedInstance.isMicMute());
            if (sharedInstance.hasEarpiece() || sharedInstance.isBluetoothHeadsetConnected()) {
                this.spkToggle.setVisibility(0);
                if (!sharedInstance.hasEarpiece()) {
                    this.spkToggle.setImageResource(C0446R.drawable.ic_bluetooth_white_24dp);
                    this.spkToggle.setChecked(sharedInstance.isSpeakerphoneOn());
                } else if (sharedInstance.isBluetoothHeadsetConnected()) {
                    switch (sharedInstance.getCurrentAudioRoute()) {
                        case 0:
                            this.spkToggle.setImageResource(C0446R.drawable.ic_phone_in_talk_white_24dp);
                            break;
                        case 1:
                            this.spkToggle.setImageResource(C0446R.drawable.ic_volume_up_white_24dp);
                            break;
                        case 2:
                            this.spkToggle.setImageResource(C0446R.drawable.ic_bluetooth_white_24dp);
                            break;
                        default:
                            break;
                    }
                    this.spkToggle.setChecked(false);
                } else {
                    this.spkToggle.setImageResource(C0446R.drawable.ic_volume_up_white_24dp);
                    this.spkToggle.setChecked(sharedInstance.isSpeakerphoneOn());
                }
            } else {
                this.spkToggle.setVisibility(4);
            }
        }
    }

    private void setStateTextAnimated(String str, boolean z) {
        if (!str.equals(this.lastStateText)) {
            this.lastStateText = str;
            if (this.textChangingAnim != null) {
                this.textChangingAnim.cancel();
            }
            if (z) {
                if (!this.ellAnimator.isRunning()) {
                    this.ellAnimator.start();
                }
                z = new SpannableStringBuilder(str.toUpperCase());
                for (TextAlphaSpan alpha : this.ellSpans) {
                    alpha.setAlpha(0);
                }
                str = new SpannableString("...");
                str.setSpan(this.ellSpans[0], 0, 1, 0);
                str.setSpan(this.ellSpans[1], 1, 2, 0);
                str.setSpan(this.ellSpans[2], 2, 3, 0);
                z.append(str);
            } else {
                if (this.ellAnimator.isRunning()) {
                    this.ellAnimator.cancel();
                }
                z = str.toUpperCase();
            }
            this.stateText2.setText(z);
            this.stateText2.setVisibility(0);
            this.stateText.setPivotX(LocaleController.isRTL ? (float) this.stateText.getWidth() : false);
            this.stateText.setPivotY((float) (this.stateText.getHeight() / true));
            this.stateText2.setPivotX(LocaleController.isRTL ? (float) this.stateText.getWidth() : false);
            this.stateText2.setPivotY((float) (this.stateText.getHeight() / true));
            this.durationText = this.stateText2;
            str = new AnimatorSet();
            z = new Animator[true];
            z[1] = ObjectAnimator.ofFloat(this.stateText2, "translationY", new float[]{(float) (this.stateText.getHeight() / 2), 0.0f});
            z[2] = ObjectAnimator.ofFloat(this.stateText2, "scaleX", new float[]{0.7f, 1.0f});
            z[3] = ObjectAnimator.ofFloat(this.stateText2, "scaleY", new float[]{0.7f, 1.0f});
            z[4] = ObjectAnimator.ofFloat(this.stateText, "alpha", new float[]{1.0f, 0.0f});
            z[5] = ObjectAnimator.ofFloat(this.stateText, "translationY", new float[]{0.0f, (float) ((-this.stateText.getHeight()) / 2)});
            z[6] = ObjectAnimator.ofFloat(this.stateText, "scaleX", new float[]{1.0f, 0.7f});
            z[7] = ObjectAnimator.ofFloat(this.stateText, "scaleY", new float[]{1.0f, 0.7f});
            str.playTogether(z);
            str.setDuration(200);
            str.setInterpolator(CubicBezierInterpolator.DEFAULT);
            str.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    VoIPActivity.this.textChangingAnim = null;
                    VoIPActivity.this.stateText2.setVisibility(8);
                    VoIPActivity.this.durationText = VoIPActivity.this.stateText;
                    VoIPActivity.this.stateText.setTranslationY(0.0f);
                    VoIPActivity.this.stateText.setScaleX(1.0f);
                    VoIPActivity.this.stateText.setScaleY(1.0f);
                    VoIPActivity.this.stateText.setAlpha(1.0f);
                    VoIPActivity.this.stateText.setText(VoIPActivity.this.stateText2.getText());
                }
            });
            this.textChangingAnim = str;
            str.start();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiDidLoaded) {
            for (ImageView invalidate : this.keyEmojiViews) {
                invalidate.invalidate();
            }
        }
        if (i == NotificationCenter.closeInCallActivity) {
            finish();
        }
    }

    private void setEmojiTooltipVisible(boolean z) {
        this.emojiTooltipVisible = z;
        if (this.tooltipAnim != null) {
            this.tooltipAnim.cancel();
        }
        this.hintTextView.setVisibility(0);
        TextView textView = this.hintTextView;
        String str = "alpha";
        float[] fArr = new float[1];
        fArr[0] = z ? true : false;
        z = ObjectAnimator.ofFloat(textView, str, fArr);
        z.setDuration(300);
        z.setInterpolator(CubicBezierInterpolator.DEFAULT);
        z.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.tooltipAnim = null;
            }
        });
        this.tooltipAnim = z;
        z.start();
    }

    private void setEmojiExpanded(boolean z) {
        boolean z2 = z;
        if (this.emojiExpanded != z2) {
            r0.emojiExpanded = z2;
            if (r0.emojiAnimator != null) {
                r0.emojiAnimator.cancel();
            }
            AnimatorSet animatorSet;
            if (z2) {
                int[] iArr = new int[]{0, 0};
                int[] iArr2 = new int[]{0, 0};
                r0.emojiWrap.getLocationInWindow(iArr);
                r0.emojiExpandedText.getLocationInWindow(iArr2);
                Rect rect = new Rect();
                getWindow().getDecorView().getGlobalVisibleRect(rect);
                int height = ((iArr2[1] - (iArr[1] + r0.emojiWrap.getHeight())) - AndroidUtilities.dp(32.0f)) - r0.emojiWrap.getHeight();
                int width = ((rect.width() / 2) - (Math.round(((float) r0.emojiWrap.getWidth()) * 2.5f) / 2)) - iArr[0];
                animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[7];
                animatorArr[0] = ObjectAnimator.ofFloat(r0.emojiWrap, "translationY", new float[]{(float) height});
                animatorArr[1] = ObjectAnimator.ofFloat(r0.emojiWrap, "translationX", new float[]{(float) width});
                animatorArr[2] = ObjectAnimator.ofFloat(r0.emojiWrap, "scaleX", new float[]{2.5f});
                animatorArr[3] = ObjectAnimator.ofFloat(r0.emojiWrap, "scaleY", new float[]{2.5f});
                animatorArr[4] = ObjectAnimator.ofFloat(r0.blurOverlayView1, "alpha", new float[]{r0.blurOverlayView1.getAlpha(), 1.0f, 1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(r0.blurOverlayView2, "alpha", new float[]{r0.blurOverlayView2.getAlpha(), r0.blurOverlayView2.getAlpha(), 1.0f});
                animatorArr[6] = ObjectAnimator.ofFloat(r0.emojiExpandedText, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(300);
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                r0.emojiAnimator = animatorSet;
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        VoIPActivity.this.emojiAnimator = null;
                    }
                });
                animatorSet.start();
            } else {
                animatorSet = new AnimatorSet();
                Animator[] animatorArr2 = new Animator[7];
                animatorArr2[0] = ObjectAnimator.ofFloat(r0.emojiWrap, "translationX", new float[]{0.0f});
                animatorArr2[1] = ObjectAnimator.ofFloat(r0.emojiWrap, "translationY", new float[]{0.0f});
                animatorArr2[2] = ObjectAnimator.ofFloat(r0.emojiWrap, "scaleX", new float[]{1.0f});
                animatorArr2[3] = ObjectAnimator.ofFloat(r0.emojiWrap, "scaleY", new float[]{1.0f});
                animatorArr2[4] = ObjectAnimator.ofFloat(r0.blurOverlayView1, "alpha", new float[]{r0.blurOverlayView1.getAlpha(), r0.blurOverlayView1.getAlpha(), 0.0f});
                animatorArr2[5] = ObjectAnimator.ofFloat(r0.blurOverlayView2, "alpha", new float[]{r0.blurOverlayView2.getAlpha(), 0.0f, 0.0f});
                animatorArr2[6] = ObjectAnimator.ofFloat(r0.emojiExpandedText, "alpha", new float[]{0.0f});
                animatorSet.playTogether(animatorArr2);
                animatorSet.setDuration(300);
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                r0.emojiAnimator = animatorSet;
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        VoIPActivity.this.emojiAnimator = null;
                    }
                });
                animatorSet.start();
            }
        }
    }

    private void updateBlurredPhotos(final BitmapHolder bitmapHolder) {
        new Thread(new Runnable() {

            /* renamed from: org.telegram.ui.VoIPActivity$30$1 */
            class C17541 implements Runnable {
                C17541() {
                }

                public void run() {
                    VoIPActivity.this.blurOverlayView1.setImageBitmap(VoIPActivity.this.blurredPhoto1);
                    VoIPActivity.this.blurOverlayView2.setImageBitmap(VoIPActivity.this.blurredPhoto2);
                    bitmapHolder.release();
                }
            }

            public void run() {
                Bitmap createBitmap = Bitmap.createBitmap(150, 150, Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                canvas.drawBitmap(bitmapHolder.bitmap, null, new Rect(0, 0, 150, 150), new Paint(2));
                Utilities.blurBitmap(createBitmap, 3, 0, createBitmap.getWidth(), createBitmap.getHeight(), createBitmap.getRowBytes());
                Palette generate = Palette.from(bitmapHolder.bitmap).generate();
                Paint paint = new Paint();
                paint.setColor((generate.getDarkMutedColor(-11242343) & 16777215) | NUM);
                canvas.drawColor(637534208);
                canvas.drawRect(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight(), paint);
                Bitmap createBitmap2 = Bitmap.createBitmap(50, 50, Config.ARGB_8888);
                Canvas canvas2 = new Canvas(createBitmap2);
                canvas2.drawBitmap(bitmapHolder.bitmap, null, new Rect(0, 0, 50, 50), new Paint(2));
                Utilities.blurBitmap(createBitmap2, 3, 0, createBitmap2.getWidth(), createBitmap2.getHeight(), createBitmap2.getRowBytes());
                paint.setAlpha(102);
                canvas2.drawRect(0.0f, 0.0f, (float) canvas2.getWidth(), (float) canvas2.getHeight(), paint);
                VoIPActivity.this.blurredPhoto1 = createBitmap;
                VoIPActivity.this.blurredPhoto2 = createBitmap2;
                VoIPActivity.this.runOnUiThread(new C17541());
            }
        }).start();
    }

    private void sendTextMessage(final String str) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                SendMessagesHelper.getInstance(VoIPActivity.this.currentAccount).sendMessage(str, (long) VoIPActivity.this.user.id, null, null, false, null, null, null);
            }
        });
    }

    private void showMessagesSheet() {
        Context context = this;
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().stopRinging();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("mainconfig", 0);
        String[] strArr = new String[]{sharedPreferences.getString("quick_reply_msg1", LocaleController.getString("QuickReplyDefault1", C0446R.string.QuickReplyDefault1)), sharedPreferences.getString("quick_reply_msg2", LocaleController.getString("QuickReplyDefault2", C0446R.string.QuickReplyDefault2)), sharedPreferences.getString("quick_reply_msg3", LocaleController.getString("QuickReplyDefault3", C0446R.string.QuickReplyDefault3)), sharedPreferences.getString("quick_reply_msg4", LocaleController.getString("QuickReplyDefault4", C0446R.string.QuickReplyDefault4))};
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        final BottomSheet bottomSheet = new BottomSheet(context, true);
        if (VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(-13948117);
            bottomSheet.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialogInterface) {
                    VoIPActivity.this.getWindow().setNavigationBarColor(0);
                }
            });
        }
        OnClickListener anonymousClass33 = new OnClickListener() {
            public void onClick(final View view) {
                bottomSheet.dismiss();
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().declineIncomingCall(4, new Runnable() {
                        public void run() {
                            VoIPActivity.this.sendTextMessage((String) view.getTag());
                        }
                    });
                }
            }
        };
        for (CharSequence charSequence : strArr) {
            View bottomSheetCell = new BottomSheetCell(context, 0);
            bottomSheetCell.setTextAndIcon(charSequence, 0);
            bottomSheetCell.setTextColor(-1);
            bottomSheetCell.setTag(charSequence);
            bottomSheetCell.setOnClickListener(anonymousClass33);
            linearLayout.addView(bottomSheetCell);
        }
        View frameLayout = new FrameLayout(context);
        final View bottomSheetCell2 = new BottomSheetCell(context, 0);
        bottomSheetCell2.setTextAndIcon(LocaleController.getString("QuickReplyCustom", C0446R.string.QuickReplyCustom), 0);
        bottomSheetCell2.setTextColor(-1);
        frameLayout.addView(bottomSheetCell2);
        final View frameLayout2 = new FrameLayout(context);
        final View editText = new EditText(context);
        editText.setTextSize(1, 16.0f);
        editText.setTextColor(-1);
        editText.setHintTextColor(DarkTheme.getColor(Theme.key_chat_messagePanelHint));
        editText.setBackgroundDrawable(null);
        editText.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
        editText.setHint(LocaleController.getString("QuickReplyCustom", C0446R.string.QuickReplyCustom));
        editText.setMinHeight(AndroidUtilities.dp(48.0f));
        editText.setGravity(80);
        editText.setMaxLines(4);
        editText.setSingleLine(false);
        editText.setInputType((editText.getInputType() | MessagesController.UPDATE_MASK_CHAT_ADMINS) | 131072);
        frameLayout2.addView(editText, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 48.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 48.0f, 0.0f));
        final View imageView = new ImageView(context);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageDrawable(DarkTheme.getThemedDrawable(context, C0446R.drawable.ic_send, Theme.key_chat_messagePanelSend));
        if (LocaleController.isRTL) {
            imageView.setScaleX(-0.1f);
        } else {
            imageView.setScaleX(0.1f);
        }
        imageView.setScaleY(0.1f);
        imageView.setAlpha(0.0f);
        frameLayout2.addView(imageView, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : 5) | 80));
        imageView.setOnClickListener(new OnClickListener() {

            /* renamed from: org.telegram.ui.VoIPActivity$34$1 */
            class C17561 implements Runnable {
                C17561() {
                }

                public void run() {
                    VoIPActivity.this.sendTextMessage(editText.getText().toString());
                }
            }

            public void onClick(View view) {
                if (editText.length() != null) {
                    bottomSheet.dismiss();
                    if (VoIPService.getSharedInstance() != null) {
                        VoIPService.getSharedInstance().declineIncomingCall(4, new C17561());
                    }
                }
            }
        });
        imageView.setVisibility(4);
        final View imageView2 = new ImageView(context);
        imageView2.setScaleType(ScaleType.CENTER);
        imageView2.setImageDrawable(DarkTheme.getThemedDrawable(context, C0446R.drawable.edit_cancel, Theme.key_chat_messagePanelIcons));
        frameLayout2.addView(imageView2, LayoutHelper.createFrame(48, 48, 80 | (LocaleController.isRTL ? 3 : 5)));
        imageView2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                frameLayout2.setVisibility(8);
                bottomSheetCell2.setVisibility(0);
                editText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                ((InputMethodManager) VoIPActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            boolean prevState = null;

            /* renamed from: org.telegram.ui.VoIPActivity$36$1 */
            class C17571 implements Runnable {
                C17571() {
                }

                public void run() {
                    imageView2.setVisibility(4);
                }
            }

            /* renamed from: org.telegram.ui.VoIPActivity$36$2 */
            class C17582 implements Runnable {
                C17582() {
                }

                public void run() {
                    imageView.setVisibility(4);
                }
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                editable = editable.length() > null ? true : null;
                if (this.prevState != editable) {
                    this.prevState = editable;
                    if (editable != null) {
                        imageView.setVisibility(0);
                        imageView.animate().alpha(1.0f).scaleX(LocaleController.isRTL ? -1.0f : 1.0f).scaleY(1.0f).setDuration(200).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                        imageView2.animate().alpha(0.0f).scaleX(0.1f).scaleY(0.1f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(200).withEndAction(new C17571()).start();
                        return;
                    }
                    imageView2.setVisibility(0);
                    imageView2.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    imageView.animate().alpha(0.0f).scaleX(LocaleController.isRTL ? -0.1f : 0.1f).scaleY(0.1f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(200).withEndAction(new C17582()).start();
                }
            }
        });
        frameLayout2.setVisibility(8);
        frameLayout.addView(frameLayout2);
        bottomSheetCell2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                frameLayout2.setVisibility(0);
                bottomSheetCell2.setVisibility(4);
                editText.requestFocus();
                ((InputMethodManager) VoIPActivity.this.getSystemService("input_method")).showSoftInput(editText, 0);
            }
        });
        linearLayout.addView(frameLayout);
        bottomSheet.setCustomView(linearLayout);
        bottomSheet.setBackgroundColor(-13948117);
        bottomSheet.show();
    }
}
