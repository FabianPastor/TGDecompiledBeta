package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AdminedChannelCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class ChannelCreateActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate {
    private static final int done_button = 1;
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList<>();
    private TextInfoPrivacyCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private TLRPC.FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    private AvatarDrawable avatarDrawable;
    /* access modifiers changed from: private */
    public RLottieImageView avatarEditor;
    /* access modifiers changed from: private */
    public BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public View avatarOverlay;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    private RLottieDrawable cameraDrawable;
    private boolean canCreatePublic = true;
    /* access modifiers changed from: private */
    public long chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    /* access modifiers changed from: private */
    public TextView checkTextView;
    /* access modifiers changed from: private */
    public boolean createAfterUpload;
    /* access modifiers changed from: private */
    public int currentStep;
    /* access modifiers changed from: private */
    public EditTextBoldCursor descriptionTextView;
    private View doneButton;
    /* access modifiers changed from: private */
    public boolean donePressed;
    private EditTextBoldCursor editText;
    private HeaderCell headerCell;
    private HeaderCell headerCell2;
    private TextView helpTextView;
    /* access modifiers changed from: private */
    public ImageUpdater imageUpdater;
    private TLRPC.InputFile inputPhoto;
    private TLRPC.InputFile inputVideo;
    private String inputVideoPath;
    private TLRPC.TL_chatInviteExported invite;
    /* access modifiers changed from: private */
    public boolean isPrivate;
    /* access modifiers changed from: private */
    public String lastCheckName;
    /* access modifiers changed from: private */
    public boolean lastNameAvailable;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout2;
    private LinearLayout linkContainer;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    /* access modifiers changed from: private */
    public EditTextEmoji nameTextView;
    private String nameToSet;
    private LinkActionView permanentLinkView;
    private LinearLayout privateContainer;
    /* access modifiers changed from: private */
    public AlertDialog progressDialog;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private ShadowSectionCell sectionCell;
    private TextInfoPrivacyCell typeInfoCell;
    private double videoTimestamp;

    public ChannelCreateActivity(Bundle args) {
        super(args);
        int i = args.getInt("step", 0);
        this.currentStep = i;
        if (i == 0) {
            this.avatarDrawable = new AvatarDrawable();
            this.imageUpdater = new ImageUpdater(true);
            TLRPC.TL_channels_checkUsername req = new TLRPC.TL_channels_checkUsername();
            req.username = "1";
            req.channel = new TLRPC.TL_inputChannelEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelCreateActivity$$ExternalSyntheticLambda12(this));
            return;
        }
        if (i == 1) {
            boolean z = args.getBoolean("canCreatePublic", true);
            this.canCreatePublic = z;
            this.isPrivate = !z;
            if (!z) {
                loadAdminedChannels();
            }
        }
        this.chatId = args.getLong("chat_id", 0);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1618lambda$new$0$orgtelegramuiChannelCreateActivity(TLRPC.TL_error error) {
        this.canCreatePublic = error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
    }

    /* renamed from: lambda$new$1$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1619lambda$new$1$orgtelegramuiChannelCreateActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelCreateActivity$$ExternalSyntheticLambda6(this, error));
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidFailCreate);
        if (this.currentStep == 1) {
            generateLink();
        }
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.parentFragment = this;
            this.imageUpdater.setDelegate(this);
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidFailCreate);
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.clear();
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    public void onResume() {
        super.onResume();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.onPause();
        }
    }

    public void dismissCurrentDialog() {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 == null || !imageUpdater2.dismissCurrentDialog(this.visibleDialog)) {
            super.dismissCurrentDialog();
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        return (imageUpdater2 == null || imageUpdater2.dismissDialogOnPause(dialog)) && super.dismissDialogOnPause(dialog);
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        }
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return true;
        }
        this.nameTextView.hidePopup(true);
        return false;
    }

    public View createView(Context context) {
        Context context2 = context;
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChannelCreateActivity.this.finishFragment();
                } else if (id != 1) {
                } else {
                    if (ChannelCreateActivity.this.currentStep == 0) {
                        if (!ChannelCreateActivity.this.donePressed && ChannelCreateActivity.this.getParentActivity() != null) {
                            if (ChannelCreateActivity.this.nameTextView.length() == 0) {
                                Vibrator v = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                                if (v != null) {
                                    v.vibrate(200);
                                }
                                AndroidUtilities.shakeView(ChannelCreateActivity.this.nameTextView, 2.0f, 0);
                                return;
                            }
                            boolean unused = ChannelCreateActivity.this.donePressed = true;
                            if (ChannelCreateActivity.this.imageUpdater.isUploadingImage()) {
                                boolean unused2 = ChannelCreateActivity.this.createAfterUpload = true;
                                AlertDialog unused3 = ChannelCreateActivity.this.progressDialog = new AlertDialog(ChannelCreateActivity.this.getParentActivity(), 3);
                                ChannelCreateActivity.this.progressDialog.setOnCancelListener(new ChannelCreateActivity$1$$ExternalSyntheticLambda0(this));
                                ChannelCreateActivity.this.progressDialog.show();
                                return;
                            }
                            int reqId = MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).createChat(ChannelCreateActivity.this.nameTextView.getText().toString(), new ArrayList(), ChannelCreateActivity.this.descriptionTextView.getText().toString(), 2, false, (Location) null, (String) null, ChannelCreateActivity.this);
                            AlertDialog unused4 = ChannelCreateActivity.this.progressDialog = new AlertDialog(ChannelCreateActivity.this.getParentActivity(), 3);
                            ChannelCreateActivity.this.progressDialog.setOnCancelListener(new ChannelCreateActivity$1$$ExternalSyntheticLambda1(this, reqId));
                            ChannelCreateActivity.this.progressDialog.show();
                        }
                    } else if (ChannelCreateActivity.this.currentStep == 1) {
                        if (!ChannelCreateActivity.this.isPrivate) {
                            if (ChannelCreateActivity.this.descriptionTextView.length() == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) ChannelCreateActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("ChannelPublicEmptyUsernameTitle", NUM));
                                builder.setMessage(LocaleController.getString("ChannelPublicEmptyUsername", NUM));
                                builder.setPositiveButton(LocaleController.getString("Close", NUM), (DialogInterface.OnClickListener) null);
                                ChannelCreateActivity.this.showDialog(builder.create());
                                return;
                            } else if (!ChannelCreateActivity.this.lastNameAvailable) {
                                Vibrator v2 = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                                if (v2 != null) {
                                    v2.vibrate(200);
                                }
                                AndroidUtilities.shakeView(ChannelCreateActivity.this.checkTextView, 2.0f, 0);
                                return;
                            } else {
                                MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).updateChannelUserName(ChannelCreateActivity.this.chatId, ChannelCreateActivity.this.lastCheckName);
                            }
                        }
                        Bundle args = new Bundle();
                        args.putInt("step", 2);
                        args.putLong("chatId", ChannelCreateActivity.this.chatId);
                        args.putInt("chatType", 2);
                        ChannelCreateActivity.this.presentFragment(new GroupCreateActivity(args), true);
                    }
                }
            }

            /* renamed from: lambda$onItemClick$0$org-telegram-ui-ChannelCreateActivity$1  reason: not valid java name */
            public /* synthetic */ void m1620lambda$onItemClick$0$orgtelegramuiChannelCreateActivity$1(DialogInterface dialog) {
                boolean unused = ChannelCreateActivity.this.createAfterUpload = false;
                AlertDialog unused2 = ChannelCreateActivity.this.progressDialog = null;
                boolean unused3 = ChannelCreateActivity.this.donePressed = false;
            }

            /* renamed from: lambda$onItemClick$1$org-telegram-ui-ChannelCreateActivity$1  reason: not valid java name */
            public /* synthetic */ void m1621lambda$onItemClick$1$orgtelegramuiChannelCreateActivity$1(int reqId, DialogInterface dialog) {
                ConnectionsManager.getInstance(ChannelCreateActivity.this.currentAccount).cancelRequest(reqId, true);
                boolean unused = ChannelCreateActivity.this.donePressed = false;
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        int i = this.currentStep;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("NewChannel", NUM));
            SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2) {
                private boolean ignoreLayout;

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                    int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                    setMeasuredDimension(widthSize, heightSize);
                    int heightSize2 = heightSize - getPaddingTop();
                    measureChildWithMargins(ChannelCreateActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    if (measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                        this.ignoreLayout = true;
                        ChannelCreateActivity.this.nameTextView.hideEmojiView();
                        this.ignoreLayout = false;
                    }
                    int childCount = getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = getChildAt(i);
                        if (!(child == null || child.getVisibility() == 8 || child == ChannelCreateActivity.this.actionBar)) {
                            if (ChannelCreateActivity.this.nameTextView == null || !ChannelCreateActivity.this.nameTextView.isPopupView(child)) {
                                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                            } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                                child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                            } else if (AndroidUtilities.isTablet()) {
                                child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                            } else {
                                child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec((heightSize2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                            }
                        }
                    }
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    int childLeft;
                    int childTop;
                    int count = getChildCount();
                    int keyboardSize = measureKeyboardHeight();
                    int paddingBottom = (keyboardSize > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : ChannelCreateActivity.this.nameTextView.getEmojiPadding();
                    setBottomClip(paddingBottom);
                    for (int i = 0; i < count; i++) {
                        View child = getChildAt(i);
                        if (child.getVisibility() != 8) {
                            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                            int width = child.getMeasuredWidth();
                            int height = child.getMeasuredHeight();
                            int gravity = lp.gravity;
                            if (gravity == -1) {
                                gravity = 51;
                            }
                            int verticalGravity = gravity & 112;
                            switch (gravity & 7 & 7) {
                                case 1:
                                    childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                    break;
                                case 5:
                                    childLeft = (r - width) - lp.rightMargin;
                                    break;
                                default:
                                    childLeft = lp.leftMargin;
                                    break;
                            }
                            switch (verticalGravity) {
                                case 16:
                                    childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                    break;
                                case 48:
                                    childTop = lp.topMargin + getPaddingTop();
                                    break;
                                case 80:
                                    childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                    break;
                                default:
                                    childTop = lp.topMargin;
                                    break;
                            }
                            if (ChannelCreateActivity.this.nameTextView != null && ChannelCreateActivity.this.nameTextView.isPopupView(child)) {
                                if (AndroidUtilities.isTablet()) {
                                    childTop = getMeasuredHeight() - child.getMeasuredHeight();
                                } else {
                                    childTop = (getMeasuredHeight() + keyboardSize) - child.getMeasuredHeight();
                                }
                            }
                            child.layout(childLeft, childTop, childLeft + width, childTop + height);
                        }
                    }
                    notifyHeightChanged();
                }

                public void requestLayout() {
                    if (!this.ignoreLayout) {
                        super.requestLayout();
                    }
                }
            };
            sizeNotifierFrameLayout.setOnTouchListener(ChannelCreateActivity$$ExternalSyntheticLambda19.INSTANCE);
            this.fragmentView = sizeNotifierFrameLayout;
            this.fragmentView.setTag("windowBackgroundWhite");
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            LinearLayout linearLayout3 = new LinearLayout(context2);
            this.linearLayout = linearLayout3;
            linearLayout3.setOrientation(1);
            sizeNotifierFrameLayout.addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
            FrameLayout frameLayout = new FrameLayout(context2);
            this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            AnonymousClass3 r3 = new BackupImageView(context2) {
                public void invalidate() {
                    if (ChannelCreateActivity.this.avatarOverlay != null) {
                        ChannelCreateActivity.this.avatarOverlay.invalidate();
                    }
                    super.invalidate();
                }

                public void invalidate(int l, int t, int r, int b) {
                    if (ChannelCreateActivity.this.avatarOverlay != null) {
                        ChannelCreateActivity.this.avatarOverlay.invalidate();
                    }
                    super.invalidate(l, t, r, b);
                }
            };
            this.avatarImage = r3;
            r3.setRoundRadius(AndroidUtilities.dp(32.0f));
            String str = "windowBackgroundWhiteBlackText";
            this.avatarDrawable.setInfo(5, (String) null, (String) null);
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            final Paint paint = new Paint(1);
            paint.setColor(NUM);
            AnonymousClass4 r5 = new View(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (ChannelCreateActivity.this.avatarImage != null && ChannelCreateActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                        paint.setAlpha((int) (ChannelCreateActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                        canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, ((float) getMeasuredWidth()) / 2.0f, paint);
                    }
                }
            };
            this.avatarOverlay = r5;
            frameLayout.addView(r5, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            this.avatarOverlay.setOnClickListener(new ChannelCreateActivity$$ExternalSyntheticLambda15(this));
            this.cameraDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(60.0f), AndroidUtilities.dp(60.0f), false, (int[]) null);
            AnonymousClass5 r52 = new RLottieImageView(context2) {
                public void invalidate(int l, int t, int r, int b) {
                    super.invalidate(l, t, r, b);
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }

                public void invalidate() {
                    super.invalidate();
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }
            };
            this.avatarEditor = r52;
            r52.setScaleType(ImageView.ScaleType.CENTER);
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.avatarEditor.setEnabled(false);
            this.avatarEditor.setClickable(false);
            this.avatarEditor.setPadding(AndroidUtilities.dp(2.0f), 0, 0, AndroidUtilities.dp(1.0f));
            frameLayout.addView(this.avatarEditor, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context2);
            this.avatarProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            this.avatarProgressView.setNoProgress(false);
            frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            showAvatarProgress(false, false);
            EditTextEmoji editTextEmoji2 = new EditTextEmoji(context2, sizeNotifierFrameLayout, this, 0);
            this.nameTextView = editTextEmoji2;
            editTextEmoji2.setHint(LocaleController.getString("EnterChannelName", NUM));
            String str2 = this.nameToSet;
            if (str2 != null) {
                this.nameTextView.setText(str2);
                this.nameToSet = null;
            }
            this.nameTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            this.nameTextView.getEditText().setSingleLine(true);
            this.nameTextView.getEditText().setImeOptions(5);
            this.nameTextView.getEditText().setOnEditorActionListener(new ChannelCreateActivity$$ExternalSyntheticLambda20(this));
            frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
            this.descriptionTextView = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 18.0f);
            this.descriptionTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.descriptionTextView.setTextColor(Theme.getColor(str));
            this.descriptionTextView.setBackgroundDrawable((Drawable) null);
            this.descriptionTextView.setLineColors(getThemedColor("windowBackgroundWhiteInputField"), getThemedColor("windowBackgroundWhiteInputFieldActivated"), getThemedColor("windowBackgroundWhiteRedText3"));
            this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.descriptionTextView.setInputType(180225);
            this.descriptionTextView.setImeOptions(6);
            this.descriptionTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(120)});
            this.descriptionTextView.setHint(LocaleController.getString("DescriptionPlaceholder", NUM));
            this.descriptionTextView.setCursorColor(Theme.getColor(str));
            this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            this.descriptionTextView.setCursorWidth(1.5f);
            this.linearLayout.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 18.0f, 24.0f, 0.0f));
            this.descriptionTextView.setOnEditorActionListener(new ChannelCreateActivity$$ExternalSyntheticLambda21(this));
            this.descriptionTextView.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                }
            });
            TextView textView = new TextView(context2);
            this.helpTextView = textView;
            textView.setTextSize(1, 15.0f);
            this.helpTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
            this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.helpTextView.setText(LocaleController.getString("DescriptionInfo", NUM));
            this.linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 20));
        } else {
            String str3 = "windowBackgroundWhiteBlackText";
            if (i == 1) {
                this.fragmentView = new ScrollView(context2);
                ScrollView scrollView = (ScrollView) this.fragmentView;
                scrollView.setFillViewport(true);
                LinearLayout linearLayout4 = new LinearLayout(context2);
                this.linearLayout = linearLayout4;
                linearLayout4.setOrientation(1);
                scrollView.addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
                this.actionBar.setTitle(LocaleController.getString("ChannelSettingsTitle", NUM));
                this.fragmentView.setTag("windowBackgroundGray");
                this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
                HeaderCell headerCell3 = new HeaderCell(context2, 23);
                this.headerCell2 = headerCell3;
                headerCell3.setHeight(46);
                this.headerCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                this.headerCell2.setText(LocaleController.getString("ChannelTypeHeader", NUM));
                this.linearLayout.addView(this.headerCell2);
                LinearLayout linearLayout5 = new LinearLayout(context2);
                this.linearLayout2 = linearLayout5;
                linearLayout5.setOrientation(1);
                this.linearLayout2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                this.linearLayout.addView(this.linearLayout2, LayoutHelper.createLinear(-1, -2));
                RadioButtonCell radioButtonCell = new RadioButtonCell(context2);
                this.radioButtonCell1 = radioButtonCell;
                radioButtonCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", NUM), LocaleController.getString("ChannelPublicInfo", NUM), false, !this.isPrivate);
                this.linearLayout2.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
                this.radioButtonCell1.setOnClickListener(new ChannelCreateActivity$$ExternalSyntheticLambda16(this));
                RadioButtonCell radioButtonCell3 = new RadioButtonCell(context2);
                this.radioButtonCell2 = radioButtonCell3;
                radioButtonCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", NUM), LocaleController.getString("ChannelPrivateInfo", NUM), false, this.isPrivate);
                this.linearLayout2.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
                this.radioButtonCell2.setOnClickListener(new ChannelCreateActivity$$ExternalSyntheticLambda17(this));
                ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context2);
                this.sectionCell = shadowSectionCell;
                this.linearLayout.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
                LinearLayout linearLayout6 = new LinearLayout(context2);
                this.linkContainer = linearLayout6;
                linearLayout6.setOrientation(1);
                this.linkContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
                HeaderCell headerCell4 = new HeaderCell(context2);
                this.headerCell = headerCell4;
                this.linkContainer.addView(headerCell4);
                LinearLayout linearLayout7 = new LinearLayout(context2);
                this.publicContainer = linearLayout7;
                linearLayout7.setOrientation(0);
                this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0f, 7.0f, 17.0f, 0.0f));
                EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context2);
                this.editText = editTextBoldCursor2;
                editTextBoldCursor2.setText(MessagesController.getInstance(this.currentAccount).linkPrefix + "/");
                this.editText.setTextSize(1, 18.0f);
                this.editText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
                this.editText.setTextColor(Theme.getColor(str3));
                this.editText.setMaxLines(1);
                this.editText.setLines(1);
                this.editText.setEnabled(false);
                this.editText.setBackgroundDrawable((Drawable) null);
                this.editText.setPadding(0, 0, 0, 0);
                this.editText.setSingleLine(true);
                this.editText.setInputType(163840);
                this.editText.setImeOptions(6);
                this.publicContainer.addView(this.editText, LayoutHelper.createLinear(-2, 36));
                EditTextBoldCursor editTextBoldCursor3 = new EditTextBoldCursor(context2);
                this.descriptionTextView = editTextBoldCursor3;
                editTextBoldCursor3.setTextSize(1, 18.0f);
                this.descriptionTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
                this.descriptionTextView.setTextColor(Theme.getColor(str3));
                this.descriptionTextView.setMaxLines(1);
                this.descriptionTextView.setLines(1);
                this.descriptionTextView.setBackgroundDrawable((Drawable) null);
                this.descriptionTextView.setPadding(0, 0, 0, 0);
                this.descriptionTextView.setSingleLine(true);
                this.descriptionTextView.setInputType(163872);
                this.descriptionTextView.setImeOptions(6);
                this.descriptionTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", NUM));
                this.descriptionTextView.setCursorColor(Theme.getColor(str3));
                this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
                this.descriptionTextView.setCursorWidth(1.5f);
                this.publicContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, 36));
                this.descriptionTextView.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                        boolean unused = channelCreateActivity.checkUserName(channelCreateActivity.descriptionTextView.getText().toString());
                    }

                    public void afterTextChanged(Editable editable) {
                    }
                });
                LinearLayout linearLayout8 = new LinearLayout(context2);
                this.privateContainer = linearLayout8;
                linearLayout8.setOrientation(1);
                this.linkContainer.addView(this.privateContainer, LayoutHelper.createLinear(-1, -2));
                String str4 = "windowBackgroundWhite";
                LinkActionView linkActionView = r0;
                ScrollView scrollView2 = scrollView;
                LinkActionView linkActionView2 = new LinkActionView(context, this, (BottomSheet) null, this.chatId, true, ChatObject.isChannel(getMessagesController().getChat(Long.valueOf(this.chatId))));
                this.permanentLinkView = linkActionView;
                linkActionView.hideRevokeOption(true);
                this.permanentLinkView.setUsers(0, (ArrayList<TLRPC.User>) null);
                this.privateContainer.addView(this.permanentLinkView);
                TextView textView2 = new TextView(context2);
                this.checkTextView = textView2;
                textView2.setTextSize(1, 15.0f);
                this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
                this.checkTextView.setVisibility(8);
                this.linkContainer.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 17, 3, 17, 7));
                TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
                this.typeInfoCell = textInfoPrivacyCell;
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
                this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
                LoadingCell loadingCell = new LoadingCell(context2);
                this.loadingAdminedCell = loadingCell;
                this.linearLayout.addView(loadingCell, LayoutHelper.createLinear(-1, -2));
                LinearLayout linearLayout9 = new LinearLayout(context2);
                this.adminnedChannelsLayout = linearLayout9;
                linearLayout9.setBackgroundColor(Theme.getColor(str4));
                this.adminnedChannelsLayout.setOrientation(1);
                this.linearLayout.addView(this.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
                TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
                this.adminedInfoCell = textInfoPrivacyCell2;
                textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
                this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
                updatePrivatePublic();
            }
        }
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$2(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1603lambda$createView$5$orgtelegramuiChannelCreateActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new ChannelCreateActivity$$ExternalSyntheticLambda22(this), new ChannelCreateActivity$$ExternalSyntheticLambda11(this));
        this.cameraDrawable.setCurrentFrame(0);
        this.cameraDrawable.setCustomEndFrame(43);
        this.avatarEditor.playAnimation();
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1601lambda$createView$3$orgtelegramuiChannelCreateActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.inputPhoto = null;
        this.inputVideo = null;
        this.inputVideoPath = null;
        this.videoTimestamp = 0.0d;
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) null);
        this.avatarEditor.setAnimation(this.cameraDrawable);
        this.cameraDrawable.setCurrentFrame(0);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1602lambda$createView$4$orgtelegramuiChannelCreateActivity(DialogInterface dialog) {
        if (!this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCustomEndFrame(86);
            this.avatarEditor.playAnimation();
            return;
        }
        this.cameraDrawable.setCurrentFrame(0, false);
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ boolean m1604lambda$createView$6$orgtelegramuiChannelCreateActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 || TextUtils.isEmpty(this.nameTextView.getEditText().getText())) {
            return false;
        }
        this.descriptionTextView.requestFocus();
        return true;
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ boolean m1605lambda$createView$7$orgtelegramuiChannelCreateActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1606lambda$createView$8$orgtelegramuiChannelCreateActivity(View v) {
        if (this.isPrivate) {
            this.isPrivate = false;
            updatePrivatePublic();
        }
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1607lambda$createView$9$orgtelegramuiChannelCreateActivity(View v) {
        if (!this.isPrivate) {
            this.isPrivate = true;
            updatePrivatePublic();
        }
    }

    private void generateLink() {
        if (!this.loadingInvite && this.invite == null) {
            TLRPC.ChatFull chatFull = getMessagesController().getChatFull(this.chatId);
            if (chatFull != null) {
                this.invite = chatFull.exported_invite;
            }
            if (this.invite == null) {
                this.loadingInvite = true;
                TLRPC.TL_messages_getExportedChatInvites req = new TLRPC.TL_messages_getExportedChatInvites();
                req.peer = getMessagesController().getInputPeer(-this.chatId);
                req.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
                req.limit = 1;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelCreateActivity$$ExternalSyntheticLambda8(this));
            }
        }
    }

    /* renamed from: lambda$generateLink$11$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1610lambda$generateLink$11$orgtelegramuiChannelCreateActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelCreateActivity$$ExternalSyntheticLambda7(this, error, response));
    }

    /* renamed from: lambda$generateLink$10$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1609lambda$generateLink$10$orgtelegramuiChannelCreateActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            this.invite = (TLRPC.TL_chatInviteExported) ((TLRPC.TL_messages_exportedChatInvites) response).invites.get(0);
        }
        this.loadingInvite = false;
        LinkActionView linkActionView = this.permanentLinkView;
        TLRPC.TL_chatInviteExported tL_chatInviteExported = this.invite;
        linkActionView.setLink(tL_chatInviteExported != null ? tL_chatInviteExported.link : null);
    }

    private void updatePrivatePublic() {
        String str;
        int i;
        String str2;
        int i2;
        if (this.sectionCell != null) {
            int i3 = 8;
            if (this.isPrivate || this.canCreatePublic) {
                this.typeInfoCell.setTag("windowBackgroundWhiteGrayText4");
                this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
                this.sectionCell.setVisibility(0);
                this.adminedInfoCell.setVisibility(8);
                this.adminnedChannelsLayout.setVisibility(8);
                TextInfoPrivacyCell textInfoPrivacyCell = this.typeInfoCell;
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell.getContext(), NUM, "windowBackgroundGrayShadow"));
                this.linkContainer.setVisibility(0);
                this.loadingAdminedCell.setVisibility(8);
                TextInfoPrivacyCell textInfoPrivacyCell2 = this.typeInfoCell;
                if (this.isPrivate) {
                    i = NUM;
                    str = "ChannelPrivateLinkHelp";
                } else {
                    i = NUM;
                    str = "ChannelUsernameHelp";
                }
                textInfoPrivacyCell2.setText(LocaleController.getString(str, i));
                HeaderCell headerCell3 = this.headerCell;
                if (this.isPrivate) {
                    i2 = NUM;
                    str2 = "ChannelInviteLinkTitle";
                } else {
                    i2 = NUM;
                    str2 = "ChannelLinkTitle";
                }
                headerCell3.setText(LocaleController.getString(str2, i2));
                this.publicContainer.setVisibility(this.isPrivate ? 8 : 0);
                this.privateContainer.setVisibility(this.isPrivate ? 0 : 8);
                this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.dp(7.0f));
                LinkActionView linkActionView = this.permanentLinkView;
                TLRPC.TL_chatInviteExported tL_chatInviteExported = this.invite;
                linkActionView.setLink(tL_chatInviteExported != null ? tL_chatInviteExported.link : null);
                TextView textView = this.checkTextView;
                if (!this.isPrivate && textView.length() != 0) {
                    i3 = 0;
                }
                textView.setVisibility(i3);
            } else {
                this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", NUM));
                this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
                this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                this.linkContainer.setVisibility(8);
                this.sectionCell.setVisibility(8);
                if (this.loadingAdminedChannels) {
                    this.loadingAdminedCell.setVisibility(0);
                    this.adminnedChannelsLayout.setVisibility(8);
                    TextInfoPrivacyCell textInfoPrivacyCell3 = this.typeInfoCell;
                    textInfoPrivacyCell3.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell3.getContext(), NUM, "windowBackgroundGrayShadow"));
                    this.adminedInfoCell.setVisibility(8);
                } else {
                    TextInfoPrivacyCell textInfoPrivacyCell4 = this.typeInfoCell;
                    textInfoPrivacyCell4.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell4.getContext(), NUM, "windowBackgroundGrayShadow"));
                    this.loadingAdminedCell.setVisibility(8);
                    this.adminnedChannelsLayout.setVisibility(0);
                    this.adminedInfoCell.setVisibility(0);
                }
            }
            this.radioButtonCell1.setChecked(!this.isPrivate, true);
            this.radioButtonCell2.setChecked(this.isPrivate, true);
            this.descriptionTextView.clearFocus();
            AndroidUtilities.hideKeyboard(this.descriptionTextView);
        }
    }

    public void onUploadProgressChanged(float progress) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(progress);
        }
    }

    public void didStartUpload(boolean isVideo) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(0.0f);
        }
    }

    public void didUploadPhoto(TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, String videoPath, TLRPC.PhotoSize bigSize, TLRPC.PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new ChannelCreateActivity$$ExternalSyntheticLambda5(this, photo, video, videoPath, videoStartTimestamp, smallSize, bigSize));
    }

    /* renamed from: lambda$didUploadPhoto$12$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1608lambda$didUploadPhoto$12$orgtelegramuiChannelCreateActivity(TLRPC.InputFile photo, TLRPC.InputFile video, String videoPath, double videoStartTimestamp, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
        if (photo == null && video == null) {
            this.avatar = smallSize.location;
            this.avatarBig = bigSize.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", (Drawable) this.avatarDrawable, (Object) null);
            showAvatarProgress(true, false);
            return;
        }
        this.inputPhoto = photo;
        this.inputVideo = video;
        this.inputVideoPath = videoPath;
        this.videoTimestamp = videoStartTimestamp;
        if (this.createAfterUpload) {
            try {
                AlertDialog alertDialog = this.progressDialog;
                if (alertDialog != null && alertDialog.isShowing()) {
                    this.progressDialog.dismiss();
                    this.progressDialog = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.donePressed = false;
            this.doneButton.performClick();
        }
        showAvatarProgress(false, true);
    }

    public String getInitialSearchString() {
        return this.nameTextView.getText().toString();
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarEditor != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.removeAllListeners();
                this.avatarAnimation.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                this.avatarAnimation = new AnimatorSet();
                if (show) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f})});
                } else {
                    if (this.avatarEditor.getVisibility() != 0) {
                        this.avatarEditor.setAlpha(0.0f);
                    }
                    this.avatarEditor.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f})});
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChannelCreateActivity.this.avatarAnimation != null && ChannelCreateActivity.this.avatarEditor != null) {
                            if (show) {
                                ChannelCreateActivity.this.avatarEditor.setVisibility(4);
                            } else {
                                ChannelCreateActivity.this.avatarProgressView.setVisibility(4);
                            }
                            AnimatorSet unused = ChannelCreateActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        AnimatorSet unused = ChannelCreateActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (show) {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(4);
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
            } else {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(0);
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
            }
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void saveSelfArgs(Bundle args) {
        if (this.currentStep == 0) {
            ImageUpdater imageUpdater2 = this.imageUpdater;
            if (!(imageUpdater2 == null || imageUpdater2.currentPicturePath == null)) {
                args.putString("path", this.imageUpdater.currentPicturePath);
            }
            EditTextEmoji editTextEmoji = this.nameTextView;
            if (editTextEmoji != null) {
                String text = editTextEmoji.getText().toString();
                if (text.length() != 0) {
                    args.putString("nameTextView", text);
                }
            }
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.currentStep == 0) {
            ImageUpdater imageUpdater2 = this.imageUpdater;
            if (imageUpdater2 != null) {
                imageUpdater2.currentPicturePath = args.getString("path");
            }
            String text = args.getString("nameTextView");
            if (text != null) {
                EditTextEmoji editTextEmoji = this.nameTextView;
                if (editTextEmoji != null) {
                    editTextEmoji.setText(text);
                } else {
                    this.nameToSet = text;
                }
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && this.currentStep != 1) {
            this.nameTextView.requestFocus();
            this.nameTextView.openKeyboard();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int i = id;
        if (i == NotificationCenter.chatDidFailCreate) {
            AlertDialog alertDialog = this.progressDialog;
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            this.donePressed = false;
        } else if (i == NotificationCenter.chatDidCreated) {
            AlertDialog alertDialog2 = this.progressDialog;
            if (alertDialog2 != null) {
                try {
                    alertDialog2.dismiss();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            long chat_id = ((Long) args[0]).longValue();
            Bundle bundle = new Bundle();
            bundle.putInt("step", 1);
            bundle.putLong("chat_id", chat_id);
            bundle.putBoolean("canCreatePublic", this.canCreatePublic);
            if (this.inputPhoto == null && this.inputVideo == null) {
                long j = chat_id;
            } else {
                long j2 = chat_id;
                MessagesController.getInstance(this.currentAccount).changeChatAvatar(chat_id, (TLRPC.TL_inputChatPhoto) null, this.inputPhoto, this.inputVideo, this.videoTimestamp, this.inputVideoPath, this.avatar, this.avatarBig, (Runnable) null);
            }
            presentFragment(new ChannelCreateActivity(bundle), true);
        }
    }

    private void loadAdminedChannels() {
        if (!this.loadingAdminedChannels) {
            this.loadingAdminedChannels = true;
            updatePrivatePublic();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_channels_getAdminedPublicChannels(), new ChannelCreateActivity$$ExternalSyntheticLambda10(this));
        }
    }

    /* renamed from: lambda$loadAdminedChannels$18$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1617x2aecf9e4(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelCreateActivity$$ExternalSyntheticLambda4(this, response));
    }

    /* renamed from: lambda$loadAdminedChannels$17$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1616x61eCLASSNAMEa3(TLObject response) {
        this.loadingAdminedChannels = false;
        if (response != null && getParentActivity() != null) {
            for (int a = 0; a < this.adminedChannelCells.size(); a++) {
                this.linearLayout.removeView(this.adminedChannelCells.get(a));
            }
            this.adminedChannelCells.clear();
            TLRPC.TL_messages_chats res = (TLRPC.TL_messages_chats) response;
            for (int a2 = 0; a2 < res.chats.size(); a2++) {
                AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), new ChannelCreateActivity$$ExternalSyntheticLambda18(this));
                TLRPC.Chat chat = (TLRPC.Chat) res.chats.get(a2);
                boolean z = true;
                if (a2 != res.chats.size() - 1) {
                    z = false;
                }
                adminedChannelCell.setChannel(chat, z);
                this.adminedChannelCells.add(adminedChannelCell);
                this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
            }
            updatePrivatePublic();
        }
    }

    /* renamed from: lambda$loadAdminedChannels$16$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1615x98eb0b62(View view) {
        TLRPC.Chat channel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        if (channel.megagroup) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", NUM, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", NUM, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new ChannelCreateActivity$$ExternalSyntheticLambda0(this, channel));
        showDialog(builder.create());
    }

    /* renamed from: lambda$loadAdminedChannels$15$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1614xcfea1421(TLRPC.Chat channel, DialogInterface dialogInterface, int i) {
        TLRPC.TL_channels_updateUsername req1 = new TLRPC.TL_channels_updateUsername();
        req1.channel = MessagesController.getInputChannel(channel);
        req1.username = "";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, new ChannelCreateActivity$$ExternalSyntheticLambda9(this), 64);
    }

    /* renamed from: lambda$loadAdminedChannels$14$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1613x6e91ce0(TLObject response1, TLRPC.TL_error error1) {
        if (response1 instanceof TLRPC.TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new ChannelCreateActivity$$ExternalSyntheticLambda1(this));
        }
    }

    /* renamed from: lambda$loadAdminedChannels$13$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1612x3de8259f() {
        this.canCreatePublic = true;
        if (this.descriptionTextView.length() > 0) {
            checkUserName(this.descriptionTextView.getText().toString());
        }
        updatePrivatePublic();
    }

    /* access modifiers changed from: private */
    public boolean checkUserName(String name) {
        if (name == null || name.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        Runnable runnable = this.checkRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (name != null) {
            if (name.startsWith("_") || name.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalid", NUM));
                this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                return false;
            }
            int a = 0;
            while (a < name.length()) {
                char ch = name.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", NUM));
                    this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                    this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalid", NUM));
                    this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                    this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                    return false;
                } else {
                    a++;
                }
            }
        }
        if (name == null || name.length() < 5) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", NUM));
            this.checkTextView.setTag("windowBackgroundWhiteRedText4");
            this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            return false;
        } else if (name.length() > 32) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", NUM));
            this.checkTextView.setTag("windowBackgroundWhiteRedText4");
            this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", NUM));
            this.checkTextView.setTag("windowBackgroundWhiteGrayText8");
            this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
            this.lastCheckName = name;
            ChannelCreateActivity$$ExternalSyntheticLambda2 channelCreateActivity$$ExternalSyntheticLambda2 = new ChannelCreateActivity$$ExternalSyntheticLambda2(this, name);
            this.checkRunnable = channelCreateActivity$$ExternalSyntheticLambda2;
            AndroidUtilities.runOnUIThread(channelCreateActivity$$ExternalSyntheticLambda2, 300);
            return true;
        }
    }

    /* renamed from: lambda$checkUserName$21$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1600lambda$checkUserName$21$orgtelegramuiChannelCreateActivity(String name) {
        TLRPC.TL_channels_checkUsername req = new TLRPC.TL_channels_checkUsername();
        req.username = name;
        req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelCreateActivity$$ExternalSyntheticLambda13(this, name), 2);
    }

    /* renamed from: lambda$checkUserName$20$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1599lambda$checkUserName$20$orgtelegramuiChannelCreateActivity(String name, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelCreateActivity$$ExternalSyntheticLambda3(this, name, error, response));
    }

    /* renamed from: lambda$checkUserName$19$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1598lambda$checkUserName$19$orgtelegramuiChannelCreateActivity(String name, TLRPC.TL_error error, TLObject response) {
        this.checkReqId = 0;
        String str = this.lastCheckName;
        if (str != null && str.equals(name)) {
            if (error != null || !(response instanceof TLRPC.TL_boolTrue)) {
                if (error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                    this.checkTextView.setText(LocaleController.getString("LinkInUse", NUM));
                } else {
                    this.canCreatePublic = false;
                    loadAdminedChannels();
                }
                this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                this.lastNameAvailable = false;
                return;
            }
            this.checkTextView.setText(LocaleController.formatString("LinkAvailable", NUM, name));
            this.checkTextView.setTag("windowBackgroundWhiteGreenText");
            this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
            this.lastNameAvailable = true;
        }
    }

    private void showErrorAlert(String error) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            char c = 65535;
            switch (error.hashCode()) {
                case 288843630:
                    if (error.equals("USERNAME_INVALID")) {
                        c = 0;
                        break;
                    }
                    break;
                case 533175271:
                    if (error.equals("USERNAME_OCCUPIED")) {
                        c = 1;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    builder.setMessage(LocaleController.getString("LinkInvalid", NUM));
                    break;
                case 1:
                    builder.setMessage(LocaleController.getString("LinkInUse", NUM));
                    break;
                default:
                    builder.setMessage(LocaleController.getString("ErrorOccurred", NUM));
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ChannelCreateActivity$$ExternalSyntheticLambda14(this);
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        themeDescriptions.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        themeDescriptions.add(new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"));
        themeDescriptions.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.headerCell2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        themeDescriptions.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"));
        themeDescriptions.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGreenText"));
        themeDescriptions.add(new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        themeDescriptions.add(new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.privateContainer, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.privateContainer, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        themeDescriptions.add(new ThemeDescription((View) this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, cellDelegate, "avatar_text"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundRed"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundOrange"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$22$org-telegram-ui-ChannelCreateActivity  reason: not valid java name */
    public /* synthetic */ void m1611x7a060851() {
        LinearLayout linearLayout3 = this.adminnedChannelsLayout;
        if (linearLayout3 != null) {
            int count = linearLayout3.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.adminnedChannelsLayout.getChildAt(a);
                if (child instanceof AdminedChannelCell) {
                    ((AdminedChannelCell) child).update();
                }
            }
        }
    }
}
