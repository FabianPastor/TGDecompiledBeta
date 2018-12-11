package org.telegram.p005ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.support.p002v7.graphics.Palette;
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
import android.view.ViewPropertyAnimator;
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
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.ByteArrayOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.CLASSNAMER;
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
import org.telegram.messenger.voip.EncryptionKeyEmojifier;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPBaseService.StateListener;
import org.telegram.messenger.voip.VoIPController;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.BottomSheet;
import org.telegram.p005ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.p005ui.ActionBar.DarkAlertDialog.Builder;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Components.BackupImageView;
import org.telegram.p005ui.Components.CorrectlyMeasuringTextView;
import org.telegram.p005ui.Components.CubicBezierInterpolator;
import org.telegram.p005ui.Components.IdenticonDrawable;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.voip.CallSwipeView;
import org.telegram.p005ui.Components.voip.CallSwipeView.Listener;
import org.telegram.p005ui.Components.voip.CheckableImageView;
import org.telegram.p005ui.Components.voip.DarkTheme;
import org.telegram.p005ui.Components.voip.FabBackgroundDrawable;
import org.telegram.p005ui.Components.voip.VoIPHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.VoIPActivity */
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

    /* renamed from: org.telegram.ui.VoIPActivity$12 */
    class CLASSNAME implements OnClickListener {
        CLASSNAME() {
        }

        public void onClick(View v) {
            boolean z = false;
            if (VoIPActivity.this.emojiTooltipVisible) {
                VoIPActivity.this.setEmojiTooltipVisible(false);
                if (VoIPActivity.this.tooltipHider != null) {
                    VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
                    VoIPActivity.this.tooltipHider = null;
                }
            }
            VoIPActivity voIPActivity = VoIPActivity.this;
            if (!VoIPActivity.this.emojiExpanded) {
                z = true;
            }
            voIPActivity.setEmojiExpanded(z);
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$13 */
    class CLASSNAME implements OnLongClickListener {

        /* renamed from: org.telegram.ui.VoIPActivity$13$1 */
        class CLASSNAME implements Runnable {
            CLASSNAME() {
            }

            public void run() {
                VoIPActivity.this.tooltipHider = null;
                VoIPActivity.this.setEmojiTooltipVisible(false);
            }
        }

        CLASSNAME() {
        }

        public boolean onLongClick(View v) {
            boolean z = false;
            if (VoIPActivity.this.emojiExpanded) {
                return false;
            }
            if (VoIPActivity.this.tooltipHider != null) {
                VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
                VoIPActivity.this.tooltipHider = null;
            }
            VoIPActivity voIPActivity = VoIPActivity.this;
            if (!VoIPActivity.this.emojiTooltipVisible) {
                z = true;
            }
            voIPActivity.setEmojiTooltipVisible(z);
            if (VoIPActivity.this.emojiTooltipVisible) {
                VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.this.tooltipHider = new CLASSNAME(), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$14 */
    class CLASSNAME extends AnimatorListenerAdapter {
        private Runnable restarter = new CLASSNAME();

        /* renamed from: org.telegram.ui.VoIPActivity$14$1 */
        class CLASSNAME implements Runnable {
            CLASSNAME() {
            }

            public void run() {
                if (!VoIPActivity.this.isFinishing()) {
                    VoIPActivity.this.ellAnimator.start();
                }
            }
        }

        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            if (!VoIPActivity.this.isFinishing()) {
                VoIPActivity.this.content.postDelayed(this.restarter, 300);
            }
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$15 */
    class CLASSNAME implements Runnable {
        CLASSNAME() {
        }

        public void run() {
            VoIPActivity.this.finish();
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$18 */
    class CLASSNAME implements Runnable {
        CLASSNAME() {
        }

        public void run() {
            if (!VoIPActivity.this.isFinishing() && VoIPService.getSharedInstance() != null) {
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

    /* renamed from: org.telegram.ui.VoIPActivity$19 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            VoIPActivity.this.swipeViewsWrap.setVisibility(8);
            VoIPActivity.this.declineBtn.setVisibility(8);
            VoIPActivity.this.accountNameText.setVisibility(8);
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$1 */
    class CLASSNAME implements ImageReceiverDelegate {
        CLASSNAME() {
        }

        public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb) {
            BitmapHolder bmp = imageReceiver.getBitmapSafe();
            if (bmp != null) {
                VoIPActivity.this.updateBlurredPhotos(bmp);
            }
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$20 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            VoIPActivity.this.swipeViewsWrap.setVisibility(8);
            VoIPActivity.this.declineBtn.setVisibility(8);
            VoIPActivity.this.acceptBtn.setVisibility(8);
            VoIPActivity.this.accountNameText.setVisibility(8);
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$21 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            VoIPActivity.this.retryAnim = null;
            VoIPActivity.this.endBtn.setEnabled(true);
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$22 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            VoIPActivity.this.cancelBtn.setVisibility(8);
            VoIPActivity.this.endBtn.setEnabled(true);
            VoIPActivity.this.retryAnim = null;
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$25 */
    class CLASSNAME implements OnDismissListener {
        CLASSNAME() {
        }

        public void onDismiss(DialogInterface dialog) {
            VoIPActivity.this.finish();
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$26 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.VoIPActivity$27 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            VoIPActivity.this.tooltipAnim = null;
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$28 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            VoIPActivity.this.emojiAnimator = null;
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$29 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            VoIPActivity.this.emojiAnimator = null;
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$2 */
    class CLASSNAME implements OnClickListener {
        private int tapCount = 0;

        CLASSNAME() {
        }

        public void onClick(View v) {
            if (BuildVars.DEBUG_VERSION || this.tapCount == 9) {
                VoIPActivity.this.showDebugAlert();
                this.tapCount = 0;
                return;
            }
            this.tapCount++;
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$32 */
    class CLASSNAME implements OnDismissListener {
        CLASSNAME() {
        }

        public void onDismiss(DialogInterface dialog) {
            VoIPActivity.this.getWindow().setNavigationBarColor(0);
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$3 */
    class CLASSNAME implements OnClickListener {

        /* renamed from: org.telegram.ui.VoIPActivity$3$1 */
        class CLASSNAME implements Runnable {
            CLASSNAME() {
            }

            public void run() {
                if (VoIPService.getSharedInstance() == null && !VoIPActivity.this.isFinishing()) {
                    VoIPActivity.this.endBtn.postDelayed(this, 100);
                } else if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().registerStateListener(VoIPActivity.this);
                }
            }
        }

        CLASSNAME() {
        }

        public void onClick(View v) {
            VoIPActivity.this.endBtn.setEnabled(false);
            if (VoIPActivity.this.retrying) {
                Intent intent = new Intent(VoIPActivity.this, VoIPService.class);
                intent.putExtra("user_id", VoIPActivity.this.user.var_id);
                intent.putExtra("is_outgoing", true);
                intent.putExtra("start_incall_activity", false);
                intent.putExtra("account", VoIPActivity.this.currentAccount);
                try {
                    VoIPActivity.this.startService(intent);
                } catch (Throwable e) {
                    FileLog.m14e(e);
                }
                VoIPActivity.this.hideRetry();
                VoIPActivity.this.endBtn.postDelayed(new CLASSNAME(), 100);
            } else if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().hangUp();
            }
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$4 */
    class CLASSNAME implements OnClickListener {
        CLASSNAME() {
        }

        public void onClick(View v) {
            VoIPService svc = VoIPService.getSharedInstance();
            if (svc != null) {
                svc.toggleSpeakerphoneOrShowRouteSheet(VoIPActivity.this);
            }
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$5 */
    class CLASSNAME implements OnClickListener {
        CLASSNAME() {
        }

        public void onClick(View v) {
            if (VoIPService.getSharedInstance() == null) {
                VoIPActivity.this.finish();
                return;
            }
            boolean checked = !VoIPActivity.this.micToggle.isChecked();
            VoIPActivity.this.micToggle.setChecked(checked);
            VoIPService.getSharedInstance().setMicMute(checked);
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$6 */
    class CLASSNAME implements OnClickListener {
        CLASSNAME() {
        }

        public void onClick(View v) {
            if (VoIPActivity.this.isIncomingWaiting) {
                VoIPActivity.this.showMessagesSheet();
                return;
            }
            Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
            intent.putExtra("currentAccount", VoIPActivity.this.currentAccount);
            intent.setFlags(32768);
            intent.putExtra("userId", VoIPActivity.this.user.var_id);
            VoIPActivity.this.startActivity(intent);
            VoIPActivity.this.finish();
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$7 */
    class CLASSNAME implements Listener {

        /* renamed from: org.telegram.ui.VoIPActivity$7$1 */
        class CLASSNAME extends AnimatorListenerAdapter {
            CLASSNAME() {
            }

            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.currentDeclineAnim = null;
            }
        }

        /* renamed from: org.telegram.ui.VoIPActivity$7$2 */
        class CLASSNAME extends AnimatorListenerAdapter {
            CLASSNAME() {
            }

            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.currentDeclineAnim = null;
            }
        }

        CLASSNAME() {
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
                return;
            }
            VoIPActivity.this.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
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
            set.addListener(new CLASSNAME());
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
            set.addListener(new CLASSNAME());
            VoIPActivity.this.currentDeclineAnim = set;
            set.start();
            VoIPActivity.this.declineSwipe.startAnimatingArrows();
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$8 */
    class CLASSNAME implements Listener {

        /* renamed from: org.telegram.ui.VoIPActivity$8$1 */
        class CLASSNAME extends AnimatorListenerAdapter {
            CLASSNAME() {
            }

            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.currentAcceptAnim = null;
            }
        }

        /* renamed from: org.telegram.ui.VoIPActivity$8$2 */
        class CLASSNAME extends AnimatorListenerAdapter {
            CLASSNAME() {
            }

            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.currentAcceptAnim = null;
            }
        }

        CLASSNAME() {
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
            set.addListener(new CLASSNAME());
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
            set.addListener(new CLASSNAME());
            VoIPActivity.this.currentAcceptAnim = set;
            set.start();
            VoIPActivity.this.acceptSwipe.startAnimatingArrows();
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$9 */
    class CLASSNAME implements OnClickListener {
        CLASSNAME() {
        }

        public void onClick(View v) {
            VoIPActivity.this.finish();
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$SignalBarsDrawable */
    private class SignalBarsDrawable extends Drawable {
        private int[] barHeights;
        private int offsetStart;
        private Paint paint;
        private RectF rect;

        private SignalBarsDrawable() {
            this.barHeights = new int[]{AndroidUtilities.m10dp(3.0f), AndroidUtilities.m10dp(6.0f), AndroidUtilities.m10dp(9.0f), AndroidUtilities.m10dp(12.0f)};
            this.paint = new Paint(1);
            this.rect = new RectF();
            this.offsetStart = 6;
        }

        /* synthetic */ SignalBarsDrawable(VoIPActivity x0, CLASSNAME x1) {
            this();
        }

        public void draw(Canvas canvas) {
            if (VoIPActivity.this.callState == 3 || VoIPActivity.this.callState == 5) {
                this.paint.setColor(-1);
                int x = getBounds().left + AndroidUtilities.m10dp(LocaleController.isRTL ? 0.0f : (float) this.offsetStart);
                int y = getBounds().top;
                for (int i = 0; i < 4; i++) {
                    this.paint.setAlpha(i + 1 <= VoIPActivity.this.signalBarsCount ? 242 : 102);
                    this.rect.set((float) (AndroidUtilities.m10dp((float) (i * 4)) + x), (float) ((getIntrinsicHeight() + y) - this.barHeights[i]), (float) (((AndroidUtilities.m10dp(4.0f) * i) + x) + AndroidUtilities.m10dp(3.0f)), (float) (getIntrinsicHeight() + y));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.m10dp(0.3f), (float) AndroidUtilities.m10dp(0.3f), this.paint);
                }
            }
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.m10dp((float) (this.offsetStart + 15));
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.m10dp(12.0f);
        }

        public int getOpacity() {
            return -3;
        }
    }

    /* renamed from: org.telegram.ui.VoIPActivity$TextAlphaSpan */
    private class TextAlphaSpan extends CharacterStyle {
        private int alpha = 0;

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
        View contentView = createContentView();
        setContentView(contentView);
        if (VERSION.SDK_INT >= 21) {
            getWindow().addFlags(Integer.MIN_VALUE);
            getWindow().setStatusBarColor(0);
            getWindow().setNavigationBarColor(0);
            getWindow().getDecorView().setSystemUiVisibility(1792);
        } else if (VERSION.SDK_INT >= 19) {
            getWindow().addFlags(NUM);
            getWindow().getDecorView().setSystemUiVisibility(1792);
        }
        this.user = VoIPService.getSharedInstance().getUser();
        if (this.user.photo != null) {
            this.photoView.getImageReceiver().setDelegate(new CLASSNAME());
            this.photoView.setImage(this.user.photo.photo_big, null, new ColorDrawable(Theme.ACTION_BAR_VIDEO_EDIT_COLOR));
            this.photoView.setLayerType(2, null);
        } else {
            this.photoView.setVisibility(8);
            contentView.setBackgroundDrawable(new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{-14994098, -14328963}));
        }
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setVolumeControlStream(0);
        this.nameText.setOnClickListener(new CLASSNAME());
        this.endBtn.setOnClickListener(new CLASSNAME());
        this.spkToggle.setOnClickListener(new CLASSNAME());
        this.micToggle.setOnClickListener(new CLASSNAME());
        this.chatBtn.setOnClickListener(new CLASSNAME());
        this.spkToggle.setChecked(((AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO)).isSpeakerphoneOn());
        this.micToggle.setChecked(VoIPService.getSharedInstance().isMicMute());
        onAudioSettingsChanged();
        this.nameText.setText(ContactsController.formatName(this.user.first_name, this.user.last_name));
        VoIPService.getSharedInstance().registerStateListener(this);
        this.acceptSwipe.setListener(new CLASSNAME());
        this.declineSwipe.setListener(new CLASSNAME());
        this.cancelBtn.setOnClickListener(new CLASSNAME());
        getWindow().getDecorView().setKeepScreenOn(true);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeInCallActivity);
        this.hintTextView.setText(LocaleController.formatString("CallEmojiKeyTooltip", CLASSNAMER.string.CallEmojiKeyTooltip, this.user.first_name));
        this.emojiExpandedText.setText(LocaleController.formatString("CallEmojiKeyTooltip", CLASSNAMER.string.CallEmojiKeyTooltip, this.user.first_name));
    }

    private View createContentView() {
        Drawable drawable;
        FrameLayout CLASSNAME = new FrameLayout(this) {
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
        CLASSNAME.setBackgroundColor(0);
        CLASSNAME.setFitsSystemWindows(true);
        CLASSNAME.setClipToPadding(false);
        View CLASSNAME = new BackupImageView(this) {
            private Drawable bottomGradient = getResources().getDrawable(CLASSNAMER.drawable.gradient_bottom);
            private Paint paint = new Paint();
            private Drawable topGradient = getResources().getDrawable(CLASSNAMER.drawable.gradient_top);

            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                this.paint.setColor(NUM);
                canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), this.paint);
                this.topGradient.setBounds(0, 0, getWidth(), AndroidUtilities.m10dp(170.0f));
                this.topGradient.setAlpha(128);
                this.topGradient.draw(canvas);
                this.bottomGradient.setBounds(0, getHeight() - AndroidUtilities.m10dp(220.0f), getWidth(), getHeight());
                this.bottomGradient.setAlpha(178);
                this.bottomGradient.draw(canvas);
            }
        };
        this.photoView = CLASSNAME;
        CLASSNAME.addView(CLASSNAME);
        this.blurOverlayView1 = new ImageView(this);
        this.blurOverlayView1.setScaleType(ScaleType.CENTER_CROP);
        this.blurOverlayView1.setAlpha(0.0f);
        CLASSNAME.addView(this.blurOverlayView1);
        this.blurOverlayView2 = new ImageView(this);
        this.blurOverlayView2.setScaleType(ScaleType.CENTER_CROP);
        this.blurOverlayView2.setAlpha(0.0f);
        CLASSNAME.addView(this.blurOverlayView2);
        TextView branding = new TextView(this);
        branding.setTextColor(-NUM);
        branding.setText(LocaleController.getString("VoipInCallBranding", CLASSNAMER.string.VoipInCallBranding));
        Drawable logo = getResources().getDrawable(CLASSNAMER.drawable.notification).mutate();
        logo.setAlpha(204);
        logo.setBounds(0, 0, AndroidUtilities.m10dp(15.0f), AndroidUtilities.m10dp(15.0f));
        this.signalBarsDrawable = new SignalBarsDrawable(this, null);
        this.signalBarsDrawable.setBounds(0, 0, this.signalBarsDrawable.getIntrinsicWidth(), this.signalBarsDrawable.getIntrinsicHeight());
        if (LocaleController.isRTL) {
            drawable = this.signalBarsDrawable;
        } else {
            drawable = logo;
        }
        if (!LocaleController.isRTL) {
            logo = this.signalBarsDrawable;
        }
        branding.setCompoundDrawables(drawable, null, logo, null);
        branding.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        branding.setGravity(LocaleController.isRTL ? 5 : 3);
        branding.setCompoundDrawablePadding(AndroidUtilities.m10dp(5.0f));
        branding.setTextSize(1, 14.0f);
        CLASSNAME.addView(branding, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 18.0f, 18.0f, 18.0f, 0.0f));
        this.brandingText = branding;
        CLASSNAME = new TextView(this);
        CLASSNAME.setSingleLine();
        CLASSNAME.setTextColor(-1);
        CLASSNAME.setTextSize(1, 40.0f);
        CLASSNAME.setEllipsize(TruncateAt.END);
        CLASSNAME.setGravity(LocaleController.isRTL ? 5 : 3);
        CLASSNAME.setShadowLayer((float) AndroidUtilities.m10dp(3.0f), 0.0f, (float) AndroidUtilities.m10dp(0.6666667f), NUM);
        CLASSNAME.setTypeface(Typeface.create("sans-serif-light", 0));
        this.nameText = CLASSNAME;
        CLASSNAME.addView(CLASSNAME, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 43.0f, 18.0f, 0.0f));
        CLASSNAME = new TextView(this);
        CLASSNAME.setTextColor(-NUM);
        CLASSNAME.setSingleLine();
        CLASSNAME.setEllipsize(TruncateAt.END);
        CLASSNAME.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        CLASSNAME.setShadowLayer((float) AndroidUtilities.m10dp(3.0f), 0.0f, (float) AndroidUtilities.m10dp(0.6666667f), NUM);
        CLASSNAME.setTextSize(1, 15.0f);
        CLASSNAME.setGravity(LocaleController.isRTL ? 5 : 3);
        this.stateText = CLASSNAME;
        CLASSNAME.addView(CLASSNAME, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        this.durationText = CLASSNAME;
        CLASSNAME = new TextView(this);
        CLASSNAME.setTextColor(-NUM);
        CLASSNAME.setSingleLine();
        CLASSNAME.setEllipsize(TruncateAt.END);
        CLASSNAME.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        CLASSNAME.setShadowLayer((float) AndroidUtilities.m10dp(3.0f), 0.0f, (float) AndroidUtilities.m10dp(0.6666667f), NUM);
        CLASSNAME.setTextSize(1, 15.0f);
        CLASSNAME.setGravity(LocaleController.isRTL ? 5 : 3);
        CLASSNAME.setVisibility(8);
        this.stateText2 = CLASSNAME;
        CLASSNAME.addView(CLASSNAME, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        this.ellSpans = new TextAlphaSpan[]{new TextAlphaSpan(), new TextAlphaSpan(), new TextAlphaSpan()};
        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(0);
        CLASSNAME.addView(buttons, LayoutHelper.createFrame(-1, -2, 80));
        TextView accountName = new TextView(this);
        accountName.setTextColor(-NUM);
        accountName.setSingleLine();
        accountName.setEllipsize(TruncateAt.END);
        accountName.setShadowLayer((float) AndroidUtilities.m10dp(3.0f), 0.0f, (float) AndroidUtilities.m10dp(0.6666667f), NUM);
        accountName.setTextSize(1, 15.0f);
        accountName.setGravity(LocaleController.isRTL ? 5 : 3);
        this.accountNameText = accountName;
        CLASSNAME.addView(accountName, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 120.0f, 18.0f, 0.0f));
        CLASSNAME = new CheckableImageView(this);
        CLASSNAME.setBackgroundResource(CLASSNAMER.drawable.bg_voip_icon_btn);
        Drawable micIcon = getResources().getDrawable(CLASSNAMER.drawable.ic_mic_off_white_24dp).mutate();
        CLASSNAME.setAlpha(204);
        CLASSNAME.setImageDrawable(micIcon);
        CLASSNAME.setScaleType(ScaleType.CENTER);
        CLASSNAME = new FrameLayout(this);
        this.micToggle = CLASSNAME;
        CLASSNAME.addView(CLASSNAME, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        buttons.addView(CLASSNAME, LayoutHelper.createLinear(0, -2, 1.0f));
        CLASSNAME = new ImageView(this);
        Drawable chatIcon = getResources().getDrawable(CLASSNAMER.drawable.ic_chat_bubble_white_24dp).mutate();
        chatIcon.setAlpha(204);
        CLASSNAME.setImageDrawable(chatIcon);
        CLASSNAME.setScaleType(ScaleType.CENTER);
        CLASSNAME = new FrameLayout(this);
        this.chatBtn = CLASSNAME;
        CLASSNAME.addView(CLASSNAME, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        buttons.addView(CLASSNAME, LayoutHelper.createLinear(0, -2, 1.0f));
        CLASSNAME = new CheckableImageView(this);
        CLASSNAME.setBackgroundResource(CLASSNAMER.drawable.bg_voip_icon_btn);
        Drawable speakerIcon = getResources().getDrawable(CLASSNAMER.drawable.ic_volume_up_white_24dp).mutate();
        CLASSNAME.setAlpha(204);
        CLASSNAME.setImageDrawable(speakerIcon);
        CLASSNAME.setScaleType(ScaleType.CENTER);
        CLASSNAME = new FrameLayout(this);
        this.spkToggle = CLASSNAME;
        CLASSNAME.addView(CLASSNAME, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        buttons.addView(CLASSNAME, LayoutHelper.createLinear(0, -2, 1.0f));
        this.bottomButtons = buttons;
        CLASSNAME = new LinearLayout(this);
        CLASSNAME.setOrientation(0);
        CallSwipeView acceptSwipe = new CallSwipeView(this);
        acceptSwipe.setColor(-12207027);
        this.acceptSwipe = acceptSwipe;
        CLASSNAME.addView(acceptSwipe, LayoutHelper.createLinear(-1, 70, 1.0f, 4, 4, -35, 4));
        CLASSNAME = new CallSwipeView(this);
        CLASSNAME.setColor(-1696188);
        this.declineSwipe = CLASSNAME;
        CLASSNAME.addView(CLASSNAME, LayoutHelper.createLinear(-1, 70, 1.0f, -35, 4, 4, 4));
        this.swipeViewsWrap = CLASSNAME;
        CLASSNAME.addView(CLASSNAME, LayoutHelper.createFrame(-1, -2.0f, 80, 20.0f, 0.0f, 20.0f, 68.0f));
        ImageView acceptBtn = new ImageView(this);
        FabBackgroundDrawable acceptBtnBg = new FabBackgroundDrawable();
        acceptBtnBg.setColor(-12207027);
        acceptBtn.setBackgroundDrawable(acceptBtnBg);
        acceptBtn.setImageResource(CLASSNAMER.drawable.ic_call_end_white_36dp);
        acceptBtn.setScaleType(ScaleType.MATRIX);
        Matrix matrix = new Matrix();
        matrix.setTranslate((float) AndroidUtilities.m10dp(17.0f), (float) AndroidUtilities.m10dp(17.0f));
        matrix.postRotate(-135.0f, (float) AndroidUtilities.m10dp(35.0f), (float) AndroidUtilities.m10dp(35.0f));
        acceptBtn.setImageMatrix(matrix);
        this.acceptBtn = acceptBtn;
        CLASSNAME.addView(acceptBtn, LayoutHelper.createFrame(78, 78.0f, 83, 20.0f, 0.0f, 0.0f, 68.0f));
        CLASSNAME = new ImageView(this);
        Drawable rejectBtnBg = new FabBackgroundDrawable();
        rejectBtnBg.setColor(-1696188);
        CLASSNAME.setBackgroundDrawable(rejectBtnBg);
        CLASSNAME.setImageResource(CLASSNAMER.drawable.ic_call_end_white_36dp);
        CLASSNAME.setScaleType(ScaleType.CENTER);
        this.declineBtn = CLASSNAME;
        CLASSNAME.addView(CLASSNAME, LayoutHelper.createFrame(78, 78.0f, 85, 0.0f, 0.0f, 20.0f, 68.0f));
        acceptSwipe.setViewToDrag(acceptBtn, false);
        CLASSNAME.setViewToDrag(CLASSNAME, true);
        CLASSNAME = new FrameLayout(this);
        FabBackgroundDrawable endBtnBg = new FabBackgroundDrawable();
        endBtnBg.setColor(-1696188);
        this.endBtnBg = endBtnBg;
        CLASSNAME.setBackgroundDrawable(endBtnBg);
        CLASSNAME = new ImageView(this);
        CLASSNAME.setImageResource(CLASSNAMER.drawable.ic_call_end_white_36dp);
        CLASSNAME.setScaleType(ScaleType.CENTER);
        this.endBtnIcon = CLASSNAME;
        CLASSNAME.addView(CLASSNAME, LayoutHelper.createFrame(70, 70.0f));
        CLASSNAME.setForeground(getResources().getDrawable(CLASSNAMER.drawable.fab_highlight_dark));
        this.endBtn = CLASSNAME;
        CLASSNAME.addView(CLASSNAME, LayoutHelper.createFrame(78, 78.0f, 81, 0.0f, 0.0f, 0.0f, 68.0f));
        CLASSNAME = new ImageView(this);
        FabBackgroundDrawable cancelBtnBg = new FabBackgroundDrawable();
        cancelBtnBg.setColor(-1);
        CLASSNAME.setBackgroundDrawable(cancelBtnBg);
        CLASSNAME.setImageResource(CLASSNAMER.drawable.edit_cancel);
        CLASSNAME.setColorFilter(-NUM);
        CLASSNAME.setScaleType(ScaleType.CENTER);
        CLASSNAME.setVisibility(8);
        this.cancelBtn = CLASSNAME;
        CLASSNAME.addView(CLASSNAME, LayoutHelper.createFrame(78, 78.0f, 83, 52.0f, 0.0f, 0.0f, 68.0f));
        this.emojiWrap = new LinearLayout(this);
        this.emojiWrap.setOrientation(0);
        this.emojiWrap.setClipToPadding(false);
        this.emojiWrap.setPivotX(0.0f);
        this.emojiWrap.setPivotY(0.0f);
        this.emojiWrap.setPadding(AndroidUtilities.m10dp(14.0f), AndroidUtilities.m10dp(10.0f), AndroidUtilities.m10dp(14.0f), AndroidUtilities.m10dp(10.0f));
        int i = 0;
        while (i < 4) {
            CLASSNAME = new ImageView(this);
            CLASSNAME.setScaleType(ScaleType.FIT_XY);
            this.emojiWrap.addView(CLASSNAME, LayoutHelper.createLinear(22, 22, i == 0 ? 0.0f : 4.0f, 0.0f, 0.0f, 0.0f));
            this.keyEmojiViews[i] = CLASSNAME;
            i++;
        }
        this.emojiWrap.setOnClickListener(new CLASSNAME());
        CLASSNAME.addView(this.emojiWrap, LayoutHelper.createFrame(-2, -2, (LocaleController.isRTL ? 3 : 5) | 48));
        this.emojiWrap.setOnLongClickListener(new CLASSNAME());
        this.emojiExpandedText = new TextView(this);
        this.emojiExpandedText.setTextSize(1, 16.0f);
        this.emojiExpandedText.setTextColor(-1);
        this.emojiExpandedText.setGravity(17);
        this.emojiExpandedText.setAlpha(0.0f);
        CLASSNAME.addView(this.emojiExpandedText, LayoutHelper.createFrame(-1, -2.0f, 17, 10.0f, 32.0f, 10.0f, 0.0f));
        this.hintTextView = new CorrectlyMeasuringTextView(this);
        this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.m10dp(3.0f), -NUM));
        this.hintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        this.hintTextView.setTextSize(1, 14.0f);
        this.hintTextView.setPadding(AndroidUtilities.m10dp(10.0f), AndroidUtilities.m10dp(10.0f), AndroidUtilities.m10dp(10.0f), AndroidUtilities.m10dp(10.0f));
        this.hintTextView.setGravity(17);
        this.hintTextView.setMaxWidth(AndroidUtilities.m10dp(300.0f));
        this.hintTextView.setAlpha(0.0f);
        CLASSNAME.addView(this.hintTextView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 42.0f, 10.0f, 0.0f));
        int ellMaxAlpha = this.stateText.getPaint().getAlpha();
        this.ellAnimator = new AnimatorSet();
        AnimatorSet animatorSet = this.ellAnimator;
        r41 = new Animator[6];
        r41[0] = createAlphaAnimator(this.ellSpans[0], 0, ellMaxAlpha, 0, 300);
        r41[1] = createAlphaAnimator(this.ellSpans[1], 0, ellMaxAlpha, 150, 300);
        r41[2] = createAlphaAnimator(this.ellSpans[2], 0, ellMaxAlpha, 300, 300);
        r41[3] = createAlphaAnimator(this.ellSpans[0], ellMaxAlpha, 0, 1000, 400);
        r41[4] = createAlphaAnimator(this.ellSpans[1], ellMaxAlpha, 0, 1000, 400);
        r41[5] = createAlphaAnimator(this.ellSpans[2], ellMaxAlpha, 0, 1000, 400);
        animatorSet.playTogether(r41);
        this.ellAnimator.addListener(new CLASSNAME());
        CLASSNAME.setClipChildren(false);
        this.content = CLASSNAME;
        return CLASSNAME;
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
        } else if (!this.isIncomingWaiting) {
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
        if (requestCode != 101) {
            return;
        }
        if (VoIPService.getSharedInstance() == null) {
            finish();
        } else if (grantResults.length > 0 && grantResults[0] == 0) {
            VoIPService.getSharedInstance().acceptIncomingCall();
            callAccepted();
        } else if (shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
            this.acceptSwipe.reset();
        } else {
            VoIPService.getSharedInstance().declineIncomingCall();
            VoIPHelper.permissionDenied(this, new CLASSNAME());
        }
    }

    private void updateKeyView() {
        if (VoIPService.getSharedInstance() != null) {
            new IdenticonDrawable().setColors(new int[]{16777215, -1, -NUM, NUM});
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
                    drawable.setBounds(0, 0, AndroidUtilities.m10dp(22.0f), AndroidUtilities.m10dp(22.0f));
                    this.keyEmojiViews[i].setImageDrawable(drawable);
                }
            }
        }
    }

    private CharSequence getFormattedDebugString() {
        String in = VoIPService.getSharedInstance().getDebugString();
        SpannableString ss = new SpannableString(in);
        int offset = 0;
        do {
            int lineEnd = in.indexOf(10, offset + 1);
            if (lineEnd == -1) {
                lineEnd = in.length();
            }
            String line = in.substring(offset, lineEnd);
            if (line.contains("IN_USE")) {
                ss.setSpan(new ForegroundColorSpan(-16711936), offset, lineEnd, 0);
            } else if (line.contains(": ")) {
                ss.setSpan(new ForegroundColorSpan(-NUM), offset, (line.indexOf(58) + offset) + 1, 0);
            }
            offset = in.indexOf(10, offset + 1);
        } while (offset != -1);
        return ss;
    }

    private void showDebugAlert() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().forceRating();
            final LinearLayout debugOverlay = new LinearLayout(this);
            debugOverlay.setOrientation(1);
            debugOverlay.setBackgroundColor(-NUM);
            int pad = AndroidUtilities.m10dp(16.0f);
            debugOverlay.setPadding(pad, pad * 2, pad, pad * 2);
            TextView title = new TextView(this);
            title.setTextColor(-1);
            title.setTextSize(1, 15.0f);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setGravity(17);
            title.setText("libtgvoip v" + VoIPController.getVersion());
            debugOverlay.addView(title, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 16.0f));
            ScrollView scroll = new ScrollView(this);
            final TextView debugText = new TextView(this);
            debugText.setTypeface(Typeface.MONOSPACE);
            debugText.setTextSize(1, 11.0f);
            debugText.setMaxWidth(AndroidUtilities.m10dp(350.0f));
            debugText.setTextColor(-1);
            debugText.setText(getFormattedDebugString());
            scroll.addView(debugText);
            debugOverlay.addView(scroll, LayoutHelper.createLinear(-1, -1, 1.0f));
            TextView closeBtn = new TextView(this);
            closeBtn.setBackgroundColor(-1);
            closeBtn.setTextColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
            closeBtn.setPadding(pad, pad, pad, pad);
            closeBtn.setTextSize(1, 15.0f);
            closeBtn.setText(LocaleController.getString("Close", CLASSNAMER.string.Close));
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
                    if (!VoIPActivity.this.isFinishing() && VoIPService.getSharedInstance() != null) {
                        debugText.setText(VoIPActivity.this.getFormattedDebugString());
                        debugOverlay.postDelayed(this, 500);
                    }
                }
            }, 500);
        }
    }

    private void showInviteFragment() {
    }

    private void startUpdatingCallDuration() {
        new CLASSNAME().run();
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
            this.spkToggle.setVisibility(0);
        } else {
            this.spkToggle.setVisibility(8);
        }
        this.bottomButtons.setVisibility(0);
        AnimatorSet set;
        AnimatorSet decSet;
        AnimatorSet accSet;
        Animator[] animatorArr;
        if (this.didAcceptFromHere) {
            ObjectAnimator colorAnim;
            this.acceptBtn.setVisibility(8);
            if (VERSION.SDK_INT >= 21) {
                colorAnim = ObjectAnimator.ofArgb(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
            } else {
                colorAnim = ObjectAnimator.ofInt(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
                colorAnim.setEvaluator(new ArgbEvaluator());
            }
            set = new AnimatorSet();
            decSet = new AnimatorSet();
            decSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[]{-135.0f, 0.0f}), colorAnim});
            decSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            decSet.setDuration(500);
            accSet = new AnimatorSet();
            animatorArr = new Animator[3];
            animatorArr[1] = ObjectAnimator.ofFloat(this.declineBtn, "alpha", new float[]{0.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.accountNameText, "alpha", new float[]{0.0f});
            accSet.playTogether(animatorArr);
            accSet.setInterpolator(CubicBezierInterpolator.EASE_IN);
            accSet.setDuration(125);
            set.playTogether(new Animator[]{decSet, accSet});
            set.addListener(new CLASSNAME());
            set.start();
            return;
        }
        set = new AnimatorSet();
        decSet = new AnimatorSet();
        decSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.bottomButtons, "alpha", new float[]{0.0f, 1.0f})});
        decSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        decSet.setDuration(500);
        accSet = new AnimatorSet();
        animatorArr = new Animator[4];
        animatorArr[1] = ObjectAnimator.ofFloat(this.declineBtn, "alpha", new float[]{0.0f});
        animatorArr[2] = ObjectAnimator.ofFloat(this.acceptBtn, "alpha", new float[]{0.0f});
        animatorArr[3] = ObjectAnimator.ofFloat(this.accountNameText, "alpha", new float[]{0.0f});
        accSet.playTogether(animatorArr);
        accSet.setInterpolator(CubicBezierInterpolator.EASE_IN);
        accSet.setDuration(125);
        set.playTogether(new Animator[]{decSet, accSet});
        set.addListener(new CLASSNAME());
        set.start();
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
        r2 = new Animator[4];
        r2[1] = ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[]{0.0f, (float) (((this.content.getWidth() / 2) - AndroidUtilities.m10dp(52.0f)) - (this.endBtn.getWidth() / 2))});
        r2[2] = colorAnim;
        r2[3] = ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[]{0.0f, -135.0f});
        set.playTogether(r2);
        set.setStartDelay(200);
        set.setDuration(300);
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.addListener(new CLASSNAME());
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
        r2 = new Animator[4];
        r2[2] = ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[]{0.0f});
        r2[3] = ObjectAnimator.ofFloat(this.cancelBtn, "alpha", new float[]{0.0f});
        set.playTogether(r2);
        set.setStartDelay(200);
        set.setDuration(300);
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.addListener(new CLASSNAME());
        this.retryAnim = set;
        set.start();
    }

    public void onStateChanged(final int state) {
        final int prevState = this.callState;
        this.callState = state;
        runOnUiThread(new Runnable() {

            /* renamed from: org.telegram.ui.VoIPActivity$23$1 */
            class CLASSNAME implements Runnable {
                CLASSNAME() {
                }

                public void run() {
                    VoIPActivity.this.finish();
                }
            }

            /* renamed from: org.telegram.ui.VoIPActivity$23$2 */
            class CLASSNAME implements Runnable {
                CLASSNAME() {
                }

                public void run() {
                    VoIPActivity.this.tooltipHider = null;
                    VoIPActivity.this.setEmojiTooltipVisible(false);
                }
            }

            /* renamed from: org.telegram.ui.VoIPActivity$23$3 */
            class CLASSNAME implements Runnable {
                CLASSNAME() {
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
                            VoIPActivity.this.accountNameText.setText(LocaleController.formatString("VoipAnsweringAsAccount", CLASSNAMER.string.VoipAnsweringAsAccount, ContactsController.formatName(self.first_name, self.last_name)));
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
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipIncoming", CLASSNAMER.string.VoipIncoming), false);
                    VoIPActivity.this.getWindow().addFlags(2097152);
                } else if (state == 1 || state == 2) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipConnecting", CLASSNAMER.string.VoipConnecting), true);
                } else if (state == 12) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipExchangingKeys", CLASSNAMER.string.VoipExchangingKeys), true);
                } else if (state == 13) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipWaiting", CLASSNAMER.string.VoipWaiting), true);
                } else if (state == 16) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRinging", CLASSNAMER.string.VoipRinging), true);
                } else if (state == 14) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRequesting", CLASSNAMER.string.VoipRequesting), true);
                } else if (state == 10) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipHangingUp", CLASSNAMER.string.VoipHangingUp), true);
                    VoIPActivity.this.endBtnIcon.setAlpha(0.5f);
                    VoIPActivity.this.endBtn.setEnabled(false);
                } else if (state == 11) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipCallEnded", CLASSNAMER.string.VoipCallEnded), false);
                    VoIPActivity.this.stateText.postDelayed(new CLASSNAME(), 200);
                } else if (state == 17) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipBusy", CLASSNAMER.string.VoipBusy), false);
                    VoIPActivity.this.showRetry();
                } else if (state == 3 || state == 5) {
                    if (!wasFirstStateChange && state == 3) {
                        int count = MessagesController.getGlobalMainSettings().getInt("call_emoji_tooltip_count", 0);
                        if (count < 3) {
                            VoIPActivity.this.setEmojiTooltipVisible(true);
                            VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.this.tooltipHider = new CLASSNAME(), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                            MessagesController.getGlobalMainSettings().edit().putInt("call_emoji_tooltip_count", count + 1).commit();
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
                } else if (state == 4) {
                    int lastError;
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipFailed", CLASSNAMER.string.VoipFailed), false);
                    if (VoIPService.getSharedInstance() != null) {
                        lastError = VoIPService.getSharedInstance().getLastError();
                    } else {
                        lastError = 0;
                    }
                    if (lastError == 1) {
                        VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerIncompatible", CLASSNAMER.string.VoipPeerIncompatible, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                    } else if (lastError == -1) {
                        VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerOutdated", CLASSNAMER.string.VoipPeerOutdated, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                    } else if (lastError == -2) {
                        VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", CLASSNAMER.string.CallNotAvailable, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                    } else if (lastError == 3) {
                        VoIPActivity.this.showErrorDialog("Error initializing audio hardware");
                    } else if (lastError == -3) {
                        VoIPActivity.this.finish();
                    } else if (lastError == -5) {
                        VoIPActivity.this.showErrorDialog(LocaleController.getString("VoipErrorUnknown", CLASSNAMER.string.VoipErrorUnknown));
                    } else {
                        VoIPActivity.this.stateText.postDelayed(new CLASSNAME(), 1000);
                    }
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
        AlertDialog dlg = new Builder(this).setTitle(LocaleController.getString("VoipFailed", CLASSNAMER.string.VoipFailed)).setMessage(message).setPositiveButton(LocaleController.getString("OK", CLASSNAMER.string.OK), null).show();
        dlg.setCanceledOnTouchOutside(true);
        dlg.setOnDismissListener(new CLASSNAME());
    }

    public void onAudioSettingsChanged() {
        VoIPBaseService svc = VoIPBaseService.getSharedInstance();
        if (svc != null) {
            this.micToggle.setChecked(svc.isMicMute());
            if (svc.hasEarpiece() || svc.isBluetoothHeadsetConnected()) {
                this.spkToggle.setVisibility(0);
                if (!svc.hasEarpiece()) {
                    this.spkToggle.setImageResource(CLASSNAMER.drawable.ic_bluetooth_white_24dp);
                    this.spkToggle.setChecked(svc.isSpeakerphoneOn());
                    return;
                } else if (svc.isBluetoothHeadsetConnected()) {
                    switch (svc.getCurrentAudioRoute()) {
                        case 0:
                            this.spkToggle.setImageResource(CLASSNAMER.drawable.ic_phone_in_talk_white_24dp);
                            break;
                        case 1:
                            this.spkToggle.setImageResource(CLASSNAMER.drawable.ic_volume_up_white_24dp);
                            break;
                        case 2:
                            this.spkToggle.setImageResource(CLASSNAMER.drawable.ic_bluetooth_white_24dp);
                            break;
                    }
                    this.spkToggle.setChecked(false);
                    return;
                } else {
                    this.spkToggle.setImageResource(CLASSNAMER.drawable.ic_volume_up_white_24dp);
                    this.spkToggle.setChecked(svc.isSpeakerphoneOn());
                    return;
                }
            }
            this.spkToggle.setVisibility(4);
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
                SpannableStringBuilder ssb = new SpannableStringBuilder(_newText.toUpperCase());
                for (TextAlphaSpan s : this.ellSpans) {
                    s.setAlpha(0);
                }
                SpannableString ell = new SpannableString("...");
                ell.setSpan(this.ellSpans[0], 0, 1, 0);
                ell.setSpan(this.ellSpans[1], 1, 2, 0);
                ell.setSpan(this.ellSpans[2], 2, 3, 0);
                ssb.append(ell);
                newText = ssb;
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
            r5 = new Animator[8];
            r5[1] = ObjectAnimator.ofFloat(this.stateText2, "translationY", new float[]{(float) (this.stateText.getHeight() / 2), 0.0f});
            r5[2] = ObjectAnimator.ofFloat(this.stateText2, "scaleX", new float[]{0.7f, 1.0f});
            r5[3] = ObjectAnimator.ofFloat(this.stateText2, "scaleY", new float[]{0.7f, 1.0f});
            r5[4] = ObjectAnimator.ofFloat(this.stateText, "alpha", new float[]{1.0f, 0.0f});
            r5[5] = ObjectAnimator.ofFloat(this.stateText, "translationY", new float[]{0.0f, (float) ((-this.stateText.getHeight()) / 2)});
            r5[6] = ObjectAnimator.ofFloat(this.stateText, "scaleX", new float[]{1.0f, 0.7f});
            r5[7] = ObjectAnimator.ofFloat(this.stateText, "scaleY", new float[]{1.0f, 0.7f});
            set.playTogether(r5);
            set.setDuration(200);
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            set.addListener(new CLASSNAME());
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
        TextView textView = this.hintTextView;
        String str = "alpha";
        float[] fArr = new float[1];
        fArr[0] = visible ? 1.0f : 0.0f;
        ObjectAnimator oa = ObjectAnimator.ofFloat(textView, str, fArr);
        oa.setDuration(300);
        oa.setInterpolator(CubicBezierInterpolator.DEFAULT);
        oa.addListener(new CLASSNAME());
        this.tooltipAnim = oa;
        oa.start();
    }

    private void setEmojiExpanded(boolean expanded) {
        if (this.emojiExpanded != expanded) {
            this.emojiExpanded = expanded;
            if (this.emojiAnimator != null) {
                this.emojiAnimator.cancel();
            }
            AnimatorSet set;
            Animator[] animatorArr;
            if (expanded) {
                int[] loc = new int[]{0, 0};
                int[] loc2 = new int[]{0, 0};
                this.emojiWrap.getLocationInWindow(loc);
                this.emojiExpandedText.getLocationInWindow(loc2);
                Rect rect = new Rect();
                getWindow().getDecorView().getGlobalVisibleRect(rect);
                int offsetY = ((loc2[1] - (loc[1] + this.emojiWrap.getHeight())) - AndroidUtilities.m10dp(32.0f)) - this.emojiWrap.getHeight();
                int firstOffsetX = ((rect.width() / 2) - (Math.round(((float) this.emojiWrap.getWidth()) * 2.5f) / 2)) - loc[0];
                set = new AnimatorSet();
                animatorArr = new Animator[7];
                animatorArr[0] = ObjectAnimator.ofFloat(this.emojiWrap, "translationY", new float[]{(float) offsetY});
                animatorArr[1] = ObjectAnimator.ofFloat(this.emojiWrap, "translationX", new float[]{(float) firstOffsetX});
                animatorArr[2] = ObjectAnimator.ofFloat(this.emojiWrap, "scaleX", new float[]{2.5f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.emojiWrap, "scaleY", new float[]{2.5f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.blurOverlayView1, "alpha", new float[]{this.blurOverlayView1.getAlpha(), 1.0f, 1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.blurOverlayView2, "alpha", new float[]{this.blurOverlayView2.getAlpha(), this.blurOverlayView2.getAlpha(), 1.0f});
                animatorArr[6] = ObjectAnimator.ofFloat(this.emojiExpandedText, "alpha", new float[]{1.0f});
                set.playTogether(animatorArr);
                set.setDuration(300);
                set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.emojiAnimator = set;
                set.addListener(new CLASSNAME());
                set.start();
                return;
            }
            set = new AnimatorSet();
            animatorArr = new Animator[7];
            animatorArr[0] = ObjectAnimator.ofFloat(this.emojiWrap, "translationX", new float[]{0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.emojiWrap, "translationY", new float[]{0.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.emojiWrap, "scaleX", new float[]{1.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.emojiWrap, "scaleY", new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.blurOverlayView1, "alpha", new float[]{this.blurOverlayView1.getAlpha(), this.blurOverlayView1.getAlpha(), 0.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.blurOverlayView2, "alpha", new float[]{this.blurOverlayView2.getAlpha(), 0.0f, 0.0f});
            animatorArr[6] = ObjectAnimator.ofFloat(this.emojiExpandedText, "alpha", new float[]{0.0f});
            set.playTogether(animatorArr);
            set.setDuration(300);
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.emojiAnimator = set;
            set.addListener(new CLASSNAME());
            set.start();
        }
    }

    private void updateBlurredPhotos(final BitmapHolder src) {
        new Thread(new Runnable() {

            /* renamed from: org.telegram.ui.VoIPActivity$30$1 */
            class CLASSNAME implements Runnable {
                CLASSNAME() {
                }

                public void run() {
                    VoIPActivity.this.blurOverlayView1.setImageBitmap(VoIPActivity.this.blurredPhoto1);
                    VoIPActivity.this.blurOverlayView2.setImageBitmap(VoIPActivity.this.blurredPhoto2);
                    src.release();
                }
            }

            public void run() {
                try {
                    Bitmap blur1 = Bitmap.createBitmap(150, 150, Config.ARGB_8888);
                    Canvas canvas = new Canvas(blur1);
                    canvas.drawBitmap(src.bitmap, null, new Rect(0, 0, 150, 150), new Paint(2));
                    Utilities.blurBitmap(blur1, 3, 0, blur1.getWidth(), blur1.getHeight(), blur1.getRowBytes());
                    Palette palette = Palette.from(src.bitmap).generate();
                    Paint paint = new Paint();
                    paint.setColor((palette.getDarkMutedColor(-11242343) & 16777215) | NUM);
                    canvas.drawColor(NUM);
                    canvas.drawRect(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight(), paint);
                    Bitmap blur2 = Bitmap.createBitmap(50, 50, Config.ARGB_8888);
                    Canvas canvas2 = new Canvas(blur2);
                    canvas2.drawBitmap(src.bitmap, null, new Rect(0, 0, 50, 50), new Paint(2));
                    Utilities.blurBitmap(blur2, 3, 0, blur2.getWidth(), blur2.getHeight(), blur2.getRowBytes());
                    paint.setAlpha(102);
                    canvas2.drawRect(0.0f, 0.0f, (float) canvas2.getWidth(), (float) canvas2.getHeight(), paint);
                    VoIPActivity.this.blurredPhoto1 = blur1;
                    VoIPActivity.this.blurredPhoto2 = blur2;
                    VoIPActivity.this.runOnUiThread(new CLASSNAME());
                } catch (Throwable th) {
                }
            }
        }).start();
    }

    private void sendTextMessage(final String text) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                SendMessagesHelper.getInstance(VoIPActivity.this.currentAccount).sendMessage(text, (long) VoIPActivity.this.user.var_id, null, null, false, null, null, null);
            }
        });
    }

    private void showMessagesSheet() {
        BottomSheetCell cell;
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().stopRinging();
        }
        SharedPreferences prefs = getSharedPreferences("mainconfig", 0);
        msgs = new String[4];
        msgs[0] = prefs.getString("quick_reply_msg1", LocaleController.getString("QuickReplyDefault1", CLASSNAMER.string.QuickReplyDefault1));
        msgs[1] = prefs.getString("quick_reply_msg2", LocaleController.getString("QuickReplyDefault2", CLASSNAMER.string.QuickReplyDefault2));
        msgs[2] = prefs.getString("quick_reply_msg3", LocaleController.getString("QuickReplyDefault3", CLASSNAMER.string.QuickReplyDefault3));
        msgs[3] = prefs.getString("quick_reply_msg4", LocaleController.getString("QuickReplyDefault4", CLASSNAMER.string.QuickReplyDefault4));
        LinearLayout sheetView = new LinearLayout(this);
        sheetView.setOrientation(1);
        BottomSheet bottomSheet = new BottomSheet(this, true);
        if (VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(-13948117);
            bottomSheet.setOnDismissListener(new CLASSNAME());
        }
        final BottomSheet bottomSheet2 = bottomSheet;
        OnClickListener listener = new OnClickListener() {
            public void onClick(final View v) {
                bottomSheet2.dismiss();
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
            cell = new BottomSheetCell(this, 0);
            cell.setTextAndIcon(msg, 0);
            cell.setTextColor(-1);
            cell.setTag(msg);
            cell.setOnClickListener(listener);
            sheetView.addView(cell);
        }
        FrameLayout customWrap = new FrameLayout(this);
        cell = new BottomSheetCell(this, 0);
        cell.setTextAndIcon(LocaleController.getString("QuickReplyCustom", CLASSNAMER.string.QuickReplyCustom), 0);
        cell.setTextColor(-1);
        customWrap.addView(cell);
        final FrameLayout editor = new FrameLayout(this);
        final EditText field = new EditText(this);
        field.setTextSize(1, 16.0f);
        field.setTextColor(-1);
        field.setHintTextColor(DarkTheme.getColor(Theme.key_chat_messagePanelHint));
        field.setBackgroundDrawable(null);
        field.setPadding(AndroidUtilities.m10dp(16.0f), AndroidUtilities.m10dp(11.0f), AndroidUtilities.m10dp(16.0f), AndroidUtilities.m10dp(12.0f));
        field.setHint(LocaleController.getString("QuickReplyCustom", CLASSNAMER.string.QuickReplyCustom));
        field.setMinHeight(AndroidUtilities.m10dp(48.0f));
        field.setGravity(80);
        field.setMaxLines(4);
        field.setSingleLine(false);
        field.setInputType((field.getInputType() | MessagesController.UPDATE_MASK_CHAT_ADMINS) | 131072);
        editor.addView(field, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 48.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 48.0f, 0.0f));
        View imageView = new ImageView(this);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageDrawable(DarkTheme.getThemedDrawable(this, CLASSNAMER.drawable.ic_send, Theme.key_chat_messagePanelSend));
        if (LocaleController.isRTL) {
            imageView.setScaleX(-0.1f);
        } else {
            imageView.setScaleX(0.1f);
        }
        imageView.setScaleY(0.1f);
        imageView.setAlpha(0.0f);
        editor.addView(imageView, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : 5) | 80));
        bottomSheet2 = bottomSheet;
        imageView.setOnClickListener(new OnClickListener() {

            /* renamed from: org.telegram.ui.VoIPActivity$34$1 */
            class CLASSNAME implements Runnable {
                CLASSNAME() {
                }

                public void run() {
                    VoIPActivity.this.sendTextMessage(field.getText().toString());
                }
            }

            public void onClick(View v) {
                if (field.length() != 0) {
                    bottomSheet2.dismiss();
                    if (VoIPService.getSharedInstance() != null) {
                        VoIPService.getSharedInstance().declineIncomingCall(4, new CLASSNAME());
                    }
                }
            }
        });
        imageView.setVisibility(4);
        final ImageView cancelBtn = new ImageView(this);
        cancelBtn.setScaleType(ScaleType.CENTER);
        cancelBtn.setImageDrawable(DarkTheme.getThemedDrawable(this, CLASSNAMER.drawable.edit_cancel, Theme.key_chat_messagePanelIcons));
        editor.addView(cancelBtn, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : 5) | 80));
        cancelBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                editor.setVisibility(8);
                cell.setVisibility(0);
                field.setText(TtmlNode.ANONYMOUS_REGION_ID);
                ((InputMethodManager) VoIPActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(field.getWindowToken(), 0);
            }
        });
        final View view = imageView;
        field.addTextChangedListener(new TextWatcher() {
            boolean prevState = false;

            /* renamed from: org.telegram.ui.VoIPActivity$36$1 */
            class CLASSNAME implements Runnable {
                CLASSNAME() {
                }

                public void run() {
                    cancelBtn.setVisibility(4);
                }
            }

            /* renamed from: org.telegram.ui.VoIPActivity$36$2 */
            class CLASSNAME implements Runnable {
                CLASSNAME() {
                }

                public void run() {
                    view.setVisibility(4);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                boolean hasText;
                if (s.length() > 0) {
                    hasText = true;
                } else {
                    hasText = false;
                }
                if (this.prevState != hasText) {
                    this.prevState = hasText;
                    if (hasText) {
                        float f;
                        view.setVisibility(0);
                        ViewPropertyAnimator alpha = view.animate().alpha(1.0f);
                        if (LocaleController.isRTL) {
                            f = -1.0f;
                        } else {
                            f = 1.0f;
                        }
                        alpha.scaleX(f).scaleY(1.0f).setDuration(200).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                        cancelBtn.animate().alpha(0.0f).scaleX(0.1f).scaleY(0.1f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(200).withEndAction(new CLASSNAME()).start();
                        return;
                    }
                    cancelBtn.setVisibility(0);
                    cancelBtn.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    view.animate().alpha(0.0f).scaleX(LocaleController.isRTL ? -0.1f : 0.1f).scaleY(0.1f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(200).withEndAction(new CLASSNAME()).start();
                }
            }
        });
        editor.setVisibility(8);
        customWrap.addView(editor);
        cell.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                editor.setVisibility(0);
                cell.setVisibility(4);
                field.requestFocus();
                ((InputMethodManager) VoIPActivity.this.getSystemService("input_method")).showSoftInput(field, 0);
            }
        });
        sheetView.addView(customWrap);
        bottomSheet.setCustomView(sheetView);
        bottomSheet.setBackgroundColor(-13948117);
        bottomSheet.show();
    }
}
