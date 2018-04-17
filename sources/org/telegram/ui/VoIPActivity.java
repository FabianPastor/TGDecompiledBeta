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
import java.io.ByteArrayOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.voip.EncryptionKeyEmojifier;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPBaseService.StateListener;
import org.telegram.messenger.voip.VoIPController;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.DarkAlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CorrectlyMeasuringTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.IdenticonDrawable;
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
        private int tapCount = 0;

        C17522() {
        }

        public void onClick(View v) {
            if (!BuildVars.DEBUG_VERSION) {
                if (this.tapCount != 9) {
                    this.tapCount++;
                    return;
                }
            }
            VoIPActivity.this.showDebugAlert();
            this.tapCount = 0;
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

        public void onClick(View v) {
            VoIPActivity.this.endBtn.setEnabled(false);
            if (VoIPActivity.this.retrying) {
                Intent intent = new Intent(VoIPActivity.this, VoIPService.class);
                intent.putExtra("user_id", VoIPActivity.this.user.id);
                intent.putExtra("is_outgoing", true);
                intent.putExtra("start_incall_activity", false);
                intent.putExtra("account", VoIPActivity.this.currentAccount);
                try {
                    VoIPActivity.this.startService(intent);
                } catch (Throwable e) {
                    FileLog.m3e(e);
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

        public void onClick(View v) {
            VoIPService svc = VoIPService.getSharedInstance();
            if (svc != null) {
                svc.toggleSpeakerphoneOrShowRouteSheet(VoIPActivity.this);
            }
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$5 */
    class C17615 implements OnClickListener {
        C17615() {
        }

        public void onClick(View v) {
            if (VoIPService.getSharedInstance() == null) {
                VoIPActivity.this.finish();
                return;
            }
            boolean checked = VoIPActivity.this.micToggle.isChecked() ^ 1;
            VoIPActivity.this.micToggle.setChecked(checked);
            VoIPService.getSharedInstance().setMicMute(checked);
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$6 */
    class C17626 implements OnClickListener {
        C17626() {
        }

        public void onClick(View v) {
            if (VoIPActivity.this.isIncomingWaiting) {
                VoIPActivity.this.showMessagesSheet();
                return;
            }
            Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("com.tmessages.openchat");
            stringBuilder.append(Math.random());
            stringBuilder.append(ConnectionsManager.DEFAULT_DATACENTER_ID);
            intent.setAction(stringBuilder.toString());
            intent.putExtra("currentAccount", VoIPActivity.this.currentAccount);
            intent.setFlags(32768);
            intent.putExtra("userId", VoIPActivity.this.user.id);
            VoIPActivity.this.startActivity(intent);
            VoIPActivity.this.finish();
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$9 */
    class C17679 implements OnClickListener {
        C17679() {
        }

        public void onClick(View v) {
            VoIPActivity.this.finish();
        }
    }

    private class SignalBarsDrawable extends Drawable {
        private int[] barHeights;
        private int offsetStart;
        private Paint paint;
        private RectF rect;

        private SignalBarsDrawable() {
            this.barHeights = new int[]{AndroidUtilities.dp(3.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(12.0f)};
            this.paint = new Paint(1);
            this.rect = new RectF();
            this.offsetStart = 6;
        }

        public void draw(Canvas canvas) {
            if (VoIPActivity.this.callState == 3 || VoIPActivity.this.callState == 5) {
                this.paint.setColor(-1);
                int x = getBounds().left + AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) this.offsetStart);
                int y = getBounds().top;
                for (int i = 0; i < 4; i++) {
                    this.paint.setAlpha(i + 1 <= VoIPActivity.this.signalBarsCount ? 242 : 102);
                    this.rect.set((float) (AndroidUtilities.dp((float) (4 * i)) + x), (float) ((getIntrinsicHeight() + y) - this.barHeights[i]), (float) (((AndroidUtilities.dp(4.0f) * i) + x) + AndroidUtilities.dp(3.0f)), (float) (getIntrinsicHeight() + y));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(0.3f), (float) AndroidUtilities.dp(0.3f), this.paint);
                }
            }
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp((float) (15 + this.offsetStart));
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(12.0f);
        }

        public int getOpacity() {
            return -3;
        }
    }

    private class TextAlphaSpan extends CharacterStyle {
        private int alpha = null;

        public int getAlpha() {
            return this.alpha;
        }

        public void setAlpha(int alpha) {
            this.alpha = alpha;
            VoIPActivity.this.stateText.invalidate();
            VoIPActivity.this.stateText2.invalidate();
        }

        public void updateDrawState(TextPaint tp) {
            tp.setAlpha(this.alpha);
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$1 */
    class C23101 implements ImageReceiverDelegate {
        C23101() {
        }

        public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb) {
            BitmapHolder bmp = imageReceiver.getBitmapSafe();
            if (bmp != null) {
                VoIPActivity.this.updateBlurredPhotos(bmp);
            }
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$7 */
    class C23117 implements Listener {

        /* renamed from: org.telegram.ui.VoIPActivity$7$1 */
        class C17631 extends AnimatorListenerAdapter {
            C17631() {
            }

            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.currentDeclineAnim = null;
            }
        }

        /* renamed from: org.telegram.ui.VoIPActivity$7$2 */
        class C17642 extends AnimatorListenerAdapter {
            C17642() {
            }

            public void onAnimationEnd(Animator animation) {
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
            AnimatorSet set = new AnimatorSet();
            r1 = new Animator[2];
            r1[0] = ObjectAnimator.ofFloat(VoIPActivity.this.declineSwipe, "alpha", new float[]{0.2f});
            r1[1] = ObjectAnimator.ofFloat(VoIPActivity.this.declineBtn, "alpha", new float[]{0.2f});
            set.playTogether(r1);
            set.setDuration(200);
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            set.addListener(new C17631());
            VoIPActivity.this.currentDeclineAnim = set;
            set.start();
            VoIPActivity.this.declineSwipe.stopAnimatingArrows();
        }

        public void onDragCancel() {
            if (VoIPActivity.this.currentDeclineAnim != null) {
                VoIPActivity.this.currentDeclineAnim.cancel();
            }
            AnimatorSet set = new AnimatorSet();
            r1 = new Animator[2];
            r1[0] = ObjectAnimator.ofFloat(VoIPActivity.this.declineSwipe, "alpha", new float[]{1.0f});
            r1[1] = ObjectAnimator.ofFloat(VoIPActivity.this.declineBtn, "alpha", new float[]{1.0f});
            set.playTogether(r1);
            set.setDuration(200);
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            set.addListener(new C17642());
            VoIPActivity.this.currentDeclineAnim = set;
            set.start();
            VoIPActivity.this.declineSwipe.startAnimatingArrows();
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$8 */
    class C23128 implements Listener {

        /* renamed from: org.telegram.ui.VoIPActivity$8$1 */
        class C17651 extends AnimatorListenerAdapter {
            C17651() {
            }

            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.currentAcceptAnim = null;
            }
        }

        /* renamed from: org.telegram.ui.VoIPActivity$8$2 */
        class C17662 extends AnimatorListenerAdapter {
            C17662() {
            }

            public void onAnimationEnd(Animator animation) {
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
            AnimatorSet set = new AnimatorSet();
            r1 = new Animator[2];
            r1[0] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, "alpha", new float[]{0.2f});
            r1[1] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, "alpha", new float[]{0.2f});
            set.playTogether(r1);
            set.setDuration(200);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new C17651());
            VoIPActivity.this.currentAcceptAnim = set;
            set.start();
            VoIPActivity.this.acceptSwipe.stopAnimatingArrows();
        }

        public void onDragCancel() {
            if (VoIPActivity.this.currentAcceptAnim != null) {
                VoIPActivity.this.currentAcceptAnim.cancel();
            }
            AnimatorSet set = new AnimatorSet();
            r1 = new Animator[2];
            r1[0] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, "alpha", new float[]{1.0f});
            r1[1] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, "alpha", new float[]{1.0f});
            set.playTogether(r1);
            set.setDuration(200);
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            set.addListener(new C17662());
            VoIPActivity.this.currentAcceptAnim = set;
            set.start();
            VoIPActivity.this.acceptSwipe.startAnimatingArrows();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        getWindow().addFlags(524288);
        super.onCreate(savedInstanceState);
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
        View createContentView = createContentView();
        View contentView = createContentView;
        setContentView(createContentView);
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
            contentView.setBackgroundDrawable(new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{-14994098, -14328963}));
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
        this.hintTextView.setText(LocaleController.formatString("CallEmojiKeyTooltip", R.string.CallEmojiKeyTooltip, this.user.first_name));
        this.emojiExpandedText.setText(LocaleController.formatString("CallEmojiKeyTooltip", R.string.CallEmojiKeyTooltip, this.user.first_name));
    }

    private View createContentView() {
        FrameLayout content = new FrameLayout(this) {
            private void setNegativeMargins(Rect insets, LayoutParams lp) {
                lp.topMargin = -insets.top;
                lp.bottomMargin = -insets.bottom;
                lp.leftMargin = -insets.left;
                lp.rightMargin = -insets.right;
            }

            protected boolean fitSystemWindows(Rect insets) {
                setNegativeMargins(insets, (LayoutParams) VoIPActivity.this.photoView.getLayoutParams());
                setNegativeMargins(insets, (LayoutParams) VoIPActivity.this.blurOverlayView1.getLayoutParams());
                setNegativeMargins(insets, (LayoutParams) VoIPActivity.this.blurOverlayView2.getLayoutParams());
                return super.fitSystemWindows(insets);
            }
        };
        content.setBackgroundColor(0);
        content.setFitsSystemWindows(true);
        content.setClipToPadding(false);
        BackupImageView photo = new BackupImageView(this) {
            private Drawable bottomGradient = getResources().getDrawable(R.drawable.gradient_bottom);
            private Paint paint = new Paint();
            private Drawable topGradient = getResources().getDrawable(R.drawable.gradient_top);

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
        this.photoView = photo;
        content.addView(photo);
        this.blurOverlayView1 = new ImageView(this);
        this.blurOverlayView1.setScaleType(ScaleType.CENTER_CROP);
        this.blurOverlayView1.setAlpha(0.0f);
        content.addView(this.blurOverlayView1);
        this.blurOverlayView2 = new ImageView(this);
        this.blurOverlayView2.setScaleType(ScaleType.CENTER_CROP);
        this.blurOverlayView2.setAlpha(0.0f);
        content.addView(this.blurOverlayView2);
        TextView branding = new TextView(this);
        branding.setTextColor(-855638017);
        branding.setText(LocaleController.getString("VoipInCallBranding", R.string.VoipInCallBranding));
        Drawable logo = getResources().getDrawable(R.drawable.notification).mutate();
        logo.setAlpha(204);
        logo.setBounds(0, 0, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f));
        this.signalBarsDrawable = new SignalBarsDrawable();
        this.signalBarsDrawable.setBounds(0, 0, this.signalBarsDrawable.getIntrinsicWidth(), this.signalBarsDrawable.getIntrinsicHeight());
        branding.setCompoundDrawables(LocaleController.isRTL ? r6.signalBarsDrawable : logo, null, LocaleController.isRTL ? logo : r6.signalBarsDrawable, null);
        branding.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        branding.setGravity(LocaleController.isRTL ? 5 : 3);
        branding.setCompoundDrawablePadding(AndroidUtilities.dp(5.0f));
        branding.setTextSize(1, 14.0f);
        content.addView(branding, LayoutHelper.createFrame(-2, -2.0f, 48 | (LocaleController.isRTL ? 5 : 3), 18.0f, 18.0f, 18.0f, 0.0f));
        r6.brandingText = branding;
        TextView name = new TextView(r6);
        name.setSingleLine();
        name.setTextColor(-1);
        name.setTextSize(1, 40.0f);
        name.setEllipsize(TruncateAt.END);
        name.setGravity(LocaleController.isRTL ? 5 : 3);
        name.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        name.setTypeface(Typeface.create("sans-serif-light", 0));
        r6.nameText = name;
        content.addView(name, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 43.0f, 18.0f, 0.0f));
        TextView state = new TextView(r6);
        state.setTextColor(-855638017);
        state.setSingleLine();
        state.setEllipsize(TruncateAt.END);
        state.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        state.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        state.setTextSize(1, 15.0f);
        state.setGravity(LocaleController.isRTL ? 5 : 3);
        r6.stateText = state;
        content.addView(state, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        r6.durationText = state;
        TextView state2 = new TextView(r6);
        state2.setTextColor(-855638017);
        state2.setSingleLine();
        state2.setEllipsize(TruncateAt.END);
        state2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        state2.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        state2.setTextSize(1, 15.0f);
        state2.setGravity(LocaleController.isRTL ? 5 : 3);
        state2.setVisibility(8);
        r6.stateText2 = state2;
        content.addView(state2, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        r6.ellSpans = new TextAlphaSpan[]{new TextAlphaSpan(), new TextAlphaSpan(), new TextAlphaSpan()};
        LinearLayout buttons = new LinearLayout(r6);
        buttons.setOrientation(0);
        content.addView(buttons, LayoutHelper.createFrame(-1, -2, 80));
        TextView accountName = new TextView(r6);
        accountName.setTextColor(-855638017);
        accountName.setSingleLine();
        accountName.setEllipsize(TruncateAt.END);
        accountName.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        accountName.setTextSize(1, 15.0f);
        accountName.setGravity(LocaleController.isRTL ? 5 : 3);
        r6.accountNameText = accountName;
        content.addView(accountName, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 120.0f, 18.0f, 0.0f));
        CheckableImageView mic = new CheckableImageView(r6);
        mic.setBackgroundResource(R.drawable.bg_voip_icon_btn);
        Drawable micIcon = getResources().getDrawable(R.drawable.ic_mic_off_white_24dp).mutate();
        mic.setAlpha(204);
        mic.setImageDrawable(micIcon);
        mic.setScaleType(ScaleType.CENTER);
        FrameLayout wrap = new FrameLayout(r6);
        r6.micToggle = mic;
        wrap.addView(mic, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        buttons.addView(wrap, LayoutHelper.createLinear(0, -2, 1.0f));
        ImageView chat = new ImageView(r6);
        Drawable chatIcon = getResources().getDrawable(R.drawable.ic_chat_bubble_white_24dp).mutate();
        chatIcon.setAlpha(204);
        chat.setImageDrawable(chatIcon);
        chat.setScaleType(ScaleType.CENTER);
        wrap = new FrameLayout(r6);
        r6.chatBtn = chat;
        wrap.addView(chat, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        Drawable chatIcon2 = chatIcon;
        buttons.addView(wrap, LayoutHelper.createLinear(0, -2, 1.0f));
        CheckableImageView speaker = new CheckableImageView(r6);
        speaker.setBackgroundResource(R.drawable.bg_voip_icon_btn);
        Drawable speakerIcon = getResources().getDrawable(R.drawable.ic_volume_up_white_24dp).mutate();
        speaker.setAlpha(204);
        speaker.setImageDrawable(speakerIcon);
        speaker.setScaleType(ScaleType.CENTER);
        FrameLayout wrap2 = new FrameLayout(r6);
        r6.spkToggle = speaker;
        wrap2.addView(speaker, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        CheckableImageView speaker2 = speaker;
        Drawable micIcon2 = micIcon;
        buttons.addView(wrap2, LayoutHelper.createLinear(0, -2, 1.0f));
        r6.bottomButtons = buttons;
        LinearLayout swipesWrap = new LinearLayout(r6);
        swipesWrap.setOrientation(0);
        CallSwipeView acceptSwipe = new CallSwipeView(r6);
        acceptSwipe.setColor(-12207027);
        r6.acceptSwipe = acceptSwipe;
        swipesWrap.addView(acceptSwipe, LayoutHelper.createLinear(-1, 70, 1.0f, 4, 4, -35, 4));
        CallSwipeView declineSwipe = new CallSwipeView(r6);
        FrameLayout wrap3 = wrap2;
        declineSwipe.setColor(-1696188);
        r6.declineSwipe = declineSwipe;
        swipesWrap.addView(declineSwipe, LayoutHelper.createLinear(-1, 70, 1.0f, -35, 4, 4, 4));
        r6.swipeViewsWrap = swipesWrap;
        content.addView(swipesWrap, LayoutHelper.createFrame(-1, -2.0f, 80, 20.0f, 0.0f, 20.0f, 68.0f));
        ImageView acceptBtn = new ImageView(r6);
        LinearLayout swipesWrap2 = swipesWrap;
        FabBackgroundDrawable acceptBtnBg = new FabBackgroundDrawable();
        Drawable speakerIcon2 = speakerIcon;
        acceptBtnBg.setColor(-12207027);
        acceptBtn.setBackgroundDrawable(acceptBtnBg);
        acceptBtn.setImageResource(R.drawable.ic_call_end_white_36dp);
        acceptBtn.setScaleType(ScaleType.MATRIX);
        Matrix matrix = new Matrix();
        FabBackgroundDrawable acceptBtnBg2 = acceptBtnBg;
        LinearLayout buttons2 = buttons;
        matrix.setTranslate((float) AndroidUtilities.dp(17.0f), (float) AndroidUtilities.dp(17.0f));
        matrix.postRotate(-135.0f, (float) AndroidUtilities.dp(35.0f), (float) AndroidUtilities.dp(35.0f));
        acceptBtn.setImageMatrix(matrix);
        r6.acceptBtn = acceptBtn;
        content.addView(acceptBtn, LayoutHelper.createFrame(78, 78.0f, 83, 20.0f, 0.0f, 0.0f, 68.0f));
        ImageView declineBtn = new ImageView(r6);
        FabBackgroundDrawable rejectBtnBg = new FabBackgroundDrawable();
        rejectBtnBg.setColor(-1696188);
        declineBtn.setBackgroundDrawable(rejectBtnBg);
        declineBtn.setImageResource(R.drawable.ic_call_end_white_36dp);
        declineBtn.setScaleType(ScaleType.CENTER);
        r6.declineBtn = declineBtn;
        content.addView(declineBtn, LayoutHelper.createFrame(78, 78.0f, 85, 0.0f, 0.0f, 20.0f, 68.0f));
        acceptSwipe.setViewToDrag(acceptBtn, false);
        declineSwipe.setViewToDrag(declineBtn, true);
        FrameLayout end = new FrameLayout(r6);
        CallSwipeView declineSwipe2 = declineSwipe;
        FabBackgroundDrawable endBtnBg = new FabBackgroundDrawable();
        ImageView acceptBtn2 = acceptBtn;
        endBtnBg.setColor(-1696188);
        r6.endBtnBg = endBtnBg;
        end.setBackgroundDrawable(endBtnBg);
        acceptBtn = new ImageView(r6);
        FabBackgroundDrawable endBtnBg2 = endBtnBg;
        acceptBtn.setImageResource(R.drawable.ic_call_end_white_36dp);
        acceptBtn.setScaleType(ScaleType.CENTER);
        r6.endBtnIcon = acceptBtn;
        CallSwipeView acceptSwipe2 = acceptSwipe;
        end.addView(acceptBtn, LayoutHelper.createFrame(70, 70.0f));
        end.setForeground(getResources().getDrawable(R.drawable.fab_highlight_dark));
        r6.endBtn = end;
        content.addView(end, LayoutHelper.createFrame(78, 78.0f, 81, 0.0f, 0.0f, 0.0f, 68.0f));
        ImageView cancelBtn = new ImageView(r6);
        endBtnBg = new FabBackgroundDrawable();
        ImageView endInner = acceptBtn;
        endBtnBg.setColor(-1);
        cancelBtn.setBackgroundDrawable(endBtnBg);
        cancelBtn.setImageResource(R.drawable.edit_cancel);
        cancelBtn.setColorFilter(-NUM);
        cancelBtn.setScaleType(ScaleType.CENTER);
        cancelBtn.setVisibility(8);
        r6.cancelBtn = cancelBtn;
        content.addView(cancelBtn, LayoutHelper.createFrame(78, 78.0f, 83, 52.0f, 0.0f, 0.0f, 68.0f));
        r6.emojiWrap = new LinearLayout(r6);
        FabBackgroundDrawable cancelBtnBg = endBtnBg;
        r6.emojiWrap.setOrientation(0);
        r6.emojiWrap.setClipToPadding(false);
        r6.emojiWrap.setPivotX(0.0f);
        r6.emojiWrap.setPivotY(0.0f);
        ImageView cancelBtn2 = cancelBtn;
        FrameLayout end2 = end;
        Matrix matrix2 = matrix;
        FabBackgroundDrawable rejectBtnBg2 = rejectBtnBg;
        r6.emojiWrap.setPadding(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(10.0f));
        int i = 0;
        while (i < 4) {
            acceptBtn = new ImageView(r6);
            acceptBtn.setScaleType(ScaleType.FIT_XY);
            r6.emojiWrap.addView(acceptBtn, LayoutHelper.createLinear(22, 22, i == 0 ? 0.0f : 4.0f, 0.0f, 0.0f, 0.0f));
            r6.keyEmojiViews[i] = acceptBtn;
            i++;
        }
        r6.emojiWrap.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (VoIPActivity.this.emojiTooltipVisible) {
                    VoIPActivity.this.setEmojiTooltipVisible(false);
                    if (VoIPActivity.this.tooltipHider != null) {
                        VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
                        VoIPActivity.this.tooltipHider = null;
                    }
                }
                VoIPActivity.this.setEmojiExpanded(VoIPActivity.this.emojiExpanded ^ 1);
            }
        });
        content.addView(r6.emojiWrap, LayoutHelper.createFrame(-2, -2, 48 | (LocaleController.isRTL ? 3 : 5)));
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

            public boolean onLongClick(View v) {
                if (VoIPActivity.this.emojiExpanded) {
                    return false;
                }
                if (VoIPActivity.this.tooltipHider != null) {
                    VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
                    VoIPActivity.this.tooltipHider = null;
                }
                VoIPActivity.this.setEmojiTooltipVisible(VoIPActivity.this.emojiTooltipVisible ^ true);
                if (VoIPActivity.this.emojiTooltipVisible) {
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
        content.addView(r6.emojiExpandedText, LayoutHelper.createFrame(-1, -2.0f, 17, 10.0f, 32.0f, 10.0f, 0.0f));
        r6.hintTextView = new CorrectlyMeasuringTextView(r6);
        r6.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), -231525581));
        r6.hintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        r6.hintTextView.setTextSize(1, 14.0f);
        r6.hintTextView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        r6.hintTextView.setGravity(17);
        r6.hintTextView.setMaxWidth(AndroidUtilities.dp(300.0f));
        r6.hintTextView.setAlpha(0.0f);
        content.addView(r6.hintTextView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 42.0f, 10.0f, 0.0f));
        int ellMaxAlpha = r6.stateText.getPaint().getAlpha();
        r6.ellAnimator = new AnimatorSet();
        AnimatorSet animatorSet = r6.ellAnimator;
        Animator[] animatorArr = new Animator[6];
        animatorArr[0] = createAlphaAnimator(r6.ellSpans[0], 0, ellMaxAlpha, 0, 300);
        int i2 = ellMaxAlpha;
        Animator[] animatorArr2 = animatorArr;
        animatorArr2[1] = createAlphaAnimator(r6.ellSpans[1], 0, i2, 150, 300);
        animatorArr2[2] = createAlphaAnimator(r6.ellSpans[2], 0, i2, 300, 300);
        int i3 = ellMaxAlpha;
        animatorArr2[3] = createAlphaAnimator(r6.ellSpans[0], i3, 0, 1000, 400);
        animatorArr2[4] = createAlphaAnimator(r6.ellSpans[1], i3, 0, 1000, 400);
        animatorArr2[5] = createAlphaAnimator(r6.ellSpans[2], i3, 0, 1000, 400);
        animatorSet.playTogether(animatorArr2);
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

            public void onAnimationEnd(Animator animation) {
                if (!VoIPActivity.this.isFinishing()) {
                    VoIPActivity.this.content.postDelayed(this.restarter, 300);
                }
            }
        });
        content.setClipChildren(false);
        r6.content = content;
        return content;
    }

    @SuppressLint({"ObjectAnimatorBinding"})
    private ObjectAnimator createAlphaAnimator(Object target, int startVal, int endVal, int startDelay, int duration) {
        ObjectAnimator a = ObjectAnimator.ofInt(target, "alpha", new int[]{startVal, endVal});
        a.setDuration((long) duration);
        a.setStartDelay((long) startDelay);
        a.setInterpolator(CubicBezierInterpolator.DEFAULT);
        return a;
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101) {
            if (VoIPService.getSharedInstance() == null) {
                finish();
            } else if (grantResults.length > 0 && grantResults[0] == 0) {
                VoIPService.getSharedInstance().acceptIncomingCall();
                callAccepted();
            } else if (shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
                this.acceptSwipe.reset();
            } else {
                VoIPService.getSharedInstance().declineIncomingCall();
                VoIPHelper.permissionDenied(this, new Runnable() {
                    public void run() {
                        VoIPActivity.this.finish();
                    }
                });
            }
        }
    }

    private void updateKeyView() {
        if (VoIPService.getSharedInstance() != null) {
            new IdenticonDrawable().setColors(new int[]{16777215, -1, -NUM, 872415231});
            EncryptedChat encryptedChat = new TL_encryptedChat();
            try {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                buf.write(VoIPService.getSharedInstance().getEncryptionKey());
                buf.write(VoIPService.getSharedInstance().getGA());
                encryptedChat.auth_key = buf.toByteArray();
            } catch (Exception e) {
            }
            String[] emoji = EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(encryptedChat.auth_key, 0, encryptedChat.auth_key.length));
            for (int i = 0; i < 4; i++) {
                Drawable drawable = Emoji.getEmojiDrawable(emoji[i]);
                if (drawable != null) {
                    drawable.setBounds(0, 0, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f));
                    this.keyEmojiViews[i].setImageDrawable(drawable);
                }
            }
        }
    }

    private CharSequence getFormattedDebugString() {
        String in = VoIPService.getSharedInstance().getDebugString();
        SpannableString ss = new SpannableString(in);
        int offset = 0;
        int indexOf;
        do {
            indexOf = in.indexOf(10, offset + 1);
            if (indexOf == -1) {
                indexOf = in.length();
            }
            String line = in.substring(offset, indexOf);
            if (line.contains("IN_USE")) {
                ss.setSpan(new ForegroundColorSpan(-16711936), offset, indexOf, 0);
            } else if (line.contains(": ")) {
                ss.setSpan(new ForegroundColorSpan(-NUM), offset, (line.indexOf(58) + offset) + 1, 0);
            }
            indexOf = in.indexOf(10, offset + 1);
            offset = indexOf;
        } while (indexOf != -1);
        return ss;
    }

    private void showDebugAlert() {
        Context context = this;
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().forceRating();
            final LinearLayout debugOverlay = new LinearLayout(context);
            debugOverlay.setOrientation(1);
            debugOverlay.setBackgroundColor(-872415232);
            int pad = AndroidUtilities.dp(NUM);
            debugOverlay.setPadding(pad, pad * 2, pad, pad * 2);
            TextView title = new TextView(context);
            title.setTextColor(-1);
            title.setTextSize(1, 15.0f);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setGravity(17);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("libtgvoip v");
            stringBuilder.append(VoIPController.getVersion());
            title.setText(stringBuilder.toString());
            debugOverlay.addView(title, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 16.0f));
            ScrollView scroll = new ScrollView(context);
            final TextView debugText = new TextView(context);
            debugText.setTypeface(Typeface.MONOSPACE);
            debugText.setTextSize(1, 11.0f);
            debugText.setMaxWidth(AndroidUtilities.dp(350.0f));
            debugText.setTextColor(-1);
            debugText.setText(getFormattedDebugString());
            scroll.addView(debugText);
            debugOverlay.addView(scroll, LayoutHelper.createLinear(-1, -1, 1.0f));
            TextView closeBtn = new TextView(context);
            closeBtn.setBackgroundColor(-1);
            closeBtn.setTextColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
            closeBtn.setPadding(pad, pad, pad, pad);
            closeBtn.setTextSize(1, 15.0f);
            closeBtn.setText(LocaleController.getString("Close", R.string.Close));
            debugOverlay.addView(closeBtn, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
            final WindowManager wm = (WindowManager) getSystemService("window");
            wm.addView(debugOverlay, new WindowManager.LayoutParams(-1, -1, 1000, 0, -3));
            closeBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    wm.removeView(debugOverlay);
                }
            });
            debugOverlay.postDelayed(new Runnable() {
                public void run() {
                    if (!VoIPActivity.this.isFinishing()) {
                        if (VoIPService.getSharedInstance() != null) {
                            debugText.setText(VoIPActivity.this.getFormattedDebugString());
                            debugOverlay.postDelayed(this, 500);
                        }
                    }
                }
            }, 500);
        }
    }

    private void showInviteFragment() {
    }

    private void startUpdatingCallDuration() {
        new Runnable() {
            public void run() {
                if (!VoIPActivity.this.isFinishing()) {
                    if (VoIPService.getSharedInstance() != null) {
                        if (VoIPActivity.this.callState == 3 || VoIPActivity.this.callState == 5) {
                            CharSequence format;
                            long duration = VoIPService.getSharedInstance().getCallDuration() / 1000;
                            TextView access$3000 = VoIPActivity.this.durationText;
                            if (duration > 3600) {
                                format = String.format("%d:%02d:%02d", new Object[]{Long.valueOf(duration / 3600), Long.valueOf((duration % 3600) / 60), Long.valueOf(duration % 60)});
                            } else {
                                format = String.format("%d:%02d", new Object[]{Long.valueOf(duration / 60), Long.valueOf(duration % 60)});
                            }
                            access$3000.setText(format);
                            VoIPActivity.this.durationText.postDelayed(this, 500);
                        }
                    }
                }
            }
        }.run();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!this.isIncomingWaiting || (keyCode != 25 && keyCode != 24)) {
            return super.onKeyDown(keyCode, event);
        }
        if (VoIPService.getSharedInstance() != null) {
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
            ObjectAnimator colorAnim;
            r0.acceptBtn.setVisibility(8);
            if (VERSION.SDK_INT >= 21) {
                colorAnim = ObjectAnimator.ofArgb(r0.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
            } else {
                colorAnim = ObjectAnimator.ofInt(r0.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
                colorAnim.setEvaluator(new ArgbEvaluator());
            }
            AnimatorSet set = new AnimatorSet();
            AnimatorSet decSet = new AnimatorSet();
            decSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(r0.endBtnIcon, "rotation", new float[]{-135.0f, 0.0f}), colorAnim});
            decSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            decSet.setDuration(500);
            AnimatorSet accSet = new AnimatorSet();
            r5 = new Animator[3];
            r5[1] = ObjectAnimator.ofFloat(r0.declineBtn, "alpha", new float[]{0.0f});
            r5[2] = ObjectAnimator.ofFloat(r0.accountNameText, "alpha", new float[]{0.0f});
            accSet.playTogether(r5);
            accSet.setInterpolator(CubicBezierInterpolator.EASE_IN);
            accSet.setDuration(125);
            set.playTogether(new Animator[]{decSet, accSet});
            set.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    VoIPActivity.this.swipeViewsWrap.setVisibility(8);
                    VoIPActivity.this.declineBtn.setVisibility(8);
                    VoIPActivity.this.accountNameText.setVisibility(8);
                }
            });
            set.start();
            return;
        }
        AnimatorSet set2 = new AnimatorSet();
        set = new AnimatorSet();
        set.playTogether(new Animator[]{ObjectAnimator.ofFloat(r0.bottomButtons, "alpha", new float[]{0.0f, 1.0f})});
        set.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        set.setDuration(500);
        accSet = new AnimatorSet();
        r5 = new Animator[4];
        r5[1] = ObjectAnimator.ofFloat(r0.declineBtn, "alpha", new float[]{0.0f});
        r5[2] = ObjectAnimator.ofFloat(r0.acceptBtn, "alpha", new float[]{0.0f});
        r5[3] = ObjectAnimator.ofFloat(r0.accountNameText, "alpha", new float[]{0.0f});
        accSet.playTogether(r5);
        accSet.setInterpolator(CubicBezierInterpolator.EASE_IN);
        accSet.setDuration(125);
        set2.playTogether(new Animator[]{set, accSet});
        set2.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.swipeViewsWrap.setVisibility(8);
                VoIPActivity.this.declineBtn.setVisibility(8);
                VoIPActivity.this.acceptBtn.setVisibility(8);
                VoIPActivity.this.accountNameText.setVisibility(8);
            }
        });
        set2.start();
    }

    private void showRetry() {
        ObjectAnimator colorAnim;
        if (this.retryAnim != null) {
            this.retryAnim.cancel();
        }
        this.endBtn.setEnabled(false);
        this.retrying = true;
        this.cancelBtn.setVisibility(0);
        this.cancelBtn.setAlpha(0.0f);
        AnimatorSet set = new AnimatorSet();
        if (VERSION.SDK_INT >= 21) {
            colorAnim = ObjectAnimator.ofArgb(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-1696188, -12207027});
        } else {
            colorAnim = ObjectAnimator.ofInt(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-1696188, -12207027});
            colorAnim.setEvaluator(new ArgbEvaluator());
        }
        r5 = new Animator[4];
        r5[1] = ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[]{0.0f, (float) (((this.content.getWidth() / 2) - AndroidUtilities.dp(52.0f)) - (this.endBtn.getWidth() / 2))});
        r5[2] = colorAnim;
        r5[3] = ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[]{0.0f, -135.0f});
        set.playTogether(r5);
        set.setStartDelay(200);
        set.setDuration(300);
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.retryAnim = null;
                VoIPActivity.this.endBtn.setEnabled(true);
            }
        });
        this.retryAnim = set;
        set.start();
    }

    private void hideRetry() {
        if (this.retryAnim != null) {
            this.retryAnim.cancel();
        }
        this.retrying = false;
        ObjectAnimator colorAnim;
        if (VERSION.SDK_INT >= 21) {
            colorAnim = ObjectAnimator.ofArgb(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
        } else {
            colorAnim = ObjectAnimator.ofInt(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
            colorAnim.setEvaluator(new ArgbEvaluator());
        }
        AnimatorSet set = new AnimatorSet();
        r4 = new Animator[4];
        r4[2] = ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[]{0.0f});
        r4[3] = ObjectAnimator.ofFloat(this.cancelBtn, "alpha", new float[]{0.0f});
        set.playTogether(r4);
        set.setStartDelay(200);
        set.setDuration(300);
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.cancelBtn.setVisibility(8);
                VoIPActivity.this.endBtn.setEnabled(true);
                VoIPActivity.this.retryAnim = null;
            }
        });
        this.retryAnim = set;
        set.start();
    }

    public void onStateChanged(final int state) {
        final int prevState = this.callState;
        this.callState = state;
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
                boolean wasFirstStateChange = VoIPActivity.this.firstStateChange;
                if (VoIPActivity.this.firstStateChange) {
                    VoIPActivity.this.spkToggle.setChecked(((AudioManager) VoIPActivity.this.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).isSpeakerphoneOn());
                    if (VoIPActivity.this.isIncomingWaiting = state == 15) {
                        VoIPActivity.this.swipeViewsWrap.setVisibility(0);
                        VoIPActivity.this.endBtn.setVisibility(8);
                        VoIPActivity.this.acceptSwipe.startAnimatingArrows();
                        VoIPActivity.this.declineSwipe.startAnimatingArrows();
                        if (UserConfig.getActivatedAccountsCount() > 1) {
                            User self = UserConfig.getInstance(VoIPActivity.this.currentAccount).getCurrentUser();
                            VoIPActivity.this.accountNameText.setText(LocaleController.formatString("VoipAnsweringAsAccount", R.string.VoipAnsweringAsAccount, ContactsController.formatName(self.first_name, self.last_name)));
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
                    if (state != 3) {
                        VoIPActivity.this.emojiWrap.setVisibility(8);
                    }
                    VoIPActivity.this.firstStateChange = false;
                }
                if (!(!VoIPActivity.this.isIncomingWaiting || state == 15 || state == 11 || state == 10)) {
                    VoIPActivity.this.isIncomingWaiting = false;
                    if (!VoIPActivity.this.didAcceptFromHere) {
                        VoIPActivity.this.callAccepted();
                    }
                }
                if (state == 15) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipIncoming", R.string.VoipIncoming), false);
                    VoIPActivity.this.getWindow().addFlags(2097152);
                } else {
                    if (state != 1) {
                        if (state != 2) {
                            if (state == 12) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipExchangingKeys", R.string.VoipExchangingKeys), true);
                            } else if (state == 13) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipWaiting", R.string.VoipWaiting), true);
                            } else if (state == 16) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRinging", R.string.VoipRinging), true);
                            } else if (state == 14) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRequesting", R.string.VoipRequesting), true);
                            } else if (state == 10) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipHangingUp", R.string.VoipHangingUp), true);
                                VoIPActivity.this.endBtnIcon.setAlpha(0.5f);
                                VoIPActivity.this.endBtn.setEnabled(false);
                            } else if (state == 11) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipCallEnded", R.string.VoipCallEnded), false);
                                VoIPActivity.this.stateText.postDelayed(new C17491(), 200);
                            } else if (state == 17) {
                                VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipBusy", R.string.VoipBusy), false);
                                VoIPActivity.this.showRetry();
                            } else {
                                int lastError;
                                if (state != 3) {
                                    if (state != 5) {
                                        if (state == 4) {
                                            VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipFailed", R.string.VoipFailed), false);
                                            lastError = VoIPService.getSharedInstance() != null ? VoIPService.getSharedInstance().getLastError() : 0;
                                            if (lastError == 1) {
                                                VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerIncompatible", R.string.VoipPeerIncompatible, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                                            } else if (lastError == -1) {
                                                VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerOutdated", R.string.VoipPeerOutdated, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                                            } else if (lastError == -2) {
                                                VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", R.string.CallNotAvailable, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                                            } else if (lastError == 3) {
                                                VoIPActivity.this.showErrorDialog("Error initializing audio hardware");
                                            } else if (lastError == -3) {
                                                VoIPActivity.this.finish();
                                            } else if (lastError == -5) {
                                                VoIPActivity.this.showErrorDialog(LocaleController.getString("VoipErrorUnknown", R.string.VoipErrorUnknown));
                                            } else {
                                                VoIPActivity.this.stateText.postDelayed(new C17513(), 1000);
                                            }
                                        }
                                    }
                                }
                                if (!wasFirstStateChange && state == 3) {
                                    lastError = MessagesController.getGlobalMainSettings().getInt("call_emoji_tooltip_count", 0);
                                    if (lastError < 3) {
                                        VoIPActivity.this.setEmojiTooltipVisible(true);
                                        VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.this.tooltipHider = new C17502(), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                                        MessagesController.getGlobalMainSettings().edit().putInt("call_emoji_tooltip_count", lastError + 1).commit();
                                    }
                                }
                                if (!(prevState == 3 || prevState == 5)) {
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
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipConnecting", R.string.VoipConnecting), true);
                }
                VoIPActivity.this.brandingText.invalidate();
            }
        });
    }

    public void onSignalBarsCountChanged(final int count) {
        runOnUiThread(new Runnable() {
            public void run() {
                VoIPActivity.this.signalBarsCount = count;
                VoIPActivity.this.brandingText.invalidate();
            }
        });
    }

    private void showErrorDialog(CharSequence message) {
        AlertDialog dlg = new Builder(this).setTitle(LocaleController.getString("VoipFailed", R.string.VoipFailed)).setMessage(message).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).show();
        dlg.setCanceledOnTouchOutside(true);
        dlg.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                VoIPActivity.this.finish();
            }
        });
    }

    public void onAudioSettingsChanged() {
        VoIPBaseService svc = VoIPBaseService.getSharedInstance();
        if (svc != null) {
            this.micToggle.setChecked(svc.isMicMute());
            if (svc.hasEarpiece() || svc.isBluetoothHeadsetConnected()) {
                this.spkToggle.setVisibility(0);
                if (!svc.hasEarpiece()) {
                    this.spkToggle.setImageResource(R.drawable.ic_bluetooth_white_24dp);
                    this.spkToggle.setChecked(svc.isSpeakerphoneOn());
                } else if (svc.isBluetoothHeadsetConnected()) {
                    switch (svc.getCurrentAudioRoute()) {
                        case 0:
                            this.spkToggle.setImageResource(R.drawable.ic_phone_in_talk_white_24dp);
                            break;
                        case 1:
                            this.spkToggle.setImageResource(R.drawable.ic_volume_up_white_24dp);
                            break;
                        case 2:
                            this.spkToggle.setImageResource(R.drawable.ic_bluetooth_white_24dp);
                            break;
                        default:
                            break;
                    }
                    this.spkToggle.setChecked(false);
                } else {
                    this.spkToggle.setImageResource(R.drawable.ic_volume_up_white_24dp);
                    this.spkToggle.setChecked(svc.isSpeakerphoneOn());
                }
            } else {
                this.spkToggle.setVisibility(4);
            }
        }
    }

    private void setStateTextAnimated(String _newText, boolean ellipsis) {
        if (!_newText.equals(this.lastStateText)) {
            CharSequence newText;
            this.lastStateText = _newText;
            if (this.textChangingAnim != null) {
                this.textChangingAnim.cancel();
            }
            if (ellipsis) {
                if (!this.ellAnimator.isRunning()) {
                    this.ellAnimator.start();
                }
                newText = new SpannableStringBuilder(_newText.toUpperCase());
                for (TextAlphaSpan s : this.ellSpans) {
                    s.setAlpha(0);
                }
                SpannableString ell = new SpannableString("...");
                ell.setSpan(this.ellSpans[0], 0, 1, 0);
                ell.setSpan(this.ellSpans[1], 1, 2, 0);
                ell.setSpan(this.ellSpans[2], 2, 3, 0);
                newText.append(ell);
            } else {
                if (this.ellAnimator.isRunning()) {
                    this.ellAnimator.cancel();
                }
                newText = _newText.toUpperCase();
            }
            this.stateText2.setText(newText);
            this.stateText2.setVisibility(0);
            this.stateText.setPivotX(LocaleController.isRTL ? (float) this.stateText.getWidth() : 0.0f);
            this.stateText.setPivotY((float) (this.stateText.getHeight() / 2));
            this.stateText2.setPivotX(LocaleController.isRTL ? (float) this.stateText.getWidth() : 0.0f);
            this.stateText2.setPivotY((float) (this.stateText.getHeight() / 2));
            this.durationText = this.stateText2;
            AnimatorSet set = new AnimatorSet();
            r6 = new Animator[8];
            r6[1] = ObjectAnimator.ofFloat(this.stateText2, "translationY", new float[]{(float) (this.stateText.getHeight() / 2), 0.0f});
            r6[2] = ObjectAnimator.ofFloat(this.stateText2, "scaleX", new float[]{0.7f, 1.0f});
            r6[3] = ObjectAnimator.ofFloat(this.stateText2, "scaleY", new float[]{0.7f, 1.0f});
            r6[4] = ObjectAnimator.ofFloat(this.stateText, "alpha", new float[]{1.0f, 0.0f});
            r6[5] = ObjectAnimator.ofFloat(this.stateText, "translationY", new float[]{0.0f, (float) ((-this.stateText.getHeight()) / 2)});
            r6[6] = ObjectAnimator.ofFloat(this.stateText, "scaleX", new float[]{1.0f, 0.7f});
            r6[7] = ObjectAnimator.ofFloat(this.stateText, "scaleY", new float[]{1.0f, 0.7f});
            set.playTogether(r6);
            set.setDuration(200);
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            set.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
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
            this.textChangingAnim = set;
            set.start();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded) {
            for (ImageView iv : this.keyEmojiViews) {
                iv.invalidate();
            }
        }
        if (id == NotificationCenter.closeInCallActivity) {
            finish();
        }
    }

    private void setEmojiTooltipVisible(boolean visible) {
        this.emojiTooltipVisible = visible;
        if (this.tooltipAnim != null) {
            this.tooltipAnim.cancel();
        }
        this.hintTextView.setVisibility(0);
        ObjectAnimator oa = this.hintTextView;
        String str = "alpha";
        float[] fArr = new float[1];
        fArr[0] = visible ? 1.0f : 0.0f;
        oa = ObjectAnimator.ofFloat(oa, str, fArr);
        oa.setDuration(300);
        oa.setInterpolator(CubicBezierInterpolator.DEFAULT);
        oa.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.tooltipAnim = null;
            }
        });
        this.tooltipAnim = oa;
        oa.start();
    }

    private void setEmojiExpanded(boolean expanded) {
        boolean z = expanded;
        if (this.emojiExpanded != z) {
            r0.emojiExpanded = z;
            if (r0.emojiAnimator != null) {
                r0.emojiAnimator.cancel();
            }
            if (z) {
                int[] loc = new int[]{0, 0};
                int[] loc2 = new int[]{0, 0};
                r0.emojiWrap.getLocationInWindow(loc);
                r0.emojiExpandedText.getLocationInWindow(loc2);
                Rect rect = new Rect();
                getWindow().getDecorView().getGlobalVisibleRect(rect);
                int offsetY = ((loc2[1] - (loc[1] + r0.emojiWrap.getHeight())) - AndroidUtilities.dp(32.0f)) - r0.emojiWrap.getHeight();
                int firstOffsetX = ((rect.width() / 2) - (Math.round(((float) r0.emojiWrap.getWidth()) * 2.5f) / 2)) - loc[0];
                AnimatorSet set = new AnimatorSet();
                Animator[] animatorArr = new Animator[7];
                animatorArr[0] = ObjectAnimator.ofFloat(r0.emojiWrap, "translationY", new float[]{(float) offsetY});
                animatorArr[1] = ObjectAnimator.ofFloat(r0.emojiWrap, "translationX", new float[]{(float) firstOffsetX});
                animatorArr[2] = ObjectAnimator.ofFloat(r0.emojiWrap, "scaleX", new float[]{2.5f});
                animatorArr[3] = ObjectAnimator.ofFloat(r0.emojiWrap, "scaleY", new float[]{2.5f});
                animatorArr[4] = ObjectAnimator.ofFloat(r0.blurOverlayView1, "alpha", new float[]{r0.blurOverlayView1.getAlpha(), 1.0f, 1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(r0.blurOverlayView2, "alpha", new float[]{r0.blurOverlayView2.getAlpha(), r0.blurOverlayView2.getAlpha(), 1.0f});
                animatorArr[6] = ObjectAnimator.ofFloat(r0.emojiExpandedText, "alpha", new float[]{1.0f});
                set.playTogether(animatorArr);
                set.setDuration(300);
                set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                r0.emojiAnimator = set;
                set.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        VoIPActivity.this.emojiAnimator = null;
                    }
                });
                set.start();
            } else {
                AnimatorSet set2 = new AnimatorSet();
                Animator[] animatorArr2 = new Animator[7];
                animatorArr2[0] = ObjectAnimator.ofFloat(r0.emojiWrap, "translationX", new float[]{0.0f});
                animatorArr2[1] = ObjectAnimator.ofFloat(r0.emojiWrap, "translationY", new float[]{0.0f});
                animatorArr2[2] = ObjectAnimator.ofFloat(r0.emojiWrap, "scaleX", new float[]{1.0f});
                animatorArr2[3] = ObjectAnimator.ofFloat(r0.emojiWrap, "scaleY", new float[]{1.0f});
                animatorArr2[4] = ObjectAnimator.ofFloat(r0.blurOverlayView1, "alpha", new float[]{r0.blurOverlayView1.getAlpha(), r0.blurOverlayView1.getAlpha(), 0.0f});
                animatorArr2[5] = ObjectAnimator.ofFloat(r0.blurOverlayView2, "alpha", new float[]{r0.blurOverlayView2.getAlpha(), 0.0f, 0.0f});
                animatorArr2[6] = ObjectAnimator.ofFloat(r0.emojiExpandedText, "alpha", new float[]{0.0f});
                set2.playTogether(animatorArr2);
                set2.setDuration(300);
                set2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                r0.emojiAnimator = set2;
                set2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        VoIPActivity.this.emojiAnimator = null;
                    }
                });
                set2.start();
            }
        }
    }

    private void updateBlurredPhotos(final BitmapHolder src) {
        new Thread(new Runnable() {

            /* renamed from: org.telegram.ui.VoIPActivity$30$1 */
            class C17541 implements Runnable {
                C17541() {
                }

                public void run() {
                    VoIPActivity.this.blurOverlayView1.setImageBitmap(VoIPActivity.this.blurredPhoto1);
                    VoIPActivity.this.blurOverlayView2.setImageBitmap(VoIPActivity.this.blurredPhoto2);
                    src.release();
                }
            }

            public void run() {
                Bitmap blur1 = Bitmap.createBitmap(150, 150, Config.ARGB_8888);
                Canvas canvas = new Canvas(blur1);
                canvas.drawBitmap(src.bitmap, null, new Rect(0, 0, 150, 150), new Paint(2));
                Utilities.blurBitmap(blur1, 3, 0, blur1.getWidth(), blur1.getHeight(), blur1.getRowBytes());
                Palette palette = Palette.from(src.bitmap).generate();
                Paint paint = new Paint();
                paint.setColor((palette.getDarkMutedColor(-11242343) & 16777215) | NUM);
                canvas.drawColor(637534208);
                canvas.drawRect(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight(), paint);
                Bitmap blur2 = Bitmap.createBitmap(50, 50, Config.ARGB_8888);
                canvas = new Canvas(blur2);
                canvas.drawBitmap(src.bitmap, null, new Rect(0, 0, 50, 50), new Paint(2));
                Utilities.blurBitmap(blur2, 3, 0, blur2.getWidth(), blur2.getHeight(), blur2.getRowBytes());
                paint.setAlpha(102);
                Bitmap blur22 = blur2;
                canvas.drawRect(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight(), paint);
                VoIPActivity.this.blurredPhoto1 = blur1;
                VoIPActivity.this.blurredPhoto2 = blur22;
                VoIPActivity.this.runOnUiThread(new C17541());
            }
        }).start();
    }

    private void sendTextMessage(final String text) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                SendMessagesHelper.getInstance(VoIPActivity.this.currentAccount).sendMessage(text, (long) VoIPActivity.this.user.id, null, null, false, null, null, null);
            }
        });
    }

    private void showMessagesSheet() {
        Context context = this;
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().stopRinging();
        }
        SharedPreferences prefs = getSharedPreferences("mainconfig", 0);
        String[] msgs = new String[]{prefs.getString("quick_reply_msg1", LocaleController.getString("QuickReplyDefault1", R.string.QuickReplyDefault1)), prefs.getString("quick_reply_msg2", LocaleController.getString("QuickReplyDefault2", R.string.QuickReplyDefault2)), prefs.getString("quick_reply_msg3", LocaleController.getString("QuickReplyDefault3", R.string.QuickReplyDefault3)), prefs.getString("quick_reply_msg4", LocaleController.getString("QuickReplyDefault4", R.string.QuickReplyDefault4))};
        LinearLayout sheetView = new LinearLayout(context);
        sheetView.setOrientation(1);
        final BottomSheet sheet = new BottomSheet(context, true);
        if (VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(-13948117);
            sheet.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    VoIPActivity.this.getWindow().setNavigationBarColor(0);
                }
            });
        }
        OnClickListener listener = new OnClickListener() {
            public void onClick(final View v) {
                sheet.dismiss();
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().declineIncomingCall(4, new Runnable() {
                        public void run() {
                            VoIPActivity.this.sendTextMessage((String) v.getTag());
                        }
                    });
                }
            }
        };
        for (String msg : msgs) {
            BottomSheetCell cell = new BottomSheetCell(context, 0);
            cell.setTextAndIcon(msg, 0);
            cell.setTextColor(-1);
            cell.setTag(msg);
            cell.setOnClickListener(listener);
            sheetView.addView(cell);
        }
        FrameLayout customWrap = new FrameLayout(context);
        final BottomSheetCell cell2 = new BottomSheetCell(context, 0);
        cell2.setTextAndIcon(LocaleController.getString("QuickReplyCustom", R.string.QuickReplyCustom), 0);
        cell2.setTextColor(-1);
        customWrap.addView(cell2);
        final FrameLayout editor = new FrameLayout(context);
        final EditText field = new EditText(context);
        field.setTextSize(1, 16.0f);
        field.setTextColor(-1);
        field.setHintTextColor(DarkTheme.getColor(Theme.key_chat_messagePanelHint));
        field.setBackgroundDrawable(null);
        field.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
        field.setHint(LocaleController.getString("QuickReplyCustom", R.string.QuickReplyCustom));
        field.setMinHeight(AndroidUtilities.dp(48.0f));
        field.setGravity(80);
        field.setMaxLines(4);
        field.setSingleLine(false);
        field.setInputType((field.getInputType() | MessagesController.UPDATE_MASK_CHAT_ADMINS) | 131072);
        editor.addView(field, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 48.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 48.0f, 0.0f));
        final ImageView sendBtn = new ImageView(context);
        sendBtn.setScaleType(ScaleType.CENTER);
        sendBtn.setImageDrawable(DarkTheme.getThemedDrawable(context, R.drawable.ic_send, Theme.key_chat_messagePanelSend));
        if (LocaleController.isRTL) {
            sendBtn.setScaleX(-0.1f);
        } else {
            sendBtn.setScaleX(0.1f);
        }
        sendBtn.setScaleY(0.1f);
        sendBtn.setAlpha(0.0f);
        editor.addView(sendBtn, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : 5) | 80));
        sendBtn.setOnClickListener(new OnClickListener() {

            /* renamed from: org.telegram.ui.VoIPActivity$34$1 */
            class C17561 implements Runnable {
                C17561() {
                }

                public void run() {
                    VoIPActivity.this.sendTextMessage(field.getText().toString());
                }
            }

            public void onClick(View v) {
                if (field.length() != 0) {
                    sheet.dismiss();
                    if (VoIPService.getSharedInstance() != null) {
                        VoIPService.getSharedInstance().declineIncomingCall(4, new C17561());
                    }
                }
            }
        });
        sendBtn.setVisibility(4);
        final ImageView cancelBtn = new ImageView(context);
        cancelBtn.setScaleType(ScaleType.CENTER);
        cancelBtn.setImageDrawable(DarkTheme.getThemedDrawable(context, R.drawable.edit_cancel, Theme.key_chat_messagePanelIcons));
        editor.addView(cancelBtn, LayoutHelper.createFrame(48, 48, 80 | (LocaleController.isRTL ? 3 : 5)));
        cancelBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                editor.setVisibility(8);
                cell2.setVisibility(0);
                field.setText(TtmlNode.ANONYMOUS_REGION_ID);
                ((InputMethodManager) VoIPActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(field.getWindowToken(), 0);
            }
        });
        field.addTextChangedListener(new TextWatcher() {
            boolean prevState = null;

            /* renamed from: org.telegram.ui.VoIPActivity$36$1 */
            class C17571 implements Runnable {
                C17571() {
                }

                public void run() {
                    cancelBtn.setVisibility(4);
                }
            }

            /* renamed from: org.telegram.ui.VoIPActivity$36$2 */
            class C17582 implements Runnable {
                C17582() {
                }

                public void run() {
                    sendBtn.setVisibility(4);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                boolean hasText = s.length() > 0;
                if (this.prevState != hasText) {
                    this.prevState = hasText;
                    if (hasText) {
                        sendBtn.setVisibility(0);
                        sendBtn.animate().alpha(1.0f).scaleX(LocaleController.isRTL ? -1.0f : 1.0f).scaleY(1.0f).setDuration(200).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                        cancelBtn.animate().alpha(0.0f).scaleX(0.1f).scaleY(0.1f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(200).withEndAction(new C17571()).start();
                        return;
                    }
                    cancelBtn.setVisibility(0);
                    cancelBtn.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    sendBtn.animate().alpha(0.0f).scaleX(LocaleController.isRTL ? -0.1f : 0.1f).scaleY(0.1f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(200).withEndAction(new C17582()).start();
                }
            }
        });
        editor.setVisibility(8);
        customWrap.addView(editor);
        cell2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                editor.setVisibility(0);
                cell2.setVisibility(4);
                field.requestFocus();
                ((InputMethodManager) VoIPActivity.this.getSystemService("input_method")).showSoftInput(field, 0);
            }
        });
        sheetView.addView(customWrap);
        sheet.setCustomView(sheetView);
        sheet.setBackgroundColor(-13948117);
        sheet.show();
    }
}
