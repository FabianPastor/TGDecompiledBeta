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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.palette.graphics.Palette;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.EncryptionKeyEmojifier;
import org.telegram.messenger.voip.TgVoip;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_encryptedChat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.DarkAlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CorrectlyMeasuringTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.CallSwipeView;
import org.telegram.ui.Components.voip.CheckableImageView;
import org.telegram.ui.Components.voip.DarkTheme;
import org.telegram.ui.Components.voip.FabBackgroundDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.VoIPActivity;

public class VoIPActivity extends Activity implements VoIPBaseService.StateListener, NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public View acceptBtn;
    /* access modifiers changed from: private */
    public CallSwipeView acceptSwipe;
    /* access modifiers changed from: private */
    public TextView accountNameText;
    /* access modifiers changed from: private */
    public ImageView blurOverlayView1;
    /* access modifiers changed from: private */
    public ImageView blurOverlayView2;
    /* access modifiers changed from: private */
    public Bitmap blurredPhoto1;
    /* access modifiers changed from: private */
    public Bitmap blurredPhoto2;
    private LinearLayout bottomButtons;
    /* access modifiers changed from: private */
    public TextView brandingText;
    /* access modifiers changed from: private */
    public int callState;
    /* access modifiers changed from: private */
    public View cancelBtn;
    private ImageView chatBtn;
    /* access modifiers changed from: private */
    public FrameLayout content;
    /* access modifiers changed from: private */
    public Animator currentAcceptAnim;
    /* access modifiers changed from: private */
    public int currentAccount = -1;
    /* access modifiers changed from: private */
    public Animator currentDeclineAnim;
    /* access modifiers changed from: private */
    public View declineBtn;
    /* access modifiers changed from: private */
    public CallSwipeView declineSwipe;
    /* access modifiers changed from: private */
    public boolean didAcceptFromHere = false;
    /* access modifiers changed from: private */
    public TextView durationText;
    /* access modifiers changed from: private */
    public AnimatorSet ellAnimator;
    private TextAlphaSpan[] ellSpans;
    /* access modifiers changed from: private */
    public AnimatorSet emojiAnimator;
    boolean emojiExpanded;
    private TextView emojiExpandedText;
    boolean emojiTooltipVisible;
    /* access modifiers changed from: private */
    public LinearLayout emojiWrap;
    /* access modifiers changed from: private */
    public View endBtn;
    private FabBackgroundDrawable endBtnBg;
    /* access modifiers changed from: private */
    public View endBtnIcon;
    /* access modifiers changed from: private */
    public boolean firstStateChange = true;
    /* access modifiers changed from: private */
    public TextView hintTextView;
    /* access modifiers changed from: private */
    public boolean isIncomingWaiting;
    private ImageView[] keyEmojiViews = new ImageView[4];
    private String lastStateText;
    private CheckableImageView micToggle;
    /* access modifiers changed from: private */
    public TextView nameText;
    /* access modifiers changed from: private */
    public BackupImageView photoView;
    /* access modifiers changed from: private */
    public AnimatorSet retryAnim;
    /* access modifiers changed from: private */
    public boolean retrying;
    /* access modifiers changed from: private */
    public int signalBarsCount;
    private SignalBarsDrawable signalBarsDrawable;
    /* access modifiers changed from: private */
    public CheckableImageView spkToggle;
    /* access modifiers changed from: private */
    public TextView stateText;
    /* access modifiers changed from: private */
    public TextView stateText2;
    /* access modifiers changed from: private */
    public LinearLayout swipeViewsWrap;
    /* access modifiers changed from: private */
    public Animator textChangingAnim;
    /* access modifiers changed from: private */
    public Animator tooltipAnim;
    /* access modifiers changed from: private */
    public Runnable tooltipHider;
    /* access modifiers changed from: private */
    public TLRPC$User user;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        requestWindowFeature(1);
        getWindow().addFlags(524288);
        super.onCreate(bundle);
        if (VoIPService.getSharedInstance() == null) {
            finish();
            return;
        }
        int account = VoIPService.getSharedInstance().getAccount();
        this.currentAccount = account;
        if (account == -1) {
            finish();
            return;
        }
        if ((getResources().getConfiguration().screenLayout & 15) < 3) {
            setRequestedOrientation(1);
        }
        final View createContentView = createContentView();
        setContentView(createContentView);
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            getWindow().addFlags(Integer.MIN_VALUE);
            getWindow().setStatusBarColor(0);
            getWindow().setNavigationBarColor(0);
            getWindow().getDecorView().setSystemUiVisibility(1792);
        } else if (i >= 19) {
            getWindow().addFlags(NUM);
            getWindow().getDecorView().setSystemUiVisibility(1792);
        }
        TLRPC$User user2 = VoIPService.getSharedInstance().getUser();
        this.user = user2;
        if (user2.photo != null) {
            this.photoView.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate() {
                public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                    ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
                }

                public void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
                    ImageReceiver.BitmapHolder bitmapSafe = imageReceiver.getBitmapSafe();
                    if (bitmapSafe != null) {
                        VoIPActivity.this.updateBlurredPhotos(bitmapSafe);
                    }
                }
            });
            this.photoView.setImage(ImageLocation.getForUser(this.user, true), (String) null, (Drawable) new ColorDrawable(-16777216), (Object) this.user);
            this.photoView.setLayerType(2, (Paint) null);
        } else {
            this.photoView.setVisibility(8);
            BackgroundGradientDrawable backgroundGradientDrawable = new BackgroundGradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{-14994098, -14328963});
            backgroundGradientDrawable.startDithering(BackgroundGradientDrawable.Sizes.ofDeviceScreen(BackgroundGradientDrawable.Sizes.Orientation.PORTRAIT), new BackgroundGradientDrawable.ListenerAdapter(this) {
                public void onAllSizesReady() {
                    createContentView.invalidate();
                }
            });
            createContentView.setBackground(backgroundGradientDrawable);
        }
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setVolumeControlStream(0);
        this.nameText.setOnClickListener(new View.OnClickListener() {
            private int tapCount = 0;

            public void onClick(View view) {
                int i;
                if (BuildVars.DEBUG_VERSION || (i = this.tapCount) == 9) {
                    VoIPActivity.this.showDebugAlert();
                    this.tapCount = 0;
                    return;
                }
                this.tapCount = i + 1;
            }
        });
        this.endBtn.setOnClickListener(new View.OnClickListener() {
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
                            if (VoIPService.getSharedInstance() == null && !VoIPActivity.this.isFinishing()) {
                                VoIPActivity.this.endBtn.postDelayed(this, 100);
                            } else if (VoIPService.getSharedInstance() != null) {
                                VoIPService.getSharedInstance().registerStateListener(VoIPActivity.this);
                            }
                        }
                    }, 100);
                } else if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().hangUp();
                }
            }
        });
        this.spkToggle.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPActivity.this.lambda$onCreate$0$VoIPActivity(view);
            }
        });
        this.micToggle.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPActivity.this.lambda$onCreate$1$VoIPActivity(view);
            }
        });
        this.chatBtn.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                VoIPActivity.this.lambda$onCreate$2$VoIPActivity(view);
            }
        });
        this.spkToggle.setChecked(((AudioManager) getSystemService("audio")).isSpeakerphoneOn());
        this.micToggle.setChecked(VoIPService.getSharedInstance().isMicMute());
        onAudioSettingsChanged();
        TextView textView = this.nameText;
        TLRPC$User tLRPC$User = this.user;
        textView.setText(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name));
        VoIPService.getSharedInstance().registerStateListener(this);
        this.acceptSwipe.setListener(new CallSwipeView.Listener() {
            public void onDragComplete() {
                VoIPActivity.this.acceptSwipe.setEnabled(false);
                VoIPActivity.this.declineSwipe.setEnabled(false);
                if (VoIPService.getSharedInstance() == null) {
                    VoIPActivity.this.finish();
                    return;
                }
                boolean unused = VoIPActivity.this.didAcceptFromHere = true;
                if (Build.VERSION.SDK_INT < 23 || VoIPActivity.this.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
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
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(VoIPActivity.this.declineSwipe, View.ALPHA, new float[]{0.2f}), ObjectAnimator.ofFloat(VoIPActivity.this.declineBtn, View.ALPHA, new float[]{0.2f})});
                animatorSet.setDuration(200);
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        Animator unused = VoIPActivity.this.currentDeclineAnim = null;
                    }
                });
                Animator unused = VoIPActivity.this.currentDeclineAnim = animatorSet;
                animatorSet.start();
                VoIPActivity.this.declineSwipe.stopAnimatingArrows();
            }

            public void onDragCancel() {
                if (VoIPActivity.this.currentDeclineAnim != null) {
                    VoIPActivity.this.currentDeclineAnim.cancel();
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(VoIPActivity.this.declineSwipe, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(VoIPActivity.this.declineBtn, View.ALPHA, new float[]{1.0f})});
                animatorSet.setDuration(200);
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        Animator unused = VoIPActivity.this.currentDeclineAnim = null;
                    }
                });
                Animator unused = VoIPActivity.this.currentDeclineAnim = animatorSet;
                animatorSet.start();
                VoIPActivity.this.declineSwipe.startAnimatingArrows();
            }
        });
        this.declineSwipe.setListener(new CallSwipeView.Listener() {
            public void onDragComplete() {
                VoIPActivity.this.acceptSwipe.setEnabled(false);
                VoIPActivity.this.declineSwipe.setEnabled(false);
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().declineIncomingCall(4, (Runnable) null);
                } else {
                    VoIPActivity.this.finish();
                }
            }

            public void onDragStart() {
                if (VoIPActivity.this.currentAcceptAnim != null) {
                    VoIPActivity.this.currentAcceptAnim.cancel();
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, View.ALPHA, new float[]{0.2f}), ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, View.ALPHA, new float[]{0.2f})});
                animatorSet.setDuration(200);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        Animator unused = VoIPActivity.this.currentAcceptAnim = null;
                    }
                });
                Animator unused = VoIPActivity.this.currentAcceptAnim = animatorSet;
                animatorSet.start();
                VoIPActivity.this.acceptSwipe.stopAnimatingArrows();
            }

            public void onDragCancel() {
                if (VoIPActivity.this.currentAcceptAnim != null) {
                    VoIPActivity.this.currentAcceptAnim.cancel();
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, View.ALPHA, new float[]{1.0f})});
                animatorSet.setDuration(200);
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        Animator unused = VoIPActivity.this.currentAcceptAnim = null;
                    }
                });
                Animator unused = VoIPActivity.this.currentAcceptAnim = animatorSet;
                animatorSet.start();
                VoIPActivity.this.acceptSwipe.startAnimatingArrows();
            }
        });
        this.cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VoIPActivity.this.finish();
            }
        });
        getWindow().getDecorView().setKeepScreenOn(true);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeInCallActivity);
        this.hintTextView.setText(LocaleController.formatString("CallEmojiKeyTooltip", NUM, this.user.first_name));
        this.emojiExpandedText.setText(LocaleController.formatString("CallEmojiKeyTooltip", NUM, this.user.first_name));
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
        boolean z = !this.micToggle.isChecked();
        this.micToggle.setChecked(z);
        VoIPService.getSharedInstance().setMicMute(z);
    }

    public /* synthetic */ void lambda$onCreate$2$VoIPActivity(View view) {
        if (!this.isIncomingWaiting) {
            Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
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
        AnonymousClass9 r7 = new FrameLayout(this) {
            private void setNegativeMargins(Rect rect, FrameLayout.LayoutParams layoutParams) {
                layoutParams.topMargin = -rect.top;
                layoutParams.bottomMargin = -rect.bottom;
                layoutParams.leftMargin = -rect.left;
                layoutParams.rightMargin = -rect.right;
            }

            /* access modifiers changed from: protected */
            public boolean fitSystemWindows(Rect rect) {
                setNegativeMargins(rect, (FrameLayout.LayoutParams) VoIPActivity.this.photoView.getLayoutParams());
                setNegativeMargins(rect, (FrameLayout.LayoutParams) VoIPActivity.this.blurOverlayView1.getLayoutParams());
                setNegativeMargins(rect, (FrameLayout.LayoutParams) VoIPActivity.this.blurOverlayView2.getLayoutParams());
                return super.fitSystemWindows(rect);
            }
        };
        r7.setBackgroundColor(0);
        r7.setFitsSystemWindows(true);
        r7.setClipToPadding(false);
        AnonymousClass10 r0 = new BackupImageView(this, this) {
            private Drawable bottomGradient = getResources().getDrawable(NUM);
            private Paint paint = new Paint();
            private Drawable topGradient = getResources().getDrawable(NUM);

            /* access modifiers changed from: protected */
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
        this.photoView = r0;
        r7.addView(r0);
        ImageView imageView = new ImageView(this);
        this.blurOverlayView1 = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.blurOverlayView1.setAlpha(0.0f);
        r7.addView(this.blurOverlayView1);
        ImageView imageView2 = new ImageView(this);
        this.blurOverlayView2 = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.blurOverlayView2.setAlpha(0.0f);
        r7.addView(this.blurOverlayView2);
        TextView textView = new TextView(this);
        textView.setTextColor(-NUM);
        textView.setText(LocaleController.getString("VoipInCallBranding", NUM));
        Drawable mutate = getResources().getDrawable(NUM).mutate();
        mutate.setAlpha(204);
        mutate.setBounds(0, 0, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f));
        SignalBarsDrawable signalBarsDrawable2 = new SignalBarsDrawable();
        this.signalBarsDrawable = signalBarsDrawable2;
        signalBarsDrawable2.setBounds(0, 0, signalBarsDrawable2.getIntrinsicWidth(), this.signalBarsDrawable.getIntrinsicHeight());
        Drawable drawable = LocaleController.isRTL ? this.signalBarsDrawable : mutate;
        if (!LocaleController.isRTL) {
            mutate = this.signalBarsDrawable;
        }
        textView.setCompoundDrawables(drawable, (Drawable) null, mutate, (Drawable) null);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setGravity(LocaleController.isRTL ? 5 : 3);
        textView.setCompoundDrawablePadding(AndroidUtilities.dp(5.0f));
        textView.setTextSize(1, 14.0f);
        r7.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 18.0f, 18.0f, 18.0f, 0.0f));
        this.brandingText = textView;
        TextView textView2 = new TextView(this);
        textView2.setSingleLine();
        textView2.setTextColor(-1);
        textView2.setTextSize(1, 40.0f);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setGravity(LocaleController.isRTL ? 5 : 3);
        textView2.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        textView2.setTypeface(Typeface.create("sans-serif-light", 0));
        this.nameText = textView2;
        r7.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 43.0f, 18.0f, 0.0f));
        TextView textView3 = new TextView(this);
        textView3.setTextColor(-NUM);
        textView3.setSingleLine();
        textView3.setEllipsize(TextUtils.TruncateAt.END);
        textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView3.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        textView3.setTextSize(1, 15.0f);
        textView3.setGravity(LocaleController.isRTL ? 5 : 3);
        this.stateText = textView3;
        r7.addView(textView3, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        this.durationText = textView3;
        TextView textView4 = new TextView(this);
        textView4.setTextColor(-NUM);
        textView4.setSingleLine();
        textView4.setEllipsize(TextUtils.TruncateAt.END);
        textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView4.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        textView4.setTextSize(1, 15.0f);
        textView4.setGravity(LocaleController.isRTL ? 5 : 3);
        textView4.setVisibility(8);
        this.stateText2 = textView4;
        r7.addView(textView4, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        this.ellSpans = new TextAlphaSpan[]{new TextAlphaSpan(), new TextAlphaSpan(), new TextAlphaSpan()};
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        r7.addView(linearLayout, LayoutHelper.createFrame(-1, -2, 80));
        TextView textView5 = new TextView(this);
        textView5.setTextColor(-NUM);
        textView5.setSingleLine();
        textView5.setEllipsize(TextUtils.TruncateAt.END);
        textView5.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        textView5.setTextSize(1, 15.0f);
        textView5.setGravity(LocaleController.isRTL ? 5 : 3);
        this.accountNameText = textView5;
        r7.addView(textView5, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 120.0f, 18.0f, 0.0f));
        CheckableImageView checkableImageView = new CheckableImageView(this);
        checkableImageView.setBackgroundResource(NUM);
        Drawable mutate2 = getResources().getDrawable(NUM).mutate();
        checkableImageView.setAlpha(204);
        checkableImageView.setImageDrawable(mutate2);
        checkableImageView.setScaleType(ImageView.ScaleType.CENTER);
        checkableImageView.setContentDescription(LocaleController.getString("AccDescrMuteMic", NUM));
        FrameLayout frameLayout = new FrameLayout(this);
        this.micToggle = checkableImageView;
        frameLayout.addView(checkableImageView, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(0, -2, 1.0f));
        this.chatBtn = new ImageView(this);
        Drawable mutate3 = getResources().getDrawable(NUM).mutate();
        mutate3.setAlpha(204);
        this.chatBtn.setImageDrawable(mutate3);
        this.chatBtn.setScaleType(ImageView.ScaleType.CENTER);
        this.chatBtn.setContentDescription(LocaleController.getString("AccDescrOpenChat", NUM));
        FrameLayout frameLayout2 = new FrameLayout(this);
        frameLayout2.addView(this.chatBtn, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        linearLayout.addView(frameLayout2, LayoutHelper.createLinear(0, -2, 1.0f));
        CheckableImageView checkableImageView2 = new CheckableImageView(this);
        checkableImageView2.setBackgroundResource(NUM);
        Drawable mutate4 = getResources().getDrawable(NUM).mutate();
        checkableImageView2.setAlpha(204);
        checkableImageView2.setImageDrawable(mutate4);
        checkableImageView2.setScaleType(ImageView.ScaleType.CENTER);
        checkableImageView2.setContentDescription(LocaleController.getString("VoipAudioRoutingSpeaker", NUM));
        FrameLayout frameLayout3 = new FrameLayout(this);
        this.spkToggle = checkableImageView2;
        frameLayout3.addView(checkableImageView2, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        linearLayout.addView(frameLayout3, LayoutHelper.createLinear(0, -2, 1.0f));
        this.bottomButtons = linearLayout;
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        CallSwipeView callSwipeView = new CallSwipeView(this);
        callSwipeView.setColor(-12207027);
        callSwipeView.setContentDescription(LocaleController.getString("Accept", NUM));
        this.acceptSwipe = callSwipeView;
        linearLayout2.addView(callSwipeView, LayoutHelper.createLinear(-1, 70, 1.0f, 4, 4, -35, 4));
        CallSwipeView callSwipeView2 = new CallSwipeView(this);
        callSwipeView2.setColor(-1696188);
        callSwipeView2.setContentDescription(LocaleController.getString("Decline", NUM));
        this.declineSwipe = callSwipeView2;
        linearLayout2.addView(callSwipeView2, LayoutHelper.createLinear(-1, 70, 1.0f, -35, 4, 4, 4));
        this.swipeViewsWrap = linearLayout2;
        r7.addView(linearLayout2, LayoutHelper.createFrame(-1, -2.0f, 80, 20.0f, 0.0f, 20.0f, 68.0f));
        ImageView imageView3 = new ImageView(this);
        FabBackgroundDrawable fabBackgroundDrawable = new FabBackgroundDrawable();
        fabBackgroundDrawable.setColor(-12207027);
        imageView3.setBackgroundDrawable(fabBackgroundDrawable);
        imageView3.setImageResource(NUM);
        imageView3.setScaleType(ImageView.ScaleType.MATRIX);
        Matrix matrix = new Matrix();
        matrix.setTranslate((float) AndroidUtilities.dp(17.0f), (float) AndroidUtilities.dp(17.0f));
        matrix.postRotate(-135.0f, (float) AndroidUtilities.dp(35.0f), (float) AndroidUtilities.dp(35.0f));
        imageView3.setImageMatrix(matrix);
        this.acceptBtn = imageView3;
        r7.addView(imageView3, LayoutHelper.createFrame(78, 78.0f, 83, 20.0f, 0.0f, 0.0f, 68.0f));
        ImageView imageView4 = new ImageView(this);
        FabBackgroundDrawable fabBackgroundDrawable2 = new FabBackgroundDrawable();
        fabBackgroundDrawable2.setColor(-1696188);
        imageView4.setBackgroundDrawable(fabBackgroundDrawable2);
        imageView4.setImageResource(NUM);
        imageView4.setScaleType(ImageView.ScaleType.CENTER);
        this.declineBtn = imageView4;
        r7.addView(imageView4, LayoutHelper.createFrame(78, 78.0f, 85, 0.0f, 0.0f, 20.0f, 68.0f));
        callSwipeView.setViewToDrag(imageView3, false);
        callSwipeView2.setViewToDrag(imageView4, true);
        FrameLayout frameLayout4 = new FrameLayout(this);
        FabBackgroundDrawable fabBackgroundDrawable3 = new FabBackgroundDrawable();
        fabBackgroundDrawable3.setColor(-1696188);
        this.endBtnBg = fabBackgroundDrawable3;
        frameLayout4.setBackgroundDrawable(fabBackgroundDrawable3);
        ImageView imageView5 = new ImageView(this);
        imageView5.setImageResource(NUM);
        imageView5.setScaleType(ImageView.ScaleType.CENTER);
        this.endBtnIcon = imageView5;
        frameLayout4.addView(imageView5, LayoutHelper.createFrame(70, 70.0f));
        frameLayout4.setForeground(getResources().getDrawable(NUM));
        frameLayout4.setContentDescription(LocaleController.getString("VoipEndCall", NUM));
        this.endBtn = frameLayout4;
        r7.addView(frameLayout4, LayoutHelper.createFrame(78, 78.0f, 81, 0.0f, 0.0f, 0.0f, 68.0f));
        ImageView imageView6 = new ImageView(this);
        FabBackgroundDrawable fabBackgroundDrawable4 = new FabBackgroundDrawable();
        fabBackgroundDrawable4.setColor(-1);
        imageView6.setBackgroundDrawable(fabBackgroundDrawable4);
        imageView6.setImageResource(NUM);
        imageView6.setColorFilter(-NUM);
        imageView6.setScaleType(ImageView.ScaleType.CENTER);
        imageView6.setVisibility(8);
        imageView6.setContentDescription(LocaleController.getString("Cancel", NUM));
        this.cancelBtn = imageView6;
        r7.addView(imageView6, LayoutHelper.createFrame(78, 78.0f, 83, 52.0f, 0.0f, 0.0f, 68.0f));
        LinearLayout linearLayout3 = new LinearLayout(this);
        this.emojiWrap = linearLayout3;
        linearLayout3.setOrientation(0);
        this.emojiWrap.setClipToPadding(false);
        this.emojiWrap.setPivotX(0.0f);
        this.emojiWrap.setPivotY(0.0f);
        this.emojiWrap.setPadding(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(10.0f));
        int i = 0;
        while (i < 4) {
            ImageView imageView7 = new ImageView(this);
            imageView7.setScaleType(ImageView.ScaleType.FIT_XY);
            this.emojiWrap.addView(imageView7, LayoutHelper.createLinear(22, 22, i == 0 ? 0.0f : 4.0f, 0.0f, 0.0f, 0.0f));
            this.keyEmojiViews[i] = imageView7;
            i++;
        }
        this.emojiWrap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VoIPActivity voIPActivity = VoIPActivity.this;
                if (voIPActivity.emojiTooltipVisible) {
                    voIPActivity.setEmojiTooltipVisible(false);
                    if (VoIPActivity.this.tooltipHider != null) {
                        VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
                        Runnable unused = VoIPActivity.this.tooltipHider = null;
                    }
                }
                VoIPActivity voIPActivity2 = VoIPActivity.this;
                voIPActivity2.setEmojiExpanded(!voIPActivity2.emojiExpanded);
            }
        });
        r7.addView(this.emojiWrap, LayoutHelper.createFrame(-2, -2, (LocaleController.isRTL ? 3 : 5) | 48));
        this.emojiWrap.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                VoIPActivity voIPActivity = VoIPActivity.this;
                if (voIPActivity.emojiExpanded) {
                    return false;
                }
                if (voIPActivity.tooltipHider != null) {
                    VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
                    Runnable unused = VoIPActivity.this.tooltipHider = null;
                }
                VoIPActivity voIPActivity2 = VoIPActivity.this;
                voIPActivity2.setEmojiTooltipVisible(!voIPActivity2.emojiTooltipVisible);
                VoIPActivity voIPActivity3 = VoIPActivity.this;
                if (voIPActivity3.emojiTooltipVisible) {
                    TextView access$2200 = voIPActivity3.hintTextView;
                    VoIPActivity voIPActivity4 = VoIPActivity.this;
                    AnonymousClass1 r2 = new Runnable() {
                        public void run() {
                            Runnable unused = VoIPActivity.this.tooltipHider = null;
                            VoIPActivity.this.setEmojiTooltipVisible(false);
                        }
                    };
                    Runnable unused2 = voIPActivity4.tooltipHider = r2;
                    access$2200.postDelayed(r2, 5000);
                }
                return true;
            }
        });
        TextView textView6 = new TextView(this);
        this.emojiExpandedText = textView6;
        textView6.setTextSize(1, 16.0f);
        this.emojiExpandedText.setTextColor(-1);
        this.emojiExpandedText.setGravity(17);
        this.emojiExpandedText.setAlpha(0.0f);
        r7.addView(this.emojiExpandedText, LayoutHelper.createFrame(-1, -2.0f, 17, 10.0f, 32.0f, 10.0f, 0.0f));
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(this);
        this.hintTextView = correctlyMeasuringTextView;
        correctlyMeasuringTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), -NUM));
        this.hintTextView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        this.hintTextView.setTextSize(1, 14.0f);
        this.hintTextView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        this.hintTextView.setGravity(17);
        this.hintTextView.setMaxWidth(AndroidUtilities.dp(300.0f));
        this.hintTextView.setAlpha(0.0f);
        r7.addView(this.hintTextView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 42.0f, 10.0f, 0.0f));
        int alpha = this.stateText.getPaint().getAlpha();
        AnimatorSet animatorSet = new AnimatorSet();
        this.ellAnimator = animatorSet;
        int i2 = alpha;
        int i3 = alpha;
        animatorSet.playTogether(new Animator[]{createAlphaAnimator(this.ellSpans[0], 0, i2, 0, 300), createAlphaAnimator(this.ellSpans[1], 0, i2, 150, 300), createAlphaAnimator(this.ellSpans[2], 0, i2, 300, 300), createAlphaAnimator(this.ellSpans[0], i3, 0, 1000, 400), createAlphaAnimator(this.ellSpans[1], i3, 0, 1000, 400), createAlphaAnimator(this.ellSpans[2], i3, 0, 1000, 400)});
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
        r7.setClipChildren(false);
        this.content = r7;
        return r7;
    }

    @SuppressLint({"ObjectAnimatorBinding"})
    private ObjectAnimator createAlphaAnimator(Object obj, int i, int i2, int i3, int i4) {
        ObjectAnimator ofInt = ObjectAnimator.ofInt(obj, "alpha", new int[]{i, i2});
        ofInt.setDuration((long) i4);
        ofInt.setStartDelay((long) i3);
        ofInt.setInterpolator(CubicBezierInterpolator.DEFAULT);
        return ofInt;
    }

    /* access modifiers changed from: protected */
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
        } else if (!this.isIncomingWaiting) {
            super.onBackPressed();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().onUIForegroundStateChanged(true);
        }
    }

    /* access modifiers changed from: protected */
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
        if (i != 101) {
            return;
        }
        if (VoIPService.getSharedInstance() == null) {
            finish();
        } else if (iArr.length > 0 && iArr[0] == 0) {
            VoIPService.getSharedInstance().acceptIncomingCall();
            callAccepted();
        } else if (!shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
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

    /* access modifiers changed from: private */
    public void updateKeyView() {
        if (VoIPService.getSharedInstance() != null) {
            new IdenticonDrawable().setColors(new int[]{16777215, -1, -NUM, NUM});
            TLRPC$TL_encryptedChat tLRPC$TL_encryptedChat = new TLRPC$TL_encryptedChat();
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(VoIPService.getSharedInstance().getEncryptionKey());
                byteArrayOutputStream.write(VoIPService.getSharedInstance().getGA());
                tLRPC$TL_encryptedChat.auth_key = byteArrayOutputStream.toByteArray();
            } catch (Exception unused) {
            }
            byte[] bArr = tLRPC$TL_encryptedChat.auth_key;
            String[] emojifyForCall = EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(bArr, 0, bArr.length));
            LinearLayout linearLayout = this.emojiWrap;
            linearLayout.setContentDescription(LocaleController.getString("EncryptionKey", NUM) + ", " + TextUtils.join(", ", emojifyForCall));
            for (int i = 0; i < 4; i++) {
                Emoji.EmojiDrawable emojiDrawable = Emoji.getEmojiDrawable(emojifyForCall[i]);
                if (emojiDrawable != null) {
                    emojiDrawable.setBounds(0, 0, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f));
                    this.keyEmojiViews[i].setImageDrawable(emojiDrawable);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public CharSequence getFormattedDebugString() {
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
                spannableString.setSpan(new ForegroundColorSpan(-NUM), i, substring.indexOf(58) + i + 1, 0);
            }
            i = debugString.indexOf(10, i2);
        } while (i != -1);
        return spannableString;
    }

    /* access modifiers changed from: private */
    public void showDebugAlert() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().forceRating();
            final LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(1);
            linearLayout.setBackgroundColor(-NUM);
            int dp = AndroidUtilities.dp(16.0f);
            int i = dp * 2;
            linearLayout.setPadding(dp, i, dp, i);
            final TextView textView = new TextView(this);
            textView.setTextColor(-1);
            textView.setTextSize(1, 15.0f);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setGravity(17);
            textView.setText(getDebugTitle());
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
            TextView textView3 = new TextView(this);
            textView3.setBackgroundColor(-1);
            textView3.setTextColor(-16777216);
            textView3.setPadding(dp, dp, dp, dp);
            textView3.setTextSize(1, 15.0f);
            textView3.setText(LocaleController.getString("Close", NUM));
            linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
            final WindowManager windowManager = (WindowManager) getSystemService("window");
            windowManager.addView(linearLayout, new WindowManager.LayoutParams(-1, -1, 1000, 0, -3));
            textView3.setOnClickListener(new View.OnClickListener(this) {
                public void onClick(View view) {
                    windowManager.removeView(linearLayout);
                }
            });
            linearLayout.postDelayed(new Runnable() {
                public void run() {
                    if (!VoIPActivity.this.isFinishing() && VoIPService.getSharedInstance() != null) {
                        textView.setText(VoIPActivity.this.getDebugTitle());
                        textView2.setText(VoIPActivity.this.getFormattedDebugString());
                        linearLayout.postDelayed(this, 500);
                    }
                }
            }, 500);
        }
    }

    /* access modifiers changed from: private */
    public String getDebugTitle() {
        String version = TgVoip.getVersion();
        if (version == null) {
            return "libtgvoip";
        }
        return "libtgvoip" + " v" + version;
    }

    /* access modifiers changed from: private */
    public void startUpdatingCallDuration() {
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
            return true;
        }
        finish();
        return true;
    }

    /* access modifiers changed from: private */
    public void callAccepted() {
        ObjectAnimator objectAnimator;
        this.endBtn.setVisibility(0);
        if (VoIPService.getSharedInstance().hasEarpiece()) {
            this.spkToggle.setVisibility(0);
        } else {
            this.spkToggle.setVisibility(8);
        }
        this.bottomButtons.setVisibility(0);
        if (this.didAcceptFromHere) {
            this.acceptBtn.setVisibility(8);
            if (Build.VERSION.SDK_INT >= 21) {
                objectAnimator = ObjectAnimator.ofArgb(this.endBtnBg, "color", new int[]{-12207027, -1696188});
            } else {
                objectAnimator = ObjectAnimator.ofInt(this.endBtnBg, "color", new int[]{-12207027, -1696188});
                objectAnimator.setEvaluator(new ArgbEvaluator());
            }
            AnimatorSet animatorSet = new AnimatorSet();
            AnimatorSet animatorSet2 = new AnimatorSet();
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.endBtnIcon, View.ROTATION, new float[]{-135.0f, 0.0f}), objectAnimator});
            animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet2.setDuration(500);
            AnimatorSet animatorSet3 = new AnimatorSet();
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.swipeViewsWrap, View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.declineBtn, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.accountNameText, View.ALPHA, new float[]{0.0f})});
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
        AnimatorSet animatorSet4 = new AnimatorSet();
        AnimatorSet animatorSet5 = new AnimatorSet();
        animatorSet5.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.bottomButtons, View.ALPHA, new float[]{0.0f, 1.0f})});
        animatorSet5.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet5.setDuration(500);
        AnimatorSet animatorSet6 = new AnimatorSet();
        animatorSet6.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.swipeViewsWrap, View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(this.declineBtn, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.acceptBtn, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.accountNameText, View.ALPHA, new float[]{0.0f})});
        animatorSet6.setInterpolator(CubicBezierInterpolator.EASE_IN);
        animatorSet6.setDuration(125);
        animatorSet4.playTogether(new Animator[]{animatorSet5, animatorSet6});
        animatorSet4.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.swipeViewsWrap.setVisibility(8);
                VoIPActivity.this.declineBtn.setVisibility(8);
                VoIPActivity.this.acceptBtn.setVisibility(8);
                VoIPActivity.this.accountNameText.setVisibility(8);
            }
        });
        animatorSet4.start();
    }

    /* access modifiers changed from: private */
    public void showRetry() {
        ObjectAnimator objectAnimator;
        AnimatorSet animatorSet = this.retryAnim;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.endBtn.setEnabled(false);
        this.retrying = true;
        this.cancelBtn.setVisibility(0);
        this.cancelBtn.setAlpha(0.0f);
        AnimatorSet animatorSet2 = new AnimatorSet();
        if (Build.VERSION.SDK_INT >= 21) {
            objectAnimator = ObjectAnimator.ofArgb(this.endBtnBg, "color", new int[]{-1696188, -12207027});
        } else {
            objectAnimator = ObjectAnimator.ofInt(this.endBtnBg, "color", new int[]{-1696188, -12207027});
            objectAnimator.setEvaluator(new ArgbEvaluator());
        }
        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.cancelBtn, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.endBtn, View.TRANSLATION_X, new float[]{0.0f, (float) (((this.content.getWidth() / 2) - AndroidUtilities.dp(52.0f)) - (this.endBtn.getWidth() / 2))}), objectAnimator, ObjectAnimator.ofFloat(this.endBtnIcon, View.ROTATION, new float[]{0.0f, -135.0f})});
        animatorSet2.setStartDelay(200);
        animatorSet2.setDuration(300);
        animatorSet2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet2.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AnimatorSet unused = VoIPActivity.this.retryAnim = null;
                VoIPActivity.this.endBtn.setEnabled(true);
            }
        });
        this.retryAnim = animatorSet2;
        animatorSet2.start();
    }

    /* access modifiers changed from: private */
    public void hideRetry() {
        ObjectAnimator objectAnimator;
        AnimatorSet animatorSet = this.retryAnim;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.retrying = false;
        if (Build.VERSION.SDK_INT >= 21) {
            objectAnimator = ObjectAnimator.ofArgb(this.endBtnBg, "color", new int[]{-12207027, -1696188});
        } else {
            objectAnimator = ObjectAnimator.ofInt(this.endBtnBg, "color", new int[]{-12207027, -1696188});
            objectAnimator.setEvaluator(new ArgbEvaluator());
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playTogether(new Animator[]{objectAnimator, ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[]{-135.0f, 0.0f}), ObjectAnimator.ofFloat(this.endBtn, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.cancelBtn, View.ALPHA, new float[]{0.0f})});
        animatorSet2.setStartDelay(200);
        animatorSet2.setDuration(300);
        animatorSet2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet2.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                VoIPActivity.this.cancelBtn.setVisibility(8);
                VoIPActivity.this.endBtn.setEnabled(true);
                AnimatorSet unused = VoIPActivity.this.retryAnim = null;
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
                String str;
                int i2;
                boolean access$3400 = VoIPActivity.this.firstStateChange;
                if (VoIPActivity.this.firstStateChange) {
                    VoIPActivity.this.spkToggle.setChecked(((AudioManager) VoIPActivity.this.getSystemService("audio")).isSpeakerphoneOn());
                    VoIPActivity voIPActivity = VoIPActivity.this;
                    boolean z = i == 15;
                    boolean unused = voIPActivity.isIncomingWaiting = z;
                    if (z) {
                        VoIPActivity.this.swipeViewsWrap.setVisibility(0);
                        VoIPActivity.this.endBtn.setVisibility(8);
                        VoIPActivity.this.acceptSwipe.startAnimatingArrows();
                        VoIPActivity.this.declineSwipe.startAnimatingArrows();
                        if (UserConfig.getActivatedAccountsCount() > 1) {
                            TLRPC$User currentUser = UserConfig.getInstance(VoIPActivity.this.currentAccount).getCurrentUser();
                            VoIPActivity.this.accountNameText.setText(LocaleController.formatString("VoipAnsweringAsAccount", NUM, ContactsController.formatName(currentUser.first_name, currentUser.last_name)));
                        } else {
                            VoIPActivity.this.accountNameText.setVisibility(8);
                        }
                        VoIPActivity.this.getWindow().addFlags(2097152);
                        VoIPService sharedInstance = VoIPService.getSharedInstance();
                        if (sharedInstance != null) {
                            sharedInstance.startRingtoneAndVibration();
                        }
                        VoIPActivity.this.setTitle(LocaleController.getString("VoipIncoming", NUM));
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
                    boolean unused2 = VoIPActivity.this.firstStateChange = false;
                }
                if (!(!VoIPActivity.this.isIncomingWaiting || (i2 = i) == 15 || i2 == 11 || i2 == 10)) {
                    boolean unused3 = VoIPActivity.this.isIncomingWaiting = false;
                    if (!VoIPActivity.this.didAcceptFromHere) {
                        VoIPActivity.this.callAccepted();
                    }
                }
                int i3 = i;
                if (i3 == 15) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipIncoming", NUM), false);
                    VoIPActivity.this.getWindow().addFlags(2097152);
                } else if (i3 == 1 || i3 == 2) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipConnecting", NUM), true);
                } else if (i3 == 12) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipExchangingKeys", NUM), true);
                } else if (i3 == 13) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipWaiting", NUM), true);
                } else if (i3 == 16) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRinging", NUM), true);
                } else if (i3 == 14) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRequesting", NUM), true);
                } else if (i3 == 10) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipHangingUp", NUM), true);
                    VoIPActivity.this.endBtnIcon.setAlpha(0.5f);
                    VoIPActivity.this.endBtn.setEnabled(false);
                } else if (i3 == 11) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipCallEnded", NUM), false);
                    VoIPActivity.this.stateText.postDelayed(new Runnable() {
                        public void run() {
                            VoIPActivity.this.finish();
                        }
                    }, 200);
                } else if (i3 == 17) {
                    VoIPActivity.this.endBtn.setContentDescription(LocaleController.getString("CallAgain", NUM));
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipBusy", NUM), false);
                    VoIPActivity.this.showRetry();
                } else if (i3 == 3 || i3 == 5) {
                    VoIPActivity.this.setTitle((CharSequence) null);
                    if (!access$3400 && i == 3 && (i = MessagesController.getGlobalMainSettings().getInt("call_emoji_tooltip_count", 0)) < 3) {
                        VoIPActivity.this.setEmojiTooltipVisible(true);
                        TextView access$2200 = VoIPActivity.this.hintTextView;
                        VoIPActivity voIPActivity2 = VoIPActivity.this;
                        AnonymousClass2 r10 = new Runnable() {
                            public void run() {
                                Runnable unused = VoIPActivity.this.tooltipHider = null;
                                VoIPActivity.this.setEmojiTooltipVisible(false);
                            }
                        };
                        Runnable unused4 = voIPActivity2.tooltipHider = r10;
                        access$2200.postDelayed(r10, 5000);
                        MessagesController.getGlobalMainSettings().edit().putInt("call_emoji_tooltip_count", i + 1).commit();
                    }
                    int i4 = i2;
                    if (!(i4 == 3 || i4 == 5)) {
                        VoIPActivity.this.setStateTextAnimated("0:00", false);
                        VoIPActivity.this.startUpdatingCallDuration();
                        VoIPActivity.this.updateKeyView();
                        if (VoIPActivity.this.emojiWrap.getVisibility() != 0) {
                            VoIPActivity.this.emojiWrap.setVisibility(0);
                            VoIPActivity.this.emojiWrap.setAlpha(0.0f);
                            VoIPActivity.this.emojiWrap.animate().alpha(1.0f).setDuration(200).setInterpolator(new DecelerateInterpolator()).start();
                        }
                    }
                } else if (i3 == 4) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipFailed", NUM), false);
                    VoIPService sharedInstance2 = VoIPService.getSharedInstance();
                    if (sharedInstance2 != null) {
                        str = sharedInstance2.getLastError();
                    } else {
                        str = "ERROR_UNKNOWN";
                    }
                    if (TextUtils.equals(str, "ERROR_UNKNOWN")) {
                        VoIPActivity.this.stateText.postDelayed(new Runnable() {
                            public final void run() {
                                VoIPActivity.AnonymousClass22.this.lambda$run$1$VoIPActivity$22();
                            }
                        }, 1000);
                    } else if (TextUtils.equals(str, "ERROR_INCOMPATIBLE")) {
                        VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerIncompatible", NUM, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                    } else if (TextUtils.equals(str, "ERROR_PEER_OUTDATED")) {
                        VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("VoipPeerOutdated", NUM, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                    } else if (TextUtils.equals(str, "ERROR_PRIVACY")) {
                        VoIPActivity.this.showErrorDialog(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", NUM, ContactsController.formatName(VoIPActivity.this.user.first_name, VoIPActivity.this.user.last_name))));
                    } else if (TextUtils.equals(str, "ERROR_AUDIO_IO")) {
                        VoIPActivity.this.showErrorDialog("Error initializing audio hardware");
                    } else if (TextUtils.equals(str, "ERROR_LOCALIZED")) {
                        VoIPActivity.this.finish();
                    } else if (TextUtils.equals(str, "ERROR_CONNECTION_SERVICE")) {
                        VoIPActivity.this.showErrorDialog(LocaleController.getString("VoipErrorUnknown", NUM));
                    } else {
                        VoIPActivity.this.stateText.postDelayed(new Runnable() {
                            public final void run() {
                                VoIPActivity.AnonymousClass22.this.lambda$run$0$VoIPActivity$22();
                            }
                        }, 1000);
                    }
                }
                VoIPActivity.this.brandingText.invalidate();
            }

            public /* synthetic */ void lambda$run$0$VoIPActivity$22() {
                VoIPActivity.this.finish();
            }

            public /* synthetic */ void lambda$run$1$VoIPActivity$22() {
                VoIPActivity.this.finish();
            }
        });
    }

    public void onSignalBarsCountChanged(final int i) {
        runOnUiThread(new Runnable() {
            public void run() {
                int unused = VoIPActivity.this.signalBarsCount = i;
                VoIPActivity.this.brandingText.invalidate();
            }
        });
    }

    /* access modifiers changed from: private */
    public void showErrorDialog(CharSequence charSequence) {
        DarkAlertDialog.Builder builder = new DarkAlertDialog.Builder(this);
        builder.setTitle(LocaleController.getString("VoipFailed", NUM));
        builder.setMessage(charSequence);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog show = builder.show();
        show.setCanceledOnTouchOutside(true);
        show.setOnDismissListener(new DialogInterface.OnDismissListener() {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v30, resolved type: android.text.SpannableStringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v33, resolved type: java.lang.String} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setStateTextAnimated(java.lang.String r10, boolean r11) {
        /*
            r9 = this;
            java.lang.String r0 = r9.lastStateText
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x0009
            return
        L_0x0009:
            r9.lastStateText = r10
            android.animation.Animator r0 = r9.textChangingAnim
            if (r0 == 0) goto L_0x0012
            r0.cancel()
        L_0x0012:
            r0 = 3
            r1 = 1
            r2 = 0
            r3 = 2
            if (r11 == 0) goto L_0x005c
            android.animation.AnimatorSet r11 = r9.ellAnimator
            boolean r11 = r11.isRunning()
            if (r11 != 0) goto L_0x0025
            android.animation.AnimatorSet r11 = r9.ellAnimator
            r11.start()
        L_0x0025:
            android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
            java.lang.String r10 = r10.toUpperCase()
            r11.<init>(r10)
            org.telegram.ui.VoIPActivity$TextAlphaSpan[] r10 = r9.ellSpans
            int r4 = r10.length
            r5 = 0
        L_0x0032:
            if (r5 >= r4) goto L_0x003c
            r6 = r10[r5]
            r6.setAlpha(r2)
            int r5 = r5 + 1
            goto L_0x0032
        L_0x003c:
            android.text.SpannableString r10 = new android.text.SpannableString
            java.lang.String r4 = "..."
            r10.<init>(r4)
            org.telegram.ui.VoIPActivity$TextAlphaSpan[] r4 = r9.ellSpans
            r4 = r4[r2]
            r10.setSpan(r4, r2, r1, r2)
            org.telegram.ui.VoIPActivity$TextAlphaSpan[] r4 = r9.ellSpans
            r4 = r4[r1]
            r10.setSpan(r4, r1, r3, r2)
            org.telegram.ui.VoIPActivity$TextAlphaSpan[] r4 = r9.ellSpans
            r4 = r4[r3]
            r10.setSpan(r4, r3, r0, r2)
            r11.append(r10)
            goto L_0x006d
        L_0x005c:
            android.animation.AnimatorSet r11 = r9.ellAnimator
            boolean r11 = r11.isRunning()
            if (r11 == 0) goto L_0x0069
            android.animation.AnimatorSet r11 = r9.ellAnimator
            r11.cancel()
        L_0x0069:
            java.lang.String r11 = r10.toUpperCase()
        L_0x006d:
            android.widget.TextView r10 = r9.stateText2
            r10.setText(r11)
            android.widget.TextView r10 = r9.stateText2
            r10.setVisibility(r2)
            android.widget.TextView r10 = r9.stateText
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            r4 = 0
            if (r11 == 0) goto L_0x0084
            int r11 = r10.getWidth()
            float r11 = (float) r11
            goto L_0x0085
        L_0x0084:
            r11 = 0
        L_0x0085:
            r10.setPivotX(r11)
            android.widget.TextView r10 = r9.stateText
            int r11 = r10.getHeight()
            int r11 = r11 / r3
            float r11 = (float) r11
            r10.setPivotY(r11)
            android.widget.TextView r10 = r9.stateText2
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 == 0) goto L_0x00a1
            android.widget.TextView r11 = r9.stateText
            int r11 = r11.getWidth()
            float r11 = (float) r11
            goto L_0x00a2
        L_0x00a1:
            r11 = 0
        L_0x00a2:
            r10.setPivotX(r11)
            android.widget.TextView r10 = r9.stateText2
            android.widget.TextView r11 = r9.stateText
            int r11 = r11.getHeight()
            int r11 = r11 / r3
            float r11 = (float) r11
            r10.setPivotY(r11)
            android.widget.TextView r10 = r9.stateText2
            r9.durationText = r10
            android.animation.AnimatorSet r10 = new android.animation.AnimatorSet
            r10.<init>()
            r11 = 8
            android.animation.Animator[] r11 = new android.animation.Animator[r11]
            android.widget.TextView r5 = r9.stateText2
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r3]
            r7 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r11[r2] = r5
            android.widget.TextView r5 = r9.stateText2
            android.util.Property r6 = android.view.View.TRANSLATION_Y
            float[] r7 = new float[r3]
            android.widget.TextView r8 = r9.stateText
            int r8 = r8.getHeight()
            int r8 = r8 / r3
            float r8 = (float) r8
            r7[r2] = r8
            r7[r1] = r4
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r11[r1] = r5
            android.widget.TextView r5 = r9.stateText2
            android.util.Property r6 = android.view.View.SCALE_X
            float[] r7 = new float[r3]
            r7 = {NUM, NUM} // fill-array
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r11[r3] = r5
            android.widget.TextView r5 = r9.stateText2
            android.util.Property r6 = android.view.View.SCALE_Y
            float[] r7 = new float[r3]
            r7 = {NUM, NUM} // fill-array
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r11[r0] = r5
            r0 = 4
            android.widget.TextView r5 = r9.stateText
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r3]
            r7 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r11[r0] = r5
            r0 = 5
            android.widget.TextView r5 = r9.stateText
            android.util.Property r6 = android.view.View.TRANSLATION_Y
            float[] r7 = new float[r3]
            r7[r2] = r4
            int r2 = r5.getHeight()
            int r2 = -r2
            int r2 = r2 / r3
            float r2 = (float) r2
            r7[r1] = r2
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r11[r0] = r1
            r0 = 6
            android.widget.TextView r1 = r9.stateText
            android.util.Property r2 = android.view.View.SCALE_X
            float[] r4 = new float[r3]
            r4 = {NUM, NUM} // fill-array
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r1, r2, r4)
            r11[r0] = r1
            r0 = 7
            android.widget.TextView r1 = r9.stateText
            android.util.Property r2 = android.view.View.SCALE_Y
            float[] r3 = new float[r3]
            r3 = {NUM, NUM} // fill-array
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r1, r2, r3)
            r11[r0] = r1
            r10.playTogether(r11)
            r0 = 200(0xc8, double:9.9E-322)
            r10.setDuration(r0)
            org.telegram.ui.Components.CubicBezierInterpolator r11 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r10.setInterpolator(r11)
            org.telegram.ui.VoIPActivity$25 r11 = new org.telegram.ui.VoIPActivity$25
            r11.<init>()
            r10.addListener(r11)
            r9.textChangingAnim = r10
            r10.start()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.VoIPActivity.setStateTextAnimated(java.lang.String, boolean):void");
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

    /* access modifiers changed from: private */
    public void setEmojiTooltipVisible(boolean z) {
        this.emojiTooltipVisible = z;
        Animator animator = this.tooltipAnim;
        if (animator != null) {
            animator.cancel();
        }
        this.hintTextView.setVisibility(0);
        TextView textView = this.hintTextView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(textView, property, fArr);
        ofFloat.setDuration(300);
        ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                Animator unused = VoIPActivity.this.tooltipAnim = null;
            }
        });
        this.tooltipAnim = ofFloat;
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    public void setEmojiExpanded(boolean z) {
        boolean z2 = z;
        if (this.emojiExpanded != z2) {
            this.emojiExpanded = z2;
            AnimatorSet animatorSet = this.emojiAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            if (z2) {
                int[] iArr = {0, 0};
                int[] iArr2 = {0, 0};
                this.emojiWrap.getLocationInWindow(iArr);
                this.emojiExpandedText.getLocationInWindow(iArr2);
                Rect rect = new Rect();
                getWindow().getDecorView().getGlobalVisibleRect(rect);
                int height = ((iArr2[1] - (iArr[1] + this.emojiWrap.getHeight())) - AndroidUtilities.dp(32.0f)) - this.emojiWrap.getHeight();
                int width = ((rect.width() / 2) - (Math.round(((float) this.emojiWrap.getWidth()) * 2.5f) / 2)) - iArr[0];
                AnimatorSet animatorSet2 = new AnimatorSet();
                ImageView imageView = this.blurOverlayView1;
                Property property = View.ALPHA;
                float[] fArr = {imageView.getAlpha(), 1.0f, 1.0f};
                ImageView imageView2 = this.blurOverlayView2;
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiWrap, View.TRANSLATION_Y, new float[]{(float) height}), ObjectAnimator.ofFloat(this.emojiWrap, View.TRANSLATION_X, new float[]{(float) width}), ObjectAnimator.ofFloat(this.emojiWrap, View.SCALE_X, new float[]{2.5f}), ObjectAnimator.ofFloat(this.emojiWrap, View.SCALE_Y, new float[]{2.5f}), ObjectAnimator.ofFloat(imageView, property, fArr), ObjectAnimator.ofFloat(imageView2, View.ALPHA, new float[]{imageView2.getAlpha(), this.blurOverlayView2.getAlpha(), 1.0f}), ObjectAnimator.ofFloat(this.emojiExpandedText, View.ALPHA, new float[]{1.0f})});
                animatorSet2.setDuration(300);
                animatorSet2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.emojiAnimator = animatorSet2;
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = VoIPActivity.this.emojiAnimator = null;
                    }
                });
                animatorSet2.start();
                return;
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            ImageView imageView3 = this.blurOverlayView1;
            Property property2 = View.ALPHA;
            float[] fArr2 = {imageView3.getAlpha(), this.blurOverlayView1.getAlpha(), 0.0f};
            ImageView imageView4 = this.blurOverlayView2;
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.emojiWrap, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiWrap, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.emojiWrap, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.emojiWrap, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(imageView3, property2, fArr2), ObjectAnimator.ofFloat(imageView4, View.ALPHA, new float[]{imageView4.getAlpha(), 0.0f, 0.0f}), ObjectAnimator.ofFloat(this.emojiExpandedText, View.ALPHA, new float[]{0.0f})});
            animatorSet3.setDuration(300);
            animatorSet3.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.emojiAnimator = animatorSet3;
            animatorSet3.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = VoIPActivity.this.emojiAnimator = null;
                }
            });
            animatorSet3.start();
        }
    }

    /* access modifiers changed from: private */
    public void updateBlurredPhotos(final ImageReceiver.BitmapHolder bitmapHolder) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Bitmap createBitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);
                    canvas.drawBitmap(bitmapHolder.bitmap, (Rect) null, new Rect(0, 0, 150, 150), new Paint(2));
                    Utilities.blurBitmap(createBitmap, 3, 0, createBitmap.getWidth(), createBitmap.getHeight(), createBitmap.getRowBytes());
                    Palette generate = Palette.from(bitmapHolder.bitmap).generate();
                    Paint paint = new Paint();
                    paint.setColor((generate.getDarkMutedColor(-11242343) & 16777215) | NUM);
                    canvas.drawColor(NUM);
                    canvas.drawRect(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight(), paint);
                    Bitmap createBitmap2 = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                    Canvas canvas2 = new Canvas(createBitmap2);
                    canvas2.drawBitmap(bitmapHolder.bitmap, (Rect) null, new Rect(0, 0, 50, 50), new Paint(2));
                    Utilities.blurBitmap(createBitmap2, 3, 0, createBitmap2.getWidth(), createBitmap2.getHeight(), createBitmap2.getRowBytes());
                    paint.setAlpha(102);
                    canvas2.drawRect(0.0f, 0.0f, (float) canvas2.getWidth(), (float) canvas2.getHeight(), paint);
                    Bitmap unused = VoIPActivity.this.blurredPhoto1 = createBitmap;
                    Bitmap unused2 = VoIPActivity.this.blurredPhoto2 = createBitmap2;
                    VoIPActivity.this.runOnUiThread(new Runnable(bitmapHolder) {
                        public final /* synthetic */ ImageReceiver.BitmapHolder f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            VoIPActivity.AnonymousClass29.this.lambda$run$0$VoIPActivity$29(this.f$1);
                        }
                    });
                } catch (Throwable unused3) {
                }
            }

            public /* synthetic */ void lambda$run$0$VoIPActivity$29(ImageReceiver.BitmapHolder bitmapHolder) {
                VoIPActivity.this.blurOverlayView1.setImageBitmap(VoIPActivity.this.blurredPhoto1);
                VoIPActivity.this.blurOverlayView2.setImageBitmap(VoIPActivity.this.blurredPhoto2);
                bitmapHolder.release();
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public void sendTextMessage(String str) {
        AndroidUtilities.runOnUIThread(new Runnable(str) {
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VoIPActivity.this.lambda$sendTextMessage$3$VoIPActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$sendTextMessage$3$VoIPActivity(String str) {
        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(str, (long) this.user.id, (MessageObject) null, (TLRPC$WebPage) null, false, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
    }

    private void showMessagesSheet() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().stopRinging();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("mainconfig", 0);
        String[] strArr = {sharedPreferences.getString("quick_reply_msg1", LocaleController.getString("QuickReplyDefault1", NUM)), sharedPreferences.getString("quick_reply_msg2", LocaleController.getString("QuickReplyDefault2", NUM)), sharedPreferences.getString("quick_reply_msg3", LocaleController.getString("QuickReplyDefault3", NUM)), sharedPreferences.getString("quick_reply_msg4", LocaleController.getString("QuickReplyDefault4", NUM))};
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        BottomSheet bottomSheet = new BottomSheet(this, true);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(-13948117);
            bottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    VoIPActivity.this.lambda$showMessagesSheet$4$VoIPActivity(dialogInterface);
                }
            });
        }
        $$Lambda$VoIPActivity$vv9zFeTuMzmokkSgVZMX6jmBDQ r8 = new View.OnClickListener(bottomSheet) {
            public final /* synthetic */ BottomSheet f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                VoIPActivity.this.lambda$showMessagesSheet$6$VoIPActivity(this.f$1, view);
            }
        };
        for (int i = 0; i < 4; i++) {
            String str = strArr[i];
            BottomSheet.BottomSheetCell bottomSheetCell = new BottomSheet.BottomSheetCell(this, 0);
            bottomSheetCell.setTextAndIcon((CharSequence) str, 0);
            bottomSheetCell.setTextColor(-1);
            bottomSheetCell.setTag(str);
            bottomSheetCell.setOnClickListener(r8);
            linearLayout.addView(bottomSheetCell);
        }
        FrameLayout frameLayout = new FrameLayout(this);
        BottomSheet.BottomSheetCell bottomSheetCell2 = new BottomSheet.BottomSheetCell(this, 0);
        bottomSheetCell2.setTextAndIcon((CharSequence) LocaleController.getString("QuickReplyCustom", NUM), 0);
        bottomSheetCell2.setTextColor(-1);
        frameLayout.addView(bottomSheetCell2);
        FrameLayout frameLayout2 = new FrameLayout(this);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(this);
        editTextBoldCursor.setTextSize(1, 16.0f);
        editTextBoldCursor.setTextColor(-1);
        editTextBoldCursor.setHintTextColor(DarkTheme.getColor("chat_messagePanelHint"));
        editTextBoldCursor.setBackgroundDrawable((Drawable) null);
        editTextBoldCursor.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
        editTextBoldCursor.setHint(LocaleController.getString("QuickReplyCustom", NUM));
        editTextBoldCursor.setMinHeight(AndroidUtilities.dp(48.0f));
        editTextBoldCursor.setGravity(80);
        editTextBoldCursor.setMaxLines(4);
        editTextBoldCursor.setSingleLine(false);
        editTextBoldCursor.setInputType(editTextBoldCursor.getInputType() | 16384 | 131072);
        frameLayout2.addView(editTextBoldCursor, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 48.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 48.0f, 0.0f));
        final ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageDrawable(DarkTheme.getThemedDrawable(this, NUM, "chat_messagePanelSend"));
        if (LocaleController.isRTL) {
            imageView.setScaleX(-0.1f);
        } else {
            imageView.setScaleX(0.1f);
        }
        imageView.setScaleY(0.1f);
        imageView.setAlpha(0.0f);
        frameLayout2.addView(imageView, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : 5) | 80));
        imageView.setOnClickListener(new View.OnClickListener(editTextBoldCursor, bottomSheet) {
            public final /* synthetic */ EditTextBoldCursor f$1;
            public final /* synthetic */ BottomSheet f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(View view) {
                VoIPActivity.this.lambda$showMessagesSheet$7$VoIPActivity(this.f$1, this.f$2, view);
            }
        });
        imageView.setVisibility(4);
        final ImageView imageView2 = new ImageView(this);
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        imageView2.setImageDrawable(DarkTheme.getThemedDrawable(this, NUM, "chat_messagePanelIcons"));
        frameLayout2.addView(imageView2, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? 3 : 5) | 80));
        imageView2.setOnClickListener(new View.OnClickListener(frameLayout2, bottomSheetCell2, editTextBoldCursor) {
            public final /* synthetic */ FrameLayout f$1;
            public final /* synthetic */ BottomSheet.BottomSheetCell f$2;
            public final /* synthetic */ EditTextBoldCursor f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onClick(View view) {
                VoIPActivity.this.lambda$showMessagesSheet$8$VoIPActivity(this.f$1, this.f$2, this.f$3, view);
            }
        });
        editTextBoldCursor.addTextChangedListener(new TextWatcher(this) {
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
        bottomSheetCell2.setOnClickListener(new View.OnClickListener(frameLayout2, bottomSheetCell2, editTextBoldCursor) {
            public final /* synthetic */ FrameLayout f$1;
            public final /* synthetic */ BottomSheet.BottomSheetCell f$2;
            public final /* synthetic */ EditTextBoldCursor f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onClick(View view) {
                VoIPActivity.this.lambda$showMessagesSheet$9$VoIPActivity(this.f$1, this.f$2, this.f$3, view);
            }
        });
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
            VoIPService.getSharedInstance().declineIncomingCall(4, new Runnable(view) {
                public final /* synthetic */ View f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VoIPActivity.this.lambda$null$5$VoIPActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$5$VoIPActivity(View view) {
        sendTextMessage((String) view.getTag());
    }

    public /* synthetic */ void lambda$showMessagesSheet$7$VoIPActivity(final EditTextBoldCursor editTextBoldCursor, BottomSheet bottomSheet, View view) {
        if (editTextBoldCursor.length() != 0) {
            bottomSheet.dismiss();
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().declineIncomingCall(4, new Runnable() {
                    public void run() {
                        VoIPActivity.this.sendTextMessage(editTextBoldCursor.getText().toString());
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$showMessagesSheet$8$VoIPActivity(FrameLayout frameLayout, BottomSheet.BottomSheetCell bottomSheetCell, EditTextBoldCursor editTextBoldCursor, View view) {
        frameLayout.setVisibility(8);
        bottomSheetCell.setVisibility(0);
        editTextBoldCursor.setText("");
        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(editTextBoldCursor.getWindowToken(), 0);
    }

    public /* synthetic */ void lambda$showMessagesSheet$9$VoIPActivity(FrameLayout frameLayout, BottomSheet.BottomSheetCell bottomSheetCell, EditTextBoldCursor editTextBoldCursor, View view) {
        frameLayout.setVisibility(0);
        bottomSheetCell.setVisibility(4);
        editTextBoldCursor.requestFocus();
        ((InputMethodManager) getSystemService("input_method")).showSoftInput(editTextBoldCursor, 0);
    }

    private class TextAlphaSpan extends CharacterStyle {
        private int alpha = 0;

        public TextAlphaSpan() {
        }

        @Keep
        public int getAlpha() {
            return this.alpha;
        }

        @Keep
        public void setAlpha(int i) {
            this.alpha = i;
            VoIPActivity.this.stateText.invalidate();
            VoIPActivity.this.stateText2.invalidate();
        }

        public void updateDrawState(TextPaint textPaint) {
            textPaint.setAlpha(this.alpha);
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
                    this.rect.set((float) (AndroidUtilities.dp((float) (i2 * 4)) + dp), (float) ((getIntrinsicHeight() + i) - this.barHeights[i2]), (float) ((AndroidUtilities.dp(4.0f) * i2) + dp + AndroidUtilities.dp(3.0f)), (float) (getIntrinsicHeight() + i));
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
}
