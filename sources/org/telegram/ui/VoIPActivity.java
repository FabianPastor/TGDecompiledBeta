package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
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
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
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
import androidx.palette.graphics.Palette;
import java.io.ByteArrayOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.Emoji.EmojiDrawable;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.telegram.messenger.ImageReceiver.ImageReceiverDelegate.-CC;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.EncryptionKeyEmojifier;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPBaseService.StateListener;
import org.telegram.messenger.voip.VoIPController;
import org.telegram.messenger.voip.VoIPService;
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

public class VoIPActivity extends Activity implements StateListener, NotificationCenterDelegate {
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

        /* synthetic */ SignalBarsDrawable(VoIPActivity voIPActivity, AnonymousClass1 anonymousClass1) {
            this();
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
                    this.rect.set((float) (AndroidUtilities.dp((float) (i2 * 4)) + dp), (float) ((getIntrinsicHeight() + i) - this.barHeights[i2]), (float) (((AndroidUtilities.dp(4.0f) * i2) + dp) + AndroidUtilities.dp(3.0f)), (float) (getIntrinsicHeight() + i));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(0.3f), (float) AndroidUtilities.dp(0.3f), this.paint);
                    i2 = i3;
                }
            }
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp((float) (this.offsetStart + 15));
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(12.0f);
        }
    }

    private class TextAlphaSpan extends CharacterStyle {
        private int alpha = 0;

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

    private void showInviteFragment() {
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
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
        View createContentView = createContentView();
        setContentView(createContentView);
        int i = VERSION.SDK_INT;
        if (i >= 21) {
            getWindow().addFlags(Integer.MIN_VALUE);
            getWindow().setStatusBarColor(0);
            getWindow().setNavigationBarColor(0);
            getWindow().getDecorView().setSystemUiVisibility(1792);
        } else if (i >= 19) {
            getWindow().addFlags(NUM);
            getWindow().getDecorView().setSystemUiVisibility(1792);
        }
        this.user = VoIPService.getSharedInstance().getUser();
        if (this.user.photo != null) {
            this.photoView.getImageReceiver().setDelegate(new ImageReceiverDelegate() {
                public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                    -CC.$default$onAnimationReady(this, imageReceiver);
                }

                public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
                    BitmapHolder bitmapSafe = imageReceiver.getBitmapSafe();
                    if (bitmapSafe != null) {
                        VoIPActivity.this.updateBlurredPhotos(bitmapSafe);
                    }
                }
            });
            this.photoView.setImage(ImageLocation.getForUser(this.user, true), null, new ColorDrawable(-16777216), this.user);
            this.photoView.setLayerType(2, null);
        } else {
            this.photoView.setVisibility(8);
            createContentView.setBackgroundDrawable(new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{-14994098, -14328963}));
        }
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setVolumeControlStream(0);
        this.nameText.setOnClickListener(new OnClickListener() {
            private int tapCount = 0;

            public void onClick(View view) {
                if (!BuildVars.DEBUG_VERSION) {
                    int i = this.tapCount;
                    if (i != 9) {
                        this.tapCount = i + 1;
                        return;
                    }
                }
                VoIPActivity.this.showDebugAlert();
                this.tapCount = 0;
            }
        });
        this.endBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                VoIPActivity.this.endBtn.setEnabled(false);
                if (VoIPActivity.this.retrying) {
                    Intent intent = new Intent(VoIPActivity.this, VoIPService.class);
                    intent.putExtra("user_id", VoIPActivity.this.user.id);
                    intent.putExtra("is_outgoing", true);
                    intent.putExtra("start_incall_activity", false);
                    intent.putExtra("account", VoIPActivity.this.currentAccount);
                    try {
                        VoIPActivity.this.startService(intent);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                    VoIPActivity.this.hideRetry();
                    VoIPActivity.this.endBtn.postDelayed(new Runnable() {
                        public void run() {
                            if (VoIPService.getSharedInstance() != null || VoIPActivity.this.isFinishing()) {
                                if (VoIPService.getSharedInstance() != null) {
                                    VoIPService.getSharedInstance().registerStateListener(VoIPActivity.this);
                                }
                                return;
                            }
                            VoIPActivity.this.endBtn.postDelayed(this, 100);
                        }
                    }, 100);
                    return;
                }
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().hangUp();
                }
            }
        });
        this.spkToggle.setOnClickListener(new -$$Lambda$VoIPActivity$dUHMFM99ze4iOU7D50RvnVcMshI(this));
        this.micToggle.setOnClickListener(new -$$Lambda$VoIPActivity$4VXO0BtKP7Yfr26BFsLWgsU7aiQ(this));
        this.chatBtn.setOnClickListener(new -$$Lambda$VoIPActivity$inP4dym5Y3UyHztb8VYJORgcvAk(this));
        this.spkToggle.setChecked(((AudioManager) getSystemService("audio")).isSpeakerphoneOn());
        this.micToggle.setChecked(VoIPService.getSharedInstance().isMicMute());
        onAudioSettingsChanged();
        TextView textView = this.nameText;
        User user = this.user;
        textView.setText(ContactsController.formatName(user.first_name, user.last_name));
        VoIPService.getSharedInstance().registerStateListener(this);
        this.acceptSwipe.setListener(new Listener() {
            public void onDragComplete() {
                VoIPActivity.this.acceptSwipe.setEnabled(false);
                VoIPActivity.this.declineSwipe.setEnabled(false);
                if (VoIPService.getSharedInstance() == null) {
                    VoIPActivity.this.finish();
                    return;
                }
                VoIPActivity.this.didAcceptFromHere = true;
                if (VERSION.SDK_INT >= 23) {
                    if (VoIPActivity.this.checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                        VoIPActivity.this.requestPermissions(new String[]{r3}, 101);
                    }
                }
                VoIPService.getSharedInstance().acceptIncomingCall();
                VoIPActivity.this.callAccepted();
            }

            public void onDragStart() {
                if (VoIPActivity.this.currentDeclineAnim != null) {
                    VoIPActivity.this.currentDeclineAnim.cancel();
                }
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[2];
                String str = "alpha";
                animatorArr[0] = ObjectAnimator.ofFloat(VoIPActivity.this.declineSwipe, str, new float[]{0.2f});
                animatorArr[1] = ObjectAnimator.ofFloat(VoIPActivity.this.declineBtn, str, new float[]{0.2f});
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(200);
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        VoIPActivity.this.currentDeclineAnim = null;
                    }
                });
                VoIPActivity.this.currentDeclineAnim = animatorSet;
                animatorSet.start();
                VoIPActivity.this.declineSwipe.stopAnimatingArrows();
            }

            public void onDragCancel() {
                if (VoIPActivity.this.currentDeclineAnim != null) {
                    VoIPActivity.this.currentDeclineAnim.cancel();
                }
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[2];
                String str = "alpha";
                animatorArr[0] = ObjectAnimator.ofFloat(VoIPActivity.this.declineSwipe, str, new float[]{1.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(VoIPActivity.this.declineBtn, str, new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(200);
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        VoIPActivity.this.currentDeclineAnim = null;
                    }
                });
                VoIPActivity.this.currentDeclineAnim = animatorSet;
                animatorSet.start();
                VoIPActivity.this.declineSwipe.startAnimatingArrows();
            }
        });
        this.declineSwipe.setListener(new Listener() {
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
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[2];
                String str = "alpha";
                animatorArr[0] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, str, new float[]{0.2f});
                animatorArr[1] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, str, new float[]{0.2f});
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(200);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        VoIPActivity.this.currentAcceptAnim = null;
                    }
                });
                VoIPActivity.this.currentAcceptAnim = animatorSet;
                animatorSet.start();
                VoIPActivity.this.acceptSwipe.stopAnimatingArrows();
            }

            public void onDragCancel() {
                if (VoIPActivity.this.currentAcceptAnim != null) {
                    VoIPActivity.this.currentAcceptAnim.cancel();
                }
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[2];
                String str = "alpha";
                animatorArr[0] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, str, new float[]{1.0f});
                animatorArr[1] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, str, new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(200);
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        VoIPActivity.this.currentAcceptAnim = null;
                    }
                });
                VoIPActivity.this.currentAcceptAnim = animatorSet;
                animatorSet.start();
                VoIPActivity.this.acceptSwipe.startAnimatingArrows();
            }
        });
        this.cancelBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                VoIPActivity.this.finish();
            }
        });
        getWindow().getDecorView().setKeepScreenOn(true);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeInCallActivity);
        String str = "CallEmojiKeyTooltip";
        this.hintTextView.setText(LocaleController.formatString(str, NUM, this.user.first_name));
        this.emojiExpandedText.setText(LocaleController.formatString(str, NUM, this.user.first_name));
        if (((AccessibilityManager) getSystemService("accessibility")).isTouchExplorationEnabled()) {
            this.nameText.postDelayed(new Runnable() {
                public void run() {
                    VoIPActivity.this.nameText.sendAccessibilityEvent(8);
                }
            }, 500);
        }
    }

    public /* synthetic */ void lambda$onCreate$0$VoIPActivity(View view) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.toggleSpeakerphoneOrShowRouteSheet(this);
        }
    }

    public /* synthetic */ void lambda$onCreate$1$VoIPActivity(View view) {
        if (VoIPService.getSharedInstance() == null) {
            finish();
            return;
        }
        int isChecked = this.micToggle.isChecked() ^ 1;
        this.micToggle.setChecked(isChecked);
        VoIPService.getSharedInstance().setMicMute(isChecked);
    }

    public /* synthetic */ void lambda$onCreate$2$VoIPActivity(View view) {
        if (!this.isIncomingWaiting) {
            Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("com.tmessages.openchat");
            stringBuilder.append(Math.random());
            stringBuilder.append(Integer.MAX_VALUE);
            intent.setAction(stringBuilder.toString());
            intent.putExtra("currentAccount", this.currentAccount);
            intent.setFlags(32768);
            intent.putExtra("userId", this.user.id);
            startActivity(intent);
            finish();
        } else if (SharedConfig.passcodeHash.length() <= 0) {
            try {
                if (!((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
                    showMessagesSheet();
                }
            } catch (Exception unused) {
            }
        }
    }

    private View createContentView() {
        AnonymousClass8 anonymousClass8 = new FrameLayout(this) {
            private void setNegativeMargins(Rect rect, LayoutParams layoutParams) {
                layoutParams.topMargin = -rect.top;
                layoutParams.bottomMargin = -rect.bottom;
                layoutParams.leftMargin = -rect.left;
                layoutParams.rightMargin = -rect.right;
            }

            /* Access modifiers changed, original: protected */
            public boolean fitSystemWindows(Rect rect) {
                setNegativeMargins(rect, (LayoutParams) VoIPActivity.this.photoView.getLayoutParams());
                setNegativeMargins(rect, (LayoutParams) VoIPActivity.this.blurOverlayView1.getLayoutParams());
                setNegativeMargins(rect, (LayoutParams) VoIPActivity.this.blurOverlayView2.getLayoutParams());
                return super.fitSystemWindows(rect);
            }
        };
        anonymousClass8.setBackgroundColor(0);
        anonymousClass8.setFitsSystemWindows(true);
        anonymousClass8.setClipToPadding(false);
        AnonymousClass9 anonymousClass9 = new BackupImageView(this) {
            private Drawable bottomGradient = getResources().getDrawable(NUM);
            private Paint paint = new Paint();
            private Drawable topGradient = getResources().getDrawable(NUM);

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
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
        this.photoView = anonymousClass9;
        anonymousClass8.addView(anonymousClass9);
        this.blurOverlayView1 = new ImageView(this);
        this.blurOverlayView1.setScaleType(ScaleType.CENTER_CROP);
        this.blurOverlayView1.setAlpha(0.0f);
        anonymousClass8.addView(this.blurOverlayView1);
        this.blurOverlayView2 = new ImageView(this);
        this.blurOverlayView2.setScaleType(ScaleType.CENTER_CROP);
        this.blurOverlayView2.setAlpha(0.0f);
        anonymousClass8.addView(this.blurOverlayView2);
        TextView textView = new TextView(this);
        textView.setTextColor(-NUM);
        textView.setText(LocaleController.getString("VoipInCallBranding", NUM));
        Drawable mutate = getResources().getDrawable(NUM).mutate();
        mutate.setAlpha(204);
        mutate.setBounds(0, 0, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f));
        this.signalBarsDrawable = new SignalBarsDrawable(this, null);
        SignalBarsDrawable signalBarsDrawable = this.signalBarsDrawable;
        signalBarsDrawable.setBounds(0, 0, signalBarsDrawable.getIntrinsicWidth(), this.signalBarsDrawable.getIntrinsicHeight());
        Drawable drawable = LocaleController.isRTL ? this.signalBarsDrawable : mutate;
        if (!LocaleController.isRTL) {
            mutate = this.signalBarsDrawable;
        }
        textView.setCompoundDrawables(drawable, null, mutate, null);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setGravity(LocaleController.isRTL ? 5 : 3);
        textView.setCompoundDrawablePadding(AndroidUtilities.dp(5.0f));
        textView.setTextSize(1, 14.0f);
        anonymousClass8.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 18.0f, 18.0f, 18.0f, 0.0f));
        this.brandingText = textView;
        textView = new TextView(this);
        textView.setSingleLine();
        textView.setTextColor(-1);
        textView.setTextSize(1, 40.0f);
        textView.setEllipsize(TruncateAt.END);
        textView.setGravity(LocaleController.isRTL ? 5 : 3);
        textView.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        textView.setTypeface(Typeface.create("sans-serif-light", 0));
        this.nameText = textView;
        anonymousClass8.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 43.0f, 18.0f, 0.0f));
        textView = new TextView(this);
        textView.setTextColor(-NUM);
        textView.setSingleLine();
        textView.setEllipsize(TruncateAt.END);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        textView.setTextSize(1, 15.0f);
        textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.stateText = textView;
        anonymousClass8.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        this.durationText = textView;
        textView = new TextView(this);
        textView.setTextColor(-NUM);
        textView.setSingleLine();
        textView.setEllipsize(TruncateAt.END);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        textView.setTextSize(1, 15.0f);
        textView.setGravity(LocaleController.isRTL ? 5 : 3);
        textView.setVisibility(8);
        this.stateText2 = textView;
        anonymousClass8.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        this.ellSpans = new TextAlphaSpan[]{new TextAlphaSpan(), new TextAlphaSpan(), new TextAlphaSpan()};
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        anonymousClass8.addView(linearLayout, LayoutHelper.createFrame(-1, -2, 80));
        TextView textView2 = new TextView(this);
        textView2.setTextColor(-NUM);
        textView2.setSingleLine();
        textView2.setEllipsize(TruncateAt.END);
        textView2.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        textView2.setTextSize(1, 15.0f);
        textView2.setGravity(LocaleController.isRTL ? 5 : 3);
        this.accountNameText = textView2;
        anonymousClass8.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 120.0f, 18.0f, 0.0f));
        CheckableImageView checkableImageView = new CheckableImageView(this);
        checkableImageView.setBackgroundResource(NUM);
        mutate = getResources().getDrawable(NUM).mutate();
        checkableImageView.setAlpha(204);
        checkableImageView.setImageDrawable(mutate);
        checkableImageView.setScaleType(ScaleType.CENTER);
        checkableImageView.setContentDescription(LocaleController.getString("AccDescrMuteMic", NUM));
        FrameLayout frameLayout = new FrameLayout(this);
        this.micToggle = checkableImageView;
        frameLayout.addView(checkableImageView, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f));
        this.chatBtn = new ImageView(this);
        Drawable mutate2 = getResources().getDrawable(NUM).mutate();
        mutate2.setAlpha(204);
        this.chatBtn.setImageDrawable(mutate2);
        this.chatBtn.setScaleType(ScaleType.CENTER);
        this.chatBtn.setContentDescription(LocaleController.getString("AccDescrOpenChat", NUM));
        FrameLayout frameLayout2 = new FrameLayout(this);
        frameLayout2.addView(this.chatBtn, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        linearLayout.addView(frameLayout2, LayoutHelper.createLinear(0, -2, 1.0f));
        checkableImageView = new CheckableImageView(this);
        checkableImageView.setBackgroundResource(NUM);
        mutate = getResources().getDrawable(NUM).mutate();
        checkableImageView.setAlpha(204);
        checkableImageView.setImageDrawable(mutate);
        checkableImageView.setScaleType(ScaleType.CENTER);
        checkableImageView.setContentDescription(LocaleController.getString("VoipAudioRoutingSpeaker", NUM));
        frameLayout = new FrameLayout(this);
        this.spkToggle = checkableImageView;
        frameLayout.addView(checkableImageView, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f));
        this.bottomButtons = linearLayout;
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        CallSwipeView callSwipeView = new CallSwipeView(this);
        callSwipeView.setColor(-12207027);
        callSwipeView.setContentDescription(LocaleController.getString("Accept", NUM));
        this.acceptSwipe = callSwipeView;
        linearLayout.addView(callSwipeView, LayoutHelper.createLinear(-1, 70, 1.0f, 4, 4, -35, 4));
        CallSwipeView callSwipeView2 = new CallSwipeView(this);
        callSwipeView2.setColor(-1696188);
        callSwipeView2.setContentDescription(LocaleController.getString("Decline", NUM));
        this.declineSwipe = callSwipeView2;
        linearLayout.addView(callSwipeView2, LayoutHelper.createLinear(-1, 70, 1.0f, -35, 4, 4, 4));
        this.swipeViewsWrap = linearLayout;
        anonymousClass8.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 80, 20.0f, 0.0f, 20.0f, 68.0f));
        ImageView imageView = new ImageView(this);
        FabBackgroundDrawable fabBackgroundDrawable = new FabBackgroundDrawable();
        fabBackgroundDrawable.setColor(-12207027);
        imageView.setBackgroundDrawable(fabBackgroundDrawable);
        imageView.setImageResource(NUM);
        imageView.setScaleType(ScaleType.MATRIX);
        Matrix matrix = new Matrix();
        matrix.setTranslate((float) AndroidUtilities.dp(17.0f), (float) AndroidUtilities.dp(17.0f));
        matrix.postRotate(-135.0f, (float) AndroidUtilities.dp(35.0f), (float) AndroidUtilities.dp(35.0f));
        imageView.setImageMatrix(matrix);
        this.acceptBtn = imageView;
        anonymousClass8.addView(imageView, LayoutHelper.createFrame(78, 78.0f, 83, 20.0f, 0.0f, 0.0f, 68.0f));
        ImageView imageView2 = new ImageView(this);
        FabBackgroundDrawable fabBackgroundDrawable2 = new FabBackgroundDrawable();
        fabBackgroundDrawable2.setColor(-1696188);
        imageView2.setBackgroundDrawable(fabBackgroundDrawable2);
        imageView2.setImageResource(NUM);
        imageView2.setScaleType(ScaleType.CENTER);
        this.declineBtn = imageView2;
        anonymousClass8.addView(imageView2, LayoutHelper.createFrame(78, 78.0f, 85, 0.0f, 0.0f, 20.0f, 68.0f));
        callSwipeView.setViewToDrag(imageView, false);
        callSwipeView2.setViewToDrag(imageView2, true);
        FrameLayout frameLayout3 = new FrameLayout(this);
        FabBackgroundDrawable fabBackgroundDrawable3 = new FabBackgroundDrawable();
        fabBackgroundDrawable3.setColor(-1696188);
        this.endBtnBg = fabBackgroundDrawable3;
        frameLayout3.setBackgroundDrawable(fabBackgroundDrawable3);
        ImageView imageView3 = new ImageView(this);
        imageView3.setImageResource(NUM);
        imageView3.setScaleType(ScaleType.CENTER);
        this.endBtnIcon = imageView3;
        frameLayout3.addView(imageView3, LayoutHelper.createFrame(70, 70.0f));
        frameLayout3.setForeground(getResources().getDrawable(NUM));
        frameLayout3.setContentDescription(LocaleController.getString("VoipEndCall", NUM));
        this.endBtn = frameLayout3;
        anonymousClass8.addView(frameLayout3, LayoutHelper.createFrame(78, 78.0f, 81, 0.0f, 0.0f, 0.0f, 68.0f));
        imageView = new ImageView(this);
        fabBackgroundDrawable3 = new FabBackgroundDrawable();
        fabBackgroundDrawable3.setColor(-1);
        imageView.setBackgroundDrawable(fabBackgroundDrawable3);
        imageView.setImageResource(NUM);
        imageView.setColorFilter(-NUM);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setVisibility(8);
        imageView.setContentDescription(LocaleController.getString("Cancel", NUM));
        this.cancelBtn = imageView;
        anonymousClass8.addView(imageView, LayoutHelper.createFrame(78, 78.0f, 83, 52.0f, 0.0f, 0.0f, 68.0f));
        this.emojiWrap = new LinearLayout(this);
        this.emojiWrap.setOrientation(0);
        this.emojiWrap.setClipToPadding(false);
        this.emojiWrap.setPivotX(0.0f);
        this.emojiWrap.setPivotY(0.0f);
        this.emojiWrap.setPadding(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(10.0f));
        int i = 0;
        while (i < 4) {
            imageView3 = new ImageView(this);
            imageView3.setScaleType(ScaleType.FIT_XY);
            this.emojiWrap.addView(imageView3, LayoutHelper.createLinear(22, 22, i == 0 ? 0.0f : 4.0f, 0.0f, 0.0f, 0.0f));
            this.keyEmojiViews[i] = imageView3;
            i++;
        }
        this.emojiWrap.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                VoIPActivity voIPActivity = VoIPActivity.this;
                if (voIPActivity.emojiTooltipVisible) {
                    voIPActivity.setEmojiTooltipVisible(false);
                    if (VoIPActivity.this.tooltipHider != null) {
                        VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
                        VoIPActivity.this.tooltipHider = null;
                    }
                }
                voIPActivity = VoIPActivity.this;
                voIPActivity.setEmojiExpanded(voIPActivity.emojiExpanded ^ 1);
            }
        });
        anonymousClass8.addView(this.emojiWrap, LayoutHelper.createFrame(-2, -2, (LocaleController.isRTL ? 3 : 5) | 48));
        this.emojiWrap.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {
                VoIPActivity voIPActivity = VoIPActivity.this;
                if (voIPActivity.emojiExpanded) {
                    return false;
                }
                if (voIPActivity.tooltipHider != null) {
                    VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
                    VoIPActivity.this.tooltipHider = null;
                }
                voIPActivity = VoIPActivity.this;
                voIPActivity.setEmojiTooltipVisible(voIPActivity.emojiTooltipVisible ^ 1);
                voIPActivity = VoIPActivity.this;
                if (voIPActivity.emojiTooltipVisible) {
                    voIPActivity.hintTextView.postDelayed(VoIPActivity.this.tooltipHider = new Runnable() {
                        public void run() {
                            VoIPActivity.this.tooltipHider = null;
                            VoIPActivity.this.setEmojiTooltipVisible(false);
                        }
                    }, 5000);
                }
                return true;
            }
        });
        this.emojiExpandedText = new TextView(this);
        this.emojiExpandedText.setTextSize(1, 16.0f);
        this.emojiExpandedText.setTextColor(-1);
        this.emojiExpandedText.setGravity(17);
        this.emojiExpandedText.setAlpha(0.0f);
        anonymousClass8.addView(this.emojiExpandedText, LayoutHelper.createFrame(-1, -2.0f, 17, 10.0f, 32.0f, 10.0f, 0.0f));
        this.hintTextView = new CorrectlyMeasuringTextView(this);
        this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), -NUM));
        this.hintTextView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        this.hintTextView.setTextSize(1, 14.0f);
        this.hintTextView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        this.hintTextView.setGravity(17);
        this.hintTextView.setMaxWidth(AndroidUtilities.dp(300.0f));
        this.hintTextView.setAlpha(0.0f);
        anonymousClass8.addView(this.hintTextView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 42.0f, 10.0f, 0.0f));
        int alpha = this.stateText.getPaint().getAlpha();
        this.ellAnimator = new AnimatorSet();
        AnimatorSet animatorSet = this.ellAnimator;
        Animator[] animatorArr = new Animator[6];
        int i2 = alpha;
        animatorArr[0] = createAlphaAnimator(this.ellSpans[0], 0, i2, 0, 300);
        animatorArr[1] = createAlphaAnimator(this.ellSpans[1], 0, i2, 150, 300);
        animatorArr[2] = createAlphaAnimator(this.ellSpans[2], 0, i2, 300, 300);
        int i3 = alpha;
        animatorArr[3] = createAlphaAnimator(this.ellSpans[0], i3, 0, 1000, 400);
        animatorArr[4] = createAlphaAnimator(this.ellSpans[1], i3, 0, 1000, 400);
        animatorArr[5] = createAlphaAnimator(this.ellSpans[2], i3, 0, 1000, 400);
        animatorSet.playTogether(animatorArr);
        this.ellAnimator.addListener(new AnimatorListenerAdapter() {
            private Runnable restarter = new Runnable() {
                public void run() {
                    if (!VoIPActivity.this.isFinishing()) {
                        VoIPActivity.this.ellAnimator.start();
                    }
                }
            };

            public void onAnimationEnd(Animator animator) {
                if (!VoIPActivity.this.isFinishing()) {
                    VoIPActivity.this.content.postDelayed(this.restarter, 300);
                }
            }
        });
        anonymousClass8.setClipChildren(false);
        this.content = anonymousClass8;
        return anonymousClass8;
    }

    @SuppressLint({"ObjectAnimatorBinding"})
    private ObjectAnimator createAlphaAnimator(Object obj, int i, int i2, int i3, int i4) {
        ObjectAnimator ofInt = ObjectAnimator.ofInt(obj, "alpha", new int[]{i, i2});
        ofInt.setDuration((long) i4);
        ofInt.setStartDelay((long) i3);
        ofInt.setInterpolator(CubicBezierInterpolator.DEFAULT);
        return ofInt;
    }

    /* Access modifiers changed, original: protected */
    public void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
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

    /* Access modifiers changed, original: protected */
    public void onResume() {
        super.onResume();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().onUIForegroundStateChanged(true);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onPause() {
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
            if (VoIPService.getSharedInstance() == null) {
                finish();
            } else if (iArr.length > 0 && iArr[0] == 0) {
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
            new IdenticonDrawable().setColors(new int[]{16777215, -1, -NUM, NUM});
            TL_encryptedChat tL_encryptedChat = new TL_encryptedChat();
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(VoIPService.getSharedInstance().getEncryptionKey());
                byteArrayOutputStream.write(VoIPService.getSharedInstance().getGA());
                tL_encryptedChat.auth_key = byteArrayOutputStream.toByteArray();
            } catch (Exception unused) {
            }
            byte[] bArr = tL_encryptedChat.auth_key;
            String[] emojifyForCall = EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(bArr, 0, bArr.length));
            LinearLayout linearLayout = this.emojiWrap;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(LocaleController.getString("EncryptionKey", NUM));
            String str = ", ";
            stringBuilder.append(str);
            stringBuilder.append(TextUtils.join(str, emojifyForCall));
            linearLayout.setContentDescription(stringBuilder.toString());
            for (int i = 0; i < 4; i++) {
                EmojiDrawable emojiDrawable = Emoji.getEmojiDrawable(emojifyForCall[i]);
                if (emojiDrawable != null) {
                    emojiDrawable.setBounds(0, 0, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f));
                    this.keyEmojiViews[i].setImageDrawable(emojiDrawable);
                }
            }
        }
    }

    private CharSequence getFormattedDebugString() {
        String debugString = VoIPService.getSharedInstance().getDebugString();
        SpannableString spannableString = new SpannableString(debugString);
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
            final LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(1);
            linearLayout.setBackgroundColor(-NUM);
            int dp = AndroidUtilities.dp(16.0f);
            int i = dp * 2;
            linearLayout.setPadding(dp, i, dp, i);
            TextView textView = new TextView(this);
            textView.setTextColor(-1);
            textView.setTextSize(1, 15.0f);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setGravity(17);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("libtgvoip v");
            stringBuilder.append(VoIPController.getVersion());
            textView.setText(stringBuilder.toString());
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 16.0f));
            ScrollView scrollView = new ScrollView(this);
            final TextView textView2 = new TextView(this);
            textView2.setTypeface(Typeface.MONOSPACE);
            textView2.setTextSize(1, 11.0f);
            textView2.setMaxWidth(AndroidUtilities.dp(350.0f));
            textView2.setTextColor(-1);
            textView2.setText(getFormattedDebugString());
            scrollView.addView(textView2);
            linearLayout.addView(scrollView, LayoutHelper.createLinear(-1, -1, 1.0f));
            textView = new TextView(this);
            textView.setBackgroundColor(-1);
            textView.setTextColor(-16777216);
            textView.setPadding(dp, dp, dp, dp);
            textView.setTextSize(1, 15.0f);
            textView.setText(LocaleController.getString("Close", NUM));
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
                    if (!VoIPActivity.this.isFinishing() && VoIPService.getSharedInstance() != null) {
                        textView2.setText(VoIPActivity.this.getFormattedDebugString());
                        linearLayout.postDelayed(this, 500);
                    }
                }
            }, 500);
        }
    }

    private void startUpdatingCallDuration() {
        new Runnable() {
            public void run() {
                if (!VoIPActivity.this.isFinishing() && VoIPService.getSharedInstance() != null) {
                    if (VoIPActivity.this.callState == 3 || VoIPActivity.this.callState == 5) {
                        VoIPActivity.this.durationText.setText(AndroidUtilities.formatShortDuration((int) (VoIPService.getSharedInstance().getCallDuration() / 1000)));
                        VoIPActivity.this.durationText.postDelayed(this, 500);
                    }
                }
            }
        }.run();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (!this.isIncomingWaiting || (i != 25 && i != 24)) {
            return super.onKeyDown(i, keyEvent);
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
        String str = "alpha";
        AnimatorSet animatorSet;
        AnimatorSet animatorSet2;
        if (this.didAcceptFromHere) {
            ObjectAnimator ofArgb;
            this.acceptBtn.setVisibility(8);
            String str2 = "color";
            if (VERSION.SDK_INT >= 21) {
                ofArgb = ObjectAnimator.ofArgb(this.endBtnBg, str2, new int[]{-12207027, -1696188});
            } else {
                ofArgb = ObjectAnimator.ofInt(this.endBtnBg, str2, new int[]{-12207027, -1696188});
                ofArgb.setEvaluator(new ArgbEvaluator());
            }
            animatorSet = new AnimatorSet();
            AnimatorSet animatorSet3 = new AnimatorSet();
            r14 = new Animator[2];
            r14[0] = ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[]{-135.0f, 0.0f});
            r14[1] = ofArgb;
            animatorSet3.playTogether(r14);
            animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet3.setDuration(500);
            animatorSet2 = new AnimatorSet();
            Animator[] animatorArr = new Animator[3];
            animatorArr[0] = ObjectAnimator.ofFloat(this.swipeViewsWrap, str, new float[]{1.0f, 0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.declineBtn, str, new float[]{0.0f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.accountNameText, str, new float[]{0.0f});
            animatorSet2.playTogether(animatorArr);
            animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_IN);
            animatorSet2.setDuration(125);
            animatorSet.playTogether(new Animator[]{animatorSet3, animatorSet2});
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
        animatorSet2 = new AnimatorSet();
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.bottomButtons, str, new float[]{0.0f, 1.0f})});
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.setDuration(500);
        AnimatorSet animatorSet4 = new AnimatorSet();
        r5 = new Animator[4];
        r5[1] = ObjectAnimator.ofFloat(this.declineBtn, str, new float[]{0.0f});
        r5[2] = ObjectAnimator.ofFloat(this.acceptBtn, str, new float[]{0.0f});
        r5[3] = ObjectAnimator.ofFloat(this.accountNameText, str, new float[]{0.0f});
        animatorSet4.playTogether(r5);
        animatorSet4.setInterpolator(CubicBezierInterpolator.EASE_IN);
        animatorSet4.setDuration(125);
        animatorSet2.playTogether(new Animator[]{animatorSet, animatorSet4});
        animatorSet2.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.swipeViewsWrap.setVisibility(8);
                VoIPActivity.this.declineBtn.setVisibility(8);
                VoIPActivity.this.acceptBtn.setVisibility(8);
                VoIPActivity.this.accountNameText.setVisibility(8);
            }
        });
        animatorSet2.start();
    }

    private void showRetry() {
        ObjectAnimator ofArgb;
        AnimatorSet animatorSet = this.retryAnim;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.endBtn.setEnabled(false);
        this.retrying = true;
        this.cancelBtn.setVisibility(0);
        this.cancelBtn.setAlpha(0.0f);
        AnimatorSet animatorSet2 = new AnimatorSet();
        String str = "color";
        if (VERSION.SDK_INT >= 21) {
            ofArgb = ObjectAnimator.ofArgb(this.endBtnBg, str, new int[]{-1696188, -12207027});
        } else {
            ofArgb = ObjectAnimator.ofInt(this.endBtnBg, str, new int[]{-1696188, -12207027});
            ofArgb.setEvaluator(new ArgbEvaluator());
        }
        Animator[] animatorArr = new Animator[4];
        animatorArr[0] = ObjectAnimator.ofFloat(this.cancelBtn, "alpha", new float[]{0.0f, 1.0f});
        animatorArr[1] = ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[]{0.0f, (float) (((this.content.getWidth() / 2) - AndroidUtilities.dp(52.0f)) - (this.endBtn.getWidth() / 2))});
        animatorArr[2] = ofArgb;
        animatorArr[3] = ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[]{0.0f, -135.0f});
        animatorSet2.playTogether(animatorArr);
        animatorSet2.setStartDelay(200);
        animatorSet2.setDuration(300);
        animatorSet2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet2.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.retryAnim = null;
                VoIPActivity.this.endBtn.setEnabled(true);
            }
        });
        this.retryAnim = animatorSet2;
        animatorSet2.start();
    }

    private void hideRetry() {
        ObjectAnimator ofArgb;
        AnimatorSet animatorSet = this.retryAnim;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.retrying = false;
        String str = "color";
        if (VERSION.SDK_INT >= 21) {
            ofArgb = ObjectAnimator.ofArgb(this.endBtnBg, str, new int[]{-12207027, -1696188});
        } else {
            ofArgb = ObjectAnimator.ofInt(this.endBtnBg, str, new int[]{-12207027, -1696188});
            ofArgb.setEvaluator(new ArgbEvaluator());
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        Animator[] animatorArr = new Animator[4];
        animatorArr[0] = ofArgb;
        animatorArr[1] = ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[]{-135.0f, 0.0f});
        animatorArr[2] = ObjectAnimator.ofFloat(this.endBtn, "translationX", new float[]{0.0f});
        animatorArr[3] = ObjectAnimator.ofFloat(this.cancelBtn, "alpha", new float[]{0.0f});
        animatorSet2.playTogether(animatorArr);
        animatorSet2.setStartDelay(200);
        animatorSet2.setDuration(300);
        animatorSet2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet2.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.cancelBtn.setVisibility(8);
                VoIPActivity.this.endBtn.setEnabled(true);
                VoIPActivity.this.retryAnim = null;
            }
        });
        this.retryAnim = animatorSet2;
        animatorSet2.start();
    }

    public void onStateChanged(final int i) {
        final int i2 = this.callState;
        this.callState = i;
        runOnUiThread(new Runnable() {
            public void run() {
                int i;
                boolean access$3300 = VoIPActivity.this.firstStateChange;
                String str = "VoipIncoming";
                if (VoIPActivity.this.firstStateChange) {
                    VoIPActivity.this.spkToggle.setChecked(((AudioManager) VoIPActivity.this.getSystemService("audio")).isSpeakerphoneOn());
                    if (VoIPActivity.this.isIncomingWaiting = i == 15) {
                        VoIPActivity.this.swipeViewsWrap.setVisibility(0);
                        VoIPActivity.this.endBtn.setVisibility(8);
                        VoIPActivity.this.acceptSwipe.startAnimatingArrows();
                        VoIPActivity.this.declineSwipe.startAnimatingArrows();
                        if (UserConfig.getActivatedAccountsCount() > 1) {
                            User currentUser = UserConfig.getInstance(VoIPActivity.this.currentAccount).getCurrentUser();
                            VoIPActivity.this.accountNameText.setText(LocaleController.formatString("VoipAnsweringAsAccount", NUM, ContactsController.formatName(currentUser.first_name, currentUser.last_name)));
                        } else {
                            VoIPActivity.this.accountNameText.setVisibility(8);
                        }
                        VoIPActivity.this.getWindow().addFlags(2097152);
                        VoIPService sharedInstance = VoIPService.getSharedInstance();
                        if (sharedInstance != null) {
                            sharedInstance.startRingtoneAndVibration();
                        }
                        VoIPActivity.this.setTitle(LocaleController.getString(str, NUM));
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
                if (VoIPActivity.this.isIncomingWaiting) {
                    i = i;
                    if (!(i == 15 || i == 11 || i == 10)) {
                        VoIPActivity.this.isIncomingWaiting = false;
                        if (!VoIPActivity.this.didAcceptFromHere) {
                            VoIPActivity.this.callAccepted();
                        }
                    }
                }
                i = i;
                int i2;
                if (i == 15) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString(str, NUM), false);
                    VoIPActivity.this.getWindow().addFlags(2097152);
                } else if (i == 1 || i == 2) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipConnecting", NUM), true);
                } else if (i == 12) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipExchangingKeys", NUM), true);
                } else if (i == 13) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipWaiting", NUM), true);
                } else if (i == 16) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRinging", NUM), true);
                } else if (i == 14) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRequesting", NUM), true);
                } else if (i == 10) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipHangingUp", NUM), true);
                    VoIPActivity.this.endBtnIcon.setAlpha(0.5f);
                    VoIPActivity.this.endBtn.setEnabled(false);
                } else if (i == 11) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipCallEnded", NUM), false);
                    VoIPActivity.this.stateText.postDelayed(new Runnable() {
                        public void run() {
                            VoIPActivity.this.finish();
                        }
                    }, 200);
                } else if (i == 17) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipBusy", NUM), false);
                    VoIPActivity.this.showRetry();
                } else if (i == 3 || i == 5) {
                    VoIPActivity.this.setTitle(null);
                    if (!access$3300 && i == 3) {
                        String str2 = "call_emoji_tooltip_count";
                        i2 = MessagesController.getGlobalMainSettings().getInt(str2, 0);
                        if (i2 < 3) {
                            VoIPActivity.this.setEmojiTooltipVisible(true);
                            VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.this.tooltipHider = new Runnable() {
                                public void run() {
                                    VoIPActivity.this.tooltipHider = null;
                                    VoIPActivity.this.setEmojiTooltipVisible(false);
                                }
                            }, 5000);
                            MessagesController.getGlobalMainSettings().edit().putInt(str2, i2 + 1).commit();
                        }
                    }
                    i2 = i2;
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
                } else if (i == 4) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipFailed", NUM), false);
                    i2 = VoIPService.getSharedInstance() != null ? VoIPService.getSharedInstance().getLastError() : 0;
                    if (i2 == 1) {
                        VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerIncompatible", NUM, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                    } else if (i2 == -1) {
                        VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerOutdated", NUM, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                    } else if (i2 == -2) {
                        VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", NUM, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                    } else if (i2 == 3) {
                        VoIPActivity.this.showErrorDialog("Error initializing audio hardware");
                    } else if (i2 == -3) {
                        VoIPActivity.this.finish();
                    } else if (i2 == -5) {
                        VoIPActivity.this.showErrorDialog(LocaleController.getString("VoipErrorUnknown", NUM));
                    } else {
                        VoIPActivity.this.stateText.postDelayed(new Runnable() {
                            public void run() {
                                VoIPActivity.this.finish();
                            }
                        }, 1000);
                    }
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
        AlertDialog show = new Builder(this).setTitle(LocaleController.getString("VoipFailed", NUM)).setMessage(charSequence).setPositiveButton(LocaleController.getString("OK", NUM), null).show();
        show.setCanceledOnTouchOutside(true);
        show.setOnDismissListener(new OnDismissListener() {
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
                    this.spkToggle.setImageResource(NUM);
                    this.spkToggle.setChecked(sharedInstance.isSpeakerphoneOn());
                } else if (sharedInstance.isBluetoothHeadsetConnected()) {
                    int currentAudioRoute = sharedInstance.getCurrentAudioRoute();
                    if (currentAudioRoute == 0) {
                        this.spkToggle.setImageResource(NUM);
                    } else if (currentAudioRoute == 1) {
                        this.spkToggle.setImageResource(NUM);
                    } else if (currentAudioRoute == 2) {
                        this.spkToggle.setImageResource(NUM);
                    }
                    this.spkToggle.setChecked(false);
                } else {
                    this.spkToggle.setImageResource(NUM);
                    this.spkToggle.setChecked(sharedInstance.isSpeakerphoneOn());
                }
            } else {
                this.spkToggle.setVisibility(4);
            }
        }
    }

    private void setStateTextAnimated(String str, boolean z) {
        if (!str.equals(this.lastStateText)) {
            CharSequence spannableStringBuilder;
            this.lastStateText = str;
            Animator animator = this.textChangingAnim;
            if (animator != null) {
                animator.cancel();
            }
            if (z) {
                if (!this.ellAnimator.isRunning()) {
                    this.ellAnimator.start();
                }
                spannableStringBuilder = new SpannableStringBuilder(str.toUpperCase());
                for (TextAlphaSpan alpha : this.ellSpans) {
                    alpha.setAlpha(0);
                }
                SpannableString spannableString = new SpannableString("...");
                spannableString.setSpan(this.ellSpans[0], 0, 1, 0);
                spannableString.setSpan(this.ellSpans[1], 1, 2, 0);
                spannableString.setSpan(this.ellSpans[2], 2, 3, 0);
                spannableStringBuilder.append(spannableString);
            } else {
                if (this.ellAnimator.isRunning()) {
                    this.ellAnimator.cancel();
                }
                spannableStringBuilder = str.toUpperCase();
            }
            this.stateText2.setText(spannableStringBuilder);
            this.stateText2.setVisibility(0);
            TextView textView = this.stateText;
            textView.setPivotX(LocaleController.isRTL ? (float) textView.getWidth() : 0.0f);
            textView = this.stateText;
            textView.setPivotY((float) (textView.getHeight() / 2));
            this.stateText2.setPivotX(LocaleController.isRTL ? (float) this.stateText.getWidth() : 0.0f);
            this.stateText2.setPivotY((float) (this.stateText.getHeight() / 2));
            this.durationText = this.stateText2;
            AnimatorSet animatorSet = new AnimatorSet();
            r13 = new Animator[8];
            String str2 = "alpha";
            r13[0] = ObjectAnimator.ofFloat(this.stateText2, str2, new float[]{0.0f, 1.0f});
            String str3 = "translationY";
            r13[1] = ObjectAnimator.ofFloat(this.stateText2, str3, new float[]{(float) (this.stateText.getHeight() / 2), 0.0f});
            String str4 = "scaleX";
            r13[2] = ObjectAnimator.ofFloat(this.stateText2, str4, new float[]{0.7f, 1.0f});
            String str5 = "scaleY";
            r13[3] = ObjectAnimator.ofFloat(this.stateText2, str5, new float[]{0.7f, 1.0f});
            r13[4] = ObjectAnimator.ofFloat(this.stateText, str2, new float[]{1.0f, 0.0f});
            r13[5] = ObjectAnimator.ofFloat(this.stateText, str3, new float[]{0.0f, (float) ((-this.stateText.getHeight()) / 2)});
            r13[6] = ObjectAnimator.ofFloat(this.stateText, str4, new float[]{1.0f, 0.7f});
            r13[7] = ObjectAnimator.ofFloat(this.stateText, str5, new float[]{1.0f, 0.7f});
            animatorSet.playTogether(r13);
            animatorSet.setDuration(200);
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    VoIPActivity.this.textChangingAnim = null;
                    VoIPActivity.this.stateText2.setVisibility(8);
                    VoIPActivity voIPActivity = VoIPActivity.this;
                    voIPActivity.durationText = voIPActivity.stateText;
                    VoIPActivity.this.stateText.setTranslationY(0.0f);
                    VoIPActivity.this.stateText.setScaleX(1.0f);
                    VoIPActivity.this.stateText.setScaleY(1.0f);
                    VoIPActivity.this.stateText.setAlpha(1.0f);
                    VoIPActivity.this.stateText.setText(VoIPActivity.this.stateText2.getText());
                }
            });
            this.textChangingAnim = animatorSet;
            animatorSet.start();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiDidLoad) {
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
        Animator animator = this.tooltipAnim;
        if (animator != null) {
            animator.cancel();
        }
        this.hintTextView.setVisibility(0);
        TextView textView = this.hintTextView;
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(textView, "alpha", fArr);
        ofFloat.setDuration(300);
        ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.tooltipAnim = null;
            }
        });
        this.tooltipAnim = ofFloat;
        ofFloat.start();
    }

    private void setEmojiExpanded(boolean z) {
        boolean z2 = z;
        if (this.emojiExpanded != z2) {
            this.emojiExpanded = z2;
            AnimatorSet animatorSet = this.emojiAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            String str = "scaleY";
            String str2 = "scaleX";
            String str3 = "translationY";
            String str4 = "translationX";
            String str5 = "alpha";
            AnimatorSet animatorSet2;
            if (z2) {
                int[] iArr = new int[]{0, 0};
                int[] iArr2 = new int[]{0, 0};
                this.emojiWrap.getLocationInWindow(iArr);
                this.emojiExpandedText.getLocationInWindow(iArr2);
                Rect rect = new Rect();
                getWindow().getDecorView().getGlobalVisibleRect(rect);
                int height = ((iArr2[1] - (iArr[1] + this.emojiWrap.getHeight())) - AndroidUtilities.dp(32.0f)) - this.emojiWrap.getHeight();
                int width = ((rect.width() / 2) - (Math.round(((float) this.emojiWrap.getWidth()) * 2.5f) / 2)) - iArr[0];
                animatorSet2 = new AnimatorSet();
                r8 = new Animator[7];
                r8[0] = ObjectAnimator.ofFloat(this.emojiWrap, str3, new float[]{(float) height});
                r8[1] = ObjectAnimator.ofFloat(this.emojiWrap, str4, new float[]{(float) width});
                r8[2] = ObjectAnimator.ofFloat(this.emojiWrap, str2, new float[]{2.5f});
                r8[3] = ObjectAnimator.ofFloat(this.emojiWrap, str, new float[]{2.5f});
                r8[4] = ObjectAnimator.ofFloat(this.blurOverlayView1, str5, new float[]{this.blurOverlayView1.getAlpha(), 1.0f, 1.0f});
                r8[5] = ObjectAnimator.ofFloat(this.blurOverlayView2, str5, new float[]{this.blurOverlayView2.getAlpha(), this.blurOverlayView2.getAlpha(), 1.0f});
                r8[6] = ObjectAnimator.ofFloat(this.emojiExpandedText, str5, new float[]{1.0f});
                animatorSet2.playTogether(r8);
                animatorSet2.setDuration(300);
                animatorSet2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.emojiAnimator = animatorSet2;
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        VoIPActivity.this.emojiAnimator = null;
                    }
                });
                animatorSet2.start();
            } else {
                animatorSet2 = new AnimatorSet();
                r2 = new Animator[7];
                r2[0] = ObjectAnimator.ofFloat(this.emojiWrap, str4, new float[]{0.0f});
                r2[1] = ObjectAnimator.ofFloat(this.emojiWrap, str3, new float[]{0.0f});
                r2[2] = ObjectAnimator.ofFloat(this.emojiWrap, str2, new float[]{1.0f});
                r2[3] = ObjectAnimator.ofFloat(this.emojiWrap, str, new float[]{1.0f});
                r2[4] = ObjectAnimator.ofFloat(this.blurOverlayView1, str5, new float[]{this.blurOverlayView1.getAlpha(), this.blurOverlayView1.getAlpha(), 0.0f});
                r2[5] = ObjectAnimator.ofFloat(this.blurOverlayView2, str5, new float[]{this.blurOverlayView2.getAlpha(), 0.0f, 0.0f});
                r2[6] = ObjectAnimator.ofFloat(this.emojiExpandedText, str5, new float[]{0.0f});
                animatorSet2.playTogether(r2);
                animatorSet2.setDuration(300);
                animatorSet2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.emojiAnimator = animatorSet2;
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        VoIPActivity.this.emojiAnimator = null;
                    }
                });
                animatorSet2.start();
            }
        }
    }

    private void updateBlurredPhotos(final BitmapHolder bitmapHolder) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Bitmap createBitmap = Bitmap.createBitmap(150, 150, Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    canvas.drawBitmap(bitmapHolder.bitmap, null, new Rect(0, 0, 150, 150), new Paint(2));
                    Utilities.blurBitmap(createBitmap, 3, 0, createBitmap.getWidth(), createBitmap.getHeight(), createBitmap.getRowBytes());
                    Palette generate = Palette.from(bitmapHolder.bitmap).generate();
                    Paint paint = new Paint();
                    paint.setColor((generate.getDarkMutedColor(-11242343) & 16777215) | NUM);
                    canvas.drawColor(NUM);
                    canvas.drawRect(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight(), paint);
                    Bitmap createBitmap2 = Bitmap.createBitmap(50, 50, Config.ARGB_8888);
                    Canvas canvas2 = new Canvas(createBitmap2);
                    canvas2.drawBitmap(bitmapHolder.bitmap, null, new Rect(0, 0, 50, 50), new Paint(2));
                    Utilities.blurBitmap(createBitmap2, 3, 0, createBitmap2.getWidth(), createBitmap2.getHeight(), createBitmap2.getRowBytes());
                    paint.setAlpha(102);
                    canvas2.drawRect(0.0f, 0.0f, (float) canvas2.getWidth(), (float) canvas2.getHeight(), paint);
                    VoIPActivity.this.blurredPhoto1 = createBitmap;
                    VoIPActivity.this.blurredPhoto2 = createBitmap2;
                    VoIPActivity.this.runOnUiThread(new -$$Lambda$VoIPActivity$28$O2Am6USxHnYzaezgtW-3ZQnLyo4(this, bitmapHolder));
                } catch (Throwable unused) {
                }
            }

            public /* synthetic */ void lambda$run$0$VoIPActivity$28(BitmapHolder bitmapHolder) {
                VoIPActivity.this.blurOverlayView1.setImageBitmap(VoIPActivity.this.blurredPhoto1);
                VoIPActivity.this.blurOverlayView2.setImageBitmap(VoIPActivity.this.blurredPhoto2);
                bitmapHolder.release();
            }
        }).start();
    }

    private void sendTextMessage(String str) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$VoIPActivity$QzixKI09iihAC1zzDghadBZbcCc(this, str));
    }

    public /* synthetic */ void lambda$sendTextMessage$3$VoIPActivity(String str) {
        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(str, (long) this.user.id, null, null, false, null, null, null, true, 0);
    }

    private void showMessagesSheet() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().stopRinging();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("mainconfig", 0);
        r4 = new String[4];
        r4[0] = sharedPreferences.getString("quick_reply_msg1", LocaleController.getString("QuickReplyDefault1", NUM));
        r4[1] = sharedPreferences.getString("quick_reply_msg2", LocaleController.getString("QuickReplyDefault2", NUM));
        r4[2] = sharedPreferences.getString("quick_reply_msg3", LocaleController.getString("QuickReplyDefault3", NUM));
        r4[3] = sharedPreferences.getString("quick_reply_msg4", LocaleController.getString("QuickReplyDefault4", NUM));
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        BottomSheet bottomSheet = new BottomSheet(this, true);
        if (VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(-13948117);
            bottomSheet.setOnDismissListener(new -$$Lambda$VoIPActivity$FNBtTABZY9SOl53qekJBUNNqq7k(this));
        }
        -$$Lambda$VoIPActivity$vv9zFeTuMzmokkSgVZMX6jmB-DQ -__lambda_voipactivity_vv9zfetumzmokksgvzmx6jmb-dq = new -$$Lambda$VoIPActivity$vv9zFeTuMzmokkSgVZMX6jmB-DQ(this, bottomSheet);
        for (CharSequence charSequence : r4) {
            BottomSheetCell bottomSheetCell = new BottomSheetCell(this, 0);
            bottomSheetCell.setTextAndIcon(charSequence, 0);
            bottomSheetCell.setTextColor(-1);
            bottomSheetCell.setTag(charSequence);
            bottomSheetCell.setOnClickListener(-__lambda_voipactivity_vv9zfetumzmokksgvzmx6jmb-dq);
            linearLayout.addView(bottomSheetCell);
        }
        FrameLayout frameLayout = new FrameLayout(this);
        BottomSheetCell bottomSheetCell2 = new BottomSheetCell(this, 0);
        String str = "QuickReplyCustom";
        bottomSheetCell2.setTextAndIcon(LocaleController.getString(str, NUM), 0);
        bottomSheetCell2.setTextColor(-1);
        frameLayout.addView(bottomSheetCell2);
        FrameLayout frameLayout2 = new FrameLayout(this);
        EditText editText = new EditText(this);
        editText.setTextSize(1, 16.0f);
        editText.setTextColor(-1);
        editText.setHintTextColor(DarkTheme.getColor("chat_messagePanelHint"));
        editText.setBackgroundDrawable(null);
        editText.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
        editText.setHint(LocaleController.getString(str, NUM));
        editText.setMinHeight(AndroidUtilities.dp(48.0f));
        editText.setGravity(80);
        editText.setMaxLines(4);
        editText.setSingleLine(false);
        editText.setInputType((editText.getInputType() | 16384) | 131072);
        frameLayout2.addView(editText, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 48.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 48.0f, 0.0f));
        final ImageView imageView = new ImageView(this);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setImageDrawable(DarkTheme.getThemedDrawable(this, NUM, "chat_messagePanelSend"));
        if (LocaleController.isRTL) {
            imageView.setScaleX(-0.1f);
        } else {
            imageView.setScaleX(0.1f);
        }
        imageView.setScaleY(0.1f);
        imageView.setAlpha(0.0f);
        frameLayout2.addView(imageView, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : 5) | 80));
        imageView.setOnClickListener(new -$$Lambda$VoIPActivity$qS8Hi4upiPbU6ePN9io5C_dQhsw(this, editText, bottomSheet));
        imageView.setVisibility(4);
        final ImageView imageView2 = new ImageView(this);
        imageView2.setScaleType(ScaleType.CENTER);
        imageView2.setImageDrawable(DarkTheme.getThemedDrawable(this, NUM, "chat_messagePanelIcons"));
        frameLayout2.addView(imageView2, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : 5) | 80));
        imageView2.setOnClickListener(new -$$Lambda$VoIPActivity$EkpRskxpSAZKneCZj3Wgdr5iMNs(this, frameLayout2, bottomSheetCell2, editText));
        editText.addTextChangedListener(new TextWatcher() {
            boolean prevState = false;

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                boolean z = editable.length() > 0;
                if (this.prevState != z) {
                    this.prevState = z;
                    if (z) {
                        imageView.setVisibility(0);
                        imageView.animate().alpha(1.0f).scaleX(LocaleController.isRTL ? -1.0f : 1.0f).scaleY(1.0f).setDuration(200).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                        imageView2.animate().alpha(0.0f).scaleX(0.1f).scaleY(0.1f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(200).withEndAction(new Runnable() {
                            public void run() {
                                imageView2.setVisibility(4);
                            }
                        }).start();
                        return;
                    }
                    imageView2.setVisibility(0);
                    imageView2.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    imageView.animate().alpha(0.0f).scaleX(LocaleController.isRTL ? -0.1f : 0.1f).scaleY(0.1f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(200).withEndAction(new Runnable() {
                        public void run() {
                            imageView.setVisibility(4);
                        }
                    }).start();
                }
            }
        });
        frameLayout2.setVisibility(8);
        frameLayout.addView(frameLayout2);
        bottomSheetCell2.setOnClickListener(new -$$Lambda$VoIPActivity$M5rvyVwU8mGC-6jzCkCqo_MOVmg(this, frameLayout2, bottomSheetCell2, editText));
        linearLayout.addView(frameLayout);
        bottomSheet.setCustomView(linearLayout);
        bottomSheet.setBackgroundColor(-13948117);
        bottomSheet.show();
    }

    public /* synthetic */ void lambda$showMessagesSheet$4$VoIPActivity(DialogInterface dialogInterface) {
        getWindow().setNavigationBarColor(0);
    }

    public /* synthetic */ void lambda$showMessagesSheet$6$VoIPActivity(BottomSheet bottomSheet, View view) {
        bottomSheet.dismiss();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().declineIncomingCall(4, new -$$Lambda$VoIPActivity$TZ1bdrRcWd888BWgOMWeIUKhz_Q(this, view));
        }
    }

    public /* synthetic */ void lambda$null$5$VoIPActivity(View view) {
        sendTextMessage((String) view.getTag());
    }

    public /* synthetic */ void lambda$showMessagesSheet$7$VoIPActivity(final EditText editText, BottomSheet bottomSheet, View view) {
        if (editText.length() != 0) {
            bottomSheet.dismiss();
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().declineIncomingCall(4, new Runnable() {
                    public void run() {
                        VoIPActivity.this.sendTextMessage(editText.getText().toString());
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$showMessagesSheet$8$VoIPActivity(FrameLayout frameLayout, BottomSheetCell bottomSheetCell, EditText editText, View view) {
        frameLayout.setVisibility(8);
        bottomSheetCell.setVisibility(0);
        editText.setText("");
        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public /* synthetic */ void lambda$showMessagesSheet$9$VoIPActivity(FrameLayout frameLayout, BottomSheetCell bottomSheetCell, EditText editText, View view) {
        frameLayout.setVisibility(0);
        bottomSheetCell.setVisibility(4);
        editText.requestFocus();
        ((InputMethodManager) getSystemService("input_method")).showSoftInput(editText, 0);
    }
}
