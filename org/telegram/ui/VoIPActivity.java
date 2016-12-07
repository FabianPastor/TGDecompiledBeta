package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.voip.VoIPController;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.messenger.voip.VoIPService.StateListener;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.CallSwipeView;
import org.telegram.ui.Components.voip.CallSwipeView.Listener;
import org.telegram.ui.Components.voip.CheckableImageView;
import org.telegram.ui.Components.voip.FabBackgroundDrawable;

public class VoIPActivity extends Activity implements StateListener {
    private static final String TAG = "tg-voip-ui";
    private View acceptBtn;
    private CallSwipeView acceptSwipe;
    private int audioBitrate = 25;
    private ImageView chatBtn;
    private Animator currentAcceptAnim;
    private Animator currentDeclineAnim;
    private View declineBtn;
    private CallSwipeView declineSwipe;
    private boolean didAcceptFromHere = false;
    private View endBtn;
    private FabBackgroundDrawable endBtnBg;
    private View endBtnIcon;
    private boolean firstStateChange = true;
    private boolean isIncomingWaiting;
    private ImageView keyButton;
    private ImageView keyImage;
    private View keyOverlay;
    private TextView keyText;
    private CheckableImageView micToggle;
    private TextView nameText;
    private int packetLossPercent = 5;
    private BackupImageView photoView;
    private CheckableImageView spkToggle;
    private TextView stateText;
    private LinearLayout swipeViewsWrap;
    private User user;

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
            public void onClick(View v) {
                VoIPActivity.this.showDebugAlert();
            }
        });
        this.nameText.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                VoIPActivity.this.showDebugCtlAlert();
                return true;
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
                if (VoIPService.getSharedInstance() != null) {
                    boolean checked = !VoIPActivity.this.spkToggle.isChecked();
                    VoIPActivity.this.spkToggle.setChecked(checked);
                    ((AudioManager) VoIPActivity.this.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).setSpeakerphoneOn(checked);
                }
            }
        });
        this.micToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (VoIPService.getSharedInstance() != null) {
                    boolean checked = !VoIPActivity.this.micToggle.isChecked();
                    VoIPActivity.this.micToggle.setChecked(checked);
                    VoIPService.getSharedInstance().setMicMute(checked);
                }
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
        this.nameText.setText(ContactsController.formatName(this.user.first_name, this.user.last_name));
        VoIPService.getSharedInstance().registerStateListener(this);
        this.acceptSwipe.setListener(new Listener() {
            public void onDragComplete() {
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
                r1[0] = ObjectAnimator.ofFloat(VoIPActivity.this.declineSwipe, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                r1[1] = ObjectAnimator.ofFloat(VoIPActivity.this.declineBtn, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
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
                VoIPService.getSharedInstance().declineIncomingCall();
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
                r1[0] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptSwipe, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                r1[1] = ObjectAnimator.ofFloat(VoIPActivity.this.acceptBtn, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
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
        this.keyButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                VoIPActivity.this.setKeyOverlayVisible(true);
            }
        });
        getWindow().getDecorView().setKeepScreenOn(true);
    }

    private View createContentView() {
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(0);
        View anonymousClass10 = new BackupImageView(this) {
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
        this.photoView = anonymousClass10;
        frameLayout.addView(anonymousClass10);
        TextView branding = new TextView(this);
        branding.setTextColor(-855638017);
        branding.setText(LocaleController.getString("VoipInCallBranding", R.string.VoipInCallBranding));
        Drawable logo = getResources().getDrawable(R.drawable.notification).mutate();
        logo.setAlpha(204);
        logo.setBounds(0, 0, AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f));
        branding.setCompoundDrawables(logo, null, null, null);
        branding.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        branding.setCompoundDrawablePadding(AndroidUtilities.dp(5.0f));
        branding.setTextSize(1, 14.0f);
        frameLayout.addView(branding, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 18.0f, 18.0f, 0.0f));
        anonymousClass10 = new TextView(this);
        anonymousClass10.setSingleLine();
        anonymousClass10.setTextColor(-1);
        anonymousClass10.setTextSize(1, 40.0f);
        anonymousClass10.setEllipsize(TruncateAt.END);
        anonymousClass10.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        anonymousClass10.setTypeface(Typeface.create("sans-serif-light", 0));
        this.nameText = anonymousClass10;
        frameLayout.addView(anonymousClass10, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 43.0f, 18.0f, 0.0f));
        anonymousClass10 = new TextView(this);
        anonymousClass10.setSingleLine();
        anonymousClass10.setEllipsize(TruncateAt.END);
        anonymousClass10.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        anonymousClass10.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        anonymousClass10.setTextSize(1, 15.0f);
        anonymousClass10.setAllCaps(true);
        this.stateText = anonymousClass10;
        frameLayout.addView(anonymousClass10, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 98.0f, 18.0f, 0.0f));
        anonymousClass10 = new CheckableImageView(this);
        anonymousClass10.setBackgroundResource(R.drawable.bg_voip_icon_btn);
        Drawable micIcon = getResources().getDrawable(R.drawable.ic_mic_off_white_24dp).mutate();
        micIcon.setAlpha(204);
        anonymousClass10.setImageDrawable(micIcon);
        anonymousClass10.setScaleType(ScaleType.CENTER);
        this.micToggle = anonymousClass10;
        frameLayout.addView(anonymousClass10, LayoutHelper.createFrame(38, 38.0f, 83, 16.0f, 0.0f, 0.0f, 10.0f));
        anonymousClass10 = new CheckableImageView(this);
        anonymousClass10.setBackgroundResource(R.drawable.bg_voip_icon_btn);
        Drawable speakerIcon = getResources().getDrawable(R.drawable.ic_volume_up_white_24dp).mutate();
        speakerIcon.setAlpha(204);
        anonymousClass10.setImageDrawable(speakerIcon);
        anonymousClass10.setScaleType(ScaleType.CENTER);
        this.spkToggle = anonymousClass10;
        frameLayout.addView(anonymousClass10, LayoutHelper.createFrame(38, 38.0f, 85, 0.0f, 0.0f, 16.0f, 10.0f));
        ImageView chat = new ImageView(this);
        Drawable chatIcon = getResources().getDrawable(R.drawable.ic_chat_bubble_white_24dp).mutate();
        chatIcon.setAlpha(204);
        chat.setImageDrawable(chatIcon);
        chat.setScaleType(ScaleType.CENTER);
        this.chatBtn = chat;
        frameLayout.addView(chat, LayoutHelper.createFrame(38, 38.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
        View swipesWrap = new LinearLayout(this);
        swipesWrap.setOrientation(0);
        CallSwipeView acceptSwipe = new CallSwipeView(this);
        acceptSwipe.setColor(-12207027);
        this.acceptSwipe = acceptSwipe;
        swipesWrap.addView(acceptSwipe, LayoutHelper.createLinear(-1, 70, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 4, 4, -35, 4));
        anonymousClass10 = new CallSwipeView(this);
        anonymousClass10.setColor(-1696188);
        this.declineSwipe = anonymousClass10;
        swipesWrap.addView(anonymousClass10, LayoutHelper.createLinear(-1, 70, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, -35, 4, 4, 4));
        this.swipeViewsWrap = swipesWrap;
        frameLayout.addView(swipesWrap, LayoutHelper.createFrame(-1, -2.0f, 80, 20.0f, 0.0f, 20.0f, 68.0f));
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
        anonymousClass10 = new ImageView(this);
        Drawable rejectBtnBg = new FabBackgroundDrawable();
        rejectBtnBg.setColor(-1696188);
        anonymousClass10.setBackgroundDrawable(rejectBtnBg);
        anonymousClass10.setImageResource(R.drawable.ic_call_end_white_36dp);
        anonymousClass10.setScaleType(ScaleType.CENTER);
        this.declineBtn = anonymousClass10;
        frameLayout.addView(anonymousClass10, LayoutHelper.createFrame(78, 78.0f, 85, 0.0f, 0.0f, 20.0f, 68.0f));
        acceptSwipe.setViewToDrag(acceptBtn, false);
        anonymousClass10.setViewToDrag(anonymousClass10, true);
        anonymousClass10 = new FrameLayout(this);
        FabBackgroundDrawable endBtnBg = new FabBackgroundDrawable();
        endBtnBg.setColor(-1696188);
        this.endBtnBg = endBtnBg;
        anonymousClass10.setBackgroundDrawable(endBtnBg);
        anonymousClass10 = new ImageView(this);
        anonymousClass10.setImageResource(R.drawable.ic_call_end_white_36dp);
        anonymousClass10.setScaleType(ScaleType.CENTER);
        this.endBtnIcon = anonymousClass10;
        anonymousClass10.addView(anonymousClass10, LayoutHelper.createFrame(70, 70.0f));
        anonymousClass10.setForeground(getResources().getDrawable(R.drawable.fab_highlight_dark));
        this.endBtn = anonymousClass10;
        frameLayout.addView(anonymousClass10, LayoutHelper.createFrame(78, 78.0f, 81, 0.0f, 0.0f, 0.0f, 68.0f));
        anonymousClass10 = new FrameLayout(this);
        this.keyButton = new ImageView(this);
        this.keyButton.setScaleType(ScaleType.CENTER);
        this.keyButton.setAlpha(204);
        this.keyButton.setBackgroundResource(R.drawable.bar_selector_white);
        this.keyButton.setImageResource(R.drawable.ic_vpn_key_white_24dp);
        anonymousClass10.setBackgroundColor(0);
        anonymousClass10.addView(this.keyButton);
        anonymousClass10.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        frameLayout.addView(anonymousClass10, LayoutHelper.createFrame(56, 56.0f, 53, 0.0f, -4.0f, -4.0f, 0.0f));
        anonymousClass10 = new LinearLayout(this);
        anonymousClass10.setBackgroundColor(-452984832);
        anonymousClass10.setOrientation(1);
        ActionBar bar = new ActionBar(this);
        bar.setTitle(LocaleController.getString("EncryptionKey", R.string.EncryptionKey));
        bar.setBackButtonImage(R.drawable.ic_ab_back);
        bar.setOccupyStatusBar(false);
        bar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    VoIPActivity.this.setKeyOverlayVisible(false);
                }
            }
        });
        anonymousClass10.addView(bar, new LayoutParams(-1, ActionBar.getCurrentActionBarHeight()));
        anonymousClass10 = new ImageView(this);
        anonymousClass10.setScaleType(ScaleType.FIT_XY);
        this.keyImage = anonymousClass10;
        anonymousClass10.addView(anonymousClass10, LayoutHelper.createLinear(-1, -1, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 20, 20, 20, 20));
        anonymousClass10 = new TextView(this);
        anonymousClass10.setTextSize(1, 16.0f);
        anonymousClass10.setTextColor(-1);
        anonymousClass10.setGravity(17);
        this.keyText = anonymousClass10;
        anonymousClass10.addView(anonymousClass10, LayoutHelper.createLinear(-1, -1, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 10, 10, 10, 10));
        anonymousClass10.setVisibility(8);
        anonymousClass10.setAlpha(0.0f);
        this.keyOverlay = anonymousClass10;
        frameLayout.addView(anonymousClass10);
        return frameLayout;
    }

    protected void onDestroy() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
        }
        super.onDestroy();
    }

    public void onBackPressed() {
        if (this.keyOverlay.getVisibility() == 0) {
            setKeyOverlayVisible(false);
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
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().onUIForegroundStateChanged(false);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 101) {
            return;
        }
        if (grantResults[0] == 0) {
            VoIPService.getSharedInstance().acceptIncomingCall();
            callAccepted();
            return;
        }
        this.acceptSwipe.reset();
    }

    private void setKeyOverlayVisible(boolean visible) {
        if (visible) {
            this.keyOverlay.animate().cancel();
            this.keyOverlay.setVisibility(0);
            this.keyOverlay.animate().alpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT).setDuration(200).setListener(null).setInterpolator(new DecelerateInterpolator()).start();
            return;
        }
        this.keyOverlay.animate().cancel();
        this.keyOverlay.animate().alpha(0.0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                VoIPActivity.this.keyOverlay.setVisibility(8);
            }
        }).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void updateKeyView() {
        IdenticonDrawable img = new IdenticonDrawable();
        EncryptedChat encryptedChat = new EncryptedChat();
        encryptedChat.auth_key = VoIPService.getSharedInstance().getEncryptionKey();
        byte[] sha256 = Utilities.computeSHA256(encryptedChat.auth_key, 0, encryptedChat.auth_key.length);
        byte[] key_hash = new byte[36];
        System.arraycopy(AndroidUtilities.calcAuthKeyHash(encryptedChat.auth_key), 0, key_hash, 0, 16);
        System.arraycopy(sha256, 0, key_hash, 16, 20);
        encryptedChat.key_hash = key_hash;
        img.setEncryptedChat(encryptedChat);
        this.keyImage.setImageDrawable(img);
        SpannableStringBuilder hash = new SpannableStringBuilder();
        if (encryptedChat.key_hash.length > 16) {
            String hex = Utilities.bytesToHex(encryptedChat.key_hash);
            for (int a = 0; a < 32; a++) {
                if (a != 0) {
                    if (a % 8 == 0) {
                        hash.append('\n');
                    } else if (a % 4 == 0) {
                        hash.append(' ');
                    }
                }
                hash.append(hex.substring(a * 2, (a * 2) + 2));
                hash.append(' ');
            }
            hash.append("\n\n");
        }
        hash.append(AndroidUtilities.replaceTags(LocaleController.formatString("EncryptionKeyDescription", R.string.EncryptionKeyDescription, this.user.first_name, this.user.first_name)));
        this.keyText.setText(hash);
    }

    private void showDebugAlert() {
        final AlertDialog dlg = new Builder(this).setTitle("libtgvoip v" + VoIPController.getVersion() + " debug").setMessage(VoIPService.getSharedInstance().getDebugString()).setPositiveButton("Close", null).create();
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

    private void showDebugCtlAlert() {
        new Builder(this).setItems(new String[]{"Set audio bitrate", "Set expect packet loss %", "Disable p2p", "Enable p2p"}, new DialogInterface.OnClickListener() {
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
        new Builder(this).setTitle(title).setView(picker).setPositiveButton("Done", null).show();
    }

    private void startUpdatingCallDuration() {
        new Runnable() {
            public void run() {
                if (!VoIPActivity.this.isFinishing() && VoIPService.getSharedInstance() != null) {
                    CharSequence format;
                    long duration = VoIPService.getSharedInstance().getCallDuration() / 1000;
                    TextView access$1900 = VoIPActivity.this.stateText;
                    if (duration > 3600) {
                        format = String.format("%d:%02d:%02d", new Object[]{Long.valueOf(duration / 3600), Long.valueOf((duration % 3600) / 60), Long.valueOf(duration % 60)});
                    } else {
                        format = String.format("%d:%02d", new Object[]{Long.valueOf(duration / 60), Long.valueOf(duration % 60)});
                    }
                    access$1900.setText(format);
                    VoIPActivity.this.stateText.postDelayed(this, 500);
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
        this.spkToggle.setVisibility(0);
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
            decSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.micToggle, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}), ObjectAnimator.ofFloat(this.spkToggle, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}), ObjectAnimator.ofFloat(this.chatBtn, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}), ObjectAnimator.ofFloat(this.endBtnIcon, "rotation", new float[]{-135.0f, 0.0f}), colorAnim});
            decSet.setInterpolator(new DecelerateInterpolator());
            decSet.setDuration(500);
            AnimatorSet accSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this.swipeViewsWrap, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f});
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
        decSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.micToggle, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}), ObjectAnimator.ofFloat(this.spkToggle, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}), ObjectAnimator.ofFloat(this.chatBtn, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT})});
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
        runOnUiThread(new Runnable() {
            public void run() {
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
                    if (state != 3) {
                        VoIPActivity.this.keyButton.setVisibility(8);
                    }
                    VoIPActivity.this.firstStateChange = false;
                }
                if (VoIPActivity.this.isIncomingWaiting && state != 10) {
                    VoIPActivity.this.isIncomingWaiting = false;
                    if (!VoIPActivity.this.didAcceptFromHere) {
                        VoIPActivity.this.callAccepted();
                    }
                }
                if (state == 10) {
                    VoIPActivity.this.stateText.setText(LocaleController.getString("VoipIncoming", R.string.VoipIncoming));
                    VoIPActivity.this.getWindow().addFlags(2097152);
                } else if (state == 1 || state == 2) {
                    VoIPActivity.this.stateText.setText(LocaleController.getString("VoipConnecting", R.string.VoipConnecting));
                } else if (state == 7) {
                    VoIPActivity.this.stateText.setText(LocaleController.getString("VoipExchangingKeys", R.string.VoipExchangingKeys));
                } else if (state == 8) {
                    VoIPActivity.this.stateText.setText(LocaleController.getString("VoipWaiting", R.string.VoipWaiting));
                } else if (state == 11) {
                    VoIPActivity.this.stateText.setText(LocaleController.getString("VoipRinging", R.string.VoipRinging));
                } else if (state == 9) {
                    VoIPActivity.this.stateText.setText(LocaleController.getString("VoipRequesting", R.string.VoipRequesting));
                } else if (state == 5) {
                    VoIPActivity.this.stateText.setText(LocaleController.getString("VoipHangingUp", R.string.VoipHangingUp));
                } else if (state == 6) {
                    VoIPActivity.this.stateText.setText(LocaleController.getString("VoipCallEnded", R.string.VoipCallEnded));
                    VoIPActivity.this.stateText.postDelayed(new Runnable() {
                        public void run() {
                            VoIPActivity.this.finish();
                        }
                    }, 200);
                } else if (state == 3) {
                    VoIPActivity.this.startUpdatingCallDuration();
                    VoIPActivity.this.updateKeyView();
                    if (VoIPActivity.this.keyButton.getVisibility() != 0) {
                        VoIPActivity.this.keyButton.setVisibility(0);
                        VoIPActivity.this.keyButton.setAlpha(0.0f);
                        VoIPActivity.this.keyButton.animate().alpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT).setDuration(200).setInterpolator(new DecelerateInterpolator()).start();
                    }
                } else if (state == 4) {
                    VoIPActivity.this.stateText.setText(LocaleController.getString("VoipFailed", R.string.VoipFailed));
                    VoIPActivity.this.stateText.postDelayed(new Runnable() {
                        public void run() {
                            VoIPActivity.this.finish();
                        }
                    }, 1000);
                }
            }
        });
    }

    public void onAudioSettingsChanged() {
        this.micToggle.setChecked(VoIPService.getSharedInstance().isMicMute());
    }
}
