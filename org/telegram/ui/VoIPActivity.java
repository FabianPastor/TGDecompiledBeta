package org.telegram.ui;

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
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.CharacterStyle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.ByteArrayOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.ExoPlayerFactory;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.voip.EncryptionKeyEmojifier;
import org.telegram.messenger.voip.VoIPController;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.messenger.voip.VoIPService.StateListener;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.CallSwipeView;
import org.telegram.ui.Components.voip.CallSwipeView.Listener;
import org.telegram.ui.Components.voip.CheckableImageView;
import org.telegram.ui.Components.voip.FabBackgroundDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPActivity extends Activity implements StateListener, NotificationCenterDelegate {
    private static final String TAG = "tg-voip-ui";
    private View acceptBtn;
    private CallSwipeView acceptSwipe;
    private int audioBitrate = 25;
    private int callState;
    private ImageView chatBtn;
    private Animator currentAcceptAnim;
    private Animator currentDeclineAnim;
    private View declineBtn;
    private CallSwipeView declineSwipe;
    private boolean didAcceptFromHere = false;
    private TextView durationText;
    private AnimatorSet ellAnimator;
    private TextAlphaSpan[] ellSpans;
    private AnimatorSet emojiAnimator;
    boolean emojiTooltipVisible;
    private View endBtn;
    private FabBackgroundDrawable endBtnBg;
    private View endBtnIcon;
    private boolean firstStateChange = true;
    private TextView hintTextView;
    private boolean isIncomingWaiting;
    private TextView keyEmojiText;
    private ImageView[] keyEmojiViews = new ImageView[5];
    private boolean keyEmojiVisible;
    private String lastStateText;
    private CheckableImageView micToggle;
    private TextView nameText;
    private int packetLossPercent = 5;
    private BackupImageView photoView;
    private CheckableImageView spkToggle;
    private TextView stateText;
    private TextView stateText2;
    private LinearLayout swipeViewsWrap;
    private Animator textChangingAnim;
    private Animator tooltipAnim;
    private Runnable tooltipHider;
    private User user;

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
        if ((getResources().getConfiguration().screenLayout & 15) < 3) {
            setRequestedOrientation(1);
        }
        View contentView = createContentView();
        setContentView(contentView);
        if (VERSION.SDK_INT >= 21) {
            getWindow().addFlags(Integer.MIN_VALUE);
            getWindow().setStatusBarColor(-16777216);
        }
        this.user = VoIPService.getSharedInstance().getUser();
        if (this.user.photo != null) {
            this.photoView.setImage(this.user.photo.photo_big, null, new ColorDrawable(-16777216));
        } else {
            this.photoView.setVisibility(8);
            contentView.setBackgroundDrawable(new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{-14994098, -14328963}));
        }
        setVolumeControlStream(0);
        this.nameText.setOnClickListener(new OnClickListener() {
            private int tapCount = 0;

            public void onClick(View v) {
                if (BuildVars.DEBUG_VERSION || this.tapCount == 9) {
                    VoIPActivity.this.showDebugAlert();
                    this.tapCount = 0;
                    return;
                }
                this.tapCount++;
            }
        });
        this.endBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                VoIPActivity.this.endBtn.setEnabled(false);
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().hangUp();
                }
            }
        });
        this.spkToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean checked = true;
                VoIPService svc = VoIPService.getSharedInstance();
                if (svc != null) {
                    if (svc.isBluetoothHeadsetConnected() && svc.hasEarpiece()) {
                        new Builder(VoIPActivity.this).setItems(new CharSequence[]{LocaleController.getString("VoipAudioRoutingBluetooth", R.string.VoipAudioRoutingBluetooth), LocaleController.getString("VoipAudioRoutingEarpiece", R.string.VoipAudioRoutingEarpiece), LocaleController.getString("VoipAudioRoutingSpeaker", R.string.VoipAudioRoutingSpeaker)}, new int[]{R.drawable.ic_bluetooth_white_24dp, R.drawable.ic_phone_in_talk_white_24dp, R.drawable.ic_volume_up_white_24dp}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                AudioManager am = (AudioManager) VoIPActivity.this.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                                if (VoIPService.getSharedInstance() != null) {
                                    switch (which) {
                                        case 0:
                                            am.setBluetoothScoOn(true);
                                            am.setSpeakerphoneOn(false);
                                            break;
                                        case 1:
                                            am.setBluetoothScoOn(false);
                                            am.setSpeakerphoneOn(false);
                                            break;
                                        case 2:
                                            am.setBluetoothScoOn(false);
                                            am.setSpeakerphoneOn(true);
                                            break;
                                    }
                                    VoIPActivity.this.onAudioSettingsChanged();
                                }
                            }
                        }).show();
                        return;
                    }
                    if (VoIPActivity.this.spkToggle.isChecked()) {
                        checked = false;
                    }
                    VoIPActivity.this.spkToggle.setChecked(checked);
                    AudioManager am = (AudioManager) VoIPActivity.this.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                    if (svc.hasEarpiece()) {
                        am.setSpeakerphoneOn(checked);
                    } else {
                        am.setBluetoothScoOn(checked);
                    }
                }
            }
        });
        this.micToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (VoIPService.getSharedInstance() == null) {
                    VoIPActivity.this.finish();
                    return;
                }
                boolean checked = !VoIPActivity.this.micToggle.isChecked();
                VoIPActivity.this.micToggle.setChecked(checked);
                VoIPService.getSharedInstance().setMicMute(checked);
            }
        });
        this.chatBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
                intent.setFlags(32768);
                intent.putExtra("userId", VoIPActivity.this.user.id);
                VoIPActivity.this.startActivity(intent);
                VoIPActivity.this.finish();
            }
        });
        this.spkToggle.setChecked(((AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO)).isSpeakerphoneOn());
        this.micToggle.setChecked(VoIPService.getSharedInstance().isMicMute());
        onAudioSettingsChanged();
        this.nameText.setText(ContactsController.formatName(this.user.first_name, this.user.last_name));
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
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        VoIPActivity.this.currentDeclineAnim = null;
                    }
                });
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
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        VoIPActivity.this.currentDeclineAnim = null;
                    }
                });
                VoIPActivity.this.currentDeclineAnim = set;
                set.start();
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
                AnimatorSet set = new AnimatorSet();
                r1 = new Animator[2];
                r1[0] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, "alpha", new float[]{0.2f});
                r1[1] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, "alpha", new float[]{0.2f});
                set.playTogether(r1);
                set.setDuration(200);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        VoIPActivity.this.currentAcceptAnim = null;
                    }
                });
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
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        VoIPActivity.this.currentAcceptAnim = null;
                    }
                });
                VoIPActivity.this.currentAcceptAnim = set;
                set.start();
                VoIPActivity.this.acceptSwipe.startAnimatingArrows();
            }
        });
        getWindow().getDecorView().setKeepScreenOn(true);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    }

    private View createContentView() {
        Drawable drawable;
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(0);
        View anonymousClass8 = new BackupImageView(this) {
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
        this.photoView = anonymousClass8;
        frameLayout.addView(anonymousClass8);
        TextView branding = new TextView(this);
        branding.setTextColor(-855638017);
        branding.setText(LocaleController.getString("VoipInCallBranding", R.string.VoipInCallBranding));
        Drawable logo = getResources().getDrawable(R.drawable.notification).mutate();
        logo.setAlpha(204);
        logo.setBounds(0, 0, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f));
        if (LocaleController.isRTL) {
            drawable = null;
        } else {
            drawable = logo;
        }
        if (!LocaleController.isRTL) {
            logo = null;
        }
        branding.setCompoundDrawables(drawable, null, logo, null);
        branding.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        branding.setGravity(LocaleController.isRTL ? 5 : 3);
        branding.setCompoundDrawablePadding(AndroidUtilities.dp(5.0f));
        branding.setTextSize(1, 14.0f);
        frameLayout.addView(branding, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 18.0f, 18.0f, 0.0f));
        anonymousClass8 = new TextView(this);
        anonymousClass8.setSingleLine();
        anonymousClass8.setTextColor(-1);
        anonymousClass8.setTextSize(1, 40.0f);
        anonymousClass8.setEllipsize(TruncateAt.END);
        anonymousClass8.setGravity(LocaleController.isRTL ? 5 : 3);
        anonymousClass8.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        anonymousClass8.setTypeface(Typeface.create("sans-serif-light", 0));
        this.nameText = anonymousClass8;
        frameLayout.addView(anonymousClass8, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 43.0f, 18.0f, 0.0f));
        anonymousClass8 = new TextView(this);
        anonymousClass8.setTextColor(-855638017);
        anonymousClass8.setSingleLine();
        anonymousClass8.setEllipsize(TruncateAt.END);
        anonymousClass8.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        anonymousClass8.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        anonymousClass8.setTextSize(1, 15.0f);
        anonymousClass8.setGravity(LocaleController.isRTL ? 5 : 3);
        this.stateText = anonymousClass8;
        frameLayout.addView(anonymousClass8, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        this.durationText = anonymousClass8;
        anonymousClass8 = new TextView(this);
        anonymousClass8.setTextColor(-855638017);
        anonymousClass8.setSingleLine();
        anonymousClass8.setEllipsize(TruncateAt.END);
        anonymousClass8.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        anonymousClass8.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        anonymousClass8.setTextSize(1, 15.0f);
        anonymousClass8.setGravity(LocaleController.isRTL ? 5 : 3);
        anonymousClass8.setVisibility(8);
        this.stateText2 = anonymousClass8;
        frameLayout.addView(anonymousClass8, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        this.ellSpans = new TextAlphaSpan[]{new TextAlphaSpan(), new TextAlphaSpan(), new TextAlphaSpan()};
        anonymousClass8 = new CheckableImageView(this);
        anonymousClass8.setBackgroundResource(R.drawable.bg_voip_icon_btn);
        Drawable micIcon = getResources().getDrawable(R.drawable.ic_mic_off_white_24dp).mutate();
        micIcon.setAlpha(204);
        anonymousClass8.setImageDrawable(micIcon);
        anonymousClass8.setScaleType(ScaleType.CENTER);
        this.micToggle = anonymousClass8;
        frameLayout.addView(anonymousClass8, LayoutHelper.createFrame(38, 38.0f, 83, 16.0f, 0.0f, 0.0f, 10.0f));
        anonymousClass8 = new CheckableImageView(this);
        anonymousClass8.setBackgroundResource(R.drawable.bg_voip_icon_btn);
        Drawable speakerIcon = getResources().getDrawable(R.drawable.ic_volume_up_white_24dp).mutate();
        speakerIcon.setAlpha(204);
        anonymousClass8.setImageDrawable(speakerIcon);
        anonymousClass8.setScaleType(ScaleType.CENTER);
        this.spkToggle = anonymousClass8;
        frameLayout.addView(anonymousClass8, LayoutHelper.createFrame(38, 38.0f, 85, 0.0f, 0.0f, 16.0f, 10.0f));
        ImageView chat = new ImageView(this);
        Drawable chatIcon = getResources().getDrawable(R.drawable.ic_chat_bubble_white_24dp).mutate();
        chatIcon.setAlpha(204);
        chat.setImageDrawable(chatIcon);
        chat.setScaleType(ScaleType.CENTER);
        this.chatBtn = chat;
        frameLayout.addView(chat, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        anonymousClass8 = new LinearLayout(this);
        anonymousClass8.setOrientation(0);
        CallSwipeView acceptSwipe = new CallSwipeView(this);
        acceptSwipe.setColor(-12207027);
        this.acceptSwipe = acceptSwipe;
        anonymousClass8.addView(acceptSwipe, LayoutHelper.createLinear(-1, 70, 1.0f, 4, 4, -35, 4));
        anonymousClass8 = new CallSwipeView(this);
        anonymousClass8.setColor(-1696188);
        this.declineSwipe = anonymousClass8;
        anonymousClass8.addView(anonymousClass8, LayoutHelper.createLinear(-1, 70, 1.0f, -35, 4, 4, 4));
        this.swipeViewsWrap = anonymousClass8;
        frameLayout.addView(anonymousClass8, LayoutHelper.createFrame(-1, -2.0f, 80, 20.0f, 0.0f, 20.0f, 68.0f));
        ImageView acceptBtn = new ImageView(this);
        FabBackgroundDrawable acceptBtnBg = new FabBackgroundDrawable();
        acceptBtnBg.setColor(-12207027);
        acceptBtn.setBackgroundDrawable(acceptBtnBg);
        acceptBtn.setImageResource(R.drawable.ic_call_end_white_36dp);
        acceptBtn.setScaleType(ScaleType.MATRIX);
        Matrix matrix = new Matrix();
        matrix.setTranslate((float) AndroidUtilities.dp(17.0f), (float) AndroidUtilities.dp(17.0f));
        matrix.postRotate(-135.0f, (float) AndroidUtilities.dp(35.0f), (float) AndroidUtilities.dp(35.0f));
        acceptBtn.setImageMatrix(matrix);
        this.acceptBtn = acceptBtn;
        frameLayout.addView(acceptBtn, LayoutHelper.createFrame(78, 78.0f, 83, 20.0f, 0.0f, 0.0f, 68.0f));
        anonymousClass8 = new ImageView(this);
        Drawable rejectBtnBg = new FabBackgroundDrawable();
        rejectBtnBg.setColor(-1696188);
        anonymousClass8.setBackgroundDrawable(rejectBtnBg);
        anonymousClass8.setImageResource(R.drawable.ic_call_end_white_36dp);
        anonymousClass8.setScaleType(ScaleType.CENTER);
        this.declineBtn = anonymousClass8;
        frameLayout.addView(anonymousClass8, LayoutHelper.createFrame(78, 78.0f, 85, 0.0f, 0.0f, 20.0f, 68.0f));
        acceptSwipe.setViewToDrag(acceptBtn, false);
        anonymousClass8.setViewToDrag(anonymousClass8, true);
        anonymousClass8 = new FrameLayout(this);
        FabBackgroundDrawable endBtnBg = new FabBackgroundDrawable();
        endBtnBg.setColor(-1696188);
        this.endBtnBg = endBtnBg;
        anonymousClass8.setBackgroundDrawable(endBtnBg);
        anonymousClass8 = new ImageView(this);
        anonymousClass8.setImageResource(R.drawable.ic_call_end_white_36dp);
        anonymousClass8.setScaleType(ScaleType.CENTER);
        this.endBtnIcon = anonymousClass8;
        anonymousClass8.addView(anonymousClass8, LayoutHelper.createFrame(70, 70.0f));
        anonymousClass8.setForeground(getResources().getDrawable(R.drawable.fab_highlight_dark));
        this.endBtn = anonymousClass8;
        frameLayout.addView(anonymousClass8, LayoutHelper.createFrame(78, 78.0f, 81, 0.0f, 0.0f, 0.0f, 68.0f));
        this.keyEmojiText = new TextView(this);
        this.keyEmojiText.setTextSize(1, 20.0f);
        frameLayout.addView(this.keyEmojiText, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 10.0f, 14.0f, 0.0f));
        this.keyEmojiText.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (VoIPActivity.this.tooltipHider != null) {
                    VoIPActivity.this.hintTextView.removeCallbacks(VoIPActivity.this.tooltipHider);
                    VoIPActivity.this.tooltipHider = null;
                }
                VoIPActivity.this.setEmojiTooltipVisible(!VoIPActivity.this.emojiTooltipVisible);
                if (VoIPActivity.this.emojiTooltipVisible) {
                    VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.this.tooltipHider = new Runnable() {
                        public void run() {
                            VoIPActivity.this.tooltipHider = null;
                            VoIPActivity.this.setEmojiTooltipVisible(false);
                        }
                    }, ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                }
            }
        });
        this.hintTextView = new TextView(this);
        this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), -231525581));
        this.hintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        this.hintTextView.setTextSize(1, 14.0f);
        this.hintTextView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        this.hintTextView.setText(LocaleController.getString("CallEmojiKeyTooltip", R.string.CallEmojiKeyTooltip));
        this.hintTextView.setGravity(21);
        this.hintTextView.setMaxWidth(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_MAGENTA));
        this.hintTextView.setAlpha(0.0f);
        frameLayout.addView(this.hintTextView, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 42.0f, 10.0f, 0.0f));
        int ellMaxAlpha = this.stateText.getPaint().getAlpha();
        this.ellAnimator = new AnimatorSet();
        AnimatorSet animatorSet = this.ellAnimator;
        r34 = new Animator[6];
        r34[0] = createAlphaAnimator(this.ellSpans[0], 0, ellMaxAlpha, 0, 300);
        r34[1] = createAlphaAnimator(this.ellSpans[1], 0, ellMaxAlpha, 150, 300);
        r34[2] = createAlphaAnimator(this.ellSpans[2], 0, ellMaxAlpha, 300, 300);
        r34[3] = createAlphaAnimator(this.ellSpans[0], ellMaxAlpha, 0, 1000, 400);
        r34[4] = createAlphaAnimator(this.ellSpans[1], ellMaxAlpha, 0, 1000, 400);
        r34[5] = createAlphaAnimator(this.ellSpans[2], ellMaxAlpha, 0, 1000, 400);
        animatorSet.playTogether(r34);
        this.ellAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (!VoIPActivity.this.isFinishing()) {
                    VoIPActivity.this.ellAnimator.setStartDelay(300);
                    VoIPActivity.this.ellAnimator.start();
                }
            }
        });
        return frameLayout;
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
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
        }
        super.onDestroy();
    }

    public void onBackPressed() {
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
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().onUIForegroundStateChanged(false);
        }
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 101) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == 0) {
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

    private void updateKeyView() {
        if (VoIPService.getSharedInstance() != null) {
            new IdenticonDrawable().setColors(new int[]{ViewCompat.MEASURED_SIZE_MASK, -1, -NUM, 872415231});
            EncryptedChat encryptedChat = new EncryptedChat();
            try {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                buf.write(VoIPService.getSharedInstance().getEncryptionKey());
                buf.write(VoIPService.getSharedInstance().getGA());
                encryptedChat.auth_key = buf.toByteArray();
            } catch (Exception e) {
            }
            this.keyEmojiText.setText(Emoji.replaceEmoji(TextUtils.join(" ", EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(encryptedChat.auth_key, 0, encryptedChat.auth_key.length))), this.keyEmojiText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(32.0f), false));
        }
    }

    private void showDebugAlert() {
        if (VoIPService.getSharedInstance() != null) {
            final AlertDialog dlg = new AlertDialog.Builder(this).setTitle("libtgvoip v" + VoIPController.getVersion() + " debug").setMessage(VoIPService.getSharedInstance().getDebugString()).setPositiveButton("Close", null).create();
            Runnable r = new Runnable() {
                public void run() {
                    if (dlg.isShowing() && !VoIPActivity.this.isFinishing() && VoIPService.getSharedInstance() != null) {
                        dlg.setMessage(VoIPService.getSharedInstance().getDebugString());
                        dlg.getWindow().getDecorView().postDelayed(this, 500);
                    }
                }
            };
            dlg.show();
            dlg.getWindow().getDecorView().postDelayed(r, 500);
        }
    }

    private void showDebugCtlAlert() {
        new AlertDialog.Builder(this).setItems(new String[]{"Set audio bitrate", "Set expect packet loss %", "Disable p2p", "Enable p2p", "Disable AEC", "Enable AEC"}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        VoIPActivity.this.showNumberPickerDialog(8, 32, VoIPActivity.this.audioBitrate, "Audio bitrate (kbit/s)", new OnValueChangeListener() {
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                VoIPActivity.this.audioBitrate = newVal;
                                VoIPService.getSharedInstance().debugCtl(1, newVal * 1000);
                            }
                        });
                        return;
                    case 1:
                        VoIPActivity.this.showNumberPickerDialog(0, 100, VoIPActivity.this.packetLossPercent, "Expected packet loss %", new OnValueChangeListener() {
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                VoIPActivity.this.packetLossPercent = newVal;
                                VoIPService.getSharedInstance().debugCtl(2, newVal);
                            }
                        });
                        return;
                    case 2:
                        VoIPService.getSharedInstance().debugCtl(3, 0);
                        return;
                    case 3:
                        VoIPService.getSharedInstance().debugCtl(3, 1);
                        return;
                    case 4:
                        VoIPService.getSharedInstance().debugCtl(4, 0);
                        return;
                    case 5:
                        VoIPService.getSharedInstance().debugCtl(4, 1);
                        return;
                    default:
                        return;
                }
            }
        }).show();
    }

    private void showNumberPickerDialog(int min, int max, int value, String title, OnValueChangeListener listener) {
        NumberPicker picker = new NumberPicker(this);
        picker.setMinValue(min);
        picker.setMaxValue(max);
        picker.setValue(value);
        picker.setOnValueChangedListener(listener);
        new AlertDialog.Builder(this).setTitle(title).setView(picker).setPositiveButton("Done", null).show();
    }

    private void startUpdatingCallDuration() {
        new Runnable() {
            public void run() {
                if (!VoIPActivity.this.isFinishing() && VoIPService.getSharedInstance() != null && VoIPActivity.this.callState == 3) {
                    CharSequence format;
                    long duration = VoIPService.getSharedInstance().getCallDuration() / 1000;
                    TextView access$2100 = VoIPActivity.this.durationText;
                    if (duration > 3600) {
                        format = String.format("%d:%02d:%02d", new Object[]{Long.valueOf(duration / 3600), Long.valueOf((duration % 3600) / 60), Long.valueOf(duration % 60)});
                    } else {
                        format = String.format("%d:%02d", new Object[]{Long.valueOf(duration / 60), Long.valueOf(duration % 60)});
                    }
                    access$2100.setText(format);
                    VoIPActivity.this.durationText.postDelayed(this, 500);
                }
            }
        }.run();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!this.isIncomingWaiting || (keyCode != 25 && keyCode != 24)) {
            return super.onKeyDown(keyCode, event);
        }
        VoIPService.getSharedInstance().stopRinging();
        return true;
    }

    private void callAccepted() {
        this.endBtn.setVisibility(0);
        this.micToggle.setVisibility(0);
        if (VoIPService.getSharedInstance().hasEarpiece()) {
            this.spkToggle.setVisibility(0);
        }
        this.chatBtn.setVisibility(0);
        if (this.didAcceptFromHere) {
            ObjectAnimator colorAnim;
            this.acceptBtn.setVisibility(8);
            if (VERSION.SDK_INT >= 21) {
                colorAnim = ObjectAnimator.ofArgb(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
            } else {
                colorAnim = ObjectAnimator.ofInt(this.endBtnBg, TtmlNode.ATTR_TTS_COLOR, new int[]{-12207027, -1696188});
                colorAnim.setEvaluator(new ArgbEvaluator());
            }
            AnimatorSet set = new AnimatorSet();
            AnimatorSet decSet = new AnimatorSet();
            decSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.micToggle, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.spkToggle, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.chatBtn, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[]{-135.0f, 0.0f}), colorAnim});
            decSet.setInterpolator(new DecelerateInterpolator());
            decSet.setDuration(500);
            AnimatorSet accSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this.swipeViewsWrap, "alpha", new float[]{1.0f, 0.0f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.declineBtn, "alpha", new float[]{0.0f});
            accSet.playTogether(animatorArr);
            accSet.setInterpolator(new AccelerateInterpolator());
            accSet.setDuration(125);
            set.playTogether(new Animator[]{decSet, accSet});
            set.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    VoIPActivity.this.swipeViewsWrap.setVisibility(8);
                    VoIPActivity.this.declineBtn.setVisibility(8);
                }
            });
            set.start();
            return;
        }
        set = new AnimatorSet();
        decSet = new AnimatorSet();
        decSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.micToggle, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.spkToggle, "alpha", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.chatBtn, "alpha", new float[]{0.0f, 1.0f})});
        decSet.setInterpolator(new DecelerateInterpolator());
        decSet.setDuration(500);
        accSet = new AnimatorSet();
        animatorArr = new Animator[3];
        animatorArr[1] = ObjectAnimator.ofFloat(this.declineBtn, "alpha", new float[]{0.0f});
        animatorArr[2] = ObjectAnimator.ofFloat(this.acceptBtn, "alpha", new float[]{0.0f});
        accSet.playTogether(animatorArr);
        accSet.setInterpolator(new AccelerateInterpolator());
        accSet.setDuration(125);
        set.playTogether(new Animator[]{decSet, accSet});
        set.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.swipeViewsWrap.setVisibility(8);
                VoIPActivity.this.declineBtn.setVisibility(8);
                VoIPActivity.this.acceptBtn.setVisibility(8);
            }
        });
        set.start();
    }

    public void onStateChanged(final int state) {
        this.callState = state;
        runOnUiThread(new Runnable() {
            public void run() {
                boolean wasFirstStateChange = VoIPActivity.this.firstStateChange;
                if (VoIPActivity.this.firstStateChange) {
                    if (VoIPActivity.this.isIncomingWaiting = state == 10) {
                        VoIPActivity.this.swipeViewsWrap.setVisibility(0);
                        VoIPActivity.this.endBtn.setVisibility(8);
                        VoIPActivity.this.micToggle.setVisibility(8);
                        VoIPActivity.this.spkToggle.setVisibility(8);
                        VoIPActivity.this.chatBtn.setVisibility(8);
                        VoIPActivity.this.acceptSwipe.startAnimatingArrows();
                        VoIPActivity.this.declineSwipe.startAnimatingArrows();
                        VoIPActivity.this.getWindow().addFlags(2097152);
                    } else {
                        VoIPActivity.this.swipeViewsWrap.setVisibility(8);
                        VoIPActivity.this.acceptBtn.setVisibility(8);
                        VoIPActivity.this.declineBtn.setVisibility(8);
                        VoIPActivity.this.getWindow().clearFlags(2097152);
                    }
                    VoIPActivity.this.firstStateChange = false;
                }
                if (!(!VoIPActivity.this.isIncomingWaiting || state == 10 || state == 6 || state == 5)) {
                    VoIPActivity.this.isIncomingWaiting = false;
                    if (!VoIPActivity.this.didAcceptFromHere) {
                        VoIPActivity.this.callAccepted();
                    }
                }
                if (state == 10) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipIncoming", R.string.VoipIncoming), false);
                    VoIPActivity.this.getWindow().addFlags(2097152);
                } else if (state == 1 || state == 2) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipConnecting", R.string.VoipConnecting), true);
                } else if (state == 7) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipExchangingKeys", R.string.VoipExchangingKeys), true);
                } else if (state == 8) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipWaiting", R.string.VoipWaiting), true);
                } else if (state == 11) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRinging", R.string.VoipRinging), true);
                } else if (state == 9) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipRequesting", R.string.VoipRequesting), true);
                } else if (state == 5) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipHangingUp", R.string.VoipHangingUp), true);
                } else if (state == 6) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipCallEnded", R.string.VoipCallEnded), false);
                    VoIPActivity.this.stateText.postDelayed(new Runnable() {
                        public void run() {
                            VoIPActivity.this.finish();
                        }
                    }, 200);
                } else if (state == 12) {
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipBusy", R.string.VoipBusy), false);
                    VoIPActivity.this.stateText.postDelayed(new Runnable() {
                        public void run() {
                            VoIPActivity.this.finish();
                        }
                    }, 2000);
                } else if (state == 3) {
                    if (!wasFirstStateChange) {
                        int count = VoIPActivity.this.getSharedPreferences("mainconfig", 0).getInt("call_emoji_tooltip_count", 0);
                        if (count < 3) {
                            VoIPActivity.this.setEmojiTooltipVisible(true);
                            VoIPActivity.this.hintTextView.postDelayed(VoIPActivity.this.tooltipHider = new Runnable() {
                                public void run() {
                                    VoIPActivity.this.tooltipHider = null;
                                    VoIPActivity.this.setEmojiTooltipVisible(false);
                                }
                            }, ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                            VoIPActivity.this.getSharedPreferences("mainconfig", 0).edit().putInt("call_emoji_tooltip_count", count).apply();
                        }
                    }
                    VoIPActivity.this.setStateTextAnimated("0:00", false);
                    VoIPActivity.this.startUpdatingCallDuration();
                    VoIPActivity.this.updateKeyView();
                } else if (state == 4) {
                    int lastError;
                    VoIPActivity.this.setStateTextAnimated(LocaleController.getString("VoipFailed", R.string.VoipFailed), false);
                    if (VoIPService.getSharedInstance() != null) {
                        lastError = VoIPService.getSharedInstance().getLastError();
                    } else {
                        lastError = 0;
                    }
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
                    } else {
                        VoIPActivity.this.stateText.postDelayed(new Runnable() {
                            public void run() {
                                VoIPActivity.this.finish();
                            }
                        }, 1000);
                    }
                }
            }
        });
    }

    private void showErrorDialog(CharSequence message) {
        AlertDialog dlg = new AlertDialog.Builder(this).setTitle(LocaleController.getString("VoipFailed", R.string.VoipFailed)).setMessage(message).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).show();
        dlg.setCanceledOnTouchOutside(true);
        dlg.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                VoIPActivity.this.finish();
            }
        });
    }

    public void onAudioSettingsChanged() {
        if (VoIPService.getSharedInstance() != null) {
            this.micToggle.setChecked(VoIPService.getSharedInstance().isMicMute());
            if (VoIPService.getSharedInstance().hasEarpiece() || VoIPService.getSharedInstance().isBluetoothHeadsetConnected()) {
                this.spkToggle.setVisibility(0);
                AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                if (!VoIPService.getSharedInstance().hasEarpiece()) {
                    this.spkToggle.setImageResource(R.drawable.ic_bluetooth_white_24dp);
                    this.spkToggle.setChecked(am.isBluetoothScoOn());
                    return;
                } else if (VoIPService.getSharedInstance().isBluetoothHeadsetConnected()) {
                    if (am.isBluetoothScoOn()) {
                        this.spkToggle.setImageResource(R.drawable.ic_bluetooth_white_24dp);
                    } else if (am.isSpeakerphoneOn()) {
                        this.spkToggle.setImageResource(R.drawable.ic_volume_up_white_24dp);
                    } else {
                        this.spkToggle.setImageResource(R.drawable.ic_phone_in_talk_white_24dp);
                    }
                    this.spkToggle.setChecked(false);
                    return;
                } else {
                    this.spkToggle.setImageResource(R.drawable.ic_volume_up_white_24dp);
                    this.spkToggle.setChecked(am.isSpeakerphoneOn());
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

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded) {
            this.keyEmojiText.invalidate();
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
        oa.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.tooltipAnim = null;
            }
        });
        this.tooltipAnim = oa;
        oa.start();
    }
}
